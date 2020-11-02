package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import jdo.inv.INV;
import jdo.inv.INVNewRepackTool;
import jdo.inv.INVPackDTool;
import jdo.inv.INVPackMTool;
import jdo.inv.INVPublicTool;
import jdo.inv.INVSQL;
import jdo.inv.InvBaseTool;
import jdo.inv.InvPackStockMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 *
 * <p>Title: ������������</p>
 *
 * <p>Description:������������ </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis  </p>
 *
 * @author wangm 2013-07-01
 * @version 1.0
 */
public class INVPackageControl
    extends TControl {
    /**
     * ����
     */
    private TTable tableM;
    /**
     * ϸ��
     */
    private TTable tableD;
    /**
     * ������ķ���
     */
    private INV inv = new INV();
    /**
     * ��¼��������������
     */
    private TParm returnParm;
    /**
     * ��¼����������Ź���
     */
    private Map map = new HashMap();
    /**
     * �Ƿ�����ע��
     */
    private boolean isNew = false;
    /**
     * ����������
     */
    private String barcode = "";
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
//        //���ʵ�������
//        callFunction("UI|PACK_CODE|setPopupMenuParameter", "PACKCODE",
//                     "%ROOT%\\config\\inv\\INVChoose.x");
//        //���ܻش�ֵ
//        callFunction("UI|PACK_CODE|addEventListener",
//                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        
        TParm parm = new TParm();
        // ���õ����˵�
        getTextField("PACK_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
        // ������ܷ���ֵ����
        getTextField("PACK_CODE").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn");
        
        
        
        //zhangyong20091110
        // ��������¼�
        addEventListener("TABLEM->" + TTableEvent.CHANGE_VALUE,
                         "onTableMChangeValue");

        //table���ɱ༭
        tableM = (TTable) getComponent("TABLEM");
        tableD = (TTable) getComponent("TABLED");
        //��ʼ��table
        onInitTable();
        //��ʼ�������ϵ�����
        initValue();
        initCombox();
        //��ӹ۲���
        observer();
        this.callFunction("UI|new|setEnabled", true);
        this.callFunction("UI|save|setEnabled", false);
        //����Ĭ�Ͽ���
        TTextFormat tf = (TTextFormat)getComponent("ORG_CODE");
        tf.setValue(Operator.getDept());
    }

    /**
     * zhangyong20091110
     * ���ֵ�ı��¼�
     *
     * @param obj
     *            Object
     */
    public boolean onTableMChangeValue(Object obj) {
        // ֵ�ı�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // �ж����ݸı�
        if (node.getValue().equals(node.getOldValue()))
            return true;
        int column = node.getColumn();
        int row = node.getRow();
        if (column == 2) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("�����������С�ڻ����0");
                return true;
            }
            if(tableM.getDataStore().getItemInt(row, "PACK_SEQ_NO")>0&&qty!=1){
            	this.messageBox("�����������������Ϊ1��");
                return true;
            }
//            double amt1 = StringTool.round(qty *
//                                           this.getValueDouble("USE_COST"),
//                                           2);
//            double amt2 = StringTool.round(qty *
//                                           this.getValueDouble("ONCE_USE_COST"),
//                                           2);
//            // ������Ϣ���
//            tableM.getDataStore().setItem(row, "USE_COST", amt1);
//            tableM.getDataStore().setItem(row, "ONCE_USE_COST", amt2);
//            tableM.setItem(row, "USE_COST", amt1);
//            tableM.setItem(row, "ONCE_USE_COST", amt2);
            // ϸ����Ϣ���
            for (int i = 0; i < tableD.getDataStore().rowCount(); i++) {
                tableD.setItem(i, "QTY", tableD.getDataStore().
                               getItemDouble(i, "QTY") * qty /
                               TypeTool.getDouble(node.getOldValue()));
            }
            return false;
        }
        return true;
    }

    /**
     * ��ӹ۲���
     */
    public void observer() {
        //����ϸ��ӹ۲���
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(
            "SELECT INV_CODE,INV_CHN_DESC,DESCRIPTION FROM INV_BASE");
        dataStore.retrieve();
        //��ϸ����ӹ۲���
        TDS tds = (TDS) tableD.getDataStore();
        tds.addObserver(new INVBaseTob(dataStore));

        //��������ӹ۲���
        dataStore = new TDataStore();
        dataStore.setSQL("SELECT PACK_CODE,PACK_DESC FROM INV_PACKM");
        dataStore.retrieve();
        //��ϸ����ӹ۲���
        tds = (TDS) tableM.getDataStore();
        tds.addObserver(new INVPackTob(dataStore));

    }

    /**
     * ���ʿ���
     */
    public void initCombox() {
    	
//2013-6-5ע��    	
//        TParm parm = INVOrgTool.getInstance().getDept();
//        TComboBox comboBox = (TComboBox)this.getComponent("ORG_CODE");
//        comboBox.setParmValue(parm);
//        comboBox.updateUI();
//        //Ĭ��������
//        String deptCode = "";
//        TParm toOrgParm = INVPublicTool.getInstance().getOrgCode("B");
//        if (toOrgParm.getErrCode() >= 0) {
//            deptCode = toOrgParm.getValue("ORG_CODE", 0);
//            comboBox.setValue(deptCode);
//        }
//        comboBox.setValue(deptCode);


        TParm parmUser = INVPublicTool.getInstance().getInvOperator();
        TComboBox comboBox = new TComboBox();
        comboBox.setParmMap("id:USER_ID;name:USER_NAME;py1:NAME_PYCODE");
        comboBox.setParmValue(parmUser);
        comboBox.setShowID(true);
        comboBox.setShowName(true);
        comboBox.setExpandWidth(30);
        comboBox.setTableShowList("name");

        tableD.addItem("USER", comboBox);


    }

