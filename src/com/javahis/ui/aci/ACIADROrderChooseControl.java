package com.javahis.ui.aci;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

/**
 * <p> Title: 住院医嘱选择窗口 </p>
 * 
 * <p> Description: 住院医嘱选择窗口  </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: BlueCore </p>
 * 
 * @author wanglong 2014.01.07
 * @version 1.0
 */
public class ACIADROrderChooseControl extends TControl {
    private TTable table;

    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        TParm parm = (TParm) this.getParameter();
        if ((parm.getErrCode() != 0) || parm.getCount() <= 0) {
            messageBox("E0024");// 初始化参数失败
            return;
        }
        this.callFunction("UI|TABLE|setParmValue", parm);
    }

    /**
     * TABLE双击事件
     */
    public void onTableDoubleCliecked() {
        if ((table.getShowCount() > 0) && (table.getSelectedRow() < 0)) {
            messageBox("请选择一条记录");
            return;
        }
        int row = table.getSelectedRow();
        TParm parm = table.getParmValue();
        TParm selparm = parm.getRow(row);
        this.setReturnValue(selparm);
        this.closeWindow();
    }
    
    /**
     * 回传
     */
    public void onReturn() {
        onTableDoubleCliecked();
    }

    /**
     * 取消
     */
    public void onCancel() {
        this.closeWindow();
    }
}
