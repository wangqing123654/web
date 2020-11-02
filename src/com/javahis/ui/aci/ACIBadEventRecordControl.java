package com.javahis.ui.aci;

import java.sql.Timestamp;
import java.util.Calendar;
import jdo.aci.ACIBadEventTool;
import jdo.sys.PatTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: �����¼��Ǽ� </p>
 *
 * <p> Description: �����¼��Ǽ� </p>
 *
 * <p> Copyright: Copyright (c) 2012 </p>
 *
 * <p> Company: BlueCore </p>
 *
 * @author WangLong 2012.11.22
 * @version 1.0
 */
public class ACIBadEventRecordControl extends TControl {
    
    TMenuItem insertMenu;// "����"��ť
    TMenuItem updateMenu;// "����"��ť
    TMenuItem examineMenu;// "���"��ť
    TMenuItem unExamineMenu;// "ȡ�����"��ť
    TMenuItem newMenu;// "����"��ť
    TMenuItem rateMenu;// "����"��ť add by wanglong 20131101
    TMenuItem smsMenu;// "������"��ť add by wanglong 20131101
    TTextFormat queryReportDept;// �ϱ�����(��ѯ��)
    TTextFormat queryDeptInCharge;// ���ο���(��ѯ��)
    TTextFormat reportDept;// �ϱ�����
    TTextFormat reportStation;// �ϱ�����
    TTextFormat reportUser;// �ϱ���
    private TParm usersParm;// ��ϵ�� add by wanglong 20140107
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        Object obj = this.getParameter();
        if (obj instanceof TParm) {// add by wanglong 20130428
            TParm parm = (TParm) obj;
            if (parm.getValue("POPEDEM").length() != 0) {
                if ("1".equals(parm.getValue("POPEDEM"))) { // һ��Ȩ��
                    this.setPopedem("NORMAL", true);
                    this.setPopedem("SYSOPERATOR", false);
                    this.setPopedem("SYSDBA", false);
                }
                if ("2".equals(parm.getValue("POPEDEM"))) { // ��ɫȨ��
                    this.setPopedem("NORMAL", false);
                    this.setPopedem("SYSOPERATOR", true);
                    this.setPopedem("SYSDBA", false);
                }
                if ("3".equals(parm.getValue("POPEDEM"))) { // ���Ȩ��
                    this.setPopedem("NORMAL", false);
                    this.setPopedem("SYSOPERATOR", false);
                    this.setPopedem("SYSDBA", true);
                }
            }
        }
        callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        callFunction("UI|DIAG_CODE|setPopupMenuParameter", "DIAG_CODE",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");//add by wanglong 20140107
        callFunction("UI|DIAG_CODE|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popICDReturn");
        insertMenu = (TMenuItem) this.getComponent("insert");// ����
        updateMenu = (TMenuItem) this.getComponent("update");// ����
        examineMenu = (TMenuItem) this.getComponent("examine");// ���
        unExamineMenu = (TMenuItem) this.getComponent("unexamine");// ȡ�����
        newMenu = (TMenuItem) this.getComponent("new");// ����
        rateMenu = (TMenuItem) this.getComponent("rate");// ����
        smsMenu = (TMenuItem) this.getComponent("sms");// ������
        queryReportDept = (TTextFormat) this.getComponent("QUERY_REPORT_DEPT");// �ϱ�����(��ѯ��)
        queryDeptInCharge = (TTextFormat) this.getComponent("QUERY_DEPT_IN_CHARGE");// ���ο���(��ѯ��)
        reportDept = (TTextFormat) this.getComponent("REPORT_DEPT");// �ϱ�����
        reportStation = (TTextFormat) this.getComponent("REPORT_STATION");// �ϱ�����
        reportUser = (TTextFormat) this.getComponent("REPORT_USER");// �ϱ���
        initUI();
    }

