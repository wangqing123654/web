package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import jdo.sys.Operator;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: ��������������</p>
 *
 * <p>Description: ��������������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2011.05.04
 * @version 1.0
 */
public class TextFormatCLPChkType
    extends TTextFormat {
    /**
     * ҽ�����
     */
    private String orderCat1Code;
    /**
     * ����
     */
    private String regionCode;
    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql = " SELECT CHKTYPE_CODE AS ID,CHKTYPE_CHN_DESC AS NAME,CHKTYPE_ENG_DESC AS ENNAME,PY1,PY2 " +
            "   FROM CLP_CHKTYPE ";
        String sql1 = " ORDER BY CHKTYPE_CODE,SEQ ";
        StringBuffer sb = new StringBuffer();
        String orderCat1CodeTemp = TypeTool.getString(getTagValue(
            getOrderCat1Code()));
        if (orderCat1CodeTemp != null && orderCat1CodeTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" ORDER_CAT1_CODE = '" + orderCat1CodeTemp + "' ");
        }

        String RegionCodeTemp = TypeTool.getString(getTagValue(getRegionCode()));
        if (RegionCodeTemp != null && RegionCodeTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" REGION_CODE = '" + RegionCodeTemp + "' ");
        }
        String operatorCodeAll = Operator.getRegion();
        if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" REGION_CODE = '" + operatorCodeAll + "' ");
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
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "������");
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
        data.add(new TAttribute("OrderCat1Code", "String", "", "Left"));
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("OrderCat1Code".equalsIgnoreCase(name)) {
            setOrderCat1Code(value);
            getTObject().setValue("OrderCat1Code", value);
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
     * �õ�ҽ�����
     * @return String
     */
    public String getOrderCat1Code() {
        return orderCat1Code;
    }

    /**
     * �õ�����
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * ����ҽ�����
     * @param orderCat1Code String
     */
    public void setOrderCat1Code(String orderCat1Code) {
        this.orderCat1Code = orderCat1Code;
        setModifySQL(true);
    }

    /**
     * ��������
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
}
