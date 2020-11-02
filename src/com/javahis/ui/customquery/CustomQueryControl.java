package com.javahis.ui.customquery;
//SELECT_TABLE�������δ��ʾ��column_NAME,ID��

import com.dongyang.control.TControl;
import com.dongyang.util.TypeTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.StringTool;
import jdo.sys.CustomQueryTool;
import java.util.Vector;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;

/**
 *
 * <p>Title: �Զ����ѯ���ڿ�����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.5.18
 * @version 1.0
 */
public class CustomQueryControl extends TControl{
    /**
     * ����
     */
    private String tableName;
    /**
     * ����Ϣ
     */
    private TParm tableParm;
    private TTable columnList;
    private TTable whereList;
    private TTable publicTable;
    private TTable privateTable;
    private TRadioButton pubRadio;
    private TRadioButton prvRadio;
    private TCheckBox defaultCheckBox;
    private TTabbedPane tab;
    private int editWhereCode = -1;
    private TComboBox templateCombo;
    /**
     * ��ʼ������
     */
    public void onInitParameter()
    {
        Object parm = getParameter();
        if(parm == null || !(parm instanceof Object[]))
            return;
        Object pData[] = (Object[])parm;
        tableName = TypeTool.getString(pData[0]);
    }
    /**
     * ��ʼ��
     */
    public void onInit()
    {
    	templateCombo=(TComboBox)getComponent("TEMP_COMBO");
    	
        columnList = (TTable)getComponent("COLUMN_LIST");
        whereList = (TTable)getComponent("WHERE_LIST");
        publicTable = (TTable)getComponent("PUBLIC_TABLE");
        privateTable = (TTable)getComponent("PRIVATE_TABLE");
        pubRadio = (TRadioButton)getComponent("PUB_RADIO");
        prvRadio = (TRadioButton)getComponent("PRV_RADIO");
        defaultCheckBox = (TCheckBox)getComponent("DEFAULT_CHECKBOX");
        tab = (TTabbedPane)getComponent("TAB");
        publicTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                     "onCheck");
        privateTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                     "onCheck");
        if(tableName == null || tableName.length() == 0)
            return;
        //���ñ���
        setTitle(getDBTool().getTableComment(tableName) + "��ѯ���ô���");
        //��ʼ��Where Table
        initWhereTable();
        //�õ������
        initTableParm();
        //��ʼ������Ϣ
        initColumnList(CustomQueryTool.getInstance().getViewTableColumn(tableName));
        //��ʼ������Table
        initPublicTable();
        //��ʼ��˽��Table
        initPrivateTable();
    }
    /**
     * ��ʼ������Table
     */
    public void initPublicTable()
    {
        TParm parm = CustomQueryTool.getInstance().getPubWhereList(tableName);
        System.out.println("pub data="+parm);
        if(parm.getErrCode() < 0)
        {
            System.out.println(parm.getErrText());
            return;
        }
        publicTable.setParmValue(parm);
    }
    /**
     * ��ʼ��˽��Table
     */
    public void initPrivateTable()
    {
        TParm parm = CustomQueryTool.getInstance().getPriWhereList(tableName);
        if(parm.getErrCode() < 0)
        {
            System.out.println(parm.getErrText());
            return;
        }
        privateTable.setParmValue(parm);
    }
    /**
     * �õ������
     */
    public void initTableParm()
    {
        if(tableName == null || tableName.length() == 0)
            return;
        tableParm = new TParm(getDBTool().getColumnsInf(tableName));
        if(tableParm.getErrCode() < 0)
        {
            messageBox_(tableParm.getErrText());
            return;
        }

        TComboBox comboBox = new TComboBox();
        comboBox.setParmMap("ID:COLUMN_NAME;TEXT:COMMENTS");
        comboBox.setParmValue(tableParm);
        comboBox.setShowID(false);
        comboBox.setTableShowList("TEXT");
        whereList.addItem("ColumnCombo",comboBox);

        comboBox = new TComboBox();
        comboBox.setParmMap("ID:ID;TEXT:TEXT");
        comboBox.setParmValue(getCParm());
        comboBox.setShowID(false);
        comboBox.setTableShowList("TEXT");
        whereList.addItem("CCombo",comboBox);

        comboBox = new TComboBox();
        comboBox.setParmMap("ID:ID;TEXT:TEXT");
        comboBox.setParmValue(getLParm());
        comboBox.setShowID(false);
        comboBox.setTableShowList("TEXT");
        whereList.addItem("LCombo",comboBox);
    }
    /**
     * �õ��ж�����
     * @return TParm
     */
    public TParm getCParm()
    {
        TParm parm = new TParm();
        parm.addData("ID","=");
        parm.addData("TEXT","����");
        parm.addData("ID","<>");
        parm.addData("TEXT","������");
        parm.addData("ID",">");
        parm.addData("TEXT","����");
        parm.addData("ID","<");
        parm.addData("TEXT","С��");
        parm.addData("ID",">=");
        parm.addData("TEXT","���ڵ���");
        parm.addData("ID","<=");
        parm.addData("TEXT","С�ڵ���");
        parm.addData("ID","like");
        parm.addData("TEXT","ģ��");
        parm.setCount(parm.getCount("ID"));
        return parm;
    }
    /**
     * �õ���������
     * @return TParm
     */
    public TParm getLParm()
    {
        TParm parm = new TParm();
        parm.addData("ID","AND");
        parm.addData("TEXT","����");
        parm.addData("ID","OR");
        parm.addData("TEXT","����");
        parm.setCount(parm.getCount("ID"));
        return parm;
    }
    /**
     * ��ʼ������Ϣ
     * @param columns String[]
     */
    public void initColumnList(String columns[])
    {
        if(tableParm == null || tableParm.getErrCode() < 0)
            return;
        TParm parm = tableParm;
        if(columns != null && columns.length > 0)
        {
            parm = new TParm();
            Vector columnNames = (Vector)tableParm.getData("COLUMN_NAME");
            for(int i = 0;i < columns.length;i++)
            {
                int index = columnNames.indexOf(columns[i]);
                if(index < 0)
                    continue;
                TParm row = tableParm.getRow(index);
                parm.setRowData(i,row);
            }
            parm.setCount(parm.getCount("COLUMN_NAME"));
        }
        columnList.setParmValue(parm);
        if(columnList.getRowCount() > 0)
            columnList.setSelectedRow(0);
    }
    /**
     * �õ����ݿ����
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool()
    {
        return TJDODBTool.getInstance();
    }
    /**
     * ����
     * N;C;V;L;E
     */
    public void onInsert()
    {
        int row = whereList.getSelectedRow();
        addWhere(row);
    }
    /**
     * ׷��
     */
    public void onNew()
    {
        int row = whereList.getRowCount();
        addWhere(row);
    }
    /**
     * ���Ӳ�ѯ����
     * BRACKET_START;COLUMN_ID;MARK;VALUE;BRACKET_END;LINK;EXTERNAL_FLG
     * @param row int
     */
    public void addWhere(int row)
    {
        String name = "";
        int selectRow = columnList.getSelectedRow();
        if(selectRow >= 0)
            name = columnList.getParmValue().getValue("COLUMN_NAME",selectRow);
        TParm parm = new TParm();
        parm.setData("BRACKET_START","");
        parm.setData("COLUMN_ID",name);
        parm.setData("MARK","=");
        parm.setData("VALUE","");
        parm.setData("BRACKET_END","");
        parm.setData("LINK","AND");
        parm.setData("EXTERNAL_FLG","Y");
        row = whereList.addRow(row,parm);
        whereList.setSelectedRow(row);
        callFunction("UI|DELETE_WHERE_BUTTOM|setEnabled",true);
    }
    /**
     * ��ʼ��Where Table
     * BRACKET_START;COLUMN_ID;MARK;VALUE;BRACKET_END;LINK;EXTERNAL_FLG
     */
    public void initWhereTable()
    {
        TParm parm = new TParm();
        parm.setData("BRACKET_START",new Vector());
        parm.setData("COLUMN_ID",new Vector());
        parm.setData("MARK",new Vector());
        parm.setData("VALUE",new Vector());
        parm.setData("BRACKET_END",new Vector());
        parm.setData("LINK",new Vector());
        parm.setData("EXTERNAL_FLG",new Vector());
        whereList.setParmValue(parm);
    }
    /**
     * ����ѡ��
     */
    public void onSelectColumn()
    {
        String[] result = (String[])openDialog("%ROOT%\\config\\customquery\\SelectColumnDialog.x",new Object[]{tableName});
        if(result == null)
            return;
        //��ʼ������Ϣ
        initColumnList(result);
    }
    /**
     * ɾ��Where
     */
    public void onDeleteWhere()
    {
        if(whereList.getRowCount() == 0)
            return;
        int row = whereList.getSelectedRow();
        if(row < 0)
            return;
        whereList.removeRow(row);
        if(whereList.getRowCount() <= 0)
        {
            callFunction("UI|DELETE_WHERE_BUTTOM|setEnabled",false);
            return;
        }
        whereList.setSelectedRow(row < whereList.getRowCount()?row:whereList.getRowCount() - 1);
    }
    /**
     * ���Ϊ
     * BRACKET_START;COLUMN_ID;MARK;VALUE;BRACKET_END;LINK;EXTERNAL_FLG
     */
    public void onSaveAs()
    {
        whereList.acceptText();
        //�������
        if(!saveCheck())
            return;
        String name = getText("WHERE_NAME");
        boolean pubFlg = pubRadio.isSelected();
        boolean defaultFlg = defaultCheckBox.isSelected();
        TParm parm = new TParm();
        TParm whereParm = whereList.getParmValue();
        if(whereParm != null)
        {
            for (int i = 0; i < whereParm.getCount(); i++) {
                parm.addData("BRACKET_START", whereParm.getValue("BRACKET_START", i));
                parm.addData("COLUMN_ID", whereParm.getValue("COLUMN_ID", i));
                parm.addData("MARK", whereParm.getValue("MARK", i));
                parm.addData("VALUE", whereParm.getValue("VALUE", i));
                parm.addData("BRACKET_END", whereParm.getValue("BRACKET_END", i));
                parm.addData("LINK", whereParm.getValue("LINK", i));
                parm.addData("EXTERNAL_FLG", whereParm.getBoolean("EXTERNAL_FLG", i)?"Y":"N");
            }
            parm.setCount(whereParm.getCount());
        }
        TParm result = CustomQueryTool.getInstance().insertViewWhere(name,tableName,pubFlg,defaultFlg,parm);
        if(result.getErrCode() < 0)
        {
            messageBox_(result.getErrText());
            return;
        }
        //��ʼ������Table
        initPublicTable();
        //��ʼ��˽��Table
        initPrivateTable();
        editWhereCode = result.getInt("WHERE_CODE");
        setText("SELECT_ID","���ڱ༭ID:" + editWhereCode);
        messageBox_("����ɹ�!");
        callFunction("UI|SAVE_BUTTOM|setEnabled",true);
    }
    /**
     * ����
     */
    public void onSave()
    {
        whereList.acceptText();
        if(editWhereCode <= 0)
            return;
        //�������
        if(!saveCheck())
            return;
        String name = getText("WHERE_NAME");
        boolean pubFlg = pubRadio.isSelected();
        boolean defaultFlg = defaultCheckBox.isSelected();
        TParm parm = new TParm();
        TParm whereParm = whereList.getParmValue();
        if(whereParm != null)
        {
            for (int i = 0; i < whereParm.getCount(); i++) {
                parm.addData("BRACKET_START", whereParm.getValue("BRACKET_START", i));
                parm.addData("COLUMN_ID", whereParm.getValue("COLUMN_ID", i));
                parm.addData("MARK", whereParm.getValue("MARK", i));
                parm.addData("VALUE", whereParm.getValue("VALUE", i));
                parm.addData("BRACKET_END", whereParm.getValue("BRACKET_END", i));
                parm.addData("LINK", whereParm.getValue("LINK", i));
                parm.addData("EXTERNAL_FLG", whereParm.getBoolean("EXTERNAL_FLG", i)?"Y":"N");
            }
            parm.setCount(whereParm.getCount());
        }
        TParm result = CustomQueryTool.getInstance().updateViewWhere(editWhereCode,name,tableName,pubFlg,defaultFlg,parm);
        if(result.getErrCode() < 0)
        {
            messageBox_(result.getErrText());
            return;
        }
        //��ʼ������Table
        initPublicTable();
        //��ʼ��˽��Table
        initPrivateTable();
        messageBox_("����ɹ�!");
    }
    /**
     * �������
     * @return boolean
     */
    public boolean saveCheck()
    {
        String name = getText("WHERE_NAME");
        if(name.length() == 0)
        {
            messageBox_("����������!");
            grabFocus("WHERE_NAME");
            return false;
        }
        return true;
    }
    /**
     * ����table���
     */
    public void onPublicClick()
    {
        privateTable.clearSelection();
    }
    /**
     * ˽��table���
     */
    public void onPrivateClick()
    {
        publicTable.clearSelection();
    }
    /**
     * ɾ��
     */
    public void onDelete()
    {
        boolean isPublic = false;
        int whereCode = 0;
        int row = publicTable.getSelectedRow();
        if(row >= 0)
        {
            TParm parm = publicTable.getParmValue();
            String name = parm.getValue("WHERE_DESC",row);
            if(this.messageBox("��ʾ��Ϣ","ȷ��ɾ�����в�ѯ'" + name + "'",YES_NO_OPTION) != 0)
                return;
            whereCode = parm.getInt("WHERE_CODE",row);
            isPublic = true;
        }else
        {
            row = privateTable.getSelectedRow();
            if(row < 0)
                return;
            TParm parm = privateTable.getParmValue();
            String name = parm.getValue("WHERE_DESC",row);
            if(this.messageBox("��ʾ��Ϣ","ȷ��ɾ��˽�в�ѯ'" + name + "'",YES_NO_OPTION) != 0)
                return;
            whereCode = parm.getInt("WHERE_CODE",row);
        }
        TParm parm = CustomQueryTool.getInstance().deleteWhere(whereCode);
        if(parm.getErrCode() < 0)
        {
            messageBox(parm.getErrText());
            return;
        }
        if(isPublic)
            publicTable.removeRow(row);
        else
            privateTable.removeRow(row);
    }
    /**
     * �༭
     */
    public void onEdit()
    {
        boolean isPublic = false;
        int whereCode = 0;
        String name = "";
        int row = publicTable.getSelectedRow();
        if(row >= 0)
        {
            TParm parm = publicTable.getParmValue();
            name = parm.getValue("WHERE_DESC",row);
            whereCode = parm.getInt("WHERE_CODE",row);
            isPublic = true;
        }else
        {
            row = privateTable.getSelectedRow();
            if(row < 0)
                return;
            TParm parm = privateTable.getParmValue();
            name = parm.getValue("WHERE_DESC",row);
            whereCode = parm.getInt("WHERE_CODE",row);
        }
        tab.setSelectedIndex(1);
        if(isPublic)
            pubRadio.setSelected(true);
        else
            prvRadio.setSelected(true);
        setText("WHERE_NAME",name);
        TParm parm = CustomQueryTool.getInstance().queryWhere(whereCode);
        if(parm.getErrCode() < 0)
        {
            messageBox_(parm.getErrText());
            return;
        }
        whereList.setParmValue(parm);
        editWhereCode = whereCode;
        setText("SELECT_ID","���ڱ༭ID:" + whereCode);
        callFunction("UI|SAVE_BUTTOM|setEnabled",true);
    }
    /**
     * ����
     */
    public void onUp()
    {
        boolean isPublic = false;
        TParm parm;
        int row = publicTable.getSelectedRow();
        if(row > 0)
        {
            parm = publicTable.getParmValue();
            isPublic = true;
        }else
        {
            row = privateTable.getSelectedRow();
            if(row <= 0)
                return;
            parm = privateTable.getParmValue();
        }
        int whereCode = parm.getInt("WHERE_CODE",row);
        int whereCode1 = parm.getInt("WHERE_CODE",row - 1);
        parm = CustomQueryTool.getInstance().updateSeq(whereCode,whereCode1);
        if(parm.getErrCode() < 0)
        {
            messageBox_(parm.getErrText());
            return;
        }
        //��ʼ������Table
        initPublicTable();
        //��ʼ��˽��Table
        initPrivateTable();
        if(isPublic)
            publicTable.setSelectedRow(row - 1);
        else
            privateTable.setSelectedRow(row - 1);
    }
    /**
     * ����
     */
    public void onDown()
    {
        boolean isPublic = false;
        TParm parm;
        int row = publicTable.getSelectedRow();
        if(row >= 0)
        {
            if(row >= publicTable.getRowCount() - 1)
                return;
            parm = publicTable.getParmValue();
            isPublic = true;
        }else
        {
            row = privateTable.getSelectedRow();
            if(row < 0)
                return;
            if(row >= privateTable.getRowCount() - 1)
                return;
            parm = privateTable.getParmValue();
        }
        int whereCode = parm.getInt("WHERE_CODE",row);
        int whereCode1 = parm.getInt("WHERE_CODE",row + 1);
        parm = CustomQueryTool.getInstance().updateSeq(whereCode,whereCode1);
        if(parm.getErrCode() < 0)
        {
            messageBox_(parm.getErrText());
            return;
        }
        //��ʼ������Table
        initPublicTable();
        //��ʼ��˽��Table
        initPrivateTable();
        if(isPublic)
            publicTable.setSelectedRow(row + 1);
        else
            privateTable.setSelectedRow(row + 1);
    }
    /**
     * ����Table���Ĭ��
     * @param obj Object
     */
    public void onCheck(Object obj)
    {
        TTable table=(TTable)obj;
        table.acceptText();
        int row = table.getSelectedRow();
        int whereCode = table.getParmValue().getInt("WHERE_CODE",row);
        TParm parm = CustomQueryTool.getInstance().getUpateDefaultSQL(tableName,whereCode);
        if(parm.getErrCode() < 0)
        {
            messageBox_(parm.getErrText());
            return;
        }
        cancelDefCheck();
        table.setItem(row,"DEF","Y");
    }
    public void cancelDefCheck()
    {
        Vector v = (Vector)publicTable.getParmValue().getData("DEF");
        for(int i = 0;i < v.size();i++)
        {
            if("Y".equals(v.get(i)))
                publicTable.setValueAt("N",i,0);
            v.set(i, "N");
        }
        v = (Vector)privateTable.getParmValue().getData("DEF");
        for(int i = 0;i < v.size();i++)
        {
            if("Y".equals(v.get(i)))
                privateTable.setValueAt("N",i,0);
            v.set(i, "N");
        }
    }
    public static void main(String args[])
    {
        Object parm = com.javahis.util.JavaHisDebug.runDialog("customquery\\customQuery.x",new Object[]{"UDD_SHEET"});
        System.out.println("parm=" + parm);
    }
}
