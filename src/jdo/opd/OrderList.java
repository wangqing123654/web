package jdo.opd;

import java.sql.Timestamp;

import jdo.reg.ClinicRoomTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TModifiedList;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.util.StringTool;
import com.javahis.util.DateUtil;

import jdo.reg.Reg;

/**
 *
 * <p>
 * Title: ҩ��������Ŀ��������list
 * </p>
 *
 * <p>
 * Description:ҩ��������Ŀ��������list
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 200800908
 * @version 1.0
 */
public class OrderList
    extends TModifiedList {
    /**
     * ������Ϣ
     */
    private int maxseq = 0;
    /**
     * ����
     */
    private Pat pat;
    /**
     * ��������
     */
    private PrescriptionList prescriptionlist;
    /**
     * Ԥ�������
     * caowl 20131117
     * */
    private String isPreOrder;
    
	/**
     *Ԥ��ʱ�� 
     *caowl 20131117
     * */
    private Timestamp preDate;
    
    /**
     * ҽ������
     */
    private String rxType;
    /**
     * ����ǩ��
     */
    private String rxNo;
    /**
     * �����
     */
    private String medApplyNo;
    /**
     * ����ǩ�ڸ��������ķ����ܼ�
     */
    private Double sumFee;
    /**
     * ������
     */
    private String mrNo;
    /**
     * ��������
     */
    private String deptCode;
    /**
     * ����ҽ��
     */
    private String drCode;
    /**
     * ��ҩʱ��
     */
    private Timestamp phaDispenseDate;
    /**
     * ���ʱ��
     */
    private Timestamp phaCheckDate;
    /**
     * ��ҩʱ��
     */
    private Timestamp phaDosageDate;
    /**
     * ��ҩʱ�䣨PHAר�ã�
     */
    private Timestamp phaRetnDate;
    /**
     * ִ�п���
     */
    private String execDeptCode;
    /**
     * ����
     */
    private int presrtNo;
    /**
     * ��ҩ��
     */
    private String decoctCode;
    /**
     * ����ע��
     */
    private String dctagentFlg;
    /**
     * ����ִ�п���
     */
    private String agencyOrgCode;
    /**
     * ��ҩ��
     */
    private int prescriptNo;
    /**
     * Ʒ��
     */
    private String variety;
    /**
     * �շ�
     */
    private int takeDays;
    /**
     * ��Ƭʹ�ü���
     */
    private int dctTakeQty;
    /**
     * Ƶ��
     */
    private String freqCode;
    /**
     * �÷�
     */
    private String routeCode;
    /**
     * ��ҩ��ʽ
     */
    private String dctAgentCode;
    /**
     * ��ע
     */
    private String drNote;
    /**
     * �ܿ���
     */
    private String totGram;
    /**
     * ����
     */
    private String urgentFlg;
    /**
     * �Ա�
     */
    private String releaseFlg;
    /**
     * �����ܰ���(����*Ƶ��)
     */
    private int packageTot;
    /**
     * ��ҩ��Ա��PHAר�ã�
     */
    private String phaRetnCode;
    /**
     * �ż�ס��
     */
    private String admType;


    /**
     * ��ҩ���ں�
     */
    private int counterNo;


    /**
     * ִ��ע��
     */
    private String execFlg;

    /**
     * ��ҩ��
     */
    private String printNo;

    /**
     * ҽ��ϸ����
     */
    private String cat1Type;
    /**
     * �ڲ����׺���
     */
    private String businessNo;//BUSINESS_NO
    
    private String memPackageId;
    
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
    private String batchNo ; 
    
    /**
     * Ƥ�Ա�־����������
     */
    private String  skintestFlg ;
    
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
    /**
     * ѡ��״̬
     */
    private boolean chargeFlg;
    public boolean getChargeFlg(){
    	return chargeFlg;
    }
    public void setChargeFlg(boolean chargeFlg){
    	this.chargeFlg=chargeFlg;
    }
    /**
     * �õ�ҽ��ϸ����
     * @return String
     */
    public String getCat1Type() {
        return cat1Type;
    }
    /**
     * ����ҽ��ϸ����
     * @param value String
     */
    public void setCat1Type(String value) {
        this.cat1Type = value;
    }
    /**
     * ȡ����ҩ��
     * @return String
     */
    public String getPrintNo() {
        return printNo;
    }

    /**
     * ������ҩ��
     * @param value String
     */
    public void setPrintNo(String value) {
        this.printNo = value;
    }

    /**
     * ȡ��ִ��ע��
     * @return String
     */
    public String getExecFlg() {
        return execFlg;
    }

    /**
     * ����ִ��ע��
     * @param value String
     */
    public void setExecFlg(String value) {
        this.execFlg = value;
    }
    /**
     * ��Ʊע��
     */
    private String receiptFlg;


    /**
     * �õ���Ʊע��
     * @return String
     */
    public String getReceiptFlg() {
        return receiptFlg;
    }

    /**
     * ���ô�Ʊע��
     * @param value String
     */
    public void setReceiptFlg(String receiptFlg) {
        this.receiptFlg = receiptFlg;
    }

    /**
     * ȡ����ҩ���ں�
     * @return int
     */
    public int getCounterNo() {
        return counterNo;
    }

    /**
     * ������ҩ���ں�
     * @param value int
     */
    public void setCounterNo(int value) {
        counterNo = value;
    }

    /**
     * ���ô���ִ�п���
     *
     * @param agencyOrgCode
     *            String
     */
    public void setAgencyOrgCode(String agencyOrgCode) {
        this.agencyOrgCode = agencyOrgCode;
    }

    /**
     * �õ�����ִ�п���
     *
     * @return agencyOrgCode String
     */
    public String getAgencyOrgCode() {
        return this.agencyOrgCode;
    }

    /**
     * ���ô���ע��
     *
     * @param dctagentFlg
     *            String
     */
    public void setDctagentFlg(String dctagentFlg) {
        this.dctagentFlg = dctagentFlg;
    }

    /**
     * �õ�����ע��
     *
     * @return dctagentFlg String
     */
    public String getDctagentFlg() {
        return this.dctagentFlg;
    }

    /**
     * �õ���ҩ��Ա
     * @return phaReturnCode String
     */
    public String getPhaRetnCode() {
        return phaRetnCode;
    }

    /**
     * ������ҩ��Ա
     * @parm dsReturnUser String
     */
    public void setPhaRetnCode(String dsReturnUser) {
        this.phaRetnCode = dsReturnUser;
    }


    /**
     * ���ü�ҩ��
     *
     * @param decoctCode
     *            String
     */
    public void setDecoctCode(String decoctCode) {
        this.decoctCode = decoctCode;
    }

    /**
     * �õ���ҩ��
     *
     * @return decoctCode String
     */
    public String getDecoctCode() {
        return this.decoctCode;
    }

    /**
     * ���÷���
     *
     * @param presrtNo
     *            int
     *
     */
    public void setPresrtNo(int presrtNo) {
        this.presrtNo = presrtNo;
    }

    /**
     * �õ�����
     *
     * @return presrtNo int
     */
    public int getPresrtNo() {
        return this.presrtNo;
    }

    /**
     * ���ò�����Ϣ
     *
     * @param pat
     */
    public void setPat(Pat pat) {
        this.pat = pat;
    }

    /**
     * �õ�������Ϣ
     *
     * @return
     */
    public Pat getPat() {
        return pat;
    }

    /**
     * ���ô�������
     *
     * @param prescriptionlist
     *            PrescriptionList
     */
    public void setPrescriptionList(PrescriptionList prescriptionlist) {
        this.prescriptionlist = prescriptionlist;
    }

    /**
     * �õ���������
     *
     * @return PrescriptionList PrescriptionList
     */
    public PrescriptionList getPrescriptionList() {
        return this.prescriptionlist;
    }

    /**
     * ���ô���ǩ��
     *
     * @param rxNo String
     */
    public void setRxNo(String rxNo) {
        this.rxNo = rxNo;
    }
    /**
     * ���������
     * @param medApplyNo String
     */
    public void setMedApplyNo(String medApplyNo) {
        this.medApplyNo = medApplyNo;
    }
    /**
     * �õ�����ǩ��
     *
     * @return rxNo String
     */
    public String getRxNo() {
        return this.rxNo;
    }

    /**
     * �õ������
     * @return String
     */
    public String getMedApplyNo() {
        return this.medApplyNo;
    }
    /**
     * �õ������ܼ�
     *
     * @return sumFee Double
     */
    public Double getSumFee() {
        return this.sumFee;
    }

    /**
     * ���÷����ܼ�
     *
     * @param sumFee
     *            Double
     */
    public void setSumFee(Double sumFee) {
        this.sumFee = sumFee;
    }

    /**
     * �õ�������
     *
     * @return mrNo String
     */
    public String getMrNo() {
        return this.mrNo;
    }

    /**
     * ���ò�����
     *
     * @param mrNo
     *            String
     */
    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    /**
     * �õ����Ŵ���
     *
     * @return deptCode String
     */
    public String getDeptCode() {
        return this.deptCode;
    }

    /**
     * ���ò���
     *
     * @param deptCode
     *            String
     */
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    /**
     * �õ�����ҽ��
     *
     * @return drCode String
     */
    public String getDrCode() {
        return this.drCode;
    }

    /**
     * ���ÿ���ҽ��
     *
     * @param drCode
     *            String
     */
    public void setDrCode(String drCode) {
        this.drCode = drCode;
    }

    /**
     * �õ���ҩ����
     *
     * @return phaDispenseDate Timestamp
     */
    public Timestamp getPhaDispenseDate() {
        return this.phaDispenseDate;
    }

    /**
     * ���÷�ҩ����
     *
     * @param phaDispenseDate
     *            String
     */
    public void setPhaDispenseDate(Timestamp dsdlvryDate) {
        this.phaDispenseDate = dsdlvryDate;
    }

    /**
     * �õ�ִ�в���
     *
     * @return execDeptCode String
     */
    public String getExecDeptCode() {
        return this.execDeptCode;
    }

    /**
     * ����ִ�в���
     *
     * @param execDeptCode
     *            String
     */
    public void setExecDeptCode(String execDeptCode) {
        this.execDeptCode = execDeptCode;
    }

    /**
     * �õ���������
     *
     * @return RxType String
     */
    public String getRxType() {
        return rxType;
    }

    /**
     * ���ô�������
     *
     * @param rxType
     *            String
     */
    public void setRxType(String rxType) {
        this.rxType = rxType;
    }

    /**
     * �õ����ʱ��
     *
     * @return phacheckDate String
     */
    public Timestamp getPhaCheckDate() {
        return phaCheckDate;
    }

    /**
     * �������ʱ��
     *
     * @param phacheckDate
     *            String
     */
    public void setPhaCheckDate(Timestamp dscheckDate) {
        this.phaCheckDate = dscheckDate;
    }

    /**
     * �õ���ҩʱ��
     *
     * @return dsdgtDate String
     */
    public Timestamp getPhaDosageDate() {
        return phaDosageDate;
    }

    /**
     * ������ҩʱ��
     *
     * @param dsdgtDate
     *            String
     */
    public void setPhaDosageDate(Timestamp dsdgtDate) {
        this.phaDosageDate = dsdgtDate;
    }

    /**
     * �õ���ҩʱ�䣨PHAר�ã�
     *
     * @return phaReturnDate String
     */
    public Timestamp getPhaRetnDate() {
        return phaRetnDate;
    }

    /**
     * ������ҩʱ�䣨PHAר�ã�
     *
     * @param dsreturnDate
     *            String
     */
    public void setPhaRetnDate(Timestamp dsreturnDate) {
        this.phaRetnDate = dsreturnDate;
    }

    /**
     * ������ҩ��
     *
     * @param prescriptNo
     *            int
     */
    public void setPrescriptNo(int prescriptNo) {
        this.prescriptNo = prescriptNo;
    }

    /**
     * �õ���ҩ��
     *
     * @return prescriptNo int
     */
    public int getPrescriptNo() {
        return this.prescriptNo;
    }

    /**
     * ����Ʒ��
     *
     * @param variety
     *            String
     */
    public void setVariety(String variety) {
        this.variety = variety;
    }

    /**
     * �õ�Ʒ��
     *
     * @return variety String
     */
    public String getVariety() {
        return this.variety;
    }

    /**
     * �õ��շ�
     *
     * @return takeDays int
     */
    public int getTakeDays() {
        return takeDays;
    }

    /**
     * �����շ�
     *
     * @parm takeDays int
     */
    public void setTakeDays(int takeDays) {
        this.takeDays = takeDays;
    }

    /**
     * �õ���Ƭʹ����
     *
     * @return dctTakeQty int
     */
    public int getDctTakeQty() {
        return dctTakeQty;
    }

    /**
     * ������Ƭʹ����
     *
     * @parm dctTakeQty int
     */
    public void setDctTakeQty(int dctTakeQty) {
        this.dctTakeQty = dctTakeQty;
    }

    /**
     * �õ�Ƶ��
     *
     * @return freqCode String
     */
    public String getFreqCode() {
        return freqCode;
    }

    /**
     * ����Ƶ��
     *
     * @parm freqCode String
     */
    public void setFreqCode(String freqCode) {
        this.freqCode = freqCode;
    }

    /**
     * �õ��÷�
     *
     * @return routeCode String
     */
    public String getRouteCode() {
        return routeCode;
    }

    /**
     * �����÷�
     *
     * @parm routeCode String
     */
    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    /**
     * �õ�����巨
     *
     * @return dctAgentCode String
     */
    public String getDctAgentCode() {
        return dctAgentCode;
    }

    /**
     * ��������巨
     *
     * @parm dctAgentCode String
     */
    public void setDctAgentCode(String dctAgentCode) {
        this.dctAgentCode = dctAgentCode;
    }

    /**
     * �õ���ע
     *
     * @return description String
     */
    public String getDrNote() {
        return drNote;
    }

    /**
     * ���ñ�ע
     *
     * @parm description String
     */
    public void setDrNote(String description) {
        this.drNote = description;
    }

    /**
     * �õ��ܿ���
     *
     * @return totGram String
     */
    public String getTotGram() {
        return totGram;
    }

    /**
     * �����ܿ���
     *
     * @parm totGram String
     */
    public void setTotGram(String totGram) {
        this.totGram = totGram;
    }

    /**
     * �õ�����
     *
     * @return urgentFlg String
     */
    public String getUrgentFlg() {
        return urgentFlg;
    }

    /**
     * ���ü���
     *
     * @parm urgentFlg String
     */
    public void setUrgentFlg(String urgentFlg) {
        this.urgentFlg = urgentFlg;
    }

    /**
     * �õ��Ա�
     *
     * @return releaseFlg String
     */
    public String getReleaseFlg() {
        return releaseFlg;
    }

    /**
     * �����Ա�
     *
     * @parm releaseFlg String
     */
    public void setReleaseFlg(String releaseFlg) {
        this.releaseFlg = releaseFlg;
    }

    /**
     * �õ��ܰ���
     *
     * @return packageTot int
     */
    public int getPackageTot() {
        return packageTot;
    }

    /**
     * �����ܰ���
     *
     * @parm packageTot int
     */
    public void setPackageTot(int packageTot) {
        this.packageTot = packageTot;
    }

    /**
     * �����ż�ס��
     * @param admType String
     */
    public void setAdmType(String admType) {
        this.admType = admType;
    }

    /**
     * �õ��ż�ס��
     * @return admType String
     */
    public String getAdmType() {
        return this.admType;
    }
    
    /**
     * �õ�����
     * @return
     */
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
     * ������
     *
     * @param prescriptionlist
     */
    public OrderList(PrescriptionList prescriptionlist) {
        this();
        this.setPrescriptionList(prescriptionlist);

    }
    //caowl 2131117 start
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
     *�õ�Ԥ��ʱ�� 
     * */
	public Timestamp getPreDate() {
		return preDate;
	}
	/**
     *����Ԥ��ʱ�� 
     * */
	public void setPreDate(Timestamp preDate) {
		this.preDate = preDate;
	}
	
	 //caowl 2131117 end
    /**
     * ������
     */
    public OrderList() {
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
                     + "dctTakeQty:DCT_TAKE_QTY;packageTot:PACKAGE_TOT;agencyOrgCode:AGENCY_ORG_CODE;dctagentFlg:DCTAGENT_FLG;businessNo:BUSINESS_NO;chargeFlg:CHARGE_FLG;"//====pangben 2012-11-3 ����ڲ����׺���
                     + "decoctCode:DECOCT_CODE;phaRetnDate:PHA_RETN_DATE;mrCode:MR_CODE;phaRetnCode:PHA_RETN_CODE;execDrCode:EXEC_DR_CODE;mrNo:MR_NO;execFlg:EXEC_FLG;receiptFlg:RECEIPT_FLG;billType:BILL_TYPE;printNo:PRINT_NO;cat1Type:CAT1_TYPE;memPackageId:MEM_PACKAGE_ID;"
                     + "phaCheckCode:PHA_CHECK_CODE_OLD:OLD;phaCheckDate:PHA_CHECK_DATE_OLD:OLD;phaDosageCode:PHA_DOSAGE_CODE_OLD:OLD;"
                     + "phaDosageDate:PHA_DOSAGE_DATE_OLD:OLD;phaDispenseCode:PHA_DISPENSE_CODE_OLD:OLD;phaDispenseDate:PHA_DISPENSE_DATE_OLD:OLD;isPreOrder:IS_PRE_ORDER;batchNo:BATCH_NO;skintestFlg:SKINTEST_FLG;age:AGE;weight:WEIGHT" //caowl 20131117 add Ԥ�����
                     + ";rxPresertNo:RX_PRESRT_NO"); //add by huangtt 20150512 ���·ַ�����´�����
    }

    /**
     * ����ҽ��
     *
     * @return Order
     */
    public Order newOrder() {
        Order order = new Order();
        if (this.getPrescriptionList() != null) {
            order.setCaseNo(getPrescriptionList().getCaseNo());
            order.setAdmType(getPrescriptionList().getAdmType());
            order.setDrCode(getPrescriptionList().getDrCode());
        }
        //��������
        order.setRxType(this.getRxType());
        //������
        order.setRxNo(this.getRxNo());
        maxseq = ++maxseq;
        order.setSeqNo(maxseq);
        order.setMedApplyNo(this.getMedApplyNo());
        //System.out.println("maxseq:="+maxseq);
        //���÷���
        order.setPresrtNo(this.getPrescriptionList().getGroupPrsptSize(this.
            getRxType()));
        //���ò�����
        order.setOptUser(Operator.getID());
        //���ö�ĩ
        order.setOptTerm(Operator.getIP());
        //��������
        order.setRegionCode(Operator.getRegion());
        // ����ҽ��
        order.setDrCode(prescriptionlist.getReg().getRealdrCode());
        // ��������
        order.setDeptCode(prescriptionlist.getReg().getRealdeptCode());
        // ����ʱ��
        order.setOrderDate(StringTool.getTimestamp(SystemTool.getInstance()
            .getDate()));
        //ִ�п���
        TParm parm = ClinicRoomTool.getInstance().getOrgCodeByRoomNo(
            prescriptionlist.getReg().getClinicroomNo());

        order.setExecDeptCode(parm.getValue("ORG_CODE", 0));
        order.setExecDrCode(Operator.getID());
        //���ò�������
        order.setPat(getPat());
        //���ò�����
        order.setMrNo(getPat().getMrNo());
        //�����ż�ס��
        order.setAdmType(getAdmType());
        //�����շ�ע��
        order.setBillFlg("N");
        order.setTemporaryFlg("N");
        order.setReleaseFlg("N");
        order.setLinkmainFlg("N");
        order.setLinkNo("");
        order.setGiveboxFlg("N");
        order.setHideFlg("N");
        order.setUrgentFlg("N");
        order.setPrinttypeflgInfant("N");
        order.setAtcFlg("N");
        order.setPrintFlg("N");
        order.setDctagentFlg("N");
        this.newData(order);
        return order;
    }

    /**
     * �õ�ҽ��
     *
     * @param index
     *            int
     * @return Order
     */
    public Order getOrder(int index) {
        return (Order) get(index);
    }

    /**
     * ��չ�ã�Ŀ��ͬ���صķ���
     *
     * @return TParm
     */
    public TParm getOrderListParm() {
        return getOrderListParm(null);
    }

    /**b
     * ȡ��TABLE�õ�����
     *
     * @param parm
     * @return TParm
     */
    public TParm getOrderListParm(TParm parm) {
        if (parm == null)
            parm = new TParm();
//        parm.addData("RX_PRESRT_NO", getRxNo()+ (this.getPresrtNo()==0? "":this.getPresrtNo()));  //add by huangtt 20150121
        parm.addData("RX_PRESRT_NO", this.getRxPresertNo());  //add by huangtt 20150121
        parm.addData("RX_NO", getRxNo());
        parm.addData("PAT_NAME", PatTool.getInstance()
                     .getNameForMrno(getMrNo()));
        parm.addData("SUM_FEE", this.getSumFee());
        parm.addData("MR_NO", getMrNo());
        parm.addData("DEPT_CODE", getDeptCode());
        parm.addData("DR_CODE", getDrCode());
        parm.addData("EXEC_DEPT_CODE", this.getExecDeptCode());
        parm.addData("PHA_CHECK_DATE", this.getPhaCheckDate());
        parm.addData("PHA_DOSAGE_DATE", this.getPhaDosageDate());
        parm.addData("PHA_DISPENSE_DATE", this.getPhaDispenseDate());
        parm.addData("PHA_RETN_DATE", this.getPhaRetnDate());
        parm.addData("PRESRT_NO", this.getPresrtNo());
        parm.addData("DCTAGENT_FLG", this.getDctagentFlg());
        parm.addData("DECOCT_CODE", this.getDecoctCode());
        parm.addData("PRESCRIPT_NbY", this.getDctTakeQty());
        parm.addData("FREQ_CODE", this.getFreqCode());
        parm.addData("ROUTE_CODE", this.getRouteCode());
        parm.addData("DCTAGENT_CODE", this.getDctAgentCode());
        parm.addData("DR_NOTE", this.getDrNote());
        parm.addData("TOT_GRAM", this.getTotGram());
        parm.addData("URGENT_FLG", this.getUrgentFlg());
        parm.addData("RELEASE_FLG", this.getReleaseFlg());
        parm.addData("PACKAGE_TOT", this.getPackageTot());
        parm.addData("RX_TYPE", this.getRxType());
        parm.addData("PHA_RETN_CODE", this.getPhaRetnCode());
        parm.setData("ACTION", "COUNT", parm.getCount("MR_NO"));
        parm.addData("COUNTER_NO", this.getCounterNo());
        parm.addData("EXEC_FLG", this.getExecFlg());
        parm.addData("RECEIPT_FLG", this.getReceiptFlg());
        parm.addData("PRINT_NO", this.getPrintNo());
        parm.addData("CAT1_TYPE", this.getCat1Type());
        parm.addData("BUSINESS_NO", this.getBusinessNo());//==pangben 2012-11-3
        parm.addData("CHARGE_FLG", this.getChargeFlg());
        parm.addData("IS_PRE_ORDER", this.getIsPreOrder());//caowl 20131117 Ԥ�����
        parm.addData("VARIETY", this.getVariety());
        parm.addData("AGE", this.getAge());// add by wangbin 20140731 ���Ӳ�������
        parm.addData("WEIGHT", this.getWeight());// add by wangbin 20140731 ���Ӳ�������
        return parm;
    }

    public void removeData(int index) {
        if (maxseq > 1) {
            maxseq = --maxseq;
        }
        else {
            maxseq = 1;
        }
        super.removeData(index);

    }

    /**
     * ����ֻ�������TParm,��������һ��Ϊ�������ݶ�Ӧ��Order
     * @return TParm
     */
    public TParm getTParmForTable() {
        TParm result = this.getParm(this.PRIMARY);
        //System.out.println("gettparmfortable"+result);
        Integer linkno;
        for (int i = result.getCount() - 1; i >= 0; i--) {
            linkno = result.getInt("LINK_NO", i);
            if (linkno != 0) {
                result.removeRow(i);
                continue;
            }
            result.setData("ORDER", i, this.getOrder(i));
        }
        return result;
    }

    /**
     * ��ʼ��List
     *
     * @param parm
     * @return �棺�ɹ����٣�ʧ��
     */
    public boolean initParm(TParm parm) {
        if (parm == null) {
            return false;
        }
        int count = parm.getCount();
        maxseq = TCM_Transform.getInt(parm.getData("SEQ_NO", count - 1));
        // ΪPHA��ʾORDERLISTʱ�ġ�����ÿ��ORDER���ۼ�
        double sumFee = 0.0;
        // ΪPHA��ʾORDERLISTʱ�ġ��ܿ�������ÿ��ORDER���ۼ�
        double totgram = 0.0;
        Timestamp sysDate = TJDODBTool.getInstance().getDBTime();
        for (int i = 0; i < count; i++) {
            Order order = new Order();
            order.setPat(getPat());
            add(order);
            order.setDcOrder(true); //fudwadd
            if (!order.initParm(parm, i)) {
                return false;
            }

            // �ۼ�PHA�á���
            sumFee += TCM_Transform.getDouble(order.getOwnAmt());
            // �ܿ���
            totgram += TCM_Transform.getDouble(order.getMediQty());
        }

        if (count > 0) {
            Order first = this.getOrder(0);
            if (first == null) {
                return false;
            }
            // ����ǩ��
            this.setRxNo(first.getRxNo());
            // ��������
            this.setRxType(first.getRxType());
            // ��������
            this.setDeptCode(first.getDeptCode());
            // ����ҽ��
            this.setDrCode(first.getDrCode());
            // ִ�п���
            this.setExecDeptCode(first.getExecDeptCode());
            // ���ʱ��
            this.setPhaCheckDate(first.getPhaCheckDate());
            // ��ҩʱ��
            this.setPhaDosageDate(first.getPhaDosageDate());
            // ��ҩʱ��
            this.setPhaDispenseDate(first.getPhaDispenseDate());
            // ��ҩʱ��
            this.setPhaRetnDate(first.getPhaRetnDate());
            // ������
            if (getPat() != null) {
                this.setMrNo(this.getPat().getMrNo());
            }
            else {
                this.setMrNo(parm.getValue("MR_NO", 0));
            }
            // ����
            this.setPresrtNo(first.getPresrtNo());
            // ����ע��
            this.setDctagentFlg(first.getDctagentFlg());
            // �������
            this.setDecoctCode(first.getDecoctCode());
            // ����ִ�п���
            this.setAgencyOrgCode(first.getAgencyOrgCode());
            // ��ҩ��
            this.setPrescriptNo(first.getPrescriptNo());
            // Ʒ��
            this.setVariety(TCM_Transform.getString(count));
            // ÿ��orderList��Ӧ������ܼ�
            this.setSumFee(sumFee);
            // ����/�շ�
            this.setTakeDays(first.getTakeDays());
            // ��Ƭʹ�ü���(ml)
            this.setDctTakeQty(first.getDctTakeQty());
            // Ƶ��
            this.setFreqCode(first.getFreqCode());
            // �÷�
            this.setRouteCode(first.getRouteCode());
            // ��ҩ��ʽ
            this.setDctAgentCode(first.getDctagentCode());
            // ��ע
            this.setDrNote(first.getDrNote());
            // �ܿ���
            this.setTotGram(TCM_Transform.getString(totgram));
            // ����
            this.setUrgentFlg(first.getUrgentFlg());
            //�Ա�
            this.setReleaseFlg(first.getReleaseFlg());
            //��ҩ��Ա
            this.setPhaRetnCode(first.getPhaRetnCode());
            //�����ܰ���(����*Ƶ��) todo:ȡ��Ƶ��
            this.setPackageTot(0);
            //�ż�ס��
            this.setAdmType(first.getAdmType());
            //��ҩ���ں�
            this.setCounterNo(first.getCounterNo());
            //ִ��ע��
            this.setExecFlg(first.getExecFlg());
            //��Ʊע��
            this.setReceiptFlg(first.getReceiptFlg());
            //��ҩ��
            this.setPrintNo(first.getPrintNo());
            //ҽ�����
            this.setCat1Type(first.getCat1Type());
            //�ڲ����׺���=pangben 2012-11-3
            this.setBusinessNo(first.getBusinessNo());
            this.setIsPreOrder(first.getIsPreOrder());//Ԥ�������λ  caowl 20131117
         
            this.setChargeFlg(first.getChargeFlg());
            this.setBatchNo(first.getBatchNo());
            this.setSkintestFlg(first.getSkintestFlg());
            
            // ���� first.getAge()
            //System.out.println("--birthDate--"+first.getBirthDate());
            String strAge="δ֪";
			try {
				strAge = DateUtil.showAge(first.getBirthDate(), sysDate);
				this.setAge(strAge);
			} catch (Exception e) {
				//
				this.setAge(first.getAge());
			}           
            
            // ����
            this.setWeight(first.getWeight());
            
            //�ַ�����´�����   //add by huangtt 20150512
            this.setRxPresertNo(first.getRxPresertNo());
            this.setUpdateTime(first.getUpdateTime());
            
        }
        return true;
    }
      
    
}
