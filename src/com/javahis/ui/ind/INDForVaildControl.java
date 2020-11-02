package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import jdo.ind.INDSQL;

/**
 * <p>Title: ����Ч�ڲ�ѯControl</p>
 *
 * <p>Description: ����Ч�ڲ�ѯControl</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangy 2009.05.23
 * @version 1.0
 */
public class INDForVaildControl
    extends TControl {

    private TParm parm;
    // ��ѯ����
    private String org_code;
    // ��ѯҩƷ
    private String order_code;
    // ��λ����
    private String unit_type;

    public INDForVaildControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ȡ�ô������
        Object obj = getParameter();
        if (obj != null) {
            parm = (TParm) obj;
            org_code = parm.getValue("ORG_CODE");
            order_code = parm.getValue("ORDER_CODE");
            unit_type = parm.getValue("UNIT_TYPE");
        }
        // ��ʼ��������
        initPage();
    }

    /**
     * ���ط���
     */
    public void onReturn() {
        TTable table = getTable("TABLE");
        table.acceptText();
        if (table.getRowCount() < 0) {
            return;
        }
        if (table.getSelectedRow() < 0) {
            this.messageBox("û��ѡ����");
            return;
        }
        TParm result = table.getParmValue().getRow(table.getSelectedRow());
        //System.out.println("---"+result);
        setReturnValue(result);
        this.closeWindow();
    }

    /**
     * ˫��ѡ�������
     */
    public void onDoubleClickedAction() {
        TTable table = getTable("TABLE");
        table.acceptText();
        if (table.getRowCount() < 0) {
            return;
        }
        if (table.getSelectedRow() < 0) {
            this.messageBox("û��ѡ����");
            return;
        }
        TParm result = table.getParmValue().getRow(table.getSelectedRow());
        //System.out.println("---"+result);
        setReturnValue(result);
        this.closeWindow();
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ѯ��Ϣ
        String sql = INDSQL.getOrderBatchNoValid(org_code, order_code, unit_type);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("ҩƷ��Ϣ����");
            return;
        }
        if (result.getCount("ORDER_CODE") == 0) {
            this.messageBox("û��ҩƷ��Ϣ");
            return;
        }
        // ���TABLE
        getTable("TABLE").setParmValue(result);
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }


}
