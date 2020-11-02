package com.javahis.ui.sys;

import jdo.sys.Operator;
import jdo.sys.SYSPhaRouteTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;

/**
 *
 * <p>
 * Title: ��ҩ;��
 * </p>
 *
 * <p>
 * Description:��ҩ;��
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 20080901
 * @version 1.0
 */
public class SYSPhaRouteControl
    extends TControl {

    TParm data, comboldata;
    int selectRow = -1;

    public void onInit() {
        super.onInit();
        callFunction("UI|TABLE|addEventListener", "TABLE->"
                     + TTableEvent.CLICKED, this, "onTABLEClicked");
        initPhaRoute();

        // ���õ����˵�
        getTextField("LINK_ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"));
        // ������ܷ���ֵ����
        getTextField("LINK_ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
    }

    /**
    * ���ܷ���ֵ����
    *
    * @param tag
    * @param obj
    */
   public void popReturn(String tag, Object obj) {
       TParm parm = (TParm) obj;
       String order_code = parm.getValue("ORDER_CODE");
       String order_desc = parm.getValue("ORDER_DESC");
       if (!StringUtil.isNullString(order_code)) {
           this.setValue("LINK_ORDER_CODE", order_code);
           this.setValue("ORDER_DESC", order_desc);
       }
   }


    /**
     * ��ʼ�����棬��ѯ���е�����
     *
     * @return TParm
     */
    public void onQuery() {
        String ROUTE_CODE = getText("ROUTE_CODE");
        if (ROUTE_CODE == null || "".equals(ROUTE_CODE)) {
            // System.out.println("ROUTE_CODE" + ROUTE_CODE);
            init();
        }
        else {
            data = SYSPhaRouteTool.getInstance().selectdata(ROUTE_CODE);
            // System.out.println("result:" + data);
            if (data.getErrCode() < 0) {
                messageBox(data.getErrText());
                return;
            }
            this.callFunction("UI|TABLE|setParmValue", data);
        }

    }

    public void onTABLEClicked(int row) {
        if (row < 0) {
            return;
        }
        setValueForParm(
            "SEQ;ROUTE_CODE;ROUTE_CHN_DESC;PY1;PY2;WESMED_FLG;CHIMED_FLG;ROUTE_ENG_DESC;DESCRIPTION;LINK_ORDER_CODE;ORDER_DESC;IGNORE_FLG;PS_FLG;CLASSIFY_TYPE;IVA_FLG;DOSAGE_PRN_TOT;IVA_FLG",
            data, row);

        selectRow = row;
        callFunction("UI|ROUTE_CODE|setEnabled", false);
        if (selectRow != -1) {
            this.callFunction("delete|setEnable", true);
        }

    }

    /**
     * ���
     */
    public void onClear() {
        // System.out.println("new");

        this.clearValue("SEQ;ROUTE_CODE;ROUTE_CHN_DESC;PY1;PY2;WESMED_FLG;CHIMED_FLG;ROUTE_ENG_DESC;DESCRIPTION;ORDER_DESC;LINK_ORDER_CODE;IGNORE_FLG;PS_FLG;CLASSIFY_TYPE;IVA_FLG;DOSAGE_PRN_TOT;IVA_FLG");
        // System.out.println("old");
        //callFunction("UI|TABLE|removeRowAll");
        this.setValue("SEQ", "0");
        this.setValue("WESMED_FLG", "Y");
        selectRow = -1;
        callFunction("UI|ROUTE_CODE|setEnabled", true);
        //initcombol();
        initPhaRoute();
    }

    /**
     * ��ʼ�����棬��ѯ���е�����
     *
     * @return TParm
     */
    public void initPhaRoute() {
        data = SYSPhaRouteTool.getInstance().selectall();
        this.callFunction("UI|TABLE|setParmValue", data);
        if (selectRow == -1) {
            this.callFunction("delete|setEnable", false);
        }
    }

    /**
     * ����
     */
    public void onInsert() {
        if (!this.emptyTextCheck("ROUTE_CODE,ROUTE_CHN_DESC")) {
            return;
        }
        TParm parm = getParmForTag("SEQ:int;ROUTE_CODE;ROUTE_CHN_DESC;PY1;PY2;WESMED_FLG;CHIMED_FLG;ROUTE_ENG_DESC;DESCRIPTION;LINK_ORDER_CODE;IGNORE_FLG;PS_FLG;CLASSIFY_TYPE;IVA_FLG;DOSAGE_PRN_TOT;IVA_FLG");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        SystemTool st = new SystemTool();
        parm.setData("OPT_DATE", st.getDate());
        // System.out.println("pram" + parm);
        data = SYSPhaRouteTool.getInstance().insertdata(parm);
        if (data.getErrCode() < 0) {
            this.messageBox("E0002");
            onClear();
            init();
        }
        else {
            this.messageBox("P0002");
            onClear();
            init();
        }
    }

    /**
     * ����
     */
    public void onUpdate() {
        if (!this.emptyTextCheck("ROUTE_CODE,ROUTE_CHN_DESC")) {
            return;
        }
        // System.out.println("update");
        TParm parm = getParmForTag("SEQ:int;ROUTE_CODE;ROUTE_CHN_DESC;PY1;PY2;WESMED_FLG;CHIMED_FLG;ROUTE_ENG_DESC;DESCRIPTION;LINK_ORDER_CODE;IGNORE_FLG;PS_FLG;CLASSIFY_TYPE;IVA_FLG;DOSAGE_PRN_TOT;IVA_FLG");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        SystemTool st = new SystemTool();
        parm.setData("OPT_DATE", st.getDate());
        // System.out.println("pram" + parm);
        TParm result = SYSPhaRouteTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
        if (row < 0) {
            this.messageBox("E0001");
            onClear();
            init();
        }
        else {
            this.messageBox("P0001");
            onClear();
            init();
        }
        // ����ĩ��ĳ�е�ֵ
        // callFunction("UI|TABLE|setValueAt",getText("POS_DESC"),row,1);
        // callFunction("UI|TABLE|setValueAt",callFunction("UI|POS_TYPE|getSelectedID"),row,2);
    }

    /**
     * ����
     */
    public void onSave() {
        if (selectRow == -1) {
            // if(!this.emptyTextCheck("ROUTE_CODE")){
            // return;
            // }
            onInsert();
            return;
        }
        onUpdate();
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        // if(selectRow == -1)
        // return;
        if (!this.emptyTextCheck("ROUTE_CODE")) {
            return;
        }
        String ROUTE_CODE = getText("ROUTE_CODE");
        if (this.messageBox("ѯ��", "ȷ��ɾ��", 2) == 0) {
            data = SYSPhaRouteTool.getInstance().deletedata(ROUTE_CODE);
        }
        else {
            return;
        }

        if (data.getErrCode() < 0) {
            this.messageBox("E0003");
            onClear();
            init();
            return;
        }
        int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
        if (row < 0) {
            this.messageBox("E0003");
            onClear();
            init();
        }
        else {
            this.messageBox("P0003");
            onClear();
            init();
        }

    }

    public void onChartoCode() {
        if ("".equals(String.valueOf(this.getValue("ROUTE_CHN_DESC")))) {
            return;
        }
        SystemTool st = new SystemTool();
        String value = st.charToCode(String.valueOf(this
            .getValue("ROUTE_CHN_DESC")));
        if (null == value || "".equals(value)) {
            this.setValue("PY1", value);
            return;
        }
        this.setValue("PY1", value);
    }

    /**
     * �趨ƴ��
     */
    public void onDescAction() {
        String py = TMessage.getPy(this.getValueString("ROUTE_CHN_DESC"));
        setValue("PY1", py);
        getTCheckBox("WESMED_FLG").grabFocus();
    }

    /**
     * �õ�ComboBox����
     *
     * @param tagName
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * �õ�TCheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getTCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }
}
