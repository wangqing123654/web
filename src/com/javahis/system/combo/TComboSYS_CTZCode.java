package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboNode;
import com.dongyang.util.StringTool;

/**
 * <p>Title: ҽ������</p>
 *
 * <p>Description: ��Ƕʽҽ������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class TComboSYS_CTZCode extends TComboBox {
    private String companyCode;
    /**
     * �����ע��
     */
    private String ctz1Flg;
    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    /**
     * ���������ע��
     * @param ctz1Flg String
     */
    public void setCtz1Flg(String ctz1Flg) {
        this.ctz1Flg = ctz1Flg;
    }

    /**
     * �õ������ע��
     * @return String
     */
    public String getCtz1Flg() {
        return ctz1Flg;
    }

    /**
     * ִ�в�ѯ����(COMBO����)
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("COMPANY_CODE", this.getTagValue(this.getCompanyCode()));
        parm.setDataN("CTZ1_FLG",this.getTagValue(this.getCtz1Flg()));
        this.setModuleParm(parm);
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
        object.setValue("Tip", "���");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmString", "");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "sys\\SYSComboSQL.x";
    }

    public String getModuleMethodName() {
        return "getSysCtzCode";
    }

    public String getParmMap() {
        return "id:CTZ_CODE;name:CTZ_DESC";
    }
    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data){
        data.add(new TAttribute("CompanyCode", "String", "", "Left"));
        data.add(new TAttribute("Ctz1Flg", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String
     * @param value String
     */
    public void setAttribute(String name, String value) {
        if ("CompanyCode".equalsIgnoreCase(name)) {
            this.setCompanyCode(value);
            this.getTObject().setValue("CompanyCode", value);
            return;
        }
        if ("Ctz1Flg".equalsIgnoreCase(name)) {
            setCtz1Flg(value);
            getTObject().setValue("Ctz1Flg", value);
            return;
        }

        super.setAttribute(name, value);
    }

}
