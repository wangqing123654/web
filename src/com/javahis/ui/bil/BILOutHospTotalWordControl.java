package com.javahis.ui.bil;

import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;
import jdo.sys.SYSSQL;

import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import java.text.DecimalFormat;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;

import jdo.bil.BILComparator;
import jdo.bil.BILSysParmTool;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Vector;

/**
 * <p>Title: ��Ժ����ҽ�Ʒ����ܱ�</p>
 *
 * <p>Description: ��Ժ����ҽ�Ʒ����ܱ�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.11.20
 * @version 1.0
 */
public class BILOutHospTotalWordControl extends TControl {
	// ==========modify-begin (by wanglong 20120710)===============
	private TTable table;//��ȡ������
	// ������Ϊ������ĸ���
	//private Compare compare = new Compare();
	private BILComparator comparator=new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;
	// ==========modify-end========================================
	
    public void onInit() {
        super.onInit();
        initPage();
    }

    String[] chargName = {"CHARGE01", "CHARGE02", "CHARGE03", "CHARGE04",
                         "CHARGE05", "CHARGE06", "CHARGE07", "CHARGE08",
                         "CHARGE09",
                         "CHARGE10", "CHARGE11", "CHARGE12", "CHARGE13",
                         "CHARGE14",
                         "CHARGE15", "CHARGE16", "CHARGE17", "CHARGE18",
                         //===zhangp 20120307 modify start
                         "CHARGE19", "CHARGE20", "CHARGE21"};
    //===ZHANGP 20120307 modify end
    private TParm parmName; //��������
    private TParm parmCode; //���ô���
    //��������
    Timestamp startDate ;
    /**
     * ��ʼ������
     */
    public void initPage() {
        startDate = getDateForInit(queryFirstDayOfLastMonth(StringTool.
                getString(SystemTool.getInstance().getDate(), "yyyyMMdd")));
        // System.out.println("startDate"+startDate);
        setValue("S_DATE", startDate);
        setValue("BILL_DATE",startDate);
        Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool.
                getInstance().getDate()), -1);
        setValue("E_DATE", rollDay);
