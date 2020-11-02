package com.javahis.ui.bil;

import java.sql.Timestamp;

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
 * <p>
 * Title: 门诊应收费用明细表
 * </p>
 * 
 * <p>
 * Description: 门诊应收费用明细表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)bluecore 2016
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 2016/5/16
 * @version 4.0
 */
public class BILAccountCostControl extends TControl {
	private TTable table;
	private String CHARGE =
        "CHARGE01,CHARGE02,CHARGE03,CHARGE04,CHARGE05,CHARGE06,CHARGE07," +
        "CHARGE08,CHARGE09,CHARGE10,CHARGE11,CHARGE12,CHARGE13,CHARGE14," +
        //===zhangp 20120310 modify start
        "CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20";
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
		onPage();
	}

	public void onPage() {
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -30);
		String startTime = StringTool.getString(yesterday, "yyyy/MM/dd");
		setValue("START_DATE", startTime + " 00:00:00");
		setValue("END_DATE", StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd")
				+ " 23:59:59");
		// this.callFunction("UI|TABLE|removeRowAll");
		table.setParmValue(new TParm());
		this.clearValue("MR_NO;DEPT_CODE;DR_CODE;REALDEPT_CODE;REALDR_CODE");
	}
	public void onClear(){
		 onPage();
	}
	public void onMrno() {
		Pat pat = Pat.onQueryByMrNo(PatTool.getInstance().checkMrno(
				getValueString("MR_NO")));
		if (pat == null) {
			messageBox_("查无此病案号");
			this.setValue("MR_NO", "");
		}
		this.setValue("MR_NO", pat.getMrNo());
	}

	public void onQuery() {
		//String where = "";
		String[] array = { "MR_NO", "DEPT_CODE", "DR_CODE", "REALDEPT_CODE"};
		String whereOne="";
		for (int i = 0; i < array.length; i++) {
			if (this.getValue(array[i]).toString().length() > 0) {
				whereOne += " AND B." + array[i] + "='"
				+ this.getValue(array[i]).toString() + "'";
			}
		}
		if (this.getValue("REALDR_CODE").toString().length() > 0) {
			whereOne+= " AND H.DR_CODE='"+ this.getValue("REALDR_CODE").toString() + "'";
		}
		String start = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmmss");
		String end = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss");
		String sql1 = "SELECT B.case_no, a.rexp_code, B.mr_no, "
				+ "a.pat_name, B.dept_code,B.dr_code, a.tot_amt, TO_CHAR(b.reg_date,'YYYY/MM/DD HH:mm:ss') reg_date,"
				+ " b.realdept_code, H.DR_CODE realdr_code,b.ctz1_code,A.RECEIPT_NO "
				+ " FROM reg_patadm b,(SELECT H.DR_CODE,H.CASE_NO FROM OPD_DIAGREC H,BIL_OPB_RECP B WHERE H.CASE_NO=B.CASE_NO  AND H.MAIN_DIAG_FLG = 'Y' AND B.ACCOUNT_DATE "+
                 " BETWEEN TO_DATE ('"+start+"','YYYYMMDDHH24MISS' ) AND TO_DATE ('"+end+"', 'YYYYMMDDHH24MISS')  GROUP BY H.DR_CODE, H.CASE_NO ) H ,(SELECT   a.case_no, a.mr_no, d.pat_name,"
				+ " SUM (c.ar_amt) AS tot_amt, c.rexp_code,Q.RECEIPT_NO "
				+ "FROM reg_patadm a,sys_patinfo d, opd_order c, bil_opb_recp q "
				+ "WHERE a.case_no = c.case_no"
				+ " AND a.mr_no = d.mr_no AND c.case_no = q.case_no(+)"
				+ " AND c.receipt_no = q.receipt_no(+) "
				+ "AND c.ar_amt <> 0 AND a.regcan_user IS NULL "
				+ "AND c.receipt_no IS NOT NULL"
				+ " AND q.account_date BETWEEN TO_DATE ('"
				+ start
				+ "','YYYYMMDDHH24MISS')"
				+ " AND TO_DATE ('"
				+ end
				+ "','YYYYMMDDHH24MISS')"
				+ " AND c.receipt_no IS NOT NULL "
				+ " GROUP BY c.rexp_code, a.case_no,"
				+ " a.mr_no,d.pat_name,Q.RECEIPT_NO "
				+ "UNION ALL "
				+ "SELECT   a.case_no, a.mr_no, d.pat_name, "
				+ "-SUM (c.ar_amt) AS tot_amt, c.rexp_code,Q.RECEIPT_NO "
				+ " FROM reg_patadm a, sys_patinfo d,"
				+ " sys_operator h,opd_order_history c,"
				+ " (SELECT a.receipt_no, a.case_no, a.mr_no,"
				+ " b.account_seq,A.RESET_RECEIPT_NO, "
				+ "b.cashier_code FROM bil_opb_recp a, (SELECT receipt_no,"
				+ " account_seq,"
				+ "cashier_code "
				+ "FROM bil_opb_recp WHERE account_date"
				+ " BETWEEN TO_DATE ('"
				+ start
				+ "','YYYYMMDDHH24MISS' )"
				+ " AND TO_DATE ('"
				+ end
				+ "','YYYYMMDDHH24MISS' )"
				+ " "
				+ " AND tot_amt < 0) b"
				+ " WHERE a.reset_receipt_no = b.receipt_no"
				+ " AND a.reset_receipt_no IS NOT NULL"
				+ " ) q"
				+ " WHERE a.case_no = c.case_no"
				+ " AND a.mr_no = d.mr_no"
				+ " AND c.case_no = q.case_no(+) "
				+ "AND c.receipt_no = q.receipt_no(+) "
				+ " AND c.ar_amt <> 0 "
				+ " AND a.dr_code = h.user_id "
				+ "AND a.regcan_user IS NULL "
				+ "AND c.receipt_no IS NOT NULL"
				+ " AND c.receipt_no = q.receipt_no"
				+ " GROUP BY c.rexp_code, "
				+ "a.case_no,a.mr_no,d.pat_name,Q.RECEIPT_NO,Q.RESET_RECEIPT_NO"
				+ " UNION ALL"
				+ " SELECT   a.case_no, a.mr_no, d.pat_name,"
				+ " SUM (c.ar_amt) AS tot_amt, c.rexp_code,Q.reset_receipt_no RECEIPT_NO "
				+ "  FROM reg_patadm a, sys_patinfo d,"
				+ "opd_order_history c,"
				+ " (SELECT a.receipt_no, a.case_no, a.mr_no,"
				+ "  b.account_seq,"
				+ " b.cashier_code,B.reset_receipt_no "
				+ " FROM bil_opb_recp a,"
				+ "(SELECT receipt_no, account_seq,"
				+ " cashier_code,reset_receipt_no "
				+ " FROM bil_opb_recp"
				+ " WHERE account_date"
				+ " BETWEEN TO_DATE ('"
				+ start
				+ "','YYYYMMDDHH24MISS')"
				+ " AND TO_DATE ('"
				+ end
				+ "','YYYYMMDDHH24MISS' ) "
				+ " AND reset_receipt_no IS NOT NULL) b"
				+ " WHERE a.receipt_no = b.receipt_no "
				+ "AND a.reset_receipt_no IS NOT NULL "
				+ ") q "
				+ "WHERE a.case_no = c.case_no "
				+ "AND a.mr_no = d.mr_no "
				+ "AND c.case_no = q.case_no(+)  "
				+ " AND c.ar_amt <> 0  "
				+ "AND a.regcan_user IS NULL "
				+ "AND c.receipt_no IS NOT NULL "
				+ "AND c.receipt_no = q.receipt_no"
				+ " GROUP BY c.rexp_code, "
				+ "a.case_no, a.mr_no,d.pat_name,Q.reset_receipt_no) a"
				+ " WHERE a.tot_amt <> 0 AND a.case_no = b.case_no AND A.CASE_NO=H.CASE_NO(+) " +whereOne;
		//System.out.println("sql1222:::"+sql1);
//		String sql2 = "SELECT A.* FROM "+
//       "(SELECT  S.REAL_AMT,SUM (Q.REDUCE_AMT) AS REDUCE_AMT,SUM(Q.TOT_AMT) AR_AMT, "+
//               "CASE WHEN Q.MEM_PACK_FLG = 'Y' "+
//             "THEN SUM (Q.TOT_AMT) ELSE 0 END AS TAOCAN,Q.RECEIPT_NO,Q.CASE_NO FROM ( SELECT  SUM (C.AR_AMT) AS REAL_AMT ,A.CASE_NO,C.RECEIPT_NO "+     
//            "FROM REG_PATADM A, SYS_PATINFO D, OPD_ORDER C, BIL_OPB_RECP Q "+
//           "WHERE A.CASE_NO = C.CASE_NO AND A.MR_NO = D.MR_NO AND C.CASE_NO = Q.CASE_NO(+) "+
//             "AND C.RECEIPT_NO = Q.RECEIPT_NO(+) AND C.AR_AMT <> 0 AND A.REGCAN_USER IS NULL "+
//             "AND C.RECEIPT_NO IS NOT NULL AND Q.ACCOUNT_DATE BETWEEN TO_DATE ('"+start+"','YYYYMMDDHH24MISS' ) "+
//                                    "AND TO_DATE ('"+end+"', 'YYYYMMDDHH24MISS') "+
//             "AND C.RECEIPT_NO IS NOT NULL GROUP BY A.CASE_NO,C.RECEIPT_NO) S,BIL_OPB_RECP Q " +
//             "WHERE S.CASE_NO = Q.CASE_NO  "+
//             "AND S.RECEIPT_NO = Q.RECEIPT_NO "+
//             "AND Q.ACCOUNT_DATE BETWEEN TO_DATE ('"+start+"','YYYYMMDDHH24MISS' ) "+
//                                    "AND TO_DATE ('"+end+"', 'YYYYMMDDHH24MISS') "+
//             "GROUP BY Q.RECEIPT_NO,Q.MEM_PACK_FLG,Q.CASE_NO ,S.REAL_AMT "+
//        "UNION ALL "+
//        "SELECT   -S.REAL_AMT, -Q.REDUCE_AMT, -Q.AR_AMT, -Q.TAOCAN,"+
//                   "Q.RESET_RECEIPT_NO RECEIPT_NO, Q.CASE_NO FROM "+
//                   "(SELECT SUM (C.AR_AMT) AS REAL_AMT ,A.CASE_NO,C.RECEIPT_NO "+
//             " FROM REG_PATADM A, SYS_PATINFO D, SYS_OPERATOR H,  OPD_ORDER_HISTORY C, "+
//                   "(SELECT  SUM (A.REDUCE_AMT) AS REDUCE_AMT,  SUM (A.TOT_AMT) AR_AMT, "+
//                             "CASE WHEN A.MEM_PACK_FLG = 'Y'  THEN SUM (A.TOT_AMT)  ELSE 0 END AS TAOCAN, A.RECEIPT_NO, A.CASE_NO "+
//                        "FROM BIL_OPB_RECP A, (SELECT RECEIPT_NO, ACCOUNT_SEQ, CASHIER_CODE FROM BIL_OPB_RECP "+
//                               "WHERE ACCOUNT_DATE  BETWEEN TO_DATE ('"+start+"','YYYYMMDDHH24MISS' ) "+
//                                    "AND TO_DATE ('"+end+"', 'YYYYMMDDHH24MISS') "+
//                                " AND TOT_AMT < 0) B "+
//                       "WHERE A.RESET_RECEIPT_NO = B.RECEIPT_NO AND A.RESET_RECEIPT_NO IS NOT NULL  "+
//                    "GROUP BY A.RECEIPT_NO, A.MEM_PACK_FLG, A.CASE_NO) Q "+
//             "WHERE A.CASE_NO = C.CASE_NO AND A.MR_NO = D.MR_NO AND C.CASE_NO = Q.CASE_NO(+)  AND C.RECEIPT_NO = Q.RECEIPT_NO(+) "+
//               "AND C.AR_AMT <> 0 AND A.DR_CODE = H.USER_ID AND A.REGCAN_USER IS NULL AND C.RECEIPT_NO IS NOT NULL AND C.RECEIPT_NO = Q.RECEIPT_NO "+
//          "GROUP BY A.CASE_NO,C.RECEIPT_NO) S,(SELECT  SUM (A.REDUCE_AMT) AS REDUCE_AMT,  SUM (A.TOT_AMT) AR_AMT, "+
//                             "CASE WHEN A.MEM_PACK_FLG = 'Y' THEN SUM (A.TOT_AMT) ELSE 0 END AS TAOCAN, A.RECEIPT_NO, A.CASE_NO,A.RESET_RECEIPT_NO "+
//                        "FROM BIL_OPB_RECP A,  (SELECT RECEIPT_NO, ACCOUNT_SEQ, CASHIER_CODE FROM BIL_OPB_RECP "+
//                               "WHERE ACCOUNT_DATE  BETWEEN TO_DATE ('"+start+"','YYYYMMDDHH24MISS' ) "+
//                                    "AND TO_DATE ('"+end+"', 'YYYYMMDDHH24MISS') "+
//                                 " AND TOT_AMT < 0) B "+
//                       "WHERE A.RESET_RECEIPT_NO = B.RECEIPT_NO AND A.RESET_RECEIPT_NO IS NOT NULL  "+
//                    "GROUP BY A.RECEIPT_NO, A.MEM_PACK_FLG, A.CASE_NO,A.RESET_RECEIPT_NO) Q WHERE  S.CASE_NO = Q.CASE_NO  "+ 
//             "AND S.RECEIPT_NO = Q.RECEIPT_NO "+
//        "UNION ALL  SELECT   S.REAL_AMT, Q.REDUCE_AMT, Q.AR_AMT, Q.TAOCAN, Q.RECEIPT_NO,"+
//                  " Q.CASE_NO FROM (SELECT  SUM (C.AR_AMT) AS REAL_AMT ,A.CASE_NO,C.RECEIPT_NO "+
//             " FROM REG_PATADM A, SYS_PATINFO D, OPD_ORDER_HISTORY C, "+
//                   "(SELECT   A.RECEIPT_NO, A.CASE_NO FROM BIL_OPB_RECP A, "+
//                             "(SELECT RECEIPT_NO, ACCOUNT_SEQ, CASHIER_CODE FROM BIL_OPB_RECP "+
//                              " WHERE ACCOUNT_DATE  BETWEEN TO_DATE ('"+start+"','YYYYMMDDHH24MISS' ) "+
//                                    "AND TO_DATE ('"+end+"', 'YYYYMMDDHH24MISS')"+
//                                "  AND RESET_RECEIPT_NO IS NOT NULL) B "+
//                      " WHERE A.RECEIPT_NO = B.RECEIPT_NO AND A.RESET_RECEIPT_NO IS NOT NULL  "+
//                    "GROUP BY A.RECEIPT_NO,A.CASE_NO) Q  "+
//             "WHERE A.CASE_NO = C.CASE_NO AND A.MR_NO = D.MR_NO AND C.CASE_NO = Q.CASE_NO AND C.AR_AMT <> 0 "+
//               "AND A.REGCAN_USER IS NULL  AND C.RECEIPT_NO IS NOT NULL AND C.RECEIPT_NO = Q.RECEIPT_NO  "+
//          "GROUP BY A.CASE_NO,C.RECEIPT_NO) S, (SELECT  SUM (A.REDUCE_AMT) AS REDUCE_AMT, SUM (A.TOT_AMT) AR_AMT, "+
//                             "CASE WHEN A.MEM_PACK_FLG = 'Y' THEN SUM (A.TOT_AMT)  ELSE 0 END AS TAOCAN, A.RECEIPT_NO, A.CASE_NO,A.RESET_RECEIPT_NO "+
//                       " FROM BIL_OPB_RECP A, (SELECT RECEIPT_NO, ACCOUNT_SEQ, CASHIER_CODE FROM BIL_OPB_RECP "+
//                               "WHERE ACCOUNT_DATE   BETWEEN TO_DATE ('"+start+"','YYYYMMDDHH24MISS' ) "+
//                                    "AND TO_DATE ('"+end+"', 'YYYYMMDDHH24MISS') "+
//                                 "  AND RESET_RECEIPT_NO IS NOT NULL) B "+
//                      " WHERE A.RECEIPT_NO = B.RECEIPT_NO AND A.RESET_RECEIPT_NO IS NOT NULL  "+
//                 "  GROUP BY A.RECEIPT_NO, A.MEM_PACK_FLG, A.CASE_NO,A.RESET_RECEIPT_NO) Q  WHERE S.CASE_NO = Q.CASE_NO AND S.RECEIPT_NO = Q.RECEIPT_NO) A,REG_PATADM B, " +
//                 "(SELECT H.DR_CODE,H.CASE_NO FROM OPD_DIAGREC H,BIL_OPB_RECP B WHERE H.CASE_NO=B.CASE_NO  AND H.MAIN_DIAG_FLG = 'Y' AND B.ACCOUNT_DATE "+
//                 " BETWEEN TO_DATE ('"+start+"','YYYYMMDDHH24MISS' ) AND TO_DATE ('"+end+"', 'YYYYMMDDHH24MISS')  GROUP BY H.DR_CODE, H.CASE_NO ) H "+
// "WHERE A.CASE_NO=B.CASE_NO AND A.CASE_NO=H.CASE_NO(+) AND A.AR_AMT <> 0 "+whereOne+" ORDER BY A.CASE_NO,A.RECEIPT_NO";
//		System.out.println("sql2:::"+sql2);
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(
				sql1 + " ORDER BY B.CASE_NO,A.RECEIPT_NO "));
		if(parm1.getErrCode()<0){
			this.messageBox("查询失败:"+parm1.getErrText());
			return;
		}
		if(parm1.getCount()<=0){
			this.messageBox("没有查询的数据");
			table.setParmValue(new TParm());
			return;
		}
		BILAccountForDetailPrint print =new BILAccountForDetailPrint();
		TParm tableParm=print.getValue(parm1);
