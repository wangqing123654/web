package com.javahis.ui.hrm;

import java.sql.Timestamp;
import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.SystemTool;

/**
 * <p> Title: ����Ӧ��ʵ�ս���ѯ </p>
 *
 * <p> Description: ����Ӧ��ʵ�ս���ѯ </p>
 *
 * <p> Copyright: Copyright (c) 2012</p>
 *
 * <p> Company:BlueCore </p>
 *
 * @author WangLong 20121120
 * @version 1.0
 */
public class HRMAccountCheckControl extends TControl {

    /**
     * ��ʼ������
     */
    public void onInit() {
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String dateStr = StringTool.getString(sysDate, "yyyy/MM/dd");
        this.setValue("START_DATE", dateStr + " 00:00:00");
        this.setValue("END_DATE", dateStr + " 23:59:59");
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        String startDate = this.getText("START_DATE");// ��ʼʱ��
        String endDate = this.getText("END_DATE");// ��������
        String sql =
                "SELECT SUM(TOT_AMT) AS TOT_AMT,"// Ӧ�ս��
                        + "SUM(TOT_AMT) - SUM(AR_AMT) AS REDUCE_AMT,"// �����Ŀǰ��ϵͳ������������������������ġ�
                        + "SUM(AR_AMT) AS AR_AMT "// ʵ�ս��
                        + "  FROM BIL_OPB_RECP  "
                        + " WHERE ADM_TYPE ='H' "
                        + "   AND BILL_DATE BETWEEN TO_DATE('#', 'YYYY/MM/DD HH24:MI:SS') "
                        + "                     AND TO_DATE('#', 'YYYY/MM/DD HH24:MI:SS')";
        sql = sql.replaceFirst("#", startDate);
        sql = sql.replaceFirst("#", endDate);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            messageBox("ERR:" + result.getErrCode() + result.getErrText());
            return;
        }
        if (result.getCount() <= 0) {
            this.callFunction("UI|TABLE|setParmValue", new TParm());
            return;
        }
        this.callFunction("UI|TABLE|setParmValue", result);
    }

    /**
     * ���
     */
    public void onClear() {
        this.callFunction("UI|TABLE|setParmValue", new TParm());
    }

}
