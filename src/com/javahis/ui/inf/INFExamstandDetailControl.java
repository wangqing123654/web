package com.javahis.ui.inf;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;

/**
 * <p>Title: ���Ҽ�ⷽ�������Ŀ���� </p>
 *
 * <p>Description: ���Ҽ�ⷽ�������Ŀ���� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis </p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFExamstandDetailControl extends TControl {

    /**
     * ��ʼ������
     */
    public void onInit() {
        //�豸��ϸ���༭�¼�
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,"onTableValueChange");
        super.onInit();
    }

    /**
     * ����׼�����¼�
     */
    public void onStand(){
        if(getValueString("EXAMSTAND_CODE").length() == 0){
            getThisTable().removeRowAll();
            getThisTable().getDataStore().resetModify();
            getThisTable().setSQL("");
            getThisTable().retrieve();
            return;
        }
        String SQL = " SELECT EXAMSTAND_CODE,INFITEM_CODE,ITEM_GAINPOINT,"+
                     "        OPT_USER,OPT_DATE,OPT_TERM "+
                     " FROM INF_EXAMSTANDDETAIL "+
                     " WHERE EXAMSTAND_CODE = '"+getValue("EXAMSTAND_CODE")+"'";
        getThisTable().setSQL(SQL);
        getThisTable().retrieve();
    }

    /**
     * ȡ�ý�����ؼ�
     * @param tag String
     * @return TTable
     */
    public TTable getThisTable(){
        return (TTable)getComponent("TABLE");
    }

    /**
     * ��������
     */
    public void onNew(){
        if(getValueString("EXAMSTAND_CODE").length() == 0){
            messageBox("��ѡ�񷽰�");
            return;
        }
        getThisTable().addRow();
        Timestamp timestamp = SystemTool.getInstance().getDate();
        getThisTable().setItem(getThisTable().getRowCount() - 1,
                                              "EXAMSTAND_CODE",
                                              getValueString("EXAMSTAND_CODE"));
        getThisTable().setItem(getThisTable().getRowCount() - 1,"OPT_USER",Operator.getID());
        getThisTable().setItem(getThisTable().getRowCount() - 1,"OPT_TERM",Operator.getIP());
        getThisTable().setItem(getThisTable().getRowCount() - 1,"OPT_DATE",timestamp);

    }

    /**
     * ɾ������
     */
    public void onDelete(){
        int row = getThisTable().getSelectedRow();
        if(row < 0)
            return;
        getThisTable().removeRow(row);
    }

    /**
     * ������
     * @return boolean
     */
    private boolean onSaveCheck(){
        double tot = 0;
        //ȫ��ɾ��
        if(getThisTable().getRowCount() <= 0)
            return false;
        for(int i = 0;i<getThisTable().getRowCount();i++){
            if(getThisTable().getItemData(i,"INFITEM_CODE") == null ||
               getThisTable().getItemString(i,"INFITEM_CODE").length() == 0){
                messageBox("��"+(i+1)+"����Ŀ���벻��Ϊ��");
                return true;
            }
            if(getThisTable().getItemData(i,"ITEM_GAINPOINT") == null ||
               getThisTable().getItemString(i,"ITEM_GAINPOINT").length() == 0||
               getThisTable().getItemDouble(i,"ITEM_GAINPOINT") == 0){
                messageBox("��"+(i+1)+"�м�ⵥ�����Ȳ���Ϊ��");
                return true;
            }
            tot += getThisTable().getItemDouble(i,"ITEM_GAINPOINT");
        }
        if(tot != 100){
            messageBox("��ⵥ�������ۺϲ��ɲ�����100");
            return true;
        }
        return false;
    }

    /**
     * ���ò�����Ա����ʱ�������ĩ
     */
    private void setOptData(){
        Timestamp timestamp = SystemTool.getInstance().getDate();
        for(int i = 0;i<getThisTable().getRowCount();i++){
            getThisTable().setItem(i,"OPT_USER",Operator.getID());
            getThisTable().setItem(i,"OPT_TERM",Operator.getIP());
            getThisTable().setItem(i,"OPT_DATE",timestamp);
        }
    }

    /**
     * ���淽��
     */
    public void onSave(){
        getThisTable().acceptText();
        if(onSaveCheck())
            return;
        setOptData();
        if(getThisTable().getDataStore().update()){
            messageBox("����ɹ�");
            return;
        }
        messageBox("����ʧ��");
    }

    /**
     * �����Ŀ�����¼�
     * @param obj Object
     * @return boolean
     */
    public boolean onTableValueChange(Object obj) {
        TTableNode node = (TTableNode) obj;
        if(node.getColumn() != 1)
            return false;
        for(int i = 0;i < getThisTable().getRowCount();i++){
           if(node.getRow() == i)
               continue;
           if(!node.getValue().equals(getThisTable().getItemData(i,"INFITEM_CODE")))
               continue;
           return true;
        }
        return false;
    }


    /**
     * ��շ���
     */
    public void onCLear(){
        setValue("EXAMSTAND_CODE","");
        getThisTable().removeRowAll();
        getThisTable().getDataStore().resetModify();
        getThisTable().setSQL("");
        getThisTable().retrieve();
    }
}
