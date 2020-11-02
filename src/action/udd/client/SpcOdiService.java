package action.udd.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2013-01-16T10:30:35.284+08:00
 * Generated source version: 2.5.2
 * 
 */
@WebService(targetNamespace = "http://services.spc.action/", name = "SpcOdiService")
@XmlSeeAlso({ObjectFactory.class})
public interface SpcOdiService {

     

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "examine", targetNamespace = "http://services.spc.action/", className = "action.udd.client.Examine")
    @WebMethod
    @ResponseWrapper(localName = "examineResponse", targetNamespace = "http://services.spc.action/", className = "action.udd.client.ExamineResponse")
    public java.lang.String examine(
        @WebParam(name = "arg0", targetNamespace = "")
        action.udd.client.SpcOdiDspnms arg0
    );

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "onUpdateRtnCfm", targetNamespace = "http://services.spc.action/", className = "action.udd.client.OnUpdateRtnCfm")
    @WebMethod
    @ResponseWrapper(localName = "onUpdateRtnCfmResponse", targetNamespace = "http://services.spc.action/", className = "action.udd.client.OnUpdateRtnCfmResponse")
    public java.lang.String onUpdateRtnCfm(
        @WebParam(name = "arg0", targetNamespace = "")
        action.udd.client.SpcOdiDspnms arg0
    );
    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "onCheckStockQty", targetNamespace = "http://services.spc.action/", className = "action.udd.client.OnCheckStockQty")
    @WebMethod
    @ResponseWrapper(localName = "onCheckStockQtyResponse", targetNamespace = "http://services.spc.action/", className = "action.udd.client.OnCheckStockQtyResponse")
    public java.lang.String onCheckStockQty(
        @WebParam(name = "arg0", targetNamespace = "")
        action.udd.client.SpcOdiDspnms arg0
    );
 
}
