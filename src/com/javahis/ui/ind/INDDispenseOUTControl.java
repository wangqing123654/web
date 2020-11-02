package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import jdo.ind.INDSQL;
import jdo.ind.INDTool;
import jdo.ind.IndSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import jdo.util.Manager;
import jdo.sys.SYSSQL;
import com.dongyang.ui.TTextFormat;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: �������
 * </p>
 *
 * <p>
 * Description: �������
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.05.25
 * @version 1.0
 */
public class INDDispenseOUTControl
    extends TControl {

    // �������
    private String action;

    // ������
    private TTable table_m;

    // ϸ����
    private TTable table_d;

    // ������ѡ����
    private int row_m;

    // ϸ����ѡ����
    private int row_d;

    // ϸ�����
    private int seq;

    // ���뵥��
    private String request_no;

    // ���벿��
    private String app_org;

    // ��������
    private String request_type;

    // ���ؽ����
    private TParm resultParm;

    // ҳ���Ϸ������б�
    private String[] pageItem = {
        "REQTYPE_CODE", "REQUEST_NO", "APP_ORG_CODE",
        "TO_ORG_CODE", "REASON_CHN_DESC", "REQUEST_DATE", "DISPENSE_NO",
        "DESCRIPTION", "URGENT_FLG"};

    // ʹ�õ�λ
    private String u_type;

    // ���ⲿ��
    private String out_org_code;

    // ��ⲿ��
    private String in_org_code;

    // �Ƿ����
    private boolean out_flg;

    // �Ƿ����
    private boolean in_flg;

    // ���ⵥ��
    private String dispense_no;

    // ȫԺҩ�ⲿ����ҵ����
    private boolean request_all_flg = true;

    java.text.DecimalFormat df4 = new java.text.DecimalFormat(
        "##########0.0000");

    public INDDispenseOUTControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��������¼�
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // ��TABLEDEPT�е�CHECKBOX��������¼�
        callFunction("UI|TABLE_D|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        // ��ʼ��������
        initPage();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
                                              "onQueryOutM", onQueryParm());
        // ȫԺҩ�ⲿ����ҵ����
        if (!request_all_flg) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(SYSSQL.
                getOperatorDept(Operator.getID())));
            String dept_code = "";
            for (int i = result.getCount("REQTYPE_CODE") - 1; i >= 0; i--) {
                boolean flg = true;
                for (int j = 0; j < parm.getCount("DEPT_CODE"); j++) {
                    dept_code = parm.getValue("DEPT_CODE", j);
                    if ("DEP".equals(result.getValue("REQTYPE_CODE", i)) ||
                        "TEC".equals(result.getValue("REQTYPE_CODE", i)) ||
                        "THI".equals(result.getValue("REQTYPE_CODE", i))) {
                        if (dept_code.equals(result.getValue("TO_ORG_CODE", i))) {
                            flg = false;
                            break;
                        }
                        else {
                            flg = true;
                        }
                    }
                    else if ("GIF".equals(result.getValue("REQTYPE_CODE", i)) ||
                             "RET".equals(result.getValue("REQTYPE_CODE", i))) {
                        if (dept_code.equals(result.getValue("APP_ORG_CODE", i))) {
                            flg = false;
                            break;
                        }
                        else {
                            flg = true;
                        }
                    }
                }
                if (flg) {
                    result.removeRow(i);
                }
            }
        }
        if (result.getCount() <= 0 || result.getCount("REQUEST_NO") == 0) {
            this.messageBox("�޲�ѯ���");
            return;
        }
        table_m.setParmValue(result);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        getRadioButton("UPDATE_FLG_B").setSelected(true);
        // ��ջ�������
        String clearString =
            "REQUEST_NO;APP_ORG_CODE;REQTYPE_CODE;DISPENSE_NO;"
            + "REQUEST_DATE;TO_ORG_CODE;REASON_CHN_DESC;START_DATE;END_DATE;"
            + "DESCRIPTION;URGENT_FLG;SELECT_ALL;"
            + "SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;PRICE_DIFFERENCE";
        clearValue(clearString);
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("DISPENSE_DATE", date);
        // ��ʼ��ҳ��״̬������
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();
        row_m = -1;
        row_d = -1;
        getTextField("REQUEST_NO").setEnabled(true);
        getTextFormat("APP_ORG_CODE").setEnabled(true);
        getComboBox("REQTYPE_CODE").setEnabled(true);
        ( (TMenuItem) getComponent("stop")).setEnabled(true);
        ( (TMenuItem) getComponent("cancle")).setEnabled(false);
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        resultParm = new TParm();
        seq = 1;
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            /** ��������(���ⵥ���ϸ��) */
            // ҩ�������Ϣ
            TParm sysParm = getSysParm();
            // �������ҵ״̬�ж�(1-���ȷ�ϣ�2-���⼴���)
            String dis_check = getDisCheckFlg(sysParm);
            // �Ƿ��д����۸�
            String reuprice_flg = sysParm.getValue("REUPRICE_FLG", 0);
            if ("1".equals(dis_check)) {
                // ���ⲿ�ſ���Ƿ���춯
                if (!getOrgBatchFlg(out_org_code)) {
                    this.messageBox("���ⲿ�Ŵ����̵�״̬����治���춯");
                    return;
                }
                // �ж��Ƿ��п�棬����û�п���ҩƷȡ����ѡ
                String message = checkStockQty();
                if (!"".equals(message) && message.length() > 0) {
                    this.messageBox(message);
                }
                // ������;��ҵ/��������������ҵ(���⼴���)
                if (getDispenseOutOn(out_org_code)) {
                    // ��ӡ���ⵥ
                    this.onPrint();
                    this.onClear();
                }
            }
            else if ("2".equals(dis_check)) {
                // ���ⲿ�ſ���Ƿ���춯
                if (!getOrgBatchFlg(out_org_code)) {
                    this.messageBox("���ⲿ�Ŵ����̵�״̬����治���춯");
                    return;
                }
                // ��ⲿ�ſ���Ƿ���춯
                if (!"".equals(in_org_code) && !getOrgBatchFlg(in_org_code)) {
                    this.messageBox("��ⲿ�Ŵ����̵�״̬����治���춯");
                    return;
                }
                // �ж��Ƿ��п�棬����û�п���ҩƷȡ����ѡ
                String message = checkStockQty();
                if (!"".equals(message) && message.length() > 0) {
                    this.messageBox(message);
                }
                // ���⼴�����ҵ(����ⲿ�ž���Ϊ��)
                getDispenseOutIn(out_org_code, in_org_code, reuprice_flg,
                                 out_flg, in_flg);
            }
        }
        else if (getRadioButton("UPDATE_FLG_C").isSelected()) {
            /** ��;���� */
            if (row_m != -1) {
                // ��������
                getUpdateDispenseMOutOn();
            }
            else {
                // ����ϸ��
                this.messageBox("���뵥�ѳ��⣬�����޸�");
            }
        }
        else {
            /** ��ɸ��� */
            this.messageBox("��������ɣ������޸�");
        }
    }

    /**
     * ȡ�����ⷽ��
     */
    public void onCancle() {
        if (table_m.getSelectedRow() < 0) {
            this.messageBox("û��ȡ������");
            return;
        }
        if (this.messageBox("��ʾ", "�Ƿ�ȡ������", 2) == 0) {
            String dispense_no = this.getValueString("DISPENSE_NO");
            String request_no = this.getValueString("REQUEST_NO");
            TParm parm = new TParm();
            parm.setData("REQUEST_NO", request_no);
            parm.setData("DISPENSE_NO", dispense_no);
            parm.setData("ORG_CODE", out_org_code);
            parm.setData("REQTYPE_CODE", this.getValueString("REQTYPE_CODE"));
            parm.setData("UPDATE_FLG", "2");
            Timestamp date = SystemTool.getInstance().getDate();
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            parm.setData("PARM_D", table_d.getParmValue().getData());
            TParm result = TIOM_AppServer.executeAction(
                "action.ind.INDDispenseAction", "onUpdateDipenseCancel", parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("ȡ��ʧ��");
                return;
            }
            this.messageBox("ȡ���ɹ�");
            this.onClear();
        }
    }

    /**
     * ��ֹ���ݷ���
     */
    public void onStop() {
        if (table_m.getSelectedRow() < 0) {
            this.messageBox("û����ֹ����");
            return;
        }
        if (this.messageBox("��ʾ", "�Ƿ���ֹ����", 2) == 0) {
            String request_no = this.getValueString("REQUEST_NO");
            TParm parm = new TParm();
            parm.setData("REQUEST_NO", request_no);
            parm.setData("UPDATE_FLG", "2");
            Timestamp date = SystemTool.getInstance().getDate();
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            TParm result = TIOM_AppServer.executeAction(
                "action.ind.INDRequestAction", "onUpdateFlg", parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("��ֹʧ��");
                return;
            }
            this.messageBox("��ֹ�ɹ�");
            this.onClear();
        }
    }

    /**
     * ��ӡ���ⵥ
     */
    public void onPrint() {
        Timestamp datetime = SystemTool.getInstance().getDate();
        TTable table_d = getTable("TABLE_D");
        if ("".equals(this.getValueString("DISPENSE_NO"))) {
            this.messageBox("�����ڳ��ⵥ");
            return;
        }
        if (table_d.getRowCount() > 0) {
            // ��ӡ����
            TParm date = new TParm();
            // ��ͷ����
            date.setData("TITLE", "TEXT", Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "���ⵥ");
            //===============pangben modify 20110607 ��Ӽ���ע��
            if (null !=
                getValue("URGENT_FLG") && getValue("URGENT_FLG").equals("Y"))
                date.setData("URGENT", "TEXT", "��");
            else
                date.setData("URGENT", "TEXT", "");
            //===============pangben modify 20110607 stop
            date.setData("DISP_NO", "TEXT",
                         "���ⵥ��: " + this.getValueString("DISPENSE_NO"));
            date.setData("REQ_NO", "TEXT",
                         "���뵥��: " + this.getValueString("REQUEST_NO"));
            date.setData("OUT_DATE", "TEXT",
                         "��������: " + this.getValueString("DISPENSE_DATE").
                         substring(0, 10).replace('-', '/'));
            date.setData("REQ_TYPE", "TEXT", "�������: " +
                         this.getComboBox("REQTYPE_CODE").getSelectedName());
            date.setData("ORG_CODE_APP", "TEXT", "���벿��: " +
                         this.getTextFormat("APP_ORG_CODE").getText());
            date.setData("ORG_CODE_FROM", "TEXT", "���ܲ���: " +
                         this.getComboBox("TO_ORG_CODE").getSelectedName());
            date.setData("DATE", "TEXT",
                         "�Ʊ�����: " +
                         datetime.toString().substring(0, 10).replace('-', '/'));

            // �������
            TParm parm = new TParm();
            String order_code = "";
            String order_desc = "";
            double qty = 0;
            double sum_retail_price = 0;
            //luhai 2012-1-22 ����ɹ����ܺ�
            double sum_verifyin_price = 0;
//            if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            	//*********************************************
            	//luhai 2012-1-22 modify �������ɹ��� begin
            	//*********************************************
//                String sql =
//                    "SELECT A.ORDER_CODE, CASE WHEN B.GOODS_DESC IS NULL "
//                    + " THEN B.ORDER_DESC ELSE B.ORDER_DESC || '(' || "
//                    + " B.GOODS_DESC || ')' END AS ORDER_DESC, "
//                    + " B.SPECIFICATION, C.UNIT_CHN_DESC, A.RETAIL_PRICE, "
//                    + " A.QTY AS OUT_QTY, A.BATCH_NO, A.VALID_DATE "
//                    + " FROM IND_DISPENSED A, SYS_FEE B, SYS_UNIT C "
//                    + " WHERE A.ORDER_CODE = B.ORDER_CODE "
//                    + " AND A.UNIT_CODE = C.UNIT_CODE "
//                    + " AND A.DISPENSE_NO = '" +
//                    this.getValueString("DISPENSE_NO") + "' "
//                    + " ORDER BY A.SEQ_NO";
//                TParm printData = new TParm(TJDODBTool.getInstance().select(sql));
//                for (int i = 0; i < printData.getCount("ORDER_CODE"); i++) {
//                    parm.addData("ORDER_DESC",
//                                 printData.getValue("ORDER_DESC", i));
//                    parm.addData("SPECIFICATION",
//                                 printData.getValue("SPECIFICATION", i));
//                    parm.addData("UNIT", printData.getValue("UNIT_CHN_DESC", i));
//                    parm.addData("UNIT_PRICE",
//                                 df4.format(printData.getDouble("RETAIL_PRICE",
//                        i)));
//                    qty = printData.getDouble("OUT_QTY", i);
//                    parm.addData("QTY", qty);
//                    parm.addData("AMT", StringTool.round(printData.getDouble(
//                        "RETAIL_PRICE", i) * qty, 2));
//                    sum_retail_price += printData.getDouble("RETAIL_PRICE", i) *
//                        qty;
//                    parm.addData("BATCH_NO", printData.getValue("BATCH_NO", i));
//                    parm.addData("VALID_DATE", StringTool.getString(printData.
//                        getTimestamp("VALID_DATE", i), "yyyy/MM/dd"));
//                }
//            }
//            else {
//                for (int i = 0; i < table_d.getRowCount(); i++) {
//                    if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
//                        continue;
//                    }
//                    order_code = table_d.getParmValue().getValue("ORDER_CODE",
//                        i);
//                    TParm inparm = new TParm(TJDODBTool.getInstance().select(
//                        INDSQL.
//                        getOrderInfoByCode(order_code, u_type)));
//                    if (inparm == null || inparm.getErrCode() < 0) {
//                        this.messageBox("ҩƷ��Ϣ����");
//                        return;
//                    }
//                    if ("".equals(inparm.getValue("GOODS_DESC", 0))) {
//                        order_desc = inparm.getValue("ORDER_DESC", 0);
//                    }
//                    else {
//                        order_desc = inparm.getValue("ORDER_DESC", 0) + "(" +
//                            inparm.getValue("GOODS_DESC", 0) + ")";
//                    }
//                    parm.addData("ORDER_DESC", order_desc);
//                    parm.addData("SPECIFICATION",
//                                 inparm.getValue("SPECIFICATION", 0));
//                    parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", 0));
//                    parm.addData("UNIT_PRICE",
//                                 df4.format(table_d.getItemDouble(i,
//                        "RETAIL_PRICE")));
//                    parm.addData("QTY", table_d.getItemDouble(i, "OUT_QTY"));
//                    qty = table_d.getItemDouble(i, "OUT_QTY");
//                    parm.addData("AMT",
//                                 StringTool.round(table_d.getItemDouble(i,
//                        "RETAIL_PRICE") * qty, 2));
//                    sum_retail_price += table_d.getItemDouble(i,
//                        "RETAIL_PRICE") * qty;
//                    parm.addData("BATCH_NO",
//                                 table_d.getItemString(i, "BATCH_NO"));
//                    parm.addData("VALID_DATE", StringTool.getString(table_d.
//                        getItemTimestamp(i, "VALID_DATE"),
//                        "yyyy/MM/dd"));
//                }
//            }
            //luhai modify ���ⵥɾ����Ʒ�� begin 
//                String sql =
//                    "SELECT A.ORDER_CODE, CASE WHEN B.GOODS_DESC IS NULL "
//                    + " THEN B.ORDER_DESC ELSE B.ORDER_DESC || '(' || "
//                    + " B.GOODS_DESC || ')' END AS ORDER_DESC, "
//                    + " B.SPECIFICATION, C.UNIT_CHN_DESC, A.RETAIL_PRICE, "
//                    + " A.QTY AS OUT_QTY, A.BATCH_NO, A.VALID_DATE,A.VERIFYIN_PRICE "//ADD VERIFYIN_PRICE
//                    + " FROM IND_DISPENSED A, SYS_FEE B, SYS_UNIT C "
//                    + " WHERE A.ORDER_CODE = B.ORDER_CODE "
//                    + " AND A.UNIT_CODE = C.UNIT_CODE "
//                    + " AND A.DISPENSE_NO = '" +
//                    this.getValueString("DISPENSE_NO") + "' "
//                    + " ORDER BY A.SEQ_NO";
                String sql =
                    "SELECT A.ORDER_CODE, CASE WHEN B.GOODS_DESC IS NULL "
                    + " THEN B.ORDER_DESC ELSE B.ORDER_DESC  "
                    + "  END AS ORDER_DESC, "
                    + " B.SPECIFICATION, C.UNIT_CHN_DESC, A.RETAIL_PRICE, "
                    + " A.QTY AS OUT_QTY, A.BATCH_NO, A.VALID_DATE,A.VERIFYIN_PRICE "//ADD VERIFYIN_PRICE
                    + " FROM IND_DISPENSED A, SYS_FEE B, SYS_UNIT C "
                    + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                    + " AND A.UNIT_CODE = C.UNIT_CODE "
                    + " AND A.DISPENSE_NO = '" +
                    this.getValueString("DISPENSE_NO") + "' "
                    + " ORDER BY A.SEQ_NO";
              //luhai modify ���ⵥɾ����Ʒ�� begin 
                TParm printData = new TParm(TJDODBTool.getInstance().select(sql));
                for (int i = 0; i < printData.getCount("ORDER_CODE"); i++) {
                    parm.addData("ORDER_DESC",
                                 printData.getValue("ORDER_DESC", i));
                    parm.addData("SPECIFICATION",
                                 printData.getValue("SPECIFICATION", i));
                    parm.addData("UNIT", printData.getValue("UNIT_CHN_DESC", i));
                    parm.addData("UNIT_PRICE",
                                 df4.format(printData.getDouble("RETAIL_PRICE",
                        i)));
                    parm.addData("VERIFYIN_PRICE",
                    		df4.format(printData.getDouble("VERIFYIN_PRICE",
                    				i)));
                    qty = printData.getDouble("OUT_QTY", i);
                    parm.addData("QTY", qty);
                    parm.addData("AMT", StringTool.round(printData.getDouble(
                        "RETAIL_PRICE", i) * qty, 2));
                    parm.addData("AMT_VERIFYIN", StringTool.round(printData.getDouble(
                    		"VERIFYIN_PRICE", i) * qty, 2));
                    sum_retail_price += printData.getDouble("RETAIL_PRICE", i) *
                        qty;
                    sum_verifyin_price += printData.getDouble("VERIFYIN_PRICE", i) *
                    qty;
                    parm.addData("BATCH_NO", printData.getValue("BATCH_NO", i));
                    parm.addData("VALID_DATE", StringTool.getString(printData.
                        getTimestamp("VALID_DATE", i), "yyyy/MM/dd"));
                }
//            }
//            else {
//                for (int i = 0; i < table_d.getRowCount(); i++) {
//                    if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
//                        continue;
//                    }
//                    order_code = table_d.getParmValue().getValue("ORDER_CODE",
//                        i);
//                    TParm inparm = new TParm(TJDODBTool.getInstance().select(
//                        INDSQL.
//                        getOrderInfoByCode(order_code, u_type)));
//                    if (inparm == null || inparm.getErrCode() < 0) {
//                        this.messageBox("ҩƷ��Ϣ����");
//                        return;
//                    }
//                    if ("".equals(inparm.getValue("GOODS_DESC", 0))) {
//                        order_desc = inparm.getValue("ORDER_DESC", 0);
//                    }
//                    else {
//                        order_desc = inparm.getValue("ORDER_DESC", 0) + "(" +
//                            inparm.getValue("GOODS_DESC", 0) + ")";
//                    }
//                    parm.addData("ORDER_DESC", order_desc);
//                    parm.addData("SPECIFICATION",
//                                 inparm.getValue("SPECIFICATION", 0));
//                    parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", 0));
//                    parm.addData("UNIT_PRICE",
//                                 df4.format(table_d.getItemDouble(i,
//                        "RETAIL_PRICE")));
//                    parm.addData("QTY", table_d.getItemDouble(i, "OUT_QTY"));
//                    qty = table_d.getItemDouble(i, "OUT_QTY");
//                    parm.addData("AMT",
//                                 StringTool.round(table_d.getItemDouble(i,
//                        "RETAIL_PRICE") * qty, 2));
//                    sum_retail_price += table_d.getItemDouble(i,
//                        "RETAIL_PRICE") * qty;
//                    parm.addData("BATCH_NO",
//                                 table_d.getItemString(i, "BATCH_NO"));
//                    parm.addData("VALID_DATE", StringTool.getString(table_d.
//                        getItemTimestamp(i, "VALID_DATE"),
//                        "yyyy/MM/dd"));
//                }
//            }
            
            //luhai 2012-1-22 modify end
            if (parm.getCount("ORDER_DESC") <= 0) {
                this.messageBox("û�д�ӡ����");
                return;
            }
            //luhai 2012-1-22 modify ����ɹ����ɹ����� begin
            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "UNIT");
            parm.addData("SYSTEM", "COLUMNS", "QTY");
            parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "AMT_VERIFYIN");
            parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "AMT");
            parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
            //luhai delte validDate 2012-2-11
