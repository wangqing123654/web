package com.javahis.system.textFormat;

import com.dongyang.ui.*;
import com.dongyang.util.TypeTool;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
import jdo.sys.Operator;

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
public class TextFormatRegClinicRoom extends TTextFormat {
    /**
     * �ż�ס��
     */
    private String admType;
    /**
     * ����
     */
    private String clinicArea;
    /**
     * λ��
     */
    private String locationCode;
    /**
     * Ԥ��ҩ��
     */
    private String orgCode;
    /**
     * ����ע��
     */
    private String activeFlg;

    public String getActiveFlg() {
        return activeFlg;
    }

    public String getAdmType() {
        return admType;
    }

    public String getClinicArea() {
        return clinicArea;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setActiveFlg(String activeFlg) {
        this.activeFlg = activeFlg;
    }

    public void setAdmType(String admType) {
        this.admType = admType;
    }

    public void setClinicArea(String clinicArea) {
        this.clinicArea = clinicArea;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }
    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =" SELECT CLINICROOM_NO AS ID,CLINICROOM_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 FROM REG_CLINICROOM ";
        String sql1 = " ORDER BY CLINICROOM_NO,SEQ";
        StringBuffer sb = new StringBuffer();
        String admTypeTemp = TypeTool.getString(getTagValue(getAdmType()));
        if (admTypeTemp != null && admTypeTemp.length() > 0)
            sb.append(" ADM_TYPE = '" + admTypeTemp + "' ");

        String clinicAreaTemp = TypeTool.getString(getTagValue(getClinicArea()));
        if (clinicAreaTemp != null && clinicAreaTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" CLINICAREA_CODE = '"+clinicAreaTemp+"' ");
        }

        String locationCodeTemp = TypeTool.getString(getTagValue(getLocationCode()));
        if (locationCodeTemp != null && locationCodeTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" LOCATION = '" + locationCodeTemp + "' ");
        }

        String orgCodeTemp = TypeTool.getString(getTagValue(getOrgCode()));
        if (orgCodeTemp != null && orgCodeTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" ORG_CODE = '" + orgCodeTemp + "' ");
        }

        String activeFlgTemp = TypeTool.getString(getTagValue(getActiveFlg()));
        if (activeFlgTemp != null && activeFlgTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" ACTIVE_FLG = '" + activeFlgTemp + "' ");
        }
        //=============pangben modify 20110420 start ��Ӻű�����ɸѡ
              String operatorCodeAll = Operator.getRegion();
              if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
                  if (sb.length() > 0)
                      sb.append(" AND ");
                  sb.append(" REGION_CODE = '" + operatorCodeAll + "' ");
              }
       //=============pangben modify 20110420 stop

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
        object.setValue("Width","81");
        object.setValue("Height","23");
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
        object.setValue("Tip", "���");
        object.setValue("ShowColumnList", "ID;NAME");

    }
    public void onInit()
    {
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
        data.add(new TAttribute("AdmType", "String", "", "Left"));
        data.add(new TAttribute("ClinicArea", "String", "", "Left"));
        data.add(new TAttribute("LocationCode", "String", "", "Left"));
        data.add(new TAttribute("OrgCode", "String", "", "Left"));
        data.add(new TAttribute("ActiveFlg", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("AdmType".equalsIgnoreCase(name)) {
            setAdmType(value);
            getTObject().setValue("AdmType", value);
            return;
        }
        if ("ClinicArea".equalsIgnoreCase(name)) {
            setClinicArea(value);
            getTObject().setValue("ClinicArea", value);
            return;
        }
        if ("LocationCode".equalsIgnoreCase(name)) {
            setLocationCode(value);
            getTObject().setValue("LocationCode", value);
            return;
        }
        if ("OrgCode".equalsIgnoreCase(name)) {
            setOrgCode(value);
            getTObject().setValue("OrgCode", value);
            return;
        }
        if ("ActiveFlg".equalsIgnoreCase(name)) {
            setActiveFlg(value);
            getTObject().setValue("ActiveFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }

}
