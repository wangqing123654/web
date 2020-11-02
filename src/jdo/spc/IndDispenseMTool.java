package jdo.spc;

import java.sql.Timestamp;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ������������Tool
 * </p>
 * 
 * <p>
 * Description: ������������Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangy 2009.05.25
 * @version 1.0
 */
public class IndDispenseMTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static IndDispenseMTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return IndStockMTool
	 */
	public static IndDispenseMTool getInstance() {
		if (instanceObject == null)
			instanceObject = new IndDispenseMTool();
		return instanceObject;
	}

	/**
	 * ������  
	 */
	public IndDispenseMTool() {
		setModuleName("spc\\INDDispenseMModule.x");
		onInit();
	}

	/**
	 * ��������
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertM(TParm parm, TConnection conn) {
		TParm result = this.update("createNewDispenseM", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ������������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQueryOutM(TParm parm) {
		String status = parm.getValue("STATUS");
		TParm result = new TParm();
		if ("A".equals(status)) {
			// ���
			result = this.query("queryDispenseOutB", parm);
			// ���˲�ѯ����
			for (int i = result.getCount("DISPENSE_NO") - 1; i >= 0; i--) {
				if ("THI".equals(result.getValue("REQTYPE_CODE", i))) {
					result.removeRow(i);
				}
			}
		} else if ("B".equals(status)) {
			// δ����
			result = IndRequestMTool.getInstance().onQueryOutReqNo(parm);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			// zhangyong20110517 ע�͵� begin
			// String request_no = "";
			// for (int i = result.getCount("REQUEST_NO") - 1; i >= 0; i--) {
			// request_no = result.getValue("REQUEST_NO", i);
			// // ���ⵥ
			// TParm dispense = new TParm(TJDODBTool.getInstance().select(
			// INDSQL.getDispenseDByReqNo(request_no)));
			// // ���뵥
			// TParm request = new TParm(TJDODBTool.getInstance().select(
			// INDSQL.getRequestDByNo(request_no)));
			// if (dispense.getCount() > 0 &&
			// dispense.getCount() == request.getCount()) {
			// result.removeRow(i);
			// }
			// }
			// zhangyong20110517 ע�͵� end
		} else {
			// ;��
			result = this.query("queryDispenseOutA", parm);
			// ���˲�ѯ����
			for (int i = result.getCount("DISPENSE_NO") - 1; i >= 0; i--) {
				if ("THI".equals(result.getValue("REQTYPE_CODE", i))
						|| "COS".equals(result.getValue("REQTYPE_CODE", i))) {
					result.removeRow(i);
				}
			}
		}
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ�����������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQueryInM(TParm parm) {
		TNull tnull = new TNull(Timestamp.class);
		String status = parm.getValue("STATUS");
		TParm result = new TParm();
		if ("A".equals(status)) {
			// ���
			result = this.query("queryDispenseOutB", parm);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			// ���˲�ѯ����
			for (int i = result.getCount("DISPENSE_NO") - 1; i >= 0; i--) {
				if ("THO".equals(result.getValue("REQTYPE_CODE", i))
						|| "EXM".equals(result.getValue("REQTYPE_CODE", i))
						|| "WAS".equals(result.getValue("REQTYPE_CODE", i))
						|| "COS".equals(result.getValue("REQTYPE_CODE", i))) {
					result.removeRow(i);
				}
			}
		} else {
			// δ���
			// ��ѯ������;
			result = this.query("queryDispenseOutA", parm);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			// ���˲�ѯ����
			for (int i = result.getCount("DISPENSE_NO") - 1; i >= 0; i--) {
				if ("COS".equals(result.getValue("REQTYPE_CODE", i))) {
					result.removeRow(i);
				}
			}
			// ������ⷽʽ:��ѯ����δ���
			TParm resultOther = IndRequestMTool.getInstance().onQueryOtherInNo(
					parm);
			if (resultOther.getErrCode() < 0) {
				err("ERR:" + resultOther.getErrCode()
						+ resultOther.getErrText() + resultOther.getErrName());
				return resultOther;
			}
			// int count = result.getCount("REQUEST_NO");
			if (resultOther.getCount("REQUEST_NO") > 0) {
				for (int i = 0; i < resultOther.getCount("REQUEST_NO"); i++) {
					result.addData("REQUEST_NO", resultOther.getData(
							"REQUEST_NO", i));
					result.addData("REQTYPE_CODE", resultOther.getData(
							"REQTYPE_CODE", i));
					result.addData("APP_ORG_CODE", resultOther.getData(
							"APP_ORG_CODE", i));
					result.addData("TO_ORG_CODE", resultOther.getData(
							"TO_ORG_CODE", i));
					result.addData("REQUEST_DATE", resultOther.getData(
							"REQUEST_DATE", i));
					// result.setData("REQUEST_USER", count + i, resultOther
					// .getData("REQUEST_USER", i));
					result.addData("REASON_CHN_DESC", resultOther.getData(
							"REASON_CHN_DESC", i));
					result.addData("DESCRIPTION", resultOther.getData(
							"DESCRIPTION", i));
					result.addData("UNIT_TYPE", resultOther.getData(
							"UNIT_TYPE", i));
					result.addData("URGENT_FLG", resultOther.getData(
							"URGENT_FLG", i));
					result.addData("DISPENSE_USER", "");
					result.addData("DISPENSE_DATE", tnull);
					result.addData("DISPENSE_NO", "");
					result.addData("OPT_TERM", "");
					result.addData("OPT_DATE", tnull);
					result.addData("OPT_USER", "");
					result.addData("WAREHOUSING_USER", "");
					result.addData("UPDATE_FLG", "0");
					result.addData("WAREHOUSING_DATE", tnull);
				}
				result.setCount(resultOther.getCount("REQUEST_NO")
						+ result.getCount());
			}
		}
		return result;
	}

	/**
	 * ��������
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onUpdateM(TParm parm, TConnection conn) {
		TParm result = this.update("updateDispenseM", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��������״̬
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onUpdateMFlg(TParm parm, TConnection conn) {
		TParm result = this.update("updateDispenseFlg", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ҩƷ���������ܱ�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQueryDispense(TParm parm) {
		// fux modify 20150911
		TParm result = new TParm();
		if (parm.getValue("REQUEST_TYPE").equals("WAS")) {
			result = this.query("getQueryDispenseWAS", parm);
		} else {
		    result = this.query("getQueryDispense", parm);  
		}
		//TParm result = this.query("getQueryDispense", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm onQueryMByNo(String dispenseNo) {
		String sql = " SELECT DISPENSE_NO, REQTYPE_CODE, REQUEST_NO, REQUEST_DATE, APP_ORG_CODE, "
				+ " TO_ORG_CODE, URGENT_FLG, DISPENSE_DATE, DISPENSE_USER, WAREHOUSING_DATE,  "
				+ " WAREHOUSING_USER, UNIT_TYPE, UPDATE_FLG, OPT_USER, OPT_DATE, "
				+ " OPT_TERM, REGION_CODE, DRUG_CATEGORY"
				+ " FROM IND_DISPENSEM "
				+ " WHERE DISPENSE_NO='"
				+ dispenseNo
				+ "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() > 0) {
			return result.getRow(0);
		}
		return null;

	}

}
