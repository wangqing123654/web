package com.javahis.ui.nss;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.nss.NSSMealTool;
import com.dongyang.ui.TTextField;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.util.TMessage;

/**
 * <p>Title: �ʹ����õ�</p>
 *
 * <p>Description: �ʹ����õ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSMealControl
    extends TControl {
    public NSSMealControl() {
        super();
    }

    private TTable table;

    /**
     * ��ʼ������
     */
    public void onInit() {
        table = getTable("TABLE");
        onQuery();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        String meal_code = this.getValueString("MEAL_CODE");
        if (meal_code != null && meal_code.length() > 0) {
            parm.setData("MEAL_CODE", meal_code);
        }
        TParm result = NSSMealTool.getInstance().onQueryNSSMeal(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
        }
        else {
            String meal_time = "";
            String stop_order_time = "";
            for (int i = 0; i < result.getCount(); i++) {
                meal_time = result.getValue("MEAL_TIME", i);
                result.setData("MEAL_TIME", i,
                               meal_time.substring(0, 2) + ":" +
                               meal_time.substring(2, 4));
                stop_order_time = result.getValue("STOP_ORDER_TIME", i);
                result.setData("STOP_ORDER_TIME", i,
                               stop_order_time.substring(0, 2) + ":" +
                               stop_order_time.substring(2, 4));
            }
            table.setParmValue(result);
        }
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (!checkData()) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("MEAL_CODE", this.getValueString("MEAL_CODE"));
        parm.setData("MEAL_CHN_DESC", this.getValueString("MEAL_CHN_DESC"));
        parm.setData("MEAL_ENG_DESC", this.getValueString("MEAL_ENG_DESC"));
        parm.setData("PY1", this.getValueString("PY1"));
        parm.setData("PY2", this.getValueString("PY2"));
        parm.setData("SEQ", this.getValueInt("SEQ"));
        parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
        String meal_time = this.getValueString("MEAL_TIME");
        parm.setData("MEAL_TIME",
                     meal_time.substring(11, 13) + meal_time.substring(14, 16));
        String stop_order_time = this.getValueString("STOP_ORDER_TIME");
        parm.setData("STOP_ORDER_TIME",
                     stop_order_time.substring(11, 13) +
                     stop_order_time.substring(14, 16));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        //System.out.println("parm----"+parm);
        TParm result = new TParm();
        if ( ( (TTextField) getComponent("MEAL_CODE")).isEnabled()) {
            // ��������
            result = NSSMealTool.getInstance().onInsertNSSMeal(parm);
        }
        else {
            // ��������
            result = NSSMealTool.getInstance().onUpdateNSSMeal(parm);
        }
        if (result.getErrCode() < 0) {
            this.messageBox("E0001");
        }
        else {
            this.messageBox("P0001");
            onQuery();
        }
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ��ɾ������");
        }
        TParm parm = table.getParmValue().getRow(row);
        TParm result = NSSMealTool.getInstance().onDeleteNSSMeal(parm);
        if (result.getErrCode() < 0) {
            this.messageBox("ɾ��ʧ��");
        }
        else {
            this.messageBox("ɾ���ɹ�");
            table.removeRow(row);
        }
    }

    /**
     * ��շ���
     */
    public void onClear() {
        this.clearValue("MEAL_CODE;MEAL_CHN_DESC;MEAL_ENG_DESC;PY1;"
                        + "PY2;SEQ;DESCRIPTION;MEAL_TIME;STOP_ORDER_TIME");
        table.removeRowAll();
        ( (TTextField) getComponent("MEAL_CODE")).setEnabled(true);
    }

    /**
     * ��񵥻��¼�
     */
    public void onTableClick() {
        this.setValueForParm("MEAL_CODE;MEAL_CHN_DESC;MEAL_ENG_DESC;PY1;"
                             + "PY2;SEQ;DESCRIPTION;MEAL_TIME;STOP_ORDER_TIME",
                             table.getParmValue().getRow(table.getSelectedRow()));
        ( (TTextField) getComponent("MEAL_CODE")).setEnabled(false);
    }

    /**
     * �ʹ�����˵���س��¼�
     */
    public void onMealDescAction(){
        String py = TMessage.getPy(this.getValueString("MEAL_CHN_DESC"));
        setValue("PY1", py);
    }

    /**
     * ���ݼ��
     * @return boolean
     */
    private boolean checkData() {
        String meal_code = this.getValueString("MEAL_CODE");
        if (meal_code == null || meal_code.length() <= 0) {
            this.messageBox("�ʹδ��벻��Ϊ��");
            return false;
        }
        String meal_chn_desc = this.getValueString("MEAL_CHN_DESC");
        if (meal_chn_desc == null || meal_chn_desc.length() <= 0) {
            this.messageBox("�ʹ�����˵������Ϊ��");
            return false;
        }
        String meal_time = this.getValueString("MEAL_TIME");
        if (meal_time == null || meal_time.length() <= 0) {
            this.messageBox("�ò�ʱ�䲻��Ϊ��");
            return false;
        }
        String stop_order_time = this.getValueString("STOP_ORDER_TIME");
        if (stop_order_time == null || stop_order_time.length() <= 0) {
            this.messageBox("ֹͣ����ʱ�䲻��Ϊ��");
            return false;
        }
        return true;
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
