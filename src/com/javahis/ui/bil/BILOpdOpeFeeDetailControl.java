package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: 门诊手术费用明细表</p>
 *
 * <p>Description: 门诊手术费用明细表</p>
 *
 * <p>Copyright: Copyright (c)bluecore 2016</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author pangben 2016/5/10
 * @version 1.0
 */
public class BILOpdOpeFeeDetailControl extends TControl{
	private TTable table;
    public void onInit() {
        super.onInit();
        table=(TTable)this.getComponent("TABLE");
           
        String head="病案号,80;姓名,60;性别,40;出生日期,80;就诊时间,80;结算时间,140;" +
		"挂号科室,100;挂号医生,70;接诊科室,100;接诊医生,70;费用总金额,100;" +
		"实收总金额,90;减免金额,80;套餐结转金额,80;点名费,80;手术费,80;输血费,80;手术时间,80";
        String mapParm="MR_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;ADM_DATE;BILL_DATE;" +
		"DEPT_CHN_DESC;USER_NAME;REAL_DEPT_CHN_DESC;REAL_USER_NAME;TOT_AMT;" +
		"AR_AMT;REDUCE_AMT;MEM_AMT;AMT;CHARGE08;CHARGE09;OPT_CHN_DESC";
        String send="0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,right;11,right;12,right;13,right;" +
		"14,right;15,right;16,right;17,left";
    	
    	table.setHeader(head);
    	table.setParmMap(mapParm);
    	table.setColumnHorizontalAlignmentData(send);
        initPage();
    }
    public void onMrno(){
    	Pat pat = Pat.onQueryByMrNo(PatTool.getInstance().checkMrno(getValueString("MR_NO")));
		if (pat == null) {
			messageBox_("查无此病案号");
			this.setValue("MR_NO", "");
		}
		this.setValue("MR_NO", pat.getMrNo());
    }
    /**
     * 初始化界面
     */
    public void initPage() {
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(),-30);
        String startTime = StringTool.getString(yesterday, "yyyy/MM/dd");
        setValue("START_DATE", startTime+" 00:00:00");
        setValue("END_DATE",  StringTool.getString(SystemTool.getInstance().
                getDate(), "yyyy/MM/dd")+" 23:59:59");
//        this.callFunction("UI|TABLE|removeRowAll");
        table.setParmValue(new TParm());
        this.clearValue("MR_NO;DEPT_CODE;DR_CODE;REALDEPT_CODE;REALDR_CODE");
    }
    public void onQuery(){
    	String where="";
    	String [] array={"MR_NO","DEPT_CODE","DR_CODE","REALDEPT_CODE","REALDR_CODE"};
    	for (int i = 0; i < array.length; i++) {
    		if (this.getValue(array[i]).toString().length()>0) {
        		where+=" AND A."+array[i]+"='"+this.getValue(array[i]).toString()+"'";
    		}
		}
    	
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMddHHmmss");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMddHHmmss");
        String sql="SELECT MR_NO , PAT_NAME ,CASE "+
         "WHEN SEX_CODE = '1' THEN '男' WHEN SEX_CODE = '2' THEN '女' ELSE '未知'END SEX_CODE,"+
         "BIRTH_DATE , ADM_DATE , BILL_DATE ,DEPT_CHN_DESC ,USER_NAME ,REAL_DEPT_CHN_DESC ,"+
       "REAL_USER_NAME ,SUM(TOT_AMT) TOT_AMT,SUM(TOT_AMT) - SUM(REDUCE_AMT) AR_AMT,SUM(REDUCE_AMT) REDUCE_AMT,"+
       "CASE WHEN MEM_PACK_FLG = 'Y' THEN SUM(TOT_AMT) ELSE 0 END MEM_AMT,SUM(AMT) AMT,SUM(CHARGE08) CHARGE08,"+
       "SUM(CHARGE09) CHARGE09,OPT_CHN_DESC,CASE_NO "+
       "FROM (SELECT TO_CHAR(BILL_DATE, 'YYYY/MM/DD HH:MM:SS') BILL_DATE,"+
               "TO_CHAR(BILL_DATE, 'YYYY/MM/DD') BILL_DATE1,A.ADM_DATE,B.RECEIPT_NO,D.PAT_NAME,E.USER_NAME,"+
               "B.CHARGE08,B.CHARGE09,B.TOT_AMT,B.AR_AMT,C.DEPT_CHN_DESC,B.ACCOUNT_SEQ,B.REDUCE_AMT,"+
               "A.MR_NO,A.CASE_NO,B.MEM_PACK_FLG,P.POST_DESCRIPTION,D.CURRENT_ADDRESS,D.SEX_CODE,D.BIRTH_DATE,"+
               "G.DEPT_CHN_DESC AS REAL_DEPT_CHN_DESC,H.USER_NAME AS REAL_USER_NAME,NVL(DM.AMT, 0) AMT,P.OPT_CHN_DESC "+
          "FROM BIL_OPB_RECP B,REG_PATADM A,SYS_DEPT C,SYS_DEPT G,SYS_OPERATOR H,SYS_PATINFO D,SYS_OPERATOR E,"+
               "SYS_POSTCODE P,(SELECT D.CASE_NO, SUM(D.AR_AMT) AMT FROM OPD_ORDER D "+
                 "WHERE D.ORDER_DESC LIKE '%特聘%' GROUP BY D.CASE_NO) DM,(SELECT A.CASE_NO, "+//现场需求   将点名改为特聘  machao
                       "LISTAGG(TO_CHAR(OP.OP_DATE, 'YYYY/MM/DD'), ',') WITHIN GROUP(ORDER BY A.CASE_NO) OPT_CHN_DESC "+
                  "FROM REG_PATADM A, OPE_OPBOOK OP WHERE A.CASE_NO = OP.CASE_NO AND OP.STATE > 0 GROUP BY A.CASE_NO) P "+
        " WHERE A.CASE_NO = B.CASE_NO "+
          " AND A.CASE_NO = P.CASE_NO(+) "+
          " AND A.CASE_NO = DM.CASE_NO(+) "+
          " AND A.DEPT_CODE = C.DEPT_CODE(+) "+
          " AND A.REALDEPT_CODE = G.DEPT_CODE(+) "+
          " AND A.REALDR_CODE = H.USER_ID(+) "+
          " AND A.MR_NO = D.MR_NO "+
          " AND A.DR_CODE = E.USER_ID(+) "+
          " AND A.REGCAN_USER IS NULL "+
          " AND D.POST_CODE = P.POST_CODE(+) "+
          " AND B.ACCOUNT_DATE BETWEEN "+
              " TO_DATE('"+startTime+"', 'YYYYMMDDHH24MISS') AND "+
              " TO_DATE('"+endTime+"', 'YYYYMMDDHH24MISS') "+where+") "+
 " GROUP BY BILL_DATE,DEPT_CHN_DESC, PAT_NAME,ACCOUNT_SEQ,USER_NAME,RECEIPT_NO,ADM_DATE,MR_NO,MEM_PACK_FLG,OPT_CHN_DESC, "+
         " CURRENT_ADDRESS,SEX_CODE,BIRTH_DATE,CASE_NO,BILL_DATE1,REAL_DEPT_CHN_DESC,REAL_USER_NAME,POST_DESCRIPTION "+
 " ORDER BY BILL_DATE, DEPT_CHN_DESC ";
        System.out.println("sql:sss:::"+sql);
    	TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
    	if (parm.getErrCode()<0) {
			this.messageBox("查询出现问题");
			return;
		}
    	String sumValue="TOT_AMT;AR_AMT;REDUCE_AMT;MEM_AMT;AMT;CHARGE08;CHARGE09";
    	String [] arrayValue=sumValue.split(";");
    	
    	if (parm.getCount()<=0) {
			this.messageBox("没有需要查询的数据");
			table.setParmValue(new TParm());
			return;
		}
    	DecimalFormat df = new DecimalFormat("##########0.00");
    	double[]sumDouble=new double[arrayValue.length];
    	for (int i = 0; i < parm.getCount(); i++) {
			for (int j = 0; j < arrayValue.length; j++) {
				parm.setData(arrayValue[j], i, df.format(parm.getDouble(arrayValue[j],i)));
				sumDouble[j]+=StringTool.round(parm.getDouble(arrayValue[j],i),2);
			}
		}
    	String stringValue="MR_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;ADM_DATE;BILL_DATE;" +
		"DEPT_CHN_DESC;USER_NAME;REAL_DEPT_CHN_DESC;REAL_USER_NAME";
    	String [] stringArray=stringValue.split(";");
    	for (int i = 0; i < stringArray.length; i++) {
    		if (stringArray[i].equals("MR_NO")) {
    			parm.addData(stringArray[i], "合计：");
			}else{
				parm.addData(stringArray[i], "");
			}
		}
    	for (int i = 0; i < sumDouble.length; i++) {
    		parm.addData(arrayValue[i], df.format(StringTool.round(sumDouble[i],2)) );
		}
    	parm.addData("CASE_NO", "");
    	parm.setCount(parm.getCount("MR_NO"));
    	table.setParmValue(parm);
    }
    public void onClear(){
    	initPage();
    }
    public void onPrint(){
    	 int row = table.getRowCount();
         if (row < 1) {
             this.messageBox_("先查询数据!");
             return;
         }
         String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                 "START_DATE")), "yyyy/MM/dd HH:mm:ss");
         String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                 "END_DATE")), "yyyy/MM/dd HH:mm:ss");
         String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                               "yyyy/MM/dd HH:mm:ss");
         TParm parm = new TParm();
       //标题
         parm.setData("TITLE", "TEXT","门诊手术费用报表");
         parm.setData("PRINT_DATE","TEXT",sysDate);
         parm.setData("DATE", "TEXT", startTime + " 至 " + endTime);
 		//parm.setData("E_DATE", eDate);
 		parm.setData("OPT_USER", "TEXT", Operator.getName());
 		parm.setData("printDate", "TEXT", sysDate);
 		parm.setData("T1", table.getParmValue().getData());
 		parm.setData("TOT_AMT", "TEXT", "合计:  "
 				+ table.getShowParmValue().getValue("TOT_AMT",
 						table.getRowCount() - 1));// ===zhangp 20120824
 		// System.out.println("在院病患医疗费用明细表" + printData.getData());
 		this.openPrintWindow(
 				"%ROOT%\\config\\prt\\BIL\\BILOpdPaidDetail.jhw", parm);
    }
    /**
     * 汇出Excel
     */
    public void onExcel() {

        //得到UI对应控件对象的方法（UI|XXTag|getThis）
        //TTable table = (TTable) callFunction("UI|TABLE|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "门诊手术费用报表");
    }
}
