package com.javahis.ui.ibs;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.reg.SysParmTool;
import jdo.sys.SYSRolePopedomTool;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import com.dongyang.control.TControl;
import com.javahis.system.textFormat.TextFormatCLPDuration;
import com.javahis.ui.testOpb.bean.OpdOrder;
import com.javahis.ui.testOpb.tools.TableTool;
import com.javahis.ui.testOpb.tools.Type;
import com.javahis.util.OdiUtil;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.ui.TTable;

import jdo.ibs.IBSNewTool;
import jdo.ibs.IBSOrderD;
import jdo.ibs.IBSTool;
import jdo.mem.MEMTool;

import com.dongyang.ui.base.TTableCellEditor;
import com.dongyang.ui.event.TTableEvent;

import java.awt.Color;
import java.awt.Component;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import java.util.Vector;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TLabel;
import com.dongyang.jdo.TDataStore;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import jdo.sys.SYSChargeHospCodeTool;
import jdo.sys.SYSFeeTool;

import com.javahis.util.StringUtil;
import jdo.sys.SYSOrderSetDetailTool;
import com.dongyang.util.TypeTool;
import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.bil.BIL;

import com.dongyang.ui.TWindow;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.table.TableCellEditor;

/**
 * 
 * <p>
 * Title: סԺ�Ƽۿ�����
 * </p>
 * 
 * <p>
 * Description: סԺ�Ƽۿ�����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangl
 * @version 1.0
 */
public class IBSOrderControl extends TControl {
	/**
	 * �����
	 */
	private String caseNo;
	/**
	 * סԺ��
	 */
	private String ipdNo;
	/**
	 * ������
	 */
	private String mrNo;
	/**
	 * ����
	 */
	private String bedNo;
	/**
	 * ϵͳ��
	 */
	String sysType;
	/**
	 * ҽ�� add caoyong 20131111
	 */
	private String orderDesc;
	private String inDate;
	public String getOrderDesc() {
		return orderDesc;
	}

	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}

	/**
	 * ����ȼ�
	 */
	String serviceLevel;
	/**
	 * ����ҽ�����
	 */
	private int groupNo;
	private TTable t;
	private IBSOrderD order;
//	private static final String actionName = "action.ibs.IBSAction";
	private String orderNo; // ҽ�����
	private String clncpathCode;// �ٴ�·������
	private String schdCode;// ʱ�̴���
	TParm operationParm; // �����һش�����
	/**
	 * TABLE
	 */
	private static String TABLE = "MAINTABLE";
	//add by huangtt start 20130922
	/**
	 * ҽ������
	 */
	private  String OrderCode;
	
	private double ownRate;    //�Ը�����
	private TParm colorParm;//=====pangben 2015-11-2 ����ҽ����ʾ��ɫ����
	
	/**
	 * ���һ 
	 */
	String ctz1Code;
	/**
	 * ��ݶ�
	 */
	String ctz2Code;
	/**
	 * �����
	 */
	String ctz3Code;
	//add by huangtt end 20130922
	String lumpworkCode="";
	double lumpworkRate=0.00;
	String caseNoNew="";//�ײ�ʹ�ã���ѯĸ�׾����
	/**
	 * ����ǰTOOLBAR
	 */
	public void onShowWindowsFunction() {
		// ��ʾUIshowTopMenu
		callFunction("UI|showTopMenu");
	}

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		// ��ʼ���������
		this.initPage();
		// tableר�õļ���
		getTTable("MAINTABLE").addEventListener(
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponent");
		// ����datastore��ֵ�ı�
		order.addEventListener(order.ACTION_SET_ITEM, this, "onSetItemEvent");
		// ģ����ѯ -------start---------
		OrderList orderDesc = new OrderList();
		TTable table = (TTable) this.getComponent("MAINTABLE");
		table.addItem("ORDER_LIST", orderDesc);
		callFunction("UI|MAINTABLE|addEventListener", "MAINTABLE" + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// ��ʱTABLEֵ�ı����
		addEventListener(TABLE + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValue");
		colorParm=new TParm();
		// ����ٴ�·������ʱ��======pangben 2012-7-9		
		TextFormatCLPDuration combo_schd1 = (TextFormatCLPDuration) this
				.getComponent("SCHD_CODE");
				combo_schd1.setClncpathCode(clncpathCode);
		        combo_schd1.onQuery();
		        
		TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this.getComponent("SCHD_CODE1");
		combo_schd.setClncpathCode(clncpathCode);
        combo_schd.onQuery();
        table.addItem("SCHD_CODE", combo_schd);
	}

	/**
	 * ����table�����¼�
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		TTable table = (TTable) this.getComponent("MAINTABLE");
		table.acceptText();

		TParm parm = order.getRowParm(row);
		if (parm.getValue("ORDER_CODE").length() == 0)
			return;
		String orderCode = parm.getValue("ORDER_CODE");
		String sysFeeSql = " SELECT ORDER_CODE,ORDER_DESC,GOODS_DESC,DESCRIPTION,SPECIFICATION "
				+ "   FROM SYS_FEE "
				+ "  WHERE ORDER_CODE = '"
				+ orderCode
				+ "' ";
		parm = new TParm(TJDODBTool.getInstance().select(sysFeeSql));
		parm = parm.getRow(0);
		// ״̬��
		callFunction("UI|setSysStatus", parm.getValue("ORDER_CODE")
				+ parm.getValue("ORDER_DESC") + parm.getValue("GOODS_DESC")
				+ parm.getValue("DESCRIPTION") + parm.getValue("SPECIFICATION"));
	}
	
	/**
	 * ֵ���¼�����
	 * 
	 * @param obj
	 *            Object
	 * @throws java.text.ParseException 
	 */
	public boolean onChangeTableValue(Object obj) throws java.text.ParseException {
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// �õ�table�ϵ�parmmap������
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		// �жϵ�ǰ���Ƿ���ҽ��
		int selRow = node.getRow();
		TParm orderP = this.getTTable(TABLE).getDataStore().getRowParm(selRow);
//		System.out.println("====++++====orderP orderP orderP is :"+orderP);
		String orderSetGroupNo = orderP.getValue("ORDERSET_GROUP_NO");
		String orderSetCode = orderP.getValue("ORDERSET_CODE");
		String indvFlg = orderP.getValue("INDV_FLG");
		
		if (orderP.getValue("ORDER_CODE").length() == 0) {
			// ���ҽ������
			this.getTTable(TABLE).setDSValue(selRow);
		}
		if ("EXEC_DATE".equals(columnName)) {
			// this.messageBox("00000000000000000000000000000000000000000");
			String execDate = node.getValue().toString().substring(0, 10);
			// ȡ��ǰʱ��
			String nowDate = SystemTool.getInstance().getDate().toString()
					.substring(0, 10);
			// ��ѯ�˲����ĵ���Ժʱ��
			String inSql = "SELECT IN_DATE FROM ADM_INP WHERE CASE_NO = '"
					+ caseNo + "' ";
			TParm inParm = new TParm(TJDODBTool.getInstance().select(inSql));
			String inDate = inParm.getValue("IN_DATE", 0).substring(0, 10);
			if (execDate.compareTo(inDate) < 0) {
				this.messageBox("ִ��ʱ�䲻��С����Ժʱ��");
				return true;
			} else if (execDate.compareTo(nowDate) > 0) {
				this.messageBox("ִ��ʱ�䲻�ɴ��ڵ�ǰʱ��");
				return true;
			}
			// ====��Ϊ����ҽ���޸�order��������Ӧ��ϸ���ִ��ʱ��
			if (orderSetCode.length() > 0 && indvFlg.equals("N")) {
				order.setFilter("");
				order.filter();
				int orderRow = order.rowCount();
				for (int i = 0; i < orderRow; i++) {
					String orderSetGroupNo1 = order.getItemString(i,
							"ORDERSET_GROUP_NO");
					String orderSetCode1 = order.getItemString(i,
							"ORDERSET_CODE");
					if (orderSetGroupNo.equals(orderSetGroupNo1)
							&& orderSetCode.equals(orderSetCode1)) {
						// this.messageBox("9999");
						order.setItem(i, "EXEC_DATE", this
								.onExecDateChange1(execDate));
					}

				}

				order.setFilter("ORDERSET_CODE ='' OR (ORDERSET_CODE !='' AND INDV_FLG='N')");
				order.filter();
			}
		}
		// �������ۿ�--xiongwg20150401 start
		if ("DOSAGE_QTY".equals(columnName) || "MEDI_QTY".equals(columnName)
				|| "TAKE_DAYS".equals(columnName)) {
			if (Double.parseDouble(node.getValue().toString().toString())<0) {//pangben 2015-8-20 ��ӿ����޸�ҽ������У�飬������Ϊ����
				if ("MEDI_QTY".equals(columnName)) {
					this.messageBox("����������Ϊ����");
				}
				else if ("TAKE_DAYS".equals(columnName)) {
					this.messageBox("����������Ϊ����");
				}else if ("DOSAGE_QTY".equals(columnName)) {
					this.messageBox("����������Ϊ����");
				}
				return true;
			}
			if ("MEDI_QTY".equals(columnName)) {
				order.setItem(selRow, "MEDI_QTY", node.getValue());
			}
			if ("TAKE_DAYS".equals(columnName)) {
				order.setItem(selRow, "TAKE_DAYS", node.getValue());
			}
			if ("DOSAGE_QTY".equals(columnName)) {
				order.setItem(selRow, "DOSAGE_QTY", node.getValue());
			}
		}
		// �������ۿ�--xiongwg20150401 end
		return false;
	}
	public void onExecDateChange() throws java.text.ParseException{
		
		if(this.getValue("EXEC_DATE")==null||
				this.getValue("EXEC_DATE").toString().length()==0){
			this.messageBox("ִ��ʱ�䲻��Ϊ��");
			this.setValue("EXEC_DATE", SystemTool.getInstance().getDate());
			return ;
			
		}else{
			String execDate = this.getValue("EXEC_DATE").toString().substring(0, 10);
//			System.out.println("====+++====execDate execDate is ::"+execDate);
//			System.out.println("------------this.getValueStringis ::"+this.getValue("EXEC_DATE"));
			if(this.getValueString("EXEC_DATE").length()==0||
					this.getValue("EXEC_DATE")==null){
				order.setExecDate(SystemTool.getInstance().getDate());
//				order.setExecDate(Timestamp.valueOf(this.getValueString("EXEC_DATE")));
			}else{
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				  format.setLenient(false);
				  //Ҫת���ַ��� str_test
//				  String str_test ="2011-04-24"; 
				  try {
				   Timestamp ts = new Timestamp(format.parse(execDate).getTime());
				   order.setExecDate(ts);
				   System.out.println(ts.toString());
				  } catch (ParseException e) {
				   // TODO Auto-generated catch block
				   e.printStackTrace();
				  }
//				order.setExecDate(SystemTool.getInstance().getDate());
//				order.setExecDate(Timestamp.valueOf(execDate));
			}
			
			//ȡ��ǰʱ��
			String nowDate = SystemTool.getInstance().getDate().toString().substring(0, 10);
			//��ѯ�˲����ĵ���Ժʱ��
			String inSql = "SELECT IN_DATE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"' ";
			TParm inParm = new TParm(TJDODBTool.getInstance().select(inSql));
			String inDate = inParm.getValue("IN_DATE", 0).substring(0, 10);
			if(execDate.compareTo(inDate)<0){
				this.messageBox("ִ��ʱ�䲻��С����Ժʱ��");
				this.setValue("EXEC_DATE", SystemTool.getInstance().getDate());
				return ;
			}else if(execDate.compareTo(nowDate)>0){
				this.setValue("EXEC_DATE", SystemTool.getInstance().getDate());
				this.messageBox("ִ��ʱ�䲻�ɴ��ڵ�ǰʱ��");
				return ;
			}
		}
		
	}
          public Timestamp onExecDateChange1(String execDate) throws java.text.ParseException{
		
//			String execDate = this.getValue("EXEC_DATE").toString().substring(0, 10);
//			System.out.println("====+++====execDate execDate is ::"+execDate);
//			System.out.println("------------this.getValueStringis ::"+this.getValue("EXEC_DATE"));
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				  format.setLenient(false);
				  //Ҫת���ַ��� str_test
//				  String str_test ="2011-04-24"; 
				  try {
				   Timestamp ts = new Timestamp(format.parse(execDate).getTime());
				   order.setExecDate(ts);
				   System.out.println(ts.toString());
				  } catch (ParseException e) {
				   // TODO Auto-generated catch block
				   e.printStackTrace();
				  }
//				order.setExecDate(SystemTool.getInstance().getDate());
//				order.setExecDate(Timestamp.valueOf(execDate));
				  return new Timestamp(format.parse(execDate).getTime());
			}
	

	/**
	 * datastore��ֵ�ı��¼�
	 * 
	 * @param columnName
	 *            String
	 * @param value
	 *            Object
	 */
	public void onSetItemEvent(String columnName, Object value) {
		if (columnName.equals("TOT_AMT")) {
			int countTable = t.getRowCount();
			double payAmt = 0.00;
			for (int i = 0; i < countTable; i++) {
				//modify by wanglong 20120814 �޸��ܼ۵ļ��㷽ʽ���ܼ۵���ÿ��ҽ���ĵ��۱���С������λ�����������������������������Ľ��壩
				//payAmt = payAmt + t.getItemDouble(i, "TOT_AMT");
				payAmt = payAmt + StringTool.round(t.getItemDouble(i, "TOT_AMT"), 2);
			}
			setValue("OWN_AMT", payAmt);

		}
		if (columnName.equals("EXE_DEPT_CODE")) {
			int countTable = t.getRowCount();
			for (int i = 0; i < countTable; i++) {
				// �ɱ�����
				t.setItem(i, "COST_CENTER_CODE", t.getItemData(i,
						"EXE_DEPT_CODE"));
				t
						.setItem(i, "EXE_DEPT_CODE", t.getItemData(i,
								"EXE_DEPT_CODE"));
			}
		}

	}

	/**
	 * sysFeeģ����ѯ
	 */
	public class OrderList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");

		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER
					: dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("ORDER_CODE");
			Vector d = (Vector) parm.getData("ORDER_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
	}

	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * sysFee��������
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		// ����sysfee�Ի������
		if (column != 0)
			return;
		//caowl 20130204  start=============	
		int selRow = this.getTTable("MAINTABLE").getSelectedRow();
		TParm existParm = this.getTTable("MAINTABLE").getDataStore().getRowParm(
				selRow);
		
		if (this.isOrderSet(existParm)) {
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			return;
		}
		//caowl  20130204 end ===============
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", this.getValueString("CAT1_TYPE"));
		//add by lx 2014/03/17   ��ʿר��
		parm.setData("USE_CAT", "2");
		
		// System.out.println("ҽ������" + getValueString("CAT1_TYPE"));
		// if (!"".equals(Operator.getRegion()))
		// parm.setData("REGION_CODE", Operator.getRegion());
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("SYS_FEE", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newOrder");
	}
	
	
	/**
	 * �Ƿ��Ǽ���ҽ��
	 * 
	 * @param row
	 *            int
	 * @param buff   caowl 20130204
	 *            String
	 * @return boolean
	 */
	public boolean isOrderSet(TParm orderParm) {
		boolean falg = false;		
		if (!orderParm.getValue("ORDERSET_CODE").equals("") && orderParm.getValue("ORDERSET_CODE")!= null ) {
			falg = true;
		}		
		return falg;
	}

	/**
	 * �ش�ҽ������
	 * 
	 * @param parm
	 * @param ownPriceSingle
	 *            ===============pangben 2012-7-9
	 */
	private void newOrderTemp(TParm parm, int selectRow) {
	
		double ownPriceSingle = 0.00;
		if ("2".equals(serviceLevel)) {
			ownPriceSingle = parm.getDouble("OWN_PRICE2");
		} else if ("3".equals(serviceLevel)) {
			ownPriceSingle = parm.getDouble("OWN_PRICE3");
		} else
			ownPriceSingle = parm.getDouble("OWN_PRICE");
		OrderCode = parm.getValue("ORDER_CODE");  //add by huangtt 20130922
		if(null!=lumpworkCode&&lumpworkCode.length()>0){//�ײͻ��߼ƷѸ�����Ժ�ǼǼ�����ۿ۲���
			if(lumpworkRate<=0){
				 this.messageBox("�ײͲ����ۿ۴�������,�����Բ���");
				 return;
			}
			TParm sysFeeParm=SYSFeeTool.getInstance().getFeeAllData(parm.getValue("ORDER_CODE"));
			if(sysFeeParm.getCount()<=0){
				 this.messageBox("δ�ҵ���ǰ����ҽ��");
				 return;
			}
			//ҩƷ��Ѫ�Ѹ�������ۿۼ���(�ײ���)
			if(null!=sysFeeParm.getValue("CAT1_TYPE",0) &&sysFeeParm.getValue("CAT1_TYPE",0).equals("PHA")||
					null!=sysFeeParm.getValue("CHARGE_HOSP_CODE",0) && sysFeeParm.getValue("CHARGE_HOSP_CODE",0).equals("RA")){
				 ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,OrderCode,serviceLevel);
			}else{
				 ownRate=lumpworkRate;//�ײͻ��߸����ײ��ۿۼ���(�ײ���)
				 ownPriceSingle=IBSTool.getInstance().getLumpOrderOwnPrice(caseNoNew, lumpworkCode, OrderCode, serviceLevel);
			}
		 }else{
			 ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,OrderCode,serviceLevel);  //add by huangtt 20130922
		 }
		order.setOwnRate(ownRate); //add by huangtt 20131114
		t = (TTable) this.getComponent("MAINTABLE");
		t.acceptText();
		String cat1Type = parm.getValue("CAT1_TYPE");
		// ״̬��
		callFunction("UI|setSysStatus", parm.getValue("ORDER_CODE")
				+ parm.getValue("ORDER_DESC") + parm.getValue("GOODS_DESC")
				+ parm.getValue("DESCRIPTION") + parm.getValue("SPECIFICATION"));
		int selRow = selectRow;
//		String execDept = parm.getValue("EXEC_DEPT_CODE");
//		if (StringUtil.isNullString(execDept)) {
//			execDept = getValue("EXE_DEPT_CODE").toString();//===========modify by  20120810 caowl
//		}
		//ʹ��ִ����Ա����
		String execDept = getValue("EXE_DEPT_CODE").toString();
		//======pangben 2015-7-16 ����ײ���ҽ����ʾ
		if (IBSTool.getInstance().getLumpworkOrderStatus(this.caseNo, parm.getValue("ORDER_CODE"))) {
			//this.messageBox(parm.getValue("ORDER_DESC")+"ҽ��Ϊ�ײ���ҽ��");
			//t.setRowColor(selRow, Color.YELLOW);//===pangben 2015-11-2 �����ɫ
			colorParm.addData("ORDER_DESC",parm.getValue("ORDER_DESC"));
		}
		String orderNo=SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO","ORDER_NO");
		//		System.out.println("====selRow selRow is ::"+selRow);
		t.setItem(selRow, "DS_FLG", "N");
		// �ж��Ƿ��Ǽ���ҽ��
		if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
			t.setItem(selRow, "SCHD_CODE", order.getSchdCode());
			// �Է�ע��
			t.setItem(selRow, "OWN_FLG", "Y");
			// �շ�ע��
			t.setItem(selRow, "BILL_FLG", "Y");
			// �Ը�����
			//t.setItem(selRow, "OWN_RATE", 1);
			t.setItem(selRow, "OWN_RATE", ownRate);  //modify by huangtt 20130922
			// Ƶ��
			t.setItem(selRow, "FREQ_CODE", "STAT");
			// ҽ������
			t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
			// ҽ������
			t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
			// Ժ�ڷ��ô���
			t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
			// ҽ�����
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// ҽ������
			t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
			// ҽ��ϸ����
			t.setItem(selRow, "ORDER_CAT1_CODE", parm
					.getValue("ORDER_CAT1_CODE"));
			// ����ҽ�����
			t.setItem(selRow, "ORDERSET_GROUP_NO", parm
					.getInt("ORDERSET_GROUP_NO"));
			t.setItem(selRow,"ORDER_NO",orderNo);
			// ��ѯ�վݷ��ô���
			TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
					parm);
			// �վݷ��ô���
			t.setItem(selRow, "REXP_CODE", hexpParm.getValue("IPD_CHARGE_CODE",
					0));
			String orderCode = parm.getValue("ORDER_CODE");
			t.setItem(selRow, "ORDERSET_CODE", orderCode);
			t.setItem(selRow, "INDV_FLG", "N");
			t.setItem(selRow, "MEDI_QTY", 1);
			t.setItem(selRow, "TAKE_DAYS", 1);
			t.setItem(selRow, "DOSAGE_QTY", 1);
			t.setItem(selRow, "INCLUDE_FLG", "N");
			t.setItem(selRow, "INCLUDE_EXEC_FLG", "N");
			t.setItem(selRow, "SCHD_CODE", order.getSchdCode());
			order.setActive(selRow, true);
			TParm parmDetail = null;
			if(null!=lumpworkCode&&lumpworkCode.length()>0){//�ײͻ��߼ƷѸ�����Ժ�ǼǼ�����ۿ۲���
				//��ѯ�ײ����Ƿ���ڴ˼���ҽ��
				parmDetail =MEMTool.getInstance().getLumpWorkOrderSetParm(caseNo, lumpworkCode, parm.getValue("ORDER_CODE"));
				if(parmDetail.getErrCode()<0||parmDetail.getCount()<=0){//�ײ��в����ڴ�ҽ��
					parmDetail = SYSOrderSetDetailTool.getInstance().selectByOrderSetCode(parm.getValue("ORDER_CODE"));
				}
			}else{
				parmDetail = SYSOrderSetDetailTool.getInstance().selectByOrderSetCode(parm.getValue("ORDER_CODE"));
			}
			// System.out.println("ϸ������"+parmDetail);
			if (parmDetail.getErrCode() != 0) {
				this.messageBox("ȡ��ϸ�����ݴ���");
				return;
			}
			// �ɱ�����
			t.setItem(selRow, "COST_CENTER_CODE", execDept);
			// ����ҽ�����
			TParm groupNoParm = this.seleMaxOrdersetGroupNo(caseNo);
			groupNo = groupNoParm.getInt("ORDERSET_GROUP_NO", 0);
			// if (groupNo == 0 || groupNoParm.getCount("ORDERSET_GROUP_NO") ==
			// 0)
			// groupNo = 1;
			// else
			// groupNo++;
			// this.messageBox_(groupNo);
			t.setItem(selRow, "ORDERSET_GROUP_NO", groupNo);
			double allOwnAmt = 0.00;
			for (int i = 0; i < parmDetail.getCount(); i++) {
				int row = order.insertRow();
				// �Է�ע��
				order.setItem(row, "OWN_FLG", "Y");
				// �շ�ע��
				order.setItem(row, "BILL_FLG", "Y");
				// �Ը�����
				//order.setItem(row, "OWN_RATE", 1);
				order.setItem(row, "OWN_RATE", ownRate);  //modify by huangtt 20130922
				// ҽ������
				order.setItem(row, "ORDER_DESC", parmDetail.getValue(
						"ORDER_CODE", i));
				// Ƶ��
				order.setItem(row, "FREQ_CODE", "STAT");
				// ��ҩ��λ
				order.setItem(row, "MEDI_UNIT", parmDetail
						.getValue("UNIT_CODE"));
				// ��ҩ��λ
				order.setItem(row, "DOSAGE_UNIT", parmDetail
						.getValue("UNIT_CODE"));
				// �Էѵ���
				if ("2".equals(serviceLevel)) {
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE2"));
				} else if ("3".equals(serviceLevel)) {
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE3"));
				} else
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE"));

				// ҽ������
				order.setItem(row, "NHI_PRICE", parmDetail
						.getDouble("NHI_PRICE"));
				// Ժ�ڷ��ô���
				order.setItem(row, "HEXP_CODE", parmDetail.getValue(
						"CHARGE_HOSP_CODE", i));
				// ����ҽ�����
				order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
				order.setItem(row, "INCLUDE_FLG", "N");
				order.setItem(row, "INCLUDE_EXEC_FLG", "N");
				// //��ѯ�վݷ��ô���
				// hexpParm =
				// SYSChargeHospCodeTool.getInstance().selectalldata(parmDetail);
				// �վݷ��ô���
				order.setItem(row, "REXP_CODE", hexpParm.getValue(
						"IPD_CHARGE_CODE", 0));

				order.setItem(row, "OPTITEM_CODE", parmDetail.getValue(
						"OPTITEM_CODE", i));
				order.setItem(row, "EXE_DEPT_CODE", execDept);
				order.setItem(row, "INSPAY_TYPE", parmDetail.getValue(
						"INSPAY_TYPE", i));
				order.setItem(row, "RPTTYPE_CODE", parmDetail.getValue(
						"RPTTYPE_CODE", i));
				order.setItem(row, "DEGREE_CODE", parmDetail.getValue(
						"DEGREE_CODE", i));
				order.setItem(row, "CHARGE_HOSP_CODE", parmDetail.getValue(
						"CHARGE_HOSP_CODE", i));
				// ҽ�����
				order.setItem(row, "CAT1_TYPE", parmDetail.getValue(
						"CAT1_TYPE", i));
				// ҽ��ϸ����
				order.setItem(row, "ORDER_CAT1_CODE", parmDetail.getValue(
						"ORDER_CAT1_CODE", i));
				// ҽ������
				order.setItem(row, "ORDER_CHN_DESC", parmDetail.getValue(
						"ORDER_DESC", i));
				// �ɱ�����
				order.setItem(row, "COST_CENTER_CODE", execDept);
				order.setItem(row, "INDV_FLG", "Y");
				order.setItem(row, "ORDERSET_CODE", orderCode);
				order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
				double ownPrice = 0.00;
				if ("2".equals(serviceLevel)) {
					ownPrice = parmDetail.getDouble("OWN_PRICE2", i);
				} else if ("3".equals(serviceLevel)) {
					ownPrice = parmDetail.getDouble("OWN_PRICE3", i);
				} else
					ownPrice = parmDetail.getDouble("OWN_PRICE", i);
				order.setItem(row, "OWN_PRICE", ownPrice);
				
				// double qty = parmDetail.getDouble("DOSAGE_QTY", i);
				order.setItem(row, "MEDI_QTY", parmDetail
						.getDouble("TOTQTY", i));
				order.setItem(row, "DISPENSE_QTY", parmDetail.getDouble(
						"TOTQTY", i));
				order.setItem(row, "TAKE_DAYS", 1);
				order.setItem(row, "DOSAGE_QTY", parmDetail.getDouble("TOTQTY",
						i));
				order.setItem(row, "MEDI_UNIT", parmDetail.getValue(
						"UNIT_CODE", i));
				order.setItem(row, "DOSAGE_UNIT", parmDetail.getValue(
						"UNIT_CODE", i));
				order.setItem(row, "DISPENSE_UNIT", parmDetail.getValue(
						"UNIT_CODE", i));
				order.setItem(row,"SCHD_CODE", order.getSchdCode());
				order.setItem(selRow,"ORDER_NO",orderNo);
				order.setActive(row, true);
				allOwnAmt = allOwnAmt + ownPrice
						* parmDetail.getDouble("TOTQTY", i);
				if(this.getValueString("EXEC_DATE").length()==0||
						this.getValue("EXEC_DATE")==null){
					order.setItem(row, "EXEC_DATE", SystemTool.getInstance().getDate());
				}else{
					order.setItem(row, "EXEC_DATE",  this.getValue("EXEC_DATE"));
				}


			}
			// �Էѵ���
			t.setItem(selRow, "OWN_PRICE", allOwnAmt);
