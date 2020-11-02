package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: �̵����
 * </p>
 *
 * <p>
 * Description: �̵����
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
 * @author zhangy 2009.06.10
 * @version 1.0
 */
public class IndQtyCheckTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IndQtyCheckTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return IndAgentTool
     */
    public static IndQtyCheckTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndQtyCheckTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public IndQtyCheckTool() {
        setModuleName("spc\\INDQtyCheckModule.x");		
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
     * ��ѯ����ʱ��
     *
     * @param parm
     * @return
     */
    public TParm onQueryFrozenDate(TParm parm) {
        TParm result = this.query("queryFrozenDate", parm);
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
     * ����ѡ��Ķ���ʱ���ѯ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryQtyCheck(TParm parm) {
        String sql = "";
        //System.out.println("----CHECKREASON_CODE----"+parm.getValue("CHECKREASON_CODE"));
        if ("0".equals(parm.getValue("CHECKREASON_CODE"))) {
            // ȫ���̵�
            sql = INDSQL.getQtyCheckDataByType0(parm.getValue("ORG_CODE"),
                                                parm.getValue("FROZEN_DATE"),
                                                parm.getValue("ACTIVE_FLG"),
                                                parm.getValue("VALID_FLG"),
                                                parm.getValue("SORT"));
        }
        else {
            // �����̵�
            if ("A".equals(parm.getValue("CHECK_TYPE"))) {
                sql = INDSQL.getQtyCheckDataByType0(parm.getValue("ORG_CODE"),
                    parm.getValue("FROZEN_DATE"),
                    parm.getValue("ACTIVE_FLG"),
                    parm.getValue("VALID_FLG"),
                    parm.getValue("SORT"));

            }
            else if ("B".equals(parm.getValue("CHECK_TYPE"))) {
                sql = INDSQL.getQtyCheckDataByTypeB(parm.getValue("ORG_CODE"),
                    parm.getValue("FROZEN_DATE"),
                    parm.getValue("ACTIVE_FLG"),
                    parm.getValue("VALID_FLG"),
                    parm.getValue("SORT"),
                    parm.getValue("ORDER_CODE"));

            }
            else if ("C".equals(parm.getValue("CHECK_TYPE"))) {
                sql = INDSQL.getQtyCheckDataByTypeC(parm.getValue("ORG_CODE"),
                    parm.getValue("FROZEN_DATE"),
                    parm.getValue("ACTIVE_FLG"),
                    parm.getValue("VALID_FLG"),
                    parm.getValue("SORT"),
                    parm.getValue("ORDER_CODE"),
                    parm.getValue("VALID_DATE"));
            }
            else if ("D".equals(parm.getValue("CHECK_TYPE"))) {
                sql = INDSQL.getQtyCheckDataByTypeD(parm.getValue("ORG_CODE"),
                    parm.getValue("FROZEN_DATE"),
                    parm.getValue("ACTIVE_FLG"),
                    parm.getValue("VALID_FLG"),
                    parm.getValue("SORT"),
                    parm.getValue("MATERIAL_LOC_CODE"));
            }
            else {
                sql = INDSQL.getQtyCheckDataByTypeOther(parm.getValue(
                    "ORG_CODE"), parm.getValue("FROZEN_DATE"));
            }
        }
        //System.out.println("SQL:" + sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * ����ʵ�������͵�����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("update", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����̵�����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyCheck(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyCheck", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateUnLock(TParm parm, TConnection conn) {
        TParm result = this.update("updateUnLock", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ӯ��ͳ��(����)
     *
     * @param parm
     * @return
     */
    public TParm onQueryQtyCheckMaster(TParm parm) {
        TParm result = this.query("getQueryQtyCheckMaster", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ӯ��ͳ��(��ϸ)
     *
     * @param parm
     * @return
     */
    public TParm onQueryQtyCheckDetail(TParm parm) {
        TParm result = this.query("getQueryQtyCheckDetail", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
