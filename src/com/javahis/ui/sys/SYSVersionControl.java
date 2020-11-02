package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTextArea;

/**
 * <p>
 * Title: �汾���¼�¼
 * </p>
 * 
 * <p>
 * Description: �汾���¼�¼
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHIS
 * </p>
 * 
 * @author yanmm 2017/9/8
 * @version 1.0
 */
public class SYSVersionControl extends TControl {

	public SYSVersionControl() {

	}

	@Override
	public void onInit() {
		super.onInit();
		readFile();

	}

	

	/**
	 * ��ȡTextArea
	 * 
	 * @param tag
	 * @return
	 */
	public TTextArea getTTextArea(String tag) {
		return (TTextArea) this.getComponent(tag);
	}
	
	/**
	 * ˢ�£����´��ļ���ȡ����
	 */
	public void onReresh() {
		readFile();
	}
	
	private void readFile() {
		StringBuilder sb = new StringBuilder("");
		byte[] res = TIOM_AppServer.readFile("%ROOT%\\WEB-INF\\config\\system\\version.txt");
		if(res == null) {
			sb.append("�ް汾��¼��");
		} else {
			sb.append(new String(res));
		}

		this.getTTextArea("VERSION_TEXT").setText(sb.toString());
	}

	public void onModifyPassword()
    {
        this.openDialog("%ROOT%\\config\\sys\\SYSUpdatePassword.x");

    }
}
