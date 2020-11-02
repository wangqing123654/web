package com.javahis.ui.reg;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.reg.SchWeekTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 *
 * <p>Title:ҽʦ�ܰ������� </p>
 *
 * <p>Description:ҽʦ�ܰ������� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.26
 * @version 1.0
 */
public class REGSchWeekControl
    extends TControl {
//    TParm data;
    int selectRow = -1;
    private boolean crmFlg = StringTool.getBoolean(TConfig.getSystemValue("crm.switch"));
    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
        //��ʼ��REGION��½Ĭ�ϵ�¼����
        //======pangben modify 20110410
        callFunction("UI|REGION_CODE|setValue", Operator.getRegion());
        //========pangben modify 20110421 start Ȩ�����
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop

        onQuery();
    }

    /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {

        if (row < 0)
            return;
        TParm data=(TParm)callFunction("UI|TABLE|getParmValue");
        setValueForParm(
            "REGION_CODE;ADM_TYPE;DAYOFWEEK;SESSION_CODE;CLINICTYPE_CODE;" +
            "DEPT_CODE;DR_CODE;CLINICROOM_NO;QUEGROUP_CODE;WEST_MEDI_FLG;" +
            "CREAT_DATE;CLINICTMP_FLG;EXP_DATE;EXP_DATE_E",
            data, row);
        selectRow = row;
        TextFormatSYSOperator operatorText = (TextFormatSYSOperator)this.getComponent("DR_CODE");
        operatorText.onQuery();
        setValue("DR_CODE",data.getValue("DR_CODE",row));
//        //��ʼ��Combo���ɱ༭
//        callFunction("UI|ADM_TYPE|setEnabled", false);

    }

    /**
     * ����
     */
    public void onInsert() {
        //У���Ƿ������ֵ,��������ʾ��Ϣ
//        if (!this.emptyTextCheck(
//            "CLINICTYPE_CODE,DEPT_CODE,DR_CODE,QUEGROUP_CODE,WEST_MEDI_FLG"))
//            return;
        if (getValue("CLINICTYPE_CODE") == null ||
            this.getValueString("CLINICTYPE_CODE").length() == 0) {
            this.messageBox("�ű���Ϊ��!");
            return;
        }
        if (getValue("DEPT_CODE") == null ||
            this.getValueString("DEPT_CODE").length() == 0) {
            this.messageBox("���Ҳ���Ϊ��!");
            return;
        }
        if (getValue("DR_CODE") == null ||
            this.getValueString("DR_CODE").length() == 0) {
            this.messageBox("ҽ������Ϊ��!");
            return;
        } 
        if (getValue("QUEGROUP_CODE") == null ||
            this.getValueString("QUEGROUP_CODE").length() == 0) {
            this.messageBox("���������Ϊ��!");
            return;
        }
        if (getValue("WEST_MEDI_FLG") == null ||
            this.getValueString("WEST_MEDI_FLG").length() == 0) {
            this.messageBox("����ҽע�ǲ���Ϊ��!");
            return;
        }

        TParm parm = getParmForTag(
            "REGION_CODE;ADM_TYPE;DAYOFWEEK;SESSION_CODE;CLINICTYPE_CODE;" +
            "DEPT_CODE;DR_CODE;CLINICROOM_NO;QUEGROUP_CODE;WEST_MEDI_FLG;" +
            "CREAT_DATE:Timestamp;CLINICTMP_FLG;EXP_DATE:Timestamp;EXP_DATE_E:Timestamp");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        
        TParm result = SchWeekTool.getInstance().insertdata(parm);

        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //��ʾ��������
        callFunction("UI|TABLE|addRow", parm,
                     "REGION_CODE;ADM_TYPE;DAYOFWEEK;SESSION_CODE;CLINICTYPE_CODE;" +
                     "DEPT_CODE;DR_CODE;CLINICROOM_NO;QUEGROUP_CODE;WEST_MEDI_FLG;" +
                     "CREAT_DATE;CLINICTMP_FLG;EXP_DATE;EXP_DATE_E;OPT_USER;OPT_DATE;OPT_TERM");
        // �������Ϸ����ؼ���ʾ
        this.messageBox("P0002");

    }

    /**
     * ����
     */
    public void onUpdate() {
        TParm parm = getParmForTag(
            "REGION_CODE;ADM_TYPE;DAYOFWEEK;SESSION_CODE;CLINICTYPE_CODE;" +
            "DEPT_CODE;DR_CODE;CLINICROOM_NO;QUEGROUP_CODE;WEST_MEDI_FLG;" +
            "CREAT_DATE:Timestamp;CLINICTMP_FLG;EXP_DATE:Timestamp;EXP_DATE_E:Timestamp");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        //old value �õ�����ǰ�ľ�ֵ
        TTable table = (TTable)this.getComponent("Table");
        int selRow = table.getSelectedRow();
        String oldRegionCode = table.getItemString(selRow,"REGION_CODE");
        String oldAdmType = table.getItemString(selRow,"ADM_TYPE");
        int oldDayOfWeek = TypeTool.getInt(table.getItemString(selRow,"DAYOFWEEK")) ;
//        this.messageBox_(oldDayOfWeek);
        String oldSessionCode = table.getItemString(selRow,"SESSION_CODE");
        String oldClinicRoomNo = table.getItemString(selRow,"CLINICROOM_NO");
        parm.setData("OLD_REGION_CODE",oldRegionCode);
        parm.setData("OLD_ADM_TYPE",oldAdmType);
        parm.setData("OLD_DAYOFWEEK",oldDayOfWeek);
        parm.setData("OLD_SESSION_CODE",oldSessionCode);
        parm.setData("OLD_CLINICROOM_NO",oldClinicRoomNo);

        TParm result = SchWeekTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //ˢ�£�����ĩ��ĳ�е�ֵ
        int row = (Integer) callFunction("UI|Table|getSelectedRow");
        if (row < 0)
            return;
        TParm data=(TParm)callFunction("UI|TABLE|getParmValue");
        data.setRowData(row, parm);
        callFunction("UI|Table|setRowParmValue", row, data);
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
        if (this.messageBox("�Ƿ�ɾ��", "ѯ��", 2) == 0) {
            if (selectRow == -1)
                return;
            String region = getValue("REGION_CODE").toString();
            String admType = getValue("ADM_TYPE").toString();
            String dayofWeek = getValue("DAYOFWEEK").toString();
            String sessionCode = getValue("SESSION_CODE").toString();
            String clinicroomNo = getValue("CLINICROOM_NO").toString();
            TParm result = SchWeekTool.getInstance().deletedata(region, admType,
                dayofWeek, sessionCode, clinicroomNo);
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
        }
        else {
            return;
        }
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = getParmForTag(
            "REGION_CODE;ADM_TYPE;DAYOFWEEK;SESSION_CODE;CLINICROOM_NO;DEPT_CODE;DR_CODE;CLINICTYPE_CODE;QUEGROUP_CODE", true);

        TParm data = SchWeekTool.getInstance().selectdata(parm);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        this.callFunction("UI|Table|setParmValue", data);

    }

    /**
     *չ��
     */
    public void onSpreadout() {
    	TParm parm = new TParm();
    	parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
    	parm.setData("SESSION_CODE", this.getValueString("SESSION_CODE"));
    	parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
    	parm.setData("DR_CODE", this.getValueString("DR_CODE"));
    	parm.setData("CLINICTYPE_CODE", this.getValueString("CLINICTYPE_CODE"));
        this.openDialog("%ROOT%\\config\\reg\\REGSchWeekForDay.x",parm);
        
    }
    /**
     *��ת��
     */
    public void onSpreadoutNew() {
        this.openDialog("%ROOT%\\config\\reg\\REGSchWeekForDayNew.x");
    }

    /**
     *���
     */
    public void onClear() {
        clearValue(
            "ADM_TYPE;DAYOFWEEK;SESSION_CODE;CLINICTYPE_CODE;" +
            "DEPT_CODE;DR_CODE;CLINICROOM_NO;QUEGROUP_CODE;WEST_MEDI_FLG;" +
            "CREAT_DATE;CLINICTMP_FLG;EXP_DATE;EXP_DATE_E;CLINICAREA_NO");
        this.callFunction("UI|Table|clearSelection");
        setValue("REGION_CODE",Operator.getRegion());
        selectRow = -1;

    }
    
    /**
     * ����ܰ�������ڣ�ʱ�Σ��Ʊ�ҽ���Ƿ����ظ�����
     * @param parm
     */
    public int checkWeekDay(TParm parm){
    	String sql="SELECT COUNT (REGION_CODE) COUNT" +
    			" FROM REG_SCHWEEK" +
    			" WHERE DAYOFWEEK = '"+parm.getValue("DAYOFWEEK")+"'" +
    			" AND SESSION_CODE = '"+parm.getValue("SESSION_CODE")+"'" +
    			" AND DEPT_CODE = '"+parm.getValue("DEPT_CODE")+"'" +
    			" AND DR_CODE = '"+parm.getValue("DR_CODE")+"'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	return result.getInt("COUNT", 0);
    }
    
    /**
     * ѡ�������õ�û��ռ�õ�����
     */
    public void getClinicroom(){
    	String admType = this.getValueString("ADM_TYPE");
    	String sessionCode = this.getValueString("SESSION_CODE");
    	String week = this.getValueString("DAYOFWEEK");
    	String clinicareaNo = this.getValueString("CLINICAREA_NO");
		if (admType.trim().length() > 0 && sessionCode.trim().length() > 0
				&& week.trim().length() > 0 && clinicareaNo.trim().length() > 0) {
    		String sql = "SELECT MIN(CLINICROOM_NO) CLINICROOM_NO" +
    				" FROM REG_CLINICROOM" +
    				" WHERE CLINICAREA_CODE = '"+clinicareaNo+"'" +
    				" AND CLINICROOM_NO NOT IN (" +
    				" SELECT DISTINCT CLINICROOM_NO" +
    				" FROM REG_SCHWEEK" +
    				" WHERE ADM_TYPE = '"+admType+"'" +
    				" AND SESSION_CODE = '"+sessionCode+"'" +
    				" AND DAYOFWEEK = '"+week+"')";
    		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    		String clinicroomNo = parm.getValue("CLINICROOM_NO",0);
    		this.setValue("CLINICROOM_NO", clinicroomNo);
    		if(clinicroomNo.trim().length() == 0){
    			this.messageBox("������������ȫ��ռ��");
    		}
    	}else{
    		this.messageBox("��ѡ���ż������ڡ�ʱ�Σ�");
    		this.setValue("CLINICAREA_NO", "");
    	}
    }

}
