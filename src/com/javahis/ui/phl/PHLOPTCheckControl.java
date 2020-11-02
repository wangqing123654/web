package com.javahis.ui.phl;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

import jdo.sys.Operator;
import jdo.sys.OperatorTool;

/**
 * <p>Title: ʹ����ȷ��</p>
 *
 * <p>Description: ʹ����ȷ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.03.30
 * @version 1.0
 */
public class PHLOPTCheckControl extends TControl {
    public PHLOPTCheckControl() {
    }
    public void onInit() {
    	this.setValue("USER_ID", Operator.getID());
    }
    /**
     * ȷ�ϰ�ť
     */
    public void onBtnOK() {
        String user_id = getValueString("USER_ID");
        String password = getValueString("USER_PASSWORD");
        if ("".equals(user_id)) {
            this.messageBox("�û�������Ϊ��");
            return;
        }
        else if ("".equals(password)) {
            this.messageBox("���벻��Ϊ��");
            return;
        }
        else {
            if (!password.equals(OperatorTool.getInstance().decrypt(OperatorTool.
                getInstance().getOperatorPassword(user_id)))) {
                this.messageBox("����������������룡����");
                return;
            }
        }
        TParm parm = new TParm();
        parm.setData("USER_ID", user_id);
        setReturnValue(parm);
        this.closeWindow();
    }
    /**
     * ȡ����ť
     */
    public void onBtnCancel() {
        this.closeWindow();
    }
}
