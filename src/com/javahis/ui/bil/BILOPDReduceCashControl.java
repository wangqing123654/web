package com.javahis.ui.bil;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.bil.PaymentCashTool;
import jdo.bil.PaymentTool;
import jdo.ekt.EKTTool;
import jdo.ekt.EKTpreDebtTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFormatEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TDialog;
/**
 * <p>
 * Title:����Ʊ�ݼ���
 * </p>
 * 
 * <p>
 * Description:����Ʊ�ݼ���:�ֽ��Ʊ����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Bluecore
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author pangben
 * @version 1.0
 */
public class BILOPDReduceCashControl extends TControl{
	private static final String actionName = "action.bil.BILOPBReduceAction";
	private TTabbedPane TabbedPane;
	private TComboBox type;
	private TTable tabled;
	private TTable tabledd;
	private TTable tablem;
	private TCheckBox alld;
	private TCheckBox alldd;
	private TParm oneReceiptParm;
	private TParm onOrderParm;
	private Map rexpMap;
	String caseNo;
	private PaymentCashTool paymentTool;
	private double commReduceAmt=0.00;
	private TNumberTextField numberTextField;
	// =================������==============
	private BILComparator compare = new BILComparator();
	/**
	 * ��ʼ��
	 * */
	public void onInit() {
		super.onInit();
		// ��ʼ���ؼ�
		initComponent();
		// ��ʼ�������¼�
		initListener();
		numberTextField = (TNumberTextField) this.getComponent("REDUCE_AMT");
		numberTextField.addFocusListener(new FocusListener(){

				@Override
				public void focusGained(FocusEvent e) {
					// TODO Auto-generated method stub
//					getRate();

				}

				@Override
				public void focusLost(FocusEvent e) {
					// TODO Auto-generated method stub
					onShare();

				}
	    		
	    	});
		TParm initParm = new TParm();
		Object obj = this.getParameter();
		if (obj == null || obj.equals("")) {
			return;
		}
		initParm=(TParm)obj;
		TParm parm=initParm.getParm("TYPE_PARM");
		TPanel p = (TPanel) getComponent("tPanel_2");
		TParm orderParm=initParm.getParm("ORDER_PARM");
		//TParm tableData = table.getParmValue();// ȡ�ñ��е�����
		String columnName[] = orderParm.getNames("Data");// �������
		String strNames = "";
		for (String tmp : columnName) {
			strNames += tmp + ";";
		}
		strNames = strNames.substring(0, strNames.length() - 1);
		Vector vct = getVector(orderParm, "Data", strNames, 0);
		
		int col = tranParmColIndex(columnName, "REXP_CODE"); // ����ת��parm�е�������
		compare.setDes(true);
		compare.setCol(col);
		java.util.Collections.sort(vct, compare);
		// ��������vectorת��parm;
		oneReceiptParm=cloneVectoryParam(vct, new TParm(), strNames);	
		col = tranParmColIndex(columnName, "ORDER_CODE"); // ����ת��parm�е�������
		compare.setDes(true);
		compare.setCol(col);
		java.util.Collections.sort(vct, compare);
		onOrderParm=cloneVectoryParam(vct, new TParm(), strNames);
		Map<String, String> map = getPayTypeMap(true);
		for (int i = 0; i <parm.getCount("PAY_TYPE"); i++) {
			parm.setData("PAY_TYPE", i,parm.getValue("PAY_TYPE", i));//����ת��
			if (parm.getValue("PAY_TYPE", i).equals("JM")) {
				commReduceAmt+=parm.getDouble("AMT", i);
			}
		}
		this.setValue("REDUCE_AMT",commReduceAmt);
		try {
			paymentTool = new PaymentCashTool(p, this,parm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		caseNo = initParm.getData("CASE_NO") + "";
		// ��ʼ��������Ϣ
		this.setValue("MR_NO", initParm.getData("MR_NO") + "");
		this.setValue("PAT_NAME", initParm.getData("PAT_NAME") + "");
		this.setValue("SEX_CODE", initParm.getData("SEX_CODE") + "");
		this.setValue("CTZ1_CODE", initParm.getData("CTZ1_CODE") + "");
		this.setValue("RECEIPT_NO", initParm.getData("RECEIPT_NO") + "");
		if (null==initParm.getValue("ADM_DATE")||initParm.getValue("ADM_DATE").length()<=5) {
		}else{
			this.setValue("ADM_DATE", initParm.getTimestamp("ADM_DATE"));
		}
		this.setValue("CASE_NO", caseNo);
		// ��ʼ���ܶ����ҳǩ
		onSelectM();
		setReduceAmt(commReduceAmt);
		onShare();
		  numberTextField = (TNumberTextField) this.getComponent("ALLFEE");
//		  numberTextField.addFocusListener(new FocusListener(){
//
//				@Override
//				public void focusGained(FocusEvent e) {
//					onShare();
//					
//				}
//
//				@Override
//				public void focusLost(FocusEvent e) {
//					onShare();
//				}
//	    		
//	    	});

	}
	private Map<String, String> getPayTypeMap(boolean flg){
		String sql =
			"SELECT * FROM BIL_GATHERTYPE_PAYTYPE";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < parm.getCount(); i++) {
			if (flg) {
				map.put(parm.getValue("PAYTYPE", i), parm.getValue("GATHER_TYPE", i));
			}else{
				map.put(parm.getValue("GATHER_TYPE", i), parm.getValue("PAYTYPE", i));
			}
			
		}
		return map;
	}
	/**
	 * ����ָ���������������е�index
	 * @param columnName
	 * @param tblColumnName
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}
	/**
	 * �����������ݣ���Vectorת��Parm
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */

	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		return parmTable;
	}
	/**
	 * ��ʼ���ؼ�
	 * */
	public void initComponent() {
		TabbedPane = (TTabbedPane) this.getComponent("tTabbedPane_1");
		TabbedPane.setSelectedIndex(0);
		type = (TComboBox) this.getComponent("TYPE");
		type.setValue("1");
		tabled = (TTable) this.getComponent("TABLED");
		tabledd = (TTable) this.getComponent("TABLEDD");
		TParm parm = new TParm();
		tabledd.setParmValue(parm);
		tablem = (TTable) this.getComponent("TABLEM");
		callFunction("UI|OWN_AMT|setEnabled", false);
		this.setValue("REDUCE_USER", Operator.getID());
		alld = (TCheckBox) this.getComponent("ALLD");
		alldd = (TCheckBox) this.getComponent("ALLDD");
		rexpMap = this.getRexpMap();
	}
	/**
	 * �����������ݣ���TParmתΪVector
	 * 
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}
	/**
	 * ȫѡ
	 */
	public void onSelAllD() {
		tabled.acceptText();
		TParm parm = tabled.getParmValue();
		if (parm.getCount() <= 0) {
			return;
		}
		// ȫѡ��ѡ
		if (this.getValueBoolean("ALLD")) {
			for (int i = 0; i < parm.getCount(); i++) {
				parm.setData("FLG", i, "Y");

			}
		} else {
			for (int i = 0; i < parm.getCount(); i++) {
				parm.setData("FLG", i, "N");
			}

		}
		tabled.setParmValue(parm);
	}

