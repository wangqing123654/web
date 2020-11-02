package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title: ҩ��ԭ�������б�</p>
 *
 * <p>Description: ҩ��ԭ�������б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboINDReason extends TComboBox{
    /**
     * ԭ�����
     */
    private String reasonType;
    /**
     * �õ�ԭ�����
     * @return String
     */
    public String getReasonType() {
        return reasonType;
    }
    /**
     * ����ԭ�����
     * @param reasonType String
     */
    public void setReasonType(String reasonType) {
        this.reasonType = reasonType;
    }
    /**
     * ִ��Module����
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("REASON_TYPE",getTagValue(getReasonType()));
        setModuleParm(parm);
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
        object.setValue("Tip","ҩ��ԭ��");
        object.setValue("TableShowList","id,name");
    }
    public String getModuleName()
    {
        return "ind\\INDReasonModule.x";
    }
    public String getModuleMethodName()
    {
        return "initINDReasonCode";
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
        data.add(new TAttribute("ReasonType","String","","Left"));
    }
    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name,String value)
    {
        if("ReasonType".equalsIgnoreCase(name))
        {
            setReasonType(value);
            getTObject().setValue("ReasonType",value);
            return;
        }
        super.setAttribute(name,value);
    }
}
