package com.javahis.ui.hrm;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jdo.bil.BIL;
import jdo.hl7.Hl7Communications;
import jdo.hrm.HRMFeePackTool;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPatAdm;
import jdo.hrm.HRMPatInfo;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Personal;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatHRMPackageD;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ҽ������������ɾ��
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author Yuanxm
 * @version 1.0
 */
public class HRMOrderBatchControl
        extends TControl {

    private TTable table;
    private HRMOrder order;// ҽ������
    private HRMPatAdm adm;// ��������
    private HRMPatInfo pat;// ��������
    TParm inParm;
    String ctz;// ���
    String dept;// ����
    int[] commonInsertRows = new int[]{};// ��¼������ҽ�����к� add by wanglong 20130226
    String packageCode = "";// �ײʹ��� add by wanglong 20130423
    TextFormatHRMPackageD packageDetail;// �ײ�ϸ������� add by wanglong 20130423
    
    /**
     * ��ʼ���¼�
     * 
     */
    public void onInit() {
        super.onInit();
        /**
         * ��ʼ���ؼ�
         */
        initComponent();
        initData();
        inParm = (TParm) this.getParameter();
        if (inParm.getValue("METHOD", 0).equals("ADD")) {
            this.setTitle("��������");
        } else if (inParm.getValue("METHOD", 0).equals("DELETE")) {
            this.setTitle("����ɾ��");
        }
        int patCount = inParm.getCount("MR_NO");
        if (patCount >= 1000) {
            this.messageBox("ͬʱ��������Ա����Ҫ����1000��");
            this.closeWindow();
        }
        ctz = Personal.getDefCtz();
        dept = Operator.getDept();
    }

    public void initComponent() {
        table = (TTable) this.getComponent("TABLE");
        table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this, "onOrderEditComponent");
        table.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE, this, "onValueChanged");
        packageDetail = (TextFormatHRMPackageD) this.getComponent("ORDER_CODE");// add by wanglong 20130423
    }

    /**
     * ֵ�ı��¼�
     * 
     * @param tNode
     *            TTableNode
     * @return boolean
     */
    public boolean onValueChanged(TTableNode tNode) {
        table.acceptText();
        int column = tNode.getColumn();
        int row = tNode.getRow();
        String colName = table.getParmMap(column);
        if (tNode.getValue().equals(tNode.getOldValue())) return true;
        // �Ʊ�����
        if ("DEPT_ATTRIBUTE".equals(colName)) {
            order.setItem(row, "DEPT_ATTRIBUTE", tNode.getValue());
            this.getTTable("TABLE").setDSValue(row);
            return false;
        }
        if ("OWN_PRICE_MAIN".equals(colName)) {// add by wanglong 20130409
            TParm parm = tNode.getTable().getDataStore().getRowParm(tNode.getRow());
            if (parm.getValue("CAT1_TYPE").equals("PHA")
                    || (parm.getValue("ORDER_CAT1_CODE").equals("MAT") && "N".equals(parm
                            .getValue("HIDE_FLG")))) {
                order.setItem(row, "OWN_PRICE", tNode.getValue());
                this.getTTable("TABLE").setDSValue(row);
            } else if (this.isOrderSet(parm)) {
                messageBox("����ҽ�����ܸ��ĵ���");
                return true;
            }
        }
        if ("DISPENSE_QTY".equals(colName)) {
            if (!tNode.getValue().toString().matches("\\d+")) {// add by wanglong 20130416
                return true;
            }
            TParm parm = tNode.getTable().getDataStore().getRowParm(tNode.getRow());
            if (parm.getValue("CAT1_TYPE").equals("PHA")
                    || (parm.getValue("ORDER_CAT1_CODE").equals("MAT") && "N".equals(parm
                            .getValue("HIDE_FLG")))) {
                order.setItem(row, "DISPENSE_QTY", tNode.getValue());
                this.getTTable("TABLE").setDSValue(row);
            } else {
                if (this.isOrderSet(parm)) {// modify by wanglong 20130408
                    if ((parm.getValue("CAT1_TYPE").equals("LIS") || parm.getValue("CAT1_TYPE")
                            .equals("RIS")) && parm.getValue("SETMAIN_FLG").equals("Y")) {// modify by wanglong 20130412
                        messageBox("�����鲻�ܸ�������");
                        return true;
                    }
                    int groupNo = parm.getInt("ORDERSET_GROUP_NO");
                    String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
                    int newRow[] = order.getNewRows(buff);
                    for (int i : newRow) {
                        TParm linkParm = order.getRowParm(i, buff);
                        if (!order.isActive(i, buff)) continue;
                        // �ҵ����˻������д�ҽ����ΨһID
                        int filterId = (Integer) order.getItemData(i, "#ID#", buff);
                        // �ҵ����������д�ҽ����ΨһID
                        int primaryId =
                                (Integer) order.getItemData(tNode.getRow(), "#ID#", order.PRIMARY);
                        double newQty = TypeTool.getDouble(tNode.getValue().toString());
                        if (filterId == primaryId) {
                            order.setItem(i, "DISPENSE_QTY", newQty, buff);// modify by wanglong 20130416
                            continue;// ����ҽ������
                        }
                        table.setItem(row, "AR_AMT_MAIN",
                                      TypeTool.getDouble(table.getItemData(row, "OWN_PRICE_MAIN"))
                                              * newQty);
                        if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
                            double oldValue =
                                    TypeTool.getDouble(order.getItemData(i, "DISPENSE_QTY", buff));
                            order.setItem(i, "DISPENSE_QTY", oldValue * newQty, buff);// modify by wanglong 20130416
                            this.getTTable("TABLE").setDSValue(i);
                        }
                    }
                }
            }
            return false;
        }
        // ��������
        if ("DEPT_CODE".equals(colName)) {
            TParm parm = tNode.getTable().getDataStore().getRowParm(tNode.getRow());
            if (parm.getValue("CAT1_TYPE").equals("PHA")
                    || (parm.getValue("ORDER_CAT1_CODE").equals("MAT") && "N".equals(parm
                            .getValue("HIDE_FLG")))) {
                order.setItem(row, "DEPT_CODE", tNode.getValue());
                this.getTTable("TABLE").setDSValue(row);
            } else {
                if (this.isOrderSet(parm)) {
                    int groupNo = parm.getInt("ORDERSET_GROUP_NO");
                    String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
                    int newRow[] = order.getNewRows(buff);
                    for (int i : newRow) {
                        TParm linkParm = order.getRowParm(i, buff);
                        if (!order.isActive(i, buff)) continue;
                        // �ҵ����˻������д�ҽ����ΨһID
                        int filterId = (Integer) order.getItemData(i, "#ID#", buff);
                        // �ҵ����������д�ҽ����ΨһID
                        int primaryId =
                                (Integer) order.getItemData(tNode.getRow(), "#ID#", order.PRIMARY);
                        if (filterId == primaryId) continue;
                        if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
                            order.setItem(i, "DEPT_CODE", tNode.getValue());
                            this.getTTable("TABLE").setDSValue(i);
                        }
                    }
                }
            }
            return false;
        }
        // ִ�п���
        if ("EXEC_DEPT_CODE".equals(colName)) {
            TParm parm = tNode.getTable().getDataStore().getRowParm(tNode.getRow());
            if (parm.getValue("CAT1_TYPE").equals("PHA")
                    || (parm.getValue("ORDER_CAT1_CODE").equals("MAT") && "N".equals(parm
                            .getValue("HIDE_FLG")))) {
                order.setItem(row, "EXEC_DEPT_CODE", tNode.getValue());
                this.getTTable("TABLE").setDSValue(row);
            } else {
                if (this.isOrderSet(parm)) {
                    int groupNo = parm.getInt("ORDERSET_GROUP_NO");
                    String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
                    int newRow[] = order.getNewRows(buff);
                    for (int i : newRow) {
                        TParm linkParm = order.getRowParm(i, buff);
                        if (!order.isActive(i, buff)) continue;
                        // �ҵ����˻������д�ҽ����ΨһID
                        int filterId = (Integer) order.getItemData(i, "#ID#", buff);
                        // �ҵ����������д�ҽ����ΨһID
                        int primaryId =
                                (Integer) order.getItemData(tNode.getRow(), "#ID#", order.PRIMARY);
                        if (filterId == primaryId) continue;
                        if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
                            order.setItem(i, "EXEC_DEPT_CODE", tNode.getValue());
                            this.getTTable("TABLE").setDSValue(i);
                        }
                    }
                }
            }
            return false;
        }
        return false;
    }

    public void initData() {
        order = new HRMOrder();
        order.onQuery();
        int row = table.getSelectedRow();
        row = order.insertRow();
        order.setItem(row, "SEQ", order.getItemInt(row, "#ID#"));
        order.setItem(row, "CHECK_SEQ", null);
        order.setItem(row, "OPT_USER", Operator.getID());
        order.setItem(row, "OPT_TERM", Operator.getIP());
        order.setItem(row, "SETMAIN_FLG", "Y");
        order.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        order.setActive(row, false);
        table.setDataStore(order);
        table.setDSValue();
        pat = new HRMPatInfo();
        pat.onQuery();
        adm = new HRMPatAdm();
        adm.onQuery();
    }

    /**
     * ���SYS_FEE��������
     * 
     * @param com
     *            Component
     * @param row
     *            int
     * @param column
     *            int
     */
    public void onOrderEditComponent(Component com, int row, int column) {
        // �����ǰ�к�
        column = table.getColumnModel().getColumnIndex(column);
        String columnName = table.getParmMap(column);
        if (!"ORDER_DESC".equalsIgnoreCase(columnName)) {
            return;
        }
        if (!(com instanceof TTextField)) {
            return;
        }
        if (!StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        TParm parm = new TParm();
        parm.setData("HRM_TYPE", "ANYCHAR");
        // ��table�ϵ���text����sys_fee��������
        textfield.setPopupMenuParameter("ORDER",
                                        getConfigParm()
                                                .newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
                                        parm);
        // ����text���ӽ���sys_fee�������ڵĻش�ֵ
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popOrderReturn");
    }

    /**
     * �ײ�ϸ������ҽ������,����ϸ�࣬��Ҳ����ϸ����Ϣ����ֻ��ʾ����
     * 
     * @param tag
     * @param obj
     */
    public void popOrderReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        int row = table.getSelectedRow();
        int oldRow = row;
        if (!StringUtil.isNullString(table.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        table.acceptText();
        order.setItem(row, "SEQ", order.getItemInt(row, "#ID#"));
        order.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
        order.setItem(row, "ORDERSET_CODE", parm.getValue("ORDERSET_CODE"));
        order.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
        order.setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC"));
        order.setItem(row, "DISPENSE_QTY", 1.0);
        order.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION"));
        order.setItem(row, "DISPENSE_UNIT", parm.getValue("UNIT_CODE"));
        order.setItem(row, "ORIGINAL_PRICE", parm.getDouble("OWN_PRICE"));
        order.setItem(row, "OWN_PRICE", parm.getDouble("OWN_PRICE"));
        order.setItem(row, "NHI_PRICE", parm.getDouble("NHI_PRICE"));// add by wanglong 20130316
        order.setItem(row, "RPTTYPE_CODE", parm.getData("RPTTYPE_CODE"));
        order.setItem(row, "OPTITEM_CODE", parm.getData("OPTITEM_CODE"));
        order.setItem(row, "DEV_CODE", parm.getData("DEV_CODE"));
        order.setItem(row, "MR_CODE", parm.getData("MR_CODE"));
        order.setItem(row, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
        order.setItem(row, "REXP_CODE", BIL.getRexpCode(parm.getValue("CHARGE_HOSP_CODE"), "H"));
        order.setItem(row, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
        order.setItem(row, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE"));
        if (StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE"))) {
            order.setItem(row, "EXEC_DEPT_CODE", Operator.getDept());
        } else {
            order.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE"));
        }
        // ====================add by wangong 20130422 �Զ������Ʊ�����
        String attrSql =
                "SELECT DISTINCT DEPT_ATTRIBUTE FROM HRM_PACKAGED WHERE ORDER_CODE = '"
                        + parm.getValue("ORDER_CODE") + "' ORDER BY DEPT_ATTRIBUTE";
        TParm attrParm = new TParm(TJDODBTool.getInstance().select(attrSql));
        if (attrParm.getErrCode() != 0) {
            this.messageBox("��ѯ�Ʊ�����ʧ��");
        }
        if (attrParm.getCount() > 0) {
            parm.setData("DEPT_ATTRIBUTE", attrParm.getValue("DEPT_ATTRIBUTE", 0));
        }
        // ====================add end
        order.setItem(row, "DEPT_ATTRIBUTE", parm.getData("DEPT_ATTRIBUTE"));
        order.setItem(row, "OPT_USER", Operator.getID());
        order.setItem(row, "OPT_TERM", Operator.getIP());
        order.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        order.setItem(row, "REGION_CODE", Operator.getRegion());
        order.setItem(row, "DR_CODE", Operator.getID());
        order.setItem(row, "DEPT_CODE", dept);
        order.setItem(row, "SETMAIN_FLG", "Y");
        order.setItem(row, "HIDE_FLG", "N");
        order.setItem(row, "CTZ1_CODE", ctz);
        order.setItem(row, "CTZ2_CODE", "");
        order.setItem(row, "CTZ3_CODE", "");
        order.setItem(row, "BILL_FLG", "N");
        order.setItem(row, "EXEC_FLG", "N");
        order.setActive(row, true);
        row = order.insertRow();
        order.setItem(row, "SETMAIN_FLG", "Y");
        order.setItem(row, "HIDE_FLG", "N");
        order.setItem(row, "BILL_FLG", "N");
        order.setItem(row, "EXEC_FLG", "N");
        order.setActive(row, false);
        if (!parm.getBoolean("ORDERSET_FLG")) {
            table.setDSValue();
            return;
        }
        String sql = HRMFeePackTool.QUERY_ORDERSET.replace("#", parm.getValue("ORDER_CODE"));
        order.setItem(oldRow, "ORDERSET_CODE", parm.getValue("ORDER_CODE"));
        order.setItem(oldRow, "SETMAIN_FLG", "Y");// ��ʾ
        int groupNo = order.getMaxGroupNo();
        order.setItem(oldRow, "ORDERSET_GROUP_NO", groupNo);
        TParm orderSet = new TParm(TJDODBTool.getInstance().select(sql));
        if (orderSet == null || orderSet.getErrCode() != 0) {
            this.messageBox_("ѡ�����ݴ���");
            return;
        }
        int count = orderSet.getCount("ORDER_CODE");
        for (int i = 0; i < count; i++) {
            order.setItem(row, "SEQ", order.getItemInt(row, "#ID#"));
            order.setItem(row, "ORDER_CODE", orderSet.getValue("ORDER_CODE", i));
            order.setItem(row, "ORDERSET_CODE", orderSet.getValue("ORDERSET_CODE", i));
            order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
            order.setItem(row, "ORDER_DESC", orderSet.getValue("ORDER_DESC", i));
            order.setItem(row, "GOODS_DESC", orderSet.getValue("GOODS_DESC", i));
            order.setItem(row, "DISPENSE_QTY", orderSet.getDouble("DOSAGE_QTY", i));
            order.setItem(row, "SPECIFICATION", orderSet.getValue("SPECIFICATION", i));
            order.setItem(row, "DISPENSE_UNIT", orderSet.getValue("UNIT_CODE", i));
            order.setItem(row, "ORIGINAL_PRICE", orderSet.getDouble("OWN_PRICE", i));
            order.setItem(row, "OWN_PRICE", orderSet.getDouble("OWN_PRICE", i));
            order.setItem(row, "NHI_PRICE", orderSet.getDouble("NHI_PRICE", i));// add by wanglong 20130316
            order.setItem(row, "RPTTYPE_CODE", orderSet.getData("RPTTYPE_CODE", i));
            order.setItem(row, "OPTITEM_CODE", orderSet.getData("OPTITEM_CODE", i));
            order.setItem(row, "DEV_CODE", orderSet.getData("DEV_CODE", i));
            order.setItem(row, "MR_CODE", orderSet.getData("MR_CODE", i));
            order.setItem(row, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
            order.setItem(row, "REXP_CODE", BIL.getRexpCode(parm.getValue("CHARGE_HOSP_CODE"), "H"));
            order.setItem(row, "CAT1_TYPE", orderSet.getValue("CAT1_TYPE", i));
            order.setItem(row, "ORDER_CAT1_CODE", orderSet.getValue("ORDER_CAT1_CODE", i));
            if (StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE"))) {
                order.setItem(row, "EXEC_DEPT_CODE", dept);
            } else {
                order.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE"));
            }
            order.setItem(row, "DEPT_ATTRIBUTE", parm.getData("DEPT_ATTRIBUTE"));
            order.setItem(row, "OPT_USER", Operator.getID());
            order.setItem(row, "OPT_TERM", Operator.getIP());
            order.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
            order.setItem(row, "REGION_CODE", Operator.getRegion());
            order.setItem(row, "DR_CODE", Operator.getID());
            order.setItem(row, "DEPT_CODE", dept);
            order.setItem(row, "SETMAIN_FLG", "N");// ����
            order.setItem(row, "HIDE_FLG", "Y");
            order.setItem(row, "CTZ1_CODE", ctz);
            order.setItem(row, "CTZ2_CODE", "");
            order.setItem(row, "CTZ3_CODE", "");
            order.setItem(row, "BILL_FLG", "N");
            order.setItem(row, "EXEC_FLG", "N");
            order.setActive(row, true);
            row = order.insertRow();
            order.setItem(row, "SETMAIN_FLG", "Y");// modify by wanglong 20130423
            order.setItem(row, "HIDE_FLG", "N");// modify by wanglong 20130423
            order.setItem(row, "BILL_FLG", "N");
            order.setItem(row, "EXEC_FLG", "N");
            order.setActive(row, false);
        }
//      row = order.insertRow();//delete by wanglong 20130423
//      order.setItem(row, "SETMAIN_FLG", "Y");
//      order.setItem(row, "HIDE_FLG", "N");
//      order.setItem(row, "BILL_FLG", "N");
//      order.setItem(row, "EXEC_FLG", "N");
//      order.setActive(row, false);
        order.setFilter(" SETMAIN_FLG='Y' ");
        order.filter();
        table.setDSValue();
        table.getTable().grabFocus();
        table.setSelectedRow(table.getRowCount() - 1);
        table.setSelectedColumn(1);
    }

    /**
     * ɾ��ҽ��
     */
    public void onDelRow() {
        if (table.getSelectedRow() < 0) {
            return;
        }
        String filter = order.getFilter();
        // String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
        // TParm newParm = order.getBuffer(buff);
        int[] newRows = order.getNewRows();
        int row = -1;
        row = table.getSelectedRow();
        String orderCode = order.getItemString(newRows[row], "ORDER_CODE");
        String orderSetCode = order.getItemString(newRows[row], "ORDERSET_CODE");
        int groupNo = order.getItemInt(newRows[row], "ORDERSET_GROUP_NO");
        // String setMainFlg = order.getItemString(newRows[row], "SETMAIN_FLG");
        if (StringUtil.isNullString(orderCode)) {
            return;
        }
        if (order.getItemString(newRows[row], "CAT1_TYPE").equals("PHA")
                || (order.getItemString(newRows[row], "ORDER_CAT1_CODE").equals("MAT") && "N"
                        .equals(order.getItemString(newRows[row], "HIDE_FLG")))) {
            order.deleteRow(newRows[row]);
        } else {
            order.setFilter("");
            order.filter();
            // String buff2 = order.isFilter() ? order.FILTER : order.PRIMARY;
            // TParm newParm2 = order.getBuffer(buff2);
            int count = order.rowCount();
            for (int i = count - 1; i > -1; i--) {
                if ((orderSetCode.equalsIgnoreCase(order.getItemString(i, "ORDERSET_CODE")) && groupNo == order
                        .getItemInt(i, "ORDERSET_GROUP_NO"))) {
                    order.deleteRow(i);
                }
            }
        }
        int count = order.rowCount();
        for (int i = count - 1; i > -1; i--) {
            if (order.getItemString(i, "ORDER_CODE").equals("")) {
                order.deleteRow(i);
            }
        }
        row = order.insertRow();
        order.setItem(row, "SEQ", order.getItemInt(row, "#ID#"));
        order.setItem(row, "CHECK_SEQ", null);
        order.setItem(row, "OPT_USER", Operator.getID());
        order.setItem(row, "OPT_TERM", Operator.getIP());
        order.setItem(row, "SETMAIN_FLG", "Y");
        order.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        order.setActive(row, false);
        // String buff3 = order.isFilter() ? order.FILTER : order.PRIMARY;
        // TParm newParm3 = order.getBuffer(buff3);
        order.setFilter(filter);
        order.filter();
        table.setDSValue();
    }

    public void onSave() {
        table.acceptText();
        int pageCount = order.rowCount();
        if (pageCount <= 0) {
            this.messageBox("û�б�������");
            return;
        } else {
            String method = inParm.getValue("METHOD", 0);
            if (method != null && method.equals("ADD")) {
                batchAdd();
            } else {
                batchDelete();
            }
        }
    }

    /**
     * ��������
     */
    public void batchAdd() {// refactor by wanglong 20130311
        order.setFilter("");
        order.filter();
        int[] commonInsertRows = order.getNewRows();// ��¼������ҽ�����к� add by wanglong 20130226
        Set<String> orderSet = new HashSet<String>();
        int length = 0;
        for (int i : commonInsertRows) {
            if (order.getItemString(i, "SETMAIN_FLG").equals("Y")) {
                length++;
                orderSet.add(order.getItemString(i, "ORDER_CODE"));
            }
        }
        if (orderSet.size() != length) {
            this.messageBox("�������ظ����ĳ��ҽ��\n���ڼ����飬ÿ��ֻ�����һ��\n����ҩ�������ģ�ͨ��������������Ӷ��");
            onClear();
            return;
        }
        String mrList = "";
        int patCount = inParm.getCount("MR_NO");
        if (patCount >= 1000) {
            this.messageBox("ͬʱ��������Ա����Ҫ����1000��");
            return;
        }
        String patSql = "SELECT * FROM SYS_PATINFO WHERE MR_NO IN (#)";// add by wanglong 20130304
        for (int i = 0; i < patCount; i++) {
            mrList += "'" + inParm.getValue("MR_NO", i) + "',";
        }
        mrList = mrList.substring(0, mrList.length() - 1);
        patSql = patSql.replaceFirst("#", mrList);
        TParm patInfoTparm = new TParm(TJDODBTool.getInstance().select(patSql));
        if (patInfoTparm.getErrCode() != 0 || patInfoTparm.getCount() < 1) {
            this.messageBox("��ѯ��Ա��Ϣ����");
            return;
        }
        int errCount = 0;// �������
        List listHl7 = new ArrayList();
        boolean flag = false;// ��¼������ҽ�����Ƿ����������
        for (int insertRow : commonInsertRows) {// add by wanglong 20130415
            TParm parm = order.getRowParm(insertRow);
            if (parm.getValue("SETMAIN_FLG").equals("Y")
                    && (parm.getValue("CAT1_TYPE").equals("LIS") || parm.getValue("CAT1_TYPE")
                            .equals("RIS"))) {
                flag = true;
                break;
            }
        }
        for (int i = 0; i < patCount; i++) {
            TParm patRow = (TParm) inParm.getRow(i);
            String mrNo = patRow.getValue("MR_NO");
            String seqNo = patRow.getValue("SEQ_NO");
            String patName = patRow.getValue("PAT_NAME");
            if (StringUtil.isNullString(mrNo)) {
                this.messageBox_("���:" + seqNo + "  ������" + patName + " ������Ϊ��");
                errCount++;
                continue;
            }
            String caseNo = "";
            String contractCode = patRow.getValue("CONTRACT_CODE");
            if (patRow.getValue("CASE_NO").length() == 0) {
                caseNo = adm.getLatestCaseNoBy(mrNo, contractCode);
            } else {
                caseNo = patRow.getValue("CASE_NO");
            }
            if (StringUtil.isNullString(caseNo)) {
                this.messageBox("���:" + seqNo + "  ������" + patName + " ��ҽ��δչ��������������");
                errCount++;
                continue;
            }
            // add by wangong 20130415 ���ӽ���״̬�ļ��
            String billSql =
                    "SELECT DISTINCT BILL_NO FROM HRM_ORDER WHERE CASE_NO = '" + caseNo + "'";
            TParm billParm = new TParm(TJDODBTool.getInstance().select(billSql));
            if (billParm.getErrCode() != 0) {
                this.messageBox("��������Ϣ����");
                return;
            }
            if (billParm.getCount() > 1
                    || (billParm.getCount() > 0 && !StringUtil.isNullString(billParm
                            .getValue("BILL_NO", 0).trim()))) {
                this.messageBox("���:" + seqNo + "  ������" + patName + " �ѽ��㣬����������");
                errCount++;
                continue;
            }
            // add end
            HRMOrder everyPatOrder;// ҽ������
            everyPatOrder = new HRMOrder();
            everyPatOrder.onQuery(caseNo, mrNo);
            everyPatOrder.retrieve();
            // ������ѱ�����seqȡ���ݿ������ֵ+1
            int orderSeqNo = everyPatOrder.getOrderMaxSeqNo(caseNo);
            int orderGroupNo = everyPatOrder.getOrderMaxGroupNo(caseNo);// add by wanglong 20130321
            /**
             * ��������
             */
            for (int insertRow : commonInsertRows) {// modify by wanglong 20130226
                TParm parm = order.getRowParm(insertRow);
                String orderCode = parm.getValue("ORDER_CODE");
                if (!orderCode.equals("")) {
                    if (parm.getValue("SETMAIN_FLG").equals("Y")) {
                        orderGroupNo++;
                    }
                    everyPatOrder =
                            addHRMOrder(everyPatOrder, insertRow, orderSeqNo, orderGroupNo, parm,
                                        caseNo, patRow);
                    orderSeqNo++;
                }
            }
            String[] sql = new String[]{};
            sql = StringTool.copyArray(sql, everyPatOrder.getUpdateSQL());
            sql = StringTool.copyArray(sql, everyPatOrder.getMedApply().getUpdateSQL());
            // ���������̨���淽���Ĳ���������֤��̨���淽���ķ���ֵ�Ƿ�ɹ�
            TParm inParm = new TParm();
            Map inMap = new HashMap();
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm result =
                    TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave",
                                                 inParm);
            if (result.getErrCode() != 0) {
                errCount++;
                this.messageBox("���:" + seqNo + "  ������" + patName + " ҽ������ʧ��" + result.getErrText());
                continue;
            }
            if (patRow.getValue("COVER_FLG").equals("Y")) {
                if (flag == true) {// add by wanglong 20130415
                    this.getHl7List(listHl7, caseNo);
                }
            }
        }
        // ����HL7��Ϣ
        if (listHl7.size() > 0) {
            TParm hl7Parm = Hl7Communications.getInstance().Hl7Message(listHl7);// add by wanglong
                                                                                // 20130312
            if (hl7Parm.getErrCode() < 0) {
                errCount = patCount;
                this.messageBox("����HL7��Ϣʧ��" + hl7Parm.getErrText());
            }
        }
        if (errCount > 0) {// add by wanglong 20130403
            onClear();
        }
        if (errCount != patCount) {
            this.messageBox("�����ɹ�");
            this.setReturnValue("SUCCESS");
            this.closeWindow();
        } else {
            onClear();
            return;
        }
    }

    private HRMOrder addHRMOrder(HRMOrder order, int row, int seqNo, int groupNo, TParm parm,
                                 String caseNo, TParm patParm) {// modify by wanglong 20130311
        int row1 = order.rowCount() - 1;// add by wanglong 20130408
        if (row1 < 0) {
            row1 = order.insertRow(-1, caseNo);
            order.setActive(row, false);
        }
        // int row;
        // ���С�ڵ���0δ����
        if (seqNo <= 0) {
            seqNo = order.getMaxSeqNo(caseNo);
        }
        if (!order.getItemString(order.rowCount() - 1, "CASE_NO").equals("")) {// modify by wanglong
                                                                               // 20130408
            // if (!order.getItemString(row, "CASE_NO").equals("")) {//add by wanglong 20130225
            row = order.insertRow();
        }
        // String orderCode = ;
        // row=order.insertRow();
        order.setItem(row, "CASE_NO", caseNo);
        order.setItem(row, "MR_NO", patParm.getValue("MR_NO"));
        order.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
        order.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
        order.setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC"));
        order.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION"));
        order.setItem(row, "SEQ_NO", seqNo);
        order.setItem(row, "ORDERSET_CODE", parm.getValue("ORDERSET_CODE"));
        order.setItem(row, "DISPENSE_QTY", parm.getDouble("DISPENSE_QTY"));
        order.setItem(row, "DISPENSE_UNIT", parm.getValue("DISPENSE_UNIT"));
        order.setItem(row, "ORIGINAL_PRICE", parm.getDouble("OWN_PRICE"));
        order.setItem(row, "OWN_PRICE", parm.getDouble("OWN_PRICE"));
        order.setItem(row, "OPTITEM_CODE", parm.getValue("OPTITEM_CODE"));
        order.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE"));
        order.setItem(row, "OPT_USER", Operator.getID());
        order.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        order.setItem(row, "OPT_TERM", Operator.getIP());
        order.setItem(row, "DR_CODE", Operator.getID());
        order.setItem(row, "ORDER_DATE", TJDODBTool.getInstance().getDBTime());
        order.setItem(row, "DEPT_CODE", parm.getValue("DEPT_CODE"));
        order.setItem(row, "REGION_CODE", Operator.getRegion());
        order.setItem(row, "NHI_PRICE", parm.getData("NHI_PRICE"));// add by wanglong 20130316
        double disCount = 1;// add by wanglong 20130527
        if (parm.getValue("CAT1_TYPE").equals("PHA")// ҩƷ�����Ĳ�����
//                || parm.getValue("ORDER_CAT1_CODE").equals("MAT")//̩�Ĳ����ܴ���
        ) {// modify by wanglong 20130306
            disCount = 1;
        } else {
            disCount = patParm.getDouble("DISCNT");
        }
        order.setItem(row, "DISCOUNT_RATE", disCount);
        double ownAmt = 0.0;
        double arAmt = 0.0;
        ownAmt = StringTool.round(parm.getDouble("OWN_PRICE") * parm.getDouble("DISPENSE_QTY"), 2);
        arAmt = StringTool.round(ownAmt * disCount, 2);// modify by wanglong 20130527
        order.setItem(row, "OWN_AMT", ownAmt);
        order.setItem(row, "AR_AMT", arAmt);
        if (parm.getValue("NEW_FLG").equals("Y")) {// �������ײ���ѡ���ҽ��add by wanglong 20130425
            order.setItem(row, "DISCOUNT_RATE", 1);
            order.setItem(row, "OWN_AMT", ownAmt);
            order.setItem(row, "AR_AMT", ownAmt);
        }
        order.setItem(row, "RPTTYPE_CODE", parm.getData("RPTTYPE_CODE"));
        // OPTITEM_CODE
        order.setItem(row, "OPTITEM_CODE", parm.getData("OPTITEM_CODE"));
        order.setItem(row, "DEV_CODE", parm.getData("DEV_CODE"));
        // MR_CODE
        order.setItem(row, "MR_CODE", parm.getData("MR_CODE"));
        order.setItem(row, "CTZ1_CODE", ctz);
        order.setItem(row, "CTZ2_CODE", "");
        order.setItem(row, "CTZ3_CODE", "");
        order.setItem(row, "HEXP_CODE", parm.getValue("HEXP_CODE"));
        order.setItem(row, "REXP_CODE", parm.getValue("REXP_CODE"));
        order.setItem(row, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE"));
        // CAT1_TYPE
        order.setItem(row, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
        order.setItem(row, "CONTRACT_CODE", patParm.getValue("CONTRACT_CODE"));
        if (StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE"))) {
            // EXEC_DEPT_CODE
            order.setItem(row, "EXEC_DEPT_CODE", Operator.getDept());
        } else {
            // EXEC_DEPT_CODE
            order.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE"));
        }
        order.setItem(row, "DEPT_ATTRIBUTE", parm.getData("DEPT_ATTRIBUTE"));
        order.setItem(row, "SETMAIN_FLG", parm.getData("SETMAIN_FLG"));
        order.setItem(row, "HIDE_FLG", parm.getData("HIDE_FLG"));
        order.setItem(row, "BILL_FLG", parm.getData("BILL_FLG"));
        order.setItem(row, "EXEC_FLG", parm.getData("EXEC_FLG"));
        // order.setItem(row, "ORDERSET_GROUP_NO", groupNo);// add by wanglong 20130321
        // // �����ҩƷ������
        // if ("PHA_W".equals(parm.getValue("ORDER_CAT1_CODE"))
        // || "MAT".equals(parm.getValue("ORDER_CAT1_CODE"))) {
        // order.setItem(row, "ORDERSET_CODE", parm.getData("ORDER_CODE"));
        // }
        boolean isSetMain = parm.getBoolean("SETMAIN_FLG");
        if (isSetMain) {//add by wanglong 20130510
            order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
            order.setItem(row, "ORDERSET_CODE", parm.getData("ORDER_CODE"));
        } else {
            // �����ҩƷ������
            if ("PHA_W".equals(parm.getValue("ORDER_CAT1_CODE"))
                    || ("MAT".equals(parm.getValue("ORDER_CAT1_CODE")) && "N".equals(parm
                            .getValue("HIDE_FLG")))) {
                order.setItem(row, "ORDERSET_GROUP_NO", 0);
                order.setItem(row, "ORDERSET_CODE", parm.getData("ORDER_CODE"));
            } else {
                order.setItem(row, "ORDERSET_GROUP_NO",
                              order.getItemData(row - 1, "ORDERSET_GROUP_NO"));
                order.setItem(row, "ORDERSET_CODE", order.getItemData(row - 1, "ORDERSET_CODE"));
            }
        }
        // �����ҩ�� ����Ҫ����
        // if(!"PHA_W".equals(parm.getValue("ORDER_CAT1_CODE"))){
        // if("Y".equals(parm.getData("SETMAIN_FLG"))){
        // String labMapKey=order.getLabNo(row, patParm);
        // order.setItem(row, "MED_APPLY_NO", labMapKey);
        // }
        // }
        if ("Y".equalsIgnoreCase(parm.getValue("SETMAIN_FLG"))
                && ("LIS".equals(parm.getValue("CAT1_TYPE")) || "RIS".equals(parm
                        .getValue("CAT1_TYPE")))) {// modify by wanglong 20130228
            // System.out.println("=====sysFee===="+sysFee);
            String labNo = order.getLabNo(row, patParm);
            order.setItem(row, "MED_APPLY_NO", labNo);
        }
        order.setActive(row, true);
        // row++;
        return order;
    }

    /**
     * �õ�HL7����
     * 
     * @param listHl7
     *            List
     * @param caseNo
     *            String
     * @return List
     */
    public List getHl7List(List listHl7, String caseNo) {// add by wanglong 20130312
        String sql =
                "SELECT CAT1_TYPE,PAT_NAME,CASE_NO,APPLICATION_NO AS LAB_NO,ORDER_NO,SEQ_NO "
                        + "FROM MED_APPLY WHERE ADM_TYPE='H' AND CASE_NO='" + caseNo
                        + "' AND SEND_FLG < 2 AND STATUS <> 9";
        // System.out.println("SQLMED==" + sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        int rowCount = parm.getCount();
        String preLabNo = "";
        for (int i = 0; i < rowCount; i++) {
            TParm temp = parm.getRow(i);
            String labNo = temp.getValue("LAB_NO");
            // if (!preLabNo.equals(labNo)) {//delete by wanglong 20130402
            temp.setData("ADM_TYPE", "H");
            temp.setData("FLG", "0");
            // System.out.println("PAT_NAME----:" + temp.getValue("PAT_NAME") + ":LAB_NO----:" +
            // temp.getValue("LAB_NO"));
            listHl7.add(temp);
            preLabNo = labNo;
            // }
        }
        // System.out.println("listHl7.size----:" + listHl7.size());
        return listHl7;
    }

    /**
     * ȡ����󼯺�ҽ�����
     * 
     * @return
     */
    public int getMaxGroupNo() {
        String filter = order.getFilter();
        order.setFilter("SETMAIN_FLG='Y'");
        order.filter();
        int count = order.rowCount();
        int no = -1;
        for (int i = 0; i < count; i++) {
            int temp = order.getItemInt(i, "ORDERSET_GROUP_NO");
            if (temp > no) no = temp;
        }
        order.setFilter(filter);
        order.filter();
        return (no + 1);
    }

    /**
     * ����ɾ��
     */
    public void batchDelete() {// refactor by wanglong 20130311
        order.setFilter("");
        order.filter();
        int[] commonInsertRows = order.getNewRows();// ��¼������ҽ�����к� add by wanglong 20130226
        Set<String> orderSet = new HashSet<String>();
        int length = 0;
        for (int i : commonInsertRows) {
            if (order.getItemString(i, "SETMAIN_FLG").equals("Y")) {
                length++;
                orderSet.add(order.getItemString(i, "ORDER_CODE"));
            }
        }
        if (orderSet.size() != length) {
            this.messageBox("�������ظ����ĳ��ҽ�������ң�ÿ��ֻ��ɾ��һ��");
            onClear();
            return;
        }
        int patCount = inParm.getCount("MR_NO");
        if (patCount >= 1000) {
            this.messageBox("ͬʱ��������Ա����Ҫ����1000��");
            return;
        }
        int rightCount = 0;// ��ȷ����
        String orderList = "";// add by wanlong 20130412
        for (int insertRow : commonInsertRows) {
            TParm parm = this.order.getRowParm(insertRow);
            if (parm.getValue("SETMAIN_FLG").equals("Y")) {
                String orderDesc = parm.getValue("ORDER_DESC");
                orderList = orderList + orderDesc + "\r\n";
            }
        }
        String patList = "";// add by wanlong 20130412
        for (int i = 0; i < patCount; i++) {
            TParm patRow = (TParm) inParm.getRow(i);
            String mrNo = patRow.getValue("MR_NO");
            String seqNo = patRow.getValue("SEQ_NO");
            String patName = patRow.getValue("PAT_NAME");
            patList = patList + patName + ",";
            if (StringUtil.isNullString(mrNo)) {
                this.messageBox_("���:" + seqNo + "  ������" + patName + " ������Ϊ��");
                continue;
            }
            String caseNo = "";
            String contractCode = patRow.getValue("CONTRACT_CODE");
            if (patRow.getValue("CASE_NO").length() == 0) {
                caseNo = adm.getLatestCaseNoBy(mrNo, contractCode);
            } else {
                caseNo = patRow.getValue("CASE_NO");
            }
            if (StringUtil.isNullString(caseNo)) {
                this.messageBox_("���:" + seqNo + "  ������" + patName + " ҽ��δչ����������ɾ��");
                continue;
            }
            // ����ѽ��㣬Ӧ��ȡ�����㣬����ɾ��
            String billStateSql =
                    "SELECT DISTINCT BILL_NO FROM HRM_ORDER WHERE MR_NO = '" + mrNo
                            + "' AND CONTRACT_CODE = '" + contractCode
                            + "' AND BILL_NO IS NOT NULL";
            TParm billStateParm = new TParm(TJDODBTool.getInstance().select(billStateSql));
            if (billStateParm.getErrCode() != 0) {
                this.messageBox("���:" + seqNo + "  ������" + patName + " ��ѯ������Ϣ����");
                continue;
            }
            if (billStateParm.getCount("BILL_NO") > 0) {
                this.messageBox("���:" + seqNo + "  ������" + patName + " �ѽ��㣬����ɾ�����ִ��ȡ���������");
                continue;
            }
            /**
             * ����ɾ��
             */
            List hl7ParmDel = new ArrayList();
            String[] sql = new String[]{};
            for (int insertRow : commonInsertRows) {
                TParm parm = order.getRowParm(insertRow);
                String orderCode = parm.getValue("ORDER_CODE");
                if (!StringUtil.isNullString(orderCode)) {
                    contractCode = patRow.getValue("CONTRACT_CODE");
                    String setMainFlg = parm.getValue("SETMAIN_FLG");
                    if (setMainFlg.equalsIgnoreCase("Y")) {
                        // ��ִ�е�ҽ������ɾ��
                        String execFlgSql =
                                "SELECT * FROM HRM_ORDER WHERE MR_NO = '" + mrNo
                                        + "' AND CONTRACT_CODE = '" + contractCode
                                        + "' AND ORDER_CODE = '" + orderCode + "' AND ROWNUM=1";
                        TParm orderParm = new TParm(TJDODBTool.getInstance().select(execFlgSql));
                        if (orderParm.getErrCode() != 0) {
                            this.messageBox("���:" + seqNo + "  ������" + patName
                                    + " ��ѯ����Աҽ��ִ���������������ɾ�����κ�ҽ��");
                            break;
                        }
                        String orderDesc = parm.getValue("ORDER_DESC");
                        if (orderParm.getValue("EXEC_FLG", 0).equals("Y")) {
                            this.messageBox("���:" + seqNo + "  ������" + patName + " " + orderDesc
                                    + " ��ִ�У�����ɾ��");
                            continue;
                        }
                        // ����Hl7��Ϣ
                        if (!StringUtil.isNullString(orderParm.getValue("MED_APPLY_NO", 0))) {// modify
                                                                                              // by
                                                                                              // wanglong
                                                                                              // 20130402
                            TParm delTemp = new TParm();
                            delTemp.setData("ADM_TYPE", "H");
                            delTemp.setData("PAT_NAME", patName);
                            delTemp.setData("CAT1_TYPE", orderParm.getValue("CAT1_TYPE", 0));
                            delTemp.setData("CASE_NO", orderParm.getValue("CASE_NO", 0));
                            delTemp.setData("LAB_NO", orderParm.getValue("MED_APPLY_NO", 0));
                            delTemp.setData("ORDER_NO", orderParm.getValue("CASE_NO", 0));
                            delTemp.setData("SEQ_NO", orderParm.getValue("SEQ_NO", 0));
                            delTemp.setData("FLG", "1");
                            try {
                                if (Hl7Communications.getInstance().IsExeOrder(delTemp, "H")) {
                                    continue;
                                }
                            }
                            catch (Exception ex) {
                                System.err.print("�����ִ���ж�ʧ�ܡ�");
                                ex.printStackTrace();
                            }
                            hl7ParmDel.add(delTemp);
                        }
                        // ɾ��HRM_ORDER��ֻɾ��һ��ƥ�������Ļ���Ҫ���ɾ����
                        String deleteOrderSql = // modify by wanglong 20130408
                                "DELETE FROM HRM_ORDER WHERE ORDERSET_GROUP_NO IN (SELECT MAX(ORDERSET_GROUP_NO) FROM HRM_ORDER "
                                        + " WHERE ORDERSET_CODE = '" + orderCode
                                        + "' AND MR_NO = '" + mrNo + "'" + " AND CONTRACT_CODE = '"
                                        + contractCode + "') AND MR_NO = '" + mrNo
                                        + "' AND CONTRACT_CODE = '" + contractCode
                                        + "' AND BILL_NO IS NULL";
                        sql = StringTool.copyArray(sql, new String[]{deleteOrderSql });
                    }
                }
            }
            TParm hl7Parm = Hl7Communications.getInstance().Hl7Message(hl7ParmDel);
            if (hl7Parm.getErrCode() < 0) {
                this.messageBox("���:" + seqNo + "  ������" + patName + " ɾ��ҽ��ʧ�ܣ�����HL7��Ϣʧ�ܣ�");
                continue;
            }
            TParm inParm = new TParm();
            Map inMap = new HashMap();
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm result =
                    TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave",
                                                 inParm);
            if (result.getErrCode() != 0) {
                this.messageBox("���:" + seqNo + "  ������" + patName + " ҽ��ɾ��ʧ��" + result.getErrText());
                continue;
            }
            rightCount++;
        }
        // add by wanglong 20130412 ������־��¼����
        String now =
                StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss");
        String log = "---------------------------------------------\r\n";
        log = log + "****��������ɾ����־(" + now + ")****\r\n";
        log = log + "������Ա��" + Operator.getName() + "(" + Operator.getID() + ")\r\n";
        log = log + "�����ն˵�ַ��" + Operator.getIP() + "\r\n";
        log = log + "������������룺" + this.inParm.getValue("COMPANY_CODE", 0) + "\r\n";
        log = log + "��������ͬ��" + this.inParm.getValue("CONTRACT_CODE", 0) + "\r\n";
        log = log + "��������Ա��" + patList.substring(0, patList.length() - 1) + "\r\n";
        log = log + "***************ɾ��ҽ���б�******************\r\n";
        log = log + orderList.substring(0, orderList.length() - 1) + "\r\n";
        log = log + "---------------------------------------------\r\n";
        TParm logParm = new TParm();
        logParm.setData("LOG", log);
        TParm parm = TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "writeLog", logParm);
        // add end
        if (rightCount > 0) {
            this.messageBox("ɾ���ɹ�");
            this.setReturnValue("SUCCESS");
            this.closeWindow();
        } else {
            onClear();
            return;
        }
    }

    /**
     * �Ƿ��Ǽ���ҽ��
     * 
     * @param row
     *            int
     * @param buff
     *            String
     * @return boolean
     */
    public boolean isOrderSet(TParm orderParm) {
        boolean falg = false;
        if (orderParm.getBoolean("SETMAIN_FLG")) {
            falg = true;
        }
        return falg;
    }

    /**
     * �õ�TTable
     * 
     * @param tag
     *            String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable) this.getComponent(tag);
    }

    /**
     * ����¼�
     */
    public void onClear() {// add by wanglong 20130224
        order.filt("#");
        table.setDSValue();
        this.clearValue("ORDER_CODE;PACKAGE_CODE");// add by wanglong 20130423
        packageDetail.getPopupMenuData().getData().clear();
        packageDetail.filter();
        initData();
    }
    
    /**
     * �ײ�-ϸ������
     */
    public void onChoosePackage() {// add by wanglong 20130423
        packageCode = this.getValueString("PACKAGE_CODE");
        packageDetail.setPackageCode(packageCode);
        packageDetail.onQuery();
    }
    
    /**
     * ����ײ�ϸ��
     */
    public void onChooseDetail() {// add by wanglong 20130423
        String seq = this.getValueString("ORDER_CODE");
        String sql =
                "SELECT A.ORDER_CODE,A.ORDERSET_CODE,A.ORDER_DESC,B.GOODS_DESC,A.SPECIFICATION,A.DISPENSE_UNIT,A.DISPENSE_QTY,"
                        + "A.ORIGINAL_PRICE,A.PACKAGE_PRICE,A.SETMAIN_FLG,A.HIDE_FLG,A.OPTITEM_CODE,A.EXEC_DEPT_CODE,A.DEPT_ATTRIBUTE,"
                        + "B.NHI_PRICE,B.RPTTYPE_CODE,B.DEV_CODE,B.MR_CODE,B.CHARGE_HOSP_CODE,B.CAT1_TYPE,B.ORDER_CAT1_CODE "
                        + "  FROM HRM_PACKAGED A, SYS_FEE B,"
                        + "(SELECT ORDERSET_GROUP_NO FROM HRM_PACKAGED WHERE SEQ = @ AND PACKAGE_CODE = '#') CC "
                        + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                        + "   AND A.ORDERSET_GROUP_NO = CC.ORDERSET_GROUP_NO "
                        + "   AND A.PACKAGE_CODE = '#' ORDER BY A.SEQ";
        sql = sql.replaceFirst("@", seq);
        sql = sql.replaceFirst("#", packageCode);
        sql = sql.replaceFirst("#", packageCode);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            this.messageBox("��ѯҽ����Ϣʧ�� " + result.getErrText());
            return;
        }
        if (result.getCount() == 0) {
            this.messageBox("ϵͳ�в����ڸ�ҽ��");
            return;
        }
        comboOrderReturn(result);
    }
    
    /**
     * �ײ�ϸ������ҽ������,����ϸ�࣬��Ҳ����ϸ����Ϣ����ֻ��ʾ����
     * @param parm
     */
    public void comboOrderReturn(TParm parm) {// add by wanglong 20130423
        int row = table.getRowCount() - 1;
        if (!StringUtil.isNullString(table.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        table.acceptText();
        double orderSetQty = parm.getDouble("DISPENSE_QTY", 0);
        int groupNo = -1;
        for (int i = 0; i < parm.getCount(); i++) {
            order.setItem(row, "NEW_FLG", "Y");// add by wanglong 20130425
            order.setItem(row, "SEQ", order.getItemInt(row, "#ID#"));
            order.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE", i));
            order.setItem(row, "ORDERSET_CODE", parm.getValue("ORDERSET_CODE", i));
            order.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC", i));
            order.setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC", i));
            order.setItem(row, "DISPENSE_QTY", parm.getDouble("DISPENSE_QTY", i) / orderSetQty);
            order.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION", i));
            order.setItem(row, "DISPENSE_UNIT", parm.getValue("DISPENSE_UNIT", i));
            order.setItem(row, "ORIGINAL_PRICE", parm.getDouble("ORIGINAL_PRICE", i));
            order.setItem(row, "OWN_PRICE", parm.getDouble("PACKAGE_PRICE", i));
            order.setItem(row, "NHI_PRICE", parm.getDouble("NHI_PRICE", i));
            order.setItem(row, "RPTTYPE_CODE", parm.getData("RPTTYPE_CODE", i));
            order.setItem(row, "OPTITEM_CODE", parm.getData("OPTITEM_CODE", i));
            order.setItem(row, "DEV_CODE", parm.getData("DEV_CODE", i));
            order.setItem(row, "MR_CODE", parm.getData("MR_CODE", i));
            order.setItem(row, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE", i));
            order.setItem(row, "REXP_CODE",
                          BIL.getRexpCode(parm.getValue("CHARGE_HOSP_CODE", i), "H"));
            order.setItem(row, "CAT1_TYPE", parm.getValue("CAT1_TYPE", i));
            order.setItem(row, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE", i));
            if (StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE", i))) {
                order.setItem(row, "EXEC_DEPT_CODE", Operator.getDept());
            } else {
                order.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE", i));
            }
            order.setItem(row, "DEPT_ATTRIBUTE", parm.getData("DEPT_ATTRIBUTE", i));
            order.setItem(row, "OPT_USER", Operator.getID());
            order.setItem(row, "OPT_TERM", Operator.getIP());
            order.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
            order.setItem(row, "REGION_CODE", Operator.getRegion());
            order.setItem(row, "DR_CODE", Operator.getID());
            order.setItem(row, "DEPT_CODE", dept);
            order.setItem(row, "SETMAIN_FLG", parm.getData("SETMAIN_FLG", i));
            order.setItem(row, "HIDE_FLG", parm.getData("HIDE_FLG", i));
            order.setItem(row, "CTZ1_CODE", ctz);
            order.setItem(row, "CTZ2_CODE", "");
            order.setItem(row, "CTZ3_CODE", "");
            order.setItem(row, "BILL_FLG", "N");
            order.setItem(row, "EXEC_FLG", "N");
            if (groupNo < 0) {
                groupNo = order.getMaxGroupNo();
            }
            order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
            order.setActive(row, true);
            row = order.insertRow();
            order.setItem(row, "SETMAIN_FLG", "Y");
            order.setItem(row, "HIDE_FLG", "N");
            order.setItem(row, "BILL_FLG", "N");
            order.setItem(row, "EXEC_FLG", "N");
            order.setActive(row, false);
        }
        order.setFilter(" SETMAIN_FLG='Y' ");
        order.filter();
        table.setDSValue();
        table.getTable().grabFocus();
        table.setSelectedRow(table.getRowCount() - 1);
        table.setSelectedColumn(1);
    }
}
