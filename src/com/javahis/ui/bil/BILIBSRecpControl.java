package com.javahis.ui.bil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

import jdo.adm.ADMInpTool;
import jdo.bil.BILInvoiceTool;
import jdo.bil.BILPayTool;
import jdo.bil.BILTool;
import jdo.bil.BilInvoice;
import jdo.bil.PaymentTool;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;
import jdo.ibs.IBSBilldTool;
import jdo.ibs.IBSBillmTool;
import jdo.ibs.IBSTool;
import jdo.ins.TJINSRecpTool;
import jdo.mro.MRORecordTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.device.NJCityInwDriver;
import com.javahis.util.StringUtil;

/**
 *
 * <p>
 * Title: 缴费作业控制类
 * </p>
 *
 * <p>
 * Description: 缴费作业控制类
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author wangl 2009.04.29
 * @version 1.0
 */
public class BILIBSRecpControl extends TControl {
	//boolean saveFLG=false;//==liling 20140421 add
    TParm endChargeParm = new TParm();
    TParm endBillPayParm = new TParm();
    // 就诊序号
    String caseNo = "";
    // 病患姓名
    String patName = "";
    // 性别
    String sexCode = "";
    // 病区
    String stationCode = "";
    // 开始票号
    String startInvno = "";
    // 病案号
    String mrNoOut = "";
    // 住院号
    String ipdNoOut = "";
    // 床位号
    String bedNoOut = "";
    // 科室代码
    String deptCodeOut = "";
    // 入院日期
    Timestamp inDataOut;
    // 出生日期
    Timestamp birthDataOut;
    //===zhangp 20130717 start
    TParm tjInsColumns;//天津医保sqlcolumns与票据中的columns对应
    TParm insValueParm = new TParm();//天津医保存值
    //===zhangp 20130717 end
    //pangben 2014-5-6 多种支付方式操作     
    PaymentTool paymentTool;
    
    private boolean flag=false;//是否已经缴费标记  add caoy 2014/5/28
    private DecimalFormat df = new DecimalFormat("##########0.00");
    private TParm parmEKT;//医疗卡集合
    /**
     * 初始化   
     */
    public void onInit() { 
        super.onInit();
        Object obj = this.getParameter();
        TParm initParm = (TParm) obj;     
        mrNoOut = initParm.getData("ODI", "MR_NO").toString();
        ipdNoOut = initParm.getData("ODI", "IPD_NO").toString();
        caseNo = initParm.getData("IBS", "CASE_NO").toString();
        patName = initParm.getData("ODI", "PAT_NAME").toString();
        sexCode = initParm.getData("ODI", "SEX_CODE").toString();
        birthDataOut = initParm.getTimestamp("ODI", "BIRTH_DATE");
        bedNoOut = initParm.getData("ODI", "BED_NO").toString();
        deptCodeOut = initParm.getData("ODI", "DEPT_CODE").toString();
        stationCode = initParm.getData("ODI", "STATION_CODE").toString();
        inDataOut = initParm.getTimestamp("ODI", "ADM_DATE");
        // 初始化界面参数
        this.initPage(false);//==liling add args
        // 账单table专用的监听
        getTTable("BillTable").addEventListener(TTableEvent.CHECK_BOX_CLICKED,
                                                this, "onBillTableComponent");
        // 预交金table专用的监听
        getTTable("BillPayTable").addEventListener(
                TTableEvent.CHECK_BOX_CLICKED, this, "onBillPayTableComponent");
        //===zhangp 20120417 start
        this.callFunction("UI|AR_AMT|SetEnabled", false);
        //===zhangp 20120417 end
        //======pangben 2014-5-6 多种支付方式操作
    	TPanel p = (TPanel) getComponent("TABLE_PANEL");
    	try {
			paymentTool = new PaymentTool(p, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.onGatherChange(0);
		//校验是否使用套餐=====pangben 2014-5-12
		String lumpwork_code=initParm.getData("IBS", "LUMPWORK_CODE").toString();
		if (lumpwork_code.length()>0) {
			
//			String sql="SELECT FEE FROM MEM_LUMPWORK WHERE LUMPWORK_CODE='"+lumpwork_code+"'";
			String sql="SELECT B.PACKAGE_CODE, C.PACKAGE_DESC, A.AR_AMT,C.CTZ_CODE,A.TRADE_NO "
				+ " FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B, MEM_PACKAGE C "
				+ " WHERE A.TRADE_NO = B.TRADE_NO "
				+ " AND B.PACKAGE_CODE = C.PACKAGE_CODE "
				+ " AND B.PACKAGE_CODE='"
				+ lumpwork_code
				+ "' AND A.MR_NO='"
				+ mrNoOut + "' AND A.CASE_NO='"+caseNo+"' GROUP BY B.PACKAGE_CODE, C.PACKAGE_DESC, A.AR_AMT,C.CTZ_CODE,A.TRADE_NO ";
			TParm lumPworkParm = new TParm(TJDODBTool.getInstance().select(sql));
			sql="SELECT SUM(TOT_AMT) TOT_AMT FROM IBS_ORDD A,ODI_SYSPARM B WHERE A.ORDER_CODE=B.LUMPWORK_ORDER_CODE AND A.CASE_NO='" +caseNo+"'";
			TParm lumpDiffParm= new TParm(TJDODBTool.getInstance().select(sql));//差异金额显示
			this.setValue("LUMPWORK_CODE", lumPworkParm.getValue("PACKAGE_DESC",0));
//			this.setValue("LUMPWORK_AMT", lumPworkParm.getDouble("FEE",0));
			this.setValue("LUMPWORK_AMT", lumPworkParm.getDouble("AR_AMT",0));//修改套餐类型相关信息--xiongwg20150707
			this.setValue("PACK_DIFF_AMT", lumpDiffParm.getDouble("TOT_AMT",0));//差异金额
		}
		String insureSql = "SELECT INSURE_INFO FROM ADM_INP WHERE CASE_NO = '"+caseNo+"'";
		TParm insureParm = new TParm(TJDODBTool.getInstance().select(insureSql));
		this.setValue("INSURE_INFO", insureParm.getValue("INSURE_INFO",0));
//		String bilAuditFlg=initParm.getData("IBS", "BIL_AUDIT_FLG").toString();
//		if(null!=bilAuditFlg&&bilAuditFlg.equals("Y")){
//			callFunction("UI|close|setEnabled", false); //退出按钮置灰//==liling 20140716 add
//		}
    }

    /**
     * 账单table监听事件
     *
     * @param obj
     *            Object
     * @return boolean
     */
    public boolean onBillTableComponent(Object obj) {
        TTable chargeTable = (TTable) obj;
        chargeTable.acceptText();
        TParm tableParm = chargeTable.getParmValue();
        endChargeParm = new TParm();
        int count = tableParm.getCount("BILL_NO");
        //=====zhangp 20120416 start
        int patselY = 0;
        TParm lumPworkParm =null;
        double lumpWorkOUtAmt=0.00;
        boolean flg=false;
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("PAY_SEL", i)) {
                endChargeParm.addData("BILL_NO", tableParm.getValue("BILL_NO",
                        i));
                endChargeParm.addData("BILL_SEQ", tableParm.getInt("BILL_SEQ",
                        i));
                endChargeParm
                        .addData("AR_AMT", tableParm.getValue("AR_AMT", i));
                endChargeParm.addData("APPROVE_FLG", tableParm.getValue(
                        "APPROVE_FLG", i));
                //=====zhangp 20120416 start
                patselY++;
                if (null!=this.getValue("LUMPWORK_CODE") && this.getValue("LUMPWORK_CODE").toString().length()>0) {
                	String sql="SELECT SUM(TOT_AMT) AS TOT_AMT FROM IBS_ORDD WHERE CASE_NO='"+tableParm.getValue("CASE_NO",i)+"' AND BILL_NO='"+ 
                	tableParm.getValue("BILL_NO",i)+"' AND INCLUDE_FLG='Y'";//查询包干外的总金额
                	lumPworkParm = new TParm(TJDODBTool.getInstance().select(sql));
        			lumpWorkOUtAmt+=lumPworkParm.getDouble("TOT_AMT",0);
                }
                flg=true;
            }
        }
        int feeCount = endChargeParm.getCount("AR_AMT");
        double totAmt = 0.00;
       // double ownAmt=0.00;
        //添加套餐金额操作======pangben 2014-5-12
        if (null!=this.getValue("LUMPWORK_CODE") &&this.getValue("LUMPWORK_CODE").toString().length()>0) {
        	if (flg) {
            	this.setValue("LUMPWORK_OUT_AMT", lumpWorkOUtAmt);//套餐外金额
			}else{
            	this.setValue("LUMPWORK_OUT_AMT", 0.00);//套餐外金额
			}
        	totAmt=this.getValueDouble("LUMPWORK_AMT");
		}else{
	        for (int j = 0; j < feeCount; j++) {
	            totAmt = totAmt + endChargeParm.getDouble("AR_AMT", j);
	        }
	        //ownAmt=totAmt;
		}
        setValue("TOT_AMT", totAmt+lumpWorkOUtAmt);
        setValue("OWN_AMT", totAmt+lumpWorkOUtAmt);
        double enArAmt = totAmt - TypeTool.getDouble(getValue("PAY_BILPAY"))+lumpWorkOUtAmt;//总金额-预交金+包干外金额
        // if(enArAmt<=0)
        // enArAmt=0;
        setValue("AR_AMT", enArAmt);
        
        if(this.getValueString("BILL_TYPE").equals("C")){
        	setValue("PAY_CASH", TypeTool.getDouble(getValue("AR_AMT")));
        	//=====pangben 2014-5-6 表格支付方式赋值
            paymentTool.setAmt(TypeTool.getDouble(this.getValueDouble("AR_AMT")));
            TParm parm =new TParm();
            if (this.getValueDouble("AR_AMT")!=0) {
            	parm.addData("PAY_TYPE","C0");//默认显示现金
            	parm.addData("AMT",this.getValueDouble("AR_AMT"));
            	parm.addData("REMARKS","");
                //paymentTool.table.removeRowAll();
//                paymentTool.table.addRow(parm);
            	paymentTool.table.setParmValue(parm);
                paymentTool.table.addRow();

    		}else{
    			parm.addData("PAY_TYPE","C0");
            	parm.addData("AMT",0.00);
            	parm.addData("REMARKS","");
    			paymentTool.table.setParmValue(parm);
    			//paymentTool.table.addRow();
    		}
        }else{
        	setValue("PAY_MEDICAL_CARD", TypeTool.getDouble(getValue("AR_AMT")));
        	setValue("EKT_AMT", StringTool.round((parmEKT.getDouble("CURRENT_BALANCE") - getValueDouble("AR_AMT")), 2));
        }
        
        if (chargeTable.getParmValue().getBoolean("PAY_SEL",
                                                  chargeTable.getSelectedRow())) {
            chargeTable.setValueAt(chargeTable.getParmValue().getDouble(
                    "AR_AMT", chargeTable.getSelectedRow()), chargeTable
                                   .getSelectedRow(), 7);
        } else {
            chargeTable.setValueAt(0.00, chargeTable.getSelectedRow(), 7);

        }
        //=====zhangp 20120416 start
        if (patselY == count) {
            setInsValue();
        }
        //=====zhangp 20120416 end
        return true;
    }

