package jdo.ctr;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import jdo.ctr.CTRMainTool;
import jdo.sys.SYSFeeTool;

/**
 * <p>Title: ҽ��ܿ�</p>
 *
 * <p>Description:ҽ��ܿ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author shibl
 * @version 1.0
 */


public class CTRTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static CTRTool instanceObject;
    /**
     * �õ�ʵ��
     *
     * @return CTRTool
     */
    public static CTRTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new CTRTool();
        }
        return instanceObject;
    }

    /**
     * ɾ������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onMDelete(TParm parm, TConnection conn) {
        TParm mParm = new TParm();
        mParm.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
        mParm.setData("CONTROL_ID", parm.getValue("CONTROL_ID"));
        //ɾ����������
        TParm result = CTRMainTool.getNewInstance().onMDelete(mParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        TParm deleteparm = new TParm();
        deleteparm.setData("ORDER_CODE",
                           parm.getValue("ORDER_CODE"));
        deleteparm.setData("CONTROL_ID",
                           parm.getValue("CONTROL_ID"));
        //ɾ����������ص�ϸ������
        result = CTRMainTool.getNewInstance().onDDelete(deleteparm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        TParm inparm = new TParm();
//        System.out.println("ɾ�����" + parm.getValue("ORDER_CODE"));
        inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
        inparm.setData("CRT_FLG", "N");
        //����Sys_fee�Ĺܿر�ʶ
        result = SYSFeeTool.getInstance().onUpdateCtrflg(inparm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        return result;
    }

    /**
     * ɾ��ϸ��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDDelete(TParm parm, TConnection conn) {
        TParm deleteparm = new TParm();
        deleteparm.setData("ORDER_CODE",
                           parm.getValue("ORDER_CODE_1"));
        deleteparm.setData("CONTROL_ID",
                           parm.getValue("CONTROL_ID_1"));
        deleteparm.setData("SERIAL_NO",
                           parm.getValue("SERIAL_NO"));
        TParm result = CTRMainTool.getNewInstance().onDDelete(deleteparm, conn);
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
     * @param conn TConnection
     * @return TParm
     */
    public TParm onMInsert(TParm parm, TConnection conn) {
        //������������
//         System.out.println("��Ρ�������"+parm.getValue("ORDER_CODE"));
        TParm result = CTRMainTool.getNewInstance().onMInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        TParm inparm = new TParm();
        inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
        inparm.setData("CRT_FLG", "Y");
//          System.out.println("���Ρ�������"+inparm);
        //����sys_fee�Ĺܿر�ʶ
        result = SYSFeeTool.getInstance().onUpdateCtrflg(inparm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
//            System.out.println("��ʶ�ı�");
        return result;
    }
}
