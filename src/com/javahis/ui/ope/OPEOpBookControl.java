package com.javahis.ui.ope;

import com.dongyang.control.*;
import com.dongyang.data.TParm;

import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

import jdo.sys.SystemTool;

import com.dongyang.root.client.SocketLink;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TPopupMenu;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;

import java.awt.Component;

import com.dongyang.ui.TTableNode;

import jdo.adm.ADMTool;

import com.dongyang.ui.TLabel;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import jdo.sys.Operator;
import jdo.ope.OPEOpBookTool;

import com.dongyang.util.StringTool;

import java.sql.Timestamp;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import jdo.adm.ADMXMLTool;
import jdo.mro.MRORecordTool;
import jdo.clp.BscInfoTool;
import jdo.adm.ADMInpTool;

/**
 * <p>Title: 手术申请</p>
 *
 * <p>Description: 手术申请</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-24
 * @version 4.0
 */
public class OPEOpBookControl
extends TControl {
	private String MR_NO = "";//MR_NO
	private String CASE_NO = "";//CASE_NO
	private String IPD_NO = "";//住院号 住院医生站调用的时候 需要传此参数
	private String ADM_TYPE = "";//门急住别
	private String BED_NO = "";//病床号  只有住院病患有此参数
	private String BOOK_DEPT_CODE = "";//申请科室code
	private String BOOK_DR_CODE = "";//申请人员ID
	private String OPBOOK_SEQ = "";//手术申请编号
	private String SAVE_FLG = "new";//保存方式 new:新建  update:修改
	private TTable Table_BOOK_AST;
	private TTable Daily_Table ;
	private TTable OP_Table;
	private String CANCEL_FLG = "N";//取消申请标记
	private SocketLink client; // 往跑马灯传数据的Socket wanglong add 20150113
	private List<String> icdList=new ArrayList<String>();
	private List<String> opList=new ArrayList<String>();


	/**
	 * 初始化
	 */
	public void onInit() {
		Table_BOOK_AST = (TTable)this.getComponent("Table_BOOK_AST");
		Daily_Table = (TTable)this.getComponent("Daily_Table");
		OP_Table = (TTable)this.getComponent("OP_Table");
		pageInit();
		TableInit();
	}
	/**
	 * 初始化参数
	 */
	public void onParmInit() {
		Object obj = this.getParameter();
		TParm parm = new TParm();
		if (obj instanceof TParm) {
			parm = (TParm) obj;
		}
		else {
			return;
		}
		//虚拟参数 测试
		//        TParm parm = new TParm();
		//        parm.setData("MR_NO", "000000000579");
		//        parm.setData("CASE_NO", "100307000010");
		//        parm.setData("ADM_TYPE", "O");//门急住别
		//        parm.setData("BOOK_DEPT_CODE", "10101");//申请部门
		//        parm.setData("STATION_CODE","1");//诊区或者病区
		//        parm.setData("BOOK_DR_CODE", "D001");//申请人员
		//        parm.setData("ICD_CODE","A01");//主诊断
		//******************************
		//*******各个医生站调用需要的参数********
		MR_NO = parm.getValue("MR_NO");
		CASE_NO = parm.getValue("CASE_NO");
		ADM_TYPE = parm.getValue("ADM_TYPE");
		BOOK_DEPT_CODE = parm.getValue("BOOK_DEPT_CODE");
		BOOK_DR_CODE = parm.getValue("BOOK_DR_CODE");
		//********查询手术安排详细信息 需要的参数******
		if (parm.getValue("FLG").length() > 0)
			SAVE_FLG = parm.getValue("FLG"); //"update" 表示查询 点击保存时是修改原有信息
		OPBOOK_SEQ = parm.getValue("OPBOOK_SEQ");
		this.setValue("OP_DEPT_CODE",BOOK_DEPT_CODE);
		this.setValue("MAIN_SURGEON",BOOK_DR_CODE);
		//modify by liming  2012/03/15 begin
		String opDateStr = SystemTool.getInstance().getDate().toString() ;
		String  year = String.valueOf(opDateStr.substring(0,4)) ;
		String  month = String.valueOf(opDateStr.substring(5,7)) ;
		String day = String.valueOf(opDateStr.substring(8,10)) ;  
		Timestamp opeDate=StringTool.getTimestamp(year+month+day, "yyyyMMdd");
		this.setValue("OP_DATE",StringTool.rollDate(opeDate, 1) );
		//modify by liming 2012/03/15 end.

		//带入主诊断
		if(parm.getValue("ICD_CODE").length()>0){
			int index = Daily_Table.addRow();
			Daily_Table.setItem(index, 2, parm.getValue("ICD_CODE"));
			Daily_Table.setItem(index, 3, parm.getValue("ICD_CODE"));
			Daily_Table.setItem(index, 1, "Y");
		}
		TLabel tLabel_18 = (TLabel)this.getComponent("tLabel_18");
		//判断门急住别 显示诊区或者病区
		if(ADM_TYPE.equals("O")||ADM_TYPE.equals("E")){
			tLabel_18.setZhText("手术诊区");
			this.callFunction("UI|OP_STATION_CODE_I|setVisible", false);
			this.callFunction("UI|OP_STATION_CODE_O|setVisible", true);
			this.setValue("OP_STATION_CODE_O",parm.getValue("STATION_CODE"));
		}else if(ADM_TYPE.equals("I")){
			tLabel_18.setZhText("手术病区");
			this.callFunction("UI|OP_STATION_CODE_I|setVisible", true);
			this.callFunction("UI|OP_STATION_CODE_O|setVisible", false);
			this.setValue("OP_STATION_CODE_I",parm.getValue("STATION_CODE"));
		}
		initList();
	}
	private void initList() {
		// TODO Auto-generated method stub
		String op="select OPERATION_ICD from SYS_OPERATIONICD";
		TParm parm=new TParm(TJDODBTool.getInstance().select(op));
		for(int i=0;i<parm.getCount();i++){
			opList.add(parm.getValue("OPERATION_ICD", i));
		}
		String icd="select ICD_CODE from SYS_DIAGNOSIS";
		TParm icdparm=new TParm(TJDODBTool.getInstance().select(icd));
		for(int i=0;i<icdparm.getCount();i++){
			icdList.add(icdparm.getValue("ICD_CODE", i));
		}
	}
	/**
	 *手术弹出界面 OpICD
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComponentOP(Component com, int row, int column) {
		//弹出ICD10对话框的列
		if (column != 2)
			return;
		if (! (com instanceof TTextField))
			return;
		final TTextField textfield = (TTextField) com;
		textfield.onInit();
		//给table上的新text增加ICD10弹出窗口
		textfield.setPopupMenuParameter("OP_ICD",getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSOpICD.x"));
		TPopupMenu popupMenu=textfield.getPopupMenu();
		popupMenu.addPopupMenuListener(new PopupMenuListener(){
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// TODO Auto-generated method stub
				String value=textfield.getValue();
				if(!"".equals(value)&&!opList.contains(value)){
					textfield.setValue("");
				}
			}
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				// TODO Auto-generated method stub
			}});
		//给新text增加接受ICD10弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newOPOrder");
	}
	/**
	 * 取得手术ICD返回值
	 * @param tag String
	 * @param obj Object
	 */
	public void newOPOrder(String tag, Object obj) {
		TTable table = (TTable)this.callFunction("UI|OP_Table|getThis");
		//sysfee返回的数据包
		TParm parm = (TParm) obj;
		String orderCode = parm.getValue("OPERATION_ICD");
		table.setItem(table.getSelectedRow(), "OP_ICD", orderCode);    
		table.setItem(0, "MAIN_FLG", "Y");  //chenxi modufy 20130319
		if("en".equals(this.getLanguage()))
			table.setItem(table.getSelectedRow(), "OP_DESC",
					parm.getValue("OPT_ENG_DESC"));
		else
			table.setItem(table.getSelectedRow(), "OP_DESC",
					parm.getValue("OPT_CHN_DESC"));
		table.getTable().grabFocus();
		// 添加一行新数据
		if (table.getSelectedRow() == table.getRowCount() - 1) {
			table.addRow();
		}
	}
	/**
	 *诊断弹出界面 ICD10
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		//弹出ICD10对话框的列
		if (column != 2)
			return;
		if (! (com instanceof TTextField))
			return;
		final TTextField textfield = (TTextField) com;
		textfield.onInit();
		//给table上的新text增加ICD10弹出窗口
		textfield.setPopupMenuParameter("ICD10",getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSICDPopup.x"));
		TPopupMenu popupMenu=textfield.getPopupMenu();
		popupMenu.addPopupMenuListener(new PopupMenuListener(){
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// TODO Auto-generated method stub
				String value=textfield.getValue();
				if(!"".equals(value)&&!icdList.contains(value)){
					textfield.setValue("");
				}
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				// TODO Auto-generated method stub
			}});
		//给新text增加接受ICD10弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newAgentOrder");
	}
	/**
	 * 取得ICD10返回值
	 * @param tag String
	 * @param obj Object
	 */
	public void newAgentOrder(String tag, Object obj) {
		TTable table = (TTable)this.callFunction("UI|Daily_Table|getThis");
		//sysfee返回的数据包
		TParm parm = (TParm) obj;
		String orderCode = parm.getValue("ICD_CODE");
		table.setItem(table.getSelectedRow(), "DAILY_CODE", orderCode);
		if("en".equals(this.getLanguage()))
			table.setItem(table.getSelectedRow(), "DAILY_DESC", parm.getValue("ICD_ENG_DESC"));
		else
			table.setItem(table.getSelectedRow(), "DAILY_DESC", parm.getValue("ICD_CHN_DESC"));

		table.getTable().grabFocus();
		// 添加一行新数据
		if (table.getSelectedRow() == table.getRowCount() - 1) {
			table.addRow();
		}
	}
	/**
	 * 诊断Grid 值改变事件
	 * @param obj Object
	 */
	public void onDiagTableValueCharge(Object obj) {
		TTable DiagGrid = (TTable)this.getComponent("Daily_Table");
		//拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
		TTableNode node = (TTableNode) obj;
		if(node.getColumn() == 2){
			if(node.getRow()==(DiagGrid.getRowCount()-1))
				DiagGrid.addRow();
		}
	}
	/**
	 * 手术Grid 值改变事件
	 * @param obj Object
	 */
	public void onOpTableValueCharge(Object obj) {
		TTable OP_Table = (TTable)this.getComponent("OP_Table");
		//拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
		TTableNode node = (TTableNode) obj;
		if(node.getColumn() == 2){
			if(node.getRow()==(OP_Table.getRowCount()-1))
				OP_Table.addRow();
		}
	}
	/**
	 * 诊断Grid 主诊断标记修改事件
	 * @param obj Object
	 */
	public void onDiagTableMainCharge(Object obj) {
		TTable DiagGrid = (TTable)this.getComponent("Daily_Table");
		DiagGrid.acceptText();
		if(DiagGrid.getSelectedColumn()==1){
			int row = DiagGrid.getSelectedRow();
			for (int i = 0; i < DiagGrid.getRowCount(); i++) {
				DiagGrid.setItem(i, "MAIN_FLG", "N");
			}
			DiagGrid.setItem(row, "MAIN_FLG", "Y");
		}
	}
	/**
	 * 手术Grid 主诊断标记修改事件
	 * @param obj Object
	 */
	public void onOpTableMainCharge(Object obj) {
		TTable OP_Table = (TTable)this.getComponent("OP_Table");
		OP_Table.acceptText();
		if(OP_Table.getSelectedColumn()==1){
			int row = OP_Table.getSelectedRow();
			for (int i = 0; i < OP_Table.getRowCount(); i++) {
				OP_Table.setItem(i, "MAIN_FLG", "N");
			}
			OP_Table.setItem(row, "MAIN_FLG", "Y");
		}
	}

	/**
	 * 页面初始化
	 */
	public void pageInit(){
		onParmInit();//初始化参数
		if("new".equals(SAVE_FLG)){
			setDataForDoctor();
		}else if("update".equals(SAVE_FLG)){
			queryDataByOPBOOK_SEQ();
		}
	}
	/**
	 * 各个医生站调用时 初始化页面
	 */
	private void setDataForDoctor(){
		if(MR_NO.length()<=0){
			return;
		}
		//病患基本信息
		Pat pat = Pat.onQueryByMrNo(MR_NO);
		this.setValue("MR_NO",pat.getMrNo());//病案号
		this.setValue("SEX",pat.getSexCode());//性别
		this.setValue("ADM_TYPE",ADM_TYPE);//患者来源


		//add by yangjj 20151106
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String age = "0";
		age =DateUtil.showAge(pat.getBirthday(), sysDate);


		if("en".equals(this.getLanguage())){
			this.setValue("PAT_NAME",pat.getName1());//姓名
			//计算年龄
			String[] res = StringTool.CountAgeByTimestamp(pat.getBirthday(),SystemTool.getInstance().getDate());
			this.setValue("AGE",age+"Y");
		}else{
			this.setValue("PAT_NAME",pat.getName());//姓名


			//modify by yangjj 20151106
			this.setValue("AGE",age);//岁数




			//this.setValue("AGE",
			//        StringUtil.getInstance().showAge(pat.getBirthday(),
			//SystemTool.getInstance().getDate()));//岁数

		}
		this.setValue("BOOK_DEPT_CODE", BOOK_DEPT_CODE);
		this.setValue("BOOK_DR_CODE",BOOK_DR_CODE);
		//判断是否是住院医生站调用
		if("I".equals(ADM_TYPE)){
			//查询病患住院信息
			TParm admParm = new TParm();
			admParm.setData("CASE_NO",CASE_NO);
			TParm admData = ADMTool.getInstance().getADM_INFO(admParm);
			IPD_NO = admData.getValue("IPD_NO",0);//记录住院号
			BED_NO = admData.getValue("BED_NO",0);//记录床位号
			this.setValue("BED_NO",BED_NO);
			TParm parm = new TParm();
			parm.setData("CASE_NO",CASE_NO);
			parm.setData("MAIN_FLG","Y");//主诊断
			parm.setData("IO_TYPE","M");//入院诊断
			//查询病患入院诊断
			TParm adm_daily = ADMTool.getInstance().queryDailyData(parm);
			if(adm_daily.getCount()>0){
				//将诊断数据转换成Grid能够识别的格式
				TParm dailyData = new TParm();
				dailyData.addData("DEL", "N");
				dailyData.addData("MAIN_FLG", "Y");
				dailyData.addData("DAILY_CODE",
						adm_daily.getValue("ICD_CODE", 0));
				dailyData.addData("DAILY_DESC",
						adm_daily.getValue("ICD_CODE", 0));
				TTable Daily_Table = (TTable)this.getComponent("Daily_Table");
				Daily_Table.setParmValue(dailyData);
			}
		}
		initOPBookTable(MR_NO);
	}
	/**
	 * 根据MR_NO 查询所有手术申请信息
	 * @param MR_NO String
	 */
	private void initOPBookTable(String MR_NO){
		TTable OPBookTable = (TTable)this.getComponent("OPBookTable");
		TParm parm = new TParm();
		parm.setData("MR_NO",MR_NO);
		TParm result = OPEOpBookTool.getInstance().selectOpBook(parm);
		OPBookTable.setParmValue(result);
	}
	/**
	 * Table初始化监听
	 */
	public void TableInit(){
		//手术Table 监听
		OP_Table = (TTable)this.getComponent("OP_Table");
		OP_Table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,this,"onCreateEditComponentOP");
		//主手术改变事件
		OP_Table.addEventListener(TTableEvent.CHECK_BOX_CLICKED,this,"onOpTableMainCharge");
		OP_Table.addRow();
		OpList opList = new OpList();
		OP_Table.addItem("OpList",opList);
		//诊断Table监听
		Daily_Table = (TTable)this.getComponent("Daily_Table");
		Daily_Table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,this,"onCreateEditComponent");
		//主诊断改变事件
		Daily_Table.addEventListener(TTableEvent.CHECK_BOX_CLICKED,this,"onDiagTableMainCharge");
		Daily_Table.addRow();
		OrderList orderList = new OrderList();
		Daily_Table.addItem("OrderList",orderList);
		//        //诊断Grid值改变事件
		//        this.addEventListener("Daily_Table->" + TTableEvent.CHANGE_VALUE,
		//                              "onDiagTableValueCharge");

		//        //手术Grid值改变事件
		//        this.addEventListener("OP_Table->" + TTableEvent.CHANGE_VALUE,
		//                              "onOpTableValueCharge");
	}
	/**
	 * 助手增加按钮事件
	 */
	public void onAddASTTable(){
		String userid = this.getValueString("BOOK_AST");//获得助手user_id
		if(userid.length()>0){
			TTable table = (TTable)this.getComponent("Table_BOOK_AST");
			int rowIndex = table.addRow();
			table.setValueAt("N",rowIndex,0);
			table.setValueAt(userid,rowIndex,1);
			table.setValueAt(userid,rowIndex,2);
		}
	}
	/**
	 * 保存
	 */
	public void onSave(){
		delTable();
		if(!checkData())
			return;
		if(SAVE_FLG.equals("new")){
			insert();
		}else if(SAVE_FLG.equals("update")){
			update();
		}
	}
	/**
	 * 新建手术申请
	 */
	private void insert(){

		OPBOOK_SEQ = SystemTool.getInstance().getNo("ALL", "OPE", "OPBOOK_SEQ",
				"OPBOOK_SEQ"); //调用取号原则
		TParm insert = getSaveData();
		//新的手术状态  0,申请；1,排程完毕；2,接患者；3,手术室交接；4,手术等待；5,手术开始；6,关胸；7,手术结束；8,返回病房
		insert.setData("STATE","0");//预约状态 0 申请， 1 排程完毕 ，2手术完成
		//===============pangben modify 20110630 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			insert.setData("REGION_CODE", Operator.getRegion());
		}
		//=============pangben modify 20110630 stop


		String oldDiagCode = "";
		//MAIN_FLG;OP_ICD
		for (int i = 0; i < OP_Table.getDataStore().rowCount(); i++) {
			if (!OP_Table.getDataStore().isActive(i)) {
				continue;
			}
			if ("Y".equals(OP_Table.getItemString(i, "MAIN_FLG")))
				oldDiagCode = OP_Table.getItemString(i, "OP_ICD");
		}
		//        System.out.println("最初手术ICD"+oldDiagCode);
		String caseNo = CASE_NO;
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm clpPathParm = ADMInpTool.getInstance().selectall(parm);
		//        System.out.println("临床路径信息"+clpPathParm);
		TParm inBscParm = new TParm();
		inBscParm.setData("CLNCPATH_CODE",
				clpPathParm.getValue("CLNCPATH_CODE", 0));
		//临床路径是否存在诊断
		TParm bscParm = BscInfoTool.getInstance().existBscinfo(inBscParm);
		parm.setData("CASE_NO", caseNo);
		if (clpPathParm.getData("CLNCPATH_CODE", 0) != null &&
				clpPathParm.getValue("CLNCPATH_CODE", 0).length() > 0) {
			//            System.out.println("1111111111");
			if (oldDiagCode.length() > 0) {
				//                System.out.println("手术诊断" + oldDiagCode);
				//IO_TYPE;MAINDIAG_FLG
				if (!oldDiagCode.equals(bscParm.getValue("ICD_CODE", 0))) {
					this.messageBox("手术诊断临床路径溢出");
				}
			}
		}else{
			//            System.out.println("手术ICD"+oldDiagCode);
			if (oldDiagCode.length() > 0) {
				TParm inBscParmNew =new TParm();
				inBscParmNew.setData("OPE_ICD_CODE",oldDiagCode);
				TParm bscParmNew = BscInfoTool.getInstance().existBscinfo(inBscParmNew);
				if(bscParmNew.getData("CLNCPATH_CODE")!=null&&bscParmNew.getValue("CLNCPATH_CODE",0).length()>0)
					this.messageBox("建议进入"+bscParmNew.getValue("CLNCPATH_CODE",0)+"临床路径");

			}
		}


		TParm result = OPEOpBookTool.getInstance().insertOpBook(insert);
		if(result.getErrCode()<0){
			this.messageBox("E0005");
			return;
		}
		this.messageBox("P0005");
		onSendLEDUIMessages(insert);//wanglong add 20150113
		SAVE_FLG = "update";
		initOPBookTable(MR_NO);
		
		/*modified by Eric 20170523 保存完刷新Daily_Table*/
		updateTable();
		
		//生成信息看板XML
		ADMXMLTool.getInstance().creatXMLFile(CASE_NO);
	}
	/**
	 * 修改手术信息
	 */
	private void update(){
		TParm updateData = getSaveData();
		TParm result = OPEOpBookTool.getInstance().updateOpBook(updateData);
		if(result.getErrCode()<0){
			this.messageBox("E0005");
			return;
		}
		this.messageBox("P0005");
		initOPBookTable(MR_NO);
		
		/*modified by Eric 20170523 保存完刷新Daily_Table*/
		updateTable();
		
		//生成信息看板XML
		ADMXMLTool.getInstance().creatXMLFile(CASE_NO);
	}
	/**
	 * 获取保存信息
	 * @return TParm
	 */
	private TParm getSaveData(){
		TParm parm = new TParm();
		parm.setData("OPBOOK_SEQ",OPBOOK_SEQ);//手术申请编号
		parm.setData("ADM_TYPE",this.getValueString("ADM_TYPE"));
		parm.setData("MR_NO",MR_NO);
		parm.setData("IPD_NO",IPD_NO);
		parm.setData("CASE_NO",CASE_NO);
		parm.setData("BED_NO",BED_NO);
		parm.setData("URGBLADE_FLG",this.getValueString("URGBLADE_FLG"));//急做手术标记
		parm.setData("OP_DATE",StringTool.getString((Timestamp)this.getValue("OP_DATE"),"yyyyMMddHHmmss"));//手术时间
		parm.setData("TF_FLG",this.getValueString("TF_FLG"));//连台标记
		parm.setData("TIME_NEED",this.getValueString("TIME_NEED"));//所需时间
		parm.setData("ROOM_NO",this.getValueString("ROOM_NO"));//手术间
		parm.setData("TYPE_CODE",this.getValueString("TYPE_CODE"));//手术类型
		parm.setData("PART_CODE",this.getValueString("PART_CODE"));//手术部位  add by wanglong 20121206
		parm.setData("ISO_FLG",this.getValueString("ISO_FLG"));//隔离手术标记 add by wanglong 20121206

		//add by yangjj 20150528
		parm.setData("STERILE_FLG",this.getValueString("STERILE_FLG"));
		parm.setData("MIRROR_FLG",this.getValueString("MIRROR_FLG"));

		parm.setData("ANA_CODE",this.getValueString("ANA_CODE"));//麻醉方式
		parm.setData("OP_DEPT_CODE",this.getValueString("OP_DEPT_CODE"));//手术科室
		if("I".equals(ADM_TYPE))
			parm.setData("OP_STATION_CODE",this.getValueString("OP_STATION_CODE_I"));//手术病区
		else if("O".equals(ADM_TYPE)||"E".equals(ADM_TYPE))
			parm.setData("OP_STATION_CODE",this.getValueString("OP_STATION_CODE_O"));//手术诊区
		TParm Daily_Data = this.getDailyData();//获取诊断信息
		parm.setData("DIAG_CODE1",Daily_Data.getValue("DIAG_CODE1"));
		parm.setData("DIAG_CODE2",Daily_Data.getValue("DIAG_CODE2"));
		parm.setData("DIAG_CODE3",Daily_Data.getValue("DIAG_CODE3"));
		parm.setData("BOOK_DEPT_CODE",this.getValue("BOOK_DEPT_CODE"));//预约部门
		TParm Op_Data = this.getOpData();//获取手术信息
		parm.setData("OP_CODE1",Op_Data.getValue("OP_CODE1"));
		parm.setData("OP_CODE2",Op_Data.getValue("OP_CODE2"));
		parm.setData("BOOK_DR_CODE",this.getValue("BOOK_DR_CODE"));//预约医师
		parm.setData("MAIN_SURGEON",this.getValue("MAIN_SURGEON"));//主刀医师
		TParm BOOK_AST = this.getASTData();//助手信息
		parm.setData("BOOK_AST_1",BOOK_AST.getValue("BOOK_AST_1"));
		parm.setData("BOOK_AST_2",BOOK_AST.getValue("BOOK_AST_2"));
		parm.setData("BOOK_AST_3",BOOK_AST.getValue("BOOK_AST_3"));
		parm.setData("BOOK_AST_4",BOOK_AST.getValue("BOOK_AST_4"));
		parm.setData("REMARK",this.getValueString("REMARK"));
		parm.setData("OPERATION_REMARK",this.getValueString("OPERATION_REMARK"));//手术室备注  add by huangjw 20140904
		parm.setData("OPT_USER",Operator.getID());
		parm.setData("OPT_TERM",Operator.getIP());
		return parm;
	}
	/**
	 * 选择科常用手术
	 */
	public void onDeptOp(){
		String dept_code = this.getValueString("OP_DEPT_CODE");
		if(dept_code.length()<=0){
			this.messageBox("E0077");
			return;
		}
		String op_icd = (String)this.openDialog("%ROOT%/config/ope/OPEDeptOpShow.x",dept_code);
		TTable Op_Table = (TTable)this.getComponent("OP_Table");
		//将回传值 显示在表格上
		Op_Table.setValueAt_(op_icd,Op_Table.getRowCount()-1,2);
		Op_Table.setValueAt_(op_icd,Op_Table.getRowCount()-1,3);
		if(op_icd.length()>0)
			Op_Table.addRow();
	}
	/**
	 * 获取诊断数据
	 */
	private TParm getDailyData(){
		TTable Daily_Table = (TTable)this.getComponent("Daily_Table");
		TParm parm = new TParm();
		int index = 2;//诊断数 以2作为开始值 因为主诊断是1
		for(int i=0;i<Daily_Table.getRowCount();i++){
			// modified by WangQing 20170516
			if(Daily_Table.getValueAt(i,2).toString().trim().length()>0 && Daily_Table.getValueAt(i,3).toString().trim().length()>0){
				//判断主诊断
				if("Y".equals(Daily_Table.getValueAt(i,1).toString())){
					parm.setData("DIAG_CODE1",Daily_Table.getValueAt(i,2));
				}else{
					parm.setData("DIAG_CODE"+index,Daily_Table.getValueAt(i,2));
					index++;
				}
			}
		}
		return parm;
	}
	/**
	 * 获取手术术式数据
	 * @return TParm
	 */
	private TParm getOpData(){
		TTable Op_Table = (TTable)this.getComponent("OP_Table");
		TParm parm = new TParm();
		for(int i=0;i<Op_Table.getRowCount();i++){
			/*modified by Eric 20170523*/
			if(Op_Table.getValueAt(i,2).toString().trim().length()>0 && Op_Table.getValueAt(i,3).toString().trim().length()>0){
				//判断主诊断
				if("Y".equals(Op_Table.getValueAt(i,1))){
					parm.setData("OP_CODE1",Op_Table.getValueAt(i,2));
				}else{
					parm.setData("OP_CODE2",Op_Table.getValueAt(i,2));
				}
			}
		}
		return parm;
	}
	/**
	 * 获取助手数据
	 * @return TParm
	 */
	private TParm getASTData(){
		TTable AST_Table = (TTable)this.getComponent("Table_BOOK_AST");
		TParm parm = new TParm();
		for(int i=0;i<AST_Table.getRowCount();i++){
			if(AST_Table.getValueAt(i,1).toString().trim().length()>0){
				parm.setData("BOOK_AST_"+(i+1),AST_Table.getValueAt(i,1));
			}
		}
		return parm;
	}
	/**
	 * 删除诊断和手术表格的信息（勾选删除标记的）
	 */
	private void delTable(){
		TTable opTable = (TTable)this.getComponent("OP_Table");
		opTable.acceptText();
		TTable dailyTable = (TTable)this.getComponent("Daily_Table");
		dailyTable.acceptText();
		TTable ASTTable = (TTable)this.getComponent("Table_BOOK_AST");
		ASTTable.acceptText();
		for(int i=opTable.getRowCount()-1;i>=0;i--){
			if("Y".equals(opTable.getValueAt(i,0))){
				opTable.removeRow(i);
			}
		}
		for(int i=dailyTable.getRowCount()-1;i>=0;i--){
			if("Y".equals(dailyTable.getValueAt(i,0))){
				dailyTable.removeRow(i);
			}
		}
		for(int i=ASTTable.getRowCount()-1;i>=0;i--){
			if("Y".equals(ASTTable.getValueAt(i,0))){
				ASTTable.removeRow(i);
			}
		}
	}
	/**
	 * 检查数据
	 */
	private boolean checkData(){
		if("Y".equals(CANCEL_FLG)){
			this.messageBox("E0089");
			return false;
		}
		TTable opTable = (TTable)this.getComponent("OP_Table");
		opTable.acceptText();
		TTable dailyTable = (TTable)this.getComponent("Daily_Table");
		dailyTable.acceptText();
		boolean flg = false;//主诊断标识 true:存在主诊断（主手术） false:不存在主诊断(主手术)
		for(int i=0;i<opTable.getRowCount();i++){
		    /*modified by Eric 20170523*/
			if("Y".equals(opTable.getValueAt(i,1)) && opTable.getValueAt(i,2).toString().trim().length()>0 && opTable.getValueAt(i,3).toString().trim().length()>0){
				flg = true;
			}
		}
		if(!flg){
			this.messageBox("E0078");
			return flg;
		}
		/*modified by Eric 20170523*/
		flg = false;
		for(int i=0;i<dailyTable.getRowCount();i++){
			/*modified by Eric 20170523*/
			if("Y".equals(dailyTable.getValueAt(i,1)) && dailyTable.getValueAt(i,2).toString().trim().length()>0 && dailyTable.getValueAt(i,3).toString().trim().length()>0){
				flg = true;
			}
		}
		if(!flg){
			this.messageBox("E0079");
			return false;
		}
		if(this.getValue("OP_DATE")==null||this.getValueString("OP_DATE").equals("")){
			this.messageBox("E0076");
			this.grabFocus("OP_DATE");
			return false;
		}
		//modify shibaoliu 20120317
		//        if(this.getValue("TIME_NEED")==null||this.getValueString("TIME_NEED").equals("")){
		//            this.messageBox("E0090");
		//            this.grabFocus("TIME_NEED");
		//            return false;
		//        }
		if(this.getValue("OP_DEPT_CODE")==null||this.getValueString("OP_DEPT_CODE").equals("")){
			this.messageBox("E0077");
			this.grabFocus("OP_DEPT_CODE");
			return false;
		}
		if(ADM_TYPE.equals("I")){
			if (this.getValue("OP_STATION_CODE_I") == null ||
					this.getValueString("OP_STATION_CODE_I").equals("")) {
				this.messageBox("E0091");
				this.grabFocus("OP_STATION_CODE_I");
				return false;
			}
		}
		if(this.getValue("MAIN_SURGEON")==null||this.getValueString("MAIN_SURGEON").equals("")){
			this.messageBox("E0092");
			this.grabFocus("MAIN_SURGEON");
			return false;
		}
		if(this.getValue("TYPE_CODE")==null||this.getValueString("TYPE_CODE").equals("")){//wanglong add 20141011
			this.messageBox("请选择手术类型");
			this.grabFocus("TYPE_CODE");
			return false;
		}
		return flg;
	}
	/**
	 * 根据OPBOOK_SEQ查询某一次手术申请的信息
	 */
	private void queryDataByOPBOOK_SEQ(){
		TParm parm = new TParm();
		parm.setData("OPBOOK_SEQ",OPBOOK_SEQ);
		TParm result = OPEOpBookTool.getInstance().selectOpBook(parm);
		if(result.getErrCode()<0){
			return;
		}
		this.setData(result);
	}
	/**
	 * 给页面控件赋值
	 */
	private void setData(TParm parm){
		MR_NO = parm.getValue("MR_NO",0);
		CASE_NO = parm.getValue("CASE_NO",0);
		IPD_NO = parm.getValue("IPD_NO",0);
		BED_NO = parm.getValue("BED_NO",0);
		CANCEL_FLG = parm.getValue("CANCEL_FLG",0);
		ADM_TYPE = parm.getValue("ADM_TYPE",0);
		if(MR_NO.length()<=0){
			return;
		}
		//病患基本信息
		Pat pat = Pat.onQueryByMrNo(MR_NO);
		this.setValue("MR_NO",pat.getMrNo());//病案号
		this.setValue("SEX",pat.getSexCode());//性别

		//add by yangjj 20151106
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String age = "0";
		age =DateUtil.showAge(pat.getBirthday(), sysDate);



		if("en".equals(this.getLanguage())){
			this.setValue("PAT_NAME",pat.getName1());//姓名
			//计算年龄
			String[] res = StringTool.CountAgeByTimestamp(pat.getBirthday(),SystemTool.getInstance().getDate());
			//modify by yangjj 20151218
			//this.setValue("AGE",res[0]+"Y");
			this.setValue("AGE",age+"Y");
		}else{
			this.setValue("PAT_NAME",pat.getName());//姓名
			//modify by yangjj 20151218
			/*this.setValue("AGE",
                      StringUtil.getInstance().showAge(pat.getBirthday(),
            SystemTool.getInstance().getDate()));//岁数
			 */

			this.setValue("AGE",age);//岁数

		}
		this.setValue("ADM_TYPE",parm.getValue("ADM_TYPE",0));
		this.setValue("BED_NO",parm.getValue("BED_NO",0));
		this.setValue("OP_DATE",parm.getTimestamp("OP_DATE",0));
		this.setValue("TYPE_CODE",parm.getValue("TYPE_CODE",0));
		this.setValue("ROOM_NO",parm.getValue("ROOM_NO",0));
		this.setValue("BOOK_DEPT_CODE",parm.getValue("BOOK_DEPT_CODE",0));
		this.setValue("BOOK_DR_CODE",parm.getValue("BOOK_DR_CODE",0));
		this.setValue("TIME_NEED",parm.getValue("TIME_NEED",0));
		this.setValue("TF_FLG",parm.getValue("TF_FLG",0));
		this.setValue("URGBLADE_FLG",parm.getValue("URGBLADE_FLG",0));
		this.setValue("PART_CODE",parm.getValue("PART_CODE",0));//手术部位  add by wanglong 20121206
		this.setValue("ISO_FLG",parm.getValue("ISO_FLG",0));//隔离手术标记 add by wanglong 20121206

		//add by yangjj 20150528
		this.setValue("MIRROR_FLG",parm.getValue("MIRROR_FLG",0));
		this.setValue("STERILE_FLG",parm.getValue("STERILE_FLG",0));

		this.setValue("OP_DEPT_CODE",parm.getValue("OP_DEPT_CODE",0));
		if(ADM_TYPE.equals("I"))
			this.setValue("OP_STATION_CODE_I",parm.getValue("OP_STATION_CODE",0));
		else if(ADM_TYPE.equals("O")||ADM_TYPE.equals("E"))
			this.setValue("OP_STATION_CODE_O",parm.getValue("OP_STATION_CODE",0));
		this.setValue("MAIN_SURGEON",parm.getValue("MAIN_SURGEON",0));
		this.setValue("ANA_CODE",parm.getValue("ANA_CODE",0));
		this.setValue("REMARK",parm.getValue("REMARK",0));
		this.setValue("OPERATION_REMARK", parm.getValue("OPERATION_REMARK",0));//手术室备注 add by huangjw 20140904
		TTable Table_BOOK_AST = (TTable)this.getComponent("Table_BOOK_AST");
		if(parm.getValue("BOOK_AST_1",0).length()>0){
			int index = Table_BOOK_AST.addRow();
			Table_BOOK_AST.setItem(index,1,parm.getValue("BOOK_AST_1",0));
			Table_BOOK_AST.setItem(index,2,parm.getValue("BOOK_AST_1",0));
		}
		if(parm.getValue("BOOK_AST_2",0).length()>0){
			int index = Table_BOOK_AST.addRow();
			Table_BOOK_AST.setItem(index,1,parm.getValue("BOOK_AST_2",0));
			Table_BOOK_AST.setItem(index,2,parm.getValue("BOOK_AST_2",0));
		}
		if(parm.getValue("BOOK_AST_3",0).length()>0){
			int index = Table_BOOK_AST.addRow();
			Table_BOOK_AST.setItem(index,1,parm.getValue("BOOK_AST_3",0));
			Table_BOOK_AST.setItem(index,2,parm.getValue("BOOK_AST_3",0));
		}
		if(parm.getValue("BOOK_AST_4",0).length()>0){
			int index = Table_BOOK_AST.addRow();
			Table_BOOK_AST.setItem(index,1,parm.getValue("BOOK_AST_4",0));
			Table_BOOK_AST.setItem(index,2,parm.getValue("BOOK_AST_4",0));
		}
		Daily_Table = (TTable)this.getComponent("Daily_Table");
		if(parm.getValue("DIAG_CODE1",0).length()>0){
			int index = Daily_Table.addRow();
			Daily_Table.setItem(index,2,parm.getValue("DIAG_CODE1",0));
			Daily_Table.setItem(index,3,parm.getValue("DIAG_CODE1",0));
			Daily_Table.setItem(index,1,"Y");
		}
		if(parm.getValue("DIAG_CODE2",0).length()>0){
			int index = Daily_Table.addRow();
			Daily_Table.setItem(index, 2, parm.getValue("DIAG_CODE2", 0));
			Daily_Table.setItem(index, 3, parm.getValue("DIAG_CODE2", 0));
		}
		if(parm.getValue("DIAG_CODE3",0).length()>0){
			int index = Daily_Table.addRow();
			Daily_Table.setItem(index, 2, parm.getValue("DIAG_CODE3", 0));
			Daily_Table.setItem(index, 3, parm.getValue("DIAG_CODE3", 0));
		}
		OP_Table = (TTable)this.getComponent("OP_Table");
		if(parm.getValue("OP_CODE1",0).length()>0){
			int index = OP_Table.addRow();
			OP_Table.setItem(index,2,parm.getValue("OP_CODE1",0));
			OP_Table.setItem(index,3,parm.getValue("OP_CODE1",0));
			OP_Table.setItem(index,1,"Y");
		}
		if(parm.getValue("OP_CODE2",0).length()>0){
			int index = OP_Table.addRow();
			OP_Table.setItem(index,2,parm.getValue("OP_CODE2",0));
			OP_Table.setItem(index,3,parm.getValue("OP_CODE2",0));
		}
		initOPBookTable(MR_NO);
	}
	/**
	 * 诊断CODE替换中文 模糊查询（内部类）
	 */
	public class OrderList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER :
				dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("ICD_CODE");
			Vector d = (Vector) parm.getData("ICD_CHN_DESC");
			Vector e = (Vector) parm.getData("ICD_ENG_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i))){
					if ("en".equals(OPEOpBookControl.this.getLanguage())) {
						return "" + e.get(i);
					}
					else {
						return "" + d.get(i);
					}
				}
			}
			return s;
		}
	}

	/**
	 * 手术CODE替换中文 模糊查询（内部类）
	 */
	public class OpList extends TLabel {
		TDataStore dataStore = new TDataStore();
		public OpList(){
			dataStore.setSQL("select * from SYS_OPERATIONICD");
			dataStore.retrieve();
		}
		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER :
				dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("OPERATION_ICD");
			Vector d = (Vector) parm.getData("OPT_CHN_DESC");
			Vector e = (Vector) parm.getData("OPT_ENG_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i))){
					if ("en".equals(OPEOpBookControl.this.getLanguage())) {
						return "" + e.get(i);
					}
					else {
						return "" + d.get(i);
					}
				}
			}
			return s;
		}
	}
	/**
	 * 手术申请列表
	 */
	public void onOPBookTableClick(){
		TTable OPBookTable = (TTable)this.getComponent("OPBookTable");
		int row = OPBookTable.getSelectedRow();
		if(row>-1){
			int re = this.messageBox("提示","E0093",0);
			if(re==0){
				OPBOOK_SEQ = OPBookTable.getValueAt(row,0).toString();
				SAVE_FLG = "update";
				this.clearValue("OP_DATE;TYPE_CODE;ROOM_NO;TIME_NEED;TF_FLG;URGBLADE_FLG;OP_DEPT_CODE;OP_STATION_CODE_I;OP_STATION_CODE_O");
				this.clearValue("MAIN_SURGEON;ANA_CODE;BOOK_AST;REMARK;PART_CODE;ISO_FLG;MIRROR_FLG;STERILE_FLG");//modify by wanglong 20121213
				TableClear();
				queryDataByOPBOOK_SEQ();
				Daily_Table.addRow();
				OP_Table.addRow();
			}
		}
	}
	/**
	 * 清空
	 */
	public void onClear(){
		this.clearValue("OP_DATE;TYPE_CODE;ROOM_NO;TIME_NEED;TF_FLG;URGBLADE_FLG;OP_DEPT_CODE;OP_STATION_CODE_I;OP_STATION_CODE_O");
		this.clearValue("MAIN_SURGEON;ANA_CODE;BOOK_AST;REMARK;PART_CODE;ISO_FLG;MIRROR_FLG;STERILE_FLG");//modify by wanglong 20121213
		onSelectOpType();// wanglong add 20140929
		Table_BOOK_AST = (TTable)this.getComponent("Table_BOOK_AST");
		Table_BOOK_AST.removeRowAll();
		Daily_Table = (TTable)this.getComponent("Daily_Table");
		Daily_Table.removeRowAll();
		Daily_Table.addRow();
		OP_Table = (TTable)this.getComponent("OP_Table");
		OP_Table.removeRowAll();
		OP_Table.addRow();
		CANCEL_FLG = "N";
		SAVE_FLG = "new";
	}
	/**
	 * 清空表格
	 */
	private void TableClear(){
		Table_BOOK_AST = (TTable)this.getComponent("Table_BOOK_AST");
		Table_BOOK_AST.removeRowAll();
		Daily_Table = (TTable)this.getComponent("Daily_Table");
		Daily_Table.removeRowAll();
		OP_Table = (TTable)this.getComponent("OP_Table");
		OP_Table.removeRowAll();
	}
	/**
	 * 取消申请
	 */
	public void onCancel(){
		if(OPBOOK_SEQ.length()<=0){
			this.messageBox("E0094");
			return;
		}
		if("Y".equals(CANCEL_FLG)){
			this.messageBox("E0095");
			return;
		}
		TParm re = OPEOpBookTool.getInstance().cancelOpBook(OPBOOK_SEQ);
		if(re.getErrCode()<0){
			this.messageBox("E0005");
			return;
		}
		this.messageBox("P0005");
		initOPBookTable(MR_NO);
		OPBOOK_SEQ = "";
		this.onClear();
	}
	/**
	 * 调用病患信息界面
	 */
	public void onPatInfo() {
		TParm parm = new TParm();
		parm.setData("OPE", "OPE");
		parm.setData("MR_NO", this.getValueString("MR_NO").trim());
		this.openDialog("%ROOT%\\config\\sys\\SYSPatInfo.x",
				parm);
	}
	/**
	 * 科室选择事件
	 */
	public void onOP_DEPT_CODE(){
		this.clearValue("OP_STATION_CODE_I;MAIN_SURGEON");
	}
	/**
	 * 备血申请
	 */
	public void onBlood(){
		if("new".equals(SAVE_FLG)){
			this.messageBox("E0096");
			return;
		}
		TParm Daily_Data = this.getDailyData();//获取诊断信息  第一条是主诊断
		if(Daily_Data.getValue("DIAG_CODE1").length()<=0){
			this.messageBox("E0097");
			return;
		}
		if(this.getValue("OP_DATE")==null){
			this.messageBox("E0076");
			this.grabFocus("OP_DATE");
			return;
		}
		OrderList order = new OrderList();
		TParm parm = new TParm();
		parm.setData("ADM_TYPE",ADM_TYPE);
		parm.setData("MR_NO",MR_NO);
		parm.setData("CASE_NO",CASE_NO);
		parm.setData("DEPT_CODE",BOOK_DEPT_CODE);
		parm.setData("DR_CODE",BOOK_DR_CODE);
		parm.setData("ICD_CODE",Daily_Data.getValue("DIAG_CODE1"));
		parm.setData("ICD_DESC",order.getTableShowValue(Daily_Data.getValue("DIAG_CODE1")));
		parm.setData("USE_DATE",this.getValue("OP_DATE"));
		this.openDialog("%ROOT%\\config\\bms\\BMSApplyNo.x",parm);
	}
	/**
	 * 手术记录
	 */
	public void onDetail(){
		if(OPBOOK_SEQ.length()<=0){
			return;
		}
		TParm parm = new TParm();
		parm.setData("OPBOOK_SEQ",OPBOOK_SEQ);
		parm.setData("MR_NO",MR_NO);
		parm.setData("ADM_TYPE",ADM_TYPE);
		this.openDialog("%ROOT%/config/ope/OPEOpDetail.x",parm);
	}

	/**
	 * “手术类型”与“手术间”联动
	 */
	public void onSelectOpType() {// wanglong add 20140929
		String typeCode = this.getValueString("TYPE_CODE");
		TTextFormat roomNo = (TTextFormat) this.getComponent("ROOM_NO");
		String sql =
				"SELECT B.ID,B.CHN_DESC AS NAME,B.PY1 FROM OPE_IPROOM A,SYS_DICTIONARY B "
						+ " WHERE B.GROUP_ID='OPE_OPROOM' AND A.ROOM_NO=B.ID # ORDER BY B.SEQ,B.ID";
		if (!StringUtil.isNullString(typeCode)) {
			sql = sql.replaceFirst("#", " AND A.TYPE_CODE = '" + typeCode + "' ");
			this.setValue("ROOM_NO", "");
		} else {
			sql =
					"SELECT ID,CHN_DESC AS NAME,PY1 FROM SYS_DICTIONARY WHERE GROUP_ID='OPE_OPROOM' ORDER BY SEQ,ID";
		}
		TParm roomParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (roomParm.getErrCode() < 0) {
			this.messageBox_("取得术间信息失败");
			return;
		}
		roomNo.setPopupMenuData(roomParm);
		roomNo.setComboSelectRow();
		roomNo.popupMenuShowData();
	}

	/**
	 * 向手术排程跑马灯发送消息
	 * 
	 * @author wanglong 20150113
	 */
	private void onSendLEDUIMessages(TParm parm) {
		TParm patInfo = PatTool.getInstance().getInfoForMrno(MR_NO);
		String opDesc = StringUtil.getDesc("SYS_OPERATIONICD", "OPT_CHN_DESC", "OPERATION_ICD='" + parm.getValue("OP_CODE1") + "'");
		client = SocketLink.running("", "ASG", "OPE");
		if (client.isClose()) {
			out(client.getErrText());
			return;
		}
		client.sendMessage("ASG", "MR_NO:" + MR_NO + "|PAT_NAME:" + patInfo.getValue("PAT_NAME", 0)
		+ "|OPBOOK_SEQ:" + OPBOOK_SEQ + "|OP_DESC:" + opDesc);
		if (client == null) {
			return;
		}
		client.close();
	}

	public void updateTable(){
		this.clearValue("OP_DATE;TYPE_CODE;ROOM_NO;TIME_NEED;TF_FLG;URGBLADE_FLG;OP_DEPT_CODE;OP_STATION_CODE_I;OP_STATION_CODE_O");
		this.clearValue("MAIN_SURGEON;ANA_CODE;BOOK_AST;REMARK;PART_CODE;ISO_FLG;MIRROR_FLG;STERILE_FLG");//modify by wanglong 20121213
		TableClear();
		queryDataByOPBOOK_SEQ();
		Daily_Table.addRow();
		OP_Table.addRow();
	}
}
