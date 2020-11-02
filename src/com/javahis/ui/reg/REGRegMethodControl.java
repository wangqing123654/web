package com.javahis.ui.reg;

import java.util.Vector;

import jdo.reg.RegMethodTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;
import com.dongyang.ui.event.TPopupMenuEvent;
import jdo.sys.SysFee;
import jdo.sys.SystemTool;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>
 * Title: �Һŷ�ʽ
 * </p>
 *
 * <p>
 * Description:�Һŷ�ʽ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author wangl 2008.08.21
 * @version 1.0
 */
public class REGRegMethodControl
    extends TControl {
    TParm data;
    int selectRow = -1;

    public void onInit() {
        super.onInit();
        ( (TTable) getComponent("Table")).addEventListener("Table->"
            + TTableEvent.CLICKED, this, "onTableClicked");
        //ֻ��text���������������sys_fee������
        callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
                     "%ROOT%\\config\\sys\\SYSFeePopup.x");
        //���ܻش�ֵ
        callFunction("UI|ORDER_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
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
            "REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ;"+
            "DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG;PRINT_FLG;SITENUM_FLG;ORDER_CODE",
            data, row);
        this.setValue("FEE", onOrderCode(data.getValue("ORDER_CODE",row)));
        this.setValue("ORDER_DESC", getOrderDesc(data.getValue("ORDER_CODE", row)));
        selectRow = row;
        // ���ɱ༭
        ( (TTextField) getComponent("REGMETHOD_CODE")).setEnabled(false);
        // ����ɾ����ť״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(true);
    }
    /**
     * ����ORDER_CODE��ѯ����
     * @param orderCode String
     * @return double
     */
    public double onOrderCode(String orderCode) {
        if (orderCode.equals("") || orderCode == null)
            return 0.00;
        double own_price = SysFee.getFee(orderCode);
        return own_price;
    }
    /**
     * ͨ��orderCode�õ�ҽ������
     * @param orderCode String
     * @return String
     */
    public String getOrderDesc(String orderCode) {
        String orderDesc = "";
        String selOrderDesc =
            " SELECT ORDER_DESC FROM SYS_FEE WHERE ORDER_CODE = '" + orderCode +
            "' ";
        TParm orderDescParm = new TParm(TJDODBTool.getInstance().select(
            selOrderDesc));
        if (orderDescParm.getErrCode() < 0) {
            err(orderDescParm.getErrName() + " " + orderDescParm.getErrName());
            return orderDesc;
        }
        orderDesc = orderDescParm.getValue("ORDER_DESC", 0);
        return orderDesc;
    }
    /**
     * ���ô��������б�ѡ��
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE"));
        this.setValue("FEE", parm.getValue("OWN_PRICE"));
        this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
        this.grabFocus("FEE");
    }

    /**
     * ����
     */
    public void onInsert() {
        if (!emptyTextCheck("REGMETHOD_CODE,REGMETHOD_DESC")) {
            return;
        }
        TParm parm = getParmForTag("REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ:int;"+
                                   "DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG;PRINT_FLG;SITENUM_FLG;ORDER_CODE");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = RegMethodTool.getInstance().insertdata(parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        // ��ʾ��������
//        callFunction("UI|Table|addRow", parm,
//                     "REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ;"+
//                     "DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG;"+
//                     "ORDER_CODE;OPT_USER;OPT_DATE;OPT_TERM");
        ( (TTable) getComponent("Table"))
            .addRow(
                parm,
                "REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ;"+
                "DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG;PRINT_FLG;SITENUM_FLG;ORDER_CODE;"+
                "OPT_USER;OPT_DATE;OPT_TERM");

//        String regMethod = this.getValue("REGMETHOD_CODE").toString();
//        data = RegMethodTool.getInstance().selectdata(regMethod);
//        int row = data.insertRow();
//        data.setRowData(row, parm);
        this.messageBox("P0001");
    }

    /**
     * ����
     */
    public void onUpdate() {
        TParm parm = getParmForTag("REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ:int;"+
                                   "DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG;PRINT_FLG;SITENUM_FLG;ORDER_CODE");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = RegMethodTool.getInstance().updatedata(parm);
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
            String regMethod = getValue("REGMETHOD_CODE").toString();
            TParm result = RegMethodTool.getInstance().deletedata(regMethod);
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
        String regMethod = getText("REGMETHOD_CODE");
        data = RegMethodTool.getInstance().selectdata(regMethod);
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
        clearValue("REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ;"+
                   "DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG;"+
                   "PRINT_FLG;ORDER_CODE;ORDER_DESC;FEE;SITENUM_FLG");
        ( (TTable) getComponent("TABLE")).clearSelection();
        selectRow = -1;
        ( (TTextField) getComponent("REGMETHOD_CODE")).setEnabled(true);
        ( (TTextField) getComponent("REGMETHOD_DESC")).setEnabled(true);
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

    /**
     * ���ݺ������ƴ������ĸ
     *
     * @return Object
     */

    public Object onCode() {
        if (TCM_Transform.getString(this.getValue("REGMETHOD_DESC")).length() <
            1) {
            return null;
        }
        String value = TMessage.getPy(this.getValueString("REGMETHOD_DESC"));
        if (null == value || value.length() < 1) {
            return null;
        }
        this.setValue("PY1", value);
        // �������
        ( (TTextField) getComponent("PY1")).grabFocus();
        return null;
    }
}
