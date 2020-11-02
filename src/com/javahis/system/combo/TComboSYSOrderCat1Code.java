package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title: ҽ�����������б�</p>
 *
 * <p>Description: ҽ�����������б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboSYSOrderCat1Code extends TComboBox{
    /**
     * ҽ��ϸ����Ⱥ��
     */
    private String cat1Type;
    /**
     * ����ҽ��ϸ����Ⱥ��
     * @param cat1Type String
     */
    public void setCat1Type(String cat1Type){
        this.cat1Type = cat1Type;
    }
    /**
     * �õ�ҽ��ϸ����Ⱥ��
     * @return String
     */
    public String getCat1Type(){
        return cat1Type;
    }
    /**
    * ִ��Module����
    */
   public void onQuery()
   {
       TParm parm = new TParm();
       parm.setDataN("CAT1_TYPE",getTagValue(getCat1Type()));
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
       object.setValue("Tip","ҽ������");
       object.setValue("TableShowList","id,name");
       object.setValue("ModuleParmString","");
       object.setValue("ModuleParmTag","");
   }
   public String getModuleName()
   {
       return "sys\\SYSOrderCAT1Module.x";
   }
   public String getModuleMethodName()
   {
       return "initOrderCat1Code";
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
       data.add(new TAttribute("Cat1Type","String","","Left"));//����ҽ��ϸ����Ⱥ��ɸѡ����
   }
   /**
    * ��������
    * @param name String ������
    * @param value String ����ֵ
    */
   public void setAttribute(String name,String value)
   {
       if("Cat1Type".equalsIgnoreCase(name))
       {
           setCat1Type(value);
           getTObject().setValue("Cat1Type",value);
           return;
       }
       super.setAttribute(name,value);
   }
}
