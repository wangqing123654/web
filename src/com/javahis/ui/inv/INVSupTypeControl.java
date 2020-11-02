package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.inv.InvSupTypeTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.util.TMessage;

/**
 * <p>Title: ��Ӧ���</p>
 *
 * <p>Description: ��Ӧ���</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.2.22
 * @version 1.0
 */
public class INVSupTypeControl
    extends TControl {

    /**
     * ���
     */
    private TTable table;

    public INVSupTypeControl() {
    }

    /**
     * ��ʼ��
     */
    public void onInit() {
        table = getTable("TABLE");
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        if (!"".equals(getValueString("SUPTYPE_CODE"))) {
            parm.setData("SUPTYPE_CODE", this.getValueString("SUPTYPE_CODE"));
        }
        if (!"".equals(getValueString("PACK_MODE"))) {
            parm.setData("PACK_MODE", this.getValueString("PACK_MODE"));
        }

        TParm result = InvSupTypeTool.getInstance().onQuery(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        table.setParmValue(result);
    }

    /**
     * ���淽��
     */
    public void onSave() {
        TParm parm = new TParm();
        if ("".equals(getValueString("SUPTYPE_CODE"))) {
            this.messageBox("�����벻��Ϊ��");
            return;
        }
        if ("".equals(getValueString("SUPTYPE_DESC"))) {
            this.messageBox("������Ʋ���Ϊ��");
            return;
        }
        if ("".equals(getValueString("PACK_MODE"))) {
            this.messageBox("���ⷽʽ����Ϊ��");
            return;
        }
        parm.setData("SUPTYPE_CODE",this.getValueString("SUPTYPE_CODE"));
        parm.setData("SUPTYPE_DESC",this.getValueString("SUPTYPE_DESC"));
        parm.setData("PY1",this.getValueString("PY1"));
        parm.setData("PY2",this.getValueString("PY2"));
        parm.setData("PACK_MODE",this.getValueString("PACK_MODE"));
        parm.setData("TYPE_FLG",
                     !"Y".equals(this.getValueString("TYPE_FLG")) ? "N" : "Y");
        parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());

        TParm result = new TParm();
        if (table.getSelectedRow() < 0) {
            // ��������
            result = InvSupTypeTool.getInstance().onInsert(parm);
        }
        else {
            // ���·���
            result = InvSupTypeTool.getInstance().onUpdate(parm);
        }
        if (result.getErrCode() < 0) {
            this.messageBox("����ʧ��");
            return;
        }
        this.messageBox("����ɹ�");
        onQuery();
    }
    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "SUPTYPE_CODE;SUPTYPE_DESC;PACK_MODE;TYPE_FLG;" +
            "PY1;PY2;DESCRIPTION";
        this.clearValue(clearStr);
        table.removeRowAll();
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        if (table.getSelectedRow() < 0) {
            this.messageBox("��ѡ��ɾ����");
            return;
        }
        TParm parm = table.getParmValue().getRow(table.getSelectedRow());
        TParm result = InvSupTypeTool.getInstance().onDelete(parm);
        if (result.getErrCode() < 0) {
            this.messageBox("ɾ��ʧ��");
            return;
        }
        table.removeRow(table.getSelectedRow());
        this.messageBox("ɾ���ɹ�");
    }

    /**
     * onSupDescAction�س��¼�
     */
    public void onSupDescAction() {
        String py = TMessage.getPy(this.getValueString("SUPTYPE_DESC"));
        setValue("PY1", py);
    }

    /**
     * ��񵥻��¼�
     */
    public void onTableClick() {
        TParm parm = table.getParmValue().getRow(table.getSelectedRow());
        this.setValueForParm("SUPTYPE_CODE;SUPTYPE_DESC;PACK_MODE;TYPE_FLG;" +
                             "PY1;PY2;DESCRIPTION", parm);
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

}
