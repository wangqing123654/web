
package com.javahis.ui.mro;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.mro.MROBorrowTool;
import jdo.mro.MROLendTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.ui.sys.LEDMROUI;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 病历临时出库确认
 * 
 * <p>
 * Description:
 * 
 * <p>
 * Copyright:
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangbin 2014.07.23
 * @version 4.0
 */
public class MROReadyOutControl extends TControl {

	private String pageParam; // 页面传参标识(REG:挂号,ADM:住院)
	private String messageText;// 弹出信息
	private TTable showTable;
	private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;
	
	/**
	 * 跑马灯
	 */
	private LEDMROUI ledMroUi;

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		this.pageParam = (String) this.getParameter();
		if (StringUtils.isEmpty(pageParam)) {
			this.messageBox("页面传参错误");
			return;
		}
		
		this.onInitPage();
		
		// 页面点击列实现排序
		this.addListener(showTable);
	}

	/**
	 * 初始化页面
	 */
	public void onInitPage() {
		showTable = (TTable) this.getComponent("TABLE");
		callFunction("UI|print|setEnabled", false);
		String now = SystemTool.getInstance().getDate().toString().substring(0,
				10).replace('-', '/');
		
		// 默认显示第二天日期
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(SystemTool.getInstance().getDate());
		calendar.add(Calendar.DATE, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String tommorwDate = sdf.format(calendar.getTime());
		
		this.setValue("S_DATE", now); // 开始时间
		this.setValue("E_DATE", tommorwDate); // 结束时间
		
		// 弹出信息
		if ("REG".equals(this.pageParam)) {
			messageText = "挂号就诊";
			
			// 显示跑马灯
			openLEDMROUI();
		} else {
			messageText = "住院";
		}
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		clearValue("SELECT_ALL");
		
		// 待出库确认状态
		if (getRadioButton("CONFIRM_STATUS_NO").isSelected()) {
			callFunction("UI|save|setEnabled", true);
			callFunction("UI|print|setEnabled", false);
		} else if (getRadioButton("CONFIRM_STATUS_YES").isSelected()) {
			callFunction("UI|save|setEnabled", false);
			callFunction("UI|print|setEnabled", true);
		}
		
		// 查询待出库的病历数据
		TParm resultParm = MROBorrowTool.getInstance().queryMroReg(this.getQueryParm());
		if (resultParm.getCount() <= 0) {
			showTable.setParmValue(new TParm());
			messageBox("查无数据");
			return;
		}
		
		showTable.setParmValue(resultParm);
		
		// 只有在库的数据才可以勾选
		String lockRows = "";
		for (int i = 0; i < showTable.getRowCount(); i++) {
			if (!"1".equals(showTable.getParmValue().getValue("IN_FLG", i))
					// add by wangbin 20141028 待出库界面增加取消状态列，已取消的也不可勾选
					|| showTable.getParmValue().getBoolean("CANCEL_FLG", i)) {
				lockRows = lockRows + i;
				if (i < showTable.getRowCount() - 1) {
					lockRows = lockRows + ",";
				}
			}
			
			// 已取消的数据行背景颜色设置为黄色
			if (showTable.getParmValue().getBoolean("CANCEL_FLG", i)) {
				showTable.setRowColor(i, new Color(255, 255, 0));
			} else {
				// 否则保留原色
				showTable.removeRowColor(i);
			}
		}
		showTable.setLockRows(lockRows);
	}

	/**
	 * 获取查询条件数据
	 * 
	 * @return
	 */
	private TParm getQueryParm() {
		TParm parm = new TParm();
		parm.setData("S_DATE", getValueString("S_DATE").substring(0, 10)
				.replace("-", "")
				+ "000000");
		parm.setData("E_DATE", getValueString("E_DATE").substring(0, 10)
				.replace("-", "")
				+ "235959");
		// 时段
		if (getValueString("SESSION_CODE").length() > 0) {
			parm.setData("SESSION_CODE", getValueString("SESSION_CODE"));
		}
		
		// 科室编码
		if (getValueString(this.pageParam + "_DEPT_CODE").length() > 0) {
			parm.setData("DEPT_CODE", getValueString(this.pageParam + "_DEPT_CODE"));
		}
		
		// 医生编码
		if (getValueString(this.pageParam + "DR_CODE").length() > 0) {
			parm.setData("DR_CODE", getValueString(this.pageParam + "DR_CODE"));
		}
		
		// 状态
		String status = ((TComboBox)this.getComponent("STATUS")).getSelectedText();
		if (status.length() > 0) {
			parm.setData("STATUS", status);
		}
		
		// 病案号
		if (getValueString("MR_NO").length() > 0) {
			parm.setData("MR_NO", getValueString("MR_NO"));
		}
		
		// 待出库确认状态
		if (getRadioButton("CONFIRM_STATUS_NO").isSelected()) {
			parm.setData("CONFIRM_STATUS", "0");
		} else if (getRadioButton("CONFIRM_STATUS_YES").isSelected()) {
			parm.setData("CONFIRM_STATUS", "1");
		}
		
		// 挂号
		if ("REG".equals(this.pageParam)) {
			parm.setData("ADM_TYPE", "O");
		} else {
			// 住院
			parm.setData("ADM_TYPE", "I");
		}
		
		return parm;
	}

	/**
	 * 
	 * 保存
	 */
	public void onSave() {
		TParm parm = showTable.getParmValue();
		
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("无保存数据");
			return;
		}
		
		// 强制失去编辑焦点
		if (showTable.getTable().isEditing()) {
			showTable.getTable().getCellEditor().stopCellEditing();
		}
		
		// 插入病案借阅管理表前准备数据
		int checkDataCount = this.setDataForQueue(parm);
		
		if (checkDataCount < 0) {
			return;
		} else if (checkDataCount == 0) {
			// 根据返回值判断是否全都没勾选
			this.messageBox("请勾选需要保存的在库病历数据");
			return;
		}
		
		TParm checkParm = new TParm();
		// 计算已勾选的数据中有多少已经取消问诊
		int checkInt = 0;
		// 当前面板上显示的所有数据
		List<String> showRegMroNoList = new ArrayList<String>();
		// 门诊挂号才有跑马灯
		if ("REG".equals(this.pageParam)) {
			showRegMroNoList = ledMroUi.getAllLEDData();
		}
		// 统计应该从跑马灯中去掉的数据
		List<String> needRemoveRegNoList = new ArrayList<String>();
		
		// 验证当前勾选的数据是否已经取消预约
		for (int i = parm.getCount() - 1; i > -1; i--) {
			if (parm.getBoolean("FLG", i)) {
				checkParm = MROBorrowTool.getInstance().queryMroRegCancel(parm.getRow(i));
				
				if (checkParm.getErrCode() < 0) {
					this.messageBox("系统错误");
					err("ERR:" + checkParm.getErrCode() + checkParm.getErrText()
							+ checkParm.getErrName());
					return;
				}
				
				if (checkParm.getCount() > 0) {
					checkInt = checkInt + 1;
					this.messageBox("病案号为"+parm.getValue("MR_NO", i)+"的病患【"+parm.getValue("PAT_NAME", i)+"】已取消" + messageText);
				}
				
				// add by wangbin 20141029 当前跑马灯中有的，并且被勾选的
				if (showRegMroNoList.contains(parm.getValue("MRO_REGNO", i))) {
					needRemoveRegNoList.add(parm.getValue("MRO_REGNO", i));
				}
			} else {
				// add by wangbin 20141029 当前跑马灯中有的
				if (showRegMroNoList.contains(parm.getValue("MRO_REGNO", i))) {
					// 不在库的或者已取消的也需要从跑马灯中移除
					if (!StringUtils.equals("1", parm.getValue("IN_FLG", i))
							|| parm.getBoolean("CANCEL_FLG", i)) {
						needRemoveRegNoList.add(parm.getValue("MRO_REGNO", i));
					} else {
						// 没被选中的面板上存在的可保存数据也需要即时验证取消状态
						checkParm = MROBorrowTool.getInstance().queryMroRegCancel(parm.getRow(i));
						
						if (checkParm.getCount() > 0) {
							needRemoveRegNoList.add(parm.getValue("MRO_REGNO", i));
						}
					}
				}
				
				parm.removeRow(i);
			}
		}
		
		// 如果勾选的数据全部取消问诊，则重新刷新数据
		if (parm.getCount() <= checkInt) {
			this.onQuery();
			if ("REG".equals(this.pageParam)) {
				// 保存后刷新跑马灯
				this.getLedMroRemoveMroRegNo(needRemoveRegNoList);
			}
			return;
		}
		
		TParm result = TIOM_AppServer.executeAction(
				"action.mro.MROBorrowAction", "insertQueue", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			this.messageBox("E0001");
			return;
		}
		this.messageBox("P0001");
		clearValue("SELECT_ALL");
		// 保存成功后回显数据
		this.onQuery();
		
		if ("REG".equals(this.pageParam)) {
			// 保存后刷新跑马灯
			this.getLedMroRemoveMroRegNo(needRemoveRegNoList);
		}
	}

	/**
	 * 全选复选框选中事件
	 */
	public void onCheckSelectAll() {
		if (showTable.getRowCount() < 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		if (getCheckBox("SELECT_ALL").isSelected()) {
			for (int i = 0; i < showTable.getRowCount(); i++) {
				//只有在库的病历才能借阅
				if ("1".equals(showTable.getParmValue().getValue("IN_FLG", i))
						// add by wangbin 20141028 待出库界面增加取消状态列，在库未取消的才可以勾选
						&& !showTable.getParmValue()
								.getBoolean("CANCEL_FLG", i)) {
					showTable.setItem(i, "FLG", "Y");
				}
			}
		} else {
			for (int i = 0; i < showTable.getRowCount(); i++) {
				showTable.setItem(i, "FLG", "N");
			}
		}
	}
	
	/**
	 * 根据病案号查询
	 */
	public void onQueryByMrNo() {
		// 取得病案号
		String mrNo = this.getValueString("MR_NO").trim();
		if (StringUtils.isEmpty(mrNo)) {
			return;
		} else {
			Pat pat = Pat.onQueryByMrNo(mrNo);
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
			this.setValue("PAT_NAME", pat.getName());
			this.onQuery();
		}
	}
	
	/**
	 * 为插入病案借阅管理表准备数据
	 * 
	 * @param parm
	 *            parm数据
	 * @return 是否有勾选要保存的数据
	 */
	private int setDataForQueue(TParm parm) {
		// 返回值(-1_系统错误,0_无勾选数据,1_有勾选数据)
		int rntResult = 0;
		// 操作人员
		String optUser = Operator.getID();
		// 操作端末
		String optTerm = Operator.getIP();
		TParm lendParm = new TParm();
		
		// 设置借阅类型
		if ("REG".equals(this.pageParam)) {
			lendParm.setData("LEND_TYPE", "O");
		} else {
			lendParm.setData("LEND_TYPE", "I");
		}
		
		// 门诊挂号、住院也可以理解为一种借阅
		lendParm = MROLendTool.getInstance().selectdata(lendParm);
		
		if (lendParm.getErrCode() < 0) {
			this.messageBox("借阅原因字典查询错误");
			err("ERR:" + lendParm.getErrCode() + lendParm.getErrText());
			return -1;
		}
		
		if (lendParm.getCount() <= 0) {
			this.messageBox("缺少借阅原因字典数据");
			return -1;
		}
		
		for (int i = 0; i < parm.getCount(); i++) {
			if (rntResult == 0 && parm.getBoolean("FLG", i)) {
				rntResult = 1;
			}
			
			// 借阅号
			parm.addData("QUE_SEQ", "");
			// 待出库日期
			parm.addData("QUE_DATE", parm.getValue("ADM_DATE", i).substring(0, 10));
			// 住院号
			parm.addData("IPD_NO", parm.getData("IPD_NO", i));
			// 院区
			parm.addData("ADM_HOSP", parm.getData("CURT_LOCATION", i));
			// 调阅单位
			parm.addData("REQ_DEPT", parm.getData("DEPT_CODE", i));
			// 借阅人
			parm.addData("MR_PERSON", parm.getData("DR_CODE", i));
			// 出库状况
			parm.addData("ISSUE_CODE", "0");

			// 借阅原因
			parm.addData("LEND_CODE", lendParm.getValue("LEND_CODE", 0));
			// 取消否
			parm.addData("CAN_FLG", "N");
			// 就诊号
			parm.addData("CASE_NO", parm.getData("CASE_NO", i));
			// 待出库院区
			parm.addData("QUE_HOSP", parm.getData("CURT_HOSP", i));
			// 归还入库日期
			parm.addData("IN_DATE", "");
			// 归还入库人员
			parm.addData("IN_PERSON", "");
			// 应归还日期
			parm.addData("RTN_DATE", "");
			// 应完成日
			parm.addData("DUE_DATE", "");
			
			if ("REG".equals(this.pageParam)) {
				// 出库方式(0_挂号出库, 1_住院出库, 2_借阅出库)
				parm.addData("OUT_TYPE", "0");
			} else {
				parm.addData("OUT_TYPE", "1");
			}
			
			// 操作人员
			parm.addData("OPT_USER", optUser);
			// 操作端末
			parm.addData("OPT_TERM", optTerm);
			// 借阅案卷号
			parm.addData("LEND_BOX_CODE", parm.getData("BOX_CODE", i));
			// 借阅册号
			parm.addData("LEND_BOOK_NO", parm.getData("BOOK_NO", i));
			// 实际出库时间
			parm.addData("OUT_DATE", "");
		}
		
		return rntResult;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		String clearValue = "SESSION_CODE;MR_NO;PAT_NAME;STATUS;SELECT_ALL;"
				+ this.pageParam + "_DEPT_CODE;" + this.pageParam + "_DR_CODE";
		clearValue(clearValue);
		showTable.setParmValue(new TParm());
	}
	
	/**
	 * 打印待出库病案报表
	 */
	public void onPrint() {
		TParm parm = showTable.getParmValue();
		
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("无打印数据");
			return;
		}
		
		// 强制失去编辑焦点
		if (showTable.getTable().isEditing()) {
			showTable.getTable().getCellEditor().stopCellEditing();
		}
		
		int dataLen = parm.getCount();
		
		// 打印用Parm
		TParm printParm = new TParm();
		// 打印数据
		TParm printData = new TParm();
		// 打印用案卷号
		String boxCode = "";
		int printDataLen = 0;
		String admTypeSql = "";
		String deptCodeSql = "";
		String drCodeSql = "";
		String admDate = "";
		TParm resultAdmType = new TParm();
		TParm resultDeptCode = new TParm();
		TParm resultDrCode = new TParm();
		for (int i = 0; i < dataLen; i++) {
			if (parm.getBoolean("FLG", i)) {
				boxCode = parm.getValue("BOX_CODE", i) + "-" + parm.getValue("ADM_TYPE", i) + "-" + parm.getValue("BOOK_NO", i);
				// 案卷号
				printData.addData("BOX_CODE", boxCode);
				// 病案号
				printData.addData("MR_NO", parm.getValue("MR_NO", i));
				// 姓名
				printData.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
				
				//病案类型
				admTypeSql = "SELECT CHN_DESC FROM (SELECT ID ,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID ='SYS_ADMTYPE') " +
							"WHERE ID = '"+parm.getValue("ADM_TYPE",i)+"'";
				
				resultAdmType =  new TParm(TJDODBTool.getInstance().select(admTypeSql));
				printData.addData("ADM_TYPE", resultAdmType.getValue("CHN_DESC",0));
				
				//借阅科室
				deptCodeSql = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE = '"+
								parm.getValue("DEPT_CODE",i)+"'";
				resultDeptCode = new TParm(TJDODBTool.getInstance().select(deptCodeSql));
				printData.addData("DEPT_CODE", resultDeptCode.getValue("DEPT_CHN_DESC",0));
				
				//借阅人
				drCodeSql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+parm.getValue("DR_CODE",i)+"'";
				resultDrCode = new TParm(TJDODBTool.getInstance().select(drCodeSql));
				
				printData.addData("DR_CODE", resultDrCode.getValue("USER_NAME",0));
				
				//借阅日期
				admDate = parm.getValue("ADM_DATE",i);
//				System.out.println("admDate = " + admDate);
//				if(null != admDate && "".equals(admDate) && admDate.length() > 0){
					admDate = admDate.substring(0, 11);
			
//				}
				
				printData.addData("ADM_DATE", admDate);
				
				printDataLen = printDataLen + 1;
			}
		}
		
		if (printDataLen < 1) {
			this.messageBox("请勾选打印数据");
			return;
		}
		
		printData.setCount(printDataLen);
		
		printData.addData("SYSTEM", "COLUMNS", "BOX_CODE");
		printData.addData("SYSTEM", "COLUMNS", "MR_NO");
		printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		printData.addData("SYSTEM", "COLUMNS", "ADM_TYPE");
		printData.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
		printData.addData("SYSTEM", "COLUMNS", "DR_CODE");
		printData.addData("SYSTEM", "COLUMNS", "ADM_DATE");
		
		printParm.setData("PRINT_TABLE", printData.getData());
		
		this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROReadyOutPrint.jhw", printParm);
	}
	
	/**
	 * 汇出Excel
	 */
	public void onExport() {
		TParm parm = showTable.getParmValue();
		if (null == parm || parm.getCount("MRO_REGNO") <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		
		if (showTable.getRowCount() > 0) {
			String title = "";
			// 门诊挂号
			if ("REG".equals(pageParam)) {
				title = "挂号病历待出库确认";
			} else {
				title = "住院病历待出库确认";
			}
			
			TParm exportParm = showTable.getShowParmValue(); 
			exportParm.setData("TITLE", title);
			// 导出的Excel内容中去掉勾选框这一列
			exportParm.setData("HEAD", showTable.getHeader().replaceAll("选,30,boolean;", ""));
	        String[] header = showTable.getParmMap().split(";");
	        for (int i = 1; i < header.length; i++) {
	        	exportParm.addData("SYSTEM", "COLUMNS", header[i]);
	        }
	        TParm[] execleTable = new TParm[]{exportParm};
	        ExportExcelUtil.getInstance().exeSaveExcel(execleTable, title);
		}
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
				// 转换出用户想排序的列和底层数据的列，然后判断
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = showTable.getParmValue();
				tableData.removeGroupData("SYSTEM");

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
				String tblColumnName = showTable.getParmMap(
						sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);

				// 将排序后的vector转成parm;
				TParm lastResultParm = new TParm();// 记录最终结果
				lastResultParm = cloneVectoryParam(vct, new TParm(), strNames);// 加入中间数据

				table.setParmValue(lastResultParm);
			}
		});
	}

	/**
	 * 加入排序功能
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
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
	 * 得到 Vector 值
	 * 
	 * @param parm
	 *            parm
	 * @param group
	 *            组名
	 * @param names
	 *            "ID;NAME"
	 * @param size
	 *            最大行数
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
	 * vectory转成param
	 */
	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
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
		return parmTable;

	}


	/**
	 * 得到CheckBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}
	
	/**
	 * 得到RadioButton对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}
	
	/**
	 * 病案待出库界面跑马灯双击操作
	 * 
	 * @param mroRegNo
	 */
	public void openInwCheckWindow(String mroRegNo) {
		getRadioButton("CONFIRM_STATUS_NO").setSelected(true);
		onQuery();
	}

	/**
	 * 跑马灯显示
	 */
	public void openLEDMROUI() {
		Component com = (Component) getComponent();
		TParm parm = new TParm();
		parm.setData("MRO", "ODO");
		while ((com != null) && (!(com instanceof Frame))) {
			com = com.getParent();
		}
		this.ledMroUi = new LEDMROUI((Frame) com, this, parm);
		this.ledMroUi.openWindow();
	}

	/**
	 * 跑马灯关闭
	 */
	public boolean onClosing() {
		if (null != ledMroUi) {
			this.ledMroUi.close();
		}
		return true;
	}

	/**
	 * 移除跑马灯数据
	 */
	public void getLedMroRemoveMroRegNo(List<String> mroRegNoList) {
		TParm parm = new TParm();
		
		for (int i = 0; i < mroRegNoList.size(); i++) {
			parm.setData("MRO_REGNO", mroRegNoList.get(i));
			if (ledMroUi != null) {
				ledMroUi.removeMessage(parm);
			}
		}
	}
}
