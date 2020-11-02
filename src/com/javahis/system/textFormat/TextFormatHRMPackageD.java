package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

/**
 * <p> Title: �����ײ�ϸ����������  </p>
 * 
 * <p>Description: �����ײ�ϸ����������  </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 * @author wanglong 20130422
 * @version 1.0
 */
public class TextFormatHRMPackageD extends TTextFormat {

    /**
     * �ײʹ���
     */
    private String packageCode;

    /**
     * ����ײʹ���
     * 
     * @param packageCode
     *            String
     */
    public String getPackageCode() {
        return packageCode;
    }

    /**
     * �����ײʹ���
     * 
     * @param packageCode
     *            String
     */
    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
        setModifySQL(true);
    }


    /**
     * ִ��Module����
     * 
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
                " WITH BB AS ("
                        + "SELECT ORDERSET_CODE,SUM(DISPENSE_QTY*ORIGINAL_PRICE) ORIGINAL_PRICE,SUM(DISPENSE_QTY*PACKAGE_PRICE) PACKAGE_PRICE "
                        + "  FROM HRM_PACKAGED "
                        + " WHERE 1 = 1 "
                        + "   AND PACKAGE_CODE = '#' "
                        + " GROUP BY ORDERSET_CODE) "
                        + "SELECT DISTINCT A.ORDER_CODE,A.ORDER_DESC,A.GOODS_DESC,A.SPECIFICATION,B.PACKAGE_PRICE,C.UNIT_CHN_DESC,"
                        + "       A.ORDER_CAT1_CODE,A.DISPENSE_UNIT,py(ORDER_DESC) PY1,py(GOODS_DESC) PY2,A.ORDERSET_CODE,"
                        + "       1 AS DISPENSE_QTY,B.ORIGINAL_PRICE,A.SETMAIN_FLG,A.HIDE_FLG,A.OPTITEM_CODE,A.EXEC_DEPT_CODE,"
                        + "       A.DEPT_ATTRIBUTE,A.MEDI_QTY,A.MEDI_UNIT,A.FREQ_CODE,A.ROUTE_CODE,A.SEQ "
                        + "  FROM HRM_PACKAGED A, BB B, SYS_UNIT C "
                        + " WHERE A.ORDERSET_CODE = B.ORDERSET_CODE AND A.SETMAIN_FLG = 'Y' AND A.DISPENSE_UNIT = C.UNIT_CODE"
                        + "   AND A.PACKAGE_CODE = '#' "
                        + "ORDER BY A.SEQ";
        String packageCode = TypeTool.getString(getTagValue(getPackageCode()));
        sql = sql.replaceFirst("#", packageCode);
        sql = sql.replaceFirst("#", packageCode);
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
        object.setValue("PopupMenuHeader", "ҽ����,100;ҽ������,180;��Ʒ��,100;���,100;����,70,double,;��λ,60;ҽ�����,80");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ORDER_CODE,1;ORDER_DESC,1;GOODS_DESC,1;SPECIFICATION,1;PACKAGE_PRICE,1;UNIT_CHN_DESC,1;ORDER_CAT1_CODE,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "�ײ�ϸ��");
        object.setValue("ShowColumnList", "ORDER_CODE;ORDER_DESC;GOODS_DESC;SPECIFICATION;PACKAGE_PRICE;UNIT_CHN_DESC;ORDER_CAT1_CODE");
    }

    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ORDER_CODE,1;ORDER_DESC,3;GOODS_DESC,3;PY1,1;PY2,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name;Goods Name;Specification;Price;Unit;Class");
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
        data.add(new TAttribute("packageCode", "String", "", "Left"));
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
        if ("packageCode".equalsIgnoreCase(name)) {
            setPackageCode(value);
            getTObject().setValue("packageCode", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
