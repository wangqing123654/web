package action.ind.stockclient;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2013-04-02T11:37:56.877+08:00
 * Generated source version: 2.5.2
 * 
 */
@WebServiceClient(name = "SpcStockServiceImplService", 
                  wsdlLocation = "http://172.20.116.105:8080/webgy/services/stockService?wsdl",
                  targetNamespace = "http://stockinf.spc.jdo/") 
public class SpcStockServiceImplService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://stockinf.spc.jdo/", "SpcStockServiceImplService");
    public final static QName SpcStockServiceImplPort = new QName("http://stockinf.spc.jdo/", "SpcStockServiceImplPort");
    static {
        URL url = null;
        try {
            url = new URL("http://172.20.116.105:8080/webgy/services/stockService?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(SpcStockServiceImplService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "http://172.20.116.105:8080/webgy/services/stockService?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public SpcStockServiceImplService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public SpcStockServiceImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SpcStockServiceImplService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public SpcStockServiceImplService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public SpcStockServiceImplService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public SpcStockServiceImplService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName);
    }

    /**
     *
     * @return
     *     returns SpcStockService
     */
    @WebEndpoint(name = "SpcStockServiceImplPort")
    public SpcStockService getSpcStockServiceImplPort() {
        return super.getPort(SpcStockServiceImplPort, SpcStockService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SpcStockService
     */
    @WebEndpoint(name = "SpcStockServiceImplPort")
    public SpcStockService getSpcStockServiceImplPort(WebServiceFeature... features) {
        return super.getPort(SpcStockServiceImplPort, SpcStockService.class, features);
    }

}
