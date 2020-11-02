/**
 * 
 */
package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.util.Date;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 
 * </p>
 * 
 * <p>
 * Description: ����������ϸ��ѯ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author wu 2012-7-25����16:59:55
 * @version 1.0
 */
public class BILHRMIncomeDetailControl extends TControl {
	private TTable table;
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}
	/**
	 * ��ʼ��ҳ��
	 */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		table = (TTable) getComponent("TABLE");	
		// ��ʼ����ѯ����
		this.setValue("E_DATE",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 23:59:59");
		this.setValue("S_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
	}	
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String date_s = getValueString("S_DATE");
		String date_e = getValueString("E_DATE");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("��������Ҫ��ѯ��ʱ�䷶Χ");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		String sql = 
		    " SELECT   TO_CHAR (A.BILL_DATE, 'YYYY-MM-DD') BILL_DATE, B.DEPT_CODE," +
		    " B.EXEC_DEPT_CODE, C.DEPT_CHN_DESC AS DEPT," +
		    " D.DEPT_CHN_DESC AS DEPT_CHN_DESC, B.REXP_CODE, E.CHN_DESC," +
		    " SUM (B.AR_AMT) AS AR_AMT" +
		    " FROM BIL_OPB_RECP A, HRM_ORDER B, SYS_DEPT C, SYS_DEPT D," +
		    " SYS_DICTIONARY E" +
		    " WHERE A.ADM_TYPE = 'H'" +
		    " AND A.BILL_DATE BETWEEN TO_DATE ('" + date_s + "', 'YYYYMMDDHH24MISS')" +
		    " AND TO_DATE ('" + date_e + "', 'YYYYMMDDHH24MISS')" +
		    " AND A.RECEIPT_NO = B.RECEIPT_NO" +
		    //" AND A.CASE_NO = B.CONTRACT_CODE" +
		    " AND B.DEPT_CODE = C.DEPT_CODE" +
		    " AND B.EXEC_DEPT_CODE = D.DEPT_CODE" +
		    " AND B.REXP_CODE = E.ID" +
		    " AND E.GROUP_ID = 'SYS_CHARGE'" +
		    " GROUP BY TO_CHAR (A.BILL_DATE, 'YYYY-MM-DD')," +
		    " B.DEPT_CODE," +
		    " B.EXEC_DEPT_CODE," +
		    " C.DEPT_CHN_DESC," +
		    " D.DEPT_CHN_DESC," +
		    " B.REXP_CODE," +
		    " E.CHN_DESC" +
		    " ORDER BY TO_CHAR (A.BILL_DATE, 'YYYY-MM-DD')";
		TParm result = new TParm( TJDODBTool.getInstance().select(sql));
		if(result.getErrCode() < 0){
			return;
		}
		this.table.setParmValue(result);
	}
	/**
	 * ���Excel
	 */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ���
		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "����������ϸ��");
	}

	/**
	 * ���
	 */
	public void onClear() {
		initPage();
		table.removeRowAll();
	}
}
