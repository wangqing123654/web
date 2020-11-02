package com.javahis.ui.hrm;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

public class HRMCJBloodstatisticsControl extends TControl
{
	public TTable getTTable(String tag) {
		return (TTable)this.getComponent(tag);
	}
	private String start_Date;
	private String end_Date;
	private String ORDER_DEPT_CODE;
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// �õ���ǰʱ��
		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("end_Date",
				date.toString().substring(0, 10).replace('-', '/'));
		this.setValue("start_Date",
				StringTool.rollDate(date, -7).toString().substring(0, 10).
				replace('-', '/'));
		setValue("PURORDER_DATE", date);
	}

	/**
	 *  ��ѯ����
	 */
	public void onQuery() {
		start_Date=this.getValue("start_Date").toString().substring(0, 10).replace('-', '/') + " 00:00:00";
		end_Date=this.getValue("end_Date").toString().substring(0, 10).replace('-', '/') + " 23:59:59";
		ORDER_DEPT_CODE=this.getValueString("ORDER_DEPT_CODE");
		String sql=" SELECT   COUNT (DISTINCT MA.APPLICATION_NO) AS COUNT,SD.DEPT_CHN_DESC AS DES "+
		"   FROM   MED_APPLY MA,SYS_DEPT SD"+
		" WHERE  MA.CAT1_TYPE = 'LIS'"+
		" AND SD.DEPT_CODE=MA.DEPT_CODE"+
		" AND  MA.ORDER_DATE BETWEEN TO_DATE('"+start_Date+"','YYYY-MM-DD HH24:MI:SS')"+
		"  AND TO_DATE('"+end_Date+"','YYYY-MM-DD HH24:MI:SS')";
		if(!"".equals(ORDER_DEPT_CODE))
//		{
//			sql+= "AND SD.DEPT_CODE = '"+ORDER_DEPT_CODE+"'";
//		}
//		else
		{
			sql+= "AND MA.DEPT_CODE = '"+ORDER_DEPT_CODE+"'";
		}
		sql+=" AND OPTITEM_CHN_DESC LIKE '%Ѫ%' GROUP BY   SD.DEPT_CHN_DESC";
		TParm selParm = new TParm();
                // System.out.println("sql==="+sql);
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		this.getTTable("tbl1").setParmValue(selParm);
		if(getTTable("tbl1").getRowCount()<1)
			this.messageBox("E0008");//������ʾ�Ի��򣨡�û�����ݡ���
	}

	/**
	 *  ��ӡ����
	 */
	public void onPrint() {
		int ATM = 0;
		TTable table_d = getTTable("tbl1");
		if (table_d.getRowCount() > 0) {
			// ��ӡ����OPT_USER;COUNT(CASE_NO)
			TParm date = new TParm();

			// ��ͷ����
			date.setData("Title", "TEXT",
					Manager.getOrganization().
					getHospitalCHNFullName(Operator.getRegion()) +
			"��Ѫ������ͳ�Ʊ���");
			date.setData("createDate", "TEXT",
					"�Ʊ�����: " +
					SystemTool.getInstance().getDate().toString().
					substring(0, 10).replace('-', '/'));
			date.setData("start_Date", "TEXT",
					"��ʼʱ��: " +
					start_Date.
					substring(0, 10).replace('-', '/'));
			date.setData("end_Date", "TEXT",
					"����ʱ��: " +
					end_Date.
					substring(0, 10).replace('-', '/'));
			// ������� B.CHN_DESC, COUNT (A.CASE_NO)
			TParm parm = new TParm();
			TParm tableParm = table_d.getParmValue();
			// System.out.println("tableParm====" + tableParm);
			int count = tableParm.getCount();

			for (int i = 0; i < count; i++) {
				parm.addData("DES",
						tableParm.getValue("DES", i));
				parm.addData("COUNT",
						tableParm.getValue("COUNT", i));
				ATM +=
					StringTool.getInt(tableParm.getValue("COUNT", i));
			}
			parm.setCount(parm.getCount("COUNT"));
			// System.out.println("parm====" + parm);
			parm.addData("SYSTEM", "COLUMNS", "DES");
			parm.addData("SYSTEM", "COLUMNS", "COUNT");
			date.setData("tbl1", parm.getData());
			// ��β����
			date.setData("createUser", "TEXT", "�Ʊ���: " + Operator.getName());
			date.setData("pass", "TEXT", "�����: ");
			date.setData("ATM", "TEXT",
					"�ϼƼ����˴Σ�" + ATM);
			// ���ô�ӡ����
			this.openPrintWindow("%ROOT%\\config\\prt\\HRM\\HRMCJBloodstatistics.jhw",
					date);
		}
		else {
			this.messageBox("E0010");//������ʾ�Ի��򣨡�û��ӡ�����ݡ���
			return;
		}
	}
	/**
	 * ���Excel
	 */
	public void onExport() {
		//�õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		TTable table_d = getTTable("tbl1");
		ExportExcelUtil.getInstance().exportExcel(table_d, "����������ͳ�Ʊ���");
	}
	/**
	 * ���
	 */
	public void onClear()
	{
		this.clearValue("ORDER_DEPT_CODE");
		this.getTTable("tbl1").setSelectionMode(0);
		this.getTTable("tbl1").removeRowAll();
		onInit();
	}

}
