package com.javahis.ui.pha;

import com.dongyang.control.TControl;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import jdo.pha.PhaDecoteTool;
import com.dongyang.ui.TMenuItem;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.data.TNull;
import com.javahis.system.textFormat.TextFormatPHADecoct;
import jdo.util.Manager;
import com.dongyang.ui.TTextFormat;
import jdo.sys.Pat;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: ��ҽ��ҩ</p>
 *
 * <p>Description: ��ҽ��ҩ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.5.17
 * @version 1.0
 */
public class PHADecoteControl
    extends TControl {

    private TTable table_m;

    private TTable table_d;

    public PHADecoteControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ���ż�ס��
        intiAdmType();
        // ��ʼ��¼��Ա��Ȩ��
        intiPopedom();
        // ��ʼ��������
        initPage();
        //========pangben modify 20110511 start Ȩ�����
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110511 stop

    }

    /**
     * ��ʼ��¼��Ա��Ȩ��
     */
    private void intiPopedom() {
        setValue("REGION_CODE", Operator.getRegion());
        setValue("DISPENSE_USER", Operator.getID());
        setValue("ORG_CODE", Operator.getDept());
    }

    /**
     * ��ʼ���ż�ס��
     */
    private void intiAdmType() {
        TCheckBox adm_o = this.getCheckBox("ADM_TYPE_O");
        TCheckBox adm_e = this.getCheckBox("ADM_TYPE_E");
        TCheckBox adm_i = this.getCheckBox("ADM_TYPE_I");
        String type = this.getParameter().toString();
        if ("O".equals(type)) {
            adm_o.setEnabled(true);
            adm_o.setSelected(true);
        }
        else if ("E".equals(type)) {
            adm_e.setEnabled(true);
            adm_e.setSelected(true);
        }
        else if ("I".equals(type)) {
            adm_i.setEnabled(true);
            adm_i.setSelected(true);
        }
        else {
            adm_o.setEnabled(true);
            adm_o.setSelected(true);
            adm_e.setEnabled(true);
            adm_e.setSelected(true);
        }
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        table_m = this.getTable("TABLE_M");
        table_d = this.getTable("TABLE_D");
        Timestamp date = SystemTool.getInstance().getDate();
        setValue("PHARM_DATE",
                 date.toString().substring(0, 10).replace('-', '/'));
        ( (TMenuItem) getComponent("cancel")).setEnabled(false);
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (table_m.getRowCount() <= 0) {
            this.messageBox("û��ִ������");
            return;
        }
        if ("".equals(this.getValueString("DISPENSE_USER"))) {
            this.messageBox("��ѡ����ҩ��Ա");
            return;
        }
        table_m.acceptText();
        TParm parm = table_m.getParmValue();
        for (int i = parm.getCount("SELECT_FLG") - 1; i >= 0; i--) {
            if (!"Y".equals(parm.getValue("SELECT_FLG", i))) {
                parm.removeRow(i);
            }
        }
        if (parm.getCount("SELECT_FLG") <= 0) {
            this.messageBox("û��ִ������");
            return;
        }
        if (this.getRadioButton("TYPE_A").isSelected()) {
            parm.setData("TYPE", "TYPE_C");
            parm.setData("FINAL_TYPE", "O");
        }
//        else if (this.getRadioButton("TYPE_B").isSelected()) {
//            parm.setData("TYPE", "TYPE_B");
//            parm.setData("FINAL_TYPE", "E");
//        }
//        else if (this.getRadioButton("TYPE_C").isSelected()) {
//            parm.setData("TYPE", "TYPE_C");
//            parm.setData("FINAL_TYPE", "O");
//        }
        parm.setData("USER_ID", getValueString("DISPENSE_USER"));
        parm.setData("DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = TIOM_AppServer.executeAction(
            "action.pha.PHADecoteAction",
            "onSave", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("����ʧ��");
            return;
        }
        this.messageBox("����ɹ�");
        table_m.removeRowAll();
        table_d.removeRowAll();
        this.onQuery();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        if (!checkData()) {
            return;
        }
        TParm parm = new TParm();
        // ��ҩʱ��
        String pharm_date = this.getValueString("PHARM_DATE");
        parm.setData("PHARM_DATE",
                     pharm_date.substring(0, 4) + pharm_date.substring(5, 7) +
                     pharm_date.substring(8, 10));
        // ����״̬
        if (this.getRadioButton("TYPE_A").isSelected()) {
            // TYPE_A:���ͼ�ҩ��
            if (this.getRadioButton("FINE_TYPE_A").isSelected()) {
                // ��ҩ
                parm.setData("TYPE_A_1", this.getValueString("FINE_TYPE_A"));
            }
            else {
                // �����ҩ
                parm.setData("TYPE_A_2", this.getValueString("FINE_TYPE_B"));
            }
        }
//        else if (this.getRadioButton("TYPE_B").isSelected()) {
//            // TYPE_B:�����ռ�ҩ
//            parm.setData("TYPE_B", this.getValueString("TYPE_B"));
//        }
//        else if (this.getRadioButton("TYPE_C").isSelected()) {
//            // TYPE_C:���ͷ�ҩҩ��
//            parm.setData("TYPE_C", this.getValueString("TYPE_C"));
//        }
        else {
            // TYPE_D:��ҩ���
            parm.setData("TYPE_D", this.getValueString("TYPE_D"));
        }
        // �ż�ס��
        if (this.getCheckBox("ADM_TYPE_O").isSelected()) {
            // ADM_TYPE_O:����
            parm.setData("ADM_TYPE_O", this.getValueString("ADM_TYPE_O"));
        }
        if (this.getCheckBox("ADM_TYPE_E").isSelected()) {
            // ADM_TYPE_E:����
            parm.setData("ADM_TYPE_E", this.getValueString("ADM_TYPE_E"));
        }
        if (this.getCheckBox("ADM_TYPE_I").isSelected()) {
            // ADM_TYPE_I:סԺ
            parm.setData("ADM_TYPE_I", this.getValueString("ADM_TYPE_I"));
        }
        // ����
        if (!"".equals(this.getValueString("REGION_CODE"))) {
            parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        }
        // ��ҩҩ��
        if (!"".equals(this.getValueString("ORG_CODE"))) {
            parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        }
        // ��ҩ��
        if (!"".equals(this.getValueString("DEPT_CODE"))) {
            parm.setData("DECOCT_CODE", this.getValueString("DEPT_CODE"));
        }
        // ������
        if (!"".equals(this.getValueString("MR_NO"))) {
            parm.setData("MR_NO", this.getValueString("MR_NO"));
        }

        // ��ѯ��ҩ����
        TParm result = PhaDecoteTool.getInstance().onQueryM(parm);
        if (result == null || result.getCount("SELECT_FLG") <= 0) {
            this.messageBox("û�в�ѯ����");
            table_m.removeRowAll();
            table_d.removeRowAll();
            return;
        }
        table_m.setParmValue(result);
    }

    /**
     * ȡ������
     */
    public void onCancel() {
        int row = table_m.getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ��ȡ��������");
        }
        TParm parm = table_m.getParmValue().getRow(row);
