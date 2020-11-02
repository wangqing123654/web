package com.javahis.ui.udd;

/**
 * <p>
 * Title: סԺҩ����ҩ���
 * </p>
 *
 * <p>
 * Description: סԺҩ����ҩ���
 * </p>
 *
 * <p>
 * Copyright: javahis 20090311
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author ehui
 * @version 1.0
 */
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import jdo.inw.InwUtil;
import jdo.odi.OdiOrderTool;
import jdo.pha.PassTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSNewRegionTool;
import jdo.sys.SystemTool;
import jdo.udd.UddChnCheckTool;
import jdo.util.Manager;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatINDOrg;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

public class UddChnMedCheck extends TControl {
	/**
	 * �����ַ�������Y������N�������ַ���
	 */
	public static final String Y = "Y";
	public static final String N = "N";
	public static final String NULL = "";
	// ҩƷϸ����
	private String odiOrdercat;
	// ����TABLE��ҽ��TABLE
	private TTable tblPat, tblDtl;
	// ִ���б�
	private List execList = new ArrayList();
	// ���������
	private TParm saveParm = new TParm();

	/**
	 * ������ҩ
	 */
	boolean passIsReady = false;
	private boolean enforcementFlg = false;
	private int warnFlg;

	// �Ƿ���Ҫ��ʿ���ע��
	private boolean isNsCheck;
	private static final String PAT_SQL = "SELECT  'N' AS EXEC,A.CASE_NO,A.BED_NO,B.PAT_NAME,"
			+ "		A.RX_NO,A.STATION_CODE,A.MR_NO,A.IPD_NO, '' AS AGE, B.BIRTH_DATE,  "
			+ "       D.USER_NAME AS PHA_CHECK_CODE, A.PHA_CHECK_DATE, A.URGENT_FLG, E.DCTAGENT_FLG, "
			+ " A.DEPT_CODE, A.ORDER_DR_CODE, A.TAKE_DAYS, F.FREQ_CHN_DESC, G.ROUTE_CHN_DESC, A.DCT_TAKE_QTY, SUM(E.MEDI_QTY) * A.TAKE_DAYS AS MEDI_QTYALL, H.DEPT_CHN_DESC, I.STATION_DESC, J.USER_NAME "
			+ "	FROM ODI_ORDER A , SYS_PATINFO B ,SYS_BED C, SYS_OPERATOR D, ODI_DSPNM E, SYS_PHAFREQ F, SYS_PHAROUTE G, SYS_DEPT H, SYS_STATION I, SYS_OPERATOR J "
			+ "	WHERE A.TEMPORARY_FLG='N' AND B.MR_NO(+)=A.MR_NO AND C.BED_NO=A.BED_NO "
			// + "AND (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y') "
			+ "AND A.RX_KIND='IG' AND A.PHA_CHECK_CODE = D.USER_ID(+) "
			+ " AND A.CASE_NO = E.CASE_NO AND A.ORDER_NO = E.ORDER_NO AND A.ORDER_SEQ = E.ORDER_SEQ "
			+ " AND A.FREQ_CODE = F.FREQ_CODE AND A.ROUTE_CODE = G.ROUTE_CODE(+) "
			+ " AND A.DEPT_CODE = H.DEPT_CODE "
			+ " AND A.STATION_CODE = I.STATION_CODE "
			+ " AND A.ORDER_DR_CODE = J.USER_ID ";

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		TParm stationParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM SYS_STATION WHERE ORG_CODE='"
						+ Operator.getDept() + "' "));
		tblPat = (TTable) this.getComponent("TBL_PAT");
		tblDtl = (TTable) this.getComponent("TBL_DTL");
		passIsReady = SYSNewRegionTool.getInstance().isIREASONABLEMED(
				Operator.getRegion());
		enforcementFlg = "Y".equals(TConfig.getSystemValue("EnforcementFlg"));
		warnFlg = Integer.parseInt(TConfig.getSystemValue("WarnFlg"));
		isNsCheck = InwUtil.getInstance().getNsCheckEXEFlg();
		callFunction("UI|TBL_PAT|addEventListener", new Object[] {
				"table.checkBoxClicked", this, "onTableCheckBoxClicked" });

		onClear();
		odiOrdercat = "A.ORDER_CAT1_CODE='PHA_G'";
		if (passIsReady) {
			if (!PassTool.getInstance().init()) {
				this.messageBox("������ҩ��ʼ��ʧ�ܣ�");
			}
		}
	}

	/**
	 * ���
	 */
	public void onClear() {
		Timestamp t = TJDODBTool.getInstance().getDBTime();
		this.setValue("START_DATE", StringTool.rollDate(t, -7));
		this.setValue("END_DATE", t);
		this.setValue("UDST", Y);
		this.setValue("EXEC_DEPT_CODE", Operator.getDept());
		// this.setValue("EXEC_DEPT_CODE", "308003");
		// this.setValue("AGENCY_ORG_CODE", NULL);
		this.setValue("STA", Y);
		this.setValue("UNCHECK", Y);
		tblPat.removeRowAll();
		tblDtl.removeRowAll();
		this.setValue("TAKE_DAYS", 0);
		this.setValue("DCT_TAKE_QTY", NULL);
		this.setValue("FREQ_CODE", NULL);
		this.setValue("ROUTE_CODE", NULL);
		this.setValue("TOT_GRAM", NULL);
		this.setValue("PACKAGE_TOT", NULL);
		this.setValue("DCTAGENT_CODE", NULL);
		this.setValue("DR_NOTE", NULL);
		this.setValue("NO", NULL);
		this.setValue("NAME", NULL);

		setLockColumn();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		StringBuffer sb = new StringBuffer(PAT_SQL);
		sb.append("").append(getWhere());
		// ===========pangben modify 20110512 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			sb.append(" AND E.REGION_CODE='" + Operator.getRegion() + "'");
		}
		// ===========pangben modify 20110512 stop

		// ִ,40,boolean;����,60,boolean;����,80;����,80;������,100;��ʿվ,120;������,120;סԺ��,120
		// EXEC;URGENT_FLG;BED_NO;PAT_NAME;PRESRT_NO;STATION_CODE;MR_NO;IPD_NO
		String sql = sb
				.append(" GROUP BY A.CASE_NO,A.BED_NO,B.PAT_NAME,A.RX_NO,A.STATION_CODE,A.MR_NO,A.IPD_NO, B.BIRTH_DATE, D.USER_NAME, A.PHA_CHECK_DATE, A.URGENT_FLG, E.DCTAGENT_FLG, A.DEPT_CODE, A.ORDER_DR_CODE, A.TAKE_DAYS, F.FREQ_CHN_DESC, G.ROUTE_CHN_DESC, A.DCT_TAKE_QTY, H.DEPT_CHN_DESC, I.STATION_DESC, J.USER_NAME ")
				.append(" ORDER BY A.CASE_NO,A.RX_NO ").toString();
		// System.out.println("sql======chn==="+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		// println("onQuery->"+sql.toString());
		Timestamp date = SystemTool.getInstance().getDate();
		for (int i = 0; i < parm.getCount("AGE"); i++) {
			parm.setData(
					"AGE",
					i,
					StringUtil.getInstance().showAge(
							parm.getTimestamp("BIRTH_DATE", i), date));
		}

		sql = null;
		tblPat.setParmValue(parm);
		if (StringTool.getBoolean(this.getValueString("MR"))
				|| StringTool.getBoolean(this.getValueString("BED"))) {
			this.setValue("NAME", parm.getValue("PAT_NAME", 0));
		}

		setLockColumn();
		/*
		 * ִ,40,boolean;����,80;����,80;������,100;��ʿվ,120;������,120;סԺ��,120
		 * EXEC;BED_NO;PAT_NAME;RX_NO;STATION_CODE;MR_NO;IPD_NO
		 */
	}

	/**
	 * ȡ��WHERE����
	 * 
	 * @return
	 */
	public String getWhere() {
		StringBuffer result = new StringBuffer();
		String startDate = StringTool.getString(
				TCM_Transform.getTimestamp(this.getValue("START_DATE")),
				"yyyyMMddHHmmss");
		String endDate = StringTool.getString(
				TCM_Transform.getTimestamp(this.getValue("END_DATE")),
				"yyyyMMddHHmmss").substring(0, 8)
				+ "235959";
		if (StringTool.getBoolean(this.getValueString("UNCHECK"))) {
			result.append(" AND A.ORDER_DATE >=TO_DATE('" + startDate
					+ "','YYYYMMDDHH24MISS') AND A.ORDER_DATE<=TO_DATE('"
					+ endDate
					+ "','YYYYMMDDHH24MISS') AND A.PHA_CHECK_DATE IS NULL");
		} else {
			result.append(" AND A.PHA_CHECK_DATE >=TO_DATE('" + startDate
					+ "','YYYYMMDDHH24MISS') AND A.PHA_CHECK_DATE<=TO_DATE('"
					+ endDate + "','YYYYMMDDHH24MISS')");
		}
		result.append(" AND A.EXEC_DEPT_CODE='"
				+ this.getValueString("EXEC_DEPT_CODE") + "'");
		// if(!StringUtil.isNullString(this.getValueString("AGENCY_ORG_CODE"))){
		// result.append(" AND A.AGENCY_ORG_CODE='" +
		// this.getValueString("AGENCY_ORG_CODE")+
		// "'");
		// }
		if (StringTool.getBoolean(this.getValueString("STA"))) {
			if (!StringUtil.isNullString(this.getValueString("STATIONCOM"))) {
				result.append(" AND A.STATION_CODE='"
						+ this.getValueString("STATIONCOM") + "'");
			}
		} else if (StringTool.getBoolean(this.getValueString("MR"))) {
			String mrNo = StringTool.fill0(this.getValueString("NO"), PatTool.getInstance().getMrNoLength());//====cehnxi
			this.setValue("NO", mrNo);
			result.append(" AND A.MR_NO='" + mrNo + "'");
		} else {
			result.append(" AND A.BED_NO='" + this.getValueString("NO") + "' ");
		}
		if (isNsCheck) {
			result.append(" AND A.NS_CHECK_CODE IS NOT NULL");
		}
		return result.toString();
	}

	/**
	 * �����б���
	 */
	public void onTblPatClick() {
		// TParm parm=tblPat.getParmValue();
		// int rows=tblPat.getSelectedRow();
		// for (int i = 0; i < tblPat.getRowCount(); i++) {
		// if (i == rows) {
		// continue;
		// }
		// if (StringTool.getBoolean(this.getValueString("UNCHECK"))) {
		// tblPat.setItem(rows, "DCTAGENT_FLG", false);
		// parm.setData("DCTAGENT_FLG", i, "N");
		// }
		// tblPat.setItem(rows, "EXEC", false);
		// parm.setData("EXEC", i, "N");
		// }
		//
		// //tblPat.acceptText();
		// String
		// sql=" SELECT CASE_NO , ORDER_NO,ORDER_SEQ,ORDER_DESC,MEDI_QTY,FREQ_CODE,ROUTE_CODE,TAKE_DAYS,DCT_TAKE_QTY,DCTAGENT_CODE,DR_NOTE,DCTEXCEP_CODE,PACKAGE_AMT,RX_NO FROM ODI_ORDER "
		// +
		// " WHERE CASE_NO='" +parm.getValue("CASE_NO",rows)+
		// "' AND RX_NO='" +parm.getValue("RX_NO",rows)+
		// "' ORDER BY ORDER_SEQ ";
		// //println("sql->"+sql);
		// saveParm=new TParm();
		// saveParm=new TParm(TJDODBTool.getInstance().select(sql));
		// //println(saveParm);
		// TParm parmShow=new TParm();
		// int count=saveParm.getCount("ORDER_SEQ");
		// if(count<=0)
		// return;
		// if(count%4==0)
		// count=count/4*4;
		// else
		// count=count/4*4+4;
		// double totQty=0.0;
		// //ORDER_DESC0;MEDI_QTY0;DCTEXCEP_CODE0;ORDER_DESC1;MEDI_QTY1;DCTEXCEP_CODE1;ORDER_DESC2;MEDI_QTY2;DCTEXCEP_CODE2;ORDER_DESC3;MEDI_QTY3;DCTEXCEP_CODE3
		// for(int i=0;i<count;i++){
		// //println(i%4);
		// //println(saveParm.getDouble("MEDI_QTY",i));
		// parmShow.addData("ORDER_DESC"+(i%4),
		// saveParm.getValue("ORDER_DESC",i));
		// parmShow.addData("MEDI_QTY"+(i%4), saveParm.getDouble("MEDI_QTY",i));
		// parmShow.addData("DCTEXCEP_CODE"+(i%4),
		// saveParm.getValue("DCTEXCEP_CODE",i));
		// totQty+=saveParm.getDouble("MEDI_QTY",i);
		// }
		// this.setValue("TAKE_DAYS", saveParm.getInt("TAKE_DAYS",0));
		// this.setValue("DCT_TAKE_QTY",
		// String.valueOf(saveParm.getDouble("DCT_TAKE_QTY",0)));
		// this.setValue("FREQ_CODE", saveParm.getValue("FREQ_CODE",0));
		// this.setValue("ROUTE_CODE", saveParm.getValue("ROUTE_CODE",0));
		// this.setValue("TOT_GRAM", String.valueOf(totQty));
		// this.setValue("PACKAGE_AMT", saveParm.getDouble("PACKAGE_AMT",0));
		// this.setValue("DCTAGENT_CODE", saveParm.getValue("DCTAGENT_CODE",0));
		// this.setValue("DR_NOTE", saveParm.getValue("DR_NOTE",0));
		// //println("parmShow->"+parmShow);
		//
		// tblDtl.setParmValue(parmShow);
	}

	/**
	 * ����
	 */
	public void onSave() {
		tblPat.acceptText();
		if (!TypeTool.getBoolean(this.getValue("UNCHECK"))) {
			this.messageBox_("�����ҩƷ�����ظ����");
			return;
		}
		TParm parm = new TParm();
		if (saveParm == null) {
			return;
		}
		int count = saveParm.getCount("RX_NO");
		if (count < 1) {
			return;
		}
		for (int i = 0; i < count; i++) {
			parm.addData("OPT_USER", Operator.getID());
			parm.addData("OPT_TERM", Operator.getIP());
			parm.addData("CASE_NO", saveParm.getValue("CASE_NO", i));
			parm.addData("ORDER_NO", saveParm.getValue("ORDER_NO", i));
			parm.addData("RX_NO", saveParm.getValue("RX_NO", i));
			// ��Ӵ���ע��
			parm.addData("DCTAGENT_FLG",
					tblPat.getItemData(tblPat.getSelectedRow(), "DCTAGENT_FLG"));
		}
		// ������ҩ
		if (!checkDrugAuto()) {
			return;
		}
		parm = TIOM_AppServer.executeAction("action.udd.UddAction",
				"onUpdateCheck", parm);
		if (parm.getErrCode() != 0) {
			this.messageBox("E0001");
		} else {
			this.messageBox("P0001");
			onPrint();
			onClear();
		}
	}

	/**
	 * ȡ�����
	 */
	public void onDelete() {
		if (TypeTool.getBoolean(this.getValue("UNCHECK"))) {
			this.messageBox_("�ޱ�������");
			return;
		}
		TParm parm = new TParm();
		// System.out.println("savePaarm==="+saveParm);
		for (int i = 0; i < saveParm.getCount("ORDER_SEQ"); i++) {
			parm.addData("OPT_USER", Operator.getID());
			parm.addData("OPT_TERM", Operator.getIP());
			parm.addData("CASE_NO", saveParm.getValue("CASE_NO", i));
			parm.addData("ORDER_NO", saveParm.getValue("ORDER_NO", i));
			parm.addData("ORDER_SEQ", saveParm.getInt("ORDER_SEQ", i));
		}
		// System.out.println("parm========"+parm);
		if (parm.getCount("OPT_USER") < 1) {
			this.messageBox_("�޿ɱ�������");
			return;
		}
		if (!isDosage()) {
			return;
		}

		parm = TIOM_AppServer.executeAction("action.udd.UddAction",
				"onUpdateUnCheck", parm);
		if (parm.getErrCode() != 0) {
			// this.messageBox_(parm.getErrText());
			this.messageBox("E0001");
		} else {
			this.messageBox("P0001");
		}
		onClear();

	}

	/**
	 * ȡ��
	 * 
	 * @return
	 */
	private boolean isDosage() {
		if (saveParm == null) {
			return false;
		}
		int count = saveParm.getCount();
		if (count < 1) {
			return false;
		}
		for (int i = 0; i < count; i++) {
			if (!UddChnCheckTool.getInstance().isDosage(saveParm.getRow(i))) {
				this.messageBox_("ҩƷ����ҩ,����ȡ�����");
				return false;
			}
		}
		return true;
	}

	/**
	 * ��ӡ����ǩ(����סԺ��ҽ����ǩ)
	 */
	public void onPrint() {
		TParm tablParm = tblPat.getParmValue().getRow(tblPat.getSelectedRow());
		String rxNo = tablParm.getValue("RX_NO");
		String case_no = tablParm.getValue("CASE_NO");
		if (rxNo.length() <= 0) {
			// ��ѡ�񴦷�ǩ
			this.messageBox("E0029");
			return;
		}
		TParm parm = this.getOrderParm("IG", rxNo, case_no);
		if (parm.getCount() <= 0) {
			// �޴�ӡ���ݣ�
			this.messageBox("E0010");
			return;
		}
		TParm orderParm = new TParm();
		orderParm.setData("RX_TYPE", "DS");
		orderParm.setData("CASE_NO", case_no);
		orderParm.setData("RX_NO", rxNo);
		orderParm.setData("ADDRESS", "TEXT", "ODIStationControl LPT1");
		orderParm.setData("PRINT_TIME", "TEXT", StringTool.getString(SystemTool
				.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss"));
		if ("en".equals(this.getLanguage())) {
			orderParm.setData("HOSP_NAME", "TEXT", Manager.getOrganization()
					.getHospitalENGFullName(Operator.getRegion()));
		} else {
			orderParm.setData("HOSP_NAME", "TEXT", Manager.getOrganization()
					.getHospitalCHNFullName(Operator.getRegion()));
		}
		orderParm.setData("ORDER_TYPE", "TEXT", "(סԺ��ҽ Chn Med)");
		orderParm.setData(
				"ORG_CODE",
				"TEXT",
				"ҩ��:"
						+ ((TextFormatINDOrg) this
								.getComponent("EXEC_DEPT_CODE")).getText());

		String sql_adm_inp = "SELECT A.CASE_NO, A.MR_NO, A.IPD_NO, "
				+ " B.SEX_CODE, B.BIRTH_DATE, A.CTZ1_CODE, A.ADM_DATE "
				+ " FROM ADM_INP A, SYS_PATINFO B "
				+ " WHERE A.MR_NO = B.MR_NO AND A.CASE_NO = '" + case_no + "'";
		TParm adm_parm = new TParm(TJDODBTool.getInstance().select(sql_adm_inp));
		if (adm_parm == null || adm_parm.getCount("CASE_NO") <= 0) {
			this.messageBox("������Ϣ����");
			return;
		}
		orderParm.setData(
				"PAY_TYPE",
				"TEXT",
				"�ѱ�"
						+ OdiOrderTool.getInstance().getCTZDesc(
								adm_parm.getValue("CTZ1_CODE", 0)));
		orderParm.setData("PAT_NAME", "TEXT",
				"������" + tablParm.getValue("PAT_NAME"));
		orderParm.setData(
				"SEX_CODE",
				"TEXT",
				"�Ա�"
						+ OdiUtil.getInstance().getDictionary("SYS_SEX",
								adm_parm.getValue("SEX_CODE", 0)));
		orderParm.setData("AGE", "TEXT", "���䣺" + tablParm.getValue("AGE"));
		orderParm.setData("MR_NO", "TEXT", "�����ţ�" + tablParm.getValue("MR_NO"));
		orderParm.setData("DEPT_CODE", "TEXT",
				"���ң�" + tablParm.getValue("DEPT_CHN_DESC"));
		orderParm.setData("CLINIC_ROOM", "TEXT",
				"������" + tablParm.getValue("STATION_DESC"));
		orderParm.setData("DR_CODE", "TEXT",
				"ҽ����" + tablParm.getValue("USER_NAME"));
		orderParm.setData("ADM_DATE", "TEXT",
				"ʱ�䣺"
						+ adm_parm.getValue("ADM_DATE", 0).substring(0, 19)
								.replace('-', '/'));
		orderParm.setData("BAR_CODE", "TEXT", tablParm.getValue("MR_NO"));
		orderParm.setData("TAKE_DAYS", "TEXT",
				"����:" + tablParm.getValue("TAKE_DAYS"));
		orderParm.setData("FREQ_CODE", "TEXT",
				"��Σ�" + tablParm.getValue("FREQ_CHN_DESC"));
		orderParm.setData("ROUTE_CODE", "TEXT",
				"������" + tablParm.getValue("ROUTE_CHN_DESC"));
		orderParm.setData("DCT_TAKE_QTY", "TEXT",
				"ÿ�η�������" + tablParm.getValue("DCT_TAKE_QTY") + "�� k");

		orderParm.setData("PACKAGE_TOT", "TEXT",
				"ÿ���ܿ�����" + tablParm.getValue("MEDI_QTYALL") + "��");
		int rowCount = parm.getCount();
		int orderRowCount = 1;
		int orderColumns = 1;
		for (int i = 0; i < rowCount; i++) {
			orderParm.setData("ORDER_DESC" + orderRowCount + orderColumns,
					"TEXT", parm.getData("ORDER_DESC", i));
			orderParm.setData("MEDI_QTY" + orderRowCount + orderColumns,
					"TEXT", parm.getData("MEDI_QTY", i));
			orderParm.setData(
					"DCTAGENT" + orderRowCount + orderColumns,
					"TEXT",
					OdiUtil.getInstance().getDictionary("PHA_DCTEXCEP",
							parm.getValue("DCTEXCEP_CODE", i)));
			if (orderColumns == 4)
				orderColumns = 1;
			if ((i + 1) % 4 == 0)
				orderRowCount++;
			else
				orderColumns++;
		}
		TParm parmAmt = this.getOrderParmAmt("IG", rxNo, case_no);
		if (parmAmt.getCount() == 0 || parmAmt.getData("AMT", 0) == null
				|| parmAmt.getValue("AMT", 0).equalsIgnoreCase("null")
				|| parmAmt.getDouble("AMT", 0) == 0) {
			String sql_amt = "SELECT ROUND(SUM(A.DOSAGE_QTY  * B.OWN_PRICE),2) "
					+ " AS AMT FROM ODI_ORDER A, SYS_FEE B "
					+ " WHERE A.ORDER_CODE = B.ORDER_CODE "
					+ " AND A.CASE_NO = '"
					+ case_no
					+ "' "
					+ " AND A.RX_NO = '" + rxNo + "'";
			TParm parm_sql_amt = new TParm(TJDODBTool.getInstance().select(
					sql_amt));
			orderParm.setData("AMT", "TEXT",
					"���:" + parm_sql_amt.getDouble("AMT", 0) + "Ԫ");
		} else {
			orderParm.setData("AMT", "TEXT",
					"���:" + parmAmt.getDouble("AMT", 0) + "Ԫ");
		}
		if ("Y".equals(tablParm.getValue("DCTAGENT_FLG"))) {
			orderParm.setData("DCTAGENT_FLG", "TEXT", "��");
		} else {
			orderParm.setData("DCTAGENT_FLG", "TEXT", "");
		}
		// System.out.println("orderParm-----" + orderParm);
		this.openPrintDialog("%ROOT%\\config\\prt\\UDD\\UddChnOrderSheet.jhw",
				orderParm);
	}

	/**
	 * �õ���ӡ����
	 * 
	 * @param type
	 *            String
	 * @param rxNo
	 *            String
	 * @return TParm
	 */
	public TParm getOrderParm(String type, String rxNo, String case_no) {
		TParm result = new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM ODI_ORDER WHERE CASE_NO='" + case_no
						+ "' AND RX_KIND='" + type + "' AND RX_NO='" + rxNo
						+ "' ORDER BY ORDER_NO,ORDER_SEQ"));
		return result;
	}

	/**
	 * �õ���ӡ����
	 * 
	 * @param type
	 *            String
	 * @param rxNo
	 *            String
	 * @return TParm
	 */
	public TParm getOrderParmAmt(String type, String rxNo, String case_no) {
		String sql = " SELECT SUM(TOT_AMT) AMT"
				+ " FROM ODI_ORDER A,IBS_ORDD B" + " WHERE A.CASE_NO='"
				+ case_no + "'" + " AND A.RX_KIND='" + type + "'"
				+ " AND A.RX_NO='" + rxNo + "'" + " AND A.CASE_NO = B.CASE_NO"
				+ " AND A.ORDER_NO = B.ORDER_NO"
				+ " AND A.ORDER_SEQ = B.ORDER_SEQ";
		// System.out.println("sql----"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * ���������嵥�е�ִ����
	 * 
	 * @param obj
	 *            Object
	 */
	public void onTableCheckBoxClicked(Object obj) {
		tblPat.acceptText();
		// ���ѡ�е���
		int column = tblPat.getSelectedColumn();
		int row_d = tblPat.getSelectedRow();
		TParm parm = tblPat.getParmValue();
		if (column == 0) {
			if ("Y".equals(parm.getValue("EXEC", row_d))) {
				for (int i = 0; i < tblPat.getRowCount(); i++) {
					if (i == row_d) {
						parm.setData("EXEC", row_d, "Y");
					} else {
						parm.setData("EXEC", i, "N");
						tblPat.setItem(i, "EXEC", false);
						if (TypeTool.getBoolean(this.getValue("UNCHECK"))) {
							tblPat.setItem(i, "DCTAGENT_FLG", false);
						}
					}
				}
				String sql = " SELECT CASE_NO , ORDER_NO,ORDER_SEQ,ORDER_DESC,MEDI_QTY,FREQ_CODE,ROUTE_CODE,TAKE_DAYS,DCT_TAKE_QTY,DCTAGENT_CODE,DR_NOTE,DCTEXCEP_CODE,PACKAGE_AMT,RX_NO FROM ODI_ORDER "
						+ " WHERE CASE_NO='"
						+ parm.getValue("CASE_NO", row_d)
						+ "' AND RX_NO='"
						+ parm.getValue("RX_NO", row_d)
						+ "' ORDER BY ORDER_SEQ ";
				// println("sql->"+sql);
				saveParm = new TParm();
				saveParm = new TParm(TJDODBTool.getInstance().select(sql));
				// println(saveParm);
				TParm parmShow = new TParm();
				int count = saveParm.getCount("ORDER_SEQ");
				if (count <= 0)
					return;
				if (count % 4 == 0)
					count = count / 4 * 4;
				else
					count = count / 4 * 4 + 4;
				double totQty = 0.0;
				for (int i = 0; i < count; i++) {
					// println(i%4);
					// println(saveParm.getDouble("MEDI_QTY",i));
					parmShow.addData("ORDER_DESC" + (i % 4),
							saveParm.getValue("ORDER_DESC", i));
					parmShow.addData("MEDI_QTY" + (i % 4),
							saveParm.getDouble("MEDI_QTY", i));
					parmShow.addData("DCTEXCEP_CODE" + (i % 4),
							saveParm.getValue("DCTEXCEP_CODE", i));
					totQty += saveParm.getDouble("MEDI_QTY", i);
				}
				this.setValue("TAKE_DAYS", saveParm.getInt("TAKE_DAYS", 0));
				this.setValue("DCT_TAKE_QTY",
						String.valueOf(saveParm.getDouble("DCT_TAKE_QTY", 0)));
				this.setValue("FREQ_CODE", saveParm.getValue("FREQ_CODE", 0));
				this.setValue("ROUTE_CODE", saveParm.getValue("ROUTE_CODE", 0));
				this.setValue("TOT_GRAM", String.valueOf(totQty));
				this.setValue("PACKAGE_AMT",
						saveParm.getDouble("PACKAGE_AMT", 0));
				this.setValue("DCTAGENT_CODE",
						saveParm.getValue("DCTAGENT_CODE", 0));
				this.setValue("DR_NOTE", saveParm.getValue("DR_NOTE", 0));
				// println("parmShow->"+parmShow);
				tblDtl.setParmValue(parmShow);
			} else {
				tblDtl.removeRowAll();
			}
		}

	}

	/**
	 * �趨TABLE������
	 */
	public void setLockColumn() {
		String lock_column = "2,3,4,5,6,7,8,9,10,11";
		if (TypeTool.getBoolean(this.getValue("UNCHECK"))) {
			lock_column = "2,3,4,5,6,7,8,9,10,11";
			callFunction("UI|save|setEnabled", true);
			callFunction("UI|delete|setEnabled", false);
		} else {
			lock_column = "1,2,3,4,5,6,7,8,9,10,11";
			callFunction("UI|save|setEnabled", false);
			callFunction("UI|delete|setEnabled", true);
		}
		tblPat.setLockColumns(lock_column);
	}

	/**
	 * �õ��������
	 * 
	 * @return TParm
	 */
	public TParm MedtableClick() {
		TParm parm = new TParm();
		int Column = tblDtl.getSelectedColumn();
		if (Column < 0) {
		}
		if (Column == 0 || Column == 1 || Column == 2) {
			System.out.println("parm" + saveParm);
			String value = saveParm.getValue("ORDER_CODE", 0);
			String orderNO = saveParm.getValue("ORDER_NO", 0);
			int seq = saveParm.getInt("ORDER_SEQ", 0);
			parm.setData("ORDER_CODE", value);
			parm.setData("ORDER_NO", orderNO);
			parm.setData("ORDER_SEQ", seq);
		}
		if (Column == 3 || Column == 4 || Column == 5) {
			String value = saveParm.getValue("ORDER_CODE", 1);
			String orderNO = saveParm.getValue("ORDER_NO", 1);
			int seq = saveParm.getInt("ORDER_SEQ", 1);
			parm.setData("ORDER_CODE", value);
			parm.setData("ORDER_NO", orderNO);
			parm.setData("ORDER_SEQ", seq);
		}
		if (Column == 6 || Column == 7 || Column == 8) {
			String value = saveParm.getValue("ORDER_CODE", 2);
			String orderNO = saveParm.getValue("ORDER_NO", 2);
			int seq = saveParm.getInt("ORDER_SEQ", 2);
			parm.setData("ORDER_CODE", value);
			parm.setData("ORDER_NO", orderNO);
			parm.setData("ORDER_SEQ", seq);
		}
		if (Column == 9 || Column == 10 || Column == 11) {
			String value = saveParm.getValue("ORDER_CODE", 3);
			String orderNO = saveParm.getValue("ORDER_NO", 3);
			int seq = saveParm.getInt("ORDER_SEQ", 3);
			parm.setData("ORDER_CODE", value);
			parm.setData("ORDER_NO", orderNO);
			parm.setData("ORDER_SEQ", seq);
		}
		return parm;
	}

	/**
	 * ҩƷ��Ϣ��ѯ
	 */
	public void queryDrug() {
		if (!passIsReady) {
			messageBox("������ҩδ����");
			return;
		}
		if (!PassTool.getInstance().init()) {
			this.messageBox("������ҩ��ʼ��ʧ�ܣ��˹��ܲ���ʹ�ã�");
			return;
		}
		int Column = getTable("TBL_DTL").getSelectedColumn();
		if (Column < 0) {
			return;
		}
		String value = (String) this
				.openDialog("%ROOT%\\config\\pha\\PHAOptChoose.x");
		if (value == null || value.length() == 0) {
			return;
		}
		int conmmand = Integer.parseInt(value);
		if (conmmand != 6) {
			PassTool.getInstance().setQueryDrug(
					MedtableClick().getValue("ORDER_CODE"), conmmand);
		} else {
			PassTool.getInstance().setWarnDrug2(
					MedtableClick().getValue("ORDER_NO"),
					" " + MedtableClick().getValue("ORDER_SEQ"));
		}

	}

	/**
	 * �ֶ���������ҩ
	 */
	public void checkDrugHand() {
		if (!passIsReady) {
			messageBox("������ҩδ����");
			return;
		}
		if (!PassTool.getInstance().init()) {
			this.messageBox("������ҩ��ʼ��ʧ�ܣ��˹��ܲ���ʹ�ã�");
			return;
		}
		if (saveParm.getValue("CASE_NO", 0) == null) {
			return;
		}
		PassTool.getInstance().init();
		PassTool.getInstance().setadmPatientInfo(
				saveParm.getValue("CASE_NO", 0));
		PassTool.getInstance().setAllergenInfo(saveParm.getValue("MR_NO", 0));
		PassTool.getInstance().setadmMedCond(saveParm.getValue("CASE_NO", 0));
		TParm parm = PassTool.getInstance().setadmRecipeInfoHand(
				saveParm.getValue("CASE_NO", 0), odiOrdercat,"");
		isWarn(parm);
	}

	/**
	 * �Զ���������ҩ
	 */
	private boolean checkDrugAuto() {
		if (!passIsReady) {
			return true;
		}
		if (!PassTool.getInstance().init()) {
			return true;
		}
		PassTool.getInstance().setadmPatientInfo(
				saveParm.getValue("CASE_NO", 0));
		PassTool.getInstance().setAllergenInfo(saveParm.getValue("MR_NO", 0));
		PassTool.getInstance().setadmMedCond(saveParm.getValue("CASE_NO", 0));
		TParm parm = PassTool.getInstance().setadmRecipeInfoAuto(
				saveParm.getValue("CASE_NO", 0), odiOrdercat,"");
		if (!isWarn(parm)) {
			return true;
		}
		if (enforcementFlg) {
			return false;
		}
		if (JOptionPane.showConfirmDialog(null, "��ҩƷʹ�ò�����,�Ƿ�浵?", "��Ϣ",
				JOptionPane.YES_NO_OPTION) != 0) {
			return false;
		}
		return true;
	}

	private boolean isWarn(TParm parm) {
		boolean warnFlg = false;
		for (int i = 0; i < parm.getCount("ORDER_NO"); i++) {
			int flg = parm.getInt("FLG", i);
			if (!warnFlg) {
				if (getWarn(flg)) {
					warnFlg = true;
				} else {
					warnFlg = false;
				}
			}
		}
		return warnFlg;
	}

	private boolean getWarn(int flg) {
		if (warnFlg != 3 && flg != 3) {
			if (warnFlg != 2 && flg != 2) {
				if (flg >= warnFlg) {
					return true;
				} else {
					return false;
				}
			} else if (warnFlg == 2 && flg != 2) {
				return false;
			} else if (warnFlg != 2 && flg == 2) {
				return true;
			} else if (warnFlg == 2 && flg == 2) {
				return true;
			}
		} else if (warnFlg == 3 && flg != 3) {
			return false;
		} else if (warnFlg != 3 && flg == 3) {
			return true;
		} else if (warnFlg == 3 && flg == 3) {
			return true;
		}
		return false;
	}

	/**
	 * ȡ�ñ��ؼ�
	 * 
	 * @param tableName
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tableName) {
		return (TTable) getComponent(tableName);
	}

	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
}
