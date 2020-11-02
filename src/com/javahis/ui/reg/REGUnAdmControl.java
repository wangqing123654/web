package com.javahis.ui.reg;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.jdo.TJDODBTool;
import java.util.ArrayList;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import com.dongyang.util.TypeTool;
import java.sql.Timestamp;
import jdo.reg.PatAdmTool;
import com.dongyang.manager.TIOM_AppServer;

/**
 *
 * <p>Title: �����˹�</p>
 *
 * <p>Description: �����˹�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class REGUnAdmControl
    extends TControl {
    TParm tableAllParm;
    private boolean crmFlg = true; //crm�ӿڿ���
    public void onInit() {
        super.onInit();
        //�˵�tableר�õļ���
        getTTable("Table").addEventListener(TTableEvent.
                                            CHECK_BOX_CLICKED, this,
                                            "onTableComponent");
        tableAllParm = new TParm();
        initPage();
    }

    /**
     * ��ʼ������
     */
    public void initPage() {
        setValue("ADM_DATE", StringTool.rollDate(SystemTool.getInstance().getDate(), +1).toString().substring(0, 10).replace("-", "/"));
        this.clearValue("CLINICROOM;SESSION_CODE");
        setValue("ALL", "N");
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        crmFlg = StringTool.getBoolean(TConfig.getSystemValue("crm.switch"));
    }

    /**
     * �˹�
     */
    public void onUnReg() {
        TParm result = new TParm();
        result = tableAllParm;
        if (result == null || result.getCount("CASE_NO") < 1) {
            this.messageBox_("���˹���Ϣ");
            return;
        }
        String optId = Operator.getID();
        String term = Operator.getIP();
        int count = result.getCount("CASE_NO");
        for (int i = 0; i < count; i++) {
        	if(crmFlg){
        		if(!"".equals(result.getValue("REG_ADM_TIME", i))){
            		TParm parmCrm = new TParm();
                	parmCrm.setData("mrNo", result.getData("MR_NO", i));
                	parmCrm.setData("date", result.getData("ADM_DATE", i).toString().replace("/", "").replace("-", "").subSequence(0, 8));
            		parmCrm.setData("timeInteval", result.getData("REG_ADM_TIME", i));
            		parmCrm.setData("dept", result.getData("REALDEPT_CODE", i));
            		parmCrm.setData("doc", result.getData("REALDR_CODE", i));
            		System.out.println("parmCrm==" + parmCrm);
            		TParm resultCrm = TIOM_AppServer.executeAction("action.reg.REGCRMAction",
            				"cancleOrderInfo", parmCrm);
            		if (!resultCrm.getBoolean("flg", 0)) {
            			this.messageBox("CRM���ԤԼȡ��ʧ��");
            			return;
            		}
            	}
        	}

            TParm parm = new TParm();
            parm.setData("REGCAN_USER",optId);
            parm.setData("CASE_NO",result.getData("CASE_NO", i));
            parm.setData("ADM_TYPE",result.getValue("ADM_TYPE", i));
            parm.setData("ADM_DATE",StringTool.getString(result.getTimestamp(
                "ADM_DATE", i), "yyyyMMdd"));
            parm.setData("SESSION_CODE",result.getValue("SESSION_CODE",i));
            parm.setData("CLINICROOM_NO",result.getValue("CLINICROOM_NO",i));
            parm.setData("QUE_NO",result.getValue("QUE_NO",i));
            parm.setData("OPT_USER",optId);
            parm.setData("OPT_TERM",term); 
            TParm re = TIOM_AppServer.executeAction(
            "action.reg.REGAction",
            "updateUnAdmReg", parm);
            if (re.getErrCode() < 0) {
                this.messageBox_("���� "+result.getValue("PAT_NAME",i) + " �˹�ʧ��");
            }
        }
        onQuery();
    }

    /**
     * table����checkBox�ı�
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        TParm tableParm = table.getParmValue();
        tableAllParm = new TParm();
        int count = tableParm.getCount("CASE_NO");
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("FLG", i)) {
                tableAllParm.addData("REGION_CODE",
                                     tableParm.getValue("REGION_CODE", i));
                tableAllParm.addData("ADM_TYPE",
                                     tableParm.getValue("ADM_TYPE", i));
                tableAllParm.addData("ADM_DATE",
                                     tableParm.getData("ADM_DATE", i));
                tableAllParm.addData("SESSION_CODE",
                                     tableParm.getValue("SESSION_CODE", i));
                tableAllParm.addData("CLINICROOM_NO",
                                     tableParm.getValue("CLINICROOM_NO", i));
                tableAllParm.addData("QUE_NO",
                                     tableParm.getValue("QUE_NO", i));
                tableAllParm.addData("CASE_NO",
                                     tableParm.getValue("CASE_NO", i));
                tableAllParm.addData("MR_NO",
                        tableParm.getValue("MR_NO", i));
                tableAllParm.addData("REG_ADM_TIME",
                        tableParm.getValue("REG_ADM_TIME", i));
                tableAllParm.addData("REALDEPT_CODE",
                        tableParm.getValue("REALDEPT_CODE", i));
                tableAllParm.addData("REALDR_CODE",
                        tableParm.getValue("REALDR_CODE", i));
            }
        }
        return true;
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm selParm = new TParm();
        if(this.getValueString("ADM_DATE").length()<=0){
            this.messageBox_("��ѡ���������");
            this.grabFocus("ADM_DATE");
            return;
        }
        if(this.getValueString("SESSION_CODE").length()>0){
            selParm.setData("SESSION_CODE",this.getValueString("SESSION_CODE"));
        }
        if(this.getValueString("CLINICROOM").length()>0){
            selParm.setData("CLINICROOM",this.getValueString("CLINICROOM"));
        }
        selParm.setData("ADM_DATE",
                        StringTool.getString( (Timestamp)this.getValue("ADM_DATE"),
                                             "yyyyMMdd"));
        if ("".equals(Operator.getRegion()))
            selParm.setData("REGION_CODE", Operator.getRegion());
        TParm result = PatAdmTool.getInstance().selectUnAdmData(selParm);
        if (result.getCount("CLINICROOM_NO") < 1) {
            this.messageBox_("��������");
            this.callFunction("UI|Table|removeRowAll");
            return;
        }
        this.callFunction("UI|Table|setParmValue", result);
        this.setValue("ALL","N");
    }

    /**
     * ���
     */
    public void onClear() {
        initPage();
    }

    /**
     * checkBoxѡ���¼�
     */
    public void selAllFlg() {
        TTable table = getTTable("Table");
        TParm tableParm = table.getParmValue();
        int count = 0;
        if (tableParm != null)
            count = tableParm.getCount();
        if ("Y".equals(getValue("ALL"))) {
            for (int i = 0; i < count; i++) {
                table.setItem(i, "FLG", "Y");
            }
            tableAllParm = tableParm;
        }
        else {
            for (int i = 0; i < count; i++) {
                table.setItem(i, "FLG", "N");
            }
            tableAllParm = new TParm();
        }
    }

    /**
     * �õ�TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
}
