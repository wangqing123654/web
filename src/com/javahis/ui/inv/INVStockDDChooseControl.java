package com.javahis.ui.inv;

import jdo.inv.INVOrgTool;
import jdo.inv.InvBaseTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.RunClass;
/**
 *
 * <p>Title:ѡ����Ź�������� </p>
 *
 * <p>Description: ѡ����Ź��������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw
 * @version 1.0
 */
public class INVStockDDChooseControl extends TControl {
    /**
     * ��
     */
    private TTable table;
    /**
     * ǰ��control
     */
    private Object dispenseMControl;
    /**
     * ���������
     */
    private int insertRow=0;
    /**
     * �洢���ҵ�������
     */
    TDataStore dataStore=null;
    /**
     * ��Ҫѡ�������
     */
    private int qty=0;
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        table = (TTable)this.getComponent("TABLE");
        this.callFunction("UI|TABLE|addEventListener",
                          TTableEvent.CHECK_BOX_CLICKED, this,
                          "onDownTableCheckBoxChangeValue");
        onQuery();
        initCombox();
    }
    /**
   * ���ʿ���
   */
  public void initCombox() {
      TParm parm = INVOrgTool.getInstance().getDept();
      TComboBox comboBox = new TComboBox();
      comboBox.setParmMap("id:ORG_CODE;name:DEPT_DESC");
      comboBox.setParmValue(parm);
      comboBox.setShowID(true);
      comboBox.setShowName(true);
      comboBox.setExpandWidth(30);
      comboBox.setTableShowList("name");
      table.addItem("DEPT", comboBox);

//        comboBox = (TComboBox)this.getComponent("TO_ORG_CODE");
//        comboBox.setParmValue(parm);
//        comboBox.updateUI();

  }

  /**
   * ��ѯ
   */
  public void onQuery() {
      //�õ��ϸ����洫�����
      TParm parm = (TParm) getParameter();
      if (parm == null)
          return;
      dispenseMControl = parm.getData("INVDISPENSE");
      if (dispenseMControl == null)
          return;
      dataStore = new TDataStore();
      String sql = getSql(parm);
      if (sql == null)
          return;
      dataStore.setSQL(sql);
      dataStore.retrieve();
      TParm parmforTable = dataStore.getBuffer(dataStore.PRIMARY);
      int rowCount = parmforTable.getCount();
      for (int i = 0; i < rowCount; i++) {
          parmforTable.addData("S", "N");
      }
      table.setParmValue(parmforTable);
      table.updateUI();
      //��Ҫѡ��ĸ���
      qty = parm.getInt("QTY");
      TParm result=InvBaseTool.getInstance().getInvChnDesc(parm.getValue("INV_CODE"));    //wm2013-06-05  INVBaseTool.
      if(result.getErrCode()<0)
          return;
      this.setValue("INV_CHN_DESC",result.getValue("INV_CHN_DESC",0));
  }
  /**
   * ��ѯ���
   * @param parm TParm
     * @return String
     */
    public String getSql(TParm parm) {
       String sql="SELECT * FROM INV_STOCKDD WHERE ";
       //���ʴ���
       String value = parm.getValue("INV_CODE");
       if (value == null || value.length() == 0)
           return null;
       sql += "INV_CODE ='" + value + "'";
       //���Ҵ���
       value = parm.getValue("ORG_CODE");
       if (value == null || value.length() == 0)
           return null;
       sql += " AND ORG_CODE ='" + value + "'";
       //�������
       int batchSeq = parm.getInt("BATCH_SEQ");
       if(batchSeq!=0)
           sql += " AND BATCH_SEQ =" + batchSeq;
       //�����κ���������
       sql+="AND PACK_FLG = 'N'";
       return sql;
   }
   /**
    * ��ѡ����
    * @param obj Object
    */
   public void onDownTableCheckBoxChangeValue(Object obj){
        table.acceptText();
        //ѡ�е���
        int row=table.getSelectedRow();
        getInv(row);
   }
   /**
    * ѡ��һ������
    * @param row int
    */
   public void getInv(int row){
       //�õ�ѡ��checkBox
        String value=table.getItemString(row,"S");
        //�ж��Ƿ�ѡ��
        if (value.equals("Y")) {
            //�Ӵ洢��������ѡ��ѡ�е���
            TParm parm = dataStore.getRowParm(row);
            //����ǰ̨control�ķ���
            RunClass.runMethod(dispenseMControl, "onNewTableDDOneRow", new Object[] {parm});
            //ѡ������ݼӼ�
            insertRow++;
            setValue("QTY",insertRow);
            //���ѡ���㹻�������Զ��رմ���
            if(insertRow==qty)
               callFunction("UI|onClose");
            //ɾ���ղ�ѡ�е���
            table.removeRow(row);
        }

   }
   /**
    * ����ɨ��
    */
   public void onSelected() {
       String scream=getValueString("INV_CODE");
       String invCode=scream.substring(0, scream.length() - 4);
       int invSeqNo=Integer.parseInt(scream.substring( (scream.length() - 4), scream.length()));
       clearValue("INV_CODE;INVSEQ_NO");
       int rowCount=table.getRowCount();
       TParm parm=table.getParmValue();

       for(int i=0;i<rowCount;i++){
           if(parm.getValue("INV_CODE",i).equals(invCode) && parm.getInt("INVSEQ_NO",i)==invSeqNo){
               table.setValueAt("Y", i, 0);
               getInv(i);
               ((TTextField)getComponent("INV_CODE")).grabFocus();
               return;
           }
       }
       messageBox_("�����ʲ��ڿ�!");
   }

   /**
    * �Ƿ�رմ���(�������ݱ��ʱ��ʾ�Ƿ񱣴�)
    * @return boolean true �ر� false ���ر�
    */
   public boolean onClosing() {
       this.setReturnValue(insertRow);
       // ����������0
       if (qty != 0){
           //�ж�ѡ�����Ƿ��㹻
           if (qty != insertRow)
               switch (this.messageBox("��ʾ��Ϣ",
                                       "����" + (qty - insertRow) +
                                       "������û��ѡ��,�Ƿ����",
                                       this.YES_NO_OPTION)) {
                   //����
                   case 0:
                       return true;
                       //������
                   case 1:
                       return false;
               }
       }
       //û�б��������
       return true;
    }

}
