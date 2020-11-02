package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;

/**
 *
 * <p>Title: ����ҩƷ�ȼ����������б�</p>
 *
 * <p>Description: ����ҩƷ�ȼ����������б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboSYSCtrlDrugClassCode
    extends TComboBox {
    /**
     * ר�ô���ǩע��
     */
    private String prnspcformFlg; // PRNSPCFORM_FLG
    /**
     * ����ҩע��
     */
    private String ctrlFlg; //CTRL_FLG
    /**
     * ������ҩע��
     */
    private String narcoticFlg; //NARCOTIC_FLG
    /**
     * ������ҩע��
     */
    private String toxicandFlg; //TOXICANT_FLG
    /**
     * ������ҩע��
     */
    private String psychophaFlg; //PSYCHOPHA_FLG
    /**
     * ������ҩע��
     */
    private String radiaFlg; //RADIA_FLG
    /**
     * ������ҩע��
     */
    private String testDrugFlg; //TEST_DRUG_FLG
    /**
     * ������ҩע��
     */
    private String antisepticFlg; //ANTISEPTIC_FLG
    /**
     * ������ע��
     */
    private String antibioticFlg; //ANTIBIOTIC_FLG
    /**
     * ȫ����Ӫ��ע��ע��
     */
    private String tpnFlg; //TPN_FLG
    /**
     * ����ר�ô���ǩע��
     * @param prnspcformFlg String
     */
    public void setPrnspcformFlg(String prnspcformFlg) {
        this.prnspcformFlg = prnspcformFlg;
    }

    /**
     * �õ�ר�ô���ǩע��
     * @return String
     */
    public String getPrnspcformFlg() {
        return prnspcformFlg;
    }

    /**
     * ���ö���ҩע��
     * @param ctrlFlg String
     */
    public void setCtrlFlg(String ctrlFlg) {
        this.ctrlFlg = ctrlFlg;
    }

    /**
     * �õ�����ҩע��
     * @return String
     */
    public String getCtrlFlg() {
        return ctrlFlg;
    }

    /**
     * ����������ҩע��
     * @param narcoticFlg String
     */
    public void setNarcoticFlg(String narcoticFlg) {
        this.narcoticFlg = narcoticFlg;
    }

    /**
     * �õ�������ҩע��
     * @return String
     */
    public String getNarcoticFlg() {
        return narcoticFlg;
    }

    /**
     * ���ö�����ҩע��
     * @param toxicandFlg String
     */
    public void setToxicandFlg(String toxicandFlg) {
        this.toxicandFlg = toxicandFlg;
    }

    /**
     * �õ�������ҩע��
     * @return String
     */
    public String getToxicandFlg() {
        return toxicandFlg;
    }

    /**
     * ���þ�����ҩע��
     * @param psychophaFlg String
     */
    public void setPsychophaFlg(String psychophaFlg) {
        this.psychophaFlg = psychophaFlg;
    }

    /**
     * �õ�������ҩע��
     * @return String
     */
    public String getPsychophaFlg() {
        return psychophaFlg;
    }

    /**
     * ���û�����ҩע��
     * @param radiaFlg String
     */
    public void setRadiaFlg(String radiaFlg) {
        this.radiaFlg = radiaFlg;
    }

    /**
     * �õ�������ҩע��
     * @return String
     */
    public String getRadiaFlg() {
        return radiaFlg;
    }

    /**
     * ����������ҩע��
     * @param testDrugFlg String
     */
    public void setTestDrugFlg(String testDrugFlg) {
        this.testDrugFlg = testDrugFlg;
    }

    /**
     * �õ�������ҩע��
     * @return String
     */
    public String getTestDrugFlg() {
        return testDrugFlg;
    }

    /**
     * ���ÿ�����ҩע��
     * @param antisepticFlg String
     */
    public void setAntisepticFlg(String antisepticFlg) {
        this.antisepticFlg = antisepticFlg;
    }

    /**
     * �õ�������ҩע��
     * @return String
     */
    public String getAntisepticFlg() {
        return antisepticFlg;
    }

    /**
     * ���ÿ�����ע��
     * @param antibioticFlg String
     */
    public void setAntibioticFlg(String antibioticFlg) {
        this.antibioticFlg = antibioticFlg;
    }

    /**
     * �õ�������ע��
     * @return String
     */
    public String getAntibioticFlg() {
        return antibioticFlg;
    }

    /**
     * ȫ����Ӫ��ע��ע��
     * @param tpnFlg String
     */
    public void setTpnFlg(String tpnFlg) {
        this.tpnFlg = tpnFlg;
    }

    /**
     * ȫ����Ӫ��ע��ע��
     * @return String
     */
    public String getTpnFlg() {
        return tpnFlg;
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
        parm.setDataN("PRNSPCFORM_FLG", getTagValue(getPrnspcformFlg()));
        parm.setDataN("CTRL_FLG", getTagValue(getCtrlFlg()));
        parm.setDataN("NARCOTIC_FLG", getTagValue(getNarcoticFlg()));
        parm.setDataN("TOXICANT_FLG", getTagValue(getToxicandFlg()));
        parm.setDataN("PSYCHOPHA_FLG", getTagValue(getPsychophaFlg()));
        parm.setDataN("RADIA_FLG", getTagValue(getRadiaFlg()));
        parm.setDataN("TEST_DRUG_FLG", getTagValue(getTestDrugFlg()));
        parm.setDataN("ANTISEPTIC_FLG", getTagValue(getAntisepticFlg()));
        parm.setDataN("ANTIBIOTIC_FLG", getTagValue(getAntibioticFlg()));
        parm.setDataN("TPN_FLG", getTagValue(getTpnFlg()));
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
        object.setValue("Tip", "����ҩƷ�ȼ�����");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmString", "");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "sys\\SYSCtrlDrugClassModule.x";
    }

    public String getModuleMethodName() {
        return "initCtrlDrugClassCode";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("PrnspcformFlg", "String", "", "Left")); //���ӿ���ɸѡ����
        data.add(new TAttribute("CtrlFlg", "String", "", "Left"));
        data.add(new TAttribute("NarcoticFlg", "String", "", "Left"));
        data.add(new TAttribute("ToxicandFlg", "String", "", "Left"));
        data.add(new TAttribute("PsychophaFlg", "String", "", "Left"));
        data.add(new TAttribute("RadiaFlg", "String", "", "Left"));
        data.add(new TAttribute("TestDrugFlg", "String", "", "Left"));
        data.add(new TAttribute("AntisepticFlg", "String", "", "Left"));
        data.add(new TAttribute("AntibioticFlg", "String", "", "Left"));
        data.add(new TAttribute("TpnFlg", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("PrnspcformFlg".equalsIgnoreCase(name)) {
            setPrnspcformFlg(value);
            getTObject().setValue("PrnspcformFlg", value);
            return;
        }
        if ("CtrlFlg".equalsIgnoreCase(name)) {
            setCtrlFlg(value);
            getTObject().setValue("CtrlFlg", value);
            return;
        }
        if ("NarcoticFlg".equalsIgnoreCase(name)) {
            setNarcoticFlg(value);
            getTObject().setValue("NarcoticFlg", value);
            return;
        }
        if ("ToxicandFlg".equalsIgnoreCase(name)) {
            setToxicandFlg(value);
            getTObject().setValue("ToxicandFlg", value);
            return;
        }
        if ("PsychophaFlg".equalsIgnoreCase(name)) {
            setPsychophaFlg(value);
            getTObject().setValue("PsychophaFlg", value);
            return;
        }
        if ("RadiaFlg".equalsIgnoreCase(name)) {
            setRadiaFlg(value);
            getTObject().setValue("RadiaFlg", value);
            return;
        }
        if ("TestDrugFlg".equalsIgnoreCase(name)) {
            setTestDrugFlg(value);
            getTObject().setValue("TestDrugFlg", value);
            return;
        }
        if ("AntisepticFlg".equalsIgnoreCase(name)) {
            setAntisepticFlg(value);
            getTObject().setValue("AntisepticFlg", value);
            return;
        }
        if ("AntibioticFlg".equalsIgnoreCase(name)) {
            setAntibioticFlg(value);
            getTObject().setValue("AntibioticFlg", value);
            return;
        }
        if ("TpnFlg".equalsIgnoreCase(name)) {
            setTpnFlg(value);
            getTObject().setValue("TpnFlg", value);
            return;
        }

        super.setAttribute(name, value);
    }
}
