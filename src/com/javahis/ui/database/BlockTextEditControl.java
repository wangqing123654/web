package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.image.GBlock;
import com.dongyang.util.StringTool;
import java.awt.Color;
import com.dongyang.tui.DColor;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TTable;
import java.util.Vector;

/**
 *
 * <p>Title: �ı����ԶԻ���</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2010.3.30
 * @version 1.0
 */
public class BlockTextEditControl extends TControl{
    private GBlock block;
    private TTable table;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        //this.messageBox("come in");
        block = (GBlock)getParameter();
        setValue("DTEXT",block.getText());
        Color c = block.getColor();
        setValue("COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|COLOR_D|setForeground",c);
        c = block.getBkColor();
        if(c != null)
        {
            setValue("BK_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
            callFunction("UI|BK_COLOR_D|setForeground", c);
        }else
        {
            setValue("BK_COLOR","");
            callFunction("UI|BK_COLOR_D|setForeground", new Color(0,0,0));
        }
        c = block.getBorderColor();
        if(c != null)
        {
            setValue("BORDER_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
            callFunction("UI|BORDER_COLOR_D|setForeground", c);
        }else
        {
            setValue("BORDER_COLOR","");
            callFunction("UI|BORDER_COLOR_D|setForeground", new Color(0,0,0));
        }
        setValue("DFONT",block.getFontName());
        setText("DFONT_SIZE",""+block.getFontSize());
        setValue("DB",block.isFontBold());
        setValue("DI",block.isFontItalic());
        setValue("DFONT_ROTATE",block.getFontRotate());
        setValue("AUTO_ENTER_HEIGHT",block.getAutoEnterHeight());
        switch(block.getAlignment())
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
        switch(block.getAlignmentH())
        {
            case 0:
                setValue("AUH",true);
                break;
            case 1:
                setValue("ACH",true);
                break;
            case 2:
                setValue("ADH",true);
                break;
        }
        setValue("BORDER_VISIBLE",block.isBorderVisible());
        setValue("BORDER_LINE_WIDTH",block.getBorderWidth());
        setValue("BORDER_LINE_TYPE",block.getBorderLineType());
        setValue("PIC_AUTO_SIZE",block.isPictureAutoSize());
        setValue("ARROW1_TYPE",block.getArrow1());
        setValue("ARROW2_TYPE",block.getArrow2());
        setValue("ARROW1_LENGTH",block.getArrow1Length());
        setValue("ARROW2_LENGTH",block.getArrow2Length());
        setValue("ARROW1_DEGREE",block.getArrow1Degree());
        setValue("ARROW2_DEGREE",block.getArrow2Degree());
        setValue("G_ENABLED",block.isEnabled());

        table = (TTable)getComponent("TABLE");
        table.setValue(newVector(block.getData()));
        if(table.getRowCount() > 0)
            table.setSelectedRow(0);
        setValue("BK_PIC",block.getPictureName());

    }
    public void onOK()
    {

    }
    /**
     * �޸��ı�
     */
    public void onText()
    {
        //this.messageBox("text"+getText("DTEXT"));
        block.setText(getText("DTEXT"));
        block.update();
    }
    /**
     * ��ɫ
     */
    public void onColor()
    {


        Color color = StringTool.getColor(getText("COLOR"),getConfigParm());
        if(color == null)
            color = new Color(0,0,0);
        block.setColor(color);
        callFunction("UI|COLOR_D|setForeground",color);
        block.update();
    }
    /**
     * ��ɫ��
     */
    public void onColorDialog()
    {
        block.setColor(DColor.colorDialog(getComponent(),block.getColor()));
        Color c = block.getColor();
        setValue("COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|COLOR_D|setForeground",c);
        block.update();
    }
    /**
     * ������ɫ
     */
    public void onBkColor()
    {
         //this.messageBox("COLOR"+getText("COLOR"));

        Color color = StringTool.getColor(getText("BK_COLOR"),getConfigParm());
        block.setBkColor(color);
        if(color != null)
            color = new Color(0,0,0);
        callFunction("UI|BK_COLOR_D|setForeground",color);
        block.update();
    }
    /**
     * ������ɫ��ɫ��
     */
    public void onBKColorDialog()
    {
        block.setBkColor(DColor.colorDialog(getComponent(),block.getColor()));
        Color c = block.getBkColor();
        if(c != null)
        {
            setValue("BK_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
            callFunction("UI|BK_COLOR_D|setForeground",c);
        }
        else
        {
            setValue("BK_COLOR", "");
            callFunction("UI|BK_COLOR_D|setForeground",new Color(0,0,0));
        }
        block.update();
    }
    /**
     * �߿���ɫ
     */
    public void onBorderColor()
    {
        Color color = StringTool.getColor(getText("BORDER_COLOR"),getConfigParm());
        block.setBorderColor(color);
        if(color != null)
            color = new Color(0,0,0);
        callFunction("UI|BORDER_COLOR_D|setForeground",color);
        block.update();
    }
    /**
     * �߿��ɫ��
     */
    public void onBorderColorDialog()
    {
        block.setBorderColor(DColor.colorDialog(getComponent(),block.getColor()));
        Color c = block.getBorderColor();
        if(c != null)
        {
            setValue("BORDER_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
            callFunction("UI|BORDER_COLOR_D|setForeground",c);
        }
        else
        {
            setValue("BORDER_COLOR", "");
            callFunction("UI|BK_COLOR_D|setForeground",new Color(0,0,0));
        }
        block.update();
    }
    /**
     * ����
     */
    public void onFont()
    {
        block.setFontName(TypeTool.getString(getValue("DFONT")));
        block.update();
    }
    /**
     * �ֺ�
     */
    public void onFontSize()
    {
        block.setFontSize(TypeTool.getInt(getText("DFONT_SIZE")));
        block.update();
    }
    /**
     * ����
     */
    public void onBold()
    {
        //this.messageBox("onBold"+TypeTool.getBoolean(getValue("DB")));
        block.setFontBold(TypeTool.getBoolean(getValue("DB")));
        block.update();
    }
    /**
     * б��
     */
    public void onItalic()
    {
        block.setFontItalic(TypeTool.getBoolean(getValue("DI")));
        block.update();
    }
    /**
     * ��ת
     */
    public void onFontRotate()
    {
        block.setFontRotate(TypeTool.getDouble(getValue("DFONT_ROTATE")));
        block.update();
    }
    /**
     * ����
     */
    public void onALeft()
    {
        block.setAlignment(0);
        block.update();
    }
    /**
     * ����
     */
    public void onACenter()
    {
        block.setAlignment(1);
        block.update();
    }
    /**
     * ����
     */
    public void onARight()
    {
        block.setAlignment(2);
        block.update();
    }
    /**
     * ����
     */
    public void onAUH()
    {
        block.setAlignmentH(0);
        block.update();
    }
    /**
     * ����
     */
    public void onACH()
    {
        block.setAlignmentH(1);
        block.update();
    }
    /**
     * ����
     */
    public void onADH()
    {
        block.setAlignmentH(2);
        block.update();
    }
    /**
     * �Զ����и߶�
     */
    public void onAutoEnterHeight()
    {
        block.setAutoEnterHeight(TypeTool.getInt(getValue("AUTO_ENTER_HEIGHT")));
        block.update();
    }
    /**
     * �Ƿ���ʾ�߿�
     */
    public void onBorderVisible()
    {
        block.setBorderVisible(TypeTool.getBoolean(getValue("BORDER_VISIBLE")));
        block.update();
    }
    /**
     * ���ñ߿��߸�
     */
    public void onBorderLineWidth()
    {
        block.setBorderWidth(TypeTool.getFloat(getValue("BORDER_LINE_WIDTH")));
        block.update();
    }
    /**
     * ���ñ߿�������
     */
    public void onBorderLineType()
    {
        block.setBorderLineType(TypeTool.getInt(getValue("BORDER_LINE_TYPE")));
        block.update();
    }
    /**
     * ���ñ���ͼƬ
     */
    public void onBkPic()
    {
        block.setPictureName(getText("BK_PIC"));
        block.update();
    }
    /**
     * ����ͼƬ�Ƿ��Զ�����ߴ�
     */
    public void onPicAutoSize()
    {
        block.setPictureAutoSize(TypeTool.getBoolean(getValue("PIC_AUTO_SIZE")));
        block.update();
    }
    public void onArrow1Type()
    {
        block.setArrow1(TypeTool.getInt(getValue("ARROW1_TYPE")));
        block.update();
    }
    public void onArrow2Type()
    {
        block.setArrow2(TypeTool.getInt(getValue("ARROW2_TYPE")));
        block.update();
    }
    public void onArrow1Length()
    {
        block.setArrow1Length(TypeTool.getInt(getValue("ARROW1_LENGTH")));
        block.update();
    }
    public void onArrow2Length()
    {
        block.setArrow2Length(TypeTool.getInt(getValue("ARROW2_LENGTH")));
        block.update();
    }
    public void onArrow1Degree()
    {
        block.setArrow1Degree(TypeTool.getDouble(getValue("ARROW1_DEGREE")));
        block.update();
    }
    public void onArrow2Degree()
    {
        block.setArrow2Degree(TypeTool.getDouble(getValue("ARROW2_DEGREE")));
        block.update();
    }
    public void onEnabled()
    {
        block.setEnabled(TypeTool.getBoolean(getValue("G_ENABLED")));
        block.update();
    }
    /**
     * Vector ��¡
     * @param v Vector
     * @return Vector
     */
    public Vector newVector(Vector v)
    {
        Vector data = new Vector();
        for(int i = 0;i < v.size();i++)
        {
            Vector t = (Vector)v.get(i);
            Vector t1 = new Vector();
            data.add(t1);
            for(int j = 0;j < t.size();j++)
                t1.add(t.get(j));
        }
        return data;
    }
    /**
     * ����
     */
    public void onNew()
    {
        Vector v = new Vector();
        v.add("");
        v.add("");
        table.insertRowValue(table.getRowCount(),v);
        table.setSelectedRow(table.getRowCount() - 1);
        block.setData(table.getValue());
    }
    /**
     * ����
     */
    public void onInsert()
    {
        int row = table.getSelectedRow();
        if(row == -1)
        {
            onNew();
            return;
        }
        Vector v = new Vector();
        v.add("");
        v.add("");
        table.insertRowValue(row,v);
        block.setData(table.getValue());
    }
    /**
     * ɾ��
     */
    public void onDelete()
    {
        int row = table.getSelectedRow();
        if(row == -1)
            return;
        table.removeRow(row);
        if(table.getRowCount() == 0)
            return;
        if(row >= table.getRowCount())
            row = table.getRowCount() - 1;
        table.setSelectedRow(row);
        block.setData(table.getValue());
    }
    /**
     * ����
     */
    public void onUp()
    {
        int row = table.getSelectedRow();
        if(row <= 0)
            return;
        Vector v = new Vector();
        v.add(table.getValueAt(row - 1,0));
        v.add(table.getValueAt(row - 1,1));
        table.insertRowValue(row + 1,v);
        table.removeRow(row - 1);
        table.setSelectedRow(row - 1);
        block.setData(table.getValue());
    }
    /**
     * ����
     */
    public void onDown()
    {
        int row = table.getSelectedRow();
        if(row == -1 || row == table.getRowCount() - 1)
            return;
        Vector v = new Vector();
        v.add(table.getValueAt(row + 1,0));
        v.add(table.getValueAt(row + 1,1));
        table.removeRow(row + 1);
        table.insertRowValue(row,v);
        table.setSelectedRow(row + 1);
        block.setData(table.getValue());
    }
}
