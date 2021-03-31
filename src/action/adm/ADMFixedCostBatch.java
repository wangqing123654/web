package action.adm;

import com.dongyang.patch.Patch;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import java.util.Date;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.adm.ADMAutoBillTool;
import jdo.sys.SystemTool;

/**
 * <p>Title: �̶���������չ��������</p>
 *
 * <p>Description: �̶���������չ��������</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2012.03.26
 * @version 1.0
 */
public class ADMFixedCostBatch extends Patch {
    public ADMFixedCostBatch() {
    }
    /**
     * �����߳�
     * @return boolean
     */
    public boolean run() {
        TConnection connection = TDBPoolManager.getInstance().getConnection();
        Timestamp date = StringTool.getTimestamp(new Date());
        String queryDate = StringTool.getString(date, "yyyyMMdd") + "000000";
        String sql =
                " SELECT C.PAT_NAME, C.BIRTH_DATE, B.IN_DATE, B.DS_DATE, B.CTZ1_CODE, B.MR_NO," +
                "        B.CASE_NO, B.IPD_NO, B.TOTAL_AMT, B.TOTAL_BILPAY, B.STATION_CODE," +
                "        B.DEPT_CODE, B.CUR_AMT, B.BILL_STATUS " +
                "   FROM SYS_BED A, ADM_INP B, SYS_PATINFO C " +
                "  WHERE A.BED_NO = B.BED_NO(+) " +
                "    AND A.CASE_NO = B.CASE_NO(+) " +
                "    AND A.MR_NO = B.MR_NO(+) " +
                "    AND A.MR_NO = C.MR_NO(+) " +
                "    AND A.ACTIVE_FLG = 'Y' " +
                "    AND B.DS_DATE IS NULL " +
                "    AND A.ALLO_FLG = 'Y' " +
                "    AND B.CANCEL_FLG <> 'Y' " +
                "    AND A.BED_STATUS = '1' " +
                // add by wangb ���0:00��0:10��Ժ�Ĳ�����Ǵ�λ�ѵ�����
                "    AND B.ADM_DATE < TO_DATE('" + queryDate + "','YYYYMMDDHH24MISS') " +
                "  ORDER BY A.BED_NO ";
        //��ѯ��Ժ����
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        parm.setData("DATE", date);
        // �Ƿ������Ժ����
        if (parm.getCount() <= 0) {
            connection.close();
            return false;
        }
        try {
            for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
                if (insertSingleData(parm, i, connection)) {
                    connection.commit();
                }
                continue;
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.commit();
        connection.close();
        return true;
    }
    /**
     * ѭ��
     * @param parm TParm
     * @param row int
     * @param connection TConnection
     * @return boolean
     */
    public boolean insertSingleData(TParm parm, int row, TConnection connection) {
        TParm result = new TParm();
        TParm batchParm = new TParm();
        TParm selMaxSeqParm = new TParm();
        TParm selMaxSeq = new TParm();
        TParm batchLogParm = new TParm();
        String caseNo = parm.getValue("CASE_NO", row);
        String postDate = StringTool.getString(parm.getTimestamp("DATE"),
                                               "yyyyMMdd") + "235959";
        selMaxSeq.setData("POST_DATE",postDate);
        selMaxSeq.setData("SYSTEM_CODE","FIXED_COST");
        //��ѯ���������־�����
        selMaxSeqParm = SystemTool.getInstance().selMaxBatchSeq(selMaxSeq);
        if (selMaxSeqParm.getErrCode() < 0) {
            return false;
        }
        int maxSeq = selMaxSeqParm.getInt("SEQ", 0);
        maxSeq = maxSeq + 1;
        TParm actionParm = new TParm();
        String mrNo = parm.getValue("CASE_NO", row);
        String ipdNo = parm.getValue("IPD_NO", row);
        String deptCode = parm.getValue("DEPT_CODE", row);
        String stationCode = parm.getValue("STATION_CODE", row);
        actionParm.setData("CASE_NO", caseNo);
        actionParm.setData("DATE", parm.getTimestamp("DATE"));
        actionParm.setData("OPT_USER", "FC_BATCH");
        actionParm.setData("OPT_TERM", "127.0.0.1");
        batchLogParm.setData("POST_DATE", postDate);
        batchLogParm.setData("SYSTEM_CODE", "FIXED_COST");
        batchLogParm.setData("SEQ", maxSeq);
        batchLogParm.setData("CASE_NO", caseNo);
        batchLogParm.setData("MR_NO", mrNo);
        batchLogParm.setData("IPD_NO", ipdNo);
        batchLogParm.setData("DEPT_CODE", deptCode);
        batchLogParm.setData("STATION_CODE", stationCode);
        batchLogParm.setData("OPT_USER", "FC_BATCH");
        batchLogParm.setData("OPT_TERM", "127.0.0.1");
        //ִ�й̶��������α���
        result = ADMAutoBillTool.getInstance().postAutoBillOfMen(actionParm,
                connection);
        if (result.getErrCode() < 0) {
        	connection.rollback();
            //д��������־��(ʧ��)
            actionParm.setData("STATUS", "0");
            batchParm = SystemTool.getInstance().insertBatchLog(batchLogParm,
                    connection);
            if (batchParm.getErrCode() < 0) {
                return false;
            }
            connection.commit();
            return false;
        }
        return true;
    }
    
	/**
	 * סԺҩ�·����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm phaServiceFee(TParm parm) {
		TConnection conn = TDBPoolManager.getInstance().getConnection();
		TParm result = new TParm();
		try {
			result = ADMAutoBillTool.getInstance().phaServiceFee(parm, conn);
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
		} finally {
			conn.close();
		}
		return result;
	}
}
