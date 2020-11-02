package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.JavaHisDebug;
import java.util.Vector;

import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;
import java.util.Map;
import java.util.HashMap;

import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ZangJH
 * @version 1.0
 */
public class SYSFee_OrdSetOptionControl extends TControl {

	String dept;
	TTextFormat DEPT;
	TTextFormat TYPE;
	double defaultQty = 0.0;
	TDS packDDS = new TDS();
	TParm parmDDS = new TParm();
	TNumberTextField TOT_FEE;
	TTable table;
	TCheckBox all;// caowl 20130305 add
	// ���ڼ�¼�����TDS�������������table��--�Ƿ�ѡ��
	Map recordRealyRow = new HashMap();
	// ���prefech��������
	TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");

	public SYSFee_OrdSetOptionControl() {
	}

	public void onInit() {
		initParmFromOutside();
		this.callFunction("UI|TABLE|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableCheckBoxChangeValue");
		myInitControler();
		all = (TCheckBox) this.getComponent("ALL");// caowl 20130305 add
	}

	/**
	 * ��ʼ���������caseNo/stationCode
	 */
	public void initParmFromOutside() {
		// ��סԺ��ʿִ�н����õ�����TParm
		TParm outsideParm = (TParm) this.getParameter();
		if (outsideParm != null) {
			// �趨��ʼ������Ĳ���
			setDept(outsideParm.getData("PACK", "DEPT").toString());
		}
	}

	public void onSelect() {
		String type = this.getValueString("TYPE");
		showByOutside(type);

	}

	/**
	 * �ⲿ�ӿڵ���
	 * 
	 * @param orderByout
	 *            String
	 * @param fromFlg
	 *            String
	 */
	public void showByOutside(String orderCode) {

		table = (TTable) this.getComponent("TABLE");

		String exeSel = getSpl(orderCode);
//		packDDS.setSQL(exeSel);
//		packDDS.retrieve();
		parmDDS = new TParm(TJDODBTool.getInstance().select(exeSel));
		if(parmDDS.getCount()<=0){
			this.messageBox("û�в�ѯ���ײ�ģ�����ݻ��ֵ�δ¼����Ŀ");
			TParm parm = new TParm();
			table.setParmValue(parm);
			return;
		}
		// �õ�ȡ������������/2,��ʾ������
		//int row = packDDS.rowCount() / 2+1;  //modify by huangtt 20140926  
		table.setParmValue(parmDDS);//modify by xiongwg 20150204
//		// �õ���table���Լ�����TDS
//		TDS tds = (TDS) table.getDataStore();
//		// ���ø�TDS����Ϊ5�У��յģ�
//		tds.getBuffer(TDS.PRIMARY).setCount(row);
//		// ��TDS������ҲΪ��
//		tds.setColumns(new String[] {});
//		// �����۲���
//		magicalObserverPackD s = new magicalObserverPackD();
//		// ���øù۲�����Ҫ�ı��TDS
//		s.setDS(packDDS);
//		// ���ù۲�����Ҫ�۲��TDS���ٵģ�
//		tds.addObserver(s);
//		table.setDSValue();
	}

