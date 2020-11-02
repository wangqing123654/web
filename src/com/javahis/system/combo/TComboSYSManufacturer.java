package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title: �������������б�</p>
 *
 * <p>Description: �������������б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboSYSManufacturer extends TComboBox{
    /**
     * ҩƷ������ע��
     */
    private String phaFlg;
    /**
     * ��ֵ�ĲĹ�����ע��
     */
    private String matFlg;
    /**
     * �豸������ע��
     */
    private String devFlg;
    /**
     * ������Ӧ����ע��
     */
    private String otherFlg;
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
       parm.setDataN("PHA_FLG",getTagValue(getPhaFlg()));
       parm.setDataN("MAT_FLG",getTagValue(getMatFlg()));
       parm.setDataN("DEV_FLG",getTagValue(getDevFlg()));
       parm.setDataN("OTHER_FLG",getTagValue(getOtherFlg()));
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
       object.setValue("Tip","��������");
       object.setValue("TableShowList","id,name");
       object.setValue("ModuleParmString","");
       object.setValue("ModuleParmTag","");
   }
   public String getModuleName()
   {
       return "sys\\SYSManufacturer.x";
   }
   public String getModuleMethodName()
   {
       return "initSYSManufacturerCode";
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
       data.add(new TAttribute("PhaFlg","String","","Left"));
       data.add(new TAttribute("MatFlg","String","","Left"));
       data.add(new TAttribute("DevFlg","String","","Left"));
       data.add(new TAttribute("OtherFlg","String","","Left"));
}
   /**
    * ��������
    * @param name String ������
    * @param value String ����ֵ
    */
   public void setAttribute(String name,String value)
   {
       if("PhaFlg".equalsIgnoreCase(name))
       {
           setPhaFlg(value);
           getTObject().setValue("PhaFlg",value);
           return;
       }
       if("MatFlg".equalsIgnoreCase(name))
       {
           setMatFlg(value);
           getTObject().setValue("MatFlg",value);
           return;
       }
       if("DevFlg".equalsIgnoreCase(name))
       {
           setDevFlg(value);
           getTObject().setValue("DevFlg",value);
           return;
       }
       if("OtherFlg".equalsIgnoreCase(name))
       {
           setOtherFlg(value);
           getTObject().setValue("OtherFlg",value);
           return;
       }
       super.setAttribute(name,value);
   }
   /**
    * �õ��豸������ע��
    * @return String
    */
   public String getDevFlg() {
        return devFlg;
    }
    /**
     * �õ���ֵ�ĲĹ�����ע��
     * @return String
     */
    public String getMatFlg() {
        return matFlg;
    }
    /**
     * �õ�������Ӧ����ע��
     * @return String
     */
    public String getOtherFlg() {
        return otherFlg;
    }
    /**
     * �õ�ҩƷ������ע��
     * @return String
     */
    public String getPhaFlg() {
        return phaFlg;
    }
    /**
     * �����豸������ע��
     * @param devFlg String
     */
    public void setDevFlg(String devFlg) {
        this.devFlg = devFlg;
    }
    /**
     * ���õ�ֵ�ĲĹ�����ע��
     * @param matFlg String
     */
    public void setMatFlg(String matFlg) {
        this.matFlg = matFlg;
    }
    /**
     * ����������Ӧ����ע��
     * @param otherFlg String
     */
    public void setOtherFlg(String otherFlg) {
        this.otherFlg = otherFlg;
    }
    /**
     * ����ҩƷ������ע��
     * @param phaFlg String
     */
    public void setPhaFlg(String phaFlg) {
        this.phaFlg = phaFlg;
    }
}
