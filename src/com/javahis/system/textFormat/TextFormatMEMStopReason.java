package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 会员停卡原因</p>
 *
 * <p>Description: 会员停卡原因</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author huangtt 2014.04.21
 * @version 1.0
 */
public class TextFormatMEMStopReason extends TTextFormat{
	/**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT ID, CHN_DESC AS NAME, ENG_DESC AS ENAME, PY1, PY2  " +
            " FROM SYS_DICTIONARY WHERE GROUP_ID = 'MEM_STOP_REASON' ORDER BY SEQ, ID";
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
        object.setValue("PopupMenuHeader", "代码,100;名称,100");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "停卡原因下拉区域");
        object.setValue("ShowColumnList", "ID;NAME");
      }

      public String getPopupMenuHeader()
      {
        return "代码,100;名称,200";
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
