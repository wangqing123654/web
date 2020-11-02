package com.javahis.bsm;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
*
* <p>Title: �����������</p>
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
@XmlRootElement(name = "CONSIS_PRESC_MSTVW")
public class PrescriptionTableMain {

	@XmlElement(name = "CONSIS_PRESC_DTLVW")
	private  List<PrescriptionTableDetail> detail; //������ϸ
	private  String  PRESC_DATE;    //����ʱ��
	private  String  PRESC_NO;      //�������
	private  String  DISPENSARY;    //��ҩҩ��
	private  String  PATIENT_ID;    //���￨��
	private  String  PATIENT_NAME;  //��������
	private  String  PATIENT_TYPE ; //��������
	private  String  DATE_OF_BIRTH; //���߳�������
	private  String  SEX;           //�����Ա�
	private  String  PRESC_IDENTITY;//�������
	private  String  CHARGE_TYPE;	//ҽ������
	private  String  PRESC_ATTR;    //��������
	private  String  PRESC_INFO;    //��������
	private  String  RCPT_INFO;     //�����Ϣ
	private  String  RCPT_REMARK;   //������ע��Ϣ
	private  String  REPETITION;    //����
	private  String  COSTS;        //����
	private  String  PAYMENTS;     //ʵ������
	private  String  ORDERED_BY;   //��������
	private  String  PRESCRIBED_BY;//����ҽ��
	private  String  ENTERED_BY;   //¼����
	private  String  DISPENSE_PRI; //��ҩ���ȼ������Ѵ���ҩ�����룩���ִ�С�����ʾ���ȼ��Ӹߵ���
	private  String  ORDERED_ID ;  //�������Ҵ���
	private  String  PRESCRIBED_ID ;//����ҽ������
	/**
	 * ����ʱ��
	 * @return
	 */
	public String getPRESC_DATE() {
		return PRESC_DATE;
	}
	/**
	 * 
	 * ����ʱ��
	 */
	public void setPRESC_DATE(String pRESCDATE) {
		PRESC_DATE = pRESCDATE;
	}
	/**
	 * �������
	 * @return
	 */
	public String getPRESC_NO() {
		return PRESC_NO;
	}
	/**
	 * �������
	 * @param pRESCNO
	 */
	public void setPRESC_NO(String pRESCNO) {
		PRESC_NO = pRESCNO;
	}
	/**
	 * ��ҩҩ��
	 * @return
	 */
	public String getDISPENSARY() {
		return DISPENSARY;
	}
	/**
	 * ��ҩҩ��
	 * @param dISPENSARY
	 */
	public void setDISPENSARY(String dISPENSARY) {
		DISPENSARY = dISPENSARY;
	}
	/**
	 * ���￨��
	 * @return
	 */
	public String getPATIENT_ID() {
		return PATIENT_ID;
	}
	/**
	 * ���￨��
	 * @param pATIENTID
	 */
	public void setPATIENT_ID(String pATIENTID) {
		PATIENT_ID = pATIENTID;
	}
	/**
	 * ��������
	 * @return
	 */
	public String getPATIENT_NAME() {
		return PATIENT_NAME;
	}
	/**
	 * ��������
	 * @param pATIENTNAME
	 */
	public void setPATIENT_NAME(String pATIENTNAME) {
		PATIENT_NAME = pATIENTNAME;
	}
	/**
	 * ��������
��00�� ��ͨ
��01�� ���� 

	 * @return
	 */
	public String getPATIENT_TYPE() {
		return PATIENT_TYPE;
	}
	/**
	 * ��������
��00�� ��ͨ
��01�� ���� 

	 * @param pATIENTTYPE
	 */
	public void setPATIENT_TYPE(String pATIENTTYPE) {
		PATIENT_TYPE = pATIENTTYPE;
	}
	/**
	 * ���߳�������
	 * @return
	 */
	public String getDATE_OF_BIRTH() {
		return DATE_OF_BIRTH;
	}
	/**
	 * ���߳�������
	 * @param dATEOFBIRTH
	 */
	public void setDATE_OF_BIRTH(String dATEOFBIRTH) {
		DATE_OF_BIRTH = dATEOFBIRTH;
	}
	/**
	 * �����Ա�
	 * @return
	 */
	public String getSEX() {
		return SEX;
	}
	/**
	 * �����Ա�
	 * @param sEX
	 */
	public void setSEX(String sEX) {
		SEX = sEX;
	}
	/**
	 * �������
	 * @return
	 */
	public String getPRESC_IDENTITY() {
		return PRESC_IDENTITY;
	}
	/**
	 * �������
	 * @param pRESCIDENTITY
	 */
	public void setPRESC_IDENTITY(String pRESCIDENTITY) {
		PRESC_IDENTITY = pRESCIDENTITY;
	}
	/**
	 * ҽ������
	 * @return
	 */
	public String getCHARGE_TYPE() {
		return CHARGE_TYPE;
	}
	/**
	 * ҽ������
	 * @param cHARGETYPE
	 */
	public void setCHARGE_TYPE(String cHARGETYPE) {
		CHARGE_TYPE = cHARGETYPE;
	}
	/**
	 * ��������
�ֹ���������ʱ�������ı���Ϣ

	 * @return
	 */
	public String getPRESC_ATTR() {
		return PRESC_ATTR;
	}
	/**
	 * ��������
�ֹ���������ʱ�������ı���Ϣ

	 * @param pRESCATTR
	 */
	public void setPRESC_ATTR(String pRESCATTR) {
		PRESC_ATTR = pRESCATTR;
	}
	/**
	 * ��������
������ش��������ı���Ϣ���Ʒѷ�ʽ��

	 * @return
	 */
	public String getPRESC_INFO() {
		return PRESC_INFO;
	}
	/**
	 * ��������
������ش��������ı���Ϣ���Ʒѷ�ʽ��

	 * @param pRESCINFO
	 */
	public void setPRESC_INFO(String pRESCINFO) {
		PRESC_INFO = pRESCINFO;
	}
	/**
	 * �����Ϣ
	 * @return
	 */
	public String getRCPT_INFO() {
		return RCPT_INFO;
	}
	/**
	 * �����Ϣ
	 * @param rCPTINFO
	 */
	public void setRCPT_INFO(String rCPTINFO) {
		RCPT_INFO = rCPTINFO;
	}
	/**
	 * ������ע��Ϣ
	 * @return
	 */
	public String getRCPT_REMARK() {
		return RCPT_REMARK;
	}
	/**
	 * ������ע��Ϣ
	 * @param rCPTREMARK
	 */
	public void setRCPT_REMARK(String rCPTREMARK) {
		RCPT_REMARK = rCPTREMARK;
	}
	/**
	 * ����
	 * @return
	 */
	public String getREPETITION() {
		return REPETITION;
	}
	/**
	 * ����
	 * @param rEPETITION
	 */
	public void setREPETITION(String rEPETITION) {
		REPETITION = rEPETITION;
	}
	/**
	 * ����
	 * @return
	 */
	public String getCOSTS() {
		return COSTS;
	}
	/**
	 * ����
	 * @param cOSTS
	 */
	public void setCOSTS(String cOSTS) {
		COSTS = cOSTS;
	}
	/**
	 * ʵ������
	 * @return
	 */
	public String getPAYMENTS() {
		return PAYMENTS;
	}
	/**
	 * ʵ������
	 * @param pAYMENTS
	 */
	public void setPAYMENTS(String pAYMENTS) {
		PAYMENTS = pAYMENTS;
	}
	/**
	 * ��������
	 * @return
	 */
	public String getORDERED_BY() {
		return ORDERED_BY;
	}
	/**
	 * ��������
	 * @param oRDEREDBY
	 */
	public void setORDERED_BY(String oRDEREDBY) {
		ORDERED_BY = oRDEREDBY;
	}
	/**
	 * ����ҽ��
	 * @return
	 */
	public String getPRESCRIBED_BY() {
		return PRESCRIBED_BY;
	}
	/**
	 * ����ҽ��
	 * @param pRESCRIBEDBY
	 */
	public void setPRESCRIBED_BY(String pRESCRIBEDBY) {
		PRESCRIBED_BY = pRESCRIBEDBY;
	}
	/**
	 * ¼����
	 * @return
	 */
	public String getENTERED_BY() {
		return ENTERED_BY;
	}
	/**
	 * ¼����
	 * @param eNTEREDBY
	 */
	public void setENTERED_BY(String eNTEREDBY) {
		ENTERED_BY = eNTEREDBY;
	}
	/**
	 * ��ҩ���ȼ������Ѵ���ҩ�����룩���ִ�С�����ʾ���ȼ��Ӹߵ���
	 * @return
	 */
	public String getDISPENSE_PRI() {
		return DISPENSE_PRI;
	}
	/**
	 * ��ҩ���ȼ������Ѵ���ҩ�����룩���ִ�С�����ʾ���ȼ��Ӹߵ���
	 * @param dISPENSEPRI
	 */
	public void setDISPENSE_PRI(String dISPENSEPRI) {
		DISPENSE_PRI = dISPENSEPRI;
	}
	/**
	 * �õ�������ϸ
	 * @return
	 */
	 public List<PrescriptionTableDetail> getDetail() {  
	        if (detail == null) {  
	        	detail = new ArrayList<PrescriptionTableDetail>();  
	        }  
	        return this.detail;  
	    }
	 /**
	  * �������Ҵ���
	  * @return
	  */
	public String getORDERED_ID() {
		return ORDERED_ID;
	}
	/**
	 * �������Ҵ���
	 * @param oRDEREDID
	 */
	public void setORDERED_ID(String oRDEREDID) {
		ORDERED_ID = oRDEREDID;
	}
	/**
	 * ����ҽ������
	 * @return
	 */
	public String getPRESCRIBED_ID() {
		return PRESCRIBED_ID;
	}
	/**
	 * ����ҽ������
	 * @param pRESCRIBEDID
	 */
	public void setPRESCRIBED_ID(String pRESCRIBEDID) {
		PRESCRIBED_ID = pRESCRIBEDID;
	}  
	 
}
