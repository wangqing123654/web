package com.javahis.ui.iva;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.ekt.EKTIO;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDSObject;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

import jdo.sys.PatTool;
import jdo.sys.Pat;

/**
 * <p>
 * Title: ������Һ������ҩ����
 * </p>
 * 
 * <p>
 * Description: ������Һ������ҩ����
 * </p>
 * 
 * <p>
 * Copyright: javahis 2015
 * </p>
 * 
 * <p>
 * Company:javahis
 * </p>
 * 
 * @author wangjc
 * @version 1.0
 */
public class IvaReturnRegistControl extends TControl {
	// ����TABLE;��ҩTABLE��ҽ����ϸTABLE
	private TTable tblPat, tblRtn, tblRtnDtl;
	// Y����
	public static final String Y = "Y";
	// N����
	public static final String N = "N";
	// ���ַ���
	public static final String NULL = "";
	// ��ҩ����
	private TDS detail = new TDS();
	// ��ҩҽ����ϸ����
	private TDS detailD = new TDS();
	// ��������
	private TDSObject jdoObj = new TDSObject();
	// ִ�п���
	public String execOrg;
	// �Ƿ����
	public boolean nsCheck;
	// ִ���б�
	public List execList = new ArrayList();
	// ���ݶ���
	public TDS tds;
	// �Ƿ񱣴�ע��
	public boolean isSave = false;
	// �Ƿ�༭ע��
	public boolean isEdit = false;
	// �״ε���ע��
	public boolean firstClick = true;
	// �����������table�Ǹ�������ˣ�0Ϊ�����table��1Ϊ�����table
	public int upOrDown = 0;
	// ��ֵ
	private double oldValue = 0.0;

