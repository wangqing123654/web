package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.div.VText;
import java.awt.Color;
import com.dongyang.util.TypeTool;
import com.dongyang.util.StringTool;
import com.dongyang.tui.DColor;
/**
 *
 * <p>Title: �������Կ�����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.5.22
 * @version 1.0
 */
public class VTextDialogControl extends TControl{
    private VText text;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        text = (VText)getParameter();
        setValue("NAME",text.getName());
        setValue("DX",text.getX());
        setValue("DY",text.getY());
        setValue("DWIDTH",text.getWidth());
        setValue("DX1B",text.isX1B());
        setValue("DY1B",text.isY1B());
        setValue("DX2B",text.isX2B());
        setValue("V_CB",text.isVisible());
        Color c = text.getColor();
        setValue("COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|COLOR_D|setForeground",c);
        setValue("DX0",text.getX0());
        setValue("DY0",text.getY0());
        setValue("DCOUNT",text.getCount());
        setValue("DTEXT",text.getText());
        setValue("DFONT",text.getFontName());
        setText("DFONT_SIZE",""+text.getFontSize());
        setValue("DB",text.isFontBold());
        setValue("DI",text.isFontItalic());
        setValue("DFONT_ROTATE",text.getFontRotate());
        setValue("OFF_SET_FLG",text.isTextOffsetFlg());
        setValue("OFF_SET",text.getTextOffset());
        setValue("OFF_SET_FORMAT",text.getTextOffsetFormat());
        setValue("AUTO_ENTER",text.isAutoEnter());
        setValue("AUTO_ENTER_HEIGHT",text.getAutoEnterHeight());
        setValue("TEXT_T",text.isTextT());
        switch(text.getAlignment())
        {
            case 0:
                setValue("ALEFT",true);
                break;
            case 1:
                setValue("ACENTER",true);
                break;
            case 2:
                setValue("ARIGHT",true);
                break;
        }
        setValue("DL",text.isLine());
        setValue("BOTTOMLINE",text.isBottomLine());
        setValue("DEL_ORDER",text.isDelOrder());
        setValue("txtGroupName",text.getGroupName());
        setValue("MACRO_NAME",text.getMicroName());
        //
        setValue("TAKECDANAME",text.getTakeCdaName());
        setValue("CDAVALUE",text.getCdaValue());
    }
    /**
     * ��������
     */
    public void onName()
    {
        text.setName(getText("NAME"));
        text.update();
        text.modify("NAME");
    }
    /**
     * ����x
     */
    public void onDX()
    {
        text.setX(TypeTool.getInt(getValue("DX")));
        text.update();
    }
    /**
     * ����y
     */
    public void onDY()
    {
        text.setY(TypeTool.getInt(getValue("DY")));
        text.update();
    }
    /**
     * ��ʾ
     */
    public void onVCB()
    {
        text.setVisible(TypeTool.getBoolean(getValue("V_CB")));
        text.update();
        text.modify("VISIBLE");
    }
    /**
     * ��ɫ
     */
    public void onColor()
    {
        Color color = StringTool.getColor(getText("COLOR"),getConfigParm());
        if(color == null)
            color = new Color(0,0,0);
        text.setColor(color);
        callFunction("UI|COLOR_D|setForeground",color);
        text.update();
    }
    /**
     * ��ɫ��
     */
    public void onColorDialog()
    {
        text.setColor(DColor.colorDialog(getComponent(),text.getColor()));
        Color c = text.getColor();
        setValue("COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|COLOR_D|setForeground",c);
        text.update();
    }
    /**
     * ����x0
     */
    public void onDX0()
    {
        text.setX0(TypeTool.getInt(getValue("DX0")));
        text.update();
    }
    /**
     * ����y0
     */
    public void onDY0()
    {
        text.setY0(TypeTool.getInt(getValue("DY0")));
        text.update();
    }
    /**
     * ��������
     */
    public void onDCount()
    {
        text.setCount(TypeTool.getInt(getValue("DCOUNT")));
        text.update();
    }
    /**
     * �ı�
     */
    public void onText()
    {
        text.setText(getText("DTEXT"));
        text.update();
    }
    /**
     * ����
     */
    public void onFont()
    {
        text.setFontName(TypeTool.getString(getValue("DFONT")));
        text.update();
    }
    /**
     * �ֺ�
     */
    public void onFontSize()
    {
        text.setFontSize(TypeTool.getInt(getText("DFONT_SIZE")));
        text.update();
    }
    /**
     * ����
     */
    public void onBold()
    {
        text.setFontBold(TypeTool.getBoolean(getValue("DB")));
        text.update();
    }
    /**
     * б��
     */
    public void onItalic()
    {
        text.setFontItalic(TypeTool.getBoolean(getValue("DI")));
        text.update();
    }
    /**
     * ��ת
     */
    public void onFontRotate()
    {
        text.setFontRotate(TypeTool.getDouble(getValue("DFONT_ROTATE")));
        text.update();
    }
    /**
     * ƫ�ƿ���
     */
    public void onOffSetFlg()
    {
        text.setTextOffsetFlg(TypeTool.getBoolean(getValue("OFF_SET_FLG")));
        text.update();
    }
    /**
     * ƫ����
     */
    public void onOffSet()
    {
        text.setTextOffset(TypeTool.getDouble(getValue("OFF_SET")));
        text.update();
    }
    /**
     * ƫ����ʾ��ʽ
     */
    public void onOffSetFormat()
    {
        text.setTextOffsetFormat(TypeTool.getString(getValue("OFF_SET_FORMAT")));
        text.update();
    }
    /**
     * �Զ�����
     */
    public void onAutoEnter()
    {
        text.setAutoEnter(TypeTool.getBoolean(getValue("AUTO_ENTER")));
        text.update();
    }
    /**
     * �Զ����и߶�
     */
    public void onAutoEnterHeight()
    {
        text.setAutoEnterHeight(TypeTool.getInt(getValue("AUTO_ENTER_HEIGHT")));
        text.update();
    }
    /**
     * ����TParm ����
     */
    public void onTextT()
    {
        text.setTextT(TypeTool.getBoolean(getValue("TEXT_T")));
        text.update();
    }
    /**
     * �޸Ĳ���
     * @param name String
     */
    public void modify(String name)
    {
        if("NAME".equals(name))
        {
            setValue("NAME",text.getName());
            return;
        }
        if("VISIBLE".equals(name))
        {
            setValue("V_CB",text.isVisible());
            return;
        }
    }
    /**
     * ���ÿ��
     */
    public void onDWidth()
    {
        text.setWidth(TypeTool.getInt(getValue("DWIDTH")));
        text.update();
    }
    public void onDX1B()
    {
        text.setX1B(TypeTool.getBoolean(getValue("DX1B")));
        text.update();
    }
    public void onDY1B()
    {
        text.setY1B(TypeTool.getBoolean(getValue("DY1B")));
        text.update();
    }
    public void onDX2B()
    {
        text.setX2B(TypeTool.getBoolean(getValue("DX2B")));
        text.update();
    }
    /**
     * ����
     */
    public void onALeft()
    {
        text.setAlignment(0);
        text.update();
    }
    /**
     * ����
     */
    public void onACenter()
    {
        text.setAlignment(1);
        text.update();
    }
    /**
     * ����
     */
    public void onARight()
    {
        text.setAlignment(2);
        text.update();
    }
    /**
     * �»���
     */
    public void onIsLine()
    {
        text.setIsLine(TypeTool.getBoolean(getValue("DL")));
        text.update();
    }
    /**
     * ����
     */
    public void onBottomLine()
    {
        text.setBottomLine(TypeTool.getBoolean(getValue("BOTTOMLINE")));
        text.update();
    }
    /**
     * ����Ĳ���ʾ
     */
    public void onDelOrder()
    {
        text.setDelOrder(TypeTool.getBoolean(getValue("DEL_ORDER")));
        text.update();
    }
    /**
     * �ر�
     * @return boolean
     */
    public boolean onClosing()
    {
        text.setPropertyWindow(null);
        return true;
    }
    /**
     * ������������
     */
    public void onSetGroupName(){
        text.setGroupName(TypeTool.getString(getValue("txtGroupName")));
        text.update();

    }
    /**
     * ���ú���
     */
    public void onSetMacroName(){
        text.setMicroName(TypeTool.getString(getValue("MACRO_NAME")));
        text.update();

    }
    /**
     * �Ƿ�ȡCDA���ƣ�
     */
    public void onSetTakeCdaName(){
    	System.out.println("==take cdaName=="+TypeTool.getBoolean(getValue("TAKECDANAME")));
        text.setTakeCdaName(TypeTool.getBoolean(getValue("TAKECDANAME")));
        text.update();
    }

    /**
     * ����CDA��Ӧֵ��
     */
    public void onSetCdaValue(){
        text.setCdaValue(TypeTool.getString(getValue("CDAVALUE")));
        text.update();
    }

}
