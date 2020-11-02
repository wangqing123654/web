package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jdo.bil.BIL;
import jdo.bil.BilInvoice;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:套餐余额结转 </p>
 *
 * <p>Description:套餐余额结转  </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:bluecore </p>
 *
 * @author huangtt 2017.11.08
 * @version 4.5
 */
public class MEMPackageBalanceTransferControl extends TControl{
	//日期格式化
	private SimpleDateFormat formateDate=new SimpleDateFormat("yyyy/MM/dd");
	private TTable table;
	private BilInvoice invoice = new BilInvoice();
	/**
     * 初始化
     */
    public void onInit() { // 初始化程序
        super.onInit();
        
        initData();
        initBil();
        table = (TTable) this.getComponent("TABLE");
        
    }
    /**
     * 初始化数据   
     */
    private void initData(){
    	//查询时间默认当天
    	Timestamp now = TJDODBTool.getInstance().getDBTime();
//   	 	this.setValue("START_DATE",
//		 		now.toString().substring(0, 10).replace('-', '/'));
   	 	this.setValue("END_DATE",
		 		now.toString().substring(0, 10).replace('-', '/'));
   	 	//开始时间为上个月-默认
   	 	Calendar cd = Calendar.getInstance();
   	 	cd.setTime(now);
   	 	cd.add(Calendar.YEAR, -1);
   	 	String format = formateDate.format(cd.getTime());
		this.setValue("START_DATE", format);
    }
    
    public void onQuery(){
    	String startDate = this.getValueString("START_DATE");
    	if(startDate.length()>0){
    		startDate = startDate.toString().replaceAll("-", "").replaceAll("/", "").substring(2, 8);
    	}
    	String endDate = this.getValueString("END_DATE");
    	if(endDate.length()>0){
    		endDate = endDate.toString().replaceAll("-", "").replaceAll("/", "").substring(2, 8);
    	}
    	
    	String sql = "SELECT 'N' FLG,A.MR_NO,D.PAT_NAME," +
    			" TO_CHAR (D.BIRTH_DATE, 'YYYY-MM-DD') BIRTH_DATE," +
    			" TO_CHAR (C.BILL_DATE, 'YYYY-MM-DD') BILL_DATE," +
    			" A.PACKAGE_DESC,A.SECTION_DESC,B.ORDER_CODE,B.ORDER_DESC," +
    			" B.ORDER_NUM, E.UNIT_CHN_DESC UNIT_DESC,B.UNIT_PRICE," +
    			" B.UNIT_PRICE * B.ORDER_NUM SUM_PRICE, B.RETAIL_PRICE," +
    			" B.TRADE_NO,B.SECTION_CODE,B.PACKAGE_CODE,B.ID" +
    			" ,F.CHARGE_HOSP_CODE, '' AS REXP_CODE " +
    			" FROM MEM_PAT_PACKAGE_SECTION A," +
    			" MEM_PAT_PACKAGE_SECTION_D B," +
    			" MEM_PACKAGE_TRADE_M C, SYS_PATINFO D, SYS_UNIT E,SYS_FEE F" +
    			" WHERE A.USED_FLG = '1'" +
    			" AND A.TRADE_NO = B.TRADE_NO" +
    			" AND A.PACKAGE_CODE = B.PACKAGE_CODE" +
    			" AND A.SECTION_CODE = B.SECTION_CODE" +
    			" AND B.USED_FLG = '0'" +
    			" AND B.HIDE_FLG = 'N'" +
    			" AND A.TRADE_NO = C.TRADE_NO" +
    			" AND A.MR_NO = D.MR_NO" +
    			" AND B.UNIT_CODE = E.UNIT_CODE" +
    			" AND B.ORDER_CODE = F.ORDER_CODE" +
    			" AND SUBSTR (A.CASE_NO, 1, 6) BETWEEN '"+startDate+"' AND '"+endDate+"'" ;
    	
    	String mrNo = this.getValue("MR_NO").toString();
    	if(mrNo.length() > 0){
    		sql += " AND A.MR_NO='"+mrNo+"'";
    	}
    	
    	sql +=	" ORDER BY A.MR_NO, C.BILL_DATE,A.PACKAGE_CODE, A.SECTION_CODE, B.ORDER_CODE";
    
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	
    	if(parm.getCount() < 0){
    		this.messageBox("没有要查询的数据");
    		table.removeRowAll();
    		return;
    	}
    	
    	table.setParmValue(parm);
    
    }
    
