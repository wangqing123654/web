package action.spc.client;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2012-12-28T08:34:48.750+08:00
 * Generated source version: 2.5.2
 * 
 */
@WebServiceClient(name = "IndServiceImplService", 
                  wsdlLocation = "http://127.0.0.1:8080/web/services/IndService?wsdl",
                  targetNamespace = "http://inf.ind.jdo/") 
public class IndServiceImplService extends Service {
    
    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://inf.ind.jdo/", "IndServiceImplService");
    public final static QName IndServiceImplPort = new QName("http://inf.ind.jdo/", "IndServiceImplPort");
    static {
        URL url = null;
        try {
            url = new URL("http://127.0.0.1:8080/web/services/IndService?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(IndServiceImplService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://127.0.0.1:8080/web/services/IndService?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public IndServiceImplService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public IndServiceImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public IndServiceImplService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public IndServiceImplService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public IndServiceImplService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public IndServiceImplService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName);
    }

    /**
     *
     * @return
     *     returns IndService
     */
    @WebEndpoint(name = "IndServiceImplPort")
    public IndService getIndServiceImplPort() {
        return super.getPort(IndServiceImplPort, IndService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns IndService
     */
    @WebEndpoint(name = "IndServiceImplPort")
    public IndService getIndServiceImplPort(WebServiceFeature... features) {
        return super.getPort(IndServiceImplPort, IndService.class, features);
    }

}
