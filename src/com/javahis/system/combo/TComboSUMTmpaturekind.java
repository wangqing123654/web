package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title: �������������б�</p>
 *
 * <p>Description: �������������б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboSUMTmpaturekind extends TComboBox{
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
        object.setValue("showPy1","Y");
        object.setValue("showPy2","Y");
        object.setValue("Editable","Y");
        object.setValue("Tip","��������");
        object.setValue("TableShowList","id,name");
        object.setValue("ModuleParmTag","");
    }
    public String getModuleName()
    {
        return "sum\\SUMTmpaturekindModule.x";
    }
    public String getModuleMethodName()
    {
        return "initSUMTmpaturekind";
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
