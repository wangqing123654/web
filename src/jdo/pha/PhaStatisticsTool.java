package jdo.pha;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;


/**
 *
 * <p>Title: �ż���ҩ����������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2008.09.26
 * @version 1.0
 */

public class PhaStatisticsTool
    extends TJDOTool {

    /**
     * ʵ��
     */
    public static PhaStatisticsTool instanceObject;

    /**
     * �õ�ʵ��
     * @return OrderTool
     */
    public static PhaStatisticsTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhaStatisticsTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public PhaStatisticsTool() {
        //����Module�ļ�
        setModuleName("pha\\PhaStatisticsModule.x");

        onInit();
    }

    /**
     * ��á��ż���ҩʦ������ͳ�Ʊ�����������
     * @return TParm
     */
    public TParm getPhaStatisticsMainDate(TParm parm, String type) {

        TParm result = new TParm();
        //��˲�ѯ
        if (type.equals("���")) {
            parm.setData("PHA_CHECK_DATE", "Y");
            result = query("queryApothecaryLoadCheck", parm);
        } //��ҩ��ѯ
        else if (type.equals("��ҩ")) {
            parm.setData("PHA_DOSAGE_DATE", "Y");
            result = query("queryApothecaryLoadDispense", parm);
        } //��ҩ��ѯ
        else if (type.equals("��ҩ")) {
            parm.setData("PHA_DISPENSE_DATE", "Y");
            result = query("queryApothecaryLoadSend", parm);
        } //��ҩ��ѯ
        else if (type.equals("��ҩ")) {
            parm.setData("PHA_RETN_DATE", "Y");
            result = query("queryApothecaryLoadReturn", parm);
        }

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
