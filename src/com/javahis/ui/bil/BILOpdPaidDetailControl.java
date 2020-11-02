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
import com.javahis.ui.testOpb.OPBChargePat;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: 门诊实收费用明细表</p>
 *
 * <p>Description: 门诊实收费用明细表</p>
 *
 * <p>Copyright: Copyright (c)bluecore 2016</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author pangben 2016/1/8
 * @version 1.0
 */
public class BILOpdPaidDetailControl extends TControl {
	private TTable table;
    public void onInit() {
        super.onInit(); 
        table=(TTable)this.getComponent("TABLE");
           
        String head="病案号,80;就诊号,90;姓名,60;性别,40;出生日期,80;就诊身份,140;客户来源,100,CUSTOMER_SOURCE;获知方式,80;就诊时间,80;结算时间,140;" +
		"挂号号别,100;挂号科室,80;挂号医生,70;接诊科室,80;接诊医生,70;主诊断,150;收据号,90;日结号,90;费用总金额,100;减免金额,80;" +
		"实收总金额,90;套餐结转金额,80;抗菌素,80;非抗菌素,80;中成药费,80;中草药费,80;常规检查,80;治疗费,80;放射费,80;" +
		"手术费,80;输血费,80;检验费,80;挂号费,80;诊疗费,80;床位费,80;CT费,80;B超费,80;核磁费,80;材料费,80;输氧费,80;" +
		"麻醉费,80;其他费,80;现金,80;刷卡,80;汇票,80;医院垫付,80;礼品卡,80;支票,80;现金折扣券,80;保险直付,80;医疗卡,80;微信支付,80;支付宝,80";
        String mapParm="MR_NO;CASE_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;CTZ_DESC;CUSTOMER_SOURCE;CHN_DESC;ADM_DATE;BILL_DATE;" +
		"CLINICTYPE_DESC;DEPT_CHN_DESC;USER_NAME;REAL_DEPT_CHN_DESC;REAL_USER_NAME;ICD_CODE;RECEIPT_NO;ACCOUNT_SEQ;TOT_AMT;" +
		"REDUCE_AMT;AR_AMT;MEM_AMT;CHARGE01;CHARGE02;CHARGE03;CHARGE04;CHARGE05;CHARGE06;CHARGE07;CHARGE08;" +
		"CHARGE09;CHARGE10;CHARGE11;CHARGE12;CHARGE13;CHARGE14;CHARGE15;CHARGE16;CHARGE17;CHARGE18;CHARGE19;" +
		"CHARGE20;PAY_TYPE01;PAY_TYPE02;PAY_TYPE03;PAY_TYPE04;PAY_TYPE05;PAY_TYPE06;PAY_TYPE07;PAY_TYPE08;PAY_MEDICAL_CARD;PAY_TYPE09;PAY_TYPE10";
        String send="0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;" +
        		"14,left;15,left;16,left;17,left;18,right;19,right;20,right;21,right;22,right;23,right;24,right;25,right;26,right;" +
        		"27,right;28,right;29,right;30,right;31,right;32,right;33,right;34,right;35,right;36,right;37,right;38,right;" +
        		"39,right;40,right;41,right;42,right;43,right;44,right;45,right;46,right;47,right;48,right;49,right;50,right;51,right;52,right;53,right;54,right";
    	String item = "CUSTOMER_SOURCE";
    	table.setItem(item);
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
        this.clearValue("MR_NO;RECEIPT_NO;ACCOUNT_SEQ;DEPT_CODE;DR_CODE;REALDEPT_CODE;REALDR_CODE;POST_CODE;SOURCE");
    }
    public void onQuery(){ 
    	String where=" WHERE 1=1 "; 
    	String [] array={"MR_NO","RECEIPT_NO","ACCOUNT_SEQ","DEPT_CODE","DR_CODE","REALDEPT_CODE","REALDR_CODE"};
    	for (int i = 0; i < array.length; i++) {
    		if (this.getValue(array[i]).toString().length()>0) {
        		where+=" AND "+array[i]+"='"+this.getValue(array[i]).toString()+"'";
    		}
		}
    	if(this.getValueString("POST_CODE").length() > 0){
    		where += "AND POST_CODE='"+this.getValueString("POST_CODE")+"'";
    	}
    	if(this.getValueString("SOURCE").length() > 0){
    		where += "AND SOURCE='"+this.getValueString("SOURCE")+"'";
    	}
    	
    	
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMddHHmmss");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMddHHmmss");
        String sql="SELECT   mr_no, case_no, pat_name,CASE  WHEN sex_code = '1' THEN '男' " +
        		" WHEN sex_code = '2' THEN '女' ELSE '未知' END sex_code," +
        		"birth_date,CTZ_DESC,customer_source,chn_desc,contacts_name,contacts_tel, post_description, current_address, adm_date," +
        		" bill_date, bill_date1, clinictype_desc, dept_chn_desc, user_name," +
        		" real_dept_chn_desc, real_user_name, icd_code || icd_chn_desc || CASE" +
        		" WHEN diag_note IS NULL THEN '' ELSE '(' || diag_note || ')'" +
        		" END icd_code,receipt_no, account_seq, " +
        		"SUM (tot_amt) tot_amt, SUM (reduce_amt) reduce_amt," +
        		" SUM (tot_amt) - SUM (reduce_amt) ar_amt, CASE" +
        		" WHEN mem_pack_flg = 'Y' THEN SUM (tot_amt) ELSE 0" +
        		"  END mem_amt, SUM (charge01) charge01, SUM (charge02) charge02," +
        		"SUM (charge03) charge03, SUM (charge04) charge04," +
        		"SUM (charge05) charge05, SUM (charge06) charge06," +
        		"SUM (charge07) charge07, SUM (charge08) charge08," +
        		" SUM (charge09) charge09, SUM (charge10) charge10," +
        		" SUM (charge11) charge11, SUM (charge12) charge12," +
        		" SUM (charge13) charge13, SUM (charge14) charge14," +
        		"SUM (charge15) charge15, SUM (charge16) charge16," +
        		"SUM (charge17) charge17, SUM (charge18) charge18," +
        		" SUM (charge19) charge19, SUM (charge20) charge20," +
        		"SUM (pay_type01) pay_type01, SUM (pay_type02) pay_type02," +
        		"SUM (pay_type03) pay_type03, SUM (pay_type04) pay_type04," +
        		" SUM (pay_type05) pay_type05, SUM (pay_type06) pay_type06," +
        		"SUM (pay_type07) pay_type07, SUM (pay_type08) pay_type08, dept_code," +
        		" realdept_code, realdr_code, dr_code, post_code,SUM (pay_type09) pay_type09," +
        		"SUM (pay_type10) pay_type10" +
        		" ,SUM (pay_type11) pay_type11,SUM (pay_medical_card) pay_medical_card " + //add by huangtt 20171225 添加医疗卡支付方式显示
        		" FROM (SELECT TO_CHAR (bill_date, 'YYYY/MM/DD HH24:MI:SS') bill_date," +
        		" TO_CHAR (bill_date, 'YYYY/MM/DD') bill_date1,Q.CTZ_DESC, a.adm_date," +
        		"b.receipt_no, d.pat_name,d.contacts_name,d.contacts_tel, e.user_name, b.charge01," +
        		" b.charge02, b.charge03, b.charge04, b.charge05, b.charge06," +
        		" b.charge07, b.charge08, b.charge09, b.charge10, b.charge11," +
        		" b.charge12, b.charge13, b.charge14, b.charge15, b.charge16," +
        		" b.charge17, b.charge18, b.charge19, b.charge20, b.charge21," +
        		" b.charge22, b.charge23, b.charge24, b.charge25, b.charge26," +
        		" b.charge27, b.charge28, b.charge29, b.charge30, b.tot_amt," +
        		" b.ar_amt, c.dept_chn_desc, b.account_seq, b.reduce_amt," +
        		" a.mr_no, b.mem_pack_flg, f.icd_code, f.icd_chn_desc," +
        		"f.diag_note, p.post_description, d.current_address," +
        		"d.sex_code, d.birth_date, a.case_no," +
        		" g.dept_chn_desc AS real_dept_chn_desc," +
        		"f.user_name AS real_user_name, b.pay_type01, b.pay_type02," +
        		" b.pay_type03, b.pay_type04, b.pay_type05, b.pay_type06," +
        		" b.pay_type07, b.pay_type08, b.pay_type09, b.pay_type10," +
        		" b.pay_type11, b.pay_medical_card," + //add by huangtt 20171225 添加医疗卡支付方式显示
        		" ms.chn_desc, a.dept_code, a.realdept_code,a.realdr_code," +
        		"hb.clinictype_desc, a.dr_code, d.post_code, ms.source,ms.customer_source " +
        		" FROM SYS_CTZ Q, bil_opb_recp b," +
        		"reg_patadm a, sys_dept c, sys_dept g, sys_operator h," +
        		" sys_patinfo d, sys_operator e,sys_postcode p," +
        		"(SELECT   a.case_no, a.icd_code, b.icd_chn_desc, " +
        		"a.diag_note, d.user_name FROM opd_diagrec a," +
        		"sys_diagnosis b, bil_opb_recp c, sys_operator d" +
        		" WHERE a.icd_code = b.icd_code" +
        		" AND a.main_diag_flg = 'Y' AND a.case_no = c.case_no " +
        		"AND a.dr_code = d.user_id" +
        		" AND c.account_date BETWEEN" +
        		" TO_DATE ('"+startTime+"','YYYYMMDDHH24MISS')" +
        		"  AND TO_DATE ('"+endTime+"','YYYYMMDDHH24MISS')" +
        		" GROUP BY a.case_no, a.icd_code,b.icd_chn_desc,a.diag_note," +
        		"d.user_name) f," +
        		" (SELECT m.mr_no, t.chn_desc, m.source,m.customer_source" +
        		" FROM mem_patinfo m, sys_dictionary t" +
        		" WHERE m.source = t.id(+) AND t.group_id = 'MEM_SOURCE') ms," +
        		"(SELECT b.case_no, b.adm_type, a.clinictype_code," +
        		"  a.clinictype_desc" +
        		" FROM reg_clinictype a, reg_patadm b" +
        		" WHERE a.adm_type = b.adm_type" +
        		" AND a.clinictype_code = b.clinictype_code) hb" +
        		" WHERE Q.CTZ_CODE=A.CTZ1_CODE " +
        		" AND hb.case_no = a.case_no AND a.case_no = b.case_no" +
        		" AND a.mr_no = ms.mr_no(+) AND a.dept_code = c.dept_code(+)" +
        		" AND a.realdept_code = g.dept_code(+)" +
        		" AND a.realdr_code = h.user_id(+) AND a.mr_no = d.mr_no" +
        		" AND a.dr_code = e.user_id(+) AND a.case_no = f.case_no(+)" +
        		" AND a.regcan_user IS NULL AND a.case_no = f.case_no(+)" +
        		" AND d.post_code = p.post_code(+)" +
        		" AND b.account_date BETWEEN" +
        		" TO_DATE ('"+startTime+"','YYYYMMDDHH24MISS')" +
        		" AND TO_DATE ('"+endTime+"','YYYYMMDDHH24MISS'))" +
        		where +
        		"GROUP BY bill_date, dept_chn_desc, pat_name," +
        		" account_seq,user_name,receipt_no, adm_date," +
        		" mr_no, mem_pack_flg,icd_code, icd_chn_desc," +
        		" diag_note,current_address, sex_code, birth_date," +
        		"case_no, bill_date1, real_dept_chn_desc, real_user_name," +
        		" chn_desc, dept_code,realdept_code, realdr_code," +
        		" dr_code, post_description, post_code, clinictype_desc, CTZ_DESC,contacts_name,contacts_tel,customer_source" +
        		" ORDER BY bill_date, dept_chn_desc ";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
        System.out.println("sql:sss:::"+sql);
    	TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
    	if (parm.getErrCode()<0) {  
			this.messageBox("查询出现问题"); 
			return;
		}
    	String sumValue="TOT_AMT;" +
		"REDUCE_AMT;AR_AMT;MEM_AMT;CHARGE01;CHARGE02;CHARGE03;CHARGE04;CHARGE05;CHARGE06;CHARGE07;CHARGE08;" +
		"CHARGE09;CHARGE10;CHARGE11;CHARGE12;CHARGE13;CHARGE14;CHARGE15;CHARGE16;CHARGE17;CHARGE18;CHARGE19;" +
		"CHARGE20;PAY_TYPE01;PAY_TYPE02;PAY_TYPE03;PAY_TYPE04;PAY_TYPE05;PAY_TYPE06;PAY_TYPE07;PAY_TYPE08;PAY_MEDICAL_CARD;PAY_TYPE09;PAY_TYPE10";
    	String [] arrayValue=sumValue.split(";");
    	
