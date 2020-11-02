package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;

/**
 * <p>Title: ����ѡ��</p>
 *
 * <p>Description:����ѡ�� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 2009803
 * @version 1.0
 */
public class BILPationChooseControl
    extends TControl {

    /**
     * ��������
     */
    private TTable table;


    public void onInit() {
        super.onInit();
        //��Ŀ����
        table = (TTable) getComponent("TABLE");
        //���벿�ֲ���
        getInitParm();

    }
    /**
     * �õ��ϼ����������
     */
    public void getInitParm() {
        if(this.getParameter()==null)
            return ;
        TParm parm = (TParm)this.getParameter();
        //���ݷ������
        table.setParmValue(parm);
        if(table.getRowCount()==0)
            return;
//        //Ĭ��ѡ���һ��
        table.grabFocus();
        table.setSelectedRow(0);
        //�����Ϸ�
        setTextValue(parm.getRow(0));
    }
    /**
     * ��parm��������ʾ�ڽ�����
     * @param parm TParm
     */
    public void setTextValue(TParm parm){
        this.setValueForParm("MR_NO;IPD_NO;PAT_NAME;SEX_CODE;IN_DATE",parm);
        this.setValue("OUT_DATE",parm.getData("DS_DATE"));
    }
    /**
     * table����¼�
     */
    public void onTableClicked(){
        int row=table.getSelectedRow();
        if(row<0)
            return;
        TParm parm=table.getParmValue().getRow(row);
        //�����Ϸ�
        setTextValue(parm);
    }
    /**
     * ˫���¼�
     */
    public void onTableDoubleCliecked(){
        int row=table.getSelectedRow();
        if(row<0){
           messageBox_("��ѡ�񲡻���Ϣ!");
           return;
       }
       TParm parm=table.getParmValue().getRow(row);
       this.setReturnValue(parm);
       this.callFunction("UI|onClose");
    }
    /**
     * ȡ��
     */
    public void onCancle(){
        this.callFunction("UI|onClose");
    }
    /**
     * ����
     */
    public void onChoose(){
        onTableDoubleCliecked();
    }
}
