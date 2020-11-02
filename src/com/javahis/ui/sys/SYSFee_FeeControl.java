package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTreeNode;
import jdo.sys.SYSRuleTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TDS;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import com.dongyang.util.TypeTool;
import java.sql.Timestamp;
import java.util.Map;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SYSUnitTool;

import com.javahis.ui.testOpb.tools.OrderTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.JavaHisDebug;
import com.dongyang.jdo.TDSObject;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_Database;
import jdo.sys.SYSRegionTool;

/**
 * <p>
 * Title:��Ŀ�շ��ֵ䵵 
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
 * @author ZangJH
 * @version 1.0
 */
public class SYSFee_FeeControl extends TControl {

    /**
     * �����
     */
    private TDSObject bigObject = new TDSObject();

    /**
     * �����������ֵ������иı���SYS_FEE�������µ�һ������
     */
    private double oldOwnPrice = 0.0;
    private double oldNhiPrice = 0.0;
    private double oldGovPrice = 0.0;
    private String orderCode = "";
    //$$ add by lx ����ѯ��SQL
    private String querySQL="";

    /**
     * ����
     */
    private TTreeNode treeRoot;
    /**
     * ��Ź�����𹤾�
     */
    private SYSRuleTool ruleTool;
    /**
     * �������ݷ���datastore���ڶ��������ݹ���
     */
    private TDataStore treeDataStore = new TDataStore();
    /**
     * SYS_FEE_HISTORY���TDS
     */
    TDS sysFeeHisDS = new TDS();
    /**
     * SYS_FEE���TDS
     */
    TDS sysFeeDS = new TDS();

    /**
     * ���ǰѡ�е��к�
     */
    int selRow = -1;

    /**
     * ���пؼ�������
     */
    private String controlName = "LET_KEYIN_FLG;OPD_FIT_FLG;EMG_FIT_FLG;IPD_FIT_FLG;"
            + "HRM_FIT_FLG;DR_ORDER_FLG;TRANS_OUT_FLG;EXEC_ORDER_FLG;START_DATE;"
            + "END_DATE;ORDER_DESC;PY1;NHI_FEE_DESC;"
            + "TRADE_ENG_DESC;ALIAS_DESC;SPECIFICATION;ALIAS_PYCODE;DESCRIPTION;"
            + "HYGIENE_TRADE_CODE;NHI_CODE_I;NHI_CODE_O;NHI_CODE_E;HABITAT_TYPE;"
            + "UNIT_CODE;MR_CODE;CHARGE_HOSP_CODE;ORDER_CAT1_CODE;LCS_CLASS_CODE;"
            + "EXEC_DEPT_CODE;INSPAY_TYPE;OWN_PRICE;OWN_PRICE2;IO_CODE;"//add by wanglong 20140217 �����Բ���
            + "OWN_PRICE3;ADDPAY_RATE;ADDPAY_AMT;ACTION_CODE;IS_REMARK;IN_OPFLG;"//add by wanglong 20131106 ����ҽ��ע��
            + "MAN_CODE;SUPPLIES_TYPE;ORD_SUPERVISION;USE_CAT;ORDER_DEPT_CODE";//shibl add ҽ�Ƽ������

    /**
     * ����Ŀؼ�
     */
    // ��
    TTree tree;
    // ����
    TTable upTable;

    // ȫ��/����/ͣ�ñ��
    TRadioButton ALL;
    TRadioButton ACTIVE_Y;
    TRadioButton ACTIVE_N;

    // ����ע��
    TCheckBox ACTIVE_FLG;
    // �����ֶ��Ǽ�ע��
    TCheckBox LET_KEYIN_FLG;
    // ��,��,ס,��,��ҽ���ñ��
    TCheckBox OPD_FIT_FLG;
    TCheckBox EMG_FIT_FLG;
    TCheckBox IPD_FIT_FLG;
    TCheckBox HRM_FIT_FLG;
    TCheckBox DR_ORDER_FLG;
    TCheckBox IN_OPFLG;//����ҽ��ע��add by wanglong 20131106
    TTextFormat IO_CODE;//�����Բ���add by wanglong 20140217
    // ת��ע��
    TCheckBox TRANS_OUT_FLG;
    TCheckBox EXEC_ORDER_FLG;

    TTextFormat START_DATE;
    TTextFormat END_DATE;
    TTextFormat ORD_SUPERVISION;
    
    TTextField ACTION_CODE;

    TTextField QUERY;
    //fux modify 20141216 
    TTextField QUERY_N;
    TTextField ORDER_CODE;
    TTextField ORDER_DESC;
    TTextField PY1;
    TTextField NHI_FEE_DESC;
    TTextField TRADE_ENG_DESC;
    TTextField ALIAS_DESC;
    TTextField SPECIFICATION;
    TTextField ALIAS_PYCODE;
    TTextField DESCRIPTION;
    TTextField HYGIENE_TRADE_CODE;
    TTextField NHI_CODE_I;
    TTextField NHI_CODE_O;
    TTextField NHI_CODE_E;

    TComboBox HABITAT_TYPE;
    TTextFormat UNIT_CODE;
    TTextFormat MR_CODE;
    TTextFormat CHARGE_HOSP_CODE;
    TTextFormat ORDER_CAT1_CODE;
    TTextFormat ORDER_DEPT_CODE;
    TComboBox LCS_CLASS_CODE;
    TComboBox TRANS_HOSP_CODE;
    TTextFormat EXEC_DEPT_CODE;
    TComboBox INSPAY_TYPE;

    TNumberTextField OWN_PRICE;
    TNumberTextField NHI_PRICE;
    TNumberTextField GOV_PRICE;
    TNumberTextField ADDPAY_RATE;
    TNumberTextField ADDPAY_AMT;

    /**
     * ��ʼ��
     */
    public SYSFee_FeeControl() {
    }

