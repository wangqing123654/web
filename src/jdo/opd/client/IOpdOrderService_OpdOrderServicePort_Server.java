
package jdo.opd.client;

import javax.xml.ws.Endpoint;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2013-05-22T18:33:51.082+08:00
 * Generated source version: 2.5.2
 * 
 */
 
public class IOpdOrderService_OpdOrderServicePort_Server{

    protected IOpdOrderService_OpdOrderServicePort_Server() throws java.lang.Exception {
        System.out.println("Starting Server");
        Object implementor = new IOpdOrderServiceImpl();
        String address = "http://127.0.0.1:8080/web/services/opdService";
        Endpoint.publish(address, implementor);
    }
    
    public static void main(String args[]) throws java.lang.Exception { 
        new IOpdOrderService_OpdOrderServicePort_Server();
        System.out.println("Server ready..."); 
        
        Thread.sleep(5 * 60 * 1000); 
        System.out.println("Server exiting");
        System.exit(0);
    }
}
