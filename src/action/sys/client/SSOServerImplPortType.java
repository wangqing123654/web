
package action.sys.client;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.1 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "SSOServerImplPortType", targetNamespace = "http://impl.service.remote.www.whaosoft.com/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface SSOServerImplPortType {


    /**
     * 
     * @param user
     * @return
     *     returns java.util.List<java.lang.Object>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "doAddLDAPUser", targetNamespace = "http://impl.service.remote.www.whaosoft.com/", className = "action.sys.client.DoAddLDAPUser")
    @ResponseWrapper(localName = "doAddLDAPUserResponse", targetNamespace = "http://impl.service.remote.www.whaosoft.com/", className = "action.sys.client.DoAddLDAPUserResponse")
    public List<Object> doAddLDAPUser(
        @WebParam(name = "user", targetNamespace = "")
        UserDTO user);

    /**
     * 
     * @param userName
     * @return
     *     returns action.sys.client.UserDTO
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "doQueryLDAPUser", targetNamespace = "http://impl.service.remote.www.whaosoft.com/", className = "action.sys.client.DoQueryLDAPUser")
    @ResponseWrapper(localName = "doQueryLDAPUserResponse", targetNamespace = "http://impl.service.remote.www.whaosoft.com/", className = "action.sys.client.DoQueryLDAPUserResponse")
    public UserDTO doQueryLDAPUser(
        @WebParam(name = "userName", targetNamespace = "")
        String userName);

    /**
     * 
     * @param userName
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "doGetUserPw", targetNamespace = "http://impl.service.remote.www.whaosoft.com/", className = "action.sys.client.DoGetUserPw")
    @ResponseWrapper(localName = "doGetUserPwResponse", targetNamespace = "http://impl.service.remote.www.whaosoft.com/", className = "action.sys.client.DoGetUserPwResponse")
    public String doGetUserPw(
        @WebParam(name = "userName", targetNamespace = "")
        String userName);

    /**
     * 
     * @param uname
     * @param password
     * @return
     *     returns java.util.List<java.lang.Object>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "doCheckLDAPUser", targetNamespace = "http://impl.service.remote.www.whaosoft.com/", className = "action.sys.client.DoCheckLDAPUser")
    @ResponseWrapper(localName = "doCheckLDAPUserResponse", targetNamespace = "http://impl.service.remote.www.whaosoft.com/", className = "action.sys.client.DoCheckLDAPUserResponse")
    public List<Object> doCheckLDAPUser(
        @WebParam(name = "uname", targetNamespace = "")
        String uname,
        @WebParam(name = "password", targetNamespace = "")
        String password);

    /**
     * 
     * @param oldpassword
     * @param newPassword
     * @param userName
     * @return
     *     returns java.util.List<java.lang.Object>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "doModifyLDAPUserPw", targetNamespace = "http://impl.service.remote.www.whaosoft.com/", className = "action.sys.client.DoModifyLDAPUserPw")
    @ResponseWrapper(localName = "doModifyLDAPUserPwResponse", targetNamespace = "http://impl.service.remote.www.whaosoft.com/", className = "action.sys.client.DoModifyLDAPUserPwResponse")
    public List<Object> doModifyLDAPUserPw(
        @WebParam(name = "userName", targetNamespace = "")
        String userName,
        @WebParam(name = "oldpassword", targetNamespace = "")
        String oldpassword,
        @WebParam(name = "newPassword", targetNamespace = "")
        String newPassword);

}
