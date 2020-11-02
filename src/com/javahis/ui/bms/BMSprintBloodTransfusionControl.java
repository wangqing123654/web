package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.bms.BMSApplyMTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TMenuItem;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BMSprintBloodTransfusionControl
    extends TControl {

    // 外部调用传参
    private TParm parm;

    private String mr_no;

    private String adm_type = "";

    private String case_no = "";

    

    /**
     * 初始化方法
     */
    public void onInit() {
        Object obj = this.getParameter();
        if (obj != null) {
            parm = (TParm) obj;
            mr_no = parm.getValue("MR_NO");
            case_no = parm.getValue("CASE_NO");
            if (parm.existData("ADM_TYPE")) {
                adm_type = parm.getValue("ADM_TYPE");
            }
            else {
                ( (TMenuItem) getComponent("new")).setEnabled(false);
            }
        }

        TParm inparm = new TParm();
        inparm.setData("MR_NO", mr_no);
        inparm.setData("CASE_NO", case_no);
        if (!"".equals(adm_type)) {
            inparm.setData("ADM_TYPE", adm_type);
        }
        TParm result = BMSApplyMTool.getInstance().onApplyQuery(inparm);
        this.getTable("TABLE").setParmValue(result);
    }

    /**
     * 新建申请单
     */
    public void onNew() {
        parm.setData("FROM_FLG", "1");
        TParm result = (TParm) openDialog("%ROOT%\\config\\bms\\BMSApply.x",
                                          parm);
        this.closeWindow();
    }

    /**
     * 返回方法
     */
    public void onReturn() {
        TTable table = this.getTable("TABLE");
        if (table.getSelectedRow() < 0) {
            this.messageBox("E0134");
            return;
        }
        TParm inparm = new TParm();
        inparm.setData("APPLY_NO",
                       table.getItemString(table.getSelectedRow(), "APPLY_NO"));
        inparm.setData("SIGN", table.getParmValue().getValue("SIGN", table.getSelectedRow()));
        inparm.setData("FROM_FLG", "2");
        if (!"".equals(adm_type)) {
            TParm result = (TParm) openDialog("%ROOT%\\config\\bms\\BMSApply.x",
                                              inparm);
        }
        else {
            this.setReturnValue(inparm);
        }
        this.closeWindow();
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }


}
