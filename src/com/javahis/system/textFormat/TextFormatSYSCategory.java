package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: ������������б�</p>
 *
 * <p>Description: ������������б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.26
 * @version 1.0
 */
public class TextFormatSYSCategory
    extends TTextFormat {
    /**
     * �������
     */
    private String ruleType; //RULE_TYPE
    /**
     * ��С����ע��
     */
    private String detailFlg; //DETAIL_FLG
    /**
     * ���ù������
     * @param ruleType String
     */
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    /**
     * �õ��������
     * @return String
     */
    public String getRuleType() {
        return ruleType;
    }

    /**
     * ������С����ע��
     * @param detailFlg String
     */
    public void setDetailFlg(String detailFlg) {
        this.detailFlg = detailFlg;
    }

    /**
     * �õ���С����ע��
     * @return String
     */
    public String getDetailFlg() {
        return detailFlg;
    }

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT CATEGORY_CODE AS ID,CATEGORY_CHN_DESC AS NAME,CATEGORY_ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_CATEGORY ";
        String sql1 = " ORDER BY SEQ,CATEGORY_CODE ";

        StringBuffer sb = new StringBuffer();

        String ruleType = TypeTool.getString(getTagValue(getRuleType()));
        if (ruleType != null && ruleType.length() > 0)
            sb.append(" RULE_TYPE = '" + ruleType + "' ");

        String detailFlg = TypeTool.getString(getTagValue(getDetailFlg()));
        if (detailFlg != null && detailFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" DETAIL_FLG = '" + detailFlg + "' ");
        }

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
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "�������");
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
        data.add(new TAttribute("RuleType", "String", "", "Left"));
        data.add(new TAttribute("DetailFlg", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("RuleType".equalsIgnoreCase(name)) {
            setRuleType(value);
            getTObject().setValue("RuleType", value);
            return;
        }
        if ("DetailFlg".equalsIgnoreCase(name)) {
            setDetailFlg(value);
            getTObject().setValue("DetailFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
