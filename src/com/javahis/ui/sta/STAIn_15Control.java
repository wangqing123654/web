package com.javahis.ui.sta;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import jdo.sta.STAIn_15Tool;
import jdo.sys.SystemTool;
import jdo.bil.BILComparator;


/**
 * <p>Title: ���ʽ��ѯ���� </p>
 *
 * <p>Description: ���ʽ��ѯ���� </p>
 *
 * <p>Copyright: Copyright (c) 2013 </p>
 *
 * <p>Company:BlueCore </p>
 *
 * @author WangLong 20131008
 * @version 1.0
 */
public class STAIn_15Control extends TControl {
	
    private String admType;
    private TextFormatDept dept;
    private TTable table;
    // =================������==============
    private BILComparator compare = new BILComparator();
    private int sortColumn = -1;
    private boolean ascending = false;
	
    /**
     * ��ʼ������
     */
    public void onInit() {
        admType = "O";
        this.setValue("ADM_TYPE", admType);
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String dateStr = StringTool.getString(sysDate, "yyyy/MM/dd");
        this.setValue("END_DATE",
                      StringTool.getTimestamp(dateStr + " 23:59:59", "yyyy/MM/dd HH:mm:ss"));
        sysDate = StringTool.rollDate(sysDate, -7);
        dateStr = StringTool.getString(sysDate, "yyyy/MM/dd");
        this.setValue("START_DATE",
                      StringTool.getTimestamp(dateStr + " 00:00:00", "yyyy/MM/dd HH:mm:ss"));
        dept = (TextFormatDept) this.getComponent("DEPT_CODE");
        table = (TTable) this.getComponent("TABLE");
        addSortListener(table);// ������
    }
    
