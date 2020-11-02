package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.EInputText;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextField;

/**
 *
 * <p>Title: �����ı�������Կ�����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.10.29
 * @version 1.0
 */
public class InputTextEditControl
    extends TControl {
    /**
     * ������ʾ�齨����
     */
    private EInputText inputText;
    /**
     * ��ʼ��
     */
    public void onInit() {
        inputText = (EInputText) getParameter();
        if (inputText == null) {
            return;
        }
        setValue("NAME", inputText.getName());
        setValue("TEXT", inputText.getText());
        setValue("ALLOW_NULL", inputText.isAllowNull() ? "Y" : "N");
        setValue("MACRONAME", inputText.getMicroName());
        setValue("GROUP_NAME", inputText.getGroupName());
        setValue("CHK_ISCDA", inputText.isIsDataElements() ? "Y" : "N");
        setValue("CHK_LOCKED", inputText.isLocked() ? "Y" : "N");

        /**if (inputText.isIsDataElements()) {
            TTextField tf_name = (TTextField) getComponent("NAME");
            TTextField tf_text = (TTextField) getComponent("TEXT");
            TCheckBox ck_text = (TCheckBox) getComponent("ALLOW_NULL");
            TTextField tf_groupname = (TTextField) getComponent("GROUP_NAME");
            TTextField tf_macroname = (TTextField) getComponent("MACRONAME");

            tf_name.setEditable(false);
            //tf_text.setEditable(false);
            ck_text.setEnabled(false);

            tf_groupname.setEditable(false);
            tf_macroname.setEditable(false);

                 }**/



    }

    /**
     * ȷ��
     */
    public void onOK() {
        if (getText("TEXT").length() == 0) {
            this.messageBox_("�ı�����Ϊ��!");
            return;
        }
        inputText.setGroupName(getText("GROUP_NAME"));
        inputText.setName(getText("NAME"));
        inputText.setText(getText("TEXT"));
        inputText.setMicroName(getText("MACRONAME"));
        if (getValue("CHK_ISCDA").equals("Y")) {
            inputText.setDataElements(true);
        }
        else {
            inputText.setDataElements(false);
        }
        inputText.setLocked(getValueBoolean("CHK_LOCKED"));

        inputText.setModify(true);
        setReturnValue("OK");
        closeWindow();
    }
}
