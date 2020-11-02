package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.TTextFormat;
/**
 *
 * <p>Title: ��ҳ������������</p>
 *
 * <p>Description: ��ҳ������������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TextFormatMROcharge extends TTextFormat {
    /**
     * ִ��Module����
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT ID,CHN_DESC AS NAME,PY1,PY2 FROM SYS_DICTIONARY WHERE GROUP_ID='MRO_CHARGE' ORDER BY SEQ,ID ";
        return sql;
    }

    /**
     * �½�����ĳ�ʼֵ
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null)
            return;
        object.setValue("Width","81");
        object.setValue("Height","23");
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "ID,100;NAME,100");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "��ҳ����");
        object.setValue("ShowColumnList", "ID;NAME");
    }
    /**
     * ��ʾ��������
     * @return String
     */
    public String getPopupMenuHeader() {

        return "ID,100;NAME,200";
    }
    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList","String","NAME","Left"));
        data.add(new TAttribute("ValueColumn","String","ID","Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean","N","Center"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        super.setAttribute(name, value);
    }}
