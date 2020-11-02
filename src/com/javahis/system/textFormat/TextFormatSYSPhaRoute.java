package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: �÷���������</p>
 *
 * <p>Description: �÷���������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.29
 * @version 1.0
 */
public class TextFormatSYSPhaRoute
    extends TTextFormat {
    /**
     * ��ҩע��
     */
    private String wesmedFlg;
    /**
     * ������ҩע��
     * @param wesmedFlg String
     */
    public void setWesmedFlg(String wesmedFlg) {
        this.wesmedFlg = wesmedFlg;
    }

    /**
     * �õ���ҩע��
     * @return wesmedFlg String
     */
    public String getWesmedFlg() {
        return this.wesmedFlg;
    }

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT ROUTE_CODE AS ID,ROUTE_CHN_DESC AS NAME ,ROUTE_ENG_DESC AS ENNAME,PY1, PY2 FROM SYS_PHAROUTE ";
        String sql1 = " ORDER BY SEQ,ROUTE_CODE";

        StringBuffer sb = new StringBuffer();

        String wesmedFlg = TypeTool.getString(getTagValue(getWesmedFlg()));
        if (wesmedFlg != null && wesmedFlg.length() > 0)
            sb.append(" WESMED_FLG = '" + wesmedFlg + "' ");

        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
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
        object.setValue("Tip", "�÷�");
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
        data.add(new TAttribute("WesmedFlg", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("WesmedFlg".equalsIgnoreCase(name)) {
            setWesmedFlg(value);
            getTObject().setValue("WesmedFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
