package action.inw;

import java.sql.Timestamp;
import java.util.Date;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;

import jdo.inf.INFCaseTool;
import jdo.sta.STADeptListTool;
import jdo.sta.STAOpdDailyTool;
import jdo.sta.STAStationDailyTool;
import jdo.sys.Operator;


/**
 * <p>Title: �ż���ҽ��ͳ���м��������������</p>
 *
 * <p>Description: �ż���ҽ��ͳ���м����������������</p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author yangjj 2015.09.28
 * @version 1.0
 */
public class INWSTADailyAction
        extends Patch {

    public INWSTADailyAction() {}

    /**
     * �����߳�
     * 
     * @return boolean
     */
    public boolean run() {
        TConnection conn = TDBPoolManager.getInstance().getConnection();
        TParm sql = new TParm();
        Timestamp date = StringTool.getTimestamp(new Date());
        Timestamp yesterday = StringTool.rollDate(date, -1);
        sql.setData("ADMDATE", StringTool.getString(yesterday, "yyyyMMdd"));
        TParm inParm = this.getParm();
        sql.setData("OPT_USER", inParm.getValue("OPT_USER"));// SL_BATCH
        sql.setData("OPT_TERM", inParm.getValue("OPT_TERM"));// 127.0.0.1
        sql.setData("REGION_CODE", inParm.getValue("REGION_CODE"));
        TParm dept = new TParm();
        dept = STADeptListTool.getInstance().selectOE_DEPT(inParm.getValue("REGION_CODE"));
        if (dept.getErrCode() < 0) {
            System.out.println("�ż�����־���Σ���ѯ�����м����ż��ﲿ��ʧ�� " + dept.getErrCode() + dept.getErrText()
                    + dept.getErrName());
            return false;
        }
        TParm parm = new TParm();
        parm.setData("SQL", sql.getData());
        parm.setData("DEPT", dept.getData());
        TParm result = new TParm();
        result = STAOpdDailyTool.getInstance().insertData(parm,conn);
        if (result.getErrCode() < 0) {
            System.out.println("�ż���ҽ��ͳ�����Σ������ż����м����Ϣʧ�� " + result.getErrCode() + result.getErrText()
                    + result.getErrName());
            conn.close();
            return false;
        }
        
        conn.commit();
        conn.close();
        return true;
    }
}