    /**
     * ��ʼ��������Ϣ
     */
    public void initUI() {
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -7);
        Timestamp today = SystemTool.getInstance().getDate();
        this.setValue("REPORT_USER", Operator.getID());// �ϱ���
        this.setValue("REPORT_DATE", today);// �ϱ�����
        this.setValue("START_DATE", yesterday);
        this.setValue("END_DATE", today);
        this.setValue("EVENT_DATE", today);// ��������
        if (this.getPopedem("NORMAL")) {
            String deptSql =
                    "SELECT A.DEPT_CODE AS ID, A.DEPT_ABS_DESC AS NAME, A.PY1 FROM SYS_DEPT A, SYS_OPERATOR_DEPT B "
                            + " WHERE A.DEPT_CODE = B.DEPT_CODE AND B.USER_ID = '#' AND A.ACTIVE_FLG = 'Y' ORDER BY A.DEPT_CODE, A.SEQ";
            deptSql = deptSql.replaceFirst("#", Operator.getID());
            queryReportDept.setPopupMenuSQL(deptSql);
            queryReportDept.setHisOneNullRow(false);// ��ͨ�û����ܽ���ȫ����ѯ
            queryReportDept.onQuery();
            String deptCode = StringUtil.getDesc("SYS_OPERATOR", "COST_CENTER_CODE", "USER_ID='" + Operator.getID() + "'");
            queryReportDept.setValue(deptCode);// ��ͨ�û��޶�ֻ�ܲ�ѯ�Լ����ڿ��ҵļ�¼
            queryDeptInCharge.setEnabled(false);// ��ͨ�û�����ʹ�����ο���
            reportDept.setPopupMenuSQL(deptSql);
            reportDept.onQuery();
            reportDept.setValue(deptCode);
            String stationSql =
                    "SELECT A.STATION_CODE AS ID, A.STATION_DESC AS NAME, A.PY1 FROM SYS_STATION A, SYS_OPERATOR_STATION B "
                            + " WHERE A.STATION_CODE = B.STATION_CLINIC_CODE AND B.USER_ID = '#' ORDER BY A.STATION_CODE, A.SEQ";
            stationSql = stationSql.replaceFirst("#", Operator.getID());
            reportStation.setPopupMenuSQL(stationSql);
            reportStation.onQuery();
//            String stationCode = StringUtil.getDesc("SYS_OPERATOR_STATION", "STATION_CLINIC_CODE", "USER_ID='" + Operator.getID() + "' AND TYPE='2' AND MAIN_FLG='Y'");
//            if(stationCode.equals("")){
//                stationCode = StringUtil.getDesc("SYS_OPERATOR_STATION", "STATION_CLINIC_CODE", "USER_ID='" + Operator.getID() + "' AND TYPE='2'");
//            }
//            reportStation.setValue(stationCode);
            reportUser.setPopupMenuSQL("SELECT USER_ID AS ID,USER_NAME AS NAME,USER_ENG_NAME AS ENNAME,PY1,PY2 FROM SYS_OPERATOR WHERE USER_ID='" + Operator.getID() + "'");
            reportUser.onQuery();
        }
        ((TRadioButton) this.getComponent("UNEXAMINED")).setSelected(true);
        insertMenu.setVisible(true);
        updateMenu.setVisible(false);
        examineMenu.setVisible(false);
        unExamineMenu.setVisible(false);
        rateMenu.setVisible(false);
        smsMenu.setVisible(false);
        this.callFunction("UI|EXAMINE_USER|setEnabled", false);
        this.callFunction("UI|EVENT_CLASS|setEnabled", false);
        this.callFunction("UI|EXAMINE_DATE|setEnabled", false);
        this.callFunction("UI|ASSESS_USER|setEnabled", false);
        this.callFunction("UI|SAC_CLASS|setEnabled", false);
        this.callFunction("UI|ASSESS_DATE|setEnabled", false);
        this.setValue("SMS_FLG", "N");
        this.callFunction("UI|SMS_FLG|setEnabled", false);
        onEnableSMS();
        if (this.getPopedem("SYSOPERATOR")) {// add by wanglong 20130428
            examineMenu.setVisible(true);
            this.callFunction("UI|EXAMINE_USER|setEnabled", true);
            this.callFunction("UI|EVENT_CLASS|setEnabled", true);
            this.callFunction("UI|EXAMINE_DATE|setEnabled", true);
            this.setValue("EXAMINE_USER", Operator.getID());// �����
            this.setValue("EXAMINE_DATE", today);// �������
            this.callFunction("UI|SMS_FLG|setEnabled", true);
        } else if (this.getPopedem("SYSDBA")) {// add by wanglong 20131101
            rateMenu.setVisible(true);
            this.callFunction("UI|ASSESS_USER|setEnabled", true);
            this.callFunction("UI|SAC_CLASS|setEnabled", true);
            this.callFunction("UI|ASSESS_DATE|setEnabled", true);
            this.setValue("ASSESS_USER", Operator.getID());// �����
            this.setValue("ASSESS_DATE", today);// �������
        }
        ((TTextArea) this.getComponent("SMS_USERS")).getTextArea().setEnabled(false);
    }
    
    /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
        if (row < 0) return;
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        setValueForParm("EVENT_TYPE;EVENT_DATE;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_USER;"
                + "REPORT_DATE;REPORT_SECTION;"
                + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;"
                + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                + "EVENT_DESC;EVENT_RESULT;"
                + "EXAMINE_USER;EVENT_CLASS;EXAMINE_DATE;"
                + "ASSESS_USER;SAC_CLASS;ASSESS_DATE", data, row);
        if (this.getPopedem("SYSOPERATOR")) {// add by wanglong 20130428
            boolean isExamined = (Boolean) data.getData("EXAMINE_FLG", row);
            if (isExamined == true) {
                unExamineMenu.setVisible(true);
                examineMenu.setVisible(false);
            } else {
                examineMenu.setVisible(true);
                unExamineMenu.setVisible(false);
            }
            if(this.getValueString("EXAMINE_USER").equals("")){// add by wanglong 20131101
                setValue("EXAMINE_USER", Operator.getID());
            }
            setValue("EXAMINE_DATE", SystemTool.getInstance().getDate());
        }
        if (this.getPopedem("SYSDBA")) {// add by wanglong 20131101
            rateMenu.setVisible(true);
            if(this.getValueString("ASSESS_USER").equals("")){
                setValue("ASSESS_USER", Operator.getID());
            }
            setValue("ASSESS_DATE", SystemTool.getInstance().getDate());
        }
        insertMenu.setVisible(false);
        updateMenu.setVisible(true);
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = new TParm();
        Timestamp startDate = (Timestamp) this.getValue("START_DATE");
        Timestamp endDate = (Timestamp) this.getValue("END_DATE");
        endDate = new Timestamp(endDate.getTime() + 24 * 60 * 60 * 1000 - 1);
        parm.setData("START_DATE", startDate);
        parm.setData("END_DATE", endDate);
        if (this.getValue("EXAMINED").equals("Y")) {
            parm.setData("EXAMINED", "Y");// ���
        } else {
            parm.setData("UNEXAMINED", "Y");// δ���
        }
        if (!this.getValueString("QUERY_REPORT_DEPT").equals("")) {
            parm.setData("REPORT_DEPT", this.getValue("QUERY_REPORT_DEPT"));// �ϱ�����
        }
        if (!this.getValueString("QUERY_DEPT_IN_CHARGE").equals("")) {
            parm.setData("DEPT_IN_CHARGE", this.getValue("QUERY_DEPT_IN_CHARGE"));// ���ο���
        }
        if (!this.getValueString("QUERY_EVENT_TYPE").equals("")) {
            parm.setData("EVENT_TYPE", this.getValue("QUERY_EVENT_TYPE") + "%");// �����¼�����
        }
        if (!this.getValueString("QUERY_EVENT_CLASS").equals("")) {
            parm.setData("EVENT_CLASS", this.getValue("QUERY_EVENT_CLASS"));// �����¼��ȼ�
        }
        if (!this.getValueString("QUERY_SAC_CLASS").equals("")) {
            parm.setData("SAC_CLASS", this.getValue("QUERY_SAC_CLASS"));// SAC�ּ�
        }
        TParm result = ACIBadEventTool.getInstance().selectBadEventData(parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + "" + result.getErrText());
        }
        clearValue("COUNT");// �������
        this.callFunction("UI|TABLE|setParmValue", new TParm());
        if (result.getCount() <= 0) {
            messageBox("E0008");// ��������
            return;
        }
        ((TTextField) getComponent("COUNT")).setValue(result.getCount() + "");// �������
        this.callFunction("UI|TABLE|setParmValue", result);
    }
    
    /**
     * ����
     */
    public void onSave() {
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow >= 0) {
            return;
        }
        TParm parm = getParmForTag("EVENT_TYPE;EVENT_DATE:Timestamp;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_USER;"
                + "REPORT_DATE:Timestamp;REPORT_SECTION;"
                + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;"
                + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                + "EVENT_DESC;EVENT_RESULT");
        String mrNo = parm.getValue("MR_NO");
        mrNo = PatTool.getInstance().checkMrno(mrNo);// �����Ų��볤�� add by wanglong 20130428
        parm.setData("MR_NO", mrNo);
        if (parm.getValue("EVENT_TYPE").equals("")) {
            messageBox("����д�¼�����");
            return;
        }
        if (parm.getValue("EVENT_DATE").equals("")) {
            messageBox("����д��������");
            return;
        }
        if (parm.getValue("REPORT_DEPT").equals("")) {
            messageBox("����д�ϱ�����");
            return;
        }
