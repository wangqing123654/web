package com.javahis.ui.sta;

import com.dongyang.control.*;
import jdo.sta.STAExListTool;
import jdo.sys.Operator;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import java.util.Vector;

/**
 * <p>Title: �ж����� ����ά��</p>
 *
 * <p>Description: �ж����� ����ά��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-2
 * @version 1.0
 */
public class STAExListControl
    extends TControl {
    TParm data;
    int selectRow = -1;

    public void onInit() {
        super.onInit();
        ((TTable) getComponent("TABLE")).addEventListener("TABLE->"
            + TTableEvent.CLICKED, this, "onTableClicked");
        onClear();
    }

    /**
     * ���Ӷ�Table�ļ���
     *
     * @param row
     * int
     */
    public void onTableClicked(int row) {
        // ѡ����
        if (row < 0)
            return;
        setValueForParm(
            "SEQ;ICD_DESC;CONDITION",
            data, row);
        selectRow = row;
        // ���ɱ༭
        ( (TTextField) getComponent("SEQ")).setEnabled(false);
        // ����ɾ����ť״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * ����
     */
    public void onInsert() {
        if (this.getText("SEQ").trim().length()<=0) {
            this.messageBox_("��Ų���Ϊ��!");
            return;
        }
        TParm parm = getParmForTag("SEQ;ICD_DESC;CONDITION");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = STAExListTool.getInstance().insertData(parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox_(result.getErrText());
            return;
        }
        // ��ʾ��������
        int row = ( (TTable) getComponent("TABLE"))
            .addRow(
                parm,
                "SEQ;ICD_DESC;CONDITION;OPT_USER;OPT_DATE;OPT_TERM");
        data.setRowData(row, parm);
        this.clearValue("SEQ;ICD_DESC;CONDITION");
        setSEQ();//������������
        this.messageBox_("��ӳɹ���");
    }

    /**
     * ����
     */
    public void onUpdate() {
        TParm parm = getParmForTag("SEQ;ICD_DESC;CONDITION");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = STAExListTool.getInstance().updateData(parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox_(result.getErrText());
            return;
        }
        // ѡ����
        int row = ( (TTable) getComponent("TABLE")).getSelectedRow();
        if (row < 0)
            return;
        // ˢ�£�����ĩ��ĳ�е�ֵ
        data.setRowData(row, parm);
        ( (TTable) getComponent("TABLE")).setRowParmValue(row, data);
        this.messageBox_("�޸ĳɹ���");
    }

    /**
     * ����
     */
    public void onSave() {
        if (selectRow == -1) {
            onInsert();
            return;
        }
        onUpdate();
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        if (this.messageBox("��ʾ", "�Ƿ�ɾ��", 2) == 0) {
            if (selectRow == -1)
                return;
            TParm parm = new TParm();
            parm.setData("SEQ",getText("SEQ"));
            TParm result = STAExListTool.getInstance().deleteData(parm);
            if (result.getErrCode() < 0) {
                messageBox_(result.getErrText());
                return;
            }
            TTable table = ( (TTable) getComponent("TABLE"));
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            this.messageBox_("ɾ���ɹ���");
            onClear();
        }
        else {
            return;
        }
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = new TParm();
        if(getText("SEQ").trim().length()>0)
            parm.setData("SEQ", getText("SEQ"));
        if(getText("ICD_DESC").trim().length()>0)
            parm.setData("ICD_DESC", getText("%"+"ICD_DESC") + "%");
        data = STAExListTool.getInstance().selectData(parm);
        // �жϴ���ֵ
        if (data.getErrCode() < 0) {
            messageBox_(data.getErrText());
            return;
        }
        ( (TTable) getComponent("TABLE")).setParmValue(data);
    }

    /**
     * ���
     */
    public void onClear() {
        clearValue("SEQ;ICD_DESC;CONDITION");
        ( (TTable) getComponent("TABLE")).clearSelection();
        selectRow = -1;
        ( (TTextField) getComponent("SEQ")).setEnabled(true);
        // ����ɾ����ť״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        onQuery();
        setSEQ();//������������
    }
    /**
     * ������������ SEQ
     */
    public void setSEQ() {
        long seq = 0;
        // ȡSEQ���ֵ
        if (data.existData("SEQ")) {
            Vector vct = data.getVectorValue("SEQ");
            for (int i = 0; i < vct.size(); i++) {
                long a = Long.parseLong( (vct.get(i)).toString().trim());
                if (a > seq)
                    seq = a;
            }
            this.setValue("SEQ", seq + 1);
        }
    }

}
