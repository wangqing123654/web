package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
/**
 *Title: 住院套餐费用清单打印提示选择框
 * @author liling
 * 2014-07-04
 *
 */
public class BILDailyControl extends TControl {
    public BILDailyControl() {
    }
    public void onInit() {
    	
    }
    public void OnSelect(){
    	String includeFlg=(String) getValue("INCLUDE_FLG");
    	String outsideFlg=(String)getValue("OUTSIDE_FLG");
    	TParm parm=new TParm();
    	parm.addData("INCLUDE_FLG", includeFlg);
    	parm.addData("OUTSIDE_FLG", outsideFlg);
    	this.setReturnValue(parm);
        this.closeWindow();
    }
}
