package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import java.util.Vector;
import com.dongyang.ui.TTabbedPane;
import java.awt.Component;
import com.dongyang.util.RunClass;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TTextArea;

/**
 *
 * <p>Title: SQL�༭���ڿ�����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.3.25
 * @version 1.0
 */
public class SQLEditControl extends TControl{
    int x = 10;
    int y = 10;
    /**
     * ���
     */
    TPanel panel;
    TTable tableInf;
    TTable columnInf;
    TTable whereInf;
    TTabbedPane tab;
    TTextArea sqlText;

    /**
     * ���ӵĵ�һ����
     */
    LinkData firstLink;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        panel = (TPanel)getComponent("Panel");
        tableInf = (TTable)getComponent("Table_TableInf");
        columnInf = (TTable)getComponent("Table_ColumnInf");
        whereInf = (TTable)getComponent("Table_WhereInf");
        tab = (TTabbedPane)getComponent("TAB");
        sqlText = (TTextArea)getComponent("TextArea_SQL");
    }
    /**
     * ѡ�����ݱ�
     */
    public void onSelectTable()
    {
        openDialog("%ROOT%\\config\\database\\DBTableDialog.x",this);
    }
    /**
     * ��Table
     * @param tableName String
     * @param tableCn String
     */
    public void openTable(String tableName,String tableCn)
    {
        panel.addItem(tableName,"%ROOT%\\config\\database\\TableView.x",new Object[]{tableName,tableCn,this},false);
        TPanel table = (TPanel)getComponent(tableName);
        table.setLocation(x,y);
        x += 200;
        if(x > 1000)
        {
            x = 10;
            y += 10;
        }
    }
    /**
     * ѡ��Table
     * @param tablename String
     * @param tableCn String
     */
    public void onAddTable(String tablename,String tableCn)
    {
        openTable(tablename,tableCn);
        TParm parm = tableInf.getParmValue();
        if(parm == null)
        {
            parm = new TParm();
            tableInf.setParmValue(parm);
            parm.setCount(0);
        }
        parm.addData("TABLE_NAME",tablename);
        parm.addData("COMMENTS",tableCn);
        parm.setCount(parm.getCount() + 1);
        tableInf.setParmValue(parm);
    }
    /**
     * �ر���ͼ
     * @param tableName String
     */
    public void onRemoveTable(String tableName)
    {
        panel.removeItem(tableName);
        TParm parm = tableInf.getParmValue();
        if(parm == null)
            return;
        Vector vector =(Vector)parm.getData("TABLE_NAME");
        if(vector == null)
            return;
        int index = vector.indexOf(tableName);
        if(index == -1)
            return;
        parm.removeRow(index);
        tableInf.setParmValue(parm);
        onRemoveColumn(tableName);
    }
    /**
     * ������
     * @param tableName String
     * @param columnName String
     * @param columnCn String
     */
    public void onAddColumn(String tableName,String columnName,String columnCn)
    {
        TParm parm = columnInf.getParmValue();
        if(parm == null)
        {
            parm = new TParm();
            columnInf.setParmValue(parm);
            parm.setCount(0);
        }
        parm.addData("TABLE_NAME",tableName);
        parm.addData("COLUMN_NAME",columnName);
        parm.addData("COMMENTS",columnCn);
        parm.addData("SYNTAX",tableName + "." + columnName);
        parm.setCount(parm.getCount() + 1);
        columnInf.setParmValue(parm);
    }
    /**
     * ɾ����
     * @param tableName String
     */
    public void onRemoveColumn(String tableName)
    {
        onRemoveColumn(tableName,"");
    }
    /**
     * ɾ����
     * @param tableName String
     * @param columnName String
     */
    public void onRemoveColumn(String tableName,String columnName)
    {
        TParm parm = columnInf.getParmValue();
        if(parm == null)
            return;
        for(int i = parm.getCount() - 1;i >= 0;i--)
        {
            String t = parm.getValue("TABLE_NAME",i);
            if(!t.equals(tableName))
                continue;
            if(columnName != null && columnName.length() > 0)
            {
                String c = parm.getValue("COLUMN_NAME", i);
                if (!c.equals(columnName))
                    continue;
            }
            parm.removeRow(i);
            columnInf.setParmValue(parm);
        }
    }
    /**
     * ������ʾ����
     * @param type int
     */
    public void setTableViewType(int type)
    {
        for(int i = 0;i < panel.getComponentCount();i++)
        {
            Component component = panel.getComponent(i);
            if(component == null || !(component instanceof TPanel))
                continue;
            TPanel p = (TPanel)component;
            TControl control = p.getControl();
            if(control == null)
                continue;
            RunClass.runMethod(control,"viewType",
                               new Object[]{type});

        }
    }
    /**
     * ��ǩ��ҳ
     */
    public void onTabChanged()
    {
        int index = tab.getSelectedIndex();
        //������ʾ����
        setTableViewType(index);
        if(index == 3)
        {
            sqlText.setText(getSQL());
        }
    }
    /**
     * �õ�SQL���
     * @return String
     */
    public String getSQL()
    {
        String columnSql = getColumnSQL();
        if(columnSql.length() == 0)
            return "";
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT ");
        sb.append(columnSql);
        sb.append("\n  FROM ");
        sb.append(getTableSQL());
        String where = getWhereSQL();
        if(where.length() > 0)
        {
            sb.append("\n WHERE ");
            sb.append(where);
        }
        return sb.toString();
    }
    /**
     * �õ���SQL
     * @return String
     */
    public String getColumnSQL()
    {
        StringBuffer sb = new StringBuffer();
        int count = columnInf.getRowCount();
        for(int row = 0;row < count;row++)
        {
            if(sb.length() > 0)
                sb.append(",  ");
            if(row % 5 == 0)
                sb.append("\n      ");
            sb.append(columnInf.getItemString(row,"SYNTAX"));
        }
        return sb.toString();
    }
    /**
     * �õ���SQL
     * @return String
     */
    public String getTableSQL()
    {
        StringBuffer sb = new StringBuffer();
        int count = tableInf.getRowCount();
        for(int row = 0;row < count;row++)
        {
            if(sb.length() > 0)
                sb.append(",  ");
            if(row % 5 == 0)
                sb.append("\n      ");
            sb.append(tableInf.getItemString(row,"TABLE_NAME"));
        }
        return sb.toString();
    }
    /**
     * �õ�����SQL
     * @return String
     */
    public String getWhereSQL()
    {
        StringBuffer sb = new StringBuffer();
        int count = whereInf.getRowCount();
        for(int row = 0;row < count;row++)
        {
            sb.append("\n      ");
            sb.append(whereInf.getItemString(row,"COLUMN1"));
            sb.append(" ");
            sb.append(whereInf.getItemString(row,"S"));
            sb.append(" ");
            sb.append(whereInf.getItemString(row,"COLUMN2"));
            sb.append(" ");
            sb.append(whereInf.getItemString(row,"LINK"));
        }
        return sb.toString();
    }
    /**
     * ����
     * @param tableName String
     * @param columnName String
     * @param control Object
     */
    public void onSelectLink(String tableName,String columnName,Object control)
    {
        LinkData linkData = new LinkData(tableName,columnName,control);
        //��һ������
        if(firstLink == null)
        {
            firstLink = linkData;
            return;
        }
        TParm parm = whereInf.getParmValue();
        if(parm == null)
        {
            parm = new TParm();
            whereInf.setParmValue(parm);
            parm.setCount(0);
        }
        if(parm.getCount() > 0)
        {
            String link = parm.getValue("LINK", parm.getCount() - 1);
            if (link == null || link.length() == 0)
                parm.setData("LINK", parm.getCount() - 1, "AND");
        }
        //����ͬһ��λ��
        if(firstLink.equals(linkData))
        {
            parm.addData("COLUMN1",firstLink.tableName + "." + firstLink.columnName);
            parm.addData("S","=");
            parm.addData("COLUMN2","''");
            parm.addData("LINK","");
            parm.setCount(parm.getCount() + 1);
            whereInf.setParmValue(parm);
            //�������
            RunClass.runMethod(firstLink.control,"clearLink",new Object[]{});
            firstLink = null;
            return;
        }
        parm.addData("COLUMN1",firstLink.tableName + "." + firstLink.columnName);
        parm.addData("S","=");
        parm.addData("COLUMN2",linkData.tableName + "." + linkData.columnName);
        parm.addData("LINK","");
        parm.setCount(parm.getCount() + 1);
        whereInf.setParmValue(parm);
        //�������
        RunClass.runMethod(linkData.control,"clearLink",new Object[]{});
        RunClass.runMethod(firstLink.control,"clearLink",new Object[]{});
        firstLink = null;
    }
    class LinkData
    {
        /**
         * ����
         */
        String tableName;
        /**
         * ����
         */
        String columnName;
        /**
         * ������
         */
        Object control;
        /**
         * ������
         * @param tableName String
         * @param columnName String
         * @param control Object
         */
        public LinkData(String tableName,String columnName,Object control)
        {
            this.tableName = tableName;
            this.columnName = columnName;
            this.control = control;
        }
        /**
         * ��ͬ�Ƚ�
         * @param obj Object
         * @return boolean
         */
        public boolean equals(Object obj)
        {
            if(obj == null || !(obj instanceof LinkData))
                return false;
            LinkData d = (LinkData)obj;
            if(!tableName.equals(d.tableName) ||
               !columnName.equals(d.columnName) ||
               control != d.control)
                return false;
            return true;
        }
    }
    /**
     * �����˵�
     */
    public void onColumnPopupMenu()
    {
        columnInf.setPopupMenuSyntax("����,U,onUpColumn;" +
                                     "����,P,onDownColumn;|;" +
                                     "�����Զ�����,C,onAddUserColumn;|;" +
                                     "ɾ��,D,onDeleteColumn");
    }
    /**
     * ɾ����
     */
    public void onDeleteColumn()
    {
        int row = columnInf.getSelectedRow();
        if(row < 0)
            return;
        String tableName = (String)columnInf.getValueAt(row,0);
        String columnName = (String)columnInf.getValueAt(row,1);
        columnInf.removeRow(row);
        if(tableName == null || tableName.length() == 0)
            return;

        TComponent component = panel.findItem(tableName);
        if(component == null)
            return;
        component.callFunction("clearSelected",columnName);
    }
    /**
     * �����Զ�����
     */
    public void onAddUserColumn()
    {
        TParm parm = new TParm();
        parm.setData("TABLE_NAME","");
        parm.setData("COLUMN_NAME","�Զ���");
        parm.setData("COMMENTS","");
        parm.setData("SYNTAX","");
        columnInf.addRow(parm);
    }
    /**
     * ����
     */
    public void onUpColumn()
    {
        int row = columnInf.getSelectedRow();
        if(row <= 0)
            return;
        columnInf.sRowItem(row - 1,row);
        columnInf.setSelectedRow(row - 1);
    }
    /**
     * ����
     */
    public void onDownColumn()
    {
        int row = columnInf.getSelectedRow();
        if(row > columnInf.getRowCount() - 2)
            return;
        columnInf.sRowItem(row,row + 1);
        columnInf.setSelectedRow(row + 1);
    }
    /**
     * �ر�
     * @return boolean
     */
    public boolean onClosing()
    {
        String sql = getSQL();
        if(sql.length() > 0)
        {
            int count = columnInf.getRowCount();
            setReturnValue(new Object[]{sql,count});
        }
        return true;
    }
    public static void main(String args[])
    {
        com.javahis.util.JavaHisDebug.runDialog("database\\SQLEdit.x");
    }
}
