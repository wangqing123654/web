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
 * Title: 病案借阅字典
 * </p>
 * 
 * <p>
 * Description: 病案借阅字典
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
	 * 增加对Table的监听
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		// 选中行
		if (row < 0) {
			return;
		}

		setValueForParm(
				"LEND_CODE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION;LEND_TYPE;PRIORITY",
				data, row);
		selectRow = row;
		// 不可编辑
		((TTextField) getComponent("LEND_CODE")).setEnabled(false);
		// 设置删除按钮状态
		((TMenuItem) getComponent("delete")).setEnabled(true);
	}

	/**
	 * 新增
	 */
	public void onInsert() {

		// 控件验证
		if (!this.validate()) {
			return;
		}
		
		// 数据验证
		if (!this.checkData()) {
			return;
		}

		TParm parm = this
				.getParmForTag("LEND_CODE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION;LEND_TYPE;PRIORITY");
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM", Operator.getIP());
		TParm result = MROLendTool.getInstance().insertdata(parm);
		// 判断错误值
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		
		if (StringUtils.equals("O", parm.getValue("LEND_TYPE"))) {
			parm.setData("LEND_TYPE_DESC", "门诊挂号");
		} else if (StringUtils.equals("I", parm.getValue("LEND_TYPE"))) {
			parm.setData("LEND_TYPE_DESC", "住院登记");
		} else {
			parm.setData("LEND_TYPE_DESC", "普通借阅");
		}
		
		// 显示新增数据
		int row = ((TTable) getComponent("TABLE"))
				.addRow(
						parm,
						"LEND_CODE;LEND_TYPE_DESC;LEND_DESC;PY1;PY2;LEND_DAY;PRIORITY;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERM;LEND_TYPE");

		data.setRowData(row, parm);
		this.messageBox("添加成功！");
	}

	/**
	 * 更新
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
			parm.setData("LEND_TYPE_DESC", "门诊挂号");
		} else if (StringUtils.equals("I", parm.getValue("LEND_TYPE"))) {
			parm.setData("LEND_TYPE_DESC", "住院登记");
		} else {
			parm.setData("LEND_TYPE_DESC", "普通借阅");
		}

		TParm result = MROLendTool.getInstance().updatedata(parm);
		// 判断错误值
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		// 选中行
		int row = ((TTable) getComponent("Table")).getSelectedRow();
		if (row < 0)
			return;
		// 刷新，设置末行某列的值
		data.setRowData(row, parm);
		((TTable) getComponent("Table")).setRowParmValue(row, data);
		this.messageBox("修改成功！");
	}

	/**
	 * 保存
	 */
	public void onSave() {
		if (selectRow == -1) {
			onInsert();
			return;
		}
		onUpdate();
	}

	/**
	 * 查询
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
		// 判断错误值
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		
		for (int i = 0; i < data.getCount(); i++) {
			if (StringUtils.equals("O", data.getValue("LEND_TYPE", i))) {
				data.addData("LEND_TYPE_DESC", "门诊挂号");
			} else if (StringUtils.equals("I", data.getValue("LEND_TYPE", i))) {
				data.addData("LEND_TYPE_DESC", "住院登记");
			} else {
				data.addData("LEND_TYPE_DESC", "普通借阅");
			}
		}
		
		((TTable) getComponent("Table")).setParmValue(data);
		((TTextField) this.getComponent("LEND_CODE")).setEnabled(true);
		this
				.clearValue("LEND_CODE;LEND_TYPE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION;PRIORITY");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this
				.clearValue("LEND_CODE;LEND_TYPE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION;PRIORITY");
		((TTable) getComponent("Table")).clearSelection();
		selectRow = -1;
		// 设置删除按钮状态
		((TMenuItem) getComponent("delete")).setEnabled(false);
		((TTextField) getComponent("LEND_CODE")).setEnabled(true);
		onQuery();
	}

	/**
	 * 删除
	 */
	public void onDelete() {
		if (this.messageBox("提示", "是否删除", 2) == 0) {
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
			this.messageBox("删除成功！");
			onClear();
		} else {
			return;
		}
	}

	/**
	 * 根据汉字输出拼音首字母
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
		// 光标下移
		((TTextField) getComponent("PY1")).grabFocus();
		return null;
	}

	/**
	 * 得到ComboBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * 页面控件数据验证
	 * 
	 * @return boolean
	 * @author wangbin 20141010
	 */
	private boolean validate() {
		if (this.getText("LEND_CODE").trim().length() <= 0
				|| this.getText("LEND_DESC").trim().length() <= 0) {
			this.messageBox("病案借阅原因编码和说明不能为空！");
			return false;
		}
        
        if (StringUtils.isEmpty(getComboBox("LEND_TYPE").getSelectedID())) {
        	this.messageBox("借阅类型不能为空！");
        	return false;
        }
        
        if (StringUtils.isEmpty(getComboBox("PRIORITY").getSelectedID())) {
        	this.messageBox("借阅优先级不能为空！");
        	return false;
        }
        
        return true;
	}
	
	/**
	 * 数据验证
	 * 
	 * @return boolean
	 * @author wangbin 20141010
	 */
	private boolean checkData() {
        // 针对借阅类别为门诊挂号和住院登记，只可以有一条数据
        if ("O,I".contains(getComboBox("LEND_TYPE").getSelectedID())) {
        	TParm parm = new TParm();
            parm.setData("LEND_TYPE", getComboBox("LEND_TYPE").getSelectedID());
            parm = MROLendTool.getInstance().selectdata(parm);
            
            if (parm.getErrCode() < 0) {
            	this.messageBox("系统错误");
            	err(parm.getErrCode()+":"+parm.getErrText());
            	return false;
            }
            
            if (parm.getCount() > 0) {
            	this.messageBox("借阅类型为:【" + getComboBox("LEND_TYPE").getSelectedText()+"】的数据已经存在");
            	return false;
            }
        }
        
        return true;
	}
}
