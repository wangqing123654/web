package com.javahis.ui.spc;

import jdo.sys.OperatorTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TPasswordField;
import com.dongyang.ui.TTextField;
import com.javahis.util.JavaHisDebug;

public class PassWordCheckControl extends TControl {

	TTextField id;
	TPasswordField passwd;
	TButton ok, cancle;
	String outType;

	public PassWordCheckControl() {
	}

	public void onInit() {
		super.onInit();
		myInitCtl();
		initID();
	}

	public void initID() {
		/**
		outType = (String) this.getParameter();
		if (outType.equals("singleExe")) {
			callFunction("UI|ID|setEnabled", true);
			this.grabFocus("ID");
		} else {
			id.setValue(Operator.getID());
			passwd.grabFocus();
		}*/
		id.grabFocus();
	}

	/**
	 * �õ��ؼ�
	 */
	public void myInitCtl() {
		id = (TTextField) this.getComponent("ID");
		//passwd = (TPasswordField) this.getComponent("PASSWORD");
		ok = (TButton) this.getComponent("OK");
		cancle = (TButton) this.getComponent("CANCLE");
	}

	public void onOK() {
		//String pass = passwd.getValue();
		String userID = id.getValue();
		if(userID.equals("")){
			this.messageBox("��������Ϊ��");
			return;
		}
		/**
		if(pass.equals("")){
			this.messageBox("���벻��Ϊ��");
			return;
		}*/
		// �ж�����
		/**
		if (!pass.equals(OperatorTool.getInstance().decrypt(
				OperatorTool.getInstance().getOperatorPassword(userID)))) {
			this.messageBox("�������!");
			passwd.setValue("");
			return;
		}*/
		
		//�ж��û��Ƿ����
		if(!OperatorTool.getInstance().existsOperator(userID)){
			this.messageBox("���Ŵ���!");
			id.setValue("");
			return ;
		}
		TParm parm = new TParm();
		parm.setData("USER_ID", userID);
		parm.setData("OK", "OK");
		this.setReturnValue(parm);
		this.closeWindow();
	}

	public void onCANCLE() {
		this.closeWindow();
	}

	// ��������
	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// JavaHisDebug.TBuilder();

		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("spc\\passWordCheck.x");

	}

}
