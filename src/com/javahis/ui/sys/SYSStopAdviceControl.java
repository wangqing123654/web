package com.javahis.ui.sys;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.SYSStopAdviceTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: ����ҽ��ͣ��֪ͨ
 * </p>
 * 
 * <p>
 * Description: ����ҽ��ͣ��֪ͨ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author wu 2012-7-30����05:08:44
 * @version 1.0
 */
public class SYSStopAdviceControl extends TControl {
	private TTable table;
	private int sortColumn = -1;
	private boolean ascending = false;
	private Compare compare = new Compare();
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		this.table = (TTable) this.getComponent("TABLE");
		addListener(table);
	}
	/**
	 * ִ�в�ѯ����
	 */
	public void onQuery(){
		String all = this.getValueString("ALL");
		String medicine = this.getValueString("MEDICINE");
		String condition = "";
		if(!"Y".equals(all)){
			if("Y".equals(medicine)){
				condition = "A.CAT1_TYPE='PHA' AND ";
			}else{
				condition = "A.CAT1_TYPE<>'PHA' AND ";
			}
		}
		TParm parm = new TParm();
		parm.setData("condition", condition);
		TParm result = SYSStopAdviceTool.getInstance().onQuery(parm);
		if (result.getCount("ORDER_CODE") < 1) {
			this.table.removeRowAll();
			this.messageBox("E0008");
			return;
		}
		this.table.setParmValue(result);
	}
	/**
	 * �ѱ���е����ݵ���excel
	 */
	public void onExport(){
		TParm parm=table.getParmValue();
    	if (parm == null ||parm.getCount("ORDER_CODE")<=0) {
    		this.messageBox("û����Ҫ����������");
			return;
		}
        if (table.getRowCount() > 0)
            ExportExcelUtil.getInstance().exportExcel(table, "����ҽ��ͣ��֪ͨ");
	}
	/**
	 * ��ӡ�������ʾ������
	 */
	public void onPrint(){
		TParm tableParm = table.getParmValue();
		if (tableParm == null || tableParm.getCount("ORDER_CODE") <= 0) {
			this.messageBox("û��Ҫ��ӡ������");
            return;
        }
        TParm printData = new TParm();
        TParm T1 = new TParm();
        int tableCount = tableParm.getCount("ORDER_CODE");
        for (int i = 0; i < tableCount; i++) {
            T1.setRowData(i, tableParm.getRow(i));
            Timestamp data = (Timestamp) T1.getData("BILL_CREATE_DATE", i);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String date = sdf.format(data);
            T1.setData("BILL_CREATE_DATE", i, date);
        }
        T1.setCount(tableCount);
        T1.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
        T1.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        T1.addData("SYSTEM", "COLUMNS", "MR_NO");
        T1.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        T1.addData("SYSTEM", "COLUMNS", "BED_NO");
        T1.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        T1.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        T1.addData("SYSTEM", "COLUMNS", "TREAT_DOC_NAME");
        T1.addData("SYSTEM", "COLUMNS", "BILL_CREATE_NAME");
        T1.addData("SYSTEM", "COLUMNS", "BILL_CREATE_DATE");
        printData.setData("T1", T1.getData());
        printData.setData("printDate", "TEXT",
                          StringTool.getString(SystemTool.getInstance().getDate(),
                                               "yyyy/MM/dd"));
        printData.setData("printUser", "TEXT", Operator.getName());
        this.openPrintWindow("%ROOT%\\config\\prt\\SYSStopAdvice.jhw",
                             printData);
	}
	public void onClear(){
		((TRadioButton)this.getComponent("ALL")).setSelected(true);
		((TRadioButton)this.getComponent("MEDICINE")).setSelected(false);
		((TRadioButton)this.getComponent("NOT_MEDICINE")).setSelected(false);
		this.table.removeRowAll();
		
	}
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = table.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);

				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
		
	}
	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}
	/**
	 * vectoryת��param
	 */
	@SuppressWarnings("rawtypes")
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
}
