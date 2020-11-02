package com.javahis.ui.bil;

import com.dongyang.control.*;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import jdo.bil.BILInvoiceTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTableNode;
import jdo.bil.BILAccountTool;
import jdo.bil.BILCounteTool;
import com.dongyang.util.StringTool;

/**
 * <p>Title:�����ս� </p>
 *
 * <p>Description:�����ս� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author FUDW
 * @version 1.0
 */
public class BILAccountControl
    extends TControl {
    int selectrow = -1;
    TParm data = new TParm();
    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        //table1�������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table1ֵ�ı��¼�
        this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                              "onTableChangeValue");
        this.onQuery();
    }

    /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
        //���������¼�
        this.callFunction("UI|TABLE|acceptText");
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        setValueForParm("ACCOUNT_TYPE;ACCOUNT_USER;TOT_AMT",
                        data, row); //�����Ϸ�
        selectrow = row;
        callFunction("UI|delete|setEnabled", true);
    }

    /**
     * ���Ӷ�Tableֵ�ı�ļ���
     * @param obj Object
     */
    public void onTableChangeValue(Object obj) {
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return;
        if (node.getColumn() != 0)
            return;
        node.getTable().getParmValue().setData("FLG", node.getRow(),
                                               node.getValue());
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        //�õ���ѯ����
        TParm parm = new TParm();
        //���table
        this.callFunction("UI|TABLE|removeRowAll");
        data = new TParm();
        //��ѯ����
        data = BILAccountTool.getInstance().selectData(parm);
        //��������
//        data = this.FinishData(data);
        //��table���
        this.callFunction("UI|TABLE|setParmValue", data);
    }

    /**
     * ���ʷ���
     */
    public void onClosedata() {
        //��˽����ϵ�����
        if (!this.emptyTextCheck("ACCOUNT_TYPE,ACCOUNT_USER"))
            return;
        TParm datat = this.Value();
        datat.setData("CLS_DATE",
                      StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyyMMdd HH:mm:ss"));
        //��Ҫ��һ
        datat.setData("END_INVNO", datat.getData("UPDATE_NO"));
        datat.setData("CASHIER_CODE", getValueString("ACCOUNT_USER"));
        datat.setData("OPT_USER", Operator.getID());
        datat.setData("OPT_TERM", Operator.getIP());
        TParm result = BILCounteTool.getInstance().updataData(datat);
        if (result.getErrCode() < 0) {
            this.messageBox("E0005"); //ִ��ʧ��
            return;
        }
        else {
            this.messageBox("P0005"); //ִ�гɹ�
        }
    }

    /**
     * �ս᷽��
     */
    public void onAccount() {
        //��˽����ϵ�����
        if (!this.emptyTextCheck("ACCOUNT_TYPE,ACCOUNT_USER"))
            return;
        if (this.checkout().getValue("RECP_TYPE").length() == 0) {
            this.messageBox("���ȹ���");
            return;
        }
        TParm parm = new TParm();
    }

    /**
     * ����Ƿ��Ѿ�����״̬.���ص�ǰʹ��Ʊ��
     * @return String
     */
    public TParm checkout() {
        //����ʱ��
        TParm parm = getParmForTag("CASHIER_CODE;RECP_TYPE");
        parm.setData("CLS_DATE", "");
        TParm result = BILAccountTool.getInstance().selectData(parm);
        return result;
    }

    /**
     * ��ѯ��ǰʹ���ŵ�Ʊ��
     * @return TParm
     */
    public TParm Value() {
        TParm parm = getParmForTag("CASHIER_CODE;RECP_TYPE");
        parm.setData("STARTDATE",
                     StringTool.getString(SystemTool.getInstance().getDate(),
                                          "yyyyMM") + " 00:00:00");
        parm.setData("ENDDATE",
                     StringTool.getString(SystemTool.getInstance().getDate(),
                                          "yyyyMM") + " 23:59:59");
        //1��ʾ����ʹ����
        parm.setData("STATUS", "1");
        TParm result = BILInvoiceTool.getInstance().selectAllData(parm);
        return result;
    }
}
