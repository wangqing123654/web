package com.javahis.ui.sta;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.sta.STAOpdDailyTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.data.TSocket;
import jdo.sta.STADeptListTool;
import java.util.Map;
import jdo.sta.STAStationDailyTool;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.ui.TTextFormat;
import jdo.sys.Operator;

/**
 * <p>Title: �м���������</p>
 *
 * <p>Description: �м���������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-27
 * @version 1.0
 */
public class STADailyControl
    extends TControl {

    public void onInit() {
        //��ȡ���������
        Timestamp time = StringTool.rollDate(SystemTool.getInstance().getDate(),
                                             -1);
        this.setValue("STADATE", time);
    }

    /**
     * ����ָ�����ڵ�������Ϣ
     */
    public void insertSTA_OPD_DAILY() {
        String STADATE = this.getText("STADATE").replace("/", "");
        if (STADATE.trim().length() <= 0) {
            this.messageBox_("��ѡ�����ڣ�");
            return;
        }
        //��ȡѡ�����ڵ�ǰһ�������
        Timestamp time = StringTool.rollDate( (Timestamp)this.getValue(
            "STADATE"), -1);
        String lastDay = StringTool.getString(time, "yyyyMMdd");
        TParm checkLastDay = new TParm();
        checkLastDay.setData("STA_DATE", lastDay);
        //======================pangben modify 20110520 start �������
        if(null!=Operator.getRegion()&&Operator.getRegion().length()>0)
            checkLastDay.setData("REGION_CODE", Operator.getRegion());
        //======================pangben modify 20110520 stop
        //��ȡѡ�����ڵ�ǰһ�������
        TParm check = STAOpdDailyTool.getInstance().select_STA_OPD_DAILY(
            checkLastDay);
        if (check.getCount("STA_DATE") <= 0) { //���ǰһ�����ݲ����ڣ��������룬��Ӱ������׼ȷ��
            switch (this.messageBox("��ʾ��Ϣ",
                                    StringTool.getString(time, "yyyy��MM��dd��") +
                                    "�����ݲ�����\n���������ݻ�Ӱ��׼ȷ��\n�Ƿ��룿",
                                    this.YES_NO_OPTION)) {
            case 0: //����
                break;
            case 1: //������
                return;
            }
        }
        //���Ҫ����������Ƿ��Ѿ�����
        TParm checkDay = new TParm();
        checkDay.setData("STA_DATE", STADATE);
        //======================pangben modify 20110520 start �������
        if(null!=Operator.getRegion()&&Operator.getRegion().length()>0)
            checkDay.setData("REGION_CODE", Operator.getRegion());
        //======================pangben modify 20110520 stop
        check = STAOpdDailyTool.getInstance().select_STA_OPD_DAILY(checkDay);
        if (check.getCount("STA_DATE") > 0) { //������ݴ��ڣ�ѯ���Ƿ���
            switch (this.messageBox("��ʾ��Ϣ",
                                    "�����Ѵ��ڣ��Ƿ��������ɣ�", this.YES_NO_OPTION)) {
                case 0: //����
                    break;
                case 1: //������
                    return;
            }
        }
        TParm sql = new TParm();
        sql.setData("ADMDATE", STADATE);
        sql.setData("OPT_USER",Operator.getID());
        sql.setData("OPT_TERM",Operator.getIP());
        //============pangben modify 20110520 start
        sql.setData("REGION_CODE",Operator.getRegion());
         //============pangben modify 20110520 stop
        TParm dept = new TParm();
        //============pangben modify 20110520 ����������
        dept = STADeptListTool.getInstance().selectOE_DEPT(Operator.getRegion());
        TParm parm = new TParm();
        parm.setData("SQL", sql.getData());
        parm.setData("DEPT", dept.getData());
        TParm result = TIOM_AppServer.executeAction(
            "action.sta.STADailyAction",
            "insertSTA_OPD_DAILY", parm);
        if (result.getErrCode() < 0) {
            System.out.println("" + result);
            return;
        }
        this.messageBox_("�����м䵵����ɹ���");
    }

    /**
     * ����ָ�����ڵ�סԺ��Ϣ
     */
    public void insertStation_Daily() {
        String STADATE = this.getText("STADATE").replace("/", "");
        if (STADATE.trim().length() <= 0) {
            this.messageBox_("��ѡ�����ڣ�");
            return;
        }
        //��ȡѡ�����ڵ�ǰһ�������
        Timestamp time = StringTool.rollDate( (Timestamp)this.getValue(
            "STADATE"), -1);
        String lastDay = StringTool.getString(time, "yyyyMMdd");
        TParm checkLastDay = new TParm();
        checkLastDay.setData("STA_DATE", lastDay);
        //==========pangben modify 20110523 start
        checkLastDay.setData("REGION_CODE", Operator.getRegion());
        //==========pangben modify 20110523 start
        //��ȡѡ�����ڵ�ǰһ�������
        TParm check = STAStationDailyTool.getInstance().select_Station_Daily(
            checkLastDay);
        if (check.getCount("STA_DATE") <= 0) { //���ǰһ�����ݲ����ڣ���Ӱ������׼ȷ�ȣ�ѯ���Ƿ���
            switch (this.messageBox("��ʾ��Ϣ",
                                    StringTool.getString(time, "yyyy��MM��dd��") +
                                    "�����ݲ�����\n���������ݻ�Ӱ��׼ȷ��\n�Ƿ��룿",
                                    this.YES_NO_OPTION)) {
                case 0: //����
                    break;
                case 1: //������
                    return;
            }
        }

        //���Ҫ����������Ƿ��Ѿ�����
        TParm checkDay = new TParm();
        checkDay.setData("STA_DATE", STADATE);
        //==========pangben modify 20110523 start
        checkDay.setData("REGION_CODE", Operator.getRegion());
        //==========pangben modify 20110523 start

        check = STAStationDailyTool.getInstance().select_Station_Daily(checkDay);
        if (check.getCount("STA_DATE") > 0) { //������ݴ��ڣ�ѯ���Ƿ���
            switch (this.messageBox("��ʾ��Ϣ",
                                    "�����Ѵ��ڣ��Ƿ��������ɣ�", this.YES_NO_OPTION)) {
                case 0: //����
                    break;
                case 1: //������
                    return;
            }
        }

        TParm sql = new TParm();
        sql.setData("STADATE", STADATE);
        sql.setData("OPT_USER",Operator.getID());
        sql.setData("OPT_TERM",Operator.getIP());
        //============pangben modify 20110520 start
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            sql.setData("REGION_CODE", Operator.getRegion());
         //============pangben modify 20110520 stop
        TParm dept = new TParm();
        dept = STADeptListTool.getInstance().selectIPD_DEPT(sql);//=====pangben modify 20110520 ����������
        TParm parm = new TParm();
        parm.setData("SQL", sql.getData());
        parm.setData("DEPT", dept.getData());
        TParm result = TIOM_AppServer.executeAction(
            "action.sta.STADailyAction",
            "insertStation_Daily", parm);
        if (result.getErrCode() < 0) {
            System.out.println("" + result);
            return;
        }
        this.messageBox_("סԺ�м䵵����ɹ���");
    }

    public static void main(String[] args) {
        com.javahis.util.JavaHisDebug.runFrame("sta\\STADaily.x");
    }
}
