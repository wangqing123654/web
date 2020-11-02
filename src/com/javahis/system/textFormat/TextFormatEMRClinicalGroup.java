package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: EMR ����������</p>
 *
 * <p>Description: EMR ����������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: </p>
 *
 * @author li.xiang790130@gmail.com  2011.06.06
 * @version 1.0
 */
public class TextFormatEMRClinicalGroup
    extends TTextFormat {

    /**
     * �ĵ�����
     */
    private String documentType; //


    public TextFormatEMRClinicalGroup() {
    }

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        StringBuffer sb = new StringBuffer();
        //�ĵ���������
        String documentType = TypeTool.getString(getTagValue(getDocumentType()));
        if (documentType != null && documentType.length() > 0) {
           sb.append(" SELECT a.GROUP_CODE AS ID,a.GROUP_DESC AS NAME from EMR_CLINICAL_DATAGROUP a,EMR_XSD b WHERE  a.GROUP_CODe=b.GROUP_CODE AND a.DATA_CODE=b.DATA_CODE");
           sb.append(" AND DOCUMENT_TYPE = '" + documentType + "' ");

       }else{
           sb.append("SELECT GROUP_CODE AS ID,GROUP_DESC AS NAME FROM EMR_CLINICAL_DATAGROUP a");
       }

        String sql1 = " GROUP BY a.GROUP_CODE,a.GROUP_DESC ORDER BY a.GROUP_CODE ";
        sb.append(sql1);
        //System.out.println("=====sql========"+sb.toString());
        return sb.toString();

    }

    /**
     * �½�����ĳ�ʼֵ
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null) {
            return;
        }
        object.setValue("Width", "81");
        object.setValue("Height", "23");
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "����,100;����,100");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "����ģ�����ݷ���");
        object.setValue("ShowColumnList", "ID;NAME");

    }

    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,1");
        setLanguageMap("NAME");
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
        data.add(new TAttribute("DocumentType", "String", "", "Left"));

    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("DocumentType".equalsIgnoreCase(name)) {
            setDocumentType(value);
            getTObject().setValue("DocumentType", value);
            return;
        }
        super.setAttribute(name, value);
    }

    /**
     * �����ĵ�����
     * @param documentType String
     */
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    /**
     * �õ��ĵ�����
     * @param documentType String
     */
    public String getDocumentType() {
        return documentType;
    }


}
