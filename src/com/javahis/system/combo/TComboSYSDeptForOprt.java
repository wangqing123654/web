package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
import jdo.sys.Operator;
/**
 *
 * <p>Title: ���������б�(����ר��)</p>
 *
 * <p>Description: ���������б�(����ר��)</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.05.11
 * @version 1.0
 */
public class TComboSYSDeptForOprt extends TComboBox{
    /**
     * ���ҵȼ�
     */
    private String grade;
    /**
     * ��С����
     */
    private String finalFlg;
    /**
     * ��������
     */
    private String opdFitFlg;
    /**
     * ��������
     */
    private String emgFitFlg;
    /**
     * סԺ����
     */
    private String ipdFitFlg;
    /**
     * ��������
     */
    private String hrmFitFlg;
    /**
     * ��Ա
     */
    private String userID;
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
     * ������С����
     * @param finalFlg boolean '' ȫ�� Y ��С���� N �������¿���
     */
    public void setFinalFlg(String finalFlg)
    {
        this.finalFlg = finalFlg;
    }
    /**
     * �Ƿ�����С����
     * @return String
     */
    public String getFinalFlg()
    {
        return finalFlg;
    }
    /**
     * ������������
     * @param opdFitFlg String
     */
    public void setOpdFitFlg(String opdFitFlg)
    {
        this.opdFitFlg = opdFitFlg;
    }
    /**
     * �õ���������
     * @return String
     */
    public String getOpdFitFlg()
    {
        return opdFitFlg;
    }
    /**
     * ���ü�������
     * @param emgFitFlg String
     */
    public void setEmgFitFlg(String emgFitFlg)
    {
        this.emgFitFlg = emgFitFlg;
    }
    /**
     * �õ���������
     * @return String
     */
    public String getEmgFitFlg()
    {
        return emgFitFlg;
    }
    /**
     * ����סԺ����
     * @param ipdFitFlg String
     */
    public void setIpdFitFlg(String ipdFitFlg)
    {
        this.ipdFitFlg = ipdFitFlg;
    }
    /**
     * �õ�סԺ����
     * @return String
     */
    public String getIpdFitFlg()
    {
        return ipdFitFlg;
    }
    /**
     * ���ý�������
     * @param hrmFitFlg String
     */
    public void setHrmFitFlg(String hrmFitFlg)
    {
        this.hrmFitFlg = hrmFitFlg;
    }
    /**
     * �õ���������
     * @return String
     */
    public String getHrmFitFlg()
    {
        return hrmFitFlg;
    }
    /**
     * ������Ա
     * @param userID String
     */
    public void setUserID(String userID)
    {
        this.userID = userID;
    }
    /**
     * �õ���Ա
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
        parm.setDataN("DEPT_GRADE",getTagValue(getGrade()));
        parm.setDataN("FINAL_FLG",getTagValue(getFinalFlg()));
        parm.setDataN("OPD_FIT_FLG",getTagValue(getOpdFitFlg()));
        parm.setDataN("EMG_FIT_FLG",getTagValue(getEmgFitFlg()));
        parm.setDataN("IPD_FIT_FLG",getTagValue(getIpdFitFlg()));
        parm.setDataN("HRM_FIT_FLG",getTagValue(getHrmFitFlg()));
        parm.setDataN("USER_ID",getTagValue(getUserID()));
        String optRegion = Operator.getRegion();
        if(!"".equals(optRegion))
            parm.setData("REGION_CODE_ALL",optRegion);
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
        object.setValue("Tip","����");
        object.setValue("TableShowList","id,name");
    }
    public String getModuleName()
    {
        return "sys\\SYSDeptModule.x";
    }
    public String getModuleMethodName()
    {
        return "selDeptForOprt";
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
        data.add(new TAttribute("Grade","String","","Left"));
        data.add(new TAttribute("FinalFlg","String","","Left"));
        data.add(new TAttribute("OpdFitFlg","String","","Left"));
        data.add(new TAttribute("EmgFitFlg","String","","Left"));
        data.add(new TAttribute("IpdFitFlg","String","","Left"));
        data.add(new TAttribute("HrmFitFlg","String","","Left"));
        data.add(new TAttribute("UserID","String","","Left"));


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
        if("FinalFlg".equalsIgnoreCase(name))
        {
            setFinalFlg(value);
            getTObject().setValue("FinalFlg",value);
            return;
        }
        if("OpdFitFlg".equalsIgnoreCase(name))
        {
            setOpdFitFlg(value);
            getTObject().setValue("OpdFitFlg",value);
            return;
        }
        if("EmgFitFlg".equalsIgnoreCase(name))
        {
            setEmgFitFlg(value);
            getTObject().setValue("EmgFitFlg",value);
            return;
        }
        if("IpdFitFlg".equalsIgnoreCase(name))
        {
            setIpdFitFlg(value);
            getTObject().setValue("IpdFitFlg",value);
            return;
        }
        if("HrmFitFlg".equalsIgnoreCase(name))
        {
            setHrmFitFlg(value);
            getTObject().setValue("HrmFitFlg",value);
            return;
        }
        if("UserID".equalsIgnoreCase(name))
        {
            setUserID(value);
            getTObject().setValue("UserID",value);
            return;
        }
        super.setAttribute(name,value);
    }}
