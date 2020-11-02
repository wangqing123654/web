package com.javahis.ui.sum;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TButton;

/**
 * <p>Title: �������ʼ�¼</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author ZangJH
 *
 * @version 1.0
 */
public class SUMDefeasanceControl
    extends TControl {

    TTextField defReason;
    TButton ok, cancle;


    public SUMDefeasanceControl() {
    }


    public void onInit() {
        super.onInit();
        myInitCtl();
    }

    /**
     *  �õ��ؼ�
     */
    public void myInitCtl() {
        defReason = (TTextField)this.getComponent("defReason");
        ok = (TButton)this.getComponent("OK");
        cancle = (TButton)this.getComponent("CANCLE");
    }

    public void onOK() {
        String reason = defReason.getValue();
        this.setReturnValue(reason);
        this.closeWindow();
    }

    public void onCANCLE() {
        this.closeWindow();
    }


}
