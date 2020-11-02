package com.javahis.ui.pha;

import java.awt.Component;
import java.util.Date;

import jdo.odi.OdiMainTool;
import jdo.pha.PHA_AntiBioticTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
/**
 * 
 * <p>
 * Title:������ҩҩƷ�����ֵ�
 * </p>
 * 
 * <p>
 * Description:������ҩҩƷ�����ֵ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) caoyong 2014
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author caoyong 201402119
 * @version 1.0
 */

public class PHAAntiBioticControl extends TControl {
	TTable tableM;// ����
	TTable tableD;// ҽ���� ODIInf
	private static int rows = -1;
	private static int count = 0;
	private static String ordercode="";
	public void onInit() {
		tableM=this.getTable("TABLEM");
		tableD=this.getTable("TABLED");
		getTable("TABLED").addEventListener(
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComoponent");
		this.callFunction("UI|TABLEM|addEventListener", "TABLEM->"
        		+ TTableEvent.CLICKED, this, "onTABLEClicked");
		onQueryAll();
		
	}
	
	

	/**
	 * ����
	 */
	public void onNew() {
		///this.onClear();
		String packCodestr = SystemTool.getInstance().getNo("ALL", "ODI",
				"ODIPACK_NO", "ODIPACK_NO");
		this.setValue("PHA_ANTIBIOTIC_NO", packCodestr);
	}




	/**
	 * �����¼�
	 */
	
	
	 public void onTABLEClicked(int row) {
		 if(row<0){
			 return;
		 }
		 rows=row;
		 TParm parm=tableM.getParmValue().getRow(row);
		 this.setValue("PHA_ANTIBIOTIC_NO", parm.getValue("PHA_ANTIBIOTIC_NO"));
		 this.setValue("PHA_ANTIBIOTIC_DESC", parm.getValue("PHA_ANTIBIOTIC_DESC"));
		 this.setValue("ANTIBIOTIC_CODE", parm.getValue("ANTIBIOTIC_CODE"));
		 
		 TParm result=this.getSelectParm(parm.getValue("PHA_ANTIBIOTIC_NO"));
		 count=result.getCount();
		 tableD.setParmValue(result);
		 newOrderTemp();
	    }
	 
	 
	 private void newOrderTemp() {
		 tableD.acceptText();
		 int selRow = tableD.addRow();
		 tableD.setItem(selRow, "ORDER_CODE","" );
		 tableD.setItem(selRow, "ORDER_DESC","");
		 tableD.setItem(selRow, "SPECIFICATION", "");
		 
	 }
	 public void newOrder(String tag, Object obj) {
			
			TParm parm = (TParm) obj;
			tableD.acceptText();
			int selRow = tableD.getSelectedRow();
			insertPackOrder(selRow,parm);
			
		}
	 
	 private void insertPackOrder(int selRow, TParm parm) {
		 String orderCode = parm.getValue("ORDER_CODE");
		 if (!isAnti(orderCode)) {
				this.messageBox("��ҩƷΪ�ǿ�����,��ֹ����");
				tableD.setItem(selRow, "ORDER_DESC", "");
				return;
			} else if (this.isSame(tableD, orderCode, selRow)) {
				this.messageBox( "���д�ҽ�������ٴο���") ;
					return;
		 } 
		 tableD.setItem(selRow, "ORDER_CODE", parm.getValue("ORDER_CODE"));
		 tableD.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_DESC"));
		 tableD.setItem(selRow, "SPECIFICATION", parm.getValue("SPECIFICATION"));
		 
		 if (selRow == tableD.getRowCount() - 1){
		     newOrderTemp();
	       }
		 
	 }	
	/**
	 * ����
	 */
	public void onSave() {
		if("".equals(this.getValueString("PHA_ANTIBIOTIC_NO"))){
			this.messageBox("ҽ��������벻��Ϊ��");
			return;
		}
		TParm parm=new TParm();
		TParm result=new TParm();
		TParm resulta=new TParm();
		if(!"".equals(this.getValueString("PHA_ANTIBIOTIC_NO"))){
			parm.setData("PHA_ANTIBIOTIC_NO",this.getValueString("PHA_ANTIBIOTIC_NO"));
			
		}
		if(!"".equals(this.getValueString("PHA_ANTIBIOTIC_DESC"))){
			parm.setData("PHA_ANTIBIOTIC_DESC",this.getValueString("PHA_ANTIBIOTIC_DESC"));
		}
		if(!"".equals(this.getValueString("ANTIBIOTIC_CODE"))){
			parm.setData("ANTIBIOTIC_CODE",this.getValueString("ANTIBIOTIC_CODE"));
			
		}
		
		parm.setData("OPT_USER",Operator.getID());
		parm.setData("OPT_TERM",Operator.getIP());
		parm.setData("OPT_DATE",StringTool.getTimestamp(new Date()));
		resulta=PHA_AntiBioticTool.getInstance().selectDataA(parm);//��ѯ
		
		if(resulta.getCount()<=0){
		
			result=PHA_AntiBioticTool.getInstance().insertDataA(parm);//����
		}else{
			result=PHA_AntiBioticTool.getInstance().updatedataA(parm);//�޸�
		}
		TParm parmM = tableM.getParmValue().getRow(rows);
		TParm parmD = tableD.getParmValue();
		
		
		if(rows!=-1){
			
		if(parmD.getValue("ORDER_CODE",0).length()>0){
			TParm parmQ=new TParm();
			TParm parmE=new TParm();
		
		for(int i=0;i<parmD.getCount()-1;i++){
			
			parmQ.setData("ORDER_CODE", parmD.getValue("ORDER_CODE",i));
			parmQ.setData("ORDER_DESC", parmD.getValue("ORDER_DESC",i));
			parmQ.setData("PHA_ANTIBIOTIC_NO", parmM.getValue("PHA_ANTIBIOTIC_NO"));
			parmQ.setData("SPECIFICATION", parmD.getValue("SPECIFICATION",i));
			parmQ.setData("OPT_USER",Operator.getID());
			parmQ.setData("OPT_TERM",Operator.getIP());
			parmQ.setData("OPT_DATE",StringTool.getTimestamp(new Date()));
			
			 parmE=PHA_AntiBioticTool.getInstance().selectDataB(parmQ);
			 
			 if(parmE.getCount()<=0){
				 result=PHA_AntiBioticTool.getInstance().insertDataB(parmQ);
			 }else if(parmM.getValue("PHA_ANTIBIOTIC_NO").equals(parmE.getValue("PHA_ANTIBIOTIC_NO",0))){
				 result=PHA_AntiBioticTool.getInstance().updatedataB(parmQ); 
			 }else if(!parmM.getValue("PHA_ANTIBIOTIC_NO").equals(parmE.getValue("PHA_ANTIBIOTIC_NO",0))){
				 ordercode=parmD.getValue("ORDER_CODE",i);
				 this.messageBox( parmD.getValue("ORDER_DESC",i)+"����������ҩƷ���࣬�����Ա���");
				 return;
			 }
			 
		   }
			}
		 }
		
		if (result.getErrCode() < 0) {
			this.messageBox("����ʧ��");
			onQuery();
			return;
		}
		this.messageBox("����ɹ�");
		onQueryAll();
	}
	/**
	 * ɾ��
	 */
	public void onDelete() {
		TParm parm=new TParm();
		TParm result=new TParm();
		
		if(tableM.getSelectedRow()>=0&&tableD.getSelectedRow()<0){
			if(count<=0){
			 parm=tableM.getParmValue().getRow(tableM.getSelectedRow());
			 result=PHA_AntiBioticTool.getInstance().deletedataA(parm);
			 if (result.getErrCode() < 0) {
					this.messageBox("ɾ��ʧ��");
					onQuery();
					return;
				}
				this.messageBox("ɾ���ɹ�");
			 }else{
					this.messageBox("�˿���ҩƷ�з���ҽ����ϸ����ֱ��ɾ��!");
					return;
				}
		} 
		if(!"".equals(ordercode)&&ordercode!=null){
			tableD.removeRow(tableD.getSelectedRow());
			ordercode="";
		}else{
			if(tableM.getSelectedRow()>=0&&tableD.getSelectedRow()>=0){
			     parm=tableD.getParmValue().getRow(tableD.getSelectedRow());
			     result=PHA_AntiBioticTool.getInstance().deletedataB(parm);
			}
		    
			if (result.getErrCode() < 0) {
				this.messageBox("ɾ��ʧ��");
				onQuery();
				return;
			}
			this.messageBox("ɾ���ɹ�");
			tableD.removeRow(tableD.getSelectedRow());
		}
		
		//this.onClear();
		onQueryAll();
	}

	

	/**
	 * ��ѯ
	 */
	public void onQueryAll() {
		
		TParm parm = PHA_AntiBioticTool.getInstance().selectAllData();
		
		this.tableM.setParmValue(parm);
	}
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		tableM.removeRowAll();
		TParm parm =new TParm();
		if(!"".equals(this.getValueString("PHA_ANTIBIOTIC_DESC"))){
			parm.setData("PHA_ANTIBIOTIC_DESC",this.getValueString("PHA_ANTIBIOTIC_DESC"));
		}
		if(!"".equals(this.getValueString("ANTIBIOTIC_CODE"))){
			parm.setData("ANTIBIOTIC_CODE",this.getValueString("ANTIBIOTIC_CODE"));
			
		}
		TParm result = PHA_AntiBioticTool.getInstance().selectM(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ����");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("û�в�ѯ����");
			return;
		}
		this.tableM.setParmValue(result);
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearValue("PHA_ANTIBIOTIC_NO;PHA_ANTIBIOTIC_DESC;ANTIBIOTIC_CODE");
		tableM.removeRowAll();
		tableD.removeRowAll();
	}

