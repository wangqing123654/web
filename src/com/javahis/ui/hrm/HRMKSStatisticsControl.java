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
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title:���Ҽ������ͳ�� </p>
 *
 * <p>Description:���Ҽ������ͳ�� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class HRMKSStatisticsControl extends TControl
{
	public TTable getTTable(String tag) {
		return (TTable)this.getComponent(tag);
	}
	private String start_Date;
	private String end_Date;
        private TTextFormat patName;
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
                patName=(TTextFormat)this.getComponent("EXEC_DR_CODE");
//                this.messageBox_(patName);
                String sql = "SELECT A.EXEC_DR_CODE AS ID,B.USER_NAME AS NAME FROM HRM_ORDER A,SYS_OPERATOR B WHERE A.DEPT_ATTRIBUTE IS NOT NULL AND A.EXEC_DR_CODE=B.USER_ID GROUP BY A.EXEC_DR_CODE,B.USER_NAME";
                TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
//                this.messageBox_(patParm);
                patName.setPopupMenuData(patParm);
                patName.setComboSelectRow();
                patName.popupMenuShowData();
		//this.onQuery();
	}

	/**
	 *  ��ѯ����
	 */
	public void onQuery() {
		start_Date=this.getValue("start_Date").toString().substring(0, 10).replace('-', '/') + " 00:00:00";
		end_Date=this.getValue("end_Date").toString().substring(0, 10).replace('-', '/') + " 23:59:59";
		String DEPT_ATTRIBUTE=this.getValueString("DEPT_ATTRIBUTE");
		String EXEC_DR_CODE=this.getValueString("EXEC_DR_CODE");
		String sql="";
		//============xueyf modify 20120222 start
		//��� ��Ҫ �����ѽɷ���δ���Ĳ��������� ���� AND a.exec_dr_code is not null
		//============xueyf modify 20120222 stop
		
       if("".equals(DEPT_ATTRIBUTE)&&"".equals(EXEC_DR_CODE))
       {
		sql=" SELECT  B.CHN_DESC, COUNT (A.CASE_NO)"+
		" FROM   HRM_ORDER A, SYS_DICTIONARY B "+
		" WHERE  A.DEPT_ATTRIBUTE IS NOT NULL"+
		" AND B.GROUP_ID = 'SYS_DEPT_ATTRIBUTE'"+
		" AND A.DEPT_ATTRIBUTE = B.ID"+
		" AND  A.SETMAIN_FLG='Y' "+   
		" AND BILL_DATE BETWEEN TO_DATE('"+start_Date+"','YYYY-MM-DD HH24:MI:SS')"+
		"  AND TO_DATE('"+end_Date+"','YYYY-MM-DD HH24:MI:SS') "+
		" GROUP BY A.DEPT_ATTRIBUTE, B.CHN_DESC";
       }
       if(!"".equals(DEPT_ATTRIBUTE)&&!"".equals(EXEC_DR_CODE))
       {
    	   sql=" SELECT  B.CHN_DESC, A.EXEC_DR_CODE,COUNT (A.CASE_NO)"+
   		" FROM   HRM_ORDER A, SYS_DICTIONARY B "+
   		" WHERE  A.DEPT_ATTRIBUTE IS NOT NULL"+
   		" AND B.GROUP_ID = 'SYS_DEPT_ATTRIBUTE'"+
   		" AND A.DEPT_ATTRIBUTE = B.ID"+
   		" AND  A.SETMAIN_FLG='Y' "+   
   		" AND B.ID='"+DEPT_ATTRIBUTE+"'"+
   		" AND A.EXEC_DR_CODE='"+EXEC_DR_CODE+"'"+
   		" AND BILL_DATE BETWEEN TO_DATE('"+start_Date+"','YYYY-MM-DD HH24:MI:SS')"+
   		"  AND TO_DATE('"+end_Date+"','YYYY-MM-DD HH24:MI:SS') "+
   		" GROUP BY A.DEPT_ATTRIBUTE, B.CHN_DESC,A.EXEC_DR_CODE";
       }
       if("".equals(DEPT_ATTRIBUTE)&&!"".equals(EXEC_DR_CODE))
       {
    	   sql=" SELECT  B.CHN_DESC,A.EXEC_DR_CODE, COUNT (A.CASE_NO)"+
   		" FROM   HRM_ORDER A, SYS_DICTIONARY B "+
   		" WHERE  A.DEPT_ATTRIBUTE IS NOT NULL"+
   		" AND B.GROUP_ID = 'SYS_DEPT_ATTRIBUTE'"+
   		" AND A.DEPT_ATTRIBUTE = B.ID"+
   		" AND  A.SETMAIN_FLG='Y' "+   
   		" AND A.EXEC_DR_CODE='"+EXEC_DR_CODE+"'"+
   		" AND BILL_DATE BETWEEN TO_DATE('"+start_Date+"','YYYY-MM-DD HH24:MI:SS')"+
   		"  AND TO_DATE('"+end_Date+"','YYYY-MM-DD HH24:MI:SS')  "+
   		" GROUP BY A.DEPT_ATTRIBUTE, B.CHN_DESC,A.EXEC_DR_CODE";
       }
       if(!"".equals(DEPT_ATTRIBUTE)&&"".equals(EXEC_DR_CODE))
       {
    	   sql=" SELECT  B.CHN_DESC, COUNT (A.CASE_NO)"+
   		" FROM   HRM_ORDER A, SYS_DICTIONARY B "+
   		" WHERE  A.DEPT_ATTRIBUTE IS NOT NULL"+
   		" AND B.GROUP_ID = 'SYS_DEPT_ATTRIBUTE'"+
   		" AND A.DEPT_ATTRIBUTE = B.ID"+
   		" AND  A.SETMAIN_FLG='Y' "+   
   		" AND B.ID='"+DEPT_ATTRIBUTE+"'"+
   		" AND BILL_DATE BETWEEN TO_DATE('"+start_Date+"','YYYY-MM-DD HH24:MI:SS')"+
   		"  AND TO_DATE('"+end_Date+"','YYYY-MM-DD HH24:MI:SS')  "+
   		" GROUP BY A.DEPT_ATTRIBUTE, B.CHN_DESC";
       }
       
		TParm selParm = new TParm();

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
					"�����������ͳ�Ʊ���");
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
				parm.addData("CHN_DESC",
						tableParm.getValue("CHN_DESC", i));
				parm.addData("COUNT(A.CASE_NO)",
						tableParm.getValue("COUNT(A.CASE_NO)", i));
				// System.out.println("num=" +
				//		tableParm.getValue("COUNT(A.CASE_NO)", i));
				ATM +=
					StringTool.getInt(tableParm.getValue("COUNT(A.CASE_NO)", i));
			}
			parm.setCount(parm.getCount("COUNT(A.CASE_NO)"));
			// System.out.println("parm====" + parm);
			parm.addData("SYSTEM", "COLUMNS", "CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "COUNT(A.CASE_NO)");
			// System.out.println("AFTER parm====" + parm);
			date.setData("tbl1", parm.getData());
			// ��β����
			date.setData("createUser", "TEXT", "�Ʊ���: " + Operator.getName());
			date.setData("pass", "TEXT", "�����: ");
			date.setData("ATM", "TEXT",
					"�ܼ�������" + ATM);
			// ���ô�ӡ����
			this.openPrintWindow("%ROOT%\\config\\prt\\HRM\\HRMKSStatistics.jhw",
					date);
		}
		else {
			this.messageBox("E0010");//����û�д�ӡ����
			return;
		}
	}
	/**
	 * ���Excel
	 */
	public void onExport() {
		//�õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		TTable table_d = getTTable("tbl1");
		ExportExcelUtil.getInstance().exportExcel(table_d, "���������˴�ͳ�Ʊ���");
	}
	/**
	 * ���
	 */
	public void onClear()
	{
		this.clearValue("EXEC_DR_CODE;DEPT_ATTRIBUTE");
		this.getTTable("tbl1").setSelectionMode(0);
		this.getTTable("tbl1").removeRowAll();
		onInit();
	}

}