	/**
	 * table�ϵ�checkBoxע�����
	 * 
	 * @param obj
	 *            Object
	 */
	public void onTableCheckBoxChangeValue(Object obj) {
		// ��õ����table����
		TTable table = (TTable) obj;
		// ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
		table.acceptText();
		// ���ѡ�е���/��
		int col = table.getSelectedColumn();
		int row = table.getSelectedRow();
		// ���ѡ�е��ǵ�11�оͼ���ִ�ж���--ִ��
		String columnName = table.getParmMap(col);
		if (columnName.equals("N_SEL")) {
			boolean exeFlg;
			// ��õ��ʱ��ֵ
			exeFlg = TypeTool.getBoolean(table.getValueAt(row, col));
			table.setItem(row, "N_SEL", exeFlg);
			// ��¼TDS�������������Ƿ���ѡ�б�ǣ�true/false��
			//recordRealyRow.put(row * 2 + "", exeFlg);
//			recordRealyRow.put(row+ "", exeFlg);
			// ѡ��Ϳɱ༭�෴���
//			table.setLockCell(row, 2, !exeFlg);//ѡ�к�ȡ������סʹ������Ԫ��
			// ����۸�������Ĭ������
			//String ordCode = packDDS.getItemString(row * 2, "ORDER_CODE");
			String ordCode = parmDDS.getValue("ORDER_CODE",row);
			double diffQty = 0.0; // �����ֵ
			// �Ƿ�ѡ��
			if (exeFlg) {
				// ѡ���ʱ���ʼ��
				//defaultQty = packDDS.getItemDouble(row * 2, "DOSAGE_QTY");
				defaultQty = parmDDS.getDouble("DOSAGE_QTY",row);
				// ����ֵ����Ĭ��ֵ
				diffQty = TypeTool.getDouble(table.getValueAt(row, 2));
				table.setLockCell(row, 2, exeFlg);//ѡ�к���ʹ����
			} else {
				// �õ���ǰֵ
				double nowQty = TypeTool.getDouble(table.getValueAt(row, 2));
				// ȡ��ѡ���ʱ���޸ĵ�Ԫ�������ΪĬ��
				//table.setItem(row, "N_DOSAGE_QTY", defaultQty); // ���ڣ����ԭ�����ѱ��ٴ�ѡ��ʱʹ�ã�
//				table.setItem(row, "DOSAGE_QTY", defaultQty); // ���ڣ����ԭ�����ѱ��ٴ�ѡ��ʱʹ�ã�
				// table.setValueAt(defaultQty+"",row,2);//����
				// Ĭ��ֵ-����ֵ=��
				diffQty = 0 - nowQty;
				table.setLockCell(row, 2, exeFlg);//δѡ�к���ʹ����
			}
			// �޸ļ����ܼ�
				countTotFee(ordCode, diffQty);
		} 
//		else if (columnName.equals("S_SEL")) {
//			boolean exeFlg;
//			// ��õ��ʱ��ֵ
//			exeFlg = TypeTool.getBoolean(table.getValueAt(row, col));
//			table.setItem(row, "S_SEL", exeFlg);
//			// ��¼TDS�������������Ƿ���ѡ�б�ǣ�true/false��
//			//recordRealyRow.put(row * 2 + 1 + "", exeFlg);		
//			table.setLockCell(row, 7, !exeFlg);
//			// ����۸�������Ĭ������
//			//String ordCode = packDDS.getItemString(row * 2 + 1, "ORDER_CODE");
//			String ordCode = packDDS.getItemString(row + 1, "ORDER_CODE");
//			double diffQty = 0.0; // �����ֵ
//			// �Ƿ�ѡ��
//			if (exeFlg) {
//				// ѡ���ʱ���ʼ��
//				//defaultQty = packDDS.getItemDouble(row * 2 + 1, "DOSAGE_QTY");
//				// ����ֵ����Ĭ��ֵ
//				diffQty = TypeTool.getDouble(table.getValueAt(row, 7));
//			} else {
//				// �õ���ǰֵ
//				double nowQty = TypeTool.getDouble(table.getValueAt(row, 7));
//				// ȡ��ѡ���ʱ���޸ĵ�Ԫ�������ΪĬ��
//				table.setItem(row, "S_DOSAGE_QTY", defaultQty); // ���ڣ����ԭ�����ѱ��ٴ�ѡ��ʱʹ�ã�
//				// table.setValueAt(defaultQty+"",row,2);//����
//				// Ĭ��ֵ-����ֵ=��
//				diffQty = 0 - nowQty;
//			}
//			// �޸ļ����ܼ�
//			countTotFee(ordCode, diffQty);
//		}

	}

