
package com.capinfo.dataswap.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for loadBehavior complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="loadBehavior">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mrNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="jsonObject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "loadBehavior", propOrder = {
    "mrNo",
    "jsonObject"
})
public class LoadBehavior {

    protected String mrNo;
    protected String jsonObject;

    /**
     * Gets the value of the mrNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMrNo() {
        return mrNo;
    }

    /**
     * Sets the value of the mrNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMrNo(String value) {
        this.mrNo = value;
    }

    /**
     * Gets the value of the jsonObject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJsonObject() {
        return jsonObject;
    }

    /**
     * Sets the value of the jsonObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJsonObject(String value) {
        this.jsonObject = value;
    }

}
