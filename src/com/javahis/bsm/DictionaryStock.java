package com.javahis.bsm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: ͬ���ֵ������</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.05.26 
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement(name = "CONSIS_PHC_STORAGEVW ")   
public class DictionaryStock {

	private String DRUG_CODE ;
	private String DISPENSARY ;
	private String DRUG_QUANTITY ;
	private String LOCATIONINFO ;
	/**
	 * ҩƷ���
	 * @return
	 */
	public String getDRUG_CODE() {
		return DRUG_CODE;
	}
	/**
	 * ҩƷ���
	 * @param dRUGCODE
	 */
	public void setDRUG_CODE(String dRUGCODE) {
		DRUG_CODE = dRUGCODE;
	}
	/**
	 * ��ҩҩ�ִ���
	 * @return
	 */
	public String getDISPENSARY() {
		return DISPENSARY;
	}
	/**
	 * ��ҩҩ�ִ���
	 * @param dISPENSARY
	 */
	public void setDISPENSARY(String dISPENSARY) {
		DISPENSARY = dISPENSARY;
	}
	/**
	 * ҩƷ����
	 * @return
	 */
	public String getDRUG_QUANTITY() {
		return DRUG_QUANTITY;
	}
	/**
	 * ҩƷ����
	 * @param dRUGQUANTITY
	 */
	public void setDRUG_QUANTITY(String dRUGQUANTITY) {
		DRUG_QUANTITY = dRUGQUANTITY;
	}
	/**
	 * ҩƷ��λ
	 * @return
	 */
	public String getLOCATIONINFO() {
		return LOCATIONINFO;
	}
	/**
	 * ҩƷ��λ
	 * @param lOCATIONINFO
	 */
	public void setLOCATIONINFO(String lOCATIONINFO) {
		LOCATIONINFO = lOCATIONINFO;
	}
	
}
