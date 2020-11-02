package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;

public class TextFormataCustomerSource extends TTextFormat {
	/**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql ="SELECT ID,CHN_DESC AS NAME FROM SYS_DICTIONARY WHERE GROUP_ID = 'CUSTOMER_SOURCE'";
        return sql;
        
    }
    
	/**
	 * 新建对象的初始值
	 * 
	 * @param object
	 *            TObject
	 */
	public void createInit(TObject object) {
		if (object == null)
			return;
		object.setValue("Width", "81");
		object.setValue("Height", "23");
		object.setValue("Text", "");
		object.setValue("HorizontalAlignment", "2");
		object.setValue("PopupMenuHeader", "代码,100;名称,100");
		object.setValue("PopupMenuWidth", "300");
		object.setValue("PopupMenuHeight", "300");
		object.setValue("PopupMenuFilter", "ID,1;NAME,1");
		object.setValue("FormatType", "combo");
		object.setValue("ShowDownButton", "Y");
		object.setValue("Tip", "客户来源");
		object.setValue("ShowColumnList", "ID;NAME");
	}

	public void onInit() {
		super.onInit();
		setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
		setLanguageMap("NAME|ENNAME");
		setPopupMenuEnHeader("Code;Name");
	}
}
