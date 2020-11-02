package jdo.opd;

import java.sql.*;

import com.dongyang.data.*;
import com.dongyang.util.*;
import jdo.sys.*;

/**
 *
 * <p>
 * Title: ҩ��������Ŀ��������jdo
 * </p>
 *
 * <p>
 * drNote:ҩ��������Ŀ��������jdo
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 200800908
 * @version 1.0
 */
public class Order
    extends TModifiedData {
    /**
     * ��������
     */
    private Pat pat;
    /**
     * Ԥ�������
     * caowl 20131117
     * */
//    private boolean isPreOrder=false;
    private String isPreOrder;
    
	/**
     *Ԥ��ʱ�� 
     *caowl 20131117
     * */
    private Timestamp preDate;
    /**
     * ������
     */
    private String mrNo;
    /**
     * �������
     */
    private StringValue caseNo = new StringValue(this);
    /**
     * ����ǩ��
     */
    private StringValue rxNo = new StringValue(this);
    /**
     * �����
     */
    private StringValue medApplyNo = new StringValue(this);
    /**
     * ѭ���
     */
    private IntValue seqNo = new IntValue(this);
    /**
     * �����ˆT
     */
    private StringValue optUser = new StringValue(this);
    /**
     * ��������
     */
    private TimestampValue optDate = new TimestampValue(this);
    /**
     * '������ĩ
     */
    private StringValue optTerm = new StringValue(this);
    /**
     * ����
     */
    private IntValue presrtNo = new IntValue(this);
    /**
     * ��������
     */
    private StringValue regionCode = new StringValue(this);
    /**
     * ����״̬
     */
    private StringValue admType = new StringValue(this);
    /**
     * ҽ������
     */
    private StringValue rxType = new StringValue(this);
    /**
     * �ݴ�ע��
     */
    private StringValue temporaryFlg = new StringValue(this);
    /**
     * '����ע�� (�Ա�)'
     */
    private StringValue releaseFlg = new StringValue(this);
    /**
     * ������ע��
     */
    private StringValue linkmainFlg = new StringValue(this);
    /**
     * ���Ӻ�
     */
    private StringValue linkNo = new StringValue(this);
    /**
     * ҽ�����
     */
    private StringValue orderCode = new StringValue(this);
    /**
     * ҽ������
     */
    private StringValue orderDesc = new StringValue(this);
    /**
     * ��Ʒ��
     */
    private StringValue goodsDesc = new StringValue(this);
    /**
     * ҽ��ϸ����
     */
    private StringValue orderCat1Code = new StringValue(this);
    /**
     * ����
     */
    private DoubleValue mediQty = new DoubleValue(this);
    /**
     * ��ҩ��λ
     */
    private StringValue mediUnit = new StringValue(this);
    /**
     * Ƶ�δ���
     */
    private StringValue freqCode = new StringValue(this);
    /**
     * ��ҩ;������
     */
    private StringValue routeCode = new StringValue(this);
    /**
     * ����/�շ�
     */
    private IntValue takeDays = new IntValue(this);
    /**
     * ����
     */
    private DoubleValue dosageQty = new DoubleValue(this);
    /**
     * ʵ�ʷ�ҩ��
     */
    private DoubleValue dispenseQty = new DoubleValue(this);
    /**
     * ��ҩ��λ
     */
    private StringValue dispenseUnit = new StringValue(this);
    /**
     * ��浥λ��ҩע��
     */
    private StringValue giveboxFlg = new StringValue(this);
    /**
     * �ԷѼ�
     */
    private DoubleValue ownPrice = new DoubleValue(this);
    /**
     * ҽ����
     */
    private DoubleValue nhiPrice = new DoubleValue(this);
    /**
     * ����ۿ�%
     */
    private DoubleValue discountRate = new DoubleValue(this);
    /**
     * �Էѽ��
     */
    private DoubleValue ownAmt = new DoubleValue(this);
    /**
     * Ӧ�����
     */
    private DoubleValue arAmt = new DoubleValue(this);
    /**
     * ��ע��
     */
    private StringValue drNote = new StringValue(this);
    /**
     * ��ʿ��ע
     */
    private StringValue nsNote = new StringValue(this);
    /**
     * ����ҽʦ
     */
    private StringValue drCode = new StringValue(this);
    /**
     * \��������ʱ��
     */
    private TimestampValue orderDate=new TimestampValue(this) ;
    /**
     * ��������
     */
    private StringValue deptCode = new StringValue(this);
    /**
     * ͣ��ҽʦ
     */
    private StringValue dcDrCode = new StringValue(this);
    /**
     * ͣ��ʱ��
     */
    private TimestampValue dcOrderDate=new TimestampValue(this) ;
    /**
     * ͣ�ÿ���
     */
    private StringValue dcDeptCode = new StringValue(this);
    /**
     * ִ�п���
     */
    private StringValue execDeptCode = new StringValue(this);
    /**
     * ����ҽ��ע��
     */
    private StringValue setmainFlg = new StringValue(this);
    /**
     * ����ҽ����ţ���������ͬʱ����2����ͬ�ļ���ҽ����
     */
    private IntValue orderSetGroupNo = new IntValue(this);
    /**
     * ����ҽ�����
     */
    private StringValue ordersetCode = new StringValue(this);
    /**
     * ����ע��
     */
    private StringValue hideFlg = new StringValue(this);
    /**
     * �������
     */
    private StringValue rpttypeCode = new StringValue(this);
    /**
     * ����
     */
    private StringValue optitemCode = new StringValue(this);
    /**
     * ��������
     */
    private StringValue devCode = new StringValue(this);
    /**
     * ���뵥ģ��
     */
    private StringValue mrCode = new StringValue(this);
    /**
     * �ṹ�����뵥���
     */
    private IntValue fileNo = new IntValue(this);
    /**
     * ҽ�켨Ч����
     */
    private StringValue degreeCode = new StringValue(this);
    /**
     * ����ע��
     */
    private StringValue urgentFlg = new StringValue(this);
    /**
     * �������
     */
    private StringValue inspayType = new StringValue(this);
    /**
     * ҩƷ����
     */
    private StringValue phaType = new StringValue(this);
    /**
     * ���ʹ����
     */
    private StringValue doseType = new StringValue(this);
    /**
     * ��ͯ����ǩע��
     */
    private StringValue printtypeflgInfant = new StringValue(this);
    /**
     * ����ע��
     */
    private StringValue expensiveFlg = new StringValue(this);
    /**
     * ����ҩƷ����
     */
    private StringValue ctrldrugclassCode = new StringValue(this);
    /**
     * ��ҩ��
     */
    private IntValue prescriptNo = new IntValue(this);
    /**
     * �Ͱ�ҩ��ע��
     */
    private StringValue atcFlg = new StringValue(this);
    /**
     * �Ͱ�ҩ��ʱ��
     */
    private TimestampValue sendatcdate=new TimestampValue(this);
    /**
     * �ż����վݺ�
     */
    private StringValue receiptNo = new StringValue(this);
    /**
     * ����ע��
     */
    private StringValue billFlg = new StringValue(this);
    /**
     * �շ�����ʱ��
     */
    private TimestampValue billDate=new TimestampValue(this);
    /**
     * �շ���Ա
     */
    private StringValue billUser = new StringValue(this);
    /**
     * Ʊ�ݴ�ӡע��
     */
    private StringValue printFlg = new StringValue(this);
    /**
     * �վݷ��ô���
     */
    private StringValue rexpCode = new StringValue(this);
    /**
     * Ժ�ڷ��ô���
     */
    private StringValue hexpCode = new StringValue(this);
    /**
     * ֧����ͬ��λ����
     */
    private StringValue contractCode = new StringValue(this);
    /**
     * ���
     */
    private StringValue ctz1Code = new StringValue(this);
    /**
     * �ۿ�1
     */
    private StringValue ctz2Code = new StringValue(this);
    /**
     * �ۿ�2
     */
    private StringValue ctz3Code = new StringValue(this);
    /**
     * ���ҩʦ
     */
    private StringValue phaCheckCode = new StringValue(this);
    /**
     * ���ʱ��
     */
    private TimestampValue phaCheckDate=new TimestampValue(this);
    /**
     * ��ҩҩʦ
     */
    private StringValue phaDosageCode = new StringValue(this);
    /**
     * ��ҩʱ��
     */
    private TimestampValue phaDosageDate=new TimestampValue(this);
    /**
     * \��ҩҩʦ
     */
    private StringValue phaDispenseCode = new StringValue(this);
    /**
     * ��ҩȷ��ʱ��
     */
    private TimestampValue phaDispenseDate=new TimestampValue(this);
    /**
     * ҽ��ȷ�ϻ�ʿ
     */
    private StringValue nsExecCode= new StringValue(this);
    /**
     * ҽ��ȷ��ʱ��
     */
    private TimestampValue nsExecDate=new TimestampValue(this);
    /**
     * ҽ��ȷ�Ͽ���
     */
    private StringValue nsExecDept = new StringValue(this);
    /**
     * ��ҩ��ʽ
     */
    private StringValue dctagentCode = new StringValue(this);
    /**
     * ����巨
     */
    private StringValue dctexcepCode = new StringValue(this);
    /**
     * ��Ƭ������(ml) or ��Ƭʹ�ü���'
     */
    private IntValue dctTakeQty = new IntValue(this);
    /**
     * �����ܰ���
     */
    private IntValue packageTot = new IntValue(this);
    /**
     * ������ҩҩ��
     */
    private StringValue agencyOrgCode = new StringValue(this);
    /**
     * ����ע��
     */
    private StringValue dctagentFlg = new StringValue(this);
    /**
     * ��ҩ��
     */
    private StringValue decoctCode = new StringValue(this);
    /**
     * ִ��ҽ��
     */
    private StringValue execDrCode=new StringValue(this);
    /**
     * ��ҩʱ�䣨PHAר�ã�
     */
    private TimestampValue phaRetnDate=new TimestampValue(this);
    /**
     * ִ��ע�ǣ�PHAר�ã�
     */
    private boolean exeFlg=true;
    /**
     * ��ҩ��Ա
     */
    private StringValue phaRetnCode=new StringValue(this);
    /**
     * ���
     */
    private StringValue specification=new StringValue(this);
    /**
     * �Ʒѵ�λ
     */
    private StringValue dosageUnit =new StringValue(this);
    /**
     * �շѱ��
     */
    private BooleanValue chargeFlg = new BooleanValue(this);
    //��¼ҽ����ҽ������
    private boolean dcOredr=false;

    /**
     * ��ҩ���ں�
     */
    private IntValue counterNo = new IntValue(this);

    /**
     * ִ��ע��
     */
    private StringValue execFlg =new StringValue(this);

    /**
     * ��ҩ��
     */
    private StringValue printNo = new StringValue(this);
    /**
     * ҽ��ϸ����Ⱥ��
     */
    private StringValue cat1Type = new StringValue(this);
    /**
     * �ɱ�����
     */
    private StringValue costCenterCode = new StringValue(this);

    /**
     * ҽ������(��)
     * ====pangben 2012-8-8
     */
    private StringValue nhiCodeO = new StringValue(this);
    /**
     * ҽ������(��)
     * ====pangben 2012-8-8
     */
    private StringValue nhiCodeE = new StringValue(this);
    /**
     * ҽ������(ס)
     * ====pangben 2012-8-8
     */
    private StringValue nhiCodeI = new StringValue(this);
    /**
     * �ڲ����׺���
     */
    private String businessNo;//BUSINESS_NO
    
    private String memPackageId;//MEM_PACKAGE_ID
    
    private String updateTime; //ʱ���
    
    public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
    
    /**
     * ����
     */
    private String batchNo;
    
    /**
     * Ƥ�ԡ���������
     */
    private String skintestFlg;
    
    /**
     * ִ��ʱ�䣨��Ϊ����ʱ�䣩
     */
    private TimestampValue execDate=new TimestampValue(this);;
    
    /**
     * ��������
     */
    private String age;
    
    /**
     * ��������
     */
    private String weight;
    
    /**
     * ���·ַ�����´�����
     */
    private String rxPresertNo;
    
    private Timestamp birthDate;
    
    public String getRxPresertNo() {
		return rxPresertNo;
	}
	public void setRxPresertNo(String rxPresertNo) {
		this.rxPresertNo = rxPresertNo;
	}
	public String getMemPackageId() {
		return memPackageId;
	}
	public void setMemPackageId(String memPackageId) {
		this.memPackageId = memPackageId;
	}
	public String getBusinessNo(){
    	return businessNo;
    }
    public void setBusinessNo(String businessNo){
    	this.businessNo=businessNo;
    }
    //caowl 20131117 start
    /**
     * �õ�Ԥ�������
     * */
    public String getIsPreOrder() {
		return isPreOrder;
	}
    /**
     * ����Ԥ�������
     * */
	public void setIsPreOrder(String isPreOrder) {
		this.isPreOrder = isPreOrder;
	}
	/**
	 * 
	 * */
	public Timestamp getPreDate() {
		return preDate;
	}
	public void setPreDate(Timestamp preDate) {
		this.preDate = preDate;
	}
	//caowl 20131117 end
	/**
     * ȡ����ҩ��
     * @return String
     */
    public String getPrintNo() {
        return printNo.getValue();
    }

    /**
     * ������ҩ��
     * @param value String
     */
    public void setPrintNo(String value) {
        printNo.setValue(value);
    }
    /**
     * ȡ��ִ��ע��
     * @return String
     */
    public String getExecFlg() {
        return execFlg.getValue();
    }

    /**
     * ����ִ��ע��
     * @param value String
     */
    public void setExecFlg(String value) {
        execFlg.setValue(value);
    }

    /**
     * ��Ʊע��
     */
    private StringValue receiptFlg =new StringValue(this);

    /**
     * �õ���Ʊע��
     * @return String
     */
    public String getReceiptFlg() {
        return receiptFlg.getValue();
    }

    /**
     * ���ô�Ʊע��
     * @param value String
     */
    public void setReceiptFlg(String value) {
        receiptFlg.setValue(value);
    }
    /**
     * �շ����
     */
    private StringValue billType = new StringValue(this);

    /**
     * �õ��շ����
     * @return String
     */
    public String getBillType() {
        return billType.getValue();
    }

    /**
     * �����շ����
     * @param value String
     */
    public void setBillType(String value) {
        this.billType.setValue(value);
    }
    /**
     * �޸��շ����
     * @param value String
     */

    public void modifyBillType(String value) {
        this.billType.modifyValue(value);
    }

    /**
     * ȡ����ҩ���ں�
     * @return int
     */
    public int getCounterNo() {
        return counterNo.getValue();
    }

    /**
     * ������ҩ���ں�
     * @param value int
     */
    public void setCounterNo(int value) {
        counterNo.setValue(value);
    }

    //����ҽ���������
    public void setDcOrder(boolean dcOredr){
        this.dcOredr=dcOredr;
    }
    //�õ�ҽ������ҽ��ע��
    public boolean getDcOrder() {
        return this.dcOredr;
    }
    /**
     * �õ�����
     * @return Pat
     */
    public Pat getPat() {
        return pat;
    }

    /**
     * ���ò���
     * @param pat Pat
     */
    public void setPat(Pat pat) {
        this.pat = pat;
    }
    /**
     * ���ò�����
     * @param mrNo String
     */
    public void setMrNo(String mrNo){
    	if(getPat()==null)
    		this.mrNo="";
    	this.mrNo=mrNo;
    }
    /**
     * �õ�������
     * @return mrNo String
     */
    public String getMrNo(){
    	return this.mrNo;
    }
    /**
     * �õ��������
     * @return String
     */
    public String getCaseNo() {
        return caseNo.getValue();
    }

    /**
     * ���ÿ������
     * @param value String
     */
    public void setCaseNo(String value) {
        this.caseNo.setValue(value);
    }

    /**
     * �õ�������
     * @return String
     */
    public String getRxNo() {
        return rxNo.getValue();
    }
    /**
     * �õ������
     * @return String
     */
    public String getMedApplyNo(){
    return medApplyNo.getValue();
    }
    /**
     * �õ�ҽ��ϸ����Ⱥ��
     * @return String
     */
    public String getCat1Type() {
        return cat1Type.getValue();
    }
    /**
     * ����ҽ��ϸ����Ⱥ��
     * @param value String
     */
    public void setCat1Type(String value) {
        this.cat1Type.setValue(value);
    }

    /**
     * ���ô�����
     * @param value String
     */
    public void setRxNo(String value) {
        this.rxNo.setValue(value);
    }
    /**
     * ���������
     * @param value String
     */
    public void setMedApplyNo(String value){
        this.medApplyNo.setValue(value);
}
    /**
     * �õ�˳���
     * @return int
     */
    public int getSeqNo() {
        return seqNo.getValue();
    }

    /**
     * ����˳���
     * @param value int
     */
    public void setSeqNo(int value) {
        this.seqNo.setValue(value);
    }

    /**
     * �õ�������Ա
     * @return String
     */
    public String getOptUser() {
        return optUser.getValue();
    }

    /**
     * ���ò�����Ա
     * @param value String
     */
    public void setOptUser(String value) {
        this.optUser.setValue(value);
    }

    /**
     * �õ���������
     * @return String Timestamp
     */
    public Timestamp getOptDate() {
        return optDate.getValue();
    }
    /**
     * ���ò�������
     */
    public void setOptDate() {
        this.optDate.setValue(StringTool.getTimestamp(SystemTool.getInstance().getDate()));
    }

    /**
     * �õ�������ĩ
     * @return String
     */
    public String getOptTerm() {
        return optTerm.getValue();
    }

    /**
     * ���ò�����ĩ
     * @param value String
     */
    public void setOptTerm(String value) {
        this.optTerm.setValue(value);
    }

    /**
     * �õ�����
     * @return int
     */
    public int getPresrtNo() {
        return presrtNo.getValue();
    }

    /**
     * ���÷���
     * @param value int
     */
    public void setPresrtNo(int value) {
        this.presrtNo.setValue(value);
    }

    /**
     * �õ�����
     * @return String
     */
    public String getRegionCode() {
        return regionCode.getValue();
    }

    /**
     * ��������
     * @param value String
     */
    public void setRegionCode(String value) {
        this.regionCode.setValue(value);
    }


    /**
     * �õ�����״̬
     * @return String
     */
    public String getAdmType() {
        return admType.getValue();
    }

    /**
     * ���þ���״̬
     * @param value String
     */
    public void setAdmType(String value) {
        this.admType.setValue(value);
    }

    /**
     * �õ�ҽ������
     * @return String
     */
    public String getRxType() {
        return rxType.getValue();
    }

    /**
     * ����ҽ������
     * @param value String
     */
    public void setRxType(String value) {
        this.rxType.setValue(value);
    }

    /**
     * �õ��ݴ�ע��
     * @return String
     */
    public String getTemporaryFlg() {
        return temporaryFlg.getValue();
    }

    /**
     * �����ݴ�ע��
     * @param value String
     */
    public void setTemporaryFlg(String value) {
        this.temporaryFlg.setValue(value);
    }

    /**
     * �õ�����ע��
     * @return String
     */
    public String getReleaseFlg() {
        return releaseFlg.getValue();
    }

    /**
     * ���ý���ע��
     * @param value String
     */
    public void setReleaseFlg(String value) {
        this.releaseFlg.setValue(value);
    }

    /**
     * �õ�������ע��
     * @return String
     */

    public String getLinkmainFlg() {
        return linkmainFlg.getValue();
    }

    /**
     * ����������ע��
     * @param value String
     */
    public void setLinkmainFlg(String value) {
        this.linkmainFlg.setValue(value);
    }

    /**
     * �õ����Ӻ�
     * @return String
     */
    public String getLinkNo() {
        return linkNo.getValue();
    }

    /**
     * �������Ӻ�
     * @param value String
     */
    public void setLinkNo(String value) {
        this.linkNo.setValue(value);
    }

    /**
     * �õ�ҽ�����
     * @return String
     */
    public String getOrderCode() {
        return orderCode.getValue();
    }

    /**
     * ����ҽ�����
     * @param value String
     */
    public void setOrderCode(String value) {
        this.orderCode.setValue(value);
    }
    /**
     * �õ�������λ
     * @return  dosageUnit String
     */
    public String getDosageUnit(){
    	return this.dosageUnit.getValue();
    }
    /**
     * ���ü�����λ
     * @param dosageUnit String
     */
    public void setDosageUnit(String dosageUnit){
    	this.dosageUnit.setValue(dosageUnit);
    }
    /**
     * �޸ļ�����λ
     * @param dosageUnit String
     */
    public void modifyDosageUnit(String dosageUnit){
    	this.dosageUnit.modifyValue(dosageUnit);
    }
    /**
     * �õ�ҽ������
     * @return String
     */
    public String getOrderDesc() {
        return orderDesc.getValue();
    }

    /**
     * ����ҽ������
     * @param value String
     */
    public void setOrderDesc(String value) {
        this.orderDesc.setValue(value);
    }

    /**
     * �õ���Ʒ��
     * @return String
     */
    public String getGoodsDesc() {
        return goodsDesc.getValue();
    }

    /**
     * ������Ʒ��
     * @param value String
     */
    public void setGoodsDesc(String value) {
        this.goodsDesc.setValue(value);
    }

    /**
     * �õ�ҽ��ϸ����
     * @return String
     */
    public String getOrderCat1Code() {
        return orderCat1Code.getValue();
    }

    /**
     * ����ҽ��ϸ����
     * @param value String
     */
    public void setOrderCat1Code(String value) {
        this.orderCat1Code.setValue(value);
    }

    /**
     * �õ�����
     * @return double
     */
    public double getMediQty() {
        return mediQty.getValue();
    }

    /**
     * ��������
     * @param value double
     */
    public void setMediQty(double value) {
        this.mediQty.setValue(value);
    }

    /**
     * �õ���ҩ��λ
     * @return String
     */
    public String getMediUnit() {
        return mediUnit.getValue();
    }

    /**
     * ���ÿ�ҩ��λ
     * @param value String
     */
    public void setMediUnit(String value) {
        this.mediUnit.setValue(value);
    }

    /**
     * �õ�Ƶ�δ���
     * @return String
     */
    public String getFreqCode() {
        return freqCode.getValue();
    }

    /**
     * ����Ƶ�δ���
     * @param value String
     */
    public void setFreqCode(String value) {
        this.freqCode.setValue(value);
    }

    /**
     * �õ���ҩ;������
     * @return String
     */
    public String getRouteCode() {
        return routeCode.getValue();
    }

    /**
     * ���ø�ҩ;������
     * @param value String
     */
    public void setRouteCode(String value) {
        this.routeCode.setValue(value);
    }

    /**
     * �õ�����/�շ�'
     * @return int
     */
    public int getTakeDays() {
        return takeDays.getValue();
    }

    /**
     * ���ø���/�շ�'
     * @param value int
     */
    public void setTakeDays(int value) {
        this.takeDays.setValue(value);
    }

    /**
     * �õ�����
     * @return Double
     */
    public double getDosageQty() {
        return dosageQty.getValue();
    }

    /**
     * ��������
     * @param value double
     */
    public void setDosageQty(double value) {
        this.dosageQty.setValue(value);
    }

    /**
     * �õ�ʵ�ʷ�ҩ��
     * @return double
     */
    public double getDispenseQty() {
        return dispenseQty.getValue();
    }
    /**
     * ����ʵ�ʷ�ҩ��
     * @param dispenseQty double
     */
    public void setDispenseQty(double dispenseQty) {
        this.dispenseQty.setValue(dispenseQty);
    }

    /**
     * �õ���ҩ��λ
     * @return String
     */
    public String getDispenseUnit() {
        return dispenseUnit.getValue();
    }

    /**
     * ���÷�ҩ��λ
     * @param value String
     */
    public void setDispenseUnit(String value) {
        this.dispenseUnit.setValue(value);
    }

    /**
     * �õ���浥λ��ҩע��
     * @return String
     */
    public String getGiveboxFlg() {
        return giveboxFlg.getValue();
    }

    /**
     * ���ÿ�浥λ��ҩע��
     * @param value String
     */
    public void setGiveboxFlg(String value) {
        this.giveboxFlg.setValue(value);
    }

    /**
     * �õ��ԷѼ�
     * @return double
     */
    public double getOwnPrice() {
        return ownPrice.getValue();
    }

    /**
     * �����ԷѼ�
     * @param value double
     */
    public void setOwnPrice(double value) {
        this.ownPrice.setValue(value);
    }

    /**
     * �õ�ҽ����
     * @return double
     */
    public double getNhiPrice() {
        return nhiPrice.getValue();
    }

    /**
     * ����ҽ����
     * @param value double
     */
    public void setNhiPrice(double value) {
        this.nhiPrice.setValue(value);
    }

    /**
     * �õ�����ۿ�%
     * @return double
     */
    public double getDiscountRate() {
        return discountRate.getValue();
    }

    /**
     * ��������ۿ�%
     * @param value double
     */
    public void setDiscountRate(double value) {
        this.discountRate.setValue(value);
    }

    /**
     * �õ��Էѽ��
     * @return double
     */
    public double getOwnAmt() {
        return ownAmt.getValue();
    }

    /**
     * �����Էѽ��
     * @param value double
     */
    public void setOwnAmt(double value) {
        this.ownAmt.setValue(value);
    }

    /**
     * �õ�Ӧ�����
     * @return double
     */
    public double getArAmt() {
        return arAmt.getValue();
    }

    /**
     * ����Ӧ�����
     * @param value double
     */
    public void setArAmt(double value) {
        this.arAmt.setValue(value);
    }

    /**
     * �õ���ע��
     * @return String
     */
    public String getDrNote() {
        return drNote.getValue();
    }

    /**
     * ���ñ�ע��
     * @param value String
     */
    public void setDrNote(String value) {
        this.drNote.setValue(value);
    }

    /**
     * �õ���ʿ��ע
     * @return String
     */
    public String getNsNote() {
        return nsNote.getValue();
    }

    /**
     * ���û�ʿ��ע
     * @param value String
     */
    public void setNsNote(String value) {
        this.nsNote.setValue(value);
    }

    /**
     * �õ�����ҽʦ
     * @return String
     */
    public String getDrCode() {
        return drCode.getValue();
    }

    /**
     * ���ÿ���ҽʦ
     * @param value String
     */
    public void setDrCode(String value) {
        this.drCode.setValue(value);
    }

    /**
     * �õ� ��������ʱ��
     * @return Timestamp
     */
    public Timestamp getOrderDate() {
        return orderDate.getValue();
    }

    /**
     * ���ÿ�������ʱ��
     * @param value Timestamp
     */
    public void setOrderDate(Timestamp value) {
        this.orderDate.setValue(value);
    }

    /**
     * �õ� ��������
     * @return String
     */
    public String getDeptCode() {
        return deptCode.getValue();
    }

    /**
     * ���ÿ�������
     * @param value String
     */
    public void setDeptCode(String value) {
        this.deptCode.setValue(value);
    }

    /**
     * �õ�ͣ��ҽʦ
     * @return String
     */
    public String getDcDrCode() {
        return dcDrCode.getValue();
    }

    /**
     * ����ͣ��ҽʦ
     * @param value String
     */
    public void setDcDrCode(String value) {
        this.dcDrCode.setValue(value);
    }

    /**
     * �õ�ͣ��ʱ��
     * @return Timestamp
     */
    public Timestamp getDcOrderDate() {
        return dcOrderDate.getValue();
    }

    /**
     * ����ͣ��ʱ��
     * @param value Timestamp
     */
    public void setDcOrderDate(Timestamp value) {
        this.dcOrderDate.setValue(value);
    }

    /**
     * �õ�ͣ�ÿ���
     * @return String
     */
    public String getDcDeptCode() {
        return dcDeptCode.getValue();
    }

    /**
     * ����ͣ�ÿ���
     * @param value String
     */
    public void setDcDeptCode(String value) {
        this.dcDeptCode.setValue(value);
    }

    /**
     * �õ�ִ�п���
     * @return String
     */
    public String getExecDeptCode() {
        return execDeptCode.getValue();
    }

    /**
     * \����ִ�п���
     * @param value String
     */
    public void setExecDeptCode(String value) {
        this.execDeptCode.setValue(value);
    }

    /**
     * �õ�����ҽ��ע��
     * @return String
     */
    public String getSetmainFlg() {
        return setmainFlg.getValue();
    }

    /**
     * ���ü���ҽ��ע��
     * @param value String
     */
    public void setSetmainFlg(String value) {
        this.setmainFlg.setValue(value);
    }

    /**
     * �õ�����ҽ����ţ���������ͬʱ����2����ͬ�ļ���ҽ����
     * @return int
     */
    public int getOrderSetGroupNo() {
        return orderSetGroupNo.getValue();
    }

    /**
     * ���ü���ҽ����ţ���������ͬʱ����2����ͬ�ļ���ҽ����
     * @param value int
     */
    public void setOrderSetGroupNo(int value) {
        this.orderSetGroupNo.setValue(value);
    }

    /**
     * �õ�����ҽ�����
     * @return String
     */
    public String getOrdersetCode() {
        return ordersetCode.getValue();
    }

    /**
     * ���ü���ҽ�����
     * @param value String
     */
    public void setOrdersetCode(String value) {
        this.ordersetCode.setValue(value);
    }

    /**
     * �õ�����ע��
     * @return String
     */
    public String getHideFlg() {
        return hideFlg.getValue();
    }

    /**
     * ��������ע��
     * @param value String
     */
    public void setHideFlg(String value) {
        this.hideFlg.setValue(value);
    }

    /**
     * �õ��������
     * @return String
     */
    public String getRpttypeCode() {
        return rpttypeCode.getValue();
    }

    /**
     * ���ñ������
     * @param value String
     */
    public void setRpttypeCode(String value) {
        this.rpttypeCode.setValue(value);
    }

    /**
     * �õ�����
     * @return String
     */
    public String getOptitemCode() {
        return optitemCode.getValue();
    }

    /**
     * ���ü���
     * @param value String
     */
    public void setOptitemCode(String value) {
        this.optitemCode.setValue(value);
    }

    /**
     * �õ���������
     * @return String
     */
    public String getDevCode() {
        return devCode.getValue();
    }

    /**
     * ������������
     * @param value String
     */
    public void setDevCode(String value) {
        this.devCode.setValue(value);
    }

    /**
     * �õ����뵥ģ��
     * @return String
     */
    public String getMrCode() {
        return mrCode.getValue();
    }

    /**
     * �������뵥ģ��
     * @param value String
     */
    public void setMrCode(String value) {
        this.mrCode.setValue(value);
    }

    /**
     * �õ��ṹ�����뵥���
     * @return int
     */
    public int getFileNo() {
        return fileNo.getValue();
    }

    /**
     * ���ýṹ�����뵥���
     * @param value int
     */
    public void setFileNo(int value) {
        this.fileNo.setValue(value);
    }

    /**
     * �õ�ҽ�켨Ч����
     * @return String
     */
    public String getDegreeCode() {
        return degreeCode.getValue();
    }

    /**
     * ����ҽ�켨Ч����
     * @param value String
     */
    public void setDegreeCode(String value) {
        this.degreeCode.setValue(value);
    }

    /**
     * �õ�����ע��
     * @return String
     */
    public String getUrgentFlg() {
        return urgentFlg.getValue();
    }

    /**
     * ���ü���ע��
     * @param value String
     */
    public void setUrgentFlg(String value) {
        this.urgentFlg.setValue(value);
    }

    /**
     * �õ��������
     * @return String
     */
    public String getInspayType() {
        return inspayType.getValue();
    }

    /**
     * ���ø������
     * @param value String
     */
    public void setInspayType(String value) {
        this.inspayType.setValue(value);
    }

    /**
     * �õ�ҩƷ����
     * @return String
     */
    public String getPhaType() {
        return phaType.getValue();
    }

    /**
     * ����ҩƷ����
     * @param value String
     */
    public void setPhaType(String value) {
        this.phaType.setValue(value);
    }

    /**
     * �õ����ʹ����
     * @return String
     */
    public String getDoseType() {
        return doseType.getValue();
    }

    /**
     * ���ü��ʹ����
     * @param value String
     */
    public void setDoseType(String value) {
        this.doseType.setValue(value);
    }


    /**
     * �õ���ͯ����ǩע��
     * @return String
     */
    public String getPrinttypeflgInfant() {
        return printtypeflgInfant.getValue();
    }

    /**
     * ���ö�ͯ����ǩע��
     * @param value String
     */
    public void setPrinttypeflgInfant(String value) {
        this.printtypeflgInfant.setValue(value);
    }

    /**
     * �õ�����ע��
     * @return String
     */
    public String getExpensiveFlg() {
        return expensiveFlg.getValue();
    }

    /**
     * ���ù���ע��
     * @param value String
     */
    public void setExpensiveFlg(String value) {
        this.expensiveFlg.setValue(value);
    }

    /**
     * �õ�����ҩƷ����
     * @return String
     */
    public String getCtrldrugclassCode() {
        return ctrldrugclassCode.getValue();
    }

    /**
     * ���ù���ҩƷ����
     * @param value String
     */
    public void setCtrldrugclassCode(String value) {
        this.ctrldrugclassCode.setValue(value);
    }

    /**
     * �õ���ҩ��
     * @return int
     */
    public int getPrescriptNo() {
        return prescriptNo.getValue();
    }

    /**
     * ������ҩ��
     * @param value int
     */
    public void setPrescriptNo(int value) {
        this.prescriptNo.setValue(value);
    }

    /**
     * �õ��Ͱ�ҩ��ע��
     * @return String
     */
    public String getAtcFlg() {
        return atcFlg.getValue();
    }

    /**
     * �����Ͱ�ҩ��ע��
     * @param value String
     */
    public void setAtcFlg(String value) {
        this.atcFlg.setValue(value);
    }

    /**
     * �õ��Ͱ�ҩ��ʱ��
     * @return Timestamp
     */
    public Timestamp getSendatcdate() {
        return sendatcdate.getValue();
    }

    /**
     * �����Ͱ�ҩ��ʱ��
     * @param value Timestamp
     */
    public void setSendatcdate(Timestamp value) {
        this.sendatcdate.setValue(value);
    }


    /**
     * �õ��ż����վݺ�
     * @return String
     */
    public String getReceiptNo() {
        return receiptNo.getValue();
    }

    /**
     * �����ż����վݺ�
     * @param value String
     */
    public void setReceiptNo(String value) {
        this.receiptNo.setValue(value);
    }

    /**
     * �õ�����ע��
     * @return String
     */
    public String getBillFlg() {
        return billFlg.getValue();
    }

    /**
     * ��������ע��
     * @param value String
     */
    public void setBillFlg(String value) {
        this.billFlg.setValue(value);
    }

    /**
     * �õ��շ�����ʱ��
     * @return Timestamp
     */
    public Timestamp getBillDate() {
        return billDate.getValue();
    }

    /**
     * �����շ�����ʱ��
     * @param value Timestamp
     */
    public void setBillDate(Timestamp value) {
        this.billDate.setValue(value);
    }

    /**
     * �õ��շ���Ա
     * @return String
     */
    public String getBillUser() {
        return billUser.getValue();
    }

    /**
     * �����շ���Ա
     * @param value String
     */
    public void setBillUser(String value) {
        this.billUser.setValue(value);
    }

    /**
     * �õ�Ʊ�ݴ�ӡע��
     * @return String
     */
    public String getPrintFlg() {
        return printFlg.getValue();
    }

    /**
     * ����Ʊ�ݴ�ӡע��
     * @param value String
     */
    public void setPrintFlg(String value) {
        this.printFlg.setValue(value);
    }

    /**
     * �õ��վݷ��ô���
     * @return String
     */
    public String getRexpCode() {
        return rexpCode.getValue();
    }

    /**
     * �����վݷ��ô���
     * @param value String
     */
    public void setRexpCode(String value) {
        this.rexpCode.setValue(value);
    }

    /**
     * �õ�Ժ�ڷ��ô���
     * @return String
     */
    public String getHexpCode() {
        return hexpCode.getValue();
    }

    /**
     * ����Ժ�ڷ��ô���
     * @param value String
     */
    public void setHexpCode(String value) {
        this.hexpCode.setValue(value);
    }

    /**
     * �õ�֧����ͬ��λ����
     * @return String
     */
    public String getContractCode() {
        return contractCode.getValue();
    }

    /**
     * ����֧����ͬ��λ����
     * @param value String
     */
    public void setContractCode(String value) {
        this.contractCode.setValue(value);
    }

    /**
     * \�õ����
     * @return String
     */
    public String getCtz1Code() {
        return ctz1Code.getValue();
    }

    /**
     * �������
     * @param value String
     */
    public void setCtz1Code(String value) {
        this.ctz1Code.setValue(value);
    }

    /**
     * �õ��ۿ�1
     * @return String
     */
    public String getCtz2Code() {
        return ctz2Code.getValue();
    }

    /**
     * �����ۿ�1
     * @param value String
     */
    public void setCtz2Code(String value) {
        this.ctz2Code.setValue(value);
    }

    /**
     * �õ��ۿ�2
     * @return String
     */
    public String getCtz3Code() {
        return ctz3Code.getValue();
    }

    /**
     * �����ۿ�2
     * @param value String
     */
    public void setCtz3Code(String value) {
        this.ctz3Code.setValue(value);
    }

    /**
     * �õ����ҩʦ
     * @return String
     */
    public String getPhaCheckCode() {
        return phaCheckCode.getValue();
    }

    /**
     * �������ҩʦ
     * @param value String
     */
    public void setPhaCheckCode(String value) {
        this.phaCheckCode.setValue(value);
    }

    /**
     * �õ����ʱ��
     * @return Timestamp
     */
    public Timestamp getPhaCheckDate() {
        return phaCheckDate.getValue();
    }

    /**
     * �������ʱ��
     * @param value Timestamp
     */
    public void setPhaCheckDate(Timestamp value) {
        this.phaCheckDate.setValue(value);
    }

    /**
     * �õ���ҩҩʦ
     * @return String
     */
    public String getPhaDosageCode() {
        return phaDosageCode.getValue();
    }

    /**
     * ������ҩҩʦ
     * @param value String
     */
    public void setPhaDosageCode(String value) {
        this.phaDosageCode.setValue(value);
    }

    /**
     * �õ���ҩʱ��
     * @return Timestamp
     */
    public Timestamp getPhaDosageDate() {
        return phaDosageDate.getValue();
    }

    /**
     * ������ҩʱ��
     * @param value Timestamp
     */
    public void setPhaDosageDate(Timestamp value) {
        this.phaDosageDate.setValue(value);
    }

    /**
     * �õ���ҩҩʦ
     * @return String
     */
    public String getPhaDispenseCode() {
        return phaDispenseCode.getValue();
    }

    /**
     * ���÷�ҩҩʦ
     * @param value String
     */
    public void setPhaDispenseCode(String value) {
        this.phaDispenseCode.setValue(value);
    }

    /**
     * �õ���ҩȷ��ʱ��
     * @return Timestamp
     */
    public Timestamp getPhaDispenseDate() {
        return phaDispenseDate.getValue();
    }
    /**
     * ���ù��
     * @param specification String
     */
    public void setSpecification(String specification){
    	this.specification.setValue(specification);
    }
    /**
     * �õ����
     * @return specification String
     */
    public String getSpecification(){
    	return this.specification.getValue();
    }
    /**
     * �޸Ĺ��
     * @param specification String
     */
    public void modifySpecification(String specification){
    	this.specification.modifyValue(specification);
    }


    /**
     * �õ�ҽ��ȷ�ϻ�ʿ
     * @return String
     */
    public String getNsExecCode() {
        return nsExecCode.getValue();
    }

    /**
     * ����ҽ��ȷ�ϻ�ʿ
     * @param value String
     */
    public void setNsExecCode(String value) {
        this.nsExecCode.setValue(value);
    }

    /**
     * �õ�ҽ��ȷ��ʱ��
     * @return Timestamp
     */
    public Timestamp getNsExecDate() {
        return nsExecDate.getValue();
    }

    /**
     * ����ҽ��ȷ��ʱ��
     * @param value Timestamp
     */
    public void setNsExecDate(Timestamp value) {
        this.nsExecDate.setValue(value);
    }

    /**
     * �õ�ҽ��ȷ�Ͽ���
     * @return String
     */
    public String getNsExecDept() {
        return nsExecDept.getValue();
    }

    /**
     * ����ҽ��ȷ�Ͽ���
     * @param value String
     */
    public void setNsExecDept(String value) {
        this.nsExecDept.setValue(value);
    }

    /**
     * �õ���ҩ��ʽ
     * @return String
     */
    public String getDctagentCode() {
        return dctagentCode.getValue();
    }

    /**
     * ���ü�ҩ��ʽ
     * @param value String
     */
    public void setDctagentCode(String value) {
        this.dctagentCode.setValue(value);
    }

    /**
     * �õ�����巨
     * @return String
     */
    public String getDctexcepCode() {
        return dctexcepCode.getValue();
    }

    /**
     * ��������巨
     * @param value String
     */
    public void setDctexcepCode(String value) {
        this.dctexcepCode.setValue(value);
    }

    /**
     * �õ���Ƭ������
     * @return int
     */
    public int getDctTakeQty() {
        return dctTakeQty.getValue();
    }

    /**
     * ������Ƭ������
     * @param value int
     */
    public void setDctTakeQty(int value) {
        this.dctTakeQty.setValue(value);
    }

    /**
     * �õ������ܰ���
     * @return int
     */
    public int getPackageTot() {
        return packageTot.getValue();
    }

    /**
     * ���ô����ܰ���
     * @param value int
     */
    public void setPackageTot(int value) {
        this.packageTot.setValue(value);
    }

    /**
     * �õ�������ҩҩ��
     * @return String
     */
    public String getAgencyOrgCode() {
        return agencyOrgCode.getValue();
    }

    /**
     * ���ô�����ҩҩ��
     * @param value String
     */
    public void setAgencyOrgCode(String value) {
        this.agencyOrgCode.setValue(value);
    }

    /**
     * �õ�����ע��
     * @return String
     */
    public String getDctagentFlg() {
        return dctagentFlg.getValue();
    }

    /**
     * ���ô���ע��
     * @param value String
     */
    public void setDctagentFlg(String value) {
        this.dctagentFlg.setValue(value);
    }

    /**
     * �õ���ҩ��
     * @return String
     */
    public String getDecoctCode() {
        return decoctCode.getValue();
    }

    /**
     * ���ü�ҩ��
     * @param value String
     */
    public void setDecoctCode(String value) {
        this.decoctCode.setValue(value);
    }

    /**
     * �޸Ŀ������
     * @param value String
     */
    public void modifyCaseNo(String value) {
        this.caseNo.modifyValue(value);
    }

    /**
     * �޸Ĵ���ǩ��
     * @param value String
     */
    public void modifyRxNo(String value) {
        this.rxNo.modifyValue(value);
    }
    /**
     * �޸������
     * @param value String
     */
    public void modifyMedApplyNo(String value){
    this.medApplyNo.modifyValue(value);
    }
    /**
     * �޸�ҽ��ϸ����Ⱥ��
     * @param value String
     */
    public void modifyCat1Type(String value) {
        this.cat1Type.modifyValue(value);
    }

    /**
     * �޸�ѭ���
     * @param value int
     */
    public void modifySeqNo(int value) {
        this.seqNo.modifyValue(value);
    }
    /**
     * �޸Ĳ����ˆT
     */
    public void modifyOptUser() {
        this.optUser.modifyValue(Operator.getID());
    }

    /**
     * �޸Ĳ�������
     * @param value String
     */
    public void modifyOptDate(String value) {
        this.optDate.modifyValue(StringTool.getTimestamp(SystemTool.getInstance().getDate()));
    }
    /**
     * �޸Ĳ�����ĩ
     */
    public void modifyOptTerm() {
        this.optTerm.modifyValue(Operator.getIP());
    }

    /**
     * �޸ķ���
     * @param value int
     */
    public void modifyPresrtNo(int value) {
        this.presrtNo.modifyValue(value);
    }

    /**
     * �޸ľ�������
     * @param value String
     */
    public void modifyRegionCode(String value) {
        this.regionCode.modifyValue(value);
    }

    /**
     * �޸ľ���״̬
     * @param value String
     */
    public void modifyAdmType(String value) {
        this.admType.modifyValue(value);
    }

    /**
     * �޸�ҽ������
     * @param value String
     */
    public void modifyRxType(String value) {
        this.rxType.modifyValue(value);
    }

    /**
     * �޸��ݴ�ע��
     * @param value String
     */
    public void modifyTemporaryFlg(String value) {
        this.temporaryFlg.modifyValue(value);
    }

    /**
     * �޸Ľ���ע��
     * @param value String
     */
    public void modifyReleaseFlg(String value) {
        this.releaseFlg.modifyValue(value);
    }

    /**
     * �޸�������ע��
     * @param value String
     */
    public void modifyLinkmainFlg(String value) {
        this.linkmainFlg.modifyValue(value);
    }

    /**
     * �޸����Ӻ�
     * @param value String
     */
    public void modifyLinkNo(String value) {
        this.linkNo.modifyValue(value);
    }

    /**
     * �޸�ҽ�����
     * @param value String
     */
    public void modifyOrderCode(String value) {
        this.orderCode.modifyValue(value);
    }

    /**
     * �޸�ҽ������
     * @param value String
     */
    public void modifyOrderDesc(String value) {
        this.orderDesc.modifyValue(value);
    }

    /**
     * �޸���Ʒ��
     * @param value String
     */
    public void modifyGoodsDesc(String value) {
        this.goodsDesc.modifyValue(value);
    }

    /**
     * �޸�ҽ��ϸ����
     * @param value String
     */
    public void modifyOrderCat1Code(String value) {
        this.orderCat1Code.modifyValue(value);
    }

    /**
     * �޸�����
     * @param value double
     */
    public void modifyMediQty(double value) {
        this.mediQty.modifyValue(value);
    }

    /**
     * �޸Ŀ�ҩ��λ
     * @param value String
     */
    public void modifyMediUnit(String value) {
        this.mediUnit.modifyValue(value);
    }

    /**
     * �޸�Ƶ�δ���
     * @param value String
     */
    public void modifyFreqCode(String value) {
        this.freqCode.modifyValue(value);
    }

    /**
     * �޸ĸ�ҩ;������
     * @param value String
     */
    public void modifyRouteCode(String value) {
        this.routeCode.modifyValue(value);
    }

    /**
     * �޸ĸ���/�շ�
     * @param value int
     */
    public void modifyTakeDays(int value) {
        this.takeDays.modifyValue(value);
    }

    /**
     * �޸�����
     * @param value double
     */
    public void modifyDosageQty(double value) {
        this.dosageQty.modifyValue(value);
    }

    /**
     * �޸�ʵ�ʷ�ҩ��
     * @param value double
     */
    public void modifyDispenseQty(double value) {
        this.dispenseQty.modifyValue(value);
    }

    /**
     * �޸ķ�ҩ��λ
     * @param value String
     */
    public void modifyDispenseUnit(String value) {
        this.dispenseUnit.modifyValue(value);
    }

    /**
     * �޸Ŀ�浥λ��ҩע��
     * @param value String
     */
    public void modifyGiveboxFlg(String value) {
        this.giveboxFlg.modifyValue(value);
    }

    /**
     * �޸��ԷѼ�
     * @param value double
     */
    public void modifyOwnPrice(double value) {
        this.ownPrice.modifyValue(value);
    }

    /**
     * �޸�ҽ����
     * @param value double
     */
    public void modifyNhiPrice(double value) {
        this.nhiPrice.modifyValue(value);
    }

    /**
     * �޸�����ۿ�%
     * @param value double
     */
    public void modifyDiscountRate(double value) {
        this.discountRate.modifyValue(value);
    }

    /**
     * �޸��Էѽ��
     * @param value double
     */
    public void modifyOwnAmt(double value) {
        if (value > 0)
            value = ( (int) (value * 100.0 + 0.5)) / 100.0;
        else if (value < 0)
            value = ( (int) (value * 100.0 - 0.5)) / 100.0;
        this.ownAmt.modifyValue(value);
    }

    /**
     * �޸�Ӧ�����
     * @param value double
     */
    public void modifyArAmt(double value) {
        if (value > 0)
            value = ( (int) (value * 100.0 + 0.5)) / 100.0;
        else if (value < 0)
            value = ( (int) (value * 100.0 - 0.5)) / 100.0;
        this.arAmt.modifyValue(value);
    }

    /**
     * �޸ı�ע��
     * @param value String
     */
    public void modifyDrNote(String value) {
        this.drNote.modifyValue(value);
    }

    /**
     * �޸Ļ�ʿ��ע
     * @param value String
     */
    public void modifyNsNote(String value) {
        this.nsNote.modifyValue(value);
    }

    /**
     * �޸Ŀ���ҽʦ
     * @param value String
     */
    public void modifyDrCode(String value) {
        this.drCode.modifyValue(value);
    }

    /**
     * �޸Ŀ�������ʱ��
     * @param value Timestamp
     */
    public void modifyOrderDate(Timestamp value) {
        this.orderDate.modifyValue(value);
    }

    /**
     * �޸Ŀ�������
     * @param value String
     */
    public void modifyDeptCode(String value) {
        this.deptCode.modifyValue(value);
    }

    /**
     * �޸�ͣ��ҽʦ
     * @param value String
     */
    public void modifyDcDrCode(String value) {
        this.dcDrCode.modifyValue(value);
    }

    /**
     * �޸�ͣ��ʱ��
     * @param value Timestamp
     */
    public void modifyDcOrderDate(Timestamp value) {
        this.dcOrderDate.modifyValue(value);
    }

    /**
     * �޸�ͣ�ÿ���
     * @param value String
     */
    public void modifyDcDeptCode(String value) {
        this.dcDeptCode.modifyValue(value);
    }

    /**
     * �޸�ִ�п���
     * @param value String
     */
    public void modifyExecDeptCode(String value) {
        this.execDeptCode.modifyValue(value);
    }

    /**
     * �޸ļ���ҽ��ע��
     * @param value String
     */
    public void modifySetmainFlg(String value) {
        this.setmainFlg.modifyValue(value);
    }

    /**
     * �޸ļ���ҽ����ţ���������ͬʱ����2����ͬ�ļ���ҽ����
     * @param value int
     */
    public void modifyOrderSetGroupNo(int value) {
        this.orderSetGroupNo.modifyValue(value);
    }

    /**
     * �޸ļ���ҽ�����
     * @param value String
     */
    public void modifyOrdersetCode(String value) {
        this.ordersetCode.modifyValue(value);
    }

    /**
     * �޸�����ע��
     * @param value String
     */
    public void modifyHideFlg(String value) {
        this.hideFlg.modifyValue(value);
    }

    /**
     * �޸ı������
     * @param value String
     */
    public void modifyRpttypeCode(String value) {
        this.rpttypeCode.modifyValue(value);
    }

    /**
     * �޸ļ���
     * @param value String
     */
    public void modifyOptitemCode(String value) {
        this.optitemCode.modifyValue(value);
    }

    /**
     * �޸���������
     * @param value String
     */
    public void modifyDevCode(String value) {
        this.devCode.modifyValue(value);
    }

    /**
     * �޸����뵥ģ��
     * @param value String
     */
    public void modifyMrCode(String value) {
        this.mrCode.modifyValue(value);
    }

    /**
     * �޸Ľṹ�����뵥���
     * @param value int
     */
    public void modifyFileNo(int value) {
        this.fileNo.modifyValue(value);
    }

    /**
     * �޸�ҽ�켨Ч����
     * @param value String
     */
    public void modifyDegreeCode(String value) {
        this.degreeCode.modifyValue(value);
    }

    /**
     * �޸ļ���ע��
     * @param value String
     */
    public void modifyUrgentFlg(String value) {
        this.urgentFlg.modifyValue(value);
    }

    /**
     * �޸ĸ������
     * @param value String
     */
    public void modifyInspayType(String value) {
        this.inspayType.modifyValue(value);
    }

    /**
     * �޸�ҩƷ����
     * @param value String
     */
    public void modifyPhaType(String value) {
        this.phaType.modifyValue(value);
    }

    /**
     * �޸ļ��ʹ����
     * @param value String
     */
    public void modifyDoseType(String value) {
        this.doseType.modifyValue(value);
    }

    /**
     * �޸Ķ�ͯ����ǩע��
     * @param value String
     */
    public void modifyPrinttypeflgInfant(String value) {
        this.printtypeflgInfant.modifyValue(value);
    }

    /**
     * �޸Ĺ���ע��
     * @param value String
     */
    public void modifyExpensiveFlg(String value) {
        this.expensiveFlg.modifyValue(value);
    }

    /**
     * �޸Ĺ���ҩƷ����
     * @param value String
     */
    public void modifyCtrldrugclassCode(String value) {
        this.ctrldrugclassCode.modifyValue(value);
    }

    /**
     * �޸���ҩ��
     * @param value int
     */
    public void modifyPrescriptNo(int value) {
        this.prescriptNo.modifyValue(value);
    }

    /**
     * �޸��Ͱ�ҩ��ע��
     * @param value String
     */
    public void modifyAtcFlg(String value) {
        this.atcFlg.modifyValue(value);
    }

    /**
     * �޸��Ͱ�ҩ��ʱ��
     * @param value Timestamp
     */
    public void modifySendatcdate(Timestamp value) {
        this.sendatcdate.modifyValue(value);
    }


    /**
     * �޸��ż����վݺ�
     * @param value String
     */
    public void modifyReceiptNo(String value) {
        this.receiptNo.modifyValue(value);
    }

    /**
     * �޸�����ע��
     * @param value String
     */
    public void modifyBillFlg(String value) {
        this.billFlg.modifyValue(value);
    }

    /**
     * �޸��շ�����ʱ��
     * @param value Timestamp
     */

    public void modifyBillDate(Timestamp value) {
        this.billDate.modifyValue(value);
    }

    /**
     * �޸��շ���Ա
     * @param value String
     */

    public void modifyBillUser(String value) {
        this.billUser.modifyValue(value);
    }

    /**
     * �޸�Ʊ�ݴ�ӡע��
     * @param value String
     */

    public void modifyPrintFlg(String value) {
        this.printFlg.modifyValue(value);
    }

    /**
     * �޸��վݷ��ô���
     * @param value String
     */

    public void modifyRexpCode(String value) {
        this.rexpCode.modifyValue(value);
    }

    /**
     * �޸�Ժ�ڷ��ô���
     * @param value String
     */

    public void modifyHexpCode(String value) {
        this.hexpCode.modifyValue(value);
    }

    /**
     * �޸�֧����ͬ��λ����
     * @param value String
     */

    public void modifyContractCode(String value) {
        this.contractCode.modifyValue(value);
    }

    /**
     * �޸����
     * @param value String
     */

    public void modifyCtz1Code(String value) {
        this.ctz1Code.modifyValue(value);
    }

    /**
     * �޸��ۿ�1
     * @param value String
     */

    public void modifyCtz2Code(String value) {
        this.ctz2Code.modifyValue(value);
    }

    /**
     * �޸��ۿ�2
     * @param value String
     */

    public void modifyCtz3Code(String value) {
        this.ctz3Code.modifyValue(value);
    }

    /**
     * �޸����ҩʦ
     * @param value String
     */

    public void modifyPhaCheckCode(String value) {
        this.phaCheckCode.modifyValue(value);
    }

    /**
     * �޸����ʱ��
     * @param value Timestamp
     */

    public void modifyPhaCheckDate(Timestamp value) {
        this.phaCheckDate.modifyValue(value);
    }

    /**
     * �޸���ҩҩʦ
     * @param value String
     */

    public void modifyPhaDosageCode(String value) {
        this.phaDosageCode.modifyValue(value);
    }

    /**
     * �޸���ҩʱ��
     * @param value Timestamp
     */

    public void modifyPhaDosageDate(Timestamp value) {
        this.phaDosageDate.modifyValue(value);
    }

    /**
     * �޸ķ�ҩҩʦ
     * @param value String
     */

    public void modifyPhaDispenseCode(String value) {
        this.phaDispenseCode.modifyValue(value);
    }

    /**
     * �޸ķ�ҩȷ��ʱ��
     * @param value Timestamp
     */

    public void modifyPhaDispenseDate(Timestamp value) {
        this.phaDispenseDate.modifyValue(value);
    }

    /**
     * �޸�ҽ��ȷ�ϻ�ʿ
     * @param value String
     */

    public void modifyNsExecCode(String value) {
        this.nsExecCode.modifyValue(value);
    }

    /**
     * �޸�ҽ��ȷ��ʱ��
     * @param value Timestamp
     */

    public void modifyNsExecDate(Timestamp value) {
        this.nsExecDate.modifyValue(value);
    }

    /**
     * �޸�ҽ��ȷ�Ͽ���
     * @param value String
     */

    public void modifyNsExecDept(String value) {
        this.nsExecDept.modifyValue(value);
    }

    /**
     * �޸ļ�ҩ��ʽ
     * @param value String
     */

    public void modifyDctagentCode(String value) {
        this.dctagentCode.modifyValue(value);
    }

    /**
     * �޸�����巨
     * @param value String
     */

    public void modifyDctexcepCode(String value) {
        this.dctexcepCode.modifyValue(value);
    }

    /**
     * �޸���Ƭ������
     * @param value int
     */

    public void modifyDctTakeQty(int value) {
        this.dctTakeQty.modifyValue(value);
    }

    /**
     * �޸Ĵ����ܰ���
     * @param value int
     */

    public void modifyPackageTot(int value) {
        this.packageTot.modifyValue(value);
    }

    /**
     * �޸Ĵ�����ҩҩ��
     * @param value String
     */

    public void modifyAgencyOrgCode(String value) {
        this.agencyOrgCode.modifyValue(value);
    }

    /**
     * �޸Ĵ���ע��
     * @param value String
     */

    public void modifyDctagentFlg(String value) {
        this.dctagentFlg.modifyValue(value);
    }


    /**
     * �޸ļ�ҩ��
     * @param value String
     */

    public void modifyDecoctCode(String value) {
        this.decoctCode.modifyValue(value);
    }

    /**
     * ������ҩʱ��
     * @param dsreturnDate Timestamp
     */
    public void setPhaRetnDate(Timestamp dsreturnDate){
    	this.phaRetnDate.setValue(dsreturnDate);
    }
    /**
     * �õ���ҩʱ��
     * @return dsreturnDate Timestamp
     */
    public Timestamp getPhaRetnDate(){
    	return phaRetnDate.getValue();
    }
    /**
     * �޸���ҩʱ��
     * @param dsreturnDate Timestamp
     */
    public void modifyPhaRetnDate(Timestamp dsreturnDate){
    	this.phaRetnDate.modifyValue(dsreturnDate);
    }
    /**
     * ����ִ��ҽ��
     * @param execDr String
     */
    public void setExecDrCode(String execDr){
    	this.execDrCode.setValue(execDr);
    }
    /**
     * �õ�ִ��ҽ��
     * @return execDr String
     */
    public String getExecDrCode(){
    	return execDrCode.getValue();
    }
    /**
     * �޸�ִ��ҽ��
     * @param execDr String
     */
    public void modifyExecDrCode(String execDr){
    	this.execDrCode.modifyValue(execDr);
    }
    /**
     * ������ҩ��Ա
     * @param dsreturnuser String
     */
    public void setPhaRetnCode(String dsreturnuser){
    	this.phaRetnCode.setValue(dsreturnuser);
    }
    /**
     * �õ���ҩ��Ա
     * @return  dsreturnuser String
     */
    public String getPhaRetnCode(){
    	return this.phaRetnCode.getValue();
    }
    /**
     * �޸���ҩ��Ա
     * @param dsreturnuser String
     */
    public void modifyPhaRetnCode(String dsreturnuser){
    	this.phaRetnCode.modifyValue(dsreturnuser);
    }
    /**
     * ����ִ��ע��
     * @param exeFlg boolean
     */
    public void setExeFlg(boolean exeFlg){
    	this.exeFlg=exeFlg;
    }
    /**
     * ���óɱ�����
     * @param value String
     */
    public void setCostCenterCode(String value) {
        this.costCenterCode.setValue(value);
    }

    /**
     * �õ��շѱ��
     * @return  chargeFlg boolean
     */
    public boolean getChargeFlg(){
        return this.chargeFlg.getValue();
    }
    /**
     * �����շѱ��
     * @param chargeFlg boolean
     */
    public void setChargeFlg(boolean chargeFlg){
        this.chargeFlg.setValue(chargeFlg);
    }
    /**
     * �޸��շѱ��
     * @param chargeFlg boolean
     */
    public void modifyChargeFlg(boolean chargeFlg){
        this.chargeFlg.modifyValue(chargeFlg);
    }
    /**
     * �õ�ִ��ע��
     * @return exeflg boolean
     */
    public boolean getExeFlg(){
    	return this.exeFlg;
    }
    /**
     * �õ��ɱ�����
     * @return String
     */
    public String getCostCenterCode() {
        return costCenterCode.getValue();
    }

    /**
     * �޸ĳɱ�����
     * @param value String
     */
    public void modifyCostCenterCode(String value) {
        costCenterCode.modifyValue(value);
    }
    /**
     * �޸�ִ�б�־
     * @param value String
     */
    public void modifyExecFlg(String value) {
        execFlg.modifyValue(value);
    }
	public TParm getParm()
    {
        TParm result = super.getParm();
        if(getPat() != null){
        	result.setData("MR_NO",pat.getMrNo());
        }
        else{
        	result.setData("MR_NO","");
        }

        result.setData("OPT_USER",Operator.getID());
        result.setData("OPT_TERM",Operator.getIP());
        return result;
    }
	/**
     * �õ�ҽ������(��)
     * @return String
     */
    public String getNhiCodeO() {
        return nhiCodeO.getValue();
    }

    /**
     * �޸�ҽ������(��)
     * @param value String
     */
    public void modifyNhiCodeO(String value) {
    	nhiCodeO.modifyValue(value);
    }
    /**
     * �õ�ҽ������(��)
     * @return String
     */
    public String getNhiCodeE() {
        return nhiCodeE.getValue();
    }

    /**
     * �޸�ҽ������(��)
     * @param value String
     */
    public void modifyNhiCodeE(String value) {
    	nhiCodeE.modifyValue(value);
    }
    /**
     * �õ�ҽ������(ס)
     * @return String
     */
    public String getNhiCodeI() {
        return nhiCodeI.getValue();
    }

    /**
     * �޸�ҽ������(��)
     * @param value String
     */
    public void modifyNhiCodeI(String value) {
    	nhiCodeI.modifyValue(value);
    }
    
    
    
    public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getSkintestFlg() {
		return skintestFlg;
	}
	public void setSkintestFlg(String skintestFlg) {
		this.skintestFlg = skintestFlg;
	}
		
	public TimestampValue getExecDate() {
		return execDate;
	}
	public void setExecDate(TimestampValue execDate) {
		this.execDate = execDate;
	}
	
    /**
     * �޸�ִ��ʱ��
     * @param value Timestamp
     */

    public void modifyExecDate(Timestamp value) {
        this.execDate.modifyValue(value);
    }
    
    /**
     * �õ���������
     * @return String
     */
	public String getAge() {
		return age;
	}
	
    /**
     * �޸�ҽ��������
     * @param age String
     */
	public void setAge(String age) {
		this.age = age;
	}
	
    /**
     * �õ���������
     * @return String
     */
	public String getWeight() {
		return weight;
	}
	
    /**
     * �޸�ҽ��������
     * @param weight String
     */
	public void setWeight(String weight) {
		this.weight = weight;
	}
	
	/**
	 * 
	 * @return
	 */
	public Timestamp getBirthDate() {
		return birthDate;
	}
	
	/**
	 * 
	 * @param birthDate
	 */
	public void setBirthDate(Timestamp birthDate) {
		this.birthDate = birthDate;
	}
	
	/**
     * ������
     */
    public Order() {
        setMapString("caseNo:CASE_NO;rxNo:RX_NO;seqNo:SEQ_NO;medApplyNo:MED_APPLY_NO;optUser:OPT_USER;optTerm:OPT_TERM;specification:SPECIFICATION;"
                     + "presrtNo:PRESRT_NO;regionCode:REGION_CODE;admType:ADM_TYPE;rxType:RX_TYPE;temporaryFlg:TEMPORARY_FLG;"
                     + "releaseFlg:RELEASE_FLG;linkmainFlg:LINKMAIN_FLG;linkNo:LINK_NO;orderCode:ORDER_CODE;orderDesc:ORDER_DESC;"
                     + "goodsDesc:GOODS_DESC;orderCat1Code:ORDER_CAT1_CODE;mediQty:MEDI_QTY;mediUnit:MEDI_UNIT;freqCode:FREQ_CODE;"
                     + "routeCode:ROUTE_CODE;takeDays:TAKE_DAYS;dosageQty:DOSAGE_QTY;dispenseQty:DISPENSE_QTY;dispenseUnit:DISPENSE_UNIT;dosageUnit:DOSAGE_UNIT;"
                     + "giveboxFlg:GIVEBOX_FLG;ownPrice:OWN_PRICE;nhiPrice:NHI_PRICE;discountRate:DISCOUNT_RATE;ownAmt:OWN_AMT;"
                     + "arAmt:AR_AMT;drNote:DR_NOTE;nsNote:NS_NOTE;drCode:DR_CODE;orderDate:ORDER_DATE;deptCode:DEPT_CODE;"
                     + "dcDrCode:DC_DR_CODE;dcOrderDate:DC_ORDER_DATE;dcDeptCode:DC_DEPT_CODE;execDeptCode:EXEC_DEPT_CODE;"
                     + "setmainFlg:SETMAIN_FLG;orderSetGroupNo:ORDERSET_GROUP_NO;ordersetCode:ORDERSET_CODE;hideFlg:HIDE_FLG;rpttypeCode:RPTTYPE_CODE;"
                     + "optitemCode:OPTITEM_CODE;devCode:DEV_CODE;fileNo:FILE_NO;degreeCode:DEGREE_CODE;urgentFlg:URGENT_FLG;"
                     + "inspayType:INSPAY_TYPE;phaType:PHA_TYPE;doseType:DOSE_TYPE;printtypeflgInfant:PRINTTYPEFLG_INFANT;"
                     + "expensiveFlg:EXPENSIVE_FLG;ctrldrugclassCode:CTRLDRUGCLASS_CODE;prescriptNo:PRESCRIPT_NO;atcFlg:ATC_FLG;"
                     + "sendatcdate:SENDATC_DATE;receiptNo:RECEIPT_NO;billFlg:BILL_FLG;billDate:BILL_DATE;"
                     + "billUser:BILL_USER;printFlg:PRINT_FLG;rexpCode:REXP_CODE;hexpCode:HEXP_CODE;contractCode:CONTRACT_CODE;"
                     + "ctz1Code:CTZ1_CODE;ctz2Code:CTZ2_CODE;ctz3Code:CTZ3_CODE;phaCheckCode:PHA_CHECK_CODE;phaCheckDate:PHA_CHECK_DATE;"
                     + "phaDosageCode:PHA_DOSAGE_CODE;phaDosageDate:PHA_DOSAGE_DATE;phaDispenseCode:PHA_DISPENSE_CODE;phaDispenseDate:PHA_DISPENSE_DATE;nsExecCode:NS_EXEC_CODE;"
                     + "nsExecDate:NS_EXEC_DATE;nsExecDept:NS_EXEC_DEPT;dctagentCode:DCTAGENT_CODE;dctexcepCode:DCTEXCEP_CODE;"
                     + "dctTakeQty:DCT_TAKE_QTY;packageTot:PACKAGE_TOT;agencyOrgCode:AGENCY_ORG_CODE;dctagentFlg:DCTAGENT_FLG;businessNo:BUSINESS_NO;"//====pangben 2012-11-3 ����ڲ����׺���
//                     + "dctTakeQty:DCT_TAKE_QTY;packageTot:PACKAGE_TOT;agencyOrgCode:AGENCY_ORG_CODE;dctagentFlg:DCTAGENT_FLG;businessNo:BUSINESS_NO;"//==== 2012-11-3 ����ڲ����׺���
                     + "decoctCode:DECOCT_CODE;phaRetnDate:PHA_RETN_DATE;mrCode:MR_CODE;phaRetnCode:PHA_RETN_CODE;execDrCode:EXEC_DR_CODE;mrNo:MR_NO;memPackageId:MEM_PACKAGE_ID;"
                     + "phaCheckCode:PHA_CHECK_CODE_OLD:OLD;phaCheckDate:PHA_CHECK_DATE_OLD:OLD;phaDosageCode:PHA_DOSAGE_CODE_OLD:OLD;phaDosageDate:PHA_DOSAGE_DATE_OLD:OLD;phaDispenseCode:PHA_DISPENSE_CODE_OLD:OLD;phaDispenseDate:PHA_DISPENSE_DATE_OLD:OLD;"
                     + "counterNo:COUNTER_NO;execFlg:EXEC_FLG;receiptFlg:RECEIPT_FLG;billType:BILL_TYPE;printNo:PRINT_NO;cat1Type:CAT1_TYPE;costCenterCode:COST_CENTER_CODE;nhiCodeO:NHI_CODE_O;nhiCodeE:NHI_CODE_E;nhiCodeI:NHI_CODE_I" +
                     		";isPreOrder:IS_PRE_ORDER;batchNo:BATCH_NO;skintestFlg:SKINTEST_FLG;execDate:EXEC_DATE;age:AGE;weight:WEIGHT" 
                     		 + ";rxPresertNo:RX_PRESRT_NO;birthDate:BIRTH_DATE;updateTime:UPDATE_TIME "); //add by huangtt 20150512 ���·ַ�����´�����
    }

}
