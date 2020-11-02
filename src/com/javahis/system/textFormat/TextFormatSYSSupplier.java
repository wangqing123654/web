package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: ��Ӧ������������</p>
 *
 * <p>Description: ��Ӧ������������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.09.11
 * @version 1.0
 */
public class TextFormatSYSSupplier
    extends TTextFormat {
    /**
     * ֹͣ�ɹ�ע��
     */
    private String supStopFlg;
    /**
     * ҩƷ������ע��
     */
    private String phaFlg;
    /**
     * ��ֵ�ĲĹ�����ע��
     */
    private String matFlg;
    /**
     * �豸������ע��
     */
    private String devFlg;
    /**
     * ������Ӧ����ע��
     */
    private String otherFlg;
    /**
     * �õ��豸������ע��
     * @return String
     */
    public String getDevFlg() {
        return devFlg;
    }

    /**
     * �õ���ֵ�ĲĹ�����ע��
     * @return String
     */
    public String getMatFlg() {
        return matFlg;
    }

    /**
     * �õ�������Ӧ����ע��
     * @return String
     */
    public String getOtherFlg() {
        return otherFlg;
    }

    /**
     * �õ�ҩƷ������ע��
     * @return String
     */
    public String getPhaFlg() {
        return phaFlg;
    }

    /**
     * �õ�ֹͣ�ɹ�ע��
     * @return String
     */
    public String getSupStopFlg() {
        return supStopFlg;
    }

    /**
     * �����豸������ע��
     * @param devFlg String
     */
    public void setDevFlg(String devFlg) {
        this.devFlg = devFlg;
        setModifySQL(true);
    }

    /**
     * ���õ�ֵ�ĲĹ�����ע��
     * @param matFlg String
     */
    public void setMatFlg(String matFlg) {
        this.matFlg = matFlg;
        setModifySQL(true);
    }

    /**
     * ����������Ӧ����ע��
     * @param otherFlg String
     */
    public void setOtherFlg(String otherFlg) {
        this.otherFlg = otherFlg;
        setModifySQL(true);
    }

    /**
     * ����ҩƷ������ע��
     * @param phaFlg String
     */
    public void setPhaFlg(String phaFlg) {
        this.phaFlg = phaFlg;
        setModifySQL(true);
    }

    /**
     * ����ֹͣ�ɹ�ע��
     * @param supStopFlg String
     */
    public void setSupStopFlg(String supStopFlg) {
        this.supStopFlg = supStopFlg;
        setModifySQL(true);
    }

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT SUP_CODE AS ID,SUP_ABS_DESC AS NAME,SUP_ENG_DESC AS ENNAME,PY1,PY2 " +
            "   FROM SYS_SUPPLIER";
        String sql1 = " ORDER BY SEQ,SUP_CODE";

        StringBuffer sb = new StringBuffer();

        String supStopFlg = TypeTool.getString(getTagValue(getSupStopFlg()));
        if (supStopFlg != null && supStopFlg.length() > 0)
            sb.append(" SUP_STOP_FLG = '" + supStopFlg + "' ");

        String phaFlg = TypeTool.getString(getTagValue(getPhaFlg()));
        if (phaFlg != null && phaFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" PHA_FLG = '" + phaFlg + "' ");
        }

        String matFlg = TypeTool.getString(getTagValue(getMatFlg()));
        if (matFlg != null && matFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" MAT_FLG = '" + matFlg + "' ");
        }

        String devFlg = TypeTool.getString(getTagValue(getDevFlg()));
        if (devFlg != null && devFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" DEV_FLG = '" + devFlg + "' ");
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
//        System.out.println("sql" + sql);
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
        object.setValue("Tip", "��Ӧ����");
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
        data.add(new TAttribute("SupStopFlg", "String", "", "Left"));
        data.add(new TAttribute("PhaFlg", "String", "", "Left"));
        data.add(new TAttribute("MatFlg", "String", "", "Left"));
        data.add(new TAttribute("DevFlg", "String", "", "Left"));
        data.add(new TAttribute("OtherFlg", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("SupStopFlg".equalsIgnoreCase(name)) {
            setSupStopFlg(value);
            getTObject().setValue("SupStopFlg", value);
            return;
        }
        if ("PhaFlg".equalsIgnoreCase(name)) {
            setPhaFlg(value);
            getTObject().setValue("PhaFlg", value);
            return;
        }
        if ("MatFlg".equalsIgnoreCase(name)) {
            setMatFlg(value);
            getTObject().setValue("MatFlg", value);
            return;
        }
        if ("DevFlg".equalsIgnoreCase(name)) {
            setDevFlg(value);
            getTObject().setValue("DevFlg", value);
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