	/**
	 * �õ�SQL���
	 * 
	 * @param code
	 *            String
	 * @return String
	 */
	public String getSpl(String packcode) {

		String sql = " SELECT '' AS N_SEL,A.ORDER_DESC,A.DOSAGE_QTY,A.DOSAGE_UNIT,B.OWN_PRICE," +
				"B.SPECIFICATION,B.MAN_CODE,A.ORDER_CODE " +
				" FROM SYS_ORDER_PACKD A,SYS_FEE B " + " WHERE A.PACK_CODE='"
				+ packcode + "' AND A.ORDER_CODE=B.ORDER_CODE ORDER BY A.SEQ_NO ";
		return sql;

	}

	/**
	 * ȷ������
	 * 
	 * @param args
	 *            String[]
	 */
	public void onOK() {
		// �ռ�Ҫ���ص����ݣ�code/������
		TParm retDate = gainRtnDate();
		// ���ظ����ý��������
		this.setReturnValue(retDate);
		this.closeWindow();
	}

	/**
	 * �ռ�Ҫ���ص�����
	 */
	public TParm gainRtnDate() {
		TParm result = new TParm();
		// ѭ����ȡѡ�е���Ŀ
//		for (int i = 0; i < packDDS.rowCount(); i++) {
		for (int i = 0; i < parmDDS.getCount(); i++) {
			// ���к�Ϊ��������map��ѯ���ظ����Ƿ���ѡ��
//			if (TypeTool.getBoolean(recordRealyRow.get(i + ""))) {
			if (parmDDS.getValue("N_SEL",i).equals("Y")) {
//				String orderCode = packDDS.getItemString(i, "ORDER_CODE");
//				double qty = packDDS.getItemDouble(i, "DOSAGE_QTY");
				String orderCode = parmDDS.getValue("ORDER_CODE",i);
				double qty = parmDDS.getDouble("DOSAGE_QTY",i);
				result.addData("ORDER_CODE", orderCode);
				result.addData("DOSAGE_QTY", qty);
//				result.addData("DOSAGE_UNIT", packDDS.getItemString(i, "DOSAGE_UNIT"));//====pangben 2013-08-05 ��ӵ�λ
				result.addData("DOSAGE_UNIT", parmDDS.getValue("DOSAGE_UNIT",i));//====pangben 2013-08-05 ��ӵ�λ
			}
			continue;
		}

		return result;
	}

	/**
	 * ��ص�5�У�����ֵ�ĸı�ˢ���ܷ���
	 * 
	 * @param node
	 *            TTableNode
	 */
	public void onChangeFee(TTableNode cell) {

		int row = cell.getRow();		
		if (cell.getColumn() == 2) {
			String oederCode = packDDS.getItemString(row * 2, "ORDER_CODE");
			double oldValue = TypeTool.getDouble(cell.getOldValue());
			double newValue = TypeTool.getDouble(cell.getValue());
			// ȡ������ֵ�ò�
			double diffValue = newValue - oldValue;
			// �ø���Ŀ����*�������+ԭ�����ܷ���=���ڵ��ܷ���
			countTotFee(oederCode, diffValue);
		}
//		if (cell.getColumn() == 7) {
//			String oederCode = packDDS.getItemString(row * 2 + 1, "ORDER_CODE");
//			double oldValue = TypeTool.getDouble(cell.getOldValue());
//			double newValue = TypeTool.getDouble(cell.getValue());
//			// ȡ������ֵ�ò�
//			double diffValue = newValue - oldValue;
//			// �ø���Ŀ����*�������+ԭ�����ܷ���=���ڵ��ܷ���
//			countTotFee(oederCode, diffValue);
//		}
	}

	/**
	 * ����orderCode���޸ĵ�cell�Ĳ��������µ��ܼ�
	 * 
	 * @param orderCode
	 *            String
	 * @param cell
	 *            TTableNode
	 */
	public void countTotFee(String orderCode, double diffValue) {

		// �õ�����
		double ownPrice = TypeTool.getDouble(getSysFeeValue(orderCode,
				"OWN_PRICE"));
		// �ø���Ŀ����*�������+ԭ�����ܷ���=���ڵ��ܷ���
		double totFee = diffValue * ownPrice
				+ TypeTool.getDouble(TOT_FEE.getValue());
		TOT_FEE.setValue(totFee);
	}

