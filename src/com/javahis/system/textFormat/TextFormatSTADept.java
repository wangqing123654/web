package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: ���տ�����������</p>
 *
 * <p>Description: ���տ�����������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.26
 * @version 1.0
 */
public class TextFormatSTADept
    extends TTextFormat {
    /**
     * �ȼ�
     */
    private String deptLevel;
    /**
     * ���õȼ�
     * @param deptLevel String
     */
    public void setDeptLevel(String deptLevel) {

        this.deptLevel = deptLevel;
        setModifySQL(true);
    }

    /**
     * �õ��ȼ�
     * @return String
     */
    public String getDeptLevel() {
        return deptLevel;
    }

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT DEPT_CODE AS ID,DEPT_DESC AS NAME,ENNAME,OE_DEPT_CODE AS PY1,IPD_DEPT_CODE AS PY2 " +
            "   FROM STA_OEI_DEPT_LIST ";
        String sql1 = " ORDER BY DEPT_CODE ";

        StringBuffer sb = new StringBuffer();

        String deptLevel = TypeTool.getString(getTagValue(getDeptLevel()));
        if (deptLevel != null && deptLevel.length() > 0)
            sb.append(" DEPT_LEVEL = '" + deptLevel + "' ");

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
        object.setValue("Tip", "���տ���");
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
        data.add(new TAttribute("DeptLevel", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("DeptLevel".equalsIgnoreCase(name)) {
            setDeptLevel(value);
            getTObject().setValue("DeptLevel", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
