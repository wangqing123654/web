package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;

/**
 *
 * <p>Title: ��λ�����б�</p>
 *
 * <p>Description: ��λ�����б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author ehui 20081012
 * @version 1.0
 */
public class TComboUnit
    extends TComboBox {

    /**
     * ����ע�� DISPOSE_FLG
     */
    private String disposeFlg;
    /**
     * ��ҩע�� PACKAGE_FLG
     */
    private String packageFlg;
    /**
     * ����ע��
     */
    private String otherFlg;
    /**
     * ҩƷע��
     */
    private String phaFlg;
    /**
     * ���ô���ע��
     * @param disposeFlg String
     */
    public void setDisposeFlg(String disposeFlg) {
        this.disposeFlg = disposeFlg;

    }

    /**
     * �õ�����ע��
     * @return disposeFlg String
     */
    public String getDisposeFlg() {
        return this.disposeFlg;
    }

    /**
     * ���ð�ע��
     * @param packageFlg String
     */
    public void setPackageFlg(String packageFlg) {
        this.packageFlg = packageFlg;
    }

    /**
     * �õ���ע��
     * @return pachageFlg String
     */
    public String getPackageFlg() {
        return this.packageFlg;
    }

    /**
     * ��������ע��
     * @param otherFlg String
     */
    public void setOtherFlg(String otherFlg) {
        this.otherFlg = otherFlg;
    }

    /**
     * �õ�����ע��
     * @return otherFlg String
     */
    public String getOtherFlg() {
        return this.otherFlg;
    }

    /**
     * ����ҩƷע��
     * @param phaFlg String
     */
    public void setPhaFlg(String phaFlg) {
        this.phaFlg = phaFlg;

    }

    /**
     * �õ�ҩƷע��
     * @return phaFlg String
     */
    public String getPhaFlg() {
        return this.phaFlg;
    }

    /**
     * ִ��Module����
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("DISPOSE_FLG", getTagValue(getDisposeFlg()));
        parm.setDataN("PACKAGE_FLG", getTagValue(getPackageFlg()));
        parm.setDataN("OTHER_FLG", getTagValue(getOtherFlg()));
        parm.setDataN("PHA_FLG", getTagValue(getPhaFlg()));
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
        object.setValue("Tip", "��λ");
        object.setValue("TableShowList", "id,name");
    }

    public String getModuleName() {
        return "sys\\SYSUnitModule.x";
    }

    public String getModuleMethodName() {
        return "selectdataForCombolExa";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * �õ������б�
     * @return TAttributeList
     */
    public TAttributeList getAttributes() {
        TAttributeList data = new TAttributeList();
        data.add(new TAttribute("Tag", "String", "", "Left"));
        data.add(new TAttribute("Visible", "boolean", "Y", "Center"));
        data.add(new TAttribute("Enabled", "boolean", "Y", "Center"));
        data.add(new TAttribute("Editable", "boolean", "Y", "Center"));
        data.add(new TAttribute("Name", "String", "", "Left"));
        data.add(new TAttribute("Text", "String", "", "Left"));
        data.add(new TAttribute("Tip", "String", "", "Left"));
        data.add(new TAttribute("controlClassName", "String", "", "Left"));
        data.add(new TAttribute("ShowID", "boolean", "Y", "Center"));
        data.add(new TAttribute("ShowName", "boolean", "N", "Center"));
        data.add(new TAttribute("ShowText", "boolean", "Y", "Center"));
        data.add(new TAttribute("ShowValue", "boolean", "N", "Center"));
        data.add(new TAttribute("ShowPy1", "boolean", "N", "Center"));
        data.add(new TAttribute("ShowPy2", "boolean", "N", "Center"));
        data.add(new TAttribute("TableShowList", "String", "", "Left"));
        data.add(new TAttribute("Action", "String", "", "Left"));
        data.add(new TAttribute("SelectedAction", "String", "", "Left"));
        data.add(new TAttribute("DisposeFlg", "String", "", "Left"));
        data.add(new TAttribute("PackageFlg", "String", "", "Left"));
        data.add(new TAttribute("OtherFlg", "String", "", "Left"));
        data.add(new TAttribute("PhaFlg", "String", "", "Left"));
        data.add(new TAttribute("X", "int", "0", "Right"));
        data.add(new TAttribute("Y", "int", "0", "Right"));
        data.add(new TAttribute("Width", "int", "0", "Right"));
        data.add(new TAttribute("Height", "int", "0", "Right"));
        return data;
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("DisposeFlg".equalsIgnoreCase(name)) {
            setDisposeFlg(value);
            getTObject().setValue("DisposeFlg", value);
            setModuleMethodName("selectdataForCombolExa");
            return;
        }
        if ("PackageFlg".equalsIgnoreCase(name)) {
            setPackageFlg(value);
            getTObject().setValue("PackageFlg", value);
            return;
        }
        if ("OtherFlg".equalsIgnoreCase(name)) {
            setOtherFlg(value);
            getTObject().setValue("OtherFlg", value);
            return;
        }
        if ("PhaFlg".equalsIgnoreCase(name)) {
            setOtherFlg(value);
            getTObject().setValue("PhaFlg", value);
            setModuleMethodName("selectdataForCombolMed");
            return;
        }
        super.setAttribute(name, value);
    }

}
