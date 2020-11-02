package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import jdo.sys.Operator;

/**
 * <p>Title: סԺҽ��վ</p>
 *
 * <p>Description: ����COMBO</p>
 *
 * <p>Copyright: Copyright (c) 2008 JavaHis</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class TComboSYSStationCode extends TComboBox {
    /**
     * ����
     */
    private String regionCode;
    /**
     * ����
     */
    private String deptCode;
    /**
     * Ԥ��ҩ��
     */
    private String orgCode;
    /**
     * λ��
     */
    private String locCode;
    /**
     * �õ�����
     * @return String
     */
    public String getDeptCode() {
        return deptCode;
    }
    /**
     * �õ�λ��
     * @return String
     */
    public String getLocCode() {
        return locCode;
    }
    /**
     * �õ�ҩ��
     * @return String
     */
    public String getOrgCode() {
        return orgCode;
    }
    /**
     * �õ�����
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }
    /**
     * ���ÿ���
     * @param deptCode String
     */
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }
    /**
     * ����λ��
     * @param locCode String
     */
    public void setLocCode(String locCode) {
        this.locCode = locCode;
    }
    /**
     * ����ҩ��
     * @param orgCode String
     */
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    /**
     * ��������
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
    /**
     * ִ��Module����
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("REGION_CODE",getTagValue(getRegionCode()));
        parm.setDataN("DEPT_CODE",getTagValue(getDeptCode()));
        parm.setDataN("ORG_CODE",getTagValue(getOrgCode()));
        parm.setDataN("LOC_CODE",getTagValue(getLocCode()));
        String optRegion = Operator.getRegion();
        if(!"".equals(optRegion))
            parm.setData("REGION_CODE_ALL",optRegion);
        setModuleParm(parm);
        super.onQuery();
    }
    /**
    * �½�����ĳ�ʼֵ
    * @param object TObject
    */
   public void createInit(TObject object) {
       if (object == null)
           return;
       object.setValue("Width", "81");
       object.setValue("Height", "23");
       object.setValue("Text", "TButton");
       object.setValue("showID", "Y");
       object.setValue("showName", "Y");
       object.setValue("showText", "N");
       object.setValue("showValue", "N");
       object.setValue("showPy1", "Y");
       object.setValue("showPy2", "Y");
       object.setValue("Editable", "Y");
       object.setValue("Tip", "����");
       object.setValue("TableShowList", "id,name");
   }

   public String getModuleName() {
       return "sys\\SYSStationModule.x";
   }

   public String getModuleMethodName() {
       return "getStation";
   }

   public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
   }
   /**
    * ������չ����
    * @param data TAttributeList
    */
   public void getEnlargeAttributes(TAttributeList data){
       data.add(new TAttribute("RegionCode","String","","Left"));
       data.add(new TAttribute("DeptCode","String","","Left"));
       data.add(new TAttribute("OrgCode","String","","Left"));
       data.add(new TAttribute("LocCode","String","","Left"));
   }
   /**
    * ��������
    * @param name String ������
    * @param value String ����ֵ
    */
   public void setAttribute(String name,String value)
   {
       if("RegionCode".equalsIgnoreCase(name))
       {
           setRegionCode(value);
           getTObject().setValue("RegionCode",value);
           return;
       }
       if("DeptCode".equalsIgnoreCase(name))
       {
           setDeptCode(value);
           getTObject().setValue("DeptCode",value);
           return;
       }
       if("OrgCode".equalsIgnoreCase(name))
       {
           setOrgCode(value);
           getTObject().setValue("OrgCode",value);
           return;
       }
       if("LocCode".equalsIgnoreCase(name))
       {
           setLocCode(value);
           getTObject().setValue("LocCode",value);
           return;
       }
       super.setAttribute(name,value);
   }

}
