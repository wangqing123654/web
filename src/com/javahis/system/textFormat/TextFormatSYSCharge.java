package com.javahis.system.textFormat;

import java.sql.Timestamp;

import jdo.sys.Operator;

import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTextFormat;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title: 收据类别下拉区域</p>
 *
 * <p>Description: 收据类别下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TextFormatSYSCharge
    extends TTextFormat {
	
	/**
     * 费用类别(住院/门诊)
     */
    private String feeType;
    
    /**
     * 设置费用类别(住院/门诊)
     * @param admType String
     */
    public void setFeeType(String feeType) {
        this.feeType = feeType;
        setModifySQL(true);
    }
    
    /**
     * 得到费用类别(住院/门诊)
     * @return String
     */
    public String getFeeType() {
        return feeType;
    }
    
    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
//        String sql =
//            " SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE' ORDER BY SEQ,ID ";
    	String sql =
            " SELECT A.ID,A.NAME,A.ENNAME,A.PY1,A.PY2 FROM (" +
            " SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2,SEQ " +
            " FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE') A  ";
        String sql1 = " ORDER BY A.SEQ,A.ID ";

        StringBuffer sb = new StringBuffer();

        String feeType = TypeTool.getString(getTagValue(getFeeType()));
        if (feeType != null && feeType.length() > 0) {
        	if(("O").equalsIgnoreCase(feeType)){
        		sb.append(" ,(SELECT OPD_CHARGE_CODE FROM SYS_CHARGE_HOSP " +
        				" GROUP BY OPD_CHARGE_CODE) B  " +
        				" WHERE A.ID=B.OPD_CHARGE_CODE ");
        	}
        	if(("I").equalsIgnoreCase(feeType)){
        		sb.append(" ,(SELECT IPD_CHARGE_CODE FROM SYS_CHARGE_HOSP " +
        				" GROUP BY IPD_CHARGE_CODE) B  " +
        				" WHERE A.ID=B.IPD_CHARGE_CODE ");
        	}     	    
        }
        if (sb.length() > 0)
            sql += sb.toString() + sql1;
        else
            sql = sql + sql1;
        return sql;
    }

    /**
     * 新建对象的初始值
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null)
            return;
        object.setValue("Width", "81");
        object.setValue("Height", "23");
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "代码,100;名称,100");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;");
//        object.setValue("ShowColumnList", "NAME");
//        object.setValue("ValueColumn", "ID");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "收据类别");
        object.setValue("ShowColumnList", "ID;NAME");
    }
    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name");
    }

    /**
     * 显示区域列明
     * @return String
     */
    public String getPopupMenuHeader() {

        return "代码,100;名称,200";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        data.add(new TAttribute("FeeType", "String", "", "Left"));
        
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
    	if ("FeeType".equalsIgnoreCase(name)) {
    		setFeeType(value);
            getTObject().setValue("FeeType", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
