package com.javahis.ui.bil;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * <p>Title:门诊收据实现类(套餐)</p>
 *
 * <p>Description:门诊收据实现类(套餐) </p>
 *
 * <p>Copyright: Copyright (c) 2014</p>
 *
 * <p>Company:bluecore </p>
 *
 * @author zhangp
 * @version 1.0
 */
public class OpbMemRecpImpl implements IRecpType {
	
	BILTaxControl bilTaxControl;
	
	private final String HEADER = 
		"选,30,boolean;打印,30,boolean;门急别,50,ADM_TYPE;病案号,100;" +
		"病患姓名,80;票据号,80;结算日期,150,Timestamp,yyyy/MM/dd HH:mm:ss;" +
		"应付金额,80,double,#########0.00;医疗卡金额,80,double,#########0.00;绿色通道,80,double,#########0.00;" +
		"礼品卡,80,double,#########0.00;现金抵用券,80,double,#########0.00;打印人,80,USER;" +
		"打发票日期,150,Timestamp,yyyy/MM/dd HH:mm:ss;打发票人员,100,USER;发票号,100";
	private final String PARMMAP = 
		"FLG;TAX_FLG;ADM_TYPE;MR_NO;" +
		"PAT_NAME;PRINT_NO;BILL_DATE;" +
		"AR_AMT;PAY_MEDICAL_CARD;PAY_OTHER1;" +
		"PAY_OTHER3;PAY_OTHER4;CASHIER_CODE;" +
		"TAX_DATE;TAX_USER;TAX_INV_NO";
	private final String COLUMNHORIZONTALALIGNMENTDATA =
		"2,left;3,left;" +
		"4,left;5,left;6,left;" +
		"7,right;8,right;9,right;" +
		"10,right;11,right;12,left;" +
		"13,left;14,left;15,left";
	
	private final String LOCKCOLUMNS = "1,2,3,4,5,6,7,8,9,10,11,12,13,14";
	
	@Override
	public String getLockColumns() throws Exception {
		// TODO Auto-generated method stub
		return LOCKCOLUMNS;
	}
	

	@Override
	public String getHeader() throws Exception {
		// TODO Auto-generated method stub
		return HEADER;
	}

	@Override
	public String getParmMap() throws Exception {
		// TODO Auto-generated method stub
		return PARMMAP;
	}
	
	@Override
	public String getColumnHorizontalAlignmentData() throws Exception {
		// TODO Auto-generated method stub
		return COLUMNHORIZONTALALIGNMENTDATA;
	}
	
	@Override
	public TParm onQuery(String mrNo, String patName, String recpStDate,
			String recpEdDate) throws Exception {
		// TODO Auto-generated method stub
		String sql =
			" SELECT DISTINCT A.*, B.PAT_NAME, 'N' FLG" +
			" FROM BIL_OPB_RECP A, SYS_PATINFO B, OPD_ORDER C" +
			" WHERE " +
			" A.BILL_DATE BETWEEN TO_DATE ('" + recpStDate + "', 'YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + recpEdDate + "', 'YYYYMMDDHH24MISS')" +
			" AND A.MR_NO = B.MR_NO" +
			" AND A.RESET_RECEIPT_NO IS NULL" +
			" AND A.TOT_AMT > 0" +
			" AND A.CASE_NO = C.CASE_NO" +
			" AND A.RECEIPT_NO = C.RECEIPT_NO" +
			" AND C.MEM_PACKAGE_ID IS NOT NULL" +
			" AND A.TAX_FLG = 'N' ";
		if(mrNo.length() > 0){
			sql += " AND A.MR_NO = '" + mrNo + "'";
		}
		sql += " ORDER BY A.BILL_DATE";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}

	@Override
	public TParm onSave(TParm recpParm, String taxUser)
			throws Exception {
		// TODO Auto-generated method stub
		TParm parm = new TParm();
		String sql = "";
    	for (int i = 0; i < recpParm.getCount("FLG"); i++) {
    		sql = 
    			" UPDATE BIL_OPB_RECP SET TAX_FLG = 'Y', TAX_DATE = SYSDATE, TAX_USER = '" + taxUser + "' " +
    			" WHERE CASE_NO = '" + recpParm.getValue("CASE_NO", i) + "' " +
    			" AND RECEIPT_NO = '" + recpParm.getValue("RECEIPT_NO", i) + "'";
    		parm.addData("SQL", sql);
		}
		return parm;
	}

	@Override
	public TParm onQueryCompeleted(String mrNo, String patName,
			String taxStDate, String taxEdDate) throws Exception {
		// TODO Auto-generated method stub
		String sql =
			" SELECT DISTINCT A.*, B.PAT_NAME, 'N' FLG" +
			" FROM BIL_OPB_RECP A, SYS_PATINFO B, OPD_ORDER C" +
			" WHERE " +
			" A.TAX_DATE BETWEEN TO_DATE ('" + taxStDate + "', 'YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + taxEdDate + "', 'YYYYMMDDHH24MISS')" +
			" AND A.MR_NO = B.MR_NO" +
			" AND A.RESET_RECEIPT_NO IS NULL" +
			" AND A.TOT_AMT > 0" +
			" AND A.CASE_NO = C.CASE_NO" +
			" AND A.RECEIPT_NO = C.RECEIPT_NO" +
			" AND C.MEM_PACKAGE_ID IS NOT NULL" +
			" AND A.TAX_FLG = 'Y' ";
		if(mrNo.length() > 0){
			sql += " AND A.MR_NO = '" + mrNo + "'";
		}
		sql += " ORDER BY A.BILL_DATE";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}

	@Override
	public TParm onCancle(TParm recpParm) throws Exception {
		// TODO Auto-generated method stub
		TParm parm = new TParm();
		String sql = "";
    	for (int i = 0; i < recpParm.getCount("FLG"); i++) {
    		sql = 
    			" UPDATE BIL_OPB_RECP SET TAX_FLG = 'N', TAX_DATE = '', TAX_USER = '' " +
    			" WHERE CASE_NO = '" + recpParm.getValue("CASE_NO", i) + "' " +
    			" AND RECEIPT_NO = '" + recpParm.getValue("RECEIPT_NO", i) + "'";
    		parm.addData("SQL", sql);
		}
		return parm;
	}


	@Override
	public TParm onSaveInvNo(TParm recpParm) throws Exception {
		// TODO Auto-generated method stub
		TParm parm = new TParm();
		String sql = "";
    	for (int i = 0; i < recpParm.getCount(); i++) {
    		sql = 
    			" UPDATE BIL_OPB_RECP SET TAX_INV_NO = '" + recpParm.getValue("TAX_INV_NO", i) + "' " +
    			" WHERE CASE_NO = '" + recpParm.getValue("CASE_NO", i) + "' " +
    			" AND RECEIPT_NO = '" + recpParm.getValue("RECEIPT_NO", i) + "'";
    		parm.addData("SQL", sql);
		}
		return parm;
	}



}