//		TParm parm2 = new TParm(TJDODBTool.getInstance().select(
//				sql2));
		//boolean flg=false;
//		for (int i = 0; i < tableParm.getCount("CASE_NO"); i++) {
//			//flg=false;
////			for (int j = 0; j < parm2.getCount(); j++) {
////				if (tableParm.getValue("CASE_NO",i).equals(parm2.getValue("CASE_NO",j))&&
////						tableParm.getValue("RECEIPT_NO",i).equals(parm2.getValue("RECEIPT_NO",j))) {
////					tableParm.addData("REAL_AMT", parm2.getDouble("REAL_AMT",j));
////					tableParm.addData("REDUCE_AMT", parm2.getDouble("REDUCE_AMT",j));
////					tableParm.addData("AR_AMT", parm2.getDouble("AR_AMT",j));
////					tableParm.addData("TAOCAN", parm2.getDouble("TAOCAN",j));
////					flg=true;
////					break;
////				}
////			}
////			if(!flg){
////				tableParm.addData("REAL_AMT", 0.00);
////				tableParm.addData("REDUCE_AMT", 0.00);
////				tableParm.addData("AR_AMT", 0.00);
////				tableParm.addData("TAOCAN", 0.00);
////			}
//		}
		tableParm.addData("SYSTEM", "COLUMNS", "AR_AMT");
