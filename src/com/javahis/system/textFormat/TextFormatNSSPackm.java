package com.javahis.system.textFormat;

import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.TTextFormat;
import com.dongyang.config.TConfigParse.TObject;

/**
 * <p>Title: ��ʳ�ײ���������</p>
 *
 * <p>Description: ��ʳ�ײ���������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author ���� 2010.11.11
 * @version 1.0
 */
public class TextFormatNSSPackm
    extends TTextFormat {
    /**
     * ��ʳ����
     */
    private String dietType;
    /**
     * ��ʳ���
     */
    private String dietKind;
    /**
     * ������ʳ����
     * @param dietType String
     */
    public void setDietType(String dietType) {

        this.dietType = dietType;
        setModifySQL(true);
    }

    /**
     * ������ʳ���
     * @param dietKind String
     */
    public void setDietKind(String dietKind) {
        this.dietKind = dietKind;
        setModifySQL(true);
    }

    /**
     * �õ���ʳ����
     * @return String
     */
    public String getDietType() {
        return dietType;
    }


    /**
     * �õ���ʳ���
     * @return String
     */
    public String getDietKind() {
        return dietKind;
    }


    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT PACK_CODE AS ID ,PACK_CHN_DESC AS NAME,PACK_ENG_DESC AS ENNAME,PY1,PY2 " +
            "   FROM NSS_PACKM ";
        String sql1 = " ORDER BY PACK_CODE ";

        StringBuffer sb = new StringBuffer();
        String dietType = TypeTool.getString(getTagValue(getDietType()));
        if (dietType != null && dietType.length() > 0)
            sb.append(" DIET_TYPE  = '" + dietType + "' ");

        String dietKind = TypeTool.getString(getTagValue(getDietKind()));
        if (dietKind != null && dietKind.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" DIET_KIND = '" + dietKind + "' ");
        }
        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
//        System.out.println("����sql"+sql);
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
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "��ʳ�ײ�");
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
        data.add(new TAttribute("DietType", "String", "", "Left"));
        data.add(new TAttribute("DietKind", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("DietType".equalsIgnoreCase(name)) {
            setDietType(value);
            getTObject().setValue("DietType", value);
            return;
        }
        if ("DietKind".equalsIgnoreCase(name)) {
            setDietKind(value);
            getTObject().setValue("DietKind", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