//        this.callFunction("UI|S_DATE|setEnabled", false);
//        this.callFunction("UI|E_DATE|setEnabled", false);
        String sql = SYSSQL.getBillRecpparm(); //��÷��ô���
        sql += " WHERE ADM_TYPE='I'";
        parmCode = new TParm(TJDODBTool.getInstance().select(sql));
		// ==========modify-begin (by wanglong 20120710)===============
        //TTable table = (TTable)this.getComponent("Table");
		 table = (TTable) this.getComponent("Table");
		// Ϊ����Ӽ�������Ϊ������׼����
		addListener(table);
		// ==========modify-end========================================
        if (parmCode.getErrCode() < 0 || parmCode.getCount() <= 0) {
            this.messageBox("���÷����ֵ�������");
            return;
        }
        //��÷�������
        sql =
                "SELECT ID ,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE'";
        parmName = new TParm(TJDODBTool.getInstance().select(sql));
        table.setParmValue(getHeard());
    }

    /**
     * ��ӱ�ͷ
     * @return TParm
     */
    private TParm getHeard() {
        TParm heardParm = new TParm();
        heardParm.addData("DEPT_CHN_DESC", "��������");
        heardParm.addData("STATION_DESC", "����");
        heardParm.addData("TOT_AMT", "�ϼƽ��");
        for (int i = 0; i < chargName.length; i++) {
            heardParm.addData(chargName[i],
                              getChargeName(parmName,
                                            parmCode.getValue(chargName[i], 0)));
        }
        heardParm.setCount(1);
        return heardParm;
    }

    /**
     * ��÷�������
     * @param parmName TParm
     * @param chargeCode String
     * @return String
     */
    private String getChargeName(TParm parmName, String chargeCode) {
        for (int i = 0; i < parmName.getCount(); i++) {
            if (parmName.getValue("ID", i).equals(chargeCode)) {
                return parmName.getValue("CHN_DESC", i);
            }
        }
        return "";
    }
    /**
     * ��ӡ
     */
    public void onPrint() {
        print();
    }

    /**
     * ���ñ����ӡԤ������
     */
    private void print() {
        TTable table = (TTable)this.getComponent("Table");
        int row = table.getRowCount();
        if (row < 2) {
            this.messageBox_("�Ȳ�ѯ����!");
            return;
        }
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                "S_DATE")), "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                "E_DATE")), "yyyyMMdd");
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy/MM/dd HH:mm:ss");
        String billTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                "BILL_DATE")), "yyyyMMdd");
        TParm printData = this.getPrintDate(startTime, endTime, billTime);
        String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
                "S_DATE")), "yyyy/MM/dd");
        String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
                "E_DATE")), "yyyy/MM/dd");
        TParm parm = new TParm();
       //=====================modify by caowl 20120621 start=========================
        //����
        if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_4"))) {
        	parm.setData("TITLE", "TEXT","��Ժ����ҽ�Ʒ��ý�ת�ܱ�(�ѽ�ת)");
        }else if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_3"))) {
        	parm.setData("TITLE", "TEXT","��Ժ����ҽ�Ʒ��ý�ת�ܱ�(ȫ��)");
        }else{
        	parm.setData("TITLE", "TEXT","��Ժ����ҽ�Ʒ��ý�ת�ܱ�(δ��ת)");
        }
        //=======================modify by caowl 20120621 end=======================
        parm.setData("DATE", "TEXT", sDate + " �� " + eDate);
        parm.setData("E_DATE", eDate);
        parm.setData("OPT_USER", "TEXT", Operator.getName());
        parm.setData("printDate", "TEXT", sysDate);
        parm.setData("T1", printData.getData());
        parm.setData("TOT_AMT", "TEXT", "�ϼ�:  " + table.getShowParmValue().getValue("TOT_AMT", table.getRowCount()-1));//===zhangp 20120824
        this.openPrintWindow(
                "%ROOT%\\config\\prt\\IBS\\BILOutHospPatTotFee.jhw",
                parm);

    }

    /**
     * �����ӡ����
     * @param startTime String
     * @param endTime String
     * @param billTime String
     * @return TParm
     */
    private TParm getPrintDate(String startTime, String endTime,
                               String billTime) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm selParm = new TParm();
        String deptWhere = "";
        if (getValue("DEPT_CODE") != null) {
            if (getValue("DEPT_CODE").toString().length() != 0)
                deptWhere = " AND B.DEPT_CODE = '" + getValue("DEPT_CODE") +
                            "'  ";
        }
        if (this.getValue("STATION_CODE") != null &&
            this.getValueString("STATION_CODE").length() > 0) {
            deptWhere += " AND B.STATION_CODE = '" +
                    this.getValueString("STATION_CODE") + "'";
        }
        //�Է�
        if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_1"))) {
            deptWhere += " AND G.NHI_CTZ_FLG='N' ";
        }
        //ҽ��
        if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_2"))) {
            deptWhere += " AND G.NHI_CTZ_FLG='Y' ";
            if (this.getValueString("CTZ_CODE").length() > 0) {
                deptWhere += " AND G.CTZ_CODE='" +
                        this.getValueString("CTZ_CODE") + "' ";
            }
        }
        String billDay = billTime.substring(0, 8); //�½�ʱ���
        //�ѽ�ת
        if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_4"))) {
            deptWhere += " AND B.BILL_DATE < TO_DATE('" + billDay +
                    "000000','yyyyMMddhh24miss')";
        }
        //δ��ת
        if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_5"))) {
            deptWhere += " AND B.BILL_DATE > TO_DATE('" + billDay +
                    "000000','yyyyMMddhh24miss')";
        }
        
		if ("Y".equalsIgnoreCase(this.getValueString("tCheckBox_0"))) {
			// �ײ�
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_6"))) {
				if (this.getValue("LUMPWORK_CODE") != null
						&& this.getValueString("LUMPWORK_CODE").length() > 0) {
					deptWhere += " AND D.LUMPWORK_CODE ='"
							+ this.getValue("LUMPWORK_CODE") + "'";
				} else {
					deptWhere += " AND D.LUMPWORK_CODE IS NOT NULL ";
				}
			}
			// ���ײ�
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_7"))) {
				deptWhere += " AND D.LUMPWORK_CODE IS NULL  ";
			}
		}
        
        String regionWhere = "";
        if (!"".equals(Operator.getRegion()))
            regionWhere = " AND E.REGION_CODE = '" + Operator.getRegion() +
                          "' ";
        String sql =
                " SELECT B.DEPT_CODE AS EXE_DEPT_CODE, SUM (C.AR_AMT)  AS TOT_AMT, C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC " +
                " FROM BIL_IBS_RECPM A,IBS_BILLM B,IBS_BILLD C,ADM_INP D,SYS_DEPT E,SYS_STATION F,SYS_CTZ G " +
                " WHERE A.RECEIPT_NO=B.RECEIPT_NO " +
                //===zhangp 20120502 start
