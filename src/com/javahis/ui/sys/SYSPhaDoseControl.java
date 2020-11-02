package com.javahis.ui.sys;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.sys.Operator;
import jdo.sys.SYSPhaDoseTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;

/**
 *
 * <p>
 * Title:ҩƷ����
 * </p>
 *
 * <p>
 * Description: �����й�ҩƷ���͵�ȫ������
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author JiaoY 2008-09-5
 * @version 1.0
 */

public class SYSPhaDoseControl
    extends TControl {
    TParm data, comboldata;
    int selectRow = -1;
    //
    //private Compare compare = new Compare();
    private BILComparator compare = new BILComparator();
    private boolean ascending = false;
	private int sortColumn = -1;

    public void onInit() {
        super.onInit();
        callFunction("UI|TABLE|addEventListener", "TABLE->"
                     + TTableEvent.CLICKED, this, "onTABLEClicked");
        callFunction("UI|delete|setEnabled", false);
        onQuery();
        init();
        //
        
        addListener(getTTable("TABLE"));

    }

    /**
     * ��ʼ����ѯ&����CTRLDRUGCLASS_CODE��ѯ
     */
    public void onQuery() {
        String doseCode = this.getValueString("DOSE_CODE"); // ���ʹ���
        data = SYSPhaDoseTool.getInstance().selectdata(doseCode,
            this.getValueString("DOSE_TYPE"));
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        if (data.getCount() == 0)
            this.messageBox("E0008");
        // System.out.println("data-->" + data);
        callFunction(
            "UI|TABLE|setParmValue",
            data,
            "SEQ;DOSE_CODE;DOSE_TYPE;DOSE_CHN_DESC;ENG_DESC;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERM;PY1;PY2 ");

    }

	/**
	 * TABLE���ʱ��
	 * 
	 * @param row
	 *            int
	 */
	public void onTABLEClicked(int row) {

		// System.out.println("row=" + row);
		// callFunction("UI|CTRLDRUGCLASS_CODE|setEnabled", false);
		if (row < 0) {
			return;
		}

		// System.out.println("data" + data);
		TParm data = getTTable("TABLE").getParmValue().getRow(row);
		this
				.setValueForParm(
						"SEQ;DOSE_CODE;DOSE_TYPE;DOSE_CHN_DESC;ENG_DESC;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERM;PY1;PY2",
						data);
		/*
		 * setValueForParm(
		 * "SEQ;DOSE_CODE;DOSE_TYPE;DOSE_CHN_DESC;ENG_DESC;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERM;PY1;PY2"
		 * , data, row);
		 */
		selectRow = row;
		callFunction("UI|delete|setEnabled", true); // ɾ���ɼ�
		callFunction("UI|DOSE_CODE|setEnabled", false);
	}

    /**
     * ����
     */
    public void onSave() {
        if (selectRow == -1) {
            onInsert();
            return;
        }
        onUpdate();
    }

    /**
     * ����
     */
    public void onInsert() {
        if (!this.emptyTextCheck("DOSE_CODE"))
            return;
        if (this.messageBox("P0009", "�Ƿ�����", 2) == 0) {

            TParm parm = new TParm();
            TParm doseCode = getParmForTag("DOSE_CODE");
            TParm exist = SYSPhaDoseTool.getInstance().existsData(doseCode); // ��֤����

            if (exist.getCount() != 0) {
                this.messageBox("E0006");
                return;
            }
            parm = getParmForTag(
                "SEQ;DOSE_CODE;DOSE_CHN_DESC;ENG_DESC;DESCRIPTION;DOSE_TYPE;PY1;PY2");

            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_TERM", Operator.getIP());
            TParm result = SYSPhaDoseTool.getInstance().insertData(parm);
            if (result.getErrCode() < 0) {
                this.messageBox(result.getErrText());
                return;
            }
            this.messageBox("P0002");
            callFunction(
                "UI|TABLE|addRow",
                parm,
                "SEQ;DOSE_CODE;DOSE_TYPE;DOSE_CHN_DESC;ENG_DESC;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERM;PY1;PY2 ");

        }
        else {
            return;
        }

    }

    /**
     * �޸�
     */
    public void onUpdate() {
        TParm parm = getParmForTag(
            "SEQ;DOSE_CODE;DOSE_CHN_DESC;ENG_DESC;DESCRIPTION;DOSE_TYPE;PY1;PY2");

        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = SYSPhaDoseTool.getInstance().updataData(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }

        int row = (Integer) callFunction("UI|Table|getSelectedRow");
        if (row < 0)
            return;
        // ˢ�£�����ĩ��ĳ�е�ֵ
       //data.setRowData(row, parm);
       //callFunction("UI|Table|setRowParmValue", row, data);
       onClear();
       onQuery();

        this.messageBox("P0005");
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        if (this.messageBox("P0009", "�Ƿ�����", 2) == 0) {

            TParm parm = getParmForTag("DOSE_CODE");
            TParm result = SYSPhaDoseTool.getInstance().deleteData(parm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            int row = (Integer) callFunction("UI|Table|getSelectedRow");
            if (row < 0)
                return;
            // ɾ��������ʾ
            this.callFunction("UI|Table|removeRow", row);
            this.callFunction("UI|Table|setSelectRow", row);
            clearValue(
                "SEQ;DOSE_CODE;DOSE_CHN_DESC;ENG_DESC;DESCRIPTION;DOSE_TYPE;PY1;PY2");
            this.messageBox("P0003");
        }
        else {
            return;
        }

    }

    /**
     * ���
     */
    public void onClear() {
        clearValue("SEQ;DOSE_CODE;DOSE_TYPE;DOSE_CHN_DESC;ENG_DESC;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERM;PY1;PY2");
        // this.callFunction("UI|Table|removeRowAll");
        selectRow = -1;
        callFunction("UI|delete|setEnabled", false); // ɾ�����ɼ�
        callFunction("UI|DOSE_CODE|setEnabled", true);

    }

    /**
     * ��ƴ
     */
    public void onCode() {
        if (String.valueOf(this.getValue("DOSE_CHN_DESC")).length() < 1) {
            return;
        }
        SystemTool st = new SystemTool();
        String value = st.charToCode(String.valueOf(this
            .getValue("DOSE_CHN_DESC")));
        if (null == value || "".equals(value)) {
            return;
        }
        this.setValue("PY1", value);

    }
    
    
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
				TParm tableData = getTTable("TABLE").getParmValue();
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
				String tblColumnName = getTTable("TABLE").getParmMap(sortColumn);
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
		getTTable("TABLE").setParmValue(parmTable);
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

}