    /**
     * 预交金table监听事件
     *
     * @param obj
     *            Object
     * @return boolean
     */
    public boolean onBillPayTableComponent(Object obj) {
        TTable billPayTable = (TTable) obj;
        billPayTable.acceptText();
        TParm tableParm = billPayTable.getParmValue();
        int selRow = billPayTable.getSelectedRow();
        
//        if(tableParm.getValue("PAY_TYPE", selRow).equals("EKT")){
//        	if(this.getValueString("BILL_TYPE").equals("C")){
//        		this.messageBox("当前缴费为多种支付方式操作，请选择相同支付类型的预交金明细");
//        		tableParm.setData("BILLPAY_SEL", selRow, "N");
//        		 // 给预交金table赋值
//                this.getTTable("BillPayTable").setParmValue(tableParm);
//                return false;
//        	}
//        }else{
//        	if(this.getValueString("BILL_TYPE").equals("E")){
//        		this.messageBox("当前缴费为多种支付方式操作，请选择相同支付类型的预交金明细");
//        		tableParm.setData("BILLPAY_SEL", selRow, "N");
//        		 // 给预交金table赋值
//                this.getTTable("BillPayTable").setParmValue(tableParm);
//                return false;
//        	}
//        }

        endBillPayParm = new TParm();
        int count = tableParm.getCount("RECEIPT_NO");
        String ektTrade = "";
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("BILLPAY_SEL", i)) {
            	endBillPayParm.addData("PAY_TYPE", tableParm.getValue(
                        "PAY_TYPE", i));
                endBillPayParm.addData("RECEIPT_NO", tableParm.getValue(
                        "RECEIPT_NO", i));
                endBillPayParm.addData("PRE_AMT", tableParm.getValue("PRE_AMT",
                        i));
                ektTrade+="'"+tableParm.getValue("BUSINESS_NO", i)+"',";
            }
        }
        int feeCount = endBillPayParm.getCount("PRE_AMT");
        double bilPayAmt = 0.00;
        for (int j = 0; j < feeCount; j++) {
            bilPayAmt = bilPayAmt + endBillPayParm.getDouble("PRE_AMT", j);
        }
        
        //===zhangp 20120417 start
        double enArAmt = 0;
        if (getValueDouble("OWN_AMT") > 0) {
            enArAmt = TypeTool.getDouble(getValue("OWN_AMT")) - bilPayAmt;
        } else {
            enArAmt = TypeTool.getDouble(getValue("TOT_AMT")) - bilPayAmt;
        }
        //===zhangp 20120417 end
        if(enArAmt < 0){
        	if(this.getValueString("BILL_TYPE").equals("C")){
        		 for (int j = 0; j < feeCount; j++) {
        			 if(endBillPayParm.getValue("PAY_TYPE", j).equals("EKT")){
        				 this.messageBox("预交金支付类型与当前缴费的支付类型不符");
                 		 tableParm.setData("BILLPAY_SEL", selRow, "N");
                 		 // 给预交金table赋值
                         this.getTTable("BillPayTable").setParmValue(tableParm);
                         return false;
        			 }
        				 
        		 }
        	}else if(this.getValueString("BILL_TYPE").equals("E")){
        		for (int j = 0; j < feeCount; j++) {
       			 if(!endBillPayParm.getValue("PAY_TYPE", j).equals("EKT")){
       				 this.messageBox("预交金支付类型与当前缴费的支付类型不符");
                		 tableParm.setData("BILLPAY_SEL", selRow, "N");
                		 // 给预交金table赋值
                        this.getTTable("BillPayTable").setParmValue(tableParm);
                        return false;
       			 }
       				 
       		 }
        	}
        }
        
        
        // if(enArAmt<=0)
        // enArAmt=0;
        setValue("PAY_BILPAY", bilPayAmt);
        setValue("AR_AMT", enArAmt);
        if(this.getValueString("BILL_TYPE").equals("C")){
        	 paymentTool.setAmt(TypeTool.getDouble(this.getValueDouble("AR_AMT")));
             TParm parm =new TParm();
             if (this.getValueDouble("AR_AMT")!=0) {
             	parm.addData("PAY_TYPE","C0");//默认显示现金
             	parm.addData("AMT",this.getValueDouble("AR_AMT"));
             	parm.addData("REMARKS","");
                 //paymentTool.table.removeRowAll();
//                 paymentTool.table.addRow(parm);
             	paymentTool.table.setParmValue(parm);
                 paymentTool.table.addRow();

     		}else{
     			parm.addData("PAY_TYPE","C0");
             	parm.addData("AMT",0.00);
             	parm.addData("REMARKS","");
     			paymentTool.table.setParmValue(parm);
     			//paymentTool.table.addRow();
     		}
             setValue("PAY_CASH", TypeTool.getDouble(getValue("AR_AMT")));
        }else{
        	setValue("PAY_MEDICAL_CARD", TypeTool.getDouble(getValue("AR_AMT")));
        	setValue("EKT_AMT", StringTool.round((parmEKT.getDouble("CURRENT_BALANCE") - getValueDouble("AR_AMT")), 2));
        	if(this.getValueDouble("AR_AMT") < 0){
        		if(ektTrade.length() > 0){
        			callFunction("UI|payLable|setVisible", true);  
        			ektTrade = ektTrade.substring(0, ektTrade.length()-1);
        			String sql = "SELECT SUM(PAY_OTHER3) AS PAY_OTHER3,SUM(PAY_OTHER4) AS PAY_OTHER4,SUM(AMT) AS AMT FROM EKT_TRADE WHERE TRADE_NO IN ("+ektTrade+")";
        			TParm ektParm = new TParm(TJDODBTool.getInstance().select(sql));
        			setValue("GIFT_CARD", ektParm.getDouble("PAY_OTHER3", 0));
        			setValue("GIFT_CARD2", ektParm.getDouble("PAY_OTHER4", 0));
        			setValue("NO_PAY_OTHER_ALL", ektParm.getDouble("AMT", 0) - ektParm.getDouble("PAY_OTHER3", 0) - ektParm.getDouble("PAY_OTHER4", 0));
        			this.setValue("PAY_OTHER3", 0.00);
            		this.setValue("PAY_OTHER4", 0.00);
            		this.onPayOther();
        		}
        		
        	}else{
        		callFunction("UI|payLable|setVisible", false);  
        	}
        }
       
        return true;
    }

    /**
     * 初始化界面
     */
    public void initPage(boolean saveFLG) {//==liling 20140421 add args
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        parm.setData("MR_NO", mrNoOut);
        boolean flg = IBSTool.getInstance().checkData(caseNo);
        if (!flg)
            this.messageBox("还有未产生账单的医嘱信息");
        // 预交金table显示数据
        TParm bilPayParm = new TParm();
        bilPayParm = BILPayTool.getInstance().selDataForCharge(parm);
        for (int i = 0; i < bilPayParm.getCount(); i++) {
            bilPayParm.addData("BILLPAY_SEL", "N");
        }
        // 账单table显示数据
        TParm chargeParm = new TParm();
        chargeParm = IBSBillmTool.getInstance().selDataForCharge(parm);
        for (int i = 0; i < chargeParm.getCount(); i++) {
            chargeParm.addData("PAY_SEL", "N");
        }
        // 给预交金table赋值
        this.getTTable("BillPayTable").setParmValue(bilPayParm);
        // 账单table赋值
        this.getTTable("BillTable").setParmValue(chargeParm);
        // 票号显示
        this.checkNo();
        if(!saveFLG){//==liling 20140421 add 如果保存成功 则不调用checkOnNewBabyRecp()
        	checkOnNewBabyRecp();
        }
    }

    /**
     * 得到TTable
     *
     * @param tag
     *            String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    /**
     * 保存事件
     *
     * @return boolean
     */
    public boolean onSave() {
    	//缴费方式对应的支付方式点击传值，不需要回车(取消编辑状态)--start
    	for (int i = 0; i < paymentTool.table.getColumnCount(); i++) {
    		paymentTool.table.getCellEditor(i).stopCellEditing();
		}
    	//缴费方式对应的支付方式点击传值，不需要回车--end
    	//$------add caoy 如果已经缴费成功不可以再次缴费 2014、5、28--start--
    	if(flag){
    		this.messageBox("该病患已缴费");
    		return false;
    	}
    	//$------add caoy 如果已经缴费成功不可以再次缴费 2014、5、28--end--
    	
        if (!checkNo()) {
            this.messageBox("尚未开帐,请先开帐");
            return false;
        }
        //===========pangben 2014-5-13 注释，不需要这个校验方法
//        if (!checkTotFee()) {
//            this.messageBox("不能超过总金额");
//            return false;
//        }
        // 初始化下一票号
        BilInvoice invoice = new BilInvoice();
        invoice = invoice.initBilInvoice("IBS");
        // 检核开关帐
        if (invoice == null) {
            this.messageBox_("你尚未开账!");
            return false;
        }
        if (invoice.getUpdateNo().compareTo(invoice.getEndInvno()) > 0) {
            this.messageBox("票据已用完!");
            return false;
        }
      //==========================================================如果是保险支付要校验病患的保险是否在有效期内add by huangjw 20150907 start
		TParm tableParm = paymentTool.table.getParmValue();
		for(int i = 0; i < tableParm.getCount("PAY_TYPE"); i++){
			if(tableParm.getValue("PAY_TYPE",i).equals("BXZF")){
				String sql = "SELECT CONTRACTOR_CODE FROM MEM_INSURE_INFO " +
						" WHERE MR_NO = '"+mrNoOut+"' AND VALID_FLG = 'Y'" +
						" AND START_DATE <= TRUNC (SYSDATE, 'dd') AND END_DATE >= TRUNC (SYSDATE, 'dd')";
				TParm dataParm = new TParm(TJDODBTool.getInstance().select(sql));
				if(dataParm.getCount() < 0){
					if(JOptionPane.showConfirmDialog(null, "该病患保险不在有效期内，是否继续", "信息",
		    				JOptionPane.YES_NO_OPTION) == 0){
						break;//跳出循环
					}else{
						return false;//返回上一级方法
					}
				}
			}
		}
		//===========================================================如果是保险支付要校验病患的保险是否在有效期内add by huangjw 20150907 end 
        //账单明细表格数据--xiongwg20150402
        TTable TBill = (TTable) getComponent("BillTable");
        TParm BillParm = TBill.getParmValue();
        
        // 检核最后一张票号
        if (invoice.getUpdateNo().equals(invoice.getEndInvno())) {
            this.messageBox_("最后一张票据!");
        }
        //===zhangp 20120412 start
        String inssql = "SELECT * FROM INS_ADM_CONFIRM " +
                        " WHERE CASE_NO = '" + caseNo + "' " +
                        "   AND IN_STATUS <> '5' ";
        TParm isInsParm = new TParm(TJDODBTool.getInstance().select(inssql));
        if (isInsParm.getCount() > 0) {
            messageBox("此病患为医保病人");
            //====zhangp 20120530 start
            if (!checkInsUpload(isInsParm)) {
            	this.messageBox("尚未申报,请先申报");
            	return false;  
            }
            TParm insparm = getInsParm(caseNo);
            double insArAmt = StringTool.round(getValueDouble("TOT_AMT"),2);
            if (insArAmt != insparm.getDouble("AR_AMT")) {
				messageBox("分割数据有误,请重新分割");
			    return false;  
			}
            //当前就诊号为医保病人，同时勾选包含婴儿账单
            if("Y".equals(getValue("NEW_BABY_FLG"))){
            	for(int i=0;i<BillParm.getCount("CASE_NO");i++){
                	String NewInssql = "SELECT * FROM INS_ADM_CONFIRM " +
                    " WHERE CASE_NO = '" + BillParm.getValue("CASE_NO",i) + "' " +
                    "   AND IN_STATUS <> '5' ";
                	TParm NEWInsParm = new TParm(TJDODBTool.getInstance().select(NewInssql));
                	if (NEWInsParm.getCount() > 0) {
                		messageBox("此医保患者的新生儿有医保");
                        //====zhangp 20120530 start
                        if (!checkInsUpload(isInsParm)) {
                        	this.messageBox("尚未申报,请先申报");
                        	return false;  
                        }
                        TParm NewInsparm = getInsParm(BillParm.getValue("CASE_NO",i));
                        double NewInsArAmt = StringTool.round(getValueDouble("TOT_AMT"),2);
                        if (NewInsArAmt != NewInsparm.getDouble("AR_AMT")) {
            				messageBox("分割数据有误,请重新分割");
            			    return false;  
            			}
                	}else{//新生儿不是医保，不能保存
                		this.messageBox("此医保患者的新生儿无医保，不能合并打印账单");
                		setValue("NEW_BABY_FLG", "N");
                		return false;
                	}
                }
            }     
        }
        int billDCount = endChargeParm.getCount("BILL_NO");
        if (null!=this.getValue("LUMPWORK_CODE") && this.getValue("LUMPWORK_CODE").toString().length()>0) {//校验是否使用套餐，如果使用套餐账单必须全部勾选
        	 if (getTTable("BillTable").getParmValue().getCount()!=billDCount) {
        		 this.messageBox("此用户使用套餐，账单必须全部操作");
				return false;
			}
		}
        
        double totAmt = 0.00;
        TParm endBillDParm = new TParm();
        String approveFlg = "";
        List bilrecGroup = new ArrayList(); // ========pangben modfiy 20110603
        // endBillDParm 分组
        String billNoSum="";
        for (int i = 0; i < billDCount; i++) {
            approveFlg = endChargeParm.getValue("APPROVE_FLG", i);
            if ("N".equals(approveFlg) || approveFlg.length() == 0) {
                this.messageBox("账单未审核");
                return false;
            }
            String billNo = endChargeParm.getValue("BILL_NO", i);
            billNoSum+="'"+endChargeParm.getValue("BILL_NO", i)+"',";
            int bilSeq = endChargeParm.getInt("BILL_SEQ", i);
           
            TParm inBillDParm = new TParm();
            inBillDParm.setData("BILL_NO", billNo);
            inBillDParm.setData("BILL_SEQ", bilSeq);
//            inBillDParm.setData("CASE_NO",caseNo);
            System.out.println("第一步查询数据校验_________________________________________"+inBillDParm);
            TParm billdParm = new TParm();
            // 账单明细档数据
            billdParm = IBSBilldTool.getInstance()
                        .selDataForCharge(inBillDParm);
            System.out.println("账单明细档数据_________________+++++++++++"+billdParm);
            int inBillDCount = billdParm.getCount("BILL_NO");
            for (int j = 0; j < inBillDCount; j++) {
                totAmt = totAmt + billdParm.getDouble("AR_AMT", j);
                endBillDParm
                        .addData("BILL_NO", billdParm.getData("BILL_NO", j));
                endBillDParm.addData("BILL_SEQ", billdParm.getData("BILL_SEQ",
                        j));
                endBillDParm.addData("REXP_CODE", billdParm.getData(
                        "REXP_CODE", j));
                endBillDParm
                        .addData("OWN_AMT", billdParm.getData("OWN_AMT", j));
                endBillDParm.addData("AR_AMT", billdParm.getData("AR_AMT", j));
                endBillDParm.addData("PAY_AR_AMT", billdParm.getData(
                        "PAY_AR_AMT", j));
                endBillDParm.addData("OPT_USER", billdParm.getData("OPT_USER",
                        j));
                endBillDParm.addData("OPT_DATE", billdParm.getData("OPT_DATE",
                        j));
                endBillDParm.addData("OPT_TERM", billdParm.getData("OPT_TERM",
                        j));
                if (!bilrecGroup.contains(billdParm.getData("REXP_CODE", j)
                                          + billNo))
                    bilrecGroup.add(billdParm.getValue("REXP_CODE", j)
                                    + billNo);
            }
        }
        System.out.println("endBillDParm数据整合_____________________"+endBillDParm);
        //========pangben 2014-4-14 添加预交金提示
   		TParm billPayTableParm =this.getTTable("BillPayTable").getParmValue();
   		boolean flg=true;
   		for (int i = 0; i < billPayTableParm.getCount(); i++) {
			if (!billPayTableParm.getBoolean("BILLPAY_SEL",i)) {
				flg=false;
				break;
			}
		}
   		if (!flg) {
			if(this.messageBox("提示","存在未勾选的预交金数据,是否继续",2)!=0)
				return false;
		}
   		//添加校验支付方式是否与应缴金额相同=====pangben 2014-5-6
   		TParm payTypeTParm=paymentTool.table.getParmValue();
   		TParm payTypeTParm2 = null;
   		try {
   			payTypeTParm2 = paymentTool.getAmts();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("payTypeTParm2 = : : : " + payTypeTParm2);
   		if (payTypeTParm.getCount("PAY_TYPE")<=0) {
   			this.messageBox("请选择缴费方式");
			return false;
		}
   		double payAmt=0.00;
   		for (int i = 0; i < payTypeTParm.getCount("PAY_TYPE"); i++) {
   			if (payTypeTParm.getValue("PAY_TYPE",i).equals("Z")) {
				this.messageBox("不存在工商圈存机支付方式");
				return false;
			}
   			if (payTypeTParm.getValue("PAY_TYPE",i).equals("C2")) {
				this.messageBox("不存在汇票支付方式");
				return false;
			}
   		
   			payAmt+=payTypeTParm.getDouble("AMT",i);
   			//----start-------add by kangy 20160708------微信支付宝支付需要添加交易号
   			boolean flg2=paymentTool.onCheckPayType(payTypeTParm);
   		    if (flg2) {
   		    } else {
   				this.messageBox("不允许出现相同的支付方式！");
   				return flg2;
   			}
   			//----end-----add by kangy 20160708------微信支付宝支付需要添加交易号
		}
   		payAmt += this.getValueDouble("PAY_MEDICAL_CARD"); //add by huangtt 20180523
   		
   		BigDecimal ar_amt_bd = new BigDecimal(this.getValueDouble("AR_AMT")).setScale(2, RoundingMode.HALF_UP); 
		BigDecimal sumPay_bd = new BigDecimal(payAmt).setScale(2, RoundingMode.HALF_UP);
		
		if(ar_amt_bd.compareTo(sumPay_bd) != 0){
			this.messageBox("缴费方式金额与应缴金额不等,不可以打票操作");
			return false;
		}  
   		                 // 现金    刷卡 支票  医院垫付             代金券      医疗卡   医保卡 保险直付
   		String [] typeName={"C0","C1","T0","C4","LPK","XJZKQ","EKT","INS","BXZF","WX","ZFB"};
   		String [] bilTypeName={"PAY_CASH","PAY_BANK_CARD","PAY_CHECK","PAY_DEBIT","PAY_GIFT_CARD",
   				"PAY_DISCNT_CARD","PAY_MEDICAL_CARD","PAY_INS_CARD","PAY_BXZF","PAY_TYPE09","PAY_TYPE10"};
        // ==============pangben modify 20110603 start 累计相同支付方式类型的金额
        TParm endBillDParms = new TParm();
        for (int i = 0; i < bilrecGroup.size(); i++) {
            double sumOwnAMT = 0.00;
            double sumArAMT = 0.00;
            for (int j = 0; j < endBillDParm.getCount("REXP_CODE"); j++) {
                if (bilrecGroup.get(i).equals(
                        endBillDParm.getValue("REXP_CODE", j)
                        + endBillDParm.getValue("BILL_NO", j))) {
                    sumOwnAMT += endBillDParm.getDouble("OWN_AMT", j);
                    sumArAMT += endBillDParm.getDouble("AR_AMT", j);
                }
            }
            endBillDParms
                    .addData("BILL_NO", endBillDParm.getData("BILL_NO", i));
            endBillDParms.addData("BILL_SEQ", endBillDParm.getData("BILL_SEQ",
                    i));
            endBillDParms.addData("REXP_CODE", endBillDParm.getData(
                    "REXP_CODE", i));
            endBillDParms.addData("OWN_AMT", sumOwnAMT);
//            System.out.println("第"+i+"次金额============="+sumArAMT);
            endBillDParms.addData("AR_AMT", sumArAMT);
            endBillDParms.addData("PAY_AR_AMT", endBillDParm.getData(
                    "PAY_AR_AMT", i));
            endBillDParms.addData("OPT_USER", endBillDParm.getData("OPT_USER",
                    i));
            endBillDParms.addData("OPT_DATE", endBillDParm.getData("OPT_DATE",
                    i));
            endBillDParms.addData("OPT_TERM", endBillDParm.getData("OPT_TERM",
                    i));
        }
        System.out.println("endBillDParms数据整合_____________________"+endBillDParms);
        // ==============pangben modify 20110603 stop
        // 住院流水号
        String receiptNo = SystemTool.getInstance().getNo("ALL", "IBS",
                "RECEIPT_NO", "RECEIPT_NO");
        TParm inAdmParm = new TParm();
        inAdmParm.setData("CASE_NO", caseNo);
        TParm admParm = ADMInpTool.getInstance().selectall(inAdmParm);
        String mrNo = admParm.getValue("MR_NO", 0);
        String ipdNo = admParm.getValue("IPD_NO", 0);
        String regionCode = admParm.getValue("REGION_CODE", 0);
        
      //=================================shibl 20140514 add=============================================
        TParm inParm=new TParm();
        inParm.setData("CASE_NO", caseNo);
        inParm.setData("MR_NO", mrNo);
        inParm.setData("IPD_NO",  ipdNo);
        Pat pat=Pat.onQueryByMrNo(mrNo);
        inParm.setData("PAT_NAME",pat.getName());
        inParm.setData("SEX_CODE", pat.getSexCode());
        inParm.setData("CTZ1_CODE", admParm.getValue("CTZ1_CODE", 0));
        inParm.setData("PRINT_NO", this.getValue("PRINT_NO"));
        inParm.setData("ADM_DATE",admParm.getData("IN_DATE", 0));
        inParm.setData("NEW_BABY_FLG",this.getValueString("NEW_BABY_FLG"));//包含婴儿账单
        inParm.setData("LUMPWORK_CODE", this.getValue("LUMPWORK_CODE"));//套餐代码
        TParm typeParm=new TParm();
        if(null!=this.getValue("LUMPWORK_CODE") && this.getValue("LUMPWORK_CODE").toString().length()>0){
        	 typeParm.addData("AMT", this.getValueDouble("LUMPWORK_OUT_AMT"));
        }else{
        	 typeParm.addData("AMT", this.getValueDouble("TOT_AMT"));
        }
        
        if(this.getValueString("BILL_TYPE").equals("C")){
        	typeParm.addData("PAY_TYPE", "C0");
        }else{
        	typeParm.addData("PAY_TYPE", "EKT");
        }
        typeParm.setCount(1);
        inParm.setData("TYPE_PARM",typeParm.getData());//支付方式显示
        inParm.setData("BILL_NO_SUM",billNoSum);
        inParm.setData("payTypeTParm",payTypeTParm.getData());
        TParm reduceParm=(TParm)openDialog("%ROOT%\\config\\bil\\BILIBSReduce.x", inParm);
        if(reduceParm==null)
        	return true;
        //=================================shibl 20140514 add=============================================
        // 收据主档数据
        TParm recpMParm = new TParm();
        recpMParm.setData("CASE_NO", caseNo);
        recpMParm.setData("RECEIPT_NO", receiptNo);
        recpMParm.setData("ADM_TYPE", "I");
        recpMParm.setData("IPD_NO", ipdNo);
        recpMParm.setData("MR_NO", mrNo);
        recpMParm.setData("REGION_CODE", regionCode);
        recpMParm.setData("RESET_RECEIPT_NO", "");
        recpMParm.setData("REFUND_FLG", "");
        recpMParm.setData("CASHIER_CODE", Operator.getID());
        recpMParm.setData("CHARGE_DATE", SystemTool.getInstance().getDate());
        recpMParm.setData("OWN_AMT", getValue("OWN_AMT") == null ? new TNull(
                String.class) : getValue("OWN_AMT"));
        recpMParm.setData("DISCT_RESON",
                          getValue("DISCT_RESON") == null ? new TNull(String.class)
                          : getValue("DISCT_RESON"));
        recpMParm.setData("DISCNT_AMT",
                          getValue("DISCNT_AMT") == null ? new TNull(String.class)
                          : getValue("DISCNT_AMT"));
        recpMParm.setData("PACK_DIFF_AMT",
                getValue("PACK_DIFF_AMT") == null ? new TNull(String.class)
                : getValue("PACK_DIFF_AMT"));//=====pangben 2015-7-28 添加套餐差异金额
        recpMParm.setData("LUMPWORK_OUT_AMT",
                getValue("LUMPWORK_OUT_AMT") == null ? new TNull(String.class)
                : getValue("LUMPWORK_OUT_AMT"));//添加套餐外金额
        recpMParm.setData("LUMPWORK_AMT",
                getValue("LUMPWORK_AMT") == null ? new TNull(String.class)
                : getValue("LUMPWORK_AMT"));//添加套餐金额
        //====zhangp 20120417 start
//        recpMParm.setData("AR_AMT", getValue("OWN_AMT") == null ? new TNull(
//                String.class) : getValue("OWN_AMT"));
        recpMParm.setData("AR_AMT", getValue("TOT_AMT") == null ? new TNull(
                String.class) : getValue("TOT_AMT"));
        //=====zhangp 20120417 end
        recpMParm.setData("REFUND_CODE", "");
        recpMParm.setData("REFUND_DATE", "");
        
        String cardTypeAndRemarks = "";
        for (int j = 0; j < bilTypeName.length; j++) {
        	recpMParm.setData(bilTypeName[j],0.00);
		}
        //==============pangben 2014-5-5 添加支付方式赋值
        for (int i = 0; i < payTypeTParm.getCount("PAY_TYPE"); i++) {
        	
        	for (int j = 0; j < typeName.length; j++) {
        		if (typeName[j].equals(payTypeTParm.getValue("PAY_TYPE",i))) {
        			recpMParm.setData(bilTypeName[j],recpMParm.getDouble(bilTypeName[j])+
        					payTypeTParm.getDouble("AMT",i));
        			
        		}
			}		
		}
        //增加卡支付类型和卡号 add by lich --------------start
        String remarkKey = "";
		String remark = "";
		String key = "";
		String sqlCardTypeAndRemark = "update bil_ibs_recpm set ";
		for (int i = 1; i < 11; i++) {
			if(i < 10){
				key = "PAY_TYPE0" + i;
				remarkKey="MEMO"+i;
			}else{
				key = "PAY_TYPE" + i;
				remarkKey="MEMO"+i;
			}
			remark="";
			for (int j = 0; j < payTypeTParm2.getCount("PAY_TYPE"); j++) {
				if(key.equals(payTypeTParm2.getValue("PAY_TYPE", j))){
					if(("PAY_TYPE02".equals(payTypeTParm2.getValue("PAY_TYPE", j)))
							||("PAY_TYPE09".equals(payTypeTParm2.getValue("PAY_TYPE", j)))
							||("PAY_TYPE10".equals(payTypeTParm2.getValue("PAY_TYPE", j)))){// modify by kangy20171018 微信支付宝保存卡类型
						remark = payTypeTParm2.getValue("CARD_TYPE",j)+ "#" +payTypeTParm2.getValue("REMARKS",j);//刷卡收费，添加卡类型及卡号add by huangjw 20141230
					}else{
						remark = payTypeTParm2.getValue("REMARKS",j);
					}
					break;
				}
			}
			recpMParm.setData(remarkKey,remark);//刷卡收费，添加卡类型及卡号
			
			sqlCardTypeAndRemark += (remarkKey + " = '" + remark + "' ," );
		}
		sqlCardTypeAndRemark = sqlCardTypeAndRemark.substring(0, sqlCardTypeAndRemark.length()-1);
		//pangben 2016-8-9微信支付宝添加交易号码
		TParm payCashParm=null;
        if(null!=reduceParm.getParm("payCashParm")){
        	payCashParm=reduceParm.getParm("payCashParm");
        	if(null!=payCashParm.getValue("WX")&&payCashParm.getValue("WX").length()>0){
        		sqlCardTypeAndRemark+=", WX_BUSINESS_NO='"+payCashParm.getValue("WX")+"'";
			}
			if(null!=payCashParm.getValue("ZFB")&&payCashParm.getValue("ZFB").length()>0){
				sqlCardTypeAndRemark+=", ZFB_BUSINESS_NO='"+payCashParm.getValue("ZFB")+"'";
			}
        }	
		sqlCardTypeAndRemark += " WHERE CASE_NO = '"+caseNo+"' AND RECEIPT_NO = '"+receiptNo+"'";
		
		//增加卡支付类型和卡号 add by lich --------------end
		
		//医疗卡20180523 
        recpMParm.setData("PAY_MEDICAL_CARD", this.getValueDouble("PAY_MEDICAL_CARD"));
        

        recpMParm.setData("PAY_BILPAY",
                          getValue("PAY_BILPAY") == null ? 0 //new TNull(String.class) 改为 0 
                          : getValue("PAY_BILPAY"));
        
        recpMParm.setData("PAY_INS", getValue("PAY_INS") == null ? new TNull(
                String.class) : getValue("PAY_INS"));
        recpMParm.setData("PAY_OTHER1",
                          getValue("PAY_OTHER1") == null ? new TNull(String.class)
                          : getValue("PAY_OTHER1"));
        recpMParm.setData("PAY_OTHER2",
                          getValue("PAY_OTHER2") == null ? new TNull(String.class)
                          : getValue("PAY_OTHER2"));
        recpMParm.setData("PAY_REMK", getValue("PAY_REMK") == null ? new TNull(
                String.class) : getValue("PAY_REMK"));
        recpMParm.setData("PREPAY_WRTOFF", 0.00);
        recpMParm.setData("PRINT_NO", this.getValueString("PRINT_NO"));
        recpMParm.setData("OPT_USER", Operator.getID());
        recpMParm.setData("OPT_TERM", Operator.getIP());
        if (((TCheckBox)this.getComponent("TAX_FLG")).isSelected()) {//打票选中操作====pangben 2014-5-12
        	recpMParm.setData("TAX_FLG", "Y");
        	recpMParm.setData("TAX_USER", Operator.getID());
            recpMParm.setData("TAX_DATE", SystemTool.getInstance().getDate());
		}else{
			recpMParm.setData("TAX_FLG", "N");
        	recpMParm.setData("TAX_USER", "");
            recpMParm.setData("TAX_DATE", "");
		}
        // 收据条数
        int recpDCount = endBillDParms.getCount("BILL_NO");
        // 收据明细档数据
        TParm recpDParm = new TParm();
        double recpDAmtCheck = 0.00;
        for (int j = 0; j < recpDCount; j++) {
            recpDParm.addData("RECEIPT_NO", receiptNo);
            recpDParm.addData("BILL_NO", endBillDParms.getData("BILL_NO", j)); // =====pangben
            // modify
            // 21010603
            recpDParm.addData("REXP_CODE", endBillDParms
                              .getData("REXP_CODE", j)); // =====pangben modify 21010603
            recpDParm
                    .addData("WRT_OFF_AMT", endBillDParms.getData("AR_AMT", j)); // =====pangben
            recpDAmtCheck += Double.parseDouble((endBillDParms.getData("AR_AMT", j) == null ?
            		0.00 :endBillDParms.getData("AR_AMT", j) ).toString()); //yanmm
            // modify
            // 21010603
            recpDParm.addData("OPT_USER", Operator.getID());
            recpDParm.addData("OPT_TERM", Operator.getIP());
        }
        System.out.println("BIL_IBS_RECPD数据整合>>>>>>>>>>>>>>>>>>>>>>>>>"+recpDParm);
        // 预交金数据
        TParm bilPayParm = new TParm();
        TParm inBilPayParm = new TParm();
        int bilPayCount = endBillPayParm.getCount("RECEIPT_NO");
        for (int i = 0; i < bilPayCount; i++) {
            bilPayParm = BILPayTool.getInstance().selAllDataByRecpNo(
                    endBillPayParm.getValue("RECEIPT_NO", i));
            inBilPayParm.addData("IBS_RECEIPT_NO", receiptNo);
            inBilPayParm.addData("RECEIPT_NO", bilPayParm.getData("RECEIPT_NO",
                    0));
            inBilPayParm.addData("CASE_NO", bilPayParm.getData("CASE_NO", 0));
            inBilPayParm.addData("IPD_NO", bilPayParm.getData("IPD_NO", 0));
            inBilPayParm.addData("MR_NO", bilPayParm.getData("MR_NO", 0));
            inBilPayParm.addData("TRANSACT_TYPE", bilPayParm.getData(
                    "TRANSACT_TYPE", 0));
            inBilPayParm.addData("REFUND_FLG", bilPayParm.getData("REFUND_FLG",
                    0));
            inBilPayParm.addData("RESET_BIL_PAY_NO", bilPayParm.getData(
                    "RESET_BIL_PAY_NO", 0));
            inBilPayParm.addData("RESET_RECP_NO", bilPayParm.getData(
                    "RESET_RECP_NO", 0));
            inBilPayParm.addData("CASHIER_CODE", bilPayParm.getData(
                    "CASHIER_CODE", 0));
            inBilPayParm.addData("CHARGE_DATE", bilPayParm.getData(
                    "CHARGE_DATE", 0));
            inBilPayParm.addData("ADM_TYPE", bilPayParm.getData("ADM_TYPE", 0));
            inBilPayParm.addData("PRE_AMT", bilPayParm.getData("PRE_AMT", 0));
            inBilPayParm.addData("PAY_TYPE", bilPayParm.getData("PAY_TYPE", 0));
            inBilPayParm.addData("CHECK_NO", bilPayParm.getData("CHECK_NO", 0));
            inBilPayParm.addData("REMARK", bilPayParm.getData("REMARK", 0));
            inBilPayParm.addData("REFUND_CODE", bilPayParm.getData(
                    "REFUND_CODE", 0));
            inBilPayParm.addData("REFUND_DATE", bilPayParm.getData(
                    "REFUND_DATE", 0));
            inBilPayParm.addData("PRINT_NO", bilPayParm.getData("PRINT_NO", 0));
            inBilPayParm.addData("OPT_USER", Operator.getID());
            inBilPayParm
                    .addData("OPT_DATE", SystemTool.getInstance().getDate());
//            bilPayDate=SystemTool.getInstance().getDate().toString();
//            bilPayDate=bilPayDate.substring(0, bilPayDate.indexOf("."));
            inBilPayParm.addData("OPT_TERM", Operator.getIP());
        }
  
        TParm allParm = new TParm();
        if(reduceParm!=null){
     	   allParm.setData("BIL_REDUCE", reduceParm.getData());// shibl  add  20140514
        }else{
        	allParm.setData("BIL_REDUCE", (new TParm()).getData());
        }
//        System.out.println("endBillDParms"+endBillDParms);
        allParm.setData("BILLD", endBillDParms.getData()); // =====pangben
        // modify 21010603
        allParm.setData("RECPM", recpMParm.getData());

        allParm.setData("BILLTABLE", BillParm.getData());//账单明细表格数据--xiongwg20150402
        
        //===zhangp 20130717 start
        allParm.setData("TJINS", insValueParm.getData());
        //===zhangp 20130717 end
        
        // 校验细项金额和界面校验yanmm
        double totCheck = StringTool.getDouble(this.getValueString("TOT_AMT"));
        if(recpDAmtCheck != totCheck){
        	this.messageBox("主细项金额不同,不执行保存");
			return false;
        }
        allParm.setData("RECPD", recpDParm.getData());
        allParm.setData("BILPAY", inBilPayParm.getData());
        // 票据主档信息
        TParm printReceipt = new TParm();
        printReceipt.setData("UPDATE_NO", this.getValueString("PRINT_NO"));
        printReceipt.setData("RECP_TYPE", "IBS");
        printReceipt.setData("CASHIER_CODE", Operator.getID());
        printReceipt.setData("STATUS", "0");
        printReceipt.setData("START_INVNO", startInvno);
        // 票据明细档数据
        TParm bilInvrcpt = new TParm();
        bilInvrcpt.setData("RECP_TYPE", "IBS");
        bilInvrcpt.setData("INV_NO", this.getValueString("PRINT_NO"));
        bilInvrcpt.setData("RECEIPT_NO", receiptNo);
        bilInvrcpt.setData("CASHIER_CODE", Operator.getID());
        bilInvrcpt.setData("AR_AMT", this.getValueString("TOT_AMT"));
        bilInvrcpt.setData("OPT_USER", Operator.getID());
        bilInvrcpt.setData("OPT_TERM", Operator.getIP());
        allParm.setData("BIL_INVOICE", printReceipt.getData());
        allParm.setData("BIL_INVRCP", bilInvrcpt.getData());
        allParm.setData("sqlCardTypeAndRemark",sqlCardTypeAndRemark);
//        System.out.println("allParm:::"+allParm);
        
        if(this.getValueBoolean("tRadioButton_2")){
        	if(this.onPayOther()){//未收费
				return false;
			}
    		
    		if(parmEKT == null){
    			this.messageBox("请读取医疗卡信息");
    			return false;
    		}
    		if (!EKTIO.getInstance().ektSwitch()) {
    			messageBox_("医疗卡流程没有启动!");
    			return false;
    		}
    		TParm parm = new TParm();
    		parm.setData("READ_CARD", parmEKT.getData());
    		parm.setData("PAY_OTHER3",this.getValueDouble("PAY_OTHER3"));
    		parm.setData("PAY_OTHER4",this.getValueDouble("PAY_OTHER4"));
    		if(reduceParm != null){
        		parm.setData("EXE_AMT",this.getValueDouble("PAY_MEDICAL_CARD")-reduceParm.getDouble("REDUCE_AMT"));

    		}else{
        		parm.setData("EXE_AMT",this.getValueDouble("PAY_MEDICAL_CARD"));

    		}
    		parm.setData("MR_NO",mrNo);
    		parm.setData("BUSINESS_TYPE","IBS");
    		parm.setData("CASE_NO",caseNo);//交易号
    		Object r =  this.openDialog("%ROOT%\\config\\ekt\\EKTChageOtherUI.x",parm);
    		if(r == null){
    			this.messageBox("医疗卡扣款取消，不执行保存");
    			return false;
    		}
    		TParm rParm = (TParm) r;
    		if(rParm.getErrCode() < 0){
    			this.messageBox(rParm.getErrText());
    			return false;
    		}else if(rParm.getValue("OP_TYPE").equals("2")){
    			return false;
    		}else{
    			allParm.setData("ektSql", rParm.getData("ektSql"));
    			String updateEktTradeSql = "update bil_ibs_recpm set EKT_BUSINESS_NO='"+rParm.getValue("TRADE_NO")+"' WHERE CASE_NO = '"+caseNo+"' AND RECEIPT_NO = '"+receiptNo+"'";
    			allParm.setData("updateEktTradeSql", updateEktTradeSql);
    		}
        }
        
        TParm result = TIOM_AppServer.executeAction("action.ibs.IBSAction",
                "onSaveIBSCharge", allParm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            flag=false;
            this.messageBox("操作失败:"+result.getErrText());
            return false;
        } else {
        	flag=true;// 2014、5、28 add caoyong 缴费成功
        	
        	
        	//===zhangp 20120803 start
    		TParm printParm = getPrintData(caseNo,receiptNo);           
            //===zhangp 20120803 end
            Timestamp printDate = (SystemTool.getInstance().getDate());
            TParm selAdmInp = new TParm();
            selAdmInp.setData("CASE_NO", caseNo);
            TParm admInpParm = ADMInpTool.getInstance().selectall(selAdmInp);
            Timestamp inDataOut = admInpParm.getTimestamp("IN_DATE", 0);
            Timestamp outDataOut;
            if (admInpParm.getData("DS_DATE", 0) != null)
                outDataOut = admInpParm.getTimestamp("DS_DATE", 0);

            else
                outDataOut = printDate;
            String inDate = StringTool.getString(inDataOut, "yyyyMMdd");
            String outDate = StringTool.getString(outDataOut, "yyyyMMdd");
            String pDate = StringTool.getString(SystemTool.getInstance()
                                                .getDate(), "yyyyMMdd");
            String sYear = inDate.substring(0, 4);
            String sMonth = inDate.substring(4, 6);
            String sDate = inDate.substring(6, 8);
            String eYear = outDate.substring(0, 4);
            String eMonth = outDate.substring(4, 6);
            String eDate = outDate.substring(6, 8);
            String pYear = pDate.substring(0, 4);
            String pMonth = pDate.substring(4, 6);
            String pDay = pDate.substring(6, 8);
            // int rollDate = StringTool.getDateDiffer(outDataOut, inDataOut) ==
            // 0 ?
            // 1 : StringTool.getDateDiffer(outDataOut, inDataOut);
            // ===========================add by wanglong 20130715
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 格式化日期类
            String strInDate = sdf.format(inDataOut);
            String strDsDate = sdf.format(outDataOut);
            Timestamp inDateByDay = java.sql.Timestamp.valueOf(strInDate + " 00:00:00.000");
            Timestamp outDateByDay = java.sql.Timestamp.valueOf(strDsDate + " 00:00:00.000");
            int stayDays = StringTool.getDateDiffer(outDateByDay, inDateByDay);// 计算住院天数
            stayDays = (stayDays == 0 ? 1 : stayDays);
//            int rollDate = StringTool.getDateDiffer(outDataOut, inDataOut);
            int rollDate = stayDays;
            // ===========================add end
            printParm.setData("COPY", "TEXT", "");
            printParm.setData("sYear", "TEXT", sYear);
            printParm.setData("sMonth", "TEXT", sMonth);
            printParm.setData("sDate", "TEXT", sDate);
            printParm.setData("pYear", "TEXT", pYear);
            printParm.setData("pMonth", "TEXT", pMonth);
            printParm.setData("pDay", "TEXT", pDay);
            //====zhangp 20120224 start
            inDate = StringTool.getString(inDataOut, "yyyy/MM/dd");
            outDate = StringTool.getString(outDataOut, "yyyy/MM/dd");
//            printParm.setData("STARTDATE", "TEXT", inDate);//入院日期
//            printParm.setData("ENDDATE", "TEXT", outDate);//出院日期 
            printParm.setData("STARTDATE", inDate);//liling
            printParm.setData("ENDDATE", outDate);//liling
            printParm.setData("PRINTDATE", "TEXT", pDate);
            //=====zhangp 20120224 end
            printParm.setData("eYear", "TEXT", eYear);
            printParm.setData("eMonth", "TEXT", eMonth);
            printParm.setData("eDate", "TEXT", eDate);
//            printParm.setData("rollDate", "TEXT", rollDate);//天数
            printParm.setData("rollDate",  rollDate);//liling
            printParm.setData("PAT_NAME", "TEXT", patName);
            printParm.setData("SEX_CODE", "TEXT", getDesc(sexCode));
            printParm.setData("STATION_CODE", "TEXT", getStation(stationCode));
//            printParm.setData("RECEIPT_NO", "TEXT", receiptNo);
            printParm.setData("CASHIER_CODE", "TEXT", Operator.getName());
            printParm.setData("MR_NO", "TEXT", mrNoOut);
          //  System.out.println("MR_NO====="+mrNoOut);
            printParm.setData("BILL_DATE", "TEXT", printDate);
            printParm.setData("CHARGE_DATE", "TEXT", printDate);
            printParm.setData("DEPT_CODE", "TEXT", getDept(deptCodeOut));
//            printParm.setData("OPT_USER", "TEXT", Operator.getName());
            printParm.setData("OPT_USER", "TEXT", Operator.getID());// modify by wanglong 20130123
            printParm.setData("OPT_DATE", "TEXT", printDate);
            String printDateC = StringTool.getString(printDate, "yyyy年MM月dd日");
            printParm.setData("DATE", "TEXT", printDateC);
            printParm.setData("NO1", "TEXT", receiptNo);//==流水号
            printParm.setData("NO2", "TEXT", this.getValueString("PRINT_NO"));//票据号
            printParm
                    .setData("HOSP", "TEXT", Operator.getHospitalCHNFullName());
            printParm.setData("IPD_NO", "TEXT", ipdNo);
            printParm.setData("BIL_PAY_C", "TEXT", StringUtil.getInstance()
                              .numberToWord(
                                      Double.parseDouble(String.valueOf(this
                    .getValue("PAY_BILPAY") == null ? "0.00" :
                    getValue("PAY_BILPAY")))));
            printParm.setData("BIL_PAY", "TEXT", this.getValue("PAY_BILPAY"));
            DecimalFormat df = new DecimalFormat("##########0.00");
            double amtD = StringTool.getDouble(this.getValueString("AR_AMT")) <
                          0 ? -StringTool
                          .getDouble(this.getValueString("AR_AMT"))
                          : StringTool.getDouble(this.getValueString("AR_AMT"));
            double tot = StringTool.getDouble(this.getValueString("TOT_AMT"));
            String amt = df.format(amtD).toString();
            String totAmtS = df.format(tot);
            
            //修改 金额为分的，后面不能加正或整字样 caoyong 20130718
            String tmp=StringUtil.getInstance().numberToWord(Double.parseDouble(totAmtS));
            if(tmp.lastIndexOf("分") > 0){
            	tmp = tmp.substring(0,tmp.lastIndexOf("分")+1);//取得正确的大写金额
            }
            printParm.setData("amtToWord", "TEXT", tmp);
            printParm.setData("AR_AMT", "TEXT", StringTool.getDouble(totAmtS));
            if (StringTool.getDouble(this.getValueString("AR_AMT")) < 0) {
                printParm.setData("XS", "TEXT", 0.00);
                printParm.setData("YT", "TEXT", amt);
            } else {
                printParm.setData("XS", "TEXT", amt);
                printParm.setData("YT", "TEXT", 0.00);
            }
            //====pangben 20140506 修改打票显示金额
            double arAmt = this.getValueDouble("AR_AMT");
            double payOtherAmt=0.00;
            double cashAmt=0.00;
            double reduceAmt=0.00;
            if(reduceParm!=null&&reduceParm.getValue("REDUCEFLG").equals("Y")){
            	reduceAmt=reduceParm.getDouble("REDUCE_AMT");
            }
            for (int i = 0; i < payTypeTParm.getCount("PAY_TYPE"); i++) {
        		if (payTypeTParm.getValue("PAY_TYPE", i).equals("C0")) {
        			cashAmt+=payTypeTParm.getDouble("AMT",i);
				}
        		if (payTypeTParm.getValue("PAY_TYPE", i).equals("C4")
        				||payTypeTParm.getValue("PAY_TYPE", i).equals("C1")
        				||payTypeTParm.getValue("PAY_TYPE", i).equals("T0")) {
        			payOtherAmt+=payTypeTParm.getDouble("AMT",i);
        		}
        	}
            if (arAmt >= 0) {
            	printParm.setData("ADDAMT", "TEXT",
                        Math.abs(StringTool.round(cashAmt, 2)-reduceAmt)); //补缴
                //===zhangp 20120331 start
                printParm.setData("ADDCHECKAMT", "TEXT", Math.abs(
                        StringTool.round(payOtherAmt, 2)
                                  )); //补缴
            } else {
                printParm.setData("BACKAMT", "TEXT",
                                  Math.abs(StringTool.round(cashAmt, 2)-reduceAmt)); //退(取绝对值)
                printParm.setData("BACKCHECKAMT", "TEXT", Math.abs(
                        StringTool.round(
                        		payOtherAmt, 2))); //补缴
            }
            //=======2014-5-6===end
//            TTable table = getTTable("BillTable");
//            TParm tableParm = table.getParmValue();
////            int i = table.getSelectedRow();
////            String caseNo = tableParm.getData("CASE_NO", i).toString();
//            String caseNo = tableParm.getData("CASE_NO", 0).toString();//xiongwg20150402
            TParm insParm = getInsParm(caseNo);
            if (insParm.getErrCode() < 0) {
                return false;
            }
            double armyAi_amt = insParm.getDouble("ARMYAI_AMT"); //补助
            double account_pay_amt = insParm.getDouble("ACCOUNT_PAY_AMT"); //个人账户
            double nhi_comment = insParm.getDouble("NHI_COMMENT"); //救助
            double nhi_pay = insParm.getDouble("NHI_PAY"); //统筹
            double ins = armyAi_amt + nhi_comment + nhi_pay; //保险给付
            double pay_cash = insParm.getDouble("OWN_PAY"); //现金支付/自付
            if (insParm.getCount() > 0) {
                printParm.setData("PAY_INS_CARD", "TEXT",
                                  Math.abs(StringTool.round(
                                          account_pay_amt,
                                          2)));
                printParm.setData("PAY_INS", "TEXT",
                                  Math.abs(StringTool.round(nhi_pay,
                        2)));
                printParm.setData("PAY_INS_PERSON", "TEXT",
                                  Math.abs(StringTool.round(
                                          pay_cash
                                          , 2)));
                printParm.setData("PAY_INS_CASH", "TEXT",
                                  Math.abs(StringTool.round(
                                          pay_cash
                                          , 2)));
                printParm.setData("PAY_INS_BIG","TEXT",Math.abs(StringTool.round(
                		nhi_comment
                        , 2)));//caowl 20130318 报表显示上增加大额支付//zhangp 20130717
                //===zhangp 20120412 end
            } else {
                printParm.setData("PAY_INS_CARD", "TEXT",
                                  Math.abs(StringTool.round(
                                          0.00,
                                          2)));
                printParm.setData("PAY_INS", "TEXT",
                                  Math.abs(StringTool.round(0.00,
                        2)));
                printParm.setData("PAY_INS_PERSON", "TEXT",
                                  Math.abs(StringTool.round(
                                          0.00,
                                          2)));
                printParm.setData("PAY_INS_CASH", "TEXT",
                                  Math.abs(StringTool.round(
                                          0.00,
                                          2)));
                printParm.setData("PAY_INS_BIG","TEXT",Math.abs(StringTool.round(
                       0.00 , 2)));//caowl 20130318 报表显示上增加大额支付
            }

            //==================liling start===================================
            DecimalFormat formatObject = new DecimalFormat("###########0.00");
//            System.out.println("receiptNo++++:"+receiptNo);
            String sql="SELECT  sum(OWN_AMT) as OWN_AMT,sum(PAY_BILPAY) as  PAY_BILPAY " +
     		"FROM BIL_IBS_RECPM WHERE REFUND_CODE IS NULL  AND AR_AMT >0  AND CASE_NO='" +caseNo+"' AND RECEIPT_NO='"+receiptNo+"' ";
//            System.out.println("sql++++"+sql);
            TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//            System.out.println("parm++++"+parm);
            String payWay="";
			double billPay = StringTool.round(parm.getDouble("PAY_BILPAY",0),2);//预交金总额
//			System.out.println("inBilPayParm=="+inBilPayParm);
//			System.out.println("inBilPayParmgetCount=="+inBilPayParm.getCount());
			if(inBilPayParm.getCount("RECEIPT_NO")>0){
			String receiptNo1=inBilPayParm.getValue("RECEIPT_NO");//缴费作业时勾选的预交金的RECEIPT_NO
         //   System.out.println("sql====<>"+sql);
			
			//控件找错group_id,GROUP_ID='SYS_PAYTYPE'是错的,正确的是GROUP_ID='GATHER_TYPE'--xiongwg20150409
       		String sql1 = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='GATHER_TYPE' ORDER BY ID ";
    
       		String sql2 = "SELECT sum(pre_amt) as pre_amt, pay_type FROM bil_pay  where case_no='" +caseNo +"'  " +
       				"AND (transact_type = '01' OR transact_type = '02' OR transact_type = '04'  OR transact_type = '03') and receipt_no in ('@')  group by pay_type ";      		
       		//==liling 20140811 add  OR transact_type = '04'  OR transact_type = '03' 每次收据召回，费用会回冲作为预交金        		
//       		System.out.println("receiptNo1=="+receiptNo1);
       		receiptNo1=receiptNo1.substring(1, receiptNo1.length()-1);
       		receiptNo1=receiptNo1.replaceAll(",", "','").replaceAll(" ", "");
       		sql2 = sql2.replaceAll("@",receiptNo1 );
//       		System.out.println("sql2=="+sql2);//===去掉sql2中不用的查询字段, case_no, ipd_no, mr_no,receipt_no
       		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
       		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));   		
//			System.out.println("parm2.getCount()==="+parm2.getCount());
			if(parm2.getCount()>0){//==liling 20140710 add 有无预交金判断
				for(int k=0;k<parm2.getCount();k++){
					String biltype = formatObject.format(parm2.getDouble("PRE_AMT", k));
					for(int j=0;j<parm1.getCount();j++){
					if(parm1.getValue("ID", j).equals(parm2.getValue("PAY_TYPE", k)))
						payWay +=parm1.getValue("CHN_DESC", j)+":"+biltype + "; ";
					//System.out.println("payWay==kpayWay="+k+"  j="+j+"  "+payWay);
					}		
				}
				payWay = payWay.substring(0, payWay.length()-2);
			}}
			printParm.setData("PAY_WAY", payWay);//预交金额   现金？预交金？
			printParm.setData("AR_AMT", formatObject.format(StringTool.getDouble(parm.getValue("OWN_AMT",0))));//自费金额是减免后需缴纳的金额
			//printParm.setData("AR_AMT",formatObject.format(getValueDouble("TOT_AMT")));
			printParm.setData("TOTAL_AW", StringUtil.getInstance().numberToWord(StringTool.getDouble(parm.getValue("OWN_AMT",0))));
			double minus=StringTool.getDouble(parm.getValue("OWN_AMT",0))-billPay;
			if(minus>0){
    			printParm.setData("ADD", formatObject.format(minus));
    			}
			if(minus<0){
    			printParm.setData("BACK",formatObject.format(-minus));}
    		 //==================liling end===================================
			//add by sunqy 住院结算收据添加支付明细   ----start----
			Map<String, String> map = new HashMap<String, String>();
			map.put("PAY_CASH", "现金");
			map.put("PAY_MEDICAL_CARD", "医疗卡");
			map.put("PAY_BANK_CARD", "银行卡");
			map.put("PAY_INS_CARD", "医保卡");
			map.put("PAY_CHECK", "支票");
			map.put("PAY_DEBIT", "记账");
			map.put("PAY_BILPAY", "预交金冲销");
			map.put("PAY_INS", "医保统筹记账");
			map.put("PAY_OTHER1", "其他支付一<慢病卡>");
			map.put("PAY_OTHER1", "其他支付二");
			map.put("PAY_GIFT_CARD", "礼品卡");
			map.put("PAY_DISCNT_CARD", "现金折扣券");
			map.put("PAY_BXZF", "保险直付");
			map.put("PAY_TYPE09", "微信");
			map.put("PAY_TYPE10", "支付宝");
			String payDetail = "";
			String sql3 = "SELECT PAY_CASH, PAY_REMK, PAY_MEDICAL_CARD, PAY_BANK_CARD, PAY_INS_CARD, " +
					"PAY_CHECK, PAY_DEBIT, PAY_BILPAY, PAY_INS, PAY_OTHER1, PAY_OTHER2, PAY_GIFT_CARD, PAY_DISCNT_CARD, PAY_BXZF,PAY_TYPE09,PAY_TYPE10 " +
					"FROM BIL_IBS_RECPM WHERE RECEIPT_NO = '"+receiptNo+"' AND CASE_NO = '"+caseNo+"'";
			TParm parm3 = new TParm(TJDODBTool.getInstance().select(sql3)).getRow(0);
			String sql4 = "SELECT MEMO1,MEMO2,MEMO3,MEMO4,MEMO5,MEMO6,MEMO7,MEMO8,MEMO9,MEMO10,WX_BUSINESS_NO,ZFB_BUSINESS_NO FROM BIL_IBS_RECPM " +//modify by kangy 20171018 添加微信支付宝交易号
					"WHERE RECEIPT_NO = '"+receiptNo+"' AND CASE_NO = '"+caseNo+"'";
			TParm parm4 = new TParm(TJDODBTool.getInstance().select(sql4));
			String memo2 = parm4.getValue("MEMO2",0);
			String memo9= parm4.getValue("MEMO9",0);
			String memo10= parm4.getValue("MEMO10",0);
			String cardtypeString = "";
			String wxCardtypeString = "";
			String zfbCardtypeString = "";
			String wxBusinessNo="";//微信交易号
			String zfbBusinessNo="";//支付宝交易号
			String x = ";";
			if (!"".equals(memo2) && !"#".equals(memo2)) {// 存在卡类型和卡号
				String[] strArray = memo2.split("#");
				String card_Type[] = strArray[0].split(";");// 卡类型
				String reMark[] = strArray[1].split(";");// 卡号
				for (int m = 0; m < card_Type.length; m++) {
					if (null != card_Type[m] && !"".equals(card_Type[m])) {
						String cardsql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE ID='"
								+ card_Type[m]
								+ "' AND GROUP_ID='SYS_CARDTYPE'";
						TParm cardParm = new TParm(TJDODBTool.getInstance()
								.select(cardsql));
						if (null != reMark[m] && !"".equals(reMark[m])) {
							cardtypeString = cardtypeString
									+ cardParm.getValue("CHN_DESC", 0) + ":"
									+ reMark[m] + x;
						} else {
							cardtypeString = cardtypeString
									+ cardParm.getValue("CHN_DESC", 0) + ":"
									+ "" + x;
						}
					}else{
						continue;
					}
				}
				// System.out.println("cardtypeString = = = = = =" +
				// cardtypeString);
			}
			//==start==add by kangy 20171018 微信支付宝添加卡类型备注支付方式
			if (!"".equals(memo9) && !"#".equals(memo9)) {// 存在卡类型和卡号
				String[] strArray = memo9.split("#");
				String card_Type[] = strArray[0].split(";");// 卡类型
				String reMark[] = strArray[1].split(";");// 备注
				wxBusinessNo=parm4.getValue("WX_BUSINESS_NO",0);
				for (int m = 0; m < card_Type.length; m++) {
					if (null != card_Type[m] && !"".equals(card_Type[m])) {
						String cardsql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE ID='"
								+ card_Type[m]
								+ "' AND GROUP_ID='SYS_CARDTYPE'";
						TParm cardParm = new TParm(TJDODBTool.getInstance()
								.select(cardsql));
						if (null != reMark[m] && !"".equals(reMark[m])) {
							wxCardtypeString = wxCardtypeString
									+ cardParm.getValue("CHN_DESC", 0) + " 备注:"
									+ reMark[m];
						} else {
							wxCardtypeString = wxCardtypeString
									+ cardParm.getValue("CHN_DESC", 0);
						}
						if(wxBusinessNo.length()>0){
							wxCardtypeString=wxCardtypeString+" 交易号:"+wxBusinessNo;
						}
					}else{
						continue;
					}
				}
			}
			if (!"".equals(memo10) && !"#".equals(memo10)) {// 存在卡类型和卡号
				String[] strArray = memo10.split("#");
				String card_Type[] = strArray[0].split(";");// 卡类型
				String reMark[] = strArray[1].split(";");// 备注
				zfbBusinessNo=parm4.getValue("ZFB_BUSINESS_NO",0);
				for (int m = 0; m < card_Type.length; m++) {
					if (null != card_Type[m] && !"".equals(card_Type[m])) {
						String cardsql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE ID='"
								+ card_Type[m]
								+ "' AND GROUP_ID='SYS_CARDTYPE'";
						TParm cardParm = new TParm(TJDODBTool.getInstance()
								.select(cardsql));
						if (null != reMark[m] && !"".equals(reMark[m])) {
							zfbCardtypeString = zfbCardtypeString
									+ cardParm.getValue("CHN_DESC", 0) + " 备注:"
									+ reMark[m];
						} else {
							zfbCardtypeString = zfbCardtypeString
									+ cardParm.getValue("CHN_DESC", 0);
						}
						if(zfbCardtypeString.length()>0){
							zfbCardtypeString=zfbCardtypeString+" 交易号:"+zfbBusinessNo;
						}
					}else{
						continue;
					}
				}
			}
			//==end==add by kangy 20171018 微信支付宝添加卡类型备注支付方式
			//cardtypeString=BILTool.getInstance().onShowpayTypeTransactionNo(memo9, memo10, cardtypeString);
//			printParm.setData("CARD_TYPE", cardtypeString);			

			if(parm3.getValue("PAY_CASH") != null && !"".equals(parm3.getValue("PAY_CASH")) && parm3.getDouble("PAY_CASH")!=0){
				payDetail += ";"+map.get("PAY_CASH")+parm3.getValue("PAY_CASH")+"元";
			}
			if(parm3.getValue("PAY_MEDICAL_CARD") != null && !"".equals(parm3.getValue("PAY_MEDICAL_CARD")) && parm3.getDouble("PAY_MEDICAL_CARD")!=0){
				payDetail += ";"+map.get("PAY_MEDICAL_CARD")+parm3.getValue("PAY_MEDICAL_CARD")+"元";
			}
			if(parm3.getValue("PAY_BANK_CARD") != null && !"".equals(parm3.getValue("PAY_BANK_CARD")) && parm3.getDouble("PAY_BANK_CARD")!=0){
				payDetail += ";"+map.get("PAY_BANK_CARD")+parm3.getValue("PAY_BANK_CARD")+"元 "+cardtypeString;			
			}
			if(parm3.getValue("PAY_INS_CARD") != null && !"".equals(parm3.getValue("PAY_INS_CARD")) && parm3.getDouble("PAY_INS_CARD")!=0){
				payDetail += ";"+map.get("PAY_INS_CARD")+parm3.getValue("PAY_INS_CARD")+"元";
			}
			if(parm3.getValue("PAY_CHECK") != null && !"".equals(parm3.getValue("PAY_CHECK")) && parm3.getDouble("PAY_CHECK")!=0){
				payDetail += ";"+map.get("PAY_CHECK")+parm3.getValue("PAY_CHECK")+"元";
			}
			if(parm3.getValue("PAY_DEBIT") != null && !"".equals(parm3.getValue("PAY_DEBIT")) && parm3.getDouble("PAY_DEBIT")!=0){
				payDetail += ";"+map.get("PAY_DEBIT")+parm3.getValue("PAY_DEBIT")+"元";
			}
			if(parm3.getValue("PAY_BILPAY") != null && !"".equals(parm3.getValue("PAY_BILPAY")) && parm3.getDouble("PAY_BILPAY")!=0){
				payDetail += ";"+map.get("PAY_BILPAY")+parm3.getValue("PAY_BILPAY")+"元";
			}
			if(parm3.getValue("PAY_INS") != null && !"".equals(parm3.getValue("PAY_INS")) && parm3.getDouble("PAY_INS")!=0){
				payDetail += ";"+map.get("PAY_INS")+parm3.getValue("PAY_INS")+"元";
			}
			if(parm3.getValue("PAY_OTHER1") != null && !"".equals(parm3.getValue("PAY_OTHER1")) && parm3.getDouble("PAY_OTHER1")!=0){
				payDetail += ";"+map.get("PAY_OTHER1")+parm3.getValue("PAY_OTHER1")+"元";
			}
			if(parm3.getValue("PAY_OTHER2") != null && !"".equals(parm3.getValue("PAY_OTHER2")) && parm3.getDouble("PAY_OTHER2")!=0){
				payDetail += ";"+map.get("PAY_OTHER2")+parm3.getValue("PAY_OTHER2")+"元";
			}
			if(parm3.getValue("PAY_GIFT_CARD") != null && !"".equals(parm3.getValue("PAY_GIFT_CARD")) && parm3.getDouble("PAY_GIFT_CARD")!=0){
				payDetail += ";"+map.get("PAY_GIFT_CARD")+parm3.getValue("PAY_GIFT_CARD")+"元";
			}
			if(parm3.getValue("PAY_DISCNT_CARD") != null && !"".equals(parm3.getValue("PAY_DISCNT_CARD")) && parm3.getDouble("PAY_DISCNT_CARD")!=0){
				payDetail += ";"+map.get("PAY_DISCNT_CARD")+parm3.getValue("PAY_DISCNT_CARD")+"元";
			}
			if(parm3.getValue("PAY_BXZF") != null && !"".equals(parm3.getValue("PAY_BXZF")) && parm3.getDouble("PAY_BXZF")!=0){
				payDetail += ";"+map.get("PAY_BXZF")+parm3.getValue("PAY_BXZF")+"元";
			}
			if(parm3.getValue("PAY_TYPE09") != null && !"".equals(parm3.getValue("PAY_TYPE09")) && parm3.getDouble("PAY_TYPE09")!=0){
				payDetail += ";"+map.get("PAY_TYPE09")+parm3.getValue("PAY_TYPE09")+"元 "+wxCardtypeString;
			}
			if(parm3.getValue("PAY_TYPE10") != null && !"".equals(parm3.getValue("PAY_TYPE10")) && parm3.getDouble("PAY_TYPE10")!=0){
				payDetail += ";"+map.get("PAY_TYPE10")+parm3.getValue("PAY_TYPE10")+"元 "+zfbCardtypeString;
			}
			if (payDetail.length()>1) {
				payDetail = payDetail.substring(1, payDetail.length());
			}
			
			//增加显示套内结转和套外自付 xiongwg20150319 start
			String space = ";";
			String lumpWork="";
			String admsql = "SELECT LUMPWORK_CODE FROM ADM_INP WHERE CASE_NO='"
					+ caseNo + "'";
			TParm adm = new TParm(TJDODBTool.getInstance().select(admsql));
			String lumpworkCode = adm.getValue("LUMPWORK_CODE",0);
			if (!"".equals(lumpworkCode) && lumpworkCode.length()>0) {
//				String lumpsql="SELECT FEE FROM MEM_LUMPWORK WHERE LUMPWORK_CODE='"+
//					getValue("LUMPWORK_CODE")+"'";
//				TParm lumpworkParm = new TParm(TJDODBTool.getInstance().select(lumpsql));
				TParm lumpworkParm = new TParm();
//				lumpWork += "套内结转:"+df.format(lumpworkParm.getDouble("FEE",0))+"元";//套内结转，套餐金额
				String lumpoutsql="SELECT LUMPWORK_AMT,LUMPWORK_OUT_AMT,REDUCE_AMT FROM BIL_IBS_RECPM WHERE CASE_NO='" +
					caseNo+"' AND RECEIPT_NO='"+receiptNo+"' AND RESET_RECEIPT_NO IS NULL";//查询包干外的总金额
            	lumpworkParm = new TParm(TJDODBTool.getInstance().select(lumpoutsql));
            	lumpWork += "套内结转:"+df.format(lumpworkParm.getDouble("LUMPWORK_AMT",0))+"元";//套餐类型控件修改--xiongwg20150707
            	lumpWork +=";套外自付:"+df.format(lumpworkParm.getDouble("LUMPWORK_OUT_AMT",0)-
            			lumpworkParm.getDouble("REDUCE_AMT",0))+"元";
			}
			//System.out.println("lumpWork::::"+lumpWork);
//			if("".equals(cardtypeString)){
//				space = ";";
//			}
			//增加显示套内结转和套外自付 xiongwg20150319 end
			
			printParm.setData("PAY_DETAIL", payDetail+space+lumpWork);
			//add by sunqy 住院结算收据添加支付明细   ----end----
			//20141223 wangjingchun add start 854
			String patinfo_sql = "SELECT VALID_FLG, DEPT_FLG FROM MEM_INSURE_INFO WHERE MR_NO = '"+mrNoOut+"' ";
			TParm patinfo_parm = new TParm(TJDODBTool.getInstance().select(patinfo_sql));
			int i1 = 1;
			if(patinfo_parm.getValue("DEPT_FLG",0).equals("N")&&patinfo_parm.getValue("VALID_FLG",0).equals("Y")){
				i1=2;
			}
			//增加打印预览开关--xiongwg20150520 start
			boolean swFlg;//增加打印预览开关注记：true为打开;false为关闭
			//System.out.println("==="+IReportTool.getInstance().getPreviewSwitchFlg());
			swFlg=IReportTool.getInstance().getPreviewSwitchFlg("IBSRecp.prtSwitch");			
			if(swFlg){//预览开关"打开"
				this.openPrintDialog(IReportTool.getInstance().getReportPath("IBSRecp.jhw"),//====liling
						IReportTool.getInstance().getReportParm("IBSRecp.class", printParm), !swFlg);
			}
			//增加打印预览开关--xiongwg20150520 end
			else{//预览开关"关闭"
				for(int j=0;j<i1;j++){
					this.openPrintDialog(IReportTool.getInstance().getReportPath("IBSRecp.jhw"),//====liling
							IReportTool.getInstance().getReportParm("IBSRecp.class", printParm), !swFlg);//报表合并modify by wanglong 20130730
				}
			}
			
			//20141223 wangjingchun add end 854
        }
        this.messageBox("生成成功");
