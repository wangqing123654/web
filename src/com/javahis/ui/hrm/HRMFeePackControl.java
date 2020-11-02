package com.javahis.ui.hrm;

import java.awt.Component;
import javax.swing.JOptionPane;
import jdo.hrm.HRMFeePackTool;
import jdo.hrm.HRMPackageD;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 * <p>Title: ��������ײ��趨������</p>
 *
 * <p>Description: ��������ײ��趨������</p>
 *
 * <p>Copyright: javahis 20090922</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMFeePackControl extends TControl{
	//����ϸTABLE
	private TTable mainTable,detailTable;
	//�ײ��������
	private TDataStore main;
	//�ײ�ϸ�����
	private HRMPackageD detail;
	//ɾ����ť�õı�������
	private String tableName;
	//�ײ�����������
	private int mainRow;
	
	/**
	 * ��ʼ���¼�
	 */
	public void onInit() {
		super.onInit();
		//��ʼ���ؼ�
		initComponent();
		//��ʼ������
		initData();
	}
	
	/**
	 * ����¼�
	 */
    public void onClear() {
        mainTable.removeRowAll();
        detailTable.removeRowAll();
        // ��ʼ���ײ�����
        main = new TDataStore();
        main.setSQL(HRMFeePackTool.INIT_PACK_MAIN);
        main.retrieve();
        main.setSort("PACKAGE_DESC ASC");// add by wanglong 20130325
        main.sort();
        // ��ʼ���ײ�ϸ��
        detail = new HRMPackageD();
        detail.setSQL(HRMFeePackTool.INIT_PACK_DETAIL.replace("#", ""));
        detail.retrieve();
        mainTable.setDataStore(main);
        mainTable.setDSValue();
        detailTable.setDataStore(detail);
        detailTable.setDSValue();
        this.setValue("PACKAGE_DESC", "");
    }
    
	/**
	 * ��ʼ���ؼ�
	 */
    private void initComponent() {
        mainTable = (TTable) this.getComponent("MAIN_TABLE");
        // �ײ�����ֵ�ı��¼�
        mainTable.addEventListener("MAIN_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                                   "onMainValueChanged");
        // �ײ�����CHECK_BOX�¼�
        mainTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onMainCheckClicked");
        detailTable = (TTable) this.getComponent("DETAIL_TABLE");
        // �ײ�ϸ������ҽ���¼�
        detailTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                     "onDetailCreateEditComponent");
        // �ײ�ϸ��ֵ�ı��¼�
        detailTable.addEventListener("DETAIL_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                                     "onDetailValueChanged");
    }
    
	/**
	 * ��ʼ������
	 */
    private void initData() {
        main = new TDataStore();
        detail = new HRMPackageD();
        main.setSQL(HRMFeePackTool.INIT_PACK_MAIN);
        main.retrieve();
        main.setSort("PACKAGE_DESC");// add by wanglong 20130224
        main.sort();// add by wanglong 20130224
        mainTable.setDataStore(main);
        mainTable.setDSValue();
        detail.setSQL(HRMFeePackTool.INIT_PACK_DETAIL.replace("#", ""));
        detail.retrieve();
    }
	
	/**
	 * ��TABLE����¼�,����ѡ�����ײʹ��룬��ʼ���ײ�ϸ����Ϣ
	 */
    public void onMainClick() {
        tableName = "MAIN_TABLE";
        int row = mainTable.getSelectedRow();
        String packCode = main.getItemString(row, "PACKAGE_CODE");
        main.setActive(row, false);
        detail.setSQL(HRMFeePackTool.INIT_PACK_DETAIL.replace("#", packCode));
        detail.retrieve();
        detail.setFilter("PACKAGE_CODE='" + packCode + "' AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='')");
        detail.filter();
        String temp = detail.getItemString(detail.rowCount() - 1, "ORDER_CODE");
        if (!StringUtil.isNullString(temp) || detail.rowCount() < 1) {
            row = detail.insertRow();
            detail.setItem(row, "PACKAGE_CODE", packCode);
            detail.setItem(row, "SEQ", detail.getItemInt(row, "#ID#"));
            detail.setItem(row, "CHECK_SEQ", null);
            detail.setItem(row, "OPT_USER", Operator.getID());
            detail.setItem(row, "OPT_TERM", Operator.getIP());
            detail.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
            detail.setActive(row, false);
        }
        detailTable.setDataStore(detail);
        detailTable.setDSValue();
        mainRow = mainTable.getSelectedRow();
    }
    
	/**
	 * ϸTABLE����¼�
	 */
    public void onDetailClick() {
        tableName = "DETAIL_TABLE";
    }
	
	 /**
     * �һ�MENU�����¼�
     * @param tableName
     */
    public void showPopMenu() {
        detailTable.setPopupMenuSyntax("��ʾ����ҽ��ϸ��,onOrderSetShow");
    }
    
    /**
     * �޸�ҽ��ϸ�࣬�ײ�ϸ��TABLE�һ��¼�������ϸ���б������޸�ϸ����Ϣ
     */
    public void onOrderSetShow() {
        TParm parm = new TParm();
        int row = detailTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        String filterString = detail.getFilter();
        String orderSetCode = detail.getItemString(row, "ORDERSET_CODE");
        int groupNo = detail.getItemInt(row, "ORDERSET_GROUP_NO");
        if (StringUtil.isNullString(orderSetCode)) {
            return;
        }
        String packCode = main.getItemString(this.mainRow, "PACKAGE_CODE");
        parm.setData("PACKAGE_CODE", packCode);
        parm.setData("ORDERSET_CODE", orderSetCode);
        parm.setData("ORDERSET_GROUP_NO", groupNo);
        Object obj = this.openDialog("%ROOT%\\config\\hrm\\HRMOrderSetShow.x", parm);
        // ============xueyf modify 20120228 start
        // ��������ײ���ϸδ����ʱ�鿴����ҽ��ϸ��ر�ϸ��ں�����ײ���ϸ�����BUG
        if (obj == null) {
            return;
        }
        // ============xueyf modify 20120228 stop
        detail.setSQL(HRMFeePackTool.INIT_PACK_DETAIL.replace("#", packCode));
        detail.retrieve();
        detail.setFilter(filterString);
        detail.filter();
        String temp = detail.getItemString(detail.rowCount() - 1, "ORDER_CODE");
        if (!StringUtil.isNullString(temp) || detail.rowCount() < 1) {
            row = detail.insertRow();
            detail.setItem(row, "PACKAGE_CODE", packCode);
            detail.setItem(row, "SEQ", detail.getItemInt(row, "#ID#"));
            detail.setItem(row, "OPT_USER", Operator.getID());
            detail.setItem(row, "OPT_TERM", Operator.getIP());
            detail.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
            detail.setActive(row, false);
        }
        detailTable.setDSValue();
        main.setItem(mainRow, "TC_TOT_AMT", detail.getPackgeTotAmt());// ԭ��
        main.setItem(mainRow, "TC_AR_AMT", detail.getPackgeArAmt());// �ײͼ�
        main.setActive(mainRow, true);
        mainTable.setDSValue();
    }
    
	/**
	 * ����ֵ�ı��¼�,�ײ������ײ������޸ģ��Զ����ɼ�ƴ���뱣�档
	 * @param tNode
	 */
    public boolean onMainValueChanged(TTableNode tNode) {
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String colName = mainTable.getParmMap(column);
        // String packCode = main.getItemString(row, "PACKAGE_CODE");
        if ("PACKAGE_DESC".equalsIgnoreCase(colName)) {
            String packDesc = tNode.getValue() + "";
            main.setItem(row, "PY1", SystemTool.getInstance().charToCode(packDesc));
            main.setActive(row, true);
            return false;
        } else {
            main.setItem(row, colName, tNode.getValue() + "");
            main.setActive(row, true);
        }
        return false;
    }
    
	/**
	 * �ײ������CHECK_BOX�¼�
	 * @param obj
	 * @return
	 */
    public boolean onMainCheckClicked(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        table.setDSValue();
        return false;
    }
	
	/**
	 * ���SYS_FEE��������
	 * @param com
	 * @param row
	 * @param column
	 */
    public void onDetailCreateEditComponent(Component com, int row, int column) {
        // �����ǰ�кţ�ֻ������ҽ������������һ��ҽ��
        column = detailTable.getColumnModel().getColumnIndex(column);
        String columnName = detailTable.getParmMap(column);
        if (!"ORDER_DESC".equalsIgnoreCase(columnName)) {
            return;
        }
        if (!(com instanceof TTextField)) {
            return;
        }
        // String packCode = main.getItemString(this.mainRow, "PACKAGE_CODE");
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        TParm parm = new TParm();
        parm.setData("HRM_TYPE", "ANYCHAR");
        // ��table�ϵ���text����sys_fee��������
        textfield.setPopupMenuParameter("ORDER",
                                        getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
                                        parm);
        // ����text���ӽ���sys_fee�������ڵĻش�ֵ
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popOrderReturn");
    }
    
	/**
	 * �ײ�ϸ������ҽ������,����ϸ�࣬��Ҳ����ϸ����Ϣ����ֻ��ʾ����
	 * @param tag
	 * @param obj
	 */
    public void popOrderReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        // �ж��Ƿ���ҩƷ(PHA_WҩƷ)
        String orderCat1Code = parm.getValue("ORDER_CAT1_CODE");
        int row = detailTable.getSelectedRow();
        int oldRow = row;
        if (!StringUtil.isNullString(detail.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        String order_code = parm.getValue("ORDER_CODE");
        String order_desc = parm.getValue("ORDER_DESC");
        // �ж��Ƿ����ظ�����
        for (int i = 0; i < detailTable.getDataStore().rowCount(); i++) {
            if (i == detailTable.getSelectedRow()) {
                continue;
            }
            if (order_code.equals(detailTable.getDataStore().getItemData(i, "ORDER_CODE"))
                    && detailTable.getDataStore().getItemData(i, "SETMAIN_FLG").equals("Y")) {
                this.messageBox(order_desc + "(" + order_code + ") �Ѵ���");
                return;
            }
        }
        detailTable.acceptText();
        detail.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
        detail.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
        detail.setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC"));
        detail.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION"));
        detail.setItem(row, "SEQ", detail.getItemInt(row, "#ID#"));
        detail.setItem(row, "ORDERSET_CODE", parm.getValue("ORDERSET_CODE"));
        detail.setItem(row, "DISPENSE_QTY", 1.0);
        detail.setItem(row, "DISPENSE_UNIT", parm.getValue("UNIT_CODE"));
        detail.setItem(row, "ORDER_CAT1_CODE", orderCat1Code);// modify by wanglong 20130523
        if ("PHA_W".equals(orderCat1Code) || "MAT".equals(orderCat1Code)) {
            // detail.setItem(row, "ORDER_CAT1_CODE", orderCat1Code);
            detail.setItem(row, "MEDI_QTY", 1.0);
            detail.setItem(row, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
            detail.setItem(row, "FREQ_CODE", "STAT");
            detail.setItem(row, "ROUTE_CODE", "PO");
            detail.setItem(row, "TAKE_DAYS", 1);
            detail.setItem(row, "ORDERSET_CODE", parm.getValue("ORDER_CODE"));
            detail.setItem(row, "SETMAIN_FLG", "Y");
        }
        detail.setItem(row, "ORIGINAL_PRICE", parm.getValue("OWN_PRICE"));
        detail.setItem(row, "PACKAGE_PRICE", parm.getValue("OWN_PRICE"));
        detail.setItem(row, "OPTITEM_CODE", parm.getValue("OPTITEM_CODE"));
        detail.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE"));
        detail.setItem(row, "OPT_USER", Operator.getID());
        detail.setItem(row, "OPT_TERM", Operator.getIP());
        detail.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        detail.setActive(row, true);
        row = detail.insertRow();
        detail.setItem(row, "PACKAGE_CODE", detail.getItemString(0, "PACKAGE_CODE"));
        detail.setActive(row, false);
        if (!parm.getBoolean("ORDERSET_FLG")) {
            double totAmt = detail.getPackgeTotAmt();// ԭ��
            double arAmt = detail.getPackgeArAmt();// �ײͼ�
            main.setItem(mainRow, "TC_TOT_AMT", totAmt);
            main.setItem(mainRow, "TC_AR_AMT", arAmt);
            main.setActive(mainRow, true);
            mainTable.setDSValue();
            detailTable.getTable().grabFocus();
            detailTable.setSelectedRow(oldRow);
            detailTable.setDSValue();
        } else {
            String sql = HRMFeePackTool.QUERY_ORDERSET.replace("#", parm.getValue("ORDER_CODE"));
            // System.out.println("sql===ffff========"+sql);
            detail.setItem(oldRow, "ORDERSET_CODE", parm.getValue("ORDER_CODE"));
            detail.setItem(oldRow, "SETMAIN_FLG", "Y");
            int groupNo = detail.getMaxGroupNo();
            detail.setItem(oldRow, "ORDERSET_GROUP_NO", groupNo);
            TParm orderSet = new TParm(TJDODBTool.getInstance().select(sql));
            if (orderSet == null || orderSet.getErrCode() != 0) {
                this.messageBox_("ѡ�����ݴ���");
                return;
            }
            int count = orderSet.getCount("ORDER_CODE");
            for (int i = 0; i < count; i++) {
                detail.setItem(row, "ORDER_CODE", orderSet.getValue("ORDER_CODE", i));
                detail.setItem(row, "SEQ", detail.getItemInt(row, "#ID#"));
                detail.setItem(row, "ORDER_DESC", orderSet.getValue("ORDER_DESC", i));
                detail.setItem(row, "GOODS_DESC", orderSet.getValue("GOODS_DESC", i));
                detail.setItem(row, "SPECIFICATION", orderSet.getValue("SPECIFICATION", i));
                detail.setItem(row, "ORDERSET_CODE", orderSet.getValue("ORDERSET_CODE", i));
                detail.setItem(row, "DISPENSE_QTY", orderSet.getDouble("DOSAGE_QTY", i));
                detail.setItem(row, "DISPENSE_UNIT", orderSet.getValue("UNIT_CODE", i));
                detail.setItem(row, "ORIGINAL_PRICE", orderSet.getDouble("OWN_PRICE", i));
                detail.setItem(row, "PACKAGE_PRICE", orderSet.getDouble("OWN_PRICE", i));
                detail.setItem(row, "OPTITEM_CODE", orderSet.getValue("OPTITEM_CODE", i));
                detail.setItem(row, "ORDER_CAT1_CODE", orderSet.getValue("ORDER_CAT1_CODE", i));// add by wanglong 20130523
                detail.setItem(row, "SETMAIN_FLG", "N");
                detail.setItem(row, "HIDE_FLG", "Y");
                detail.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE"));
                detail.setItem(row, "ORDERSET_GROUP_NO", groupNo);
                detail.setItem(row, "OPT_USER", Operator.getID());
                detail.setItem(row, "OPT_TERM", Operator.getIP());
                detail.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
                detail.setActive(row, true);
                row = detail.insertRow();
                detail.setItem(row, "PACKAGE_CODE", detail.getItemString(0, "PACKAGE_CODE"));
                detail.setActive(row, false);
            }
            String packCode = main.getItemString(this.mainRow, "PACKAGE_CODE");
            detail.setFilter("PACKAGE_CODE='" + packCode
                    + "' AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='')");
            detail.filter();
            // System.out.println("after pop");
            detailTable.setDSValue();
            double totAmt = detail.getPackgeTotAmt();// ԭ��
            double arAmt = detail.getPackgeArAmt();// �ײͼ�
            main.setItem(mainRow, "TC_TOT_AMT", totAmt);
            main.setItem(mainRow, "TC_AR_AMT", arAmt);
            main.setActive(mainRow, true);
            mainTable.setDSValue();
            detailTable.getTable().grabFocus();
            detailTable.setSelectedRow(detailTable.getRowCount() - 1);
            detailTable.setSelectedColumn(1);
        }
        parm = null;
    }

	/**
	 * ϸ��ֵ�ı��¼�
	 * @param tNode
	 */
    public boolean onDetailValueChanged(TTableNode tNode) {
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String packCode = detail.getItemString(row, "PACKAGE_CODE");
        String colName = tNode.getTable().getParmMap(column);
        if (!StringUtil.isNullString(detail.getItemString(row, "ORDER_CODE"))) {
            if ("ORDER_DESC".equalsIgnoreCase(colName)) {
                this.messageBox_("�ѿ���ҽ�������ظ�����");
                return true;
            }
        } else {
            return true;
        }
        if ("DEPT_ATTRIBUTE".equalsIgnoreCase(colName)) {
            String orderCode = detail.getItemString(row, "ORDER_CODE");
            int groupNo = detail.getItemInt(row, "ORDERSET_GROUP_NO");
            String value = tNode.getValue() + "";
            // System.out.println();
            detail.updateDeptAttribute(value, packCode, orderCode, groupNo);
            detailTable.acceptText();
            return false;
        }
        if ("PACKAGE_PRICE_MAIN".equalsIgnoreCase(colName)) {
            double price = StringTool.getDouble(tNode.getValue() + "");
            if (price < 0) {
                return true;
            }
            double oldPrice = StringTool.getDouble(tNode.getOldValue() + "");
            double nowPrice = main.getItemDouble(mainRow, "TC_TOT_AMT") - oldPrice + price;
            main.setItem(this.mainRow, "TC_TOT_AMT", nowPrice);
            main.setActive(row, true);
            mainTable.setDSValue(mainRow);
            return false;
        }
        if ("CHECK_SEQ".equalsIgnoreCase(colName)) {
            if (detail.getItemString(tNode.getRow(), "ORDER_CODE").length() == 0) {
                return true;
            }
        }
        if ("DISPENSE_QTY".equalsIgnoreCase(colName)) {// add by wanglong 20130523
            String orderCat1Code = detail.getItemString(tNode.getRow(), "ORDER_CAT1_CODE");
            if (!orderCat1Code.matches("PHA.*") && !"MAT".equals(orderCat1Code)) {
                this.messageBox("����ҽ�����ܸ�������");
                return true;
            }
        }
        detail.setActive(tNode.getRow(), true);
        return false;
    }
    
	/**
	 * ����һ���ײ���������
	 */
    public void onNew() {
        if (main.rowCount() > 0
                && StringUtil.isNullString(main.getItemString(main.rowCount() - 1, "PACKAGE_DESC"))) {
            return;
        }
        String packCode = HRMFeePackTool.getInstance().getNewPackCode();
        if (StringUtil.isNullString(packCode)) {
            this.messageBox_("ȡ���ײͱ���ʧ��");
            return;
        }
        int row = mainTable.addRow();
        mainTable.setSelectedRow(row);
        main.setItem(row, "OPT_USER", Operator.getID());
        main.setItem(row, "OPT_TERM", Operator.getIP());
        main.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        main.setItem(row, "PACKAGE_CODE", packCode);
        // ===================add by wanglong 20130408
        String sql =
                "SELECT SUBCLASS_CODE AS ID,SUBCLASS_DESC AS NAME,PY1 FROM EMR_TEMPLET "
                        + " WHERE HRM_FLG='Y' AND SYSTEM_CODE='HRM' AND SUBCLASS_DESC LIKE '%�ܼ�%'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            this.messageBox("��ѯ�ܼ�ģ����Ϣ����");
            return;
        }
        main.setItem(row, "TOT_MR_CODE", result.getValue("ID", 0));
        // ===================add end
        main.setItem(row, "ACTIVE_FLG", "Y");
        main.setActive(row, false);
        mainTable.setDSValue();
    }
    
	/**
	 * ɾ��һ������
	 */
    public void onDelete() {
        String packCode = main.getItemString(mainRow, "PACKAGE_CODE");
        // this.messageBox_(tableName);
        if ("MAIN_TABLE".equalsIgnoreCase(tableName)) {
            if (this.messageBox("��ʾ��Ϣ", "ȷ��ɾ����", JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            main.setActive(mainRow, true);
            main.deleteRow(mainRow);
            String[] sql = main.getUpdateSQL();
            detail.setFilter("PACKAGE_CODE='" + packCode + "'");
            detail.filter();
            int count = detail.rowCount();
            for (int i = count - 1; i > -1; i--) {
                detail.deleteRow(i);
            }
            String[] detailSql = detail.getUpdateSQL();
            sql = StringTool.copyArray(sql, detailSql);
            TParm result = new TParm(TJDODBTool.getInstance().update(sql));
            if (result.getErrCode() != 0) {
                // this.messageBox_(result.getErrText());
                return;
            }
            detail.resetModify();
            main.resetModify();
            mainTable.setDSValue();
            detailTable.setDSValue();
            return;
        }
        int row = -1;
        row = detailTable.getSelectedRow();
        if (StringUtil.isNullString(detail.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        if (!TypeTool.getBoolean(detail.getItemData(row, "SETMAIN_FLG"))) {
            detail.deleteRow(row);
        } else {
            String orderSetCode = detail.getItemString(row, "ORDERSET_CODE");
            int groupNo = detail.getItemInt(row, "ORDERSET_GROUP_NO");
            String filterString = detail.getFilter();
            detail.setFilter("PACKAGE_CODE='" + packCode + "'");
            detail.filter();
            int count = detail.rowCount();
            for (int i = count - 1; i > -1; i--) {
                if (orderSetCode.equalsIgnoreCase(detail.getItemString(i, "ORDERSET_CODE"))
                        && groupNo == detail.getItemInt(i, "ORDERSET_GROUP_NO")) {
                    detail.deleteRow(i);
                }
            }
            detail.setFilter(filterString);
            detail.filter();
        }
        String[] sql = detail.getUpdateSQL();
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() != 0) {
            this.messageBox_(result.getErrText());
            return;
        }
        detail.resetModify();
        detail.setFilter("PACKAGE_CODE='" + packCode + "' AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='')");
        detail.filter();
        detailTable.setDSValue();
        double totAmt = detail.getPackgeTotAmt();// ԭ��
        double arAmt = detail.getPackgeArAmt();// �ײͼ�
        main.setItem(mainRow, "TC_TOT_AMT", totAmt);
        main.setItem(mainRow, "TC_AR_AMT", arAmt);
        main.setActive(mainRow, true);
        mainTable.setDSValue();
    }
    
	/**
	 * ����
	 */
    public void onSave() {
        mainTable.acceptText();
        detailTable.acceptText();
        for (int i = 0; i < detail.rowCount(); i++) {
            String orderCode = detail.getItemString(i, "ORDER_CODE");
            if (orderCode == null || orderCode.equals("")) {
                detail.setActive(i, false);
            }
        }
        String[] sql = main.getUpdateSQL();
        String[] detailSql = detail.getUpdateSQL();
        sql = StringTool.copyArray(sql, detailSql);
        if (sql == null || sql.length < 1) {
            this.messageBox_("û���ҵ����������");
            return;
        }
        TParm result;
        for (int i = 0; i < sql.length; i++) {
            // System.out.println("sql============" + sql[i]);
            result = new TParm(TJDODBTool.getInstance().update(sql[i]));
            if (result.getErrCode() != 0) {
                this.messageBox("E0001");// ����ʧ��
                return;
            }
        }
        this.messageBox("P0001");// ����ɹ�
        onClear();
        return;
    }
    
	/**
	 * ģ�����Ʋ�ѯ�¼�
	 */
    public void onQueryByCode() {
        String packCode = this.getValueString("PACKAGE_DESC");
        main.setFilter("PACKAGE_CODE='" + packCode + "'");
        main.filter();
        mainTable.setDSValue();
        detailTable.removeRowAll();
    }
	
    
    //=====================  chenxi modify 20130219 ��Ӹ��ƹ���
    /**
     * ����
     */
    public void onNewCopy() {
        int selectRow = mainTable.getSelectedRow();
        if (selectRow < 0) {
            messageBox("ѡ��Ҫ���Ƶ�����");
            return;
        }
        // ȡ���������ֵ PACKAGE_CODE;PACKAGE_DESC;TC_TOT_AMT
        String packCode = mainTable.getItemString(selectRow, "PACKAGE_CODE");
        String packageDesc = mainTable.getItemString(selectRow, "PACKAGE_DESC");
        this.onNew();
        int addRow = mainTable.getRowCount() - 1;
        String packageCode = mainTable.getItemString(addRow, "PACKAGE_CODE");
        // ��������
        mainTable.setItem(addRow, 2, "����" + packageDesc);
        main.setItem(addRow, "TC_TOT_AMT", mainTable.getItemDouble(selectRow, "TC_TOT_AMT"));
        main.setItem(addRow, "TC_AR_AMT", mainTable.getItemDouble(selectRow, "TC_AR_AMT"));// add by wanglong 20130523
        main.setItem(addRow, "PY1", SystemTool.getInstance().charToCode("����" + packageDesc));
        main.setActive(addRow, true);
        // ϸ����
        String sql = "SELECT *  FROM HRM_PACKAGED WHERE PACKAGE_CODE ='" + packCode + "' ";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        for (int i = 0; i < parm.getCount(); i++) {
            int row = detail.insertRow();
            detail.setItem(row, "PACKAGE_CODE", packageCode);
            detail.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE", i));
            detail.setItem(row, "SEQ", parm.getInt("SEQ", i));
            detail.setItem(row, "ORDERSET_CODE", parm.getValue("ORDERSET_CODE", i));
            detail.setItem(row, "DISPENSE_QTY", parm.getDouble("DISPENSE_QTY", i));
            detail.setItem(row, "ORIGINAL_PRICE", parm.getDouble("ORIGINAL_PRICE", i));
            detail.setItem(row, "PACKAGE_PRICE", parm.getDouble("PACKAGE_PRICE", i));
            detail.setItem(row, "DISPENSE_UNIT", parm.getValue("DISPENSE_UNIT", i));
            detail.setItem(row, "OPTITEM_CODE", parm.getValue("OPTITEM_CODE", i));
            detail.setItem(row, "OPT_USER", Operator.getID());
            detail.setItem(row, "OPT_TERM", Operator.getIP());
            detail.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
            detail.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE", i));
            detail.setItem(row, "ORDERSET_GROUP_NO", parm.getInt("ORDERSET_GROUP_NO", i));
            detail.setItem(row, "SETMAIN_FLG", parm.getValue("SETMAIN_FLG", i));
            detail.setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC", i));
            detail.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC", i));
            detail.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION", i));
            detail.setItem(row, "DEPT_ATTRIBUTE", parm.getValue("DEPT_ATTRIBUTE", i));
            detail.setItem(row, "CHECK_SEQ", parm.getInt("CHECK_SEQ", i));
            detail.setItem(row, "HIDE_FLG", parm.getValue("HIDE_FLG", i));
            detail.setItem(row, "MEDI_QTY", parm.getDouble("MEDI_QTY", i));
            detail.setItem(row, "MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
            detail.setItem(row, "FREQ_CODE", parm.getValue("FREQ_CODE", i));
            detail.setItem(row, "ROUTE_CODE", parm.getValue("ROUTE_CODE", i));
            detail.setItem(row, "TAKE_DAYS", parm.getInt("TAKE_DAYS", i));
            detail.setItem(row, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE", i));
            detail.setActive(row, true);
        }
        this.onSaveCopy();
    }

	/**
	 * ���Ʊ���
	 */
    public void onSaveCopy() {
        mainTable.acceptText();
        detailTable.acceptText();
        for (int i = 0; i < detail.rowCount(); i++) {
            String orderCode = detail.getItemString(i, "ORDER_CODE");
            if (orderCode == null || orderCode.equals("")) {
                detail.setActive(i, false);
            }
        }
        String[] sql = main.getUpdateSQL();
        String[] detailSql = detail.getUpdateSQL();
        sql = StringTool.copyArray(sql, detailSql);
        if (sql == null || sql.length < 1) {
            this.messageBox_("û���ҵ����Ƶ�����");
            return;
        }
        TParm result;
        for (int i = 0; i < sql.length; i++) {
            result = new TParm(TJDODBTool.getInstance().update(sql[i]));
            if (result.getErrCode() != 0) {
                this.messageBox("����ʧ��");
                return;
            }
        }
        this.messageBox("���Ƴɹ�");
        onClear();
        return;
    }
	//================  add by chenxi end
}
