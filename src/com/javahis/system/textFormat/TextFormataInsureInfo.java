package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

public class TextFormataInsureInfo extends TTextFormat{
	private String mrNo;
	private String validFlg;
	public String getMrNo() {
		return mrNo;
	}
	
	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}
	
	public String getValidFlg(){
		return validFlg;
	}
	
	public void setValidFlg(String validFlg){
		this.validFlg=validFlg;
		setModifySQL(true);
	}
	/**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT B.CONTRACTOR_CODE AS ID,A.CONTRACTOR_DESC AS NAME, A.UNIT_ENG_DESC AS ENNAME, A.PY1, A.PY2 " +
            " FROM MEM_CONTRACTOR A, MEM_INSURE_INFO B WHERE A.CONTRACTOR_CODE=B.CONTRACTOR_CODE ";
        String sql1 = " ORDER BY A.SEQ, ID ";//modify by sunqy 20140521
        //add by sunqy 20140521 ---start---
        StringBuffer sb = new StringBuffer();
        String mrNo = TypeTool.getString(getTagValue(this.getMrNo()));
        if (mrNo != null && mrNo.length() > 0) {
        	sb.append(" AND ");
        	sb.append(
        			" B.MR_NO = '"+mrNo+"' ");
        }
        /*if(this.validFlg.equals("Y") && !"".equals(this.validFlg)){
        	sb.append(" AND B.VALID_FLG='Y'");
        }*/
        
        String validFlgTemp = TypeTool.getString(getTagValue(getValidFlg()));
        if (validFlgTemp != null && validFlgTemp.length() > 0) {
            sb.append(" AND ");
            sb.append(" VALID_FLG = '" + validFlgTemp + "' ");
        }
        if (sb.length() > 0){
        	sql += sb.toString() + sql1;
        }
        else {
        	sql = sql + sql1;
        }
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
        data.add(new TAttribute("MrNo", "String", "", "Left"));
        data.add(new TAttribute("ValidFlg", "String", "", "Left"));
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
    	if ("ValidFlg".equalsIgnoreCase(name)) {
    		setEditValueAction(value);
            getTObject().setValue("ValidFlg", value);
            return;
        }
    	//add by sunqy 20140521 ---end---w
        super.setAttribute(name, value);
    }
}
