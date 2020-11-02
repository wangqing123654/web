/**
 * 
 */
package com.javahis.ui.opb;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 
 * </p>
 * 
 * <p>
 * Description: 门诊收入明细查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author wu 2012-7-25下午16:59:55
 * @version 1.0
 */
public class OPBIncomeDetailControl extends TControl {
	private TTable table;
	private int sortColumn = -1;
	private boolean ascending = false;
	private Compare compare = new Compare();
	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}
	/**
	 * 初始化页面
	 */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		table = (TTable) getComponent("TABLE");	
		// 初始化查询区间
		this.setValue("E_DATE",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 23:59:59");
		this.setValue("S_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		// 为表单添加监听器，为排序做准备。
//		addListener(table);
	}
	
	/**
	 * 查询
	 */
	public void onQuery() {
		String date_s = getValueString("S_DATE");
		String date_e = getValueString("E_DATE");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("请输入需要查询的时间范围");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		String sql = 
			" SELECT   A.BILL_DATE, B.DEPT_CHN_DESC DEPT, C.DEPT_CHN_DESC EXE_DEPT, D.CHN_DESC," +
			" SUM (AR_AMT) AR_AMT " +
			" FROM (SELECT TO_CHAR (BILL_DATE, 'YYYY/MM/DD') BILL_DATE, AR_AMT," +
			" REXP_CODE, DEPT_CODE, EXEC_DEPT_CODE" +
			" FROM OPD_ORDER" +
			" WHERE BILL_DATE BETWEEN TO_DATE ('" + date_s + "','YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + date_e + "','YYYYMMDDHH24MISS')) A," +
			" SYS_DEPT B," +
			" SYS_DEPT C," +
			" (SELECT ID, CHN_DESC" +
			" FROM SYS_DICTIONARY" +
			" WHERE GROUP_ID = 'SYS_CHARGE') D" +
			" WHERE A.DEPT_CODE = B.DEPT_CODE" +
			" AND A.EXEC_DEPT_CODE = C.DEPT_CODE" +
			" AND A.REXP_CODE = D.ID " +
			" GROUP BY A.BILL_DATE, B.DEPT_CHN_DESC, C.DEPT_CHN_DESC, D.CHN_DESC" +
			" UNION ALL " +
			" SELECT A.PRINT_DATE BILL_DATE, B.DEPT_CHN_DESC, B.DEPT_CHN_DESC," +
			" '挂号费' CHN_DESC, SUM(A.REG_FEE_REAL) AR_AMT" +
			" FROM SYS_DEPT B," +
			" REG_PATADM C," +
			" (SELECT   TO_CHAR (PRINT_DATE, 'YYYY/MM/DD') PRINT_DATE, CASE_NO, SUM (REG_FEE_REAL) REG_FEE_REAL," +
			" SUM (CLINIC_FEE_REAL) CLINIC_FEE_REAL" +
			" FROM BIL_REG_RECP" +
			" WHERE PRINT_DATE BETWEEN TO_DATE ('" + date_s + "','YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + date_e + "','YYYYMMDDHH24MISS')" +
			" GROUP BY PRINT_DATE, CASE_NO) A" +
			" WHERE A.CASE_NO = C.CASE_NO AND C.REALDEPT_CODE = B.DEPT_CODE" +
			" GROUP BY A.PRINT_DATE, B.DEPT_CHN_DESC, B.DEPT_CHN_DESC " +
			" UNION ALL " +
			" SELECT A.PRINT_DATE BILL_DATE, B.DEPT_CHN_DESC, B.DEPT_CHN_DESC," +
			" '诊察费' CHN_DESC, SUM(A.CLINIC_FEE_REAL) AR_AMT" +
			" FROM SYS_DEPT B," +
			" REG_PATADM C," +
			" (SELECT   TO_CHAR (PRINT_DATE, 'YYYY/MM/DD') PRINT_DATE, CASE_NO, SUM (REG_FEE_REAL) REG_FEE_REAL," +
			" SUM (CLINIC_FEE_REAL) CLINIC_FEE_REAL" +
			" FROM BIL_REG_RECP" +
			" WHERE PRINT_DATE BETWEEN TO_DATE ('" + date_s + "','YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + date_e + "','YYYYMMDDHH24MISS')" +
			" GROUP BY PRINT_DATE, CASE_NO) A" +
			" WHERE A.CASE_NO = C.CASE_NO AND C.REALDEPT_CODE = B.DEPT_CODE" +
			" GROUP BY A.PRINT_DATE, B.DEPT_CHN_DESC, B.DEPT_CHN_DESC" +
			" ORDER BY BILL_DATE";
		TParm result = new TParm( TJDODBTool.getInstance().select(sql));
		if(result.getErrCode() < 0){
			return;
		}
		this.table.setParmValue(result);
	}
	/**
	 * 汇出Excel
	 */
	public void onExport() {
		// 得到UI对应控件对象的方法
		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "门诊收入明细表");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		initPage();
		table.removeRowAll();
	}
	/**
	 * 得到 Vector 值
	 * @param parm TParm
	 * @param group String
	 * @param names String
	 * @param size int
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
	 * 转换parm中的列
	 * @param columnName String[]
	 * @param tblColumnName String
	 * @return int
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
	 * vectory转成param
	 * @param vectorTable Vector
	 * @param parmTable TParm
	 * @param columnNames String
	 */
	    //================start===============
//	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
//			String columnNames) {
	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
    	//================end=================
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
		//================start===============
		//table.setParmValue(parmTable);
		return parmTable;
		//================end=================
		// System.out.println("排序后===="+parmTable);
	}
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// 表格中parm值一致,
				// 1.取paramw值;
//				TParm tableData = CLP_BSCINFO.getParmValue();
				TParm tableData = table.getShowParmValue();
				//=====标题行 和 "总计"行  不参与处理处理======
//				TParm titRowParm=new TParm();//记录标题行
//				titRowParm.addRowData(table.getParmValue(), 0);
				TParm totRowParm=new TParm();//记录“总计”行
				totRowParm.addRowData(table.getParmValue(), tableData.getCount()-1);
				int rowCount=tableData.getCount();//数据的总行数（包括小计行和总计行）
//				tableData.removeRow(0);//去除第一行（标题行）
				tableData.removeRow(tableData.getCount()-1);//去除最后一行(总计行)
				//=========================================
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);

				// 3.根据点击的列,对vector排序
				// 表格排序的列名;
				String tblColumnName = table.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);
				TParm lastResultParm=new TParm();//记录最终结果
				TParm tmpParm=cloneVectoryParam(vct, new TParm(), strNames);
//				lastResultParm.addRowData(titRowParm, 0);//加入标题行
				for (int k = 0; k < tmpParm.getCount(); k++) {
					lastResultParm.addRowData(tmpParm, k);//加入中间数据
				}
				lastResultParm.addRowData(totRowParm, 0);//加入总计行
				lastResultParm.setCount(rowCount);
//				System.out.println("lastResultParm:\r\n"+lastResultParm+"\r\n\r\n");////////////////////
				table.setParmValue(lastResultParm);
				//getTMenuItem("save").setEnabled(false);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
}
