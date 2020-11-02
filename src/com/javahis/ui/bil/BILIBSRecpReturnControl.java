package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;

import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;

import jdo.sys.PatTool;
import jdo.bil.BILIBSRecpmTool;
import jdo.bil.BILIBSRecpdTool;

import com.javahis.util.StringUtil;

import java.sql.Timestamp;

import com.dongyang.util.StringTool;

import jdo.sys.IReportTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.dongyang.jdo.TJDODBTool;

import jdo.bil.BilInvoice;

import com.dongyang.manager.TIOM_AppServer;

import jdo.bil.BILInvoiceTool;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * <p>Title: 住院账务召回控制类</p>
 *
 * <p>Description: 住院账务召回控制类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILIBSRecpReturnControl
    extends TControl {
    //计算病患住院天数方法
    //int i = ADMTool.getInstance().getAdmDays(CASE_NO);
    TParm mainParm = new TParm();
    TParm detailParm = new TParm();
    String caseNo = "";
    String stationCode = "";
    private TParm parmEKT;//医疗卡集合
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        //账单主档table专用的监听
        getTTable("MainTable").addEventListener(TTableEvent.
                                                CHECK_BOX_CLICKED, this,
                                                "onMainTableComponent");
//        test();
        this.onClear();
    }
    public void test() {
        String admSql =
                " SELECT CASE_NO FROM ADM_INP ";
        TParm admParm = new TParm(TJDODBTool.getInstance().select(admSql)) ;
    //    System.out.println("住院数据"+admParm);
        for (int i = 0; i < admParm.getCount(); i++) {
            String sql =
                    " SELECT CASE_NO,SUM (TOT_AMT) AS TOT_AMT " +
                    "   FROM IBS_ORDD " +
                    "  WHERE CASE_NO = '" + admParm.getValue("CASE_NO", i) +
                    "' " +
                    "    AND CASE_NO_SEQ NOT IN (SELECT DISTINCT (CASE_NO_SEQ) " +
                    "                              FROM IBS_ORDM " +
                    "                             WHERE CASE_NO = '" +
                    admParm.getValue("CASE_NO", i) + "') "+
                    "  GROUP BY CASE_NO ";
            TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
            if(parm.getDouble("TOT_AMT",0)!=0)
                System.out.println("第"+i+"个人员"+parm.getValue("CASE_NO",0));
        }
    }
    /**
     * 初始化界面
     */
    public void initPage() {
        //当前时间
        Timestamp today = SystemTool.getInstance().getDate();
        //获取选定日期的前一天的日期
        Timestamp yesterday = StringTool.rollDate(today, -1);
        this.setValue("S_DATE", yesterday);
        this.setValue("E_DATE", today);
    }

    /**
     * 主表的单击事件
     */
    public void onMainTableClicked() {
        TTable mainTable = getTTable("MainTable");
        TTable detailTable = getTTable("DetailTable");
        int row = mainTable.getSelectedRow();
        if (row < 0)
            return;
        TParm parm = new TParm();
        TParm regionParm = mainTable.getParmValue();
        parm.setData("RECEIPT_NO", regionParm.getData("RECEIPT_NO", row));
        TParm data = BILIBSRecpdTool.getInstance().selDateForReturn(parm);
        detailTable.setParmValue(data);
    }

    /**
     * 主表监听事件
     * @param obj Object
     * @return boolean
     */
    public boolean onMainTableComponent(Object obj) {
        TTable mainTable = (TTable) obj;
        mainTable.acceptText();
        TParm tableParm = mainTable.getParmValue();
        mainParm = new TParm();
        int count = tableParm.getCount("RECEIPT_NO");
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("FLG", i)) {
                mainParm.addData("RECEIPT_NO",
                                 tableParm.getValue("RECEIPT_NO", i));
                mainParm.addData("IPD_NO",
                                 tableParm.getValue("IPD_NO", i));
                mainParm.addData("CASE_NO",
                                 tableParm.getValue("CASE_NO", i));
                mainParm.addData("MR_NO",
                                 tableParm.getValue("MR_NO", i));
                //===zhangp 20120827 start
//                mainParm.addData("AR_AMT",
//                                 tableParm.getValue("AR_AMT", i));
                mainParm.addData("AR_AMT",
                				 tableParm.getValue("OWN_AMT", i));
                //===zhangp 20120827 end
                mainParm.addData("PAY_MEDICAL_CARD",tableParm.getValue("PAY_MEDICAL_CARD", i));
            }
        }
        return true;
    }

    /**
     * 查询
     */
    public void onQuery() {
        TTable mainTable = getTTable("MainTable");
        String mrNo = this.getValueString("MR_NO");
        String realMrNo = PatTool.getInstance().checkMrno(mrNo);
        String ipdNo = this.getValueString("IPD_NO");
        String realIpdNo = PatTool.getInstance().checkIpdno(ipdNo);
        //==liling 20140821 modify start=====
        TParm patInfoParm = PatTool.getInstance().getInfoForMrno(realMrNo);
        if(patInfoParm.getCount("MR_NO")<=0){
        	this.messageBox("该病案号不存在");
        	return;
        }
        String patName = patInfoParm.getValue("PAT_NAME", 0);
        String sexCode = patInfoParm.getValue("SEX_CODE", 0);
        Timestamp birthDate = patInfoParm.getTimestamp("BIRTH_DATE", 0);
        String  age= StringUtil.showAge(birthDate, SystemTool.getInstance().getDate());
        TParm parm = new TParm();
        if (getValueString("MR_NO").length() == 0 &&
            getValueString("IPD_NO").length() == 0) {
            this.messageBox("请输入病案号或住院号");
            return;
        }
        if (getValueString("MR_NO").length() != 0){
            parm.setData("MR_NO", realMrNo);}
        if (getValueString("IPD_NO").length() != 0){
            parm.setData("IPD_NO", realIpdNo);}
            parm.setData("PAT_NAME", patName);
            parm.setData("SEX_CODE", sexCode);
            parm.setData("AGE", age);
            parm.setData("count", "0");// 判断是否从明细点开的就诊号选择      
        TParm caseNoParm = (TParm) openDialog(
				"%ROOT%\\config\\bil\\BILChooseVisit.x", parm);
		caseNo = caseNoParm.getValue("CASE_NO");    
		parm.setData("CASE_NO",caseNo);
		//==liling 20140821 modify end=====
		TParm mParm = BILIBSRecpmTool.getInstance().selDateForReturn(parm);
		if (mParm.getCount("MR_NO") <= 0) {
        this.messageBox("此患者无收据");
        return;
		}
//        caseNo = mParm.getValue("CASE_NO", 0);
		setValue("MR_NO", realMrNo);
		setValue("IPD_NO", realIpdNo);  
		setValue("AGE", age);
		setValue("PAT_NAME", patName);
		setValue("SEX_CODE", sexCode);
        mainTable.setParmValue(mParm);
//        TParm patInfoParm = PatTool.getInstance().getInfoForMrno(realMrNo);
//        String patName = patInfoParm.getValue("PAT_NAME", 0);
//        String sexCode = patInfoParm.getValue("SEX_CODE", 0);
//        Timestamp birthDate = patInfoParm.getTimestamp("BIRTH_DATE", 0);
//        setValue("PAT_NAME", patName);
//        setValue("SEX_CODE", sexCode);
//        TParm  admInpParm = new TParm();
//        admInpParm.setData("CASE_NO", caseNo);
//        TParm admInpInfoParm = ADMInpTool.getInstance().selectall(admInpParm);
//        stationCode = admInpInfoParm.getValue("IN_STATION_CODE",0);
//        Timestamp inDate = admInpInfoParm.getTimestamp("IN_DATE", 0);
//        String AGE = StringUtil.showAge(birthDate, inDate);
//        setValue("AGE", AGE);
        TParm  admInpParm = new TParm();
        admInpParm.setData("CASE_NO", caseNo);
        TParm admInpInfoParm = ADMInpTool.getInstance().selectall(admInpParm);
        stationCode = admInpInfoParm.getValue("IN_STATION_CODE",0);

    }

    /**
     * 得到TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    /**
     * 保存事件
     * @return boolean
     */
    public boolean onSave() {
    	if (getValueString("MR_NO").length() == 0 &&getValueString("IPD_NO").length() == 0) {
	        this.messageBox("请输入病案号或住院号");
	         return false;
	    }
    	TTable table = getTTable("MainTable");
        TParm tableParm = table.getParmValue();
        int count = tableParm.getCount("RECEIPT_NO");
        if (tableParm== null||tableParm.getCount("RECEIPT_NO")==0) {
            this.messageBox_("无可操作的票据!");
            return false;
        }
        int num=0;
        for (int i = 0; i < count; i++) {
            if ("Y".equals(tableParm.getValue("FLG", i))) {
            	num++;
            }}
        if(num<=0){
           	this.messageBox("请选择要操作的数据");
   	        return false;
   	        }
    	TParm parm = new TParm();
    	String mrNo = this.getValueString("MR_NO");
    	String realMrNo = PatTool.getInstance().checkMrno(mrNo);    	
    	if (getValueString("MR_NO").length() != 0)
    	     parm.setData("MR_NO", realMrNo);
    	 parm.setData("DS_DATE","N");//======pangben 2014-5-12 添加管控，在院病患不可以操作召回
         parm.setData("CANCEL_FLG","N");//======pangben 2014-8-4 添加管控取消住院
         TParm admInpParm= ADMInpTool.getInstance().selectall(parm);
         if (admInpParm.getCount()>0) {
 			this.messageBox("此病患住院中,不可以操作召回");
 			return false;
 		}
        //==liling 20140818 modify start=====
//        String sql="SELECT A.ADMCHK_FLG,A.DIAGCHK_FLG,A.BILCHK_FLG,A.QTYCHK_FLG FROM MRO_RECORD A WHERE A.CASE_NO='"+caseNo+"'  AND A.OUT_DATE IS NOT NULL ";
//        TParm flgParm=new TParm(TJDODBTool.getInstance().select(sql));
//        if (flgParm.getCount()>0) {
//        	if(null !=flgParm.getValue("ADMCHK_FLG", 0)&&"Y".equals(flgParm.getValue("ADMCHK_FLG", 0))){
//        		this.messageBox("病案首页住院处，已提交不可召回");
//    			return false;
//        	}
//        	if(null !=flgParm.getValue("DIAGCHK_FLG", 0)&&"Y".equals(flgParm.getValue("DIAGCHK_FLG", 0))){
//        		this.messageBox("病案首页医师，已提交不可召回");
//    			return false;
//        	}
//        	if(null !=flgParm.getValue("BILCHK_FLG", 0)&&"Y".equals(flgParm.getValue("BILCHK_FLG", 0))){
//        		this.messageBox("病案首页财务，已提交不可召回");
//    			return false;
//        	}
//        	if(null !=flgParm.getValue("QTYCHK_FLG", 0)&&"Y".equals(flgParm.getValue("QTYCHK_FLG", 0))){
//        		this.messageBox("病案首页病案室，已提交不可召回");
//    			return false;
//        	}
//        }
      //==liling 20140818 modify end=====
        mainParm.addData("PAT_NAME", this.getValueString("PAT_NAME"));
        mainParm.addData("FLG", this.getValueString("FLG"));
        mainParm.addData("SEX_CODE", this.getValueString("SEX_CODE"));
//        TParm parm=new TParm();
//        parm.setData("CASE_NO",mainParm.getValue("CASE_NO",0));
//        TParm admInpParm= ADMInpTool.getInstance().selectall(parm);
//        System.out.println("admInpParm：：：：：："+admInpParm);
//        if (admInpParm.getValue("NEW_BORN_FLG",0).equals("Y")) {//操作的人为新生儿
//        	//查询是否存在新生儿数据
//    		String sql = "SELECT A.MR_NO,A.DS_DATE,B.PAT_NAME,A.CASE_NO,IPD_NO FROM ADM_INP A,SYS_PATINFO B WHERE A.MR_NO=B.MR_NO AND A.IPD_NO='"+
//    		admInpParm.getValue("IPD_NO",0)+"' AND IN_DATE IS NOT NULL AND CANCEL_FLG <> 'Y' AND (NEW_BORN_FLG='N' OR NEW_BORN_FLG IS NULL) ORDER BY DS_DATE DESC ";
//    		TParm result= new TParm(TJDODBTool.getInstance().select(sql));
//    		if (result.getCount()>0) {
//        		parm.setData("CASE_NO",result.getValue("CASE_NO",0));
//        		TParm mParm = BILIBSRecpmTool.getInstance().selDateForReturn(parm);
//        		if (mParm.getCount()>0) {
//					this.messageBox("此病患为新生儿,母亲:"+result.getValue("PAT_NAME",0)+"存在票据不可以操作");
//					return false;
//				}
//			}
//		}
//        TParm actionParm  = mainParm.getRow();
        Object result = this.openDialog(
            "%ROOT%\\config\\bil\\BILIBSRecpReturnDetail.x", mainParm, false);
        if (result == null) {
            return false;
        }
        else
            this.messageBox("P0001");
        onClear();
        return true;
    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue("S_DATE;E_DATE;MR_NO;IPD_NO;PAT_NAME;" +
                   "SEX_CODE;AGE");
        this.callFunction("UI|MainTable|removeRowAll");
        this.callFunction("UI|DetailTable|removeRowAll");
        parmEKT=null;
        initPage();
    }

    /**
     * 补印
     * @throws ParseException 
     */
    public void onPrint() throws ParseException {
        if (!checkNo()) {
            this.messageBox("尚未开帐,请先开帐");
            return ;
        }
        TTable table = getTTable("MainTable");
        TParm tableParm = table.getParmValue();
        //=======pangben modify 20110513 start
        if (tableParm== null||tableParm.getCount("RECEIPT_NO")==0) {
            this.messageBox_("无可打印的票据!");
            return;
        }

        int count = tableParm.getCount("RECEIPT_NO");
        //==liling start=========
//        String [] typeName={"C0","C1","T0","C4","LPK","XJZKQ","EKT","INS"};
//   		String [] bilTypeName={"PAY_CASH","PAY_BANK_CARD","PAY_CHECK","PAY_DEBIT","PAY_GIFT_CARD",
//   				"PAY_DISCNT_CARD","PAY_MEDICAL_CARD","PAY_INS_CARD"};
//        String mrNo = this.getValueString("MR_NO");
//        String sql3="SELECT RESET_RECP_NO,case_no,receipt_no,opt_date FROM bil_pay  where mr_no='" +mrNo +"' order by opt_date desc";
//        String sql3="SELECT RESET_RECP_NO,opt_date FROM bil_pay  where case_no='" +caseNo +"' order by opt_date desc";
//        System.out.println("sql3++"+sql3);
//   		TParm parm3 = new TParm(TJDODBTool.getInstance().select(sql3));
//   		String recpNo=parm3.getValue("RESET_RECP_NO",0);
//   		System.out.println("parm3.getValue(RESET_RECP_NO)=="+recpNo);
//   		String sql4="select receipt_no from bil_pay where case_no='" +caseNo +"' and RESET_RECP_NO='" +recpNo +"' and transact_type !='03'";
//   		TParm parm4 = new TParm(TJDODBTool.getInstance().select(sql4));
//   		String receiptNo1=parm4.getValue("RECEIPT_NO");//缴费作业时勾选的预交金的RECEIPT_NO
//   		System.out.println("receiptNo1=="+receiptNo1);
//   		receiptNo1=receiptNo1.substring(1, receiptNo1.length()-1);
//   		receiptNo1=receiptNo1.replaceAll(",", "','").replaceAll(" ", "");
////   		System.out.println("bilPayDate+++ "+receiptNo1);
//   		String sql1 = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_PAYTYPE' ORDER BY ID ";
//   		String sql2 = "SELECT sum(pre_amt) as pre_amt, pay_type FROM bil_pay  where case_no='" +caseNo +"'  " +
//			"AND (transact_type = '01' OR transact_type = '02' OR transact_type = '04'  OR transact_type = '03') and receipt_no in ('@')  group by pay_type ";      		
//   		sql2 = sql2.replaceAll("@",receiptNo1 );
//   		System.out.println("sql2=="+sql2);
//   		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
//   		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
   		
   		DecimalFormat formatObject = new DecimalFormat("###########0.00");
   	  //  System.out.println("parm2=="+parm2);
//   		for (int j = 0; j < bilTypeName.length; j++) {
//   			parm2.setData(bilTypeName[j],0.00);
//		}
   	 //==liling end=========
     //=======pangben modify 20110513 stop
//        int row = table.getSelectedRow();
   		String payWay="";
        for (int i = 0; i < count; i++) {
            if ("Y".equals(tableParm.getValue("FLG", i))) {
                //初始化下一票号
                BilInvoice invoice = new BilInvoice();
                invoice = invoice.initBilInvoice("IBS");
                //检核开关帐
                if (invoice == null) {
                    this.messageBox_("你尚未开账!");
                    return;
                }
                //检核当前票号
                if (invoice.getUpdateNo().length() == 0 ||
                    invoice.getUpdateNo() == null) {
                    this.messageBox_("无可打印的票据!");
                    return;
                }
                //检核当前票号
                if (invoice.getUpdateNo().equals(invoice.
                    getEndInvno())) {
                    this.messageBox_("最后一张票据!");
//            return;
                }
                String printNo = invoice.getUpdateNo();
                String receiptNo = tableParm.getValue("RECEIPT_NO", i);
                TParm onREGReprintParm = new TParm();
                onREGReprintParm.setData("CASE_NO",  tableParm.getValue("CASE_NO", i));
                onREGReprintParm.setData("RECEIPT_NO", receiptNo);
                onREGReprintParm.setData("OPT_USER", Operator.getID());
                onREGReprintParm.setData("OPT_TERM", Operator.getIP());
                TParm result = new TParm();
                result = TIOM_AppServer.executeAction("action.bil.BILAction",
                    "onIBSReprint", onREGReprintParm);
                if (result.getErrCode() < 0) {
                    err(result.getErrName());
                    return;
                }
                TParm printParm = getPrintData(receiptNo);
                Timestamp printDate = (SystemTool.getInstance().getDate());

                TParm selAdmInp = new TParm();
                selAdmInp.setData("CASE_NO", caseNo);
                TParm admInpParm = ADMInpTool.getInstance().selectall(selAdmInp
                    );
                Timestamp inDataOut = admInpParm.getTimestamp("IN_DATE", 0);
                Timestamp outDataOut;
                if (admInpParm.getData("DS_DATE", 0) != null)
                    outDataOut = admInpParm.getTimestamp("DS_DATE", 0);
                else
                    outDataOut = printDate;

                String deptCode = admInpParm.getValue("DEPT_CODE", 0);

                String inDate = StringTool.getString(inDataOut, "yyyyMMdd");
                String outDate = StringTool.getString(outDataOut, "yyyyMMdd");
                String pDate = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");
                
                if(getInsAdmConfirm(caseNo).getCount()>1)
                {
                	TParm parm  = getBillmEndDate(receiptNo);
             //   	System.out.println("parm:"+parm);
                    String enddate = ""; 
                    String appdate = "";
                    Timestamp yesterday = StringTool.rollDate(
            				StringTool.getTimestamp(""+parm.getData("END_DATE",0), "yyyy-MM-dd HH:mm:ss"), -1);
                    enddate  = StringTool.getString(yesterday, "yyyyMMdd") ;
                    Timestamp sysDate = SystemTool.getInstance().getDate();
                    appdate = StringTool.getString(sysDate,"yyyyMMdd");
//                    System.out.println("enddate:"+enddate+"  "+appdate);
                    //同一年，并且confirmNo记录为2的 取带KN的资格确认书
                    if (enddate.substring(0, 4).equals(appdate.substring(0, 4))) 
                    {
                   	 inDate = enddate.substring(0,4)+"0101"; 
                   	 inDataOut = StringTool.getTimestamp(inDate, "yyyyMMdd");
                    }
                    //取非KN的资格确认书
                    else {
                   	 outDate = enddate.substring(0,4)+"1231";
                   	 outDataOut = StringTool.getTimestamp(outDate, "yyyyMMdd");
                    }                	
                }
                String sYear = inDate.substring(0,4);
                String sMonth = inDate.substring(4,6);
                String sDate = inDate.substring(6,8);
                String eYear = outDate.substring(0,4);
                String eMonth = outDate.substring(4,6);
                String eDate = outDate.substring(6,8);
                String pYear = pDate.substring(0,4);
                String pMonth = pDate.substring(4,6);
                String pDay = pDate.substring(6,8);
//                int rollDate = StringTool.getDateDiffer(outDataOut, inDataOut) ==
//                    0 ? 1 : StringTool.getDateDiffer(outDataOut, inDataOut);
        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        		Date now = new Date();
        		now = sdf.parse(eYear+"-"+eMonth+"-"+eDate+" 23:59:59");
        		Date date = sdf.parse(sYear+"-"+sMonth+"-"+sDate+" 00:00:00");
        		long l=now.getTime()-date.getTime();
        		long day=l/(24*60*60*1000);
                day = (day == 0 ? 1 : day);// add by wanglong 20130715
        		int rollDate = Integer.valueOf(""+day);
              //  int rollDate = StringTool.getDateDiffer(outDataOut, inDataOut)+1;
    			//====zhangp 20120224 start
    			inDate = StringTool.getString(inDataOut, "yyyy/MM/dd");
    			outDate = StringTool.getString(outDataOut, "yyyy/MM/dd");
//    			printParm.setData("STARTDATE", "TEXT", inDate);
//    			printParm.setData("ENDDATE", "TEXT", outDate);
    			printParm.setData("STARTDATE", inDate);//==liling
    			printParm.setData("ENDDATE",  outDate);//==liling
    			printParm.setData("PRINTDATE", "TEXT", pDate);
    			//=====zhangp 20120224 end
                printParm.setData("COPY", "TEXT", "COPY");
                printParm.setData("sYear","TEXT", sYear);
                printParm.setData("sMonth","TEXT", sMonth);
                printParm.setData("sDate","TEXT", sDate);
                printParm.setData("eYear","TEXT", eYear);
                printParm.setData("eMonth","TEXT", eMonth);
                printParm.setData("eDate","TEXT", eDate);
                printParm.setData("pYear","TEXT", pYear);
                printParm.setData("pMonth","TEXT", pMonth);
                printParm.setData("pDay","TEXT", pDay);
//                printParm.setData("rollDate","TEXT", rollDate);
                printParm.setData("rollDate",rollDate);//==liling
                printParm.setData("PAT_NAME","TEXT", this.getValueString("PAT_NAME"));
                printParm.setData("SEX_CODE","TEXT", getDesc(this.getValueString("SEX_CODE")));
                printParm.setData("STATION_CODE","TEXT",getStation(stationCode));
                printParm.setData("RECEIPT_NO","TEXT",receiptNo);
                printParm.setData("CASHIER_CODE","TEXT", Operator.getName());
                printParm.setData("MR_NO","TEXT", this.getValueString("MR_NO"));
                printParm.setData("BILL_DATE","TEXT", outDataOut);
                printParm.setData("CHARGE_DATE","TEXT", outDataOut);
                printParm.setData("DEPT_CODE","TEXT", getDept(deptCode));
                printParm.setData("OPT_USER", "TEXT", Operator.getID());// modify by wanglong 20130221
                printParm.setData("OPT_DATE","TEXT", printDate);
                String printDateC = StringTool.getString(printDate,
                    "yyyy年MM月dd日");
                printParm.setData("DATE", "TEXT", printDateC);
                printParm.setData("NO1", "TEXT", receiptNo);//===流水号
                printParm.setData("NO2", "TEXT", printNo);//===票据号
                printParm.setData("HOSP", "TEXT",
                                  Operator.getHospitalCHNFullName());
                printParm.setData("IPD_NO","TEXT", this.getValueString("IPD_NO"));
                printParm.setData("BIL_PAY_C","TEXT",
                                  StringUtil.getInstance().numberToWord(Double.
                    parseDouble(String.valueOf(tableParm.getValue("PAY_BILPAY",i)))));
                printParm.setData("BIL_PAY","TEXT",tableParm.getValue("PAY_BILPAY",i));
                printParm.setData("REDUCE_AMT","TEXT",tableParm.getValue("REDUCE_AMT",i));
                DecimalFormat df = new DecimalFormat("##########0.00");
                double amtD = StringTool.getDouble(tableParm.getValue(
                    "AR_AMT", i)) -
                    StringTool.getDouble(tableParm.getValue("PAY_BILPAY", i));

                String amt = formatObject.format(amtD<0?-amtD:amtD);
                String tmp=StringUtil.getInstance().numberToWord(Double.valueOf(printParm.getData("TOT_AMT", "TEXT").toString()));
                          // StringUtil.getInstance().numberToWord( Double.valueOf(printParm.getData("TOT_AMT", "TEXT").toString()));
                
      		  if(tmp.lastIndexOf("分") > 0){//修改金额为分的，后面不能加正或整字样 modify 2013722 caoyong
                	tmp = tmp.substring(0,tmp.lastIndexOf("分")+1);//取得正确的大写金额
                }
      		  
                printParm.setData("amtToWord", "TEXT",tmp );

                if (StringTool.getDouble(tableParm.getValue("AR_AMT",i))-StringTool.getDouble(tableParm.getValue("PAY_BILPAY",i)) < 0) {
                    printParm.setData("XS","TEXT", 0.00);
                    printParm.setData("YT","TEXT", amt);
                }
                else {
                    printParm.setData("XS","TEXT", amt);
                    printParm.setData("YT","TEXT", 0.00);
                }
                String caseNo = tableParm.getData("CASE_NO",i).toString();
                //===zhangp 20120412 start
//                String sql =
//                	"SELECT SUM(ACCOUNT_PAY_AMT) ACCOUNT_PAY_AMT,SUM(APPLY_AMT) APPLY_AMT,SUM(OWN_AMT) OWN_AMT FROM INS_IBS " +
//                	" WHERE CASE_NO = '"+caseNo+"'";
//                TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));
                TParm insParm = getInsParm(caseNo,receiptNo);
                if(insParm.getErrCode()<0){
                	return;
                }
                double armyAi_amt = insParm.getDouble("ARMYAI_AMT");//补助
                double account_pay_amt =  insParm.getDouble("ACCOUNT_PAY_AMT");//个人账户
                double nhi_comment =  insParm.getDouble("NHI_COMMENT");//救助
                double nhi_pay =  insParm.getDouble("NHI_PAY");//统筹
                double ins = armyAi_amt + nhi_comment + nhi_pay;//保险给付
                double pay_cash = insParm.getDouble("OWN_PAY");//现金支付/自付

                if(insParm.getCount()>0){
//                	printParm.setData("PAY_INS_CARD", "TEXT",
//                            Math.abs(StringTool.round(
//                            		insParm.getDouble("ACCOUNT_PAY_AMT", 0),
//                                             2)));
//                	printParm.setData("PAY_INS", "TEXT",
//                            Math.abs(StringTool.round(insParm.getDouble("APPLY_AMT", 0),
//                                             2)));
//                	printParm.setData("PAY_INS_PERSON", "TEXT",
//                            Math.abs(StringTool.round(
//                            		insParm.getDouble("OWN_AMT", 0),
//                                             2)));
//                	printParm.setData("PAY_INS_CASH", "TEXT",
//                            Math.abs(StringTool.round(
//                            		insParm.getDouble("OWN_AMT", 0) -
//                            		insParm.getDouble("ACCOUNT_PAY_AMT", 0),
//                                             2)));
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
                            		,2)));
                	printParm.setData("PAY_INS_CASH", "TEXT",
                            Math.abs(StringTool.round(
                            		pay_cash
                            		,2)));
                	printParm.setData("PAY_INS_BIG","TEXT",Math.abs(StringTool.round(nhi_comment, 2)));//caowl 20130318 报表显示上增加大额支付
                	//===zhangp 20120412 end
                }else{
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
                	printParm.setData("PAY_INS_BIG","TEXT", Math.abs(StringTool.round(
                            		0.00,
                                             2)));//caowl 20130318 报表显示上增加大额支付
                }
                //===zhangp 20120321 end
