package com.javahis.ui.ins;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * 
 * <p>
 * Title:医保住院医疗费申请支付信息导出EXCLE
 * </p>
 * 
 * <p>
 * Description:医保住院医疗费申请支付信息导出EXCLE
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author pangb 2012-8-8
 * @version 4.0
 */
public class INSIbsMaterialSearchControl extends TControl{
	private TTable table; // 病患基本信息列表
	//======排序
	private Compare compare = new Compare();
	private int sortColumn = -1;
	private boolean ascending = false;
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE"); // 病患基本信息列表
		this.setValue("OUT_DATE_START", SystemTool.getInstance().getDate());
		this.setValue("OUT_DATE_END", SystemTool.getInstance().getDate());
		this.setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		addListener(table);
	}
	private String sql ="SELECT A.MR_NO,C.IPD_NO, A.PAT_NAME, E.CHN_DESC,"+
	       "A.PAT_AGE, F.CTZ_DESC,"+
	       "TO_CHAR (A.IN_DATE, 'YYYYMMDD') AS IN_DATE,"+
	       "TO_CHAR (A.DS_DATE, 'YYYYMMDD') AS DS_DATE,"+
	      "CASE WHEN TRUNC (A.DS_DATE, 'DD') - TRUNC (A.IN_DATE, 'DD') = 0 THEN 1 "+
	      "ELSE TRUNC (A.DS_DATE, 'DD') - TRUNC (A.IN_DATE, 'DD') END AS IN_DAYS,"+
	      "A.DEPT_DESC, A.PHA_AMT, A.EXM_AMT,"+
	      "A.TREAT_AMT, A.OP_AMT, A.BED_AMT,"+
	      "A.MATERIAL_AMT, A.BLOODALL_AMT,"+
	      "A.BLOOD_AMT, A.OTHER_AMT,"+
	      "A.PHA_AMT+ A.EXM_AMT+ A.TREAT_AMT+ A.OP_AMT+ A.BED_AMT+ A.MATERIAL_AMT"+
	      "+ A.BLOODALL_AMT+ A.BLOOD_AMT+ A.OTHER_AMT AS SUM_AMT,"+
	      "A.OWN_AMT,"+
	      "A.ADD_AMT,"+
	      "A.RESTART_STANDARD_AMT+ A.STARTPAY_OWN_AMT+ A.OWN_AMT+ A.PERCOPAYMENT_RATE_AMT"+
	      "+ A.ADD_AMT+ A.INS_HIGHLIMIT_AMT AS OWN_SUM_AMT,"+
	      "A.RESTART_STANDARD_AMT+ A.STARTPAY_OWN_AMT+ A.OWN_AMT + A.PERCOPAYMENT_RATE_AMT "+
	      "+A.ADD_AMT + A.INS_HIGHLIMIT_AMT - A.ACCOUNT_PAY_AMT - A.ARMYAI_AMT  AS SJZFJE,"+
	      "A.NHI_PAY,A.NHI_PAY_REAL,A.NHI_COMMENT AS HOSP_APPLY_AMT,A.NHI_COMMENT,A.ACCOUNT_PAY_AMT ,"+
	      "A.ACCOUNT_PAY_AMT AS GRZHZF,"+
	      "A.NHI_PAY_REAL+ A.TOT_PUBMANADD_AMT+ A.ARMYAI_AMT+ A.ACCOUNT_PAY_AMT AS HJZFJE,"+
	      "K.OP_DESC, (SELECT CASE WHEN SUM(L.TOTAL_AMT) IS NULL THEN 0 ELSE SUM(L.TOTAL_AMT) END "+
	  	  "FROM INS_IBS_ORDER  L, SYS_FEE M "+
		  "WHERE  L.YEAR_MON=A.YEAR_MON AND  L.CASE_NO=A.CASE_NO "+
		  "AND L.ORDER_CODE=M.ORDER_CODE  AND  M.OWN_PRICE>=100  "+
		  "AND  M.ORDER_CAT1_CODE ='PHA_W' "+ 
		  "AND  M.ACTIVE_FLG ='Y' ) AS SPHA, (SELECT CASE WHEN SUM(N.TOTAL_AMT) IS NULL THEN 0 ELSE SUM(N.TOTAL_AMT) END "+
	  	  "FROM INS_IBS_ORDER  N, SYS_FEE O "+
		  "WHERE  N.YEAR_MON=A.YEAR_MON AND  N.CASE_NO=A.CASE_NO "+
		  "AND N.ORDER_CODE=O.ORDER_CODE  AND  O.OWN_PRICE>=500  "+
		  "AND  O.ORDER_CAT1_CODE ='MAT' "+ 
		  "AND  O.ACTIVE_FLG ='Y' ) AS SMATERIAL_AMT "+
	   "FROM INS_IBS A LEFT JOIN INS_ADM_CONFIRM B ON A.CONFIRM_NO = B.CONFIRM_NO "+
	       "LEFT JOIN ADM_INP C "+
	       "ON  A.CASE_NO = C.CASE_NO "+
	       "LEFT JOIN SYS_OPERATOR D "+
	       "ON C.VS_DR_CODE = D.USER_ID "+
	       "LEFT JOIN SYS_DICTIONARY E ON A.SEX_CODE = E.ID "+
	       "AND E.GROUP_ID = 'SYS_SEX' "+
	       "INNER JOIN SYS_CTZ F "+
	       "ON B.HIS_CTZ_CODE = F.CTZ_CODE " +   
	       "LEFT JOIN SYS_DICTIONARY G "+
	       "ON A.INSBRANCH_CODE = G.ID AND G.GROUP_ID = 'INS_FZX' "+
	       "LEFT JOIN SYS_DICTIONARY H "+
	       "ON A.SPEDRS_CODE = H.ID AND H.GROUP_ID = 'INS_MTLBA' "+
	       "LEFT JOIN SYS_DICTIONARY I "+
	       "ON I.GROUP_ID = 'INS_JYLB' AND I.ID = A.ADM_CATEGORY "+
	       "LEFT JOIN SYS_DICTIONARY J "+
	       "ON J.GROUP_ID = 'SP_PRESON_TYPE' AND J.ID = B.SPECIAL_PAT "+
	       "LEFT JOIN MRO_RECORD_OP K ON K.MAIN_FLG = 'Y' "+
	       "AND K.CASE_NO = A.CASE_NO ";
	
	//病案号,100;病患名称,100;性别,100;年龄,100;身份,100;入院日期,100;
	//出院日期,100;住院天数,100;科室,100;药品费,100;检查费,100;治疗费,100;
	//手术费,100;床位费,100;医用材料,100;输全血,100;成分输血,100;其他,100;合计,100;
	//自费金额,100;增负金额,100;个人合计,100;实际自负金额,100;医保基金_医疗机构申请金额,100;
	//医保基金_社保支付金额,100;大额救助_医疗机构申请金额,100;大额救助_社保建议支付金额,100;
	//个人账户支付_医疗机构申请金额,100;个人账户支付_社保支付金额,100;合计支付_社保支付合计金额,100;术式,100
	/**
	 * 查询方法
	 */
	public void onQuery() {
		if (null == this.getValue("OUT_DATE_START")
				|| this.getValue("OUT_DATE_START").toString().length() <= 0
				|| null == this.getValue("OUT_DATE_END")
				|| this.getValue("OUT_DATE_END").toString().length() <= 0) {

			if (null == this.getValue("OUT_DATE_START")
					|| this.getValue("OUT_DATE_START").toString().length() <= 0) {
				this.grabFocus("OUT_DATE_START");
			}
			if (null == this.getValue("OUT_DATE_END")
					|| this.getValue("OUT_DATE_END").toString().length() <= 0) {
				this.grabFocus("OUT_DATE_END");
			}
			this.messageBox("请输入出院日期");
			return;
		}
		StringBuffer bf = new StringBuffer();
		
		bf.append(" WHERE A.DS_DATE BETWEEN TO_DATE('").append(
				SystemTool.getInstance().getDateReplace(
						this.getValueString("OUT_DATE_START"), true)).append(
				"','YYYYMMDDHH24MISS') AND TO_DATE('").append(
				SystemTool.getInstance().getDateReplace(
						this.getValueString("OUT_DATE_END").substring(0,10), false)).append(
				"','YYYYMMDDHH24MISS')");
		if (this.getValue("REGION_CODE").toString().length() > 0) {
			bf.append(" AND C.REGION_CODE ='").append(
					this.getValue("REGION_CODE").toString()).append("'");
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql + bf));
		if (result.getErrCode() < 0) {
			this.messageBox("查询失败");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("没有需要查询的数据");
			return;
		}
		table.setParmValue(result);
	}
	/**
	 * 汇出Excel
	 */
	public void onExport() {

		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		// TTable table = (TTable) callFunction("UI|Table|getThis");
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(
					table,
					StringTool.getString((Timestamp) this
							.getValue("OUT_DATE_START"), "yyyy.MM")
							+ "住院医疗费申请支付审核表");
	}
	public void onClear(){
		this.setValue("OUT_DATE_START", SystemTool.getInstance().getDate());
		this.setValue("OUT_DATE_END", SystemTool.getInstance().getDate());
		this.setValue("REGION_CODE", Operator.getRegion());
		table.removeRowAll();
	}
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
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
				TParm tableData = table.getParmValue();
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
				String tblColumnName = table.getParmMap(sortColumn);
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
		table.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}
}
