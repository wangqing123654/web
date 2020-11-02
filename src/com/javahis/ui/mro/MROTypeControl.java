package com.javahis.ui.mro;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTextField;
import com.dongyang.util.TMessage;
import com.dongyang.manager.TCM_Transform;
import jdo.mro.MROTypeTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p>Title: ���������Ŀ���͹���</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk  2009-4-29
 * @version 1.0
 */
public class MROTypeControl
    extends TControl {
    TParm data;
    int selectRow = -1;

    public void onInit() {
        super.onInit();
        ( (TTable) getComponent("Table")).addEventListener("Table->"
            + TTableEvent.CLICKED, this, "onTableClicked");
        onClear();
    }

    /**
     * ���Ӷ�Table�ļ���
     *
     * @param row
     *            int
     */
    public void onTableClicked(int row) {
        // ѡ����
        if (row < 0)
            return;
        setValueForParm(
            "TYPE_CODE;TYPE_DESC;PY1;PY2;DESCRIPTION",
            data, row);
        selectRow = row;
        // ���ɱ༭
        ((TTextField) getComponent("TYPE_CODE")).setEnabled(false);
        // ����ɾ����ť״̬
        ((TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * ����
     */
    public void onInsert() {
        if(this.getText("TYPE_CODE").trim().length()<=0||this.getText("TYPE_DESC").trim().length()<=0){
            this.messageBox("���������Ŀ���ͱ�������Ʋ���Ϊ�գ�");
            return;
        }
        TParm parm = this.getParmForTag("TYPE_CODE;TYPE_DESC;PY1;PY2;DESCRIPTION");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = MROTypeTool.getInstance().insertdata(parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        // ��ʾ��������
        int row = ( (TTable) getComponent("TABLE"))
            .addRow(
                parm,
                "TYPE_CODE;TYPE_DESC;PY1;PY2;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERM");
        data.setRowData(row, parm);
        this.messageBox("��ӳɹ���");
    }

    /**
     * ����
     */
    public void onUpdate() {
        TParm parm = this.getParmForTag("TYPE_CODE;TYPE_DESC;PY1;PY2;DESCRIPTION");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = MROTypeTool.getInstance().updatedata(parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        // ѡ����
        int row = ( (TTable) getComponent("Table")).getSelectedRow();
        if (row < 0)
            return;
        // ˢ�£�����ĩ��ĳ�е�ֵ
        data.setRowData(row, parm);
        ( (TTable) getComponent("Table")).setRowParmValue(row, data);
        this.messageBox("�޸ĳɹ���");
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
     * ��ѯ
     */
    public void onQuery() {
        String code = getText("TYPE_CODE").trim();
        String desc = getText("TYPE_DESC").trim();
        TParm parm = new TParm();
        parm.setData("TYPE_CODE",code);
        parm.setData("TYPE_DESC",desc);
        data = MROTypeTool.getInstance().selectType(parm);
        // �жϴ���ֵ
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ((TTable) getComponent("Table")).setParmValue(data);
    }
    /**
     * ���
     */
    public void onClear() {
        this.clearValue("TYPE_CODE;TYPE_DESC;PY1;PY2;DESCRIPTION");
        ((TTable) getComponent("Table")).clearSelection();
        selectRow = -1;
        // ����ɾ����ť״̬
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        ((TTextField) getComponent("TYPE_CODE")).setEnabled(true);
        onQuery();
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        if (this.messageBox("��ʾ", "�Ƿ�ɾ��", 2) == 0) {
            if (selectRow == -1)
                return;
            String code = getValue("TYPE_CODE").toString();
            TParm result = MROTypeTool.getInstance().deletedata(code);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            TTable table = ( (TTable) getComponent("Table"));
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            this.messageBox("ɾ���ɹ���");
            onClear();
        }
        else {
            return;
        }
    }

    /**
     * ���ݺ������ƴ������ĸ
     *
     * @return Object
     */

    public Object onCode() {
        if (TCM_Transform.getString(this.getValue("TYPE_DESC")).length() <
            1) {
            return null;
        }
        String value = TMessage.getPy(this.getValueString("TYPE_DESC"));
        if (null == value || value.length() < 1) {
            return null;
        }
        this.setValue("PY1", value);
        // �������
        ((TTextField) getComponent("PY1")).grabFocus();
        return null;
    }
}
