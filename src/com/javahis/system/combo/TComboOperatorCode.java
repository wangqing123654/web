package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;

/**
 *
 * <p>Title:��Ա�����б� </p>
 *
 * <p>Description:��Ա�����б� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.04
 * @version 1.0
 */
public class TComboOperatorCode
    extends TComboBox {
    /**
     * ����
     */
    private String dept;
    /**
     * ְ�����
     */
    private String posType;
    /**
     * ���ҵȼ�
     */
    private String deptGrade;
    /**
     * ���ҹ���
     */
    private String classify;
    /**
     * ��С����
     */
    private String finalFlg;
    /**
     * ��������
     */
    private String opdFitFlg;
    /**
     * ��������
     */
    private String emgFitFlg;
    /**
     * סԺ����
     */
    private String ipdFitFlg;
    /**
     * ��������
     */
    private String hrmFitFlg;

    /**
     * ���ÿ���
     * @param dept String
     */
    public void setDept(String dept) {
        this.dept = dept;
    }

    /**
     * �õ�����
     * @return String
     */
    public String getDept() {
        return dept;
    }

    /**
     * ����ְ�����
     * @param posType String
     */
    public void setPosType(String posType) {
        this.posType = posType;
    }

    /**
     * �õ�ְ�����
     * @return String
     */
    public String getPosType() {
        return posType;
    }

    /**
     * ���ÿ��ҵȼ�
     * @param deptGrade String
     */
    public void setDeptGrade(String deptGrade) {
        this.deptGrade = deptGrade;
    }

    /**
     * �õ����ҵȼ�
     * @return String
     */
    public String getDeptGrade() {
        return deptGrade;
    }

    /**
     * ���ÿ��ҹ���
     * @param classify String
     */
    public void setClassify(String classify) {
        this.classify = classify;
    }

    /**
     * �õ����ҹ���
     * @return String
     */
    public String getClassify() {
        return classify;
    }

    /**
     * ������С����
     * @param finalFlg boolean '' ȫ�� Y ��С���� N �������¿���
     */
    public void setFinalFlg(String finalFlg) {
        this.finalFlg = finalFlg;
    }

    /**
     * �Ƿ�����С����
     * @return String
     */
    public String getFinalFlg() {
        return finalFlg;
    }

    /**
     * ������������
     * @param opdFitFlg String
     */
    public void setOpdFitFlg(String opdFitFlg) {
        this.opdFitFlg = opdFitFlg;
    }

    /**
     * �õ���������
     * @return String
     */
    public String getOpdFitFlg() {
        return opdFitFlg;
    }

    /**
     * ���ü�������
     * @param emgFitFlg String
     */
    public void setEmgFitFlg(String emgFitFlg) {
        this.emgFitFlg = emgFitFlg;
    }

    /**
     * �õ���������
     * @return String
     */
    public String getEmgFitFlg() {
        return emgFitFlg;
    }

    /**
     * ����סԺ����
     * @param ipdFitFlg String
     */
    public void setIpdFitFlg(String ipdFitFlg) {
        this.ipdFitFlg = ipdFitFlg;
    }

    /**
     * �õ�סԺ����
     * @return String
     */
    public String getIpdFitFlg() {
        return ipdFitFlg;
    }

    /**
     * ���ý�������
     * @param hrmFitFlg String
     */
    public void setHrmFitFlg(String hrmFitFlg) {
        this.hrmFitFlg = hrmFitFlg;
    }

    /**
     * �õ���������
     * @return String
     */
    public String getHrmFitFlg() {
        return hrmFitFlg;
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
        parm.setDataN("DEPT_CODE", getTagValue(getDept()));
        parm.setDataN("DEPT_GRADE", getTagValue(getDeptGrade()));
        parm.setDataN("POS_TYPE", getTagValue(getPosType()));
        parm.setDataN("CLASSIFY", getTagValue(getClassify()));
        parm.setDataN("FINAL_FLG", getTagValue(getFinalFlg()));
        parm.setDataN("OPD_FIT_FLG", getTagValue(getOpdFitFlg()));
        parm.setDataN("EMG_FIT_FLG", getTagValue(getEmgFitFlg()));
        parm.setDataN("IPD_FIT_FLG", getTagValue(getIpdFitFlg()));
        parm.setDataN("HRM_FIT_FLG", getTagValue(getHrmFitFlg()));
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
        object.setValue("Tip", "��Ա");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmString", "");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "sys\\SYSOperatorModule.x";
    }

    public String getModuleMethodName() {
        return "initoperatorcode";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("Dept", "String", "", "Left")); //���ӿ���ɸѡ����
        data.add(new TAttribute("PosType", "String", "", "Left"));
        data.add(new TAttribute("DeptGrade", "String", "", "Left"));
        data.add(new TAttribute("Classify", "String", "", "Left"));
        data.add(new TAttribute("FinalFlg", "String", "", "Left"));
        data.add(new TAttribute("OpdFitFlg", "String", "", "Left"));
        data.add(new TAttribute("EmgFitFlg", "String", "", "Left"));
        data.add(new TAttribute("IpdFitFlg", "String", "", "Left"));
        data.add(new TAttribute("HrmFitFlg", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("Dept".equalsIgnoreCase(name)) {
            setDept(value);
            getTObject().setValue("Dept", value);
            return;
        }
        if ("DeptGrade".equalsIgnoreCase(name)) {
            setDeptGrade(value);
            getTObject().setValue("DeptGrade", value);
            return;
        }
        if ("PosType".equalsIgnoreCase(name)) {
            setPosType(value);
            getTObject().setValue("PosType", value);
            return;
        }
        if ("Classify".equalsIgnoreCase(name)) {
            setClassify(value);
            getTObject().setValue("Classify", value);
            return;
        }
        if ("FinalFlg".equalsIgnoreCase(name)) {
            setFinalFlg(value);
            getTObject().setValue("FinalFlg", value);
            return;
        }
        if ("OpdFitFlg".equalsIgnoreCase(name)) {
            setOpdFitFlg(value);
            getTObject().setValue("OpdFitFlg", value);
            return;
        }
        if ("EmgFitFlg".equalsIgnoreCase(name)) {
            setEmgFitFlg(value);
            getTObject().setValue("EmgFitFlg", value);
            return;
        }
        if ("IpdFitFlg".equalsIgnoreCase(name)) {
            setIpdFitFlg(value);
            getTObject().setValue("IpdFitFlg", value);
            return;
        }
        if ("HrmFitFlg".equalsIgnoreCase(name)) {
            setHrmFitFlg(value);
            getTObject().setValue("HrmFitFlg", value);
            return;
        }

        super.setAttribute(name, value);
    }
}
