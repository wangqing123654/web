package com.javahis.ui.ind;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Vector;

import jdo.ind.INDSQL;
import jdo.ind.INDTool;
import jdo.ind.IndStockMTool;
import jdo.ind.IndSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.IndRequestObserver;
import com.javahis.system.textFormat.TextFormatINDOrg;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ������ҵ����ҩ����Control</p>
 *
 * <p>Description: ������ҵ����ҩ����Control</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangy 2009.05.23
 * @version 1.0
 */
public class INDRequestDepTecControl
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

    // ҳ���Ϸ������б�
    private String[] pageItem = {
        "REQUEST_DATE", "REQUEST_NO", "APP_ORG_CODE",
        "REQTYPE_CODE", "TO_ORG_CODE", "REASON_CHN_DESC", "DESCRIPTION",
        "URGENT_FLG", "UNIT_TYPE"};

    // ϸ�����
    private int seq;

    // ���뵥��
    private String request_no;

    // ��������
    private String request_type;

    // ȫ������Ȩ��
    private boolean dept_flg = true;

    public INDRequestDepTecControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ע�ἤ��SYS_FEE�������¼�
        getTable("TABLE_D").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
                                             this, "onCreateEditComoponentUD");
        // ��������¼�
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // ��ʼ����������
        initPage();
    }

    /**
     * ��շ���
     */
    public void onClear() {
        getRadioButton("UPDATE_FLG_A").setSelected(true);
        String clearString =
            "START_DATE;END_DATE;REQUEST_DATE;APP_ORG_CODE;REQUEST_NO;" +
            "REQTYPE_CODE;TO_ORG_CODE;REASON_CHN_DESC;DESCRIPTION;" +
            "URGENT_FLG;SUM_RETAIL_PRICE";
        this.clearValue(clearString);
        Timestamp date = SystemTool.getInstance().getDate();
        setValue("REQUEST_DATE", date);
        //getComboBox("REQTYPE_CODE").setSelectedIndex(0);
        onChangeRequestType();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        // ����״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        getComboBox("REQTYPE_CODE").setEnabled(true);
        getTextField("REQUEST_NO").setEnabled(true);
        getTextFormat("APP_ORG_CODE").setEnabled(true);
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();
        action = "insertM";
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();

        // ������ѯ
        TParm result = TIOM_AppServer.executeAction(
            "action.ind.INDRequestAction", "onQueryM", onQueryParm());
        // ����״̬��������
        result = onFilterQueryByStatus(result);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("�޲�ѯ���");
        }
        else {
            table_m.setParmValue(result);
        }
    }

    /**
     * ���淽��
     */
    public void onSave() {
        // ���������
        TParm parm = new TParm();
        // ���ؽ����
        TParm result = new TParm();
        if (!"updateD".equals(action)) {
            if (!CheckDataM()) {
                return;
            }
            // ���������Ϣ
            parm = getRequestMParm(parm);
            if ("insertM".equals(action)) {
                // ���쵥��
                request_no = SystemTool.getInstance().getNo("ALL", "IND",
                    "IND_REQUEST", "No");
                parm.setData("REQUEST_NO", request_no);
                // ִ����������
                result = TIOM_AppServer.executeAction(
                    "action.ind.INDRequestAction", "onInsertM", parm);
            }
            else {
                parm.setData("REQUEST_NO", request_no);
                // ִ�����ݸ���
                result = TIOM_AppServer.executeAction(
                    "action.ind.INDRequestAction", "onUpdateM", parm);
            }
            // ������ж�
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            setValue("REQUEST_NO", request_no);
            this.messageBox("P0001");
            onQuery();
        }
        else {
            if (!CheckDataD()) {
                return;
            }
            // ��Ӧ���ſ�漰ҩƷ�������ж�
            if (!getOrgStockCheck()) {
                return;
            }

            Timestamp date = SystemTool.getInstance().getDate();
            table_d.acceptText();
            TDataStore dataStore = table_d.getDataStore();
            // ���ȫ���Ķ����к�
            int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
            // ���ȫ����������
            int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);
            // ���̶�����������
            Vector vct = new Vector();
            for (int i = 0; i < newrows.length; i++) {
                dataStore.setItem(newrows[i], "REQUEST_NO",
                                  getValueString("REQUEST_NO"));
                dataStore.setItem(newrows[i], "SEQ_NO", seq + i);
                dataStore.setItem(newrows[i], "UPDATE_FLG", "0");
                vct.add(dataStore.getItemString(newrows[i], "ORDER_CODE"));
            }
            for (int i = 0; i < rows.length; i++) {
                dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
                dataStore.setItem(rows[i], "OPT_DATE", date);
                dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
            }

            TParm inParm = new TParm();
            inParm.setData("ORG_CODE", this.getValueString("APP_ORG_CODE"));
            inParm.setData("ORDER_CODE_LIST", vct);
            inParm.setData("OPT_USER", Operator.getID());
            inParm.setData("OPT_DATE", date);
            inParm.setData("OPT_TERM", Operator.getIP());
            inParm.setData("REGION_CODE", Operator.getRegion());
            inParm.setData("UPDATE_SQL", dataStore.getUpdateSQL());
            inParm.setData("REGION_CODE", Operator.getRegion());//========pangben modify 20110621
            // ִ����������
            result = TIOM_AppServer.executeAction(
                "action.ind.INDRequestAction", "onUpdateD", inParm);
            // ������ж�
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            messageBox("P0001");
            table_d.setDSValue();
            // ���ô�ӡ����
            if (getRadioButton("UPDATE_FLG_A").isSelected()) {
                this.onPrint();
            }
            return;
        }
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            this.messageBox("��������ɲ���ɾ��");
        }
        else {
            if (row_m > -1) {
                if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����뵥", 2) == 0) {
                    TParm parm = new TParm();
                    // ϸ����Ϣ
                    if (table_d.getRowCount() > 0) {
                        table_d.removeRowAll();
                        String deleteSql[] = table_d.getDataStore()
                            .getUpdateSQL();
                        parm.setData("DELETE_SQL", deleteSql);
                    }
                    // ������Ϣ
                    parm.setData("REQUEST_NO", request_no);
                    TParm result = new TParm();
                    result = TIOM_AppServer.executeAction(
                        "action.ind.INDRequestAction", "onDeleteM", parm);
                    // ɾ���ж�
                    if (result.getErrCode() < 0) {
                        this.messageBox("ɾ��ʧ��");
                        return;
                    }
                    table_m.removeRow(row_m);
                    this.messageBox("ɾ���ɹ�");
                    onClear();
                }
            }
            else {
                if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����뵥ϸ��", 2) == 0) {
                	//luhai modify 2012-05-11 begin
                	//�Ѿ������ϸ�����ɾ��  
                	String requestNo = table_d.getDataStore().getItemString(row_d,"REQUEST_NO");
                	String seqNo = table_d.getDataStore().getItemString(row_d,"SEQ_NO");
                	if(!"".equals(requestNo)){
                		if(checkIsDispense(requestNo,seqNo)){
                			this.messageBox("������ϸ���Ѿ����⣬�޷�ɾ����");
                			return;
                		}
                	}
                	//luhai modify 2012-05-11 end  
                	
                    if ("".equals(table_d.getDataStore().getItemString(row_d,
                        "REQUEST_NO"))) {
                        table_d.removeRow(row_d);
                        return;
                    }
                    table_d.removeRow(row_d);
                    // ϸ����ж�
                    if (!table_d.update()) {
                        messageBox("E0001");
                        return;
                    }
                    messageBox("P0001");
                    table_d.setDSValue();
                    //onQuery();
                }
            }
        }
    }
    /**
     * �ж�ϸ���Ƿ���� 
     * @return
     */
    private boolean  checkIsDispense(String requestNo,String seqNo ){
    	StringBuffer sqlbf = new StringBuffer();
    	sqlbf.append(" SELECT  * ");
    	sqlbf.append(" FROM IND_DISPENSEM M,IND_DISPENSED D  ");
    	sqlbf.append(" WHERE M.DISPENSE_NO=D.DISPENSE_NO  ");
    	sqlbf.append(" AND M.REQUEST_NO='"+requestNo+"' ");
    	sqlbf.append(" AND D.REQUEST_SEQ="+seqNo );
    	Map select = TJDODBTool.getInstance().select(sqlbf.toString());
    	TParm resultParm = new TParm(select);
    	if(resultParm.getCount()<=0){
    		return false;
    	}else{
    		return true;
    	}
    }
    /**
     * ��ӡ����
     */
    public void onPrint() {
        Timestamp datetime = SystemTool.getInstance().getDate();
        TTable table_d = getTable("TABLE_D");
        if ("".equals(this.getValueString("REQUEST_NO"))) {
            this.messageBox("���������뵥");
            return;
        }
        if (table_d.getRowCount() > 0) {
            // ��ӡ����
            TParm date = new TParm();
            // ��ͷ����
            date.setData("TITLE", "TEXT", Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         this.getComboBox("REQTYPE_CODE").getSelectedName() +
                         "��");
            date.setData("ORG_CODE_OUT", "TEXT", "��������: " +
                         this.getTextFormat("TO_ORG_CODE").getText());
            date.setData("ORG_CODE_IN", "TEXT", "���벿��: " +
                         this.getTextFormat("APP_ORG_CODE").getText());
            date.setData("REQ_NO", "TEXT",
                         "���뵥��: " + this.getValueString("REQUEST_NO"));
            date.setData("REQ_TYPE", "TEXT",
                         "�������: " +
                         this.getComboBox("REQTYPE_CODE").getSelectedName());
            date.setData("DATE", "TEXT",
                         "�Ʊ�����: " +
                         datetime.toString().substring(0, 10).replace('-', '/'));
            //����ע��, �����뵥Ϊ����ʱ,�����쵥����ʾ������ע��(URGENT) modify ZhenQin 2011-06-01
            TCheckBox URGENT_FLG = (TCheckBox)this.getComponent("URGENT_FLG");
            date.setData("URGENT", "TEXT", URGENT_FLG.isSelected() ? "��" : "");


            // �������
            TParm parm = new TParm();
            String order_code = "";
            String unit_type = "0";
            String order_desc = "";
            if ("DEP".equals(getValueString("REQTYPE_CODE")))
                unit_type = IndSysParmTool.getInstance().onQuery().getValue(
                    "UNIT_TYPE", 0);
            else
                unit_type = "1";

            for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
                if (!table_d.getDataStore().isActive(i)) {
                    continue;
                }
                order_code = table_d.getDataStore().getItemString(i,
                    "ORDER_CODE");
                TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                    getOrderInfoByCode(order_code, unit_type)));
                if (inparm == null || inparm.getErrCode() < 0) {
                    this.messageBox("ҩƷ��Ϣ����");
                    return;
                }
                if ("".equals(inparm.getValue("GOODS_DESC", 0))) {
                    order_desc = inparm.getValue("ORDER_DESC", 0);
                }
                else {
                    order_desc = inparm.getValue("ORDER_DESC", 0) + "(" +
                        inparm.getValue("GOODS_DESC", 0) + ")";
                }
                parm.addData("ORDER_DESC", order_desc);
                parm.addData("SPECIFICATION",
                             inparm.getValue("SPECIFICATION", 0));
                parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", 0));
                parm.addData("UNIT_PRICE",
                             table_d.getItemDouble(i, "RETAIL_PRICE"));
                parm.addData("QTY",
                             table_d.getItemDouble(i, "QTY"));
                parm.addData("AMT", StringTool.round(table_d.getItemDouble(i,
                    "RETAIL_PRICE") * table_d.getItemDouble(i, "QTY"), 2));
            }
            if (parm.getCount("ORDER_DESC") == 0) {
                this.messageBox("û�д�ӡ����");
                return;
            }

            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "UNIT");
            parm.addData("SYSTEM", "COLUMNS", "QTY");
            parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "AMT");
            date.setData("TABLE", parm.getData());

            // ��β����
            date.setData("TOT", "TEXT", "�ϼ�: " +
                         StringTool.round(Double.parseDouble(this.
                getValueString("SUM_RETAIL_PRICE")),
                                          2));
            
            //luhai 2012-2-11 add �ϼƴ�д begin
            date.setData("TOT_DESC", "TEXT", "" +StringUtil.getInstance().numberToWord(StringTool.round(Double.parseDouble(this.
    				getValueString("SUM_RETAIL_PRICE")),2))
            		);
            //luhai 2012-2-11 add �ϼƴ�д end
            date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
            // ���ô�ӡ����
            this.openPrintWindow("%ROOT%\\config\\prt\\IND\\RequestDepTec.jhw",
                                 date);
        }
        else {
            this.messageBox("û�д�ӡ����");
            return;
        }
    }


    /**
     * ����������¼�
     */
    public void onChangeRequestType() {
        TTextFormat app_org_code = (TTextFormat) getComponent(
            "APP_ORG_CODE");
        TextFormatINDOrg to_org_code = (TextFormatINDOrg)this.getComponent(
            "TO_ORG_CODE");
        app_org_code.setValue("");
        to_org_code.setValue("");
        request_type = this.getValueString("REQTYPE_CODE");
        // ȫ������Ȩ��
        if (dept_flg) {
            // ������ѡ��������趨���벿�źͽ��ܲ���
            if ("DEP".equals(request_type)) {
                getTextFormat("APP_ORG_CODE").setPopupMenuSQL(INDSQL.
                    initTextFormatIndOrg("B", Operator.getRegion()));
                to_org_code.setOrgType("A");
            }
            else if ("TEC".equals(request_type)) {
                getTextFormat("APP_ORG_CODE").setPopupMenuSQL(INDSQL.
                    initTextFormatIndOrg("C", Operator.getRegion()));
                to_org_code.setOrgType("B");
            }
            else if ("EXM".equals(request_type)) {
                getTextFormat("APP_ORG_CODE").setPopupMenuSQL(INDSQL.
                    initTextFormatIndOrg("C","Y"));
                to_org_code.setOrgType("B");
            }
            to_org_code.setOrgFlg("Y");
            app_org_code.onQuery();
            to_org_code.onQuery();
        }
        else {
            if ("DEP".equals(request_type)) {
                app_org_code.setPopupMenuSQL(INDSQL.getIndOrgByUserId(Operator.
                    getID(), Operator.getRegion(), " AND B.ORG_TYPE = 'B' "));
                to_org_code.setOrgType("A");
            }
            else if ("TEC".equals(request_type)) {
                app_org_code.setPopupMenuSQL(INDSQL.getIndOrgByUserId(Operator.
                    getID(), Operator.getRegion(), " AND B.ORG_TYPE = 'C' "));
                to_org_code.setOrgType("B");
            }
            else if ("EXM".equals(request_type)) {
                app_org_code.setPopupMenuSQL(INDSQL.getIndOrgByUserId(Operator.
                    getID(), Operator.getRegion(), " AND B.ORG_TYPE = 'C' AND B.EXINV_FLG = 'Y' "));
                to_org_code.setOrgType("B");
            }
            app_org_code.onQuery();
            to_org_code.setOrgFlg("Y");
            to_org_code.onQuery();
        }
    }

    /**
     * ���뵥״̬����¼�
     */
    public void onChangeRequestStatus() {
        String clearString =
            "REQUEST_NO;REQTYPE_CODE;TO_ORG_CODE;REASON_CHN_DESC;" +
            "DESCRIPTION;URGENT_FLG;SUM_RETAIL_PRICE";
        this.clearValue(clearString);
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();

        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(false);
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
            // �趨ҳ��״̬
            getTextField("REQUEST_NO").setEnabled(false);
            getTextFormat("APP_ORG_CODE").setEnabled(false);
            getComboBox("REQTYPE_CODE").setEnabled(false);
            ((TMenuItem) getComponent("delete")).setEnabled(true);
            action = "updateM";
            // �������
            request_type = getValueString("REQTYPE_CODE");
            // ���뵥��
            request_no = getValueString("REQUEST_NO");
            // ��ϸ��Ϣ
            getTableDInfo(request_no);
            // ���һ��ϸ��
            onAddRow();
            table_d.setSelectionMode(0);
        }
    }

    /**
     * ��ϸ���(TABLE_D)�����¼�
     */
    public void onTableDClicked() {
        row_d = table_d.getSelectedRow();
        if (row_d != -1) {
            action = "updateD";
            table_m.setSelectionMode(0);
            row_m = -1;
            // ȡ��SYS_FEE��Ϣ��������״̬����
            /*
             String order_code = table_d.getDataStore().getItemString(table_d.
                getSelectedRow(), "ORDER_CODE");
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
     * ��TABLE�����༭�ؼ�ʱ����
     *
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComoponentUD(Component com, int row, int column) {
        if (column != 0)
            return;
        if (! (com instanceof TTextField))
            return;
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        // ���õ����˵�
        textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // ������ܷ���ֵ����
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");
    }

    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        table_d.acceptText();
        String order_code = parm.getValue("ORDER_CODE");
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_code))
            table_d.getDataStore().setItem(table_d.getSelectedRow(),
                                           "ORDER_CODE", order_code);

        // ��鹩Ӧ�����Ƿ���ڸ�ҩƷ
        TParm qtyParm = new TParm();
        qtyParm.setData("ORG_CODE", this.getValueString("TO_ORG_CODE"));
        qtyParm.setData("ORDER_CODE", order_code);
        TParm getQTY = IndStockMTool.getInstance().onQuery(qtyParm);
        if (getQTY.getCount() <= 0 || getQTY.getErrCode() < 0) {
            this.messageBox("��Ӧ���Ų�����ҩƷ:" + order_desc + "(" + order_code + ")");
            table_d.removeRow(table_d.getSelectedRow());
            onAddRow();
            return;
        }

        // ����Ƿ�����ظ�ҩƷ
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!table_d.getDataStore().isActive(i)) {
                continue;
            }
            if (table_d.getSelectedRow() == i) {
                continue;
            }
            if (order_code.equals(table_d.getDataStore().getItemString(i,
                "ORDER_CODE"))) {
                this.messageBox("ҩƷ�����ظ�");
                return;
            }
        }

        TParm result = new TParm(TJDODBTool.getInstance().select(INDSQL.
            getPHAInfoByOrder(order_code)));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            this.messageBox("ҩƷ��Ϣ����");
            return;
        }
        double stock_price = 0;
        double retail_price = 0;
        if ("0".equals(getValueString("UNIT_TYPE"))) {
            // ��浥λ
            table_d.setItem(table_d.getSelectedRow(), "UNIT_CODE", result
                            .getValue("STOCK_UNIT", 0));
            stock_price = StringTool.round(result.getDouble("STOCK_PRICE", 0)
                                           * result.getDouble("DOSAGE_QTY", 0),
                                           2);
            retail_price = StringTool.round(result.getDouble("RETAIL_PRICE", 0)
                                            * result.getDouble("DOSAGE_QTY", 0),
                                            2);
        }
        else {
            // ��ҩ��λ
            table_d.setItem(table_d.getSelectedRow(), "UNIT_CODE",
                            result.getValue("DOSAGE_UNIT",
                                            0));
            stock_price = result.getDouble("STOCK_PRICE", 0);
            retail_price = result.getDouble("RETAIL_PRICE", 0);
        }
        // �ɱ���
        table_d.getDataStore().setItem(table_d.getSelectedRow(), "STOCK_PRICE",
                                       stock_price);
        // ���ۼ�
        table_d.setItem(table_d.getSelectedRow(), "RETAIL_PRICE", retail_price);
        // �趨ѡ���е���Ч��
        table_d.getDataStore().setActive(table_d.getSelectedRow(), true);
        int old_row = table_d.getSelectedRow();
        onAddRow();
        table_d.getTable().grabFocus();
        table_d.setSelectedRow(old_row);
        table_d.setSelectedColumn(2);
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
        // Table������
        TTable table = node.getTable();
        String columnName = table.getDataStoreColumnName(node.getColumn());
        int row = node.getRow();
        if ("QTY".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("������������С�ڻ����0");
                return true;
            }
            table.getDataStore().setItem(row, "QTY", qty);
            this.setValue("SUM_RETAIL_PRICE", getSumRetailPrice());
            return false;
        }
        return true;
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        /**
         * Ȩ�޿���
         * Ȩ��1:һ�����ֻ��ʾ������������
         * Ȩ��9:���Ȩ����ʾȫԺҩ�ⲿ��
         */
        // ��ʾȫԺҩ�ⲿ��
        if (!this.getPopedem("deptAll")) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion(), " AND B.ORG_TYPE = 'B' ")));
            getTextFormat("APP_ORG_CODE").setPopupMenuSQL(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion(), " AND B.ORG_TYPE = 'B' "));
            if (parm.getCount("NAME") > 0) {
                getTextFormat("APP_ORG_CODE").setValue(parm.getValue("ID", 0));
            }
            dept_flg = false;
        }
        else {
            getTextFormat("APP_ORG_CODE").setPopupMenuSQL(INDSQL.
                initTextFormatIndOrg("B", Operator.getRegion()));
        }
        getTextFormat("APP_ORG_CODE").onQuery();
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        // ��ʼ������ʱ��
        setValue("REQUEST_DATE", date);
        // ��ʼ�����뵥����
        //getComboBox("REQTYPE_CODE").setSelectedIndex(0);
        // ��ʼ������״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        // ��ʼ��TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        row_m = -1;
        row_d = -1;
        seq = 0;
        action = "insertM";
    }

    /**
     * ��ѯ��������
     *
     * @return
     */
    private TParm onQueryParm() {
        TParm parm = new TParm();
        if (!"".equals(getValueString("APP_ORG_CODE"))) {
            parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        }
        if (!"".equals(getValueString("REQUEST_NO"))) {
            parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
        }
        if (!"".equals(getValueString("REQTYPE_CODE"))) {
            parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
        }
        else {
            parm.setData("TYPE_DEPT_TEC", "");
        }
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
     * �������뵥��ȡ��ϸ����Ϣ����ʾ��ϸ������
     *
     * @param req_no
     */
    private void getTableDInfo(String req_no) {
        // ��ϸ��Ϣ
        table_d.removeRowAll();
        table_d.setSelectionMode(0);
        TDS tds = new TDS();
        tds.setSQL(INDSQL.getRequestDByNo(req_no));
        tds.retrieve();
        if (tds.rowCount() == 0) {
            seq = 1;
        }
        else {
            seq = getMaxSeq(tds, "SEQ_NO");
        }

        // �۲���
        IndRequestObserver obser = new IndRequestObserver();
        tds.addObserver(obser);
        table_d.setDataStore(tds);
        table_d.setDSValue();
        this.setValue("SUM_RETAIL_PRICE", getSumRetailPrice());
    }

    /**
     * ���һ��ϸ��
     */
    private void onAddRow() {
        // ��δ�༭��ʱ����
        if (!this.isNewRow())
            return;
        int row = table_d.addRow();
        TParm parm = new TParm();
        parm.setData("ACTIVE", false);
        table_d.getDataStore().setActive(row, false);
    }

    /**
     * �Ƿ���δ�༭��
     *
     * @return boolean
     */
    private boolean isNewRow() {
        Boolean falg = false;
        TParm parmBuff = table_d.getDataStore().getBuffer(
            table_d.getDataStore().PRIMARY);
        int lastRow = parmBuff.getCount("#ACTIVE#");
        Object obj = parmBuff.getData("#ACTIVE#", lastRow - 1);
        if (obj != null) {
            falg = (Boolean) parmBuff.getData("#ACTIVE#", lastRow - 1);
        }
        else {
            falg = true;
        }
        return falg;
    }

    /**
     * ���ݶ���״̬���˲�ѯ���
     *
     * @param parm
     * @return
     */
    private TParm onFilterQueryByStatus(TParm parm) {
        String update_flg = "0";
        boolean flg = false;
        TDataStore ds = new TDataStore();
        for (int i = parm.getCount("REQUEST_NO") - 1; i >= 0; i--) {
            ds.setSQL(INDSQL.getRequestDByNo(parm.getValue("REQUEST_NO", i)));
            ds.retrieve();
            if (ds.rowCount() == 0) {
                flg = false;
            }
            else {
                flg = true;
                for (int j = 0; j < ds.rowCount(); j++) {
                    update_flg = ds.getItemString(j, "UPDATE_FLG");
                    if ("0".equals(update_flg) || "1".equals(update_flg)) {
                        // δ���
                        flg = false;
                        break;
                    }
                }
            }
            // ����״̬
            if (getRadioButton("UPDATE_FLG_A").isSelected()) {
                // δ���
                if (flg) {
                    parm.removeRow(i);
                }
            }
            else {
                // ���
                if (!flg) {
                    parm.removeRow(i);
                }
            }
        }
        // û��ȫ������Ȩ��
        if (!dept_flg) {
            TParm optOrg = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion())));
            for (int i = parm.getCount("APP_ORG_CODE") - 1; i >= 0; i--) {
                boolean check_flg = true;
                for (int j = 0; j < optOrg.getCount("ID"); j++) {
                    if (parm.getValue("APP_ORG_CODE", i).equals(optOrg.getValue(
                        "ID", j))) {
                        check_flg = false;
                        break;
                    }
                    else {
                        continue;
                    }
                }
                if (check_flg) {
                    parm.removeRow(i);
                }
            }
        }
        return parm;
    }


    /**
     * ����������������PARM
     *
     * @param parm
     * @return
     */
    private TParm getRequestMParm(TParm parm) {
        Timestamp date = SystemTool.getInstance().getDate();
        parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
        parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        parm.setData("TO_ORG_CODE", getValueString("TO_ORG_CODE"));
        parm.setData("REQUEST_DATE", getValue("REQUEST_DATE"));
        parm.setData("REQUEST_USER", Operator.getID());
        parm.setData("REASON_CHN_DESC", getValueString("REASON_CHN_DESC"));
        parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
        String unit_type = "0";
        if ("DEP".equals(getValueString("REQTYPE_CODE")))
        	//���տ�浥λ���� 
            unit_type = IndSysParmTool.getInstance().onQuery().getValue(
                "UNIT_TYPE",
                0);
        else
        	//1 �ǿۿⵥλ����ҩ��
            unit_type = "1";
        parm.setData("UNIT_TYPE", unit_type);
        parm.setData("URGENT_FLG", getValueString("URGENT_FLG"));
        //zhangyong20110517
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        return parm;
    }

    /**
     * ���������ܽ��
     *
     * @return
     */
    private double getSumRetailPrice() {
        table_d.acceptText();
        double sum = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!table_d.getDataStore().isActive(i)) {
                continue;
            }
            double amount1 = table_d.getItemDouble(i, "QTY");
            sum += table_d.getItemDouble(i, "RETAIL_PRICE") * amount1;
        }
        return StringTool.round(sum, 2);
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("APP_ORG_CODE"))) {
            this.messageBox("���첿�Ų���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("REQTYPE_CODE"))) {
            this.messageBox("���������Ϊ��");
            return false;
        }
        TextFormatINDOrg to_org_code = (TextFormatINDOrg)this.getComponent(
            "TO_ORG_CODE");
        if (to_org_code.isEnabled()) {
            if ("".equals(getValueString("TO_ORG_CODE"))) {
                this.messageBox("��Ӧ���Ų���Ϊ��");
                return false;
            }
        }
        return true;
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataD() {
        table_d.acceptText();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!table_d.getDataStore().isActive(i)) {
                continue;
            }
            if (table_d.getItemDouble(i, "QTY") <= 0) {
                this.messageBox("������������С�ڻ����0");
                return false;
            }
        }
        return true;
    }

    /**
     * ���ⲿ�ſ�漰ҩƷ�������ж�
     */
    private boolean getOrgStockCheck() {
        /** ��Ӧ����ҩƷ¼��ܿ� */
        String org_code = getValueString("TO_ORG_CODE");
        String order_code = "";
        double stock_qty = 0;
        double qty = 0;

        for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
            if (!table_d.getDataStore().isActive(i)) {
                continue;
            }
            order_code = table_d.getDataStore().getItemString(i,
                "ORDER_CODE");
            qty = table_d.getDataStore().getItemDouble(i, "QTY");
            TParm resultParm = new TParm();
            if ("0".equals(getValueString("UNIT_TYPE"))) {
                String sql = INDSQL.getPHAInfoByOrder(order_code);
                resultParm = new TParm(TJDODBTool.getInstance().select(sql));
                if (resultParm.getErrCode() < 0) {
                    this.messageBox("ҩƷ��Ϣ����");
                    return false;
                }
                qty = StringTool.round(qty *
                                       resultParm.getDouble("DOSAGE_QTY", 0), 2);
            }
            TParm inparm = new TParm();
            inparm.setData("ORG_CODE", org_code);
            inparm.setData("ORDER_CODE", order_code);
            TParm resultQTY = INDTool.getInstance().getStockQTY(inparm);
            stock_qty = resultQTY.getDouble("QTY", 0);
            if (stock_qty < 0) {
                stock_qty = 0;
            }
            if (qty > stock_qty) {
                TParm order = INDTool.getInstance().getSysFeeOrder(
                    order_code);
                if (this.messageBox("��ʾ",
                                    "ҩƷ:" + order.getValue("ORDER_DESC") + "(" +
                                    order_code + ") �����������,��ǰ�����Ϊ" +
                                    stock_qty + ",�Ƿ����", 2) == 0) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }


    /**
     * �õ����ı��
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    private int getMaxSeq(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return 0;
        // ����������
        int count = dataStore.rowCount();
        // ��������
        int s = 0;
        for (int i = 0; i < count; i++) {
            int value = dataStore.getItemInt(i, columnName);
            // �������ֵ
            if (s < value) {
                s = value;
                continue;
            }
        }
        // ���ż�1
        s++;
        return s;
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

}
