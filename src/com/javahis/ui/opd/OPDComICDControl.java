package com.javahis.ui.opd;

import java.awt.Component;

import jdo.odo.CommonDiag;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.StringUtil;

/**
 * 
 * <p> Title: ����ҽ������վ������� </p>
 * 
 * <p> Description:����ҽ������վ������� </p>
 * 
 * <p> Copyright: Copyright (c) Liu dongyang 2008 </p>
 * 
 * <p> Company:Javahis </p>
 * 
 * @author ehui 20090406
 * @version 1.0
 */

public class OPDComICDControl extends TControl {
	//����ֵ�Ʊ�ҽʦ������
	private String deptOrDr;
	//��̬�Ʊ�������
	private final static String DEPT="1";
	//�ɱ��ҽʦ�Ĵ���
	private String code;
	//������
	private String icdType;
	//������϶���
	private CommonDiag diag;
	//��TABLE
	private TTable table;
	//ȡ�������ʾ���SQL
	public final static String GET_MAX_SEQ=
		"SELECT MAX(SEQ) AS SEQ FROM OPD_COMDIAG";
	private boolean isEng;
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		//ȡ�ô������
		getInitParameter();
		//��ʼ������
		if("W".equalsIgnoreCase(icdType)){
			table.addEventListener("MED_TABLE->" + TTableEvent.CHANGE_VALUE,this, "onMedTableChangeValue");
		}else{
			table.addEventListener("MED_TABLE->" + TTableEvent.CHANGE_VALUE,this, "onChnTableChangeValue");
		}
		table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
		"onDiagCreateEditComponent");
		onClear();
		isEng="en".equalsIgnoreCase(Operator.getLanguage());
	}
	/**
	 * ���ݴ����������DEPTORDR��ֵ��DEPTORDR_CODE��ֵ
	 */
	public void getInitParameter(){
		String temp=TCM_Transform.getString(this.getParameter());
		deptOrDr=temp;
		icdType="W";
		TComboBox dr=(TComboBox)this.getComponent("OPERATOR");
		TComboBox dept=(TComboBox)this.getComponent("DEPT_CODE");
		TLabel label=(TLabel)this.getComponent("LABEL");
		if(DEPT.equals(deptOrDr)){
			code=Operator.getDept();
			label.setZhText("����");
			label.setEnText("Dept.");
			dept.setVisible(true);
			dept.setEnabled(false);
			dept.setSelectedID(Operator.getDept());
			dr.setEnabled(false);
			dr.setVisible(false);
		}else{
			code=Operator.getID();
			label.setZhText("ҽʦ");
			label.setEnText("Dr.");
			dept.setEnabled(false);
			dept.setVisible(false);
			dr.setSelectedID(Operator.getID());
			dr.setEnabled(false);
			dr.setVisible(true);
		}
		if("W".equalsIgnoreCase(icdType))
			table=(TTable)this.getComponent("MED_TABLE");
	}
	/**
	 * ��ʼ��TABLE����
	 */
	public void onClear(){
		if(table==null)
		{
			table=(TTable)this.getComponent("MED_TABLE");
		}
		diag=new CommonDiag(deptOrDr,code);
		table.setDataStore(diag);
		int row=diag.insertRow();
		table.setDSValue();
	}
	/**
	 * �����ϵ�������
	 * 
	 * @param com Component
	 * @param row int
	 * @param column int
	 */
	public void onDiagCreateEditComponent(Component com, int row, int column) {
		if (column != 0)
			return;
		if (!(com instanceof TTextField))
			return;
		TParm parm=new TParm();
		parm.setData("ICD_TYPE",icdType);
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ICD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSICDPopup.x"),parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popDiagReturn");
	}
	/**
	 * �������
	 * @param tag String
	 * @param obj Object
	 */
	public void popDiagReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		TTable tableDiag=(TTable)this.getComponent("MED_TABLE");
		int row=tableDiag.getSelectedRow();
		table.acceptText();
		if(!"W".equalsIgnoreCase(icdType))
			table.setValueAt(parm.getValue("SYNDROME_FLG"), row, 4);
		diag.setItem(row, "ICD_CODE", parm.getValue("ICD_CODE"));
		diag.setItem(row, "ICD_TYPE", icdType);
		diag.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
		diag.setItem(row, "OPT_TERM", Operator.getIP());
		diag.setItem(row, "OPT_USER", Operator.getID());
		diag.setActive(row,true);
		int newRow=diag.insertRow();
		table.setDSValue();
		table.getTable().grabFocus();
		table.setSelectedRow(newRow);
		table.setSelectedColumn(0);
	}
	/**
	 * ���Ŀ���
	 */
	public void onChangeDept(){
		code=this.getValueString("DEPT_CODE");
		onClear();
	}
	/**
	 * ����
	 */
	public void onSave(){
		String[] sql=diag.getUpdateSQL();
		if(sql== null||sql.length<1){
			this.messageBox_("����ʧ��");
			return;
		}
			
		
		TParm result=new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()!=0){
			this.messageBox("E0001");
//			this.messageBox_(result.getErrText());
		}else{
			this.messageBox("P0001");
		}
		onClear();
	}
	/**
	 * ɾ���¼�
	 */
	public void onDelete(){
		if(table==null){
			return;
		}
		int row=table.getSelectedRow();
		if(row<0){
			return;
		}
		if(StringUtil.isNullString(diag.getItemString(row, "ICD_CODE"))){
			return;
		}
		diag.deleteRow(row);
		if(diag.isModified()){
			onSave();	
		}
		table.setDSValue();
		
	}
}
