package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TComboNode;
import com.dongyang.data.TParm;

/**
 * <p>Title: ҽ������</p>
 *
 * <p>Description: ��Լ������COMBO</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class TComboSYS_CompanyCode extends TComboBox {
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
        object.setValue("Tip", "��Լ��λ");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmString", "HOSP_AREA:HIS");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "sys\\SYSComboSQL.x";
    }

    public String getModuleMethodName() {
        return "getGroupList";
    }

    public String getParmMap() {
        return "id:COMPANY_CODE;name:COMPANY_DESC";
    }
    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data){
  }
    /**
     * ��������
     * @param name String
     * @param value String
     */
    public void setAttribute(String name, String value) {
        super.setAttribute(name, value);
    }

    /**
     * ִ�в�ѯ����(COMBO����)
     */
    public void onQuery() {
        super.onQuery();
    }

}
