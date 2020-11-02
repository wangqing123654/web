package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import com.dongyang.util.TypeTool;
/**
 *
 * <p>Title: ��λ�����������</p>
 *
 * <p>Description: ��λ�����������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.06.17
 * @version 1.0
 */
public class TextFormatSYSBedType
    extends TTextFormat {
    /**
     * �����ۿ�ע��
     */
    private String labDiscntFlg;
    /**
     * ����ע��
     */
    private String isolationFlg;
    /**
     * ����ע��
     */
    private String burnFlg;
    /**
     * С����
     */
    private String pediatricFlg;
    /**
     * ���۴�ע��
     */
    private String observationFlg;
    /**
     * ��ֲע��
     */
    private String transplantFlg;
    /**
     * ��֢�໤ע��
     */
    private String icuFlg;
    /**
     * CCUע��
     */
    private String ccuFlg;
    /**
     * ���ض�ע��
     */
    private String bcFlg;


    /**
     * ���ü����ۿ�ע��
     * @param labDiscntFlg String
     */
    public void setLabDiscntFlg(String labDiscntFlg) {

        this.labDiscntFlg = labDiscntFlg;
        setModifySQL(true);
    }
    /**
     * �������ض�ע��
     * @param bcFlg String
     */
    public void setBcFlg(String bcFlg) {
        this.bcFlg = bcFlg;
        setModifySQL(true);
    }
    /**
     * ��������ע��
     * @param burnFlg String
     */
    public void setBurnFlg(String burnFlg) {
        this.burnFlg = burnFlg;
        setModifySQL(true);
    }
    /**
     * ����CCUע��
     * @param ccuFlg String
     */
    public void setCcuFlg(String ccuFlg) {
        this.ccuFlg = ccuFlg;
        setModifySQL(true);
    }
    /**
     * ������֢�໤ע��
     * @param icuFlg String
     */
    public void setIcuFlg(String icuFlg) {
        this.icuFlg = icuFlg;
        setModifySQL(true);
    }
    /**
     * ���ø���ע��
     * @param isolationFlg String
     */
    public void setIsolationFlg(String isolationFlg) {
        this.isolationFlg = isolationFlg;
        setModifySQL(true);
    }
    /**
     * �������۴�ע��
     * @param observationFlg String
     */
    public void setObservationFlg(String observationFlg) {
        this.observationFlg = observationFlg;
        setModifySQL(true);
    }
    /**
     * ����С����
     * @param pediatricFlg String
     */
    public void setPediatricFlg(String pediatricFlg) {
        this.pediatricFlg = pediatricFlg;
        setModifySQL(true);
    }
    /**
     * ������ֲע��
     * @param transplantFlg String
     */
    public void setTransplantFlg(String transplantFlg) {
        this.transplantFlg = transplantFlg;
        setModifySQL(true);
    }

    /**
     * �õ������ۿ�ע��
     * @return String
     */
    public String getLabDiscntFlg() {
        return labDiscntFlg;
    }
    /**
     * �õ����ض�ע��
     * @return String
     */
    public String getBcFlg() {
        return bcFlg;
    }
    /**
     * �õ�����ע��
     * @return String
     */
    public String getBurnFlg() {
        return burnFlg;
    }
    /**
     * �õ�CCUע��
     * @return String
     */
    public String getCcuFlg() {
        return ccuFlg;
    }
    /**
     * �õ���֢�໤ע��
     * @return String
     */
    public String getIcuFlg() {
        return icuFlg;
    }
    /**
     * �õ�����ע��
     * @return String
     */
    public String getIsolationFlg() {
        return isolationFlg;
    }
    /**
     * �õ����۴�ע��
     * @return String
     */
    public String getObservationFlg() {
        return observationFlg;
    }
    /**
     * �õ�С����
     * @return String
     */
    public String getPediatricFlg() {
        return pediatricFlg;
    }
    /**
     * �õ�
     * @return String
     */
    public String getTransplantFlg() {
        return transplantFlg;
    }
    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT BED_TYPE_CODE AS ID,BEDTYPE_DESC AS NAME,ENNAME,PY1,PY2 " +
            "   FROM SYS_BED_TYPE ";
        String sql1 = " ORDER BY BED_TYPE_CODE,SEQ";

        StringBuffer sb = new StringBuffer();

        String labDiscntFlg = TypeTool.getString(getTagValue(getLabDiscntFlg()));
        if (labDiscntFlg != null && labDiscntFlg.length() > 0)
            sb.append(" LAB_DISCNT_FLG = '" + labDiscntFlg + "' ");

        String bcFlg = TypeTool.getString(getTagValue(getBcFlg()));
        if (bcFlg != null && bcFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" BC_FLG = '"+bcFlg+"' ");
        }

        String isolationFlg = TypeTool.getString(getTagValue(getIsolationFlg()));
        if (isolationFlg != null && isolationFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" ISOLATION_FLG = '"+isolationFlg+"' ");
        }

        String burnFlg = TypeTool.getString(getTagValue(getBurnFlg()));
        if (burnFlg != null && burnFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" BURN_FLG = '"+burnFlg+"' ");
        }

        String pediatricFlg = TypeTool.getString(getTagValue(getPediatricFlg()));
        if (pediatricFlg != null && pediatricFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" PEDIATRIC_FLG = '"+pediatricFlg+"' ");
        }

        String observationFlg = TypeTool.getString(getTagValue(getObservationFlg()));
        if (observationFlg != null && observationFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" OBSERVATION_FLG = '"+observationFlg+"' ");
        }

        String transplantFlg = TypeTool.getString(getTagValue(getTransplantFlg()));
        if (transplantFlg != null && transplantFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" TRANSPLANT_FLG = '"+transplantFlg+"' ");
        }

        String icuFlg = TypeTool.getString(getTagValue(getIcuFlg()));
        if (icuFlg != null && icuFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" ICU_FLG = '"+icuFlg+"' ");
        }

        String ccuFlg = TypeTool.getString(getTagValue(getCcuFlg()));
        if (ccuFlg != null && ccuFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" CCU_FLG = '"+ccuFlg+"' ");
        }
        if(sb.length() > 0)
            sql += " WHERE " + sb.toString()+sql1 ;
        else
        sql = sql+sql1;
