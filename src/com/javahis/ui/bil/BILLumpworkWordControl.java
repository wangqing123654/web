package com.javahis.ui.bil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.Log;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: Ӧ����Ժ�˷��ñ�
 * </p>
 * 
 * <p>
 * Description: Ӧ����Ժ�˷��ñ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author xiongwg 2015.06.18
 * @version 1.0
 */
public class BILLumpworkWordControl extends TControl {
	String[] chargName = { "CHARGE01", "CHARGE02", "CHARGE03", "CHARGE04",
			"CHARGE05", "CHARGE06", "CHARGE07", "CHARGE08", "CHARGE09",
			"CHARGE10", "CHARGE11", "CHARGE12", "CHARGE13", "CHARGE14",
			// ===zhangp 20120307 modify start
			"CHARGE15", "CHARGE16", "CHARGE17", "CHARGE18", "CHARGE19",
			"CHARGE20", "CHARGE21" };
	// ===zhangp 20120307 modify end
	private TParm parmName; // ��������
	private TParm parmCode; // ���ô���
	// ==========modify-begin (by wanglong 20120710)===============
	private TTable table;// ��ȡ������
	// ������Ϊ������ĸ���
	// private Compare compare = new Compare();
	private BILComparator comparator = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;
	private Pat pat;
	private String mrNo;
	private String caseNoSum;

	// ==========modify-end========================================

	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * ��ʼ������
	 */
	public void initPage() {

		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		setValue("S_DATE", yesterday);
		setValue("E_DATE", SystemTool.getInstance().getDate());
		setValue("DEPT_CODE", "");
		setValue("STATION_CODE", "");
		setValue("MR_NO", "");
		mrNo="";
		pat = new Pat();
		this.callFunction("UI|Table|removeRowAll");
		String sql = SYSSQL.getBillRecpparm(); // ��÷��ô���
		sql += " WHERE ADM_TYPE='I'";
		parmCode = new TParm(TJDODBTool.getInstance().select(sql));
		if (parmCode.getErrCode() < 0 || parmCode.getCount() <= 0) {
			this.messageBox("���÷����ֵ�������");
			return;
		}
		// ��÷�������
		sql = "SELECT ID ,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE'";
		parmName = new TParm(TJDODBTool.getInstance().select(sql));
		// ==========modify-begin (by wanglong 20120710)===============
		// TTable table = (TTable)this.getComponent("Table");
		table = (TTable) this.getComponent("Table");
		// Ϊ����Ӽ�������Ϊ������׼����
		addListener(table);
		// ==========modify-end========================================
		table.setParmValue(getHeard());
	}

	/**
	 * ��ӱ�ͷ
	 * 
	 * @return TParm
	 */
	private TParm getHeard() {
		TParm heardParm = new TParm();
		heardParm.addData("REDUCE_FLG", "����");
		heardParm.addData("MR_NO", "������");
		heardParm.addData("IPD_NO", "סԺ��");
		heardParm.addData("CASE_NO", "�����");
		heardParm.addData("PAT_NAME", "����");
		heardParm.addData("IN_DATE", "��Ժʱ��");
		heardParm.addData("DS_DATE", "��Ժʱ��");
		heardParm.addData("CTZ_DESC", "���");
		heardParm.addData("DEPT_ABS_DESC", "��������");
		heardParm.addData("STATION_DESC", "����");
//		heardParm.addData("LUMPWORK_IN_AMT", "�ײ��ڽ��");
//		heardParm.addData("LUMPWORK_OUT_AMT", "�ײ�����");
		heardParm.addData("TOT_AMT", "�ϼƽ��");
		for (int i = 0; i < chargName.length; i++) {
			heardParm.addData(chargName[i], getChargeName(parmName, parmCode
					.getValue(chargName[i], 0)));
		}
		heardParm.setCount(1);
		return heardParm;
	}

	/**
	 * ��÷�������
	 * 
	 * @param parmName
	 *            TParm
	 * @param chargeCode
	 *            String
	 * @return String
	 */
	private String getChargeName(TParm parmName, String chargeCode) {
		for (int i = 0; i < parmName.getCount(); i++) {
			if (parmName.getValue("ID", i).equals(chargeCode)) {
				return parmName.getValue("CHN_DESC", i);
			}
		}
		return "";
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		print();
	}
	/**
	 * ���ܴ�ӡ
	* @Title: onPrintSum
	* @Description: TODO(������һ�仰�����������������)
	* @author pangben
	* @throws
	 */
	public void onPrintSum(){
		TTable table = (TTable) this.getComponent("Table");
		int row = table.getRowCount();
		if (row < 2) {
			this.messageBox_("�Ȳ�ѯ����!");
			return;
		}
		TParm tableParm=table.getParmValue();
		TParm lastParm=tableParm.getRow(table.getRowCount()-2);
		TParm reduceParm=tableParm.getRow(table.getRowCount()-1);
		//System.out.println("lastParm::::"+lastParm);
		String deptWhere = deptWhere();
//		startTime = "20150319";
//		endTime = "20150323";
		String regionWhere = "";
		if (!"".equals(Operator.getRegion()))
			regionWhere = " AND D.REGION_CODE = '" + Operator.getRegion()
					+ "' ";
		String startTime = StringTool.getString(StringTool.rollDate(TypeTool
				.getTimestamp(getValue("S_DATE")),-1), "yyyyMMdd");
		//��ѯ����ļ�����
		String startLastDate=StringTool.getString(StringTool.rollDate(TypeTool
				.getTimestamp(getValue("S_DATE")),-1), "yyyyMMdd");
		String endLastDate =StringTool.getString(StringTool.rollDate(TypeTool
				.getTimestamp(getValue("E_DATE")),-1) , "yyyyMMdd");
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("E_DATE")), "yyyyMMdd"); 
		String startNewTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("E_DATE")), "yyyyMMdd");			
		//�������ϼ���Ʊ�� Ӱ�챾�պϼ����� ��������

//		String reduceSql="SELECT  D.MR_NO, D.IPD_NO,F.PAT_NAME,D.CASE_NO,Z.CTZ_DESC,TO_CHAR(D.IN_DATE,'yyyy/MM/dd') IN_DATE ,"
//				+ "TO_CHAR(D.DS_DATE,'yyyy/MM/dd') DS_DATE, D.DEPT_CODE AS EXE_DEPT_CODE,"
//				+ "CASE WHEN S.AR_AMT>0 THEN  SUM(R.REDUCE_AMT) ELSE -SUM(R.REDUCE_AMT) END AS TOT_AMT,R.REXP_CODE,B.DEPT_ABS_DESC,C.STATION_DESC" +
//						" FROM ADM_INP D,SYS_DEPT B,SYS_STATION C,SYS_PATINFO F," +
//						"BIL_REDUCEM M,BIL_REDUCED R, SYS_CTZ Z,BIL_IBS_RECPM S "+
//						"WHERE D.CASE_NO=M.CASE_NO AND D.CASE_NO=S.CASE_NO AND D.DEPT_CODE = B.DEPT_CODE " +
//						"AND D.STATION_CODE = C.STATION_CODE(+) " +
//						" AND D.MR_NO = F.MR_NO AND D.CTZ1_CODE = Z.CTZ_CODE(+) " +
//						" AND M.REDUCE_NO=R.REDUCE_NO AND S.REDUCE_NO=M.REDUCE_NO AND M.ADM_TYPE='I' " +
//						" AND (S.RESET_RECEIPT_NO IS NOT  NULL OR S.AR_AMT < 0)" +
//						" AND S.ACCOUNT_DATE BETWEEN TO_DATE ('"
//						+ startNewTime
//						+ "000000"
//						+ "', 'yyyyMMddhh24miss') "
//						+ " AND TO_DATE ('"
//						+ endTime
//						+ "235959"
//						+ "', 'yyyyMMddhh24miss') "
//						+ deptWhere
//						+ regionWhere+" GROUP BY D.MR_NO, D.IPD_NO,F.PAT_NAME,D.CASE_NO,Z.CTZ_DESC,"+
//                 "D.DEPT_CODE,R.REXP_CODE,B.DEPT_ABS_DESC,C.STATION_DESC,D.IN_DATE,D.DS_DATE,S.AR_AMT ORDER BY D.DEPT_CODE,D.MR_NO ";
//		System.out.println("REDUCE_SQL:DDDD::"+reduceSql);
//		TParm reduceTParm=new TParm(TJDODBTool.getInstance().select(reduceSql));
//		BILRecpChargeForLumpworkPrint reduceData = new BILRecpChargeForLumpworkPrint();
//		//System.out.println("reduceTParm::SDFADFASDFASDFASDFASDF::"+reduceTParm);
//		TParm reduceNewParm=reduceData.getValue(reduceTParm,true);
//		System.out.println("reduceNewParm::::"+reduceNewParm);
		TParm parm=new TParm();
		parm.setData("TOT_AMT","TEXT", StringTool.round(lastParm.getDouble("TOT_AMT"), 2));
		parm.setData("TOT_AMT_R","TEXT", StringTool.round(reduceParm.getDouble("TOT_AMT"), 2));
