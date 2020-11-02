package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

/**
 * <p>Title: �����Ŀѡ��Ի���</p>
 *
 * <p>Description: �����Ŀѡ��Ի���</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis </p>
 *
 * @author sundx
 * @version 1.0
 */
public class SYSFEEEXMItemControl extends TControl {

    /**
     * ������
     */
    public SYSFEEEXMItemControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        initDate();
    }

    /**
     * ��ʼ����������
     */
    public void initDate(){
        TParm parmIn = (TParm)getParameter();
        parmIn = new TParm(TJDODBTool.getInstance().select(" SELECT OPTITEM_CODE "+
                                                           " FROM SYS_ORDEROPTITEM "+
                                                           " WHERE ORDER_CODE = '"+parmIn.getValue("ORDER_CODE")+"'"));
        TParm parm = new TParm(TJDODBTool.getInstance().select(" SELECT 'N' FLG,ID,CHN_DESC "+
                                                               " FROM SYS_DICTIONARY "+
                                                               " WHERE GROUP_ID='SYS_OPTITEM' "+
                                                               " ORDER BY SEQ,ID "));
        for(int i = 0;i < parmIn.getCount();i++){
            String optitemCode = parmIn.getValue("OPTITEM_CODE",i);
            for(int j = 0;j < parm.getCount();j++){
                if(optitemCode.equals(parm.getValue("ID",j)))
                    parm.setData("FLG",j,"Y");
            }
        }
        ((TTable)getComponent("TABLE")).setParmValue(parm);
    }

    /**
     * ȷ����ť
     */
    public void onConfirm(){
        ((TTable)getComponent("TABLE")).acceptText();
        int rowCount = ((TTable)getComponent("TABLE")).getRowCount();
        TParm parm = ((TTable)getComponent("TABLE")).getParmValue();
        TParm returnParm = new TParm();
        for(int i = 0;i < rowCount;i++){
            if(parm.getValue("FLG",i).equals("N"))
                continue;
            returnParm.addData("ID",parm.getData("ID",i));
            returnParm.addData("CHN_DESC",parm.getData("CHN_DESC",i));
        }
        setReturnValue(returnParm);
        closeWindow();
    }

    /**
     * ȡ����ť�رմ���
     */
    public void onCancel(){
        closeWindow();
    }
}














