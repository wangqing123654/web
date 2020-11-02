package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

public class TextFormatPatPackage extends TTextFormat{
	private String mrNo;
	private String validFlg;
	public String getMrNo() {
		return mrNo;
	}
	
	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}
	
	
	/**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
    	String date = TJDODBTool.getInstance().getDBTime().toString().substring(0,10).replaceAll("-", "/");
        String sql =
            " SELECT DISTINCT * FROM (SELECT DISTINCT B.PACKAGE_CODE ID,C.PACKAGE_DESC AS NAME, C.PY1  " +
            " FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B,MEM_PACKAGE C " +
            " WHERE A.TRADE_NO = B.TRADE_NO AND B.PACKAGE_CODE=C.PACKAGE_CODE " +
            " AND A.REST_FLAG='N' AND A.USED_FLG='0' AND  TO_DATE('"+date+"','yyyy/MM/dd') BETWEEN A.START_DATE AND A.END_DATE " +
            " AND B.REST_TRADE_NO IS NULL";
        String sql1 = " ORDER BY C.SEQ, ID ";//modify by sunqy 20140521
        //add by sunqy 20140521 ---start---
        StringBuffer sb = new StringBuffer();
        String mrNo = TypeTool.getString(getTagValue(this.getMrNo()));
        if (mrNo != null && mrNo.length() > 0) {
        	sb.append(" AND ");
        	sb.append(
        			" A.MR_NO = '"+mrNo+"' ");
        }
        /*if(this.validFlg.equals("Y") && !"".equals(this.validFlg)){
        	sb.append(" AND B.VALID_FLG='Y'");
        }*/
       
       
        if (sb.length() > 0){
        	sql += sb.toString() + sql1;
        }
        else {
        	sql = sql + sql1;
        }
        sql = sql + ")";
        //System.out.println("sql:::::::::::::::::::::::"+sql);
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
        object.setValue("PopupMenuHeader", "代码,100;名称,200");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "病患保险");
        object.setValue("ShowColumnList", "ID;NAME");
    }
    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;PY1,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name");
    }

    /**
     * 显示区域列名
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
        data.add(new TAttribute("MrNo", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
    	if ("MrNo".equalsIgnoreCase(name)) {
    		setEditValueAction(value);
            getTObject().setValue("MrNo", value);
            return;
        }
    	
    	//add by sunqy 20140521 ---end---w
        super.setAttribute(name, value);
    }
}