//			t.setItem(selRow, "TOT_AMT", allOwnAmt);
			t.setItem(selRow, "TOT_AMT", allOwnAmt*ownRate);  //modify by huangtt 20130922
			if(this.getValueString("EXEC_DATE").length()==0||
					this.getValue("EXEC_DATE")==null){
				t.setItem(selRow, "EXEC_DATE", SystemTool.getInstance().getDate());
			}else{
				t.setItem(selRow, "EXEC_DATE", this.getValue("EXEC_DATE"));
			}
			order
					.setFilter("ORDERSET_CODE ='' OR (ORDERSET_CODE !='' AND INDV_FLG='N')");
			order.filter();

			// order.insertRow();
			t.setDSValue();
			onInsert(selRow);
		} else {
			// ��ѯ�վݷ��ô���
			TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
					parm);
			if (null==hexpParm||null==hexpParm.getValue("IPD_CHARGE_CODE",0)||hexpParm.getValue("IPD_CHARGE_CODE",0).length()<=0) {
				this.messageBox("����շ���ϸ��������");
				return;
			}
			t.setItem(selRow, "SCHD_CODE", order.getSchdCode());
			// �Է�ע��
			t.setItem(selRow, "OWN_FLG", "Y");
			// double ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,
			// parm.getValue("ORDER_CODE"),
			// serviceLevel);
			// if (ownRate < 0) {
			// return result.newErrParm( -1, "�Ը���������");
			// }
			// �Ը�����
			//t.setItem(selRow, "OWN_RATE", 1);
			t.setItem(selRow, "INCLUDE_FLG", "N");
			t.setItem(selRow, "INCLUDE_EXEC_FLG", "N");
			t.setItem(selRow, "OWN_RATE", ownRate);//modify by huangtt 20130922
			// �շ�ע��
			t.setItem(selRow, "BILL_FLG", "Y");
			// Ƶ��
			t.setItem(selRow, "FREQ_CODE", "STAT");
			// ����
			t.setItem(selRow, "MEDI_QTY", 1);
			// ����
			t.setItem(selRow, "TAKE_DAYS", 1);
			// ����
			t.setItem(selRow, "DOSAGE_QTY", 1);
			// ҽ������
			t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
			// �Էѵ���
			t.setItem(selRow, "OWN_PRICE", ownPriceSingle);
			// �Է�
			t.setItem(selRow, "OWN_AMT", ownPriceSingle);
			// �ܼ�
//			t.setItem(selRow, "TOT_AMT", ownPriceSingle);
			t.setItem(selRow, "TOT_AMT", ownPriceSingle*ownRate);  //modify by huangtt 20130922
			// ҽ������
			t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
			// Ժ�ڷ��ô���
			t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
			// ҽ��ϸ����
			t.setItem(selRow, "ORDER_CAT1_CODE", parm
					.getValue("ORDER_CAT1_CODE"));
			// ҽ������
			t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
			// �ɱ�����
			t.setItem(selRow, "COST_CENTER_CODE", this
					.getValueString("EXE_DEPT_CODE"));
			t.setItem(selRow, "EXE_DEPT_CODE", execDept);
			// ҽ��ϸ�������
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			t.setItem(selRow,"ORDER_NO",orderNo);
			
			// �վݷ��ô���
			t.setItem(selRow, "REXP_CODE", hexpParm.getValue("IPD_CHARGE_CODE",
					0));
			if(this.getValueString("EXEC_DATE").length()==0||
					this.getValue("EXEC_DATE")==null){
				t.setItem(selRow, "EXEC_DATE", SystemTool.getInstance().getDate());
			}else{
				t.setItem(selRow, "EXEC_DATE", this.getValue("EXEC_DATE"));
			}
			// System.out.println("����ִ�е�λ"+order.getItemData(selRow,"EXE_DEPT_CODE"));
			order.setActive(selRow, true);
			onInsert(selRow);
		}
		setIncludeColor();
		onLockCellRow();
	}

	/**
	 * ����ҽ��
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newOrder(String tag, Object obj) {
		
		TParm parm = (TParm) obj;
		newOrderTemp(parm, t.getSelectedRow());
	}

	/**
	 * �ɲ�������Ĳ�ѯ���� �ٴθ�ֵ����
	 */
	public void onInitReset() {
		// ��ʼ��ҳ��
		this.initPage();
	}

	/**
	 * ����Ƽ�ȡ��ҽ�����
	 * 
	 * @return String
	 */
	private String getNo() {
		if (orderNo == null) {
			orderNo = SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO",
					"ORDER_NO");
		}
		return orderNo;
	}

	/**
	 * ��ʼ��ҳ��(�Զ���)
	 */
	public void initPage() {
		TParm parm = new TParm();
		Object obj = this.getParameter();
		if (obj == null)
			return;
		if (obj != null) {
			parm = (TParm) obj;
			// System.out.println("����Ƽ�"+parm);
			// //�ж��ǻ�ʿ������
			// if (parm.existGroup("INWEXE")) {
			// }
			t = (TTable) this.getComponent("MAINTABLE");
			if(parm.getData("IBS", "INWLEAD_FLG").toString().equals("true")){//��ʿ��Ȩ��
				callFunction("UI|EXEC_DATE|setEnabled", true);
				t.setLockColumns("2,7,8,9,13,14,15,16,19");//������ŷſ�
			}else{//һ��Ȩ��
				callFunction("UI|EXEC_DATE|setEnabled", false);
				t.setLockColumns("2,7,8,9,13,14,15,16,17,19");//������ŷſ�
			}
			caseNo = parm.getData("IBS", "CASE_NO").toString();
			String sql="SELECT CASE_NO,LUMPWORK_CODE,LUMPWORK_RATE FROM ADM_INP WHERE CASE_NO='"+caseNo+"' AND LUMPWORK_CODE IS NOT NULL";
			TParm result=new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode()<0) {
				this.messageBox("��ѯ�ײʹ���������⣺"+result.getErrText());
				return ;
			}
			if(null!=result.getValue("LUMPWORK_CODE",0)&&result.getValue("LUMPWORK_CODE",0).length()>0){
				lumpworkCode=result.getValue("LUMPWORK_CODE",0);
				lumpworkRate=result.getDouble("LUMPWORK_RATE",0);
			}
			if (null==parm.getData("ADM", "ADM_DATE")||parm.getData("ADM", "ADM_DATE").toString().length()<=0) {
				String inSql = "SELECT IN_DATE FROM ADM_INP WHERE CASE_NO = '"
					+ caseNo + "' ";
				TParm inParm = new TParm(TJDODBTool.getInstance().select(inSql));
				inDate= inParm.getValue("IN_DATE",0);
			}else{
				inDate= parm.getData("ADM", "ADM_DATE").toString();
			}
		
			// if (parm.getData("IBS", "TYPE") != null)
			// sysType = parm.getData("IBS", "TYPE").toString();
			// ==========pangben 2012-7-9 start
			String schdCodeInit = "SELECT SCHD_CODE,CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"'";
			//=========pangben 2015-10-16 �޸����²�ѯ�ٴ�·������
			TParm schdCodeParm = new TParm(TJDODBTool.getInstance().select(schdCodeInit));
			schdCode = schdCodeParm.getValue("SCHD_CODE", 0);// ʱ�̴���
			clncpathCode = schdCodeParm.getValue("CLNCPATH_CODE", 0);// �ٴ�·������
			//===modify by caowl 20121126 start
			//===modify by caowl 20121126 end 
			IBSNewTool tool=new IBSNewTool();
			TParm queryInfoParm=tool.onCheckLumWorkCaseNo("",caseNo);
			caseNoNew=queryInfoParm.getValue("CASE_NO");
			
		}
		// ===========pangben 2012-7-9 �ٴ�·������ʱ����ʾ
		if (null != clncpathCode && clncpathCode.length() > 0) {
			callFunction("UI|clpOrderQuote|setEnabled", true);
		} else {
			callFunction("UI|clpOrderQuote|setEnabled", false);
		}
		if (parm.getData("IBS", "TYPE") != null) {
			callFunction("UI|close|setEnabled", false);
		}
		if (caseNo == null || caseNo.length() == 0) {
			this.messageBox("����������");
			return;
		}
		ipdNo = parm.getData("IBS", "IPD_NO").toString();
		mrNo = parm.getData("IBS", "MR_NO").toString();
		bedNo = parm.getData("IBS", "BED_NO").toString();
		setValue("CAT1_TYPE", "");
		// ��ʼ����ǰtable
		t = (TTable) this.getComponent("MAINTABLE");
		order = new IBSOrderD(caseNo);
		// �������
		order.setCaseNo(caseNo);
		// �������
		TParm maxCaseNoSeq = selMaxCaseNoSeq(caseNo);
		if (maxCaseNoSeq.getCount("CASE_NO_SEQ") == 0) {
			order.setCaseNoSeq(1);
		} else {
			order.setCaseNoSeq((maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1));
		}
		// ��Ч����(��ʱҽ��)
		order.setBeginDate(SystemTool.getInstance().getDate());
		// ��Ч����(��ʱҽ��)
		order.setEndDate(SystemTool.getInstance().getDate());
		// �Ƽ�����
		order.setBillDate(SystemTool.getInstance().getDate());
		// ִ������
		order.setExecDate(SystemTool.getInstance().getDate());
		// ҽ������
		order.setCat1Type(getValue("CAT1_TYPE").toString());
		// ��������
