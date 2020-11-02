package com.javahis.system.textFormat;

import java.sql.*;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.manager.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import com.dongyang.util.*;
import jdo.sys.Operator;

/**
 * <p>Title: ��������������������</p>
 *
 * <p>Description: ��������������������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.24
 * @version 1.0
 */
public class TextFormatREGClinicRoomForReg
    extends TTextFormat {
    /**
     * ����
     */
    private String regionCode;
    /**
     * �ż���
     */
    private String admType;
    /**
     * ��������
     */
    private String admDate;
    /**
     * ʱ��
     */
    private String sessionCode;
    /**
     * ��������
     */
    private String phlRegionCode;
    /**
     * ��������
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {

        this.regionCode = regionCode;
        setModifySQL(true);
    }

    /**
     * �����ż���
     * @param admType String
     */
    public void setAdmType(String admType) {
        this.admType = admType;
        setModifySQL(true);
    }

    /**
     * �õ�����
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }


    /**
     * �õ��ż���
     * @return String
     */
    public String getAdmType() {
        return admType;
    }

    /**
     * ����ʱ��
     * @param sessionCode String
     */
    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
        setModifySQL(true);
    }

    /**
     * ���þ�������
     * @param admDate String
     */
    public void setAdmDate(String admDate) {
        this.admDate = admDate;
        setModifySQL(true);
    }

    /**
     * ���þ�������
     * @param phlRegionCode String
     */
    public void setPhlRegionCode(String phlRegionCode) {
        this.phlRegionCode = phlRegionCode;
    }


    /**
     * �õ���������
     * @return String
     */
    public String getAdmDate() {
        return admDate;
    }


    /**
     * �õ�ʱ��
     * @return String
     */
    public String getSessionCode() {
        return sessionCode;
    }

    /**
     * �õ���������
     * @return String
     */
    public String getPhlRegionCode() {
        return phlRegionCode;
    }

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT DISTINCT A.CLINICROOM_NO AS ID,B.CLINICROOM_DESC AS NAME,B.ENG_DESC AS ENNAME,B.PY1 AS PY1, B.PY2 AS PY2  " +
            "   FROM REG_SCHDAY A,REG_CLINICROOM B  " +
            "  WHERE B.ACTIVE_FLG = 'Y' " +
            "    AND A.CLINICROOM_NO = B.CLINICROOM_NO ";
        String sql1 = " ORDER BY A.CLINICROOM_NO ";

        StringBuffer sb = new StringBuffer();

        String regionCode = TypeTool.getString(getTagValue(getRegionCode()));
        if (regionCode != null && regionCode.length() > 0)
            sb.append(" AND A.REGION_CODE = '" + regionCode + "' ");

        String admType = TypeTool.getString(getTagValue(getAdmType()));
        if (admType != null && admType.length() > 0) {
            sb.append(" AND A.ADM_TYPE = '" + admType + "' ");
        }
        Object value = getTagValue(getAdmDate());
        if (value instanceof Timestamp)
            value = StringTool.getString(TCM_Transform.getTimestamp(value),
                                         "yyyyMMdd");
        else
            value = getTagValue(getAdmDate());

        String admDate = TypeTool.getString(value);
        if (admDate != null && admDate.length() > 0) {
            sb.append(" AND A.ADM_DATE = '" + admDate + "' ");
        }

        String sessionCode = TypeTool.getString(getTagValue(getSessionCode()));
        if (sessionCode != null && sessionCode.length() > 0) {
            sb.append(" AND A.SESSION_CODE = '" + sessionCode + "' ");
        }
        String phlRegionCode = TypeTool.getString(getTagValue(getPhlRegionCode()));
        if (phlRegionCode != null && phlRegionCode.length() > 0) {
            sb.append(" AND B.PHL_REGION_CODE = '" + phlRegionCode + "' ");
        }
        //=============pangben modify 20110420 start ��Ӻű�����ɸѡ
               String operatorCodeAll = Operator.getRegion();
               if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
//                   if (sb.length() > 0)
//                       sb.append(" AND ");
                   sb.append(" AND B.REGION_CODE = '" + operatorCodeAll + "' ");
               }
       //=============pangben modify 20110420 stop
        if (sb.length() > 0)
            sql += sb.toString() + sql1;
        else
            sql = sql + sql1;
       // System.out.println("sql1:::@"+ sql);
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
        object.setValue("Tip", "������������");
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
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
        data.add(new TAttribute("AdmType", "String", "", "Left"));
        data.add(new TAttribute("AdmDate", "String", "", "Left"));
        data.add(new TAttribute("SessionCode", "String", "", "Left"));
        data.add(new TAttribute("PhlRegionCode", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("RegionCode".equalsIgnoreCase(name)) {
            setRegionCode(value);
            getTObject().setValue("RegionCode", value);
            return;
        }
        if ("AdmType".equalsIgnoreCase(name)) {
            setAdmType(value);
            getTObject().setValue("AdmType", value);
            return;
        }
        if ("AdmDate".equalsIgnoreCase(name)) {
            setAdmDate(value);
            getTObject().setValue("AdmDate", value);
            return;
        }
        if ("SessionCode".equalsIgnoreCase(name)) {
            setSessionCode(value);
            getTObject().setValue("SessionCode", value);
            return;
        }
        if ("PhlRegionCode".equalsIgnoreCase(name)) {
            setPhlRegionCode(value);
            getTObject().setValue("PhlRegionCode", value);
            return;
        }

        super.setAttribute(name, value);
    }
}
