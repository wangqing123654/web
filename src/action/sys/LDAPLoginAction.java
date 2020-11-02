package action.sys;

import java.util.List;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import action.sys.client.SSOServerImplPortType;
import action.sys.client.UserDTO;

import com.dongyang.action.TAction;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;


/**
 *
 * @author whaosoft
 *
 */
public class LDAPLoginAction extends TAction{


    /**
    *
    * @param parm
    * @return
    */
   public TParm doLDAPLogin(TParm parm) {

		List<Object> obj = this.getLDAPWS().doCheckLDAPUser(parm.getValue("USERID"), parm.getValue("PASSWORD"));
		// System.out.println( "============= "+ obj.get(0) );
		// System.out.println( "============= "+ obj.get(1) );

		TParm result = new TParm();
		result.setData("RESULT", obj);

		return result;
	}

	/**
	 *
	 * @param parm
	 * @return
	 */
	public TParm getLDAPUser(TParm parm){

		UserDTO dto = this.getLDAPWS().doQueryLDAPUser(parm.getValue("USER"));

		TParm result = new TParm();
		result.setData("USER_NAME",  dto.getDisplayName());
		result.setData("TEL1",  dto.getTelephonenumber());
		result.setData("DESCRIPTION",  dto.getDescription());
		result.setData("E_MAIL",  dto.getMail());

		return result;
	}

	/**
	 *
	 * @return
	 */
	private SSOServerImplPortType getLDAPWS() {

		TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
		String url = config.getString("", "LDAP_WEB_SERVICE");
        String all_url =  "http://" + url + "/cxf/sso";

		//
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		//factory.setAddress("http://localhost:8080/BC_LDAP_SERVER/cxf/sso");
		factory.setAddress(all_url);
		factory.setServiceClass(SSOServerImplPortType.class);
		SSOServerImplPortType service = (SSOServerImplPortType) factory.create();

		return service;
	}
}
