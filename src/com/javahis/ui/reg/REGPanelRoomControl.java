package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import jdo.sys.Operator;
import jdo.reg.PanelRoomTool;
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
public class REGPanelRoomControl extends TControl{
    int selectRow = -1;
    public void onInit() {
        super.onInit();
        callFunction("UI|TABLEROOM|addEventListener",
                     "TABLEROOM->" + TTableEvent.CLICKED, this, "onTABLEROOMClicked");
        onQuery();
    }
    /**
     *���Ӷ�TABLEROOM�ļ���
     * @param row int
     */
    public void onTABLEROOMClicked(int row) {

        if (row < 0)
            return;
        TParm data=(TParm)callFunction("UI|TABLEROOM|getParmValue");
        //tag:value
        setValueForParm("CLINICROOM_NO;CLINICROOM_DESC;PY1;PY2;SEQ;"+
                        "DESCRIPTION;ADM_TYPE;REGION_CODE;CLINICAREA_CODE;ROOM_LOCATION:LOCATION;ORG_CODE;ACTIVE_FLG",
                        data, row);
        selectRow = row;
    }
    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = getParmForTag("CLINICROOM_NO", true);
        TParm data = PanelRoomTool.getInstance().selectdata(parm);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        this.callFunction("UI|TABLEROOM|setParmValue", data);
    }

    /**
     * ����
     */
    public void onInsert() {
        if (!this.emptyTextCheck("CLINICROOM_NO"))
            return;
        //value:type:tag
        TParm parm = getParmForTag("CLINICROOM_NO;CLINICROOM_DESC;PY1;PY2;SEQ;"+
                                   "DESCRIPTION;ADM_TYPE;REGION_CODE;CLINICAREA_CODE;LOCATION:String:ROOM_LOCATION;ORG_CODE;ACTIVE_FLG");

        parm.setData("OPT_USER", Operator.getID());
//        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = PanelRoomTool.getInstance().insertdata(parm);

        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //2008.09.05 --------start--------table�ϼ���������������ʾ
        callFunction("UI|TABLEROOM|addRow", parm,
                     "CLINICROOM_NO;CLINICROOM_DESC;PY1;PY2;SEQ;"+
                     "DESCRIPTION;ADM_TYPE;REGION_CODE;CLINICAREA_CODE;LOCATION;"+
                     "ORG_CODE;ACTIVE_FLG;OPT_USER;OPT_DATE;OPT_TERM");

        //���������ɹ���ʾ��
        this.messageBox("P0002");

    }

    /**
     * ����
     */
    public void onUpdate() {
        TParm parm = getParmForTag("CLINICROOM_NO;CLINICROOM_DESC;PY1;PY2;SEQ;"+
                                   "DESCRIPTION;ADM_TYPE;REGION_CODE;CLINICAREA_CODE;LOCATION:String:ROOM_LOCATION;ORG_CODE;ACTIVE_FLG");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = PanelRoomTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }

        //ˢ�£�����ĩ��ĳ�е�ֵ
        int row = (Integer) callFunction("UI|TABLEROOM|getSelectedRow");
        if (row < 0)
            return;
        TParm data=(TParm)callFunction("UI|TABLEROOM|getParmValue");
        data.setRowData(row, parm);
        callFunction("UI|TABLEROOM|setRowParmValue", row, data);
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
            String clinicRoomNo = getValue("CLINICROOM_NO").toString();
            TParm result = PanelRoomTool.getInstance().deletedata(clinicRoomNo);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //ɾ��������ʾ
            int row = (Integer) callFunction("UI|TABLEROOM|getSelectedRow");
            if (row < 0)
                return;
            this.callFunction("UI|TABLEROOM|removeRow", row);
            this.callFunction("UI|TABLEROOM|setSelectRow", row);

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
        clearValue("CLINICROOM_NO;CLINICROOM_DESC;PY1;PY2;SEQ;"+
                   "DESCRIPTION;ADM_TYPE;REGION_CODE;ROOM_LOCATION;ORG_CODE;ACTIVE_FLG");
        this.callFunction("UI|TABLEROOM|clearSelection");
        selectRow = -1;

    }
}
