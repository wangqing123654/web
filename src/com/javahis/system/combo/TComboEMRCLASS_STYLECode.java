package com.javahis.system.combo;

import com.dongyang.ui.*;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TComboEMRCLASS_STYLECode
    extends TComboBox {
    /**
      * �½�����ĳ�ʼֵ
      * @param object TObject
      */
     public void createInit(TObject object)
     {
         if(object == null)
             return;
         object.setValue("Width","81");
         object.setValue("Height","23");
         object.setValue("Text","TButton");
         object.setValue("showID","Y");
         object.setValue("showName","Y");
         object.setValue("showText","N");
         object.setValue("showValue","N");
         object.setValue("showPy1","Y");
         object.setValue("showPy2","Y");
         object.setValue("Editable","Y");
         object.setValue("Tip","������ʽ");
         object.setValue("TableShowList","id,name");
         object.setValue("ModuleParmString","GROUP_ID:EMR_TYPE");
         object.setValue("ModuleParmTag","");
     }
     public String getModuleName()
     {
         return "sys\\SYSDictionaryModule.x";
     }
     public String getModuleMethodName()
     {
         return "getGroupList";
     }
     public String getParmMap()
     {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
     }
     /**
      * ������չ����
      * @param data TAttributeList
      */
     public void getEnlargeAttributes(TAttributeList data){
     }
}