    public void onMrNo(){
    	String mrNo = this.getValue("MR_NO").toString();
    	if(mrNo.length() > 0){
    		mrNo = PatTool.getInstance().checkMrno(mrNo);
    		this.setValue("MR_NO", mrNo);
    	}
    	onQuery();
    	
    }
    
    public void onSave(){
    	table.acceptText();
    	boolean showFlg=true;
    	TParm parm =  table.getParmValue();
		int rowCount = parm.getCount("FLG");
		TParm selParm = new TParm();
		List<String> scrList = new ArrayList<String>();
		for (int i = 0; i < rowCount; i++) {
			if(parm.getBoolean("FLG", i)){
				String REXP_CODE = BIL.getRexpCode(parm.getValue("CHARGE_HOSP_CODE",i),"O");
				System.out.println("rexp_code---"+REXP_CODE+"----"+parm.getValue("CHARGE_HOSP_CODE",i));
				parm.setData("REXP_CODE", i, REXP_CODE);
				selParm.addRowData(parm, i);
				String scr = parm.getValue("MR_NO", i)+";"+parm.getValue("PACKAGE_CODE", i)+";"+parm.getValue("TRADE_NO", i);
				if(!scrList.contains(scr)){
					scrList.add(scr);
				}
			}
		}
		
		if(selParm.getCount("FLG") < 0){
			this.messageBox("没有要保存的数据");
			return;
		}
		
		
		
		
		selParm.setCount(selParm.getCount("FLG"));
		
		TParm bilRecpParm = new TParm();
		for (int i = 0; i < scrList.size(); i++) {
			String aa[] = scrList.get(i).split(";");
			String mrNo = aa[0];
			String packageCode = aa[1];
			String tradeNo = aa[2];
			TParm calParm = new TParm();
			for (int j = 0; j < selParm.getCount(); j++) {
				if(tradeNo.equals(selParm.getValue("TRADE_NO", j)) &&
						mrNo.equals(selParm.getValue("MR_NO", j)) &&
						packageCode.equals(selParm.getValue("PACKAGE_CODE", j))){
					calParm.addRowData(selParm, j);
				}
			}
			if(calParm.getCount("MR_NO") > 0){
				showFlg = initBil();
				if(showFlg){
					bilRecpParm = dealSaveParm(calParm);
					TParm allParm = new TParm();
					allParm.setData("bilRecpParm", bilRecpParm.getData());//收据表数据插入
					allParm.setData("selParm", calParm.getData());//更新套餐为使用状态
					allParm.setData("START_INVNO", invoice.getStartInvno());
					TParm result = TIOM_AppServer.executeAction("action.mem.MEMPackageSectionAction",
							"onPackBalanceTransfer", allParm);
					if (result.getErrCode() < 0) {
						this.messageBox("结转保存失败");
						return;
					}
				}
				
			}
			
			
		}
		
		if(showFlg){
			this.messageBox("保存成功");
			this.onQuery();
			initBil();
		}
		
		
		
		
    }
    
    public boolean initBil(){
    	
		 invoice = invoice.initBilInvoice("MEM");
		// 检核开关帐
			if (invoice == null) {
				this.messageBox_("你尚未对套餐余额结转开账!");
				return false;
			}
			// 检核当前票号
			if (invoice.getUpdateNo().length() == 0
					|| invoice.getUpdateNo() == null) {
				this.messageBox_("无可打印的套餐余额结转票据!");
				// this.onClear();
				return false;
			}
//			// 检核当前票号
//			if (invoice.getUpdateNo().equals(invoice.getEndInvno())) {
//				this.messageBox_("最后一张套餐余额结转票据!");
//			}
			

             if (invoice.getUpdateNo().compareTo(invoice.getEndInvno()) > 0) {
                 this.messageBox("票据已用完!");
                 return false;
             }
			
			this.setValue("PRINT_NO", invoice.getUpdateNo());
			return true;
    }
    
