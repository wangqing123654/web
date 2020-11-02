package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import jdo.ekt.EKTIO;
import jdo.mem.MEMSQL;
import jdo.sid.IdCardO;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.device.NJSMCardDriver;
import com.javahis.device.NJSMCardYYDriver;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

/**
 *
 * <p>Title: 患者注册页面</p>
 *
 * <p>Description: 患者注册页面</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author duzhw 20140114
 * @version 4.5
 */
public class MEMPatRegisterControl extends TControl {
	Pat pat;
	//String oper = "";//操作   NEW:新增 、UPDATE：修改
	//监护人单选按钮
	//	TRadioButton rubtton1;
	//	TRadioButton rubtton2;

	private TParm p3; // 医保卡参数
	private boolean txEKT = false;  // 泰心医疗卡管理执行直接写卡操作


	TParm parmPrint; //打印数据
	private boolean crmFlg = true; //crm接口开关
	//	private boolean ifTradeCard = false;//是否可以预办卡
	private boolean bookFlg = false;
	Timestamp birthDay=null;//出生日期
	TParm returnParm;
	boolean modifyFlg=false;//修改权限
	/**
	 * 初始化界面
	 */
	public void onInit() {
		super.onInit();
		//初始化个人权限
		onInitPopemed();
//		System.out.println(Operator.getID()+"LEADER--建档权限-"+this.getPopedem("LEADER"));
//		System.out.println(Operator.getID()+"MODIFY--修改权限-"+this.getPopedem("MODIFY"));
		if (this.getPopedem("LEADER")) { //建档权限
			bookFlg = true; 
		}
		if(this.getPopedem("MODIFY")){//修改权限
			modifyFlg=true;
		}
		onInitAuth();//根据权限设置界面显示
		TTextField mrNo = (TTextField)this.getComponent("MR_NO");
		mrNo.grabFocus();
		initComponent();
		initData();
//		if(!bookFlg){
//			lockName();
//		}

	}
	/**
	 * 根据权限设置界面显示
	 */
	public void onInitAuth(){
		//只有建档权限
		if( (! modifyFlg) && bookFlg  ){
			callFunction("UI|save|setEnabled", false); 		// 保存按钮置灰
		}
		
		//只有修改权限
		if( (! bookFlg) && modifyFlg  ){
			callFunction("UI|new|setEnabled", false); 		// 新增按钮置灰
		}
		
		if( (! modifyFlg) && (! bookFlg)  ){
			callFunction("UI|save|setEnabled", false);
			callFunction("UI|new|setEnabled", false);
		}
	}
	
	/**
	 * 初始化个人权限
	 */
	public void onInitPopemed(){
		TParm parm = SYSOperatorTool.getUserPopedem(Operator.getID(), getUITag());
		for (int i = 0; i < parm.getCount(); i++) {
			this.setPopedem(parm.getValue("AUTH_CODE", i), true);
		}
	}

	/**
	 * 初始化控件
	 */
	private void initComponent(){
		callFunction("UI|save|setEnabled", false); 		// 保存按钮置灰
		callFunction("UI|buycard|setEnabled", false); 	// 购卡按钮置灰
		callFunction("UI|makecard|setEnabled", false); 	// 制卡按钮置灰
		//    	rubtton1 = (TRadioButton) this.getComponent("RBUTTON1");
		//    	rubtton2 = (TRadioButton) this.getComponent("RBUTTON2");
		callFunction("UI|IDNO|setEnabled", false); //证件号置灰
		callFunction("UI|MEMprint|setEnabled", false); 	// 补印按钮置灰
		callFunction("UI|hl|setEnabled", false); 		// 历史交易按钮置灰
		callFunction("UI|TRADE_HIS|setEnabled", false); // 会员历史查询按钮置灰
		callFunction("UI|Wrist|setEnabled", false); // 打印腕带按钮置灰


	}
	/**
	 * 初始化数据
	 */
	public void initData(){
		//预办卡
		Timestamp now = StringTool.getTimestamp(new Date());
		//   	 	this.setValue("START_DATE_TRADE",
		//		 		now.toString().substring(0, 10).replace('-', '/'));// 预生效日期-默认当天
		this.setValue("IN_DATE",now.toString().substring(0, 10).replace('-', '/'));//当前时间

		//国籍
		//this.setValue("NATION_CODE", "86");//默认中国
		//民族
		//this.setValue("NATION_CODE2", "1");//默认汉族
		//办卡年限 默认1
		//   	 	this.setValue("CARD_YEAR", 1);
		crmFlg = StringTool.getBoolean(TConfig.getSystemValue("crm.switch"));
		TParm parm = new TParm();

		parm.addData("ID", "");
		parm.addData("NAME", "");
		parm.addData("ID", "01");
		parm.addData("NAME", "是");
		parm.addData("ID", "02");
		parm.addData("NAME", "否");		
		parm.addData("SYSTEM", "COLUMNS", "ID");
		parm.addData("SYSTEM", "COLUMNS", "NAME");		
		parm.setCount(3);		
		TTextFormat memCombo = (TTextFormat) getComponent("BOOK_BUILD");
		try {
			memCombo.setHorizontalAlignment(2);
			memCombo.setPopupMenuHeader("代码,75;名称,100");
			memCombo.setPopupMenuWidth(180);
			memCombo.setPopupMenuHeight(100);
			memCombo.setFormatType("combo");
			memCombo.setShowColumnList("NAME");
			memCombo.setValueColumn("ID");
			memCombo.setPopupMenuData(parm);
		} catch (Exception e) {
			// TODO: handle exception
		}


	}

	/**
	 * 查询
	 */
	public void onQuery() {
		onMrno();
	}

	/**
	 * 保存
	 * --判断修改还是新增--
	 * 老：病案号置灰：修改  未置灰：新增
	 * 新：病案号置灰有数据：修改  置灰无数据：新增
	 */
	public void onSave() {
		TParm parm = new TParm();
		TParm result = new TParm();
		String oper = "";
		TTextField mrNo = (TTextField)this.getComponent("MR_NO");
		boolean flag = mrNo.isEnabled();
		String mrNo2 = this.getValue("MR_NO").toString();
		if(!flag){
			if(mrNo2.length()==0){//没数据-新增操作
				oper = "ADD";
			}else if(mrNo2.length()>0){//有数据-修改操作
				oper = "UPDATE";
			}
		}
		//System.out.println("-------oper"+oper);
		//获取页面监护人数据
		//    	String deGuardian = "";
		//    	if (rubtton1.isSelected()){
		//    		deGuardian = "1";
		//    	}else if(rubtton2.isSelected()){
		//    		deGuardian = "2";
		//    	}
		//this.messageBox(oper);
		//校验数据
		if(!checkData(oper)){
			return;
		}

		//获取页面数据
		parm = getData();
		parmPrint = getData();//打印parm
		if(birthDay!=null && birthDay.toString().length()>0){//处理出生日期
			parm.setData("BIRTH_DATE", birthDay.toString().substring(0,19).replace("-", "/"));
		}
		//数据处理
		//1:如果是新增数据-新增SYS_PATINFO表、插入MEM_PATINFO表
		if("ADD".equals(oper)){
			//    		pat = new Pat();
			//            pat = this.readModifyPat(pat);
			//            if (!pat.onNew()) {
			//                this.messageBox("E0005"); // 失败
			//                return ;
			//            } else {
			//            	
			//                setValue("MR_NO", pat.getMrNo());
			//                callFunction("UI|MR_NO|setEnabled", false); // 病案号
			//                return;
			//            }
			String newMrNo = SystemTool.getInstance().getMrNo();
			if (newMrNo == null || newMrNo.length() == 0) {
				err("-1 取病案号错误!");
				return;
			}
			parm.setData("MR_NO", newMrNo);
			result = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
					"newSysPatinfo", parm);
			//判断是否办理预办卡业务 是：插入交易表
			boolean ifTrade = false;
			if(result.getErrCode()>=0){//写交易表
				if(parm.getValue("MEM_CODE").toString().length()>0 ){//卡类型不为空&&可以预办卡
					String tradeNo = getMEMTradeNo();
					parm.setData("TRADE_NO", tradeNo);
					//获取会员卡编号
					String memCardNo = getMemCardNo(newMrNo);
					parm.setData("MEM_CARD_NO", memCardNo);
					result = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
							"insertMemTradeData", parm);
					ifTrade = true;
				}

			}

