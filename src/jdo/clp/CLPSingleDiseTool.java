package jdo.clp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.dongyang.jdo.*;
import com.dongyang.ui.TWord;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: �����ֹ�����</p> 
 *
 * <p>Description: �����ֹ�����</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore </p>
 *
 * @author WangLong 20120926
 * @version 1.0
 */
public class CLPSingleDiseTool extends TJDOTool {

    private static CLPSingleDiseTool instance = null;//ʵ��
    private TWord word;// WORD�ؼ�
    
    /**
     * ����ʵ��
     */
    public static CLPSingleDiseTool getInstance() {
        if (instance == null) {
            instance = new CLPSingleDiseTool();
        }
        return instance;
    }
    
    private CLPSingleDiseTool() {
        this.setModuleName("clp\\CLPSingleDiseModule.x");
        onInit();
    }

	/**
	 * ��ȡ���ﲡ�����ε�������Ϣ�����ڡ��ϲ������ڣ�
	 */
	public TParm queryEMRHistoryForMerge(TParm parm) {
		TParm result = query("queryEMRHistoryForMerge", parm);//parm�м���MR_NO
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * ��ȡ���ﲡ��������Ϣ
	 */
	public TParm queryPatInfoFromE(TParm parm) {
		TParm result=new TParm();
		if(parm.getData("CASE_NO")!=null){
			TParm diagResult = query("queryPatDiagFromE", parm);
            result = query("querySinglePatInfoFromE", parm);
            result.addData("ICD_CODE", diagResult.getValue("ICD_CODE", 0));
            result.addData("ICD_CHN_DESC", diagResult.getValue("ICD_CHN_DESC", 0));
		}else{
			 result = query("queryPatInfoFromE", parm);//parm�м���CASE_NO,MR_NO,START_DATE,END_DATE,DEPT_CODE
		}
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * ��ȡסԺ����������Ϣ
	 */
	public TParm queryPatInfoFromI(TParm parm) {
		TParm result=new TParm();
		if(parm.getData("CASE_NO")!=null){
			TParm diagResult = query("queryPatDiagFromI", parm);
            result = query("querySinglePatInfoFromI", parm);
            result.addData("ICD_CODE", diagResult.getValue("ICD_CODE", 0));
            result.addData("ICD_CHN_DESC", diagResult.getValue("ICD_CHN_DESC", 0));
		}else{
			result = query("queryPatInfoFromI", parm);//parm�м���CASE_NO,MR_NO,START_DATE,END_DATE,DEPT_CODE
		}
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * ��ȡסԺ��������ԴADM_SOURCE����Ժ״̬
	 */
	public TParm queryASandPCFromI(TParm parm) {
		TParm result = query("queryASandPCFromI", parm);//parm�м���CASE_NO
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * ��ѯ�Һű��еĵ���������
	 */
	public TParm queryREGAdmSDInfo(TParm parm) {
		TParm result = query("queryREGAdmSDInfo", parm);//parm�м���CASE_NO
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * ��ѯԤԼסԺ���еĵ���������
	 */
	public TParm queryADMResvSDInfo(TParm parm) {
		TParm result = query("queryADMResvSDInfo", parm);//parm�м���CASE_NO
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * ��ѯסԺ�����еĵ���������
	 */
	public TParm queryADMInpSDInfo(TParm parm) {
		TParm result = query("queryADMInpSDInfo", parm);//parm�м���CASE_NO
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * ���¹Һű�����������Ϣ����
	 */
	public TParm updateREGAdmSDInfo(TParm parm) {
		//System.out.println("=====��������========"+parm);
		TParm result = update("updateREGAdmSDInfo", parm);//parm�м���CASE_NO,DISE_CODE
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * ����ԤԼסԺ������������Ϣ����
	 */
	public TParm updateADMResvSDInfo(TParm parm) {
		//System.out.println("=====��������========"+parm);
		TParm result = update("updateADMResvSDInfo", parm);//parm�м���CASE_NO,DISE_CODE
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * ����סԺ����������������Ϣ����
	 */
	public TParm updateADMInpSDInfo(TParm parm) {
		//System.out.println("=====��������========"+parm);
		TParm result = update("updateADMInpSDInfo", parm);//parm�м���CASE_NO,DISE_CODE
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * ɾ���Һű��еĵ�������Ϣ
	 */
	public TParm deleteREGAdmSDInfo(TParm parm) {
		//System.out.println("=====��������========"+parm);
		TParm result = update("deleteREGAdmSDInfo", parm);//parm�м���CASE_NO
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * ɾ��ԤԼסԺ���еĵ�������Ϣ
	 */
	public TParm deleteADMResvSDInfo(TParm parm) {
		//System.out.println("=====��������========"+parm);
		TParm result = update("deleteADMResvSDInfo", parm);//parm�м���CASE_NO
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * ɾ��סԺ�����еĵ�������Ϣ
	 */
	public TParm deleteADMInpSDInfo(TParm parm) {
		//System.out.println("=====��������========"+parm);
		TParm result = update("deleteADMInpSDInfo", parm);//parm�м���CASE_NO
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	
	/**
	 * ��ѯ�����ֲ�����Ϣ
	 */
	public TParm querySDData(TParm parm) {
		TParm result =new TParm();
		if (parm.getData("ADM_TYPE") == null) {
			result = query("querySDDataFromE", parm);// ADM_TYPE��DISE_CODE
			TParm eResult = query("querySDDataFromI", parm);// START_DATE��END_DATE��ADM_TYPE��DISE_CODE
			for (int i = 0; i < eResult.getCount("CASE_NO"); i++) {
				result.addRowData(eResult, i);
			}
		} else if (parm.getData("ADM_TYPE").equals("E")) {
			result = query("querySDDataFromE", parm);// ADM_TYPE��DISE_CODE
		} else if (parm.getData("ADM_TYPE").equals("I")) {
			result = query("querySDDataFromI", parm);// ADM_TYPE��DISE_CODE
		}
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	

	/**
	 * ɾ�������ֲ�����Ϣ
	 */
	public TParm deleteSDData(TParm parm,TConnection conn) {//CASE_NO
		TParm result = update("deleteSDData", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * ���뵥���ֲ�����Ϣ
	 */
	public TParm insertSDData(TParm parm,TConnection conn) {
		//System.out.println("=====��������========"+parm);
		TParm result = update("insertSDData", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * �����ֲ����ļ���¼���
	 */
	public TParm clearSDFileHistory(TParm parm,TConnection conn) {
		//System.out.println("=====��������========"+parm);
		TParm result = update("clearSDFileHistory", parm, conn);//parm�м���CASE_NO
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * �����ֲ����ļ���¼�ϲ�
	 */
	public TParm copySDFileHistory(TParm parm,TConnection conn) {
		TParm actionParm=new TParm();
		actionParm.setData("CASE_NO",parm.getData("CASE_NO"));
		actionParm.setData("CASE_NO_OLD",parm.getData("CASE_NO_OLD"));
		//System.out.println("=====��������========"+parm);
		TParm result = update("copySDFileHistory", actionParm, conn);//parm�м���CASE_NO_OLD,CASE_NO
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * ������������¼���
	 */
	public TParm clearSDDBHistory(TParm parm,TConnection conn) {
		//System.out.println("=====��������========"+parm);
		TParm result = update("clearSDDBHistory", parm, conn);//parm�м���CASE_NO
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ������������¼�ϲ�
	 */
	public TParm copySDDBHistory(TParm parm,TConnection conn) {
		TParm actionParm=new TParm();
		actionParm.setData("CASE_NO",parm.getData("CASE_NO"));
		actionParm.setData("CASE_NO_OLD",parm.getData("CASE_NO_OLD"));
		//System.out.println("=====��������========"+parm);
		TParm result = update("copySDDBHistory", actionParm, conn);//parm�м���CASE_NO_OLD,CASE_NO
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * ���Ƶ����ֲ����ļ�
	 */
	public boolean copySDEMRFile(TParm parm){
		TParm result = query("querySDEMRFile", parm);//��ѯ�����ֵĲ����ļ���Ϣ�������ļ����Ʋ���
		if ((result.getErrCode() < 0)||(result.getCount()<1)) {
			return false;
		}
		int i = 0;
		String newFilePath="";
		String newFileName="";
		for (; i < result.getCount("FILE_PATH"); i++) {
			newFilePath="JHW\\"+parm.getValue("CASE_NO").substring(0, 2)+ File.separator+parm.getValue("CASE_NO").substring(2, 4)+File.separator +result.getValue("MR_NO",i);
			newFileName=parm.getValue("CASE_NO")+result.getValue("FILE_NAME", i).substring(12);
			String srcFilePath = TConfig.getSystemValue("FileServer.Main.Root") + File.separator  + TConfig.getSystemValue("EmrData") + File.separator + result.getValue("FILE_PATH", i);
			String srcFileName = result.getValue("FILE_NAME", i) + ".jhw";
			//word.onOpen(result.getValue("FILE_PATH", i), result.getValue("FILE_NAME", i), 3, true);
			result.setData("FILE_PATH", i, newFilePath);
			result.setData("FILE_NAME", i, newFileName);
			String destFilePath=TConfig.getSystemValue("FileServer.Main.Root") + File.separator  + TConfig.getSystemValue("EmrData") + File.separator + newFilePath;
			String destFileName=newFileName + ".jhw";
			if(!fileCopy(srcFilePath+File.separator +srcFileName, destFilePath+File.separator +destFileName)){//����jhw�ļ�
				break;
			}
            if (!fileCopy(srcFilePath.replaceFirst("JHW", "XML") + File.separator
                                  + srcFileName.replaceFirst("\\.jhw", "\\.xml"),
                          destFilePath.replaceFirst("JHW", "XML") + File.separator
                                  + destFileName.replaceFirst("\\.jhw", "\\.xml"))) {//����xml�ļ�
                break;
            }
            if (!fileCopy(srcFilePath.replaceFirst("JHW", "XML") + File.separator
                                  + srcFileName.replaceFirst("\\.jhw", "\\_CDA.xml"),
                          destFilePath.replaceFirst("JHW", "XML") + File.separator
                                  + destFileName.replaceFirst("\\.jhw", "\\_CDA.xml"))) {//���ƺ�׺Ϊ_CDA��xml�ļ�
                break;
            }
		}
		if (i != result.getCount("FILE_PATH")) {//����ʱrollback
			for (int j=0; j < i; j++) {
				String filePath = TConfig.getSystemValue("FileServer.Main.Root") + File.separator  + TConfig.getSystemValue("EmrData") + result.getValue("FILE_PATH", i);
				String fileName = result.getValue("FILE_NAME", i) + ".jhw";
                new File(filePath + File.separator + fileName).delete();//ɾ�����ƹ�����jhw�ļ�
                new File(filePath.replaceFirst("JHW", "XML") + File.separator
                        + fileName.replaceFirst("\\.jhw", "\\.xml")).delete();//ɾ�����ƹ�����xml�ļ�
                new File(filePath.replaceFirst("JHW", "XML") + File.separator
                        + fileName.replaceFirst("\\.jhw", "\\_CDA.xml")).delete();//ɾ�����ƹ�����׺Ϊ_CDA��xml�ļ�
			}
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * �����ļ�(Ŀ���ļ������򸲸�����)
	 * @param srcFileName
	 * @param destFileName
	 * @return
	 */
	public boolean fileCopy(String srcFileName, String destFileName) {
		File srcFile = new File(srcFileName);
		File targetFile = new File(destFileName);
		try {
			InputStream in = new FileInputStream(srcFile);
			OutputStream out = new FileOutputStream(targetFile);
			byte[] buffer = new byte[1024];
			int byteread = 0; // ��ȡ�ļ����ֽ���
			while ((byteread = in.read(buffer)) != -1) {
				out.write(buffer, 0, byteread);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	



}
