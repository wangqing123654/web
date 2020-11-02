package com.javahis.ui.bil;

import java.util.Date;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import javax.swing.JOptionPane;

import org.apache.commons.lang.time.DateUtils;

import jdo.bil.BILFinanceTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.ctc.wstx.util.DataUtil;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.DateUtil;
import com.javahis.util.ExportExcelUtil;

/**
 *
 * <p>Title: 财务收入报表</p>
 *
 * <p><b>Description:</b>
 * <br>给用友发送数据
 * </p>
 *
 * <p>Copyright: </p>
 *
 * <p>Company: bluecore </p>
 *
 * @alias财务收入报表
 * @author design: pangben 2014-4-22
 * <br> coding:
 * @version 4.0
 */
public class BILFinanceIncomeControl  extends TControl {
	TTable table;
	private TTabbedPane tabbedPane; // 页签
	DateFormat df3 = new SimpleDateFormat("yyyyMMddhhmmss");
	DateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
	private TParm tableResult=null;
	private String tableName="";
	/**
	 * 初始化
	 */
	public void onInit() {
		// 继承
		super.onInit();
		onPage();
		tabbedPane = (TTabbedPane) this.getComponent("TABBEDPANE"); // 页签
		tabbedPane.setEnabledAt(3, false);
		TTable table6 = (TTable) this.getComponent("TABLE6");
		table6.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		this.setValue("MEM_TYPE", "3");
		this.setValue("PRINT_TYPE", "2");
		callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
				"%ROOT%\\config\\sys\\SYSFeePopup.x");// 医嘱代码
		callFunction("UI|ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE"));
		this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
	}
	/**
	 * 查询
	 */
	public void onQuery(){
		boolean isDedug=true; //add by huangtt 20160505 日志输出
		try {
			
		
		if (this.getValue("TYPE").toString().length()<=0) {
			this.messageBox("类型不能为空");
			return;
		}
		if (tabbedPane.getSelectedIndex()==0) {
			this.setValue("TOT_AMT",0.00);
		}
		if (tabbedPane.getSelectedIndex()==1) {
			this.setValue("SS_TOT_AMT",0.00);
		}
		if (tabbedPane.getSelectedIndex()==2) {
			this.setValue("YJJ_TOT_AMT", 0.00);
		}
		TParm parm=new TParm();
		TParm billTypeParm=new TParm();
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd");
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd");
		String sTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_TIME")), "HHmmss");
		String eTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_TIME")), "HHmmss");
		if(tabbedPane.getSelectedIndex()==5 && !sDate.equals(eDate)){
			this.messageBox("只能查询一天的数据");
			return;
		}
		parm.setData("START_DATE",sDate+sTime);
		parm.setData("END_DATE",eDate+eTime);
		parm.setData("TYPE",this.getValue("TYPE"));
		parm.setData("BATCHNAME",eDate);//日期，删除此日期的数据
		int dataType=0;
		if (this.getValue("TYPE").equals("1")) {
			parm.setData("BUSITYPENAME","门诊");
			dataType=13;
		}else{
			parm.setData("BUSITYPENAME","住院");
			dataType=14;
		}
		TParm checkParm=null;
		TParm checkLog=new TParm();
		switch (tabbedPane.getSelectedIndex()) {
		case 0:
			parm.setData("BATCHNAME",StringTool.getString(TypeTool.getTimestamp(getValue(
	        "START_DATE")), "yyyyMMdd"));
			parm.setData("EXPTABLENAME","DI_INCOMEEXP".toLowerCase());
			parm.setData("SOURCESYSTEM",dataType);
			parm.setData("MR_NO", this.getValue("MR_NO"));
			parm.setData("PRINT_TYPE", this.getValue("PRINT_TYPE"));
			parm.setData("REXP_CODE", this.getValue("REXP_CODE"));
			parm.setData("MEM_TYPE", this.getValue("MEM_TYPE"));
			parm.setData("OPD_TYPE", this.getValue("OPD_TYPE_Y"));
			tableResult=BILFinanceTool.getInstance().onQueryByAccounts(parm);
			table=(TTable)this.getComponent("TABLE");
			tableName="应收数据";
			break;
		case 1:
			parm.setData("BATCHNAME",StringTool.getString(TypeTool.getTimestamp(getValue(
	        "START_DATE")), "yyyyMMdd"));
			parm.setData("EXPTABLENAME","DI_INCOMEREAL".toLowerCase());
			parm.setData("SOURCESYSTEM",dataType);
			parm.setData("BIL_PAYTYPE",this.getValue("BIL_PAYTYPE"));
			parm.setData("MR_NO",this.getValue("MR_NO_S"));
			parm.setData("OPD_TYPE", this.getValue("OPD_TYPE_SD"));
			parm.setData("MEM_TYPE", this.getValue("MEM_TYPE_S"));
			tableResult=BILFinanceTool.getInstance().onQueryByPaid(parm);
			String id="";
			String sql="";
			for(int i=0;i<tableResult.getCount();i++){// add by kangy 20170711 医院垫付，保险直付  卡类型跟支付方式一起传回
				if("C4".equals(tableResult.getValue("BILL_ID",i))||"BXZF".equals(tableResult.getValue("BILL_ID",i))){
					if("C4".equals(tableResult.getValue("BILL_ID",i))&&tableResult.getValue("REMARK04",i).length()>0&&tableResult.getValue("REMARK04",i).indexOf("#")>=0){// 医院垫付
						id=tableResult.getValue("REMARK04",i).substring(0, tableResult.getValue("REMARK04",i).indexOf("#"));
						sql="SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CARDTYPE' AND ID='"+id+"'";
						billTypeParm=new TParm(TJDODBTool.getInstance().select(sql));
						tableResult.setData("BILL_TYPE",i,tableResult.getValue("BILL_TYPE",i)+billTypeParm.getValue("CHN_DESC",0));
					}else if("BXZF".equals(tableResult.getValue("BILL_ID",i))&&tableResult.getValue("REMARK08",i).length()>0&&tableResult.getValue("REMARK08",i).indexOf("#")>=0){//保险直付
						id=tableResult.getValue("REMARK08",i).substring(0, tableResult.getValue("REMARK08",i).indexOf("#"));
						sql="SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CARDTYPE' AND ID='"+id+"'";
						billTypeParm=new TParm(TJDODBTool.getInstance().select(sql));
						tableResult.setData("BILL_TYPE",i,tableResult.getValue("BILL_TYPE",i)+billTypeParm.getValue("CHN_DESC",0));
					}
				}
			}
			table=(TTable)this.getComponent("TABLE2");
			tableName="实收数据";
			break;
		case 2:
			parm.setData("BATCHNAME",StringTool.getString(TypeTool.getTimestamp(getValue(
	        "START_DATE")), "yyyyMMdd"));
			parm.setData("EXPTABLENAME","DI_INCOMEPRE".toLowerCase());
			parm.setData("SOURCESYSTEM",dataType);
			parm.setData("PAY_TYPE",this.getValue("PAY_TYPE"));
			parm.setData("MR_NO",this.getValue("MR_NO_Y"));
			tableResult=BILFinanceTool.getInstance().onQueryByTypePay(parm);
			for(int i=0;i<tableResult.getCount();i++){
				if("C4".equals(tableResult.getValue("BILL_ID",i))||"BXZF".equals(tableResult.getValue("BILL_ID",i))||"C1".equals(tableResult.getValue("BILL_ID",i))){
				if("预收套餐".equals(tableResult.getValue("TYPE",i))){
					if("C4".equals(tableResult.getValue("BILL_ID",i))&&tableResult.getValue("MEMO4",i).length()>0){
						id=tableResult.getValue("MEMO4",i).substring(0, tableResult.getValue("MEMO4",i).indexOf("#"));
						sql="SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CARDTYPE' AND ID='"+id+"'";
						billTypeParm=new TParm(TJDODBTool.getInstance().select(sql));
						tableResult.setData("BILL_TYPE",i,tableResult.getValue("BILL_TYPE",i)+billTypeParm.getValue("CHN_DESC",0));
					}else if("BXZF".equals(tableResult.getValue("BILL_ID",i))&&tableResult.getValue("MEMO8",i).length()>0){
						id=tableResult.getValue("MEMO8",i).substring(0, tableResult.getValue("MEMO8",i).indexOf("#"));
						sql="SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CARDTYPE' AND ID='"+id+"'";
						billTypeParm=new TParm(TJDODBTool.getInstance().select(sql));
						tableResult.setData("BILL_TYPE",i,tableResult.getValue("BILL_TYPE",i)+billTypeParm.getValue("CHN_DESC",0));
					}else if("C1".equals(tableResult.getValue("BILL_ID",i))&&tableResult.getValue("MEMO2",i).length()>0&&tableResult.getDouble("TOT_AMT",i)<0){
						id=tableResult.getValue("MEMO2",i).substring(0, tableResult.getValue("MEMO2",i).indexOf("#"));
						sql="SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CARDTYPE' AND ID='"+id+"'";
						billTypeParm=new TParm(TJDODBTool.getInstance().select(sql));
						tableResult.setData("BILL_TYPE",i,tableResult.getValue("BILL_TYPE",i)+billTypeParm.getValue("CHN_DESC",0));
					}
				
				}else {	
					tableResult.setData("BILL_TYPE",i,tableResult.getValue("BILL_TYPE",i)+tableResult.getValue("CARD_TYPE",i));
				}
				}
			}
			table=(TTable)this.getComponent("TABLE3");
			tableName="预交金";
			break;
		case 3://成本
			eDate=StringTool.getString(TypeTool.getTimestamp(getValue("MONTH_DATE")), "yyyyMM");
			int year = Integer.parseInt(eDate.substring(0, 4));
			int month = Integer.parseInt(eDate.substring(4, 6));
			TParm dateParm = getMonthDate(year, month);
			parm.setData("MONTH_DATE",eDate);
			parm.setData("DAY",dateParm.getValue("LAST_DATE"));
			tableResult=BILFinanceTool.getInstance().onQueryCost(parm);
			table=(TTable)this.getComponent("TABLE4");
			tableName="成本数据";
			break;
		case 4://高值耗材
			parm.setData("BATCHNAME",StringTool.getString(TypeTool.getTimestamp(getValue(
	        "START_DATE")), "yyyyMMdd"));
			parm.setData("EXPTABLENAME","HIS_FEEDETAIL".toLowerCase());
			parm.setData("SOURCESYSTEM",dataType);
			tableResult=BILFinanceTool.getInstance().onQureyByBillDate(parm);
			table=(TTable)this.getComponent("TABLE6");
			tableName="高值耗材";
			break;
		case 5://成本核算
			parm.setData("BATCHNAME",StringTool.getString(TypeTool.getTimestamp(getValue(
	        "START_DATE")), "yyyyMMdd"));
			parm.setData("EXPTABLENAME","DI_SERVICEVOLEXP".toLowerCase());
			parm.setData("SOURCESYSTEM",dataType);
			tableResult=BILFinanceTool.getInstance().onQueryCostAcount(parm);
			table=(TTable)this.getComponent("TABLE7");
			tableName="成本核算";
			break;
		case 6://药品入库
			if(null==this.getValue("BUSITYPE_NAME")||this.getValue("BUSITYPE_NAME").toString().length()<=0){
				this.messageBox("请选择业务类型");
				this.grabFocus("BUSITYPE_NAME");
				return;
			}
			parm.setData("BATCHNAME",StringTool.getString(TypeTool.getTimestamp(getValue(
	        "START_DATE")), "yyyyMMdd"));
			parm.setData("EXPTABLENAME","DI_DRUGS".toLowerCase());
			parm.setData("BUSITYPE_NAME",this.getValue("BUSITYPE_NAME"));
			String []valueName={"TYPE_CODE","ORDER_CODE","ORG_CODE","DEPT_CODE","EXEC_DEPT_CODE","SUP_CODE"};
			for (int i = 0; i < valueName.length; i++) {
				if(null!=this.getValue(valueName[i]) && this.getValueString(valueName[i]).length()>0){
					parm.setData(valueName[i],this.getValue(valueName[i]));
				}
			}
			tableResult=BILFinanceTool.getInstance().onQueryPhaData(parm);
			table=(TTable)this.getComponent("PHA_TABLE");
			tableName="药品入库";
			break;
		default:
			break;
		}
		if (tableResult.getErrCode()<0) {
			this.messageBox("查询出现错误："+tableResult.getErrText());
			return;
		}
		if(tabbedPane.getSelectedIndex()!=5){//非成本核算
			if (tableResult.getCount("ACCOUNT_DATE")<=0) {//为了统一数据，将TYPE_ID改为ACCOUNT_DATE  modify by huangjw 20141114
				this.messageBox("没有查询的数据");
				table.setParmValue(new TParm());
				return;
			}
		}else{//成本核算
			if (tableResult.getCount("TID")<=0) {
				this.messageBox("没有查询的数据");
				table.setParmValue(new TParm());
				return;
			}
		}
		/*if (tabbedPane.getSelectedIndex() == 0
				|| tabbedPane.getSelectedIndex() == 1
				|| tabbedPane.getSelectedIndex() == 2
				|| tabbedPane.getSelectedIndex() == 5) {
*/		
		checkLog = BILFinanceTool.getInstance().checkLogDrlogAyh(parm);
		if (checkLog.getErrCode() < 0) {
			System.out
					.println("查询中间库LOG表出现错误:::::" + checkLog.getErrText());
			this.messageBox("查询中间库LOG表出现错误");
			return;
		}
		//}//暂时屏蔽
		switch (tabbedPane.getSelectedIndex()) {
		case 0://应收
			checkParm=BILFinanceTool.getInstance().checkDiIncomeexp(parm);
			break;
		case 1://实收
			checkParm=BILFinanceTool.getInstance().checkDiIncomereal(parm);
			break;
		case 2://预交金
			if(parm.getValue("BUSITYPENAME").equals("门诊")){
				checkParm=BILFinanceTool.getInstance().checkDiIncomepreO(parm);
//				System.out.println("checkParm==="+checkParm);
				break;
			}
			checkParm=BILFinanceTool.getInstance().checkDiIncomepre(parm);		
			break;
		case 4://高值耗材add by huangjw 20141112
			checkParm=BILFinanceTool.getInstance().checkDiIncomeHigh(parm);
			break;
		case 5://成本核算
			checkParm=BILFinanceTool.getInstance().checkDiIncomeCostAcount(parm);
			break;
		case 6://药品入库
			parm.setData("BUSITYPENAME",this.getText("BUSITYPE_NAME"));
			checkParm=BILFinanceTool.getInstance().checkDidrugs(parm);
			break;
		default:
			break;
		}
		/*if (tabbedPane.getSelectedIndex()==0||
				tabbedPane.getSelectedIndex()==1||
				tabbedPane.getSelectedIndex()==2||tabbedPane.getSelectedIndex()==5) {*/
		if (checkParm.getErrCode()<0) {
			System.out.println("查询中间库出现错误:::::"+checkParm.getErrText());
			this.messageBox("2222查询中间库出现错误");
			return;
		}
		//}
//		if (checkLog.getCount()>0&&checkParm.getCount()>0) {
//			for (int i = 0; i < tableResult.getCount("TYPE_ID"); i++) {
//				tableResult.setData("LOAD_DOWN",i,"已审核");
//				tableResult.setData("UP_LOAD",i,"已上传");
//			}
//		}else 
		/*if (checkLog.getCount()>0 && tabbedPane.getSelectedIndex()!=5 ) {//非成本核算
			for (int i = 0; i < tableResult.getCount("TID"); i++) {//为了统一数据，将TYPE_ID改为ACCOUNT_DATE  modify by huangjw 20141114
				if (tableResult.getValue("UP_LOAD",i).equals("未上传")) {
					tableResult.setData("LOAD_DOWN",i,"未审核");
				}else{
					tableResult.setData("LOAD_DOWN",i,"已审核");
				}
			}
		}else{//成本核算
		
	*/	double arAmt=0.00;
		double phaAmt=0.00;
		if(tabbedPane.getSelectedIndex()!=4){
		  if(checkParm.getCount()>0){
				if(checkLog.getCount()>0){
					for (int i = 0; i < tableResult.getCount("TID"); i++) {
						tableResult.setData("UP_LOAD",i,"已上传");
						tableResult.setData("LOAD_DOWN",i,"已审核");
						if (tabbedPane.getSelectedIndex()==0||tabbedPane.getSelectedIndex()==1
								||tabbedPane.getSelectedIndex()==2) {
							arAmt+=tableResult.getDouble("TOT_AMT",i);
						}else if (tabbedPane.getSelectedIndex()==6){
							phaAmt+=tableResult.getDouble("AR_AMT",i);
						}
					}
				}else{
					for (int i = 0; i < tableResult.getCount("TID"); i++) {
						tableResult.setData("UP_LOAD",i,"已上传");
						tableResult.setData("LOAD_DOWN",i,"未审核");
						if (tabbedPane.getSelectedIndex()==0||tabbedPane.getSelectedIndex()==1
								||tabbedPane.getSelectedIndex()==2) {
							arAmt+=tableResult.getDouble("TOT_AMT",i);
						}else if (tabbedPane.getSelectedIndex()==6){
							phaAmt+=tableResult.getDouble("AR_AMT",i);
						}
					}
				}
			}else{
				for (int i = 0; i < tableResult.getCount("TID"); i++) {
					tableResult.setData("UP_LOAD",i,"未上传");
					tableResult.setData("LOAD_DOWN",i,"未审核");
					if (tabbedPane.getSelectedIndex()==0||tabbedPane.getSelectedIndex()==1
							||tabbedPane.getSelectedIndex()==2) {
						arAmt+=tableResult.getDouble("TOT_AMT",i);
					}else if (tabbedPane.getSelectedIndex()==6){
						phaAmt+=tableResult.getDouble("AR_AMT",i);
					}
				}
			}
		}
		//}
		if (tabbedPane.getSelectedIndex()==4) {//耗材校验是否已经审核
			if(checkParm.getCount()>0&&checkParm.getValue("BISPROC",0).equals("Y")){
				for (int i = 0; i < tableResult.getCount("ACCOUNT_DATE"); i++) {
					if (tableResult.getValue("UP_LOAD",i).equals("未上传")) {
						tableResult.setData("LOAD_DOWN",i,"未审核");
					}else{
						tableResult.setData("LOAD_DOWN",i,"已审核");
					}
				}
			}
			for(int i=0;i<tableResult.getCount();i++){//年龄处理
				tableResult.setData("PAT_AGE",i,this.patAge(tableResult.getTimestamp("BIRTH_DATE",i)));
			}
		}
//		}else if (checkParm.getCount()>0&&checkLog.getCount()<=0) {
//			for (int i = 0; i < tableResult.getCount("TYPE_ID"); i++) {
//				tableResult.setData("UP_LOAD",i,"已上传");
//			}
//		}
//		if (tabbedPane.getSelectedIndex()==1&&this.getValueInt("TYPE")==3) {
//			for (int i = 0; i < tableResult.getCount("TYPE_ID"); i++) {
//				tableResult.setData("TID",i,""+(i+1));
//			}
//		}
		if (tabbedPane.getSelectedIndex()==0) {
			this.setValue("TOT_AMT", arAmt);
			this.setValue("REDUCE_AMT_Y", tableResult.getDouble("REDUCE_AMT"));
			this.setValue("YS_AMT", arAmt-tableResult.getDouble("REDUCE_AMT"));
		}
		if (tabbedPane.getSelectedIndex()==1) {
			this.setValue("SS_TOT_AMT", arAmt);
			this.setValue("REDUCE_AMT_S", tableResult.getDouble("REDUCE_AMT"));
			this.setValue("SS_AMT", arAmt-tableResult.getDouble("REDUCE_AMT"));
		}
		if (tabbedPane.getSelectedIndex()==2) {
			this.setValue("YJJ_TOT_AMT", arAmt);
		}
		if(tabbedPane.getSelectedIndex()==6){
			this.setValue("PHA_SUM_TOT_AMT", phaAmt);
		}
		table.setParmValue(tableResult);
		
		} catch (Exception e) {
			// TODO: handle exception
			if(isDedug){  
				System.out.println(" come in class: BILFinanceIncomeControl.class ，method ：onQuery");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 计算年龄
	 * 
	 * @param date
	 * @return
	 */
	private String patAge(Timestamp date) {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp temp = date == null ? sysDate : date;
		String age = "0";
		age = DateUtil.showAge(temp, sysDate);
		return age;
	}
	/**
	 * 初始化数据
	 */
	public void onPage(){
		//this.setValue("START_DATE", StringTool.rollDate(SystemTool.getInstance().getDate(), -1));
		this.setValue("START_DATE",SystemTool.getInstance().getDate());
		this.setValue("END_DATE", SystemTool.getInstance().getDate());
		this.setValue("START_TIME", "00:00:00");
		this.setValue("END_TIME", "23:59:59");
		this.setValue("TYPE", "1");
		this.setValue("MONTH_DATE", SystemTool.getInstance().getDate());
		this.setValue("PHA_SUM_TOT_AMT", 0.00);
	}
	/**
	 * 药品入库
	 * @param type
	 * @return
	 */
	private TParm diDrugsParm(){
		String date="";
		TParm parmValue=new TParm();
		TParm parm=table.getParmValue();
		int row=parm.getCount();
		if (row<=0) {
			this.messageBox("没有需要操作的数据");
			return null;
		}
		parm=tableResult;
		int sum=0;
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd");
		String eDateT= StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyy-MM-dd");
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd");
		String sTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_TIME")), "HHmmss");
		String eTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_TIME")), "HHmmss");
		parmValue.setData("START_DATE",sDate+sTime);
		parmValue.setData("END_DATE",eDate+eTime);
		String uid=getUUID();//日志表主键
		//String checkDate="";
		for (int i = 0; i <row; i++) {
			date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			parmValue.addData("PK_DRUGS",getUUID());//外部系统收入表主键
			//parmValue.addData("DATA_TYPE","C");
			parmValue.addData("AMOUNT",  parm.getDouble("AR_AMT",i));//金额
			parmValue.addData("PK_EXPLOG", uid);//导出日志表主键
			parmValue.addData("BATCHNAME",sDate);//批标记建议用"YYYYMMDD"
			parmValue.addData("BILLCLERKNAME",parm.getValue("USER_NAME",i));//制单人
			parmValue.addData("BUSITYPENAME",parm.getValue("BILL_TYPE",i));//业务类型名称
			parmValue.addData("CDAY",parm.getValue("ACCOUNT_DATE",i));//日期
			parmValue.addData("CMONTH", date.substring(4,6));//月份
			parmValue.addData("CYEAR", date.substring(0,4));//年度
			//parmValue.addData("EXPTIME", SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//导出时间建议用“HHMMSS”
			parmValue.addData("CORPNAME", "北京爱育华妇儿医院");//公司名称--可选编码，名称，pk,推荐名称
			parmValue.addData("BILLDEPTCODE", parm.getValue("DEPT_CODE",i));//开单科室代码
			parmValue.addData("BILLDEPTNAME", parm.getValue("DEPT_DESC",i));//开单科室名称
			parmValue.addData("DDATE", parm.getValue("ACCOUNT_DATE",i));//日期
			parmValue.addData("PK_CORP", "001");//公司主键--可选编码，名称，pk,推荐名称
			parmValue.addData("EXECDEPTCODE", parm.getValue("EXE_DEPT_CODE",i));//执行科室代码
			parmValue.addData("EXECDEPTNAME", parm.getValue("EXE_DEPT_DESC",i));//执行单科室名称
			parmValue.addData("DURGCLCODE", parm.getValue("PHA_TYPE",i));//药品类别代码
			parmValue.addData("DURGCLNAME", parm.getValue("PHA_TYPE_DESC",i));//药品类别名称
			parmValue.addData("CUSTCODE", parm.getValue("SUP_CODE",i));//供货商代码
			parmValue.addData("CUSTNAME", parm.getValue("SUP_CHN_DESC",i));//供货商名称
			parmValue.addData("PHARMACYCODE", parm.getValue("PHA_CODE",i));//药房代码
			parmValue.addData("PHARMACYNAME", parm.getValue("PHA_DESC",i));//药房名称		
			parmValue.addData("VDEF1", parm.getValue("VDEF1",i));//门诊住院类别
			parmValue.addData("INOUT", parm.getValue("INOUT",i));//收/退
			parmValue.addData("OPT_USER", Operator.getID());//
			parmValue.addData("OPT_TERM", Operator.getIP());//
			sum++;
		}
		//parmValue.setData("TYPE_SUM",this.getValue("PHA_DEPT"));
		parmValue.setCount(parmValue.getCount("PK_EXPLOG"));
		parmValue.setData("CHEKC_DATE",sDate);//主键
		//====================================================添加到日志表add by huangjw 20150414
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//主键
		logParm.setData("EXPTABLENAME","DI_DRUGS".toLowerCase());//导出表名
		logParm.setData("BATCHNAME",sDate);
		logParm.setData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//导出时间建议用“HHMMSS”
		logParm.setData("EXPOPERATOR",Operator.getName());//导出操作员姓名
		logParm.setData("EXPSTATE",0);//导出状态
		logParm.setData("TAOTALNUM",sum);//导出记录总数
		logParm.setData("SOURCESYSTEM",parm.getValue("BILL_NAME",0));
		//logParm.setData("ADM_TYPE",type);
		//logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		//=====================================================
		return parmValue;
	}
	/**
	 * 预交金
	 * @return
	 */
	private TParm diIncomepreParm(String type){
		String date="";
		TParm parmValue=new TParm();
		TParm parm=table.getParmValue();
		int row=parm.getCount();
		if (row<=0) {
			this.messageBox("没有需要操作的数据");
			return null;
		}
		parm=tableResult;
		int sum=0;
		int dataType=0;
		//String dataMessage="";
		if (this.getValue("TYPE").toString().equals("1")) {
			dataType=13;
			//dataMessage="门诊";
		}else if (this.getValue("TYPE").toString().equals("2")) {
			dataType=14;
			//dataMessage="住院";
		}
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd");
		String eDateT= StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyy-MM-dd");
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd");
		String sTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_TIME")), "HHmmss");
		String eTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_TIME")), "HHmmss");
		parmValue.setData("START_DATE",sDate+sTime);
		parmValue.setData("END_DATE",eDate+eTime);
		String uid=getUUID();//日志表主键
		String bilDate="";
		//String checkDate="";
		for (int i = 0; i <row; i++) {
			date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			bilDate=SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE",i),true).toString();
			//checkDate=date.substring(0,8);
			parmValue.addData("PK_INCOMEPRE",getUUID());//外部系统收入表主键
			parmValue.addData("DATA_TYPE","C");
			parmValue.addData("PK_EXPLOG", uid);//导出日志表主键
			parmValue.addData("BATCHNAME",sDate);//批标记建议用"YYYYMMDD"
			parmValue.addData("EXPTIME", SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//导出时间建议用“HHMMSS”
			parmValue.addData("CYEAR", date.substring(0,4));//年度
			parmValue.addData("CMONTH", date.substring(4,6));//月份
			parmValue.addData("CDAY",bilDate.substring(0,4)+"-"+bilDate.substring(4,6)+"-"+bilDate.substring(6,8));//日期
			parmValue.addData("DDATE", parm.getValue("ACCOUNT_DATE",i).substring(0,10));//日期
			parmValue.addData("PK_CORP", "001");//公司主键--可选编码，名称，pk,推荐名称
			parmValue.addData("CORPNAME", "北京爱育华妇儿医院");//公司名称--可选编码，名称，pk,推荐名称
			parmValue.addData("SOURCESYSTEM", 1);//外部数据来源系统
			parmValue.addData("PK_OUTERSYSTEM", "HIS");//外系统主键
			parmValue.addData("REALDATE",  SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE",i),true));//结算日期--中间表
			parmValue.addData("VNUMBER", parm.getValue("TID",i));//序号--中间表
			parmValue.addData("BUSITYPENAME", parm.getValue("TYPE",i));//业务类型名称
			parmValue.addData("PK_INCOMETYPE", parm.getValue("BILL_ID",i));//收入分类主键
			parmValue.addData("INCOMETYPENAME", parm.getValue("BILL_TYPE",i));//收入分类名称
			parmValue.addData("AMOUNT",  parm.getDouble("TOT_AMT",i));//金额
			parmValue.addData("CPAYMENT", parm.getValue("BILL_TYPE",i));//支付方式--中间表
			parmValue.addData("CPAYITEM", "");//支付明细--中间表
			parmValue.addData("CASHBACK", parm.getDouble("TOT_AMT",i)<0?"退":"收");//收退
			parmValue.addData("BILLCLERKNAME",parm.getValue("PRINT_NAME",i));//收费员名称
			parmValue.addData("PK_BILLCLERK",parm.getValue("PRINT_USER",i));//收费员主键
			parmValue.addData("CPAYMENTNUM", parm.getValue("BILL_ID",i));//支付方式编码--中间表
			parmValue.addData("CPAYITEMNUM", "");//支付明细编码--中间表
			parmValue.addData("PK_PATIENT", parm.getValue("MR_NO",i));//病人主键
			parmValue.addData("PATIENTNAME", parm.getValue("PAT_NAME",i));//病人名称
			parmValue.addData("ADM_TYPE", type);//类型
			parmValue.addData("OPT_USER", Operator.getID());//
			parmValue.addData("OPT_TERM", Operator.getIP());//
			parmValue.addData("VDEF2", parm.getValue("PACKAGE_CODE",i));//套餐编码 add by huangtt 20160324
			parmValue.addData("VDEF3", parm.getValue("PACKAGE_DESC",i));//套餐名称 add by huangtt 20160324
			sum++;
		}
		parmValue.setData("TYPE_SUM",type);
		parmValue.setCount(parmValue.getCount("PK_INCOMEPRE"));
		parmValue.setData("CHEKC_DATE",sDate);
		//====================================================添加到日志表add by huangjw 20150414
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//主键
		logParm.setData("EXPTABLENAME","DI_INCOMEPRE".toLowerCase());//导出表名
		logParm.setData("BATCHNAME",sDate);
		logParm.setData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//导出时间建议用“HHMMSS”
		logParm.setData("EXPOPERATOR",Operator.getName());//导出操作员姓名
		logParm.setData("EXPSTATE",0);//导出状态
		logParm.setData("TAOTALNUM",sum);//导出记录总数
		logParm.setData("SOURCESYSTEM",dataType);
		//logParm.setData("ADM_TYPE",type);
		//logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		//=====================================================
		return parmValue;
	}
	/**
	 * 应收\实收
	 * @return
	 */
	private TParm diIncomeexpAndDiIncomerealParm(int index,String type){
		String date="";
		TParm parmValue=new TParm();
		TParm parm=table.getParmValue();
		int row=parm.getCount();
		if (row<=0) {
			this.messageBox("没有需要操作的数据");
			return null;
		}
		parm=tableResult;
		int sum=0;
		int dataType=0;
		//String dataMessage="";
		if (this.getValue("TYPE").toString().equals("1")) {
			dataType=13;
			//dataMessage="门诊";
		}else if (this.getValue("TYPE").toString().equals("2")) {
			dataType=14;
			//dataMessage="住院";
		}
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd");
		String eDateT= StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyy-MM-dd");
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd");
		String sTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_TIME")), "HHmmss");
		String eTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_TIME")), "HHmmss");
		parmValue.setData("START_DATE",sDate+sTime);
		parmValue.setData("END_DATE",eDate+eTime);
		String uid=getUUID();//作为日志表主键  add by huangjw 20150414
		String bilDate="";
		//String checkDate="";
		for (int i = 0; i <row; i++) {
			date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			bilDate=SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE",i),true).toString();
			//checkDate=date.substring(0,8);
			if (index==1) {
				parmValue.addData("PK_INCOMEEXP",getUUID());//外部系统收入表主键
				parmValue.addData("DATA_TYPE","A");
			}else if(index==2){
				parmValue.addData("DATA_TYPE","B");
				parmValue.addData("PK_INCOMEREAL",getUUID());//外部系统收入表主键
			}
			parmValue.addData("PK_EXPLOG", uid);//导出日志表主键
			parmValue.addData("BATCHNAME", sDate);//批标记建议用"YYYYMMDD"
			parmValue.addData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//导出时间建议用“HHMMSS”
			parmValue.addData("CYEAR", date.substring(0,4));//年度
			parmValue.addData("CMONTH", date.substring(4,6));//月份
			parmValue.addData("CDAY", bilDate.substring(0,4)+"-"+bilDate.substring(4,6)+"-"+bilDate.substring(6,8));//日期
			parmValue.addData("PK_CORP", "001");//公司主键--可选编码，名称，pk,推荐名称
			parmValue.addData("CORPNAME", "北京爱育华妇儿医院");//公司名称--可选编码，名称，pk,推荐名称
			parmValue.addData("PK_ITEM", parm.getValue("BILL_ID",i));//收入主键
			parmValue.addData("ITEMNAME", parm.getValue("BILL_TYPE",i));//收入名称
			parmValue.addData("PK_INCOMETYPE", parm.getValue("BILL_ID",i));//收入分类主键
			parmValue.addData("INCOMETYPENAME", parm.getValue("BILL_TYPE",i));//收入分类名称
			parmValue.addData("PK_BUSITYPE",parm.getValue("TYPE_ID",i));//业务类型主键
			parmValue.addData("BUSITYPENAME", parm.getValue("TYPE",i));//业务类型名称
			parmValue.addData("PK_DIAGNOSISDEPT", parm.getValue("DEPT_CODE",i));//开单科室主键
			parmValue.addData("DIAGNOSISDEPTNAME", parm.getValue("DEPT_DESC",i));//开单科室名称
			parmValue.addData("PK_DIAGNOSTICIAN", parm.getValue("DR_CODE",i));//开单医生主键
			parmValue.addData("DIAGNOSTICIANNAME",parm.getValue("DR_DESC",i));//开单医生名称
			parmValue.addData("PK_EXEDEPT", parm.getValue("EXE_DEPT_CODE",i));//执行科室主键
			parmValue.addData("EXEDEPTNAME", parm.getValue("EXE_DEPT_DESC",i));//执行科室名称
			parmValue.addData("PK_EXECUTOR", Operator.getID());//执行人主键
			parmValue.addData("EXECUTORNAME",Operator.getName());//执行人名称
			parmValue.addData("PK_BILLCLERK",parm.getValue("PRINT_USER",i));//收费员主键
			parmValue.addData("BILLCLERKNAME",parm.getValue("PRINT_NAME",i));//收费员名称
			parmValue.addData("PK_PATIENT", parm.getValue("MR_NO",i));//病人主键
			parmValue.addData("PATIENTNAME", parm.getValue("PAT_NAME",i));//病人名称
			parmValue.addData("CUSTCODE", parm.getValue("MR_NO",i));//客户编码--中间表
			parmValue.addData("CUSTNAME", parm.getValue("PAT_NAME",i));//客户名称--中间表
			parmValue.addData("CPAYMENT", parm.getValue("BILL_TYPE",i));//支付方式--中间表
			parmValue.addData("CPAYITEM", "");//支付明细--中间表
			parmValue.addData("DDATE", parm.getValue("ACCOUNT_DATE",i).substring(0,10));//核算日期--中间表
			parmValue.addData("REALDATE",  SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE",i),true));//结算日期--中间表
			parmValue.addData("VNUMBER", parm.getValue("TID",i));//序号--中间表
			parmValue.addData("CPAYMENTNUM", parm.getValue("BILL_ID",i));//支付方式编码--中间表
			parmValue.addData("CPAYITEMNUM", "");//支付明细编码--中间表
			parmValue.addData("PK_CINPATIENTAREA", parm.getValue("CLINICAREA_CODE",i));//病区主键
			parmValue.addData("CINPATIENTAREA", parm.getValue("CLINIC_DESC",i));//病区
			parmValue.addData("ISINSURANCE", parm.getValue("INS_FLG",i));//是否医保Y:是医保;N:非医保
			parmValue.addData("DISEASE", "");//单病种
			parmValue.addData("DISEASEGROUP", "");//病种组
			parmValue.addData("AMOUNT",  parm.getDouble("TOT_AMT",i));//金额
			parmValue.addData("SOURCESYSTEM", 1);//外部数据来源系统
			parmValue.addData("PK_OUTERSYSTEM", "HIS");//外系统主键
			parmValue.addData("INPRICE", "");//收入单价
			parmValue.addData("CASHBACK", "");//收退
			parmValue.addData("ADM_TYPE", type);//类型
			parmValue.addData("OPT_USER", Operator.getID());//
			parmValue.addData("OPT_TERM", Operator.getIP());//
			sum++;
		}
		parmValue.setData("TYPE_SUM",type);
		parmValue.setCount(parmValue.getCount("PK_EXPLOG"));
		parmValue.setData("CHEKC_DATE",sDate);
		//====================================================添加到日志表add by huangjw 20150414
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//主键
		if(index==1){//应收表
			logParm.setData("EXPTABLENAME","DI_INCOMEEXP".toLowerCase());//导出表名
		}else{//实收表
			logParm.setData("EXPTABLENAME","DI_INCOMEREAL".toLowerCase());//导出表名
		}
		
		logParm.setData("BATCHNAME",sDate);
		logParm.setData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//导出时间建议用“HHMMSS”
		logParm.setData("EXPOPERATOR",Operator.getName());//导出操作员姓名
		logParm.setData("EXPSTATE",0);//导出状态
		logParm.setData("TAOTALNUM",sum);//导出记录总数
		logParm.setData("SOURCESYSTEM",dataType);
		//logParm.setData("ADM_TYPE",type);
		//logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		//=====================================================
		return parmValue;
	}
	
	/**
	 * 高值耗材数据add by huangjw 20141112
	 * @param type
	 * @return
	 */
	public TParm highPriceDataParm(int index,String type){
		String date="";
		TParm parmValue=new TParm();
		table.acceptText();
		TParm parm=table.getParmValue();
		
		int row=parm.getCount();
		if (row<=0) {
			this.messageBox("没有需要操作的数据");
			return null;
		}
		String uid= getUUID();
		int sum=0;
		int dataType=0;
		String dataMessage="";
		if (this.getValue("TYPE").toString().equals("1")) {
			dataType=13;
			dataMessage="门诊";
		}else if (this.getValue("TYPE").toString().equals("2")) {
			dataType=14;
			dataMessage="住院";
		}
		//parm=tableResult;
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd");
		String eDateT= StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyy-MM-dd");
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd");
		String sTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_TIME")), "HHmmss");
		String eTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_TIME")), "HHmmss");
		parmValue.setData("START_DATE",sDate+sTime);
		parmValue.setData("END_DATE",eDate+eTime);
		for (int i = 0; i <row; i++) {
			if (parm.getValue("FLG",i).equals("N")) {
				continue;
			}
			parmValue.addData("FLG", "Y");
			//System.out.println("BILL_DATE::::"+parm.getValue("ACCOUNT_DATE",i));
			//System.out.println("BILL_DATE::ddd::"+StringTool.getString(TypeTool.getTimestamp(parm.getValue("ACCOUNT_DATE",i)), "yyyyMMdd"));
			//date=df2.format(parm.getValue("ACCOUNT_DATE",i));
			date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			parmValue.addData("PK_EXPLOG",uid);//导出日志表主键
			parmValue.addData("BATCHNAME", sDate);//批标记建议用"YYYYMMDD"
			parmValue.addData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));
			parmValue.addData("SOURCESYSTEM", dataType);//外部数据来源系统
			
			parmValue.addData("PK_FEEDETAIL",getUUID());//外部系统收入表主键
			parmValue.addData("BISCHARGE",parm.getValue("BILL_FLG",i));
			//parmValue.addData("BISPROC", parm.getValue("HRP_FLG",i));
			parmValue.addData("VHISBUSID",parm.getValue("RECEIPT_NO",i));
			parmValue.addData("VBARCODE",parm.getValue("INV_CODE",i));
			parmValue.addData("VITEMCODE",parm.getValue("ORDER_CODE",i));
			parmValue.addData("VITEMNAME",parm.getValue("ORDER_DESC",i));
			parmValue.addData("VRECEIPTCODE",parm.getValue("REXP_CODE",i));
			parmValue.addData("VRECEIPTNAME",parm.getValue("REXP_DESC",i));
			parmValue.addData("PK_CORP","001");
			parmValue.addData("NCHARGEMNY",parm.getValue("AR_AMT",i));
			parmValue.addData("NCHARGENUMBER",parm.getValue("DOSAGE_QTY",i));
			parmValue.addData("DCHARGEDATE",date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6,8));
			parmValue.addData("VBILLCLERKCODE",parm.getValue("BILL_USER",i));
			parmValue.addData("VBILLCKERKNAME",parm.getValue("BILL_NAME",i));
			parmValue.addData("VBILLDEPTCODE",parm.getValue("DEPT_CODE",i));
			parmValue.addData("VBILLDEPTNAME",parm.getValue("DEPT_NAME",i));
			parmValue.addData("VEXECDEPTCODE",parm.getValue("EXEC_DEPT_CODE",i));
			parmValue.addData("VEXECDEPTNAME",parm.getValue("EXEC_DEPT_NAME",i));
			parmValue.addData("VADMNUMBER",parm.getValue("MR_NO",i));
			parmValue.addData("VPATIENTNAME",parm.getValue("PAT_NAME",i));
			parmValue.addData("VSEX",parm.getValue("SEX_CODE",i));
			parmValue.addData("VOPNAME",parm.getValue("OP_DESC",i));
			parmValue.addData("DOPDATE",parm.getValue("OP_DATE",i));
			parmValue.addData("BUSITYPENAME",type);
			parmValue.addData("VNAMEPHYSICIAN",parm.getValue("DR_NAME",i));
			parmValue.addData("IPATIENTAGE",getYear(parm.getTimestamp("BIRTH_DATE",i)));
			parmValue.addData("VFAMILYMEMBERS",parm.getValue("PAT_FAMLIY",i));
			parmValue.addData("VTEL",parm.getValue("PAT_TEL",i));
			parmValue.addData("VUSERCODE",parm.getValue("BILL_USER",i));//Operator.getID() 改为 parm.getValue("BILL_USER",i)
			parmValue.addData("UNITCODE","001");
			//parmValue.addData("VHISBUSID",parm.getValue("RECEIPT_NO",i));
			parmValue.addData("BISPROC","N");
			//=================================住院用
			parmValue.addData("CASE_NO_SEQ", parm.getValue("CASE_NO_SEQ",i));//
			parmValue.addData("CASE_NO", parm.getValue("CASE_NO",i));//就诊记录
			parmValue.addData("SEQ_NO", parm.getValue("SEQ_NO",i));//序号
			//=================================住院用
			sum++;
		}
		parmValue.setData("TYPE_SUM",type);
		parmValue.setCount(parmValue.getCount("PK_EXPLOG"));
		parmValue.setData("CHEKC_DATE",sDate);
		//====================================================添加到日志表add by huangjw 20150414
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//主键
		logParm.setData("EXPTABLENAME","his_feedetail");//导出表名
		logParm.setData("BATCHNAME",sDate);
		logParm.setData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//导出时间建议用“HHMMSS”
		logParm.setData("EXPOPERATOR",Operator.getName());//导出操作员姓名
		logParm.setData("EXPSTATE",0);//导出状态
		logParm.setData("TAOTALNUM",sum);//导出记录总数
		logParm.setData("SOURCESYSTEM",dataType);
		//logParm.setData("ADM_TYPE",type);
		//logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		return parmValue;
	}
	private String getYear(Timestamp newDate){
		//SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = new Date();
		long day = (date.getTime() - newDate.getTime()) / (24 * 60 * 60 * 1000)
				+ 1;
		String year=new java.text.DecimalFormat("#.00").format(day/365f);
		return year;
	}
	/**
	 * 成本数据
	 * @param type
	 * @return
	 */
	private TParm costDataParm(String type){
		TParm parmValue=new TParm();
		TParm parm=table.getParmValue();
		int row=parm.getCount();
		if (row<=0) {
			this.messageBox("没有需要操作的数据");
			return null;
		}
		parm=tableResult;
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "MONTH_DATE")), "yyyyMM");
		int year = Integer.parseInt(eDate.substring(0, 4));
		int month = Integer.parseInt(eDate.substring(4, 6));
		TParm dateParm = getMonthDate(year, month);
		String uid= getUUID();
		int sum=0;
		int dataType=0;
		String dataMessage="";
		if (this.getValue("TYPE").toString().equals("1")) {
			dataType=2;
			dataMessage="挂号";
		}else if (this.getValue("TYPE").toString().equals("2")) {
			dataType=3;
			dataMessage="门诊";
		}else if (this.getValue("TYPE").toString().equals("3")) {
			dataType=4;
			dataMessage="住院";
		}
		for (int i = 0; i <row; i++) {
			//date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			//checkDate=date.substring(0,8);
			parmValue.addData("PK_INCOMEEXP",getUUID());//外部系统收入表主键
			parmValue.addData("DATA_TYPE","D");
			parmValue.addData("PK_EXPLOG", uid);//导出日志表主键
			parmValue.addData("BATCHNAME", eDate+dateParm.getValue("LAST_DATE"));//批标记建议用"YYYYMMDD"
			parmValue.addData("EXPTIME","000000");//导出时间建议用“HHMMSS”
			parmValue.addData("CYEAR", eDate.substring(0,4));//年度
			parmValue.addData("CMONTH", eDate.substring(4,6));//月份
			parmValue.addData("CDAY", eDate+dateParm.getValue("LAST_DATE")+"000000");//日期
			parmValue.addData("PK_CORP", "001");//公司主键--可选编码，名称，pk,推荐名称
			parmValue.addData("CORPNAME", "西安交通大学第二附属医院");//公司名称--可选编码，名称，pk,推荐名称
			parmValue.addData("PK_ITEM", parm.getValue("BILL_ID",i));//收入主键
			parmValue.addData("ITEMNAME", parm.getValue("BILL_TYPE",i));//收入名称
			parmValue.addData("PK_INCOMETYPE", parm.getValue("BILL_ID",i));//收入分类主键
			parmValue.addData("INCOMETYPENAME", parm.getValue("BILL_TYPE",i));//收入分类名称
			parmValue.addData("PK_BUSITYPE",parm.getValue("TYPE_ID",i));//业务类型主键
			parmValue.addData("BUSITYPENAME", parm.getValue("TYPE",i));//业务类型名称
			parmValue.addData("PK_DIAGNOSISDEPT", parm.getValue("DEPT_CODE",i));//开单科室主键
			parmValue.addData("DIAGNOSISDEPTNAME", parm.getValue("DEPT_DESC",i));//开单科室名称
			parmValue.addData("PK_DIAGNOSTICIAN", "");//开单医生主键
			parmValue.addData("DIAGNOSTICIANNAME","");//开单医生名称
			parmValue.addData("PK_EXEDEPT", parm.getValue("DEPT_CODE",i));//执行科室主键
			parmValue.addData("EXEDEPTNAME", parm.getValue("DEPT_DESC",i));//执行科室名称
			parmValue.addData("PK_EXECUTOR", Operator.getID());//执行人主键
			parmValue.addData("EXECUTORNAME",Operator.getName());//执行人名称
			parmValue.addData("PK_BILLCLERK","");//收费员主键
			parmValue.addData("BILLCLERKNAME","");//收费员名称
			parmValue.addData("PK_PATIENT", "");//病人主键
			parmValue.addData("PATIENTNAME", "");//病人名称
			parmValue.addData("CUSTCODE", "");//客户编码--中间表
			parmValue.addData("CUSTNAME","");//客户名称--中间表
			parmValue.addData("CPAYMENT", parm.getValue("BILL_TYPE",i));//支付方式--中间表
			parmValue.addData("CPAYITEM", "");//支付明细--中间表
			parmValue.addData("DDATE", eDate+dateParm.getValue("LAST_DATE")+"000000");//核算日期--中间表
			parmValue.addData("REALDATE", eDate+dateParm.getValue("LAST_DATE")+"000000");//结算日期--中间表
			parmValue.addData("VNUMBER", parm.getValue("TID",i));//序号--中间表
			parmValue.addData("CPAYMENTNUM", parm.getValue("BILL_ID",i));//支付方式编码--中间表
			parmValue.addData("CPAYITEMNUM", "");//支付明细编码--中间表
			parmValue.addData("PK_CINPATIENTAREA","");//病区主键
			parmValue.addData("CINPATIENTAREA", "");//病区
			parmValue.addData("ISINSURANCE","");//是否医保Y:是医保;N:非医保
			parmValue.addData("DISEASE", "");//单病种
			parmValue.addData("DISEASEGROUP", "");//病种组
			parmValue.addData("AMOUNT",  parm.getDouble("TOT_AMT",i));//金额
			parmValue.addData("SOURCESYSTEM", dataType);//外部数据来源系统
			parmValue.addData("PK_OUTERSYSTEM", dataMessage);//外系统主键
			parmValue.addData("INPRICE", "");//收入单价
			parmValue.addData("CASHBACK", "");//收退
			parmValue.addData("ADM_TYPE", type);//类型
			sum+=parm.getInt("NUM",i);
		}
		parmValue.setData("TYPE_SUM",type);
		parmValue.setCount(parmValue.getCount("PK_EXPLOG"));
		parmValue.setData("CHEKC_DATE",eDate+dateParm.getValue("LAST_DATE"));
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//主键
		logParm.setData("EXPTABLENAME","");//导出表名
		logParm.setData("BATCHNAME",eDate+dateParm.getValue("LAST_DATE"));
		logParm.setData("EXPTIME","000000");//导出时间建议用“HHMMSS”
		logParm.setData("EXPOPERATOR",Operator.getName());//导出操作员姓名
		logParm.setData("EXPSTATE",1);//导出状态
		logParm.setData("TAOTALNUM",sum);//导出记录总数
		logParm.setData("SOURCESYSTEM",2);
		logParm.setData("ADM_TYPE",type);
		logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		return parmValue; 
	}
	/**
	 * 成本核算数据 add by huangjw 20150409
	 * @param type
	 * @return
	 */
	public TParm costAcountParm(String type){
		TParm parmValue=new TParm();
		table=(TTable) this.getComponent("TABLE7");
		TParm parm=table.getParmValue();
		int row=parm.getCount();
		if (row<=0) {
			this.messageBox("没有需要操作的数据");
			return null;
		}
		parm=tableResult;
		String exptime=SystemTool.getInstance().getDate().toString().substring(0,19);
		String exptime1=SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", "");
		
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd");
		String eDateT= StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyy-MM-dd");
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd");
		String sTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_TIME")), "HHmmss");
		String eTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_TIME")), "HHmmss");
		String cyear=eDateT.substring(0,4);
		String cmonth=eDateT.substring(5,7);
		String cday=eDateT;
		parmValue.setData("START_DATE",sDate+sTime);
		parmValue.setData("END_DATE",eDate+eTime);
		String uid= getUUID();
		int sum=0;
		int dataType=0;
		String dataMessage="";
		if (this.getValue("TYPE").toString().equals("1")) {
			dataType=13;
			dataMessage="门诊";
		}else if (this.getValue("TYPE").toString().equals("2")) {
			dataType=14;
			dataMessage="住院";
		}
		for (int i = 0; i <row; i++) {
			parmValue.addData("PK_SERVICEVOLEXP", getUUID());//外部系统对外服务量表主建
			parmValue.addData("PK_EXPLOG", uid);//导出日志表主键
			parmValue.addData("BATCHNAME", sDate);//导出批标记
			if("O".equals(type)){
				parmValue.addData("TYPE", "1");
			}else if("I".equals(type)){
				parmValue.addData("TYPE", "2");
			}
			
			parmValue.addData("BUSITYPENAME", parm.getValue("TYPE",i));
			parmValue.addData("EXPTIME", exptime1);//导出时间
			parmValue.addData("CYEAR", cyear);//年度
			parmValue.addData("CMONTH", cmonth);//月份
			parmValue.addData("CDAY", cday);//业务日期
			parmValue.addData("PK_CORP", "001");//公司主键
			parmValue.addData("CORPNAME", "北京爱育华妇儿医院");//公司名称
			parmValue.addData("PK_ITEM", parm.getValue("PK_ITEM",i));//服务项目主键
			parmValue.addData("ITEMNAME", parm.getValue("ITEMNAME",i));//服务项目名称
			parmValue.addData("VOLUME", parm.getDouble("VOLUME",i));//服务量
			parmValue.addData("PK_DIAGNOSISDEPT", parm.getValue("PK_DIAGNOSISDEPT",i));//科室主键
			parmValue.addData("DIAGNOSISDEPTNAME", parm.getValue("DIAGNOSISDEPTNAME",i));//科室名称
			parmValue.addData("PK_OUTERSYSTEM", dataMessage);//
			parmValue.addData("SOURCESYSTEM", dataType);//
			sum+=parm.getDouble("VOLUME",i);
		}
		parmValue.setData("CHEKC_DATE",sDate);
		parmValue.setCount(parmValue.getCount("PK_EXPLOG"));
		 
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//主键
		logParm.setData("EXPTABLENAME","DI_SERVICEVOLEXP".toLowerCase());//导出表名
		logParm.setData("BATCHNAME",sDate);
		logParm.setData("EXPTIME",exptime1);//导出时间建议用“HHMMSS”
		logParm.setData("EXPOPERATOR",Operator.getName());//导出操作员姓名
		logParm.setData("EXPSTATE",0);//导出状态
		logParm.setData("TAOTALNUM",sum);//导出记录总数
		logParm.setData("SOURCESYSTEM",dataType);
		//logParm.setData("ADM_TYPE",type);
		//logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		parmValue.setData("LOCAL_LOG_FLG",parm.getBoolean("LOCAL_LOG_FLG"));
		return parmValue;
	}
	
	
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		// 去掉“-”符号
		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18)
				+ s.substring(19, 23);
   } 
	/**
	 * 导入操作
	 */
	public void onSave(){
		boolean isDebug = true;
		try{
			TParm result=new TParm();
			TParm checkParm=null;
			TParm checkLog=null;
			int index=0;
			String type="";
			String type4="";
			if (this.getValue("TYPE").toString().equals("1")) {
				type="O";
				type4="门诊";
			}else if (this.getValue("TYPE").toString().equals("2")) {
				type="I";
				type4="住院";
			}
			//调用接口，查询HRP是否已经下载 没有下载可以操作导入功能
			switch (tabbedPane.getSelectedIndex()) {
			case 0://第一个页签 应收
				
				table=(TTable)this.getComponent("TABLE");
				result=diIncomeexpAndDiIncomerealParm(1,type);
				index=1;
				break;
			case 1://第二个页签  实收
				table=(TTable)this.getComponent("TABLE2");
				result=diIncomeexpAndDiIncomerealParm(2,type);
				index=2;
				break;
			case 2://第三个页签 预交金
				index=3;
				table=(TTable)this.getComponent("TABLE3");
				result=diIncomepreParm(type);
				break;
			case 3://第四个页签
				index=4;
				table=(TTable)this.getComponent("TABLE4");
				result=costDataParm(type);
				break;
			case 4://第五个页签 高值耗材 add by huangjw 20141112
				index=5;
				table=(TTable)this.getComponent("TABLE6");
				result=highPriceDataParm(index,type4);
				break;
			case 5://第六个页签 成本核算 add by huangjw 20150409
				index=6;
				table=(TTable)this.getComponent("TABLE6");
				result=this.costAcountParm(type);
				break;
			case 6://药品入库
				index=7;
				table=(TTable)this.getComponent("PHA_TABLE");
				if(null==this.getValue("BUSITYPE_NAME")||this.getValue("BUSITYPE_NAME").toString().length()<=0){
					this.messageBox("请选择业务类型");
					this.grabFocus("BUSITYPE_NAME");
					return;
				}
				result=diDrugsParm();
				break;
			default:
				break;
			}
			if (result.getCount()<=0) {
				this.messageBox("没有需要导入的数据");
				return;
			}
			String funation="";
			if (index>0) {
				TParm parm=new TParm();
				parm.setData("BATCHNAME",StringTool.getString(TypeTool.getTimestamp(getValue(
		        "START_DATE")), "yyyyMMdd"));//日期，删除此日期的数据
				if (this.getValue("TYPE").equals("1")) {
					parm.setData("BUSITYPENAME","门诊");
					result.setData("BUSITYPENAME_SUM","门诊");
				}else{
					parm.setData("BUSITYPENAME","住院");
					result.setData("BUSITYPENAME_SUM","住院");
				}
				if (index!=5) {
					switch(index){
					case 1: parm.setData("EXPTABLENAME","DI_INCOMEEXP".toLowerCase());//应收
						break;
					case 2: parm.setData("EXPTABLENAME","DI_INCOMEREAL".toLowerCase());//实收
						break;
					case 3: parm.setData("EXPTABLENAME","DI_INCOMEPRE".toLowerCase());//预交金
						break;
					//case 4: parm.setData("EXPTABLENAME",);
						//break;
					case 6: parm.setData("EXPTABLENAME","DI_SERVICEVOLEXP".toLowerCase());//成本核算表
						break;
					case 7: 
						parm.setData("EXPTABLENAME","DI_DRUGS".toLowerCase());//药品入库
						parm.setData("BUSITYPENAME",this.getText("BUSITYPE_NAME"));
						result.setData("BUSITYPENAME_SUM",this.getText("BUSITYPE_NAME"));
						break;
					}
					checkLog=BILFinanceTool.getInstance().checkLogDrlogAyh(parm);
					if (checkLog.getErrCode()<0) {
						System.out.println("查询中间库LOG表出现错误:::::"+checkLog.getErrText());
						this.messageBox("3333查询中间库LOG表出现错误");
						return;
					}
				}                                                                                                                                          //暂时屏蔽
				if (index==1) {			
					//parm.setData("SOURCESYSTEM","1");//外部系统，1.收入 2.成本
					//result.setData("SOURCESYSTEM_SUM","1");//外部系统，1.收入 2.成本
					checkParm=BILFinanceTool.getInstance().checkDiIncomeexp(parm);
					funation="onSaveDiIncomeexp";//应收
				}else if (index==2) {
					checkParm=BILFinanceTool.getInstance().checkDiIncomereal(parm);
					funation="onSaveDiIncomereal";//实收
				}else if(index ==3){
					if(parm.getValue("BUSITYPENAME").equals("门诊")){
						checkParm=BILFinanceTool.getInstance().checkDiIncomepreO(parm);
						
					}else{
						checkParm=BILFinanceTool.getInstance().checkDiIncomepre(parm);
					}
					
					funation="onSaveDiIncomepre";//预交金
				}else if(index ==4){//成本
					//result.setData("SOURCESYSTEM_SUM","2");//外部系统，1.收入 2.成本
					checkParm=BILFinanceTool.getInstance().checkDiIncomeexp(parm);
					funation="onSaveCost";//成本
				}else if(index==5){//高值耗材
					checkParm=BILFinanceTool.getInstance().checkDiIncomeHigh(parm);
					funation="onSaveDiIncomeHigh";//高值耗材
				}else if(index==6){//成本核算
					checkParm=BILFinanceTool.getInstance().checkDiIncomeCostAcount(parm);
					funation="onsaveDiIncomeCostAcount";
				}else if(index==7){//药品入库
					checkParm=BILFinanceTool.getInstance().checkDidrugs(parm);
					funation="onSaveDidrugs";
				}
				if (index!=5) {
					if (checkParm.getErrCode()<0) {
						this.messageBox("查询错误");
						return;
					}
					if (checkParm.getCount()>0) {
						if(tableResult.getValue("LOAD_DOWN",0).endsWith("已审核")){
							this.messageBox("数据已审核,不可重新上传");
							return;
						}
						if(JOptionPane.showConfirmDialog(null, "数据已上传,是否重新上传？", "信息",JOptionPane.YES_NO_OPTION) == 0){
								result.setData("CHECK_FLG","Y");
						}else{
							return;
						}
					}
				}
			}else{
				return;
			}
			//System.out.println("result:555555::"+result);
			result = TIOM_AppServer.executeAction(
					"action.bil.BILFinanceAction", funation, result);
			if (result.getErrCode()<0) {
				System.out.println("result::ERROR:::"+result.getErrText());
				this.messageBox("导入失败");
			}else{
				this.messageBox("导入成功");
				//onClear();
			}
			this.onQuery();
		}catch(Exception e){
			if(isDebug){
				System.out.println("come in class: BILFinanceIncomeControl ，method ：onSave");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 选择类型
	 */
	public void onSelect(){
		callFunction("UI|TYPE|setEnabled", true);
		switch (tabbedPane.getSelectedIndex()) {
		case 0://第一个页签 应收
			table=(TTable)this.getComponent("TABLE");
			if (this.getValue("TYPE").toString().equals("1")) {
				table.setHeader("序号,60;业务类型,80,ADM_TYPE;收入分类,80;病案号,80;病患名称,80;开单科室,80;开单医生,80;执行科室,80;诊区,80;票据号码,100;总金额,80; 结算编号,100;结算时间,80;收费人员,80,DR_CODE;就诊号,100;上传状态,80;审核状态,80");
				callFunction("UI|OPD_TYPE_Y|setEnabled", true);	
				callFunction("UI|OPD_TYPE_S|setEnabled", true);	
				this.setValue("OPD_TYPE_Y", "");
				//table.setParmMap("TYPE;CASE_NO;MR_NO;PAT_NAME;DEPT_DESC;DR_DESC;EXE_DEPT_DESC;CLINIC_DESC;PRINT_NO;AR_AMT;ACCOUNT_SEQ;ACCOUNT_DATE;PRINT_USER");
			}else if(this.getValue("TYPE").toString().equals("2")){
				table.setHeader("序号,60;业务类型,80;收入分类,80;病案号,80;病患名称,80;开单科室,80;开单医生,80;执行科室,80;病区,80;票据号码,100;总金额,80; 结算编号,100;结算时间,80;收费人员,80,DR_CODE;就诊号,100;上传状态,80;审核状态,80");
				//table.setParmMap("TYPE;CASE_NO;MR_NO;PAT_NAME;DEPT_DESC;DR_DESC;EXE_DEPT_DESC;CLINIC_DESC;PRINT_NO;AR_AMT;ACCOUNT_SEQ;ACCOUNT_DATE;PRINT_USER");
				callFunction("UI|OPD_TYPE_Y|setEnabled", false);	
				callFunction("UI|OPD_TYPE_S|setEnabled", false);	
			}
			this.setValue("OPD_TYPE_Y", "");
			this.setValue("OPD_TYPE_S", "");
			break;
		case 1://第二个页签  实收
			table=(TTable)this.getComponent("TABLE2");
			if (this.getValue("TYPE").toString().equals("1")) {
				table.setHeader("序号,60;业务类型,80,ADM_TYPE;支付方式,80;病案号,80;病患名称,80;开单科室,80;开单医生,80;执行科室,80;诊区,80;票据号码,100;总金额,80;日结号码,100;日结时间,80;收费人员,80,DR_CODE;就诊号,100;上传状态,80;审核状态,80");
				callFunction("UI|OPD_TYPE_Y|setEnabled", true);	
				callFunction("UI|OPD_TYPE_S|setEnabled", true);	
				//table.setParmMap("TYPE;CASE_NO;MR_NO;PAT_NAME;DEPT_DESC;DR_DESC;EXE_DEPT_DESC;CLINIC_DESC;PRINT_NO;AR_AMT;ACCOUNT_SEQ;ACCOUNT_DATE;PRINT_USER");
			}else if(this.getValue("TYPE").toString().equals("2")){
				table.setHeader("序号,60;业务类型,80;支付方式,80;病案号,80;病患名称,80;开单科室,80;开单医生,80;执行科室,80;病区,80;票据号码,100;总金额,80;日结号码,100;日结时间,80;收费人员,80,DR_CODE;就诊号,100;上传状态,80;审核状态,80");
				callFunction("UI|OPD_TYPE_Y|setEnabled", false);	
				callFunction("UI|OPD_TYPE_S|setEnabled", false);	
				//table.setParmMap("TYPE;CASE_NO;MR_NO;PAT_NAME;DEPT_DESC;DR_DESC;EXE_DEPT_DESC;CLINIC_DESC;PRINT_NO;AR_AMT;ACCOUNT_SEQ;ACCOUNT_DATE;PRINT_USER");
			}
			this.setValue("OPD_TYPE_Y", "");
			this.setValue("OPD_TYPE_S", "");
			break;
		case 2://第三个页签 预交金
//			if (!this.getValue("TYPE").equals("3")) {
//				this.messageBox("请选择住院类型");
//				this.setValue("TYPE", "3");
//				//table.setHeader("业务类型,80;支付方式,80;就诊号,100;病案号,80;病患名称,80;开单科室,80;开单医生,80;执行科室,80;诊区,80;票据号码,80;总金额,80;结算编号,80;结算时间,80;收费人员,80");
//			}
			break;
		case 4://第五个页签  高值耗材
			table=(TTable)this.getComponent("TABLE6");
			if(this.getValue("TYPE").toString().equals("1")){
				table.setHeader("选,30,boolean;序号,60;收费,30,boolean;HRP标识,50,boolean;单据号,100;条码,100;收费项目编码,100;收费项目名称,100;收据分类编码,100;收据分类名称;公司编码,60;收费金额,60,double;收费数量,60,double;收费日期,80;收费员工号,80;收费员,60;开单科室编码,100;开单科室名称,100;执行科室编码,100;执行科室名称,100;病案号,80;姓名,80;性别,60;年龄,60;患者家属,80;联系电话,100;医生姓名,80;上传状态,80;审核状态,80");
				table.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28");
			}else{
				table.setHeader("选,30,boolean;序号,60;收费,30,boolean;HRP标识,50,boolean;单据号,100;条码,100;收费项目编码,100;收费项目名称,100;收据分类编码,100;收据分类名称;公司编码,60;收费金额,60,double;收费数量,60,double;收费日期,80;收费员工号,80;收费员,60;开单科室编码,100;开单科室名称,100;执行科室编码,100;执行科室名称,100;病案号,80;姓名,80;性别,60;年龄,60;患者家属,80;联系电话,100;医生姓名,80;上传状态,80;审核状态,80;手术名称,120;手术日期,100");
				table.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30");
			}
			break;
		case 6:
			callFunction("UI|TYPE|setEnabled", false);	
			break;
		default:
			break;
		}
	}
	/**
	 * 清空
	 */
	public void onClear(){
		onPage();
		table=(TTable)this.getComponent("TABLE");
		table.setParmValue(new TParm());
		table=(TTable)this.getComponent("TABLE2");
		table.setParmValue(new TParm());
		table=(TTable)this.getComponent("TABLE3");
		table.setParmValue(new TParm());
		table=(TTable)this.getComponent("TABLE6");		
		table.setParmValue(new TParm());
		table=(TTable)this.getComponent("PHA_TABLE");
		table.setParmValue(new TParm());
		tabbedPane.setEnabledAt(3, false);
	}
	/**
	 * 页签点击事件
	 */
	public void onChangeTab() {
		onSelect();
	}
	/**
	 * 生成xml文件
	 */
//	public void onSaveXml(){
////		table=(TTable)this.getComponent("TABLE4");
////		TParm parm=table.getParmValue();
//		TParm parm=new TParm();
//		String eDate=StringTool.getString(TypeTool.getTimestamp(getValue("MONTH_DATE")), "yyyyMM");
//		int year = Integer.parseInt(eDate.substring(0, 4));
//		int month = Integer.parseInt(eDate.substring(4, 6));
//		TParm dateParm = SystemTool.getInstance().getMonthDate(year, month);
//		parm.setData("MONTH_DATE",eDate);
//		parm.setData("DAY",dateParm.getValue("LAST_DATE"));
//		//parm.setData("TYPE",this.getValue("TYPE"));
//		TParm result=BILFinanceTool.getInstance().onQueryCostXml(parm);
//		
//		if (result.getCount()<=0) {
//			this.messageBox("没有需要导出的数据");
//			return;
//		}
////		String type="";
////		switch (this.getValueInt("TYPE")){
////		case 1:
////			type="-挂号";
////			break;
////		case 2:
////			type="-门诊";
////			break;
////		case 3:
////			type="-住院";
////			break;
////		}
//		TParm headParm=new TParm();
//		headParm.addData("corp","001");
//		headParm.addData("billnodate",StringTool.getString(SystemTool.getInstance().getDate(), "yyyy-MM-dd"));//
//		headParm.addData("operator",Operator.getID());
//		if (!XMLCityInwDriver.createXMLFile(headParm, result,  "c:/分摊参数.xml")) {
//			this.messageBox("分摊参数XML文件生成失败!");
//			return;
//		}
//		this.messageBox("分摊参数XML文件生成成功,请查看C盘根目录");
//	}
	/**
	 * 分摊
	 */
	public void onApportion(){
		if (this.getValue("TYPE").toString().equals("1")) {
			this.messageBox("请选择门诊或住院类型");
			return;
		}
		TParm parm=new TParm();
		String eDate=StringTool.getString(TypeTool.getTimestamp(getValue("MONTH_DATE")), "yyyyMM");
		int year = Integer.parseInt(eDate.substring(0, 4));
		int month = Integer.parseInt(eDate.substring(4, 6));
		TParm dateParm = getMonthDate(year, month);
		parm.setData("MONTH_DATE",eDate);
		parm.setData("DAY",dateParm.getValue("LAST_DATE"));
		parm.setData("TYPE",this.getValue("TYPE"));
		TParm result=BILFinanceTool.getInstance().onApportion(parm);
		if (result.getCount()<=0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		String dataType="";
		String type="";
		String message="";
		if (this.getValue("TYPE").toString().equals("1")) {
			dataType="挂号";
		}else if (this.getValue("TYPE").toString().equals("2")) {
			dataType="门诊";
			type="O";
			message="13";
		}else if (this.getValue("TYPE").toString().equals("3")) {
			dataType="住院";
			type="I";
			message="14";
		}
		parm.setData("BATCHNAME",eDate+dateParm.getValue("LAST_DATE"));
		parm.setData("TYPE",type);
		TParm checkParm=BILFinanceTool.getInstance().checkApportion(parm);
		TParm parmValue=new TParm();
		if (checkParm.getErrCode()<0) {
			this.messageBox("查询失败");
			System.out.println("checkParm;;;;;:"+checkParm);
			return;
		}
		if (checkParm.getCount()>0) {
			if(this.messageBox("提示","存在"+eDate+"数据,是否重新导入",2)!=0){
				return;
			}else{
				parmValue.setData("CHECK_FLG","Y");
			}
		}
		for (int i = 0; i <result.getCount(); i++) {
			//date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			//checkDate=date.substring(0,8);
			parmValue.addData("PK_SERVICEVOLEXP",getUUID());//外部系统收入表主键
			//parmValue.addData("DATA_TYPE","A");
			parmValue.addData("PK_EXPLOG", "");//导出日志表主键
			parmValue.addData("BATCHNAME", eDate+dateParm.getValue("LAST_DATE"));//批标记建议用"YYYYMMDD"
			parmValue.addData("EXPTIME","000000");//导出时间建议用“HHMMSS”
			parmValue.addData("CYEAR", eDate.substring(0,4));//年度
			parmValue.addData("CMONTH", eDate.substring(4,6));//月份
			parmValue.addData("CDAY", eDate+dateParm.getValue("LAST_DATE")+"000000");//日期
			parmValue.addData("PK_CORP", "001");//公司主键--可选编码，名称，pk,推荐名称
			parmValue.addData("CORPNAME", "西安交通大学第二附属医院");//公司名称--可选编码，名称，pk,推荐名称
			parmValue.addData("PK_ITEM", result.getValue("TYPE_ID",i));//收入主键
			parmValue.addData("ITEMNAME", result.getValue("TYPE",i));//收入名称
			parmValue.addData("PK_DIAGNOSISDEPT_TWO", result.getValue("DEPT_CODE",i));//开单科室主键
			parmValue.addData("DIAGNOSISDEPTNAME_TWO", result.getValue("DEPT_DESC",i));//开单科室名称
			parmValue.addData("VOLUME",  result.getDouble("NUM",i));//服务量
			parmValue.addData("SOURCESYSTEM", message);//外部数据来源系统
			parmValue.addData("PK_OUTERSYSTEM", dataType);//外系统主键
			parmValue.addData("PK_MEASURE", "1");
			parmValue.addData("MEASURENAME", "人次");
			parmValue.addData("TYPE", type);//类型
		}
		parmValue.setData("CHEKC_DATE",eDate+dateParm.getValue("LAST_DATE"));
		parmValue.setData("TYPE_SUM",type);
		parmValue.setCount(result.getCount());
		result = TIOM_AppServer.executeAction(
				"action.bil.BilAction", "onSaveApportion", parmValue);
		if (result.getErrCode()<0) {
			this.messageBox("导入失败");
		}else{
			this.messageBox("导入成功");
		}
	}
	/**
     * 汇出Excel
     */
    public void onExcel() {
    	if(table.getRowCount()<=0){
    		this.messageBox("没有汇出数据");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, tableName);
    }
    /**
     * 获得一个月的最后一天几号\星期几 \第一天星期
     * @param year
     * @param month
     * @return parm 参数 返回值 1.LAST_DATE最后一天几号 2.LAST_DAY 最后一天星期 3.FIRST_DAY 第一天星期
     */
  	private TParm getMonthDate(int year, int month) {
  		// 不加下面2行，就是取当前时间前一个月的第一天及最后一天
  		Calendar cal = Calendar.getInstance();
  		cal.set(Calendar.YEAR, year);
  		cal.set(Calendar.MONTH, month);
  		cal.set(Calendar.DAY_OF_MONTH, 1);
  		cal.add(Calendar.DAY_OF_MONTH, -1);
  		Date lastDate = cal.getTime();
  		cal.set(Calendar.DAY_OF_MONTH, 1);
  		Date firstDate = cal.getTime();
  		TParm parm = new TParm();
  		parm.setData("LAST_DATE", lastDate.getDate());// 最后一天几号
  		parm.setData("LAST_DAY", lastDate.getDay());// 最后一天星期
  		parm.setData("FIRST_DAY", firstDate.getDay());// 第一天星期
  		return parm;
  	}
  	public void onSel(){
  		TTable table=(TTable)this.getComponent("TABLE6");
  		TParm tableParm =table.getParmValue();
  		if (tableParm.getCount()<=0) {
			this.messageBox("没有需要操作的数据");
			return;
		}
  		TCheckBox chkSel=(TCheckBox)this.getComponent("CHK_SEL");
  		if (chkSel.isSelected()) {
  			for (int i = 0; i < tableParm.getCount(); i++) {
  				if (tableParm.getValue("UP_LOAD",i).equals("未上传")) {
  					tableParm.setData("FLG",i,"Y");
  				}
  			}
		}else{
			for (int i = 0; i < tableParm.getCount(); i++) {
  				if (tableParm.getValue("UP_LOAD",i).equals("未上传")) {
  					tableParm.setData("FLG",i,"N");
  				}
  			}
		}
  		
  		table.setParmValue(tableParm);
  	}
  	
  	/**
	 * table checkbox监听事件
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onCheckBox(Object obj) {
		TTable chargeTable = (TTable) obj;
		chargeTable.acceptText();
		TTable table=(TTable)this.getComponent("TABLE6");
		int col = table.getSelectedColumn();
		String columnName = table.getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		if ("FLG".equals(columnName)) {
			if (chargeTable.getParmValue().getValue("UP_LOAD",row).equals("未上传")) {
				if(chargeTable.getParmValue().getValue("FLG",row).equals("Y")){
					chargeTable.getParmValue().setData("FLG",row,"Y");
				}else{
					chargeTable.getParmValue().setData("FLG",row,"N");
				}
			}else{
				this.messageBox("已上传不可以操作");
				chargeTable.getParmValue().setData("FLG",row,"N");
			}
		}
		table.setParmValue(chargeTable.getParmValue());
		return true;
	}
	public void onBusitype(){
		this.setValue("DEPT_CODE", "");
		this.setValue("SUP_CODE", "");
		if(this.getText("BUSITYPE_NAME").equals("药品采购入库")){
			callFunction("UI|DEPT_CODE|setEnabled", false);	
			callFunction("UI|SUP_CODE|setEnabled", true);
		}else if(this.getText("BUSITYPE_NAME").equals("药品消耗")){
			callFunction("UI|DEPT_CODE|setEnabled", true);	
			callFunction("UI|SUP_CODE|setEnabled", false);	
		}else if(this.getText("BUSITYPE_NAME").equals("科室领用")){
			callFunction("UI|DEPT_CODE|setEnabled", false);	
			callFunction("UI|SUP_CODE|setEnabled", false);	
		}
	}
}