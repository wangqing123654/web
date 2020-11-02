package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.EPic;
import com.dongyang.util.TypeTool;
import com.dongyang.util.StringTool;
import java.awt.Color;
import com.dongyang.tui.DColor;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import java.util.Vector;
import com.dongyang.tui.text.graph.Axis;

/**
 *
 * <p>Title: </p>
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
public class PicDataDialogControl extends TControl{
    EPic pic;
    TTable columnTable;
    TTable rowTable;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        pic = (EPic)getParameter();
        columnTable = (TTable)getComponent("COLUMN_TABLE");
        rowTable = (TTable)getComponent("ROW_TABLE");
        setValue("PIC_NAME",pic.getName());
        setValue("PIC_WIDTH",pic.getWidth());
        setValue("PIC_Height",pic.getHeight());
        //��ʼ��
        initData();
    }
    /**
     * ��ʼ��
     */
    public void initData()
    {
        if(pic.getPicData() != null)
        {
            setValue("POWER_DATA", true);
            setType(pic.getPicData().getType());
            setValue("PIC_DATA_BKCOLOR",pic.getPicData().getBackgroundString());
            Color color = pic.getPicData().getBackground();
            if(color == null)
                color = new Color(0,0,0);
            callFunction("UI|PIC_DATA_BKCOLOR_D|setForeground",color);
            setValue("PIC_X",pic.getPicData().getX());
            setValue("PIC_Y",pic.getPicData().getY());
            //��ʼ����
            initColumn();
            //��ʼ����
            initRow();
            //��ʼ��������
            initNumberAxis();
            //��ʼ��������
            initTypeAxis();
        }
    }
    /**
     * ��ʼ��������
     */
    public void initNumberAxis()
    {
        Axis axis = pic.getPicData().getNumberAxis();
        setValue("N_VISIBLE",axis.isVisible());
        setValue("N_WAY",axis.getWay());
        setValue("N_MIN_VALUE",axis.getMinValue());
        setValue("N_MAX_VALUE",axis.getMaxValue());
        setValue("N_NAIN_UNIT",axis.getMainUnit());
        setValue("N_SECOND_UNIT",axis.getSecondUnit());
        setValue("N_MAIN_LINE_COLOR",axis.getMainLineColorString());
        Color c = axis.getMainLineColor();
        if(c == null)
            c = new Color(255,255,255);
        callFunction("UI|N_MAIN_LINE_COLOR_D|setForeground",c);
        setValue("N_MAIN_LINE_TYPE",axis.getMainLineType());
        setValue("N_MAIN_LINE_WIDTH",axis.getMainLineWidth());
        setValue("N_MAIN_LINE_WIDTH1",axis.getMainLineWidth1());
        setValue("N_MAIN_AXIS_LINE_COLOR",axis.getMainAxisLineColorString());
        c = axis.getMainAxisLineColor();
        if(c == null)
            c = new Color(255,255,255);
        callFunction("UI|N_MAIN_AXIS_LINE_COLOR_D|setForeground",c);
        setValue("N_MAIN_AXIS_LINE_TYPE",axis.getMainAxisLineType());
        setValue("N_MAIN_AXIS_LINE_WIDTH",axis.getMainAxisLineWidth());
        setValue("N_MAIN_AXIS_LINE_WIDTH1",axis.getMainAxisLineWidth1());
        setValue("N_MAIN_WIDTH",axis.getMainWidth());
        setValue("N_MAIN_TYPE_WAY_1",axis.isMainTypeWay(1));
        setValue("N_MAIN_TYPE_WAY_2",axis.isMainTypeWay(2));
        setValue("N_MAIN_TYPE_" + axis.getMainType(),true);
        setValue("N_SECOND_AXIS_LINE_COLOR",axis.getSecondAxisLineColorString());
        c = axis.getSecondAxisLineColor();
        if(c == null)
            c = new Color(255,255,255);
        callFunction("UI|N_SECOND_AXIS_LINE_COLOR_D|setForeground",c);
        setValue("N_SECOND_AXIS_LINE_TYPE",axis.getSecondAxisLineType());
        setValue("N_SECOND_AXIS_LINE_WIDTH",axis.getSecondAxisLineWidth());
        setValue("N_SECOND_AXIS_LINE_WIDTH1",axis.getSecondAxisLineWidth1());
        setValue("N_SECOND_WIDTH",axis.getSecondWidth());
        setValue("N_SECOND_TYPE_WAY_1",axis.isSecondTypeWay(1));
        setValue("N_SECOND_TYPE_WAY_2",axis.isSecondTypeWay(2));
        setValue("N_SECOND_TYPE_" + axis.getSecondType(),true);
        setValue("N_BACK_MAIN_LINE_VISIBLE",axis.isBackMainVisible());
        setValue("N_BACK_MAIN_LINE_COLOR",axis.getBackMainLineColorString());
        c = axis.getBackMainLineColor();
        if(c == null)
            c = new Color(255,255,255);
        callFunction("UI|N_BACK_MAIN_LINE_COLOR_D|setForeground",c);
        setValue("N_BACK_MAIN_LINE_TYPE",axis.getBackMainLineType());
        setValue("N_BACK_MAIN_LINE_WIDTH",axis.getBackMainLineWidth());
        setValue("N_BACK_MAIN_LINE_WIDTH1",axis.getBackMainLineWidth1());
        setValue("N_BACK_SECOND_LINE_VISIBLE",axis.isBackSecondVisible());
        setValue("N_BACK_SECOND_LINE_COLOR",axis.getBackSecondLineColorString());
        c = axis.getBackSecondLineColor();
        if(c == null)
            c = new Color(255,255,255);
        callFunction("UI|N_BACK_SECOND_LINE_COLOR_D|setForeground",c);
        setValue("N_BACK_SECOND_LINE_TYPE",axis.getBackSecondLineType());
        setValue("N_BACK_SECOND_LINE_WIDTH",axis.getBackSecondLineWidth());
        setValue("N_BACK_SECOND_LINE_WIDTH1",axis.getBackSecondLineWidth1());
        setValue("N_TEXT_COLOR",axis.getTextColorString());
        c = axis.getTextColor();
        if(c == null)
            c = new Color(255,255,255);
        callFunction("UI|N_TEXT_COLOR_D|setForeground",c);
        setValue("N_TEXT_FONT",axis.getTextFontName());
        setValue("N_TEXT_SIZE","" + axis.getTextFontSize());
        setValue("N_DB",axis.isTextFontBold());
        setValue("N_DI",axis.isTextFontItalic());
        setValue("N_TEXT_ROTATE",axis.getTextFontRotate());
        setValue("N_TEXT_WAY_1",axis.isTextWay(1));
        setValue("N_TEXT_WAY_2",axis.isTextWay(2));
        setValue("N_TEXT_OFFSET",axis.getTextOffset());
        setValue("N_TEXT_AUTO_ENTER",axis.isTextAutoEnter());
        setValue("N_TEXT_ENTER_OFFSET",axis.getTextEnterOffset());
        setValue("N_TEXT_OFFSET_FORMAT",axis.getTextOffsetFormat());
        setValue("N_TEXT_CENTER",axis.isTextCenter());
    }
    /**
     * ��ʼ��������
     */
    public void initTypeAxis()
    {
        Axis axis = pic.getPicData().getTypeAxis();
        setValue("T_VISIBLE",axis.isVisible());
        setValue("T_WAY",axis.getWay());
        setValue("T_MIN_VALUE",axis.getMinValue());
        setValue("T_MAX_VALUE",axis.getMaxValue());
        setValue("T_NAIN_UNIT",axis.getMainUnit());
        setValue("T_SECOND_UNIT",axis.getSecondUnit());
        setValue("T_MAIN_LINE_COLOR",axis.getMainLineColorString());
        Color c = axis.getMainLineColor();
        if(c == null)
            c = new Color(255,255,255);
        callFunction("UI|T_MAIN_LINE_COLOR_D|setForeground",c);
        setValue("T_MAIN_LINE_TYPE",axis.getMainLineType());
        setValue("T_MAIN_LINE_WIDTH",axis.getMainLineWidth());
        setValue("T_MAIN_LINE_WIDTH1",axis.getMainLineWidth1());
        setValue("T_MAIN_AXIS_LINE_COLOR",axis.getMainAxisLineColorString());
        c = axis.getMainAxisLineColor();
        if(c == null)
            c = new Color(255,255,255);
        callFunction("UI|T_MAIN_AXIS_LINE_COLOR_D|setForeground",c);
        setValue("T_MAIN_AXIS_LINE_TYPE",axis.getMainAxisLineType());
        setValue("T_MAIN_AXIS_LINE_WIDTH",axis.getMainAxisLineWidth());
        setValue("T_MAIN_AXIS_LINE_WIDTH1",axis.getMainAxisLineWidth1());
        setValue("T_MAIN_WIDTH",axis.getMainWidth());
        setValue("T_MAIN_TYPE_WAY_1",axis.isMainTypeWay(1));
        setValue("T_MAIN_TYPE_WAY_2",axis.isMainTypeWay(2));
        setValue("T_MAIN_TYPE_" + axis.getMainType(),true);
        setValue("T_SECOND_AXIS_LINE_COLOR",axis.getSecondAxisLineColorString());
        c = axis.getSecondAxisLineColor();
        if(c == null)
            c = new Color(255,255,255);
        callFunction("UI|T_SECOND_AXIS_LINE_COLOR_D|setForeground",c);
        setValue("T_SECOND_AXIS_LINE_TYPE",axis.getSecondAxisLineType());
        setValue("T_SECOND_AXIS_LINE_WIDTH",axis.getSecondAxisLineWidth());
        setValue("T_SECOND_AXIS_LINE_WIDTH1",axis.getSecondAxisLineWidth1());
        setValue("T_SECOND_WIDTH",axis.getSecondWidth());
        setValue("T_SECOND_TYPE_WAY_1",axis.isSecondTypeWay(1));
        setValue("T_SECOND_TYPE_WAY_2",axis.isSecondTypeWay(2));
        setValue("T_SECOND_TYPE_" + axis.getSecondType(),true);
        setValue("T_BACK_MAIN_LINE_VISIBLE",axis.isBackMainVisible());
        setValue("T_BACK_MAIN_LINE_COLOR",axis.getBackMainLineColorString());
        c = axis.getBackMainLineColor();
        if(c == null)
            c = new Color(255,255,255);
        callFunction("UI|T_BACK_MAIN_LINE_COLOR_D|setForeground",c);
        setValue("T_BACK_MAIN_LINE_TYPE",axis.getBackMainLineType());
        setValue("T_BACK_MAIN_LINE_WIDTH",axis.getBackMainLineWidth());
        setValue("T_BACK_MAIN_LINE_WIDTH1",axis.getBackMainLineWidth1());
        setValue("T_BACK_SECOND_LINE_VISIBLE",axis.isBackSecondVisible());
        setValue("T_BACK_SECOND_LINE_COLOR",axis.getBackSecondLineColorString());
        c = axis.getBackSecondLineColor();
        if(c == null)
            c = new Color(255,255,255);
        callFunction("UI|T_BACK_SECOND_LINE_COLOR_D|setForeground",c);
        setValue("T_BACK_SECOND_LINE_TYPE",axis.getBackSecondLineType());
        setValue("T_BACK_SECOND_LINE_WIDTH",axis.getBackSecondLineWidth());
        setValue("T_BACK_SECOND_LINE_WIDTH1",axis.getBackSecondLineWidth1());
        setValue("T_TEXT_COLOR",axis.getTextColorString());
        c = axis.getTextColor();
        if(c == null)
            c = new Color(255,255,255);
        callFunction("UI|T_TEXT_COLOR_D|setForeground",c);
        setValue("T_TEXT_FONT",axis.getTextFontName());
        setValue("T_TEXT_SIZE","" + axis.getTextFontSize());
        setValue("T_DB",axis.isTextFontBold());
        setValue("T_DI",axis.isTextFontItalic());
        setValue("T_TEXT_ROTATE",axis.getTextFontRotate());
        setValue("T_TEXT_WAY_1",axis.isTextWay(1));
        setValue("T_TEXT_WAY_2",axis.isTextWay(2));
        setValue("T_TEXT_OFFSET",axis.getTextOffset());
        setValue("T_TEXT_AUTO_ENTER",axis.isTextAutoEnter());
        setValue("T_TEXT_ENTER_OFFSET",axis.getTextEnterOffset());
        setValue("T_TEXT_OFFSET_FORMAT",axis.getTextOffsetFormat());
        setValue("T_TEXT_CENTER",axis.isTextCenter());
    }
    /**
     * ��ʼ����
     */
    public void initColumn()
    {
        String column[] = pic.getPicData().getColumns();
        TParm parm = new TParm();
        for(int i = 0;i < column.length;i++)
            parm.addData("COLUMN",column[i]);
        columnTable.setParmValue(parm);
    }
    /**
     * ��ʼ����
     */
    public void initRow()
    {
        String rows[] = pic.getPicData().getRows();
        TParm parm = new TParm();
        for(int i = 0;i < rows.length;i++)
            parm.addData("ROW",rows[i]);
        rowTable.setParmValue(parm);
    }
    /**
     * ��������
     */
    public void onPicName()
    {
        pic.setName(getText("PIC_NAME"));
    }
    /**
     * ����ͼ�����
     */
    public void onPicWidth()
    {
        pic.setWidth(TypeTool.getInt(getValue("PIC_WIDTH")));
        pic.update();
    }
    /**
     * ����ͼ���߶�
     */
    public void onPicHeight()
    {
        pic.setHeight(TypeTool.getInt(getValue("PIC_HEIGHT")));
        pic.update();
    }
    /**
     * ����
     */
    public void onPowerData()
    {
        if(TypeTool.getBoolean(getValue("POWER_DATA")))
        {
            pic.createPicData();
            setType(pic.getPicData().getType());
            //��ʼ��
            initData();
        }
        else
            pic.setPicData(null);
        pic.update();
    }
    /**
     * ѡ������
     * @param type int
     */
    public void onSelectType(int type)
    {
        if(pic.getPicData() == null)
            return;
        setType(type);
        pic.getPicData().setType(type);
        pic.update();
    }
    /**
     * ����ѡ������
     * @param type int
     */
    private void setType(int type)
    {
        switch(type)
        {
            case 1:
                callFunction("UI|TYPE_1|setPictureName","PicData01F.JPG");
                callFunction("UI|TYPE_2|setPictureName","PicData02.JPG");
                callFunction("UI|TYPE_3|setPictureName","PicData03.JPG");
                break;
            case 2:
                callFunction("UI|TYPE_1|setPictureName","PicData01.JPG");
                callFunction("UI|TYPE_2|setPictureName","PicData02F.JPG");
                callFunction("UI|TYPE_3|setPictureName","PicData03.JPG");
                break;
            case 3:
                callFunction("UI|TYPE_1|setPictureName","PicData01.JPG");
                callFunction("UI|TYPE_2|setPictureName","PicData02.JPG");
                callFunction("UI|TYPE_3|setPictureName","PicData03F.JPG");
                break;
        }
    }
    /**
     * ���ñ�����ɫ
     */
    public void onPicDataBkColor()
    {
        Color color = StringTool.getColor(getText("PIC_DATA_BKCOLOR"),getConfigParm());
        pic.getPicData().setBackground(color);
        if(color == null)
            color = new Color(0,0,0);
        callFunction("UI|PIC_DATA_BKCOLOR_D|setForeground",color);
        pic.update();
    }
    /**
     * ������ɫ��ɫ��
     */
    public void onPicDataBkColorD()
    {
        Color c = pic.getPicData().getBackground();
        if(c == null)
            c = new Color(255,255,255);
        c = DColor.colorDialog(getComponent(),c);
        pic.getPicData().setBackground(c);
        setValue("PIC_DATA_BKCOLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|PIC_DATA_BKCOLOR_D|setForeground",c);
        pic.update();
    }
    public void onPicX()
    {
        pic.getPicData().setX(TypeTool.getInt(getValue("PIC_X")));
        pic.update();
    }
    public void onPicY()
    {
        pic.getPicData().setY(TypeTool.getInt(getValue("PIC_Y")));
        pic.update();
    }
    /**
     * ���޸�
     */
    public void onColumnTextChange()
    {
        TParm parm = columnTable.getParmValue();
        Vector v = ((Vector)parm.getData("COLUMN"));
        if(v == null)
            return;
        pic.getPicData().setColumns((String[])v.toArray(new String[]{}));
        pic.update();
    }
    /**
     * ������
     */
    public void onRowTextChange()
    {
        TParm parm = rowTable.getParmValue();
        Vector v = ((Vector)parm.getData("ROW"));
        if(v == null)
            return;
        pic.getPicData().setRows((String[])v.toArray(new String[]{}));
        pic.update();
    }
    /**
     * ������
     */
    public void onColumnInsert()
    {
        int row = columnTable.getSelectedRow();
        columnTable.addRow(row);
        onColumnTextChange();
    }
    /**
     * ׷����
     */
    public void onColumnAdd()
    {
        columnTable.addRow();
        onColumnTextChange();
    }
    /**
     * ɾ����
     */
    public void onColumnDelete()
    {
        columnTable.removeRow();
        onColumnTextChange();
    }
    /**
     * ����������
     */
    public void onRowDataInsert()
    {
        int row = rowTable.getSelectedRow();
        rowTable.addRow(row);
        onRowTextChange();
    }
    /**
     * ׷��������
     */
    public void onRowDataAdd()
    {
        rowTable.addRow();
        onRowTextChange();
    }
    /**
     * ɾ��������
     */
    public void onRowDataDelete()
    {
        rowTable.removeRow();
        onRowTextChange();
    }
    /**
     * ��ʾ������
     */
    public void onNVisible()
    {
        pic.getPicData().getNumberAxis().setVisible(TypeTool.getBoolean(getValue("N_VISIBLE")));
        pic.update();
    }
    /**
     * �����᷽��
     */
    public void onNWay()
    {
        pic.getPicData().getNumberAxis().setWay(TypeTool.getBoolean(getValue("N_WAY")));
        pic.update();
    }
    /**
     * ������������Сֵ
     */
    public void onNMinValue()
    {
        pic.getPicData().getNumberAxis().setMinValue(TypeTool.getDouble(getValue("N_MIN_VALUE")));
        pic.update();
    }
    /**
     * �������������ֵ
     */
    public void onNMaxValue()
    {
        pic.getPicData().getNumberAxis().setMaxValue(TypeTool.getDouble(getValue("N_MAX_VALUE")));
        pic.update();
    }
    /**
     * �������������߶ȵ�λ
     */
    public void onNMainUnit()
    {
        pic.getPicData().getNumberAxis().setMainUnit(TypeTool.getDouble(getValue("N_NAIN_UNIT")));
        pic.update();
    }
    /**
     * ����������γ߶ȵ�λ
     */
    public void onNSecondUnit()
    {
        pic.getPicData().getNumberAxis().setSecondUnit(TypeTool.getDouble(getValue("N_SECOND_UNIT")));
        pic.update();
    }
    /**
     * ������ɫ
     */
    public void onNMainLineColor()
    {
        Color color = StringTool.getColor(getText("N_MAIN_LINE_COLOR"),getConfigParm());
        pic.getPicData().getNumberAxis().setMainLineColor(color);
        if(color == null)
            color = new Color(0,0,0);
        callFunction("UI|N_MAIN_LINE_COLOR_D|setForeground",color);
        pic.update();
    }
    /**
     * ������ɫ��ɫ��
     */
    public void onNMainLineColorD()
    {
        Color c = pic.getPicData().getNumberAxis().getMainLineColor();
        if(c == null)
            c = new Color(255,255,255);
        c = DColor.colorDialog(getComponent(),c);
        pic.getPicData().getNumberAxis().setMainLineColor(c);
        setValue("N_MAIN_LINE_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|N_MAIN_LINE_COLOR_D|setForeground",c);
        pic.update();
    }
    /**
     * ��������
     */
    public void onNMainLineType()
    {
        pic.getPicData().getNumberAxis().setMainLineType(TypeTool.getInt(getValue("N_MAIN_LINE_TYPE")));
        pic.update();
    }
    /**
     * �����߸�
     */
    public void onNMainLineWidth()
    {
        pic.getPicData().getNumberAxis().setMainLineWidth(TypeTool.getFloat(getValue("N_MAIN_LINE_WIDTH")));
        pic.update();
    }
    /**
     * �����߸�
     */
    public void onNMainLineWidth1()
    {
        pic.getPicData().getNumberAxis().setMainLineWidth1(TypeTool.getFloat(getValue("N_MAIN_LINE_WIDTH1")));
        pic.update();
    }
    /**
     * �������ɫ
     */
    public void onNMainAxisLineColor()
    {
        Color color = StringTool.getColor(getText("N_MAIN_AXIS_LINE_COLOR"),getConfigParm());
        pic.getPicData().getNumberAxis().setMainAxisLineColor(color);
        if(color == null)
            color = new Color(0,0,0);
        callFunction("UI|N_MAIN_AXIS_LINE_COLOR_D|setForeground",color);
        pic.update();
    }
    /**
     * �������ɫ��ɫ��
     */
    public void onNMainAxisLineColorD()
    {
        Color c = pic.getPicData().getNumberAxis().getMainAxisLineColor();
        if(c == null)
            c = new Color(255,255,255);
        c = DColor.colorDialog(getComponent(),c);
        pic.getPicData().getNumberAxis().setMainAxisLineColor(c);
        setValue("N_MAIN_AXIS_LINE_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|N_MAIN_AXIS_LINE_COLOR_D|setForeground",c);
        pic.update();
    }
    /**
     * ���������
     */
    public void onNMainAxisLineType()
    {
        pic.getPicData().getNumberAxis().setMainAxisLineType(TypeTool.getInt(getValue("N_MAIN_AXIS_LINE_TYPE")));
        pic.update();
    }
    /**
     * ������߸�
     */
    public void onNMainAxisLineWidth()
    {
        pic.getPicData().getNumberAxis().setMainAxisLineWidth(TypeTool.getFloat(getValue("N_MAIN_AXIS_LINE_WIDTH")));
        pic.update();
    }
    /**
     * ������߸�
     */
    public void onNMainAxisLineWidth1()
    {
        pic.getPicData().getNumberAxis().setMainAxisLineWidth1(TypeTool.getFloat(getValue("N_MAIN_AXIS_LINE_WIDTH1")));
        pic.update();
    }
    /**
     * ���̶��߿��
     */
    public void onNMainWidth()
    {
        pic.getPicData().getNumberAxis().setMainWidth(TypeTool.getInt(getValue("N_MAIN_WIDTH")));
        pic.update();
    }
    /**
     * ���̶ȷ���1
     */
    public void onNMainTypeWay1()
    {
        pic.getPicData().getNumberAxis().setMainTypeWay(1,TypeTool.getBoolean(getValue("N_MAIN_TYPE_WAY_1")));
        pic.update();
    }
    /**
     * ���̶ȷ���2
     */
    public void onNMainTypeWay2()
    {
        pic.getPicData().getNumberAxis().setMainTypeWay(2,TypeTool.getBoolean(getValue("N_MAIN_TYPE_WAY_2")));
        pic.update();
    }
    /**
     * ���̶�����
     * @param type int
     */
    public void onNMainType(int type)
    {
        pic.getPicData().getNumberAxis().setMainType(type);
        pic.update();
    }
    /**
     * �α����ɫ
     */
    public void onNSecondAxisLineColor()
    {
        Color color = StringTool.getColor(getText("N_SECOND_AXIS_LINE_COLOR"),getConfigParm());
        pic.getPicData().getNumberAxis().setSecondAxisLineColor(color);
        if(color == null)
            color = new Color(0,0,0);
        callFunction("UI|N_SECOND_AXIS_LINE_COLOR_D|setForeground",color);
        pic.update();
    }
    /**
     * �α����ɫ��ɫ��
     */
    public void onNSecondAxisLineColorD()
    {
        Color c = pic.getPicData().getNumberAxis().getSecondAxisLineColor();
        if(c == null)
            c = new Color(255,255,255);
        c = DColor.colorDialog(getComponent(),c);
        pic.getPicData().getNumberAxis().setSecondAxisLineColor(c);
        setValue("N_SECOND_AXIS_LINE_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|N_SECOND_AXIS_LINE_COLOR_D|setForeground",c);
        pic.update();
    }
    /**
     * �α������
     */
    public void onNSecondAxisLineType()
    {
        pic.getPicData().getNumberAxis().setSecondAxisLineType(TypeTool.getInt(getValue("N_SECOND_AXIS_LINE_TYPE")));
        pic.update();
    }
    /**
     * �α���߸�
     */
    public void onNSecondAxisLineWidth()
    {
        pic.getPicData().getNumberAxis().setSecondAxisLineWidth(TypeTool.getFloat(getValue("N_SECOND_AXIS_LINE_WIDTH")));
        pic.update();
    }
    /**
     * �α�������߼�
     */
    public void onNSecondAxisLineWidth1()
    {
        pic.getPicData().getNumberAxis().setSecondAxisLineWidth1(TypeTool.getFloat(getValue("N_SECOND_AXIS_LINE_WIDTH1")));
        pic.update();
    }
    /**
     * �ο̶��߿��
     */
    public void onNSecondWidth()
    {
        pic.getPicData().getNumberAxis().setSecondWidth(TypeTool.getInt(getValue("N_SECOND_WIDTH")));
        pic.update();
    }
    /**
     * �ο̶ȷ���1
     */
    public void onNSecondTypeWay1()
    {
        pic.getPicData().getNumberAxis().setSecondTypeWay(1,TypeTool.getBoolean(getValue("N_SECOND_TYPE_WAY_1")));
        pic.update();
    }
    /**
     * �ο̶ȷ���2
     */
    public void onNSecondTypeWay2()
    {
        pic.getPicData().getNumberAxis().setSecondTypeWay(2,TypeTool.getBoolean(getValue("N_SECOND_TYPE_WAY_2")));
        pic.update();
    }
    /**
     * �ο̶�����
     * @param type int
     */
    public void onNSecondType(int type)
    {
        pic.getPicData().getNumberAxis().setSecondType(type);
        pic.update();
    }
    /**
     * �������̶�����ɫ
     */
    public void onNBackMainLineColor()
    {
        Color color = StringTool.getColor(getText("N_BACK_MAIN_LINE_COLOR"),getConfigParm());
        pic.getPicData().getNumberAxis().setBackMainLineColor(color);
        if(color == null)
            color = new Color(0,0,0);
        callFunction("UI|N_BACK_MAIN_LINE_COLOR_D|setForeground",color);
        pic.update();
    }
    /**
     * �������̶�����ɫ��ɫ��
     */
    public void onNBackMainLineColorD()
    {
        Color c = pic.getPicData().getNumberAxis().getBackMainLineColor();
        if(c == null)
            c = new Color(255,255,255);
        c = DColor.colorDialog(getComponent(),c);
        pic.getPicData().getNumberAxis().setBackMainLineColor(c);
        setValue("N_BACK_MAIN_LINE_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|N_BACK_MAIN_LINE_COLOR_D|setForeground",c);
        pic.update();
    }
    /**
     * �������̶�������
     */
    public void onNBackMainLineType()
    {
        pic.getPicData().getNumberAxis().setBackMainLineType(TypeTool.getInt(getValue("N_BACK_MAIN_LINE_TYPE")));
        pic.update();
    }
    /**
     * �������̶��߸߶�
     */
    public void onNBackMainLineWidth()
    {
        pic.getPicData().getNumberAxis().setBackMainLineWidth(TypeTool.getFloat(getValue("N_BACK_MAIN_LINE_WIDTH")));
        pic.update();
    }
    /**
     * �������̶��������߼�
     */
    public void onNBackMainLineWidth1()
    {
        pic.getPicData().getNumberAxis().setBackMainLineWidth1(TypeTool.getFloat(getValue("N_BACK_MAIN_LINE_WIDTH1")));
        pic.update();
    }
    /**
     * �Ƿ���ʾ�������̶���
     */
    public void onNBackMainLineVisible()
    {
        pic.getPicData().getNumberAxis().setBackMainVisible(TypeTool.getBoolean(getValue("N_BACK_MAIN_LINE_VISIBLE")));
        pic.update();
    }
    /**
     * �����ο̶�����ɫ
     */
    public void onNBackSecondLineColor()
    {
        Color color = StringTool.getColor(getText("N_BACK_SECOND_LINE_COLOR"),getConfigParm());
        pic.getPicData().getNumberAxis().setBackSecondLineColor(color);
        if(color == null)
            color = new Color(0,0,0);
        callFunction("UI|N_BACK_SECOND_LINE_COLOR_D|setForeground",color);
        pic.update();
    }
    /**
     * �����ο̶�����ɫ��ɫ��
     */
    public void onNBackSecondLineColorD()
    {
        Color c = pic.getPicData().getNumberAxis().getBackSecondLineColor();
        if(c == null)
            c = new Color(255,255,255);
        c = DColor.colorDialog(getComponent(),c);
        pic.getPicData().getNumberAxis().setBackSecondLineColor(c);
        setValue("N_BACK_SECOND_LINE_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|N_BACK_SECOND_LINE_COLOR_D|setForeground",c);
        pic.update();
    }
    /**
     * �����ο̶�������
     */
    public void onNBackSecondLineType()
    {
        pic.getPicData().getNumberAxis().setBackSecondLineType(TypeTool.getInt(getValue("N_BACK_SECOND_LINE_TYPE")));
        pic.update();
    }
    /**
     * �����ο̶��߸߶�
     */
    public void onNBackSecondLineWidth()
    {
        pic.getPicData().getNumberAxis().setBackSecondLineWidth(TypeTool.getFloat(getValue("N_BACK_SECOND_LINE_WIDTH")));
        pic.update();
    }
    /**
     * �����ο̶��������߼�
     */
    public void onNBackSecondLineWidth1()
    {
        pic.getPicData().getNumberAxis().setBackSecondLineWidth1(TypeTool.getFloat(getValue("N_BACK_SECOND_LINE_WIDTH1")));
        pic.update();
    }
    /**
     * �Ƿ���ʾ�����ο̶���
     */
    public void onNBackSecondLineVisible()
    {
        pic.getPicData().getNumberAxis().setBackSecondVisible(TypeTool.getBoolean(getValue("N_BACK_SECOND_LINE_VISIBLE")));
        pic.update();
    }
    /**
     * �����ο̶�����ɫ
     */
    public void onNTextColor()
    {
        Color color = StringTool.getColor(getText("N_TEXT_COLOR"),getConfigParm());
        pic.getPicData().getNumberAxis().setTextColor(color);
        if(color == null)
            color = new Color(0,0,0);
        callFunction("UI|N_TEXT_COLOR_D|setForeground",color);
        pic.update();
    }
    /**
     * �����ο̶�����ɫ��ɫ��
     */
    public void onNTextColorD()
    {
        Color c = pic.getPicData().getNumberAxis().getTextColor();
        if(c == null)
            c = new Color(255,255,255);
        c = DColor.colorDialog(getComponent(),c);
        pic.getPicData().getNumberAxis().setTextColor(c);
        setValue("N_TEXT_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|N_TEXT_COLOR_D|setForeground",c);
        pic.update();
    }
    /**
     * ����
     */
    public void onNTextFont()
    {
        pic.getPicData().getNumberAxis().setTextFontName(TypeTool.getString(getValue("N_TEXT_FONT")));
        pic.update();
    }
    /**
     * �ֺ�
     */
    public void onNTextSize()
    {
        pic.getPicData().getNumberAxis().setTextFontSize(TypeTool.getInt(getValue("N_TEXT_SIZE")));
        pic.update();
    }
    /**
     * ����
     */
    public void onNDB()
    {
        pic.getPicData().getNumberAxis().setTextFontBold(TypeTool.getBoolean(getValue("N_DB")));
        pic.update();
    }
    /**
     * б��
     */
    public void onNDI()
    {
        pic.getPicData().getNumberAxis().setTextFontItalic(TypeTool.getBoolean(getValue("N_DI")));
        pic.update();
    }
    /**
     * ������ת
     */
    public void onNTextRotate()
    {
        pic.getPicData().getNumberAxis().setTextFontRotate(TypeTool.getDouble(getValue("N_TEXT_ROTATE")));
        pic.update();
    }
    /**
     * ����1
     */
    public void onNTextWay1()
    {
        pic.getPicData().getNumberAxis().setTextWay(1,TypeTool.getBoolean(getValue("N_TEXT_WAY_1")));
        pic.update();
    }
    /**
     * ����2
     */
    public void onNTextWay2()
    {
        pic.getPicData().getNumberAxis().setTextWay(2,TypeTool.getBoolean(getValue("N_TEXT_WAY_2")));
        pic.update();
    }
    /**
     * ����ƫ��
     */
    public void onNTextOffset()
    {
        pic.getPicData().getNumberAxis().setTextOffset(TypeTool.getInt(getValue("N_TEXT_OFFSET")));
        pic.update();
    }
    /**
     * �����Զ�����
     */
    public void onNTextAutoEnter()
    {
        pic.getPicData().getNumberAxis().setTextAutoEnter(TypeTool.getBoolean(getValue("N_TEXT_AUTO_ENTER")));
        pic.update();
    }
    /**
     * �Զ�����ƫ��
     */
    public void onNTextEnterOffset()
    {
        pic.getPicData().getNumberAxis().setTextEnterOffset(TypeTool.getInt(getValue("N_TEXT_ENTER_OFFSET")));
        pic.update();
    }
    /**
     * ���ָ�ʽ
     */
    public void onNTextOffsetFormat()
    {
        pic.getPicData().getNumberAxis().setTextOffsetFormat(TypeTool.getString(getValue("N_TEXT_OFFSET_FORMAT")));
        pic.update();
    }
    /**
     * ���־���
     */
    public void onNTextCenter()
    {
        pic.getPicData().getNumberAxis().setTextCenter(TypeTool.getBoolean(getValue("N_TEXT_CENTER")));
        pic.update();
    }
    /**
     * ��ʾ������
     */
    public void onTVisible()
    {
        pic.getPicData().getTypeAxis().setVisible(TypeTool.getBoolean(getValue("T_VISIBLE")));
        pic.update();
    }
    /**
     * �����᷽��
     */
    public void onTWay()
    {
        pic.getPicData().getTypeAxis().setWay(TypeTool.getBoolean(getValue("T_WAY")));
        pic.update();
    }
    /**
     * ���÷�������Сֵ
     */
    public void onTMinValue()
    {
        pic.getPicData().getTypeAxis().setMinValue(TypeTool.getDouble(getValue("T_MIN_VALUE")));
        pic.update();
    }
    /**
     * ���÷��������ֵ
     */
    public void onTMaxValue()
    {
        pic.getPicData().getTypeAxis().setMaxValue(TypeTool.getDouble(getValue("T_MAX_VALUE")));
        pic.update();
    }
    /**
     * ���÷��������߶ȵ�λ
     */
    public void onTMainUnit()
    {
        pic.getPicData().getTypeAxis().setMainUnit(TypeTool.getDouble(getValue("T_NAIN_UNIT")));
        pic.update();
    }
    /**
     * ���÷�����γ߶ȵ�λ
     */
    public void onTSecondUnit()
    {
        pic.getPicData().getTypeAxis().setSecondUnit(TypeTool.getDouble(getValue("T_SECOND_UNIT")));
        pic.update();
    }
    /**
     * ������ɫ
     */
    public void onTMainLineColor()
    {
        Color color = StringTool.getColor(getText("T_MAIN_LINE_COLOR"),getConfigParm());
        pic.getPicData().getTypeAxis().setMainLineColor(color);
        if(color == null)
            color = new Color(0,0,0);
        callFunction("UI|T_MAIN_LINE_COLOR_D|setForeground",color);
        pic.update();
    }
    /**
     * ������ɫ��ɫ��
     */
    public void onTMainLineColorD()
    {
        Color c = pic.getPicData().getTypeAxis().getMainLineColor();
        if(c == null)
            c = new Color(255,255,255);
        c = DColor.colorDialog(getComponent(),c);
        pic.getPicData().getTypeAxis().setMainLineColor(c);
        setValue("T_MAIN_LINE_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|T_MAIN_LINE_COLOR_D|setForeground",c);
        pic.update();
    }
    /**
     * ��������
     */
    public void onTMainLineType()
    {
        pic.getPicData().getTypeAxis().setMainLineType(TypeTool.getInt(getValue("T_MAIN_LINE_TYPE")));
        pic.update();
    }
    /**
     * �����߸�
     */
    public void onTMainLineWidth()
    {
        pic.getPicData().getTypeAxis().setMainLineWidth(TypeTool.getFloat(getValue("T_MAIN_LINE_WIDTH")));
        pic.update();
    }
    /**
     * �����߸�
     */
    public void onTMainLineWidth1()
    {
        pic.getPicData().getTypeAxis().setMainLineWidth1(TypeTool.getFloat(getValue("T_MAIN_LINE_WIDTH1")));
        pic.update();
    }
    /**
     * �������ɫ
     */
    public void onTMainAxisLineColor()
    {
        Color color = StringTool.getColor(getText("T_MAIN_AXIS_LINE_COLOR"),getConfigParm());
        pic.getPicData().getTypeAxis().setMainAxisLineColor(color);
        if(color == null)
            color = new Color(0,0,0);
        callFunction("UI|T_MAIN_AXIS_LINE_COLOR_D|setForeground",color);
        pic.update();
    }
    /**
     * �������ɫ��ɫ��
     */
    public void onTMainAxisLineColorD()
    {
        Color c = pic.getPicData().getTypeAxis().getMainAxisLineColor();
        if(c == null)
            c = new Color(255,255,255);
        c = DColor.colorDialog(getComponent(),c);
        pic.getPicData().getTypeAxis().setMainAxisLineColor(c);
        setValue("T_MAIN_AXIS_LINE_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|T_MAIN_AXIS_LINE_COLOR_D|setForeground",c);
        pic.update();
    }
    /**
     * ���������
     */
    public void onTMainAxisLineType()
    {
        pic.getPicData().getTypeAxis().setMainAxisLineType(TypeTool.getInt(getValue("T_MAIN_AXIS_LINE_TYPE")));
        pic.update();
    }
    /**
     * ������߸�
     */
    public void onTMainAxisLineWidth()
    {
        pic.getPicData().getTypeAxis().setMainAxisLineWidth(TypeTool.getFloat(getValue("T_MAIN_AXIS_LINE_WIDTH")));
        pic.update();
    }
    /**
     * ������߸�
     */
    public void onTMainAxisLineWidth1()
    {
        pic.getPicData().getTypeAxis().setMainAxisLineWidth1(TypeTool.getFloat(getValue("T_MAIN_AXIS_LINE_WIDTH1")));
        pic.update();
    }
    /**
     * ���̶��߿��
     */
    public void onTMainWidth()
    {
        pic.getPicData().getTypeAxis().setMainWidth(TypeTool.getInt(getValue("T_MAIN_WIDTH")));
        pic.update();
    }
    /**
     * ���̶ȷ���1
     */
    public void onTMainTypeWay1()
    {
        pic.getPicData().getTypeAxis().setMainTypeWay(1,TypeTool.getBoolean(getValue("T_MAIN_TYPE_WAY_1")));
        pic.update();
    }
    /**
     * ���̶ȷ���2
     */
    public void onTMainTypeWay2()
    {
        pic.getPicData().getTypeAxis().setMainTypeWay(2,TypeTool.getBoolean(getValue("T_MAIN_TYPE_WAY_2")));
        pic.update();
    }
    /**
     * ���̶�����
     * @param type int
     */
    public void onTMainType(int type)
    {
        pic.getPicData().getTypeAxis().setMainType(type);
        pic.update();
    }
    /**
     * �α����ɫ
     */
    public void onTSecondAxisLineColor()
    {
        Color color = StringTool.getColor(getText("T_SECOND_AXIS_LINE_COLOR"),getConfigParm());
        pic.getPicData().getTypeAxis().setSecondAxisLineColor(color);
        if(color == null)
            color = new Color(0,0,0);
        callFunction("UI|T_SECOND_AXIS_LINE_COLOR_D|setForeground",color);
        pic.update();
    }
    /**
     * �α����ɫ��ɫ��
     */
    public void onTSecondAxisLineColorD()
    {
        Color c = pic.getPicData().getTypeAxis().getSecondAxisLineColor();
        if(c == null)
            c = new Color(255,255,255);
        c = DColor.colorDialog(getComponent(),c);
        pic.getPicData().getTypeAxis().setSecondAxisLineColor(c);
        setValue("T_SECOND_AXIS_LINE_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|T_SECOND_AXIS_LINE_COLOR_D|setForeground",c);
        pic.update();
    }
    /**
     * �α������
     */
    public void onTSecondAxisLineType()
    {
        pic.getPicData().getTypeAxis().setSecondAxisLineType(TypeTool.getInt(getValue("T_SECOND_AXIS_LINE_TYPE")));
        pic.update();
    }
    /**
     * �α���߸�
     */
    public void onTSecondAxisLineWidth()
    {
        pic.getPicData().getTypeAxis().setSecondAxisLineWidth(TypeTool.getFloat(getValue("T_SECOND_AXIS_LINE_WIDTH")));
        pic.update();
    }
    /**
     * �α�������߼�
     */
    public void onTSecondAxisLineWidth1()
    {
        pic.getPicData().getTypeAxis().setSecondAxisLineWidth1(TypeTool.getFloat(getValue("T_SECOND_AXIS_LINE_WIDTH1")));
        pic.update();
    }
    /**
     * �ο̶��߿��
     */
    public void onTSecondWidth()
    {
        pic.getPicData().getTypeAxis().setSecondWidth(TypeTool.getInt(getValue("T_SECOND_WIDTH")));
        pic.update();
    }
    /**
     * �ο̶ȷ���1
     */
    public void onTSecondTypeWay1()
    {
        pic.getPicData().getTypeAxis().setSecondTypeWay(1,TypeTool.getBoolean(getValue("T_SECOND_TYPE_WAY_1")));
        pic.update();
    }
    /**
     * �ο̶ȷ���2
     */
    public void onTSecondTypeWay2()
    {
        pic.getPicData().getTypeAxis().setSecondTypeWay(2,TypeTool.getBoolean(getValue("T_SECOND_TYPE_WAY_2")));
        pic.update();
    }
    /**
     * �ο̶�����
     * @param type int
     */
    public void onTSecondType(int type)
    {
        pic.getPicData().getTypeAxis().setSecondType(type);
        pic.update();
    }
    /**
     * �������̶�����ɫ
     */
    public void onTBackMainLineColor()
    {
        Color color = StringTool.getColor(getText("T_BACK_MAIN_LINE_COLOR"),getConfigParm());
        pic.getPicData().getTypeAxis().setBackMainLineColor(color);
        if(color == null)
            color = new Color(0,0,0);
        callFunction("UI|T_BACK_MAIN_LINE_COLOR_D|setForeground",color);
        pic.update();
    }
    /**
     * �������̶�����ɫ��ɫ��
     */
    public void onTBackMainLineColorD()
    {
        Color c = pic.getPicData().getTypeAxis().getBackMainLineColor();
        if(c == null)
            c = new Color(255,255,255);
        c = DColor.colorDialog(getComponent(),c);
        pic.getPicData().getTypeAxis().setBackMainLineColor(c);
        setValue("T_BACK_MAIN_LINE_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|T_BACK_MAIN_LINE_COLOR_D|setForeground",c);
        pic.update();
    }
    /**
     * �������̶�������
     */
    public void onTBackMainLineType()
    {
        pic.getPicData().getTypeAxis().setBackMainLineType(TypeTool.getInt(getValue("T_BACK_MAIN_LINE_TYPE")));
        pic.update();
    }
    /**
     * �������̶��߸߶�
     */
    public void onTBackMainLineWidth()
    {
        pic.getPicData().getTypeAxis().setBackMainLineWidth(TypeTool.getFloat(getValue("T_BACK_MAIN_LINE_WIDTH")));
        pic.update();
    }
    /**
     * �������̶��������߼�
     */
    public void onTBackMainLineWidth1()
    {
        pic.getPicData().getTypeAxis().setBackMainLineWidth1(TypeTool.getFloat(getValue("T_BACK_MAIN_LINE_WIDTH1")));
        pic.update();
    }
    /**
     * �Ƿ���ʾ�������̶���
     */
    public void onTBackMainLineVisible()
    {
        pic.getPicData().getTypeAxis().setBackMainVisible(TypeTool.getBoolean(getValue("T_BACK_MAIN_LINE_VISIBLE")));
        pic.update();
    }
    /**
     * �����ο̶�����ɫ
     */
    public void onTBackSecondLineColor()
    {
        Color color = StringTool.getColor(getText("T_BACK_SECOND_LINE_COLOR"),getConfigParm());
        pic.getPicData().getTypeAxis().setBackSecondLineColor(color);
        if(color == null)
            color = new Color(0,0,0);
        callFunction("UI|T_BACK_SECOND_LINE_COLOR_D|setForeground",color);
        pic.update();
    }
    /**
     * �����ο̶�����ɫ��ɫ��
     */
    public void onTBackSecondLineColorD()
    {
        Color c = pic.getPicData().getTypeAxis().getBackSecondLineColor();
        if(c == null)
            c = new Color(255,255,255);
        c = DColor.colorDialog(getComponent(),c);
        pic.getPicData().getTypeAxis().setBackSecondLineColor(c);
        setValue("T_BACK_SECOND_LINE_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|T_BACK_SECOND_LINE_COLOR_D|setForeground",c);
        pic.update();
    }
    /**
     * �����ο̶�������
     */
    public void onTBackSecondLineType()
    {
        pic.getPicData().getTypeAxis().setBackSecondLineType(TypeTool.getInt(getValue("T_BACK_SECOND_LINE_TYPE")));
        pic.update();
    }
    /**
     * �����ο̶��߸߶�
     */
    public void onTBackSecondLineWidth()
    {
        pic.getPicData().getTypeAxis().setBackSecondLineWidth(TypeTool.getFloat(getValue("T_BACK_SECOND_LINE_WIDTH")));
        pic.update();
    }
    /**
     * �����ο̶��������߼�
     */
    public void onTBackSecondLineWidth1()
    {
        pic.getPicData().getTypeAxis().setBackSecondLineWidth1(TypeTool.getFloat(getValue("T_BACK_SECOND_LINE_WIDTH1")));
        pic.update();
    }
    /**
     * �Ƿ���ʾ�����ο̶���
     */
    public void onTBackSecondLineVisible()
    {
        pic.getPicData().getTypeAxis().setBackSecondVisible(TypeTool.getBoolean(getValue("T_BACK_SECOND_LINE_VISIBLE")));
        pic.update();
    }
    /**
     * �����ο̶�����ɫ
     */
    public void onTTextColor()
    {
        Color color = StringTool.getColor(getText("T_TEXT_COLOR"),getConfigParm());
        pic.getPicData().getTypeAxis().setTextColor(color);
        if(color == null)
            color = new Color(0,0,0);
        callFunction("UI|T_TEXT_COLOR_D|setForeground",color);
        pic.update();
    }
    /**
     * �����ο̶�����ɫ��ɫ��
     */
    public void onTTextColorD()
    {
        Color c = pic.getPicData().getTypeAxis().getTextColor();
        if(c == null)
            c = new Color(255,255,255);
        c = DColor.colorDialog(getComponent(),c);
        pic.getPicData().getTypeAxis().setTextColor(c);
        setValue("T_TEXT_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|T_TEXT_COLOR_D|setForeground",c);
        pic.update();
    }
    /**
     * ����
     */
    public void onTTextFont()
    {
        pic.getPicData().getTypeAxis().setTextFontName(TypeTool.getString(getValue("T_TEXT_FONT")));
        pic.update();
    }
    /**
     * �ֺ�
     */
    public void onTTextSize()
    {
        pic.getPicData().getTypeAxis().setTextFontSize(TypeTool.getInt(getValue("T_TEXT_SIZE")));
        pic.update();
    }
    /**
     * ����
     */
    public void onTDB()
    {
        pic.getPicData().getTypeAxis().setTextFontBold(TypeTool.getBoolean(getValue("T_DB")));
        pic.update();
    }
    /**
     * б��
     */
    public void onTDI()
    {
        pic.getPicData().getTypeAxis().setTextFontItalic(TypeTool.getBoolean(getValue("T_DI")));
        pic.update();
    }
    /**
     * ������ת
     */
    public void onTTextRotate()
    {
        pic.getPicData().getTypeAxis().setTextFontRotate(TypeTool.getDouble(getValue("T_TEXT_ROTATE")));
        pic.update();
    }
    /**
     * ����1
     */
    public void onTTextWay1()
    {
        pic.getPicData().getTypeAxis().setTextWay(1,TypeTool.getBoolean(getValue("T_TEXT_WAY_1")));
        pic.update();
    }
    /**
     * ����2
     */
    public void onTTextWay2()
    {
        pic.getPicData().getTypeAxis().setTextWay(2,TypeTool.getBoolean(getValue("T_TEXT_WAY_2")));
        pic.update();
    }
    /**
     * ����ƫ��
     */
    public void onTTextOffset()
    {
        pic.getPicData().getTypeAxis().setTextOffset(TypeTool.getInt(getValue("T_TEXT_OFFSET")));
        pic.update();
    }
    /**
     * �����Զ�����
     */
    public void onTTextAutoEnter()
    {
        pic.getPicData().getTypeAxis().setTextAutoEnter(TypeTool.getBoolean(getValue("T_TEXT_AUTO_ENTER")));
        pic.update();
    }
    /**
     * �Զ�����ƫ��
     */
    public void onTTextEnterOffset()
    {
        pic.getPicData().getTypeAxis().setTextEnterOffset(TypeTool.getInt(getValue("T_TEXT_ENTER_OFFSET")));
        pic.update();
    }
    /**
     * ���ָ�ʽ
     */
    public void onTTextOffsetFormat()
    {
        pic.getPicData().getTypeAxis().setTextOffsetFormat(TypeTool.getString(getValue("T_TEXT_OFFSET_FORMAT")));
        pic.update();
    }
    /**
     * ���־���
     */
    public void onTTextCenter()
    {
        pic.getPicData().getTypeAxis().setTextCenter(TypeTool.getBoolean(getValue("T_TEXT_CENTER")));
        pic.update();
    }
}
