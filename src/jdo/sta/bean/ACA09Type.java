//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.06.30 at 10:28:02 ���� CST 
//


package jdo.sta.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ACA09Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ACA09Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ACA0901C" type="{}String"/>
 *         &lt;element name="ACA0901N" type="{}String"/>
 *         &lt;element name="ACA0902C" type="{}ACA0902CType"/>
 *         &lt;element name="ACA0903C" type="{}ACA0903CType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ACA09Type", propOrder = {
    "aca0901C",
    "aca0901N",
    "aca0902C",
    "aca0903C"
})
public class ACA09Type {

    @XmlElement(name = "ACA0901C", required = true)
    protected String aca0901C;
    @XmlElement(name = "ACA0901N", required = true)
    protected String aca0901N;
    @XmlElement(name = "ACA0902C", required = true)
    protected ACA0902CType aca0902C;
    @XmlElement(name = "ACA0903C", required = true)
    protected ACA0903CType aca0903C;

    /**
     * Gets the value of the aca0901C property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACA0901C() {
        return aca0901C;
    }

    /**
     * Sets the value of the aca0901C property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACA0901C(String value) {
        this.aca0901C = value;
    }

    /**
     * Gets the value of the aca0901N property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACA0901N() {
        return aca0901N;
    }

    /**
     * Sets the value of the aca0901N property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACA0901N(String value) {
        this.aca0901N = value;
    }

    /**
     * Gets the value of the aca0902C property.
     * 
     * @return
     *     possible object is
     *     {@link ACA0902CType }
     *     
     */
    public ACA0902CType getACA0902C() {
        return aca0902C;
    }

    /**
     * Sets the value of the aca0902C property.
     * 
     * @param value
     *     allowed object is
     *     {@link ACA0902CType }
     *     
     */
    public void setACA0902C(ACA0902CType value) {
        this.aca0902C = value;
    }

    /**
     * Gets the value of the aca0903C property.
     * 
     * @return
     *     possible object is
     *     {@link ACA0903CType }
     *     
     */
    public ACA0903CType getACA0903C() {
        return aca0903C;
    }

    /**
     * Sets the value of the aca0903C property.
     * 
     * @param value
     *     allowed object is
     *     {@link ACA0903CType }
     *     
     */
    public void setACA0903C(ACA0903CType value) {
        this.aca0903C = value;
    }

}
