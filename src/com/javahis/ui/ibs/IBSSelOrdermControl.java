package com.javahis.ui.ibs;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import jdo.adm.ADMInpTool;
import jdo.bil.BILComparator;
import jdo.bil.BILPayTool;
import jdo.ibs.IBSOrderdTool;
import jdo.ibs.IBSOrderdmTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComboNode;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.base.TComboBoxModel;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: ���ò�ѯ������
 * </p>
 * 
 * <p>
 * Description: ���ò�ѯ������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangl
 * @version 1.0
 */
public class IBSSelOrdermControl extends TControl {
	String mrNo;
	String caseNo;
	int caseNoFirst = 0;
	int maxSeq = -1; // ����˻���
	int minSeq = -1; // ��С�˻���
	TParm selDifferentParm; // ��ѯ��ͬ���˺�
	String user; // ��ǰ��¼�û�
	String dept;// ������
	private int sortColumn = -1;
	private boolean ascending = false;
	private BILComparator compare = new BILComparator();// modify by wanglong
														// 20120815 �����Ա���
	private TTable deptFeeTable;// ���ҷ��ò�ѯ��
	private TTable deptFeeSumTable;// modify by zhanglei
									// 20170620 �����Ա��� ��ӿ��ҷ��û���ҳǩ
	// =======add by wanglong begin 20120815 Ϊ������������������
	private TTable mergeTable;// �ϲ�������ϸ��
	private TTable feeDTable;// ������ϸ��

	// ==========add end========================================
	private TTabbedPane tTabbedPane_0;//====caowl
	
	//2016.9.23 ZL��ý���Ttable�ؼ�
	private TTable table;
	
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		
		super.onInit();
		this.tTabbedPane_0 = (TTabbedPane) this.getComponent("tTabbedPane_0");//====caowl
		
		callFunction("UI|print|setEnabled", false);//==caowl 20121018
		callFunction("UI|export|setEnabled", false);//-==caowl 20121018
		callFunction("UI|Refresh|setEnabled", false);//==caowl  20121018
		
		
		//callFunction("UI|PAT_NAME|setEnabled", false);
		// add begin wangzl 20120906
		String ipd="";
		TParm result = IBSOrderdTool.getInstance().initCostCenterCode();
		if (result != null && result.getCount() > 0) {
			ipd= result.getValue("IPD_FIT_FLG", 0);
		}
		// add end wangzl 20120906
		if (null==ipd||"".equals(ipd)||"N".equals(ipd)) { // ���fig ΪN
			// ��ʾ��ǰ��¼user
			// û�п��ҳɱ�����
			setValue("COST_CENTER_CODE", "");
		} else {
			setValue("COST_CENTER_CODE", result.getData("COST_CENTER_CODE", 0));
		}
		// ��� wangzl 20120806 stop
		
		// zhanglei 20170629 ��ӿ��ҷ��û���ҳǩ start
		this.deptFeeSumTable = (TTable) this.getComponent("deptFeeSumTable");
		addListener(deptFeeSumTable);
		// zhanglei 20170629 ��ӿ��ҷ��û���ҳǩ end
		
		this.deptFeeTable = (TTable) this.getComponent("deptFeeTable");
		addListener(deptFeeTable);
		// =======add by wanglong begin 20120815 ����������
		this.mergeTable = (TTable) this.getComponent("mergeFeeTable");
		addListener(mergeTable);// �������
		this.feeDTable = (TTable) this.getComponent("feeDetailTable");
		addListener(feeDTable); // �������
		// ==========add end===============================
		String aa = selInsICDCode("A30.501");
		//System.out.println("������ϴ���" + aa);
		// ============xueyf modify 20120307 start
		Timestamp time = SystemTool.getInstance().getDate();
		time.setHours(0);
		time.setMinutes(0);
		time.setSeconds(0);
		setValue("START_DATE", time);
		setValue("END_DATE", SystemTool.getInstance().getDate());
		//20170628 zhanglei ��ӿ��һ��ܽ���
		setValue("START_DATESUM", time);
		setValue("END_DATESUM", SystemTool.getInstance().getDate());
		
		//2016.9.23 ZL ��ʼ��������ϸ��ѯʱ��
		//��÷�����ϸ����Table�Ŀؼ�����
		table=(TTable)this.getComponent("feeDetailTable");
		// ��ʼ��ʱ��
        Timestamp date = SystemTool.getInstance().getDate();
        
        // ��ʼ����ѯ����
        this.setValue("END_DATE_FYMX",
                date.toString().substring(0, 10).replace('-', '/') +
                " 23:59:59");
        this.setValue("START_DATE_FYMX",
                date.toString().substring(0, 10).replace('-', '/') +
                " 00:00:00");
		
