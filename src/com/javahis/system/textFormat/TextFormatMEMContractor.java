package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;
/**
 * <p>Title: 合同单位下拉区域</p>
 *
 * <p>Description: 合同单位下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author duzhw 2014.02.08
 * @version 1.0
 */
public class TextFormatMEMContractor extends TTextFormat {
	//add by sunqy 20140521 ---start---
	private String editValueAction;
	
	public String getEditValueAction() {
		return editValueAction;
	}
	
	public void setEditValueAction(String editValueAction) {
		this.editValueAction = editValueAction;
	}
	//add by sunqy 20140521 ---end---
	/**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT CONTRACTOR_CODE AS ID,CONTRACTOR_DESC AS NAME, UNIT_ENG_DESC AS ENNAME, PY1, PY2 " +
            " FROM MEM_CONTRACTOR WHERE 1=1";
        String sql1 = " ORDER BY SEQ, ID ";//modify by sunqy 20140521
        //add by sunqy 20140521 ---start---
        StringBuffer sb = new StringBuffer();
        String editValueAction = TypeTool.getString(getTagValue(getEditValueAction()));
//        System.out.println("editValueAction------->"+editValueAction);
        if (editValueAction != null && editValueAction.length() > 0) {
        	sb.append(" AND ");
        	sb.append(
        			" CONTRACTOR_TYPE IN(SELECT CONTRACTOR_TYPE FROM MEM_CONTRACTOR WHERE CONTRACTOR_TYPE='" +
        			editValueAction + "')");
        }
        if (sb.length() > 0){
        	sql += sb.toString() + sql1;
        }
        else {
        	sql = sql + sql1;
        }
//        System.out.println("sql--------->"+sql);
        //add by sunqy 20140521 ---end---
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
        object.setValue("Tip", "合同单位");
        object.setValue("ShowColumnList", "ID;NAME");
    }
    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
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
        data.add(new TAttribute("EditValueAction", "String", "", "Left"));//add by sunqy 20140521
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
    	//add by sunqy 20140521 ---start---
    	if ("EditValueAction".equalsIgnoreCase(name)) {
    		setEditValueAction(value);
            getTObject().setValue("EditValueAction", value);
            return;
        }
    	//add by sunqy 20140521 ---end---w
        super.setAttribute(name, value);
    }
}
