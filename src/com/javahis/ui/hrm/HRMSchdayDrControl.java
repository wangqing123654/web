package com.javahis.ui.hrm;

import java.sql.Timestamp;

import jdo.hrm.HRMSchdayDr;
import jdo.reg.SessionTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 * <p> Title: ��������հ������� </p>
 *
 * <p> Description: ��������հ������� </p>
 *
 * <p> Copyright: javahis 20090922 </p>
 *
 * <p> Company:JavaHis </p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMSchdayDrControl extends TControl {
	//�հ�����
	private HRMSchdayDr sch;
	//TABLE
	private TTable table;
	//AREA_COMBO,ROOM_COMBO
	private TTextFormat areaCombo,roomCombo;
	//������
	private StringUtil util;
	/**
	 * ��ʼ���¼�
	 */
	public void onInit() {
		super.onInit();
		//��ʼ���ؼ�
		onInitComponent();
		//��ʼ������
		onInitData();
		//���
		onClear();
	}
	/**
	 * ��ʼ���ؼ�
	 */
	private void onInitComponent(){
		table=(TTable)this.getComponent("TABLE");
		//��ͬϸ��TABLEֵ�ı��¼�
		table.addEventListener("TABLE->"+TTableEvent.CHANGE_VALUE, this,
		"onTabValueChanged");

	}

	/**
	 * ��ʼ������
	 */
	private void onInitData(){
		sch=new HRMSchdayDr();
		if(!sch.onQuery()){
			this.messageBox_("��ʼ��ʧ��");
		}
		table.setDataStore(sch);
		table.setDSValue();
	}

	/**
	 * ����¼�
	 */
	public void onClear(){
		sch.onQuery();
		table.setDSValue();
		this.setValue("REGION_CODE", Operator.getRegion());
//		this.setValue("ADM_DATE", sch.getDBTime());
//		this.setValue("SESSION_CODE", SessionTool.getInstance().getDrSessionNow("O"));
		this.setValue("CLINIC_AREA", "");
		this.setValue("CLINIC_ROOM", "");
		this.setValue("DEPT_CODE", "");
		this.setValue("OPERATOR", "");
	}
	/**
	 * ��ѯ�¼�
	 */
	public void onQuery(){
		String regionCode=this.getValueString("REGION_CODE");
		Timestamp adm=(Timestamp)this.getValue("ADM_DATE");
//		String admDate=StringTool.getString(adm,"yyyyMMdd");
//		String sessionCode=this.getValueString("SESSION_CODE");
		String clinicArea=this.getValueString("CLINIC_AREA");
		String clinicRoom=this.getValueString("CLINIC_ROOM");
		String deptCode=this.getValueString("DEPT_CODE");
		String drCode=this.getValueString("OPERATOR");
		String filter="";
		if(!StringUtil.isNullString(regionCode)){
			filter+="REGION_CODE='" +regionCode+"' ";
		}
//		if(!StringUtil.isNullString(admDate)){
//			filter+="AND ADM_DATE='" +admDate+"' ";
//		}
//		if(!StringUtil.isNullString(sessionCode)){
//			filter+="AND SESSION_CODE='" +sessionCode+"' ";
//		}
		if(!StringUtil.isNullString(clinicArea)){
			filter+="AND CLINIC_AREA='" +clinicArea+"' ";
		}
		if(!StringUtil.isNullString(clinicRoom)){
			filter+="AND CLINICROOM_NO='" +clinicRoom+"' ";
		}
		if(!StringUtil.isNullString(deptCode)){
			filter+="AND DEPT_CODE='" +deptCode+"' ";
		}
		if(!StringUtil.isNullString(drCode)){
			filter+="AND DR_CODE='" +drCode+"'";
		}
		if(filter.indexOf("AND")==0){
			filter=filter.replaceFirst("AND", "");
		}
		sch.setFilter(filter);
		sch.filter();
		if(sch.rowCount()<=0){
			this.messageBox_("û������");
			onClear();
			return;
		}
		table.setDSValue();
	}
	/**
	 * �����¼�
	 */
	public void onSave(){
                table.acceptText();
		if(sch==null){
			return;
		}
		if(!sch.isModified()){
			this.messageBox_("û�����ݿɱ���");
			return;
		}

		String[] sql=sch.getUpdateSQL();
		TParm parm=new TParm(TJDODBTool.getInstance().update(sql));
		if(parm.getErrCode()!=0){
//			this.messageBox_(parm.getErrText());
			this.messageBox("E0001");
			return;
		}
		this.messageBox("P0001");
		sch.resetModify();
		onClear();
	}
	/**
	 * ɾ���¼�
	 */
	public void onDelete(){
		if(sch==null){
			return;
		}
		int row=table.getSelectedRow();
		if(row<0){
			this.messageBox_("û�����ݿ�ɾ��");
			return;
		}
		sch.setItem(row, "DEPT_CODE", "");
		sch.setItem(row, "DR_CODE", "");
		sch.setItem(row, "DEPT_ATTRIBUTE", "");
		sch.setActive(row,true);
		onSave();
	}
	/**
	 * Ա��TABLEֵ�ı��¼�
	 * @param tNode
	 * @return
	 */
	public boolean onTabValueChanged(TTableNode tNode){
		int row=table.getSelectedRow();
		int column=table.getSelectedColumn();
		String colName=table.getParmMap(column);
//		String oldDate=sch.getItemString(row, "ADM_DATE");
//		if(!StringUtil.isNullString(oldDate)){
//			Timestamp oldTime=StringTool.getTimestamp(oldDate,"yyyyMMdd");
//			if(StringTool.getDateDiffer(oldTime, sch.getDBTime())==-1){
//				this.messageBox_("�����޸���ǰ��¼");
//				return true;
//			}
//		}

		table.acceptText();
//		if("SESSION_CODE".equalsIgnoreCase(colName)){
//			int value=TypeTool.getInt(tNode.getValue());
//			int session=StringTool.getInt(SessionTool.getInstance().getDrSessionNow("O"));
//			if(value<session){
//				this.messageBox_("�����޸���ǰ��¼");
//				return true;
//			}
//		}
		if("DR_CODE".equalsIgnoreCase(colName)){
			String value=tNode.getValue()+"";
			if(StringUtil.isNullString(value)){
				return false;
			}
			sch.setActive(row,true);
		}
		if("DEPT_ATTRIBUTE".equalsIgnoreCase(colName)){

			int lastRow;
			if(row==sch.rowCount()-1){
				lastRow=sch.insertRow();
				sch.setItem(lastRow,"OPT_USER",Operator.getID());
				sch.setItem(lastRow, "OPT_DATE", sch.getDBTime());
				sch.setItem(lastRow, "OPT_TERM", Operator.getIP());
				sch.setActive(lastRow,false);
			}
			table.setDSValue();
			table.getTable().grabFocus();
			table.setSelectedRow(row);
			table.setSelectedColumn(column);
		}
		return false;
	}

}
