package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextField;
import java.awt.event.KeyEvent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.tui.text.EAssociateChoose;
import java.util.Vector;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;

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
public class AssociatePopMenuControl
    extends TControl {
    private static final String SUB_TEMPLATE_PAHT="JHW/Ƭ��";
    /**
     * ��ѡ�ı�����
     */
    private EAssociateChoose eAssociateChoose;
    private TTable table;
    private TTextField value;
    /**
     * ��ʼ��
     */
    public void onInit() {
        eAssociateChoose = (EAssociateChoose) getParameter();
        if (eAssociateChoose == null) {
            return;
        }
        table = (TTable) getComponent("TABLE");
        table.setValue(eAssociateChoose.getData());
        String text = eAssociateChoose.getText();
        value = (TTextField) getComponent("VALUE");
        value.setText(text);
        for (int i = 0; i < table.getRowCount(); i++) {
            if (text.equals(table.getValueAt(i, 0))) {
                table.setSelectedRow(i);
                break;
            }
        }
        table.setSelectedColumn(0);
        callFunction("UI|VALUE|addEventListener",
                     "VALUE->" + TKeyListener.KEY_PRESSED, this, "onKeyPressed");
    }

    /**
     * �����¼�
     * @param e KeyEvent
     */
    public void onKeyPressed(KeyEvent e) {
        //this.messageBox("onKeyPressed"+e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            closeWindow();
            return;
        }
        int count = table.getRowCount();
        if (count <= 0) {
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                int row = table.getSelectedRow() - 1;
                if (row < 0) {
                    row = 0;
                }
                table.setSelectedRow(row);
                value.setText( (String) table.getValueAt(row, 0));
                value.selectAll();
                break;
            case KeyEvent.VK_DOWN:
                row = table.getSelectedRow() + 1;
                if (row >= count) {
                    row = count - 1;
                }
                table.setSelectedRow(row);
                value.setText( (String) table.getValueAt(row, 0));
                value.selectAll();
                break;
            case KeyEvent.VK_ENTER:
                onSelect();
                break;
            default:
                table.clearSelection();
                break;
        }
    }

    /**
     * ѡ��
     */
    public void onSelect() {
        int row = table.getSelectedRow();
        String startTag = eAssociateChoose.getStartTag();
        //this.messageBox("startTag"+startTag);
        String endTag = eAssociateChoose.getEndTag();
        //this.messageBox("endTag"+endTag);

        if (row == -1) {
            eAssociateChoose.setText(value.getText());
        }

        else {
            //this.messageBox("����ģ���ļ�����"+(String) table.getValueAt(row, 3));
            Vector d = table.getValue();
            //this.messageBox("����ģ���ļ���"+(((Vector)d.get(row))).get(3));
            String fileName = (String) ( ( (Vector) d.get(row))).get(3);

            //this.messageBox("file name" + fileName);
            if (!fileName.equals("")) {
                //ɾ���Է�Χ�����ݣ�
                //delete(startTag,endTag,false);
                eAssociateChoose.clear();
                //this.messageBox("==startTag=="+startTag);
                //������ģ������
                EFixed fiexed = (EFixed) eAssociateChoose.getPM().
                    getFocusManager().findObject(startTag,
                                                 EComponent.FIXED_TYPE);
                //this.messageBox("fixed" + fiexed.getName());
                fiexed.setFocusLast();

                //���ļ�����ؼ�;
                //insertFile(SUB_TEMPLATE_PAHT,fileName);

                boolean b = fiexed.getPM().getFocusManager().onInsertFile(
                    "JHW/Ƭ��", fileName, 2, false);

               //ɾ��һ����ģ�濪ʼ�Ļس�
               fiexed.setFocusLast();
               boolean b1=fiexed.deleteChar();

               //this.messageBox("b1=="+b1);
               //ɾ��һ�н�������ģ������Ļس�
               EFixed fiexedEnd = (EFixed) eAssociateChoose.getPM().
                    getFocusManager().findObject(endTag, EComponent.FIXED_TYPE);
               //this.messageBox("fiexedEnd.hasPreviousLink()=="+fiexedEnd.hasPreviousLink());
               fiexedEnd.setFocus(0);
               //this.messageBox("name"+fiexedEnd.getName());
               fiexedEnd.backspaceChar();
               //

            }
            else {
                //ɾ����Χ�ڵ����ݣ�
                //delete(startTag, endTag,true);
                 eAssociateChoose.clear();
            }
            eAssociateChoose.setText( (String) table.getValueAt(row, 0));
        }
        eAssociateChoose.getPM().getFocusManager().update();
        closeWindow();
    }

    /**
     * ȷ��
     */
    public void onOK() {
        onSelect();
    }

    /**
     * ȡ��
     */
    public void onCancel() {
        closeWindow();
    }

}
