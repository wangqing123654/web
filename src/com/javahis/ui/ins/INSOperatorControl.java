package com.javahis.ui.ins;

import jdo.ope.OPEOpDetailTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * 
 * <p>
 * Title:�����ַ��÷ָ��в�����ҳ֮��������
 * </p>
 * 
 * <p>
 * Description:�����ַ��÷ָ��в�����ҳ֮��������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author pangb 2012-02-12
 * @version 2.0
 */
public class INSOperatorControl extends TControl {
	private TTable table;

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		initParm();
	}

	/**
	 * ��ʼ������
	 */
	private void initParm() {
		// �õ�ǰ̨���������ݲ���ʾ�ڽ�����
		TParm recptype = (TParm) getParameter();
		setValueForParm("MR_NO;CASE_NO;PAT_NAME", recptype, -1);
		table = (TTable) this.getComponent("TABLE");// ���
		TParm parm = new TParm();
		parm.setData("MR_NO", this.getValue("MR_NO"));
		parm.setData("CASE_NO", this.getValue("CASE_NO"));
		String sql = "SELECT OP_DATE,OP_RECORD_NO,"
				+ "OP_DEPT_CODE, OP_STATION_CODE, BED_NO,"
				+ "TYPE_CODE, RANK_CODE, WAY_CODE,"
				+ "ANA_CODE, DIAG_CODE1, "
				+ " OP_CODE1,"
				+ "MAIN_SURGEON, REAL_AST1, CIRCULE_USER1,"
				+ "SCRUB_USER1, ANA_USER1,EXTRA_USER1,DRG_CODE,B.ICD_CHN_DESC " 
				+ "FROM OPE_OPDETAIL A ,SYS_DIAGNOSIS B WHERE DIAG_CODE1=ICD_CODE(+) ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("û�в�ѯ������");
			return;
		}
		table.setParmValue(result);
	}
}
