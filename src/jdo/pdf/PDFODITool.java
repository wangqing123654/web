package jdo.pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.FileTool;
import com.javahis.util.StringUtil;

public class PDFODITool extends PdfTool {
	/**
	 * ������ʱ���ݵ�����
	 * 
	 * @param mrno
	 *            String
	 * @param caseno
	 *            String
	 * @param path
	 *            String
	 * @return boolean
	 */
	public boolean downLoadTempFile(String filePath, String tempPath, TParm parm,List list) {
		if (!filePath.endsWith("/"))
			filePath += "/";
		if (!tempPath.endsWith("/"))
			tempPath += "/";

		String s[] = listPDFFile(filePath);
		for (int parmIndex = 0; parmIndex < parm.getCount("FileName"); parmIndex++) {

			for (int i = 0; i < s.length; i++) {
				if (!StringUtil.isNullString(s[i])
						&& s[i].equals(parm.getData("FileName", parmIndex)) && "Y".equalsIgnoreCase(parm.getData("FLG", parmIndex).toString())) {
					byte data[] = TIOM_FileServer.readFile(TIOM_FileServer
							.getSocket(), filePath + "\\" + s[i]);
					//s[i] = "";  
					if (data == null)
						break;
					try {
						if(StringUtil.isNullString((String)parm.getData("ORDER_DESC", parmIndex))){
							list.add(parm.getData("Type", parmIndex));
						}else{
							list.add(parm.getData("ORDER_DESC", parmIndex));
						}
						
						FileTool.setByte(tempPath + parmIndex + ".pdf", data);
						break;
					} catch (Exception e) {
						break;
					}
				}
			}
		}
		return true;
	}

	public boolean downLoadTempFile1(String filePath, String tempPath) {
		if (!filePath.endsWith("/"))
			filePath += "/";
		if (!tempPath.endsWith("/"))
			tempPath += "/";

		String s[] = listPDFFile(filePath);
		for (int i = 0; i < s.length; i++) {
			byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
					filePath + "\\" + s[i]);
			if (data == null)
				return false;
			try {
				String[] str = s[i].split("_");
				FileTool.setByte(tempPath + str[str.length - 1], data);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * �б��ļ�
	 * 
	 * @param mrno
	 *            String
	 * @param caseno
	 *            String
	 * @return String[]
	 */
	public String[] listPDFFile(String filePath) {
		return TIOM_FileServer.listFile(TIOM_FileServer.getSocket(), filePath);
	}

	/**
	 * �ϲ������ļ�
	 * 
	 * @param parm
	 *            TParm
	 * @param path
	 *            String
	 * @param caseno
	 *            String
	 * @return TParm
	 */
	public TParm addPdf(TParm parm, String path, String caseno) {
		//System.out.println("--------come in---------");
		TParm p = new TParm();
		File f = new File(path);
		String[] listFile=f.list();
		if (!f.exists())
			f.mkdirs();
		int c = parm.getCount("FileName");
		// ����ִ���ļ�
		byte data[] = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				getRoot() + "\\pdftk");
		if (data == null) {
			p.setErr(-1, "��������û���ҵ��ļ� " + getRoot() + "\\pdftk");
			return p;
		}
		try {
			FileTool.setByte(path + "\\pdftk.exe", data);
		} catch (Exception e) {
			p.setErr(-1, e.getMessage());
			return p;
		}
		String s = "";
		// С��1000�ݲ���
		if (c <= 1000) {
			// �����������ļ�
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < c; i++) {
				for (int j = 0; j < listFile.length; j++) {
					if ((i + ".pdf").equals(listFile[j]))
						sb.append(i + ".pdf ");
					// break;
				}
			}
			
			s = path.substring(0, 2) + " \n" + "cd " + path + " \n"
					+ "pdftk.exe " + sb.toString() + " cat output " + caseno
					+ ".pdf \n exit ";
		//  add by lx 2013/01/18 ����1000�ݲ�������
		} else {
			s = path.substring(0, 2) + " \n" + "cd " + path + " \n";
			//�����������ļ�
			StringBuffer sb = new StringBuffer();		
			//						
			int sum=0;			
			for (int i = 0; i < c; i++) {				
				//����С��1000			
				for (int j = 0; j < listFile.length; j++) {
					if ((i + ".pdf").equals(listFile[j]))
						sb.append(i + ".pdf ");
					// break;					
				}
				//add by lx 2013/01/18 ��������ʱ��
					if((i!=0)&&(i%1000)==0){
						s+= "pdftk.exe " + sb.toString() + " cat output " + caseno+"_"+sum
						+ ".pdf \n";
						//System.out.println("------s111------"+s);
						//
						sb=null;
						sb=new StringBuffer();
						sum++;
					}
				//����ȡ��
				if(i==c-1){
					s+= "pdftk.exe " + sb.toString() + " cat output " + caseno+"_"+sum
					+ ".pdf \n";
					//System.out.println("----s���------"+s);
					//sum++;
				}
				
			}
			//System.out.println("----sum-----"+sum);
			s+= "pdftk.exe " ;
			//�������е�pdf
			for(int z=0;z<=sum;z++){
				//System.out.println("----z-----"+z);
				s+= caseno+"_"+z+".pdf ";
			}
			
			s+="cat output " + caseno
			+ ".pdf \n exit";
			/*s+= "pdftk.exe " + sb.toString() + " cat output " + caseno
				+ ".pdf \n exit ";*/
			//System.out.println("==final s=="+s);
		}
		try {
			FileTool.setByte(path + "/pdf.bat", s.replaceAll("/", "\\\\")
					.getBytes());
		} catch (Exception e) {
			p.setErr(-1, e.getMessage());
			return p;
		}
		// ִ���������ļ�
		p = new TParm(exec(path + "\\","pdf.bat",caseno));//�޸ķ���
		if (p.getErrCode() != 0)
			return p;
		return p;
	}
	