//        jsXML(); // 生成xml文件
        //=====shibl 20120806 start
        //更新病案首页，财务
        TParm mroParm = MRORecordTool.getInstance().updateMROIbsForIBS(caseNo);
        if(mroParm.getErrCode()<0){
        	messageBox("更新病案首页费用失败");
        }
        //=====shibl 20120806 end
        onClear();
        return true;
    }

    /**
     * 校验医保病患是否上传
     * @param parm TParm
     * @return boolean
     */
    public boolean checkInsUpload(TParm parm) {
        if (!"2".equals(parm.getValue("IN_STATUS", 0))&&!"4".equals(parm.getValue("IN_STATUS", 0)))
            return false;
        return true;
    }

    /**
     * 得到科室说明
     *
     * @param deptCode
     *            String
     * @return String
     */
    public String getDept(String deptCode) {
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                "SELECT DEPT_CODE,DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
                + deptCode + "'"));
        return parm.getValue("DEPT_CHN_DESC", 0);
    }

    /**
     * 得到姓名说明
     *
     * @param code
     *            String
     * @return String
     */
    public String getDesc(String code) {
        TParm descParm = new TParm();
        descParm
                .setData(TJDODBTool
                         .getInstance()
                         .select(
                                 "SELECT ID,CHN_DESC FROM  SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX'"));
        if (descParm.getCount() <= 0)
            return "";
        Vector vct = (Vector) descParm.getData("ID");
        int index = vct.indexOf(code);
        if (index < 0)
            return "";
        return descParm.getValue("CHN_DESC", index);
    }

    /**
     * 得到病区说明
     *
     * @param stationCode
     *            String
     * @return String
     */
    public String getStation(String stationCode) {
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                "SELECT STATION_CODE,STATION_DESC FROM SYS_STATION  WHERE STATION_CODE='"
                + stationCode + "'"));
        return parm.getValue("STATION_DESC", 0);
    }

    /**
     * 得到票据信息
     * @param caseNo String
     * @return TParm
     */
    public TParm getPrintData(String caseNo,String receipt_no) {
    	
        DecimalFormat formatObject = new DecimalFormat("###########0.00");
        String selRecpD =
                " SELECT D.RECEIPT_NO, D.REXP_CODE, D.WRT_OFF_AMT,M.REDUCE_AMT" +
                "   FROM BIL_IBS_RECPM M, BIL_IBS_RECPD D " +
                "  WHERE M.CASE_NO = '" + caseNo + "' " +
                "  AND M.RECEIPT_NO = '" + receipt_no + "'" +
                "    AND M.RECEIPT_NO = D.RECEIPT_NO ";
        // 查询不同支付方式付款金额(日结金额)
        TParm selRecpDParm = new TParm(TJDODBTool.getInstance()
                                       .select(selRecpD));
        int recpDCount = selRecpDParm.getCount("REXP_CODE");
        TParm charge = new TParm();
        TParm chargeParm = new TParm();
        //====================20140609 liling  modify start 查出各项收费对应的中文=====
		String sql="SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE' ORDER BY ID ";
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
		String sql2= "SELECT ADM_TYPE,CHARGE01,CHARGE02,CHARGE03,CHARGE04, CHARGE05,CHARGE06,CHARGE07,CHARGE08,CHARGE09, "+
                     " CHARGE10,CHARGE11,CHARGE12,CHARGE13,CHARGE14, CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20,CHARGE21 "+
                     " FROM BIL_RECPPARM   WHERE  ADM_TYPE='I'  " ;
		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2)); 
		String	id="";
		String	chargeCode ="";
		double wrtOffSingle = 0.00;
		double wrtOffAmt = 0.00;
		String rexpCode = "";
		for(int k=1;k<=21;k++){
			wrtOffAmt = 0.00;
			if (k < 10) {
				chargeCode =parm2.getValue("CHARGE0"+k, 0);
			for(int j=0;j<parm1.getCount();j++){
				id=parm1.getValue("ID", j);	
				if(id.equals(chargeCode)){
					charge.setData("CHARGE0" + k+"_D", parm1.getData("CHN_DESC", j));
					}
			}
			for(int h=0;h<recpDCount;h++){
				 rexpCode = selRecpDParm.getValue("REXP_CODE", h);
		         wrtOffSingle = selRecpDParm.getDouble("WRT_OFF_AMT", h);
		         if(rexpCode.equals(chargeCode)){
		        	 wrtOffAmt +=  wrtOffSingle;
		             String arAmtS = formatObject.format(wrtOffAmt);
		             chargeParm.setData("CHARGE0" + k, arAmtS);	
		             charge.setData("CHARGE0" + k,  chargeParm.getData("CHARGE0" + k) == null ? 0.00 : chargeParm.getData("CHARGE0" + k));
		         }
			}
			} else {
				chargeCode =parm2.getValue("CHARGE"+k, 0);
			for(int j=0;j<parm1.getCount();j++){
				id=parm1.getValue("ID", j);	
				if(!id.equals(chargeCode))continue;
				else{
					charge.setData("CHARGE" + k+"_D", parm1.getData("CHN_DESC", j));
					}
				}
			for(int h=0;h<recpDCount;h++){
				 rexpCode = selRecpDParm.getValue("REXP_CODE", h);
		         wrtOffSingle = selRecpDParm.getDouble("WRT_OFF_AMT", h);
		         if(rexpCode.equals(chargeCode)){
		        	 wrtOffAmt +=  wrtOffSingle;
		             String arAmtS = formatObject.format(wrtOffAmt);
		             chargeParm.setData("CHARGE" + k, arAmtS);	
		             charge.setData("CHARGE" + k,  chargeParm.getData("CHARGE" + k) == null ? 0.00 : chargeParm.getData("CHARGE" + k));
		         }
			}
			}
		}
		double charger03 = Double.parseDouble((charge.getData("CHARGE03") == null ? 0.00 :charge.getData("CHARGE03") ).toString());
		double charger04 = Double.parseDouble((charge.getData("CHARGE04") == null ? 0.00 :charge.getData("CHARGE04") ).toString());
		charge.setData("CHARGE001",formatObject.format(charger03+charger04).equals("0.00") ? "" :formatObject.format(charger03+charger04));//西药费数据
		//System.out.println("chargeParm====》"+chargeParm);
		charge.setData("CHARGE001_D", "西药费");
		//====================20140609 liling  modify end  ===========================
        double totAmt = 0.00;