	/**
	 * ��ʼ������
	 */
	public void onInit() {

		super.onInit();
		tblPat = (TTable) this.getComponent("TBL_PAT");
		tblRtn = (TTable) this.getComponent("TBL_RTN");
		tblRtnDtl = (TTable) this.getComponent("TBL_RTNDTL");
		String sql = "SELECT * FROM SYS_STATION WHERE STATION_CODE='"
				+ Operator.getStation() + "' ";
		execOrg = (new TParm(TJDODBTool.getInstance().select(sql))).getValue(
				"ORG_CODE", 0);
		nsCheck = (new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM ODI_SYSPARM"))).getBoolean("NS_CHECK_FLG", 0);
		tblRtnDtl.addEventListener("TBL_RTNDTL->" + TTableEvent.CHANGE_VALUE,
				this, "onClickDtl");
		tblRtnDtl.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxClick");
		onClear();
	}

	/**
	 * ��ն���
	 */
	public void onClear() {
		this.setValue("START_DATE", StringTool.rollDate(SystemTool
				.getInstance().getDate(), -7));
		this.setValue("END_DATE", SystemTool.getInstance().getDate());
		this.setValue("RTN_IND", execOrg);
		this.setValue("STATION", Operator.getStation());
		this.setValue("STA", Y);
		this.setValue("NO", NULL);
		this.setValue("NAME", NULL);
		this.setValue("UN_DONE", Y);
        this.callFunction("UI|DC_RDBTN|setEnabled", true);// add by wanglong 20130628
        this.callFunction("UI|ALL_RDBTN|setEnabled", true);
        this.callFunction("UI|DC_RDBTN|setSelected", true);
		this.callFunction("UI|LBL_RT|setVisible", false);
		this.setValue("RTN_NO", "");
		this.callFunction("UI|RTN_NO|setVisible", false);
		this.callFunction("UI|BED_NO|setVisible", false);
		this.callFunction("UI|NO|setVisible", true);
		tblPat.removeRow();
		tblRtn.removeRow();
		tblRtnDtl.removeRow();
		// this.setValue("RTN_IND", execOrg);
		// TTextFormat start=(TTextFormat)this.getComponent("START_DATE");
		// start.setEnabled(true);
		// start=(TTextFormat)this.getComponent("END_DATE");
		// start.setEnabled(true);
		execList = new ArrayList();
		upOrDown = 0;
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		// ����mr_no �Զ�����0 luhai 2012-04-16 begin
		TRadioButton tb = (TRadioButton) this.getComponent("MR");
		if ("true".equals(tb.getValue())) {
			this.setValue("NO", PatTool.getInstance().checkMrno(
					this.getValue("NO") + ""));
		}
		// ����mr_no �Զ�����0 luhai 2012-04-16 end
		// luhai 2012-04-07 add ��֤ begin
		if ("".equals(this.getValueString("RTN_IND"))) {
			this.messageBox("��ѡ����ҩҩ����");
			return;
		}
		// luhai 2012-04-07 add ��֤ end
		/*
		 * ִ,40,boolean;����,80,left;����,80,left;
		 * ������,100,left;��ʿվ,120,STATION,left;������,120,left;סԺ��,120,left
		 */
		String where = getWhere();
		System.out.println("��ҩ�Ǽ�:"+where);
		TParm parm = new TParm(TJDODBTool.getInstance().select(where));
		tblPat.setParmValue(parm);
		tblRtnDtl.removeRowAll();
		tblRtn.removeRowAll();
		execList = new ArrayList();
		isEdit = false;
		isSave = false;
		upOrDown = 0;
	}

	/**
	 * ƴװSQL��WHERE����
	 * 
	 * @return String
	 */
	public String getWhere() {
		StringBuffer result = new StringBuffer();
		String no = "";
		String startDate = this.getValueString("START_DATE");
		startDate = startDate.substring(0, startDate.lastIndexOf(" "));
		String endDate = this.getValueString("END_DATE");
		endDate = endDate.substring(0, endDate.lastIndexOf(" "));
		if (TypeTool.getBoolean(this.getValue("UN_DONE"))) {// δ���
			result
					.append(" (A.BILL_DATE >=TO_DATE('"
							+ startDate
							+ " 000000','YYYY-MM-DD HH24MISS') AND A.BILL_DATE <=TO_DATE('"
							+ endDate + " 235959','YYYY-MM-DD HH24MISS'))");
			result.append(" AND A.EXE_DEPT_CODE='"
					+ this.getValueString("RTN_IND") + "' ");// ��ҩ����
		}
		// ��ҩ��ɺ�δ��� ��Ҫ����ʱ������ by liyh 20120806
		else {
			result
					.append(" AND  (A.DSPN_DATE >=TO_DATE('"
							+ startDate
							+ " 000000','YYYY-MM-DD HH24MISS') AND A.DSPN_DATE <=TO_DATE('"
							+ endDate + " 235959','YYYY-MM-DD HH24MISS'))");
			result.append(" AND A.EXEC_DEPT_CODE='"
					+ this.getValueString("RTN_IND") + "' ");
		}

		// ѡȡ��ʿվ
		if (Y.equalsIgnoreCase(this.getValueString("STA"))) {
			String stationCode = this.getValueString("STATION");
			if (!StringUtil.isNullString(stationCode)) {
				result.append(" AND A.STATION_CODE='" + stationCode + "' ");
			}
		} else {
			// ѡȡ������
			if (Y.equalsIgnoreCase(this.getValueString("MR"))) {
				no = this.getValueString("NO");
				if (!StringUtil.isNullString(no)){
					no = PatTool.getInstance().checkMrno(no);
				}
				result.append(" AND C.MR_NO='" + no + "' ");
			}
			// ѡȡ������
			else {
				// this.messageBox_(this.getValueString("BED_NO"));
				result.append(" AND C.BED_NO='" + this.getValueString("BED_NO")
						+ "' ");
			}
		}
		String sql = "";
		if (TypeTool.getBoolean(this.getValue("UN_DONE"))) {
			// ===========pangben modify 20110511 start
			String region = "";
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region = " AND C.REGION_CODE='" + Operator.getRegion() + "' ";
			}
			// ===========pangben modify 20110511 stop

			// luhai 2012-04-13 begin ���봲λ����
			// sql =
			// "SELECT DISTINCT C.CASE_NO,'N' AS EXEC,C.BED_NO,B.PAT_NAME,A.STATION_CODE,C.MR_NO,C.IPD_NO"
			// +
			// "  FROM IBS_ORDD A,SYS_PATINFO B,SYS_BED C" +
			// " WHERE # " +
			// "	AND A.CASE_NO=C.CASE_NO" +
			// " 	AND B.MR_NO=C.MR_NO  " +
			// "   AND (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y') " +
			// "	AND (C.BED_OCCU_FLG IS  NULL OR C.BED_OCCU_FLG='N') " +
			// "   AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')";
			// sql += region; //=pangben modify 20110512
			// sql = sql.replaceFirst("#", result.toString());
			sql = "SELECT DISTINCT C.CASE_NO,'N' AS EXEC,C.BED_NO_DESC,B.PAT_NAME,A.STATION_CODE,C.MR_NO,C.IPD_NO,C.BED_NO"
					+ "  FROM IBS_ORDD A,SYS_PATINFO B,SYS_BED C"
					+ " WHERE # "
					+ "	AND A.CASE_NO=C.CASE_NO"
					+ " 	AND B.MR_NO=C.MR_NO  "
					+ "   AND (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y') "
					+ "	AND (C.BED_OCCU_FLG IS  NULL OR C.BED_OCCU_FLG='N') "
					+ "   AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')";

			sql += region; // =pangben modify 20110512
			sql = sql.replaceFirst("#", result.toString());
			sql += " ORDER BY C.BED_NO  ";
			// luhai 2012-04-13 end
			// System.out.println("unDone.sql=" + sql);
		} else {
			// ===========pangben modify 20110511 start
			String region = "";
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
			}
			// ===========pangben modify 20110511 stop
			// luhai 2012-04-13 modify ���봲λ���� begin
			// sql =
			// "SELECT DISTINCT 'Y' AS EXEC,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO,A.CASE_NO,A.RTN_NO,A.RTN_NO_SEQ"
			// +
			// "	FROM ODI_DSPNM A, SYS_PATINFO B,SYS_BED C" +
			// "	WHERE A.CASE_NO=C.CASE_NO" +
			// "		  AND A.MR_NO=B.MR_NO" +
			// "   	  AND (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y') " +
			// "		  AND (C.BED_OCCU_FLG IS  NULL OR C.BED_OCCU_FLG='N') " +
			// "   	  AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')"
			// +
			// "		  AND A.RTN_NO IS NOT NULL " +
			// "		  AND (A.PHA_RETN_CODE IS NULL OR A.PHA_RETN_CODE='')" +
			// region + //=pangben modify 20110512
			// "	ORDER BY A.RTN_NO_SEQ";
			sql = "SELECT DISTINCT 'Y' AS EXEC,C.BED_NO_DESC,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO,A.CASE_NO,'' AS RTN_NO ,'' AS RTN_NO_SEQ,A.BED_NO "
					+ // ��ѯ��ҩ�嵥 �ǰ���case_no����ģ������û�б�Ҫ�� RTN_NO_SEQ, by liyh
						// 20120803
					"	FROM ODI_DSPNM A, SYS_PATINFO B,SYS_BED C"
					+ "	WHERE A.CASE_NO=C.CASE_NO"
					+ "		  AND A.MR_NO=B.MR_NO"
					+
					/*
					 * "   	  AND (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y') "
					 * +
					 * "		  AND (C.BED_OCCU_FLG IS  NULL OR C.BED_OCCU_FLG='N') "
					 * +
					 * "   	  AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')"
					 * + "		  AND A.RTN_NO IS NOT NULL " +
					 * "		  AND (A.PHA_RETN_CODE IS NULL OR A.PHA_RETN_CODE='')"
					 * +
					 */
					"   	  AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')"
					+ "       AND A.DSPN_KIND= 'RT' AND A.IVA_FLG='Y' " + result.toString() + // ��ҩ��ɺ�δ���
																			// ��Ҫ����ʱ������
																			// by
																			// liyh
																			// 20120806
					region + // =pangben modify 20110512
					"	ORDER BY A.BED_NO ";
			// luhai 2012-04-13 modify ���봲λ���� end
			// System.out.println("done.sql=" + sql);
		}
		//System.out.println("��ѯsql:"+sql);
		return sql;
	}

	/**
	 * ����table����¼������δ����ҩ������IBS_ORDD�в�ѯ��ϸ���������ҩ�������ODI_DSPNM�в�ѯ��ҩ��
	 */
	public void onPatClick() {
		for (int i = 0; i < tblPat.getRowCount(); i++) {
			tblPat.setValueAt(false, i, 0);
		}
		tblPat.setValueAt(true, tblPat.getSelectedRow(), 0);
		String startDate = this.getValueString("START_DATE");
		startDate = startDate.substring(0, startDate.lastIndexOf(" "))
				+ " 000000";
		String endDate = this.getValueString("END_DATE");
		endDate = endDate.substring(0, endDate.lastIndexOf(" ")) + " 235959";
		String caseNo = TCM_Transform.getString(tblPat.getParmValue().getValue(
				"CASE_NO", tblPat.getSelectedRow()));
		//����--------����
		//System.out.println("ѡ�е�casenoΪ��"+caseNo);
		
		String sql = "";
		// ����,����SQL �������� by liyh 20120529 start
		String stationCodeSQL = " ";
		String stationCode = TCM_Transform.getString(tblPat.getParmValue()
				.getValue("STATION_CODE", tblPat.getSelectedRow()));
		if (null != stationCode && !"".equals(stationCode)) {
			stationCodeSQL = " AND A.STATION_CODE='" + stationCode + "' ";
		}
		// ����SQL �������� by liyh 20120529 end
		if (TypeTool.getBoolean(this.getValue("UN_DONE"))) {
			// ===========pangben modify 20110512 start
			String region = "";
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";//modify by wanglong 20130628
			}
			// ===========pangben modify 201105121 stop
			// ***************************************************************************************************
			// luhai 2012-2-1 ����ҩ����ϸ��λ��ÿһ����ҩ��������ҩʱ��Ҫ���ݲ�������ҩ��Ϣ��λ��ûһ��batchSeq
			// begin
			// ***************************************************************************************************
			// luhai modify 2012-04-13 begin ������odi_dspnm begin
			// sql =
			// "SELECT   'N' EXEC,B.ORDER_DESC || B.GOODS_DESC|| ' (' || B.SPECIFICATION || ')' ORDER_DESC,SUM (A.DOSAGE_QTY) RTN_DOSAGE_QTY,A.ORDER_CODE,"
			// +
			// "           A.DOSAGE_UNIT UNIT_DESC,'' TRANSMIT_RSN_CODE,0 CANCEL_DOSAGE_QTY,'' CANCELRSN_CODE,A.CASE_NO,A.OWN_PRICE,A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,"
			// +
			// "           C.VS_DR_CODE , C.DEPT_CODE, D.DOSE_CODE, D.GIVEBOX_FLG, D.PHA_TYPE, D.ROUTE_CODE "
			// +
			// "    FROM   IBS_ORDD A, SYS_FEE B,ADM_INP C, PHA_BASE D, ODI_DSPNM E "
			// +
			// "   WHERE   A.CASE_NO = '" + caseNo + "'" +
			// "           AND A.ORDER_CODE = B.ORDER_CODE" +
			// "	    AND A.CASE_NO=C.CASE_NO" +
			// "	    AND C.DS_DATE IS NULL" +
			// "	    AND (C.CANCEL_FLG IS NULL OR C.CANCEL_FLG ='N')" +
			// "	    AND A.ORDER_CAT1_CODE IN('PHA_W','PHA_C')" +
			// "           AND A.ORDER_CODE = D.ORDER_CODE " +
			// "           AND A.CASE_NO = E.CASE_NO(+) " +//luhai 2012-04-13
			// ����������
			// "           AND A.ORDER_NO = E.ORDER_NO(+) " +//luhai 2012-04-13
			// ����������
			// "           AND A.ORDER_SEQ = E.ORDER_SEQ(+) " +//luhai
			// 2012-04-13 ����������
			// //"           AND E.DC_DATE IS NOT NULL "
			// // region + //======pangben modify 20110512 //ɾregion
			// "	GROUP BY   A.ORDER_CODE," +
			// "           A.DOSAGE_UNIT," +
			// "           B.ORDER_DESC," +
			// "           B.SPECIFICATION," +
			// "	    B.GOODS_DESC," +
			// "           A.DOSAGE_UNIT," +
			// "           A.CASE_NO," +
			// "	    A.OWN_PRICE," +
			// "	    A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE, "
			// +
			// "           C.VS_DR_CODE, C.DEPT_CODE, D.DOSE_CODE, D.GIVEBOX_FLG,D.PHA_TYPE,D.ROUTE_CODE ";
			// System.out.println("me.sql=" + sql);
//			sql = "SELECT   'N' EXEC,B.ORDER_DESC || B.GOODS_DESC|| ' (' || B.SPECIFICATION || ')' ORDER_DESC,SUM (A.DOSAGE_QTY) RTN_DOSAGE_QTY,A.ORDER_CODE,"
//					+ "           A.DOSAGE_UNIT UNIT_DESC,'' TRANSMIT_RSN_CODE,0 CANCEL_DOSAGE_QTY,'' CANCELRSN_CODE,A.CASE_NO,A.OWN_PRICE,A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,"
//					+ "           C.VS_DR_CODE , C.DEPT_CODE, D.DOSE_CODE, D.GIVEBOX_FLG, D.PHA_TYPE, D.ROUTE_CODE, B.NHI_PRICE,  B.ADDPAY_RATE,a.SEQ_NO,a.CASE_NO_SEQ,e.DOSE_TYPE  "
//					+ // update by liyh 20120525 :add cloumn
//						// NHI_PRICE��DISCOUNT_RATE �� DOSE_TYPE �� IBS_CASE_NO_SEQ
//						// ��IBS_SEQ_NO
//					"    FROM   IBS_ORDD A, SYS_FEE B,ADM_INP C, PHA_BASE D,pha_dose e "
//					+ // , ODI_DSPNM E
//					"   WHERE   A.CASE_NO = '"
//					+ caseNo
//					+ "'"
//					+ "           AND A.ORDER_CODE = B.ORDER_CODE"
//					+ "	    AND A.CASE_NO=C.CASE_NO"
//					+ "	    AND C.DS_DATE IS NULL"
//					+ "     and d.DOSE_CODE=e.DOSE_CODE "
//					+ "	    AND (C.CANCEL_FLG IS NULL OR C.CANCEL_FLG ='N')"
//					+ "	    AND A.ORDER_CAT1_CODE IN('PHA_W','PHA_C')"
//					+ " "
//					+ stationCodeSQL
//					+ " "
//					+ "           AND A.ORDER_CODE = D.ORDER_CODE and d.DOSE_CODE=e.DOSE_CODE "
//					+
//					// "           AND A.CASE_NO = E.CASE_NO(+) " +//luhai
//					// 2012-04-13 ����������
//					// "           AND A.ORDER_NO = E.ORDER_NO(+) " +//luhai
//					// 2012-04-13 ����������
//					// "           AND A.ORDER_SEQ = E.ORDER_SEQ(+) " +//luhai
//					// 2012-04-13 ����������
//					// "           AND E.DC_DATE IS NOT NULL "
//					// region + //======pangben modify 20110512 //ɾregion
//					"	GROUP BY   A.ORDER_CODE,"
//					+ "           A.DOSAGE_UNIT,"
//					+ "           B.ORDER_DESC,"
//					+ "           B.SPECIFICATION,"
//					+ "	    B.GOODS_DESC,"
//					+ "           A.DOSAGE_UNIT,"
//					+ "           A.CASE_NO,"
//					+ "	    A.OWN_PRICE,"
//					+ "	    A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE, "
//					+ "           C.VS_DR_CODE, C.DEPT_CODE, D.DOSE_CODE, D.GIVEBOX_FLG,D.PHA_TYPE,D.ROUTE_CODE, B.NHI_PRICE,  B.ADDPAY_RATE,a.SEQ_NO,a.CASE_NO_SEQ,e.DOSE_TYPE  ";
			// luhai modify 2012-04-13 begin ������odi_dspnm end
            if (this.getValueBoolean("DC_RDBTN")) {// DCҽ�� add by wanglong 20130628
                sql =
                        "SELECT 'N' EXEC,B.ORDER_DESC||B.GOODS_DESC||' ('||B.SPECIFICATION||')' ORDER_DESC,SUM(A.DOSAGE_QTY-A.RT_REQTY) RTN_DOSAGE_QTY,"
                                + "A.ORDER_CODE,A.DOSAGE_UNIT UNIT_DESC,'' TRANSMIT_RSN_CODE,0 CANCEL_DOSAGE_QTY,'' CANCELRSN_CODE,A.CASE_NO,"
                                + "A.OWN_PRICE,A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,C.VS_DR_CODE,C.DEPT_CODE,"
                                + "D.DOSE_CODE,D.GIVEBOX_FLG,D.PHA_TYPE,A.ORDER_NO,A.ORDER_SEQ,D.ROUTE_CODE,B.NHI_PRICE,B.ADDPAY_RATE,E.DOSE_TYPE,"
                                + "CASE A.DSPN_KIND WHEN 'F' THEN 'UD' ELSE A.DSPN_KIND END AS DSPN_KIND,A.START_DTTM "
                                //add by wukai on 20170313 start
                                + ", A.IBS_CASE_NO_SEQ, A.IBS_SEQ_NO "
                                //add by wukai on 20170313 end
                                + "  FROM ODI_DSPNM A, SYS_FEE B, ADM_INP C, PHA_BASE D, PHA_DOSE E "
                                + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                                + "   AND A.CASE_NO = C.CASE_NO "
                                + "   AND C.DS_DATE IS NULL "
                                + "   AND D.DOSE_CODE = E.DOSE_CODE "
                                + "   AND A.PHA_DOSAGE_DATE IS NOT NULL "
                                + "   AND (A.DC_DATE IS NOT NULL AND (A.DSPN_KIND = 'F' OR A.DSPN_KIND = 'UD')) "
                                + "   AND (A.DOSAGE_QTY > A.RT_REQTY OR A.RT_REQTY IS NULL) "
                                + "   AND A.IVA_FLG = 'Y' "//20150506 wangjingchun add PIVAs����
                                + "   AND (B.IS_REMARK <> 'Y' OR B.IS_REMARK IS NULL) "// wanglong add 20140725 ���˵�Ϊ��ע��ҽ�������ֶ���Ĭ��ֵ�������ʾ�Ǳ�ע��
                                + "   AND (C.CANCEL_FLG IS NULL OR C.CANCEL_FLG = 'N') "
                                + "   AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C') "
                                + "   AND A.CASE_NO = '#'  #  #  "
                                + "   AND A.ORDER_CODE = D.ORDER_CODE "
                                + "   AND D.DOSE_CODE = E.DOSE_CODE "
                                + "GROUP BY A.ORDER_CODE,A.DOSAGE_UNIT,B.ORDER_DESC,B.SPECIFICATION,B.GOODS_DESC,A.DOSAGE_UNIT,A.CASE_NO,A.OWN_PRICE,"
                                + "         A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,C.VS_DR_CODE,C.DEPT_CODE,D.DOSE_CODE,"
                                + "         D.GIVEBOX_FLG,D.PHA_TYPE,D.ROUTE_CODE,B.NHI_PRICE,B.ADDPAY_RATE,E.DOSE_TYPE,A.ORDER_NO,A.ORDER_SEQ,A.DSPN_KIND,A.START_DTTM,A.IBS_CASE_NO_SEQ,A.IBS_SEQ_NO "
                                + " UNION "
                                + "SELECT 'N' EXEC,B.ORDER_DESC||B.GOODS_DESC||' ('||B.SPECIFICATION||')' ORDER_DESC,SUM(A.DOSAGE_QTY-A.RT_REQTY) RTN_DOSAGE_QTY,"
                                + "       A.ORDER_CODE,A.DOSAGE_UNIT UNIT_DESC,'' TRANSMIT_RSN_CODE,0 CANCEL_DOSAGE_QTY,'' CANCELRSN_CODE,A.CASE_NO,A.OWN_PRICE,"
                                + "       A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,C.VS_DR_CODE,C.DEPT_CODE,D.DOSE_CODE,D.GIVEBOX_FLG,D.PHA_TYPE,"
                                + "       A.ORDER_NO,A.ORDER_SEQ,D.ROUTE_CODE,B.NHI_PRICE,B.ADDPAY_RATE,E.DOSE_TYPE,A.DSPN_KIND,A.START_DTTM "
                                //add by wukai on 20170313 start
                                + ", A.IBS_CASE_NO_SEQ, A.IBS_SEQ_NO "
                                //add by wukai on 20170313 end
                                + "  FROM ODI_DSPNM A, SYS_FEE B, ADM_INP C, PHA_BASE D, PHA_DOSE E "
                                + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                                + "   AND A.CASE_NO = C.CASE_NO "
                                + "   AND C.DS_DATE IS NULL "
                                + "   AND D.DOSE_CODE = E.DOSE_CODE "
                                + "   AND A.PHA_DOSAGE_DATE IS NOT NULL "
                                + "   AND (A.DC_NS_CHECK_DATE IS NOT NULL AND (A.DSPN_KIND = 'ST' OR A.DSPN_KIND = 'DS')) "
                                + "   AND (A.DOSAGE_QTY > A.RT_REQTY OR A.RT_REQTY IS NULL) "
                                + "   AND A.IVA_FLG = 'Y' "//20150506 wangjingchun add PIVAs����
                                + "   AND (B.IS_REMARK <> 'Y' OR B.IS_REMARK IS NULL) "// wanglong add 20140725 ���˵�Ϊ��ע��ҽ�������ֶ���Ĭ��ֵ�������ʾ�Ǳ�ע��
                                + "   AND (C.CANCEL_FLG IS NULL OR C.CANCEL_FLG = 'N') "
                                + "   AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C') "
                                + "   AND A.CASE_NO = '#'  #  #  "
                                + "   AND A.ORDER_CODE = D.ORDER_CODE "
                                + "   AND D.DOSE_CODE = E.DOSE_CODE "
                                + "GROUP BY A.ORDER_CODE,A.DOSAGE_UNIT,B.ORDER_DESC,B.SPECIFICATION,B.GOODS_DESC,A.DOSAGE_UNIT,A.CASE_NO,A.OWN_PRICE,"
                                + "         A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,C.VS_DR_CODE,C.DEPT_CODE,D.DOSE_CODE,D.GIVEBOX_FLG,"
                                + "         D.PHA_TYPE,D.ROUTE_CODE,B.NHI_PRICE,B.ADDPAY_RATE,E.DOSE_TYPE,A.DSPN_KIND,A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM, A.IBS_CASE_NO_SEQ,A.IBS_SEQ_NO";
                sql = sql.replaceFirst("#", caseNo);
                sql = sql.replaceFirst("#", stationCodeSQL);
                sql = sql.replaceFirst("#", region);
                sql = sql.replaceFirst("#", caseNo);
                sql = sql.replaceFirst("#", stationCodeSQL);
                sql = sql.replaceFirst("#", region);
            } else {// ȫ��ҽ��
                sql =
                        "SELECT EXEC,ORDER_DESC,SUM(RTN_DOSAGE_QTY) RTN_DOSAGE_QTY,ORDER_CODE,UNIT_DESC,TRANSMIT_RSN_CODE,CANCEL_DOSAGE_QTY,"
                                + "CANCELRSN_CODE,CASE_NO,OWN_PRICE,ORDER_CAT1_CODE,CAT1_TYPE,IPD_NO,MR_NO,BED_NO,STATION_CODE,VS_DR_CODE,DEPT_CODE,"
                                + "DOSE_CODE,GIVEBOX_FLG, PHA_TYPE,ROUTE_CODE,NHI_PRICE,ADDPAY_RATE,DOSE_TYPE,DSPN_KIND,IBS_CASE_NO_SEQ,IBS_SEQ_NO "
                                + " ,START_DTTM,ORDER_NO,ORDER_SEQ "//add by wangjc 20170718
                                + "  FROM (SELECT 'N' EXEC,B.ORDER_DESC||B.GOODS_DESC||' ('||B.SPECIFICATION||')' ORDER_DESC,SUM(A.DOSAGE_QTY) RTN_DOSAGE_QTY,"
                                + "               A.ORDER_CODE,A.DOSAGE_UNIT UNIT_DESC,'' TRANSMIT_RSN_CODE,0 CANCEL_DOSAGE_QTY,'' CANCELRSN_CODE,A.CASE_NO,"
                                + "               A.OWN_PRICE,A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,C.VS_DR_CODE,C.DEPT_CODE,"
                                + "               D.DOSE_CODE,D.GIVEBOX_FLG,D.PHA_TYPE,D.ROUTE_CODE,B.NHI_PRICE,B.ADDPAY_RATE,E.DOSE_TYPE,"
                                + "               CASE A.DSPN_KIND WHEN 'F' THEN 'UD' ELSE A.DSPN_KIND END AS DSPN_KIND "
                                   //add by wukai on 20170313 start
                                + ", A.IBS_CASE_NO_SEQ, A.IBS_SEQ_NO "
                                   //add by wukai on 20170313 end
                                + " ,A.START_DTTM,A.ORDER_NO,A.ORDER_SEQ "//add by wangjc 20170718
                                + "          FROM ODI_DSPNM A, SYS_FEE B, ADM_INP C, PHA_BASE D, PHA_DOSE E "
                                + "         WHERE A.ORDER_CODE = B.ORDER_CODE "
                                + "           AND A.CASE_NO = C.CASE_NO "
                                + "           AND C.DS_DATE IS NULL "
                                + "           AND D.DOSE_CODE = E.DOSE_CODE "
                                + "           AND A.DSPN_KIND <> 'RT' "
                                + "           AND A.PHA_DOSAGE_DATE IS NOT NULL "
                                + "   		  AND A.IVA_FLG = 'Y' "//20150506 wangjingchun add PIVAs����
                                + "           AND (C.CANCEL_FLG IS NULL OR C.CANCEL_FLG = 'N') "
                                + "           AND (B.IS_REMARK <> 'Y' OR B.IS_REMARK IS NULL) "// wanglong add 20140725 ���˵�Ϊ��ע��ҽ�������ֶ���Ĭ��ֵ�������ʾ�Ǳ�ע��
                                + "           AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C') "
                                + "           AND A.CASE_NO = '#'  #  #  "
                                + "           AND A.ORDER_CODE = D.ORDER_CODE "
                                + "           AND D.DOSE_CODE = E.DOSE_CODE "
                                + "      GROUP BY A.ORDER_CODE,A.DOSAGE_UNIT,B.ORDER_DESC,B.SPECIFICATION,B.GOODS_DESC,A.DOSAGE_UNIT,A.CASE_NO,A.OWN_PRICE,A.ORDER_CAT1_CODE,"
                                + "               B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,C.VS_DR_CODE,C.DEPT_CODE,D.DOSE_CODE,D.GIVEBOX_FLG,D.PHA_TYPE,"
                                + "               D.ROUTE_CODE,B.NHI_PRICE,B.ADDPAY_RATE,E.DOSE_TYPE,A.DSPN_KIND,A.IBS_CASE_NO_SEQ, A.IBS_SEQ_NO "
                                + " ,A.START_DTTM,A.ORDER_NO,A.ORDER_SEQ "//add by wangjc 20170718
                                + "        UNION "
                                + "        SELECT 'N' EXEC,B.ORDER_DESC||B.GOODS_DESC||' ('||B.SPECIFICATION||')' ORDER_DESC,SUM(-A.RTN_DOSAGE_QTY) RTN_DOSAGE_QTY,"
                                + "               A.ORDER_CODE,A.RTN_DOSAGE_UNIT UNIT_DESC,'' TRANSMIT_RSN_CODE,0 CANCEL_DOSAGE_QTY,'' CANCELRSN_CODE,"
                                + "               A.CASE_NO,A.OWN_PRICE,A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,C.VS_DR_CODE,"
                                + "               C.DEPT_CODE,D.DOSE_CODE,D.GIVEBOX_FLG,D.PHA_TYPE,D.ROUTE_CODE,B.NHI_PRICE,B.ADDPAY_RATE,E.DOSE_TYPE,"
                                + "               CASE A.RT_KIND WHEN 'F' THEN 'UD' ELSE A.RT_KIND END AS DSPN_KIND "
                                	//add by wukai on 20170313 start
                                + ", A.IBS_CASE_NO_SEQ, A.IBS_SEQ_NO "
                                   //add by wukai on 20170313 end
                                + " ,A.START_DTTM,A.ORDER_NO,A.ORDER_SEQ "//add by wangjc 20170718
                                + "          FROM ODI_DSPNM A, SYS_FEE B, ADM_INP C, PHA_BASE D, PHA_DOSE E "
                                + "         WHERE A.ORDER_CODE = B.ORDER_CODE "
                                + "           AND A.CASE_NO = C.CASE_NO "
                                + "           AND C.DS_DATE IS NULL "
                                + "           AND D.DOSE_CODE = E.DOSE_CODE "
                                + "           AND A.DSPN_KIND = 'RT' "
                                + "   		  AND A.IVA_FLG = 'Y' "//20150506 wangjingchun add PIVAs����
                                + "           AND (C.CANCEL_FLG IS NULL OR C.CANCEL_FLG = 'N') "
                                + "           AND (B.IS_REMARK <> 'Y' OR B.IS_REMARK IS NULL) "// wanglong add 20140725 ���˵�Ϊ��ע��ҽ�������ֶ���Ĭ��ֵ�������ʾ�Ǳ�ע��
                                + "           AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C') "
                                + "           AND A.CASE_NO = '#'  #  #  "
                                + "           AND A.ORDER_CODE = D.ORDER_CODE "
                                + "           AND D.DOSE_CODE = E.DOSE_CODE "
                                + "        GROUP BY A.ORDER_CODE,A.DOSAGE_UNIT,B.ORDER_DESC,B.SPECIFICATION,B.GOODS_DESC,A.RTN_DOSAGE_UNIT,A.CASE_NO,"
                                + "                 A.OWN_PRICE,A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,C.VS_DR_CODE,"
                                + "                 C.DEPT_CODE,D.DOSE_CODE,D.GIVEBOX_FLG,D.PHA_TYPE,D.ROUTE_CODE,B.NHI_PRICE,B.ADDPAY_RATE,"
                                + "                 E.DOSE_TYPE,A.RT_KIND,A.IBS_CASE_NO_SEQ, A.IBS_SEQ_NO"
                                + " ,A.START_DTTM,A.ORDER_NO,A.ORDER_SEQ "//add by wangjc 20170718
                                + " ) TAB "
                                + " WHERE TAB.RTN_DOSAGE_QTY > 0 "
                                + "GROUP BY EXEC,ORDER_DESC,ORDER_CODE,UNIT_DESC,TRANSMIT_RSN_CODE,CANCEL_DOSAGE_QTY,CANCELRSN_CODE,CASE_NO,OWN_PRICE,"
                                + "         ORDER_CAT1_CODE,CAT1_TYPE,IPD_NO,MR_NO,BED_NO,STATION_CODE,VS_DR_CODE,DEPT_CODE,DOSE_CODE,GIVEBOX_FLG,"
                                + "         PHA_TYPE,ROUTE_CODE,NHI_PRICE,ADDPAY_RATE,DOSE_TYPE,DSPN_KIND,IBS_CASE_NO_SEQ,IBS_SEQ_NO"
                                + " ,START_DTTM,ORDER_NO,ORDER_SEQ";//add by wangjc 20170718
                sql = sql.replaceFirst("#", caseNo);
                sql = sql.replaceFirst("#", stationCodeSQL);
                sql = sql.replaceFirst("#", region);
                sql = sql.replaceFirst("#", caseNo);
                sql = sql.replaceFirst("#", stationCodeSQL);
                sql = sql.replaceFirst("#", region);
            }
//			System.out.println("--22-me.sql=" + sql);
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			
			//System.out.println("patclick parm >>>>>> " + parm);
			
			tblRtnDtl.removeRowAll();
			tblRtnDtl.setParmValue(parm);
			
			// TParm parm =new TParm();
			// //�õ���ҩ��TParm
			// parm=getRtnRarmWithoutReturn(caseNo);
			// tblRtnDtl.removeRowAll();
			// tblRtnDtl.setParmValue(parm);
			// ***************************************************************************************************
			// luhai 2012-2-1 ����ҩ����ϸ��λ��ÿһ����ҩ��������ҩʱ��Ҫ���ݲ�������ҩ��Ϣ��λ��ûһ��batchSeq end
			// ***************************************************************************************************
			return;
		} else {
			// ===========pangben modify 20110512 start

			// ===========pangben modify 20110512 stop

			// ҩ��ȷ��,80,boolean;��ҩ����,100;��ҩʱ��,100;��ҩ��Ա,100;��¼ʱ��,100;��¼��Ա,100,OPERATOR
			// EXEC;ORDER_NO;PHA_RTN_DATE;PHA_RTN_CODE;DSPN_DATE;DSPN_USER
			sql = "SELECT   CASE WHEN PHA_RETN_CODE = '' THEN 'N' ELSE 'Y' END EXEC,RTN_NO,PHA_RETN_DATE,PHA_RETN_CODE,CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,START_DTTM,STATION_CODE"
					+ "  FROM   ODI_DSPNM"
					+ " WHERE   CASE_NO = '"
					+ caseNo
					+ "' AND RTN_NO IS NOT NULL AND IVA_FLG='Y' " + getRegionWhereStr(); // ======pangben
																		// modify
																		// 20110512
			// ������ҩ���ű����ʾ����ֹ�����ظ���
			StringBuffer sqlbf = new StringBuffer();
			sqlbf
					.append("SELECT DISTINCT CASE WHEN (PHA_RETN_CODE = '' or PHA_RETN_CODE is null) THEN 'N' ELSE 'Y' END EXEC,RTN_NO,TO_CHAR(DSPN_DATE,'yyyy-MM-dd') AS  PHA_RETN_DATE, ");
			sqlbf.append("       DSPN_USER AS PHA_RETN_CODE,CASE_NO ,B.USER_NAME ");
			sqlbf.append(" FROM  ODI_DSPNM A,SYS_OPERATOR B");
			sqlbf.append(" WHERE  A.DSPN_USER=B.USER_ID AND CASE_NO = '"
					+ caseNo + "' AND RTN_NO IS NOT NULL AND A.IVA_FLG='Y' ");
			sqlbf.append(" AND  (DSPN_DATE >=TO_DATE('" + startDate
					+ "','YYYY-MM-DD HH24MISS') AND DSPN_DATE <=TO_DATE('"
					+ endDate + "','YYYY-MM-DD HH24MISS'))");
			sqlbf.append(" AND A.REGION_CODE='" + Operator.getRegion() + "' ");
			sqlbf.append(" ORDER BY RTN_NO DESC ");
			// sql+="  AND RTN_NO IN (SELECT DISTINCT RTN_NO FROM ODI_DSPNM "+" WHERE   CASE_NO = '"
			// + caseNo + "' AND RTN_NO IS NOT NULL"+")";
			// luhai modify
			//System.out.println("your.sql=========" + sqlbf.toString());
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					sqlbf.toString()));
			tblRtn.removeRowAll();
			tblRtn.setParmValue(parm);
			
		}
	}

	/**
	 * 
	 * �õ�Ժ���Ĳ�ѯ���� luhai 2012-2-2 add
	 * 
	 * @return
	 */
	private String getRegionWhereStr() {
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND REGION_CODE='" + Operator.getRegion() + "' ";
		}
		return region;
	}

	/**
	 * 
	 * �õ�δ��ҩ�б��sqls
	 * 
	 * @param regionStr
	 * @param caseNo
	 *            luhai 2012-2-2
	 * @return
	 */
	private String getSelectRtnSql(String caseNo, int batchIndex) {
		// ************************************************************************************************************
		// luhai 2012-04-97 ��ҩʱ��odi_dspm �� ����ѯ����������ѯ�����ʾ��odi_dspm
		// ����������ֶ���ظ�ҽ�������
		// ************************************************************************************************************
		// String dispenseStr="E.DISPENSE_QTY"+batchIndex;
		// String returnQtyStr="E.RETURN_QTY"+batchIndex;
		// String diffQtyStr="("+dispenseStr+"-"+returnQtyStr+")";
		// StringBuffer sqlbf = new StringBuffer();
		// sqlbf.append(" SELECT   'N' EXEC,B.ORDER_DESC || B.GOODS_DESC|| ' (' || B.SPECIFICATION || ')' ORDER_DESC,"+diffQtyStr+" AS RTN_DOSAGE_QTY,A.ORDER_CODE, ");
		// sqlbf.append(" A.DOSAGE_UNIT UNIT_DESC,'' TRANSMIT_RSN_CODE,0 CANCEL_DOSAGE_QTY,'' CANCELRSN_CODE,A.CASE_NO,A.OWN_PRICE,A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE, ");
		// sqlbf.append(" C.VS_DR_CODE , C.DEPT_CODE, D.DOSE_CODE, D.GIVEBOX_FLG, D.PHA_TYPE, D.ROUTE_CODE, ");
		// sqlbf.append(" E.BATCH_SEQ"+batchIndex+" BATCH_SEQ ,E.VERIFYIN_PRICE"+batchIndex+" VERIFYIN_PRICE,E.DISPENSE_QTY"+batchIndex+" DISPENSE_QTY, ");
		// sqlbf.append(" E.ORDER_NO,E.ORDER_SEQ,E.START_DTTM,"+batchIndex+" AS \"INDEX\",F.BATCH_NO,F.VALID_DATE ");
		// sqlbf.append("  FROM  IBS_ORDD A, SYS_FEE B,ADM_INP C, PHA_BASE D, ODI_DSPNM E,IND_STOCK F ");
		// sqlbf.append(" WHERE   A.CASE_NO = '" + caseNo + "' ");
		// sqlbf.append(" AND A.ORDER_CODE = B.ORDER_CODE ");
		// sqlbf.append("  AND A.CASE_NO=C.CASE_NO ");
		// sqlbf.append(" AND C.DS_DATE IS NULL ");
		// sqlbf.append(" AND C.DS_DATE IS NULL ");
		// sqlbf.append(" AND (C.CANCEL_FLG IS NULL OR C.CANCEL_FLG ='N') ");
		// sqlbf.append(" AND A.ORDER_CAT1_CODE IN('PHA_W','PHA_C') ");
		// sqlbf.append(" AND A.ORDER_CODE = D.ORDER_CODE ");
		// sqlbf.append(" AND A.CASE_NO = E.CASE_NO ");
		// sqlbf.append(" AND A.ORDER_NO = E.ORDER_NO ");
		// sqlbf.append(" AND A.ORDER_SEQ = E.ORDER_SEQ ");
		// sqlbf.append(" AND E.DISPENSE_QTY"+batchIndex+">0 ");
		// sqlbf.append(" AND E.REGION_CODE='"+Operator.getRegion()+"' ");
		// sqlbf.append(" AND E.DSPN_KIND <>'RT' ");
		// sqlbf.append( " AND "+diffQtyStr+">0 ");
		// sqlbf.append(" AND F.ORDER_CODE=A.ORDER_CODE AND E.EXEC_DEPT_CODE=F.ORG_CODE AND F.BATCH_SEQ=E.BATCH_SEQ"+batchIndex);
		String dispenseStr = "E.DISPENSE_QTY" + batchIndex;
		String returnQtyStr = "E.RETURN_QTY" + batchIndex;
		String diffQtyStr = "(" + dispenseStr + "-" + returnQtyStr + ")";
		StringBuffer sqlbf = new StringBuffer();
		sqlbf
				.append(" SELECT   'N' EXEC,B.ORDER_DESC || B.GOODS_DESC|| ' (' || B.SPECIFICATION || ')' ORDER_DESC,"
						+ diffQtyStr + " AS RTN_DOSAGE_QTY,E.ORDER_CODE, ");
		sqlbf
				.append(" E.DOSAGE_UNIT UNIT_DESC,'' TRANSMIT_RSN_CODE,0 CANCEL_DOSAGE_QTY,'' CANCELRSN_CODE,E.CASE_NO,E.OWN_PRICE,E.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,E.STATION_CODE, ");
		sqlbf
				.append(" C.VS_DR_CODE , C.DEPT_CODE, D.DOSE_CODE, D.GIVEBOX_FLG, D.PHA_TYPE, D.ROUTE_CODE, ");
		sqlbf.append(" E.BATCH_SEQ" + batchIndex
				+ " BATCH_SEQ ,E.VERIFYIN_PRICE" + batchIndex
				+ " VERIFYIN_PRICE,E.DISPENSE_QTY" + batchIndex
				+ " DISPENSE_QTY, ");
		sqlbf.append(" E.ORDER_NO,E.ORDER_SEQ,E.START_DTTM," + batchIndex
				+ " AS \"INDEX\",F.BATCH_NO,F.VALID_DATE ");
		sqlbf
				.append("  FROM  SYS_FEE B,ADM_INP C, PHA_BASE D, ODI_DSPNM E,IND_STOCK F ");
		sqlbf.append(" WHERE   E.CASE_NO = '" + caseNo + "' ");
		sqlbf.append(" AND E.ORDER_CODE = B.ORDER_CODE ");
		sqlbf.append("  AND E.CASE_NO=C.CASE_NO ");
		sqlbf.append(" AND C.DS_DATE IS NULL ");
		sqlbf.append(" AND C.DS_DATE IS NULL ");
		sqlbf.append(" AND (C.CANCEL_FLG IS NULL OR C.CANCEL_FLG ='N') ");
		sqlbf.append(" AND E.ORDER_CAT1_CODE IN('PHA_W','PHA_C') ");
		sqlbf.append(" AND E.ORDER_CODE = D.ORDER_CODE ");
		// sqlbf.append(" AND A.CASE_NO = E.CASE_NO ");
		// sqlbf.append(" AND A.ORDER_NO = E.ORDER_NO ");
		// sqlbf.append(" AND A.ORDER_SEQ = E.ORDER_SEQ ");
		sqlbf.append(" AND E.DISPENSE_QTY" + batchIndex + ">0 ");
		sqlbf.append(" AND E.REGION_CODE='" + Operator.getRegion() + "' ");
		sqlbf.append(" AND E.DSPN_KIND <>'RT' ");
		sqlbf.append(" AND " + diffQtyStr + ">0 ");
		sqlbf
				.append(" AND F.ORDER_CODE=E.ORDER_CODE AND E.EXEC_DEPT_CODE=F.ORG_CODE AND F.BATCH_SEQ=E.BATCH_SEQ"
						+ batchIndex);
		// System.out.println("sql:"+sqlbf.toString());
		// ************************************************************************************************************
		// luhai 2012-04-97 ��ҩʱ��odi_dspm �� ����ѯ����������ѯ�����ʾ��odi_dspm
		// ����������ֶ���ظ�ҽ�������
		// ************************************************************************************************************
		return sqlbf.toString();
	}

	/**
	 * 
	 * �õ�δ��ҩ��Parm
	 * 
	 * @return
	 */
	private TParm getRtnRarmWithoutReturn(String caseNo) {
		TParm returnParm = new TParm();
		String sql = "";
		for (int i = 1; i <= 3; i++) {
			sql = getSelectRtnSql(caseNo, i);
			// System.out.println("me.sql=" + sql);
			TParm parmtmp = new TParm(TJDODBTool.getInstance().select(sql));
			for (int j = 0; j < parmtmp.getCount(); j++) {
				returnParm.addData("EXEC", parmtmp.getData("EXEC", j));
				returnParm.addData("ORDER_DESC", parmtmp.getData("ORDER_DESC",
						j));
				returnParm.addData("RTN_DOSAGE_QTY", parmtmp.getData(
						"RTN_DOSAGE_QTY", j));
				returnParm.addData("ORDER_CODE", parmtmp.getData("ORDER_CODE",
						j));
				returnParm
						.addData("UNIT_DESC", parmtmp.getData("UNIT_DESC", j));
				returnParm.addData("TRANSMIT_RSN_CODE", parmtmp.getData(
						"TRANSMIT_RSN_CODE", j));
				returnParm.addData("CANCEL_DOSAGE_QTY", parmtmp.getData(
						"CANCEL_DOSAGE_QTY", j));
				returnParm.addData("CANCELRSN_CODE", parmtmp.getData(
						"CANCELRSN_CODE", j));
				returnParm.addData("CASE_NO", parmtmp.getData("CASE_NO", j));
				returnParm
						.addData("OWN_PRICE", parmtmp.getData("OWN_PRICE", j));
				returnParm.addData("ORDER_CAT1_CODE", parmtmp.getData(
						"ORDER_CAT1_CODE", j));
				returnParm
						.addData("CAT1_TYPE", parmtmp.getData("CAT1_TYPE", j));
				returnParm.addData("IPD_NO", parmtmp.getData("IPD_NO", j));
				returnParm.addData("MR_NO", parmtmp.getData("MR_NO", j));
				returnParm.addData("BED_NO", parmtmp.getData("BED_NO", j));
				returnParm.addData("STATION_CODE", parmtmp.getData(
						"STATION_CODE", j));
				returnParm.addData("VS_DR_CODE", parmtmp.getData("VS_DR_CODE",
						j));
				returnParm
						.addData("DOSE_CODE", parmtmp.getData("DOSE_CODE", j));
				returnParm.addData("GIVEBOX_FLG", parmtmp.getData(
						"GIVEBOX_FLG", j));
				returnParm.addData("PHA_TYPE", parmtmp.getData("PHA_TYPE", j));
				returnParm.addData("ROUTE_CODE", parmtmp.getData("ROUTE_CODE",
						j));
				returnParm
						.addData("BATCH_SEQ", parmtmp.getData("BATCH_SEQ", j));
				returnParm.addData("VERIFYIN_PRICE", parmtmp.getData(
						"VERIFYIN_PRICE", j));
				returnParm.addData("DISPENSE_QTY", parmtmp.getData(
						"DISPENSE_QTY", j));
				returnParm.addData("ORDER_NO", parmtmp.getData("ORDER_NO", j));
				returnParm
						.addData("ORDER_SEQ", parmtmp.getData("ORDER_SEQ", j));
				returnParm.addData("START_DTTM", parmtmp.getData("START_DTTM",
						j));
				returnParm.addData("INDEX", parmtmp.getData("INDEX", j));
				returnParm.addData("BATCH_NO", parmtmp.getData("BATCH_NO", j));
				returnParm.addData("VALID_DATE", parmtmp.getData("VALID_DATE",
						j));
			}
		}
		returnParm.setCount(returnParm.getCount("ORDER_DESC"));
		return returnParm;
	}

	/**
	 * �Ա�����ҩ����ϸTABLE����¼�
	 */
	public void onClickRtn(Object ttableNode) {
		if (isEdit) {
			int yesOrNo = this.messageBox("��ʾ", "���޸����ݣ����ȷ������", 0);
			if (0 == yesOrNo) {
				onSave();
			}
		}
		int row = tblRtn.getSelectedRow();
		String caseNo = tblRtn.getParmValue().getValue("CASE_NO", row);
		String orderNo = tblRtn.getParmValue().getValue("RTN_NO", row);
		// ===========pangben modify 20110512 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110512 stop

		// ��ҩ����ϸSQL
		String sql = " SELECT NULL AS EXEC, ORDER_SEQ,ORDER_DESC,RTN_DOSAGE_QTY,RTN_DOSAGE_UNIT UNIT_DESC, "
				+ "	CANCEL_DOSAGE_QTY,TRANSMIT_RSN_CODE,CANCELRSN_CODE,ORDER_CODE,CASE_NO,"
				+ "	ORDER_NO,ORDER_SEQ AS ORDER_SEQ_TEMP,START_DTTM,"
				+ " DSPN_KIND,PARENT_CASE_NO,PARENT_ORDER_NO,PARENT_ORDER_SEQ,PARENT_START_DTTM,REGION_CODE,RTN_NO,DSPN_KIND,STATION_CODE,RT_KIND,DISPENSE_QTY,PHA_RETN_CODE,PHA_RETN_DATE "// add by wanglong 20130628   //add by duzhw 20130923
				+ " FROM  ODI_DSPNM A WHERE CASE_NO='"
				+ caseNo
				+ "'"
				+ region
				+ " AND RTN_NO='"
				+ orderNo
				+ "' AND DSPN_KIND='RT' "
				+ " AND IVA_FLG = 'Y' "//20150506 wangjingchun add
				+ " ORDER BY ORDER_SEQ";
		//System.out.println("��ҩ����ϸSQLrtn----->" + sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		tblRtnDtl.setParmValue(parm);
		execList = new ArrayList();
		firstClick = true;
		upOrDown = 0;
	}

	/**
	 * 
	 * @param tNode
	 * @return �٣��ɹ����棺ʧ��
	 */
	public boolean onClickDtl(TTableNode tNode) {
		String columnName = tblRtnDtl.getParmMap(tNode.getColumn());
		if (!"EXEC".equalsIgnoreCase(columnName)
				&& !"RTN_DOSAGE_QTY".equalsIgnoreCase(columnName)
				&& !"TRANSMIT_RSN_CODE".equalsIgnoreCase(columnName)) {
			return true;
		}
		if ("EXEC".equalsIgnoreCase(columnName)) {
			return false;
		}
		if ("TRANSMIT_RSN_CODE".equalsIgnoreCase(columnName)) {
			return false;
		}
		if (!tblRtnDtl.getParmValue().getBoolean("EXEC", tNode.getRow())) {
			this.messageBox_("���ȵ�ѡִ��");
			return true;
		}

		if (firstClick) {
			oldValue = TCM_Transform.getDouble(tNode.getOldValue());
		}
		double value = TCM_Transform.getDouble(tNode.getValue());
		if (value < 0) {
			this.messageBox_("��ҩ��������Ϊ����������������");
			return true;
		}
		if (oldValue < value) {
		}
		isEdit = true;
		firstClick = false;
		upOrDown = 1;
		return false;
	}

	/**
	 * ��ϸ��ϢTABLECHECK_BOX��ѡ�¼�
	 * 
	 * @param obj
	 */
	public void onCheckBoxClick(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		boolean value = TCM_Transform.getBoolean(table.getValueAt(table
				.getSelectedRow(), table.getSelectedColumn()));
		if (value) {
			execList.add(table.getSelectedRow());
		} else {
			execList.remove((Object) table.getSelectedRow());
		}
		isEdit = true;
		firstClick = true;
		upOrDown = 1;
	}

	/**
	 * �����¼�
	 */
	public void onSave() {
		if(this.tblRtn.getSelectedRow() >= 0){
			TParm tblRtnParm = this.tblRtn.getParmValue();
			if("Y".equals(tblRtnParm.getValue("EXEC", this.tblRtn.getSelectedRow()))){
				this.messageBox("ҩ����ȷ�ϣ��޷����޸ġ�");
				return;
			}
		}
		// luhai 2012-04-16 ������ձ�������ڱ༭��ֵ�Ĵ���
		// ��� ����ʱtable�����ڱ༭״̬���û��������ֵû�б����ϵ�����begin
		this.tblRtnDtl.acceptText();
		// luhai 2012-04-16 ������ձ�������ڱ༭��ֵ�Ĵ�
		// ��� ����ʱtable�����ڱ༭״̬���û��������ֵû�б����ϵ����� end
		tblRtnDtl.acceptText();
		if (execList == null || execList.size() < 1)
			return;
		String orderNo = SystemTool.getInstance().getNo("ALL", "ODI",
				"ORDER_NO", "ORDER_NO");
		TParm parm = tblRtnDtl.getParmValue();
//		System.out.println("tblRtnDtl============"+parm);
		String caseNo, sttDttm;
		double rtnDosageQty;
		int orderSeq;
		Timestamp t = TJDODBTool.getInstance().getDBTime();
		sttDttm = StringTool.getString(t, "yyyyMMddHHmm");
		TParm result = new TParm();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		String nowStr = StringTool.getString(now, "yyyyMMddHHmmss");
		String orderDate = nowStr.substring(0, 8);
		String orderDateTime = nowStr.substring(8);
		// System.out.println("PARM=" + parm);
		boolean save = TypeTool.getBoolean(this.getValue("UN_DONE"));
		if (save) {
			// 2012-2-2 luhai modify ��ҩ�߼��޸� begin
			for (int i = 0; i < execList.size(); i++) {
				int j = (Integer) execList.get(i);
				caseNo = parm.getValue("CASE_NO", j);

				orderSeq = i + 1;
				rtnDosageQty = parm.getDouble("RTN_DOSAGE_QTY", j);
				if(!checkOrderData(parm.getRow(j)))//������� shibl add 20130312 
					return;
				if(!onCheckDispense(parm.getRow(j))) //��Һ���޷���ҩ
					return;
				result.addData("START_DTTM", sttDttm);
				/******************
				 * update by liyh 20120525:add cloumn NHI_PRICE��DISCOUNT_RATE ��
				 * IBS_CASE_NO_SEQ ��IBS_SEQ_NO start
				 ******/
				result.addData("NHI_PRICE", parm.getDouble("NHI_PRICE", j));
				result.addData("DISCOUNT_RATE", parm.getDouble("ADDPAY_RATE", j));
				//alert and add by wukai on 20170313 start 
				result.addData("IBS_SEQ_NO", parm.getInt("IBS_SEQ_NO", j));
				result.addData("IBS_CASE_NO_SEQ", parm.getInt("IBS_CASE_NO_SEQ", j));
				//alert and add by wukai on 20170313 end 
				/******************
				 * update by liyh 20120525:add cloumn
				 * NHI_PRICE��DISCOUNT_RATEIBS_CASE_NO_SEQ ��IBS_SEQ_NO end
				 ******/
				result.addData("END_DTTM", sttDttm);
				result.addData("CASE_NO", caseNo);
				result.addData("ORDER_NO", orderNo);
				result.addData("ORDER_SEQ", orderSeq);
				result.addData("ORDER_CODE", parm.getValue("ORDER_CODE", j));
				result.addData("RTN_NO", orderNo);
				result.addData("RTN_NO_SEQ", orderSeq);
				result.addData("ORDER_DATE", orderDate);
				result.addData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
				result.addData("REGION_CODE", Operator.getRegion());
				result.addData("ORDER_DATETIME", orderDateTime);
				result.addData("ORDER_CAT1_CODE", parm.getValue(
						"ORDER_CAT1_CODE", j));
				result.addData("CAT1_TYPE", parm.getValue("CAT1_TYPE", j));
				result.addData("DOSE_TYPE", parm.getValue("DOSE_TYPE", j));
				result.addData("GIVEBOX_FLG", parm.getValue("GIVEBOX_FLG", j));
				// result.addData("LINKMAIN_FLG", parm.getValue("LINKMAIN_FLG",
				// j));
				result.addData("OWN_PRICE", parm.getData("OWN_PRICE", j));
				result.addData("PHA_TYPE", parm.getValue("PHA_TYPE", j));
				result.addData("ROUTE_CODE", parm.getValue("ROUTE_CODE", j));
				result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", j));
				result.addData("MR_NO", parm.getValue("MR_NO", j));
				result
						.addData("STATION_CODE", parm.getValue("STATION_CODE",
								j));
				result.addData("BED_NO", parm.getValue("BED_NO", j));
				result.addData("IPD_NO", parm.getValue("IPD_NO", j));
				result.addData("EXEC_DEPT_CODE", this.getValue("RTN_IND"));
				result.addData("RX_KIND", "RT");
				result.addData("DC_TOT", rtnDosageQty
						* parm.getDouble("OWN_PRICE", j));
				result.addData("RTN_DOSAGE_QTY", rtnDosageQty);
				result
						.addData("RTN_DOSAGE_UNIT", parm.getValue("UNIT_DESC",
								j));

				result.addData("DISPENSE_QTY", rtnDosageQty);
				result.addData("DISPENSE_UNIT", parm.getValue("UNIT_DESC", j));

				result.addData("PHA_RETN_CODE", Operator.getID());
				result.addData("PHA_RETN_DATE", TJDODBTool.getInstance()
						.getDBTime());
				result.addData("TRANSMIT_RSN_CODE", parm.getValue(
						"TRANSMIT_RSN_CODE", j));
				// zhangyong20100720 begin
				result.addData("VS_DR_CODE", parm.getValue("VS_DR_CODE", j));
				result.addData("ORDER_DR_CODE", parm.getValue("VS_DR_CODE", j));
				result.addData("ORDER_DEPT_CODE", this
						.getValueString("RTN_IND"));
				// zhangyong20100720 end
				//20150710 wangjc add start д��BATCH_SEQ1���տ�����ҩƷ������IND_STOCK�в�ѯ���BATCH_SEQ
//				String maxSeqSql = "SELECT MAX(BATCH_SEQ) AS BATCH_SEQ FROM IND_STOCK WHERE ORG_CODE='"
//										+this.getValue("RTN_IND")
//										+ "' AND ORDER_CODE='"
//										+parm.getValue("ORDER_CODE", j)
//										+ "' AND ACTIVE_FLG='Y' "
//										+ " AND VALID_DATE > SYSDATE ";
////				System.out.println(maxSeqSql);
//				TParm maxSeqParm = new TParm(TJDODBTool.getInstance().select(maxSeqSql));
//				result.addData("BATCH_SEQ1", maxSeqParm.getValue("BATCH_SEQ", 0));
				//20150710 wangjc add end

				/**
				 * ����
				 */
				result.addData("DSPN_USER", Operator.getID());
				result.addData("OPT_USER", Operator.getID());
				result.addData("OPT_TERM", Operator.getIP());
                
                result.addData("RT_KIND", parm.getValue("DSPN_KIND", j));//add by wanglong 20130628
                result.addData("RT_REQTY", rtnDosageQty);
                if (TypeTool.getBoolean(this.getValue("DC_RDBTN"))) {
                    result.addData("RT_TYPE", "DC");
                } else {
                    result.addData("RT_TYPE", "ALL");
                }
                result.addData("PARENT_CASE_NO", caseNo);
                result.addData("PARENT_ORDER_NO", parm.getValue("ORDER_NO", j));
                result.addData("PARENT_ORDER_SEQ", parm.getValue("ORDER_SEQ", j));
                result.addData("PARENT_START_DTTM", parm.getValue("START_DTTM", j));
                
                result.addData("BATCH_SEQ1",parm.getInt("BATCH_SEQ1", j));
                
			}
			// System.out.println("----------------result:"+result);
			result = TIOM_AppServer.executeAction("action.iva.IvaRtnRgsAction",
					"onInsert", result);
			// 2012-2-2 luhai modify ��ҩ�߼��޸� end
		} else {
			for (int i = 0; i < execList.size(); i++) {

				// EXEC;ORDER_SEQ;ORDER_DESC;RTN_DOSAGE_QTY;UNIT_DESC;TRANSMIT_RSN_CODE;CANCEL_DOSAGE_QTY;CANCELRSN_CODE

				int j = (Integer) execList.get(i);
				caseNo = parm.getValue("CASE_NO", j);
				orderSeq = i + 1;
				rtnDosageQty = parm.getDouble("RTN_DOSAGE_QTY", j);
				result.addData("START_DTTM", parm.getValue("START_DTTM", j));
				result.addData("CASE_NO", caseNo);
				result.addData("ORDER_NO", parm.getValue("ORDER_NO", j));
				result.addData("ORDER_SEQ", parm.getData("ORDER_SEQ", j));
				result.addData("ORDER_CODE", parm.getValue("ORDER_CODE", j));
				if (TypeTool.getBoolean(this.getValue("UN_DONE"))) {
					result.addData("ORDER_DATE", orderDate);
				} else {
					result
							.addData("ORDER_DATE", parm.getValue("ORDER_DATE",
									j));
					result
							.addData("START_DTTM", parm.getValue("ORDER_DATE",
									j));
				}

				result.addData("ORDER_DATETIME", orderDateTime);
				result.addData("DC_TOT", rtnDosageQty);
				result
						.addData("EXEC_DEPT_CODE", this
								.getValueString("RTN_IND"));
				result.addData("DOSAGE_QTY", rtnDosageQty);
				result.addData("DOSAGE_UNIT", parm.getValue("UNIT_DESC", j));
				result.addData("RTN_DOSAGE_QTY", rtnDosageQty);
				result.addData("OPT_USER", Operator.getID());
				result.addData("OPT_TERM", Operator.getIP());
				result.addData("TRANSMIT_RSN_CODE", parm.getValue(
						"TRANSMIT_RSN_CODE", j));
			}
			result = TIOM_AppServer.executeAction("action.iva.IvaRtnRgsAction",
					"onUpdate", result);
		}
		if (result.getErrCode() != 0) {
			this.messageBox("E0001");
			// this.messageBox_(result.getErrText());
			return;
		} else {
			this.messageBox("P0001");
			this.onQuery();
			return;
		}

	}

	/**
	 * ���ҩƷ��������
	 * 
	 * @param parm
	 * @return
	 */
	public boolean checkOrderData(TParm parm){
    	String caseNo=parm.getValue("CASE_NO");//�����
    	String orderCode=parm.getValue("ORDER_CODE");//ҩƷ����
    	String phaDisSql="SELECT SUM(A.DOSAGE_QTY) AS QTY,A.DOSAGE_UNIT,A.ORDER_CODE, " +//wanglong modify 20150129 
    	                                                  //�����ͬorder_code��ͬorder_desc������ҩ�Ǽ�ʧ�ܵ�����
    			" (SELECT B.ORDER_DESC FROM SYS_FEE B WHERE A.ORDER_CODE = B.ORDER_CODE AND ROWNUM = 1) ORDER_DESC " +
    			" FROM ODI_DSPNM A " +
    			" WHERE A.CASE_NO='"+caseNo+"'" +
    			" AND A.ORDER_CODE='"+orderCode+"'" +
    			" AND A.DSPN_KIND<>'RT' AND A.PHA_DOSAGE_CODE IS NOT NULL" +
    			" AND A.IVA_FLG='Y' "+
    			" GROUP BY A.DOSAGE_UNIT,A.ORDER_CODE";
    	TParm disParm=new TParm(TJDODBTool.getInstance().select(phaDisSql));
    	double qty=0;
    	if(disParm.getCount()<=0){
    		this.messageBox("û�в�ѯ����ҩ��Ϣ");
    		return false;
    	}
    	qty=disParm.getDouble("QTY", 0);//�ѷ�ҩ����
    	String phaRenSql="SELECT SUM(RTN_DOSAGE_QTY) AS QTY,RTN_DOSAGE_UNIT,ORDER_CODE " +
		" FROM ODI_DSPNM " +
		" WHERE CASE_NO='"+caseNo+"'" +
		" AND ORDER_CODE='"+orderCode+"'" +
		" AND DSPN_KIND='RT'" +
		" AND IVA_FLG='Y' "+
		" GROUP BY RTN_DOSAGE_UNIT,ORDER_CODE ";
        TParm renParm=new TParm(TJDODBTool.getInstance().select(phaRenSql));
        double renQty=0;
        if(disParm.getCount()>0)
        	renQty=renParm.getDouble("QTY", 0);//����ҩ�Ǽ�����
    	if(qty-(renQty+parm.getDouble("RTN_DOSAGE_QTY"))<0){
    		this.messageBox(disParm.getValue("ORDER_DESC", 0)+"��ҩ�������ܴ��ڷ�ҩ����\n ����ҩ����"+(qty-renQty));
    		return false;
    	}
    	return true;
    }

	/**
	 * ɾ���¼�
	 */
	public void onDelete() {
		if (!isSave) {
			this.messageBox_("�µǼ���ҩ����ҽ������ɾ��");
			return;
		}
		int row;
		TParm parm;
		// ��ҩ��TABLE���
		if (0 == upOrDown) {
			row = tblRtn.getSelectedRow();
			parm = tblRtnDtl.getParmValue();
			// System.out.println("0:parm->"+parm);
			for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
				String rtnUser = parm.getValue("PHA_RETN_CODE", i);
				if (!StringUtil.isNullString(rtnUser)) {
					this.messageBox_("��ȷ�ϵ���ҩ�������޸�");
					return;
				}
			}
			TParm result = TIOM_AppServer.executeAction(
					"action.iva.IvaRtnRgsAction", "onDelete", parm);
			if (result.getErrCode() != 0) {
				this.messageBox("E0003");
				return;
			} else {
				this.messageBox("P0003");
				this.onQuery();
				return;
			}
		}
		// ��ҩ��ϸTABLE���
		else {
			row = tblRtnDtl.getSelectedRow();
			parm = tblRtnDtl.getParmValue().getRow(row);
			String rtnUser = parm.getValue("PHA_RETN_CODE", 0);
			if (!StringUtil.isNullString(rtnUser)) {
				this.messageBox_("��ȷ�ϵ���ҩ�������޸�");
				return;
			}
			TParm result = TIOM_AppServer.executeAction(
					"action.iva.IvaRtnRgsAction", "onDeleteSingle", parm);
			if (result.getErrCode() != 0) {
				this.messageBox("E0003");
				return;
			} else {
				this.messageBox("P0003");
				this.onQuery();
				return;
			}
		}
	}

	/**
	 * δ����¼�
	 */
	public void onUnDone() {
		this.callFunction("UI|LBL_RT|setVisible", false);
		this.setValue("RTN_NO", "");
		this.callFunction("UI|RTN_NO|setVisible", false);
        this.callFunction("UI|DC_RDBTN|setEnabled", true);// add by wanglong 20130628
        this.callFunction("UI|ALL_RDBTN|setEnabled", true);
        this.callFunction("UI|DC_RDBTN|setSelected", true);
	}

	/**
	 * ����¼�
	 */
	public void onDone() {
		this.callFunction("UI|LBL_RT|setVisible", true);
		this.setValue("RTN_NO", "");
		this.callFunction("UI|RTN_NO|setVisible", true);
        this.callFunction("UI|DC_RDBTN|setEnabled", false);// add by wanglong 20130628
        this.callFunction("UI|ALL_RDBTN|setEnabled", false);
        this.callFunction("UI|ALL_RDBTN|setSelected", true);
	}

	/**
	 * ��ʿվ�������š�������RADIO����¼�
	 */
	public void onRegion() {
		boolean isBed = TypeTool.getBoolean(this.getValue("BED"));
		if (isBed) {
			this.callFunction("UI|BED_NO|setVisible", true);
			this.callFunction("UI|NO|setVisible", false);
		} else {
			this.callFunction("UI|BED_NO|setVisible", false);
			this.callFunction("UI|NO|setVisible", true);
		}
	}

	/**
	 * ������ѡ���¼�
	 */
	public void onBedNo() {
		onQuery();
	}

	/**
	 * ��ҩ���Ų�ѯ�¼�
	 */
	public void onRtnNo() {
		String rtnNo = this.getValueString("RTN_NO");
		// ===========pangben modify 20110516 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110516 stop

		String sql = "SELECT DISTINCT 'Y' AS EXEC,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO,A.CASE_NO,A.RTN_NO,A.RTN_NO_SEQ"
				+ "	FROM ODI_DSPNM A, SYS_PATINFO B,SYS_BED C"
				+ "	WHERE A.CASE_NO=C.CASE_NO"
				+ "		  AND A.MR_NO=B.MR_NO"
				+ "		  AND A.RTN_NO='" + rtnNo + "'" + region;
		// System.out.println("rtnNo=========" + rtnNo);

		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		tblPat.setParmValue(parm);
		tblRtnDtl.removeRowAll();
		tblRtn.removeRowAll();
		execList = new ArrayList();
		isEdit = false;
		isSave = false;
		upOrDown = 0;
	}

	/**
	 * ��ҩ��ϸTABLE����¼�
	 */
	public void onClickRtnDtl() {
		upOrDown = 1;
	}

	/**
	 * �����Żس��¼�
	 */
	public void onMrNoAction() {
		String mr_no = PatTool.getInstance().checkMrno(getValueString("NO"));
		this.setValue("NO", mr_no);
		Pat pat = Pat.onQueryByMrNo(mr_no);
		//modify by huangtt 20160930 EMPI���߲�����ʾ  start

		if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
	            this.messageBox("������" + mr_no + " �Ѻϲ��� " + "" + pat.getMrNo());
	            setValue("NOs", pat.getMrNo());
	    }
		//modify by huangtt 20160930 EMPI���߲�����ʾ  end
		
		this.setValue("NAME", pat.getName());
		onQuery();
	}

	/**
	 * ҽ�ƿ����� 2012-2-27 luhai
	 */
	public void onEKT() {
		TParm parm = EKTIO.getInstance().TXreadEKT();
		// System.out.println("parm==="+parm);
		if (null == parm || parm.getValue("MR_NO").length() <= 0) {
			this.messageBox("��鿴ҽ�ƿ��Ƿ���ȷʹ��");
			return;
		}
		// zhangp 20120130
		if (parm.getErrCode() < 0) {
			messageBox(parm.getErrText());
		}
		setValue("NO", parm.getValue("MR_NO"));
		
		//modify by huangtt 20160930 EMPI���߲�����ʾ  start
		String mrNo = parm.getValue("MR_NO");
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
	            this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
	            setValue("NO", pat.getMrNo());
	    }
		this.setValue("NAME", pat.getName());
		//modify by huangtt 20160930 EMPI���߲�����ʾ  end
		
		TRadioButton td = (TRadioButton) this.getComponent("MR");
		td.setSelected(true);
		this.onQuery();
		// �޸Ķ�ҽ�ƿ����� end luhai 2012-2-27
	}
	/**
	 * ȡ���Ǽ�-��ҩ�Ǽǵ�ҩƷ�����δ������ҩȷ�������ȡ���Ǽǲ���  add by duzhw 20130918
	 */
	public void cancelReturnRegist() {
		boolean flag = false;		//�Ƿ�ѡ����ҩ��ϸѡ��
		boolean success = false;	//�Ƿ�ɹ�ȡ����ҩ�Ǽ�
		if (TypeTool.getBoolean(this.getValue("UN_DONE"))) {//δ��� ҳ����ʾȡ����ť������
			messageBox("����	'�����'ҳ��ȡ����ҩ�ǼǼ�¼��");
			return;
		}else {
			if(tblRtnDtl.getSelectedRow() < 0){
				messageBox("��ѡ����ҩ��ϸ��");
				return;
			}
			for (int i = 0; i < tblRtnDtl.getRowCount(); i++) {
				//System.out.println("�Ƿ�ѡ�У�"+tblRtnDtl.getValueAt(i, 0));
				String result = (String)tblRtnDtl.getValueAt(i, 0);
				if("Y".equals(result)){
					String phaRetnCode = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("PHA_RETN_CODE", i)); 
					if(!"".equals(phaRetnCode)){
						messageBox("�Ѿ���ҩȷ�ϣ�����ȡ����ҩ�Ǽǲ�����");
						return;
					}
					String caseNo = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("CASE_NO", i));
					String prtNo = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("RTN_NO", i));
					String regionCode = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("REGION_CODE", i));
					String dspnKind = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("DSPN_KIND", i));
					String orderNo = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("ORDER_NO", i));
					String orderSeq = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("ORDER_SEQ", i));
					double rtReqty = TCM_Transform.getDouble(tblRtnDtl.getParmValue().getValue("DISPENSE_QTY", i));		//��ҩ����
					String parentOrderNo = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("PARENT_ORDER_NO", i));
					String parentOrderSeq = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("PARENT_ORDER_SEQ", i));
					String stationCode = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("STATION_CODE", i));
					String rtKind = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("RT_KIND", i));
