package com.javahis.ui.mro;

import org.apache.commons.lang.StringUtils;

import jdo.mro.MROLendTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;

/**
 * <p>
 * Title: ���������ֵ�
 * </p>
 * 
 * <p>
 * Description: ���������ֵ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2009-5-6
 * @version 1.0
 */
public class MROLendControl extends TControl {
	private TParm data;
	private int selectRow = -1;

	public void onInit() {
		super.onInit();
		((TTable) getComponent("Table")).addEventListener("Table->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		onClear();
	}

	/**
	 * ���Ӷ�Table�ļ���
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		// ѡ����
		if (row < 0) {
			return;
		}

		setValueForParm(
				"LEND_CODE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION;LEND_TYPE;PRIORITY",
				data, row);
		selectRow = row;
		// ���ɱ༭
		((TTextField) getComponent("LEND_CODE")).setEnabled(false);
		// ����ɾ����ť״̬
		((TMenuItem) getComponent("delete")).setEnabled(true);
	}

	/**
	 * ����
	 */
	public void onInsert() {

		// �ؼ���֤
		if (!this.validate()) {
			return;
		}
		
		// ������֤
		if (!this.checkData()) {
			return;
		}

		TParm parm = this
				.getParmForTag("LEND_CODE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION;LEND_TYPE;PRIORITY");
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM", Operator.getIP());
		TParm result = MROLendTool.getInstance().insertdata(parm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		
		if (StringUtils.equals("O", parm.getValue("LEND_TYPE"))) {
			parm.setData("LEND_TYPE_DESC", "����Һ�");
		} else if (StringUtils.equals("I", parm.getValue("LEND_TYPE"))) {
			parm.setData("LEND_TYPE_DESC", "סԺ�Ǽ�");
		} else {
			parm.setData("LEND_TYPE_DESC", "��ͨ����");
		}
		
		// ��ʾ��������
		int row = ((TTable) getComponent("TABLE"))
				.addRow(
						parm,
						"LEND_CODE;LEND_TYPE_DESC;LEND_DESC;PY1;PY2;LEND_DAY;PRIORITY;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERM;LEND_TYPE");

		data.setRowData(row, parm);
		this.messageBox("��ӳɹ���");
	}

	/**
	 * ����
	 */
	public void onUpdate() {
		if (!this.validate()) {
			return;
		}

		TParm parm = this
				.getParmForTag("LEND_CODE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION;LEND_TYPE;PRIORITY");
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM", Operator.getIP());
		
		if (StringUtils.equals("O", parm.getValue("LEND_TYPE"))) {
			parm.setData("LEND_TYPE_DESC", "����Һ�");
		} else if (StringUtils.equals("I", parm.getValue("LEND_TYPE"))) {
			parm.setData("LEND_TYPE_DESC", "סԺ�Ǽ�");
		} else {
			parm.setData("LEND_TYPE_DESC", "��ͨ����");
		}

		TParm result = MROLendTool.getInstance().updatedata(parm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		// ѡ����
		int row = ((TTable) getComponent("Table")).getSelectedRow();
		if (row < 0)
			return;
		// ˢ�£�����ĩ��ĳ�е�ֵ
		data.setRowData(row, parm);
		((TTable) getComponent("Table")).setRowParmValue(row, data);
		this.messageBox("�޸ĳɹ���");
	}

	/**
	 * ����
	 */
	public void onSave() {
		if (selectRow == -1) {
			onInsert();
			return;
		}
		onUpdate();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm queryParm = new TParm();
		if (!StringUtils.isEmpty(getText("LEND_CODE").trim())) {
			queryParm.setData("LEND_CODE", getText("LEND_CODE").trim());
		}
		
		if (!StringUtils.isEmpty(getText("LEND_DESC").trim())) {
			queryParm.setData("LEND_DESC", getText("LEND_DESC").trim()+"%");
		}
		
		data = MROLendTool.getInstance().selectdata(queryParm);
		// �жϴ���ֵ
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		
		for (int i = 0; i < data.getCount(); i++) {
			if (StringUtils.equals("O", data.getValue("LEND_TYPE", i))) {
				data.addData("LEND_TYPE_DESC", "����Һ�");
			} else if (StringUtils.equals("I", data.getValue("LEND_TYPE", i))) {
				data.addData("LEND_TYPE_DESC", "סԺ�Ǽ�");
			} else {
				data.addData("LEND_TYPE_DESC", "��ͨ����");
			}
		}
		
		((TTable) getComponent("Table")).setParmValue(data);
		((TTextField) this.getComponent("LEND_CODE")).setEnabled(true);
		this
				.clearValue("LEND_CODE;LEND_TYPE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION;PRIORITY");
	}

	/**
	 * ���
	 */
	public void onClear() {
		this
				.clearValue("LEND_CODE;LEND_TYPE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION;PRIORITY");
		((TTable) getComponent("Table")).clearSelection();
		selectRow = -1;
		// ����ɾ����ť״̬
		((TMenuItem) getComponent("delete")).setEnabled(false);
		((TTextField) getComponent("LEND_CODE")).setEnabled(true);
		onQuery();
	}

	/**
	 * ɾ��
	 */
	public void onDelete() {
		if (this.messageBox("��ʾ", "�Ƿ�ɾ��", 2) == 0) {
			if (selectRow == -1)
				return;
			String code = getValue("LEND_CODE").toString();
			TParm result = MROLendTool.getInstance().deletedata(code);
			if (result.getErrCode() < 0) {
				messageBox(result.getErrText());
				return;
			}
			// TTable table = ( (TTable) getComponent("Table"));
			// int row = table.getSelectedRow();
			// if (row < 0)
			// return;
			this.messageBox("ɾ���ɹ���");
			onClear();
		} else {
			return;
		}
	}

	/**
	 * ���ݺ������ƴ������ĸ
	 * 
	 * @return Object
	 */
	public Object onCode() {
		if (TCM_Transform.getString(this.getValue("LEND_DESC")).length() < 1) {
			return null;
		}
		String value = TMessage.getPy(this.getValueString("LEND_DESC"));
		if (null == value || value.length() < 1) {
			return null;
		}
		this.setValue("PY1", value);
		// �������
		((TTextField) getComponent("PY1")).grabFocus();
		return null;
	}

	/**
	 * �õ�ComboBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * ҳ��ؼ�������֤
	 * 
	 * @return boolean
	 * @author wangbin 20141010
	 */
	private boolean validate() {
		if (this.getText("LEND_CODE").trim().length() <= 0
				|| this.getText("LEND_DESC").trim().length() <= 0) {
			this.messageBox("��������ԭ������˵������Ϊ�գ�");
			return false;
		}
        
        if (StringUtils.isEmpty(getComboBox("LEND_TYPE").getSelectedID())) {
        	this.messageBox("�������Ͳ���Ϊ�գ�");
        	return false;
        }
        
        if (StringUtils.isEmpty(getComboBox("PRIORITY").getSelectedID())) {
        	this.messageBox("�������ȼ�����Ϊ�գ�");
        	return false;
        }
        
        return true;
	}
	
	/**
	 * ������֤
	 * 
	 * @return boolean
	 * @author wangbin 20141010
	 */
	private boolean checkData() {
        // ��Խ������Ϊ����Һź�סԺ�Ǽǣ�ֻ������һ������
        if ("O,I".contains(getComboBox("LEND_TYPE").getSelectedID())) {
        	TParm parm = new TParm();
            parm.setData("LEND_TYPE", getComboBox("LEND_TYPE").getSelectedID());
            parm = MROLendTool.getInstance().selectdata(parm);
            
            if (parm.getErrCode() < 0) {
            	this.messageBox("ϵͳ����");
            	err(parm.getErrCode()+":"+parm.getErrText());
            	return false;
            }
            
            if (parm.getCount() > 0) {
            	this.messageBox("��������Ϊ:��" + getComboBox("LEND_TYPE").getSelectedText()+"���������Ѿ�����");
            	return false;
            }
        }
        
        return true;
	}
}
