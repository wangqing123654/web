package com.javahis.system.textFormat;

import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.TTextFormat;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;

/**
 * <p>Title: �ű���������</p>
 *
 * <p>Description: �ű���������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.24
 * @version 1.0
 */
public class TextFormatREGClinicType
    extends TTextFormat {
    /**
     * �ż���
     */
    private String admType;
    /**
     * ר����ע��
     */
    private String profFlg;
    /**
     * �õ��ż���
     * @return String
     */
    public String getAdmType() {
        return admType;
    }

    /**
     * �õ�ר����ע��
     * @return String
     */
    public String getProfFlg() {
        return profFlg;
    }

    /**
     * �����ż���
     * @param admType String
     */
    public void setAdmType(String admType) {
        this.admType = admType;
    }

    /**
     * ����ר����ע��
     * @param profFlg String
     */
    public void setProfFlg(String profFlg) {
        this.profFlg = profFlg;
    }

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT CLINICTYPE_CODE AS ID,CLINICTYPE_DESC AS NAME ,PY1,PY2 " +
            "   FROM REG_CLINICTYPE ";

        String sqlEnd = " ORDER BY CLINICTYPE_CODE,SEQ ";
        StringBuffer sb = new StringBuffer();
        String admType = TypeTool.getString(getTagValue(getAdmType()));
        if (admType != null && admType.length() > 0)
            sb.append(" ADM_TYPE = '" + admType + "' ");

        String profFlg = TypeTool.getString(getTagValue(getProfFlg()));
        if (profFlg != null && profFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" PROF_FLG = '" + profFlg + "' ");
        }

        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sqlEnd;
        else
            sql = sql + sqlEnd;
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
        object.setValue("Tip", "�ű�");
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
        data.add(new TAttribute("AdmType", "String", "", "Left"));
        data.add(new TAttribute("ProfFlg", "String", "", "Left"));
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
        if ("ProfFlg".equalsIgnoreCase(name)) {
            setProfFlg(value);
            getTObject().setValue("ProfFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }

}
