package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.adm.ADMResvTool;
import jdo.adm.ADMTool;
import jdo.bil.BILPayTool;
import jdo.bil.BILTool;
import jdo.bil.BilInvoice;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;
import jdo.odo.OpdRxSheetTool;
import jdo.opb.OPBTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 *
 * <p>Title: 预交金控制类</p>
 *
 * <p>Description: 预交金控制类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILPayControl
    extends TControl {
    int selectrow = -1;
    TParm data = new TParm();
    String startInvno = "";
    private TParm parmEKT;//医疗卡集合
    private TParm admParm;//包干套餐使用
    private DecimalFormat df = new DecimalFormat("##########0.00");
    /**
     * 初始化界面
     */
    public void onInit() {
        super.onInit();
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            //住院ADM调用----------start---------------
            admParm = (TParm) obj;
            TParm parm = new TParm();
            TParm UIParm = new TParm();
            String caseNo = "";
            if (admParm != null) {
                if (admParm.getData("IBS", "CASE_NO") != null)
                    caseNo = admParm.getData("IBS", "CASE_NO").toString();
                else
                    caseNo = admParm.getValue("CASE_NO");
                parm.setData("CASE_NO", caseNo);
                this.setValue("PRE_AMT",admParm.getValue("PRE_AMT"));
                //添加界面显示pangben 2016-8-2
                if (null!=admParm.getData("BILL_PAY_FLG")&&admParm.getValue("BILL_PAY_FLG").equals("Y")) {
        			callFunction("UI|close|setEnabled", false);
        		}
            }
            if (caseNo.length() != 0) {
                UIParm = BILPayTool.getInstance().seldataByCaseNo(parm);
                //清空table
                this.callFunction("UI|MAINTABLE|removeRowAll");
                data = new TParm();
                //查询数据
                data = BILPayTool.getInstance().selectAllData(parm);
                //整理数据
                //给table配参
                this.callFunction("UI|MAINTABLE|setParmValue", data);
            }else{//=====pangben 2014-7-31 添加包干套餐显示数据
            	if (null!=admParm.getValue("LUMPWORK_FLG")
            			&&admParm.getValue("LUMPWORK_FLG").equals("Y")) {//套餐操作
            		UIParm =ADMResvTool.getInstance().selectallByPatInfo(admParm);
				}else{
					if (admParm != null) {
						this.messageBox("出现问题不可以执行充值操作");
						callFunction("UI|print|setEnabled", false);
						callFunction("UI|save|setEnabled", false);
						callFunction("UI|returnFee|setEnabled", false);
						return;
					}
				}
            }
            //界面控件赋值
            initPage(UIParm);
            //住院ADM调用----------end---------------
        }
        //table1值改变事件
        this.addEventListener("MAINTABLE->" + TTableEvent.CHANGE_VALUE,
                              "onTableChangeValue");
        //table1的侦听事件
        callFunction("UI|MAINTABLE|addEventListener",
                     "MAINTABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //初始化数据
        initiation();
    }
    /**
     * 处理当前TOOLBAR
     */
    public void onShowWindowsFunction() {
        //显示UIshowTopMenu
        callFunction("UI|showTopMenu");
    }

    /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row) {
        //接收所有事件
        this.callFunction("UI|MAINTABLE|acceptText");
        TParm data = (TParm) callFunction("UI|MAINTABLE|getParmValue");
        setValueForParm(
            "PRE_AMT;RECEIPT_NO;TRANSACT_TYPE;PAY_TYPE;CHECK_NO;REMARK;CARD_TYPE",
            data, row); //数据上翻
        if("WX".equals(data.getData("PAY_TYPE",row))||"ZFB".equals(data.getData("PAY_TYPE",row))){
        	callFunction("UI|PAY_TYPE|setEnabled", false);
        }else{
        	callFunction("UI|PAY_TYPE|setEnabled", true);
        }
        //是否可打印
        if (data.getData("REFUND_FLG", row).equals("Y"))
            callFunction("UI|print|setEnabled", false);
        else
            callFunction("UI|print|setEnabled", true);
        //是否可退费
        if (data.getData("REFUND_FLG", row).equals("Y") ||
            data.getData("TRANSACT_TYPE", row).equals("02"))
            callFunction("UI|returnFee|setEnabled", false);
        else
            callFunction("UI|returnFee|setEnabled", true);
        selectrow = row;
        callFunction("UI|delete|setEnabled", true);
    }

    /**
     * 根据ID号查询(实际查当前ID号的最大就诊序号)
     */
    public void onQueryByCaseNo() {
        TParm parm = new TParm();
        String mrNo = PatTool.getInstance().checkMrno(getValue("MR_NO").
            toString());
        setValue("MR_NO", mrNo);
        parm.setData("MR_NO", mrNo);
        String caseNo = (BILPayTool.getInstance().selectPatCaseNo(parm)).
            getValue("CASE_NO", 0);
        setValue("CASE_NO", caseNo);
        if (caseNo == null) {
            this.messageBox_("无此就诊号");
            return;
        }
        parm.setData("CASE_NO", caseNo);
        TParm UIParm = new TParm();
        UIParm = BILPayTool.getInstance().seldataByCaseNo(parm);
//        stationCode = UIParm.getValue("STATION_CODE", 0);
        //界面控件赋值
        initPage(UIParm);
        //清空table
        this.callFunction("UI|MAINTABLE|removeRowAll");
        data = new TParm();
        //查询数据
        data = BILPayTool.getInstance().selectAllData(parm);
        //整理数据
        //给table配参
        this.callFunction("UI|MAINTABLE|setParmValue", data);
    }

    /**
     * 根据住院号查询数据
     */
    public void onQueryByIpdNo() {
        TParm parm = new TParm();
        String ipdNo = PatTool.getInstance().checkMrno(getValue("IPD_NO").
            toString());
        setValue("IPD_NO", ipdNo);
        if (ipdNo == null) {
            this.messageBox_("无此住院号");
            return;
        }
        //清空table
        this.callFunction("UI|MAINTABLE|removeRowAll");
        parm.setData("IPD_NO", ipdNo);
        TParm UIParm = new TParm();
        UIParm = BILPayTool.getInstance().seldataByIpdNo(parm);
//        stationCode = UIParm.getValue("STATION_CODE", 0);
        //界面控件赋值
        initPage(UIParm);
        data = new TParm();
        //查询数据
        data = BILPayTool.getInstance().selectAllData(parm);
        //整理数据
        //给table配参
        this.callFunction("UI|MAINTABLE|setParmValue", data);
    }

    /**
     * 保存事件(交预交金)
     */
    public void onSave() {
    	//===zhangp 20120731 start
//        if (!checkNo()) {
    	BilInvoice bilInvoice = new BilInvoice();
       	if (!initBilInvoice(bilInvoice.initBilInvoice("PAY"))) {
//            this.messageBox("尚未开帐,请先开帐");
            return;
        }
       	//===zhangp 20120731 end
        if (this.getValueString("MR_NO") == null ||
            this.getValueString("MR_NO").length() == 0) {
            this.messageBox("请输入病案号");
            return;
        }
        if (getValue("PRE_AMT") == null ||
            TypeTool.getDouble(getValue("PRE_AMT")) <= 0) {
            this.messageBox("输入金额有误!");
            return;
        }
        //不能输入空值
        if (!this.emptyTextCheck("PAY_TYPE"))
            return;
        String payType = "";
        payType = this.getValueString("PAY_TYPE");
        String cardType = "";
        String remark = "";
//        System.out.println("payType = " + payType);
        
        if("C1".equals(payType)){
        	
        	TTextFormat textFormat = (TTextFormat) getComponent("CARD_TYPE");
        	cardType = textFormat.getText();
//        	System.out.println("cardType = " + cardType);
        	if(null == cardType || "".equals(cardType)){
        		
        		messageBox("支付方式为刷卡，请输入卡类型");
        		return ;
        	}
        	remark = this.getValueString("REMARK");
//        	System.out.println("remark =" + remark);
        	if(null == remark || "".equals(remark)){
        		messageBox("支付方式为刷卡，请在备注栏填写卡号");
        		return ;
        	}
        }
        TParm payCashParm=null;
        if("WX".equals(payType)||"ZFB".equals(payType)){
        	//现金打票操作，校验是否存在支付宝或微信金额
    		TParm checkCashTypeParm=new TParm();
    		if("WX".equals(payType)){
    			checkCashTypeParm.setData("WX_AMT", this.getValueDouble("PRE_AMT"));
				checkCashTypeParm.setData("WX_FLG", "Y");
			}
			if("ZFB".equals(payType)){
				checkCashTypeParm.setData("ZFB_AMT",this.getValueDouble("PRE_AMT"));
				checkCashTypeParm.setData("ZFB_FLG", "Y");
			}
    		Object result = this.openDialog(
    	            "%ROOT%\\config\\bil\\BILPayTypeOnFeeTransactionNo.x", checkCashTypeParm, false);
			if(null==result){
				return ;
			}
			payCashParm=(TParm)result;
        }
        //add by huangtt 20180520 
        TParm ektParm = null;
        if("EKT".equals(payType)){

    		if(this.onPayOther()){//未收费
				return ;
			}
    		
    		if(parmEKT == null){
    			this.messageBox("请读取医疗卡信息");
    			return ;
    		}
    		if (!EKTIO.getInstance().ektSwitch()) {
    			messageBox_("医疗卡流程没有启动!");
    			return ;
    		}
    		this.selBilPay();
    		TParm parm = new TParm();
    		parm.setData("READ_CARD", parmEKT.getData());
    		parm.setData("PAY_OTHER3",this.getValueDouble("PAY_OTHER3"));
    		parm.setData("PAY_OTHER4",this.getValueDouble("PAY_OTHER4"));
    		parm.setData("EXE_AMT",this.getValueDouble("PRE_AMT"));
    		parm.setData("MR_NO",this.getValueString("MR_NO"));
    		parm.setData("BUSINESS_TYPE","PAY");
    		parm.setData("CASE_NO",this.getValueString("CASE_NO"));//交易号
    		Object r =  this.openDialog("%ROOT%\\config\\ekt\\EKTChageOtherUI.x",parm);
    		if(r == null){
    			this.messageBox("医疗卡扣款取消，不执行保存");
    			return;
    		}
    		TParm rParm = (TParm) r;
    		if(rParm.getErrCode() < 0){
    			this.messageBox(rParm.getErrText());
    			return;
    		}else if(rParm.getValue("OP_TYPE").equals("2")){
    			return;
    		}else{
    			ektParm = rParm;
    		}
    		
    	
        }
        
        TParm parm = getParmForTag(
            "ADM_TYPE;YELLOW_SIGN:int;RED_SIGN:int;GREENPATH_VALUE:int;MR_NO;" +
            "IPD_NO;CASE_NO;BED_NO;PAT_NAME;SEX_CODE;DEPT_CODE;STATION_CODE;PRE_AMT:double;" +
            "PAY_TYPE;CHECK_NO;REMARK");
        parm.setData("ADM_TYPE", "I");
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("REFUND_FLG", "N");
        parm.setData("TRANSACT_TYPE", "01");
        parm.setData("START_INVNO", startInvno);
        parm.setData("INV_NO", getValue("PRINT_NO"));
//        parm.setData("INV_NO", bilInvoice.getUpdateNo());
        parm.setData("CHARGE_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("ADM_RESV_PARM",null!=admParm?admParm.getData():null);//=====pangben 2014-7-31 包干套餐使用,创建ADM_INP表,实现充值功能
        parm.setData("CARD_TYPE", getValue("CARD_TYPE") == null ? new TNull(String.class):getValue("CARD_TYPE"));//add by sunqy 20140801 卡类型
        if(null!=payCashParm){//===pangben 2016-8-10 微信支付宝添加交易号码
	    	  if(null!=payCashParm.getValue("WX")&&payCashParm.getValue("WX").length()>0){
	    		  parm.setData("BUSINESS_NO",payCashParm.getValue("WX"));
			  }
			  if(null!=payCashParm.getValue("ZFB")&&payCashParm.getValue("ZFB").length()>0){
				 parm.setData("BUSINESS_NO",payCashParm.getValue("ZFB"));
			  }
        }
        if(null != ektParm){
        	 parm.setData("BUSINESS_NO",ektParm.getValue("TRADE_NO"));
        	 parm.setData("ektSql", ektParm.getData("ektSql"));
        }
        TParm result = TIOM_AppServer.executeAction("action.bil.BILPayAction",
            "onBillPay", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return;
        }
        parm.setData("RECEIPT_NO", result.getValue("RECEIPT_NO", 0));
        setValue("RECEIPT_NO", result.getValue("RECEIPT_NO", 0));
        //校验是否停止划价
        TParm checkStopFee = ADMTool.getInstance().checkStopFee(this.
            getValueString("CASE_NO"));
        if (checkStopFee.getErrCode() < 0) {
            err(checkStopFee.getErrCode() + " " + checkStopFee.getErrText());
            return;
        }

        //table上加入新增的数据显示
        callFunction("UI|MAINTABLE|addRow", parm,
                     "RECEIPT_NO;CHARGE_DATE;PRE_AMT;TRANSACT_TYPE;PAY_TYPE;CHECK_NO;REMARK;CASHIER_CODE;" +
                     "REFUND_FLG;REFUND_CODE;REFUND_DATE;RESET_RECP_NO");
        //打印预交金收据
        TParm forPrtParm = new TParm();
        forPrtParm.setData("TITLE", "TEXT", "住院预交金收据");
        String bilPayC = StringUtil.getInstance().numberToWord(TypeTool.
            getDouble(getValue("PRE_AMT")));
        Timestamp printDate = (SystemTool.getInstance().getDate());
        String pDate = StringTool.getString(printDate, "yyyy/MM/dd HH:mm:ss");
        forPrtParm.setData("COPY", "TEXT", "");
        forPrtParm.setData("REGION_CHN_DESC", "TEXT",
                           Operator.getHospitalCHNFullName());
        forPrtParm.setData("CHECK_NO","TEXT",this.getValue("CHECK_NO")) ;
        if("C1".equals(this.getValue("PAY_TYPE"))
        		||"WX".equals(this.getValue("PAY_TYPE"))||
        		"ZFB".equals(this.getValue("PAY_TYPE"))){//add by sunqy 20140715支付方式为刷卡时 存入备注处信息(银行卡号)
        	forPrtParm.setData("REMARK","TEXT",this.getValue("REMARK")) ;
        	
        }else{
        	forPrtParm.setData("REMARK","TEXT","") ;
        }
        if("WX".equals(this.getValue("PAY_TYPE"))||"ZFB".equals(this.getValue("PAY_TYPE"))){
        	String sql="SELECT BUSINESS_NO FROM BIL_PAY WHERE RECEIPT_NO='"+this.getValue("RECEIPT_NO")+"'";
        	TParm businessNoParm=new TParm(TJDODBTool.getInstance().select(sql));
        	if(this.getValue("REMARK").toString().length()>0){
        	forPrtParm.setData("REMARK","TEXT"," 备注:"+this.getValue("REMARK")+" 交易号:"+businessNoParm.getValue("BUSINESS_NO",0)) ;
        	}else{
            	forPrtParm.setData("REMARK","TEXT","交易号:"+businessNoParm.getValue("BUSINESS_NO",0)) ;
        	}
        	}else{
        	forPrtParm.setData("REMARK","TEXT",this.getValue("REMARK")) ;
        }
      
        forPrtParm.setData("REGION_ENG_DESC", "TEXT",
                           Operator.getHospitalENGFullName());
//        forPrtParm.setData("Data", "TEXT", "日期:" + pDate);
        forPrtParm.setData("Data", "TEXT",  pDate);
        //forPrtParm.setData("Name", "TEXT", "姓名:" + getValue("PAT_NAME"));
        forPrtParm.setData("Name", "TEXT", getValue("PAT_NAME"));
        //forPrtParm.setData("TOLL_COLLECTOR", "TEXT", "收费员:" + Operator.getName());
        forPrtParm.setData("TOLL_COLLECTOR", "TEXT", Operator.getID());
        //forPrtParm.setData("MR_N0", "TEXT", "病案号:" + this.getValue("MR_NO"));
        forPrtParm.setData("MR_N0", "TEXT", this.getValue("MR_NO"));
        forPrtParm.setData("IPD_NO", "TEXT", "住院号:" + this.getValue("IPD_NO"));
        String sexCode = this.getValueString("SEX_CODE");
        if (sexCode.equals("1"))
            forPrtParm.setData("SEX", "TEXT", "性别:" + "男");
        else if (sexCode.equals("2"))
            forPrtParm.setData("SEX", "TEXT", "性别:" + "女");
        else
            forPrtParm.setData("SEX", "TEXT", "性别:" + "不详");
//        forPrtParm.setData("DEPT", "TEXT",
//                           "就诊科室:" + getDeptDesc( (String) getValue("DEPT_CODE")));
      forPrtParm.setData("DEPT", "TEXT",getDeptDesc( (String) getValue("DEPT_CODE")));

//        forPrtParm.setData("STATION", "TEXT",
//                           "就诊病区:" + getStationDesc(this.getValueString("STATION_CODE")));
        forPrtParm.setData("Capital", "TEXT",  bilPayC);
        DecimalFormat formatObject = new DecimalFormat("###########0.00");

        forPrtParm.setData("SmallCaps", "TEXT",formatObject.format(
                           TypeTool.getDouble(this.getValueString("PRE_AMT")))+"元");
        forPrtParm.setData("SEQ_NO", "TEXT",
                           "流水号:" + this.getValueString("RECEIPT_NO"));
        forPrtParm.setData("PRINT_DATE", "TEXT", "打印日期:" + pDate);
        //TComboBox payType=(TComboBox)this.getComponent("PAY_TYPE");
       
        payType = "";
        payType += this.getText("PAY_TYPE") + "：" + formatObject.format(TypeTool.getDouble(this.getValueString("PRE_AMT")))+"元";
        TTextFormat textFormat = (TTextFormat) getComponent("CARD_TYPE");
    	cardType = textFormat.getText();
        forPrtParm.setData("WAY","TEXT",payType + "; " + cardType) ;//====pangben 2014-4-15 支付方式显示正确
        forPrtParm.setData("RECEIPT_NO","TEXT",this.getText("RECEIPT_NO")) ;//====pangben 2014-4-15 支付方式显示正确
        forPrtParm.setData("HOSP_NAME", "TEXT",
                OpdRxSheetTool.getInstance().getHospFullName());
       //payType.getName();
//        if (this.getValueString("PAY_TYPE").equals("PAY_CASH")) {
////            forPrtParm.setData("CASH", "TEXT", "√");
////            forPrtParm.setData("BANK", "TEXT", "");
////            forPrtParm.setData("OTHERS", "TEXT", "");
//        	forPrtParm.setData("WAY","TEXT","现金") ;
//        }
//        else if (this.getValueString("PAY_TYPE").equals("PAY_BANK_CARD")) {
////            forPrtParm.setData("CASH", "TEXT", "");
////            forPrtParm.setData("BANK", "TEXT", "√");
////            forPrtParm.setData("OTHERS", "TEXT", "");
//                forPrtParm.setData("WAY","TEXT","银行卡") ;
//        }
//        else if (this.getValueString("PAY_TYPE").equals("PAY_CHECK")) {
////            forPrtParm.setData("CASH", "TEXT", "");
////            forPrtParm.setData("BANK", "TEXT", "√");
////            forPrtParm.setData("OTHERS", "TEXT", "");
//                forPrtParm.setData("WAY","TEXT","支票") ;
//        }
//        else {
////            forPrtParm.setData("CASH", "TEXT", "");
////            forPrtParm.setData("BANK", "TEXT", "");
////            forPrtParm.setData("OTHERS", "TEXT", "√");
//        	forPrtParm.setData("WAY","TEXT","其他") ;
//        }
//        for(int i=0;i<2;i++){
        this.openPrintDialog(IReportTool.getInstance().getReportPath("BILPrepayment_V45.jhw"),
				IReportTool.getInstance().getReportParm("BILPrepayment_V45.class", forPrtParm));//合并报表
//        }
        this.onClear();
        if (admParm.getValue("CASE_NO").length()<=0) {
        	onQueryByCaseNo();
		}
    }
   
    /**
     * 补印
     */
    public void onPrint() {

        TTable table = (TTable)this.getComponent("MAINTABLE");
        if (table.getSelectedRow() < 0) {
            this.messageBox("请点选table数据");
            return;
        }
        TParm onREGReprintParm = new TParm();
        onREGReprintParm.setData("CASE_NO", this.getValueString("CASE_NO"));
        onREGReprintParm.setData("RECEIPT_NO", this.getValueString("RECEIPT_NO"));
        onREGReprintParm.setData("OPT_USER", Operator.getID());
        onREGReprintParm.setData("OPT_TERM", Operator.getIP());
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction("action.bil.BILPayAction",
                                              "onBilPayReprint",
                                              onREGReprintParm);
        if (result.getErrCode() < 0) {
            err(result.getErrName());
            return;
        }
        //RECEIPT_NO;CHARGE_DATE;PRE_AMT;TRANSACT_TYPE;PAY_TYPE;CARD_TYPE;CHECK_NO;REMARK;CASHIER_CODE;REFUND_FLG;REFUND_CODE;REFUND_DATE;RESET_RECP_NO
        //打印预交金收据
       
        TParm forPrtParm = new TParm();
        String bilPayC = StringUtil.getInstance().numberToWord(Math.abs(TypeTool.
            getDouble(getValue("PRE_AMT"))));
        Timestamp printDate = (SystemTool.getInstance().getDate());
        String pDate = StringTool.getString(printDate, "yyyy/MM/dd HH:mm:ss");
        forPrtParm.setData("COPY", "TEXT", "(copy)");
        forPrtParm.setData("HOSP_NAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalCHNFullName() : "所有医院");
        forPrtParm.setData("HOSP_ENAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalENGFullName() : "ALL HOSPITALS");
        forPrtParm.setData("COPY", "TEXT", "(copy)");
        forPrtParm.setData("REGION_CHN_DESC", "TEXT",
                           Operator.getHospitalCHNFullName());
        forPrtParm.setData("REGION_ENG_DESC", "TEXT",
                           Operator.getHospitalENGFullName());
        int row = (Integer) callFunction("UI|MAINTABLE|getSelectedRow");
        if (row < 0)
            return;
        TParm parm = (TParm) callFunction("UI|MAINTABLE|getParmValue");
        forPrtParm.setData("Data", "TEXT",StringTool.getString(parm.
                                                getTimestamp("CHARGE_DATE", row),
                                                "yyyy/MM/dd HH:mm:ss"));
        forPrtParm.setData("CHECK_NO","TEXT",this.getValue("CHECK_NO")) ;
        if("WX".equals(this.getValue("PAY_TYPE"))||"ZFB".equals(this.getValue("PAY_TYPE"))){
        	String sql="SELECT BUSINESS_NO FROM BIL_PAY WHERE RECEIPT_NO='"+this.getValue("RECEIPT_NO")+"'";
        	TParm businessNoParm=new TParm(TJDODBTool.getInstance().select(sql));
        	if(this.getValue("REMARK").toString().length()>0){
            	forPrtParm.setData("REMARK","TEXT","备注:"+this.getValue("REMARK")+" 交易号:"+businessNoParm.getValue("BUSINESS_NO",0)) ;
        	}else{
            	forPrtParm.setData("REMARK","TEXT","交易号:"+businessNoParm.getValue("BUSINESS_NO",0)) ;
        	}
        }else{
        	forPrtParm.setData("REMARK","TEXT",this.getValue("REMARK")) ;
        }
       // forPrtParm.setData("REMARK","TEXT",this.getValue("REMARK")) ;
        forPrtParm.setData("Name", "TEXT", getValue("PAT_NAME"));
        forPrtParm.setData("TOLL_COLLECTOR", "TEXT", Operator.getID());
        forPrtParm.setData("MR_N0", "TEXT", this.getValue("MR_NO"));
        forPrtParm.setData("IPD_NO", "TEXT", "住院号:" + this.getValue("IPD_NO"));
        String sexCode = this.getValueString("SEX_CODE");
        if (sexCode.equals("1"))
            forPrtParm.setData("SEX", "TEXT", "性别:" + "男");
        else if (sexCode.equals("2"))
            forPrtParm.setData("SEX", "TEXT", "性别:" + "女");
        else
            forPrtParm.setData("SEX", "TEXT", "性别:" + "不详");
        forPrtParm.setData("DEPT", "TEXT", getDeptDesc( (String) getValue("DEPT_CODE")));
//        forPrtParm.setData("STATION", "TEXT",
//                           "就诊病区:" + getStationDesc(this.getValueString("STATION_CODE")));
        forPrtParm.setData("Capital", "TEXT", bilPayC);
        DecimalFormat formatObject = new DecimalFormat("###########0.00");

        forPrtParm.setData("SmallCaps", "TEXT",
        		formatObject.format(Math.abs(this.getValueDouble("PRE_AMT")))+"元");
        forPrtParm.setData("SEQ_NO", "TEXT",
                           "流水号:" + this.getValueString("RECEIPT_NO"));
        forPrtParm.setData("PRINT_DATE", "TEXT", "打印日期:" + pDate);
        //TComboBox payType=(TComboBox)this.getComponent("PAY_TYPE");
        String payType="";
        TTextFormat textFormat = (TTextFormat) getComponent("CARD_TYPE");
    	String cardType = textFormat.getText();
    	//System.out.println("cardType = " + cardType);
        payType += this.getText("PAY_TYPE") + "：" + formatObject.format(Math.abs(this.getValueDouble("PRE_AMT")))+"元";
        forPrtParm.setData("WAY","TEXT",payType +"; " + cardType) ;//====pangben 2014-4-15 支付方式显示正确
        forPrtParm.setData("RECEIPT_NO","TEXT",this.getText("RECEIPT_NO")) ;//====caoy 2014-6-13 支付方式显示正确
        //--------------add caoy 如果是退费操作要中报表上体现出来2014/6/13------start
        String  transactType=table.getParmValue().getRow(table.getSelectedRow()).getValue("TRANSACT_TYPE");
        if("02".equals(transactType)){
        	 forPrtParm.setData("RETURN1","TEXT","退") ;
        	 forPrtParm.setData("RETURN2","TEXT","o") ;
        }
        forPrtParm.setData("HOSP_NAME", "TEXT",
                OpdRxSheetTool.getInstance().getHospFullName());
      //--------------add caoy 如果是退费操作要中报表上体现出来2014/6/13------end
//        if (this.getValueString("PAY_TYPE").equals("PAY_CASH")) {
////            forPrtParm.setData("CASH", "TEXT", "√");
////            forPrtParm.setData("BANK", "TEXT", "");
////            forPrtParm.setData("OTHERS", "TEXT", "");
//        	forPrtParm.setData("WAY","TEXT","现金") ;
//        }
//        else if (this.getValueString("PAY_TYPE").equals("PAY_BANK_CARD")) {
////            forPrtParm.setData("CASH", "TEXT", "");
////            forPrtParm.setData("BANK", "TEXT", "√");
////            forPrtParm.setData("OTHERS", "TEXT", "");
//            forPrtParm.setData("WAY","TEXT","银行卡") ;
//        }
//        else if (this.getValueString("PAY_TYPE").equals("PAY_CHECK")) {
////            forPrtParm.setData("CASH", "TEXT", "");
////            forPrtParm.setData("BANK", "TEXT", "√");
////            forPrtParm.setData("OTHERS", "TEXT", "");
//                forPrtParm.setData("WAY","TEXT","支票") ;
//        }
//        else {
////            forPrtParm.setData("CASH", "TEXT", "");
////            forPrtParm.setData("BANK", "TEXT", "");
////            forPrtParm.setData("OTHERS", "TEXT", "√");
//        	forPrtParm.setData("WAY","TEXT","其他") ;
//        }
//        for(int i=0;i<2;i++){
        if(parm.getData("TRANSACT_TYPE",row).equals("01")){
        	forPrtParm.setData("TITLE", "TEXT", "住院预交金收据");
        }
        if(parm.getData("TRANSACT_TYPE",row).equals("02")){
        	forPrtParm.setData("TITLE", "TEXT", "住院预交金退费收据");
        }
        this.openPrintDialog(IReportTool.getInstance().getReportPath("BILPrepayment_V45.jhw"),
				IReportTool.getInstance().getReportParm("BILPrepayment_V45.class", forPrtParm));//合并报表
//        }
        this.onClear();
    }

    /**
     * 退费(退预交金)
     */
    public void onReturnFee() {
    	if("C1".equals(this.getValue("PAY_TYPE"))){
    		TTextFormat textFormat = (TTextFormat) getComponent("CARD_TYPE");
        	String cardType = textFormat.getText();
    		if(cardType.length()<=0||"".equals(cardType)){
    			this.messageBox("支付方式为刷卡,请选择卡类型");
    			return;
    		}
    		if(this.getValue("REMARK").toString().length()<=0){
    			this.messageBox("支付方式为刷卡,需要在备注栏填写卡号");
    			return;
    		}
    	}
    	
        int row = (Integer) callFunction("UI|MAINTABLE|getSelectedRow");
        if (row < 0)
            return;
        TParm parm = (TParm) callFunction("UI|MAINTABLE|getParmValue");
        String receiptNo = parm.getValue("RECEIPT_NO", row);
        TParm actionParm = new TParm();
        actionParm = BILPayTool.getInstance().selAllDataByRecpNo(receiptNo);
        TParm endParm = new TParm();
        endParm = actionParm.getRow(0);
        if("WX".equals(this.getValue("PAY_TYPE"))||"ZFB".equals(this.getValue("PAY_TYPE"))){
    		//现金打票操作，校验是否存在支付宝或微信金额
    		TParm checkCashTypeParm=new TParm();
    		if("WX".equals(this.getValue("PAY_TYPE"))){
    			checkCashTypeParm.setData("WX_AMT", endParm.getDouble("PRE_AMT"));
				checkCashTypeParm.setData("WX_FLG", "Y");
			}
			if("ZFB".equals(this.getValue("PAY_TYPE"))){
				checkCashTypeParm.setData("ZFB_AMT",endParm.getDouble("PRE_AMT"));
				checkCashTypeParm.setData("ZFB_FLG", "Y");
			}
    		Object result = this.openDialog(
    	            "%ROOT%\\config\\bil\\BILPayTypeTransactionNo.x", checkCashTypeParm, false);
			if(null==result){
				return ;
			}
			TParm payCashParm=(TParm) result;
			if(null!=payCashParm.getValue("WX")&&payCashParm.getValue("WX").length()>0){
			  endParm.setData("BUSINESS_NO",payCashParm.getValue("WX"));
			}
			if(null!=payCashParm.getValue("ZFB")&&payCashParm.getValue("ZFB").length()>0){
			  endParm.setData("BUSINESS_NO",payCashParm.getValue("ZFB"));
			}
    	}
        
      //add by huangtt 20180520 
        if("EKT".equals(this.getValue("PAY_TYPE"))){
    		if(parmEKT == null){
    			this.messageBox("请读取医疗卡信息");
    			return ;
    		}
    		if (!EKTIO.getInstance().ektSwitch()) {
    			messageBox_("医疗卡流程没有启动!");
    			return ;
    		}
    		this.selBilPay();
    		TParm parmE = new TParm();
    		parmE.setData("READ_CARD", parmEKT.getData());
    		parmE.setData("PAY_OTHER3",this.payOther(endParm.getValue("BUSINESS_NO"), "PAY_OTHER3"));
    		parmE.setData("PAY_OTHER4",this.payOther(endParm.getValue("BUSINESS_NO"), "PAY_OTHER4"));
    		parmE.setData("EXE_AMT",-this.getValueDouble("PRE_AMT"));
    		parmE.setData("MR_NO",this.getValueString("MR_NO"));
    		parmE.setData("BUSINESS_TYPE","PAY");
    		parmE.setData("CASE_NO",this.getValueString("CASE_NO"));//交易号
    		parmE.setData("RESET_TRADE_NO", endParm.getValue("BUSINESS_NO"));
    		Object r =  this.openDialog("%ROOT%\\config\\ekt\\EKTChageOtherUI.x",parmE);
    		if(r == null){
    			this.messageBox("医疗卡扣款取消，不执行保存");
    			return;
    		}
    		TParm rParm = (TParm) r;
    		if(rParm.getErrCode() < 0){
    			this.messageBox(rParm.getErrText());
    			return;
    		}else if(rParm.getValue("OP_TYPE").equals("2")){
    			return;
    		}else{
    			 endParm.setData("BUSINESS_NO",rParm.getValue("TRADE_NO"));
    			 endParm.setData("ektSql", rParm.getData("ektSql"));
    		}
    		
    	
        }
        
        endParm.setData("RESET_BIL_PAY_NO", parm.getData("RECEIPT_NO", row));
        endParm.setData("REFUND_CODE", Operator.getID());
        //==============chenxi   2012.05.29
        endParm.setData("CASHIER_CODE", Operator.getID());
        endParm.setData("CHARGE_DATE", SystemTool.getInstance().getDate());
        //================chenxi    2012.05.29
        endParm.setData("REFUND_DATE", SystemTool.getInstance().getDate());
        endParm.setData("OPT_USER", Operator.getID());
        endParm.setData("OPT_TERM", Operator.getIP());
        endParm.setData("REMARK", this.getValue("REMARK"));
     
        if (null==endParm.getValue("IPD_NO")||endParm.getValue("IPD_NO").equals("null")
        		||endParm.getValue("IPD_NO").length()<=0) {
        	endParm.setData("IPD_NO", new TNull(String.class));
		}
        endParm.setData("CARD_TYPE", parm.getData("CARD_TYPE", row));//add by sunqy 预交金退费卡类型  20140804
        TParm endDate = TIOM_AppServer.executeAction("action.bil.BILPayAction",
            "onReturnBillPay", endParm);
        if (endDate.getErrCode() < 0) {
            err(endDate.getErrCode() + " " + endDate.getErrText());
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0005");
        //查询数据
        TParm allParm = new TParm();
        TParm allForParm = new TParm();
        allForParm.setData("IPD_NO", getValue("IPD_NO"));
        allForParm.setData("CASE_NO", getValue("CASE_NO"));//==liling 20140418 修改
        allParm = BILPayTool.getInstance().selectAllData(allForParm);
        //清空table
      //  this.callFunction("UI|MAINTABLE|removeRowAll");
        
        String sqlResetBilPayNo =
            " SELECT  RESET_BIL_PAY_NO   FROM  BIL_PAY   WHERE "
            + "RECEIPT_NO = '" + receiptNo + "' ";
//        System.out.println("selStation>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+sqlResetBilPayNo);
        //查询病区名称
        TParm selReceiptParm = new TParm(TJDODBTool.getInstance().select(
        		sqlResetBilPayNo));
       String resetReceiptNo = selReceiptParm.getValue("RESET_BIL_PAY_NO", 0);
//       System.out.println("sSSSSSS<<<<<<<<<<<<<<<<<<<<<<"+resetReceiptNo);
        //打印出预交金退费收据 add by sunqy 20140619--start--
        DecimalFormat df = new DecimalFormat("#0.00");
        TParm forPrtParm = new TParm();
        forPrtParm.setData("TITLE", "TEXT", "住院预交金退费收据");
        forPrtParm.setData("RECEIPT_NO", "TEXT", resetReceiptNo);
        forPrtParm.setData("Name", "TEXT", this.getValue("PAT_NAME"));
        forPrtParm.setData("MR_N0", "TEXT", this.getValue("MR_NO"));
        forPrtParm.setData("DEPT", "TEXT", getDeptDesc((String) getValue("DEPT_CODE")));
        forPrtParm.setData("SmallCaps", "TEXT", df.format(TypeTool.getDouble(parm.getData("PRE_AMT", row)))+"元");
        forPrtParm.setData("Capital", "TEXT", StringUtil.getInstance().numberToWord(TypeTool.getDouble(parm.getData("PRE_AMT", row))));
        String payType="";
        payType += this.getText("PAY_TYPE") + "：" + df.format(TypeTool.getDouble(parm.getData("PRE_AMT", row)))+"元";
        forPrtParm.setData("WAY","TEXT",payType);
        forPrtParm.setData("Data", "TEXT",StringTool.getString(parm.getTimestamp("CHARGE_DATE", row), "yyyy/MM/dd HH:mm:ss"));
        forPrtParm.setData("TOLL_COLLECTOR", "TEXT", Operator.getID());
        forPrtParm.setData("HOSP_NAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalCHNFullName() : "所有医院");
        forPrtParm.setData("HOSP_ENAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalENGFullName() : "ALL HOSPITALS");
//        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILPrepayment_V45.jhw",forPrtParm,true);
        this.openPrintDialog(IReportTool.getInstance().getReportPath("BILPrepayment_V45.jhw"),
				IReportTool.getInstance().getReportParm("BILPrepayment_V45.class", forPrtParm));//合并报表
        //打印出预交金退费收据 add by sunqy 20140619--end--
        
        this.onClear();
        //整理数据
        //给table配参
        this.callFunction("UI|MAINTABLE|setParmValue", allParm);
    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue("ADM_TYPE;YELLOW_SIGN;RED_SIGN;GREENPATH_VALUE;MR_NO;IPD_NO;CASE_NO;BED_NO;PAT_NAME;SEX_CODE;" +
                   "DEPT_CODE;STATION_CODE;PRE_AMT;LEFT_BILPAY;SUM_TOTAL;RECEIPT_NO;CHECK_NO;REMARK;TRANSACT_TYPE;CARD_TYPE");
        initiation();
        this.callFunction("UI|MAINTABLE|removeRowAll");
        callFunction("UI|PAY_TYPE|setEnabled", true);
        parmEKT=null;
      //add by huangtt 20180515
    	this.clearValue("EKT_CURRENT_BALANCE;EKT_AMT;GIFT_CARD2;GIFT_CARD;NO_PAY_OTHER_ALL;PAY_OTHER4;PAY_OTHER3;NO_PAY_OTHER;");
    	callFunction("UI|PanelEkt|setVisible", false); 
    	
        onInit();
    }

    /**
     * 支付方式改变事件
     */
    public void payTypeChange() {
        if (getValue("PAY_TYPE").toString().equals("PAY_CHECK")) {
            this.callFunction("UI|CHECK_NO|setEnabled", true);
            this.callFunction("UI|REMARK|setEnabled", true);
        }
        else {
            this.callFunction("UI|CHECK_NO|setEnabled", false);
            this.callFunction("UI|REMARK|setEnabled", false);
        }
    }

    /**
     * 查询后界面控件赋值
     * @param parm TParm
     */
    public void initPage(TParm parm) {
        setValue("ADM_TYPE", "I");
        setValue("YELLOW_SIGN", parm.getDouble("YELLOW_SIGN", 0));
        setValue("RED_SIGN", parm.getDouble("RED_SIGN", 0));
        setValue("GREENPATH_VALUE", parm.getDouble("GREENPATH_VALUE", 0));
        setValue("MR_NO", parm.getValue("MR_NO", 0));
        setValue("IPD_NO", parm.getValue("IPD_NO", 0));
        setValue("CASE_NO", parm.getValue("CASE_NO", 0));
        setValue("BED_NO", parm.getValue("BED_NO", 0));
        setValue("PAT_NAME", parm.getValue("PAT_NAME", 0));
        setValue("SEX_CODE", parm.getValue("SEX_CODE", 0));
        setValue("DEPT_CODE", parm.getValue("DEPT_CODE", 0));
        setValue("STATION_CODE", parm.getValue("STATION_CODE", 0));
        setValue("LEFT_BILPAY", parm.getDouble("TOTAL_BILPAY", 0)-parm.getDouble("TOTAL_AMT", 0));
        setValue("SUM_TOTAL", parm.getValue("CUR_AMT", 0));
    }

//    /**
//     * 校验开关帐
//     * @return boolean
//     */
//    public boolean checkNo() {
//        TParm parm = new TParm();
//        parm.setData("RECP_TYPE", "PAY");
//        parm.setData("CASHIER_CODE", Operator.getID());
//        parm.setData("STATUS", "0");
//        parm.setData("TERM_IP", Operator.getIP());
//        TParm noParm = BILInvoiceTool.getInstance().selectNowReceipt(parm);
//        String updateNo = noParm.getValue("UPDATE_NO", 0);
//        startInvno = "";
//        startInvno = noParm.getValue("START_INVNO", 0);
//        if (updateNo == null || updateNo.length() == 0) {
//            return false;
//        }
//
//        setValue("PRINT_NO", updateNo);
//        return true;
//    }
    
	/**
	 * 初始化票据
	 * 
	 * ===zhangp 20120731
	 * 
	 * @param bilInvoice
	 *            BilInvoice
	 * @return boolean
	 */
	private boolean initBilInvoice(BilInvoice bilInvoice) {
		// 检核开关帐
		if (bilInvoice == null) {
			this.messageBox_("你尚未开账!");
			return false;
		}
		// 检核当前票号
		if (bilInvoice.getUpdateNo().length() == 0
				|| bilInvoice.getUpdateNo() == null) {
			this.messageBox_("无可打印的票据!");
			return false;
		}
		// 检核当前票号
		if (bilInvoice.getUpdateNo().equals(bilInvoice.getEndInvno())) {
			this.messageBox_("最后一张票据!");
		}
		if (BILTool.getInstance().compareUpdateNo("PAY", Operator.getID(),
				Operator.getRegion(), bilInvoice.getUpdateNo())) {
			setValue("PRINT_NO", bilInvoice.getUpdateNo());
			startInvno = bilInvoice.getStartInvno();//===zhangp 20120821
		} else {
			messageBox("票据已用完");
			return false;
		}
		return true;
	}

    /**
     * 初始化数据方法
     */
	public void initiation() {
		setValue("ADM_TYPE", "I");
		this.setValue("PAY_TYPE", "C0");
		// 显示票号
		// ===zhangp 20120731 start
		// if (!checkNo()) {
		BilInvoice bilInvoice = new BilInvoice();
		initBilInvoice(bilInvoice.initBilInvoice("PAY"));
		// this.checkNo();
		// ===zhangp 20120731 end
	}

    /**
     * 得到科室名称
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode) {
        String deptDesc = "";
        String selDept =
            " SELECT DEPT_CHN_DESC " +
            "   FROM SYS_DEPT " +
            "  WHERE DEPT_CODE = '" + deptCode + "' ";
//        System.out.println("selDept"+selDept);
        //查询科室名称
        TParm selDeptParm = new TParm(TJDODBTool.getInstance().select(
            selDept));
        deptDesc = selDeptParm.getValue("DEPT_CHN_DESC", 0);
//        System.out.println("deptDesc"+deptDesc);
        return deptDesc;
    }

    /**
     * 得到病区名称
     * @param stationCode String
     * @return String
     */
    public String getStationDesc(String stationCode) {
        String stationDesc = "";
        String selStation =
            " SELECT STATION_DESC " +
            "   FROM SYS_STATION " +
            "  WHERE STATION_CODE = '" + stationCode + "' ";
//        System.out.println("selStation"+selStation);
        //查询病区名称
        TParm selStationParm = new TParm(TJDODBTool.getInstance().select(
            selStation));
        stationDesc = selStationParm.getValue("STATION_DESC", 0);
//        System.out.println("stationDesc"+stationDesc);
        return stationDesc;
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
        if(!parmEKT.getValue("MR_NO").equals(this.getValue("MR_NO"))){
        	this.messageBox("医疗卡信息与病人信息不符");
            return; 
        }
        this.setValue("PAY_TYPE", "EKT");
        callFunction("UI|PAY_TYPE|setEnabled", false);
        this.selBilPay();
        setValue("EKT_CURRENT_BALANCE", parmEKT
				.getDouble("CURRENT_BALANCE"));
        double totAmt = getValueDouble("EKT_TOT_AMT");
		setValue("EKT_AMT", StringTool.round((parmEKT.getDouble("CURRENT_BALANCE") - totAmt), 2));
		double payOther3 = EKTpreDebtTool.getInstance().getPayOther(parmEKT.getValue("MR_NO"), EKTpreDebtTool.PAY_TOHER3);
		double payOther4 = EKTpreDebtTool.getInstance().getPayOther(parmEKT.getValue("MR_NO"), EKTpreDebtTool.PAY_TOHER4);
		setValue("GIFT_CARD", payOther3);
		setValue("GIFT_CARD2", payOther4);
		setValue("NO_PAY_OTHER_ALL", getValueDouble("EKT_CURRENT_BALANCE") - payOther3 - payOther4);
        
//        this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
//        onQueryByCaseNo();
    }
    
    public void onPayOther3(){	
		double payOther3 = getValueDouble("PAY_OTHER3");
		double payOther4 = 0;
		double payOtherTop3 = getValueDouble("GIFT_CARD");
		double payOtherTop4 = getValueDouble("GIFT_CARD2");
		double arAmt = getValueDouble("PRE_AMT");
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
		double arAmt = getValueDouble("PRE_AMT");
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
		double arAmt = getValueDouble("PRE_AMT");
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
    
    public void selBilPay(){
    	if(this.getValue("PAY_TYPE").equals("EKT")){
    		callFunction("UI|PanelEkt|setVisible", true);  
    		
    	}else{
    		callFunction("UI|PanelEkt|setVisible", false); 
    		clearValue("PAY_OTHER4;PAY_OTHER3;NO_PAY_OTHER");
    	}
    }
    
    public void setEktAmt(){
    	if(this.getValue("PAY_TYPE").equals("EKT")){
    		this.setValue("EKT_AMT", df.format(this.getValueDouble("EKT_CURRENT_BALANCE")-this.getValueDouble("PRE_AMT")));
    	}
    }
    
    public double payOther(String tradeNo,String payOther){
    	String ektSql = "SELECT TRADE_NO,AMT,PAY_OTHER3,PAY_OTHER4 FROM EKT_TRADE WHERE TRADE_NO='"+tradeNo+"' AND  BUSINESS_TYPE='PAY'";
		System.out.println("ektsql----"+ektSql);
		TParm ektParm = new TParm(TJDODBTool.getInstance().select(ektSql));
		double pay = 0;
		if(ektParm.getCount() > 0){
			pay = ektParm.getDouble(payOther, 0);
		}
		return pay;
    }
}
