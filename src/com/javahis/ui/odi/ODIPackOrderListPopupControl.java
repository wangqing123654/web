package com.javahis.ui.odi;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.data.TParm;

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
public class ODIPackOrderListPopupControl extends TControl {
    /**
     * TABLE
     */
    private TTable table;
    /**
     * �ײ����
     */
    private String packType;
    /**
     * ������Ա
     */
    private String deptOrDr;
    /**
     * ҽ������
     */
    private String rxKind;
    /**
     * ��ʼ������
     */
    public void onInit(){
        super.onInit();
        table = (TTable) callFunction("UI|TABLE|getThis");
        callFunction("UI|EDIT|addEventListener", TTextFieldEvent.KEY_RELEASED, this,
                     "onKeyReleased");
        callFunction("UI|EDIT|addEventListener",
                     "EDIT->" + TKeyListener.KEY_PRESSED, this, "onKeyPressed");
        table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                               "onDoubleClicked");
        initParamenter();
    }
    /**
     * ��ʼ������
     */
    public void initParamenter() {
        Object obj = this.getParameter();
        if(obj!=null){
            TParm parm = (TParm)obj;
            packType = parm.getValue("PACK_TYPE");
            deptOrDr = parm.getValue("DEPTORDR");
            rxKind = parm.getValue("RX_KIND");
        }
    }
}
