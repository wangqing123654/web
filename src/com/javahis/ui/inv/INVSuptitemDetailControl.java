package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.ui.TTextFormat;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TNumberTextField;
import jdo.inv.InvSuptitemDetailTool;
import com.dongyang.util.TypeTool;
import jdo.util.Manager;

/**
 * <p>Title: ���������ϸ</p>
 *
 * <p>Description: ���������ϸ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 *
 * @author zhangy 2010.3.27
 * @version 1.0
 */
public class INVSuptitemDetailControl
    extends TControl {

    private TTable table;

    public INVSuptitemDetailControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        //��Ŀ����
        table = (TTable) getComponent("TABLE");
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        this.setValue("CASHIER_CODE", Operator.getID());
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (!checkData()) {
            return;
        }
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("SUP_DETAIL_SEQ", this.getValueInt("SUP_DETAIL_SEQ"));
        parm.setData("SUP_DATE", SystemTool.getInstance().getDate());
        parm.setData("USE_DEPT", this.getValueString("USE_DEPT"));
        parm.setData("SUPITEM_CODE", this.getValueString("SUPITEM_CODE"));
        parm.setData("QTY", this.getValueDouble("QTY"));
        parm.setData("COST_PRICE", this.getValueDouble("COST_PRICE"));
        parm.setData("ADD_PRICE", this.getValueDouble("ADD_PRICE"));
        parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
        parm.setData("CASHIER_CODE", this.getValueString("CASHIER_CODE"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TTextFormat format = (TTextFormat)this.getComponent("SUPITEM_CODE");
        parm.setData("SUPITEM_DESC", format.getComboValue("SUPITEM_DESC"));

        if ( ( (TTextField) getComponent("SUP_DETAIL_NO")).isEnabled()) {
            //��������
            parm.setData("SUP_DETAIL_NO",
                         SystemTool.getInstance().getNo("ALL", "INV", "SUPDETAIL", "No"));
            result = InvSuptitemDetailTool.getInstance().onInsert(parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            table.setSelectedRow(table.addRow(parm));
            onClickedTable();
            this.onPrint();
            this.messageBox("P0001");
        }
        else {
            //���·���
            parm.setData("SUP_DETAIL_NO", this.getValueString("SUP_DETAIL_NO"));
            result = InvSuptitemDetailTool.getInstance().onUpdate(parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            onQuery();
            this.messageBox("P0001");
        }
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        TParm parm = table.getParmValue().getRow(row);
        TParm result = InvSuptitemDetailTool.getInstance().onDelete(parm);
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("ɾ��ʧ��");
            return;
        }
        this.messageBox("ɾ���ɹ�");
        table.removeRow(row);
        this.clearValue("SUP_DETAIL_NO;SUP_DETAIL_SEQ;USE_DEPT;SUPITEM_CODE;"
            + "QTY;COST_PRICE;ADD_PRICE;DESCRIPTION");
        ( (TTextField) getComponent("SUP_DETAIL_NO")).setEnabled(true);
        ( (TNumberTextField) getComponent("SUP_DETAIL_SEQ")).setEnabled(true);
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        // ��ѯʱ��
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
            parm.setData("START_DATE", this.getValue("START_DATE"));
            parm.setData("END_DATE", this.getValue("END_DATE"));
        }
        if (!"".equals(this.getValueString("SUP_DETAIL_NO"))) {
            parm.setData("SUP_DETAIL_NO", this.getValueString("SUP_DETAIL_NO"));
        }
        parm.setData("SUP_DETAIL_SEQ", this.getValueInt("SUP_DETAIL_SEQ"));
        if (!"".equals(this.getValueString("USER_DEPT"))) {
            parm.setData("USE_DEPT", this.getValueString("USE_DEPT"));
        }
        if (!"".equals(this.getValueString("SUPITEM_CODE"))) {
            parm.setData("SUPITEM_CODE", this.getValueString("SUPITEM_CODE"));
        }
        // ���ݲ�ѯ
        TParm result = InvSuptitemDetailTool.getInstance().onQuery(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            table.removeRowAll();
            return;
        }
        table.setParmValue(result);
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "��Ӧ��������˵�");
        date.setData("SUP_DETAIL_NO", "TEXT",
                     "���˵���:" + getValueString("SUP_DETAIL_NO"));
        date.setData("USE_DEPT", "TEXT",
                     "���˿���:" + getTextFormat("USE_DEPT").getText());
        date.setData("SUP_DATE", "TEXT", "����ʱ��:" +
                     StringTool.getString(table.getItemTimestamp(row, "SUP_DATE"),
                                          "yyyy/MM/dd HH:mm:ss"));
        date.setData("CASHIER_CODE", "TEXT",
                     "�շ�����:" + getTextFormat("CASHIER_CODE").getText());
        TParm parm = new TParm();
        TTextFormat format = (TTextFormat)this.getComponent("SUPITEM_CODE");
        parm.addData("SUPITEM_DESC", format.getComboValue("SUPITEM_DESC").toString());
        parm.addData("QTY", this.getValueDouble("QTY"));
        parm.addData("COST_PRICE", this.getValueDouble("COST_PRICE"));
        parm.addData("ADD_PRICE", this.getValueDouble("ADD_PRICE"));
        parm.addData("USER_NAME", "");
        parm.addData("DESCRIPTION", this.getValueString("DESCRIPTION"));
        parm.setCount(1);
        parm.addData("SYSTEM", "COLUMNS", "SUPITEM_DESC");
        parm.addData("SYSTEM", "COLUMNS", "QTY");
        parm.addData("SYSTEM", "COLUMNS", "COST_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "ADD_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "USER_NAME");
        parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
        date.setData("TABLE", parm.getData());
        date.setData("OPT_USER", "TEXT", "������:" + Operator.getName());
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVSuptitemDetall.jhw",
                             date);
    }

    /**
     * ���
     */
    public void onClear() {
        this.clearValue("SUP_DETAIL_NO;SUP_DETAIL_SEQ;USE_DEPT;SUPITEM_CODE;"
            + "QTY;COST_PRICE;ADD_PRICE;DESCRIPTION");
        ( (TTextField) getComponent("SUP_DETAIL_NO")).setEnabled(true);
        ( (TNumberTextField) getComponent("SUP_DETAIL_SEQ")).setEnabled(true);
        table.removeRowAll();
    }

    /**
     * ��񵥻��¼�
     */
    public void onClickedTable() {
        int row = table.getSelectedRow();
        TParm parm = table.getParmValue().getRow(row);
        this.setValueForParm("SUP_DETAIL_NO;SUP_DETAIL_SEQ;USE_DEPT;"
                             + "SUPITEM_CODE;QTY;COST_PRICE;ADD_PRICE;"
                             + "DESCRIPTION", parm);
        ( (TTextField) getComponent("SUP_DETAIL_NO")).setEnabled(false);
        ( (TNumberTextField) getComponent("SUP_DETAIL_SEQ")).setEnabled(false);
    }

    /**
     * ������Ŀ����¼�
     */
    public void onSupitemCodeSelected() {
        TTextFormat format = (TTextFormat)this.getComponent("SUPITEM_CODE");
        double qty = this.getValueDouble("QTY");
        if (qty <= 0) {
            qty = 1;
        }
        this.setValue("COST_PRICE",
                      TypeTool.getDouble(format.getComboValue("COST_PRICE")) * qty);
        this.setValue("ADD_PRICE",
                      TypeTool.getDouble(format.getComboValue("ADD_PRICE")) * qty);
    }

    /**
     * ������������¼�
     */
    public void onChangeQty() {
        double qty = this.getValueDouble("QTY");
        TTextFormat format = (TTextFormat)this.getComponent("SUPITEM_CODE");
        this.setValue("COST_PRICE",
                      Double.parseDouble(format.getComboValue("COST_PRICE").
                                         toString()) * qty);
        this.setValue("ADD_PRICE",
                      Double.parseDouble(format.getComboValue("ADD_PRICE").
                                         toString()) * qty);
    }

    /**
     * ���ݼ��
     * @return boolean
     */
    private boolean checkData() {
        if ("".equals(this.getValueString("USE_DEPT"))) {
            this.messageBox("���˿��Ҳ���Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("SUPITEM_CODE"))) {
            this.messageBox("������Ŀ����Ϊ��");
            return false;
        }
        if (this.getValueDouble("QTY") <= 0) {
            this.messageBox("������������С�ڵ���0");
            return false;
        }
        return true;
    }

    /**
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

}
