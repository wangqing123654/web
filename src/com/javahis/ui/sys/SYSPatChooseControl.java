package com.javahis.ui.sys;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
/**
 * <p>Title: ����ѡ�����</p>
 *
 * <p>Description: ����ѡ�����</p>
 *
 * <p>Copyright: javahis 20090922</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui
 * @version 1.0
 */
public class SYSPatChooseControl extends TControl {
	TTable table;
	//===zhangp 20120813 start
	private Compare compare = new Compare();
	private boolean ascending = false;
	private int sortColumn = -1;
	//===zhangp 20120813 end
	/**
	 * ��ʼ���¼�
	 */
	public void onInit() {
		super.onInit();
		initComponent();
		initData();
		onClear();
	}
	/**
	 * ��ʼ������
	 */
	private void initComponent(){
		table=(TTable)this.getComponent("TABLE");
		addListener(table);//===zhangp 20120813
	}
	/**
	 * ��ʼ������
	 */
	private void initData(){
		Object obj=this.getParameter();
		if(obj instanceof TParm){
			TParm parm=(TParm)obj;
			//System.out.println("parm============="+parm);
			table.setParmValue(parm);
		}
	}
	/**
	 * ���
	 */
	public void onClear(){
		this.clearValue("PAT_NAME;MR_NO;IDNO;BIRTH_DATE;SEX_CODE;RESID_ADDRESS;ID_TYPE;CURRENT_ADDRESS");  //add by huangtt 20131106 PRESENTADDRESS;IDTYPE
	}
	/**
	 * �е���¼�
	 */
	public void onTableClicked(){
		int row=table.getSelectedRow();
		TParm parm=table.getParmValue();
		TParm parmRow=parm.getRow(row);
		this.setValueForParm("PAT_NAME;MR_NO;IDNO;BIRTH_DATE;SEX_CODE;RESID_ADDRESS;ID_TYPE;CURRENT_ADDRESS", parmRow);  //add by huangtt 20131106 PRESENTADDRESS;IDTYPE
	}
	/**
	 * ˫���¼�
	 */
	public void onTableDoubleClicked(){
		int row=table.getSelectedRow();
		TParm parm=table.getParmValue();
		TParm parmRow=parm.getRow(row);
		this.setReturnValue(parmRow);
		this.closeWindow();
	}
	
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
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
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

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
	 * vectoryת��param
	 */
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
		// System.out.println("�����===="+parmTable);

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
}