//        double wrtOffSingle = 0.00;
//		String rexpCode = "";
        double reduceAmt=selRecpDParm.getDouble("REDUCE_AMT", 0);
//        TParm chargeParm = new TParm();
        for (int i = 0; i < recpDCount; i++) {
            rexpCode = selRecpDParm.getValue("REXP_CODE", i);
            wrtOffSingle = selRecpDParm.getDouble("WRT_OFF_AMT", i);
            totAmt = totAmt + wrtOffSingle;}
        charge.setData("TOT_AMT", "TEXT", formatObject.format(totAmt));
        charge.setData("AR_AMT", "TEXT", formatObject.format(totAmt));
        charge.setData("REDUCE_AMT", "TEXT", formatObject.format(reduceAmt));
        return charge;
    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue("TOT_AMT;OWN_AMT;PAY_INS;PAY_BILPAY;PAY_MEDICAL_CARD;"
                   + "PAY_INS_CARD;PAY_OTHER1;PAY_OTHER2;AR_AMT;PAY_CASH;"
                   + "PAY_CHECK;PAY_BANK_CARD;PAY_DEBIT;PAY_REMK;DISCNT_AMT;"
                   + "DISCT_RESON;NEW_BABY_FLG;PACK_DIFF_AMT");//===liling 20140419 添加NEW_BABY_FLG字段
        this.callFunction("UI|BillTable|removeRowAll");
        this.callFunction("UI|BillPayTable|removeRowAll");
        initPage(true);//==liling 20140421 add args
        insValueParm = new TParm();
        TParm parm=new TParm();
        parm.addData("PAY_TYPE","C0");
    	parm.addData("AMT",0.00);
    	parm.addData("REMARKS","");
		paymentTool.table.setParmValue(parm);
		parmEKT=null;
		TRadioButton cashPay = (TRadioButton) getComponent("tRadioButton_0");
		cashPay.setSelected(true);
		this.onGatherChange(0);
		this.clearValue("EKT_CURRENT_BALANCE;EKT_AMT;GIFT_CARD2;GIFT_CARD;NO_PAY_OTHER_ALL;PAY_OTHER4;PAY_OTHER3;NO_PAY_OTHER;");
		callFunction("UI|payLable|setVisible", false);  
    }

