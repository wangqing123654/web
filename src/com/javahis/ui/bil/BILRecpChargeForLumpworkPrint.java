package com.javahis.ui.bil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;


/**
 * <p>Title: Ʊ�ݶ�Ӧ(For�ײͽ�ת����)</p>
 *
 * <p>Description: Ʊ�ݶ�Ӧ(For�ײͽ�ת����)</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: Bluecore</p>
 *
 * @author xiongwg
 * @version 1.0
 */

public class BILRecpChargeForLumpworkPrint {
	/**
	 * Ʊ��
	 */
	private Map chargeMap = new HashMap();

	/**
	 * ����Ʊ��
	 * 
	 * @param chargeMap
	 *            Map
	 */
	public void setChargeMap(Map chargeMap) {
		this.chargeMap = chargeMap;
	}

	/**
	 * �õ�Ʊ��
	 * 
	 * @return Map
	 */
	public Map getChargeMap() {
		return chargeMap;
	}

	/**
	 * �վݷ�����ϸ
	 */
	private List chargeList = new ArrayList();
	public static final String CHARGE = "CHARGE01, CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07,"
			+ "CHARGE08, CHARGE09, CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14,"
			+
			// ===zhangp 20120310 modify start
			"CHARGE15, CHARGE16, CHARGE17, CHARGE18, CHARGE19,CHARGE20,CHARGE21 ";
	// ====zhangp 20120310 modify end
	/**
	 * ���ؽ����
	 */
	private TParm result = new TParm();

	/**
	 * ������
	 */
	public BILRecpChargeForLumpworkPrint() {
		initCharge();
		initChargeList();
		initResult();

	}

	/**
	 *��ʼ��chargeMap
	 */
	public void initCharge() {
		TParm recpParm = getChargeData();
		// System.out.println("Ʊ��" + recpParm);
		Vector columns = (Vector) recpParm.getData("SYSTEM", "COLUMNS");
		for (int i = 0; i < columns.size(); i++) {
			String value = (String) columns.get(i);
			String name = recpParm.getValue(value, 0);
			getChargeMap().put(name, value);
		}
	}

	/**
	 * ��ʼ��chargeList
	 */
	public void initChargeList() {
		String[] a = StringTool.parseLine(CHARGE, ",");
		int count = a.length;
		for (int i = 0; i < count; i++) {
			chargeList.add(a[i]);
		}
	}

	/**
	 * ��ʼ��result
	 */
	public void initResult() {
		result.addData("SYSTEM", "COLUMNS", "REDUCE_FLG");
		result.addData("SYSTEM", "COLUMNS", "MR_NO");
		result.addData("SYSTEM", "COLUMNS", "IPD_NO");
		result.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		result.addData("SYSTEM", "COLUMNS", "IN_DATE");
		result.addData("SYSTEM", "COLUMNS", "DS_DATE");
		result.addData("SYSTEM", "COLUMNS", "CTZ_DESC");
		result.addData("SYSTEM", "COLUMNS", "DEPT_ABS_DESC");
		result.addData("SYSTEM", "COLUMNS", "STATION_DESC");
//		result.addData("SYSTEM", "COLUMNS", "LUMPWORK_IN_AMT");
//		result.addData("SYSTEM", "COLUMNS", "LUMPWORK_OUT_AMT");
		result.addData("SYSTEM", "COLUMNS", "TOT_AMT");
		int count = chargeList.size();

		for (int i = 0; i < count; i++) {
			result.addData("SYSTEM", "COLUMNS", chargeList.get(i));

		}

	}

	/**
	 * ������ͷ��
	 * 
	 * @param recpCode
	 *            String
	 * @return String
	 */
	public String getCharge(String recpCode) {
		return (String) getChargeMap().get(recpCode);

	}

	public void setResult(TParm result) {
		this.result = result;
	}

	public TParm getResult() {
		return result;
	}

