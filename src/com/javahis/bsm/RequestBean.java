package com.javahis.bsm;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: ���쵥����</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.05.14 
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD)  
@XmlRootElement(name = "ROOT")
public class RequestBean {
	private String BARCODE;      //������ҩƷ����
	private String   APPQUANTITY;  //��������
	private String   CURQUANTITY;  //��ǰ���
	/**
	 * ������ҩƷ����
	 * @return
	 */
	public String getBARCODE() {
		return BARCODE;
	}
	/**
	 * ������ҩƷ����
	 * @param bARCODE
	 */
	public void setBARCODE(String bARCODE) {
		BARCODE = bARCODE;
	}
	/**
	 * ��������
	 * @return
	 */
	public String getAPPQUANTITY() {
		return APPQUANTITY;
	}
	/**
	 * ��������
	 * @param aPPQUANTITY
	 */
	public void setAPPQUANTITY(String aPPQUANTITY) {
		APPQUANTITY = aPPQUANTITY;
	}
	/**
	 * ��ǰ���
	 * @return
	 */
	public String getCURQUANTITY() {
		return CURQUANTITY;
	}
	/**
	 * ��ǰ���
	 * @param cURQUANTITY
	 */
	public void setCURQUANTITY(String cURQUANTITY) {
		CURQUANTITY = cURQUANTITY;
	}
	
}