//        if (parm.getValue("REPORT_STATION").equals("")) {
//            String sql =
//                    "SELECT * FROM SYS_STATION WHERE DEPT_CODE = '" + parm.getValue("REPORT_DEPT")
//                            + "'";
//            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//            if (result.getErrCode() != 0) {
//                this.messageBox("��鲡������ʧ��");
//                return;
//            }
//            if (result.getCount() > 0) {
//                messageBox("����д����");
//                return;
//            }
//        }
        if (parm.getValue("REPORT_USER").equals("")) {
            messageBox("����д�ϱ���");
            return;
        }
        if (parm.getTimestamp("REPORT_DATE").getTime() < parm.getTimestamp("EVENT_DATE").getTime()) {
            messageBox("�ϱ����ڲ������ڷ�������");
            return;
        }
        if (parm.getValue("REPORT_SECTION").equals("")) {
            messageBox("����д���Ͳ���");
            return;
        }
//        if (parm.getValue("PAT_NAME").equals("")) {
//            messageBox("����д��������");
//            return;
//        }
//        if (parm.getValue("SEX_CODE").equals("")) {
//            messageBox("����д�Ա�");
//            return;
//        }
//        if (parm.getValue("AGE").equals("")) {
//            messageBox("����д����");
//            return;
//        }
        // modify by wangb 2017/11/10
        // ��Ҫ�����ˡ����ο��ҡ���ϡ������¼������������¼������������������ȡ����ʾ
