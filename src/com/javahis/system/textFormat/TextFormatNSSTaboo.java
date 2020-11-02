package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: �˵�������������</p>
 *
 * <p>Description: �˵�������������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2010.11.12
 * @version 1.0
 */
public class TextFormatNSSTaboo
    extends TTextFormat {
    /**
     * ����ע��
     */
    private String tabooFlg;
    /**
     * ���ý���ע��
     * @param tabooFlg String
     */
    public void setTabooFlg(String tabooFlg) {

        this.tabooFlg = tabooFlg;
        setModifySQL(true);
    }

    /**
     * �õ�����ע��
     * @return String
     */
    public String getTabooFlg() {
        return tabooFlg;
    }

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT TABOO_CODE AS ID ,TABOO_CHN_DESC AS NAME,TABOO_ENG_DESC AS ENNAME,PY1,PY2 " +
            "   FROM NSS_TABOO ";
        String sql1 = " ORDER BY TABOO_CODE ";

        StringBuffer sb = new StringBuffer();
        String tabooFlg = TypeTool.getString(getTagValue(getTabooFlg()));
        if (tabooFlg != null && tabooFlg.length() > 0)
            sb.append(" TABOO_FLG  = '" + tabooFlg + "' ");

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
        object.setValue("Tip", "�˵�����");
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
        data.add(new TAttribute("TabooFlg", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("TabooFlg".equalsIgnoreCase(name)) {
            setTabooFlg(value);
            getTObject().setValue("TabooFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
