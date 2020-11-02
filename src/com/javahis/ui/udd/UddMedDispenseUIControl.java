package com.javahis.ui.udd;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.config.TConfig;
import com.dongyang.util.TypeTool;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.SYSOperatorTool;
import com.dongyang.data.TParm;
import jdo.udd.UddMedDispenseTool;
import com.dongyang.manager.TCM_Transform;
import java.util.ArrayList;
import java.util.List;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TRadioButton;

/**
 * <p>Title: סԺҩ����ҩ��ҩ</p>
 *
 * <p>Description: סԺҩ����ҩ��ҩ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class UddMedDispenseUIControl
    extends TControl {
    //�����ļ� DOSAGE������ҩ���䣻DISPENSE������ҩ��ҩ
    private String controlName = "DOSAGE";
    //�����ļ�:�Ƿ����ù���ҩƷ�ж�
    private boolean isCtrl;
    //ҩ���ۿ�Ʒѵ�
    private String charge;
    //סԺҩ�������Ƿ����������
    private boolean isCheckNeeded;
    //סԺҩ�������Ƿ�����ҩ����
    private boolean isDosage;
    //�����嵥
    private TTable tablePat;
    //ͳҩ��
    private TTable tableSumMed;
    //ҩƷ��ϸ
    private TTable tableMed;
    //ȱҩ��ϸ
    private TTable tablelShtMed;
    //����ҩƷȨ��
    private boolean ctrl_flg = true;

    public UddMedDispenseUIControl() {
    }

    public void onInitParameter() {
        charge = TConfig.getSystemValue("CHARGE_POINT");
        isDosage = TypeTool.getBoolean(TConfig.getSystemValue("IS_DOSAGE"));
        isCheckNeeded = TypeTool.getBoolean(TConfig.getSystemValue("IS_CHECK"));
        isCtrl = TypeTool.getBoolean(TConfig.getSystemValue("IS_CTRL"));
        controlName = getParameter().toString();
        if ("DOSAGE".equalsIgnoreCase(controlName))
            setTitle("����ҩ����");
        else
            setTitle("����ҩ��ҩ");
    }

    public void onInit() {
        tablePat = (TTable) getComponent("TABLE_PAT");
        tableSumMed = (TTable) getComponent("TABLE_SUM_MED");
        tableMed = (TTable) getComponent("TABLE_MED");
        tablelShtMed = (TTable) getComponent("TABLE_SHT_MED");

        Timestamp datetime = TJDODBTool.getInstance().getDBTime();
        setValue("START_DATE", datetime);
        setValue("END_DATE", datetime);

        //�жϵ�¼��Ա�Ƿ��й���ҩƷȨ��
        if (isCtrl) {
            TParm opertor = SYSOperatorTool.getInstance().getOperator(Operator.
                getID(),Operator.getRegion());
            if ("Y".equals(opertor.getValue("CTRL_FLG", 0))) {
                ctrl_flg = true;
            }
            else {
                ctrl_flg = false;
            }
        }

        // �������嵥(TABLE_PAT)�е�CHECKBOX��������¼�
        callFunction("UI|TABLE_PAT|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTablePatCheckBoxClicked");
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        //��ѯ���ݼ��
        if (!checkData()) {
            return;
        }
        //ȡ�ò����嵥��ѯ����
        TParm parm = getQueryPatCondition();
        TParm result = UddMedDispenseTool.getInstance().onQueryPat(parm);
        if (result == null || result.getCount("PAT_NAME") <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        tablePat.setParmValue(result);
    }

    /**
     * ��շ���
     */
    public void onClear() {

    }

    /**
     * ���淽��
     */
    public void onSave() {

    }

    /**
     * ɾ������
     */
    public void onDelete() {

    }

    /**
     * �������嵥(TABLE_PAT)�е�CHECKBOX��������¼�
     *
     * @param obj
     */
    public void onTablePatCheckBoxClicked(Object obj) {
        tablePat.acceptText();
        int column = tablePat.getSelectedColumn();
        if (column == 0) {
            onQueryTableSumMed();
            onQueryTableMed();
        }
    }

    /**
     * ͳҩ����ѯ
     */
    private void onQueryTableSumMed() {
        //ȡ�ò�ѯ����
        TParm parm = getQueryTableMedCondition();
        //��ҩƷ��ʾͳҩ���򰴲�����ʾͳҩ��
        parm.setData("ORDER_BY",
                     this.getRadioButton("ORDER_BY_ORDER").isSelected() ?
                     "ORDER_BY_ORDER" : "ORDER_BY_MRNO");
        TParm result = UddMedDispenseTool.getInstance().onQueryTableSumMed(parm);
        tableSumMed.setParmValue(result);
    }

    /**
     * ҩƷϸ���ѯ
     */
    private void onQueryTableMed() {
        //ȡ�ò�ѯ����
        TParm parm = getQueryTableMedCondition();
        TParm result = UddMedDispenseTool.getInstance().onQueryTableMed(parm);
        tableMed.setParmValue(result);
    }

    /**
     * ȡ�ò�ѯ����
     * @return TParm
     */
    private TParm getQueryTableMedCondition(){
        TParm parm = new TParm();
        String startDate = StringTool.getString(TCM_Transform.getTimestamp(
            getValue("START_DATE")), "yyyyMMddHHmm").substring(0, 8) + "0000";
        String endDate = StringTool.getString(TCM_Transform.getTimestamp(
            getValue("END_DATE")), "yyyyMMddHHmm").substring(0, 8) + "2359";
        //��ʼʱ��
        parm.setData("START_DATE", startDate);
        //����ʱ��
        parm.setData("END_DATE", endDate);
        //��ҩ����
        if (TypeTool.getBoolean(getValue("ST"))) {
            parm.setData("ST", "ST");
        }
        else if (TypeTool.getBoolean(getValue("UD"))) {
            parm.setData("UD", "UD");
        }
        else {
            parm.setData("DS", "DS");
        }
        //���ͷ���
        String getDoseType = "";
        List list = new ArrayList();
        if ("Y".equals(this.getValueString("DOSE_TYPEO"))) {
            list.add("O");
        }
        if ("Y".equals(this.getValueString("DOSE_TYPEE"))) {
            list.add("E");
        }
        if ("Y".equals(this.getValueString("DOSE_TYPEI"))) {
            list.add("I");
        }
        if ("Y".equals(this.getValueString("DOSE_TYPEF"))) {
            list.add("F");
        }

        if (list == null || list.size() == 0) {
            getDoseType = "";
        }
        else {
            for (int i = 0; i < list.size(); i++) {
                getDoseType = getDoseType + "'" + list.get(i) + "' ,";
            }
            getDoseType = getDoseType.substring(0, getDoseType.length() - 1);
        }
        parm.setData("DOSE_TYPE", getDoseType);
        //סԺҩ�������Ƿ����������
        if (isCheckNeeded) {
            parm.setData("CHECK", "Y");
        }
        //����ҩƷȨ��
        if (!ctrl_flg) {
            parm.setData("CTRL_FLG", "Y");
        }
        //ȡ��CASE_NO����
        parm.setData("CASE_NO", getCaseNos());
        //ȡ����ҩ����
        String dispnese_no_list = getPhaDispenseNos();
        if (!"".equals(dispnese_no_list) && !"''".equals(dispnese_no_list) &&
            !"''".equals(dispnese_no_list)) {
            parm.setData("PHA_DISPENSE_NO", dispnese_no_list);
        }

        //���״̬
        if (TypeTool.getBoolean(getValue("UNCHECK"))) {
            //δ���
            if ("DOSAGE".equals(controlName)) {
                //��ҩ
                parm.setData("UNCHECK_DOSAGE", "Y");
            }
            else {
                //��ҩ
                parm.setData("UNCHECK_DISPENSE", "Y");
            }
        }
        else {
            //���
            if ("DOSAGE".equals(controlName)) {
                //��ҩ
                parm.setData("CHECK_DOSAGE", "Y");
            }
            else {
                //��ҩ
                parm.setData("CHECK_DISPENSE", "Y");
            }
        }
        return parm;
    }

    /**
     * ȡ��PAT table��ѡ�е�CASE_NO��Ϊ����SQLƴWHERE��
     * @return
     */
    public String getCaseNos() {
        TParm parm = tablePat.getParmValue();
        StringBuffer caseNos = new StringBuffer();
        if (parm.getCount() < 1)
            return "''";
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            if (StringTool.getBoolean(parm.getValue("EXEC", i)))
                caseNos.append("'").append(parm.getValue("CASE_NO", i)).append(
                    "',");
        }
        if (caseNos.length() < 1) {
            return "''";
        }
        else {
            caseNos.deleteCharAt(caseNos.length() - 1);
            return caseNos.toString();
        }
    }

    /**
     * ȡ����ҩ����
     * @return String
     */
    private String getPhaDispenseNos() {
        TParm parm = tablePat.getParmValue();
        StringBuffer phaDispenseNo = new StringBuffer();
        if (parm.getCount() < 1)
            return "";
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            if (StringTool.getBoolean(parm.getValue("EXEC", i)) &&
                !"".equals(parm.getValue("PHA_DISPENSE_NO", i)))
                phaDispenseNo.append("'").append(parm.getValue(
                    "PHA_DISPENSE_NO", i)).append("',");
        }
        if (phaDispenseNo.length() < 1) {
            return "";
        }
        else {
            phaDispenseNo.deleteCharAt(phaDispenseNo.length() - 1);
            return phaDispenseNo.toString();
        }
    }

    /**
     * ��ѯ���ݼ��
     * @return boolean
     */
    private boolean checkData() {
        if ("".equals(this.getValueString("EXEC_DEPT_CODE"))) {
            this.messageBox("��ѡ��ִ�п���");
            return false;
        }
        return true;
    }

    /**
     * ȡ�ò����嵥��ѯ����
     * @return TParm
     */
    private TParm getQueryPatCondition() {
        TParm parm = new TParm();
        String startDate = StringTool.getString(TCM_Transform.getTimestamp(
            getValue("START_DATE")), "yyyyMMddHHmm").substring(0, 8) + "0000";
        String endDate = StringTool.getString(TCM_Transform.getTimestamp(
            getValue("END_DATE")), "yyyyMMddHHmm").substring(0, 8) + "2359";
        //��ʼʱ��
        parm.setData("START_DATE", startDate);
        //����ʱ��
        parm.setData("END_DATE", endDate);
        //ִ�п���
        parm.setData("EXEC_DEPT_CODE", this.getValueString("EXEC_DEPT_CODE"));
        //�������
        if (!"".equals(this.getValueString("AGENCY_ORG_CODE"))) {
            parm.setData("AGENCY_ORG_CODE",
                         this.getValueString("AGENCY_ORG_CODE"));
        }
        //��ҩ����
        if (TypeTool.getBoolean(getValue("ST"))) {
            parm.setData("ST", "ST");
        }
        else if (TypeTool.getBoolean(getValue("UD"))) {
            parm.setData("UD", "UD");
        }
        else {
            parm.setData("DS", "DS");
        }
        //����
        if (TypeTool.getBoolean(getValue("STATION")) &&
            !"".equals(this.getValueString("STATION_CODE"))) {
            parm.setData("STATION_CODE", getValueString("STATION_CODE"));
        }
        //������
        if (TypeTool.getBoolean(getValue("MR")) &&
            !"".equals(this.getValueString("MR_NO"))) {
            parm.setData("MR_NO", getValueString("MR_NO"));
        }
        //��λ
        if (TypeTool.getBoolean(getValue("BED")) &&
            !"".equals(this.getValueString("BED_NO"))) {
            parm.setData("BED_NO", getValueString("BED_NO"));
        }
        //��ҩ����
        if (!"".equals(this.getValueString("PHA_DISPENSE_NO"))) {
            parm.setData("PHA_DISPENSE_NO", getValueString("PHA_DISPENSE_NO"));
        }
        //סԺҩ�������Ƿ����������
        if (isCheckNeeded) {
            parm.setData("CHECK", "Y");
        }
        //���״̬
        if (TypeTool.getBoolean(getValue("UNCHECK"))) {
            //δ���
            if ("DOSAGE".equals(controlName)) {
                //��ҩ
                parm.setData("UNCHECK_DOSAGE", "Y");
            }
            else {
                //��ҩ
                parm.setData("UNCHECK_DISPENSE", "Y");
            }
        }
        else {
            //���
            if ("DOSAGE".equals(controlName)) {
                //��ҩ
                parm.setData("CHECK_DOSAGE", "Y");
            }
            else {
                //��ҩ
                parm.setData("CHECK_DISPENSE", "Y");
            }
        }
        return parm;
    }

    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

}
