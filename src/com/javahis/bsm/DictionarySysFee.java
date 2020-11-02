package com.javahis.bsm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: ͬ���ֵ�SYS_FEE����</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.05.24 
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement(name = "CONSIS_BASIC_DRUGSVW ")
public class DictionarySysFee {

	/**
	 * ҩƷ���
	 */
	private String DRUG_CODE  ;          //ҩƷ���
	private String DRUG_NAME  ;          //ҩƷ����
	private String TRADE_NAME  ;         //ҩƷ��Ʒ��
	private String DRUG_SPEC  ;          //ҩƷ���
	private String DRUG_PACKAGE  ;       //ҩƷ��װ���
	private String DRUG_UNIT  ;          //ҩƷ��λHISϵͳ�ۼ۵�λ
	private String FIRM_ID  ;            //ҩƷ����
	private String DRUG_PRICE  ;         //ҩƷ�۸�
	private String DRUG_FORM  ;          //ҩƷ����
	private String DRUG_SORT  ;          //ҩƷ����ҩ��������ִ��ڷ��ŵķ������ƣ�������ࣩ
	private String BARCODE  ;            //ҩƷ����
	private String LAST_DATE  ;          //������ʱ��
	private String PINYIN  ;             //ҩƷƴ��
	private String DRUG_CONVERTATION  ;  //����ҩ����ҩ��λ��ҩƷ�ۼ۵�λ�������1�У�ҩ����ҩ��λ��=24Ƭ���ۼ۵�λ����Ϊ24
	private String ALLOWIND  ;           //	ҩƷ�Ƿ����á�Y�����á�N��ͣ��
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
	 * ҩƷ����
	 * @return
	 */
	public String getDRUG_NAME() {
		return DRUG_NAME;
	}
	/**
	 * ҩƷ����
	 * @param dRUGNAME
	 */
	public void setDRUG_NAME(String dRUGNAME) {
		DRUG_NAME = dRUGNAME;
	}
	/**
	 * ҩƷ��Ʒ��
	 * @return
	 */
	public String getTRADE_NAME() {
		return TRADE_NAME;
	}
	/**
	 * ҩƷ��Ʒ��
	 * @param tRADENAME
	 */
	public void setTRADE_NAME(String tRADENAME) {
		TRADE_NAME = tRADENAME;
	}
	/**
	 * ҩƷ���
	 * @return
	 */
	public String getDRUG_SPEC() {
		return DRUG_SPEC;
	}
	/**
	 * ҩƷ���
	 * @param dRUGSPEC
	 */
	public void setDRUG_SPEC(String dRUGSPEC) {
		DRUG_SPEC = dRUGSPEC;
	}
	/**
	 * ҩƷ��װ���
	 * @return
	 */
	public String getDRUG_PACKAGE() {
		return DRUG_PACKAGE;
	}
	/**
	 * ҩƷ��װ���
	 * @param dRUGPACKAGE
	 */
	public void setDRUG_PACKAGE(String dRUGPACKAGE) {
		DRUG_PACKAGE = dRUGPACKAGE;
	}
	/**
	 * ҩƷ��λHISϵͳ�ۼ۵�λ
	 * @return
	 */
	public String getDRUG_UNIT() {
		return DRUG_UNIT;
	}
	/**
	 * ҩƷ��λHISϵͳ�ۼ۵�λ
	 * @param dRUGUNIT
	 */
	public void setDRUG_UNIT(String dRUGUNIT) {
		DRUG_UNIT = dRUGUNIT;
	}
	/**
	 * ҩƷ����
	 * @return
	 */
	public String getFIRM_ID() {
		return FIRM_ID;
	}
	/**
	 * ҩƷ����
	 * @param fIRMID
	 */
	public void setFIRM_ID(String fIRMID) {
		FIRM_ID = fIRMID;
	}
	/**
	 * ҩƷ�۸�
	 * @return
	 */
	public String getDRUG_PRICE() {
		return DRUG_PRICE;
	}
	/**
	 * ҩƷ�۸�
	 * @param dRUGPRICE
	 */
	public void setDRUG_PRICE(String dRUGPRICE) {
		DRUG_PRICE = dRUGPRICE;
	}
	/**
	 * ҩƷ����
	 * @return
	 */
	public String getDRUG_FORM() {
		return DRUG_FORM;
	}
	/**
	 * ҩƷ����
	 * @param dRUGFORM
	 */
	public void setDRUG_FORM(String dRUGFORM) {
		DRUG_FORM = dRUGFORM;
	}
	/**
	 * ҩƷ����
	 * @return
	 */
	public String getDRUG_SORT() {
		return DRUG_SORT;
	}
	/**
	 * ҩƷ����
	 * @param dRUGSORT
	 */
	public void setDRUG_SORT(String dRUGSORT) {
		DRUG_SORT = dRUGSORT;
	}
	/**
	 * ҩƷ����
	 * @return
	 */
	public String getBARCODE() {
		return BARCODE;
	}
	/**
	 * ҩƷ����
	 * @param bARCODE
	 */
	public void setBARCODE(String bARCODE) {
		BARCODE = bARCODE;
	}
	/**
	 * ������ʱ��
	 * @return
	 */
	public String getLAST_DATE() {
		return LAST_DATE;
	}
	/**
	 * ������ʱ��
	 * @param lASTDATE
	 */
	public void setLAST_DATE(String lASTDATE) {
		LAST_DATE = lASTDATE;
	}
	/**
	 * ҩƷƴ��
	 * @return
	 */
	public String getPINYIN() {
		return PINYIN;
	}
	/**
	 * ҩƷƴ��
	 * @param pINYIN
	 */
	public void setPINYIN(String pINYIN) {
		PINYIN = pINYIN;
	}
	/**
	 * ������
ҩ����ҩ��λ��ҩƷ�ۼ۵�λ�����
��1�У�ҩ����ҩ��λ��
=24Ƭ���ۼ۵�λ����Ϊ24

	 * @return
	 */
	public String getDRUG_CONVERTATION() {
		return DRUG_CONVERTATION;
	}
	/**
	 * ������
ҩ����ҩ��λ��ҩƷ�ۼ۵�λ�����
��1�У�ҩ����ҩ��λ��
=24Ƭ���ۼ۵�λ����Ϊ24

	 * @param dRUGCONVERTATION
	 */
	public void setDRUG_CONVERTATION(String dRUGCONVERTATION) {
		DRUG_CONVERTATION = dRUGCONVERTATION;
	}
	/**
	 * ҩƷ�Ƿ�����
	 * @return
	 */
	public String getALLOWIND() {
		return ALLOWIND;
	}
	/**
	 * ҩƷ�Ƿ�����
	 * @param aLLOWIND
	 */
	public void setALLOWIND(String aLLOWIND) {
		ALLOWIND = aLLOWIND;
	}

	
}
