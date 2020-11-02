package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;
import jdo.sys.Operator;

/**
 * <p>Title: ������������</p>
 *
 * <p>Description: ������������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.27
 * @version 1.0
 */
public class TextFormatSYSStation
    extends TTextFormat {
    /**
     * ����
     */
    private String regionCode;
    /**
     * ����
     */
    private String deptCode;
    /**
     * Ԥ��ҩ��
     */
    private String orgCode;
    /**
     * λ��
     */
    private String locCode;
    /**
     * �õ�����
     * @return String
     */
    public String getDeptCode() {
        return deptCode;
    }

    /**
     * �õ�λ��
     * @return String
     */
    public String getLocCode() {
        return locCode;
    }

    /**
     * �õ�ҩ��
     * @return String
     */
    public String getOrgCode() {
        return orgCode;
    }

    /**
     * �õ�����
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * ���ÿ���
     * @param deptCode String
     */
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    /**
     * ����λ��
     * @param locCode String
     */
    public void setLocCode(String locCode) {
        this.locCode = locCode;
    }

    /**
     * ����ҩ��
     * @param orgCode String
     */
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    /**
     * ��������
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT DISTINCT(A.STATION_CODE) AS ID,A.STATION_DESC AS NAME,A.ENG_DESC AS ENNAME,A.PY1,A.PY2 "+
            "   FROM SYS_STATION A ,SYS_STADEP_LIST B "+
            "  WHERE A.STATION_CODE = B.STATION_CODE(+) ";
        String sql1 = " ORDER BY A.STATION_CODE ";

        StringBuffer sb = new StringBuffer();

        String regionCode = TypeTool.getString(getTagValue(getRegionCode()));
        if (regionCode != null && regionCode.length() > 0)
            sb.append(" AND A.REGION_CODE = '" + regionCode + "' ");

        String deptCode = TypeTool.getString(getTagValue(getDeptCode()));
        if (deptCode != null && deptCode.length() > 0) {
            sb.append(" AND B.DEPT_CODE = '" + deptCode + "' ");
        }

        String orgCode = TypeTool.getString(getTagValue(getOrgCode()));
        if (orgCode != null && orgCode.length() > 0) {
            sb.append(" AND A.ORG_CODE = '" + orgCode + "' ");
        }

        String locCode = TypeTool.getString(getTagValue(getLocCode()));
        if (locCode != null && locCode.length() > 0) {
            sb.append(" AND A.LOC_CODE = '" + locCode + "' ");
        }
        //=============pangben modify 20110420 start ��Ӻű�����ɸѡ
       String operatorCodeAll = Operator.getRegion();
       if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
           sb.append(" AND A.REGION_CODE = '" + operatorCodeAll + "' ");
       }
       //=============pangben modify 20110420 stop

        if (sb.length() > 0)
            sql +=  sb.toString() + sql1;
        else
            sql = sql + sql1;
       // System.out.println("��������sql����������"+sql);
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
        object.setValue("Tip", "����");
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
        data.add(new TAttribute("DeptCode", "String", "", "Left"));
        data.add(new TAttribute("OrgCode", "String", "", "Left"));
        data.add(new TAttribute("LocCode", "String", "", "Left"));
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
        if ("DeptCode".equalsIgnoreCase(name)) {
            setDeptCode(value);
            getTObject().setValue("DeptCode", value);
            return;
        }
        if ("OrgCode".equalsIgnoreCase(name)) {
            setOrgCode(value);
            getTObject().setValue("OrgCode", value);
            return;
        }
        if ("LocCode".equalsIgnoreCase(name)) {
            setLocCode(value);
            getTObject().setValue("LocCode", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
