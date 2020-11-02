package com.javahis.ui.customquery;

import com.dongyang.control.TControl;
import com.dongyang.util.TypeTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import jdo.sys.CustomQueryTool;
import com.dongyang.ui.TComboBox;
import java.util.Vector;

/**
 *
 * <p>Title: ��������Ի��������</p>
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
public class ParmInputControl extends TControl{
    /**
     * ����
     */
    private String tableName;
    private TParm whereParm;
    private TParm tableParm;
    private TTable table;
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
        whereParm = (TParm)pData[1];
        tableParm = (TParm)pData[2];
    }
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        table = (TTable)getComponent("TABLE");
        //���ñ���
        setTitle(getDBTool().getTableComment(tableName) + "��ѯ����");
        //��ʼ��Table
        initTable();
    }
    /**
     * ��ʼ��Table
     */
    public void initTable()
    {
        TComboBox comboBox = new TComboBox();
        comboBox.setParmMap("ID:COLUMN_NAME;TEXT:COMMENTS");
        comboBox.setParmValue(tableParm);
        comboBox.setShowID(false);
        comboBox.setTableShowList("TEXT");
        table.addItem("ColumnCombo",comboBox);

        Vector v = (Vector)whereParm.getData("EXTERNAL_FLG");
        TParm parm = new TParm();
        for(int i = 0;i < v.size();i++)
        {
            if(!"Y".equals(v.get(i)))
                continue;
            parm.addData("COLUMN_ID",whereParm.getValue("COLUMN_ID",i));
            parm.addData("VALUE",whereParm.getValue("VALUE",i));
            parm.addData("ID",i);
        }
        parm.setCount(parm.getCount("COLUMN_ID"));
        table.setParmValue(parm);
    }
    /**
     * �õ����ݿ⹤��
     * @return CustomQueryTool
     */
    public CustomQueryTool getDBTool()
    {
        return CustomQueryTool.getInstance();
    }
    /**
     * ȷ��
     */
    public void onOK()
    {
        table.acceptText();
        TParm parm = table.getParmValue();
        for(int i = 0;i < parm.getCount();i++)
            whereParm.setData("VALUE",parm.getInt("ID",i),parm.getValue("VALUE",i));
        setReturnValue("OK");
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
