package com.javahis.ui.med;

import jdo.med.MedSmsDeptSetupTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;


public class MEDSmsDeptSetupControl extends TControl {

	public static String SQL = "";
	/**
	 * TABLE
	 */
	private static String TABLE = "TABLE";
	
	// ��¼���ѡ������
    int selectedRowIndex = -1;
    
	TTable table ;
	
	private TParm data;
	
	public void onInit() {
		super.onInit();
		table=(TTable)this.getComponent(TABLE); 
		callFunction("UI|Table|addEventListener",
                "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
		initTable();
	}
	
	public void initTable(){
		callFunction("UI|DELETE|setEnabled", false);
        callFunction("UI|REGION_CODE|setEnabled", true);
        callFunction("UI|DEPT_CODE|setEnabled", true);
        callFunction("UI|COMPETENT_TYPE|setEnabled", true);
		onQuery();
	}
	
	public TTable getTable(String tableName){
		return  (TTable)this.getComponent(tableName);
	}
	
	 
	
	/**
	 * ��ѯ�¼� 
	 */
	public void onQuery(){
		 TParm selectCondition = getParmForTag("DEPT_CODE;COMPETENT_TYPE", true);
		 data = MedSmsDeptSetupTool.getInstance().onQuery(selectCondition);
         if (data.getErrCode() < 0) {
             messageBox(data.getErrText());
             return;
         }else {
            callFunction("UI|Table|setParmValue", new Object[] {
                         data
            });
            return;
        }
	}
	
	/**
	 * �����¼�
	 */
	public void onSave(){
		// �жϱ����ֶ�
        if (this.getValue("REGION_CODE").equals("")
            || this.getValue("DEPT_CODE").equals("")
            || this.getValue("COMPETENT_TYPE").equals("")
            || this.getValue("PERSON_CODE").equals("")) {
            this.messageBox("������߿��һ������ܷ��������Ա��ѡ");
            return;
        }
        // -----------��������ֵ-----------------
        TParm parm = new TParm();
        parm.setData("REGION_CODE", this.getValue("REGION_CODE"));
        parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
        parm.setData("COMPETENT_TYPE", this.getValue("COMPETENT_TYPE"));
        parm.setData("PERSON_CODE", this.getValue("PERSON_CODE")); // �б��
        parm.setData("SEQ", this.getValue("SEQ"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        
        // -----------��������ֵ����-----------------
        // �жϡ�REGION_CODE���ؼ��Ƿ���ֻ�����ԣ���������޸ġ��������½���
        TParm result = new TParm();
        if ( ( (TTextFormat)this.getComponent("DEPT_CODE")).isEnabled()) {
             
            result = MedSmsDeptSetupTool.getInstance().onInsert(parm);
            this.messageBox("�½��ɹ���");
            onClear();
            initTable();
        }
        else {
            result = MedSmsDeptSetupTool.getInstance().onUpdate(parm);
            this.onClear();
            this.initTable();
            this.messageBox("�޸ĳɹ���");
            this.getTable("TABLE").setSelectedRow(selectedRowIndex);
        }
        if (result.getErrCode() < 0) {
            this.messageBox("����", result.getErrText(), -1);
            return;
        }

	}
	
	
	 public void onTableClicked(int row) {
        callFunction("UI|DELETE|setEnabled", true);
        if (row < 0) {
            return;
        }
        else {
            setValueForParm("REGION_CODE;DEPT_CODE;COMPETENT_TYPE;PERSON_CODE;SEQ",
                            data, row);
            selectedRowIndex  = row;
            callFunction("UI|REGION_CODE|setEnabled", new Object[] {
                         Boolean.valueOf(false)
            });
            callFunction("UI|DEPT_CODE|setEnabled", new Object[] {
                         Boolean.valueOf(false)
            });
            callFunction("UI|COMPETENT_TYPE|setEnabled", new Object[] {
                         Boolean.valueOf(false)
            });
            return;
        }
    }
	
	
	/**
	 * ɾ���¼�
	 */
	public void onDelete(){
		 // ȷ��ɾ��
        if (this.messageBox("ѯ��", "ȷ��ɾ��?", 0) == 1) {
            return;
        }
        // ���ɾ����Ϣ����Ϊ��
        if (this.getValue("DEPT_CODE").equals("")
            || this.getValue("DEPT_CODE").equals(null)) {
            this.messageBox("��ѡ��Ҫɾ�����");
            return;
        }
        TParm parm = new TParm();
        parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
        parm.setData("COMPETENT_TYPE",this.getValue("COMPETENT_TYPE"));
        
        TParm result = MedSmsDeptSetupTool.getInstance().onDelete(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        this.messageBox("ɾ���ɹ���");
        this.onClear();
        this.initTable();
	}
	
	/**
	 * ����¼�
	 */
	public void onClear(){
		this
        .clearValue("REGION_CODE;DEPT_CODE;COMPETENT_TYPE;PERSON_CODE;SEQ");
		selectedRowIndex = -1;
		callFunction("UI|DELETE|setEnabled", false);
	    callFunction("UI|REGION_CODE|setEnabled", true);
	    callFunction("UI|DEPT_CODE|setEnabled", true);
	    callFunction("UI|COMPETENT_TYPE|setEnabled", true);
		this.getTable("TABLE").clearSelection(); // ���TABLEѡ��״̬
		
	}
	
	
	
	/**
	 * getDBTool ���ݿ⹤��ʵ��
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
}