	/**
	 * ȫѡ
	 */
	public void onSelAllDD() {
		tabled.acceptText();
		TParm parm = tabledd.getParmValue();
		if (tabledd.getRowCount() <= 0) {
			return;
		}
		// ȫѡ��ѡ
		if (alldd.isSelected()) {
			for (int i = 0; i < tabledd.getRowCount(); i++) {
				parm.setData("FLG", i, "Y");

			}
		} else {
			for (int i = 0; i < tabledd.getRowCount(); i++) {
				parm.setData("FLG", i, "N");
			}

		}

		tabledd.setParmValue(parm);

	}

	/**
	 * ���Ӽ����¼�
	 * */
	public void initListener() {

		// ʹ�ñ�Ǹı��¼�
		tabled.addEventListener(tabled.getTag() + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onTableReduceAmt");

	}

	/**
	 * tabled���������
	 * */
	public void onTableReduceAmt(TTableNode cell) {
		tabled.acceptText();
		int col = tabled.getSelectedColumn();
		int row = tabled.getSelectedRow();
		String columnName = tabled.getParmMap(col);
		if (columnName.equals("REDUCE_AMT")) {
			double oldValue = TypeTool.getDouble(cell.getOldValue());
			double reduceAmt = TypeTool.getDouble(cell.getValue());
			double arAmt = tabled.getParmValue().getDouble("AR_AMT", row);
			if (reduceAmt > arAmt) {
				this.messageBox("������ܴ��ڷ������");
				cell.setValue(oldValue);
				return;
			}
		}
	}

	/**
	 *�л�ҳǩ
	 * */
	public void onSelect() {
		// ��ȡҳǩ����
		int selectedIndex = TabbedPane.getSelectedIndex();
		// ѡ�е�һ��ҳǩ �ܶ����
		if (selectedIndex <= 0) {

			// ��չ��õĲ���
			this.setValue("OWN_AMT", "");
			setReduceAmt(commReduceAmt);
			this.setValue("REDUCE_USER", Operator.getID());
			this.setValue("REDUCE_NOTE", "");
			callFunction("UI|OWN_AMT|setEnabled", false);
			callFunction("UI|REDUCE_AMT|setEnabled", true);
			onSelectM();
			TParm parm = new TParm();
			tabled.setParmValue(parm);
			tabledd.setParmValue(parm);
			return;
		}

		// ѡ�еڶ���ҳǩ ϸ�����
		if (selectedIndex == 1) {
			// ��չ��õĲ���
			this.setValue("OWN_AMT", "");
			setReduceAmt(commReduceAmt);
			this.setValue("REDUCE_USER", Operator.getID());
			this.setValue("REDUCE_NOTE", "");
			this.setValue("TYPE", "1");
			callFunction("UI|OWN_AMT|setEnabled", false);
			callFunction("UI|REDUCE_AMT|setEnabled", false);
			onChange();
			return;
		}
	}
	/**
	 * ��������ѯ���ݹ���
	 * @param tableParm
	 * @return
	 */
	private BigDecimal commTableParm(TParm tableParm){
		double sum=0.00;
		BigDecimal ownAmt = new BigDecimal(0);
		BigDecimal sf =null;
		BigDecimal data=null;
		String rexpCode=oneReceiptParm.getValue("REXP_CODE",0);
		for (int i = 0; i < oneReceiptParm.getCount(); i++) {
			if (rexpCode.equals(oneReceiptParm.getValue("REXP_CODE",i))) {
				sum+=oneReceiptParm.getDouble("AR_AMT",i);
			}else{
				tableParm.addData("FLG", "N");
				tableParm.addData("REXP_CODE", rexpCode);
				tableParm.addData("AR_AMT",StringTool.round(sum, 2));
				tableParm.addData("REDUCE_AMT", 0.00);
				tableParm.addData("DATA_TYPE", "1");
				tableParm.addData("ORDER_CODE", rexpCode);
				rexpCode=oneReceiptParm.getValue("REXP_CODE",i);
				sum=oneReceiptParm.getDouble("AR_AMT",i);
			}
			if (i==oneReceiptParm.getCount()-1) {
				tableParm.addData("FLG", "N");
				tableParm.addData("REXP_CODE", rexpCode);
				tableParm.addData("AR_AMT",StringTool.round(sum, 2));
				tableParm.addData("REDUCE_AMT", 0.00);
				tableParm.addData("DATA_TYPE", "1");
				tableParm.addData("ORDER_CODE", rexpCode);
			}
			sf = new BigDecimal(String.valueOf(oneReceiptParm.getDouble(
					"AR_AMT", i)));
			data = sf.setScale(2, RoundingMode.HALF_UP);
			ownAmt = ownAmt.add(data);
		}
		tableParm.setCount(tableParm.getCount("FLG"));
		return ownAmt;
	}
	/**
	 * ����ҽ����ѯ���ݹ���
	 * @param tableParm
	 * @return
	 */
	private void commTableOrderParm(TParm tableParm){
		double sum=0.00;
		double qty=0.00;
		double ownPrice=0.00;
		String orderDesc="";
		String specification="";
		String dosageUnit="";
		String setmainFlg="";
		String rexpCode="";
		String orderCode=onOrderParm.getValue("ORDER_CODE",0);
		String orderSetCode="";
		for (int i = 0; i < onOrderParm.getCount(); i++) {
			if (null!=onOrderParm.getValue("ORDERSET_CODE",i)
					&&onOrderParm.getValue("ORDERSET_CODE",i).length()>0
					&&onOrderParm.getValue("SETMAIN_FLG",i).equals("N")) {//����ҽ��ϸ����Բ���
				orderCode=onOrderParm.getValue("ORDER_CODE",i);
				orderSetCode=onOrderParm.getValue("ORDERSET_CODE",i);
				setmainFlg=onOrderParm.getValue("SETMAIN_FLG",i);
				continue;
			}
			if (orderCode.equals(onOrderParm.getValue("ORDER_CODE",i))) {
				sum+=onOrderParm.getDouble("AR_AMT",i);
				qty+=onOrderParm.getDouble("QTY",i);
				ownPrice=onOrderParm.getDouble("OWN_PRICE",i);
				orderDesc=onOrderParm.getValue("ORDER_DESC",i);
				specification=onOrderParm.getValue("SPECIFICATION",i);
				dosageUnit=onOrderParm.getValue("DOSAGE_UNIT",i);
				setmainFlg=onOrderParm.getValue("SETMAIN_FLG",i);
				rexpCode=onOrderParm.getValue("REXP_CODE",i);
			}else{
				if (null!=orderSetCode
						&&orderSetCode.length()>0
						&&setmainFlg.equals("N")) {//����ҽ��ϸ����Բ���				
				}else{
					tableParm.addData("FLG", "N");
					tableParm.addData("ORDER_CODE", orderCode);
					tableParm.addData("OWN_PRICE", ownPrice);
					tableParm.addData("DOSAGE_QTY", qty);
					tableParm.addData("ORDER_DESC", orderDesc);//����
					tableParm.addData("SPECIFICATION", specification);//���
					tableParm.addData("DOSAGE_UNIT", dosageUnit);//��ҩ��λ
					tableParm.addData("AR_AMT",StringTool.round(sum, 2));
					tableParm.addData("REDUCE_AMT", 0.00);
					tableParm.addData("DATA_TYPE", "2");
					tableParm.addData("SETMAIN_FLG",setmainFlg);
					tableParm.addData("REXP_CODE", rexpCode);
				}
				orderCode=onOrderParm.getValue("ORDER_CODE",i);
				sum=onOrderParm.getDouble("AR_AMT",i);
				qty=onOrderParm.getDouble("QTY",i);
				ownPrice=onOrderParm.getDouble("OWN_PRICE",i);
				orderDesc=onOrderParm.getValue("ORDER_DESC",i);
				specification=onOrderParm.getValue("SPECIFICATION",i);
				dosageUnit=onOrderParm.getValue("DOSAGE_UNIT",i);
				setmainFlg=onOrderParm.getValue("SETMAIN_FLG",i);
				rexpCode=onOrderParm.getValue("REXP_CODE",i);
				orderSetCode=onOrderParm.getValue("ORDERSET_CODE",i);
			}
			if (i==onOrderParm.getCount()-1) {
				tableParm.addData("FLG", "N");
				tableParm.addData("ORDER_CODE", orderCode);
				tableParm.addData("OWN_PRICE", onOrderParm.getDouble("OWN_PRICE",i));
				tableParm.addData("DOSAGE_QTY", qty);
				tableParm.addData("ORDER_DESC", onOrderParm.getValue("ORDER_DESC",i));//����
				tableParm.addData("SPECIFICATION", onOrderParm.getValue("SPECIFICATION",i));//���
				tableParm.addData("DOSAGE_UNIT", onOrderParm.getValue("DOSAGE_UNIT",i));//��ҩ��λ
				tableParm.addData("AR_AMT",StringTool.round(sum, 2));
				tableParm.addData("REDUCE_AMT", 0.00);
				tableParm.addData("DATA_TYPE", "2");
				tableParm.addData("SETMAIN_FLG", onOrderParm.getValue("SETMAIN_FLG",i));
				tableParm.addData("REXP_CODE", onOrderParm.getValue("REXP_CODE",i));
			}
			
		}
		tableParm.setCount(tableParm.getCount("FLG"));
	}
	/**
	 * �ܶ����ҳǩ��ѯ
	 * */
	public void onSelectM() {
//		String sql = "SELECT   'N' FLG,REXP_CODE,SUM(AR_AMT) AR_AMT,0 AS REDUCE_AMT,'1' DATA_TYPE,REXP_CODE AS ORDER_CODE "
//				+ " FROM OPD_ORDER "
//				+ "  WHERE CASE_NO = '"
//				+ this.getValue("CASE_NO")
//				+ "' "
//				+ " AND RECEIPT_NO IS NULL"
//				+ " GROUP BY REXP_CODE ORDER BY REXP_CODE";
//		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
//		if (selParm.getCount() < 0) {
//			return;
//		}
		TParm tableParm =new TParm();
		BigDecimal ownAmt=commTableParm(tableParm);
		// �����ֵ
		callFunction("UI|TABLEM|setParmValue", tableParm);
		this.setValue("OWN_AMT", ownAmt.doubleValue());
//		this.grabFocus("tButton_0");
	}
	private void setReduceAmt(double amt){
		this.setValue("REDUCE_AMT", amt);
		paymentTool.setReduceAmt(amt);
		paymentTool.getAmts();
	}
	/**
	 * �ܶ�����̯����
	 * */
	public void onShare() {
		int selectedIndex = TabbedPane.getSelectedIndex();
		// ѡ�е�һ��ҳǩ �ܶ����
		if (selectedIndex == 0) {
			double reduceAmt = this.getValueDouble("REDUCE_AMT");
			double arAmt = TypeTool.getDouble(getValue("OWN_AMT"));
			if (reduceAmt < 0) {
				this.messageBox("�����ܶ��С��0��");
				setReduceAmt(0.00);
				return;
			}
			if (reduceAmt > arAmt) {
				this.messageBox("�����ܶ�ܴ��ڷ��÷����ܶ");
				setReduceAmt(0.00);
				return;
			}
			DecimalFormat sfd= new DecimalFormat("######0.00");
			BigDecimal sum = new BigDecimal(0);
			BigDecimal reduceAmtBig = new BigDecimal(reduceAmt);
			tablem.acceptText();
			TParm tableParm = tablem.getParmValue();
			for (int i = 0; i < tableParm.getCount(); i++) {
				double ar_amt = tableParm.getDouble("AR_AMT", i);
				double reduceAmts = (ar_amt / arAmt) * reduceAmt;
				BigDecimal sf = new BigDecimal(String.valueOf(reduceAmts));
				BigDecimal data = sf.setScale(2, RoundingMode.HALF_UP);
				if (i == tableParm.getCount() - 1) {
					data = reduceAmtBig.subtract(sum);
				} else {
					sum = sum.add(data);
				}
				tableParm.setData("REDUCE_AMT", i, sfd.format(data.doubleValue()));
			}
			tablem.setParmValue(tableParm);
			setReduceAmt(reduceAmt);
		}
	}

	/**
	 * ��������ܶ�
	 * */
	public void onSumReduce() {
		this.tabledd.acceptText();
		TParm parm = tabledd.getParmValue();
		if (parm.getCount() <= 0)
			setReduceAmt(0.00);
		BigDecimal sum = new BigDecimal(0);
		for (int i = 0; i < parm.getCount(); i++) {
			BigDecimal sf = new BigDecimal(String.valueOf(parm.getDouble(
					"REDUCE_AMT", i)));
			BigDecimal data = sf.setScale(2, RoundingMode.HALF_UP);
			sum = sum.add(data);
		}
		setReduceAmt(sum.doubleValue());
	}

	/**
	 * ���
	 * 
	 */
	public void onAdd() {
		tabled.acceptText();
		tabledd.acceptText();
		TParm parmd = tabled.getParmValue();
		if (parmd.getCount() < 0) {
			this.messageBox("��ѡ������");
			return;
		}
		int count=0;
		TParm rowParm = new TParm();
		if (tabledd.getParmValue() != null)
			rowParm = tabledd.getParmValue();
		for (int i = 0; i < parmd.getCount(); i++) {
			if ("Y".equals(parmd.getData("FLG", i))) {
				TParm errorParm = this.onCheck(parmd.getRow(i), rowParm);
				if (errorParm.getErrCode() < 0) {
					this.messageBox(errorParm.getErrText());
					return;
				}
				count++;
			}
		}
		if(count<=0){
			this.messageBox("�޿���ӵ�����");
			return;
		}
		for (int i = 0; i < parmd.getCount(); i++) {
			if ("Y".equals(parmd.getData("FLG", i))) {
				// �վ���Ŀ
				if (this.getTComboBox("TYPE").getSelectedID().equals("1")) {
					rowParm.addData("FLG", "Y");
					rowParm.addData("DATA_DESC", rexpMap.get(parmd.getData(
							"REXP_CODE", i)));
					rowParm.addData("SPECIFICATION", "");
					rowParm.addData("UNIT", "");
					rowParm.addData("OWN_PRICE", "");
					rowParm.addData("QTY", "1");
					rowParm.addData("AR_AMT", parmd.getData("AR_AMT", i));
					rowParm.addData("REDUCE_AMT", parmd
							.getData("REDUCE_AMT", i));
					rowParm.addData("DATA_CODE", parmd.getData("REXP_CODE", i));
					rowParm.addData("DATA_TYPE", parmd.getData("DATA_TYPE", i));
					rowParm.addData("REXP_CODE", parmd.getData("REXP_CODE", i));

				}
				// ��Ŀ��ϸ
				if (this.getTComboBox("TYPE").getSelectedID().equals("2")) {
					rowParm.addData("FLG", "Y");
					rowParm
							.addData("DATA_DESC", parmd
									.getData("ORDER_DESC", i));
					rowParm.addData("SPECIFICATION", parmd.getData(
							"SPECIFICATION", i));
					rowParm.addData("UNIT", parmd.getData("DOSAGE_UNIT", i));
					rowParm.addData("OWN_PRICE", parmd.getData("OWN_PRICE", i));
					rowParm.addData("QTY", parmd.getData("DOSAGE_QTY", i));
					rowParm.addData("AR_AMT", parmd.getData("AR_AMT", i));
					rowParm.addData("REDUCE_AMT", parmd
							.getData("REDUCE_AMT", i));
					rowParm
							.addData("DATA_CODE", parmd
									.getData("ORDER_CODE", i));
					rowParm.addData("DATA_TYPE", parmd.getData("DATA_TYPE", i));
					rowParm.addData("REXP_CODE", parmd.getData("REXP_CODE", i));
				}
			}
		}
		rowParm.setCount(rowParm.getCount("DATA_CODE"));
		tabledd.setParmValue(rowParm);
		// ��������ܽ��
		onSumReduce();

	}

	/**
	 * У������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onCheck(TParm parm, TParm tableParm) {
		this.tabledd.acceptText();
		TParm result = new TParm();
		String dataType = parm.getValue("DATA_TYPE");
		String dataCode = parm.getValue("ORDER_CODE");
		String rexpCode = parm.getValue("REXP_CODE");
		BigDecimal sum = new BigDecimal(parm.getDouble("REDUCE_AMT"));
		for (int i = 0; i < tableParm.getCount(); i++) {
			TParm parmRow = tableParm.getRow(i);
			if (dataType.equals("2")
					&& dataCode.equals(parmRow.getValue("DATA_CODE"))) {// ��ϸ����
				BigDecimal sf = new BigDecimal(String.valueOf(parmRow
						.getDouble("REDUCE_AMT")));
				BigDecimal data = sf.setScale(2, RoundingMode.HALF_UP);
				sum = sum.add(data);
			}
		}
		if (dataType.equals("2")) {
			result = this.getOrderCodeSumAmt(parm, sum.doubleValue());
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		TParm parmValue = tabledd.getParmValue();
		if (parmValue == null) {
			return result;
		}
		if (parmValue.getCount() <= 0)
			return result;
		for (int i = 0; i < parmValue.getCount(); i++) {
			TParm parmRow = parmValue.getRow(i);
			if (parmRow.getValue("DATA_TYPE").equals(dataType)
					&& parmRow.getValue("DATA_CODE").equals(dataCode)) {
				result.setErr(-1, parmRow.getValue("DATA_DESC") + "�Ѽ���,�����²�����");
				return result;
			}
			if ((!parmRow.getValue("DATA_TYPE").equals(dataType))
					&& parmRow.getValue("REXP_CODE").equals(rexpCode)) {
				result.setErr(-1, parmRow.getValue("DATA_DESC") + "�Ѽ���,�����²�����");
				return result;
			}
		}
		return result;
	}

	/**
	 * ���������Ŀ���ܽ��
	 * 
	 * @param parm
	 */
	public TParm getOrderCodeSumAmt(TParm parm, double reduceAmt) {
		TParm result = new TParm();
		if (parm.getValue("SETMAIN_FLG").equals("Y")) {
			String sql = "SELECT SUM(AR_AMT) AR_AMT FROM OPD_ORDER WHERE CASE_NO='"
					+ this.caseNo
					+ "' AND ORDERSET_CODE='"
					+ parm.getValue("ORDER_CODE") + "' AND RECEIPT_NO IS NULL GROUP BY ORDERSET_CODE";
			TParm Reparm = new TParm(TJDODBTool.getInstance().select(sql));
			if (Reparm.getDouble("AR_AMT", 0) - reduceAmt < 0) {
				result.setErr(-1, "ҽ��:" + parm.getValue("ORDER_DESC")
						+ "�ܶ�����Ϊ��ֵ,��������д�����");
				return result;
			}
		} else {
			String sql = "SELECT SUM(AR_AMT) AR_AMT FROM OPD_ORDER WHERE CASE_NO='"
					+ this.caseNo
					+ "' AND ORDER_CODE='"
					+ parm.getValue("ORDER_CODE") + "' AND RECEIPT_NO IS NULL GROUP BY ORDER_CODE";
			TParm Reparm = new TParm(TJDODBTool.getInstance().select(sql));
			if (Reparm.getDouble("AR_AMT", 0) - reduceAmt < 0) {
				result.setErr(-1, "ҽ��:" + parm.getValue("ORDER_DESC")
						+ "�ܶ�����Ϊ��ֵ,��������д�����");
				return result;
			}
		}
		return result;
	}

	/**
	 * �Ƴ�
	 * */
	public void onDelete() {
		tabledd.acceptText();
		TParm parmdd = tabledd.getParmValue();
		if (tabledd.getRowCount() <=0) {
			this.messageBox("û�п��Ƴ�������");
			return;
		}
		int result = this.messageBox("�Ƴ�ȷ��", "ȷ��Ҫ�Ƴ���", 1);
		// ȷ��ɾ��
		if (result == 0) {
			for (int i = tabledd.getRowCount() - 1; i >= 0; i--) {
				if ("Y".equals(parmdd.getData("FLG", i))) {
					// �Ƴ�
					tabledd.removeRow(i);
				}
			}
		} else {
			return;
		}
		// ��������ܶ�
		onSumReduce();

	}

	/**
	 * combox�¼�
	 * */
	public void onChange() {
		if ("1".equals(type.getValue())) {

			tabled
					.setHeader("ѡ,30,boolean;�վ����,150,REXP_CODE;�������,80;������,80,double,#########0.00");
			tabled.setLockColumns("1,2");
			tabled.setParmMap("FLG;REXP_CODE;AR_AMT;REDUCE_AMT;ORDER_CODE");
			tabled.setItem("REXP_CODE");
//			String sql = "SELECT   'N' FLG,REXP_CODE,SUM(AR_AMT) AR_AMT,0 AS REDUCE_AMT,'1' DATA_TYPE,REXP_CODE AS ORDER_CODE "
//					+ " FROM OPD_ORDER "
//					+ "  WHERE CASE_NO = '"
//					+ this.getValue("CASE_NO")
//					+ "' "
//					+ " AND RECEIPT_NO IS NULL"
//					+ " GROUP BY REXP_CODE ORDER BY REXP_CODE";
			TParm tableParm=new TParm();
			BigDecimal ownAmt=commTableParm(tableParm);
//			TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
//			if (selParm.getCount() < 0) {
//				return;
//			}
			// �����ֵ
			// FLG;CHARGE_HOSP_DESC;AR_AMT;REDUCE_AMT;HEXP_CODE
			callFunction("UI|TABLED|setParmValue", tableParm);
			tabled.setColumnHorizontalAlignmentData("1,left;2,right;3,right");
			this.setValue("OWN_AMT", ownAmt.doubleValue());
		}
		// ������ϸ
		if ("2".equals(type.getValue())) {
			tabled
					.setHeader("ѡ,30,boolean;��������,150;���,80;��λ,50,UNIT;����,60;����,40;�������,80;������,80,double,#########0.00");
			tabled.setLockColumns("1,2,3,4,5,6");
			tabled.setItem("UNIT");
			tabled
					.setParmMap("FLG;ORDER_DESC;SPECIFICATION;DOSAGE_UNIT;OWN_PRICE;DOSAGE_QTY;AR_AMT;REDUCE_AMT;ORDER_CODE;REXP_CODE");
			// ��ѯ������ϸ
//			String sql = "SELECT 'N' AS FLG, ORDER_CODE, ORDER_DESC, SPECIFICATION, OWN_PRICE, SUM(AR_AMT) AS AR_AMT, "
//					+ " 0 AS REDUCE_AMT, SUM(DOSAGE_QTY) AS DOSAGE_QTY, DOSAGE_UNIT,REXP_CODE,'2' DATA_TYPE,SETMAIN_FLG"
//					+ " FROM OPD_ORDER "
//					+ " WHERE CASE_NO = '"
//					+ this.getValue("CASE_NO")
//					+ "'  AND RECEIPT_NO IS NULL AND (HIDE_FLG='N' OR HIDE_FLG IS NULL) GROUP BY REXP_CODE,ORDER_CODE," +
//							"OWN_PRICE,DOSAGE_UNIT,SPECIFICATION,ORDER_DESC,SETMAIN_FLG ORDER BY ORDER_CODE";
//			TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
//			if (selParm.getCount() < 0) {
//				return;
//			}
			DecimalFormat sfd= new DecimalFormat("######0.00");
			// �����ֵ
			tabled.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,right;5,right;6,right;7,right");
			TParm tableParm=new TParm(); 
			double ownAmt=0.00;
			commTableOrderParm(tableParm);
			for (int i = 0; i < tableParm.getCount(); i++) {
				if (tableParm.getValue("SETMAIN_FLG", i).equals("Y")) {
					TParm parmRow = this.getOrderSetAmt(tableParm.getRow(i));
					tableParm.setData("OWN_PRICE", i,sfd.format(parmRow
							.getDouble("OWN_PRICE")/tableParm.getDouble("DOSAGE_QTY",i)));
					tableParm.setData("AR_AMT", i, parmRow.getDouble("AR_AMT"));
				}
				ownAmt+=tableParm.getDouble("AR_AMT",i);
			}
//			for (int i = 0; i < selParm.getCount(); i++) {
//				if (selParm.getValue("SETMAIN_FLG", i).equals("Y")) {
//					TParm parmRow = this.getOrderSetAmt(selParm.getRow(i));
//					selParm.setData("OWN_PRICE", i,sfd.format(parmRow
//							.getDouble("OWN_PRICE")/selParm.getDouble("DOSAGE_QTY",i)));
//					selParm.setData("AR_AMT", i, parmRow.getDouble("AR_AMT"));
//				}
//				sum += selParm.getDouble("AR_AMT", i);
//			}
			this.setValue("OWN_AMT", sfd.format(ownAmt));
			callFunction("UI|TABLED|setParmValue", tableParm);
		}
	}

	/**
	 * ����ҽ���������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getOrderSetAmt(TParm parm) {
		String orderSetCode = parm.getValue("ORDER_CODE");
		//String orderSetGroupNo = parm.getValue("ORDERSET_GROUP_NO");
		//String rxNo = parm.getValue("RX_NO");
		double ownPrice=0.00;
		double arAmt=0.00;
		for (int i = 0; i < onOrderParm.getCount("ORDER_CODE"); i++) {
			if (onOrderParm.getValue("ORDERSET_CODE",i).equals(orderSetCode)) {
				ownPrice+=onOrderParm.getDouble("OWN_PRICE",i);
				arAmt+=onOrderParm.getDouble("AR_AMT",i);
			}
		}
		TParm reParm=new TParm();
		reParm.setData("OWN_PRICE",ownPrice);
		reParm.setData("AR_AMT",arAmt);
//		String sql = "SELECT SUM(OWN_PRICE) AS OWN_PRICE ,SUM(AR_AMT) AR_AMT" + " FROM OPD_ORDER "
//				+ " WHERE ORDERSET_CODE='" + orderSetCode+"' AND SETMAIN_FLG='N' AND RECEIPT_NO IS NULL";
//		TParm reparm = new TParm(TJDODBTool.getInstance().select(sql));
		return reParm;
	}

	/**
	 * ��ӡ
	 * */
	public void onPrint() {
		TParm result = new TParm();
		result.setData("REDUCEFLG", "N");

		// ���ظ����ý��������
		this.setReturnValue(result);
		this.closeWindow();

	}

	/**
	 * �����ӡ
	 * */
	public void onReducePrint() {
		if (this.getValueDouble("REDUCE_AMT")<=0) {
			this.messageBox("������С�ڻ����0�������Բ�����Ʊ");
			return;
		}
		if (this.getValueDouble("REDUCE_AMT")>this.getValueDouble("OWN_AMT")) {
			this.messageBox("������ܴ��ڷ����ܶ�");
			return;
		}
		TParm paymentParm=paymentTool.table.getParmValue();
		double payMentAmt=0.00;
		for (int i = 0; i <paymentParm.getCount(); i++) {
			payMentAmt+=paymentParm.getDouble("REDUCE_AMT",i);
			if (paymentParm.getValue("PAY_TYPE",i).equals("LPK")
					&&paymentParm.getDouble("REDUCE_AMT",i)!=0||
					paymentParm.getValue("PAY_TYPE",i).equals("XJZKQ")
					&&paymentParm.getDouble("REDUCE_AMT",i)!=0) {//	֧����ʽΪ�ֽ���������Ͳ����Բ�������
				this.messageBox("��Ʒ�������ȯ֧����ʽ�����Բ�������");
				return;
			}
		}
		if (this.getValueDouble("REDUCE_AMT")!=payMentAmt) {
			this.messageBox("��������֧����ʽ���ۼƼ����ܶ��");
			return;
		}
		String reduce_no = SystemTool.getInstance().getNo("ALL", "OPB",
				"REDUCE_NO", "REDUCE_NO");
		TParm result = new TParm();
		result.setData("REDUCEFLG", "Y");
		result.setData("REDUCE_AMT", this.getValueDouble("REDUCE_AMT"));
		result.setData("REDUCE_NO", reduce_no);
		Map<String, String> map = getPayTypeMap(false);
		for (int i = 0; i <paymentParm.getCount("PAY_TYPE"); i++) {
			paymentParm.setData("PAY_TYPE", i, map.get(paymentParm.getValue("PAY_TYPE", i)));//����ת��
		}
		result.setData("PAYMENT_PARM",paymentParm.getData());//����֧����ʽ
		TParm parmForReduceM = new TParm();
		parmForReduceM.setData("REDUCE_NO", reduce_no);
		parmForReduceM.setData("ADM_TYPE", "O");
		parmForReduceM.setData("CASE_NO", this.getValue("CASE_NO"));
		parmForReduceM.setData("MR_NO", this.getValue("MR_NO"));
		int selectedIndex = TabbedPane.getSelectedIndex();
		if (selectedIndex <= 0) {
			parmForReduceM.setData("REDUCE_TYPE", "1");
		}
		if (selectedIndex == 1) {
			parmForReduceM.setData("REDUCE_TYPE", "2");
		}
		parmForReduceM.setData("AR_AMT", this.getValueDouble("OWN_AMT"));
		parmForReduceM.setData("REDUCE_AMT", this.getValueDouble("REDUCE_AMT"));
		parmForReduceM.setData("REDUCE_USER", this.getValue("REDUCE_USER"));
		parmForReduceM.setData("REDUCE_NOTE", this.getValue("REDUCE_NOTE"));
		parmForReduceM.setData("REDUCE_FLG", "Y");
		parmForReduceM.setData("RESET_USER", "");
		parmForReduceM.setData("RESET_DATE", "");
		parmForReduceM.setData("OPT_USER", Operator.getID());
		parmForReduceM.setData("OPT_TERM", Operator.getIP());
		parmForReduceM.setData("RESET_REDUCE_NO", "");
		parmForReduceM.setData("BUSINESS_NO", "");
		parmForReduceM.setData("ACCOUNT_FLG", "");
		parmForReduceM.setData("ACCOUNT_SEQ", "");
		parmForReduceM.setData("ACCOUNT_USER", "");
		parmForReduceM.setData("ACCOUNT_DATE", "");
		result.setData("parmForReduceM", parmForReduceM.getData());

		TParm parmForReduceD = new TParm();
		int seq = 1;
		if (selectedIndex <= 0) {
			// �ܶ����
			int count = tablem.getRowCount();
			for (int i = 0; i < count; i++) {
				parmForReduceD.addData("REDUCE_NO", reduce_no);
				parmForReduceD.addData("SEQ", seq);
				parmForReduceD.addData("REDUCE_TYPE", "1");
				parmForReduceD.addData("DATA_TYPE", "1");
				parmForReduceD.addData("DATA_NAME", this.rexpMap.get(tablem
						.getParmValue().getData("REXP_CODE", i)));
				parmForReduceD.addData("DATA_CODE", tablem.getParmValue()
						.getData("REXP_CODE", i));
				parmForReduceD.addData("DESCRIPTION", "");
				parmForReduceD.addData("UNIT_CODE", "");
				parmForReduceD.addData("OWN_PRICE", "");
				parmForReduceD.addData("QTY", "");
				parmForReduceD.addData("AR_AMT", tablem.getParmValue().getData(
						"AR_AMT", i));
				parmForReduceD.addData("REDUCE_AMT", tablem.getParmValue()
						.getData("REDUCE_AMT", i));
				parmForReduceD.addData("OPT_USER", Operator.getID());
				parmForReduceD.addData("OPT_TERM", Operator.getIP());
				parmForReduceD.addData("RESET_REDUCE_NO", "");
				seq++;
			}
		}
		if (selectedIndex == 1) {
			// �������
			int count = tabledd.getRowCount();
			for (int i = 0; i < count; i++) {
				parmForReduceD.addData("REDUCE_NO", reduce_no);
				parmForReduceD.addData("SEQ", seq);
				parmForReduceD.addData("REDUCE_TYPE", "2");
				parmForReduceD.addData("DATA_TYPE", tabledd.getParmValue()
						.getData("DATA_TYPE", i));

				// DATA_DESC;SPECIFICATION;UNIT;OWN_PRICE;QTY;AR_AMT;REDUCE_AMT;DATA_CODE
				parmForReduceD.addData("DATA_NAME", tabledd.getParmValue()
						.getData("DATA_DESC", i));
				parmForReduceD.addData("DESCRIPTION", tabledd.getParmValue()
						.getData("SPECIFICATION", i));
				parmForReduceD.addData("UNIT_CODE", tabledd.getParmValue()
						.getData("UNIT", i));
				parmForReduceD.addData("OWN_PRICE", tabledd.getParmValue()
						.getData("OWN_PRICE", i));
				parmForReduceD.addData("QTY", tabledd.getParmValue().getData(
						"QTY", i));
				parmForReduceD.addData("AR_AMT", tabledd.getParmValue()
						.getData("AR_AMT", i));
				parmForReduceD.addData("REDUCE_AMT", tabledd.getParmValue()
						.getData("REDUCE_AMT", i));
				parmForReduceD.addData("OPT_USER", Operator.getID());
				parmForReduceD.addData("OPT_TERM", Operator.getIP());
				parmForReduceD.addData("DATA_CODE", tabledd.getParmValue()
						.getData("DATA_CODE", i));
				parmForReduceD.addData("RESET_REDUCE_NO", "");
				seq++;
			}
		}
		result.setData("parmForReduceD", parmForReduceD.getData());
		this.setReturnValue(result);
		this.closeWindow();

	}

	/**
	 * �õ��վ�����
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private Map<String, String> getRexpMap() {
		Map<String, String> RexpMap = new HashMap<String, String>();
		String sql = "SELECT  ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < parm.getCount(); i++) {
			String id = parm.getValue("ID", i);
			String chn = parm.getValue("CHN_DESC", i);
			RexpMap.put(id, chn);
		}
		return RexpMap;
	}

	/**
	 * ȡ��
	 * */
	public void onCancel() {
		this.setReturnValue(null);
		this.closeWindow();
	}

	/**
	 * �õ�TComboBox
	 * 
	 * @param tag
	 *            String
	 * @return TComboBox
	 */
	public TComboBox getTComboBox(String tag) {
		return (TComboBox) this.getComponent(tag);
	}
	
	public void getTTextFormat(){
		numberTextField =  (TNumberTextField) getComponent("REDUCE_AMT");
		
	}
}
