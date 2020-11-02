package com.javahis.ui.dev;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;

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
public class SupControl extends TControl {
    /**
     * ����������
     */
    private String actionName = "action.dev.DevAction";

    /**
     * table
     */
    private static String TABLE = "TABLE";
    /**
     * Ȩ��
     */
    private String popedemType="1";
    /**
     * ��ʼ��
     */
    public void onInit() {
        Object obj = this.getParameter();
        if(obj!=null){
            this.getTTable(TABLE).setParmValue((TParm)obj);
            popedemType = ((TParm)obj).getValue("POPEDEM");
        }
        callFunction("UI|" + TABLE + "|addEventListener",
                    TABLE + "->" + TTableEvent.DOUBLE_CLICKED, this, "onTableDoubleClicked");
    }
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
    /**
     * ˫���¼�
     * @param row int
     */
    public void onTableDoubleClicked(int row){
        TParm rowParm = this.getTTable(TABLE).getParmValue().getRow(row);
        if(popedemType.equals("1")&&!rowParm.getBoolean("CHOOSE_FLG")){
            this.messageBox("����Ȩ���޷�ѡ��δ�б�ĳ��̣�");
            this.setReturnValue(null);
            this.closeWindow();
            return;
        }
        if(popedemType.equals("2")&&!rowParm.getBoolean("CHOOSE_FLG")){
            if(this.messageBox("��Ϣ��ʾ","�˳���δ�б��Ƿ����Ϊ�б�״̬��",this.YES_NO_OPTION)!=0){
                this.closeWindow();
                return;
            }
            if(!updateStart(rowParm)){
                this.closeWindow();
                return;
            }
            this.setReturnValue(rowParm);
            this.closeWindow();
            return;
        }
        this.setReturnValue(rowParm);
        this.closeWindow();
    }
    /**
     * �����б�״̬
     * @param parm TParm
     * @return boolean
     */
    public boolean updateStart(TParm parm){
        boolean falg = true;
        String[] sql =new String[]{"UPDATE DEV_NEGPRICE SET CHK_DATE=SYSDATE,CHK_USER='"+Operator.getID()+"',CHOOSE_FLG='Y' WHERE REQUEST_NO='"+parm.getValue("REQUEST_NO")+"' AND SUP_CODE='"+parm.getValue("SUP_CODE")+"'"};
        TParm sqlParm = new TParm();
        sqlParm.setData("SQL",sql);
        TParm actionParm = TIOM_AppServer.executeAction(actionName,"saveDevRequest", sqlParm);
        if (actionParm.getErrCode() < 0) {
            this.messageBox("����ʧ�ܣ�");
            falg = false;
        }
        this.messageBox("���³ɹ���");
        return falg;
    }
}
