package com.javahis.ui.sum;

import java.util.Vector;

import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.JavaHisDebug;
import jdo.sum.SUMTmptrKindTool;

/**
 * <p>Title: ��������ά������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author ZangJH
 *
 * @version 1.0
 */
public class SUMTmptrKindControl
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
            "TMPTRKINDCODE;TMPTRKINDDESC;PRESENTNOTATION",
            data, row);
        selectRow = row;
        // ���ɱ༭
        ( (TTextField) getComponent("TMPTRKINDCODE")).setEnabled(false);
        // ����ɾ����ť״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * ����
     */
    public void onInsert() {

        if (!emptyTextCheck("TMPTRKINDCODE")) {
            this.messageBox("����������벻����Ϊ�գ�");
            return;
        }
        TParm parm = getParmForTag(
            "TMPTRKINDCODE;TMPTRKINDDESC;PRESENTNOTATION");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = SUMTmptrKindTool.getInstance().insertdata(parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        // ��ʾ��������
        ( (TTable) getComponent("TABLE"))
            .addRow(
                parm,
                "TMPTRKINDCODE;TMPTRKINDDESC;PRESENTNOTATION;OPT_USER;OPT_DATE;OPT_TERM");
        int row = data.insertRow();
        data.setRowData(row, parm);
        this.messageBox("P0001");
    }

    /**
     * ����
     */
    public void onUpdate() {
        TParm parm = getParmForTag(
            "TMPTRKINDCODE;TMPTRKINDDESC;PRESENTNOTATION");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = SUMTmptrKindTool.getInstance().updatedata(parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        // ѡ����
        int row = ( (TTable) getComponent("TABLE")).getSelectedRow();
        if (row < 0)
            return;
        // ˢ�£�����ĩ��ĳ�е�ֵ
        data.setRowData(row, parm);
        ( (TTable) getComponent("TABLE")).setRowParmValue(row, data);
        this.messageBox("P0005");
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
            String code = getValue("TMPTRKINDCODE").toString();
            TParm result = SUMTmptrKindTool.getInstance().deletedata(code);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            TTable table = ( (TTable) getComponent("TABLE"));
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            // ɾ��������ʾ
            table.removeRow(row);
            if (row == table.getRowCount())
                table.setSelectedRow(row - 1);
            else
                table.setSelectedRow(row);
            this.messageBox("P0003");
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
        String code = getText("TMPTRKINDCODE");
        data = SUMTmptrKindTool.getInstance().selectdata(code);
        // �жϴ���ֵ
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ( (TTable) getComponent("TABLE")).setParmValue(data);
    }

    /**
     * ���
     */
    public void onClear() {
        clearValue("TMPTRKINDCODE;TMPTRKINDDESC;PRESENTNOTATION");
        ( (TTable) getComponent("TABLE")).clearSelection();
        selectRow = -1;
        ( (TTextField) getComponent("TMPTRKINDCODE")).setEnabled(true);
        ( (TTextField) getComponent("TMPTRKINDDESC")).setEnabled(true);
        // ����ɾ����ť״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        onQuery();
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

    public static void main(String[] args) {
//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("sum\\SUMTmptrKind.x");
    }
}
