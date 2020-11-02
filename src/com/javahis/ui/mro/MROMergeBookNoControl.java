package com.javahis.ui.mro;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title: �ϲ������ŵ����������
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
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		this.onInitPage();
	}

	/**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage() {
		this.setValue("BOOK_NO", this.getParameter());
	}
	
	/**
	 * ���ȷ���¼�
	 */
	public void onConfirm() {
		String newBookNo = this.getValueString("BOOK_NO").trim();
		
		if (StringUtils.isEmpty(newBookNo)) {
			this.messageBox("�ϲ����Ų���Ϊ��");
			return;
		}
		
		if (!newBookNo.matches("[0-9]+")) {
			this.messageBox("�ϲ�����ֻ��Ϊ����");
			return;
		}
		
		int bookNo = Integer.parseInt(newBookNo);
		if (bookNo <= 0) {
			this.messageBox("������01��ʼ");
			return;
		}
		
		newBookNo = String.format("%02d", bookNo);
		
		TParm parm = new TParm();
		parm.setData("NEW_BOOK_NO", newBookNo);
		this.setReturnValue(parm);
		this.closeWindow();
	}
	
	/**
	 * ���ȡ���¼�
	 */
	public void onCancel() {
		this.closeWindow();
	}
}
