package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;

import jdo.adm.ADMInpTool;
import jdo.bil.BILIBSRecpmTool;
import jdo.bil.BILTool;
import jdo.ekt.EKTIO;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.text.DecimalFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import com.dongyang.util.TypeTool;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: 收据召回退费窗口</p>
 *
 * <p>Description: 收据召回退费窗口</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILIBSRecpReturnDetailControl extends TControl {
    String caseNo = "";
    String sexCode = "";
    private TParm parmEKT;//医疗卡集合
    public void onInit() {
        super.onInit();
        TParm parm = (TParm)this.getParameter();
        setValue("RECEIPT_NO", parm.getValue("RECEIPT_NO", 0));
        setValue("MR_NO", parm.getValue("MR_NO", 0));
        setValue("IPD_NO", parm.getValue("IPD_NO", 0));
        setValue("PAT_NAME", parm.getValue("PAT_NAME", 0));
        setValue("AR_AMT", parm.getValue("AR_AMT", 0));
        caseNo = parm.getValue("CASE_NO", 0);
        sexCode = parm.getValue("SEX_CODE", 0);
        callFunction("UI|EKT_SHOW|setVisible", false);

    }

    /**
     * 退费
     */
    public void onSave() {
        TParm parm = new TParm();
        parm.setData("RECEIPT_NO", this.getValueString("RECEIPT_NO"));
        parm.setData("MR_NO", this.getValueString("MR_NO"));
        parm.setData("IPD_NO", this.getValueString("IPD_NO"));
        parm.setData("CASE_NO", caseNo);
        parm.setData("AR_AMT", this.getValueString("AR_AMT"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        if (this.getValueBoolean("FLG")){
            parm.setData("FLG", "Y");
            //====pangben 2016-1-21 套餐病患校验
            if(!ADMInpTool.getInstance().onCheckLumWorkReturn(caseNo)){
            	this.messageBox("套餐患者作废账单,先将母亲召回");
            	return;
            }
        }else
            parm.setData("FLG", "N");
        TParm actionParm = new TParm();
        actionParm.setData("DATA", parm.getData());
        TParm payTypeParm=BILIBSRecpmTool.getInstance().selectAllData(parm);
        if(payTypeParm.getCount()<=0){
        	this.messageBox("没有查询到票据信息");
        	return;
        }
        //======pangben 2016-6-27 添加微信、支付宝退票交易号录入
        if(payTypeParm.getDouble("PAY_TYPE09",0)>0||payTypeParm.getDouble("PAY_TYPE10",0)>0){
        	TParm typeParm=new TParm();
        	if(payTypeParm.getDouble("PAY_TYPE09",0)>0){
        		typeParm.setData("WX_FLG","Y");
        		typeParm.setData("WX_AMT",payTypeParm.getDouble("PAY_TYPE09",0));
        		
        	}
        	if(payTypeParm.getDouble("PAY_TYPE10",0)>0){
        		typeParm.setData("ZFB_FLG","Y");
        		typeParm.setData("ZFB_AMT",payTypeParm.getDouble("PAY_TYPE10",0));
        	}
        	Object result = this.openDialog(
        	            "%ROOT%\\config\\bil\\BILPayTypeTransactionNo.x", typeParm, false);
        	if(null==result){
        		return;
        	}
        	actionParm.setData("PAY_TYPE_FLG","Y");//支付类型退票添加交易号
        	actionParm.setData("PAY_TYPE_PARM",((TParm)result).getData());
        }
        boolean ektFlg = false;
        if(payTypeParm.getDouble("PAY_MEDICAL_CARD", 0) != 0){
        	if(parmEKT == null){
    			this.messageBox("请读取医疗卡信息");
    			return ;
    		}
    		if (!EKTIO.getInstance().ektSwitch()) {
    			messageBox_("医疗卡流程没有启动!");
    			return ;
    		}
    		
    		//缴费作业时扣款记录
    		String ibsSql = "SELECT PAY_OTHER3,PAY_OTHER4 FROM EKT_TRADE WHERE TRADE_NO='"+payTypeParm.getValue("EKT_BUSINESS_NO", 0)+"'";
    		System.out.println("ibsSql----"+ibsSql);
    		TParm ibsParm = new TParm(TJDODBTool.getInstance().select(ibsSql));
    		System.out.println("ibsParm---"+ibsParm);
    		//查询缴费作业时用预交金冲销，并且预交金是用医疗卡支付的数据
    		String bilSql = "SELECT PAY_OTHER3,PAY_OTHER4 FROM EKT_TRADE" +
    				" WHERE TRADE_NO IN (SELECT BUSINESS_NO FROM BIL_PAY" +
    				" WHERE RESET_RECP_NO = '"+this.getValueString("RECEIPT_NO")+"'" +
    				" AND CASE_NO = '"+caseNo+"'" +
    				" AND BUSINESS_NO IS NOT NULL" +
    				" AND PAY_TYPE = 'EKT')";
    		TParm bilParm = new TParm(TJDODBTool.getInstance().select(bilSql));
    		double payOther3 = 0;
    		double payOther4 = 0;
    		if(ibsParm.getCount() > 0){
    			payOther3 += ibsParm.getDouble("PAY_OTHER3", 0);
    			payOther4 += ibsParm.getDouble("PAY_OTHER4", 0);
    		}
    		if(bilParm.getCount() > 0){
    			payOther3 += bilParm.getDouble("PAY_OTHER3", 0);
    			payOther4 += bilParm.getDouble("PAY_OTHER4", 0);
    		}
    		
    		TParm parmE = new TParm();
    		parmE.setData("READ_CARD", parmEKT.getData());
    		parmE.setData("PAY_OTHER3",payOther3);
    		parmE.setData("PAY_OTHER4",payOther4);
    		parmE.setData("EXE_AMT",-this.getValueDouble("AR_AMT"));
    		parmE.setData("MR_NO",this.getValueString("MR_NO"));
    		parmE.setData("BUSINESS_TYPE","IBS");
    		parmE.setData("CASE_NO",caseNo);//交易号
    		Object r =  this.openDialog("%ROOT%\\config\\ekt\\EKTChageOtherUI.x",parmE);
    		if(r == null){
    			this.messageBox("医疗卡扣款取消，不执行保存");
    			return ;
    		}
    		TParm rParm = (TParm) r;
    		if(rParm.getErrCode() < 0){
    			this.messageBox(rParm.getErrText());
    			return ;
    		}else if(rParm.getValue("OP_TYPE").equals("2")){
    			return ;
    		}else{
    			actionParm.setData("ektSql", rParm.getData("ektSql"));
    			ektFlg = true;
    		}
        }
        
        TParm result = TIOM_AppServer.executeAction("action.bil.BILAction",
                "onSaveReceiptReturn", actionParm);
        
        if(ektFlg){
        	 this.setReturnValue("true");
        	 this.closeWindow();
        }
        
        String preAmt = result.getValue("PRE_AMT");
        String payType = result.getValue("PAY_TYPE");
        String payTypeName= result.getValue("PAY_TYPE_NAME");//====pangben 2014-6-24 显示中文
        String invNo = result.getValue("INV_NO");
        String stationCode = result.getValue("STATION_CODE");
        String deptCode = result.getValue("DEPT_CODE");
        String recpNo = result.getValue("RECEIPT_NO");
//        System.out.println("回冲预交金入参" + preAmt + "|" + invNo + "|" + recpNo +
//                           "|" + stationCode + "|" + deptCode);
        if (recpNo.length() > 0)
            printBillPay(preAmt, payTypeName, recpNo, stationCode, deptCode);
        this.messageBox("票据" + result.getValue("INV_NO") + "已收回");
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return;
        } else
            this.setReturnValue("true");
        this.closeWindow();
    }

    /**
     * 回冲打票
     * @param preAmt String
     * @param payType String
     * @param recpNo String
     * @param stationCode String
     * @param deptCode String
     */
    public void printBillPay(String preAmt, String payType, String recpNo,
                             String stationCode, String deptCode) {
        //打印预交金收据
        TParm forPrtParm = new TParm();
        String bilPayC = StringUtil.getInstance().numberToWord(TypeTool.
                getDouble(preAmt));
        forPrtParm.setData("TITLE", "TEXT", "住院预交金收据");
        Timestamp printDate = (SystemTool.getInstance().getDate());
        String pDate = StringTool.getString(printDate, "yyyy/MM/dd HH:mm:ss");
        forPrtParm.setData("COPY", "TEXT", "");
        forPrtParm.setData("REGION_CHN_DESC", "TEXT",
                           Operator.getHospitalCHNFullName());
        forPrtParm.setData("REGION_ENG_DESC", "TEXT",
                           Operator.getHospitalENGFullName());
        forPrtParm.setData("Data", "TEXT",pDate);
        forPrtParm.setData("Name", "TEXT",
                           this.getValueString("PAT_NAME"));
        forPrtParm.setData("TOLL_COLLECTOR", "TEXT", Operator.getName());
        forPrtParm.setData("MR_N0", "TEXT",
                           this.getValueString("MR_NO"));
        forPrtParm.setData("IPD_NO", "TEXT",
                           "住院号:" + this.getValueString("IPD_NO"));
        if (sexCode.equals("1"))
            forPrtParm.setData("SEX", "TEXT", "性别:" + "男");
        else if (sexCode.equals("2"))
            forPrtParm.setData("SEX", "TEXT", "性别:" + "女");
        else
            forPrtParm.setData("SEX", "TEXT", "性别:" + "不详");
        forPrtParm.setData("DEPT", "TEXT",
                           getDeptDesc(deptCode));
        forPrtParm.setData("STATION", "TEXT",
                           getStationDesc(stationCode));
        forPrtParm.setData("Capital", "TEXT", bilPayC);
        DecimalFormat formatObject = new DecimalFormat("###########0.00");

        forPrtParm.setData("SmallCaps", "TEXT",
                           formatObject.format(
                                   TypeTool.getDouble(preAmt))+"元");
//        forPrtParm.setData("SEQ_NO", "TEXT",
//               "流水号:"+ recpNo);
        forPrtParm.setData("RECEIPT_NO", "TEXT",
                            recpNo);
        forPrtParm.setData("PRINT_DATE", "TEXT", "打印日期:" + pDate);
//        if (payType.equals("C0")) {
//            forPrtParm.setData("CASH", "TEXT", "√");
//            forPrtParm.setData("BANK", "TEXT", "");
//            forPrtParm.setData("OTHERS", "TEXT", "");
//        } else if (payType.equals("C1")) {
//            forPrtParm.setData("CASH", "TEXT", "");
//            forPrtParm.setData("BANK", "TEXT", "√");
//            forPrtParm.setData("OTHERS", "TEXT", "");
//        } else {
//            forPrtParm.setData("CASH", "TEXT", "");
//            forPrtParm.setData("BANK", "TEXT", "");
//            forPrtParm.setData("OTHERS", "TEXT", "√");
//        }
        payType +="：" + formatObject.format(TypeTool.getDouble(preAmt))+"元";
        forPrtParm.setData("WAY","TEXT",payType) ;//====pangben 2014-4-15 支付方式显示正确

//        System.out.println("回冲预交金" + forPrtParm);
//        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILPrepayment.jhw",
//                             forPrtParm);
        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILPrepayment_V45.jhw",
                forPrtParm);
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
        //查询科室名称
        TParm selDeptParm = new TParm(TJDODBTool.getInstance().select(
                selDept));
        deptDesc = selDeptParm.getValue("DEPT_CHN_DESC", 0);
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
        //查询病区名称
        TParm selStationParm = new TParm(TJDODBTool.getInstance().select(
                selStation));
        stationDesc = selStationParm.getValue("STATION_DESC", 0);
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
        if(!parmEKT.getValue("MR_NO").equals(this.getValueString("MR_NO"))){
        	this.messageBox("医疗卡信息与病人信息不符");
            return; 
        }
        callFunction("UI|EKT_SHOW|setVisible", true);  
        
        
    }

}

