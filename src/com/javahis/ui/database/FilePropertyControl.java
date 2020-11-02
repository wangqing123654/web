package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.MFile;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TCheckBox;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title: �ļ����ԶԻ���</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.5.15
 * @version 1.0
 */
public class FilePropertyControl extends TControl{
    private MFile file;
    private TTextField title;
    private TCheckBox center;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        title = (TTextField)getComponent("TITLE");
        center = (TCheckBox)getComponent("ICENTER");

        file = (MFile)getParameter();
        if(file == null)
            return;
        title.setText(file.getTitle());
        setValue("IX",file.getPreviewWindowX());
        setValue("IY",file.getPreviewWindowY());
        setValue("IWIDTH",file.getPreviewWindowWidth());
        setValue("IHEIGHT",file.getPreviewWindowHeight());
        center.setSelected(file.isPreviewWindowCenter());
        setValue("ENTITLE",file.getEnTitle());
    }
    /**
     * ȷ��
     */
    public void onOK()
    {
        if(file == null)
            return;
        file.setTitle(title.getText());
        file.setPreviewWindowX(TypeTool.getInt(getValue("IX")));
        file.setPreviewWindowY(TypeTool.getInt(getValue("IY")));
        file.setPreviewWindowWidth(TypeTool.getInt(getValue("IWIDTH")));
        file.setPreviewWindowHeight(TypeTool.getInt(getValue("IHEIGHT")));
        file.setPreviewWindowCenter(center.isSelected());
        file.setEnTitle(getText("ENTITLE"));
        closeWindow();
    }
    /**
     * ȡ��
     */
    public void onCancel()
    {
        closeWindow();
    }
}