//	/**
//	 * 关闭事件
//	 *
//	 * @return boolean
//	 */
//	public boolean onClosing() {
//		switch (messageBox("提示信息", "是否保存?", this.YES_NO_CANCEL_OPTION)) {
//		case 0:
//			if (!onSave())
//				return false;
//			break;
//		case 1:
//			break;
//		case 2:
//			return false;
//		}
//		return true;
//	}

    /**
     * 校验开关帐
     *
     * @return boolean
     */
    public boolean checkNo() {
        TParm parm = new TParm();
        parm.setData("RECP_TYPE", "IBS");
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("STATUS", "0");
        parm.setData("TERM_IP", Operator.getIP());
        TParm noParm = BILInvoiceTool.getInstance().selectNowReceipt(parm);
        String updateNo = noParm.getValue("UPDATE_NO", 0);
        startInvno = "";
        startInvno = noParm.getValue("START_INVNO", 0);
        if (updateNo == null || updateNo.length() == 0) {
            return false;
        }
        // 初始化下一票号
        BilInvoice invoice = new BilInvoice();
        invoice = invoice.initBilInvoice("IBS");
        if (invoice.getUpdateNo().compareTo(invoice.getEndInvno()) > 0) {
            this.messageBox("票据已用完!");
            return false;
        }
        setValue("PRINT_NO", updateNo);
        return true;
    }

    /**
     * 校验冲销预交款,累计应缴总费用大小
     *
     * @param totAmt
     *            double
     * @return boolean
     */
    public boolean checkFee(double totAmt) {
        double bilPayAmt = 0.00;
        int feeCount = endBillPayParm.getCount("PRE_AMT");
        for (int j = 0; j < feeCount; j++) {
            bilPayAmt = bilPayAmt + endBillPayParm.getDouble("PRE_AMT", j);
        }
        if (bilPayAmt < totAmt)
            return false;
        return true;
    }

    /**
     * 重新出院
     */
    public void onOutHosp() {
        TParm sendParm = new TParm();
        // 病案号
        sendParm.setData("MR_NO", mrNoOut);
        // 住院号
        sendParm.setData("IPD_NO", ipdNoOut);
        // 就诊号
        sendParm.setData("CASE_NO", caseNo);
        // 姓名
        sendParm.setData("PAT_NAME", patName);
        // 性别
        sendParm.setData("SEX_CODE", sexCode);
        // 计算年龄
        if (birthDataOut == null)
            return;
        String AGE = com.javahis.util.StringUtil.showAge(birthDataOut,
                inDataOut);
        // 年龄
        sendParm.setData("AGE", AGE);
        // 床号
        sendParm.setData("BED_NO", bedNoOut);
        // 科室
        sendParm.setData("OUT_DEPT_CODE", deptCodeOut);
        // 病区
        sendParm.setData("OUT_STATION_CODE", stationCode);
        // 入院时间
        sendParm.setData("IN_DATE", inDataOut);

        //==========================zhangp start
        String sql = 
        	" SELECT CASE_NO" +
        	" FROM ADM_WAIT_TRANS" +
        	" WHERE CASE_NO = '" + caseNo + "'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if(result.getCount()>0){
        	messageBox("此病患在待转入或转出中,请入床,再出院");
        	return;
        }
        //==========================zhangp end
        
        TParm reParm = (TParm)this.openDialog(
                "%ROOT%\\config\\adm\\ADMOutInp.x", sendParm);

    }

    /**
     * 处理当前TOOLBAR
     */
    public void onShowWindowsFunction() {
        // 显示UIshowTopMenu
        callFunction("UI|showTopMenu");
    }

    /**
     * 出院结果计算数据文件
     */
    public void jsXML() {
        double phaAmt = 0.00;
        StringBuffer sql = new StringBuffer();
        sql
                .append(
                        "SELECT ORDER_CAT1_CODE ,TOT_AMT FROM IBS_ORDD WHERE CASE_NO='"
                        + caseNo + "'");
        TParm orderParm = new TParm(TJDODBTool.getInstance().select(
                sql.toString()));
        // 累计药品总价格
        for (int i = 0; i < orderParm.getCount(); i++) {
            if (orderParm.getValue("ORDER_CAT1_CODE", i).contains("PHA")) {
                phaAmt += orderParm.getDouble("TOT_AMT", i);
            }
        }
        DecimalFormat df = new DecimalFormat("##########0.00");
        // 1.构造数据
        TParm inparm = new TParm();
        inparm.insertData("TBR", 0, this.getValue("MR_NO")); // 病案号
        String name = null;
        if (this.getValueString("PAT_NAME").length() > 0)
            name = this.getValueString("PAT_NAME");
        else
            name = this.getValueString("PAT_NAMEOUT");
        inparm.insertData("XM", 0, name); // 姓名
        inparm.insertData("RYXZ", 0, "在职"); // 人员性质
        inparm.insertData("XH", 0, this.getValue("IPD_NO")); // 入院序号
        StringBuffer SQL = new StringBuffer();
        SQL.append("SELECT DS_DATE FROM ADM_INP WHERE MR_NO='" + mrNoOut
                   + "' AND CASE_NO='" + caseNo + "'");
        TParm result = new TParm(TJDODBTool.getInstance()
                                 .select(SQL.toString()));
        // 没有出院不生成xml
        if (null == result)
            return;
        Double totAmt = new Double(getValue("TOT_AMT").toString());
        String dDate = result.getValue("DS_DATE", 0).substring(0,
                result.getValue("DS_DATE", 0).indexOf(" ")).replace("-", "");
        inparm.insertData("CYSJ", 0, dDate); // 出院时间
        inparm.insertData("ZFY", 0, df.format(totAmt)); // 医疗费用合计
        inparm.insertData("YF", 0, phaAmt); // 药费合计
        inparm.insertData("XMF", 0, df.format(totAmt - phaAmt)); // 治疗项目费合计
        inparm.insertData("GRZL", 0, 0.00); // 个人自理
        inparm.insertData("GRZF", 0, df.format(this.getValue("OWN_AMT"))); // 个人自付
        inparm.insertData("YBZF", 0, ""); // 统筹大病和补助支付
        inparm.insertData("ZHYE", 0, "0"); // 个人账户余额
        inparm.insertData("ZHZF", 0, df.format(this.getValue("OWN_AMT"))); // 个人账户支付
        inparm.insertData("XJZF", 0, df.format(this.getValue("OWN_AMT"))); // 现金支付
        inparm.insertData("XZMC", 0, ""); // 险种
        inparm.insertData("DJH", 0, ""); // 单据号
        inparm.insertData("YSM", 0, ""); // 管床医生码
        inparm.insertData("YH1", 0, "0"); // 优惠1
        inparm.insertData("YH2", 0, "0"); // 优惠2
        inparm.insertData("YH3", 0, "0"); // 优惠3
        inparm.insertData("TCZF", 0, "0"); // 统筹支付
        inparm.insertData("DBZF", 0, "0"); // 大病支付
        inparm.insertData("BZZF", 0, "0"); // 补助支付
        inparm.addData("SYSTEM", "COLUMNS", "TBR");
        inparm.addData("SYSTEM", "COLUMNS", "XM");
        inparm.addData("SYSTEM", "COLUMNS", "RYXZ");
        inparm.addData("SYSTEM", "COLUMNS", "XH");
        inparm.addData("SYSTEM", "COLUMNS", "CYSJ");
        inparm.addData("SYSTEM", "COLUMNS", "ZFY");
        inparm.addData("SYSTEM", "COLUMNS", "YF");
        inparm.addData("SYSTEM", "COLUMNS", "XMF");
        inparm.addData("SYSTEM", "COLUMNS", "GRZL");
        inparm.addData("SYSTEM", "COLUMNS", "GRZF");
        inparm.addData("SYSTEM", "COLUMNS", "YBZF");
        inparm.addData("SYSTEM", "COLUMNS", "ZHYE");
        inparm.addData("SYSTEM", "COLUMNS", "ZHZF");
        inparm.addData("SYSTEM", "COLUMNS", "XJZF");
        inparm.addData("SYSTEM", "COLUMNS", "XZMC");
        inparm.addData("SYSTEM", "COLUMNS", "DJH");
        inparm.addData("SYSTEM", "COLUMNS", "YSM");
        inparm.addData("SYSTEM", "COLUMNS", "YH1");
        inparm.addData("SYSTEM", "COLUMNS", "YH2");
        inparm.addData("SYSTEM", "COLUMNS", "YH3");
        inparm.addData("SYSTEM", "COLUMNS", "TCZF");
        inparm.addData("SYSTEM", "COLUMNS", "DBZF");
        inparm.addData("SYSTEM", "COLUMNS", "BZZF");
        inparm.setCount(1);
//		System.out.println("=======inparm=============" + inparm);
        // 2.生成文件
        NJCityInwDriver.createXMLFile(inparm, "c:/NGYB/cyjsd.xml");
        this.messageBox("生成成功");
    }

    /**
     * 医保申报
     */
    public void onInsUpload() {
        //===zhangp 20120416 start
        TTable billPayTable = (TTable) getComponent("BillTable");
        TParm tableParm = billPayTable.getParmValue();
        int count = tableParm.getCount("BILL_NO");
        int countsel = 0;
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("PAY_SEL", i)) {
                countsel++;
            }
        }