//        if (this.getRadioButton("TYPE_B").isSelected()) {
//            parm.setData("TYPE", "TYPE_B");
//            parm.setData("FINAL_TYPE", "F");
//        }
//        else if (this.getRadioButton("TYPE_C").isSelected()) {
//            parm.setData("TYPE", "TYPE_C");
//            parm.setData("FINAL_TYPE", "S");
//        }
//        else
        if (this.getRadioButton("TYPE_D").isSelected()) {
            parm.setData("TYPE", "TYPE_B");
            parm.setData("FINAL_TYPE", "F");
        }
        parm.setData("USER_ID", "");
        TNull tnull = new TNull(Timestamp.class);
        parm.setData("DATE", tnull);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = TIOM_AppServer.executeAction(
            "action.pha.PHADecoteAction",
            "onCancel", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("ȡ��ʧ��");
            return;
        }
        this.messageBox("ȡ���ɹ�");
        table_m.removeRowAll();
        table_d.removeRowAll();
        this.onQuery();
    }

    /**
     * ��ӡ�嵥����
     */
    public void onPrintList() {
        if (table_m.getRowCount() <= 0) {
            this.messageBox("û��ִ������");
            return;
        }
        table_m.acceptText();

        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "�����嵥");
        date.setData("ORG_CODE", "TEXT", "��ҩҩ��: " +
                     this.getTextFormat("ORG_CODE").getText());
        Timestamp datetime = SystemTool.getInstance().getDate();
        date.setData("PHARM_DATE", "TEXT",
                     "Ԥ�Ʒ�ҩʱ��: " +
                     datetime.toString().substring(0, 19).replace('-', '/'));
        date.setData("DECOCT_CODE", "TEXT", "��ҩ�ң�" +
                     this.getTextFormat("DEPT_CODE").getText());
        TParm parm_date = new TParm();
        TParm parm = table_m.getParmValue();
        //System.out.println("parm--"+parm);
        int no = 0;
        for (int i = 0; i < parm.getCount("SELECT_FLG"); i++) {
            if (!"Y".equals(parm.getValue("SELECT_FLG", i))) {
                continue;
            }
            parm_date.addData("NO", no + 1);
            if ("Y".equals(parm.getValue("URGENT_FLG", i))) {
                parm_date.addData("URGENT_DESC", "��");
            }
            else {
                parm_date.addData("URGENT_DESC", "");
            }
            if ("O".equals(parm.getValue("ADM_TYPE", i))) {
                parm_date.addData("ADM_TYPE_DESC", "����");
            }
            else if ("E".equals(parm.getValue("ADM_TYPE", i))) {
                parm_date.addData("ADM_TYPE_DESC", "����");
            }
            else {
                parm_date.addData("ADM_TYPE_DESC", "סԺ");
            }
            parm_date.addData("PRINT_NO", parm.getValue("PRINT_NO", i));
            parm_date.addData("CLINICROOM_DESC",
                              parm.getValue("CLINICROOM_DESC", i));
            parm_date.addData("MR_NO", parm.getValue("MR_NO", i));
            parm_date.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
            parm_date.addData("DCT_TAKE_QTY", parm.getDouble("DCT_TAKE_QTY", i));
            parm_date.addData("FREQ_CHN_DESC", parm.getValue("FREQ_CHN_DESC", i));
            parm_date.addData("TAKE_DAYS", parm.getInt("TAKE_DAYS", i));
            parm_date.addData("PACKAGE_AMT", parm.getInt("PACKAGE_AMT", i));
            parm_date.addData("DECOCT_REMARK", parm.getValue("DECOCT_REMARK", i));
        }
        if (parm_date.getCount("URGENT_DESC") <= 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        date.setData("COUNT", "TEXT",
                     "����������: " + parm_date.getCount("URGENT_DESC"));
        parm_date.setCount(parm_date.getCount("URGENT_DESC"));
        parm_date.addData("SYSTEM", "COLUMNS", "NO");
        parm_date.addData("SYSTEM", "COLUMNS", "URGENT_DESC");
        parm_date.addData("SYSTEM", "COLUMNS", "PRINT_NO");
        parm_date.addData("SYSTEM", "COLUMNS", "ADM_TYPE_DESC");
        parm_date.addData("SYSTEM", "COLUMNS", "CLINICROOM_DESC");
        parm_date.addData("SYSTEM", "COLUMNS", "MR_NO");
        parm_date.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        parm_date.addData("SYSTEM", "COLUMNS", "DCT_TAKE_QTY");
        parm_date.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
        parm_date.addData("SYSTEM", "COLUMNS", "TAKE_DAYS");
        parm_date.addData("SYSTEM", "COLUMNS", "PACKAGE_AMT");
        parm_date.addData("SYSTEM", "COLUMNS", "DECOCT_REMARK");

        date.setData("TABLE", parm_date.getData());
        //System.out.println("date---"+date);
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\PHA\\PHAList.jhw",
                             date);
    }

    /**
     * ��ӡ��ֽ����
     */
    public void onPrintPaster() {
        // ѡ����
        int select_row = table_m.getSelectedRow();
        if (select_row < 0) {
            this.messageBox("��ѡ���ӡ����");
            return;
        }
        // ѡ��������
        TParm selectParmM = table_m.getParmValue().getRow(table_m.
            getSelectedRow());
        TParm selectParmD = table_d.getParmValue();
        String cln_0 = selectParmM.getValue("PAT_NAME");
        String cln_1 = selectParmM.getValue("MR_NO");
        String cln_2 = selectParmD.getValue("DEPT_CHN_DESC", 0);
        String cln_3 = selectParmM.getValue("CLINICROOM_DESC");
        String cln_4 = selectParmM.getValue("RX_NO");
        String cln_5 = selectParmM.getValue("PACKAGE_AMT");
        double cln_6 = 0;
        //System.out.println("selectParmD---" + selectParmD);
        for (int i = 0; i < selectParmD.getCount(); i++) {
            cln_6 += selectParmD.getDouble("MEDI_QTY", i);
        }
        String cln_7_1 = selectParmD.getValue("ROUTE_CHN_DESC", 0);
        double cln_7_2 = selectParmD.getDouble("DCT_TAKE_QTY", 0);
        String cln_8 = this.getValueString("PHARM_DATE").substring(0,
            10).replace("-", "/");
        String cln_9 = selectParmM.getValue("DECOCT_REMARK");
        // ��ӡ����
        int package_amt = selectParmM.getInt("PACKAGE_AMT");

        String[] cln_i = {
            "_0", "_1", "_2", "_3", "_4", "_5", "_6", "_7",
            "_8", "_9", "_10", "_11", "_12", "_13", "_14", "_15"};
        String[] cln_j = {
            "_A", "_B", "_C", "_D", "_E", "_F", "_G"};

        // ��ӡ����ҳ
        int page_count = package_amt / 14;
        // ��ӡ������
        int row_count = package_amt / 7;
        // ��ӡʣ����
        int column_count = package_amt % 7;

        if (page_count >= 1) {
            for (int i = 0; i < page_count; i++) {
                TParm data = new TParm();
                TParm parmData = new TParm();
                for (int row = 0; row < 2; row++) {
                    for (int k = 0; k < 7; k++) {
                        parmData.addData("CLN_0" + cln_j[k],
                                         "" + cln_0);
                        parmData.addData("CLN_1" + cln_j[k],
                                         "" + cln_1);
                        parmData.addData("CLN_2" + cln_j[k],
                                         "" + cln_2);
                        parmData.addData("CLN_3" + cln_j[k],
                                          "  "+cln_3);
                        parmData.addData("CLN_4" + cln_j[k],
                                         "" + cln_4);
                        parmData.addData("CLN_5" + cln_j[k],
                                         "" + cln_5);
                        parmData.addData("CLN_6" + cln_j[k],
                                         "" + cln_7_2 + "ml/��");
                        parmData.addData("CLN_7" + cln_j[k],
                                         "�ڷ�" + cln_7_2 + "ml/��");
                        parmData.addData("CLN_8" + cln_j[k],
                                         "" + cln_8);
                        parmData.addData("CLN_9" + cln_j[k],
                                         "" + cln_9);
                        parmData.addData("CLN_10" + cln_j[k],
                                         "�� �� ��");
                        parmData.addData("CLN_11" + cln_j[k],
                                         "�� �� ��");
                        parmData.addData("CLN_12" + cln_j[k],
                                         "��    ��");
                        parmData.addData("CLN_13" + cln_j[k],
                                         "�÷�����");
                        parmData.addData("CLN_14" + cln_j[k],
                                         "��ҩ����");
                        parmData.addData("CLN_15" + cln_j[k],
                                         "��   ע");
                    }

                }
                for (int j = 0; j < 16; j++) {
                    for (int k = 0; k < 7; k++) {
                        parmData.addData("SYSTEM", "COLUMNS",
                                         "CLN" + cln_i[j] + cln_j[k]);
                    }
                }
                parmData.setCount(2);
                //System.out.println("parmData---" + parmData);
                data.setData("TABLE", parmData.getData());
                this.openPrintWindow("%ROOT%\\config\\prt\\PHA\\PHAPaster.jhw",
                                     data);
                page_count--;
            }
        }
        else {
            TParm data = new TParm();
            TParm parmData = new TParm();
            int count = 0;
            if (row_count % 2 == 1) {
                for (int k = 0; k < 7; k++) {
                    parmData.addData("CLN_0" + cln_j[k],
                                     "" + cln_0);
                    parmData.addData("CLN_1" + cln_j[k],
                                     "" + cln_1);
                    parmData.addData("CLN_2" + cln_j[k],
                                     "" + cln_2);
                    parmData.addData("CLN_3" + cln_j[k],
                                      "  "+cln_3);
                    parmData.addData("CLN_4" + cln_j[k],
                                     "" + cln_4);
                    parmData.addData("CLN_5" + cln_j[k],
                                     "" + cln_5);
                    parmData.addData("CLN_6" + cln_j[k],
                                     "" + cln_7_2 + "ml/��");
                    parmData.addData("CLN_7" + cln_j[k],
                                     "�ڷ�" + cln_7_2 + "ml/��");
                    parmData.addData("CLN_8" + cln_j[k],
                                     "" + cln_8);
                    parmData.addData("CLN_9" + cln_j[k],
                                     "" + cln_9);
                    parmData.addData("CLN_10" + cln_j[k],
                                     "�� �� ��");
                    parmData.addData("CLN_11" + cln_j[k],
                                     "�� �� ��");
                    parmData.addData("CLN_12" + cln_j[k],
                                     "��    ��");
                    parmData.addData("CLN_13" + cln_j[k],
                                     "�÷�����");
                    parmData.addData("CLN_14" + cln_j[k],
                                     "��ҩ����");
                    parmData.addData("CLN_15" + cln_j[k],
                                     "��   ע");
                }
                count++;
            }
            if (column_count > 0) {
                for (int k = 0; k < 7; k++) {
                    if (column_count > k) {
                        parmData.addData("CLN_0" + cln_j[k],
                                         "" + cln_0);
                        parmData.addData("CLN_1" + cln_j[k],
                                         "" + cln_1);
                        parmData.addData("CLN_2" + cln_j[k],
                                         "" + cln_2);
                        parmData.addData("CLN_3" + cln_j[k],
                                          "  "+cln_3);
                        parmData.addData("CLN_4" + cln_j[k],
                                         "" + cln_4);
                        parmData.addData("CLN_5" + cln_j[k],
                                         "" + cln_5);
                        parmData.addData("CLN_6" + cln_j[k],
                                         "" + cln_7_2 + "ml/��");
                        parmData.addData("CLN_7" + cln_j[k],
                                         "�ڷ�" + cln_7_2 + "ml/��");
                        parmData.addData("CLN_8" + cln_j[k],
                                         "" + cln_8);
                        parmData.addData("CLN_9" + cln_j[k],
                                         "" + cln_9);
                        parmData.addData("CLN_10" + cln_j[k],
                                         "�� �� ��");
                        parmData.addData("CLN_11" + cln_j[k],
                                         "�� �� ��");
                        parmData.addData("CLN_12" + cln_j[k],
                                         "��    ��");
                        parmData.addData("CLN_13" + cln_j[k],
                                         "�÷�����");
                        parmData.addData("CLN_14" + cln_j[k],
                                         "��ҩ����");
                        parmData.addData("CLN_15" + cln_j[k],
                                         "��   ע");
                    }
                    else {
                        parmData.addData("CLN_0" + cln_j[k], "");
                        parmData.addData("CLN_1" + cln_j[k], "");
                        parmData.addData("CLN_2" + cln_j[k], "");
                        parmData.addData("CLN_3" + cln_j[k], "");
                        parmData.addData("CLN_4" + cln_j[k], "");
                        parmData.addData("CLN_5" + cln_j[k], "");
                        parmData.addData("CLN_6" + cln_j[k], "");
                        parmData.addData("CLN_7" + cln_j[k], "");
                        parmData.addData("CLN_8" + cln_j[k], "");
                        parmData.addData("CLN_9" + cln_j[k], "");
                        parmData.addData("CLN_10" + cln_j[k],"");
                        parmData.addData("CLN_11" + cln_j[k],"");
                        parmData.addData("CLN_12" + cln_j[k],"");
                        parmData.addData("CLN_13" + cln_j[k],"");
                        parmData.addData("CLN_14" + cln_j[k],"");
                        parmData.addData("CLN_15" + cln_j[k],"");
                    }
                }
                count++;
            }
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 7; k++) {
                    parmData.addData("SYSTEM", "COLUMNS",
                                     "CLN" + cln_i[j] + cln_j[k]);
                }
            }
            parmData.setCount(count);
            //System.out.println("parmData---" + parmData);
            data.setData("TABLE", parmData.getData());
            this.openPrintWindow("%ROOT%\\config\\prt\\PHA\\PHAPaster.jhw",
                                 data);
        }
    }

    /**
     * ��շ���
     */
    public void onClear() {
        this.clearValue("ORG_CODE;DEPT_CODE;SELECT_ALL;DISPENSE_USER;PACKAGE;"
                        + "DCT_TAKE_QTY;FREQ_CODE;ROUTE_CODE;DCTAGENT_CODE");
        getRadioButton("TYPE_A").setSelected(true);
        table_m.removeRowAll();
        table_d.removeRowAll();
        Timestamp date = SystemTool.getInstance().getDate();
        setValue("PHARM_DATE",
                 date.toString().substring(0, 10).replace('-', '/'));
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        ( (TMenuItem) getComponent("cancel")).setEnabled(false);
    }

    /**
     * ȫѡ����
     */
    public void onSelectAll() {
        table_m.acceptText();
        if (table_m.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table_m.getRowCount(); i++) {
                table_m.setItem(i, "SELECT_FLG", "Y");
            }
        }
        else {
            for (int i = 0; i < table_m.getRowCount(); i++) {
                table_m.setItem(i, "SELECT_FLG", "N");
            }
        }
    }

    /**
     * ��ѡ��ť�������
     */
    public void onChangeRadioButton() {
        this.clearValue("SELECT_ALL;DISPENSE_USER;PACKAGE;"
                        + "DCT_TAKE_QTY;FREQ_CODE;ROUTE_CODE;DCTAGENT_CODE");
        table_m.removeRowAll();
        table_d.removeRowAll();
        if (this.getRadioButton("TYPE_A").isSelected()) {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            ( (TMenuItem) getComponent("cancel")).setEnabled(false);
            getRadioButton("FINE_TYPE_A").setEnabled(true);
            getRadioButton("FINE_TYPE_B").setEnabled(true);
        }
//        else if (this.getRadioButton("TYPE_B").isSelected()) {
//            ( (TMenuItem) getComponent("save")).setEnabled(true);
//            ( (TMenuItem) getComponent("cancel")).setEnabled(true);
//            getRadioButton("FINE_TYPE_A").setEnabled(false);
//            getRadioButton("FINE_TYPE_B").setEnabled(false);
//        }
//        else if (this.getRadioButton("TYPE_C").isSelected()) {
//            ( (TMenuItem) getComponent("save")).setEnabled(true);
//            ( (TMenuItem) getComponent("cancel")).setEnabled(true);
//            getRadioButton("FINE_TYPE_A").setEnabled(false);
//            getRadioButton("FINE_TYPE_B").setEnabled(false);
//        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(false);
            ( (TMenuItem) getComponent("cancel")).setEnabled(true);
            getRadioButton("FINE_TYPE_A").setEnabled(false);
            getRadioButton("FINE_TYPE_B").setEnabled(false);
        }
    }

    /**
     * ҩ������¼�
     */
    public void onChangeOrgCode() {
        this.setValue("DEPT_CODE", "");
        String org_code = this.getValueString("ORG_CODE");
        if (!"".equals(org_code)) {
            TextFormatPHADecoct decoct_code = (TextFormatPHADecoct)this.
                getComponent("DEPT_CODE");
            decoct_code.setOrgCode(org_code);
            decoct_code.onQuery();
        }
    }

    /**
     * �����Żس��¼�
     */
    public void onMrNoAction() {
        String mr_no = this.getValueString("MR_NO");
        Pat pat = Pat.onQueryByMrNo(mr_no);
        if (pat == null) {
            this.messageBox("û�в�ѯ����");
            this.setValue("MR_NO", "");
            return;
        }
        mr_no = pat.getMrNo();
        this.setValue("MR_NO", mr_no);
    }

    /**
     * ���ݼ��
     * @return boolean
     */
    private boolean checkData() {
        // ����
        if ("".equals(this.getValueString("REGION_CODE"))) {
            this.messageBox("��ѡ������");
            return false;
        }
        // ��ҩҩ��
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("��ѡ��ҩ��");
            return false;
        }
        // ��ҩ��
        if ("".equals(this.getValueString("DEPT_CODE"))) {
            this.messageBox("��ѡ���ҩ��");
            return false;
        }
        return true;
    }

    /**
     * TABLEM�����¼�
     */
    public void onTableMClicked() {
        TParm parm = table_m.getParmValue().getRow(table_m.getSelectedRow());
        //=============pangben modify 20110512 start ��Ӳ���
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            parm.setData("REGION_CODE", Operator.getRegion());
         //=============pangben modify 20110512 stop
        TParm result = PhaDecoteTool.getInstance().onQueryD(parm);
        if (result == null || result.getCount("ORDER_CODE") <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        table_d.setParmValue(result);
    }

    /**
     * TABLED�����¼�
     */
    public void onTableDClicked() {
        TParm parm = table_d.getParmValue().getRow(table_d.getSelectedRow());
        this.setValue("PACKAGE", parm.getDouble("TAKE_DAYS"));
        this.setValue("DCT_TAKE_QTY", parm.getDouble("DCT_TAKE_QTY"));
        this.setValue("FREQ_CODE", parm.getValue("FREQ_CODE"));
        this.setValue("ROUTE_CODE", parm.getValue("ROUTE_CODE"));
        this.setValue("DCTAGENT_CODE", parm.getValue("DCTAGENT_CODE"));
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

    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
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
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

}
