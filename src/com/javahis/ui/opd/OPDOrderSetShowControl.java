package com.javahis.ui.opd;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
/**
 *
 * <p>
 * Title: ����ҽ������վ��ʾ����ҽ��
 * </p>
 *
 * <p>
 * Description:����ҽ������վ��ʾ����ҽ��
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 200800901
 * @version 1.0
 */
public class OPDOrderSetShowControl extends TControl {
	TTable table;
	TParm parm;
	public void onInit() {
		super.onInit();
		initParam();
		initForm();
	}
	/**
	 * ��ʼ��������ܵĲ���
	 */
	public void initParam(){
		parm=(TParm)this.getParameter();
//		// System.out.println("OPDOrderSetShowControl.init parm"+parm);
	}
	/**
	 * ��ʼ������
	 */
	public void initForm(){
		/*
		 *
		 * ҽ��,200;���,100;����,40,double;��λ,40,UNIT_CODE;����,50,double;�ܼ�,60,double;ִ�п���,80,TECH_DEPT;����,80,OPTITEM_CODE;�������,80,INSPAY_TYPE
		 * 0,right;1,left;2,right;3,right;4,right;5,left;6,left;7,left
		 * ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
		 * ҽ��,200;���,100;����,40,double;��λ,40,UNIT_CODE;����,50,double;�ܼ�,60,double;ִ�п���,80,TECH_DEPT;����,80,OPT_ITEM;�������,80,INS_PAY
		 */
		table=(TTable)this.getComponent("TABLEORDERSET");
		table.setParmValue(parm);
		int count=parm.getCount("ORDER_DESC");
		double arAmt=0.0;
		for(int i=0;i<count;i++){
			arAmt+=parm.getDouble("OWN_AMT",i);
		}
		this.setValue("AR_AMT", arAmt);
	}
}
