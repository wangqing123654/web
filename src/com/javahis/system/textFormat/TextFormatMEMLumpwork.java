package com.javahis.system.textFormat;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
* <p>Title: �����ײ���������</p>
 *
 * <p>Description: �����ײ���������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 * @author liling 20140509
 *
 */
public class TextFormatMEMLumpwork extends TTextFormat {
	/**
	 * �ײ��ż�ס����("O"---����;"E"---����;"I"---סԺ;"H"---����)
	 */
	private String admType;
	public String getAdmType() {
		return admType;
	}

	public void setAdmType(String admType) {
		this.admType = admType;
	}

	/**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
//    	Timestamp sysDate = StringTool.getTimestamp(StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd HH:mm:ss"), "yyyy/MM/dd HH:mm:ss");
//    	//Timestamp sysDate = SystemTool.getInstance().getDate();
//    	System.out.println("sysdate==>"+StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd HH:mm:ss"));
    	//String sysDate=StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd HH:mm:ss");
    	String sql = " SELECT PACKAGE_CODE AS ID, PACKAGE_DESC AS NAME, PY1, PY2 FROM MEM_PACKAGE " ;
    	 StringBuffer sb = new StringBuffer();
    	String admType = TypeTool.getString(getTagValue(getAdmType()));
        if (admType != null && admType.length() > 0) {
            sb.append(" WHERE ADM_TYPE = '" + admType + "' ");
        }
        if (sb.length() > 0)
            sql += sb.toString();
//    	System.out.println("sql===>"+sql);
    return sql;
    }

    /**
     * �½�����ĳ�ʼֵ
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null)
            return;
        object.setValue("Width", "100");
        object.setValue("Height", "23");
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "����,43;����,395");
        object.setValue("PopupMenuWidth", "519");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "�����ײ�");
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

        return "����,43;����,395";
    }

    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        data.add(new TAttribute("AdmType", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        super.setAttribute(name, value);
        if ("AdmType".equalsIgnoreCase(name)) {
    		setAdmType(value);
            getTObject().setValue("AdmType", value);
            return;
        }
        super.setAttribute(name, value);
    }

}
