package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import com.dongyang.util.*;
import jdo.sys.*;

/**
 * <p>Title: �����ٴ�·����������</p>
 *
 * <p>Description: �����ٴ�·����������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2011.05.06
 * @version 1.0
 */
public class TextFormatCLPBscInfo
    extends TTextFormat {
    /**
     * ����
     */
    private String deptCode;
    /**
     * ����ע��
     */
    private String activeFlg;
    /**
     * ����
     */
    private String regionCode;
    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT CLNCPATH_CODE AS ID,CLNCPATH_CHN_DESC AS NAME,CLNCPATH_ENG_DESC AS ENNAME,PY1,PY2 " +
            "   FROM CLP_BSCINFO "+
            "  WHERE 1=1 ";
           // + "ACTIVE_FLG ='Y' ";
        String sql1 = " ORDER BY CLNCPATH_CODE,SEQ ";
        StringBuffer sb = new StringBuffer();
        String deptCodeTemp = TypeTool.getString(getTagValue(
            getDeptCode()));
        if (deptCodeTemp != null && deptCodeTemp.length() > 0) {
            sb.append(" AND DEPT_CODE = '" + deptCodeTemp + "' ");
        }
        String activeFlgTemp = TypeTool.getString(getTagValue(
            getActiveFlg()));
        if (activeFlgTemp != null && activeFlgTemp.length() > 0) {
            sb.append(" AND ACTIVE_FLG = '" + activeFlgTemp + "' ");
        }

        String RegionCodeTemp = TypeTool.getString(getTagValue(getRegionCode()));
        if (RegionCodeTemp != null && RegionCodeTemp.length() > 0) {
            sb.append(" AND REGION_CODE = '" + RegionCodeTemp + "' ");
        }
        String operatorCodeAll = Operator.getRegion();
        if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
            sb.append(" AND REGION_CODE = '" + operatorCodeAll + "' ");
        }
        sql = sql +sb.toString()+ sql1;
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
        object.setValue("Tip", "�����ٴ�·��");
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
        data.add(new TAttribute("ActiveFlg", "String", "", "Left"));
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
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
        if ("ActiveFlg".equalsIgnoreCase(name)) {
            this.setActiveFlg(value);
            getTObject().setValue("ActiveFlg", value);
            return;
        }
        if ("RegionCode".equalsIgnoreCase(name)) {
            this.setRegionCode(value);
            getTObject().setValue("RegionCode", value);
            return;
        }
        super.setAttribute(name, value);
    }

    /**
     * �õ�����
     * @return String
     */
    public String getDeptCode() {
        return deptCode;
    }

    /**
     * �õ�����
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * �õ�����ע��
     * @return String
     */
    public String getActiveFlg() {
        return activeFlg;
    }

    /**
     * ���ÿ���
     * @param deptCode String
     */
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
        setModifySQL(true);
    }

    /**
     * ��������
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * ��������ע��
     * @param activeFlg String
     */
    public void setActiveFlg(String activeFlg) {
        this.activeFlg = activeFlg;
    }
}
