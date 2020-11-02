package com.javahis.ui.sys;

import com.dongyang.control.*;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.ui.TTableNode;
import jdo.sys.SYSSQL;

/**
 * <p>Title:Ʊ�Է������ </p>
 *
 * <p>Description:�������� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author zhangy  2009.10.12
 * @version 1.0
 */
public class SYSRecpparmControl
    extends TControl {
    /**
     * ��ʼ������
     *  @return TParm
     */
    public void onInit() {
        super.onInit();
        //table �ĵ��
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //��Table���ֵ�ı�
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                         "onTableStationChangeValue");
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        String sql = SYSSQL.getBillRecpparm();
        table.getDataStore().setSQL(sql);
        table.getDataStore().retrieve();
        table.setDSValue();
//        System.out.println("ssss"+table.getDataStore().getItemInt(0,0));
    }

    /**
     *���Ӷ�Table�ĵ��
     * @param row int
     */
    public void onTableClicked(int row) {
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        callFunction("UI|ADM_TYPE|setValue",
                     table.getDataStore().getRowParm(row).getValue("ADM_TYPE"));
        callFunction("UI|RECPIPT_TYPE|setValue",
                     table.getValueAt(row, table.getSelectedColumn()));
    }

    /**
     * table ��ֵ�ı��¼�
     */
    public boolean onTableStationChangeValue(Object obj) {
        //�õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        //����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
        if (node.getValue().equals(node.getOldValue()))
            return false;
        //�õ�table�ϵ�parmmap������
        String columnName = node.getTable().getDataStoreColumnName(node.
            getColumn());
        //�õ���ǰ�ı�������
        String value = "" + node.getValue();
        if (value == "" || value.length() == 0)
            return false;
        //���øı�
        if (columnName.startsWith("CHARGE")) {
            String[] columns = node.getTable().getDataStore().getColumns();
            for (int i = 0; i < columns.length; i++) {
                if (!columns[i].startsWith("CHARGE"))
                    continue;
                if (columnName.equals(columns[i]))
                    continue;
                String s = node.getTable().getDataStore().getItemString(node.
                    getRow(),
                    columns[i]);
                if (s != null && s.equals(value)) {
                    messageBox_("���" + node.getValue() + "�ظ�!");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ��������
     * @return TParm
     */
    public boolean onSave() {
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        //�����ı�
        table.acceptText();
        if (!CheckChange()) {
            return false;
        }
        Timestamp date = SystemTool.getInstance().getDate();

        //���ȫ���Ķ����к�
        int rowsBed[] = table.getModifiedRowsFilter();
        //���̶�����������
        for (int i = 0; i < rowsBed.length; i++) {
            table.setItemFilter(rowsBed[i], "OPT_USER", Operator.getID());
            table.setItemFilter(rowsBed[i], "OPT_DATE", date);
            table.setItemFilter(rowsBed[i], "OPT_TERM", Operator.getIP());
        }
        if (!table.update()) {
            messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        return true;
    }

    /**
     * ����Ƿ������ݱ��
     * @return boolean
     */
    public boolean CheckChange() {
        //������ݱ��
        TTable tableBed = (TTable) callFunction("UI|TABLE|getThis");
        if (tableBed.isModified())
            return true;
        return false;
    }

    /**
     * ���
     */
    public void onClear() {
        this.clearValue("RECP_TYPE;RECPIPT_TYPE");
    }

    /**
     * �Ƿ�رմ���
     * @return boolean true �ر� false ���ر�
     */
    public boolean onClosing() {
        // ��������ݱ��
        if (CheckChange())
            switch (this.messageBox("��ʾ��Ϣ",
                                    "�Ƿ񱣴�", this.YES_NO_CANCEL_OPTION)) {
                //����
                case 0:
                    if (!onSave())
                        return false;
                    break;
                    //������
                case 1:
                    return true;
                    //����
                case 2:
                    return false;
            }
        //û�б��������
        return true;

    }

    /**
     *
     * @param args String[]
     */
    public static void main(String args[]) {
        com.javahis.util.JavaHisDebug.TBuilder();
//       Operator.setData("admin","HIS","127.0.0.1","C00101");
    }

}
