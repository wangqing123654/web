package jdo.ins;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * <p>
 * Title: ����֧����Ϣ
 * </p>
 * 
 * <p>
 * Description:����֧����Ϣ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) xueyf
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author xueyf 2011.12.30
 * @version 1.0
 */
public class INSDownloadPayTool extends TJDODBTool {

	/**
	 * ʵ��
	 */
	public static INSDownloadPayTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INSNoticeTool
	 */
	public static INSDownloadPayTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSDownloadPayTool();
		return instanceObject;
	}

	/**
	 * ����ҽ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertIBSPay(TParm parm, TConnection connection) {
		//System.out.println("deleteIBSPay=========" + parm);
		String deleteIBSPaySQL = "DELETE INS_IBS_PAY WHERE REPORT_CODE='"
				+ parm.getValue("REPORT_CODE") + "' and CTZ_CODE='"
				+ parm.getValue("CTZ_CODE") + "' and NHI_HOSP_CODE='"
				+ parm.getValue("NHI_HOSP_CODE") + "' and NHI_PAY_TYPE='"
				+ parm.getValue("PAY_TYPE") + "'";
		//System.out.println(deleteIBSPaySQL);
		TParm result = new TParm(this.update(deleteIBSPaySQL, connection));
		String insertIBSPaySQL = "INSERT INTO JAVAHIS.INS_IBS_PAY ( REPORT_CODE, NHI_HOSP_CODE, NHI_PAY_TYPE, CTZ_CODE, REGION_CODE, YEAR_MON, CTZ_COUNT, TOTAL_AMT, OWN_AMT, ADDPAY_AMT, TOTAL_NHI_AMT, ACTURAL_STANDARD_AMT, OWN_AMT_UPON_STANDARD," +
				" BLOOD_OWN_AMT, OWN_AMT_IN_ASSIST, OWN_AMT_UPON_ASSIST, TOTAL_NHI_AMT_AGENT_PAY, TOTAL_NHI_AMT_ASSIST_PAY, REFUSE_AMT, TOTAL_AMT_CONFIRM, TOTAL_NHI_AMT_AGENT_PAY_CONFIR, TOTAL_NHI_AMT_ASSIST_PAY_CONFI, TOTAL_AMT_ADJUST," +
				" TOTAL_AMT_REFUSE_ADJUST, CENTRAL_SUM_YEAR_MON, SORCE_SERIAL, EXAM_PRE_AMT, EXAM_PAY_AMT, EXAM_ACTURAL_AMT, MEMO, OPT_USER, OPT_DATE, OPT_TERM) VALUES "
				+ "('"
				+ parm.getValue("REPORT_CODE")
				+ "','"
				+ parm.getValue("NHI_HOSP_CODE")
				+ "','"
				+ parm.getValue("PAY_TYPE")
				+ "','"
				+ parm.getValue("CTZ_CODE")
				+ "','"
				+ parm.getValue("REGION_CODE")
				+ "','"
				+ parm.getValue("ISSUE")
				+ parm.getValue("SUM_YEAR_MON")
				+ "','"
				+ parm.getValue("CTZ_COUNT")
				+ "','"
				+ parm.getValue("TOTAL_AMT")
				+ "','"
				+ parm.getValue("OWN_AMT")
				+ "','"
				+ parm.getValue("ADDPAY_AMT")
				+ "','"
				+ parm.getValue("TOTAL_NHI_AMT")
				+ "','"
				+ parm.getValue("BCSSQF_STANDRD_AMT")
				+ "','"
				+ parm.getValue("INS_STANDARD_AMT")
				+ "','"
				+ parm.getValue("BLOOD_OWN_AMT")
				+ "','"
				+ parm.getValue("OWN_AMT_IN_ASSIST")
				+ "','"
				+ parm.getValue("OWN_AMT_UPON_ASSIST")
				+ "','"
				+ parm.getValue("TOTAL_NHI_AMT_AGENT_PAY")
				+ "','"
				+ parm.getValue("TOTAL_NHI_AMT_ASSIST_PAY")
				+ "','"
				+ parm.getValue("REFUSE_AMT")
				+ "','"
				+ parm.getValue("TOTAL_AMT_CONFIRM")
				+ "','"
				+ parm.getValue("TOTAL_NHI_AMT_AGENT_PAY_CONFIRM")
				+ "','"
				+ parm.getValue("TOTAL_NHI_AMT_ASSIST_PAY_CONFIRM")
				+ "','"
				+ parm.getValue("TOTAL_AMT_ADJUST")
				+ "','"
				+ parm.getValue("TOTAL_AMT_REFUSE_ADJUST")
				+ "','"
				+ parm.getValue("CENTRAL_SUM_YEAR_MON")
				+ "','"
				+ parm.getValue("SORCE_SERIAL")
				+
				// ��Ӧ���ϵ��ֶ�CTZ_COUNT,TOTAL_AMT,ADDPAY_AMT,TOTAL_NHI_AMT,SORCE_SERIAL,KHYL_AMT,ACCOUNT_PAY_AMT,MEMO

				"','"
				+ parm.getValue("KHYL_AMT")
				+ "','"
				+ parm.getValue("EXAM_PAY_AMT")
				+ "','"
				+ parm.getValue("ACCOUNT_PAY_AMT")
				+ "','"
				+ parm.getValue("MEMO")
				+ "','"
				+ parm.getValue("OPT_USER")
				+ "',sysdate ,'" + parm.getValue("OPT_TERM") + "')";
		//System.out.println("insertIBSPay=" + insertIBSPaySQL);
		result = new TParm(this.update(insertIBSPaySQL, connection));
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ҽ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateIBS(TParm parm, TConnection connection) {
		TParm result = new TParm();
		//System.out.println("updateIBS=========" + parm);
		// parm.getValue("")
		String updateIBSSQL = "UPDATE Ins_IBS SET PHA_AMT ="
				+ parm.getDouble("PHA_AMT") + ",PHA_NHI_AMT ="
				+ parm.getDouble("PHA_NHI_AMT") + ", EXM_AMT ="
				+ parm.getDouble("EXM_AMT") + ", EXM_NHI_AMT ="
				+ parm.getDouble("EXM_NHI_AMT") + "," + " TREAT_AMT ="
				+ parm.getDouble("TREAT_AMT") + ", TREAT_NHI_AMT ="
				+ parm.getDouble("TREAT_NHI_AMT") + ", OP_AMT ="
				+ parm.getDouble("OP_AMT") + ", OP_NHI_AMT ="
				+ parm.getDouble("OP_NHI_AMT") + ", BED_AMT ="
				+ parm.getDouble("BED_AMT") + "," + " BED_NHI_AMT ="
				+ parm.getDouble("BED_NHI_AMT") + ", MATERIAL_AMT ="
				+ parm.getDouble("MATERIAL_AMT") + ", MATERIAL_NHI_AMT ="
				+ parm.getDouble("MATERIAL_NHI_AMT") + ", OTHER_AMT ="
				+ parm.getDouble("OTHER_AMT") + "," + " OTHER_NHI_AMT ="
				+ parm.getDouble("OTHER_NHI_AMT") + ", BLOODALL_AMT ="
				+ parm.getDouble("BLOODALL_AMT") + ", BLOODALL_NHI_AMT ="
				+ parm.getDouble("BLOODALL_NHI_AMT") + ", BLOOD_AMT ="
				+ parm.getDouble("BLOOD_AMT") + "," + " BLOOD_NHI_AMT ="
				+ parm.getDouble("BLOOD_NHI_AMT") + ", REFUSE_TOTAL_AMT ="
				+ parm.getDouble("REFUSE_ADD_AMT") + ", AUDIT_TOTAL_AMT ="
				+ parm.getDouble("NUM_AMT") + ","
				+ " RESTART_STANDARD_AMT ="
				+ parm.getDouble("BCSSQF_STANDRD_AMT")
				+ ", STARTPAY_OWN_AMT =" + parm.getDouble("INS_STANDARD_AMT")
				+ ", OWN_AMT =" + parm.getDouble("OWN_AMT") + ","
				+ " PERCOPAYMENT_RATE_AMT ="
				+ parm.getDouble("PERCOPAYMENT_RATE_AMT") + ", ADD_AMT ="
				+ parm.getDouble("ADD_AMT") + ", INS_HIGHLIMIT_AMT ="
				+ parm.getDouble("INS_HIGHLIMIT_AMT") + ","
				+ " TRANBLOOD_OWN_AMT =" + parm.getDouble("TRANBLOOD_OWN_AMT")
				+ ", APPLY_AMT =" + parm.getDouble("APPLY_AMT")
				+ ", HOSP_APPLY_AMT =" + parm.getDouble("HOSP_APPLY_AMT")
				+ ", MATERIAL_SINGLE_AMT =" + parm.getDouble("SPEC_NEED_AMT")
				+ ", NHI_NUM ='" + parm.getValue("REPORT_CODE") + "', "
				+ " OPT_DATE = sysdate, ARMYAI_AMT ="
				+ parm.getDouble("ARMYAI_AMT") + ", PUBMANAI_AMT ="
				+ parm.getDouble("SERVANT_AMT") + "," + " SINGLE_NHI_AMT ="
				+ parm.getDouble("NHI_OWN_AMT")
				+ ", SINGLE_STANDARD_OWN_AMT ="
				+ parm.getDouble("EXT_OWN_AMT")
				+ ", SINGLE_SUPPLYING_AMT ="
				+ parm.getDouble("COMP_AMT") 
				+ " WHERE ADM_SEQ ='" + parm.getValue("ADM_SEQ") + "'";
		// ҽ�����غ�û���ֶ� OTHER_AMT OTHER_NHI_AMT REFUSE_TOTAL_AMT AUDIT_TOTAL_AMT
		result = new TParm(this.update(updateIBSSQL, connection));
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}

	/**
	 * ����ҽ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateConfirmSet(TParm parm, TConnection connection) {
		TParm result = new TParm();
		String updateConfirmSetSQL = " Update ins_Adm_Confirm Set "
				+ "  IN_STATUS='4' ," + "  OPT_USER = '"
				+ parm.getValue("OPT_USER") + "'," + "  OPT_TERM = '"
				+ parm.getValue("OPT_TERM") + "'," + "  OPT_DATE =  sysdate "
				+ " where Adm_seq='" + parm.getValue("ADM_SEQ") + "' ";
		//System.out.println("updateConfirmSetSQL==" + updateConfirmSetSQL);
		result = new TParm(this.update(updateConfirmSetSQL, connection));
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}

	/**
	 * ����ҽ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertIBSDownload(TParm parm, TConnection connection) {
		String deleteIBSDownloadSQL = "DELETE INS_IBSORDER_DOWNLOAD WHERE ADM_SEQ='"
				+ parm.getValue("ADM_SEQ")
				+ "' and SEQ_NO='"
				+ parm.getValue("SEQ_NO") + "'";
		//System.out.println("deleteIBSDownloadSQL====" + deleteIBSDownloadSQL);
		TParm result = new TParm(this.update(deleteIBSDownloadSQL, connection));
		//System.out.println("ok");
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		String insertIBSDownloadSQL = "INSERT INTO INS_IBSORDER_DOWNLOAD (ADM_SEQ,SEQ_NO,REGION_CODE,CHARGE_DATE,NHI_ORDER_CODE,ORDER_CODE,ORDER_DESC, OWN_RATE,DOSE_CODE,STANDARD,PRICE,QTY,TOTAL_AMT,TOTAL_NHI_AMT,OWN_AMT,ADDPAY_AMT," +
				"REFUSE_AMT,REFUSE_REASON_CODE,REFUSE_REASON_NOTE,OP_FLG,CARRY_FLG,PHAADD_FLG,ADDPAY_FLG," +
				"NHI_ORD_CLASS_CODE,OPT_USER,OPT_DATE,OPT_TERM,REPORT_CODE,HYGIENE_TRADE_CODE) VALUES ('"+
				 parm.getValue("ADM_SEQ")+"','"+
				 parm.getValue("SEQ_NO")+"','"+
				 parm.getValue("REGION_CODE")+"',to_date('"+
				 parm.getValue("CHARGE_DATE")+"','yyyyMMddHH24MISS'),'"+
				 parm.getValue("NHI_ORDER_CODE")+"','"+
				 parm.getValue("ORDER_CODE")+"','"+
				 parm.getValue("ORDER_DESC")+"','"+
				 parm.getValue("OWN_RATE")+"','"+
				 parm.getValue("DOSE_CODE")+"','"+
				 parm.getValue("STANDARD")+"','"+
				 parm.getValue("PRICE")+"','"+
				 parm.getValue("QTY")+"','"+
				 parm.getValue("TOTAL_AMT")+"','"+
				 parm.getValue("TOTAL_NHI_AMT")+"','"+
				 parm.getValue("OWN_AMT")+"','"+
				 parm.getValue("ADDPAY_AMT")+"','"+
				 parm.getValue("REFUSE_AMT")+"','"+
				 parm.getValue("REPORT_CODE")+"','"+
				 parm.getValue("REFUSE_DESC")+"','"+
				 parm.getValue("OP_FLG")+"','"+
				 parm.getValue("CARRY_FLG")+"','"+
				 parm.getValue("PHAADD_FLG")+"','"+
				 parm.getValue("ADDPAY_FLG")+"','"+
				 parm.getValue("NHI_ORD_CLASS_CODE")+"','"+
				 parm.getValue("OPT_USER")+"', sysdate,'"+
				 parm.getValue("OPT_TERM")+"','"+
				 parm.getValue("REPORT_CODE")+"','"+
				 parm.getValue("PZWH")+"')";
		//System.out.println("insertIBSDownloadSQL====" + insertIBSDownloadSQL);
		result = new TParm(this.update(insertIBSDownloadSQL, connection));
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}

	/**
	 * ��ѯ
	 * 
	 * @param queryTParm
	 *            TParm ��ѯ����
	 * @return TParm
	 */
	public TParm selectdata(TParm queryTParm) {
		String sql = "SELECT CONFIRM_NO,PAT_NAME,SEX_CODE,MR_NO   FROM INS_ADM_CONFIRM WHERE CONFIRM_NO in("
				+ queryTParm.getValue("CONFIRM_NO") + ")";
		// TParm result = query("selectdata", queryTParm);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

}
