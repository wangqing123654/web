package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title: ������Ի���</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.10.23
 * @version 1.0
 */
public class InsertTableBaseControl extends TControl{
    /**
     * ȷ��
     */
    public void onOK()
    {
        int column = TypeTool.getInt(getValue("ROW_COUNT"));
        int row = TypeTool.getInt(getValue("COLUMN_COUNT"));
        String name = TypeTool.getString(getValue("NAME"));
        setReturnValue(new Object[]{name,column,row});
        closeWindow();
    }

}
