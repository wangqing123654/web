package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: ������λ��������</p>
 *
 * <p>Description: ������λ��������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.27
 * @version 1.0
 */
public class TextFormatSYSUnit
    extends TTextFormat {
    /**
     * ҩƷ������λע��
     */
    private String mediFlg;
    /**
     * ���õ�λע��
     */
    private String disposeFlg;
    /**
     * ����װע��
     */
    private String packageFlg;
    /**
     * ����ע��
     */
    private String otherFlg;
    /**
     * ����ҩƷ������λע��
     * @param mediFlg String
     */
    public void setMediFlg(String mediFlg) {
        this.mediFlg = mediFlg;
    }

    /**
     * �õ�ҩƷ������λע��
     * @return String
     */
    public String getMediFlg() {
        return mediFlg;
    }

    /**
     * ���ô��õ�λע��
     * @param disposeFlg String
     */
    public void setDisposeFlg(String disposeFlg) {
        this.disposeFlg = disposeFlg;
    }

    /**
     * �õ����õ�λע��
     * @return String
     */
    public String getDisposeFlg() {
        return disposeFlg;
    }

    /**
     * ��������װע��
     * @param packageFlg String
     */
    public void setPackageFlg(String packageFlg) {
        this.packageFlg = packageFlg;
    }

    /**
     * �õ�����װע��
     * @return String
     */
    public String getPackageFlg() {
        return packageFlg;
    }

    /**
     * ��������ע��
     * @param otherFlg String
     */
    public void setOtherFlg(String otherFlg) {
        this.otherFlg = otherFlg;
    }

    /**
     * �õ�����ע��
     * @return String
     */
    public String getOtherFlg() {
        return otherFlg;
    }

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT UNIT_CODE AS ID,UNIT_CHN_DESC AS NAME,UNIT_ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_UNIT ";
        String sql1 = " ORDER BY UNIT_CODE ";

        StringBuffer sb = new StringBuffer();

        String mediFlg = TypeTool.getString(getTagValue(getMediFlg()));
        if (mediFlg != null && mediFlg.length() > 0)
            sb.append(" MEDI_FLG = '" + mediFlg + "' ");

        String disposeFlg = TypeTool.getString(getTagValue(getDisposeFlg()));
        if (disposeFlg != null && disposeFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" DISPOSE_FLG = '" + disposeFlg + "' ");
        }

        String packageFlg = TypeTool.getString(getTagValue(getPackageFlg()));
        if (packageFlg != null && packageFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" PACKAGE_FLG = '" + packageFlg + "' ");
        }

        String otherFlg = TypeTool.getString(getTagValue(getOtherFlg()));
        if (otherFlg != null && otherFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" OTHER_FLG = '" + otherFlg + "' ");
        }

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
        object.setValue("Tip", "������λ");
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
        data.add(new TAttribute("MediFlg", "String", "", "Left"));
        data.add(new TAttribute("DisposeFlg", "String", "", "Left"));
        data.add(new TAttribute("PackageFlg", "String", "", "Left"));
        data.add(new TAttribute("OtherFlg", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("MediFlg".equalsIgnoreCase(name)) {
            setMediFlg(value);
            getTObject().setValue("MediFlg", value);
            return;
        }
        if ("DisposeFlg".equalsIgnoreCase(name)) {
            setDisposeFlg(value);
            getTObject().setValue("DisposeFlg", value);
            return;
        }
        if ("PackageFlg".equalsIgnoreCase(name)) {
            setPackageFlg(value);
            getTObject().setValue("PackageFlg", value);
            return;
        }
        if ("OtherFlg".equalsIgnoreCase(name)) {
            setOtherFlg(value);
            getTObject().setValue("OtherFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
