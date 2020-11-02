package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ���뵥����Tool
 * </p>
 *
 * <p>
 * Description: ���뵥����Tool
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
 * @author zhangy 2009.05.24
 * @version 1.0
 */
public class IndRequestMTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IndRequestMTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return IndStockMTool
     */
    public static IndRequestMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndRequestMTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public IndRequestMTool() {
        setModuleName("ind\\INDRequestMModule.x");
        onInit();
    }

    /**
     * ��ѯ��������
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryRequestM", parm);
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
        TParm result = this.update("createNewRequestM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    //==============================��װ��ҩ��
    /**
     * ����
     *
     * @param parm
     * @return
     */
    public TParm onBoxInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createBoxRequestM", parm, conn);
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
        TParm result = this.update("updateRequestM", parm, conn);
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
        TParm result = this.update("deleteRequestM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ���������ҵ�ĵ���
     *
     * @param parm
     * @return
     */
    public TParm onQueryOutReqNo(TParm parm) {
        TParm result = this.query("queryOutReqNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ������ⷽʽ�ĵ���
     *
     * @param parm
     * @return
     */
    public TParm onQueryOtherInNo(TParm parm) {
        TParm result = this.query("queryOtherInNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ұ�ҩ���ɻ��ܲ�ѯ(סԺ)
     *
     * @param parm
     * @return
     */
    public TParm onQueryIBSDeptExmM(TParm parm) {
        TParm result = this.query("queryIBSDeptExmM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ұ�ҩ������ϸ��ѯ(סԺ)
     *
     * @param parm
     * @return
     */
    public TParm onQueryIBSDeptExmD(TParm parm) {
        TParm result = this.query("queryIBSDeptExmD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���ұ�ҩ������ϸ��ѯ(סԺ)����� 
     *
     * 2012-04-27 
     * @param parm
     * @return
     */
    public TParm onQueryIBSDeptExmDFinish(TParm parm) {
    	TParm result = this.query("queryIBSDeptExmDFinish", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText()
    				+ result.getErrName());
    		return result;
    	}
    	return result;
    }

    /**
     * ���ұ�ҩ���ɻ��ܲ�ѯ(�ż���)
     *
     * @param parm
     * @return
     */
    public TParm onQueryOPDDeptExmM(TParm parm) {
        TParm result = this.query("queryOPDDeptExmM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ұ�ҩ������ϸ��ѯ(�ż���)
     *
     * @param parm
     * @return
     */
    public TParm onQueryOPDDeptExmD(TParm parm) {
        TParm result = this.query("queryOPDDeptExmD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ұ�ҩ���ɻ��ܲ�ѯ(��ɵ�)
     *
     * @param parm
     * @return
     */
    public TParm onQueryExm(TParm parm) {
        TParm result = this.query("onQueryExm", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ұ�ҩ���ɻ��ܲ�ѯ(����-סԺ)-δ����
     *
     * @param parm
     * @return
     */
    public TParm queryOdiDsnpmExmM(TParm parm) {
        TParm result = this.query("queryOdiDsnpmExmM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ұ�ҩ������ϸ��ѯ(����-סԺ)-δ����
     *
     * @param parm
     * @return
     */
    public TParm queryOdiDspnmExmD(TParm parm) {
        TParm result = this.query("queryOdiDspnmExmD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���ұ�ҩ������ϸ��ѯ(����-סԺ)-�Ѿ�����
     *
     * @param parm
     * @return
     */
    public TParm queryOdiDspnmExmEd(TParm parm) {
        TParm result = this.query("queryOdiDspnmExmEd", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }    
}
