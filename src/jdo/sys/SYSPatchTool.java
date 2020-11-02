package jdo.sys;

import java.sql.Timestamp;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 *
 * <p>
 * Title: ���γ���
 * </p>
 *
 * <p>
 * Description:���γ���
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author zhangy 2009/07/22
 * @version 1.0
 */

public class SYSPatchTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static SYSPatchTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return
     */
    public static SYSPatchTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSPatchTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SYSPatchTool() {
        setModuleName("sys\\SYSPatchModule.x");
        onInit();
    }

    /**
     * ����
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm) {
        TParm result = this.update("insert", parm);
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
     * @param conn
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
     * ����
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm) {
        TParm result = this.update("update", parm);
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
     * ɾ��
     *
     * @param parm
     * @return
     */
    public TParm onDelete(TParm parm) {
        TParm result = this.update("delete", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * SYS_FEEʹ�ýӿڣ�����һ����ִ�е�����
     *
     * @param parm
     *
     * @param conn
     * @return
     */
    public TParm onCreateNewPatch(TParm parm, TConnection conn) {
        TParm result = new TParm();
        // ȡ��ԭ��
        String patch_code = SystemTool.getInstance().getNo("ALL", "PUB",
            "PATCH_CODE", "PATCH_CODE");
        parm.setData("PATCH_CODE", patch_code);
        parm.setData("PATCH_DESC", "���ۼƻ�" + patch_code);
        parm.setData("PATCH_TYPE", 1);
        parm.setData("PATCH_REOMIT_COUNT", 0);
        parm.setData("PATCH_REOMIT_INTERVAL", "");
        parm.setData("PATCH_REOMIT_POINT", "N");
        parm.setData("STATUS", "Y");
        TNull tnull = new TNull(Timestamp.class);
        parm.setData("END_DATE", tnull);
        // �������γ���
        result = this.onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // �������β���-1: ҩƷ����
        parm.setData("PATCH_PARM_NAME", "ORDER_CODE");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_ORDER_CODE"));
        result = SYSPatchParmTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // �������β���-2: �����ۼ۸�
        parm.setData("PATCH_PARM_NAME", "OWN_PRICE");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_OWN_PRICE"));
        result = SYSPatchParmTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // �������β���-3: �¹���۸�
        parm.setData("PATCH_PARM_NAME", "OWN_PRICE2");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_OWN_PRICE2"));
        result = SYSPatchParmTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // �������β���-4: �¹���ҽ�Ƽ۸�
        parm.setData("PATCH_PARM_NAME", "OWN_PRICE3");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_OWN_PRICE3"));
        result = SYSPatchParmTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // �������β���-5: ���ۼƻ�����
        parm.setData("PATCH_PARM_NAME", "RPP_CODE");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_RPP_CODE"));
        result = SYSPatchParmTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        return result;
    }

    /**
     * SYS_FEEʹ�ýӿڣ�����һ����ִ�е�����
     *
     * @param parm
     *
     * @param conn
     * @return
     */
    public TParm onUpdateNewPatch(TParm parm, TConnection conn) {
        TParm result = new TParm();
        parm.setData("PATCH_PARM_NAME", "RPP_CODE");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_RPP_CODE"));
        result = SYSPatchParmTool.getInstance().onQueryPatchCode(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        parm.setData("PATCH_CODE", result.getValue("PATCH_CODE", 0));
        parm.setData("PATCH_DESC", "���ۼƻ�" + parm.getValue("PARM_RPP_CODE"));
        parm.setData("PATCH_TYPE", 1);
        parm.setData("PATCH_REOMIT_COUNT", 0);
        parm.setData("PATCH_REOMIT_INTERVAL", "");
        parm.setData("PATCH_REOMIT_POINT", "N");
        parm.setData("STATUS", "Y");
        TNull tnull = new TNull(Timestamp.class);
        parm.setData("END_DATE", tnull);
        // �������γ���
        result = this.onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // �������β���-1: ҩƷ����
        parm.setData("PATCH_PARM_NAME", "ORDER_CODE");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_ORDER_CODE"));
        result = SYSPatchParmTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // �������β���-2: �����ۼ۸�
        parm.setData("PATCH_PARM_NAME", "OWN_PRICE");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_OWN_PRICE"));
        result = SYSPatchParmTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // �������β���-3: �¹���۸�
        parm.setData("PATCH_PARM_NAME", "OWN_PRICE2");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_OWN_PRICE2"));
        result = SYSPatchParmTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // �������β���-4: �¹���ҽ�Ƽ۸�
        parm.setData("PATCH_PARM_NAME", "OWN_PRICE3");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_OWN_PRICE3"));
        result = SYSPatchParmTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        return result;
    }

}
