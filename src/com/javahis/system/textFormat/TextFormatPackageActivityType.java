package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;

/**
 * <p>Title: 活动套餐种类属性下拉区域</p>
 *
 * <p>Description: 活动套餐种类属性下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author huangtt 20150928
 * @version 4.5
 */
public class TextFormatPackageActivityType extends TTextFormat {
	private int popupMenuWidth;
	private int popupMenuHeight;
	/**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            "  SELECT PACKAGE_CODE AS ID,PACKAGE_DESC AS NAME,PACKAGE_ENG_DESC AS ENNAME,PY1, PY2" +
            " FROM MEM_PACKAGE WHERE PARENT_PACKAGE_CODE IS NOT NULL AND PACKAGE_PRICE IS NOT NULL" +
            " AND (PARENT_PACKAGE_CODE='0202' OR PACKAGE_CODE LIKE '04%') ORDER BY ID,SEQ";
    return sql;
    }

    /**
     * 新建对象的初始值
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null)
            return;
        object.setValue("Width", "81");
        object.setValue("Height", "23");
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "代码,70;名称,200");
        object.setValue("PopupMenuWidth", "200");
        object.setValue("PopupMenuHeight", "100");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "活动套餐种类属性");
        object.setValue("ShowColumnList", "ID;NAME");
    }
    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name");
        this.setPopupMenuWidth(260);
        this.setPopupMenuHeight(200);
    }

    /**
     * 显示区域列名
     * @return String
     */
    public String getPopupMenuHeader() {

        return "代码,70;名称,200";
    }
    
    /**
     * 显示区域列名
     * @return String
     */
    public int getPopupMenuWidth() {

        return popupMenuWidth;
    }
    /**
     * 显示区域列名
     * @return String
     */
    public int getPopupMenuHeight() {

        return popupMenuHeight;
    }
    
    public void setPopupMenuWidth(int popupMenuWidth){
    	this.popupMenuWidth=popupMenuWidth;
    }
    
    public void setPopupMenuHeight(int popupMenuHeight){
    	this.popupMenuHeight=popupMenuHeight;
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {

        super.setAttribute(name, value);
    }

}
