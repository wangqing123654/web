package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;

public class TextFormatCardType extends TTextFormat {
	/**
	 * 执行Module动作
	 * 
	 * @return String
	 */
	public String getPopupMenuSQL() {
		String sql = " SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENAME,PY1 FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_CARDTYPE' ORDER BY SEQ, ID";
		return sql;
	}

	public void onInit() {
		super.onInit();
		setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
		setLanguageMap("NAME|ENNAME");
		setPopupMenuEnHeader("Code;Name");
	}

	public void createInit(TObject object) {
		if (object == null)
			return;
		object.setValue("Width", "81");
		object.setValue("Height", "23");
		object.setValue("Text", "");
		object.setValue("HorizontalAlignment", "2");
		object.setValue("PopupMenuHeader", "号码,100;名称,150");
		object.setValue("PopupMenuWidth", "250");
		object.setValue("PopupMenuHeight", "100");
		object.setValue("PopupMenuFilter", "ID,1;NAME,1");
		object.setValue("FormatType", "combo");
		object.setValue("ShowDownButton", "Y");
		object.setValue("Tip", "卡类型下拉区域");
		object.setValue("ShowColumnList", "ID;NAME");
	}

	public String getPopupMenuHeader() {
		return "号码,100;名称,150";
	}

	public void getEnlargeAttributes(TAttributeList data) {
		data.add(new TAttributeList.TAttribute("ShowColumnList", "String",
				"NAME", "Left"));
		data.add(new TAttributeList.TAttribute("ValueColumn", "String", "ID",
				"Left"));
		data.add(new TAttributeList.TAttribute("HisOneNullRow", "boolean", "N",
				"Center"));//如果参数为Y，控件没法加空行

	}

	public void setAttribute(String name, String value) {

		super.setAttribute(name, value);
	}
}
