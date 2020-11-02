package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TCM_Transform;

/**
 *
 * <p>Title:���ŷ�ʽ������ </p>
 *
 * <p>Description:���ŷ�ʽ������ </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.17
 * @version 1.0
 */
public class REGQueMethodTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static REGQueMethodTool instanceObject;
    /**
     * �õ�ʵ��
     * @return REGQueMethodTool
     */
    public static REGQueMethodTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGQueMethodTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public REGQueMethodTool() {
        setModuleName("reg\\REGQueMethodModule.x");
        onInit();
    }
    /**
     * ��ѯ���ŷ�ʽ
     * @param quegroupCode String
     * @param queNo String
     * @return TParm
     */
    public TParm queryTree(String quegroupCode, String queNo) {
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE", quegroupCode);
        parm.setData("QUE_NO", queNo);
        TParm result = query("queryTree", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;

    }
    /**
     * �������ŷ�ʽ
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String quegroupCode = parm.getValue("QUEGROUP_CODE");
        String queNo = parm.getValue("QUE_NO");
        if (existsQueMethod(quegroupCode, queNo)) {
            result.setErr( -1, "���ŷ�ʽ" + " �Ѿ�����!");
            return result;
        }

        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���¸��ŷ�ʽ
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���¸��ŷ�ʽ(������)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updatQueNo(TParm parm, TConnection connection) {
        TParm result = update("updatQueNo", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ���ŷ�ʽ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ�����ŷ�ʽ
     * @param quegroupCode String
     * @param queNo String
     * @return TParm
     */
    public TParm deletedata(String quegroupCode, String queNo) {
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE", quegroupCode);
        parm.setData("QUE_NO", queNo);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �ж��Ƿ���ڸ��ŷ�ʽ
     * @param quegroupCode String
     * @param queNo String
     * @return boolean
     */
    public boolean existsQueMethod(String quegroupCode, String queNo) {
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE", quegroupCode);
        parm.setData("QUE_NO", queNo);
        return getResultInt(query("existsQueMethod", parm), "COUNT") > 0;
    }

    /**
     * �趨��Ժʱ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm setArriveTime(TParm parm, TConnection connection) {
        TParm result = update("updateArriveTime", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ʱ���ۼӷ���
     * @param startTime String
     * @param interveenTime String
     * @return String
     */
    public String addTime(String startTime, String interveenTime) {
        //����Сʱ
        String hourS = startTime.substring(0, 2);
        //�������
        String minuteS = startTime.substring(2, 4);
        int hourM = TCM_Transform.getInt(hourS);
        int minuteM = TCM_Transform.getInt(minuteS);
        //���ʱ��
        int partition = TCM_Transform.getInt(interveenTime);
        double c = (minuteM + partition) / 60;
        //���Ӳ���+���ʱ���,ȡ��(Сʱ��λ��)
        int f = TCM_Transform.getInt(c);
        //���Ӳ���+���ʱ���,ȡ��(����ʣ����)
        int d = (minuteM + partition) % 60;
        minuteM = minuteM + partition;
        if (minuteM + partition > 59) {
            hourM = hourM + f;
            minuteM = d;
        }
        if (hourM > 23) {
            hourM = hourM - 24;
        }
        String hourMS = TCM_Transform.getString(hourM);
        int hourLength = hourMS.length();
        if (hourLength <= 1)
            hourLength = 2;
        String hourE = StringTool.fill("0", hourLength - hourMS.length()) +
            hourMS;
        String minuteMS = TCM_Transform.getString(minuteM);
        int minuteLength = minuteMS.length();
        if (minuteLength <= 1)
            minuteLength = 2;
        String minuteE = StringTool.fill("0", minuteLength - minuteMS.length()) +
            minuteMS;
        String endTime = hourE + minuteE;
        return endTime;
    }

    /**
     * ˢ��table�ò�ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm seldataForTable(TParm parm) {
        TParm result = new TParm();
        result = query("seldataForTable", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
}
