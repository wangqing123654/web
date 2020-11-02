package jdo.bms;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: ѪҺ��Ϣ
 * </p>
 *
 * <p>
 * Description: ѪҺ��Ϣ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class BMSBloodTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static BMSBloodTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return BMSBloodTool
     */
    public static BMSBloodTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BMSBloodTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BMSBloodTool() {
        setModuleName("bms\\BMSBloodModule.x");
        onInit();
    }

    /**
     * ��ѯ
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("query", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ������
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updateIn", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ��
     *
     * @param parm
     * @return
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("delete", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ������ѧ��ѯ
     *
     * @param parm
     * @return
     */
    public TParm onQueryBloodCross(TParm parm) {
//        TParm result = this.query("queryBloodCross", parm);
    	TParm result = this.query("queryBloodCrossExceptOut", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����ѯ
     *
     * @param parm
     * @return
     */
    public TParm onQueryBloodStock(TParm parm) {
        TParm result = this.query("queryBloodStock", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ݾ������,�����ź�סԺ�Ż�ò�����Ѫ��Ϣ(��ϸ��,ѪС��,Ѫ��,ȫѪ)
     *
     * @param parm
     * @return
     */
    public TParm getApplyInfo(TParm parm) {
        TParm result = this.query("getApplyInfo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���±�Ѫ����Ϣ(������Ѫ)
     *
     * @param parm
     * @return
     */
    public TParm onUpdateBloodCross(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyUpdateBloodCross", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���±�Ѫ����Ϣ(ѪƷ����)
     *
     * @param parm
     * @return
     */
    public TParm onUpdateBloodOut(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyUpdateBloodOut", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ (��Ѫ�� + ������)
     *
     * @param parm
     * @return
     */
    public TParm getSumTot(TParm parm) {
        TParm result = this.query("getSumTot", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���״̬��ѯ
     *
     * @param parm
     * @return
     */
    public TParm onQueryBloodStockStatus(TParm parm) {
        TParm result = this.query("queryBloodStockStatus", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ѪƷ����ѯ
     *
     * @param parm
     * @return
     */
    public TParm onQueryBloodQtyStock(TParm parm) {
        TParm result = this.query("queryBloodQtyStock", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * Ѫ����Ѫ�±���
     *
     * @param parm
     * @return
     */
    public TParm onQueryMonthStock(TParm parm) {
        TParm result = this.query("queryMonthStock", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ������ѧ��ѯ���ѳ���Ĳ�����ʾ�ڽ�����Ѫ���桾TABLE_M���У�
     *
     * @param parm
     * @return
     */
    public TParm onQueryBloodCrossExceptOut(TParm parm) {
        TParm result = this.query("queryBloodCrossExceptOut", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }    

}
