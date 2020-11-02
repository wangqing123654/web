package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.manager.TCM_Transform;
import jdo.reg.PanelGroupTool;
/**
 *
 * <p>Title:������������   </p>
 *
 * <p>Description:������������ </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.17
 * @version 1.0
 */
public class REGPanelGroupContrl extends TControl{
    int selectRow = -1;
    public void onInit() {
        super.onInit();
        callFunction("UI|TABLEGROUP|addEventListener",
                     "TABLEGROUP->" + TTableEvent.CLICKED, this, "onTABLEGROUPClicked");
        onQuery();
    }
    /**
     *���Ӷ�TABLEGROUP�ļ���
     * @param row int
     */
    public void onTABLEGROUPClicked(int row) {

        if (row < 0)
            return;
        TParm data=(TParm)callFunction("UI|TABLEGROUP|getParmValue");
        setValueForParm("QUEGROUP_CODE;QUEGROUP_DESC;PY1;PY2;SEQ;DESCRIPTION;MAX_QUE;VIP_FLG;SESSION_CODE;ADM_TYPE;SESSION_CODE;ADM_TYPE",
                        data, row);
        selectRow = row;
    }
    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = getParmForTag("QUEGROUP_CODE", true);
        TParm data = PanelGroupTool.getInstance().selectdata(parm);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        this.callFunction("UI|TABLEGROUP|setParmValue", data);
    }

    /**
     * ����
     */
    public void onInsert() {
        if (!this.emptyTextCheck("QUEGROUP_CODE"))
            return;
        //QUEGROUP_DESC
        if(this.getValueString("QUEGROUP_DESC").equals("")){
        	this.messageBox("���������˵����");
        	return;
        	
        }
        if(this.getValueString("ADM_TYPE").equals("")){
        	this.messageBox("�������ż���");
        	return;
        	
        }
        
        if(("".equals(this.getValueString("SESSION_CODE")) || this.getValue("SESSION_CODE") == null) && this.getValueBoolean("VIP_FLG") ){
        	this.messageBox("������ʱ�Σ�");
        	return;
        }
        	
        
        TParm parm = getParmForTag("QUEGROUP_CODE;QUEGROUP_DESC;PY1;PY2;SEQ;DESCRIPTION;MAX_QUE;VIP_FLG;SESSION_CODE;ADM_TYPE");

        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = PanelGroupTool.getInstance().insertdata(parm);

        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //2008.09.05 --------start--------table�ϼ���������������ʾ
        callFunction("UI|TABLEGROUP|addRow", parm,
                     "QUEGROUP_CODE;QUEGROUP_DESC;PY1;PY2;SEQ;DESCRIPTION;MAX_QUE;VIP_FLG;SESSION_CODE;ADM_TYPE;OPT_USER;OPT_DATE;OPT_TERM");

        //���������ɹ���ʾ��
        this.messageBox("P0002");

    }

    /**
     * ����
     */
    public void onUpdate() {
    	 if (!this.emptyTextCheck("QUEGROUP_CODE"))
             return;
         //QUEGROUP_DESC
         if(this.getValueString("QUEGROUP_DESC").equals("")){
         	this.messageBox("���������˵����");
         	return;
         	
         }
         if(this.getValueString("ADM_TYPE").equals("")){
         	this.messageBox("�������ż���");
         	return;
         	
         }
         if(("".equals(this.getValueString("SESSION_CODE")) || this.getValue("SESSION_CODE") == null) && this.getValueBoolean("VIP_FLG") ){
         	this.messageBox("������ʱ�Σ�");
         	return;
         }
        TParm parm = getParmForTag("QUEGROUP_CODE;QUEGROUP_DESC;PY1;PY2;SEQ;DESCRIPTION;MAX_QUE;VIP_FLG;SESSION_CODE;ADM_TYPE");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = PanelGroupTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }

        //ˢ�£�����ĩ��ĳ�е�ֵ 
        int row = (Integer) callFunction("UI|TABLEGROUP|getSelectedRow");
        if (row < 0)
            return;
        TParm data=(TParm)callFunction("UI|TABLETYPE|getParmValue");
//        data.setRowData(row, parm);
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate().toString().replace("-", "/").substring(0, 10) );
        callFunction("UI|TABLEGROUP|setRowParmValue", row, parm);
        this.messageBox("P0001");

    }

    /**
     * ����
     */
    public void onSave() {
        if (selectRow == -1) {
            onInsert();
            this.clearValue("QUEGROUP_CODE;QUEGROUP_DESC;PY1;PY2;SEQ;DESCRIPTION;MAX_QUE;SESSION_CODE;ADM_TYPE");
            TCheckBox vip = (TCheckBox) this.getComponent("VIP_FLG");
            vip.setSelected(false);
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
            String quegroupCode = getValue("QUEGROUP_CODE").toString();
            TParm result = PanelGroupTool.getInstance().deletedata(quegroupCode);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //ɾ��������ʾ
            int row = (Integer) callFunction("UI|TABLEGROUP|getSelectedRow");
            if (row < 0)
                return;
            this.callFunction("UI|TABLEGROUP|removeRow", row);
            this.callFunction("UI|TABLEGROUP|setSelectRow", row);

            this.messageBox("P0003");
            this.clearValue("QUEGROUP_CODE;QUEGROUP_DESC;PY1;PY2;SEQ;DESCRIPTION;MAX_QUE;SESSION_CODE;ADM_TYPE");
            TCheckBox vip = (TCheckBox) this.getComponent("VIP_FLG");
            vip.setSelected(false);
        }
        else {
            return;
        }
    }

    /**
     * ���ݺ������ƴ������ĸ
     * @return Object
     */
    public Object onCode() {
        if (TCM_Transform.getString(this.getValue("QUEGROUP_DESC")).length() <
            1) {
            return null;
        }
        SystemTool st = new SystemTool();
        String value = st.charToCode(String.valueOf(this.getValue(
            "QUEGROUP_DESC")));
        if (null == value || value.length() < 1) {
            return null;
        }
        this.setValue("PY1", value);
        //�������
        this.callFunction("UI|afterFocus","QUEGROUP_DESC");

        return null;
    }
}