//                " AND D.DS_DATE BETWEEN TO_DATE ('" + startTime +
                " AND A.CHARGE_DATE BETWEEN TO_DATE ('" + startTime +
                //===zhangp 20120502 end
                "000000', 'yyyyMMddhh24miss') " +
                " AND TO_DATE ('" + endTime + "235959', 'yyyyMMddhh24miss') " +
                " AND B.BILL_NO=C.BILL_NO " +
                " AND B.BILL_SEQ=C.BILL_SEQ " +
                " AND A.CASE_NO=D.CASE_NO " +
                " AND B.DEPT_CODE=E.DEPT_CODE(+) " +
                " AND B.STATION_CODE=F.STATION_CODE(+) " +
                " AND B.CTZ1_CODE=G.CTZ_CODE " +
                deptWhere +
                regionWhere +
                " GROUP BY B.DEPT_CODE,B.STATION_CODE,C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC " +
                " ORDER BY B.DEPT_CODE";
        // System.out.println("sql:"+sql);
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        // System.out.println("selParm" + selParm);
        if (selParm.getCount("EXE_DEPT_CODE") < 1) {
            //��������
            this.messageBox("E0008");
            this.initPage();
            return selParm;
        }
        BILRecpChargeForPrint endData = new BILRecpChargeForPrint();
        //System.out.println("--selParm--"+selParm);
        TParm endParm = endData.getValue(selParm);        
        //System.out.println("--endData--"+endParm);
        TParm resultParm = getHeard(); //��ͷ
        int count = resultParm.getCount();
        for (int i = 0; i < endParm.getCount(); i++) {
            resultParm.setRowData(count, endParm, i);
            count++;
        }
        resultParm.setCount(count);
        resultParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        resultParm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        resultParm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
        for (int i = 0; i < chargName.length; i++) {
            resultParm.addData("SYSTEM", "COLUMNS", chargName[i]);
        }
        this.callFunction("UI|Table|setParmValue", resultParm);
        return resultParm;
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
    	// System.out.println("��ѯ����" + queryFirstDayOfLastMonth("20110914"));

        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                "S_DATE")), "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                "E_DATE")), "yyyyMMdd");
