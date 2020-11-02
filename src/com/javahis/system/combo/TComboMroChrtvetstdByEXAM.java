package com.javahis.system.combo;

import com.dongyang.ui.*;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: ������˱�׼ </p>
 *
 * <p>Description: ʹ��EXAMINE_CODE���� </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: Bluecore </p>
 *
 * @author wanglong 20130909
 * @version 1.0
 */
public class TComboMroChrtvetstdByEXAM
    extends TComboBox {
    /**
     * ��˱��
     */
    private String examineCode;

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
        object.setValue("Tip", "������˱�׼");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmString", "");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "sys\\SYSMROComboModule.x";
    }

    public String getModuleMethodName() {
        return "getMroChrtvetstd";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;text:TEXT;py1:PY1;py2:PY2";
    }

    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ExamineCode", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String
     * @param value String
     */
    public void setAttribute(String name, String value) {
        if ("ExamineCode".equalsIgnoreCase(name)) {
            setExamineCode(value);
            getTObject().setValue("ExamineCode", value);
            return;
        }
        super.setAttribute(name, value);
    }

    /**
     * ִ�в�ѯ����(COMBO����)
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("EXAMINE_CODE", this.getTagValue(this.getExamineCode()));
        this.setModuleParm(parm);
        super.onQuery();
    }

    /**
     * ������˱���
     * @return
     */
    public String getExamineCode() {
        return examineCode;
    }

    /**
     * �õ���˱���
     * @param examineCode
     */
    public void setExamineCode(String examineCode) {
        this.examineCode = examineCode;
    }
}
