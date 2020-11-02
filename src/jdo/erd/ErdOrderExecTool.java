package jdo.erd;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.data.TNull;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: �������ۻ�ʿվTool</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class ErdOrderExecTool
    extends TJDOTool {
    public ErdOrderExecTool() {
    }

    /**
     * ʵ��
     */
    private static ErdOrderExecTool instanceObject;

    /**
     * �õ�ʵ��
     * @return PatTool
     */
    public static ErdOrderExecTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ErdOrderExecTool();
        return instanceObject;
    }


    /**
     * ִ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onExec(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //ǰ̨��������
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            //ȡ������
            TParm execData = new TParm();
            execData.setData("CASE_NO", parm.getData("CASE_NO", i));
            execData.setData("RX_NO", parm.getData("RX_NO", i));
            execData.setData("SEQ_NO", parm.getData("SEQ_NO", i));
            execData.setData("NS_EXEC_CODE", parm.getData("OPT_USER", i));
            execData.setData("NS_EXEC_DATE", parm.getData("NS_EXEC_DATE", i));
            execData.setData("NS_NOTE",
                             parm.getData("NS_NOTE", i) == null ?
                             new TNull(String.class) :
                             parm.getData("NS_NOTE", i));
            execData.setData("OPT_USER", parm.getData("OPT_USER", i));
            execData.setData("OPT_TERM", parm.getData("OPT_TERM", i));
            execData.setData("OPT_DATE", parm.getData("OPT_DATE", i));

            result = ErdForBedAndRecordTool.getInstance().updateExec(
                execData,
                connection);

            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        return result;
    }


    /**
     * ִ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUndoExec(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //ǰ̨��������
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            //ȡ������
            TParm execData = new TParm();
            execData.setData("CASE_NO", parm.getData("CASE_NO", i));
            execData.setData("RX_NO", parm.getData("RX_NO", i));
            execData.setData("SEQ_NO", parm.getData("SEQ_NO", i));
            execData.setData("NS_EXEC_CODE", new TNull(String.class));
            execData.setData("NS_EXEC_DATE", new TNull(Timestamp.class));
            execData.setData("NS_NOTE",
                             parm.getData("NS_NOTE", i) == null ?
                             new TNull(String.class) :
                             parm.getData("NS_NOTE", i));
            execData.setData("OPT_USER", parm.getData("OPT_USER", i));
            execData.setData("OPT_TERM", parm.getData("OPT_TERM", i));
            execData.setData("OPT_DATE", parm.getData("OPT_DATE", i));

            result = ErdForBedAndRecordTool.getInstance().updateExec(
                execData,
                connection);

            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        return result;
    }

}
