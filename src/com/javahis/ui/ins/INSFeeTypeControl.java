package com.javahis.ui.ins;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
/**
 * <p>Title: ҽ������</p>
 *
 * <p>Description: �շѵȼ�������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class INSFeeTypeControl extends TControl {
    private static String TABLE = "Table";
    private String action = "INSERT";
     public void onInit() {
         super.onInit();
         callFunction("UI|" + TABLE + "|addEventListener",TABLE + "->" + TTableEvent.CLICKED,this,"onTableClicked");
         onClear();
     }
     public void onTableClicked(int row)
     {
         if(row < 0)
             return;
         callFunction("UI|FEE_TYPE|setEnabled",false);
         callFunction("UI|delete|setEnabled",true);
         action = "EDIT";
     }
     /**
      * ����
      */
     public void onSave(){
         out("����begin");
         TParm parm = new TParm();
         parm.setData("OPT_USER",Operator.getID());
         parm.setData("OPT_TERM",Operator.getIP());
         if("EDIT".equals(action))
         {
             if(!emptyTextCheck("FEE_TYPE_DESC"))
                 return;
             callFunction("UI|" + TABLE + "|setModuleParmUpdate",parm);
             if(!(Boolean)callFunction("UI|" + TABLE + "|onUpdate")){
                 messageBox_("����ʧ��");
                 return;
             }
             messageBox_("����ɹ�");
         }else
         {
             if(!emptyTextCheck("FEE_TYPE,FEE_TYPE_DESC"))
                 return;
             callFunction("UI|" + TABLE + "|setModuleParmInsert",parm);
             if(!(Boolean)callFunction("UI|" + TABLE + "|onInsert")){
                 messageBox_("����ʧ��");
                 return;
             }
             messageBox_("�����ɹ�");
             action = "EDIT";
             callFunction("UI|delete|setEnabled",true);
             callFunction("UI|FEE_TYPE|setEnabled",false);
         }
         out("����end");
     }
     /**
      * ɾ��
      */
     public void onDelete(){
         out("ɾ��begin");
         if(messageBox("��ʾ��Ϣ","�Ƿ�ɾ��?",this.YES_NO_OPTION) != 0)
             return;
         int row = (Integer)callFunction("UI|" + TABLE + "|getSelectedRow");
         if(row < 0)
             return;
         if(!(Boolean)callFunction("UI|" + TABLE + "|onDelete"))
         {
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
      * ���
      */
     public void onClear(){
         out("���begin");
         clearValue("FEE_TYPE;FEE_TYPE_DESC;PY_CODE;REMARK");
         callFunction("UI|" + TABLE + "|clearSelection");
         callFunction("UI|save|setEnabled",true);
         callFunction("UI|delete|setEnabled",false);
         callFunction("UI|FEE_TYPE|setEnabled",true);
         action = "INSERT";
         out("���end");
     }
     /**
      * ��ѯ��ʼ���Զ�ִ��
      */
     public void onQuery()
     {
         callFunction("UI|" + TABLE + "|onQuery");
         int row = (Integer) callFunction("UI|" + TABLE + "|getRowCount");
         if (row < 0)
             this.onClear();

     }
     /**
    * �س�����ƴ��
    */
   public void onCode(){
       if ("".equals(String.valueOf(this.getValue("FEE_TYPE_DESC")))) {
           return;
       }
       SystemTool st = new SystemTool();
       String value = st.charToCode(String.valueOf(this.getValue(
           "FEE_TYPE_DESC")));
       if (null == value || "".equals(value)) {
           return;
       }
       this.setValue("PY_CODE", value);
       this.callFunction("UI|afterFocus","FEE_TYPE_DESC");
   }
}
