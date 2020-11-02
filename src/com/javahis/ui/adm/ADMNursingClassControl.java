package com.javahis.ui.adm;

import jdo.sys.Operator;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import jdo.reg.RegMethodTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import java.util.Vector;
import com.dongyang.control.TControl;
import com.dongyang.ui.event.TPopupMenuEvent;

/**
 * <p>Title:����ȼ� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMNursingClassControl extends TControl {
    public ADMNursingClassControl() {
    }
    TParm data;
    int selectRow = -1;

    public void onInit() {
            super.onInit();
            ((TTable) getComponent("Table")).addEventListener("Table->"
                            + TTableEvent.CLICKED, this, "onTableClicked");
            //ֻ��text���������������sys_fee������
        callFunction("UI|ORDER_CODE|setPopupMenuParameter","aaa","%ROOT%\\config\\sys\\SYSICDPopup.x");

        //textfield���ܻش�ֵ
        callFunction("UI|ORDER_CODE|addEventListener",TPopupMenuEvent.RETURN_VALUE,this,"popReturn");

            onClear();
    }
    /**
 * ����¼�
 * @param tag String
 * @param obj Object
 */
public void popReturn(String tag,Object obj)
       {
           TParm parm=(TParm)obj;
           parm.getValue("ICD_CHN_DESC");
           this.setValue("ORDER_CODE",parm.getValue("ICD_CODE"));
           this.setValue("ORDER_DESC",parm.getValue("ICD_CHN_DESC"));

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
                            "NURSING_CLASS_CODE;NURSING_CLASS_DESC;ORDER_CODE;COLOUR_RED;COLOUR_GREEN;COLOUR_BLUE;PSFKIN_CODE",
                            data, row);
            selectRow = row;
            // ���ɱ༭
            ((TTextField) getComponent("NURSING_CLASS_CODE")).setEnabled(false);
            // ����ɾ����ť״̬
            ((TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * ����
     */
    public void onInsert() {
            if (!emptyTextCheck("NURSING_CLASS_CODE,NURSING_CLASS_DESC")) {
                    return;
            }
            TParm parm = getParmForTag("NURSING_CLASS_CODE;NURSING_CLASS_DESC;ORDER_CODE;COLOUR_RED;COLOUR_GREEN:COLOUR_BLUE;PSFKIN_CODE");
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_TERM", Operator.getIP());
            TParm result = RegMethodTool.getInstance().insertdata(parm);
            // �жϴ���ֵ
            if (result.getErrCode() < 0) {
                    messageBox(result.getErrText());
                    return;
            }
            // ��ʾ��������
            ((TTable) getComponent("TABLE"))
                            .addRow(
                                            parm,
                                            "REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ;DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG;OPT_USER;OPT_DATE;OPT_TERM");
            int row = data.insertRow();
            data.setRowData(row, parm);
            this.messageBox("P0001");
    }

    /**
     * ����
     */
    public void onUpdate() {
            TParm parm = getParmForTag("REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ:int;DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG");
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_TERM", Operator.getIP());
            TParm result = RegMethodTool.getInstance().updatedata(parm);
            // �жϴ���ֵ
            if (result.getErrCode() < 0) {
                    messageBox(result.getErrText());
                    return;
            }
            // ѡ����
            int row = ((TTable) getComponent("TABLE")).getSelectedRow();
            if (row < 0)
                    return;
            // ˢ�£�����ĩ��ĳ�е�ֵ
            data.setRowData(row, parm);
            ((TTable) getComponent("TABLE")).setRowParmValue(row, data);
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
                    String regMethod = getValue("REGMETHOD_CODE").toString();
                    TParm result = RegMethodTool.getInstance().deletedata(regMethod);
                    if (result.getErrCode() < 0) {
                            messageBox(result.getErrText());
                            return;
                    }
                    TTable table = ((TTable) getComponent("TABLE"));
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
            } else {
                    return;
            }
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
            String regMethod = getText("REGMETHOD_CODE");
            data = RegMethodTool.getInstance().selectdata(regMethod);
            // �жϴ���ֵ
            if (data.getErrCode() < 0) {
                    messageBox(data.getErrText());
                    return;
            }
            ((TTable) getComponent("TABLE")).setParmValue(data);
    }

    /**
     * ���
     */
    public void onClear() {
            clearValue("REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ;DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG");
            ((TTable) getComponent("TABLE")).clearSelection();
            selectRow = -1;
            ((TTextField) getComponent("REGMETHOD_CODE")).setEnabled(true);
            ((TTextField) getComponent("REGMETHOD_DESC")).setEnabled(true);
            // ����ɾ����ť״̬
            ((TMenuItem) getComponent("delete")).setEnabled(false);
            onQuery();
            long seq = 0;
            // ȡSEQ���ֵ
            if (data.existData("SEQ")) {
                    Vector vct = data.getVectorValue("SEQ");
                    for (int i = 0; i < vct.size(); i++) {
                            long a = Long.parseLong((vct.get(i)).toString().trim());
                            if (a > seq)
                                    seq = a;
                    }
                    this.setValue("SEQ", seq + 1);
            }
    }

}
