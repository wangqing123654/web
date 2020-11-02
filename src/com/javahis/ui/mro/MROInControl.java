package com.javahis.ui.mro;

import java.util.HashMap;
import java.util.Map;

import jdo.mro.MROBorrowTool;
import jdo.mro.MROQueueTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 病历入库管理
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
 * @author wangbin 2014.08.25
 * @version 4.0
 */
public class MROInControl extends TControl {
	
	private String pageParam; // 页面传参
	private TTable tableUp; // 借阅信息数据
	private TTable tableDown; // 案卷明细数据
	private TTable tableNew; // 新建病历
	private TTable tableNewPrint; // 新建病历打印
	private TParm lendAreaParm;// 借阅诊区病区控件数据
	private Map<String, String> lendAreaMap;// 借阅诊区病区控件Map数据
	private String bigLabelPrtSwitch;// 打印大标签开关
	private String smallLabelPrtSwitch;// 打印小标签开关
	
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
		tableNew = (TTable) this.getComponent("TABLE_NEW");
		tableNewPrint = (TTable) this.getComponent("TABLE_NEW_PRINT");
		
		// 禁用分册按钮
		callFunction("UI|separate|setEnabled", false);
		// 新建病历表格数据勾选事件
		tableNew.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onCheckNewMroData");
		
		// 页面标题
		String title = "";
		
		// 门诊挂号
		if ("O".equals(pageParam)) {
			// 第一页签出库方式
			getComboBox("IN_TYPE").setSelectedID("0");
			// 第二页签出库方式
			getComboBox("NEW_IN_TYPE").setSelectedID("0");
			// 第一页签诊区病区控件显示
			this.callFunction("UI|CLINIC_AREA|setVisible", true);
			this.callFunction("UI|ENDEMIC_AREA|setVisible", false);
			
			// 第二页签诊区病区控件显示
			this.callFunction("UI|NEW_CLINIC_AREA|setVisible", true);
			this.callFunction("UI|NEW_ENDEMIC_AREA|setVisible", false);
			// 页面标题
			title = "门诊病历入库管理";
		} else if ("I".equals(pageParam)) {
			// 第一页签出库方式
			getComboBox("IN_TYPE").setSelectedID("1");
			// 第二页签出库方式
			getComboBox("NEW_IN_TYPE").setSelectedID("1");
			// 第一页签诊区病区控件显示
			this.callFunction("UI|CLINIC_AREA|setVisible", false);
			this.callFunction("UI|ENDEMIC_AREA|setVisible", true);
			
			// 第二页签诊区病区控件显示
			this.callFunction("UI|NEW_CLINIC_AREA|setVisible", false);
			this.callFunction("UI|NEW_ENDEMIC_AREA|setVisible", true);
			// 页面标题
			title = "住院病历入库管理";
			
			// 设定住院入库表头数据解析(TABLE_UP)
			String tableUpHeader = this.tableUp.getHeader().replace("CLINIC_AREA", "ENDEMIC_AREA");
			this.tableUp.setHeader(tableUpHeader);
			
			// 设定住院入库表头数据解析(TABLE_DOWN)
			String tableDownHeader = this.tableDown.getHeader().replace("就诊序号,60;", "");
			this.tableDown.setHeader(tableDownHeader);
			
			String tableDownParmMap = this.tableDown.getParmMap().replace("QUE_NO;", "");
			this.tableDown.setParmMap(tableDownParmMap);
			
			this.callFunction("UI|TABLE_DOWN|setColumnHorizontalAlignmentData",
					"1,center;2,left;3,center;4,center");
			
			// 第二页签表格数据解析
			// 设定住院入库表头数据解析(TABLE_UP)
			String tableNewHeader = this.tableNew.getHeader().replace("NEW_CLINIC_AREA", "NEW_ENDEMIC_AREA");
			this.tableNew.setHeader(tableNewHeader);
		} else {
			// 第一页签出库方式
			getComboBox("IN_TYPE").setSelectedID("2");
			// 由于借阅病历不存在新建病历归还，所以禁用该页签
			callFunction("UI|tTabbedPane_0|setEnabled", false);
			// 第二页签出库方式
			getComboBox("NEW_IN_TYPE").setSelectedID("2");
			// 页面标题
			title = "借阅病历入库管理";
			
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
			String tableDownHeader = this.tableDown.getHeader().replace("就诊序号,60;", "案卷类型,80;");
			this.tableDown.setHeader(tableDownHeader);
			
			String tableDownParmMap = this.tableDown.getParmMap().replace("QUE_NO;", "ARCHIVES_TYPE;");
			this.tableDown.setParmMap(tableDownParmMap);
			
			this.callFunction("UI|TABLE_DOWN|setColumnHorizontalAlignmentData",
					"1,center;2,center;3,left;4,center;5,center");
			
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
		
		String now = SystemTool.getInstance().getDate().toString().substring(0,
				10).replace('-', '/');
		// 第一页签
		this.setValue("S_DATE", now); // 开始时间
		this.setValue("E_DATE", now); // 结束时间
		// 第二页签
		this.setValue("NEW_S_DATE", now); // 开始时间
		this.setValue("NEW_E_DATE", now); // 结束时间
		
        // add by wangbin 2015/1/4 增加建档后是否自动打印标签开关的判断 START
		// 取得打印标签开关
        bigLabelPrtSwitch = IReportTool.getInstance().getPrintSwitch("MROInArchivingBigLabel.prtSwitch");
        smallLabelPrtSwitch = IReportTool.getInstance().getPrintSwitch("MROInArchivingSmallLabel.prtSwitch");
        // add by wangbin 2015/1/4 增加建档后是否自动打印标签开关的判断 END
	}
	
