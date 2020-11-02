package action.spc.phaSyncClient;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.5.2 2013-08-14T17:51:31.948+08:00
 * Generated source version: 2.5.2
 * 
 */
@WebServiceClient(name = "SPCPhaBaseSynchWsToolImplService", wsdlLocation = "http://localhost:8080/webgy/services/SPCPhaBaseSynchWs?wsdl", targetNamespace = "http://phaBaseSyncClient.spc.jdo/")
public class SPCPhaBaseSynchWsToolImplService extends Service {

	public final static URL WSDL_LOCATION;

	public final static QName SERVICE = new QName(
			"http://phaBaseSyncClient.spc.jdo/",
			"SPCPhaBaseSynchWsToolImplService");
	public final static QName SPCPhaBaseSynchWsToolImplPort = new QName(
			"http://phaBaseSyncClient.spc.jdo/",
			"SPCPhaBaseSynchWsToolImplPort");
	static {
		URL url = null;
		try {
			url = new URL(
					"http://localhost:8080/webgy/services/SPCPhaBaseSynchWs?wsdl");
		} catch (MalformedURLException e) {
			java.util.logging.Logger
					.getLogger(SPCPhaBaseSynchWsToolImplService.class.getName())
					.log(java.util.logging.Level.INFO,
							"Can not initialize the default wsdl from {0}",
							"http://localhost:8080/webgy/services/SPCPhaBaseSynchWs?wsdl");
		}
		WSDL_LOCATION = url;
	}

	public SPCPhaBaseSynchWsToolImplService(URL wsdlLocation) {
		super(wsdlLocation, SERVICE);
	}

	public SPCPhaBaseSynchWsToolImplService(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public SPCPhaBaseSynchWsToolImplService() {
		super(WSDL_LOCATION, SERVICE);
	}

	// This constructor requires JAX-WS API 2.2. You will need to endorse the
	// 2.2
	// API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS
	// 2.1
	// compliant code instead.
	public SPCPhaBaseSynchWsToolImplService(WebServiceFeature... features) {
		super(WSDL_LOCATION, SERVICE);
	}

	// This constructor requires JAX-WS API 2.2. You will need to endorse the
	// 2.2
	// API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS
	// 2.1
	// compliant code instead.
	public SPCPhaBaseSynchWsToolImplService(URL wsdlLocation,
			WebServiceFeature... features) {
		super(wsdlLocation, SERVICE);
	}

	// This constructor requires JAX-WS API 2.2. You will need to endorse the
	// 2.2
	// API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS
	// 2.1
	// compliant code instead.
	public SPCPhaBaseSynchWsToolImplService(URL wsdlLocation,
			QName serviceName, WebServiceFeature... features) {
		super(wsdlLocation, serviceName);
	}

	/**
	 * 
	 * @return returns SPCPhaBaseSynchWsTool
	 */
	@WebEndpoint(name = "SPCPhaBaseSynchWsToolImplPort")
	public SPCPhaBaseSynchWsTool getSPCPhaBaseSynchWsToolImplPort() {
		return super.getPort(SPCPhaBaseSynchWsToolImplPort,
				SPCPhaBaseSynchWsTool.class);
	}

	/**
	 * 
	 * @param features
	 *            A list of {@link javax.xml.ws.WebServiceFeature} to configure
	 *            on the proxy. Supported features not in the
	 *            <code>features</code> parameter will have their default
	 *            values.
	 * @return returns SPCPhaBaseSynchWsTool
	 */
	@WebEndpoint(name = "SPCPhaBaseSynchWsToolImplPort")
	public SPCPhaBaseSynchWsTool getSPCPhaBaseSynchWsToolImplPort(
			WebServiceFeature... features) {
		return super.getPort(SPCPhaBaseSynchWsToolImplPort,
				SPCPhaBaseSynchWsTool.class, features);
	}

}
