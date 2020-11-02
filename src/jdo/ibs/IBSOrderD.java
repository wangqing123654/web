package jdo.ibs;

import java.sql.Timestamp;
import java.util.Vector;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.util.TypeTool;
import jdo.odo.StoreBase;
import jdo.opd.TotQtyTool;

/**
 *
 * <p>Title: סԺ������ϸ����</p>
 *
 * <p>Description: סԺ������ϸ����</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class IBSOrderD extends StoreBase {
    //
    boolean isOriginal = false;
    TDataStore sysFee = TIOM_Database.getLocalTable("SYS_FEE");
    /**
     * ������
     * @param caseNo String
     */
    public IBSOrderD(String caseNo) {
        this.caseNo = caseNo;

    }

    /**
     * �������
     */
    private String caseNo;
    /**
     * �������
     */
    private int caseNoSeq;
    /**
     * ���������
     */
    private String seqNo;
    /**
     * ��������
     */
    private Timestamp billDate;
    /**
    * ִ������
    */
   private Timestamp execDate;
   /**
    /**
     * ҽ�����
     */
    private String orderNo; //ORDER_NO
    /**
     * ҽ��˳���
     */
    private String orderSeq; //ORDER_SEQ
    /**
     * ҽ������
     */
    private String orderCode;
    /**
     * ҽ��ϸ����
     */
    private String orderCat1Code;
    /**
     * ҽ��ϸ����Ⱥ��
     */
    private String cat1Type;
    /**
     * ����ҽ��Ⱥ���
     */
    private String ordersetGroupNo;
    /**
     * ����ҽ��
     */
    private String ordersetCode;
    /**
     * ����ҽ��ϸ������ע��
     */
    private String indvFlg;
    /**
     * ���Ҵ���
     */
    private String deptCode;
    /**
     *  ��������
     */
    private String stationCode;
    /**
     * ҽ������
     */
    private String drCode;
    /**
     * ִ�в���
     */
    private String exeStationCode;
    /**
     * ִ�п��Ҵ���
     */
    private String exeDeptCode;
    /**
     * ִ��ҽʦ����
     */
    private String exeDrCode;
    /**
     * ��ҩ����
     */
    private double mediQty;
    /**
     * ��ҩ��λ
     */
    private String mediUnit;
    /**
     * ���ʹ���
     */
    private String doseCode;
    /**
     * Ƶ�δ���
     */
    private String freqCode;
    /**
     * ��ҩ����
     */
    private int takeDays;
    /**
     * ��ҩ����
     */
    private double dosageQty;
    /**
     * ��ҩ��λ
     */
    private String dosageUnit;
    /**
     * �Էѵ���
     */
    private double ownPrice;
    /**
     * ҽ������
     */
    private double nhiPrice;
    /**
     * �������
     */
    private double totAmt;
    /**
     * �Է�ע��
     */
    private String ownFlg;
    /**
     * �շ�ע��
     */
    private String billFlg;
    /**
     * �վݷ��ô���
     */
    private String rexpCode;
    /**
     * �ʵ���
     */
    private String billNo; //BILL_NO
    /**
     * Ժ�ڷ��ô���
     */
    private String hexpCode;
    /**
     * ִ����ʼʱ��
     */
    private Timestamp beginDate;
    /**
     * ��Ч����
     */
    private Timestamp endDate;
    /**
     * �Է��ܼ�
     */
    private double ownAmt;
    /**
     * �Էѱ���
     */
    private double ownRate;
    /**
     * ����ע��
     */
    private String requestFlg;
    /**
     * ���쵥��
     */
    private String requestNo;
    /**
     * �����������
     */
    private String invCode;
    /**
     * ������Ա
     */
    private String optUser;
    /**
     * ��������
     */
    private Timestamp optDate;
    /**
     * �����ն�
     */
    private String optTerm;
    /**
     * ҽ������
     */
    private String orderDescIBS;
    /**
     * �ɱ�����
     */
    private String costCenterCode;
    /**
     * ʱ��
     */
    private String schdCode; //SCHD_CODE
    /**
     * �ٴ�·��
     */
    private String clncpathCode; //CLNCPATH_CODE
    /**
     * �ײ�״̬ Y �ײ���==pangben 2015-7-17
     */
    private String includeFlg;//INCLUDE_FLG
    /**
     * �ײ�ִ��״̬==pangben 2015-10-29
     */
    private String includeExecFlg;//INCLUDE_EXEC_FLG
    //סԺ�Ʒѽ����˷Ѳ���ʹ�ã�����ҽ���˷ѣ���ý��ױ��е����ݣ����ǲ�ѯ�ֵ��
    private boolean unFeeFlg=false;
    public boolean isUnFeeFlg() {
		return unFeeFlg;
	}

	public void setUnFeeFlg(boolean unFeeFlg) {
		this.unFeeFlg = unFeeFlg;
	}

	/**
     * ��Ժ��ҩע��
     */
    private String dsFlg; //DS_FLG
    public String getIncludeExecFlg() {
		return includeExecFlg;
	}

	public void setIncludeExecFlg(String includeExecFlg) {
		this.includeExecFlg = includeExecFlg;
	}

	public String getIncludeFlg() {
		return includeFlg;
	}

	public void setIncludeFlg(String includeFlg) {
		this.includeFlg = includeFlg;
	}

	/**
     * ���þ������
     * @param caseNo String
     */
    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    /**
     * �õ��������
     * @return String
     */
    public String getCaseNo() {
        return caseNo;
    }

    /**
     * �õ�ִ����ʼʱ��
     * @return Timestamp
     */
    public Timestamp getBeginDate() {
        return beginDate;
    }

    /**
     * �õ���������
     * @return Timestamp
     */
    public Timestamp getBillDate() {
        return billDate;
    }

    /**
     * �õ�ִ������
     * @return Timestamp
     */
    public Timestamp getExecDate() {
        return execDate;
    }

    /**
     * �õ��շ�ע��
     * @return String
     */
    public String getBillFlg() {
        return billFlg;
    }

    /**
     * �õ��������
     * @return int
     */
    public int getCaseNoSeq() {
        return caseNoSeq;
    }

    /**
     * �õ�ҽ��ϸ����Ⱥ��
     * @return String
     */
    public String getCat1Type() {
        return cat1Type;
    }

    /**
     * �õ�ִ�в�������
     * @return String
     */
    public String getExeStationCode() {
        return exeStationCode;
    }

    /**
     * �õ�ִ�п��Ҵ���
     * @return String
     */
    public String getExeDeptCode() {
        return exeDeptCode;
    }

    /**
     * �õ���Ч����
     * @return Timestamp
     */
    public Timestamp getEndDate() {
        return endDate;
    }

    /**
     * �õ����Ҵ���
     * @return String
     */
    public String getDeptCode() {
        return deptCode;
    }

    /**
     * �õ���ҩ����
     * @return double
     */
    public double getDosageQty() {
        return dosageQty;
    }

    /**
     * �õ���ҩ��λ
     * @return String
     */
    public String getDosageUnit() {
        return dosageUnit;
    }

    /**
     * �õ����ʹ���
     * @return String
     */
    public String getDoseCode() {
        return doseCode;
    }

    /**
     * �õ�ҽʦ����
     * @return String
     */
    public String getDrCode() {
        return drCode;
    }

    /**
     * �õ�ִ��ҽʦ����
     * @return String
     */
    public String getExeDrCode() {
        return exeDrCode;
    }

    /**
     * �õ�Ƶ�δ���
     * @return String
     */
    public String getFreqCode() {
        return freqCode;
    }

    /**
     * �õ�Ժ�ڷ��ô���
     * @return String
     */
    public String getHexpCode() {
        return hexpCode;
    }

    /**
     * �õ�����ҽ��ϸ������ע��
     * @return String
     */
    public String getIndvFlg() {
        return indvFlg;
    }

    /**
     * �õ������������
     * @return String
     */
    public String getInvCode() {
        return invCode;
    }

    /**
     * �õ���ҩ����
     * @return double
     */
    public double getMediQty() {
        return mediQty;
    }

    /**
     * �õ���ҩ��λ
     * @return String
     */
    public String getMediUnit() {
        return mediUnit;
    }

    /**
     * �õ�ҽ������
     * @return double
     */
    public double getNhiPrice() {
        return nhiPrice;
    }

    /**
     * �õ�ҽ��ϸ����
     * @return String
     */
    public String getOrderCat1Code() {
        return orderCat1Code;
    }

    /**
     * �õ�ҽ������
     * @return String
     */
    public String getOrderCode() {
        return orderCode;
    }

    /**
     * �õ�����ҽ������
     * @return String
     */
    public String getOrdersetCode() {
        return ordersetCode;
    }

    /**
     * �õ�����ҽ��Ⱥ���
     * @return String
     */
    public String getOrdersetGroupNo() {
        return ordersetGroupNo;
    }

    /**
     * �õ��Է��ܼ�
     * @return double
     */
    public double getOwnAmt() {
        return ownAmt;
    }

    /**
     * �õ��Է�ע��
     * @return String
     */
    public String getOwnFlg() {
        return ownFlg;
    }

    /**
     * �õ��Էѵ���
     * @return double
     */
    public double getOwnPrice() {
        return ownPrice;
    }

    /**
     * �õ��Ը�����
     * @return double
     */
    public double getOwnRate() {
        return ownRate;
    }

    /**
     * �õ�����ע��
     * @return String
     */
    public String getRequestFlg() {
        return requestFlg;
    }

    /**
     * �õ����쵥��
     * @return String
     */
    public String getRequestNo() {
        return requestNo;
    }

    /**
     * �õ��վݷ��ô���
     * @return String
     */
    public String getRexpCode() {
        return rexpCode;
    }

    /**
     * �õ��˵������
     * @return String
     */
    public String getSeqNo() {
        return seqNo;
    }

    /**
     *�õ���������
     * @return String
     */
    public String getStationCode() {
        return stationCode;
    }

    /**
     * �õ�����
     * @return int
     */
    public int getTakeDays() {
        return takeDays;
    }

    /**
     * �õ��ܼ�
     * @return double
     */
    public double getTotAmt() {
        return totAmt;
    }

    /**
     * �õ���������
     * @return Timestamp
     */
    public Timestamp getOptDate() {
        return optDate;
    }

    /**
     * �õ������ն�
     * @return String
     */
    public String getOptTerm() {
        return optTerm;
    }

    /**
     * �õ�������Ա
     * @return String
     */
    public String getOptUser() {
        return optUser;
    }

    /**
     * �õ��ʵ���
     * @return String
     */
    public String getBillNo() {
        return billNo;
    }

    /**
     * �õ�ҽ�����
     * @return String
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * �õ�ҽ��˳���
     * @return String
     */
    public String getOrderSeq() {
        return orderSeq;
    }

    /**
     * �õ�ҽ������
     * @return String
     */
    public String getOrderDescIBS() {
        return orderDescIBS;
    }

    /**
     * �õ��ɱ�����
     * @return String
     */
    public String getCostCenterCode() {
        return costCenterCode;
    }

    /**
     * �õ��ٴ�·��
     * @return String
     */
    public String getSchdCode() {
        return schdCode;
    }

    /**
     * �õ�ʱ��
     * @return String
     */
    public String getClncpathCode() {
        return clncpathCode;
    }
    /**
     * �õ���Ժ��ҩע��
     * @return String
     */
    public String getDsFlg() {
        return dsFlg;
    }

    /**
     * ����ִ����ʼʱ��
     * @param beginDate Timestamp
     */
    public void setBeginDate(Timestamp beginDate) {
        this.beginDate = beginDate;
    }

    /**
     * ���ü�������
     * @param billDate Timestamp
     */
    public void setBillDate(Timestamp billDate) {
        this.billDate = billDate;
    }

    /**
     * ����ִ������
     * @param billDate Timestamp
     */
    public void setExecDate(Timestamp execDate) {
        this.execDate = execDate;
    }

    /**
     * �����շ�ע��
     * @param billFlg String
     */
    public void setBillFlg(String billFlg) {
        this.billFlg = billFlg;
    }

    /**
     * �����������
     * @param caseNoSeq int
     */
    public void setCaseNoSeq(int caseNoSeq) {
        this.caseNoSeq = caseNoSeq;
    }

    /**
     * ����ҽ��ϸ����Ⱥ��
     * @param cat1Type String
     */
    public void setCat1Type(String cat1Type) {
        this.cat1Type = cat1Type;
    }

    /**
     * ������Ч����
     * @param endDate Timestamp
     */
    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    /**
     * ����ִ�п���
     * @param exeDeptCode String
     */
    public void setExeDeptCode(String exeDeptCode) {
        this.exeDeptCode = exeDeptCode;
    }

    /**
     * ���ÿ�������
     * @param deptCode String
     */
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    /**
     * ������ҩ����
     * @param dosageQty double
     */
    public void setDosageQty(double dosageQty) {
        this.dosageQty = dosageQty;
    }

    /**
     * ������ҩ��λ
     * @param dosageUnit String
     */
    public void setDosageUnit(String dosageUnit) {
        this.dosageUnit = dosageUnit;
    }

    /**
     * ���ü��ʹ���
     * @param doseCode String
     */
    public void setDoseCode(String doseCode) {
        this.doseCode = doseCode;
    }

    /**
     * ���ÿ���ҽʦ
     * @param drCode String
     */
    public void setDrCode(String drCode) {
        this.drCode = drCode;
    }

    /**
     * ����ִ�в���
     * @param exeStationCode String
     */
    public void setExeStationCode(String exeStationCode) {
        this.exeStationCode = exeStationCode;
    }

    /**
     * ����ִ��ҽʦ
     * @param exeDrCode String
     */
    public void setExeDrCode(String exeDrCode) {
        this.exeDrCode = exeDrCode;
    }

    /**
     * ����Ƶ�δ���
     * @param freqCode String
     */
    public void setFreqCode(String freqCode) {
        this.freqCode = freqCode;
    }

    /**
     * ����Ժ�ڷ��ô���
     * @param hexpCode String
     */
    public void setHexpCode(String hexpCode) {
        this.hexpCode = hexpCode;
    }

    /**
     * ���ü���ҽ��ϸ������ע��
     * @param indvFlg String
     */
    public void setIndvFlg(String indvFlg) {
        this.indvFlg = indvFlg;
    }

    /**
     * ���������������
     * @param invCode String
     */
    public void setInvCode(String invCode) {
        this.invCode = invCode;
    }

    /**
     * ���ÿ�ҩ����
     * @param mediQty double
     */
    public void setMediQty(double mediQty) {
        this.mediQty = mediQty;
    }

    /**
     * ���ÿ�ҩ��λ
     * @param mediUnit String
     */
    public void setMediUnit(String mediUnit) {
        this.mediUnit = mediUnit;
    }

    /**
     * ����ҽ������
     * @param nhiPrice double
     */
    public void setNhiPrice(double nhiPrice) {
        this.nhiPrice = nhiPrice;
    }

    /**
     * ����ҽ��ϸ����
     * @param orderCat1Code String
     */
    public void setOrderCat1Code(String orderCat1Code) {
        this.orderCat1Code = orderCat1Code;
    }

    /**
     * ����ҽ������
     * @param orderCode String
     */
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    /**
     * ���ü���ҽ��
     * @param ordersetCode String
     */
    public void setOrdersetCode(String ordersetCode) {
        this.ordersetCode = ordersetCode;
    }

    /**
     * ���ü���ҽ��Ⱥ���
     * @param ordersetGroupNo String
     */
    public void setOrdersetGroupNo(String ordersetGroupNo) {
        this.ordersetGroupNo = ordersetGroupNo;
    }

    /**
     * �����Է��ܼ�
     * @param ownAmt double
     */
    public void setOwnAmt(double ownAmt) {
        this.ownAmt = ownAmt;
    }

    /**
     * �����Էѱ��
     * @param ownFlg String
     */
    public void setOwnFlg(String ownFlg) {
        this.ownFlg = ownFlg;
    }

    /**
     * �����Էѵ���
     * @param ownPrice double
     */
    public void setOwnPrice(double ownPrice) {
        this.ownPrice = ownPrice;
    }

    /**
     * ����֧������
     * @param ownRate double
     */
    public void setOwnRate(double ownRate) {
        this.ownRate = ownRate;
    }

    /**
     * ��������ע��
     * @param requestFlg String
     */
    public void setRequestFlg(String requestFlg) {
        this.requestFlg = requestFlg;
    }

    /**
     * �������쵥��
     * @param requestNo String
     */
    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    /**
     * �����վݴ���
     * @param rexpCode String
     */
    public void setRexpCode(String rexpCode) {
        this.rexpCode = rexpCode;
    }

    /**
     * �������������
     * @param seqNo String
     */
    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    /**
     * ���ÿ�������
     * @param stationCode String
     */
    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    /**
     * ��������
     * @param takeDays int
     */
    public void setTakeDays(int takeDays) {
        this.takeDays = takeDays;
    }

    /**
     * �����ܼ�
     * @param totAmt double
     */
    public void setTotAmt(double totAmt) {
        this.totAmt = totAmt;
    }

    /**
     * ���ò�������
     * @param optDate Timestamp
     */
    public void setOptDate(Timestamp optDate) {
        this.optDate = optDate;
    }

    /**
     * ���ò����ն�
     * @param optTerm String
     */
    public void setOptTerm(String optTerm) {
        this.optTerm = optTerm;
    }

    /**
     * ���ò�����Ա
     * @param optUser String
     */
    public void setOptUser(String optUser) {
        this.optUser = optUser;
    }

    /**
     * �����ʵ���
     * @param billNo String
     */
    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    /**
     * ����ҽ�����
     * @param orderNo String
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * ����ҽ��˳���
     * @param orderSeq String
     */
    public void setOrderSeq(String orderSeq) {
        this.orderSeq = orderSeq;
    }

    /**
     * ����ҽ������
     * @param orderDescIBS String
     */
    public void setOrderDescIBS(String orderDescIBS) {
        this.orderDescIBS = orderDescIBS;
    }

    /**
     * ���óɱ�����
     * @param costCenterCode String
     */
    public void setCostCenterCode(String costCenterCode) {
        this.costCenterCode = costCenterCode;
    }

    /**
     * �����ٴ�·��
     * @param schdCode String
     */
    public void setSchdCode(String schdCode) {
        this.schdCode = schdCode;
    }

    /**
     * ����ʱ��
     * @param clncpathCode String
     */
    public void setClncpathCode(String clncpathCode) {
        this.clncpathCode = clncpathCode;
    }
    /**
     * ���ó�Ժ��ҩע��
     * @param dsFlg String
     */
    public void setDsFlg(String dsFlg) {
        this.dsFlg = dsFlg;
    }


    /**
     * �õ�SQL
     * @return String
     */
    protected String getQuerySQL() {
        return "SELECT * FROM IBS_ORDD WHERE CASE_NO = '' ";
    }

    /**
     * ���Ǹ��ำֵ����
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */
	public boolean setItem(int row, String column, Object value) {
		super.setItem(row, column, value);
		double ownRate = getItemDouble(row, "OWN_RATE");
		// ����
		if (column.equals("DOSAGE_QTY")) {
			// System.out.println("�ı�����" + value);
			double dosageQty = TypeTool.getDouble(value);
			double ownPrice = getItemDouble(row, "OWN_PRICE");
			// �������ۿ�--xiongwg20150401 start
			if (ownRate != 0.00) {
				setItem(row, "TOT_AMT", dosageQty * ownPrice * ownRate);
			} else {
				setItem(row, "TOT_AMT", dosageQty * ownPrice);
			}
			// �������ۿ�--xiongwg20150401 end
			setItem(row, "OWN_AMT", dosageQty * ownPrice);
			String orderSetCode = this.getItemString(row, "ORDERSET_CODE");
			// System.out.println("����ҽ���������"+orderSetCode);
			// �ж��Ƿ��Ǽ���ҽ������ �����������ô���������������
			
			if (orderSetCode != null
					&& getItemString(row, "ORDER_CODE").equals(orderSetCode)) {
				double totAmt=0.00;
				String orderCode = this.getItemString(row, "ORDERSET_CODE");
				setItem(row, "OWN_AMT", 0.00);
				TParm parm = this.getRowParm(row);
				// System.out.println("��������"+parm);
				// ѭ���������������
				for (int k = 0; k < this.rowCountFilter(); k++) {
					// System.out.println("����ҽ��ϸ�����"+this.
					// getItemData(k, "ORDER_CODE", this.FILTER));
					// �ж��Ƿ��Ǹü���ҽ��������
					if (orderCode.equals(TypeTool.getString(this.getItemData(k,
							"ORDERSET_CODE", this.FILTER)))
							&& (!this.getItemData(k, "ORDER_CODE", this.FILTER)
									.equals(orderCode))) {
						// System.out.println("�Ǽ���ҽ��ϸ��");
						// ϸ���Ĭ������
						double osTot = this.getOrderSetTot(
								TypeTool.getString(this.getItemData(k,
										"ORDER_CODE", this.FILTER)),
								TypeTool.getString(this.getItemData(k,
										"ORDERSET_CODE", this.FILTER)))
								* parm.getDouble("DOSAGE_QTY");
						// System.out.println("Ĭ������"+osTot);
						this.setItem(k, "DOSAGE_QTY", osTot, this.FILTER);

						// �������ۿ�--xiongwg20150401 start
						if (TypeTool.getDouble(this.getItemData(k,
								"OWN_RATE", this.FILTER)) != 0.00) {
							this.setItem(k, "TOT_AMT", osTot
									* TypeTool.getDouble(this.getItemData(k,
											"OWN_PRICE", this.FILTER))
									* TypeTool.getDouble(this.getItemData(k,
											"OWN_RATE", this.FILTER)), this.FILTER);
							totAmt+= osTot
							* TypeTool.getDouble(this.getItemData(k,
									"OWN_PRICE", this.FILTER))
							* TypeTool.getDouble(this.getItemData(k,
									"OWN_RATE", this.FILTER));
						} else {
							this.setItem(k, "TOT_AMT", osTot
									* TypeTool.getDouble(this.getItemData(k,
											"OWN_PRICE", this.FILTER)),
									this.FILTER);
							totAmt+=osTot* TypeTool.getDouble(this.getItemData(k,
									"OWN_PRICE", this.FILTER));
						}
						// �������ۿ�--xiongwg20150401 end
						this
								.setItem(k, "OWN_AMT", osTot
										* TypeTool.getDouble(this.getItemData(
												k, "OWN_PRICE", this.FILTER)),
										this.FILTER);
					}
				}
			}
			
		}

		// ����
		if (column.equals("MEDI_QTY")) {
			double mediqty = TypeTool.getDouble(value);
			int takeDays = getItemInt(row, "TAKE_DAYS");
			String mediUnit = getItemString(row, "MEDI_UNIT");
			String freqCode = getItemString(row, "FREQ_CODE");
			TParm parm = TotQtyTool.getIbsQty(mediqty, mediUnit, freqCode,
					takeDays);
			setItem(row, "DOSAGE_QTY", parm.getDouble("DOSAGE_QTY"));

			double dosageQty = parm.getDouble("DOSAGE_QTY");
			double ownPrice = getItemDouble(row, "OWN_PRICE");
			// �������ۿ�--xiongwg20150401 start
			if (ownRate != 0.00) {
				setItem(row, "TOT_AMT", dosageQty * ownPrice * ownRate);
			} else {
				setItem(row, "TOT_AMT", dosageQty * ownPrice);
			}
			// �������ۿ�--xiongwg20150401 end
			setItem(row, "OWN_AMT", dosageQty * ownPrice);
			String orderSetCode = this.getItemString(row, "ORDERSET_CODE");
			// �ж��Ƿ��Ǽ���ҽ������ �����������ô���������������
			if (orderSetCode != null
					&& getItemString(row, "ORDER_CODE")
							.equals(orderSetCode)) {
				String orderCode = this.getItemString(row, "ORDERSET_CODE");
				setItem(row, "OWN_AMT", 0.00);
				// ѭ���������������
				for (int k = 0; k < this.rowCountFilter(); k++) {
					// �ж��Ƿ��Ǹü���ҽ��������
					if (orderCode.equals(TypeTool.getString(this
							.getItemData(k, "ORDERSET_CODE", this.FILTER)))
							&& (!this.getItemData(k, "ORDER_CODE",
									this.FILTER).equals(orderCode))) {
						double osTot = 0.00;
						osTot = this.getOrderSetTot(TypeTool
								.getString(this.getItemData(k,
										"ORDER_CODE", this.FILTER)),
								TypeTool.getString(this.getItemData(k,
										"ORDERSET_CODE", this.FILTER)))
								* dosageQty;
						// ϸ���Ĭ������
						this.setItem(k, "MEDI_QTY", mediqty, this.FILTER);
						this.setItem(k, "DOSAGE_QTY", osTot, this.FILTER);
						// �������ۿ�--xiongwg20150401 start
						if (ownRate != 0.00) {
							this.setItem(k, "TOT_AMT", osTot
									* TypeTool.getDouble(this.getItemData(
											k, "OWN_PRICE", this.FILTER))
									* ownRate, this.FILTER);
						} else {
							this.setItem(k, "TOT_AMT", osTot
									* TypeTool.getDouble(this.getItemData(
											k, "OWN_PRICE", this.FILTER)),
									this.FILTER);
						}
						// �������ۿ�--xiongwg20150401 end
						this.setItem(k, "OWN_AMT", osTot
								* TypeTool.getDouble(this.getItemData(k,
										"OWN_PRICE", this.FILTER)),
								this.FILTER);
					}
				}
			}

		}
		// ����
		if (column.equals("TAKE_DAYS")) {
			int takeDays = TypeTool.getInt(value);
			double mediqty = getItemDouble(row, "MEDI_QTY");
			String mediUnit = getItemString(row, "MEDI_UNIT");
			String freqCode = getItemString(row, "FREQ_CODE");
			//setItem(row, "DOSAGE_QTY", mediqty * takeDays);
			TParm parm = TotQtyTool.getIbsQty(mediqty, mediUnit, freqCode,
					takeDays);
			setItem(row, "DOSAGE_QTY", parm.getDouble("DOSAGE_QTY"));
			double dosageQty = mediqty * takeDays;
			double ownPrice = getItemDouble(row, "OWN_PRICE");
			setItem(row, "OWN_AMT", dosageQty * ownPrice);
			// �������ۿ�--xiongwg20150401 start
			if (ownRate != 0.00) {
				setItem(row, "TOT_AMT", dosageQty * ownPrice * ownRate);
			} else {
				setItem(row, "TOT_AMT", dosageQty * ownPrice);
			}
			// �������ۿ�--xiongwg20150401 end
			String orderSetCode = this.getItemString(row, "ORDERSET_CODE");
			// �ж��Ƿ��Ǽ���ҽ������ �����������ô���������������
			if (orderSetCode != null
					&& getItemString(row, "ORDER_CODE")
							.equals(orderSetCode)) {
				String orderCode = this.getItemString(row, "ORDERSET_CODE");
				setItem(row, "OWN_AMT", 0.00);
				// ѭ���������������
				for (int k = 0; k < this.rowCountFilter(); k++) {
					// �ж��Ƿ��Ǹü���ҽ��������
					if (orderCode.equals(TypeTool.getString(this
							.getItemData(k, "ORDERSET_CODE", this.FILTER)))
							&& (!this.getItemData(k, "ORDER_CODE",
									this.FILTER).equals(orderCode))) {
						double osTot = this.getOrderSetTot(TypeTool
								.getString(this.getItemData(k,
										"ORDER_CODE", this.FILTER)),
								TypeTool.getString(this.getItemData(k,
										"ORDERSET_CODE", this.FILTER)))
								* dosageQty;
						// ϸ���Ĭ������

						this.setItem(k, "TAKE_DAYS", takeDays, this.FILTER);
						this.setItem(k, "DOSAGE_QTY", osTot, this.FILTER);
						// �������ۿ�--xiongwg20150401 start
						if (ownRate != 0.00) {
							this.setItem(k, "TOT_AMT", osTot
									* TypeTool.getDouble(this.getItemData(
											k, "OWN_PRICE", this.FILTER))
									* ownRate, this.FILTER);
						} else {
							this.setItem(k, "TOT_AMT", osTot
									* TypeTool.getDouble(this.getItemData(
											k, "OWN_PRICE", this.FILTER)),
									this.FILTER);
						}
						// �������ۿ�--xiongwg20150401 end
						this.setItem(k, "OWN_AMT", osTot
								* TypeTool.getDouble(this.getItemData(k,
										"OWN_PRICE", this.FILTER)),
								this.FILTER);
					}
				}
			}

		}
		// Ƶ��
		if (column.equals("FREQ_CODE")) {
			String freqCode = TypeTool.getString(value);
			double mediqty = getItemDouble(row, "MEDI_QTY");
			String mediUnit = getItemString(row, "MEDI_UNIT");
			int takeDays = getItemInt(row, "TAKE_DAYS");
			TParm parm = TotQtyTool.getIbsQty(mediqty, mediUnit, freqCode,
					takeDays);
			setItem(row, "DOSAGE_QTY", parm.getDouble("DOSAGE_QTY"));
			double dosageQty = parm.getDouble("DOSAGE_QTY");
			String orderSetCode = this.getItemString(row, "ORDERSET_CODE");
			double ownPrice = getItemDouble(row, "OWN_PRICE");
			setItem(row, "OWN_AMT", dosageQty * ownPrice);
			// �ж��Ƿ��Ǽ���ҽ������ �����������ô���������������
			if (orderSetCode != null
					&& getItemString(row, "ORDER_CODE")
							.equals(orderSetCode)) {
				String orderCode = this.getItemString(row, "ORDERSET_CODE");
				//String groupNoSeq=this.getItemString(row, "ORDERSET_GROUP_NO");
				setItem(row, "OWN_AMT", 0.00);
				// ѭ���������������
				for (int k = 0; k < this.rowCountFilter(); k++) {
					// �ж��Ƿ��Ǹü���ҽ��������
					if (orderCode.equals(TypeTool.getString(this
							.getItemData(k, "ORDERSET_CODE", this.FILTER)))
							&& (!this.getItemData(k, "ORDER_CODE",
									this.FILTER).equals(orderCode))) {
						double osTot = 0.00;
						// ϸ���Ĭ������
						osTot = this.getOrderSetTot(TypeTool
								.getString(this.getItemData(k,
										"ORDER_CODE", this.FILTER)),
								TypeTool.getString(this.getItemData(k,
										"ORDERSET_CODE", this.FILTER)))
								* dosageQty;
						this.setItem(k, "FREQ_CODE", freqCode, this.FILTER);
						this.setItem(k, "DOSAGE_QTY", osTot, this.FILTER);
						this.setItem(k, "TOT_AMT", osTot
								* TypeTool.getDouble(this.getItemData(k,
										"OWN_PRICE", this.FILTER)),
								this.FILTER);
						this.setItem(k, "OWN_AMT", osTot
								* TypeTool.getDouble(this.getItemData(k,
										"OWN_PRICE", this.FILTER)),
								this.FILTER);
					}
				}
			}

		}
	
		return true;
	}
	/**
     * ���Ǹ��ำֵ����===�˷�ʹ��
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */
	public boolean setItemRe(int row, String column, Object value) {
		super.setItem(row, column, value);
		double ownRate = getItemDouble(row, "OWN_RATE");
		// ����
		if (column.equals("DOSAGE_QTY")) {
			// System.out.println("�ı�����" + value);
			double dosageQty = TypeTool.getDouble(value);
			double ownPrice = getItemDouble(row, "OWN_PRICE");
			// �������ۿ�--xiongwg20150401 start
			if (ownRate != 0.00) {
				setItemRe(row, "TOT_AMT", dosageQty * ownPrice * ownRate);
			} else {
				setItemRe(row, "TOT_AMT", dosageQty * ownPrice);
			}
			// �������ۿ�--xiongwg20150401 end
			setItemRe(row, "OWN_AMT", dosageQty * ownPrice);
			String orderSetCode = this.getItemString(row, "ORDERSET_CODE");
			// System.out.println("����ҽ���������"+orderSetCode);
			// �ж��Ƿ��Ǽ���ҽ������ �����������ô���������������
			
			if (orderSetCode != null
					&& getItemString(row, "ORDER_CODE").equals(orderSetCode)) {
				double totAmt=0.00;
				String orderCode = this.getItemString(row, "ORDERSET_CODE");
				String groupNoSeq=this.getItemString(row, "ORDERSET_GROUP_NO");
				setItemRe(row, "OWN_AMT", 0.00);
				TParm parm = this.getRowParm(row);
				// System.out.println("��������"+parm);
				// ѭ���������������
				for (int k = 0; k < this.rowCountFilter(); k++) {
					// System.out.println("����ҽ��ϸ�����"+this.
					// getItemData(k, "ORDER_CODE", this.FILTER));
					// �ж��Ƿ��Ǹü���ҽ��������
					if (orderCode.equals(TypeTool.getString(this.getItemData(k,
							"ORDERSET_CODE", this.FILTER)))
							&& (!this.getItemData(k, "ORDER_CODE", this.FILTER)
									.equals(orderCode))&&groupNoSeq.equals(TypeTool.getString(this.getItemData(k,
							"ORDERSET_GROUP_NO", this.FILTER)))) {
						// System.out.println("�Ǽ���ҽ��ϸ��");
						// ϸ���Ĭ������
						// ��Ӽ����˷�У�齻�ױ�����ѯ����ҽ���ֵ��
						double osTot = parm.getDouble("DOSAGE_QTY")
						* (TypeTool.getDouble(this.getItemData(k,
								"DOSAGE_QTY", this.FILTER)));
						this.setItem(k, "MEDI_QTY", osTot, this.FILTER);
						// System.out.println("Ĭ������"+osTot);
						this.setItem(k, "DOSAGE_QTY", osTot, this.FILTER);

						// �������ۿ�--xiongwg20150401 start
						if (TypeTool.getDouble(this.getItemData(k,
								"OWN_RATE", this.FILTER)) != 0.00) {
							this.setItem(k, "TOT_AMT", osTot
									* TypeTool.getDouble(this.getItemData(k,
											"OWN_PRICE", this.FILTER))
									* TypeTool.getDouble(this.getItemData(k,
											"OWN_RATE", this.FILTER)), this.FILTER);
							totAmt+= osTot
							* TypeTool.getDouble(this.getItemData(k,
									"OWN_PRICE", this.FILTER))
							* TypeTool.getDouble(this.getItemData(k,
									"OWN_RATE", this.FILTER));
						} else {
							this.setItem(k, "TOT_AMT", osTot
									* TypeTool.getDouble(this.getItemData(k,
											"OWN_PRICE", this.FILTER)),
									this.FILTER);
							totAmt+=osTot* TypeTool.getDouble(this.getItemData(k,
									"OWN_PRICE", this.FILTER));
						}
						// �������ۿ�--xiongwg20150401 end
						this
								.setItem(k, "OWN_AMT", osTot
										* TypeTool.getDouble(this.getItemData(
												k, "OWN_PRICE", this.FILTER)),
										this.FILTER);
					}
				}
				setItemRe(row, "TOT_AMT", totAmt);
			}
			
		}

		// ����
//		if (column.equals("MEDI_QTY")) {
//			double mediqty = TypeTool.getDouble(value);
//			int takeDays = getItemInt(row, "TAKE_DAYS");
//			String mediUnit = getItemString(row, "MEDI_UNIT");
//			String freqCode = getItemString(row, "FREQ_CODE");
//			TParm parm = TotQtyTool.getIbsQty(mediqty, mediUnit, freqCode,
//					takeDays);
//			setItem(row, "DOSAGE_QTY", parm.getDouble("DOSAGE_QTY"));
//
//			double dosageQty = parm.getDouble("DOSAGE_QTY");
//			double ownPrice = getItemDouble(row, "OWN_PRICE");
//			// �������ۿ�--xiongwg20150401 start
//			if (ownRate != 0.00) {
//				setItem(row, "TOT_AMT", dosageQty * ownPrice * ownRate);
//			} else {
//				setItem(row, "TOT_AMT", dosageQty * ownPrice);
//			}
//			// �������ۿ�--xiongwg20150401 end
//			setItem(row, "OWN_AMT", dosageQty * ownPrice);
//			String orderSetCode = this.getItemString(row, "ORDERSET_CODE");
//			// �ж��Ƿ��Ǽ���ҽ������ �����������ô���������������
//			if (orderSetCode != null
//					&& getItemString(row, "ORDER_CODE")
//							.equals(orderSetCode)) {
//				String orderCode = this.getItemString(row, "ORDERSET_CODE");
//				setItem(row, "OWN_AMT", 0.00);
//				// ѭ���������������
//				for (int k = 0; k < this.rowCountFilter(); k++) {
//					// �ж��Ƿ��Ǹü���ҽ��������
//					if (orderCode.equals(TypeTool.getString(this
//							.getItemData(k, "ORDERSET_CODE", this.FILTER)))
//							&& (!this.getItemData(k, "ORDER_CODE",
//									this.FILTER).equals(orderCode))) {
//						// ��Ӽ����˷�У�齻�ױ�����ѯ����ҽ���ֵ��
//						double osTot = parm.getDouble("DOSAGE_QTY")
//						* (TypeTool.getDouble(this.getItemData(
//								k, "DOSAGE_QTY", this.FILTER)));
//						// ϸ���Ĭ������
//						this.setItem(k, "MEDI_QTY", mediqty, this.FILTER);
//						this.setItem(k, "DOSAGE_QTY", osTot, this.FILTER);
//						// �������ۿ�--xiongwg20150401 start
//						if (ownRate != 0.00) {
//							this.setItem(k, "TOT_AMT", osTot
//									* TypeTool.getDouble(this.getItemData(
//											k, "OWN_PRICE", this.FILTER))
//									* ownRate, this.FILTER);
//						} else {
//							this.setItem(k, "TOT_AMT", osTot
//									* TypeTool.getDouble(this.getItemData(
//											k, "OWN_PRICE", this.FILTER)),
//									this.FILTER);
//						}
//						// �������ۿ�--xiongwg20150401 end
//						this.setItem(k, "OWN_AMT", osTot
//								* TypeTool.getDouble(this.getItemData(k,
//										"OWN_PRICE", this.FILTER)),
//								this.FILTER);
//					}
//				}
//			}
//
//		}
		// ����
//		if (column.equals("TAKE_DAYS")) {
//			int takeDays = TypeTool.getInt(value);
//			double mediqty = getItemDouble(row, "MEDI_QTY");
//			String mediUnit = getItemString(row, "MEDI_UNIT");
//			String freqCode = getItemString(row, "FREQ_CODE");
//			//setItem(row, "DOSAGE_QTY", mediqty * takeDays);
//			TParm parm = TotQtyTool.getIbsQty(mediqty, mediUnit, freqCode,
//					takeDays);
//			//setItem(row, "DOSAGE_QTY", parm.getDouble("DOSAGE_QTY"));
//			double dosageQty = mediqty * takeDays;
//			double ownPrice = getItemDouble(row, "OWN_PRICE");
//			setItem(row, "OWN_AMT", dosageQty * ownPrice);
//			// �������ۿ�--xiongwg20150401 start
//			if (ownRate != 0.00) {
//				setItem(row, "TOT_AMT", dosageQty * ownPrice * ownRate);
//			} else {
//				setItem(row, "TOT_AMT", dosageQty * ownPrice);
//			}
//			// �������ۿ�--xiongwg20150401 end
//			String orderSetCode = this.getItemString(row, "ORDERSET_CODE");
//			// �ж��Ƿ��Ǽ���ҽ������ �����������ô���������������
//			if (orderSetCode != null
//					&& getItemString(row, "ORDER_CODE")
//							.equals(orderSetCode)) {
//				String orderCode = this.getItemString(row, "ORDERSET_CODE");
//				String groupNoSeq=this.getItemString(row, "ORDERSET_GROUP_NO");
//				setItem(row, "OWN_AMT", 0.00);
//				// ѭ���������������
//				for (int k = 0; k < this.rowCountFilter(); k++) {
//					// �ж��Ƿ��Ǹü���ҽ��������
//					if (orderCode.equals(TypeTool.getString(this
//							.getItemData(k, "ORDERSET_CODE", this.FILTER)))
//							&& (!this.getItemData(k, "ORDER_CODE",
//									this.FILTER).equals(orderCode))&&groupNoSeq.equals(TypeTool.getString(this.getItemData(k,
//											"ORDERSET_GROUP_NO", this.FILTER)))) {
//						// ��Ӽ����˷�У�齻�ױ�����ѯ����ҽ���ֵ��
//						double osTot = parm.getDouble("DOSAGE_QTY")
//						* (TypeTool.getDouble(this.getItemData(
//								k, "DOSAGE_QTY", this.FILTER)));
//						// ϸ���Ĭ������
//
//						this.setItem(k, "TAKE_DAYS", takeDays, this.FILTER);
//						this.setItem(k, "DOSAGE_QTY", osTot, this.FILTER);
//						// �������ۿ�--xiongwg20150401 start
//						if (ownRate != 0.00) {
//							this.setItem(k, "TOT_AMT", osTot
//									* TypeTool.getDouble(this.getItemData(
//											k, "OWN_PRICE", this.FILTER))
//									* ownRate, this.FILTER);
//						} else {
//							this.setItem(k, "TOT_AMT", osTot
//									* TypeTool.getDouble(this.getItemData(
//											k, "OWN_PRICE", this.FILTER)),
//									this.FILTER);
//						}
//						// �������ۿ�--xiongwg20150401 end
//						this.setItem(k, "OWN_AMT", osTot
//								* TypeTool.getDouble(this.getItemData(k,
//										"OWN_PRICE", this.FILTER)),
//								this.FILTER);
//					}
//				}
//			}
//
//		}
		// Ƶ��
//		if (column.equals("FREQ_CODE")) {
//			String freqCode = TypeTool.getString(value);
//			double mediqty = getItemDouble(row, "MEDI_QTY");
//			String mediUnit = getItemString(row, "MEDI_UNIT");
//			int takeDays = getItemInt(row, "TAKE_DAYS");
//			TParm parm = TotQtyTool.getIbsQty(mediqty, mediUnit, freqCode,
//					takeDays);
//			setItem(row, "DOSAGE_QTY", parm.getDouble("DOSAGE_QTY"));
//			double dosageQty = parm.getDouble("DOSAGE_QTY");
//			String orderSetCode = this.getItemString(row, "ORDERSET_CODE");
//			double ownPrice = getItemDouble(row, "OWN_PRICE");
//			setItem(row, "OWN_AMT", dosageQty * ownPrice);
//			// �ж��Ƿ��Ǽ���ҽ������ �����������ô���������������
//			if (orderSetCode != null
//					&& getItemString(row, "ORDER_CODE")
//							.equals(orderSetCode)) {
//				String orderCode = this.getItemString(row, "ORDERSET_CODE");
//				String groupNoSeq=this.getItemString(row, "ORDERSET_GROUP_NO");
//				setItem(row, "OWN_AMT", 0.00);
//				// ѭ���������������
//				for (int k = 0; k < this.rowCountFilter(); k++) {
//					// �ж��Ƿ��Ǹü���ҽ��������
//					if (orderCode.equals(TypeTool.getString(this
//							.getItemData(k, "ORDERSET_CODE", this.FILTER)))
//							&& (!this.getItemData(k, "ORDER_CODE",
//									this.FILTER).equals(orderCode))&&groupNoSeq.equals(TypeTool.getString(this.getItemData(k,
//											"ORDERSET_GROUP_NO", this.FILTER)))) {
//						double osTot =  parm.getDouble("DOSAGE_QTY")// ��Ӽ����˷�У�齻�ױ�����ѯ����ҽ���ֵ��
//						* (TypeTool.getDouble(this.getItemData(
//								k, "DOSAGE_QTY", this.FILTER)));
//						this.setItem(k, "FREQ_CODE", freqCode, this.FILTER);
//						this.setItem(k, "DOSAGE_QTY", osTot, this.FILTER);
//						this.setItem(k, "TOT_AMT", osTot
//								* TypeTool.getDouble(this.getItemData(k,
//										"OWN_PRICE", this.FILTER)),
//								this.FILTER);
//						this.setItem(k, "OWN_AMT", osTot
//								* TypeTool.getDouble(this.getItemData(k,
//										"OWN_PRICE", this.FILTER)),
//								this.FILTER);
//					}
//				}
//			}
//
//		}
	
		return true;
	}

    /**
     * ��ȡ����ҽ��ϸ���Ĭ������
     * @param order_code String ϸ��CODE
     * @param orderset_code String  ����CODE
     * @return double
     */
    public double getOrderSetTot(String order_code, String orderset_code) {
        String sql =
                "SELECT DOSAGE_QTY FROM SYS_ORDERSETDETAIL WHERE ORDERSET_CODE='" +
                orderset_code + "' AND ORDER_CODE = '" + order_code + "'";
//        System.out.println("סԺ����ҽ��sql" + sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result.getDouble("DOSAGE_QTY", 0);
    }

    /**
     * ��������
     * @param row int
     * @return int
     */
    public int insertRow(int row) {
        int newRow = super.insertRow(row);
        if (newRow == -1)
            return -1;
        //���þ������
        setItem(newRow, "CASE_NO", getCaseNo());
        //�˵����
        setItem(newRow, "CASE_NO_SEQ", this.getCaseNoSeq());
        //�˵������
        setItem(newRow, "SEQ_NO", this.getSeqNo());
        //��������
        setItem(newRow, "BILL_DATE", this.getBillDate());
        //ִ������
//        System.out.println("+++===+==vvvvv is ::"+ this.getExecDate());
        setItem(newRow, "EXEC_DATE", this.getExecDate());
        //ҽ�����
        setItem(newRow, "ORDER_NO", this.getOrderNo());
        //ҽ��˳���
        setItem(newRow, "ORDER_SEQ", this.getOrderSeq());
        //��Ч����
        setItem(newRow, "BEGIN_DATE", this.getBeginDate());
        //��Ч����
        setItem(newRow, "END_DATE", this.getEndDate());
        //��������
        setItem(newRow, "DEPT_CODE", this.getDeptCode());
        //��������
        setItem(newRow, "STATION_CODE", this.getStationCode());
        //����ҽʦ
        setItem(newRow, "DR_CODE", this.getDrCode());
//        System.out.println("�ɱ�����"+this.getCostCenterCode());
//        System.out.println("ִ�е�λ"+this.getExeDeptCode());
        //ִ�п���
        setItem(newRow, "EXE_DEPT_CODE", this.getCostCenterCode());
        //ִ�в���
        setItem(newRow, "EXE_STATION_CODE", this.getExeStationCode());
        //ִ��ҽʦ
        setItem(newRow, "EXE_DR_CODE", this.getExeDrCode());
        //������Ա
        setItem(newRow, "OPT_USER", this.getOptUser());
        //��������
        setItem(newRow, "OPT_DATE", this.getOptDate());
        //������Ա
        setItem(newRow, "OPT_TERM", this.getOptTerm());
        //ҽ������
        setItem(newRow, "ORDER_CHN_DESC", this.getOrderDescIBS());
        //�ɱ�����
        setItem(newRow, "COST_CENTER_CODE", this.getCostCenterCode());
        //�ٴ�·��
        setItem(newRow, "SCHD_CODE", this.getSchdCode());
        //ʱ��
        setItem(newRow, "CLNCPATH_CODE", this.getClncpathCode());
        //��Ժ��ҩע��
        setItem(newRow, "DS_FLG", this.getDsFlg());
        //�ײ�״̬ע��==pangben 2015-7-17
        setItem(newRow, "INCLUDE_FLG", this.getIncludeFlg());
        //�ײ�ִ��״̬ע��==pangben 2015-10-29
        setItem(newRow, "INCLUDE_EXEC_FLG", this.getIncludeExecFlg());
        return newRow;
    }

    /**
     * ��������������
     * @param parm TParm
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */
    public boolean setOtherColumnValue(TParm parm, int row, String column,
                                       Object value) {
        if ("ORDER_DESC".equalsIgnoreCase(column)) {
            setItem(row, "ORDER_CODE", value);
        }
        return true;
    }


    /**
     * �õ�����������
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TParm parm, int row, String column) {
        if ("ORDER_DESC".equalsIgnoreCase(column)) {
            if (parm.getValue("ORDER_CODE", row).length() == 0)
                return "";

            TParm sysFeeParm = sysFee.getBuffer(sysFee.isFilter() ?
                                                sysFee.FILTER :
                                                sysFee.PRIMARY);
            Vector code = (Vector) sysFeeParm.getData("ORDER_CODE");
            String orderCode = parm.getValue("ORDER_CODE", row);
            int rowNow = code.indexOf(orderCode);
            if (rowNow == -1)
                return "";
            if (sysFeeParm.getValue("SPECIFICATION", rowNow).toString().length() ==
                0 || sysFeeParm.getValue("SPECIFICATION", rowNow) == null) {
                //�ش�table��ʾ����(ҽ������)�����Ϊ��ʱ
                return sysFeeParm.getValue("ORDER_DESC", rowNow);
            }
            //�ش�table��ʾ����(ҽ������+���)
            return sysFeeParm.getValue("ORDER_DESC", rowNow) + "(" +
                    sysFeeParm.getValue("SPECIFICATION", rowNow) + ")";
        }
        return "";
    }
}