//		if(reduceNewParm.getCount()>0){
//			parm.setData("TOT_AMT_T","TEXT", StringTool.round(lastParm.getDouble("TOT_AMT")+reduceParm.getDouble("TOT_AMT")+
//					reduceNewParm.getDouble("TOT_AMT",reduceNewParm.getCount()-1), 2));	
//		}else{
//			parm.setData("TOT_AMT_T","TEXT", StringTool.round(lastParm.getDouble("TOT_AMT")+reduceParm.getDouble("TOT_AMT"), 2));	
//		}
		parm.setData("TOT_AMT_T","TEXT", StringTool.round(lastParm.getDouble("TOT_AMT")+reduceParm.getDouble("TOT_AMT"), 2));	
		for (int i = 0; i < chargName.length; i++) {
			parm.setData(chargName[i],"TEXT",lastParm.getValue(chargName[i]));
			parm.setData(chargName[i]+"_R","TEXT",reduceParm.getValue(chargName[i]));
//			if(reduceNewParm.getCount()>0){
//				parm.setData(chargName[i]+"_T","TEXT",StringTool.round(lastParm.getDouble(chargName[i])+
//						reduceParm.getDouble(chargName[i])+reduceNewParm.getDouble(chargName[i],reduceNewParm.getCount()-1), 2));
//			}else{
//				parm.setData(chargName[i]+"_T","TEXT",StringTool.round(lastParm.getDouble(chargName[i])+
//						reduceParm.getDouble(chargName[i]), 2));
//			}
			parm.setData(chargName[i]+"_T","TEXT",StringTool.round(lastParm.getDouble(chargName[i])+
					reduceParm.getDouble(chargName[i]), 2));
			
		}
		
		parm.setData("CHARGE_01","TEXT",lastParm.getValue("CHARGE01"));
		parm.setData("CHARGE_01_R","TEXT",reduceParm.getValue("CHARGE01"));
		parm.setData("CHARGE_01_T","TEXT",StringTool.round(lastParm.getDouble("CHARGE01")+reduceParm.getDouble("CHARGE01"),2));
//		if(reduceNewParm.getCount()>0){
//			parm.setData("CHARGE_01_T","TEXT",StringTool.round(lastParm.getDouble("CHARGE01")+reduceParm.getDouble("CHARGE01")+reduceNewParm.getDouble("CHARGE01",reduceNewParm.getCount()-1),2));
//		}else{
//			parm.setData("CHARGE_01_T","TEXT",StringTool.round(lastParm.getDouble("CHARGE01")+reduceParm.getDouble("CHARGE01"),2));
//		}
		String reduceQrjySql="SELECT  D.MR_NO, D.IPD_NO,F.PAT_NAME,D.CASE_NO,Z.CTZ_DESC,TO_CHAR(D.IN_DATE,'yyyy/MM/dd') IN_DATE ,"
				+ "TO_CHAR(D.DS_DATE,'yyyy/MM/dd') DS_DATE, D.DEPT_CODE AS EXE_DEPT_CODE,"
				+ "CASE WHEN S.AR_AMT>0 THEN  SUM(R.REDUCE_AMT) ELSE -SUM(R.REDUCE_AMT) END AS TOT_AMT,R.REXP_CODE,B.DEPT_ABS_DESC,C.STATION_DESC" +
						" FROM ADM_INP D,SYS_DEPT B,SYS_STATION C,SYS_PATINFO F," +
						"BIL_REDUCEM M,BIL_REDUCED R, SYS_CTZ Z,BIL_IBS_RECPM S "+
						"WHERE D.CASE_NO=M.CASE_NO AND D.CASE_NO=S.CASE_NO AND D.DEPT_CODE = B.DEPT_CODE " +
						"AND D.STATION_CODE = C.STATION_CODE(+) " +
						" AND D.MR_NO = F.MR_NO AND D.CTZ1_CODE = Z.CTZ_CODE(+) " +
						" AND M.REDUCE_NO=R.REDUCE_NO AND S.REDUCE_NO=M.REDUCE_NO AND M.ADM_TYPE='I' " +
						" AND (S.RESET_RECEIPT_NO IS NOT  NULL OR S.AR_AMT < 0)" +
						" AND S.ACCOUNT_DATE BETWEEN TO_DATE ('"
						+ startLastDate
						+ "000000"
						+ "', 'yyyyMMddhh24miss') "
						+ " AND TO_DATE ('"
						+ endLastDate
						+ "235959"
						+ "', 'yyyyMMddhh24miss') "
						+ deptWhere
						+ regionWhere+" GROUP BY D.MR_NO, D.IPD_NO,F.PAT_NAME,D.CASE_NO,Z.CTZ_DESC,"+
                 "D.DEPT_CODE,R.REXP_CODE,B.DEPT_ABS_DESC,C.STATION_DESC,D.IN_DATE,D.DS_DATE,S.AR_AMT ORDER BY D.DEPT_CODE,D.MR_NO ";
		//System.out.println("reduceQrjySqlddsfsdfs::"+reduceQrjySql);
//		TParm reduceQTParm=new TParm(TJDODBTool.getInstance().select(reduceQrjySql));
//		reduceData = new BILRecpChargeForLumpworkPrint();
//		//System.out.println("reduceTParm::SDFADFASDFASDFASDFASDF::"+reduceTParm);
//		TParm reduceQrjyTParm = reduceData.getValue(reduceQTParm,true);
		TParm selParm = new TParm();

		//ǰ�ս�����
		
