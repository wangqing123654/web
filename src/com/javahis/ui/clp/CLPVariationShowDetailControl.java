package com.javahis.ui.clp;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import jdo.clp.CLPVariationShowTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComboNode;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.base.TComboBoxModel;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>
 * Title: �ٴ�·�����������ϸ
 * </p>
 * 
 * <p>
 * Description: �ٴ�·�����������ϸ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author pangben
 * @version 1.0
 */
public class CLPVariationShowDetailControl extends TControl {
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		initPage();
		// Ȩ�����
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
	}

	private TTable table;

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("��������Ҫ��ѯ��ʱ�䷶Χ");
			return;
		}
	
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		String where="";
		if (null!=Operator.getRegion()) {
			where = " AND A.REGION_CODE='" + Operator.getRegion()
			+ "' ";
		}
		String clncPath = this.getValueString("CLNCPATH_CODE");
		if (this.checkNullAndEmpty(clncPath)) {
			where+=" AND A.CLNCPATH_CODE='"+clncPath+"'";
		}
		String deptCode = this.getValueString("DEPT_CODE");
		if (this.checkNullAndEmpty(deptCode)) {
			where+=" AND A.DEPT_CODE='"+deptCode+"'";
		}
		if(((TRadioButton) this.getComponent("RDO_OWN")).isSelected()) {//pangben 2015-5-5����������
			where+=" AND F.MAIN_CTZ_FLG='Y' AND F.NHI_CTZ_FLG='N' ";
		}else if(((TRadioButton) this.getComponent("RDO_INS")).isSelected()) {//pangben 2015-5-5����������
			if (this.getValueString("SYS_CTZ").length()>0) {
				where+=" AND F.NHI_CTZ_FLG='Y' AND F.CTZ_CODE='"+this.getValueString("SYS_CTZ")+"' ";	
			}else{
				where+=" AND F.NHI_CTZ_FLG='Y' ";
			}
		}
		parm.setData("REGION_CODE", Operator.getRegion());
		String sql="SELECT A.MR_NO,B.PAT_NAME,CASE WHEN B.SEX_CODE='1' THEN '��' ELSE 'Ů' END SEX_DESC ," +
				"TO_CHAR(A.IN_DATE,'YYYY/MM/DD') IN_DATE,TO_CHAR(A.DS_DATE,'YYYY/MM/DD') DS_DATE,C.DEPT_CHN_DESC,F.CTZ_DESC,E.CLNCPATH_CHN_DESC " +
				" FROM ADM_INP A,SYS_PATINFO B,SYS_DEPT C,CLP_MANAGED D,CLP_BSCINFO E,SYS_CTZ F" +
				" WHERE A.MR_NO=B.MR_NO AND A.DEPT_CODE=C.DEPT_CODE AND A.CTZ1_CODE=F.CTZ_CODE AND A.CLNCPATH_CODE=E.CLNCPATH_CODE" +
				" AND A.CLNCPATH_CODE=D.CLNCPATH_CODE AND A.CASE_NO=D.CASE_NO" +
				" AND A.CLNCPATH_CODE IS NOT NULL AND D.MEDICAL_MONCAT IS NOT NULL AND A.DS_DATE IS NOT NULL" +
				" AND A.DS_DATE BETWEEN TO_DATE('"+date_s+"','YYYYMMDDHH24MISS') AND TO_DATE('"+date_e+"','YYYYMMDDHH24MISS') " +
				where;
		TParm searchParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (searchParm.getErrCode()<0) {
			this.messageBox("��ѯ���ִ���");
			return ;
		}
		if (searchParm.getCount()<=0) {
			this.messageBox("û�в�ѯ������");
			table.removeRowAll();
			return;
		}
		table.setParmValue(searchParm);
	}
	/**
	 * ����������
	 * 
	 * @param count
	 *            int
	 */
	private void updateTotalCount(int count) {
		this.setValue("COUNT", count);
	}

	/**
	 * ����������
	 */
	private void updataTotalCount() {
		int total = table.getRowCount() - 1;
		total = total < 0 ? 0 : total;
		updateTotalCount(total);
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		table = (TTable) getComponent("TABLE");
		this.setValue("REGION_CODE", Operator.getRegion());
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");

	}

	/**
	 * ��ӡ����
	 */
	public void onPrint() {

		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("û����Ҫ��ӡ������");
			return;
		}
		TParm result = new TParm();
		parm.addData("SYSTEM", "COLUMNS", "REGION_CODE");
		parm.addData("SYSTEM", "COLUMNS", "CLNCPATH_CHN_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SCHD_DESC");
		parm.addData("SYSTEM", "COLUMNS", "CHKTYPE_CHN_DESC");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		// parm.addData("SYSTEM", "COLUMNS", "CLNCPATH_CHN_DESC");
		parm.addData("SYSTEM", "COLUMNS", "STANDARD_COUNT");
		parm.addData("SYSTEM", "COLUMNS", "MAIN_COUNT");
		parm.addData("SYSTEM", "COLUMNS", "PERCENT");
		result.setData("S_DATE", "TEXT", getValueString("DATE_S").substring(0,
				getValueString("DATE_S").lastIndexOf(".")));
		result.setData("E_DATE", "TEXT", getValueString("DATE_E").substring(0,
				getValueString("DATE_S").lastIndexOf(".")));
		result.setData("OPT_USER", Operator.getName());
		result.setData("TABLE", parm.getData());
		result.setData("TITLE", "TEXT", (null != Operator
				.getHospitalCHNShortName() ? Operator.getHospitalCHNShortName()
				: "����Ժ��")
				+ "�ٴ�·���������");
		// ¬�������Ʊ���
		// ��β
		result.setData("CREATEUSER", "TEXT", Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\CLP\\CLPNewVariationShow.jhw",
				result);

	}

	/**
	 * ���
	 */
	public void onClear() {
		initPage();
		table.removeRowAll();
		((TRadioButton) this.getComponent("RDO_SEL")).setSelected(true);
		onRdoSel();
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
		ExportExcelUtil.getInstance().exportExcel(table, "�ٴ�·�����������ϸ��");
	}

	/**
	 * ����Ƿ�Ϊ�ջ�մ�
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}
	/**
	 * 
	* @Title: onRdoSel
	* @Description: TODO(������ݲ�ѯ����)
	* @author Dangzhang
	* @throws
	 */
	public void onRdoSel(){
		if(((TRadioButton) this.getComponent("RDO_SEL")).isSelected()) {
			callFunction("UI|SYS_CTZ|setEnabled", false);
		}else if(((TRadioButton) this.getComponent("RDO_OWN")).isSelected()) {//pangben 2015-5-5����������
			callFunction("UI|SYS_CTZ|setEnabled", false);
		}else if(((TRadioButton) this.getComponent("RDO_INS")).isSelected()) {//pangben 2015-5-5����������
			callFunction("UI|SYS_CTZ|setEnabled", true);
		}
		this.setValue("SYS_CTZ", "");
	}
}
