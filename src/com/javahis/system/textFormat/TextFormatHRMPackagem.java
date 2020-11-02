package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: ��������ײ���������</p>
 *
 * <p>Description: ��������ײ���������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.11.18
 * @version 1.0
 */
public class TextFormatHRMPackagem
    extends TTextFormat {
    /**
     * ����ע��
     */
    private String activeFlg;
    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT PACKAGE_CODE AS ID,PACKAGE_DESC AS NAME,DESCRIPTION,ENNAME,PY1,PY2 " +
            "   FROM HRM_PACKAGEM ";
        String sql1 = " ORDER BY PACKAGE_CODE ";
        StringBuffer sb = new StringBuffer();
        String activeFlgTemp = TypeTool.getString(getTagValue(getActiveFlg()));
        if (activeFlgTemp != null && activeFlgTemp.length() > 0)
            sb.append(" ACTIVE_FLG = '" + activeFlgTemp + "' ");
        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
        //System.out.println("SQL==="+sql);
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
        object.setValue("PopupMenuHeader", "����,100;����,150;��ע,200");
        object.setValue("PopupMenuWidth", "450");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;DESCRIPTION,1;PY1,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "��������ײ�");
        object.setValue("ShowColumnList", "ID;NAME;DESCRIPTION");

    }
    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name;Remark");
    }

    /**
     * ��ʾ��������
     * @return String
     */
    public String getPopupMenuHeader() {

        return "����,100;����1,150;��ע,200";
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
        if ("ActiveFlg".equalsIgnoreCase(name)) {
            setActiveFlg(value);
            getTObject().setValue("ActiveFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }


    public String getActiveFlg() {
        return activeFlg;
    }

    public void setActiveFlg(String activeFlg) {
        this.activeFlg = activeFlg;
    }
}
