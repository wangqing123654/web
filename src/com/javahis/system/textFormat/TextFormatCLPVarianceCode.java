package com.javahis.system.textFormat;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;
/**
 * <p>Title: �ٴ�·������ԭ��</p>
 *
 * <p>Description: �ٴ�·������ԭ��</p>
 *
 * <p>Copyright: Copyright (c) pangben 2015</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author pangben 20151020
 * @version 1.0
 */
public class TextFormatCLPVarianceCode extends TTextFormat {

	private String moncatCode;

	public String getMoncatCode() {
		return moncatCode;
	}

	public void setMoncatCode(String moncatCode) {
		this.moncatCode = moncatCode;
	}
	 /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT VARIANCE_CODE AS ID , VARIANCE_CHN_DESC AS NAME ," +
            " VARIANCE_ENG_DESC AS ENNAME,PY1  FROM CLP_VARIANCE ";
        //StringBuffer sb = new StringBuffer();
        String moncatCode = TypeTool.getString(getTagValue(getMoncatCode()));
        if (moncatCode != null && moncatCode.length() > 0)
        	sql+=" WHERE MONCAT_CODE = '" + moncatCode + "' ";
        //System.out.println("sbDDFDF::::::"+sql);
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
        object.setValue("Tip", "����ԭ��");
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
        data.add(new TAttribute("moncatCode", "String", "", "Left"));
    }
    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("moncatCode".equalsIgnoreCase(name)) {
            setMoncatCode(value);
            getTObject().setValue("moncatCode", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
