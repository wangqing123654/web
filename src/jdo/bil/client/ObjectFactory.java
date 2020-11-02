
package jdo.bil.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the jdo.bil.client package. 
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

    private final static QName _OnMrNo_QNAME = new QName("http://bil.jdo/", "onMrNo");
    private final static QName _OnFeeData_QNAME = new QName("http://bil.jdo/", "onFeeData");
    private final static QName _InsertIBSOrder_QNAME = new QName("http://bil.jdo/", "insertIBSOrder");
    private final static QName _OnMrNoResponse_QNAME = new QName("http://bil.jdo/", "onMrNoResponse");
    private final static QName _InsertOpdOrderResponse_QNAME = new QName("http://bil.jdo/", "insertOpdOrderResponse");
    private final static QName _OnFeeDataResponse_QNAME = new QName("http://bil.jdo/", "onFeeDataResponse");
    private final static QName _InsertIBSOrderResponse_QNAME = new QName("http://bil.jdo/", "insertIBSOrderResponse");
    private final static QName _InsertOpdOrder_QNAME = new QName("http://bil.jdo/", "insertOpdOrder");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: jdo.bil.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OnFeeData }
     * 
     */
    public OnFeeData createOnFeeData() {
        return new OnFeeData();
    }

    /**
     * Create an instance of {@link OnMrNo }
     * 
     */
    public OnMrNo createOnMrNo() {
        return new OnMrNo();
    }

    /**
     * Create an instance of {@link InsertOpdOrder }
     * 
     */
    public InsertOpdOrder createInsertOpdOrder() {
        return new InsertOpdOrder();
    }

    /**
     * Create an instance of {@link InsertIBSOrderResponse }
     * 
     */
    public InsertIBSOrderResponse createInsertIBSOrderResponse() {
        return new InsertIBSOrderResponse();
    }

    /**
     * Create an instance of {@link OnFeeDataResponse }
     * 
     */
    public OnFeeDataResponse createOnFeeDataResponse() {
        return new OnFeeDataResponse();
    }

    /**
     * Create an instance of {@link InsertOpdOrderResponse }
     * 
     */
    public InsertOpdOrderResponse createInsertOpdOrderResponse() {
        return new InsertOpdOrderResponse();
    }

    /**
     * Create an instance of {@link OnMrNoResponse }
     * 
     */
    public OnMrNoResponse createOnMrNoResponse() {
        return new OnMrNoResponse();
    }

    /**
     * Create an instance of {@link InsertIBSOrder }
     * 
     */
    public InsertIBSOrder createInsertIBSOrder() {
        return new InsertIBSOrder();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnMrNo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://bil.jdo/", name = "onMrNo")
    public JAXBElement<OnMrNo> createOnMrNo(OnMrNo value) {
        return new JAXBElement<OnMrNo>(_OnMrNo_QNAME, OnMrNo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnFeeData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://bil.jdo/", name = "onFeeData")
    public JAXBElement<OnFeeData> createOnFeeData(OnFeeData value) {
        return new JAXBElement<OnFeeData>(_OnFeeData_QNAME, OnFeeData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertIBSOrder }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://bil.jdo/", name = "insertIBSOrder")
    public JAXBElement<InsertIBSOrder> createInsertIBSOrder(InsertIBSOrder value) {
        return new JAXBElement<InsertIBSOrder>(_InsertIBSOrder_QNAME, InsertIBSOrder.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnMrNoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://bil.jdo/", name = "onMrNoResponse")
    public JAXBElement<OnMrNoResponse> createOnMrNoResponse(OnMrNoResponse value) {
        return new JAXBElement<OnMrNoResponse>(_OnMrNoResponse_QNAME, OnMrNoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertOpdOrderResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://bil.jdo/", name = "insertOpdOrderResponse")
    public JAXBElement<InsertOpdOrderResponse> createInsertOpdOrderResponse(InsertOpdOrderResponse value) {
        return new JAXBElement<InsertOpdOrderResponse>(_InsertOpdOrderResponse_QNAME, InsertOpdOrderResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnFeeDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://bil.jdo/", name = "onFeeDataResponse")
    public JAXBElement<OnFeeDataResponse> createOnFeeDataResponse(OnFeeDataResponse value) {
        return new JAXBElement<OnFeeDataResponse>(_OnFeeDataResponse_QNAME, OnFeeDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertIBSOrderResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://bil.jdo/", name = "insertIBSOrderResponse")
    public JAXBElement<InsertIBSOrderResponse> createInsertIBSOrderResponse(InsertIBSOrderResponse value) {
        return new JAXBElement<InsertIBSOrderResponse>(_InsertIBSOrderResponse_QNAME, InsertIBSOrderResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertOpdOrder }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://bil.jdo/", name = "insertOpdOrder")
    public JAXBElement<InsertOpdOrder> createInsertOpdOrder(InsertOpdOrder value) {
        return new JAXBElement<InsertOpdOrder>(_InsertOpdOrder_QNAME, InsertOpdOrder.class, null, value);
    }

}
