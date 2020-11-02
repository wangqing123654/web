package action.onw;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import java.util.Map;
import java.util.HashMap;
import jdo.onw.ONWRegSchdayDrTool;
import jdo.reg.ClinicRoomTool;
import jdo.reg.SessionTool;
import java.util.Iterator;
import jdo.reg.SchDayTool;


/**
 *
 * <p>Title: ��ͨ�����ҽʦά��</p>
 *
 * <p>Description:��ͨ�����ҽʦά�� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author JiaoY
 *
 * @version 1.0
 */
public class ONWRegSchdayDrAction
    extends TAction {

    /**
     * �����������ͬ��
     * @param parm TParm
     * @return TParm
     */
    public TParm seldate(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        Map map = new HashMap();
        String regionCode= parm.getValue("REGION_CODE");
        //��ѯ��ͨ��ҽ�������Ű���Ϣ
        TParm schDayDr = ONWRegSchdayDrTool.getInstance().selectdata();
        int count = schDayDr.getCount();
        //����ͨ��ҽ��������ƴ�ӳ��ַ��� �������հ�����Ϣ���жԱȣ��ҳ�������
        for (int i = 0; i < count; i++) {
            String key = schDayDr.getValue("ADM_TYPE", i) + ":" +
                schDayDr.getValue("SESSION_CODE", i) + ":" +
                schDayDr.getValue("CLINIC_AREA", i) + ":" +
                schDayDr.getValue("CLINICROOM_NO", i);
            map.put(key, i);
        }
        if (parm.getErrCode() < 0)
            return null;
        //�����ż���
        TParm admTypes = new TParm();
        admTypes.addData("admType", "O");
        admTypes.addData("admType", "E");
        for (int admIndex = 0; admIndex < admTypes.getCount("admType");
             admIndex++) {
            String admType = admTypes.getData("admType", admIndex).toString();
            TParm ro = new TParm();
            ro.setData("ADM_TYPE", admType); //�ż�ס��
            ro.setData("REGION_CODE",regionCode);
            TParm rooms = ClinicRoomTool.getInstance().getClinicRoomForONW(ro); //��ȡ������Ϣ
            String[] sessions = SessionTool.getInstance().getSessionCode(
                admType,regionCode); //�õ���ʱ�α��
            int roomCount = rooms.getCount();
            for (int roomIndex = 0; roomIndex < roomCount; roomIndex++) {
                for (int sessionIndex = 0; sessionIndex < sessions.length;
                     sessionIndex++) {
                    String key = admType + ":" + sessions[sessionIndex] + ":" +
                        rooms.getValue("CLINICAREA_CODE", roomIndex) + ":" +
                        rooms.getValue("CLINICROOM_NO", roomIndex);
                    //����������ַ�ת �� ��ͨ����в����� �����µ����� ��Ҫ���뵽��ͨ�����
                    if (map.get(key) == null) {
                        //insert
                        TParm inParm = new TParm();
                        inParm.setData("ADM_TYPE", admType);
                        inParm.setData("SESSION_CODE", sessions[sessionIndex]);
                        inParm.setData("CLINIC_AREA",
                                       rooms.getValue("CLINICAREA_CODE",
                            roomIndex));
                        inParm.setData("CLINICROOM_NO",
                                       rooms.getValue("CLINICROOM_NO",
                            roomIndex));
                        inParm.setData("REGION",
                                       rooms.getValue("REGION_CODE", roomIndex));
                        inParm.setData("OPT_USER", parm.getData("OPT_USER"));
                        inParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
                        if (inParm.getValue("REGION") == null) {
                            return null;
                        }
                        result = ONWRegSchdayDrTool.getInstance().insertData(
                            inParm, connection);
                        //�������
                        if (result.getErrCode() < 0) {
                            connection.close();
                            return result;
                        }
                    }
                    else//�������д��� ��ɾ��Map�����ַ���
                        map.remove(key);
                    //----ͬ�����-----
                }
            }
        }
        //map��ʣ�µ�ֵ ��Ŀǰ�հ�����Ѿ������ڵ����ң�����Ҫ����ͨ�Ű���еĶ�Ӧ������ɾ����
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
//          ---------ɾ��-----------
            String name = (String) iterator.next();
            int index = (Integer) map.get(name);
            TParm inParm = new TParm();
            inParm.setRowData( -1, schDayDr, index);
            inParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
            result = ONWRegSchdayDrTool.getInstance().deleteData(inParm,
                connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * �����Ű�ͬ��
     * @param parm TParm
     * @return TParm
     */
    public TParm selReghday(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        TParm date = new TParm();
        TParm seldate = new TParm();
        TParm select = ONWRegSchdayDrTool.getInstance().selectdata(); //��ѯREG_SCHDAY_DR

        select.setData("REGION_CODE", parm.getData("REGION_CODE"));
        select.setData("ADM_DATE", parm.getData("ADM_DATE"));
        select.setData("OPT_USER", parm.getData("OPT_USER"));
        select.setData("OPT_TERM", parm.getData("OPT_USER"));

        date = SchDayTool.getInstance().selectdata(parm); //��ѯ�հ��
        int count = date.getCount();

        TParm schdydr = ONWRegSchdayDrTool.getInstance().clear(connection); //���REG_SCHDAY_DR�� �� ���� ҽ��
        for (int i = 0; i < count; i++) {
            seldate = new TParm();
            seldate.setData("REGION_CODE", parm.getValue("REGION_CODE"));
            seldate.setData("ADM_TYPE", date.getData("ADM_TYPE", i));
            seldate.setData("SESSION_CODE", date.getData("SESSION_CODE", i));
            seldate.setData("CLINICROOM_NO", date.getData("CLINICROOM_NO", i));
            seldate.setData("DEPT_CODE", date.getData("DEPT_CODE", i));
            seldate.setData("DR_CODE",
                            (date.getData("DR_CODE", i) == null) ? "" :
                            date.getData("DR_CODE", i));
            seldate.setData("CLINICTYPE_CODE",
                            date.getData("CLINICTYPE_CODE", i));
            seldate.setData("OPT_USER", parm.getValue("OPT_USER"));
            seldate.setData("OPT_TERM", parm.getValue("OPT_TERM"));
            result = new TParm();
            result = ONWRegSchdayDrTool.getInstance().update(seldate,
                connection); //���հ�� ͬ��
            if(result.getErrCode()<0){
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ���� ��insert reg_schday ,updata reg_schday_dr��
     * @param parm TParm
     * @return TParm
     */
    public TParm onSave(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        result = SchDayTool.getInstance().insertdata(parm,
            connection); //���հ���������
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        result = ONWRegSchdayDrTool.getInstance().onSave(parm, connection); //����chday_dr��
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ����
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdata(TParm parm) {
        TParm result = new TParm();
        TConnection connection = getConnection();
        result = SchDayTool.getInstance().updateForSchdayDr(parm, connection); //����Schday��
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }

        result = ONWRegSchdayDrTool.getInstance().onSave(parm, connection); //����chday_dr��
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
}
