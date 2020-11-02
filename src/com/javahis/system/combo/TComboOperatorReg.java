package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TCM_Transform;
import java.sql.Timestamp;
import jdo.sys.Operator;
/**
 *
 * <p>Title:�ż���������Ա�����б� </p>
 *
 * <p>Description:�ż���������Ա�����б� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.28
 * @version 1.0
 */
public class TComboOperatorReg extends TComboBox{
    /**
     * ����
     */
    private String region;
    /**
     * �ż���
     */
    private String admType;
    /**
     * ��������
     */
    private String admDate;
    /**
     * ʱ��
     */
    private String sessionCode;
    /**
     * ����
     */
    private String deptCode;
    /**
     * ����
     */
    private String clinicroomNo;
    /**
     * �ű�
     */
    private String clinicType;
    /**
     * ��������
     * @param region String
     */
    public void setRegion(String region)
    {
        this.region = region;
    }
    /**
     * �õ�����
     * @return String
     */
    public String getRegion()
    {
        return region;
    }
    /**
     * �����ż���
     * @param admType String
     */
    public void setAdmType(String admType)
    {
        this.admType = admType;
    }
    /**
     * �õ��ż���
     * @return String
     */
    public String getAdmType()
    {
        return admType;
    }
    /**
     * ����ʱ��
     * @param sessionCode String
     */
    public void setSessionCode(String sessionCode)
    {
        this.sessionCode = sessionCode;
    }
    /**
     * �õ�ʱ��
     * @return String
     */
    public String getSessionCode()
    {
        return sessionCode;
    }
    /**
     * ���ó�������
     * @param admDate String
     */
    public void setAdmDate(String admDate)
    {
        this.admDate = admDate;
    }
    /**
     * �õ���������
     * @return String
     */
    public String getAdmDate()
    {
        return admDate;
    }
    /**
     * ���ÿ���
     * @param deptCode String
     */
    public void setDeptCode(String deptCode)
    {
        this.deptCode = deptCode;
    }
    /**
     * �õ�����
     * @return String
     */
    public String getDeptCode()
    {
        return deptCode;
    }
    /**
     * ��������
     * @param clinicroomNo String
     */
    public void setClinicroomNo(String clinicroomNo)
    {
        this.clinicroomNo = clinicroomNo;
    }
    /**
     * �õ�����
     * @return String
     */
    public String getClinicroomNo()
    {
        return clinicroomNo;
    }
    /**
     * ���úű�
     * @param clinicType String
     */
    public void setClinicType(String clinicType)
    {
        this.clinicType = clinicType;
    }
    /**
     * �õ��ű�
     * @return String
     */
    public String getClinicType()
    {
        return clinicType;
    }
    /**
     * ִ��Module����
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("REGION_CODE",getTagValue(getRegion()));
        parm.setDataN("ADM_TYPE",getTagValue(getAdmType()));
        parm.setDataN("SESSION_CODE",getTagValue(getSessionCode()));
        Object value =getTagValue(getAdmDate());
        if(value instanceof Timestamp)
            parm.setDataN("ADM_DATE",StringTool.getString(TCM_Transform.getTimestamp(value),"yyyyMMdd") );
        else
        parm.setDataN("ADM_DATE",getTagValue(getAdmDate()));
        parm.setDataN("DEPT_CODE",getTagValue(getDeptCode()));
        parm.setDataN("CLINICROOM_NO",getTagValue(getClinicroomNo()));
        parm.setDataN("CLINICTYPE_CODE",getTagValue(getClinicType()));
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
        object.setValue("Tip","�ż���������Ա");
        object.setValue("TableShowList","id,name");
        object.setValue("ModuleParmString","");
        object.setValue("ModuleParmTag","");
    }
    public String getModuleName()
    {
        return "sys\\SYSOperatorModule.x";
    }
    public String getModuleMethodName()
    {
        return "initoperatreg";
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
        data.add(new TAttribute("Region","String","","Left"));
        data.add(new TAttribute("AdmType","String","","Left"));
        data.add(new TAttribute("SessionCode","String","","Left"));
        data.add(new TAttribute("AdmDate","String","","Left"));
        data.add(new TAttribute("DeptCode","String","","Left"));
        data.add(new TAttribute("ClinicroomNo","String","","Left"));
        data.add(new TAttribute("ClinicType","String","","Left"));
    }
    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name,String value)
    {
        if("Region".equalsIgnoreCase(name))
        {
            setRegion(value);
            getTObject().setValue("Region",value);
            return;
        }
        if("AdmType".equalsIgnoreCase(name))
        {
            setAdmType(value);
            getTObject().setValue("AdmType",value);
            return;
        }
        if("SessionCode".equalsIgnoreCase(name))
        {
            setSessionCode(value);
            getTObject().setValue("SessionCode",value);
            return;
        }
        if("AdmDate".equalsIgnoreCase(name))
        {
            setAdmDate(value);
            getTObject().setValue("AdmDate",value);
            return;
        }
        if("DeptCode".equalsIgnoreCase(name))
        {
            setDeptCode(value);
            getTObject().setValue("DeptCode",value);
            return;
        }
        if("ClinicroomNo".equalsIgnoreCase(name))
        {
            setClinicroomNo(value);
            getTObject().setValue("ClinicroomNo",value);
            return;
        }
        if("ClinicType".equalsIgnoreCase(name))
        {
            setClinicType(value);
            getTObject().setValue("ClinicType",value);
            return;
        }

        super.setAttribute(name,value);
    }
}
