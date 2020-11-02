package com.javahis.ui.sta;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TMenuItem;
import jdo.sys.Operator;
import java.util.Vector;
import jdo.sta.STASDListTool;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.util.TMessage;
import com.dongyang.manager.TCM_Transform;

/**
 * <p>Title: ������ά��</p>
 *
 * <p>Description: ������ά��</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-6
 * @version 3.0
 */
public class STASDListControl extends TControl {
    TParm data;
    int selectRow = -1;
    public void onInit(){
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
            "SEQ;SD_DESC;PY;CONDITION",
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
        TParm parm = getParmForTag("SEQ;SD_DESC;PY;CONDITION");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = STASDListTool.getInstance().insertData(parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox_(result.getErrText());
            return;
        }
        // ��ʾ��������
        int row = ( (TTable) getComponent("TABLE"))
            .addRow(
                parm,
                "SEQ;SD_DESC;PY;CONDITION;OPT_USER;OPT_DATE;OPT_TERM");
        data.setRowData(row, parm);
        this.clearValue("SEQ;SD_DESC;PY;CONDITION");
        setSEQ();//������������
        this.messageBox_("����ɹ���");
    }

    /**
     * ����
     */
    public void onUpdate() {
        TParm parm = getParmForTag("SEQ;SD_DESC;PY;CONDITION");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = STASDListTool.getInstance().updateData(parm);
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
            TParm result = STASDListTool.getInstance().deleteData(parm);
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
        if(getText("SD_DESC").trim().length()>0)
            parm.setData("SD_DESC", "%"+ getText("SD_DESC") + "%");
        data = STASDListTool.getInstance().selectData(parm);
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
        clearValue("SEQ;SD_DESC;PY;CONDITION");
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
            this.setValue("SEQ", seq + 1 + "");
        }
    }
    /**
     * ���ݺ������ƴ������ĸ
     *
     * @return Object
     */
    public Object onCode() {
        if (TCM_Transform.getString(this.getValue("SD_DESC")).length() <
            1) {
            return null;
        }
        String value = TMessage.getPy(this.getValueString("SD_DESC"));
        if (null == value || value.length() < 1) {
            return null;
        }
        this.setValue("PY", value);
        // �������
        ( (TTextField) getComponent("PY")).grabFocus();
        return null;
    }

}
