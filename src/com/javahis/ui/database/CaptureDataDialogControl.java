package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TWord;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.ECapture;

/**
 *
 * <p>Title: ץȡ����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CaptureDataDialogControl extends TControl{
    private TWord word;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        word = (TWord) getParameter();
    }
    /**
     * ȷ��
     */
    public void onOK()
    {
        String name = getText("NAME");
        if(name == null || name.length() == 0)
        {
            setValue("VALUE","����������");
            return;
        }
        setValue("VALUE",word.getCaptureValue(name));
    }
}
