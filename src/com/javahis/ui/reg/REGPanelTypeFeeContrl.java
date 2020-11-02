package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.event.TTableEvent;
import jdo.reg.PanelTypeFeeTool;
import com.dongyang.manager.TCM_Transform;
import jdo.sys.SysFee;
import com.dongyang.ui.event.TPopupMenuEvent;

/**
 *
 * <p>Title:�ű���ÿ����� </p>
 *
 * <p>Description:�ű���ÿ����� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.16
 * @version 1.0
 */
public class REGPanelTypeFeeContrl
    extends TControl {
    String order_code;
    int selectRow = -1;
    public void onInit() {
        super.onInit();
        callFunction("UI|TABLETYPEFEE|addEventListener",
                     "TABLETYPEFEE->" + TTableEvent.CLICKED, this,
                     "onTABLETYPEFEEClicked");
        //ֻ��text���������������sys_fee������
        callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
                     "%ROOT%\\config\\sys\\SYSFeePopup.x");
        //���ܻش�ֵ
        callFunction("UI|ORDER_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        //��ѯ����
        initData();
    }

    /**
     * ��ѯ����
     */
    public void initData() {
        String s[] = (String[]) getParameter();
        if (s != null) {
            setValue("CLINICTYPE_CODE", s[0]);
            setValue("ADM_TYPE", s[1]);
            callFunction("UI|CLINICTYPE_CODE|setEnabled", false);
            callFunction("UI|ADM_TYPE|setEnabled", false);
        }
        onQuery();
    }

    /**
     * ���¼�������
     */
    public void onInitReset() {
        initData();
    }

    /**
     * ���ô��������б�ѡ��
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE"));
        this.grabFocus("OWN_PRICE");
    }

    /**
     *���Ӷ�TABLETYPEFEE�ļ���
     * @param row int
     */
    public void onTABLETYPEFEEClicked(int row) {

        if (row < 0)
            return;
        //ȡdata�������ݣ����ݵ����ͬ�����ݣ�-------start---------
        TParm data = (TParm) callFunction("UI|TABLETYPEFEE|getParmValue");
        //ȡdata�������ݣ����ݵ����ͬ�����ݣ�-------end---------
        setValueForParm("ADM_TYPE;CLINICTYPE_CODE;ORDER_CODE;RECEIPT_TYPE",
                        data, row);
        selectRow = row;
        order_code = (String) getValue("ORDER_CODE");
        //��ʼ��Combo���ɱ༭
        callFunction("UI|ADM_TYPE|setEnabled", false);
        callFunction("UI|CLINICTYPE_CODE|setEnabled", false);
        callFunction("UI|OWN_PRICE|setEnabled", false);
        //�ű��ܷ���
        selAllPrice();
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = getParmForTag("ADM_TYPE;CLINICTYPE_CODE;ORDER_CODE", true);
        TParm data = PanelTypeFeeTool.getInstance().selectdata(parm);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        int count = data.getCount("ORDER_CODE");
        for (int i = 0; i < count; i++) {
            double own_price = SysFee.getFee(TCM_Transform.getString(
                data.getData("ORDER_CODE", i)));
            //���ӷ�����
            data.addData("OWN_PRICE", own_price);
        }
        this.callFunction("UI|TABLETYPEFEE|setParmValue", data);
    }

    /**
     * ����
     */
    public void onInsert() {
        if (!this.emptyTextCheck("ADM_TYPE,CLINICTYPE_CODE"))
            return;
        TParm parm = getParmForTag(
            "ADM_TYPE;CLINICTYPE_CODE;ORDER_CODE;RECEIPT_TYPE");

        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = PanelTypeFeeTool.getInstance().insertdata(parm);

        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //2008.09.05 --------start--------table�ϼ���������������ʾ
        callFunction("UI|TABLETYPEFEE|addRow", parm,
                     "ADM_TYPE;CLINICTYPE_CODE;ORDER_CODE;RECEIPT_TYPE;OWN_PRICE;OPT_USER;OPT_DATE;OPT_TERM");

        //���������ɹ���ʾ��
        this.messageBox("P0002");

    }

    /**
     * ����
     */
    public void onUpdate() {
        TParm parm = getParmForTag(
            "ADM_TYPE;CLINICTYPE_CODE;ORDER_CODE;RECEIPT_TYPE");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("OLD_ORDER_CODE", order_code);
        TParm result = PanelTypeFeeTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }

        //ˢ�£�����ĩ��ĳ�е�ֵ
        int row = (Integer) callFunction("UI|TABLETYPEFEE|getSelectedRow");
        if (row < 0)
            return;
        //ȡdata�������ݣ����ݵ����ͬ�����ݣ�-------start---------
        TParm data = (TParm) callFunction("UI|TABLETYPEFEE|getParmValue");
        data.setRowData(row, parm);
        //ȡdata�������ݣ����ݵ����ͬ�����ݣ�-------end---------
        callFunction("UI|TABLETYPEFEE|setRowParmValue", row, data);
        this.messageBox("P0001");

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
        if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 2) != 0)
            return;

        if (selectRow == -1)
            return;
        String admType = getValue("ADM_TYPE").toString();
        String clinicTypeCode = getValue("CLINICTYPE_CODE").toString();
        String orderCode = getValue("ORDER_CODE").toString();
        TParm result = PanelTypeFeeTool.getInstance().deletedata(admType,
            clinicTypeCode, orderCode);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //ɾ��������ʾ
        int row = (Integer) callFunction("UI|TABLETYPEFEE|getSelectedRow");
        if (row < 0)
            return;
        this.callFunction("UI|TABLETYPEFEE|removeRow", row);
        this.callFunction("UI|TABLETYPEFEE|setSelectRow", row);

        this.messageBox("P0003");

    }

    /**
     *����ORDER_CODE��ѯ����
     */
    public void onOrderCode() {
        double own_price = SysFee.getFee(TCM_Transform.getString(getValue(
            "ORDER_CODE")));
        this.setValue("OWN_PRICE", own_price);
    }

    /**
     * �ű��ܷ���
     */
    public void selAllPrice() {
        String admType = TCM_Transform.getString(getValue("ADM_TYPE"));
        String clinicType = TCM_Transform.getString(getValue("CLINICTYPE_CODE"));
        TParm getOrderCode = PanelTypeFeeTool.getInstance().getOrderCode(
            admType, clinicType);
        int count = getOrderCode.getCount("ORDER_CODE");
        double allPrice = 0.00;
        for (int i = 0; i < count; i++) {
            double own_price = SysFee.getFee(TCM_Transform.getString(
                getOrderCode.getData("ORDER_CODE", i)));
            allPrice = allPrice + own_price;
        }
        this.setValue("ALL_PRICE", allPrice);
    }

    /**
     *���
     */
    public void onClear() {
        clearValue(
            "ADM_TYPE;CLINICTYPE_CODE;ORDER_CODE;RECEIPT_TYPE;OWN_PRICE;ALL_PRICE");
        this.callFunction("UI|TABLETYPEFEE|clearSelection");
        selectRow = -1;
        callFunction("UI|ADM_TYPE|setEnabled", true);
        callFunction("UI|CLINICTYPE_CODE|setEnabled", true);

    }
}
