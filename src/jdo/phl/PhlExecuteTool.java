package jdo.phl;

import java.sql.Timestamp;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

import jdo.inw.InwForOdiTool;
import jdo.onw.ONWTool;
import jdo.sys.Operator;

/**
 * <p>
 * Title: ������ִ��
 * </p>
 *
 * <p>
 * Description: ������ִ��
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
public class PhlExecuteTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static PhlExecuteTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return IndAgentTool
     */
    public static PhlExecuteTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhlExecuteTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public PhlExecuteTool() {
        setModuleName("phl\\PHLExecuteModule.x");
        onInit();
    }

    /**
     * ��ѯ
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryExecute", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ��ȫ����
     *
     * @param parm
     * @return
     */
    public TParm onQueryForOnw(TParm parm) {
        TParm result = this.query("queryExecuteForOnw", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ��δִ�У�
     *
     * @param parm
     * @return
     */
    public TParm onQueryForOnwNo(TParm parm) {
        TParm result = this.query("queryExecuteForOnwNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ����ִ�У�
     *
     * @param parm
     * @return
     */
    public TParm onQueryForOnwYes(TParm parm) {
        TParm result = this.query("queryExecuteForOnwYes", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ������ִ�б���
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onPhlExecute(TParm parm, TConnection conn) {
        // ���ݼ��
        if (parm == null)
            return null;
        TParm result = new TParm();
        // ����ִ��ҽ��
        TParm orderParm = parm.getParm("ORDER_PARM");
        for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
            TParm inparm = orderParm.getRow(i);
            result = PhlOrderTool.getInstance().onUpdate(inparm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        // ���²���״̬
        TParm bedParm = parm.getParm("BED_PARM");
        result = PhlBedTool.getInstance().onUpdatePatStatus(bedParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // ���»�ʿ��ע��Ƥ�Խ��
//        System.out.println("��� ��� orderParm orderParm is ::"+orderParm);
        for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
            TParm inparm = new TParm();
//            if (!"".equals(orderParm.getValue("NS_NOTE", i))||!"".equals(orderParm.getValue("BATCH_NO", i))) {
                inparm.setData("CASE_NO", orderParm.getData("CASE_NO", i));
                inparm.setData("RX_NO", orderParm.getValue("ORDER_NO", i));
                inparm.setData("SEQ_NO", orderParm.getValue("SEQ_NO", i));
                inparm.setData("NS_NOTE", orderParm.getValue("NS_NOTE", i));
                inparm.setData("BATCH_NO", orderParm.getValue("BATCH_NO", i));
                inparm.setData("SKINTEST_FLG", orderParm.getValue("SKINTEST_FLG", i));
                inparm.setData("EXEC_DATE",orderParm.getValue("EXEC_DATE", i));//add by huangjw 20141031
                inparm.setData("EXEC_DR_DESC",orderParm.getValue("OPT_USER", i));//add by yanjing 20150226
                result = ONWTool.getInstance().updateNS_NOTE(inparm, conn);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText()
                        + result.getErrName());
                    return result;
                }
//            }
        }
        return result;
    }
    
    /**
     * ������ִ�б���
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onPhlExecuteForOnw(TParm parm, TConnection conn) {
        // ���ݼ��
        if (parm == null)
            return null;
        TParm result = new TParm();
        // ����ִ��ҽ��
        TParm orderParm = parm.getParm("ORDER_PARM");
        for (int i = 0; i < orderParm.getCount(); i++) {
            TParm inparm = new TParm();
//            if (!"".equals(orderParm.getValue("NS_NOTE", i))||!"".equals(orderParm.getValue("BATCH_NO", i))) {
                inparm.setData("CASE_NO", orderParm.getData("CASE_NO", i));
                inparm.setData("RX_NO", orderParm.getValue("ORDER_NO", i));
                inparm.setData("SEQ_NO", orderParm.getValue("SEQ_NO", i));
                inparm.setData("NS_NOTE", orderParm.getValue("NS_NOTE", i));
                inparm.setData("BATCH_NO", orderParm.getValue("BATCH_NO", i));
                inparm.setData("SKINTEST_FLG", orderParm.getValue("SKINTEST_FLG", i));
                inparm.setData("EXEC_DATE",orderParm.getValue("EXEC_DATE",i));//add by huangjw 20141105
                inparm.setData("EXEC_DR_DESC",orderParm.getValue("OPT_USER",i));//add by yanjing 20150226
                result = ONWTool.getInstance().updateNS_NOTE(inparm, conn);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText()
                        + result.getErrName());
                    return result;
                }
//            }
        }
        return result;
    }
    /**
     * Ƥ�� add caoyong 
     * @param orderCode
     * @return
     */
/* public TParm getPsresult(String orderCode){
	 
    	String sql= "SELECT A.SKINTEST_FLG,B.PS_FLG "+
					"FROM PHA_BASE A, SYS_PHAROUTE B "+
					"WHERE A.ORDER_CODE='"+orderCode+"' "+
					"AND A.SKINTEST_FLG='Y' "+
					"AND A.ANTIBIOTIC_CODE IS NOT NULL "+
					"AND A.ROUTE_CODE=B.ROUTE_CODE ";
    	
//    	System.out.println("sql"+sql);
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	
    	 if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
    	 return result;
    }*/
 
 

}