	/**
	 * ����
	 * 
	 * @param mrNo
	 *            String
	 * @param ipdNo
	 *            String
	 * @param patName
	 *            String
	 * @param ctzDesc
	 *            String
	 * @param deptDesc
	 *            String
	 * @param stationCode
	 *            String
	 */
	public void addResult(String mrNo, String ipdNo, String patName, String caseNo,
			String ctzDesc, String deptDesc, String stationCode,boolean flg,String inDate,String dsDate) {
		result.addData("REDUCE_FLG", flg);
		result.addData("MR_NO", mrNo);
		result.addData("IPD_NO", ipdNo);
		result.addData("PAT_NAME", patName);
		result.addData("IN_DATE", inDate);
		result.addData("DS_DATE", dsDate);
		result.addData("CTZ_DESC", ctzDesc);
		result.addData("CASE_NO", caseNo);
		result.addData("CTZ_DESC", ctzDesc);
		result.addData("DEPT_ABS_DESC", deptDesc);
		result.addData("STATION_DESC", stationCode);
		Iterator deptIterator = getChargeMap().keySet().iterator();
		while (deptIterator.hasNext()) {
			String name = (String) deptIterator.next();
			String value = (String) getChargeMap().get(name);
			result.addData(value, 0.00);
//			result.addData("LUMPWORK_IN_AMT", 0.00);
//			result.addData("LUMPWORK_OUT_AMT", 0.00);
		}
		result.addData("TOT_AMT", 0.00);		
		result.setCount(result.getCount("CASE_NO"));

	}

	/**
	 * ���ҿ��Ҷ�Ӧ�к�
	 * 
	 * @param ipdNo
	 *            String
	 * @return int
	 */
	public int findDept(String ipdNo, String deptDesc, String mrNo,String caseNo) {// ======pangben
																		// 2014-6-5
																		// ��Ӳ����Ų�����������������ʾ��
		// ͬ����סԺ�����У��
		if (result.getCount() <= 0) {
			return -1;
		}// �޸��жϿ��� caoyong
		if (((Vector) result.getData("CASE_NO")).indexOf(caseNo) != -1) {
			return ((Vector) result.getData("CASE_NO")).indexOf(caseNo);// =====pangben
																	// 2014-6-5
																	// ��ѯΨһ��λ��
		} else {
			return -1;
		}
	}

	/**
	 * ��ÿ��charge��ֵ
	 * 
	 * @param mrNo
	 *            String
	 * @param ipdNo
	 *            String
	 * @param patName
	 *            String
	 * @param ctzDesc
	 *            String
	 * @param deptDesc
	 *            String
	 * @param stationCode
	 *            String
	 * @param recpCode
	 *            String
	 * @param lumpInFee
	 *            double
	 * @param lumpOutFee
	 *            double
	 * @param fee
	 *            double
	 */
	public void setValue(String mrNo, String ipdNo, String patName,String caseNo,
			String ctzDesc, String deptDesc, String stationCode,
			String recpCode, double fee,boolean flg,String inDate,String dsDate) {
		int row = findDept(ipdNo, deptDesc, mrNo,caseNo);//�ҵ���ͬ�������к�
		if (row == -1) {
			this
					.addResult(mrNo, ipdNo, patName, caseNo, ctzDesc, deptDesc,
							stationCode,flg,inDate,dsDate);
			row = result.getCount() - 1;
		}
		String chargeCode = getCharge(recpCode);
		double value = result.getDouble(chargeCode, row) + fee;
		
		//��ͬ�����Ĳ������úϲ�
//		double lumpInValue = result.getDouble("LUMPWORK_IN_AMT", row) + lumpInFee;
//		double lumpOutValue = result.getDouble("LUMPWORK_OUT_AMT", row) + lumpOutFee;
		
		DecimalFormat df = new DecimalFormat("##########0.00");
		result.setData(chargeCode, row, df.format(value));
//		result.setData("LUMPWORK_IN_AMT", row, df.format(lumpInValue));
//		result.setData("LUMPWORK_OUT_AMT", row, df.format(lumpOutValue));
	}

