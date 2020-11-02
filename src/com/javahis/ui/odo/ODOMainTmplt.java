package com.javahis.ui.odo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.odo.Diagrec;
import jdo.odo.OpdOrder;
import jdo.opd.ODOTool;
import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.tui.text.ECapture;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWindow;
import com.dongyang.util.StringTool;
import com.javahis.util.EmrUtil;
import com.javahis.util.OdoUtil;
import com.javahis.util.OrderUtil;
import com.javahis.util.StringUtil;

/**
 *
 * <p>
 *
 * Title: ����ҽ������վģ�����
 * </p>
 *
 * <p>
 * Description:����ҽ������վģ�����
 * </p>
 *
 * <p>
 * Company:Bluecore
 * </p>
 *
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODOMainTmplt {

	public OdoMainControl odoMainControl;
	public ODOMainOther odoMainOther;
	public ODOMainPat odoMainPat;
	public ODOMainOpdOrder odoMainOpdOrder;
	public ODOMainReg odoMainReg;

	private static final String URLCOMMONORDERQUOTE = "%ROOT%\\config\\opd\\CommonOrderQuote.x";
	private static final String URLOPDCOMPACKQUOTE = "%ROOT%\\config\\opd\\OPDComPackQuote.x";
	private static final String URLODOCOMMONICDQUOTE = "%ROOT%\\config\\opd\\ODOCommonIcdQuote.x";
	private static final String URLSYSEXASHEETTREE = "%ROOT%\\config\\sys\\SysExaSheetTree.x";
	private static final String URLEMRSINGLEUI = "%ROOT%\\config\\emr\\EMRSingleUI.x";
	private static final String URLOPDCASEHISTORY = "%ROOT%\\config\\opd\\OPDCaseHistory.x";
	private static final String URLDOCTORTCSZ = "%ROOT%\\config\\mem\\MEMDoctorPackage.x";

	private static final String NULLSTR = "";

	public ODOMainTmplt(OdoMainControl odoMainControl){
		this.odoMainControl = odoMainControl;
	}

	/**
	 * ��ʼ��������
	 */
	public void onInit() throws Exception{
		odoMainOther = odoMainControl.odoMainOther;
		odoMainPat = odoMainControl.odoMainPat;
		odoMainOpdOrder = odoMainControl.odoMainOpdOrder;
		odoMainReg = odoMainControl.odoMainReg;
	}

	/**
	 * ����ҽʦ����ҽ��
	 */
	public void onDrOrder()throws Exception {
		TParm parm = new TParm();
		parm.setData("DEPT_DR", "2");
		parm.setData("DEPTORDR_CODE", Operator.getID());
		if (ODOMainReg.O.equalsIgnoreCase(odoMainReg.admType)) {
			parm.setData("FIT", "OPD_FIT_FLG");
		} else {
			parm.setData("FIT", "EMG_FIT_FLG");
		}
		TTabbedPane tabPane = (TTabbedPane) odoMainControl.getComponent(ODOMainOpdOrder.TAGTTABPANELORDER);
		int index = tabPane.getSelectedIndex();
		String orderCat1Type = NULLSTR;
		String tableName = NULLSTR;
		String tag = NULLSTR;
		String rxTag = NULLSTR;
		// ѡ���ĸ�ҳǩ
		switch (index) {
		// ������
		case ODORxExa.TABBEDPANE_INDEX:
			parm.setData("RX_TYPE", ODORxExa.EXA);
			orderCat1Type = ODORxExa.ORDERCAT1TYPE;
			tableName = ODORxExa.TABLE_EXA;
			tag = ODORxExa.AMT_TAG;
			rxTag = ODORxExa.EXA_RX;
			break;
		// ����
		case ODORxOp.TABBEDPANE_INDEX:
			parm.setData("RX_TYPE", ODORxOp.OP);
			orderCat1Type = ODORxOp.ORDERCAT1TYPE;
			tableName = ODORxOp.TABLE_OP;
			tag = ODORxOp.AMT_TAG;
			rxTag = ODORxOp.OP_RX;
			break;
		// ��ҩ
		case ODORxMed.TABBEDPANE_INDEX:
			parm.setData("ADM_TYPE",odoMainReg.admType);//yanjing,�ż������֣�20130716
			parm.setData("RX_TYPE", ODORxMed.MED);
			orderCat1Type = ODORxMed.ORDERCAT1TYPE;
			tableName = ODORxMed.TABLE_MED;
			tag = ODORxMed.AMT_TAG;
			rxTag = ODORxMed.MED_RX;
			break;
		// ��ҩ
		case ODORxChnMed.TABBEDPANE_INDEX:
			parm.setData("RX_TYPE", ODORxChnMed.CHN);
			orderCat1Type = ODORxChnMed.ORDERCAT1TYPE;
			tableName = ODORxChnMed.TABLE_CHN;
			tag = ODORxChnMed.AMT_TAG;
			rxTag = ODORxChnMed.CHN_RX;
			if (StringUtil.isNullString(odoMainControl
					.getValueString("CHN_EXEC_DEPT_CODE"))) {
				odoMainControl.messageBox("E0053");
				return;
			}
			break;
		// ����ҩƷ
		case ODORxCtrl.TABBEDPANE_INDEX:
			parm.setData("RX_TYPE", ODORxCtrl.CTRL);
			orderCat1Type = ODORxCtrl.ORDERCAT1TYPE;
			tableName = ODORxCtrl.TABLE_CTRL;
			tag = ODORxCtrl.AMT_TAG;
			rxTag = ODORxCtrl.CTRL_RX;
			break;
		}
		TParm result = (TParm) odoMainControl.openDialog(
				URLCOMMONORDERQUOTE, parm);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		// ============pangben 2012-7-12 ��ӹܿ�
		if (!odoMainOpdOrder.getCheckRxNo()) {
			return;
		}
		//deleteLisPosc = false;// ������ɾ���ܿ� HL7�˷Ѳ����޸�ע��
		if (ODORxChnMed.CHN_RX.equalsIgnoreCase(rxTag)) {
			// ������ҽҽ��
			if (!odoMainOpdOrder.checkPhaIsSave(ODORxChnMed.CHN_RX,"CHN")) {
				return;
			}
			odoMainOpdOrder.odoRxChn.insertOrder(result);
		} else if (!ODORxExa.EXA_RX.equalsIgnoreCase(rxTag)) {
			// ����ҽ��
			if (!odoMainOpdOrder.checkPhaIsSave(ODORxMed.MED_RX,"MED")) {
				return;
			}
			odoMainOpdOrder.insertOrder(tableName, tag, orderCat1Type, result, rxTag);
		} else {
			int count = result.getCount("ORDER_CODE");
			for (int i = 0; i < count; i++) {
				odoMainOpdOrder.odoRxExa.insertPack(result.getRow(i), true);
			}

		}
	}

	/**
	 * ���ÿ��ҳ���ҽ��
	 */
	public void onDeptOrder() throws Exception{
		TParm parm = new TParm();
		parm.setData("DEPT_DR", "1");
		if (ODOMainReg.O.equalsIgnoreCase(odoMainReg.admType)) {
			parm.setData("FIT", "OPD_FIT_FLG");
		} else {
			parm.setData("FIT", "EMG_FIT_FLG");
		}
		TTabbedPane tabPane = (TTabbedPane) odoMainControl.getComponent(ODOMainOpdOrder.TAGTTABPANELORDER);
		int index = tabPane.getSelectedIndex();
		String orderCat1Type = NULLSTR;
		String tableName = NULLSTR;
		String tag = NULLSTR;
		String rxTag = NULLSTR;
		// ѡ���ĸ�ҳǩ
		switch (index) {
		// ������
		case ODORxExa.TABBEDPANE_INDEX:
			parm.setData("RX_TYPE", ODORxExa.EXA);
			orderCat1Type = ODORxExa.ORDERCAT1TYPE;
			tableName = ODORxExa.TABLE_EXA;
			tag = ODORxExa.AMT_TAG;
			rxTag = ODORxExa.EXA_RX;
			break;
		// ����
		case ODORxOp.TABBEDPANE_INDEX:
			parm.setData("RX_TYPE", ODORxOp.OP);
			orderCat1Type = ODORxOp.ORDERCAT1TYPE;
			tableName = ODORxOp.TABLE_OP;
			tag = ODORxOp.AMT_TAG;
			rxTag = ODORxOp.OP_RX;
			break;
		// ��ҩ
		case ODORxMed.TABBEDPANE_INDEX:
			parm.setData("ADM_TYPE",odoMainReg.admType);//yanjing,�ż������֣�20130716
			parm.setData("RX_TYPE", ODORxMed.MED);
			orderCat1Type = "PHA_W";
			tableName = ODORxMed.TABLE_MED;
			tag = ODORxMed.AMT_TAG;
			rxTag = ODORxMed.MED_RX;
			break;
		// ��ҩ
		case ODORxChnMed.TABBEDPANE_INDEX:
			parm.setData("RX_TYPE", ODORxChnMed.CHN);
			orderCat1Type = ODORxChnMed.ORDERCAT1TYPE;
			tableName = ODORxChnMed.TABLE_CHN;
			tag = ODORxChnMed.AMT_TAG;
			rxTag = ODORxChnMed.CHN_RX;
			if (StringUtil.isNullString(odoMainControl
					.getValueString("CHN_EXEC_DEPT_CODE"))) {
				odoMainControl.messageBox("E0053");
				return;
			}
			break;
		// ����ҩƷ
		case ODORxCtrl.TABBEDPANE_INDEX:
			parm.setData("RX_TYPE", ODORxCtrl.CTRL);
			orderCat1Type = "PHA_W";
			tableName = ODORxCtrl.TABLE_CTRL;
			tag = ODORxCtrl.AMT_TAG;
			rxTag = ODORxCtrl.CTRL_RX;
			break;
		}
		TParm result = (TParm) odoMainControl.openDialog(
				URLCOMMONORDERQUOTE, parm);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		// ============pangben 2012-7-12 ��ӹܿ�
		if (!odoMainOpdOrder.getCheckRxNo()) {
			return;
		}
		//deleteLisPosc = false;// ������ɾ���ܿ� HL7�˷Ѳ����޸�ע��
		if (ODORxChnMed.CHN_RX.equalsIgnoreCase(rxTag)) {
			// ������ҽҽ��
			if (!odoMainOpdOrder.checkPhaIsSave(ODORxChnMed.CHN_RX,"CHN")) {
				return;
			}
			odoMainOpdOrder.odoRxChn.insertOrder(result);
		} else if (!ODORxExa.EXA_RX.equalsIgnoreCase(rxTag)) {
			// ����ҽ��
			if (!odoMainOpdOrder.checkPhaIsSave(ODORxMed.MED_RX,"MED")) {
				return;
			}
			odoMainOpdOrder.insertOrder(tableName, tag, orderCat1Type, result, rxTag);
		} else {
			int count = result.getCount("ORDER_CODE");
			for (int i = 0; i < count; i++) {
				odoMainOpdOrder.odoRxExa.insertPack(result.getRow(i), true);
			}
		}
	}

	/**
	 * ���ÿƳ���ģ��
	 */
	public void onDeptPack() throws Exception{
		TParm parm = new TParm();
		parm.setData("DEPT_OR_DR", "1");
		parm.setData("DEPTORDR_CODE", Operator.getDept());
		parm.setData("ADM_TYPE", odoMainReg.admType);//yanjing,�ż������֣�20130614
		TParm result = (TParm) odoMainControl.openDialog(
				URLOPDCOMPACKQUOTE, parm, false);
		if(null==result){
			return;
		}
		
		// ҩƷ 
		TParm orderResult = ((TParm) result.getData("ORDER"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			
			if (Operator.getSpcFlg().equals("Y")) {//pangben 2013-5-17���������У���Ƿ�ǰ����ǩ�Ѿ����
				if (!odoMainControl.odoMainSpc.checkSpcPha(odoMainControl.odo.getOpdOrder())) {
					return;
				}
			}else{
				if (!odoMainOpdOrder.checkPhaIsSave(ODORxMed.MED_RX,"MED")) {
					return;
				}
			}
		}
		
		
		// ============pangben 2012-7-12 ��ӹܿ�
		String resultString = odoMainOpdOrder.getCheckRxNoSum(result);
		if (null!=resultString) {
			odoMainControl.messageBox(resultString);
			return;
		}
		odoMainOpdOrder.insertPack(result);
	}

	/**
	 * ����ҽʦ����ģ��
	 */
	public void onDrPack() throws Exception{
		TParm parm = new TParm();
		parm.setData("DEPT_OR_DR", "2");
		parm.setData("ADM_TYPE",odoMainReg.admType);//yanjing,�ż������֣�20130614
		parm.setData("DEPTORDR_CODE", Operator.getID());
		TParm result = (TParm) odoMainControl.openDialog(
				URLOPDCOMPACKQUOTE, parm, false);
		if(null==result){
			return;
		}
		
		
		// ҩƷ 
		TParm orderResult = ((TParm) result.getData("ORDER"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			
			if (Operator.getSpcFlg().equals("Y")) {//���������У���Ƿ�ǰ����ǩ�Ѿ����
				if (!odoMainControl.odoMainSpc.checkSpcPha(odoMainControl.odo.getOpdOrder())) {
					return;
				}
			}else{
				if (!odoMainOpdOrder.checkPhaIsSave(ODORxMed.MED_RX,"MED")) {
					return;
				}
			}
		}
		
		// ============pangben 2012-7-12 ��ӹܿ�
		String resultString = odoMainOpdOrder.getCheckRxNoSum(result);
		if (null!=resultString) {
			odoMainControl.messageBox(resultString);
			return;
		}
		odoMainOpdOrder.insertPack(result);
	}

	/**
	 * ����ҽʦ�������
	 */
	public void onDrDiag() throws Exception{
		String param = "2," + Operator.getID();
		if (StringTool.getBoolean(odoMainControl.getValueString("W_FLG"))) {
			param += ",W";
		} else {
			param += ",C";
		}
		TParm result = (TParm) odoMainControl.openDialog(
				URLODOCOMMONICDQUOTE, param);
		if (result == null || result.getCount("ICD_CODE") < 1) {
			return;
		}
		insertDiag(result);
	}

	/**
	 * ����ҽ�������ײ������
	 *
	 * @param diagParm
	 *            TParm��ϼ���
	 */
	private void insertDiag(TParm diagParm)throws Exception {
		int count = diagParm.getCount("ICD_CODE");
		Diagrec diag = odoMainControl.odo.getDiagrec();
		int row = diag.rowCount() - 1;
		TTable table = (TTable) odoMainControl.getComponent(ODOMainOther.TABLEDIAGNOSIS);
		for (int i = 0; i < count; i++) {

			if (!StringUtil.isNullString(diag.getItemString(
					diag.rowCount() - 1, "ICD_CODE"))) {
				row = diag.insertRow(-1);
			}
			if (diag.isHaveSameDiag(diagParm.getValue("ICD_CODE", i))) {
				odoMainControl.messageBox("E0041");
				continue;
			}
			if (!OdoUtil.isAllowDiag(diagParm.getRow(i), Operator.getDept(),
					odoMainPat.pat.getSexCode(), odoMainPat.pat.getBirthday(), (Timestamp) odoMainControl
							.getValue("ADM_DATE"))) {
				odoMainControl.messageBox("E0042"); // liudy
				diag.deleteRow(row);
				table.acceptText();
				table.getTable().grabFocus();
				table.setSelectedRow(0);
				table.setSelectedColumn(1);
				row = table.addRow();
				table.setDSValue();
				continue;
			}

			diag.setItem(row, "ICD_TYPE", odoMainOpdOrder.wc);
			diag.setItem(row, "ICD_CODE", diagParm.getValue("ICD_CODE", i));
			diag.setItem(row, "ORDER_DATE", diag.getDBTime());
			diag.setActive(row, true);
			if (!diag.haveMainDiag(new int[1])) {
				if (ODOMainOpdOrder.C.equalsIgnoreCase(odoMainOpdOrder.wc)
						&& !OdoUtil.isAllowChnDiag(diagParm.getRow(i))) {
					odoMainControl.messageBox("E0018");
					diag.deleteRow(row);
					// odo.getDiagrec().insertRow();
					table.acceptText();
					table.getTable().grabFocus();
					table.setSelectedRow(0);
					table.setSelectedColumn(1);
					table.addRow();
					table.setDSValue();
					return;
				}

				diag.setItem(row, "MAIN_DIAG_FLG", "Y");
			} else {
				diag.setItem(row, "MAIN_DIAG_FLG", "N");
			}

		}
		if (!StringUtil.isNullString(diag.getItemString(diag.rowCount() - 1,
				"ICD_CODE")))
			row = diag.insertRow(diag.rowCount());
		table.setDSValue();
		table.getTable().grabFocus();
		table.setSelectedRow(row);
		table.setSelectedColumn(1);
	}

	/**
	 * ���ÿƳ������
	 */
	public void onDeptDiag() throws Exception{
		String param = "1," + Operator.getDept();
		if (StringTool.getBoolean(odoMainControl.getValueString("W_FLG"))) {
			param += ",W";
		} else {
			param += ",C";
		}
		TParm result = (TParm) odoMainControl.openDialog(
				URLODOCOMMONICDQUOTE, param);
		// System.out.println("result="+result);
		if (result == null || result.getCount("ICD_CODE") < 1) {
			return;
		}
		insertDiag(result);
	}

	/**
	 * ��ʾ���ñ�����
	 */
	public void onShowQuoteSheet() throws Exception{
		TTabbedPane orderPane = (TTabbedPane) odoMainControl
				.getComponent(ODOMainOpdOrder.TAGTTABPANELORDER);
		if (orderPane.getSelectedIndex() != 0) {
			// ======pangben 2013-4-25 ���Ĭ��ѡ�м�����ҳǩ�����������ҳǩʱ����ʾ��Ϣ
			orderPane.setSelectedIndex(0);// ===Ĭ����ҩҳǩ
			odoMainOpdOrder.onChangeOrderTab();// ҳǩ�л�
		}
		TWindow window = (TWindow) odoMainControl.openWindow(
				URLSYSEXASHEETTREE, odoMainOpdOrder, true);
		window.setVisible(true);
	}

	/**
	 * �������뵥
	 */
	public void onEmr(){
		if (odoMainControl.odo == null)
			return;
		// �õ����뵥�����
		TParm emrParm = new TParm();
		TParm actionParm = new TParm();
		if (odoMainReg.admType.equals("O")) {
			emrParm = OrderUtil.getInstance().getOrderPasEMRAllM(
					 odoMainControl.odo.getOpdOrder(), "ODO");
			actionParm.setData("SYSTEM_CODE", "ODO");
		} else if (odoMainReg.admType.equals("E")) {
			emrParm = OrderUtil.getInstance().getOrderPasEMRAllM(
					 odoMainControl.odo.getOpdOrder(), "EMG");
			actionParm.setData("SYSTEM_CODE", "EMG");
		}
		TParm result=ODOTool.getInstance().getPrintExaDataSum(odoMainControl.odo.getCaseNo(),true);
		if (emrParm.getInt("ACTION", "COUNT") > 0||(!"".equals(emrParm.getValue("MED_APPLY"))&&emrParm.getValue("MED_APPLY")!=null)) {
			actionParm.setData("ADM_TYPE", odoMainReg.admType);
			actionParm.setData("MR_NO", odoMainPat.pat.getMrNo());
			if ("en".equals(odoMainControl.getLanguage())) // �����Ӣ�� ��ôȡӢ������
				actionParm.setData("PAT_NAME", odoMainPat.pat.getName1());
			else
				actionParm.setData("PAT_NAME", odoMainPat.pat.getName());
			actionParm.setData("CASE_NO", odoMainControl.odo.getCaseNo());
			actionParm.setData("IPD_NO", "");
			actionParm.setData("ADM_DATE", odoMainControl.odo.getRegPatAdm().getItemData(0,
					"ADM_DATE"));
			actionParm.setData("DEPT_CODE", Operator.getDept());
			actionParm.setData("STYLETYPE", "1");
			actionParm.setData("RULETYPE", "2");
			actionParm.setData("EMR_DATA_LIST", emrParm);
			actionParm.setData("CParm", result);
			// System.out.println("for Emr parm"+actionParm);
			// this.openDialog("%ROOT%\\config\\emr\\EMRSingleUI.x",actionParm);
			odoMainControl.openWindow(URLEMRSINGLEUI, actionParm);
		}
	}

	/**
	 * �����¼
	 */
	public void onCaseHistory() throws Exception{

		//---
		

		if (odoMainControl.odo == null) {
			return;
		}
		//======pangben 2012-7-24 ���Ĭ��ѡ����ҩҳǩ������ڼ�����ҳǩʱ��ҩƷҳǩ���ּ�������������
		TTabbedPane orderPane = (TTabbedPane) odoMainControl.getComponent(ODOMainOpdOrder.TAGTTABPANELORDER);
		if (orderPane.getSelectedIndex()!=2) {
			orderPane.setSelectedIndex(2);//===Ĭ����ҩҳǩ
			odoMainOpdOrder.onChangeOrderTab();//ҳǩ�л�
		}
		Object obj = odoMainControl.openDialog(URLOPDCASEHISTORY,
				odoMainControl.odo.getMrNo());
		if (obj == null) {
			return;
		}
		if (!(obj instanceof TParm)) {
			return;
		}
		if (Operator.getSpcFlg().equals("Y")) {//���������У���Ƿ�ǰ����ǩ�Ѿ����
			if (!odoMainControl.odoMainSpc.checkSpcPha(odoMainControl.odo.getOpdOrder())) {
				return;
			}
		}else{//=======pangben 2014-1-15
			if (!odoMainOpdOrder.checkPhaIsSave(ODORxMed.MED_RX,"MED")) {
				return;
			}
		}
		
		//�����Ƿ��ӡ
		String printuser =  EmrUtil.getInstance().getGSFile(odoMainControl.odo.getCaseNo())[3];
		System.out.println("--------11111printuser11111------"+printuser);
		//odoMainControl.messageBox("++++++!"+printuser);
		if( !StringUtil.isNullString(printuser) ){
			odoMainControl.messageBox("�����Ѵ�ӡ�������¼��ֹ�ش�!");
			return;
		}
		//
		
		//=============pangben 2012-7-24 ��ӹܿ�
//		if (!odoMainOpdOrder.getCheckRxNo()) {
//			return;
//		}
		//deleteLisPosc = false;// =============pangben 2012-6-15
		TParm result = (TParm) obj;
		String sub = result.getValue("SUB");
		String objStr = result.getValue("OBJ");
		String phy = result.getValue("PHY");
		String exaR = result.getValue("EXA_R");// =========pangben 2012-6-28 ��Ӽ����\����ش�ֵ��ʾ
		String pro = result.getValue("PRO");
		// ======xueyf start �����ֲ�ʷ�ش�����
		if (odoMainOther.word != null) {
			if (!StringUtil.isNullString(sub)) {
				sub = odoMainOther.word.getCaptureValue("SUB")+sub; //add by huangtt 20150610
				odoMainOther.word.clearCapture("SUB");
				odoMainOther.word.pasteString(sub);
			}
			if (!StringUtil.isNullString(objStr)) {
				objStr = odoMainOther.word.getCaptureValue("OBJ")+objStr; //add by huangtt 20150610
				odoMainOther.word.clearCapture("OBJ");
				odoMainOther.word.pasteString(objStr);
			}
			if (!StringUtil.isNullString(phy)) {
				phy ="\r\n"+"         "+phy; //add by huangtt 20150610
				ECapture capture = odoMainOther.word.findCapture("PHY");			
				ECapture endCapture = capture.getEndCapture();
				endCapture.setFocus();
				odoMainOther.word.pasteString(phy);
				
				
			}
			// �����
			if (!StringUtil.isNullString(exaR)) {
				exaR = odoMainOther.word.getCaptureValue("EXA_RESULT")+exaR; //add by huangtt 20150610
				odoMainOther.word.clearCapture("EXA_RESULT");
				odoMainOther.word.pasteString(exaR);
			}
			// ����
			if (!StringUtil.isNullString(pro)) {
				pro = odoMainOther.word.getCaptureValue("PROPOSAL")+pro; //add by huangtt 20150610
				odoMainOther.word.clearCapture("PROPOSAL");
				odoMainOther.word.pasteString(pro);
			}
		}
		// ======xueyf stop
		// ȡ�����
		TParm diagResult = ((TParm) result.getData("DIAG"));
		if (diagResult != null) {
			int count = diagResult.getCount("ICD_CODE");
			for (int i = 0; i < count; i++) {
				odoMainOther.tblDiag.setSelectedRow(odoMainOther.tblDiag.getRowCount() - 1);
				odoMainOther.popDiagReturn(NULLSTR, diagResult.getRow(i));
			}
		}
		odoMainOpdOrder.onCaseHistoryInsertPack(result);
	}

	/**
	 * �����ײ�
	 * =======zhangp 2014
	 */
	public void onPack() throws Exception{
		TParm parm = new TParm();
		parm.setData("MR_NO", odoMainControl.odo.getMrNo());
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String recentFilter = order.getFilter();
		order.setFilter("");
		order.filter();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < order.rowCount(); i++) {
			String memPackageId = order.getItemString(i, "MEM_PACKAGE_ID");
			if(memPackageId.length() > 0 && !list.contains(memPackageId)){
				list.add(memPackageId);
			}
		}
		order.setFilter(recentFilter);
		order.filter();
		parm.setData("IDS", list);
		parm.setData("TYPE", "MZYSZ");
		TParm result = (TParm) odoMainControl.openDialog(
				URLDOCTORTCSZ, parm, false);
		if(null==result){
			return;
		}
		TParm orderResult = ((TParm) result.getData("ORDER"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			if (Operator.getSpcFlg().equals("Y")) {//���������У���Ƿ�ǰ����ǩ�Ѿ����
				if (!odoMainControl.odoMainSpc.checkSpcPha(odoMainControl.odo.getOpdOrder())) {
					return;
				}
			}else{
				if (!odoMainOpdOrder.checkPhaIsSave(ODORxMed.MED_RX,"MED")) {
					return;
				} 
			}
		}
		// ============pangben 2012-7-12 ��ӹܿ�
		String resultString = odoMainOpdOrder.getCheckRxNoSum(result);
		if (null!=resultString) {
			odoMainControl.messageBox(resultString);
			return;
		}
		odoMainOpdOrder.insertPack(result);
	}

	public void onSaveMemPackage() throws Exception{
		TParm result = ODOTool.getInstance().updateMemPackage(odoMainControl.caseNo);
		if(result.getErrCode()<0){
			odoMainControl.messageBox("�����ײ�ʧ��");
		}
	}
}
