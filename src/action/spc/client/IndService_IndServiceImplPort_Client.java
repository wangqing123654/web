
package action.spc.client;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2012-12-28T04:49:52.856+08:00
 * Generated source version: 2.5.2
 * 
 */
public final class IndService_IndServiceImplPort_Client {

    private static final QName SERVICE_NAME = new QName("http://inf.ind.jdo/", "IndServiceImplService");

    private IndService_IndServiceImplPort_Client() {
    	
    }
    
    
    public static String onSaveDispense(String str){
    	 URL wsdlURL = IndServiceImplService.WSDL_LOCATION;
    	 IndServiceImplService ss = new IndServiceImplService(wsdlURL, SERVICE_NAME);
    	  System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
         IndService port = ss.getIndServiceImplPort(); 
        String  str1 =  port.onSaveDispense(str);
        return str1;                               
    }                                 
    

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = IndServiceImplService.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        IndServiceImplService ss = new IndServiceImplService(wsdlURL, SERVICE_NAME);
        IndService port = ss.getIndServiceImplPort();  
        
        {
        System.out.println("Invoking onSaveDispenseDM...");
     //   mm.setDispenseNo("");                     
        java.lang.String _onSaveDispenseDM__return = port.onSaveDispense("111");
        System.out.println("onSaveDispenseDM.result=" + _onSaveDispenseDM__return);
   

        }

        System.exit(0);
    }

}
