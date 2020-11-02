package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import java.awt.event.KeyEvent;

/**
 * <p>Title: �ٴ�·����Ŀ</p>
 *
 * <p>Description: �ٴ�·����Ŀ</p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 2011-05-03
 * @version 1.0
 */
public class ClpBscInfoPopupControl extends TControl {
    private String oldText = "";
    private TTable table;
    private TParm dataParm;
    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * ǰ̨��������
     */
    private TParm tableData;
    /**
     * ��ѯSQL
     */
    private String SQL = " SELECT CLNCPATH_CODE, CLNCPATH_CHN_DESC, CLNCPATH_ENG_DESC, STAYHOSP_DAYS,"+
      " AVERAGECOST, ACPT_CODE, EXIT_CODE, PY1,PY2 FROM CLP_BSCINFO";
    public ClpBscInfoPopupControl() {

    }

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        table = (TTable) callFunction("UI|TABLE|getThis");
        callFunction("UI|EDIT|addEventListener", TTextFieldEvent.KEY_RELEASED, this,
                     "onKeyReleased");
        callFunction("UI|EDIT|addEventListener",
                     "EDIT->" + TKeyListener.KEY_PRESSED, this, "onKeyPressed");
        table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                               "onDoubleClicked");
        initParamenter();
        //��ʼ������
        onResetDW();
    }

    /**
     * ���±���
     */
    public void onResetDW() {
        dataParm = new TParm(this.getDBTool().select(SQL));
        table.setParmValue(dataParm);
       // table.retrieve();
    }

    /**
     * ��ʼ������
     */
    public void initParamenter() {
        Object obj = getParameter();
        if (obj == null)
            return;
        if (!(obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        setEditText(text);
    }

    /**
     * ���¼���
     */
    public void onInitReset() {
        Object obj = getParameter();
        if (obj == null)
            return;
        if (!(obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        String oldText = (String) callFunction("UI|EDIT|getText");
        if (oldText.equals(text)) {
            return;
        }
        setEditText(text);
    }

    /**
     * ������������
     * @param s String
     */
    public void setEditText(String s) {
        callFunction("UI|EDIT|setText", s);
        int x = s.length();
        callFunction("UI|EDIT|select", x, x);
        onKeyReleased(s);
    }

    /**
     * �����¼�
     * @param s String
     */
    public void onKeyReleased(String s) {
        s = s.toUpperCase();
        if (oldText.equals(s))
            return;
        oldText = s;
        int count = dataParm.getCount("CLNCPATH_CODE");
        String names[] = dataParm.getNames();
        TParm temp = new TParm();
        for (int i = 0; i < count; i++) {
            TParm rowParm = dataParm.getRow(i);
            if (this.filter(rowParm)) {
                for (String tempData : names) {
                    temp.addData(tempData, rowParm.getData(tempData));
                }
            }
        }
        table.setParmValue(temp);
        int rowConunt = temp.getCount("CLNCPATH_CODE");
        if (rowConunt > 0)
            table.setSelectedRow(0);
    }

    /**
     * ���˷���
     * @param parm TParm
     * @param row int
     * @return boolean
     */
    public boolean filter(TParm parm) {
        boolean falg = parm.getValue("CLNCPATH_CODE").toUpperCase().
                       contains(oldText) ||
                       parm.getValue("CLNCPATH_CHN_DESC").toUpperCase().contains(
                               oldText)  ||
                       parm.getValue("PY1").toUpperCase().contains(oldText) ||
                       parm.getValue("PY2").toUpperCase().contains(oldText);
        return falg;
    }
    /**
    * �����¼�
    * @param e KeyEvent
    */
   public void onKeyPressed(KeyEvent e) {
       if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
           callFunction("UI|setVisible", false);
           return;
       }
       int count = table.getRowCount();
       if (count <= 0)
           return;
       switch (e.getKeyCode()) {
           case KeyEvent.VK_UP:
               int row = table.getSelectedRow() - 1;
               if (row < 0)
                   row = 0;
               table.getTable().grabFocus();
               table.setSelectedRow(row);
               break;
           case KeyEvent.VK_DOWN:
               row = table.getSelectedRow() + 1;
               if (row >= count)
                   row = count - 1;
               table.getTable().grabFocus();
               table.setSelectedRow(row);
               break;
           case KeyEvent.VK_ENTER:
               callFunction("UI|setVisible", false);
               onSelected();
               break;
       }
   }
   /**
       * ѡ��
       */
      public void onSelected() {
          int row = table.getSelectedRow();
          if (row < 0)
              return;
          setReturnValue(table.getParmValue().getRow(row));
    }
    /**
    * ��������ȫ��
    */
   public void onResetFile() {
       this.setValue("EDIT","");
       TParm parm = new TParm(this.getDBTool().select(SQL));
       table.setParmValue(parm);
   }
   /**
    * ��˫���¼�
    * @param row int
    */
   public void onDoubleClicked(int row) {
       if (row < 0)
           return;
       callFunction("UI|setVisible", false);
       onSelected();
    }
}
