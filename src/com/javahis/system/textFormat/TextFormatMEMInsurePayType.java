package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
/**
 * <p>Title: 患者保险先期支付类型</p>
 *
 * <p>Description: 患者保险先期支付类型</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author sunqy 2014.05.14
 * @version 1.0
 */
public class TextFormatMEMInsurePayType extends TTextFormat {
	/**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT ID, CHN_DESC AS NAME, ENG_DESC AS ENAME, PY1, PY2  " +
            " FROM SYS_DICTIONARY WHERE GROUP_ID = 'MEM_INSURE_PAY_TYPE' ORDER BY SEQ, ID";
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
        object.setValue("PopupMenuHeader", "ID,100;NAME,100");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "保险先期支付类型下拉区域");
        object.setValue("ShowColumnList", "ID;NAME");
      }

      public String getPopupMenuHeader()
      {
        return "ID,100;NAME,200";
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
