package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;

/**
 * <p>Title: ����Ԫ�ضԻ���</p>
 *
 * <p>Description:����Ԫ�ضԻ��� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JAVAHIS</p>
 *
 * @author li.xiang790130@gmail.com
 * @version 1.0
 */
public class InsertElementDialogControl extends TControl
{
    private TTextFormat ftElemCategory;
    private TTextFormat ftElem;
    private TComboBox  cmbMacro;
    private TComboBox  cmbComponentType;

    private TTextField txtDocumentType;

    public InsertElementDialogControl() {
    }

    public void onInit()
    {
        //ȡ�ĵ����Ͳ�������ֵ;
        Object obj=this.getParameter();
        String documentType=((TParm) obj).getValue("DOCUMENT_TYPE");
        txtDocumentType=(TTextField)getComponent("txt_document_type");
        //this.messageBox("name+++"+txtDocumentType.getName());
        txtDocumentType.setValue(documentType);

        ftElemCategory=(TTextFormat)getComponent("tf_elemCategory");
        ftElem = (TTextFormat)getComponent("tf_elem");
        cmbMacro=(TComboBox)getComponent("cmb_macro");
        cmbComponentType=(TComboBox)getComponent("cmb_componenttype");
        //��ʼ���ؼ���
        cmbComponentType.setStringData("[[id,name],[,],[3,�̶��ı�],[4,��ѡ],[5,��ѡ],[6,����ѡ��],[7,������ʾ],[8,�����],[9,ץȡ����],[10,ѡ���],[14,����],[15,������]]");

    }
    /**
     *���ѯ
     */
    public void macroQuery(){
        String elemData=(String)ftElem.getValue();
        //�ؼ��Ǻ����ץȡ��
        //ͨ��EMR_MICRO_CONVERT��ȡ�����ƣ�
       cmbMacro.setSQL("SELECT MICRO_NAME FROM EMR_MICRO_CONVERT WHERE DATA_ELEMENT_CODE='" + elemData + "'");
       //System.out.println("sql============"+"SELECT MICRO_NAME,MICRO_NAME FROM EMR_MICRO_CONVERT WHERE DATA_ELEMENT_CODE='" + elemData + "'");
       cmbMacro.retrieve();
        //�������0
        //this.messageBox("==count=="+cmbMacro.getDataStore().rowCount());
        //���Ǻ�
        if(cmbMacro.getDataStore().rowCount()==0){
            cmbMacro.setValue("");
            cmbMacro.setEnabled(false);

        }else if(cmbMacro.getDataStore().rowCount()>1){
            cmbMacro.setEnabled(true);
            cmbMacro.setValue("");

        }else{
            cmbMacro.setEnabled(true);
            cmbMacro.setValue("");
            cmbMacro.setSelectedIndex(1);
        }

        //����Ĭ�Ͽؼ���
        String sql="SELECT COMPONENT_TYPE FROM EMR_CLINICAL_DATAGROUP WHERE 1=1";
            sql+=" AND GROUP_CODE='"+ftElemCategory.getValue()+"'";
            sql+=" AND DATA_CODE='"+ftElem.getValue()+"'";
            TParm returnParm=new TParm(this.getDBTool().select(sql));
            cmbComponentType.setValue(returnParm.getValue("COMPONENT_TYPE",0));

    }

    /**
     * ȷ������
     */
    public void onOk()
    {
        if(cmbMacro.isEnabled()&&cmbMacro.getValue().equals("")){
            //this.messageBox("��ѡ���!");
            //return;
        }

        if(ftElemCategory.getValue()==null  ||ftElem.getValue() == null)
        {
            this.messageBox("��ѡ��Ԫ�ط����Ԫ������!");
            return;
        }else{
            //ȡ��Ӧ�����ݿ�ֵ�������������壻
            String sql="SELECT * FROM EMR_CLINICAL_DATAGROUP WHERE 1=1";
            sql+=" AND GROUP_CODE='"+ftElemCategory.getValue()+"'";
            sql+=" AND DATA_CODE='"+ftElem.getValue()+"'";
            //System.out.println("======sql====="+sql);
            TParm returnParm=new TParm(this.getDBTool().select(sql));
            returnParm.addData("MACRO_NAME",cmbMacro.getValue());
            returnParm.addData("SELECTED_COMPONENT_TYPE",cmbComponentType.getValue());

            //System.out.println("returnParm====="+returnParm);
            //this.messageBox("aa"+returnParm.getCount());
            if(returnParm.getCount()<0){
                this.messageBox("������Դ��Ϣ��");
                return;
            }

            this.setReturnValue(returnParm);
            this.closeWindow();


        }


    }
    /**
     * ȡ���رչ���
     */
    public void onCancel()
    {
        closeWindow();
    }

    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }




}
