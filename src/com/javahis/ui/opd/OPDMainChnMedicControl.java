package com.javahis.ui.opd;

import java.awt.Component;

import jdo.opd.ODO;
import jdo.opd.OPDSysParmTool;
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
 * Title: ��ҩPanel
 * </p>
 *
 * <p>
 * Description:��ҩPanel
 * </p>
 *
 * <p>
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 20080924
 * @version 1.0
 */
public class OPDMainChnMedicControl extends TControl {
	ODO odo;
	int selectRow=-1;
	int orignalrows=0;
	int selectcolumn=-1;
	String nullStr="";
	public void onInit() {
		super.onInit();
		callFunction("UI|TABLECHNMEDIC|addEventListener","TABLECHNMEDIC->"+TTableEvent.CLICKED,this,"onTableClicked");
		this.addEventListener("TABLECHNMEDIC->" + TTableEvent.CHANGE_VALUE,
		"onTableChangeValue");
		//tableר�õļ���
        callFunction("UI|TABLECHNMEDIC|addEventListener",TTableEvent.CREATE_EDIT_COMPONENT,this,"onCreateEditComponent");
        TParm parm=OPDSysParmTool.getInstance().selectAll();
        this.setValue("DCT_TAKE_DAYS", StringTool.getInt(parm.getValue("DCT_TAKE_DAYS",0)));
        this.setValue("DCT_TAKE_QTY", StringTool.getInt(parm.getValue("DCT_TAKE_QTY",0)));
		initChnMedic(0);
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
		  selectcolumn=column;
	        if(column != 0&&column!=3&&column!=6&&column!=9){
	        	return;
	        }

	        if(!(com instanceof TTextField)){
	        	return;
	        }

	        TTextField textfield = (TTextField)com;
	        textfield.onInit();
	        //��table�ϵ���text����sys_fee��������
	        textfield.setPopupMenuParameter("bbb",getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"));
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
	        TParm parm=(TParm )obj;
	        this.callFunction("UI|TABLECHNMEDIC|acceptText");
	        OrderList ol=odo.getPrescriptionList().getOrderList("3", this.getValueInt("RX_NO")-1);
			if(ol==null||ol.size()<1||ol.size()-1<selectRow)
				return;
	        Order o= ol.getOrder(OdoUtil.calculateOrderIndexForChnMed(selectRow, selectcolumn));
//	        o.initParm(parm);
	        o.modifyOrderCode(parm.getValue("ORDER_CODE"));
	        o.modifyOrderDesc(parm.getValue("ORDER_DESC"));
	        o.modifyGoodsDesc(parm.getValue("GOODS_DESC"));
	        String[] ctz=new String[]{
					odo.getReg().getCtz1Code(),odo.getReg().getCtz2Code(),odo.getReg().getCtz3Code()
			};
			OdoUtil.fillOrder(o,parm,ctz);
			this.callFunction("UI|TABLECHNMEDIC|setValueAt", o.getOrderDesc(),selectRow,selectcolumn);
			this.callFunction("UI|TABLECHNMEDIC|setValueAt", o.getMediQty(),selectRow,selectcolumn+1);
			if(selectcolumn==9){
	        	onNew();
	        }
	    }
	/**
	 * ��ʼ������
	 * @param  int indexOfPrescription TParm���±�
	 */
	public void initChnMedic(int indexOfPrescription){
		if(odo== null)
			return;
		if(odo.getPrescriptionList().getOrderList("3", indexOfPrescription)==null){
			OrderList ol=odo.getPrescriptionList().newOrderList("3");
			for(int i=0;i<4;i++){
				Order o=ol.newOrder();
				o.modifyOrderDesc("");
				o.modifyOrderCode("");
				o.modifyDctexcepCode("");
				o.modifyMediQty(0.0);
			}
		}
		//��,30,boolean;��,30,boolean;���,60;ҽ��,100;����,100;��ҩ��λ,80;Ƶ��,80;;��,80;�շ�,100;����,100;����,80;
		//��ҩ��λ,80;��,30,boolean;�ܼ�,200;�ۿ۽��,200;Ӧ�����,200;
		//ҽ����ע,200;��ʿ��ע,200;ִ�п���,80;����,30,boolean;�������,80;�շ�ʱ��,200;��ҩʱ��,200;ҽ��ȷ��,200
		TParm rxparm = OdoUtil.prepareRxNoTParm(odo, "3");
		if (rxparm == null)
			return;
		this.callFunction("UI|RX_NO|setParmValue", rxparm);
		OrderList ol=odo.getPrescriptionList().getOrderList("3",
				indexOfPrescription);
		if(ol==null||ol.size()<1)
			return;
		String rborderDeptCode = ol.getOrder(0).getExecDeptCode();
		this.setValue("RBORDER_DEPT_CODE", rborderDeptCode);
		// +1:TParm �±껻���"RX_NO"���±�
		this.setValue("RX_NO", indexOfPrescription + 1);
		Order o=ol.getOrder(0);
		this.setValue("DCT_TAKE_DAYS", o.getTakeDays());
		this.setValue("DCT_TAKE_QTY", o.getDctTakeQty());
		this.setValue("FREQ_CODE", o.getFreqCode());
		this.setValue("ROUTE_CODE", o.getRouteCode());
		this.setValue("DCTAGENT_CODE",o.getDctagentCode());
		this.setValue("DESCRIPTION", o.getDrNote());
		this.setValue("URGENT_FLG",o.getUrgentFlg());
		this.setValue("RELEASE_FLG", o.getReleaseFlg());
		this.setValue("TOTQTY", ol.getTotGram());
	}
	/**
	 * ��ʼ��TABLE
	 * @param rxNo TParm ���±꣬��RX_NO-1
	 */
	public void initTable(int rxNo){
		callFunction("UI|TABLECHNMEDIC|setParmValue",new TParm());
		callFunction("UI|TABLECHNMEDIC|removeRowAll");
		if(odo==null){
			return;
		}
		PrescriptionList pl=odo.getPrescriptionList();
		if(pl==null)
			return;
		OrderList ol=pl.getOrderList("3", rxNo);
//		// System.out.println("6");
		if(ol==null||ol.size()<1)
			return;
		int differ=4-ol.size()%4;
		if(differ==4)differ=0;
//		// System.out.println("7:"+ol.size());
//		// System.out.println("7:"+differ);
		for(int i=0;i<differ;i++){
			Order o=ol.newOrder();
			o.modifyOrderCode("");
			o.modifyOrderDesc("");
			o.modifyMediQty(0.0);
			o.modifyDctexcepCode("");
		}
		TParm parm=ol.getParm(OrderList.PRIMARY);
//		// System.out.println("8:ol.parm"+parm);
		TParm parmtable=new TParm();
		parmtable=OdoUtil.chnMedicReArrange(parm);
		parm=null;
//		// System.out.println("chinese==="+parmtable);
		callFunction("UI|TABLECHNMEDIC|setParmValue",parmtable);

	}

	/**
	 * TABLE����¼�
	 * @param row
	 */
	public void onTableClicked(int row){
		 String message = (String)callFunction("UI|delete|getActionMessage");
		 String s[] = StringTool.getHead(message, "|");
		 if(s!=null&&!callFunction("UI|getTag").equals(s[0])){
			 callFunction("UI|" + s[0] + "|clearTableSelection");
		 }
		 callFunction("UI|delete|setActionMessage", callFunction("UI|getTag")
		 + "|onDelete");
		selectRow=row;
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
		int rxNo=OdoUtil.calculateOrderIndexForChnMed(selectRow,column);
//		this.messageBox_(rxNo);
		OrderList ol = odo.getPrescriptionList().getOrderList("3", i);
		// ȡ��һ��Order
		if(ol==null||ol.size()<1){
			this.messageBox_("����װ��ʧ��");
			return;
		}
		Order o = ol.getOrder(rxNo);
		if(StringUtil.isNullString(o.getOrderCode())){
			return;
		}
		TTable table = (TTable) this
				.callFunction("UI|TABLECHNMEDIC|getThis");
		String columnName = table.getParmMap(column);
		// ����
		if (!StringUtil.isNullString(columnName)&&columnName.contains("TAKE_QTY")) {
//			this.messageBox_("here"+o.getOrderCode());
			o.modifyMediQty(TCM_Transform.getDouble(value));
			parm = OdoUtil.calcuQty(o).getParm();
			return;
		}
		// ����巨
		if (!StringUtil.isNullString(columnName)&&columnName.contains("DCTEXCEP_CODE")) {
			o.modifyDctexcepCode(value);
			return;
		}
	}
	/**
	 * ������comboֵ�ı��¼�
	 */
	public void onRxNoChanged(){
		initTable(TCM_Transform.getInt(this.getValue("RX_NO"))-1);
	}
	/**
	 * TABLE����һ������
	 */
	public void onNew(){
		if (odo == null){
			return;
		}

		int index = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", index);
		if(ol==null|ol.size()<1){
//			this.messageBox_("ol is null");
			return;
		}
		//��ҩ��Ƭһ�����ݾ���4��ҽ��
		TParm parm=new TParm();

		for(int i=0;i<4;i++){
			Order o=ol.newOrder();
			parm.setData("ORDER_CODE" + (i+1), "");
			parm.setData("TAKE_QTY" + (i+1), 0.0);
			parm.setData("DCTEXCEP_CODE" + (i+1), "");
		}


		this.callFunction("UI|TABLECHNMEDIC|addRow",parm);
	}
	/**
	 * TABLEɾ��һ������
	 */
	public void onDelete(){
//		this.messageBox_("in delete");
		if(selectRow==-1)
			return;
		int column=TCM_Transform.getInt(this.callFunction("UI|TABLECHNMEDIC|getSelectedColumn"));
		int rxNo=OdoUtil.calculateOrderIndexForChnMed(selectRow,column);
//		this.messageBox_(rxNo);
		OrderList ol=odo.getPrescriptionList().getOrderList("3", TCM_Transform.getInt(this.getValue("RX_NO"))-1);
		ol.removeData(rxNo);
//		this.messageBox_(ol.size());

		initTable(TCM_Transform.getInt(this.getValue("RX_NO"))-1);

//		for(int i=rxNo;i<rxNo+3;i++){
//			this.callFunction("UI|TABLECHNMEDIC|setValueAt", null,selectRow,column);
//		}
////		this.callFunction("UI|TABLECHNMEDIC|removeRow", selectRow);
//		odo.getPrescriptionList().getOrderList("3", TCM_Transform.getInt(this.getValue("RX_NO"))-1).removeData(rxNo);
	}
	/**
	 * ����һ�Ŵ���ǩ
	 * ���ܣ�1.������ڵ�TABLE�е�����
	 *      2.�½�ͬ�������͵Ĵ���ǩ
	 *      3.TABLE����������
	 */
	public void onNewOrderList(){
		callFunction("UI|TABLECHNMEDIC|setParmValue",new TParm());
		callFunction("UI|TABLECHNMEDIC|removeRowAll");
		OrderList ol=odo.getPrescriptionList().newOrderList("3");
//		for(int i=0;i<4;i++){
//			ol.newOrder();
//		}

		initChnMedic(odo.getPrescriptionList().getGroupPrsptSize("3")-1);
	}
	/**
	 * ɾ��һ�Ŵ���ǩ
	 */
	public void onDeleteOrderList(){
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if (rxNo == -1) {
			return;
		}
		if (!OdoUtil.deleteOrderList(ol)) {
			return;
		}
		callFunction("UI|TABLECHNMEDIC|setParmValue",new TParm());
		callFunction("UI|TABLECHNMEDIC|removeRowAll");
		initChnMedic(0);
	}
	/**
	 * ����ODO����
	 * @param odo
	 */
	public void setOdo(Object odo)
	{

		if(odo==null)
			return;

		this.odo=(ODO)odo;
		initChnMedic(0);
	}
	/**
	 * �շݻ�܇�¼�
	 */
	public void onTakeDays(){
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		if(rxNo<0)
			return;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if(ol==null||ol.size()<1)
			return;
		for(int i=0;i<ol.size();i++){
			Order o=ol.getOrder(i);
			o.modifyTakeDays(this.getValueInt("DCT_TAKE_DAYS"));

		}
	}
	/**
	 * �ƬӋ����܇�¼�
	 */
	public void onDctTakeQty(){
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		if(rxNo<0)
			return;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if(ol==null||ol.size()<1)
			return;
		for(int i=0;i<ol.size();i++){
			Order o=ol.getOrder(i);
			o.modifyDctTakeQty(this.getValueInt("DCT_TAKE_QTY"));
		}
	}
	/**
	 * �l�λ�܇�¼�
	 */
	public void onFreq(){

		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		if(rxNo<0)
			return;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if(ol==null||ol.size()<1)
			return;
		for(int i=0;i<ol.size();i++){
			Order o=ol.getOrder(i);
			o.modifyFreqCode(TCM_Transform.getString(this.getValue("FREQ_CODE")));
		}

	}
	/**
	 * �÷���܇�¼�
	 */
	public void onRoute(){
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		if(rxNo<0)
			return;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if(ol==null||ol.size()<1)
			return;
		for(int i=0;i<ol.size();i++){
			Order o=ol.getOrder(i);
			o.modifyRouteCode(TCM_Transform.getString(this.getValue("ROUTE_CODE")));
		}
	}
	/**
	 * ��ˎ��ʽ��܇�¼�
	 */
	public void onDctAgent(){

		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		if(rxNo<0)
			return;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if(ol==null||ol.size()<1)
			return;
		for(int i=0;i<ol.size();i++){
			Order o=ol.getOrder(i);
			o.modifyDctagentCode(TCM_Transform.getString(this.getValue("DCTAGENT_CODE")));
		}

	}
	/**
	 * ��עʧȥ���c�¼�
	 */
	public void onDesc(){
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		if(rxNo<0)
			return;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if(ol==null||ol.size()<1)
			return;
		for(int i=0;i<ol.size();i++){
			Order o=ol.getOrder(i);
			o.modifyDrNote(TCM_Transform.getString(this.getValue("DESCRIPTION")));
		}
	}
	/**
	 * �����c���¼�
	 */
	public void onUrgent(){
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		if(rxNo<0)
			return;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if(ol==null||ol.size()<1)
			return;
		for(int i=0;i<ol.size();i++){
			Order o=ol.getOrder(i);
			o.modifyUrgentFlg(TCM_Transform.getString(this.getValue("URGENT_FLG")));
		}
	}
	/**
	 * �Ԃ��c���¼�
	 */
	public void onRelease(){

		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		if(rxNo<0)
			return;
		OrderList ol=odo.getPrescriptionList().getOrderList("3", rxNo);
		if(ol==null||ol.size()<1)
			return;
		for(int i=0;i<ol.size();i++){
			Order o=ol.getOrder(i);
			o.modifyReleaseFlg(TCM_Transform.getString(this.getValue("RELEASE_FLG")));
		}

	}
	/**
	 * ��ս���
	 */
	public void onClear(){
//		this.setValue("RBORDER_DEPT_CODE", nullStr);
		this.setValue("RX_NO", nullStr);
		this.setValue("EKT", nullStr);
		this.setValue("TOT_PAY", nullStr);
		this.setValue("DCT_TAKE_DAYS", nullStr);
		this.setValue("DCT_TAKE_QTY", nullStr);
		this.setValue("FREQ_CODE", nullStr);
		this.setValue("ROUTE_CODE", nullStr);
		this.setValue("DCTAGENT_CODE", nullStr);
		this.setValue("DESCRIPTION", nullStr);
		this.setValue("TOTQTY", nullStr);
		this.setValue("URGENT_FLG", false);
		this.setValue("RELEASE_FLG", false);
		this.setValue("TOT_PAY", nullStr);
		callFunction("UI|TABLECHNMEDIC|setParmValue",new TParm());
		callFunction("UI|TABLECHNMEDIC|removeRowAll");
	}
	public Object callMessage(String message,Object parm){
		return 	super.callMessage(message, parm);
	}
}
