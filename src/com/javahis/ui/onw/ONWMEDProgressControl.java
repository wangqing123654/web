package com.javahis.ui.onw;

import com.dongyang.control.*;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import jdo.onw.ONWMEDProgressTool;
import com.dongyang.ui.TTable;

/**
 * <p>Title: ҽ������</p>
 *
 * <p>Description: ҽ������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-26
 * @version 1.0
 */
public class ONWMEDProgressControl
    extends TControl {
    TTable TABLE;
    public void onInit(){
        super.onInit();
        this.setValue("DATE",SystemTool.getInstance().getDate());
        this.setValue("ADM_TYPE", "O");
        TABLE= (TTable)this.getComponent("Table");
        onQuery();
    }
    /**
     * ��ѯ
     */
    public void onQuery(){
//    	if("".equals(this.getValueString("ADM_TYPE"))||this.getValueString("ADM_TYPE").equals(null)){
//    		this.messageBox("�������ż�ס���");
//    		return;
//    	}
        String Q_DATE = this.getText("DATE").replace("/","");//��ѯ����
        String RPTTYPE_CODE = "";
        if(this.getValue("RPTTYPE_CODE")!=null&&!this.getValueString("RPTTYPE_CODE").equals("")){
            RPTTYPE_CODE = this.getValueString("RPTTYPE_CODE");
            TABLE.setHeader("�����Ŀ,150,RPT_TYPE;ִ�п���,150;�����˴�,150;�ȴ��˴�,150");
        }else if(this.getValue("SUB_SYSTEM_CODE")!=null&&!this.getValueString("SUB_SYSTEM_CODE").equals("")){
            RPTTYPE_CODE = this.getValueString("SUB_SYSTEM_CODE");
            TABLE.setHeader("�����Ŀ,150,RPT_TYPE;ִ�п���,150;�����˴�,150;�ȴ��˴�,150");
        }else{
            RPTTYPE_CODE = this.getValueString("SUB_SYSTEM_CODE");
            TABLE.setHeader("ϵͳ����,150,RPT_TYPE;ִ�п���,150;�����˴�,150;�ȴ��˴�,150");
        }
        TParm result = ONWMEDProgressTool.getInstance().selectData(RPTTYPE_CODE,this.getValueString("EXEC_DEPT_CODE"),Q_DATE,this.getValueString("ADM_TYPE"));
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            TABLE.removeRowAll();
            return;
        }
        if(result.getCount()<=0){
            this.messageBox("E0008");
            TABLE.removeRowAll();
            return;
        }
        TABLE.setParmValue(result);
    }
    /**
     * ���
     */
    public void onClear(){
        this.clearValue("SUB_SYSTEM_CODE;RPTTYPE_CODE;EXEC_DEPT_CODE");
        this.onQuery();
    }
    /**
     * ϵͳ���� ѡ���¼�
     */
    public void onSelect(){
        this.clearValue("RPTTYPE_CODE");
    }
}
