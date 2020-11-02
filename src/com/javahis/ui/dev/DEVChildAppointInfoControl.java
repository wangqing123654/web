package com.javahis.ui.dev;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdo.dev.DEVChildAppointInfoTool;
import jdo.spc.StringUtils;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title:��������Ϣ��ѯ
 * </p>
 * 
 * <p>
 * Description: ��������Ϣ��ѯ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wukai
 * @version 4.5
 */
public class DEVChildAppointInfoControl extends TControl {
	/**
	 * ��Ϣ���
	 */
	private TTable tableChildInfo;
	/**
	 * ���ݿ������
	 */
	private DEVChildAppointInfoTool mTool;
	/**
	 * ��ʾ��ѯ���������
	 */
	private TLabel tResNums;
	/**
	 * ѡ����������������42�졢3�¡�6�¡�9�¡�1����ĺ���
	 */
	private TComboBox tAgeDays;
	/**
	 * δ������
	 */
	private TNumberTextField tFetureDays;
	/**
	 * ��ѯ���еĻ���sql���
	 */
	private String baseSQL;
	/**
	 * ȫ�ֳ���ʱ��
	 */
	private String birthDay = "0";
	/**
	 * ȫ��δ������
	 */
	private String fetureDay = "7";

	@Override
	public void onInit() {
		super.onInit();
		mTool = DEVChildAppointInfoTool.getNewInstance();
		tableChildInfo = (TTable) this.getComponent("ChildTable");
		tResNums = (TLabel) this.getComponent("SearchNums");
		tAgeDays = (TComboBox) this.getComponent("ComboxAge");
		tFetureDays = (TNumberTextField) this.getComponent("NumberFutDays");
		initTable();
		baseSQL = "SELECT b.MR_NO,b.PAT_NAME, "
				+ "(CASE WHEN b.SEX_CODE = '1' THEN '��' WHEN b.SEX_CODE='2' THEN 'Ů' WHEN b.SEX_CODE='0' THEN 'δ֪' END) AS SEX_CODE, "
				+ "b.BIRTH_DATE,m.MOTHER_NAME,m.CELL_PHONE FROM "
				+ "(SELECT a.CASE_NO,A.IPD_NO, s.PAT_NAME , s.MR_NO, s.BIRTH_DATE ,s.SEX_CODE from SYS_PATINFO s, ADM_INP a WHERE s.MR_NO in (SELECT MR_NO FROM sys_patinfo WHERE new_born_flg='Y') and s.MR_NO = a.MR_NO ORDER BY S.MR_NO)"
				+ "b, "
				+ "(SELECT s.PAT_NAME AS MOTHER_NAME,s.BIRTH_DATE AS MOTHER_DATE, s.CELL_PHONE, s.MR_NO FROM SYS_PATINFO s WHERE MR_NO in (SELECT IPD_NO FROM adm_inp WHERE new_born_flg='Y' ) ORDER BY s.MR_NO ) "
				+ "m WHERE b.IPD_NO = m.MR_NO";
	}

	/**
	 * ������е�����������
	 */
	private void initTable() {
		TParm t = mTool.onQueryAll();
		if (t != null) {
			tableChildInfo.setParmValue(mTool.onQueryAll());
			tResNums.setText(t.getCount() + " ��");
		}

	}

	/**
	 * ��������ѯ��ť ������ѯ��ǰ���ӵ���Ϣ
	 */
	public void onSearch() {
		// ��������������
		String ageDays = tAgeDays.getText();
		// String unit = this.getText("TimeUnit");
		this.birthDay = ageDays;
		int num = 0;
		if (StringUtils.isEmpty(ageDays)) {
			num = 0;
		} else {
			String numStr = getNum(ageDays);
			if (StringUtils.isEmpty(numStr)) {
				this.messageBox("������Ϸ���ʱ��!");
				return;
			}
			num = Integer.parseInt(numStr);
			if (ageDays.contains("��"))
				num = num * 30;
			else if (ageDays.contains("��") || ageDays.contains("����"))
				num = num * 365;
			// if(unit.equals("��") || unit.equals("")) {
			// num = num * 1;
			// } else if(unit.equals("��")) {
			// num = num * 7;
			// } else if(unit.equals("����")) {
			// num = num * 365;
			// }
		}
		ageDays = String.valueOf(num);
		// ����δ�����������
		String fetureDays = tFetureDays.getText();
		if (StringUtils.isEmpty(fetureDays)) {
			fetureDays = "0";
		}
		this.fetureDay = fetureDays;
		// this.messageBox("ageDays:" + ageDays + ",fetureDays:" + fetureDays);
		StringBuffer sb = new StringBuffer();
		if (!ageDays.equals("0") || !fetureDays.equals("0")) {
			sb.append(" AND (SELECT trunc(sysdate - b.birth_date) FROM dual) > ");
			sb.append(ageDays);
			sb.append(" AND (SELECT trunc(sysdate - b.birth_date) FROM dual) < ");
			sb.append(Integer.parseInt(ageDays) + Integer.parseInt(fetureDays));
		}
		String sql = baseSQL + sb.toString();
		TParm res = new TParm(TJDODBTool.getInstance().select(sql));
		tableChildInfo.setParmValue(res);
		if (res.getCount() <= 0) {
			tResNums.setText("0 ��");
		} else {
			tResNums.setText(res.getCount() + " ��");
		}

	}

