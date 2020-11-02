package com.javahis.ui.bil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import com.dongyang.control.TControl;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;

import jdo.bil.BILComparator;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import jdo.util.Manager;
import com.dongyang.util.StringTool;

/**
 * <p>Title: Ԥ��������ձ�</p>
 *
 * <p>Description: Ԥ��������ձ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.5.5
 * @version 1.0
 */
public class BILPayPreResetQueryControl extends TControl {

    private TTable table;
	// ==========modify-begin (by wanglong 20120710)===============
	// ������Ϊ������ĸ���
	//private Compare compare = new Compare();
	private BILComparator comparator=new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;
	// ==========modify-end========================================
	
    public BILPayPreResetQueryControl() {
    }

    /*
     * ��ʼ��
     */
    public void onInit() {
        table = (TTable)this.getComponent("TABLE");
		// ==========modify-begin (by wanglong 20120710)===============
		// Ϊ����Ӽ�������Ϊ������׼����
		addListener(table);
		// ==========modify-end========================================
        String datetime = SystemTool.getInstance().getDate().toString();
        this.setValue("START_DATE", datetime.substring(0, 10).replace("-", "/"));
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        String start_date = this.getValueString("START_DATE").substring(0, 19);
        start_date = start_date.substring(0, 4) + start_date.substring(5, 7) +
                     start_date.substring(8, 10);
        String user_id = this.getValueString("USER");

        String sql = getSQL(start_date, user_id);
//        System.out.println("sql---" + sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        //System.out.println("parm---"+parm);
        if (parm == null || parm.getCount("MR_NO") <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        // �ֽ�
        int pay_cash = 0;
        double pay_cash_amt = 0;
        // ֧Ʊ
        int pay_check = 0;
        double pay_check_amt = 0;
        // ���п�
        int pay_bank_card = 0;
        double pay_bank_card_amt = 0;
        // ����
        int pay_debit = 0;
        double pay_debit_amt = 0;
        // ҽ�ƿ�
        int pay_medical_card = 0;
        double pay_medical_card_amt = 0;
        // ΢��
        int pay_type09 = 0;
        double pay_type09_amt = 0;
        // ֧����
        int pay_type10 = 0;
        double pay_type10_amt = 0;

        for (int i = 0; i < parm.getCount("MR_NO"); i++) {
            if ("C0".equals(parm.getValue("TYPE", i))) {
                pay_cash++;
                pay_cash_amt += parm.getDouble("AMT", i);
            } else if ("T0".equals(parm.getValue("TYPE", i))) {
                pay_check++;
                pay_check_amt += parm.getDouble("AMT", i);
            } else if ("C1".equals(parm.getValue("TYPE", i))) {
            	pay_bank_card++;
            	pay_bank_card_amt += parm.getDouble("AMT", i);
            } else if ("C4".equals(parm.getValue("TYPE", i))) {
                pay_debit++;
                pay_debit_amt += parm.getDouble("AMT", i);
            } else if ("EKT".equals(parm.getValue("TYPE", i))) {
                pay_medical_card++;
                pay_medical_card_amt += parm.getDouble("AMT", i);
            }else if ("WX".equals(parm.getValue("TYPE", i))) {
                pay_type09++;
                pay_type09_amt += parm.getDouble("AMT", i);
            }else if ("ZFB".equals(parm.getValue("TYPE", i))) {
                pay_type10++;
                pay_type10_amt += parm.getDouble("AMT", i);
            }
        }
        this.setValue("PAY_CASH", pay_cash);
        this.setValue("PAY_CASH_AMT", pay_cash_amt);
        this.setValue("PAY_CHECK", pay_check);
        this.setValue("PAY_CHECK_AMT", pay_check_amt);
        this.setValue("PAY_BANK_CARD", pay_bank_card);
        this.setValue("PAY_BANK_CARD_AMT", pay_bank_card_amt);
        this.setValue("PAY_DEBIT", pay_debit);
        this.setValue("PAY_DEBIT_AMT", pay_debit_amt);
        this.setValue("PAY_MEDICAL_CARD", pay_medical_card);
        this.setValue("PAY_MEDICAL_CARD_AMT", pay_medical_card_amt);
        this.setValue("PAY_TYPE09", pay_type09);
        this.setValue("PAY_TYPE09_AMT", pay_type09_amt);
        this.setValue("PAY_TYPE10", pay_type10);
        this.setValue("PAY_TYPE10_AMT", pay_type10_amt);
        this.setValue("SUM_COUNT",
                      pay_cash + pay_check + pay_bank_card + pay_debit +
                      pay_medical_card+pay_type09+pay_type10);
        this.setValue("SUM_AMT",
                      pay_cash_amt + pay_check_amt + pay_bank_card_amt +
                      pay_debit_amt + pay_medical_card_amt+pay_type09_amt+pay_type10_amt);
        table.setParmValue(parm);
    }

    /**
     * ȡ�ò�ѯSQL
     * @param start_date String
     * @param user_id String
     * @return String
     */
    private String getSQL(String start_date, String user_id) {
        String where = "";
        if (!"".equals(user_id)) {
            where = " AND B.CASHIER_CODE = '" + user_id + "' ";
        }
        String regionWhere = "";
        if (!"".equals(Operator.getRegion()))
            regionWhere = " AND I.REGION_CODE = '" + Operator.getRegion() +
                          "' ";
        return
                " SELECT B.RECEIPT_NO, B.MR_NO, S.PAT_NAME, P.DEPT_CHN_DESC, T.STATION_DESC, "
                + " B.PRE_AMT AS AMT, Y.CHN_DESC AS PAY_TYPE, B.CHARGE_DATE, O.USER_NAME, B.PAY_TYPE AS TYPE "
                + " FROM BIL_PAY B, SYS_PATINFO S, SYS_OPERATOR O, ADM_INP I, "
                + " SYS_BED D, SYS_STATION T, SYS_DICTIONARY Y, SYS_DEPT P "
                + " WHERE TO_CHAR (B.CHARGE_DATE, 'YYYYMMDD') = '"
                + start_date + "' "
                + " AND B.TRANSACT_TYPE = '03' "
                + where + " AND B.MR_NO = S.MR_NO "
                + " AND B.CASHIER_CODE = O.USER_ID "
                + " AND B.CASE_NO = I.CASE_NO "
                + " AND I.BED_NO = D.BED_NO "
                + regionWhere
                + " AND D.STATION_CODE = T.STATION_CODE "
                + " AND T.DEPT_CODE = P.DEPT_CODE "
                + " AND B.PAY_TYPE = Y.ID "
                + " AND Y.GROUP_ID = 'GATHER_TYPE' "
                + " ORDER BY B.RECEIPT_NO ";
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        if (table.getRowCount() <= 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) + "Ԥ��������ձ�");
        String start_date = getValueString("START_DATE");
        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
        date.setData("DATE_AREA", "TEXT", "�շ�ʱ��: " +
                     start_date.substring(0, 10).replace('-', '/'));
        date.setData("DATE", "TEXT",
                     "�Ʊ�ʱ��: " +
                     SystemTool.getInstance().getDate().toString().
                     substring(0, 19).
                     replace('-', '/'));
        // �������
        TParm parm = new TParm();
        TParm tableParm = table.getParmValue();
        for (int i = 0; i < table.getRowCount(); i++) {
            parm.addData("RECEIPT_NO", tableParm.getValue("RECEIPT_NO", i));
            parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
            parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
            parm.addData("DEPT_CHN_DESC", tableParm.getValue("DEPT_CHN_DESC", i));
            parm.addData("STATION_DESC", tableParm.getValue("STATION_DESC", i));
            parm.addData("AMT", Math.abs(StringTool.round(tableParm.getDouble("AMT", i), 2)));
            parm.addData("PAY_TYPE", tableParm.getValue("PAY_TYPE", i));
            parm.addData("CHARGE_DATE",
                         tableParm.getValue("CHARGE_DATE", i).substring(0, 19).
                         replace('-', '/'));
            parm.addData("USER_NAME", tableParm.getValue("USER_NAME", i));
        }
        parm.setCount(parm.getCount("MR_NO"));
        parm.addData("SYSTEM", "COLUMNS", "RECEIPT_NO");
        parm.addData("SYSTEM", "COLUMNS", "MR_NO");
        parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        parm.addData("SYSTEM", "COLUMNS", "AMT");
        parm.addData("SYSTEM", "COLUMNS", "PAY_TYPE");
        parm.addData("SYSTEM", "COLUMNS", "CHARGE_DATE");
        parm.addData("SYSTEM", "COLUMNS", "USER_NAME");
        date.setData("TABLE", parm.getData());
        // ��β����
        date.setData("PAY_CASH", "TEXT",
                     "�ֽ� " + this.getValueInt("PAY_CASH") + "��");
        date.setData("PAY_CASH_AMT", "TEXT",
                     "�ֽ�С�ƣ� " + Math.abs(StringTool.round(this.getValueDouble("PAY_CASH_AMT"), 2)));
        date.setData("PAY_CHECK", "TEXT",
                     "֧Ʊ�� " + this.getValueInt("PAY_CHECK") + "��");
        date.setData("PAY_CHECK_AMT", "TEXT",
                     "֧ƱС�ƣ� " + Math.abs(StringTool.round(this.getValueDouble("PAY_CHECK_AMT"), 2)));
        date.setData("PAY_BACK_CARD", "TEXT",
                     "���п��� " + this.getValueInt("PAY_BANK_CARD") + "��");
        date.setData("PAY_BACK_CARD_AMT", "TEXT",
                     "���п�С�ƣ� " + Math.abs(StringTool.round(this.getValueDouble("PAY_BANK_CARD_AMT"), 2)));
        date.setData("PAY_DEBIT", "TEXT",
                     "���ʣ� " + this.getValueInt("PAY_DEBIT") + "��");
        date.setData("PAY_DEBIT_AMT", "TEXT",
                     "����С�ƣ� " + Math.abs(StringTool.round(this.getValueDouble("PAY_DEBIT_AMT"), 2)));
        date.setData("PAY_MEDICAL_CARD", "TEXT",
                     "ҽ�ƿ��� " + this.getValueInt("PAY_MEDICAL_CARD") + "��");
        date.setData("PAY_MEDICAL_CARD_AMT", "TEXT",
                     "ҽ�ƿ�С�ƣ� " + Math.abs(StringTool.round(this.getValueDouble("PAY_MEDICAL_CARD_AMT"), 2)));
        date.setData("PAY_TYPE09", "TEXT",
        		"΢�ţ� " + this.getValueInt("PAY_TYPE09") + "��");
        date.setData("PAY_TYPE09_AMT", "TEXT",
        		"΢��С�ƣ� " + Math.abs(StringTool.round(this.getValueDouble("PAY_TYPE09_AMT"), 2)));
        date.setData("PAY_TYPE10", "TEXT",
        		"֧������ " + this.getValueInt("PAY_TYPE09") + "��");
        date.setData("PAY_TYPE10_AMT", "TEXT",
        		"֧����С�ƣ� " + Math.abs(StringTool.round(this.getValueDouble("PAY_TYPE10_AMT"), 2)));
        date.setData("SUM_COUNT", "TEXT",
                     "�ܼƣ� " + this.getValueInt("SUM_COUNT") + "��");
        date.setData("SUM_AMT", "TEXT",
                     "�ܼƽ� " +  Math.abs(StringTool.round(this.getValueDouble("SUM_AMT"), 2)));
        // ���ô�ӡ����
        this.openPrintWindow(
                "%ROOT%\\config\\prt\\BIL\\BILPayPreResetQuery.jhw",
                date);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String datetime = SystemTool.getInstance().getDate().toString();
        this.setValue("START_DATE", datetime.replace("-", "/"));
        this.clearValue("USER;PAY_CASH;PAY_CASH_AMT;PAY_CHECK;PAY_CHECK_AMT;"
                        + "PAY_BANK_CARD;PAY_BANK_CARD_AMT;PAY_DEBIT;"
                        + "PAY_DEBIT_AMT;PAY_MEDICAL_CARD;"
                        + "PAY_MEDICAL_CARD_AMT;SUM_COUNT;SUM_AMT;" 
                        +"PAY_TYPE09;PAT_TYPE09_AMT;"
                        +"PAT_TYPE10;PAY_TYPE10_AMT");
        table.removeRowAll();
    }

	// ==========modify-begin (by wanglong 20120710)===============
	// ����Ϊ��Ӧ��굥���¼��ķ��������ڻ�ȡȫ����Ԫ���ֵ������ĳ�������Լ���ظ���������
	/**
	 * �����������������
	 * @param table TTable
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				int i = table.getTable().columnAtPoint(me.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
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
				comparator.setDes(ascending);
				comparator.setCol(col);
				java.util.Collections.sort(vct, comparator);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);
				//getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * �õ� Vector ֵ
	 * @param parm TParm
	 * @param group String
	 * @param names String
	 * @param size int
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
	 * ת��parm�е���
	 * @param columnName String[]
	 * @param tblColumnName String
	 * @return int
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
	 * @param vectorTable Vector
	 * @param parmTable TParm
	 * @param columnNames String
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
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
	// ==========modify-end========================================
}