//    			System.out.println("printParm"+printParm);
    			//====zhangp 20120306 modify end
    			//====zhangp 20120306 modify start
//    			double arAmt = tableParm.getDouble("AR_AMT",i)-tableParm.getDouble("PAY_BILPAY",i);
                
                double arAmt = 0;
                if(pay_cash>0){
                	arAmt = pay_cash - tableParm.getDouble("PAY_BILPAY",i);
                }else{
                	arAmt = tableParm.getDouble("AR_AMT",i) - tableParm.getDouble("PAY_BILPAY",i);
                }
    			if(arAmt>=0){
    				printParm.setData("ADDAMT", "TEXT", Math.abs(StringTool.round(tableParm.getDouble("PAY_CASH",i), 2)));//补缴
    				//===zhangp 20120331 start
    				printParm.setData("ADDCHECKAMT", "TEXT", Math.abs(
    						StringTool.round(
    								//===zhangp 20120417 start
//    								tableParm.getDouble("PAY_CHECK",i)
    								tableParm.getDouble("PAY_CHECK",i) + tableParm.getDouble("PAY_BANK_CARD",i) + tableParm.getDouble("PAY_DEBIT",i)
    								//===zhangp 20120417 end
    						, 2)));//补缴
    				//===zhangp 20120331 end
    			}else{
    				printParm.setData("BACKAMT", "TEXT", Math.abs(StringTool.round(tableParm.getDouble("PAY_CASH",i), 2)));//退
    				//===zhangp 20120331 start
    				printParm.setData("BACKCHECKAMT", "TEXT", Math.abs(
    						StringTool.round(
    								//===zhangp 20120417 start
//    								tableParm.getDouble("PAY_CHECK",i)
    								tableParm.getDouble("PAY_CHECK",i) + tableParm.getDouble("PAY_BANK_CARD",i) + tableParm.getDouble("PAY_DEBIT",i)
    								//===zhangp 20120417 end
    								, 2)));//退
    				//===zhangp 20120331 end
    			}
