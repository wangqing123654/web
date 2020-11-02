package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.jdo.TJDODBTool;
import java.io.File;

/**
 * <p>Title: ҽ������</p>
 *
 * <p>Description: ֧����׼��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class INSRuleControl extends TControl {
    private static String TABLE = "Table";
    private String action = "INSERT";
    public void onInit() {
        super.onInit();
        callFunction("UI|" + TABLE + "|addEventListener",
                     TABLE + "->" + TTableEvent.CLICKED, this, "onTableClicked");

        onClear();
    }

    /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
        if(row < 0)
              return;
          callFunction("UI|NHI_COMPANY|setEnabled", false);
          callFunction("UI|FEE_TYPE|setEnabled", false);
          callFunction("UI|CTZ_CODE|setEnabled", false);
          callFunction("UI|delete|setEnabled",true);
          TParm parm = this.getSelectRowData(TABLE);
          if(!parm.getBoolean("AMTPAY_FLG")&&!parm.getBoolean("ADDPAY_FLG")&&!parm.getBoolean("LIMIT_PRICE_FLG")&&!parm.getBoolean("STARTLINE_FLG")&&!parm.getBoolean("PLAN_FLG")&&!parm.getBoolean("ALLPRICE_FLG")&&!parm.getBoolean("PLAN_MAX_FLG")&&!parm.getBoolean("PLANJS_FLG")){
              callFunction("UI|ALLNO|setSelected",true);
          }
          action = "EDIT";
    }
    /**
    * �õ�ѡ��������
    * @param tableTag String
    * @return TParm
    */
   public TParm getSelectRowData(String tableTag){
       int selectRow = (Integer) callFunction("UI|" + tableTag +"|getSelectedRow");
       out("�к�" + selectRow);
       TParm parm = (TParm) callFunction("UI|" + tableTag + "|getParmValue");
       out("GRID����" + parm);
       TParm parmRow = parm.getRow(selectRow);
       parmRow.setData("OPT_USER",Operator.getID());
       parmRow.setData("OPT_TERM",Operator.getIP());
       return parmRow;
   }
    /**
     * ��ѯ
     */
    public void onQuery() {
        out("��ѯbegin");
        callFunction("UI|" + TABLE + "|onQuery");
        int row = (Integer) callFunction("UI|" + TABLE + "|getRowCount");
        if(row < 0)
            this.onClear();
        out("��ѯend");
    }
    /**
     * ����
     */
    public void onSave() {
        out("����begin");
        TParm parm = new TParm();
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        if ("EDIT".equals(action)) {
            if (!emptyTextCheck("NHI_COMPANY,FEE_TYPE,CTZ_CODE"))
                return;
            callFunction("UI|" + TABLE + "|setModuleParmUpdate", parm);
            if (!(Boolean) callFunction("UI|" + TABLE + "|onUpdate")) {
                messageBox_("����ʧ��");
                return;
            }
            messageBox_("����ɹ�");
        } else {
            if (!emptyTextCheck("NHI_COMPANY,FEE_TYPE,CTZ_CODE"))
                return;
            callFunction("UI|" + TABLE + "|setModuleParmInsert", parm);
            if (!(Boolean) callFunction("UI|" + TABLE + "|onInsert")) {
                messageBox_("����ʧ��");
                return;
            }
            messageBox_("�����ɹ�");
            action = "EDIT";
            callFunction("UI|delete|setEnabled", true);
            callFunction("UI|NHI_COMPANY|setEnabled", false);
            callFunction("UI|FEE_TYPE|setEnabled", false);
            callFunction("UI|CTZ_CODE|setEnabled", false);
        }
        out("����end");
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        out("ɾ��begin");
        if (messageBox("��ʾ��Ϣ", "�Ƿ�ɾ��?", this.YES_NO_OPTION) != 0)
            return;
        int row = (Integer) callFunction("UI|" + TABLE + "|getSelectedRow");
        if (row < 0)
            return;
        if (!(Boolean) callFunction("UI|" + TABLE + "|onDelete")) {
            messageBox_("ɾ��ʧ��");
            return;
        }
        messageBox_("ɾ���ɹ�");
        int rows = (Integer) callFunction("UI|" + TABLE + "|getSelectedRow");
        if (rows < 0) {
            this.onClear();
        }
        out("ɾ��end");
    }

    /**
     *���
     */
    public void onClear() {
        out("���begin");
        clearValue("NHI_COMPANY;FEE_TYPE;CTZ_CODE;START_RANGE;END_RANGE;OWN_RATE;STA_SEQ;INS_PAY_PARM;STARTLINE_PRICE;PLAN_PRICE;PLAN_MAX_FLG;PLANJS_FLG");
        callFunction("UI|ALLNO|setSelected",true);
        callFunction("UI|" + TABLE + "|clearSelection");
        callFunction("UI|save|setEnabled", true);
        callFunction("UI|delete|setEnabled", false);
        callFunction("UI|NHI_COMPANY|setEnabled",true);
        callFunction("UI|FEE_TYPE|setEnabled",true);
        callFunction("UI|CTZ_CODE|setEnabled",true);
        action = "INSERT";
        out("���end");
    }
//    /**
//     * ִ��
//     */
//    public void onOk(){
//        StringBuffer buff = new StringBuffer();
//        TParm parm = new TParm(this.getDBTool().select("SELECT ORDER_CODE FROM SYS_FEE WHERE SYSDATE BETWEEN START_DATE AND END_DATE"));
//        int rowCount = parm.getInt("ACTION","COUNT");
//        for(int i=0;i<rowCount;i++){
//            TParm action = new TParm(this.getDBTool().select("SELECT ORDER_CODE FROM SYS_FEE WHERE ORDER_CODE='"+parm.getValue("ORDER_CODE",i)+"' AND SYSDATE BETWEEN START_DATE AND END_DATE"));
//            if(action.getInt("ACTION","COUNT")>1){
//                buff.append(action.getValue("ORDER_CODE",0));
//                buff.append("\n");
//            }
//        }
//        this.messageBox(""+buff);
//    }
    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

}
