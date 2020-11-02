package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import java.sql.Timestamp;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.data.TParm;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTextField;
import jdo.adm.ADMAutoBillTool;
import com.dongyang.ui.TLabel;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import java.util.Vector;
import jdo.bil.BIL;

/**
 *
 * <p>Title: �Զ��Ʒ��շ���Ŀ</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author JiaoY 2008.12.18 test
 * @version 1.0
 */
public class ADMAutoBillControl extends TControl {
    TParm data;
    int selectRow = -1;
    OrderList orderDesc;

    public void onInit() {
        super.onInit();
        ((TTable) getComponent("Table")).addEventListener("Table->"
                + TTableEvent.CLICKED, this, "onTableClicked");
        //ģ����ѯ -------start---------
        orderDesc = new OrderList();
        TTable table = (TTable)this.getComponent("Table");
        table.addItem("ORDER_LIST", orderDesc);
        //ģ����ѯ -------end---------

        //ֻ��text���������������sys_fee������
        callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
                     "%ROOT%\\config\\sys\\SYSFeePopup.x");

        //textfield���ܻش�ֵ
        callFunction("UI|ORDER_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        onClear();
    }

    /**
     * ����¼�
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE"));
        this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
        this.setValue("UNIT_CODE", parm.getValue("UNIT_CODE"));
        this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
        this.setValue("DOSEAGE_QTY",1);
        //�����ܷ���
        onQty();
        this.grabFocus("DOSEAGE_QTY");
    }

    /**
     * ģ����ѯ
     */
    public class OrderList extends TLabel {
        TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                                  dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("ORDER_CODE");
            Vector d = (Vector) parm.getData("ORDER_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i)))
                    return "" + d.get(i);
            }
            return s;
        }
    }


    /**
     * �����������
     * @return boolean
     */
    public boolean checkdata() {
       String startMonth = getValue("START_MONTH").toString();
        if (getValue("START_MONTH") == null || "".equals(getValue("START_MONTH"))||Integer.parseInt(startMonth)==0) {
            this.messageBox_("��ʼ�·ݲ���Ϊ0��");
            return false;
        }
        if (getValue("START_DAY") == null || "".equals(getValue("START_DAY"))||Integer.parseInt(getValue("START_DAY").toString())==0) {
            this.messageBox_("��ʼ���ڲ���Ϊ0��");
            return false;
        }
        if (getValue("END_MONTH") == null || "".equals(getValue("END_MONTH"))||Integer.parseInt(getText("END_MONTH"))==0) {
            this.messageBox_("�����·ݲ���Ϊ0��");
            return false;
        }
        if (getValue("END_DAY") == null || "".equals(getValue("END_DAY"))||Integer.parseInt(getText("END_DAY"))==0) {
            this.messageBox_("�������ڲ���Ϊ0��");
            return false;
        }
        //�˶����ڵ���ȷ��
        int month_s = this.getValueInt("START_MONTH");//�·���ʼ
        int day_s = this.getValueInt("START_DAY");//��������
        int month_e = this.getValueInt("END_MONTH");//��ֹ�·�
        int day_e = this.getValueInt("END_DAY");//��ֹ����
        if(month_s<1||month_s>12){
            this.messageBox_("��ʼ�·ݲ���ȷ");
            return false;
        }
        if(month_e<1||month_e>12){
            this.messageBox_("��ֹ�·ݲ���ȷ");
            return false;
        }
        //�����·��ж����ڷ�Χ
        if(month_s==1||month_s==3||month_s==5||month_s==7||month_s==8||month_s==10||month_s==12){
            if(day_s<1||day_s>31){
                this.messageBox_("��ʼ���ڳ�����Χ");
                return false;
            }
        }
        //�����·��ж����ڷ�Χ
        if(month_s==4||month_s==6||month_s==9||month_s==11){
            if(day_s<1||day_s>30){
                this.messageBox_("��ʼ���ڳ�����Χ");
                return false;
            }
        }
        if(month_s==2){
            if(day_s<1||day_s>29){
                this.messageBox_("��ʼ���ڳ�����Χ");
                return false;
            }
        }
        //�����·��ж����ڷ�Χ
        if(month_e==1||month_e==3||month_e==5||month_e==7||month_e==8||month_e==10||month_e==12){
            if(day_e<1||day_e>31){
                this.messageBox_("��ֹ���ڳ�����Χ");
                return false;
            }
        }
        //�����·��ж����ڷ�Χ
        if(month_e==4||month_e==6||month_e==9||month_e==11){
            if(day_e<1||day_e>30){
                this.messageBox_("��ֹ���ڳ�����Χ");
                return false;
            }
        }
        if(month_e==2){
            if(day_e<1||day_e>29){
                this.messageBox_("��ֹ���ڳ�����Χ");
                return false;
            }
        }
        if(Integer.parseInt(getText("DOSEAGE_QTY"))<=0){
            this.messageBox_("�����������0��");
            return false;
        }
        return true;
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
                "ORDER_CODE;DOSEAGE_QTY;UNIT_CODE;SUM_PRICE;OCCUFEE_FLG;BABY_FLG",
                data, row);
        setValue("START_MONTH", data.getValue("START_DATE", row).substring(0, 2));
        setValue("START_DAY", data.getValue("START_DATE", row).substring(2));
        setValue("END_MONTH", data.getValue("END_DATE", row).substring(0, 2));
        setValue("END_DAY", data.getValue("END_DATE", row).substring(2));
        setValue("ORDER_DESC",
                 orderDesc.getTableShowValue(getValue("ORDER_CODE").toString()));
        selectRow = row;
        // ���ɱ༭
        ((TTextField) getComponent("ORDER_CODE")).setEnabled(false);
        // ����ɾ����ť״̬
        ((TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * �����ܼ�
     */
    public void onQty() {
        String orderCode = this.getValueString("ORDER_CODE");
        double qty = Double.parseDouble(this.getValue("DOSEAGE_QTY").toString());
        BIL bil = new BIL();
//        double price = bil.getFee(orderCode);
        double sumPrice = bil.getFee(orderCode, qty);
        this.setValue("SUM_PRICE",sumPrice);
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("ORDER_CODE", getValue("ORDER_CODE"));
        data = ADMAutoBillTool.getInstance().queryData(parm);
        // �жϴ���ֵ
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ((TTable) getComponent("TABLE")).setParmValue(data);

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
     * ����
     */
    public void onInsert() {
        if (!emptyTextCheck("ORDER_CODE,ORDER_DESC")) {
            return;
        }
        if (!this.checkdata()) {
            return;
        }
        BIL bil = new BIL();
        TParm parm = getParmForTag(
                "ORDER_CODE;UNIT_CODE;DOSEAGE_QTY;OCCUFEE_FLG;BABY_FLG");
        String month = getValue("START_MONTH").toString().trim();
        String activeMonth = month.length()==1?"0"+month:month;
        String day = getValue("START_DAY").toString().trim();
        String activeDay=day.length()==1?"0"+day:day;
        parm.setData("START_DATE",
                     activeMonth+activeDay);
        month = getValue("END_MONTH").toString().trim();
        activeMonth = month.length()==1?"0"+month:month;
        day = getValue("END_DAY").toString().trim();
        activeDay=day.length()==1?"0"+day:day;
        parm.setData("END_DATE",activeMonth+activeDay);
        parm.setData("PRICE", bil.getFee(getValue("ORDER_CODE").toString()));
        parm.setData("SUM_PRICE",
                     bil.getFee(getValue("ORDER_CODE").toString(),
                                Double.
                                parseDouble(this.getValue("DOSEAGE_QTY").toString())));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = ADMAutoBillTool.getInstance().insertdata(parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        this.onClear();
        this.messageBox("P0001");
    }

    /**
     * ����
     */
    public void onUpdate() {
        if (!this.checkdata()) {
            return;
        }
        TParm parm = getParmForTag(
            "ORDER_CODE;DOSEAGE_QTY;OCCUFEE_FLG;BABY_FLG");
        String month = getValue("START_MONTH").toString().trim();
        String activeMonth = month.length() == 1 ? "0" + month : month;
        String day = getValue("START_DAY").toString().trim();
        String activeDay = day.length() == 1 ? "0" + day : day;

        parm.setData("START_DATE", activeMonth + activeDay);

        month = getValue("END_MONTH").toString().trim();
        activeMonth = month.length() == 1 ? "0" + month : month;
        day = getValue("END_DAY").toString().trim();
        activeDay = day.length() == 1 ? "0" + day : day;

        parm.setData("SUM_PRICE", this.getValue("SUM_PRICE"));
        parm.setData("END_DATE", activeMonth + activeDay);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = ADMAutoBillTool.getInstance().updatedata(parm);
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
     * ɾ��
     */
    public void onDelete() {
        if (this.messageBox("��ʾ", "�Ƿ�ɾ��", 2) == 0) {
            if (selectRow == -1)
                return;
            String orderCode = getValue("ORDER_CODE").toString();
            TParm result = ADMAutoBillTool.getInstance().deletedata(orderCode);
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
     * ���
     */
    public void onClear() {
        clearValue("ORDER_CODE;ORDER_DESC;DOSEAGE_QTY;UNIT_CODE;SUM_PRICE;OCCUFEE_FLG;BABY_FLG;START_MONTH;START_DAY;END_MONTH;END_DAY");
        ((TTable) getComponent("Table")).clearSelection();
        selectRow = -1;
        ((TTextField) getComponent("ORDER_CODE")).setEnabled(true);
        // ����ɾ����ť״̬
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        onQuery();
    }
}