//                            printParm.setData("ADDCHECKAMT", "TEXT", 1293.29);
//                            printParm.setData("BACKCHECKAMT", "TEXT",  0.00);
    	           //===zhangp 20120321 start
//                this.openPrintWindow("%ROOT%\\config\\prt\\IBS\\IBSRecp.jhw",
//                                     printParm,true);
    			//========liling start =============================
    			
//    			double money=0.00;
    			double billPay = StringTool.round(tableParm.getDouble("PAY_BILPAY",i), 2);//预交金总额
    			payWay=getPayWay(caseNo);
    			printParm.setData("PAY_WAY", payWay);//预交金额   现金？预交金？
    			printParm.setData("AR_AMT",formatObject.format(StringTool.getDouble(tableParm.getValue("OWN_AMT", i))));//自费金额是减免后需缴纳的金额
    			printParm.setData("TOTAL_AW", StringUtil.getInstance().numberToWord(Double.valueOf(tableParm.getValue("OWN_AMT", i))));
    			double minus=StringTool.getDouble(tableParm.getValue("OWN_AMT", i))-billPay;
    			if(minus>0){
    			printParm.setData("ADD", formatObject.format(minus));
    			}
    			if(minus<0){
    			printParm.setData("BACK",formatObject.format(-minus));}
    			//========liling end =============================
    			//tableParm.getDouble("PAY_BANK_CARD",i) + tableParm.getDouble("PAY_DEBIT",i)
    			
    			//20141223 wangjingchun add start 854
    			String patinfo_sql = "SELECT VALID_FLG, DEPT_FLG FROM MEM_INSURE_INFO WHERE MR_NO = '"+this.getValueString("MR_NO")+"' ";
    			TParm patinfo_parm = new TParm(TJDODBTool.getInstance().select(patinfo_sql));
    			String payDetail = "";
    			String sql3 = "SELECT PAY_CASH, PAY_REMK, PAY_MEDICAL_CARD, PAY_BANK_CARD, PAY_INS_CARD, " +
    						"PAY_CHECK, PAY_DEBIT, PAY_BILPAY, PAY_INS, PAY_OTHER1, PAY_OTHER2, PAY_GIFT_CARD, PAY_DISCNT_CARD, PAY_BXZF,PAY_TYPE09,PAY_TYPE10 " +
    						"FROM BIL_IBS_RECPM WHERE RECEIPT_NO = '"+receiptNo+"' AND CASE_NO = '"+caseNo+"'";
    			TParm parm3 = new TParm(TJDODBTool.getInstance().select(sql3)).getRow(0);
    			String sql4 = "SELECT MEMO1,MEMO2,MEMO3,MEMO4,MEMO5,6,MEMO7,MEMO8,MEMO9,MEMO10 FROM BIL_IBS_RECPM " +
							"WHERE RECEIPT_NO = '"+receiptNo+"' AND CASE_NO = '"+caseNo+"'";
    			TParm parm4 = new TParm(TJDODBTool.getInstance().select(sql4));
    			String memo2 = parm4.getValue("MEMO2",0);
    			String cardtypeString = "";
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
//    				 System.out.println("cardtypeString = = = = = =" +
//    				 cardtypeString);
    			}
