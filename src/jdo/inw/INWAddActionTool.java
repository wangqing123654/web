package jdo.inw;

import com.dongyang.action.IOAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.adm.ADMTool;

/**
 * <p>Title: ��ʿվ��Ҷ�����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH
 * @version 1.0
 */
public class INWAddActionTool
    extends IOAction {

    public INWAddActionTool() {
    }

    public void onInit() {
        TParm orderParm = this.getParm();
        //��������·��Key
        this.setLogPathKey("ServerLogPath");
        //���������ļ���
        this.setLogFileName("TEST");
        //���������û���Ϣ
        this.setLogUserInf(orderParm.getValue("OPT_USER"));
        //���������û�IP
        this.setLogUserIP(orderParm.getValue("OPT_TERM"));
    }

    public void onRun() {
        TConnection con = getConnection();
        TParm orderParm = this.getParm();
        String orderCode = orderParm.getValue("ORDER_CODE");
        String caseNo = orderParm.getValue("CASE_NO");
        String runID = orderParm.getValue("RUN_ID");
        TParm parm1 = new TParm();
        TParm parm2 = new TParm();
        if ("0001".equals(runID)) {
            parm1.setData("CASE_NO", caseNo);
            parm1.setData("NURSING_CLASS", "N1");

            parm2.setData("CASE_NO", caseNo);
            parm2.setData("PATIENT_STATUS", "S" + 2);
            //һ������
            ADMTool.getInstance().updateNURSING_CLASS(parm1, con);
            ADMTool.getInstance().updatePATIENT_STATUS(parm2, con);

        }
        else if ("0002".equals(runID)) {
            parm1.setData("CASE_NO", caseNo);
            parm1.setData("NURSING_CLASS", "N2");

            parm2.setData("CASE_NO", caseNo);
            parm2.setData("PATIENT_STATUS", "S" + 2);
            //��������
            ADMTool.getInstance().updateNURSING_CLASS(parm1, con);
            ADMTool.getInstance().updatePATIENT_STATUS(parm2, con);
        }
    }

}
