package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;

/**
 * <p>Title: ҽ������</p>
 *
 * <p>Description: ��Ա���Combo</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class TComboINS_PAY_KINDCode extends TComboBox {
    private String nhiCompany;
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
        object.setValue("Tip", "��Ա���");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmString", "");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "sys\\SYSComboSQL.x";
    }

    public String getModuleMethodName() {
        return "getInsPayKind";
    }

    public String getParmMap() {
        return "id:PAY_KIND_CODE;name:PAY_KIND_DESC";
    }
    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data){
        data.add(new TAttribute("NhiCompany", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String
     * @param value String
     */
    public void setAttribute(String name, String value) {
        if ("NhiCompany".equalsIgnoreCase(name)) {
            this.setNhiCompany(value);
            this.getTObject().setValue("NhiCompany", value);
            return;
        }
        super.setAttribute(name, value);
    }
    /**
     * ִ�в�ѯ����(COMBO����)
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("NHI_COMPANY", this.getTagValue(this.getNhiCompany()));
        this.setModuleParm(parm);
        super.onQuery();
    }
    public String getNhiCompany() {
        return nhiCompany;
    }

    public void setNhiCompany(String nhiCompany) {
        this.nhiCompany = nhiCompany;
    }

}
