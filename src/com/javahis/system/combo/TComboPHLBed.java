package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.data.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import jdo.sys.Operator;

/**
 * <p>Title: ���㴲λ�����б�</p>
 *
 * <p>Description: ���㴲λ�����б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.17
 * @version 1.0
 */
public class TComboPHLBed
    extends TComboBox {
    /**
     * ����
     */
    private String regionCode;
    /**
     * ��λ״̬
     */
    private String bedStatus;
    /**
     * ��������
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * ���ô�λ״̬
     * @param bedStatus String
     */
    public void setBedStatus(String bedStatus) {
        this.bedStatus = bedStatus;
    }

    /**
     * �õ�����
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * �õ���λ״̬
     * @return String
     */
    public String getBedStatus() {
        return bedStatus;
    }

    /**
     * ִ��Module����
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("REGION_CODE", getTagValue(getRegionCode()));
        parm.setDataN("BED_STATUS", getTagValue(getBedStatus()));
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
        object.setValue("Tip", "���㴲λ");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "phl\\PHLBedModule.x";
    }

    public String getModuleMethodName() {
        return "initCombo";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME";
    }

    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
        data.add(new TAttribute("BedStatus", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("RegionCode".equalsIgnoreCase(name)) {
            setRegionCode(value);
            getTObject().setValue("RegionCode", value);
            return;
        }
        if ("BedStatus".equalsIgnoreCase(name)) {
            setBedStatus(value);
            getTObject().setValue("BedStatus", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