    /**
     * ��ѯ
     */
    public void onQuery() {
        String startDate = this.getText("START_DATE");// ��ʼʱ��
        String endDate = this.getText("END_DATE");// ��������
        String deptCode = this.getValueString("DEPT_CODE");// ����
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("START_DATE", startDate);
        parm.setData("END_DATE", endDate);
        if (!StringUtil.isNullString(deptCode)) {
            parm.setData("DEPT_CODE", deptCode);
        }
        TParm dataParm = new TParm();
        TParm deptParm = new TParm();
        TParm ctzParm = new TParm();
        String header = "";
        String parmMap = "";
        String alignment = "";
        TParm result = new TParm();
        if (admType.equals("O")) {
            dataParm = STAIn_15Tool.getInstance().selectOPD(parm);
            if (dataParm.getErrCode() < 0) {
                messageBox(dataParm.getErrText());
                return;
            }
            if (dataParm.getCount() > 0) {
                Set<String> deptSet = new HashSet<String>();
                Set<String> ctzSet = new HashSet<String>();
                for (int i = 0; i < dataParm.getCount(); i++) {
                    deptSet.add(dataParm.getValue("DEPT_CODE", i));
                    ctzSet.add(dataParm.getValue("CTZ_CODE", i) + "#" + dataParm.getValue("CTZ_DESC", i)+ "#" + dataParm.getValue("PY", i));
                }
                for (String dept : deptSet) {
                    deptParm.addData("DEPT_CODE", dept);
                }
                deptParm.setCount(deptSet.size());
                for (String ctz : ctzSet) {
                    ctzParm.addData("CTZ_CODE", ctz.split("#")[0]);
                    ctzParm.addData("CTZ_DESC", ctz.split("#")[1]);
                    ctzParm.addData("PY", ctz.split("#")[2]);
                }
                ctzParm.setCount(ctzSet.size());
                header = "����,100,DEPT_CODE_SQL;";
                parmMap = "DEPT_CODE;";
                alignment = "0,left;";
                for (int i = 0; i < ctzParm.getCount(); i++) {
                    header += ctzParm.getValue("CTZ_DESC",i) + countTitleItemLength(ctzParm.getValue("CTZ_DESC",i));
                    alignment += (i + 1) + ",right;";
                    parmMap += ctzParm.getValue("PY",i) + ";";
                }
                for (int i = 0; i < ctzParm.getCount(); i++) {
                    for (int j = 0; j < deptParm.getCount(); j++) {
                        result.addData(ctzParm.getValue("PY", i), 0);// ��ֵռλ
                    }
                }
                for (int i = 0; i < deptParm.getCount(); i++) {
                    for (int j = 0; j < ctzParm.getCount(); j++) {
                        for (int k = 0; k < dataParm.getCount(); k++) {
                            if (dataParm.getValue("CTZ_CODE", k).equals(ctzParm.getValue("CTZ_CODE", j))
                                    && dataParm.getValue("DEPT_CODE", k).equals(deptParm.getValue("DEPT_CODE", i))) {
                                result.setData(ctzParm.getValue("PY", j), i,
                                               dataParm.getInt("COUNT", k));
                            }
                        }
                    }
                }
                result.setCount(deptParm.getCount());
                result = addSumRowCol(result);// ���ܼ��к���
                for (int i = 0; i < deptParm.getCount(); i++) {
                    result.addData("DEPT_CODE", deptParm.getValue("DEPT_CODE", i));
                }
                result.addData("DEPT_CODE", "�ϼ�");
                result.setCount(result.getCount("DEPT_CODE"));
                header += "�ϼ�,60";
                parmMap += "SUM";
                alignment += ctzParm.getCount("CTZ_CODE") + 1 + ",right";
            }
            table.setHeader(header);
            table.setColumnHorizontalAlignmentData(alignment);
            table.setParmMap(parmMap);
        } else if (admType.equals("I")) {
            dataParm = STAIn_15Tool.getInstance().selectIPD(parm);
            if (dataParm.getErrCode() < 0) {
                messageBox(dataParm.getErrText());
                return;
            }
            if (dataParm.getCount() > 0) {
                Set<String> deptSet = new HashSet<String>();
                Set<String> ctzSet = new HashSet<String>();
                for (int i = 0; i < dataParm.getCount(); i++) {
                    deptSet.add(dataParm.getValue("DEPT_CODE", i));
                    ctzSet.add(dataParm.getValue("CTZ_CODE", i) + "#" + dataParm.getValue("CTZ_DESC", i)+ "#" + dataParm.getValue("PY", i));
                }
                for (String dept : deptSet) {
                    deptParm.addData("DEPT_CODE", dept);
                }
                deptParm.setCount(deptSet.size());
                for (String ctz : ctzSet) {
                    ctzParm.addData("CTZ_CODE", ctz.split("#")[0]);
                    ctzParm.addData("CTZ_DESC", ctz.split("#")[1]);
                    ctzParm.addData("PY", ctz.split("#")[2]);
                }
                ctzParm.setCount(ctzSet.size());
                header = "����,100,DEPT_CODE_SQL;";
                parmMap = "DEPT_CODE;";
                alignment = "0,left;";
                for (int i = 0; i < ctzParm.getCount(); i++) {
                    header += ctzParm.getValue("CTZ_DESC",i) + countTitleItemLength(ctzParm.getValue("CTZ_DESC",i));
                    alignment += (i + 1) + ",right;";
                    parmMap += ctzParm.getValue("PY",i) + ";";
                }
                for (int i = 0; i < ctzParm.getCount(); i++) {
                    for (int j = 0; j < deptParm.getCount(); j++) {
                        result.addData(ctzParm.getValue("PY", i), 0);// ��ֵռλ
                    }
                }
                for (int i = 0; i < deptParm.getCount(); i++) {
                    for (int j = 0; j < ctzParm.getCount(); j++) {
                        for (int k = 0; k < dataParm.getCount(); k++) {
                            if (dataParm.getValue("CTZ_CODE", k).equals(ctzParm.getValue("CTZ_CODE", j))
                                    && dataParm.getValue("DEPT_CODE", k).equals(deptParm.getValue("DEPT_CODE", i))) {
                                result.setData(ctzParm.getValue("PY", j), i,
                                               dataParm.getInt("COUNT", k));
                            }
                        }
                    }
                }
                result.setCount(deptParm.getCount());
                result = addSumRowCol(result);// ���ܼ��к���
                for (int i = 0; i < deptParm.getCount(); i++) {
                    result.addData("DEPT_CODE", deptParm.getValue("DEPT_CODE", i));
                }
                result.addData("DEPT_CODE", "�ϼ�");
                result.setCount(result.getCount("DEPT_CODE"));
                header += "�ϼ�,60";
                parmMap += "SUM";
                alignment += ctzParm.getCount("CTZ_CODE") + 1 + ",right";
            }
            table.setHeader(header);
            table.setColumnHorizontalAlignmentData(alignment);
            table.setParmMap(parmMap);
        }
		if (dataParm.getCount() <= 0) {
			messageBox("E0008");//��������
			this.callFunction("UI|TABLE|setParmValue", new TParm());
			this.clearValue("COUNT");
			return;
        }
        this.clearValue("TABLE");
        ((TTextField) getComponent("COUNT")).setValue(result.getValue("SUM", result.getCount()-1) + "");
        this.callFunction("UI|TABLE|setParmValue", result);
    }