    public void onInit() { // ��ʼ������
        super.onInit();
        myInitControler();

        // ��ʼ����
        onInitTree();
        // ��tree��Ӽ����¼�
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        // ��ʼ�����
        onInitNode();
        canSave();
        // ========pangben modify 20110427 start Ȩ�����
        // ��ʼ��Ժ��
        setValue("REGION_CODE", Operator.getRegion());
        TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
                this.getValueString("REGION_CODE")));
        // ===========pangben modify 20110427 stop

    }

    private void canSave() {
        Object obj = this.getParameter();
        if (obj == null)
            return;
        if (obj.equals("QUERY"))
            callFunction("UI|save|setEnabled", false);
        
        if(obj.equals("QUERY_N")){
        	callFunction("UI|save|setEnabled", false);  
        }
    }

    /**
     * ��ʼ����
     */
    public void onInitTree() {
        // �õ�����
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        if (treeRoot == null)
            return;
        // �����ڵ����������ʾ
        treeRoot.setText("������Ŀ�շѷ���");
        // �����ڵ㸳tag
        treeRoot.setType("Root");
        // ���ø��ڵ��id
        treeRoot.setID("");
        // ������нڵ������
        treeRoot.removeAllChildren();
        // ���������ʼ������
        callMessage("UI|TREE|update");
    }

    /**
     * ��ʼ�����Ľ��
     */

    public void onInitNode() {
        // ��dataStore��ֵ
        treeDataStore
                .setSQL("SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='SYS_FEE_RULE'");
        // �����dataStore���õ�������С��0
        if (treeDataStore.retrieve() <= 0)
            return;
        // ��������,�Ǳ�������еĿ�������
        ruleTool = new SYSRuleTool("SYS_FEE_RULE");
        if (ruleTool.isLoad()) { // �����۽ڵ����:datastore���ڵ����,�ڵ���ʾ����,,�ڵ�����
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                    "CATEGORY_CODE", "CATEGORY_CHN_DESC", "Path", "SEQ");
            // ѭ����������ڵ�
            for (int i = 0; i < node.length; i++)
                treeRoot.addSeq(node[i]);
        }
        // �õ������ϵ�������
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        // ������
        tree.update();
        // ��������Ĭ��ѡ�нڵ�
        tree.setSelectNode(treeRoot);
    }

    /**
     * ������
     * 
     * @param parm
     *            Object
     */
    public void onTreeClicked(Object parm) {
    	
    	if(ACTIVE_Y.isSelected()){
    		QUERY.setVisible(true); 	  
    		QUERY_N.setVisible(false);   
    	}
    	else if(ACTIVE_N.isSelected()){  
    		QUERY.setVisible(false); 	
    		QUERY_N.setVisible(true);
    	}else if (ALL.isSelected()){    
       		QUERY.setVisible(true); 	  
    		QUERY_N.setVisible(false); 
    	}  
        // ���
        onClear();

        // ������ť������
        callFunction("UI|new|setEnabled", false);
        // �õ�������Ľڵ����
        // TTreeNode node = (TTreeNode) parm;
        TTreeNode node = tree.getSelectNode();
        // TTreeNode node=tree.getSelectionNode();
        if (node == null)
            return;
        // �õ�table����
        TTable table = (TTable) this.callFunction("UI|upTable|getThis");
        // table�������иı�ֵ
        table.acceptText();
        // �жϵ�����Ƿ������ĸ����
        if (node.getType().equals("Root")) {
            // ��������ĸ��ӵ�table�ϲ���ʾ����
            table.removeRowAll();
        } else { // �����Ĳ��Ǹ����
            // �õ���ǰѡ�еĽڵ��idֵ
            String id = node.getID();
            // �õ���ѯTDS��SQL��䣨ͨ����һ���IDȥlike���е�orderCode��
            // ======pangben modify 20110427 ��Ӳ���
            String sql = getSQL(id, Operator.getRegion());
            //$$===add by lx 2013/01/06====$$//
            querySQL=sql;
            // ��ʼ��table��TDS
            initTblAndTDS(sql);

        }
        // ��table���ݼ���������
        // table.setSort("ORDER_CODE");
        // table��������¸�ֵ
        // table.sort();
        // �õ���ǰ�������ID
        String nowID = node.getID();

        int classify = 1;
        if (nowID.length() > 0)
            classify = ruleTool.getNumberClass(nowID) + 1;
        // �������С�ڵ�,��������һ��(ʹ������ť����)
        if (classify > ruleTool.getClassifyCurrent()) {
            this.callFunction("UI|new|setEnabled", true);
        }
    }

    /**
     * �õ���ʼ��TDS��SQL���(����Ŀǰ�������õ���Ŀ�б�����START_DATE/END_DATE ֻ��ACTIVE_FLGΪ��Y��)
     * 
     * @return String ===============pangben modify 20110427 ����������
     */
    private String getSQL(String orderCode, String regionCode) {
        String active = "";

        if (ACTIVE_Y.isSelected()) { // ����
            active = " AND ACTIVE_FLG='Y'";
            setEnabledForCtl(true);
        } else if (ACTIVE_N.isSelected()) { // ͣ��
            active = " AND ACTIVE_FLG='N'";
            setEnabledForCtl(false);
        } else { // ȫ��
            setEnabledForCtl(false);
        }
        // =========pangben modify 20110427 start
        String region = "";
        if (null != regionCode && !"".equals(regionCode))
            region = " AND (REGION_CODE='" + regionCode
                    + "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
        // =========pangben modify 20110427 stop
        String sql = ""; 
        // ���������
        if (orderCode != null && orderCode.length() > 0)
            sql = " SELECT * " + " FROM SYS_FEE_HISTORY" + " WHERE "  
                    + " ORDER_CODE like '" + orderCode + "%'" + active + region
//			        //fux modify 20141020 ֻ��ʾϸ�� 
//					+ " AND ORDERSET_FLG = 'N'"     
                    + " ORDER BY ORDER_CODE";

        return sql;
    }

    /**
     * ��ʼ������ı�������еĿؼ�����
     * 
     * @param sql
     *            String  
     */
    public void initTblAndTDS(String sql) {
        sysFeeHisDS.setSQL(sql);  
        sysFeeHisDS.retrieve(); 
        // ���û��������ձ���ϵ�����
        if (sysFeeHisDS.rowCount() <= 0) {
            upTable.removeRowAll();
        }

        upTable.setDataStore(sysFeeHisDS);
        upTable.setDSValue();

    }

    /**
     * ���ȵõ�����UI�Ŀؼ�����/ע����Ӧ���¼� ����
     */
    public void myInitControler() {

        tree = (TTree) callFunction("UI|TREE|getThis");

        // �õ�table�ؼ�
        upTable = (TTable) this.getComponent("upTable");

        ALL = (TRadioButton) this.getComponent("ALL");
        ACTIVE_Y = (TRadioButton) this.getComponent("ACTIVE_Y");
        ACTIVE_N = (TRadioButton) this.getComponent("ACTIVE_N");

        ACTIVE_FLG = (TCheckBox) this.getComponent("ACTIVE_FLG");
        LET_KEYIN_FLG = (TCheckBox) this.getComponent("LET_KEYIN_FLG");
        OPD_FIT_FLG = (TCheckBox) this.getComponent("OPD_FIT_FLG");
        EMG_FIT_FLG = (TCheckBox) this.getComponent("EMG_FIT_FLG");
        IPD_FIT_FLG = (TCheckBox) this.getComponent("IPD_FIT_FLG");
        HRM_FIT_FLG = (TCheckBox) this.getComponent("HRM_FIT_FLG");
        DR_ORDER_FLG = (TCheckBox) this.getComponent("DR_ORDER_FLG");
        IN_OPFLG = (TCheckBox) this.getComponent("IN_OPFLG");//add by wanglong 20131106
        IO_CODE = (TTextFormat) this.getComponent("IO_CODE");//add by wanglong 20140217
        TRANS_OUT_FLG = (TCheckBox) this.getComponent("TRANS_OUT_FLG");
        EXEC_ORDER_FLG = (TCheckBox) this.getComponent("EXEC_ORDER_FLG");

        START_DATE = (TTextFormat) this.getComponent("START_DATE");
        END_DATE = (TTextFormat) this.getComponent("END_DATE");
        ORD_SUPERVISION=(TTextFormat)this.getComponent("ORD_SUPERVISION");
        ACTION_CODE = (TTextField) this.getComponent("ACTION_CODE");

        QUERY = (TTextField) this.getComponent("QUERY");
        //fux  modify 20141216  
        QUERY_N= (TTextField)this.getComponent("QUERY_N");
        
        ORDER_CODE = (TTextField) this.getComponent("ORDER_CODE");
        ORDER_DESC = (TTextField) this.getComponent("ORDER_DESC");
        PY1 = (TTextField) this.getComponent("PY1");
        NHI_FEE_DESC = (TTextField) this.getComponent("NHI_FEE_DESC");
        TRADE_ENG_DESC = (TTextField) this.getComponent("TRADE_ENG_DESC");

        ALIAS_DESC = (TTextField) this.getComponent("ALIAS_DESC");
        SPECIFICATION = (TTextField) this.getComponent("SPECIFICATION");
        ALIAS_PYCODE = (TTextField) this.getComponent("ALIAS_PYCODE");
        DESCRIPTION = (TTextField) this.getComponent("DESCRIPTION");
        HYGIENE_TRADE_CODE = (TTextField) this
                .getComponent("HYGIENE_TRADE_CODE");

        NHI_CODE_I = (TTextField) this.getComponent("NHI_CODE_I");
        NHI_CODE_O = (TTextField) this.getComponent("NHI_CODE_O");
        NHI_CODE_E = (TTextField) this.getComponent("NHI_CODE_E");

        HABITAT_TYPE = (TComboBox) this.getComponent("HABITAT_TYPE");
        UNIT_CODE = (TTextFormat) this.getComponent("UNIT_CODE");
        MR_CODE = (TTextFormat) this.getComponent("MR_CODE");
        CHARGE_HOSP_CODE = (TTextFormat) this.getComponent("CHARGE_HOSP_CODE");
        ORDER_CAT1_CODE = (TTextFormat) this.getComponent("ORDER_CAT1_CODE");
        ORDER_DEPT_CODE = (TTextFormat) this.getComponent("ORDER_DEPT_CODE");

        LCS_CLASS_CODE = (TComboBox) this.getComponent("LCS_CLASS_CODE");
        TRANS_HOSP_CODE = (TComboBox) this.getComponent("TRANS_HOSP_CODE");
        EXEC_DEPT_CODE = (TTextFormat) this.getComponent("EXEC_DEPT_CODE");
        INSPAY_TYPE = (TComboBox) this.getComponent("INSPAY_TYPE");

        NHI_PRICE = (TNumberTextField) this.getComponent("OWN_PRICE2");
        GOV_PRICE = (TNumberTextField) this.getComponent("OWN_PRICE3");
        OWN_PRICE = (TNumberTextField) this.getComponent("OWN_PRICE");
        ADDPAY_RATE = (TNumberTextField) this.getComponent("ADDPAY_RATE");
        ADDPAY_AMT = (TNumberTextField) this.getComponent("ADDPAY_AMT");  

        // ������tableע�ᵥ���¼����� 
        this.callFunction("UI|upTable|addEventListener", "upTable->"
                + TTableEvent.CLICKED, this, "onUpTableClicked");
        TParm parm = new TParm();     
        parm.setData("CAT1_TYPE", "");
        parm.setData("RX_TYPE",6);                  
        // ���õ����˵�     
        QUERY.setPopupMenuParameter("UD", getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);    
        // ������ܷ���ֵ����  
        QUERY.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        
        
        // ���õ����˵�
        QUERY_N.setPopupMenuParameter("UD", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeeNPopup.x"), parm);   
        // ������ܷ���ֵ����
        QUERY_N.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturnN");

        // ������ť������
        callFunction("UI|new|setEnabled", false);
    }

    /**
     * ���ܷ���ֵ����
     * 
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            ORDER_CODE.setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            ORDER_DESC.setValue(order_desc);
        // ��ղ�ѯ�ؼ�
        QUERY.setValue("");
        onQuery();
    }
    
    
    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj  
     */
    public void popReturnN(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            ORDER_CODE.setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            ORDER_DESC.setValue(order_desc);
        //��ղ�ѯ�ؼ�    
        QUERY_N.setValue("");
        onQuery();
    }
    

    public void onQuery() {

        String selCode = ORDER_CODE.getValue();
        if ("".equals(selCode)) {
            this.messageBox("������Ҫ��ѯ��Ŀ�ı��룡");
        }

        // �õ���ѯcode��SQL���
        // ===============pangben modify 20110427 start ���region����
        String sql = getSQL(selCode, this.getValueString("REGION_CODE"));
        // ===============pangben modify 20110427 stop
        //add by lx 2013/01/06
        querySQL=sql;
        
        // ��ʼ��table��TDS
        initTblAndTDS(sql);
        // ����ѯ���ֻ��һ�����ݵ�ʱ��ֱ����ʾ����ϸ��Ϣ
        if (upTable.getRowCount() == 1) {
            onUpTableClicked(0);
        }

    }

    /**
     * ���������table�¼�
     */
    public void onUpTableClicked(int row) {
        //System.out.println("==onUpTableClicked row=="+row);
        // ��ǰѡ�е��к�
        selRow = row;
        // �������ؼ���ֵ
        clearCtl();
        // �õ����parm
        TParm tableDate = ((TDS) upTable.getDataStore()).getBuffer(TDS.PRIMARY)
                .getRow(selRow);
        // �����еĿؼ�ֵ
        // setValueForDownCtl(tableDate, row);
        setValueForDownCtl(tableDate);
        // ������ʾ��ȫ������ʱ��Ĭ�ϲ��ɱ༭(����״̬�ָ��༭/ͣ��״̬���ɱ༭)
        if (ALL.isSelected()) {
            boolean activeFlg = ACTIVE_FLG.isSelected();
            setEnabledForCtl(activeFlg);
        }
        // ��¼��������������SYSFEE���ݵĹؼ�����(��������ʱ��orderCode���䣬ֻ�������۸���˵��Ҫ���£�����)
        oldOwnPrice = TypeTool.getDouble(OWN_PRICE.getText());
        oldNhiPrice = TypeTool.getDouble(NHI_PRICE.getText());
        oldGovPrice = TypeTool.getDouble(GOV_PRICE.getText());
        orderCode = ORDER_CODE.getValue();
    }

    /**
     * ��ճ���table����Ŀؼ���ֵ
     */
    public void clearCtl() {

        this.clearValue(controlName + ";ORDER_CODE;ACTIVE_FLG");
        // =========pangben modify 20110426 start
        this.setValue("REGION_CODE", Operator.getRegion());
        // =========pangben modify 20110426 stop
        // =========wangzl modify 20120810 start
        this.setValue("MAN_CODE", "");
        this.setValue("SUPPLIES_TYPE", "");
        // =========wangzl modify 20120810 stop
        this.setValue("USE_CAT", "");
    }

    /**
     * ��ղ���
     */
    public void onClear() {
        clearCtl();
        //upTable.removeRowAll();
    }
    /**
     * ��������
     */
    public void onExport() {
    	System.out.println("111111");
    	TTable table = (TTable) getComponent("upTable");
    	
    	TDataStore td = table.getDataStore();
    	String buff = td.isFilter()? td.FILTER : td.PRIMARY;
    	int rows  = td.rowCount();
    	TParm parm = new TParm();
    	int count = 0;
    	TParm rowParm = null;
    	Map<String,String> map = SYSUnitTool.getInstance().getUnitMap();
    	for(int i=0; i < rows; i++){
    		rowParm = td.getRowParm(i,buff);
    		rowParm.setData("UNIT_CODE",map.get(rowParm.getValue("UNIT_CODE")));
    		parm.setRowData(count, rowParm);
    		count++;
    	}
    	this.setValue("UNIT_CODE", "");
    	parm.setCount(count);
    	if (parm.getCount()<= 0) {
    		this.messageBox("û�л������");
    		return;
    	}
    	//System.out.println("������������������������������������������������������������"+parm);
    	ExportExcelUtil.getInstance().exportExcel(table.getHeader(),table.getParmMap(),parm, "��Ŀ�շ���Ϣ");

    }
    

    /**
     * �½�
     */
    public void onNew() {
        // ��ǰ���ݿ�ʱ��
        Timestamp date = TJDODBTool.getInstance().getDBTime();

        clearCtl();
        cerateNewDate();
        // Ĭ�ϸ���ǰʱ��
        START_DATE.setValue(date);
        END_DATE.setValue(date);
        // '�����ֶ��Ǽ�ע��'Ĭ��ѡ��
        LET_KEYIN_FLG.setSelected(true);
        // �ָ��༭״̬
        setEnabledForCtl(true);
    }

    /**
     * ����
     */
    public boolean onSave() {
        double newOwnPrice = TypeTool.getDouble(OWN_PRICE.getText());
        double newNhiPrice = TypeTool.getDouble(NHI_PRICE.getText());
        double newGovPrice = TypeTool.getDouble(GOV_PRICE.getText());
        String nowOrdCode = ORDER_CODE.getValue();
        if (onSaveCheck())
            return false;
        // �ж�ʱ��
        String stDate = START_DATE.getText();
        String endDate = END_DATE.getText();
        Timestamp start = (Timestamp) StringTool.getTimestamp(stDate,
                "yyyy/MM/dd HH:mm:ss");
        Timestamp end = (Timestamp) StringTool.getTimestamp(endDate,
                "yyyy/MM/dd HH:mm:ss");
        if (start.getTime() > end.getTime()) {
            this.messageBox("��Ч���ڲ����Դ���ʧЧ���ڣ�");
            return false;
        }

        // �õ���ǰѡ���е����ݣ�Ҫ���ĺ��½����У�
        selRow = upTable.getSelectedRow();
        // ȡtable����
        TDataStore dataStore = upTable.getDataStore();
        // ���õ�������λһ�У���Ĭ�ϱ�����Ǹ��С���0
        if (selRow == -1 && dataStore.rowCount() == 1)
            selRow = 0;
        // ����ڴ���û�иı������¸ı��������۸���һ��Ҫ����������һ��
        if (orderCode.equals(nowOrdCode)
                && ((newOwnPrice != oldOwnPrice)
                        || (newNhiPrice != oldNhiPrice) || (newGovPrice != oldGovPrice))) {
            //add by lx 
            //$$ add by lx 2012/01/06 ûѡ��Ҫ�޸ĵ�ҽ��
            if(selRow==-1){
                this.messageBox("��ѡ����Ҫ�޸ĵ�ҽ����");
                return false;
            }
            
            // ��Ҫ�ı����ڣ�true��
            setDataInTDS(dataStore, selRow, true);
            // ����һ���µ����ݣ��õ��к�
            int insertRowNumber = dataStore.insertRow();
            setDataInTDS(dataStore, insertRowNumber, true);
        } else {
            // �ӽ������õ����ݣ��ŵ�TDS�У����ڸ��±���
            setDataInTDS(dataStore, selRow, false);

        }
        // ���ӱ���SYS_FEE��TDS
        if (setDataInSysFeeTds())
            bigObject.addDS("SYS_FEE", sysFeeDS);

        bigObject.addDS("SYS_FEE_HISTORY", (TDS) dataStore);

        if (!bigObject.update()) {
            messageBox("E0001");
            return false;
        }       
         
        //add by lx 2013/01/06 ˢ��һ��ҽ�����
        //int selectRow = upTable.getSelectedRow();
        //System.out.println("-----selectRow----"+selectRow);
        initTblAndTDS(querySQL);
        //System.out.println("====onsave selRow===="+selRow);       
        //upTable.setSelectedRow(selectRow);        
        //
        
        messageBox("P0001");
        
        // ǰ̨ˢ��
        TIOM_Database.logTableAction("SYS_FEE");
        return true;

    }

    /**
     * �õ�SYS_FEE��TDS
     */
    private boolean setDataInSysFeeTds() {
        String sql = "";
        String orderCode = ORDER_CODE.getValue();
        // =============pangben modify 20110427 start ������������ѯ
        String regionCode = this.getValueString("REGION_CODE");
        String region = "";
        if (null != regionCode && !"".equals(regionCode))
            region = " AND (REGION_CODE='" + regionCode
                    + "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";

        sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE='" + orderCode + "'"
                + region;
        // System.out.println("--------------");
        // ============pangben modify 20110427 stop
        sysFeeDS.setSQL(sql);
        if (sysFeeDS.retrieve() <= 0)
            return false;

        // ��ǰ���ݿ�ʱ��
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        // ���������
        sysFeeDS.setItem(0, "OPT_USER", Operator.getID());
        sysFeeDS.setItem(0, "OPT_DATE", date);
        sysFeeDS.setItem(0, "OPT_TERM", Operator.getIP());
        sysFeeDS.setItem(0, "ORDER_CODE", ORDER_CODE.getValue());
        sysFeeDS.setItem(0, "ORDER_DESC", ORDER_DESC.getValue());
        sysFeeDS.setItem(0, "PY1", PY1.getValue());
        sysFeeDS.setItem(0, "NHI_FEE_DESC", NHI_FEE_DESC.getValue());
        sysFeeDS.setItem(0, "TRADE_ENG_DESC", TRADE_ENG_DESC.getValue());
        sysFeeDS.setItem(0, "ALIAS_DESC", ALIAS_DESC.getValue());
        sysFeeDS.setItem(0, "SPECIFICATION", SPECIFICATION.getValue());
        sysFeeDS.setItem(0, "DESCRIPTION", DESCRIPTION.getValue());
        sysFeeDS.setItem(0, "ALIAS_PYCODE", ALIAS_PYCODE.getValue());
        sysFeeDS.setItem(0, "HABITAT_TYPE", HABITAT_TYPE.getValue());
        sysFeeDS.setItem(0, "UNIT_CODE", UNIT_CODE.getValue());

        sysFeeDS.setItem(0, "MR_CODE", MR_CODE.getValue());
        sysFeeDS.setItem(0, "CHARGE_HOSP_CODE", CHARGE_HOSP_CODE.getValue());
        sysFeeDS.setItem(0, "ORDER_CAT1_CODE", ORDER_CAT1_CODE.getValue());
        sysFeeDS.setItem(0, "ORDER_DEPT_CODE", ORDER_DEPT_CODE.getValue());

        sysFeeDS
                .setItem(0, "HYGIENE_TRADE_CODE", HYGIENE_TRADE_CODE.getValue());
        sysFeeDS.setItem(0, "LET_KEYIN_FLG", LET_KEYIN_FLG.getValue());

        sysFeeDS.setItem(0, "OPD_FIT_FLG", OPD_FIT_FLG.getValue());
        sysFeeDS.setItem(0, "EMG_FIT_FLG", EMG_FIT_FLG.getValue());
        sysFeeDS.setItem(0, "IPD_FIT_FLG", IPD_FIT_FLG.getValue());
        sysFeeDS.setItem(0, "DR_ORDER_FLG", DR_ORDER_FLG.getValue());
        sysFeeDS.setItem(0, "HRM_FIT_FLG", HRM_FIT_FLG.getValue());
        sysFeeDS.setItem(0, "IN_OPFLG", IN_OPFLG.getValue());//add by wanglong 20131106
        sysFeeDS.setItem(0, "IO_CODE", IO_CODE.getValue());//add by wanglong 20140217
        sysFeeDS.setItem(0, "LCS_CLASS_CODE", LCS_CLASS_CODE.getValue());
        sysFeeDS.setItem(0, "TRANS_OUT_FLG", TRANS_OUT_FLG.getValue());
        sysFeeDS.setItem(0, "TRANS_HOSP_CODE", TRANS_HOSP_CODE.getValue());
        sysFeeDS.setItem(0, "EXEC_ORDER_FLG", EXEC_ORDER_FLG.getValue());
        sysFeeDS.setItem(0, "EXEC_DEPT_CODE", EXEC_DEPT_CODE.getValue());

        sysFeeDS.setItem(0, "INSPAY_TYPE", INSPAY_TYPE.getValue());
        sysFeeDS.setItem(0, "ADDPAY_RATE", ADDPAY_RATE.getValue());
        sysFeeDS.setItem(0, "ADDPAY_AMT", ADDPAY_AMT.getValue());
        sysFeeDS.setItem(0, "NHI_CODE_I", NHI_CODE_I.getValue());
        sysFeeDS.setItem(0, "NHI_CODE_O", NHI_CODE_O.getValue());
        sysFeeDS.setItem(0, "NHI_CODE_E", NHI_CODE_E.getValue());
        sysFeeDS.setItem(0, "ACTION_CODE", ACTION_CODE.getValue());
        sysFeeDS.setItem(0, "CAT1_TYPE", getCta1Type(""
                + ORDER_CAT1_CODE.getValue()));
        sysFeeDS.setItem(0, "ACTIVE_FLG", getValue("ACTIVE_FLG"));
        sysFeeDS.setItem(0, "IS_REMARK", getValue("IS_REMARK"));
        // ==========pangben modify 20110427 start
        sysFeeDS.setItem(0, "REGION_CODE", getValue("REGION_CODE"));
        // ==========pangben modify 20110427 start
        // ==========wangzl modify 20110427 start
        sysFeeDS.setItem(0, "MAN_CODE", getValue("MAN_CODE"));
        sysFeeDS.setItem(0, "SUPPLIES_TYPE", getValue("SUPPLIES_TYPE"));
        // ==========wangzl modify 20110427 start
        sysFeeDS.setItem(0, "ORD_SUPERVISION", ORD_SUPERVISION.getValue()); 
        sysFeeDS.setItem(0, "USE_CAT", getValue("USE_CAT"));
        
        return true;
    }

    /**
     * ȡ��ҽ��ϸ����
     * 
     * @param orderCat1Type
     *            String
     * @return String
     */
    private String getCta1Type(String orderCat1Type) {
        String SQL = "SELECT CAT1_TYPE FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE = '"
                + orderCat1Type + "'";
        TParm parm = new TParm(getDBTool().select(SQL));
        if (parm.getErrCode() != 0)
            return "";
        return parm.getValue("CAT1_TYPE", 0);
    }

    /**
     * ȡ�����ݿ������
     * 
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * �ռ������ϵ�ֵ����ʹ�ã����±��棩
     * 
     * @param dataStore
     *            TDataStore
     */
    public void setDataInTDS(TDataStore dataStore, int row, boolean dateFlg) {
        
        // ��ǰ���ݿ�ʱ��
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        // ���������
        dataStore.setItem(row, "OPT_USER", Operator.getID());
        dataStore.setItem(row, "OPT_DATE", date);
        dataStore.setItem(row, "OPT_TERM", Operator.getIP());

        // ����޸����ڱ��Ϊ�ٵ�ʱ���������ڰ���������ʾ�ı���
        String tempStartDate;
        String tempEndDate;
        if (!dateFlg) {
            tempStartDate = START_DATE.getText();
            String startDate = tempStartDate.substring(0, 4)
                    + tempStartDate.substring(5, 7)
                    + tempStartDate.substring(8, 10)
                    + tempStartDate.substring(11, 13)
                    + tempStartDate.substring(14, 16)
                    + tempStartDate.substring(17, 19);
            dataStore.setItem(row, "START_DATE", startDate);

            tempEndDate = END_DATE.getText();
            String endDate = tempEndDate.substring(0, 4)
                    + tempEndDate.substring(5, 7)
                    + tempEndDate.substring(8, 10)
                    + tempEndDate.substring(11, 13)
                    + tempEndDate.substring(14, 16)
                    + tempEndDate.substring(17, 19);
            dataStore.setItem(row, "END_DATE", endDate);

            // ִ�б�ǲ���
            dataStore.setItem(row, "ACTIVE_FLG", ACTIVE_FLG.getValue());
            dataStore.setItem(row, "OWN_PRICE", OWN_PRICE.getText());
            dataStore.setItem(row, "OWN_PRICE2", NHI_PRICE.getText());
            dataStore.setItem(row, "OWN_PRICE3", GOV_PRICE.getText());
            dataStore.setItem(row, "ACTION_CODE", ACTION_CODE.getValue());

        } else { // ���Ϊ��START_DATE
            //System.out.println("======row======"+row);
            //System.out.println("======selRow======"+selRow);
            
            Timestamp nowTime = TJDODBTool.getInstance().getDBTime();
            if (row == selRow) { // ���������кŵ��ڱ�ѡ�е��к�˵������Ҫ���µ�����
                //System.out.println("=====�޸��ϵļ�¼=====");
                tempEndDate = StringTool.getString(nowTime,
                        "yyyy/MM/dd HH:mm:ss");
                String endDate = tempEndDate.substring(0, 4)
                        + tempEndDate.substring(5, 7)
                        + tempEndDate.substring(8, 10)
                        + tempEndDate.substring(11, 13)
                        + tempEndDate.substring(14, 16)
                        + tempEndDate.substring(17, 19);
                dataStore.setItem(row, "END_DATE", endDate);

                // ִ�б�Ǳ�Ϊ��
                dataStore.setItem(row, "ACTIVE_FLG", 'N');
                // ���ϵ��������ݼ�¼�ϵļ�Ǯ�������Ķ����µ�ֵ��
                dataStore.setItem(row, "OWN_PRICE", oldOwnPrice);
                dataStore.setItem(row, "OWN_PRICE2", oldNhiPrice);
                dataStore.setItem(row, "OWN_PRICE3", oldGovPrice);
                dataStore.setItem(row, "ACTION_CODE", ACTION_CODE.getValue());
            } else {
                //System.out.println("=====�����ļ�¼=====");
                tempStartDate = StringTool.getString(nowTime,
                        "yyyy/MM/dd HH:mm:ss");
                String startDate = tempStartDate.substring(0, 4)
                        + tempStartDate.substring(5, 7)
                        + tempStartDate.substring(8, 10)
                        + tempStartDate.substring(11, 13)
                        + tempStartDate.substring(14, 16)
                        + tempStartDate.substring(17, 19);
                dataStore.setItem(row, "START_DATE", startDate);

                tempEndDate = END_DATE.getText();
                String endDate = tempEndDate.substring(0, 4)
                        + tempEndDate.substring(5, 7)
                        + tempEndDate.substring(8, 10)
                        + tempEndDate.substring(11, 13)
                        + tempEndDate.substring(14, 16)
                        + tempEndDate.substring(17, 19);
                //System.out.println("====�����ļ�¼ endDate===="+endDate);
                dataStore.setItem(row, "END_DATE", endDate);

                // ִ�б�Ǳ�Ϊ��
                dataStore.setItem(row, "ACTIVE_FLG", 'Y');
                dataStore.setItem(row, "OWN_PRICE", OWN_PRICE.getText());
                dataStore.setItem(row, "OWN_PRICE2", NHI_PRICE.getText());
                dataStore.setItem(row, "OWN_PRICE3", GOV_PRICE.getText());
                dataStore.setItem(row, "ACTION_CODE", ACTION_CODE.getValue());
            }
        }

        dataStore.setItem(row, "ORDER_CODE", ORDER_CODE.getValue());
        dataStore.setItem(row, "ORDER_DESC", ORDER_DESC.getValue());
        dataStore.setItem(row, "PY1", PY1.getValue());
        dataStore.setItem(row, "NHI_FEE_DESC", NHI_FEE_DESC.getValue());
        dataStore.setItem(row, "TRADE_ENG_DESC", TRADE_ENG_DESC.getValue());
        dataStore.setItem(row, "ALIAS_DESC", ALIAS_DESC.getValue());
        dataStore.setItem(row, "SPECIFICATION", SPECIFICATION.getValue());
        dataStore.setItem(row, "DESCRIPTION", DESCRIPTION.getValue());
        dataStore.setItem(row, "ALIAS_PYCODE", ALIAS_PYCODE.getValue());
        dataStore.setItem(row, "HABITAT_TYPE", HABITAT_TYPE.getValue());
        dataStore.setItem(row, "UNIT_CODE", UNIT_CODE.getValue());

        dataStore.setItem(row, "MR_CODE", MR_CODE.getValue());
        dataStore.setItem(row, "CHARGE_HOSP_CODE", CHARGE_HOSP_CODE.getValue());
        dataStore.setItem(row, "ORDER_CAT1_CODE", ORDER_CAT1_CODE.getValue());
        dataStore.setItem(row, "ORDER_DEPT_CODE", ORDER_DEPT_CODE.getValue());

        dataStore.setItem(row, "HYGIENE_TRADE_CODE", HYGIENE_TRADE_CODE
                .getValue());
        dataStore.setItem(row, "LET_KEYIN_FLG", LET_KEYIN_FLG.getValue());

        dataStore.setItem(row, "OPD_FIT_FLG", OPD_FIT_FLG.getValue());
        dataStore.setItem(row, "EMG_FIT_FLG", EMG_FIT_FLG.getValue());
        dataStore.setItem(row, "IPD_FIT_FLG", IPD_FIT_FLG.getValue());
        dataStore.setItem(row, "DR_ORDER_FLG", DR_ORDER_FLG.getValue());
        dataStore.setItem(row, "HRM_FIT_FLG", HRM_FIT_FLG.getValue());
        dataStore.setItem(row, "IN_OPFLG", IN_OPFLG.getValue());//add by wanglong 20131106
        dataStore.setItem(row, "IO_CODE", IO_CODE.getValue());//add by wanglong 20140217
        dataStore.setItem(row, "LCS_CLASS_CODE", LCS_CLASS_CODE.getValue());
        dataStore.setItem(row, "TRANS_OUT_FLG", TRANS_OUT_FLG.getValue());
        dataStore.setItem(row, "TRANS_HOSP_CODE", TRANS_HOSP_CODE.getValue());
        dataStore.setItem(row, "EXEC_ORDER_FLG", EXEC_ORDER_FLG.getValue());
        dataStore.setItem(row, "EXEC_DEPT_CODE", EXEC_DEPT_CODE.getValue());

        dataStore.setItem(row, "INSPAY_TYPE", INSPAY_TYPE.getValue());
        dataStore.setItem(row, "ADDPAY_RATE", ADDPAY_RATE.getValue());
        dataStore.setItem(row, "ADDPAY_AMT", ADDPAY_AMT.getValue());
        dataStore.setItem(row, "NHI_CODE_I", NHI_CODE_I.getValue());
        dataStore.setItem(row, "NHI_CODE_O", NHI_CODE_O.getValue());
        dataStore.setItem(row, "NHI_CODE_E", NHI_CODE_E.getValue());
        dataStore.setItem(row, "IS_REMARK", getValue("IS_REMARK"));
        dataStore.setItem(row, "CAT1_TYPE", getCta1Type(""
                + getValue("ORDER_CAT1_CODE")));
        // =============pangben modify 20110426 start
        dataStore.setItem(row, "REGION_CODE", getValue("REGION_CODE"));
        // =============pangben modify 20110426 stop
        // =============wangzl modify 20120810 start
        dataStore.setItem(row, "MAN_CODE", getValue("MAN_CODE"));
        dataStore.setItem(row, "SUPPLIES_TYPE", getValue("SUPPLIES_TYPE"));
        // =============wangzl modify 20120810 stop
        dataStore.setItem(row, "ORD_SUPERVISION", ORD_SUPERVISION.getValue());
        
        dataStore.setItem(row, "USE_CAT", getValue("USE_CAT"));

    }

    /**
     * ���½���ʱ���Զ����ɱ���ŵ�������
     */
    public void cerateNewDate() {
        String newCode = "";

        // �����ı�
        upTable.acceptText();
        // ȡtable����
        // �ҵ������Ҵ���(dataStore,����)
        // ========pangben modify 20110427 start
        // ע��ȥ������Ҫ��ѯ���е�����ţ�ͨ����ѯ���ݿ��еĵ���ֵ��ʾ�����
        // TDataStore dataStore = upTable.getDataStore();
        // String maxCode = getMaxCode(dataStore, "ORDER_CODE");
        // ========pangben modify 20110427 start

        // �ҵ�ѡ�е����ڵ�
        TTreeNode node = tree.getSelectionNode();
        // ���û��ѡ�еĽڵ�
        if (node == null)
            return;
        // �õ���ǰID
        String nowID = node.getID();
        int classify = 1;
        // �����������ĸ��ڵ����,�õ���ǰ�������
        if (nowID.length() > 0)
            classify = ruleTool.getNumberClass(nowID) + 1;
        // �������С�ڵ�,��������һ��
        if (classify > ruleTool.getClassifyCurrent()) {
            // �õ�Ĭ�ϵ��Զ���ӵ�ҽ������
            // ===pangben modify 20110427 start
            // ============���Ҵ˱�Ź����б�����ֵ
            String sql = "SELECT MAX(ORDER_CODE) AS ORDER_CODE FROM SYS_FEE_HISTORY WHERE ORDER_CODE LIKE '"
                    + nowID + "%'";
            // System.out.println("=========SQL========"+sql);
            TParm parm = new TParm(getDBTool().select(sql));
            String maxCode = parm.getValue("ORDER_CODE", 0);
            // ===pangben modify 20110427 start
            // $$=============Modified by lx 2012/08/06
            // �Զ���ҽ������Ϊ�մ�====================$$//
            String no = "";
            try {
                no = ruleTool.getNewCode(maxCode, classify);
                newCode = nowID + no;
            } catch (Exception e) {
                newCode = "";
            }
            // $$=============Modified by lx 2012/08/06
            // �Զ���ҽ������Ϊ�մ�====================$$//
            // �õ�����ӵ�table�����к�(�൱��TD�е�insertRow()����)
            int row = upTable.addRow();
            // ���õ�ǰѡ����Ϊ��ӵ���
            upTable.setSelectedRow(row);
            // �����Ҵ������Ĭ��ֵ
            upTable.setItem(row, "ORDER_CODE", newCode);
            // Ĭ�Ͽ�������
            upTable.setItem(row, "ORDER_DESC", "(�½�����)");
            // Ĭ�Ͽ��Ҽ��
            upTable.setItem(row, "SPECIFICATION", null);
            // Ĭ�Ͽ���
            upTable.setItem(row, "OWN_PRICE", null);
            // Ĭ����С����ע��
            upTable.setItem(row, "UNIT_CODE", null);
        }
        // ���Զ����ɵ�orderCode���õ�ORDER_CODE��
        ORDER_CODE.setText(newCode);
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
    public String getMaxCode(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return "";
        String s = "";
        if (dataStore.isFilter()) {
            TParm a = dataStore.getBuffer(TDataStore.FILTER);
            int count = a.getCount();
            for (int i = 0; i < count; i++) {
                String value = a.getValue(columnName, i);
                if (StringTool.compareTo(s, value) < 0)
                    s = value;
            }
        } else {
            int count = dataStore.rowCount();
            for (int i = 0; i < count; i++) {
                String value = dataStore.getItemString(i, columnName);
                if (StringTool.compareTo(s, value) < 0)
                    s = value;
            }
        }
        return s;
    }

    /**
     * ����table�ϵ�ĳһ�����ݸ�����Ŀؼ���ʼ��ֵ
     * 
     * @param date
     *            TParm
     */
    public void setValueForDownCtl(TParm date) {

        clearCtl();
        this.setValue("ACTIVE_FLG", date.getValue("ACTIVE_FLG"));
        Timestamp start = StringTool.getTimestamp(date.getValue("START_DATE"),
                "yyyyMMddHHmmss");
        this.setValue("START_DATE", start);
        Timestamp end = StringTool.getTimestamp(date.getValue("END_DATE"),
                "yyyyMMddHHmmss");
        this.setValue("END_DATE", end);

        this.setValue("ORDER_CODE", date.getValue("ORDER_CODE"));
        this.setValue("ORDER_DESC", date.getValue("ORDER_DESC"));
        this.setValue("PY1", date.getValue("PY1"));
        this.setValue("NHI_FEE_DESC", date.getValue("NHI_FEE_DESC"));
        this.setValue("TRADE_ENG_DESC", date.getValue("TRADE_ENG_DESC"));
        this.setValue("ALIAS_DESC", date.getValue("ALIAS_DESC"));
        this.setValue("SPECIFICATION", date.getValue("SPECIFICATION"));
        this.setValue("DESCRIPTION", date.getValue("DESCRIPTION"));
        this.setValue("HABITAT_TYPE", date.getValue("HABITAT_TYPE"));
        this.setValue("MAN_CODE", date.getValue("MAN_CODE"));
        this.setValue("ALIAS_PYCODE", date.getValue("ALIAS_PYCODE"));
        this.setValue("UNIT_CODE", date.getValue("UNIT_CODE"));

        this.setValue("MR_CODE", date.getValue("MR_CODE"));
        this.setValue("CHARGE_HOSP_CODE", date.getValue("CHARGE_HOSP_CODE"));
        this.setValue("ORDER_CAT1_CODE", date.getValue("ORDER_CAT1_CODE"));
        this.setValue("ORDER_DEPT_CODE", date.getValue("ORDER_DEPT_CODE"));
        this.setValue("OWN_PRICE", date.getValue("OWN_PRICE"));
        this.setValue("OWN_PRICE2", date.getValue("OWN_PRICE2"));
        this.setValue("OWN_PRICE3", date.getValue("OWN_PRICE3"));
        this
                .setValue("HYGIENE_TRADE_CODE", date
                        .getValue("HYGIENE_TRADE_CODE"));
        this.setValue("LET_KEYIN_FLG", date.getValue("LET_KEYIN_FLG"));

        this.setValue("OPD_FIT_FLG", date.getValue("OPD_FIT_FLG"));
        this.setValue("EMG_FIT_FLG", date.getValue("EMG_FIT_FLG"));
        this.setValue("IPD_FIT_FLG", date.getValue("IPD_FIT_FLG"));
        this.setValue("HRM_FIT_FLG", date.getValue("HRM_FIT_FLG"));
        this.setValue("DR_ORDER_FLG", date.getValue("DR_ORDER_FLG"));
        this.setValue("IN_OPFLG", date.getValue("IN_OPFLG"));//add by wanglong 20131106
        this.setValue("IO_CODE", date.getValue("IO_CODE"));//add by wanglong 20140217
        this.setValue("TRANS_OUT_FLG", date.getValue("TRANS_OUT_FLG"));
        this.setValue("EXEC_ORDER_FLG", date.getValue("EXEC_ORDER_FLG"));
        this.setValue("LCS_CLASS_CODE", date.getValue("LCS_CLASS_CODE"));
        this.setValue("TRANS_HOSP_CODE", date.getValue("TRANS_HOSP_CODE"));
        this.setValue("EXEC_DEPT_CODE", date.getValue("EXEC_DEPT_CODE"));

        this.setValue("INSPAY_TYPE", date.getValue("INSPAY_TYPE"));
        this.setValue("ADDPAY_RATE", date.getValue("ADDPAY_RATE"));
        this.setValue("ADDPAY_AMT", date.getValue("ADDPAY_AMT"));
        this.setValue("NHI_CODE_I", date.getValue("NHI_CODE_I"));
        this.setValue("NHI_CODE_O", date.getValue("NHI_CODE_O"));
        this.setValue("NHI_CODE_E", date.getValue("NHI_CODE_E"));
        this.setValue("IS_REMARK", date.getValue("IS_REMARK"));
        // ===========wangzl modify 20120810 start
        String manCode = date.getValue("MAN_CODE");// ��������
        if ("".equals(manCode) || manCode.equals("0")) {
            this.setValue("MAN_CODE", "");
        } else {
            this.setValue("MAN_CODE", date.getValue("MAN_CODE"));
        }
        this.setValue("SUPPLIES_TYPE", date.getValue("SUPPLIES_TYPE"));
        // ===========wangzl modify 20120810 stop
        ACTION_CODE.setValue(date.getValue("ACTION_CODE"));
        // ===========pangben modify 20110427 start
        this.setValue("REGION_CODE", date.getValue("REGION_CODE"));
        this.setValue("ORD_SUPERVISION",date.getValue("ORD_SUPERVISION"));
        
        this.setValue("USE_CAT", date.getValue("USE_CAT"));
    }

    /**
     * ѡ��TRANS_OUT_FLG���
     */
    public void onOutHosp() {
        String value = TRANS_OUT_FLG.getValue();
        TRANS_HOSP_CODE.setEnabled(TypeTool.getBoolean(value));
        if (!TypeTool.getBoolean(value)) {
            TRANS_HOSP_CODE.setText("");
        }
    }

    /**
     * ѡ�񿪵���ִ�б��
     */
    public void onExrOrd() {
        String value = EXEC_ORDER_FLG.getValue();
        // ��������ʱ��
        EXEC_DEPT_CODE.setEnabled(TypeTool.getBoolean(value));
        // ���ֵ
        if (!TypeTool.getBoolean(value)) {
            EXEC_DEPT_CODE.setText("");
        }

    }

    /**
     * �����ԷѼ��¼�
     */
    public void onOwnPrice() {
        // �õ���ǰ�ԷѼ۸�
        double ownPrice = TypeTool.getDouble(OWN_PRICE.getText());
        double nhiPrice = TypeTool.getDouble(NHI_PRICE.getText());
        double govPrice = TypeTool.getDouble(GOV_PRICE.getText());

        // Ĭ��ҽ��/������߼۵����ԷѼ۸�
        // if (nhiPrice > ownPrice || nhiPrice == 0)
        NHI_PRICE.setValue(ownPrice * 2);
        // if (govPrice < ownPrice || govPrice == 0)
        GOV_PRICE.setValue(ownPrice * 2.5);
        // �ƶ����
        NHI_PRICE.grabFocus();
    }

    /**
     * ����ҽ�����¼�
     */
    public void onNhiPrice() {/*
                             * //�õ���ǰҽ���۸� double nhiPrice =
                             * TypeTool.getDouble(NHI_PRICE.getText()); double
                             * ownPrice =
                             * TypeTool.getDouble(OWN_PRICE.getText()); double
                             * govPrice =
                             * TypeTool.getDouble(GOV_PRICE.getText());
                             * 
                             * //���ҽ���۸�����ԷѼ۸� // if (ownPrice < nhiPrice ||
                             * nhiPrice == 0) OWN_PRICE.setValue(nhiPrice*0.5);
                             * //�����������۸�С��ҽ���۸� // if (govPrice < nhiPrice ||
                             * govPrice == 0) GOV_PRICE.setValue(nhiPrice*1.25);
                             * //�ƶ���� GOV_PRICE.grabFocus();
                             */
    }

    /**
     * ����������߼��¼�
     */
    public void onGovPrice() {/*
                             * //�õ���ǰ�ԷѼ۸� double govPrice =
                             * TypeTool.getDouble(GOV_PRICE.getText()); double
                             * ownPrice =
                             * TypeTool.getDouble(OWN_PRICE.getText()); double
                             * nhiPrice =
                             * TypeTool.getDouble(NHI_PRICE.getText());
                             * 
                             * //����ԷѼ۸����������߼۸� // if (ownPrice > govPrice ||
                             * ownPrice == 0) OWN_PRICE.setValue(govPrice*0.4);
                             * //���ҽ���۸����������߼۸� // if (nhiPrice > govPrice ||
                             * govPrice == 0) NHI_PRICE.setValue(govPrice*0.8);
                             * //�ƶ���� HYGIENE_TRADE_CODE.grabFocus();
                             */
    }

    /**
     * �õ�����ƴ��
     */
    public void onPY1() {
        String orderDesc = ORDER_DESC.getText();
        String orderPy = SYSHzpyTool.getInstance().charToCode(orderDesc);
        PY1.setText(orderPy);
        NHI_FEE_DESC.grabFocus();
    }

    /**
     * �õ�����ƴ��
     */
    public void onPY2() {
        String aliasDesc = ALIAS_DESC.getText();
        String aliasPy = SYSHzpyTool.getInstance().charToCode(aliasDesc);
        ALIAS_PYCODE.setText(aliasPy);
        SPECIFICATION.grabFocus();
    }

    /**
     * ��ʷ��ϸ��ť
     * 
     * @param args
     *            String[]
     */
    public void onHistory() {

        String orderCode = ORDER_CODE.getText();
        if ("".equals(orderCode)) {
            this.messageBox("��ѡ��Ҫ�鿴����Ŀ��");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        // ==========pangben modify 20110427 start ����������
        parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        // ==========pangben modify 20110427 stop
        // ��ģ̬����ʽ�򿪴��ڣ� ·�� ����
        TParm returnData = (TParm) this.openDialog(
                "%ROOT%\\config\\sys\\SYS_FEE\\SYSFEE_HISTORY.x", parm);

        if (returnData != null) {
            setValueForDownCtl(returnData);
            // ����ʷ�е��������ݲ������޸�
            setEnabledForCtl(false);
        }
    }

    /**
     * �������еĿؼ��Ƿ���ã���ʷ���ݲ������޸ģ�
     * 
     * @param flg
     *            boolean
     */
    public void setEnabledForCtl(boolean flg) {
        String tag[] = controlName.split(";");
        int count = tag.length;
        for (int i = 0; i < count; i++) {
            this.callFunction("UI|" + tag[i] + "|setEnabled", flg);
        }
        ACTIVE_FLG.setEnabled(true);
    }

    public void onExe() {
        Timestamp time = TJDODBTool.getInstance().getDBTime();
        // �����ʼʱ��Ϊ�գ���Ĭ�ϸ���ǰʱ��
        if ("".equals(TypeTool.getString(START_DATE.getValue()))) {
            START_DATE.setValue(time);
        }
        // �������õ�ʱ��Ĭ�ϡ���ʧЧ����дΪ"9999/12/31 23:59:59"
        if (TypeTool.getBoolean(ACTIVE_FLG.getValue())) {
            END_DATE.setText("9999/12/31 23:59:59");
            START_DATE.setValue(time);  //modify chenxi ����ʱ���¿�ʼʱ��

        } else {
            String date = StringTool.getString(time, "yyyy/MM/dd HH:mm:ss");  
            END_DATE.setText(date);
        }

        // �ָ��༭״̬
        setEnabledForCtl(true);

    }

    /**
     * table˫��ѡ����
     * 
     * @param row
     *            int
     */
    public void onTableDoubleCleck() {

        upTable.acceptText();
        int row = upTable.getSelectedRow();
        String value = upTable.getItemString(row, 0);
        // �õ��ϲ����
        String partentID = ruleTool.getNumberParent(value);
        TTreeNode node = treeRoot.findNodeForID(partentID);
        if (node == null)
            return;
        // �õ������ϵ�������
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        // ��������ѡ�нڵ�
        tree.setSelectNode(node);
        tree.update();
        // ���ò�ѯ�¼�
        // ====pangben modify 20110427 ����������
        onCleckClassifyNode(partentID, upTable, Operator.getRegion());
        // ====pangben modify 20110427 stop
        // table������ѡ����
        int count = upTable.getRowCount(); // table������
        for (int i = 0; i < count; i++) {
            // �õ����ʴ���
            String invCode = upTable.getItemString(i, 0);
            if (value.equals(invCode)) {
                // ����ѡ����
                upTable.setSelectedRow(i);
                return;
            }
        }
    }

    /**
     * ѡ�ж�Ӧ���ڵ���¼�
     * 
     * @param parentID
     *            String
     * @param table
     *            TTable
     */
    public void onCleckClassifyNode(String parentID, TTable table,
            String regionCode) {
        // ==========pangben modify 20110427 start ������������ѯ
        String region = "";
        if (null != regionCode && !"".equals(regionCode))
            region = " AND (REGION_CODE='" + regionCode
                    + "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
        // table�е�datastore�в�ѯ����sql
        table.setSQL("select * from SYS_FEE_HISTORY where ORDER_CODE like '"
                + parentID + "%'" + region);
        // ��������
        table.retrieve();
        // �������ݵ�table��
        table.setDSValue();
        // ����������ť
        callFunction("UI|new|setEnabled", true);

    }

    /**
     * ����
     * 
     * @param ob
     *            Object
     */
    public void onFilter(Object ob) {
        if ("ALL".equals(ob.toString())) {
            upTable.setFilter("");
            upTable.filter();
            // �������ݵ�table��
            upTable.setDSValue();
            // ����������ť
            callFunction("UI|new|setEnabled", true);
        } else if ("Y".equals(ob.toString())) {
            upTable.setFilter("ACTIVE_FLG='Y'");
            upTable.filter();
            // �������ݵ�table��
            upTable.setDSValue();
            // ����������ť
            callFunction("UI|new|setEnabled", true);
        } else {
            upTable.setFilter("ACTIVE_FLG='N'");
            upTable.filter();
            // �������ݵ�table��
            upTable.setDSValue();
            // ����������ť
            callFunction("UI|new|setEnabled", false);
        }
    }

    public boolean onSaveCheck() {
        if (getValueString("START_DATE").length() == 0) {
            messageBox("��Ч���ڲ���Ϊ��");
            return true;
        }
        if (getValueString("END_DATE").length() == 0) {
            messageBox("ʧЧ���ڲ���Ϊ��");
            return true;
        }
        if (getValueString("ORDER_CODE").length() == 0) {
            messageBox("��Ŀ���벻��Ϊ��");
            return true;
        }
        if (getValueString("ORDER_DESC").length() == 0) {
            messageBox("��Ŀ���Ʋ���Ϊ��");
            return true;
        }
        if (getValueString("PY1").length() == 0) {
            messageBox("����ƴ������Ϊ��");
            return true;
        }
        if (getValueString("UNIT_CODE").length() == 0) {
            messageBox("��λ����Ϊ��");
            return true;
        }
        if (getValueString("CHARGE_HOSP_CODE").length() == 0) {
            messageBox("Ժ�ڴ��벻��Ϊ��");
            return true;
        }
        if (getValueString("ORDER_CAT1_CODE").length() == 0) {
            messageBox("ϸ���಻��Ϊ��");
            return true;
        }
        if (getValueString("OWN_PRICE").length() == 0) {
            messageBox("�ԷѼ۲���Ϊ��");
            return true;
        }
        if (getValueString("OWN_PRICE2").length() == 0) {
            messageBox("����۲���Ϊ��");
            return true;
        }
        if (getValueString("OWN_PRICE3").length() == 0) {
            messageBox("����ҽ�Ƽ۲���Ϊ��");
            return true;
        }
        // if(getValueString("EXEC_DEPT_CODE").length() == 0){
        // messageBox("�����Ų���Ϊ��");
        // return true;
        // }
        return false;
    }
    
    /**
     * ���ֵ��еļ�Ǯ�����ײ��е���Ŀ�ļ�Ǯ
     * add by huangtt 20150626
     */
    public void onUpdate(){
    	String orderCode = this.getValueString("ORDER_CODE");
    	double ownPrice = this.getValueDouble("OWN_PRICE");
    	if(orderCode.length() == 0){
    		return;
    	}

    	String sql = "SELECT ID,SECTION_CODE, PACKAGE_CODE, ORDER_CODE,ORDER_NUM," +
		" UNIT_PRICE, SETMAIN_FLG, HIDE_FLG, ORDERSET_CODE, ORDERSET_GROUP_NO," +
		  ownPrice +"-UNIT_PRICE DIFF_PRICE,OPT_USER,OPT_TERM,VERSION_NUMBER, " +
		" '' DIFF_PRICE_SUM  FROM MEM_PACKAGE_SECTION_D" +
		" WHERE SETMAIN_FLG='N' AND " +
		" ORDER_CODE = '"+orderCode+"' AND UNIT_PRICE <> "+ownPrice +
    	" ORDER BY PACKAGE_CODE,SECTION_CODE";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getCount() > 0){
    		for (int i = 0; i < parm.getCount(); i++) {
    			
    			parm.setData("VERSION_NUMBER", i, OrderTool.getInstance().getSystemTime());
   			 	parm.setData("OPT_USER", i, Operator.getID());
   			 	parm.setData("OPT_TERM", i, Operator.getIP());
   			 	parm.setData("DIFF_PRICE_SUM", i, parm.getDouble("DIFF_PRICE", i)*parm.getDouble("ORDER_NUM", i));
			}
    		
    		TParm result = TIOM_AppServer.executeAction("action.mem.MEMPackageSectionAction",
    				"onUpdatePackageOrderPrice", parm);
        	if(result.getErrCode()<0){
    			this.messageBox("����ʧ�ܣ�");
    			
    		}else{
    			this.messageBox("���³ɹ���");
    		}
    	}else{
    		this.messageBox("û��Ҫ���ĵ�����");
    	}
    	
    	
    	
    	
    }

    // ��������
    public static void main(String[] args) {
        JavaHisDebug.initClient();
        // JavaHisDebug.TBuilder();

        // JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("sys\\SYS_FEE\\SYSFEE_FEE.x");
    }

}
