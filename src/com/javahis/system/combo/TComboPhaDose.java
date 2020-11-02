package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title: ���������б�</p>
 *
 * <p>Description: ���������б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboPhaDose extends TComboBox{
    /**
     * ���ʹ����
     */
    private String doseType;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        super.onInit();
    }
    /**
    * ִ��Module����
    */
   public void onQuery()
   {
       TParm parm = new TParm();
       parm.setDataN("DOSE_TYPE",getTagValue(getDoseType()));
       setModuleParm(parm);
       super.onQuery();
   }
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
       object.setValue("Tip","����");
       object.setValue("TableShowList","id,name");
       object.setValue("ModuleParmString","");
       object.setValue("ModuleParmTag","");
   }
   public String getModuleName()
   {
       return "sys\\SYSPhaDoseModule.x";
   }
   public String getModuleMethodName()
   {
       return "initPHADoseCode";
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
       data.add(new TAttribute("DoseType","String","","Left"));
}
   /**
    * ��������
    * @param name String ������
    * @param value String ����ֵ
    */
   public void setAttribute(String name,String value)
   {
       if("DoseType".equalsIgnoreCase(name))
       {
           setDoseType(value);
           getTObject().setValue("DoseType",value);
           return;
       }
       super.setAttribute(name,value);
   }
   /**
    * �õ����ʹ����
    * @return String
    */
   public String getDoseType() {
        return doseType;
    }
    /**
     * ���ü��ʹ����
     * @param doseType String
     */
    public void setDoseType(String doseType) {
        this.doseType = doseType;
    }
}