	/**
	 * add by lx ��������
	 * ִ��������ϲ�����
	 * @param com
	 * @param caseNo
	 * @return
	 */
	public Map exec(String path,String batFile,String caseNo){
		//1.���֮ǰ�ϲ�����ļ�
		this.deleteFiles(path, caseNo);
		//2.�ͻ����ϲ��ļ�
		TParm reset = new TParm();
		try {
			String command = "cmd /k "+path+batFile;
			Process proc = Runtime.getRuntime().exec(command);
			proc.getOutputStream().close();
			StreamGobbler errorGobbler = new StreamGobbler(proc
					.getErrorStream(), "Error");
			StreamGobbler outputGobbler = new StreamGobbler(proc
					.getInputStream(), "Output");
			errorGobbler.start();
			outputGobbler.start();

			int a = proc.waitFor();
			//System.out.println("-----a-------" + a);// 0 �ǳɹ� //1��ʧ��
			if (a != 0) {
				// System.out.println("errorGobbler"+errorGobbler.getErrors());
				String strErrors="";
				for (String v : errorGobbler.getErrors()) {
					//System.out.println("------Errors-----" + v);
					strErrors+=v+"\n";
				}
				reset.setErr(-1, strErrors);
			}else{
				//reset.setErr(-1, strErrors)
				//System.out.println("-----success------");
			}
		} catch (Exception e) {
			reset.setErr(-1, e.getMessage());
		}
		//
		return reset.getData();
	}

	public Map exec(String com) {
		TParm reset = new TParm();
		try {
			Runtime rt = Runtime.getRuntime();
			Process p=rt.exec( "cmd   /c start "+com);
			//p=rt.exec( "cmd   /c start "+com);
			// �����Ļ���;
//			try {
//				// �����ȴ���ȫ���;
//				p.waitFor();
//				p.destroy();
//			} catch (InterruptedException e) {
//				// e.printStackTrace();
//				reset.setErr(-1, e.getMessage());
//			}

		} catch (Exception e) {
			reset.setErr(-1, e.getMessage());
		}
		return reset.getData();
	}

	/**
	 * ���
	 * 
	 * @param input
	 *            InputStream
	 * @return String
	 */
	public String out(InputStream input) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String s = br.readLine();
			while (s != null) {
				if (sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(s);
				s = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
    /**
     * ���벡������
     * @param parm TParm
     * @return TParm
     */
    public TParm insertMRV(TParm parm){
        //�ж��Ƿ���ڲ������� ����������½�
        String select = "SELECT MR_NO,CASE_NO FROM MRO_MRV_TECH WHERE MR_NO='"+parm.getValue("MR_NO")+"' AND CASE_NO='"+parm.getValue("CASE_NO")+"'";
        //System.out.println("selectSQL:"+select);
        TParm re = new TParm(TJDODBTool.getInstance().select(select));
        //System.out.println("re:"+re);
        if(re.getCount("MR_NO")>0){//�Ѵ��ڲ�������
            return new TParm();
        }
    	String insert = "insert into MRO_MRV_TECH (MR_NO,CASE_NO,CHECK_FLG,IN_FLG, "
			+ " BOX_CODE,"
			+ " OPT_USER,OPT_TERM) values ("
			+ " '"
			+ parm.getValue("MR_NO")
			+ "','"
			+ parm.getValue("CASE_NO")
			+ "','"
			+ parm.getValue("CHECK_FLG")
			+ "','"
			+ parm.getValue("IN_FLG")
			+ "',"
			+ " '"
			+ parm.getValue("BOX_CODE")
			+ "',"
			+ " '"
			+ Operator.getID() + "','" + Operator.getIP() + "')";
        //System.out.println("insertSQL:"+insert);
        TParm result = new TParm(TJDODBTool.getInstance().update(insert));
        //System.out.println("result:"+result);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