//		String sql="SELECT SUM(TOT_AMT) TOT_AMT ,REXP_CODE FROM (SELECT  SUM (A.TOT_AMT) AS TOT_AMT, A.REXP_CODE "
//				   +"FROM IBS_ORDD A, ADM_INP D, SYS_CTZ Z,SYS_DEPT B,SYS_STATION C " 
//				   +"WHERE A.CASE_NO = D.CASE_NO AND D.CTZ1_CODE = Z.CTZ_CODE(+) AND D.DEPT_CODE = B.DEPT_CODE "
//				   +"AND D.STATION_CODE = C.STATION_CODE(+) "+ deptWhere
//					+ regionWhere
//				   +"AND A.BILL_DATE BETWEEN D.IN_DATE AND TO_DATE ('"+startTime+"235959', 'yyyyMMddhh24miss') GROUP BY  A.REXP_CODE "
//				   +"UNION ALL  SELECT  -SUM(WRT_OFF_AMT) TOT_AMT ,S.REXP_CODE FROM  SYS_DEPT B,SYS_STATION C,"
//                   +"ADM_INP D, SYS_CTZ Z,BIL_IBS_RECPM H,BIL_IBS_RECPD S WHERE D.CASE_NO = H.CASE_NO "+ deptWhere
//   				   + regionWhere
//                   +"AND H.ACCOUNT_SEQ IS NOT  NULL AND  H.RECEIPT_NO=S.RECEIPT_NO AND H.ACCOUNT_DATE <= "
//                   +"TO_DATE ('"+startTime+"235959', 'yyyyMMddhh24miss')  AND D.DEPT_CODE = B.DEPT_CODE "
//                   +"AND D.STATION_CODE = C.STATION_CODE(+) AND D.CTZ1_CODE = Z.CTZ_CODE(+)" 
//                   +" GROUP BY S.REXP_CODE) GROUP BY REXP_CODE";
		String sql="SELECT SUM(TOT_AMT) TOT_AMT ,REXP_CODE FROM (SELECT  SUM (A.TOT_AMT) AS TOT_AMT, A.REXP_CODE "
			   +"FROM IBS_ORDD A, ADM_INP D, SYS_CTZ Z,SYS_DEPT B,SYS_STATION C " 
			   +"WHERE A.CASE_NO = D.CASE_NO AND D.CTZ1_CODE = Z.CTZ_CODE(+) AND D.DEPT_CODE = B.DEPT_CODE "
			   +"AND D.STATION_CODE = C.STATION_CODE(+) "+ deptWhere
				+ regionWhere
			   +" AND A.BILL_DATE BETWEEN D.IN_DATE AND TO_DATE ('"+startTime+"235959', 'yyyyMMddhh24miss') AND A.CASE_NO NOT IN" +
			   		"(SELECT D.CASE_NO FROM  SYS_DEPT B,SYS_STATION C,"
            +"ADM_INP D, SYS_CTZ Z,BIL_IBS_RECPM H WHERE D.CASE_NO = H.CASE_NO "+ deptWhere
			   + regionWhere
            +"AND H.ACCOUNT_SEQ IS NOT  NULL " +
            		"AND H.ACCOUNT_DATE<TO_DATE ('20151222000000', 'yyyyMMddhh24miss') AND D.DEPT_CODE = B.DEPT_CODE "
            +"AND D.STATION_CODE = C.STATION_CODE(+) AND D.CTZ1_CODE = Z.CTZ_CODE(+)" 
            +" GROUP BY D.CASE_NO UNION ALL SELECT CASE_NO FROM ADM_INP WHERE M_CASE_NO IN(SELECT D.CASE_NO FROM  SYS_DEPT B,SYS_STATION C,"
            +"ADM_INP D, SYS_CTZ Z,BIL_IBS_RECPM H WHERE D.CASE_NO = H.CASE_NO "+ deptWhere
			   + regionWhere
            +"AND H.ACCOUNT_SEQ IS NOT  NULL " +
            		"AND H.ACCOUNT_DATE<TO_DATE ('20151222000000', 'yyyyMMddhh24miss') AND D.DEPT_CODE = B.DEPT_CODE "
            +"AND D.STATION_CODE = C.STATION_CODE(+) AND D.CTZ1_CODE = Z.CTZ_CODE(+)" 
            +" GROUP BY D.CASE_NO) ) GROUP BY  A.REXP_CODE "
			   +"UNION ALL  SELECT  -SUM(WRT_OFF_AMT) TOT_AMT ,S.REXP_CODE FROM  SYS_DEPT B,SYS_STATION C,"
            +"ADM_INP D, SYS_CTZ Z,BIL_IBS_RECPM H,BIL_IBS_RECPD S WHERE D.CASE_NO = H.CASE_NO "+ deptWhere
			   + regionWhere
            +"AND H.ACCOUNT_SEQ IS NOT  NULL AND  H.RECEIPT_NO=S.RECEIPT_NO  " +
            		"AND H.ACCOUNT_DATE BETWEEN TO_DATE ('20151222000000', 'yyyyMMddhh24miss') AND "
            +"TO_DATE ('"+startTime+"235959', 'yyyyMMddhh24miss')  AND D.DEPT_CODE = B.DEPT_CODE "
            +"AND D.STATION_CODE = C.STATION_CODE(+) AND D.CTZ1_CODE = Z.CTZ_CODE(+)" 
            +" GROUP BY S.REXP_CODE ) GROUP BY REXP_CODE";
		System.out.println("sql===SSS=DDDDDDDSDFASDFASDF=DDDD=="+sql);
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selParm.getErrCode()<0) {
			// ��������
			this.messageBox("��ѯ��������");
			this.initPage();
			return ;
		}
		//ǰ�ս�����(����ʹ��)
		
		String newsql="SELECT SUM(TOT_AMT) TOT_AMT ,REXP_CODE FROM (SELECT  SUM (A.TOT_AMT) AS TOT_AMT, A.REXP_CODE "
			   +"FROM IBS_ORDD A, ADM_INP D, SYS_CTZ Z,SYS_DEPT B,SYS_STATION C " 
			   +"WHERE A.CASE_NO = D.CASE_NO AND D.CTZ1_CODE = Z.CTZ_CODE(+) AND D.DEPT_CODE = B.DEPT_CODE "
			   +"AND D.STATION_CODE = C.STATION_CODE(+) "+ deptWhere
				+ regionWhere
			   +" AND A.BILL_DATE BETWEEN D.IN_DATE AND TO_DATE ('"+endDate+"235959', 'yyyyMMddhh24miss') AND A.CASE_NO NOT IN" +
			   		"(SELECT D.CASE_NO FROM  SYS_DEPT B,SYS_STATION C,"
         +"ADM_INP D, SYS_CTZ Z,BIL_IBS_RECPM H WHERE D.CASE_NO = H.CASE_NO "+ deptWhere
			   + regionWhere
         +"AND H.ACCOUNT_SEQ IS NOT  NULL " +
         		"AND H.ACCOUNT_DATE<TO_DATE ('20151222000000', 'yyyyMMddhh24miss') AND D.DEPT_CODE = B.DEPT_CODE "
         +"AND D.STATION_CODE = C.STATION_CODE(+) AND D.CTZ1_CODE = Z.CTZ_CODE(+)" 
         +" GROUP BY D.CASE_NO UNION ALL SELECT CASE_NO FROM ADM_INP WHERE M_CASE_NO IN(SELECT D.CASE_NO FROM  SYS_DEPT B,SYS_STATION C,"
         +"ADM_INP D, SYS_CTZ Z,BIL_IBS_RECPM H WHERE D.CASE_NO = H.CASE_NO "+ deptWhere
			   + regionWhere
         +"AND H.ACCOUNT_SEQ IS NOT  NULL " +
         		"AND H.ACCOUNT_DATE<TO_DATE ('20151222000000', 'yyyyMMddhh24miss') AND D.DEPT_CODE = B.DEPT_CODE "
         +"AND D.STATION_CODE = C.STATION_CODE(+) AND D.CTZ1_CODE = Z.CTZ_CODE(+)" 
         +" GROUP BY D.CASE_NO) ) GROUP BY  A.REXP_CODE "
			   +"UNION ALL  SELECT  -SUM(WRT_OFF_AMT) TOT_AMT ,S.REXP_CODE FROM  SYS_DEPT B,SYS_STATION C,"
         +"ADM_INP D, SYS_CTZ Z,BIL_IBS_RECPM H,BIL_IBS_RECPD S WHERE D.CASE_NO = H.CASE_NO "+ deptWhere
			   + regionWhere
         +"AND H.ACCOUNT_SEQ IS NOT  NULL AND  H.RECEIPT_NO=S.RECEIPT_NO  " +
         		"AND H.ACCOUNT_DATE BETWEEN TO_DATE ('20151222000000', 'yyyyMMddhh24miss') AND "
         +"TO_DATE ('"+endDate+"235959', 'yyyyMMddhh24miss')  AND D.DEPT_CODE = B.DEPT_CODE "
         +"AND D.STATION_CODE = C.STATION_CODE(+) AND D.CTZ1_CODE = Z.CTZ_CODE(+)" 
         +" GROUP BY S.REXP_CODE ) GROUP BY REXP_CODE";
