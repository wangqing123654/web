package com.javahis.ui.odo;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.sys.Operator;
import jdo.sys.PATLockTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTextFormat;
import com.javahis.system.root.RootClientListener;
import com.javahis.util.DateUtil;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * 
 * Title: 门诊医生工作站病患对象
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站病患对象
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODOMainPat {
	public OdoMainControl odoMainControl;
	public ODOMainReg odoMainReg;
    Pat pat;
	public String lastMrNo = "";// 上一个病人的mr_no
	public String[] ctz = new String[3]; // liudy // 身份全局变量
	
	private static final String URLSYSPATINFO = "%ROOT%\\config\\sys\\SYSPatInfo.x";
	private static final String URLSYSPATLCOKMESSAGE = "%ROOT%\\config\\sys\\SYSPatLcokMessage.x";
	private static final String URLSYSPATUNLCOKMESSAGE = "%ROOT%\\config\\sys\\SYSPatUnLcokMessage.x";
	
	private static final String NULLSTR = "";
	
	public ODOMainPat(OdoMainControl odoMainControl){
		this.odoMainControl = odoMainControl;
	}
	
	public void onInit() throws Exception{
		this.odoMainReg = odoMainControl.odoMainReg;
	}
	
	/**
	 * 初始化病患个人信息，包括病生理
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 */
	public void initPatInfo(TParm parm, int row) throws Exception{
		odoMainControl.setValue("VISIT_STATE", odoMainReg.reg.getVisitState()); //add by huangtt 20140307
		odoMainControl.setValue("QUE_NO", parm.getValue("QUE_NO", row));
		if (odoMainControl.isEng) {
			odoMainControl.setValue("PAT_NAME", parm.getValue("PAT_NAME1", row));
		} else {
			odoMainControl.setValue("PAT_NAME", parm.getValue("PAT_NAME", row));
		}
		odoMainControl.serviceLevel = odoMainReg.reg.getServiceLevel();
		odoMainControl.setValue("SERVICE_LEVEL", odoMainReg.reg.getServiceLevel());
		odoMainControl.setValue("CTZ_CODE", odoMainReg.reg.getCtz1Code());
		odoMainControl.setValue("INT_PAT_TYPE", odoMainReg.reg.getInsPatType());
		if (!StringUtil.isNullString(odoMainReg.reg.getInsPatType())
				&& !odoMainReg.reg.getInsPatType().equals("1")) {
			TTextFormat INS_DISEAS_CODE = (TTextFormat) odoMainControl
					.getComponent("INS_DISEAS_CODE");
			TParm cat1TypeParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT DISEASE_CODE FROM INS_MZ_CONFIRM WHERE CASE_NO='"
							+ odoMainControl.caseNo + "' AND MR_NO='"
							+ parm.getValue("MR_NO", row) + "'"));
			INS_DISEAS_CODE.setValue(cat1TypeParm.getValue("DISEASE_CODE", 0));
		}
		odoMainControl.odo.getOpdOrder().setServiceLevel(odoMainControl.serviceLevel); // opdorder对象设置服务等级
		odoMainControl.setValue("WEIGHT", odoMainControl.odo.getRegPatAdm().getItemString(0, "WEIGHT"));
		odoMainControl.setValue("MR_NO", parm.getValue("MR_NO", row));
		odoMainControl.setValue("SEX_CODE", odoMainControl.odo.getPatInfo().getItemString(0,
						"SEX_CODE"));
		odoMainControl.setValue("BIRTH_DATE", odoMainControl.odoMainPat.pat.getBirthday());
		
		String age = patAge(parm.getTimestamp("BIRTH_DATE", row));//年龄格式修改
		/*String age = OdoUtil.showAge(parm.getTimestamp("BIRTH_DATE", row),
				SystemTool.getInstance().getDate());*/
		odoMainControl.setValue("AGE", age);
		odoMainControl.setValue("PAT_PACKAGE", this.getPackage(odoMainReg.reg.caseNo()));
		if (!"2".equalsIgnoreCase(pat.getSexCode())) {
			TTextFormat tFormat = (TTextFormat) odoMainControl.getComponent("LMP_DATE");
			tFormat.setValue(NULLSTR);
			tFormat.setEnabled(false);
			tFormat = (TTextFormat) odoMainControl.getComponent("BREASTFEED_STARTDATE");
			tFormat.setValue(NULLSTR);
			tFormat.setEnabled(false);
			tFormat = (TTextFormat) odoMainControl.getComponent("BREASTFEED_ENDDATE");
			tFormat.setValue(NULLSTR);
			tFormat.setEnabled(false);
		} else {
			TTextFormat tFormat = (TTextFormat) odoMainControl.getComponent("LMP_DATE");
			tFormat.setEnabled(true);
			tFormat = (TTextFormat) odoMainControl.getComponent("BREASTFEED_STARTDATE");
			tFormat.setEnabled(true);
			tFormat = (TTextFormat) odoMainControl.getComponent("BREASTFEED_ENDDATE");
			tFormat.setEnabled(true);
		}
		odoMainControl.setValue("PAT1_CODE", parm.getValue("PAT1_CODE", row));
		odoMainControl.setValue("PAT2_CODE", parm.getValue("PAT2_CODE", row));
		odoMainControl.setValue("PAT3_CODE", parm.getValue("PAT3_CODE", row));
		odoMainControl.setValue("HANDICAP_FLG", parm.getValue("HANDICAP_FLG", row));
		odoMainControl.setValue("PREMATURE_FLG", parm.getValue("PREMATURE_FLG", row));
		//add by huangtt 高危产妇标识  start 20161008	 	
		odoMainControl.setValue("HIGHRISKMATERNAL_FLG", parm.getValue("HIGHRISKMATERNAL_FLG", row)); 
		if("Y".equals( parm.getValue("HIGHRISKMATERNAL_FLG", row))){			
		
			odoMainControl.setValue("MATERNAL_MESSAGE", "高危产妇");
		}else{
			odoMainControl.setValue("MATERNAL_MESSAGE", "");
		}
		//add by huangtt 高危产妇标识  end  20161008
		if(odoMainControl.odo.getRegPatAdm().getItemData(0, "LMP_DATE") == null){//末次月经时间，如果reg_patadm表的LMP_DATE不为空则按照挂号表显示，否则按照sys_patinfo表显示
			odoMainControl.setValue("LMP_DATE", parm.getTimestamp("LMP_DATE", row));
			if(odoMainControl.odo.getRegPatAdm().getItemData(0, "ADM_DATE") != null){
				odoMainControl.setValue("PRE_WEEK", OdoUtil.getPreWeekNew((Timestamp)odoMainControl.odo.getRegPatAdm().getItemData(0, "ADM_DATE"),parm.getTimestamp(
						"LMP_DATE", row)));
			}else{
				odoMainControl.setValue("PRE_WEEK", OdoUtil.getPreWeekNew(TJDODBTool.getInstance().getDBTime(),parm.getTimestamp(
						"LMP_DATE", row)));
			}
			
			/*odoMainControl.setValue("PRE_WEEK", OdoUtil.getPreWeek(TJDODBTool.getInstance().getDBTime(),parm.getTimestamp(
					"LMP_DATE", row))
					+ NULLSTR);*/
		}else{
			Timestamp lmpDate = (Timestamp) odoMainControl.odo.getRegPatAdm().getItemData(0, "LMP_DATE");
			odoMainControl.setValue("LMP_DATE", lmpDate);
			if(odoMainControl.odo.getRegPatAdm().getItemData(0, "ADM_DATE") != null){
				odoMainControl.setValue("PRE_WEEK", OdoUtil.getPreWeekNew((Timestamp)odoMainControl.odo.getRegPatAdm().getItemData(0, "ADM_DATE"),lmpDate));
			}else{
				odoMainControl.setValue("PRE_WEEK", OdoUtil.getPreWeekNew(TJDODBTool.getInstance().getDBTime(),lmpDate));
			}
			
			/*odoMainControl.setValue("PRE_WEEK", OdoUtil.getPreWeek(TJDODBTool.getInstance().getDBTime(),lmpDate)
					+ NULLSTR);*/
		}
		
		//System.out.println("tttt::::"+parm.getTimestamp("PREGNANT_DATE", row));
		odoMainControl.setValue("EDC_DATE", parm.getTimestamp("PREGNANT_DATE", row));//预产期 add by huangjw 20141016
		//添加预产期  add by huangjw 20140929 start
		/*if(parm.getTimestamp("LMP_DATE",row)!=null){
			GregorianCalendar gc= (GregorianCalendar) Calendar.getInstance();
			//Date date=parm.getTimestamp("LMP_DATE",row);
			gc.setTime(parm.getTimestamp("LMP_DATE",row));
			odoMainControl.setValue("EDC_DATE", preDate(gc));
		}else{
			odoMainControl.setValue("EDC_DATE", "");
		}*/
		//添加预产期  add by huangjw 20140929 end
		// 将数据库中的值赋值到结构化病历中
		
		odoMainControl.setValue("BREASTFEED_STARTDATE", parm.getTimestamp(
				"BREASTFEED_STARTDATE", row));
		odoMainControl.setValue("BREASTFEED_ENDDATE", parm.getTimestamp(
				"BREASTFEED_ENDDATE", row));
		String ctz1 = parm.getValue("CTZ1_CODE", row);
		String ctz2 = parm.getValue("CTZ2_CODE", row);
		String ctz3 = parm.getValue("CTZ3_CODE", row);
		odoMainControl.setValue("CTZ1_CODE", ctz1);
		odoMainControl.setValue("CTZ2_CODE", ctz2);
		odoMainControl.setValue("CTZ3_CODE", ctz3);
		ctz = new String[3];
		ctz[0] = ctz1;
		ctz[1] = ctz2;
		ctz[2] = ctz3;
		
		odoMainControl.setValue("REASSURE_FLG", parm.getValue("REASSURE_FLG", row));
	}
	
	
	/**
	 * 获取本次就诊套餐
	 * @param caseNo
	 * @return
	 */
	public String getPackage(String caseNo){
		return new TParm(TJDODBTool.getInstance().select("SELECT PAT_PACKAGE FROM REG_PATADM WHERE CASE_NO='"+caseNo+"'")).getValue("PAT_PACKAGE",0);
	}
	
	/**
	 * 计算预产期 add by huangjw 20140929
	 * @param Calendar
	 * @return
	 */
	/*public String preDate(Calendar gc){
		
		gc.add(1, 1);
		gc.add(2, -3);
		gc.add(5, 7);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString=formatter.format(gc.getTime());
		return dateString;
	}*/
	/**
    * 计算年龄
    * @param date
    * @return
    */
   public String patAge(Timestamp date){
	   Timestamp sysDate = SystemTool.getInstance().getDate();
       Timestamp temp = date == null ? sysDate : date;
       String age = "0";
       age = DateUtil.showAge(temp, sysDate);
       return age;
   }
	
	
	
	/**
	 * 身份1点击事件，将身分2和身份3清空
	 */
	public void onCtz1() throws Exception{
		TComboBox t = (TComboBox) odoMainControl.getComponent("CTZ2_CODE");
		t.setValue(NULLSTR);
		t = (TComboBox) odoMainControl.getComponent("CTZ3_CODE");
		t.setValue(NULLSTR);
	}
	
	/**
	 * 身份2点击事件，与身份1和身份3比较，不能和他们的值相同
	 */
	public void onCtz2() throws Exception{
		TComboBox t = (TComboBox) odoMainControl.getComponent("CTZ1_CODE");
		TComboBox t2 = (TComboBox) odoMainControl.getComponent("CTZ2_CODE");
		if (StringUtil.isNullString(t2.getValue()))
			return;
		if (StringUtil.isNullString(t.getValue())) {
			t2.setValue(NULLSTR);
			return;
		}
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			odoMainControl.messageBox("E0057");
			t2.setValue(NULLSTR);
			return;
		}
		t = (TComboBox) odoMainControl.getComponent("CTZ3_CODE");
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			odoMainControl.messageBox("E0058");
			t2.setValue(NULLSTR);
			return;
		}
	}
	
	/**
	 * 身份3点击事件，与身份1和身份2比较，不能和他们的值相同
	 */
	public void onCtz3() throws Exception{
		TComboBox t = (TComboBox) odoMainControl.getComponent("CTZ1_CODE");
		TComboBox t3 = (TComboBox) odoMainControl.getComponent("CTZ3_CODE");
		if (StringUtil.isNullString(t3.getValue()))
			return;
		if (StringUtil.isNullString(t.getValue())) {
			t3.setValue(NULLSTR);
			return;
		}
		if (t3.getValue().equalsIgnoreCase(t.getValue())) {
			odoMainControl.messageBox("E0057");
			t3.setValue(NULLSTR);
			return;
		}
		t = (TComboBox) odoMainControl.getComponent("CTZ2_CODE");
		if (t3.getValue().equalsIgnoreCase(t.getValue())) {
			odoMainControl.messageBox("E0059");
			t3.setValue(NULLSTR);
			return;
		}
	}
	
	/**
	 * 病生理1点击事件，清空病生理2、3
	 */
	public void onPat1() throws Exception{
		TComboBox t = (TComboBox) odoMainControl.getComponent("PAT2_CODE");
		t.setValue(NULLSTR);
		t = (TComboBox) odoMainControl.getComponent("PAT3_CODE");
		t.setValue(NULLSTR);
	}
	
	/**
	 * 病生理2点击事件，与病生理1、3比较，不能和他们的值相同
	 */
	public void onPat2() throws Exception{
		TComboBox t = (TComboBox) odoMainControl.getComponent("PAT1_CODE");
		TComboBox t2 = (TComboBox) odoMainControl.getComponent("PAT2_CODE");
		if (StringUtil.isNullString(t2.getValue()))
			return;
		if (StringUtil.isNullString(t.getValue())) {
			t2.setValue(NULLSTR);
			return;
		}
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			odoMainControl.messageBox("E0060");
			t2.setValue(NULLSTR);
			return;
		}
		t = (TComboBox) odoMainControl.getComponent("PAT3_CODE");
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			odoMainControl.messageBox("E0061");
			t2.setValue(NULLSTR);
			return;
		}
	}
	
	/**
	 * 病生理3点击事件，与病生理1、2比较，不能和他们的值相同
	 */
	public void onPat3() throws Exception{
		TComboBox t = (TComboBox) odoMainControl.getComponent("PAT1_CODE");
		TComboBox t2 = (TComboBox) odoMainControl.getComponent("PAT3_CODE");
		if (StringUtil.isNullString(t2.getValue()))
			return;
		if (StringUtil.isNullString(t.getValue())) {
			t2.setValue(NULLSTR);
			return;
		}
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			odoMainControl.messageBox("E0060");
			t2.setValue(NULLSTR);
			return;
		}
		t = (TComboBox) odoMainControl.getComponent("PAT2_CODE");
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			odoMainControl.messageBox("E0062");
			t2.setValue(NULLSTR);
			return;
		}
	}
	
	/**
	 * 调用病患详细信息界面
	 */
	public void onPatDetail() throws Exception{
		TParm parm = new TParm();
		// 判断科室权限
		if (odoMainControl.getPopedem("DEPT_POPEDEM"))
			parm.setData("OPD", "ONW");
		else
			parm.setData("OPD", "OPD");
		parm.setData("MR_NO", odoMainReg.reg.getPat().getMrNo());
		odoMainControl.openDialog(URLSYSPATINFO, parm);
	}
	
	/**
	 * 给现在的病人上锁
	 * 
	 * @param odo_type
	 *            String
	 * @return boolean
	 */
