package com.javahis.ui.ibs;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import jdo.adm.ADMInpTool;
import jdo.bil.BILLumpWorkTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

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
import com.javahis.manager.sysfee.sysOdrPackDObserver;
/**
 * <p>
 * Title: 包干套餐费用审核控制类
 * </p>
 * 
 * <p>
 * Description:  包干套餐费用审核控制类
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author liling 20140513
 * @version 1.0
 */
public class IBSLumpworkCheckControl extends TControl {
	String caseNo;
	String billStatus;
	String ctz1Code;
	String ctz2Code;
	String ctz3Code;
	String admDate;
	//TCheckBox tbk;
	private TTable table;
	//===start===add by kangy 20160907
	//private int row=0;
	//private TParm tableXParm=new TParm();
	private TCheckBox checkedAll;
	private TTable lumpTABLE;
	private TTable tableX;
	//===end====add by kangy  20160907
	public IBSLumpworkCheckControl()
	{
	}
   public void onInit() {
       super.onInit();
//       ( (TTable) getComponent("lumpTABLE")).addEventListener("TABLE->"
//	            + TTableEvent.CLICKED, this, "onTABLEClicked");
       //row =0;
       checkedAll=(TCheckBox) getComponent("CHECKEDALL");
		// 只有text有这个方法，调用sys_fee弹出框
		callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
				"%ROOT%\\config\\sys\\SYSFeePopup.x");//医嘱代码
		// 接受回传值
		callFunction("UI|ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		table=(TTable) getComponent("TABLE_SUM");
		lumpTABLE=(TTable) getComponent("lumpTABLE");
		tableX=(TTable) getComponent("TABLEX");
		table.addEventListener("TABLE_SUM->"+ TTableEvent.CLICKED, this, "onTABLEClicked");
		table.addEventListener("lumpTABLE->"+ TTableEvent.RIGHT_CLICKED, this, "onRightClicked");
		TParm initParm = new TParm();
		Object obj = this.getParameter();
		if (obj == null)
			return;
		if (obj != null) {
			initParm = (TParm) obj;
		}
		//tbk=(TCheckBox)this.getComponent("CHK_SEL");
		caseNo = initParm.getData("IBS", "CASE_NO").toString();
		billStatus = initParm.getData("IBS", "BILL_STATUS").toString();//===yanjing 20150107
		admDate=initParm.getData("ADM","ADM_DATE").toString();
	//	TParm parm =new TParm(TJDODBTool.getInstance().update("UPDATE IBS_ORDD SET INCLUDE_FLG ='N' WHERE  CASE_NO = '"+caseNo+"' AND INCLUDE_FLG is null "));
	//	mrNo = initParm.getData("IBS", "MR_NO").toString();
		 ( (TTable) getComponent("lumpTABLE")).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
			"onCheckBox");
		 //tbk.setEnabled(false);
		 onClear();
   }
   /**
	 * 处理当前TOOLBAR
	 */
	public void onShowWindowsFunction() {
		// 显示UIshowTopMenu
		callFunction("UI|showTopMenu");
	}
   /**
	 * 费用代码下拉列表选择
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
		onQuery();
		// this.grabFocus("ORDER_DESC");
	}
   /**
	 * 清空
	 * */
	public void onClear(){
		//tableXParm=new TParm();
		clearValue("START_DATE;END_DATE;IBS_INCLUDE_FLG;ORDER_CAT1_CODE;PACK_DESC;ORDER_CODE;ORDER_DESC;ADM_INCLUDE_FLG;OUT_LUMPWORK_AMT;COST_CENTER_CODE");
		//TTable table = (TTable) this.getComponent("lumpTABLE");
		lumpTABLE.removeRowAll();
        //add by kangy start 20160907
        //TTable tableX = (TTable) this.getComponent("TABLEX");
        tableX.removeRowAll();
        //add by kangy end 20160907
 		Timestamp date = StringTool.getTimestamp(new Date());
		// 初始化查询区间
		this.setValue("END_DATE",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 23:59:59");
		this.setValue("START_DATE", admDate
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		TParm parm=ADMInpTool.getInstance().onCheckLumWorkNew(caseNo);//D.PACKAGE_DESC
		String lumpDesc=(String) parm.getData("PACKAGE_DESC", 0);
		String admInflg=(String) parm.getData("INCLUDE_FLG", 0);
		ctz1Code=parm.getValue("CTZ1_CODE",0);
		ctz2Code=parm.getValue("CTZ2_CODE",0);
		ctz3Code=parm.getValue("CTZ3_CODE",0);
		//this.messageBox(lumpCode);
		if (lumpDesc != null && lumpDesc.toString().length() != 0) {
			this.setValue("PACK_DESC", lumpDesc);	
		}
		if (admInflg != null && admInflg.toString().length() != 0) {
			this.setValue("ADM_INCLUDE_FLG", admInflg);	
		}
	}
	
	/**
	 * 查询
	 */
	public void onQuery() {
		//tableXParm=new TParm();
		TParm parm = new TParm();
//		caseNo="140512000002";
		parm.setData("CASE_NO", caseNo);
		String where="";
		lumpTABLE.setParmValue(new TParm());
		//this.callFunction("UI|TABLEX|setParmValue", new TParm());
		// 校验开始日期
		if (getValue("START_DATE") != null) {
			Timestamp start = (Timestamp) getValue("START_DATE");
			String startDate = StringTool.getString(start, "yyyyMMdd HH:mm:ss");
			parm.setData("START_DATE", startDate);
		}
		
		// 校验结束日期
		if (getValue("END_DATE") != null) {
			Timestamp end = (Timestamp) getValue("END_DATE");
			String endDate = StringTool.getString(end, "yyyyMMdd HH:mm:ss");
			parm.setData("END_DATE", endDate);
		}
		String sql = "SELECT D.INCLUDE_FLG, D.ORDER_CODE,S.ORDER_DESC ,"
		+ " SUM(D.DOSAGE_QTY) DOSAGE_QTY,D.DOSAGE_UNIT,"
		+          "D.OWN_PRICE, SUM(D.TOT_AMT) TOT_AMT,D.ORDERSET_CODE"
		+ " FROM IBS_ORDD  D,SYS_FEE S "//PHA_BASE  P,SYS_CTRLDRUGCLASS C,
		+ " WHERE  S.ORDER_CODE = D.ORDER_CODE " +
				"  AND (D.ORDERSET_CODE IS NULL OR D.ORDERSET_CODE=D.ORDER_CODE) AND D.CASE_NO = '"+ parm.getData("CASE_NO") + "' " 
		+ 		  " AND ( d.bill_date >= TO_DATE('" + parm.getData("START_DATE")+ "', 'YYYYMMDD HH24:MI:SS') "
		+         " AND d.bill_date <= TO_DATE('" + parm.getData("END_DATE") + "', 'YYYYMMDD HH24:MI:SS')) ";
		String sqlSum="SELECT SUM(TOT_AMT) TOT_AMT,INCLUDE_FLG FROM IBS_ORDD  D WHERE D.CASE_NO = '"+
		parm.getData("CASE_NO") + "' AND d.bill_date BETWEEN TO_DATE('" + parm.getData("START_DATE")+ 
		"', 'YYYYMMDD HH24:MI:SS') AND TO_DATE('" + parm.getData("END_DATE") + "', 'YYYYMMDD HH24:MI:SS') ";
		//校验医嘱代码
		parm.setData("ORDER_CODE", getValue("ORDER_CODE"));
		if (parm.getData("ORDER_CODE") != null
				&& parm.getData("ORDER_CODE").toString().length() != 0) {
			where += " AND D.ORDER_CODE='"+parm.getData("ORDER_CODE")+"' ";
		}
		//校验医嘱分类
		parm.setData("order_cat1_code", this.getValueString("ORDER_CAT1_CODE"));
		if (parm.getData("order_cat1_code") != null
				&& parm.getData("order_cat1_code").toString().length() != 0) {
			where += " AND D.ORDER_CAT1_CODE='"
					+ parm.getData("order_cat1_code") + "'";
		}
		//校验包干外复选框
		if(getValue("IBS_INCLUDE_FLG").equals("Y")){//包干外
			where +=" AND   D.INCLUDE_FLG  ='Y' ";//在住院登记时，选择了套餐类型（包干套餐）的患者，默认本次住院期间费用全部为包干内项目。选择出包干外的项目进行审核
		}
		//成本中心COST_CENTER_CODE
		parm.setData("COST_CENTER_CODE", this.getValueString("COST_CENTER_CODE"));
		if (parm.getData("COST_CENTER_CODE") != null
				&& parm.getData("COST_CENTER_CODE").toString().length() != 0) {
			where += " AND D.EXE_DEPT_CODE='"
					+ parm.getData("COST_CENTER_CODE") + "'";
		}
		TParm includeParm=new TParm(TJDODBTool.getInstance().select(sqlSum+where+ "GROUP BY INCLUDE_FLG"));
		//int Count = selectparm.getCount("TOT_AMT");
		double totFee = 0.00;
		double outFee = 0.00;
		for (int i = 0; i < includeParm.getCount(); i++) {
			if(includeParm.getValue("INCLUDE_FLG", i).equals("Y")){
				outFee=includeParm.getDouble("TOT_AMT",i);
			}else{
				totFee=includeParm.getDouble("TOT_AMT",i);
			}
		}
		setValue("TOT_AMT", totFee);//套餐总金额
		setValue("OUT_LUMPWORK_AMT", outFee);//包干外金额
		TParm selectparm = new TParm(TJDODBTool.getInstance().select("SELECT * FROM ("+sql+where+"GROUP BY D.INCLUDE_FLG, D.ORDER_CODE,S.ORDER_DESC ,"
		+ " D.OWN_PRICE,D.DOSAGE_UNIT,D.ORDERSET_CODE) WHERE DOSAGE_QTY<>0 ORDER BY INCLUDE_FLG"));
		if (selectparm.getCount("ORDER_CODE") <= 0) {
			// 查无资料
			this.messageBox("E0008");
			TParm newNarm = new TParm();
			table.setParmValue(newNarm);
			return;
		}
		String orderDetailSql="SELECT SUM(TOT_AMT) TOT_AMT,SUM(OWN_AMT) OWN_AMT FROM IBS_ORDD D WHERE CASE_NO='"+parm.getData("CASE_NO")+"'";
		String orderSql="";
		TParm orderParm=null;
		//集合医嘱汇总总金额
		for (int i = 0; i < selectparm.getCount("ORDER_CODE"); i++) {
			if (selectparm.getValue("ORDER_CODE",i).equals(selectparm.getValue("ORDERSET_CODE",i))) {
				orderSql=orderDetailSql+" AND D.ORDERSET_CODE='"+selectparm.getValue("ORDER_CODE",i)+
				 		  "' AND D.INCLUDE_FLG='"+selectparm.getValue("INCLUDE_FLG",i)+"' AND ( d.bill_date >= TO_DATE('" + parm.getData("START_DATE")+ "', 'YYYYMMDD HH24:MI:SS') "
				+         " AND d.bill_date <= TO_DATE('" + parm.getData("END_DATE") + "', 'YYYYMMDD HH24:MI:SS')) "+where;
				orderParm=new TParm(TJDODBTool.getInstance().select(orderSql));
				selectparm.setData("TOT_AMT",i,orderParm.getDouble("TOT_AMT",0));
				selectparm.setData("OWN_PRICE",i,orderParm.getDouble("OWN_AMT",0)/selectparm.getDouble("DOSAGE_QTY",i));
			}
		}
		selectparm.setCount(selectparm.getCount("ORDER_CODE"));
		this.callFunction("UI|TABLE_SUM|setParmValue", selectparm);
	}
	/**
	 * 
	* @Title: filterDeleteParm
	* @Description: TODO(过滤删除医嘱)
	* @author pangben
	* @throws
	 */
	private void filterDeleteParm(TParm deleteParm,TParm selectparm){
//		if(deleteParm.getCount("ORDER_CODE")>0){
//			double ownPrice=0.00;
//			double exeOwnPrice=0.00;
//			double deleteDosageQty=0.00;
//			for (int j = selectparm.getCount("ORDER_CODE")-1; j >= 0; j--) {
//				exeOwnPrice=StringTool.round(selectparm.getDouble("TOT_AMT",j)/selectparm.getDouble("DOSAGE_QTY",j), 2);
//				for (int i = deleteParm.getCount("ORDER_CODE")-1; i >=0; i--) {
//					if (deleteParm.getValue("ORDER_CODE",i)
//							.equals(selectparm.getValue("ORDER_CODE",j))){
//						ownPrice=StringTool.round(deleteParm.getDouble("TOT_AMT",i)/deleteParm.getDouble("DOSAGE_QTY",i), 2);
//						deleteDosageQty=Math.abs(deleteParm.getDouble("DOSAGE_QTY",i));
//						if (Math.abs(ownPrice)==exeOwnPrice) {
//							if (deleteDosageQty-selectparm.getDouble("DOSAGE_QTY",j)>=0) {
//								deleteDosageQty=deleteDosageQty-selectparm.getDouble("DOSAGE_QTY",j);
//								selectparm.removeRow(j);
//							}
//							if (deleteDosageQty==0) {
//								deleteParm.removeRow(i);
//								break;
//							}
//						}
//					}
//				}
//			}
//		}
		for (int i = 0; i < deleteParm.getCount("ORDER_CODE"); i++) {
			selectparm.addRowData(deleteParm, i);
		}
		//selectparm.setCount(selectparm.getCount("ORDER_CODE"));
//		if (deleteParm.getCount("ORDER_CODE")>0) {
//			filterDeleteParm(deleteParm, selectparm);
//		}
	}
	/** 
	* 精确的加法运算. 
	*/ 
	public static double add(double v1, double v2) { 
	BigDecimal b1 = new BigDecimal(v1); 
	BigDecimal b2 = new BigDecimal(v2); 
	return b1.add(b2).doubleValue(); 
	} 
//   /**
//    *增加对TABLE的监听
//    * @param row int
//    */
    public void onTABLEClicked(int row) {
   	 // 选中行
       if (row < 0)
           return;
        TParm data =table.getParmValue().getRow(row);
		String where="";
		String sql = "SELECT  'N' FLG,D.INCLUDE_FLG,D.BILL_DATE, D.ORDER_CODE," 
			+ "S.ORDER_DESC || CASE  WHEN S.SPECIFICATION IS NOT NULL THEN  '(' || S.SPECIFICATION || ')'  ELSE  ''   END AS ORDER_DESC,"
		+ " D.OWN_FLG, D.DOSAGE_QTY,D.DOSAGE_UNIT,"
		+          "D.OWN_PRICE, D.TOT_AMT ,X.COST_CENTER_CHN_DESC COST_CENTER_CODE, D.OPT_DATE,"
		+          "U.USER_NAME, D.INDV_FLG,D.ORDERSET_GROUP_NO,"
		+          " D.ORDER_NO,D.ORDERSET_CODE, D.CASE_NO,D.CASE_NO_SEQ,D.SEQ_NO,D.ORDER_SEQ,D.OWN_RATE  "
		+ " FROM IBS_ORDD  D,SYS_FEE S,SYS_OPERATOR  U,SYS_COST_CENTER X "//PHA_BASE  P,SYS_CTRLDRUGCLASS C,
		+ " WHERE S.ORDER_CODE = D.ORDER_CODE AND U.USER_ID = D.OPT_USER " +
				" AND D.EXE_DEPT_CODE = X.COST_CENTER_CODE AND (D.ORDERSET_CODE IS NULL OR D.ORDERSET_CODE=D.ORDER_CODE) AND D.CASE_NO = '"+ caseNo + "' " 
		+ 		  " AND  D.ORDER_CODE='"+data.getValue("ORDER_CODE")+"' AND  D.INCLUDE_FLG  ='"+data.getValue("INCLUDE_FLG")+"'";
			//校验医嘱代码
		String unSql=sql+" AND DOSAGE_QTY<0 "+where+ " ORDER BY D.CASE_NO_SEQ, D.SEQ_NO ";
		sql +=" AND DOSAGE_QTY>=0 "+where+ " ORDER BY D.CASE_NO_SEQ, D.SEQ_NO ";
		TParm selectparm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selectparm.getCount("BILL_DATE") <= 0) {
			// 查无资料
			this.messageBox("E0008");
			TParm newNarm = new TParm();
			lumpTABLE.setParmValue(newNarm);
			return;
		}
		String orderDetailSql="SELECT SUM(TOT_AMT) TOT_AMT,SUM(OWN_AMT) OWN_AMT FROM IBS_ORDD WHERE CASE_NO='"+caseNo+"'";
		String orderSql="";
		TParm orderParm=null;
		//集合医嘱汇总总金额
		for (int i = 0; i < selectparm.getCount("BILL_DATE"); i++) {
			if (selectparm.getValue("ORDER_CODE",i).equals(selectparm.getValue("ORDERSET_CODE",i))
					&&selectparm.getValue("INDV_FLG",i).equals("N")) {
				orderSql=orderDetailSql+" AND CASE_NO_SEQ='"+selectparm.getValue("CASE_NO_SEQ",i)+
				"' AND ORDERSET_CODE='"+selectparm.getValue("ORDERSET_CODE",i)+"' AND ORDERSET_GROUP_NO='"+selectparm.getValue("ORDERSET_GROUP_NO",i)+"'";
				orderParm=new TParm(TJDODBTool.getInstance().select(orderSql));
				selectparm.setData("TOT_AMT",i,orderParm.getDouble("TOT_AMT",0));
				selectparm.setData("OWN_PRICE",i,orderParm.getDouble("OWN_AMT",0)/selectparm.getDouble("DOSAGE_QTY",i));
			}
		}
		//添加查询负的数据将重复的数据过滤pangben 2015-10-19
		TParm selectUnParm= new TParm(TJDODBTool.getInstance().select(unSql));
		//System.out.println("selectUnParm::::"+selectUnParm);
		//集合医嘱汇总总金额
		for (int i = 0; i < selectUnParm.getCount("BILL_DATE"); i++) {
			if (selectUnParm.getValue("ORDER_CODE",i).equals(selectUnParm.getValue("ORDERSET_CODE",i))
					&&selectUnParm.getValue("INDV_FLG",i).equals("N")) {
				orderSql=orderDetailSql+" AND CASE_NO_SEQ='"+selectUnParm.getValue("CASE_NO_SEQ",i)+
				"' AND ORDERSET_CODE='"+selectUnParm.getValue("ORDERSET_CODE",i)+"' AND ORDERSET_GROUP_NO='"+selectUnParm.getValue("ORDERSET_GROUP_NO",i)+"'";
				orderParm=new TParm(TJDODBTool.getInstance().select(orderSql));
				selectUnParm.setData("TOT_AMT",i,orderParm.getDouble("TOT_AMT",0));
				selectUnParm.setData("OWN_PRICE",i,orderParm.getDouble("OWN_AMT",0)/Math.abs(selectUnParm.getDouble("DOSAGE_QTY",i)));
			}
		}
		for (int i = selectUnParm.getCount("BILL_DATE"); i >=0; i--) {
			for (int j = selectparm.getCount("BILL_DATE")-1; j >=0; j--) {
				if (selectUnParm.getValue("ORDER_CODE",i)
						.equals(selectparm.getValue("ORDER_CODE",j))&&
							Math.abs(selectUnParm.getDouble("TOT_AMT",i))==
								selectparm.getDouble("TOT_AMT",j)&&
								Math.abs(selectUnParm.getDouble("OWN_PRICE",i))==
									selectparm.getDouble("OWN_PRICE",j)&&
									selectparm.getValue("INCLUDE_FLG",j).
									equals(selectUnParm.getValue("INCLUDE_FLG",i))&&selectUnParm.getDouble("OWN_RATE",i)==
										selectparm.getDouble("OWN_RATE",j)) {
					selectparm.removeRow(j);
					selectUnParm.removeRow(i);
					break;
				}
			}
		}
		//作废数据中存在汇总的医嘱，需要在正常医嘱中去掉
		filterDeleteParm(selectUnParm, selectparm);
		callFunction("UI|print|setEnabled", true);
		selectparm.setCount(selectparm.getCount("ORDER_CODE"));
		//System.out.println("selectparm:::::"+selectparm);
		//this.callFunction("UI|lumpTABLE|setParmValue", selectparm);
		TParm tableXParm=tableX.getParmValue(); 
		if (null!=tableXParm && tableXParm.getCount()>0) {//出发单击事件获得医嘱明细 根据右面表格同步左下表格勾选操作
			for(int i=0;i<selectparm.getCount("ORDER_CODE");i++){
				for(int j=0;j<tableXParm.getCount("ORDER_CODE");j++){
					if(selectparm.getValue("CASE_NO",i).equals(tableXParm.getValue("CASE_NO",j))
							&&selectparm.getValue("CASE_NO_SEQ",i).equals(tableXParm.getValue("CASE_NO_SEQ",j))
							&&selectparm.getValue("SEQ_NO",i).equals(tableXParm.getValue("SEQ_NO",j))){
						selectparm.setData("FLG", i, "Y");
					}
				}
			}
		}
		//System.out.println("dddddd:::::"+selectparm);
		lumpTABLE.setParmValue(selectparm);
    }
	/**
     * 保存
     */
    public void onSave() {
    	//((TTable) getComponent("lumpTABLE")).acceptText();
    	//TParm resultParm=( (TTable) getComponent("lumpTABLE")).getParmValue();
    	tableX.acceptText();
    	TParm resultParm=tableX.getParmValue();
    	TParm exeParm=new TParm();
    	boolean flg=false;
    	for (int i = 0; i < resultParm.getCount("ORDER_CODE"); i++) {
			if (resultParm.getValue("FLG",i).equals("Y")) {
				exeParm.addRowData(resultParm, i);
				flg=true;
			}
		}
    	if (!flg) {
			this.messageBox("请在选中表中勾选需要操作的数据");
			return;
		}
    	String sql="SELECT CASE_NO FROM IBS_ORDD WHERE CASE_NO='"+caseNo+"' AND INCLUDE_EXEC_FLG='N'";
    	TParm selectParm= new TParm(TJDODBTool.getInstance().select(sql));
    	if (selectParm.getCount("CASE_NO")>0) {
			this.messageBox("存在未执行的医嘱，请操作套餐批次执行");
			table.setParmValue(new TParm());
			lumpTABLE.setParmValue(new TParm());
			return;
		}
    	//System.out.println("exeParm::::::"+exeParm);
    	exeParm.setData("CTZ1_CODE",ctz1Code);
    	exeParm.setData("CTZ2_CODE",ctz2Code);
    	exeParm.setData("CTZ3_CODE",ctz3Code);
    	exeParm.setData("ADM_INCLUDE_FLG",this.getValue("ADM_INCLUDE_FLG"));
    	exeParm.setData("OPT_USER",Operator.getID());
    	exeParm.setData("OPT_TERM",Operator.getIP());
		exeParm.setData("CASE_NO",caseNo);
		sql="SELECT LUMPWORK_RATE,LUMPWORK_CODE,SERVICE_LEVEL FROM ADM_INP WHERE CASE_NO='"+caseNo+"'";
		selectParm= new TParm(TJDODBTool.getInstance().select(sql));
		if (null==selectParm.getValue("LUMPWORK_RATE",0)||
				selectParm.getValue("LUMPWORK_RATE",0).length()<=0||selectParm.getDouble("LUMPWORK_RATE",0)==0.00){
			this.messageBox("此病患未设置套餐折扣,不可以操作");
			return;
		}
			
		exeParm.setData("FLG","Y");
		exeParm.setData("LUMPWORK_RATE",selectParm.getDouble("LUMPWORK_RATE",0));
		exeParm.setData("LUMPWORK_CODE",selectParm.getValue("LUMPWORK_CODE",0));
		exeParm.setData("SERVICE_LEVEL",selectParm.getValue("SERVICE_LEVEL",0));
		TParm result = TIOM_AppServer.executeAction(
				"action.ibs.IBSAction", "onSaveLumpworkCheck", exeParm);
		if (result.getErrCode()<0) {
			this.messageBox("操作失败:"+result.getErrText());
			//onQuery();
		}else{
			this.messageBox("P0001");
			//tableXParm=new TParm();
			onClear();
			onQuery();
		}
    }
	
    /**
	 * 集合医嘱如果选中主项细项必须也被选中
	 * 
	 * @param obj
	 * @return
	 */
    public boolean onCheckBox(Object obj) {
//		this.messageBox(" xxxxxxx");
    	//=====yanjing 20150107 add 校验
    	if(billStatus.equals("3")){
    		this.messageBox("费用已审核，不可再修改");
    		return false;
    	}else if(billStatus.equals("4")){
    		this.messageBox("出院已结算，不可再修改");
    		return false;
    	}
		TTable tables = (TTable) obj;
		TParm tableParm=lumpTABLE.getParmValue();
		TParm resultParm=tableParm.getRow(tables.getSelectedRow());
		int col = tables.getSelectedColumn();
		tables.acceptText();
		TParm exeParm=new TParm();
    	String	columnName =  tables.getDataStoreColumnName(col);
    	//System.out.println("DDDD::::s::::::::::"+resultParm);
		if(!onCheckExeOrder(tableParm,columnName, resultParm,exeParm,true)){
			this.messageBox(exeParm.getValue("MESSAGE"));
			tableParm.setData("FLG",tables.getSelectedRow(),"N");
			tables.setParmValue(tableParm);
			return false;
		}
		choiceLumpTABLE();
		
		return true;
	}
    private boolean onCheckExeOrder(TParm tableParm,String columnName,TParm resultParm,TParm exeParm,boolean tableFlg){
    	String sql="";
    	String message="";
    	boolean flg=true;
		if ("FLG".equals(columnName)&&resultParm.getValue("FLG").equals("N")) {
			if (resultParm.getValue("ORDER_CODE").equals(resultParm.getValue("ORDERSET_CODE"))
					&&resultParm.getValue("INDV_FLG").equals("N")) {//集合医嘱校验
				if (resultParm.getDouble("DOSAGE_QTY")<0) {
					exeParm.setData("MESSAGE","不可以操作数量小于0的医嘱");
					flg=false;
					return false;
				}else{
					sql="SELECT A.TOT_AMT,B.DOSAGE_QTY FROM (SELECT SUM(TOT_AMT) TOT_AMT,ORDERSET_CODE FROM IBS_ORDD WHERE CASE_NO='"+
					caseNo+"' AND ORDERSET_CODE='"+resultParm.getValue("ORDER_CODE")
					+"' AND ORDERSET_GROUP_NO='"+resultParm.getValue("ORDERSET_GROUP_NO")+"' AND INCLUDE_FLG='"+
					resultParm.getValue("INCLUDE_FLG")+"' AND OWN_RATE="+resultParm.getDouble("OWN_RATE")+" GROUP BY ORDERSET_CODE) A," +
							" (SELECT SUM(DOSAGE_QTY) DOSAGE_QTY,ORDER_CODE FROM IBS_ORDD WHERE CASE_NO='"+
					caseNo+"' AND ORDER_CODE='"+resultParm.getValue("ORDER_CODE")
					+"' AND ORDERSET_GROUP_NO='"+resultParm.getValue("ORDERSET_GROUP_NO")+"' AND INCLUDE_FLG='"+
					resultParm.getValue("INCLUDE_FLG")+"' AND OWN_RATE="+resultParm.getDouble("OWN_RATE")+" GROUP BY ORDER_CODE) B" +
							" WHERE A.ORDERSET_CODE=B.ORDER_CODE";	
				}
			}else{
				if (resultParm.getDouble("DOSAGE_QTY")<0) {
					exeParm.setData("MESSAGE","不可以操作数量小于0的医嘱");
					flg=false;
					return false;
				}else{
					sql="SELECT SUM(DOSAGE_QTY) DOSAGE_QTY,SUM(TOT_AMT) TOT_AMT FROM IBS_ORDD WHERE CASE_NO='"+
					caseNo+"' AND ORDER_CODE='"+resultParm.getValue("ORDER_CODE")
					+"' AND INCLUDE_FLG='"+
					resultParm.getValue("INCLUDE_FLG")+"'  AND OWN_PRICE = "+resultParm.getDouble("OWN_PRICE")+" AND OWN_RATE="+resultParm.getDouble("OWN_RATE");
				}
			}
			System.out.println("sql::55555555555555pangben ::"+sql);
			TParm checkParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (checkParm.getDouble("DOSAGE_QTY",0)<=0) {
				exeParm.setData("MESSAGE","操作医嘱总数量小于0不可以操作");
				flg=false;
				return false;
			}
			
			double ownPrice=StringTool.round(checkParm.getDouble("TOT_AMT",0)/checkParm.getDouble("DOSAGE_QTY",0), 2);
			double totAmt=0.00;
			double dosageQty=0.00;
			for (int i = 0; i < tableParm.getCount("ORDER_CODE"); i++) {
				if (tableFlg) {
					if (tableParm.getValue("FLG",i).equals("Y")&&
							resultParm.getValue("INCLUDE_FLG").equals(tableParm.getValue("INCLUDE_FLG",i))&&
							resultParm.getValue("ORDER_CODE").equals(tableParm.getValue("ORDER_CODE",i))&&
							resultParm.getDouble("OWN_RATE")==tableParm.getDouble("OWN_RATE",i) &&
							resultParm.getDouble("OWN_PRICE")==tableParm.getDouble("OWN_PRICE",i)) {//选中校验
						totAmt+=tableParm.getDouble("TOT_AMT",i);
						dosageQty+=tableParm.getDouble("DOSAGE_QTY",i);
					}
				}else{
					if (resultParm.getValue("INCLUDE_FLG").equals(tableParm.getValue("INCLUDE_FLG",i))&&
							resultParm.getValue("ORDER_CODE").equals(tableParm.getValue("ORDER_CODE",i))) {//选中校验
						totAmt+=tableParm.getDouble("TOT_AMT",i);
						dosageQty+=tableParm.getDouble("DOSAGE_QTY",i);
					}
				}
			}
			totAmt=StringTool.round(totAmt, 2);
			if (StringTool.round(checkParm.getDouble("TOT_AMT",0), 2)<totAmt) {
				exeParm.setData("MESSAGE","已经超过总金额,不可以操作");
				flg=false;
				return false;
			}else{
				double exePrice=StringTool.round(totAmt/dosageQty, 2);
				if (exePrice!=0 && ownPrice%exePrice!=0) {
					exeParm.setData("MESSAGE","当前操作医嘱折扣比例不匹配,不可以操作");
					flg=false;
					return false;
				}
			}
			
		}
		if (!flg) {
			exeParm.setData("MESSAGE",message);
			return false;
		}
		return true;
    }
    /**
     * 全选
    * @Title: onSelSum
    * @Description: TODO(这里用一句话描述这个方法的作用)
    * @author Dangzhang
    * @throws
     */
//    public void onSelSum(){
//    	TParm resultParm=( (TTable) getComponent("lumpTABLE")).getParmValue();
//    	if (resultParm.getCount()<=0) {
//			this.messageBox("没有需要操作的数据");
//			return;
//		}
//    	TParm exeParm=new TParm();
//    	TParm tableParm=( (TTable) getComponent("lumpTABLE")).getParmValue();
////    	for (int i = 0; i <tableParm.getCount(); i++) {
////    		tableParm.setData("FLG", i,"Y");
////		}
//    	for (int i = 0; i < resultParm.getCount(); i++) {
//    		if (tbk.isSelected()) {
//    			resultParm.getRow(i).setData("FLG",i,"N");
//    			if(!onCheckExeOrder(resultParm, "FLG", resultParm.getRow(i), exeParm,true)){
//    				System.out.println(""+exeParm.getValue("MESSAGE"));
//        			resultParm.setData("FLG",i,"N");
//        		}else{
//            		resultParm.setData("FLG",i,"Y");
//        		}
//			}else{
//				resultParm.setData("FLG",i,"N");
//			}
//		}
//    	( (TTable) getComponent("lumpTABLE")).setParmValue(resultParm);
//	}
    /**
	 * 
	* @Title: onExeIncludeBatch
	* @Description: TODO(套餐修改身份执行批次)
	* @author pangben 2015-10-20
	* @throws
	 */
	public void onExeIncludeBatch() {
		TParm selParm = new TParm();
		String sql = "SELECT MR_NO,LUMPWORK_RATE FROM ADM_INP WHERE CASE_NO='"
				+ caseNo
				+ "' AND IN_DATE IS NOT NULL AND CANCEL_FLG <> 'Y' AND LUMPWORK_CODE IS NOT NULL ";
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selParm.getCount() <= 0) {
			this.messageBox("此病患不是套餐患者,不可以操作");
			return;
		}
		if(null==selParm.getValue("LUMPWORK_RATE",0)||
				selParm.getValue("LUMPWORK_RATE",0).length()<=0||selParm.getDouble("LUMPWORK_RATE",0)==0.00){
			this.messageBox("此病患未设置套餐折扣,不可以操作");
    		return ;
    	}
		if (null != caseNo && caseNo.length() > 0) {
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			TParm result = TIOM_AppServer.executeAction("action.ibs.IBSAction",
					"onExeIbsLumpWorkBatch", parm);
			if (result.getErrCode() < 0) {
				this.messageBox("操作失败," + result.getErrText());
				return;
			}
			if (null != result.getValue("MESSAGE")
					&& result.getValue("MESSAGE").length() > 0) {
				this.messageBox(result.getValue("MESSAGE"));
				return;
			}
			this.messageBox("P0005");
			onQuery();
		} else {
			this.messageBox("请选择病患");
		}
	}

	/**
     * 明细表勾选数据
     */
	public void choiceLumpTABLE(){// add by kangy 20160930
		TParm lumpTABLEParm=lumpTABLE.getParmValue();
		//((TTable) getComponent("lumpTABLE")).acceptText();
		((TTable) getComponent("TABLEX")).acceptText();
		
		TParm tableXParm=tableX.getParmValue();
		TParm parm=new TParm();
		int selectedrow=lumpTABLE.getSelectedRow();
		parm=lumpTABLEParm.getRow(selectedrow);
		if("Y".equals(lumpTABLEParm.getValue("FLG",selectedrow))){
			//parm=lumpTABLEParm.getRow(sss);
			//parm.setData("FLG","Y");
			//++row;
			if (null != tableXParm && tableXParm.getCount("ORDER_CODE") > 0) {
				for (int i = 0; i < tableXParm.getCount("ORDER_CODE"); i++) {
					if (parm.getValue("CASE_NO").equals(
							tableXParm.getValue("CASE_NO", i))
							&& parm.getValue("CASE_NO_SEQ").equals(
									tableXParm.getValue("CASE_NO_SEQ", i))
							&& parm.getValue("SEQ_NO").equals(
									tableXParm.getValue("SEQ_NO", i))) {
						tableXParm.setData("FLG", i,"Y");
						tableX.setParmValue(tableXParm);
						return;
					}
				}
			}else{
				tableXParm=new TParm();
			}
			tableXParm.addRowData(lumpTABLEParm, selectedrow);
			tableX.setParmValue(tableXParm);
		}
		if("N".equals(lumpTABLEParm.getValue("FLG",selectedrow))){
			boolean flg=false;
			for(int i=tableXParm.getCount("ORDER_CODE")-1;i>=0;i--){
				if(parm.getValue("CASE_NO").equals(tableXParm.getValue("CASE_NO",i))
						&&parm.getValue("CASE_NO_SEQ").equals(tableXParm.getValue("CASE_NO_SEQ",i))
						&&parm.getValue("SEQ_NO").equals(tableXParm.getValue("SEQ_NO",i))){
					flg=true;
					tableXParm.removeRow(i);
				}
			}
			if(flg){
				tableX.setParmValue(tableXParm);
				return;
			}
		}
//		if (null != tableX && tableX.getCount("ORDER_CODE") > 0) {
//			if (parm.getValue("FLG").equals("Y")) {
//				for (int i = 0; i < tableX.getCount("ORDER_CODE"); i++) {
//					if (parm.getValue("CASE_NO").equals(
//							tableX.getValue("CASE_NO", i))
//							&& parm.getValue("CASE_NO_SEQ").equals(
//									tableX.getValue("CASE_NO_SEQ", i))
//							&& parm.getValue("SEQ_NO").equals(
//									tableX.getValue("SEQ_NO", i))) {
//						this.messageBox("该数据已存在！");
//						tableX.setData("FLG", i,"Y");
//						((TTable) getComponent("TABLEX")).setParmValue(tableX);
//						return;
//					}
//				}
//			}
//			
//		}
		
	}
	/**
     * 选中表移除数据
     */
	public void removeFromTableX(){// add by kangy 20160930
		TParm parm=tableX.getParmValue();
		//TParm tableMparm=((TTable) getComponent("TABLE_SUM")).getParmValue();
	    int row=tableX.getSelectedRow();
	    //TParm removeParm=parm.getRow(row);
		parm.removeRow(row);
		tableX.setParmValue(parm);
		onQuery();
//		for(int i=0;i<tableMparm.getCount();i++){
//			if(removeParm.getValue("ORDER_CODE").equals(tableMparm.getValue("ORDER_CODE",i))){
//			TTable table = (TTable) this.getComponent("TABLE_SUM");
//			onTABLEClicked(i);
//			table.setSelectedRow(i);
//			}
//		}
	}
	/**
     * 选中表全选
     */
	public void checkedAll(){// add by kangy  20160930
		TParm parm=tableX.getParmValue();
		if(checkedAll.isSelected()){
			for(int i=0;i<parm.getCount("ORDER_SEQ");i++){
				parm.setData("FLG",i,"Y");
			}
		}else{
			for(int i=0;i<parm.getCount("ORDER_SEQ");i++){
				parm.setData("FLG",i,"N");
			}
		}
		tableX.setParmValue(parm);
	}

