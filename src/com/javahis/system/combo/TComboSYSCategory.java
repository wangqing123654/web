package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;

/**
 *
 * <p>Title: ������������б�</p>
 *
 * <p>Description: ������������б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboSYSCategory
    extends TComboBox {
    /**
     * �������
     */
    private String ruleType; //RULE_TYPE
    /**
     * ��С����ע��
     */
    private String detailFlg; //DETAIL_FLG
    /**
     * ���ù������
     * @param ruleType String
     */
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    /**
     * �õ��������
     * @return String
     */
    public String getRuleType() {
        return ruleType;
    }

    /**
     * ������С����ע��
     * @param detailFlg String
     */
    public void setDetailFlg(String detailFlg) {
        this.detailFlg = detailFlg;
    }

    /**
     * �õ���С����ע��
     * @return String
     */
    public String getDetailFlg() {
        return detailFlg;
    }

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
    }

    /**
     * ִ��Module����
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("RULE_TYPE", getTagValue(getRuleType()));
        parm.setDataN("DETAIL_FLG", getTagValue(getDetailFlg()));
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
        object.setValue("Tip", "�������");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmString", "");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "sys\\SYSCategoryModule.x";
    }

    public String getModuleMethodName() {
        return "initCategoryCode";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("RuleType", "String", "", "Left")); //���ӹ������ɸѡ����
        data.add(new TAttribute("DetailFlg", "String", "", "Left")); //������С����ע��ɸѡ����
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("RuleType".equalsIgnoreCase(name)) {
            setRuleType(value);
            getTObject().setValue("RuleType", value);
            return;
        }
        if ("DetailFlg".equalsIgnoreCase(name)) {
            setDetailFlg(value);
            getTObject().setValue("DetailFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