	/**
	 * 查询
	 */
	public boolean onQuery() {
		boolean flg = false;
        //获取当前选择的页签 索引
        int selectedPage = ((TTabbedPane)this.getComponent("tTabbedPane_0")).getSelectedIndex();
        
        // 验证必填项
        this.validate(selectedPage);
        
        if (selectedPage == 0) {
        	flg = this.onQueryOne(selectedPage);
        } else {
        	flg = this.onQueryTwo(selectedPage);
        }
        
        
        
        return flg;
	}
	
	/**
	 * 第一页签查询入库数据
	 */
	private boolean onQueryOne(int selectedPage) {
		clearValue("BOX_CODE;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE");

		TParm queryParm = this.getQueryParm(selectedPage);
		
		// 查询待入库的病历数据
		TParm resultParmUp = MROQueueTool.getInstance().selectMroOutIn(queryParm);
		if (resultParmUp.getCount() <= 0) {
			tableDown.setParmValue(new TParm());
			tableUp.setParmValue(new TParm());
			messageBox("查无数据");
			return false;
		}
		
		// 借阅入库
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
			return false;
		}
		
		// 向明细表中增加案卷类型列
		for (int i = 0; i < resultParmDown.getCount(); i++) {
			if (StringUtils.equals("I", resultParmDown.getValue("ADM_TYPE", i))) {
				resultParmDown.addData("ARCHIVES_TYPE", "住院病案");
			} else {
				resultParmDown.addData("ARCHIVES_TYPE", "门诊病案");
			}
		}
		
		this.tableDown.setParmValue(resultParmDown);
		
		// 设定焦点
		grabFocus("BOX_CODE");
		
