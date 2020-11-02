package com.javahis.ui.odi.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.5.2
 * 2013-01-15T15:53:26.083+08:00
 * Generated source version: 2.5.2
 * 
 */
@WebService(targetNamespace = "http://services.spc.action/", name = "SpcOdiService")
@XmlSeeAlso({ObjectFactory.class})
public interface SpcOdiService {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "sendElectronicTag", targetNamespace = "http://services.spc.action/", className = "com.javahis.ui.odi.client.SendElectronicTag")
    @WebMethod
    @ResponseWrapper(localName = "sendElectronicTagResponse", targetNamespace = "http://services.spc.action/", className = "com.javahis.ui.odi.client.SendElectronicTagResponse")
    public java.lang.String sendElectronicTag(
        @WebParam(name = "arg0", targetNamespace = "")
        java.lang.String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        java.lang.String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        java.lang.String arg2,
        @WebParam(name = "arg3", targetNamespace = "")
        java.lang.String arg3
    );

}
