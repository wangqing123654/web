package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;
/**
 * <p>Title: ҽ��ҽԺ��������</p>
 *
 * <p>Description: ҽ��ҽԺ��������:HOSP_TYPE:1 һ��ҽԺ 2.����ҽԺ 3.����ҽԺ 4.����ר��ҽԺ 5.����ҩ��</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author pangb 2012.04.10
 * @version 4.0
 */
public class TextFormatINSHospital extends TTextFormat{
	private String hospType;
	 public String getHospType() {
		return hospType;
	}
	public void setHospType(String hospType) {
		this.hospType = hospType;
		setModifySQL(true);
	}
	/**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT HOSP_CODE AS ID,HOSP_DESC AS NAME FROM INS_HOSP_LIST ";
        String sql1 = " ORDER BY HOSP_CODE";
        String hospType = TypeTool.getString(getTagValue(getHospType()));
        if (null!=hospType &&hospType.length()>0) {
        	sql+=" WHERE HOSP_TYPE='"+hospType+"' ";
		}
        sql+=sql1;
        return sql;
    }
    /**
     * �½�����ĳ�ʼֵ
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null)
            return;
        object.setValue("Width", "150");
        object.setValue("Height", "23");
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "����,100;����,100");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "ҽ��ҽԺ");
        object.setValue("ShowColumnList", "ID;NAME");
    }

    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,2");
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
        data.add(new TAttribute("hospType", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
    	if ("hospType".equalsIgnoreCase(name)) {
    		setHospType(value);
            getTObject().setValue("hospType", value);
            return;
        }
        super.setAttribute(name, value);
    }
   
}
