package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import jdo.inv.InvSupRequestMTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.inv.INVSQL;
import com.dongyang.ui.TCheckBox;

/**
 * <p>Title: ��Ӧ�ҳ����������쵥</p>
 *
 * <p>Description: ��Ӧ�ҳ����������쵥</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.3.8
 * @version 1.0
 */
public class INVSuprequestChooseControl extends TControl{

    // ��������
    private TTable tableM;
    // �Ĳĳ���
    private TTable tableD;
    // ���ⷽʽ
    private String pack_mode = "0";


    public INVSuprequestChooseControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        tableM = getTable("TABLEM");
        tableD = getTable("TABLED");

        // ��������
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setData("START_DATE", this.getValue("START_DATE"));
        parm.setData("END_DATE", this.getValue("END_DATE"));
        parm.setData("UPDATE_FLG_B", "Y");
        if (!"".equals(this.getValueString("SUPTYPE_CODE"))) {
            parm.setData("SUPTYPE_CODE", getValueString("SUPTYPE_CODE"));
        }
        if (!"".equals(this.getValueString("REQUEST_NO"))) {
            parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
        }
        if (!"".equals(this.getValueString("TO_ORG_CODE"))) {
            parm.setData("TO_ORG_CODE", getValueString("TO_ORG_CODE"));
        }
        if (!"".equals(this.getValueString("APP_ORG_CODE"))) {
            parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        }
        TParm result = InvSupRequestMTool.getInstance().onQuery(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        tableM.setParmValue(result);
    }

    /**
     * ���ط���
     */
    public void onReturn() {
        tableD.acceptText();
        boolean flg = true;
        for (int i = 0; i < tableD.getRowCount(); i++) {
            if ("Y".equals(tableD.getItemString(i, "SELECT_FLG"))) {
                flg = false;
            }
        }
        if (flg) {
            this.messageBox("û�лش�����");
            return;
        }
        TParm result = new TParm();
        result.setData("REQUEST_M",
                       tableM.getParmValue().getRow(tableM.getSelectedRow()).
                       getData());
        TParm tableDParm = tableD.getParmValue();
        for (int i = tableDParm.getCount("INV_CODE") - 1; i >= 0; i--) {
            if (!"Y".equals(tableDParm.getValue("SELECT_FLG", i))) {
                tableDParm.removeRow(i);
            }
        }
        result.setData("REQUEST_D", tableDParm.getData());
        result.setData("PACK_MODE", pack_mode);
        //System.out.println("result==="+result);
        setReturnValue(result);
        this.closeWindow();
    }

    /**
     * ��շ���
     */
    public void onClear() {
        this.clearValue(
            "SUPTYPE_CODE;APP_ORG_CODE;REQUEST_NO;TO_ORG_CODE;SELECT_ALL");
        // ��������
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        tableM.removeRowAll();
        tableD.removeRowAll();
    }

    /**
     * ��񵥻��¼�
     */
    public void onTableMClicked() {
        String request_no = tableM.getItemString(tableM.getSelectedRow(),
                                                 "REQUEST_NO");
        String suptype_code = tableM.getItemString(tableM.getSelectedRow(),
                                                   "SUPTYPE_CODE");
        TParm pack_mode_parm = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getINVSupType(suptype_code)));
        pack_mode = pack_mode_parm.getValue("PACK_MODE", 0);
        TParm result = new TParm();
        if ("0".equals(pack_mode)) {
            result = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvSupRequestDInv(request_no)));
        }
        else {
            result = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvSupRequestDPack(request_no)));
        }
        tableD.setParmValue(result);
    }
    /**
     * ȫѡ�¼�
     */
    public void onSelectAll() {
        tableD.acceptText();
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < tableD.getRowCount(); i++) {
                tableD.setItem(i, "SELECT_FLG", true);
            }
        }
        else {
            for (int i = 0; i < tableD.getRowCount(); i++) {
                tableD.setItem(i, "SELECT_FLG", false);
            }
        }
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

    /**
     * �õ�CheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

}