//					System.out.println("��ҩ��ϸ��ѡ caseNo="+caseNo+"  prtNo="+prtNo+" regionCode="+regionCode+" dspnKind="+dspnKind+"  orderSeq="+orderSeq
//							+" orderNo="+orderNo+" rtReqty="+rtReqty+" parentOrderNo="+parentOrderNo+"  parentOrderSeq="+parentOrderSeq+" stationCode="+stationCode+" rtKind="+rtKind);
					
					//�ж��Ƿ񾭹���ҩȷ�ϲ���-����˼����ķ���������bug
					String sql_flag = confirmODISql( caseNo, orderNo, orderSeq, parentOrderNo, 
							prtNo, dspnKind, regionCode, stationCode);
					TParm result_flag = new TParm(TJDODBTool.getInstance().select(sql_flag.toString()));
					if(result_flag.getCount("CASE_NO") > 0){
						messageBox("�Ѿ������ҩȷ�ϲ���������ȡ����ҩ����ˢ�£�");
						return;
					}
					
					boolean DC_flag = false;
					if(!"".equals(parentOrderNo) && (parentOrderNo != null)){
						DC_flag = true;
					}
					if(DC_flag){//UDҽ�� 
						//�޸���ҩ����
						String sql2 = getUpdateODISql(caseNo, rtReqty, parentOrderNo, parentOrderSeq, regionCode, stationCode);
						TParm result2 = new TParm(TJDODBTool.getInstance().update(sql2.toString()));
						if(result2.getErrCode()==0) {
							//ɾ��ȡ������ҩ��¼��Ϣ
							String sql3 = getDeleteODISql(DC_flag, caseNo, orderNo, orderSeq, parentOrderNo, 
									prtNo, dspnKind, regionCode, stationCode);
							TParm result3 = new TParm(TJDODBTool.getInstance().update(sql3.toString()));
							if(result3.getErrCode()==0) {
								success = true;
							}
						}
						
					}else {//ȫ��ҽ��
						//ɾ��ȡ������ҩ��¼��Ϣ
						String sql4 = getDeleteODISql(DC_flag, caseNo, orderNo, orderSeq, parentOrderNo, 
								prtNo, dspnKind, regionCode, stationCode);
						TParm result4 = new TParm(TJDODBTool.getInstance().update(sql4.toString()));
						if(result4.getErrCode()==0) {
							success = true;
						}
					}
					flag = true;
				}
				
				//tblRtnDtl.setValueAt(false, i, 0);
			}
			if(!flag){
				messageBox("��ѡ����ҩ��ϸ��");
				return;
			}
			if(success){
				messageBox("�ɹ�ȡ����ҩ�Ǽǣ�");
			}
			//���²�ѯ��������������
			onQuery();
			
		}
		
	}
	
	
	//ȡ����ҩ�Ǽ��޸���ҩ����sql
	public String getUpdateODISql(String caseNo, double num, String parentOrderNo, String parentOrderSeq, String regionCode, String stationCode){
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE ODI_DSPNM SET RT_REQTY = RT_REQTY -").append(num)
			.append(" WHERE CASE_NO = '").append(caseNo).append("' AND ORDER_NO = '")
			.append(parentOrderNo).append("' AND ORDER_SEQ = '").append(parentOrderSeq).append("' AND PHA_DOSAGE_DATE IS NOT NULL")
			.append(" AND DC_DATE IS NOT NULL AND (DSPN_KIND = 'F' OR DSPN_KIND = 'UD')")
			.append(" AND (DOSAGE_QTY >= RT_REQTY OR RT_REQTY IS NULL)")
			.append(" AND ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C')")
			.append(" AND STATION_CODE = '").append(stationCode).append("' AND REGION_CODE = '").append(regionCode).append("'");
		//System.out.println("�޸Ĳ���sql--->"+sql.toString());
		return sql.toString();
	} 
	
	//ȡ����ҩ�Ǽ�ɾ����ҩ��¼sql
	public String getDeleteODISql (boolean DC_flag, String caseNo, String orderNo, String orderSeq, String parentOrderNo, 
			String prtNo,String dspnKind, String regionCode, String stationCode) {
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE FROM ODI_DSPNM WHERE CASE_NO = '")
				.append(caseNo).append("' AND ORDER_NO = '").append(orderNo).append("' ");
		if(DC_flag){//UDҽ��
			sql.append(" AND PARENT_ORDER_NO = '").append(parentOrderNo).append("'");
		}else{//ȫ��ҽ��
			sql.append(" AND (PARENT_ORDER_NO IS NULL OR PARENT_ORDER_NO = '') ");
		}
		sql.append(" AND ORDER_SEQ = '").append(orderSeq).append("' AND STATION_CODE = '").append(stationCode)
				.append("' AND REGION_CODE = '").append(regionCode).append("' ")
				.append(" AND RTN_NO = '").append(prtNo).append("' AND DSPN_KIND = '").append(dspnKind).append("'");
		
		//System.out.println("ɾ������sql--->"+sql.toString());
		return sql.toString();
	}
	//�ж���ҩ�����Ƿ�ȷ��
	public String confirmODISql (String caseNo, String orderNo, String orderSeq, String parentOrderNo, 
			String prtNo,String dspnKind, String regionCode, String stationCode) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT CASE_NO FROM ODI_DSPNM WHERE CASE_NO = '")
				.append(caseNo).append("' AND ORDER_NO = '").append(orderNo).append("' ")
				.append(" AND ORDER_SEQ = '").append(orderSeq).append("' AND STATION_CODE = '").append(stationCode)
				.append("' AND REGION_CODE = '").append(regionCode).append("' ")
				.append(" AND RTN_NO = '").append(prtNo).append("' AND DSPN_KIND = '").append(dspnKind).append("'")
				.append(" AND PHA_RETN_CODE IS NOT NULL AND PHA_RETN_DATE IS NOT NULL ");
		
		//System.out.println("�ж���ҩ�����Ƿ�ȷ��sql--->"+sql.toString());
		return sql.toString();
	}
	
	
	/**
	 * ���ҩƷ�Ƿ����Һ
	 * @param parm
	 * @return
	 */
	private boolean onCheckDispense(TParm parm){
		
		String caseNo = parm.getValue("CASE_NO");
		String orderNo = parm.getValue("ORDER_NO");
		int orderSeq = parm.getInt("ORDER_SEQ");
		String startDTTM = parm.getValue("START_DTTM");
		
		String sql = "SELECT ORDER_CODE FROM ODI_DSPNM WHERE CASE_NO = '" +  caseNo
					+ "'  AND ORDER_NO = '" + orderNo
					+ "' AND ORDER_SEQ = " + orderSeq
					+ "  AND START_DTTM = '" + startDTTM 
				    + "' AND IVA_DEPLOY_USER IS NULL ";    //��ѯδ��Һ��ҩƷ
//		System.out.println("sql>>>>>>>>>>>>>>>>>>>>>>"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result == null || result.getErrCode() < 0 || result.getCount("ORDER_CODE") <= 0) {
			//������δ��Һ
			this.messageBox_(parm.getValue("ORDER_DESC")+ "����Һ���޷���ҩ��");
			return false;
		}
		return true;
		
	}
	
	
	
	
	
	
	
}