//		order.setDeptCode(parm.getValue("IBS", "DEPT_CODE"));
		order.setDeptCode(Operator.getDept());
		// ��������
		order.setStationCode(parm.getValue("IBS", "STATION_CODE"));
		// ����ҽʦ
		order.setDrCode(parm.getValue("IBS", "VS_DR_CODE"));
		// ִ�п���
		order.setExeDeptCode(Operator.getCostCenter());
		// ִ�в���
		order.setExeStationCode(parm.getValue("IBS", "STATION_CODE"));
		// ִ��ҽʦ
		order.setExeDrCode(Operator.getID());
		// �ɱ�����
		order.setCostCenterCode(Operator.getCostCenter());
		// ������Ա
		order.setOptUser(Operator.getID());
		// ��������
		order.setOptDate(SystemTool.getInstance().getDate());
		// �����ն�
		order.setOptTerm(Operator.getIP());
		//===caowl 20121126 start
		//ʱ��
		//order.setSchdCode(SCHD_CODE.getComboValue());
		//�ٴ�·��
		order.setClncpathCode(clncpathCode);
		order.setSchdCode(schdCode);
		//===caowl 20121126 end
		order.onQuery();
		// ��datastore
		t.setDataStore(order);
		
		t.setDSValue();
		// �Ƽ�����
		setValue("BILL_DATE", SystemTool.getInstance().getDate());
		setValue("EXEC_DATE", SystemTool.getInstance().getDate());//===yanjing 20140805 ���ִ��ʱ���ֶ�
		// --------���εõ�--------
		// System.out.println("����Ƽۿ�������"+parm.getData("IBS", "STATION_CODE"));
		// ��������
		setValue("STATION_CODE", parm.getData("IBS", "STATION_CODE"));
		setValue("SCHD_CODE", schdCode);
		setValue("SCHD_CODE1", schdCode);
		setValue("CLNCPATH_CODE", clncpathCode);
		// ��������
