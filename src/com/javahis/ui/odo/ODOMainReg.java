package com.javahis.ui.odo;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import jdo.device.CallNo;
import jdo.ekt.EKTpreDebtTool;
import jdo.odo.ODO;
import jdo.odo.OpdOrder;
import jdo.odo.PatInfo;
import jdo.odo.RegPatAdm;
import jdo.opb.OPB;
import jdo.opd.OPDSysParmTool;
import jdo.reg.ClinicRoomTool;
import jdo.reg.PatAdmTool;
import jdo.reg.REGAdmForDRTool;
import jdo.reg.REGSysParmTool;
import jdo.reg.REGTool;
import jdo.reg.Reg;
import jdo.reg.SessionTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.root.client.SocketLink;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.combo.TComboSession;
import com.javahis.util.DateUtil;
import com.javahis.util.EmrUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * 
 * Title: ����ҽ������վ�ҺŶ���
 * </p>
 * 
 * <p>
 * Description:����ҽ������վ�ҺŶ���
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODOMainReg {
	
	public OdoMainControl odoMainControl;
	public ODOMainOpdOrder odoMainOpdOrder;
	public ODOMainTjIns odoMainTjIns;
	public ODOMainPat odoMainPat;
	public ODOMainOther odoMainOther;
	Reg reg;// reg����
	
	private TTable tblPat;
	public final static String TABLEPAT = "TABLEPAT";
	public static boolean  selectPat = false;// ���ò��Ҳ��˷����Ŀ��أ���ĳЩ�����ֵʱ����Щ�����ĵ�������е���selectPat�������ͻ�ݹ�
	public TParm parmpat;// ������Ϣ
	
	private TComboSession t;// ����ʱ��combo
	private TComboBox clinicroom, dr, insteadDept;// ���ң�ҽʦ�����ﲿ��
	public String admType; // liudy // �ż�ס��
	public TParm regSysEFFParm;//pangben 2013-4-28 �Һ���Ч����
	private Thread erdThread;// ���ﲡ���б��ɫ���߳�
	Map map; // liudy // tableʹ�õ��е���ɫ����
	public String realDeptCode; // ��������
	private int PatSelectRow; //===huangtt 20131128 ˫�������б�ʱѡ�еĵ�ǰ��
	private boolean aheadFlg = true;
	TParm clinicroomParm;
	
	private static final String OHEADER =  "���,40;������,100;����,80;������,50,VISIT_CODE;������,50,FIRST_FLG;����״̬,80,ADM_STATUS;����״̬,80,REPORT_STATUS";
	private static final String OENHEADER = "QueNo;PatNo;Name;visitName;Status;ReportStatus;FirstNext";
	private static final String OPARMMAP = "QUE_NO;MR_NO;PAT_NAME;VISIT_CODE;FIRST_FLG;ADM_STATUS;REPORT_STATUS";
	private static final String OLANGUAGEMAP = "PAT_NAME|PAT_NAME1";
	private static final String OLOCKCOLUMNS = "0,1,2,3,4,5";
	private static final String OCOLUMNHORIZONTALALIGNMENTDATA = "0,right;1,right;2,left;3,left;4,left;5,left;6,left";
	private static final String EHEADER = "���,40;������,100;����,80;������,50,VISIT_CODE;���˵ȼ�,80,ERD_LEVEL;������,120,DISE_CODE;��Ժʱ��,120,Timestamp,yyyy-MM-dd HH:mm;����ҽ��,120,Timestamp,yyyy-MM-dd HH:mm;����״̬,80,ADM_STATUS;����״̬,80,REPORT_STATUS";
	private static final String EENHEADER = "QueNo;PatNo;PatName;visitName;TriageLevel;Single Disease;Arrived Date;Latest OrderDate;Status;ReportStatus";
	private static final String EPARMMAP = "QUE_NO;MR_NO;PAT_NAME;VISIT_CODE;ERD_LEVEL;DISE_CODE;REG_DATE;ORDER_DATE;ADM_STATUS;REPORT_STATUS;ADM_DATE";
	private static final String ELANGUAGEMAP = "PAT_NAME|PAT_NAME1";
	private static final String ELOCKCOLUMNS = "all";
	private static final String ECOLUMNHORIZONTALALIGNMENTDATA = "0,right;2,left;3,left;4,left;5,left;7,left;8,left;9,left";
	private static final String CLINICROOMPARMMAPEN = "id:ID;name:ENG_DESC";
	private static final String CLINICROOMPARMMAPCN = "id:ID;name:NAME;";
	private static final String DRPARMMAPEN = "id:ID;name:USER_ENG_NAME;Py1:PY1";
	private static final String DRPARMMAPCN = "id:ID;name:NAME;Py1:PY1";
	
	private static final String URLREGADMFORCR = "%ROOT%\\config\\reg\\REGAdmForClinicRoom.x";
	private static final String URLREGADMFORDR = "%ROOT%\\config\\reg\\REGAdmForDr.x";
	private static final String URLOPDABNORMALREG = "%ROOT%\\config\\opd\\OPDAbnormalReg.x";
	private static final String URLOPDEMR = "%ROOT%\\config\\opd\\OPDEMRQuery.x";
	
	public static final String MEGOVERTIME = "�ò������˹ң����ܱ�������"; 
	
	public static final String O = "O";//����
	public static final String E = "E";//����
	private static final String NULLSTR = "";
	public int i = 0; // liudy // �ż�ס��
	/**
	 * Socket��������ҩ������
	 */
	private SocketLink client1;
	/**
	 * ����
	 * @param odoMainControl
	 */
	public ODOMainReg(OdoMainControl odoMainControl){
		this.odoMainControl = odoMainControl;
	}
	
	/**
	 * �Һų�ʼ��
	 * @throws Exception
	 */
	public void onInit()throws Exception{
		this.odoMainOpdOrder = odoMainControl.odoMainOpdOrder;
		this.odoMainTjIns = odoMainControl.odoMainTjIns;
		this.odoMainPat = odoMainControl.odoMainPat;
		this.odoMainOther = odoMainControl.odoMainOther;
		t = (TComboSession) odoMainControl.getComponent("SESSION_CODE");
		clinicroom = (TComboBox) odoMainControl.getComponent("CLINICROOM");
		dr = (TComboBox) odoMainControl.getComponent("INSTEAD_DR");
		insteadDept = (TComboBox) odoMainControl.getComponent("INSTEAD_DEPT");
		admType = (String) odoMainControl.getParameter();
		if(admType.equals(O)){
			odoMainControl.callFunction("UI|singledise|setEnabled", false);//add by wanglong 20121119
		}
		t.setAdmType(admType);
		t.onQuery();
		// ��ʼ�������б�
		initOPD();
		// ��ʼ���ż���
		initOE();
		// �жϿ���Ȩ��
		if (odoMainControl.getPopedem("DEPT_POPEDEM"))
			onSelectPat("INSTEAD_DEPT");
		else
			// ��TABLE������
			onSelectPat(NULLSTR);
		SynLogin("1"); // �кŵ�½
		regSysEFFParm = REGTool.getInstance().getRegParm();//pangben 2013-4-28 �Һ���Ч����
		aheadFlg = REGSysParmTool.getInstance().selAeadFlg();
	}
	
	/**
	 * ��ʼ�������б�
	 */
	public void initOPD() throws Exception{
		if (odoMainControl.getParameter() == null) {
			odoMainControl.messageBox("E0024"); // ��ʼ������ʧ��
			return;
		}
		String sessionCode = initSessionCode();
		Timestamp admDate = TJDODBTool.getInstance().getDBTime();
		// ����ʱ���ж�Ӧ����ʾ�����ڣ����������0������⣬���0������Ӧ����ʾǰһ������ڣ�
		if (!StringUtil.isNullString(sessionCode)
				&& !StringUtil.isNullString(admType)) {
			admDate = SessionTool.getInstance().getDateForSession(admType,
					sessionCode, Operator.getRegion());
		}
		odoMainControl.setValue("ADM_DATE", admDate);
		initClinicRoomCombo();
		String roomNo = PatAdmTool.getInstance().getClinicRoomByRealDept(
				StringTool.getString(admDate, "yyyy-MM-dd"), sessionCode,
				admType, Operator.getID(), Operator.getDept())
				.getValue("ID", 0);
		clinicroom.setValue(roomNo);
		selectPat = true;
		initORadio();
		initInstradCombo();
	}
	
	/**
	 * �ż������𣬼��ﲡ���б������˵ȼ�����Ժʱ����ֶ�
	 */
	public void initOE() throws Exception{
		TButton erd = (TButton) odoMainControl.getComponent("ERD");
		TButton bodyTemp = (TButton) odoMainControl.getComponent("BODY_TEMP");
		TButton orderSheet = (TButton) odoMainControl.getComponent("ORDER_SHEET");
		TButton erdSheet = (TButton) odoMainControl.getComponent("ERD_SHEET");
		tblPat = (TTable) odoMainControl.getComponent(TABLEPAT);
		// ==========pangben 2012-6-28 ��ӳ�������ֵVISIT_CODE
		if (O.equalsIgnoreCase(admType)) {
			odoMainControl.setTitle("����ҽ��վ");
			tblPat.setHeader(OHEADER);
			tblPat.setParmMap(OPARMMAP);
			tblPat.setEnHeader(OENHEADER);
			tblPat.setLanguageMap(OLANGUAGEMAP);
			tblPat.setLockColumns(OLOCKCOLUMNS);
			tblPat.setColumnHorizontalAlignmentData(OCOLUMNHORIZONTALALIGNMENTDATA);
			erd.setEnabled(false);
			bodyTemp.setEnabled(false);
			orderSheet.setEnabled(false);
			erdSheet.setEnabled(true);
			return;
		}
		//===================== modify by wanglong 20121024 =====================================
		odoMainControl.setTitle("����ҽ��վ");
		tblPat.setHeader(EHEADER);
		tblPat.setEnHeader(EENHEADER);
		tblPat.setParmMap(EPARMMAP);
		tblPat.setLanguageMap(ELANGUAGEMAP);
		tblPat.setLockColumns(ELOCKCOLUMNS);//modify by wanglong 20121119
		tblPat.setColumnHorizontalAlignmentData(ECOLUMNHORIZONTALALIGNMENTDATA);
		//===================== modify end ======================================================
		erd.setEnabled(true);
		bodyTemp.setEnabled(true);
		orderSheet.setEnabled(true);
		erdSheet.setEnabled(true);
	}

	/**
	 * ����SESSION combo���ż����ԣ������ص�ǰ��SESSION_CODE
	 * 
	 * @return String sessionCode
	 */
	public String initSessionCode() throws Exception{
		// Ϊ�˽����SESSION_CODE��ʾ�ż������𣬷���һ������ʾ��TEXTFIELD��
		String sessionCode = SessionTool.getInstance().getDefSessionNow(
				admType, Operator.getRegion());
		odoMainControl.setValue("SESSION_CODE", sessionCode);
		return sessionCode;
	}
	
	/**
	 * ��ʼ�����combo
	 */
	public void initClinicRoomCombo()throws Exception {
		Timestamp admDate = (Timestamp) odoMainControl.getValue("ADM_DATE");
		String sessionCode = odoMainControl.getValueString("SESSION_CODE");
		TParm comboParm = PatAdmTool.getInstance().getClinicRoomForODO(
				StringTool.getString(admDate, "yyyy-MM-dd"), sessionCode,
				admType, Operator.getDept(), Operator.getID());
		if ("en".equals(odoMainControl.getLanguage()))
			clinicroom.setParmMap(CLINICROOMPARMMAPEN);
		else
			clinicroom.setParmMap(CLINICROOMPARMMAPCN);
		clinicroomParm = comboParm;
		clinicroom.setParmValue(comboParm);
	}
	
	/**
	 * ��ʼ�������ݴ���ɵ�radioButton
	 */
	public void initORadio() throws Exception{
		String date = StringTool.getString((Timestamp) odoMainControl
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		String sql = "SELECT CASE_NO,SEE_DR_FLG " + "	FROM REG_PATADM "
				+ "	WHERE  ADM_TYPE='" + admType + "' AND ADM_DATE=TO_DATE('"
				+ date + "','YYYY-MM-DD') " + "  AND SESSION_CODE='"
				+ odoMainControl.getValue("SESSION_CODE") + "' AND REALDR_CODE='"
				+ Operator.getID() + "' AND CLINICROOM_NO='"
				+ odoMainControl.getValueString("CLINICROOM") + "' AND REGION_CODE = '"
				+ Operator.getRegion() + "'" + "  AND REGCAN_USER IS NULL ";
		TParm regSysParm = REGSysParmTool.getInstance().selectdata();
		if (regSysParm.getBoolean("CHECKIN_FLG", 0)) {
			sql += "AND ARRIVE_FLG='Y'";
		}
		TDataStore radioNo = new TDataStore();
		radioNo.setSQL(sql);
		if (radioNo.retrieve() == -1) {
			odoMainControl.messageBox("E0024"); // ��ʼ������ʧ��
			return;
		}
		radioNo.setFilter(" SEE_DR_FLG='N'");
		radioNo.filter();
		TLabel label = (TLabel) odoMainControl.getComponent("WAIT_NO");
		label.setZhText(radioNo.rowCount() + " ��");
		label.setEnText(radioNo.rowCount() + " P(s)");
		label.changeLanguage(Operator.getLanguage());
		radioNo.setFilter(" SEE_DR_FLG='Y'");
		radioNo.filter();
		label = (TLabel) odoMainControl.getComponent("DONE_NO");
		label.setZhText(radioNo.rowCount() + " ��");
		label.setEnText(radioNo.rowCount() + " P(s)");
		label.changeLanguage(Operator.getLanguage());
		radioNo.setFilter("SEE_DR_FLG='T'");
		radioNo.filter();
		label = (TLabel) odoMainControl.getComponent("TEMP_NO");
		label.setZhText(radioNo.rowCount() + " ��");
		label.setEnText(radioNo.rowCount() + " P(s)");
		label.changeLanguage(Operator.getLanguage());
	}
	
	/**
	 * ��ʼ��ҽ��վ������Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @throws Exception 
	 */
	public void initOpd(TParm parm, int row) throws Exception {
		odoMainControl.caseNo = parm.getValue("CASE_NO", row);// ==========pangben modify
		// 20110914
		initReg(parm.getValue("MR_NO", row), parm.getValue("CASE_NO", row));
		// ��ʼ��odo����
		odoMainControl.odo = new ODO(parm.getValue("CASE_NO", row), parm
				.getValue("MR_NO", row), Operator.getDept(), Operator.getID(),
				parm.getValue("ADM_TYPE", row));
//		String odo_type = "ODO";
//		if (E.equals(admType)) {
//			odo_type = "ODE";
//		}
		this.odoMainPat = odoMainControl.odoMainPat;
//		if (!odoMainPat.onLockPat(odo_type)) {
//			odoMainControl.odo = null;
//			return;
//		}
		realDeptCode = parm.getValue("REALDEPT_CODE", row);
		odoMainControl.odo.getOpdOrder().addEventListener(OpdOrder.ACTION_SET_ITEM,
				this, "onSetItemEvent");
		odoMainControl.mp.onDoubleClicked(true);
		if (!odoMainControl.odo.onQuery()) {
			odoMainControl.messageBox("E0024"); // ��ʼ������ʧ��
			//deleteLisPosc = false;
			return;
		}
		
		//����С��16�꣨��16�꣩����������Ϣ�����ж� add by huangtt 20180911
		String age = this.odoMainPat.patAge(parm.getTimestamp("BIRTH_DATE", row));
		if(checkWeight(age,odoMainControl.odo.getRegPatAdm().getItemString(0, "WEIGHT"))){
			odoMainControl.messageBox("���ξ������������ݣ��뼰ʱ¼�룡");
		}
		
		// ��ʼ��pat����
		odoMainPat.initPatInfo(parm, row);
		odoMainControl.initPanel();
		odoMainOther.setRootPanelWidth();
		odoMainOpdOrder.phaCode = ClinicRoomTool.getInstance().getOrgByOdo(
				Operator.getRegion(),
				StringTool.getString((Timestamp) odoMainControl.getValue("ADM_DATE"),
						"yyyyMMdd"), odoMainControl.getValueString("SESSION_CODE"),
				admType, parm.getValue("REALDR_CODE", row),
				parm.getValue("REALDEPT_CODE", row),
				parm.getValue("CASE_NO", row)).getValue("ORG_CODE", 0);
		if (!odoMainControl.odo.getOpdOrder().isOrgAvalible(odoMainOpdOrder.phaCode)) {
			odoMainControl.messageBox("E0117"); // ҩ��û�п������ڣ����ܿ���
			odoMainControl.closeWindow();
		}
		odoMainControl.setValue("MED_RBORDER_DEPT_CODE", odoMainOpdOrder.phaCode);
		odoMainControl.setValue("CTRL_RBORDER_DEPT_CODE", odoMainOpdOrder.phaCode);
//		odoMainControl.setValue("OP_EXEC_DEPT", parm.getValue("REALDEPT_CODE", row));
		odoMainControl.setValue("OP_EXEC_DEPT", Operator.getCostCenter());//����ҳǩִ�п��ҳ�ʼ��Ϊ�ɱ����ģ�yanjing 20141128
		if (ODOMainOpdOrder.C.equalsIgnoreCase(odoMainOpdOrder.wc)) {
			odoMainControl.setValue("CHN_EXEC_DEPT_CODE", odoMainOpdOrder.phaCode);
		}
		odoMainPat.lastMrNo = odoMainPat.pat.getMrNo();
	}
	
	
	
	/**
	 * �жϲ���С��16�����û����д����
	 * @param mrNo
	 * @return
	 */
	public boolean checkWeight(String age,String weight){
//		System.out.println("age----"+age);
//		System.out.println("weight----"+weight);
	     int ageShowInt = age.indexOf("��");
	     String age1 = "0";
	     if(ageShowInt > 0){
	    	 age1 =age.substring(0, age.indexOf("��"));
	     }
	     int ageShow = Integer.parseInt(age1);
	     if(ageShow <= 16){
	    	 if(weight == null || Double.parseDouble(weight) <=0 ){
	    		 return true;
	    	 }
	     }
	     return false;
	     
	}
	
	/**
	 * ��ʼ��REG����
	 * 
	 * @param mrNo
	 *            String
	 * @param caseNo
	 *            String
	 */
	public void initReg(String mrNo, String caseNo) throws Exception{
		odoMainPat.pat = Pat.onQueryByMrNo(mrNo);
		reg = Reg.onQueryByCaseNo(odoMainPat.pat, caseNo);
		odoMainControl.opb = OPB.onQueryByCaseNo(reg);// ====pangben 2012-2-28
	}
	
	/**
	 * ��ʼ���������
	 */
	public void initInsteadDept() throws Exception{
		odoMainControl.clearValue("INSTEAD_DEPT;INSTEAD_DR");
		String admDate = StringTool.getString((Timestamp) odoMainControl
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		String sessionCode = odoMainControl.getValueString("SESSION_CODE");
		TParm parm = PatAdmTool.getInstance().getInsteadDept(admType, admDate,
				sessionCode, Operator.getRegion());
		insteadDept.setParmValue(parm);
		odoMainControl.callFunction("UI|INSTEAD_DR|setEnabled", false);
	}
	
	/**
	 * ����Ʊ���ʱ�䣬������ҽʦcombo��Ϊ���ã�����ʼ������ҽʦcombo
	 */
	public void onInsteadDept() throws Exception{
		String dept = odoMainControl.getValueString("INSTEAD_DEPT");
		odoMainControl.clearValue("INSTEAD_DR");
		if (StringUtil.isNullString(dept)) {
			dr.setEnabled(false);
			return;
		}
		dr.setEnabled(true);
		String admDate = StringTool.getString((Timestamp) odoMainControl
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		String sessionCode = odoMainControl.getValueString("SESSION_CODE");
		TParm parm = PatAdmTool.getInstance().getInsteadDrByDept(admType,
				sessionCode, admDate, dept);
		if ("en".equals(odoMainControl.getLanguage()))
			dr.setParmMap(DRPARMMAPEN);
		else
			dr.setParmMap(DRPARMMAPCN);
		dr.setParmValue(parm);
		if (E.equals(admType)) { // ����ʱ ���������ʾ�ÿ��ҵ����в�����Ϣ
			this.onSelectPat("INSTEAD_DEPT");
		}
	}
	
	/**
	 * Ȩ�޳�ʼ�� �Ƿ���Դ���
	 */
	private void initInstradCombo() throws Exception{
		Object obj = odoMainControl.getPopedemParm();
		if (obj == null)
			return;
		TParm parm = (TParm) obj;
		for (int i = 0; i < parm.getCount(); i++) {
			if ("INSTEAD".equals(parm.getValue("ID", i))) {
				odoMainControl.callFunction("UI|INSTEAD_DEPT|setEnabled", true);
			}
		}
	}
	
	
	/**
	 * ��ѡ���ˣ���ʼ��ҽ��վ
	 * @throws Exception 
	 */
	public void onTablePatDoubleClick() throws Exception {
		TTable table = (TTable) odoMainControl.getComponent(TABLEPAT);
		int row = table.getSelectedRow();
		PatSelectRow=row; //add by huangtt 20131128
		//add by huangtt 20140926 start
		//�ж��Ǹû����Ƿ��˹�
		boolean unRegFlg = REGTool.getInstance().queryUnReg(parmpat.getValue("CASE_NO", row));
		if(unRegFlg){
			odoMainControl.messageBox("�ò������˹�,������ˢ�²����б�");
			onSelectPat(NULLSTR);
			return;
		}
		//add by huangtt 20140926 end
		//˹�ʹ�
		if(!aheadFlg){
			TParm clinicFeeParm = EKTpreDebtTool.getInstance().checkClinicFee(parmpat.getValue("CASE_NO", row));
			if(clinicFeeParm.getErrCode()<0){
				odoMainControl.messageBox(clinicFeeParm.getErrText());
				return;
			}
		}
		odoMainControl.odoMainOpdOrder.wc = PatAdmTool.getInstance().getWestMediFlg(
				Operator.getRegion(),
				parmpat.getValue("ADM_TYPE", row),
				StringTool.getString(parmpat.getTimestamp("ADM_DATE", row),
						"yyyyMMdd"), odoMainControl.getValueString("SESSION_CODE"),
				parmpat.getValue("CLINICROOM_NO", row)).getValue(
				"WEST_MEDI_FLG", 0);
		if (StringUtil.isNullString(odoMainOpdOrder.wc)) {
			odoMainControl.messageBox("E0119"); // �ż����Ǵ���
			odoMainControl.closeWindow();
		}
		odoMainControl.onClear();
		initOpd(parmpat, row);
		odoMainTjIns.initINS();
		viewClinicFee();
		
		// �ж�reg������SEEN_DR_TIME�Ƿ������� ���Ϊ�ռ�¼��ǰʱ��   add by huangtt 20150128 
		if (odoMainControl.odo.getRegPatAdm().getItemData(0, "SEEN_DR_TIME") == null) {
			odoMainControl.odo.getRegPatAdm().setItem(0, "SEEN_DR_TIME",
					SystemTool.getInstance().getDate());
		}
		
	}
	
	/**
	 * �����б�չ���¼�
	 */
	public void onPat() throws Exception{
		// �жϿ���Ȩ��
		if (odoMainControl.getPopedem("DEPT_POPEDEM"))
			onSelectPat("INSTEAD_DEPT");
		else
			onSelectPat(NULLSTR);
		odoMainControl.mp.onDoubleClicked(false);
	}
	
	/**
	 * �Ŷӽк�
	 */
	public void onCallNo() throws Exception{
		if (tblPat == null) {
			return;
		}
		int row = tblPat.getSelectedRow();
		if (row < 0) {
			return;
		}
		TParm parm = tblPat.getParmValue();
		if (parm == null || parm.getCount() <= 0) {
			return;
		}
		CallNo callUtil = new CallNo();
		// System.out.println();
		if (!callUtil.init()) {
			return;
		}
		//�к�ͬ��
		String caseNo = parm.getValue("CASE_NO", row);
		String patName = parm.getValue("PAT_NAME", row);
		String drID = Operator.getID();
		String ip = Operator.getIP();
		if (StringUtil.isNullString(caseNo) || StringUtil.isNullString(patName)
				|| StringUtil.isNullString(drID) || StringUtil.isNullString(ip)) {
			odoMainControl.messageBox("E0133");
			return;
		}
		String result = callUtil.CallClinicMaster(NULLSTR, caseNo, NULLSTR, NULLSTR, patName,
				NULLSTR, NULLSTR, drID, ip);
		if ("true".equalsIgnoreCase(result)) {

			odoMainControl.messageBox("P0119");
			return;
		}
		odoMainControl.messageBox("E0133");
	}
	
	/**
	 * ԤԼ�Һ�
	 */
	public void onReg() throws Exception{
		if (odoMainControl.odo == null)
			return;
		String MR_NO = odoMainControl.odo.getMrNo();
		TParm parm = new TParm();
		parm.setData("MR_NO", MR_NO);
		parm.setData("NHI_NO", odoMainPat.pat.getNhiNo());// ҽ������
		odoMainControl.openWindow(URLREGADMFORDR, parm);
	}
	
	/**
	 * CRMҽʦԤԼ
	 * @throws Exception
	 */
	public void onCrmDr() throws Exception{
		if (odoMainControl.odo == null)
			return;
		String MR_NO = odoMainControl.odo.getMrNo();
		TParm parm = new TParm();
		parm.setData("MR_NO", MR_NO);
		odoMainControl.openWindow(URLREGADMFORCR, parm);
	}
	
	/**
	 * �ǳ�̬����
	 * @throws Exception 
	 */
	public void onAbnormalReg() throws Exception {
		Object obj = odoMainControl.openDialog(URLOPDABNORMALREG);
		if (obj == null || !(obj instanceof TParm)) {
			return;
		}
		TParm parm = (TParm) obj; // �ǳ�̬����Һ���Ϣ
		odoMainOpdOrder.wc = ODOMainOpdOrder.W; // Ĭ��Ϊ��ҽ
		initOpd(parm, 0); // ��ʼ��ҽ��վ��Ϣ
	}
	
	/**
	 * ɸѡ����
	 * 
	 * @param type
	 *            String ��ʾ �Ǹ��ؼ����ø÷���
	 */
	public void onSelectPat(String type) throws Exception{
		boolean isDedug=true; //add by huangtt 20160505 ��־���
		if (!selectPat) {
			selectPat = true;
			return;
		}
		//TTable table = (TTable) this.getComponent("TABLEPAT");
		String date = StringTool.getString((Timestamp) odoMainControl
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		String insteadDr = odoMainControl.getValueString("INSTEAD_DR");
		if (!"INSTEAD_DR".equals(type) && !"INSTEAD_DEPT".equals(type)) {
			if (E.equalsIgnoreCase(admType)) {
				onEPatQuery();
			} else {
				onPatQuery();
			}
			initORadio();
			// ��������ѡ���¼� ��ô���ø������
			if (!"CLINICROOM".equals(type)) {
				initClinicRoomCombo();
			}
			// ����ǡ�ʱ�Ρ�combo���û����� ���������¼�����ô��� ���combo
			if ("SESSION_CODE".equals(type) || "ADM_DATE".equals(type)) {
				odoMainControl.clearValue("CLINICROOM");
			}
			if (insteadDept != null) {
				initInsteadDept();
			}
			FirstNext(); //add by huangtt 20131128
			tblPat.removeRowAll();
			tblPat.setParmValue(parmpat);
			tblPat.changeLanguage(Operator.getLanguage());
			selectPat = true;
		} else if ("INSTEAD_DR".equals(type) && !NULLSTR.equals(insteadDr)) {
			try {

			parmpat = PatAdmTool.getInstance()
					.getInsteadPatList(date,
							odoMainControl.getValueString("SESSION_CODE"), insteadDr,
							admType, odoMainControl.getValueString("INSTEAD_DEPT"));
			tblPat.removeRowAll();
			FirstNext(); //add by huangtt 20131128
			tblPat.setParmValue(parmpat);
			tblPat.changeLanguage(Operator.getLanguage());
			selectPat = true;
			
			} catch (Exception e) {
				// TODO: handle exception
				if(isDedug){  
					System.out.println(" come in class: ODOMainReg.class ��method ��onSelectPat-INSTEAD_DR");
					e.printStackTrace();
				}
			}
		} else if ("INSTEAD_DEPT".equals(type)
				&& !NULLSTR.equals(odoMainControl.getValueString("INSTEAD_DEPT"))) {
			try {
				

			parmpat = PatAdmTool.getInstance().getInsteadPatList(date,
					odoMainControl.getValueString("SESSION_CODE"), NULLSTR, admType,
					odoMainControl.getValueString("INSTEAD_DEPT"));
			tblPat.removeRowAll();
			FirstNext(); //add by huangtt 20131128
			tblPat.setParmValue(parmpat);
			tblPat.changeLanguage(Operator.getLanguage());
			selectPat = true;
			
			} catch (Exception e) {
				// TODO: handle exception
				if(isDedug){  
					System.out.println(" come in class: ODOMainReg.class ��method ��onSelectPat-INSTEAD_DEPT");
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * ������Żس���ѯ�¼�
	 * @throws Exception 
	 */
	public void onQueNo() throws Exception {
		if (parmpat == null || parmpat.getCount() < 1) {
			odoMainControl.messageBox("E0025"); // û�в���
			return;
		}
		String queNo = odoMainControl.getValueString("QUE_NO");
		odoMainControl.onClear();
		for (int i = 0; i < parmpat.getCount(); i++) {
			String tempQue = parmpat.getValue("QUE_NO", i);
			if (StringUtil.isNullString(tempQue)) {
				continue;
			}
			if (queNo.equalsIgnoreCase(tempQue)) {
				odoMainControl.odo = new ODO(parmpat.getValue("CASE_NO", i), parmpat.getValue(
						"MR_NO", i), Operator.getDept(), Operator.getID(),
						parmpat.getValue("ADM_TYPE", i));
//				String odo_type = "ODO";
//				if (E.equals(admType)) {
//					odo_type = "ODE";
//				}
//				if (!odoMainPat.onLockPat(odo_type)) {
//					odoMainControl.odo = null;
//					return;
//				}
				odoMainControl.mp.onDoubleClicked(true);
				realDeptCode = parmpat.getValue("REALDEPT_CODE", i);
				odoMainOpdOrder.wc = PatAdmTool.getInstance().getWestMediFlg(
						Operator.getRegion(),
						parmpat.getValue("ADM_TYPE", i),
						StringTool.getString(parmpat
								.getTimestamp("ADM_DATE", i), "yyyyMMdd"),
								odoMainControl.getValueString("SESSION_CODE"),
						parmpat.getValue("CLINICROOM_NO", i)).getValue(
						"WEST_MEDI_FLG", 0);
				if (StringUtil.isNullString(odoMainOpdOrder.wc)) {
					odoMainControl.messageBox("E0119"); // �ż����Ǵ���
					odoMainControl.closeWindow();
				}
				odoMainControl.caseNo=parmpat.getValue("CASE_NO", i);//=====��������¸�ֵ pangben 2014-1-20
				initReg(parmpat.getValue("MR_NO", i), parmpat.getValue(
						"CASE_NO", i));
				if (!odoMainControl.odo.onQuery()) {
					odoMainControl.messageBox("E0024"); // ��ʼ������ʧ��
					return;
				}
				odoMainControl.odo.getOpdOrder().addEventListener(
						odoMainControl.odo.getOpdOrder().ACTION_SET_ITEM, this,
						"onSetItemEvent");
				//System.out.println("reg=====222222222");
				odoMainPat.initPatInfo(parmpat, i);
				odoMainControl.initPanel();
				return;
			}
		}
		odoMainControl.messageBox("E0025"); // û�в���
	}
	
	/**
	 * ���ﲡ����ѯ
	 */
	public void onPatQuery() throws Exception{
		TParm parm = new TParm();
		parm.setData("DR_CODE", Operator.getID());
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("ADM_TYPE", admType);
		//parm.setData("CLINICROOM_NO", odoMainControl.getValueString("CLINICROOM"));
		if (odoMainControl.getValueString("CLINICROOM").length() > 0)
			parm.setData("CLINICROOM_NO", odoMainControl.getValueString("CLINICROOM"));
		String date = StringTool.getString((Timestamp) odoMainControl
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		parm.setData("ADM_DATE", date);
		//parm.setData("SESSION_CODE", odoMainControl.getValue("SESSION_CODE"));
		if (odoMainControl.getValueString("SESSION_CODE").length() > 0)
			parm.setData("SESSION_CODE", odoMainControl.getValueString("SESSION_CODE"));
		if ("Y".equalsIgnoreCase(odoMainControl.getValueString("REG_WAIT"))) {
			parm.setData("WAIT_DR", "N");
		} else if ("Y".equalsIgnoreCase(odoMainControl.getValueString("REG_DONE"))) {
			parm.setData("SEE_DR", "Y");
		} else if ("Y".equalsIgnoreCase(odoMainControl.getValueString("REG_TEMP"))) {
			parm.setData("TEMP_DR", "T");
		}
		// Date patd1 = new Date();
		parmpat = PatAdmTool.getInstance().selDateForODOByWait(parm);
		// Date patd2 = new Date();
		if (parmpat.getErrCode() < 0) {
			odoMainControl.messageBox("E0005"); // ִ��ʧ��
			return;
		}
		jsEnd();
	}
	/**
	 * ���ﻤʿվ�������һ��
	 * @return
	 * @throws Exception
	 */
	private TParm onPatQuerySendOnw()throws Exception{
		i++;
		TParm parm = new TParm();
		parm.setData("DR_CODE", Operator.getID());
		parm.setData("REGION_CODE", Operator.getRegion()); 
		parm.setData("ADM_TYPE", admType);
		parm.setData("CLINICROOM_NO", odoMainControl.getValueString("CLINICROOM"));
		String date = StringTool.getString((Timestamp) odoMainControl
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		parm.setData("ADM_DATE", date);
		parm.setData("SESSION_CODE", odoMainControl.getValue("SESSION_CODE"));
		parm.setData("WAIT_DR", "N");
		// Date patd1 = new Date();
		parmpat = PatAdmTool.getInstance().selDateForODOByWait(parm);
		// Date patd2 = new Date();
		if (parmpat.getErrCode() < 0) {
			odoMainControl.messageBox("E0005"); // ִ��ʧ��
			return null;
		}
		if (parmpat.getCount()<=0) {
			return null;
		}
		return parmpat.getRow(i);
	}
	/**
	 * ���ﲡ����ѯ
	 */
	public void onEPatQuery() throws Exception{
		TParm parm = new TParm();
		// parm.setData("DR_CODE", Operator.getID());//����Ҫ���ܿ��������ҵ����в���
		// ���ԾͲ���ҽʦΪ������
		parm.setData("ADM_TYPE", admType);
		parm.setData("REGION_CODE", Operator.getRegion());
		if (odoMainControl.getValueString("CLINICROOM").length() > 0)
			parm.setData("CLINICROOM_NO", odoMainControl.getValueString("CLINICROOM"));
		String date = StringTool.getString((Timestamp) odoMainControl
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		parm.setData("ADM_DATE", date);
		if (odoMainControl.getValueString("SESSION_CODE").length() > 0)
			parm.setData("SESSION_CODE", odoMainControl.getValue("SESSION_CODE"));
		if ("Y".equalsIgnoreCase(odoMainControl.getValueString("REG_WAIT"))) {
			parm.setData("WAIT_DR", "N");
		} else if ("Y".equalsIgnoreCase(odoMainControl.getValueString("REG_DONE"))) {
			parm.setData("SEE_DR", "Y");
		} else if ("Y".equalsIgnoreCase(odoMainControl.getValueString("REG_TEMP"))) {
			parm.setData("TEMP_DR", "T");
		}
		parm.setData("REALDEPT_CODE", Operator.getDept());
		parmpat = PatAdmTool.getInstance().selDateForODOEmgc(parm);
		// QUE_NO;MR_NO;PAT_NAME;ADM_STATUS;REPORT_STATUS
		if (parmpat.getErrCode() < 0) {
			odoMainControl.messageBox("E0005"); // ִ��ʧ��
			return;
		}
		// liudy
		jsStart();
	}
	
	// $$=================add by lx 2011/02/12
	// Start���нӿ�=============================$$//
	/**
	 * �ؽ�
	 */
	public void onReCallNo() throws Exception{
		// ����ҽʦ���� Varchar 6 000234
		String drCode = Operator.getID() + "|";
		// IP��ַ Varchar 15 172.201.132.123
		String ip = Operator.getIP();

		TParm inParm = new TParm();
		inParm.setData("msg", drCode + ip);
		
		TIOM_AppServer.executeAction("action.device.CallNoAction", "doReCall",
				inParm);
		//=======yanjing 20140403 ��RootServerͨѶ���������Ӧ�����Ļ�ʿվ������Ϣ
//		sendMedMessages(onPatQuerySendOnw());
		sendMedMessages(Operator.getID(),Operator.getIP());
		
	}
	
	/**
	 * ��һ��
	 */
	public void onNextCallNo() throws Exception{
		String drCode = Operator.getID() + "|";
		// IP��ַ Varchar 15 172.201.132.123
		String ip = Operator.getIP();
		TParm inParm = new TParm();
		inParm.setData("msg", drCode + ip);
	
		TIOM_AppServer.executeAction("action.device.CallNoAction",
				"doNextCall", inParm);
//		sendMedMessages(onPatQuerySendOnw());
		sendMedMessages(Operator.getID(),Operator.getIP());
		
	}
	// $$=================add by lx 2011/02/12
	// ���нӿ�=============================$$//
	
	/**
	 * �кŵ�½����
	 * 
	 * @param type
	 *            String 1��½,0�˳�
	 */
	public void SynLogin(String type) throws Exception{
		CallNo callUtil = new CallNo();
		if (!callUtil.init()) {
			return;
		}
	}
	
	/**
	 * ����reg����
	 */
	public void saveRegInfo() throws Exception{
		PatInfo patInfo = odoMainControl.odo.getPatInfo();
		RegPatAdm regPatAdm = odoMainControl.odo.getRegPatAdm();
		patInfo.setActive(0, true);
		regPatAdm.setActive(0, true);
		patInfo.setItem(0, "PAT1_CODE", odoMainControl.getValue("PAT1_CODE"));
		patInfo.setItem(0, "PAT2_CODE", odoMainControl.getValue("PAT2_CODE"));
		patInfo.setItem(0, "PAT3_CODE", odoMainControl.getValue("PAT3_CODE"));
		patInfo.setItem(0, "PREMATURE_FLG", odoMainControl.getValue("PREMATURE_FLG"));
		patInfo.setItem(0, "HANDICAP_FLG", odoMainControl.getValue("HANDICAP_FLG"));
		patInfo.setItem(0, "HIGHRISKMATERNAL_FLG", odoMainControl.getValue("HIGHRISKMATERNAL_FLG"));
		patInfo.setItem(0, "LMP_DATE", odoMainControl.getValue("LMP_DATE"));
		patInfo.setItem(0, "PREGNANT_DATE", odoMainControl.getValue("EDC_DATE"));//Ԥ���ڣ�add by huangjw 20141016
		patInfo.setItem(0, "BREASTFEED_STARTDATE", odoMainControl
				.getValue("BREASTFEED_STARTDATE"));
		patInfo.setItem(0, "BREASTFEED_ENDDATE", odoMainControl
				.getValue("BREASTFEED_ENDDATE"));
		String nowValue = odoMainOther.familyWord.getCaptureValue("FAMILY_HISTORY");
		String oldValue = patInfo.getItemString(0, "FAMILY_HISTORY");
		String nowValue1=odoMainOther.familyWord.getCaptureValue("PAST_HISTORY");
		String oldValue1 = patInfo.getItemString(0, "PAST_HISTORY");
		if (!nowValue.equalsIgnoreCase(oldValue)||nowValue1.equalsIgnoreCase(oldValue1)) {
			// 2:δ�����
			if ("2".equalsIgnoreCase(odoMainOther.familyHisFiles[2])) {
				odoMainOther.familyWord.setMessageBoxSwitch(false);
				TParm familyParm = EmrUtil.getInstance()
						.getFamilyHistorySavePath(odoMainControl.odo.getCaseNo(),
								odoMainControl.odo.getMrNo(), odoMainOther.familyHisFiles[3],
								odoMainOther.familyHisFiles[1]);
				odoMainOther.familyWord.onSaveAs(familyParm.getValue("PATH"), familyParm
						.getValue("FILENAME"), 3);
				odoMainOther.familyWord.setCanEdit(true);
			}
			// 3:�����
			else {
				odoMainOther.familyWord.setMessageBoxSwitch(false);
				odoMainOther.familyWord.onSaveAs(odoMainOther.familyHisFiles[0], odoMainOther.familyHisFiles[1], 3);
				odoMainOther.familyWord.setCanEdit(true);
			}
		}
		
		patInfo.setItem(0, "FAMILY_HISTORY", odoMainOther.familyWord
				.getCaptureValue("FAMILY_HISTORY"));
		patInfo.setItem(0, "PAST_HISTORY", odoMainOther.familyWord
				.getCaptureValue("PAST_HISTORY"));
		// add by wangb 2017/10/12 ��ز�ʷ����������Ѫʷ
		patInfo.setItem(0, "OP_BLOOD_HISTORY", odoMainOther.familyWord
				.getCaptureValue("OP_BLOOD_HISTORY"));
		regPatAdm.setItem(0, "CTZ1_CODE", odoMainControl.getValue("CTZ1_CODE"));
		regPatAdm.setItem(0, "CTZ2_CODE", odoMainControl.getValue("CTZ2_CODE"));
		regPatAdm.setItem(0, "CTZ3_CODE", odoMainControl.getValue("CTZ3_CODE"));
		regPatAdm.setItem(0, "WEIGHT", TypeTool.getDouble(odoMainControl
				.getValue("WEIGHT")));
		regPatAdm.setItem(0, "VISIT_STATE", odoMainControl.getValueString("VISIT_STATE"));
		regPatAdm.setItem(0, "LMP_DATE", odoMainControl.getValue("LMP_DATE"));
		// �ж�reg������REALDR_CODE�Ƿ������� ���Ϊ�ռ�¼��ǰ��ϵͳ��Ա
		if (regPatAdm.getItemData(0, "REALDR_CODE") == null || "".equals(regPatAdm.getItemData(0, "REALDR_CODE").toString())) {
			regPatAdm.setItem(0, "REALDR_CODE",
					Operator.getID());
		}
		
//		//zhangp 20131206
//		String regionCode = Operator.getRegion();
//		String optUser = Operator.getID();
//		String optTerm = Operator.getIP();
//		if((!aheadFlg) && odoMainControl.odoMainEkt.preDebtFlg){
//			TParm result = EKTpreDebtTool.getInstance().insertOpdClinicFee(odoMainControl.caseNo, regionCode, optUser, optTerm);
//			if(result.getErrCode()<0){
//				odoMainControl.messageBox("���Ѳ���ʧ��");
//			}
//		}
	}
	
	public void onRegClear() throws Exception{
		reg = null;
		odoMainControl.clearValue("CTZ_CODE;SERVICE_LEVEL;CLINIC_FEE;VISIT_STATE;REASSURE_FLG");
	}
	
	/**
	 * �����ɫ�߳̿���
	 */
	public void jsStart() {
		if (erdThread != null)
			return;
		erdThread = new Thread() {
			public void run() {
				try {
					erdThread.sleep(500);
				} catch (Exception e) {

				}
				while (erdThread != null) {
					jsRun();
					try {
						erdThread.sleep(100);
					} catch (Exception e) {

					}
				}
			}
		};
		erdThread.start();
	}

	/**
	 * �����ɫ�߳̽���
	 */
	public void jsEnd() {
		erdThread = null;
	}

	/**
	 * �����ɫ�߳�����
	 */
	public void jsRun() {
		map = new HashMap();
		if (parmpat == null || parmpat.getCount("TIME_LIMIT") < 1)
			return;
		for (int i = 0; i < parmpat.getCount(); i++) {
			int limit = parmpat.getInt("TIME_LIMIT", i);
			Timestamp admDate = parmpat.getTimestamp("ADM_DATE", i);
			Timestamp now = TJDODBTool.getInstance().getDBTime();
			int minute = new Long(
					(now.getTime() - admDate.getTime()) / 1000 / 60).intValue();
			if (minute >= limit) {
				map.put(i, OdoMainControl.RED);
			}
		}
		if (map.size() > 0) {
			tblPat.setRowTextColorMap(map);
		}
	}
	
	/**
	 * �ж��Ƿ�����޸�ҽ�� ���ADM_DATE�ĵ���23��59��59���Ժ󲻿����ٶԲ�����ҽ�������޸ģ�����ɾ����
	 * ���ADM_DATE��������23��59��59���Ժ󲻿����ٶԲ�����ҽ�������޸ģ�����ɾ����
	 * 
	 * @return boolean
	 */
	public boolean canEdit() throws Exception {
		Timestamp admDate = reg.getAdmDate(); // �Һ�����
		Timestamp now = SystemTool.getInstance().getDate(); // ��ǰʱ��
		if (O.equals(admType)) {
			// ��ȡ�Һŵ���23��59��59���ʱ�䣨�����޸�ҽ�����޶�ʱ�䣩
			Timestamp time = StringTool.getTimestamp(StringTool.getString(
					admDate, "yyyyMMdd")
					+ "235959", "yyyyMMddHHmmss");
			// ��ǰʱ�������޸�ҽ�����޶�ʱ�� �򲻿����޸�
			if (now.getTime() > time.getTime()) {
				return false;
			}
		} else if (E.equals(admType)) {
			// ���� ��ȡҽ��վ�������С��޶�������������23��59��59���ʱ�䣨�����޸�ҽ�����޶�ʱ�䣩
			// ������� ��������� ����3/5 ���ϹҺţ�һֱ�� 3/6 ȫ�춼��Ϊ��Ч
			int OPDDay = OPDSysParmTool.getInstance().getEDays();
			String sessionCode = odoMainControl.getValueString("SESSION_CODE");
			if (sessionCode.equalsIgnoreCase("8") && OPDDay == 0) { // �������
				OPDDay++;
			}
			Timestamp time = StringTool.getTimestamp(StringTool.getString(
					StringTool.rollDate(admDate, OPDDay), "yyyyMMdd")
					+ "235959", "yyyyMMddHHmmss");
			// ��ǰʱ�������޸�ҽ�����޶�ʱ�� �򲻿����޸�
			if (now.getTime() > time.getTime()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ����
	 * @throws Exception
	 */
	public boolean onSave() throws Exception{
		//add by huangtt 20140227 strat
		//�жϾ���״̬�Ƿ�Ϊ��
		String visitState = odoMainControl.getValueString("VISIT_STATE");
		if(visitState.equals("")){
			odoMainControl.messageBox("��ѡ�����״̬��");
			return true;
		}
		odoMainControl.odo.getRegPatAdm().setItem(0, "VISIT_STATE", visitState);
		//add by huangtt 20140227 end
		saveRegInfo();
		odoMainControl.odo.getRegPatAdm().setItem(0, "SEE_DR_FLG", "Y");
		// �ж�reg������SEEN_DR_TIME�Ƿ������� ���Ϊ�ռ�¼��ǰʱ��
		if (odoMainControl.odo.getRegPatAdm().getItemData(0, "SEEN_DR_TIME") == null) {
			odoMainControl.odo.getRegPatAdm().setItem(0, "SEEN_DR_TIME",
					SystemTool.getInstance().getDate());
		}
		odoMainControl.odo.getRegPatAdm().setItem(0, "LMP_DATE", odoMainControl.getValue("LMP_DATE"));
		
		//huangtt 20131128 start
		tblPat.acceptText();
		String firstFlg=tblPat.getParmValue().getValue("FIRST_FLG", PatSelectRow);
		if(firstFlg.equals("����")){
			odoMainControl.odo.getRegPatAdm().setItem(0, "FIRST_FLG", "Y");
		}
		if(firstFlg.equals("����")){
			odoMainControl.odo.getRegPatAdm().setItem(0, "FIRST_FLG", "N");
		}
		//huangtt 20131128 end
		String admStatus = odoMainControl.odo.getRegPatAdm().getItemString(0, "ADM_STATUS");
		if (!("6".equalsIgnoreCase(admStatus) || "9"
				.equalsIgnoreCase(admStatus))) {
			odoMainControl.odo.getRegPatAdm().setItem(0, "ADM_STATUS", "2");
		}

		return false;
	}
	
	/**
	 * ���¹Һŵ��еĵı���״̬  add by huangtt 20150720 
	 * 
	 */
	public void onSaveReportStatus(){
		//add by huangtt 20150720 start  ������û�п����Ļ�������reg_patadm����REPORT_STATUSΪ0
		TParm re = new TParm();
		String caseNo = odoMainControl.odo.getRegPatAdm().getItemString(0, "CASE_NO");
		re.setData("CASE_NO", caseNo);
		String sql="SELECT STATUS FROM MED_APPLY WHERE CASE_NO='"+caseNo+"' ORDER BY STATUS";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()> 0){
			int count =0;
			for (int i = 0; i < parm.getCount(); i++) {
				if("7".equals(parm.getValue("STATUS", i)) ||
						"8".equals(parm.getValue("STATUS", i))){
					count++;
				}
			}
			if(count == 0){

				re.setData("REPORT_STATUS", 1);
			}else if(count == parm.getCount()){

				re.setData("REPORT_STATUS", 3);
			}else{

				re.setData("REPORT_STATUS", 2);
			}
		}else{
			re.setData("REPORT_STATUS", 0);
		}
	   TParm result = REGTool.getInstance().updateReportStatus(re);
	   	if(result.getErrCode()<0){
			odoMainControl.messageBox("���¹Һŵ�����״̬ʧ��");
		}
		//add by huangtt 20150720 end
		
	}
	
	/**
	 * Ϊ������Ϣ����������
	 * add by huangtt 20131128
	 */
	public void FirstNext() throws Exception{
		for(int i=0;i<parmpat.getCount("MR_NO");i++){
			if(parmpat.getValue("FIRST_FLG", i).equals("")){
				String mr_no = parmpat.getValue("MR_NO", i);
				String dept_code = parmpat.getValue("REALDEPT_CODE",i);
				String sql = "SELECT COUNT(MR_NO) SUM FROM SYS_EMR_INDEX WHERE MR_NO = '"+mr_no+"' AND DEPT_CODE = '"+dept_code+"'";
				TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
				//û�����ݣ���Ϊ����
				if(selParm.getInt("SUM", 0)<=1){
					parmpat.setData("FIRST_FLG", i, "����");
			    //����
				}else{
					parmpat.setData("FIRST_FLG", i, "����");
				}
			}else{
				if(parmpat.getValue("FIRST_FLG", i).equals("Y")){
					parmpat.setData("FIRST_FLG", i, "����");
				}else{
					parmpat.setData("FIRST_FLG", i, "����");
				}
			}
		}
	}
	
	/**
	 * ��ʾ����
	 * ==zhangp
	 */
    public void viewClinicFee() throws Exception{
    	double clinicFee = EKTpreDebtTool.getInstance().getClinicFee(odoMainControl.caseNo);
    	odoMainControl.setValue("CLINIC_FEE", StringTool.round(clinicFee,2));
    }
    
	/**
	 * �����Ԥ�����   дһ��ԤԼ�Һ���Ϣ
	 * 
	 * ������һ��case_no
	 * */
	public void onInsertReg(TParm parm){
		String newCaseNo = parm.getValue("CASE_NO");
        String mr_no =parm.getValue("MR_NO");
        String APPT_CODE = "Y";     
        String ARRIVE_FLG = "N";
        String OPT_USER = Operator.getID();
        Timestamp OPT_DATE = SystemTool.getInstance().getDate();
        String OPT_TERM = Operator.getIP();
        TParm tp = new TParm();
        tp.setData("REGMETHOD_CODE", "T");//�Һŷ�ʽ,�绰�˹�
        tp.setData("SERVICE_LEVEL", reg.getServiceLevel());//����ȼ�
        tp.setData("ADM_TYPE", admType);//�ż�ס��ԤԼ��Ϣ��������ʾ
        tp.setData("REGION_CODE", Operator.getRegion());
        tp.setData("CTZ1_CODE", parm.getData("CTZ1_CODE"));//���
        tp.setData("OLD_CASE_NO", odoMainControl.caseNo);
        tp.setData("REG_DATE", OPT_DATE);
        tp.setData("ADM_DATE", parm.getData("ADM_DATE"));
        tp.setData("CASE_NO", newCaseNo);
        tp.setData("MR_NO", mr_no);
        tp.setData("APPT_CODE", APPT_CODE);
        tp.setData("ARRIVE_FLG", ARRIVE_FLG);
        tp.setData("OPT_USER", OPT_USER);
        tp.setData("OPT_DATE", OPT_DATE);
        tp.setData("OPT_TERM", OPT_TERM);
        tp.setData("IS_PRE_ORDER","Y");//�Ƿ���Ԥ�����
			TParm onSaveP = REGAdmForDRTool.getInstance().onSavePreOrder(tp);
			if(onSaveP.getErrCode()<0){
				odoMainControl.messageBox("Ԥ����鱣��ʧ�ܣ�");
	            return;
			}
		}
	
	public void afterRead() throws Exception{
		String mrNo = odoMainControl.odoMainEkt.ektReadParm.getValue("MR_NO");
		TRadioButton reg_wait = (TRadioButton) odoMainControl.getComponent("REG_WAIT");
		TRadioButton reg_temp = (TRadioButton) odoMainControl.getComponent("REG_TEMP");
		TRadioButton reg_done = (TRadioButton) odoMainControl.getComponent("REG_DONE");
		reg_wait.setSelected(true);
		if(clickTable(mrNo)){
			reg_temp.setSelected(true);
			if(clickTable(mrNo)){
				reg_done.setSelected(true);
				if(clickTable(mrNo)){
//					odoMainControl.messageBox("");	
					if(odoMainControl.getValue("SESSION_CODE").equals("02")){
						odoMainControl.setValue("SESSION_CODE", "01");
						onSelectPat("SESSION_CODE");
						for(int i=0;i<clinicroomParm.getCount();i++){
							odoMainControl.setValue("CLINICROOM", clinicroomParm.getValue("ID", i));
							onSelectPat("CLINICROOM");
							reg_wait.setSelected(true);
							if(clickTable(mrNo)){
								if(clickTable(mrNo)){
									reg_temp.setSelected(true);
									if(clickTable(mrNo)){
										reg_done.setSelected(true);
										if(clickTable(mrNo)){
											odoMainControl.messageBox("�Ǳ����Ҷ������ܵ�¼����ҽ��վ");
										}
									}	
								}
							}	
						}					
					}
				}
			}	
		}	
	}
	
	private boolean clickTable(String mrNo) throws Exception{
		onSelectPat(null);
		tblPat.acceptText();
		TParm patParm = tblPat.getParmValue();
		for (int i = 0; i < patParm.getCount(); i++) {
			if(patParm.getValue("MR_NO", i).equals(mrNo)){
				odoMainControl.odoMainEkt.ektReadParmBefore = odoMainControl.odoMainEkt.ektReadParm;
				tblPat.setSelectedRow(i);
				this.onTablePatDoubleClick();
				odoMainControl.odoMainEkt.isReadEKT = true;
				odoMainControl.odoMainEkt.ektReadParm = odoMainControl.odoMainEkt.ektReadParmBefore;
				odoMainControl.setValue("LBL_EKT_MESSAGE", "�Ѷ���");//====pangben 2013-3-19 ��Ӷ���
				odoMainControl.odoMainEkt.ekt_lable.setForeground(OdoMainControl.GREEN);//======yanjing 2013-06-14���ö�����ɫ
				return false;
			}
		}
		return true;
	}
	/**
	 * ���Ӧ�����ﻤʿվ������Ϣ
	 * =======pangben 2013-5-13 
	 */
	public void sendMedMessages(String drCode,String ip) {
		if (null==drCode) {
			return;
		}
		client1 = SocketLink
				.running("","ODO", "ODO");
		if (client1.isClose()) {
			System.out.println(client1.getErrText());
			return;
		}
			client1.sendMessage("ONW", "IP:"+Operator.getIP()
					+"|DR_CODE:"+drCode+"|CLINICAREA_CODE:"+Operator.getStation());
		if (client1 == null)
			return;
			
		client1.close();
	}
	
	public boolean canSave(){
		//�жϸò����Ƿ�Ϊ�˹���Ա  true �˹�
		
		if(REGTool.getInstance().queryUnReg(reg.caseNo())){
			return true;
		}
		return false;
	}
	
	/**
	 * ������ѯ   add by huangtt 20150205
	 */
	public void onOpdEmrQuery() {
		TTable table = (TTable) odoMainControl.getComponent(TABLEPAT);
		int row = table.getSelectedRow();
		if(row == -1){
			odoMainControl.messageBox("��ѡ����");
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", table.getItemString(row, "MR_NO"));
		odoMainControl.openDialog(URLOPDEMR, parm);
	}
	
}
