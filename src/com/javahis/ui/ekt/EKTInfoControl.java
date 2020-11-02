package com.javahis.ui.ekt;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title: ҽ�ƿ��ۿ�
 * </p>
 * 
 * <p>
 * Description: ����ҽ��վʹ�ò�ѯҽ�ƿ���Ϣ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 2012-1-04
 * @version 1.0
 */
public class EKTInfoControl extends TControl{
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		TParm parm = (TParm) getParameter();
		if (null == parm) {
			return;
		}
		this.setValue("MR_NO", parm.getValue("MR_NO"));
		this.setValue("CARD_NO", parm.getValue("CARD_NO"));
		this.setValue("PAT_NAME", parm.getValue("PAT_NAME"));
		this.setValue("SEX", parm.getValue("SEX"));
		this.setValue("OLD_AMT", parm.getDouble("CURRENT_BALANCE"));
		
	}
}
