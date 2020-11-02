package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;

/**
 *
 * <p>Title:ҽ����Ϣ�����б� </p>
 *
 * <p>Description:ҽ����Ϣ�����б� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2008.11.03
 * @version JavaHis 1.0
 */
public class TComboSYS_FEECode extends TComboBox {
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
        object.setValue("Tip", "ҽ����Ϣ");
        object.setValue("TableShowList", "id,name,py1");
        object.setValue("ModuleParmString", "");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "sys\\SYSComboSQL.x";
    }

    public String getModuleMethodName() {
        return "getOrderCode";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;py1:PY1";
    }
    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data){
    }
}