//		tableParm.addData("SYSTEM", "COLUMNS", "REDUCE_AMT");
//		tableParm.addData("SYSTEM", "COLUMNS", "AR_AMT");
//		tableParm.addData("SYSTEM", "COLUMNS", "TAOCAN");
//		double realAmt=0.00;
		double arAmt=0.00;
//		double reduceAmt=0.00;
//		double taoCan=0.00;
		//double [] chargeArray=new double[20];
		String [] chargeStringArray=CHARGE.split(",");
		TParm tableLastParm=tableParm.getRow(tableParm.getCount()-1);
		for (int j = 0; j < chargeStringArray.length; j++) {
			arAmt+=tableLastParm.getDouble(chargeStringArray[j]);
		}
		tableParm.setData("AR_AMT", tableParm.getCount()-1,arAmt);
//		tableParm.addData("MR_NO", "");
//		tableParm.addData("CASE_NO", "");
//		tableParm.addData("PAT_NAME", "");
//		tableParm.addData("CTZ1_CODE", "");
//		tableParm.addData("REG_DATE", "");
//		tableParm.addData("DEPT_CODE", "");
//		tableParm.addData("DR_CODE", "");
//		tableParm.addData("REALDEPT_CODE", "");
//		tableParm.addData("REALDR_CODE", "");
//		tableParm.addData("RECEIPT_NO", "合计：");
//		tableParm.addData("REAL_AMT", StringTool.round(realAmt, 2));
//		tableParm.addData("AR_AMT", StringTool.round(arAmt, 2));
//		tableParm.addData("REDUCE_AMT", StringTool.round(reduceAmt, 2));
//		tableParm.addData("TAOCAN", StringTool.round(taoCan, 2));
//		for (int j = 0; j < chargeArray.length; j++) {
//			tableParm.addData(chargeStringArray[j],StringTool.round(chargeArray[j], 2));
//		}
		table.setParmValue(tableParm);
	}
	/**
	 * 汇出Excel
	 */
	public void onExcel() {
		// 得到UI对应控件对象的方法
		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "门急诊应收费用统计表");
	}
}
