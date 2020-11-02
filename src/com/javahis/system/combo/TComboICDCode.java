package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title:������ϴ��������б� </p>
 *
 * <p>Description:������ϴ��������б� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.26
 * @version 1.0
 */
public class TComboICDCode extends TComboBox{
    /**
     * ����ҽ
     */
    private String icdType;
    /**
      * ��������ҽ
      * @param icdType String
      */
     public void setIcdType(String icdType) {
         this.icdType = icdType;
     }

     /**
      * �õ�����ҽ
      * @return String
      */
     public String getIcdType() {
         return icdType;
     }
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
        parm.setDataN("ICD_TYPE",getTagValue(getIcdType()));
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
         object.setValue("Tip","������ϴ���");
         object.setValue("TableShowList","id,text");
         object.setValue("ModuleParmString","");
         object.setValue("ModuleParmTag","");
     }
     public String getModuleName()
     {
         return "sys\\SYSDiagnosisModule.x";
     }
     public String getModuleMethodName()
     {
         return "initcombo";
     }
     public String getParmMap()
     {
         return "id:ID;text:TEXT;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
     }
     /**
      * ������չ����
      * @param data TAttributeList
      */
     public void getEnlargeAttributes(TAttributeList data){
         data.add(new TAttribute("IcdType","String","","Left"));//��������ҽɸѡ����
     }
     /**
      * ��������
      * @param name String ������
      * @param value String ����ֵ
      */
     public void setAttribute(String name,String value)
     {
         if("IcdType".equalsIgnoreCase(name))
         {
             setIcdType(value);
             getTObject().setValue("IcdType",value);
             return;
         }

         super.setAttribute(name,value);
    }}
