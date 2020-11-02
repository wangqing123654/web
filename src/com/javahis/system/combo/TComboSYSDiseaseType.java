package com.javahis.system.combo;

import com.dongyang.ui.*;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: ��Ⱦ������Combo</p>
 *
 * <p>Description: ��Ⱦ������Combo</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-10-20
 * @version 1.0
 */
public class TComboSYSDiseaseType
    extends TComboBox {
    /**
     * �½�����ĳ�ʼֵ
     * @param object TObject
     */
    public void createInit(TObject object)
    {
        if(object == null)
            return;
        object.setValue("Width","81");
        object.setValue("Height","23");
        object.setValue("Text","TButton");
        object.setValue("showID","Y");
        object.setValue("showName","Y");
        object.setValue("showText","N");
        object.setValue("showValue","N");
        object.setValue("showPy1","N");
        object.setValue("showPy2","N");
        object.setValue("Editable","Y");
        object.setValue("Tip","��Ⱦ�����������б�");
        object.setValue("TableShowList","id,name");
    }
    public String getModuleName()
    {
        return "sys\\SYSDiseaseTypeModule.x";
    }
    public String getModuleMethodName()
    {
        return "selectForCombo";
    }
    public String getParmMap()
    {
        return "id:ID;name:NAME;enname:ENNAME";
    }
    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data){
    }

}
