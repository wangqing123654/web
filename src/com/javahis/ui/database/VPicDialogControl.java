package com.javahis.ui.database;
import com.dongyang.tui.text.div.VPic;
import com.dongyang.util.TypeTool;
/**
 *
 * <p>Title: ͼƬ���ԶԻ��������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.5.26
 * @version 1.0
 */
public class VPicDialogControl extends VTableDialogControl{
    VPic pic;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        pic = (VPic)getParameter();
        super.onInit();
        setValue("PIC_NAME",pic.getPictureName());
        setValue("DSCALE",pic.getScale());
        setValue("AUTO_SCALE",pic.isAutoScale());
    }
    /**
     * ͼƬ����
     */
    public void onPicName()
    {
        pic.setPictureName(getText("PIC_NAME"));
        pic.update();
    }
    /**
     * ���ű���
     */
    public void onDScale()
    {
        pic.setScale(TypeTool.getDouble(getText("DSCALE")));
        pic.update();
    }
    /**
     * �Զ�����
     */
    public void onActionScale()
    {
        pic.setAutoScale(TypeTool.getBoolean(getValue("AUTO_SCALE")));
    }
}
