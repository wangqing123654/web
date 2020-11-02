
package com.capinfo.dataswap.webservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.capinfo.dataswap.webservice package. 
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

    private final static QName _CancleOrderInfo_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "cancleOrderInfo");
    private final static QName _DelOrderResponse_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "delOrderResponse");
    private final static QName _FindMember_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "findMember");
    private final static QName _OrderCount_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "orderCount");
    private final static QName _CancelUpdateOrderResponse_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "cancelUpdateOrderResponse");
    private final static QName _AddOrder_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "addOrder");
    private final static QName _LoadBehavior_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "loadBehavior");
    private final static QName _UpdateOrderResponse_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "updateOrderResponse");
    private final static QName _CreateMember_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "createMember");
    private final static QName _AddOrderInfoResponse_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "addOrderInfoResponse");
    private final static QName _UpdateMember_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "updateMember");
    private final static QName _OrderCountResponse_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "orderCountResponse");
    private final static QName _OrderInfo_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "orderInfo");
    private final static QName _SystemState_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "systemState");
    private final static QName _DelOrder_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "delOrder");
    private final static QName _AddOrderResponse_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "addOrderResponse");
    private final static QName _UpdateCode_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "updateCode");
    private final static QName _LoadBehaviorResponse_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "loadBehaviorResponse");
    private final static QName _GetOrder_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "getOrder");
    private final static QName _CreateMemberResponse_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "createMemberResponse");
    private final static QName _UpdateMemberByMrNoResponse_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "updateMemberByMrNoResponse");
    private final static QName _CancleOrderInfoResponse_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "cancleOrderInfoResponse");
    private final static QName _UpdateOrder_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "updateOrder");
    private final static QName _UpdateMemberByMrNo_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "updateMemberByMrNo");
    private final static QName _SystemStateResponse_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "systemStateResponse");
    private final static QName _CancelUpdateOrder_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "cancelUpdateOrder");
    private final static QName _FindMemberResponse_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "findMemberResponse");
    private final static QName _AddOrderInfo_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "addOrderInfo");
    private final static QName _OrderInfoResponse_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "orderInfoResponse");
    private final static QName _GetOrderResponse_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "getOrderResponse");
    private final static QName _UpdateCodeResponse_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "updateCodeResponse");
    private final static QName _UpdateMemberResponse_QNAME = new QName("http://webservice.dataswap.capinfo.com/", "updateMemberResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.capinfo.dataswap.webservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AddOrder }
     * 
     */
    public AddOrder createAddOrder() {
        return new AddOrder();
    }

    /**
     * Create an instance of {@link LoadBehavior }
     * 
     */
    public LoadBehavior createLoadBehavior() {
        return new LoadBehavior();
    }

    /**
     * Create an instance of {@link UpdateOrderResponse }
     * 
     */
    public UpdateOrderResponse createUpdateOrderResponse() {
        return new UpdateOrderResponse();
    }

    /**
     * Create an instance of {@link DelOrderResponse }
     * 
     */
    public DelOrderResponse createDelOrderResponse() {
        return new DelOrderResponse();
    }

    /**
     * Create an instance of {@link CancleOrderInfo }
     * 
     */
    public CancleOrderInfo createCancleOrderInfo() {
        return new CancleOrderInfo();
    }

    /**
     * Create an instance of {@link FindMember }
     * 
     */
    public FindMember createFindMember() {
        return new FindMember();
    }

    /**
     * Create an instance of {@link CancelUpdateOrderResponse }
     * 
     */
    public CancelUpdateOrderResponse createCancelUpdateOrderResponse() {
        return new CancelUpdateOrderResponse();
    }

    /**
     * Create an instance of {@link OrderCount }
     * 
     */
    public OrderCount createOrderCount() {
        return new OrderCount();
    }

    /**
     * Create an instance of {@link OrderInfo }
     * 
     */
    public OrderInfo createOrderInfo() {
        return new OrderInfo();
    }

    /**
     * Create an instance of {@link SystemState }
     * 
     */
    public SystemState createSystemState() {
        return new SystemState();
    }

    /**
     * Create an instance of {@link CreateMember }
     * 
     */
    public CreateMember createCreateMember() {
        return new CreateMember();
    }

    /**
     * Create an instance of {@link AddOrderInfoResponse }
     * 
     */
    public AddOrderInfoResponse createAddOrderInfoResponse() {
        return new AddOrderInfoResponse();
    }

    /**
     * Create an instance of {@link UpdateMember }
     * 
     */
    public UpdateMember createUpdateMember() {
        return new UpdateMember();
    }

    /**
     * Create an instance of {@link OrderCountResponse }
     * 
     */
    public OrderCountResponse createOrderCountResponse() {
        return new OrderCountResponse();
    }

    /**
     * Create an instance of {@link UpdateMemberByMrNoResponse }
     * 
     */
    public UpdateMemberByMrNoResponse createUpdateMemberByMrNoResponse() {
        return new UpdateMemberByMrNoResponse();
    }

    /**
     * Create an instance of {@link CancleOrderInfoResponse }
     * 
     */
    public CancleOrderInfoResponse createCancleOrderInfoResponse() {
        return new CancleOrderInfoResponse();
    }

    /**
     * Create an instance of {@link UpdateOrder }
     * 
     */
    public UpdateOrder createUpdateOrder() {
        return new UpdateOrder();
    }

    /**
     * Create an instance of {@link UpdateMemberByMrNo }
     * 
     */
    public UpdateMemberByMrNo createUpdateMemberByMrNo() {
        return new UpdateMemberByMrNo();
    }

    /**
     * Create an instance of {@link SystemStateResponse }
     * 
     */
    public SystemStateResponse createSystemStateResponse() {
        return new SystemStateResponse();
    }

    /**
     * Create an instance of {@link CancelUpdateOrder }
     * 
     */
    public CancelUpdateOrder createCancelUpdateOrder() {
        return new CancelUpdateOrder();
    }

    /**
     * Create an instance of {@link FindMemberResponse }
     * 
     */
    public FindMemberResponse createFindMemberResponse() {
        return new FindMemberResponse();
    }

    /**
     * Create an instance of {@link AddOrderInfo }
     * 
     */
    public AddOrderInfo createAddOrderInfo() {
        return new AddOrderInfo();
    }

    /**
     * Create an instance of {@link DelOrder }
     * 
     */
    public DelOrder createDelOrder() {
        return new DelOrder();
    }

    /**
     * Create an instance of {@link AddOrderResponse }
     * 
     */
    public AddOrderResponse createAddOrderResponse() {
        return new AddOrderResponse();
    }

    /**
     * Create an instance of {@link UpdateCode }
     * 
     */
    public UpdateCode createUpdateCode() {
        return new UpdateCode();
    }

    /**
     * Create an instance of {@link LoadBehaviorResponse }
     * 
     */
    public LoadBehaviorResponse createLoadBehaviorResponse() {
        return new LoadBehaviorResponse();
    }

    /**
     * Create an instance of {@link CreateMemberResponse }
     * 
     */
    public CreateMemberResponse createCreateMemberResponse() {
        return new CreateMemberResponse();
    }

    /**
     * Create an instance of {@link GetOrder }
     * 
     */
    public GetOrder createGetOrder() {
        return new GetOrder();
    }

    /**
     * Create an instance of {@link UpdateMemberResponse }
     * 
     */
    public UpdateMemberResponse createUpdateMemberResponse() {
        return new UpdateMemberResponse();
    }

    /**
     * Create an instance of {@link GetOrderResponse }
     * 
     */
    public GetOrderResponse createGetOrderResponse() {
        return new GetOrderResponse();
    }

    /**
     * Create an instance of {@link OrderInfoResponse }
     * 
     */
    public OrderInfoResponse createOrderInfoResponse() {
        return new OrderInfoResponse();
    }

    /**
     * Create an instance of {@link UpdateCodeResponse }
     * 
     */
    public UpdateCodeResponse createUpdateCodeResponse() {
        return new UpdateCodeResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancleOrderInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "cancleOrderInfo")
    public JAXBElement<CancleOrderInfo> createCancleOrderInfo(CancleOrderInfo value) {
        return new JAXBElement<CancleOrderInfo>(_CancleOrderInfo_QNAME, CancleOrderInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DelOrderResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "delOrderResponse")
    public JAXBElement<DelOrderResponse> createDelOrderResponse(DelOrderResponse value) {
        return new JAXBElement<DelOrderResponse>(_DelOrderResponse_QNAME, DelOrderResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindMember }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "findMember")
    public JAXBElement<FindMember> createFindMember(FindMember value) {
        return new JAXBElement<FindMember>(_FindMember_QNAME, FindMember.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrderCount }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "orderCount")
    public JAXBElement<OrderCount> createOrderCount(OrderCount value) {
        return new JAXBElement<OrderCount>(_OrderCount_QNAME, OrderCount.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelUpdateOrderResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "cancelUpdateOrderResponse")
    public JAXBElement<CancelUpdateOrderResponse> createCancelUpdateOrderResponse(CancelUpdateOrderResponse value) {
        return new JAXBElement<CancelUpdateOrderResponse>(_CancelUpdateOrderResponse_QNAME, CancelUpdateOrderResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddOrder }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "addOrder")
    public JAXBElement<AddOrder> createAddOrder(AddOrder value) {
        return new JAXBElement<AddOrder>(_AddOrder_QNAME, AddOrder.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LoadBehavior }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "loadBehavior")
    public JAXBElement<LoadBehavior> createLoadBehavior(LoadBehavior value) {
        return new JAXBElement<LoadBehavior>(_LoadBehavior_QNAME, LoadBehavior.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateOrderResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "updateOrderResponse")
    public JAXBElement<UpdateOrderResponse> createUpdateOrderResponse(UpdateOrderResponse value) {
        return new JAXBElement<UpdateOrderResponse>(_UpdateOrderResponse_QNAME, UpdateOrderResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateMember }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "createMember")
    public JAXBElement<CreateMember> createCreateMember(CreateMember value) {
        return new JAXBElement<CreateMember>(_CreateMember_QNAME, CreateMember.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddOrderInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "addOrderInfoResponse")
    public JAXBElement<AddOrderInfoResponse> createAddOrderInfoResponse(AddOrderInfoResponse value) {
        return new JAXBElement<AddOrderInfoResponse>(_AddOrderInfoResponse_QNAME, AddOrderInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateMember }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "updateMember")
    public JAXBElement<UpdateMember> createUpdateMember(UpdateMember value) {
        return new JAXBElement<UpdateMember>(_UpdateMember_QNAME, UpdateMember.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrderCountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "orderCountResponse")
    public JAXBElement<OrderCountResponse> createOrderCountResponse(OrderCountResponse value) {
        return new JAXBElement<OrderCountResponse>(_OrderCountResponse_QNAME, OrderCountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrderInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "orderInfo")
    public JAXBElement<OrderInfo> createOrderInfo(OrderInfo value) {
        return new JAXBElement<OrderInfo>(_OrderInfo_QNAME, OrderInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SystemState }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "systemState")
    public JAXBElement<SystemState> createSystemState(SystemState value) {
        return new JAXBElement<SystemState>(_SystemState_QNAME, SystemState.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DelOrder }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "delOrder")
    public JAXBElement<DelOrder> createDelOrder(DelOrder value) {
        return new JAXBElement<DelOrder>(_DelOrder_QNAME, DelOrder.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddOrderResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "addOrderResponse")
    public JAXBElement<AddOrderResponse> createAddOrderResponse(AddOrderResponse value) {
        return new JAXBElement<AddOrderResponse>(_AddOrderResponse_QNAME, AddOrderResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateCode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "updateCode")
    public JAXBElement<UpdateCode> createUpdateCode(UpdateCode value) {
        return new JAXBElement<UpdateCode>(_UpdateCode_QNAME, UpdateCode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LoadBehaviorResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "loadBehaviorResponse")
    public JAXBElement<LoadBehaviorResponse> createLoadBehaviorResponse(LoadBehaviorResponse value) {
        return new JAXBElement<LoadBehaviorResponse>(_LoadBehaviorResponse_QNAME, LoadBehaviorResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetOrder }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "getOrder")
    public JAXBElement<GetOrder> createGetOrder(GetOrder value) {
        return new JAXBElement<GetOrder>(_GetOrder_QNAME, GetOrder.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateMemberResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "createMemberResponse")
    public JAXBElement<CreateMemberResponse> createCreateMemberResponse(CreateMemberResponse value) {
        return new JAXBElement<CreateMemberResponse>(_CreateMemberResponse_QNAME, CreateMemberResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateMemberByMrNoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "updateMemberByMrNoResponse")
    public JAXBElement<UpdateMemberByMrNoResponse> createUpdateMemberByMrNoResponse(UpdateMemberByMrNoResponse value) {
        return new JAXBElement<UpdateMemberByMrNoResponse>(_UpdateMemberByMrNoResponse_QNAME, UpdateMemberByMrNoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancleOrderInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "cancleOrderInfoResponse")
    public JAXBElement<CancleOrderInfoResponse> createCancleOrderInfoResponse(CancleOrderInfoResponse value) {
        return new JAXBElement<CancleOrderInfoResponse>(_CancleOrderInfoResponse_QNAME, CancleOrderInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateOrder }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "updateOrder")
    public JAXBElement<UpdateOrder> createUpdateOrder(UpdateOrder value) {
        return new JAXBElement<UpdateOrder>(_UpdateOrder_QNAME, UpdateOrder.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateMemberByMrNo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "updateMemberByMrNo")
    public JAXBElement<UpdateMemberByMrNo> createUpdateMemberByMrNo(UpdateMemberByMrNo value) {
        return new JAXBElement<UpdateMemberByMrNo>(_UpdateMemberByMrNo_QNAME, UpdateMemberByMrNo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SystemStateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "systemStateResponse")
    public JAXBElement<SystemStateResponse> createSystemStateResponse(SystemStateResponse value) {
        return new JAXBElement<SystemStateResponse>(_SystemStateResponse_QNAME, SystemStateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelUpdateOrder }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "cancelUpdateOrder")
    public JAXBElement<CancelUpdateOrder> createCancelUpdateOrder(CancelUpdateOrder value) {
        return new JAXBElement<CancelUpdateOrder>(_CancelUpdateOrder_QNAME, CancelUpdateOrder.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindMemberResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "findMemberResponse")
    public JAXBElement<FindMemberResponse> createFindMemberResponse(FindMemberResponse value) {
        return new JAXBElement<FindMemberResponse>(_FindMemberResponse_QNAME, FindMemberResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddOrderInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "addOrderInfo")
    public JAXBElement<AddOrderInfo> createAddOrderInfo(AddOrderInfo value) {
        return new JAXBElement<AddOrderInfo>(_AddOrderInfo_QNAME, AddOrderInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrderInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "orderInfoResponse")
    public JAXBElement<OrderInfoResponse> createOrderInfoResponse(OrderInfoResponse value) {
        return new JAXBElement<OrderInfoResponse>(_OrderInfoResponse_QNAME, OrderInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetOrderResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "getOrderResponse")
    public JAXBElement<GetOrderResponse> createGetOrderResponse(GetOrderResponse value) {
        return new JAXBElement<GetOrderResponse>(_GetOrderResponse_QNAME, GetOrderResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateCodeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "updateCodeResponse")
    public JAXBElement<UpdateCodeResponse> createUpdateCodeResponse(UpdateCodeResponse value) {
        return new JAXBElement<UpdateCodeResponse>(_UpdateCodeResponse_QNAME, UpdateCodeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateMemberResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.dataswap.capinfo.com/", name = "updateMemberResponse")
    public JAXBElement<UpdateMemberResponse> createUpdateMemberResponse(UpdateMemberResponse value) {
        return new JAXBElement<UpdateMemberResponse>(_UpdateMemberResponse_QNAME, UpdateMemberResponse.class, null, value);
    }

}