    public TParm dealSaveParm(TParm opdParm){
    	String rexpCode="";
		double allTotAmt = 0.00;
    	double[] chargeDouble = new double[30]; // 金额分类
    	String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_CHARGE' ORDER BY SEQ";
		TParm sysChargeParm = new TParm(TJDODBTool.getInstance().select(sql));
		sql = "SELECT CHARGE01, CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07,"
				+ " CHARGE08, CHARGE09, CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14,CHARGE15, "
				+ " CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24, "
				+ " CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29,CHARGE30 "
				+ " FROM BIL_RECPPARM WHERE ADM_TYPE ='O'";
		TParm bilRecpParm = new TParm(TJDODBTool.getInstance().select(sql));
		int chargeCount = sysChargeParm.getCount("ID");
		String[] chargeName = new String[30];
		int index = 1;
		for (int i = 0; i < 30; i++) {
			String chargeTemp = "CHARGE";
			if (i < 9) {
				chargeName[i] = bilRecpParm
						.getData(chargeTemp + "0" + index, 0).toString();
			} else {
				chargeName[i] = bilRecpParm.getData(chargeTemp + index, 0)
						.toString();
			}
			index++;
		}
		TParm p = new TParm();
		for (int i = 0; i < chargeCount; i++) {
			String sysChargeId = sysChargeParm.getData("ID", i).toString();
			for (int j = 0; j < chargeName.length; j++) {
				if (sysChargeId.equals(chargeName[j])) {
					p.setData("CHARGE", i, j);
					p.setData("ID", i, sysChargeParm.getData("ID", i));
					p.setData("CHN_DESC", i, sysChargeParm.getData("CHN_DESC",
							i));
					break;
				}
			}
		}
		
		String idCharge = "";
		double arAmt=0;
		int charge = 0;
		for (int i = 0; i < opdParm.getCount("MR_NO"); i++) {
			rexpCode = opdParm.getValue("REXP_CODE", i);
			arAmt = opdParm.getDouble("RETAIL_PRICE", i); 
			allTotAmt = allTotAmt + arAmt;
			for (int j = 0; j < p.getCount("ID"); j++) {
				idCharge = p.getData("ID", j).toString();
				charge = p.getInt("CHARGE", j);
				if (rexpCode.equals(idCharge))
					chargeDouble[charge] = chargeDouble[charge] + arAmt;
				
			}
		}
		
		TParm opbreceipt = new TParm();
		
		opbreceipt.setData("ADM_TYPE", "M");
		opbreceipt.setData("MEM_PACK_FLG", "Y");
		opbreceipt.setData("REGION_CODE", Operator.getRegion());
		opbreceipt.setData("MR_NO", opdParm.getValue("MR_NO",0));
		opbreceipt.setData("TRADE_NO", opdParm.getValue("TRADE_NO",0));
		opbreceipt.setData("PACKAGE_CODE", opdParm.getValue("PACKAGE_CODE",0));
		opbreceipt.setData("PACKAGE_DESC", opdParm.getValue("PACKAGE_DESC",0));
		opbreceipt.setData("RESET_RECEIPT_NO", new TNull(String.class));
		 //取号原则得到票据号
        String receiptNo = SystemTool.getInstance().getNo("ALL", "MEM",
                "RECEIPT_NO",
                "RECEIPT_NO");
		opbreceipt.setData("RECEIPT_NO", receiptNo);
		opbreceipt.setData("PRINT_NO", invoice.getUpdateNo());
		opbreceipt.setData("BILL_DATE", SystemTool.getInstance().getDate());
		opbreceipt.setData("CHARGE_DATE", SystemTool.getInstance().getDate());
		opbreceipt.setData("PRINT_DATE", SystemTool.getInstance().getDate());
		// 写入数据
		index = 1;
		for (int i = 0; i < chargeDouble.length; i++) {
			String chargeTemp = "CHARGE";
			if (i < 9) {
				opbreceipt.setData(chargeTemp + "0" + index, chargeDouble[i]);
			} else {
				opbreceipt.setData(chargeTemp + index, chargeDouble[i]);
			}
			index++;
		}
		opbreceipt.setData("AR_AMT", 0.00); // 总金额
		opbreceipt.setData("REDUCE_AMT", 0.00);	
		opbreceipt.setData("REDUCE_NO", new TNull(String.class));
		opbreceipt.setData("REDUCE_DATE", new TNull(Timestamp.class));
		opbreceipt.setData("TOT_AMT", allTotAmt); // 总金额
		opbreceipt.setData("REDUCE_REASON", new TNull(String.class));
		opbreceipt.setData("REDUCE_DEPT_CODE", new TNull(String.class));
		opbreceipt.setData("REDUCE_RESPOND", new TNull(String.class));
		opbreceipt.setData("PAY_CASH", 0.00);
		opbreceipt.setData("PAY_OTHER1", 0.00);
		opbreceipt.setData("PAY_MEDICAL_CARD", 0.00);//=====pangben 2014-7-17 医疗卡金额修改
		opbreceipt.setData("PAY_BANK_CARD", 0.00);
		opbreceipt.setData("PAY_INS_CARD", 0.00);
		opbreceipt.setData("PAY_CHECK", 0.00);
		opbreceipt.setData("PAY_DEBIT", 0.00);
		opbreceipt.setData("PAY_BILPAY", 0.00);
		opbreceipt.setData("PAY_INS", 0.00);
		opbreceipt.setData("PAY_OTHER2", 0.00);
		opbreceipt.setData("PAY_OTHER3", 0.00);
		opbreceipt.setData("PAY_OTHER4", 0.00);
		opbreceipt.setData("PAY_REMARK", new TNull(String.class));
		opbreceipt.setData("CASHIER_CODE", Operator.getID());
		opbreceipt.setData("OPT_USER", Operator.getID());
		opbreceipt.setData("OPT_DATE", SystemTool.getInstance().getDate());
		opbreceipt.setData("OPT_TERM", Operator.getIP());
		opbreceipt.setData("TAX_FLG","Y");
		opbreceipt.setData("TAX_DATE",SystemTool.getInstance().getDate());
		opbreceipt.setData("TAX_USER",Operator.getID());
    	return opbreceipt;
    }
    
