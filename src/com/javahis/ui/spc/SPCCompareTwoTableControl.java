package com.javahis.ui.spc;

import jdo.spc.INDSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:�Ƚ�IND_VERIFYIND��IND_ACCOUNT
 * </p>
 * 
 * <p>
 * Description:�Ƚ�IND_VERIFYIND��IND_ACCOUNT
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author shendr 2013.06.27
 * @version 1.0
 */
public class SPCCompareTwoTableControl extends TControl {

	/* Table�ؼ� */
	private TTable table_count;
	private TTable table_price;

	/**
	 * ��ȡTTable�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	public TTable getTTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * ��ȡTRadioButton�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	public TRadioButton getTradioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		table_count = this.getTTable("TABLE_COUNT");
		table_price = this.getTTable("TABLE_PRICE");
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String START_DATE = getValueString("START_DATE");
		String END_DATE = getValueString("END_DATE");
		if (!StringUtil.isNullString(START_DATE)) {
			START_DATE = START_DATE.substring(0, 19);
		}
		if (!StringUtil.isNullString(END_DATE)) {
			END_DATE = END_DATE.substring(0, 19);
		}
		String sql = INDSQL.getTwoTable(START_DATE, END_DATE);
		if (getTradioButton("TYPEA").isSelected()) {
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			table_count.setParmValue(result);
			this.getTTable("TABLE_PRICE").setVisible(false);
			this.getTTable("TABLE_COUNT").setVisible(true);
		} else if (getTradioButton("TYPEB").isSelected()) {
			sql += " AND A.RETAIL_PRICE < A.VERIFYIN_PRICE";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			table_price.setParmValue(result);
			this.getTTable("TABLE_COUNT").setVisible(false);
			this.getTTable("TABLE_PRICE").setVisible(true);
		}
	}
}
