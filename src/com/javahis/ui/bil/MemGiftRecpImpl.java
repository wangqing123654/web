package com.javahis.ui.bil;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title:��Ʒ��ʵ����</p>
 *
 * <p>Description:��Ʒ��ʵ���� </p>
 *
 * <p>Copyright: Copyright (c) 2014</p>
 *
 * <p>Company:bluecore </p>
 *
 * @author zhangp
 * @version 1.0
 */
public class MemGiftRecpImpl implements IRecpType {
	
	BILTaxControl bilTaxControl;
	
	private final String HEADER = 
		"ѡ,30,boolean;��ӡ,30,boolean;" +
		"�˿�����,80;Ʊ�ݺ�,130;��������,150,Timestamp,yyyy/MM/dd HH:mm:ss;" +
		"Ӧ�����,80,double,#########0.00;�ֽ�,80,double,#########0.00;ˢ��,80,double,#########0.00;" +
		"֧Ʊ,80,double,#########0.00;��Ʒ��,80,double,#########0.00;�ֽ����ȯ,80,double,#########0.00;��ӡ��,80,USER;" +
		"����,200;" +
		"��Ʊ����,150,Timestamp,yyyy/MM/dd HH:mm:ss;��Ʊ��Ա,100,USER;��Ʊ��,100";
	private final String PARMMAP = 
		"FLG;TAX_FLG;" +
		"PURCHASER_NAME;TRADE_NO;OPT_DATE;" +
		"AR_AMT;PAY_TYPE01;PAY_TYPE02;" +
		"PAY_TYPE06;PAY_TYPE05;PAY_TYPE07;OPT_USER;" +
		"DESCRIPTION;" +
		"TAX_DATE;TAX_USER;TAX_INV_NO";
	private final String COLUMNHORIZONTALALIGNMENTDATA =
		"2,left;3,left;" +
		"4,left;5,right;6,right;" +
		"7,right;8,right;9,right;" +
		"10,right;11,left;12,left;13,left;" +
		"14,left;15,left;16,left";
	
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
			" SELECT 'N' FLG, A.*" +
			" FROM MEM_GIFTCARD_TRADE_M A" +
			" WHERE " +
			" A.OPT_DATE BETWEEN TO_DATE ('" + recpStDate + "', 'YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + recpEdDate + "', 'YYYYMMDDHH24MISS')" +
			" AND A.TAX_FLG = 'N' ";
//		if(mrNo.length() > 0){
//			sql += " AND A.MR_NO = '" + mrNo + "'";
//		}
		sql += " ORDER BY A.OPT_DATE";
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
    			" UPDATE MEM_GIFTCARD_TRADE_M SET TAX_FLG = 'Y', TAX_DATE = SYSDATE, TAX_USER = '" + taxUser + "'" +
    			" WHERE TRADE_NO = '" + recpParm.getValue("TRADE_NO", i) + "'";
    		parm.addData("SQL", sql);
		}
		return parm;
	}

	@Override
	public TParm onQueryCompeleted(String mrNo, String patName,
			String taxStDate, String taxEdDate) throws Exception {
		// TODO Auto-generated method stub
		String sql =
			" SELECT 'N' FLG, A.*" +
			" FROM MEM_GIFTCARD_TRADE_M A" +
			" WHERE " +
			" A.TAX_DATE BETWEEN TO_DATE ('" + taxStDate + "', 'YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + taxEdDate + "', 'YYYYMMDDHH24MISS')" +
			" AND A.TAX_FLG = 'Y' ";
//		if(mrNo.length() > 0){
//			sql += " AND A.MR_NO = '" + mrNo + "'";
//		}
		sql += " ORDER BY A.OPT_DATE";
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
    			" UPDATE MEM_GIFTCARD_TRADE_M SET TAX_FLG = 'N', TAX_DATE = '', TAX_USER = '' " +
    			" WHERE TRADE_NO = '" + recpParm.getValue("TRADE_NO", i) + "'";
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
    			" UPDATE MEM_GIFTCARD_TRADE_M SET TAX_INV_NO = '" + recpParm.getValue("TAX_INV_NO", i) + "' " +
    			" WHERE TRADE_NO = '" + recpParm.getValue("TRADE_NO", i) + "'";
    		parm.addData("SQL", sql);
		}
		return parm;
	}

	
}
