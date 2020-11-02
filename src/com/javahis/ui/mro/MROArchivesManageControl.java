package com.javahis.ui.mro;

import java.util.ArrayList;
import java.util.List;

import jdo.mro.MROBorrowTool;
import jdo.mro.MROQueueTool;
import jdo.sys.Operator;
import jdo.sys.Pat;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 病历案卷管理
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
 * @author wangbin 2014.08.29
 * @version 4.0
 */
public class MROArchivesManageControl extends TControl {

	private TTable table;
	private String storeLocation; // 当前存放位置
	private TParm parameterParm; // 页面传输参数

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		
		Object obj = this.getParameter();
		if (null != obj) {
			if (obj instanceof TParm) {
				this.parameterParm = (TParm) obj;
			}
		}
		
		this.onInitPage();
	}

	/**
	 * 初始化页面
	 */
	public void onInitPage() {
		table = (TTable) this.getComponent("TABLE");
		
		// 表格数据点击事件
		this.callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		
		// 如果没有页面传参，说明是点击菜单正常进入页面
		if (null != parameterParm) {
			((TComponent) callFunction("UI|getThis")).setTag("MROArchivesManageUI");
			this.setValue("MR_NO", parameterParm.getValue("MR_NO"));
			getComboBox("ADM_TYPE").setSelectedID(parameterParm.getValue("ADM_TYPE"));
			this.onQuery();
		}
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		// 查询待出库的病历数据
		TParm resultParm = MROBorrowTool.getInstance().queryMroMrv(this.getQueryParm());
		if (resultParm.getCount() <= 0) {
			table.setParmValue(new TParm());
			messageBox("查无数据");
			return;
		}
		
		table.setParmValue(resultParm);
	}

	/**
	 * 获取查询条件数据
	 * 
	 * @return
	 */
	private TParm getQueryParm() {
		TParm parm = new TParm();
		
		// 案卷类型
		if (StringUtils.isNotEmpty(this.getValueString("ADM_TYPE"))) {
			parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
		}
		
		String status = ((TComboBox)this.getComponent("STATUS")).getSelectedID();
		// 在库状态
		if (StringUtils.isNotEmpty(status)) {
			parm.setData("IN_FLG", status);
		}
		
		// 病案号
		if (StringUtils.isNotEmpty(this.getValueString("MR_NO"))) {
			parm.setData("MR_NO", this.getValueString("MR_NO"));
		}
		
		String archivesNo = this.getValueString("ARCHIVES_NO");
		// 案卷册号
		if (StringUtils.isNotEmpty(archivesNo.trim())) {
			String[] array = archivesNo.split("-");
			
			if (array.length > 2) {
				// 案卷号
				String boxCode = array[0];
				// 册号
				String bookNo = array[2];
				
				parm.setData("BOX_CODE", boxCode);
				
				parm.setData("BOOK_NO", bookNo);
			}
		}
		
		return parm;
	}

	/**
	 * 
	 * 保存
	 */
	public void onSave() {
		if (null == table.getParmValue() || table.getParmValue().getCount() <= 0) {
			this.messageBox("无保存数据");
			return;
		}
		
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("请选中需要修改的数据行");
			return;
		}
		
		// 修改后存放位置
		String updateStoreLocation = this.getValueString("CURT_LOCATION").trim();
		if (StringUtils.equals(storeLocation, updateStoreLocation)) {
			this.messageBox("请修改当前案卷的存放位置后保存");
			return;
		}
		
		// 操作人员
		String optUser = Operator.getID();
		// 操作端末
		String optTerm = Operator.getIP();
		
		TParm parm = table.getParmValue().getRow(selRow);
		TParm updateParm = new TParm();
		updateParm.setData("MR_NO", parm.getValue("MR_NO"));
		updateParm.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
		updateParm.setData("BOX_CODE", parm.getValue("BOX_CODE"));
		updateParm.setData("BOOK_NO", parm.getValue("BOOK_NO"));
		// 修改后存放位置
		updateParm.setData("CURT_LOCATION", updateStoreLocation);
		updateParm.setData("OPT_USER", optUser);
		updateParm.setData("OPT_TERM", optTerm);
		
		TParm result = MROBorrowTool.getInstance().updateMroMrvStoreLocation(updateParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0001");
			err(result.getErrCode() + " " + result.getErrText());
			return;
		}
		
		this.messageBox("P0001");
		table.setValueAt(updateStoreLocation, selRow, 5);
		table.getParmValue().setData("CURT_LOCATION", selRow, updateStoreLocation);
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
			this.onQuery();
		}
	}
	
	/**
	 * 根据案卷册号查询
	 */
	public void onQueryByArchivesNo() {
		String[] array = this.getValueString("ARCHIVES_NO").split("-");
		if (array.length != 3) {
			this.messageBox("查无数据");
			return;
		}
		
		TParm queryParm = new TParm();
		queryParm.setData("BOX_CODE", array[0]);
		queryParm.setData("ADM_TYPE", array[1]);
		queryParm.setData("BOOK_NO", array[2]);
		
		TParm resultParm = MROBorrowTool.getInstance().queryMroMrv(queryParm);
		if (resultParm.getCount() <= 0) {
			table.setParmValue(new TParm());
			messageBox("查无数据");
			return;
		}
		
		table.setParmValue(resultParm);
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
		clearValue("ADM_TYPE;STATUS;MR_NO;ARCHIVES_NO;CURT_LOCATION");
		table.setParmValue(new TParm());
	}
	
	/**
	 * 添加对table 的监听事件
	 * 
	 * @param row
	 */
	public void onTableClicked(int row) {
		if (row < 0) {
			return;
		}
		
		TParm data = table.getParmValue();
		
		setValueForParm("CURT_LOCATION", data, row);
		
		storeLocation = this.getValueString("CURT_LOCATION").trim();
	}
	
	/**
	 * 打印新建归档病历的案卷号
	 */
	public void onPrint() {
		if (null == table.getParmValue() || table.getParmValue().getCount() <= 0) {
			this.messageBox("无打印数据");
			return;
		}
		
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("请选中需要补印案卷册号的数据行");
			return;
		}
		
		TParm parm = table.getParmValue().getRow(selRow);
		// 打印用Parm
		TParm printParm = new TParm();
		
		String boxCode = parm.getValue("BOX_CODE");
		String bookNo = parm.getValue("BOOK_NO");
		
		if (StringUtils.isEmpty(boxCode) || StringUtils.isEmpty(bookNo)) {
			this.messageBox("当前选中的数据无案卷册号");
			return;
		}
		
		TParm queryParm = new TParm();
		queryParm.setData("MR_NO", parm.getValue("MR_NO"));
		
		queryParm = MROQueueTool.getInstance().selectMroPrintData(queryParm);
		
		if (queryParm.getErrCode() < 0) {
			this.messageBox("查询病患信息出错");
			err("ERR:" + queryParm.getErrCode() + queryParm.getErrText()
					+ queryParm.getErrName());
            return;
        }
		
		String barCode = boxCode + "-" + parm.getValue("ADM_TYPE") + "-" + bookNo;
		// 条码
		printParm.setData("BAR_CODE", "TEXT", barCode);
		// 案卷册号
		printParm.setData("ARC_CODE", "TEXT", "案卷号:" + barCode);
		// 病患姓名
		printParm.setData("PAT_NAME", "TEXT", "姓名:" + queryParm.getValue("PAT_NAME", 0));
		// 性别
		printParm.setData("SEX", "TEXT", "性别:" + queryParm.getValue("SEX", 0));
		// 出生日期
		String birthday = queryParm.getValue("BIRTH_DATE", 0);
		if (StringUtils.isNotEmpty(birthday)) {
			birthday = birthday.substring(0, 10);
		}
		// 出生日期
		printParm.setData("BIRTHDAY", "TEXT", "出生日期:" + birthday);
		// 病案号
		printParm.setData("MR_NO", "TEXT", "病案号:" + queryParm.getValue("MR_NO", 0));
		String tel = "联系电话:";
		if (StringUtils.isNotEmpty(queryParm.getValue("CELL_PHONE", 0))) {
			tel = tel + queryParm.getValue("CELL_PHONE", 0) + "  " + queryParm.getValue("GUARDIAN1_PHONE", 0);
		} else {
			tel = tel + queryParm.getValue("GUARDIAN1_PHONE", 0);
		}
		// 联系电话
		printParm.setData("TEL", "TEXT", tel);
		
		// 打印时不弹出打印设置对话框
		this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROArchivesBarCodePrint.jhw", printParm, true);
	}
	
	/**
	 * 补打案卷号(小label)
	 */
	public void onPrintSmall() {
		if (null == table.getParmValue() || table.getParmValue().getCount() <= 0) {
			this.messageBox("无打印数据");
			return;
		}
		
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("请选中需要补印案卷册号的数据行");
			return;
		}
		
		TParm parm = table.getParmValue().getRow(selRow);
		// 打印用Parm
		TParm printParm = new TParm();
		
		String boxCode = parm.getValue("BOX_CODE");
		String bookNo = parm.getValue("BOOK_NO");
		
		if (StringUtils.isEmpty(boxCode) || StringUtils.isEmpty(bookNo)) {
			this.messageBox("当前选中的数据无案卷册号");
			return;
		}
		
		TParm queryParm = new TParm();
		queryParm.setData("MR_NO", parm.getValue("MR_NO"));
		
		queryParm = MROQueueTool.getInstance().selectMroPrintData(queryParm);
		
		if (queryParm.getErrCode() < 0) {
			this.messageBox("查询病患信息出错");
			err("ERR:" + queryParm.getErrCode() + queryParm.getErrText()
					+ queryParm.getErrName());
            return;
        }
		
		String barCode = boxCode + "-" + parm.getValue("ADM_TYPE") + "-" + bookNo;
		// 条码
		printParm.setData("BAR_CODE", "TEXT", barCode);
		// 案卷册号
		printParm.setData("ARC_CODE", "TEXT", "案卷号:" + barCode);
		// 病患姓名
		printParm.setData("PAT_NAME", "TEXT", "姓名:" + queryParm.getValue("PAT_NAME", 0));
		// 性别
		printParm.setData("SEX", "TEXT", "性别:" + queryParm.getValue("SEX", 0));
		// 出生日期
		String birthday = queryParm.getValue("BIRTH_DATE", 0);
		if (StringUtils.isNotEmpty(birthday)) {
			birthday = birthday.substring(0, 10);
		}
		// 出生日期
		printParm.setData("BIRTHDAY", "TEXT", "出生日期:" + birthday);
		// 病案号
		printParm.setData("MR_NO", "TEXT", "病案号:" + queryParm.getValue("MR_NO", 0));
		// 住院号
		printParm.setData("IPD_NO", "TEXT", "住院号:" + queryParm.getValue("IPD_NO", 0));
		// 去掉固定打印张数，改为弹出预览界面由用户选择打印张数
		this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROArchivesBarCodeSmallPrint.jhw", printParm);
	}
	
	/**
	 * 分册
	 */
	public void onSeparate() {
		TParm result = new TParm();
		TParm parm = table.getParmValue();
		int dataLen = 0;
		int selCount = 0;
		int selRow = 0;
		
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("无分册数据");
			return;
		}
		
		// 强制失去编辑焦点
		if (table.getTable().isEditing()) {
			table.getTable().getCellEditor().stopCellEditing();
		}
		
		dataLen = parm.getCount();
		for (int i = 0; i < dataLen; i++) {
			if (parm.getBoolean("FLG", i)) {
				selCount = selCount + 1;
				selRow = i;
			}
		}
		
		if (selCount == 0) {
			this.messageBox("请勾选需要进行分册操作的数据");
			return;
		}else if (selCount > 1) {
			this.messageBox("分册操作只能针对单条数据");
			return;
		}
		
		TParm selRowParm = parm.getRow(selRow);
		TParm queryParm = new TParm();
		queryParm.setData("MR_NO", selRowParm.getValue("MR_NO"));
		queryParm.setData("ADM_TYPE", selRowParm.getValue("ADM_TYPE"));
		queryParm.setData("BOX_CODE", selRowParm.getValue("BOX_CODE"));
		queryParm.setData("BOOK_NO", selRowParm.getValue("BOOK_NO"));
		// 查询数据库中最新的在库状态
		result = MROQueueTool.getInstance().selectMRO_MRV(queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox_("系统错误");
			err(result.getErrCode() + " " + result.getErrText());
			return;
		}
		
		if (!StringUtils.equals("1", result.getValue("IN_FLG", 0))) {
			this.messageBox("在库案卷才可进行分册操作,请刷新页面查询当前数据的在库状态");
			return;
		}
		
		// 将数据库中最新的在库状态更新
		selRowParm.setData("IN_FLG", result.getValue("IN_FLG", 0));
		
		queryParm.removeData("BOOK_NO");
		// 根据病案号、门急住标识和案卷号查询当下的最大册号
		result = MROBorrowTool.getInstance().queryMroMrv(queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox_("E0001");
			err(result.getErrCode() + " " + result.getErrText());
			return;
		}
		
		dataLen = result.getCount();
		if (dataLen > 1) {
			// 取得最大的册号
			String maxBookNo = result.getValue("BOOK_NO", dataLen - 1);
			// 最大册号的案卷才可进行分册
			if (!StringUtils.equals(maxBookNo, selRowParm.getValue("BOOK_NO"))) {
				this.messageBox("相同案卷号下，最大册号的案卷才可进行分册操作");
				return;
			}
		}
		
		// 取得新数据的流水号
		int newSeq = result.getInt("SEQ", dataLen - 1) + 1;
		// 取得新数据的册号
		String newBookNo = String.format("%02d", Integer.parseInt(result
				.getValue("BOOK_NO", dataLen - 1)) + 1);
		
		// 判断最大长度(册号为两位)
		if (newBookNo.length() > 2) {
			this.messageBox("当前案卷册号为最大值，不可再分册");
			return;
		}
		
		selRowParm.setData("SEQ", newSeq);
		selRowParm.setData("BOOK_NO", newBookNo);
		selRowParm.setData("OPT_USER", Operator.getID());
		selRowParm.setData("OPT_TERM", Operator.getIP());
		
		result = MROQueueTool.getInstance().insertMRO_MRV(selRowParm);
		if (result.getErrCode() < 0) {
			this.messageBox_("分册失败");
			err(result.getErrCode() + " " + result.getErrText());
			return;
		}
		
		this.messageBox("分册成功");
		// 回显数据
		this.onQuery();
	}
	
	/**
	 * 合并
	 */
	public void onMerge() {
		TParm result = new TParm();
		TParm parm = table.getParmValue();
		TParm selRowParm = new TParm();
		TParm queryParm = new TParm();
		int dataLen = 0;
		int count = 0;
		List<String> mrNoList = new ArrayList<String>();
		List<String> admTypeList = new ArrayList<String>();
		List<String> bookNoList = new ArrayList<String>();
		
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("无合并数据");
			return;
		}
		
		// 强制失去编辑焦点
		if (table.getTable().isEditing()) {
			table.getTable().getCellEditor().stopCellEditing();
		}
		
		dataLen = parm.getCount();
		for (int i = 0; i < dataLen; i++) {
			if (parm.getBoolean("FLG", i)) {
				queryParm = new TParm();
				queryParm.setData("MR_NO", parm.getValue("MR_NO", i));
				queryParm.setData("ADM_TYPE", parm.getValue("ADM_TYPE", i));
				queryParm.setData("BOX_CODE", parm.getValue("BOX_CODE", i));
				queryParm.setData("BOOK_NO", parm.getValue("BOOK_NO", i));
				// 查询数据库中最新的在库状态
				result = MROQueueTool.getInstance().selectMRO_MRV(queryParm);
				
				if (result.getErrCode() < 0) {
					this.messageBox_("系统错误");
					err(result.getErrCode() + " " + result.getErrText());
					return;
				}
				
				// 只有在库状态的案卷才可以进行合并操作
				if (!StringUtils.equals("1", result.getValue("IN_FLG", 0))) {
					this.messageBox("在库案卷才可进行合并操作");
					return;
				}
				
				// 记录勾选的病案号
				if (!mrNoList.contains(parm.getValue("MR_NO", i))) {
					mrNoList.add(parm.getValue("MR_NO", i));
				}
				
				// 记录勾选的类型
				if (!admTypeList.contains(parm.getValue("ADM_TYPE", i))) {
					admTypeList.add(parm.getValue("ADM_TYPE", i));
				}
				
				// 记录勾选的册号
				if (!bookNoList.contains(parm.getValue("BOOK_NO", i))) {
					bookNoList.add(parm.getValue("BOOK_NO", i));
				}
				
				count = count + 1;
				selRowParm.addData("MR_NO", parm.getValue("MR_NO", i));
				selRowParm.addData("ADM_TYPE", parm.getValue("ADM_TYPE", i));
				selRowParm.addData("BOX_CODE", parm.getValue("BOX_CODE", i));
				selRowParm.addData("BOOK_NO", parm.getValue("BOOK_NO", i));
			}
		}
		
		selRowParm.setCount(count);
		
		selRowParm.addData("SYSTEM", "COLUMNS", "MR_NO");
		selRowParm.addData("SYSTEM", "COLUMNS", "ADM_TYPE");
		selRowParm.addData("SYSTEM", "COLUMNS", "BOX_CODE");
		selRowParm.addData("SYSTEM", "COLUMNS", "BOOK_NO");
		
		if (selRowParm.getCount() < 2) {
			this.messageBox("合并操作至少选中两条数据");
			return;
		}
		
		if (mrNoList.size() > 1) {
			this.messageBox("相同病案号的案卷才可以进行合并");
			return;
		}
		
		if (admTypeList.size() > 1) {
			this.messageBox("相同类型的案卷才可以进行合并");
			return;
		}
		
		// 弹出新册号确认框
		result = (TParm) this.openDialog(
				"%ROOT%\\config\\mro\\MROMergeBookNo.x", selRowParm.getValue("BOOK_NO", 0));
		
		// 点击取消
		if (null == result) {
			return;
		}
		
		// 合并前验证
		// 如果新输入的册号属于当前勾选的其中一个册号，则不用单独验证
		// 否则合并前验证新输入的册号是否已经存在
		if (!bookNoList.contains(result.getValue("NEW_BOOK_NO"))) {
			queryParm = new TParm();
			queryParm.setData("MR_NO", selRowParm.getValue("MR_NO", 0));
			queryParm.setData("ADM_TYPE", selRowParm.getValue("ADM_TYPE", 0));
			queryParm.setData("BOX_CODE", selRowParm.getValue("BOX_CODE", 0));
			queryParm.setData("BOOK_NO", result.getValue("NEW_BOOK_NO"));
			
			TParm checkResult = MROQueueTool.getInstance().selectMRO_MRV(queryParm);
			
			if (checkResult.getErrCode() < 0) {
				this.messageBox_("合并失败");
				err(checkResult.getErrCode() + " " + checkResult.getErrText());
				return;
			}
			
			if (checkResult.getCount() > 0) {
				this.messageBox("新输入的册号已经存在，请重新输入");
				return;
			}
		}
		
		int rowNum = 0;
		boolean existFlg = false;
		for (int i = 0; i < bookNoList.size(); i++) {
			// 如果新册号等于当前行数据中的册号，则将此行数据过滤掉
			if (StringUtils.equals(result.getValue("NEW_BOOK_NO"), bookNoList.get(i))) {
				rowNum = i;
				existFlg = true;
				break;
			}
		}
		
		TParm updateParm = new TParm();
		updateParm.setData("MR_NO", selRowParm.getValue("MR_NO", rowNum));
		updateParm.setData("ADM_TYPE", selRowParm.getValue("ADM_TYPE", rowNum));
		updateParm.setData("BOX_CODE", selRowParm.getValue("BOX_CODE", rowNum));
		updateParm.setData("NEW_BOOK_NO", result.getValue("NEW_BOOK_NO"));
		updateParm.setData("OLD_BOOK_NO", selRowParm.getValue("BOOK_NO", rowNum));
		
		// 合并后赋予新的册号
		result = MROBorrowTool.getInstance().updateMroMrvBookNo(updateParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox_("合并失败");
			err(result.getErrCode() + " " + result.getErrText());
			return;
		}
		
		if (existFlg) {
			// 过滤数据
			selRowParm.removeRow(rowNum);
		}
		// 合并后删除多余的数据
		result = TIOM_AppServer.executeAction("action.mro.MROBorrowAction",
				"deleteMroMrv", selRowParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("合并失败");
			err(result.getErrCode() + " " + result.getErrText());
			return;
		}

		this.messageBox("合并成功");
		// 回显数据
		this.onQuery();
	}
	
	/**
	 * 汇出Excel
	 */
	public void onExport() {
		TParm parm = table.getShowParmValue();
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		
		if (table.getRowCount() > 0) {
			TParm exportParm = table.getShowParmValue(); 
			exportParm.setData("TITLE", "病历案卷管理");
			exportParm.setData("HEAD", table.getHeader().replaceAll("选,40,boolean;", ""));
	        String[] header = table.getParmMap().split(";");
	        for (int i = 1; i < header.length; i++) {
	        	exportParm.addData("SYSTEM", "COLUMNS", header[i]);
	        }
	        TParm[] execleTable = new TParm[]{exportParm};
	        ExportExcelUtil.getInstance().exeSaveExcel(execleTable, "病历案卷管理");
		}
	}
	
	/**
	 * 得到ComboBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}
}
