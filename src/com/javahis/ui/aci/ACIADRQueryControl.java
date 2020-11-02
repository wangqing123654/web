package com.javahis.ui.aci;

import java.sql.Timestamp;
import jdo.aci.ACIADRTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;


/**
 * <p> Title: ҩƷ�����¼���ѯ </p>
 *
 * <p> Description: ҩƷ�����¼���ѯ </p>
 *
 * <p> Copyright: Copyright (c) 2013 </p>
 *
 * <p> Company: BlueCore </p>
 *
 * @author  WangLong 2013.09.30
 * @version 1.0
 */
public class ACIADRQueryControl extends TControl {
	
    TTextFormat reportDept;// �ϱ�����
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            TParm parm = (TParm) obj;
            if (parm.getValue("POPEDEM").length() != 0) {
                if ("1".equals(parm.getValue("POPEDEM"))) { // һ��Ȩ��
                    this.setPopedem("NORMAL", true);
                    this.setPopedem("SYSOPERATOR", false);
                }
                if ("2".equals(parm.getValue("POPEDEM"))) { // ��ɫȨ��
                    this.setPopedem("NORMAL", false);
                    this.setPopedem("SYSOPERATOR", true);
                }
            }
        }
        reportDept = (TTextFormat) this.getComponent("REPORT_DEPT");// �ϱ�����
        callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        initUI();
    }

    /**
     * ��ʼ��������Ϣ
     */
    public void initUI() {
        Timestamp oneWeekAgo = StringTool.rollDate(SystemTool.getInstance().getDate(), -7);
        Timestamp today = SystemTool.getInstance().getDate();
        this.setValue("START_DATE", oneWeekAgo);
        this.setValue("END_DATE", today);
        this.setValue("UPLOAD_DATE", today);// �������
        this.clearValue("REPORT_DEPT");// ȥ�����ϱ����һ���Ĭ��ֵ�������¼��������������ʼ��ʱ����ջ��߸���ֵ�����߽�tag����
        this.callFunction("UI|report|setEnabled", false);
        this.callFunction("UI|report|setVisible", false);
        this.callFunction("UI|unReport|setVisible", false);
        this.callFunction("UI|UPLOAD_DATE|setEnabled", false);
        this.callFunction("UI|UPLOAD_FLG|setEnabled", false);
        if (this.getPopedem("NORMAL")) {
            String deptSql =
                    "SELECT A.DEPT_CODE AS ID, A.DEPT_ABS_DESC AS NAME, A.PY1 FROM SYS_DEPT A, SYS_OPERATOR_DEPT B "
                            + " WHERE A.DEPT_CODE = B.DEPT_CODE AND B.USER_ID = '#' AND A.ACTIVE_FLG = 'Y' ORDER BY A.DEPT_CODE, A.SEQ";
            deptSql = deptSql.replaceFirst("#", Operator.getID());
            reportDept.setPopupMenuSQL(deptSql);// ��ͨ�û��޶�ֻ�ܲ�ѯ�Լ����ڿ��ҵļ�¼
            reportDept.setHisOneNullRow(false);// ��ͨ�û����ܽ���ȫ����ѯ
            reportDept.onQuery();
            String deptCode =
                    StringUtil.getDesc("SYS_OPERATOR", "COST_CENTER_CODE",
                                       "USER_ID='" + Operator.getID() + "'");
            reportDept.setValue(deptCode);
        }
        if (this.getPopedem("SYSOPERATOR")) {
            this.callFunction("UI|report|setVisible", true);
            this.callFunction("UI|UPLOAD_FLG|setEnabled", true);
        }
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
        if (this.getValue("UNREPORTED").equals("Y")) {
            parm.setData("UNREPORTED", "Y");// δ�ϱ�
        }
        if (this.getValue("REPORTED").equals("Y")) {
            parm.setData("REPORTED", "Y");// ���ϱ�
        }
        if (!this.getValueString("REPORT_DEPT").equals("")) {
            parm.setData("REPORT_DEPT", this.getValue("REPORT_DEPT"));// �ϱ�����
        }
        if (!this.getValueString("REPORT_TYPE").equals("")) {
            parm.setData("REPORT_TYPE", this.getValue("REPORT_TYPE"));// ��������
        }
        if (!this.getValueString("ADR_ID").equals("")) {
            parm.setData("ADR_ID", this.getValue("ADR_ID"));// �����¼�����
        }
        if (!this.getValueString("PHA_RULE").equals("")) {
            parm.setData("PHA_RULE", this.getValue("PHA_RULE"));// ҩƷ����
        }
        TParm result = ACIADRTool.getInstance().queryADRData(parm);
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
        if (!this.getPopedem("SYSOPERATOR")) {
            if (!Operator.getID().equals(data.getValue("REPORT_USER", selRow))) {
                this.messageBox("��߼�Ȩ�޻����ϱ����Լ�����ɾ����¼");
                return;
            }
        }
        boolean uploadFlg = data.getBoolean("UPLOAD_FLG", selRow);
        if (uploadFlg == true) {
            messageBox("����ɾ��������ϱ��ļ�¼");
            return;
        }
        if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 2) == 0) {
            TParm parm = new TParm();
            parm.setData("ACI_NO", data.getValue("ACI_NO", selRow));
            TParm result =
                    TIOM_AppServer.executeAction("action.aci.ACIADRAction", "onDelete", parm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            this.callFunction("UI|TABLE|removeRow", selRow);
            this.clearValue("REPORT_DEPT;REPORT_TYPE;ADR_ID;PHA_RULE");
            this.setValue("COUNT", StringTool.addString(this.getValueString("COUNT")));
            table.clearSelection();
            this.messageBox("P0003");// ɾ���ɹ�
        }
    }
    
    /**
     * ���ϱ�
     */
    public void onReport(){
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            messageBox("��ѡ��һ����¼");
            return;
        }
        TParm data = table.getParmValue();
        if (!checkUIData(data.getRow(selRow))) {
            return;
        }
        String aciNo = data.getValue("ACI_NO", selRow);
        TParm parm = getParmForTag("UPLOAD_FLG;UPLOAD_DATE:Timestamp");
        if (!parm.getValue("UPLOAD_FLG").equals("Y")) {
            parm.setData("UPLOAD_DATE", "");
        }
        parm.setData("ACI_NO", aciNo);
        TParm result = ACIADRTool.getInstance().updateADRReportState(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        data.setData("UPLOAD_FLG", selRow, parm.getData("UPLOAD_FLG"));
        data.setData("UPLOAD_DATE", selRow, parm.getData("UPLOAD_DATE"));
        callFunction("UI|TABLE|setParmValue", data);
        callFunction("UI|TABLE|setSelectedRow", selRow);
        this.messageBox("P0001");// ����ɹ�
        if (parm.getValue("UPLOAD_FLG").equals("Y")) {
            this.callFunction("UI|report|setVisible", false);
            this.callFunction("UI|unReport|setVisible", true);
            this.callFunction("UI|REPORTED|setSelected", true);
        } else {
            this.callFunction("UI|report|setVisible", true);
            this.callFunction("UI|unReport|setVisible", false);
            this.callFunction("UI|UNREPORTED|setSelected", true);
            this.callFunction("UI|UPLOAD_FLG|setSelected", false);
            onChooseR();
        }
    }

    /**
     * ȡ�����ϱ�
     */
    public void onUnReport(){
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            messageBox("��ѡ��һ����¼");
            return;
        }
        TParm data = table.getParmValue();
        String aciNo = data.getValue("ACI_NO", selRow);
        TParm parm = new TParm();
        parm.setData("ACI_NO", aciNo);
        parm.setData("UPLOAD_FLG", "");
        parm.setData("UPLOAD_DATE", "");
        TParm result = ACIADRTool.getInstance().updateADRReportState(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        data.setData("UPLOAD_FLG", selRow, "");
        data.setData("UPLOAD_DATE", selRow, "");
        callFunction("UI|TABLE|setParmValue", data);
        callFunction("UI|TABLE|setSelectedRow", selRow);
        this.messageBox("P0001");// ����ɹ�
        if (parm.getValue("UPLOAD_FLG").equals("Y")) {
            this.callFunction("UI|report|setVisible", false);
            this.callFunction("UI|unReport|setVisible", true);
            this.callFunction("UI|REPORTED|setSelected", true);
        } else {
            this.callFunction("UI|report|setVisible", true);
            this.callFunction("UI|unReport|setVisible", false);
            this.callFunction("UI|UNREPORTED|setSelected", true);
            this.callFunction("UI|UPLOAD_FLG|setSelected", false);
            onChooseR();
        }
    }
    
    

    /**
     * �������������Ч��
     */
    public boolean checkUIData(TParm parm) {
        if (parm.getValue("MR_NO").equals("")) {
            this.messageBox("��Ϣ��������δ��д������");
            return false;
        }
        if (parm.getValue("PAT_NAME").equals("")) {
            this.messageBox("��Ϣ��������δ��д��������");
            return false;
        }
        if (parm.getValue("SEX_CODE").equals("")) {
            this.messageBox("��Ϣ��������δѡ���Ա�");
            return false;
        }
        if (parm.getValue("SPECIES_CODE").equals("")) {
            this.messageBox("��Ϣ��������δѡ������");
            return false;
        }
        if (parm.getData("BIRTH_DATE") == null
                || parm.getData("BIRTH_DATE").equals(new TNull(Timestamp.class))) {
            this.messageBox("��Ϣ��������δ��д��������");
            return false;
        }
        if (parm.getValue("WEIGHT").equals("")) {
            this.messageBox("��Ϣ��������δ��д����");
            return false;
        }
        if (parm.getValue("TEL").equals("")) {
            this.messageBox("��Ϣ��������δ��д������ϵ��ʽ");
            return false;
        }
        if (parm.getValue("DIAG_CODE1").equals("")) {
            this.messageBox("��Ϣ��������δѡ��ԭ������");
            return false;
        }
        if(parm.getValue("ALLERGY_FLG").equals("Y")&&parm.getValue("ALLERGY_REMARK").equals("")){
            this.messageBox("��Ϣ��������δ��д����ʷ�����Ϣ");
            return false;
        }
        if(parm.getValue("OTHER_FLG").equals("Y")&&parm.getValue("OTHER_REMARK").equals("")){
            this.messageBox("��Ϣ��������δ��д�����������Ϣ");
            return false;
        }
        if (parm.getValue("ADR_ID1").equals("")) {
            this.messageBox("��Ϣ��������δѡ���¼�����");
            return false;
        }
        if (parm.getData("EVENT_DATE") == null
                || parm.getData("EVENT_DATE").equals(new TNull(Timestamp.class))) {
            this.messageBox("��Ϣ��������δ��д�¼���������");
            return false;
        }
        if (parm.getValue("EVENT_DESC").equals("")) {
            this.messageBox("��Ϣ��������δ��д�¼�����");
            return false;
        }
        if (parm.getValue("EVENT_RESULT").equals("3") && parm.getValue("RESULT_REMARK").equals("")) {
            this.messageBox("��Ϣ��������δ��д����֢�ı���");
            return false;
        }
        if (parm.getValue("EVENT_RESULT").equals("4") && parm.getValue("RESULT_REMARK").equals("")) {
            this.messageBox("��Ϣ��������δ��д����");
            return false;
        }
        if (parm.getValue("EVENT_RESULT").equals("4")
                && (parm.getData("DIED_DATE") == null || parm.getData("DIED_DATE")
                        .equals(new TNull(Timestamp.class)))) {
            this.messageBox("��Ϣ��������δ��д����ʱ��");
            return false;
        }
        if (parm.getValue("REPORT_USER").equals("")) {
            this.messageBox("��Ϣ��������δ��д�ϱ�����");
            return false;
        }
        if (parm.getValue("REPORT_OCC").equals("")) {
            this.messageBox("��Ϣ��������δѡ���ϱ���ְҵ");
            return false;
        } else if (parm.getValue("REPORT_OCC").equals("6")) {// ����
            if (parm.getValue("OCC_REMARK").equals("")) {
                this.messageBox("��Ϣ��������δ��д�ϱ���ְҵ��ע");
                return false;
            }
        }
        if (parm.getValue("REPORT_TEL").equals("")) {
            this.messageBox("��Ϣ��������δ��д��������ϵ�绰");
            return false;
        }
        if (parm.getValue("REPORT_EMAIL").equals("")) {
            this.messageBox("��Ϣ��������δ��д��ϵ�˵�������");
            return false;
        }
        if (parm.getValue("REPORT_DEPT").equals("")) {
            this.messageBox("��Ϣ��������δѡ�񱨸����");
            return false;
        }
        // if (parm.getValue("REPORT_STATION").equals("")) {
        // this.messageBox("��ѡ�񱨸没��");
        // return false;
        // }
        if (parm.getValue("REPORT_UNIT").equals("")) {
            this.messageBox("��Ϣ��������δѡ�񱨸浥λ");
            return false;
        }
        if (parm.getValue("UNIT_CONTACTS").equals("")) {
            this.messageBox("��Ϣ��������δѡ����ϵ��");
            return false;
        }
        if (parm.getValue("UNIT_TEL").equals("")) {
            this.messageBox("��Ϣ��������δ��д��ϵ�˵绰");
            return false;
        }
        if (parm.getData("REPORT_DATE") == null
                || parm.getData("REPORT_DATE").equals(new TNull(Timestamp.class))) {
            this.messageBox("��Ϣ��������δ��д��������");
            return false;
        }
        if (parm.getTimestamp("REPORT_DATE").getTime() < parm.getTimestamp("EVENT_DATE").getTime()) {
            this.messageBox("��Ϣ���������������������¼��������ڣ�");
            return false;
        }
        if (parm.getValue("ORDER_DESC").equals("")) {
            this.messageBox("��Ϣ��������δ��д����ҩƷ��Ϣ");
            return false;
        }
        return true;
    }
    
    
    /**
     * ���Excel
     */
    public void onExport() {
        if (((Integer) this.callFunction("UI|TABLE|getRowCount")) <= 0) {
            this.messageBox("û�л������");
            return;
        }
        TParm parmValue = (TParm) this.callFunction("UI|TABLE|getParmValue");
        String aciNoList = "";
        for (int i = 0; i < parmValue.getCount(); i++) {
            aciNoList += "'" + parmValue.getValue("ACI_NO", i) + "',";
        }
        aciNoList = aciNoList.substring(0, aciNoList.length() - 1);
 
        String sql =
                "SELECT "// A.ACI_NO,
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.FIRST_FLG = C.ID AND C.GROUP_ID = 'ADR_FIRSTFLG') AS FIRST_FLG,"
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.REPORT_TYPE = C.ID AND C.GROUP_ID = 'ADR_REPORTTYPE') AS REPORT_TYPE,"
                        + "WM_CONCAT(B.ORDER_DESC) ORDER_LIST, A.MR_NO, A.PAT_NAME,"
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.SEX_CODE = C.ID AND C.GROUP_ID = 'SYS_SEX') AS SEX_CODE,"
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.SPECIES_CODE = C.ID AND C.GROUP_ID = 'SYS_SPECIES') AS SPECIES_CODE,"
                        + "TO_CHAR(A.BIRTH_DATE, 'YYYY-MM-DD') AS BIRTH_DATE, TO_CHAR(A.WEIGHT) || 'kg' AS WEIGHT, A.TEL,"
                        + "('[' || (SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE1 = C.ICD_CODE) || ']' "
                        + "     || CASE WHEN ( SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE2 = C.ICD_CODE) IS NOT NULL "
                        + "             THEN '[' || ( SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE2 = C.ICD_CODE) || ']'"
                        + "             ELSE '' END "
                        + "     || CASE WHEN (SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE3 = C.ICD_CODE) IS NOT NULL"
                        + "             THEN '[' || (SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE3 = C.ICD_CODE) || ']'"
                        + "             ELSE '' END "
                        + "     || CASE WHEN (SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE4 = C.ICD_CODE) IS NOT NULL"
                        + "             THEN '[' || (SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE4 = C.ICD_CODE) || ']'"
                        + "             ELSE '' END"
                        + "     || CASE WHEN (SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE5 = C.ICD_CODE) IS NOT NULL"
                        + "             THEN '[' || (SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE5 = C.ICD_CODE) || ']'"
                        + "             ELSE '' END) AS DIAG_CODE,"
                        + "(CASE WHEN A.PERSON_HISTORY = '1' THEN A.PERSON_REMARK"
                        + "      ELSE (SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.PERSON_HISTORY = C.ID AND C.GROUP_ID = 'ADR_HISTORYSTATE') END)"
                        + "      AS PERSON_HISTORY,"
                        + "(CASE WHEN A.FAMILY_HISTORY = '1' THEN A.FAMILY_REMARK"
                        + "      ELSE (SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.FAMILY_HISTORY = C.ID AND C.GROUP_ID = 'ADR_HISTORYSTATE') END)"
                        + "      AS FAMILY_HISTORY,"
                        + "(CASE WHEN A.SMOKE_FLG = 'Y' THEN '[����ʷ]' ELSE '' END "
                        + "||CASE WHEN A.DRINK_FLG = 'Y' THEN '[����ʷ]' ELSE '' END "
                        + "||CASE WHEN A.PREGNANT_FLG = 'Y' THEN '[������]' ELSE '' END "
                        + "||CASE WHEN A.HEPATOPATHY_FLG = 'Y' THEN '[�β�ʷ]' ELSE '' END "
                        + "||CASE WHEN A.NEPHROPATHY_FLG = 'Y' THEN '[����ʷ]' ELSE '' END "
                        + "||CASE WHEN A.ALLERGY_FLG = 'Y' THEN '[����ʷ��' || A.ALLERGY_REMARK || ']' ELSE '' END "
                        + "||CASE WHEN A.OTHER_FLG = 'Y' THEN '[������' || A.OTHER_REMARK || ']' ELSE '' END)  AS OTHER_CASE,"
                        + " ('[' || A.ADR_DESC1 || ']' "
                        + "      || CASE WHEN (SELECT C.ADR_DESC FROM ACI_ADRNAME C WHERE A.ADR_ID2 = C.ADR_ID) IS NOT NULL"
                        + "              THEN '[' || (SELECT C.ADR_DESC FROM ACI_ADRNAME C WHERE A.ADR_ID2 = C.ADR_ID) || ']' ELSE '' END "
                        + "      || CASE WHEN (SELECT C.ADR_DESC FROM ACI_ADRNAME C WHERE A.ADR_ID3 = C.ADR_ID) IS NOT NULL"
                        + "              THEN '[' || (SELECT C.ADR_DESC FROM ACI_ADRNAME C WHERE A.ADR_ID3 = C.ADR_ID) || ']' ELSE '' END "
                        + "      || CASE WHEN (SELECT C.ADR_DESC FROM ACI_ADRNAME C WHERE A.ADR_ID4 = C.ADR_ID) IS NOT NULL"
                        + "              THEN '[' || (SELECT C.ADR_DESC FROM ACI_ADRNAME C WHERE A.ADR_ID4 = C.ADR_ID) || ']' ELSE '' END) AS ADR_DESC,"
                        + "TO_CHAR(A.EVENT_DATE, 'YYYY/MM/DD HH24:MI') AS EVENT_DATE, A.EVENT_DESC,"
                        + "(CASE WHEN A.EVENT_RESULT = '3' THEN '�к���֢��' || A.RESULT_REMARK"
                        + "      WHEN A.EVENT_RESULT = '4' THEN '����[ֱ������' || A.RESULT_REMARK || '][����ʱ�䣺' || TO_CHAR(A.DIED_DATE, 'YYYY/MM/DD HH24:MI:SS') || ']'"
                        + "      ELSE (SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.EVENT_RESULT = C.ID AND C.GROUP_ID = 'ADR_RESULT')"
                        + "      END) AS EVENT_RESULT,"
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.STOP_REDUCE = C.ID AND C.GROUP_ID = 'ADR_STOPRESULT') AS STOP_REDUCE,"
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.AGAIN_APPEAR = C.ID AND C.GROUP_ID = 'ADR_AGAINRESULT') AS AGAIN_APPEAR,"
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.DISE_DISTURB = C.ID AND C.GROUP_ID = 'ADR_DISTURBDISE') AS DISE_DISTURB,"
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.USER_REVIEW = C.ID AND C.GROUP_ID = 'ADR_REVIEW') AS USER_REVIEW,"
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.UNIT_REVIEW = C.ID AND C.GROUP_ID = 'ADR_REVIEW') AS UNIT_REVIEW,"
                        + "(SELECT C.USER_NAME FROM SYS_OPERATOR C WHERE A.REPORT_USER = C.USER_ID) AS REPORT_USER, A.REPORT_TEL,"
                        + "(CASE WHEN A.REPORT_OCC = '6' THEN A.OCC_REMARK"
                        + "      ELSE (SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.REPORT_OCC = C.ID AND C.GROUP_ID = 'SYS_POSITION')"
                        + "      END) AS REPORT_OCC, A.REPORT_EMAIL,"
                        + "(SELECT C.REGION_CHN_DESC FROM SYS_REGION C WHERE A.REPORT_UNIT = C.REGION_CODE) AS REPORT_UNIT,"
                        + "(SELECT C.USER_NAME FROM SYS_OPERATOR C WHERE A.UNIT_CONTACTS = C.USER_ID) AS UNIT_CONTACTS,A.UNIT_TEL,"
                        + "TO_CHAR(A.REPORT_DATE, 'YYYY/MM/DD HH24:MI') AS REPORT_DATE "
                        + " FROM ACI_ADRM A, ACI_ADRD B "
                        + "WHERE A.ACI_NO = B.ACI_NO "
                        + "  AND B.TYPE = '0' "
                        + "  AND A.ACI_NO IN (#) "// ///////
                        + "GROUP BY A.ACI_NO, A.FIRST_FLG, A.REPORT_TYPE, A.MR_NO, A.PAT_NAME, A.SEX_CODE, A.SPECIES_CODE, A.BIRTH_DATE,"
                        + "         A.WEIGHT, A.TEL, A.DIAG_CODE1, A.DIAG_CODE2, A.DIAG_CODE3, A.DIAG_CODE4, A.DIAG_CODE5, A.PERSON_HISTORY,"
                        + "         A.PERSON_REMARK, A.FAMILY_HISTORY, A.FAMILY_REMARK, A.SMOKE_FLG, A.DRINK_FLG, A.PREGNANT_FLG,"
                        + "         A.HEPATOPATHY_FLG, A.NEPHROPATHY_FLG, A.ALLERGY_FLG, A.ALLERGY_REMARK, A.OTHER_FLG, A.OTHER_REMARK,"
                        + "         A.ADR_ID1, A.ADR_DESC1, A.ADR_ID2, A.ADR_ID3, A.ADR_ID4, A.EVENT_DATE, A.EVENT_DESC, A.EVENT_RESULT,"
                        + "         A.RESULT_REMARK, A.DIED_DATE, A.STOP_REDUCE, A.AGAIN_APPEAR, A.DISE_DISTURB, A.USER_REVIEW,A.UNIT_REVIEW,"
                        + "         A.REPORT_USER, A.REPORT_OCC, A.OCC_REMARK, A.REPORT_TEL, A.REPORT_EMAIL, A.REPORT_DEPT, A.REPORT_STATION,"
                        + "         A.REPORT_UNIT, A.UNIT_CONTACTS, A.UNIT_TEL, A.REPORT_DATE";
        sql = sql.replaceFirst("#", aciNoList);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        String titleHead =
                "�״�/���ٱ���,100;��������,110;����ҩƷ,200;������,130;����,90;"
                        + "�Ա�,65;����,80;��������,120;����,60;��ϵ��ʽ,125;ԭ������,180;"
                        + "����ҩƷ������Ӧ/�¼�,155;����ҩƷ������Ӧ/�¼�,150;���������Ϣ,250;ҩƷ������Ӧ/�¼�����,180;����ʱ��,170;"
                        + "������Ӧ/�¼�����,250;������Ӧ/�¼����,220;ͣҩ������󣬷�Ӧ�Ƿ���ʧ�����,190;�ٴ�ʹ�ÿ���ҩƷ���Ƿ��ٴγ���ͬ����Ӧ,185;"
                        + "��ԭ��������Ӱ��,145;����������,95;�����������,110;������,90;��ϵ�绰,125;"
                        + "ְҵ,100;�����ʼ�,140;���浥λ����,160;��ϵ��,90;��ϵ��ʽ,125;" + "��������,170";
        String[] parmMap =
                new String[]{"FIRST_FLG", "REPORT_TYPE", "ORDER_LIST", "MR_NO", "PAT_NAME",
                        "SEX_CODE", "SPECIES_CODE", "BIRTH_DATE", "WEIGHT", "TEL", "DIAG_CODE",
                        "PERSON_HISTORY", "FAMILY_HISTORY", "OTHER_CASE", "ADR_DESC", "EVENT_DATE",
                        "EVENT_DESC", "EVENT_RESULT", "STOP_REDUCE", "AGAIN_APPEAR",
                        "DISE_DISTURB", "USER_REVIEW", "UNIT_REVIEW", "REPORT_USER", "REPORT_TEL",
                        "REPORT_OCC", "REPORT_EMAIL", "REPORT_UNIT", "UNIT_CONTACTS", "UNIT_TEL",
                        "REPORT_DATE" };
        for (int i = 0; i < parmMap.length; i++) {
            result.addData("SYSTEM", "COLUMNS", parmMap[i]);
        }
        result.setCount(result.getCount());
        result.setData("TITLE", "ҩƷ������Ӧ��ϸ��");
        result.setData("HEAD", titleHead);
        TParm[] parm = new TParm[]{result };
        ExportExcelUtil.getInstance().exeSaveExcel(parm, "ҩƷ������Ӧ��ϸ��");
    }

    /**
     * ���
     */
    public void onClear() {
        ((TRadioButton) this.getComponent("UNREPORTED")).setSelected(true);
        this.callFunction("UI|UPLOAD_FLG|setSelected", false);
        onChooseR();
        clearValue("REPORT_DEPT;DEPT_IN_CHARGE;REPORT_TYPE;ADR_ID;PHA_RULE;COUNT");
        callFunction("UI|TABLE|setParmValue", new TParm());
        initUI();
    }

	  /**
     * ѡ��δ�ϱ�/���ϱ���RadioButton
     */
    public void onReportBtn() {
        if (((TRadioButton) this.getComponent("UNREPORTED")).isSelected()) {
            if (this.getPopedem("SYSOPERATOR")) {
                this.callFunction("UI|report|setVisible", true);
                this.callFunction("UI|unReport|setVisible", false);
            }
        } else if (((TRadioButton) this.getComponent("REPORTED")).isSelected()) {
            if (this.getPopedem("SYSOPERATOR")) {
                this.callFunction("UI|report|setVisible", false);
                this.callFunction("UI|unReport|setVisible", true);
            }
        }// UNREPORTED
        callFunction("UI|TABLE|setParmValue", new TParm());
    }
	
    /**
     * ѡ�����ϱ���
     */
    public void onChooseR() {
        if ((Boolean) this.callFunction("UI|UPLOAD_FLG|isSelected")) {
            this.callFunction("UI|UPLOAD_DATE|setEnabled", true);
            this.callFunction("UI|report|setEnabled", true);
        } else {
            this.callFunction("UI|UPLOAD_DATE|setEnabled", false);
            this.callFunction("UI|report|setEnabled", false);
        }
    }

	/**
	 * TABLE�����¼�
	 */
    public void onTableClicked(int row) {
        TTable table = (TTable) this.getComponent("TABLE");
        if (row < 0) {
            return;
        }
        TParm parm = table.getParmValue().getRow(row);
        setValueForParm("UPLOAD_FLG;UPLOAD_DATE", parm);
        if (((TRadioButton) this.getComponent("UNREPORTED")).isSelected()) {
  
            setValue("UPLOAD_DATE", SystemTool.getInstance().getDate());
        }
        if (this.getPopedem("SYSOPERATOR")) {
 
            boolean isReported = parm.getBoolean("UPLOAD_FLG");
            if (isReported) {
                this.callFunction("UI|report|setVisible", false);
                this.callFunction("UI|unReport|setVisible", true);
            } else {
                this.callFunction("UI|report|setVisible", true);
                this.callFunction("UI|unReport|setVisible", false);
            }
        }
    }
	
    /**
     * ˫����񣬵��á������¼��Ǽǡ�����鿴�¼���ϸ��¼
     */
    public void onTableDoubleClicked() {
        TTable table = (TTable) this.getComponent("TABLE");
        int row = table.getSelectedRow();
        if (row < 0) {
            messageBox("��ѡ��һ����¼");
            return;
        }
        if (!this.getPopedem("SYSOPERATOR")
                && !table.getParmValue().getValue("REPORT_USER", row).equals(Operator.getID())) {// ֻ������ˣ��ϱ����ܲ鿴�Ǽ���Ϣ
            this.messageBox("��û�и�Ȩ��");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ACI_NO", table.getParmValue().getRow(row).getValue("ACI_NO"));
        parm.setData("ADM_TYPE", table.getParmValue().getRow(row).getValue("ADM_TYPE"));
        parm.setData("CASE_NO", table.getParmValue().getRow(row).getValue("CASE_NO"));
        parm.setData("MR_NO", table.getParmValue().getRow(row).getValue("MR_NO"));
//        parm.setData("ADR_DATA", table.getParmValue().getRow(row));
        parm.addListener("onReturnContent", this, "onReturnContent");
        Object obj = this.openDialog("%ROOT%\\config\\aci\\ACIADRRecord.x", parm);
        if (obj != null) {
            TParm result = (TParm) obj;
            onQuery();
        }
    }
    
    /**
     * �ش�
     * @param obj
     */
    public void onReturnContent(Object obj) {
        if (obj != null) {
            TParm parm = (TParm) obj;
            String aciNo = parm.getValue("ACI_NO");
            this.onQuery();
        }
    }
}