//    /**
//     * Ĭ�ϲ�ѯʱ��
//     */
//    public void setDate() {
//        String disinfectionUser = getValueString("DISINFECTION_USER");
//        if (disinfectionUser == null | disinfectionUser.length() == 0) {
//            this.setValue("DISINFECTION_DATE", null);
//            this.setValue("VALUE_DATE", null);
//            return;
//        }
//        //ȡϵͳ�¼�
//        Timestamp date = SystemTool.getInstance().getDate();
//        this.setValue("DISINFECTION_DATE", date);
//        this.setValue("VALUE_DATE", date);
//    }
    /**
     * ��ʼ��table
     */
    public void onInitTable() {
        //����
        tableM.setSQL("select * from INV_PACKSTOCKM WHERE PACK_CODE IS NULL");
        tableM.retrieve();
        //ϸ��
        tableD.setSQL("select * from INV_PACKSTOCKD WHERE PACK_CODE IS NULL");
        tableD.retrieve();
    }

    /**
     * ��ʼ�������ϵ�����
     */
    public void initValue() {
        //״̬
        setValue("STATUS", "0");
        initCombox();
    }

    /**
     * ��ѯPACK_CODE
     */
    public void onQuery() {
        String sql = "SELECT * FROM INV_PACKSTOCKM  ";
        //������
        String value = getValueString("PACK_CODE");
        if (value != null && value.length() != 0)
            sql += "WHERE PACK_CODE = '" + value + "'";
        sql += " ORDER BY PACK_CODE,PACK_SEQ_NO,PACK_BATCH_NO DESC";
        //ˢ��table
        retrieveTable(tableM, sql);
        //��ѯ��Ĵ���
        afterQuery();
    }

    /**
     * ��ѯ��Ĵ���
     */
    public void afterQuery() {
        //�ɱ༭״̬
        clearEnable(false);
        //�������ɱ༭
        setOtherEnable(false);
        this.callFunction("UI|new|setEnabled", false);
        this.callFunction("UI|save|setEnabled", false);
    }

    /**
     * �����Ĳ��ɱ༭
     * @param statuse boolean
     */
    public void setOtherEnable(boolean statuse) {
        //����
        this.callFunction("UI|new|setEnabled", statuse);
        //����
        this.callFunction("UI|save|setEnabled", statuse);
        //�̶��ɱ�
        this.callFunction("UI|USE_COST|setEnabled", statuse);
        //����Ч��
        this.callFunction("UI|VALUE_DATE|setEnabled", statuse);
        //������Ա
        this.callFunction("UI|DISINFECTION_USER|setEnabled", statuse);
        //ʹ��״̬
        this.callFunction("UI|STATUS|setEnabled", statuse);
        //��������
        this.callFunction("UI|DISINFECTION_DATE|setEnabled", statuse);

    }

    /**
     * ����ĵ���¼�
     */
    public void onTableMClicked() {
        //����������ݱ��
//        if (!this.checkValueChange())
//            return;
        //zhangyong20100913 begin
        int row = tableM.getSelectedRow();
        TParm parm = tableM.getDataStore().getRowParm(row);
//        setValueForParm("PACK_CODE;PACK_SEQ_NO;QTY;ORG_CODE;" +
//                        "DISINFECTION_DATE;VALUE_DATE;DISINFECTION_USER;STATUS",
//                        parm);
        setValueForParm("PACK_CODE;PACK_SEQ_NO;QTY;" +
        		"DISINFECTION_DATE;VALUE_DATE;DISINFECTION_USER;STATUS",
        		parm);
        String pack_code = parm.getValue("PACK_CODE");
        String packM_sql = "SELECT * FROM INV_PACKM WHERE PACK_CODE = '" + pack_code + "'";
        TParm packM = new TParm(TJDODBTool.getInstance().select(packM_sql));
        //System.out.println("packM---"+packM);
        this.setValue("PACK_DESC",packM.getValue("PACK_DESC",0));
        //zhangyong20100913 end
        TMenuItem save_item = (TMenuItem)this.getComponent("save");
        if (!save_item.isEnabled()) {
            //����
            String sql = "select * from INV_PACKSTOCKD WHERE PACK_CODE='" +
                getValue("PACK_CODE") + "' AND PACK_SEQ_NO=" +
                getValueInt("PACK_SEQ_NO") + " AND PACK_BATCH_NO = " + parm.getValue("PACK_BATCH_NO");
            //ˢ��table
            retrieveTable(tableD, sql);
        }
        //zhangyong20091110 end
    }

    /**
     * ˢ��table
     * @param table TTable
     * @param sql String
     */
    public void retrieveTable(TTable table, String sql) {
        table.setSQL(sql);
        table.retrieve();
        table.setDSValue();
    }

