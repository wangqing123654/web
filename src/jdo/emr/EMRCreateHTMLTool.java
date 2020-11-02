package jdo.emr;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.dongyang.config.TConfig;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.FileTool;

/**
 * ����html���뵥
 * @author lixiang
 *
 */
public class EMRCreateHTMLTool {
	
    /**
     * ʵ��
     */
    private static EMRCreateHTMLTool instanceObject;
	
    /**
     * �õ�ʵ��
     * @return EMRCreateXMLTool
     */
    public static EMRCreateHTMLTool getInstance() {
        if (instanceObject == null)
            instanceObject = new EMRCreateHTMLTool();
        return instanceObject;
    }
    /**
     * ����html���뵥
     * @param path
     * @param fileName
     * @param targetName
     * @param dir
     */
    public void createHTML(String path,String fileName,String targetName,String dir){
    	 String fileServerIP = TConfig.getSystemValue("FileServer.Main.IP");
    	 //C:\JavaHis\temp\
    	 String temp = TConfig.getSystemValue("PatInfPIC.LocalPath");
    	 //�ж��Ƿ����
    	 new File(temp).mkdirs();
	        int port = TSocket.FILE_SERVER_PORT;
	        TSocket socket = new TSocket(fileServerIP,port);
        //1.String xmlFileName = "f:/test/110628000006_CT������뵥��ס��_1.xml";
    	//C:\JavaHisFile\EmrFileData\EmrData\XML\12\02\000000001008
	    String xmlpath=path.replaceFirst("JHW","XML");
	    //System.out.println("=====fileName======"+fileName);
	    String xmlFileName=getDir(dir)+getServerFileName(xmlpath,fileName,"xml");
	    //System.out.println("======xmlFileName======="+xmlFileName);
    	byte[] b=TIOM_FileServer.readFile(socket, xmlFileName);
    	
    	//System.out.println("==b=="+b.length);
    	 try {
			FileTool.setByte(temp + "\\" + fileName+".xml", b);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	//TIOM_FileServer.writeFile(temp +"\\"+fileName+".xml", b);
		//System.out.println("app ip========="+TIOM_AppServer.SOCKET.getIP());
		//System.out.println("app prot========="+TIOM_AppServer.SOCKET.getPort());
		//System.out.println("Root========="+TIOM_AppServer.SOCKET.getRoot());
    	String xslurl="http://"+TIOM_AppServer.SOCKET.getIP()+":"+TIOM_AppServer.SOCKET.getPort()+"/web/jsp/emr/xml/cdaemr.xsl";
    	//2.String xslFileName = "f:/test/cdaemr.xsl";
    	//http://127.0.0.1:8080/web/jsp/emr/xml/cdaemr.xsl
    	
        //3.String htmlFileName = "f:/test/110628000006_CT������뵥��ס��_1.html";
    	//C:\JavaHisFile\EmrFileData\EmrData\HTML\12\02\000000001008\
    	Transform(temp +"\\"+fileName+".xml", xslurl,temp +"\\"+targetName+".html");
    	//д�ļ����ļ�������
    	writeFile(path,targetName+".html",dir);
    	
    	
    	
    }
    
	/**
	 * ͨ��xml+xsl����html�ļ�
	 * @param xmlFileName
	 * @param xslFileName
	 * @param htmlFileName
	 */
	private  void Transform(String xmlFileName, String xslFileName,
            String htmlFileName) {
        try {
            TransformerFactory tFac = TransformerFactory.newInstance();
            Source xslSource = new StreamSource(xslFileName);
            Transformer t = tFac.newTransformer(xslSource);
            File xmlFile = new File(xmlFileName);
            File htmlFile = new File(htmlFileName);
            Source source = new StreamSource(xmlFile);
            Result result = new StreamResult(htmlFile);
            //System.out.println(result.toString());
            t.setOutputProperty("encoding","gb2312");
            t.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
	 /**
	    * д�ļ�()
	    * @param path String
	    * @param fileName String
	    * @param dir String
	    * @return boolean
	    */
	   private boolean writeFile(String path,String fileName,String dir){
		   //
		   String temp = TConfig.getSystemValue("PatInfPIC.LocalPath");
		   byte[] b=TIOM_FileServer.readFile(temp +"\\"+fileName);
		   
	        String fileServerIP = TConfig.getSystemValue("FileServer.Main.IP");
	        int port = TSocket.FILE_SERVER_PORT;
	        TSocket socket = new TSocket(fileServerIP,port);
	        path = path.replaceFirst("JHW","HTML");
	        try{
	            if (!TIOM_FileServer.writeFile(socket, getDir(dir) +
	                                           getServerFileName(path, fileName,"html"),
	                                           b));
	                return false;
	        }catch(Exception e){
	            e.printStackTrace();
	        }
	        return true;

	    }
	   
	   /**
	     * �õ��ļ���
	     * @param path String
	     * @param fileName String
	     * @return String
	     */
	    private String getServerFileName(String path,String fileName,String fileType)
	    {
	        if(path == null || path.length() == 0)
	            return "";
	        if(fileName == null || fileName.length() == 0)
	            return "";
	        if(!path.endsWith("\\"))
	            path += "\\";
	        fileName = path + fileName;
	        if(!fileName.endsWith("."+fileType))
	            fileName += "."+fileType;
	        return fileName;
	    }
	    
	    /**
	     * �õ�Ŀ¼
	     * @param dir String
	     * @return String
	     */
	    private String getDir(String dir)
	    {
	        return TIOM_FileServer.getRoot() + TIOM_FileServer.getPath(dir);
	    }
	

}
