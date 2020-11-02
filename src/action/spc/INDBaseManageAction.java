package action.spc;

import java.util.ArrayList;
import java.util.List;
  
import jdo.spc.INDSQL;
import jdo.spc.INDTool;
import jdo.spc.IndBaseManageDTool;
import jdo.spc.IndBaseManageMTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

import jdo.spc.IndRequestDTool;

/**
 * <p>
 * Title:���뵥
 * </p>
 *
 * <p>
 * Description: ���뵥
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
 * @author zhangy 2009.05.24
 * @version 1.0
 */
public class INDBaseManageAction
    extends TAction {

    /** 
     * ��ѯ��������
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryM(TParm parm) {
        TParm result = new TParm();
        result = IndBaseManageMTool.getInstance().onQuery(parm);
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
     *            TParm
     * @return TParm
     */
    public TParm onInsertM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndBaseManageMTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ����
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndBaseManageMTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ɾ�����뵥(���ϸ��)
     *
     * @param parm
     * @return
     */
    public TParm onDeleteM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onDeleteRequestM(parm, conn);
        if (result == null || result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ������ϸ
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateD(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm(); 
        result = IndBaseManageDTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ���ұ�ҩ���ɲ�ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDeptExm(TParm parm) {
        TParm result = new TParm();
        result = INDTool.getInstance().onQueryDeptExm(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �������쵥
     * @param parm TParm
     * @return TParm
     */
    public TParm onCreateDeptExmRequest(TParm parm) {
        TParm result = new TParm();
        TConnection conn = getConnection();
        result = INDTool.getInstance().onCreateDeptExmRequest(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();  
        conn.close();
        return result;
    }
    
  

    /**
     * �������뵥״̬
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateFlg(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndRequestDTool.getInstance().onUpdateFlg(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
      /*  String reurnString =this.onStopDispenseOut(parm);
        if("fail".equals(reurnString)) {
 			result.setErrCode(-1);
 			return result;
 		}  */ 
        conn.commit();
        conn.close();
        return result;
    }
   
    
   
    
}
