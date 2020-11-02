package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title: Ƶ�������б�</p>
 *
 * <p>Description: Ƶ�������б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboPhaFreq extends TComboBox{
    /**
     * ִ��Module����
     */
    public void onQuery()
    {
//        TParm parm = new TParm();
//        setModuleParm(parm);
        super.onQuery();
    }
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
        object.setValue("Tip","Ƶ��");
        object.setValue("TableShowList","id,name");
    }
    public String getModuleName()
    {
        return "sys\\SYSPhaFreqModule.x";
    }
    public String getModuleMethodName()
    {
        return "selectdataForCombo";
    }
    public String getParmMap()
    {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }
    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data){
//        data.add(new TAttribute("Grade","String","","Left"));
    }
//    /**
//     * ��������
//     * @param name String ������
//     * @param value String ����ֵ
//     */
//    public void setAttribute(String name,String value)
//    {
//        if("Grade".equalsIgnoreCase(name))
//        {
//            setGrade(value);
//            getTObject().setValue("Grade",value);
//            return;
//        }
//        super.setAttribute(name,value);
//    }
}
