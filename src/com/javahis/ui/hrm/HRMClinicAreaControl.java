package com.javahis.ui.hrm;

import jdo.hrm.HRMClinicArea;
import jdo.hrm.HRMClinicRoom;
import jdo.sys.MessageTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.StringUtil;
/**
 * <p> Title: ���������������Ӧ������ </p>
 *
 * <p> Description: ���������������Ӧ������ </p>
 *
 * <p> Copyright: javahis 20090922 </p>
 *
 * <p> Company:JavaHis </p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMClinicAreaControl extends TControl {
	//����TABLE�����TABLE
	private TTable areaTable,roomTable;
	//��������
	private HRMClinicArea area;
	//������
	private HRMClinicRoom room;
	//�ַ�������
	private StringUtil util;
	//TABLE��TAG����TABLE����ʱ��¼��TAG����ɾ����ť����ʱʹ��
	private String tableName;
	/**
	 * ��ʼ���¼�
	 */
	public void onInit() {
		super.onInit();
		//���
		onClear();
	}
	/**
	 * ���
	 */
	private void onClear() {
		//��ʼ���ؼ�
		initComponent();
		//��ʼ������
		initData();
	}
	/**
	 * ��ʼ������
	 */
	private void initData() {
		//��ʼ����������
		area=new HRMClinicArea();
		area.onQuery();
		area.newRow(-1);
		//����������ֵ������TABLE��
		areaTable.setDataStore(area);
		areaTable.setDSValue();
		room=new HRMClinicRoom();
		roomTable.removeRowAll();
	}
	/**
	 * ��ʼ���ؼ�
	 */
	private void initComponent() {
		areaTable=(TTable)this.getComponent("AREA_TABLE");
		roomTable=(TTable)this.getComponent("ROOM_TABLE");
		//����TABLEֵ�ı��¼�
		areaTable.addEventListener("AREA_TABLE->"+ TTableEvent.CHANGE_VALUE,this, "onAreaValueChange");
		//���TABLEֵ�ı��¼�
		roomTable.addEventListener("ROOM_TABLE->"+ TTableEvent.CHANGE_VALUE,this, "onRoomValueChange");
		//�ײ�����CHECK_BOX�¼�
		roomTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
		"onCheckClicked");
	}
	/**
	 * �ײ������CHECK_BOX�¼�
	 * @param obj
	 * @return
	 */
	public boolean onCheckClicked(Object obj){
		TTable table=(TTable)obj;
		table.acceptText();
		table.setDSValue();
		return false;
	}
	/**
	 * ����TABLE�����¼�
	 */
	public void onAreaClick(){
		int row=areaTable.getSelectedRow();
		if(row<0){
			return;
		}
		String areaCode=area.getItemString(row, "CLINICAREA_CODE");
		if(util.isNullString(areaCode)){
			return;
		}
		if("ROOM_TABLE".equalsIgnoreCase(tableName)&&room.isModified()){
			int yesNo=this.messageBox("��ʾ��Ϣ", "�Ƿ񱣴������Ϣ", this.YES_NO_OPTION);
			if(yesNo==0){
				onNewRoom();
			}
		}
		//��������TABLE��ѡ����������룬��ʼ�������󲢸�ֵ�����TABLE
		room.onQuery(areaCode,area.getItemString(row, "REGION_CODE"));
		room.newRow(-1);
		roomTable.setDataStore(room);
		roomTable.setDSValue();
		tableName="AREA_TABLE";
	}
	/**
	 * ���TABLE�����¼�
	 */
	public void onRoomClick(){
		int row=roomTable.getSelectedRow();
		if(row<0){
			return;
		}
		//�����һ�β�����TABLE������TABLE�����������������ݸı䣬����ʾ�Ƿ񱣴棬ѡ���򱣴�
		if("AREA_TABLE".equalsIgnoreCase(tableName)&&area.isModified()){
			int yesNo=this.messageBox("��ʾ��Ϣ", "�Ƿ񱣴�������Ϣ", this.YES_NO_OPTION);
			if(yesNo==0){
				//�����������������
				onNewArea();
			}
		}
		tableName="ROOM_TABLE";
	}
	/**
	 * ɾ��
	 */
	public void onDelete(){
		//���ݲ�����TABLE��TAG����ʵ����TABLE
		TTable table=(TTable)this.getComponent(tableName);
		int row=table.getSelectedRow();
		TParm result;
		//��������TABLEɾ��ѡ���в�����
		if("ROOM_TABLE".equalsIgnoreCase(tableName)){
			room.deleteRow(row);
			result=room.onSave();
			if(result.getErrCode()!=0){
				this.messageBox("E0001");
			}else{
				this.messageBox("P0001");
			}
			onClear();
			return;
		}
		//��֤������
		result=room.deleteByAreaCode();
		if(result.getErrCode()!=0){
			this.messageBox("E0001");
			return;
		}
		//ɾ����������֤ɾ�����ݲ�����
		area.deleteRow(row);
		result=area.onSave();
		if(result.getErrCode()!=0){
			this.messageBox("E0001");
			return;
		}
		this.messageBox("P0001");
		onClear();
	}
	/**
	 * ����
	 */
	public void onSave(){
            areaTable.acceptText();
            TParm result=new TParm();
            if(area.isModified()){
                result=area.onSave();
            }
            if(result.getErrCode()!=0){
                this.messageBox_("��������ʧ��");
                return;
            }else{
                area.resetModify();
            }
            //		this.messageBox_(room.isModified());
            if(room.isModified()){
                // System.out.println("room is modify");
               // room.showDebug();
                result=room.onSave();
            }
            if(result.getErrCode()!=0){
                this.messageBox_("��䱣��ʧ��");

                return;
            }else{
                    room.resetModify();
            }
            this.messageBox("P0001");
	}
	/**
	 * ����������Ϣ
	 */
	public void onNewArea(){
		if(area.isModified()){
			TParm result=area.onSave();
			if(result.getErrCode()!=0){
				this.messageBox("E0002");
			}else{
				this.messageBox("P0002");
			}
		}else{
			this.messageBox_("û�����ݿɱ���");
		}
	}
	/**
	 * ���������Ϣ
	 */
	public void onNewRoom(){
		if(room.isModified()){
			TParm result=room.onSave();
			if(result.getErrCode()!=0){
				this.messageBox("E0002");
			}else{
				this.messageBox("P0002");
			}
		}else{
			this.messageBox_("û�����ݿɱ���");
		}
	}
	/**
	 * ����ֵ�ı��¼��������벻�գ���д��������ʱ����һ��
	 * @param tNode
	 */
	public boolean onAreaValueChange(TTableNode tNode){
		int row=tNode.getRow();
		int column=tNode.getColumn();
		String colName=areaTable.getParmMap(column);
		if(!"CLINICAREA_CODE".equalsIgnoreCase(colName)){
			if(util.isNullString(area.getItemString(row, "CLINICAREA_CODE"))){
				this.messageBox_("������д��������");
				return true;
			}
		}
		if("CLINIC_DESC".equalsIgnoreCase(colName)){
			String value=tNode.getValue()+"";
			if(util.isNullString(value)){
				return true;
			}
			String oldValue=tNode.getOldValue()+"";
			if(value.equalsIgnoreCase(oldValue)){
				return true;
			}
			MessageTool d=new MessageTool();
			String py=d.getPy(value);
			area.setActive(row,true);
			area.setItem(row, "PY1", py);
			if(row>0&&!util.isNullString(area.getItemString(area.rowCount()-1, "CLINIC_DESC"))){
				area.newRow(-1);
			}else if(row==0){
				area.newRow(-1);
			}

			areaTable.setDSValue();
			areaTable.getTable().grabFocus();
			areaTable.setSelectedRow(row);
			areaTable.setSelectedColumn(column+1);

		}
		return false;
	}
	/**
	 * ���ֵ�ı��¼��������벻�գ���д�������ʱ����һ��
	 * @param tNode
	 */
	public boolean onRoomValueChange(TTableNode tNode){
		int row=tNode.getRow();
		int column=tNode.getColumn();
		String colName=roomTable.getParmMap(column);
		if(!"CLINICROOM_NO".equalsIgnoreCase(colName)){
			if(util.isNullString(room.getItemString(row, "CLINICROOM_NO"))){
				this.messageBox_("������д���Ҵ���");
				return true;
			}
		}
		if("CLINICROOM_DESC".equalsIgnoreCase(colName)){
			String value=tNode.getValue()+"";
			if(util.isNullString(value)){
				return true;
			}
			String oldValue=tNode.getOldValue()+"";
			if(value.equalsIgnoreCase(oldValue)){
				return true;
			}
			MessageTool d=new MessageTool();
			String py=d.getPy(value);
			room.setActive(row,true);
			room.setItem(row, "PY1", py);
			if(row>0&&!util.isNullString(room.getItemString(room.rowCount()-1, "CLINICROOM_NO"))){
				room.newRow(-1);
			}else if(row==0){
				room.newRow(-1);
			}

			roomTable.setDSValue();
			roomTable.getTable().grabFocus();
			roomTable.setSelectedRow(row);
			roomTable.setSelectedColumn(column+1);
		}
		return false;
	}

}