//    /**
//     * ������ѡ�񷵻����ݴ���
//     * @param tag String
//     * @param obj Object
//     */
//    public void popReturn(String tag, Object obj) {
//        if (obj == null)
//            return;
//        returnParm = (TParm) obj;
//        //���ô��������ݵķ���
//        tableD.acceptText();
//        onDealRetrunValue(returnParm);
//    }
    
    /**
     * ���ܷ���ֵ����wm2013
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("��ѡ��������");
            getTextField("PACK_CODE").setValue("");
            return;
        }
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        returnParm = (TParm) obj;  //wm2013-06-05  ���
        String pack_code = parm.getValue("PACK_CODE");
        if (!StringUtil.isNullString(pack_code))
            getTextField("PACK_CODE").setValue(pack_code);
        String pack_desc = parm.getValue("PACK_DESC");
        if (!StringUtil.isNullString(pack_desc))
            getTextField("PACK_DESC").setValue(pack_desc);
        if ("0".equals(parm.getValue("SEQ_FLG"))) {
            this.setValue("PACK_SEQ_NO", 0);
//            seq_flg = "0";
        }
        else {
            TParm packStockM = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getPackMaxSeq(this.getValueString("ORG_CODE"),
                              this.getValueString("PACK_CODE"))));
            this.setValue("PACK_SEQ_NO",
                          packStockM.getInt("PACK_SEQ_NO", 0) + 1);
 //           seq_flg = "1";
        }
        this.setValue("USE_COST", parm.getValue("USE_COST"));
        TParm packD = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getINVPackDByCostPrice(getValueString("PACK_CODE"))));
        double once_use_cost = 0;
        for (int i = 0; i < packD.getCount("COST_PRICE"); i++) {
        	
        	if(packD.getValue("PACK_TYPE", i).equals("1")){
        		once_use_cost += packD.getDouble("COST_PRICE", i) * packD.getDouble("QTY", i);
        	}
        }
        this.setValue("ONCE_USE_COST", once_use_cost);
    }
    

    /**
     * ����������ѡ�񷵻�����
     * @param parm TParm
     */
    public void onDealRetrunValue(TParm parm) {
        //����
        setValue("PACK_CODE", parm.getValue("PACK_CODE"));
        //����
        setValue("PACK_DESC", parm.getValue("PACK_DESC"));
        //���
        setValue("PACK_SEQ_NO", onPackSeqNo(parm.getValue("PACK_CODE")));
        //�̶���ֵ
        setValue("USE_COST", parm.getValue("USE_COST"));
        this.callFunction("UI|PACK_CODE|setEnabled", false);
        this.callFunction("UI|PACK_SEQ_NO|setEnabled", false);
    }

    /**
     * �������������
     * @param packCode String
     * @return int
     */
    public int onPackSeqNo(String packCode) {
        int packSeqNo = 0;
        //�������Ź���
        if (onCheckSeqFlg()) {
            packSeqNo = getPackSeqNo(packCode);
        }
        return packSeqNo;
    }

    /**
     * �����ݿ��м������������
     * @param packCode String
     * @return int
     */
    public int getPackSeqNo(String packCode) {
        //����������
        TParm result = InvPackStockMTool.getInstance().getStockSeq(packCode);	//wm2013-06-04    INVPackStockMTool.
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return -1;
        }
        //������
        int packSeqNo = result.getInt("MAX(PACK_SEQ_NO)", 0) + 1;
        return packSeqNo;
    }

    /**
     * ����������Ƿ�����Ź���
     * @return boolean
     */
    public boolean onCheckSeqFlg() {
        String seqManFlg = returnParm.getValue("SEQ_FLG");
        if (seqManFlg == null || seqManFlg.length() == 0 ||
            seqManFlg.equals("0"))
            return false;
        return true;
    }

    /**
     * ����
     */
    public void onNew() {
        //����������ݱ��
        if (!this.checkValueChange())
            return;
        //�����������¼��
        if (!newTableMCheckValue())
            return;
        //�������������
        //zhangyong20091110 begin
        String returnStr = setTabaleMValue();
        if ("0".equals(returnStr)) {
            // ����Ź���
            //����������ϸ����
            if (!onGetInv()) {
                this.callFunction("UI|new|setEnabled", false);
                this.callFunction("UI|save|setEnabled", false);
                return;
            }
            //���ɱ༭
            clearEnable(false);
            this.callFunction("UI|new|setEnabled", false);
            this.callFunction("UI|save|setEnabled", true);
        }
        else if ("1".equals(returnStr)) {
            // ��Ź���
            //����������ϸ����
            if (!onGetInv()) {
                this.callFunction("UI|new|setEnabled", false);
                this.callFunction("UI|save|setEnabled", false);
                return;
            }
            //���ɱ༭
            clearEnable(false);
            this.callFunction("UI|new|setEnabled", false);
            this.callFunction("UI|save|setEnabled", true);
        }
        //zhangyong20091110 end
    }

    /**
     * ����������ʱ�������
     * @return boolean
     */
    public boolean newTableMCheckValue() {
        //������
        String packCode = this.getValueString("PACK_CODE");
        if (packCode == null || packCode.length() == 0) {
            messageBox("��������������!");
            return false;
        }
        //�������
        String orgCode = getValueString("ORG_CODE");
        if (orgCode == null || orgCode.length() == 0) {
            messageBox("������������!");
            return false;
        }
        //�̶��ɱ�
        double useCost = getValueDouble("USE_COST");
        if (useCost <= 0) {
            messageBox("������̶��ɱ�!");
            return false;
        }
        //�������
        String status = getValueString("STATUS");
        if (status == null || status.length() == 0) {
            messageBox("��ѡ��״̬!");
            return false;
        }
        return true;
    }

    /**
     * zhangyong20091110
     * ����ʱ���������¼������
     */
    public String setTabaleMValue() {
        //zhangyong20091110 begin �����Ź���ͷ���Ź��������
        String sql = INVSQL.getInvPackM(this.getValueString("PACK_CODE"));
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if ("0".equals(parm.getValue("SEQ_FLG", 0))) {
            // ����Ź���
            // ��������������
            tableM.setLockColumns("0,1,3,4,5,7,8,9,10");
            int row = tableM.addRow();
            TDataStore dataStore = tableM.getDataStore();
            //����
            dataStore.setItem(row, "PACK_CODE", getValueString("PACK_CODE"));
            //����(Ĭ����ֵΪ1�����޸�)
            dataStore.setItem(row, "QTY", 1);
            //���
            dataStore.setItem(row, "PACK_SEQ_NO", 0);
            //�̶��ɱ�
            dataStore.setItem(row, "USE_COST", getValueDouble("USE_COST"));
            Timestamp valueDate = TCM_Transform.getTimestamp(getValue(
                "DISINFECTION_DATE"));
            //��������
            dataStore.setItem(row, "DISINFECTION_DATE", valueDate);
            if (valueDate == null)
                dataStore.setItem(row, "DISINFECTION_DATE", new TNull(Timestamp.class));
            //����Ч��
            valueDate = TCM_Transform.getTimestamp(getValue("VALUE_DATE"));
            dataStore.setItem(row, "VALUE_DATE", valueDate);
            if (valueDate == null)
                dataStore.setItem(row, "VALUE_DATE", new TNull(Timestamp.class));
            //������Ա
            String valueStr = getValueString("DISINFECTION_USER");
            dataStore.setItem(row, "DISINFECTION_USER", valueStr);
            if (valueStr == null)
                dataStore.setItem(row, "DISINFECTION_USER", new TNull(String.class));
            //ʹ��״̬
            dataStore.setItem(row, "STATUS", getValueString("STATUS"));
            tableM.setDSValue();
            return "0";
        }
        else if ("1".equals(parm.getValue("SEQ_FLG", 0))) {
            // ��Ź���
            // �ر�������������
            tableM.setLockColumns("0,1,3,4,5,7,8,9,10");
            int row = tableM.addRow();
            TDataStore dataStore = tableM.getDataStore();
            //����
            dataStore.setItem(row, "PACK_CODE", getValueString("PACK_CODE"));
            //����(һ��ֻ�ܴ�һ����)
            dataStore.setItem(row, "QTY", 1);
            //���
            dataStore.setItem(row, "PACK_SEQ_NO", getValueInt("PACK_SEQ_NO"));
            //�̶��ɱ�
            dataStore.setItem(row, "USE_COST", getValueDouble("USE_COST"));
            Timestamp valueDate = TCM_Transform.getTimestamp(getValue(
                "DISINFECTION_DATE"));
            //��������
            dataStore.setItem(row, "DISINFECTION_DATE", valueDate);
            if (valueDate == null)
                dataStore.setItem(row, "DISINFECTION_DATE", new TNull(Timestamp.class));
            //����Ч��
            valueDate = TCM_Transform.getTimestamp(getValue("VALUE_DATE"));
            dataStore.setItem(row, "VALUE_DATE", valueDate);
            if (valueDate == null)
                dataStore.setItem(row, "VALUE_DATE", new TNull(Timestamp.class));
            //������Ա
            String valueStr = getValueString("DISINFECTION_USER");
            dataStore.setItem(row, "DISINFECTION_USER", valueStr);
            if (valueStr == null)
                dataStore.setItem(row, "DISINFECTION_USER", new TNull(String.class));
            //ʹ��״̬
            dataStore.setItem(row, "STATUS", getValueString("STATUS"));
            tableM.setDSValue();
            return "1";
        }
        return "1";
        //zhangyong20091110 end
    }

    /**
     * ����������ϸ����
     * @return boolean
     */
    public boolean onGetInv() {
        String packCode = getValueString("PACK_CODE");
        //������������ϸ����
        TParm result = INVPackDTool.getInstance().getPackDetial(packCode);
        if (result == null || result.getErrCode() < 0)
            return false;
        //��������ϸ������ϸ��
        return onAddTableD(result);
    }

    /**
     * ����ϸ��������
     * @param parm TParm
     * @return boolean
     */
    public boolean onAddTableD(TParm parm) {
        //����
        int rowCount = parm.getCount();
        for (int i = 0; i < rowCount; i++) {
            //�ó�һ������
            TParm oneParm = parm.getRow(i);
            //��˿����
            if (!onChangeQty(oneParm.getValue("INV_CODE"),
                             oneParm.getDouble("QTY")))
                return false;
            //������ϸ��
            if (!onAddTableDOneRow(oneParm))
                return false;
        }
        getFee();
        return true;
    }

    /**
     * ��˿�����Ƿ���
     * @param invCode String
     * @param qty double
     * @return boolean
     */
    public boolean onChangeQty(String invCode, double qty) {
        //���Ҵ���
        String orgCode = getValueString("ORG_CODE");
        //�ܿ����
        double stockQty = inv.getStockQty(orgCode, invCode);
        if (stockQty < 0 || qty > stockQty) {
            messageBox("����" + invCode + "���������!");
            return false;
        }
        return true;
    }

    /**
     * ����һ������
     * @param parm TParm
     * @return boolean
     */
    public boolean onAddTableDOneRow(TParm parm) {
        //�������Ź��������Ź������
        if (parm.getValue("SEQMAN_FLG").equals("Y")) {
            if (!onOpenDilog(parm)) {
                return false;
            }
            //��¼����Ź��������
            if (map.get(parm.getValue("INV_CODE")) == null)
                map.put(parm.getValue("INV_CODE"), "Y");
            return true;
        }
        //û����Ź���Ĵ���
        if (!chooseBatchSeq(parm))
            return false;
        return true;
    }

    /**
     * ��������ϸ���һ������
     * @param parm TParm
     * @return int
     */
    public int onNewTableDDOneRow(TParm parm) {
        int row = tableD.addRow();
        //���ص�����
        setTableDValue(parm, row);
        return row;
    }

    /**
     * ��������ϸ��ӻ�������
     * @param parm TParm
     * @param row int
     */
    public void setTableDValue(TParm parm, int row) {
        TDataStore dataStore = tableD.getDataStore();
        String packCode = getValueString("PACK_CODE");
        String invCode = parm.getValue("INV_CODE");
        //����
        dataStore.setItem(row, "PACK_CODE", packCode);
        //�����(Ӧ�����Զ�����)
        dataStore.setItem(row, "PACK_SEQ_NO", getValueInt("PACK_SEQ_NO"));
        //���ʴ���
        dataStore.setItem(row, "INV_CODE", invCode);

        dataStore.setItem(row, "INVSEQ_NO", parm.getInt("INVSEQ_NO"));

        //�������
        dataStore.setItem(row, "BATCH_SEQ", parm.getData("BATCH_SEQ"));
        //����
        dataStore.setItem(row, "QTY", parm.getInt("STOCK_QTY"));
//        COST_PRICE
        double costPrice = parm.getDouble("UNIT_PRICE");
//        String discription=parm.getValue("DESCRIPTION");
        if (costPrice < 0) {
            TParm result = InvBaseTool.getInstance().getCostPrice(invCode);//wm2013-06-04	INVBaseTool.
            if (result.getErrCode() >= 0)
                costPrice = result.getDouble("COST_PRICE", 0);
        }
        dataStore.setItem(row, "COST_PRICE", costPrice);
        dataStore.setItem(row, "DESCRIPTION", parm.getValue("DESCRIPTION"));

        //��λ
        String valueStr = parm.getValue("STOCK_UNIT");
//        String valueStr = parm.getValue("UNIT_CHN_DESC");   //wm2013-06-17
        dataStore.setItem(row, "STOCK_UNIT", valueStr);
        if (valueStr == null)
            dataStore.setItem(row, "STOCK_UNIT", new TNull(String.class));
        //һ����ע��
        TParm result = INVPackDTool.getInstance().getPackType(packCode, invCode);
        String packtype = "";
        if (result != null && result.getErrCode() >= 0)
            packtype = result.getValue("PACK_TYPE",0);  
        String onceUseFlg = "Y";
//wm2013-06-17ע��        
//        if (packtype.equals("1"))
//            onceUseFlg = "N";
//        else
//            onceUseFlg = "Y";
        if (packtype.equals("0"))
        	 onceUseFlg = "N";
        else
        	onceUseFlg = "Y";
        dataStore.setItem(row, "ONCE_USE_FLG", onceUseFlg);
        tableD.setDSValue();
    }

    /**
     * ѡ���������
     * @param parm TParm
     * @return boolean
     */
    public boolean chooseBatchSeq(TParm parm) {
        //��Ҫ����������
        double qty = parm.getDouble("QTY");
        //�õ����д����ʵ��������
        TParm result = getBatchSeqInv(parm);
        if (result == null || result.getErrCode() < 0)
            return false;
        //������������θ���
        int rowCount = result.getCount();
        //ѭ��ȡ����������
        for (int i = 0; i < rowCount; i++) {
            //�ó�һ��
            TParm oneRow = result.getRow(i);
            double stockQty = oneRow.getDouble("STOCK_QTY");
            //��������㹻(���Ȳ���Ϊ0)
            if (stockQty > 0) {
                if (stockQty >= qty) {
                    oneRow.setData("STOCK_QTY", qty);
                    //���ò���һ�еķ���
                    onNewTableDDOneRow(oneRow);
                    //���˾���
                    return true;
                }
                //�������
                if (stockQty < qty) {
                    //������ֵ
                    qty = qty - stockQty;
                    //���ò���һ�еķ���
                    onNewTableDDOneRow(oneRow);  
                }
            }
        }
        return true;
    }

    /**
     * �õ�����Ź��������
     * @param parm TParm
     * @return TParm
     */
    public TParm getBatchSeqInv(TParm parm) {
        //���Ҵ���
        String orgCode = getValueString("ORG_CODE");
        //���ʴ���
        String invCode = parm.getValue("INV_CODE");
        //�õ����д����ʵ��������
        TParm result = inv.getAllStockQty(orgCode, invCode);
        if (result == null || result.getErrCode() < 0)
            return result;
        int count = result.getCount();
        if (count == 0) {
            //���ѡ��Ĳ�������
            clearNoEnoughInv();
            messageBox("���ʲ���!");
            this.callFunction("UI|new|setEnabled", false);
            this.callFunction("UI|save|setEnabled", false);
            return result;
        }
        return result;
    }

    /**
     * ����Ź���ѡ�����
     * @param parm TParm
     * @return boolean
     */
    public boolean onOpenDilog(TParm parm) {
        parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        parm.setData("INVDISPENSE", this);
        int qty = (Integer)this.openDialog("%ROOT%\\config\\inv\\INVStockDD.x",
                                           parm);
        if (qty == parm.getInt("QTY")) {
            //��¼�����Ѿ�ѡ�����
            return true;
        }
        messageBox("û��ѡ���㹻������!");
        //���ѡ��Ĳ�������
        clearNoEnoughInv();
        return false;
    }

    /**
     * ����һ�������ʵķ���
     * @return boolean
     */
    public boolean getFee() {
        double fee = 0.0;
        int rowCount = tableD.getRowCount();
        String invCode = "";
        for (int i = 0; i < rowCount; i++) {
        	
        		invCode = tableD.getItemString(i, "INV_CODE");
                if (map.get(invCode) == null) {
                    double invPrice = getInvPrice(invCode);
                    if (invPrice < 0)
                        return false;
                    //������д�ϵ���
                    tableD.setItem(i, "COST_PRICE", invPrice);
                    //����
                    int qty = tableD.getItemInt(i, "QTY");
                    if(tableD.getItemString(i, "ONCE_USE_FLG").equals("Y")){
                    	fee = fee + invPrice * qty;
                    }
                }
        }
        //һ���Բ����ܷ���
        this.setValue("ONCE_USE_COST", fee);
        //���������
        tableM.setItem(0, "ONCE_USE_COST", fee);
        return true;
    }

    /**
     * �õ����ʼ۸�
     * @param invCode String
     * @return double
     */
    public double getInvPrice(String invCode) {
        TParm result = InvBaseTool.getInstance().getCostPrice(invCode);//wm2013-06-04	INVBaseTool.
        if (result.getErrCode() < 0)
            return -1;
        return result.getDouble("COST_PRICE", 0);
    }

    /**
     * ���
     */
    public void onClear() {
        //������ݱ��
        if (!checkValueChange())
            return;
        //�������
        onClearText();
        //��ղ�������
        clearNoEnoughInv();
        //�ɱ༭״̬
        clearEnable(true);
        //�������ɱ༭
        setOtherEnable(true);
        //��ʼ�������ϵ�����
        initValue();
        this.callFunction("UI|new|setEnabled", true);
        this.callFunction("UI|save|setEnabled", false);
        //zhangyong20091110
        // �ر�������������
        tableM.setLockColumns("0,1,3,4,5,7,8,9,10");
//         setDate();
    }

    /**
     * ���û��ѡ���㹻������
     */
    public void clearNoEnoughInv() {
        //�������
        onClearTable(tableM);
        //���ϸ��
        onClearTable(tableD);
        //�������״̬
        isNew = false;
        this.callFunction("UI|new|setEnabled", true);
        this.callFunction("UI|save|setEnabled", false);
    }

    /**
     * �ı�༭״̬
     * @param statuse boolean
     */
    public void clearEnable(boolean statuse) {
        //����
        this.callFunction("UI|ORG_CODE|setEnabled", statuse);
        //����
        this.callFunction("UI|PACK_CODE|setEnabled", statuse);
        //���
        this.callFunction("UI|PACK_SEQ_NO|setEnabled", statuse);
    }

    /**
     * ��ս���
     */
    public void onClearText() {
        //��չ�������
        clearValue("PACK_CODE;PACK_DESC;PACK_SEQ_NO;QTY;USE_COST;ONCE_USE_COST;" +
                   "DISINFECTION_DATE;VALUE_DATE;DISINFECTION_USER");
    }

    /**
     * ���table
     * @param table TTable
     */
    public void onClearTable(TTable table) {
        //�������˸���
        table.acceptText();
        //���ѡ����
        table.clearSelection();
        //ɾ��������
        table.removeRowAll();
        //����޸ļ�¼
        table.resetModify();
    }

    /**
     * ����
     * @return boolean
     */
    public boolean onUpdate() {
        if (isNew) {
            messageBox("������ѡ������!");
            return false;
        }

        //zhnngyong20091110 begin
        //�������
        String inv_code = "";
        double qty = 0;
        for (int i = 0; i < tableD.getDataStore().rowCount(); i++) {
            inv_code = tableD.getDataStore().getItemString(i, "INV_CODE");
            qty = tableD.getDataStore().getItemDouble(i, "QTY");
            if (!onChangeQty(inv_code, qty)) {
                return false;
            }
        }
        //zhangyong20091110 end

        
        String seqFlg = returnParm.getValue("SEQ_FLG");
        if(seqFlg.equals("1")){//�����������
        	//��ø�����������
            barcode = this.getPackageBarcode();
        }else if(seqFlg.equals("0")){//��������ư�
        	barcode = returnParm.getValue("PACK_CODE") + "000000";
        }
        
        TParm saveParm = getSaveParm();
        if (saveParm == null)
            return false;
        //���ñ�������
        TParm result = TIOM_AppServer.executeAction("action.inv.INVAction",
            "onSavePackAge", saveParm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            //����ʧ��
            messageBox("����ʧ��!");
            return false;
        }
        //����ɹ�
        messageBox("����ɹ�!");
        //��ӡ��ⵥ
        onPrint();
        //�������������
        resertSave();
        this.onClear();
        return true;
    }

    /**
     * ����������������
     */
    public void resertSave() {
        //����������¼
        tableM.resetModify();
        tableM.setDSValue();
        //���ϸ�����¼
        tableD.resetModify();
        tableD.setDSValue();
        //�������ѡ���м�¼
        //����һ������Ҫ������´���һ��������
        isNew = true;
        this.callFunction("UI|new|setEnabled", false);
        this.callFunction("UI|save|setEnabled", false);
    }


    /**
     * ��ñ���parm
     * @return TParm
     */
    public TParm getSaveParm() {
    	int batchNo = 0;  //����
        //Ҫ���������
        TParm saveParm = new TParm();
        //ȡ��ǰʱ��
        Timestamp date = SystemTool.getInstance().getDate();

        //���ô����������������������
        TParm packMParm = dealPackMSaveParm(date);
        if (packMParm == null)
            return null;
        packMParm.setData("BARCODE", barcode);	//���barcode����
        if(!barcode.substring(6).equals("000000")){//����Ϊ������
        	batchNo = 0;
        	packMParm.setData("PACK_BATCH_NO", batchNo);
        }else{//����Ϊ���ư�
        	TParm p = new TParm();
        	p.setData("PACK_CODE", 0, packMParm.getData("PACK_CODE"));
        	
        	TParm tp = InvPackStockMTool.getInstance().queryPackBatch(p.getRow(0));
        	if (tp.getErrCode() < 0)
                err(tp.getErrCode() + " " + tp.getErrText());
        	if(tp.getData("MAXNO", 0) == null){
        		batchNo = 1;
        		packMParm.setData("PACK_BATCH_NO", batchNo);
        	}else{
        		batchNo = Integer.parseInt(tp.getData("MAXNO", 0).toString())+1;
        		packMParm.setData("PACK_BATCH_NO", batchNo);
        	}
        }
        //��ӱ���parm
        saveParm.setData("PACKMSAVEPARM", packMParm.getData());
        //��������ϸ
        TParm packDSaveParm = getPackDSaveParm(date);
        if (packDSaveParm == null)
            return null;
        //���barcode���룬����������δʹ������������
        for(int i=0;i<packDSaveParm.getCount();i++){
        	packDSaveParm.setData("BARCODE", i, barcode);
        	packDSaveParm.setData("USED_QTY", i, 0);
        	packDSaveParm.setData("NOTUSED_QTY", i, 0);
        	packDSaveParm.setData("PACK_BATCH_NO", i, batchNo);
        }
        //������������ϸ
        saveParm.setData("PACKDSAVEPARM", packDSaveParm.getData());
        //�õ�Ҫ�����parm
        TParm stockParm = getStockSaveParm();
        if (stockParm == null)
            return null;
        //��ӱ���parm
        saveParm.setData("SAVEPARM", stockParm.getData());

        return saveParm;
    }

    /**
     * �õ�����������
     * @param date Timestamp
     * @return TParm
     */
    public TParm getDisinfectionParm(Timestamp date) {
        TParm disinfectionParm = new TParm();
        disinfectionParm.setCount(0);
        return disinfectionParm;
    }

    /**
     * �����������������
     * @param date Timestamp
     * @return TParm
     */
    public TParm dealdisinfectionParm(Timestamp date) {
        //ȡϵͳ�¼�
        TParm dispenfectionParm = new TParm();
        //״̬ 0 �ڿ⣬1 ����, 2 �ѻ��� 3 ������ 4:ά����
        //״̬����Ϊ�Ѿ�����
        tableM.setItem(0, "STATUS", "3");
        //������¼
        dispenfectionParm = tableM.getDataStore().getRowParm(0);
        //����ʱ��
        dispenfectionParm.setData("DISINFECTION_DATE",
                                  TCM_Transform.getString(date));
        //����Ч�� VALID_DATE
        Timestamp validDate = dispenfectionParm.getTimestamp("VALID_DATE");
        if (validDate == null)
            dispenfectionParm.setData("VALUE_DATE",
                                      new TNull(Timestamp.class));
        dispenfectionParm.setData("DISINFECTION_USER",
                                  getValueString("DISINFECTION_USER"));
        //����
        dispenfectionParm.setData("QTY", 1);
        //������
        dispenfectionParm.setData("OPT_USER", Operator.getID());
        //ʱ��
        dispenfectionParm.setData("OPT_DATE", date);
        //����IP
        dispenfectionParm.setData("OPT_TERM", Operator.getIP());
        dispenfectionParm.setCount(1);
        return dispenfectionParm;
    }

    /**
     * �õ���������ϸ����parm
     * @param date Timestamp
     * @return TParm
     */
    public TParm getPackDSaveParm(Timestamp date) {
        //�������һ���޸�
        tableD.acceptText();
        //ȡ������
        TDataStore dataStore = tableD.getDataStore();
        //������
        int rowCount = tableD.getRowCount();
        //���̶�����������
        for (int i = 0; i < rowCount; i++) {
            //��עҲ����Ϊ��
            String eescription = dataStore.getItemString(i, "DESCRIPTION");
            if (eescription == null || eescription.length() == 0)
                dataStore.setItem(i, "DESCRIPTION", new TNull(String.class));
            //����Ա
            dataStore.setItem(i, "OPT_USER",
                              Operator.getID());
            //��������
            dataStore.setItem(i, "OPT_DATE", date);
            //������ַ
            dataStore.setItem(i, "OPT_TERM",
                              Operator.getIP());
            dataStore.setItem(i, "ORG_CODE", getValueString("ORG_CODE"));  //wm2013-06-06���
        }
        //ȡ��ȫ������
        return dataStore.getBuffer(dataStore.PRIMARY);

    }

    /**
     * �������������������������
     * @param date Timestamp
     * @return TParm
     */
    public TParm dealPackMSaveParm(Timestamp date) {
        //�����û����Ź����������
        if (!onCheckSeqFlg()) {
            return getNoseqManParm(date);
        }
        else //����Ź����������
            return getHaveSeqManParm(date);
    }

    /**
     * ��������Ź�������������������������
     * @param date Timestamp
     * @return TParm
     */
    public TParm getNoseqManParm(Timestamp date) {
        //�õ�����Ź������������������
        TParm packMSaveParm = getPackMSaveParm(date);
        if (packMSaveParm == null)
            return null;
        //��ӱ��淽ʽΪ����
        packMSaveParm.setData("SAVETYPE", "U");
        return packMSaveParm;
    }

    /**
     * ��������Ź�������������������������
     * @param date Timestamp
     * @return TParm
     */
    public TParm getHaveSeqManParm(Timestamp date) {
        //�õ���Ź������������������
        TParm packMSaveParm = getPackMSaveParm(date);
        if (packMSaveParm == null)
            return null;
        //��ӱ��淽ʽΪ����
        packMSaveParm.setData("SAVETYPE", "I");
        return packMSaveParm;
    }

    /**
     * �����������������������parm
     * @param date Timestamp
     * @return TParm
     */
    public TParm getPackMSaveParm(Timestamp date) {
        //�������һ���޸�
        tableM.acceptText();
        TDataStore dateStore = tableM.getDataStore();
        int rowCount = tableM.getRowCount();
        //���̶�����������
        for (int i = 0; i < rowCount; i++) {
            dateStore.setItem(i, "OPT_USER",
                              Operator.getID());
            dateStore.setItem(i, "OPT_DATE", date);
            dateStore.setItem(i, "OPT_TERM",
                              Operator.getIP());
            dateStore.setItem(i,"ORG_CODE",getValueString("ORG_CODE"));     //wm2013-06-06    ���
        }
        //����ȫ����
        return dateStore.getBuffer(dateStore.PRIMARY).getRow(0);
    }

    /**
     * ����parm
     * @return TParm
     */
    public TParm getStockSaveParm() {
        //������Ҳ���Ϊ��
        String orgCode = getValueString("ORG_CODE");
        //���
        if (orgCode == null || orgCode.length() == 0) {
            messageBox("��ѡ��������!");
            return null;
        }
        //�������
        TParm stockParm = new TParm();
        //��ϸ�������
        TDataStore dataStore = tableD.getDataStore();
        //��������
        int rowCount = tableD.getRowCount();
        //���е�����
        TParm tableDParm = dataStore.getBuffer(dataStore.PRIMARY);
        TParm stockDDParm = new TParm();
        //����ÿ������
        for (int i = 0; i < rowCount; i++) {
            //�õ�һ������
            TParm oneRow = dataStore.getRowParm(i);
            //������Ź���
            oneRow = setStockDDParm(oneRow);
            if (oneRow != null) {
                int row = stockDDParm.insertRow();
                oneRow.setData("PACK_FLG", "Y");
                stockDDParm.setRowData(row, oneRow);
                stockDDParm.setCount(row + 1);
            }
        }
        stockParm.setData("STOCKM",
                          dealSaveStockMParm(tableDParm, orgCode).getData());
        stockParm.setData("STOCKD",
                          dealStockDSaveParm(tableDParm, orgCode).getData());
        stockParm.setData("STOCKDD", stockDDParm.getData());

        return stockParm;
    }

    /**
     * ������Ź�������
     * @param parm TParm
     * @return TParm
     */
    public TParm setStockDDParm(TParm parm) {
        //���ʴ���
        String invCode = parm.getValue("INV_CODE");
        if (map.get(invCode) != null)
            return dealStockDDParm(parm);
        return null;
    }

    /**
     * ������Ź���
     * @param parm TParm
     * @return TParm
     */
    public TParm dealStockDDParm(TParm parm) {
        TParm stockDDParm = new TParm();
        //���ʴ���
        stockDDParm.setData("INV_CODE", parm.getData("INV_CODE"));
        //���
        stockDDParm.setData("INVSEQ_NO", parm.getData("INVSEQ_NO"));
        //��������
        stockDDParm.setData("PACK_CODE", parm.getData("PACK_CODE"));
        //���������
        stockDDParm.setData("PACK_SEQ_NO", parm.getData("PACK_SEQ_NO"));
        //������
        stockDDParm.setData("PACK_FLG", "Y");
        return stockDDParm;
    }

    /**
     * �õ�������������parm
     * @param parm TParm
     * @param orgCode String
     * @return TParm
     */
    public TParm dealSaveStockMParm(TParm parm, String orgCode) {
        //�洢�������
        Map stockMMap = new HashMap();
        TParm returnParm = new TParm();
        //�ܸ���
        int rowCount = parm.getCount();
        String invCode;
        TParm oneParm;
        //ѭ��
        for (int i = 0; i < rowCount; i++) {
            oneParm = parm.getRow(i);
            invCode = oneParm.getValue("INV_CODE");
            //��������
            String str = invCode;
            //���û�д���������һ������
            if (stockMMap.get(str) == null) {
                int row = returnParm.insertRow();
                stockMMap.put(str, row);
                oneParm.setData("ORG_CODE", orgCode);
                oneParm.setData("STOCK_QTY", oneParm.getInt("QTY"));
                returnParm.setRowData(row, oneParm);
                returnParm.setCount(row + 1);
            }
            else { //�Ѿ����ھ͸ı�����
                int row = (Integer) stockMMap.get(str);
                returnParm.setData("STOCK_QTY", row,
                                   (returnParm.getInt("STOCK_QTY", row) +
                                    oneParm.getInt("QTY")));
            }
        }
        return returnParm;
    }

    /**
     * �����������ϸ��������
     * @param parm TParm
     * @param orgCode String
     * @return TParm
     */
    public TParm dealStockDSaveParm(TParm parm, String orgCode) {
        //�洢�������
        Map stockMMap = new HashMap();
        TParm returnParm = new TParm();
        //�ܸ���
        int rowCount = parm.getCount();
        String invCode;
        int batchSeq;
        TParm oneParm;
        //ѭ��
        for (int i = 0; i < rowCount; i++) {
            oneParm = parm.getRow(i);
            invCode = oneParm.getValue("INV_CODE");
            batchSeq = oneParm.getInt("BATCH_SEQ");
            //��������
            String str = invCode + "|" + batchSeq;
            //���û�д���������һ������
            if (stockMMap.get(str) == null) {
                int row = returnParm.insertRow();
                stockMMap.put(str, row);
                oneParm.setData("ORG_CODE", orgCode);
                oneParm.setData("STOCK_QTY", oneParm.getInt("QTY"));
                returnParm.setRowData(row, oneParm);
                returnParm.setCount(row + 1);
            }
            else { //�Ѿ����ھ͸ı�����
                int row = (Integer) stockMMap.get(str);
                returnParm.setData("STOCK_QTY", row,
                                   (returnParm.getInt("STOCK_QTY", row) +
                                    oneParm.getInt("QTY")));
            }
        }
        return returnParm;

    }
    
    /** 
	 * ����������� 
	 *  */
	private String getPackageBarcode() {
		String barcode = SystemTool.getInstance().getNo("ALL", "INV",
				"INV_PACKSTOCKM", "No");
		return barcode;
	}

    /**
     * ������ݱ����ʾ��Ϣ�������ϸ��ʱ��ʾ���ݵı����
     * @return boolean
     */
    public boolean checkValueChange() {
        // ��������ݱ��
        if (checkData())
            switch (this.messageBox("��ʾ��Ϣ",
                                    "���ݱ���Ƿ񱣴�", this.YES_NO_CANCEL_OPTION)) {
                //����
                case 0:
                    if (!onUpdate())
                        return false;
                    return true;
                    //������
                case 1:
                    return true;
                    //����
                case 2:
                    return false;
            }
        return true;
    }

    /**
     * ������ݱ��
     * @return boolean
     */
    public boolean checkData() {
        //����
        if (tableM.isModified())
            return true;
        //ϸ��
        if (tableD.isModified())
            return true;
        return false;
    }

    /**
     * �Ƿ�رմ���(�������ݱ��ʱ��ʾ�Ƿ񱣴�)
     * @return boolean true �ر� false ���ر�
     */
    public boolean onClosing() {
        // ��������ݱ��
        if (checkData())
            switch (this.messageBox("��ʾ��Ϣ",
                                    "�Ƿ񱣴�", this.YES_NO_CANCEL_OPTION)) {
                //����
                case 0:
                    if (!onUpdate())
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
     * ��ӡ��ⵥ
     */
    public void onPrintInfo() {
        TParm parm = new TParm();
        int row = tableM.getSelectedRow();
        if (row < 0)
            row = 0;
        String packCode = tableM.getItemString(row, "PACK_CODE");
        if (packCode == null || packCode.length() == 0) {
            messageBox("������ѡ�����!");
            return;
        }
        //���������:
        int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
        String packCodeSeq = "" + packSeqNo;
        for (int i = packCodeSeq.length(); i < 4; i++) {
            packCodeSeq = "0" + packCodeSeq;
        }
        packCodeSeq = "" + packCodeSeq;
        
        int packBatchNo = tableM.getItemInt(row, "PACK_BATCH_NO");
        
        parm.setData("PACKCODESEQ", packCode + packCodeSeq);
        //��������
        parm.setData("PACK_CODE", packCode);
        //����
        parm.setData("PACK_DESC", getValueString("PACK_DESC"));
        //���������
        parm.setData("PACK_SEQ_NO", packSeqNo);
        TParm packParm = INVPackMTool.getInstance().getPackDesc(packCode);
        String packDesc = packParm.getValue("PACK_DESC", 0);

        //��������
        TParm result = InvPackStockMTool.getInstance().getPackDateBatch(packCode,
            packSeqNo,packBatchNo);		 
        String disinfectionDate = StringTool.getString( (Timestamp) result.
            getData(
                "DISINFECTION_DATE", 0), "yyyy/MM/dd");
        String valueDate = StringTool.getString( (Timestamp) result.getData(
            "VALUE_DATE", 0), "yyyy/MM/dd");
        String optUser = result.getValue("OPT_USER", 0);
        String disinfectionUser = result.getValue("DISINFECTION_USER", 0);

        parm.setData("PACK_DESC", packDesc);
        parm.setData("DISINFECTION_DATE", disinfectionDate);
        parm.setData("VALUE_DATE", valueDate);
        parm.setData("OPT_USER", getUserName(optUser));
        parm.setData("DISINFECTION_USER", getUserName(disinfectionUser));

        parm.setData("OPT_DATE",
                     StringTool.getString( (Timestamp) tableM.
                                          getItemData(row, "OPT_DATE"),
                                          "yyyy/MM/dd"));
        //���������
        parm.setData("T1", "PACK_SEQ_NO", packSeqNo);
        //��������
        parm.setData("T1", "PACK_CODE", packCode);
        //���������
        parm.setData("USE_COST", tableM.getItemDouble(row, "USE_COST"));
        //���������
        parm.setData("ONCE_USE_COST",
                     tableM.getItemDouble(row, "ONCE_USE_COST"));
        TComboBox value;
        //�������
        value = (TComboBox)this.getComponent("ORG_CODE");
        parm.setData("ORG_CODE", value.getSelectedName());
        parm.setData("HOSP_AREA", getHospArea());
        //���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVPackPackage.jhw",
                             parm);
    }

 
    /**
     * ��ӡ��Ϣ��
     */
    public void onPrint() {
    	TParm parm = new TParm();
    	int row = tableM.getSelectedRow();
    	if (row < 0)
    		row = 0;
    	String packCode = tableM.getItemString(row, "PACK_CODE");
    	if (packCode == null || packCode.length() == 0) {
    		messageBox("������ѡ�����!");
    		return;
    	}
    	//���������:
    	int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
    	//��������
    	parm.setData("PACK_CODE", packCode);
    	//���������
    	parm.setData("PACK_SEQ_NO", packSeqNo);
    	
    	TParm packList = new TParm();
		packList = INVNewRepackTool.getInstance().queryPackList(parm);
		
		TParm packInfo = new TParm();
    	packInfo=INVNewRepackTool.getInstance().queryPackageInfo(parm);
    	
    	TParm reportTParm = new TParm();
		reportTParm.setData("PACK_DESC", "TEXT", packInfo.getValue("PACK_DESC", 0));	//����
		reportTParm.setData("PACK_BARCODE", "TEXT", packInfo.getValue("BARCODE", 0));	//����
		reportTParm.setData("PACK_USER", "TEXT", packInfo.getValue("USER_NAME", 0));	//�����Ա
		reportTParm.setData("PACK_DATE", "TEXT", packInfo.getValue("OPT_DATE", 0).toString().substring(0, 10));	//�������
		
		int tag=packList.getCount("QTY")%2;
		//�������
        TParm tableParm = new TParm();
		if(packList.getCount("QTY") == 1){
			tableParm.addData("SEQ_F", 1);
			tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", 0));
			tableParm.addData("QTY_F", packList.getData("QTY", 0));
			tableParm.addData("SEQ_S", "");
			tableParm.addData("CHN_DESC_S", "");
			tableParm.addData("QTY_S", "");
		}else if(packList.getCount("QTY") > 1){
			
			if(tag == 0){
				for(int i=0;i<packList.getCount("QTY");){
					tableParm.addData("SEQ_F", i+1);
					tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", i));
					tableParm.addData("QTY_F", packList.getData("QTY", i));
					tableParm.addData("SEQ_S", i+2);
					tableParm.addData("CHN_DESC_S", packList.getData("INV_CHN_DESC", i+1));
					tableParm.addData("QTY_S", packList.getData("QTY", i+1));
					i=i+2;
				}
			}
			if(tag == 1){
				for(int i=0;i<packList.getCount("QTY")-1;){
					tableParm.addData("SEQ_F", i+1);
					tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", i));
					tableParm.addData("QTY_F", packList.getData("QTY", i));
					tableParm.addData("SEQ_S", i+2);
					tableParm.addData("CHN_DESC_S", packList.getData("INV_CHN_DESC", i+1));
					tableParm.addData("QTY_S", packList.getData("QTY", i+1));
					i=i+2;
				}
				tableParm.addData("SEQ_F", packList.getCount("QTY"));
				tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", packList.getCount("QTY")-1));
				tableParm.addData("QTY_F", packList.getData("QTY", packList.getCount("QTY")-1));
				tableParm.addData("SEQ_S", "");
				tableParm.addData("CHN_DESC_S", "");
				tableParm.addData("QTY_S", "");
			}
		}
		
		
		
		tableParm.setCount(tableParm.getCount("SEQ_F"));
		
	    tableParm.addData("SYSTEM", "COLUMNS", "SEQ_F");
	    tableParm.addData("SYSTEM", "COLUMNS", "CHN_DESC_F");
	    tableParm.addData("SYSTEM", "COLUMNS", "QTY_F");
	    tableParm.addData("SYSTEM", "COLUMNS", "SEQ_S");
	    tableParm.addData("SYSTEM", "COLUMNS", "CHN_DESC_S");
	    tableParm.addData("SYSTEM", "COLUMNS", "QTY_S");

	    reportTParm.setData("TABLE", tableParm.getData());
		
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVNewPackageList.jhw",
				reportTParm);
    	
    }
    
    
