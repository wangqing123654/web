package com.javahis.ui.sys;

import com.dongyang.control.*;
import java.awt.event.KeyEvent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title: �豸�ֵ�</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author Miracle
 * @version 1.0
 */
public class DEVBASEPopupControl extends TControl {
    private String oldText = "";
    private TTable table;
    private String SQL="SELECT * FROM DEV_BASE";
    /** 
     * ����ע��
     */
    private String activeFlg;
    /**
     * �豸����
     */
    private String devKindCode;
    /**
     * �豸���
     */
    private String devTypeCode;
    /**
     * �豸����
     */
    private String devProCode;
    /**
     * ����;��
     */
    private String buyWayCode;
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
     * ��ʼ������
     */
    public void initParamenter() {
        Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        setEditText(text);
        String sql = " WHERE ";
        int andCount = 0;
        //����ע��
        activeFlg = parm.getValue("ACTIVE_FLG");
        if(activeFlg.length()!=0){
            sql+="ACTIVE_FLG='"+activeFlg+"'";
            andCount++;
        }
        //�豸����
        devKindCode = parm.getValue("DEVKIND_CODE");
        if(devKindCode.length()!=0){
            if(andCount>0){
                sql+= " AND ";
            }
            sql+="DEVKIND_CODE='"+devKindCode+"'";
            andCount++;
        }
        //�豸���
        devTypeCode = parm.getValue("DEVTYPE_CODE");
        if(devTypeCode.length()!=0){
            if(andCount>0){
                sql+= " AND ";
            }
            sql+="DEVTYPE_CODE='"+devTypeCode+"'";
            andCount++;
        }
        //�豸����
        devProCode = parm.getValue("DEVPRO_CODE");
        if(devProCode.length()!=0){
            if(andCount>0){
                sql+= " AND ";
            }
            sql+="DEVPRO_CODE='"+devProCode+"'";
            andCount++;
        }
        //����;��
        buyWayCode = parm.getValue("BUYWAY_CODE");
        if(buyWayCode.length()!=0){
            if(andCount>0){
                sql+= " AND ";
            }
            sql+="BUYWAY_CODE='"+buyWayCode+"'";
            andCount++;
        }
        this.SQL+=sql;
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
        table.filterObject(this, "filter");
        int count = table.getRowCount();
        if (count > 0)
            table.setSelectedRow(0);
    }

    /**
     * ���˷���
     * @param parm TParm
     * @param row int
     * @return boolean
     */
    public boolean filter(TParm parm, int row) {
        boolean falg = parm.getValue("DEV_CODE", row).toUpperCase().
            startsWith(oldText) ||
            parm.getValue("DEV_CHN_DESC", row).toUpperCase().indexOf(oldText) > 0 ||
            parm.getValue("DEV_ENG_DESC",
            row).toUpperCase().startsWith(oldText) ||
            parm.getValue("DEV_ABS_DESC", row).toUpperCase().startsWith(oldText) ||
            parm.getValue("PY1", row).toUpperCase().startsWith(oldText) ||
            parm.getValue("PY2", row).toUpperCase().startsWith(oldText);


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
     * ���±���
     */
    public void onResetDW() {
//        System.out.println("��ѯSQL��"+SQL);
        table.setSQL(SQL);
        table.retrieve();
    }

    /**
     * ��������ȫ��
     */
    public void onResetFile() {
        table.setSQL(SQL);
        table.retrieve();
    }
}