//        if (parm.getValue("EVENT_DESC").equals("")) {
//            messageBox("����д�����¼�����");
//            return;
//        }
//        if (parm.getValue("EVENT_RESULT").equals("")) {
//            messageBox("����д�����¼��������");
//            return;
//        }
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = ACIBadEventTool.getInstance().insertBadEventData(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        } else {
            clearValue("EVENT_TYPE;EVENT_DATE;"
//                    + "REPORT_DEPT;REPORT_STATION;REPORT_USER;"
                    + "REPORT_STATION;"
                    + "REPORT_DATE;REPORT_SECTION;"
                    + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;"
                    + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                    + "EVENT_DESC;EVENT_RESULT;");
            this.messageBox("P0002");//�����ɹ�
        } 
    }

    /**
     * ����
     */
    public void onUpdate() {
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            return;
        }
        TParm data = table.getParmValue();
        if (this.getPopedem("NORMAL")) {
            if (!data.getValue("REPORT_USER", selRow).equals(Operator.getID())) {
                this.messageBox("ֻ���ϱ����ܸ��´��¼�");
                return;
            }
        }
        String sacClass = data.getValue("SAC_CLASS", selRow);
        if (!sacClass.equals("")) {
            messageBox("��������������ܸ�����Ϣ");
            return;
        }
        boolean examineFlg = (Boolean) data.getData("EXAMINE_FLG", selRow);
        if (examineFlg == true) {
            messageBox("�������ˣ����ܸ�����Ϣ");
            return;
        }
        String aciNo = data.getValue("ACI_NO", selRow);
        TParm parm = getParmForTag("EVENT_TYPE;EVENT_DATE:Timestamp;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_USER;REPORT_DATE:Timestamp;REPORT_SECTION;"
                + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;USER_IN_CHARGE;DEPT_IN_CHARGE;"
                + "EVENT_DESC;EVENT_RESULT");
        if (parm.getValue("EVENT_TYPE").equals("")) {
            messageBox("����д�¼�����");
            return;
        }
        if (parm.getValue("EVENT_DATE").equals("")) {
            messageBox("����д��������");
            return;
        }
        if (parm.getValue("REPORT_DEPT").equals("")) {
            messageBox("����д�ϱ�����");
            return;
        }
//        if (parm.getValue("REPORT_STATION").equals("")) {
//            String sql =
//                    "SELECT * FROM SYS_STATION WHERE DEPT_CODE = '" + parm.getValue("REPORT_DEPT")
//                            + "'";
//            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//            if (result.getErrCode() != 0) {
//                this.messageBox("��鲡������ʧ��");
//                return;
//            }
//            if (result.getCount() > 0) {
//                messageBox("����д����");
//                return;
//            }
//        }
        if (parm.getValue("REPORT_USER").equals("")) {
            messageBox("����д�ϱ���");
            return;
        }
        if (parm.getTimestamp("REPORT_DATE").getTime() < parm.getTimestamp("EVENT_DATE").getTime()) {
            messageBox("�ϱ����ڲ������ڷ�������");
            return;
        }
        if (parm.getValue("REPORT_SECTION").equals("")) {
            messageBox("����д���Ͳ���");
            return;
        }
