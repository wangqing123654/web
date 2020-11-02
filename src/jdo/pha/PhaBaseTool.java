package jdo.pha;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 *
 * <p> Title: ҩƷ������tool </p>
 *
 * <p> Description: ҩƷ������tool </p>
 *
 * <p> Copyright: Copyright (c) Liu dongyang 2008 </p>
 *
 * <p> Company: javahis </p>
 *
 * @author ehui 20081005
 * @version 1.0
 */
public class PhaBaseTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static PhaBaseTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return PhaBaseTool
     */
    public static PhaBaseTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhaBaseTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public PhaBaseTool() {
        setModuleName("pha\\PhaBaseModule.x");
        onInit();
    }

    /**
     * ץȡָ��ҩƷ��������
     */
    public TParm queryForAmout(String orderCode) {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        result = query("queryforAmout", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ץȡָ��ҩƷ��������
     */
    public TParm selectByOrder(String orderCode) {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        result = query("selectByOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ץȡָ��ҩƷ��������_�÷�
     * add by huangtt 20141104
     */
    public TParm selectByOrderRoute(String orderCode) {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        result = query("selectByOrderRoute", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ؿ�����Ĭ��ʹ������
     * @param orderCode
     * @return
     */
	public TParm getAntiDrugDays(TParm orderCode) { // modify by wanglong 20121206
		TParm result = new TParm(TJDODBTool.getInstance().select(
				"SELECT A.TAKE_DAYS FROM PHA_BASE A,SYS_CTRLDRUGCLASS B"
						+ " WHERE A.ORDER_CODE='" + orderCode.getValue("ORDER_CODE")
						+ "' AND B.CTRLDRUGCLASS_CODE=A.CTRLDRUGCLASS_CODE"
						+ " AND B.ANTIBIOTIC_FLG='Y'"));
		return result;
	}

    /**
     * �����Ƿ�Ϊ������
     * @param orderCode
     * @return
     */
	public TParm isAntiDrug(TParm orderCode) { // modify by wanglong 20121206
		TParm result = new TParm(
				TJDODBTool.getInstance().select(
								"SELECT A.ORDER_CODE FROM PHA_BASE A,SYS_CTRLDRUGCLASS B"
										+ " WHERE A.ORDER_CODE='" + orderCode.getValue("ORDER_CODE")
										+ "' AND B.CTRLDRUGCLASS_CODE=A.CTRLDRUGCLASS_CODE AND B.ANTIBIOTIC_FLG='Y'"));
		return result;
	}

    /**
     * �����Ƿ�Ϊ����ҩ
     * @param orderCode
     * @return
     */
	public TParm isCtrlDrug(TParm orderCode) { // modify by wanglong 20121206
		TParm result = new TParm(
				TJDODBTool.getInstance().select(
								"SELECT A.ORDER_CODE FROM PHA_BASE A,SYS_CTRLDRUGCLASS B"
										+ " WHERE A.ORDER_CODE='" + orderCode.getValue("ORDER_CODE")
										+ "' AND B.CTRLDRUGCLASS_CODE=A.CTRLDRUGCLASS_CODE AND B.CTRL_FLG='Y'"));
		return result;
	}

    /**
     * �����ƶ���Ȩƽ����
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateStockPrice(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockPrice", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����������
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateTradePrice(TParm parm, TConnection conn) {
        TParm result = this.update("updateTradePrice", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �������ۼ�
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateRetailPrice(TParm parm, TConnection conn) {
        TParm result = this.update("updateRetailPrice", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
