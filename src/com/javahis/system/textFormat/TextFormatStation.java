package com.javahis.system.textFormat;

import com.dongyang.ui.*;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TextFormatStation
    extends TTextFormat {
    /**
     * ���ÿ���
     */
    private String deptCode;
    /**
     * Ԥ��ҩ��
     */
    private String orgCode;
    public String getDeptCode() {
        return deptCode;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT STATION_CODE AS ID,STATION_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_STATION";
        String sql1 = " ORDER BY STATION_CODE,SEQ";
        StringBuffer sb = new StringBuffer();
        String deptCodeTemp = TypeTool.getString(getTagValue(getDeptCode()));
        if (deptCodeTemp != null && deptCodeTemp.length() > 0)
            sb.append(" DEPT_CODE = '" + deptCodeTemp + "' ");

        String orgCodeTemp = TypeTool.getString(getTagValue(getOrgCode()));
        if (orgCodeTemp != null && orgCodeTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" ORG_CODE = '" + orgCodeTemp + "' ");
        }

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
//        object.setValue("ShowColumnList", "NAME");
//        object.setValue("ValueColumn", "ID");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "����");
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
        data.add(new TAttribute("DeptCode", "String", "", "Left"));
        data.add(new TAttribute("OrgCode", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("DeptCode".equalsIgnoreCase(name)) {
            setDeptCode(value);
            getTObject().setValue("DeptCode", value);
            return;
        }
        if ("OrgCode".equalsIgnoreCase(name)) {
            setOrgCode(value);
            getTObject().setValue("OrgCode", value);
            return;
        }
        super.setAttribute(name, value);
    }

}
