package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;

/**
 * <p> Title: 药品不良事件名称下拉列表 </p>
 * 
 * <p>Description: 药品不良事件名称下拉列表 </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: BlueCore </p>
 * 
 * @author wanglong 2013.09.30
 * @version 1.0
 */
public class TextFormatADRName extends TTextFormat {

    /**
     * 器官代码
     */
    private String organCode1;

    /**
     * 得到器官代码
     * @return
     */
    public String getOrganCode1() {
        return organCode1;
    }

    /**
     * 设置器官代码
     * @param organCode1
     */
    public void setOrganCode1(String organCode1) {
        this.organCode1 = organCode1;
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
                "SELECT ADR_ID AS ID, ADR_DESC AS NAME, ORGAN_CODE1||' '||ORGAN_CODE2||' '||ORGAN_CODE3 AS SID,ADR_CODE AS TID, PY "
                        + " FROM ACI_ADRNAME WHERE 1=1 # ORDER BY ADR_CODE, ORGAN_CODE1 ";
        String organCode1 = getTagValue(getOrganCode1().trim()).toString();
        if (organCode1 != null && organCode1.length() > 0) {
            sql = sql.replaceFirst("#", " AND ORGAN_CODE1 = '" + organCode1 + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        return sql;
    }

    /**
     * 新建对象的初始值
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null) return;
        object.setValue("Width", "81");
        object.setValue("Height", "23");
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "不良反应ID,90;不良反应名称,100;系统-器官代码,110");
        object.setValue("PopupMenuWidth", "310");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "TID,1;SID,1;PY,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "药品不良事件名称");
        object.setValue("ShowColumnList", "NAME");
    }

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        setPopupMenuFilter("TID,1;SID,1;PY,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("ADR Code;ADR Name;Organ Code");
    }
    
    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        data.add(new TAttribute("organCode1", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("organCode1".equalsIgnoreCase(name)) {
            setOrganCode1(value);
            getTObject().setValue("organCode1", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
