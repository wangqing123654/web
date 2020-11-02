package com.javahis.ui.hrm;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;

/**
 * <p>Title: HISҽ��ϵͳ</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author WangM
 * @version 1.0
 */
public class HRMDEPTAttributeControl extends TControl {
    public void onInit() {
        super.onInit();
    }
    public void onSave(){
        this.getTTable("TABLE1").acceptText();
        int rowCount = this.getTTable("TABLE1").getDataStore().rowCount();
        boolean falg = true;
        for(int i=0;i<rowCount;i++){
            if(this.getTTable("TABLE1").getDataStore().getItemString(i,"DEPT_ATTRIBUTE").length()==0){
                falg = false;
                break;
            }
        }
        if(falg){
            for (int i = 0; i < rowCount; i++) {
                this.getTTable("TABLE1").getDataStore().setActive(i,true);
            }
        }else{
            this.messageBox("�Ʊ����Բ���Ϊ�գ�");
            return;
        }
        if(this.getTTable("TABLE1").getDataStore().update()){
            this.messageBox("����ɹ���");
        }else{
            this.messageBox("����ʧ�ܣ�");
        }
    }
    public void onDelete(){
        this.getTTable("TABLE1").acceptText();
        this.getTTable("TABLE1").getDataStore().deleteRow(this.getTTable("TABLE1").getSelectedRow());
        this.getTTable("TABLE1").setDSValue();
    }
    public void onNew(){
        this.getTTable("TABLE1").acceptText();
        int row = this.getTTable("TABLE1").getDataStore().insertRow();
        this.getTTable("TABLE1").getDataStore().setActive(row,false);
        this.getTTable("TABLE1").setDSValue();
    }
    /**
     * �õ�TABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
}