//    /**
//     * ��ӡ��Ϣ��
//     */
//    public void onPrint() {
//        TParm parm = new TParm();
//        int row = tableM.getSelectedRow();
//        if (row < 0)
//            row = 0;
//        String packCode = tableM.getItemString(row, "PACK_CODE");
//        if (packCode == null || packCode.length() == 0) {
//            messageBox("������ѡ�����!");
//            return;
//        }
//        //���������:
//        int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
//        String packCodeSeq = "" + packSeqNo;
//        for (int i = packCodeSeq.length(); i < 4; i++) {
//            packCodeSeq = "0" + packCodeSeq;
//        }
//        packCodeSeq = "" + packCodeSeq;
//        parm.setData("PACKCODESEQ", packCode + packCodeSeq);
//        //��������
//        parm.setData("PACK_CODE", packCode);
//        //����
//        parm.setData("PACK_DESC", getValueString("PACK_DESC"));
//        //���������
//        parm.setData("PACK_SEQ_NO", packSeqNo);
//
//
//        //��������
//        TParm result = InvPackStockMTool.getInstance().getPackDate(packCode,
//            packSeqNo);   //wm2013-06-04   INVPackStockMTool.
//        String disinfectionDate = StringTool.getString( (Timestamp) result.
//            getData(
//                "DISINFECTION_DATE", 0), "yyyy/MM/dd");
//
//        String optUser = result.getValue("OPT_USER", 0);
//        String disinfectionUser = result.getValue("DISINFECTION_USER", 0);
//
//        //parm.setData("PACK_DESC", packDesc);
//        parm.setData("DISINFECTION_DATE", disinfectionDate);
//        parm.setData("VALUE_DATE", "");  //2013-07-17
//        parm.setData("OPT_USER", getUserName(optUser));
//        parm.setData("DISINFECTION_USER", getUserName(disinfectionUser));
//
//        parm.setData("OPT_DATE",
//                     StringTool.getString( (Timestamp) tableM.
//                     getItemData(row, "OPT_DATE"),"yyyy/MM/dd"));
//        //���������
//        parm.setData("T1", "PACK_SEQ_NO", packSeqNo);
//        //��������
//        parm.setData("T1", "PACK_CODE", packCode);
//        //���������
//        parm.setData("USE_COST", tableM.getItemDouble(row, "USE_COST"));
//        //���������
//        parm.setData("ONCE_USE_COST",
//                     tableM.getItemDouble(row, "ONCE_USE_COST"));
//
//        String org_code = ((TTextFormat)this.getComponent("ORG_CODE")).getText();  //wm2013-06-17
//        parm.setData("ORG_CODE", org_code);			//wm2013-06-17
//        parm.setData("HOSP_AREA", getHospArea());
//        //�������
//        TParm tableParm = new TParm();
//        String sql = "";
//        double sum_amt = 0;
//        for (int i = 0; i < tableD.getRowCount(); i++) {
//
//            sql = INVSQL.getInvInfo(tableD.getDataStore().getItemString(i,
//            "INV_CODE"));
//            TParm inv_base = new TParm(TJDODBTool.getInstance().select(sql));
//            tableParm.addData("INV_DESC", inv_base.getValue("INV_CHN_DESC", 0));
//            tableParm.addData("INVSEQ_NO",
//                              tableD.getDataStore().getItemInt(i, "INVSEQ_NO"));
//            tableParm.addData("PRICE",
//                              tableD.getDataStore().getItemDouble(i,
//                "COST_PRICE"));
//            tableParm.addData("QTY",
//                              tableD.getDataStore().getItemDouble(i, "QTY"));
//            tableParm.addData("UNIT", inv_base.getValue("UNIT_CHN_DESC", 0));
//            tableParm.addData("AMT", StringTool.round(tableD.getDataStore().
//                getItemDouble(i, "COST_PRICE") *
//                tableD.getDataStore().getItemDouble(i, "QTY"), 2));
//            sum_amt += StringTool.round(tableD.getDataStore().
//                                        getItemDouble(i, "COST_PRICE") *
//                                        tableD.getDataStore().getItemDouble(i,
//                "QTY"), 2);
//        }
//        tableParm.addData("INV_DESC", "�ϼ�:");
//        tableParm.addData("INVSEQ_NO","");
//        tableParm.addData("PRICE","");
//        tableParm.addData("QTY","");
//        tableParm.addData("UNIT", "");
//        tableParm.addData("AMT", sum_amt);
//
//        tableParm.setCount(tableParm.getCount("INV_DESC"));
//        tableParm.addData("SYSTEM", "COLUMNS", "INV_DESC");
//        tableParm.addData("SYSTEM", "COLUMNS", "INVSEQ_NO");
//        tableParm.addData("SYSTEM", "COLUMNS", "PRICE");
//        tableParm.addData("SYSTEM", "COLUMNS", "QTY");
//        tableParm.addData("SYSTEM", "COLUMNS", "UNIT");
//        tableParm.addData("SYSTEM", "COLUMNS", "AMT");
//
//        parm.setData("TABLE", tableParm.getData());
//
//        //���ô�ӡ����
//        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVPackPackage2.jhw",
//                             parm);
//    }


    /**
     * �õ�ҽԺ���
     * @return String
     */
    public String getHospArea() {
        String sql = INVSQL.getHospArea();
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        String hospChnAbn = result.getValue("HOSP_CHN_ABN", 0);
        return hospChnAbn;
    }

    /**
     * �õ��û�����
     * @param userId String
     * @return String
     */
    public String getUserName(String userId) {
        String sql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='" +
            userId + "'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0)
            return userId;
        return result.getValue("USER_NAME", 0);

    }

    /**
     * ��ӡ����
     */
    public void onBarcode() {
        int row = tableM.getSelectedRow();
        if (row < 0)
            row = 0;
        String packCode = tableM.getItemString(row, "PACK_CODE");
        if (packCode == null || packCode.length() == 0) {
            messageBox("������ѡ�����!");
            return;
        }
        //���������
        int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
        if(packSeqNo == 0){//���ư������ӡ
        	
        	Object obj = this.openDialog("%ROOT%\\config\\inv\\INVPrintCount.x");
        	if (obj == null)
        		return;
        	TParm parm = (TParm) obj;
        	int times =  Integer.parseInt(parm.getValue("PRINTCOUNT"));
    
        	for(int i=0;i<times;i++){
        		this.printDisposablePackageBarcode();
        	}
        	
        }else if(packSeqNo != 0){//�����������ӡ
        	this.printDisposablePackageBarcode();
        }
    }
    /**
     * һ�������ư������ӡ����
     */
    private void printDisposablePackageBarcode(){
    	int row = tableM.getSelectedRow();
        if (row < 0)
            row = 0;
        String packCode = tableM.getItemString(row, "PACK_CODE");
    	//���������
        int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
        //���������ţ���Ҫָ���ư������������Ŷ�Ϊ0��
        int packBatchNo = tableM.getItemInt(row, "PACK_BATCH_NO");
        //�������
        TParm packCodeSeqParm = new TParm();
        TParm packParm = INVPackMTool.getInstance().getPackDesc(packCode);
        String packDesc = packParm.getValue("PACK_DESC", 0);
        String packDept = ((TTextFormat)this.getComponent("ORG_CODE")).getText();   
        //��������
        TParm result = InvPackStockMTool.getInstance().getPackDateBatch(packCode,packSeqNo,packBatchNo);    
        String packageDate = StringTool.getString( (Timestamp) result.getData("OPT_DATE", 0), "yyyy/MM/dd");
        
        int valid = Integer.parseInt(result.getData("VALUE_DATE",0).toString());
        Calendar cal = new GregorianCalendar();
        cal.set(Integer.parseInt(packageDate.substring(0, 4)), Integer.parseInt(packageDate.substring(5, 7))-1, Integer.parseInt(packageDate.substring(8)));
        cal.add(cal.DATE,valid);//��������������N��
        Date d=cal.getTime(); 
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(d);
        
        String optUser = result.getValue("OPT_USER", 0);
        
        TParm reportParm = new TParm();
        reportParm.setData("PACK_CODE_SEQ","TEXT", result.getData("BARCODE", 0));
        reportParm.setData("PACK_DESC","TEXT",packDesc);
        reportParm.setData("PACK_DEPT","TEXT","(" + packDept + ")");
        reportParm.setData("PACKAGE_DATE","TEXT",packageDate);
        reportParm.setData("VALUE_DATE","TEXT",dateString);
        reportParm.setData("OPT_USER","TEXT",getUserName(optUser));
        reportParm.setData("PACK_DATE","TEXT",packageDate);
        reportParm.setData("PACK_CODE_SEQ_SEC","TEXT", result.getData("BARCODE", 0));
        //���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVGeneralOncePackageBarcode.jhw", reportParm, true);

    }
    
    
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
}
