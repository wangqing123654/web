package com.javahis.ui.opd;


import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
/**
 * 
 * <p>
 * Title:就诊金额查询
 * </p>
 * 
 * <p>
 * Description:门急诊医生站查询就诊病患操作金额
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author zhangp 2013.12.31
 * @version 4.0
 */
public class OPDOrderPreviewAmtForPreControl extends TControl {
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}
	private void initPage(){
		TParm parm = (TParm) getParameter();
		double exe = parm.getDouble("EXE");
		double opb = parm.getDouble("OPB");
		double master = parm.getDouble("MASTER");
		double billedAmt = parm.getDouble("BILLAMT");
		setValue("EKT_AMT", master-opb-exe+billedAmt);
		setValue("FEE_Y", opb);
		setValue("EXE_AMT", exe);
		setValue("ALL_MASTER", master + billedAmt);
	}
}
