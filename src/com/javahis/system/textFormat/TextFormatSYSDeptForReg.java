package com.javahis.system.textFormat;

import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.TTextFormat;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TCM_Transform;
import java.sql.Timestamp;
import jdo.sys.Operator;

/**
 * <p>Title: �������ÿ�����������</p>
 *
 * <p>Description: �������ÿ�����������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.24
 * @version 1.0
 */
public class TextFormatSYSDeptForReg
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
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT DISTINCT A.DEPT_CODE AS ID,B.DEPT_ABS_DESC AS NAME,DEPT_ENG_DESC AS ENNAME, B.PY1 AS PY1, B.PY2 AS PY2 " +
            "   FROM REG_SCHDAY A,SYS_DEPT B " +
            "  WHERE B.ACTIVE_FLG='Y' " +
            "    AND A.DEPT_CODE = B.DEPT_CODE ";
        String sql1 = " ORDER BY A.DEPT_CODE ";

        StringBuffer sb = new StringBuffer();

        String regionCode = TypeTool.getString(getTagValue(getRegionCode()));
        if (regionCode != null && regionCode.length() > 0)
            sb.append(" AND A.REGION_CODE = '" + regionCode + "' ");

        String admType = TypeTool.getString(getTagValue(getAdmType()));
        if (admType != null && admType.length() > 0) {
            sb.append(" AND A.ADM_TYPE = '" + admType + "' ");
        }
        Object value = getTagValue(getAdmDate());
        String admDate = "";
        if (value instanceof Timestamp)
            admDate = StringTool.getString(TCM_Transform.getTimestamp(value),
                                           "yyyyMMdd");
        else
            admDate = "" + value;

        if (admDate != null && admDate.length() > 0) {
            sb.append(" AND A.ADM_DATE = '" + admDate + "' ");
        }

        String sessionCode = TypeTool.getString(getTagValue(getSessionCode()));
        if (sessionCode != null && sessionCode.length() > 0) {
            sb.append(" AND A.SESSION_CODE = '" + sessionCode + "' ");
        }
        String operatorCodeAll = Operator.getRegion();
        if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
            sb.append(" AND B.REGION_CODE = '" + operatorCodeAll + "' ");
        }

        if (sb.length() > 0)
            sql += sb.toString() + sql1;
        else
            sql = sql + sql1;
//        System.out.println("����sql"+sql);
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
        object.setValue("Tip", "�������ÿ���");
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
        super.setAttribute(name, value);
    }
}
