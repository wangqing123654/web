package com.javahis.ui.opd;

import java.awt.Component;

import jdo.opd.ODO;
import jdo.opd.Order;
import jdo.opd.OrderList;
import jdo.opd.PrescriptionList;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: ����ҩƷPanel
 * </p>
 * 
 * <p>
 * Description:����ҩƷPanel
 * </p>
 * 
 * <p>
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author ehui 20080924
 * @version 1.0
 */
public class OPDMainCtrlDrugControl extends TControl {
	ODO odo;
	int selectRow = -1;
	int orignalrows = 0;
	int linkno=0;
	int linkOrder=-1;
	boolean linkFlg=false;
	String nullStr="";
	public void onInit() {
		super.onInit();
		callFunction("UI|TABLECONTROLDRUG|addEventListener",
				"TABLECONTROLDRUG->" + TTableEvent.CLICKED, this,
				"onTableClicked");
		this.addEventListener("TABLECONTROLDRUG->" + TTableEvent.CHANGE_VALUE,
				"onTableChangeValue");
		TTable table=(TTable)this.callFunction("UI|TABLECONTROLDRUG|getThis");
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED,this, "onMainFlg");
		//tableר�õļ���
        callFunction("UI|TABLECONTROLDRUG|addEventListener",TTableEvent.CREATE_EDIT_COMPONENT,this,"onCreateEditComponent");
		initCtrlDrug(0);
	}
	/**
	 * �����ӵ�checkbox�ĵ���¼�
	 * @param o Object
	 */
	public void onMainFlg(Object obj){
		
		TTable table=(TTable)obj;
		table.acceptText();
//		// �ڼ��Ŵ���
//		int i = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
//		// TABLE�ϵڼ���
//		int c = table.getSelectedRow();
//		// �ڼ���
//		int column = table.getSelectedColumn();
//		Object value=table.getValueAt(c,column);
//		// ȡ��һ��Order
//		Order o = odo.getPrescriptionList().getOrderList("2", i).getOrder(c);
//		o.modifyLinkmainFlg(TCM_Transform.getString(value));
//		if("Y".equalsIgnoreCase(TCM_Transform.getString(value))){
//			linkno++;
//		}else{
//			linkno=--linkno;
//		}
//		o.modifyLinkNo(linkno);
//		this.callFunction("UI|TABLECONTROLDRUG|setValueAt",linkno,c,column+1);
	}
	  /**
	   * 
	   * @param com
	   * @param row
	   * @param column
	   */
	  public void onCreateEditComponent(Component com,int row,int column)
	    {
		  selectRow=row;
	        if(column != 3)
	            return;
	        if(!(com instanceof TTextField))
	            return;
	        TTextField textfield = (TTextField)com;
	        textfield.onInit();
	        TParm parm=new TParm();
	        parm.setData("RX_TYPE",2);
	        //��table�ϵ���text����sys_fee��������
	        textfield.setPopupMenuParameter("bbb",getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),parm);

	        //����text���ӽ���sys_fee�������ڵĻش�ֵ
	        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE,this,"popReturn");
	        
	    }
	  /**
	   * 
	   * @param tag
	   * @param obj
	   */
	  public void popReturn(String tag,Object obj)
	    {
			TParm parm = (TParm) obj;
			this.callFunction("UI|TABLECONTROLDRUG|acceptText");
			OrderList ol = odo.getPrescriptionList().getOrderList("2",
					this.getValueInt("RX_NO") - 1);
			if(ol==null||ol.size()<1||ol.size()-1<selectRow)
				return;
			Order o = ol.getOrder(selectRow);
			String[] ctz=new String[]{
					odo.getReg().getCtz1Code(),odo.getReg().getCtz2Code(),odo.getReg().getCtz3Code()
			};
			OdoUtil.fillOrder(o,parm,ctz);
			TParm parmOrder=o.getParm();
			TParm parmtable = OdoUtil.calculatePayAmount(parmOrder);
			this.callFunction("UI|TABLECONTROLDRUG|setRowParmValue", selectRow,
					parmtable);	
			onNew();
		}
	/**
	 * ��ʼ������
	 * 
	 * @param int
	 *            indexOfPrescription TParm���±�
	 */
	public void initCtrlDrug(int indexOfPrescription) {
		if (odo == null){
			return;
		}
		Order o;
		if(odo.getPrescriptionList().getOrderList("2",
				indexOfPrescription)==null){
			OrderList ol=odo.getPrescriptionList().newOrderList("2");
			o=ol.newOrder();
		}else{
			OrderList ol=odo.getPrescriptionList().getOrderList("2",
					indexOfPrescription);
			if(!StringUtil.isNullString(ol.getOrder(ol.size()-1).getOrderCode())){
				o=ol.newOrder();
			}else{
				o=ol.getOrder(0);
			}
		}
		// ��,30,boolean;��,30,boolean;���,60;ҽ��,100;����,100;��ҩ��λ,80;Ƶ��,80;;��,80;�շ�,100;����,100;����,80;
		// ��ҩ��λ,80;��,30,boolean;�ܼ�,200;�ۿ۽��,200;Ӧ�����,200;
		// ҽ����ע,200;��ʿ��ע,200;ִ�п���,80;����,30,boolean;�������,80;�շ�ʱ��,200;��ҩʱ��,200;ҽ��ȷ��,200
		TParm rxparm = OdoUtil.prepareRxNoTParm(odo, "2");
		
		if (rxparm == null){
			callFunction("UI|RX_NO|setParmValue", new TParm());
			setValue("RBORDER_DEPT_CODE",o.getExecDeptCode());
			setValue("RX_NO", "");
			return;
		}
			
		callFunction("UI|RX_NO|setParmValue", rxparm);
		if(odo.getPrescriptionList().getOrderList("2",
				indexOfPrescription)==null){
			setValue("RBORDER_DEPT_CODE", o.getExecDeptCode());
			setValue("RX_NO", "");
		}else{
			setValue("RBORDER_DEPT_CODE", o.getExecDeptCode());	
			setValue("RX_NO", odo.getPrescriptionList().getOrderList("2",
					indexOfPrescription).getPresrtNo());
		}
//		setValue("RBORDER_DEPT_CODE", rborderDeptCode);
		// +1:TParm �±껻���"RX_NO"���±�
		
//		initTable(indexOfPrescription);
	}

	/**
	 * ��ʼ��TABLE
	 * 
	 * @param rxNo
	 *            TParm ���±꣬��RX_NO-1
	 */
	public void initTable(int rxNo) {
		callFunction("UI|TABLECONTROLDRUG|setParmValue",new TParm());
		callFunction("UI|TABLECONTROLDRUG|removeRowAll");
		if (rxNo < 0 || rxNo >= odo.getPrescriptionList().getGroupPrsptSize("2")){
			return;
		}
		
            
		PrescriptionList pl=odo.getPrescriptionList();
		if(pl==null)
			return;
		OrderList ol=pl.getOrderList("2", rxNo);
		if(ol==null||ol.size()<1)
			return;
		TParm parm=odo.getPrescriptionList().getOrderList("2", rxNo).getParm(ol.PRIMARY);
		
		if (parm.getData("PAYAMOUNT",0) == null) {
			// ����Ӧ�������ܽ��-�ۿ۽��
			OdoUtil.calculatePayAmount(parm);

		}
		if(parm.getCount() <= 0)
			return;	

		callFunction("UI|TABLECONTROLDRUG|setParmValue", parm);

	}

	/**
	 * TABLE����¼�
	 * 
	 * @param row
	 */
	public void onTableClicked(int row) {
		 String message = (String)callFunction("UI|delete|getActionMessage");
		 String s[] = StringTool.getHead(message, "|");
		 if(s!=null&&!callFunction("UI|getTag").equals(s[0])){
			 callFunction("UI|" + s[0] + "|clearTableSelection");
		 }
		 callFunction("UI|delete|setActionMessage", callFunction("UI|getTag")
		 + "|onDelete");
		selectRow=row;
	}
	public void clearTableSelection()
	{
		TTable table=(TTable)this.callFunction("UI|TABLECONTROLDRUG|getThis");
		table.clearSelection();
	}
	/**
	 * TABLEֵ�ı��¼�
	 * 
	 * @param tableNode
	 */
	public void onTableChangeValue(Object tableNode) {
		TParm parm = new TParm();
		TTableNode mytableNode = (TTableNode) tableNode;
		String value = TCM_Transform.getString(mytableNode.getValue());
		// �ڼ��Ŵ���
		int i = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		// TABLE�ϵڼ���
		int c = mytableNode.getRow();
		// �ڼ���
		int column = mytableNode.getColumn();
		// ȡ��һ��Order
		OrderList ol=odo.getPrescriptionList().getOrderList("2", i);
		Order o =ol .getOrder(c);
		TTable table = (TTable) this.callFunction("UI|TABLECONTROLDRUG|getThis");
		String columnName = table.getParmMap(column);
		if(StringUtil.isNullString(o.getOrderCode())){
			return;
		}
		/*
		 * RELEASE_FLG;LINKMAIN_FLG;LINK_NO;ORDER_CODE;MEDI_QTY;MEDI_UNIT;
		 * FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OPD_GIVEBOX_FLG;
		 * OWN_PRICE;DISPENSE_QTY;DISPENSE_UNIT;OWN_AMT;
		 * PAYAMOUNT;AR_AMT;DESCRIPTION;NS_NOTE;RBORDER_DEPT_CODE;
		 * URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE;PHA_CHECK_DATE
		 */
		// �Ա�
		if ("RELEASE_FLG".equalsIgnoreCase(columnName)) {
			o.modifyReleaseFlg(TCM_Transform.getString(value));
			return;
		}
		// ��
		if ("LINKMAIN_FLG".equalsIgnoreCase(columnName)) {
			o.modifyLinkmainFlg(TCM_Transform.getString(value));
			linkOrder=c;
			if ("Y".equalsIgnoreCase(TCM_Transform.getString(value))) {
				linkno++;
				linkFlg=true;
			} else {
				linkno--;
				linkFlg=false;
			}
			o.modifyLinkNo(TCM_Transform.getString(linkno));
			callFunction("UI|TABLECONTROLDRUG|setValueAt", linkno, c, column + 1);
			return;
		}
		// ���
		if ("LINK_NO".equalsIgnoreCase(columnName)) {
			
			int tempNo=TCM_Transform.getInt(value);
			if(tempNo>linkno)
			{
				this.messageBox_("�����Ӻ�û������");
				return;
			}
			if(tempNo==0){
				linkFlg=false;
			}
			o.modifyLinkNo(TCM_Transform.getString(tempNo));
			return;
		}
		
		// ��ҩ
		if ("GIVEBOX_FLG".equalsIgnoreCase(columnName)) {

			// the value is original value, not the last 1,so the real value is
			// opposite to itself
//			this.messageBox_(value);
			o.modifyGiveboxFlg(TCM_Transform.getString(value));
			OdoUtil.calcuQty(o);
			TParm boxparm = o.getParm();
			
			boxparm.setData("PAYAMOUNT", o.getOwnAmt() - o.getArAmt());
			boxparm.setData("DOSAGE_QTY", o.getDispenseQty());
			this.callFunction("UI|TABLECONTROLDRUG|setRowParmValue", selectRow,
					boxparm);
			return;
		}
		// ����
		if ("URGENT_FLG".equalsIgnoreCase(columnName)) {
			o.modifyUrgentFlg(TCM_Transform.getString(value));
			return;
		}
		// ����
		if ("MEDI_QTY".equalsIgnoreCase(columnName)) {
			if(TCM_Transform.getDouble(value)==o.getMediQty()){
				return;
			}
			o.modifyMediQty(TCM_Transform.getDouble(value));
			parm = OdoUtil.calcuQty(o).getParm();
			parm.setData("PAYAMOUNT", o.getOwnAmt() - o.getArAmt());
			this.callFunction("UI|TABLECONTROLDRUG|setRowParmValue", selectRow, parm);

			return;
		}
		// Ƶ��
		if ("FREQ_CODE".equalsIgnoreCase(columnName)) {
			if(value.equalsIgnoreCase(o.getFreqCode())){
				return;
			}
//			this.messageBox_(value);
			o.modifyFreqCode(value);
			parm = OdoUtil.calcuQty(o).getParm();
			parm.setData("PAYAMOUNT", o.getOwnAmt() - o.getArAmt());
			this.callFunction("UI|TABLECONTROLDRUG|setRowParmValue", selectRow, parm);
			if("Y".equalsIgnoreCase(o.getLinkmainFlg()) ){
				String linkNoTemp=TCM_Transform.getString(o.getLinkNo());
				for(int x=0;x<ol.size();x++){
					
					Order oTemp=ol.getOrder(x);
					if(oTemp==null){
						return;
					}
					if(linkNoTemp.equals(oTemp.getLinkNo())){
//						this.messageBox_(oTemp.getSeqNo());
						oTemp.modifyFreqCode(o.getFreqCode());
						parm = OdoUtil.calcuQty(o).getParm();
						parm.setData("PAYAMOUNT", o.getOwnAmt() - o.getArAmt());
						this.callFunction("UI|TABLECONTROLDRUG|setRowParmValue", oTemp.getSeqNo()-1, parm);
					}
				}
			}
			return;
		}
		// ;��
		if ("ROUTE_CODE".equalsIgnoreCase(columnName)) {
			if(value.equalsIgnoreCase(o.getRouteCode())){
				return;
			}
			o.modifyRouteCode(value);
			if("Y".equalsIgnoreCase(o.getLinkmainFlg()) ){

				String linkNoTemp=TCM_Transform.getString(o.getLinkNo());
				for(int x=0;x<ol.size();x++){
					
					Order oTemp=ol.getOrder(x);
					if(oTemp==null){
						return;
					}
					if(linkNoTemp.equals(oTemp.getLinkNo())){
						oTemp.modifyFreqCode(o.getFreqCode());
						parm.setData("PAYAMOUNT", o.getOwnAmt() - o.getArAmt());
						this.callFunction("UI|TABLECONTROLDRUG|setRowParmValue", oTemp.getSeqNo()-1, parm);
					}
				}
			
			}
			return;
		}
		// �շ�
		if ("TAKE_DAYS".equalsIgnoreCase(columnName)) {
			if(TCM_Transform.getInt(value)==o.getTakeDays()){
				return;
			}
			o.modifyTakeDays(TCM_Transform.getInt(value));
			parm = OdoUtil.calcuQty(o).getParm();
			parm.setData("PAYAMOUNT", o.getOwnAmt() - o.getArAmt());
			this.callFunction("UI|TABLECONTROLDRUG|setRowParmValue", selectRow, parm);
			if("Y".equalsIgnoreCase(o.getLinkmainFlg()) ){

				String linkNoTemp=TCM_Transform.getString(o.getLinkNo());
				for(int x=0;x<ol.size();x++){
					
					Order oTemp=ol.getOrder(x);
					if(oTemp==null){
						return;
					}
					if(linkNoTemp.equals(oTemp.getLinkNo())){
//						this.messageBox_(oTemp.getSeqNo());
						oTemp.modifyFreqCode(o.getFreqCode());
						parm = OdoUtil.calcuQty(o).getParm();
						parm.setData("PAYAMOUNT", o.getOwnAmt() - o.getArAmt());
						this.callFunction("UI|TABLECONTROLDRUG|setRowParmValue", oTemp.getSeqNo()-1, parm);
					}
				}
			
			}
			return;
		}
		// ����
		if ("DISPENSE_QTY".equalsIgnoreCase(columnName)) {
			if(TCM_Transform.getDouble(value)==o.getDispenseQty()){
				return;
			}
			if("Y".equalsIgnoreCase(o.getGiveboxFlg())){
				return;
			}
			o.modifyDispenseQty(TCM_Transform.getDouble(value));
			parm = OdoUtil.calcuMediQty(o).getParm();
			parm.setData("PAYAMOUNT", o.getOwnAmt() - o.getArAmt());
			this.callFunction("UI|TABLECONTROLDRUG|setRowParmValue", selectRow, parm);
			return;
		}
		// ��ע
		if ("DR_NOTE".equalsIgnoreCase(columnName)) {
			o.modifyDrNote(value);
			return;
		}
	}

	/**
	 * ������comboֵ�ı��¼�
	 */
	public void onRxNoChanged() {
		if(this.getValue("RX_NO")!=null&&!"".equals(((String)this.getValue("RX_NO")))){
			initTable(TCM_Transform.getInt(this.getValue("RX_NO")) - 1);
		}
			
		
	}
	
	/**
	 * TABLE����һ������
	 */
	public void onNew() {
		if (odo == null)
			return;
		int index = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		OrderList ol=odo.getPrescriptionList().getOrderList("2", index);
		Order o = ol.newOrder();
		
		if(linkFlg){
			Order orderMain=ol.getOrder(linkOrder);
			OdoUtil.linkOrder(o, orderMain,linkno);
		}
		TParm parm = o.getParm();
		TParm parmtable=OdoUtil.calculatePayAmount(parm);
		this.callFunction("UI|TABLECONTROLDRUG|addRow", parmtable);
	}

	/**
	 * TABLEɾ��һ������
	 */
	public void onDelete() {
		if (selectRow == -1)
			return;
		callFunction("UI|TABLECONTROLDRUG|removeRow", selectRow);
		OrderList ol = odo.getPrescriptionList().getOrderList("2",
				TCM_Transform.getInt(this.getValue("RX_NO")) - 1);
		Order o = ol.getOrder(selectRow);
		if (!OdoUtil.isChargedOrder(o)) {
			ol.removeData(o);
			onNew();
			return;
		}
		
	}

	/**
	 * ����һ�Ŵ���ǩ ���ܣ�1.������ڵ�TABLE�е����� 2.�½�ͬ�������͵Ĵ���ǩ 3.TABLE����������
	 */
	public void onNewOrderList() {
		callFunction("UI|TABLECONTROLDRUG|setParmValue",new TParm());
		callFunction("UI|TABLECONTROLDRUG|removeRowAll");
		odo.getPrescriptionList().newOrderList("2");
		initCtrlDrug(odo.getPrescriptionList().getGroupPrsptSize("2") - 1);
		
	}

	/**
	 * ɾ��һ�Ŵ���ǩ
	 */
	public void onDeleteOrderList() {
		callFunction("UI|TABLECONTROLDRUG|setParmValue",new TParm());
		callFunction("UI|TABLECONTROLDRUG|removeRowAll");
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		OrderList ol=odo.getPrescriptionList().getOrderList("2", rxNo);
		if (rxNo == -1) {
			return;
		}
		if (!OdoUtil.deleteOrderList(ol)) {
			return;
		}
		
//		odo.getPrescriptionList().removeOrderList("2", rxNo);
		initCtrlDrug(rxNo-1);
	}

	/**
	 * ����ODO����
	 * 
	 * @param odo
	 */
	public void setOdo(Object odo) {

		if (odo == null)
			return;
		this.odo = (ODO) odo;
		initCtrlDrug(0);
	}
	/**
	 * ��ս���
	 */
	public void onClear(){
		this.setValue("RBORDER_DEPT_CODE", nullStr);
		this.setValue("RX_NO", nullStr);
		this.setValue("EKT", nullStr);
		this.setValue("TOT_PAY", nullStr);
		this.setValue("SHOW_GOODS", false);
		callFunction("UI|TABLECONTROLDRUG|setParmValue",new TParm());
		callFunction("UI|TABLECONTROLDRUG|removeRowAll");
	}
	public Object callMessage(String message, Object parm) {
		return super.callMessage(message, parm);
	}
}