//            parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
            //luhai 2012-1-22 modify ����ɹ����ɹ����� end
            //System.out.println("PARM---" + parm);
            date.setData("TABLE", parm.getData());

            // ��β����
            //luhai 2012-1-22 ����ϼ�atm
            String atm = StringTool.round(sum_retail_price, 2)+"";
            String verifyinAtm=StringTool.round(sum_verifyin_price, 2)+"";
//            date.setData("TOT_CHN", "TEXT",
//                         "�ϼ�(��д): " + StringUtil.getInstance().numberToWord(atm));
            date.setData("ATM", "TEXT",atm);
            date.setData("VERIFYIN_ATM", "TEXT",verifyinAtm);
            date.setData("VERIFYIN_ATM_DESC", "TEXT",StringUtil.getInstance().numberToWord(Double.parseDouble(verifyinAtm)));
            date.setData("TOT", "TEXT", "�ϼ�: ");
            date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
            // ���ô�ӡ����
            this.openPrintWindow("%ROOT%\\config\\prt\\IND\\DispenseOut.jhw",
                                 date);
        }
        else {
            this.messageBox("û�д�ӡ����");
            return;
        }
    }

    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        row_m = table_m.getSelectedRow();
        if (row_m != -1) {
            // ������ѡ���������Ϸ�
            getTableSelectValue(table_m, row_m, pageItem);
            // ����ʱ��
            if (table_m.getItemData(row_m, "DISPENSE_DATE") != null) {
                this.setValue("DISPENSE_DATE", table_m.getItemData(row_m,
                    "DISPENSE_DATE"));
            }
            // �趨ҳ��״̬
            getComboBox("REQTYPE_CODE").setEnabled(false);
            getTextFormat("APP_ORG_CODE").setEnabled(false);
            getTextField("REQUEST_NO").setEnabled(false);
            // �������
            request_type = getValueString("REQTYPE_CODE");
            // ���뵥��
            request_no = getValueString("REQUEST_NO");
            // ���ⵥ��
            dispense_no = getValueString("DISPENSE_NO");
            if (getRadioButton("UPDATE_FLG_B").isSelected()) {
                // ��ϸ��Ϣ
                getTableDInfo(request_no);
            }
            else {
                // ��ϸ��Ϣ
                getTableDInfo2(dispense_no);
            }
            table_d.setSelectionMode(0);
            row_d = -1;

            setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
            setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
            setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
                - getSumRegMoney(), 2));
        }
    }

    /**
     * ��ϸ���(TABLE_D)�����¼�
     */
    public void onTableDClicked() {
        row_d = table_d.getSelectedRow();
        if (row_d != -1) {
            table_m.setSelectionMode(0);
            row_m = -1;
            // ȡ��SYS_FEE��Ϣ��������״̬����
            /*
             String order_code = table_d.getParmValue().getValue("ORDER_CODE",
                table_d.getSelectedRow());
                         if (!"".equals(order_code)) {
                this.setSysStatus(order_code);
                         }
                         else {
                callFunction("UI|setSysStatus", "");
                         }
             */
        }
    }

    /**
     * ȫѡ��ѡ��ѡ���¼�
     */
    public void onCheckSelectAll() {
        table_d.acceptText();
        if (table_d.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table_d.getRowCount(); i++) {
                if (!CheckDataD()) {
                    getCheckBox("SELECT_ALL").setSelected(false);
                    return;
                }
                table_d.setItem(i, "SELECT_FLG", true);
            }
            setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
            setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
            setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
                - getSumRegMoney(), 2));
        }
        else {
            for (int i = 0; i < table_d.getRowCount(); i++) {
                table_d.setItem(i, "SELECT_FLG", false);
            }
            setValue("SUM_RETAIL_PRICE", 0);
            setValue("SUM_VERIFYIN_PRICE", 0);
            setValue("PRICE_DIFFERENCE", 0);
        }
    }

    /**
     * ���ֵ�ı��¼�
     *
     * @param obj
     *            Object
     */
    public boolean onTableDChangeValue(Object obj) {
        // ֵ�ı�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // �ж����ݸı�
        if (node.getValue().equals(node.getOldValue()))
            return true;
        int column = node.getColumn();
        if (column == 6) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("��������������С�ڻ����0");
                return true;
            }
            double stock_qty = table_d.getItemDouble(row_d, "STOCK_QTY");
            if (qty > stock_qty) {
                this.messageBox("�������������ܴ��ڿ������");
                return true;
            }
            double dep_qty = table_d.getItemDouble(row_d, "QTY");
            if (qty > dep_qty) {
                this.messageBox("�������������ܴ�����������");
                return true;
            }
            double amt1 = StringTool.round(qty * table_d.getItemDouble(row_d,
                "STOCK_PRICE"), 2);
            double amt2 = StringTool.round(qty * table_d.getItemDouble(row_d,
                "RETAIL_PRICE"), 2);
            table_d.setItem(row_d, "STOCK_ATM", amt1);
            table_d.setItem(row_d, "RETAIL_ATM", amt2);
            table_d.setItem(row_d, "DIFF_ATM", amt2 - amt1);
            return false;
        }
        if (column == 8) {
            double price = TypeTool.getDouble(node.getValue());
            if (price <= 0) {
                this.messageBox("�ɱ��۲���С�ڻ����0");
                return true;
            }
            double out_qty = table_d.getItemDouble(row_d, "OUT_QTY");
            table_d.setItem(row_d, "STOCK_ATM", StringTool.round(price
                * out_qty, 2));
            double atm = table_d.getItemDouble(row_d, "RETAIL_ATM");
            table_d.setItem(row_d, "DIFF_ATM", StringTool.round(atm - price
                * out_qty, 2));
            return false;
        }
        return true;
    }

    /**
     * ���(TABLE)��ѡ��ı��¼�
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        table_d.acceptText();
        // ���ѡ�е���
        int column = table_d.getSelectedColumn();
        if (column == 0) {
            if ("Y".equals(table_d.getItemString(row_d, column))) {
                table_d.setItem(row_d, "SELECT_FLG", false);
            }
            else {
                table_d.setItem(row_d, "SELECT_FLG", true);
            }
            setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
            setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
            setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
                - getSumRegMoney(), 2));
        }
    }

    /**
     * ����״̬�ı��¼�
     */
    public void onChangeSelectAction() {
        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            onClear();
            getRadioButton("UPDATE_FLG_A").setSelected(true);
            getTextField("DISPENSE_NO").setEnabled(true);
            ( (TMenuItem) getComponent("cancle")).setEnabled(false);
            ( (TMenuItem) getComponent("stop")).setEnabled(false);
            ( (TMenuItem) getComponent("save")).setEnabled(false);
        }
        else if (getRadioButton("UPDATE_FLG_C").isSelected()) {
            onClear();
            getRadioButton("UPDATE_FLG_C").setSelected(true);
            getTextField("DISPENSE_NO").setEnabled(true);
            ( (TMenuItem) getComponent("cancle")).setEnabled(true);
            ( (TMenuItem) getComponent("stop")).setEnabled(false);
            ( (TMenuItem) getComponent("save")).setEnabled(false);
        }
        else {
            onClear();
            getRadioButton("UPDATE_FLG_C").setSelected(false);
            ( (TMenuItem) getComponent("save")).setEnabled(true);
        }
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        if (!this.getPopedem("requestAll")) {
            request_all_flg = false;
        }

        // ��ʼ������ʱ��
        // ��������
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("DISPENSE_DATE", date);
        // ��ʼ��TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        row_m = -1;
        row_d = -1;
        seq = 1;
        ( (TMenuItem) getComponent("stop")).setEnabled(true);
        ( (TMenuItem) getComponent("cancle")).setEnabled(false);
        resultParm = new TParm();
    }

    /**
     * ��ѯ��������
     *
     * @return
     */
    private TParm onQueryParm() {
        // ���������
        TParm parm = new TParm();
        // ���뵥��
        if (!"".equals(getValueString("REQUEST_NO"))) {
            parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
        }
        // ���벿��
        if (!"".equals(getValueString("APP_ORG_CODE"))) {
            parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        }
        // �������
        if (!"".equals(getValueString("REQTYPE_CODE"))) {
            parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
        }
        // ���ⵥ��
        if (!"".equals(getValueString("DISPENSE_NO"))) {
            parm.setData("DISPENSE_NO", getValueString("DISPENSE_NO"));
        }
        // ��ѯ����
        if (!"".equals(getValueString("START_DATE"))
            && !"".equals(getValueString("END_DATE"))) {
            parm.setData("START_DATE", getValue("START_DATE"));
            parm.setData("END_DATE", getValue("END_DATE"));
        }
        //zhangyong20110517
        parm.setData("REGION_CODE", Operator.getRegion());

        if (parm == null) {
            return parm;
        }
        // ����״̬
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            // δ����
            parm.setData("STATUS", "B");
        }
        else if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            // ���
            parm.setData("STATUS", "A");
        }
        else {
            // ;��
            parm.setData("STATUS", "C");
        }
        return parm;
    }

    /**
     * �������뵥��ȡ��ϸ����Ϣ����ʾ��ϸ������
     *
     * @param req_no
     */
    private void getTableDInfo(String req_no) {
        // ȡ��δ��ɵ�ϸ����Ϣ
        TParm result = new TParm(TJDODBTool.getInstance().select(
            INDSQL.getOutRequestDInfo(req_no, "3")));
//        System.out.println("sql==="+INDSQL.getOutRequestDInfo(req_no, "3"));
//        System.out.println("result==" + result);
        if (result.getCount("ORDER_CODE") == 0) {
            this.messageBox("û��������ϸ");
            return;
        }
        table_d.removeRowAll();
        table_d.setSelectionMode(0);
        if ("TEC".equals(request_type) || "EXM".equals(request_type)
            || "COS".equals(request_type)) {
            u_type = "1";
        }
        else if ("DEP".equals(request_type)) {
            u_type = IndSysParmTool.getInstance().onQuery().getValue(
                "UNIT_TYPE", 0);
        }
        else {
            u_type = "0";
        }
        if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = true;
        }
        else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = true;
        }
        else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = false;
        }
        else if ("EXM".equals(request_type) || "COS".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = false;
        }

        // �������
        resultParm = result;
        // ���TABLE_D
        result = setTableDValue(result);
        if (result.getCount("ORDER_DESC") == 0) {
            this.messageBox("û��������ϸ");
            return;
        }
        table_d.setParmValue(result);
    }

    /**
     * ���ݳ��ⵥ��ȡ��ϸ����Ϣ����ʾ��ϸ������
     *
     * @param dispense_no
     */
    private void getTableDInfo2(String dispense_no) {
    	//===zhangp 20120710 start
    	String sqlGetIndDispenseDByNo = 
    		"SELECT DISTINCT UNIT_TYPE FROM IND_DISPENSEM WHERE DISPENSE_NO = '" + dispense_no + "'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sqlGetIndDispenseDByNo));
        if (result.getCount("UNIT_TYPE") == 0) {
            this.messageBox("û��������ϸ");
            return;
        }
    	if(result.getValue("UNIT_TYPE", 0).equals("1")){
    		sqlGetIndDispenseDByNo = 
    			" SELECT CASE" +
    			" WHEN B.GOODS_DESC IS NULL" +
    			" THEN B.ORDER_DESC" +
    			" ELSE B.ORDER_DESC || '(' || B.GOODS_DESC || ')'" +
    			" END AS ORDER_DESC," +
    			" B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE," +
    			" A.VERIFYIN_PRICE AS STOCK_PRICE, A.RETAIL_PRICE AS RETAIL_PRICE," +
    			" A.BATCH_NO, A.VALID_DATE, B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY," +
    			" C.DOSAGE_QTY, B.TRADE_PRICE, A.SEQ_NO, A.REQUEST_SEQ" +
    			" FROM IND_DISPENSED A, PHA_BASE B, PHA_TRANSUNIT C" +
    			" WHERE A.ORDER_CODE = B.ORDER_CODE" +
    			" AND A.ORDER_CODE = C.ORDER_CODE" +
    			" AND A.DISPENSE_NO = '" + dispense_no + "'";
    	}else{
    		sqlGetIndDispenseDByNo = INDSQL.getIndDispenseDByNo(dispense_no);
    	}
//        TParm result = new TParm(TJDODBTool.getInstance().select(
//            INDSQL.getIndDispenseDByNo(dispense_no)));
        result = new TParm(TJDODBTool.getInstance().select(
        		sqlGetIndDispenseDByNo));
        //===zhangp 20120710 end
        //System.out.println("result----"+result);
        if (result.getCount("ORDER_CODE") == 0) {
            this.messageBox("û��������ϸ");
            return;
        }
        table_d.removeRowAll();
        table_d.setSelectionMode(0);
        // �趨��λ����
        if ("TEC".equals(request_type) || "EXM".equals(request_type)
            || "COS".equals(request_type)) {
            u_type = "1";
        }
        else if ("DEP".equals(request_type)) {
            u_type = IndSysParmTool.getInstance().onQuery().getValue(
                "UNIT_TYPE", 0);
        }
        else {
            u_type = "0";
        }
        // �趨����ⲿ��
        if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = true;

        }
        else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = true;
        }
        else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = false;
        }

        resultParm = result;
        // ���TABLE_D
        //System.out.println("result---"+result);
        result = setTableDValue(result);
        if (result.getCount("ORDER_DESC") == 0) {
            this.messageBox("û��������ϸ");
            return;
        }
        table_d.setParmValue(result);
    }


    /**
     * ���TABLE_D
     *
     * @param table
     * @param parm
     * @param args
     */
    private TParm setTableDValue(TParm result) {
        TParm parm = new TParm();
        double qty = 0;
        double actual_qty = 0;
        double stock_price = 0;
        double retail_price = 0;
        double atm = 0;
        String order_code = "";
        boolean flg = false;
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            flg = false;
        }
        else {
            flg = true;
        }
        for (int i = 0; i < result.getCount(); i++) {
            parm.setData("SELECT_FLG", i, flg);
            parm.setData("ORDER_DESC", i, result.getValue("ORDER_DESC", i));
            parm.setData("SPECIFICATION", i, result
                         .getValue("SPECIFICATION", i));
            qty = result.getDouble("QTY", i);
            parm.setData("QTY", i, qty);
            actual_qty = result.getDouble("ACTUAL_QTY", i);
            parm.setData("ACTUAL_QTY", i, actual_qty);
            order_code = result.getValue("ORDER_CODE", i);
            // �����(���ⲿ��)
            if (!"".equals(result.getValue("BATCH_NO", i))) {
                if ("0".equals(u_type)) {//0�����浥λ�Ǻ� ����stock_qty��Ƭ�� Ҫ���Թ��=�У�1����ҩ��λby liyh 20120910
                	if("RET".equals(request_type)){//�������ҩ����ѯ����� ���ø���batch_no,valid_date����ѯ��ֻ�����order_code��Ok by liyh 20120910
                        parm.setData("STOCK_QTY", i,
                                INDTool.getInstance().getStockQTY(
                                    out_org_code, order_code) /
                                result.getDouble("DOSAGE_QTY", i));
                	}else{
	                    parm.setData("STOCK_QTY", i,
	                                 INDTool.getInstance().getStockQTY(
	                                     out_org_code, order_code,
	                                     result.getValue("BATCH_NO", i),
	                                     result.getValue("VALID_DATE",
	                        i).substring(0, 10), Operator.getRegion()) /
	                                 result.getDouble("DOSAGE_QTY", i)
	                        );
                	}
                }
                else {
                    parm.setData("STOCK_QTY", i,
                                 INDTool.getInstance().getStockQTY(
                                     out_org_code, order_code,
                                     result.getValue("BATCH_NO", i),
                                     result.getValue("VALID_DATE",
                        i).substring(0, 10), Operator.getRegion()));
                }
            }
            else {
                if ("0".equals(u_type)) {
                    parm.setData("STOCK_QTY", i,
                                 INDTool.getInstance().getStockQTY(
                                     out_org_code, order_code) /
                                 result.getDouble("DOSAGE_QTY", i));
                }
                else {
                    parm.setData("STOCK_QTY", i,
                                 INDTool.getInstance().getStockQTY(
                                     out_org_code, order_code));
                }
            }
            if (getRadioButton("UPDATE_FLG_B").isSelected()) {
                parm.setData("OUT_QTY", i, qty - actual_qty);
            }
            else {
                parm.setData("OUT_QTY", i, qty);
            }
            parm.setData("UNIT_CODE", i, result.getValue("UNIT_CODE", i));
            // ʹ�õ�λ
            if ("0".equals(u_type)) {
                // ��浥λ
                stock_price = result.getDouble("STOCK_PRICE", i)
                    * result.getDouble("DOSAGE_QTY", i);
                retail_price = result.getDouble("RETAIL_PRICE", i)
                    * result.getDouble("DOSAGE_QTY", i);
            }
            else {
                // ��ҩ��λ
                stock_price = result.getDouble("STOCK_PRICE", i);
                retail_price = result.getDouble("RETAIL_PRICE", i);
            }
            parm.setData("STOCK_PRICE", i, stock_price);
            atm = StringTool.round(stock_price * qty, 2);
            parm.setData("STOCK_ATM", i, atm);
            parm.setData("RETAIL_PRICE", i, retail_price);
            atm = StringTool.round(retail_price * qty, 2);
            parm.setData("RETAIL_ATM", i, atm);
            atm = StringTool.round(retail_price * qty - stock_price * qty, 2);
            parm.setData("DIFF_ATM", i, atm);
            parm.setData("BATCH_NO", i, result.getValue("BATCH_NO", i));
            parm.setData("VALID_DATE", i, result.getTimestamp("VALID_DATE", i));
            parm.setData("PHA_TYPE", i, result.getValue("PHA_TYPE", i));
            parm.setData("ORDER_CODE", i, order_code);
            //luhai 2012-1-16 add batch_seq
            parm.setData("BATCH_SEQ",i,result.getValue("BATCH_SEQ",i));
        }
        //System.out.println("------"+parm);
        return parm;
    }

    /**
     * ���ѡ���������Ϸ�
     *
     * @param table
     * @param row
     * @param args
     */
    private void getTableSelectValue(TTable table, int row, String[] args) {
        for (int i = 0; i < args.length; i++) {
            setValue(args[i], table.getItemData(row, args[i]));
        }
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("REQUEST_NO"))) {
            this.messageBox("���뵥�Ų���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("APP_ORG_CODE"))) {
            this.messageBox("���벿�Ų���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("REQTYPE_CODE"))) {
            this.messageBox("���������Ϊ��");
            return false;
        }
        return true;
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataD() {
        // �ж�ϸ���Ƿ��б�ѡ����
        table_d.acceptText();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            // �ж�������ȷ��
            double qty = table_d.getItemDouble(i, "OUT_QTY");
            if (qty <= 0) {
                this.messageBox("��������������С�ڻ����0");
                return false;
            }
            double price = table_d.getItemDouble(i, "STOCK_PRICE");
            if (price <= 0) {
                this.messageBox("�ɱ��۲���С�ڻ����0");
                return false;
            }
        }
        return true;
    }

    /**
     * ���������ܽ��
     *
     * @return
     */
    private double getSumRetailMoney() {
        table_d.acceptText();
        double sum = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            sum += table_d.getItemDouble(i, "RETAIL_ATM");
        }
        return StringTool.round(sum, 2);
    }

    /**
     * ����ɱ��ܽ��
     *
     * @return
     */
    private double getSumRegMoney() {
        table_d.acceptText();
        double sum = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            sum += table_d.getItemDouble(i, "STOCK_ATM");
        }
        return StringTool.round(sum, 2);
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
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
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
     * ҩ�������Ϣ
     * @return TParm
     */
    private TParm getSysParm() {
        return IndSysParmTool.getInstance().onQuery();
    }

    /**
     * �������ҵ״̬�ж�
     *
     * @return
     */
    private String getDisCheckFlg(TParm parm) {
        // �������ҵ״̬�ж�
        if ("Y".equals(parm.getValue("DISCHECK_FLG", 0))
            && !"".equals(out_org_code) && !"".equals(in_org_code)) {
            // ��������ȷ���������뵥״̬����ⲿ�ŽԲ�Ϊ��-->��;״̬
            return "1";
        }
        else if ("N".equals(parm.getValue("DISCHECK_FLG", 0))
                 && !"".equals(out_org_code) && !"".equals(in_org_code)) {
            // ����������ȷ���������뵥״̬����ⲿ�ŽԲ�Ϊ��-->���⼴���
            return "2";
        }
        return "1";
    }

    /**
     * ����Ƿ���춯״̬�ж�
     *
     * @param org_code
     * @return
     */
    private boolean getOrgBatchFlg(String org_code) {
        // ����Ƿ���춯״̬�ж�
        if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
            return false;
        }
        return true;
    }

    /**
     * ���������ж�: 1,��ָ�����ź�Ч��;2,ָ�����ź�Ч��
     *
     * @return
     */
    private String getBatchValid(String type) {
        if ("DEP".equals(type) || "TEC".equals(type) || "EXM".equals(type)
            || "GIF".equals(type) || "COS".equals(type)) {
            return "1";
        }
        return "2";
    }

    /**
     * ������;��ҵ/��������������ҵ�����Ĳġ����ұ�ҩ(���⼴���)
     */
    private boolean getDispenseOutOn(String org_code) {
        if (!checkSelectRow()) {
            return false;
        }
        //�ж��������
        if ("DEP".equals(request_type)) {
            String order_code = "";
            double out_qty = 0;
            String order_desc = "";
            for (int i = 0; i < table_d.getRowCount(); i++) {
                if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                    //�ж���ҩ������������
                    order_code = table_d.getParmValue().getValue("ORDER_CODE",
                        i);
                    out_qty = table_d.getItemDouble(i, "OUT_QTY");
                    String sql =
                        " SELECT A.STOCK_QTY / B.STOCK_QTY / B.DOSAGE_QTY AS "
                        + "STOCK_QTY,C.UNIT_CHN_DESC,A.BATCH_NO,A.VALID_DATE, "
                        + " A.ORDER_CODE, D.ORDER_DESC "
                        + " FROM IND_STOCK A, PHA_TRANSUNIT B, "
                        + " SYS_UNIT C, PHA_BASE D "
                        + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                        + " AND A.ORDER_CODE = D.ORDER_CODE "
                        + " AND B.ORDER_CODE = D.ORDER_CODE "
                        + " AND A.ORG_CODE = '" + org_code
                        + "' AND A.ORDER_CODE = '" + order_code
                        + "' AND A.STOCK_QTY > 0 AND A.VALID_DATE > SYSDATE "
                        + " AND A.ACTIVE_FLG = 'Y' AND D.PHA_TYPE = 'G'"
                        + " AND B.STOCK_UNIT = C.UNIT_CODE "
                        + " ORDER BY A.VALID_DATE DESC, A.BATCH_SEQ";
                    TParm checkParm = new TParm(TJDODBTool.getInstance().select(
                        sql));
                    if (checkParm.getCount("ORDER_CODE") > 0) {
                        if (out_qty > checkParm.getDouble("STOCK_QTY", 0)) {
                            order_desc = checkParm.getValue("ORDER_DESC", 0);
                            this.messageBox(order_desc + " " + order_code
                                            + " ������� " + out_qty + " " +
                                            checkParm.getValue("UNIT_CHN_DESC",
                                0) + ", ����:" +
                                            checkParm.getValue("BATCH_NO", 0) +
                                            ",Ч��:" +
                                            checkParm.getValue("VALID_DATE", 0).
                                            substring(0, 10) +
                                            ",��ǰ���Ϊ" +
                                            checkParm.getDouble("STOCK_QTY", 0) +
                                            " " +
                                            checkParm.getValue("UNIT_CHN_DESC",
                                0) + " ,�����Ƚ���ȫ������");
                            return false;
                        }
                    }
                }
            }
        }

        TParm parm = new TParm();
        // ������Ϣ
        if (!CheckDataM()) {
            return false;
        }
        parm = getDispenseMParm(parm, "1");
        // ϸ����Ϣ
        if (!CheckDataD()) {
            return false;
        }
        parm = getDispenseDParm(parm);
        // ʹ�õ�λ
        parm.setData("UNIT_TYPE", u_type);
        // ���뵥����
        parm.setData("REQTYPE_CODE", request_type);
        // ���ⲿ��
        parm.setData("ORG_CODE", org_code);
        // ִ����������
        parm = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
                                            "onInsertOutOn", parm);

        // �����ж�
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return false;
        }
        this.messageBox("P0001");
        this.setValue("DISPENSE_NO", dispense_no);
        return true;
    }

    /**
     * ���⼴�����ҵ
     *
     * @param out_org_code
     * @param in_org_code
     * @param batchvalid
     */
    private void getDispenseOutIn(String out_org_code, String in_org_code,
                                  String reuprice_flg, boolean out_flg,
                                  boolean in_flg) {
        if (!checkSelectRow()) {
            return;
        }
        TParm parm = new TParm();
        // ������Ϣ(OUT_M)
        if (!CheckDataM()) {
            return;
        }
        parm = getDispenseMParm(parm, "3");
        // ϸ����Ϣ(OUT_D)
        if (!CheckDataD()) {
            return;
        }
        parm = getDispenseDParm(parm);
        //System.out.println("PARM-> " + parm);

        // ʹ�õ�λ
        parm.setData("UNIT_TYPE", u_type);
        // ���뵥����
        parm.setData("REQTYPE_CODE", request_type);
        // ���ⲿ��
        parm.setData("OUT_ORG_CODE", out_org_code);
        // ��ⲿ��
        parm.setData("IN_ORG_CODE", in_org_code);
        // �Ƿ����(IN_FLG)
        parm.setData("IN_FLG", in_flg);
        // �ж��Ƿ��Զ����ɱ��۴��������
        parm.setData("REUPRICE_FLG", reuprice_flg);

        // ִ����������
        parm = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
                                            "onInsertOutIn", parm);
        // �����ж�
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

    /**
     * ��������(��;)
     */
    private void getUpdateDispenseMOutOn() {
        TParm parm = new TParm();
        // ������Ϣ
        if (!CheckDataM()) {
            return;
        }
        parm = getDispenseMParm(parm, "1").getParm("OUT_M");
        // ִ�����ݸ���
        parm = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
                                            "onUpdateMOutOn", parm);
        // �����ж�
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

    /**
     * ������ж�(��ͬҩƷͬʱ��������������һ����пۿ�)
     *
     * @return
     */
    private boolean checkSameOrderQty(Map map) {
        String order_code = "";
        double qty = 0;
        double stock_qty = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            order_code = resultParm.getValue("ORDER_CODE", i);
            stock_qty = INDTool.getInstance().getStockQTY(out_org_code,
                order_code);
            qty = TypeTool.getDouble(map.get(order_code));
            if ("0".equals(u_type)) {
                qty = qty * resultParm.getDouble("DOSAGE_QTY", i);
            }
            if (qty > stock_qty) {
                this.messageBox("�����������ڿ����");
                return false;
            }
        }
        return true;
    }

    /**
     * ��ͬҩƷ��������
     *
     * @return
     */
    private Map getSumQtyByOrder() {
        /** ��ͬҩƷ�������� */
        Map map = new HashMap();
        String order_code = "";
        double out_qty = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            order_code = resultParm.getValue("ORDER_CODE", i);
            out_qty = table_d.getItemDouble(i, "OUT_QTY");
            if (map.containsKey(order_code)) {
                double qty = TypeTool.getDouble(map.get(order_code)) + out_qty;
                map.remove(order_code);
                map.put(order_code, qty);
            }
            else {
                map.put(order_code, out_qty);
            }
        }
        return map;
    }

    /**
     * �ж�ϸ���Ƿ�ѡ��
     *
     * @return
     */
    private boolean checkSelectRow() {
        // �ж�ϸ���Ƿ�ѡ��
        boolean flg = true;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                flg = false;
            }
        }
        if (flg) {
            this.messageBox("û��ѡ��");
            return false;
        }
        return true;
    }

    /**
     * ���������Ϣ
     *
     * @param parm
     * @return
     */
    private TParm getDispenseMParm(TParm parm, String update_flg) {
        TParm parmM = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        // ���ⵥ��
        dispense_no = "";
        if ("".equals(getValueString("DISPENSE_NO"))) {
            dispense_no = SystemTool.getInstance().getNo("ALL", "IND",
                "IND_DISPENSE", "No");
        }
        else {
            dispense_no = getValueString("DISPENSE_NO");
        }
        parmM.setData("DISPENSE_NO", dispense_no);
        parmM.setData("REQTYPE_CODE", getValue("REQTYPE_CODE"));
        parmM.setData("REQUEST_NO", getValue("REQUEST_NO"));
        parmM.setData("REQUEST_DATE", getValue("REQUEST_DATE"));
        parmM.setData("APP_ORG_CODE", getValue("APP_ORG_CODE"));
        parmM.setData("TO_ORG_CODE", getValue("TO_ORG_CODE"));
        parmM.setData("URGENT_FLG", getValue("URGENT_FLG"));
        parmM.setData("DESCRIPTION", getValue("DESCRIPTION"));
        parmM.setData("DISPENSE_DATE", getValue("DISPENSE_DATE"));
        parmM.setData("DISPENSE_USER", Operator.getID());
        if (!"1".equals(update_flg)) {
            parmM.setData("WAREHOUSING_DATE", date);
            parmM.setData("WAREHOUSING_USER", Operator.getIP());
        }
        else {
            parmM.setData("WAREHOUSING_DATE", tnull);
            parmM.setData("WAREHOUSING_USER", "");
        }
        parmM.setData("REASON_CHN_DESC", getValue("REASON_CHN_DESC"));
        parmM.setData("UNIT_TYPE", u_type);
        if ("WAS".equals(getValue("REQTYPE_CODE")) ||
            "THO".equals(getValue("REQTYPE_CODE")) ||
            "COS".equals(getValue("REQTYPE_CODE")) ||
            "EXM".equals(getValue("REQTYPE_CODE"))) {
            update_flg = "3";
        }
        parmM.setData("UPDATE_FLG", update_flg);
        parmM.setData("OPT_USER", Operator.getID());
        parmM.setData("OPT_DATE", date);
        parmM.setData("OPT_TERM", Operator.getIP());
        //zhangyong20110517
        parmM.setData("REGION_CODE", Operator.getRegion());

        if (parmM != null) {
            parm.setData("OUT_M", parmM.getData());
        }
        return parm;
    }

    /**
     * �����ϸ��Ϣ
     *
     * @param parm
     * @return
     */
    private TParm getDispenseDParm(TParm parm) {
        TParm parmD = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        String batch_no = "";
        int count = 0;
        String order_code = "";
        String valid_date = "";
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            parmD.setData("DISPENSE_NO", count, dispense_no);
            parmD.setData("SEQ_NO", count, seq + count);
            parmD.setData("REQUEST_SEQ", count, resultParm.getInt(
                "REQUEST_SEQ", i));
            order_code = resultParm.getValue("ORDER_CODE", i);
            parmD.setData("ORDER_CODE", count, order_code);
            parmD.setData("QTY", count, table_d.getItemDouble(i, "QTY"));
            parmD.setData("UNIT_CODE", count, table_d.getItemString(i,
                "UNIT_CODE"));
            parmD.setData("RETAIL_PRICE", count, table_d.getItemDouble(i,
                "RETAIL_PRICE"));
            parmD.setData("STOCK_PRICE", count, table_d.getItemDouble(i,
                "STOCK_PRICE"));
            parmD.setData("ACTUAL_QTY", count, table_d.getItemDouble(i,
                "OUT_QTY"));
            parmD.setData("PHA_TYPE", count, table_d.getItemString(i,
                "PHA_TYPE"));
            //luahi modify 2012-1-16 batch_seq ��ֵ��Ҫ��Tparm��ȡ�ã���Ϊҳ���в�������batch_seq begin
//            parmD.setData("BATCH_SEQ", count, table_d.getItemData(i,
//                "BATCH_SEQ"));
              parmD.setData("BATCH_SEQ",count,table_d.getParmValue().getInt("BATCH_SEQ",i));
            //luahi modify 2012-1-16 batch_seq ��ֵ��Ҫ��Tparm��ȡ�ã���Ϊҳ���в�������batch_seq end
            
            batch_no = table_d.getItemString(i, "BATCH_NO");
            parmD.setData("BATCH_NO", count, batch_no);
            valid_date = table_d.getItemString(i, "VALID_DATE");
            if ("".equals(valid_date)) {
                parmD.setData("VALID_DATE", count, tnull);
            }
            else {
                parmD.setData("VALID_DATE", count,
                              table_d.getItemTimestamp(i, "VALID_DATE"));
            }
            parmD.setData("DOSAGE_QTY", count, resultParm.getDouble(
                "DOSAGE_QTY", i));
            parmD.setData("OPT_USER", count, Operator.getID());
            parmD.setData("OPT_DATE", count, date);
            parmD.setData("OPT_TERM", count, Operator.getIP());
            count++;
        }
        if (parmD != null) {
            parm.setData("OUT_D", parmD.getData());
        }
        return parm;
    }

    /**
     * �ж��Ƿ�����в�ҩ
     *
     * @return
     */
    private boolean checkPhaType() {
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            if ("C".equals(table_d.getItemString(i, "PHA_TYPE"))) {
                return true;
            }
        }
        return false;
    }


    /**
     * ȡ��SYS_FEE��Ϣ��������״̬����
     * @param order_code String
     */
    private void setSysStatus(String order_code) {
        TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
        String status_desc = "ҩƷ����:" + order.getValue("ORDER_CODE")
            + " ҩƷ����:" + order.getValue("ORDER_DESC")
            + " ��Ʒ��:" + order.getValue("GOODS_DESC")
            + " ���:" + order.getValue("SPECIFICATION");
        callFunction("UI|setSysStatus", status_desc);
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

    /**
     * �ж��Ƿ��п�棬����û�п���ҩƷȡ����ѡ
     * @return String
     */
    private String checkStockQty() {
        double stock_qty = 0;
        // ��治����Ϣ
        String message = "";
        for (int i = 0; i < table_d.getRowCount(); i++) {
            boolean flg = false;
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            stock_qty = table_d.getItemDouble(i, "STOCK_QTY");
            double qty = table_d.getItemDouble(i, "OUT_QTY");

            if (qty > stock_qty) {
                flg = true;
            }
            // û�п���ҩƷȡ����ѡ����¼����ҩƷ����Ϣ
            if (flg) {
                table_d.setItem(i, "SELECT_FLG", "N");
                message += table_d.getItemString(i, "ORDER_DESC") + "\n";
            }
        }
        if (!"".equals(message) && message.length() > 0) {
            message += "��治�㣡";
        }
        return message;
    }
}
