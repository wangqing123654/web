package com.javahis.bsm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
*
* <p>Title: ����ϸ�����</p>
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
@XmlRootElement(name = "CONSIS_PRESC_DTLVW")
public class PrescriptionTableDetail {

	private  String  PRESC_DATE  ; //����ʱ��
	private  String  PRESC_NO ;    //�������
	private  String  ITEM_NO;      //ҩƷ���
	private  String  FLG;          //�Ƿ��ҩ����ҩע��(���ֺ�װ)
	private  String  ATC_FLG;      //�����Ƿ��Ϳڷ���ҩ���ı�־
	private  String  ADVICE_CODE;  //ҽ�����
	private  String  DRUG_CODE ;   //ҩƷ���
	private  String  DRUG_NAME;    //ҩƷ����
	private  String  TRADE_NAME;   //ҩƷ��Ʒ��
	private  String  DRUG_SPEC;    //ҩƷ���
	private  String  DRUG_PACKAGE; //ҩƷ��װ���
	private  String  DRUG_UNIT;    //ҩƷ��λ
	private  String  FIRM_ID;      //ҩƷ����
	private  String  DRUG_PRICE;   //ҩƷ�۸�
	private  String  QUANTITY;     //����
	private  String  COSTS;        //����
	private  String  PAYMENTS;     //ʵ������
	private  String  DOSAGE;	   //ҩƷ������ÿ�η�������
	private  String  DOSAGE_UNITS; //������λ��ÿ�η��õ�λ��
	private  String  ADMINISTRATION;//ҩƷ�÷���ʹ�÷�����
	private  String  FREQUENCY;     //ҩƷ���� ��ʹ��Ƶ�� ÿ�켸�Σ�
    private  String  DISPESEDDOSE ;  //һ����İ�ҩ��
    private  String  DISPENSEDTOTALDOSE ;//��ҩ������
    private  String  DISPENSEDUNIT ;     //��ҩ��λ
    private  String  AMOUNT_PER_PACKAGE; //һƬ�ļ��� (��ֵ)
    private  String  DISPENSE_DAYS;      //��ҩ����
    private  String  FREQ_DESC_DETAIL_CODE;//����ʱ����� 
    private  String  FREQ_DESC_DETAIL;     //����ʱ��
    private  String  BARCODE ;    //����������
	/**
	 * �õ�����ʱ��
	 * @return
	 */
	public String getPRESC_DATE() {
		return PRESC_DATE;
	}
	/**
	 * ���ô���ʱ��
	 * @param pRESCDATE
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
	 * ҩƷ���
	 * @return
	 */
	public String getITEM_NO() {
		return ITEM_NO;
	}
    /**
     * ҩƷ���
     * @param iTEMNO
     */
	public void setITEM_NO(String iTEMNO) {
		ITEM_NO = iTEMNO;
	}
	/**
	 * ҽ�����
	 * @return
	 */
	public String getADVICE_CODE() {
		return ADVICE_CODE;
	}
	/**
	 * ҽ�����
	 * @param aDVICECODE
	 */
	public void setADVICE_CODE(String aDVICECODE) {
		ADVICE_CODE = aDVICECODE;
	}
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
	/**ҩƷ��װ���
	 * 
	 * @param dRUGPACKAGE
	 */
	public void setDRUG_PACKAGE(String dRUGPACKAGE) {
		DRUG_PACKAGE = dRUGPACKAGE;
	}
	/**
	 * ҩƷ��λ
	 * @return
	 */
	public String getDRUG_UNIT() {
		return DRUG_UNIT;
	}
	/**
	 * ҩƷ��λ
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
	 * ����
	 * @return
	 */
	public String getQUANTITY() {
		return QUANTITY;
	}
	/**
	 * ����
	 * @param qUANTITY
	 */
	public void setQUANTITY(String qUANTITY) {
		QUANTITY = qUANTITY;
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
	 * ҩƷ������ÿ�η�������
	 * @return
	 */
	public String getDOSAGE() {
		return DOSAGE;
	}
	/**
	 * ҩƷ������ÿ�η�������
	 * @param dOSAGE
	 */
	public void setDOSAGE(String dOSAGE) {
		DOSAGE = dOSAGE;
	}
	/**
	 * ������λ��ÿ�η��õ�λ�� 
	 * @return
	 */
	public String getDOSAGE_UNITS() {
		return DOSAGE_UNITS;
	}
	/**
	 * ������λ��ÿ�η��õ�λ�� 
	 * @param dOSAGEUNITS
	 */
	public void setDOSAGE_UNITS(String dOSAGEUNITS) {
		DOSAGE_UNITS = dOSAGEUNITS;
	}
	/**
	 * ҩƷ�÷���ʹ�÷�����
	 * @return
	 */
	public String getADMINISTRATION() {
		return ADMINISTRATION;
	}
	/**
	 * ҩƷ�÷���ʹ�÷�����
	 * @param aDMINISTRATION
	 */
	public void setADMINISTRATION(String aDMINISTRATION) {
		ADMINISTRATION = aDMINISTRATION;
	}
	/**
	 * ҩƷ���� ��ʹ��Ƶ�� ÿ�켸�Σ�
	 * @return
	 */
	public String getFREQUENCY() {
		return FREQUENCY;
	}
	/**
	 * ҩƷ���� ��ʹ��Ƶ�� ÿ�켸�Σ�
	 * @param fREQUENCY
	 */
	public void setFREQUENCY(String fREQUENCY) {
		FREQUENCY = fREQUENCY;
	}
	/**
	 * ��ҩ���Ƿ��ҩע��
	 * @return
	 */
	public String getFLG() {
		return FLG;
	}
	/**
	 * ��ҩ���Ƿ��ҩע��
	 * @param fLG
	 */
	public void setFLG(String fLG) {
		FLG = fLG;
	}
	/**
	 * һ����İ�ҩ��
	 * @return
	 */
	public String getDISPESEDDOSE() {
		return DISPESEDDOSE;
	}
	/**
	 * һ����İ�ҩ��
	 * @param dISPESEDDOSE
	 */
	public void setDISPESEDDOSE(String dISPESEDDOSE) {
		DISPESEDDOSE = dISPESEDDOSE;
	}
	/**
	 * ��ҩ������
	 * @return
	 */
	public String getDISPENSEDTOTALDOSE() {
		return DISPENSEDTOTALDOSE;
	}
	/**
	 * ��ҩ������
	 * @param dISPENSEDTOTALDOSE
	 */
	public void setDISPENSEDTOTALDOSE(String dISPENSEDTOTALDOSE) {
		DISPENSEDTOTALDOSE = dISPENSEDTOTALDOSE;
	}
	/**
	 * ��ҩ��λ
	 * @return
	 */
	public String getDISPENSEDUNIT() {
		return DISPENSEDUNIT;
	}
	/**
	 * ��ҩ��λ
	 * @param dISPENSEDUNIT
	 */
	public void setDISPENSEDUNIT(String dISPENSEDUNIT) {
		DISPENSEDUNIT = dISPENSEDUNIT;
	}
	/**
	 * һƬ�ļ��� (��ֵ)
	 * @return
	 */
	public String getAMOUNT_PER_PACKAGE() {
		return AMOUNT_PER_PACKAGE;
	}
	/**
	 * һƬ�ļ��� (��ֵ)
	 * @param aMOUNTPERPACKAGE
	 */
	public void setAMOUNT_PER_PACKAGE(String aMOUNTPERPACKAGE) {
		AMOUNT_PER_PACKAGE = aMOUNTPERPACKAGE;
	}
	/**
	 * ��ҩ����
	 * @return
	 */
	public String getDISPENSE_DAYS() {
		return DISPENSE_DAYS;
	}
	/**
	 * ��ҩ����
	 * @param dISPENSEDAYS
	 */
	public void setDISPENSE_DAYS(String dISPENSEDAYS) {
		DISPENSE_DAYS = dISPENSEDAYS;
	}
	/**
	 * ����ʱ�����
	 * @return
	 */
	public String getFREQ_DESC_DETAIL_CODE() {
		return FREQ_DESC_DETAIL_CODE;
	}
	/**
	 * ����ʱ�����
	 * @param fREQDESCDETAILCODE
	 */
	public void setFREQ_DESC_DETAIL_CODE(String fREQDESCDETAILCODE) {
		FREQ_DESC_DETAIL_CODE = fREQDESCDETAILCODE;
	}
	/**
	 * ����ʱ��
	 * @return
	 */
	public String getFREQ_DESC_DETAIL() {
		return FREQ_DESC_DETAIL;
	}
	/**
	 *����ʱ�� 
	 * @param fREQDESCDETAIL
	 */
	public void setFREQ_DESC_DETAIL(String fREQDESCDETAIL) {
		FREQ_DESC_DETAIL = fREQDESCDETAIL;
	}
	/**
	 * �����Ƿ��Ϳڷ���ҩ���ı�־
	 * @return
	 */
	public String getATC_FLG() {
		return ATC_FLG;
	}
	/**
	 * �����Ƿ��Ϳڷ���ҩ���ı�־
	 * @param aTCFLG
	 */
	public void setATC_FLG(String aTCFLG) {
		ATC_FLG = aTCFLG;
	}
	/**
	 * ����������
	 * @return
	 */
	public String getBAT_CODE() {
		return BARCODE;
	}
	/**
	 * ����������
	 * @param bATCODE
	 */
	public void setBAT_CODE(String bATCODE) {
		BARCODE = bATCODE;
	}
	
}
