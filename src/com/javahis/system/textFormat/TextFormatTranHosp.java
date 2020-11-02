package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import jdo.sys.Operator;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: ��תԺ����������</p>
 *
 * <p>Description: ��תԺ����������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author shibl 2012.01.11
 * @version 1.0
 */
public class TextFormatTranHosp
    extends TTextFormat {
    /**
     * Ժ������
     */
    private String hospType;

	/**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT HOSP_CODE AS ID,HOSP_DESC AS NAME,PY1,PY2  FROM SYS_TRN_HOSP ";
        String sql1 = " ORDER BY HOSP_TYPE,SEQ ";
        StringBuffer sb = new StringBuffer();
        String hospType = TypeTool.getString(getTagValue(this.getHospType()));
        if (hospType != null && hospType.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" HOSP_TYPE = '" + hospType + "' ");
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
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "��תԺ��");
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
        data.add(new TAttribute("HospType", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("HospType".equalsIgnoreCase(name)) {
            this.setHospType(value);
            getTObject().setValue("HospType", value);
            return;
        }
        super.setAttribute(name, value);
    }

    /**
     * �õ�Ժ������
	 * @return the hospType
	 */
	public String getHospType() {
		return hospType;
	}

	/**
	 * �õ�Ժ������
	 * @param hospType the hospType to set
	 */
	public void setHospType(String hospType) {
		this.hospType = hospType;
	}
}
