package jdo.phl;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: �����Ҵ�λ
 * </p>
 *
 * <p>
 * Description: �����Ҵ�λ
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
public class PhlBedTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static PhlBedTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return IndAgentTool
     */
    public static PhlBedTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhlBedTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public PhlBedTool() {
        setModuleName("phl\\PHLBedModule.x");
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
     * ���´�λ״̬
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateBed(TParm parm, TConnection conn) {
        TParm result = this.update("updateBed", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���²���״̬
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdatePatStatus(TParm parm, TConnection conn) {
        TParm result = this.update("updatePatStatus", parm, conn);
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
     * add ������λ
     * 2014/4/17
     * @param parm
     * @param conn
     * @return
     */
    public TParm PHLBed(TParm parm, TConnection conn) {
        // ���ݼ��
        if (parm == null)
            return null;
        TParm result = new TParm();
        // ����ִ��ҽ��
        TParm orderParm = parm.getParm("BED_NUM");
        for (int i = 0; i < orderParm.getCount("BED_NO"); i++) {
            TParm inparm = orderParm.getRow(i);
            result = onInsertdate(inparm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        TParm uporderParm = parm.getParm("UPBED_NUM");
        for (int i = 0; i < uporderParm.getCount("BED_NO"); i++) {
        	TParm upinparm = uporderParm.getRow(i);
        	result = onUpdatebed(upinparm, conn);
        	if (result.getErrCode() < 0) {
        		err("ERR:" + result.getErrCode() + result.getErrText()
        				+ result.getErrName());
        		return result;
        	}
        }
        return result;
    }
    /**
     * add ������λ
     * 2014/4/17
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsertdate(TParm parm, TConnection conn) {
    	
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
    public TParm onUpdatebed (TParm parm, TConnection conn) {
        TParm result = this.update("update", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