    /**
     * ����ѡ���¼�
     */
    public void onChooseDept() {
        this.clearValue("DEPT_CODE");
        admType = this.getValueString("ADM_TYPE");// �ż�ס��
        if (admType.equals("O")) {
            dept.setOpdFitFlg("Y");
            dept.setEmgFitFlg("Y");
            dept.setIpdFitFlg("");
            dept.onQuery();
        } else if (admType.equals("I")) {
            dept.setOpdFitFlg("");
            dept.setEmgFitFlg("");
            dept.setIpdFitFlg("Y");
            dept.onQuery();
        }
    }
    
    /**
     * ����
     */
    public void onExport(){
        if (admType.equals("O")) {
            ExportExcelUtil.getInstance().exportExcel(table,"�ż��ﻼ�߰����ʽ��ѯ");
        } else if (admType.equals("I")) {
            ExportExcelUtil.getInstance().exportExcel(table,"��Ժ���߰����ʽ��ѯ");
        }
       
    }

    /**
     * �õ�TABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
    /**
     * ���
     */
    public void onClear(){
        admType="O";
        this.setValue("ADM_TYPE", admType);
        onChooseDept();
        ((TTextField) getComponent("COUNT")).setValue("");
        this.getTTable("TABLE").setDSValue();
    }
    
    /**
     * �����ܼ��к��ܼ���
     * @param parm
     * @return
     */
    public TParm addSumRowCol(TParm parm) {
        int count = parm.getCount();
        int sum = 0;
        for (int i = 0; i < parm.getCount(); i++) {
            for (int j = 0; j < parm.getNames().length; j++) {
                sum += parm.getInt(parm.getNames()[j], i);
            }
            parm.addData("SUM", sum);
            sum = 0;
        }
        for (int i = 0; i < parm.getNames().length; i++) {
            for (int j = 0; j < parm.getCount(); j++) {
                sum += parm.getInt(parm.getNames()[i], j);
            }
            parm.addData(parm.getNames()[i], sum);
            sum = 0;
        }
        parm.setCount(count + 1);
        return parm;
    }
    
    /**
     * ���ݱ�������,�����䳤�ȵ��ַ��� 
     * 
     * @param titleItem
     * @return
     */
    public String countTitleItemLength(String titleItem) {
        try {
            titleItem = new String(titleItem.getBytes("GBK"), "ISO8859_1");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int length = (titleItem.length() * 8) < 50 ? 50 : (titleItem.length() * 8);//��Ȳ�С��50
        return "," + length + ",int;";
    }
    
	// ====================������begin======================
	/**
	 * �����������������
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// �����ͬ�У���ת����
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// ȡ�ñ��е�����
				String columnName[] = tableData.getNames("Data");// �������
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // ������������;
				int col = tranParmColIndex(columnName, tblColumnName); // ����ת��parm�е�������
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * �����������ݣ���TParmתΪVector
	 * 
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
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
	 * ����ָ���������������е�index
	 * @param columnName
	 * @param tblColumnName
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * �����������ݣ���Vectorת��Parm
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */

	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
	// ====================������end======================
}
