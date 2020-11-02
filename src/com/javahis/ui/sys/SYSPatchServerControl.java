package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.manager.TIOM_PatchServer;

/**
 * <p>
 * Title:���η���������
 * </p>
 * 
 * <p>
 * Description:���η���������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author zhangy 2009.7.20
 * @version 1.0
 */
public class SYSPatchServerControl extends TControl {
	public SYSPatchServerControl() {
		super();
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		initPage();
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		if (!checkPatchServerData()) {
			return;
		}
		TIOM_PatchServer.setSleeptime(this.getValueInt("SleepTime"));
		TIOM_PatchServer.setExpiredtime(this.getValueInt("ExpiredTime"));
		this.messageBox("P0001");
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		this.setValue("SleepTime", 0);
		this.setValue("ExpiredTime", 0);
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		this.setValue("SleepTime", TIOM_PatchServer.getSleeptime());
		this.setValue("ExpiredTime", TIOM_PatchServer.getExpiredtime());
	}

	/**
	 * ���ݼ���
	 * 
	 * @return
	 */
	private boolean checkPatchServerData() {
		if (this.getValueInt("SleepTime") == 0) {
			this.messageBox("����ʱ�䲻��Ϊ0");
			return false;
		}
		if (this.getValueInt("ExpiredTime") == 0) {
			this.messageBox("����ɨ��ʱ�䲻��Ϊ0");
			return false;
		}
		return true;
	}
}