    public void onClear(){
    	initData();
    	this.clearValue("MR_NO;ALL");
    	table.removeRowAll();
    	 initBil();
    }
    
    /**
	 * 汇出Excel
	 */
	public void onExport() {
		
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable tableIN = (TTable) callFunction("UI|TABLE|getThis");
	
		String[] header;
		List<TParm> parmList = new ArrayList<TParm>();

		TParm parmIN = tableIN.getShowParmValue();
		if (tableIN.getRowCount() > 0) {
			parmIN.setData("TITLE", "套餐余额结转数据报表");
			
			String [] aa = tableIN.getHeader().split(";");
			String heards="";
			for (int i = 1; i < aa.length; i++) {
				heards+=aa[i]+";";
			}
			parmIN.setData("HEAD",heards.substring(0, heards.length()-1));
			
			header = tableIN.getParmMap().split(";");
	        for (int i = 1; i < header.length; i++) {
	        	
	        	parmIN.addData("SYSTEM", "COLUMNS", header[i]);
	        }
	       
	        parmList.add(parmIN);
		}
		
		 TParm[] execleTable = new TParm[parmList.size()];
	        for (int i = 0; i < parmList.size(); i++) {
	        	execleTable[i] = parmList.get(i);
	        }
		
	        ExportExcelUtil.getInstance().exeSaveExcel(execleTable, "套餐余额结转数据报表");
	}
    
    public void selAll(){
    	TParm parm = table.getParmValue();
		int rowCount = parm.getCount();
		
		String flg = "";
		if (this.getTCheckBox("ALL").isSelected())
			flg = "Y";			
		else
			flg = "N";
		
		
		if(rowCount>0){
			for (int i = 0; i < rowCount; i++) {
				parm.setData("FLG", i, flg);
			}
			table.setParmValue(parm);
		}
    }
		
		/**
		 * 得到TCheckBox
		 * 
		 * @param tag
		 *            String
		 * @return TCheckBox
		 */
		public TCheckBox getTCheckBox(String tag) {
			return (TCheckBox) this.getComponent(tag);
		}
}
