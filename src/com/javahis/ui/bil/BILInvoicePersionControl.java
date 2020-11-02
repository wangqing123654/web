package com.javahis.ui.bil;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import jdo.bil.BILInvoiceTool;
import jdo.sys.Operator;
import jdo.bil.BILCounteTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import jdo.bil.BILInvrcptTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TComboBox;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:����Ʊ�Ź��� </p>
 *
 * <p>Description:����Ʊ�Ź��� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */
public class BILInvoicePersionControl
    extends TControl {
    int selectrow = -1;
    TParm data = new TParm();
    TTable tableM;
    TTable tableD;
    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        tableM = (TTable)this.getComponent("TABLE1");
        tableD = (TTable)this.getComponent("TABLE2");
        //table1�������¼�
        callFunction("UI|TABLE1|addEventListener", "TABLE1->"
                     + TTableEvent.CLICKED, this, "onTableClicked");

        //        //checkbox����¼�
        //        this.callFunction("UI|TABLE1|addEventListener",
        //                          TTableEvent.CHECK_BOX_CLICKED, this,
        //                          "onCheckCliced");
        //
        //        //table1ֵ�ı��¼�
        //        this.addEventListener("TABLE1->" + TTableEvent.CHANGE_VALUE,
        //                              "onTableChangeValue");
        //����ʹ��ID
        this.callFunction("UI|CASHIER_CODE|setValue", Operator.getID());
        callFunction("UI|CASHIER_CODE|setEnabled", false);
        //����ʹ����
        this.callFunction("UI|OPT_TERM|setValue", Operator.getIP());
        callFunction("UI|OPT_TERM|setEnabled", false);
        this.onQuery();
        initCombo();
    }

    /**
     * ��ʼ��combo
     */
    public void initCombo() {
        TComboBox comboBox = new TComboBox();
        comboBox.setParmMap("id:ORG_CODE;name:DEPT_DESC");
        comboBox.setStringData(
            "[[id,name],[,],['0','��Ч'],['1','����'],['2','����Ʊ��'],['3','���ϲ�ӡ']]");
        comboBox.setShowID(true);
        comboBox.setShowName(true);
        comboBox.setExpandWidth(30);
        comboBox.setTableShowList("name");
        tableD.addItem("SS", comboBox);

    }

    /**
     * ���Ӷ�Table�ļ���
     */
    public void onTableClicked() {
        int row = tableM.getSelectedRow();
        //��¼��ǰѡ�е���
        selectrow = row;
        //�õ���ǰѡ��������
        TParm parm = tableM.getParmValue().getRow(row);
        //��ѯƱ����ϸ
        TParm result = BILInvrcptTool.getInstance().selectByInvNo(parm);
        if (result.getErrCode() < 0)
            return;
        tableD.setParmValue(result);
        //�����һƱ��Ϊ��,��˵����Ʊ���Ѿ��ù�
        if (parm.getValue("UPDATE_NO") == null ||
            parm.getValue("UPDATE_NO").length() == 0)
            return;
        for (int i = 0; i < tableM.getRowCount(); i++) {
            tableM.setValueAt("N", i, 0);
        }
        tableM.setValueAt("Y", row, 0);
        tableM.acceptText();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        selectrow = -1;
        //�õ���ѯ����
        TParm parm = this.getdata();
        //���table
        this.callFunction("UI|TABLE1|removeRowAll");
        data = new TParm();
        //��ѯ����
        data = BILInvoiceTool.getInstance().selectAllData(parm);
        //��table���
        tableM.setParmValue(data);
    }

    /**
     * ѡ������������table
     */
    public void onSelect() {
        this.onQuery();
    }

    /**
     * ��ѯ���
     * @return TParm
     */
    public TParm getdata() {
        TParm parm = new TParm();
        String value = getValueString("RECP_TYPE");
        if (value.length() > 0)
            parm.setData("RECP_TYPE", value);
        value = Operator.getID();
        if (value.length() != 0)
            parm.setData("CASHIER_CODE", value);
        //0��ʾʹ���У�1��ʾ״̬��û�н��ص�
        parm.setData("STATUS", "1");
        return parm;
    }

    /**
     * ����
     */
    public void onSave() {
        int row = tableM.getSelectedRow();
        if (row < 0) {
            this.messageBox("E0012");
            return;
        }
        //ȡtable�����ݣ�ת��TParm
        TParm parm = tableM.getParmValue().getRow(row);
        //�����һƱ��Ϊ��,��˵����Ʊ���Ѿ��ù�
        if (parm.getValue("UPDATE_NO") == null ||
            parm.getValue("UPDATE_NO").length() == 0) {
            messageBox_("��Ʊ���Ѿ�����!");
            return;
        }
        //01����ʹ����
        if (checkout(parm)) {
            //��������....�Ѿ����˲����ٿ�
            this.messageBox("E0013");
            return;
        }
        //datat.setData("START_INVNO", parm.getData("UPDATE_NO", selectrow));
        //״̬0����ʹ����
        parm.setData("STATUS", "0");
        parm.setData("OPEN_DATE", StringTool.getString(SystemTool
            .getInstance().getDate(), "yyyyMMdd HHmmss"));
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("TERM_IP", Operator.getIP());
//        System.out.println("����Action����" + parm);
        //���ÿ���Action
        TParm result = TIOM_AppServer.executeAction(
            "action.bil.InvoicePersionAction", "opencheck", parm);
        if (result.getErrCode() != 0) {
            this.messageBox("E0005"); //ִ��ʧ��
        }
        else {
            this.messageBox("P0005"); //ִ�гɹ�
        }
        onQuery();
    }

    /**
     * ���ʷ���
     */
    public void onClosedata() {
        int row = tableM.getSelectedRow();
        if (row < 0) {
            this.messageBox("E0012");
            return;
        }
        //ȡtable�����ݣ�ת��TParm
        TParm parm = tableM.getParmValue().getRow(row);

        if (!parm.getValue("STATUS").equals("0")) {
            this.messageBox("��Ʊ�ݲ���ʹ����");
            return;
        }
        if (!checkout(parm)) {
            this.messageBox("E0014");
            return;
        }
        //����action������
        TParm closeParm = new TParm();
        //����invoice��������
        parm.setData("STATUS", "1");
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("TERM_IP", Operator.getIP());
        closeParm.setData("invoice", parm.getData());
        parm.setData("CLS_DATE",
                     StringTool.getString(SystemTool.getInstance()
                                          .getDate(), "yyyyMMdd HH:mm:ss"));
        //��Ҫ��1
        //�����һƱ��Ϊ��,�����Ʊ�ž��ǽ���Ʊ��
        if (parm.getValue("UPDATE_NO") == null ||
            parm.getValue("UPDATE_NO").length() == 0) {
            parm.setData("END_INVNO", parm.getValue("END_INVNO"));
        }
        else {
            //��һƱ�ż�һ��Ϊ����Ʊ��
            parm.setData("END_INVNO",
                         StringTool.subString(parm.getValue("UPDATE_NO")));
        }
        //�����һƱ�ŵ��ڳ�ʼƱ��,����Ʊ�ŵ�����ʼƱ��
        if (parm.getValue("START_INVNO").equals(parm.getValue("UPDATE_NO")))
            parm.setData("END_INVNO", parm.getValue("UPDATE_NO"));
        //��Ӹ���counter��������
        closeParm.setData("counter", parm.getData());
        //���ù���Action
        TParm result = TIOM_AppServer.executeAction(
            "action.bil.InvoicePersionAction", "closeCheck", closeParm);
        if (result.getErrCode() != 0) {
            this.messageBox("E0005"); //ִ��ʧ��
        }
        else {
            this.messageBox("P0005"); //ִ�гɹ�
        }
        this.onQuery();
    }

