package com.javahis.ui.sys;

import java.awt.event.KeyEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.control.TControl;

/**
 * <p>Title:������ </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:JavaHis </p>
 *
 * @author JiaoY 2009.05.06
 * @version 1.0
 */
public class SYSHomePlacePopupControl  extends TControl{
    /**
   * ��
   */
  private TTable table;
  //
  private String oldText = "";

  /**
  * ��ʼ��
  */
 public void onInit()
 {
     super.onInit();
     table = (TTable) callFunction("UI|TABLE|getThis");
     callFunction("UI|HOMEPLACE_CODE|addEventListener", TTextFieldEvent.KEY_RELEASED, this,
                  "onKeyReleased");
     callFunction("UI|HOMEPLACE_CODE|addEventListener",
                  "HOMEPLACE_CODE->" + TKeyListener.KEY_PRESSED, this, "onKeyPressed");
     table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                            "onDoubleClicked");
     initParamenter();
 }
 /**
    * ��ʼ������
    */
   public void initParamenter()
   {
       Object obj = getParameter();
       if (obj == null)
           return;
       if (! (obj instanceof TParm))
           return;
       TParm parm = (TParm) obj;
       String text = parm.getValue("TEXT");
       setEditText(text);
   }
 /**
  * ������������
  * @param s String
  */
 public void setEditText(String str)
 {
     callFunction("UI|HOMEPLACE_CODE|setText",str);
     int x = str.length();
     callFunction("UI|HOMEPLACE_CODE|select",x,x);
     onKeyReleased(str);
 }

 /**
  * ���¼���
  */
 public void onInitReset() {
     Object obj = getParameter();
     if (obj == null)
         return;
     if (! (obj instanceof TParm))
         return;
     TParm parm = (TParm) obj;
     String text = parm.getValue("TEXT");
     String oldText = (String) callFunction("UI|HOMEPLACE_CODE|getText");
     if (oldText.equals(text))
         return;
     setEditText(text);
 }
 /**
  * �����¼�
  * @param s String
  */
 public void onKeyReleased(String s)
 {
     s = s.toUpperCase();
     if(oldText.equals(s))
         return;
     oldText = s;
     filter();
     int count = table.getRowCount();
     if(count > 0)
         table.setSelectedRow(0);
 }
 /**
      * ���˷���
      * @param parm TParm
      * @param row int
      * @return boolean
      */
     public void filter(){
         String filterSql = "HOMEPLACE_CODE like '" + oldText +
             "%' OR HOMEPLACE_DESC like '" + oldText + "%' OR PY1 like '" + oldText +"%'";
         table.setFilter(filterSql);
         table.filter();
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

     /**
      * ѡ��
      */
     public void onSelected() {
         int row = table.getSelectedRow();
         if (row < 0)
             return;
         TDataStore dataStore = table.getDataStore();
         TParm parm = dataStore.getRowParm(row);
         setReturnValue(parm);
     }
     /**
   * �����¼�
   * @param e KeyEvent
   */
  public void onKeyPressed(KeyEvent e)
  {
      if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
      {
          callFunction("UI|setVisible",false);
          return;
      }
      int count = table.getRowCount();
      if(count <= 0)
          return;
      switch(e.getKeyCode()){
      case KeyEvent.VK_UP:
          int row = table.getSelectedRow() - 1;
          if(row < 0)
              row = 0;
          table.setSelectedRow(row);
          break;
      case KeyEvent.VK_DOWN:
          row = table.getSelectedRow() + 1;
          if(row >= count)
              row = count - 1;
          table.setSelectedRow(row);
          break;
      case KeyEvent.VK_ENTER:
          callFunction("UI|setVisible",false);
          onSelected();
          break;
      }
  }

}
