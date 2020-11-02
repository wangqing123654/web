package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextField;

/**
 *
 * <p>Title: ѡ������ԶԻ��������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.11.30
 * @version 1.0
 */
public class CheckBoxChooseControl  extends TControl{
    /**
     * ѡ������
     */
    private ECheckBoxChoose checkBoxChoose;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        checkBoxChoose = (ECheckBoxChoose) getParameter();
        if (checkBoxChoose == null)
            return;
        setValue("CDA_GROUP_NAME", checkBoxChoose.getCdaGroupName());
        setValue("NAME", checkBoxChoose.getName());
        setValue("TEXT", checkBoxChoose.getText());
        setValue("CHECK_VALUE",checkBoxChoose.isChecked());
        setValue("INLEFT",checkBoxChoose.inLeft());
        setValue("T_ENABLED",checkBoxChoose.isEnabled());
        setValue("CHECK_EVENT",checkBoxChoose.getCheckedScript());
        setValue("GROUP_NAME",checkBoxChoose.getGroupName());

        //��Ԫ�������޸�
        setValue("ALLOW_NULL",checkBoxChoose.isAllowNull()?"Y" : "N");
        setValue("CHK_ISCDA",checkBoxChoose.isIsDataElements()?"Y" : "N");
        setValue("CHK_LOCKED",checkBoxChoose.isLocked()?"Y" : "N");

        /**if(checkBoxChoose.isIsDataElements()){
            TTextField tf_name = (TTextField)getComponent("NAME");
            TTextField tf_text = (TTextField)getComponent("TEXT");
            TCheckBox ck_text = (TCheckBox)getComponent("ALLOW_NULL");

            tf_name.setEditable(false);
            tf_text.setEditable(false);
            ck_text.setEnabled(false);
        }**/




    }
    /**
     * ȷ��
     */
    public void onOK()
    {
        checkBoxChoose.setcCaGroupName(getText("CDA_GROUP_NAME"));
        checkBoxChoose.setName(getText("NAME"));
        checkBoxChoose.setText(getText("TEXT"));
        checkBoxChoose.setChecked(TypeTool.getBoolean(getValue("CHECK_VALUE")));
        checkBoxChoose.setInLeft(TypeTool.getBoolean(getValue("INLEFT")));
        checkBoxChoose.setEnabled(TypeTool.getBoolean(getValue("T_ENABLED")));
        checkBoxChoose.setCheckedScript(getText("CHECK_EVENT"));
        checkBoxChoose.setGroupName(getText("GROUP_NAME"));
        if(getValue("CHK_ISCDA").equals("Y")){
            checkBoxChoose.setDataElements(true);
        }else{
             checkBoxChoose.setDataElements(false);
        }
        checkBoxChoose.setLocked(getValueBoolean("CHK_LOCKED"));

        checkBoxChoose.setModify(true);
        setReturnValue("OK");
        closeWindow();
    }
}
