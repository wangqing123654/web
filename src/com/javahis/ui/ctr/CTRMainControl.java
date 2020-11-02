package com.javahis.ui.ctr;

import com.dongyang.control.*;
import com.dongyang.data.*;
import com.dongyang.jdo.*;
import com.dongyang.manager.*;
import com.dongyang.ui.*;
import com.dongyang.ui.event.*;
import com.javahis.util.*;
import jdo.ctr.*;
import jdo.sys.*;
import com.dongyang.util.StringTool;

/**
 * <p>Title:ҽ��ܿ��趨 </p>
 *
 * <p>Description: ҽ��ܿ��趨</p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company:bluecore </p>
 *
 * @author shibl 2011.11.30
 * @version 1.0
 */
public class CTRMainControl
    extends TControl {

    //����
    private TTable mtable;
    //ϸ��
    private TTable dtable;
    //ϸ�����һ
    private TTextFormat PARAVALUE;
    /**
     * ��ʼ��
     * onInit
     */
    public void onInit() {
        super.init();
        mtable = this.getTable("MTABLE");
        dtable = this.getTable("DTABLE");
        callFunction("UI|ORDER_CODE|setPopupMenuParameter", "aaa",
                     "%ROOT%\\config\\sys\\SYSFeePopup.x");
        //textfield���ܻش�ֵ
        callFunction("UI|ORDER_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn1");
        //textfield���ܻش�ֵ
        callFunction("UI|PARAVALUE_1|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn2");
        //�������¼�ע��
        callFunction("UI| mtable|addEventListener",
                     mtable + "->" + TTableEvent.CLICKED, this,
                     "onMTableClicked");
        //����˫���¼�ע��
        callFunction("UI| mtable|addEventListener",
                     mtable + "->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onMTabledoubleClicked");
        //ϸ�����¼�ע��
        callFunction("UI| dtable|addEventListener",
                     dtable + "->" + TTableEvent.CLICKED, this,
                     "ondtableClicked");
        //ֵ�����¼�ע��
        callFunction("UI|RESTRITEM_CODE|addEventListener",
                     TComboBoxEvent.SELECTED, this,
                     "CodeSelect");
        //��ʼ��TextFormat
        PARAVALUE = getTextFormat("PARAVALUE");
        getcheckdata(05);
        //ҳǩ�ı��¼�
        onChangedPanel();
    }

    /**
     * ҳǩ�ı��¼�<br>
     * onChangedPanel
     */
    public void onChangedPanel() {
        int index = ( (TTabbedPane) getComponent("TabbedPane")).
            getSelectedIndex();
        //�����ʼ��
        if (index == 0) {
            initpage();

        }
        //ϸ���ʼ��
        else if (index == 1) {
            initdetail();
        }
    }

    /**
     * ��ʼ��ҳ��
     */
    public void initpage() {
        setUIMainF();
        onQuery();
    }

    /**
     * �Ի����ʱ�ķ���
     */
    public void onDiagLost() {
        if (this.getValueString("ORDER_CODE").trim().length() <= 0) {
            this.setValue("ORDER_DESC", "");
        }
    }

    /**
     * ҽ����ܷ���ֵ����
     *
     * @param tag String
     * @param obj Object
     */
    public void popReturn1(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code)) {
            getTextField("ORDER_CODE").setValue(order_code);
        }
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc)) {
            getTextField("ORDER_DESC").setValue(order_desc);
        }
    }

    /**
     * ����ֵһ���ܷ���ֵ����
     *
     * @param tag String
     * @param obj Object
     */
    public void popReturn2(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_code)) {
            getTextField("PARAVALUE_1").setValue(order_code);
        }
        if (!StringUtil.isNullString(order_desc)) {
            getTextField("PARAVALUE_1_DESC").setValue(order_desc);
        }
    }

    /**
     * Ϊ�����¼�����ֵһ���ܷ���ֵ����
     *
     * @param value String
     * @return String
     */
    public String meddesc(String value) {
        //����һֵ��Ϊ�ա�
        String testitem_desc = "";
        if (!value.equals("")) {
            TParm medparm = new TParm(this.getDBTool().select(
                "SELECT DISTINCT TESTITEM_CHN_DESC FROM MED_LIS_RPT WHERE TESTITEM_CODE='" +
                value +
                "'"));
            if (medparm == null || medparm.getCount() < 0) {
                return testitem_desc;
            }
            testitem_desc = medparm.getValue("TESTITEM_CHN_DESC", 0);
        }
        else {
            testitem_desc = "";
        }
        return testitem_desc;
    }

    /**
     * Ϊ�����¼�����ֵһ���ܷ���ֵ����
     *
     * @param value String
     * @return String
     */
    public String paravaluedesc(String value) {
        //����һֵ��Ϊ�ա�
        String order_desc = "";
        if (!value.equals("")) {
            TParm feeparm = new TParm(this.getDBTool().select(
                "SELECT ORDER_DESC FROM SYS_FEE  WHERE ORDER_CODE='" + value +
                "'"));
            if (feeparm == null || feeparm.getCount() < 0) {
                return order_desc;
            }
            order_desc = feeparm.getValue("ORDER_DESC", 0);
        }
        else {
            order_desc = "";
        }
        return order_desc;
    }

    /**
     * ��ҳ��ؼ����ɱ༭
     * setUIMainF
     */
    public void setUIMainF() {
        callFunction("UI|save|setEnabled", false);
        callFunction("UI|ORDER_CODE|setEnabled", false);
        callFunction("UI|ORDER_DESC|setEnabled", false);
        callFunction("UI|CONTROL_ID|setEnabled", false);
        callFunction("UI|NHI_FLG|setEnabled", false);
        callFunction("UI|OWN_FLG|setEnabled", false);
        callFunction("UI|FORCE_FLG|setEnabled", false);
        callFunction("UI|MESSAGE_TEXT|setEnabled", false);
        callFunction("UI|MESSAGE_TEXT_E|setEnabled", false);
        callFunction("UI|CTRL_COMMENT|setEnabled", false);
        callFunction("UI|ACTIVE_FLG|setEnabled", false);
        callFunction("UI|SUBCLASS_CODE|setEnabled", false);
    }

    /**
     * ��ҳ��ؼ��ɱ༭
     * setUIMainT
     */

    public void setUIMainT() {
        callFunction("UI|save|setEnabled", true);
        callFunction("UI|new|setEnabled", true);
        callFunction("UI|ORDER_CODE|setEnabled", false);
        callFunction("UI|ORDER_DESC|setEnabled", false);
        callFunction("UI|CONTROL_ID|setEnabled", false);
        callFunction("UI|NHI_FLG|setEnabled", true);
        callFunction("UI|OWN_FLG|setEnabled", true);
        callFunction("UI|FORCE_FLG|setEnabled", true);
        callFunction("UI|MESSAGE_TEXT|setEnabled", true);
        callFunction("UI|MESSAGE_TEXT_E|setEnabled", true);
        callFunction("UI|CTRL_COMMENT|setEnabled", true);
        callFunction("UI|ACTIVE_FLG|setEnabled", true);
        callFunction("UI|SUBCLASS_CODE|setEnabled", true);
    }

    /**
     * ϸ��ҳ��ؼ����ɱ༭
     * setUIDMainF
     */

    public void setUIDMainF() {
        callFunction("UI|save|setEnabled", false);
        callFunction("UI|ORDER_CODE_1|setEnabled", false);
        callFunction("UI|ORDER_DESC_1|setEnabled", false);
        callFunction("UI|CONTROL_ID_1|setEnabled", false);
        callFunction("UI|RESTRITEM_CODE|setEnabled", false);
        callFunction("UI|PARAVALUE_1|setEnabled", false);
        callFunction("UI|PARAVALUE|setEnabled", false);
        callFunction("UI|PARAVALUE_1_DESC|setEnabled", false);
        callFunction("UI|GROUP_NO|setEnabled", false);
        callFunction("UI|PARADATATYPE_1|setEnabled", false);
        callFunction("UI|PERIOD_CODE|setEnabled", false);
        callFunction("UI|PERIOD_VALUE|setEnabled", false);
        callFunction("UI|LIMIT_TYPE_1|setEnabled", false);
        callFunction("UI|LIMIT_TYPE_2|setEnabled", false);
        callFunction("UI|and|setEnabled", false);
        callFunction("UI|or|setEnabled", false);
        callFunction("UI|none|setEnabled", false);
    }

    /**
     * ϸ��ҳ��ؼ��ɱ༭
     * setUIDMainT
     */
    public void setUIDMainT() {
        callFunction("UI|save|setEnabled", true);
        callFunction("UI|new|setEnabled", true);
        callFunction("UI|RESTRITEM_CODE|setEnabled", true);
        callFunction("UI|PARAVALUE_1|setEnabled", true);
        callFunction("UI|PARAVALUE_1_DESC|setEnabled", true);
        callFunction("UI|PARAVALUE|setEnabled", true);
        callFunction("UI|GROUP_NO|setEnabled", true);
        callFunction("UI|PERIOD_CODE|setEnabled", true);
        callFunction("UI|PERIOD_VALUE|setEnabled", true);
        callFunction("UI|LIMIT_TYPE_1|setEnabled", true);
        callFunction("UI|LIMIT_TYPE_2|setEnabled", true);
        callFunction("UI|and|setEnabled", true);
        callFunction("UI|or|setEnabled", true);
        callFunction("UI|none|setEnabled", true);
    }


    /**
     * ��������<br>
     * onSave
     */
    public void onSave() {
        int index = ( (TTabbedPane) getComponent("TabbedPane")).
            getSelectedIndex();
        TParm result = new TParm();
        //ϸ���趨���淽��
        if (index == 1) {
            //������������
            if (!checkData()) {
                return;
            }
            String restritem = this.getValueString("RESTRITEM_CODE");
            int restritemcode = Integer.parseInt(restritem);
            TParm parm = new TParm();
            //����һ�����ж�
            if ("Y".equals(this.getValueString("LIMIT_TYPE_1"))) {
                parm.setData("LIMIT_TYPE", "1");
            }
            else if ("Y".equals(this.getValueString("LIMIT_TYPE_2"))) {
                parm.setData("LIMIT_TYPE", "2");
            }
            else {
                parm.setData("LIMIT_TYPE", "");
            }
            //�߼��ж�
            if ("Y".equals(this.getValueString("and"))) {
                parm.setData("LOGICAL_TYPE", "1");
            }
            else if ("Y".equals(this.getValueString("or"))) {
                parm.setData("LOGICAL_TYPE", "2");
            }
            else if ("Y".equals(this.getValueString("none"))) {
                parm.setData("LOGICAL_TYPE", "3");
            }
            else {
                parm.setData("LOGICAL_TYPE", "");
            }
            //����һֵ�Ŀؼ�ѡ��
            if (restritemcode == 5) {
                parm.setData("PARAVALUE_1", this.getValueString("PARAVALUE"));

            }
            else {
                parm.setData("PARAVALUE_1", this.getValueString("PARAVALUE_1"));
            }
            parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE_1"));
            parm.setData("CONTROL_ID", this.getValueInt("CONTROL_ID_1"));
            parm.setData("SERIAL_NO", this.getValueInt("SERIAL_NO"));
            parm.setData("GROUP_NO", this.getValueInt(" GROUP_NO"));
            parm.setData("RESTRITEM_CODE", this.getValueString("RESTRITEM_CODE"));
            parm.setData("START_VALUE", this.getValueString("START_VALUE"));
            parm.setData("END_VALUE", this.getValueString("END_VALUE"));
            parm.setData("PARADATATYPE_1", this.getValueString("PARADATATYPE_1"));
            parm.setData("PERIOD_CODE", this.getValueString("PERIOD_CODE"));
            parm.setData("PERIOD_VALUE", this.getValueInt("PERIOD_VALUE"));
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
            parm.setData("OPT_TERM", Operator.getIP());
            result = CTRMainTool.getNewInstance().onDQuery(
                parm);
            int row = dtable.getClickedRow();
            TParm tableparm = dtable.getParmValue();
            if (result.getCount() <= 0) {
                //ϸ���ĵ�һ������
                if (logicalstr().equals("")) {
                    // ��������
                    result = CTRMainTool.getNewInstance().onDInsert(parm);
                }
                else {
                    //���һ�����ݵ��߼�����ΪNONE
                    if (logicalstr().equals("3")) {
                        this.messageBox("���һ�����ݵ��߼���������ΪNONE!");
                        return;
                    }
                    else {
                        result = CTRMainTool.getNewInstance().onDInsert(parm);
                    }
                }
            }
            else {
                //�����߼�����
                int serialno = tableparm.getInt("SERIAL_NO", row);
                if (serialno < (MaxSeq() - 1)) {
                    if (parm.getValue("LOGICAL_TYPE").equals("3")) {
                        this.messageBox("�߼���������ΪNONE��");
                        return;
                    }
                }
                // ��������
                result = CTRMainTool.getNewInstance().onDUpdate(parm);
            }
            if (result.getErrCode() < 0) {
                this.messageBox("E0001");
            }
            else {
                this.messageBox("P0001");
                onQuery();
            }
        }
        else if (index == 0) {
            //�����趨���淽��
            //������������
            if (!checkData()) {
                return;
            }
            TParm parm = new TParm();
            parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
            parm.setData("CONTROL_ID", this.getValueInt("CONTROL_ID"));
            parm.setData("NHI_FLG", this.getValueString("NHI_FLG"));
            parm.setData("OWN_FLG", this.getValueString("OWN_FLG"));
            parm.setData("FORCE_FLG", this.getValueString("FORCE_FLG"));
            parm.setData("MESSAGE_TEXT", this.getValueString("MESSAGE_TEXT"));
            parm.setData("MESSAGE_TEXT_E",
                         this.getValueString("MESSAGE_TEXT_E"));
            parm.setData("CTRL_COMMENT", this.getValueString("CTRL_COMMENT"));
            parm.setData("ACTIVE_FLG", this.getValueString("ACTIVE_FLG"));
            parm.setData("SUBCLASS_CODE", this.getValueString("SUBCLASS_CODE"));
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
            parm.setData("OPT_TERM", Operator.getIP());
            result = CTRMainTool.getNewInstance().onMQuery(
                parm);
            if (result.getCount() <= 0) {
                // ��������
                result = TIOM_AppServer.executeAction(
                    "action.ctr.CTRMainAction",
                    "InsertCTRMaincode", parm);

            }
            else {
                // ��������
                result = CTRMainTool.getNewInstance().onMUpdate(parm);
            }
            if (result.getErrCode() < 0) {
                this.messageBox("E0001");
            }
            else {
                this.messageBox("P0001");
                //ˢ��
                onQuery();
            }

        }
    }

    /**
     * ��������������¼�<br>
     *
     */
    public void onMTableClicked() {
        if (mtable.getSelectedRow() < 0) {
            this.messageBox("û��ѡ������!");
        }
        int row = mtable.getClickedRow();
        TParm tableParm = mtable.getParmValue();
        TParm parm = tableParm.getRow(row);
        TParm mresult = CTRMainTool.getNewInstance().onMQuery(parm);
        if (mresult.getCount() < 0) {
            this.messageBox("E0116");
        }
        setValueForParm(
            "ORDER_CODE;ORDER_DESC;CONTROL_ID;NHI_FLG;OWN_FLG;FORCE_FLG;ACTIVE_FLG;" +
            "MESSAGE_TEXT;MESSAGE_TEXT_E;CTRL_COMMENT;SUBCLASS_CODE", mresult, 0);
        setUIMainT();

    }

    /**
     * onAdd
     *����
     */
    public void onAdd() {
        int index = ( (TTabbedPane) getComponent("TabbedPane")).
            getSelectedIndex();
        //����ܿص�����
        if (index == 0) {
            onMClear();
            setUIMainT();
            callFunction("UI|ORDER_CODE|setEnabled", true);
            callFunction("UI|CONTROL_ID|setEnabled", true);
        }
        //ϸ��ܿ�����
        else if (index == 1) {
            onDClear();
            setUIDMainT();
            getTextField("SERIAL_NO").setValue(MaxSeq() + "");
        }
    }

    /**
     * onDelete
     * ɾ������
     */
    public void onDelete()

    {
        int index = ( (TTabbedPane) getComponent("TabbedPane")).
            getSelectedIndex();
        //�����趨ɾ��
        if (index == 0) {
            int row = mtable.getSelectedRow();
            if (row < 0) {
                this.messageBox("��ѡ��ɾ�����ݣ�");
                return;
            }
            TParm mtableparm = mtable.getParmValue().getRow(row);
            TParm result = TIOM_AppServer.executeAction(
                "action.ctr.CTRMainAction",
                "deleteCTRMaincode", mtableparm);
            if (result.getErrCode() < 0) {
                this.messageBox("ɾ��ʧ��!");
            }
            else {
                this.messageBox("ɾ���ɹ�!");
                mtable.removeRow(row);
                onMClear();
                onQuery();

            }
        }
        //ϸ���趨ɾ��
        else if (index == 1) {
            int row = dtable.getSelectedRow();
            if (row < 0) {
                this.messageBox("��ѡ��ɾ������!");
                return;
            }
            TParm dtableparm = dtable.getParmValue().getRow(row);
            TParm result = TIOM_AppServer.executeAction(
                "action.ctr.CTRMainAction",
                "deleteCTRDetailcode", dtableparm);
            if (result.getErrCode() < 0) {
                this.messageBox("ɾ��ʧ��!");
            }
            else {
                this.messageBox("ɾ���ɹ�!");
                dtable.removeRow(row);
                onDClear();
                onQuery();
            }

        }

    }

    /**
     * ��ѯ����<br>
     *onQuery
     */
    public void onQuery() {
        int index = ( (TTabbedPane) getComponent("TabbedPane")).
            getSelectedIndex();
        //����ܿصĲ�ѯ
        if (index == 0) {
            TParm parm = new TParm();
            String code = this.getValueString("ORDER_CODE").trim();
            int id = this.getValueInt("CONTROL_ID");
            if (!"".equals(code)) {
                parm.setData("ORDER_CODE", code);
                if (!"".equals(id)) {
                    parm.setData("CONTROL_ID", id);
                }
            }
            TParm mresult = CTRMainTool.getNewInstance().onMQuery(parm);
            // �жϴ���ֵ
            if (mresult == null || mresult.getCount() <= 0) {
                this.messageBox("û�в�ѯ����!");
                return;
            }
            TParm tableparm = new TParm();
            for (int i = 0; i < mresult.getCount(); i++) {
                tableparm.addData("ORDER_CODE",
                                  mresult.getValue("ORDER_CODE", i));
                tableparm.addData("ORDER_DESC",
                                  mresult.getValue("ORDER_DESC", i));
                tableparm.addData("CONTROL_ID",
                                  mresult.getValue("CONTROL_ID", i));
                tableparm.addData("NHI_FLG", mresult.getValue("NHI_FLG", i));
                tableparm.addData("OWN_FLG", mresult.getValue("OWN_FLG", i));
                tableparm.addData("FORCE_FLG", mresult.getValue("FORCE_FLG", i));
                String text = mresult.getValue("MESSAGE_TEXT", i) +
                    mresult.getValue("MESSAGE_TEXT_E", i);
                tableparm.addData("MESSAGE_TEXT", text);
                tableparm.addData("CTRL_COMMENT",
                                  mresult.getValue("CTRL_COMMENT", i));
                tableparm.addData("ACTIVE_FLG",
                                  mresult.getValue("ACTIVE_FLG", i));
                tableparm.addData("SUBCLASS_CODE",
                        mresult.getValue("SUBCLASS_CODE", i));
            }
            ( (TTable)this.getTable("MTABLE")).setParmValue(tableparm);
            setUIMainF();
        }
        //ϸ���趨��ѯ
        if (index == 1) {
            TParm parm = new TParm();
            String code = this.getValueString("ORDER_CODE");
            getTextField("ORDER_CODE_1").setValue(code);
            String desc = this.getValueString("ORDER_DESC");
            getTextField("ORDER_DESC_1").setValue(desc);
            int id = this.getValueInt("CONTROL_ID");
            getNumberTextField("CONTROL_ID_1").setValue(id);
            if (!"".equals(code)) {
                parm.setData("ORDER_CODE", code);
            }
            if (!"".equals(id)) {
                parm.setData("CONTROL_ID", id);
            }
            TParm result = CTRMainTool.getNewInstance().onDQuery(parm);
            // �жϴ���ֵ
            if (result == null || result.getCount() <= 0) {
                dtable.removeRowAll();
                return;
            }
            TParm tableparm = new TParm();
            for (int i = 0; i < result.getCount(); i++) {
                String value = result.getValue("PARAVALUE_1", i);
                String restritemcode = result.getValue("RESTRITEM_CODE", i);
                tableparm.addData("ORDER_CODE_1",
                                  result.getValue("ORDER_CODE_1", i));
                tableparm.addData("CONTROL_ID_1",
                                  result.getValue("CONTROL_ID_1", i));
                tableparm.addData("SERIAL_NO", result.getValue("SERIAL_NO", i));
                tableparm.addData("GROUP_NO", result.getValue("GROUP_NO", i));
                tableparm.addData("RESTRITEM_CODE",
                                  result.getValue("RESTRITEM_CODE", i));
                tableparm.addData("PARAVALUE_1",
                                  result.getValue("PARAVALUE_1", i));
                //����ֵ
                if (restritemcode.equals("05")) {
                    tableparm.addData("PARAVALUE_1_DESC",
                                      meddesc(value));
                }
                else {
                    tableparm.addData("PARAVALUE_1_DESC",
                                      paravaluedesc(value));
                }
                tableparm.addData("PARAVALUE_2",
                                  result.getValue("PARAVALUE_2", i));
                tableparm.addData("PARAVALUE_3",
                                  result.getValue("PARAVALUE_3", i));
                tableparm.addData("PERIOD_CODE",
                                  result.getValue("PERIOD_CODE", i));
                tableparm.addData("PERIOD_VALUE",
                                  result.getValue("PERIOD_VALUE", i));
                tableparm.addData("START_VALUE",
                                  result.getValue("START_VALUE", i));
                tableparm.addData("END_VALUE", result.getValue("END_VALUE", i));
                String type = result.getValue("LOGICAL_TYPE", i);
                int Inttype = Integer.parseInt(type);
                switch (Inttype) {
                    case 1:
                        tableparm.addData("LOGICAL_TYPE", "AND");
                        break;
                    case 2:
                        tableparm.addData("LOGICAL_TYPE", "OR");
                        break;
                    case 3:
                        tableparm.addData("LOGICAL_TYPE", "NONE");
                        break;
                    default:
                        break;
                }
            }
            ( (TTable)this.getTable("dtable")).setParmValue(tableparm);
            setUIDMainF();
        }

    }

    /**
     * �������<br>
     * onClear
     */
    public void onClear() {
        int index = ( (TTabbedPane) getComponent("TabbedPane")).
            getSelectedIndex();
        //�����趨���
        if (index == 0) {
            onMClear();
            setUIMainF();
        }
        //ϸ���趨���
        else if (index == 1) {
            onDClear();
        }
    }

    /**
     *��������¼�<br>
     *onMClear
     */

    public void onMClear() {
        this.clearValue("ORDER_CODE;ORDER_DESC;CONTROL_ID;NHI_FLG;OWN_FLG;FORCE_FLG;MESSAGE_TEXT;MESSAGE_TEXT_E;CTRL_COMMENT;ACTIVE_FLG;SUBCLASS_CODE;");
    }

    /**
     * ϸ������¼�<br>
     *onDClear
     */
    public void onDClear() {
        this.clearValue(
            "SERIAL_NO;GROUP_NO;RESTRITEM_CODE;LIMIT_TYPE;PARAVALUE_1;PARAVALUE;PARAVALUE_1_DESC;PARADATATYPE_1;PARAMETER;PARAVALUE;"
            +
            "PERIOD_CODE;PERIOD_VALUE;LOGICAL_TYPE;PARAMETER_FLG;START_VALUE;END_VALUE;");
    }

    /**
     * ����ϸ��������¼�<br>
     * ondtableClicked
     */
    public void ondtableClicked() {
        if (dtable.getSelectedRow() < 0) {
            this.messageBox("û��ѡ������!");
        }
        int row = dtable.getClickedRow();
        TParm parm = new TParm();
        TParm tableParm = dtable.getParmValue();
        parm.setData("ORDER_CODE", tableParm.getValue("ORDER_CODE_1", row));
        parm.setData("CONTROL_ID", tableParm.getValue("CONTROL_ID_1", row));
        parm.setData("SERIAL_NO", tableParm.getValue("SERIAL_NO", row));
        TParm data = CTRMainTool.getNewInstance().onDQuery(parm);
        //��ѯ������
        if (!data.equals("") && data.getCount() < 0) {
            this.messageBox("E0116");
            return;
        }
        setValueForParm(
            "ORDER_CODE_1;ORDER_DESC_1;CONTROL_ID_1;GROUP_NO;RESTRITEM_CODE;" +
            "LIMIT_TYPE;PARADATATYPE_1;SERIAL_NO;" +
            "PERIOD_CODE;PERIOD_VALUE;LOGICAL_TYPE;START_VALUE;END_VALUE"
            , data, 0);
        this.getTextField("PARAMETER").setValue(data.getValue("PROMPT_MSG1", 0));
        this.getTextField("SERIAL_NO").setValue(data.getValue("SERIAL_NO", 0));
        this.getTextField("PARAMETER").setEnabled(false);
        setUIDMainT();
        //�����ų�
        if ("1".equals(data.getValue("LIMIT_TYPE", 0))) {
            this.getRadioButton("LIMIT_TYPE_1").setSelected(true);
        }
        else if ("2".equals(data.getValue("LIMIT_TYPE", 0))) {
            this.getRadioButton("LIMIT_TYPE_2").setSelected(true);
        }
        //�߼�����
        if ("1".equals(data.getValue("LOGICAL_TYPE", 0))) {
            this.getRadioButton("and").setSelected(true);
        }
        else if ("2".equals(data.getValue("LOGICAL_TYPE", 0))) {
            this.getRadioButton("or").setSelected(true);
        }
        else if ("3".equals(data.getValue("LOGICAL_TYPE", 0))) {
            this.getRadioButton("none").setSelected(true);
        }
        //�жϹܿ�һ����
        if (data.getValue("RESTRPARA_TYPE1", 0).equals("1")) {
            callFunction("UI|PARAMETER|setEnabled", false);
            callFunction("UI|START_VALUE|setEnabled", false);
            callFunction("UI|END_VALUE|setEnabled", false);
        }
        else if (data.getValue("RESTRPARA_TYPE1", 0).equals("2")) {
            callFunction("UI|PARAMETER|setEnabled", false);
            callFunction("UI|START_VALUE|setEnabled", true);
            callFunction("UI|END_VALUE|setEnabled", true);
        }
        //�ж���������
        if (data.getValue("PARADATATYPE_1", 0).equals("")) {
            callFunction("UI|PARADATATYPE_1|setEnabled", false);
            callFunction("UI|PARAVALUE_1|setEnabled", false);
        }
        else {
            String restritem = data.getValue("RESTRITEM_CODE", 0);
            String value = data.getValue("PARAVALUE_1", 0);
            int restritemcode = Integer.parseInt(restritem);
            switch (restritemcode) {
                //�����
                case 1:
                    this.getTextField("PARAVALUE_1").setValue(value);
                    this.getTextField("PARAVALUE_1_DESC").setValue(
                        paravaluedesc(value));
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", false);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "");
                    break;
                    //��һ���
                case 2:
                    this.getTextField("PARAVALUE_1").setValue(value);
                    this.getTextField("PARAVALUE_1_DESC").setValue(
                        paravaluedesc(value));
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", false);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "");
                    break;
                    //ʩ��ҽ��֮ǰ
                case 3:
                    this.getTextField("PARAVALUE_1").setValue(value);
                    this.getTextField("PARAVALUE_1_DESC").setValue(
                        paravaluedesc(value));
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", true);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", true);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "%ROOT%\\config\\sys\\SYSFeePopup.x");
                    break;
                    //ʩ��ҽ��֮��
                case 4:
                    this.getTextField("PARAVALUE_1").setValue(value);
                    this.getTextField("PARAVALUE_1_DESC").setValue(
                        paravaluedesc(value));
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", true);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", true);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "%ROOT%\\config\\sys\\SYSFeePopup.x");
                    break;
                    //����ֵ
                case 5:
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", false);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", false);
                    callFunction("UI|PARAVALUE|setVisible", true);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "");
                    this.getTextFormat("PARAVALUE").setValue(value);
                    break;
                    //ʩ��ҽ��֮ǰ
                case 6:
                    this.getTextField("PARAVALUE_1").setValue(value);
                    this.getTextField("PARAVALUE_1_DESC").setValue(
                        paravaluedesc(value));
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", false);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "");
                    break;
                default:
                    break;
            }

        }
    }

    /**
     * initdetail
     * ϸ���ʼ��
     */
    public void initdetail() {
        String code = this.getValueString("ORDER_CODE");
        getTextField("ORDER_CODE_1").setValue(code);
        String desc = this.getValueString("ORDER_DESC");
        getTextField("ORDER_DESC_1").setValue(desc);
        int id = this.getValueInt("CONTROL_ID");
        getNumberTextField("CONTROL_ID_1").setValue(id);
        onQuery();
        onDClear();
    }

    /**
     * CodeSelect
     * ��Ŀ����ѡ���¼�
     */
    public void CodeSelect() {
        String box = getComboBox("RESTRITEM_CODE").getValue();
        TParm result = new TParm();
        if (box.equals("")) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("RESTRITEM_CODE", box);
        result = CtrQueryTool.getNewInstance().onQuery(parm);
        //��ѯ������
        if (!result.equals("") && result.getCount() <= 0) {
            this.messageBox("E0116");
            return;
        }
        //�������
        this.clearValue(
            "PARAVALUE_1;PARAVALUE_1_DESC;PARAVALUE;START_VALUE;END_VALUE;PERIOD_CODE;PERIOD_VALUE;");
        setUIDMainT();
        //����ʾ��ֵ
        getComboBox("PARADATATYPE_1").setValue(result.getValue(
            "PARADATATYPE_1", 0));
        int restritemcode = Integer.parseInt(box);
        //�ж���������
        if (result.getValue("PARADATATYPE_1", 0).equals("")) {
            callFunction("UI|PARADATATYPE_1|setEnabled", false);
            callFunction("UI|PARAVALUE_1|setEnabled", false);
        }
        else {
            switch (restritemcode) {
                //�����
                case 1:
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE|setEnabled", false);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "");
                    break;
                    //��һ���
                case 2:
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE|setEnabled", false);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "");
                    break;
                    //ʩ��ҽ��֮ǰ
                case 3:
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", true);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE|setEnabled", false);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "%ROOT%\\config\\sys\\SYSFeePopup.x");
                    break;
                    //ʩ��ҽ��֮��
                case 4:
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", true);
                    callFunction("UI|PARAVALUE_1|setVisible", true);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", true);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", true);
                    callFunction("UI|PARAVALUE|setVisible", false);
                    callFunction("UI|PARAVALUE|setEnabled", false);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "%ROOT%\\config\\sys\\SYSFeePopup.x");
                    break;
                    //����ֵ
                case 5:
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", false);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", false);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", false);
                    callFunction("UI|PARAVALUE|setVisible", true);
                    callFunction("UI|PARAVALUE|setEnabled", true);
                    callFunction("UI|PERIOD_CODE|setEnabled", true);
                    callFunction("UI|PERIOD_VALUE|setEnabled", true);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "");
                    getcheckdata(restritemcode);
                    break;
                    //ʩ��ҽ��֮ǰ
                case 6:
                    callFunction("UI|PARADATATYPE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setVisible", false);
                    callFunction("UI|PARAVALUE_1_DESC|setEnabled", false);
                    callFunction("UI|PARAVALUE_1_DESC|setVisible", false);
                    callFunction("UI|PARAVALUE|setVisible", true);
                    callFunction("UI|PARAVALUE|setEnabled", false);
                    callFunction("UI|PERIOD_CODE|setEnabled", false);
                    callFunction("UI|PERIOD_VALUE|setEnabled", false);
                    callFunction("UI|PARAVALUE_1|setPopupMenuParameter", "aaa",
                                 "");
                    break;
            }

        }
        //��ʾ��
        getTextField("PARAMETER").setValue(result.getValue("PROMPT_MSG1", 0));
        //�ж��Ƿ�������
        if (result.getValue("RESTRPARA_TYPE1", 0).equals("1")) {
            callFunction("UI|PARAMETER|setEnabled", false);
            callFunction("UI|START_VALUE|setEnabled", false);
            callFunction("UI|END_VALUE|setEnabled", false);
        }
        else if (result.getValue("RESTRPARA_TYPE1", 0).equals("2")) {
            callFunction("UI|PARAMETER|setEnabled", false);
            callFunction("UI|START_VALUE|setEnabled", true);
            callFunction("UI|END_VALUE|setEnabled", true);
        }
    }

    /**
     * ��ѯ�������
     * @param code int
     */
    private void getcheckdata(int code) {
        //������
        if (code == 05) {
            String sql =
                "SELECT DISTINCT TESTITEM_CODE AS ID,TESTITEM_CHN_DESC AS NAME,PY1 FROM MED_LIS_RPT ORDER BY TESTITEM_CODE ";
            PARAVALUE.setPopupMenuSQL(sql);
            PARAVALUE.onQuery();
            PARAVALUE.setText("");
        }
    }

    /**
     * ��������
     * @return boolean
     */
    private boolean checkData() {
        int index = ( (TTabbedPane) getComponent("TabbedPane")).
            getSelectedIndex();
        if (index == 1) {
            //������Ŀ�Ƿ�Ϊ��
            String seq = this.getValueString("SERIAL_NO");
            if (seq == null || seq.length() <= 0) {
                this.messageBox("˳��Ų���Ϊ�գ�");
                return false;
            }
            //������Ŀ�Ƿ�Ϊ��
            String RESTRITEM_CODE = this.getValueString("RESTRITEM_CODE");
            if (RESTRITEM_CODE == null || RESTRITEM_CODE.length() <= 0) {
                this.messageBox("������Ŀ���벻��Ϊ�գ�");
                return false;
            }
            //����Ƿ�Ϊ��
            String GROUP_NO = this.getValueString("GROUP_NO");
            if (GROUP_NO == null || GROUP_NO.length() <= 0) {
                this.messageBox("��Ų���Ϊ�գ�");
                return false;
            }
            String start = this.getValueString("START_VALUE");
            String end = this.getValueString("END_VALUE");
            String[] str = new String[] {
                start, end};
            //������ĿΪ����ֵ
            if (Integer.parseInt(RESTRITEM_CODE) == 05) {
                if (!start.equals("") && !end.equals("") && start.length() > 0 &&
                    end.length() > 0) {
                    if (this.getValueString("PARAVALUE").equals("")) {
                        this.messageBox("������д����һѡ�");
                        return false;
                    }
                    if (CTRPanelTool.isCharic(str) ||
                        CTRPanelTool.isChinese(str)) {
                        if (!start.equals(end)) {
                            this.messageBox("���ֻ��ַ�����ȣ�");
                            return false;
                        }
                    }
                    else if (CTRPanelTool.isNumeric(str)) {
                        if (Double.parseDouble(start) > Double.parseDouble(end)) {
                            this.messageBox("���ִ�С����");
                            return false;
                        }
                    }
                    else {
                        this.messageBox("����Ƿ��ַ���");
                        return false;
                    }
                }
                //�����ı�����һ��Ϊ��
                else if ( (!start.equals("") && end.equals("")) ||
                         (start.equals("") && !end.equals(""))) {
                    this.messageBox("����д��������ֵ���䣡");
                    return false;
                }
                else {
                    if (!this.getValueString("PERIOD_CODE").equals("")) {
                        this.messageBox("����д����ֵ���䣡");
                        return false;
                    }
                }
            }
            //������ĿΪ����ϻ���һ���
            if (Integer.parseInt(RESTRITEM_CODE) == 01 ||
                Integer.parseInt(RESTRITEM_CODE) == 02) {
                if ( (!start.equals("") && end.equals("")) ||
                    (start.equals("") && !end.equals(""))) {
                    this.messageBox("����д�������ֵ���䣡");
                    return false;
                }
                if (!start.equals("") && !end.equals("")) {
                    if (!CTRPanelTool.ismixic(str)) {
                        this.messageBox("���ֵ�������ݲ��淶��");
                        return false;
                    }
                    if (StringTool.compareTo(start, end) > 0) {
                        this.messageBox("���ֵ�����С����");
                        return false;
                    }
                }

            }
            TParm inparm = new TParm();
            inparm.setData("RESTRITEM_CODE", RESTRITEM_CODE);
            TParm result = CtrQueryTool.getNewInstance().onQuery(inparm);
            //�������÷�Χ
            if (!result.getBoolean("INCLUDE_FLG", 0)) {
                if ("Y".equals(this.getValueString("LIMIT_TYPE_1"))) {
                    this.messageBox("�˹��Ʋ��ʺ����ƣ�");
                    return false;
                }
            }
            //�ų����÷�Χ
            if (!result.getBoolean("EXCLUDE_FLG", 0)) {
                if ("Y".equals(this.getValueString("LIMIT_TYPE_2"))) {
                    this.messageBox("�˹��Ʋ��ʺ��ų���");
                    return false;
                }
            }
            //�����ڼ���������
            if (!result.getBoolean("OPD_FLG", 0)) {
                if (this.getValue("PERIOD_CODE").equals("O")) {
                    this.messageBox("�˹��Ʋ��ʺ����");
                    return false;
                }
            }
            //���ﷶΧ����
            if (!result.getBoolean("EMG_FLG", 0)) {
                if (this.getValue("PERIOD_CODE").equals("E")) {
                    this.messageBox("�˹��Ʋ��ʺϼ��");
                    return false;
                }
            }
            //סԺ��Χ����
            if (!result.getBoolean("INP_FLG", 0)) {
                if (this.getValue("PERIOD_CODE").equals("I")) {
                    this.messageBox("�˹��Ʋ��ʺ�סԺ��");
                    return false;
                }
            }
            //UD��Χ����
            if (!result.getBoolean("UD_FLG", 0)) {
                if (this.getValue("PERIOD_CODE").equals("UD")) {
                    this.messageBox("�˹��Ʋ��ʺ�UD��");
                    return false;
                }
            }
        }
        else {
            //ҽ������Ƿ�Ϊ��
            String order_code = this.getValueString("ORDER_CODE");
            if (order_code == null || order_code.length() <= 0) {
                this.messageBox("ҽ����벻��Ϊ��");
                return false;
            }
            //���Ʊ����Ƿ�Ϊ��
            String control_code = this.getValueString("CONTROL_ID");
            if (control_code == null || control_code.length() <= 0) {
                this.messageBox("���Ʊ��벻��Ϊ��");
                return false;
            }
            //��Ϣ�����Ƿ�Ϊ��
            String text = this.getValueString("MESSAGE_TEXT");
            if (text == null || text.length() <= 0) {
                this.messageBox("��Ϣ���ݲ���Ϊ��");
                return false;
            }
        }
        return true;
    }

    /**
     * �õ�TTable����
     * @param tagName String
     * @return TTable
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�TTextField����
     * @param tagName String
     * @return TTextField
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * �õ�TNumberTextField����
     * @param tagName String
     * @return TNumberTextField
     */
    private TNumberTextField getNumberTextField(String tagName) {
        return (TNumberTextField) getComponent(tagName);
    }

    /**
     * �õ�TRadioButton����
     * @param tagName String
     * @return TRadioButton
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     *  �õ�TComboBox����
     * @param tagName String
     * @return TComboBox
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * �õ�TextFormat����
     * @param tagName String
     * @return TTextFormat
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

    /**
     * getDBTool
     * ���ݿ⹤��ʵ��
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * MaxSeq
     * ϸ���ȡ���˳���
     * @return int
     */
    private int MaxSeq() {
        int number;
        TParm MaxSeqParm = new TParm(this.getDBTool().select(
            "SELECT MAX(SERIAL_NO) AS NO FROM CTR_DETAIL WHERE ORDER_CODE='" +
            this.getValueString("ORDER_CODE_1") + "' AND CONTROL_ID='" +
            this.getValueInt("CONTROL_ID_1") + "'"));
        if (MaxSeqParm.getCount() <= 0) {
            number = 0;
        }
        else {
            number = MaxSeqParm.getInt("NO", 0) + 1;
        }
        return number;
    }

    /**
     * ��ȡϸ���߼�����
     * @return String
     */
    private String logicalstr() {
        //�����߼�����
        String logicalytypestr = "";
        if (MaxSeq() == 0) {
            logicalytypestr = "";
        }
        if (MaxSeq() > 0) {
            TParm checklogical = new TParm(this.getDBTool().select(
                "SELECT LOGICAL_TYPE FROM CTR_DETAIL WHERE ORDER_CODE='" +
                this.getValueString("ORDER_CODE_1") +
                "' AND CONTROL_ID='" +
                this.getValueInt("CONTROL_ID_1") + "' AND SERIAL_NO='" +
                (MaxSeq() - 1) + "'" +
                "ORDER BY SERIAL_NO"));
            if (checklogical.getCount() <= 0) {
                logicalytypestr = "";
            }
            else {
                logicalytypestr = checklogical.getValue("LOGICAL_TYPE", 0);
            }
        }
        return logicalytypestr;
    }


    /**
     * ��̬���ؽ���
     * @param name String
     * @return TPanel
     */
    public TPanel getTPanel(String name) {

        TPanel panel = (TPanel)this.getComponent(name);
        return panel;
    }

}
