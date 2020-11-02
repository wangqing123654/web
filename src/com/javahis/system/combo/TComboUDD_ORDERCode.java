package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title: סԺ�����������б�</p>
 *
 * <p>Description: סԺ�����������б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.01.06
 * @version 1.0
 */
public class TComboUDD_ORDERCode extends TComboBox{
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
        object.setValue("Tip","סԺ������");
        object.setValue("TableShowList","id,name");
        object.setValue("ModuleParmTag","");
    }
    public String getModuleName()
    {
        return "sys\\UDD_ORDERModule.x";
    }
    public String getModuleMethodName()
    {
        return "qeuryUddOrder";
    }
    public String getParmMap()
    {
        return "id:ID;name:NAME";
    }
    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data){
    }
}
