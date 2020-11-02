package jdo.udd;

import jdo.clp.intoPathStatisticsTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * <p>
 * Title: סԺҩ��tool
 * 
 * <p>
 * Description: ҽ��tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * 
 * @author ehui 20080921
 * @version 1.0
 */
public class UddRtnRgsTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static UddRtnRgsTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return OPDSubjrecTool
	 */
	public static UddRtnRgsTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new UddRtnRgsTool();
		}
		return instanceObject;
	}

	/**
	 * ������
	 */
	public UddRtnRgsTool() {
		setModuleName("udd\\UddRtnRgstModule.x");
		onInit();
	}

	public TParm onInsertM(TParm parm, TConnection connection) {

		TParm result = new TParm();
		result = update("onInsertM", parm, connection);
		// System.out.println("insert");
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 
	 * ������ҩʱ�ķ�ҩ������ҩ��¼ luhai 2012-2-3
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
    public TParm onUpdateDispenseReturnQty(TParm parm, TConnection connection) {//modify by wanglong 20130628
        TParm result = new TParm();
        String colName = " RT_REQTY";
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append(" UPDATE ODI_DSPNM SET ");
        if (parm.getValue("OPT_TYPE").equals("ADD")) {
            sqlbf.append(colName + "=" + colName + "+" + parm.getValue("RTN_DOSAGE_QTY") + " ");
        } else if (parm.getValue("OPT_TYPE").equals("MINUS")) {
            sqlbf.append(colName + "=" + colName + "-" + parm.getValue("RTN_DOSAGE_QTY") + " ");
        }
        sqlbf.append(" WHERE CASE_NO='" + parm.getValue("CASE_NO") + "' ");
        sqlbf.append(" AND ORDER_NO='" + parm.getValue("PARENT_ORDER_NO")
                + "' ");
        sqlbf.append(" AND ORDER_SEQ=" + parm.getValue("PARENT_ORDER_SEQ")
                + " ");
        sqlbf.append(" AND START_DTTM='" + parm.getValue("PARENT_START_DTTM")
                + "' ");
        TJDODBTool.getInstance().update(sqlbf.toString(), connection);
        // result = update("onInsertM", parm,connection);
        // System.out.println("insert");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
            return result;
        }
        return result;
    }

	/**
	 * 
	 * ������ҩʱ�ķ�ҩ������ҩ��¼
	 * 
	 * @date 2012-08-06
	 * @author liyh
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm onUpdateIBSQty(TParm parm, TConnection connection) {
		TParm result = new TParm();
		int dosageQty = parm.getInt("DISPENSE_QTY");
		String sql = " UPDATE IBS_ORDD SET DOSAGE_QTY=DOSAGE_QTY-" + dosageQty
				+ " 	WHERE CASE_NO='" + parm.getValue("CASE_NO") + "' "
				+ "    AND CASE_NO_SEQ=" + parm.getInt("IBS_CASE_NO_SEQ")
				+ "    AND SEQ_NO=" + parm.getInt("IBS_SEQ_NO");
		// System.out.println("---------------update ibs sql :"+sql);
		TJDODBTool.getInstance().update(sql, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm onInsertD(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("onInsertD", parm, connection);
		// System.out.println("insert");
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm onUpdateM(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("onUpdateM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm onUpdateD(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("onUpdateD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	public TParm onDeleteM(TParm parm, TConnection connection) {

		TParm result = new TParm();
		result = update("onDeleteM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	public TParm onDeleteD(TParm parm, TConnection connection) {

		TParm result = new TParm();
		result = update("onDeleteD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm onUpdateRtnCfmM(TParm parm, TConnection connection) {

		TParm result = new TParm();
		result = update("onUpdateRtnCfmM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	public TParm onUpdateRtnCfmD(TParm parm, TConnection connection) {

		TParm result = new TParm();
		result = update("onUpdateRtnCfmD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * 
	 * �õ�סԺҩ����ҩ��ʵ����ҩbatchSeq����
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm getUDDRTNData(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = query("getUDDRtnData", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * ȡ����ҩ
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm onCancleDisM(TParm parm, TConnection connection) {

		TParm result = new TParm();
		result = update("onCancleDisM", parm, connection);
		// System.out.println("insert");
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ȡ����ҩ
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm onCancleDisD(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("onCancleDisD", parm, connection);
		// System.out.println("insert");
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
