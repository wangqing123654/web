package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;

/**
*
* <p>Title: ��Ա�����������</p>
*
* <p>Description: ��Ա�����������</p>

* <p>Company: BlueCore</p>
*
* @author duzhw
* @version 1.0
*/
public class TextFormatMEMBershipFeeType extends TTextFormat {
	
	public String getPopupMenuSQL()
	  {
	    
	    String sql = 
	      " SELECT MEM_CODE AS ID,DESCRIPTION AS NAME,MEM_ENG_DESC AS ENNAME,PY1  FROM MEM_MEMBERSHIP_INFO ORDER BY SEQ ";
	    return sql;
	  }

	  public void onInit() {
	    super.onInit();
	    setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
	    setLanguageMap("NAME|ENNAME");
	    setPopupMenuEnHeader("Code;Name");
	  }

	  public void createInit(TObject object)
	  {
	    if (object == null)
	      return;
	    object.setValue("Width", "81");
	    object.setValue("Height", "23");
	    object.setValue("Text", "");
	    object.setValue("HorizontalAlignment", "2");
	    object.setValue("PopupMenuHeader", "����,100;����,100");
	    object.setValue("PopupMenuWidth", "300");
	    object.setValue("PopupMenuHeight", "300");
	    object.setValue("PopupMenuFilter", "ID,1;NAME,1");
	    object.setValue("FormatType", "combo");
	    object.setValue("ShowDownButton", "Y");
	    object.setValue("Tip", "��Ա�����������");
	    object.setValue("ShowColumnList", "ID;NAME");
	  }

	  public String getPopupMenuHeader()
	  {
	    return "����,100;����,200";
	  }

	  public void getEnlargeAttributes(TAttributeList data)
	  {
	    data.add(new TAttributeList.TAttribute("ShowColumnList", "String", "NAME", "Left"));
	    data.add(new TAttributeList.TAttribute("ValueColumn", "String", "ID", "Left"));
	    data.add(new TAttributeList.TAttribute("HisOneNullRow", "boolean", "N", "Center"));
	    
	  }

	  public void setAttribute(String name, String value)
	  {
	   
	    super.setAttribute(name, value);
	  }
}