/**
 * 明细表右击
 */
	public void onRightClicked(){
		int row=lumpTABLE.getSelectedRow();
		if(lumpTABLE.getParmValue().getDouble("DOSAGE_QTY",row)>1){
		TParm parm=lumpTABLE.getParmValue().getRow(row);
		 parm.setData("CTZ1_CODE",ctz1Code);
		 parm.setData("CTZ2_CODE",ctz2Code);
		 parm.setData("CTZ3_CODE",ctz3Code);
			String sql="SELECT CASE_NO FROM IBS_ORDD WHERE CASE_NO='"+caseNo+"' AND INCLUDE_EXEC_FLG='N'";
	    	TParm selectParm= new TParm(TJDODBTool.getInstance().select(sql));
	    	if (selectParm.getCount("CASE_NO")>0) {
				this.messageBox("存在未执行的医嘱，请操作套餐批次执行");
				table.setParmValue(new TParm());
				lumpTABLE.setParmValue(new TParm());
				return;
			}

	    	parm.setData("OPT_USER",Operator.getID());
	    	parm.setData("OPT_TERM",Operator.getIP());
			sql="SELECT LUMPWORK_RATE,LUMPWORK_CODE,SERVICE_LEVEL FROM ADM_INP WHERE CASE_NO='"+caseNo+"'";
			selectParm= new TParm(TJDODBTool.getInstance().select(sql));
			if (null==selectParm.getValue("LUMPWORK_RATE",0)||
					selectParm.getValue("LUMPWORK_RATE",0).length()<=0||selectParm.getDouble("LUMPWORK_RATE",0)==0.00){
				this.messageBox("此病患未设置套餐折扣,不可以操作");
				return;
			}
			parm.setData("LUMPWORK_RATE",selectParm.getDouble("LUMPWORK_RATE",0));
			parm.setData("LUMPWORK_CODE",selectParm.getValue("LUMPWORK_CODE",0));
			parm.setData("SERVICE_LEVEL",selectParm.getValue("SERVICE_LEVEL",0));
            TParm result=(TParm) openDialog("%ROOT%\\config\\ibs\\IBSLumpworkAdjustment.x",parm);
            	onQuery();
		}
}
		
}
