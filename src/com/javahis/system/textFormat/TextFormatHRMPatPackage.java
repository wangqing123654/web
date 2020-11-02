package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

/**
 * <p> Title: ������Ա�ײ������������  </p>
 * 
 * <p>Description: ������Ա�ײ������������  </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 * @author wanglong 20130508
 * @version 1.0
 */
public class TextFormatHRMPatPackage extends TTextFormat {

    /**
     * ��ͬ����
     */
    private String contractCode;

    /**
     * ����ע��
     */
    private String filterFlg;
    
    /**
     * ��ú�ͬ����
     * 
     * @param contractCode
     *            String
     */
    public String getContractCode() {
        return contractCode;
    }

    /**
     * ���ú�ͬ����
     * 
     * @param contractCode
     *            String
     */
    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
        setModifySQL(true);
    }
    
    /**
     * ��ù���ע��
     * @return
     */
    public String getFilterFlg() {
        return filterFlg;
    }

    /**
     * ���ù���ע��
     * @param filterFlg
     */
    public void setFilterFlg(String filterFlg) {
        this.filterFlg = filterFlg;
    }

    /**
     * ִ��Module����
     * 
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
                " SELECT A.SEQ_NO, A.PAT_NAME, C.CHN_DESC SEX_DESC, B.PACKAGE_DESC, A.MR_NO "
                        + "  FROM HRM_CONTRACTD A, HRM_PACKAGEM B, SYS_DICTIONARY C, HRM_PATADM D "
                        + " WHERE A.PACKAGE_CODE = B.PACKAGE_CODE "
                        + "   AND C.GROUP_ID = 'SYS_SEX' AND A.SEX_CODE = C.ID "
                        + "   AND A.MR_NO = D.MR_NO(+)   AND A.CONTRACT_CODE = D.CONTRACT_CODE(+) "
                        + "   AND A.CONTRACT_CODE = '#'  AND D.CASE_NO IS # NULL "
                        + "ORDER BY A.SEQ_NO";
        String contractCode = TypeTool.getString(getTagValue(getContractCode()));
        sql = sql.replaceFirst("#", contractCode);
        String filterFlg = TypeTool.getString(getTagValue(getFilterFlg()));
        if (TypeTool.getBoolean(filterFlg)) {
            sql = sql.replaceFirst("#", "NOT");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        sql = sql.replaceFirst("#", filterFlg.toUpperCase());
        // System.out.println("-------------������sql---------------" + sql);
        return sql;
    }

    /**
     * �½�����ĳ�ʼֵ
     * 
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null) return;
        object.setValue("Width", "81");
        object.setValue("Height", "23");
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "���,80;����,120;�Ա�,70;�ײ�,160");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "SEQ_NO,1;PAT_NAME,1;SEX_DESC,1;PACKAGE_DESC,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "��Ա�ײ����");
        object.setValue("ShowColumnList", "PAT_NAME;PACKAGE_DESC");
    }

    public void onInit() {
        super.onInit();
        setPopupMenuFilter("SEQ_NO,1;PAT_NAME,1;SEX_DESC,1;PACKAGE_DESC,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("No.;Name;Sex;Package Name");
    }

    /**
     * ��ʾ��������
     * 
     * @return String
     */
    // public String getPopupMenuHeader() {
    // return "����,100;����,200";
    // }

    /**
     * ������չ����
     * 
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        data.add(new TAttribute("contractCode", "String", "", "Left"));
        data.add(new TAttribute("filterFlg", "String", "", "Left"));
    }

    /**
     * ��������
     * 
     * @param name
     *            String ������
     * @param value
     *            String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("contractCode".equalsIgnoreCase(name)) {
            setContractCode(value);
            getTObject().setValue("contractCode", value);
            return;
        }
        if ("filterFlg".equalsIgnoreCase(name)) {
            setContractCode(value);
            getTObject().setValue("filterFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
