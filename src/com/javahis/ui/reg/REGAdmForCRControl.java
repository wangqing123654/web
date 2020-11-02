package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import jdo.reg.PanelRoomTool;
import jdo.reg.REGAdmForDRTool;
import jdo.reg.Reg;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
/**
 * <p>Title: CRM医师预约挂号</p>
 *
 * <p>Description:CRM医师预约挂号 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:bluecore </p> 
 *
 * @author huangtt
 * @version 1.0
 */
public class REGAdmForCRControl extends TControl {

	private  String ADM_TYPE = "O";
	private  String MR_NO = "";
	private  String ADM_DATE = ""; // 时间
	private  String DEPT_CODE = ""; // 科室
	private  String DR_CODE = ""; // 医生
	private  String START_TIME = ""; // 预约时间
	private  String CTZ1_CODE1 = ""; // 身份

	private  String caseNO; // 就诊号
	private  String sessionCode; // 就诊时段
	private  String CLINICROOM_NO; // 诊间
	private  String CLINICTYPE_CODE; // 号别
	private  String QUE_STATUS; 
	private  String QUE_NO;// 诊号
	private TParm tpVip; // his的vip班表信息
	private   String preOrderFlg = "";//预开检查标记
    private   String oldCaseNo = "";//原来的就诊号   yanjing 20131230
    private boolean crmFlg = true; // crm接口开关


	/**
	 * 初始化
	 */
	public void onInit() {

		// 接收数据
		Object obj = getParameter();
		TParm t;
		MR_NO = "";
		if (obj != null) {
			t = (TParm) obj;
			MR_NO = t.getValue("MR_NO");
			this.setValue("MR_NO", MR_NO);
			this.queryNo();
			// System.out.println("MR_NO=="+t.getValue("MR_NO"));
		}
		crmFlg = StringTool.getBoolean(TConfig.getSystemValue("crm.switch"));
		// 得到当前时间
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("ADM_DATE", StringTool.rollDate(date, +7).toString()
				.substring(0, 10).replace('-', '/'));

		// add by huangtt 20131205 start
		this.callFunction("UI|SESSION_CODE|AdmType", "O");
		this.callFunction("UI|SESSION_CODE|Region", Operator.getRegion());
		callFunction("UI|SESSION_CODE|onQuery");
		// add by huangtt 20131205 end
		callFunction("UI|DEPT_CODE|onQuery");

		String sessionCode = this.getSession_code(date);
		this.setValue("SESSION_CODE", sessionCode);
		this.setValue("DEPT_CODE", Operator.getDept());
		String w = this.getValue("ADM_DATE").toString().substring(0, 10)
				.replace("-", "");
		TParm parm = QueryDRName(this.getValueString("SESSION_CODE"), Operator
				.getDept(), w);
		this.getComboBox("DR_CODE").setParmValue(parm);
		this.setValue("DR_CODE", Operator.getID());
		
		 this.onInitTable1();//初始化挂号信息
//		 this.onInitSchdayTable();//初始化班表信息

	}

	public void queryNo() {
		MR_NO = PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
		TParm Tparm = this.QuseryPatMess(MR_NO);
		this.setValue("MR_NO", MR_NO);
		this.setValue("PAT_NAME", Tparm.getValue("PAT_NAME", 0));
		this.setValue("PY1", Tparm.getValue("PY1", 0));
		this.setValue("BIRTH_DATE", Tparm.getValue("BIRTH_DATE", 0).toString()
				.substring(0, 10).replace('-', '/'));
		this.setValue("SEX_CODE", Tparm.getValue("SEX_CODE", 0));
		this.setValue("FOREIGNER_FLG", Tparm.getValue("FOREIGNER_FLG", 0));
		this.setValue("ID_TYPE", Tparm.getValue("ID_TYPE", 0));
		this.setValue("IDNO", Tparm.getValue("IDNO", 0));
		this.setValue("TEL_HOME", Tparm.getValue("TEL_HOME", 0));
		this.setValue("POST_CODE", Tparm.getValue("POST_CODE", 0));
		this.setValue("ADDRESS", Tparm.getValue("ADDRESS", 0));
		CTZ1_CODE1 = Tparm.getValue("CTZ1_CODE", 0);
		onInitTable1();

	}

