package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;

/**
 *
 * <p>Title: �û��������������б�</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TComboOperatorStation extends TComboBox{
    /**
     * �û����
     */
    private String userID;
    /**
     * �����û����
     * @param userID String
     */
    public void setUserID(String userID)
    {
        this.userID = userID;
    }
    /**
     * �õ��û����
     * @return String
     */
    public String getUserID()
    {
        return userID;
    }
    /**
     * ִ��Module����
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setData("USER_ID",getTagValue(getUserID()));
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
        object.setValue("Text","");
        object.setValue("showID","Y");
        object.setValue("showName","Y");
        object.setValue("showText","N");
        object.setValue("showValue","N");
        object.setValue("showPy1","Y");
        object.setValue("showPy2","Y");
        object.setValue("Editable","Y");
        object.setValue("Tip","����");
        object.setValue("TableShowList","id,name,text");
    }
    /**
     * Module�ļ���
     * @return String
     */
    public String getModuleName()
    {
        return "sys\\SYSOperatorStationModule.x";
    }
    /**
     * Module������
     * @return String
     */
    public String getModuleMethodName()
    {
        return "getComboList";
    }
    /**
     * ����
     * @return String
     */
    public String getParmMap()
    {
        return "id:ID;name:NAME;text:TYPE;enname:ENNAME;Py1:PY1;py2:PY2";
    }
    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data)
    {
        data.add(new TAttribute("UserID","String","","Left"));
    }
    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name,String value)
    {
        if("UserID".equalsIgnoreCase(name))
        {
            setUserID(value);
            getTObject().setValue("UserID",value);
            return;
        }
        super.setAttribute(name,value);
    }
}
