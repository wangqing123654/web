package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: �����ӡ������������</p>
 *
 * <p>Description: �����ӡ������������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2010.07.05
 * @version 1.0
 */
public class TextFormatHRMPrintType
    extends TTextFormat {
    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT ID,CHN_DESC AS NAME,ENG_DESC AS  ENNAME,PY1,PY2 FROM SYS_DICTIONARY " +
            "  WHERE GROUP_ID= 'HRM_PRINTTYPE' ORDER BY SEQ,ID ";
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
        object.setValue("PopupMenuHeader", "����,100;����,100");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "�����ӡ����");
        object.setValue("ShowColumnList", "ID;NAME");

    }

    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name");
    }

    /**
     * ��ʾ��������
     * @return String
     */
    public String getPopupMenuHeader() {

        return "����,100;����,200";
    }

    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        data.add(new TAttribute("ActiveFlg", "String", "", "Left"));
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
