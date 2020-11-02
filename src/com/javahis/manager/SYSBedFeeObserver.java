package com.javahis.manager;

import java.util.Vector;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.manager.TIOM_Database;

/**
 * <p>
 * Title: ��λ�����趨�۲���
 * </p>
 * 
 * <p>
 * Description: ��λ�����趨�۲���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangy 2009.06.04
 * @version 1.0
 */
public class SYSBedFeeObserver extends TObserverAdapter {
	public SYSBedFeeObserver() {

	}

	// ���prefech��������
	TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");

	/**
	 * 
	 * @param s
	 *            ���ݵ�ORDER_CODE
	 * @param colName
	 *            Ҫ�������
	 * @return String
	 */
	public String getTableShowValue(String s, String colName) {
		if (dataStore == null)
			return s;
		String bufferString = dataStore.isFilter() ? dataStore.FILTER
				: dataStore.PRIMARY;
		TParm parm = dataStore.getBuffer(bufferString);
		Vector vKey = (Vector) parm.getData("ORDER_CODE");
		Vector vDesc = (Vector) parm.getData(colName);
		int count = vKey.size();
		for (int i = 0; i < count; i++) {
			if (s.equals(vKey.get(i)))
				return "" + vDesc.get(i);
		}
		return s;
	}

	/**
	 * �õ�����������
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @param column
	 *            String
	 * @return Object
	 */
	public Object getOtherColumnValue(TDS ds, TParm parm, int row, String column) {
		if ("ORDER_DESC".equals(column)) {
			String orderCode = parm.getValue("ORDER_CODE", row);
			String icd_name = getTableShowValue(orderCode, "ORDER_DESC");
			if ("".equals(icd_name))
				return "";
			// �������һ��ֵ
			return icd_name;
		}
		return "";
	}

	/**
	 * ��������������
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int �����ö�������(table)
	 * @param column
	 *            String �����ö�������(table)
	 * @param value
	 *            Object
	 * @return boolean ����Ŀǰ��ֵ
	 */
	public boolean setOtherColumnValue(TDS ds, TParm parm, int row,
			String column, Object value) {
		return true;
	}
}
