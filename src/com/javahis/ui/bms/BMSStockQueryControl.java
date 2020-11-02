package com.javahis.ui.bms;

import java.util.ArrayList;
import java.util.List;

import com.dongyang.control.TControl;
import com.javahis.system.combo.TComboBMSBldsubcat;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.bms.BMSBloodTool;

/**
 * <p>
 * Title: 库存查询
 * </p>
 *
 * <p>
 * Description: 库存查询
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSStockQueryControl
    extends TControl {
    public BMSStockQueryControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 取得传入参数
        Object obj = getParameter();
        if (obj != null) {
            TParm parm = (TParm) obj;
            if (!"".equals(parm.getValue("BLOOD_NO"))) {
                this.setValue("BLOOD_NO", parm.getValue("BLOOD_NO"));
            }
            if (!"".equals(parm.getValue("BLD_CODE"))) {
                this.setValue("BLD_CODE", parm.getValue("BLD_CODE"));
            }
            if (!"".equals(parm.getValue("BLD_TYPE"))) {
                this.setValue("BLD_TYPE", parm.getValue("BLD_TYPE"));
            }
            if (!"".equals(parm.getValue("SUBCAT_CODE"))) {
                this.setValue("SUBCAT_CODE", parm.getValue("SUBCAT_CODE"));
            }
            this.onQuery();
        }
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        if (!"".equals(this.getValueString("BLOOD_NO"))) {
            parm.setData("BLOOD_NO", this.getValue("BLOOD_NO"));
        }
        if (!"".equals(this.getValueString("BLD_CODE"))) {
            parm.setData("BLD_CODE", this.getValue("BLD_CODE"));
        }
        if (!"".equals(this.getValueString("BLD_TYPE"))) {
            parm.setData("BLD_TYPE", this.getValue("BLD_TYPE"));
        }
        if (!"".equals(this.getValueString("SUBCAT_CODE"))) {
            parm.setData("SUBCAT_CODE", this.getValue("SUBCAT_CODE"));
        }
        if (!"".equals(this.getValueString("END_DATE"))) {
            parm.setData("END_DATE", this.getValue("END_DATE"));
        }
        TParm result = BMSBloodTool.getInstance().onQueryBloodStock(parm);
        //modify by lim 2012/04/27 begin
        for (int i = 0; i < result.getCount(); i++) {
			result.addData("FLG", "N") ;
		}
        //modify by lim 2012/04/27 end
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        this.getTable("TABLE").setParmValue(result);
    }

    /**
     * 传回方法
     */
    public void onReturn() {
//        TTable table = this.getTable("TABLE");
//        if (table.getSelectedRow() < 0) {
//            this.messageBox("没有选中行");
//            return;
//        }
//        setReturnValue(table.getParmValue().getRow(table.getSelectedRow()));
//        this.closeWindow();
    	
        TTable table = this.getTable("TABLE");
        table.acceptText();
        boolean bool = false ;
        List<TParm> returnVal = new ArrayList<TParm>() ;
        for (int i = 0; i < table.getRowCount(); i++) {
			String flg = table.getItemString(i, "FLG") ;
			if("Y".equals(flg)){
				bool = true ;
				returnVal.add(table.getParmValue().getRow(i)) ;
			}
		}
        if (!bool) {
            this.messageBox("没有选中行");
            return;
        }
        setReturnValue(returnVal);
        this.closeWindow();    	
    }

    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "BLOOD_NO;BLD_CODE;BLD_TYPE;SUBCAT_CODE;END_DATE";
        this.clearValue(clearStr);
        getTable("TABLE").removeRowAll();
    }

    /**
     * 变更血品
     */
    public void onChangeBld() {
        String bld_code = getComboBox("BLD_CODE").getSelectedID();
        ( (TComboBMSBldsubcat)this.getComponent("SUBCAT_CODE")).setBldCode(
            bld_code);
        ( (TComboBMSBldsubcat)this.getComponent("SUBCAT_CODE")).onQuery();
    }

    /**
     * 得到ComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
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