	/**
	 * �õ�charge����
	 * 
	 * @return TParm
	 */
	public TParm getChargeData() {
		String recpSql = " SELECT " + CHARGE + "   FROM BIL_RECPPARM "
				+ "  WHERE ADM_TYPE = 'I' ";
		return new TParm(TJDODBTool.getInstance().select(recpSql));

	}

	/**
	 * ���кϼ�
	 * 
	 * @param row
	 *            int
	 */
	public void sumRowTot(int row) {
		double totAmt = 0.00;
//		double lumpInAmt = 0.00;
//        double lumpOutAmt = 0.00;
		Iterator deptIterator = getChargeMap().keySet().iterator();
		while (deptIterator.hasNext()) {
			String name = (String) deptIterator.next();
			String value = (String) getChargeMap().get(name);
			totAmt += result.getDouble(value, row);
		}
		DecimalFormat df = new DecimalFormat("##########0.00");
		result.setData("TOT_AMT", row, df.format(totAmt));
	}

	/**
	 * ���кϼ�
	 */
	public void sumTot() {
		int count = result.getCount();

		for (int i = 0; i < count; i++) {
			sumRowTot(i);
		}
	}

	/**
	 * �ܼ�
	 */
	public void allSumTot(boolean flg) {
		if (flg) {
			this.addResult("����ϼ�:", "", "", "", "", "", "",false,"","");
		}else{
			this.addResult("�ϼ�:", "", "", "", "", "", "",false,"","");
		}
		int count = result.getCount() - 1;
		DecimalFormat df = new DecimalFormat("##########0.00");
		Iterator deptIterator = getChargeMap().keySet().iterator();
		double arAmt = 0.00;
//		double lumpInAmtSum = 0.00;
//		double lumpOutAmtSum = 0.00;
//		System.out.println("result:::"+result);
//		for (int i = 0; i < count; i++){
//			lumpInAmtSum += result.getDouble("LUMPWORK_IN_AMT", i);
//			lumpOutAmtSum += result.getDouble("LUMPWORK_OUT_AMT", i);
//		}	
		while (deptIterator.hasNext()) {
			double totAmt = 0.00;
			String name = (String) deptIterator.next();
			String value = (String) getChargeMap().get(name);
			for (int i = 0; i < count; i++){
				totAmt += result.getDouble(value, i);
			}	
			result.setData(value, count, df.format(totAmt));
			arAmt = arAmt + totAmt;
		}
		result.setData("TOT_AMT", count, df.format(arAmt));
//		result.setData("LUMPWORK_IN_AMT", count, df.format(lumpInAmtSum));
//		result.setData("LUMPWORK_OUT_AMT", count, df.format(lumpOutAmtSum));
	}

	/**
	 * �������ձ�������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getValue(TParm parm,boolean flg) {
		int count = parm.getCount();
		double lumpInAmt =0.00;
		double lumpOutAmt =0.00;
		for (int i = 0; i < count; i++) {
			String mrNo = parm.getValue("MR_NO", i);
			String ipdNo = parm.getValue("IPD_NO", i);
			String patName = parm.getValue("PAT_NAME", i);
			String inDate = parm.getValue("IN_DATE", i);
			String dsDate = parm.getValue("DS_DATE", i);
			String caseNo = parm.getValue("CASE_NO", i);
			String ctzDesc = parm.getValue("CTZ_DESC", i);
			String deptCode = parm.getValue("DEPT_ABS_DESC", i);
			String recpCode = parm.getValue("REXP_CODE", i);
			String stationCode = parm.getValue("STATION_DESC", i);				
//			lumpInAmt = parm.getDouble("LUMPWORK_IN_AMT", i);
//			lumpOutAmt = parm.getDouble("LUMPWORK_OUT_AMT", i);		
			double totAmt = parm.getDouble("TOT_AMT", i);
			this.setValue(mrNo, ipdNo, patName, caseNo,ctzDesc, deptCode, stationCode,
					recpCode, totAmt,flg,inDate,dsDate);
		}
		this.sumTot(); // ���кϼ�
		this.allSumTot(flg); // �ܼ�
		return getResult();
	}
}
