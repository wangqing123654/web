package com.javahis.ui.sys;

import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.javahis.manager.sysfee.sysOdrsetDtlObserver;
import java.awt.Component;
import com.dongyang.jdo.TDSObject;
import com.dongyang.ui.TTreeNode;
import jdo.sys.SYSRuleTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TDS;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TTableNode;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import jdo.sys.SYSHzpyTool;

import com.javahis.util.ExportExcelUtil;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TTextFormat;
import java.util.Vector;
import com.dongyang.manager.TIOM_Database;
import com.javahis.util.StringUtil;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: ����ҽ���ֵ䵵����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS 1.0 (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH
 */
public class SYSFee_OrdPackControl
    extends TControl {

    public SYSFee_OrdPackControl() {
    }

    /**
     * �����
     */
    private TDSObject bigObject = new TDSObject();
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
     * ���ǰѡ�е��к�
     */
    int selRow = -1;
    /**
     * ��ǰѡ�е���Ŀ����(SYS_FEE_HISTORY�У�ORDER_CODE/SYS_ORDERSETDETAI�У�ORDERSET_CODE)
     */
    String orderCode = "";
    /**
     * SYS_FEE_HISTORY���TDS
     */
    TDS sysFeeHisDS = new TDS();
    /**
     * SYS_ORDERSETDETAI���TDS
     */
    TDS sysOrdSetDtlDS = new TDS();
	/**
	 * SYS_FEE���TDS
	 */
	TDS sysFeeDS = new TDS();

    /**
     * ���пؼ�������
     */
    private String controlName =
        "USEDEPT_CODE;EXEC_DEPT_CODE;" +
        "ACTIVE_FLG;CHARGE_HOSP_CODE;ORDER_DESC;PY1;" +
        "DESCRIPTION;ORDER_CAT1_CODE;TRANS_OUT_FLG;" +
        "OPD_FIT_FLG;EMG_FIT_FLG;IPD_FIT_FLG;HRM_FIT_FLG;UNIT;IS_REMARK;ORD_SUPERVISION";


    /**
     * ����Ŀؼ�
     */
    //��
    TTree tree;
    //����
    TTable upTable, downTable;

    //ȫ��/����/ͣ�ñ��
    TRadioButton ALL;
    TRadioButton ACTIVE_Y;
    TRadioButton ACTIVE_N;


    //��,��,ס,��,��ҽ���ñ��
    TCheckBox OPD_FIT_FLG;
    TCheckBox EMG_FIT_FLG;
    TCheckBox IPD_FIT_FLG;
    TCheckBox HRM_FIT_FLG;

    TTextFormat USEDEPT_CODE;
    TTextFormat EXEC_DEPT_CODE;
    TTextFormat CHARGE_HOSP_CODE;
    TTextFormat ORDER_CAT1_CODE;
    TTextFormat ORD_SUPERVISION;

    //����ע��
    TCheckBox ACTIVE_FLG;

    TTextField QUERY;
    TTextField ORDER_CODE;
    TTextField ORDER_DESC;
    TTextField PY1;
    TTextField DESCRIPTION;

    //ת��ע��
    TCheckBox TRANS_OUT_FLG;
    TComboBox TRANS_HOSP_CODE;

    TNumberTextField TOT_FEE;


    //����ҽ��ע��(�ڽ���������)
    TCheckBox ORDERSET_FLG;
    //����ʱ��(��ʱ��Ч--��ǰʱ��)
    String START_DATE;
    //ͣ��ʱ��(99991231235959)
    String END_DATE;

    //--------------------
    TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");
    //����order�õ�����
    public double getPrice(String code) {
        if (dataStore == null)
            return 0.0;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER :
            dataStore.PRIMARY;
        TParm parm = dataStore.getBuffer(bufferString);
        Vector vKey = (Vector) parm.getData("ORDER_CODE");
        Vector vPrice = (Vector) parm.getData("OWN_PRICE");
        int count = vKey.size();
        for (int i = 0; i < count; i++) {
            if (code.equals(vKey.get(i)))
                return TypeTool.getDouble(vPrice.get(i));
        }
        return 0.0;
    }

//--------------------



    public void onInit() { //��ʼ������
        super.onInit();
        myInitControler();

        //��ʼ����
        onInitTree();
        //��tree��Ӽ����¼�
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        //��ʼ�����
        onInitNode();
        callFunction("UI|downTable|addEventListener",
                            TTableEvent.CHECK_BOX_CLICKED, this,"onTableCheckBoxClicked");
        canSave();
        //========pangben modify 20110427 start Ȩ�����
        //��ʼ��Ժ��
        setValue("REGION_CODE", Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110427 stop

    }


    private void canSave(){
        Object obj = this.getParameter();
        if(obj==null)
            return;
        if(obj.equals("QUERY"))
            callFunction("UI|save|setEnabled", false);
    }

       /**
        * ���(TABLE)��ѡ��ı��¼�
        *
        * @param obj
        */
       public void onTableCheckBoxClicked(Object obj) {
           TTable table = (TTable)obj;
           //=========pangben 2013-2-19 ��ӿ��в���ɾ��У��
           if(table.getShowParmValue().getValue("ORDER_DESC",table.getSelectedRow()).length()<=0){
        	   this.messageBox("���в���ɾ��");
        	   table.setItem(table.getSelectedRow(),"N_DEL","N");
        	   table.acceptText();
        	   return;
           }
           table.removeRow(table.getSelectedRow());
       }

    /**
     * ��ʼ����
     */
    public void onInitTree() {
        //�õ�����
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        if (treeRoot == null)
            return;
        //�����ڵ����������ʾ
        treeRoot.setText("ҽ���ײͷ���");
        //�����ڵ㸳tag
        treeRoot.setType("Root");
        //���ø��ڵ��id
        treeRoot.setID("");
        //������нڵ������
        treeRoot.removeAllChildren();
        //���������ʼ������
        callMessage("UI|TREE|update");
    }

    /**
     * ��ʼ�����Ľ��
     */

    public void onInitNode() {
        //��dataStore��ֵ
        treeDataStore.setSQL(
            "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='ORDPACK_RULE'");
        //�����dataStore���õ�������С��0
        if (treeDataStore.retrieve() <= 0)
            return;
        //��������,�Ǳ�������еĿ�������
        ruleTool = new SYSRuleTool("ORDPACK_RULE");
        if (ruleTool.isLoad()) { //�����۽ڵ����:datastore���ڵ����,�ڵ���ʾ����,,�ڵ�����
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                "CATEGORY_CODE",
                "CATEGORY_CHN_DESC", "Path", "SEQ");
            //ѭ����������ڵ�
            for (int i = 0; i < node.length; i++)
                treeRoot.addSeq(node[i]);
        }
        //�õ������ϵ�������
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        //������
        tree.update();
        //��������Ĭ��ѡ�нڵ�
        tree.setSelectNode(treeRoot);
    }

    /**
     * ������
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
        //���
        onClear();

        //������ť������
        callFunction("UI|new|setEnabled", false);
        //�õ�������Ľڵ����
        // TTreeNode node = (TTreeNode) parm;
        TTreeNode node = tree.getSelectNode();
        //TTreeNode node=tree.getSelectionNode();
        if (node == null)
            return;
        //�õ�table����
        TTable table = (TTable)this.callFunction("UI|upTable|getThis");
        //table�������иı�ֵ
        table.acceptText();
        //�жϵ�����Ƿ������ĸ����
        if (node.getType().equals("Root")) {
            //��������ĸ��ӵ�table�ϲ���ʾ����
            table.removeRowAll();
        }
        else { //�����Ĳ��Ǹ����
            //�õ���ǰѡ�еĽڵ��idֵ
            String id = node.getID();
            //�õ���ѯTDS��SQL��䣨ͨ����һ���IDȥlike���е�orderCode��
            //===pangben modify 20110427 ����������
            String sql = getSQL(id,Operator.getRegion());
            //��ʼ��table��TDS
            initTblAndTDS(sql);

        }
        //��table���ݼ���������
//        table.setSort("ORDER_CODE");
        //table��������¸�ֵ
//        table.sort();
        //�õ���ǰ�������ID
        String nowID = node.getID();

        int classify = 1;
        if (nowID.length() > 0)
            classify = ruleTool.getNumberClass(nowID) + 1;
        //�������С�ڵ�,��������һ��(ʹ������ť����)
        if (classify > ruleTool.getClassifyCurrent()) {
            this.callFunction("UI|new|setEnabled", true);
        }
        //�����TABLE��������ܷ��á�
        updateMastOwnPrice();
    }

    /**
 * ���������ʱ�����ϸ����ʵ�۸���µ�����
 */
private void updateMastOwnPrice() {
    String ordersetCode = "";
    int tblCount = upTable.getRowCount();
    TDataStore ds = upTable.getDataStore();
    //ѭ��ÿһ��
    for (int j = 0; j < tblCount; j++) {
        ordersetCode = (String) ds.getItemData(j, "ORDER_CODE");
        //�õ�������Ķ�Ӧϸ���б�
        String selordCodeForDtl =
            "SELECT ORDER_CODE,DOSAGE_QTY FROM SYS_ORDERSETDETAIL WHERE ORDERSET_CODE = '" +
            ordersetCode + "'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            selordCodeForDtl));
        int count = parm.getCount();
        double totFee = 0.0;
        for (int i = 0; i < count; i++) {
            String code = parm.getValue("ORDER_CODE", i);
            double qty = TypeTool.getDouble(parm.getValue("DOSAGE_QTY", i));
            totFee += getPrice(code) * qty;
        }
        upTable.setValueAt(totFee, j, 3);
    }

}


    /**
     * �õ���ʼ��TDS��SQL���(����Ŀǰ�������õ���Ŀ�б�����START_DATE/END_DATE ֻ��ACTIVE_FLGΪ��Y��)
     * @return String
     */
    private String getSQL(String orderCode,String regionCode) {
        String active = "";
        if (ACTIVE_Y.isSelected()) { //����
            active = " AND ACTIVE_FLG='Y'";
            setEnabledForCtl(true);
        }
        else if (ACTIVE_N.isSelected()) { //ͣ��
            active = " AND ACTIVE_FLG='N'";
            setEnabledForCtl(false);
        }
        else { //ȫ��
            setEnabledForCtl(false);
        }
        //======pangben modify 20110426 start
        String region = "";
        if (null != regionCode && !"".equals(regionCode))
            region = " AND (REGION_CODE='" + regionCode + "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
        //======pangben modify 20110426 stop

        String sql = "";
        //���������
        if (orderCode != null && orderCode.length() > 0)
        	sql = " SELECT  * "
				+ " FROM SYS_FEE_HISTORY"
				+ " WHERE "
				+ " ORDER_CODE like '"
				+ orderCode
				+ "%'"
				+ active
				+ region
				+ " ORDER BY ORDER_CODE";

        return sql;
    }

    /**
     * ��ʼ������ı�������еĿؼ�����
     * @param sql String
     */
    public void initTblAndTDS(String sql) {
        sysFeeHisDS.setSQL(sql);
        sysFeeHisDS.retrieve();
        //���û��������ձ���ϵ�����
        if (sysFeeHisDS.rowCount() <= 0) {
            upTable.removeRowAll();
        }
        sysFeeHisDS.showDebug();

        upTable.setDataStore(sysFeeHisDS);
        upTable.setDSValue();

    }

    /**
     * ���ȵõ�����UI�Ŀؼ�����/ע����Ӧ���¼�
     * ����
     */
    public void myInitControler() {

        tree = (TTree) callFunction("UI|TREE|getThis");

        //�õ�table�ؼ�
        upTable = (TTable)this.getComponent("upTable");
        downTable = (TTable)this.getComponent("downTable");

        ALL = (TRadioButton)this.getComponent("ALL");
        ACTIVE_Y = (TRadioButton)this.getComponent("ACTIVE_Y");
        ACTIVE_N = (TRadioButton)this.getComponent("ACTIVE_N");

        ACTIVE_FLG = (TCheckBox)this.getComponent("ACTIVE_FLG");
        QUERY = (TTextField)this.getComponent("QUERY");
        ORDER_CODE = (TTextField)this.getComponent("ORDER_CODE");
        ORDER_DESC = (TTextField)this.getComponent("ORDER_DESC");
        PY1 = (TTextField)this.getComponent("PY1");
        DESCRIPTION = (TTextField)this.getComponent("DESCRIPTION");
        TRANS_OUT_FLG = (TCheckBox)this.getComponent("TRANS_OUT_FLG");
        TRANS_HOSP_CODE = (TComboBox)this.getComponent("TRANS_HOSP_CODE");

        USEDEPT_CODE = (TTextFormat)this.getComponent("USEDEPT_CODE");
        EXEC_DEPT_CODE = (TTextFormat)this.getComponent("EXEC_DEPT_CODE");
        CHARGE_HOSP_CODE = (TTextFormat)this.getComponent("CHARGE_HOSP_CODE");
        ORDER_CAT1_CODE = (TTextFormat)this.getComponent("ORDER_CAT1_CODE");
        ORD_SUPERVISION=(TTextFormat)this.getComponent("ORD_SUPERVISION");
        
        //�ܷ���
        TOT_FEE = (TNumberTextField)this.getComponent("TOT_FEE");

        OPD_FIT_FLG = (TCheckBox)this.getComponent("OPD_FIT_FLG");
        EMG_FIT_FLG = (TCheckBox)this.getComponent("EMG_FIT_FLG");
        IPD_FIT_FLG = (TCheckBox)this.getComponent("IPD_FIT_FLG");
        HRM_FIT_FLG = (TCheckBox)this.getComponent("HRM_FIT_FLG");

        //����tableע�ᵥ���¼�����
        this.callFunction("UI|upTable|addEventListener",
                          "upTable->" + TTableEvent.CLICKED, this,
                          "onUpTableClicked");
        //����tableע������¼�
        downTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                   "onCreateSYSFEE");

        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "");
        // ���õ����˵�
        QUERY.setPopupMenuParameter("UD", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // ������ܷ���ֵ����
        QUERY.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

        //������ť������
        callFunction("UI|new|setEnabled", false);
    }

    /**
     * ���ܷ���ֵ����
     * @param tag String
     * @param obj Object
     */
    public void popReturnFEE(String tag, Object obj) {
        //�ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
        if (obj == null && ! (obj instanceof TParm)) {
            return;
        }
        //����ת����TParm
        TParm result = (TParm) obj;
        String ordCode = result.getValue("ORDER_CODE");
        //���ݷ��ص�CODE����table��TDS��ֵ
        setDownTableAndTDS(ordCode);
    }
    /**
     * ���ܷ���ֵ����
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
        //��ղ�ѯ�ؼ�
        QUERY.setValue("");
        onQuery();
    }

    public void onQuery() {

        String selCode = ORDER_CODE.getValue();
        if ("".equals(selCode)) {
            this.messageBox("������Ҫ��ѯ��Ŀ�ı��룡");
        }

        //�õ���ѯcode��SQL���
        //=====pangben modify 20110427 start
        String sql = getSQL(selCode,this.getValueString("REGION_CODE"));
        //=====pangben modify 20110427 stop
        //��ʼ��table��TDS
        initTblAndTDS(sql);
        //����ѯ���ֻ��һ�����ݵ�ʱ��ֱ����ʾ����ϸ��Ϣ
        if (upTable.getRowCount() == 1) {
            onUpTableClicked(0);
        }

    }


    /**
     * ��TABLE�����༭�ؼ�ʱ
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateSYSFEE(Component com, int row, int column) {

        if (column != 1)
            return;
        if (! (com instanceof TTextField))
            return;
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        //��ѯ������ϸ��--6
        TParm parm = new TParm();
        //parm.setData("RX_TYPE", 6);
        textFilter.setPopupMenuParameter("IG",
                                         getConfigParm().newConfig(
                                             "%ROOT%\\config\\sys\\SYSFeePopup.x"),
                                         parm);
        //������ܷ���ֵ����
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturnFEE");
    }


    public void setDownTableAndTDS(String code) {

        downTable.acceptText();
        int selrow = downTable.getSelectedRow();
        downTable.setItem(selrow, "ORDER_CODE", code);
        //������һ����Ŀ֮��Ĭ��Ϊ1.0
        if (TypeTool.getDouble(downTable.getValueAt_(selrow, 5)) == 0.0) {
            downTable.setItem(selrow, "DOSAGE_QTY", 1.0);
        }

        //����һ��
        //sysOrdSetDtlDS.insertRow();
        if(downTable.getSelectedRow()+1==downTable.getRowCount())
            downTable.addRow();
        //ˢ���ܷ��ÿؼ�
        onBrushTotFeeCtl();
    }

    /**
     * ���ڳ�ʼ���ܷ��ú�ˢ���ܷ���
     */
    public void onBrushTotFeeCtl() {
        //�ǵ�table������
        double totFee = 0.0;
        int rows = downTable.getRowCount();
        for (int i = 0; i < rows; i++) {
            totFee += TypeTool.getDouble(downTable.getValueAt(i, 6));
        }

        TOT_FEE.setValue(totFee);

    }

    /**
     * ��ص�5�У�����ֵ�ĸı�ˢ���ܷ���
     * @param node TTableNode
     */
    public void onChangeFee(TTableNode node) {
        if (node.getColumn() == 5) {
            double ownPrice = TypeTool.getDouble(downTable.getValueAt(node.
                getRow(), 4));
            double oldValue = TypeTool.getDouble(node.getOldValue());
            double newValue = TypeTool.getDouble(node.getValue());
            //ȡ������ֵ�ò�
            double diffValue = newValue - oldValue;
            //�ø���Ŀ����*�������+ԭ�����ܷ���=���ڵ��ܷ���
            double totFee = diffValue * ownPrice +
                TypeTool.getDouble(TOT_FEE.getValue());
            TOT_FEE.setValue(totFee);
        }
    }


    /**
     * ���������table�¼�
     */
    public void onUpTableClicked(int row) {
        //��ǰѡ�е��к�
        selRow = row;
        //�������ؼ���ֵ
        clearCtl();
        //�õ����parm
        TParm tableDate = ( (TDS) upTable.getDataStore()).getBuffer(TDS.
            PRIMARY).getRow(selRow);
        //�����еĿؼ�ֵ
        //setValueForDownCtl(tableDate, row);
        setValueForDownCtl(tableDate);
        //������ʾ��ȫ������ʱ��Ĭ�ϲ��ɱ༭(����״̬�ָ��༭/ͣ��״̬���ɱ༭)
        if (ALL.isSelected()) {
            boolean activeFlg = ACTIVE_FLG.isSelected();
            setEnabledForCtl(activeFlg);
        }
        //�õ���ǰ��Ŀ����(SYS_FEE_HISTORY�У�ORDER_CODE/SYS_ORDERSETDETAI�У�ORDERSET_CODE)
        orderCode = ORDER_CODE.getValue();
        //��ʼ�������table
        initDownTable(orderCode);

        //ˢ���ܷ��ÿؼ������ڳ�ʼ�������table��
        onBrushTotFeeCtl();
    }

    /**
     * ��ʼ�������table
     * @param ordersetCode String
     */
    public void initDownTable(String ordersetCode) {

        downTable.acceptText();
        String sqlForDtl =
            "SELECT * FROM SYS_ORDERSETDETAIL WHERE ORDERSET_CODE = '" +
            ordersetCode + "'";
        sysOrdSetDtlDS.setSQL(sqlForDtl);
        sysOrdSetDtlDS.retrieve();
        //����������
        sysOdrsetDtlObserver obser = new sysOdrsetDtlObserver();
        //�Ѽ��������õ�ĳ��DS��
        sysOrdSetDtlDS.addObserver(obser);

        //���û��������ձ���ϵ�����
        if (sysOrdSetDtlDS.rowCount() <= 0) {
            downTable.removeRowAll();
        }

        sysOrdSetDtlDS.insertRow();

        downTable.setDataStore(sysOrdSetDtlDS);
        downTable.setDSValue();
    }

    /**
     * ��ճ���table����Ŀؼ���ֵ
     */
    public void clearCtl() {

        this.clearValue(controlName + ";TRANS_HOSP_CODE;ORDER_CODE" +
                        ";TOT_FEE");
        //===========pangben modify 20110427
        this.setValue("REGION_CODE",Operator.getRegion());
    }

    /**
     * ��ղ���
     */
    public void onClear() {
        clearCtl();
        upTable.removeRowAll();
        downTable.removeRowAll();
        setValue("UNIT","");
    }
    /**
	 * ��������
	 */
	public void onExport() {
		//���������ѯ�����Ľ����������ҳ����ʾ�����е���������ʱ��ֱ������
		String Sql = "SELECT * FROM (SELECT AA.ORDER_CODE ORDERSET_CODE,"
				+ "AA.ORDER_CODE,"
				+ "AA.ORDER_DESC,"
				+ "CC.UNIT_CHN_DESC,"
				+ "AA.OWN_PRICE,"
				+ "AA.SPECIFICATION,"
				+ "BB.DOSAGE_QTY,"
				+ "AA.ORDERSET_FLG"
				+ " FROM SYS_FEE AA, SYS_ORDERSETDETAIL BB,SYS_UNIT CC"
				+ " WHERE     AA.ORDER_CODE = BB.ORDERSET_CODE AND AA.UNIT_CODE = CC.UNIT_CODE"
				+ " AND AA.ORDER_CODE LIKE 'U%'"
				+ " AND (   AA.REGION_CODE = 'H01'"
				+ " OR AA.REGION_CODE IS NULL"
				+ " OR AA.REGION_CODE = '')"
				+ " UNION"
				+ " SELECT AA.ORDERSET_CODE,"
				+ "BB.ORDER_CODE,"
				+ "BB.ORDER_DESC,"
				+ "CC.UNIT_CHN_DESC,"
				+ "BB.OWN_PRICE,"
				+ "BB.SPECIFICATION,"
				+ "AA.DOSAGE_QTY,"
				+ "BB.ORDERSET_FLG "
				+ "FROM SYS_ORDERSETDETAIL AA, SYS_FEE BB,SYS_UNIT CC"
				+ " WHERE     AA.ORDER_CODE = BB.ORDER_CODE AND BB.UNIT_CODE = CC.UNIT_CODE"
				+ " AND AA.ORDERSET_CODE LIKE 'U%'"
				+ " AND (   BB.REGION_CODE = 'H01'"
				+ " OR BB.REGION_CODE IS NULL"
				+ " OR BB.REGION_CODE = ''))"
				+ " ORDER BY ORDERSET_CODE,ORDER_CODE,(CASE ORDERSET_FLG WHEN 'Y' THEN 0 ELSE 1 END)";
		
		TParm parm = new TParm(TJDODBTool.getInstance().select(Sql));
		
		//System.out.println("������������������������������������������������������������"+parm);
		if (parm.getCount() <= 0) {
			this.messageBox("û�л������");
			return;
		}
		//���õ������ķ�������ͷ���ֶ�������������Ҫ����д
		ExportExcelUtil.getInstance().exportExcel(
				"����,100;��������Ŀ����,200;���,170;�ԷѼ�,100,double,########0.0000;��λ,50,UNIT_CODE;"
				+"����,70;С��,100",
				"ORDER_CODE;ORDER_DESC;SPECIFICATION;OWN_PRICE;UNIT_CHN_DESC;"
				+"DOSAGE_QTY;N_TOTFEE",
				parm, "ҽ���ײ���ϸ");

	}

    

    /**
     * �½�
     */
    public void onNew() {

        clearCtl();
        cerateNewDate();
        //�ָ��༭״̬
        setEnabledForCtl(true);
        String nowOrdCode = ORDER_CODE.getValue();
        //��ʼ�������table
        initDownTable(nowOrdCode);

    }

    /**
     * ����
     */
    public boolean onSave() {

//        if (!ACTIVE_FLG.isSelected()) {
//            this.messageBox("��ѡ�����ñ�ǣ�");
//            return false;
//        }else{
//            onExe();
//        }
        if(onSaveCheck())
            return false;
        onExe();
        //�õ���ǰѡ���е����ݣ�Ҫ���ĺ��½����У�
        selRow = upTable.getSelectedRow();
        //==========pangben 2013-2-19 ���û��ѡ���в�ִ�в���
        if(selRow<0){
        	this.messageBox("��ѡ��Ҫ����������");
        	return false;
        }
        //ȡtable����
        TDataStore dataStore = upTable.getDataStore();

        dataStore.showDebug();
        setDataInSysFeeTds();
        bigObject.addDS("SYS_FEE", sysFeeDS);
        //�ӽ������õ����ݣ��ŵ�TDS�У����ڸ��±���
        setDataInTDS(dataStore, selRow, false);
        //�ô���󱣴棬�γ�һ������
        bigObject.addDS("SYS_FEE_HISTORY", (TDS) dataStore);
        getdownTableDS();
        bigObject.addDS("SYS_ORDERSETDETAIL", sysOrdSetDtlDS);
        if (!bigObject.update()) {
            messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        //ǰ̨ˢ��
        TIOM_Database.logTableAction("SYS_FEE");
        return true;

    }

    public void getdownTableDS() {

        //orderCode = ORDER_CODE.getValue();
        //��ǰ���ݿ�ʱ��
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        int detailCount = sysOrdSetDtlDS.rowCount();
        String orderSetCode = ORDER_CODE.getValue();
        for (int i = 0; i < detailCount; i++) {
            sysOrdSetDtlDS.setItem(i, "ORDERSET_CODE", orderSetCode);
            sysOrdSetDtlDS.setItem(i, "OPT_USER", Operator.getID());
            sysOrdSetDtlDS.setItem(i, "OPT_DATE", now);
            sysOrdSetDtlDS.setItem(i, "OPT_TERM", Operator.getIP());
            //==========pangben 2013-2-19 ����ظ�����������,ɾ��һ���Ѿ����ڵ�����
            if(null==sysOrdSetDtlDS.getItemString(i, 1) || sysOrdSetDtlDS.getItemString(i, 1).length()<=0)
            	sysOrdSetDtlDS.deleteRow(i);
        }
        //ɾ�����һ�У���Ϊ�Զ����������û������
        //sysOrdSetDtlDS.deleteRow(detailCount);
    }


    /**
     * �ռ������ϵ�ֵ����ʹ�ã����±��棩
     * @param dataStore TDataStore
     */
    public void setDataInTDS(TDataStore dataStore, int row, boolean dateFlg) {
        //��ǰ���ݿ�ʱ��
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        //���������
        dataStore.setItem(row, "OPT_USER", Operator.getID());
        dataStore.setItem(row, "OPT_DATE", date);
        dataStore.setItem(row, "OPT_TERM", Operator.getIP());

        dataStore.setItem(row, "START_DATE", START_DATE);
        dataStore.setItem(row, "END_DATE", END_DATE);
        //ִ�б��
        dataStore.setItem(row, "ACTIVE_FLG", getValue("ACTIVE_FLG"));

        dataStore.setItem(row, "ORDER_CODE", ORDER_CODE.getValue());
        dataStore.setItem(row, "ORDER_DESC", ORDER_DESC.getValue());
        dataStore.setItem(row, "PY1", PY1.getValue());
        dataStore.setItem(row, "USEDEPT_CODE", USEDEPT_CODE.getValue());
        dataStore.setItem(row, "EXEC_DEPT_CODE", EXEC_DEPT_CODE.getValue());

        dataStore.setItem(row, "CHARGE_HOSP_CODE", CHARGE_HOSP_CODE.getValue());
        dataStore.setItem(row, "ORDER_CAT1_CODE", ORDER_CAT1_CODE.getValue());
        dataStore.setItem(row, "DESCRIPTION", DESCRIPTION.getValue());

        dataStore.setItem(row, "OPD_FIT_FLG", OPD_FIT_FLG.getValue());
        dataStore.setItem(row, "EMG_FIT_FLG", EMG_FIT_FLG.getValue());
        dataStore.setItem(row, "IPD_FIT_FLG", IPD_FIT_FLG.getValue());
        dataStore.setItem(row, "HRM_FIT_FLG", HRM_FIT_FLG.getValue());
        dataStore.setItem(row, "TRANS_OUT_FLG", TRANS_OUT_FLG.getValue());
        dataStore.setItem(row, "UNIT_CODE", getValue("UNIT"));
        dataStore.setItem(row, "IS_REMARK", getValue("IS_REMARK"));
        dataStore.setItem(row, "ORDERSET_FLG", "Y");
        TParm parm = getPriceAction();
        dataStore.setItem(row, "OWN_PRICE", parm.getValue("OWN_PRICE",0));
        dataStore.setItem(row, "OWN_PRICE2", parm.getValue("OWN_PRICE2",0));
        dataStore.setItem(row, "OWN_PRICE3", parm.getValue("OWN_PRICE3",0));
        dataStore.setItem(row, "CAT1_TYPE",getCta1Type("" + getValue("ORDER_CAT1_CODE")));
        //=============pangben modify 20110427 start
        dataStore.setItem(row, "REGION_CODE", getValue("REGION_CODE"));
        //=============pangben modify 20110427 stop
        dataStore.setItem(row, "ORD_SUPERVISION", ORD_SUPERVISION.getValue());

    }

    public TParm getPriceAction(){
       double ownPrice = 0;
       double nhiPrice = 0;
       double govPrice = 0;
       /*for(int i = 0;i<downTable.getRowCount();i++){
           if(sysOrdSetDtlDS.getRowParm(i).getValue("ORDER_CODE").length() == 0)
               continue;
           String SQL = " SELECT (CASE WHEN OWN_PRICE IS NULL THEN 0 ELSE OWN_PRICE END) OWN_PRICE,"+
                        "        (CASE WHEN OWN_PRICE2 IS NULL THEN 0 ELSE OWN_PRICE2 END) OWN_PRICE2,"+
                        "        (CASE WHEN OWN_PRICE3 IS NULL THEN 0 ELSE OWN_PRICE3 END) OWN_PRICE3"+
                        " FROM SYS_FEE"+
                        " WHERE ORDER_CODE = '"+sysOrdSetDtlDS.getRowParm(i).getValue("ORDER_CODE")+"'";
           TParm price = new TParm(TJDODBTool.getInstance().select(SQL));
           ownPrice += (price.getDouble("OWN_PRICE",0) * sysOrdSetDtlDS.getRowParm(i).getDouble("DOSAGE_QTY"));
           nhiPrice += (price.getDouble("OWN_PRICE2",0) * sysOrdSetDtlDS.getRowParm(i).getDouble("DOSAGE_QTY"));
           govPrice += (price.getDouble("OWN_PRICE3",0) * sysOrdSetDtlDS.getRowParm(i).getDouble("DOSAGE_QTY"));
       }*/
       TParm parmPrice = new TParm();
       parmPrice.addData("OWN_PRICE",ownPrice);
       parmPrice.addData("OWN_PRICE2",nhiPrice);
       parmPrice.addData("OWN_PRICE3",govPrice);
       return parmPrice;
   }
    /**
     * �õ�SYS_FEE��TDS
     */
    private void setDataInSysFeeTds() {
        if(upTable.getSelectedRow() < 0)
            return;
        String sql = "";
        String orderCode = ORDER_CODE.getValue();
        sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE='" + orderCode + "'";
        sysFeeDS.setSQL(sql);
        if (sysFeeDS.retrieve() <= 0)
            return;
        //��ǰ���ݿ�ʱ��
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        //���������
        sysFeeDS.setItem(0, "OPT_USER", Operator.getID());
        sysFeeDS.setItem(0, "OPT_DATE", date);
        sysFeeDS.setItem(0, "OPT_TERM", Operator.getIP());
        sysFeeDS.setItem(0, "ORDER_CAT1_CODE", ORDER_CAT1_CODE.getValue());
        sysFeeDS.setItem(0, "CAT1_TYPE",getCta1Type("" + ORDER_CAT1_CODE.getValue()));
        sysFeeDS.setItem(0, "ACTIVE_FLG",getValue("ACTIVE_FLG"));
        sysFeeDS.setItem(0, "UNIT_CODE", getValue("UNIT"));
        sysFeeDS.setItem(0, "IS_REMARK", getValue("IS_REMARK"));
        //====pangben modify 20110427 start
        sysFeeDS.setItem(0, "REGION_CODE", getValue("REGION_CODE"));
         //====pangben modify 20110427 stop
        sysFeeDS.setItem(0, "ORD_SUPERVISION", getValue("ORD_SUPERVISION"));
        return ;
    }



    /**
     * ȡ��ҽ��ϸ����
     * @param orderCat1Type String
     * @return String
     */
    private String getCta1Type(String orderCat1Type){
        String SQL = "SELECT CAT1_TYPE FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE = '"+orderCat1Type+"'";
        TParm parm = new TParm(getDBTool().select(SQL));
        if(parm.getErrCode() != 0)
            return "";
        return parm.getValue("CAT1_TYPE",0);
    }

    /**
     * ȡ�����ݿ������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
       return TJDODBTool.getInstance();
    }
    /**
     * ���½���ʱ���Զ����ɱ���ŵ�������
     */
    public void cerateNewDate() {
        String newCode = "";

        //�����ı�
        upTable.acceptText();
        //ȡtable����
       // TDataStore dataStore = upTable.getDataStore();
        //�ҵ������Ҵ���(dataStore,����)
        //========pangben modify 20110427 start ע��ȥ������Ҫ��ѯ���е�����ţ�ͨ����ѯ���ݿ��еĵ���ֵ��ʾ�����
        // String maxCode = getMaxCode(dataStore, "ORDER_CODE");
        //System.out.println("maxCode:"+maxCode);
        //========pangben modify 20110427 start

        //�ҵ�ѡ�е����ڵ�
        TTreeNode node = tree.getSelectionNode();
        //���û��ѡ�еĽڵ�
        if (node == null)
            return;
        //�õ���ǰID
        String nowID = node.getID();
        int classify = 1;
        //�����������ĸ��ڵ����,�õ���ǰ�������
        if (nowID.length() > 0)
            classify = ruleTool.getNumberClass(nowID) + 1;
        //�������С�ڵ�,��������һ��
        if (classify > ruleTool.getClassifyCurrent()) {
            //�õ�Ĭ�ϵ��Զ���ӵ�ҽ������
            //============���Ҵ˱�Ź����б�����ֵ
            String sql =
                    "SELECT MAX(ORDER_CODE) AS ORDER_CODE FROM SYS_FEE_HISTORY WHERE ORDER_CODE LIKE '" +
                    nowID + "%'";
            TParm parm = new TParm(getDBTool().select(sql));
            String maxCode = parm.getValue("ORDER_CODE", 0);
            //===pangben modify 20110427 start

            String no = ruleTool.getNewCode(maxCode, classify);
            newCode = nowID + no;
            //�õ�����ӵ�table�����к�(�൱��TD�е�insertRow()����)
            int row = upTable.addRow();
            //���õ�ǰѡ����Ϊ��ӵ���
            upTable.setSelectedRow(row);
            //�����Ҵ������Ĭ��ֵ
            upTable.setItem(row, "ORDER_CODE", newCode);
            //Ĭ�Ͽ�������
            upTable.setItem(row, "ORDER_DESC", "(�½�����)");
            //Ĭ�Ͽ��Ҽ��
            upTable.setItem(row, "SPECIFICATION", null);
            //Ĭ�Ͽ���
            upTable.setItem(row, "OWN_PRICE", null);
            //Ĭ����С����ע��
            upTable.setItem(row, "UNIT_CODE", null);
        }
        //���Զ����ɵ�orderCode���õ�ORDER_CODE��
        ORDER_CODE.setText(newCode);
    }


    /**
     * �õ����ı��
     * @param dataStore TDataStore
     * @param columnName String
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
        }
        else {
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
     * @param date TParm
     */
    public void setValueForDownCtl(TParm date) {

        clearCtl();
        this.setValue("SUB_SYSTEM_CODE", date.getValue("SUB_SYSTEM_CODE"));
        this.setValue("SUB_SYSTEM_CODE", date.getValue("SUB_SYSTEM_CODE"));
        this.setValue("RPTTYPE_CODE", date.getValue("RPTTYPE_CODE"));
        this.setValue("DEV_CODE", date.getValue("DEV_CODE"));
        this.setValue("OPTITEM_CODE", date.getValue("OPTITEM_CODE"));
        this.setValue("MR_CODE", date.getValue("MR_CODE"));
        this.setValue("DEGREE_CODE", date.getValue("DEGREE_CODE"));

        this.setValue("ACTIVE_FLG", date.getValue("ACTIVE_FLG"));
        this.setValue("ORDER_CODE", date.getValue("ORDER_CODE"));
        this.setValue("ORDER_DESC", date.getValue("ORDER_DESC"));
        this.setValue("PY1", date.getValue("PY1"));
        this.setValue("DESCRIPTION", date.getValue("DESCRIPTION"));

        this.setValue("CHARGE_HOSP_CODE", date.getValue("CHARGE_HOSP_CODE"));
        this.setValue("ORDER_CAT1_CODE", date.getValue("ORDER_CAT1_CODE"));
        this.setValue("USEDEPT_CODE", date.getValue("USEDEPT_CODE"));
        this.setValue("EXEC_DEPT_CODE", date.getValue("EXEC_DEPT_CODE"));
        this.setValue("DESCRIPTION", date.getValue("DESCRIPTION"));

        this.setValue("OPD_FIT_FLG", date.getValue("OPD_FIT_FLG"));
        this.setValue("EMG_FIT_FLG", date.getValue("EMG_FIT_FLG"));
        this.setValue("IPD_FIT_FLG", date.getValue("IPD_FIT_FLG"));
        this.setValue("HRM_FIT_FLG", date.getValue("HRM_FIT_FLG"));
        this.setValue("TRANS_OUT_FLG", date.getValue("TRANS_OUT_FLG"));
        this.setValue("TRANS_HOSP_CODE", date.getValue("TRANS_HOSP_CODE"));
        this.setValue("UNIT", date.getValue("UNIT_CODE"));
        this.setValue("IS_REMARK", date.getValue("IS_REMARK"));
        //=============pangben modify 20110427 start
        this.setValue("REGION_CODE",date.getValue("REGION_CODE"));
        //=============pangben modify 20110427 stop
        this.setValue("ORD_SUPERVISION",date.getValue("ORD_SUPERVISION"));
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
     * �õ�����ƴ��
     */
    public void onPY1() {
        String orderDesc = ORDER_DESC.getText();
        String orderPy = SYSHzpyTool.getInstance().charToCode(orderDesc);
        PY1.setText(orderPy);
        DESCRIPTION.grabFocus();
    }


    /**
     * �������еĿؼ��Ƿ���ã���ʷ���ݲ������޸ģ�
     * @param flg boolean
     */
    public void setEnabledForCtl(boolean flg) {
        String tag[] = controlName.split(";");
        int count = tag.length;
        for (int i = 0; i < count; i++) {
            this.callFunction("UI|" + tag[i] + "|setEnabled", flg);
        }
        ACTIVE_FLG.setEnabled(true);
    }

    /**
     * �����á�ֻ����end_date�������start_date(�½�����ǰʱ�䣻��ѯ���ģ��Ǿɵ�ʱ��)
     */
    public void onExe() {

        Timestamp time = TJDODBTool.getInstance().getDBTime();
        String tempStartDate = StringTool.getString(time,
            "yyyy/MM/dd HH:mm:ss");
        START_DATE = tempStartDate.substring(0, 4) +
            tempStartDate.substring(5, 7) +
            tempStartDate.substring(8, 10) +
            tempStartDate.substring(11, 13) +
            tempStartDate.substring(14, 16) +
            tempStartDate.substring(17, 19);

        //�������õ�ʱ���ʧЧ����дΪ"99991231235959"
        if (TypeTool.getBoolean(ACTIVE_FLG.getValue())) {
            END_DATE = "99991231235959";
        }
        else { //ͣ�õ�ʱ��end_date��Ϊ��ǰʱ��
            String date = StringTool.getString(time, "yyyy/MM/dd HH:mm:ss");
            END_DATE = date.substring(0, 4) +
                date.substring(5, 7) + date.substring(8, 10) +
                date.substring(11, 13) +
                date.substring(14, 16) +
                date.substring(17, 19);
        }
    }


    /**
     * table˫��ѡ����
     * @param row int
     */
    public void onTableDoubleCleck() {

        upTable.acceptText();
        int row = upTable.getSelectedRow();
        String value = upTable.getItemString(row, 0);
        //�õ��ϲ����
        String partentID = ruleTool.getNumberParent(value);
        TTreeNode node = treeRoot.findNodeForID(partentID);
        if (node == null)
            return;
        //�õ������ϵ�������
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        //��������ѡ�нڵ�
        tree.setSelectNode(node);
        tree.update();
        //���ò�ѯ�¼�
        //=====pangben modify 20110427 start
        onCleckClassifyNode(partentID, upTable,Operator.getRegion());
        //=====pangben modify 20110427 stop
        //table������ѡ����
        int count = upTable.getRowCount(); //table������
        for (int i = 0; i < count; i++) {
            //�õ����ʴ���
            String invCode = upTable.getItemString(i, 0);
            if (value.equals(invCode)) {
                //����ѡ����
                upTable.setSelectedRow(i);
                return;
            }
        }
    }

    /**
     * ѡ�ж�Ӧ���ڵ���¼�
     * @param parentID String
     * @param table TTable
     */
    public void onCleckClassifyNode(String parentID, TTable table,String regionCode) {
        //===========pangben modify 20110427 start
        String region="";
        if(null!=regionCode&&!"".equals(regionCode))
            region=" AND (REGION_CODE='"+regionCode+"' OR REGION_CODE IS NULL OR REGION_CODE ='') ";
        //table�е�datastore�в�ѯ����sql
        table.setSQL("select * from SYS_FEE_HISTORY where ORDER_CODE like '" +
                     parentID +
                     "%'"+region);
          //===========pangben modify 20110427 stop
        //��������
        table.retrieve();
        //�������ݵ�table��
        table.setDSValue();
        //����������ť
        callFunction("UI|new|setEnabled", true);

    }

    /**
     * ����
     * @param ob Object
     */
    public void onFilter(Object ob) {
        if ("ALL".equals(ob.toString())) {
            upTable.setFilter("");
            upTable.filter();
            //�������ݵ�table��
            upTable.setDSValue();
            //����������ť
            callFunction("UI|new|setEnabled", true);
        }
        else if ("Y".equals(ob.toString())) {
            upTable.setFilter("ACTIVE_FLG='Y'");
            upTable.filter();
            //�������ݵ�table��
            upTable.setDSValue();
            //����������ť
            callFunction("UI|new|setEnabled", true);
        }
        else {
            upTable.setFilter("ACTIVE_FLG='N'");
            upTable.filter();
            //�������ݵ�table��
            upTable.setDSValue();
            //����������ť
            callFunction("UI|new|setEnabled", false);
        }
        updateMastOwnPrice();

    }
    public boolean onSaveCheck(){
//        if(getValueString("USEDEPT_CODE").length() == 0){
//            messageBox("���ÿ��Ҳ���Ϊ��");
//            return true;
//        }
//        if(getValueString("EXEC_DEPT_CODE").length() == 0){
//            messageBox("ִ�п��Ҳ���Ϊ��");
//            return true;
//        }
        if(getValueString("ORDER_CODE").length() == 0){
            messageBox("ҽ�����벻��Ϊ��");
            return true;
        }
        if(getValueString("CHARGE_HOSP_CODE").length() == 0){
            messageBox("Ժ�ڴ��벻��Ϊ��");
            return true;
        }
        if(getValueString("ORDER_DESC").length() == 0){
            messageBox("ҽ�����Ʋ���Ϊ��");
            return true;
        }
        if(getValueString("UNIT").length() == 0){
            messageBox("��λ����Ϊ��");
            return true;
        }
        if(getValueString("PY1").length() == 0){
            messageBox("ҽ��ƴ������Ϊ��");
            return true;
        }
        if(getValueString("ORDER_CAT1_CODE").length() == 0){
            messageBox("ҽ�����಻��Ϊ��");
            return true;
        }
        return false;
    }


    //��������
    public static void main(String[] args) {
        JavaHisDebug.initClient();
        //JavaHisDebug.TBuilder();

//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("sys\\SYS_FEE\\SYSFEE_ORDPACK.x");
    }

}