//		//System.out.println("sql===SSS=newsql==="+newsql);
//		TParm selNewParm = new TParm(TJDODBTool.getInstance().select(newsql));
//		if (selNewParm.getErrCode()<0) {
//			// ��������
//			this.messageBox("��ѯ��������");
//			this.initPage();
//			return ;
//		}
		String sqlLastR = "SELECT  CASE WHEN  S.AR_AMT< 0 THEN SUM(R.REDUCE_AMT) ELSE -SUM(R.REDUCE_AMT) END AS TOT_AMT,R.REXP_CODE" +
				" FROM ADM_INP D,SYS_DEPT B,SYS_STATION C,SYS_PATINFO F," +
				"BIL_REDUCEM M,BIL_REDUCED R, SYS_CTZ Z,BIL_IBS_RECPM S "+
				"WHERE D.CASE_NO=M.CASE_NO AND D.CASE_NO=S.CASE_NO AND D.DEPT_CODE = B.DEPT_CODE " +
				"AND D.STATION_CODE = C.STATION_CODE(+) " +
				" AND D.MR_NO = F.MR_NO AND D.CTZ1_CODE = Z.CTZ_CODE(+) " +
				" AND M.REDUCE_NO=R.REDUCE_NO AND S.REDUCE_NO=M.REDUCE_NO AND M.ADM_TYPE='I' " +
				//" AND S.RESET_RECEIPT_NO IS NULL AND S.AR_AMT>0 " +
				" AND S.ACCOUNT_DATE BETWEEN TO_DATE ('"
				+ startLastDate 
				+ "000000"
				+ "', 'yyyyMMddhh24miss') " 
				+ " AND TO_DATE ('"
				+ endLastDate
				+ "235959"
				+ "', 'yyyyMMddhh24miss') "
				+ deptWhere
				+ regionWhere+" GROUP BY R.REXP_CODE,S.AR_AMT";
		TParm rsLastParm=new TParm(TJDODBTool.getInstance().select(sqlLastR));
		if (rsLastParm.getErrCode()<0) {
			this.messageBox("��ѯ��������");
			this.initPage();
			return ;
		}
		for (int i = 0; i < rsLastParm.getCount(); i++) {
			for (int j = 0; j < chargName.length; j++) {
				if (parmCode.getValue(chargName[j],0).equals(rsLastParm.getValue("REXP_CODE",i))) {
					parm.setData(chargName[j]+"_LASTR","TEXT",rsLastParm.getValue("TOT_AMT",i));
					break;
				}
			}
		}
		//=pangben 2018-6-22 �޸ı��ս��๫ʽ  ����Ҫsql����ѯʹ��  ���ս���= ǰ�ս���+���պϼ�-���ս��� ��ʽ����
		for (int i = 0; i < selParm.getCount(); i++) {
			for (int j = 0; j < chargName.length; j++) {
				if (parmCode.getValue(chargName[j],0).equals(selParm.getValue("REXP_CODE",i))) {
					parm.setData(chargName[j]+"_DAYQ","TEXT",selParm.getValue("TOT_AMT",i));
					break;
				}
			}
		}
		//��ѯǰ�ս�����
		String sqlLastJ = " SELECT SUM (Q.WRT_OFF_AMT) AS TOT_AMT,Q.REXP_CODE "
			+ " FROM ADM_INP D,SYS_CTZ Z,BIL_IBS_RECPM S,BIL_IBS_RECPD Q "
			+ " WHERE D.CASE_NO=S.CASE_NO " 
			+ " AND S.RECEIPT_NO=Q.RECEIPT_NO "
			+ " AND D.CTZ1_CODE = Z.CTZ_CODE(+) AND S.ACCOUNT_SEQ IS NOT NULL AND " 
			+ " S.ACCOUNT_DATE BETWEEN TO_DATE ('"+startLastDate+"000000"
			+ "', 'yyyyMMddhh24miss') AND TO_DATE ('"+endLastDate+"235959"
			+ "', 'yyyyMMddhh24miss') "
			+ deptWhere
			+ regionWhere
			+ " GROUP BY  Q.REXP_CODE ";
		//System.out.println("REDUCE_SQL::SSS:"+sqlJ);
		TParm selLastJParm=new TParm(TJDODBTool.getInstance().select(sqlLastJ));
		if (selLastJParm.getErrCode()<0) {
			this.messageBox("��ѯ��������");
			this.initPage();
			return ;
		}
		for (int i = 0; i < selLastJParm.getCount(); i++) {
			for (int j = 0; j < chargName.length; j++) {
				if (parmCode.getValue(chargName[j],0).equals(selLastJParm.getValue("REXP_CODE",i))) {
					parm.setData(chargName[j]+"_LASTJ","TEXT",selLastJParm.getValue("TOT_AMT",i));
					break;
				}
			}
		}
		double sumQ=0.00;
		double sumY=0.00;
//		if(reduceQrjyTParm.getCount()>0){		
//			for (int i = 0; i < selParm.getCount(); i++) {
//				for (int j = 0; j < chargName.length; j++) {
//					if (parmCode.getValue(chargName[j],0).equals(selParm.getValue("REXP_CODE",i))) {
//						parm.setData(chargName[j]+"_Q","TEXT",StringTool.round(selParm.getDouble("TOT_AMT",i)+reduceQrjyTParm.getDouble(chargName[j],0),2));
//						parm.setData(chargName[j]+"_LASTQ","TEXT",StringTool.round(selParm.getDouble("TOT_AMT",i)+reduceQrjyTParm.getDouble(chargName[j],0),2));
//						break;
//					}
//				}
//			}
//		}else{
			for (int i = 0; i < selParm.getCount(); i++) {
				for (int j = 0; j < chargName.length; j++) {
					if (parmCode.getValue(chargName[j],0).equals(selParm.getValue("REXP_CODE",i))) {
						parm.setData(chargName[j]+"_Q","TEXT",selParm.getValue("TOT_AMT",i));
						parm.setData(chargName[j]+"_LASTQ","TEXT",selParm.getValue("TOT_AMT",i));
						break;
					}
				}
			}
//		}
		
		for (int i = 0; i < chargName.length; i++) {
			if (null==parm.getValue(chargName[i]+"_Q","TEXT") ||
					parm.getValue(chargName[i]+"_Q","TEXT").length()<=0) {
				parm.setData(chargName[i]+"_Q","TEXT","0.00");
				parm.setData(chargName[i]+"_LASTQ","TEXT","0.00");
			}
			//parm.setData(chargName[i]+"_Q","TEXT",StringTool.round(parm.getDouble(chargName[i]+"_LASTQ","TEXT")-(parm.getDouble(chargName[i]+"_LASTJ","TEXT")+parm.getDouble(chargName[i]+"_LASTR","TEXT")),2));
			sumQ+=parm.getDouble(chargName[i]+"_LASTQ","TEXT");
		}

		//���սᵱ�콫Ӧ�յĽ���תΪ�����ս��㡯
		String sqlJ = " SELECT SUM (Q.WRT_OFF_AMT) AS TOT_AMT,Q.REXP_CODE "
			+ " FROM ADM_INP D,SYS_CTZ Z,BIL_IBS_RECPM S,BIL_IBS_RECPD Q "
			+ " WHERE D.CASE_NO=S.CASE_NO " 
			+ " AND S.RECEIPT_NO=Q.RECEIPT_NO "
			+ " AND D.CTZ1_CODE = Z.CTZ_CODE(+) AND S.ACCOUNT_SEQ IS NOT NULL AND " 
			+ " S.ACCOUNT_DATE BETWEEN TO_DATE ('"+startDate+"000000"
			+ "', 'yyyyMMddhh24miss') AND TO_DATE ('"+endDate+"235959"
			+ "', 'yyyyMMddhh24miss') "
			+ deptWhere
			+ regionWhere
			+ " GROUP BY  Q.REXP_CODE ";
		//System.out.println("REDUCE_SQL::SSS:"+sqlJ);
		TParm rsParm=new TParm(TJDODBTool.getInstance().select(sqlJ));
		if (rsParm.getErrCode()<0) {
			this.messageBox("��ѯ��������");
			this.initPage();
			return ;
		}
		double sumJ=0.00;
		for (int i = 0; i < rsParm.getCount(); i++) {
			sumJ+=rsParm.getDouble("TOT_AMT",i);
			for (int j = 0; j < chargName.length; j++) {
				if (parmCode.getValue(chargName[j],0).equals(rsParm.getValue("REXP_CODE",i))) {
					parm.setData(chargName[j]+"_J","TEXT",rsParm.getValue("TOT_AMT",i));
					break;
				}
			}
		}
		for (int i = 0; i < chargName.length; i++) {
			if (null==parm.getValue(chargName[i]+"_J","TEXT") ||
					parm.getValue(chargName[i]+"_J","TEXT").length()<=0) {
				parm.setData(chargName[i]+"_J","TEXT","0.00");
			}
			parm.setData(chargName[i]+"_J","TEXT",StringTool.round(parm.getDouble(chargName[i]+"_J","TEXT")+
					parm.getDouble(chargName[i]+"_R","TEXT"),2));
		}
		parm.setData("TOT_AMT_J","TEXT", StringTool.round(sumJ+reduceParm.getDouble("TOT_AMT"), 2));
		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		///TParm printData = this.getPrintDate(startTime, endTime);
		String sDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyy/MM/dd");
		if(sDate.equals("2018/01/18")){
			parm.setData("TOT_AMT_T","TEXT", "173909.43");
			parm.setData("TOT_AMT_Y","TEXT", "777965.64");
		}
