package com.javahis.ui.adm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import jdo.adm.ADMResvTool;
import jdo.bil.BILComparator;
import jdo.sys.SystemTool;

/**
 * <p>Title: ԤԼ����ͳ�� </p>
 *
 * <p>Description: ԤԼ����ͳ�� </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company:BlueCore </p>
 *
 * @author WangLong 20120921
 * @version 1.0
 */
public class ADMResvStatisticsControl extends TControl {
	
	// =================������==============
	private BILComparator compare = new BILComparator();
	private int sortColumn = -1;
	private boolean ascending = false;
	
	   /**
     * ��ʼ������
     */
    public void onInit(){
    	super.onInit();
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String dateStr = StringTool.getString(sysDate,"yyyyMMdd");
        this.setValue("START_DATE",StringTool.getTimestamp(dateStr+" 00:00:00","yyyyMMdd hh:mm:ss"));
        this.setValue("END_DATE",sysDate);
        this.setValue("DEPT_CODE", "");
        this.setValue("STATION_CODE", "");
        this.setValue("DR_CODE", "");
        this.setValue("STATE_FLG", "");
        this.setValue("ADM_SOURCE", "");
		TTable table = (TTable) this.getComponent("TABLE");
		addSortListener(table);//������
    }
    /**
     * ��ѯ
     */
    public void onQuery(){
		String admSource = this.getValueString("ADM_SOURCE");//������Դ
		Timestamp startDate = (Timestamp)this.getValue("START_DATE");// ��ʼʱ��
		Timestamp endDate = (Timestamp)this.getValue("END_DATE");// ��������
//		String deptCode1 = this.getValueString("DEPT_CODE");// ����
//		System.out.println("��ֿ���deptCode1 deptCode1 is ::"+deptCode1);
		String deptCode = this.getValueString("DEPT_CODE");// ����
		String stationCode = this.getValueString("STATION_CODE");// ����
		String drCode = this.getValueString("DR_CODE");// ҽ��
		String stateFlg = this.getValueString("STATE_FLG");//״̬

		TParm parm = new TParm();
		TParm admResvStatistics = new TParm();
		if (!StringUtil.isNullString(admSource)) {
			parm.setData("ADM_SOURCE", admSource);
		}

		parm.setData("START_DATE", startDate);
		parm.setData("END_DATE", endDate);

		
		if (!StringUtil.isNullString(deptCode)) {
			parm.setData("DEPT_CODE", deptCode);
		}
		if (!StringUtil.isNullString(stationCode)) {
			parm.setData("STATION_CODE", stationCode);
		}
		if (!StringUtil.isNullString(drCode)) {
			parm.setData("DR_CODE", drCode);
		}
		if(!StringUtil.isNullString(stateFlg)){
			if(stateFlg.equals("0")){//ԤԼ
				 admResvStatistics = ADMResvTool.getInstance().queryADMResvStatistics0(parm);
			}else if(stateFlg.equals("1")){//ȡ��
				 admResvStatistics = ADMResvTool.getInstance().queryADMResvStatistics1(parm);
			}else if(stateFlg.equals("2")){//��Ժ
				 admResvStatistics = ADMResvTool.getInstance().queryADMResvStatistics2(parm);
			}
			
		}else{
			admResvStatistics = ADMResvTool.getInstance().queryADMResvStatistics(parm);
		}
		if (admResvStatistics.getErrCode() < 0) {
			messageBox(admResvStatistics.getErrText());
			return;
		}
		if (admResvStatistics.getCount() <= 0) {
			messageBox("E0008");
			this.callFunction("UI|TABLE|setParmValue", new TParm());
			this.clearValue("COUNT");
			return;
		}else{
				for(int i = 0;i<admResvStatistics.getCount();i++){
					String inFlg = admResvStatistics.getValue("IN_DATE", i);
					String cancelFlg = admResvStatistics.getValue("CAN_DATE",i);
					if((inFlg.equals(null)||inFlg.length()==0)&&
							(cancelFlg.equals(null)||cancelFlg.length()==0)){//ԤԼ״̬
						admResvStatistics.setData("STATE_FLG", i, "0");
					}else if((!inFlg.equals(null)&&inFlg.length()!=0)&&
							(cancelFlg.equals(null)||cancelFlg.length()==0)){//��Ժ״̬
						admResvStatistics.setData("STATE_FLG", i, "2");
					}else if((inFlg.equals(null)||inFlg.length()==0)&&
							(!cancelFlg.equals(null)&&cancelFlg.length()!=0)){
						admResvStatistics.setData("STATE_FLG", i, "1");//ȡ��״̬
					}
					
				}
			
		}
		
		this.clearValue("TABLE");
		((TTextField) getComponent("COUNT")).setValue(admResvStatistics.getCount()+"");
		this.callFunction("UI|TABLE|setParmValue", admResvStatistics);
    }

    /**
     * ����ѡ���¼�
     */
    public void onDEPT_CODE() {
        this.clearValue("STATION_CODE");
        this.callFunction("UI|STATION_CODE|onQuery");
    }
    
    /**
     * ����
     */
    public void onExecl(){
        ExportExcelUtil.getInstance().exportExcel(this.getTTable("TABLE"),"ͳ�Ʋ���ԤԼסԺ���");
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
        this.clearValue("ADM_SOURCE;DEPT_CODE;STATION_CODE;DR_CODE;STATE_FLG");
        ((TTextField) getComponent("COUNT")).setValue("");
        onInit();
        this.getTTable("TABLE").setDSValue();
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
