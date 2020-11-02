package jdo.pha;

import jdo.opd.OrderList;
import jdo.opd.OrderTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PHAOrderTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static PHAOrderTool instanceObject;

    /**
     * �õ�ʵ��
     * @return OrderTool
     */
    public static PHAOrderTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new PHAOrderTool();
        }
        return instanceObject;
    }

    /**
     * ������
     */
    public PHAOrderTool() {
        setModuleName("opd\\OPDOrderModule.x");
        onInit();
    }

    /**
     * PHA�춯�����
     * @param parm
     * @param connection
     * @return result ������
     */
    public TParm onSave(TParm parm, TConnection connection) {
        TParm result = OrderTool.getInstance().onDelete(parm.getParm(OrderList.
                DELETED), connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        result = OrderTool.getInstance().onInsert(parm.getParm(OrderList.NEW),
                                                  connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        
        //add by huangtt 20170322 У��ҽ���Ƿ��ѱ��޸� start
		
		result = OrderTool.getInstance().checkUpdate(parm.getParm(OrderList.MODIFIED), connection);
		if(result.getErrCode() < 0){
			err("ERR:" + result.getErrCode() + result.getErrText());
			return result;
		}
		//add by huangtt 20170322 У��ҽ���Ƿ��ѱ��޸� end
        
        result = onUpdate(parm.getParm(OrderList.MODIFIED), connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * ����
     * @param parm
     * @return result
     */
    public TParm onUpdate(TParm parm, TConnection connection) {
        int count = parm.getCount();
        TParm result = new TParm();
        for (int i = 0; i < count; i++) {
            TParm inParm = new TParm();
            inParm.setRowData( -1, parm, i);
            inParm.setData("UPDATE_TIME", SystemTool.getInstance().getUpdateTime());
//            System.out.println("updatedata---"+i+"----"+inParm);
            result = this.updatedata(inParm, connection);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }

    /**
     * ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm, TConnection connection) {
        TParm result = new TParm();
//        result = update("updatedataForPHA", parm, connection);
        result = update("updatedataForPHAExactly", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm updateCostAmt(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("updateCostAmtForPHA", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����ʵ�ʵķ�ҩ����
     * @param parm TParm
     * @return TParm
     */
    public TParm updateDispenseDetailForPHA(TParm parm, TConnection connection) {
    	TParm result = new TParm();
    	result = update("updateDispenseDetailForPHA", parm, connection);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText()
    				+ result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * 
     * ��ѯ������ʵ�ʿۿ���Ϣ
     * luhai 2012-1-31
     * @param parm
     * @param connection
     * @return
     */
    public TParm queryDispenseDetailForPHA(TParm parm, TConnection connection) {
    	TParm result = new TParm();
    	result = query("queryDispenseDetailForPHA", parm, connection);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText()
    				+ result.getErrName());
    		return result;
    	}
    	return result;
    }
}
