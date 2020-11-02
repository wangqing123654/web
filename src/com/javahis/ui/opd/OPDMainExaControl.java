package com.javahis.ui.opd;

import java.awt.Component;

import jdo.opd.ODO;
import jdo.opd.Order;
import jdo.opd.OrderList;
import jdo.opd.PrescriptionList;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TPopupMenu;
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
 * Title: ������Panel
 * </p>
 *
 * <p>
 * Description:������Panel
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
 * @author ehui 20080901
 * @version 1.0
 */
public class OPDMainExaControl extends TControl {
	ODO odo;
	int selectRow=-1;
	int orignalrows=0;
	int linkno=0;
	boolean linkFlg=false;
	int linkOrder=-1;
	String nullStr="";
	double totpay=0.0;
	public void onInit() {
		super.onInit();
		callFunction("UI|TABLEEXA|addEventListener","TABLEEXA->"+TTableEvent.CLICKED,this,"onTableClicked");
		this.addEventListener("TABLEEXA->" + TTableEvent.CHANGE_VALUE,
		"onTableChangeValue");
		//tableר�õļ���
        callFunction("UI|TABLEEXA|addEventListener",TTableEvent.CREATE_EDIT_COMPONENT,this,"onCreateEditComponent");
        TTable table=(TTable)this.callFunction("UI|TABLEEXA|getThis");
//        table.addEventListener(table.getTag()+"->"+TTableEvent.RIGHT_CLICKED,this, "showOrderSet");
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED,this, "onMainFlg");
		initExa(0);
	}
	/**
	 * �һ��¼�
	 * @param row �һ��к�
	 * @param x ���λ��
	 * @param y ���λ��
	 */
	public void showOrderSet(){
		TTable table=(TTable)this.callFunction("UI|TABLEEXA|getThis");
		selectRow=table.getClickedRow();
		int x=table.getMouseX();
		int y=table.getMouseY();
		TPopupMenu popup = new TPopupMenu();
		popup.setTag("POPUPMENU");
		popup.setParentComponent(table);
		popup.setLoadTag("UI");

		popup.init(getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSPOPUPMENU.x"));
		popup.onInit();
		popup.show(table,x,y);
	}
	public void onOrderSetShow(){
////		this.messageBox_(!"Y".equalsIgnoreCase(o.getSetmainFlg()));
////		this.messageBox_(o.getSetmainFlg());
//		if(o==null||StringUtil.isNullString(o.getOrderCode())||!"Y".equalsIgnoreCase(o.getSetmainFlg())){
//			this.messageBox_("��ҽ���Ǽ���ҽ��");
//			return;
//		}
		TParm parm=new TParm();
		 OrderList ol=odo.getPrescriptionList().getOrderList("5", this.getValueInt("RX_NO")-1);
	        if(ol==null){
	        	this.messageBox_("����װ��ʧ��");
	        	return;
	        }
		parm.setData("OL",ol);
		parm.setData("ORDER",ol.getOrder(selectRow));
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x",parm);

	}
	/**
	 * �����ӵ�checkbox�ĵ���¼�
	 * @param o Object
	 */
	public void onMainFlg(Object obj){

		TTable table=(TTable)obj;
		table.acceptText();
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
	        if(column != 0)
	            return;
	        if(!(com instanceof TTextField))
	            return;

	        TTextField textfield = (TTextField)com;
	        textfield.onInit();
			TParm rxType=new TParm();
			rxType.setData("RX_TYPE",5);
	        //��table�ϵ���text����sys_fee��������
	        textfield.setPopupMenuParameter("bbb",getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),rxType);
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
	        this.callFunction("UI|TABLEEXA|acceptText");
	        OrderList ol=odo.getPrescriptionList().getOrderList("5", this.getValueInt("RX_NO")-1);
			if(ol==null||ol.size()<1||ol.size()-1<selectRow)
				return;
	        int olindex=ol.size()-1;
	        Order o= ol.getOrder(olindex);
	        if(!StringUtil.isNullString(o.getOrderCode())){
	        	this.messageBox_("�ѿ���ҽ�����ܱ��������ɾ��");
	        	return;
	        }


//	        this.messageBox_(ol.size()+"ol.size");
	        o=OdoUtil.initExaOrder(odo.getReg(),parm,o,odo.isChild());
	        double own_amt=0.0;
	        double ar_amt=0.0;
	        double own_price=0.0;
	        String setFlg=parm.getValue("ORDERSET_FLG");

			TParm parmtable=new TParm();
	        if(!StringUtil.isNullString(setFlg)&&"Y".equalsIgnoreCase(setFlg)){
//	        	this.messageBox_(o.getOrderDesc());
	        	OrderList oltemp=OdoUtil.addOrder(odo.getReg(), ol, olindex,odo.isChild());
	        	if(oltemp==null)
	        	{
	        		this.messageBox_("��ȡҽ�����ݴ���");
	        		return ;
	        	}

	        	o= ol.getOrder(olindex);
	        	o.modifyDosageQty(1.0);
	        	o.modifyDispenseQty(1.0);
	        	o.modifyMediQty(1.0);
	        	own_amt=o.getOwnAmt();
	        	o.modifyOwnAmt(0.0);
	        	ar_amt=o.getArAmt();
	        	o.modifyArAmt(0.0);
	        	own_price=o.getOwnPrice();
	        	o.modifyOwnPrice(0.0);
	        	o.modifySetmainFlg("Y");
		        parm = o.getParm();
//		        this.messageBox_(o.getOrderDesc());

		        parm.setData("OWN_PRICE", own_price);
		        parm.setData("OWN_AMT",own_amt);
		        parm.setData("AR_AMT",ar_amt);

//		        // System.out.println("my parm=="+parm);
		        parmtable=new TParm();
				parmtable=OdoUtil.calculatePayAmount(parm);
//				// System.out.println("my last parm==="+parmtable);
	        }
//	        // System.out.println("my last parm==="+parmtable);
//	        this.setValue("TOTPAY", ol.getSumFee()+own_amt);
	        this.callFunction("UI|TABLEEXA|setRowParmValue",selectRow, new TParm());
			this.callFunction("UI|TABLEEXA|setRowParmValue",selectRow, parmtable);
//			this.messageBox_(ol.size());
			onNew();
	    }

	/**
	 * ��ʼ������
	 * @param  int indexOfPrescription TParm���±�
	 */
	public void initExa(int indexOfPrescription){
		if(odo== null)
			return;
		if(odo.getPrescriptionList().getOrderList("5",
				indexOfPrescription)==null){
			OrderList ol=odo.getPrescriptionList().newOrderList("5");
			ol.newOrder();
		}else{
			OrderList ol=odo.getPrescriptionList().getOrderList("5", indexOfPrescription);
			if(!StringUtil.isNullString(ol.getOrder(ol.size()-1).getOrderCode())){
				ol.newOrder();
			}
		}
		//��,30,boolean;��,30,boolean;���,60;ҽ��,100;����,100;��ҩ��λ,80;Ƶ��,80;;��,80;�շ�,100;����,100;����,80;
		//��ҩ��λ,80;��,30,boolean;�ܼ�,200;�ۿ۽��,200;Ӧ�����,200;
		//ҽ����ע,200;��ʿ��ע,200;ִ�п���,80;����,30,boolean;�������,80;�շ�ʱ��,200;��ҩʱ��,200;ҽ��ȷ��,200
		TParm rxparm = OdoUtil.prepareRxNoTParm(odo, "5");
		if (rxparm == null){
			callFunction("UI|RX_NO|setParmValue", new TParm());
			setValue("RX_NO", "");
			return;
		}

		callFunction("UI|RX_NO|setParmValue", rxparm);
//			setValue("RBORDER_DEPT_CODE", rborderDeptCode);
			setValue("RX_NO", odo.getPrescriptionList().getOrderList("5",
					indexOfPrescription).getPresrtNo());

//		setValue("RBORDER_DEPT_CODE", rborderDeptCode);
		// +1:TParm �±껻���"RX_NO"���±�

//		initTable(indexOfPrescription);
	}

	/**
	 * ��ʼ��TABLE
	 * @param rxNo TParm ���±꣬��RX_NO-1
	 */
	public void initTable(int rxNo){
		callFunction("UI|TABLEEXA|setParmValue",new TParm());
		callFunction("UI|TABLEEXA|removeRowAll");
		if (rxNo < 0 || rxNo >= odo.getPrescriptionList().getGroupPrsptSize("5"))
            return;
		PrescriptionList pl=odo.getPrescriptionList();
		if(pl==null)
			return;
		OrderList ol=pl.getOrderList("5", rxNo);
		if(ol==null||ol.size()<1)
			return;
		this.setValue("TOTPAY", ol.getSumFee());
		TParm parm=odo.getPrescriptionList().getOrderList("5", rxNo).getTParmForTable();
		TParm oldpamr=odo.getPrescriptionList().getOrderList("5", rxNo).getParm(ol.PRIMARY);
//		// System.out.println("old parm"+oldpamr);
//		// System.out.println("exa parm"+parm);
		if(parm.getCount() <= 0)
			return;
		if (parm.getData("PAYAMOUNT",0) == null) {
			// ����Ӧ�������ܽ��-�ۿ۽��
			OdoUtil.calculatePayAmount(parm);

		}
//		// System.out.println("after method"+parm);


		callFunction("UI|TABLEEXA|setParmValue", parm);

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
	 * @param tableNode
	 */
	public void onTableChangeValue(Object tableNode){
		TTableNode	mytableNode=(TTableNode)tableNode;
		//��CELL��ֵ
		String value=TCM_Transform.getString(mytableNode.getValue());
		//TABLE�ϵڼ���
		int c=mytableNode.getRow();
		//�ڼ���
		int column=mytableNode.getColumn();
		int seq_no=TCM_Transform.getInt(this.getValue("RX_NO"))-1;
		TTable table = (TTable) this.callFunction("UI|TABLEORDER|getThis");
		String columnName = table.getParmMap(column);
		Order o=odo.getPrescriptionList().getOrderList("5", seq_no).getOrder(c);
		if(StringUtil.isNullString(o.getOrderCode())){
			return;
		}
		/* ҽ��,100;
		 * ����,100;����,80,double;��λ,80;����,100,double;
		 * �ܼ�,200,double;�ۿ۽��,200,double;Ӧ�����,200,double;
		 * ҽ����ע,200;��ʿ��ע,200;ִ�п���,80;����,40,boolean;
		 * �������,80;�շ�ʱ��,200;ִ��ʱ��,200;ҽ��ȷ��,200
		 */

		if("OPTITEM_CODE".equalsIgnoreCase(columnName)){
			o.modifyOptitemCode(value);
			return;
		}
		if("DR_NOTE".equalsIgnoreCase(columnName)){
			o.modifyDrNote(value);
			return;
		}
		if("URGENT_FLG".equalsIgnoreCase(columnName)){
			o.modifyUrgentFlg(value);
			return;
		}
	}
	/**
	 * ������comboֵ�ı��¼�
	 */
	public void onRxNoChanged(){
		if(this.getValue("RX_NO")!=null&&!"".equals(((String)this.getValue("RX_NO")))){
			initTable(TCM_Transform.getInt(this.getValue("RX_NO")) - 1);
		}
	}
	/**
	 * TABLE����һ������
	 */
	public void onNew(){
		if (odo == null)
			return;
		int index = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		OrderList ol=odo.getPrescriptionList().getOrderList("5", index);
		Order o = ol.newOrder();

//		if(linkFlg){
//			Order orderMain=ol.getOrder(linkOrder);
//			OdoUtil.linkOrder(o, orderMain,linkno);
//		}
		TParm parm = o.getParm();
		TParm parmtable=OdoUtil.calculatePayAmount(parm);
		this.callFunction("UI|TABLEEXA|addRow", parmtable);
	}

	/**
	 * TABLEɾ��һ������
	 */
	public void onDelete(){
		callFunction("UI|TABLEEXA|removeRow", selectRow);
		if (selectRow == -1)
			return;
		OrderList ol = odo.getPrescriptionList().getOrderList("5",
				TCM_Transform.getInt(this.getValue("RX_NO")) - 1);

		Order o = ol.getOrder(selectRow);
		if (!OdoUtil.isChargedOrder(o)) {
			OdoUtil.deleteOrderSet(ol,o);
			initExa(TCM_Transform.getInt(this.getValue("RX_NO")) - 1);
//			callFunction("UI|TABLEEXA|setParmValue",new TParm());
//			callFunction("UI|TABLEEXA|removeRowAll");
//			TParm parmtable=OdoUtil.calculatePayAmount(ol.getTableParm());
//			callFunction("UI|TABLEEXA|setParmValue",parmtable);
//			onNew();
		}

	}
	/**
	 * ����һ�Ŵ���ǩ
	 * ���ܣ�1.������ڵ�TABLE�е�����
	 *      2.�½�ͬ�������͵Ĵ���ǩ
	 *      3.TABLE����������
	 */
	public void onNewOrderList(){
		callFunction("UI|TABLEEXA|setParmValue",new TParm());
		callFunction("UI|TABLEEXA|removeRowAll");
		odo.getPrescriptionList().newOrderList("5");
		initExa(odo.getPrescriptionList().getGroupPrsptSize("5") - 1);

	}
	/**
	 * ɾ��һ�Ŵ���ǩ
	 */
	public void onDeleteOrderList(){
		int rxNo = TCM_Transform.getInt(this.getValue("RX_NO")) - 1;
		OrderList ol=odo.getPrescriptionList().getOrderList("5", rxNo);
		if (rxNo == -1) {
			return;
		}
		if (!OdoUtil.deleteOrderList(ol)) {
			return;
		}
		callFunction("UI|TABLEEXA|setParmValue",new TParm());
		callFunction("UI|TABLEEXA|removeRowAll");
		initExa(rxNo-1);
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
		initExa(0);
	}
	/**
	 * ��ս���
	 */
	public void onClear(){
		this.setValue("RX_NO", nullStr);
		this.setValue("EKT", nullStr);
		this.setValue("TOT_PAY", nullStr);
		this.setValue("SHOW_GOODS", false);
		callFunction("UI|TABLEEXA|setParmValue",new TParm());
		callFunction("UI|TABLEEXA|removeRowAll");
	}
	public Object callMessage(String message,Object parm){
		return 	super.callMessage(message, parm);
	}

}
