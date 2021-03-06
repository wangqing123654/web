
package action.sys.client;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.1 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "SSOServerImpl", targetNamespace = "http://impl.service.remote.www.whaosoft.com/", wsdlLocation = "http://localhost:8080/BC_LDAP_SERVER/cxf/sso?wsdl")
public class SSOServerImpl
    extends Service
{

    private final static URL SSOSERVERIMPL_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/BC_LDAP_SERVER/cxf/sso?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        SSOSERVERIMPL_WSDL_LOCATION = url;
    }

    public SSOServerImpl(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SSOServerImpl() {
        super(SSOSERVERIMPL_WSDL_LOCATION, new QName("http://impl.service.remote.www.whaosoft.com/", "SSOServerImpl"));
    }

    /**
     * 
     * @return
     *     returns SSOServerImplPortType
     */
    @WebEndpoint(name = "SSOServerImplPort")
    public SSOServerImplPortType getSSOServerImplPort() {
        return (SSOServerImplPortType)super.getPort(new QName("http://impl.service.remote.www.whaosoft.com/", "SSOServerImplPort"), SSOServerImplPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SSOServerImplPortType
     */
    @WebEndpoint(name = "SSOServerImplPort")
    public SSOServerImplPortType getSSOServerImplPort(WebServiceFeature... features) {
        return (SSOServerImplPortType)super.getPort(new QName("http://impl.service.remote.www.whaosoft.com/", "SSOServerImplPort"), SSOServerImplPortType.class, features);
    }

}
