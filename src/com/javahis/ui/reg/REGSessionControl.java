package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import jdo.reg.SessionTool;
import jdo.sys.Operator;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.event.TPopupMenuEvent;
import jdo.sys.SysFee;
import com.dongyang.util.TMessage;
import com.dongyang.ui.TTextField;
import java.util.Vector;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title:�Һ�ʱ�ο����� </p>
 *
 * <p>Description:�Һ�ʱ�ο����� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.22
 * @version 1.0
 */
public class REGSessionControl
    extends TControl {
//    TParm data;
    int selectRow = -1;
    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
        //ֻ��text���������������sys_fee������
        callFunction("UI|VALUEADD_CODE|setPopupMenuParameter", "ORDER_CODELIST",
                     "%ROOT%\\config\\sys\\SYSFeePopup.x");
        //���ܻش�ֵ
        callFunction("UI|VALUEADD_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        onClear();
        //========pangben modify 20110421 start Ȩ�����
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop

    }

    /**
     * ���ô��������б�ѡ��
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
//        System.out.println("parm"+parm);
        this.setValue("VALUEADD_CODE", parm.getValue("ORDER_CODE"));
        this.setValue("DESC", parm.getValue("OWN_PRICE"));
        this.setValue("VALUEADD_DESC", parm.getValue("ORDER_DESC"));
        this.grabFocus("DESC");
    }

    /**
     * ����ORDER_CODE��ѯ����
     * @param orderCode String
     * @return double
     */
    public double onOrderCode(String orderCode) {
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
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {

        if (row < 0)
            return;
        TParm data = (TParm) callFunction("UI|Table|getParmValue");
        setValueForParm("ADM_TYPE;REGION_CODE;SESSION_CODE;SESSION_DESC;PY1;" +
                        "PY2;SEQ;DESCRIPTION;" +
                        "VALUEADD_CODE;VALUEADD_DESC",
                        data, row);
        //===============pangben modify 20110602 ʱ������ת��
        //�Һ���ʱ
        String startTime = data.getValue("START_REG_TIME", row);
        Timestamp sTime = StringTool.getTimestamp(startTime, "HH:mm:ss");
        this.setValue("START_REG_TIME", sTime);
        //�ҺŽ���
        String endTime = data.getValue("END_REG_TIME", row);
        Timestamp eTime = StringTool.getTimestamp(endTime, "HH:mm:ss");
        this.setValue("END_REG_TIME", eTime);
        //������ʱ
        String startClinicTime = data.getValue("START_CLINIC_TIME", row);
        Timestamp sClinicTime = StringTool.getTimestamp(startClinicTime, "HH:mm:ss");
        this.setValue("START_CLINIC_TIME", sClinicTime);
        //������ʱ
        String endClinicTime = data.getValue("END_CLINIC_TIME", row);
        Timestamp eClinicTime = StringTool.getTimestamp(endClinicTime, "HH:mm:ss");
        this.setValue("END_CLINIC_TIME", eClinicTime);
        //����ʱ��
        String valueaddTime = data.getValue("VALUEADD_TIME", row);
        Timestamp vaddTime = StringTool.getTimestamp(valueaddTime, "HH:mm:ss");
        this.setValue("VALUEADD_TIME", vaddTime);
        //===============pangben modify 20110602 stop
        this.setValue("DESC", onOrderCode(data.getValue("VALUEADD_CODE", row)));
        this.setValue("VALUEADD_DESC",
                      getOrderDesc(data.getValue("VALUEADD_CODE", row)));
        selectRow = row;
        //��ʼ��Combo���ɱ༭
        callFunction("UI|ADM_TYPE|setEnabled", false);
        callFunction("UI|REGION_CODE|setEnabled", false);
        callFunction("UI|SESSION_CODE|setEnabled", false);
        callFunction("UI|DESC|setEnabled", false);

    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        out("begin");
        String admType = getValue("ADM_TYPE").toString();
        String regionCode = getValue("REGION_CODE").toString();
        String sessionCode = getValue("SESSION_CODE").toString();

        TParm data = SessionTool.getInstance().selectdata(admType, regionCode,
            sessionCode);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        //out("data=" + data);
        this.callFunction("UI|Table|setParmValue", data);
        out("end");

    }

    /**
     * ����
     */
    public void onInsert() {
        TParm parm = getParmForTag(
            "ADM_TYPE;REGION_CODE;SESSION_CODE;SESSION_DESC;PY1;" +
            "PY2;SEQ;DESCRIPTION;VALUEADD_CODE;VALUEADD_DESC");
        //=====pangben modify 20110602 start ��ʽת��
        String START_CLINIC_TIME = StringTool.getString(TypeTool.getTimestamp(this.getValue("START_CLINIC_TIME")) ,"HH:mm:ss");
        String END_CLINIC_TIME = StringTool.getString(TypeTool.getTimestamp(this.getValue("END_CLINIC_TIME")) ,"HH:mm:ss");
        String START_REG_TIME = StringTool.getString(TypeTool.getTimestamp(this.getValue("START_REG_TIME")) ,"HH:mm:ss");
        String END_REG_TIME = StringTool.getString(TypeTool.getTimestamp(this.getValue("END_REG_TIME")) ,"HH:mm:ss");
        String VALUEADD_TIME = StringTool.getString(TypeTool.getTimestamp(this.getValue("VALUEADD_TIME")) ,"HH:mm:ss");
        parm.setData("START_CLINIC_TIME", START_CLINIC_TIME);
        parm.setData("END_CLINIC_TIME",END_CLINIC_TIME);
        parm.setData("START_REG_TIME", START_REG_TIME);
        parm.setData("END_REG_TIME", END_REG_TIME);
        parm.setData("VALUEADD_TIME", VALUEADD_TIME);
         //=====pangben modify 20110602 stop
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());

        TParm result = SessionTool.getInstance().insertdata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }

        this.messageBox("P0001");

    }

    /**
     * ����
     */
    public void onUpdate() {
        TParm parm = getParmForTag("ADM_TYPE;REGION_CODE;SESSION_CODE;SESSION_DESC;PY1;"+
                                   "PY2;SEQ;DESCRIPTION;"+
                                   "VALUEADD_CODE;VALUEADD_DESC");
        //=====pangben modify 20110602 start ��ʽת��
        String START_CLINIC_TIME = StringTool.getString(TypeTool.getTimestamp(this.getValue("START_CLINIC_TIME")) ,"HH:mm:ss");
        String END_CLINIC_TIME = StringTool.getString(TypeTool.getTimestamp(this.getValue("END_CLINIC_TIME")) ,"HH:mm:ss");
        String START_REG_TIME = StringTool.getString(TypeTool.getTimestamp(this.getValue("START_REG_TIME")) ,"HH:mm:ss");
        String END_REG_TIME = StringTool.getString(TypeTool.getTimestamp(this.getValue("END_REG_TIME")) ,"HH:mm:ss");
        String VALUEADD_TIME = StringTool.getString(TypeTool.getTimestamp(this.getValue("VALUEADD_TIME")) ,"HH:mm:ss");
        parm.setData("START_CLINIC_TIME", START_CLINIC_TIME);
        parm.setData("END_CLINIC_TIME",END_CLINIC_TIME);
        parm.setData("START_REG_TIME", START_REG_TIME);
        parm.setData("END_REG_TIME", END_REG_TIME);
        parm.setData("VALUEADD_TIME", VALUEADD_TIME);
         //=====pangben modify 20110602 stop

        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = SessionTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        int row = (Integer) callFunction("UI|Table|getSelectedRow");
        if (row < 0)
            return;
        /**
         * ����data��ŵ�ֵ
         * 20080819 wangl------------start--------
         */
        //ˢ�£�����ĩ��ĳ�е�ֵ
        TParm data = (TParm) callFunction("UI|Table|getParmValue");
        data.setRowData(row, parm);
        callFunction("UI|Table|setRowParmValue", row, data);
//        20080819 wangl------------end--------
        this.messageBox("P0005");

    }

    /**
     * ����
     */
    public void onSave() {
        if (selectRow == -1) {
            onInsert();
            onClear();
            return;
        }
        onUpdate();
        onClear();
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 2) == 0) {
            if (selectRow == -1)
                return;
            String admType = getValue("ADM_TYPE").toString();
            String regionCode = getValue("REGION_CODE").toString();
            String sessionCode = getValue("SESSION_CODE").toString();

            TParm result = SessionTool.getInstance().deletedata(admType,
                regionCode,
                sessionCode);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //ɾ��������ʾ
            int row = (Integer) callFunction("UI|Table|getSelectedRow");
            if (row < 0)
                return;
            this.callFunction("UI|Table|removeRow", row);
            this.callFunction("UI|Table|setSelectRow", row);
            this.messageBox("P0003");
            onClear();
        }
        else {
            return;
        }
    }

    /**
     *���
     */
    public void onClear() {

        clearValue("ADM_TYPE;REGION_CODE;SESSION_CODE;SESSION_DESC;PY1;"+
                   "PY2;SEQ;DESCRIPTION;START_CLINIC_TIME;END_CLINIC_TIME;"+
                   "START_REG_TIME;END_REG_TIME;VALUEADD_TIME;VALUEADD_CODE;ORDER_DESC;DESC");
        this.callFunction("UI|Table|clearSelection");
        selectRow = -1;
        callFunction("UI|ADM_TYPE|setEnabled", true);
        //===========pangben modify 20110422 start
        this.setValue("REGION_CODE",Operator.getRegion());
       // callFunction("UI|REGION_CODE|setEnabled", true);
        //===========pangben modify 20110422 stop
        callFunction("UI|SESSION_CODE|setEnabled", true);
        String admType = getValue("ADM_TYPE").toString();
        String regionCode = getValue("REGION_CODE").toString();
        String sessionCode = getValue("SESSION_CODE").toString();
        this.setValue("START_REG_TIME","00:00:00");
        this.setValue("END_REG_TIME","00:00:00");
        this.setValue("START_CLINIC_TIME","00:00:00");
        this.setValue("END_CLINIC_TIME","00:00:00");
        this.setValue("VALUEADD_TIME","00:00:00");
        TParm data = SessionTool.getInstance().selectdata(admType, regionCode,
            sessionCode);
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
        if (TCM_Transform.getString(this.getValue("SESSION_DESC")).length() <
            1) {
            return null;
        }
        String value = TMessage.getPy(this.getValueString("SESSION_DESC"));
        if (null == value || value.length() < 1) {
            return null;
        }
        this.setValue("PY1", value);
        // �������
        ( (TTextField) getComponent("PY1")).grabFocus();
        return null;
    }
}
