package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: 临床路径变异类别
 * </p>
 * 
 * <p>
 * Description: 临床路径变异类别
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) pangben 2015
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 20151020
 * @version 1.0
 */
public class TextFormatCLPMoncatCode extends TTextFormat {

	private String varamceCode;

	public String getVaramceCode() {
		return varamceCode;
	}

	public void setVaramceCode(String varamceCode) {
		this.varamceCode = varamceCode;
	}

	/**
	 * 执行Module动作
	 * 
	 * @return String
	 */
	public String getPopupMenuSQL() {
		String sql = " SELECT MONCAT_CODE AS ID , MONCAT_CHN_DESC AS NAME , MONCAT_ENG_DESC AS ENNAME,PY1   FROM CLP_VARMONCAT ";
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
		object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
		object.setValue("FormatType", "combo");
		object.setValue("ShowDownButton", "Y");
		object.setValue("Tip", "变异类别");
		object.setValue("ShowColumnList", "ID;NAME");
	}

	public void onInit() {
		super.onInit();
		setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
		setLanguageMap("NAME|ENNAME");
		setPopupMenuEnHeader("Code;Name");
	}

	/**
	 * 显示区域列明
	 * 
	 * @return String
	 */
	public String getPopupMenuHeader() {
		return "代码,100;名称,200";
	}

	/**
	 * 增加扩展属性
	 * 
	 * @param data
	 *            TAttributeList
	 */
	public void getEnlargeAttributes(TAttributeList data) {
		data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
		data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
		data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
		data.add(new TAttribute("varamceCode", "String", "", "Left"));
	}

	/**
	 * 设置属性
	 * 
	 * @param name
	 *            String 属性名
	 * @param value
	 *            String 属性值
	 */
	public void setAttribute(String name, String value) {
		if ("varamceCode".equalsIgnoreCase(name)) {
			setVaramceCode(value);
			getTObject().setValue("varamceCode", value);
			return;
		}
		super.setAttribute(name, value);
	}
}
