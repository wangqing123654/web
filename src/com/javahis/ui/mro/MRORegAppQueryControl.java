package com.javahis.ui.mro;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

import jdo.mro.MROBorrowTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title:
 * 
 * <p>
 * Description:预约挂号数据提取
 * 
 * <p>
 * Copyright:
 * 
 * <p>
 * Company: JavaHis       
 * </p>
 * 
 * @author chenx
 * @version 4.0
 */
public class MRORegAppQueryControl extends TControl {
	
	private TTable table;

	/**
	 * 初始化
	 */

	public void onInit() {
		super.onInit();
		this.onInitPage();
	}

	/**
	 * 初始化界面
	 */
	public void onInitPage() {
		// 默认显示第二天日期
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(SystemTool.getInstance().getDate());
		calendar.add(Calendar.DATE, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String tommorwDate = sdf.format(calendar.getTime());
		
		this.setValue("S_DATE", tommorwDate); // 开始时间
		table = (TTable) this.getComponent("TABLE");
	}
	
	/**
	 * 数据提取
	 */
	public void onQuery() {
		TParm data = getQueryParm();
		// webservice 获取crm数据
		TParm parm = TIOM_AppServer.executeAction("action.reg.REGCRMAction",
				"getOrder", data);
		if (parm.getCount() <= 0) {
			table.setParmValue(new TParm());
			this.messageBox("查无数据");
			return;
		}
		
		// 将crm数据插入到中间表MRO_REG
		this.getCrmParm(parm);
		
		TParm result = TIOM_AppServer.executeAction(
				"action.mro.MROBorrowAction", "insertMroReg", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("CRM数据插入临时表时发生异常");
			err("ERR:" + result.getErrCode() + result.getErrName()
					+ result.getErrText());
			return;
		}
		
		// 筛除非预约数据
		// modify by wangb 2017/11/24 按照CRM给出的回复，从CRM接口提取到的数据全部为有效预约，故无需过滤数据
//		this.dealNotAppointmentCrmData(parm);
		
		if (parm.getCount() <= 0) {
			table.setParmValue(new TParm());
			this.messageBox("查无数据");    
			return;
		}
		
		table.setParmValue(parm);
		this.messageBox("数据提取完毕");
	}

	/**
	 * 将得到的crm数据整理
	 * 
	 * @param parm
	 * @return
	 */
	public void getCrmParm(TParm parm) {
		
		// 取得VIP挂号信息
		TParm vipInfoParm = new TParm();
		
		for (int i = parm.getCount() - 1; i > -1; i--) {
			// modify by wangbin 2015/1/7 过滤掉初诊即无病案号的预约病人
			if (StringUtils.isEmpty(parm.getValue("MR_NO", i))) {
				parm.removeRow(i);
				continue;
			}
		}
		
		for (int i = parm.getCount() - 1; i > -1; i--) {
			parm.addData("MRO_REGNO", "");
			parm.addData("SEQ", "");
			parm.addData("BOOK_ID", parm.getValue("CRM_ID", i));
			// 0_预约挂号(APP), 1_现场挂号(LOC), 2_住院登记
			parm.addData("ORIGIN_TYPE", "0");
			// 门急住标识
			parm.addData("ADM_TYPE", "O");
			// 就诊号
			parm.addData("CASE_NO", "");
			// 待出库确认状态
			parm.addData("CONFIRM_STATUS", "0");
			
			// 取得CRM预约挂号信息
			vipInfoParm = MROBorrowTool.getInstance().queryVipRegInfo(parm.getRow(i));
			
			if (vipInfoParm.getErrCode() < 0) {
				err(vipInfoParm.getErrCode() + " " + vipInfoParm.getErrText());
				this.messageBox("取得预约挂号信息异常");
				return;
			}
			
			// 诊区
			parm.addData("ADM_AREA_CODE", vipInfoParm.getValue("REG_CLINICAREA", 0));
			// 挂号时段
			parm.addData("SESSION_CODE", vipInfoParm.getValue("SESSION_CODE", 0));
			// 诊号
			parm.addData("QUE_NO", vipInfoParm.getValue("QUE_NO", 0));
			// 取消注记(Y_取消,N_未取消)
			parm.addData("CANCEL_FLG", "N");
			parm.addData("OPT_USER", Operator.getID());
			parm.addData("OPT_TERM", Operator.getIP());
		}
	}
	
	/**
	 * 获取查询参数
	 * 
	 * @return
	 */
	public TParm getQueryParm() {
		TParm queryParm = new TParm();
		queryParm.setData("admDate", getValueString("S_DATE"));
		queryParm.setData("session", "");
		queryParm.setData("deptCode", getValueString("DEPT_CODE"));
		queryParm.setData("drCode", getValueString("DR_CODE"));
		return queryParm;

	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.onInit();
		this.clearValue("DEPT_CODE;DR_CODE");
		table.removeRowAll();
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		// 得到UI对应控件对象的方法
		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount("MR_NO") <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "挂号预约数据");
	}
	
	/**
	 * 处理crm数据删除非预约数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm dealNotAppointmentCrmData(TParm parm) {
		for (int i = parm.getCount() - 1; i > -1; i--) {
			if (!"1,4,5".contains(parm.getValue("STATUS", i))) {
				parm.removeRow(i);
			}
		}
		
		return parm;
	}
}
