package com.javahis.ui.bil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: סԺ���շ�Ա�����ձ���
 * </p>
 * 
 * <p>
 * Description:סԺ���շ�Ա�����ձ���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 2012-3-19
 * @version 4.0.1
 */
public class BILCashierDailyControl extends TControl {
	private TTable table;
	private String receiptNo="";//����Ԥ����Ʊ�ݺ���
	private double receiptAmt=0.00;//����Ԥ������
	DecimalFormat df1 = new DecimalFormat("########0.00");
	// ==========modify-begin (by wanglong 20120710)===============
	// ������Ϊ������ĸ���
	//private Compare compare = new Compare();
	private BILComparator comparator=new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;
	// ==========modify-end========================================
	
	public BILCashierDailyControl() {

	}
	/*
	 * ��ʼ��
	 */
	public void onInit() {
		initPage();
	}

	private void initPage() {
		table = (TTable) this.getComponent("TABLE");
		// ==========modify-begin (by wanglong 20120710)===============
		// Ϊ����Ӽ�������Ϊ������׼����
		addListener(table);
		// ==========modify-end========================================
		String now = StringTool.getString(SystemTool.getInstance().getDate(),
				"yyyyMMdd");
		this.setValue("START_DATE", StringTool.getTimestamp(now + "000000",
				"yyyyMMddHHmmss"));// ��ʼʱ��
		this.setValue("END_DATE", StringTool.getTimestamp(now + "235959",
				"yyyyMMddHHmmss"));// ����ʱ��
		this.setValue("USER_ID", Operator.getID());

	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		if (this.getValueString("START_DATE").length() == 0) {
			messageBox("��ʼʱ�䲻��ȷ!");
			return;
		}
		if (this.getValueString("END_DATE").length() == 0) {
			messageBox("����ʱ�䲻��ȷ!");
			return;
		}
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmmss");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss");
		TParm returnParm = new TParm();
		getQueryParm(returnParm, startTime, endTime);
		getIbsRecptParm(returnParm, startTime, endTime);
		if (returnParm.getCount() <= 0) {
			this.messageBox("û����Ҫ��ѯ������");
			table.removeRowAll();
			return;
		}
		// int cashCount=0;
		// double cashAmt=0.00;
		// int cardCount=0;
		// double cardAmt=0.00;
		// int remarkCount=0;
		// double remarkAmt=0.00;
		// for (int i = 0; i < returnParm.getCount(); i++) {
		// cashCount+=returnParm.getInt("CASH_COUNT",i);
		// cashAmt+=returnParm.getInt("CASH_AMT",i);
		// cashCount+=returnParm.getInt("CASH_COUNT",i);
		// cardCount+=returnParm.getInt("CARD_COUNT",i);
		// cardAmt+=returnParm.getInt("CARD_AMT",i);
		// remarkCount+=returnParm.getInt("REMARK_COUNT",i);
		// remarkAmt+=returnParm.getInt("REMARK_AMT",i);
		// }
		// returnParm.addData("TRANSACT_TYPE", "�ϼ�:");
		// returnParm.addData("CASH_COUNT", cashCount);
		// returnParm.addData("CASH_AMT", cashAmt);
		// returnParm.addData("CARD_COUNT", cardCount);
		// returnParm.addData("CARD_AMT", cardAmt);
		// returnParm.addData("REMARK_COUNT", remarkCount);
		// returnParm.addData("REMARK_AMT", remarkAmt);
		// returnParm.setCount(returnParm.getCount()+1);
		table.setParmValue(returnParm);
	}

	/**
	 * ���Ԥ��������
	 * 
	 * @param sql
	 * @return
	 */
	private void getQueryParm(TParm returnParm, String startTime, String endTime) {
		StringBuffer sql = new StringBuffer();
		// Ԥ�������ݲ�ѯ
		sql.append("SELECT  TRANSACT_TYPE,"
				+ "CASE WHEN TRANSACT_TYPE = '01' THEN SUM (PRE_AMT) "
				+ "WHEN TRANSACT_TYPE = '02' THEN SUM (PRE_AMT) "
				+ " END AS PRE_AMT,"
				+ "CASE WHEN TRANSACT_TYPE = '01' THEN COUNT (MR_NO)"
				+ " WHEN TRANSACT_TYPE = '02' THEN COUNT (MR_NO)"
				+ " END AS COUNT ,PAY_TYPE,RECEIPT_NO  " + "FROM BIL_PAY "
				+ "WHERE CHARGE_DATE BETWEEN TO_DATE('" + startTime
				+ "','YYYYMMDDHH24MISS') AND TO_DATE('" + endTime
				+ "','YYYYMMDDHH24MISS') ");
		// �շ���Ա
		if (this.getValue("USER_ID").toString().length() > 0) {
			sql.append(" AND CASHIER_CODE ='").append(this.getValue("USER_ID"))
					.append("'");
		}
		sql.append(" GROUP BY TRANSACT_TYPE,PAY_TYPE,RECEIPT_NO ORDER BY COUNT  ");
		TParm bilPayParm = new TParm(TJDODBTool.getInstance().select(
				sql.toString()));
		if (bilPayParm.getErrCode() < 0) {
			return;
		}
		// if (bilPayParm.getCount()<=0) {
		// this.messageBox("û����Ҫ��ѯ������");
		// table.removeRowAll();
		// return null;
		// }
		double payCash = 0.00;
		double payUCash = 0.00;
		double payCard = 0.00;
		double payUCard = 0.00;
		double payRemark = 0.00;
		double payURemark = 0.00;
		int payCashCount = 0;
		int payUCashCount = 0;
		int payCardCount = 0;
		int payUCardCount = 0;
		int payRemarkCount = 0;
		int payURemarkCount = 0;
		for (int i = 0; i < bilPayParm.getCount(); i++) {
			if (bilPayParm.getValue("TRANSACT_TYPE", i).equals("01")
					|| bilPayParm.getValue("TRANSACT_TYPE", i).equals("02")) {// Ԥ�����տ�
				if (bilPayParm.getValue("TRANSACT_TYPE", i).equals("01")) {//�տ�
//					if (bilPayParm.getValue("PAY_TYPE", i).equals("PAY_CASH")) {// �ֽ����
					if (bilPayParm.getValue("PAY_TYPE", i).equals("C0")||
							bilPayParm.getValue("PAY_TYPE", i).equals("PAY_CASH")) {// �ֽ����
						payCash += bilPayParm.getDouble("PRE_AMT", i);
						payCashCount += bilPayParm.getInt("COUNT", i);
					}
					if (bilPayParm.getValue("PAY_TYPE", i).equals("T0")||
							bilPayParm.getValue("PAY_TYPE", i).equals("PAY_CHECK")) {// ֧Ʊ
//						if (bilPayParm.getValue("PAY_TYPE", i).equals("PAY_CHECK")) {// ֧Ʊ
						payCard += bilPayParm.getDouble("PRE_AMT", i);
						payCardCount += bilPayParm.getInt("COUNT", i);
					}
					if (bilPayParm.getValue("PAY_TYPE", i).equals("C1")||
							bilPayParm.getValue("PAY_TYPE", i).equals("PAY_BANK_CARD")) {// ˢ��
						payRemark += bilPayParm.getDouble("PRE_AMT", i);
						payRemarkCount += bilPayParm.getInt("COUNT", i);
					}
				} else {//�˿�
					receiptNo+=bilPayParm.getValue("RECEIPT_NO", i)+",";
					receiptAmt+=bilPayParm.getDouble("PRE_AMT", i);
					if (bilPayParm.getValue("PAY_TYPE", i).equals("C0")||
							bilPayParm.getValue("PAY_TYPE", i).equals("PAY_CHECK")) {// �ֽ����
						payUCash += bilPayParm.getDouble("PRE_AMT", i);
						payUCashCount += bilPayParm.getInt("COUNT", i);
					}
					if (bilPayParm.getValue("PAY_TYPE", i).equals("T0")||
							bilPayParm.getValue("PAY_TYPE", i).equals("PAY_CHECK")) {// ֧Ʊ
						payUCard += bilPayParm.getDouble("PRE_AMT", i);
						payUCardCount += bilPayParm.getInt("COUNT", i);
					}
					if (bilPayParm.getValue("PAY_TYPE", i).equals("C1")||
							bilPayParm.getValue("PAY_TYPE", i).equals("PAY_BANK_CARD")) {// ˢ��
						payURemark += bilPayParm.getDouble("PRE_AMT", i);
						payURemarkCount += bilPayParm.getInt("COUNT", i);
					}

				}

			}
		}
		returnParm.addData("TRANSACT_TYPE", "1");// ��Ŀ���Ͳ���
		returnParm.addData("TRANSACT_TYPE", "2");//�����˷�
		returnParm.addData("CASH_COUNT", payCashCount);
		returnParm.addData("CASH_COUNT", payUCashCount);
		returnParm.addData("CASH_AMT", df1.format(payCash));
		returnParm.addData("CASH_AMT", df1.format(payUCash));
		returnParm.addData("CARD_COUNT", payCardCount);
		returnParm.addData("CARD_COUNT", payUCardCount);
		returnParm.addData("CARD_AMT", df1.format(payCard));
		returnParm.addData("CARD_AMT", df1.format(payUCard));
		returnParm.addData("REMARK_COUNT", payRemarkCount);
		returnParm.addData("REMARK_COUNT", payURemarkCount);
		returnParm.addData("REMARK_AMT", df1.format(payRemark));
		returnParm.addData("REMARK_AMT", df1.format(payURemark));
		returnParm.setCount(2);
	}

	/**
	 * סԺ�վ�����
	 * 
	 * @param sql
	 * @return
	 */
	private void getIbsRecptParm(TParm returnParm, String startTime,
			String endTime) {
		StringBuffer sql = new StringBuffer();
		sql
				.append("SELECT CASE WHEN AR_AMT-PAY_BILPAY>0 THEN '03' ELSE '04' END AS TRANSACT_TYPE,PAY_CASH," +
						"PAY_CHECK,PAY_BANK_CARD,AR_AMT-PAY_BILPAY AS PRE_AMT FROM BIL_IBS_RECPM "
						+ "WHERE CHARGE_DATE BETWEEN TO_DATE('"
						+ startTime
						+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
						+ endTime
						+ "','YYYYMMDDHH24MISS') ");
		// �շ���Ա
		if (this.getValue("USER_ID").toString().length() > 0) {
			sql.append(" AND CASHIER_CODE ='").append(this.getValue("USER_ID"))
					.append("'");
		}
		TParm bilPayParm = new TParm(TJDODBTool.getInstance().select(
				sql.toString()));
		if (bilPayParm.getErrCode() < 0) {
			return;
		}
		double payCard=0.00,payUCard=0.00,payCheck=0.00,payUCheck=0.00;
		int cardCount=0,cardUCount=0,checkCount=0,checkUCount=0;
		double payCash = 0.00;// ����
		double payUCash = 0.00;// �˿�
		int cashCount = 0;// �������
		int cashUCount = 0;// �˿����
		for (int i = 0; i < bilPayParm.getCount(); i++) {
			if (bilPayParm.getDouble("PRE_AMT", i) == 0) {// ���ݳ�����ȥ�ۼ�
				continue;
			}
			if (bilPayParm.getValue("TRANSACT_TYPE", i).equals("03")) {// ����
				if (bilPayParm.getDouble("PAY_CASH", i)!=0) {// �ֽ�
					payCash += bilPayParm.getDouble("PRE_AMT", i);
					cashCount++;
				}
				if (bilPayParm.getDouble("PAY_CHECK", i)!=0) {// ֧Ʊ
					payCheck += bilPayParm.getDouble("PRE_AMT", i);
					checkCount++;
				}
				if (bilPayParm.getDouble("PAY_BANK_CARD", i)!=0) {// ˢ��
					payCard += bilPayParm.getDouble("PRE_AMT", i);
					cardCount++;
				}
			} else {
				// �˿�
				if (bilPayParm.getDouble("PAY_CASH", i)!=0) {// �ֽ�
					payUCash += bilPayParm.getDouble("PRE_AMT", i);
					cashUCount++;
				}
				if (bilPayParm.getDouble("PAY_CHECK", i)!=0) {// ֧Ʊ
					payUCheck += bilPayParm.getDouble("PRE_AMT", i);
					checkUCount++;
				}
				if (bilPayParm.getDouble("PAY_BANK_CARD", i)!=0) {// ˢ��
					payUCard += bilPayParm.getDouble("PRE_AMT", i);
					cardUCount++;
				}
			}

		}
		returnParm.addData("TRANSACT_TYPE", "3");//����
		returnParm.addData("TRANSACT_TYPE", "4");//�˿�
		returnParm.addData("CASH_COUNT", cashCount);
		returnParm.addData("CASH_COUNT", cashUCount);
		returnParm.addData("CASH_AMT", df1.format(payCash));
		returnParm.addData("CASH_AMT", df1.format(payUCash));
		returnParm.addData("CARD_COUNT", cardCount);
		returnParm.addData("CARD_COUNT", cardUCount);
		returnParm.addData("CARD_AMT", df1.format(payCard));
		returnParm.addData("CARD_AMT", df1.format(payUCard));
		returnParm.addData("REMARK_COUNT", checkCount);
		returnParm.addData("REMARK_COUNT", checkUCount);
		returnParm.addData("REMARK_AMT", df1.format(payCheck));
		returnParm.addData("REMARK_AMT", df1.format(payUCheck));
		returnParm.setCount(returnParm.getCount() + 2);
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		TParm tableParm = table.getParmValue();
		if (tableParm.getCount() <= 0) {
			this.messageBox("û����Ҫ��ӡ������");
			return;
		}
		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		String sDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyy/MM/dd HH:mm:ss");
		String eDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyy/MM/dd HH:mm:ss");
		TParm parm = new TParm();
		tableParm.setData("TRANSACT_TYPE" ,0,"Ԥ�����տ�");
		tableParm.setData("TRANSACT_TYPE" ,1,"Ԥ���������˿�");
		tableParm.setData("TRANSACT_TYPE" ,2,"��Ժ���㲹��");
		tableParm.setData("TRANSACT_TYPE" ,3,"��Ժ�����˿�");
		tableParm.addData("SYSTEM", "COLUMNS", "TRANSACT_TYPE"); // 20120306
		tableParm.addData("SYSTEM", "COLUMNS", "CASH_COUNT");
		tableParm.addData("SYSTEM", "COLUMNS", "CASH_AMT");
		tableParm.addData("SYSTEM", "COLUMNS", "CARD_COUNT");
		tableParm.addData("SYSTEM", "COLUMNS", "CARD_AMT");
		tableParm.addData("SYSTEM", "COLUMNS", "REMARK_COUNT");
		tableParm.addData("SYSTEM", "COLUMNS", "REMARK_AMT");
		parm.setData("TITLE", "TEXT", "סԺ���շ�Ա�����ձ���");
		parm.setData("S_DATE", "TEXT", sDate);
		parm.setData("E_DATE", "TEXT", eDate);
		parm.setData("OPT_USER", "TEXT", Operator.getName());
		parm.setData("OPT_DATE", "TEXT", sysDate);
		parm.setData("TABLE", tableParm.getData());
		// TRANSACT_TYPE;CASH_COUNT;CASH_AMT;CARD_COUNT;CARD_AMT;REMARK_COUNT;REMARK_AMT
		// for (int i = 0; i < tableParm.getCount(); i++) {
		int sumCashCount = tableParm.getInt("CASH_COUNT", 0)
				- tableParm.getInt("CASH_COUNT", 1)
				+ tableParm.getInt("CASH_COUNT", 2)
				- tableParm.getInt("CASH_COUNT", 3);// �ֽ�������
		double sumCashAmt = tableParm.getDouble("CASH_AMT", 0)
				- tableParm.getDouble("CASH_AMT", 1)
				+ tableParm.getDouble("CASH_AMT", 2)
				- tableParm.getDouble("CASH_AMT", 3);// �ֽ��ܽ��
		int sumCardCount = tableParm.getInt("CARD_COUNT", 0)
				- tableParm.getInt("CARD_COUNT", 1)
				+ tableParm.getInt("CARD_COUNT", 2)
				- tableParm.getInt("CARD_COUNT", 3);// ˢ��������
		double sumCardAmt = tableParm.getDouble("CARD_AMT", 0)
				- tableParm.getDouble("CARD_AMT", 1)
				+ tableParm.getDouble("CARD_AMT", 2)
				- tableParm.getDouble("CARD_AMT", 3);// ˢ���ܽ��
		int sumRemarkCount = tableParm.getInt("REMARK_COUNT", 0)
				- tableParm.getInt("REMARK_COUNT", 1)
				+ tableParm.getInt("REMARK_COUNT", 2)
				- tableParm.getInt("REMARK_COUNT", 3);// ֧Ʊ������
		double sumRemarkAmt = tableParm.getDouble("REMARK_AMT", 0)
				- tableParm.getDouble("REMARK_AMT", 1)
				+ tableParm.getDouble("REMARK_AMT", 2)
				- tableParm.getDouble("REMARK_AMT", 3);// ֧Ʊ�ܽ��
		// ʵ�����=Ԥ�����տ�-Ԥ�����˿�+��Ժ���㲹��-��Ժ�����˿�
		parm.setData("SUM_CASH_COUNT", "TEXT", sumCashCount);
		parm.setData("SUM_CASH_AMT", "TEXT", sumCashAmt);
		parm.setData("SUM_CARD_COUNT", "TEXT", sumCardCount);
		parm.setData("SUM_CARD_AMT", "TEXT", sumCardAmt);
		parm.setData("SUM_REMARK_COUNT", "TEXT", sumRemarkCount);
		parm.setData("SUM_REMARK_AMT", "TEXT", sumRemarkAmt);
		String receiptNoName="";
		if (null!=this.getValue("USER_ID")&& this.getValue("USER_ID").toString().length()>0) {
			if (receiptNo.length()>0) {
				receiptNo=receiptNo.substring(0,receiptNo.lastIndexOf(","));
				String [] receiptSum=receiptNo.split(",");
				for (int i = 0; i < receiptSum.length; i++) {
					for (int j = 0; j < receiptSum.length-i-1; j++) {
						if (receiptSum[i].compareTo(receiptSum[j])<0) {
							receiptSum[i]=receiptSum[j];
						}
					}
				}
				receiptNoName=receiptSum[0]+"~"+receiptSum[receiptSum.length-1];
			}
			parm.setData("TXT_MESSAGE", "TEXT", "����Ʊ�ݺ�:");
			parm.setData("TXT_AMT", "TEXT", "���:");
			parm.setData("RECEIPT_NO", "TEXT", receiptNoName);
			parm.setData("RECEIPT_ATM", "TEXT", StringTool.round(receiptAmt, 2));
		}
		//parm.setData("SUM_REMARK_AMT", "TEXT", sumRemarkAmt);
		this.openPrintWindow("%ROOT%\\config\\prt\\bil\\BILCashierDaily.jhw",
				parm);
	}
	/**
     * ���Excel
     */
    public void onExport() {

        // �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
        //TTable table = (TTable) callFunction("UI|Table|getThis");
        if (table.getRowCount() > 0)
            ExportExcelUtil.getInstance().exportExcel(table, "סԺ���շ�Ա�����ձ���");
    }
    /**
     * ���
     */
    public void onClear(){
    	initPage();
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
				Vector vct = getVector(tableData, "Data", strNames, 0);
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
