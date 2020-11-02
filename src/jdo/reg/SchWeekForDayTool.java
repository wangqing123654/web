package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 *
 * <p>Title:�ܰ�ת�հ๤���� </p>
 *
 * <p>Description:�ܰ�ת�հ๤���� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.18
 * @version 1.0
 */
public class SchWeekForDayTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static SchWeekForDayTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SchWeekForDayTool
     */
    public static SchWeekForDayTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SchWeekForDayTool();
        return instanceObject;
    }
    /**
     * ������
     */
    public SchWeekForDayTool()
    {
        setModuleName("reg\\REGSchWeekForDayModule.x");
        onInit();
    }
    /**
     * �����ܰ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertdata(TParm parm,TConnection connection) {
        TParm result = new TParm();
        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }}
