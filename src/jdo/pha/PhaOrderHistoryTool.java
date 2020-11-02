package jdo.pha;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 *
 * <p>Title: ����ҩ����ҩ��¼������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS (c) 2008</p>
 *
 * @author ZangJH 2008.09.26
 *
 * @version 1.0
 */


public class PhaOrderHistoryTool
    extends TJDOTool {

    /**
     * ʵ��
     */
    public static PhaOrderHistoryTool instanceObject;

    /**
     * �õ�ʵ��
     * @return OrderTool
     */
    public static PhaOrderHistoryTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhaOrderHistoryTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public PhaOrderHistoryTool() {
        setModuleName("pha\\PhaOrderHistoryModule.x");

        onInit();
    }

    /**
     * ����ҽ��
     * ����һ��
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm,TConnection connection) {

        TParm result = new TParm();
        //ִ��module�ϵ�insert update delete��update
        result = update("insertdata", parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * �������
     * @param parm
     * @return result
     */
    public TParm onInsert(TParm parm,TConnection connection) {

        int count = parm.getCount();
        TParm result = new TParm();
        for (int i = 0; i < count; i++) {
            TParm inParm = new TParm();
            //��һ������TParm��ȡ��ĳһ�е����ݣ��ŵ�һ������Parm��
            inParm.setRowData( -1, parm, i); //����ȡ�ö���parm�ĵ�i�У��ŵ�����parm-inParm��
            //ִ�в������
            result = this.insertdata(inParm,connection);
            if (result.getErrCode() < 0)
                return result;
        }
        return result;
    }

}
