package com.javahis.device.provobj;
/**
 * ����Ϣ
 * @author lixiang
 *
 */
public class CardInfo {	
	int piInsID;       //O����ID[N8]
	String psCardID;   //O����[C16]
	String psName;//O����[C8]
	String psAreaCode; 	//O��������[C3]
	String psQueryID; //O���֤����[C18]
	String psUnitID;    //O��λID[C8]
	String psUnitName; //O��λ����[C50]
	String psSex;  //O�Ա�[C2](��/Ů)
	String psKind;   //O��Ա���[C4]
	String psBirthday;  //O��������[C10](YYYY-MM-DD)
	String psNational;  //O����[C20]
	String psIndustry;  //O�������[C20]
	String psDuty;    //O����ְ��[C30]
	String psChronic;  // O�������ⲡ��[C200](�ѻ����ء�δ֪��/����δ����ʱ���ء�δ���롯/������������������ⲡ��)
	String psOthers1;  // O����(����)[C200]
	float pcInHosNum;  //O�����ۼ�סԺ����[N9, 4]
	float pcAccIn;     //O�ʻ�����[N10, 2]
	float pcAccOut;    //O�ʻ���֧[N10, 2]
	float pcFeeNO;     //O֧���汾��[N10, 2]
	float pcPubPay;   //O����ͳ��֧���ۼ�[N10, 2]
	float pcHelpPay;   //O����󲡻���֧���ۼ�[N10, 2]
	float pcSupplyPay; //O���깫��Ա����/��ҵ����֧���ۼ�[N10, 2]
	float pcOutpatSum;  //O������ͨ��������ۼ�[N10, 2]
	float pcOutpatGen1;  //O������ͨ����������Χ�ڷ����ۼ�[N10, 2]	
	float pcOutpatGen2;  //O������������������Χ�ڷ����ۼ�[N10, 2]
	float pcOutpatGen3;  //O�������סԺ������Χ�ڷ����ۼ�[N10, 2]
	float pcInpatSum;    //O������ͨסԺ�����ۼ�[N10, 2]
	float pcInpatGen1;   //O������ͨסԺ������Χ�ڷ����ۼ�[N10, 2]
	float pcInpatGen2;   //O�����ͥ����סԺ������Χ���ۼ�[N10, 2]
	float pcOther1;		//O�����ۼ��Ը�[N10, 2]
	float pcOther2;		//O�����ۼ��Է�[N10, 2]
	float pcBankAccPay;  //O����[N10, 2]
	float pcOtrPay;	     //O������������֧���ۼ�[N10, 2]
	float pcCashPay;	//O�����ֽ�֧���ۼ�[N10, 2]
	float pcAccLeft;    //O�ʻ����[N10, 2]
	public int getPiInsID() {
		return piInsID;
	}
	public void setPiInsID(int piInsID) {
		this.piInsID = piInsID;
	}
	public String getPsCardID() {
		return psCardID;
	}
	public void setPsCardID(String psCardID) {
		this.psCardID = psCardID;
	}
	public String getPsName() {
		return psName;
	}
	public void setPsName(String psName) {
		this.psName = psName;
	}
	public String getPsAreaCode() {
		return psAreaCode;
	}
	public void setPsAreaCode(String psAreaCode) {
		this.psAreaCode = psAreaCode;
	}
	public String getPsQueryID() {
		return psQueryID;
	}
	public void setPsQueryID(String psQueryID) {
		this.psQueryID = psQueryID;
	}
	public String getPsUnitID() {
		return psUnitID;
	}
	public void setPsUnitID(String psUnitID) {
		this.psUnitID = psUnitID;
	}
	public String getPsUnitName() {
		return psUnitName;
	}
	public void setPsUnitName(String psUnitName) {
		this.psUnitName = psUnitName;
	}
	public String getPsSex() {
		return psSex;
	}
	public void setPsSex(String psSex) {
		this.psSex = psSex;
	}
	public String getPsKind() {
		return psKind;
	}
	public void setPsKind(String psKind) {
		this.psKind = psKind;
	}
	public String getPsBirthday() {
		return psBirthday;
	}
	public void setPsBirthday(String psBirthday) {
		this.psBirthday = psBirthday;
	}
	public String getPsNational() {
		return psNational;
	}
	public void setPsNational(String psNational) {
		this.psNational = psNational;
	}
	public String getPsIndustry() {
		return psIndustry;
	}
	public void setPsIndustry(String psIndustry) {
		this.psIndustry = psIndustry;
	}
	public String getPsDuty() {
		return psDuty;
	}
	public void setPsDuty(String psDuty) {
		this.psDuty = psDuty;
	}
	public String getPsChronic() {
		return psChronic;
	}
	public void setPsChronic(String psChronic) {
		this.psChronic = psChronic;
	}
	public String getPsOthers1() {
		return psOthers1;
	}
	public void setPsOthers1(String psOthers1) {
		this.psOthers1 = psOthers1;
	}
	public float getPcInHosNum() {
		return pcInHosNum;
	}
	public void setPcInHosNum(float pcInHosNum) {
		this.pcInHosNum = pcInHosNum;
	}
	public float getPcAccIn() {
		return pcAccIn;
	}
	public void setPcAccIn(float pcAccIn) {
		this.pcAccIn = pcAccIn;
	}
	public float getPcAccOut() {
		return pcAccOut;
	}
	public void setPcAccOut(float pcAccOut) {
		this.pcAccOut = pcAccOut;
	}
	public float getPcFeeNO() {
		return pcFeeNO;
	}
	public void setPcFeeNO(float pcFeeNO) {
		this.pcFeeNO = pcFeeNO;
	}
	public float getPcPubPay() {
		return pcPubPay;
	}
	public void setPcPubPay(float pcPubPay) {
		this.pcPubPay = pcPubPay;
	}
	public float getPcHelpPay() {
		return pcHelpPay;
	}
	public void setPcHelpPay(float pcHelpPay) {
		this.pcHelpPay = pcHelpPay;
	}
	public float getPcSupplyPay() {
		return pcSupplyPay;
	}
	public void setPcSupplyPay(float pcSupplyPay) {
		this.pcSupplyPay = pcSupplyPay;
	}
	public float getPcOutpatSum() {
		return pcOutpatSum;
	}
	public void setPcOutpatSum(float pcOutpatSum) {
		this.pcOutpatSum = pcOutpatSum;
	}
	public float getPcOutpatGen1() {
		return pcOutpatGen1;
	}
	public void setPcOutpatGen1(float pcOutpatGen1) {
		this.pcOutpatGen1 = pcOutpatGen1;
	}
	public float getPcOutpatGen2() {
		return pcOutpatGen2;
	}
	public void setPcOutpatGen2(float pcOutpatGen2) {
		this.pcOutpatGen2 = pcOutpatGen2;
	}
	public float getPcOutpatGen3() {
		return pcOutpatGen3;
	}
	public void setPcOutpatGen3(float pcOutpatGen3) {
		this.pcOutpatGen3 = pcOutpatGen3;
	}
	public float getPcInpatSum() {
		return pcInpatSum;
	}
	public void setPcInpatSum(float pcInpatSum) {
		this.pcInpatSum = pcInpatSum;
	}
	public float getPcInpatGen1() {
		return pcInpatGen1;
	}
	public void setPcInpatGen1(float pcInpatGen1) {
		this.pcInpatGen1 = pcInpatGen1;
	}
	public float getPcInpatGen2() {
		return pcInpatGen2;
	}
	public void setPcInpatGen2(float pcInpatGen2) {
		this.pcInpatGen2 = pcInpatGen2;
	}
	public float getPcOther1() {
		return pcOther1;
	}
	public void setPcOther1(float pcOther1) {
		this.pcOther1 = pcOther1;
	}
	public float getPcOther2() {
		return pcOther2;
	}
	public void setPcOther2(float pcOther2) {
		this.pcOther2 = pcOther2;
	}
	public float getPcBankAccPay() {
		return pcBankAccPay;
	}
	public void setPcBankAccPay(float pcBankAccPay) {
		this.pcBankAccPay = pcBankAccPay;
	}
	public float getPcOtrPay() {
		return pcOtrPay;
	}
	public void setPcOtrPay(float pcOtrPay) {
		this.pcOtrPay = pcOtrPay;
	}
	public float getPcCashPay() {
		return pcCashPay;
	}
	public void setPcCashPay(float pcCashPay) {
		this.pcCashPay = pcCashPay;
	}
	public float getPcAccLeft() {
		return pcAccLeft;
	}
	public void setPcAccLeft(float pcAccLeft) {
		this.pcAccLeft = pcAccLeft;
	}
	
	
}
