package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextArea;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title: �쿴��ű��Ի��������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.4.12
 * @version 1.0
 */
public class LookScriptDialogControl extends TControl{
    private TTextArea text;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        text = (TTextArea)getComponent("TEXT");
        text.setText(TypeTool.getString(getParameter()));
    }
    public static void main(String args[])
    {
        com.javahis.util.JavaHisDebug.runDialog("database\\LookScriptDialog.x",
                                                "abc");
    }
}