//        this.setPopupMenuSQL(sql);
//        super.onQuery();
//    System.out.println("sql"+sql);
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
        object.setValue("Tip", "��λ���");
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
        data.add(new TAttribute("ShowColumnList","String","NAME","Left"));
        data.add(new TAttribute("ValueColumn","String","ID","Left"));
        data.add(new TAttribute("LabDiscntFlg", "String", "", "Left"));
        data.add(new TAttribute("IsolationFlg", "String", "", "Left"));
        data.add(new TAttribute("BurnFlg", "String", "", "Left"));
        data.add(new TAttribute("PediatricFlg", "String", "", "Left"));
        data.add(new TAttribute("ObservationFlg", "String", "", "Left"));
        data.add(new TAttribute("TransplantFlg", "String", "", "Left"));
        data.add(new TAttribute("IcuFlg", "String", "", "Left"));
        data.add(new TAttribute("CcuFlg", "String", "", "Left"));
        data.add(new TAttribute("BcFlg", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean","N","Center"));

    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("LabDiscntFlg".equalsIgnoreCase(name)) {
            setLabDiscntFlg(value);
            getTObject().setValue("LabDiscntFlg", value);
            return;
        }
        if ("IsolationFlg".equalsIgnoreCase(name)) {
            setIsolationFlg(value);
            getTObject().setValue("IsolationFlg", value);
            return;
        }
        if ("BcFlg".equalsIgnoreCase(name)) {
            setBcFlg(value);
            getTObject().setValue("BcFlg", value);
            return;
        }
        if ("BurnFlg".equalsIgnoreCase(name)) {
            setBurnFlg(value);
            getTObject().setValue("BurnFlg", value);
            return;
        }
        if ("CcuFlg".equalsIgnoreCase(name)) {
            setCcuFlg(value);
            getTObject().setValue("CcuFlg", value);
            return;
        }
        if ("IcuFlg".equalsIgnoreCase(name)) {
            setIcuFlg(value);
            getTObject().setValue("IcuFlg", value);
            return;
        }
        if ("ObservationFlg".equalsIgnoreCase(name)) {
            setObservationFlg(value);
            getTObject().setValue("ObservationFlg", value);
            return;
        }
        if ("PediatricFlg".equalsIgnoreCase(name)) {
            setPediatricFlg(value);
            getTObject().setValue("PediatricFlg", value);
            return;
        }
        if ("TransplantFlg".equalsIgnoreCase(name)) {
            setTransplantFlg(value);
            getTObject().setValue("TransplantFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