	/**
	 * �õ�TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	
	public void onCreateEditComoponent(Component com, int row, int column) {
		// �õ�����
		String columnName = this.getFactColumnName("tableD", column);
		if (!columnName.contains("ORDER_DESC"))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		TParm parm = new TParm();
		parm.setData("PACK", "DEPT", Operator.getDept());
		// ���õ����˵�
		textFilter.setPopupMenuParameter("ITEM", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newOrder");

	}
	public String getFactColumnName(String tableTag, int column) {
		int col = this.getThisColumnIndex(column, tableTag);
		return this.getTable(tableTag).getDataStoreColumnName(col);
	}
	 /**
	 * �õ�����֮ǰ���к�
	 * add caoyong 20131108
	 * @param column
	 *            int
	 * @return int
	 */
	public int getThisColumnIndex(int column, String table) {
		return this.getTable(table).getColumnModel().getColumnIndex(column);
	}
	
	public TParm getSelectParm(String antNo){
		TParm parm=new TParm();
		TParm result= new TParm ();
		parm.setData("PHA_ANTIBIOTIC_NO",antNo);
		
		
		result=PHA_AntiBioticTool.getInstance().selectDataC(parm);
		return result;
		
	}
 	private boolean isAnti(String orderCode) {
   		TParm actionDs = new TParm();
   		actionDs.setData("ORDER_CODE", orderCode);
   		TParm resultDs = OdiMainTool.getInstance().queryPhaBase(actionDs);
   		if (resultDs.getValue("ANTIBIOTIC_CODE", 0).equals("")) {
   			return false;
   		} else {
   			return true;
   		}
   	}
 	  /**
   	 * 
   	 * @param table
   	 * @param orderCode
   	 * @param row
   	 * @return
   	 */
   	public boolean isSame(TTable table, String orderCode, int row) {
   		table.acceptText();
   		for (int i = 0; i < table.getRowCount(); i++) {
   			if (i == row)
   				continue;
   			String orderCodeC = table.getItemString(i, "ORDER_CODE");
   			if (orderCodeC.equals(orderCode))
   				return true;
   		}
   		return false;
   	}
}
