package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.data.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import jdo.sys.Operator;
/**
 *
 * <p>Title:ʱ�������б� </p>
 *
 * <p>Description:ʱ�������б� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2008.08.25
 * @version 1.0
 */
public class TComboSession extends TComboBox{
    /**
     * �ż���
     */
    private String admType;
    /**
     * ����
     */
    private String region;
    /**
     * �����ż���
     * @param admType String
     */
    public void setAdmType(String admType)
    {
        this.admType = admType;
    }
    /**
     * ��������
     * @param region String
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * �õ��ż���
     * @return String
     */
    public String getAdmType()
    {
        return admType;
    }
    /**
     * �õ�����
     * @return String
     */
    public String getRegion() {
        return region;
    }

    /**
     * ִ��Module����
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("ADM_TYPE",getTagValue(getAdmType()));
        parm.setDataN("REGION_CODE",getTagValue(getRegion()));
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
        object.setValue("Tip","ʱ��");
        object.setValue("TableShowList","id,name");
        object.setValue("ModuleParmString","");
        object.setValue("ModuleParmTag","");
    }
    public String getModuleName()
    {
        return "reg\\REGSessionModule.x";
    }
    public String getModuleMethodName()
    {
        return "initsessioncode";
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
        data.add(new TAttribute("AdmType","String","","Left"));
        data.add(new TAttribute("Region","String","","Left"));
    }
    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name,String value)
    {
        if("AdmType".equalsIgnoreCase(name))
        {
            setAdmType(value);
            getTObject().setValue("AdmType",value);
            return;
        }
        if("Region".equalsIgnoreCase(name))
        {
            setRegion(value);
            getTObject().setValue("Region",value);
            return;
        }
       super.setAttribute(name,value);
    }

}
