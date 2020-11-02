package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.data.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import jdo.sys.Operator;
/**
 *
 * <p>Title:ҩ�������б� </p>
 *
 * <p>Description:ҩ�������б� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.27
 * @version 1.0
 */
public class TComboOrgCode extends TComboBox{
    /**
     * �ⷿ���
     */
    private String orgFlg;//ORG_FLG
    /**
     * �ⷿ����
     */
    private String orgType;
    /**
     * ��������ע��
     */
    private String exinvFlg;//EXINV_FLG
    /**
     * ��ʿվע��
     */
    private String stationFlg;//STATION_FLG
    /**
     * �������ע��
     */
    private String injOrgFlg;//INJ_ORG_FLG
    /**
     * ���ÿⷿ����
     * @param orgType String
     */
    public void setOrgType(String orgType)
    {
        this.orgType = orgType;
    }
    /**
     * �õ��ⷿ����
     * @return String
     */
    public String getOrgType()
    {
        return orgType;
    }
    /**
     * ���ÿⷿ���
     * @param orgFlg String
     */
    public void setOrgFlg(String orgFlg){
        this.orgFlg = orgFlg;
    }
    /**
     * ���ÿ�������ע��
     * @param exinvFlg String
     */
    public void setExinvFlg(String exinvFlg) {
        this.exinvFlg = exinvFlg;
    }
    /**
     * ���þ������ע��
     * @param injOrgFlg String
     */
    public void setInjOrgFlg(String injOrgFlg) {
        this.injOrgFlg = injOrgFlg;
    }
    /**
     * ���û�ʿվע��
     * @param stationFlg String
     */
    public void setStationFlg(String stationFlg) {
        this.stationFlg = stationFlg;
    }

    /**
     * �õ��ⷿ���
     * @return String
     */
    public String getOrgFlg(){
        return orgFlg;
    }
    /**
     * �õ���������ע��
     * @return String
     */
    public String getExinvFlg() {
        return exinvFlg;
    }
    /**
     * �õ��������ע��
     * @return String
     */
    public String getInjOrgFlg() {
        return injOrgFlg;
    }
    /**
     * �õ���ʿվע��
     * @return String
     */
    public String getStationFlg() {
        return stationFlg;
    }

    /**
     * ִ��Module����
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("ORG_TYPE",getTagValue(getOrgType()));
        parm.setDataN("ORG_FLG",getTagValue(getOrgFlg()));
        parm.setDataN("EXINV_FLG",getTagValue(this.getExinvFlg()));
        parm.setDataN("STATION_FLG",getTagValue(this.getStationFlg()));
        parm.setDataN("INJ_ORG_FLG",getTagValue(this.getInjOrgFlg()));
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
        object.setValue("Tip","ҩ��");
        object.setValue("TableShowList","id,name");
        object.setValue("ModuleParmTag","");
    }
    public String getModuleName()
    {
        return "ind\\INDOrgModule.x";
    }
    public String getModuleMethodName()
    {
        return "getOrgCode";
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
        data.add(new TAttribute("OrgType","String","","Left"));
        data.add(new TAttribute("OrgFlg","String","","Left"));
        data.add(new TAttribute("ExinvFlg","String","","Left"));
        data.add(new TAttribute("InjOrgFlg","String","","Left"));
        data.add(new TAttribute("StationFlg","String","","Left"));
    }
    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name,String value)
    {
        if("OrgType".equalsIgnoreCase(name))
        {
            setOrgType(value);
            getTObject().setValue("OrgType",value);
            return;
        }
        if("OrgFlg".equalsIgnoreCase(name))
        {
            setOrgFlg(value);
            getTObject().setValue("OrgFlg",value);
            return;
        }
        if("ExinvFlg".equalsIgnoreCase(name))
        {
            setExinvFlg(value);
            getTObject().setValue("ExinvFlg",value);
            return;
        }
        if("InjOrgFlg".equalsIgnoreCase(name))
        {
            setInjOrgFlg(value);
            getTObject().setValue("InjOrgFlg",value);
            return;
        }
        if("StationFlg".equalsIgnoreCase(name))
        {
            setStationFlg(value);
            getTObject().setValue("StationFlg",value);
            return;
        }
        super.setAttribute(name,value);
    }
}