    	if (parm.getCount()<=0) {
			this.messageBox("没有需要查询的数据");
			table.setParmValue(new TParm());
			return;
		}
    	DecimalFormat df = new DecimalFormat("##########0.00");
    	double[]sumDouble=new double[arrayValue.length];
    	for (int i = 0; i < parm.getCount(); i++) {
    		//==start==add by kangy 20170222
			String caseNo = parm.getValue("CASE_NO", i); 
			TParm reDate = this.getReceiveDrDept(caseNo);
			parm.setData("REAL_DEPT_CHN_DESC",i, reDate.getData("REAL_DEPT_CHN_DESC"));
			parm.setData("REAL_USER_NAME",i, reDate.getData("REAL_USER_NAME"));
			//==end==add by kangy 20170222
			
			parm.setData("PAY_MEDICAL_CARD",i,parm.getDouble("PAY_MEDICAL_CARD", i)+parm.getDouble("PAY_TYPE11", i));
			
			for (int j = 0; j < arrayValue.length; j++) {
				parm.setData(arrayValue[j], i, df.format(parm.getDouble(arrayValue[j],i)));
				sumDouble[j]+=StringTool.round(parm.getDouble(arrayValue[j],i),2);
			}
		}
    	String stringValue="MR_NO;CASE_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;CHN_DESC;CONTACTS_NAME;CONTACTS_TEL;POST_DESCRIPTION;CURRENT_ADDRESS;ADM_DATE;BILL_DATE;" +
		"CLINICTYPE_DESC;DEPT_CHN_DESC;USER_NAME;REAL_DEPT_CHN_DESC;REAL_USER_NAME;ICD_CODE;RECEIPT_NO;ACCOUNT_SEQ";
    	String [] stringArray=stringValue.split(";");
    	for (int i = 0; i < stringArray.length; i++) {
    		if (stringArray[i].equals("ACCOUNT_SEQ")) {
    			parm.addData(stringArray[i], "合计：");
			}else{
				parm.addData(stringArray[i], "");
			}
		}
    	for (int i = 0; i < sumDouble.length; i++) {
    		parm.addData(arrayValue[i], df.format(StringTool.round(sumDouble[i],2)) );
		}
    	parm.setCount(parm.getCount("ACCOUNT_SEQ"));
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
         parm.setData("TITLE", "TEXT","门急诊实收入明细报表");
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
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "门急诊实收入明细报表");
    }
    
    /**
	 * 得到接诊医生科室数据
	 * @param caseNo
	 * @return
	 */
	public TParm getReceiveDrDept(String caseNo){  //add by kangy 20170222
		String sql = "  SELECT C.USER_NAME DR_CODE, B.DEPT_CHN_DESC DEPT_CODE" +
				" FROM OPD_ORDER A, SYS_DEPT B, SYS_OPERATOR C" +
				" WHERE CASE_NO = '"+caseNo+"'" +
				" AND A.DR_CODE = C.USER_ID" +
				" AND A.DEPT_CODE = B.DEPT_CODE" +
				" ORDER BY A.RX_TYPE ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		TParm result = new TParm();
		if(parm.getCount() > 0){
			result.setData("REAL_DEPT_CHN_DESC", parm.getValue("DEPT_CODE", 0));
			result.setData("REAL_USER_NAME", parm.getValue("DR_CODE", 0));
		}else{
			result.setData("DR_CODE", "");
			result.setData("DEPT_CODE", "");
		}
		return result;
		
	}
}
