
package action.sys.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the action.sys.client package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _DoCheckLDAPUser_QNAME = new QName("http://impl.service.remote.www.whaosoft.com/", "doCheckLDAPUser");
    private final static QName _DoQueryLDAPUserResponse_QNAME = new QName("http://impl.service.remote.www.whaosoft.com/", "doQueryLDAPUserResponse");
    private final static QName _DoAddLDAPUserResponse_QNAME = new QName("http://impl.service.remote.www.whaosoft.com/", "doAddLDAPUserResponse");
    private final static QName _DoGetUserPw_QNAME = new QName("http://impl.service.remote.www.whaosoft.com/", "doGetUserPw");
    private final static QName _DoGetUserPwResponse_QNAME = new QName("http://impl.service.remote.www.whaosoft.com/", "doGetUserPwResponse");
    private final static QName _DoModifyLDAPUserPw_QNAME = new QName("http://impl.service.remote.www.whaosoft.com/", "doModifyLDAPUserPw");
    private final static QName _DoCheckLDAPUserResponse_QNAME = new QName("http://impl.service.remote.www.whaosoft.com/", "doCheckLDAPUserResponse");
    private final static QName _DoAddLDAPUser_QNAME = new QName("http://impl.service.remote.www.whaosoft.com/", "doAddLDAPUser");
    private final static QName _DoQueryLDAPUser_QNAME = new QName("http://impl.service.remote.www.whaosoft.com/", "doQueryLDAPUser");
    private final static QName _DoModifyLDAPUserPwResponse_QNAME = new QName("http://impl.service.remote.www.whaosoft.com/", "doModifyLDAPUserPwResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: action.sys.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DoCheckLDAPUser }
     * 
     */
    public DoCheckLDAPUser createDoCheckLDAPUser() {
        return new DoCheckLDAPUser();
    }

    /**
     * Create an instance of {@link DoModifyLDAPUserPwResponse }
     * 
     */
    public DoModifyLDAPUserPwResponse createDoModifyLDAPUserPwResponse() {
        return new DoModifyLDAPUserPwResponse();
    }

    /**
     * Create an instance of {@link DoQueryLDAPUser }
     * 
     */
    public DoQueryLDAPUser createDoQueryLDAPUser() {
        return new DoQueryLDAPUser();
    }

    /**
     * Create an instance of {@link DoGetUserPwResponse }
     * 
     */
    public DoGetUserPwResponse createDoGetUserPwResponse() {
        return new DoGetUserPwResponse();
    }

    /**
     * Create an instance of {@link DoQueryLDAPUserResponse }
     * 
     */
    public DoQueryLDAPUserResponse createDoQueryLDAPUserResponse() {
        return new DoQueryLDAPUserResponse();
    }

    /**
     * Create an instance of {@link DoGetUserPw }
     * 
     */
    public DoGetUserPw createDoGetUserPw() {
        return new DoGetUserPw();
    }

    /**
     * Create an instance of {@link DoAddLDAPUserResponse }
     * 
     */
    public DoAddLDAPUserResponse createDoAddLDAPUserResponse() {
        return new DoAddLDAPUserResponse();
    }

    /**
     * Create an instance of {@link DoAddLDAPUser }
     * 
     */
    public DoAddLDAPUser createDoAddLDAPUser() {
        return new DoAddLDAPUser();
    }

    /**
     * Create an instance of {@link UserDTO }
     * 
     */
    public UserDTO createUserDTO() {
        return new UserDTO();
    }

    /**
     * Create an instance of {@link DoCheckLDAPUserResponse }
     * 
     */
    public DoCheckLDAPUserResponse createDoCheckLDAPUserResponse() {
        return new DoCheckLDAPUserResponse();
    }

    /**
     * Create an instance of {@link DoModifyLDAPUserPw }
     * 
     */
    public DoModifyLDAPUserPw createDoModifyLDAPUserPw() {
        return new DoModifyLDAPUserPw();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoCheckLDAPUser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.service.remote.www.whaosoft.com/", name = "doCheckLDAPUser")
    public JAXBElement<DoCheckLDAPUser> createDoCheckLDAPUser(DoCheckLDAPUser value) {
        return new JAXBElement<DoCheckLDAPUser>(_DoCheckLDAPUser_QNAME, DoCheckLDAPUser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoQueryLDAPUserResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.service.remote.www.whaosoft.com/", name = "doQueryLDAPUserResponse")
    public JAXBElement<DoQueryLDAPUserResponse> createDoQueryLDAPUserResponse(DoQueryLDAPUserResponse value) {
        return new JAXBElement<DoQueryLDAPUserResponse>(_DoQueryLDAPUserResponse_QNAME, DoQueryLDAPUserResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoAddLDAPUserResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.service.remote.www.whaosoft.com/", name = "doAddLDAPUserResponse")
    public JAXBElement<DoAddLDAPUserResponse> createDoAddLDAPUserResponse(DoAddLDAPUserResponse value) {
        return new JAXBElement<DoAddLDAPUserResponse>(_DoAddLDAPUserResponse_QNAME, DoAddLDAPUserResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoGetUserPw }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.service.remote.www.whaosoft.com/", name = "doGetUserPw")
    public JAXBElement<DoGetUserPw> createDoGetUserPw(DoGetUserPw value) {
        return new JAXBElement<DoGetUserPw>(_DoGetUserPw_QNAME, DoGetUserPw.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoGetUserPwResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.service.remote.www.whaosoft.com/", name = "doGetUserPwResponse")
    public JAXBElement<DoGetUserPwResponse> createDoGetUserPwResponse(DoGetUserPwResponse value) {
        return new JAXBElement<DoGetUserPwResponse>(_DoGetUserPwResponse_QNAME, DoGetUserPwResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoModifyLDAPUserPw }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.service.remote.www.whaosoft.com/", name = "doModifyLDAPUserPw")
    public JAXBElement<DoModifyLDAPUserPw> createDoModifyLDAPUserPw(DoModifyLDAPUserPw value) {
        return new JAXBElement<DoModifyLDAPUserPw>(_DoModifyLDAPUserPw_QNAME, DoModifyLDAPUserPw.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoCheckLDAPUserResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.service.remote.www.whaosoft.com/", name = "doCheckLDAPUserResponse")
    public JAXBElement<DoCheckLDAPUserResponse> createDoCheckLDAPUserResponse(DoCheckLDAPUserResponse value) {
        return new JAXBElement<DoCheckLDAPUserResponse>(_DoCheckLDAPUserResponse_QNAME, DoCheckLDAPUserResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoAddLDAPUser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.service.remote.www.whaosoft.com/", name = "doAddLDAPUser")
    public JAXBElement<DoAddLDAPUser> createDoAddLDAPUser(DoAddLDAPUser value) {
        return new JAXBElement<DoAddLDAPUser>(_DoAddLDAPUser_QNAME, DoAddLDAPUser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoQueryLDAPUser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.service.remote.www.whaosoft.com/", name = "doQueryLDAPUser")
    public JAXBElement<DoQueryLDAPUser> createDoQueryLDAPUser(DoQueryLDAPUser value) {
        return new JAXBElement<DoQueryLDAPUser>(_DoQueryLDAPUser_QNAME, DoQueryLDAPUser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoModifyLDAPUserPwResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.service.remote.www.whaosoft.com/", name = "doModifyLDAPUserPwResponse")
    public JAXBElement<DoModifyLDAPUserPwResponse> createDoModifyLDAPUserPwResponse(DoModifyLDAPUserPwResponse value) {
        return new JAXBElement<DoModifyLDAPUserPwResponse>(_DoModifyLDAPUserPwResponse_QNAME, DoModifyLDAPUserPwResponse.class, null, value);
    }

}
