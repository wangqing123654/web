package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: 住院套餐类型属性下拉区域</p>
 *
 * <p>Description: 住院套餐类型属性下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: Bluecore</p>
 *
 * @author xiongwg 2015.07.07
 * @version 1.0
 */
public class TextFormatADMPackageType extends TTextFormat {
	private int popupMenuWidth;
	private int popupMenuHeight;
	/**
	 * 病案号
	 */
	private String mrNo;
	/**
	 * 套餐使用注记("0"---未使用;"1"---使用)
	 */
	private String usedFlg;
	/**
	 * 套餐门急住类型("O"---门诊;"E"---急诊;"I"---住院;"H"---健检)
	 */
	private String admType;

	
	/**
	 * 得到病案号
	 * 
	 * @return String
	 */
	public String getMrNo() {
		return mrNo;
	}
	/**
	 * 得到套餐使用注记
	 * 
	 * @return String
	 */
	public String getUsedFlg() {
		return usedFlg;
	}
	/**
	 * 得到套餐门急住类型
	 * 
	 * @return String
	 */
	public String getAdmType() {
		return admType;
	}
	
	

	/**
	 * 设置病案号
	 * 
	 * @param mrNo
	 *            String
	 */
	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}
	/**
	 * 设置套餐使用注记
	 * 
	 * @param usedFlg
	 *            String
	 */
	public void setUsedFlg(String usedFlg) {
		this.usedFlg = usedFlg;
	}
	/**
	 * 设置套餐门急住类型
	 * 
	 * @param admType
	 *            String
	 */
	public void setAdmType(String admType) {
		this.admType = admType;
	}

	/**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT C.PACKAGE_CODE AS ID, C.PACKAGE_DESC AS NAME, " +
            " TO_CHAR(A.AR_AMT,'999999990.00') AS AR_AMT, " +
            " A.TRADE_NO,C.PACKAGE_ENG_DESC AS ENNAME, C.PY1, C.PY2,C.SEQ  " +
            " FROM MEM_PACKAGE_TRADE_M A , MEM_PAT_PACKAGE_SECTION B, MEM_PACKAGE C  " +
            " WHERE A.TRADE_NO = B.TRADE_NO  " +
            " AND B.PACKAGE_CODE = C.PACKAGE_CODE AND C.PARENT_PACKAGE_CODE IS NOT NULL AND B.REST_TRADE_NO IS NULL" ;
        String sql1 = " GROUP BY C.SEQ,C.PACKAGE_CODE, C.PACKAGE_DESC,A.AR_AMT, " +
        		" A.TRADE_NO,C.PACKAGE_ENG_DESC,C.PY1,C.PY2 " +
        		" ORDER BY C.SEQ, C.PACKAGE_CODE ";
        StringBuffer sb = new StringBuffer();

        String mrNo = TypeTool.getString(getTagValue(getMrNo()));
        if (mrNo != null && mrNo.length() > 0){
            sb.append(" AND A.MR_NO = '" + mrNo + "' ");
        }

        String usedFlg = TypeTool.getString(getTagValue(getUsedFlg()));
        if (usedFlg != null && usedFlg.length() > 0) {
            sb.append("  AND A.USED_FLG = '" + usedFlg + "' ");
        }
        
        String admType = TypeTool.getString(getTagValue(getAdmType()));
        if (admType != null && admType.length() > 0) {
            sb.append(" AND C.ADM_TYPE = '" + admType + "' ");
        }
        if (sb.length() > 0)
            sql += sb.toString() + sql1;
        else
            sql = sql + sql1;
//        System.out.println("sql:::::"+sql);
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
        object.setValue("PopupMenuHeader", "代码,70;名称,180;金额,70");
        object.setValue("PopupMenuWidth", "200");
        object.setValue("PopupMenuHeight", "100");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "住院套餐类型属性");
        object.setValue("ShowColumnList", "ID;NAME;AR_AMT");
    }
    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name");
        this.setPopupMenuWidth(330);
        this.setPopupMenuHeight(200);
    }

    /**
     * 显示区域列名
     * @return String
     */
    public String getPopupMenuHeader() {

        return "代码,70;名称,180;金额,70";
    }
    
    /**
     * 显示区域列名
     * @return String
     */
    public int getPopupMenuWidth() {

        return popupMenuWidth;
    }
    /**
     * 显示区域列名
     * @return String
     */
    public int getPopupMenuHeight() {

        return popupMenuHeight;
    }
    
    public void setPopupMenuWidth(int popupMenuWidth){
    	this.popupMenuWidth=popupMenuWidth;
    }
    
    public void setPopupMenuHeight(int popupMenuHeight){
    	this.popupMenuHeight=popupMenuHeight;
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
        data.add(new TAttribute("UsedFlg", "String", "", "Left"));
        data.add(new TAttribute("AdmType", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
    	if ("MrNo".equalsIgnoreCase(name)) {
    		setMrNo(value);
            getTObject().setValue("MrNo", value);
            return;
        }
    	if ("UsedFlg".equalsIgnoreCase(name)) {
    		setUsedFlg(value);
            getTObject().setValue("UsedFlg", value);
            return;
        }
    	if ("AdmType".equalsIgnoreCase(name)) {
    		setAdmType(value);
            getTObject().setValue("AdmType", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
