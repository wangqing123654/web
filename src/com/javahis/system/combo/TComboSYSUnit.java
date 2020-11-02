package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title: ������λ�����б�</p>
 *
 * <p>Description: ������λ�����б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.2.4
 * @version 1.0
 */
public class TComboSYSUnit extends TComboBox{
    /**
     * ҩƷ������λע��
     */
    private String mediFlg;
    /**
     * ���õ�λע��
     */
    private String disposeFlg;
    /**
     * ����װע��
     */
    private String packageFlg;
    /**
     * ����ע��
     */
    private String otherFlg;
    /**
     * ����ҩƷ������λע��
     * @param mediFlg String
     */
    public void setMediFlg(String mediFlg)
    {
        this.mediFlg = mediFlg;
    }
    /**
     * �õ�ҩƷ������λע��
     * @return String
     */
    public String getMediFlg()
    {
        return mediFlg;
    }
    /**
     * ���ô��õ�λע��
     * @param disposeFlg String
     */
    public void setDisposeFlg(String disposeFlg){
        this.disposeFlg = disposeFlg;
    }
    /**
     * �õ����õ�λע��
     * @return String
     */
    public String getDisposeFlg(){
        return disposeFlg;
    }
    /**
     * ��������װע��
     * @param packageFlg String
     */
    public void setPackageFlg(String packageFlg){
        this.packageFlg = packageFlg;
    }
    /**
     * �õ�����װע��
     * @return String
     */
    public String getPackageFlg(){
        return packageFlg;
    }
    /**
     * ��������ע��
     * @param otherFlg String
     */
    public void setOtherFlg(String otherFlg){
        this.otherFlg = otherFlg;
    }
    /**
     * �õ�����ע��
     * @return String
     */
    public String getOtherFlg(){
        return otherFlg;
    }
    /**
     * ִ��Module����
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("MEDI_FLG",getTagValue(getMediFlg()));
        parm.setDataN("DISPOSE_FLG",getTagValue(getDisposeFlg()));
        parm.setDataN("PACKAGE_FLG",getTagValue(getPackageFlg()));
        parm.setDataN("OTHER_FLG",getTagValue(getOtherFlg()));
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
        object.setValue("Tip","������λ");
        object.setValue("TableShowList","id,name");
    }
    public String getModuleName()
    {
        return "sys\\SYSUnitModule.x";
    }
    public String getModuleMethodName()
    {
        return "initSYSUnit";
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
        data.add(new TAttribute("MediFlg","String","","Left"));
        data.add(new TAttribute("DisposeFlg","String","","Left"));
        data.add(new TAttribute("PackageFlg","String","","Left"));
        data.add(new TAttribute("OtherFlg","String","","Left"));


    }
    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name,String value)
    {
        if("MediFlg".equalsIgnoreCase(name))
        {
            setMediFlg(value);
            getTObject().setValue("MediFlg",value);
            return;
        }
        if("DisposeFlg".equalsIgnoreCase(name))
        {
            setDisposeFlg(value);
            getTObject().setValue("DisposeFlg",value);
            return;
        }
        if("PackageFlg".equalsIgnoreCase(name))
        {
            setPackageFlg(value);
            getTObject().setValue("PackageFlg",value);
            return;
        }
        if("OtherFlg".equalsIgnoreCase(name))
        {
            setOtherFlg(value);
            getTObject().setValue("OtherFlg",value);
            return;
        }
        super.setAttribute(name,value);
    }
}
