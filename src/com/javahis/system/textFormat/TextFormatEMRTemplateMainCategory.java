package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: EMR ģ�������������б�(����,����ʷ,�����)</p>
 *
 * <p>Description: EMR ģ�������������б�</p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TextFormatEMRTemplateMainCategory
    extends TTextFormat {

    /**
     * ִ��Module����
     */
    public String getPopupMenuSQL() {
        StringBuffer sb = new StringBuffer();
        //PARENT_CLASS_CODE = ROOT Ϊ������
        sb.append("SELECT CLASS_CODE AS ID,PHRASE_CODE AS NAME,PY1,PY2 FROM OPD_COMTEMPLATE_PHRASE");
        sb.append(" WHERE PARENT_CLASS_CODE='ROOT'");
        sb.append(" ORDER BY SEQ");
        return  sb.toString();
    }


    /**
     * �½�����ĳ�ʼֵ
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null) {
            return;
        }
        object.setValue("Width", "81");
        object.setValue("Height", "23");
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "����,100;����,100");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
//        object.setValue("ShowColumnList", "NAME");
//        object.setValue("ValueColumn", "ID");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "�ṹ������ģ��������");
        object.setValue("ShowColumnList", "ID;NAME");

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
