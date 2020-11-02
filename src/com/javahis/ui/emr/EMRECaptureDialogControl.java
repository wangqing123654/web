package com.javahis.ui.emr;

import com.dongyang.control.TControl;

import com.dongyang.tui.text.ECapture;
import com.dongyang.ui.TWord;
import com.dongyang.wcomponent.util.TiString;


/**
 *
 * @author whaosoft
 *
 */
public class EMRECaptureDialogControl extends TControl {

	/**  */
	private TWord word = null;

	/**
	 * ��ʼ��
	 */
	public void onInit() {

		word = (TWord) getParameter();
	}

	/**
	 * ȷ��
	 */
	public void onSave() {

		if (!this.doCheck())
			return;

		//
		this.closeWindow();
	}

	/**
	 *
	 * @return
	 */
	private boolean doCheck() {

		String ecName = (String) this.getValue("NAME");

		ECapture ec = word.findCapture(ecName);

		if( TiString.isEmpty(ecName) ){
			this.messageBox("�ؼ����Ʋ�����Ϊ��!");
			return false;
		}
		else if( null!=ec ){
			this.messageBox("��'"+ecName+"'������ץȡ�ؼ��Ѵ���!");
			return false;
		}else{
	        this.addECapture(ecName);
			return true;
		}
	}

	/**
	 *
	 * @param ecName
	 */
	private void addECapture( String ecName ){

        ECapture obj2 = word.insertCaptureObject();
        ECapture obj1  =word.insertCaptureObject();
        obj2.setCaptureType(1);

        //
        obj1.setName(ecName);
        obj2.setName(ecName);
	}

}
