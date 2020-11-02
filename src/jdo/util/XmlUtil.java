package jdo.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.javahis.util.DateUtil;

public class XmlUtil {

	
	/**
	 * ��������XML����
	 * @param parm
	 * @param telParm
	 */
	public static  void createSmsFile(TParm parm,TParm telParm){
		//Document document = createSmsXml(parm,telParm);
		String smsPath = getSmsPath();
		String fileName = "";
		if(parm.getValue("SysNo").length() > 0){
			fileName = parm.getValue("SysNo") + getNowTime("yyyyMMddHHmmssSSS")+parm.getValue("MrNo")+".xml";
		}else{
			fileName = getNowTime("yyyyMMddHHmmssSSS")+parm.getValue("MrNo")+".xml";
		}
		String path = smsPath+fileName;
		
		createXml(parm, telParm, path,fileName);
		//createFile(document,path);
		 
	}

	/**
	 * ����绰����XML���� 
	 * @param parm
	 * @param telList
	 * @return
	 */
	private static  Document createSmsXml(TParm parm,TParm telParm) {
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("Root");
		Element telsElement = rootElement.addElement("Tels");
		if(telParm != null && telParm.getCount() >  0 ){
			for(int i = 0 ; i < telParm.getCount() ; i++ ){
				
				Element telElement = telsElement.addElement("Tel");
				telElement.setText(telParm.getValue("TEL",i));
			}
		}
		
		String content = parm.getValue("Content");
		Element contentElement = rootElement.addElement("Content");
		contentElement.setText(content);
		
		String mrNo = parm.getValue("MrNo");
		Element mrNoElement = rootElement.addElement("MrNo");
		mrNoElement.setText(mrNo);
		
		String caseNo = parm.getValue("CaseNo");
		Element caseNoElement = rootElement.addElement("CaseNo");
		caseNoElement.setText(caseNo);
		
		String name = parm.getValue("Name");
		Element nameElement = rootElement.addElement("Name");
		nameElement.setText(name);
		//System.out.println("xml=============:"+document.asXML());
		return document;
	}
	
	/**
	 * д�����ļ���ָ����·����
	 * @param document    
	 * @param path        ����·��
	 */
	public static  void createFile(Document document,String path){
		XMLWriter output = null ;
		try{
			output = new XMLWriter(
                  new FileWriter( new File(path))); //�����ĵ�
            output.write( document );
              
        }catch(IOException e){
        	System.out.println(e.getMessage());
        }finally{
        	if(output != null ){
	        	try {
	        		
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
    }
	
	 private static  TConfig getProp() {
	        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
//	        TConfig config = new TConfig(
//	            "%ROOT%\\WEB-INF\\config\\system\\TConfig.x",
//	            TIOM_AppServer.SOCKET);
	        return config;
	 }
	 
	 public  static String getSmsPath(){
		 TConfig config = getProp() ;
		 String smsPath = config.getString("", "sms.path");
		 return smsPath;
	 }
	 
	  
	 public static void createXml(TParm parm,TParm telParm,String path,String fileName){
	        FileOutputStream ps = null; 
			OutputStreamWriter ow = null;
			BufferedWriter buf = null;
			PrintWriter out = null;
			createNewFile(path);
			
			try {
				ps = new FileOutputStream(path); 
				ow = new OutputStreamWriter(ps, "utf8");
				buf = new BufferedWriter(ow);
				out = new PrintWriter(buf);
				out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				out.println("<Root>");
				out.println("<Tels>");
				if(telParm != null && telParm.getCount("TEL1") >  0 ){//modify by caoyong 20131021 add("TEL1")
					for(int i = 0 ; i < telParm.getCount("TEL1") ; i++ ){
						out.println("<Tel>"+telParm.getValue("TEL1",i).replace("[", "").replace("]", "")+"</Tel>");
					}
				}
				
				out.println("</Tels>");
				//========add caoyong if()else{} 20131021===========
				if(parm.getValue("Title").length()>0){
					out.println("<TITLE>"+parm.getValue("Title")+"</TITLE>");
				 }else{
					 out.println("<TITLE>Σ��ֵ֪ͨ</TITLE>");
				 }
				
				out.println("<Content>"+parm.getValue("Content")+"</Content>");
				out.println("<MrNo>"+parm.getValue("MrNo")+"</MrNo>");
				out.println("<Name>"+parm.getValue("Name")+"</Name>");
				out.println("</Root>");
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}finally{
				if(out != null ){
					out.close();
				}
				try {
					if(buf != null ){
						buf.close();
					}
					if(ow != null ){
						ow.close();
					}
					if(ps != null ){
						ps.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
			//�ϴ�FTP
	        invokeFtp(path, fileName);
		}
	 
	 
	 
	 public static void invokeFtp(String path,String fileName){
		 TConfig config = getProp();
		 String url = config.getString("", "FTP.Server");
		 int port = config.getInt("", "FTP.Port");
		 String username = config.getString("", "FTP.Username");
		 String password = config.getString("", "FTP.Password");
		 FileInputStream in = null;
		 try {
			 in = new FileInputStream(new File(
					path));
		 } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }

		 try{
			 FtpClient.uploadFile(url, port, username, password, path, fileName, in);
		 }catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace() ;
		}
		 try {
			 if(in != null ){
				 in.close();
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 
	 /**
	     * �ж� ������ļ��в����� �򴴽����ļ��� �ٴ����ļ�
	     * @param filePath �ļ�ȫ·��
	     */
	    public static void createNewFile(String filePath) {
	        File f = new File(filePath);
	        if(!f.getParentFile().exists()) {
	            f.getParentFile().mkdirs();
	            try {
	                f.createNewFile();
	            }
	            catch (Exception e) {
	                System.out.println("create file failed!\n"+filePath);
	                e.printStackTrace();
	            }
	        }
	        f = null;
	        
	    }
	    
	 
	 public static void main(String[] args) {
		 /**
		TParm xmlParm = new TParm();
		xmlParm.setData("Content","li");
	     xmlParm.setData("MrNo","1866666667");
	     xmlParm.setData("CaseNo","999999");
	     xmlParm.setData("Name","22222");
	     
	     TParm telParm = new TParm();
	     telParm.setData("TEL","111111111");
	     createSmsFile(xmlParm,telParm);*/
		 
	     System.out.println(DateUtil.getNowTime("yyyyMMddHHmmssSSS"));
	     
	}
	 
	// ��ȡ����ʱ��
		public static String getNowTime(String dateformat) {
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);// ���Է�����޸����ڸ�ʽ
			String hehe = dateFormat.format(now);
			return hehe;
		}
	 
}
