
package jdo.spc.bsm;
import javax.xml.ws.Endpoint;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2013-05-21T08:54:31.500+08:00
 * Generated source version: 2.5.2
 * 
 */
 
public class ConsisServiceSoap_ConsisServiceSoap12_Server{

    protected ConsisServiceSoap_ConsisServiceSoap12_Server() throws java.lang.Exception {
        System.out.println("Starting Server");
        Object implementor = new ConsisServiceSoapImpl1();
        String address = "http://192.168.8.140/webservice/ServiceConsis.asmx";
        Endpoint.publish(address, implementor);
    }
    
    public static void main(String args[]) throws java.lang.Exception { 
        new ConsisServiceSoap_ConsisServiceSoap12_Server();
        System.out.println("Server ready..."); 
        
        Thread.sleep(5 * 60 * 1000); 
        System.out.println("Server exiting");
        System.exit(0);
    }
}
