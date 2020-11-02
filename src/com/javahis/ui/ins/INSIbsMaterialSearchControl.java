package com.javahis.ui.ins;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * 
 * <p>
 * Title:ҽ��סԺҽ�Ʒ�����֧����Ϣ����EXCLE
 * </p>
 * 
 * <p>
 * Description:ҽ��סԺҽ�Ʒ�����֧����Ϣ����EXCLE
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author pangb 2012-8-8
 * @version 4.0
 */
public class INSIbsMaterialSearchControl extends TControl{
	private TTable table; // ����������Ϣ�б�
	//======����
	private Compare compare = new Compare();
	private int sortColumn = -1;
	private boolean ascending = false;
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE"); // ����������Ϣ�б�
		this.setValue("OUT_DATE_START", SystemTool.getInstance().getDate());
		this.setValue("OUT_DATE_END", SystemTool.getInstance().getDate());
		this.setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		addListener(table);
	}
	private String sql ="SELECT A.MR_NO,C.IPD_NO, A.PAT_NAME, E.CHN_DESC,"+
	       "A.PAT_AGE, F.CTZ_DESC,"+
	       "TO_CHAR (A.IN_DATE, 'YYYYMMDD') AS IN_DATE,"+
	       "TO_CHAR (A.DS_DATE, 'YYYYMMDD') AS DS_DATE,"+
	      "CASE WHEN TRUNC (A.DS_DATE, 'DD') - TRUNC (A.IN_DATE, 'DD') = 0 THEN 1 "+
	      "ELSE TRUNC (A.DS_DATE, 'DD') - TRUNC (A.IN_DATE, 'DD') END AS IN_DAYS,"+
	      "A.DEPT_DESC, A.PHA_AMT, A.EXM_AMT,"+
	      "A.TREAT_AMT, A.OP_AMT, A.BED_AMT,"+
	      "A.MATERIAL_AMT, A.BLOODALL_AMT,"+
	      "A.BLOOD_AMT, A.OTHER_AMT,"+
	      "A.PHA_AMT+ A.EXM_AMT+ A.TREAT_AMT+ A.OP_AMT+ A.BED_AMT+ A.MATERIAL_AMT"+
	      "+ A.BLOODALL_AMT+ A.BLOOD_AMT+ A.OTHER_AMT AS SUM_AMT,"+
	      "A.OWN_AMT,"+
	      "A.ADD_AMT,"+
	      "A.RESTART_STANDARD_AMT+ A.STARTPAY_OWN_AMT+ A.OWN_AMT+ A.PERCOPAYMENT_RATE_AMT"+
	      "+ A.ADD_AMT+ A.INS_HIGHLIMIT_AMT AS OWN_SUM_AMT,"+
	      "A.RESTART_STANDARD_AMT+ A.STARTPAY_OWN_AMT+ A.OWN_AMT + A.PERCOPAYMENT_RATE_AMT "+
	      "+A.ADD_AMT + A.INS_HIGHLIMIT_AMT - A.ACCOUNT_PAY_AMT - A.ARMYAI_AMT  AS SJZFJE,"+
	      "A.NHI_PAY,A.NHI_PAY_REAL,A.NHI_COMMENT AS HOSP_APPLY_AMT,A.NHI_COMMENT,A.ACCOUNT_PAY_AMT ,"+
	      "A.ACCOUNT_PAY_AMT AS GRZHZF,"+
	      "A.NHI_PAY_REAL+ A.TOT_PUBMANADD_AMT+ A.ARMYAI_AMT+ A.ACCOUNT_PAY_AMT AS HJZFJE,"+
	      "K.OP_DESC, (SELECT CASE WHEN SUM(L.TOTAL_AMT) IS NULL THEN 0 ELSE SUM(L.TOTAL_AMT) END "+
	  	  "FROM INS_IBS_ORDER  L, SYS_FEE M "+
		  "WHERE  L.YEAR_MON=A.YEAR_MON AND  L.CASE_NO=A.CASE_NO "+
		  "AND L.ORDER_CODE=M.ORDER_CODE  AND  M.OWN_PRICE>=100  "+
		  "AND  M.ORDER_CAT1_CODE ='PHA_W' "+ 
		  "AND  M.ACTIVE_FLG ='Y' ) AS SPHA, (SELECT CASE WHEN SUM(N.TOTAL_AMT) IS NULL THEN 0 ELSE SUM(N.TOTAL_AMT) END "+
	  	  "FROM INS_IBS_ORDER  N, SYS_FEE O "+
		  "WHERE  N.YEAR_MON=A.YEAR_MON AND  N.CASE_NO=A.CASE_NO "+
		  "AND N.ORDER_CODE=O.ORDER_CODE  AND  O.OWN_PRICE>=500  "+
		  "AND  O.ORDER_CAT1_CODE ='MAT' "+ 
		  "AND  O.ACTIVE_FLG ='Y' ) AS SMATERIAL_AMT "+
	   "FROM INS_IBS A LEFT JOIN INS_ADM_CONFIRM B ON A.CONFIRM_NO = B.CONFIRM_NO "+
	       "LEFT JOIN ADM_INP C "+
	       "ON  A.CASE_NO = C.CASE_NO "+
	       "LEFT JOIN SYS_OPERATOR D "+
	       "ON C.VS_DR_CODE = D.USER_ID "+
	       "LEFT JOIN SYS_DICTIONARY E ON A.SEX_CODE = E.ID "+
	       "AND E.GROUP_ID = 'SYS_SEX' "+
	       "INNER JOIN SYS_CTZ F "+
	       "ON B.HIS_CTZ_CODE = F.CTZ_CODE " +   
	       "LEFT JOIN SYS_DICTIONARY G "+
	       "ON A.INSBRANCH_CODE = G.ID AND G.GROUP_ID = 'INS_FZX' "+
	       "LEFT JOIN SYS_DICTIONARY H "+
	       "ON A.SPEDRS_CODE = H.ID AND H.GROUP_ID = 'INS_MTLBA' "+
	       "LEFT JOIN SYS_DICTIONARY I "+
	       "ON I.GROUP_ID = 'INS_JYLB' AND I.ID = A.ADM_CATEGORY "+
	       "LEFT JOIN SYS_DICTIONARY J "+
	       "ON J.GROUP_ID = 'SP_PRESON_TYPE' AND J.ID = B.SPECIAL_PAT "+
	       "LEFT JOIN MRO_RECORD_OP K ON K.MAIN_FLG = 'Y' "+
	       "AND K.CASE_NO = A.CASE_NO ";
	
	//������,100;��������,100;�Ա�,100;����,100;���,100;��Ժ����,100;
	//��Ժ����,100;סԺ����,100;����,100;ҩƷ��,100;����,100;���Ʒ�,100;
	//������,100;��λ��,100;ҽ�ò���,100;��ȫѪ,100;�ɷ���Ѫ,100;����,100;�ϼ�,100;
	//�Էѽ��,100;�������,100;���˺ϼ�,100;ʵ���Ը����,100;ҽ������_ҽ�ƻ���������,100;
	//ҽ������_�籣֧�����,100;������_ҽ�ƻ���������,100;������_�籣����֧�����,100;
	//�����˻�֧��_ҽ�ƻ���������,100;�����˻�֧��_�籣֧�����,100;�ϼ�֧��_�籣֧���ϼƽ��,100;��ʽ,100
	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		if (null == this.getValue("OUT_DATE_START")
				|| this.getValue("OUT_DATE_START").toString().length() <= 0
				|| null == this.getValue("OUT_DATE_END")
				|| this.getValue("OUT_DATE_END").toString().length() <= 0) {

			if (null == this.getValue("OUT_DATE_START")
					|| this.getValue("OUT_DATE_START").toString().length() <= 0) {
				this.grabFocus("OUT_DATE_START");
			}
			if (null == this.getValue("OUT_DATE_END")
					|| this.getValue("OUT_DATE_END").toString().length() <= 0) {
				this.grabFocus("OUT_DATE_END");
			}
			this.messageBox("�������Ժ����");
			return;
		}
		StringBuffer bf = new StringBuffer();
		
		bf.append(" WHERE A.DS_DATE BETWEEN TO_DATE('").append(
				SystemTool.getInstance().getDateReplace(
						this.getValueString("OUT_DATE_START"), true)).append(
				"','YYYYMMDDHH24MISS') AND TO_DATE('").append(
				SystemTool.getInstance().getDateReplace(
						this.getValueString("OUT_DATE_END").substring(0,10), false)).append(
				"','YYYYMMDDHH24MISS')");
		if (this.getValue("REGION_CODE").toString().length() > 0) {
			bf.append(" AND C.REGION_CODE ='").append(
					this.getValue("REGION_CODE").toString()).append("'");
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql + bf));
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯʧ��");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("û����Ҫ��ѯ������");
			return;
		}
		table.setParmValue(result);
	}
	/**
	 * ���Excel
	 */
	public void onExport() {

		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		// TTable table = (TTable) callFunction("UI|Table|getThis");
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(
					table,
					StringTool.getString((Timestamp) this
							.getValue("OUT_DATE_START"), "yyyy.MM")
							+ "סԺҽ�Ʒ�����֧����˱�");
	}
	public void onClear(){
		this.setValue("OUT_DATE_START", SystemTool.getInstance().getDate());
		this.setValue("OUT_DATE_END", SystemTool.getInstance().getDate());
		this.setValue("REGION_CODE", Operator.getRegion());
		table.removeRowAll();
	}
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = table.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}
	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}
	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}
}
