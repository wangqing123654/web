package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.div.MV;
import com.dongyang.tui.text.div.VPic;
import com.dongyang.tui.text.div.DIV;
import com.dongyang.util.TypeTool;
import javax.swing.ImageIcon;
import com.dongyang.util.FileTool;
import java.io.IOException;
import javax.swing.Icon;

/**
 *
 * <p>Title: ͼƬ���ԶԻ��������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author lzk 2010.2.3
 * @version 1.0
 */
public class PictureEditDialogControl
    extends TControl {
    /**
     * ��
     */
    private EMacroroutine mactroroutine;
    /**
     * ��ʼ��
     */
    public void onInit() {
        Object obj = getParameter();
        if (obj == null || ! (obj instanceof EMacroroutine)) {
            setTitle("����ͼƬ");
            setValue("NAME", "�½�ͼƬ");
            return;
        }
        mactroroutine = (EMacroroutine) obj;
        setValue("NAME", mactroroutine.getName());
        setValue("D_WIDTH", mactroroutine.getWidth());
        setValue("D_HEIGHT", mactroroutine.getHeight());
        MV mv = mactroroutine.getModel().getMVList().get("ͼ��");
        if (mv == null) {
            return;
        }
        DIV div = mv.get("ͼƬ");
        if (! (div instanceof VPic)) {
            return;
        }
        VPic pic = (VPic) div;

        setValue("PIC_NAME", pic.getPictureName());
    }

    /**
     * ȷ��
     */
    public void onOK() {
        //�����ļ���ʽ;
        if (getText("PIC_NAME").indexOf("\\") != -1) {
            //this.messageBox("�����뱾���ϴ��ļ�����ȷ��ʽ!���磺C:/XXX/XXX.gif");
            //return;
            String templatePath = getText("PIC_NAME").replaceAll("\\\\", "/");
            //this.messageBox("templatePath" + templatePath);
            setValue("PIC_NAME", templatePath);
        }
        //�½�
        if (mactroroutine == null) {
                 setReturnValue(new Object[] {getText("NAME"),
                                getText("PIC_NAME"), getValue("D_WIDTH"),
                                getValue("D_HEIGHT")});

        }
        else {
            //˵��Ϊ�����ϴ�;
            if (getText("PIC_NAME").indexOf(":") == 1) {
                localUpload();
            }
            else {
                mactroroutine.setName(getText("NAME"));
                mactroroutine.getModel().setWidth(TypeTool.getInt(getValue(
                    "D_WIDTH")));
                mactroroutine.getModel().setHeight(TypeTool.getInt(getValue(
                    "D_HEIGHT")));

                MV mv = mactroroutine.getModel().getMVList().get("ͼ��");
                if (mv == null) {
                    return;
                }
                DIV div = mv.get("ͼƬ");
                if (! (div instanceof VPic)) {
                    return;
                }
                VPic pic = (VPic) div;
                //
                pic.setPictureName(getText("PIC_NAME"));

                setReturnValue("OK");
            }
        }
        closeWindow();
    }

    /**
     *
     * @param fileName String
     * @return ImageIcon
     */
    private static ImageIcon getImage(String fileName) {

        ImageIcon icon = null;
        //
        //byte[] data = readFile(fileName);
        byte[] data = null;
        try {
            data = FileTool.getByte(fileName);
        }
        catch (IOException ex) {
        }
        if (data == null) {
            System.out.println("ERR:û���ҵ�ͼƬ" + fileName);

            return icon;
        }
        icon = new ImageIcon(data);
        return icon;
    }

    private void localUpload(){


        mactroroutine.setName(getText("NAME"));
                mactroroutine.getModel().setWidth(TypeTool.getInt(getValue(
                    "D_WIDTH")));
                mactroroutine.getModel().setHeight(TypeTool.getInt(getValue(
                    "D_HEIGHT")));

                MV mv = mactroroutine.getModel().getMVList().get("ͼ��");
                if (mv == null) {
                    return;
                }
                DIV div = mv.get("ͼƬ");
                if (! (div instanceof VPic)) {
                    return;
                }
                VPic pic = (VPic) div;
                //����ͼ
                //pic.setPictureNameBackground(getText("PIC_NAME"));
                //this.messageBox("�����ϴ� PictureNameBackground===="+pic.getPictureNameBackground());
                //�ϳ�ͼ
                pic.setPictureName(getText("PIC_NAME"));
                // ((ImageIcon)pic.getIcon()).getImage();
                Icon bgIcon = (Icon) getImage(getText("PIC_NAME"));

                if (bgIcon == null) {
                    this.messageBox("û���ҵ�ͼƬ!");
                    return;
                }

                pic.setIcon(bgIcon);
                setReturnValue("OK");

    }


    /**
     * ȡ��
     */
    public void onCancel() {
        closeWindow();
    }
}
