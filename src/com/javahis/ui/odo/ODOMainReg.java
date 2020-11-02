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
 * Title: 门诊医生工作站挂号对象
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站挂号对象
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
	Reg reg;// reg对象
	
	private TTable tblPat;
	public final static String TABLEPAT = "TABLEPAT";
	public static boolean  selectPat = false;// 调用查找病人方法的开关，给某些组件赋值时，这些组件里的点击方法有调用selectPat方法，就会递归
	public TParm parmpat;// 病患信息
	
	private TComboSession t;// 就诊时段combo
	private TComboBox clinicroom, dr, insteadDept;// 诊室，医师，代诊部门
	public String admType; // liudy // 门急住别
	public TParm regSysEFFParm;//pangben 2013-4-28 挂号有效天数
	private Thread erdThread;// 急诊病患列表变色的线程
	Map map; // liudy // table使用的行的颜色集合
	public String realDeptCode; // 开单科室
	private int PatSelectRow; //===huangtt 20131128 双击病患列表时选中的当前行
	private boolean aheadFlg = true;
	TParm clinicroomParm;
	
	private static final String OHEADER =  "诊号,40;病案号,100;姓名,80;初复诊,50,VISIT_CODE;首再诊,50,FIRST_FLG;就诊状态,80,ADM_STATUS;报告状态,80,REPORT_STATUS";
	private static final String OENHEADER = "QueNo;PatNo;Name;visitName;Status;ReportStatus;FirstNext";
	private static final String OPARMMAP = "QUE_NO;MR_NO;PAT_NAME;VISIT_CODE;FIRST_FLG;ADM_STATUS;REPORT_STATUS";
	private static final String OLANGUAGEMAP = "PAT_NAME|PAT_NAME1";
	private static final String OLOCKCOLUMNS = "0,1,2,3,4,5";
	private static final String OCOLUMNHORIZONTALALIGNMENTDATA = "0,right;1,right;2,left;3,left;4,left;5,left;6,left";
	private static final String EHEADER = "诊号,40;病案号,100;姓名,80;初复诊,50,VISIT_CODE;检伤等级,80,ERD_LEVEL;单病种,120,DISE_CODE;到院时间,120,Timestamp,yyyy-MM-dd HH:mm;最新医嘱,120,Timestamp,yyyy-MM-dd HH:mm;就诊状态,80,ADM_STATUS;报告状态,80,REPORT_STATUS";
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
	
	public static final String MEGOVERTIME = "该病患已退挂，不能保存数据"; 
	
	public static final String O = "O";//门诊
	public static final String E = "E";//急诊
	private static final String NULLSTR = "";
	public int i = 0; // liudy // 门急住别
	/**
	 * Socket传送门诊药房工具
	 */
	private SocketLink client1;
	/**
	 * 构造
	 * @param odoMainControl
	 */
	public ODOMainReg(OdoMainControl odoMainControl){
		this.odoMainControl = odoMainControl;
	}
	
	/**
	 * 挂号初始化
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
		// 初始化病患列表
		initOPD();
		// 初始化门急别
		initOE();
		// 判断科室权限
		if (odoMainControl.getPopedem("DEPT_POPEDEM"))
			onSelectPat("INSTEAD_DEPT");
		else
			// 给TABLE放数据
			onSelectPat(NULLSTR);
		SynLogin("1"); // 叫号登陆
		regSysEFFParm = REGTool.getInstance().getRegParm();//pangben 2013-4-28 挂号有效天数
		aheadFlg = REGSysParmTool.getInstance().selAeadFlg();
	}
	
	/**
	 * 初始化病患列表
	 */
	public void initOPD() throws Exception{
		if (odoMainControl.getParameter() == null) {
			odoMainControl.messageBox("E0024"); // 初始化参数失败
			return;
		}
		String sessionCode = initSessionCode();
		Timestamp admDate = TJDODBTool.getInstance().getDBTime();
		// 根据时段判断应该显示的日期（针对于晚班夸0点的问题，跨过0点的晚班应该显示前一天的日期）
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
	 * 门急诊区别，急诊病患列表多出检伤等级，到院时间等字段
	 */
	public void initOE() throws Exception{
		TButton erd = (TButton) odoMainControl.getComponent("ERD");
		TButton bodyTemp = (TButton) odoMainControl.getComponent("BODY_TEMP");
		TButton orderSheet = (TButton) odoMainControl.getComponent("ORDER_SHEET");
		TButton erdSheet = (TButton) odoMainControl.getComponent("ERD_SHEET");
		tblPat = (TTable) odoMainControl.getComponent(TABLEPAT);
		// ==========pangben 2012-6-28 添加初复诊列值VISIT_CODE
		if (O.equalsIgnoreCase(admType)) {
			odoMainControl.setTitle("门诊医生站");
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
		odoMainControl.setTitle("急诊医生站");
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
	 * 设置SESSION combo的门急属性，并返回当前的SESSION_CODE
	 * 
	 * @return String sessionCode
	 */
	public String initSessionCode() throws Exception{
		// 为了界面的SESSION_CODE显示门急诊区别，放置一个不显示的TEXTFIELD。
		String sessionCode = SessionTool.getInstance().getDefSessionNow(
				admType, Operator.getRegion());
		odoMainControl.setValue("SESSION_CODE", sessionCode);
		return sessionCode;
	}
	
	/**
	 * 初始化诊间combo
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
	 * 初始化代诊暂存完成的radioButton
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
			odoMainControl.messageBox("E0024"); // 初始化参数失败
			return;
		}
		radioNo.setFilter(" SEE_DR_FLG='N'");
		radioNo.filter();
		TLabel label = (TLabel) odoMainControl.getComponent("WAIT_NO");
		label.setZhText(radioNo.rowCount() + " 人");
		label.setEnText(radioNo.rowCount() + " P(s)");
		label.changeLanguage(Operator.getLanguage());
		radioNo.setFilter(" SEE_DR_FLG='Y'");
		radioNo.filter();
		label = (TLabel) odoMainControl.getComponent("DONE_NO");
		label.setZhText(radioNo.rowCount() + " 人");
		label.setEnText(radioNo.rowCount() + " P(s)");
		label.changeLanguage(Operator.getLanguage());
		radioNo.setFilter("SEE_DR_FLG='T'");
		radioNo.filter();
		label = (TLabel) odoMainControl.getComponent("TEMP_NO");
		label.setZhText(radioNo.rowCount() + " 人");
		label.setEnText(radioNo.rowCount() + " P(s)");
		label.changeLanguage(Operator.getLanguage());
	}
	
	/**
	 * 初始化医生站病患信息
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
		// 初始化odo对象
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
			odoMainControl.messageBox("E0024"); // 初始化参数失败
			//deleteLisPosc = false;
			return;
		}
		
		//对于小于16岁（含16岁）患者体重信息进行判断 add by huangtt 20180911
		String age = this.odoMainPat.patAge(parm.getTimestamp("BIRTH_DATE", row));
		if(checkWeight(age,odoMainControl.odo.getRegPatAdm().getItemString(0, "WEIGHT"))){
			odoMainControl.messageBox("本次就诊无体重数据，请及时录入！");
		}
		
		// 初始化pat对象
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
			odoMainControl.messageBox("E0117"); // 药房没有开启窗口，不能开诊
			odoMainControl.closeWindow();
		}
		odoMainControl.setValue("MED_RBORDER_DEPT_CODE", odoMainOpdOrder.phaCode);
		odoMainControl.setValue("CTRL_RBORDER_DEPT_CODE", odoMainOpdOrder.phaCode);
//		odoMainControl.setValue("OP_EXEC_DEPT", parm.getValue("REALDEPT_CODE", row));
		odoMainControl.setValue("OP_EXEC_DEPT", Operator.getCostCenter());//处置页签执行科室初始化为成本中心，yanjing 20141128
		if (ODOMainOpdOrder.C.equalsIgnoreCase(odoMainOpdOrder.wc)) {
			odoMainControl.setValue("CHN_EXEC_DEPT_CODE", odoMainOpdOrder.phaCode);
		}
		odoMainPat.lastMrNo = odoMainPat.pat.getMrNo();
	}
	
	
	
	/**
	 * 判断病人小于16岁的有没有填写体重
	 * @param mrNo
	 * @return
	 */
	public boolean checkWeight(String age,String weight){
//		System.out.println("age----"+age);
//		System.out.println("weight----"+weight);
	     int ageShowInt = age.indexOf("岁");
	     String age1 = "0";
	     if(ageShowInt > 0){
	    	 age1 =age.substring(0, age.indexOf("岁"));
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
	 * 初始化REG对象
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
	 * 初始化代诊科室
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
	 * 代诊科别点击时间，将代诊医师combo置为可用，并初始化代诊医师combo
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
		if (E.equals(admType)) { // 急诊时 代诊可以显示该科室的所有病人信息
			this.onSelectPat("INSTEAD_DEPT");
		}
	}
	
	/**
	 * 权限初始化 是否可以代诊
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
	 * 点选病人，初始化医生站
	 * @throws Exception 
	 */
	public void onTablePatDoubleClick() throws Exception {
		TTable table = (TTable) odoMainControl.getComponent(TABLEPAT);
		int row = table.getSelectedRow();
		PatSelectRow=row; //add by huangtt 20131128
		//add by huangtt 20140926 start
		//判断是该患者是否退挂
		boolean unRegFlg = REGTool.getInstance().queryUnReg(parmpat.getValue("CASE_NO", row));
		if(unRegFlg){
			odoMainControl.messageBox("该病患已退挂,请重新刷新病患列表");
			onSelectPat(NULLSTR);
			return;
		}
		//add by huangtt 20140926 end
		//斯巴达
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
			odoMainControl.messageBox("E0119"); // 门急诊标记错误
			odoMainControl.closeWindow();
		}
		odoMainControl.onClear();
		initOpd(parmpat, row);
		odoMainTjIns.initINS();
		viewClinicFee();
		
		// 判断reg主档的SEEN_DR_TIME是否有数据 如果为空记录当前时间   add by huangtt 20150128 
		if (odoMainControl.odo.getRegPatAdm().getItemData(0, "SEEN_DR_TIME") == null) {
			odoMainControl.odo.getRegPatAdm().setItem(0, "SEEN_DR_TIME",
					SystemTool.getInstance().getDate());
		}
		
	}
	
	/**
	 * 病患列表展开事件
	 */
	public void onPat() throws Exception{
		// 判断科室权限
		if (odoMainControl.getPopedem("DEPT_POPEDEM"))
			onSelectPat("INSTEAD_DEPT");
		else
			onSelectPat(NULLSTR);
		odoMainControl.mp.onDoubleClicked(false);
	}
	
	/**
	 * 排队叫号
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
		//叫号同步
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
	 * 预约挂号
	 */
	public void onReg() throws Exception{
		if (odoMainControl.odo == null)
			return;
		String MR_NO = odoMainControl.odo.getMrNo();
		TParm parm = new TParm();
		parm.setData("MR_NO", MR_NO);
		parm.setData("NHI_NO", odoMainPat.pat.getNhiNo());// 医保卡号
		odoMainControl.openWindow(URLREGADMFORDR, parm);
	}
	
	/**
	 * CRM医师预约
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
	 * 非常态门诊
	 * @throws Exception 
	 */
	public void onAbnormalReg() throws Exception {
		Object obj = odoMainControl.openDialog(URLOPDABNORMALREG);
		if (obj == null || !(obj instanceof TParm)) {
			return;
		}
		TParm parm = (TParm) obj; // 非常态门诊挂号信息
		odoMainOpdOrder.wc = ODOMainOpdOrder.W; // 默认为西医
		initOpd(parm, 0); // 初始化医生站信息
	}
	
	/**
	 * 筛选病患
	 * 
	 * @param type
	 *            String 表示 那个控件调用该方法
	 */
	public void onSelectPat(String type) throws Exception{
		boolean isDedug=true; //add by huangtt 20160505 日志输出
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
			// 如果是诊间选择事件 那么不用更新诊间
			if (!"CLINICROOM".equals(type)) {
				initClinicRoomCombo();
			}
			// 如果是“时段”combo调用或者是 看诊日期事件，那么清空 诊间combo
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
					System.out.println(" come in class: ODOMainReg.class ，method ：onSelectPat-INSTEAD_DR");
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
					System.out.println(" come in class: ODOMainReg.class ，method ：onSelectPat-INSTEAD_DEPT");
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 就诊序号回车查询事件
	 * @throws Exception 
	 */
	public void onQueNo() throws Exception {
		if (parmpat == null || parmpat.getCount() < 1) {
			odoMainControl.messageBox("E0025"); // 没有病人
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
					odoMainControl.messageBox("E0119"); // 门急诊标记错误
					odoMainControl.closeWindow();
				}
				odoMainControl.caseNo=parmpat.getValue("CASE_NO", i);//=====就诊号重新赋值 pangben 2014-1-20
				initReg(parmpat.getValue("MR_NO", i), parmpat.getValue(
						"CASE_NO", i));
				if (!odoMainControl.odo.onQuery()) {
					odoMainControl.messageBox("E0024"); // 初始化参数失败
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
		odoMainControl.messageBox("E0025"); // 没有病人
	}
	
	/**
	 * 门诊病患查询
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
			odoMainControl.messageBox("E0005"); // 执行失败
			return;
		}
		jsEnd();
	}
	/**
	 * 门诊护士站跑马灯下一个
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
			odoMainControl.messageBox("E0005"); // 执行失败
			return null;
		}
		if (parmpat.getCount()<=0) {
			return null;
		}
		return parmpat.getRow(i);
	}
	/**
	 * 急诊病患查询
	 */
	public void onEPatQuery() throws Exception{
		TParm parm = new TParm();
		// parm.setData("DR_CODE", Operator.getID());//急诊要求能看到本科室的所有病人
		// 所以就不以医师为条件了
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
			odoMainControl.messageBox("E0005"); // 执行失败
			return;
		}
		// liudy
		jsStart();
	}
	
	// $$=================add by lx 2011/02/12
	// Start呼叫接口=============================$$//
	/**
	 * 重叫
	 */
	public void onReCallNo() throws Exception{
		// 看诊医师代码 Varchar 6 000234
		String drCode = Operator.getID() + "|";
		// IP地址 Varchar 15 172.201.132.123
		String ip = Operator.getIP();

		TParm inParm = new TParm();
		inParm.setData("msg", drCode + ip);
		
		TIOM_AppServer.executeAction("action.device.CallNoAction", "doReCall",
				inParm);
		//=======yanjing 20140403 用RootServer通讯服务器向对应诊区的护士站发送消息
//		sendMedMessages(onPatQuerySendOnw());
		sendMedMessages(Operator.getID(),Operator.getIP());
		
	}
	
	/**
	 * 下一个
	 */
	public void onNextCallNo() throws Exception{
		String drCode = Operator.getID() + "|";
		// IP地址 Varchar 15 172.201.132.123
		String ip = Operator.getIP();
		TParm inParm = new TParm();
		inParm.setData("msg", drCode + ip);
	
		TIOM_AppServer.executeAction("action.device.CallNoAction",
				"doNextCall", inParm);
//		sendMedMessages(onPatQuerySendOnw());
		sendMedMessages(Operator.getID(),Operator.getIP());
		
	}
	// $$=================add by lx 2011/02/12
	// 呼叫接口=============================$$//
	
	/**
	 * 叫号登陆方法
	 * 
	 * @param type
	 *            String 1登陆,0退出
	 */
	public void SynLogin(String type) throws Exception{
		CallNo callUtil = new CallNo();
		if (!callUtil.init()) {
			return;
		}
	}
	
	/**
	 * 保存reg对象
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
		patInfo.setItem(0, "PREGNANT_DATE", odoMainControl.getValue("EDC_DATE"));//预产期，add by huangjw 20141016
		patInfo.setItem(0, "BREASTFEED_STARTDATE", odoMainControl
				.getValue("BREASTFEED_STARTDATE"));
		patInfo.setItem(0, "BREASTFEED_ENDDATE", odoMainControl
				.getValue("BREASTFEED_ENDDATE"));
		String nowValue = odoMainOther.familyWord.getCaptureValue("FAMILY_HISTORY");
		String oldValue = patInfo.getItemString(0, "FAMILY_HISTORY");
		String nowValue1=odoMainOther.familyWord.getCaptureValue("PAST_HISTORY");
		String oldValue1 = patInfo.getItemString(0, "PAST_HISTORY");
		if (!nowValue.equalsIgnoreCase(oldValue)||nowValue1.equalsIgnoreCase(oldValue1)) {
			// 2:未保存过
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
			// 3:保存过
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
		// add by wangb 2017/10/12 相关病史增加手术输血史
		patInfo.setItem(0, "OP_BLOOD_HISTORY", odoMainOther.familyWord
				.getCaptureValue("OP_BLOOD_HISTORY"));
		regPatAdm.setItem(0, "CTZ1_CODE", odoMainControl.getValue("CTZ1_CODE"));
		regPatAdm.setItem(0, "CTZ2_CODE", odoMainControl.getValue("CTZ2_CODE"));
		regPatAdm.setItem(0, "CTZ3_CODE", odoMainControl.getValue("CTZ3_CODE"));
		regPatAdm.setItem(0, "WEIGHT", TypeTool.getDouble(odoMainControl
				.getValue("WEIGHT")));
		regPatAdm.setItem(0, "VISIT_STATE", odoMainControl.getValueString("VISIT_STATE"));
		regPatAdm.setItem(0, "LMP_DATE", odoMainControl.getValue("LMP_DATE"));
		// 判断reg主档的REALDR_CODE是否有数据 如果为空记录当前的系统人员
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
//				odoMainControl.messageBox("诊查费插入失败");
//			}
//		}
	}
	
	public void onRegClear() throws Exception{
		reg = null;
		odoMainControl.clearValue("CTZ_CODE;SERVICE_LEVEL;CLINIC_FEE;VISIT_STATE;REASSURE_FLG");
	}
	
	/**
	 * 急诊变色线程开启
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
	 * 急诊变色线程结束
	 */
	public void jsEnd() {
		erdThread = null;
	}

	/**
	 * 急诊变色线程运行
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
	 * 判断是否可以修改医嘱 门诊：ADM_DATE的当天23点59分59秒以后不可以再对病患的医嘱进行修改（可以删除）
	 * 急诊：ADM_DATE的三天后的23点59分59面以后不可以再对病患的医嘱进行修改（可以删除）
	 * 
	 * @return boolean
	 */
	public boolean canEdit() throws Exception {
		Timestamp admDate = reg.getAdmDate(); // 挂号日期
		Timestamp now = SystemTool.getInstance().getDate(); // 当前时间
		if (O.equals(admType)) {
			// 获取挂号当天23点59分59秒的时间（门诊修改医嘱的限定时间）
			Timestamp time = StringTool.getTimestamp(StringTool.getString(
					admDate, "yyyyMMdd")
					+ "235959", "yyyyMMddHHmmss");
			// 当前时间晚于修改医嘱的限定时间 则不可以修改
			if (now.getTime() > time.getTime()) {
				return false;
			}
		} else if (E.equals(admType)) {
			// 急诊 获取医生站参数档中“限定看诊天数”的23点59分59秒的时间（急诊修改医嘱的限定时间）
			// 急诊晚班 是特殊情况 例如3/5 晚上挂号，一直到 3/6 全天都视为有效
			int OPDDay = OPDSysParmTool.getInstance().getEDays();
			String sessionCode = odoMainControl.getValueString("SESSION_CODE");
			if (sessionCode.equalsIgnoreCase("8") && OPDDay == 0) { // 急诊晚班
				OPDDay++;
			}
			Timestamp time = StringTool.getTimestamp(StringTool.getString(
					StringTool.rollDate(admDate, OPDDay), "yyyyMMdd")
					+ "235959", "yyyyMMddHHmmss");
			// 当前时间晚于修改医嘱的限定时间 则不可以修改
			if (now.getTime() > time.getTime()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 保存
	 * @throws Exception
	 */
	public boolean onSave() throws Exception{
		//add by huangtt 20140227 strat
		//判断就诊状态是否为空
		String visitState = odoMainControl.getValueString("VISIT_STATE");
		if(visitState.equals("")){
			odoMainControl.messageBox("请选择就诊状态！");
			return true;
		}
		odoMainControl.odo.getRegPatAdm().setItem(0, "VISIT_STATE", visitState);
		//add by huangtt 20140227 end
		saveRegInfo();
		odoMainControl.odo.getRegPatAdm().setItem(0, "SEE_DR_FLG", "Y");
		// 判断reg主档的SEEN_DR_TIME是否有数据 如果为空记录当前时间
		if (odoMainControl.odo.getRegPatAdm().getItemData(0, "SEEN_DR_TIME") == null) {
			odoMainControl.odo.getRegPatAdm().setItem(0, "SEEN_DR_TIME",
					SystemTool.getInstance().getDate());
		}
		odoMainControl.odo.getRegPatAdm().setItem(0, "LMP_DATE", odoMainControl.getValue("LMP_DATE"));
		
		//huangtt 20131128 start
		tblPat.acceptText();
		String firstFlg=tblPat.getParmValue().getValue("FIRST_FLG", PatSelectRow);
		if(firstFlg.equals("首诊")){
			odoMainControl.odo.getRegPatAdm().setItem(0, "FIRST_FLG", "Y");
		}
		if(firstFlg.equals("再诊")){
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
	 * 更新挂号档中的的报告状态  add by huangtt 20150720 
	 * 
	 */
	public void onSaveReportStatus(){
		//add by huangtt 20150720 start  检验检查没有开立的话，更新reg_patadm表中REPORT_STATUS为0
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
			odoMainControl.messageBox("更新挂号档报告状态失败");
		}
		//add by huangtt 20150720 end
		
	}
	
	/**
	 * 为病患信息加上首再诊
	 * add by huangtt 20131128
	 */
	public void FirstNext() throws Exception{
		for(int i=0;i<parmpat.getCount("MR_NO");i++){
			if(parmpat.getValue("FIRST_FLG", i).equals("")){
				String mr_no = parmpat.getValue("MR_NO", i);
				String dept_code = parmpat.getValue("REALDEPT_CODE",i);
				String sql = "SELECT COUNT(MR_NO) SUM FROM SYS_EMR_INDEX WHERE MR_NO = '"+mr_no+"' AND DEPT_CODE = '"+dept_code+"'";
				TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
				//没有数据，则为首诊
				if(selParm.getInt("SUM", 0)<=1){
					parmpat.setData("FIRST_FLG", i, "首诊");
			    //再诊
				}else{
					parmpat.setData("FIRST_FLG", i, "再诊");
				}
			}else{
				if(parmpat.getValue("FIRST_FLG", i).equals("Y")){
					parmpat.setData("FIRST_FLG", i, "首诊");
				}else{
					parmpat.setData("FIRST_FLG", i, "再诊");
				}
			}
		}
	}
	
	/**
	 * 显示诊查费
	 * ==zhangp
	 */
    public void viewClinicFee() throws Exception{
    	double clinicFee = EKTpreDebtTool.getInstance().getClinicFee(odoMainControl.caseNo);
    	odoMainControl.setValue("CLINIC_FEE", StringTool.round(clinicFee,2));
    }
    
	/**
	 * 如果有预开检查   写一条预约挂号信息
	 * 
	 * 新生成一个case_no
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
        tp.setData("REGMETHOD_CODE", "T");//挂号方式,电话人工
        tp.setData("SERVICE_LEVEL", reg.getServiceLevel());//服务等级
        tp.setData("ADM_TYPE", admType);//门急住别，预约信息表数据显示
        tp.setData("REGION_CODE", Operator.getRegion());
        tp.setData("CTZ1_CODE", parm.getData("CTZ1_CODE"));//身份
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
        tp.setData("IS_PRE_ORDER","Y");//是否是预开检查
			TParm onSaveP = REGAdmForDRTool.getInstance().onSavePreOrder(tp);
			if(onSaveP.getErrCode()<0){
				odoMainControl.messageBox("预开检查保存失败！");
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
											odoMainControl.messageBox("非本科室读卡不能登录门诊医生站");
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
				odoMainControl.setValue("LBL_EKT_MESSAGE", "已读卡");//====pangben 2013-3-19 添加读卡
				odoMainControl.odoMainEkt.ekt_lable.setForeground(OdoMainControl.GREEN);//======yanjing 2013-06-14设置读卡颜色
				return false;
			}
		}
		return true;
	}
	/**
	 * 向对应的门诊护士站发送消息
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
		//判断该病人是否为退挂人员  true 退挂
		
		if(REGTool.getInstance().queryUnReg(reg.caseNo())){
			return true;
		}
		return false;
	}
	
	/**
	 * 病历查询   add by huangtt 20150205
	 */
	public void onOpdEmrQuery() {
		TTable table = (TTable) odoMainControl.getComponent(TABLEPAT);
		int row = table.getSelectedRow();
		if(row == -1){
			odoMainControl.messageBox("请选择病人");
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", table.getItemString(row, "MR_NO"));
		odoMainControl.openDialog(URLOPDEMR, parm);
	}
	
}
