package com.javahis.ui.emr;

import com.dongyang.control.*;

/**
 * <p>Title: HISҽ��ϵͳ</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author WangM
 * @version 1.0
 */
public class ModifControl extends TControl {
    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        Object obj = this.getParameter();
        if(obj!=null){
            this.setValue("TextArea",obj.toString());
        }
    }
    /**
     * ����
     */
    public void onSend(){
       this.setReturnValue(this.getValue("TextArea"));
       this.closeWindow();
    }
}
