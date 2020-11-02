package com.javahis.ui.ins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.adm.ADMResvTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 预约未结案
 * </p>
 * 
 * <p>
 * Description: 资格确认书预约未结案
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 20111128
 * @version 1.0
 */
public class INSResvNCloseControl extends TControl {
	private int selectrow = -1;// 选择的行
	SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	private String flg;// 区分查询数据

	public void onInit() {
		super.onInit();
		// 得到前台传来的数据并显示在界面上
		TParm recptype = (TParm) getParameter();
		setValueForParm("REGION_CODE;DR_CODE;DEPT_CODE", recptype, -1);
		flg = recptype.getValue("FLG");// 走分支
		DateFormat df = new SimpleDateFormat("yyyy");
		if (flg.equals("Y")) {// 预约未结案
			// 预设就诊时间段
			String date = df.format(SystemTool.getInstance().getDate())
					+ "-01-01";
			this.callFunction("UI|STARTTIME|setValue", StringTool.getTimestamp(
					date, "yyyy-MM-dd"));
			this.callFunction("UI|ENDTIME|setValue", SystemTool.getInstance()
					.getDate());
		} else {// 跨年度查询
			// TLabel A= new TLabel();
			this.setTitle("跨年度医保患者查询");
			((TLabel) this.getComponent("LBL")).setValue("跨年度查询:");
			callFunction("UI|ENDTIME|setVisible", false);
			callFunction("UI|tButton_1|setVisible", false);
			callFunction("UI|LBL1|setVisible", false);
			callFunction("UI|STARTTIME|setEnabled", false);
			String year = df.format(SystemTool.getInstance().getDate());
			int yearTemp = Integer.parseInt(year) - 1;// 获得年数

			this.callFunction("UI|STARTTIME|setValue", StringTool.getTimestamp(
					yearTemp + "-12-31", "yyyy-MM-dd"));
		}
		// table1的单击侦听事件
		callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// table1的单击侦听事件
		callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.DOUBLE_CLICKED, this, "onTableDoubleClicked");

		onQuery();
	}

	/**
	 *增加对Table的监听
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		// 接收所有事件
		this.callFunction("UI|TABLE|acceptText");
		// TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
		selectrow = row;
	}

	public void onTableDoubleClicked(int row) {
		TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
		this.setReturnValue(data.getRow(row));
		this.callFunction("UI|onClose");
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		TParm parm = new TParm();
		if (this.getValue("REGION_CODE").toString().length() > 0) {
			parm.setData("REGION_CODE", this.getValue("REGION_CODE"));
		}
		if (this.getValueString("DR_CODE").length() > 0) {
			parm.setData("DR_CODE", this.getValue("DR_CODE"));
		}
		if (this.getValueString("DEPT_CODE").length() > 0) {
			parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
		}
		if (this.getValue("STARTTIME").toString().length() > 0) {
			parm.setData("START_DATE", df1.format(this.getValue("STARTTIME"))+"000000");
		}
		//病区
		if (this.getValueString("STATION_CODE").length()>0) {
			parm.setData("STATION_CODE",this.getValue("STATION_CODE"));	
		}
		TParm result=null;
		System.out.println();
		if (flg.equals("Y")) {//预约未结案
			if (this.getValue("ENDTIME").toString().length() > 0) {
				parm.setData("END_DATE", df1.format(this.getValue("ENDTIME"))+"235959");
			}
			result = ADMResvTool.getInstance().queryResvNClose(parm);
		}else{//跨年度
			result = ADMResvTool.getInstance().overYearNHIPatInfo(parm);
		}
	   
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			err(result.getErrText() + ":" + result.getErrName());
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("E0008");
			TTable table = (TTable) this.callFunction("UI|TABLE|getThis");
			table.removeRowAll();
			return;
		}
		this.callFunction("UI|TABLE|setParmValue", result);
	}

	/**
    *
    */
	public void onOK() {
		TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
		this.setReturnValue(data.getRow(selectrow));
		this.callFunction("UI|onClose");
	}
}
