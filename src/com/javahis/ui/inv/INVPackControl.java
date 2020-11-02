package com.javahis.ui.inv;

import java.awt.Component;
import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SYSPublic;
import jdo.sys.SYSRuleTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: �������ֵ�</p>
 *
 * <p>Description:�������趨 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw
 * @version 1.0
 */
public class INVPackControl
    extends TControl {
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
     * ����ѡ�е���
     */
    private int tableMSelectedRow = -1;
    /**
     * ����
     */
    private TTable tableM;
    /**
     * ϸ��
     */
    private TTable tableD;
    /**
     * ��
     */
    private TTree tree;
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        //��
        tree = (TTree) getComponent("TREE");
        //��ʼ����
        onInitSelectTree();
        //��tree��Ӽ����¼�
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        //��Table��������¼�(ֵ�ı�)
        addEventListener("TABLEM->" + TTableEvent.CHANGE_VALUE,
                         "onTableMChangeValue");
        //��Table��������¼�(ֵ�ı�)
        addEventListener("TABLED->" + TTableEvent.CHANGE_VALUE,
                         "onTableChangeDValue");
        //tableר�õļ���
        callFunction("UI|TABLED|addEventListener",
                     TTableEvent.CREATE_EDIT_COMPONENT, this,
                     "onCreateEditComponent");
        //����
        onCreatTree();
        //table���ɱ༭
        tableM = (TTable) getComponent("TABLEM");
        tableD = (TTable) getComponent("TABLED");
        //��ʼ��table
        tableM.setLockColumns("0,2,7,8,9");    //wm2013-05-28     tableM.setLockColumns("0,1,2,3,4,5,6,7");
        onInitTable();
        //��ӹ۲���
         observer();
        
    }

    /**
     * ��ӹ۲���
     */
    public void observer() {
        TTable table = (TTable) callFunction("UI|TABLED|getThis");
        TDS tds = (TDS) table.getDataStore();
        //��������ӹ۲���
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL("SELECT INV_CODE,INV_CHN_DESC,DESCRIPTION FROM INV_BASE");
        dataStore.retrieve();
        tds.addObserver(new INVBaseTob(dataStore));
    }
    /**
     * ��ʼ��table
     */
    public void onInitTable() {
        //����
        tableM.setSQL("SELECT * from INV_PACKM WHERE PACK_CODE IS NULL");
        tableM.retrieve();
        //ϸ��
        tableD.setSQL("SELECT * from INV_PACKD WHERE PACK_CODE IS NULL");
        tableD.retrieve();
    }

    /**
     * ��ʼ����
     */
    public void onInitSelectTree() {
        //�õ�����
        treeRoot = tree.getRoot();
        if (treeRoot == null)
            return;
        //�����ڵ����������ʾ
        //zhangyong20091028 ����������==>���������
        treeRoot.setText("���������");
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
     * ��ʼ�����ϵĽڵ�
     */
    public void onCreatTree() {
        //��dataStore��ֵ
        treeDataStore.setSQL(
            "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='INV_PACK'");
        //�����dataStore���õ�������С��0
        if (treeDataStore.retrieve() <= 0)
            return;
        //��������,�Ǳ�������е���������
        ruleTool = new SYSRuleTool("INV_PACK");
        if (ruleTool.isLoad()) {
            //�����۽ڵ����:datastore���ڵ����,�ڵ���ʾ����,,�ڵ�����
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                "CATEGORY_CODE",
                "CATEGORY_CHN_DESC", "Path", "SEQ");
            //ѭ����������ڵ�
            for (int i = 0; i < node.length; i++)
                treeRoot.addSeq(node[i]);
        }
        //������
        tree.update();
        //��������Ĭ��ѡ�нڵ�
        tree.setSelectNode(treeRoot);
        getTreeLoyar();
    }

    /**
     * ������
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
        //�õ�������Ľڵ����
        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        //�õ�table����
        //table�������иı�ֵ
        tableM.acceptText();
        //table���ɱ༭
        tableM.setLockColumns("0,2,7,8,9");					//wm2013-05-28   tableM.setLockColumns("0,1,2,3,4,5,6,7");
        tableMSelectedRow = -1;
        //�õ���ID
        String parentID = node.getID();
        int classify = 1;
        if (parentID.length() > 0)
            classify = ruleTool.getNumberClass(parentID) + 1;
        
        //�������С�ڵ�,��������һ��
        if (classify >= ruleTool.getClassifyCurrent()) {//wm2013-05-28      classify > ruleTool.getClassifyCurrent()
            //�������ݱ����ʾ
            if (!checkValueChange())
                return;
            onCleckClassifyNode(parentID, tableM);
        }
    }

    /**
     * ��չ����combox��ֵ
     */
    public void getTreeLoyar() {
        TComboBox comBox = (TComboBox)this.callFunction("UI|TREELOYAR|getThis");
        //�����ݸ���combo
        comBox.setParmValue(SYSPublic.getTreeLoyar(treeRoot));
        //ˢ�ֽ����ϵ�combo
        comBox.updateUI();
    }

    /**
      * չ������Ӧ�Ĳ�
      */
     public void onExtendToLoyar(){
         tree.expandLayer(getValueInt("TREELOYAR")-1);
         tree.collapseLayer(getValueInt("TREELOYAR"));
     }
    /**
     * ѡ�����һ����¼�
     * @param parentID String
     * @param table TTable
     */
    public void onCleckClassifyNode(String parentID, TTable table) {
        //����Ƿ������ݱ��
        if (!checkValueChange())
            return;

        //table�е�datastore�в�ѯ����sql
        table.setSQL("SELECT * FROM INV_PACKM where PACK_CODE like '" +
                     parentID +
                     "%'");
        //��������
        table.retrieve();
        //�������ݵ�table��
        table.setDSValue();
        //table���Ա༭
        table.setLockColumns("0");	//wm2013-07-14
        // ����ϸ������
        //�����ʾ����
        tableD.clearSelection();
        //���table����������
        tableD.removeRowAll();
        //��ձ����¼
        tableD.getDataStore().resetModify();

    }

    /**
     * table˫��ѡ����
     */
    public void onTableDoubleCleck() {
        tableM.acceptText();
        int row = tableM.getSelectedRow();
        String value = tableM.getItemString(row, 0);
        //�õ��ϲ����
        String partentID = ruleTool.getNumberParent(value);
        TTreeNode node = treeRoot.findNodeForID(partentID);
        if (node == null)
            return;
        //��������ѡ�нڵ�
        tree.setSelectNode(node);
        tree.update();
        //���ò�ѯ�¼�
        onCleckClassifyNode(partentID, tableM);
        //table������ѡ����
        int count = tableM.getRowCount(); //table������
        for (int i = 0; i < count; i++) {
            //�õ����ʴ���
            String invCode = tableM.getItemString(i, 0);
            if (value.equals(invCode)) {
                //����ѡ����
                tableM.setSelectedRow(i);
                return;
            }
        }
    }

    /**
     * �������¼�
     */
    public void onTableMClicked() {
        int row = tableM.getSelectedRow();
        //���ѡ��ͬһ�У���ִ���κβ���
        if (tableMSelectedRow == row)
            return;
        //��������Ƿ���
        if (!checkValueChange())
            return;
        tableMSelectedRow = row;
        //�õ�����
        String packCode = tableM.getItemString(row, "PACK_CODE");
        if (packCode == null || packCode.length() <= 0) {
            this.messageBox("������������������!");
            return;
        }
        //��
        setValue("PACK_CODED", packCode);
        //����ϸ������
        tableD.acceptText();
        tableD.removeRowAll();
        //ȡtable����
        tableD.setSQL("SELECT * FROM INV_PACKD WHERE PACK_CODE='" +
                      packCode + "'");
        tableD.retrieve();
        tableD.setDSValue();
    }

    /**
     * ϸ�����¼�
     */
    public void onTableDClecked() {
        tableM.acceptText();
    }

    /**
     * ��������
     */
    public void onNew() {
        //���ѡ�������л���ѡ��ϸ��������ϸ��
        if (tableM.getSelectedRow() >= 0) {
            onNewPackD();
            return;
        }
        //��������
        onNewPack();
    }

    /**
     * ����������
     */
    public void onNewPack() {
        //�����ı�
        tableM.acceptText();
        //ȡtable����
        TDataStore dataStore = tableM.getDataStore();
        //�ҵ���ǰ�����������
        String maxCode = getMaxCode(dataStore, "PACK_CODE");
        //�ҵ�ѡ�е����ڵ�
        TTreeNode node = tree.getSelectionNode();
        //���û��ѡ�еĽڵ�
        if (node == null)
            return;
        //�õ���ID
        String parentID = node.getID();
        int classify = 1;
        //�����������ĸ��ڵ����,�õ���ǰ�������
        if (parentID.length() > 0)
            classify = ruleTool.getNumberClass(parentID) + 1;
        //�������С�ڵ�,��������һ��
        if (classify >= ruleTool.getClassifyCurrent()) { //�õ�Ĭ�ϵ��Զ���ӵĿ��Ҵ���           wm2013-05-28     ԭclassify > ruleTool.getClassifyCurrent()
            String no = ruleTool.getNewCode(maxCode, classify);
            //�õ�����ӵ�table�����к�
            int row = tableM.addRow();
            //Ĭ����������
            tableM.setItem(row, "PACK_CODE", parentID + no);
            //Ĭ����Ź���
            tableM.setItem(row, "SEQ_FLG", "1");
            //ѡ���е�����������
            tableMSelectedRow = row;
            //����ϸ������
            //ȡtable����
            tableD.setSQL("SELECT * FROM INV_PACKD WHERE PACK_CODE='" +
                          (parentID + no) +
                          "'");
            tableD.retrieve();
            tableD.setDSValue();
        }
    }

    /**
     * ��������ϸ
     */
    public void onNewPackD() {
        if (tableMSelectedRow < 0) {
            messageBox("��ѡ��������!");
            return;
        }
        String packCode = tableM.getItemString(tableMSelectedRow, "PACK_CODE");
        if (packCode == null || packCode.length() <= 0) {
            this.messageBox("��ѡ��������!");
            return;
        }
        //������
        int row = tableD.addRow();
        //����
        tableD.setItem(row, "PACK_CODE", packCode);

    }

    /**
     *
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateEditComponent(Component com, int row, int column) {
        //����sysfee�Ի������
        if (column != 0)
            return;
        if (! (com instanceof TTextField))
            return;
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        //��table�ϵ���text����sys_fee��������
          textfield.setPopupMenuParameter("INVBASE",
                                          getConfigParm().newConfig(
                                              "%ROOT%\\config\\inv\\INVBasePopup.x"));
        //����text���ӽ���sys_fee�������ڵĻش�ֵ
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                   "newInv");
    }

    /**
     * ������ϸ
     * @param tag String
     * @param obj Object
     */
    public void newInv(String tag, Object obj) {
        //sysfee���ص����ݰ�
        TParm parm = (TParm) obj;
        //sysfee���ص����ʴ���
        String inv_code = parm.getValue("INV_CODE");
        int row = tableD.getSelectedRow();
        //�õ�����
        String packCode = tableD.getItemString(row, "PACK_CODE");
        TDataStore dataStore=tableD.getDataStore();
        if (dataStore.exist("PACK_CODE='" + packCode +
                                        "'AND INV_CODE ='" + inv_code + "'")) {
            messageBox_("���" + inv_code + "�ظ�!");
            return;
        }
        tableD.acceptText();
        //���ʴ���
        dataStore.setItem(row, "INV_CODE", inv_code);
        //��Ź���
        dataStore.setItem(row, "SEQMAN_FLG", parm.getData("SEQMAN_FLG"));
        //��λ
        dataStore.setItem(row, "STOCK_UNIT", parm.getData("STOCK_UNIT"));
        //��������
        if(parm.getData("SEQMAN_FLG").equals("Y")){
        	dataStore.setItem(row, "PACK_TYPE", 0);	//��һ����
        }else{
        	dataStore.setItem(row, "PACK_TYPE", 1);	//һ����
        }
        //���   2013-07-18
        dataStore.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
        
        
//        //��������
//        dataStore.setItem(row, "PACK_TYPE", parm.getData("PACK_TYPE"));

        tableD.setDSValue();
        tableD.getTable().grabFocus();
        tableD.setSelectedColumn(2);
        tableD.setSelectedRow(row);

    }

    /**
     * ����ֵ�ı��¼�
     * @param obj Object
     * @return boolean
     */
    public boolean onTableMChangeValue(Object obj) {
        //�õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        //����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
        if (node.getValue().equals(node.getOldValue()))
            return false;
        //�õ�table�ϵ�parmmap������
        String columnName = tableM.getDataStoreColumnName(node.
            getColumn());
        //�õ��ı����
        int row = node.getRow();
        //�õ���ǰ�ı�������
        String value = "" + node.getValue();
        //��������Ƹı���ƴ��1�Զ�����,�������Ʋ���Ϊ��
        if ("PACK_DESC".equals(columnName)) {
            if (value.equals("") || value == null) {
                messageBox_("���Ʋ���Ϊ��!");
                return true;
            }
            String py =SYSHzpyTool.getInstance().charToCode(value);
            tableM.setItem(row, "PY1", py);
            return false;
        }
        //��Ź�����Ϊ��
        if ("SEQ_FLG".equals(columnName)) {
            if (value.equals("") || value == null) {
                messageBox_("��ѡ���Ƿ�����Ź���!");
                return true;
            }
            return false;
        }
        //����Ǵ���ı���Ҫ���м���У��
        if ("PACK_CODE".equals(columnName)) {
            //��������������ݳ���
            int length = ruleTool.getTot();
            //����ı��ĳ��Ȳ����ڱ�������еĳ��ȷ���
            if (length != value.length()) {
                messageBox_("���" + value + "���Ȳ�����" + length + "!");
                return true;
            }
            //�Դ�����м��...����������ظ�
            if (tableM.getDataStore().exist("PACK_CODE='" + value +
                                            "'")) {
                messageBox_("���" + value + "�ظ�!");
                return true;
            }
            //�õ��ϲ����
            String partent = ruleTool.getNumberParent(value);
            //����ϲ�����Ƿ����
            if (treeRoot.findNodeForID(partent) == null) {
                messageBox_("��ŷ���" + partent + "������!");
                return true;
            }
            //�õ�ѡ�еĽڵ�
            String nodeId = tree.getSelectionNode().getID();
            //����ϼ�����Ƿ���ȷ
            int ruleLength = nodeId.length();
            //���ܻ�����
            if (!nodeId.equals(value.substring(0, ruleLength))) {
                messageBox_("�ϼ���ŷ���" + partent + "¼�����!");
                return true;
            }
            //������İ��Ÿ���...ϸ�����ͬʱ����
            //ϸ�����Ŀ��
            int rowCount = tableD.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                if (tableD.getItemString(i,
                                         "PACK_CODE").equals(node.getOldValue())) {
                    messageBox("������ϸ��,���ɸ���");
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * ϸ��ֵ�ı��¼�
     * @param obj Object
     * @return boolean
     */
    public boolean onTableDChangeValue(Object obj) {
        //�õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return true;
        //����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
        if (node.getValue().equals(node.getOldValue()))
            return true;
        //�õ�table�ϵ�parmmap������
        String columnName = tableD.getDataStoreColumnName(node.
            getColumn());
        //�������Ÿ���
        if ("QTY".equals(columnName)) {
            int seq = Integer.parseInt("" + node.getValue());
            //����Ϊ��
            if (seq <= 0) {
                messageBox_("��������С��0!");
                return true;
            }
        }
        return false;
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        //����Ƿ������ݱ��
        if (!checkValueChange())
            return;
        //�õ�������Ľڵ����
        TTreeNode node = tree.getSelectionNode();
        if (node == null)
            return;
        //table�������иı�ֵ
        tableM.acceptText();
        //�������������ĸ����
        if (node.getType().equals("Root")) {
            //���ݹ�����������Tablet�ϵ�����
            tableM.setSQL(
                "select * from INV_PACKM WHERE PACK_CODE IS NOT NULL " +
                onFilterM());
            tableM.retrieve();
            tableM.setDSValue();
            return;
        }
        //�õ���ID
        String parentID = node.getID();
        //table�е�datastore�в�ѯ����sql
        tableM.setSQL("select * from INV_PACKM where PACK_CODE like '" +
                      parentID +
                      "%'" + onFilterM());
        //��������
        tableM.retrieve();
        //�������ݵ�table��
        tableM.setDSValue();
        // ����ϸ������
        //���ѡ��
        tableD.clearSelection();
        //���table����������
        tableD.removeRowAll();
        //��ձ����¼
        tableD.getDataStore().resetModify();
    }

    /**
     * �����������
     * @return String
     */
    public String onFilterM() {
        //��������
        String filterStr = "";
        // ����
        String value = getValueString("PACK_CODE");
        if (value.length() > 0)
            filterStr = " AND PACK_CODE like '" + value + "%'";
        //����
        value = getValueString("PACK_DESC");
        if (value.length() > 0)
            filterStr += " AND PACK_DESC like '" + value + "%'";
        //ƴ��
        value = getValueString("PY1");
        if (value.length() > 0)
            filterStr += " AND PY1 like'" + value + "%'";
        //��Ź���
        value = getValueString("SEQ_FLG");
        if (value.length() > 0)
            filterStr += " AND SEQ_FLG ='" + value + "'";
        //��ע
        value = getValueString("DESCRIPTION");
        if (value.length() > 0)
            filterStr += " AND BUYWAY_CODE like'" + value + "%'";
        return filterStr;
    }

    /**
     * ���
     */
    public void onClear() {
        //�������ݱ����ʾ
        if (!checkValueChange())
            return;
        //��չ�������
        clearValue("PACK_CODE;PACK_DESC;PY1;SEQ_FLG;DESCRIPTION;");
        //����
        tableM.acceptText();
        //���ѡ��״̬
        tableM.clearSelection();
        
        tableM.removeRowAll();				//wm2013-05-28���
        //��ձ����¼
        tableM.getDataStore().resetModify();

        //ͬ������ϸ��
        tableD.acceptText();
        //�����ʾ����
        tableD.clearSelection();
        //���table����������
        tableD.removeRowAll();
        //��ձ����¼
        tableD.getDataStore().resetModify();
        //table���ɱ༭
        tableM.setLockColumns("0,2,7,8,9");			//wm2013-05-28		tableM.setLockColumns("0,1,2,3,4,5,6,7");
        //����ѡ�и��ڵ�
        tree.setSelectNode(treeRoot);
        //��������ѡ����
        tableMSelectedRow = -1;
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        //����ѡ�е���
        int tableMSelectRow = tableM.getSelectedRow();
        //ϸ��ѡ�е���
        int tableDSelectRow = tableD.getSelectedRow();
        //���ѡ������������Ŀ
        if (tableMSelectRow >= 0 && tableDSelectRow < 0) {
            onDeleteM(tableMSelectRow, tableM, tableD);
            return;
        }
        //�����ϸ��ɾ��
        if (tableDSelectRow >= 0) {
            tableD.removeRow(tableDSelectRow);
            return;
        }
    }

    /**
     * ɾ����
     * @param row int
     * @param tableM TTable
     * @param tableD TTable
     */
    public void onDeleteM(int row, TTable tableM, TTable tableD) {
        //�õ�Ҫɾ��������������
        String packCode = tableM.getItemString(row, "PACK_CODE").trim();
        //ֻ���������Ų�Ϊ�ղ�ɾ��
        if (packCode != null && packCode.length() != 0) {
            //�õ�ϸ��������
            int rowCount = tableD.getRowCount();
            //ѭ��ɾ��ϸ����
            for (int i = rowCount - 1; i >= 0; i--) {
                //���ϸ���е��������ź�������ͬ��ɾ��
                String packCodeD = tableD.getItemString(i, "PACK_CODE");
                if (packCode.equals(packCodeD))
                    onDeleteD(i, tableD);
            }
        }
        tableM.removeRow(row);
        return;
    }

    /**
     * ɾ����ϸ
     * @param row int
     * @param table TTable
     */
    public void onDeleteD(int row, TTable table) {
        table.removeRow(row);
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
        int count = dataStore.rowCount();
        String s = "";
        for (int i = 0; i < count; i++) {
            String value = dataStore.getItemString(i, columnName);
            if (StringTool.compareTo(s, value) < 0)
                s = value;
        }
        return s;
    }

    /**
     * չ�����ϵĽڵ�
     */
    public void onGetTreeNode() {
        String nodeId = getValueString("NODE").trim();
        if (nodeId == null || nodeId.length() <= 0)
            return;
        treeRoot.getAllowsChildren();
        treeRoot.getChildCount();

    }

    public TTreeNode getNode(TTreeNode node, String str) {

        int count = node.getChildCount();
        if (count <= 0)
            return null;
        for (int i = 0; i < count; i++) {
//            String nodeId=node.getChildAt(0);
//            return node.getChildAt(0);
        }
        return null;
    }

    /**
     * ����
     * @return boolean
     */
    public boolean onUpdate() {
        //ȡϵͳ�¼�
        Timestamp date = SystemTool.getInstance().getDate();
        //�õ�����sql�Ķ���
        TJDODBTool dbTool = new TJDODBTool();
        //�����ı�
        tableM.acceptText();
        TDataStore dateStore = tableM.getDataStore();
        //�õ�����������
        String name = dateStore.isFilter() ? dateStore.FILTER :
            dateStore.PRIMARY;
        int rowsSup[] = dateStore.getModifiedRows(name);
        //���̶�����������
        for (int i = 0; i < rowsSup.length; i++) {
            tableM.setItem(rowsSup[i], "OPT_USER",
                           Operator.getID());
            tableM.setItem(rowsSup[i], "OPT_DATE", date);
            tableM.setItem(rowsSup[i], "OPT_TERM",
                           Operator.getIP());
        }
        //ȡ������sql
        String[] sqlM = tableM.getUpdateSQL();
        //�õ������Ʒ��
        //�����ı�
        tableD.acceptText();
        dateStore = tableD.getDataStore();
        //�õ�ȫ���ݻ�����������
        name = dateStore.isFilter() ? dateStore.FILTER : dateStore.PRIMARY;
        int rowAngent[] = dateStore.getModifiedRows(name);
        //���̶�����������
        for (int i = 0; i < rowAngent.length; i++) {
            tableD.setItem(rowAngent[i], "OPT_USER",
                           Operator.getID());
            tableD.setItem(rowAngent[i], "OPT_DATE", date);
            tableD.setItem(rowAngent[i], "OPT_TERM",
                           Operator.getIP());
        }
        //ȡ��ϸ��sql
        String[] sqlD = tableD.getUpdateSQL();
        //������sql�ϲ�(����Ϊ��)
        if (sqlM != null && sqlD != null)
            sqlM = StringTool.copyArray(sqlM, sqlD);
        //����ģ�ϸ��û��
        if (sqlM == null && sqlD != null)
            sqlM = sqlD;
        //��ϸ��û��
        if (sqlM == null && sqlD == null) {
            messageBox("û��Ҫ���������");
            return true;
        }
        //��������
        TParm result = new TParm(dbTool.update(sqlM));
        if (result.getErrCode() < 0) {
            out("update err " + result.getErrText());
            this.messageBox("����ʧ��!");
            return false;
        }
        messageBox("����ɹ�!");
        //��ձ���ۼ�
        tableD.getDataStore().resetModify();
        tableM.getDataStore().resetModify();
        return true;
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
     * �Ƿ�رմ���
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
     * ������ݱ��
     * @return boolean
     */
    public boolean checkData() {
        //��������Ƿ���
        if (tableM.isModified())
            return true;
        if (tableD.isModified())
            return true;
        return false;
    }

}