//    /**
//     *��Ʊ
//     */
//    public void Recipients() {
//        String value = getValueString("RECP_TYPE");
//        if (value.length() == 0) {
//            this.messageBox("E0012");
//            return;
//        }
//        this.openDialog("%ROOT%\\config\\bil\\BILRecipients.x", value);
//        this.onQuery();
//    }

    /**
     *�ɻ�
     */
    public void returnback() {
        if (selectrow < 0) {
            this.messageBox("E0012");
            return;
        }
        //ȡtable�����ݣ�ת��TParm
        TParm parm = (TParm)this.callFunction("UI|TABLE1|getParmValue");
        TParm datat = new TParm();
        datat.setRowData( -1, parm, selectrow);
        if (checkout(datat)) {
            this.messageBox("�˹�̨��δ����");
            return;
        }
        this.openDialog("%ROOT%\\config\\bil\\BILRecipientsReturn.x", datat);
        this.onQuery();
    }

    /**
     * ����Ʊ��
     */
    public void onAdjustment() {
        if (selectrow < 0) {
            this.messageBox("E0012");
            return;
        }
        TParm parm = (TParm)this.callFunction("UI|TABLE1|getParmValue");
        TParm datat = new TParm();
        datat.setRowData( -1, parm, selectrow);
        datat.setData("CASHIER_CODE", this.getValueString("CASHIER_CODE"));	//������Ա����  2016/11/15 yanmm
        this
            .openDialog("%ROOT%\\config\\bil\\BILAdjustmentRecipients.x",
                        datat);
        this.onQuery();

    }

    /**
     * ��շ���
     */
    public void onClear() {
        clearValue("RECP_TYPE");
        selectrow = -1;
        callFunction("UI|TABLE2|clearSelection");
        callFunction("UI|TABLE1|clearSelection");
        ((TTable)this.getComponent("TABLE2")).removeRowAll();
        this.onQuery();
    }

    /**
     * ����Ƿ��Ѿ�����״̬
     * @param parm TParm
     * @return boolean
     */
    public boolean checkout(TParm parm) {
        //�ý���Ʊ����Ϊ�������Ʊ�Ƿ�����ʹ����
        parm.setData("CASHIER_CODE", Operator.getID());
//		System.out.println("check parm="+parm);
        //����ʱ��
        TParm result = BILCounteTool.getInstance().CheckCounter(parm);
        if (result.getCount("CASHIER_CODE") > 0)
            return true;
        return false;
    }

    public boolean checkUseNow() {
        return false;
    }
    /**
     * ����EXECL
     */
    public void onExecl(){
        if(tableM.getRowCount()<=0){
            this.messageBox("�޵������ݣ�");
            return;
        }
//        tableD.getShowParmValue();

        ExportExcelUtil.getInstance().exportExcel(tableD,"Ʊ��ʹ����ϸ");

    }
    /**
     * �õ�TABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }

    //    /**
     //    *
     //    * @param args String[]
     //    */
    //   public static void main(String args[]) {
    //       com.javahis.util.JavaHisDebug.TBuilder();
    //       Operator.setData("admin","HIS","127.0.0.1","C00101");
    //       System.out.println("sssssss-->"+Operator.getID());
    //   }

}
