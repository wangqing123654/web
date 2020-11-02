package com.javahis.ui.med;

import java.awt.Color;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.med.MedSmsTool;
import jdo.sta.bean.TimeStamp;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

public class MEDSmsMagControl extends TControl {

	/**
	 * TABLE
	 */
	private static String TABLE = "TABLE";

	// 记录表的选中行数
	int selectedRowIndex = -1;

	TTable table;

	private TParm data;
	String mrno;// 病案号

	// private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// private String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * saveFlg为insert时，插入；saveFlg为update时，更新；否则，不进行任何操作
	 */
	private String saveFlg = "";

	public void onInit() {
		// this.messageBox("111");
		super.onInit();// add by wangqing 20180130 call parent's onInit method
		table = (TTable) this.getComponent(TABLE);
		// 注册表格单击事件
		callFunction("UI|Table|addEventListener", "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
		// setStarLabel(false);
		// 初始化验收时间
		// 出库日期
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("END_TIME", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		this.setValue("BEGIN_TIME", date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		this.setValue("DEPT_CODE", Operator.getDept());
		this.setValue("SMS_STATE", "1");
		// onQuery();
		callFunction("UI|SAVE|setEnabled", false);
		// if(table.getRowCount() <= 0)
		// tableAddRow();

		// add by wangqing 20180130 set the status of SMS_STATE is enabled
		callFunction("UI|SMS_STATE|setEnabled", true);

	}

	/**
	 * 得到表格控件
	 * 
	 * @param tableName 表格名称
	 * @return
	 */
	public TTable getTable(String tableName) {
		return (TTable) this.getComponent(tableName);
	}

	/**
	 * 病案号回车方法
	 */
	public void onQueryNO() {
		TParm parm = new TParm();
		String mrNo = getValueString("MR_NO");
		String amdType = getValueString("ADM_TYPE");
		if (amdType == null || "".equals(amdType.trim())) {
			this.messageBox("门急住类别不能为空！");
			return;
		}
		String caseNo = "", ipdNo = "", bedNo = "";
		if (mrNo.length() > 0) {
			// mrNo = PatTool.getInstance().checkMrno(mrNo);
			// this.setValue("MR_NO", mrNo);
			//// parm.setData("ADM_TYPE", amdType);
			//// TParm result = MedSmsTool.getInstance().getPatInfoByMrNo(parm);
			//// if (result.getCount() <= 0) {
			//// this.messageBox("无对应病患信息！");
			//// return;
			//// }
			//// caseNo = result.getValue("CASE_NO");
			//// ipdNo = result.getValue("IPD_NO");
			//// bedNo = result.getValue("BED_NO");
			// // 如果新建标记位为true，则只给病人姓名赋值（就诊号、住院号、床号目前无法赋值），不执行查询
			// parm.setData("MR_NO", mrNo);
			// //查询病人姓名
			// TParm patName = MedSmsTool.getInstance().getPatName(mrNo);
			// if (patName.getCount() <= 0) {
			// this.messageBox("无对应病患信息！");
			// return;
			// }
			// String patNameValue = patName.getValue("PAT_NAME", 0);
			// setValue("PATIENT_NAME", patNameValue);
			//// setValue("IPD_NO", ipdNo);
			//// setValue("BED_NO", bedNo);

			mrNo = PatTool.getInstance().checkMrno(mrNo);
			this.setValue("MR_NO", mrNo);
			parm.setData("MR_NO", mrNo);
			parm.setData("ADM_TYPE", amdType);
			TParm result = MedSmsTool.getInstance().getPatInfoByMrNo(parm);
			if (result.getCount() <= 0) {
				this.messageBox("无对应病患信息！");
				return;
			}
			caseNo = result.getValue("CASE_NO", 0);
			ipdNo = result.getValue("IPD_NO", 0);
			bedNo = result.getValue("BED_NO", 0);
			String deptCode = result.getValue("DEPT_CODE", 0);
			String stationCode = "";
			// 诊区 病区
			if (amdType.equals("O") || amdType.equals("E")) {
				stationCode = result.getValue("CLINICAREA_CODE", 0);
				setValue("CLINIC_CODE", stationCode);// 病区/诊区
			} else if (amdType.equals("I")) {
				stationCode = result.getValue("STATION_CODE", 0);
				setValue("STATION_CODE", stationCode);// 病区/诊区
			}

			TParm pat = MedSmsTool.getInstance().getPat(mrNo);
			String patNameValue = pat.getValue("PAT_NAME", 0);
			String sexValue = pat.getValue("SEX_CODE", 0);
			Timestamp birthDay = pat.getTimestamp("BIRTH_DATE", 0);

			setValue("PATIENT_NAME", patNameValue);// 姓名
			setValue("SEX_NAME", sexValue);// 性别
			if (birthDay != null) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				String dateString = format.format(birthDay);
				setValue("BIRTHDAY", dateString);// 出生日期
			}
			setValue("CASE_NO", caseNo);// 就诊号
			setValue("IPD_NO", ipdNo);// 住院号
			setValue("BED_NO", bedNo);// 床号
			setValue("DEPT_CODE", deptCode);// 科室
		}
	}

	// 表格单击方法
	public void onTableClicked(int row) {
		if (row < 0) {
			return;
		} else {

			String tagNames = "ADM_TYPE;MR_NO;PATIENT_NAME;DEPT_CODE;SMS_CODE;"
					+ "TESTITEM_CHN_DESC;TEST_VALUE;BILLING_DOCTORS;"
					+ "HANDLE_OPINION;CASE_NO;BED_NO;IPD_NO;REPORT_USER;REPORT_DEPT_CODE;SEX_CODE;BIRTH_DAY;HANDLE_TIME;REPORT_TIME";
			setValueForParm(tagNames, data, row);
			// 登记时间
			if (data.getTimestamp("HANDLE_TIME", row) != null) {
				this.setValue("HANDLE_TIME",
						StringTool.getString(data.getTimestamp("HANDLE_TIME", row), "yyyy/MM/dd HH:mm:ss"));
			}
			// 报告时间
			System.out.println(data);
			if (data.getTimestamp("REPOTR_TIME", row) != null) {
				this.setValue("REPOTR_TIME", data.getTimestamp("REPOTR_TIME", row));
			}

			this.changeAdmType();
			TParm parm = table.getParmValue().getRow(row);
//			System.out.println(data);

			setValue("SEX_NAME", parm.getValue("SEX_CODE"));// 性别
			setValue("BIRTHDAY", parm.getValue("BIRTH_DAY"));// 出生日期
			setValue("IPD_NO", parm.getValue("IPD_NO"));// 住院号
			setValue("BED_NO", parm.getValue("BED_NO"));// 床号
			if ("I".equals(data.getValue("ADM_TYPE", row))) {
				this.setValue("STATION_CODE", data.getValue("STATION_CODE", row));
			} else {
				this.setValue("CLINIC_CODE", data.getValue("STATION_CODE", row));
			}
			this.setValue("SMS_STATE", data.getValue("STATE", row));

			selectedRowIndex = row;
			saveFlg = "update";

			// add by wangqing 20180130 start
			// if the message selected have been managed, set the state of save button is
			// disable
			if (data.getValue("STATE", row) != null && data.getValue("STATE", row).equals("9")) {
				callFunction("UI|SAVE|setEnabled", false);
			} else {
				callFunction("UI|SAVE|setEnabled", true);
			}
			// add by wangqing 20180130 end

			return;
		}
	}

	public void onQuery() {
		saveFlg = "";

		if (this.getComponent("SAVE") != null) {
			callFunction("UI|SAVE|setEnabled", true);
		}
		TParm selectCondition = getParmForTag("DEPT_CODE;SMS_STATE;MR_NO;ADM_TYPE", true);
		if (this.getValueString("ADM_TYPE").equals("I")) {
			selectCondition.setData("STATION_CODE", getValue("STATION_CODE"));
		} else {
			selectCondition.setData("STATION_CODE", getValue("CLINIC_CODE"));
		}
		selectCondition.setData("BEGIN_TIME", getValue("BEGIN_TIME"));
		selectCondition.setData("END_TIME", getValue("END_TIME"));
		selectCondition.setData("REPOTR_TIME", getValue("REPOTR_TIME"));

//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>{selectCondition:"+selectCondition+"}"+getValue("REPOTR_TIME"));
		data = MedSmsTool.getInstance().onQuery(selectCondition);
		System.out.println("查出的数据：>>>>>>>>>>>>>>>>>>>>>>>" + data);
		
		
		if (data.getErrCode() < 0) {
			messageBox("无符合条件的记录！");
			messageBox(data.getErrText());
			return;
		} else {
			// callFunction("UI|Table|setParmValue", new Object[] { data });

			for (int i = 0; i < data.getCount(); i++) {
				TParm p = data.getRow(i);
				System.out.println("{HANDLE_TIME:" + p.getValue("HANDLE_TIME") + "}");
				long time = getDiffTime(p.getTimestamp("HANDLE_TIME"));
				// System.out.println("time=========:"+time);
				if (!p.getValue("STATE").equals("9")) {
					if (time > 0 && time < 31) {
						/** 淡蓝色 */
						this.getTable(TABLE).setRowTextColor(i, new Color(0, 128, 255));
					}
					if (time > 30 && time < 41) {
						/** 蓝色 */
						this.getTable(TABLE).setRowTextColor(i, new Color(0, 0, 255));
					}
					if (time > 41) {
						/** 红色 */
						this.getTable(TABLE).setRowTextColor(i, new Color(255, 0, 0));
					}
				} else {
					this.getTable(TABLE).setRowTextColor(i, new Color(0, 0, 0));
				}
			}
			table.setParmValue(data);
			return;
		}
	}

	/**
	 * 清空事件
	 */
	public void onClear() {
		this.clearValue("ADM_TYPE;CASE_NO;IPD_NO;BED_NO;STATION_CODE;DEPT_CODE;"
				+ "BEGIN_TIME;END_TIME;MR_NO;HANDLE_OPINION;SMS_STATE;SMS_CODE;PATIENT_NAME;"
				+ "CLINIC_CODE;TESTITEM_CHN_DESC;TEST_VALUE;BILLING_DOCTORS;SEX_NAME;BIRTHDAY;HANDLE_TIME;REPOTR_TIME;");
		selectedRowIndex = -1;
		callFunction("UI|QUERY|setEnabled", true);
		TParm parm = new TParm();
		table.setParmValue(parm);
		this.getTable("TABLE").clearSelection(); // 清空TABLE选中状态
		// setStarLabel(false);
		// 初始化验收时间
		// 出库日期
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("END_TIME", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		this.setValue("BEGIN_TIME", date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		this.setValue("DEPT_CODE", Operator.getDept());
		callFunction("UI|SAVE|setEnabled", false);
		this.setValue("SMS_STATE", "1");
		// onInit();

		saveFlg = "";
	}

	/**
	 * 短信处理
	 */
	public void onSave() {
		// add by wangqing 20171229
		if (saveFlg != null && saveFlg.equals("insert") || saveFlg.equals("update")) {

		} else {
			return;
		}
		if (this.getValueString("ADM_TYPE") == null || this.getValueString("ADM_TYPE").length() <= 0) {
			this.messageBox("门急住类别不能为空！请选择！");
			this.grabFocus("ADM_TYPE");
			return;
		}
		if (this.getValueString("MR_NO") == null || this.getValueString("MR_NO").length() <= 0) {
			this.messageBox("病案号不能为空！请选择！");
			this.grabFocus("MR_NO");
			return;
		}
		if (this.getValueString("PATIENT_NAME") == null || this.getValueString("PATIENT_NAME").length() <= 0) {
			this.onQueryNO();
		}
		if (this.getValueString("DEPT_CODE") == null || this.getValueString("DEPT_CODE").length() <= 0) {
			this.messageBox("科室不能为空！请选择！");
			this.grabFocus("DEPT_CODE");
			return;
		}
		if (this.getValueString("ADM_TYPE").equals("I")) {
			if (this.getValueString("STATION_CODE") == null || this.getValueString("STATION_CODE").length() <= 0) {
				this.messageBox("病区不能为空！请选择！");
				this.grabFocus("STATION_CODE");
				return;
			}
		} else {
			if (this.getValueString("CLINIC_CODE") == null || this.getValueString("CLINIC_CODE").length() <= 0) {
				this.messageBox("诊区不能为空！请选择！");
				this.grabFocus("CLINIC_CODE");
				return;
			}
		}

		if (this.getValueString("BILLING_DOCTORS") == null || this.getValueString("BILLING_DOCTORS").length() <= 0) {
			this.messageBox("开单医生不能为空！请选择！");
			this.grabFocus("BILLING_DOCTORS");
			return;
		}
		TParm parm = new TParm();

		parm.setData("MR_NO", this.getValueString("MR_NO"));
		parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));

		parm.setData("SEX_CODE", this.getValueString("SEX_NAME"));
		parm.setData("BIRTH_DAY", this.getValueString("BIRTHDAY"));

		if (this.getValueString("ADM_TYPE").equals("I"))
			parm.setData("STATION_CODE", this.getValueString("STATION_CODE"));
		else
			parm.setData("STATION_CODE", this.getValueString("CLINIC_CODE"));
		parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
		parm.setData("PAT_NAME", this.getValueString("PATIENT_NAME"));
		parm.setData("BILLING_DOCTORS", this.getValueString("BILLING_DOCTORS"));
		parm.setData("DIRECTOR_DR_CODE", "");
		parm.setData("TESTITEM_CHN_DESC", this.getValueString("TESTITEM_CHN_DESC"));
		parm.setData("TEST_VALUE", this.getValueString("TEST_VALUE"));
		// modified by wangqing 20171229 爱育华#6090：危急值登记管理界面优化完善，要求去掉此字段
		// parm.setData("CRTCLLWLMT", this.getValueString("CRTCLLWLMT"));
		parm.setData("CRTCLLWLMT", "");
		parm.setData("BED_NO", this.getValueString("BED_NO"));
		parm.setData("CASE_NO", this.getValueString("CASE_NO"));
		parm.setData("IPD_NO", this.getValueString("IPD_NO"));

		parm.setData("HANDLE_USER", Operator.getID());// 登记者
		parm.setData("HANDLE_OPINION", this.getValueString("HANDLE_OPINION"));// 通知事项

		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("REPOTR_TIME", this.getValue("REPOTR_TIME"));

		// modified by wangqing 20171229
		if (saveFlg.equals("insert")) {// 新增
			TParm result = TIOM_AppServer.executeAction("action.med.MedSmsAction", "onSaveBySelf", parm);
			if (result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return;
			}
			this.messageBox("危急值登记成功！");
		} else {// 更新
				// 校验是否已经管理
			String smsCode = table.getParmValue().getValue("SMS_CODE", table.getSelectedRow());
			if (smsCode == null || smsCode.trim().length() <= 0) {
				this.messageBox("bug:::smsCode is null");
				return;
			}
			String sql = " SELECT SMS_CODE, CASE_NO, STATE FROM MED_SMS WHERE SMS_CODE='" + smsCode + "' ";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0 || result.getCount() <= 0) {
				this.messageBox("bug:::查询危急值信息失败");
				return;
			}
			String state = result.getValue("STATE", 0);
			if (state != null && state.equals("9")) {
				this.messageBox("此信息已经处理，不可更新");
				return;
			}
			// 更新操作
			parm.setData("SMS_CODE", smsCode);
			result = TIOM_AppServer.executeAction("action.med.MedSmsAction", "updateMedSms", parm);
			if (result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return;
			}
			this.messageBox("危急值更新成功！");
		}
		onClear();
		onQuery();
		saveFlg = "";
	}

	/**
	 * 两时间之差分钟
	 * 
	 * @param medSms
	 * @return
	 */
	private long getDiffTime(Timestamp time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String systemTime = StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss");
		String handleTime = StringTool.getString(time, "yyyy/MM/dd HH:mm:ss");
		Date begin = null;
		Date end = null;
		try {
			end = sdf.parse(systemTime);
			begin = sdf.parse(handleTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		/** 秒 **/
		long between = (end.getTime() - begin.getTime()) / 1000;
		/** 分钟 **/
		long minute = between / 60;
		return minute;
	}

	/**
	 * 危急值新登记
	 */
	public void onNew() {
		// table.removeRowAll();
		// table.clearSelection();
		// setStarLabel(true);
		// setTextField(true);
		// tableAddRow();
		callFunction("UI|QUERY|setEnabled", false);
		callFunction("UI|SAVE|setEnabled", true);
		saveFlg = "insert";
	}

	/**
	 * 给表格新增一行空行
	 */
	private void tableAddRow() {
		TParm parm = new TParm();
		parm.addData("ADM_TYPE", "");
		parm.addData("MR_NO", "");
		parm.addData("PATIENT_NAME", "");
		parm.addData("TESTITEM_CHN_DESC", "");
		parm.addData("TEST_VALUE", "");
		// modified by wangqing 20171229 爱育华#6090：危急值登记管理界面优化完善，要求去掉此字段
		// parm.addData("CRTCLLWLMT", "");
		parm.addData("STATE", "");
		parm.addData("DEPT_CODE", "");
		parm.addData("BILLING_DOCTORS", "");
		parm.addData("NOTIFY_DOCTORS_TIME", "");
		// parm.addData("DIRECTOR_DR_CODE", "");
		parm.addData("NOTIFY_DIRECTOR_DR_TIME", "");
		parm.addData("COMPETENT_CODE", "");
		parm.addData("NOTIFY_COMPETENT_TIME", "");
		parm.addData("DEAN_CODE", "");
		parm.addData("NOTIFY_DEAN_TIME", "");
		parm.addData("HANDLE_USER", "");
		parm.addData("HANDLE_TIME", "");
		parm.addData("SMS_CODE", "");
		parm.addData("HANDLE_OPINION", "");
		parm.addData("STATION_CODE", "");
		parm.addData("REPOTR_TIME", "");
		table.setParmValue(parm);
	}

	// /**
	// * 设置科室和病区是否可选
	// *
	// * @param b
	// */
	// private void setTextField(boolean b) {
	// ((TComboBox) this.getComponent("DEPT_CODE")).setEnabled(b);
	// ((TTextFormat) this.getComponent("STATION_CODE")).setEnabled(b);
	// }

	// /**
	// * 设置是否显示门急住别、病案号旁边的红星
	// *
	// * @param flag
	// */
	// public void setStarLabel(boolean flag) {
	// TLabel star_1 = (TLabel) this.getComponent("STAR_1");
	// TLabel star_2 = (TLabel) this.getComponent("STAR_2");
	// TLabel star_4 = (TLabel) this.getComponent("STAR_4");
	// star_1.setVisible(flag);
	// star_2.setVisible(flag);
	// star_4.setVisible(!flag);
	// }

	/**
	 * 门急住别下拉框值改变事件
	 */
	public void changeAdmType() {
		if (!this.getValueString("ADM_TYPE").equals("I")) {
			((TLabel) this.getComponent("AREA_LABEL")).setText("诊  区：");
			((TTextFormat) this.getComponent("CLINIC_CODE")).setVisible(true);
			((TTextFormat) this.getComponent("CLINIC_CODE")).setEnabled(true);
			((TTextFormat) this.getComponent("STATION_CODE")).setVisible(false);
			((TTextFormat) this.getComponent("STATION_CODE")).setEnabled(false);
		} else {
			((TLabel) this.getComponent("AREA_LABEL")).setText("病  区：");
			((TTextFormat) this.getComponent("CLINIC_CODE")).setVisible(false);
			((TTextFormat) this.getComponent("CLINIC_CODE")).setEnabled(false);
			((TTextFormat) this.getComponent("STATION_CODE")).setVisible(true);
			((TTextFormat) this.getComponent("STATION_CODE")).setEnabled(true);
		}
	}

	/**
	 * yanglu20180905 点击报告时间按钮
	 */
	public void clickReportTime() {
		TParm sendParm = new TParm();

		sendParm.setData("MR_NO", this.getValue("MR_NO"));
//		System.out.println("+++++++++++++++++++++++"+this.getValue("MR_NO"));
		TParm reportTime = (TParm) this.openDialog("%ROOT%\\config\\onw\\ONWPlanReport.x", sendParm, false);
//		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+reportTime.getData("EXAMINE_DATE"));

		this.setValue("REPOTR_TIME", reportTime.getData("EXAMINE_DATE"));

	}
}
