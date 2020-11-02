package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: ҽ��������������</p>
 *
 * <p>Description: ҽ��������������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.27
 * @version 1.0
 */
public class TextFormatSYSOrderCat1Code
    extends TTextFormat {
    /**
     * ҽ��ϸ����Ⱥ��
     */
    private String cat1Type;
    /**
     * ����ҽ��ϸ����Ⱥ��
     * @param cat1Type String
     */
    public void setCat1Type(String cat1Type) {
        this.cat1Type = cat1Type;
    }

    /**
     * �õ�ҽ��ϸ����Ⱥ��
     * @return String
     */
    public String getCat1Type() {
        return cat1Type;
    }

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT ORDER_CAT1_CODE AS ID,ORDER_CAT1_DESC AS NAME,ENNAME,PY1,PY2 FROM SYS_ORDER_CAT1 ";
        String sql1 = " ORDER BY SEQ,ORDER_CAT1_CODE ";

        StringBuffer sb = new StringBuffer();

        String cat1Type = TypeTool.getString(getTagValue(getCat1Type()));
        if (cat1Type != null && cat1Type.length() > 0)
            sb.append(" CAT1_TYPE = '" + cat1Type + "' ");

        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
//        this.setPopupMenuSQL(sql);
//        super.onQuery();
//    System.out.println("sql"+sql);
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
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
//        object.setValue("ShowColumnList", "NAME");
//        object.setValue("ValueColumn", "ID");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "ҽ������");
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
        data.add(new TAttribute("Cat1Type", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("Cat1Type".equalsIgnoreCase(name)) {
            setCat1Type(value);
            getTObject().setValue("Cat1Type", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
