package com.javahis.ui.opb;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 门诊收费数据
 * </p>
 * 
 * <p>
 * Description: 门诊收费数据
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author zhangp
 * @version 1.0
 */
public class OPBAccountMonthControl extends TControl {
	
	private static TTable table1;
	private static TTable table2;
	private static TTable table3;
	private static TTable table4;
	private static String seq1 = "";
	private static String seq2 = "";
	private static String seq3 = "";
	private static String seq4 = "";
	
	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		table1 = (TTable) getComponent("TABLE1");
		table2 = (TTable) getComponent("TABLE2");
		table3 = (TTable) getComponent("TABLE3");
		table4 = (TTable) getComponent("TABLE4");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -30).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		setValue("REGION_CODE", Operator.getRegion());
		setValue("DEPT", Operator.getDept());
	}

	/**
	 * 查询
	 */
	public void onQuery() {
//		boolean flag = false;
		TRadioButton tb0 = (TRadioButton)getComponent("YES");
		TRadioButton tb1 = (TRadioButton)getComponent("NO");
		TRadioButton tb2 = (TRadioButton)getComponent("ALL");
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("请输入需要查询的时间范围");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		String s = " ";
		if(tb0.isSelected()) {
			s = " AND ADM_TYPE='H' ";
//			flag = true;
		} 
		if(tb1.isSelected()) {
			s = " AND ADM_TYPE != 'H' ";
//			flag = false;
		}
		if(tb2.isSelected()) {
			s = " ";
//			flag = false;
		}
		String sql1 = 
			"SELECT 'N' FLG,ACCOUNT_SEQ FROM BIL_ACCOUNT WHERE ACCOUNT_TYPE = 'REG' AND ACCOUNT_DATE BETWEEN TO_DATE ('" + date_s + "','YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + date_e + "','YYYYMMDDHH24MISS')"+s+" ORDER BY ACCOUNT_SEQ";
		String sql2 = 
				"SELECT 'N' FLG,ACCOUNT_SEQ FROM BIL_ACCOUNT WHERE ACCOUNT_TYPE = 'REG' AND ACCOUNT_DATE BETWEEN TO_DATE ('" + date_s + "','YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('" + date_e + "','YYYYMMDDHH24MISS') "+s+" ORDER BY ACCOUNT_SEQ DESC";
		String sql3 = 
				"SELECT 'N' FLG,ACCOUNT_SEQ FROM BIL_ACCOUNT WHERE ACCOUNT_TYPE = 'OPB' AND ACCOUNT_DATE BETWEEN TO_DATE ('" + date_s + "','YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('" + date_e + "','YYYYMMDDHH24MISS')"+s+" ORDER BY ACCOUNT_SEQ";
		String sql4 = 
				"SELECT 'N' FLG,ACCOUNT_SEQ FROM BIL_ACCOUNT WHERE ACCOUNT_TYPE = 'OPB' AND ACCOUNT_DATE BETWEEN TO_DATE ('" + date_s + "','YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('" + date_e + "','YYYYMMDDHH24MISS') "+s+" ORDER BY ACCOUNT_SEQ DESC";
		TParm result = new TParm();
		table1.removeRowAll();
		table2.removeRowAll();
//		if(flag) {
//			result = new TParm(TJDODBTool.getInstance().select(sql3));
//			table3.setParmValue(result);
//			result = new TParm(TJDODBTool.getInstance().select(sql4));
//			table4.setParmValue(result);
//		}else {
			result = new TParm(TJDODBTool.getInstance().select(sql1));
			table1.setParmValue(result);
			result = new TParm(TJDODBTool.getInstance().select(sql2));
			table2.setParmValue(result);
			result = new TParm(TJDODBTool.getInstance().select(sql3));
			table3.setParmValue(result);
			result = new TParm(TJDODBTool.getInstance().select(sql4));
			table4.setParmValue(result);
//		}
	}
	
	/**
	 * 增加对Table的监听
	 */
	public void onTableClicked(int index) {
		TTable table = (TTable) getComponent("TABLE" + index);
		int row = table.getSelectedRow();
		// 得到当前选中行数据
		for (int i = 0; i < table.getRowCount(); i++) {
			table.setValueAt("N", i, 0);
		}
		table.setValueAt("Y", row, 0);
		table.acceptText();
		if (index == 1) {
			seq1 = table.getParmValue().getValue("ACCOUNT_SEQ", row);
		}
		if (index == 2) {
			seq2 = table.getParmValue().getValue("ACCOUNT_SEQ", row);
		}
		if (index == 3) {
			seq3 = table.getParmValue().getValue("ACCOUNT_SEQ", row);
		}
		if (index == 4) {
			seq4 = table.getParmValue().getValue("ACCOUNT_SEQ", row);
		}
	}
	
	/**
	 * 打印
	 */
	public void onPrint() {
		TParm printParm = getPrintParm();
		if (printParm == null) {
			return;
		}
		String date = SystemTool.getInstance().getDate().toString();
		TTextFormat tf = (TTextFormat) getComponent("DEPT");
		printParm.setData("DEPT", "TEXT", "科室: " + tf.getText());
		printParm.setData("PRINTDATE", "TEXT", "打印日期: " + date.substring(0, 4)
				+ "/" + date.substring(5, 7) + "/" + date.substring(8, 10));
		this.openPrintWindow("%ROOT%\\config\\prt\\OPB\\OPBAccountMonth.jhw",
				printParm);

	}
	
	
	public void onExport(){
		TTable table = (TTable) getComponent("TABLE");
		TParm parm = getPrintParm();
		TParm exportParm = new TParm();
		//行1
		exportParm.addData("C0", "挂号费");
		exportParm.addData("C1", parm.getValue("REG_FEE_REAL"));
		exportParm.addData("C2", parm.getValue("REG_FEE_REAL_INS"));
		exportParm.addData("C3", parm.getValue("REG_FEE_REAL_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		//行2
		exportParm.addData("C0", "诊察费");
		exportParm.addData("C1", parm.getValue("CLINIC_FEE_REAL"));
		exportParm.addData("C2", parm.getValue("CLINIC_FEE_REAL_INS"));
		exportParm.addData("C3", parm.getValue("CLINIC_FEE_REAL_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		//行3
		exportParm.addData("C0", "西药费");
		exportParm.addData("C1", parm.getValue("XY"));
		exportParm.addData("C2", parm.getValue("XY_INS"));
		exportParm.addData("C3", parm.getValue("XY_NOINS"));
		exportParm.addData("C4", "其中抗生素");
		exportParm.addData("C5", parm.getValue("KSS"));
		exportParm.addData("C6", parm.getValue("KSS_INS"));
		exportParm.addData("C7", parm.getValue("KSS_NOINS"));
		table.setParmValue(exportParm);
		//行4
		exportParm.addData("C0", "中成药费");
		exportParm.addData("C1", parm.getValue("ZCY"));
		exportParm.addData("C2", parm.getValue("ZCY_INS"));
		exportParm.addData("C3", parm.getValue("ZCY_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		//行5
		exportParm.addData("C0", "检查费");
		exportParm.addData("C1", parm.getValue("JCF"));
		exportParm.addData("C2", parm.getValue("JCF_INS"));
		exportParm.addData("C3", parm.getValue("JCF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		//行6
		exportParm.addData("C0", "治疗费");
		exportParm.addData("C1", parm.getValue("ZLF"));
		exportParm.addData("C2", parm.getValue("ZLF_INS"));
		exportParm.addData("C3", parm.getValue("ZLF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		//行7
		exportParm.addData("C0", "放射费");
		exportParm.addData("C1", parm.getValue("FSF"));
		exportParm.addData("C2", parm.getValue("FSF_INS"));
		exportParm.addData("C3", parm.getValue("FSF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		//行8
		exportParm.addData("C0", "手术费");
		exportParm.addData("C1", parm.getValue("SSF"));
		exportParm.addData("C2", parm.getValue("SSF_INS"));
		exportParm.addData("C3", parm.getValue("SSF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		//行9
		exportParm.addData("C0", "化验费");
		exportParm.addData("C1", parm.getValue("HYF"));
		exportParm.addData("C2", parm.getValue("HYF_INS"));
		exportParm.addData("C3", parm.getValue("HYF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		//行10
		exportParm.addData("C0", "输血费");
		exportParm.addData("C1", parm.getValue("SXF"));
		exportParm.addData("C2", parm.getValue("SXF_INS"));
		exportParm.addData("C3", parm.getValue("SXF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		//行11
		exportParm.addData("C0", "输氧费");
		exportParm.addData("C1", parm.getValue("SYF"));
		exportParm.addData("C2", parm.getValue("SYF_INS"));
		exportParm.addData("C3", parm.getValue("SYF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		//行12
		exportParm.addData("C0", "体检费");
		exportParm.addData("C1", parm.getValue("TJF"));
		exportParm.addData("C2", parm.getValue("TJF_INS"));
		exportParm.addData("C3", parm.getValue("TJF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		//行13
		exportParm.addData("C0", "观察床费");
		exportParm.addData("C1", parm.getValue("GCCF"));
		exportParm.addData("C2", parm.getValue("GCCF_INS"));
		exportParm.addData("C3", parm.getValue("GCCF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		//行14
		exportParm.addData("C0", "自费部分");
		exportParm.addData("C1", parm.getValue("ZFBF"));
		exportParm.addData("C2", parm.getValue("ZFBF_INS"));
		exportParm.addData("C3", parm.getValue("ZFBF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		//行15
		exportParm.addData("C0", "CT");
		exportParm.addData("C1", parm.getValue("CT"));
		exportParm.addData("C2", parm.getValue("CT_INS"));
		exportParm.addData("C3", parm.getValue("CT_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		//行16
		exportParm.addData("C0", "MR");
		exportParm.addData("C1", parm.getValue("MR"));
		exportParm.addData("C2", parm.getValue("MR_INS"));
		exportParm.addData("C3", parm.getValue("MR_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		//行17
		exportParm.addData("C0", "材料费");
		exportParm.addData("C1", parm.getValue("CLF"));
		exportParm.addData("C2", parm.getValue("CLF_INS"));
		exportParm.addData("C3", parm.getValue("CLF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		//行18
		exportParm.addData("C0", "合计");
		exportParm.addData("C1", parm.getValue("TOT_AMT"));
		exportParm.addData("C2", "");
		exportParm.addData("C3", "");
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		table.setParmValue(exportParm);
		TTextFormat tf = (TTextFormat) getComponent("DEPT");
		ExportExcelUtil.getInstance().exportExcel(table, tf.getText() + "特批款查询表");
	}
	
	private TParm getPrintParm() {
		String s = "";
		TRadioButton tb0 = (TRadioButton)getComponent("YES");
		TRadioButton tb1 = (TRadioButton)getComponent("NO");
		TRadioButton tb2 = (TRadioButton)getComponent("ALL");
		if(tb0.isSelected()) {
			s = " AND A.ADM_TYPE='H' ";
//			flag = true;
			seq1 = "1";
			seq2 = "1";
		} 
		if(tb1.isSelected()) {
			s = " AND A.ADM_TYPE != 'H' ";
//			flag = false;
		}
		if(tb2.isSelected()) {
			s = " ";
//			flag = false;
		}
		TParm printParm = new TParm();
		if (!seq1.equals("") && !seq2.equals("") && !seq3.equals("")
				&& !seq4.equals("")) {
			String deptTable = "";
			String deptWhere = "";
			if (!getValue("DEPT").equals("")) {
				deptTable = " , SYS_OPERATOR_DEPT B";
				deptWhere = " AND A.ACCOUNT_USER = B.USER_ID AND B.DEPT_CODE = '"
						+ getValue("DEPT") + "' AND B.MAIN_FLG = 'Y' ";
			}
			String regSeq = "SELECT DISTINCT A.ACCOUNT_SEQ FROM BIL_ACCOUNT A "
					+ deptTable
					+ " WHERE A.ACCOUNT_TYPE = 'REG' AND A.ACCOUNT_SEQ >= "
					+ seq1 + " AND A.ACCOUNT_SEQ <= " + seq2 + "" + deptWhere
					+ " ORDER BY A.ACCOUNT_SEQ";
			TParm regAccountParm;
			if(!tb0.isSelected()) {
				regAccountParm = new TParm(TJDODBTool.getInstance().select(
						regSeq));
			}else{
				regAccountParm = new TParm();
			}
			String opbSeq = "SELECT DISTINCT A.ACCOUNT_SEQ FROM BIL_ACCOUNT A "
					+ deptTable
					+ " WHERE A.ACCOUNT_TYPE = 'OPB' AND A.ACCOUNT_SEQ >= "
					+ seq3 + " AND A.ACCOUNT_SEQ <= " + seq4 + "" + deptWhere + s
					+ " ORDER BY A.ACCOUNT_SEQ";
			TParm opbAccountParm = new TParm(TJDODBTool.getInstance().select(
					opbSeq));
			regSeq = "";
			opbSeq = "";
			for (int i = 0; i < regAccountParm.getCount(); i++) {
				regSeq = regSeq + regAccountParm.getValue("ACCOUNT_SEQ", i) + ",";
			}
			
			for (int i = 0; i < opbAccountParm.getCount(); i++) {
				opbSeq += opbAccountParm.getValue("ACCOUNT_SEQ", i) + ",";
			}
			try {
				if(!tb0.isSelected()) {
					regSeq = regSeq.substring(0, regSeq.length()-1);
				}else{
					regSeq = "";
				}
				opbSeq = opbSeq.substring(0, opbSeq.length()-1); 
			} catch (StringIndexOutOfBoundsException e) {
				messageBox("无数据！");
				return null;
			}
		//	messageBox("");
			String sql = " SELECT SUM (REG_FEE_REAL) REG_FEE_REAL, SUM (CLINIC_FEE_REAL) CLINIC_FEE_REAL "
					+
					// " FROM BIL_REG_RECP A " + deptTable +
					" FROM BIL_REG_RECP A " +
					// " WHERE (A.ACCOUNT_SEQ >= " + seq1 +
					// " AND A.ACCOUNT_SEQ <= " + seq2 + ")" +
					" WHERE A.ACCOUNT_SEQ IN (" + regSeq + ")" +
					// deptWhere +
					" AND A.PAY_INS_CARD = 0";
			TParm regNoIns = new TParm(TJDODBTool.getInstance().select(sql));
			sql = " SELECT SUM (REG_FEE_REAL) REG_FEE_REAL, SUM (CLINIC_FEE_REAL) CLINIC_FEE_REAL "
					+
					// " FROM BIL_REG_RECP A " + deptTable +
					" FROM BIL_REG_RECP A " +
					// " WHERE (A.ACCOUNT_SEQ >= " + seq1 +
					// " AND A.ACCOUNT_SEQ <= " + seq2 + ")"
					" WHERE A.ACCOUNT_SEQ IN (" + regSeq + ")"
			// + deptWhere
			;
			TParm reg = new TParm(TJDODBTool.getInstance().select(sql));
			sql = " SELECT SUM(A.CHARGE01+A.CHARGE02) XY,SUM (A.CHARGE01) KSS, SUM (A.CHARGE02) FKSS, SUM (A.CHARGE03) ZCY,"
					+ " SUM (A.CHARGE04) ZCAY, SUM (A.CHARGE05) JCF, SUM (A.CHARGE06) ZLF,"
					+ " SUM (A.CHARGE07) FSF, SUM (A.CHARGE08) SSF, SUM (A.CHARGE09) SXF,"
					+ " SUM (A.CHARGE10) HYF, SUM (A.CHARGE11) TJF, SUM (A.CHARGE12) SQYL,"
					+ " SUM (A.CHARGE13) GCCF, SUM (A.CHARGE14) CT, SUM (A.CHARGE15) MR,"
					+ " SUM (A.CHARGE16) ZFBF, SUM (A.CHARGE17) CLF, SUM (A.CHARGE18) SYF,"
					+ " SUM (A.CHARGE19) ZCF, SUM (A.CHARGE20), SUM (A.CHARGE21), SUM (A.CHARGE22),"
					+ " SUM (A.CHARGE23), SUM (A.CHARGE24), SUM (A.CHARGE25), SUM (A.CHARGE26),"
					+ " SUM (A.CHARGE27), SUM (A.CHARGE28), SUM (A.CHARGE29), SUM (A.CHARGE30)"
					+
					// " FROM BIL_OPB_RECP A " + deptTable +
					" FROM BIL_OPB_RECP A " +
					// " WHERE (A.ACCOUNT_SEQ >= " + seq3 +
					// " AND A.ACCOUNT_SEQ <= " + seq4 + ")"
					" WHERE A.ACCOUNT_SEQ IN (" + opbSeq + ")"
			// + deptOPBWhere
			;
			TParm opb = new TParm(TJDODBTool.getInstance().select(sql));	
			sql = " SELECT SUM(A.CHARGE01+A.CHARGE02) XY,SUM (A.CHARGE01) KSS, SUM (A.CHARGE02) FKSS, SUM (A.CHARGE03) ZCY,"
					+ " SUM (A.CHARGE04) ZCAY, SUM (A.CHARGE05) JCF, SUM (A.CHARGE06) ZLF,"
					+ " SUM (A.CHARGE07) FSF, SUM (A.CHARGE08) SSF, SUM (A.CHARGE09) SXF,"
					+ " SUM (A.CHARGE10) HYF, SUM (A.CHARGE11) TJF, SUM (A.CHARGE12) SQYL,"
					+ " SUM (A.CHARGE13) GCCF, SUM (A.CHARGE14) CT, SUM (A.CHARGE15) MR,"
					+ " SUM (A.CHARGE16) ZFBF, SUM (A.CHARGE17) CLF, SUM (A.CHARGE18) SYF,"
					+ " SUM (A.CHARGE19) ZCF, SUM (A.CHARGE20), SUM (A.CHARGE21), SUM (A.CHARGE22),"
					+ " SUM (A.CHARGE23), SUM (A.CHARGE24), SUM (A.CHARGE25), SUM (A.CHARGE26),"
					+ " SUM (A.CHARGE27), SUM (A.CHARGE28), SUM (A.CHARGE29), SUM (A.CHARGE30)"
					+
					// " FROM BIL_OPB_RECP A " + deptTable +
					" FROM BIL_OPB_RECP A " +
					// " WHERE (A.ACCOUNT_SEQ >= " + seq3 +
					// " AND A.ACCOUNT_SEQ <= " + seq4 + ")" +
					" WHERE A.ACCOUNT_SEQ IN (" + opbSeq + ")" +
					// deptOPBWhere +
					" AND PAY_INS_CARD = 0";
			//System.out.println(sql+"dfdf");
			TParm opbNoIns = new TParm(TJDODBTool.getInstance().select(sql));
			double reg_fee_real = StringTool.round(reg.getDouble(
					"REG_FEE_REAL", 0), 2);
			double clinic_fee_real = StringTool.round(reg.getDouble(
					"CLINIC_FEE_REAL", 0), 2);
			double reg_fee_real_noins = StringTool.round(regNoIns.getDouble(
					"REG_FEE_REAL", 0), 2);
			double clinic_fee_real_noins = StringTool.round(regNoIns.getDouble(
					"CLINIC_FEE_REAL", 0), 2);
			double reg_fee_real_ins = reg_fee_real - reg_fee_real_noins;
			double clinic_fee_real_ins = clinic_fee_real
					- clinic_fee_real_noins;
			double xy = StringTool.round(opb.getDouble("XY", 0), 2);
			double kss = StringTool.round(opb.getDouble("KSS", 0), 2);
			double fkss = StringTool.round(opb.getDouble("FKSS", 0), 2);
			double zcy = StringTool.round(opb.getDouble("ZCY", 0), 2);
			double zcay = StringTool.round(opb.getDouble("ZCAY", 0), 2);
			double jcf = StringTool.round(opb.getDouble("JCF", 0), 2);
			double zlf = StringTool.round(opb.getDouble("ZLF", 0), 2);
			double fsf = StringTool.round(opb.getDouble("FSF", 0), 2);
			double ssf = StringTool.round(opb.getDouble("SSF", 0), 2);
			double sxf = StringTool.round(opb.getDouble("SXF", 0), 2);
			double hyf = StringTool.round(opb.getDouble("HYF", 0), 2);
			double tjf = StringTool.round(opb.getDouble("TJF", 0), 2);
			double sqyl = StringTool.round(opb.getDouble("SQYL", 0), 2);
			double gccf = StringTool.round(opb.getDouble("GCCF", 0), 2);
			double ct = StringTool.round(opb.getDouble("CT", 0), 2);
			double mr = StringTool.round(opb.getDouble("MR", 0), 2);
			double zfbf = StringTool.round(opb.getDouble("ZFBF", 0), 2);
			double clf = StringTool.round(opb.getDouble("CLF", 0), 2);
			double syf = StringTool.round(opb.getDouble("SYF", 0), 2);
			double zcf = StringTool.round(opb.getDouble("ZCF", 0), 2);
			double xy_noins = StringTool.round(opbNoIns.getDouble("XY", 0), 2);
			double kss_noins = StringTool
					.round(opbNoIns.getDouble("KSS", 0), 2);
			double fkss_noins = StringTool.round(opbNoIns.getDouble("FKSS", 0),
					2);
			double zcy_noins = StringTool
					.round(opbNoIns.getDouble("ZCY", 0), 2);
			double zcay_noins = StringTool.round(opbNoIns.getDouble("ZCAY", 0),
					2);
			double jcf_noins = StringTool
					.round(opbNoIns.getDouble("JCF", 0), 2);
			double zlf_noins = StringTool
					.round(opbNoIns.getDouble("ZLF", 0), 2);
			double fsf_noins = StringTool
					.round(opbNoIns.getDouble("FSF", 0), 2);
			double ssf_noins = StringTool
					.round(opbNoIns.getDouble("SSF", 0), 2);
			double sxf_noins = StringTool
					.round(opbNoIns.getDouble("SXF", 0), 2);
			double hyf_noins = StringTool
					.round(opbNoIns.getDouble("HYF", 0), 2);
			double tjf_noins = StringTool
					.round(opbNoIns.getDouble("TJF", 0), 2);
			double sqyl_noins = StringTool.round(opbNoIns.getDouble("SQYL", 0),
					2);
			double gccf_noins = StringTool.round(opbNoIns.getDouble("GCCF", 0),
					2);
			double ct_noins = StringTool.round(opbNoIns.getDouble("CT", 0), 2);
			double mr_noins = StringTool.round(opbNoIns.getDouble("MR", 0), 2);
			double zfbf_noins = StringTool.round(opbNoIns.getDouble("ZFBF", 0),
					2);
			double clf_noins = StringTool
					.round(opbNoIns.getDouble("CLF", 0), 2);
			double syf_noins = StringTool
					.round(opbNoIns.getDouble("SYF", 0), 2);
			double zcf_noins = StringTool
					.round(opbNoIns.getDouble("ZCF", 0), 2);
			double xy_ins = xy - xy_noins;
			double kss_ins = kss - kss_noins;
			double fkss_ins = fkss - fkss_noins;
			double zcy_ins = zcy - zcy_noins;
			double zcay_ins = zcay - zcay_noins;
			double jcf_ins = jcf - jcf_noins;
			double zlf_ins = zlf - zlf_noins;
			double fsf_ins = fsf - fsf_noins;
			double ssf_ins = ssf - ssf_noins;
			double sxf_ins = sxf - sxf_noins;
			double hyf_ins = hyf - hyf_noins;
			double tjf_ins = tjf - tjf_noins;
			double sqyl_ins = sqyl - sqyl_noins;
			double gccf_ins = gccf - gccf_noins;
			double ct_ins = ct - ct_noins;
			double mr_ins = mr - mr_noins;
			double zfbf_ins = zfbf - zfbf_noins;
			double clf_ins = clf - clf_noins;
			double syf_ins = syf - syf_noins;
			double zcf_ins = zcf - zcf_noins;
			double tot_amt = reg_fee_real + clinic_fee_real + xy + zcy + zcay
					+ jcf + zlf + fsf + ssf + sxf + hyf + tjf + sqyl + gccf
					+ ct + mr + zfbf + clf + syf + zcf;
			printParm
					.setData("REG_FEE_REAL", StringTool.round(reg_fee_real, 2));
			printParm.setData("CLINIC_FEE_REAL", StringTool.round(
					clinic_fee_real, 2));
			printParm.setData("REG_FEE_REAL_NOINS", StringTool.round(
					reg_fee_real_noins, 2));
			printParm.setData("CLINIC_FEE_REAL_NOINS", StringTool.round(
					clinic_fee_real_noins, 2));
			printParm.setData("REG_FEE_REAL_INS", StringTool.round(
					reg_fee_real_ins, 2));
			printParm.setData("CLINIC_FEE_REAL_INS", StringTool.round(
					clinic_fee_real_ins, 2));
			printParm.setData("XY", StringTool.round(xy, 2));
			printParm.setData("KSS", StringTool.round(kss, 2));
			printParm.setData("FKSS", StringTool.round(fkss, 2));
			printParm.setData("ZCY", StringTool.round(zcy, 2));
			printParm.setData("ZCAY", StringTool.round(zcay, 2));
			printParm.setData("JCF", StringTool.round(jcf, 2));
			printParm.setData("ZLF", StringTool.round(zlf, 2));
			printParm.setData("FSF", StringTool.round(fsf, 2));
			printParm.setData("SSF", StringTool.round(ssf, 2));
			printParm.setData("SXF", StringTool.round(sxf, 2));
			printParm.setData("HYF", StringTool.round(hyf, 2));
			printParm.setData("TJF", StringTool.round(tjf, 2));
			printParm.setData("SQYL", StringTool.round(sqyl, 2));
			printParm.setData("GCCF", StringTool.round(gccf, 2));
			printParm.setData("CT", StringTool.round(ct, 2));
			printParm.setData("MR", StringTool.round(mr, 2));
			printParm.setData("ZFBF", StringTool.round(zfbf, 2));
			printParm.setData("CLF", StringTool.round(clf, 2));
			printParm.setData("SYF", StringTool.round(syf, 2));
			printParm.setData("ZCF", StringTool.round(zcf, 2));
			printParm.setData("XY_NOINS", StringTool.round(xy_noins, 2));
			printParm.setData("KSS_NOINS", StringTool.round(kss_noins, 2));
			printParm.setData("FKSS_NOINS", StringTool.round(fkss_noins, 2));
			printParm.setData("ZCY_NOINS", StringTool.round(zcy_noins, 2));
			printParm.setData("ZCAY_NOINS", StringTool.round(zcay_noins, 2));
			printParm.setData("JCF_NOINS", StringTool.round(jcf_noins, 2));
			printParm.setData("ZLF_NOINS", StringTool.round(zlf_noins, 2));
			printParm.setData("FSF_NOINS", StringTool.round(fsf_noins, 2));
			printParm.setData("SSF_NOINS", StringTool.round(ssf_noins, 2));
			printParm.setData("SXF_NOINS", StringTool.round(sxf_noins, 2));
			printParm.setData("HYF_NOINS", StringTool.round(hyf_noins, 2));
			printParm.setData("TJF_NOINS", StringTool.round(tjf_noins, 2));
			printParm.setData("SQYL_NOINS", StringTool.round(sqyl_noins, 2));
			printParm.setData("GCCF_NOINS", StringTool.round(gccf_noins, 2));
			printParm.setData("CT_NOINS", StringTool.round(ct_noins, 2));
			printParm.setData("MR_NOINS", StringTool.round(mr_noins, 2));
			printParm.setData("ZFBF_NOINS", StringTool.round(zfbf_noins, 2));
			printParm.setData("CLF_NOINS", StringTool.round(clf_noins, 2));
			printParm.setData("SYF_NOINS", StringTool.round(syf_noins, 2));
			printParm.setData("ZCF_NOINS", StringTool.round(zcf_noins, 2));
			printParm.setData("XY_INS", StringTool.round(xy_ins, 2));
			printParm.setData("KSS_INS", StringTool.round(kss_ins, 2));
			printParm.setData("FKSS_INS", StringTool.round(fkss_ins, 2));
			printParm.setData("ZCY_INS", StringTool.round(zcy_ins, 2));
			printParm.setData("ZCAY_INS", StringTool.round(zcay_ins, 2));
			printParm.setData("JCF_INS", StringTool.round(jcf_ins, 2));
			printParm.setData("ZLF_INS", StringTool.round(zlf_ins, 2));
			printParm.setData("FSF_INS", StringTool.round(fsf_ins, 2));
			printParm.setData("SSF_INS", StringTool.round(ssf_ins, 2));
			printParm.setData("SXF_INS", StringTool.round(sxf_ins, 2));
			printParm.setData("HYF_INS", StringTool.round(hyf_ins, 2));
			printParm.setData("TJF_INS", StringTool.round(tjf_ins, 2));
			printParm.setData("SQYL_INS", StringTool.round(sqyl_ins, 2));
			printParm.setData("GCCF_INS", StringTool.round(gccf_ins, 2));
			printParm.setData("CT_INS", StringTool.round(ct_ins, 2));
			printParm.setData("MR_INS", StringTool.round(mr_ins, 2));
			printParm.setData("ZFBF_INS", StringTool.round(zfbf_ins, 2));
			printParm.setData("CLF_INS", StringTool.round(clf_ins, 2));
			printParm.setData("SYF_INS", StringTool.round(syf_ins, 2));
			printParm.setData("ZCF_INS", StringTool.round(zcf_ins, 2));
			printParm.setData("TOT_AMT", StringTool.round(tot_amt, 2));
			printParm.setData("REG_SEQ", regSeq);
			printParm.setData("OPB_SEQ", opbSeq);
		} else {
			messageBox("请选择开始和结束表号");
			return null;
		}
		return printParm;
	}
}