	/**
	 * 初始化挂号信息表 yanjing 20131230
	 */
	private void onInitTable1() {
		// 得到当前时间
		Timestamp date = SystemTool.getInstance().getDate();
		TParm tparm = QueryREGPatMess(MR_NO);
		this.getTTable("Table1").setParmValue(tparm);
	}

	/**
	 * 初始化班表信息
	 * 
	 */
	private void onInitSchdayTable() {

		String admDate = this.getValueString("ADM_DATE").toString().substring(
				0, 10);
		String session = this.getValueString("SESSION_CODE");
		String deptCode = this.getValueString("DEPT_CODE");
		String drCode = this.getValueString("DR_CODE");
		String quegroup = this.getValueString("CLINICTYPE_CODE");
		tpVip = new TParm(TJDODBTool.getInstance().select(
				REGAdmForDRTool.QueryVipCrm(admDate.replace("-", ""), drCode, session, deptCode,
						"O",quegroup)));
//		System.out.println("vip=="+tpVip);
		if(crmFlg){
			TParm regParm = new TParm();
			TParm parm = new TParm();
			parm.setData("admDate", admDate);
			parm.setData("session", this.getValueString("TIME"));
			parm.setData("deptCode", deptCode);
			parm.setData("drCode", drCode);
			parm.setData("quegroup", quegroup);
//			System.out.println("parm--" + parm);
			TParm result = TIOM_AppServer.executeAction("action.reg.REGCRMAction",
					"getOrder", parm);
			System.out.println("result=="+result);
			for(int j=0;j<tpVip.getCount();j++){
				for(int i=0;i<result.getCount();i++){
					String time = result.getValue("START_TIME", i).substring(0, 5).replace(":", "");
					if (result.getValue("DEPT_CODE", i).equals(tpVip.getValue("DEPT_CODE", j)) &&
							result.getValue("CLINICTYPE_CODE", i).equals(tpVip.getValue("CLINICTYPE_CODE", j)) &&
							result.getValue("DR_CODE", i).equals(tpVip.getValue("DR_CODE", j)) &&
							time.equals(tpVip.getValue("START_TIME", j))) {
						regParm.addData("CLINICTYPE_CODE", tpVip.getValue("CLINICTYPE_CODE", j));
						regParm.addData("SESSION_CODE", tpVip.getValue("SESSION_CODE", j));
						regParm.addData("DEPT_CODE", tpVip.getValue("DEPT_CODE", j));
						regParm.addData("CLINICROOM_NO", tpVip.getValue("CLINICROOM_NO", j));
						regParm.addData("DR_CODE", tpVip.getValue("DR_CODE", j));
						regParm.addData("QUE_NO", tpVip.getValue("QUE_NO", j));
						regParm.addData("QUEGROUP_CODE", tpVip.getValue("QUEGROUP_CODE", j));
						regParm.addData("START_TIME", result.getValue("START_TIME", i));
						regParm.addData("QUE_STATUS", tpVip.getValue("QUE_STATUS", j));
						if("0".equals(result.getValue("STATUS", i))){
							regParm.addData("STATUS", "N");
						}else{
							regParm.addData("STATUS", "Y");
						}
						
						regParm.addData("ADM_DATE", result.getValue("ADM_DATE", i));
						break;
					}
				}
			}
			
//			System.out.println("regParm=="+regParm);
			regParm.setCount(regParm.getCount("DEPT_CODE"));
			if (regParm.getCount() < 0) {
				this.messageBox("没有要查询的数据");
				return;
			}
			this.getTTable("Table2").setParmValue(regParm);// VIP班表
		}else{
			for(int i=0;i<tpVip.getCount();i++){
				String oldAdmDate = tpVip.getValue("ADM_DATE", i);
//				System.out.println("oldAdmDate===="+oldAdmDate);
				tpVip.setData("ADM_DATE", i, oldAdmDate.subSequence(0, 4)+"-"+oldAdmDate.subSequence(4, 6)+"-"+oldAdmDate.subSequence(6, 8));
			}
			this.getTTable("Table2").setParmValue(tpVip);// VIP班表
		}
		
		

	}

