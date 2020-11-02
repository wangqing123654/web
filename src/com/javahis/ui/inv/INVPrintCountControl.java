package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;



/**
 * 
 * <p>
 * Title:��ӡ�����������
 * </p>
 * 
 * <p>
 * Description: ��ӡ�����������
 * </p>
 * @author wangming 2013-8-30
 * @version 1.0
 */
public class INVPrintCountControl extends TControl{

	/**
     * ��ʼ������
     */
    public void onInit() {
    	
    }
	
	
	/**
     * ���ط���
     */
	public void onReturn(){
		TTextField tf = (TTextField) getComponent("PRINTCOUNT");
		
		boolean tag = true;
		try {
			Integer.parseInt(tf.getText());
		} catch (NumberFormatException e) {
			tag = false;
		}
		if(!tag){
			this.messageBox("���������֣�");
			return;
		}
		
		TParm result = new TParm();
		result.setData("PRINTCOUNT", tf.getText());
		setReturnValue(result);
        this.closeWindow();
	}
	
	
}
