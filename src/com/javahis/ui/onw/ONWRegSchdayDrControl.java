package com.javahis.ui.onw;

import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import com.dongyang.control.TControl;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import jdo.onw.ONWRegSchdayDrTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.util.StringTool;
import java.util.Vector;
import com.dongyang.ui.TComboBox;
import com.javahis.system.combo.TComboDept;
import com.dongyang.ui.TComboBox;
import jdo.reg.SchDayTool;
import jdo.reg.PanelGroupTool;
import com.dongyang.ui.TTextFormat;
import jdo.sys.SYSRegionTool;

/**
 *
 * <p>Title: ��ͨ�����ҽ��ά��</p>
 *
 * <p>Description:��ͨ�����ҽ��ά�� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ONWRegSchdayDrControl
    extends TControl {
    TParm data, comboldata;
    int selectRow = -1;
    public void onInit() {
        super.onInit();
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTABLEClicked");
        callFunction("UI|save|setEnabled", false);

        setValue("ADM_DATE", SystemTool.getInstance().getDate());//��ʼ��ʱ��
        //��ʼ������Combo
        this.setValue("CLINIC_AREA",Operator.getStation());
        if(this.getValueString("CLINIC_AREA").length()<=0)
            ((TTextFormat)this.getComponent("CLINIC_AREA")).setEnabled(true);
        else{
            ( (TTextFormat)this.getComponent("CLINIC_AREA")).setEnabled(true);
        }
        //===========pangben modify 20110421 start
        this.setValue("REGION_CODE", Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop

        onQuery();
        init();

    }

    /**
     * ��ʼ����ѯ�����հ��ͬ��
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setData("CLINIC_AREA", Operator.getStation()); //��ȡ��ʿ����������  ��ʿֻ�ܸ�����������ͨ��
        parm.setData("REGION_CODE", Operator.getRegion()); //����
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm = TIOM_AppServer.executeAction("action.onw.ONWRegSchdayDrAction",
                                            "seldate", parm); //��䣬����ͬ��
        if (parm.getErrCode() < 0) {
            messageBox_(parm.getErrText());
            return;
        }
        TParm admdate = new TParm();
        admdate.setData("ADM_DATE", getValue("ADM_DATE"));
        admdate.setData("REGION_CODE", Operator.getRegion());
        admdate.setData("OPT_USER", Operator.getID());
        admdate.setData("OPT_TERM", Operator.getIP());
        admdate = TIOM_AppServer.executeAction(
            "action.onw.ONWRegSchdayDrAction",
            "selReghday", admdate); //���հ��ͬ��
        if (parm.getErrCode() < 0) {
            messageBox_(parm.getErrText());
            return;
        }
        this.onSel();
    }

    /**
     * ��ѯ�¼�
     */
    public void onSel() {
        TParm selData = new TParm();
        selData = this.finishdata();
        data = new TParm();
        data = ONWRegSchdayDrTool.getInstance().initsel(selData);
        TParm values = new TParm();
        values.setData("CLINIC_AREA", data.getData("CLINIC_AREA"));
        values.setData("CLINICROOM_NO", data.getData("CLINICROOM_NO"));
        values.setData("DEPT_CODE", data.getData("DEPT_CODE"));
        values.setData("DR_CODE", data.getData("DR_CODE"));
        values.setData("SESSION_CODE", data.getData("SESSION_CODE"));
        values.setData("OPT_USER", data.getData("OPT_USER"));
        values.setData("OPT_DATE", data.getData("OPT_DATE"));
        values.setData("OPT_TERM", data.getData("OPT_TERM"));

        this.callFunction("UI|TABLE|setParmValue", values,
                          "CLINIC_AREA;CLINICROOM_NO;SESSION_CODE;DEPT_CODE;DR_CODE;OPT_USER;OPT_DATE;OPT_TERM");
    }

    /**
     * ��˿�����
     * @param parm TParm
     * @return TParm
     */
    public TParm checkEmpty(TParm parm) {
        TParm result = new TParm();
        String str[] = parm.getNames();
        int count = str.length;
        for (int i = 0; i < count; i++) {
            Vector vct = new Vector();
            Vector vct1 = new Vector();
            vct = (Vector) parm.getData(str[i]);
            for (int j = 0; j < vct.size(); j++) {
                vct1.add(vct.get(j) == null ? "" : vct.get(j));
            }
            result.setData(str[i], vct1);
        }
        return result;
    }

    /**
     * TABLE ����¼�
     * @param row int
     */
    public void onTABLEClicked(int row) {
        callFunction("UI|save|setEnabled", true);
        callFunction("UI|ADM_TYPE|setEnabled", false);
        callFunction("UI|SESSION_CODE|setEnabled", false);
        callFunction("UI|CLINICROOM_NO|setEnabled", false);
        callFunction("UI|CLINIC_AREA|setEnabled", false);
        if (row < 0) {
            return;
        }
        setValueForParm(
            "CLINIC_AREA;CLINICROOM_NO;DEPT_CODE;ADM_TYPE;SESSION_CODE",
            data, row);
        selectRow = row;
        //���ݿ��Ҷ���Ա�����б����ɸѡ  ɸѡ��ֵ
        onDEPT_CODE();
        callFunction("UI|DR_CODE|onQuery");
        this.setValue("DR_CODE",data.getValue("DR_CODE",row));
        //��ѯ�հ�� �����������
        TParm parm = new TParm();
        parm.setData("ADM_DATE", getValue("ADM_DATE"));
        parm.setData("REGION_CODE",Operator.getRegion());
        parm.setData("ADM_TYPE",data.getValue("ADM_TYPE",row));
        parm.setData("SESSION_CODE",data.getValue("SESSION_CODE",row));
        parm.setData("CLINICROOM_NO",data.getValue("CLINICROOM_NO",row));
        TParm schDay = SchDayTool.getInstance().selectdata(parm); //��ѯ�հ��
        System.out.println("�����ѯ���schDay is����"+schDay);
        this.setValue("QUEGROUP_CODE",schDay.getValue("QUEGROUP_CODE",0));
        this.setValue("CLINICTYPE_CODE",schDay.getValue("CLINICTYPE_CODE",0));
        this.setValue("MAX_QUE",schDay.getValue("MAX_QUE",0));
        this.setValue("QUE_NO",schDay.getValue("QUE_NO",0));
        this.setValue("WEST_MEDI_FLG",schDay.getValue("WEST_MEDI_FLG",0));
        if(schDay.getValue("QUEGROUP_CODE",0).length()>0){
            callFunction("UI|QUEGROUP_CODE|setEnabled", false);
        }else{
            callFunction("UI|QUEGROUP_CODE|setEnabled", true);
        }
        if(schDay.getValue("CLINICTYPE_CODE",0).length()<=0){
            this.setValue("CLINICTYPE_CODE","3");
        }
        if(schDay.getValue("WEST_MEDI_FLG",0).length()>0){
            callFunction("UI|WEST_MEDI_FLG|setEnabled", false);
        }else{
            callFunction("UI|WEST_MEDI_FLG|setEnabled", true);
        }
    }

    /**
     * �����¼�
     */
    public void onSave() {
        if (getValueString("QUEGROUP_CODE").length() == 0) {
            this.messageBox_("���������Ϊ�գ�");
            return;
        }
        if (getValueString("DEPT_CODE").length() <= 0) {
            this.messageBox_("�Ʊ���Ϊ�գ�");
            return;
        }
        TParm parm = getParmForTag("CLINICROOM_NO;DEPT_CODE;DR_CODE;ADM_TYPE;SESSION_CODE;QUEGROUP_CODE;CLINICTYPE_CODE;QUE_NO;MAX_QUE;WEST_MEDI_FLG");
        parm.setData("OPT_USER", Operator.getID());
        //============pangben modify 20110422 start
        parm.setData("REG_CLINICAREA",this.getValueString("CLINIC_AREA"));
         //============pangben modify 20110422 stop
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("CLINICTMP_FLG","N");//��ʱ���  Ĭ��Ϊ��N��
        parm.setData("VIP_FLG","N");//�Ƿ���VIP��  Ĭ��Ϊ��N��
        parm.setData("STOP_SESSION","N");//�Ƿ�ͣ��  Ĭ��Ϊ��N��
        parm.setData("REFRSN_CODE","");//ͣ��ԭ�� Ĭ��Ϊ��
        parm.setData("REALDEPT_CODE",this.getValue("DEPT_CODE"));//ʵ�ʿ������
        parm.setData("REALDR_CODE",this.getValue("DR_CODE"));//ʵ�ʿ���ҽʦ
        String admDate = StringTool.getString(TCM_Transform.getTimestamp(
            getValue("ADM_DATE")), "yyyyMMdd");
        parm.setData("ADM_DATE", admDate);
        /**
         * �ж��������Ǹ���
         */
        if ("".equals(data.getValue("DEPT_CODE", selectRow))) {
            TParm result = TIOM_AppServer.executeAction(
                "action.onw.ONWRegSchdayDrAction",
                "onSave", parm); //��䣬����ͬ��
            if (result.getErrCode() < 0) {
                messageBox_(result.getErrText());
                return;
            }
        }
        else {
            TParm result = TIOM_AppServer.executeAction(
                "action.onw.ONWRegSchdayDrAction",
                "onUpdata", parm); //��䣬����ͬ��
            if (result.getErrCode() < 0) {
                messageBox_(result.getErrText());
                return;
            }
        }
        int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
        if (row < 0)
            return;
        //ˢ�£�����ĩ��ĳ�е�ֵ
        data.setRowData(row, parm);
        callFunction("UI|TABLE|setRowParmValue", row, data);
        callFunction("UI|QUEGROUP_CODE|setEnabled", false);
        this.messageBox("P0005");
    }

    /**
     * ����¼�
     */
    public void onClear() {
        clearValue(
            "ADM_TYPE;DEPT_CODE;DR_CODE;QUEGROUP_CODE;CLINIC_AREA");
        callFunction("UI|ADM_TYPE|setEnabled", true);
        callFunction("UI|SESSION_CODE|setEnabled", true);
        callFunction("UI|CLINICROOM_NO|setEnabled", true);
        callFunction("UI|save|setEnabled", false);
        //��ʼ������Combo
        this.setValue("CLINIC_AREA",Operator.getStation());
        if(this.getValueString("CLINIC_AREA").length()<=0)
            ((TTextFormat)this.getComponent("CLINIC_AREA")).setEnabled(true);
        else{
            ( (TTextFormat)this.getComponent("CLINIC_AREA")).setEnabled(false);
        }
        //===========pangben modify 20110421 start
       this.setValue("REGION_CODE",Operator.getRegion());
        //===========pangben modify 20110421 stop

        this.callFunction("UI|Table|removeRowAll");
        callFunction("UI|QUEGROUP_CODE|setEnabled", true);
        selectRow = -1;
    }

    /**
     * �����ѯ����
     * @return TParm
     */
    public TParm finishdata() {
        TParm parm = new TParm();
        String values = TCM_Transform.getString(this.getValue(
            "ADM_TYPE"));
        if (values.length() > 0)
            parm.setData("ADM_TYPE", values);
        values = TCM_Transform.getString(this.getValue(
            "SESSION_CODE"));
        if (values.length() > 0)
            parm.setData("SESSION_CODE", values);
        values = TCM_Transform.getString(this.getValue(
            "CLINIC_AREA"));
        if (values.length() > 0)
            parm.setData("CLINIC_AREA", values);
        values = TCM_Transform.getString(this.getValue(
            "CLINICROOM_NO"));
        if (values.length() > 0)
            parm.setData("CLINICROOM_NO", values);
        //=========pangben modify 20110421 start
        values = TCM_Transform.getString(this.getValue(
            "REGION_CODE"));
        if (values.length() > 0)
            parm.setData("REGION_CODE", values);
        //=========pangben modify 20110421 start
        return parm;
    }

    /**
     * �õ�����combo
     * @return TComboDept
     */
    public TComboDept getDeptCombo() {
        return (TComboDept)this.callFunction("UI|DEPT_CODE|getThis");
    }

    /**
     * �ż����ѡ�¼�
     */
    public void onAdmType() {
        String admType = getValue("ADM_TYPE").toString();
        if ("O".equals(admType)) { //��������
            getDeptCombo().setOpdFitFlg("Y");
            getDeptCombo().setEmgFitFlg("");
        }
        else { //��������
            getDeptCombo().setOpdFitFlg("");
            getDeptCombo().setEmgFitFlg("Y");
        }
        getDeptCombo().onQuery();
        getDeptCombo().setSelectedIndex(0);
    }
    /**
     * ����ѡ���¼�
     */
    public void onDEPT_CODE(){
        this.clearValue("DR_CODE");
    }
    /**
     * �ű�ı��¼�
     */
    public void onClinicTypeCombo() {
        TParm data = PanelGroupTool.getInstance().getInfobyClinicType(this.
            getValueString("QUEGROUP_CODE"));
        if (this.getValueString("QUEGROUP_CODE").length()> 0) {
            if (data.getErrCode() < 0) {
                messageBox_(data.getErrText());
                return;
            }
            setValue("QUE_NO", 1);
            setValue("MAX_QUE", data.getInt("MAX_QUE", 0));
        }
        else {
            setValue("QUE_NO", 0);
            setValue("MAX_QUE", 0);
        }
    }
}
