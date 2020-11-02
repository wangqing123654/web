package com.javahis.ui.hrm;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
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
public class HRMQueryMrNoControl extends TControl {
    private String idNo;
    public void onInit(){
        Object obj = this.getParameter();
        if(obj!=null){
            idNo = obj.toString();
        }
        //TABLE˫���¼�
callFunction("UI|" + "TABLE" + "|addEventListener",
             "TABLE" + "->" + TTableEvent.DOUBLE_CLICKED, this, "onTableDoubled");

        onQuery();
    }
    /**
     * ��ѯ
     */
    public void onQuery(){
        String sql = "SELECT MR_NO,PAT_NAME,SEX_CODE,PAT_NAME FROM SYS_PATINFO WHERE IDNO='"+idNo+"'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        getTTable("TABLE").setParmValue(parm);
    }
    /**
     * �õ�TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
    /**
     * ˫���¼�
     * @param row int
     */
    public void onTableDoubled(int row){
        this.setReturnValue(getTTable("TABLE").getParmValue().getRow(row));
        this.closeWindow();
    }

}
