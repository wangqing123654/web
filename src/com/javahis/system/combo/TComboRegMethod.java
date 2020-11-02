package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.data.TParm;
import com.dongyang.ui.edit.TAttributeList.TAttribute;

/**
 *
 * <p>Title:�Һŷ�ʽ�����б� </p>
 *
 * <p>Description:�Һŷ�ʽ�����б� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.10.07
 * @version 1.0
 */
public class TComboRegMethod extends TComboBox {
    /**
     * �ֶ�ѡ��ע��
     */
    private String comboFlg;
    /**
     * �õ��ֶ�ѡ��ע��
     * @return String
     */
    public String getComboFlg() {
        return comboFlg;
    }

    /**
     * �����ֶ�ѡ��ע��
     * @param comboFlg String
     */
    public void setComboFlg(String comboFlg) {
        this.comboFlg = comboFlg;
    }

    /**
     * ִ��Module����
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("COMBO_FLG", getTagValue(getComboFlg()));
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
        object.setValue("Tip", "�Һŷ�ʽ");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "reg\\REGRegMethodModule.x";
    }

    public String getModuleMethodName() {
        return "getregmethodCombo";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ComboFlg", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("ComboFlg".equalsIgnoreCase(name)) {
            setComboFlg(value);
            getTObject().setValue("ComboFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