//        if (countsel != count) {
//            messageBox("未选择全部账单");
//            return;
//        }
        //===zhangp 20120416 end
    	// ==========modify-begin (by wanglong 20120719)===============
        String repsql=
            "SELECT B.NHI_CTZ_FLG,A.BILL_STATUS"+
           	 " FROM ADM_INP A, SYS_CTZ B"+
           	" WHERE A.CASE_NO = '"+caseNo+"' AND A.CTZ1_CODE = B.CTZ_CODE";
        TParm rstParm = new TParm(TJDODBTool.getInstance().select(repsql));
        //System.out.println("申报的Parm:"+rstParm);
        if(rstParm.getCount()>0){//数据库是否有记录
            if(rstParm.getValue("NHI_CTZ_FLG",0).equals("Y")){//是否为医保用户
                if(!rstParm.getValue("BILL_STATUS",0).equals("3")){
                     messageBox("费用未审核");
                     return;
                 }
             }
        }else{
             messageBox("无记录");
             return;
        }
    	// ==========modify-end========================================
        TParm sendParm = new TParm();
//        caseNo = "120320000008";
        // 就诊号
        sendParm.setData("CASE_NO", caseNo);
//        TParm insAdmConfirmParm = INSADMConfirmTool.getInstance().queryADMConfirm(sendParm);
        String selInsIbs =
                " SELECT SUBSTR (B.HIS_CTZ_CODE, 0, 1) AS CTZ1_CODE, B.SDISEASE_CODE  " +
                "   FROM INS_IBS A,INS_ADM_CONFIRM B " +
                "  WHERE A.CASE_NO = '" + caseNo + "' " +
                "    AND A.CASE_NO = B.CASE_NO ";
        TParm selInsIbsParm = new TParm(TJDODBTool.getInstance().select(
                selInsIbs));
        if (selInsIbsParm.getErrCode() < 0) {
            this.messageBox("" + selInsIbsParm.getErrName());
            return;
        }
        if (selInsIbsParm.getCount() <= 0) {
            this.messageBox("无申报数据");
            return;
        }
        //1.城职 2.城居
        sendParm.setData("INS_PAT_TYPE", selInsIbsParm.getValue("CTZ1_CODE", 0));
        //1.普通 2.单病种
        sendParm.setData("SINGLE_TYPE",
                         selInsIbsParm.getValue("SDISEASE_CODE", 0).length() > 0 ?
                         2 : 1);
        sendParm.setData("INV_NO", this.getValueString("PRINT_NO"));
        TParm result = (TParm)this.openDialog(
                "%ROOT%\\config\\ins\\INSUpLoad.x", sendParm);
    }

    /**
     * 现金支付失去焦点事件
     */
    public void grabFocusPayCash() {
        setValue("PAY_BANK_CARD", TCM_Transform.getDouble(getValue("AR_AMT")) -
                 (TCM_Transform.getDouble(getValue("PAY_CASH")) +
                  TCM_Transform.getDouble(getValue("PAY_CHECK")) +
                  TCM_Transform.getDouble(getValue("PAY_DEBIT")) +
                  TCM_Transform.getDouble(getValue("DISCNT_AMT"))));
        this.grabFocus("PAY_BANK_CARD");

    }

    /**
     * 刷卡支付失去焦点事件
     */
    public void grabFocusPayBankCard() {
        setValue("PAY_CHECK", TCM_Transform.getDouble(getValue("AR_AMT")) -
                 (TCM_Transform.getDouble(getValue("PAY_CASH")) +
                  TCM_Transform.getDouble(getValue("PAY_BANK_CARD")) +
                  TCM_Transform.getDouble(getValue("PAY_DEBIT")) +
                  TCM_Transform.getDouble(getValue("DISCNT_AMT"))));
        this.grabFocus("PAY_CHECK");

    }

    /**
     * 支票支付失去焦点事件
     */
    public void grabFocusPayCheck() {
        setValue("PAY_DEBIT", TCM_Transform.getDouble(getValue("AR_AMT")) -
                 (TCM_Transform.getDouble(getValue("PAY_CASH")) +
                  TCM_Transform.getDouble(getValue("PAY_BANK_CARD")) +
                  TCM_Transform.getDouble(getValue("PAY_CHECK")) +
                  TCM_Transform.getDouble(getValue("DISCNT_AMT"))));
        this.grabFocus("PAY_DEBIT");

    }

    /**
     * 记账支付失去焦点事件
     */
    public void grabFocusPayDebit() {
        setValue("DISCNT_AMT", TCM_Transform.getDouble(getValue("AR_AMT")) -
                 (TCM_Transform.getDouble(getValue("PAY_CASH")) +
                  TCM_Transform.getDouble(getValue("PAY_BANK_CARD")) +
                  TCM_Transform.getDouble(getValue("PAY_CHECK")) +
                  TCM_Transform.getDouble(getValue("PAY_DEBIT"))));
        this.grabFocus("DISCNT_AMT");

    }

    /**
     * 其他支付失去焦点事件
     */
    public void grabFocusDiscntAmt() {
//        setValue("PAY_CASH", TCM_Transform.getDouble(getValue("AR_AMT")) -
//                 (TCM_Transform.getDouble(getValue("DISCNT_AMT")) +
//                  TCM_Transform.getDouble(getValue("PAY_BANK_CARD")) +
//                  TCM_Transform.getDouble(getValue("PAY_CHECK")) +
//                  TCM_Transform.getDouble(getValue("PAY_DEBIT"))));
//        this.grabFocus("PAY_CASH");
    	
    }

    /**
     * 校验合计金额
     * @return boolean
     */
    public boolean checkTotFee() {
        if (StringTool.round(TCM_Transform.getDouble(getValue("AR_AMT")) -
                             (TCM_Transform.getDouble(getValue("DISCNT_AMT")) +
                              TCM_Transform.getDouble(getValue("PAY_BANK_CARD")) +
                              TCM_Transform.getDouble(getValue("PAY_CHECK")) +
                              TCM_Transform.getDouble(getValue("PAY_DEBIT")) +
                              TCM_Transform.getDouble(getValue("PAY_CASH"))), 2
            ) != 0
                )
            return false;
        return true;
    }

    /**
     * 获得医保数据====zhangp 20120412
     * @param caseNo String
     * @return TParm
     */
    public TParm getInsParm(String caseNo) {
        String sql = "SELECT CONFIRM_NO,SDISEASE_CODE FROM INS_ADM_CONFIRM WHERE CASE_NO = '" +
                     caseNo + "' AND IN_STATUS <> '5' ";
        TParm confirmParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (confirmParm.getErrCode() < 0) {
            messageBox("医保读取错误");
            return confirmParm;
        }
        if (confirmParm.getCount() < 0) {
            //messageBox("无资格确认书");//=====pangben2014-4-8 非医保病患不提示
            return confirmParm;
        }else{
        	messageBox("存在资格确认书");
        }
        if((""+confirmParm.getData("SDISEASE_CODE", 0)).length()==0)
        //城职
        {
        	sql = "SELECT A.INSBASE_LIMIT_BALANCE, A.INS_LIMIT_BALANCE," +
              " B.RESTART_STANDARD_AMT, A.REALOWN_RATE, A.INSOWN_RATE,B.ARMYAI_AMT," +
              " (  CASE " +
              " WHEN ARMYAI_AMT IS NULL" +
              " THEN 0" +
              " ELSE ARMYAI_AMT" +
              " END" +
              " + CASE" +
              " WHEN TOT_PUBMANADD_AMT IS NULL" +
              " THEN 0" +
              " ELSE TOT_PUBMANADD_AMT" +
              " END" +
              " + B.NHI_PAY" +
//              " + CASE" +
//              " WHEN B.REFUSE_TOTAL_AMT IS NULL" +
//              " THEN 0" +
//              " ELSE B.REFUSE_TOTAL_AMT" +
//              " END" +
              " ) NHI_PAY," +
              " B.NHI_COMMENT," +
              " (  B.OWN_AMT" +
              " + B.ADD_AMT" +
              " + B.RESTART_STANDARD_AMT" +
              " + B.STARTPAY_OWN_AMT" +
              " + B.PERCOPAYMENT_RATE_AMT" +
              " + B.INS_HIGHLIMIT_AMT" +
              " - CASE" +
              " WHEN B.ACCOUNT_PAY_AMT IS NULL" +
              " THEN 0" +
              " ELSE B.ACCOUNT_PAY_AMT" +
              " END" +
              " - CASE" +
              " WHEN ARMYAI_AMT IS NULL" +
              " THEN 0" +
              " ELSE ARMYAI_AMT" +
              " END" +
              " - CASE" +
              " WHEN TOT_PUBMANADD_AMT IS NULL" +
              " THEN 0" +
              " ELSE TOT_PUBMANADD_AMT" +
              " END" +
              " ) OWN_PAY," +
              " (  B.NHI_COMMENT" +
              " + B.NHI_PAY" +
              " + B.OWN_AMT" +
              " + B.ADD_AMT" +
              " + B.RESTART_STANDARD_AMT" +
              " + B.STARTPAY_OWN_AMT" +
              " + B.PERCOPAYMENT_RATE_AMT" +
              " + B.INS_HIGHLIMIT_AMT" +
              " ) AR_AMT," +
              " (CASE" +
              " WHEN B.ACCOUNT_PAY_AMT IS NULL" +
              " THEN 0" +
              " ELSE B.ACCOUNT_PAY_AMT" +
              " END" +
              " ) ACCOUNT_PAY_AMT" +
              " FROM INS_ADM_CONFIRM A, INS_IBS B" +
              " WHERE B.REGION_CODE = '" + Operator.getRegion() + "'" +
              " AND A.CONFIRM_NO = '" + confirmParm.getData("CONFIRM_NO", 0) +
              "'" +
              " AND B.CASE_NO = '" + caseNo + "'" +
              " AND A.IN_STATUS IN ('1', '2', '3', '4')";
        }
        //单病种
        else
        {
        	sql = "SELECT A.INSBASE_LIMIT_BALANCE, A.INS_LIMIT_BALANCE," +
            " B.RESTART_STANDARD_AMT, A.REALOWN_RATE, A.INSOWN_RATE,B.ARMYAI_AMT," +
            " (  CASE " +
            " WHEN ARMYAI_AMT IS NULL" +
            " THEN 0" +
            " ELSE ARMYAI_AMT" +
            " END" +
            " + CASE" +
            " WHEN TOT_PUBMANADD_AMT IS NULL" +
            " THEN 0" +
            " ELSE TOT_PUBMANADD_AMT" +
            " END" +
            " + B.NHI_PAY" +
            " ) NHI_PAY," +
            " B.NHI_COMMENT," +
            " (  B.STARTPAY_OWN_AMT + B.BED_SINGLE_AMT + B.MATERIAL_SINGLE_AMT " +
            " + B.PERCOPAYMENT_RATE_AMT" +
            " + B.INS_HIGHLIMIT_AMT" +
            " - CASE" +
            " WHEN B.ACCOUNT_PAY_AMT IS NULL" +
            " THEN 0" +
            " ELSE B.ACCOUNT_PAY_AMT" +
            " END" +
            " - CASE" +
            " WHEN ARMYAI_AMT IS NULL" +
            " THEN 0" +
            " ELSE ARMYAI_AMT" +
            " END" +
            " - CASE" +
            " WHEN TOT_PUBMANADD_AMT IS NULL" +
            " THEN 0" +
            " ELSE TOT_PUBMANADD_AMT" +
            " END" +
            " ) OWN_PAY," +
            //单病种患者报销金额与总费用不等
            " (B.PHA_AMT  + B.EXM_AMT  + B.TREAT_AMT +  B.OP_AMT  +  B.BED_AMT  + B.MATERIAL_AMT + B.OTHER_AMT + B.BLOODALL_AMT  +  B.BLOOD_AMT )  AS AR_AMT, "+
            " (CASE" +
            " WHEN B.ACCOUNT_PAY_AMT IS NULL" +
            " THEN 0" +
            " ELSE B.ACCOUNT_PAY_AMT" +
            " END" +
            " ) ACCOUNT_PAY_AMT" +
            " FROM INS_ADM_CONFIRM A, INS_IBS B" +
            " WHERE B.REGION_CODE = '" + Operator.getRegion() + "'" +
            " AND A.CONFIRM_NO = '" + confirmParm.getData("CONFIRM_NO", 0) +
            "'" +
            " AND B.CASE_NO = '" + caseNo + "'" +
            " AND A.IN_STATUS IN ('1', '2', '3', '4')";        	
        }
//        System.out.println("校验医保申报sql" + sql);
        TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (insParm.getErrCode() < 0) {
            messageBox("医保读取错误");
            return insParm;
        }
        if (insParm.getCount() < 0) {
            messageBox("无医保数据");
            return insParm;
        }
        TParm result = new TParm();
        result = insParm.getRow(0);
        result.setCount(1);
        return result;
    }

    /**
     * 为页面上的医保赋值
     * ===zhangp 20120412
     */
    public void setInsValue() {
    	//====zhangp 20130717 start
    	//TODO
    	tjInsColumns = TJINSRecpTool.getInstance().getColumns();
    	insValueParm = new TParm();
    	TParm insParm = getInsParm(caseNo);
    	setTjInsValueParm("ARMYAI_AMT", insParm);//军残
    	setTjInsValueParm("ACCOUNT_PAY_AMT", insParm);//个人账户
    	setTjInsValueParm("NHI_COMMENT", insParm);//大额
    	setTjInsValueParm("NHI_PAY", insParm);//统筹
    	//====zhangp 20130717 end
        double armyAi_amt = insParm.getDouble("ARMYAI_AMT"); //补助
        double account_pay_amt = insParm.getDouble("ACCOUNT_PAY_AMT"); //个人账户
        double nhi_comment = insParm.getDouble("NHI_COMMENT"); //救助
        double nhi_pay = insParm.getDouble("NHI_PAY"); //统筹
        double ins = armyAi_amt + nhi_comment + nhi_pay; //保险给付
        double pay_cash = insParm.getDouble("OWN_PAY"); //现金支付/自付
        if (insParm.getCount() > 0) {
            setValue("PAY_INS_CARD",
                     Math.abs(StringTool.round(
                             account_pay_amt,
                             2)));
            setValue("PAY_INS",
                     Math.abs(StringTool.round(nhi_pay+nhi_comment,
                                               2)));
            setValue("AR_AMT",
                     Math.abs(StringTool.round(
                             pay_cash
                             , 2)));
            setValue("OWN_AMT",
                     Math.abs(StringTool.round(
                             pay_cash
                             , 2)));
            setValue("PAY_CASH",
                     Math.abs(StringTool.round(
                             pay_cash
                             , 2)));
        	setValue("tNumberTextField_1", Math.abs(StringTool.round(ins,2)));//caowl 20130318
        	
        }
    }
    /**
     * 天津医保放值
     * ===zhangp 20130717
     * @param name
     * @param insParm
     */
    private void setTjInsValueParm(String name,TParm insParm){
    	insValueParm.setData(tjInsColumns.getValue(name), insParm.getDouble(name));
    }
    /**
     * 勾选新生儿注记方法
     * 如果此病患存在新生儿数据，将数据显示到表格
     * ========pangben 2014-4-11 
     */
    public void onNewBabyBilIbsRecp(){
    	//选中操作
    	if (((TCheckBox)this.getComponent("NEW_BABY_FLG")).isSelected()) {
    		//查询是否存在新生儿数据
    		String sql = "SELECT A.MR_NO,A.DS_DATE,B.PAT_NAME,A.CASE_NO FROM ADM_INP A,SYS_PATINFO B WHERE A.MR_NO=B.MR_NO AND A.MR_NO LIKE '"+
    		mrNoOut+"%' AND IN_DATE IS NOT NULL AND CANCEL_FLG <> 'Y' AND NEW_BORN_FLG='Y'";
    		TParm result= new TParm(TJDODBTool.getInstance().select(sql));
    		if (result.getCount()<=0) {
				this.messageBox("此病患不存在新生儿数据");
				((TCheckBox)this.getComponent("NEW_BABY_FLG")).setSelected(false);
				return;
			}else{
				boolean mrFlg=false;
				for (int i = 0; i < result.getCount(); i++) {
					if (!result.getValue("MR_NO",i).equals(mrNoOut)) {
						mrFlg=true;
						break;
					}
				}
				if(!mrFlg){
					this.messageBox("此病患不存在新生儿数据");
					((TCheckBox)this.getComponent("NEW_BABY_FLG")).setSelected(false);
					return ;
				}
			}
    		boolean newFlg=false;//判断是否存在新生儿数据
    		StringBuffer mrNoDsDate=new StringBuffer();//统计已经出院的数据
    		TParm bilRecpParm=null;
    		boolean newDataFlg=false;//判断是否存在可以缴费的数据
    		TParm parm = new TParm();
    		TParm billPayTableParm =this.getTTable("BillPayTable").getParmValue();
    		TParm billTableParm=this.getTTable("BillTable").getParmValue();
    		for (int i = 0; i < result.getCount(); i++) {
				if (result.getValue("MR_NO",i).equals(mrNoOut)) {//与当前操作病患病案号不同
					continue;
				}
				newFlg=true;
				if (null==result.getValue("DS_DATE",i)||result.getValue("DS_DATE",i).length()<=0) {//统计没有办理出院的新生儿信息
					mrNoDsDate.append("病案号:"+result.getValue("MR_NO",i)+" "+result.getValue("PAT_NAME",i)+"\n");
					continue;
				}
				sql = "SELECT CASE_NO FROM IBS_BILLM WHERE CASE_NO='"+result.getValue("CASE_NO",i)+"' AND RECEIPT_NO IS NULL AND AR_AMT >0";
				bilRecpParm=new TParm(TJDODBTool.getInstance().select(sql));//没有生成账单可以操作
				if (bilRecpParm.getCount()>0) {
				}else{
					continue;
				}
				if (!IBSTool.getInstance().checkData(result.getValue("CASE_NO",i))) {//此病患医嘱数据不平不可以执行
					this.messageBox(result.getValue("MR_NO",i)+" "+result.getValue("PAT_NAME",i)+"病患还有未产生账单的医嘱信息");
					continue;
				}
				newDataFlg=true;
			    parm.setData("CASE_NO", result.getValue("CASE_NO",i));
	            parm.setData("MR_NO", result.getValue("MR_NO",i));
	            // 预交金table显示数据
	            TParm bilPayParm = new TParm();
	            bilPayParm = BILPayTool.getInstance().selDataForCharge(parm);
	            for (int j = 0; j < bilPayParm.getCount(); j++) {
	                bilPayParm.addData("BILLPAY_SEL", "N");
	            }
	            // 账单table显示数据
	            TParm chargeParm = new TParm();
	            chargeParm = IBSBillmTool.getInstance().selDataForCharge(parm);
	            for (int j = 0; j < chargeParm.getCount(); j++) {
	                chargeParm.addData("PAY_SEL", "N");
	            }
	            if (bilPayParm.getCount()>0) {
	            	billPayTableParm.addParm(bilPayParm);
				}
	            if (chargeParm.getCount()>0) {
	            	 billTableParm.addParm(chargeParm);
				}
			}
    		if (!newFlg) {
    			this.messageBox("此病患不存在新生儿数据");
				((TCheckBox)this.getComponent("NEW_BABY_FLG")).setSelected(false);
				return;
			}
    		if (mrNoDsDate.length()>0) {
				this.messageBox(mrNoDsDate+"没有办理出院不能操作");
			}
    		if (newDataFlg) {
	            // 给预交金table赋值
	            this.getTTable("BillPayTable").setParmValue(billPayTableParm);
	            // 账单table赋值
	            this.getTTable("BillTable").setParmValue(billTableParm);
	            this.messageBox("添加成功");
			}else{
				this.messageBox("此病患不存在新生儿数据");
				((TCheckBox)this.getComponent("NEW_BABY_FLG")).setSelected(false);
			}
		}else{
			//不选中将数据移除
			initPage(true);//==liling 20140421 add args
		}
    }
    /**
     * 校验新生儿数据
     * @return
     * ===========pangben 2014-4-14
     */
    private void checkOnNewBabyRecp(){
    	//查询是否存在新生儿数据
		String sql = "SELECT A.MR_NO,A.DS_DATE,B.PAT_NAME,A.CASE_NO FROM ADM_INP A,SYS_PATINFO B WHERE A.MR_NO=B.MR_NO AND A.MR_NO LIKE '"
			+mrNoOut+"%' AND IN_DATE IS NOT NULL AND CANCEL_FLG <> 'Y' AND NEW_BORN_FLG='Y'";
		TParm result= new TParm(TJDODBTool.getInstance().select(sql));
//		if (result.getCount()>0) {
//			for (int i = 0; i < result.getCount(); i++) {
//				if (!result.getValue("MR_NO",i).equals(mrNoOut)) {
//					this.messageBox("此病患存在未结算的新生儿数据");
//					return ;
//				}
//			}
//		}
		TParm bilRecpParm=null;
		for (int i = 0; i < result.getCount(); i++) {
			if (result.getValue("MR_NO",i).equals(mrNoOut)) {//与当前操作病患病案号不同
				continue;
			}
			if (null==result.getValue("DS_DATE",i)||result.getValue("DS_DATE",i).length()<=0) {//统计没有办理出院的新生儿信息
				this.messageBox("此病患存在未出院的新生儿数据");
				return;
			}
			sql = "SELECT CASE_NO FROM IBS_BILLM WHERE CASE_NO='"+result.getValue("CASE_NO",i)+"' AND RECEIPT_NO IS NULL AND AR_AMT >0";
			bilRecpParm=new TParm(TJDODBTool.getInstance().select(sql));//没有生成账单可以操作
			if (bilRecpParm.getCount()>0) {
				this.messageBox("此病患存在未结算的新生儿数据");
				((TCheckBox)this.getComponent("NEW_BABY_FLG")).setSelected(true);
				onNewBabyBilIbsRecp();
				return;
			}else{
				continue;
			}
		}
    }
    /**
     * 调用患者保险信息维护页面 add by liling 20140812
     */
    public void onInsureInfo(){
    	TParm parm = new TParm();
    	parm.setData("MR_NO", mrNoOut );
    	parm.setData("EDIT", "N");
    	this.openDialog(
				"%ROOT%\\config\\mem\\MEMInsureInfo.x", parm);
    }
    
    /**
     * 医疗卡操作
     * huangtt 20140318
     */
    public void onEKTcard(){
        //读取医疗卡
        parmEKT = EKTIO.getInstance().TXreadEKT();
        if (null == parmEKT || parmEKT.getErrCode() < 0 ||
            parmEKT.getValue("MR_NO").length() <= 0) {
            this.messageBox(parmEKT.getErrText());
            parmEKT = null;
            return;
        }
        if(!parmEKT.getValue("MR_NO").equals(mrNoOut)){
        	this.messageBox("医疗卡信息与病人信息不符");
            return; 
        }
        setValue("EKT_CURRENT_BALANCE", parmEKT
				.getDouble("CURRENT_BALANCE"));
        double totAmt = getValueDouble("AR_AMT");
		setValue("EKT_AMT", StringTool.round((parmEKT.getDouble("CURRENT_BALANCE") - totAmt), 2));
		double payOther3 = EKTpreDebtTool.getInstance().getPayOther(parmEKT.getValue("MR_NO"), EKTpreDebtTool.PAY_TOHER3);
		double payOther4 = EKTpreDebtTool.getInstance().getPayOther(parmEKT.getValue("MR_NO"), EKTpreDebtTool.PAY_TOHER4);
		setValue("GIFT_CARD", payOther3);
		setValue("GIFT_CARD2", payOther4);
		setValue("NO_PAY_OTHER_ALL", getValueDouble("EKT_CURRENT_BALANCE") - payOther3 - payOther4);
		TRadioButton ektPay = (TRadioButton) getComponent("tRadioButton_2");
		ektPay.setSelected(true);
		this.onGatherChange(1);
        
    }
    
    public void onPayOther3(){	
		double payOther3 = getValueDouble("PAY_OTHER3");
		double payOther4 = 0;
		double payOtherTop3 = getValueDouble("GIFT_CARD");
		double payOtherTop4 = getValueDouble("GIFT_CARD2");
		double arAmt =Math.abs(getValueDouble("PAY_MEDICAL_CARD")) ;
		if(getValueDouble("PAY_OTHER4") == 0){
			payOther4 = arAmt - payOther3;
			setValue("PAY_OTHER4", df.format(payOther4) );
		}
		TParm result = EKTpreDebtTool.getInstance().checkPayOther(payOther3, payOther4, arAmt, payOtherTop3, payOtherTop4);
		if(result.getErrCode() == -3){
			setValue("PAY_OTHER4", df.format(payOtherTop4) );
		}
		onPayOther();
	}

	public void onPayOther4(){		
		double payOther3 = 0;
		double payOther4 = getValueDouble("PAY_OTHER4");
		double payOtherTop3 = getValueDouble("GIFT_CARD");
		double payOtherTop4 = getValueDouble("GIFT_CARD2");
		double arAmt = Math.abs(getValueDouble("PAY_MEDICAL_CARD"));
		if(getValueDouble("PAY_OTHER3") == 0){
			payOther3 = arAmt - payOther4;
			setValue("PAY_OTHER3", df.format(payOther3) );
		}
		TParm result = EKTpreDebtTool.getInstance().checkPayOther(payOther3, payOther4, arAmt, payOtherTop3, payOtherTop4);
		if(result.getErrCode() == -2){
			setValue("PAY_OTHER3", df.format(payOtherTop3));
		}
		onPayOther();
	}

	public boolean onPayOther(){
		double payOther3 = getValueDouble("PAY_OTHER3");
		double payOther4 = getValueDouble("PAY_OTHER4");
		double payOtherTop3 = getValueDouble("GIFT_CARD");
		double payOtherTop4 = getValueDouble("GIFT_CARD2");
		double payCashTop = getValueDouble("NO_PAY_OTHER_ALL");
		double arAmt = Math.abs(getValueDouble("PAY_MEDICAL_CARD"));
		double payCash = Double.parseDouble(df.format(arAmt - payOther3 - payOther4));
		TParm result = EKTpreDebtTool.getInstance().checkPayOther(payOther3, payOther4, arAmt, payOtherTop3, payOtherTop4);
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			setValue("PAY_OTHER3", 0);
			setValue("PAY_OTHER4", 0);
			return true;
		}
		if(payCash > payCashTop){
			messageBox("现金不足");
			System.out.println(this.getValue("MR_NO")+"-----payCash===="+payCash+"-----payCashTop==="+payCashTop);
			setValue("PAY_OTHER3", 0);
			setValue("PAY_OTHER4", 0);
			return true;
		}
		setValue("NO_PAY_OTHER", payCash);
		return false;
	}
	
	public void onGatherChange(int t){

		boolean b = false;
		String lockRow = "";
		TTable billPayTable = getTTable("BillPayTable");
		billPayTable.acceptText();
        TParm tableParm = billPayTable.getParmValue();
        for (int i = 0; i < tableParm.getCount("RECEIPT_NO"); i++) {
            if (tableParm.getBoolean("BILLPAY_SEL", i)) {
                this.messageBox("已选择预交金明细，不能变更支付方式 ！！！");
                TRadioButton ektPay = (TRadioButton) getComponent("tRadioButton_2");
            	TRadioButton cashPay = (TRadioButton) getComponent("tRadioButton_0");
                if(t == 0){
                	ektPay.setSelected(true);
                }else{ 	
            		cashPay.setSelected(true);
                }
                
                return;
            }
        }

		switch (t) {
		case 0:
			clearValue("PAY_OTHER4;PAY_OTHER3;NO_PAY_OTHER");
			setValue("BILL_TYPE", "C");
			setValue("PAY_MEDICAL_CARD", 0.00);
			setValue("PAY_CASH", TypeTool.getDouble(getValue("AR_AMT")));
			if(parmEKT != null)
			 setValue("EKT_AMT", StringTool.round((parmEKT.getDouble("CURRENT_BALANCE")), 2));
        	//=====pangben 2014-5-6 表格支付方式赋值
            paymentTool.setAmt(TypeTool.getDouble(this.getValueDouble("AR_AMT")));
            TParm parm =new TParm();
            if (this.getValueDouble("AR_AMT")!=0) {
            	parm.addData("PAY_TYPE","C0");//默认显示现金
            	parm.addData("AMT",this.getValueDouble("AR_AMT"));
            	parm.addData("REMARKS","");
                //paymentTool.table.removeRowAll();
//                paymentTool.table.addRow(parm);
            	paymentTool.table.setParmValue(parm);
                paymentTool.table.addRow();

    		}else{
    			parm.addData("PAY_TYPE","C0");
            	parm.addData("AMT",0.00);
            	parm.addData("REMARKS","");
    			paymentTool.table.setParmValue(parm);
    			//paymentTool.table.addRow();
    		}
			break;
		case 1:
			b = true;
			lockRow = "0";
			setValue("BILL_TYPE", "E");
			paymentTool.onClear();
			setValue("PAY_MEDICAL_CARD", TypeTool.getDouble(getValue("AR_AMT")));
			setValue("PAY_CASH", 0);
			setValue("EKT_AMT", StringTool.round((parmEKT.getDouble("CURRENT_BALANCE") - getValueDouble("AR_AMT")), 2));
			double payOther3 = EKTpreDebtTool.getInstance().getPayOther(parmEKT.getValue("MR_NO"), EKTpreDebtTool.PAY_TOHER3);
			double payOther4 = EKTpreDebtTool.getInstance().getPayOther(parmEKT.getValue("MR_NO"), EKTpreDebtTool.PAY_TOHER4);
			setValue("GIFT_CARD", payOther3);
			setValue("GIFT_CARD2", payOther4);
			setValue("NO_PAY_OTHER_ALL", getValueDouble("EKT_CURRENT_BALANCE") - payOther3 - payOther4);
			break;
		}

		TNumberTextField payOther4 = (TNumberTextField) getComponent("PAY_OTHER4");
		TNumberTextField payOther3 = (TNumberTextField) getComponent("PAY_OTHER3");

		payOther4.setEnabled(b);
		payOther3.setEnabled(b);

		paymentTool.table.setLockRows(lockRow);
	}
}
