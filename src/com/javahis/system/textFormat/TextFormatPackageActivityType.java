package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;

/**
 * <p>Title: ��ײ�����������������</p>
 *
 * <p>Description: ��ײ�����������������</p>
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
     * ִ��Module����
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
     * �½�����ĳ�ʼֵ
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null)
            return;
        object.setValue("Width", "81");
        object.setValue("Height", "23");
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "����,70;����,200");
        object.setValue("PopupMenuWidth", "200");
        object.setValue("PopupMenuHeight", "100");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "��ײ���������");
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
     * ��ʾ��������
     * @return String
     */
    public String getPopupMenuHeader() {

        return "����,70;����,200";
    }
    
    /**
     * ��ʾ��������
     * @return String
     */
    public int getPopupMenuWidth() {

        return popupMenuWidth;
    }
    /**
     * ��ʾ��������
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
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        super.setAttribute(name, value);
    }

}
