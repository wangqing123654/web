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
import jdo.sta.STAStationDailyTool;


/**
 * <p>Title: ������־����չ��������</p>
 *
 * <p>Description: ������־����չ��������</p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wanglong 2013.06.06
 * @version 1.0
 */
public class INWStationLogBatch
        extends Patch {

    public INWStationLogBatch() {}

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
        sql.setData("STADATE", StringTool.getString(yesterday, "yyyyMMdd"));
        TParm inParm = this.getParm();
        sql.setData("OPT_USER", inParm.getValue("OPT_USER"));// SL_BATCH
        sql.setData("OPT_TERM", inParm.getValue("OPT_TERM"));// 127.0.0.1
        sql.setData("REGION_CODE", inParm.getValue("REGION_CODE"));
        TParm dept = new TParm();
        dept = STADeptListTool.getInstance().selectIPD_DEPT(sql);
        if (dept.getErrCode() < 0) {
            System.out.println("������־���Σ���ѯ�����м���סԺ����ʧ�� " + dept.getErrCode() + dept.getErrText()
                    + dept.getErrName());
            return false;
        }
        TParm parm = new TParm();
        parm.setData("SQL", sql.getData());
        parm.setData("DEPT", dept.getData());
        TParm result = new TParm();
        result = STAStationDailyTool.getInstance().insertData(parm, conn);
        if (result.getErrCode() < 0) {
            System.out.println("������־���Σ����벡���м����Ϣʧ�� " + result.getErrCode() + result.getErrText()
                    + result.getErrName());
            conn.close();
            return false;
        }
        
        //add by yangjj 20150923Ժ����־����
        TParm result1 = new TParm();
        result1 = INFCaseTool.getInstance().insertDailyRecData(parm, conn);
        if (result1.getErrCode() < 0) {
        	System.out.println("Ժ����־���Σ����벡���м����Ϣʧ�� " + result1.getErrCode() + result1.getErrText()
                    + result1.getErrName());
            conn.close();
            return false;
        }
        conn.commit();
        conn.close();
        return true;
    }
}
