package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.div.VBarCode;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import java.awt.Color;
import com.dongyang.util.StringTool;
import com.dongyang.tui.DColor;

/**
 *
 * <p>Title: ����Ի���</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.6.18
 * @version 1.0
 */
public class VBarCodeDialogControl extends TControl{
    VBarCode barCode;
    TComboBox symbologyIDCombo;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        symbologyIDCombo = (TComboBox)getComponent("SYMBOLOGY_ID");
        barCode = (VBarCode)getParameter();
        setValue("NAME",barCode.getName());
        setValue("DX",barCode.getX());
        setValue("DY",barCode.getY());
        setValue("V_CB",barCode.isVisible());
        setValue("DTEXT",barCode.getText());
        initSymbologyCombo();
        setValue("SYMBOLOGY_ID",barCode.getSymbologyID());
        setValue("SHOW_TEXT",barCode.isShowText());
        setValue("BAR_COLOR",barCode.getBarColorString());
        Color c = barCode.getBarColor();
        if(c == null)
            c = new Color(255,255,255);
        callFunction("UI|BAR_COLOR_D|setForeground",c);
        setValue("BAR_TEXT_COLOR",barCode.getBarTextColorString());
        c = barCode.getBarTextColor();
        if(c == null)
            c = new Color(255,255,255);
        callFunction("UI|BAR_TEXT_COLOR_D|setForeground",c);
        setValue("BACKGROUND",barCode.getBackgroundString());
        c = barCode.getBackground();
        if(c == null)
            c = new Color(255,255,255);
        callFunction("UI|BACKGROUND_D|setForeground",c);
        setValue("ROTATION_ANGLE",barCode.getRotationAngle());
        setValue("BAR_HEIGHT_CM",barCode.getBarHeightCM());
        setValue("X_DIMENSION_CM",barCode.getXDimensionCM());
        setValue("CHECK_C",barCode.getCheckCharacter());
        setValue("DFONT",barCode.getFontName());
        setText("DFONT_SIZE",""+barCode.getFontSize());
        setValue("DB",barCode.isFontBold());
        setValue("DI",barCode.isFontItalic());
        setValue("TEXT_T",barCode.isTextT());
    }
    /**
     * ��ʼ������Combo
     */
    public void initSymbologyCombo()
    {
        TParm parm = new TParm();
        parm.addData("ID",0);
        parm.addData("NAME","CODE39");
        parm.addData("ID",1);
        parm.addData("NAME","CODE39EXT");
        parm.addData("ID",2);
        parm.addData("NAME","INTERLEAVED25");
        parm.addData("ID",3);
        parm.addData("NAME","CODE11");
        parm.addData("ID",4);
        parm.addData("NAME","CODABAR");
        parm.addData("ID",5);
        parm.addData("NAME","MSI");
        parm.addData("ID",6);
        parm.addData("NAME","UPCA");
        parm.addData("ID",7);
        parm.addData("NAME","IND25");
        parm.addData("ID",8);
        parm.addData("NAME","MAT25");
        parm.addData("ID",9);
        parm.addData("NAME","CODE93");
        parm.addData("ID",10);
        parm.addData("NAME","EAN13");
        parm.addData("ID",11);
        parm.addData("NAME","EAN8");
        parm.addData("ID",12);
        parm.addData("NAME","UPCE");
        parm.addData("ID",13);
        parm.addData("NAME","CODE128");
        parm.addData("ID",14);
        parm.addData("NAME","CODE93EXT");
        parm.addData("ID",15);
        parm.addData("NAME","POSTNET");
        parm.addData("ID",16);
        parm.addData("NAME","PLANET");
        parm.addData("ID",17);
        parm.addData("NAME","UCC128");
        parm.setCount(18);

        symbologyIDCombo.setParmMap("ID:ID;NAME:NAME");
        symbologyIDCombo.setParmValue(parm);
    }
    /**
     * ��������
     */
    public void onName()
    {
        barCode.setName(getText("NAME"));
        barCode.update();
        barCode.modify("NAME");
    }
    /**
     * ����x
     */
    public void onDX()
    {
        barCode.setX(TypeTool.getInt(getValue("DX")));
        barCode.update();
    }
    /**
     * ����y
     */
    public void onDY()
    {
        barCode.setY(TypeTool.getInt(getValue("DY")));
        barCode.update();
    }
    /**
     * ��ʾ
     */
    public void onVCB()
    {
        barCode.setVisible(TypeTool.getBoolean(getValue("V_CB")));
        barCode.update();
        barCode.modify("VISIBLE");
    }
    /**
     * �ı�
     */
    public void onText()
    {
        barCode.setText(getText("DTEXT"));
        barCode.update();
    }
    /**
     * ��������
     */
    public void onSymbologyID()
    {
        barCode.setSymbologyID(TypeTool.getInt(getValue("SYMBOLOGY_ID")));
        barCode.update();
    }
    /**
     * ��ʾ��������
     */
    public void onShowText()
    {
        barCode.setShowText(TypeTool.getBoolean(getValue("SHOW_TEXT")));
        barCode.update();
    }
    /**
     * ������ɫ
     */
    public void onBarColor()
    {
        Color color = StringTool.getColor(getText("BAR_COLOR"),getConfigParm());
        barCode.setBarColor(color);
        if(color == null)
            color = new Color(0,0,0);
        callFunction("UI|BAR_COLOR_D|setForeground",color);
        barCode.update();
    }
    /**
     * ������ɫ��ɫ��
     */
    public void onBarColorD()
    {
        Color c = barCode.getBarColor();
        if(c == null)
            c = new Color(255,255,255);
        c = DColor.colorDialog(getComponent(),c);
        barCode.setBarColor(c);
        setValue("BAR_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|BAR_COLOR_D|setForeground",c);
        barCode.update();
    }
    /**
     * ����������ɫ
     */
    public void onBarTextColor()
    {
        Color color = StringTool.getColor(getText("BAR_TEXT_COLOR"),getConfigParm());
        barCode.setBarTextColor(color);
        if(color == null)
            color = new Color(0,0,0);
        callFunction("UI|BAR_TEXT_COLOR_D|setForeground",color);
        barCode.update();
    }
    /**
     * ����������ɫ��ɫ��
     */
    public void onBarTextColorD()
    {
        Color c = barCode.getBarTextColor();
        if(c == null)
            c = new Color(255,255,255);
        c = DColor.colorDialog(getComponent(),c);
        barCode.setBarTextColor(c);
        setValue("BAR_TEXT_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|BAR_TEXT_COLOR_D|setForeground",c);
        barCode.update();
    }
    /**
     * ������ɫ
     */
    public void onBackground()
    {
        Color color = StringTool.getColor(getText("BACKGROUND"),getConfigParm());
        barCode.setBackground(color);
        if(color == null)
            color = new Color(0,0,0);
        callFunction("UI|BACKGROUND_D|setForeground",color);
        barCode.update();
    }
    /**
     * ������ɫ��ɫ��
     */
    public void onBackgroundD()
    {
        Color c = barCode.getBackground();
        if(c == null)
            c = new Color(255,255,255);
        c = DColor.colorDialog(getComponent(),c);
        barCode.setBackground(c);
        setValue("BACKGROUND",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|BACKGROUND_D|setForeground",c);
        barCode.update();
    }
    /**
     * ��ת�Ƕ�
     */
    public void onRotationAngle()
    {
        barCode.setRotationAngle(TypeTool.getInt(getValue("ROTATION_ANGLE")));
        barCode.update();
    }
    /**
     * ����߶�
     */
    public void onBarHeightCM()
    {
        barCode.setBarHeightCM(TypeTool.getDouble(getValue("BAR_HEIGHT_CM")));
        barCode.update();
    }
    /**
     * ����X���
     */
    public void onXDimensionCM()
    {
        barCode.setXDimensionCM(TypeTool.getDouble(getValue("X_DIMENSION_CM")));
        barCode.update();
    }
    /**
     * ������֤��
     */
    public void onCheckC()
    {
        barCode.setCheckCharacter(TypeTool.getBoolean(getValue("CHECK_C")));
        barCode.update();
    }
    /**
     * ����
     */
    public void onFont()
    {
        barCode.setFontName(TypeTool.getString(getValue("DFONT")));
        barCode.update();
    }
    /**
     * �ֺ�
     */
    public void onFontSize()
    {
        barCode.setFontSize(TypeTool.getInt(getText("DFONT_SIZE")));
        barCode.update();
    }
    /**
     * ����
     */
    public void onBold()
    {
        barCode.setFontBold(TypeTool.getBoolean(getValue("DB")));
        barCode.update();
    }
    /**
     * б��
     */
    public void onItalic()
    {
        barCode.setFontItalic(TypeTool.getBoolean(getValue("DI")));
        barCode.update();
    }
    /**
     * TParm����
     */
    public void onTextT()
    {
        barCode.setTextT(TypeTool.getBoolean(getValue("TEXT_T")));
        barCode.update();
    }
}
