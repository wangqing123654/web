package com.javahis.ui.mro;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jdo.adm.ADMInpTool;
import jdo.mro.MROLendTool;
import jdo.mro.MROQueueTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.DateUtil;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 病历出库管理
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
 * @author wangbin 2014.08.19
 * @version 4.0
 */
public class MROOutControl extends TControl {
	
	private String pageParam; // 页面传参
	private TTable tableUp; // 借阅信息数据
	private TTable tableDown; // 案卷明细数据
	private TParm lendAreaParm;// 借阅诊区病区控件数据
	private Map<String, String> lendAreaMap;// 借阅诊区病区控件Map数据
	
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
		
		// 初始化
		this.onInitPage();
	}

	/**
	 * 初始化页面
	 */
	private void onInitPage() {
		tableUp = (TTable) this.getComponent("TABLE_UP");
		tableDown = (TTable) this.getComponent("TABLE_DOWN");
		
		// 页面标题
		String title = "";
		
		// 门诊挂号
		if ("O".equals(pageParam)) {
			// 出库方式
			getComboBox("OUT_TYPE").setSelectedID("0");
			// 诊区病区控件显示
			this.callFunction("UI|CLINIC_AREA|setVisible", true);
			this.callFunction("UI|ENDEMIC_AREA|setVisible", false);
			// 隐藏打印按钮
//			this.callFunction("UI|print|setVisible", false);
			// 页面标题
			title = "门诊病历出库管理";
		} else if ("I".equals(pageParam)) {
			// 出库方式
			getComboBox("OUT_TYPE").setSelectedID("1");
			// 诊区病区控件显示
			this.callFunction("UI|CLINIC_AREA|setVisible", false);
			this.callFunction("UI|ENDEMIC_AREA|setVisible", true);
			// 隐藏打印按钮
//			this.callFunction("UI|print|setVisible", false);
			// 页面标题
			title = "住院病历出库管理";
			
			// 设定住院出库表头数据解析(TABLE_UP)
			String tableUpHeader = this.tableUp.getHeader().replace("CLINIC_AREA", "ENDEMIC_AREA");
			this.tableUp.setHeader(tableUpHeader);
			
			// 设定住院出库表头数据解析(TABLE_DOWN)
			String tableDownHeader = this.tableDown.getHeader().replace("就诊序号,60;", "");
			this.tableDown.setHeader(tableDownHeader);
			
			String tableDownParmMap = this.tableDown.getParmMap().replace("QUE_NO;", "");
			this.tableDown.setParmMap(tableDownParmMap);
			
			this.callFunction("UI|TABLE_DOWN|setColumnHorizontalAlignmentData",
					"1,center;2,left;3,center;4,center;5,center");
		} else {
			// 出库方式
			getComboBox("OUT_TYPE").setSelectedID("2");
			// 页面标题
			title = "借阅病历出库管理";
			
			// 诊区/病区下拉框
			this.callFunction("UI|CLINIC_AREA|setVisible", false);
			this.callFunction("UI|ENDEMIC_AREA|setVisible", false);
			this.callFunction("UI|LEND_AREA|setVisible", true);
			this.callFunction("UI|LEND_AREA|setEnabled", true);
			
			// 设定借阅出库表头数据解析(TABLE_UP)
			String tableUpHeader = this.tableUp.getHeader().replace(",CLINIC_AREA", "");
			this.tableUp.setHeader(tableUpHeader);
			
			String tableUpParmMap = this.tableUp.getParmMap().replace("ADM_AREA_CODE", "LEND_AREA_DESC");
			this.tableUp.setParmMap(tableUpParmMap);
			
			this.callFunction("UI|TABLE_UP|setColumnHorizontalAlignmentData",
					"0,left;1,left;2,left;3,right;4,right");
			
			// 设定借阅出库表头数据解析(TABLE_DOWN)
			String tableDownHeader = this.tableDown.getHeader().replace("就诊序号,60;", "病案类型,80;");
			this.tableDown.setHeader(tableDownHeader);
			
			String tableDownParmMap = this.tableDown.getParmMap().replace("QUE_NO;", "ARCHIVES_TYPE;");
			this.tableDown.setParmMap(tableDownParmMap);
			
			this.callFunction("UI|TABLE_DOWN|setColumnHorizontalAlignmentData",
					"1,center;2,center;3,left;4,center;5,center;6,center");
			
			// 诊区/病区控件设定
			TTextFormat lendArea = ((TTextFormat)this.getComponent("LEND_AREA"));
			lendAreaParm = MROQueueTool.getInstance().selectMroLendArea();
			lendArea.setPopupMenuData(lendAreaParm);
			lendArea.setComboSelectRow();
			lendArea.popupMenuShowData();
			
			// 组装诊区病区数据Map
			lendAreaMap = new HashMap<String, String>();
			for (int i = 0; i < lendAreaParm.getCount(); i++) {
				lendAreaMap.put(lendAreaParm.getValue("ID", i), lendAreaParm.getValue("NAME", i));
			}
		}
		
		this.setTitle(title);
		
		// 时间设定
		String now = SystemTool.getInstance().getDate().toString().substring(0,
				10).replace('-', '/');
		
		// 默认显示第二天日期
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(SystemTool.getInstance().getDate());
		calendar.add(Calendar.DATE, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String tommorwDate = sdf.format(calendar.getTime());
		
		this.setValue("OUT_S_DATE", now); // 开始时间
		this.setValue("OUT_E_DATE", tommorwDate); // 结束时间
	}
	
	/**
	 * 查询出库数据
	 */
	public void onQuery() {
		clearValue("BOX_CODE;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE");
		
		if (!this.validate()) {
			return;
		}
		
		// 获取查询条件数据
		TParm queryParm = this.getQueryParm();
		
		// 查询待出库的病历数据
		TParm resultParmUp = MROQueueTool.getInstance().selectMroOutIn(queryParm);
		if (resultParmUp.getCount() <= 0) {
			tableDown.setParmValue(new TParm());
			tableUp.setParmValue(new TParm());
			messageBox("查无数据");
			return;
		}
	
		// 借阅出库
		if ("L".equals(pageParam)) {
			// 设置主表显示数据(诊区病区)
			for (int i = 0; i < resultParmUp.getCount(); i++) {
				resultParmUp.addData("LEND_AREA_DESC", lendAreaMap
						.get(resultParmUp.getValue("ADM_AREA_CODE", i)));
			}
		}
		
		tableUp.setParmValue(resultParmUp);
		
		// 查询病患案卷详情
		TParm resultParmDown = MROQueueTool.getInstance().selectMroOutInDetail(queryParm);
		
		if (resultParmDown.getErrCode() < 0) {
			this.messageBox("查询病患案卷数据出错");
			err("ERR:" + resultParmDown.getErrCode() + resultParmDown.getErrText()
					+ resultParmDown.getErrName());
			return;
		}
		
		// 向明细表中增加案卷类型列
		for (int i = 0; i < resultParmDown.getCount(); i++) {
			if (StringUtils.equals("I", resultParmDown.getValue("ADM_TYPE", i))) {
				resultParmDown.addData("ARCHIVES_TYPE", "住院病案");
			} else {
				resultParmDown.addData("ARCHIVES_TYPE", "门诊病案");
			}
		}
		
		tableDown.setParmValue(resultParmDown);
		
		// 设定焦点
		grabFocus("BOX_CODE");
	}
	
	/**
	 * 获取查询条件数据
	 * 
	 * @return parm
	 */
	private TParm getQueryParm() {
		TParm queryParm = new TParm();
		// 出库状态(0_登记未出库,1_已出库,2_已归还)
		queryParm.setData("ISSUE_CODE", "0");
		// 增加出库期间查询条件
		queryParm.setData("OUT_S_DATE", this.getValueString("OUT_S_DATE").substring(0, 10));
		queryParm.setData("OUT_E_DATE", this.getValueString("OUT_E_DATE").substring(0, 10));
		// 出库方式
		queryParm.setData("OUT_TYPE", getComboBox("OUT_TYPE").getSelectedID());
		if (!StringUtils.equals("L", this.pageParam)) {
			// 门急住标识
			queryParm.setData("ADM_TYPE", this.pageParam);
		}
		// 病案主档表的在库状态(0_未建档,1_在库,2出库)
//		queryParm.setData("IN_FLG", "1");
		// 只查询未取消的出库数据
		queryParm.setData("CAN_FLG", "N");
		
		// 门诊挂号
		if ("O".equals(pageParam)) {
			if (StringUtils.isNotEmpty(this.getValueString("CLINIC_AREA"))) {
				// 诊区
				queryParm.setData("ADM_AREA_CODE", this.getValueString("CLINIC_AREA"));
			}
		} else if ("I".equals(pageParam)) {
			if (StringUtils.isNotEmpty(this.getValueString("ENDEMIC_AREA"))) {
				// 病区
				queryParm.setData("ADM_AREA_CODE", this.getValueString("ENDEMIC_AREA"));
			}
		} else {
			if (StringUtils.isNotEmpty(this.getValueString("LEND_AREA"))) {
				// 诊区/病区
				queryParm.setData("ADM_AREA_CODE", this.getValueString("LEND_AREA"));
			}
		}
		
		return queryParm;
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
		clearValue("CLINIC_AREA;ENDEMIC_AREA;BOX_CODE;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;LEND_AREA");
		tableUp.setParmValue(new TParm());
		tableDown.setParmValue(new TParm());
	}
	
	/**
	 * 保存出库信息
	 */
	public boolean onSave(TParm selParm) {
		TParm executeParm = new TParm();
		TParm result = new TParm();
		// 操作人员
		String optUser = Operator.getID();
		// 操作端末
		String optTerm = Operator.getIP();
		
		// 保存前根据借阅优先级判断当前出库是否可执行
		TParm checkParm = new TParm();
		checkParm.setData("QUE_DATE", SystemTool.getInstance().getDate()
				.toString().substring(0, 10));
		checkParm.setData("MR_NO", selParm.getValue("MR_NO"));
		checkParm.setData("ADM_TYPE", selParm.getValue("ADM_TYPE"));
		checkParm.setData("ISSUE_CODE", "2");
		checkParm.setData("BOX_CODE", selParm.getValue("BOX_CODE"));
		checkParm.setData("BOOK_NO", selParm.getValue("BOOK_NO"));
		
		checkParm = MROQueueTool.getInstance().selectMroQueueInfo(checkParm);
		
		if (checkParm.getErrCode() < 0) {
			this.messageBox("查询借阅数据出错");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		
		TParm lendParm = new TParm();
		
		for (int i = 0; i < checkParm.getCount(); i++) {
			if (StringUtils.equals(selParm.getValue("QUE_SEQ"), checkParm.getValue("QUE_SEQ", i))) {
				continue;
			} else {
				lendParm = new TParm();
				lendParm.setData("LEND_CODE", selParm.getValue("LEND_CODE"));
				lendParm = MROLendTool.getInstance().selectdata(lendParm);
				
				// 比较优先级
				if (lendParm.getInt("PRIORITY", 0) > checkParm.getInt("PRIORITY", i)) {
					this.messageBox("针对该案卷册号，因在当前借阅日期下存在优先级更高的待出库数据，不可出库");
					return false;
				}
			}
		}

		// 更新方法标记
		selParm.setData("TYPE", "OUT");
		selParm.setData("OPT_USER", optUser);
		selParm.setData("OPT_TERM", optTerm);
		// 出库状态(0_未出库,1_已出库,2_已归还)
		selParm.setData("ISSUE_CODE", "1");
		// 病历在库状态
		selParm.setData("IN_FLG", "2");
		// 当前借阅科室
		selParm.setData("CURT_LEND_DEPT_CODE", selParm.getValue("DEPT_CODE"));
		// 当前借阅人员
		selParm.setData("CURT_LEND_DR_CODE", selParm.getValue("DR_CODE"));
		// 应归还日期
		String returnDate = "";
		
		// 只有门诊挂号才在出库时设定归还时间，住院的在出院登记时设定归还时间
		if ("O".equals(pageParam)) {
			TParm queryParm = new TParm();
			queryParm.setData("LEND_TYPE", pageParam);
			
			// 查询病历归还时限
			result = MROLendTool.getInstance().selectdata(queryParm);
			
			if (result.getErrCode() < 0) {
				this.messageBox("查询借阅字典错误");
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return false;
			}
			
			if (result.getCount() <= 0) {
				this.messageBox("无对应的借阅字典数据");
				return false;
			}
			
			// 间隔天数
			int intervalDay = result.getInt("LEND_DAY", 0);
			Date date = DateUtils.addDays(DateUtil.strToDate(selParm.getValue("QUE_DATE").substring(0, 10).replaceAll("-", "/")), intervalDay);
			// 计算应归还日期
			returnDate = StringTool.getString(date, "yyyy/MM/dd");
		}
		
		// 针对借阅出库，借阅出库在登记时就按照借阅原因设定了归还日期
		if (StringUtils.isEmpty(selParm.getValue("RTN_DATE"))) {
			// 应归还日期,按照出库类别设定
			selParm.setData("RTN_DATE", returnDate);
		} else {
			selParm.setData("RTN_DATE", selParm.getValue("RTN_DATE").substring(0, 10).replaceAll("-", "/"));
		}

		executeParm.setData("MRV", selParm.getData());
		executeParm.setData("Queue", selParm.getData());

		// 执行病历出库
		result = TIOM_AppServer.executeAction("action.mro.MROQueueAction",
				"updateMroOutIn", executeParm);

		if (result.getErrCode() < 0) {
			this.messageBox("当前案卷出库失败");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}

		// 清空扫描到的案卷号
		clearValue("BOX_CODE");
		// 设定焦点
		grabFocus("BOX_CODE");

		return true;
	}
	
	/**
	 * 案卷号回车事件(扫描案卷号条码)
	 */
	public void onSaveByBoxCode() {
		TParm parmDown = this.tableDown.getParmValue();
		TParm parmUp = this.tableUp.getParmValue();
		
		if (StringUtils.isEmpty(this.getValueString("BOX_CODE").trim())) {
			return;
		}
		
		if (null == parmUp || parmUp.getCount() <= 0) {
			this.messageBox("无就诊数据");
			return;
		}
		
		if (null == parmDown || parmDown.getCount() <= 0) {
			this.messageBox("无就诊数据");
			return;
		}
		int len = parmDown.getCount();
		boolean existFlg = false;
		TParm queryParm = new TParm();
		TParm checkAdmInpParm = new TParm();
		
		// 案卷册号
		String archivesNo = "";
		for (int i = 0; i < len; i++) {
			archivesNo = parmDown.getValue("BOX_CODE", i) + "-" + parmDown.getValue("ADM_TYPE", i) + "-" + parmDown.getValue("BOOK_NO", i);
			// 找到对应案卷号数据
			if (StringUtils.equals(archivesNo, this.getValueString("BOX_CODE"))) {
				
				// 如果当前数据已经被勾选，则不做任何处理
				if (parmDown.getBoolean("FLG", i)) {
					this.messageBox("当前数据已经完成出库");
					// 清空扫描到的案卷号
					clearValue("BOX_CODE");
					// 设定焦点
					grabFocus("BOX_CODE");
					return;
				}
				
				// 验证该案卷册是否在库
				queryParm.setData("ADM_TYPE", this.getValueString("BOX_CODE").split("-")[1]);
				queryParm.setData("IN_FLG", "2");
				queryParm.setData("BOX_CODE", this.getValueString("BOX_CODE").split("-")[0]);
				queryParm.setData("BOOK_NO", this.getValueString("BOX_CODE").split("-")[2]);
				
				queryParm = MROQueueTool.getInstance().selectMRO_MRV(queryParm);
				
				if (queryParm.getErrCode() < 0) {
					this.messageBox("查询病案在库状态出错");
					err(queryParm.getErrCode() + ":" + queryParm.getErrText());
					return;
				}
				
				if (queryParm.getCount() > 0) {
					this.messageBox("当前案卷已经出库");
					// 清空扫描到的案卷号
					clearValue("BOX_CODE");
					// 设定焦点
					grabFocus("BOX_CODE");
					return;
				}
				
				// 对于住院出库的数据，出库前先判断当前病患是否已经出院，如果出院则不可出库
				if ("I".equals(pageParam)) {
					checkAdmInpParm.setData("CASE_NO", parmDown.getRow(i).getValue("CASE_NO"));
					// 查询在院病患信息
					checkAdmInpParm = ADMInpTool.getInstance().selectInHosp(checkAdmInpParm);
					
					if (checkAdmInpParm.getErrCode() < 0) {
						this.messageBox("查询住院数据错误");
						err("ERR:" + queryParm.getErrCode() + queryParm.getErrText());
						return;
					} else if (checkAdmInpParm.getCount() <= 0) {
						this.messageBox("当前病患已经出院，不可出库");
						return;
					}
				}
				
				if (this.onSave(parmDown.getRow(i))) {
					// 勾选
					tableDown.setItem(i, "FLG", "Y");
					// 设定病案号
					this.setValue("MR_NO", parmDown.getValue("MR_NO", i));
					// 设定姓名
					this.setValue("PAT_NAME", parmDown.getValue("PAT_NAME", i));
					// 设定科室
					this.setValue("DEPT_CODE", parmDown.getValue("DEPT_CODE", i));
					// 设定医生
					this.setValue("DR_CODE", parmDown.getValue("DR_CODE", i));
					
					// 实际入库计数器
					this.onTableUpCount(parmDown.getRow(i));
				}
				
				existFlg = true;
				break;
			}
		}
		
		// 没有找到对应的案卷号
		if (!existFlg) {
			this.messageBox("当前待出库数据中无该案卷号数据");
			// 清空扫描到的案卷号
			clearValue("BOX_CODE");
			// 设定焦点
			grabFocus("BOX_CODE");
			return;
		}
	}
	
	/**
	 * 病案出库计数器
	 */
	private void onTableUpCount(TParm selParmDown) {
		TParm parmUp = tableUp.getParmValue();
		// 由于不同出库方式对应页面的主表列不同，取得对应的最后一列位置，即出库数量
		int outPosition = this.tableUp.getParmMap().split(";").length;
		
		for (int i = 0; i < parmUp.getCount(); i++) {
			if (StringUtils.equals(selParmDown.getValue("QUE_DATE"), parmUp
					.getValue("QUE_DATE", i))
					&& StringUtils.equals(selParmDown.getValue("ADM_TYPE"),
							parmUp.getValue("ADM_TYPE", i))
					&& StringUtils.equals(selParmDown.getValue("DEPT_CODE"),
							parmUp.getValue("DEPT_CODE", i))
					&& StringUtils.equals(selParmDown.getValue("DR_CODE"),
							parmUp.getValue("DR_CODE", i))) {
				
				// 找到对应的数据，选中，并且增加实际出库数量
				tableUp.setSelectedRow(i);
				int outCount = Integer.parseInt(String.valueOf(tableUp
						.getValueAt(i, outPosition - 1)));

				if (0 == outCount) {
					tableUp.setValueAt("1", i, outPosition - 1);
				} else {
					outCount = outCount + 1;
					tableUp.setValueAt(String.valueOf(outCount), i, outPosition - 1);
				}
				
				// 当前科室医生下的所有待出库的案卷都出库后，对应的TABLE_UP中的数据需要重新查询
				if (StringUtils.equals(
						String.valueOf(tableUp.getValueAt(i, outPosition - 2)), String
								.valueOf(tableUp.getValueAt(i, outPosition - 1)))) {
					tableUp.removeRow(i);
				}
				
				break;
			}
		}
	}
	
	/**
	 * 打印待出库借阅病案报表
	 */
	public void onPrint() {
		TParm parm = tableDown.getParmValue();

		
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("无打印数据");
			return;
		}
		
		int dataLen = parm.getCount();
		
		// 打印用Parm
		TParm printParm = new TParm();
		// 打印数据
		TParm printData = new TParm();
		// 打印用案卷号
		String boxCode = "";
		int printDataLen = 0;
		
		// 打印数据出库类型
		String archivesType = "";
		
		if ("O".equals(pageParam)) {
			archivesType = "门诊病案";
		} else if ("I".equals(pageParam)) {
			archivesType = "住院病案";
		} else {
			archivesType = "借阅病案";
		}
		
		for (int i = 0; i < dataLen; i++) {
			if (!parm.getBoolean("FLG", i)) {
				// 案卷类型
				printData.addData("ARCHIVES_TYPE", archivesType);
				
				boxCode = parm.getValue("BOX_CODE", i) + "-" + parm.getValue("ADM_TYPE", i) + "-" + parm.getValue("BOOK_NO", i);
				// 案卷号
				printData.addData("BOX_CODE", boxCode);
				// 病案号
				printData.addData("MR_NO", parm.getValue("MR_NO", i));//parm.getValue("MR_NO", i)
				// 姓名
				printData.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
				// 借阅科室
				printData.addData("DEPT_CODE", parm.getValue("DEPT_CHN_DESC", i));//
//				System.out.println("DEPT_CODE = = =" + parm.getValue("DEPT_CODE", i));
				// 借阅人
				printData.addData("DR_CODE", parm.getValue("DR_NAME", i));//
//				System.out.println("DR_CODE = = =" + parm.getValue("DR_CODE", i));
				// 借阅日期
				printData.addData("QUE_DATE", parm.getValue("QUE_DATE", i).substring(0, 10));//
//				System.out.println("DR_CODE = = =" + parm.getValue("DR_CODE", i));
				printDataLen = printDataLen + 1;
			}
		}
		//并按类型，案卷号，病案号，姓名，借阅科室，借阅人，借阅日期
		if (printDataLen < 1) {
			this.messageBox("没有待出库案卷数据");
			return;
		}
		
		printData.setCount(printDataLen);
		
		printData.addData("SYSTEM", "COLUMNS", "ARCHIVES_TYPE");
		printData.addData("SYSTEM", "COLUMNS", "QUE_DATE");
		printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		printData.addData("SYSTEM", "COLUMNS", "MR_NO");
		printData.addData("SYSTEM", "COLUMNS", "BOX_CODE");
		printData.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
		printData.addData("SYSTEM", "COLUMNS", "DR_CODE");
//		System.out.println("printData = = = = " + printData);
		printParm.setData("TITLE", "TEXT", archivesType + "出库确认单");
		
		printParm.setData("TABLE1", printData.getData());
		
		this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROOutPrint.jhw", printParm);
	}
	
	/**
	 * 页面控件数据验证
	 */
	private boolean validate() {
		if (this.getValueString("OUT_S_DATE").length() < 10
				|| this.getValueString("OUT_E_DATE").length() < 10) {
			this.messageBox("出库期间为必填项");
			return false;
		}
		return true;
	}
	
	/**
	 * 取消出库
	 */
	public void onDelete() {
		TParm parm = tableDown.getParmValue();
		
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("无出库数据");
			return;
		}
		
		int selRow = this.tableDown.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("请选中要取消出库的数据行");
			return;
		}
		
		int sel = this.messageBox("询问", "是否取消出库？", 0);
		if (0 == sel) {
			
			TParm selParm = parm.getRow(selRow);
			
			TParm result = MROQueueTool.getInstance().deleteMroOut(selParm);
			
			if (result.getErrCode() < 0) {
				this.messageBox("系统错误");
				err(result.getErrCode() + ":" + result.getErrText());
				return;
			}
			
			this.messageBox("取消成功");
			
			// 数据回显
			this.onQuery();
		}
	}
	
	/**
	 * 导出报表
	 */
	public void onExport() {
		if (this.tableDown.getRowCount() > 0) {
			String title = "";
			// 门诊挂号
			if ("O".equals(pageParam)) {
				title = "门诊";
			} else if ("I".equals(pageParam)) {
				title = "住院";
			} else {
				title = "借阅";
			}
			ExportExcelUtil.getInstance().exportExcel(tableDown, title + "病案出库");
		} else {
			this.messageBox("没有需要导出的数据");
			return;
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