		return true;
	}
	
	/**
	 * 第二页签查询入库数据
	 */
	private boolean onQueryTwo(int selectedPage) {
		// 清除行锁定
		tableNew.setLockRows("");
		
		// 查询待入库的病历数据
		TParm resultParm = MROQueueTool.getInstance().selectMroNew(this.getQueryParm(selectedPage));
		if (resultParm.getCount() <= 0) {
			tableNew.setParmValue(new TParm());
			messageBox("查无数据");
			//------------add yanglu 20181116 begin ---------------
	        this.clearValue("NEW_MR_NO") ;
			//------------add yanglu 20181116 end ---------------
			return false;
		}
		
		// add by wangbin 2015/2/10 需求分析 #968 增加建档状态列 START
		int dataCount = resultParm.getCount();
		for (int i = 0; i < dataCount; i++) {
			resultParm.addData("ARCHIVING_STATUS", "未建档");
			if (!StringUtils.isEmpty(resultParm.getValue("BOX_CODE", i))) {
				resultParm.setData("ARCHIVING_STATUS", i, "已建档");
			}
		}
		// add by wangbin 2015/2/10 需求分析 #968 增加建档状态列 END
		
		tableNew.setParmValue(resultParm);
		
		// 设定焦点
		grabFocus("NEW_MR_NO");
		//------------add yanglu 20181116 begin ---------------
        this.clearValue("NEW_MR_NO") ;
		//------------add yanglu 20181116 end ---------------
		
		return true;
	}
	
	/**
	 * 根据病案号查询
	 */
	public void onQueryByMrNo() {
		// 取得病案号
		String mrNo = this.getValueString("NEW_MR_NO").trim();
		if (StringUtils.isEmpty(mrNo)) {
			return;
		} else {
			Pat pat = Pat.onQueryByMrNo(mrNo);
			mrNo = pat.getMrNo();
			this.setValue("NEW_MR_NO", mrNo);
			// 查询数据
			if (this.onQuery()) {
				// 根据病案号查询到数据后，自动勾选，并弹出是否建档对话框
				tableNew.setItem(0, "FLG", "Y");
				tableNew.setSelectedRow(0);
				this.onCheckNewMroData(tableNew);
			}
		}
	}
	
	/**
	 * 获取查询条件数据
	 * 
	 * @return parm
	 */
	private TParm getQueryParm(int selectedPage) {
		TParm queryParm = new TParm();
		
        if (selectedPage == 0) {
        	// 入库状态(0_登记未出库,1_已出库,2_已归还)
    		queryParm.setData("ISSUE_CODE", "1");
    		queryParm.setData("S_DATE", this.getValueString("S_DATE").substring(0, 10));
    		queryParm.setData("E_DATE", this.getValueString("E_DATE").substring(0, 10));
    		// 入库方式
    		queryParm.setData("OUT_TYPE", getComboBox("IN_TYPE").getSelectedID());
    		if (!StringUtils.equals("L", this.pageParam)) {
    			// 门急住标识
    			queryParm.setData("ADM_TYPE", this.pageParam);
    		}
    		// 病案主档表的在库状态(0_未建档,1_在库,2出库)
    		queryParm.setData("IN_FLG", "2");
    		
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
        } else {
        	// 查询时间范围
    		queryParm.setData("NEW_S_DATE", this.getValueString("NEW_S_DATE").substring(0, 10));
    		queryParm.setData("NEW_E_DATE", this.getValueString("NEW_E_DATE").substring(0, 10));
    		// 门急住标识
    		queryParm.setData("ADM_TYPE", this.pageParam);
    		// 新建档
    		queryParm.setData("BOOK_BUILD", "Y");
    		// 预约取消标记
    		queryParm.setData("CANCEL_FLG", "N");
    		// 出库确认标记
    		queryParm.setData("CONFIRM_STATUS", "0");
    		// 在库标记(0_未建档)
    		queryParm.setData("IN_FLG", "0");
    		
    		// 门诊挂号
    		if ("O".equals(pageParam)) {
    			if (StringUtils.isNotEmpty(this.getValueString("NEW_CLINIC_AREA"))) {
    				// 诊区
    				queryParm.setData("ADM_AREA_CODE", this.getValueString("NEW_CLINIC_AREA"));
    			}
    		} else if ("I".equals(pageParam)) {
    			if (StringUtils.isNotEmpty(this.getValueString("NEW_ENDEMIC_AREA"))) {
    				// 病区
    				queryParm.setData("ADM_AREA_CODE", this.getValueString("NEW_ENDEMIC_AREA"));
    			}
    		}
    		
    		if (StringUtils.isNotEmpty(this.getValueString("NEW_DEPT_CODE"))) {
    			// 科室
				queryParm.setData("DEPT_CODE", this.getValueString("NEW_DEPT_CODE"));
    		}
    		
    		if (StringUtils.isNotEmpty(this.getValueString("NEW_MR_NO"))) {
    			// 医生
				queryParm.setData("MR_NO", this.getValueString("NEW_MR_NO"));
    		}
        }
		
		return queryParm;
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
        //获取当前选择的页签 索引
        int selectedPage = ((TTabbedPane)this.getComponent("tTabbedPane_0")).getSelectedIndex();
        String now = SystemTool.getInstance().getDate().toString().substring(0,
				10).replace('-', '/');
        
        if (selectedPage == 0) {
    		// 禁用案卷号
//    		this.callFunction("UI|BOX_CODE|setEnabled", false);
    		clearValue("CLINIC_AREA;ENDEMIC_AREA;BOX_CODE;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;LEND_AREA");
    		tableUp.setParmValue(new TParm());
    		tableDown.setParmValue(new TParm());
    		
    		// 第一页签
    		this.setValue("S_DATE", now); // 开始时间
    		this.setValue("E_DATE", now); // 结束时间
        } else {
        	clearValue("NEW_CLINIC_AREA;NEW_ENDEMIC_AREA;NEW_DEPT_CODE;NEW_DR_CODE;NEW_MR_NO");
    		
    		// 第二页签
    		this.setValue("NEW_S_DATE", now); // 开始时间
    		this.setValue("NEW_E_DATE", now); // 结束时间
        	
        	tableNew.setParmValue(new TParm());
        }
	}
	
	/**
	 * 保存入库信息
	 */
	private boolean onSave(TParm selParm) {
		TParm executeParm = new TParm();
		TParm result = new TParm();
		// 操作人员
		String optUser = Operator.getID();
		// 操作端末
		String optTerm = Operator.getIP();

		// 更新方法标记
		selParm.setData("TYPE", "IN");
		selParm.setData("OPT_USER", optUser);
		selParm.setData("OPT_TERM", optTerm);
		// 出库状态(0_未出库,1_已出库,2_已归还)
		selParm.setData("ISSUE_CODE", "2");
		// 病历在库状态
		selParm.setData("IN_FLG", "1");
		// 归还入库人员
		selParm.setData("IN_PERSON", selParm.getValue("DR_CODE"));
		// 当前借阅科室
		selParm.setData("CURT_LEND_DEPT_CODE", "");
		// 当前借阅人员
		selParm.setData("CURT_LEND_DR_CODE", "");

		executeParm.setData("MRV", selParm.getData());
		executeParm.setData("Queue", selParm.getData());

		// 执行病历出库
		result = TIOM_AppServer.executeAction("action.mro.MROQueueAction",
				"updateMroOutIn", executeParm);

		if (result.getErrCode() < 0) {
			this.messageBox("当前案卷入库失败");
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
		
		// 案卷册号
		String archivesNo = "";
		for (int i = 0; i < len; i++) {
			archivesNo = parmDown.getValue("BOX_CODE", i) + "-" + parmDown.getValue("ADM_TYPE", i) + "-" + parmDown.getValue("BOOK_NO", i);
			// 找到对应案卷号数据
			if (StringUtils.equals(archivesNo, this.getValueString("BOX_CODE"))) {
				
				// 如果当前数据已经被勾选，则不做任何处理
				if (parmDown.getBoolean("FLG", i)) {
					this.messageBox("当前数据已经完成入库");
					return;
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
			this.messageBox("无该案卷号数据");
			// 清空扫描到的案卷号
			clearValue("BOX_CODE");
			// 设定焦点
			grabFocus("BOX_CODE");
			return;
		}
	}
	
	/**
	 * 病案入库计数器
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
	 * 打印待出库病案报表
	 */
	public void onPrint() {
		int selectedPage = ((TTabbedPane)this.getComponent("tTabbedPane_0")).getSelectedIndex();
		if (selectedPage == 0) {
			// 打印入库确认单
			this.onPrintMroIn();
		} else {
			this.onPrintNewMro(selectedPage);
		}
	}
	
	/**
	 * 打印入库确认单
	 */
	private void onPrintMroIn () {
		TParm parm = tableUp.getShowParmValue();
		
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("无打印数据");
			return;
		}
		
		int dataLen = parm.getCount();
		
		// 打印用Parm
		TParm printParm = new TParm();
		// 打印数据
		TParm printData = new TParm();
		
		for (int i = 0; i < dataLen; i++) {
			if (!"L".equals(pageParam)) {
				// 诊区/病区
				printData.addData("ADM_AREA", parm.getValue("ADM_AREA_CODE", i));
			} else {
				// 借阅诊区/病区
				printData.addData("ADM_AREA", parm.getValue("LEND_AREA_DESC", i));
			}

			// 科室
			printData.addData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
			// 医生
			printData.addData("DR_CODE", parm.getValue("DR_CODE", i));
			// 应入库数量
			printData.addData("EXPECTED_IN_COUNT", parm.getValue("EXPECTED_OUT_COUNT", i));
		}
		
		printParm.setData("TITLE", "TEXT", "病历入库确认单");
		
		printData.setCount(dataLen);
		
		printData.addData("SYSTEM", "COLUMNS", "ADM_AREA");
		printData.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
		printData.addData("SYSTEM", "COLUMNS", "DR_CODE");
		printData.addData("SYSTEM", "COLUMNS", "EXPECTED_IN_COUNT");
		
		printParm.setData("PRINT_TABLE", printData.getData());
		
		this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROInPrint.jhw", printParm);
	}
	
	/**
	 * 打印新建病历归档
	 */
	private void onPrintNewMro (int selectedPage) {
		TParm parm = MROQueueTool.getInstance().selectMroNewToPrint(this.getQueryParm(selectedPage));
		tableNewPrint.setParmValue(parm);
		parm = tableNewPrint.getShowParmValue();
		
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("无打印数据");
			return;
		}
		
		int dataLen = parm.getCount();
		
		// 打印用Parm
		TParm printParm = new TParm();
		// 打印数据
		TParm printData = new TParm();
		
		for (int i = 0; i < dataLen; i++) {
			// 诊区/病区
			printData.addData("ADM_AREA", parm.getValue("ADM_AREA_CODE", i));
			// 科室
			printData.addData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
			// 医生
			printData.addData("DR_CODE", parm.getValue("DR_CODE", i));
			// 应入库数量
			printData.addData("EXPECTED_IN_COUNT", parm.getValue("EXPECTED_IN_COUNT", i));
		}
		
		printParm.setData("TITLE", "TEXT", "新建病历归档入库确认单");
		
		printData.setCount(dataLen);
		
		printData.addData("SYSTEM", "COLUMNS", "ADM_AREA");
		printData.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
		printData.addData("SYSTEM", "COLUMNS", "DR_CODE");
		printData.addData("SYSTEM", "COLUMNS", "EXPECTED_IN_COUNT");
		
		printParm.setData("PRINT_TABLE", printData.getData());
		
		this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROInPrint.jhw", printParm);
	}
	
	/**
	 * 勾选新建病历归档数据事件
	 */
	public void onCheckNewMroData(Object obj) {
		TParm result = new TParm();
		// 强制失去编辑焦点
		if (this.tableNew.getTable().isEditing()) {
			this.tableNew.getTable().getCellEditor().stopCellEditing();
		}
		// 当前选中的行号
		int selRow = tableNew.getSelectedRow();
		// 当前选中的行数据
		TParm parm = tableNew.getParmValue().getRow(selRow);
		// 操作人员
		String optUser = Operator.getID();
		// 操作端末
		String optTerm = Operator.getIP();
		// 当前锁定的行
		String lockedRows = "";
		// 查询条件
		TParm queryParm = new TParm();
		
		if (parm.getBoolean("FLG")) {
			// modify by wangbin 2015/01/23 建档前先验证是否已经存在案卷号
			queryParm.setData("ADM_TYPE", pageParam);
			queryParm.setData("MR_NO", parm.getValue("MR_NO"));
			
			// modify by wangbin 2015/01/26 首先验证该病患当前的建档状态
			result = PatTool.getInstance().getInfoForMrno(parm.getValue("MR_NO"));
			
			if (result.getErrCode() < 0) {
				this.messageBox("查询病患信息错误");
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
	            return;
			}
			
			if (!StringUtils.equalsIgnoreCase("Y", result.getValue("BOOK_BUILD", 0))) {
				this.messageBox("当前病历已经取消建档");
				// 清空当前行
				tableNew.removeRow(selRow);
				return;
			}
			
			// 查询该病案号的案卷信息
			result = MROQueueTool.getInstance().selectMRO_MRV(queryParm);
			
			if (result.getErrCode() < 0) {
				this.messageBox("查询案卷号出错");
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
	            return;
			}
			
			// 如果查询到该病案号的案卷数据
			if (result.getCount() > 0
					&& StringUtils.isNotEmpty(result.getValue("BOOK_NO", 0))) {
				this.messageBox("该病患已经分配过案卷号");
				tableNew.setValueAt(result.getValue("BOX_CODE", 0), selRow, 6);
				tableNew.setValueAt(result.getValue("BOOK_NO", 0), selRow, 7);
				// add by wangbin 2015/2/10 需求分析 #968 增加建档状态列 START
				tableNew.setValueAt("已建档", selRow, 8);
				// add by wangbin 2015/2/10 需求分析 #968 增加建档状态列 END
				
				// add by wangbin 2015/2/4 内部错误 #980 门诊病历入库归档界面与待出库确认界面数据状态不同步的问题 START
				TParm updateParm = new TParm();
				updateParm.setData("MRO_REGNO", parm.getValue("MRO_REGNO"));
				updateParm.setData("SEQ", parm.getValue("SEQ"));
				updateParm.setData("OPT_USER", optUser);
				updateParm.setData("OPT_TERM", optTerm);
				
				// 更新待出库确认状态
				result = MROBorrowTool.getInstance().updateConfirmStatus(updateParm);
				
				if (result.getErrCode() < 0) {
					this.messageBox("更新待出库状态错误");
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
		            return;
		        }
				// add by wangbin 2015/2/4 内部错误 #980 门诊病历入库归档界面与待出库确认界面数据状态不同步的问题 END
				
				lockedRows = tableNew.getLockRows() + "," + String.valueOf(selRow);
				// 勾选后锁定该行
				tableNew.setLockRows(lockedRows);
				return;
			}
			
			int sel = this.messageBox("建档", "是否建档？", 1);

			// 选择建档
			if (sel == 0) {
				
				result = MROQueueTool.getInstance().selectMaxBoxCode(queryParm);
				
				if (result.getErrCode() < 0) {
					this.messageBox("查询案卷号出错");
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
		            return;
		        }
				
				int newBoxCode = 0;
				// 取得当前数据库中的最大案卷号
				String maxBoxCode = result.getValue("MAX_BOX_CODE", 0);
				
				if (StringUtils.isEmpty(maxBoxCode)) {
					newBoxCode = 1;
				} else {
					newBoxCode = Integer.parseInt(maxBoxCode) + 1;
				}
				
				// 向左补位,分配新的案卷号
				String boxCode = String.format("%06d", newBoxCode);
				// 新的册号
				String bookNo = "01";
				
				// 病案在库状态更新为在库
				parm.setData("IN_FLG", "1");
				parm.setData("BOX_CODE", boxCode);
				parm.setData("BOOK_NO", bookNo);
				parm.setData("OPT_USER", optUser);
				parm.setData("OPT_TERM", optTerm);
				
				// 分配案卷号
				result = MROQueueTool.getInstance().updateMroMrvBoxCode(parm);
				
				if (result.getErrCode() < 0) {
					this.messageBox("分配案卷号出错");
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
		            return;
		        }
				
				TParm updateParm = new TParm();
				updateParm.setData("MRO_REGNO", parm.getValue("MRO_REGNO"));
				updateParm.setData("SEQ", parm.getValue("SEQ"));
				updateParm.setData("OPT_USER", optUser);
				updateParm.setData("OPT_TERM", optTerm);
				
				// 新建病历归档后，更新待出库确认状态
				result = MROBorrowTool.getInstance().updateConfirmStatus(updateParm);
				
				if (result.getErrCode() < 0) {
					this.messageBox("更新待出库状态错误");
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
		            return;
		        }
				
				tableNew.setValueAt(boxCode, selRow, 6);
				tableNew.setValueAt(bookNo, selRow, 7);
				// add by wangbin 2015/2/10 需求分析 #968 增加建档状态列 START
				tableNew.setValueAt("已建档", selRow, 8);
				// add by wangbin 2015/2/10 需求分析 #968 增加建档状态列 END
				
				lockedRows = tableNew.getLockRows() + "," + String.valueOf(selRow);
				// 勾选后锁定该行
				tableNew.setLockRows(lockedRows);
				
	            // add by wangbin 2015/1/4 增加建档后是否自动打印标签开关的判断 START
				if (StringUtils.equals(bigLabelPrtSwitch, IReportTool.ON)
						|| StringUtils.equals(smallLabelPrtSwitch,
								IReportTool.ON)) {
					TParm printParm = new TParm();
					// 案卷册号
					printParm.setData("BAR_CODE", boxCode + "-" + pageParam
							+ "-" + bookNo);
					// 病案号
					printParm.setData("MR_NO", parm.getValue("MR_NO"));
					// 打印新建归档病历的案卷号
					this.onPrintArchivesBarCode(printParm);
				}
	            // add by wangbin 2015/1/4 增加建档后是否自动打印标签开关的判断 END
			} else if (sel == 1) {
				// 病患信息表建档标记
				parm.setData("BOOK_BUILD", "N");
				// 清空客服建档状态标记
				result = MROQueueTool.getInstance().updateSysPatInfoBookBuild(parm);
				
				if (result.getErrCode() < 0) {
					this.messageBox("系统错误");
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
		            return;
		        }
				
				// 清空当前行
				tableNew.removeRow(selRow);
			} else {
				tableNew.setItem(selRow, "FLG", "N");
				return;
			}
		}
	}
	
	/**
	 * 页签点选事件
	 */
	public void onTabChange() {
        //获取当前选择的页签 索引
        int selectedPage = ((TTabbedPane)this.getComponent("tTabbedPane_0")).getSelectedIndex();
        
        if (selectedPage == 0) {
    		// 设定焦点
    		grabFocus("BOX_CODE");
    		
    		// 禁用分册按钮
    		callFunction("UI|separate|setEnabled", false);
        } else {
    		// 设定焦点
    		grabFocus("NEW_MR_NO");
    		
    		// 启用分册按钮
    		callFunction("UI|separate|setEnabled", true);
        }
	}
	
	
	/**
	 * 检验必填项
	 * 
	 * @param selectedPage
	 *            选中的页签
	 */
	private void validate(int selectedPage) {
        if (selectedPage == 0) {
    		if (StringUtils.isEmpty(this.getValueString("S_DATE"))
    				|| StringUtils.isEmpty(this.getValueString("E_DATE"))) {
    			this.messageBox("请输入归还日期范围");
    			return;
    		}
        } else {
    		if (StringUtils.isEmpty(this.getValueString("NEW_S_DATE"))
    				|| StringUtils.isEmpty(this.getValueString("NEW_E_DATE"))) {
    			this.messageBox("请输入查询日期范围");
    			return;
    		}
        }
	}
	
	/**
	 * 分册
	 */
	public void onSeparate() {
		if (null == tableNew.getShowParmValue()
				|| tableNew.getShowParmValue().getCount() <= 0) {
			this.messageBox("无分册数据");
			return;
		}
		
		// 强制失去编辑焦点
		if (this.tableNew.getTable().isEditing()) {
			this.tableNew.getTable().getCellEditor().stopCellEditing();
		}
		
		// 当前选中的行号
		int selRow = tableNew.getSelectedRow();
		// 当前选中的行数据
		TParm selParm = tableNew.getShowParmValue().getRow(selRow);
		
		if (selParm.getBoolean("FLG")) {
			if (StringUtils.isEmpty(selParm.getValue("BOX_CODE"))) {
				this.messageBox("请先进行归档操作");
				return;
			}
		} else {
			this.messageBox("请勾选数据");
			return;
		}
		
		// 设置门急住类别
		selParm.setData("ADM_TYPE", pageParam);
		
		// 弹出分册页面(模态框)
		this.openDialog("%ROOT%\\config\\mro\\MROArchivesManage.x", selParm);
	}
	
	/**
	 * 打印新建归档病历的案卷号
	 */
	private void onPrintArchivesBarCode(TParm parm) {
		// 打印用Parm
		TParm printParm = new TParm();
		
		TParm queryParm = new TParm();
		queryParm.setData("MR_NO", parm.getValue("MR_NO"));
		
		queryParm = MROQueueTool.getInstance().selectMroPrintData(queryParm);
		
		if (queryParm.getErrCode() < 0) {
			this.messageBox("查询病患信息出错");
			err("ERR:" + queryParm.getErrCode() + queryParm.getErrText()
					+ queryParm.getErrName());
            return;
        }
		
		// 条码
		printParm.setData("BAR_CODE", "TEXT", parm.getValue("BAR_CODE"));
		// 案卷册号
		printParm.setData("ARC_CODE", "TEXT", "案卷号:" + parm.getValue("BAR_CODE"));
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
		
		if (StringUtils.equals(bigLabelPrtSwitch, IReportTool.ON)) {
			// 打印大标签
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\MRO\\MROArchivesBarCodePrint.jhw",
					printParm, true);
		}
		
		if (StringUtils.equals(smallLabelPrtSwitch, IReportTool.ON)) {
			// 小标签一次打印四张
			for (int i = 0; i < 4; i++) {
				this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROArchivesBarCodeSmallPrint.jhw",
						printParm, true);
			}
		}
	}
	
	/**
	 * 汇出Excel
	 */
	public void onExport() {
        //获取当前选择的页签 索引
        int selectedPage = ((TTabbedPane)this.getComponent("tTabbedPane_0")).getSelectedIndex();
        TTable table = new TTable();
        String title = "";
        
        // 根据当前选中的页签取相应的表格数据
        if (selectedPage == 0) {
        	table = tableDown;
        	title = "出库病例入库归档";
        } else {
        	table = tableNew;
        	title = "新建病例入库归档";
        }
		
		TParm parm = table.getShowParmValue();
		if (null == parm || parm.getCount("MR_NO") <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		
		if (table.getRowCount() > 0) {
			parm.setData("TITLE", title);
			// 导出的Excel内容中去掉勾选框这一列
			parm.setData("HEAD", table.getHeader().replaceAll("选,30,boolean;", ""));
	        String[] header = table.getParmMap().split(";");
	        for (int i = 1; i < header.length; i++) {
	        	parm.addData("SYSTEM", "COLUMNS", header[i]);
	        }
	        TParm[] execleTable = new TParm[]{parm};
	        ExportExcelUtil.getInstance().exeSaveExcel(execleTable, title);
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
