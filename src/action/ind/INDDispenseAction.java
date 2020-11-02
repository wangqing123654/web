package action.ind;

import jdo.ind.INDTool;
import jdo.ind.IndDispenseDTool;
import jdo.ind.IndDispenseMTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.manager.TIOM_Database;

/**
 * <p>
 * Title: ��������
 * </p>
 *
 * <p>
 * Description: ��������
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
 * @author zhangy 2009.04.25
 * @version 1.0
 */
public class INDDispenseAction
    extends TAction {
    /**
     * ��ѯ��������
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryOutM(TParm parm) {
        TParm result = new TParm();
        result = IndDispenseMTool.getInstance().onQueryOutM(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ�������
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryInM(TParm parm) {
        TParm result = new TParm();
        result = IndDispenseMTool.getInstance().onQueryInM(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��������(��;) / ��������������ҵ�����Ĳġ����ұ�ҩ(���⼴���)
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertOutOn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertDispenseOutOn(parm, conn);
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
     * �����������(��;)
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateMOutOn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndDispenseMTool.getInstance().onUpdateM(parm, conn);
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
     * ���⼴���
     *
     * @param parm
     * @return
     */
    public TParm onInsertOutIn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertDispenseOutIn(parm, conn);
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
     * �������
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertIn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertDispenseIn(parm, conn);
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
     * �����������
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertOtherIn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertDispenseOtherIn(parm, conn);
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
     * ȡ��������ҵ
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateDipenseCancel(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onUpdateDipenseCancel(parm, conn);
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
     * ��ҩ�Զ�ά���շѱ�׼
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateGrpricePrice(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onUpdateGrpricePrice(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        TIOM_Database.logTableAction("SYS_FEE");
        return result;
    }
    
    /**
     * ����������HIS���⣨HIS���������������
     * @param parm
     * @return
     */
    public TParm onInsertOutOnSPC(TParm parm){
    	 TConnection conn = getConnection();
         TParm result = new TParm();
         TParm parmM = parm.getParm("OUT_M");
         
         //ֱ��������������
         result = IndDispenseMTool.getInstance().onInsertM(parmM, conn);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             conn.close();
             return result;
         }
         
         //ֱ�ӱ���ϸ������
         TParm parmD = parm.getParm("OUT_D");
         for(int i = 0 ; i <   parmD.getCount("ORDER_CODE"); i++ ){
        	 
        	 TParm rowParm = (TParm)parmD.getRow(i);
	         result = IndDispenseDTool.getInstance().onInsertD(
	        		 rowParm, conn);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText()
	                 + result.getErrName());
	             conn.close();
	             return result;
	         }  
	         
	       //�������뵥״̬��ʵ�ʳ��������
	         result = INDTool.getInstance().onUpdateRequestFlgAndActual(parmM.getValue("REQUEST_NO"),rowParm,conn);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText()
	                 + result.getErrName());
	             conn.close();
	             return result;
	         }  
         }
         conn.commit();
         conn.close();
         return result;
    }
    
    
    /**
     * ���⼴���(������ֱ��д���ݲ����κ��ж�)
     *
     * @param parm
     * @return
     */
    public TParm onInsertOutInSPC(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        
        //����
        TParm parmM = parm.getParm("OUT_M");
        result = IndDispenseMTool.getInstance().onInsertM(parmM, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        
        TParm parmD = parm.getParm("OUT_D");
        
        for(int i = 0 ; i <   parmD.getCount("ORDER_CODE"); i++ ){
        	
        	TParm rowParm = (TParm)parmD.getRow(i);
	        result = IndDispenseDTool.getInstance().onInsertD(
	                rowParm, conn);
	        
	      //�������뵥״̬��ʵ�ʳ��������
	        result = INDTool.getInstance().onUpdateRequestFlgAndActual(parmM.getValue("REQUEST_NO"),
	                                             parmD, conn);
        }
        
        conn.commit();
        conn.close();
        return result;
    }


    /**
     * �����ۿ�
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateStockQty(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
          //�ۿ⶯��
        result = INDTool.getInstance().onUpdateStockQty(parm, conn);
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
    
}
