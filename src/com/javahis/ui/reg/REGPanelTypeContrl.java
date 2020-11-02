package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.reg.PanelTypeTool;
import jdo.sys.SystemTool;
import com.dongyang.manager.TCM_Transform;

/**
 *
 * <p>Title:�ű������ </p>
 *
 * <p>Description:�ű������ </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.16
 * @version 1.0
 */
public class REGPanelTypeContrl
    extends TControl {
    int selectRow = -1;
    public void onInit() {
        super.onInit();
        callFunction("UI|TABLETYPE|addEventListener",
                     "TABLETYPE->" + TTableEvent.CLICKED, this,
                     "onTABLETYPEClicked");
        onQuery();
    }
    /**
     * ���Ӷ�TABLETYPE�ļ���
     * @param row int
     */
    public void onTABLETYPEClicked(int row) {

        if (row < 0)
            return;
        TParm data = (TParm) callFunction("UI|TABLETYPE|getParmValue");
        setValueForParm(
            "ADM_TYPE;CLINICTYPE_CODE;CLINICTYPE_DESC;PY1;PY2;SEQ;DESCRIPTION;PROF_FLG",
            data, row);
        selectRow = row;
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = getParmForTag("ADM_TYPE;CLINICTYPE_CODE", true);
        TParm data = PanelTypeTool.getInstance().selectdata(parm);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        this.callFunction("UI|TABLETYPE|setParmValue", data);
    }

    /**
     * ����
     */
    public void onInsert() {
        if (!this.emptyTextCheck("ADM_TYPE,CLINICTYPE_CODE"))
            return;
        TParm parm = getParmForTag(
            "ADM_TYPE;CLINICTYPE_CODE;CLINICTYPE_DESC;PY1;PY2;SEQ;DESCRIPTION;PROF_FLG");

        parm.setData("OPT_USER", Operator.getID());
//        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = PanelTypeTool.getInstance().insertdata(parm);

        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //2008.09.05 --------start--------table�ϼ���������������ʾ
        callFunction("UI|TABLETYPE|addRow", parm,
                     "ADM_TYPE;CLINICTYPE_CODE;CLINICTYPE_DESC;PY1;PY2;"+
                     "SEQ;DESCRIPTION;PROF_FLG;OPT_USER;OPT_DATE;OPT_TERM");
        String id = parm.getValue("CLINICTYPE_CODE");
        String name = parm.getValue("CLINICTYPE_DESC");
        //��������ͬ��ˢ�·���
        this.callFunction("UI|addNode", id, name);
        //���������ɹ���ʾ��
        this.messageBox("P0002");

    }

    /**
     * ����
     */
    public void onUpdate() {
        TParm parm = getParmForTag(
            "ADM_TYPE;CLINICTYPE_CODE;CLINICTYPE_DESC;PY1;PY2;SEQ;DESCRIPTION;PROF_FLG");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = PanelTypeTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }

        //ˢ�£�����ĩ��ĳ�е�ֵ
        int row = (Integer) callFunction("UI|TABLETYPE|getSelectedRow");
        if (row < 0)
            return;
        TParm data = (TParm) callFunction("UI|TABLETYPEFEE|getParmValue");
        data.setRowData(row, parm);
        callFunction("UI|TABLETYPE|setRowParmValue", row, data);

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
            String admType = getValue("ADM_TYPE").toString();
            String clinicTypeCode = getValue("CLINICTYPE_CODE").toString();
            TParm result = PanelTypeTool.getInstance().deletedata(admType,
                clinicTypeCode);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //ɾ��������ʾ
            int row = (Integer) callFunction("UI|TABLETYPE|getSelectedRow");
            if (row < 0)
                return;
            this.callFunction("UI|TABLETYPE|removeRow", row);
            this.callFunction("UI|TABLETYPE|setSelectRow", row);
            this.callFunction("UI|deleteNode", clinicTypeCode);
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
        clearValue(
            "ADM_TYPE;CLINICTYPE_CODE;CLINICTYPE_DESC;PY1;PY2;SEQ;DESCRIPTION;PROF_FLG");
        this.callFunction("UI|TABLETYPE|clearSelection");
        selectRow = -1;

    }

    /**
     * ���ݺ������ƴ������ĸ
     * @return Object
     */
    public Object onCode() {
        if (TCM_Transform.getString(this.getValue("CLINICTYPE_DESC")).length() <
            1) {
            return null;
        }
        SystemTool st = new SystemTool();
        String value = st.charToCode(String.valueOf(this.getValue(
            "CLINICTYPE_DESC")));
        if (null == value || value.length() < 1) {
            return null;
        }
        this.setValue("PY1", value);
        //�������
        this.callFunction("UI|afterFocus", "CLINICTYPE_DESC");

        return null;
    }
}
