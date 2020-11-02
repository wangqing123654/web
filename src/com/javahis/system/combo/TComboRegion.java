package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TComboNode;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:���������б� </p>
 *
 * <p>Description: ���������б�</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2008.08.25
 * @version 1.0
 */
public class TComboRegion extends TComboBox{
    /**
     * ����Ժ��
     */
    private boolean allRegionFlg;
    /**
     * ��������Ժ��
     * @param allRegionFlg boolean true  ��������Ժ�� flase ����������Ժ��
     */
    public void setAllRegionFlg(boolean allRegionFlg)
    {
        this.allRegionFlg = allRegionFlg;
    }
    /**
     * �Ƿ�������Ժ��
     * @return boolean
     */
    public boolean isAllRegionFlg()
    {
        return allRegionFlg;
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
        object.setValue("ModuleParmString","");
        object.setValue("ModuleParmTag","");
    }
    public String getModuleName()
    {
        return "sys\\SYSRegionModule.x";
    }
    public String getModuleMethodName()
    {
        return "initregioncode";
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
        data.add(new TAttribute("AllRegionFlg","String","","Left"));
    }
   /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name,String value)
    {
        if("AllRegionFlg".equalsIgnoreCase(name))
        {
            setAllRegionFlg(StringTool.getBoolean(value));
            getTObject().setValue("AllRegionFlg",value);
            return;
        }
        super.setAttribute(name,value);
    }
    /**
     * �������󣬲���һ�����ݣ����ұ�������AllRegionFlgѡ��Ϊ��ʱ��ʾ
     * wangl 2008.09.03
     */
    public void onQuery() {
        super.onQuery();
        TParm parm = new TParm();
        String optRegion = Operator.getRegion();
        if (!"".equals(optRegion))
            parm.setData("REGION_CODE_ALL", optRegion);
        setModuleParm(parm);
        super.onQuery();
        if (isAllRegionFlg()) {
            TComboNode node = new TComboNode();
            node.setID("ALL");
            node.setName("ȫ��Ժ��");
            getModel().getItems().add(1, node);
        }

    }
}