	/**
	 * ��һ���ַ����е�������ȡ����
	 * 
	 * @param str
	 * @return
	 */
	private String getNum(String str) {
		// int num = 0;
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * add by wukai on 20160512 ��ӡ����
	 */
	public void onPrint() {
		// this.messageBox("print");
		int rowCount = tableChildInfo.getRowCount();
		if (rowCount <= 0) {
			this.messageBox("û��Ҫ��ӡ������");
			return;
		}
		TParm data = new TParm();
		// �����ͷ����
		data.setData("TABLENAME", "TEXT", "��ͯ��������ö�ͯ��Ϣ��");
		// ��ӡʱ�䷶Χ
		//this.messageBox(this.birthDay + "," + this.fetureDay);
		if ("0".equals(this.birthDay) || StringUtils.isEmpty(birthDay)
				&& "0".equals(this.fetureDay)) {                      
			data.setData("TimeRange", "TEXT", "����������" + "����ʱ��");
		} else {
			StringBuilder sb = new StringBuilder();
			if (StringUtils.isEmpty(this.birthDay)) {
				sb.append("0  ��  ");
				sb.append(this.fetureDay + "��");
			} else {
				sb.append(this.birthDay);
				sb.append("  ��  ");
				if(this.birthDay.contains("��")) {
					int days = Integer.parseInt(getNum(this.birthDay)) + Integer.parseInt(this.fetureDay);
					sb.append(days + "��");
				} else {
					sb.append(this.birthDay + this.fetureDay + "��");
				}
				
			}
			data.setData("TimeRange", "TEXT", "����������" + sb.toString()); // ʱ�䷶Χ
		}
		// ��ӡ��ǰʱ��ʹ�ӡ��
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date());
		data.setData("PrintTime", "TEXT", "��ӡʱ�䣺" + time);
		data.setData("PrintPeople", "TEXT", "�Ʊ��ˣ�" + Operator.getName());

		// ������ݴ���CASE_NO, PAT_NAME, SEX_CODE, BIRTH_DATE, MOTHER_NAME,
		// CELL_PHONE
		TParm parm = new TParm();
		TParm tableParm = tableChildInfo.getParmValue();
		for (int i = 0; i < rowCount; i++) {
			parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
			parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
			parm.addData("SEX_CODE", tableParm.getValue("SEX_CODE", i));
			parm.addData("BIRTH_DATE", tableParm.getValue("BIRTH_DATE", i));
			parm.addData("MOTHER_NAME", tableParm.getValue("MOTHER_NAME", i));
			parm.addData("CELL_PHONE", tableParm.getValue("CELL_PHONE", i));
		}
		parm.setCount(rowCount);
		parm.addData("SYSTEM", "COLUMNS", "MR_NO");
		parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		parm.addData("SYSTEM", "COLUMNS", "SEX_CODE");
		parm.addData("SYSTEM", "COLUMNS", "BIRTH_DATE");
		parm.addData("SYSTEM", "COLUMNS", "MOTHER_NAME");
		parm.addData("SYSTEM", "COLUMNS", "CELL_PHONE");

		data.setData("TABLE", parm.getData());
		// TODO ���÷�����ӡ����
		this.openPrintWindow("%ROOT%\\config\\prt\\ChildAppointInfo.jhw", data,
				true);
	}
}
