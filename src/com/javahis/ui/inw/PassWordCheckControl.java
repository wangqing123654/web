package com.javahis.ui.inw;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TButton;
import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TPasswordField;

/**
 * <p>
 * Title: �û�����������֤
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
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
		outType = (String) this.getParameter();
		if (!outType.equals("singleExe")) {
			callFunction("UI|ID|setEnabled", true);
			id.setValue(Operator.getID());
			passwd.grabFocus();
		} else {
			id.setValue(Operator.getID());
			passwd.grabFocus();
		}
	}

	/**
	 * �õ��ؼ�
	 */
	public void myInitCtl() {
		id = (TTextField) this.getComponent("ID");
		passwd = (TPasswordField) this.getComponent("PASSWORD");
		ok = (TButton) this.getComponent("OK");
		cancle = (TButton) this.getComponent("CANCLE");
	}

	public void onOK() {
		String pass = passwd.getValue();
		String userID = id.getValue();
		if(userID.equals("")){
			this.messageBox("�û�������Ϊ��");
			return;
		}
		if(pass.equals("")){
			this.messageBox("���벻��Ϊ��");
			return;
		}
		// �ж�����
		if (!pass.equals(OperatorTool.getInstance().decrypt(
				OperatorTool.getInstance().getOperatorPassword(userID)))) {
			this.messageBox("�������!");
			passwd.setValue("");
			return;
		}
		if (outType.equals("singleExe")) {
			TParm parm=new TParm();
			parm.setData("USER_ID", userID);
			parm.setData("RESULT", "OK");
			this.setReturnValue(parm);
		} else {
			this.setReturnValue("OK");
		}
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
		JavaHisDebug.runFrame("inw\\passWordCheck.x");

	}

}