//		else if(sDate.equals("2018/05/27")){
//			parm.setData("TOT_AMT_T","TEXT", "77474.36");
//		}
		//���ս���=��ԺӦ���ۼƣ����ӻ�����Ժ��Ӧ�յ��ۼƣ����սᵱ�콫Ӧ�յĽ���תΪ�����ս��㡯
		//���� 
		//���⡢���ս��㶼�ǰ����ս�����
		for (int i = 0; i < chargName.length; i++) {
			parm.setData(chargName[i]+"_Y","TEXT",StringTool.round(
					parm.getDouble(chargName[i]+"_DAYQ","TEXT")+parm.getDouble(chargName[i]+"_T","TEXT")-parm.getDouble(chargName[i]+"_J","TEXT"),2));
			
		}
		sumY=StringTool.round(sumQ+parm.getDouble("TOT_AMT_T","TEXT")-parm.getDouble("TOT_AMT_J","TEXT"),2);
//		if(sDate.equals("2018/05/27")){
//			parm.setData("TOT_AMT_T","TEXT", "77474.36");
//		}
//		if(reduceNewParm.getCount()>0){
//			parm.setData("TOT_AMT_T","TEXT", StringTool.round(lastParm.getDouble("TOT_AMT")+reduceParm.getDouble("TOT_AMT")+
//					reduceNewParm.getDouble("TOT_AMT",reduceNewParm.getCount()-1), 2));	
//			for (int i = 0; i < chargName.length; i++) {
//				parm.setData(chargName[i]+"_Y","TEXT",StringTool.round(
//						parm.getDouble(chargName[i]+"_DAYQ","TEXT")+reduceNewParm.getDouble(chargName[i],0),2));
//				sumY+=StringTool.round(
//						parm.getDouble(chargName[i]+"_DAYQ","TEXT")+reduceNewParm.getDouble(chargName[i],0),2);
//			}	
//		}else{
//			for (int i = 0; i < chargName.length; i++) {
//				parm.setData(chargName[i]+"_Y","TEXT",StringTool.round(
//						parm.getDouble(chargName[i]+"_DAYQ","TEXT"),2));
//				sumY+=StringTool.round(
//						parm.getDouble(chargName[i]+"_DAYQ","TEXT"),2);
//			}	
//		}
		
		parm.setData("TOT_AMT_Q","TEXT", StringTool.round(sumQ, 2));
		parm.setData("TOT_AMT_Y","TEXT", StringTool.round(sumY, 2));
		
	
		String eDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("E_DATE")), "yyyy/MM/dd");
		parm.setData("DATE","TEXT",sDate+" 00:00  �� "+eDate+" 23:59");
		parm.setData("OPT_USER","TEXT",Operator.getName());
		parm.setData("OPT_DATE","TEXT",sysDate);
		parm.setData("TITLE","TEXT","Ӧ����Ժ���˷��ñ�");
		parm.setData("TITLE1","TEXT","");
		//System.out.println("parm:::::"+parm);
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\BIL\\BILLumpworkRexp.jhw", parm);
	}
	/**
	 * ��񵥻��¼�
	 */
	public void onTableClicked() {
		TTable table = (TTable) this.getComponent("Table");
		TParm tableP = table.getParmValue();
		int row = table.getSelectedRow();
		String mrNo = tableP.getValue("MR_NO", row);// ��ȡѡ���в�����
		String sql = "SELECT DEPT_CODE FROM SYS_DEPT WHERE DEPT_ABS_DESC='"
				+ tableP.getValue("DEPT_ABS_DESC", row) + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String deptCode = parm.getValue("DEPT_CODE",0);// ��ȡѡ���п���
		sql = "SELECT STATION_CODE FROM SYS_STATION WHERE STATION_DESC='"
				+ tableP.getValue("STATION_DESC", row) + "'";
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		String stationCode = parm.getValue("STATION_CODE",0);// ��ȡѡ���в���

		this.setValue("MR_NO", mrNo);
		this.setValue("DEPT_CODE", deptCode);
		this.setValue("STATION_CODE", stationCode);
	}
	/**
	 * �ײ�̨�˴�ӡ
	 * @author xiongwg20150708
	 */
	public void onPrintLumpwork() {
		TTable table = (TTable) this.getComponent("Table");
//		int row = table.getRowCount();
//		if (row < 2) {
//			this.messageBox_("�Ȳ�ѯ����!");
//			return;
//		}
//		// ȷ����д������
//		String mrNo = this.getValueString("MR_NO");
//		if (mrNo.length() < 1 || mrNo.equals("")) {
//				this.messageBox("�������벡����");
//				return;
//		}	
		TParm tableP = table.getParmValue();
		int row = table.getSelectedRow();
		if(row<0){
			this.messageBox("��ѡ��ĳһ������");
			return;
		}
		String sql="SELECT LUMPWORK_CODE FROM ADM_INP WHERE CASE_NO='"
			+tableP.getValue("CASE_NO", row)+"'";
		TParm check = new TParm(TJDODBTool.getInstance().select(sql));		
		if(check.getValue("LUMPWORK_CODE",0)==null 
				|| check.getValue("LUMPWORK_CODE",0).length()<=0){
			this.messageBox("�þ���Ų������ײ�");
			return;
		}
		String patName = tableP.getValue("PAT_NAME", row);
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("E_DATE")), "yyyyMMdd");
		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		TParm parm = getPrintLumpwork(startTime,endTime);//�������
//		System.out.println("parm::::"+parm);
		String sDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyy/MM/dd");
		String eDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("E_DATE")), "yyyy/MM/dd");
		
		TParm printData = new TParm();
		printData.setData("TITLE", "TEXT", patName+"סԺ�����ۿ��嵥");
		printData.setData("DATE", "TEXT", sDate + " �� " + eDate);		
		printData.setData("USER_NAME", "TEXT", Operator.getName());
		printData.setData("PRINT_DATE", "TEXT", sysDate);
		printData.setData("TABLE_L", parm.getData());
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\BIL\\BILLumpworkFee_T45.jhw", printData);
		
	}

	/**
	 * ���ñ����ӡԤ������
	 */
	private void print() {
		TTable table = (TTable) this.getComponent("Table");
		int row = table.getRowCount();
		if (row < 2) {
			this.messageBox_("�Ȳ�ѯ����!");
			return;
		}
//		String startTime = StringTool.getString(TypeTool
//				.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
//		String endTime = StringTool.getString(TypeTool
//				.getTimestamp(getValue("E_DATE")), "yyyyMMdd");
		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		TParm printData = table.getParmValue();
		printData.setData("REDUCE_FLG",0,"����");
		for (int i = 0; i < printData.getCount(); i++) {
			if (i==0) {
				continue;
			}
			if (printData.getValue("REDUCE_FLG",i).equals("Y")) {
				printData.setData("REDUCE_FLG",i,"��");
			}else{
				printData.setData("REDUCE_FLG",i,"��");
			}
		}
		String sDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyy/MM/dd");
		String eDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("E_DATE")), "yyyy/MM/dd");
		TParm parm = new TParm();
		parm.setData("DATE", "TEXT", sDate + " �� " + eDate);
		parm.setData("E_DATE", eDate);
		parm.setData("OPT_USER", "TEXT", Operator.getName());
		parm.setData("printDate", "TEXT", sysDate);
		parm.setData("T1", printData.getData());
		parm.setData("TITLE", "TEXT","סԺ����ҽ�Ʒ�����ϸ��");
		parm.setData("TOT_AMT", "TEXT", "�ϼ�:  "
				+ table.getShowParmValue().getValue("TOT_AMT",
						table.getRowCount() - 2));// ===zhangp 20120824
		// System.out.println("��Ժ����ҽ�Ʒ�����ϸ��" + printData.getData());
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\IBS\\BILOutHospPatDetailFee_new.jhw", parm);
	}
	/**
	 * ��ѯ����(deptWhere)
	 * @return String
	 */
	private String deptWhere() {
		String deptWhere = "";
		if (getValue("DEPT_CODE") != null) {
			if (getValue("DEPT_CODE").toString().length() != 0)
				deptWhere = " AND D.DEPT_CODE = '" + getValue("DEPT_CODE")
						+ "'  ";
		}
		if (this.getValue("STATION_CODE") != null
				&& this.getValueString("STATION_CODE").length() > 0) {
			deptWhere += " AND D.STATION_CODE = '"
					+ this.getValueString("STATION_CODE") + "'";
		}
		// �Է�
		if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_1"))) {
			deptWhere += " AND Z.NHI_CTZ_FLG='N' ";
		}
		// ҽ��
		if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_2"))) {
			deptWhere += " AND Z.NHI_CTZ_FLG='Y' ";
			if (this.getValueString("CTZ_CODE").length() > 0) {
				deptWhere += " AND Z.CTZ_CODE='"
						+ this.getValueString("CTZ_CODE") + "' ";
			}
		}
		// ������
		if (this.getValue("MR_NO") != null
				&& this.getValueString("MR_NO").length() > 0) {
				deptWhere += " AND D.MR_NO='"
						+ this.getValueString("MR_NO") + "' ";
		}
		return deptWhere;
	}

	/**
	 * �����ӡ����
	 * 
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @return TParm
	 */
	private TParm getPrintDate(String startTime, String endTime) {
		DecimalFormat df = new DecimalFormat("##########0.00");
		TParm selParm = new TParm();
		String deptWhere = deptWhere();
//		startTime = "20150319";
//		endTime = "20150323";
		String regionWhere = "";
		if (!"".equals(Operator.getRegion()))
			regionWhere = " AND B.REGION_CODE = '" + Operator.getRegion()
					+ "' ";
		String sql = " SELECT D.MR_NO, D.IPD_NO,F.PAT_NAME,D.CASE_NO,Z.CTZ_DESC,TO_CHAR(D.IN_DATE,'yyyy/MM/dd') IN_DATE,"
				+ " TO_CHAR(D.DS_DATE,'yyyy/MM/dd') DS_DATE,D.DEPT_CODE AS EXE_DEPT_CODE,"
//				+ " SUM (CASE WHEN A.INCLUDE_FLG = 'Y' THEN A.TOT_AMT ELSE 0 END) LUMPWORK_IN_AMT,"
//				+ " SUM (CASE WHEN A.INCLUDE_FLG = 'Y' THEN 0 ELSE A.TOT_AMT END) LUMPWORK_OUT_AMT, "
				+ " SUM (A.TOT_AMT) AS TOT_AMT,A.REXP_CODE,B.DEPT_ABS_DESC,C.STATION_DESC "
				+ " FROM IBS_ORDD A,SYS_DEPT B,SYS_STATION C,ADM_INP D,SYS_PATINFO F,"
				+ " SYS_CTZ Z,IBS_ORDM G"
				+ " WHERE A.BILL_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "000000"
				+ "', 'yyyyMMddhh24miss') "
				+ "                      AND TO_DATE ('"
				+ endTime
				+ "235959"
				+ "', 'yyyyMMddhh24miss') "
				+ deptWhere
				+ regionWhere
				+ " AND D.DEPT_CODE = B.DEPT_CODE "
				+ " AND D.STATION_CODE = C.STATION_CODE(+) "
				+ " AND A.CASE_NO = D.CASE_NO "
				+ " AND D.MR_NO = F.MR_NO "
				+ " AND D.CTZ1_CODE = Z.CTZ_CODE(+) "
				+ " AND A.CASE_NO = G.CASE_NO "
				+ " AND A.CASE_NO_SEQ = G.CASE_NO_SEQ "
				+ " GROUP BY D.MR_NO,D.IPD_NO,F.PAT_NAME,D.CASE_NO,Z.CTZ_DESC,D.DEPT_CODE,"
				+ " A.REXP_CODE,B.DEPT_ABS_DESC,C.STATION_DESC,D.IN_DATE,D.DS_DATE "
				+ " ORDER BY D.DEPT_CODE, D.MR_NO";// add D.IPD_NO ����

		//System.out.println("sql====DDDDFADF==="+sql);
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selParm.getCount("EXE_DEPT_CODE") < 1) {
			// ��������
			this.messageBox("E0008");
			this.initPage();
			return selParm;
		}
		String reduceSql="SELECT  D.MR_NO, D.IPD_NO,F.PAT_NAME,D.CASE_NO,Z.CTZ_DESC,TO_CHAR(D.IN_DATE,'yyyy/MM/dd') IN_DATE ,"
				+ "TO_CHAR(D.DS_DATE,'yyyy/MM/dd') DS_DATE, D.DEPT_CODE AS EXE_DEPT_CODE,"
				+ " CASE WHEN  S.AR_AMT< 0 THEN SUM(R.REDUCE_AMT) ELSE -SUM(R.REDUCE_AMT) END AS TOT_AMT,R.REXP_CODE,B.DEPT_ABS_DESC,C.STATION_DESC" +
						" FROM ADM_INP D,SYS_DEPT B,SYS_STATION C,SYS_PATINFO F," +
						"BIL_REDUCEM M,BIL_REDUCED R, SYS_CTZ Z,BIL_IBS_RECPM S "+
						"WHERE D.CASE_NO=M.CASE_NO AND D.CASE_NO=S.CASE_NO AND D.DEPT_CODE = B.DEPT_CODE " +
						"AND D.STATION_CODE = C.STATION_CODE(+) " +
						" AND D.MR_NO = F.MR_NO AND D.CTZ1_CODE = Z.CTZ_CODE(+) " +
						" AND M.REDUCE_NO=R.REDUCE_NO AND S.REDUCE_NO=M.REDUCE_NO AND M.ADM_TYPE='I' " +
						//" AND S.RESET_RECEIPT_NO IS NULL AND S.AR_AMT>0" +
						" AND S.ACCOUNT_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "000000"
						+ "', 'yyyyMMddhh24miss') "
						+ " AND TO_DATE ('"
						+ endTime
						+ "235959"
						+ "', 'yyyyMMddhh24miss') "
						+ deptWhere
						+ regionWhere+" GROUP BY D.MR_NO, D.IPD_NO,F.PAT_NAME,D.CASE_NO,Z.CTZ_DESC,"+
                 "D.DEPT_CODE,R.REXP_CODE,B.DEPT_ABS_DESC,C.STATION_DESC,D.IN_DATE,D.DS_DATE,S.AR_AMT ORDER BY D.DEPT_CODE,D.MR_NO ";
		//System.out.println("REDUCE_SQL::DD:"+reduceSql);
		TParm reduceParm=new TParm(TJDODBTool.getInstance().select(reduceSql));
		if (reduceParm.getErrCode()<0) {
			this.messageBox("��ѯ��������");
			this.initPage();
			return reduceParm;
		}
//		System.out.println("selParm::::"+selParm);
		BILRecpChargeForLumpworkPrint endData = new BILRecpChargeForLumpworkPrint();
		// endData.setResult(selParm);
		TParm endParm = endData.getValue(selParm,false);
		BILRecpChargeForLumpworkPrint reduceData = new BILRecpChargeForLumpworkPrint();
//		System.out.println("endParm::::"+endParm);
		TParm reduceNewParm=reduceData.getValue(reduceParm,true);
		TParm resultParm = getHeard(); // ��ͷ
		int count = resultParm.getCount();
		caseNoSum="";
		for (int i = 0; i < endParm.getCount(); i++) {
			resultParm.setRowData(count, endParm, i);
			if (!caseNoSum.contains(endParm.getValue("CASE_NO",i))) {
				caseNoSum+="'"+endParm.getValue("CASE_NO",i)+"',";
			}
			count++;
			for (int j = 0; j < reduceNewParm.getCount(); j++) {
				if (endParm.getValue("CASE_NO",i).
						equals(reduceNewParm.getValue("CASE_NO",j))) {
					resultParm.setRowData(count, reduceNewParm, j);
					count++;
				}
			}
		}
		resultParm.setCount(count);
		resultParm.addData("SYSTEM", "COLUMNS", "REDUCE_FLG");
		resultParm.addData("SYSTEM", "COLUMNS", "MR_NO");
		resultParm.addData("SYSTEM", "COLUMNS", "IPD_NO");
		resultParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		resultParm.addData("SYSTEM", "COLUMNS", "IN_DATE");
		resultParm.addData("SYSTEM", "COLUMNS", "DS_DATE");
		resultParm.addData("SYSTEM", "COLUMNS", "CTZ_DESC");
		resultParm.addData("SYSTEM", "COLUMNS", "DEPT_ABS_DESC");
		resultParm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
