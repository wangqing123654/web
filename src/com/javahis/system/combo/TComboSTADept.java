package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: ҽ��ͳ��ϵͳ �м���տ��� combo</p>
 *
 * <p>Description: ҽ��ͳ��ϵͳ �м���տ��� combo</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-18
 * @version 1.0
 */
public class TComboSTADept
    extends TComboBox {
    /**
     * ���ҵȼ�
     */
    private String grade;
    /**
     * ���ÿ��ҵȼ�
     * @param grade String
     */
    public void setGrade(String grade)
    {
        this.grade = grade;
    }
    /**
     * �õ����ҵȼ�
     * @return String
     */
    public String getGrade()
    {
        return grade;
    }

    /**
     * ִ��Module����
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("DEPT_LEVEL",getTagValue(getGrade()));
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
        object.setValue("showPy1", "Y");
        object.setValue("showPy2", "Y");
        object.setValue("Editable", "Y");
        object.setValue("Tip", "���տ���");
        object.setValue("TableShowList", "id,name");
    }
    public String getModuleName()
    {
        return "sys\\SYSSTADeptModule.x";
    }
    public String getModuleMethodName()
    {
        return "initData";
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
        data.add(new TAttribute("Grade","String","","Left"));//���ҵȼ�����
    }
    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name,String value)
    {
        if("Grade".equalsIgnoreCase(name))
        {
            setGrade(value);
            getTObject().setValue("Grade",value);
            return;
        }
        super.setAttribute(name,value);
    }

}
