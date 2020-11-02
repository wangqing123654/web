package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: ����ԭ����������</p>
 *
 * <p>Description: ����ԭ����������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.29
 * @version 1.0
 */
public class TextFormatINVReason
    extends TTextFormat {
    /**
     * ����ʹ��ע��
     */
    private String purFlg; //PUR_FLG
    /**
     * ����ʹ��ע��
     */
    private String verFlg; //VER_FLG
    /**
     * �˻�ʹ��ע��
     */
    private String regFlg; //REG_FLG
    /**
     * ����ʹ��ע��
     */
    private String reqFlg; //REQ_FLG
    /**
     * ����ʹ��ע��
     */
    private String gifFlg; //GIF_FLG
    /**
     * �˿�ʹ��ע��
     */
    private String retFlg; //RET_FLG
    /**
     * ����ʹ��ע��
     */
    private String wasFlg; //WAS_FLG
    /**
     * ͣ��ע��
     */
    private String delFlg; //DEL_FLG

    /**
     * ���ö���ʹ��ע��
     * @param purFlg String
     */
    public void setPurFlg(String purFlg) {

        this.purFlg = purFlg;
        setModifySQL(true);
    }

    /**
     * �����˻�ʹ��ע��
     * @param v String
     */
    public void setRegFlg(String v) {
        this.regFlg = regFlg;
        setModifySQL(true);
    }

    /**
     * ����ͣ��ע��
     * @param delFlg String
     */
    public void setDelFlg(String delFlg) {
        this.delFlg = delFlg;
        setModifySQL(true);
    }

    /**
     * ���ú���ʹ��ע��
     * @param wasFlg String
     */
    public void setWasFlg(String wasFlg) {
        this.wasFlg = wasFlg;
        setModifySQL(true);
    }

    /**
     * ��������ʹ��ע��
     * @param verFlg String
     */
    public void setVerFlg(String verFlg) {
        this.verFlg = verFlg;
        setModifySQL(true);
    }
    /**
     * �������۴�ע��
     * @param gifFlg String
     */
    public void setGifFlg(String gifFlg) {
        this.gifFlg = gifFlg;
        setModifySQL(true);
    }

    /**
     * ��������ʹ��ע��
     * @param reqFlg String
     */
    public void setReqFlg(String reqFlg) {
        this.reqFlg = reqFlg;
        setModifySQL(true);
    }

    /**
     * �����˿�ʹ��ע��
     * @param retFlg String
     */
    public void setRetFlg(String retFlg) {
        this.retFlg = retFlg;
        setModifySQL(true);
    }

    /**
     * �õ�����ʹ��ע��
     * @return String
     */
    public String getPurFlg() {
        return purFlg;
    }

    /**
     * �õ��˻�ʹ��ע��
     * @return String
     */
    public String getRegFlg() {
        return regFlg;
    }

    /**
     * �õ�ͣ��ע��
     * @return String
     */
    public String getDelFlg() {
        return delFlg;
    }

    /**
     * �õ�����ʹ��ע��
     * @return String
     */
    public String getWasFlg() {
        return wasFlg;
    }

    /**
     * �õ�����ʹ��ע��
     * @return String
     */
    public String getVerFlg() {
        return verFlg;
    }

    /**
     * �õ����۴�ע��
     * @return String
     */
    public String getGifFlg() {
        return gifFlg;
    }

    /**
     * �õ�����ʹ��ע��
     * @return String
     */
    public String getReqFlg() {
        return reqFlg;
    }

    /**
     * �õ��˿�ʹ��ע��
     * @return String
     */
    public String getRetFlg() {
        return retFlg;
    }

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT REN_CODE AS ID,REN_DESC AS NAME,ENNAME,PY1,PY2 " +
            "   FROM INV_REASON ";
        String sql1 = " ORDER BY SEQ,REN_CODE";

        StringBuffer sb = new StringBuffer();

        String purFlg = TypeTool.getString(getTagValue(getPurFlg()));
        if (purFlg != null && purFlg.length() > 0)
            sb.append(" PUR_FLG = '" + purFlg + "' ");

        String verFlg = TypeTool.getString(getTagValue(getVerFlg()));
        if (verFlg != null && verFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" VER_FLG = '" + verFlg + "' ");
        }

        String regFlg = TypeTool.getString(getTagValue(getRegFlg()));
        if (regFlg != null && regFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" REG_FLG = '" + regFlg + "' ");
        }

        String reqFlg = TypeTool.getString(getTagValue(getReqFlg()));
        if (reqFlg != null && reqFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" REQ_FLG = '" + reqFlg + "' ");
        }

        String gifFlg = TypeTool.getString(getTagValue(getGifFlg()));
        if (gifFlg != null && gifFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" GIF_FLG = '" + gifFlg + "' ");
        }

        String retFlg = TypeTool.getString(getTagValue(getRetFlg()));
        if (retFlg != null && retFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" RET_FLG = '" + retFlg + "' ");
        }

        String wasFlg = TypeTool.getString(getTagValue(getWasFlg()));
        if (wasFlg != null && wasFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" WAS_FLG = '" + wasFlg + "' ");
        }

        String delFlg = TypeTool.getString(getTagValue(getDelFlg()));
        if (delFlg != null && delFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" DEL_FLG = '" + delFlg + "' ");
        }
        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
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
        object.setValue("Tip", "����ԭ��");
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
        data.add(new TAttribute("PurFlg", "String", "", "Left"));
        data.add(new TAttribute("VerFlg", "String", "", "Left"));
        data.add(new TAttribute("RegFlg", "String", "", "Left"));
        data.add(new TAttribute("ReqFlg", "String", "", "Left"));
        data.add(new TAttribute("GifFlg", "String", "", "Left"));
        data.add(new TAttribute("RetFlg", "String", "", "Left"));
        data.add(new TAttribute("WasFlg", "String", "", "Left"));
        data.add(new TAttribute("DelFlg", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("PurFlg".equalsIgnoreCase(name)) {
            setPurFlg(value);
            getTObject().setValue("PurFlg", value);
            return;
        }
        if ("VerFlg".equalsIgnoreCase(name)) {
            setVerFlg(value);
            getTObject().setValue("VerFlg", value);
            return;
        }
        if ("RegFlg".equalsIgnoreCase(name)) {
            setRegFlg(value);
            getTObject().setValue("RegFlg", value);
            return;
        }
        if ("ReqFlg".equalsIgnoreCase(name)) {
            setReqFlg(value);
            getTObject().setValue("ReqFlg", value);
            return;
        }
        if ("DelFlg".equalsIgnoreCase(name)) {
            setDelFlg(value);
            getTObject().setValue("DelFlg", value);
            return;
        }
        if ("WasFlg".equalsIgnoreCase(name)) {
            setWasFlg(value);
            getTObject().setValue("WasFlg", value);
            return;
        }
        if ("GifFlg".equalsIgnoreCase(name)) {
            setGifFlg(value);
            getTObject().setValue("GifFlg", value);
            return;
        }
        if ("RetFlg".equalsIgnoreCase(name)) {
            setRetFlg(value);
            getTObject().setValue("RetFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