			if(result.getErrCode()<0){
				this.messageBox("保存失败！");
				return;
			}else{
				setValue("MR_NO", newMrNo);
				callFunction("UI|MR_NO|setEnabled", false);  // 病案号
				callFunction("UI|buycard|setEnabled", true); // 购卡按钮置有效
				callFunction("UI|makecard|setEnabled", true); // 购卡按钮置有效

				//this.messageBox("病患注册成功！");

				//add by huangtt 20140401 CRM----start
				if(crmFlg){
					TParm parmCRM = TIOM_AppServer.executeAction("action.reg.REGCRMAction","createMember1",parm);
					if(!parmCRM.getBoolean("flg", 0)){
						this.messageBox("CRM信息新增同步失败！");

					}
				}

				if(ifTrade){//如果办理预办卡则打印凭条
					onPrint(newMrNo);//打印会员注册凭据
				}
				//add by huangtt 20140401 CRM----end
				if (JOptionPane.showConfirmDialog(null, "病患注册成功！是否继续医疗卡制卡？", "信息",
						JOptionPane.YES_NO_OPTION) == 0) {
					onEKTBuy();
				}
			}

		}else if("UPDATE".equals(oper)){//2:如果是修改数据-更新MEM_PATINFO表同时更新SYS_PATINFO
			TParm regParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT * FROM SYS_PATINFO WHERE MR_NO = '" + this.getValueString("MR_NO")
					+ "'"));
			//this.messageBox(""+modifyFlg);
			//parm.setData("MODIFY", modifyFlg);
			//判断会员表是是否有数据-有：修改 无：新增
			TParm mrNoParm = new TParm();
			mrNoParm.addData("MR_NO", this.getValueString("MR_NO"));
			//System.out.println("保存修改-MR_NO="+this.getValueString("MR_NO"));
			TParm memParm = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
					"onQueryMem", mrNoParm);
			//System.out.println("memParm-count="+memParm.getCount());
			if(memParm.getCount()>0){
				//修改会员信息
				//System.out.println("页面parm="+parm);
				result = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
						"updateMemData", parm);
				//System.out.println("result.getErrCode()="+result.getErrCode());
				//写入交易表
				if(parm.getValue("MEM_CODE").toString().length()>0 ){//卡类型不为空&&可以预办卡
					//判断交易表是否存在
					//判断交易表是否存在数据-存在：修改、不存在：新增
					if(exitsMemTrade(this.getValueString("MR_NO"))){//存在
						result = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
								"updateMemTrade", parm);
					}else{//新增交易表
						//获取当前交易号
						String tradeNo = getMEMTradeNo();
						//            			int tradeNo = getMaxSeq("TRADE_NO","MEM_TRADE","","","","");
						//            			System.out.println("当前交易号1："+tradeNo);
						parm.setData("TRADE_NO", tradeNo);
						//获取会员卡编号
						String memCardNo = getMemCardNo(this.getValueString("MR_NO"));
						//System.out.println("会员卡编号1：：："+memCardNo);
						parm.setData("MEM_CARD_NO", memCardNo);
						result = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
								"insertMemTradeData", parm);
					}

				}
				if(result.getErrCode()<0){
					this.messageBox("保存失败！");

				}
			}else{
				//会员信息校验
				//    			if(!checkMemData()){
				//    	    		return;
				//    	    	}
				//新增会员信息
				result = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
						"insertMemData", parm);

				//写入交易表
				if(parm.getValue("MEM_CODE").toString().length()>0){
					//判断交易表是否存在数据-存在：修改、不存在：新增
					if(exitsMemTrade(this.getValueString("MR_NO"))){//存在
						result = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
								"updateMemTrade", parm);
					}else{//新增交易表
						//获取当前交易号
						String tradeNo = getMEMTradeNo();
						//            			int tradeNo = getMaxSeq("TRADE_NO","MEM_TRADE","","","","");
						//            			System.out.println("当前交易号2："+tradeNo);
						parm.setData("TRADE_NO", tradeNo);
						//获取会员卡编号
						String memCardNo = getMemCardNo(this.getValueString("MR_NO"));
						//System.out.println("会员卡编号2：：："+memCardNo);
						parm.setData("MEM_CARD_NO", memCardNo);
						//新增交易信息
						result = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
								"insertMemTradeData", parm);
					}

				}


				if(result.getErrCode()<0){
					this.messageBox("保存失败！");
				}
			}
			//保存成功
			if(result.getErrCode()>=0){
				this.messageBox("保存成功！");
				writeLog(mrNoParm.getValue("MR_NO",0), regParm.getRow(0), "UPDATE");
				//add by huangtt 20140401 CRM----start
				if(crmFlg){
					System.out.println("CRM信息更新同步==="+parm);
					TParm memTrade =new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemTradeCrm(getValueString("MR_NO")))); 
					parm.setData("MEM_CODE", memTrade.getValue("MEM_CODE", 0));
					parm.setData("REASON", memTrade.getValue("REASON", 0));
					parm.setData("START_DATE_TRADE", "".equals(memTrade.getValue("START_DATE", 0))? "": memTrade.getValue("START_DATE", 0).substring(0, 10));
					parm.setData("END_DATE_TRADE", "".equals(memTrade.getValue("END_DATE", 0))? "": memTrade.getValue("END_DATE", 0).substring(0, 10));
					parm.setData("MEM_FEE", memTrade.getValue("MEM_FEE", 0));
					parm.setData("INTRODUCER1", memTrade.getValue("INTRODUCER1", 0));
					parm.setData("INTRODUCER2", memTrade.getValue("INTRODUCER2", 0));
					parm.setData("INTRODUCER3", memTrade.getValue("INTRODUCER3", 0));
					parm.setData("DESCRIPTION", memTrade.getValue("DESCRIPTION", 0));
					TParm parmCRM = TIOM_AppServer.executeAction("action.reg.REGCRMAction","updateMemberByMrNo1",parm);
					if(!parmCRM.getBoolean("flg", 0)){
						this.messageBox("CRM信息更新同步失败！");
					}
				}

				//add by huangtt 20140401 CRM----end
				String sql = "select CARD_NO from EKT_ISSUELOG where mr_no = '"
						+ parm.getValue("MR_NO") + "'";
				TParm ekt = new TParm(TJDODBTool.getInstance().select(sql));
				if(ekt.getCount()< 0){
					if (messageBox("提示", "该病患未办理医疗卡,是否办理医疗卡", 0) == 0) {
						onEKTBuy(); // 制卡
						// ====zhangp 20120227 modify start
					}
				}
			}

		}
		if(returnParm!=null){//更新病患的孕周 出生体重，出生身长
			String sql="UPDATE SYS_PATINFO SET GESTATIONAL_WEEKS='"+returnParm.getValue("GESTATIONAL_WEEKS")+"', " +
					" NEW_BODY_WEIGHT='"+returnParm.getValue("NEW_BODY_WEIGHT")+"'," +
					" NEW_BODY_HEIGHT='"+returnParm.getValue("NEW_BODY_HEIGHT")+"'" +
					" WHERE MR_NO='"+this.getValueString("MR_NO")+"'";
			System.out.println("parm:::"+sql);
			new TParm(TJDODBTool.getInstance().update(sql));
		}
	}

	/**
	 * 新增方法
	 */
	public void onNew() {
		onClear();
		callFunction("UI|MR_NO|setEnabled", false); 	// 病案号置灰
		callFunction("UI|new|setEnabled", false); 		// 新增按钮置灰
		callFunction("UI|save|setEnabled", true); 		// 保存按钮置可用
		callFunction("UI|buycard|setEnabled", false);   // 购卡按钮置灰
		callFunction("UI|makecard|setEnabled", false);   // 购卡按钮置灰
		this.setValue("BOOK_BUILD", "01");  //add by huangtt 20140928
	}

	/**
	 * 购卡方法
	 */
	public void onBuy() {
		TParm parm = new TParm();
		//获取页面数据
		parm = getData();
		TParm reParm = (TParm)this.openDialog(
				"%ROOT%\\config\\mem\\MEMMarketCard.x", parm);
		String mr_no = this.getValueString("MR_NO");
		this.onClear();
		this.setValue("MR_NO", mr_no);
		this.onMrno();
	}

	/**
	 * 查询会费MEM_FEE

    public void queryFee(){
    	//String menFee = "";
    	String MemCode = this.getValueString("MEM_CODE");
    	//System.out.println("MemCode="+MemCode);
    	if(MemCode.length()>0){
    		String sql = "SELECT MEM_FEE FROM MEM_MEMBERSHIP_INFO WHERE MEM_CODE = '"+MemCode+"'";
    		//System.out.println("sql="+sql);
    		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    		//System.out.println("--"+parm.getValue("MEM_FEE", 0));
    		if(parm.getCount("MEM_FEE")>0){
    			this.setValue("MEM_FEE", parm.getValue("MEM_FEE", 0));
    		}
    	}
    } */

	/**
	 * 清除
	 */
	public void onClear() {
		callFunction("UI|MR_NO|setEnabled", true); 	  // 病案号可用
		callFunction("UI|new|setEnabled", true); 	  // 新增按钮置可用
		callFunction("UI|save|setEnabled", false); 	  // 保存按钮置灰
		callFunction("UI|buycard|setEnabled", false); // 购卡按钮置灰
		callFunction("UI|makecard|setEnabled", false); // 购卡按钮置灰

		callFunction("UI|IDNO|setEnabled", false); //证件号置灰
		callFunction("UI|MEMprint|setEnabled", false); 	// 补印按钮置灰
		callFunction("UI|hl|setEnabled", false); 		// 历史交易按钮置灰
		callFunction("UI|TRADE_HIS|setEnabled", false); // 会员历史查询按钮置灰
		callFunction("UI|Wrist|setEnabled", false); // 打印腕带按钮置灰

		this.clearValue("MR_NO;PAT_NAME;PAT_NAME1;LAST_NAME;FIRST_NAME;PY1;ID_TYPE;IDNO;OLD_NAME;SEX_CODE;BIRTH_DATE;AGE;" +
				"NATION_CODE;NATION_CODE2;RELIGION;MARRIAGE;ADDRESS;CURRENT_ADDRESS;POST_CODE;" +
				"RESID_ADDRESS;RESID_POST_CODE;HOMEPLACE_CODE;BIRTH_HOSPITAL;CELL_PHONE;SCHOOL_NAME;SCHOOL_TEL;" +
				"TEL_HOME;E_MAIL;SOURCE;INSURANCE_COMPANY1_CODE;INSURANCE_NUMBER1;INSURANCE_COMPANY2_CODE;INSURANCE_NUMBER2;" +
				"GUARDIAN1_NAME;GUARDIAN1_RELATION;GUARDIAN1_TEL;GUARDIAN1_PHONE;GUARDIAN1_COM;GUARDIAN1_ID_TYPE;GUARDIAN1_ID_CODE;GUARDIAN1_EMAIL;" +
				"GUARDIAN2_NAME;GUARDIAN2_RELATION;GUARDIAN2_TEL;GUARDIAN2_PHONE;GUARDIAN2_COM;GUARDIAN2_ID_TYPE;GUARDIAN2_ID_CODE;GUARDIAN2_EMAIL;" +
				"CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;SPECIAL_DIET;MEM_TYPE;FAMILY_DOCTOR;ACCOUNT_MANAGER_CODE;START_DATE;END_DATE;BUY_MONTH_AGE;HAPPEN_MONTH_AGE;" +
				"MEM_CODE;END_DATE_TRADE;MEM_FEE;INTRODUCER1;INTRODUCER2;INTRODUCER3;DESCRIPTION;START_DATE_TRADE;COMPANY_DESC;BOOK_BUILD;REMARKS;CUSTOMER_SOURCE;INCOME;GUARDIAN1_AGE;GUARDIAN2_AGE");
		//国籍
		//this.setValue("NATION_CODE", "86");//默认中国
		//民族
		//this.setValue("NATION_CODE2", "1");//默认汉族
		//预办卡
		//    	Timestamp now = StringTool.getTimestamp(new Date());
		//   	 	this.setValue("START_DATE_TRADE",
		//		 		now.toString().substring(0, 10).replace('-', '/'));// 预生效日期-默认当天
		birthDay=null;//add by huangjw 20150423
//		if(!bookFlg){
//			lockName();
//		}
		this.onInitAuth();
	}

	/**
	 * 校验页面数据
	 */
	public boolean checkData(String oper) {
		boolean flg = true;
		//校验是否输入空值,并弹出提示信息
		if("UPDATE".equals(oper)){
			if (getValue("MR_NO") == null || getValue("MR_NO").toString().length()<=0) {
				this.messageBox("病案号不能为空!");
				this.grabFocus("MR_NO");
				flg = false;
				return flg;
			}
		}

		//add by huangtt 20140320
		if(getValueString("PAT_NAME").equals("")){
			if(getValueString("FIRST_NAME").equals("") && !getValueString("LAST_NAME").equals("")){
				this.messageBox("请输入firstName!");
				this.grabFocus("FIRST_NAME");
				flg = false;
				return flg;
			}else if(!getValueString("FIRST_NAME").equals("") && getValueString("LAST_NAME").equals("")){
				this.messageBox("请输入lastName!");
				this.grabFocus("LAST_NAME");
				flg = false;
				return flg;
			}else if(!getValueString("FIRST_NAME").equals("") && !getValueString("LAST_NAME").equals("")){
				this.setValue("PAT_NAME1", getValueString("FIRST_NAME")+" "+getValueString("LAST_NAME"));
				if(this.messageBox("姓名赋值", "是否将firstName和lastName合并赋值给姓名？", 0) != 0) {
					this.messageBox("姓名不能为空!");
					flg = false;
					return flg;
				} 
				this.setValue("PAT_NAME", getValueString("FIRST_NAME")+" "+getValueString("LAST_NAME"));

			}else if(getValueString("FIRST_NAME").equals("") && getValueString("LAST_NAME").equals("")){
				//if(this.modifyFlg || oper.equals("ADD")){//有“MODIFY”权限则校验，否则不校验
				this.messageBox("姓名不能为空!");
				this.grabFocus("PAT_NAME");
				flg = false;
				return flg;
				//}
		}
			
			
			
			
		}else{
			if(getValueString("FIRST_NAME").equals("") && !getValueString("LAST_NAME").equals("")){
				this.messageBox("请输入firstName!");
				this.grabFocus("FIRST_NAME");
				flg = false;
				return flg;
			}else if(!getValueString("FIRST_NAME").equals("") && getValueString("LAST_NAME").equals("")){
				this.messageBox("请输入lastName!");
				this.grabFocus("LAST_NAME");
				flg = false;
				return flg;
			}else if(!getValueString("FIRST_NAME").equals("") && !getValueString("LAST_NAME").equals("")){
				this.setValue("PAT_NAME1", getValueString("FIRST_NAME")+" "+getValueString("LAST_NAME"));	
			}
		}

		if (getValue("ID_TYPE") == null || getValue("ID_TYPE").toString().length()<=0) {
			this.messageBox("证件类型不能为空!");
			this.grabFocus("ID_TYPE");
			flg = false;
			return flg;
		}
		if(!getValue("ID_TYPE").toString().equals("99")){
			if (getValue("IDNO") == null || getValue("IDNO").toString().length()<=0) {
				this.messageBox("证件号不能为空!");
				this.grabFocus("IDNO");
				flg = false;
				return flg;
			}
		}
		
		
		
		//-------------modefied by wangqing at 2016.11.16 start-----------------
		//修改方法：注销代码  工作单位改为非必选项
		
		//        //工作单位是必填项，如果没有由患者填"无"   sunqy  20140509
		//    	if(getValue("COMPANY_DESC") == null || getValue("COMPANY_DESC").toString().length()<=0){
		//    		this.messageBox("工作单位是必填项,如果没有填'无'");
		//    		flg = false;
		//    		return flg;
		//    	}
		//-------------modefied by wangqing at 2016.11.16 end-----------------
		
		//身份一校验      add by sunqy 20140512
		if(getValue("CTZ1_CODE") == null || getValue("CTZ1_CODE").toString().length()<=0){
			this.messageBox("身份一不能为空!");
			flg = false;
			return flg;
		}
		//证件类型是身份证时，证件号码要按照身份证号校验格式
		if(getValue("ID_TYPE").toString().equals("01")){
			if(!isCard(getValue("IDNO").toString())){
				this.messageBox("录入的身份证号不符合要求！");
				this.grabFocus("IDNO");
				flg = false;
				return flg;
			}
		}
		//if(this.modifyFlg || oper.equals("ADD")){//有“MODIFY”权限则校验，否则不校验
		if (getValue("SEX_CODE") == null || getValue("SEX_CODE").toString().length()<=0) {
			this.messageBox("性别不能为空!");
			this.grabFocus("SEX_CODE");
			flg = false;
			return flg;
		}
		//}
		//if(this.modifyFlg || oper.equals("ADD")){//有“MODIFY”权限则校验，否则不校验
		if (getValue("BIRTH_DATE") == null || getValue("BIRTH_DATE").toString().length()<=0) {
			this.messageBox("出生日期不能为空!");
			this.grabFocus("BIRTH_DATE");
			flg = false;
			return flg;
		}else{
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
			String sTime = getValueString("BIRTH_DATE").substring(0,10);
			  try {
				Date bt=sdf.parse(sTime);
				Date now = new Date();
		        if(bt.after(now)){
		        	this.messageBox("出生日期不能超过当前日期!");
		        	this.grabFocus("BIRTH_DATE");
					flg = false;
					return flg;
		        }
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		//}
		if (getValue("NATION_CODE") == null || getValue("NATION_CODE").toString().length()<=0) {
			this.messageBox("国籍不能为空!");
			this.grabFocus("NATION_CODE");
			flg = false;
			return flg;
		}
		if (getValue("NATION_CODE2") == null || getValue("NATION_CODE2").toString().length()<=0) {
			this.messageBox("民族不能为空!");
			this.grabFocus("NATION_CODE2");
			flg = false;
			return flg;
		}
		if (getValue("MARRIAGE") == null || getValue("MARRIAGE").toString().length()<=0) {
			this.messageBox("婚姻不能为空!");
			this.grabFocus("MARRIAGE");
			flg = false;
			return flg;
		}
		//        //户籍地址下拉框
		//        if(getValue("RESID_POST_CODE") == null || getValue("RESID_POST_CODE").toString().length()<=0){
		//        	this.messageBox("户籍地址不能为空!");
		//        	this.grabFocus("RESID_POST_CODE");
		//        	flg = false;
		//            return flg;
		//        }
		//        //户籍地址-详细地址
		//        if(getValue("RESID_ADDRESS") == null || getValue("RESID_ADDRESS").toString().length()<=0){
		//        	this.messageBox("户籍地址-详细地址不能为空!");
		//        	this.grabFocus("RESID_ADDRESS");
		//        	flg = false;
		//            return flg;
		//        }
		//现住址下拉框
		if(getValue("POST_CODE") == null || getValue("POST_CODE").toString().length()<=0){
			this.messageBox("现住址不能为空!");
			this.grabFocus("POST_CODE");
			flg = false;
			return flg;
		}
		//现住址-详细地址
		if (getValue("CURRENT_ADDRESS") == null || getValue("CURRENT_ADDRESS").toString().length()<=0) {
			this.messageBox("现住址-详细地址不能为空!");
			this.grabFocus("CURRENT_ADDRESS");
			flg = false;
			return flg;
		}
		//        //保险卡号1不能超过50
		//        if(this.getValueString("INSURANCE_NUMBER1").length()>0){
		//        	if(this.getValueString("INSURANCE_NUMBER1").length()>50){
		//        		this.messageBox("保险卡号1不能超过50位！");
		//        		this.grabFocus("INSURANCE_NUMBER1");
		//                flg = false;
		//                return flg;
		//        	}
		//        }
		//        //保险卡号2不能超过50
		//        if(this.getValueString("INSURANCE_NUMBER2").length()>0){
		//        	if(this.getValueString("INSURANCE_NUMBER2").length()>50){
		//        		this.messageBox("保险卡号2不能超过50位！");
		//        		this.grabFocus("INSURANCE_NUMBER2");
		//                flg = false;
		//                return flg;
		//        	}
		//        }
		//手机
		if (getValue("CELL_PHONE") == null || getValue("CELL_PHONE").toString().length()<=0) {
			this.messageBox("手机不能为空!");
			this.grabFocus("CELL_PHONE");
			flg = false;
			return flg;
		}
		if(this.getValueString("CELL_PHONE").length()>0){

			if(!isNumeric(this.getValueString("CELL_PHONE"))){
				this.messageBox("请填写正确的手机号");
				this.grabFocus("CELL_PHONE");
				flg = false;
				return flg;
			}
			if(this.getValueString("CELL_PHONE").length() != 11){
				this.messageBox("请填写正确的手机号");
				this.grabFocus("CELL_PHONE");
				flg = false;
				return flg;
			}
		}
		//座机
		//        if (getValue("TEL_HOME") == null || getValue("TEL_HOME").toString().length()<=0) {
		//            this.messageBox("座机不能为空!");
		//            this.grabFocus("TEL_HOME");
		//            flg = false;
		//            return flg;
		//        }
		//监护人校验
		if (getValue("GUARDIAN1_NAME") == null || getValue("GUARDIAN1_NAME").toString().length()<=0) {
			this.messageBox("监护人1姓名不能为空!");
			this.grabFocus("GUARDIAN1_NAME");
			flg = false;
			return flg;
		}
		if (getValue("GUARDIAN1_RELATION") == null || getValue("GUARDIAN1_RELATION").toString().length()<=0) {
			this.messageBox("监护人1关系不能为空!");
			this.grabFocus("GUARDIAN1_RELATION");
			flg = false;
			return flg;
		}
		if (getValue("GUARDIAN1_PHONE") == null || getValue("GUARDIAN1_PHONE").toString().length()<=0) {
			this.messageBox("监护人1手机不能为空!");
			this.grabFocus("GUARDIAN1_PHONE");
			flg = false;
			return flg;
		}
		if (this.getValueString("GUARDIAN1_PHONE").length() > 0) {

			if (!isNumeric(this.getValueString("GUARDIAN1_PHONE"))) {
				this.messageBox("请填写正确的手机号");
				this.grabFocus("GUARDIAN1_PHONE");
				flg = false;
				return flg;
			}
			if (this.getValueString("GUARDIAN1_PHONE").length() != 11) {
				this.messageBox("请填写正确的手机号");
				this.grabFocus("GUARDIAN1_PHONE");
				flg = false;
				return flg;
			}
		}

		if (getValue("SOURCE") == null || getValue("SOURCE").toString().length()<=0) {
			this.messageBox("获知方式不能为空!");
			this.grabFocus("SOURCE");
			flg = false;
			return flg;
		}


		if (getValue("BOOK_BUILD") == null || getValue("BOOK_BUILD").toString().length()<=0) {
			this.messageBox("建档录入不能为空!");
			this.grabFocus("BOOK_BUILD");
			flg = false;
			return flg;
		}

		//add by huangtt 20140425
		TTextFormat t = this.getTextFormat("REASON");
		String reason =t.getText();
		if(reason.equals("续卡")){
			if(!this.getValueString("MEM_TYPE").equals(this.getValueString("MEM_TYPE1"))){
				this.messageBox("会员身份与发卡类型不一致，不可以续卡！");
				flg = false;
				return flg;
			}
		}

		//预办卡会费校验
		//        if(this.getValue("MEM_CODE").toString().length() > 0){
		//        	flg = onQueryFee();
		//        }

		return flg;
	}
	//    /**
	//     * 会员信息校验
	//     */
	//    public boolean checkMemData() {
	//    	boolean flg = true;
	//    	//校验是否输入空值,并弹出提示信息
	//    	if (getValue("MEM_TYPE") == null || getValue("MEM_TYPE").toString().length()<=0) {
	//            this.messageBox("会员身份不能为空!");
	//            this.grabFocus("MEM_TYPE");
	//            flg = false;
	//            return flg;
	//        }
	//    	if (getValue("MEM_CODE") == null || getValue("MEM_CODE").toString().length()<=0) {
	//            this.messageBox("发卡类型不能为空!");
	//            this.grabFocus("MEM_CODE");
	//            flg = false;
	//            return flg;
	//        }
	//    	
	//    	return flg;
	//    }

	/**
	 * 病案号回车查询事件
	 */
	public void onMrno() {

		boolean flg = false;
		//SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		//String date = df.format(SystemTool.getInstance().getDate());
		//setValue("IN_DATE", StringTool.getTimestamp(date, "yyyyMMddHH")); // 操作日期
		pat = new Pat();
		String mrno = getValue("MR_NO").toString().trim();
		if (!this.queryPat(mrno))
			return;
		pat = Pat.onQueryByMrNo(mrno);
		//System.out.println("pat="+pat.getParm());
		if (pat == null || "".equals(getValueString("MR_NO"))) {
			this.messageBox_("查无病患! ");
			this.onClear(); // 清空
			return;
		} else {
			callFunction("UI|save|setEnabled", true); 		// 保存按钮置可用
			callFunction("UI|buycard|setEnabled", true);	// 购卡按钮置可用
			callFunction("UI|makecard|setEnabled", true);	// 购卡按钮置可用
			callFunction("UI|MR_NO|setEnabled", false); 	// 病案号
			callFunction("UI|IDNO|setEnabled", true); 		//证件号置有效
			callFunction("UI|MEMprint|setEnabled", true); 	// 补印按钮置有效
			callFunction("UI|hl|setEnabled", true); 		// 历史交易按钮置有效
			callFunction("UI|TRADE_HIS|setEnabled", true);  // 会员历史查询按钮置有效
			callFunction("UI|Wrist|setEnabled", true); 		// 打印腕带按钮置有效
			flg = true;
			//MR_NO = pat.getMrNo();
//			if(!bookFlg){
//				lockName();
//			}
			this.onInitAuth();
		}
		this.setPatForUI(pat);

		if(flg){
			//查询MEM_PATINFO表数据
			TParm mrNoParm = new TParm();
			mrNoParm.addData("MR_NO", this.getValueString("MR_NO"));
			TParm memParm = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
					"onQueryMem", mrNoParm);
			//        	System.out.println("会员信息："+memParm);
			//        	System.out.println("数量："+memParm.getCount());
			if(memParm.getCount()>0){
				//把数据set到页面
				this.setMemPatinfoForUI(memParm);
				//查询会员交易表主档-预办卡信息
				TParm memTradeParm = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
						"onQueryTrade", mrNoParm);
				if(memTradeParm.getCount()>0){
					//把数据set到页面
					this.setMemTradeForUI(memTradeParm);
				}
			}
		}

		if(!modifyFlg){//没有修改权限时，保存按钮要置灰 add by huangjw 20150727
			TMenuItem save=(TMenuItem) this.getComponent("save");
			save.setEnabled(false);
		}
	}
	/**
	 * 获取出生日期（精确到时分秒）
	 * @param mrNO
	 */
	public Timestamp onQueryBirthDateByMrNO(String mrNO){
		String sql="SELECT BIRTH_DATE FROM SYS_PATINFO WHERE MR_NO='"+mrNO+"'";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		Timestamp birth=parm.getTimestamp("BIRTH_DATE",0);
		return birth;
	}
	/**
	 * 查询病患信息
	 * 
	 * @param mrNo
	 *            String
	 */
	public void onQueryNO(String mrNo) {
		if (pat != null)
			PatTool.getInstance().unLockPat(pat.getMrNo());

		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.messageBox("无此病案号!");
			return;
		}
		setValue("MR_NO", mrNo);
		setValue("PAT_NAME", pat.getName());
		setValue("PAT_NAME1", pat.getName1());
		setValue("PY1", pat.getPy1());
		setValue("IDNO", pat.getIdNo());
		//setValue("FOREIGNER_FLG", pat.isForeignerFlg());
		setValue("BIRTH_DATE", pat.getBirthday());
		setValue("SEX_CODE", pat.getSexCode());
		setValue("TEL_HOME", pat.getTelHome());
		setValue("POST_CODE", pat.getPostCode());
		//onPost();
		setValue("ADDRESS", pat.getAddress());
		setValue("CTZ1_CODE", pat.getCtz1Code());
		//setValue("REG_CTZ1", getValue("CTZ1_CODE"));
		setValue("CTZ2_CODE", pat.getCtz2Code());
		//setValue("REG_CTZ2", getValue("CTZ2_CODE"));
		setValue("CTZ3_CODE", pat.getCtz3Code());
		// setValue("REG_CTZ3", getValue("CTZ3_CODE"));
		//String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
		onMrno();

	}
	/**
	 * 身份证号得到出生地-身份证号回车事件--暂时手动录入
	 */
	public void onIdNo() {
		String homeCode = "";
		String idNo = this.getValueString("IDNO");
		homeCode = StringUtil.getIdNoToHomeCode(idNo);//出生地
		setValue("HOMEPLACE_CODE", homeCode);
	}

	/**
	 * 根据类型号查重
	 * 身份证号得到出生地-身份证号回车事件--暂时手动录入
	 */
	public void onBirthday() {
		try{
			String idNo = this.getValueString("IDNO");
			if(getValue("ID_TYPE").toString().equals("01")){//证件类型为身份证
				if(idNo.length()>0){
					if(!isCard(idNo)){
						this.messageBox("录入的身份证号不符合要求！");
						return;
					}else{
						if(idNo.length()>14){
							String time = idNo.substring(6, 14);
							String time2 = time.substring(0, 4)+"/"+time.substring(4, 6)+"/"+time.substring(6, 8);
							//System.out.println("time2="+time2);
							//显示出生日期
							//if(getValue("BIRTH_DATE")==null){//如果出生日期有值则不覆盖
							this.setValue("BIRTH_DATE", time2);
							//设置年龄
							setBirth(true);
							//}
							String sexCode = idNo.substring(idNo.length()-2);
							sexCode = sexCode.substring(0, 1);
							//System.out.println("性别："+sexCode);
							//设置性别
							setSexCode(sexCode);


						}

					}
				}


			}
			//添加检测是否有相同数据
			String selPat = "SELECT   A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
					+ " POST_CODE, RESID_ADDRESS,CURRENT_ADDRESS, A.MR_NO,ID_TYPE "
					+ " FROM SYS_PATINFO A "
					+ " WHERE A.IDNO = '"
					+ idNo
					+ "'  "
					+ " ORDER BY A.OPT_DATE";
			// ===zhangp 20120319 end
			TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
			if (same.getErrCode() != 0) {
				this.messageBox_(same.getErrText());
			}
			// 选择病患信息
			if (same.getCount("MR_NO") > 0) {
				//			int sameCount = this.messageBox("提示信息", "已有相同证件号码的病患信息,是否继续保存此人信息", 0);
				//add by huangtt 20140926 start
				Object[] possibilities = { "  是   ", "  否   " };
				int sameCount = JOptionPane.showOptionDialog(null,
						"已有相同证件号码的病患信息,是否继续保存此人信息", "提示信息",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
						null, possibilities, possibilities[1]);
				//add by huangtt 20140926 end
				if (sameCount != 1) {
					this.grabFocus("SEX_CODE");
					return;
				}
				Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
					onQueryNO(patParm.getValue("MR_NO"));
					//onMrno();
					this.grabFocus("SEX_CODE");
					return;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			this.grabFocus("SEX_CODE");
		}


	}

	/**
	 * 证件类型改变事件
	 */
	public void onClickIdType() {
		//    	String idType = getValue("ID_TYPE").toString();
		//    	if("99".equals(idType)){
		//    		callFunction("UI|IDNO|setEnabled", false); //证件号置灰
		//    		//清除证件号数据
		//    		this.setValue("IDNO", "");
		//    	}else{
		//    		callFunction("UI|IDNO|setEnabled", true); //证件号置有效
		//    	}
		callFunction("UI|IDNO|setEnabled", true); //证件号置有效
	}

	/**
	 * 查询病患信息
	 * @param mrNo String
	 * @return boolean
	 */
	public boolean queryPat(String mrNo) {
		//this.setMenu(false); //MENU 显示控制
		pat = new Pat();
		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			//this.setMenu(false); //MENU 显示控制
			this.messageBox("E0081");
			return false;
		}
		String allMrNo = PatTool.getInstance().checkMrno(mrNo);
		if (mrNo != null && !allMrNo.equals(pat.getMrNo())) {
			//============xueyf modify 20120307 start
			messageBox("病案号" + allMrNo + " 已合并至" + pat.getMrNo());
			//============xueyf modify 20120307 stop
		}

		return true;
	}
	/**
	 * 病患信息赋值
	 *
	 * @param patInfo
	 *            Pat
	 */
	public void setPatForUI(Pat patInfo) {
		// 病案号,姓名,性别,生日,身份证号,电话
		this.setValueForParm(
				"MR_NO;PAT_NAME;PAT_NAME1;FIRST_NAME;LAST_NAME;PY1;IDNO;ID_TYPE;SEX_CODE;BIRTH_DATE;POST_CODE;ADDRESS;RESID_ADDRESS;CURRENT_ADDRESS;" +
						"NATION_CODE;RESID_POST_CODE;HOMEPLACE_CODE;CELL_PHONE;TEL_HOME;E_MAIL;SPECIAL_DIET;OLD_NAME;COMPANY_DESC;REMARKS;CUSTOMER_SOURCE",patInfo.getParm());
		this.setValue("NATION_CODE2", patInfo.getSpeciesCode());//民族
		this.setValue("RELIGION", patInfo.getReligionCode());//宗教
		this.setValue("MARRIAGE", patInfo.getMarriageCode());//婚姻
		this.setValue("OLD_NAME", patInfo.getOldName());//曾用名
		this.setValue("CTZ2_CODE", patInfo.getCtz2Code());
		this.setValue("CTZ1_CODE", patInfo.getCtz1Code());
		this.setValue("CTZ3_CODE", patInfo.getCtz3Code());
		if(patInfo.isBookBuild()){
			this.setValue("BOOK_BUILD", "01");
		}else{
			this.setValue("BOOK_BUILD", "02");
		}
		//this.setValue("CTZ3_CODE", patInfo.getCtz3Code());


		//this.setValue("IPD_NO", pat.getIpdNo());
		birthDay=onQueryBirthDateByMrNO(getValue("MR_NO").toString().trim());
		setBirth(false); // 计算年龄
	}
	/**
	 * 病患会员信息赋值
	 */
	public void setMemPatinfoForUI(TParm parm) {
		this.setValue("CUSTOMER_SOURCE", parm.getValue("CUSTOMER_SOURCE", 0));
		this.setValue("BIRTH_HOSPITAL", parm.getValue("BIRTH_HOSPITAL", 0));
		this.setValue("SCHOOL_NAME", parm.getValue("SCHOOL_NAME", 0));
		this.setValue("SCHOOL_TEL", parm.getValue("SCHOOL_TEL", 0));
		//this.setValue("TEL_HOME", parm.getValue("TEL_HOME", 0));
		//this.setValue("EMAIL", parm.getValue("EMAIL", 0));
		//this.setValue("WEIXIN_ACCOUNT", parm.getValue("WEIXIN_ACCOUNT", 0));
		this.setValue("SOURCE", parm.getValue("SOURCE", 0));
		this.setValue("INSURANCE_COMPANY1_CODE", parm.getValue("INSURANCE_COMPANY1_CODE", 0));
		this.setValue("INSURANCE_NUMBER1", parm.getValue("INSURANCE_NUMBER1", 0));
		this.setValue("INSURANCE_COMPANY2_CODE", parm.getValue("INSURANCE_COMPANY2_CODE", 0));
		this.setValue("INSURANCE_NUMBER2", parm.getValue("INSURANCE_NUMBER2", 0));
		this.setValue("GUARDIAN1_NAME", parm.getValue("GUARDIAN1_NAME", 0));
		this.setValue("GUARDIAN1_RELATION", parm.getValue("GUARDIAN1_RELATION", 0));
		this.setValue("GUARDIAN1_TEL", parm.getValue("GUARDIAN1_TEL", 0));
		this.setValue("GUARDIAN1_PHONE", parm.getValue("GUARDIAN1_PHONE", 0));
		this.setValue("GUARDIAN1_COM", parm.getValue("GUARDIAN1_COM", 0));
		this.setValue("GUARDIAN1_ID_TYPE", parm.getValue("GUARDIAN1_ID_TYPE", 0));
		this.setValue("GUARDIAN1_ID_CODE", parm.getValue("GUARDIAN1_ID_CODE", 0));
		this.setValue("GUARDIAN1_EMAIL", parm.getValue("GUARDIAN1_EMAIL", 0));
		this.setValue("GUARDIAN2_NAME", parm.getValue("GUARDIAN2_NAME", 0));
		this.setValue("GUARDIAN2_RELATION", parm.getValue("GUARDIAN2_RELATION", 0));
		this.setValue("GUARDIAN2_TEL", parm.getValue("GUARDIAN2_TEL", 0));
		this.setValue("GUARDIAN2_PHONE", parm.getValue("GUARDIAN2_PHONE", 0));
		this.setValue("GUARDIAN2_COM", parm.getValue("GUARDIAN2_COM", 0));
		this.setValue("GUARDIAN2_ID_TYPE", parm.getValue("GUARDIAN2_ID_TYPE", 0));
		this.setValue("GUARDIAN2_ID_CODE", parm.getValue("GUARDIAN2_ID_CODE", 0));
		this.setValue("GUARDIAN2_EMAIL", parm.getValue("GUARDIAN2_EMAIL", 0));
		//    	this.setValue("REG_CTZ1_CODE", parm.getValue("REG_CTZ1_CODE", 0));//新增 挂号身份一
		//    	this.setValue("REG_CTZ2_CODE", parm.getValue("REG_CTZ2_CODE", 0));//新增 挂号身份二 
		//    	this.setValue("CTZ1_CODE", parm.getValue("CTZ1_CODE", 0));
		//    	this.setValue("CTZ2_CODE", parm.getValue("CTZ2_CODE", 0));
		//    	this.setValue("CTZ3_CODE", parm.getValue("CTZ3_CODE", 0));
		this.setValue("MEM_TYPE", parm.getValue("MEM_CODE", 0));
		this.setValue("FAMILY_DOCTOR", parm.getValue("FAMILY_DOCTOR", 0));
		this.setValue("ACCOUNT_MANAGER_CODE", parm.getValue("ACCOUNT_MANAGER_CODE", 0));
		//    	this.setValue("REASON", parm.getValue("REASON", 0));
		String sDate = parm.getValue("START_DATE", 0);
		String eDate = parm.getValue("END_DATE", 0);
		if(sDate.length()>0){
			this.setValue("START_DATE", sDate.substring(0, 10).replaceAll("-", "/"));
		}
		if(eDate.length()>0){
			this.setValue("END_DATE", eDate.substring(0, 10).replaceAll("-", "/"));
		}
		if(sDate.length()>0 && eDate.length()>0){
			//计算购买月龄
			int buyMonthAge = getBuyMonth(sDate.substring(0, 10).replaceAll("-", ""),
					eDate.substring(0, 10).replaceAll("-", ""));

			//发生月龄
			int currMonthAge = getBuyMonth(sDate.substring(0, 10).replaceAll("-", ""),
					this.getValue("IN_DATE").toString().substring(0, 10).replaceAll("-", ""));
			this.setValue("BUY_MONTH_AGE", String.valueOf(buyMonthAge));
			this.setValue("HAPPEN_MONTH_AGE", String.valueOf(currMonthAge));
			//System.out.println("购买月份："+buyMonthAge);
			//System.out.println("发生月份："+currMonthAge);
		}
		//监护人默认按钮
		//    	String deGuardian = parm.getValue("DEFAULT_GUARDIAN", 0);
		//    	if("1".equals(deGuardian)){
		//    		rubtton1.setSelected(true);
		//    	}else if("2".equals(deGuardian)){
		//    		rubtton2.setSelected(true);
		//    	}
		this.setValue("INCOME", parm.getValue("INCOME", 0));
		this.setValue("GUARDIAN1_AGE", parm.getValue("GUARDIAN1_AGE", 0));
		this.setValue("GUARDIAN2_AGE", parm.getValue("GUARDIAN2_AGE", 0));



	}
	/**
	 * 预办卡信息赋值
	 */
	public void setMemTradeForUI(TParm parm) {
		this.setValue("MEM_CODE", parm.getValue("MEM_CODE", 0));
		//this.setValue("REASON", parm.getValue("REASON", 0));
		this.setValue("MEM_FEE", parm.getValue("MEM_FEE", 0));
		this.setValue("DESCRIPTION", parm.getValue("DESCRIPTION", 0));
		this.setValue("INTRODUCER1", parm.getValue("INTRODUCER1", 0));
		this.setValue("INTRODUCER2", parm.getValue("INTRODUCER2", 0));
		this.setValue("INTRODUCER3", parm.getValue("INTRODUCER3", 0));
		//this.setValue("REASON", parm.getValue("REASON", 0));
		String sDate = parm.getValue("START_DATE", 0);
		if(sDate.length()>0){
			sDate = sDate.substring(0, 10).replaceAll("-", "/");
			this.setValue("START_DATE_TRADE", sDate);
		}else{//初始化：当前日期
			Timestamp now = StringTool.getTimestamp(new Date());
			this.setValue("START_DATE_TRADE",
					now.toString().substring(0, 10).replace('-', '/'));// 预生效日期-默认当天
		}
		String eDate = parm.getValue("END_DATE", 0);
		if(eDate.length()>0){
			eDate = eDate.substring(0, 10).replaceAll("-", "/");
			this.setValue("END_DATE_TRADE", eDate);
		}


	}

	/**
	 * 获取页面数据
	 */
	public TParm getData(){
		TParm parm = new TParm();
		//    	if (rubtton1.isSelected()){
		//    		//parm.addData("DEFAULT_GUARDIAN", "1");
		//    		//parm.setData("DEFAULT_GUARDIAN", "1");
		//    		this.setValue("DEFAULT_GUARDIAN", "1");
		//    	}else if(rubtton2.isSelected()){
		//    		//parm.addData("DEFAULT_GUARDIAN", "2");
		//    		//parm.setData("DEFAULT_GUARDIAN", "2");
		//    		this.setValue("DEFAULT_GUARDIAN", "2");
		//    	}

		parm = getParmForTag("MR_NO;PAT_NAME;PAT_NAME1;FIRST_NAME;LAST_NAME;PY1;ID_TYPE;REMARKS;"+	//患者信息
				"IDNO;OLD_NAME;SEX_CODE;BIRTH_DATE:Timestamp;NEW_BODY_DATE;"+
				"AGE;NATION_CODE;NATION_CODE2;RELIGION;MARRIAGE;"+
				"ADDRESS;CURRENT_ADDRESS;POST_CODE;RESID_ADDRESS;RESID_POST_CODE;"+
				"HOMEPLACE_CODE;BIRTH_HOSPITAL;SCHOOL_NAME;SCHOOL_TEL;CELL_PHONE;TEL_HOME;E_MAIL;SOURCE;"+
				"INSURANCE_COMPANY1_CODE;INSURANCE_NUMBER1;INSURANCE_COMPANY2_CODE;INSURANCE_NUMBER2;"+
				"GUARDIAN1_NAME;GUARDIAN1_RELATION;GUARDIAN1_TEL;GUARDIAN1_PHONE;DEFAULT_GUARDIAN;"+
				"GUARDIAN1_COM;GUARDIAN1_ID_TYPE;GUARDIAN1_ID_CODE;GUARDIAN1_EMAIL;"+	//监护人1信息
				"GUARDIAN2_NAME;GUARDIAN2_RELATION;GUARDIAN2_TEL;GUARDIAN2_PHONE;"+
				"GUARDIAN2_COM;GUARDIAN2_ID_TYPE;GUARDIAN2_ID_CODE;GUARDIAN2_EMAIL;"+	//监护人2信息
				"CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;SPECIAL_DIET;COMPANY_DESC;CUSTOMER_SOURCE;"+	//工作单位
				"MEM_TYPE;FAMILY_DOCTOR;ACCOUNT_MANAGER_CODE;" + //会员信息
				"START_DATE;END_DATE;BUY_MONTH_AGE;HAPPEN_MONTH_AGE;"+ 
				"MEM_CODE;START_DATE_TRADE;END_DATE_TRADE;MEM_FEE;INTRODUCER1;INTRODUCER2;INTRODUCER3;DESCRIPTION;BOOK_BUILD;"//预办卡信息
				// add by wangqing 20201001 
				+ "INCOME;GUARDIAN1_AGE;GUARDIAN2_AGE"); // 家庭年可支配收入、监护人/联系人1 的年龄、监护人/联系人2 的年龄

		String birthDate = parm.getValue("BIRTH_DATE");
		//if(this.modifyFlg){//有“MODIFY”权限则校验，否则不校验
		if(birthDate.length()>0){//生日
			birthDate = birthDate.substring(0, 10).replaceAll("-", "/");
			parm.setData("BIRTH_DATE", birthDate);
		}
		//}
		String sDate = parm.getValue("START_DATE");
		String eDate = parm.getValue("END_DATE");
		if(sDate.length()>0){//生效日期
			sDate = sDate.substring(0, 10).replaceAll("-", "/");
			parm.setData("START_DATE", sDate);
		}
		if(eDate.length()>0){//失效日期
			eDate = eDate.substring(0, 10).replaceAll("-", "/");
			parm.setData("END_DATE", eDate);
		}
		String sDateTrade = parm.getValue("START_DATE_TRADE");
		String eDateTrade = parm.getValue("END_DATE_TRADE");
		if(sDateTrade.length()>0){//预生效日期
			sDateTrade = sDateTrade.substring(0, 10).replaceAll("-", "/");
			parm.setData("START_DATE_TRADE", sDateTrade);
		}
		if(eDateTrade.length()>0){//预失效日期
			eDateTrade = eDateTrade.substring(0, 10).replaceAll("-", "/");
			parm.setData("END_DATE_TRADE", eDateTrade);
		}
		//System.out.println("sDate="+sDate+" eDate="+eDate);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());

		parm.setData("MEM_DESC", this.getTextFormat("MEM_CODE").getText());
		parm.setData("ACCOUNT_MANAGER_NAME", this.getTextFormat("ACCOUNT_MANAGER_CODE").getText());
		parm.setData("INSURANCE_COMPANY1_NAME", "");
		parm.setData("INSURANCE_COMPANY2_NAME", "");
		parm.setData("COMPANY_DESC", this.getValue("COMPANY_DESC"));
		parm.setData("REASON", "");
		parm.setData("OLDNAME", this.getValue("OLD_NAME"));
		parm.setData("CHECK_BOX", "N");//add by sunqy 20140527
		//打印测试
		System.out.println("页面数据："+parm);

		String bookBuild = parm.getValue("BOOK_BUILD");
		if("01".equals(bookBuild)){
			parm.setData("BOOK_BUILD", "Y");
		}
		if("02".equals(bookBuild)){
			parm.setData("BOOK_BUILD", "N");
		}

		return parm;
	}

	/**
	 * 修改病患信息读取参数
	 *
	 * @param modifyPat
	 *            Pat
	 * @return Pat
	 */
	public Pat readModifyPat(Pat modifyPat) {
		modifyPat.modifyName(getValueString("PAT_NAME")); // 姓名
		modifyPat.modifySexCode(getValueString("SEX_CODE")); // 性别
		modifyPat.modifyBirthdy(TCM_Transform
				.getTimestamp(getValue("BIRTH_DATE"))); // 出生日期
				//        modifyPat.modifyBirthdy(TCM_Transform
		//                .getTimestamp(getValue("BIRTH_DATE"))); // 出生日期
		modifyPat.modifyBirthdy(birthDay); // 出生日期
		modifyPat.modifyCtz1Code(""); // 付款方式1
		modifyPat.modifyhomePlaceCode(""); // 出生地
		modifyPat.modifyOccCode(""); // 职业
		modifyPat.modifyIdNo(getValueString("IDNO")); // 身份证号
		modifyPat.modifySpeciesCode(getValueString("NATION_CODE2")); // 种族SPECIES_CODE
		modifyPat.modifyNationCode(getValueString("NATION_CODE")); // 国籍
		modifyPat.modifyMarriageCode(getValueString("MARRIAGE")); // 婚姻状态MARRIAGE_CODE
		modifyPat.modifyCompanyDesc(""); // 单位 COMPANY_DESC
		modifyPat.modifyTelCompany(""); // 公司电话
		modifyPat.modifyPostCode(getValueString("POST_CODE")); // 邮编
		modifyPat.modifyResidAddress(getValueString("RESID_ADDRESS")); // 户籍地址
		modifyPat.modifyResidPostCode("");
		modifyPat.modifyContactsName("");
		modifyPat.modifyRelationCode("");
		modifyPat.modifyContactsTel("");
		modifyPat.modifyContactsAddress("");
		modifyPat.modifyTelHome(getValueString("TEL_HOME")); // 家庭电话
		modifyPat.modifyAddress(getValueString("ADDRESS")); // 家庭住址
		modifyPat.modifyAddress(getValueString("CURRENT_ADDRESS")); // 现住址
		modifyPat.modifyForeignerFlg(false); // 外国人注记
		modifyPat.modifyBirthPlace(""); // 籍贯
		modifyPat.modifyCompanyAddress(""); // 单位地址
		modifyPat.modifyCompanyPost(""); // 单位地址
		return modifyPat;
	}
	/**
	 * 计算年龄
	 */
	public void setBirth(boolean flg) {
		/*if (getValue("BIRTH_DATE") == null || "".equals(getValue("BIRTH_DATE")))
            return;
//        String AGE = com.javahis.util.StringUtil.showAge(
//                (Timestamp) getValue("BIRTH_DATE"),
//                (Timestamp) getValue("IN_DATE"));
        birthDay=onQueryBirthDateByMrNO(getValue("MR_NO").toString().trim());
        String AGE = com.javahis.util.DateUtil.showAge(
        		(Timestamp) getValue("BIRTH_DATE"),
                (Timestamp) getValue("IN_DATE"));
        this.messageBox("111::"+AGE);*/
		if(flg){
			birthDay=(Timestamp) getValue("BIRTH_DATE");
		}
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp temp = birthDay == null ? sysDate : birthDay;
		String age = "0";
		age = DateUtil.showAge(temp, sysDate);
		setValue("AGE", age);
	}
	/**
	 * 设置性别
	 */
	public void setSexCode(String sexCode) {
		try {
			int a = Integer.parseInt(sexCode);
			int b = a%2;
			if(b==0){//女
				this.setValue("SEX_CODE", 2);
			}else if(b==1){//男
				this.setValue("SEX_CODE", 1);
			}
			this.grabFocus("NATION_CODE");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * 查询预失效日期-【预生效日期变化时】
	 */
	public void queryValidDays(){
		String validDays = "";
		//Timestamp now = StringTool.getTimestamp(new Date());
		String MemCode = this.getValueString("MEM_CODE");
		String sDate = this.getValueString("START_DATE_TRADE");
		if(sDate.length()>0){
			sDate = sDate.substring(0, 10);
			//查询有效天数
			String sql = "SELECT VALID_DAYS FROM MEM_MEMBERSHIP_INFO WHERE  MEM_CODE = '"+MemCode+"'";
			//System.out.println("sql="+sql);
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if(parm.getCount()>0){
				validDays = parm.getValue("VALID_DAYS", 0);
				//getNextDay
				String eTradeDate = getNextDay2(sDate,validDays);
				this.setValue("END_DATE_TRADE", eTradeDate.replaceAll("-", "/"));
			}
		}
	}

	/**
	 * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
	 * 
	 * @param date
	 *            String
	 * @return String
	 */
	public String getNextDay(String nowdate, String delay) {
		try {
			//System.out.println("nowdate="+nowdate+" delay="+delay);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String mdate = "";
			Date d = strToDate(nowdate);
			long myTime = (d.getTime() / 1000) + (Integer.parseInt(delay)-1) * 24
					* 60 * 60;
			d.setTime(myTime * 1000);
			mdate = format.format(d);
			return mdate;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 得到一个时间延后n年的时间点
	 */
	public String getNextDay2(String nowdate, String delay) {
		try {
			SimpleDateFormat formateDate=new SimpleDateFormat("yyyy-MM-dd");
			Date d = strToDate(nowdate);
			Calendar canlandar = Calendar.getInstance();
			canlandar.setTime(d);
			canlandar.add(Calendar.YEAR, +Integer.parseInt(delay));
			canlandar.add(Calendar.DAY_OF_MONTH, -1);
			String a = formateDate.format(canlandar.getTime());
			//System.out.println("--------->"+a);
			return a;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd
	 * 
	 * @param strDate
	 *            String
	 * @return Date
	 */
	public Date strToDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}
	/**
	 * 获取购买月龄
	 */
	public int getBuyMonth(String s, String s1){
		//System.out.println("s="+s+" s1="+s1);
		Date d = null;
		Date d1 = null;
		DateFormat df=new SimpleDateFormat("yyyyMMdd");
		try {  
			d = df.parse(s);
			d1=df.parse(s1);
			//比较日期大小
			//		    if(d.getTime()>d1.getTime()){
			//		      System.out.println(df.format(d));
			//		    }else{
			//		      System.out.println(df.format(d1));
			//		     	  }
		}catch (ParseException e){    
			e.printStackTrace(); }
		Calendar c = Calendar.getInstance();

		c.setTime(d);
		//c.add(Calendar.YEAR, 1);  //年份加一年
		//System.out.println(df.format(c.getTime()));//按照yyyyMMdd格式输出

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);

		c.setTime(d1);
		int year1 = c.get(Calendar.YEAR);
		int month1 = c.get(Calendar.MONTH);

		int result;
		if(year==year1){
			result=month1-month;//两个日期相差几个月，即月份差
		}else{
			result=12*(year1-year)+month1-month;//两个日期相差几个月，即月份差
		}
		return result;
	}
	//    /**
	//     * 得到最大的编号 +1
	//     *
	//     * @param dataStore
	//     *            TDataStore
	//     * @param columnName
	//     *            String
	//     * @return String
	//     */
	//    public int getMaxSeq(String maxValue, String tableName,
	//                         String where1,String value1,String where2,String value2) {
	//    	String sql = "SELECT MAX("+maxValue+") AS "+maxValue+" FROM "+tableName+" WHERE 1=1 ";
	//    	if(where1.trim().length()>0){
	//    		sql += " AND "+where1+" ='"+value1+"'";
	//    	}
	//    	if(where2.trim().length()>0){
	//    		sql += " AND "+where2+" ='"+value2+"'";
	//    	}
	//    	System.out.println("最大的编号sql="+sql);
	//    	// 保存最大号
	//        int max = 0;
	//    	//查询最大序号
	//    	TParm seqParm = new TParm(TJDODBTool.getInstance().select(sql));
	//    	String seq = seqParm.getValue(maxValue,0).toString().equals("")?"0"
	//    			:seqParm.getValue(maxValue,0).toString();
	//    	System.out.println("seq="+seq);
	//    	int value = Integer.parseInt(seq);
	//    	System.out.println("value="+value);
	//    	// 保存最大值
	//        if (max < value) {
	//            max = value;
	//        }
	//        // 最大号加1
	//        max++;
	//        System.out.println("最大的编号 +1="+max);
	//        return max;
	//        
	//    }
	/**
	 * 生成交易号表的会员卡编号
	 */
	public String getMemCardNo(String mrNo) {
		String result = "";
		String sql = "SELECT MEM_CARD_NO,MEM_CODE FROM MEM_TRADE WHERE MR_NO = '"+mrNo+"' AND STATUS = '1' ORDER BY MEM_CARD_NO DESC";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String memCardNo = parm.getValue("MEM_CARD_NO",0).toString().equals("")?"0"
				:parm.getValue("MEM_CARD_NO",0).toString();
		if(memCardNo.length()>0 && !"0".equals(memCardNo)){
			String reason = this.getText("REASON");
			if("续卡".equals(reason)){
				result = memCardNo;
			}else{
				//取后三位转成int+1，再拼上病案号
				if(memCardNo.length()>3){
					String no = memCardNo.substring(memCardNo.length()-3);
					//System.out.println("后三位："+no);
					int intNo = Integer.parseInt(no)+1;
					result = StringTool.fillLeft(String.valueOf(intNo) ,3 ,"0" );
					result = mrNo + result;
				}
			}

		}else{
			//    		//病案号+001
			//    		result = mrNo + "001";

			sql = "SELECT MAX(CARD_NO) CARD_NO FROM EKT_ISSUELOG WHERE MR_NO='"+mrNo+"' ";
			parm = new TParm(TJDODBTool.getInstance().select(sql));
			memCardNo = parm.getValue("CARD_NO",0).toString().equals("")?"0"
					:parm.getValue("CARD_NO",0).toString();
			if(memCardNo.length()>0 && !"0".equals(memCardNo)){
				//取后三位转成int+1，再拼上病案号
				if(memCardNo.length()>3){
					String no = memCardNo.substring(memCardNo.length()-3);
					//System.out.println("后三位："+no);
					int intNo = Integer.parseInt(no)+1;
					result = StringTool.fillLeft(String.valueOf(intNo) ,3 ,"0" );
					result = mrNo + result;
				}
			}else{
				//病案号+001
				result = mrNo + "001";

			}

		}
		//System.out.println("生成会员卡编号"+result);
		return result;
	}
	/**
	 * 判断交易表是否存在
	 */
	public boolean exitsMemTrade(String mrNo) {
		boolean flg = false;//不存在
		String sql = "SELECT * FROM MEM_TRADE WHERE MR_NO = '"+mrNo+"' AND STATUS = '0' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
			flg = true;
		}
		//System.out.println("交易表是否存在"+flg);
		return flg;
	}
	/**
	 * 数据处理
	 */
	public TParm handleData(TParm parm) {
		TParm result = new TParm();

		return result;
	}
	/**
	 * 取号原则:mem_trade
	 */
	public String getMEMTradeNo() {
		return SystemTool.getInstance().getNo("ALL", "EKT", "MEM_TRADE_NO",
				"MEM_TRADE_NO");
	}

	/**
	 * 身份证读卡回传值add by huangjw 20141110
	 * @param parm
	 */
	public void setValueByQueryIdNo(TParm parm){
		if(this.getValueString("MR_NO").equals(parm.getValue("MR_NO"))){
			//TParm p = this.getParmForTag("MR_NO;PY1");
			clearValue("PAT_NAME;SEX_CODE;BRITHPLACE;BIRTH_DATE;IDNO;RESID_ADDRESS");
			this.setValueForParm("PAT_NAME;SEX_CODE;BRITHPLACE;BIRTH_DATE;IDNO;RESID_ADDRESS", parm);
		}else{
			this.setValue("MR_NO",parm.getValue("MR_NO"));
			onMrno();
			clearValue("PAT_NAME;SEX_CODE;BRITHPLACE;BIRTH_DATE;IDNO;RESID_ADDRESS");
			this.setValueForParm("PAT_NAME;SEX_CODE;BRITHPLACE;BIRTH_DATE;IDNO;RESID_ADDRESS", parm);
		}
		setBirth(true);//身份证读卡时 年龄处理 modify by  huangjw
	}
	/**
	 * 身份正读卡操作 ==============pangben 2013-3-18
	 */
	public void onIdCard() {
		if (JOptionPane.showConfirmDialog(null, "是否覆盖？", "信息",
				JOptionPane.YES_NO_OPTION) == 0) {//读身份证时，加提示信息，add by huangjw 20141119
			TParm idParm = IdCardO.getInstance().readIdCard();
			if (idParm.getErrCode() < 0) {
				this.messageBox(idParm.getErrText());
				return;
			}
			if (idParm.getCount() > 0) {// 多行数据显示
				if (idParm.getCount() == 1) {// pangben 2013-8-8 只存在一条数据
					// this.setValue("MR_NO", idParm.getValue("MR_NO",0));
					// onMrno();
					setValueByQueryIdNo(idParm);// modify by huangjw 20141110
				} else {
					Object obj = openDialog(
							"%ROOT%\\config\\sys\\SYSPatChoose.x", idParm);
					TParm patParm = new TParm();
					if (obj != null) {
						patParm = (TParm) obj;
						// this.setValue("MR_NO", patParm.getValue("MR_NO"));
						// onMrno();
						setValueByQueryIdNo(patParm);// modify by huangjw
						// 20141110
					} else {
						return;
					}
				}
				// setValue("VISIT_CODE_F", "Y"); // 复诊
				this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
						TypeTool.getString(getValue("PAT_NAME"))));// 简拼
				// setPatName1();// 设置英文
			} else {
				String sql = "SELECT MR_NO,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS,CURRENT_ADDRESS,RESID_ADDRESS FROM SYS_PATINFO WHERE PAT_NAME LIKE '"
						+ idParm.getValue("PAT_NAME") + "%'";
				TParm infoParm = new TParm(TJDODBTool.getInstance().select(sql));
				if(!checkPatIsExist()){
					this.onNew(); // add by huangtt 20141022
				}
				if (infoParm.getCount() <= 0) {
					this.messageBox(idParm.getValue("MESSAGE"));
					// setValue("VISIT_CODE_C", "Y"); // 默认初诊
					// callFunction("UI|MR_NO|setEnabled", false); //
					// 病案号不可编辑--初诊操作
				} else {
					this.messageBox("存在相同姓名的病患信息");
					this.grabFocus("PAT_NAME");// 默认选中
				}
				this.setValue("PAT_NAME", idParm.getValue("PAT_NAME"));
				this.setValue("ID_TYPE", "01"); // add by huangtt 20141022
				this.setValue("IDNO", idParm.getValue("IDNO"));
				this.setValue("BIRTH_DATE", idParm.getValue("BIRTH_DATE"));
				this.setValue("SEX_CODE", idParm.getValue("SEX_CODE"));
				sql = "SELECT ID FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SPECIES' AND CHN_DESC LIKE '"
						+ idParm.getValue("BRITHPLACE") + "%'";
				TParm species = new TParm(TJDODBTool.getInstance().select(sql));
				if (species.getCount() > 0) {
					this.setValue("NATION_CODE2", species.getValue("ID", 0));
				}
				this.setValue("NATION_CODE", "86"); // add by huangtt 20141022
				// this.setValue("ADDRESS", idParm.getValue("ADDRESS"));// 家庭地址
				this.setValue("CURRENT_ADDRESS", idParm
						.getValue("RESID_ADDRESS"));// 现地址 add by huangtt
				// 20141110
				this
				.setValue("RESID_ADDRESS", idParm
						.getValue("RESID_ADDRESS"));// 户籍地址
				this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
						TypeTool.getString(getValue("PAT_NAME"))));// 简拼
				// setPatName1();// 设置英文

			}
		}
	}

	/**
	 * 根据CRM传回的病案号，查询此病患是否已存在
	 * @return
	 */
	public boolean checkPatIsExist(){
		boolean flg=false;
		String mr_no=this.getValueString("MR_NO");
		String sql="select * from sys_patinfo where mr_no='"+mr_no+"'";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
			flg=true;
		}
		return flg;
	}

	/**
	 * 泰心医疗卡读卡 =========================
	 */
	public void onEKTcard() {
		// 南京医保卡读卡操作
		// 泰心医疗卡操作
		p3 = EKTIO.getInstance().TXreadEKT();
		// System.out.println("P3=================" + p3);
		// 6.释放读卡设备
		// int ret99 = NJSMCardDriver.FreeReader(ret0);
		// 7.注销TFReader.dll
		// int ret100 = NJSMCardDriver.close();
		StringBuffer sql = new StringBuffer();
		int typeEKT = -1; // 医疗卡类型
		if (null != p3 && p3.getValue("identifyNO").length() > 0) {
			sql.append("SELECT * FROM SYS_PATINFO WHERE MR_NO in (select max(MR_NO) from SYS_PATINFO");
			typeEKT = 1; // 南京医保卡
			sql.append(" WHERE IDNO='" + p3.getValue("identifyNO").trim()
					+ "' ) ");
		} else if (null != p3 && p3.getValue("MR_NO").length() > 0) {
			// sql
			// .append("SELECT A.MR_NO,A.NHI_NO,B.BANK_CARD_NO FROM SYS_PATINFO A,EKT_ISSUELOG B WHERE A.MR_NO = B.MR_NO AND B.CARD_NO ='"
			// + p3.getValue("MR_NO")
			// + p3.getValue("SEQ")
			// + "' AND WRITE_FLG='Y'");
			typeEKT = 2; // 泰心医疗卡
			//this.setValue("PAY_WAY", "PAY_MEDICAL_CARD"); // 支付方式修改
			//this.setValue("CONTRACT_CODE", "");
			//callFunction("UI|CONTRACT_CODE|setEnabled", false); // 记账单位不可编辑
		}
		// 通过身份证号查找是否存在此病患信息
		// callFunction("UI|FOREIGNER_FLG|setEnabled", false);//其他证件不可编辑
		if (typeEKT > 0) {
			onReadTxEkt(p3, typeEKT);
		} else {
			this.messageBox("此医疗卡无效");
			return;
		}
		// 南京医保卡操作
		if (typeEKT == 1) {
			NJSMCardDriver.close();
			NJSMCardYYDriver.close();
		}
		//setValue("EKT_CURRENT_BALANCE", p3.getDouble("CURRENT_BALANCE"));
		// ===zhangp 20120318 end
	}

	/**
	 * 医疗卡读卡操作
	 * 
	 * @param IDParm
	 *            TParm
	 * @param typeEKT
	 *            int
	 */
	private void onReadTxEkt(TParm IDParm, int typeEKT) {
		// TParm IDParm = new TParm(TJDODBTool.getInstance().select(sql));
		// 通过身份证号查找是否存在次病患
		if (IDParm.getValue("MR_NO").length() > 0) {
			setValue("MR_NO", IDParm.getValue("MR_NO")); // 存在将病案号显示
			onMrno(); // 执行赋值方法
			//setValue("NHI_NO", IDParm.getValue("NHI_NO")); // ==-============pangben
			//tjINS = true; // 天津医保使用，判断是否执行了医疗卡操作
			//callFunction("UI|PAY_WAY|setEnabled", false); // 支付类别
		} else {
			this.messageBox("此医疗卡无效"); // 不存在显示市民卡上的信息：身份证号、名称、医保号
			switch (typeEKT) {
			// 南京医保卡 没有此病患信息时执行赋值操作
			case 1:
				this.setValue("IDNO", p3.getValue("identifyNO")); // 身份证号
				//this.setValue("NHI_NO", p3.getValue("siNO")); // 医保号
				this.setValue("PAT_NAME", p3.getValue("patientName").trim()); // 姓名
				break;
				// 泰心医疗卡没有此病患信息时执行赋值操作
			case 2:

				// this.setValue("MR_NO",p3.getValue("MR_NO"));
				txEKT = true; // 泰心医疗卡写卡操作管控
				break;
			}
			// this.setValue("VISIT_CODE_C","N");
			callFunction("UI|MR_NO|setEnabled", false); // 病案号不可编辑
			this.grabFocus("PAT_NAME");
			//setValue("VISIT_CODE_C", "Y"); // 默认初诊
		}
	}

	/**
	 * 地址复写操作
	 */
	public void onAddress() {
		String rePostCode = this.getValueString("RESID_POST_CODE");
		String reAddress = this.getValueString("RESID_ADDRESS");
		if(reAddress.length()>0){
			this.setValue("CURRENT_ADDRESS", reAddress);
		}
		if(rePostCode.length()>0){
			this.setValue("POST_CODE", rePostCode);
		}
		this.grabFocus("SPECIAL_DIET");
	}

	/**
	 * 身份证号校验
	 */
	public boolean isCard(String idcard)
	{
		return idcard == null || "".equals(idcard) ? false : Pattern.matches(   
				"(^\\d{15}$)|(\\d{17}(?:\\d|x|X)$)", idcard);
	}



	/**
	 * 初复诊状态

	public void onClickRadioButton() {
		if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_C"))) {
			this.onClear();
	    	callFunction("UI|MR_NO|setEnabled", false); 	// 病案号置灰
	    	callFunction("UI|new|setEnabled", false); 		// 新增按钮置灰
	    	callFunction("UI|save|setEnabled", true); 		// 保存按钮置可用
	    	callFunction("UI|buycard|setEnabled", false);   // 购卡按钮置灰
	    	this.grabFocus("PAT_NAME");
		}
		if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_F"))) {
			this.onClear();
			callFunction("UI|MR_NO|setEnabled", true); 		// 病案号置有效
	    	callFunction("UI|new|setEnabled", false); 		// 新增按钮置灰
	    	callFunction("UI|save|setEnabled", true); 		// 保存按钮置可用
	    	callFunction("UI|buycard|setEnabled", false);   // 购卡按钮置灰
			this.grabFocus("MR_NO");
		}

	}  */

	/**
	 * 检测病患相同姓名
	 */
	public void onPatName(String type) {
		String py1 = this.getValueString("PY1");
		String patName = this.getValueString("PAT_NAME");
		if (StringUtil.isNullString(patName)) {	
			return;
		}
		String sexCode = this.getValueString("SEX_CODE");
		if (StringUtil.isNullString(sexCode)) {
			this.grabFocus("PY1");
			return;
		}
		try {
			String selPat = "SELECT   A.MR_NO, A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
					+ " POST_CODE, RESID_ADDRESS,CURRENT_ADDRESS,ID_TYPE"
					+ " FROM SYS_PATINFO A "
					+ " WHERE PAT_NAME = '"
					+ patName
					+ "' AND  SEX_CODE='"
					+ sexCode    // add by huangtt 20131126
					+ "' "
					+ " ORDER BY A.OPT_DATE,A.BIRTH_DATE";
			// ===zhangp 20120319 end
			TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
			if (same.getErrCode() != 0) {
				this.messageBox_(same.getErrText());
			}
			//			setPatName1();
			// 选择病患信息
			if (same.getCount("MR_NO") > 0) {
				//				int sameCount = this.messageBox("提示信息", "已有相同姓名病患信息,是否继续保存此人信息", 0);
				//				int sameCount = JOptionPane.showConfirmDialog(null, "已有相同姓名病患信息,是否继续保存此人信息", "提示信息", JOptionPane.YES_OPTION);
				//add by huangtt 20140926 start
				Object[] possibilities = { "  是   ", "  否   " };
				int sameCount = JOptionPane.showOptionDialog(null,
						"已有相同姓名的病患信息,是否继续保存此人信息", "提示信息",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
						null, possibilities, possibilities[1]);
				//add by huangtt 20140926 end
				if (sameCount != 1) {
					if("name".equals(type)){
						this.grabFocus("FIRST_NAME");
					}
					if("sex".equals(type)){
						this.grabFocus("BIRTH_DATE");
					}

					return;
				}
				Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
					onQueryNO(patParm.getValue("MR_NO"));
					return;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if("name".equals(type)){
			this.grabFocus("FIRST_NAME");
		}
		if("sex".equals(type)){
			this.grabFocus("BIRTH_DATE");
		}

	}

	/**
	 * 设置英文名
	 */
	public void setPatName1() {
		String patName1 = SYSHzpyTool.getInstance().charToAllPy(
				TypeTool.getString(getValue("PAT_NAME")));
		setValue("PAT_NAME1", patName1);
	}

	/**
	 * 会员注册打印
	 */
	public void onPrint(String newMrNo) {
		if(this.getValueString("MEM_CODE").equals("")){
			return;
		}
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT",
				(Operator.getRegion() != null &&
				Operator.getRegion().length() > 0 ?
						Operator.getHospitalCHNFullName() : "所有医院") );
		parm.setData("MR_NO", "TEXT", newMrNo); //病案号
		parm.setData("MEM_NAME", "TEXT", parmPrint.getValue("PAT_NAME")); //会员名称
		parm.setData("SEX_TYPE", "TEXT", parmPrint.getValue("SEX_CODE").equals("1") ? "男" : "女"); //性别
		parm.setData("MEM_TYPE", "TEXT", this.getTextFormat("MEM_CODE").getText()); //发卡类型
		parm.setData("FEE", "TEXT", StringTool.round(parmPrint.getDouble("MEM_FEE"), 2)); //费用
		parm.setData("FEE_NAME", "TEXT",
				StringUtil.getInstance().numberToWord(
						parmPrint.getDouble("MEM_FEE"))); //大写金额

		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
				getInstance().getDBTime()), "yyyy/MM/dd"); //年月日
		String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "HH:mm"); //时分

		parm.setData("OPT_DATE", "TEXT", yMd); //打印时间
		parm.setData("OPT_MINUTE", "TEXT", hms); //具体时间：时分
		parm.setData("OPT_USER", "TEXT", Operator.getName()); //操作人
		//打印
		this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMRegister.jhw", parm ,true);


	}

	/**
	 * 补印
	 */
	public void onRePrint() {
		if(parmPrint==null){
			parmPrint = getData();//打印parm
		}
		String newMrNo = this.getValueString("MR_NO");
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT",
				(Operator.getRegion() != null &&
				Operator.getRegion().length() > 0 ?
						Operator.getHospitalCHNFullName() : "所有医院") );
		parm.setData("MR_NO", "TEXT", newMrNo); //病案号
		parm.setData("MEM_NAME", "TEXT", parmPrint.getValue("PAT_NAME")); //会员名称
		parm.setData("SEX_TYPE", "TEXT", parmPrint.getValue("SEX_CODE").equals("1") ? "男" : "女"); //性别
		parm.setData("MEM_TYPE", "TEXT", this.getTextFormat("MEM_CODE").getText()); //发卡类型
		parm.setData("FEE", "TEXT", StringTool.round(parmPrint.getDouble("MEM_FEE"), 2)); //费用
		parm.setData("FEE_NAME", "TEXT",
				StringUtil.getInstance().numberToWord(
						parmPrint.getDouble("MEM_FEE"))); //大写金额

		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
				getInstance().getDBTime()), "yyyy/MM/dd"); //年月日
		String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "HH:mm"); //时分

		parm.setData("OPT_DATE", "TEXT", yMd); //打印时间
		parm.setData("OPT_MINUTE", "TEXT", hms); //具体时间：时分
		parm.setData("OPT_USER", "TEXT", Operator.getName()); //操作人
		//打印
		this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMRegister2.jhw", parm ,true);
	}

	/**
	 * 得到TextFormat对象
	 * @param tagName String
	 * @return TTextFormat
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}

	public void onCRM() throws ParseException{
		TParm parm = new TParm();
		//		parm.addData("MR_NO", this.getValueString("MR_NO"));
		TParm result = (TParm) this.openDialog("%ROOT%\\config\\reg\\REGCRM.x",parm,false);
		if(result != null){
			this.setValue("MR_NO", result.getData("MR_NO",0));
			this.onMrno();
			this.setValue("BOOK_BUILD", "01");  //add by huangtt 20150603
			callFunction("UI|save|setEnabled", true); 		// 保存按钮置可用
			this.grabFocus("PAT_NAME");
		}

	}

	/**
	 * 购卡退卡历史交易查询
	 */
	public void onHisInfo(){
		TParm parm = new TParm();
		//获取页面数据
		parm = getData();
		TParm reParm = (TParm)this.openDialog(
				"%ROOT%\\config\\mem\\MEMMarketCardQuery.x", parm);
	}

	/**
	 * 保险专员录入信息按钮事件      add by sunqy 2014/05/14
	 */
	public void onInsureInfo(){
		TParm parm = new TParm();
		parm.setData("MR_NO",this.getValueString("MR_NO"));
		parm.setData("EDIT", "Y");
		//    	System.out.println("````````````parm=="+parm);
		TParm reParm = (TParm)this.openDialog(
				"%ROOT%\\config\\mem\\MEMInsureInfo.x", parm);
	}

	/**
	 * 会费查询：
	 * 1,根据会员卡种类+办卡方式+年限自动关联(不用)
	 * 2,根据会费编号MEM_CODE查询
	 */
	//    public boolean onQueryFee(){
	//    	boolean flg = false;
	//    	if(this.getValueString("MEM_CODE").length() >0 &&
	//    			this.getValueString("CARD_YEAR").length() > 0 &&
	//    			this.getValueString("REASON").length() > 0){
	//    		flg = onFee();
	//    	}else{
	//    		flg = false;
	//    		return flg;
	//    	}
	//    	return flg;
	//    }
	public void onQueryFee(){
		String memCode = this.getValueString("MEM_CODE");
		String sql = "SELECT MEM_FEE,MEM_IN_REASON,MEM_CARD FROM MEM_MEMBERSHIP_INFO WHERE MEM_CODE = '"+memCode+"' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
			this.setValue("MEM_FEE", parm.getValue("MEM_FEE", 0));
			this.setValue("REASON", parm.getValue("MEM_IN_REASON", 0));
			this.setValue("MEM_TYPE1", parm.getValue("MEM_CARD", 0));

			try {
				this.onContinuationCard();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 会费查询2:没查到提示没有对应的会员类型
	 */
	//    public boolean onFee() {
	//    	boolean flg = false;
	//    	String memCode = this.getValueString("MEM_CODE");
	//    	String cardYear = this.getValueString("CARD_YEAR");
	//    	String reason = this.getValueString("REASON");
	//    	String sql = "SELECT MEM_FEE FROM MEM_MEMBERSHIP_INFO WHERE MEM_CODE = '"+memCode+"' " +
	//    			" AND VALID_DAYS = '"+cardYear+"' AND MEM_IN_REASON = '"+reason+"'";
	//    	System.out.println("sql-------->"+sql);
	//    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
	//    	if(parm.getCount()>0){
	//    		this.setValue("MEM_FEE", parm.getValue("MEM_FEE", 0));
	//    		//判断续费情况-是否和会员身份一致
	//    		if(this.getValueString("REASON").equals("02")){
	//    			if(!this.getValueString("MEM_CODE").equals(this.getValueString("MEM_TYPE"))){
	//    				this.messageBox("发卡类型与会员身份不一致,无法续卡！");
	//    				ifTradeCard = false;//不可以预办卡
	//    				flg = false;
	//    				return flg;
	//    			}
	//    		}
	//    		flg = true;
	//    		ifTradeCard = true;//可以预办卡
	//    	}else{
	//    		this.messageBox("没有对应的会员类型！");
	//    		//清空
	//    		this.setValue("MEM_FEE", "");
	//    		flg = false;
	//    		ifTradeCard = false;//不可以预办卡
	//    		return flg;
	//    	}
	//    	return flg;
	//    }

	/**
	 * 医疗卡购卡
	 */
	public void onEKTBuy() {
		TParm parm = new TParm();
		//获取页面数据
		parm = getData();
		TParm reParm = (TParm)this.openDialog(
				"%ROOT%\\config\\ekt\\EKTWorkUI.x", parm);
		String mr_no = this.getValueString("MR_NO");
		this.onClear();
		this.setValue("MR_NO", mr_no);
		this.onMrno(); 
	}

	/**
	 * 续卡判断
	 * @throws ParseException 
	 */
	public void onContinuationCard() throws ParseException{
		String reason = this.getText("REASON");
		//    	TTextFormat t = this.getTextFormat("REASON");
		if("续卡".equals(reason)){
			String memType = this.getValueString("MEM_TYPE");
			if(memType.equals("")){
				this.messageBox("该病患没有会员卡，不允许续卡！");
				this.clearValue("REASON;MEM_CODE;MEM_FEE");
				return;
			}else{
				String date =this.getValueString("END_DATE");
				Timestamp  ts = Timestamp.valueOf(date);
				this.setValue("START_DATE_TRADE", StringTool.rollDate(ts, +1).toString()
						.substring(0, 10).replace('-', '/'));
				queryValidDays();
			}
		}
		if("升级".equals(reason)){
			String sql = "SELECT * FROM MEM_TRADE WHERE STATUS = '1' AND REMOVE_FLG = 'N' AND MR_NO='"+this.getValueString("MR_NO")+"'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if(parm.getCount()>0){
				this.messageBox("该卡没有停卡，不允许升级");
				this.clearValue("REASON;MEM_CODE;MEM_FEE");
				return;
			}else{
				Timestamp date = SystemTool.getInstance().getDate();
				this.setValue("START_DATE_TRADE", date);
				queryValidDays();
			}

		}
		if("新办".equals(reason)){
			TParm parm = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemTrade1(this.getValueString("MR_NO"))));
			if(parm.getCount()>0){
				this.messageBox("该病患有办卡记录，不允许新办");
				this.clearValue("REASON;MEM_CODE;MEM_FEE");
				return;
			}
		}
	}

	/**
	 * 打印腕带
	 */
	public void onWrist() {
		if (this.getValueString("MR_NO").length() == 0) {
			return;
		}
		String birthday = this.getValueString("BIRTH_DATE");
		if(birthday.length()>8){
			birthday = birthday.substring(0, 10).replaceAll("-", "/");
		}
		TParm print = new TParm();
		print.setData("Barcode", "TEXT", this.getValueString("MR_NO"));
		print.setData("PatName", "TEXT", this.getValueString("PAT_NAME"));
		String sexName=this.getTextFormat("SEX_CODE").getText();//modify by huangjw 20140715
		print.setData("Sex", "TEXT", sexName);
		print.setData("BirthDay", "TEXT", birthday);
		//	        this.openPrintDialog(IReportTool.getInstance().getReportPath("MEMWrist.jhw"),
		//	                             IReportTool.getInstance().getReportParm("MEMWrist.class", print));
		this.openPrintDialog("%ROOT%\\config\\prt\\MEM\\MEMWrist.jhw",
				print, true); 
	}


	/**
	 * 判断是否为数字
	 * @param str
	 * @return
	 */
	public  boolean isNumeric(String str){ 
		Pattern pattern = Pattern.compile("[0-9]*"); 
		return pattern.matcher(str).matches();    
	} 

	/**
	 * 写入LOG信息
	 * 
	 * @return boolean
	 */
	private void writeLog(String mr_no, TParm patParm, String action) {
		String insert_sql = "INSERT INTO "
				+ "SYS_PATLOG(MR_NO, OPT_DATE, MODI_ITEM, ITEM_OLD, ITEM_NEW, OPT_USER, OPT_TERM ) "
				+ "VALUES('" + mr_no + "', SYSDATE, '#', '#', '#', '"
				+ Operator.getID() + "', '" + Operator.getIP() + "')";
		TParm parm = new TParm();

		String columns[] = {
				"PAT_NAME",
				"PY1",
				"FIRST_NAME",
				"LAST_NAME",
				"OLD_NAME", // 1-5
				"ID_TYPE",
				"IDNO",
				"SEX_CODE",
				"BIRTH_DATE",
				"NATION_CODE", // 6-10
				"NATION_CODE2",
				"MARRIAGE",
				"CELL_PHONE",
				"TEL_HOME",
				"E_MAIL", // 11-15
				"RESID_POST_CODE",
				"RESID_ADDRESS",
				"SPECIAL_DIET",
				"RELIGION",
				"POST_CODE", // 16-20
				"CURRENT_ADDRESS",
				"COMPANY_DESC",
				"CTZ1_CODE",
				"CTZ2_CODE",
				"CTZ3_CODE", // 21-25
				"BOOK_BUILD",
				"GUARDIAN1_NAME", // 26-30
				"GUARDIAN1_RELATION",
				"GUARDIAN1_PHONE"
		};

		String columnNames[] = { "姓名", "简拼", "FirstName", "FamilyName", "曾用名", // 1-5
				"证件类型代码", "证件号", "性别", "出生日期", "国籍/地区代码", // 6-10
				"民 族代码", "婚姻代码", "手 机", "座 机", "邮箱", // 11-15
				"户籍邮编", "户籍地址", "特殊饮食", "宗教代码", "现住址邮编", // 16-20
				"现住址地址", "工作单位", "身份一", "身份二", "身份三", // 21-25
				"建档录入", "紧急联系人", // 26-30
				"紧急联系人关系","紧急联系人电话"
		};

		if ("UPDATE".equals(action)) {
			for (int i = 0; i < columns.length; i++) {

				if ("BIRTH_DATE".equals(columns[i])) {
					String new_date_time = this.getValueString(columns[i]);
					String old_date_time = TypeTool.getString(patParm
							.getData(columns[i]));
					if (old_date_time != null && old_date_time.length() >= 10) {
						new_date_time = new_date_time.substring(0, 10);
						old_date_time = old_date_time.substring(0, 10);
						if (!old_date_time.equals(new_date_time)) {
							parm.addData("MODI_ITEM", columnNames[i]);
							parm.addData("ITEM_OLD", old_date_time);
							parm.addData("ITEM_NEW", new_date_time);
						} else {
							continue;
						}
					} else {
						if (new_date_time != null
								&& new_date_time.length() > 10) {
							parm.addData("MODI_ITEM", columnNames[i]);
							parm.addData("ITEM_OLD", "");
							parm.addData("ITEM_NEW",
									new_date_time.substring(0, 10));
						} else {
							continue;
						}
					}
				} else if("NATION_CODE2".equals(columns[i])) {
					String old_date = patParm.getData("SPECIES_CODE")+"";
					String new_date = this.getValueString(columns[i]);
					if(!old_date.equals(new_date)){
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", old_date);
						parm.addData("ITEM_NEW",new_date);
					}
				} else if("MARRIAGE".equals(columns[i])) {
					String old_date = patParm.getData("MARRIAGE_CODE")+"";
					String new_date = this.getValueString(columns[i]);
					if(!old_date.equals(new_date)){
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", old_date);
						parm.addData("ITEM_NEW",new_date);
					}
				} else if("RELIGION".equals(columns[i])) {
					String old_date = patParm.getData("RELIGION_CODE")+"";
					String new_date = this.getValueString(columns[i]);
					if(!old_date.equals(new_date)){
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", old_date);
						parm.addData("ITEM_NEW",new_date);
					}
				} else if("GUARDIAN1_NAME".equals(columns[i])) {
					String old_date = patParm.getData("CONTACTS_NAME")+"";
					String new_date = this.getValueString(columns[i]);
					if(!old_date.equals(new_date)){
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", old_date);
						parm.addData("ITEM_NEW",new_date);
					}
				} else if("GUARDIAN1_RELATION".equals(columns[i])) {
					String old_date = patParm.getData("RELATION_CODE")+"";
					String new_date = this.getValueString(columns[i]);
					if(!old_date.equals(new_date)){
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", old_date);
						parm.addData("ITEM_NEW",new_date);
					}
				} else if("GUARDIAN1_PHONE".equals(columns[i])) {
					String old_date = patParm.getData("CONTACTS_TEL")+"";
					String new_date = this.getValueString(columns[i]);
					if(!old_date.equals(new_date)){
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", old_date);
						parm.addData("ITEM_NEW",new_date);
					}
				} else if("BOOK_BUILD".equals(columns[i])) {
					String old_date = patParm.getData("BOOK_BUILD")+"";
					String new_date = "";
					if("01".equals(this.getValueString(columns[i]))){
						new_date = "Y";
					}else{
						new_date = "N";
					}
					if(!old_date.equals(new_date)){
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", old_date);
						parm.addData("ITEM_NEW",new_date);
					}
				}else {
					if (!this.getValueString(columns[i]).equals(
							patParm.getData(columns[i]))) {
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", patParm.getData(columns[i]));
						parm.addData("ITEM_NEW",
								this.getValueString(columns[i]));
					}
				}
			}
		} 
		parm.setData("SQL", insert_sql);
		if (parm == null || parm.getCount("MODI_ITEM") <= 0) {
			return;
		} else {
			// System.out.println("parm----"+parm);
			// 执行数据新增
			TParm result = TIOM_AppServer.executeAction(
					"action.sys.SYSWriteLogAction", "onSYSPatLog", parm);
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("LOG写入失败！");
			} else {
				// this.messageBox("LOG写入成功！");
			}
		}
	}

	/**
	 * 除了病案号和是否建档，界面上其他的锁定，不让修改
	 * add by huangtt 20150227
	 */
	public void lockName(){

		String name = "PAT_NAME;PY1;FIRST_NAME;LAST_NAME;OLD_NAME;ID_TYPE;IDNO;SEX_CODE;" +
				"BIRTH_DATE;AGE;NATION_CODE;NATION_CODE2;MARRIAGE;CELL_PHONE;TEL_HOME;E_MAIL;" +
				"RESID_POST_CODE;RESID_ADDRESS;SPECIAL_DIET;RELIGION;POST_CODE;CURRENT_ADDRESS;INSURE_INFO;" +
				"COMPANY_DESC;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;SOURCE;SCHOOL_NAME;SCHOOL_TEL;HOMEPLACE_CODE;" +
				"BIRTH_HOSPITAL;GUARDIAN1_NAME;GUARDIAN1_RELATION;GUARDIAN1_PHONE;GUARDIAN1_TEL;GUARDIAN1_COM;" +
				"GUARDIAN1_ID_TYPE;GUARDIAN1_ID_CODE;GUARDIAN1_EMAIL;GUARDIAN2_NAME;GUARDIAN2_RELATION;" +
				"GUARDIAN2_PHONE;GUARDIAN2_TEL;GUARDIAN2_COM;GUARDIAN2_ID_TYPE;GUARDIAN2_ID_CODE;GUARDIAN2_EMAIL;" +
				"MEM_TYPE;FAMILY_DOCTOR;ACCOUNT_MANAGER_CODE;START_DATE;END_DATE;BUY_MONTH_AGE;HAPPEN_MONTH_AGE;" +
				"MEM_CODE;MEM_FEE;START_DATE_TRADE;END_DATE_TRADE;INTRODUCER1;INTRODUCER2;INTRODUCER3;DESCRIPTION";
		String [] lock = name.split(";");
		for (int i = 0; i < lock.length; i++) {
			callFunction("UI|"+lock[i]+"|setEnabled", false);
		}

		callFunction("UI|save|setEnabled", true); 		// 保存按钮置灰
		callFunction("UI|query|setEnabled", true); 		// 保存按钮置灰
		callFunction("UI|EKTcard|setEnabled", true); 		// 保存按钮置灰
		callFunction("UI|idcard|setEnabled", true); 		// 保存按钮置灰
		callFunction("UI|new|setEnabled", false); 	// 购卡按钮置灰
		callFunction("UI|buycard|setEnabled", false); 	// 购卡按钮置灰
		callFunction("UI|makecard|setEnabled", false); 	// 制卡按钮置灰
		callFunction("UI|MEMprint|setEnabled", false); 	// 补印按钮置灰
		callFunction("UI|hl|setEnabled", false); 		// 历史交易按钮置灰
		callFunction("UI|Wrist|setEnabled", false);  
		callFunction("UI|crmreg|setEnabled", false);  
	}

	/**
	 * 修改出生日期
	 */
	public void onUpdateBirthDate(){
		if(!this.getValueString("MR_NO").contains("-")){
			TParm parm=new TParm();
			parm.setData("MR_NO",this.getValueString("MR_NO"));
			returnParm=(TParm) this.openDialog("%ROOT%\\config\\mem\\MEMUpdateBirthDate.x",parm);
			birthDay=returnParm.getTimestamp("BIRTH_DATE");
			this.setValue("BIRTH_DATE", birthDay);
			setBirth(false); // 计算年龄
		}else{
			String motherMrNo=this.getValueString("MR_NO").substring(0,this.getValueString("MR_NO").indexOf("-"));
			String sql="SELECT A.IPD_NO,A.CASE_NO,A.IN_DATE,B.SEX_CODE,A.DEPT_CODE,A.STATION_CODE " +
					" FROM ADM_INP A,SYS_PATINFO B WHERE A.MR_NO='"+motherMrNo+"' AND A.MR_NO=B.MR_NO ";
			TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
			TParm actionParm=new TParm();
			actionParm.setData("ADM", "CASE_NO", parm.getValue("CASE_NO",0));
			actionParm.setData("ADM", "MR_NO", this.getValueString("MR_NO"));
			actionParm.setData("ADM", "IPD_NO", parm.getData("IPD_NO",0));
			actionParm.setData("ADM", "IN_DATE", parm.getData("IN_DATE",0));
			actionParm.setData("ADM", "ADM_FLG", "N");
			actionParm.setData("ODI", "SEX_CODE", parm.getData("SEX_CODE",0));
			actionParm.setData("ODI", "DEPT_CODE", parm.getData("DEPT_CODE",0));
			actionParm.setData("ODI", "STATION_CODE", parm.getData("STATION_CODE",0));
			actionParm.setData("MEM_FLG", "Y");
			this.openDialog("%ROOT%\\config\\adm\\ADMNewBodyRegister.x",actionParm);
			this.onClear();
		}
	}

}