//    			printParm.setData("CARD_TYPE", cardtypeString);
    			
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
    			if(parm3.getValue("PAY_CASH") != null && !"".equals(parm3.getValue("PAY_CASH")) && parm3.getDouble("PAY_CASH")!=0){
    				payDetail += ";"+map.get("PAY_CASH")+parm3.getValue("PAY_CASH")+"元";
    			}
    			if(parm3.getValue("PAY_MEDICAL_CARD") != null && !"".equals(parm3.getValue("PAY_MEDICAL_CARD")) && parm3.getDouble("PAY_MEDICAL_CARD")!=0){
    				payDetail += ";"+map.get("PAY_MEDICAL_CARD")+parm3.getValue("PAY_MEDICAL_CARD")+"元";
    			}
    			if(parm3.getValue("PAY_BANK_CARD") != null && !"".equals(parm3.getValue("PAY_BANK_CARD")) && parm3.getDouble("PAY_BANK_CARD")!=0){
    				payDetail += ";"+map.get("PAY_BANK_CARD")+parm3.getValue("PAY_BANK_CARD")+"元";			
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
    				payDetail += ";"+map.get("PAY_TYPE09")+parm3.getValue("PAY_TYPE09")+"元";
    			}
    			if(parm3.getValue("PAY_TYPE10") != null && !"".equals(parm3.getValue("PAY_TYPE10")) && parm3.getDouble("PAY_TYPE10")!=0){
    				payDetail += ";"+map.get("PAY_TYPE10")+parm3.getValue("PAY_TYPE10")+"元";
    			}
    			payDetail = payDetail.substring(1, payDetail.length());
    			
    			//增加显示套内结转和套外自付 xiongwg20150319 start
    			String space = ";";
    			String lumpWork="";
    			String admsql = "SELECT LUMPWORK_CODE FROM ADM_INP WHERE CASE_NO='" +
    					caseNo+"'";
    			TParm admParm = new TParm(TJDODBTool.getInstance().select(admsql));
    			String lumpworkCode = admParm.getValue("LUMPWORK_CODE",0);
    			if(!"".equals(lumpworkCode) && lumpworkCode.length()>0){
//        			String lumpsql="SELECT FEE FROM MEM_LUMPWORK WHERE LUMPWORK_CODE='"+
//        					lumpworkCode+"'";
//        			TParm lumpworkParm = new TParm(TJDODBTool.getInstance().select(lumpsql));
    				TParm lumpworkParm = new TParm();
//        			lumpWork += "套内结转:"+df.format(lumpworkParm.getDouble("FEE",0))+"元";//套内结转，套餐金额
        			String lumpoutsql="SELECT LUMPWORK_AMT,LUMPWORK_OUT_AMT,REDUCE_AMT FROM BIL_IBS_RECPM WHERE CASE_NO='" +
							caseNo+"' AND RECEIPT_NO='"+receiptNo+"' AND RESET_RECEIPT_NO IS NULL";//查询包干外的总金额
        			lumpworkParm = new TParm(TJDODBTool.getInstance().select(lumpoutsql));
        			lumpWork += "套内结转:"+df.format(lumpworkParm.getDouble("LUMPWORK_AMT",0))+"元";//套餐类型控件修改--xiongwg20150707
        			lumpWork +=";套外自付:"+df.format(lumpworkParm.getDouble("LUMPWORK_OUT_AMT",0)-
        					lumpworkParm.getDouble("REDUCE_AMT",0))+"元";
//            		System.out.println("lumpWork::::"+lumpWork);
    			}
