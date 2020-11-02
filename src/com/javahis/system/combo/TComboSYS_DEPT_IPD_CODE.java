package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;

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
public class TComboSYS_DEPT_IPD_CODE extends TComboBox {
    /**
     * �ż�ס��
     */
    private String deptType;
    /**
     * ���Ҽ���
     */
    private String classType;
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
        object.setValue("Tip", "����");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmString", "");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "sys\\SYSComboSQL.x";
    }

    public String getModuleMethodName() {
        return "getSysDept";
    }

    public String getParmMap() {
        return "id:DEPT_CODE;name:DEPT_DESC";
    }
    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data){
        data.add(new TAttribute("DeptType", "String", "", "Left"));
        data.add(new TAttribute("ClassType", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String
     * @param value String
     */
    public void setAttribute(String name, String value) {
        if ("DeptType".equalsIgnoreCase(name)) {
            this.setDeptType(value);
            this.getTObject().setValue("DeptType", value);
            return;
        }
        if ("ClassType".equalsIgnoreCase(name)) {
            this.setClassType(value);
            this.getTObject().setValue("ClassType", value);
            return;
        }
        super.setAttribute(name, value);
    }

    /**
     * ִ�в�ѯ����(COMBO����)
     */
    public void onQuery() {
        TParm parm = new TParm();
        if ("ODI".equalsIgnoreCase(this.getDeptType())) {
            parm.setDataN("IPD_FIT_FLG", this.getTagValue("Y"));
            parm.setDataN("CLASSIFY", this.getTagValue(this.getClassType()));
        }
        if ("ODO".equalsIgnoreCase(this.getDeptType())) {
            parm.setDataN("OPD_FIT_FLG", this.getTagValue("Y"));
            parm.setDataN("CLASSIFY", this.getTagValue(this.getClassType()));
        }
        if ("EMG".equalsIgnoreCase(this.getDeptType())) {
            parm.setDataN("EMG_FIT_FLG", this.getTagValue("Y"));
            parm.setDataN("CLASSIFY", this.getTagValue(this.getClassType()));
        }
        if ("HRM".equalsIgnoreCase(this.getDeptType())) {
            parm.setDataN("HRM_FIT_FLG", this.getTagValue("Y"));
            parm.setDataN("CLASSIFY", this.getTagValue(this.getClassType()));
        }
        this.setModuleParm(parm);
        super.onQuery();
    }
    /**
     * �õ����Ҽ���
     * @return String
     */
    public String getClassType() {
        return classType;
    }
    /**
     * �õ��ż�ס��
     * @return String
     */
    public String getDeptType() {
        return deptType;
    }
    /**
     * ���ÿ��Ҽ���
     * @param classType String
     */
    public void setClassType(String classType) {
        this.classType = classType;
    }
    /**
     * �����ż�ס��
     * @param deptType String
     */
    public void setDeptType(String deptType) {
        this.deptType = deptType;
    }

}
