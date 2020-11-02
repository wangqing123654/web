package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TMovePane;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.RunClass;
import java.awt.Color;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * <p>Title: ��ʾͼ������</p>
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
public class TableViewControl extends TControl{
    /**
     * Table�ؼ�
     */
    TTable table;
    /**
     * �ƶ���
     */
    TMovePane movePane;
    /**
     * ����
     */
    Object parent;
    /**
     * ����
     */
    String tableName;
    /**
     * ������
     */
    String tableCn;
    /**
     * ���ݿ⹤��
     */
    TJDODBTool dbTool = TJDODBTool.getInstance();
    /**
     * ��ѡ����ɫ
     */
    Color selectRowColor = new Color(49,106,197);
    /**
     * ��ѡ��������ɫ
     */
    Color selectRowTextColor = new Color(255,255,255);
    /**
     * ������ɫ
     */
    Map columnRowColorMap = new HashMap();
    /**
     * ������ɫ
     */
    Map columnRowTextColorMap = new HashMap();
    /**
     * ��������ɫ
     */
    Map linkRowColorMap = new HashMap();
    /**
     * ��������ɫ
     */
    Map linkRowTextColorMap = new HashMap();
    /**
     * ������ʾ����
     */
    int viewType;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        //�õ��ƶ���
        movePane = (TMovePane)getComponent("MovePane");
        //�õ�Table
        table = (TTable)getComponent("Table");
        Object[] parm = (Object[])getParameter();
        if(parm != null)
        {
            tableName = (String)parm[0];
            tableCn = (String)parm[1];
            parent = parm[2];
            movePane.setText(tableName);
        }
        //��ʼ��Table
        initTable();
    }
    /**
     * ��ʼ��Table
     */
    public void initTable()
    {
        if(tableName == null || tableName.length() == 0)
            return;
        TParm parm = new TParm(dbTool.getColumnsAndComments(tableName));
        if(parm == null || parm.getErrCode() < 0)
        {
            messageBox_(parm.getErrText());
            return;
        }
        table.setParmValue(parm);
        if(parm.getCount() > 0)
            table.setSelectedRow(0);
    }
    /**
     * �ر���ͼ
     */
    public void onTableClose()
    {
        //���е��ÿ�����
        RunClass.runMethod(parent,"onRemoveTable",new Object[]{tableName});
    }
    /**
     * �����
     */
    public void onClickedRow()
    {
        int row = table.getSelectedRow();
        if(row == -1)
            return;
        switch(viewType)
        {
            case 0:
                selectColumn(row);
                break;
            case 2:
                selectLink(row);
                break;
        }
    }
    /**
     * ѡ������
     * @param row int
     */
    public void selectLink(int row)
    {
        table.setRowColor(row, selectRowColor);
        table.setRowTextColor(row, selectRowTextColor);
        table.getTable().repaint();
        RunClass.runMethod(parent,"onSelectLink",
                           new Object[]{tableName,
                           table.getValueAt(row,0),this});
    }
    /**
     * �������
     */
    public void clearLink()
    {
        table.setRowColorMap(new HashMap());
        table.setRowTextColorMap(new HashMap());
        table.getTable().repaint();
    }
    /**
     * ���ѡ����
     * @param columnName String
     */
    public void clearSelected(String columnName)
    {
        int row = getFindRow(columnName);
        if(row == -1)
            return;
        table.setRowColor(row,null);
        table.setRowTextColor(row,null);
        table.getTable().repaint();
    }
    /**
     * �����к�
     * @param columnName String
     * @return int
     */
    public int getFindRow(String columnName)
    {
        TParm parm = table.getParmValue();
        if(parm == null)
            return -1;
        Vector v = (Vector)parm.getData("COLUMN_NAME");
        if(v == null)
            return -1;
        return v.indexOf(columnName);
    }
    /**
     * ѡ����
     * @param row int
     */
    public void selectColumn(int row)
    {
        Color color = table.getRowColor(row);
        if(color == null)
        {
            table.setRowColor(row, selectRowColor);
            table.setRowTextColor(row, selectRowTextColor);
            //���е��ÿ�����
            RunClass.runMethod(parent,"onAddColumn",
                               new Object[]{tableName,
                               table.getValueAt(row,0),
                               table.getValueAt(row,1)});
        }
        else
        {
            table.removeRowColor(row);
            table.removeRowTextColor(row);
            RunClass.runMethod(parent,"onRemoveColumn",
                               new Object[]{tableName,
                               table.getValueAt(row,0)});
        }
        table.getTable().repaint();
    }
    /**
     * ������ʾ����
     * @param viewType int
     * 0 ��
     * 1 ��
     * 2 ����
     */
    public void viewType(int viewType)
    {
        if(this.viewType == viewType)
            return;
        switch(this.viewType)
        {
            case 0:
                columnRowColorMap = table.getRowColorMap();
                columnRowTextColorMap = table.getRowTextColorMap();
                break;
            case 2:
                linkRowColorMap = table.getRowColorMap();
                linkRowTextColorMap = table.getRowTextColorMap();
                break;
        }
        switch(viewType)
        {
            case 0:
                table.setRowColorMap(columnRowColorMap);
                table.setRowTextColorMap(columnRowTextColorMap);
                break;
            case 1:
                table.setRowColorMap(new HashMap());
                table.setRowTextColorMap(new HashMap());
            case 2:
                table.setRowColorMap(linkRowColorMap);
                table.setRowTextColorMap(linkRowTextColorMap);
                break;
        }
        this.viewType = viewType;
        table.getTable().repaint();
    }
}