//		setValue("DEPT_CODE", parm.getData("IBS", "DEPT_CODE"));
		setValue("DEPT_CODE", Operator.getDept());
		// ����ҽ��
		setValue("DR_CODE", parm.getData("IBS", "VS_DR_CODE"));
		// סԺ��
		setValue("IPD_NO", parm.getData("IBS", "IPD_NO"));
		// Ĭ�ϺͿ�����ͬ
		// ִ�в���
		setValue("EXE_STATION_CODE", getValue("STATION_CODE"));
		// String deptCode = Operator.getDept();
		// TParm deptParm = DeptTool.getInstance().selUserDept(deptCode);
		// //ִ�п���
		// if (deptParm.getCount("DEPT_CODE") > 0)
		// setValue("EXE_DEPT_CODE",
		// DeptTool.getInstance().getCostCenter(this.
		// getValueString("DEPT_CODE"), this.getValueString("STATION_CODE")));
		// else
		// setValue("EXE_DEPT_CODE", "");
		setValue("EXE_DEPT_CODE", Operator.getCostCenter());
		// ִ��ҽ��
		setValue("EXE_DR_CODE", Operator.getID());
		// �Ʒѽ��
		setValue("OWN_AMT", "");
		this.onInsert(0);
		TParm admParm = new TParm();
		admParm.setData("CASE_NO", caseNo);
		TParm selAdmParm = ADMInpTool.getInstance().selectall(admParm);
		serviceLevel = selAdmParm.getValue("SERVICE_LEVEL", 0);
		//add by huangtt start  20130922		
		ctz1Code = selAdmParm.getValue("CTZ1_CODE",0);
		ctz2Code = selAdmParm.getValue("CTZ2_CODE",0);
		ctz3Code = selAdmParm.getValue("CTZ3_CODE",0);
		//add by huangtt end 20130922
		String billStatus = selAdmParm.getValue("BILL_STATUS", 0);
		// ��Ժ�ѽ���״̬���˵������״̬
		if ("4".equals(billStatus) || "3".equals(billStatus)) {
			this.messageBox("�˲����ѳ�Ժ,�����ٿ���ҽ��");
			callFunction("UI|save|setEnabled", false);
		}
		TTextFormat OPBOOK_SEQ = (TTextFormat) this.getComponent("OPBOOK_SEQ");//wanglong add 20141010 �������뵥��
        String opBookSeqSql =
                " SELECT OPBOOK_SEQ ID FROM OPE_OPBOOK WHERE CASE_NO='#' ORDER BY OPBOOK_SEQ ";
        opBookSeqSql = opBookSeqSql.replaceFirst("#", caseNo);
        OPBOOK_SEQ.setPopupMenuSQL(opBookSeqSql);
        OPBOOK_SEQ.onQuery();
        // ��Ӧ״̬ʱ��
        this.setValue("STATE_TIME", SystemTool.getInstance().getDate());
		// ���ļ�ע��
		this.setRessureFlg();
	}
	
	/**
	 * ���ð��ļ�״̬
	 */
	private void setRessureFlg() {
		// ���ļ�ע��
		String sql = "SELECT A.REASSURE_FLG FROM ADM_INP A WHERE A.CASE_NO = '" + caseNo + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		String reassureFlg = StringUtil.isNullString(result.getValue("REASSURE_FLG", 0)) ? "N"
				: result.getValue("REASSURE_FLG", 0);
		this.setValue("REASSURE_FLG", reassureFlg);
	}
	
	public void getinPage(){
		TParm maxCaseNoSeq = selMaxCaseNoSeq(caseNo);
		if (maxCaseNoSeq.getCount("CASE_NO_SEQ") == 0) {
			order.setCaseNoSeq(1);
		} else {
			order.setCaseNoSeq((maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1));
		}
		t.setDataStore(order);
	}
	// /**
	// * �ر��¼�
	// * @return boolean
	// */
	// public boolean onClosing() {
	// switch (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", this.YES_NO_CANCEL_OPTION)) {
	// case 0:
	// if (!onSave())
	// return false;
	// break;
	// case 1:
	// break;
	// case 2:
	// return false;
	// }
	// return true;
	// }

	// /**
	// * �ر�
	// * @return Object
	// */
	// public Object onClosePanel() {
	// if (sysType.equals("BMS")) {
	// this.closeWindow();
	// return "OK";
	// }
	// return null;
	// }

	/**
	 * ���ҽ��
	 * 
	 * @return TParm(У����Ϣ)
	 */
	public TParm checkOrderSave() {
		TParm result = new TParm();
		if(null!=lumpworkCode && lumpworkCode.length()>0){
			//�ײͻ���У���Ƿ��޸Ĺ��ײ�
			result= ADMInpTool.getInstance().checkLumpWorkisUpdate(caseNo, lumpworkCode);
			if(result.getErrCode()<0){
				return result;
			}
		}
		String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
		// �¼ӵ�����
		int newRow[] = order.getNewRows(buff);

		TParm tempParm=new TParm();
		double dosageQty=0;
		boolean arrayFlg=false;
		for (int i : newRow) {
			if (!order.isActive(i, buff))
				continue;
			if (order.getRowParm(i, buff).getValue("ORDERSET_CODE").
					equals(order.getRowParm(i, buff).getValue("ORDER_CODE"))
					&&order.getRowParm(i, buff).getValue("ORDERSET_CODE").length()>0) {
				continue;
			}
			for (int j = 0; j < tempParm.getCount("ORDER_CODE"); j++) {
				
				if (tempParm.getValue("ORDER_CODE",j).
						equals(order.getRowParm(i, buff).getValue("ORDER_CODE"))) {
					arrayFlg=true;
				}
			}
			if(!arrayFlg){
				tempParm.addData("ORDER_CODE", order.getRowParm(i, buff).getValue("ORDER_CODE"));
				tempParm.addData("ORDER_DESC", order.getRowParm(i, buff).getValue("ORDER_CHN_DESC"));
				arrayFlg=false;
			}
		}
		
		for(int j=0;j<tempParm.getCount("ORDER_CODE");j++){
			for (int i : newRow) {
				if (!order.isActive(i, buff))
					continue;
				//System.out.println("order.getRowParm(i, buff)::"+i+":::"+order.getRowParm(i, buff));
				if (tempParm.getValue("ORDER_CODE",j).equals(order.getRowParm(i, buff).getValue("ORDER_CODE"))) {
					dosageQty+=order.getRowParm(i, buff).getDouble("DOSAGE_QTY");
				}
			}
			tempParm.setData("DOSAGE_QTY",j,dosageQty);
			dosageQty=0;
		}
		for (int k = 0; k < tempParm.getCount("ORDER_CODE"); k++) {
			if (tempParm.getDouble("DOSAGE_QTY", k) < 0) {
				String selQtySql = " SELECT SUM(DOSAGE_QTY) AS DOSAGE_QTY,ORDER_CODE "
						+ "   FROM IBS_ORDD "
						+ "  WHERE ORDER_CODE = '"
						+ tempParm.getValue("ORDER_CODE", k)
						+ "' "
						+ "    AND CASE_NO = '"
						+ caseNo
						+ "' "
						+ "  GROUP BY ORDER_CODE ";
				TParm selQtyParm = new TParm(TJDODBTool.getInstance().select(
						selQtySql));
				double dosageQtyTot = selQtyParm.getDouble("DOSAGE_QTY", 0);
				// System.out.println("selQtySql:::"+selQtySql);
				if (Math.abs(tempParm.getDouble("DOSAGE_QTY", k)) > dosageQtyTot) {
					String orderDesc = tempParm.getValue("ORDER_DESC", k);
					result.setErr(-1, "'"+orderDesc+"'"+"�˷����������ϼ�����,���ܱ���");
					return result;
				}
			}
		}
		
		for (int i : newRow) {
			if (!order.isActive(i, buff))
				continue;
			// ���Ӵ������λ�ܿ�-------start-------- wangl modify
			String orderCode = order.getRowParm(i, buff).getValue("ORDER_CODE");
			String orderCodeSql = " SELECT ORDER_CODE,ORDER_DESC,SPECIFICATION,DR_ORDER_FLG "
					+ "   FROM SYS_FEE "
					+ "  WHERE ORDER_CODE = '"
					+ orderCode
					+ "'";
			// System.out.println("����Ȩ�޲�ѯsql" + orderCodeSql);
			TParm orderCodeParm = new TParm(TJDODBTool.getInstance().select(
					orderCodeSql));
			boolean drOrderFlg = orderCodeParm.getBoolean("DR_ORDER_FLG", 0);
			if (drOrderFlg) {
				//
				if(this.messageBox("��ʾ",orderCodeParm.getValue("ORDER_DESC", 0) + "||"
						+ orderCodeParm.getValue("SPECIFICATION", 0)
						+ "Ϊҽʦ����ҩ,�Ƿ���",2)!=0){
					//====pangben 2014-5-9�޸���ʾ��Ϣ
					//continue;
					result.setErrCode(-1);
					result.setErrText("");
					/* result.setData("ERR", "ORDER_CODE",
					 order.getRowParm(i, buff).getValue("ORDER_CODE"));*/
					return result;
					
				}
				
			}
			if (order.getRowParm(i, buff).getValue("ORDERSET_CODE") == null
					|| order.getRowParm(i, buff).getValue("ORDERSET_CODE")
							.length() == 0) {
				// ����
				if (order.getRowParm(i, buff).getDouble("MEDI_QTY") == 0) {
					// this.messageBox_(ds.getRowParm(i,buff).getDouble("MEDI_QTY")+"==="+i);
					result.setErrCode(-1);
					result.setErrText(order.getRowParm(i, buff).getValue(
							"ORDER_DESC")
							+ "��������Ϊ:0");
					result.setData("ERR", "ORDER_CODE", order.getRowParm(i,
							buff).getValue("ORDER_CODE"));
					return result;
				}
				//caowl 20130130 start
				 //Ƶ��
				 if (order.getRowParm(i, buff).getValue("FREQ_CODE").length()==0) {
					 result.setErrCode( -2);
					 result.setErrText(order.getRowParm(i,
					 buff).getValue("ORDER_DESC") +
					 "ҽ��Ƶ�β�����Ϊ��");
					 result.setData("ERR", "ORDER_CODE",
					 order.getRowParm(i,
					 buff).getValue("ORDER_CODE"));
					 return result;
				 }
				 //caowl 20130130 end
				// ����
				if (order.getRowParm(i, buff).getInt("TAKE_DAYS") == 0) {
					result.setErrCode(-3);
					result.setErrText(order.getRowParm(i, buff).getValue(
							"ORDER_DESC")
							+ "ҽ������������Ϊ0");
					result.setData("ERR", "ORDER_CODE", order.getRowParm(i,
							buff).getValue("ORDER_CODE"));
					return result;
				}

			}
			// ִ�п���
			if (order.getRowParm(i, buff).getValue("EXE_DEPT_CODE").length() == 0) {
				result.setErrCode(-4);
				result.setErrText(order.getRowParm(i, buff).getValue(
						"ORDER_DESC")
						+ "ִ�п��Ҳ���Ϊ��");
				result.setData("ERR", "ORDER_CODE", order.getRowParm(i, buff)
						.getValue("ORDER_CODE"));
				return result;
			}
			// ��������
			if (order.getRowParm(i, buff).getValue("DEPT_CODE").length() == 0) {
				result.setErrCode(-4);
				result.setErrText(order.getRowParm(i, buff).getValue(
						"ORDER_DESC")
						+ "�������Ҳ���Ϊ��");
				result.setData("ERR", "ORDER_CODE", order.getRowParm(i, buff)
						.getValue("ORDER_CODE"));
				return result;
			}
			// ҽ������
			if (order.getRowParm(i, buff).getValue("ORDER_CODE").length() == 0) {
				result.setErrCode(-4);
				result.setErrText(order.getRowParm(i, buff).getValue(
						"ORDER_DESC")
						+ "ҽ�����벻��Ϊ��");
				result.setData("ERR", "ORDER_CODE", order.getRowParm(i, buff)
						.getValue("ORDER_CODE"));
				return result;
			}
			// System.out.println("����"+order.getRowParm(i,
			// buff).getDouble("DOSAGE_QTY"));
			//caowl 20130130 start ��������Ϊ��
			 //����
			 if (order.getRowParm(i, buff).getDouble("DOSAGE_QTY") == 0) {
				 result.setErrCode( -6);
				 result.setErrText(order.getRowParm(i,
				 buff).getValue("ORDER_CHN_DESC") + "��������Ϊ0");
				 result.setData("ERR", "ORDER_CODE",
				 order.getRowParm(i, buff).getValue("ORDER_CODE"));
				 return result;
			 }
			 //caowl 20130130 end
			// ��˿��
			// if("PHA".equals(ds.getRowParm(i,buff).getValue("CAT1_TYPE"))){
			// if(!INDTool.getInstance().inspectIndStock(ds.getRowParm(i,buff).getValue("EXEC_DEPT_CODE"),ds.getRowParm(i,buff).getValue("ORDER_CODE"),
			// ds.getRowParm(i,buff).getDouble("DOSAGE_QTY"))){
			// result.setErrCode(-5);
			// result.setErrText(ds.getRowParm(i,buff).getValue("ORDER_DESC")+"��治�㣡");
			// result.setData("ERR","INDEX",index.get(ds.getRowParm(i,buff).getValue("RX_KIND")));
			// result.setData("ERR","ORDER_CODE",ds.getRowParm(i,buff).getValue("ORDER_CODE"));
			// return result;
			// }
			// }
			//�Ĳ����add by huangjw 20150306
			boolean isDebug = true;
			try {
				String sql = "SELECT ORDER_CODE,ORDER_DESC FROM SYS_FEE WHERE ORDER_CODE = '"+orderCode+"' AND  SUPPLIES_TYPE='1'";
				TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
				if(parm.getCount() > 0){
					if(order.getRowParm(i, buff).getValue("INV_CODE").length() == 0){
						result.setErrCode(-4);
						result.setErrText(order.getRowParm(i, buff).getValue(
								"ORDER_DESC")
								+ parm.getValue("ORDER_DESC",0) +"Ϊ��ֵ�Ĳģ�������Ų���Ϊ��");
						result.setData("ERR", "ORDER_CODE", order.getRowParm(i, buff)
								.getValue("ORDER_CODE"));
						return result;
					}
				}
			} catch (Exception e) {
				if(isDebug){
					System.out.println("come in class: IBSOrderControl ��method ��checkOrderSave");
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	/**
	 * ����
	 * 
	 * @return boolean
	 */
	public boolean onSave() {
		// if (!checkFee(caseNo)) {
		// this.messageBox("����!");
		// return false;
		// }
		t.acceptText();
		double newTotamt = TypeTool.getDouble(getValue("OWN_AMT"));
		getinPage();
		// ���
		TParm checkParm = checkOrderSave();
		if (checkParm.getErrCode() < 0) {
			if (checkParm.getErrText() != null
					&& !checkParm.getErrText().equals("")) {
				this.messageBox(checkParm.getErrText());
			}
			return false;
		}
		//==pangben 2016-9-13
		try {
			IBSTool.getInstance().onCheckClpDiff(caseNo);
		} catch (Exception e) {
			// TODO: handle exception
		}
		// ҽ�����
		order.setOrderNo(getNo());
		order.setFilter("");
		order.filter();
		int max = 1;
		int rowPCount = order.rowCount();
		String ordCode="";
		boolean flag=false;
		TParm sresult=new TParm();
		for (int i = 0; i < rowPCount; i++) {
			// ҽ��˳���
			ordCode=t.getItemString(i, "ORDER_CODE");
			if(ordCode.length()>0){
				sresult=this.getOrderCode(ordCode);
				if(sresult.getValue("ORDER_CODE",0).length()<=0){
					 this.setOrderDesc(t.getItemString(i, "ORDER_CODE"));
					 flag=true;
					 break;
				}
			}
			max++;
			t.setItem(i, "ORDER_SEQ", "" + max);	
			order.setFilter("ORDERSET_CODE ='' OR (ORDERSET_CODE !='' AND INDV_FLG='N')");
			order.filter();
		}
		////$------------start caoyong 20131111----------------
		if(flag){//t.getItemString(i, "ORDER_DESC")+
			 this.messageBox(this.getOrderDesc()+"ҽ�������ڣ�������ѡ��");
			  return false;
		}
		//==========modify by caowl 20120911 start
		// �������
		TParm maxCaseNoSeq = selMaxCaseNoSeq(caseNo);
		if (maxCaseNoSeq.getCount("CASE_NO_SEQ") == 0) {
			order.setCaseNoSeq(1);
		} else {
			order.setCaseNoSeq((maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1));
		}		
		//=========modify by caowl 20120911 end
		//$------------end caoyong 2013111----------------
		String buffer = order.isFilter() ? order.FILTER : order.PRIMARY;
		// �˵������
		int[] countArray = order.getNewRows(buffer);
		int seqNo = 1;
		for (int i : countArray) {
			order.setItem(countArray[i], "SEQ_NO", seqNo, buffer);
			seqNo++;
			order.setItem(i, "CASE_NO_SEQ", order.getCaseNoSeq(),buffer);//add caoyong ���¸�ֵ����������ͻ
		}
		order.setFilter("");
		int rowNewCount[] = order.getNewRows();
		for (int temp : rowNewCount) {
			TParm parmTemp = order.getRowParm(temp);
			if ("N".equals(parmTemp.getValue("INDV_FLG"))
					&& parmTemp.getValue("ORDERSET_CODE").length() != 0) {
				order.setItem(temp, "TOT_AMT", 0);
				order.setItem(temp, "OWN_PRICE", 0);
			}
		}
		String[] sql = order.getUpdateSQL();
		String[] insertOrdM = new String[2];
		//===zhangp 20130131 start
//		String selADMSql = " SELECT TOTAL_AMT,CUR_AMT " + "   FROM ADM_INP "
//				+ "  WHERE CASE_NO = '" + order.getCaseNo() + "'";
		String selADMSql = " SELECT TOTAL_AMT,CUR_AMT,DEPT_CODE " + "   FROM ADM_INP "
				+ "  WHERE CASE_NO = '" + order.getCaseNo() + "'";
		TParm selADMParm = new TParm(TJDODBTool.getInstance().select(selADMSql));
		
		//===start===add by kangy 20161012 TOTAL_AMT��IBS_ORDD��ȡֵ       TOTAL_BILPAY ��BIL_PAY��ȡֵ
		String totAmtSql="SELECT SUM(TOT_AMT) TOTAL_AMT FROM IBS_ORDD WHERE CASE_NO='"+order.getCaseNo()+"'";
		TParm totAmtParm=new TParm(TJDODBTool.getInstance().select(totAmtSql));
		
		String totalBilPaySql="SELECT SUM(PRE_AMT) TOTAL_BILPAY FROM BIL_PAY WHERE CASE_NO='"+order.getCaseNo()+"'";
		TParm totalBilPayParm=new TParm(TJDODBTool.getInstance().select(totalBilPaySql));
		
		double totalBilPay=totalBilPayParm.getDouble("TOTAL_BILPAY",0);
		double oldTotalAmt=totAmtParm.getDouble("TOTAL_AMT",0);//���ܼ�
		double oldCurAmt=totalBilPay-oldTotalAmt;//�����
		//===end===add by kangy 20161012 TOTAL_AMT��IBS_ORDD��ȡֵ       TOTAL_BILPAY ��BIL_PAY��ȡֵ	
		
		
		String deptCode = selADMParm.getValue("DEPT_CODE", 0);
		if(deptCode.length()==0){
			deptCode = order.getDeptCode();
		}
		//===zhangp 20130131 end
		//double oleTotalAmt = selADMParm.getDouble("TOTAL_AMT", 0);
		// System.out.println("���ܼ�"+oleTotalAmt);
		//double oleCurAmt = selADMParm.getDouble("CUR_AMT", 0);
		// System.out.println("��ʣ����"+oleTotalAmt);
		double newTotalAmt = oldTotalAmt + newTotamt;
		// System.out.println("�½�����"+newTotamt);
		double newCurAmt = oldCurAmt - newTotamt;
		// System.out.println("������Ա����"+Operator.getRegion() );
		insertOrdM[0] = " INSERT INTO IBS_ORDM (CASE_NO,CASE_NO_SEQ,BILL_DATE,IPD_NO,MR_NO,"
				+ "             DEPT_CODE,STATION_CODE,BED_NO,DATA_TYPE,BILL_NO,"
				+ "             OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE)"
				+ "      VALUES ('"
				+ order.getCaseNo()
				+ "','"
				+ order.getCaseNoSeq()
				+ "',TO_DATE('"
				+ StringTool.getString(order.getBillDate(),
						"yyyy/MM/dd HH:mm:ss")
				+ "','YYYY/MM/DD HH24:MI:SS'),'"
				+ ipdNo
				+ "','"
				+ mrNo
				+ "',"
				+ "             '"
				//====zhangp 20130131 start
//				+ order.getDeptCode()
				+ deptCode
				//====zhangp 20130131 end
				+ "','"
				+ order.getStationCode()
				+ "','"
				+ bedNo
				+ "','"
				+ "1"
				+ "','"
				+ ""
				+ "','"
				+ order.getOptUser()
				+ "',SYSDATE,'"
				+ order.getOptTerm()
				+ "','"
				+ Operator.getRegion() + "')";
		insertOrdM[1] = " UPDATE ADM_INP SET TOTAL_AMT = '" + newTotalAmt
				+ "',CUR_AMT = '" + newCurAmt + "' " + "  WHERE CASE_NO = '"
				+ order.getCaseNo() + "'";
		sql = StringTool.copyArray(sql, insertOrdM);
		 // wanglong add 20141010 ��������״̬������ʱ�䣬�������������
        //int StateSeq = -1;
        String opBookSeq = this.getValueString("OPBOOK_SEQ");
        String opState = this.getValueString("OPE_STATE");
        if (!opBookSeq.equals("")) {
            String opBookSql = "SELECT TYPE_CODE FROM OPE_OPBOOK WHERE OPBOOK_SEQ = '#'";
            opBookSql = opBookSql.replaceFirst("#", opBookSeq);
            TParm typeParm = new TParm(TJDODBTool.getInstance().select(opBookSql));
            if (typeParm.getErrCode() < 0) {
                this.messageBox("�����������ʧ�� " + typeParm.getErrText());
            } else if (typeParm.getCount() > 0) {
                if (opState.equals("")) {
                    this.messageBox("��ѡ������״̬");
                    return false;
                } 
                if (this.getValueString("STATE_TIME").equals("")) {
                    this.messageBox("����д����ʱ��");
                    return false;
                }
//                if (typeParm.getValue("TYPE_CODE", 0).equals("1")) {// 1����������
//                    if (!StringUtil.getDesc("OPE_OPBOOK", "TO_CHAR(STATE_TIME_3)",
//                                            "OPBOOK_SEQ='" + opBookSeq + "'").equals("")) {
//                        StateSeq = 7;
//                    } else if (!StringUtil.getDesc("OPE_OPBOOK", "TO_CHAR(STATE_TIME_2)",
//                                                   "OPBOOK_SEQ='" + opBookSeq + "'").equals("")) {
//                        StateSeq = 6;
//                    } else if (!StringUtil.getDesc("OPE_OPBOOK", "TO_CHAR(STATE_TIME_1)",
//                                                   "OPBOOK_SEQ='" + opBookSeq + "'").equals("")) {
//                        StateSeq = 5;
//                    } else if (!StringUtil.getDesc("OPE_OPBOOK", "TO_CHAR(STATE_TIME_0)",
//                                                   "OPBOOK_SEQ='" + opBookSeq + "'").equals("")) {
//                        StateSeq = 2;
//                    }
//                } else {// 1��������
//                    if (!StringUtil.getDesc("OPE_OPBOOK", "TO_CHAR(STATE_TIME_3)",
//                                            "OPBOOK_SEQ='" + opBookSeq + "'").equals("")) {
//                        StateSeq = 7;
//                    } else if (!StringUtil.getDesc("OPE_OPBOOK", "TO_CHAR(STATE_TIME_2)",
//                                                   "OPBOOK_SEQ='" + opBookSeq + "'").equals("")) {
//                        StateSeq = 5;
//                    } else if (!StringUtil.getDesc("OPE_OPBOOK", "TO_CHAR(STATE_TIME_1)",
//                                                   "OPBOOK_SEQ='" + opBookSeq + "'").equals("")) {
//                        StateSeq = 4;
//                    } else if (!StringUtil.getDesc("OPE_OPBOOK", "TO_CHAR(STATE_TIME_0)",
//                                                   "OPBOOK_SEQ='" + opBookSeq + "'").equals("")) {
//                        StateSeq = 2;
//                    }
//                }
                Timestamp stateTime = (Timestamp) this.getValue("STATE_TIME");
                String updateOpBook = "";
                if (opState.equals("2")) {
                    updateOpBook =
                            "UPDATE OPE_OPBOOK SET STATE='&',STATE_TIME_0=TO_DATE('@','YYYY/MM/DD HH24:MI:SS') WHERE OPBOOK_SEQ='#'";
                } else if ((opState.equals("5") && typeParm.getValue("TYPE_CODE", 0).equals("1"))
                        || (opState.equals("4") && typeParm.getValue("TYPE_CODE", 0).equals("2"))) {
                    updateOpBook =
                            "UPDATE OPE_OPBOOK SET STATE='&',STATE_TIME_1=TO_DATE('@','YYYY/MM/DD HH24:MI:SS') WHERE OPBOOK_SEQ='#'";
                } else if ((opState.equals("6") && typeParm.getValue("TYPE_CODE", 0).equals("1"))
                        || (opState.equals("5") && typeParm.getValue("TYPE_CODE", 0).equals("2"))) {
                    updateOpBook =
                            "UPDATE OPE_OPBOOK SET STATE='&',STATE_TIME_2=TO_DATE('@','YYYY/MM/DD HH24:MI:SS') WHERE OPBOOK_SEQ='#'";
                } else if (opState.equals("7")) {
                    updateOpBook =
                            "UPDATE OPE_OPBOOK SET STATE='&',STATE_TIME_3=TO_DATE('@','YYYY/MM/DD HH24:MI:SS') WHERE OPBOOK_SEQ='#'";
                }
                if (!updateOpBook.equals("")) {
                    updateOpBook = updateOpBook.replaceFirst("&", opState);
                    updateOpBook =
                            updateOpBook.replaceFirst("@", StringTool
                                    .getString(stateTime, "yyyy/MM/dd HH:mm:ss"));
                    updateOpBook = updateOpBook.replaceFirst("#", opBookSeq);
                    sql = StringTool.copyArray(sql, new String[]{updateOpBook });
                }
            } else {
                this.messageBox("�������Ų�����");
            }
        }
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			//caowl 20130320 start	   		    
			//�����ظ���ʱ����ʾ���±���
			String errText = result.getErrText().substring(0,9);	
			if(errText.equals("ORA-00001")){
				this.messageBox("����һλ��ʿ���Դ˲��˽��мƷѲ�������رս������½���");
			}else{
				// ����ʧ��
				this.messageBox("E0001");
			}
			//caowl 20130320 end
		} else {
			// ����ɹ�
			this.messageBox("P0001");
			result = ADMTool.getInstance().checkStopFee(caseNo);
			onInit();
			for (int i = 0; i < t.getRowCount(); i++) {
				t.setLockCellRow(i, false);
				t.setRowColor(i, Color.WHITE);//===pangben 2015-11-2 �����ɫ
			}
		}
		return true;
	}
	 /**
     * ��֤SYS_FEE�����Ƿ�����ӵ�ҽ��
     */
	public TParm getOrderCode(String ordCode){
		String sql="SELECT ORDER_CODE FROM SYS_FEE WHERE ORDER_CODE='"+ordCode+"'";
		 TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		 return result;
	}
	/**
	 * У�����
	 * 
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	public boolean checkFee(String caseNo) {
		TParm inSelADMAllData = new TParm();
		inSelADMAllData.setData("CASE_NO", caseNo);
		TParm selADMAllData = ADMInpTool.getInstance().selectall(
				inSelADMAllData);
		// System.out.println("У�����"+selADMAllData);
		if ("Y".equals(selADMAllData.getValue("STOP_BILL_FLG", 0)))
			return false;
		return true;
	}

	/**
	 * ɾ��
	 */
	public void onDelete() {
		int row = t.getSelectedRow();
		TParm tableParm = t.getDataStore().getRowParm(row);
		// System.out.println("tableParm��ʾ����" + tableParm);
		int ordersetGroupNo = tableParm.getInt("ORDERSET_GROUP_NO");
		String ordersetCode = tableParm.getValue("ORDERSET_CODE");
		String orderCode = tableParm.getValue("ORDER_CODE");
		if (orderCode.length() == 0)
			return;
		if (orderCode.equals(ordersetCode)) {
			deleteDetialCode(ordersetGroupNo, ordersetCode);
			setIncludeColor();
			onLockCellRow();
			return;
		}
		t.getDataStore().deleteRow(row);
		t.setDSValue();
		t.acceptText();
		order.showDebug();
		setTotAmt();
		// int allCount = t.getRowCount();
		// if (allCount - 1 != row && row >= 0) {
		// t.removeRow(row);
		// }
		setIncludeColor();
		onLockCellRow();
	}
	/**
	 * 
	* @Title: setIncludeColor
	* @Description: TODO(�����ײ�����ɫ)
	* @author pangben 2015-11-2
	* @throws
	 */
	private void setIncludeColor(){
		if (null==colorParm||colorParm.getCount("ORDER_DESC")<=0) {
			for (int i = 0; i < t.getRowCount(); i++) {
				t.setRowColor(i, Color.WHITE);//===pangben 2015-11-2 �����ɫ
			}
		}else{
			for (int i = 0; i < t.getRowCount(); i++) {
				for (int j = 0; j < colorParm.getCount("ORDER_DESC"); j++) {
					if (null!=t.getShowParmValue().getValue("ORDER_DESC",i)&&t.getShowParmValue().getValue("ORDER_DESC",i).length()>0&&
							colorParm.getValue("ORDER_DESC",j).length()>0&&t.getShowParmValue().getValue("ORDER_DESC",i).contains(colorParm.getValue("ORDER_DESC",j))) {
						t.setRowColor(i, Color.YELLOW);//===pangben 2015-11-2 �����ɫ
						break;
					}else{
						t.setRowColor(i, Color.WHITE);//===pangben 2015-11-2 �����ɫ
					}	
				}
			}
		}
	}
	/**
	 * ɾ������ҽ��ϸ��
	 * 
	 * @param ordersetGroupNo
	 *            String
	 * @param ordersetCode
	 *            String
	 */
	public void deleteDetialCode(int ordersetGroupNo, String ordersetCode) {
		// String filterStr = "INDV_FLG='N' OR INDV_FLG IS NULL ";
		String filterStr = "INDV_FLG='N' OR INDV_FLG ='' ";
		t.setFilter("");
		t.filter();
		String buffer = order.isFilter() ? order.FILTER : order.PRIMARY;
		int rowCount = order.isFilter() ? order.rowCountFilter() : order
				.rowCount();
		order.showDebug();
		for (int i = rowCount - 1; i >= 0; i--) {
			if (!order.isActive(i, buffer))
				continue;
			TParm temp = order.getRowParm(i, buffer);
			if (temp.getInt("ORDERSET_GROUP_NO") == ordersetGroupNo) {
				order.deleteRow(i, buffer);
			}
		}
		t.setFilter(filterStr);
		t.filter();
		t.setDSValue();
		order.showDebug();
		t.getDataStore().showDebug();
		setTotAmt();
	}

	/**
	 * �һ�MENU�����¼�
	 */
	public void showPopMenu() {
		TTable table = (TTable) this.getComponent("MAINTABLE");
		TParm parm = table.getShowParmValue();
		int row = table.getSelectedRow();
		if (parm.getData("ORDER_DESC", row) != null
				&& order.getItemString(row, "ORDERSET_CODE").length() > 0) {
			table.setPopupMenuSyntax("��ʾ����ҽ��ϸ��,openRigthPopMenu");
			return;
		} else {
			table.setPopupMenuSyntax("");
			return;
		}
	}

	/**
	 * �򿪼���ҽ��ϸ���ѯ
	 */
	public void openRigthPopMenu() {
		TTable table = (TTable) this.getComponent("MAINTABLE");
		int row = table.getSelectedRow();
		int groupNo = order.getItemInt(row, "ORDERSET_GROUP_NO");
		String orderCode = order.getItemString(row, "ORDER_CODE");
		// this.messageBox_("����ҽ��groupNo" + groupNo);
		// this.messageBox_("����ҽ��orderCode" + orderCode);
		TParm parm = getOrderSetDetails(groupNo, orderCode);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
	}

	/**
	 * ���ؼ���ҽ��ϸ���TParm��ʽ
	 * 
	 * @param groupNo
	 *            int
	 * @param orderSetCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderSetDetails(int groupNo, String orderSetCode) {
		// System.out.println("����ҽ�����" + groupNo);
		// System.out.println("����ҽ�������" + orderSetCode);
		TParm result = new TParm();
		if (groupNo < 0) {
			System.out
					.println("OpdOrder->getOrderSetDetails->groupNo is invalie");
			return result;
		}
		if (StringUtil.isNullString(orderSetCode)) {
			System.out
					.println("OpdOrder->getOrderSetDetails->orderSetCode is invalie");
			return result;
		}
		TParm parm = order.getBuffer(order.isModified() ? order.FILTER
				: order.PRIMARY);

		// System.out.println("ҽ����Ϣ" + parm);
		int count = parm.getCount();
		if (count < 0) {
			System.out.println("OpdOrder->getOrderSetDetails->count <  0");
			return result;
		}
		// temperrϸ��۸�
		for (int i = 0; i < count; i++) {
			if (!orderSetCode.equals(parm.getValue("ORDER_CODE", i))
					&& parm.getBoolean("#ACTIVE#", i)
					&& orderSetCode.equals(parm.getValue("ORDERSET_CODE", i))) {
				// ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
				result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
				result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				// ��ѯ����
				TParm orderParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT OWN_PRICE,ORDER_DESC,SPECIFICATION,OPTITEM_CODE,INSPAY_TYPE "
								+ "FROM SYS_FEE WHERE ORDER_CODE='"
								+ parm.getValue("ORDER_CODE", i) + "'"));
				// this.messageBox_(ownPriceParm);
				// �����ܼ۸�
				double ownPrice = orderParm.getDouble("OWN_PRICE", 0)
						* parm.getDouble("MEDI_QTY", i);
				result
						.addData("OWN_PRICE", orderParm.getDouble("OWN_PRICE",
								0));
				result.addData("OWN_AMT", ownPrice);
				result.addData("ORDER_DESC", orderParm
						.getValue("ORDER_DESC", 0));
				result.addData("SPECIFICATION", orderParm.getValue(
						"SPECIFICATION", 0));
				result.addData("EXEC_DEPT_CODE", parm.getValue("EXE_DEPT_CODE",
						i));
				result.addData("OPTITEM_CODE", orderParm.getValue(
						"OPTITEM_CODE", 0));
				result.addData("INSPAY_TYPE", orderParm.getValue("INSPAY_TYPE",
						0));
			}
		}
		return result;
	}

	/**
	 *ҽ�����ı�
	 */
	public void onChangeCat1Type() {
		if (t != null)
			t.acceptText();
	}

	public static void main(String[] args) {
		com.javahis.util.JavaHisDebug.TBuilder();
		System.out.println(TDataStore.helpEvent());

	}

	/**
	 * �õ���ʿվ����
	 * 
	 * @param parm
	 *            TParm
	 */
	public void getINWData(TParm parm) {
		// this.messageBox_("���뻤ʿִ��");
		TTable table = (TTable) this.getComponent("MAINTABLE");
		table.removeAll();
		// this.messageBox_("���ջ�ʿվ����" + parm);
	}

	/**
	 * ��ѯ����������
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public synchronized TParm selMaxCaseNoSeq(String caseNo) {
		String sql = " SELECT MAX(CASE_NO_SEQ) AS CASE_NO_SEQ FROM IBS_ORDM WHERE CASE_NO = '"
				+ caseNo + "' ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * ��ѯ���ҽ��˳���
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm selMaxOrderSeq(String caseNo) {
		String sql = " SELECT MAX(ORDER_SEQ) AS ORDER_SEQ FROM IBS_ORDD "
				+ "  WHERE CASE_NO = '" + caseNo + "' "
				+ "    AND ORDER_NO = '" + orderNo + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ��󼯺�ҽ�����
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm seleMaxOrdersetGroupNo(String caseNo) {
		String sql = " SELECT MAX(TO_NUMBER(ORDERSET_GROUP_NO)) AS ORDERSET_GROUP_NO FROM IBS_ORDD"
				+ "  WHERE CASE_NO = '" + caseNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return result;
		}
		int groupNo = result.getInt("ORDERSET_GROUP_NO", 0);
		TParm filterParm = order.getBuffer(order.isFilter() ? order.FILTER
				: order.PRIMARY);
		for (int i = 0; i < filterParm.getCount(); i++) {
			if (filterParm.getInt("ORDERSET_GROUP_NO", i) > groupNo) {
				groupNo = filterParm.getInt("ORDERSET_GROUP_NO", i);
			}
		}
		result.setData("ORDERSET_GROUP_NO", 0, groupNo + 1);
		return result;
	}

	/**
	 * �����Ҳ���Ʒ�
	 */
	public void onOperation() {
		operationParm = new TParm();
		TParm parm = new TParm();
		parm.setData("PACK", "DEPT", Operator.getDept());
		operationParm = (TParm) this.openDialog(
				"%ROOT%\\config\\sys\\sys_fee\\SYSFEE_ORDSETOPTION.x", parm,
				false);
		// System.out.println("��������Ƽ�" + operationParm);
		TParm parm_obj = new TParm();
		for (int i = 0; i < operationParm.getCount("ORDER_CODE"); i++) {
			String sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE = '"
					+ operationParm.getValue("ORDER_CODE", i) + "' ";
			parm_obj = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm_obj == null || parm_obj.getCount() <= 0) {
				continue;
			}
			insertNewOperationOrder(parm_obj.getRow(0), operationParm
					.getDouble("DOSAGE_QTY", i));
		}
	}
	/**
	 * 
	* @Title: insertReOperationOrder
	* @Description: TODO(�˷Ѵ���ҽ��)
	* @author pangben 2016-2-24
	* @param parm
	* @param dosage_qty
	* @throws
	 */
	private void insertReOperationOrder(TParm parm, double dosage_qty){
		t.acceptText();
		int selRow = t.getRowCount() - 1;
		double ownPriceSingle = 0.00;
		String execDept= parm.getValue("EXEC_DEPT_CODE");
		ownPriceSingle = parm.getDouble("OWN_PRICE");
		if (StringUtil.isNullString(execDept)) {
			execDept = getValue("EXE_DEPT_CODE").toString();//modify by caowl 20120810	
		}
		OrderCode = parm.getValue("ORDER_CODE");  //add by huangtt 20130922
		ownRate =parm.getDouble("OWN_RATE");  //add by huangtt 20130922
		order.setOwnRate(ownRate); //add by huangtt 20131114	
		// ״̬��
		callFunction("UI|setSysStatus", parm.getValue("ORDER_CODE")
				+ parm.getValue("ORDER_DESC") + parm.getValue("GOODS_DESC")
				+ parm.getValue("DESCRIPTION") + parm.getValue("SPECIFICATION"));
		t.setSelectedRow(selRow);   //add by huangtt 20130922
		//======pangben 2015-7-16 ����ײ���ҽ����ʾ
		if (IBSTool.getInstance().getLumpworkOrderStatus(this.caseNo, parm.getValue("ORDER_CODE"))) {
			//this.messageBox(parm.getValue("ORDER_DESC")+"ҽ��Ϊ�ײ���ҽ��");
			//t.setRowColor(selRow, Color.YELLOW);//===pangben 2015-11-2 �����ɫ
			colorParm.addData("ORDER_DESC",parm.getValue("ORDER_DESC"));
		}
		t.setItem(selRow, "DS_FLG", "N");
		// �ж��Ƿ��Ǽ���ҽ��
		if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
			//TParm hexpParm =null;
			TParm parmDetail=null;
			//�˷Ѳ�������ѯIBS_ORDD���ҽ����ϸ���ֹ�޸��ֵ��������
			//hexpParm=new TParm();
			// �վݷ��ô���
			t.setItem(selRow, "REXP_CODE", parm.getValue("REXP_CODE"));
			String sql="SELECT A.ORDER_CODE,A.OWN_PRICE,A.NHI_PRICE,ABS(A.TOT_AMT/"+parm.getDouble("DOSAGE_QTY")+") TOT_AMT,A.FREQ_CODE," +
					"B.ORDER_DESC,B.CHARGE_HOSP_CODE,B.OPTITEM_CODE,ABS(A.DOSAGE_QTY/"+parm.getDouble("DOSAGE_QTY")+") AS TOTQTY,A.REXP_CODE,B.ORDER_CODE AS ORDER_CODE_FEE," +
					"B.CAT1_TYPE,B.ORDER_CAT1_CODE,B.UNIT_CODE,B.INSPAY_TYPE,B.DEGREE_CODE,A.OWN_RATE,A.EXE_DEPT_CODE,A.COST_CENTER_CODE " +
					"FROM IBS_ORDD A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE(+) AND A.CASE_NO='"+caseNo+"' AND A.ORDERSET_GROUP_NO='"+
					parm.getValue("ORDERSET_GROUP_NO")+"' AND A.INDV_FLG='Y' AND A.ORDERSET_CODE='"+parm.getValue("ORDER_CODE")+"' AND A.TOT_AMT>=0";
			parmDetail=new TParm(TJDODBTool.getInstance().select(sql));
			if (parmDetail.getErrCode()<0||parmDetail.getCount()<=0) {
				this.messageBox("���ױ��ѯ"+parm.getValue("ORDER_DESC")+"����ҽ�����ݳ�������");
				return;
			}
			for (int i = 0; i < parmDetail.getCount(); i++) {
				if (null==parmDetail.getValue("ORDER_CODE_FEE",i) ||
						parmDetail.getValue("ORDER_CODE_FEE",i).length()<=0) {
					this.messageBox("ҽ������:"+parmDetail.getValue("ORDER_CODE",i)+"���ֵ��в�����");
					return;
				}
			}
			// �Է�ע��
			t.setItem(selRow, "OWN_FLG", "Y");
			// �շ�ע��
			t.setItem(selRow, "BILL_FLG", "Y");
			// �Ը�����
//			t.setItem(selRow, "OWN_RATE", 1);
			t.setItem(selRow, "OWN_RATE", ownRate); //modify by huangtt 20130922

			// ҽ������
			t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
			// ҽ�����
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// ҽ������
			t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
			// ҽ��ϸ����
			t.setItem(selRow, "ORDER_CAT1_CODE", parm
					.getValue("ORDER_CAT1_CODE"));
			//�ɱ�����
			t.setItem(selRow, "COST_CENTER_CODE", execDept);
			// ��ҩ��λ
			t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
			// ҽ������
			t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
			// Ժ�ڷ��ô���
			t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
			// ҽ�����
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// ����ҽ�����
//			t.setItem(selRow, "ORDERSET_GROUP_NO", parm
//					.getInt("ORDERSET_GROUP_NO"));
			
			String orderCode = parm.getValue("ORDER_CODE");
			t.setItem(selRow, "ORDERSET_CODE", orderCode);
			t.setItem(selRow, "INDV_FLG", "N");
			t.setItem(selRow, "MEDI_QTY", dosage_qty);
			t.setItem(selRow, "TAKE_DAYS", 1);
			t.setItem(selRow, "DOSAGE_QTY", dosage_qty);
			t.setItem(selRow, "EXE_DEPT_CODE", execDept);
			t.setItem(selRow, "FREQ_CODE", parmDetail.getValue(
					"FREQ_CODE", 0));//============modify by caowl 20120810
			order.setActive(selRow, true);
			
			if (parmDetail.getErrCode() != 0) {
				this.messageBox("ȡ��ϸ�����ݴ���");
				return;
			}
			// ����ҽ�����
			TParm groupNoParm = this.seleMaxOrdersetGroupNo(caseNo);
			groupNo = groupNoParm.getInt("ORDERSET_GROUP_NO", 0);
			t.setItem(selRow, "ORDERSET_GROUP_NO", groupNo);
			double allOwnAmt = 0.00;
			double allTotAmt=0.00;//�˷ѻ���IBS_ORDD����
			for (int i = 0; i < parmDetail.getCount(); i++) {
				int row =order.insertRow();
				// �Է�ע��
				order.setItemRe(row, "OWN_FLG", "Y");
				// ҽ������
				order.setItemRe(row, "ORDER_DESC", parmDetail.getValue(
						"ORDER_CODE", i));
				// ҽ�����
				order.setItemRe(row, "CAT1_TYPE", parmDetail.getValue(
						"CAT1_TYPE", i));
				// ҽ��ϸ����
				order.setItemRe(row, "ORDER_CAT1_CODE", parmDetail.getValue(
						"ORDER_CAT1_CODE", i));
				// ҽ������
				order.setItemRe(row, "ORDER_CHN_DESC", parmDetail.getValue(
						"ORDER_DESC", i));
				order.setItemRe(row, "BILL_FLG", "Y");
				
				
				order.setItemRe(row, "OWN_PRICE", parmDetail.getDouble("OWN_PRICE",i));
				// �վݷ��ô���
				order.setItemRe(row, "REXP_CODE", parmDetail.getValue("REXP_CODE",i));
				order.setItemRe(row, "FREQ_CODE",  parmDetail.getValue(
						"FREQ_CODE", i)); 
				//Ƶ��
//				t.setItem(selRow, "FREQ_CODE", parmDetail.getValue(
//						"FREQ_CODE", i));//============modify by caowl 20120810
				
				// ҽ������
				order.setItemRe(row, "NHI_PRICE", parmDetail
						.getDouble("NHI_PRICE",i));
				// Ժ�ڷ��ô���
				order.setItemRe(row, "HEXP_CODE", parmDetail.getValue(
						"CHARGE_HOSP_CODE", i));
				// ����ҽ�����
				order.setItemRe(row, "ORDERSET_GROUP_NO", groupNo);
				// //��ѯ�վݷ��ô���
				// hexpParm =
				// SYSChargeHospCodeTool.getInstance().selectalldata(parmDetail);


				order.setItemRe(row, "OPTITEM_CODE", parmDetail.getValue(
						"OPTITEM_CODE", i));
				order.setItemRe(row, "EXE_DEPT_CODE", parmDetail.getValue(
						"EXE_DEPT_CODE", i));
				//�ɱ�����
				order.setItemRe(row, "COST_CENTER_CODE", parmDetail.getValue(
						"COST_CENTER_CODE", i));
				order.setItemRe(row, "INSPAY_TYPE", parmDetail.getValue(
						"INSPAY_TYPE", i));
				order.setItemRe(row, "RPTTYPE_CODE", parmDetail.getValue(
						"RPTTYPE_CODE", i));
				order.setItemRe(row, "DEGREE_CODE", parmDetail.getValue(
						"DEGREE_CODE", i));
				order.setItemRe(row, "CHARGE_HOSP_CODE", parmDetail.getValue(
						"CHARGE_HOSP_CODE", i));
				order.setItemRe(row, "INDV_FLG", "Y");
				order.setItemRe(row, "ORDERSET_CODE", orderCode);
				order.setItemRe(row, "ORDERSET_GROUP_NO", groupNo);
				order.setItemRe(row, "SCHD_CODE", order.getSchdCode());
				double ownPrice =parmDetail.getDouble("OWN_PRICE",i);
				allTotAmt+=parmDetail.getDouble("TOT_AMT",i)/parmDetail.getDouble("TOTQTY",i);
			
				order.setItemRe(row, "OWN_PRICE", ownPrice);
				order.setItemRe(row, "TOT_AMT", parmDetail.getDouble("TOT_AMT",i));
				order.setItemRe(row, "OWN_RATE", parmDetail.getDouble("OWN_RATE",i));
				order.setItemRe(row, "INCLUDE_FLG", parm.getValue("INCLUDE_FLG"));
				order.setItemRe(row, "INCLUDE_EXEC_FLG", "Y");
				order.setItemRe(row, "TAKE_DAYS", 1);
				allOwnAmt+= ownPrice*parmDetail.getDouble("TOTQTY",i);
				
				// double qty = parmDetail.getDouble("DOSAGE_QTY", i);
				
				// ��ҩ��λ
				order.setItemRe(row, "MEDI_UNIT", parmDetail.getValue(
						"UNIT_CODE", i));
				// ��ҩ��λ
				order.setItemRe(row, "DOSAGE_UNIT", parmDetail.getValue(
						"UNIT_CODE", i));
				order.setItemRe(row, "DISPENSE_UNIT", parmDetail.getValue(
						"UNIT_CODE", i));
				order.setActive(row, true);
				if(this.getValueString("EXEC_DATE").length()==0||
						this.getValue("EXEC_DATE")==null){
					order.setItemRe(row, "EXEC_DATE", SystemTool.getInstance().getDate());
				}else{
					order.setItemRe(row, "EXEC_DATE",  this.getValue("EXEC_DATE"));
				}
				order.setItemRe(row, "MEDI_QTY", parmDetail.getDouble("TOTQTY",i));
				order.setItemRe(row, "DISPENSE_QTY", parmDetail.getDouble("TOTQTY",i));
				order.setItemRe(row, "DOSAGE_QTY",parmDetail.getDouble("TOTQTY",i) );
			}
			t.setItem(selRow, "OWN_PRICE", allOwnAmt);
			t.setItem(selRow, "TOT_AMT",allTotAmt*dosage_qty*-1);
			order.setItemRe(selRow, "BILL_FLG", "Y");
			order.setItemRe(selRow, "ORDER_CHN_DESC", null==parm.getValue("ORDER_CHN_DESC")||
					parm.getValue("ORDER_CHN_DESC").length()<=0?parm.getValue("ORDER_DESC"):parm.getValue("ORDER_CHN_DESC"));
			t.setItem(selRow, "INCLUDE_FLG", parm.getValue("INCLUDE_FLG"));	
			t.setItem(selRow, "INCLUDE_EXEC_FLG", "Y");
			t.setItem(selRow, "SCHD_CODE", order.getSchdCode());
			order.setItemRe(selRow, "INCLUDE_FLG", parm.getValue("INCLUDE_FLG"));
			order.setItemRe(selRow, "INCLUDE_EXEC_FLG", "Y");
			//order.setUnFeeFlg(returnFlg);
			order.setItemRe(selRow, "TAKE_DAYS", 1);
			order.setItemRe(selRow, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE"));
			order.setItemRe(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			if(this.getValueString("EXEC_DATE").length()==0||
					this.getValue("EXEC_DATE")==null){
				t.setItem(selRow, "EXEC_DATE", SystemTool.getInstance().getDate());
			}else{
				t.setItem(selRow, "EXEC_DATE", this.getValue("EXEC_DATE"));
			}
			order.setFilter("ORDERSET_CODE ='' OR (ORDERSET_CODE !='' AND INDV_FLG='N')");
			order.filter();
			t.setDSValue();
			// �¼ӵ�����
			order.setItemRe(selRow, "MEDI_QTY", dosage_qty);
			order.setItemRe(selRow, "DOSAGE_QTY", dosage_qty);
			//order.setUnFeeFlg(false);
			onInsert(selRow);
		} else {
			// �Է�ע��
			t.setItem(selRow, "OWN_FLG", "Y");
			// �շ�ע��
			t.setItem(selRow, "BILL_FLG", "Y");
			// �Ը�����
//			t.setItem(selRow, "OWN_RATE", 1);
			t.setItem(selRow, "OWN_RATE", ownRate);  //modify by huangtt 20130922
			// ҽ������
			t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
			// ҽ�����
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// ҽ������
			t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
			//============modify by caowl 20120810 start
			//Ƶ��
			t.setItem(selRow, "FREQ_CODE", "STAT");
			t.setItem(selRow, "EXE_DEPT_CODE", execDept);
			//============modify by caowl 20120810 end
			// ҽ��ϸ����
			t.setItem(selRow, "ORDER_CAT1_CODE", parm
					.getValue("ORDER_CAT1_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
			// �Էѵ���
			t.setItem(selRow, "OWN_PRICE", ownPriceSingle);
			// ҽ������
			t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
			// Ժ�ڷ��ô���
			t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
			t.setItem(selRow, "SCHD_CODE", order.getSchdCode());
			// ��ѯ�վݷ��ô���
			TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
					parm);
			// �վݷ��ô���
			t.setItem(selRow, "REXP_CODE", hexpParm.getValue("IPD_CHARGE_CODE",
					0));
			if(this.getValueString("EXEC_DATE").length()==0||
					this.getValue("EXEC_DATE")==null){
				t.setItem(selRow, "EXEC_DATE", SystemTool.getInstance().getDate());
			}else{
				t.setItem(selRow, "EXEC_DATE", this.getValue("EXEC_DATE"));
			}
			
			order.setActive(selRow, true);
			t.setItem(selRow, "INCLUDE_FLG", parm.getValue("INCLUDE_FLG"));	
			t.setItem(selRow, "INCLUDE_EXEC_FLG","Y");	
			order.setItemRe(selRow, "INCLUDE_FLG", parm.getValue("INCLUDE_FLG"));
			order.setItemRe(selRow, "INCLUDE_EXEC_FLG", "Y");
			order.setItemRe(selRow, "SCHD_CODE", order.getSchdCode());
			order.setItemRe(selRow, "TAKE_DAYS", 1);
			order.setItemRe(selRow, "MEDI_QTY", dosage_qty);
			order.setItemRe(selRow, "DOSAGE_QTY", dosage_qty);
			order.setItemRe(selRow, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE"));
			order.setItemRe(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			order.setItemRe(selRow, "ORDER_CHN_DESC", null==parm.getValue("ORDER_CHN_DESC")||
					parm.getValue("ORDER_CHN_DESC").length()<=0?parm.getValue("ORDER_DESC"):parm.getValue("ORDER_CHN_DESC"));
			t.setItem(selRow, "INDV_FLG", "N");
			order.setItemRe(selRow, "INV_CODE", parm.getValue("INV_CODE"));
			t.setItem(selRow, "TOT_AMT",dosage_qty*ownPriceSingle*ownRate);
			onInsert(selRow);
		}
		onLockCellRow();
		setIncludeColor();
	}
	/**
	 * �����ײͻش�����
	 * 
	 * @param parm
	 *            TParm
	 * @param dosage_qty
	 *            double
	 */
	private void insertNewOperationOrder(TParm parm, double dosage_qty) {
		// System.out.println("parm---"+parm)
		// System.out.println("dosage_qty---"+dosage_qty);
		
		t.acceptText();
		int selRow = t.getRowCount() - 1;
		
		double ownPriceSingle = 0.00;
		String execDept="";
		if ("2".equals(serviceLevel)) {
			ownPriceSingle = parm.getDouble("OWN_PRICE2");
		} else if ("3".equals(serviceLevel)) {
			ownPriceSingle = parm.getDouble("OWN_PRICE3");
		} else
			ownPriceSingle = parm.getDouble("OWN_PRICE");
		execDept = getValue("EXE_DEPT_CODE").toString();//modify by caowl 20120810	
		OrderCode = parm.getValue("ORDER_CODE");  //add by huangtt 20130922
		if(null!=lumpworkCode&&lumpworkCode.length()>0){//�ײͻ��߼ƷѸ�����Ժ�ǼǼ�����ۿ۲���
			 if(lumpworkRate<=0){
				 this.messageBox("�ײͲ����ۿ۴�������,�����Բ���");
				 return;
			 }
			TParm sysFeeParm=SYSFeeTool.getInstance().getFeeAllData(parm.getValue("ORDER_CODE"));
			if(sysFeeParm.getCount()<=0){
				 this.messageBox("δ�ҵ���ǰ����ҽ��");
				 return;
			}
			//ҩƷ��Ѫ�Ѹ�������ۿۼ���(�ײ���)
			if(null!=sysFeeParm.getValue("CAT1_TYPE",0) &&sysFeeParm.getValue("CAT1_TYPE",0).equals("PHA")||
					null!=sysFeeParm.getValue("CHARGE_HOSP_CODE",0) && sysFeeParm.getValue("CHARGE_HOSP_CODE",0).equals("RA")){
				 ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,OrderCode,serviceLevel);
			}else{
				 ownRate=lumpworkRate;//�ײͻ��߸����ײ��ۿۼ���(�ײ���)
				 ownPriceSingle=IBSTool.getInstance().getLumpOrderOwnPrice(caseNoNew, lumpworkCode, OrderCode, serviceLevel);
			}
		 }else{
			 ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,OrderCode,serviceLevel);  //add by huangtt 20130922
		 }
		//ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,OrderCode,serviceLevel);  //add by huangtt 20130922
		order.setOwnRate(ownRate); //add by huangtt 20131114	

		// ״̬��
		callFunction("UI|setSysStatus", parm.getValue("ORDER_CODE")
				+ parm.getValue("ORDER_DESC") + parm.getValue("GOODS_DESC")
				+ parm.getValue("DESCRIPTION") + parm.getValue("SPECIFICATION"));

//		if(returnFlg){//pangben 2015-8-20 ��Ӵ����˷�ҽ��������ǰ�й���
//			for (int i = 0; i < t.getParmValue().getCount(); i++) {
//				if (t.getParmValue().getDouble("DOSAGE_QTY",i)<0) {
//					t.setLockCellRow(i, true);
//				}/
//			}
//		}
		t.setSelectedRow(selRow);   //add by huangtt 20130922
		//======pangben 2015-7-16 ����ײ���ҽ����ʾ
		if (IBSTool.getInstance().getLumpworkOrderStatus(this.caseNo, parm.getValue("ORDER_CODE"))) {
			//this.messageBox(parm.getValue("ORDER_DESC")+"ҽ��Ϊ�ײ���ҽ��");
			//t.setRowColor(selRow, Color.YELLOW);//===pangben 2015-11-2 �����ɫ
			colorParm.addData("ORDER_DESC",parm.getValue("ORDER_DESC"));
		}
		t.setItem(selRow, "DS_FLG", "N");
		// �ж��Ƿ��Ǽ���ҽ��
		if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
			TParm hexpParm =null;
			TParm parmDetail=null;
			// ��ѯ�վݷ��ô���
			hexpParm= SYSChargeHospCodeTool.getInstance().selectalldata(
					parm);
			if(null!=lumpworkCode&&lumpworkCode.length()>0){//�ײͻ��߼ƷѸ�����Ժ�ǼǼ�����ۿ۲���
				//��ѯ�ײ����Ƿ���ڴ˼���ҽ��
				parmDetail =MEMTool.getInstance().getLumpWorkOrderSetParm(caseNo, lumpworkCode, parm.getValue("ORDER_CODE"));
				if(parmDetail.getErrCode()<0||parmDetail.getCount()<=0){//�ײ��в����ڴ�ҽ��
					parmDetail = SYSOrderSetDetailTool.getInstance().selectByOrderSetCode(parm.getValue("ORDER_CODE"));
				}
			}else{
				parmDetail = SYSOrderSetDetailTool.getInstance().selectByOrderSetCode(parm.getValue("ORDER_CODE"));
			}
			// �վݷ��ô���
			t.setItem(selRow, "REXP_CODE", hexpParm.getValue("IPD_CHARGE_CODE",
					0));
			// �Է�ע��
			t.setItem(selRow, "OWN_FLG", "Y");
			// �շ�ע��
			t.setItem(selRow, "BILL_FLG", "Y");
			// �Ը�����
//			t.setItem(selRow, "OWN_RATE", 1);
			t.setItem(selRow, "OWN_RATE", ownRate); //modify by huangtt 20130922

			// ҽ������
			t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
			// ҽ�����
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// ҽ������
			t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
			// ҽ��ϸ����
			t.setItem(selRow, "ORDER_CAT1_CODE", parm
					.getValue("ORDER_CAT1_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
			// ҽ������
			t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
			// Ժ�ڷ��ô���
			t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
			// ҽ�����
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// ����ҽ�����
//			t.setItem(selRow, "ORDERSET_GROUP_NO", parm
//					.getInt("ORDERSET_GROUP_NO"));
			t.setItem(selRow, "EXE_DEPT_CODE", execDept);
			String orderCode = parm.getValue("ORDER_CODE");
			t.setItem(selRow, "ORDERSET_CODE", orderCode);
			t.setItem(selRow, "INDV_FLG", "N");
			t.setItem(selRow, "MEDI_QTY", dosage_qty);
			t.setItem(selRow, "TAKE_DAYS", 1);
			t.setItem(selRow, "DOSAGE_QTY", dosage_qty);
			//Ƶ��
			t.setItem(selRow, "FREQ_CODE", "STAT");//============modify by caowl 20120810
			order.setActive(selRow, true);
			
			if (parmDetail.getErrCode() != 0) {
				this.messageBox("ȡ��ϸ�����ݴ���");
				return;
			}
			// ����ҽ�����
			TParm groupNoParm = this.seleMaxOrdersetGroupNo(caseNo);
			groupNo = groupNoParm.getInt("ORDERSET_GROUP_NO", 0);
			t.setItem(selRow, "ORDERSET_GROUP_NO", groupNo);
			double allOwnAmt = 0.00;
			for (int i = 0; i < parmDetail.getCount(); i++) {
				int row =order.insertRow();
				// �Է�ע��
				order.setItem(row, "OWN_FLG", "Y");
				// ҽ������
				order.setItem(row, "ORDER_DESC", parmDetail.getValue(
						"ORDER_CODE", i));
				// ҽ�����
				order.setItem(row, "CAT1_TYPE", parmDetail.getValue(
						"CAT1_TYPE", i));
				// ҽ��ϸ����
				order.setItem(row, "ORDER_CAT1_CODE", parmDetail.getValue(
						"ORDER_CAT1_CODE", i));
				// ҽ������
				order.setItem(row, "ORDER_CHN_DESC", parmDetail.getValue(
						"ORDER_DESC", i));
				order.setItem(row, "BILL_FLG", "Y");
				order.setItem(row, "FREQ_CODE",  "STAT"); 
				
				// �Էѵ���
				if ("2".equals(serviceLevel)) {
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE2",i));
				} else if ("3".equals(serviceLevel)) {
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE3",i));
				} else
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE",i));
				// �վݷ��ô���
				order.setItem(row, "REXP_CODE", hexpParm.getValue(
						"IPD_CHARGE_CODE", 0));
				
				// ҽ������
				order.setItem(row, "NHI_PRICE", parmDetail
						.getDouble("NHI_PRICE",i));
				// Ժ�ڷ��ô���
				order.setItem(row, "HEXP_CODE", parmDetail.getValue(
						"CHARGE_HOSP_CODE", i));
				// ����ҽ�����
				order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
				// //��ѯ�վݷ��ô���
				// hexpParm =
				// SYSChargeHospCodeTool.getInstance().selectalldata(parmDetail);


				order.setItem(row, "OPTITEM_CODE", parmDetail.getValue(
						"OPTITEM_CODE", i));
				order.setItem(row, "EXE_DEPT_CODE", execDept);
				order.setItem(row, "INSPAY_TYPE", parmDetail.getValue(
						"INSPAY_TYPE", i));
				order.setItem(row, "RPTTYPE_CODE", parmDetail.getValue(
						"RPTTYPE_CODE", i));
				order.setItem(row, "DEGREE_CODE", parmDetail.getValue(
						"DEGREE_CODE", i));
				order.setItem(row, "CHARGE_HOSP_CODE", parmDetail.getValue(
						"CHARGE_HOSP_CODE", i));
				order.setItem(row, "INDV_FLG", "Y");
				order.setItem(row, "ORDERSET_CODE", orderCode);
				order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
				double ownPrice = 0.00;
				if ("2".equals(serviceLevel)) {
					ownPrice = parmDetail.getDouble("OWN_PRICE2",i);
				} else if ("3".equals(serviceLevel)) {
					ownPrice = parmDetail.getDouble("OWN_PRICE3",i);
				} else
					ownPrice = parmDetail.getDouble("OWN_PRICE",i);
				order.setItem(row, "OWN_PRICE", ownPrice);
				order.setItem(row, "INCLUDE_FLG","N");
				order.setItem(row, "INCLUDE_EXEC_FLG", "N");
				order.setItem(row, "TAKE_DAYS", 1);
				allOwnAmt+= ownPrice*parmDetail.getDouble("TOTQTY",i);
				
				// double qty = parmDetail.getDouble("DOSAGE_QTY", i);
				
				// ��ҩ��λ
				order.setItem(row, "MEDI_UNIT", parmDetail.getValue(
						"UNIT_CODE", i));
				order.setItem(row, "SCHD_CODE", order.getSchdCode());
				// ��ҩ��λ
				order.setItem(row, "DOSAGE_UNIT", parmDetail.getValue(
						"UNIT_CODE", i));
				order.setItem(row, "DISPENSE_UNIT", parmDetail.getValue(
						"UNIT_CODE", i));
				order.setActive(row, true);
				if(this.getValueString("EXEC_DATE").length()==0||
						this.getValue("EXEC_DATE")==null){
					order.setItem(row, "EXEC_DATE", SystemTool.getInstance().getDate());
				}else{
					order.setItem(row, "EXEC_DATE",  this.getValue("EXEC_DATE"));
				}
				order.setItem(row, "MEDI_QTY", parmDetail.getDouble("TOTQTY",i));
				order.setItem(row, "DISPENSE_QTY", parmDetail.getDouble("TOTQTY",i));
				order.setItem(row, "DOSAGE_QTY",parmDetail.getDouble("TOTQTY",i) );
			}
			// �Էѵ���
			t.setItem(selRow, "OWN_PRICE", allOwnAmt);
			t.setItem(selRow, "TOT_AMT", allOwnAmt*ownRate);//modify by huangtt 20130922
			order.setItem(selRow, "BILL_FLG", "Y");
			order.setItem(selRow, "ORDER_CHN_DESC", null==parm.getValue("ORDER_CHN_DESC")||
					parm.getValue("ORDER_CHN_DESC").length()<=0?parm.getValue("ORDER_DESC"):parm.getValue("ORDER_CHN_DESC"));
			t.setItem(selRow, "INCLUDE_FLG", "N");	
			t.setItem(selRow, "INCLUDE_EXEC_FLG", "N");
			order.setItem(selRow, "INCLUDE_FLG","N");
			order.setItem(selRow, "INCLUDE_EXEC_FLG", "N");
			order.setItem(selRow, "TAKE_DAYS", 1);
			order.setItem(selRow, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE"));
			order.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			t.setItem(selRow, "SCHD_CODE", order.getSchdCode());
			if(this.getValueString("EXEC_DATE").length()==0||
					this.getValue("EXEC_DATE")==null){
				t.setItem(selRow, "EXEC_DATE", SystemTool.getInstance().getDate());
			}else{
				t.setItem(selRow, "EXEC_DATE", this.getValue("EXEC_DATE"));
			}
			
			order.setFilter("ORDERSET_CODE ='' OR (ORDERSET_CODE !='' AND INDV_FLG='N')");
			order.filter();
			t.setDSValue();
			// �¼ӵ�����
			order.setItem(selRow, "MEDI_QTY", dosage_qty);
			order.setItem(selRow, "DOSAGE_QTY", dosage_qty);
			onInsert(selRow);
		} else {
			// �Է�ע��
			t.setItem(selRow, "OWN_FLG", "Y");
			// �շ�ע��
			t.setItem(selRow, "BILL_FLG", "Y");
			// �Ը�����
//			t.setItem(selRow, "OWN_RATE", 1);
			t.setItem(selRow, "OWN_RATE", ownRate);  //modify by huangtt 20130922
			// ҽ������
			t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
			// ҽ�����
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// ҽ������
			t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
			//============modify by caowl 20120810 start
			//Ƶ��
			t.setItem(selRow, "FREQ_CODE", "STAT");
			t.setItem(selRow, "EXE_DEPT_CODE", execDept);
			//============modify by caowl 20120810 end
			// ҽ��ϸ����
			t.setItem(selRow, "ORDER_CAT1_CODE", parm
					.getValue("ORDER_CAT1_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
			// �Էѵ���
			t.setItem(selRow, "OWN_PRICE", ownPriceSingle);
			// ҽ������
			t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
			// Ժ�ڷ��ô���
			t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
			// ��ѯ�վݷ��ô���
			TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
					parm);
			// �վݷ��ô���
			t.setItem(selRow, "REXP_CODE", hexpParm.getValue("IPD_CHARGE_CODE",
					0));
			if(this.getValueString("EXEC_DATE").length()==0||
					this.getValue("EXEC_DATE")==null){
				t.setItem(selRow, "EXEC_DATE", SystemTool.getInstance().getDate());
			}else{
				t.setItem(selRow, "EXEC_DATE", this.getValue("EXEC_DATE"));
			}
			t.setItem(selRow, "SCHD_CODE", order.getSchdCode());
			order.setActive(selRow, true);
			t.setItem(selRow, "INCLUDE_FLG","N");	
			t.setItem(selRow, "INCLUDE_EXEC_FLG","N");
			order.setItem(selRow, "INCLUDE_FLG", "N");
			order.setItem(selRow, "INCLUDE_EXEC_FLG", "N");
			order.setItem(selRow, "TAKE_DAYS", 1);
			order.setItem(selRow, "MEDI_QTY", dosage_qty);
			order.setItem(selRow, "DOSAGE_QTY", dosage_qty);
			order.setItem(selRow, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE"));
			order.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			order.setItem(selRow, "SCHD_CODE", order.getSchdCode());
			order.setItem(selRow, "ORDER_CHN_DESC", null==parm.getValue("ORDER_CHN_DESC")||
					parm.getValue("ORDER_CHN_DESC").length()<=0?parm.getValue("ORDER_DESC"):parm.getValue("ORDER_CHN_DESC"));
			t.setItem(selRow, "INDV_FLG", "N");
			order.setItem(selRow, "INV_CODE", parm.getValue("INV_CODE"));
			t.setItem(selRow, "TOT_AMT",dosage_qty*ownPriceSingle*ownRate);
			onInsert(selRow);
		}
//		if(returnFlg){//pangben 2015-8-20 ��Ӵ����˷�ҽ��������ǰ�й���
//			onLockCellRow();
//		}
		onLockCellRow();
		setIncludeColor();
	}
	private void onLockCellRow(){
		for (int i = 0; i < t.getRowCount(); i++) {
			if (null!=t.getItemData(i,"DOSAGE_QTY") &&t.getItemDouble(i,"DOSAGE_QTY")<0) {
				t.setLockCellRow(i, true);
			}else{
				t.setLockCellRow(i, false);
			}
		}
	}
	/**
	 * ���ò�ѯ
	 */
	public void onSelFee() {
		TParm parm = new TParm();
		parm.setData("IBS", "CASE_NO", caseNo);
		parm.setData("IBS", "MR_NO", mrNo);
		parm.setData("IBS", "TYPE", "IBS");
		this.openWindow("%ROOT%\\config\\ibs\\IBSSelOrderm.x", parm);
	}

	// ��table����һ��ҽ����
	public void onInsert(int selRow) {
		// �鿴�Ƿ���δ�༭��
		if (isNewRow()) {
			return;
		}
		order.insertRow();
		t.setDSValue();
		t.getTable().grabFocus();
		t.setSelectedRow(selRow);
		t.setSelectedColumn(1);
	}

	/**
	 * �Ƿ���δ�༭��
	 * 
	 * @return boolean
	 */
	public boolean isNewRow() {
		boolean falg = false;
		int rowCount = order.rowCount();
		for (int i = 0; i < rowCount; i++) {
			if (!order.isActive(i)) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	/**
	 * ��ʿ�ײ�
	 */
	public void onNbwPackage() {
		// ��ʿ�ײ�
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "IBS");
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("USER_ID", Operator.getID());
		parm.setData("DEPT_OR_DR", 4);
		parm.setData("RULE_TYPE", 4);
		parm.addListener("INSERT_TABLE", this, "onQuoteSheetList");
		TWindow window = (TWindow) this.openDialog(
				"%ROOT%\\config\\odi\\ODIPACKOrderUI.x", parm, true);
		window.setVisible(true);
	}

	/**
	 * �����ײ�
	 */
	public void onDeptPackage() {
		// �����ײ�
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "IBS");
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("USER_ID", Operator.getID());
		parm.setData("DEPT_OR_DR", 3);
		parm.setData("RULE_TYPE", 4);
		parm.addListener("INSERT_TABLE", this, "onQuoteSheetList");
		TWindow window = (TWindow) this.openDialog(
				"%ROOT%\\config\\odi\\ODIPACKOrderUI.x", parm, true);
		window.setVisible(true);

	}

	/**
	 * �ײ͸�ֵ
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onQuoteSheetList(Object obj) {
		boolean falg = true;
		if (obj != null) {
			List orderList = (ArrayList) obj;
			Iterator iter = orderList.iterator();
			while (iter.hasNext()) {
				TParm temp = (TParm) iter.next();
				// System.out.println("�ײͲ���"+temp);
				insertNewOperationOrder(temp, temp.getDouble("MEDI_QTY"));
			}
		}
		return falg;
	}

	/**
	 * ���¼����ܼ�
	 */
	public void setTotAmt() {
		int countTable = t.getRowCount();
		double payAmt = 0.0D;
		for (int i = 0; i < countTable; i++)
			//modify by wanglong 20120814 �޸��ܼ۵ļ��㷽ʽ���ܼ۵���ÿ��ҽ���ĵ��۱���С������λ�����������������������������Ľ��壩
			//payAmt += t.getItemDouble(i, "TOT_AMT");
		    payAmt += StringTool.round(t.getItemDouble(i, "TOT_AMT"), 2);
		setValue("OWN_AMT", Double.valueOf(payAmt));
	}

	/**
	 * ����·�� ===============pangben 2012-7-9
	 */
	public void onAddCLNCPath() {
		TParm inParm = new TParm();
		// =======�Ӡ�2012-06-04 ��õ�ǰʱ��
		StringBuffer sqlbf = new StringBuffer();
//		sqlbf
//				.append("SELECT SCHD_CODE FROM CLP_THRPYSCHDM_REAL WHERE CLNCPATH_CODE= '"
//						+ clncpathCode + "'");
//		sqlbf.append(" AND CASE_NO='" + caseNo + "' ");
//		sqlbf.append(" AND REGION_CODE='" + Operator.getRegion() + "' ");
//		sqlbf.append(" AND SYSDATE BETWEEN START_DATE AND END_DATE");
//		sqlbf.append(" ORDER BY SEQ ");
		 sqlbf.append("SELECT SCHD_CODE FROM ADM_INP WHERE CASE_NO= '"
	                + caseNo + "'");
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sqlbf.toString()));
		// �õ���ǰʱ��
		String schdCode = "";
		if (result.getCount("SCHD_CODE") > 0) {
			schdCode = result.getValue("SCHD_CODE", 0);
		}
		inParm.setData("CLNCPATH_CODE", clncpathCode);
		inParm.setData("SCHD_CODE", schdCode);
		inParm.setData("CASE_NO", caseNo);
		inParm.setData("IND_FLG", "Y");
		inParm.setData("IND_CLP_FLG", true);//סԺ�Ƽ�ע��-xiongwg20150429
		result = (TParm) this.openDialog(
				"%ROOT%\\config\\clp\\CLPTemplateOrderQuote.x", inParm);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		int rowCount = result.getCount("ORDER_CODE");
		for (int i = 0; i < rowCount; i++) {
			//������������ʾ-xiongwg20150429
			TParm OrderParm = OdiUtil.getInstance().getSysFeeAndclp(
                    result.getValue("ORDER_CODE", i),result.getValue("CLNCPATH_CODE", i),
                    result.getValue("SCHD_CODE", i),result.getValue("CHKTYPE_CODE", i),
                    result.getValue("ORDER_SEQ_NO", i),result.getValue("ORDER_TYPE", i));
			newOrderTemp(OrderParm,
					getExitRow());
			// this.popReturn(tag, OrderParm);
		}
	}
	/**
	 * ����·������
	 * yanjing 20140919
	 */
    public void onChangeSchd(){
    	String sql = "SELECT CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"' AND CLNCPATH_CODE IS NOT NULL ";//��ѯ�û����Ƿ�����ٴ�·��
    	TParm parm = new TParm (TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()>0){//�����ٴ�·��
    		String clncPathCode = parm.getValue("CLNCPATH_CODE", 0);
    		//���ø���ʱ�̵Ľ���
    		TParm sendParm = new TParm();
            sendParm.setData("CASE_NO", caseNo); 
            sendParm.setData("CLNCPATH_CODE", clncPathCode);
            TParm result = (TParm) this.openDialog(
                    "%ROOT%\\config\\odi\\ODIintoDuration.x", sendParm);
            String schdCodeSql="SELECT SCHD_CODE FROM ADM_INP WHERE CASE_NO= '"+ caseNo + "' ";
            TParm schdParm = new TParm (TJDODBTool.getInstance().select(schdCodeSql));
            this.setValue("SCHD_CODE", schdParm.getValue("SCHD_CODE", 0));
            //���ö����е�schd_codeֵ
//            order.setSchdCode(schdParm.getValue("SCHD_CODE", 0));
            onSetSchdCode();
    	}else{
    		this.messageBox("�������ٴ�·�������ɸ���ʱ�̡�");
    		return;
    	}
    	
    }
    /**
	 * ����dateStore��schd_code������
	 */
	public void onSetSchdCode(){
		order.setSchdCode(this.getValueString("SCHD_CODE"));
		schdCode = order.getSchdCode();
//		System.out.println("--------ʱ����� is����"+order.getSchdCode());
	}
    /**
     * ����ʱ���޸�================xiongwg 2015-4-26
     */
	public void onClpOrderReSchdCode() {
		String sql = "SELECT CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"
				+ caseNo + "' AND CLNCPATH_CODE IS NOT NULL ";// ��ѯ�û����Ƿ�����ٴ�·��
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {// �����ٴ�·��
			parm = new TParm();
			parm.setData("CLP", "CASE_NO", caseNo);
			parm.setData("CLP", "MR_NO", mrNo);
			parm.setData("CLP", "FLG", "Y");
			TParm result = (TParm) this.openDialog(
					"%ROOT%\\config\\clp\\CLPOrderReplaceSchdCode.x", parm);
		} else {
			this.messageBox("�������ٴ�·�������ɸ���ʱ�̡�");
			return;
		}

	}
	/**
	 * �õ���ǰ�ɱ༭��
	 * 
	 * @return int
	 */
	public int getExitRow() {
		TTable table = this.getTTable("MAINTABLE");
		int rowCount = table.getDataStore().rowCount();
		int rowOnly = -1;
		for (int i = 0; i < rowCount; i++) {
			if (!table.getDataStore().isActive(i)) {
				rowOnly = i;
				break;
			}
		}
		return rowOnly;
	}
	/**
	 * ����Ƽ��˷�
	 * add caoy
	 * 2014/5/12
	 */
	public void onRreturn(){
		double sumNum=0;
		int count=0;
		boolean flag=false;
		TParm parm = new TParm();
        parm.setData("MR_NO", mrNo);
        parm.setData("CASE_NO",caseNo);
        parm.setData("ADM_DATE",inDate);
		Object result = openDialog("%ROOT%\\config\\ibs\\IBSReturn.x",parm);
		 TParm resultParm = (TParm) result;
		 TTable table=(TTable) this.getComponent(TABLE);
		if (result != null) {
			if(resultParm.getCount("RETURN_SUM")>0){//���ܴ���ֵ
				count=resultParm.getCount("RETURN_SUM");
				flag=true;
			}else{//��ϸ����ֵ
				count=resultParm.getCount("ORDER_CODE");
			}
           TParm row=null;
            for (int i = 0; i < count; i++) {
            	 table.setLockCellRow(i, false);
            	 row=resultParm.getRow(i);
            	 row.setData("ORDER_CHN_DESC",row.getValue("ORDER_DESC"));
            	 insertNewOrderReturn(row,flag);
            }
       }
	}
	/**
	 * 
	* @Title: insertNewOrderReturn
	* @Description: TODO(�˷Ѵ��ز���)
	* @author pangben 2015-7-17
	* @throws
	 */
	private void insertNewOrderReturn(TParm parm,boolean flg){
		if (flg) {//���ܲ���
			if (parm.getValue("CAT_FLG").equals("Y")) {//����ҽ��
				String sql="";
				sql="SELECT A.CASE_NO, A.CASE_NO_SEQ, A.SEQ_NO, A.BILL_DATE, A.ORDER_NO, A.ORDER_SEQ, "+
				"A.ORDER_CODE, A.ORDER_CAT1_CODE, A.CAT1_TYPE, "+
				"A.ORDERSET_GROUP_NO, A.ORDERSET_CODE, A.INDV_FLG, A.DEPT_CODE, A.STATION_CODE, A.DR_CODE, "+
				"A.EXE_DEPT_CODE, A.EXE_STATION_CODE, A.EXE_DR_CODE, A.MEDI_QTY, A.MEDI_UNIT, A.DOSE_CODE, "+
				"A.FREQ_CODE, A.TAKE_DAYS, A.DOSAGE_QTY, A.DOSAGE_UNIT, A.OWN_PRICE, A.NHI_PRICE, "+
				"A.TOT_AMT, A.OWN_FLG, A.BILL_FLG, A.REXP_CODE, A.BILL_NO, A.HEXP_CODE, "+
				"A.BEGIN_DATE, A.END_DATE, A.OWN_AMT, A.OWN_RATE, A.REQUEST_FLG, A.REQUEST_NO, "+
				"A.INV_CODE, A.OPT_USER, A.OPT_DATE, A.OPT_TERM, A.COST_AMT, A.ORDER_CHN_DESC, "+
				"A.COST_CENTER_CODE, A.SCHD_CODE, A.CLNCPATH_CODE, A.DS_FLG, A.KN_FLG, A.INCLUDE_FLG, "+
				"A.BIL_FINANCE_FLG, A.EXEC_DATE,B.ORDERSET_FLG,B.CHARGE_HOSP_CODE,B.SPECIFICATION DESCRIPTION ," +
		"A.DOSAGE_UNIT AS UNIT_CODE,B.ORDER_DESC FROM IBS_ORDD A,SYS_FEE B WHERE  A.ORDER_CODE=B.ORDER_CODE(+) AND A.CASE_NO='"+caseNo+"' AND (A.INDV_FLG = 'N' OR A.INDV_FLG IS NULL) AND" +
				" A.ORDERSET_CODE='"+parm.getValue("ORDER_CODE")+"' AND A.ORDERSET_GROUP_NO='"+parm.getValue("ORDERSET_GROUP_NO")+
				"' AND INCLUDE_FLG='"+parm.getValue("INCLUDE_FLG")+"' ";
//				+parm.getValue("INCLUDE_FLG")+"'
//				double totAmt=parm.getDouble("OWN_PRICE");//*parm.getDouble("RETURN_SUM");
//				String checkSql="SELECT DOSAGE_QTY,ORDER_CODE FROM IBS_ORDD A WHERE CASE_NO='"+caseNo+
//				"' AND  (A.INDV_FLG = 'N' OR A.INDV_FLG IS NULL) AND ORDERSET_CODE='"+parm.getValue("ORDER_CODE")+"' AND INCLUDE_FLG='"
//				+parm.getValue("INCLUDE_FLG")+"' AND A.OWN_RATE= "+parm.getDouble("OWN_RATE")+" AND DOSAGE_QTY>1";
//				//��������ҽ����������1
//				TParm checkQtyParm = new TParm(TJDODBTool.getInstance().select(checkSql));
//				if (checkQtyParm.getCount()>0) {
//					sql="SELECT A.CASE_NO, A.CASE_NO_SEQ, A.SEQ_NO, A.BILL_DATE, A.ORDER_NO, A.ORDER_SEQ, "+
//							"A.ORDER_CODE, A.ORDER_CAT1_CODE, A.CAT1_TYPE, "+
//							"A.ORDERSET_GROUP_NO, A.ORDERSET_CODE, A.INDV_FLG, A.DEPT_CODE, A.STATION_CODE, A.DR_CODE, "+
//							"A.EXE_DEPT_CODE, A.EXE_STATION_CODE, A.EXE_DR_CODE, A.MEDI_QTY, A.MEDI_UNIT, A.DOSE_CODE, "+
//							"A.FREQ_CODE, A.TAKE_DAYS, A.DOSAGE_QTY, A.DOSAGE_UNIT, A.OWN_PRICE, A.NHI_PRICE, "+
//							"A.TOT_AMT, A.OWN_FLG, A.BILL_FLG, A.REXP_CODE, A.BILL_NO, A.HEXP_CODE, "+
//							"A.BEGIN_DATE, A.END_DATE, A.OWN_AMT, A.OWN_RATE, A.REQUEST_FLG, A.REQUEST_NO, "+
//							"A.INV_CODE, A.OPT_USER, A.OPT_DATE, A.OPT_TERM, A.COST_AMT, A.ORDER_CHN_DESC, "+
//							"A.COST_CENTER_CODE, A.SCHD_CODE, A.CLNCPATH_CODE, A.DS_FLG, A.KN_FLG, A.INCLUDE_FLG, "+
//							"A.BIL_FINANCE_FLG, A.EXEC_DATE,B.ORDERSET_FLG,B.CHARGE_HOSP_CODE,B.SPECIFICATION DESCRIPTION ," +
//					"A.DOSAGE_UNIT AS UNIT_CODE,B.ORDER_DESC FROM IBS_ORDD A,SYS_FEE B," +
//					"(SELECT A.CASE_NO,A.CASE_NO_SEQ,A.ORDERSET_GROUP_NO FROM (SELECT CASE_NO,CASE_NO_SEQ,A.ORDERSET_GROUP_NO," +
//					"DOSAGE_QTY FROM IBS_ORDD A  WHERE CASE_NO='"+caseNo+"' AND A.ORDERSET_CODE='"+parm.getValue("ORDER_CODE")+"' "+
//					" AND A.INDV_FLG = 'Y' AND A.OWN_RATE= "+parm.getDouble("OWN_RATE")+
//					" AND A.INCLUDE_FLG='"+parm.getValue("INCLUDE_FLG")+"' GROUP BY CASE_NO,CASE_NO_SEQ,A.ORDERSET_GROUP_NO," +
//					"DOSAGE_QTY) A,(SELECT CASE_NO,CASE_NO_SEQ,A.ORDERSET_GROUP_NO," +
//					"SUM(A.OWN_AMT) TOT_AMT FROM IBS_ORDD A WHERE CASE_NO='"+caseNo+"' AND A.ORDERSET_CODE='"+parm.getValue("ORDER_CODE")+"' "+
//					" AND A.INDV_FLG = 'Y' AND A.OWN_RATE= "+parm.getDouble("OWN_RATE")+
//					" AND A.INCLUDE_FLG='"+parm.getValue("INCLUDE_FLG")+"' GROUP BY CASE_NO,CASE_NO_SEQ,A.ORDERSET_GROUP_NO) B WHERE A.CASE_NO=B.CASE_NO AND A.CASE_NO_SEQ=B.CASE_NO_SEQ" +
//					" AND A.ORDERSET_GROUP_NO=B.ORDERSET_GROUP_NO AND B.TOT_AMT/A.DOSAGE_QTY = "+totAmt+") C WHERE A.CASE_NO=C.CASE_NO " +
//								"AND A.ORDERSET_GROUP_NO=C.ORDERSET_GROUP_NO AND A.CASE_NO_SEQ=C.CASE_NO_SEQ AND A.ORDER_CODE=B.ORDER_CODE " +
//								"AND  A.CASE_NO='"+caseNo+"' AND A.ORDERSET_CODE='"+parm.getValue("ORDER_CODE")+"' "+
//					" AND (A.INDV_FLG = 'N' OR A.INDV_FLG IS NULL) AND A.OWN_RATE= "+parm.getDouble("OWN_RATE")+
//					" AND A.INCLUDE_FLG='"
//					+parm.getValue("INCLUDE_FLG")+"'";
//				}else{
//					sql=" SELECT A.*,B.ORDERSET_FLG,B.CHARGE_HOSP_CODE,B.SPECIFICATION DESCRIPTION ," +
//					"A.DOSAGE_UNIT AS UNIT_CODE,B.ORDER_DESC FROM IBS_ORDD A,SYS_FEE B ," +
//					"(SELECT A.ORDERSET_GROUP_NO,SUM(A.OWN_AMT)/"+parm.getDouble("DOSAGE_QTY")+" TOT_AMT,A.ORDERSET_CODE,A.CASE_NO FROM IBS_ORDD A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE(+) " +
//					" AND CASE_NO='"+caseNo+"' AND A.ORDERSET_CODE='"+parm.getValue("ORDER_CODE")+"' "+
//					" AND A.INDV_FLG = 'Y' AND A.OWN_RATE= "+parm.getDouble("OWN_RATE")+
//					" AND A.INCLUDE_FLG='"
//						+parm.getValue("INCLUDE_FLG")+"' GROUP BY  A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,A.CASE_NO ) C WHERE A.CASE_NO=C.CASE_NO " +
//								"AND A.ORDERSET_GROUP_NO=C.ORDERSET_GROUP_NO AND A.ORDER_CODE=B.ORDER_CODE AND " +
//								"A.ORDER_CODE=C.ORDERSET_CODE AND  A.CASE_NO='"+caseNo+"' AND A.ORDERSET_CODE='"+parm.getValue("ORDER_CODE")+"' "+
//					" AND (A.INDV_FLG = 'N' OR A.INDV_FLG IS NULL) AND A.OWN_RATE= "+parm.getDouble("OWN_RATE")+
//					" AND A.INCLUDE_FLG='"
//					+parm.getValue("INCLUDE_FLG")+"' AND C.TOT_AMT="+totAmt;
//				}
				
				TParm result=new TParm(TJDODBTool.getInstance().select(sql));
				if (result.getErrCode()<0||result.getCount()<=0) {
					this.messageBox("������ݳ�������");
					return;
				}
				insertReOperationOrder(result.getRow(0),-parm.getDouble("RETURN_SUM"));
			} else{
				String sql=" SELECT A.*,B.ORDERSET_FLG,B.CHARGE_HOSP_CODE,B.SPECIFICATION DESCRIPTION,A.DOSAGE_UNIT" +
						" AS UNIT_CODE,B.ORDER_DESC  FROM IBS_ORDD A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE(+)  AND CASE_NO='"+caseNo+
				"' AND A.ORDER_CODE='"+parm.getValue("ORDER_CODE")+"' "+
						"AND (A.INDV_FLG = 'N' OR A.INDV_FLG IS NULL ) AND A.OWN_RATE= "+
						parm.getDouble("OWN_RATE")+" AND A.OWN_PRICE="+parm.getDouble("OWN_PRICE")+" AND A.INCLUDE_FLG='"
						+parm.getValue("INCLUDE_FLG")+"'";
				TParm result=new TParm(TJDODBTool.getInstance().select(sql));
				if (result.getErrCode()<0) {
					this.messageBox("������ݳ�������");
					return;
				}
				insertReOperationOrder(result.getRow(0),-parm.getDouble("RETURN_SUM"));
			}
		}else{
			insertReOperationOrder(parm,-parm.getDouble("DOSAGE_QTY"));
		}
	}
	  /**
     * ѡ���������뵥��������
     */
    public void onChooseOpBookSeq() {// wanglong add 20141010
        String opBookSeq = this.getValueString("OPBOOK_SEQ");
        TTextFormat OPE_STATE = (TTextFormat) this.getComponent("OPE_STATE");
        if (opBookSeq.equals("")) {
            OPE_STATE.getTablePopupMenu().setParmValue(new TParm());
            OPE_STATE.setValue(null);
            return;
        }
        String opBookSql = "SELECT TYPE_CODE FROM OPE_OPBOOK WHERE OPBOOK_SEQ = '#'";
        opBookSql = opBookSql.replaceFirst("#", opBookSeq);
        TParm result = new TParm(TJDODBTool.getInstance().select(opBookSql));
        if (result.getErrCode() < 0) {
            this.messageBox("�������״̬ʧ�� " + result.getErrText());
            return;
        }
        String opStateSql =
                "SELECT ID,CHN_DESC NAME FROM SYS_DICTIONARY WHERE GROUP_ID='#' ORDER BY ID";
        if (result.getValue("TYPE_CODE", 0).equals("1")) {// ����������
            opStateSql = opStateSql.replaceFirst("#", "OPE_STATE1");
        } else {// TYPE_CODE=2 ��������
            opStateSql = opStateSql.replaceFirst("#", "OPE_STATE2");
        }
        OPE_STATE.setPopupMenuSQL(opStateSql);
        OPE_STATE.onQuery();
    }
    
    /**
     * �ײ�ҽ��=== add by kangy
     */
    public void onReturnOrderPackage() {// add by kangy  20160829
    	
    	TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() > 3) {
			// ���ɿ�����ҽ��
			this.messageBox("E0175");
			return;
		}
		String sql=" SELECT MAX(CASE_NO) AS CASE_NO FROM ADM_INP WHERE MR_NO='"+this.getValue("MR_NO")+"'";
		TParm caseParm=new TParm(TJDODBTool.getInstance().select(sql));
		if(caseParm.getCount()<=0){
			this.messageBox("��ѯ���ξ����ʧ��");
			return;
		}
		caseNo=caseParm.getValue("CASE_NO", 0);
		//this.yyList = true;
		//У���Ƿ�Ϊ�ײͲ���
		String sqlS="SELECT LUMPWORK_CODE FROM ADM_INP WHERE CASE_NO='"+caseNo+"'";
		TParm TCparm=new TParm(TJDODBTool.getInstance().select(sqlS));
		if(TCparm.getValue("LUMPWORK_CODE",0).length()<=0){
			this.messageBox("�˲��˲����ײͲ���");
			return;
		}
		TParm parm = new TParm();
		TParm caseNoParm=new TParm();
		IBSNewTool ibs=new IBSNewTool();
		caseNoParm=ibs.onCheckLumWorkCaseNo(this.getValue("MR_NO").toString(),caseNo);
		parm.setData("CASE_NO", caseNoParm.getValue("CASE_NO"));
		parm.setData("MR_NO",caseNoParm.getValue("MR_NO"));
		parm.setData("SYSTEM_TYPE", "ODI");
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("USER_ID", Operator.getID());
		parm.setData("TYPE", "ZYJJ");//סԺ�Ƽ�
		parm.addListener("INSERT_TABLE", this, "onQuoteSheetList");
		//parm.addListener("INSERT_TABLE_FLG", this, "setYYlist");
		parm.setData("RULE_TYPE", tab.getSelectedIndex());
			TParm resultParm  =(TParm)this.openDialog(
					"%ROOT%\\config\\mem\\MEMDoctorPackage.x", parm);
			TParm result=new TParm();
			TParm resultOrder=(resultParm.getData("ORDER") instanceof TParm)?(TParm)resultParm.getData("ORDER"):new TParm();
			TParm resultChn=(resultParm.getData("CHN") instanceof TParm)?(TParm)resultParm.getData("CHN"):new TParm();
			TParm resultExa=(resultParm.getData("EXA") instanceof TParm)?(TParm)resultParm.getData("EXA"):new TParm();
			TParm resultOp=(resultParm.getData("OP") instanceof TParm)?(TParm)resultParm.getData("OP"):new TParm();
			TParm resultCtrl=(resultParm.getData("CTRL") instanceof TParm)?(TParm)resultParm.getData("CTRL"):new TParm();
			int j=0;
			 if(resultOrder.getCount("ORDER_CODE") > 0){
				 for(int i=0;i<resultOrder.getCount("ORDER_CODE");i++){
					 result.setRowData(j,resultOrder.getRow(i));
					 j++;
				 }
			 }
			 if(resultChn.getCount("ORDER_CODE") > 0){
				 for(int i=0;i<resultChn.getCount("ORDER_CODE");i++){
					 result.setRowData(j,resultChn.getRow(i));
					 j++;
				 }
			 }
			 if(resultExa.getCount("ORDER_CODE") > 0){
				 for(int i=0;i<resultExa.getCount("ORDER_CODE");i++){
					 result.setRowData(j,resultExa.getRow(i));
					 j++;
				 }
			 }
			
			 if(resultOp.getCount("ORDER_CODE") > 0){
				 for(int i=0;i<resultOp.getCount("ORDER_CODE");i++){
					 result.setRowData(j,resultOp.getRow(i));
					 j++;
				 }
			 }
			 if(resultCtrl.getCount("ORDER_CODE") > 0){
				 for(int i=0;i<resultCtrl.getCount("ORDER_CODE");i++){
					 result.setRowData(j,resultCtrl.getRow(i));
					 j++;
				 }
			 }
			 if (result == null || result.getCount("ORDER_CODE") < 1) {
					return;
				}
			for(int i=0;i<result.getCount("ORDER_CODE");i++){
			switch (tab.getSelectedIndex()) {
			case 0:
				result.setData("RX_KIND",i,"ST");
			break;	
			case 1:
				result.setData("RX_KIND",i,"UD");
				break;	
			case 2:
				result.setData("RX_KIND",i,"DS");
				break;	
			}
		
			String sq="SELECT IPD_FIT_FLG,CAT1_TYPE,ORDER_DESC,UNIT_CODE,CHARGE_HOSP_CODE FROM SYS_FEE WHERE ORDER_CODE='"+result.getData("ORDER_CODE",i)+"'";
			TParm SysFeeParm=new TParm(TJDODBTool.getInstance().select(sq));
			result.setData("IPD_FIT_FLG",i,SysFeeParm.getData("IPD_FIT_FLG",0));
			result.setData("CAT1_TYPE",i,SysFeeParm.getData("CAT1_TYPE",0));
			result.setData("ORDER_DESC",i,SysFeeParm.getData("ORDER_DESC",0));
			result.setData("ORDER_CODE",i,result.getData("ORDER_CODE",i));
			result.setData("UNIT_CODE",i,SysFeeParm.getData("UNIT_CODE",0));
			result.setData("DOSAGE_QTY",i,result.getData("MEDI_QTY",i));
			result.setData("CHARGE_HOSP_CODE",i,SysFeeParm.getData("CHARGE_HOSP_CODE",0));
			insertNewOperationOrder(result.getRow(i),result.getDouble("MEDI_QTY",i));
			}
		}
    }

	
