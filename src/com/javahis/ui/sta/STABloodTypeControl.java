package com.javahis.ui.sta;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: Ԥ��Ժ����Ѫ��ͳ�Ʊ�</p>
 *
 * <p>Description: Ԥ��Ժ����Ѫ��ͳ�Ʊ�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author kangy
 * @version 1.0
 */
public class STABloodTypeControl extends TControl{
private	String startDate="";
private	String endDate="";
private	SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
private	SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd");
private TTable table;
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		table=(TTable) this.getComponent("TABLE");
		Timestamp date = StringTool.rollDate(SystemTool.getInstance().getDate(), +30);
		startDate=sdf.format(SystemTool.getInstance().getDate());
		endDate=sdf.format(date);
		this.setValue("START_DATE", startDate);
		this.setValue("END_DATE", endDate);
	}
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String sql="SELECT ROWNUM as No,A.* FROM " +
				"(SELECT DISTINCT A.MR_NO,A.PAT_NAME,CASE  WHEN A.SEX_CODE = '1' THEN '��'" +
				" WHEN A.SEX_CODE = '2' THEN  'Ů' ELSE 'δ֪' END SEX_CODE, " +
				" A.PREGNANT_DATE,B.TEST_VALUE FROM SYS_PATINFO A,MED_LIS_RPT B, " +
				" OPD_ORDER C  WHERE A.MR_NO=C.MR_NO AND C.MED_APPLY_NO=B.APPLICATION_NO " +
				" AND B.TESTITEM_CODE='30500' AND A.PREGNANT_DATE IS NOT NULL " +
				" AND C.ADM_TYPE='O' AND A.PREGNANT_DATE BETWEEN TO_DATE('"+sdf.format(this.getValue("START_DATE"))+"','yyyy/MM/dd')" +
						" AND TO_DATE('"+sdf.format(this.getValue("END_DATE"))+"','yyyy/MM/dd') ORDER BY A.PREGNANT_DATE) A ";

		// add by wangb 2017/08/28 ����Ѫ�Ͳ�ѯ����
		if (getRadioButton("BLOOD_TYPE_A").isSelected()) {
			sql = sql + " WHERE A.TEST_VALUE = '" + getRadioButton("BLOOD_TYPE_A").getText() + "' ";
		} else if (getRadioButton("BLOOD_TYPE_B").isSelected()) {
			sql = sql + " WHERE A.TEST_VALUE = '" + getRadioButton("BLOOD_TYPE_B").getText() + "' ";
		} else if (getRadioButton("BLOOD_TYPE_AB").isSelected()) {
			sql = sql + " WHERE A.TEST_VALUE = '" + getRadioButton("BLOOD_TYPE_AB").getText() + "' ";
		} else if (getRadioButton("BLOOD_TYPE_O").isSelected()) {
			sql = sql + " WHERE A.TEST_VALUE = '" + getRadioButton("BLOOD_TYPE_O").getText() + "' ";
		}
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		table.setParmValue(result);
		this.setValue("TOTAL", result.getCount());
	} 
	/**
	 * ���
	 */
	public void onClear() {
		onInit();
		table.removeRowAll();
		getRadioButton("BLOOD_TYPE_ALL").setSelected(true);
		this.setValue("TOTAL", "");
	}
	/**
	 * ��ӡ
	 */
	public void onPrint() {
		TParm tableParm=new TParm();
	TParm tableData=table.getShowParmValue();
	tableData.setCount(tableData.getCount("MR_NO"));
	tableData.addData("SYSTEM", "COLUMNS", "NO");
	tableData.addData("SYSTEM", "COLUMNS", "MR_NO");
	tableData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
	tableData.addData("SYSTEM", "COLUMNS", "SEX_CODE");
	tableData.addData("SYSTEM", "COLUMNS", "PREGNANT_DATE");
	tableData.addData("SYSTEM", "COLUMNS", "TEST_VALUE");
	tableParm.setData("TABLE", tableData.getData());
	tableParm.setData("DATE","TEXT","��ѯ����[Ԥ����]:"+sd.format(this.getValue("START_DATE"))+"��"+sd.format(this.getValue("END_DATE")));
		this.openPrintDialog("%ROOT%\\config\\prt\\sta\\STABloodType.jhw",
				tableParm);
	}
	
	/**
	 * ����Excel
	 */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ���
		TParm parm = table.getShowParmValue();
		if (null == parm || parm.getCount("MR_NO") <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "Ԥ��Ժ����Ѫ��ͳ�Ʊ���");
	}
	
	/**
	 * ���TRadioButton����
	 * 
	 * @param tagName
	 * @return TRadioButton
	 */
	private TRadioButton getRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}
}
