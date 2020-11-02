package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import com.dongyang.util.*;

/**
 * <p>Title: �ṹ����������ԭ����������</p>
 *
 * <p>Description: �ṹ����������ԭ����������</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TextFormatEMRClinicalDatagroup
    extends TTextFormat {
    /**
     * Ԫ�����
     */
    private String componentType;
    /**
     * ����Ԫ������
     */
    private String groupCode; //GROUP_CODE

    /**
     * �ĵ�����
     */
    private String documentType;

    /**
     * ִ��Module����
     */
    public String getPopupMenuSQL() {
        /**String sql =
            " SELECT DATA_CODE AS ID,DATA_DESC AS NAME,PY1,PY2 FROM EMR_CLINICAL_DATAGROUP ";

        String sql1 = " ORDER BY DATA_CODE ";
        StringBuffer sb = new StringBuffer();
        String componentTypeTemp = TypeTool.getString(getTagValue(
            getComponentType()));
        if (componentTypeTemp != null && componentTypeTemp.length() > 0)
            sb.append(" COMPONENT_TYPE = '" + componentTypeTemp + "' ");
        String groupCodeTemp = TypeTool.getString(getTagValue(getGroupCode()));
        if (groupCodeTemp != null && groupCodeTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" GROUP_CODE = '" + groupCodeTemp + "' ");
        }

        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;*/
        StringBuffer sb = new StringBuffer();
       //�ĵ���������
       String documentType = TypeTool.getString(getTagValue(getDocumentType()));
       //�����ĵ��������
       if (documentType != null && documentType.length() > 0) {
           sb.append("SELECT a.DATA_CODE AS ID,a.DATA_DESC AS NAME,PY1,PY2 FROM EMR_CLINICAL_DATAGROUP a,EMR_XSD b");
           sb.append(" WHERE a.GROUP_CODE=b.GROUP_CODE and a.DATA_CODE=b.DATA_CODE");
           sb.append(" AND DOCUMENT_TYPE = '" + documentType + "' ");
       //û�ĵ����ͣ���ԭ��һ��ȫ������;
       }else{
           sb.append("SELECT DATA_CODE AS ID,DATA_DESC AS NAME,PY1,PY2 FROM EMR_CLINICAL_DATAGROUP a WHERE 1=1");
       }

       String componentTypeTemp = TypeTool.getString(getTagValue(
            getComponentType()));
       if (componentTypeTemp != null && componentTypeTemp.length() > 0){
           sb.append(" AND a.COMPONENT_TYPE = '" + componentTypeTemp + "' ");

       }

       String groupCodeTemp = TypeTool.getString(getTagValue(getGroupCode()));
        if (groupCodeTemp != null && groupCodeTemp.length() > 0) {
            sb.append(" AND a.GROUP_CODE = '" + groupCodeTemp + "' ");
        }

        sb.append(" ORDER BY a.DATA_CODE");
        //System.out.println("==========sql TextFormatEMRClinicalDatagroup==========="+sb.toString());
        return sb.toString();
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
        object.setValue("Tip", "�ṹ����������ԭ��");
        object.setValue("ShowColumnList", "ID;NAME");

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
        data.add(new TAttribute("ComponentType", "String", "", "Left"));
        data.add(new TAttribute("GroupCode", "String", "", "Left"));
        data.add(new TAttribute("DocumentType", "String", "", "Left"));

    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("ComponentType".equalsIgnoreCase(name)) {
            setComponentType(value);
            getTObject().setValue("ComponentType", value);
            return;
        }
        if ("GroupCode".equalsIgnoreCase(name)) {
            setGroupCode(value);
            getTObject().setValue("GroupCode", value);
            return;
        }

        if ("DocumentType".equalsIgnoreCase(name)) {
           setDocumentType(value);
           getTObject().setValue("DocumentType", value);
           return;
       }

        super.setAttribute(name, value);
    }

    /**
     * �õ�Ԫ�����
     * @return String
     */
    public String getComponentType() {
        return componentType;
    }

    /**
     * �õ�����Ԫ������
     * @return String
     */
    public String getGroupCode() {
        return groupCode;
    }

    /**
     * ����Ԫ�����
     * @param componentType String
     */
    public void setComponentType(String componentType) {
        this.componentType = componentType;
        setModifySQL(true);
    }

    /**
     * ��������Ԫ������
     * @param groupCode String
     */
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
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