	/**
	 * ���ȵõ�����UI�Ŀؼ�����/ע����Ӧ���¼� ����
	 */
	public void myInitControler() {

		DEPT = (TTextFormat) this.getComponent("DEPT");
		TYPE = (TTextFormat) this.getComponent("TYPE");
		// �õ�table�ؼ�
		table = (TTable) this.getComponent("TABLE");
		//���ε��¼�ֵ�ı�-xiongwg20150205 start
//		table.addEventListener(
//				table.getTag() + "->" + TTableEvent.CHANGE_VALUE, this,
//				"onChangeFee");
		// PS:��סĳһ��--���Խ�����ĳһ��cell
		//���ε��¼�ֵ�ı�-xiongwg20150205 end
		table.setLockCellColumn(2, true);
//		table.setLockCellColumn(7, true);
		TOT_FEE = (TNumberTextField) this.getComponent("TOT_FEE");

		DEPT.setValue(getDept());
	}

	/**
	 * ȡ��
	 * 
	 * @param args
	 *            String[]
	 */
	public void onCANCLE() {
		switch (messageBox("��ʾ��Ϣ", "ȷ��ȡ��ѡ��", this.YES_NO_OPTION)) {
		case 0:
			this.closeWindow();
		case 1:
			break;
		}
		return;
	}

	// caowl 20130305 start
	/**
	 * ȫѡ
	 */
	public void onSelAll() {
		table.acceptText();
		if (table == null) {
			return;
		}
		int row = table.getRowCount();
		double totFee = 0.0;// �ܼ�
		boolean exeFlg = all.isSelected();
		if (exeFlg) {

			for (int i = 0; i < row; i++) {
				table.setItem(i, "N_SEL", true);
//				table.setItem(i, "S_SEL", true);
			}
			for (int i = 0; i < 2 * row; i++) {
//				String ordCode = packDDS.getItemString(i, "ORDER_CODE");
				String ordCode = parmDDS.getValue("ORDER_CODE",i);
				double ownPrice = TypeTool.getDouble(getSysFeeValue

				(ordCode, "OWN_PRICE"));
				// ѡ���ʱ���ʼ��
//				double qty = packDDS.getItemDouble(i, "DOSAGE_QTY");
				double qty = parmDDS.getDouble("DOSAGE_QTY",i);

				totFee += qty * ownPrice;
//				recordRealyRow.put(i + "", true);
			}
			table.setLockCellColumn(2, exeFlg);//ѡ�к���ʹ����
		} else {
			for (int i = 0; i < row; i++) {

				table.setItem(i, "N_SEL", false);
//				table.setItem(i, "S_SEL", false);
			}
			for (int i = 0; i < 2 * row; i++) {
//				recordRealyRow.put(i + "", false);
			}
			totFee = 0.0;
			table.setLockCellColumn(2, exeFlg);//δѡ�к���ʹ����
		}
		TOT_FEE.setValue(totFee);

	}

	// caowl 20130305 end

	/**
	 * 
	 * @param s
	 *            ���ݵ�ORDER_CODE
	 * @param colName
	 *            Ҫ�������
	 * @return String
	 */
	public String getSysFeeValue(String s, String colName) {
//		if (dataStore == null)
//			return s;
//		String bufferString = dataStore.isFilter() ? dataStore.FILTER
//				: dataStore.PRIMARY;
//		TParm parm = dataStore.getBuffer(bufferString);
//		Vector vKey = (Vector) parm.getData("ORDER_CODE");
//		Vector vOwnPrice = (Vector) parm.getData(colName);
		if(parmDDS.getCount()<=0)
			return s;	
		Vector vKey = (Vector) parmDDS.getData("ORDER_CODE");
		Vector vOwnPrice = (Vector) parmDDS.getData(colName);
		int count = vKey.size();
		for (int i = 0; i < count; i++) {
			if (s.equals(vKey.get(i)))
				return "" + vOwnPrice.get(i);
		}
		return s;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	// ��������
	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// JavaHisDebug.TBuilder();

		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("sys\\SYS_FEE\\SYSFEE_ORDSETOPTION.x");
	}

}
