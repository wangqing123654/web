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
 * Title:药品剂型
 * </p>
 *
 * <p>
 * Description: 处理有关药品剂型的全部数据
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
     * 初始化查询&按照CTRLDRUGCLASS_CODE查询
     */
    public void onQuery() {
        String doseCode = this.getValueString("DOSE_CODE"); // 剂型代码
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
	 * TABLE点击时间
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
		callFunction("UI|delete|setEnabled", true); // 删除可见
		callFunction("UI|DOSE_CODE|setEnabled", false);
	}

    /**
     * 保存
     */
    public void onSave() {
        if (selectRow == -1) {
            onInsert();
            return;
        }
        onUpdate();
    }

    /**
     * 新增
     */
    public void onInsert() {
        if (!this.emptyTextCheck("DOSE_CODE"))
            return;
        if (this.messageBox("P0009", "是否新增", 2) == 0) {

            TParm parm = new TParm();
            TParm doseCode = getParmForTag("DOSE_CODE");
            TParm exist = SYSPhaDoseTool.getInstance().existsData(doseCode); // 验证数据

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
     * 修改
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
        // 刷新，设置末行某列的值
       //data.setRowData(row, parm);
       //callFunction("UI|Table|setRowParmValue", row, data);
       onClear();
       onQuery();

        this.messageBox("P0005");
    }

    /**
     * 删除
     */
    public void onDelete() {
        if (this.messageBox("P0009", "是否新增", 2) == 0) {

            TParm parm = getParmForTag("DOSE_CODE");
            TParm result = SYSPhaDoseTool.getInstance().deleteData(parm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            int row = (Integer) callFunction("UI|Table|getSelectedRow");
            if (row < 0)
                return;
            // 删除单行显示
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
     * 清空
     */
    public void onClear() {
        clearValue("SEQ;DOSE_CODE;DOSE_TYPE;DOSE_CHN_DESC;ENG_DESC;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERM;PY1;PY2");
        // this.callFunction("UI|Table|removeRowAll");
        selectRow = -1;
        callFunction("UI|delete|setEnabled", false); // 删除不可见
        callFunction("UI|DOSE_CODE|setEnabled", true);

    }

    /**
     * 简拼
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
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = getTTable("TABLE").getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = getTTable("TABLE").getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTTable("TABLE").setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}
	
	
	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
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
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}

	/**
	 * 得到TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

}
