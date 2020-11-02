package com.javahis.ui.odi.client;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2013-01-15T15:53:26.090+08:00
 * Generated source version: 2.5.2
 * 
 */
@WebServiceClient(name = "SpcOdiServiceImplService", 
                  wsdlLocation = "http://172.20.116.105:8080/webgy/services/SpcOdiService?wsdl",
                  targetNamespace = "http://services.spc.action/") 
public class SpcOdiServiceImplService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://services.spc.action/", "SpcOdiServiceImplService");
    public final static QName SpcOdiServiceImplPort = new QName("http://services.spc.action/", "SpcOdiServiceImplPort");
    static {
        URL url = null;
        try {
            url = new URL("http://172.20.116.105:8080/webgy/services/SpcOdiService?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(SpcOdiServiceImplService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://172.20.10.89:8080/webgy/services/SpcOdiService?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public SpcOdiServiceImplService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public SpcOdiServiceImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SpcOdiServiceImplService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public SpcOdiServiceImplService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public SpcOdiServiceImplService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public SpcOdiServiceImplService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName);
    }

    /**
     *
     * @return
     *     returns SpcOdiService
     */
    @WebEndpoint(name = "SpcOdiServiceImplPort")
    public SpcOdiService getSpcOdiServiceImplPort() {
        return super.getPort(SpcOdiServiceImplPort, SpcOdiService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SpcOdiService
     */
    @WebEndpoint(name = "SpcOdiServiceImplPort")
    public SpcOdiService getSpcOdiServiceImplPort(WebServiceFeature... features) {
        return super.getPort(SpcOdiServiceImplPort, SpcOdiService.class, features);
    }

}
