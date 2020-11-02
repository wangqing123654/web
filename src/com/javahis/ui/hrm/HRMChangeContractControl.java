package com.javahis.ui.hrm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title: ��������ͬ���������</p>
 *
 * <p>Description: ��������ͬ���������</p>
 *
 * <p>Copyright: javahis 20090922</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui
 * @version 1.0
 */

public class HRMChangeContractControl extends TControl{

	/**
	 * ��ʼ���¼�
	 */
	public void onInit() {
		super.onInit();
		//��ʼ������
		initData();
	}
	TParm showParm=null;
	TParm dataStoreParm=null;
	/**
	 * ��ʼ������
	 */
	private void initData() {
		showParm=(TParm)((TParm)this.getParameter()).getData("showParm");
		dataStoreParm=(TParm)((TParm)this.getParameter()).getData("dataStoreParm");
		((TTextField)this.getComponent("MR_NO")).setText(showParm.getValue("MR_NO"));
		((TTextField)this.getComponent("PAT_NAME")).setText(showParm.getValue("PAT_NAME"));
		((TTextField)this.getComponent("SEX_CODE")).setText(showParm.getValue("SEX_CODE"));
		((TTextField)this.getComponent("BIRTHDAY")).setText(showParm.getValue("BIRTHDAY"));
		((TTextField)this.getComponent("STAFF_NO")).setText(showParm.getValue("STAFF_NO"));
		((TTextField)this.getComponent("PAT_DEPT")).setText(showParm.getValue("PAT_DEPT"));
		((TTextField)this.getComponent("CONTRACT_DESC")).setText(showParm.getValue("CONTRACT_DESC"));
		
	
//		mrTab.setDataStore(contractD);
	}
	public void onSave() {
		String value=(String)((TTextFormat)this.getComponent("CONTRACT")).getValue();
		if(value.equals(dataStoreParm.getValue("CONTRACT_CODE"))){
			this.messageBox("���ǰ��ĺ�ͬ���Ʋ�����ͬ��");
			return ;
			
		}
		TParm parm=new TParm();
		parm.setData("COMPANY_CODE",((TTextFormat)this.getComponent("COMPANY_CODE")).getValue());
		parm.setData("CONTRACT_CODE",  ((TTextFormat)this.getComponent("CONTRACT")).getValue());
		parm.setData("CONTRACT_DESC", ((TTextFormat)this.getComponent("CONTRACT")).getText());
		this.setReturnValue(parm);
		this.closeWindow();
	}
}
