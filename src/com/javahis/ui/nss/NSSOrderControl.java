package com.javahis.ui.nss;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import jdo.adm.ADMInpTool;
import jdo.sys.Pat;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import jdo.nss.NSSOrderTool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title: ��ʳ����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSOrderControl
    extends TControl {
    public NSSOrderControl() {
        super();
    }

    private TTable table;

    private String case_no = "";

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��TABLEDEPT�е�CHECKBOX��������¼�
        callFunction("UI|TABLE_ORDER|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        // Ĭ�϶���һ��
        Timestamp now = SystemTool.getInstance().getDate();
        int week = StringTool.getWeek(now);
        this.setValue("START_DATE", now);
        this.setValue("END_DATE", StringTool.rollDate(now, 7 - week));
        table = getTable("TABLE_ORDER");
        ( (TMenuItem) getComponent("delete")).setEnabled(false);

        Object obj = this.getParameter();
        if (obj != null) {
            String mr_no = (String) ( (TParm) obj).getData("NSS", "MR_NO");
            this.setValue("MR_NO", mr_no);
            onMrNoAction();
        }
        onQuery();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        if (case_no.length() <= 0 && getRadioButton("RadioButton_2").isSelected()) {
            this.messageBox("��ѡ�񶩲Ͳ���");
            return;
        }
        TParm parm = new TParm();
        String diet_type = this.getValueString("DIET_TYPE");
        if (diet_type != null && diet_type.length() > 0) {
            parm.setData("DIET_TYPE", diet_type);
        }
        String pack_Code = this.getValueString("PACK_CODE");
        if (pack_Code != null && pack_Code.length() > 0) {
            parm.setData("PACK_CODE", pack_Code);
        }

        TParm result = new TParm();
        if (getRadioButton("RadioButton_1").isSelected()) {
            int start_date = StringTool.getWeek( (Timestamp)this.getValue(
                "START_DATE"));
            int end_date = StringTool.getWeek( (Timestamp)this.getValue(
                "END_DATE"));
            parm.setData("START_DATE", "0" + start_date);
            if (end_date == 0) {
                end_date = 7;
            }
            parm.setData("END_DATE", "0" + end_date);
            result = NSSOrderTool.getInstance().getNSSWeeklyOrder(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("û�в�ѯ��Ϣ");
            }
            // �������ڼ�ȷ������
            int weekly_code = 0;
            Timestamp now = SystemTool.getInstance().getDate();
            int week = StringTool.getWeek(now);
            for (int i = 0; i < result.getCount(); i++) {
                weekly_code = result.getInt("WEEKLY_CODE", i);
                if (weekly_code == 0) {
                    weekly_code = 7;
                }
                result.setData("DIET_DATE", i,
                               StringTool.rollDate(now, weekly_code - week));
            }
        }
        else {
            parm.setData("START_DATE", this.getValue("START_DATE"));
            parm.setData("END_DATE", this.getValue("END_DATE"));
            parm.setData("CASE_NO", case_no);
            result = NSSOrderTool.getInstance().getNSSOrder(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("û�в�ѯ��Ϣ");
            }
            // ��������ȷ�����ڼ�
            for (int i = 0; i < result.getCount(); i++) {
                Timestamp diet_date = StringTool.getTimestamp(result.getValue(
                    "DIET_DATE", i), "yyyyMMdd");
                result.setData("DIET_DATE", i, diet_date);
                int week = StringTool.getWeek(diet_date);
                if (week == 0) {
                    week = 7;
                }
                result.setData("WEEKLY_CODE", i, "0" + week);
            }
        }
        table.setParmValue(result);
    }

    /**
     * ���淽��(����)
     */
    public void onSave() {
        if (!checkData()) {
            return;
        }
        TParm parm = new TParm();
        TParm result = new TParm();
        TParm tableParm = table.getParmValue();
        TParm rowParm = new TParm();
        Timestamp now = SystemTool.getInstance().getDate();
        for (int i = 0; i < tableParm.getCount("SELECT_FLG"); i++) {
            if ("Y".equals(tableParm.getValue("CHECK_FLG", i))) {
                parm.addData("CASE_NO", case_no);
                parm.addData("DIET_DATE", StringTool.getString(tableParm.
                    getTimestamp("DIET_DATE", i), "yyyyMMdd"));
                parm.addData("MEAL_CODE", tableParm.getValue("MEAL_CODE", i));
                parm.addData("MEAL_TIME", tableParm.getValue("MEAL_TIME", i));
                parm.addData("ADD_ON", tableParm.getValue("ADD_ON", i));
                parm.addData("DIET_TYPE", tableParm.getValue("DIET_TYPE", i));
                parm.addData("DIET_KIND", tableParm.getValue("DIET_KIND", i));
                parm.addData("PACK_CODE", tableParm.getValue("PACK_CODE", i));
                parm.addData("PACK_CHN_DESC",
                             tableParm.getValue("PACK_CHN_DESC", i));
                parm.addData("PACK_ENG_DESC",
                             tableParm.getValue("PACK_ENG_DESC", i));
                parm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i));
                parm.addData("PRICE", tableParm.getDouble("PRICE", i));
                parm.addData("BILL_FLG", "N");
                parm.addData("STOP_FLG", "N");
                parm.addData("IPD_NO", this.getValueString("IPD_NO"));
                parm.addData("MR_NO", this.getValueString("MR_NO"));
                parm.addData("OPT_USER", Operator.getID());
                parm.addData("OPT_DATE", now);
                parm.addData("OPT_TERM", Operator.getIP());

                //����Ƿ��Ѿ����˸��ײ�
                rowParm = parm.getRow(parm.getCount("CASE_NO") - 1);
                result = NSSOrderTool.getInstance().queryNSSOrder(rowParm);
                if (result != null && result.getCount() > 0) {
                    this.messageBox("������" + this.getValueString("PAT_NAME") +
                                    "  " + tableParm.getValue("DIET_DATE",i).
                                    substring(0, 10) + "  " +
                                    tableParm.getValue("MEAL_CHN_DESC", i) +
                                    "�Ѷ��ͣ������ظ�");
                    return;
                }
            }
        }
        result = TIOM_AppServer.executeAction(
            "action.nss.NSSOrderAction", "onInsertNSSOrder", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("����ʧ��");
        }
        else {
            this.messageBox("����ɹ�");
            onQuery();
        }
    }

    /**
     * ɾ������(ȡ������)
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ��ȡ�����ײ�");
        }
        TParm parm = table.getParmValue().getRow(row);
        String message = "������" + this.getValueString("PAT_NAME") + "  " +
            parm.getValue("DIET_DATE").substring(0, 10) + "  " +
            parm.getValue("PACK_CHN_DESC");
        // ����ײ��Ƿ����շ�
        if ("Y".equals(parm.getValue("BILL_FLG"))) {
            this.messageBox(message + "���շѣ�����ȡ��");
            return;
        }
//        // ����ײ��Ƿ��ѹ��˲�ʱ��
//        Timestamp now = SystemTool.getInstance().getDate();
//        String stop = StringTool.getString(parm.getTimestamp("DIET_DATE"),
//                                           "yyyyMMdd") +
//            parm.getValue("STOP_ORDER_TIME");
//        Timestamp stop_time = StringTool.getTimestamp(stop, "yyyyMMddHHmm");
//        if (now.compareTo(stop_time) > 0) {
//            this.messageBox(message + "�ѹ��˲�ʱ�䣬����ȡ��");
//            return;
//        }

        // ȡ������
        parm.setData("DIET_DATE",
                     StringTool.getString(parm.getTimestamp("DIET_DATE"),
                                          "yyyyMMdd"));
        TParm result = NSSOrderTool.getInstance().onDeleteNSSOrder(parm);
        if (result.getErrCode() < 0) {
            this.messageBox("ȡ��ʧ��");
        }
        else {
            this.messageBox("ȡ���ɹ�");
            table.removeRow(row);
        }
    }

    /**
     * ��շ���
     */
    public void onClear() {
        this.clearValue("DIET_TYPE;PACK_CODE");
        table.removeRowAll();
        this.getRadioButton("RadioButton_1").setSelected(true);
        onChangeRadionButton();
        //case_no = "";
    }

    /**
     * MR_NO�س��¼�
     */
    public void onMrNoAction() {
        String mr_no = this.getValueString("MR_NO");
        this.setValue("MR_NO", StringTool.fill0(mr_no, PatTool.getInstance().getMrNoLength())); //====chenxi
        TParm parm = new TParm();
        parm.setData("MR_NO", this.getValue("MR_NO"));
        TParm result = ADMInpTool.getInstance().selectInHosp(parm);
        if (result == null || result.getCount("CASE_NO") <= 0) {
            this.messageBox("�ò�����ǰ����Ժ");
        }
        else {
            setPatInfo(result.getRow(0));
        }
    }

    /**
     * IPD_NO�س��¼�
     */
    public void onIpdNoAction() {
        String mr_no = this.getValueString("IPD_NO");
        this.setValue("IPD_NO", StringTool.fill0(mr_no, PatTool.getInstance().getIpdNoLength()));//=====chenxi
        TParm parm = new TParm();
        parm.setData("IPD_NO", this.getValue("IPD_NO"));
        TParm result = ADMInpTool.getInstance().selectInHosp(parm);
        if (result == null || result.getCount("CASE_NO") <= 0) {
            this.messageBox("�ò�����ǰ����Ժ");
        }
        else {
            setPatInfo(result.getRow(0));
        }
    }

    /**
     * ��ѡ��ť�ı��¼�
     */
    public void onChangeRadionButton() {
        if (this.getRadioButton("RadioButton_1").isSelected()) {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
            table.setLockColumns("1,2,3,4,8,9");
        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            table.setLockColumns("all");
        }
        table.removeRowAll();
        onQuery();
    }

    /**
     * ���(TABLE)��ѡ��ı��¼�
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        table.acceptText();
        int row = table.getSelectedRow();
        // ���ѡ�е���
        int column = table.getSelectedColumn();
        if (column == 0) {
            table.setItem(row, "CHECK_FLG",
                          table.getItemString(row, "SELECT_FLG"));
        }
        else if (column == 5) {
            String check_flg = table.getItemString(row, "CHECK_FLG");
            if ("Y".equals(check_flg)) {
                table.setItem(row, "SELECT_FLG", check_flg);
            }
        }
    }

    /**
     * ���������Ϣ
     * @param parm TParm
     */
    private void setPatInfo(TParm parm) {
        this.setValue("MR_NO", parm.getValue("MR_NO"));
        this.setValue("IPD_NO", parm.getValue("IPD_NO"));
        this.setValue("DEPT_CODE", parm.getValue("DEPT_CODE"));
        this.setValue("STATION_CODE", parm.getValue("STATION_CODE"));
        Pat pat = Pat.onQueryByMrNo(parm.getValue("MR_NO"));
        this.setValue("PAT_NAME", pat.getName());
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("AGE",
                      StringUtil.getInstance().showAge(pat.getBirthday(), date));
        this.setValue("SEX", pat.getSexCode());
        case_no = parm.getValue("CASE_NO");
    }

    /**
     * ���ݼ��
     * @return boolean
     */
    private boolean checkData() {
        if (case_no == null || case_no.length() <= 0) {
            this.messageBox("������Ų���Ϊ��");
            return false;
        }
        String mr_no = this.getValueString("MR_NO");
        if (mr_no == null || mr_no.length() <= 0) {
            this.messageBox("�����Ų���Ϊ��");
            return false;
        }
        String ipd_no = this.getValueString("IPD_NO");
        if (ipd_no == null || ipd_no.length() <= 0) {
            this.messageBox("סԺ�Ų���Ϊ��");
            return false;
        }
        TParm parm = table.getParmValue();
        boolean flg = false;
        // ����ײ��Ƿ��ѹ��˲�ʱ��
        Timestamp now = SystemTool.getInstance().getDate();

        for (int i = 0; i < parm.getCount("SELECT_FLG"); i++) {
            if ("Y".equals(parm.getValue("CHECK_FLG", i))) {
                flg = true;
                String stop = StringTool.getString(parm.getTimestamp("DIET_DATE", i),
                                                   "yyyyMMdd") +
                    parm.getValue("STOP_ORDER_TIME", i);
                Timestamp stop_time = StringTool.getTimestamp(stop, "yyyyMMddHHmm");
                if (now.compareTo(stop_time) > 0) {
                    this.messageBox("������" + this.getValueString("PAT_NAME") +
                                    "  " + parm.getValue("DIET_DATE", i).
                                    substring(0, 10) + "  " +
                                    parm.getValue("PACK_CHN_DESC", i)
                                    + "�ѹ�����ʱ�䣬���ɶ���");
                    return false;
                }
            }
        }
        if (flg) {
            return true;
        }
        this.messageBox("����δ����");
        return false;
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
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
