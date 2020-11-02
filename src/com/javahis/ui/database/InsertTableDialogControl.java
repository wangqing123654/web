package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TCheckBox;
import com.dongyang.data.TParm;

/**
 *
 * <p>Title: ������Ի��������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.3.30
 * @version 1.0
 */
public class InsertTableDialogControl extends TControl{
    private TTextFormat columncount;
    private TTextFormat rowcount;
    private TTextArea sql;
    private TCheckBox retrieveCheckBox;
    private TCheckBox maxCheckBox;
    private TCheckBox inputData;
    private TTextFormat insertDataRow;
    /**
     * ����
     */
    private TParm group;
    /**
     * ������ʾ��������
     */
    private boolean showData;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        columncount = (TTextFormat)getComponent("COLUMN_COUNT");
        rowcount = (TTextFormat)getComponent("ROW_COUNT");
        sql = (TTextArea)getComponent("TextArea_SQL");
        retrieveCheckBox = (TCheckBox)getComponent("RetrieveCheckBox");
        //�ϼ���
        maxCheckBox = (TCheckBox)getComponent("MAX_CHECKBOX");
        //���������к�
        insertDataRow = (TTextFormat)getComponent("InsertDataRow");
        //��TParm����
        inputData = (TCheckBox)getComponent("INPUT_DATA");
    }
    /**
     * ȷ��
     */
    public void onOK()
    {
        int column = TypeTool.getInt(columncount.getValue());
        int row = TypeTool.getInt(rowcount.getValue());
        setReturnValue(new Object[]{column,row,sql.getValue(),
                       retrieveCheckBox.isSelected(),
                       maxCheckBox.isSelected(),
                       TypeTool.getInt(insertDataRow.getValue()),
                       group,showData,inputData.isSelected(),getText("TABLE_ID")});
        closeWindow();
    }
    /**
     * ��ѯ���ݿ�
     */
    public void onSelectDB()
    {
        Object reset[] = (Object[])openDialog("%ROOT%\\config\\database\\SQLEdit.x");
        if(reset != null)
        {
            sql.setValue((String) reset[0]);
            columncount.setValue((Integer)reset[1]);
        }
    }
    /**
     * ����ѡ��
     */
    public void onGroup()
    {
        Object obj[] = (Object[])openDialog("%ROOT%\\config\\database\\GroupDialog.x");
        group = (TParm)obj[0];
        showData = (Boolean)obj[1];
    }
    /**
     * ȡ��
     */
    public void onCancel()
    {
        closeWindow();
    }
}