//    			if("".equals(cardtypeString)){
//        			space = ";";
//        		}
    			
    			//增加显示套内结转和套外自付 xiongwg20150319 end
    			
    			printParm.setData("PAY_DETAIL", payDetail+space+cardtypeString+ lumpWork);
    			
    			int i1 = 1;
    			if(patinfo_parm.getValue("DEPT_FLG",0).equals("N")&&patinfo_parm.getValue("VALID_FLG",0).equals("Y")){
    				i1=2;
    			}
    			for(int j=0;j<i1;j++){
    				this.openPrintWindow(IReportTool.getInstance().getReportPath("IBSRecp.jhw"),
    						IReportTool.getInstance().getReportParm("IBSRecp.class", printParm),false);//报表合并modify by wanglong 20130730
    			}
    			//20141223 wangjingchun add end 854

            }

        }

    }
    /**
     * 获得预交金额
     * @param caseNo
     * @return
     * liling 20140820 add
     */
    private String getPayWay(String caseNo){
    	DecimalFormat formatObject = new DecimalFormat("###########0.00");
    	String payWay="";
        String sql3="SELECT RESET_RECP_NO,opt_date FROM bil_pay  where case_no='" +caseNo +"' order by opt_date desc";
//        System.out.println("sql3++"+sql3);
   		TParm parm3 = new TParm(TJDODBTool.getInstance().select(sql3));
   		if(parm3.getCount()>0){
   		String recpNo=parm3.getValue("RESET_RECP_NO",0);
   		if(recpNo !=null && recpNo.length()>0){
//   		System.out.println("parm3.getValue(RESET_RECP_NO)=="+recpNo);
   		String sql4="select receipt_no from bil_pay where case_no='" +caseNo +"' and RESET_RECP_NO='" +recpNo +"' and transact_type !='03'";
   		TParm parm4 = new TParm(TJDODBTool.getInstance().select(sql4));
//   		System.out.println("sql4++"+sql4);
   		String receiptNo1=parm4.getValue("RECEIPT_NO");//缴费作业时勾选的预交金的RECEIPT_NO
//   		System.out.println("receiptNo1=="+receiptNo1);
   		receiptNo1=receiptNo1.substring(1, receiptNo1.length()-1);
   		receiptNo1=receiptNo1.replaceAll(",", "','").replaceAll(" ", "");
//   		System.out.println("bilPayDate+++ "+receiptNo1);
   		
   		//控件找错group_id,GROUP_ID='SYS_PAYTYPE'是错的,正确的是GROUP_ID='GATHER_TYPE'--xiongwg20150409
   		String sql1 = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='GATHER_TYPE' ORDER BY ID ";
   		
   		String sql2 = "SELECT sum(pre_amt) as pre_amt, pay_type FROM bil_pay  where case_no='" +caseNo +"'  " +
			"AND (transact_type = '01' OR transact_type = '02' OR transact_type = '04'  OR transact_type = '03') and receipt_no in ('@')  group by pay_type ";      		
   		sql2 = sql2.replaceAll("@",receiptNo1 );
//   		System.out.println("sql2=="+sql2);
   		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
   		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));    			
		if(parm2.getCount()>0){//==liling 20140710 add 有无预交金判断
			for(int k=0;k<parm2.getCount();k++){
				String biltype = formatObject.format(parm2.getDouble("PRE_AMT", k));
				for(int j=0;j<parm1.getCount();j++){
				if(parm1.getValue("ID", j).equals(parm2.getValue("PAY_TYPE", k)))
					payWay +=parm1.getValue("CHN_DESC", j)+":"+biltype + "; ";
				//System.out.println("payWay==kpayWay="+k+"  j="+j+"  "+payWay);
				}		
			}
//			System.out.println("parm2.getCount()++"+parm2.getCount());
//			System.out.println("payWay++"+payWay);
			payWay = payWay.substring(0, payWay.length()-2);
		}
   		}}
    	return payWay;
    }
    /**
     * 得到科室说明
     * @param deptCode String
     * @return String
     */
    public String getDept(String deptCode) {
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            "SELECT DEPT_CODE,DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='" +
            deptCode + "'"));
        return parm.getValue("DEPT_CHN_DESC", 0);
    }

    /**
     * 得到姓名说明
     * @param code String
     * @return String
     */
    public String getDesc(String code) {
        TParm descParm = new TParm();
        descParm.setData(TJDODBTool.getInstance().select(
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
     * @param stationCode String
     * @return String
     */
    public String getStation(String stationCode) {
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            "SELECT STATION_CODE,STATION_DESC FROM SYS_STATION  WHERE STATION_CODE='" +
            stationCode + "'"));
        return parm.getValue("STATION_DESC", 0);
    }

    /**
     * 得到票据信息
     * @param receiptNo String
     * @return TParm
     */
    public TParm getPrintData(String receiptNo) {
        DecimalFormat formatObject = new DecimalFormat("###########0.00");
        String selRecpD =
            " SELECT REXP_CODE,WRT_OFF_AMT " +
            "   FROM BIL_IBS_RECPD " +
            "  WHERE RECEIPT_NO = " + receiptNo + " ";
        //查询不同支付方式付款金额(日结金额)
        TParm selRecpDParm = new TParm(TJDODBTool.getInstance().select(
            selRecpD));
        int recpDCount = selRecpDParm.getCount("REXP_CODE");
        TParm charge = new TParm();
        String rexpCode = "";
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
		//chargeParm.setData("CHARGE001",formatObject.format(charger03+charger04));//西药费数据
		charge.setData("CHARGE001",formatObject.format(charger03+charger04).equals("0.00") ? "" :formatObject.format(charger03+charger04));//西药费数据
		//System.out.println("chargeParm====》"+chargeParm);
		charge.setData("CHARGE001_D", "西药费");
		//====================20140609 liling  modify end  ===========================
//        double wrtOffAmt101 = 0.00;
//        double wrtOffAmt102 = 0.00;
//        double wrtOffAmt103 = 0.00;
//        double wrtOffAmt104 = 0.00;
//        double wrtOffAmt105 = 0.00;
//        double wrtOffAmt106 = 0.00;
//        double wrtOffAmt107 = 0.00;
//        double wrtOffAmt108 = 0.00;
//        double wrtOffAmt109 = 0.00;
//        double wrtOffAmt110 = 0.00;
//        double wrtOffAmt111 = 0.00;
//        double wrtOffAmt112 = 0.00;
//        double wrtOffAmt113 = 0.00;
//        double wrtOffAmt114 = 0.00;
//        double wrtOffAmt115 = 0.00;
//        double wrtOffAmt116 = 0.00;
//        double wrtOffAmt117 = 0.00;
//        double wrtOffAmt118 = 0.00;
//        double wrtOffAmt119 = 0.00;
//        //===zhangp 20120307 modify start
//        double wrtOffAmt120 = 0.00;
        //===zhangp 20120307 modify end
        double totAmt = 0.00;
//        double wrtOffSingle = 0.00;
//        TParm chargeParm = new TParm();
        for (int i = 0; i < recpDCount; i++) {
            rexpCode = selRecpDParm.getValue("REXP_CODE", i);
//            System.out.println("类别第"+i+"次=="+rexpCode);

            wrtOffSingle = selRecpDParm.getDouble("WRT_OFF_AMT", i);
//            System.out.println("wrtOffSingle单次第"+i+"次=="+wrtOffSingle);
            totAmt = totAmt + wrtOffSingle;}
          //========20120224 zhangp modify start
//			if ("020".equals(rexpCode)) { // 床位费
//				wrtOffAmt101 = wrtOffAmt101 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt101);
//				chargeParm.setData("CHARGE01", arAmtS);
//			} else if ("021".equals(rexpCode)) { // 诊查费
//				wrtOffAmt102 = wrtOffAmt102 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt102);
//				chargeParm.setData("CHARGE02", arAmtS);
//			} else if ("022.01".equals(rexpCode)||"022.02".equals(rexpCode)) { // 西药=抗生素+非抗生素
//				wrtOffAmt103 = wrtOffAmt103 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt103);
//				chargeParm.setData("CHARGE03", arAmtS);
////			} else if ("104".equals(rexpCode)) { // 检查费
////				wrtOffAmt104 = wrtOffAmt104 + wrtOffSingle;
////				String arAmtS = formatObject.format(wrtOffAmt104);
////				chargeParm.setData("CHARGE04", arAmtS);
//			} else if ("023".equals(rexpCode)) { // 中成药
//				wrtOffAmt105 = wrtOffAmt105 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt105);
//				chargeParm.setData("CHARGE05", arAmtS);
//			} else if ("024".equals(rexpCode)) { // 中草药
//				wrtOffAmt106 = wrtOffAmt106 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt106);
//				chargeParm.setData("CHARGE06", arAmtS);
//			} else if ("025".equals(rexpCode)) { // 检查费
//				wrtOffAmt107 = wrtOffAmt107 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt107);
//				chargeParm.setData("CHARGE07", arAmtS);
//			} else if ("026".equals(rexpCode)) { // 治疗费
//				wrtOffAmt108 = wrtOffAmt108 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt108);
//				chargeParm.setData("CHARGE08", arAmtS);
//			} else if ("027".equals(rexpCode)) { // 放射费
//				wrtOffAmt109 = wrtOffAmt109 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt109);
//				chargeParm.setData("CHARGE09", arAmtS);
//			} else if ("028".equals(rexpCode)) { // 手术费
//				wrtOffAmt110 = wrtOffAmt110 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt110);
//				chargeParm.setData("CHARGE10", arAmtS);
//			} else if ("029".equals(rexpCode)) { // 化验
//				wrtOffAmt111 = wrtOffAmt111 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt111);
//				chargeParm.setData("CHARGE11", arAmtS);
//			} else if ("02A".equals(rexpCode)) { // 输血费
//				wrtOffAmt112 = wrtOffAmt112 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt112);
//				chargeParm.setData("CHARGE12", arAmtS);
//			} else if ("02B".equals(rexpCode)) { // 氧气费
//				wrtOffAmt113 = wrtOffAmt113 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt113);
//				chargeParm.setData("CHARGE13", arAmtS);
//			} else if ("02C".equals(rexpCode)) { // 接生
//				wrtOffAmt114 = wrtOffAmt114 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt114);
//				chargeParm.setData("CHARGE14", arAmtS);
//			} else if ("02D".equals(rexpCode)) { // 护理
//				wrtOffAmt115 = wrtOffAmt115 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt115);
//				chargeParm.setData("CHARGE15", arAmtS);
//			} else if ("02E".equals(rexpCode)) { // 家床
//				wrtOffAmt116 = wrtOffAmt116 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt116);
//				chargeParm.setData("CHARGE16", arAmtS);
//			} else if ("032".equals(rexpCode)) { // CT
//				wrtOffAmt117 = wrtOffAmt117 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt117);
//				chargeParm.setData("CHARGE17", arAmtS);
//			} else if ("033".equals(rexpCode)) { // MR
//				wrtOffAmt118 = wrtOffAmt118 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt118);
//				chargeParm.setData("CHARGE18", arAmtS);
//			} else if ("02F".equals(rexpCode)) { // 自费
//				wrtOffAmt119 = wrtOffAmt119 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt119);
//				chargeParm.setData("CHARGE19", arAmtS);
//			}
//			//====zhangp 20120307 modify start
//			else if ("035".equals(rexpCode)) { // 自费
//				wrtOffAmt120 = wrtOffAmt120 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt120);
//				chargeParm.setData("CHARGE20", arAmtS);
//			}
//			//===zhangp 20120307 modify end
//		}
//		charge.setData("CHARGE01", "TEXT",
//				chargeParm.getData("CHARGE01") == null ? 0.00 : chargeParm
//						.getData("CHARGE01"));
//		charge.setData("CHARGE02", "TEXT",
//				chargeParm.getData("CHARGE02") == null ? 0.00 : chargeParm
//						.getData("CHARGE02"));
//		charge.setData("CHARGE03", "TEXT",
//				chargeParm.getData("CHARGE03") == null ? 0.00 : chargeParm
//						.getData("CHARGE03"));
////		charge.setData("CHARGE04", "TEXT",
////				chargeParm.getData("CHARGE04") == null ? 0.00 : chargeParm
////						.getData("CHARGE04"));
//		//======zhangp modify end 20120224
//        charge.setData("CHARGE05","TEXT",
//                       chargeParm.getData("CHARGE05") == null ? 0.00 :
//                       chargeParm.getData("CHARGE05"));
//        charge.setData("CHARGE06","TEXT",
//                       chargeParm.getData("CHARGE06") == null ? 0.00 :
//                       chargeParm.getData("CHARGE06"));
//        charge.setData("CHARGE07","TEXT",
//                       chargeParm.getData("CHARGE07") == null ? 0.00 :
//                       chargeParm.getData("CHARGE07"));
//        charge.setData("CHARGE08","TEXT",
//                       chargeParm.getData("CHARGE08") == null ? 0.00 :
//                       chargeParm.getData("CHARGE08"));
//        charge.setData("CHARGE09","TEXT",
//                       chargeParm.getData("CHARGE09") == null ? 0.00 :
//                       chargeParm.getData("CHARGE09"));
//        charge.setData("CHARGE10","TEXT",
//                       chargeParm.getData("CHARGE10") == null ? 0.00 :
//                       chargeParm.getData("CHARGE10"));
//        charge.setData("CHARGE11","TEXT",
//                       chargeParm.getData("CHARGE11") == null ? 0.00 :
//                       chargeParm.getData("CHARGE11"));
//        charge.setData("CHARGE12","TEXT",
//                       chargeParm.getData("CHARGE12") == null ? 0.00 :
//                       chargeParm.getData("CHARGE12"));
//        charge.setData("CHARGE13","TEXT",
//                       chargeParm.getData("CHARGE13") == null ? 0.00 :
//                       chargeParm.getData("CHARGE13"));
//        charge.setData("CHARGE14","TEXT",
//                       chargeParm.getData("CHARGE14") == null ? 0.00 :
//                       chargeParm.getData("CHARGE14"));
//        charge.setData("CHARGE15","TEXT",
//                       chargeParm.getData("CHARGE15") == null ? 0.00 :
//                       chargeParm.getData("CHARGE15"));
//        charge.setData("CHARGE16","TEXT",
//                       chargeParm.getData("CHARGE16") == null ? 0.00 :
//                       chargeParm.getData("CHARGE16"));
//        charge.setData("CHARGE17","TEXT",
//                       chargeParm.getData("CHARGE17") == null ? 0.00 :
//                       chargeParm.getData("CHARGE17"));
//        charge.setData("CHARGE18","TEXT",
//                       chargeParm.getData("CHARGE18") == null ? 0.00 :
//                       chargeParm.getData("CHARGE18"));
//        charge.setData("CHARGE19","TEXT",
//                       chargeParm.getData("CHARGE19") == null ? 0.00 :
//                       chargeParm.getData("CHARGE19"));
//        //===zhangp 20120307 modify start
//        charge.setData("CHARGE20","TEXT",
//        			chargeParm.getData("CHARGE20") == null ? 0.00 :
//        			chargeParm.getData("CHARGE20"));
        //===zhangp 20120307 modify end
        charge.setData("TOT_AMT","TEXT", formatObject.format(totAmt));
        charge.setData("AR_AMT","TEXT", formatObject.format(totAmt));
        return charge;
    }
    /**
     * 校验开关帐
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
        if (updateNo == null || updateNo.length() == 0) {
            return false;
        }
        return true;
    }
    /**
     * 获得医保数据
     * ====zhangp 20120412
     * @param caseNo String
     * @return TParm
     */
    public TParm getInsParm(String caseNo,String receiptNo){
    	String sql = "SELECT CONFIRM_NO FROM INS_ADM_CONFIRM WHERE CASE_NO = '"+caseNo+"'";
        TParm confirmParm = new TParm(TJDODBTool.getInstance().select(sql));
        if(confirmParm.getErrCode()<0){
        	messageBox("医保读取错误");
        	return confirmParm;
        }
        if(confirmParm.getCount()<0){
//        	messageBox("无资格确认书");//===yanjing 20140710 注
        	return confirmParm;
        }
    	TParm parm  = getBillmEndDate(receiptNo);
        String enddate = ""; 
        String appdate = "";
        String confirmNo = "";
        Timestamp yesterday = StringTool.rollDate(
				StringTool.getTimestamp(""+parm.getData("END_DATE",0), "yyyy-MM-dd HH:mm:ss"), -1);
        enddate  = StringTool.getString(yesterday, "yyyyMMdd") ;
        Timestamp sysDate = SystemTool.getInstance().getDate();
        appdate = StringTool.getString(sysDate,"yyyyMMdd");
        System.out.println("enddate:"+enddate+"  "+appdate);
        int count = confirmParm.getCount("CONFIRM_NO");
        //同一年，并且confirmNo记录为2的 取带KN的资格确认书
        if (enddate.substring(0, 4).equals(appdate.substring(0, 4)) &&
            count > 1) {
          confirmNo = getConfirmNo(confirmParm, "KN");
        }
        //取非KN的资格确认书
        else {
          confirmNo = getConfirmNo(confirmParm, "");
        }
        System.out.println("confirmNo:"+confirmNo);       
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
        		" + CASE" +
        		" WHEN B.REFUSE_TOTAL_AMT IS NULL" +
        		" THEN 0" +
        		" ELSE B.REFUSE_TOTAL_AMT" +
        		" END" +
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
        		" + CASE" +
        		" WHEN B.REFUSE_TOTAL_AMT IS NULL" +
        		" THEN 0" +
        		" ELSE B.REFUSE_TOTAL_AMT" +
        		" END" +
        		" ) AR_AMT," +
        		" (CASE" +
        		" WHEN B.ACCOUNT_PAY_AMT IS NULL" +
        		" THEN 0" +
        		" ELSE B.ACCOUNT_PAY_AMT" +
        		" END" +
        		" ) ACCOUNT_PAY_AMT" +
        		" FROM INS_ADM_CONFIRM A, INS_IBS B" +
        		" WHERE B.REGION_CODE = '"+Operator.getRegion()+"'" +
        		" AND A.CONFIRM_NO = '"+confirmNo+"'" +
        		" AND A.CONFIRM_NO = B.CONFIRM_NO " +
        		" AND B.CASE_NO = '"+caseNo+"'" +
        		" AND A.IN_STATUS IN ('1', '2', '3', '4')";
        TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));
        if(insParm.getErrCode()<0){
        	messageBox("医保读取错误");
        	return insParm;
        }
        if(insParm.getCount()<0){
        	messageBox("无医保数据");
        	return insParm;
        }
        TParm result = new TParm();
        result = insParm.getRow(0);
        result.setCount(1);
    	return result;

    }
    /**
     * 取得收据对应账单结束时间
     * @return
     */
    public TParm getBillmEndDate(String receiptNo)
    {
	  String sql = " SELECT  MAX(A.END_DATE) AS END_DATE FROM JAVAHIS.IBS_BILLM A" +
	  		       " WHERE  A.RECEIPT_NO  = '"+receiptNo+"'";
	//  System.out.println("sql:"+sql);
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql));   
	//  System.out.println("getBillmEndDate:"+result);
      if(result.getErrCode()<0){
      	messageBox("数据读取错误");
      	return result;
      }	  
      return result;	
    }
    /**
     * 取得资格确认书
     * @param caseNo
     * @return
     */
    public TParm getInsAdmConfirm(String caseNo)
    {
    	String sql = "SELECT CONFIRM_NO FROM INS_ADM_CONFIRM WHERE CASE_NO = '"+caseNo+"'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        System.out.println("getInsAdmConfirm:"+result);
        if(result.getErrCode()<0)
        {
          	messageBox("数据读取错误");
          	return result;
         }	         
        return result;
    }
    /**
     * 过滤资格确认书号
     * @param parm
     * @param type
     * @return
     */
    public String getConfirmNo(TParm parm, String type) {
        String knConfirmNo = "";
        String confirmNo = "";
        for (int i = 0; i < parm.getCount("CONFIRM_NO"); i++) {
          String s = ""+parm.getData("CONFIRM_NO", i);
          if (s.startsWith("KN")) {
            knConfirmNo = s;
          }
          else {
            confirmNo = s;
          }
        }

        if (type.equals("KN")) {
          return knConfirmNo;
        }
        else {
          return confirmNo;
        }
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
        this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
        this.onQuery();
        
    }
}