//        if (!startTime.substring(0, 6).equalsIgnoreCase(endTime.substring(0, 6))) {
//            this.messageBox_("��������Ҫ��ͬһ������");
//            return;
//        }
        String billTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                "BILL_DATE")), "yyyyMMdd");
        TParm printData = this.getPrintDate(startTime, endTime, billTime);
    }

    /**
     * ���Excel
     */
    public void onExport() {

        //�õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
        TTable table = (TTable) callFunction("UI|Table|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "��Ժ����ҽ�Ʒ����ܱ�");
    }

    /**
     * ���
     */
    public void onClear() {
        initPage();
        //TTable table = (TTable)this.getComponent("Table");
        //table.removeRowAll();
        this.clearValue("STATION_CODE;DEPT_CODE;LUMPWORK_CODE");
        this.callFunction("UI|tCheckBox_0|setSelected",false);
        this.callFunction("UI|tRadioButton_6|setEnabled", false);
        this.callFunction("UI|tRadioButton_7|setEnabled", false);
        this.callFunction("UI|tRadioButton_7|setSelected",true);
        this.callFunction("UI|LUMPWORK_CODE|setEnabled", false);
    }

    /**
     * ����combo�¼�
     */
    public void onDEPT() {
        this.clearValue("STATION_CODE");
        this.callFunction("UI|STATION_CODE|onQuery");
    }

    /**
     * ��ѡ��ť�¼�
     * @param type String
     */
    public void onRodio(String type) {
        if ("CTZ".equalsIgnoreCase(type)) {
            this.callFunction("UI|CTZ_CODE|setEnabled", true);
        } else {
            this.callFunction("UI|CTZ_CODE|setEnabled", false);
        }
    }
    
	/**
	 * �����ײ͸�ѡ��ť�¼�
	 * 
	 * @param type
	 *            String
	 */
	public void onCheckBox(String type) {
		if ("Y".equalsIgnoreCase(this.getValueString("tCheckBox_0"))) {
			this.callFunction("UI|tRadioButton_6|setEnabled", true);
			this.callFunction("UI|tRadioButton_7|setEnabled", true);
			if ("LUMPWORK".equalsIgnoreCase(type)) {
				this.callFunction("UI|LUMPWORK_CODE|setEnabled", true);
			} else {
				this.callFunction("UI|LUMPWORK_CODE|setEnabled", false);
				this.clearValue("LUMPWORK_CODE");
			}
		} else {
			this.callFunction("UI|tRadioButton_6|setEnabled", false);
			this.callFunction("UI|tRadioButton_7|setEnabled", false);
			//this.callFunction("UI|tRadioButton_8|setSelected", true);
			this.callFunction("UI|LUMPWORK_CODE|setEnabled", false);
			this.clearValue("LUMPWORK_CODE");
		}
	}

    /**
     * �õ��ϸ���
     * @param dateStr String
     * @return Timestamp
     */
    public Timestamp queryFirstDayOfLastMonth(String dateStr) {
        DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
        Date d = null;
        try {
            d = defaultFormatter.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return StringTool.getTimestamp(cal.getTime());
    }

    /**
     * ��ʼ��ʱ������
     * @param date Timestamp
     * @return Timestamp
     */
    public Timestamp getDateForInit(Timestamp date) {
        String dateStr = StringTool.getString(date, "yyyyMMdd");
        TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
        int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
        String monThCycle = "" + monthM;
        dateStr = dateStr.substring(0, 6) + monThCycle;
        Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
        return result;
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
				//=====������ �� "�ܼ�"��  �����봦����======
				TParm titRowParm=new TParm();//��¼������
				titRowParm.addRowData(table.getParmValue(), 0);
				TParm totRowParm=new TParm();//��¼���ܼơ���
				totRowParm.addRowData(table.getParmValue(), tableData.getCount()-1);
				int rowCount=tableData.getCount();//���ݵ�������������С���к��ܼ��У�
				tableData.removeRow(0);//ȥ����һ�У������У�
				tableData.removeRow(tableData.getCount()-1);//ȥ�����һ��(�ܼ���)
				//=========================================
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
				TParm lastResultParm=new TParm();//��¼���ս��
				TParm tmpParm=cloneVectoryParam(vct, new TParm(), strNames);
				lastResultParm.addRowData(titRowParm, 0);//���������
				for (int k = 0; k < tmpParm.getCount(); k++) {
					lastResultParm.addRowData(tmpParm, k);//�����м�����
				}
				lastResultParm.addRowData(totRowParm, 0);//�����ܼ���
				lastResultParm.setCount(rowCount);
				table.setParmValue(lastResultParm);
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
	    //================start===============
//	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
//			String columnNames) {
	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
    	//================end=================
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
		//================start===============
		//table.setParmValue(parmTable);
		return parmTable;
		//================end=================
		// System.out.println("�����===="+parmTable);
	}
	// ==========modify-end========================================
}