//	public boolean onLockPat(String odo_type) throws Exception{
//		if (odoMainControl.odo == null) {
//			return false;
//		}
//		// 处理上一个病人解锁
//		if (!StringUtil.isNullString(lastMrNo)) {
//			// 判断是否同一个病人，如不是，则为上一个病人解锁
//			if (lastMrNo.equalsIgnoreCase(pat.getMrNo())) {
//				// return true;
//			} else {
//				PatTool.getInstance().unLockPat(lastMrNo);
//			}
//		}
//		TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
//		// System.out.println("parm----" + parm);
//		// 如果本病人没有锁
//		if (parm == null || parm.getCount() <= 0) {
//			PatTool.getInstance().lockPat(pat.getMrNo(), odo_type,
//					Operator.getID(), Operator.getIP());
//
//			lastMrNo = pat.getMrNo();
//			return true;
//		}
//		// 本病人是自己加的锁
//		if (isMyPat()) {
//			lastMrNo = pat.getMrNo();
//			return true;
//		}
//		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
//		// 如果没有启动在线监听解锁
//		if (!RootClientListener.getInstance().isClient()) {
//			if (odoMainControl.messageBox("是否解锁\r\nUnlock this pat?", PatTool
//					.getInstance().getLockParmString(pat.getMrNo()), 0) != 0) {
//				pat = null;
//				return false;
//			}
//
//			if (!PatTool.getInstance().unLockPat(pat.getMrNo())) {
//				pat = null;
//				return false;
//			}
//			PATLockTool.getInstance().log(
//					odo_type + "->" + SystemTool.getInstance().getDate() + " "
//							+ Operator.getID() + " " + Operator.getName()
//							+ " 强制解锁[" + aa + " 病案号：" + pat.getMrNo() + "]");
//			PatTool.getInstance().lockPat(pat.getMrNo(), odo_type,
//					Operator.getID(), Operator.getIP());
//			lastMrNo = pat.getMrNo();
//			return true;
//		}
//		// 在线解锁
//		parm.setData("PRGID_U", odo_type);
//		parm.setData("MR_NO", pat.getMrNo());
//		String prgId = parm.getValue("PRG_ID", 0);
//		if ("ODO".equals(prgId))
//			parm.setData("WINDOW_ID", "OPD01");
//		else if ("ODE".equals(prgId))
//			parm.setData("WINDOW_ID", "ERD01");
//		else if ("OPB".equals(prgId))
//			parm.setData("WINDOW_ID", "OPB0101");
//		else if ("ONW".equals(prgId))//====pangben 2013-5-14 添加护士站解锁管控：门诊
//			parm.setData("WINDOW_ID", "ONW01");
//		else if ("ENW".equals(prgId))//====pangben 2013-5-14 添加护士站解锁管控:急诊
//			parm.setData("WINDOW_ID", "ONWE");
//		String flg = (String) odoMainControl.openDialog(
//				URLSYSPATLCOKMESSAGE, parm);
//		// 拒绝
//		if ("LOCKING".equals(flg)) {
//			pat = null;
//			return false;
//		}
//		// 同意
//		if ("UNLOCKING".equals(flg)) {
//			PatTool.getInstance().lockPat(pat.getMrNo(), odo_type,
//					Operator.getID(), Operator.getIP());
//			lastMrNo = pat.getMrNo();
//			return true;
//		}
//		// 强解锁
//		if ("OK".equals(flg)) {
//			PatTool.getInstance().unLockPat(pat.getMrNo());
//			PATLockTool.getInstance().log(
//					odo_type + "->" + SystemTool.getInstance().getDate() + " "
//							+ Operator.getID() + " " + Operator.getName()
//							+ " 强制解锁[" + aa + " 病案号：" + pat.getMrNo() + "]");
//			PatTool.getInstance().lockPat(pat.getMrNo(), odo_type,
//					Operator.getID(), Operator.getIP());
//			lastMrNo = pat.getMrNo();
//			return true;
//		}
//		pat = null;
//		return false;
//	}

	/**
	 * 是否正在本人手中锁住病患
	 * 
	 * @return boolean
	 */
	public boolean isMyPat() throws Exception{
		if (odoMainControl.odo == null) {
			// System.out.println("------------"+odo.getMrNo());
			return false;
		}
//		TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
		// System.out.println("----------------------" + parm);
//		String odo_type = "ODO";
//		if (ODOMainReg.E.equals(odoMainReg.admType)) {
//			odo_type = "ODE";
//		}
//		if (!odo_type.equals(parm.getValue("PRG_ID", 0))
//				|| !(Operator.getIP().equals(parm.getValue("OPT_TERM", 0)))
//				|| !(Operator.getID().equals(parm.getValue("OPT_USER", 0)))) {
//			// this.messageBox(PatTool.getInstance().getLockParmString(pat.getMrNo()));
//			return false;
//		}
		return true;
	}
	
	/**
	 * 病患解锁
	 */
	public void unLockPat() throws Exception{
		if (pat == null)
			return;
		String odo_type = "ODO";
		if (ODOMainReg.E.equals(odoMainReg.admType)) {
			odo_type = "ODE";
		}
		// 判断是否加锁
//		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
//			TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
//			if (odo_type.equals(parm.getValue("PRG_ID", 0))
//					&& (Operator.getIP().equals(parm.getValue("OPT_TERM", 0)))
//					&& (Operator.getID().equals(parm.getValue("OPT_USER", 0))))
//				PatTool.getInstance().unLockPat(pat.getMrNo());
//		}
		pat = null;
	}
	
	/**
	 * 解锁监听方法
	 * 
	 * @param prgId
	 *            String
	 * @param mrNo
	 *            String
	 * @param prgIdU
	 *            String
	 * @param userId
	 *            String
	 * @return Object
	 */
	public Object onListenPm(String prgId, String mrNo, String prgIdU,
			String userId) throws Exception{
		if (!"ODO".equalsIgnoreCase(prgId) && !"ODE".equalsIgnoreCase(prgId)) {
			return null;
		}
		TParm parm = new TParm();
		parm.setData("PRG_ID", prgId);
		parm.setData("MR_NO", mrNo);
		parm.setData("PRG_ID_U", prgIdU);
		parm.setData("USE_ID", userId);
		String flg = (String) odoMainControl.openDialog(
				URLSYSPATUNLCOKMESSAGE, parm);
		if ("OK".equals(flg)) {
			String odo_type = "ODO";
			if (ODOMainReg.E.equals(odoMainReg.admType)) {
				odo_type = "ODE";
			}
			// 判断是否加锁
			if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
				TParm parmPat = PatTool.getInstance().getLockPat(pat.getMrNo());
				if (odo_type.equals(parmPat.getValue("PRG_ID", 0))
						&& (Operator.getIP().equals(parmPat.getValue(
								"OPT_TERM", 0)))
						&& (Operator.getID().equals(parmPat.getValue(
								"OPT_USER", 0))))
					PatTool.getInstance().unLockPat(pat.getMrNo());
			}
			odoMainControl.messageBox("此患者被其他人锁定，主诉现病史可以保存，其他医嘱不可保存");
			// this.closeWindow();
			return "OK";
		}
		return NULLSTR;
	}
	
	public void onPatClear() throws Exception{
		unLockPat(); // 解锁
		odoMainControl.clearValue("BIRTH_DATE");
		pat = null;
	}

}
