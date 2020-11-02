package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: �����ڼ���������</p>
 *
 * <p>Description: �����ڼ���������</p>
 *
 * <p>Company: Bluecore</p>
 *
 * @author zhangp 2012.10.15
 * @version 1.0
 */

public class TextFormatPESNo extends TTextFormat {
	/**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
    	String sql =
            "SELECT PES_NO ID FROM PES_RESULT ORDER BY PES_NO";
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
        object.setValue("PopupMenuHeader", "�����ڼ�,200");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1");
//        object.setValue("ShowColumnList", "NAME");
//        object.setValue("ValueColumn", "ID");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "�����ڼ�");
        object.setValue("ShowColumnList", "ID;NAME;PY1");
    }
    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1");
        setLanguageMap("NAME");
        setPopupMenuEnHeader("Code;Name");
    }
    
    /**
     * ��ʾ��������
     * @return String
     */
    public String getPopupMenuHeader() {
        return "�����ڼ�,200";
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
