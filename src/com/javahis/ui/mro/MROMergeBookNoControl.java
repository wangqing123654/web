package com.javahis.ui.mro;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title: 合并案卷册号弹出框控制类
 * 
 * <p>
 * Description:
 * 
 * <p>
 * Copyright:
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangbin 2014.08.29
 * @version 4.0
 */
public class MROMergeBookNoControl extends TControl {


	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		this.onInitPage();
	}

	/**
	 * 初始化页面
	 */
	public void onInitPage() {
		this.setValue("BOOK_NO", this.getParameter());
	}
	
	/**
	 * 点击确定事件
	 */
	public void onConfirm() {
		String newBookNo = this.getValueString("BOOK_NO").trim();
		
		if (StringUtils.isEmpty(newBookNo)) {
			this.messageBox("合并后册号不能为空");
			return;
		}
		
		if (!newBookNo.matches("[0-9]+")) {
			this.messageBox("合并后册号只能为数字");
			return;
		}
		
		int bookNo = Integer.parseInt(newBookNo);
		if (bookNo <= 0) {
			this.messageBox("册号需从01开始");
			return;
		}
		
		newBookNo = String.format("%02d", bookNo);
		
		TParm parm = new TParm();
		parm.setData("NEW_BOOK_NO", newBookNo);
		this.setReturnValue(parm);
		this.closeWindow();
	}
	
	/**
	 * 点击取消事件
	 */
	public void onCancel() {
		this.closeWindow();
	}
}