	/**
	 * 获得TComboBox
	 * 
	 * @param tagName
	 *            String
	 * @return TComboBox
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * 查询当班医生信息，返回给下拉框
	 * 
	 * @param sessionCode
	 *            String
	 * @param dept_code
	 *            String
	 * @param w
	 *            int
	 * @return TParm
	 */
	public TParm QueryDRName(String sessionCode, String dept_code, String w) {

		String sql = "SELECT REG_SCHDAY.DR_CODE AS DR_CODE,SYS_OPERATOR.USER_NAME AS USER_NAME"
				+ "  FROM   REG_SCHDAY,SYS_OPERATOR"
				+ "  WHERE   ADM_DATE = '"
				+ w
				+ "' AND SESSION_CODE = '"
				+ sessionCode
				+ "' AND DEPT_CODE = '"
				+ dept_code
				+ "' AND SYS_OPERATOR.USER_ID=REG_SCHDAY.DR_CODE";
		// System.out.println("sql----"+sql);

		TParm parm = new TParm(TJDODBTool.getInstance().select(
				REGAdmForDRTool.QueryDRNameSql(sessionCode, dept_code, w)));
		return parm;
	}

	/**
	 * 患者基本信息
	 * 
	 * @param MR_NO
	 *            String
	 * @return TParm
	 */
	public TParm QuseryPatMess(String MR_NO) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				REGAdmForDRTool.QuseryPatMessSql(MR_NO)));
		System.out.println(REGAdmForDRTool.QuseryPatMessSql(MR_NO));
		return parm;
	}

	/**
	 * 退挂事件
	 */
	public void onUnReg() {
		TParm result = new TParm();
		if(crmFlg){
			TParm parm = new TParm();
			parm.setData("mrNo", this.getValueString("MR_NO"));
			parm.setData("date", ADM_DATE.replace("-", ""));
			parm.setData("timeInteval", START_TIME);
			parm.setData("dept", DEPT_CODE);
			parm.setData("doc", DR_CODE);
			parm.setData("quegroup", CLINICTYPE_CODE);
			System.out.println("parm==" + parm);
			result = TIOM_AppServer.executeAction("action.reg.REGCRMAction",
					"cancleOrderInfo", parm);
			if (result.getBoolean("flg", 0)) {
				this.messageBox("CRM诊间预约取消成功");
			} else {
				this.messageBox("CRM诊间预约取消失败");
				return;
			}
		}
		

		// System.out.println("caseNO=="+caseNO);
		Timestamp date = SystemTool.getInstance().getDate();
		TParm t = new TParm();
		t.setData("CASE_NO", caseNO);
		t.setData("REGCAN_USER", Operator.getID());
		t.setData("REGCAN_DATE", date);
		t.setData("ADM_STATUS", "2"); // 已退挂
		result = REGAdmForDRTool.getInstance().onUpdate(t);
		String sql = "UPDATE REG_CLINICQUE SET QUE_STATUS = 'N' WHERE ADM_TYPE = '"
			+ ADM_TYPE
			+ "'AND ADM_DATE = '"
			+ ADM_DATE.replace("-", "")
			+ "' AND "
			+ "SESSION_CODE = '"
			+ sessionCode
			+ "' AND "
			+ "CLINICROOM_NO = '"
			+ CLINICROOM_NO
			+ "' AND "
			+ "QUE_NO = '"
			+ QUE_NO + "'";
	    TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql));
	    if (updateParm.getErrCode() < 0) {
		    messageBox("退挂失败");
		    return;
	    }
        this.messageBox("退挂成功");
		// huangtt start 20121105
		onInitTable1();
		onChangeDR();
		// huangtt end 20121105
	}

	/**
	 * Table1表格点击事件
	 * 
	 * @param date
	 *            Timestamp
	 * @return int
	 */
	public void onTableClick1() {
		TTable table = getTTable("Table1");
		int row = getTTable("Table1").getSelectedRow();
		TParm parm = table.getParmValue().getRow(row);
		caseNO = parm.getValue("CASE_NO");
		// System.out.println("caseNO=="+caseNO);
		// System.out.println(parm.getBoolean("APPT_CODE"));
		preOrderFlg = parm.getData("IS_PRE_ORDER").toString();//预开检查挂号标记,yanjing 20131227
        oldCaseNo = parm.getData("OLD_CASE_NO").toString();//获取原来的就诊号,yanjing 20131227
//        String preAdmDate = parm.getData("ADM_DATE").toString();
        String preAdmDate = parm.getValue("ADM_DATE");
        if(preOrderFlg.equals("Y")){//预开检查时时间自动赋值  
        	if(preAdmDate.equals("")){
//        		this.setValue("start_Date","");
        	}else{
        	this.setValue("ADM_DATE",preAdmDate.substring(0, 10).replace('-', '/'));
        	}
        }
		if (parm.getBoolean("APPT_CODE")) {
			this.callFunction("UI|unreg|setEnabled", true);
			// caseNO = parm.getData("CASE_NO").toString();
			ADM_TYPE = parm.getData("ADM_TYPE").toString();
			sessionCode = parm.getData("SESSION_CODE").toString();
			CLINICROOM_NO = parm.getData("CLINICROOM_NO").toString();
			ADM_DATE = parm.getData("ADM_DATE").toString().substring(0, 10)
					.replace("/", "-");
			QUE_NO = parm.getData("QUE_NO").toString();
			START_TIME = parm.getData("REG_ADM_TIME").toString();
			DEPT_CODE = parm.getData("DEPT_CODE").toString();
			DR_CODE = parm.getData("DR_CODE").toString();
			CLINICTYPE_CODE = parm.getData("CLINICTYPE_CODE").toString();

		} else {
			this.messageBox("当前挂号不允许退挂");
			this.callFunction("UI|unreg|setEnabled", false);
			return;
		}

	}

	/**
	 * Table2表格事件
	 * 
	 * @param date
	 *            Timestamp
	 * @return int
	 */

	public void onTableClick2() {

		TTable table = getTTable("Table2");
		int row = getTTable("Table2").getSelectedRow();
		TParm parm2 = table.getParmValue().getRow(row);
		// =====huangtt 20131013 start
		 if(parm2.getBoolean("QUE_STATUS") || parm2.getBoolean("STATUS") ){
		 this.messageBox("该号不可预约，请重新选择！");
		 this.callFunction("UI|SAVE|setEnabled", false);
		 return;       	
		 }
		 this.callFunction("UI|SAVE|setEnabled",true );
		// =====huangtt 20131013 end
		// DEPT_CODE;DR_CODE;AMD_DATE;START_TIME;STATUS;QUE_STATUS
		DEPT_CODE = parm2.getData("DEPT_CODE").toString();
		DR_CODE = parm2.getData("DR_CODE").toString();
		ADM_DATE = parm2.getData("ADM_DATE").toString();
		START_TIME = parm2.getData("START_TIME").toString();
		sessionCode = parm2.getData("SESSION_CODE").toString();
		QUE_NO = parm2.getData("QUE_NO").toString();
		QUE_STATUS = parm2.getData("QUE_STATUS").toString();
		CLINICTYPE_CODE = parm2.getData("CLINICTYPE_CODE").toString();
		CLINICROOM_NO = parm2.getData("CLINICROOM_NO").toString();

		
//		this.messageBox(ADM_DATE);
		// System.out.println(START_TIME.replace(":", "").substring(0, 4));
		// for(int i=0;i<tpVip.getCount();i++){
		// if (tpVip.getValue("DEPT_CODE", i).equals(DEPT_CODE)
		// && tpVip.getValue("DR_CODE", i).equals(DR_CODE)
		// && tpVip.getValue("ADM_DATE", i).equals(ADM_DATE)
		// && tpVip.getValue("START_TIME", i).equals(START_TIME.replace(":",
		// "").substring(0, 4))) {
		//				
		// QUE_NO=tpVip.getValue("QUE_NO", i);
		// CLINICROOM_NO = tpVip.getValue("CLINICROOM_NO", i);
		// return;
		//				
		// }
		// }

	}

	/**
	 * 保存方法
	 * 
	 * @param date
	 *            Timestamp
	 * @return int
	 */
	public void onSave() {
		TParm result= new TParm();
		if(crmFlg){
			TParm parm = new TParm();
			parm.setData("mrNo", this.getValueString("MR_NO"));
			parm.setData("date", ADM_DATE);
			parm.setData("timeInteval", START_TIME.replace(":", "").substring(0,4));
			parm.setData("dept", DEPT_CODE);
			parm.setData("doc", DR_CODE);
			parm.setData("quegroup", CLINICTYPE_CODE); 
			System.out.println("parm==" + parm);
			result = TIOM_AppServer.executeAction("action.reg.REGCRMAction",
					"addOrderInfo", parm);
			if (result.getBoolean("flg", 0)) {
				this.messageBox("CRM预约成功");
			} else {
				this.messageBox("CRM预约失败,请重新预约！");
				onChangeDR();
				return;
			}
		}
		
		
		
		//his判断该号是否被占用 
		TParm tpVip = new TParm(TJDODBTool.getInstance().select(REGAdmForDRTool.
				QueryVipCrm(ADM_DATE,DR_CODE,sessionCode,DEPT_CODE,ADM_TYPE,CLINICTYPE_CODE)));
		
    	for(int i=0;i<tpVip.getCount();i++){
    		if(QUE_NO.equals(tpVip.getValue("QUE_NO", i))){
    			if(tpVip.getBoolean("QUE_STATUS", i)){
    				this.messageBox("该号已经被其他医师预约，请重新选择诊号！");
    				onChangeDR();
    				return;
    			}
    		}
    	}
    	
    	String newCaseNo = "";
    	if(preOrderFlg.equals("Y")){
    		newCaseNo = caseNO;
    		//查询reg_patadm表删除已有信息
    		String regSql = "DELETE REG_PATADM WHERE CASE_NO = '"+caseNO+"'";
        	TParm deleteParm = new TParm(TJDODBTool.getInstance().update(regSql));
    	}else{
            newCaseNo = SystemTool.getInstance().getNo("ALL", "REG",
            "CASE_NO", "CASE_NO");
    	}

		String APPT_CODE = "Y";
		String VISIT_CODE = "1";
		String REGMETHOD_CODE = "D";
		String ARRIVE_FLG = "N";
		String ADM_REGION = Operator.getRegion();
		String HEAT_FLG = "N";
		String ADM_STATUS = "1";
		String REPORT_STATUS = "1";
		String OPT_USER = Operator.getID();
		Timestamp OPT_DATE = SystemTool.getInstance().getDate();
		String OPT_TERM = Operator.getIP();
		// String CTZ1_CODE = "11";
		String CTZ1_CODE = CTZ1_CODE1;
		String admDate = this.getValue("ADM_DATE").toString().substring(0, 10)
				.replace("-", "")
				+ "000000"; // 这个将控件上的数据截取以后赋给变量
//		String newCaseNo = SystemTool.getInstance().getNo("ALL", "REG",
//				"CASE_NO", "CASE_NO");
		TParm tpv = new TParm();
		tpv.setData("IS_PRE_ORDER", preOrderFlg);//===========yanjing 20131230
        tpv.setData("OLD_CASE_NO", oldCaseNo);//============yanjing 20131230
		tpv.setData("CASE_NO", newCaseNo);
		tpv.setData("ADM_TYPE", "O");
		tpv.setData("MR_NO", MR_NO);
		tpv.setData("REGION_CODE", Operator.getRegion());
		tpv.setData("ADM_DATE", StringTool.getTimestamp(admDate,
				"yyyyMMddHHmmss"));
		tpv.setData("REG_DATE", SystemTool.getInstance().getDate());
		tpv.setData("SESSION_CODE", sessionCode);
		tpv.setData("CLINICAREA_CODE", (PanelRoomTool.getInstance()
				.getAreaByRoom(TypeTool.getString(CLINICROOM_NO)))
				.getValue("CLINICAREA_CODE", 0)); 
		tpv.setData("CLINICROOM_NO", CLINICROOM_NO);
		tpv.setData("CLINICTYPE_CODE", CLINICTYPE_CODE);
		tpv.setData("DEPT_CODE", DEPT_CODE);
		tpv.setData("DR_CODE", DR_CODE);
		// ===zhangp 20120628 start
		tpv.setData("REALDEPT_CODE", DEPT_CODE);
		tpv.setData("REALDR_CODE", DR_CODE);
		// ===zhangp 20120628 end
		tpv.setData("APPT_CODE", APPT_CODE);
		tpv.setData("VISIT_CODE", VISIT_CODE);
		tpv.setData("REGMETHOD_CODE", REGMETHOD_CODE);
		tpv.setData("CTZ1_CODE", CTZ1_CODE);
		tpv.setData("ARRIVE_FLG", ARRIVE_FLG);
		tpv.setData("ADM_REGION", ADM_REGION);
		tpv.setData("HEAT_FLG", HEAT_FLG);
		tpv.setData("ADM_STATUS", ADM_STATUS);
		tpv.setData("REPORT_STATUS", REPORT_STATUS);
		tpv.setData("ERD_LEVEL", "");
		tpv.setData("OPT_USER", OPT_USER);
		tpv.setData("OPT_DATE", OPT_DATE);
		tpv.setData("OPT_TERM", OPT_TERM);
		tpv.setData("VIP_FLG", "Y");
		tpv.setData("REG_ADM_TIME", START_TIME.replace(":", "").substring(0, 4));
		tpv.setData("QUE_NO", QUE_NO);
		tpv.setData("NHI_NO", "");// ===========panben 20110809
		result = REGAdmForDRTool.getInstance().onSaveV(tpv);
		if (result.getErrCode() < 0) {
			this.messageBox("HIS预约失败，请重新预约！");
			this.onInitTable1();
			
			return;
		}
		TParm  tparm = new TParm(TJDODBTool.getInstance().select(REGAdmForDRTool.
                QueryDRSql(ADM_DATE.toString().replace("-", "").substring(0, 8), DEPT_CODE, DR_CODE, sessionCode,"O")));
		String RS = tparm.getValue("QUE_NO", 0); // 就诊人数
		 int q = 0;
	        if(RS!=null&&!"".equals(RS))    
	            q = Integer.parseInt(RS);
	        q += 1;
	        String qu = "" + q;
	        TJDODBTool.getInstance().update(REGAdmForDRTool.updateDept(qu,
	        		ADM_DATE.toString().replace("-", "").
		            substring(0, 8), DEPT_CODE, DR_CODE,sessionCode, CLINICROOM_NO,ADM_TYPE,DR_CODE));

		

		onPrint(newCaseNo);
		this.onInitTable1();
		onChangeDR();

	}

	/**
	 * 根据当前日期，获得今天是星期几
	 * 
	 * @param date
	 *            Timestamp
	 * @return int
	 */
	public int getWeek(Timestamp date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK) - 1;
	}

	/**
	 * 根据当前日期，获得当前时段
	 * 
	 * @param date
	 *            Timestamp
	 * @return int
	 */
	public String getSession_code(Timestamp date) {
		String time = date.toString().substring(11, 19);
		String sql = "SELECT SESSION_CODE, START_REG_TIME, END_REG_TIME "
				+ " FROM REG_SESSION WHERE REGION_CODE = '"
				+ Operator.getRegion() + "' AND ADM_TYPE = 'O'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));

		for (int i = 0; i < parm.getCount("SESSION_CODE"); i++) {
			if (time.compareTo(parm.getValue("START_REG_TIME", i)) >= 0
					&& time.compareTo(parm.getValue("END_REG_TIME", i)) <= 0) {
				return parm.getValue("SESSION_CODE", i);
			}
		}

		return "";
	}

	/**
	 * 科室下拉菜单的点击事件
	 */
	public void onChange() {
		TParm parm = QueryDRName(this.getValueString("SESSION_CODE"), this
				.getValue("DEPT_CODE").toString(), this.getValue("ADM_DATE")
				.toString().substring(0, 10).replace("-", ""));
		// System.out.println("-----------"+parm);
		this.getComboBox("DR_CODE").setParmValue(parm);
	}

	/**
	 * 医生下拉菜单点击事件
	 */
	public void onChangeDR() {
		if (this.getValueString("DR_CODE").equals("")) {
			this.messageBox("请选择医生");
			return;
		}
		if (this.getValueString("DEPT_CODE").equals("")) {
			this.messageBox("请选择科室");
			return;
		}
//		 if(this.getValueString("SESSION_CODE").equals("")){
//		 this.messageBox("请选择时段");
//		 return;
//		 }
		String date = SystemTool.getInstance().getDate().toString().replace("-", "").replace("/", "").substring(0, 8);
		String date1 = this.getValueString("ADM_DATE").replace("-", "").replace("/", "").substring(0, 8);
		if(date1.equals(date)){
			this.messageBox("不能预约当天，请重新选择日期！");
			return;
		}
		
		this.getTTable("Table2").removeRowAll(); // add by huangtt 20131108
		onInitSchdayTable();

	}

	/**
	 * 查询患者挂号信息
	 * 
	 * @param MR_NO
	 *            String
	 * @param DR_CODE
	 *            String
	 * @param state_date
	 *            String
	 * @param end_date
	 *            String
	 * @param dept_code
	 *            String
	 * @param session_code
	 *            String
	 * @return TParm
	 */
	public TParm QueryREGPatMess(String MR_NO) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				REGAdmForDRTool
						.QueryREGPatOrder(MR_NO)));
		return parm;
	}

	/**
	 * 获得table
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	
	/**
     * 打印
     * @param caseNo
     */
    private void onPrint(String caseNo)
    {
      Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
      Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
  
      int que = reg.getQueNo();
      String sql1 = "SELECT B.CHN_DESC NAME FROM REG_CLINICROOM A, SYS_DICTIONARY B WHERE A.LOCATION = B.ID AND B.GROUP_ID = 'SYS_LOCATION'  AND A.CLINICROOM_NO='" + 
        getValueString("CLINICROOM_NO") + "'";
      TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
      String dept = "";
      if (!parm1.getValue("NAME", 0).equals("")) {
        dept = "(" + parm1.getValue("NAME", 0) + ")";
      }
      TParm data = new TParm();
      data.setData("TITLE", "TEXT", "");
      //病案号
      data.setData("MR_NO", "TEXT", getValueString("MR_NO"));
      //姓名
      data.setData("PAT_NAME", "TEXT", getValueString("PAT_NAME"));
      //性别
      data.setData("PAT_SEX", "TEXT", pat.getSexString());
      //预约时间
      TComboBox ts = (TComboBox)getComponent("SESSION_CODE");
      data.setData("APPOINTMENT", "TEXT", getText("ADM_DATE").toString().replace('/', '-') + " " + ts.getSelectedName());
      //诊号
      data.setData("QUE_NO", "TEXT", Integer.valueOf(que));
      //科室
      data.setData("DEPT", "TEXT", getText("DEPT_CODE") + dept);
      //诊区
      String area = reg.getClinicareaCode();
      String sql2 = "SElECT CLINIC_DESC FROM REG_CLINICAREA WHERE CLINICAREA_CODE='" + area + "'";
      TParm areaParm = new TParm(TJDODBTool.getInstance().select(sql2));
      data.setData("AREA", "TEXT", areaParm.getValue("CLINIC_DESC", 0));
      
        TTable table2 = (TTable)getComponent("Table2");
        int row2 = table2.getSelectedRow();
        TParm parm2 = table2.getShowParmValue();
        String clinicroom_no = parm2.getValue("CLINICROOM_NO", row2);
        //医生
        data.setData("DR", "TEXT", parm2.getValue("DR_CODE", row2));
        //诊室
        data.setData("ORG_CODE", "TEXT", clinicroom_no);
        StringBuffer time = new StringBuffer("");
        time.append(parm2.getValue("START_TIME", row2).substring(0, 2));
        time.append(":");
        if(crmFlg){
        	time.append(parm2.getValue("START_TIME", row2).substring(3, 5));
        }else{
        	time.append(parm2.getValue("START_TIME", row2).substring(2, 4));
        }
        
        //预计就诊时间
        data.setData("TIME", "TEXT", time);
      
      //说明
      data.setData("DESCRIPTION", "TEXT", "");
      //打印日期
      data.setData("DATE", "TEXT", StringTool.getTimestamp(new Date()).toString().substring(0, 19));
      openPrintWindow("%ROOT%\\config\\prt\\REG\\NEWREG.jhw", data);
    }
    /**
     * 补印
     * 
     */
    public void onDoublePrint()
    {
      TTable table = (TTable)getComponent("Table1");
      int row = table.getSelectedRow();
      TParm result = table.getParmValue();
      String case_no = result.getValue("CASE_NO", row);
      Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
      Reg reg = Reg.onQueryByCaseNo(pat, case_no);
      int que = reg.getQueNo();
      String sql1 = "SELECT B.CHN_DESC NAME FROM REG_CLINICROOM A, SYS_DICTIONARY B WHERE A.LOCATION = B.ID AND B.GROUP_ID = 'SYS_LOCATION'  AND A.CLINICROOM_NO='" + 
        getValueString("CLINICROOM_NO") + "'";
      TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
      String dept = "";
      if (!parm1.getValue("NAME", 0).equals("")) {
        dept = "(" + parm1.getValue("NAME", 0) + ")";
      }
      TParm data = new TParm();
      //病案号
      data.setData("MR_NO", "TEXT", getValueString("MR_NO"));
      //姓名
      data.setData("PAT_NAME", "TEXT", getValueString("PAT_NAME"));
      //性别
      data.setData("PAT_SEX", "TEXT", pat.getSexString());
      //预约时间
      //TComboBox ts = (TComboBox)getComponent("SESSION_CODE");
      String sql="SELECT A.ADM_DATE,(SELECT SESSION_DESC FROM REG_SESSION WHERE SESSION_CODE=A.SESSION_CODE) AS SESSION_CODE," +
      		" (SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE=A.DEPT_CODE) AS DEPT_CODE,(SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID=A.DR_CODE) AS DR_CODE" +
      		" FROM REG_PATADM A WHERE A.CASE_NO='"+case_no+"'";
      System.out.println("==========================+++++++++++++"+sql);
      TParm sqlParm=new TParm(TJDODBTool.getInstance().select(sql));
      data.setData("APPOINTMENT", "TEXT", sqlParm.getValue("ADM_DATE",0).toString().substring(0,10).replace('/', '-') + " " + sqlParm.getValue("SESSION_CODE",0));
      //诊号
      data.setData("QUE_NO", "TEXT", Integer.valueOf(que));
      //科室
      data.setData("DEPT", "TEXT", sqlParm.getValue("DEPT_CODE",0) + dept);
      
      String area = reg.getAdmRegion();
      String sql2 = "SElECT CLINIC_DESC FROM REG_CLINICAREA WHERE CLINICAREA_CODE='" + area + "'";
      TParm areaParm = new TParm(TJDODBTool.getInstance().select(sql2));
      //诊区
      data.setData("AREA", "TEXT", areaParm.getValue("CLINIC_DESC", 0));
      //医生
      String doctor = result.getValue("DR_CODE", row);
      String doctor_sql = "SELECT B.USER_NAME FROM REG_PATADM A,SYS_OPERATOR B WHERE A.CASE_NO='" + case_no + "' AND A.DR_CODE=B.USER_ID";
      TParm doctorParm = new TParm(TJDODBTool.getInstance().select(doctor_sql));
      data.setData("DR", "TEXT", sqlParm.getValue("DR_CODE", 0));
      //诊室
      String room = "SELECT A.CLINICROOM_DESC FROM REG_CLINICROOM A,REG_PATADM B  WHERE B.CASE_NO='" + 
        case_no + "' AND B.CLINICROOM_NO=A.CLINICROOM_NO ";
      TParm roomParm = new TParm(TJDODBTool.getInstance().select(room));
      //System.out.println("CLINICROOM_DESC+++" + roomParm.getValue("CLINICROOM_DESC", 0));
      data.setData("ORG_CODE", "TEXT", roomParm.getValue("CLINICROOM_DESC", 0));
      
        String timesql = "SELECT B.START_TIME FROM REG_PATADM A,REG_CLINICQUE B WHERE A.CASE_NO='" + case_no + "'" + 
          " AND A.ADM_TYPE=B.ADM_TYPE" + 
          " AND TO_CHAR(A.ADM_DATE,'YYYYMMdd')=B.ADM_DATE" + 
          " AND A.SESSION_CODE=B.SESSION_CODE" + 
          " AND A.CLINICROOM_NO=B.CLINICROOM_NO" + 
          " AND A.QUE_NO=B.QUE_NO";
        //预计就诊时间
        TParm timeParm = new TParm(TJDODBTool.getInstance().select(timesql));
        StringBuffer time = new StringBuffer("");
        time.append(timeParm.getValue("START_TIME", 0).substring(0, 2));
        time.append(":");
        time.append(timeParm.getValue("START_TIME", 0).substring(2, 4));
        data.setData("TIME", "TEXT", time);
      
      //说明
      data.setData("DESCRIPTION", "TEXT", " ");
      //打印日期
      data.setData("DATE", "TEXT", StringTool.getTimestamp(new Date()).toString().substring(0, 19));
      openPrintWindow("%ROOT%\\config\\prt\\REG\\NEWREG.jhw", data);
    }
    
}
