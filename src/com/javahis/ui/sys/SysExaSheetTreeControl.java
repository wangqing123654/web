package com.javahis.ui.sys;

import com.dongyang.control.*;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TTreeNode;
import jdo.sys.SYSRuleTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TButton;
import com.dongyang.util.RunClass;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;

/**
 * <p>Title: ���ñ�</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author WangM
 * @version 1.0
 */
public class SysExaSheetTreeControl
    extends TControl {
    /**
     * ��
     */
    private static String TREE = "TREE";
    /**
     * ���ݱ�1
     */
    private static String TABLE1 = "TABLE1";
    /**
     * ���ݱ�2
     */
    private static String TABLE2 = "TABLE2";
    /**
     * ����
     */
    private TTreeNode treeRoot;
    /**
     * �������ݷ���datastore���ڶ��������ݹ���
     */
    private TDataStore treeDataStore = new TDataStore();
    /**
     * ��Ź�����𹤾�
     */
    private SYSRuleTool ruleTool;
    /**
     * ���ղ���
     */
    private Object main;
    /**
     * ��ǰҳ
     */
    private int onlyPage = 1;
    /**
     * ��ID
     */
    private String treeId;
    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        getInitParameter();
        onInitTree();
        initEven();
        onInitNode();
    }

    /**
     * ��ʼ���¼�
     */
    public void initEven() {
        //��tree��Ӽ����¼�
        addEventListener(TREE + "->" + TTreeEvent.CLICKED, "onTreeClicked");
    }

    /**
     * ��ʼ������
     */
    public void getInitParameter() {
        main = this.getParameter();
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
        treeRoot.setText("���������");
        treeRoot.setEnText("Inspection Inspection Classification");
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
            "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='EXM_RULE'");
        //�����dataStore���õ�������С��0
        if (treeDataStore.retrieve() <= 0)
            return;
        //��������,�Ǳ�������еĿ�������
        ruleTool = new SYSRuleTool("EXM_RULE");
        if (ruleTool.isLoad()) { //�����۽ڵ����:datastore���ڵ����,�ڵ���ʾ����,,�ڵ�����
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                "CATEGORY_CODE",
                "CATEGORY_CHN_DESC", "Path", "SEQ","CATEGORY_ENG_DESC");
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
     * ���¼�
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
        //���
        if (parm == null)
            return;
        onClear();
        TTreeNode node = (TTreeNode) parm;
        if ("Root".equals(node.getType())) {
            return;
        }
        
        //$$ ====2012/03/16 add by lx start �����ʱ һ������������һҳ���ٿ��������ʱ������һҳ����Ҫ�ٵ���һҳ����===$$//
        onlyPage = 1;
        //$$ ====2012/03/16 add by lx start===$$//
        treeId = node.getID();
        queryTabelData();
    }

    /**
     * ��ѯ���
     */
    public void queryTabelData() {
        int row = (onlyPage - 1) * 20;
        TParm table1Parm = this.queryExa(treeId, row, row + 19);
        //ȫ������
        int rowCount = (int) ( (double) table1Parm.getInt("SYSTEM", "COUNT") /
                              20.0 + 0.999);
        this.getTTable(TABLE1).setParmValue(table1Parm);
        if("en".equals(this.getLanguage()))
            this.setValue("PAGECOUNT", "Page" + onlyPage + "/" + rowCount);
        else
            this.setValue("PAGECOUNT", "��" + onlyPage + "ҳ/��" + rowCount + "ҳ");
        ( (TButton)this.getComponent("BP")).setEnabled(onlyPage > 1);
        ( (TButton)this.getComponent("BN")).setEnabled(onlyPage < rowCount);
        //TABLE2
        table1Parm = this.queryExa(treeId, row + 20, row + 39);
        this.getTTable(TABLE2).setParmValue(table1Parm);
    }

    /**
     * ��һҳ
     */
    public void nextPage() {
        onlyPage++;
        queryTabelData();
    }

    /**
     * ��һҳ
     */
    public void upPage() {
        onlyPage--;
        queryTabelData();
    }

    /**
     *
     * @param code String
     * @return ��ѯsys_fee
     */
    public TParm queryExa(String code, int startRow, int endRow) {
        //=========pangben modify 20110607 start
       String region="";
       if(null!=Operator.getRegion()&&!"".equals(Operator.getRegion()))
           region = " AND (REGION_CODE='" + Operator.getRegion() + "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
       //=========pangben modify 20110607 stop
        String sql =
            "SELECT 'N' AS EXEC ,ORDER_CODE,ORDER_DESC,PY1,PY2," +
            "	SEQ,DESCRIPTION,TRADE_ENG_DESC,GOODS_DESC,GOODS_PYCODE," +
            "	ALIAS_DESC,ALIAS_PYCODE,SPECIFICATION,NHI_FEE_DESC,HABITAT_TYPE," +
            "	MAN_CODE,HYGIENE_TRADE_CODE,ORDER_CAT1_CODE,CHARGE_HOSP_CODE,OWN_PRICE,NHI_PRICE," +
            "	GOV_PRICE,UNIT_CODE,LET_KEYIN_FLG,DISCOUNT_FLG,EXPENSIVE_FLG," +
            "	OPD_FIT_FLG,EMG_FIT_FLG,IPD_FIT_FLG,HRM_FIT_FLG,DR_ORDER_FLG," +
            "	INTV_ORDER_FLG,LCS_CLASS_CODE,TRANS_OUT_FLG,TRANS_HOSP_CODE,USEDEPT_CODE," +
            "	EXEC_ORDER_FLG,EXEC_DEPT_CODE,INSPAY_TYPE,ADDPAY_RATE,ADDPAY_AMT," +
            "	NHI_CODE_O,NHI_CODE_E,NHI_CODE_I,CTRL_FLG,CLPGROUP_CODE," +
            "	ORDERSET_FLG,INDV_FLG,SUB_SYSTEM_CODE,RPTTYPE_CODE,DEV_CODE," +
            "	OPTITEM_CODE,MR_CODE,DEGREE_CODE,CIS_FLG,OPT_USER," +
            "	OPT_DATE,OPT_TERM,CAT1_TYPE " +
            " FROM SYS_FEE " +
            " WHERE ORDER_CODE LIKE '%" + code +
            "%' AND ACTIVE_FLG='Y' " + region+" ORDER BY SEQ";//�����ORDER_DESC���� 20130722 caoyong
        TParm result = new TParm(TJDODBTool.getInstance().select("", sql, true,
            startRow, endRow));
//        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        //System.out.println("RESULT===============" + result);
        return result;
    }

    /**
     * �ش��¼�
     */
    public void onFetchBack(String tableName) {
        TTable table = (TTable)this.getComponent(tableName);
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();
        if ("ORDER_DESC".equalsIgnoreCase(table.getParmMap(column))) {
            return;
        }
        table.setValueAt("Y", row, column);
        TParm parm = table.getParmValue().getRow(row);
        boolean result = TypeTool.getBoolean(RunClass.runMethod(main,
            "onQuoteSheet", new Object[] {parm}));
        if (result) {
            table.setValueAt("N", row, column);
        }
    }
    /**
     * �Ҽ�
     */
    public void showPopMenu(String tableName){
        TTable table = (TTable)this.getComponent(tableName);
        table.setPopupMenuSyntax("��ʾ����ҽ��ϸ�� \n Display collection details with your doctor,openRigthPopMenu|"+tableName+"");
    }
    /**
     * ϸ��
     */
    public void openRigthPopMenu(String tableName){
        TTable table = (TTable)this.getComponent(tableName);
        TParm parm = table.getParmValue().getRow(table.getSelectedRow());
//        System.out.println("ѡ����:"+parm);
        TParm result = this.getOrderSetDetails(parm.getValue("ORDER_CODE"));
        this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", result);
    }
    /**
     * ���ؼ���ҽ��ϸ���TParm��ʽ
     * @return result TParm
     */
    public TParm getOrderSetDetails(String orderCode) {
        TParm result = new TParm();
        String sql = "SELECT B.*,A.DOSAGE_QTY FROM SYS_ORDERSETDETAIL A,SYS_FEE B  WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORDERSET_CODE='"+orderCode+"'";
        TParm parm = new TParm(this.getDBTool().select(sql));
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            //ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
            result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
            result.addData("SPECIFICATION",
                           parm.getValue("SPECIFICATION", i));
            result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
            result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
            //�����ܼ۸�
            double ownPrice = parm.getDouble("OWN_PRICE", i) *
                parm.getDouble("DOSAGE_QTY", i);
            result.addData("OWN_PRICE", parm.getDouble("OWN_PRICE", i));
            result.addData("OWN_AMT", ownPrice);
            result.addData("EXEC_DEPT_CODE",
                           parm.getValue("EXEC_DEPT_CODE", i));
            result.addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE", i));
            result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
        }
        return result;
    }

    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    /**
     * �ر�
     */
    public boolean onClosing() {
        TParm parm = new TParm();
        parm.setData("YYLIST","Y");
        TypeTool.getBoolean(RunClass.runMethod(main,
            "setYYlist", new Object[] {parm}));
        return true;
    }

    /**
     * ��ղ���
     */
    public void onClear() {
        this.getTTree(TREE).update();
        this.getTTable(TABLE1).removeRowAll();
        this.getTTable(TABLE2).removeRowAll();
    }

    /**
     * ��
     * @param tag String
     * @return TTree
     */
    public TTree getTTree(String tag) {
        return (TTree)this.getComponent(tag);
    }

    /**
     * ���ݱ�
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
}