//        if (parm.getValue("PAT_NAME").equals("")) {
//            messageBox("����д��������");
//            return;
//        }
//        if (parm.getValue("SEX_CODE").equals("")) {
//            messageBox("����д�Ա�");
//            return;
//        }
//        if (parm.getValue("AGE").equals("")) {
//            messageBox("����д����");
//            return;
//        }
        // modify by wangb 2017/12/18
        // ��Ҫ�����ˡ����ο��ҡ���ϡ������¼������������¼������������������ȡ����ʾ
        /*if (parm.getValue("EVENT_DESC").equals("")) {
            messageBox("����д�����¼�����");
            return;
        }
        if (parm.getValue("EVENT_RESULT").equals("")) {
            messageBox("����д�����¼��������");
            return;
        }*/
        parm.setData("ACI_NO", aciNo);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = ACIBadEventTool.getInstance().updateBadEventData(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        parm.setData("EXAMINE_FLG", data.getData("EXAMINE_FLG", selRow));
        data.setRowData(selRow, parm);
        callFunction("UI|TABLE|setParmValue", data);
        callFunction("UI|TABLE|setSelectedRow", selRow);
        this.messageBox("���³ɹ�");
    }
    
    /**
     * ����
     */
    public void onNew() {
        clearValue("EVENT_TYPE;EVENT_DATE;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_USER;REPORT_DATE;REPORT_SECTION;"
                + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;"
                + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                + "EVENT_DESC;EVENT_RESULT;"
                + "SMS_FLG;SMS_USERS;SMS_TEXT;"
                + "EVENT_CLASS;SAC_CLASS");
        TTable table = (TTable) this.getComponent("TABLE");
        table.clearSelection();
        if (this.getPopedem("SYSOPERATOR")) {// add by wanglong 20130428
            clearValue("ASSESS_USER;ASSESS_DATE");
        } 

        if (this.getPopedem("SYSDBA")) {// add by wanglong 20131101
            clearValue("EXAMINE_USER;EXAMINE_DATE");
        }
        initUI();
    }
    
    /**
     * ɾ��
     */
    public void onDelete() {
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            messageBox("��ѡ��һ����¼");
            return;
        }
        TParm data = table.getParmValue();
        if (this.getPopedem("NORMAL")) {
            if (!data.getValue("REPORT_USER", selRow).equals(Operator.getID())) {
                this.messageBox("ֻ���ϱ�����ɾ�����¼�");
                return;
            }
        }
        boolean examineFlg = data.getBoolean("EXAMINE_FLG", selRow);
        if (examineFlg == true) {
            messageBox("����ɾ���������˵ļ�¼������ȡ�����");
            return;
        }
        if (!this.getPopedem("SYSOPERATOR")) {// add by wanglong 20130503
            if (!Operator.getID().equals(data.getValue("REPORT_USER", selRow))) {
                this.messageBox("�������Ȩ�޻����ϱ����Լ�����ɾ����¼");
                return;
            }
        }
        if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 2) == 0) {
            // TParm data = table.getParmValue();
            String aciNo = data.getValue("ACI_NO", selRow);
            TParm result = ACIBadEventTool.getInstance().deleteBadEventData(aciNo);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            this.callFunction("UI|TABLE|removeRow", selRow);
            clearValue("EVENT_TYPE;EVENT_CLASS;EVENT_DATE;"
                    + "REPORT_DEPT;REPORT_STATION;REPORT_USER;REPORT_DATE;REPORT_SECTION;"
                    + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;"
                    + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                    + "EVENT_DESC;EVENT_RESULT;"
                    + "EXAMINE_USER;SAC_CLASS;EXAMINE_DATE;");
            table.clearSelection();
            insertMenu.setVisible(true);
            updateMenu.setVisible(false);
//            examineMenu.setVisible(true);
//            unExamineMenu.setVisible(false);
            this.messageBox("P0003");// ɾ���ɹ�
        }
    }
    
    /**
     * ���
     */
    public void onExamine() {
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            messageBox("��ѡ��һ����¼");
            return;
        }
        TParm data = table.getParmValue();
        String aciNo = data.getValue("ACI_NO", selRow);
        boolean examineFlg = (Boolean) data.getData("EXAMINE_FLG", selRow);
        if (examineFlg == true) {
            messageBox("�벻Ҫ������");
            return;
        }
        TParm parm = getParmForTag("EXAMINE_USER;EVENT_CLASS;EXAMINE_DATE:Timestamp");
        if (parm.getValue("EXAMINE_USER").equals("")) {
            messageBox("����д�����");
            return;
        }
        if (parm.getValue("EVENT_CLASS").equals("")) {
            messageBox("����д�쳣�¼��ȼ�");
            return;
        }
        if (this.getValue("EXAMINE_DATE") == null) {// modify by wanglong 20130516
            messageBox("����д�������");
            return;
        }
        if (parm.getTimestamp("EXAMINE_DATE").getTime() < data.getTimestamp("REPORT_DATE", selRow).getTime()) {
            messageBox("������ڲ��������ϱ�����");
            return;
        }
        parm.setData("ACI_NO", aciNo);
        TParm result = ACIBadEventTool.getInstance().examineBadEventData(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
//        data.setData("EXAMINE_FLG", selRow, true);
//        data.setData("EXAMINE_USER", selRow, parm.getData("EXAMINE_USER"));
//        data.setData("EVENT_CLASS", selRow, parm.getData("EVENT_CLASS"));
//        data.setData("EXAMINE_DATE", selRow, parm.getData("EXAMINE_DATE"));
        data.removeRow(selRow);
        callFunction("UI|TABLE|setParmValue", data);
        callFunction("UI|TABLE|setSelectedRow", selRow);
//        examineMenu.setVisible(false);
//        unExamineMenu.setVisible(true);
        clearValue("EVENT_TYPE;EVENT_DATE;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_USER;REPORT_DATE;REPORT_SECTION;"
                + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;"
                + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                + "EVENT_DESC;EVENT_RESULT;"
                + "EVENT_CLASS");
        this.setValue("SMS_FLG", "N");
        this.callFunction("UI|SMS_FLG|setEnabled", false);
        onEnableSMS();
        this.messageBox("P0001");// ����ɹ�
    }

    /**
     * ȡ�����
     */
    public void onUnExamine() {
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            messageBox("��ѡ��һ����¼");
            return;
        }
        TParm data = table.getParmValue();
        String aciNo = data.getValue("ACI_NO", selRow);
        boolean examineFlg = data.getBoolean("EXAMINE_FLG", selRow);
        if (examineFlg == false) {
            messageBox("δ��˹�������ȡ�����");
            return;
        }
        TParm result = ACIBadEventTool.getInstance().unExamineBadEventData(aciNo);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