//		resultParm.addData("SYSTEM", "COLUMNS", "LUMPWORK_IN_AMT");
//		resultParm.addData("SYSTEM", "COLUMNS", "LUMPWORK_OUT_AMT");
		resultParm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
		for (int i = 0; i < chargName.length; i++) {
			resultParm.addData("SYSTEM", "COLUMNS", chargName[i]);
		}
		// System.out.println(resultParm);
		this.callFunction("UI|Table|setParmValue", resultParm);
		return resultParm;
	}
	
	/**
	 * �����Żس��¼�
	 */
	public void onMrNo() {
		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("�޴˲�����!");
			this.setValue("MR_NO", "");
			return;
		}
		mrNo = PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
		setValue("MR_NO", mrNo);
		onQuery();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("E_DATE")), "yyyyMMdd");					
		TParm printData = this.getPrintDate(startTime, endTime);
	}

	/**
	 * ���Excel
	 */
	public void onExport() {

		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		TTable table = (TTable) callFunction("UI|Table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "�ײͽ�ת����");
	}

	/**
	 * ���
	 */
	public void onClear() {
		initPage();
		// TTable table = (TTable)this.getComponent("Table");
		// table.removeRowAll();
		this.clearValue("STATION_CODE;DEPT_CODE");
		this.clearValue("MR_NO");
		mrNo="";
		pat = new Pat();
	}

	/**
	 * ����combo�¼�
	 */
	public void onDEPT() {
		this.clearValue("STATION_CODE");
		this.callFunction("UI|STATION_CODE|onQuery");
	}

	/**
	 * ��ѡ��ť�¼�
	 * 
	 * @param type
	 *            String
	 */
	public void onRodio(String type) {
		if ("CTZ".equalsIgnoreCase(type)) {
			this.callFunction("UI|CTZ_CODE|setEnabled", true);
		} else {
			this.callFunction("UI|CTZ_CODE|setEnabled", false);
		}
	}
	
	/**
	 * �����ײ�̨�˴�ӡ����
	 * @author xiongwg20150708
	 * 
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @return TParm
	 */
	private TParm getPrintLumpwork(String startTime, String endTime) {
		DecimalFormat df = new DecimalFormat("##########0.00");
		String deptWhere = deptWhere();
		String regionWhere = "";
		if (!"".equals(Operator.getRegion()))
			regionWhere = " AND B.REGION_CODE = '" + Operator.getRegion()
					+ "' ";
		TParm result = new TParm();
		TParm init  = new TParm();//��ʼ��������
		String sqlInit = " SELECT A.REXP_CODE,H.CHN_DESC,SUM (OWN_AMT) OWN_AMT, "
				+ " SUM (CASE WHEN A.INCLUDE_FLG = 'Y' THEN A.OWN_AMT ELSE 0 END) OUT_OWN_AMT, "
				+ " SUM (CASE WHEN A.INCLUDE_FLG = 'Y' THEN 0 ELSE A.OWN_AMT END) IN_OWN_AMT, "
				+ " '' AS RATE, "
				+ " SUM (CASE WHEN A.INCLUDE_FLG = 'Y' THEN 0 ELSE A.TOT_AMT END) IN_TOT_AMT, "
				+ " SUM (TOT_AMT) TOT_AMT "
				+ " FROM IBS_ORDD A,SYS_DEPT B,SYS_STATION C,ADM_INP D,SYS_PATINFO F, "
				+ " SYS_CTZ Z,IBS_ORDM G,SYS_DICTIONARY H "
				+ " WHERE A.BILL_DATE BETWEEN TO_DATE ('" + startTime
				+ "000000" + "', 'yyyyMMddhh24miss') "
				+ "                      AND TO_DATE ('" + endTime + "235959"
				+ "', 'yyyyMMddhh24miss') " + deptWhere + regionWhere
				+ " AND A.DEPT_CODE = B.DEPT_CODE "
				+ " AND A.STATION_CODE = C.STATION_CODE(+) "
				+ " AND A.CASE_NO = D.CASE_NO " + " AND D.MR_NO = F.MR_NO "
				+ " AND D.CTZ1_CODE = Z.CTZ_CODE(+) "
				+ " AND A.CASE_NO = G.CASE_NO "
				+ " AND A.CASE_NO_SEQ = G.CASE_NO_SEQ "
				+ " AND H.GROUP_ID = 'SYS_CHARGE' "
				+ " AND A.REXP_CODE = H.ID "
				+ " GROUP BY A.REXP_CODE, H.CHN_DESC ";
		init = new TParm(TJDODBTool.getInstance().select(sqlInit));
		if (init.getCount("REXP_CODE") < 1) {
			// ��������
			this.messageBox("E0008");
			this.initPage();
			return init;
		}
		
		String sqlRecp = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY "
				+ " WHERE GROUP_ID = 'SYS_CHARGE' AND ID  LIKE '2%' ORDER BY ID";
		TParm recp = new TParm(TJDODBTool.getInstance().select(sqlRecp));
		int row = init.getCount("REXP_CODE");// �����Զ�����
		for (int i = 0; i < recp.getCount("ID"); i++) {
			int m = 0;// ���������ж�����ͬ���շ���Ŀ

			if (recp.getValue("CHN_DESC", i).equals("������")
					|| recp.getValue("CHN_DESC", i).equals("�ǿ�����"))
				continue;// �޳���������ǿ�����

			for (int j = 0; j < init.getCount("REXP_CODE"); j++) {
				if (recp.getValue("ID", i)
						.equals(init.getValue("REXP_CODE", j))) {
					m = 1;
					break;
				}
			}
			if (m == 0) {// ��ʼ���������շ���Ŀ���ݵ��Զ����ò�0
				row++;
				init.setData("REXP_CODE", row, recp.getValue("ID", i));
				init.setData("CHN_DESC", row, recp.getValue("CHN_DESC", i));
				init.setData("OWN_AMT", row, "0.00");
				init.setData("OUT_OWN_AMT", row, "0.00");
				init.setData("IN_OWN_AMT", row, "0.00");
				init.setData("RATE", row, "0.00");
				init.setData("IN_TOT_AMT", row, "0.00");
				init.setData("TOT_AMT", row, "0.00");
			}
		}

		// �ϲ������غͷǿ����ع�Ϊ��ҩ��
		double ownAmt = 0.00;// Ӧ�ս��
		double ownAmtOut = 0.00;// Ӧ��������
		double ownAmtIn = 0.00;// Ӧ�����ڽ��
		double totAmtIn = 0.00;// ʵ�����ڽ��
		double totAmt = 0.00;// ʵ�ս��
		// �ϼƷ���
		double ownAmtSum = 0.00;// Ӧ�ս��ϼ�
		double ownAmtOutSum = 0.00;// Ӧ��������ϼ�
		double ownAmtInSum = 0.00;// Ӧ�����ڽ��ϼ�
		double totAmtInSum = 0.00;// ʵ�����ڽ��ϼ�
		double totAmtSum = 0.00;// ʵ�ս��ϼ�
		// ҩƷ����,���ڼ����̯��
		double ownAmtInPha = 0.00;// Ӧ������ҩƷ���
		double totAmtInPha = 0.00;// ʵ������ҩƷ���

		// ���������ҩ����
		for(int i=0;i<init.getCount("REXP_CODE");i++){		
			if(init.getValue("CHN_DESC",i).equals("������")
					|| init.getValue("CHN_DESC",i).equals("�ǿ�����")
					|| init.getValue("CHN_DESC",i).equals("��ҩ��")){			
				ownAmt +=init.getDouble("OWN_AMT", i);
				ownAmtOut +=init.getDouble("OUT_OWN_AMT", i);
				ownAmtIn +=init.getDouble("IN_OWN_AMT", i);
				totAmtIn +=init.getDouble("IN_TOT_AMT", i);
				totAmt +=init.getDouble("TOT_AMT", i);			
				ownAmtInPha +=init.getDouble("IN_OWN_AMT", i);
				totAmtInPha +=init.getDouble("IN_TOT_AMT", i);
			}
			if(init.getValue("CHN_DESC",i).equals("�г�ҩ��")
					|| init.getValue("CHN_DESC",i).equals("�в�ҩ��")){
				ownAmtInPha +=init.getDouble("IN_OWN_AMT", i);
				totAmtInPha +=init.getDouble("IN_TOT_AMT", i);
			}
			ownAmtSum +=init.getDouble("OWN_AMT", i);
			ownAmtOutSum +=init.getDouble("OUT_OWN_AMT", i);
			ownAmtInSum +=init.getDouble("IN_OWN_AMT", i);
			totAmtInSum +=init.getDouble("IN_TOT_AMT", i);
			totAmtSum +=init.getDouble("TOT_AMT", i);
		}
		//��̯�ȼ���
		double rate=0.00;
		if(ownAmtInSum-ownAmtInPha==0.00){
			rate = 0.00;
		}else{
			rate = (totAmtInSum-totAmtInPha)/(ownAmtInSum-ownAmtInPha);
		}
		 
		//�����ܷ��÷�����ҩ��
		for(int i=0;i<init.getCount("REXP_CODE");i++){
			if(init.getValue("CHN_DESC",i).equals("��ҩ��")){
				init.setData("OWN_AMT", i, ownAmt);
				init.setData("OUT_OWN_AMT", i, ownAmtOut);
				init.setData("IN_OWN_AMT", i, ownAmtIn);
				init.setData("RATE", i ,"0.00");
				init.setData("IN_TOT_AMT", i, totAmtIn);
				init.setData("TOT_AMT", i, totAmt);
			}
			if(init.getValue("CHN_DESC",i).equals("������")
					||init.getValue("CHN_DESC",i).equals("�ǿ�����")){
				init.removeRow(i);
			}
			if(init.getValue("CHN_DESC",i).equals("��ҩ��")
					|| init.getValue("CHN_DESC",i).equals("�г�ҩ��")
					|| init.getValue("CHN_DESC",i).equals("�в�ҩ��")){
				init.setData("RATE", i, 0.00);
			}else{
				init.setData("RATE", i, rate);
			}
		}
		
		init = onChargeOrder(init);//����charge����
		//�ϼ���
		row = init.getCount("CHN_DESC");
		init.setData("REXP_CODE", row+1 ,"");
		init.setData("CHN_DESC", row+1 ,"�ϼ�");
		init.setData("OWN_AMT", row+1 , df.format(ownAmtSum));
		init.setData("OUT_OWN_AMT", row+1 , df.format(ownAmtOutSum));
		init.setData("RATE", row+1 , "");
		init.setData("IN_OWN_AMT", row+1 ,  df.format(ownAmtInSum));
		init.setData("IN_TOT_AMT", row+1 ,  df.format(totAmtInSum));
		init.setData("TOT_AMT", row+1 ,  df.format(totAmtSum));
		
		
		int count = init.getCount("CHN_DESC");
		for(int i=0;i<count;i++){
			result.addData("CHN_DESC",init.getValue("CHN_DESC", i));
			result.addData("OWN_AMT", df.format(init.getDouble("OWN_AMT", i)));
			result.addData("OUT_OWN_AMT", df.format(init.getDouble("OUT_OWN_AMT", i)));
			//��ʾ�ٷֱ�
			if(i==count-1){
				result.addData("RATE", "");
			}else{
				String rateR = df.format(init.getDouble("RATE", i) *100.00);
				result.addData("RATE", rateR+"%");
			}
			result.addData("IN_OWN_AMT", df.format(init.getDouble("IN_OWN_AMT", i)));
			result.addData("IN_TOT_AMT", df.format(init.getDouble("IN_TOT_AMT", i)));
			result.addData("TOT_AMT", df.format(init.getDouble("TOT_AMT", i)));
		}
		result.setCount(count);		
		result.addData("SYSTEM", "COLUMNS", "CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "OWN_AMT");
		result.addData("SYSTEM", "COLUMNS", "OUT_OWN_AMT");
		result.addData("SYSTEM", "COLUMNS", "IN_OWN_AMT");
		result.addData("SYSTEM", "COLUMNS", "RATE");
		result.addData("SYSTEM", "COLUMNS", "IN_TOT_AMT");
		result.addData("SYSTEM", "COLUMNS", "TOT_AMT");
//		System.out.println("result::::"+result);

		return result;
	}
	
	/**
	 * ����charge˳������
	 * 
	 * @param parm
	 *            TParm
	 * @return result TParm
	 */
	public TParm onChargeOrder(TParm parm) {
		TParm result = new TParm();
		BILRecpChargeForLumpworkPrint charge = new BILRecpChargeForLumpworkPrint();
		TParm chargeP = charge.getChargeData();
		for (int i = 0; i < 31; i++) {
			if (i < 10) {
				if (i == 2 || i == 3) {// �����غͷǿ�����
					if (i == 2) {
						for (int j = 0; j < parm.getCount("REXP_CODE"); j++) {
							if (parm.getValue("CHN_DESC", j).equals("��ҩ��")) {
								result.addRowData(parm, j);
							}
						}
					}
				} else {
					for (int j = 0; j < parm.getCount("REXP_CODE"); j++) {
						if (chargeP.getValue("CHARGE0" + (i+1), 0).equals(
								parm.getValue("REXP_CODE", j))) {
							result.addRowData(parm, j);
						}
					}
				}

			} else {//>10�����
				if (chargeP.getValue("CHARGE" + i, 0) == null
						|| chargeP.getValue("CHARGE" + i, 0).equals("")) {
					break;
				}
				for (int j = 0; j < parm.getCount("REXP_CODE"); j++) {
					if (chargeP.getValue("CHARGE" + i, 0).equals(
							parm.getValue("REXP_CODE", j))) {
						result.addRowData(parm, j);
					}
				}
			}

		}
		return result;
	}
			
	// ==========modify-begin (by wanglong 20120710)===============
	// ����Ϊ��Ӧ��굥���¼��ķ��������ڻ�ȡȫ����Ԫ���ֵ������ĳ�������Լ���ظ���������
	/**
	 * �����������������
	 * 
	 * @param table
	 *            TTable
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				int i = table.getTable().columnAtPoint(me.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				// TParm tableData = table.getParmValue();
				TParm tableData = table.getShowParmValue();
				// =====������ �� "�ܼ�"�� �����봦����======
				TParm titRowParm = new TParm();// ��¼������
				titRowParm.addRowData(table.getParmValue(), 0);
				TParm totRowParm = new TParm();// ��¼���ܼơ���
				totRowParm.addRowData(table.getParmValue(), tableData
						.getCount() - 1);
				int rowCount = tableData.getCount();// ���ݵ�������������С���к��ܼ��У�
				tableData.removeRow(0);// ȥ����һ�У������У�
				tableData.removeRow(tableData.getCount() - 1);// ȥ�����һ��(�ܼ���)
				// =========================================
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);
				comparator.setDes(ascending);
				comparator.setCol(col);
				java.util.Collections.sort(vct, comparator);
				// ��������vectorת��parm;
				TParm lastResultParm = new TParm();// ��¼���ս��
				TParm tmpParm = cloneVectoryParam(vct, new TParm(), strNames);
				lastResultParm.addRowData(titRowParm, 0);// ���������
				for (int k = 0; k < tmpParm.getCount(); k++) {
					lastResultParm.addRowData(tmpParm, k);// �����м�����
				}
				lastResultParm.addRowData(totRowParm, 0);// �����ܼ���
				lastResultParm.setCount(rowCount);
//				System.out.println("lastResultParm:\r\n" + lastResultParm
//						+ "\r\n\r\n");// //////////////////
				table.setParmValue(lastResultParm);
				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param parm
	 *            TParm
	 * @param group
	 *            String
	 * @param names
	 *            String
	 * @param size
	 *            int
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * ת��parm�е���
	 * 
	 * @param columnName
	 *            String[]
	 * @param tblColumnName
	 *            String
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * vectoryת��param
	 * 
	 * @param vectorTable
	 *            Vector
	 * @param parmTable
	 *            TParm
	 * @param columnNames
	 *            String
	 */
	// ================start===============
	// private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
	// String columnNames) {
	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// ================end=================
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		// ================start===============
		// table.setParmValue(parmTable);
		return parmTable;
		// ================end=================
		// System.out.println("�����===="+parmTable);
	}
	// ==========modify-end========================================
}
