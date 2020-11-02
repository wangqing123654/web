package com.javahis.ui.sys;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.sys.SYSPatchUpTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * 
 * <p>
 * Title: ִ������ɾ����
 * </p>
 * 
 * <p>
 * Description: ִ������ɾ����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangzl
 * @version 1.0
 */
public class SYSPatchUpControl extends TControl {
	private TTable table;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		table = this.getTable("TABLE_INFO");
		initTime();
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");

	}

	/**
	 * ��ʼ��ʱ��
	 * */
	private void initTime() {
		Date startDate = SystemTool.getInstance().getDate();
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.MONTH, -1);
		Date date = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String resultDate = sdf.format(date);
		resultDate = (resultDate.substring(0, 10) + " 00:00:00");
		setValue("S_DATE", resultDate);
		Date e_Date = SystemTool.getInstance().getDate();
		SimpleDateFormat sdfe = new SimpleDateFormat("yyyy/MM/dd");
		String resulteDate = sdfe.format(e_Date);
		resulteDate = (resulteDate.substring(0, 10) + " 23:59:59");
		setValue("E_DATE", resulteDate);
	}

	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	private TRadioButton getTRadioButton(String tagName) {
		return (TRadioButton) this.getComponent(tagName);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery(int b) {
		TParm parm = new TParm();
		TRadioButton patch_a = this.getTRadioButton("PATCH_A");// ҵ������
		TRadioButton patch_b = this.getTRadioButton("PATCH_B");// ��������
		Date s = (Date) this.getValue("S_DATE");
		Date e = (Date) this.getValue("E_DATE");
		String dateStr = StringTool.getString(s, "yyyyMMddHHmmss");
		String dateStre = StringTool.getString(e, "yyyyMMddHHmmss");
		String PATCH_DESC = this.getValueString("PATCH_DESC");
		int patchType = 0;
		if (patch_a.isSelected()) {
			patchType = 2;
		}
		if (patch_b.isSelected()) {
			patchType = 1;
		}
		parm.setData("patchType", patchType);
		parm.setData("dateStr", dateStr);
		parm.setData("dateStre", dateStre);
		parm.setData("patch_desc", PATCH_DESC);
		TParm resultDate = SYSPatchUpTool.getInstance().onQuery(parm);
		if (b == 2) {
			table.setParmValue(resultDate);
			TCheckBox c = (TCheckBox) this.getComponent("SELECT_ALL");
			c.setSelected(true);
			return;
		}
		if (resultDate.getCount() <= 0) {
			messageBox("��������");
			onClear();
			return;
		}
		table.setParmValue(resultDate);

	}

	/**
	 * ɾ������
	 */
	public void onDelete() {
		int b = 2;
		TParm conditions = new TParm();
		TParm tableParm = table.getParmValue();
		int count = tableParm.getCount();
		for (int i = 0; i < count; i++) {
			boolean fig = StringTool.getBoolean(tableParm.getValue("FLG", i));
			if (!fig) {
				continue;
			}
			String PATCH_CODE = tableParm.getValue("PATCH_CODE", i);
			String PATCH_START_DATE = tableParm.getValue("START_DATE", i);
			conditions.addData("PATCH_CODE", PATCH_CODE);
			conditions.addData("START_DATE", PATCH_START_DATE);
		}
		// if(conditions.getCount()<0){
		// messageBox("��ѡ��Ҫɾ��������");
		// return;
		// }
		if (this.messageBox("��ʾ", "�Ƿ�ɾ������", 2) == 0) {
			TParm result = SYSPatchUpTool.getInstance().onDelete(conditions);
			if (result.getErrCode() < 0) {
				this.messageBox("ɾ��ʧ��");
				return;
			} else {
				this.messageBox("ɾ���ɹ�");
				this.onQuery(2);
				return;
			}
		}
	}

	/**
	 * ��ѡ��ѡ���¼�
	 * 
	 * @param obj
	 * @return
	 */
	public void onCheckBox(Object obj) {
		TTable table_info = (TTable) obj;
		table_info.acceptText();
	}

	/**
	 * SYS_FEEȫѡ����
	 */
	public void onSelectAll() {
		boolean flg = false;// �ж��Ƿ�ѡ��
		if (this.getValueBoolean("SELECT_ALL")) {
			flg = true;
		} else {
			flg = false;
		}
		TParm parm = table.getParmValue();
		for (int i = 0; i < parm.getCount(); i++) {
			parm.setData("FLG", i, flg);
		}
		table.setParmValue(parm);
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		this.table.removeRowAll();
		initTime();
		this.setValue("PATCH_DESC", "");
		TCheckBox c = (TCheckBox) this.getComponent("SELECT_ALL");
		c.setSelected(true);
	}
}