		// ============xueyf modify 20120307 stop
		TParm initParm = new TParm();
		Object obj = this.getParameter();
		if (obj == null)
			return;
		if (obj != null) {
			initParm = (TParm) obj;
		}
		caseNo = initParm.getData("IBS", "CASE_NO").toString();
		mrNo = initParm.getData("IBS", "MR_NO").toString();
		if (initParm.getData("IBS", "TYPE") != null) {
			callFunction("UI|close|setEnabled", false);
			String sysType = initParm.getData("IBS", "TYPE").toString();
			if ("ODISTATION".equals(sysType)) {
				callFunction("UI|showpat|setEnabled", false);
				callFunction("UI|bedcard|setEnabled", false);
			}

		}
		// ֻ��text���������������sys_fee������
		callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
				"%ROOT%\\config\\sys\\SYSFeePopup.x");
		// ���ܻش�ֵ
		callFunction("UI|ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		this.initPage();
			
	}

	
	private TTextField getTextField(String string) {
		// TODO Auto-generated method stub
		return (TTextField) this.getComponent(string);
	}

	/**
	 * ���ô��������б�ѡ��
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE"));
		this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
		onQuerySingleOrder();
		// this.grabFocus("ORDER_DESC");
	}
	/**
	 * ���
	 * */
	public void onClear(){
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		if(selectedIndex == 0){
			
			TTable table = (TTable) this.getComponent("rexpTable");
	        table.removeRowAll();
	        initPage();
		}
		if(selectedIndex == 1){
			
			TTable table = (TTable) this.getComponent("feeDetailTable");
	        table.removeRowAll();
	        table.setParmValue(new TParm());//===pangben 2014-4-10
	        table.removeRowAll();
	        table.setParmValue(new TParm());//===pangben 2014-4-10
	        //initPage();
		}
		if(selectedIndex == 2){
		
			TTable table = (TTable) this.getComponent("billDetailTable");
	        table.removeRowAll();
	        initPage();
		}
		if(selectedIndex == 3){
			this.clearValue("ORDER_CODE;ORDER_DESC");
			TTable table = (TTable) this.getComponent("singleOrderTable");
	        table.removeRowAll();
	        initPage();
		}
		if(selectedIndex == 4){
		    this.clearValue("C_BILL;C_UNBILL;C_RECP_TYPE;REXP_TYPE;C_UP_FEE;TOT_AMT;MERGE_TOT_FEE");
			TTable table = (TTable) this.getComponent("mergeFeeTable");
	        table.removeRowAll();
	        initPage();
		}
		
		if(selectedIndex == 5){	
			this.clearValue("COST_CENTER_CODE;ORDER_CAT1_CODE;CTRLDRUGCLASS_CODE;DEPT_TOT_FEE");				
			initPage();			
			TTable table =  (TTable) this.getComponent("deptFeeTable");			
			TParm parm = new TParm();
			table.setParmValue(parm);
			       
		}
		
		
		
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		
		
		//===modify by caowl 20121018 start
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		
		if(selectedIndex == 1){
			initPage();
		}
		if(selectedIndex == 2){
			//�Ƽ���ϸ			
			onQueryBillDetail();
		}
		if(selectedIndex == 4){
			//�ϲ�������ϸ			
			onQueryMergeFee();
		}
		if(selectedIndex == 5){
			//���ҷ��ò�ѯ				
			onQueryDeptFee();
		}	
		//=== modify by caowl 20121018 end
		//20170628 zhanglei ���ӿ��ҷ��û���ҳǩ
		if(selectedIndex == 6){
			//���ҷ��û���
			onQueryDeptFeeSum();
		}	
	}

	/**
	 * ��ʼ������
	 */

	public void initPage() {
		Timestamp today = SystemTool.getInstance().getDate();
		String sDateStr = StringTool.getString(today, "yyyyMMdd");
		String sTimeStr = "000000";
		String eTimeStr = StringTool.getString(today, "HHmmss");
		Timestamp sDate = StringTool.getTimestamp(sDateStr + sTimeStr,
				"yyyyMMddHHmmss");
		Timestamp eDate = StringTool.getTimestamp(sDateStr + eTimeStr,
				"yyyyMMddHHmmss");
		setValue("BILL_DATE", sDate);
		setValue("BILL_DATE_E", eDate);
		Timestamp time = SystemTool.getInstance().getDate();
		time.setHours(0);
		time.setMinutes(0);
		time.setSeconds(0);
		setValue("START_DATE", time);
		setValue("END_DATE", SystemTool.getInstance().getDate());
		//20170628 zhanglei ��ӿ��һ��ܽ���
		setValue("START_DATESUM", time);
		setValue("END_DATESUM", SystemTool.getInstance().getDate());
		// rexpTable��ѯ����
		TParm rexpParm = new TParm();
		rexpParm.setData("CASE_NO", caseNo);
		TParm rexpData = new TParm();
		rexpData = IBSOrderdTool.getInstance().selectdataAll(rexpParm);
		// System.out.println("rexpData�վ���Ŀ" + rexpData);
		double sunTotAmt = 0.00;
		double initTotAmt = 0.00;
		for (int i = 0; i < rexpData.getCount(); i++) {
			double totAmt = 0.00;
			totAmt = rexpData.getDouble("AR_AMT", i);
			sunTotAmt = sunTotAmt + totAmt;
			//����ԭ�ۺϼƽ��--xiongwg20150312 start
			double ownAmt = 0.00;
			ownAmt = rexpData.getDouble("OWN_AMT", i);
			initTotAmt = initTotAmt + ownAmt;
			//����ԭ�ۺϼƽ��--xiongwg20150312 end
		}
		// ��rexpTable���
		this.callFunction("UI|rexpTable|setParmValue", rexpData);
		// feeDetailTable��ѯ����
		String start = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE_FYMX")), "yyyyMMddHHmmss");
		String end = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE_FYMX")), "yyyyMMddHHmmss");
		TParm feeDetailParm = new TParm();
		feeDetailParm.setData("START_DATE_FYMX",start);
		feeDetailParm.setData("END_DATE_FYMX",end);
		feeDetailParm.setData("CASE_NO", caseNo);
		TParm feeDetailData = new TParm();
		feeDetailData = IBSOrderdTool.getInstance().selFeeDetail(feeDetailParm);
		TParm selADMdata = new TParm();
		selADMdata.setData("CASE_NO", caseNo);
		TParm admDataParm = ADMInpTool.getInstance().selectall(selADMdata);
		TParm bilPayParm = BILPayTool.getInstance().selBilPayLeft(caseNo);
		setValue("CUR_AMT", bilPayParm.getDouble("PRE_AMT", 0)); // �������
		setValue("TOTAL_BILPAY", admDataParm.getDouble("TOTAL_BILPAY", 0)); // Ԥ�����ܶ�
		setValue("TOTAL_AMT", sunTotAmt); // �����ܶ�
		setValue("INIT_TOTAL_AMT", initTotAmt); // ԭ�ۺϼƽ��
		setValue("NHI_AMT", ""); // ͳ��֧��
		setValue("OWN_AMT", sunTotAmt); // �Ը����
		setValue("Y_FEE", admDataParm.getDouble("TOTAL_BILPAY", 0) - sunTotAmt); // Ӧ�����
		// ��feeDetailTable���===pangben 2011-11-15
		TParm tableinparm = new TParm();
		// ɾ��ϸ���
		int count = 0;
		for (int i = 0; i < feeDetailData.getCount(); i++) {
			if (feeDetailData.getValue("INDV_FLG", i).length() <= 0
					|| "N".equals(feeDetailData.getValue("INDV_FLG", i))) { // ��������
				tableinparm.addRowData(feeDetailData, i);
				count++;
			}
		}
		this.callFunction("UI|PG_UP|setEnabled", false);
		this.callFunction("UI|PG_DOWN|setEnabled", false);
		tableinparm.setCount(count);
		tableinParm(feeDetailData, tableinparm);
		// ===pangben 2011-11-15 stop
		this.callFunction("UI|feeDetailTable|setParmValue", tableinparm);
		//������ϸ��ѯ 2016.10.13  zl �ٴ��޸� �޸�9.23������bug ��Ӳ�ѯ����
		//onQueryFYMX();
	}
	

	/**
	 * 2016.09.23 zl
	 * ��ѯ������ϸ
	 */
	public void onQueryFYMX(){
		initPage() ;
	}
	
	
			

	/**
	 * ��ѯ�Ƽ���ϸ
	 */
	public void onQueryBillDetail() {
		// billDetailTable��ѯ����
		TParm billDetailParm = new TParm();
		billDetailParm.setData("CASE_NO", caseNo);
		
		if (getValue("BILL_DATE") != null && getValue("BILL_DATE_E") != null) {
			Timestamp bill = (Timestamp) getValue("BILL_DATE");
			Timestamp billE = (Timestamp) getValue("BILL_DATE_E");
			String billDate = StringTool.getString(bill, "yyyyMMddHHmmss");
			String billDateE = StringTool.getString(billE, "yyyyMMddHHmmss");	
			
			billDetailParm.setData("BILL_DATE", billDate);
			billDetailParm.setData("BILL_DATE_E", billDateE);
		}
		// billDetailParm.setData("CASE_NO_SEQ", caseNoFirst);
		TParm billDetailData = new TParm();
		billDetailData = IBSOrderdTool.getInstance().selBillDetail(
				billDetailParm);
		
		if (billDetailData.getCount() <= 0) {

			this.messageBox("û�в�ѯ������");
			return;
		}

		// ��billDetailTable���

		this.callFunction("UI|billDetailTable|setParmValue", billDetailData);
		TParm selMaxSeq = new TParm();
		selMaxSeq.setData("CASE_NO", caseNo);
		if (getValue("BILL_DATE") != null && getValue("BILL_DATE_E") != null) {
			Timestamp bill = (Timestamp) getValue("BILL_DATE");
			Timestamp billE = (Timestamp) getValue("BILL_DATE_E");
			String billDate = StringTool.getString(bill, "yyyyMMddHHmmss");
			String billDateE = StringTool.getString(billE, "yyyyMMddHHmmss");
			billDetailParm.setData("BILL_DATE", billDate);
			billDetailParm.setData("BILL_DATE_E", billDateE);
			selMaxSeq.setData("BILL_DATE", billDate);
			selMaxSeq.setData("BILL_DATE_E", billDateE);
		}
		TParm selMaxSeqParm = IBSOrderdTool.getInstance().selMaxSeq(selMaxSeq);
		selDifferentParm = IBSOrderdTool.getInstance().selDifferentSeq(
				selMaxSeq); // ��ѯ��ͬ���ʺ�
		maxSeq = selMaxSeqParm.getInt("CASE_NO_SEQ", 0);
		minSeq = selMaxSeqParm.getInt("MIN_CASE_NO_SEQ", 0);
		caseNoFirst = 0;
		this.setValue("PG_NO", minSeq + "");
		if (minSeq >= maxSeq) {
			// this.messageBox("���һ��");
			this.callFunction("UI|PG_DOWN|setEnabled", false);
			// this.callFunction("UI|PG_UP|setEnabled", true);
			// return;
		} else {
			this.callFunction("UI|PG_DOWN|setEnabled", true);
		}
		this.callFunction("UI|PG_UP|setEnabled", false);

	}
	
	

	/**
	 * ��һ��
	 */
	public void pgDn() {
		// billDetailTable��ѯ����
		TParm billDetailParm = new TParm();
		billDetailParm.setData("CASE_NO", caseNo);
		TParm selMaxSeq = new TParm();
		selMaxSeq.setData("CASE_NO", caseNo);
		if (getValue("BILL_DATE") != null && getValue("BILL_DATE_E") != null) {
			Timestamp bill = (Timestamp) getValue("BILL_DATE");
			Timestamp billE = (Timestamp) getValue("BILL_DATE_E");
			String billDate = StringTool.getString(bill, "yyyyMMddHHmmss");
			String billDateE = StringTool.getString(billE, "yyyyMMddHHmmss");
			billDetailParm.setData("BILL_DATE", billDate);
			billDetailParm.setData("BILL_DATE_E", billDateE);
			selMaxSeq.setData("BILL_DATE", billDate);
			selMaxSeq.setData("BILL_DATE_E", billDateE);
		}
		// TParm selMaxSeqParm =
		// IBSOrderdTool.getInstance().selMaxSeq(selMaxSeq);
		// int maxSeq = selMaxSeqParm.getInt("CASE_NO_SEQ", 0);
		// int minSeq = selMaxSeqParm.getInt("MIN_CASE_NO_SEQ", 0);//��С
		caseNoFirst = caseNoFirst + 1;
		if (caseNoFirst <= selDifferentParm.getCount()) {
			if (selDifferentParm.getInt("CASE_NO_SEQ", caseNoFirst) >= maxSeq) {
				// this.messageBox("���һ��");
				this.callFunction("UI|PG_DOWN|setEnabled", false);
				this.callFunction("UI|PG_UP|setEnabled", true);
				// return;
			} else {
				this.callFunction("UI|PG_UP|setEnabled", true);
				this.callFunction("UI|PG_DOWN|setEnabled", true);
			}
		} else {
			this.callFunction("UI|PG_DOWN|setEnabled", false);
			this.callFunction("UI|PG_UP|setEnabled", true);
		}

		this.setValue("PG_NO", selDifferentParm.getValue("CASE_NO_SEQ",
				caseNoFirst));
		billDetailParm.setData("CASE_NO_SEQ", selDifferentParm.getValue(
				"CASE_NO_SEQ", caseNoFirst));
		TParm billDetailData = new TParm();
		billDetailData = IBSOrderdTool.getInstance().selBillDetail(
				billDetailParm);
		// ��billDetailTable���
		this.callFunction("UI|billDetailTable|setParmValue", billDetailData);

	}

	/**
	 * ��һ��
	 */
	public void pgUp() {
		// billDetailTable��ѯ����
		TParm billDetailParm = new TParm();
		billDetailParm.setData("CASE_NO", caseNo);
		if (getValue("BILL_DATE") != null && getValue("BILL_DATE_E") != null) {
			Timestamp bill = (Timestamp) getValue("BILL_DATE");
			Timestamp billE = (Timestamp) getValue("BILL_DATE_E");
			String billDate = StringTool.getString(bill, "yyyyMMddHHmmss");
			String billDateE = StringTool.getString(billE, "yyyyMMddHHmmss");
			billDetailParm.setData("BILL_DATE", billDate);
			billDetailParm.setData("BILL_DATE_E", billDateE);
		}

		// TParm selMaxSeqParm =
		// IBSOrderdTool.getInstance().selMaxSeq(billDetailParm);
		// int maxSeq = selMaxSeqParm.getInt("CASE_NO_SEQ", 0);
		// int minSeq = selMaxSeqParm.getInt("MIN_CASE_NO_SEQ", 0);//��С
		caseNoFirst = caseNoFirst - 1;
		if (caseNoFirst <= selDifferentParm.getCount() && caseNoFirst >= 0) {
			if (selDifferentParm.getInt("CASE_NO_SEQ", caseNoFirst) <= minSeq) {
				// this.messageBox("��һ��");
				this.callFunction("UI|PG_UP|setEnabled", false);
				this.callFunction("UI|PG_DOWN|setEnabled", true);
				// return;
			} else {
				this.callFunction("UI|PG_UP|setEnabled", true);
				this.callFunction("UI|PG_DOWN|setEnabled", true);
			}
		} else {
			caseNoFirst = 0;
			this.callFunction("UI|PG_UP|setEnabled", false);
			this.callFunction("UI|PG_DOWN|setEnabled", true);
		}

		this.setValue("PG_NO", selDifferentParm.getValue("CASE_NO_SEQ",
				caseNoFirst));
		billDetailParm.setData("CASE_NO_SEQ", selDifferentParm.getValue(
				"CASE_NO_SEQ", caseNoFirst));
		TParm billDetailData = new TParm();
		billDetailData = IBSOrderdTool.getInstance().selBillDetail(
				billDetailParm);
		// ��billDetailTable���
		this.callFunction("UI|billDetailTable|setParmValue", billDetailData);
	}

	/**
	 * ��ѯ����ҽ��
	 */
	public void onQuerySingleOrder() {
		// singleOrderTable��ѯ����
		TParm singleOrderParm = new TParm();
		singleOrderParm.setData("CASE_NO", caseNo);
		singleOrderParm.setData("ORDER_CODE", getValue("ORDER_CODE"));
		TParm singleOrderData = new TParm();
		//����ҽ���뵥��ҽ���ܼ۲�Ϊ0  donglt 2016-3-22
        //��sql���д��TOOL����
		singleOrderData = IBSOrderdmTool.getInstance().queryPatInfo(singleOrderParm);
//		singleOrderData = IBSOrderdTool.getInstance().selSingleOrder(singleOrderParm);
		if (singleOrderData.getCount() == 0
				|| getValue("ORDER_CODE").toString().length() == 0) {
			// ��������
			this.messageBox("E0008");
		}
		// ��singleOrderTable���
		this.callFunction("UI|singleOrderTable|setParmValue", singleOrderData);
	}

	/**
	 * ��ѯ�ϲ�������ϸ
	 */
	public void onQueryMergeFee() {
		// mergeFeeTable��ѯ����
		TParm mergeFeeParm = new TParm();
		mergeFeeParm.setData("CASE_NO", caseNo);
		// У���վ����
		if (getValue("REXP_TYPE").toString().length() != 0) {
			mergeFeeParm.setData("REXP_CODE", getValue("REXP_TYPE"));
		}
		if (!(getValue("C_BILL").equals("Y") || getValue("C_UNBILL")
				.equals("Y"))) {
			this.messageBox("��ѡ���ѽ�/δ�ᡱ��ѯ����");
			return;
		}
		if (getValue("C_BILL").equals("N") || getValue("C_UNBILL").equals("N")) {
			if (getValue("C_BILL").equals("Y")) {
				mergeFeeParm.setData("BILL", "Y");
			}
			if (getValue("C_UNBILL").equals("Y")) {
				mergeFeeParm.setData("UNBILL", "Y");
			}

		}
		if (getValue("C_UP_FEE").equals("Y")) {
			mergeFeeParm.setData("TOT_AMT", getValue("TOT_AMT"));
		}
		TParm mergeFeeData = new TParm();
		mergeFeeData = IBSOrderdTool.getInstance().selMergeFee(mergeFeeParm);
		if (mergeFeeData.getCount() == 0) {
			// ��������
			this.messageBox("E0008");
		}
		// �����ν�ֵ
		int feeCount = mergeFeeData.getCount("TOT_AMT");
		double mergeTotFee = 0.00;
		for (int i = 0; i < feeCount; i++) {
			mergeTotFee = mergeTotFee + mergeFeeData.getDouble("TOT_AMT", i);
		}
		setValue("MERGE_TOT_FEE", mergeTotFee);
		
		// ��mergeFeeTable���
		this.callFunction("UI|mergeFeeTable|setParmValue", mergeFeeData);
	}

	/**
	 * ���ҷ��ò�ѯ
	 */
	public void onQueryDeptFee() {
		// ��� wangzl 20120806 start
		// deptFeeTable��ѯ����
		TParm deptFeeParm = new TParm();
		deptFeeParm.setData("CASE_NO", caseNo);
		//this.messageBox("���ҷ��ò�ѯ" + deptFeeParm.getData("CASE_NO"));
		// У�鿪ʼ����
		if (getValue("START_DATE") != null) {
			Timestamp start = (Timestamp) getValue("START_DATE");
			String startDate = StringTool.getString(start, "yyyyMMdd HH:mm:ss");
			deptFeeParm.setData("START_DATE", startDate);
		}
		
		// У���������
		if (getValue("END_DATE") != null) {
			Timestamp end = (Timestamp) getValue("END_DATE");
			String endDate = StringTool.getString(end, "yyyyMMdd HH:mm:ss");
			deptFeeParm.setData("END_DATE", endDate);
		}
	
		deptFeeParm.setData("cost_center_code", this
				.getValueString("COST_CENTER_CODE"));// �ɱ�����
		deptFeeParm.setData("order_cat1_code", this
				.getValueString("ORDER_CAT1_CODE"));// ҽ������
		
		deptFeeParm.setData("ctfig", this.getValue("CTRLDRUGCLASS_CODE"));// modify
																			// by
																			// caowl
																			// 20120814
		TParm deptFeeData = IBSOrderdTool.getInstance().selDeptFee(deptFeeParm);
		if (deptFeeData.getCount("BILL_DATE") < 0) {
			// ��������
			this.messageBox("E0008");
			return;
		}
		// ��� wangzl 20120806 stop
		// �����ν�ֵ
		int feeCount = deptFeeData.getCount("TOT_AMT");
		double deptTotFee = 0.00;
		// ===pangben 2011-11-15
		// ɾ��ϸ���
		int count = 0;
		TParm tableinparm = new TParm();
		for (int i = 0; i < feeCount; i++) {
			deptTotFee = deptTotFee + deptFeeData.getDouble("TOT_AMT", i);
			if (deptFeeData.getValue("INDV_FLG", i).length() <= 0
					|| "N".equals(deptFeeData.getValue("INDV_FLG", i))) { // ��������
				tableinparm.addRowData(deptFeeData, i);
				count++;
			}
		}
		tableinparm.setCount(count);
		tableinParm(deptFeeData, tableinparm);
		// ===pangben 2011-11-15 stop
		setValue("DEPT_TOT_FEE", deptTotFee);
		callFunction("UI|print|setEnabled", true);//===caowl
		callFunction("UI|export|setEnabled", true);//===caowl
		// ��deptFeeTable���
		this.callFunction("UI|deptFeeTable|setParmValue", tableinparm);
		// this.ORDER_CAT1_CODE="";
	}
	
	/**
	 * ���ҷ��û���
	 */
	public void onQueryDeptFeeSum(){
		//this.messageBox("������ҷ��û���" + getValue("END_DATESUM") + "���ҷ���" + this.getValue("END_DATE"));
		// ��� zhanglei 20170628 start
		// deptFeeSumTable��ѯ����
		TParm deptFeeParmSum = new TParm();
		deptFeeParmSum.setData("CASE_NO", caseNo);
		//this.messageBox("���ҷ��û���" + deptFeeParmSum.getData("CASE_NO"));
		// У�鿪ʼ����
		if (getValue("START_DATESUM") != null) {
			Timestamp start = (Timestamp) getValue("START_DATESUM");
			String startDate = StringTool.getString(start, "yyyyMMdd HH:mm:ss");
			deptFeeParmSum.setData("START_DATESUM", startDate);
		}
		
		// У���������
		if (getValue("END_DATESUM") != null) {
			Timestamp end = (Timestamp) getValue("END_DATESUM");
			//this.messageBox("end" + end);
			String endDate = StringTool.getString(end, "yyyyMMdd HH:mm:ss");
			deptFeeParmSum.setData("END_DATESUM", endDate);
			//this.messageBox("END_DATESUM" + deptFeeParmSum.getData("END_DATESUM"));
		}
		
		deptFeeParmSum.setData("cost_center_code", this
				.getValueString("COST_CENTER_CODESUM"));// �ɱ�����
		deptFeeParmSum.setData("order_cat1_code", this
				.getValueString("ORDER_CAT1_CODESUM"));// ҽ������
		
		deptFeeParmSum.setData("ctfig", this.getValue("CTRLDRUGCLASS_CODESUM"));// modify
																			// by
																			// caowl
																			// 20120814
		TParm deptFeeSumData = IBSOrderdTool.getInstance().selDeptFeeSum(deptFeeParmSum);
		//this.messageBox(""+deptFeeSumData.getCount("TOT_AMT"));
		if (deptFeeSumData.getCount("TOT_AMT") < 0) {
			// ��������
			//this.messageBox(""+deptFeeSumData.getCount("TOT_AMT"));
			this.messageBox("E0008");
			return;
		}
		// ��� wangzl 20120806 stop
		// �����ν�ֵ
		int feeCount = deptFeeSumData.getCount("TOT_AMT");
		double deptTotFee = 0.00;
		// ===pangben 2011-11-15
		// ɾ��ϸ���
		int count = 0;
		TParm tableinparm = new TParm();
		for (int i = 0; i < feeCount; i++) {
			deptTotFee = deptTotFee + deptFeeSumData.getDouble("TOT_AMT", i);
			if (deptFeeSumData.getValue("INDV_FLG", i).length() <= 0
					|| "N".equals(deptFeeSumData.getValue("INDV_FLG", i))) { // ��������
				tableinparm.addRowData(deptFeeSumData, i);
				count++;
			}
		}
		tableinparm.setCount(count);
		tableinParm(deptFeeSumData, tableinparm);
		// ===pangben 2011-11-15 stop
		setValue("DEPT_TOT_FEESUM", deptTotFee);
		callFunction("UI|print|setEnabled", true);//===caowl
		callFunction("UI|export|setEnabled", true);//===caowl
		// ��deptFeeTable���
		this.callFunction("UI|deptFeeSumTable|setParmValue", tableinparm);
	}

	/**
	 * ��ʾ����ҽ�����
	 * 
	 * @param tempParm
	 *            TParm
	 * @param tableinparm
	 *            TParm
	 */
	private void tableinParm(TParm tempParm, TParm tableinparm) {

		for (int z = 0; z < tableinparm.getCount(); z++) {
			double sumTotAmt = 0.00;
			double sumOwnAmt = 0.00;
			double sumNhiAmt = 0.00;
			boolean isSet = false;
			for (int j = 0; j < tempParm.getCount(); j++) {
				if (("Y".equals(tempParm.getValue("INDV_FLG", j)) || tempParm
						.getValue("INDV_FLG", j).length() <= 0)
						&& tableinparm.getValue("ORDER_CODE", z).equals(
								tempParm.getValue("ORDERSET_CODE", j))
						&& tempParm.getValue("CASE_NO_SEQ", j).equals(
								tableinparm.getValue("CASE_NO_SEQ", z))
						&& tempParm.getValue("ORDERSET_GROUP_NO", j).equals(
								tableinparm.getValue("ORDERSET_GROUP_NO", z))) { // ��������
					sumTotAmt += tempParm.getDouble("TOT_AMT", j);
					// sumOwnAmt += tempParm.getDouble("OWN_PRICE", j);
					// modify by wanglong 20120808 ����ҽ�����۵ļ���Ҫ����������������ܼ۲���
					sumOwnAmt += tempParm.getDouble("OWN_PRICE", j)
							* tempParm.getDouble("DOSAGE_QTY", j);
					sumNhiAmt += tempParm.getDouble("NHI_PRICE", j);
					isSet = true;
				}
			}
			if (isSet) {
				tableinparm.setData("TOT_AMT", z, sumTotAmt);
				tableinparm.setData("OWN_PRICE", z, sumOwnAmt);
				tableinparm.setData("NHI_PRICE", z, sumNhiAmt);
			}
		}
	}

	/**
	 * ���checkBox�ı��¼�
	 */
	public void onChangeRexpType() {
		if (getValue("C_RECP_TYPE").equals("Y"))
			this.callFunction("UI|REXP_TYPE|setEnabled", true);
		else {
			this.callFunction("UI|REXP_TYPE|setEnabled", false);
			this.setValue("REXP_TYPE", "");
		}
	}

	/**
	 * ����checkBox�ı��¼�
	 */
	public void onChangeUpFee() {
		if (getValue("C_UP_FEE").equals("Y"))
			this.callFunction("UI|TOT_AMT|setEnabled", true);
		else {
			this.callFunction("UI|TOT_AMT|setEnabled", false);
			this.setValue("TOT_AMT", 0);
		}
	}

	/**
	 * ����ǰTOOLBAR
	 */
	public void onShowWindowsFunction() {
		// ��ʾUIshowTopMenu
		callFunction("UI|showTopMenu");
	}

	// /**
	// * �һ�MENU�����¼�
	// * @param tableName
	// */
	// public void showPopMenu() {
	// TTable table = (TTable)this.getComponent("feeDetailTable");
	// int row = table.getSelectedRow();
	// TParm parm = table.getShowParmValue() ;
	// String order_code = parm.getValue("ORDER_CODE", row) ;
	// String sql =
	// "SELECT ORDERSET_FLG FROM SYS_FEE WHERE ORDER_CODE = '"+order_code+"'" ;
	// TParm result = new TParm(TJDODBTool.getInstance().select(sql));
	// if (result.getErrCode() < 0) {
	// messageBox("���ݴ��� " + result.getErrText());
	// return ;
	// }
	// if(result.getCount()>0){
	// if ("Y".equals(result.getValue("ORDERSET_FLG", 0))) {
	// table
	// .setPopupMenuSyntax("��ʾ����ҽ��ϸ��|Show LIS/RIS Detail Items,onOrderSetShow");
	// return;
	// } else {
	// table.setPopupMenuSyntax("");
	// return;
	// }
	// }
	// }
	//
	// /**
	// * �һ�MENU��ʾ����ҽ���¼�
	// */
	// public void onOrderSetShow(){
	// TTable table = (TTable)this.getComponent("feeDetailTable");
	// int row = table.getSelectedRow();
	// TParm parm = table.getShowParmValue() ;
	// String order_code = parm.getValue("ORDER_CODE", row) ;
	// String sql = "SELECT A.ORDER_DESC, A.SPECIFICATION, DOSAGE_QTY, "
	// + " UNIT_CODE AS MEDI_UNIT, OWN_PRICE, OWN_PRICE * DOSAGE_QTY "
	// + " AS OWN_AMT, EXEC_DEPT_CODE, OPTITEM_CODE, INSPAY_TYPE "
	// + " FROM SYS_FEE A, SYS_ORDERSETDETAIL B "
	// + " WHERE A.ORDER_CODE = B.ORDER_CODE "
	// + " AND B.ORDERSET_CODE = '" + order_code + "' "
	// + " ORDER BY B.ORDERSET_CODE, B.ORDER_CODE";
	// TParm result = new TParm(TJDODBTool.getInstance().select(sql));
	// this.openDialog("%ROOT%\\config\\Zodo\\OPDOrderSetShow.x", result);
	//
	// }
	/**
	 * �һ�MENU�����¼�
	 */
	public void showPopMenu() {
		TTable table = (TTable) this.getComponent("feeDetailTable");
		table.setPopupMenuSyntax("��ʾ����ҽ��ϸ��,openRigthPopMenu");
	}

	/**
	 * �򿪼���ҽ��ϸ���ѯ
	 */
	public void openRigthPopMenu() {
		TTable table = (TTable) this.getComponent("feeDetailTable");
		int row = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		String caseNo = tableParm.getValue("CASE_NO", row);
		String orderCode = tableParm.getValue("ORDER_CODE", row);
		String orderSetCode = tableParm.getValue("ORDERSET_CODE", row);
		String caseNoSeq = tableParm.getValue("CASE_NO_SEQ", row);
		int orderSetGroupNo = Integer.parseInt(tableParm.getValue(
				"ORDERSET_GROUP_NO", row));
		TParm parm = getOrderSetDetails(orderSetGroupNo, orderSetCode, caseNo,
				caseNoSeq, orderCode);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
	}

	/**
	 * ���ؼ���ҽ��ϸ���TParm��ʽ
	 * 
	 * @param groupNo
	 *            int
	 * @param orderSetCode
	 *            String
	 * @param caseNo
	 *            String
	 * @param caseNoSeq
	 *            String
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderSetDetails(int groupNo, String orderSetCode,
			String caseNo, String caseNoSeq, String orderCode) {

		TParm result = new TParm();
		if (groupNo < 0) {
			System.out
					.println("OpdOrder->getOrderSetDetails->groupNo is invalie");
			return result;
		}
		if (StringUtil.isNullString(orderSetCode)) {
			System.out
					.println("OpdOrder->getOrderSetDetails->orderSetCode is invalie");
			return result;
		}
		String sql = "SELECT * FROM IBS_ORDD WHERE CASE_NO='" + caseNo
				+ "' AND CASE_NO_SEQ='" + caseNoSeq
				+ "' AND ORDERSET_GROUP_NO=" + groupNo + "";

		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		int count = parm.getCount();
		if (count < 0) {
			//System.out.println("OpdOrder->getOrderSetDetails->count <  0");
			return result;
		}
		// temperrϸ��۸�
		for (int i = 0; i < count; i++) {
			if (!orderCode.equals(parm.getValue("ORDER_CODE", i))
					&& orderSetCode.equals(parm.getValue("ORDERSET_CODE", i))) {
				// ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
				result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
				result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				// ��ѯ����
				TParm orderParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT OWN_PRICE,ORDER_DESC,SPECIFICATION,OPTITEM_CODE,INSPAY_TYPE "
								+ "FROM SYS_FEE WHERE ORDER_CODE='"
								+ parm.getValue("ORDER_CODE", i) + "'"));
				// this.messageBox_(ownPriceParm);
				// �����ܼ۸�
				double ownPrice = orderParm.getDouble("OWN_PRICE", 0)
						* parm.getDouble("DOSAGE_QTY", i);
				result
						.addData("OWN_PRICE", orderParm.getDouble("OWN_PRICE",
								0));
				result.addData("OWN_AMT", ownPrice);
				result.addData("ORDER_DESC", orderParm
						.getValue("ORDER_DESC", 0));
				result.addData("SPECIFICATION", orderParm.getValue(
						"SPECIFICATION", 0));
				result.addData("EXEC_DEPT_CODE", parm.getValue("EXE_DEPT_CODE",
						i));
				result.addData("OPTITEM_CODE", orderParm.getValue(
						"OPTITEM_CODE", 0));
				result.addData("INSPAY_TYPE", orderParm.getValue("INSPAY_TYPE",
						0));
			}
		}
		return result;
	}

	public String selInsICDCode(String icdCode) {
		TParm result = new TParm();
		int count = icdCode.length();
		String insIcdCode = "";
		for (int i = 0; i < count; i++) {
			icdCode = icdCode.substring(0, count - i);
			String selIcdCode = " SELECT ICD_CODE, ICD_CHN_DESC "
					+ "   FROM INS_DIAGNOSIS " + "  WHERE ICD_CODE = '"
					+ icdCode + "' ";
			result = new TParm(TJDODBTool.getInstance().select(selIcdCode));
			if (result.getErrCode() < 0) {
				return "";
			}
			if (result.getCount() <= 0) {
				continue;
			} else {
				insIcdCode = result.getValue("ICD_CODE", 0);
				return insIcdCode;
			}
		}
		return insIcdCode;

	}

	/**
	 * �õ�table
	 */
	public TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	/**
	 * ����Excel
	 */
	public void onExport() {
		// ��� wangzl 20120806 start
		if (deptFeeTable.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(deptFeeTable, "���ҷ��ò�ѯϸ��");
		} else {
			this.messageBox("�޻������");
			return;
		}
		// ��� wangzl 20120806 stop
	}

	/**
	 * ����Ƿ�Ϊ�ջ�մ�
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		// ��� wangzl 20120806 start
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
		// ��� wangzl 20120806 stop
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		
		//��� zhanglei ��ӿ��ҷ��û��� 20170629 start
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		
		if(selectedIndex == 6){
			onPrintSum();
			return;
		}
		//��� zhanglei ��ӿ��ҷ��û��� 20170629 end
		
		// ��� wangzl 20120806 start
		DecimalFormat df = new DecimalFormat("##########0.00");
		if (deptFeeTable.getRowCount() <= 0) {
			this.messageBox("1�޴�ӡ����");
			return;
		}
		TParm prtParm = new TParm();
		// ��ͷ
		prtParm.setData("TITLE", "TEXT", "���ҷ��ò�ѯ����");
		prtParm.setData("MR_NO", "TEXT", mrNo);
		Pat pat = Pat.onQueryByMrNo(mrNo);
		prtParm.setData("PAT_NAME", "TEXT", pat.getName());
		// ��������
		Timestamp birthDate = pat.getBirthday();
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String age = "0";
		if (birthDate != null)
			age = OdiUtil.getInstance().showAge(birthDate, sysDate);
		else
			age = "";
		prtParm.setData("AGE", "TEXT", age);
		String sexCode = pat.getSexCode();
		String sql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX' AND ID='"
				+ sexCode + "'";
		TParm dataIn = new TParm(TJDODBTool.getInstance().select(sql));
		prtParm.setData("SEX", "TEXT", dataIn.getValue("CHN_DESC",0));
		//�ϼƽ��
		double totalFee = 0.00;
//		String start_Date = this.getValueString("START_DATE");
//		String end_Date = this.getValueString("END_DATE");
//		if (this.checkNullAndEmpty(start_Date)) {
//			start_Date = start_Date.substring(0, (start_Date.length() - 2));
//		}
//		if (this.checkNullAndEmpty(end_Date)) {
//			end_Date = end_Date.substring(0, (end_Date.length() - 2));
//		}
//		prtParm.setData("START_DATE", "TEXT", start_Date);
//		prtParm.setData("END_DATE", "TEXT", end_Date);
		// �������
		TParm parm = new TParm();
		TParm tableParm = deptFeeTable.getParmValue();
		TComboBox com = (TComboBox) this.getComponent("DOSAGE_UNIT");
		for (int i = 0; i < deptFeeTable.getRowCount(); i++) {
			String blld = tableParm.getValue("BILL_DATE", i);
			blld = blld.substring(0, 19);
			parm.addData("BILL_DATE", blld);
//			parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
			//ҽ������
			parm.addData("ORDER_DESC1", tableParm.getValue("ORDER_DESC1", i));
			//���
			parm.addData("SPECIFICATION", tableParm.getValue("SPECIFICATION", i));
//			String ownFig = tableParm.getValue("OWN_FLG", i);
//			if (ownFig.equals("Y")) {
//				ownFig = "��";
//			} else {
//				ownFig = "��";
//			}
//			parm.addData("OWN_FLG", ownFig);
			//����
			parm.addData("DOSAGE_QTY", tableParm.getDouble("DOSAGE_QTY", i));
			//��λ
			String rn = tableParm.getValue("DOSAGE_UNIT", i);
			TComboBoxModel tbm = com.getModel();
			Vector v = tbm.getItems();
			for (int j = 0; j < v.size(); j++) {
				TComboNode tn = (TComboNode) v.get(j);
				if (rn.equals(tn.getID())) {
					rn = tn.getName();
					break;
				}
			}
			parm.addData("DOSAGE_UNIT", rn);
			//����
			double o = tableParm.getDouble("OWN_PRICE", i);
			o = StringTool.round(o, 4);
			parm.addData("OWN_PRICE", o);
			//Ӧ��
			double ot = tableParm.getDouble("OWN_AMT", i);
			ot = StringTool.round(ot, 2);
			parm.addData("OWN_AMT", ot);
			//�ܼ�
			double t = tableParm.getDouble("TOT_AMT", i);
			t = StringTool.round(t, 2);
			parm.addData("TOT_AMT", t);
			
			totalFee +=t;//�����ܼۼ���ϼƽ��
			
			//ִ��ʱ��
			String optdate = tableParm.getValue("OPT_DATE", i);		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String exeDate;
			try {
//				System.out.println("exeDate:::"+sdf.parse(optdate.substring(0,19)));
				exeDate = sdf.format(sdf.parse(optdate.substring(0,19)));
				parm.addData("OPT_DATE", exeDate.replace("-", "/"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			//ִ����
			parm.addData("USER_NAME", tableParm.getValue("USER_NAME", i));
			//ִ�п���
			parm.addData("COST_CENTER_CHN_DESC", tableParm.getValue("COST_CENTER_CHN_DESC", i));
		}
		parm.setCount(parm.getCount("BILL_DATE"));
//		parm.addData("SYSTEM", "COLUMNS", "BILL_DATE");
//		parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
//		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//		parm.addData("SYSTEM", "COLUMNS", "OWN_FLG");
//		parm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
//		parm.addData("SYSTEM", "COLUMNS", "DOSAGE_UNIT");
//		parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
//		parm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
//		parm.addData("SYSTEM", "COLUMNS", "OPT_DATE");
//		parm.addData("SYSTEM", "COLUMNS", "USER_NAME");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC1");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");		
		parm.addData("SYSTEM", "COLUMNS", "DOSAGE_UNIT");
		parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
		parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
		parm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
		parm.addData("SYSTEM", "COLUMNS", "USER_NAME");
		parm.addData("SYSTEM", "COLUMNS", "COST_CENTER_CHN_DESC");
		parm.addData("SYSTEM", "COLUMNS", "OPT_DATE");
		// �ѱ��������ӽ�Ҫ��ӡ��parm
		prtParm.setData("TABLE", parm.getData());
		prtParm.setData("TOTAL_FEE", "TEXT", df.format(totalFee));
//		prtParm.setData("NAME", "TEXT", "�����ˣ�" + Operator.getName());
		// ���ô�ӡ����,����·��
		this.openPrintWindow("%ROOT%\\config\\prt\\IBS\\IBSSelOrderm.jhw",
				prtParm);
		// ��� wangzl 20120806 stop
	}
	
	/**
	 * ���ҷ��û���ҳǩ��ӡ����
	 */
	public void onPrintSum(){
		//this.messageBox("1111111111");
		//��� zhanglei ��ӿ��ҷ��û��� 20170629 start
		DecimalFormat df = new DecimalFormat("##########0.00");
		if (deptFeeSumTable.getRowCount() <= 0) {
			this.messageBox("2�޴�ӡ����");
			return;
		}
		TParm prtParm = new TParm();
		// ��ͷ
		prtParm.setData("TITLE", "TEXT", "���ҷ��û��ܱ���");
		prtParm.setData("MR_NO", "TEXT", mrNo);
		Pat pat = Pat.onQueryByMrNo(mrNo);
		prtParm.setData("PAT_NAME", "TEXT", pat.getName());
		// ��������
		Timestamp birthDate = pat.getBirthday();
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String age = "0";
		if (birthDate != null)
			age = OdiUtil.getInstance().showAge(birthDate, sysDate);
		else
			age = "";
		prtParm.setData("AGE", "TEXT", age);
		String sexCode = pat.getSexCode();
		String sql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX' AND ID='"
				+ sexCode + "'";
		TParm dataIn = new TParm(TJDODBTool.getInstance().select(sql));
		prtParm.setData("SEX", "TEXT", dataIn.getValue("CHN_DESC",0));
		//�ϼƽ��
		double totalFee = 0.00;
		// �������
		TParm parm = new TParm();
		TParm tableParm = deptFeeSumTable.getParmValue();
		TComboBox com = (TComboBox) this.getComponent("DOSAGE_UNIT");
		for (int i = 0; i < deptFeeSumTable.getRowCount(); i++) {
			//ҽ������
			parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
			//ҽ������
			parm.addData("ORDER_DESC1", tableParm.getValue("ORDER_DESC1", i) + tableParm.getValue("SPECIFICATION", i));
			//�Է�
			String ownFig = tableParm.getValue("OWN_FLG", i);
			if (ownFig.equals("Y")) {
				ownFig = "��";
			} else {
				ownFig = "��";
			}
			parm.addData("OWN_FLG", ownFig);
			//���
//			parm.addData("SPECIFICATION", tableParm.getValue("SPECIFICATION", i));
			//����
			parm.addData("DOSAGE_QTY", tableParm.getDouble("DOSAGE_QTY", i));
			//��λ
			String rn = tableParm.getValue("DOSAGE_UNIT", i);
			TComboBoxModel tbm = com.getModel();
			Vector v = tbm.getItems();
			for (int j = 0; j < v.size(); j++) {
				TComboNode tn = (TComboNode) v.get(j);
				if (rn.equals(tn.getID())) {
					rn = tn.getName();
					break;
				}
			}
			parm.addData("DOSAGE_UNIT", rn);
			//����
			double o = tableParm.getDouble("OWN_PRICE", i);
			o = StringTool.round(o, 4);
			parm.addData("OWN_PRICE", o);
			//Ӧ��
//			double ot = tableParm.getDouble("OWN_AMT", i);
//			ot = StringTool.round(ot, 2);
//			parm.addData("OWN_AMT", ot);
			//�ܼ�
			double t = tableParm.getDouble("TOT_AMT", i);
			t = StringTool.round(t, 2);
			parm.addData("TOT_AMT", t);
			
			totalFee +=t;//�����ܼۼ���ϼƽ��
			//�ɱ�����
			parm.addData("COST_CENTER_CHN_DESC", tableParm.getValue("COST_CENTER_CHN_DESC", i));
			//System.out.println("111111111" + tableParm.getValue("COST_CENTER_CODE", i));
		}
		parm.setCount(parm.getCount("ORDER_CODE"));
		parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");//ҽ������
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC1");//ҽ������
		parm.addData("SYSTEM", "COLUMNS", "OWN_FLG");//�Է�
		//parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");//���
		parm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");//����	
		parm.addData("SYSTEM", "COLUMNS", "DOSAGE_UNIT");//��λ
		parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");//����
//		parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
		parm.addData("SYSTEM", "COLUMNS", "TOT_AMT");//�ܼ�
		parm.addData("SYSTEM", "COLUMNS", "COST_CENTER_CHN_DESC");//�ɱ�����
		// �ѱ��������ӽ�Ҫ��ӡ��parm
		
		prtParm.setData("TABLE", parm.getData());
		prtParm.setData("TOTAL_FEE", "TEXT", df.format(totalFee));
//		prtParm.setData("NAME", "TEXT", "�����ˣ�" + Operator.getName());
		//System.out.println("66666666666" + prtParm);
		// ���ô�ӡ����,����·��
		this.openPrintWindow("%ROOT%\\config\\prt\\IBS\\IBSSelOrdermSum.jhw",
				prtParm);
		//��� zhanglei ��ӿ��ҷ��û��� 20170629 end
	}

	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) { // =======modify by wanglong
													// begin 20120815
													// �޸ı�����,ʹ�˷���ͨ��
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() { // =======modify
																				// by
																				// wanglong
																				// begin
																				// 20120815
																				// �޸ı�����,ʹ�˷���ͨ��
					public void mouseClicked(MouseEvent mouseevent) {
						int i = table.getTable().columnAtPoint(
								mouseevent.getPoint());// modify by wanglong
														// 20120815 ʹ�˷���ͨ��
						int j = table.getTable().convertColumnIndexToModel(i);// modify
																				// by
																				// wanglong
																				// 20120815
																				// ʹ�˷���ͨ��
						// System.out.println("+i+"+i);
						// System.out.println("+i+"+j);
						// �������򷽷�;
						// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
						if (j == sortColumn) {
							ascending = !ascending;
						} else {
							ascending = true;
							sortColumn = j;
						}
						// �����parmֵһ��,
						// 1.ȡparamwֵ;
						TParm tableData = table.getParmValue();// modify by
																// wanglong
																// 20120815
																// ʹ�˷���ͨ��
						// 2.ת�� vector����, ��vector ;
						String columnName[] = tableData.getNames("Data");
						String strNames = "";
						for (String tmp : columnName) {
							strNames += tmp + ";";
						}
						strNames = strNames.substring(0, strNames.length() - 1);
						Vector vct = getVector(tableData, "Data", strNames, 0);

						// 3.���ݵ������,��vector����
						// System.out.println("sortColumn===="+sortColumn);
						// ������������;
						String tblColumnName = table.getParmMap(sortColumn);// modify
																			// by
																			// wanglong
																			// 20120815
																			// ʹ�˷���ͨ��
						// ת��parm�е���
						int col = tranParmColIndex(columnName, tblColumnName);
						// System.out.println("==col=="+col);

						compare.setDes(ascending);
						compare.setCol(col);
						java.util.Collections.sort(vct, compare);
						// ��������vectorת��parm;
						// modify by wanglong 20120815
						// Ϊ��ʹ�˷���ͨ�ã�����Ҫ��cloneVectoryParam()Ҳ��ͨ�ã����ܽ�����deptFeeTable
						cloneVectoryParam(vct, new TParm(), strNames, table);
						// getTMenuItem("save").setEnabled(false);
					}
				});
		// ��� wangzl 20120806 Stop
	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" })
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		// ��� wangzl 20120806 start
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
		// ��� wangzl 20120806 stop
	}

	/**
	 * vectoryת��param
	 */
	@SuppressWarnings("rawtypes")
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {// modify by wanglong
														// 20120815
														// ����һ���βΣ�Ϊ��ʹ�˷���ͨ�ã�
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);// modify by wanglong 20120815 Ϊ��ʹ�˷���ͨ��
	}
}