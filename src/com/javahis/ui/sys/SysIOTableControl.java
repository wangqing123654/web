package com.javahis.ui.sys;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

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
public class SysIOTableControl extends TControl {
    /**
     * TABLE
     */
    private static String TABLE="TABLE1";
    /**
     * ���̴���
     */
    private String supCode;
    /**
     * ��ʼ��
     */
    public void onInit(){
        Object obj = this.getParameter();
        if(obj!=null){
            TParm parm = (TParm)obj;
            supCode = parm.getValue("IO_CODE");
            onQuery();
        }
    }
    /**
     * ��ѯ
     */
    public void onQuery(){
        String sql = "SELECT * FROM SYS_IO_TABLE WHERE IO_CODE='"+supCode+"'";
        TDataStore dateStore = new TDataStore();
        dateStore.setSQL(sql);
        dateStore.retrieve();
        this.getTTable(TABLE).setDataStore(dateStore);
        this.getTTable(TABLE).setDSValue();
        TTable table = this.getTTable(TABLE);
        int rowCount = table.getDataStore().rowCount();
        for(int i=0;i<rowCount;i++){
            table.setLockCell(i, 1, true);
        }
    }

    /**
     * ����
     */
    public void onNewData() {
        int row = this.getTTable(TABLE).addRow();
        this.getTTable(TABLE).getDataStore().setItem(row,"IO_CODE",this.supCode);
        this.getTTable(TABLE).getDataStore().setItem(row,"READ","N");
        this.getTTable(TABLE).getDataStore().setItem(row,"WRITE","N");
        this.getTTable(TABLE).getDataStore().setItem(row,"LISTEN","N");
        this.getTTable(TABLE).getDataStore().setItem(row,"OPT_USER",Operator.getID());
        this.getTTable(TABLE).getDataStore().setItem(row,"OPT_DATE",SystemTool.getInstance().getDate());
        this.getTTable(TABLE).getDataStore().setItem(row,"OPT_TERM",Operator.getIP());
        this.getTTable(TABLE).setDSValue();
    }
    /**
     * ɾ��
     */
    public void onDelete(){
        int selRow = this.getTTable(TABLE).getSelectedRow();
        this.getTTable(TABLE).getDataStore().deleteRow(selRow);
        this.getTTable(TABLE).setDSValue();
    }
    /**
     * ���
     */
    public void onClear(){
        getTTable(TABLE).removeRowAll();
    }

    /**
     * ����
     */
    public void onSave(){
        if(!this.getTTable(TABLE).update())
            this.messageBox("����ʧ�ܣ�");
        else
            this.messageBox("����ɹ���");
    }
    /**
      * TABLE
      * @param tag String
      * @return TTable
      */
     public TTable getTTable(String tag){
         return (TTable)this.getComponent(tag);
     }


}
