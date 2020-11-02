package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: ������λ�����б�</p>
 *
 * <p>Description: ������λ�����б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2010.01.23
 * @version 1.0
 */
public class TextFormatINVMaterialloc
    extends TTextFormat {
    /**
     * ҩ������
     */
    private String orgCode;
    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT MATERIAL_LOC_CODE AS ID,DESCRIPTION AS NAME,ENNAME,PY1,PY2 " +
            "   FROM INV_MATERIALLOC ";
        String sql1 = " ORDER BY MATERIAL_LOC_CODE ";
        StringBuffer sb = new StringBuffer();
        String orgCode = TypeTool.getString(getTagValue(getOrgCode()));
        if (orgCode != null && orgCode.length() > 0)
            sb.append(" ORG_CODE = '" + orgCode + "' ");
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
        object.setValue("Tip", "������λ");
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
        data.add(new TAttribute("OrgCode", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("OrgCode".equalsIgnoreCase(name)) {
            setOrgCode(value);
            getTObject().setValue("OrgCode", value);
            return;
        }
        super.setAttribute(name, value);
    }

    /**
     * �õ�ҩ������
     * @return String
     */
    public String getOrgCode() {
        return orgCode;
    }

    /**
     * ����ҩ������
     * @param orgCode String
     */
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
}