//        data.setData("EXAMINE_FLG", selRow, false);
//        data.setData("EXAMINE_USER", selRow, null);
//        data.setData("SAC_CLASS", selRow, null);
//        data.setData("EXAMINE_DATE", selRow, null);
        data.removeRow(selRow);
        callFunction("UI|TABLE|setParmValue", data);
        //callFunction("UI|TABLE|setSelectedRow", selRow-1);
        clearValue("EVENT_TYPE;EVENT_DATE;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_USER;REPORT_DATE;REPORT_SECTION;"
                + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;"
                + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                + "EVENT_DESC;EVENT_RESULT;"
                + "EXAMINE_USER;EVENT_CLASS;EXAMINE_DATE");
        this.setValue("SMS_FLG", "N");
        this.callFunction("UI|SMS_FLG|setEnabled", false);
        onEnableSMS();
        this.messageBox("P0001");// ����ɹ�
    }
    
    /**
     * ����
     */
    public void onRate(){
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            messageBox("��ѡ��һ����¼");
            return;
        }
        TParm data = table.getParmValue();
        if(data.getBoolean("EXAMINE_FLG",selRow)==false){
            this.messageBox("����ִ�����");
            return;
        }
        String aciNo = data.getValue("ACI_NO", selRow);
        TParm parm = getParmForTag("ASSESS_USER;SAC_CLASS;ASSESS_DATE:Timestamp");
        if (parm.getValue("ASSESS_USER").equals("")) {
            messageBox("����д������");
            return;
        }
        if (parm.getValue("SAC_CLASS").equals("")) {
            messageBox("����дSAC�ȼ�");
            return;
        }
        if (this.getValue("ASSESS_DATE") == null) {// modify by wanglong 20130516
            messageBox("����д��������");
            return;
        }
        if (parm.getTimestamp("ASSESS_DATE").getTime() < data.getTimestamp("EXAMINE_DATE", selRow).getTime()) {
            messageBox("�������ڲ��������������");
            return;
        }
        parm.setData("ACI_NO", aciNo);
        TParm result = ACIBadEventTool.getInstance().rateBadEventData(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        data.setData("SAC_CLASS", selRow, parm.getData("SAC_CLASS"));
        data.setData("ASSESS_USER", selRow, parm.getData("ASSESS_USER"));
        data.setData("ASSESS_DATE", selRow, parm.getData("ASSESS_DATE"));
        callFunction("UI|TABLE|setParmValue", data);
        callFunction("UI|TABLE|setSelectedRow", selRow);
        this.messageBox("P0001");// ����ɹ�
    }

    /**
     * �����Ų�ѯ
     */
    public void onMrNo() {
        String mrNo = this.getValueString("MR_NO");
        TParm parm = new TParm();
        if (mrNo.equals("")) {
            return;
        } else {
            mrNo = PatTool.getInstance().checkMrno(mrNo);
            parm = PatTool.getInstance().getInfoForMrno(mrNo);
        }
        if (parm.getCount() > 0) {
            this.setValue("MR_NO", mrNo);// ������
            this.setValue("PAT_NAME", parm.getValue("PAT_NAME", 0));// ����
            this.setValue("SEX_CODE", parm.getValue("SEX_CODE", 0));// �Ա�
            Timestamp birthday = parm.getTimestamp("BIRTH_DATE", 0);
            if (birthday != null) {
                String age = StringTool.CountAgeByTimestamp(birthday,
                        SystemTool.getInstance().getDate())[0];// ����
                this.setValue("AGE", age);
            }
            String diagSql =
                    "SELECT CASE_NO, MIN(SEQ_NO), ICD_CODE, ICD_DESC FROM MRO_RECORD_DIAG "
                            + " WHERE MAIN_FLG = 'Y' AND MR_NO = '#' "
                            + " GROUP BY CASE_NO, ICD_CODE, ICD_DESC "
                            + " ORDER BY CASE_NO DESC, MIN(SEQ_NO)";// add by wanglong 20140107
            diagSql = diagSql.replaceFirst("#", mrNo);
            TParm diagParm = new TParm(TJDODBTool.getInstance().select(diagSql));
            if (diagParm.getCount() >= 1) {
                this.setValue("DIAG_CODE", diagParm.getValue("ICD_CODE", 0));
                this.setValue("DIAG_DESC", diagParm.getValue("ICD_DESC", 0));
            }
        }
    }

    /**
     * �����¼�����
     */
    public void onCopyEventDesc(){
        String smsText = "";
        if (!this.getValue("EVENT_TYPE").equals("")) {
            smsText += "�����¼����ͣ�" + this.getText("EVENT_TYPE") + "\r\n";
        }
        if (!this.getValue("MR_NO").equals("")) {
            smsText += "������" + this.getValueString("MR_NO");
            if (!this.getValue("PAT_NAME").equals("")) {
                if (smsText.length() > 0) {
                    smsText += "��" + this.getValueString("PAT_NAME");
                } else {
                    smsText += this.getValueString("PAT_NAME");
                }
            }
            if (!this.getValue("SEX_CODE").equals("")) {
                if (smsText.length() > 0) {
                    smsText += "��" + this.getText("SEX_CODE");
                } else {
                    smsText += this.getText("SEX_CODE");
                }
            }
            if (!this.getValue("AGE").equals("")) {
                if (smsText.length() > 0) {
                    smsText += "��" + this.getValueString("AGE") + "��";
                } else {
                    smsText += this.getValueString("AGE") + "��";
                }
            }
            if (!this.getValue("DIAG_CODE").equals("")) {
                if (smsText.length() > 0) {
                    smsText += "��" + this.getValueString("DIAG_DESC");
                } else {
                    smsText += this.getValueString("DIAG_DESC");
                }
            }
            if (smsText.length() > 0) {
                smsText += "\r\n";
            }
        }
        smsText += this.getValue("EVENT_DESC") + "\r\n";
        if (!this.getValue("EVENT_RESULT").equals("")) {
            smsText += "�����" + this.getValue("EVENT_RESULT");
        }
        this.setValue("SMS_TEXT", smsText);
        if (smsText.length() > 166) {
            this.messageBox("������������");
            return;
        }
    }
    
    /**
     * ���Ͷ���
     */
    public void onSendSMS() {//add by wanglong 20131101
        TTable table = (TTable) this.getComponent("TABLE");
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("����ѡ��ĳ���¼�");
            return;
        }
        if (this.getValueString("SMS_USERS").equals("")) {
            this.messageBox("��ѡ����ϵ��");
            return;
        }
        if (this.getValueString("SMS_TEXT").length() > 166) {
            this.messageBox("������������");
            return;
        }
        usersParm.setData("SMS_TEXT", this.getValueString("SMS_TEXT"));
        usersParm.setData("ACI_NO", table.getParmValue().getValue("ACI_NO", row));
        ACIBadEventTool.sendSms(usersParm);
    }
    
    /**
     * ���Excel
     */
    public void onExport() {
        if (((Integer) this.callFunction("UI|TABLE|getRowCount")) <= 0) {
            this.messageBox("û�л�����ݣ����Ȳ�ѯ");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel((TTable) this.getComponent("TABLE"), "�����¼�����");
    }

    /**
     * ѡ�����/δ��ˡ�RadioButton
     */
    public void onExamineRdBtn() {
        if (((TRadioButton) this.getComponent("UNEXAMINED")).isSelected()) {
            if (this.getPopedem("SYSOPERATOR")) {
                examineMenu.setVisible(true);
                unExamineMenu.setVisible(false);
            }
            if (this.getPopedem("SYSDBA")) {
                this.callFunction("UI|ASSESS_USER|setEnabled", false);
                this.callFunction("UI|SAC_CLASS|setEnabled", false);
                this.callFunction("UI|ASSESS_DATE|setEnabled", false);
//                smsMenu.setVisible(false);
            }
        } else {
            if (this.getPopedem("SYSOPERATOR")) {
                examineMenu.setVisible(false);
                unExamineMenu.setVisible(true);
            }
            if (this.getPopedem("SYSDBA")) {
                this.callFunction("UI|ASSESS_USER|setEnabled", true);
                this.callFunction("UI|SAC_CLASS|setEnabled", true);
                this.callFunction("UI|ASSESS_DATE|setEnabled", true);
//                smsMenu.setVisible(true);
            }
        }
        clearValue("EVENT_TYPE;EVENT_DATE;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_USER;REPORT_DATE;REPORT_SECTION;"
                + "MR_NO;PAT_NAME;SEX_CODE;AGE;"
                + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                + "EVENT_DESC;EVENT_RESULT;"
                + "SMS_FLG;SMS_USERS;SMS_TEXT;"
                + "EVENT_CLASS;SAC_CLASS");
        if (this.getPopedem("SYSOPERATOR")) {// add by wanglong 20130428
            clearValue("ASSESS_USER;ASSESS_DATE");
        } 

        if (this.getPopedem("SYSDBA")) {// add by wanglong 20131101
            clearValue("EXAMINE_USER;EXAMINE_DATE");
        }
        callFunction("UI|TABLE|setParmValue", new TParm());
    }
    
    /**
     * ����/���ö���
     */
    public void onEnableSMS() {//add by wanglong 20131101
        if (this.getValueString("SMS_FLG").equals("Y")) {
            callFunction("UI|WITH_DESC|setEnabled", true);
            callFunction("UI|CHOOSE_USER|setEnabled", true);
            ((TTextArea) this.getComponent("SMS_TEXT")).getTextArea().setEnabled(true);
            smsMenu.setVisible(true);
        } else {
            callFunction("UI|WITH_DESC|setEnabled", false);
            callFunction("UI|CHOOSE_USER|setEnabled", false);
            ((TTextArea) this.getComponent("SMS_TEXT")).getTextArea().setEnabled(false);
            smsMenu.setVisible(false);
        }
    }

    /**
     * ���
     */
    public void onClear() {
        clearValue("QUERY_REPORT_DEPT;QUERY_DEPT_IN_CHARGE;QUERY_EVENT_TYPE;QUERY_EVENT_CLASS;QUERY_SAC_CLASS;COUNT;"
                + "EVENT_TYPE;EVENT_DATE;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_USER;REPORT_DATE;REPORT_SECTION;"
                + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;"
                + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                + "EVENT_DESC;EVENT_RESULT;"
                + "SMS_FLG;SMS_USERS;SMS_TEXT;"
                + "EXAMINE_USER;EVENT_CLASS;EXAMINE_DATE;"
                + "ASSESS_USER;SAC_CLASS;ASSESS_DATE");
        usersParm = new TParm();
        callFunction("UI|TABLE|setParmValue", new TParm());
        initUI();
    }
    
    /**
     * ��������򷵻�ֵ
     * 
     * @param tag
     * @param obj
     */
    public void popICDReturn(String tag, Object obj) {// add by wanglong 20140107
        TParm parm = (TParm) obj;
        this.setValue("DIAG_CODE", parm.getValue("ICD_CODE"));
        this.setValue("DIAG_DESC", parm.getValue("ICD_CHN_DESC"));
    }
    
    /**
     * ѡ����ϵ��
     */
    public void onChooseUser() {// add by wanglong 20140107
        TParm result =
                (TParm) this.openDialog("%ROOT%\\config\\aci\\ACIPackageChoose.x", new TParm());
        if (result != null && result.getCount() > 0) {
            usersParm = result;
            String nameList = "";
            for (int i = 0; i < usersParm.getCount(); i++) {
                nameList += usersParm.getValue("USER_NAME", i)+";";
            }
            nameList = nameList.substring(0, nameList.length() - 1);
            this.setValue("SMS_USERS", nameList);
        }
    }
    
    /**
     * ����EMRģ��༭��
     */
    public void onEditEMR() {//add by wanglong 20140220
        TTable table = (TTable) this.getComponent("TABLE");
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ��һ������");
            return;
        }
        String aciNo = table.getParmValue().getValue("ACI_NO", row);
        TParm actionParm = new TParm();
        actionParm.setData("SYSTEM_TYPE", "ACI");
        actionParm.setData("MR_NO", this.getValueString("MR_NO"));// ������
        actionParm.setData("CASE_NO", aciNo);// �����(��ʱΪ�����¼����)
        TParm data = new TParm();
        data.setData("HOSP_DESC", Operator.getHospitalCHNFullName());// ҽԺ����
        Calendar cal = Calendar.getInstance();
        cal.setTime((Timestamp) this.getValue("EVENT_DATE"));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        data.setData("EVENT_DATE", year + "��" + month + "��" + day + "��" + hour + "ʱ" + minute + "��");// ����ʱ��
        data.setData("EVENT_DATE_YEAR", year + "");
        data.setData("EVENT_DATE_MONTH", month + "");
        data.setData("EVENT_DATE_DAY", day + "");
        cal.setTime((Timestamp) this.getValue("REPORT_DATE"));
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DATE);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        data.setData("REPORT_DATE", year + "��" + month + "��" + day + "��");// �ϱ�ʱ��
        data.setData("REPORT_DATE_YEAR", year + "");
        data.setData("REPORT_DATE_MONTH", month + "");
        data.setData("REPORT_DATE_DAY", day + "");
        data.setData("REPORT_DEPT", this.getText("REPORT_DEPT"));// �������
        data.setData("MR_NO", this.getValueString("MR_NO"));// ������
        data.setData("CASE_NO", "");// �����
        data.setData("IPD_NO", "");// סԺ��
        data.setData("PAT_NAME", this.getValue("PAT_NAME"));// ����
        data.setData(this.getText("SEX_CODE"), true);// �Ա�
        data.setData("AGE", this.getText("AGE")+"��");// ����
        data.setData("EVENT_DESC", this.getValue("EVENT_DESC"));// �¼�����
        data.setData("OPT_USER", Operator.getID());
        data.setData("OPT_DATE", SystemTool.getInstance().getDate());
        data.setData("OPT_TERM", Operator.getIP());
        actionParm.setData("ACI_DATA", data.getData());
        actionParm.setData("RULETYPE", "2");// Ȩ�޿��ƣ�Ȩ�ޣ�1,ֻ�� 2,��д 3,���ֶ�д��
        actionParm.setData("TYPE", "F");// ��ΪF����ʾ�ò���ÿ�ξ���Ĳ�����ΪF��ֻ��ʾ���ξ���Ĳ���
        
        String newSql =
                "SELECT A.* FROM EMR_TEMPLET A, ACI_EVENTTYPE B "
                        + " WHERE A.SUBCLASS_CODE = B.MR_CODE AND B.TYPE_CODE = '#'";
        newSql = newSql.replaceFirst("#", this.getValueString("EVENT_TYPE"));
        TParm result1 = new TParm(TJDODBTool.getInstance().select(newSql));
        if (result1.getErrCode() < 0) {
            this.messageBox(result1.getErrText());
            return;
        } 
        if (result1.getCount() < 1) {
            this.messageBox("û������ģ�壬�޷���д����");
            return;
        }
        String oldSql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO='#' AND SUBCLASS_CODE='#'";
        oldSql = oldSql.replaceFirst("#", aciNo);
        oldSql = oldSql.replaceFirst("#", result1.getValue("SUBCLASS_CODE", 0));
        TParm result = new TParm(TJDODBTool.getInstance().select(oldSql));
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount() < 1) {// �½�
            TParm emrFileData = new TParm();
            emrFileData.setData("TEMPLET_PATH", result1.getValue("TEMPLET_PATH", 0));
            emrFileData.setData("EMT_FILENAME", result1.getValue("SUBCLASS_DESC", 0));
            emrFileData.setData("SUBCLASS_CODE", result1.getValue("SUBCLASS_CODE", 0));
            emrFileData.setData("CLASS_CODE", result1.getValue("CLASS_CODE", 0));
            actionParm.setData("EMR_FILE_DATA", emrFileData);
            actionParm.setData("STYLETYPE", "1");// �Ƿ���ʾ������
        } else {// ���Ѵ��ڵ�
//            TParm emrFileData = new TParm();
//            emrFileData.setData("FILE_PATH", result.getValue("FILE_PATH", 0));
//            emrFileData.setData("FILE_NAME", result.getValue("FILE_NAME", 0));
//            emrFileData.setData("FILE_SEQ", result.getValue("FILE_SEQ", 0));
//            emrFileData.setData("SUBCLASS_CODE", result.getValue("SUBCLASS_CODE", 0));
//            emrFileData.setData("CLASS_CODE", result.getValue("CLASS_CODE", 0));
//            emrFileData.setData("FLG", true);
//            actionParm.setData("EMR_FILE_DATA", emrFileData);
            actionParm.setData("STYLETYPE", "2");// �Ƿ���ʾ������
        }
        Object obj = this.openDialog("%ROOT%\\config\\emr\\TEmrWordUI.x", actionParm);
    }

}
