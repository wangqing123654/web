package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;

/**
 *
 * <p>Title: �ṹ���������뵥�����б�</p>
 *
 * <p>Description: �ṹ���������뵥�����б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.06.05
 * @version 1.0
 */
public class TComboEMRTemplet
    extends TComboBox {
    /**
     * ִ��Module����
     */
    public void onQuery() {
        TParm parm = new TParm();
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
        object.setValue("showPy1", "N");
        object.setValue("showPy2", "N");
        object.setValue("Editable", "Y");
        object.setValue("Tip", "�ṹ���������뵥");
        object.setValue("TableShowList", "id,name");
    }

    public String getModuleName() {
        return "emr\\EMRTempletModule.x";
    }

    public String getModuleMethodName() {
        return "initCombo";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME ";
    }

    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        super.setAttribute(name, value);
    }
}
