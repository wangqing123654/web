package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import jdo.sys.Operator;
import jdo.reg.PanelAreaTool;
/**
 *
 * <p>Title:����ά�������� </p>
 *
 * <p>Description:����ά�������� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.25
 * @version 1.0
 */
public class REGPanelAreaControl extends TControl{
    int selectRow = -1;
    public void onInit() {
        super.onInit();
        callFunction("UI|TABLEAREA|addEventListener",
                     "TABLEAREA->" + TTableEvent.CLICKED, this, "onTABLEAREAClicked");
        onQuery();
    }
    /**
     *���Ӷ�TABLEAREA�ļ���
     * @param row int
     */
    public void onTABLEAREAClicked(int row) {

        if (row < 0)
            return;
        TParm data=(TParm)callFunction("UI|TABLEAREA|getParmValue");
        setValueForParm("CLINICAREA_CODE;CLINIC_DESC;PY1;PY2;SEQ;DESCRIPTION;REGION_CODE",
                        data, row);
        selectRow = row;
    }
    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = getParmForTag("CLINICAREA_CODE", true);
        TParm data = PanelAreaTool.getInstance().selectdata(parm);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        this.callFunction("UI|TABLEAREA|setParmValue", data);
    }

    /**
     * ����
     */
    public void onInsert() {
        if (!this.emptyTextCheck("CLINICAREA_CODE"))
            return;
        TParm parm = getParmForTag("CLINICAREA_CODE;CLINIC_DESC;PY1;PY2;SEQ:int;DESCRIPTION;REGION_CODE");

        parm.setData("OPT_USER", Operator.getID());
//        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = PanelAreaTool.getInstance().insertdata(parm);

        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //2008.09.05 --------start--------table�ϼ���������������ʾ
        callFunction("UI|TABLEAREA|addRow", parm,
                     "CLINICAREA_CODE;CLINIC_DESC;PY1;PY2;SEQ;DESCRIPTION;REGION_CODE;OPT_USER;OPT_DATE;OPT_TERM");

        //���������ɹ���ʾ��
        this.messageBox("P0002");

    }

    /**
     * ����
     */
    public void onUpdate() {
        TParm parm = getParmForTag("CLINICAREA_CODE;CLINIC_DESC;PY1;PY2;SEQ:int;DESCRIPTION;REGION_CODE");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = PanelAreaTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }

        //ˢ�£�����ĩ��ĳ�е�ֵ
        int row = (Integer) callFunction("UI|TABLEAREA|getSelectedRow");
        if (row < 0)
            return;
        TParm data=(TParm)callFunction("UI|TABLEAREA|getParmValue");
        data.setRowData(row, parm);
        callFunction("UI|TABLEAREA|setRowParmValue", row, data);

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
        if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 2) == 0) {
            if (selectRow == -1)
                return;
            String clinicAreaCode = getValue("CLINICAREA_CODE").toString();
            TParm result = PanelAreaTool.getInstance().deletedata(clinicAreaCode);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //ɾ��������ʾ
            int row = (Integer) callFunction("UI|TABLEAREA|getSelectedRow");
            if (row < 0)
                return;
            this.callFunction("UI|TABLEAREA|removeRow", row);
            this.callFunction("UI|TABLEAREA|setSelectRow", row);

            this.messageBox("P0003");
        }
        else {
            return;
        }
    }


    /**
     *���
     */
    public void onClear() {
        clearValue("CLINICAREA_CODE;CLINIC_DESC;PY1;PY2;SEQ;DESCRIPTION;REGION_CODE");
        this.callFunction("UI|TABLEAREA|clearSelection");
        selectRow = -1;

    }
}
