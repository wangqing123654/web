package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTreeNode;
import jdo.sys.SYSRuleTool;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTree;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TMessage;

/**
 *
 * <p>
 * Title: ���ҹ���
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
 * Company: JavaHis
 * </p>
 *
 * @author fudw 2008-12-24
 * @version 1.0
 */
public class SYSDeptControl
    extends TControl {
    /**
     * ����
     */
    private TTreeNode treeRoot;
    /**
     * ��Ź�����𹤾�
     */
    SYSRuleTool ruleTool;
    /**
     * �������ݷ���datastore���ڶ��������ݹ���
     */
    TDataStore treeDataStore = new TDataStore();

    /**
     * ��ʼ��
     */
    public void onInit() { // ��ʼ������
        super.onInit();
        // ��ʼ����
        onInitSelectTree();
        // ��tree��Ӽ����¼�
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        // ��Table��������¼�
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                         "onTableChangeValue");
        // ����
        onCreatTree();
        callFunction("UI|new|setEnabled", false);
        onPageInit();
    }

    /**
     * ��ʼ����
     */
    public void onInitSelectTree() {
        // �õ�����
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        if (treeRoot == null)
            return;
        // �����ڵ����������ʾ
        treeRoot.setText("���ҹ���");
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
     * ��ʼ�����ϵĽڵ�
     */
    public void onCreatTree() {
        // ��dataStore��ֵ
        treeDataStore.setSQL("SELECT RULE_TYPE, CATEGORY_CODE,"
                             + " CATEGORY_CHN_DESC, CATEGORY_ENG_DESC,"
                             + " PY1, PY2, SEQ, DESCRIPTION, DETAIL_FLG,"
                             + " OPT_USER, OPT_DATE, OPT_TERM"
                             + " FROM SYS_CATEGORY WHERE RULE_TYPE='SYS_DEPT'");
        // �����dataStore���õ�������С��0
        if (treeDataStore.retrieve() <= 0)
            return;
        // ��������,�Ǳ�������еĿ�������
        ruleTool = new SYSRuleTool("SYS_DEPT");
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
    public void onTreeClicked(Object parm) { // ������ť������
        callFunction("UI|new|setEnabled", false);
        // �õ�������Ľڵ����
        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        // �õ�table����
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        // table�������иı�ֵ
        table.acceptText();
        // �������������ĸ����
        if (node.getType().equals("Root"))
            // ���ݹ�����������Tablet�ϵ�����
            table.setFilter(getQuerySrc());
        else { // �����Ĳ��Ǹ����

            // �õ���ǰѡ�еĽڵ��idֵ
            String id = node.getID();
            // �õ���������
            String s = getQuerySrc();
            // ������������д�������
            if (s.length() > 0)
                s += " AND ";
            s += "DEPT_CODE like '" + id + "%'";
            // table�е�datastore�й��������������
            table.setFilter(s);
        }
        // ִ��table�Ĺ���
        table.filter();
        // ��table���ݼ���������
        table.setSort("DEPT_CODE");
        // table��������¸�ֵ
        table.sort();
        // �õ���ID
        String parentID = node.getID();
        int classify = 1;
        if (parentID.length() > 0)
            classify = ruleTool.getNumberClass(parentID) + 1;
        // �������С�ڵ�,��������һ��
        if (classify > ruleTool.getClassifyCurrent()) {
            callFunction("UI|new|setEnabled", true);
        }
    }

    /**
     * ����Table�ı�ֵ
     *
     * @param obj
     *            Object
     */

    public boolean onTableChangeValue(Object obj) {
        // �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
        if (node.getValue().equals(node.getOldValue()))
            return false;
        // �õ�table�ϵ�parmmap������
        String columnName = node.getTable().getDataStoreColumnName(
            node.getColumn());
        // �õ���ǰ�ı�������
        String value = "" + node.getValue();
        // ��������Ƹı���ƴ��1�Զ�����,���ҿ������Ʋ���Ϊ��
        if ("DEPT_CHN_DESC".equals(columnName)) {
            if (value.equals("") || value == null) {
                messageBox_("�������Ʋ���Ϊ��!");
                return true;
            }
            String py = TMessage.getPy(value);
            node.getTable().setItem(node.getRow(), "PY1", py);
            return false;
        }
        // ����Ǽ�Ƹı���ƴ��1�Զ�����,���ҿ������Ʋ���Ϊ��
        if ("DEPT_ABS_DESC".equals(columnName)) {
            if (value.equals("") || value == null) {
                messageBox_("���Ҽ�Ʋ���Ϊ��!");
                return true;
            }
        }

        // ����ǿ��Ҵ���ı���Ҫ���м���У��
        if ("DEPT_CODE".equals(columnName)) {
            // ��������������ݳ���
            int length = ruleTool.getTot();
            // ����ı��ĳ��Ȳ����ڱ�������еĳ��ȷ���
            if (length != value.length()) {
                messageBox_("���" + node.getValue() + "���Ȳ�����" + length + "!");
                return true;
            }
            // �Կ��Ҵ�����м��...����������ظ�
            if (node.getTable().getDataStore().exist(
                "DEPT_CODE='" + value + "'")) {
                messageBox_("���" + node.getValue() + "�ظ�!");
                return true;
            }
            // �õ��ϲ����
            String partent = ruleTool.getNumberParent(value);
            // ����ϲ�����Ƿ����
            if (treeRoot.findNodeForID(partent) == null) {
                messageBox_("��ŷ���" + partent + "������!");
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * ��������
     */
    public void onNew() { // �õ�table����
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        // �����ı�
        table.acceptText();
        // ȡtable����
        TDataStore dataStore = table.getDataStore();
        // �ҵ������Ҵ���(dataStore,����)
        String maxCode = getMaxCode(dataStore, "DEPT_CODE");
        // �õ�������
        TTree tree = getTree();
        // �ҵ�ѡ�е����ڵ�
        TTreeNode node = tree.getSelectionNode();
        // ���û��ѡ�еĽڵ�
        if (node == null)
            return;
        // �õ���ID
        String parentID = node.getID();
        int classify = 1;
        // �����������ĸ��ڵ����,�õ���ǰ�������
        if (parentID.length() > 0)
            classify = ruleTool.getNumberClass(parentID) + 1;
        // �������С�ڵ�,��������һ��
        if (classify > ruleTool.getClassifyCurrent()) { // �õ�Ĭ�ϵ��Զ���ӵĿ��Ҵ���
            String no = ruleTool.getNewCode(maxCode, classify);
            // �õ�����ӵ�table�����к�
            int row = table.addRow();
            // ���õ�ǰѡ����Ϊ��ӵ���
            table.setSelectedRow(row);
            // �����Ҵ������Ĭ��ֵ
            table.setItem(row, "DEPT_CODE", parentID + no);
            // Ĭ�Ͽ�������
            table.setItem(row, "DEPT_CHN_DESC", "(�½�����)");
            // Ĭ�Ͽ��Ҽ��
            table.setItem(row, "DEPT_ABS_DESC", "(�½�����)");
            // Ĭ�Ͽ���
            table.setItem(row, "REGION_CODE", Operator.getRegion());
            // Ĭ����С����ע��
            table.setItem(row, "FINAL_FLG", "Y");
            // ˳���
            int seq = Integer.parseInt(getMaxCode(table.getDataStore(), "SEQ"));
            table.setItem(row, "SEQ", seq + 1);
        }
    }

    /**
     * ��շ���
     */
    public void onClear() {
        this.setValue("DEPT_CODE", "");
        this.setValue("DEPT_CHN_DESC", "");
    }

    /**
     * �õ�������
     *
     * @return TTree
     */
    public TTree getTree() {
        return (TTree) callFunction("UI|TREE|getThis");
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
     * ��������
     */
    public boolean onUpdate() {
        Timestamp date = SystemTool.getInstance().getDate();
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        // �����ı�
        table.acceptText();
        // ���ȫ���Ķ����к�
        int rows[] = table.getModifiedRowsFilter();
        // ���̶�����������
        for (int i = 0; i < rows.length; i++) {
            table.setItemFilter(rows[i], "OPT_USER", Operator.getID());
            table.setItemFilter(rows[i], "OPT_DATE", date);
            table.setItemFilter(rows[i], "OPT_TERM", Operator.getIP());
        }
        if (!table.update()) {
            messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        return true;
    }

    /**
     * ��������
     *
     * @return String
     */
    private String getQuerySrc() { // �õ����Ҵ���
        String code = getText("DEPT_CODE");
        // �õ���������
        String desc = getText("DEPT_CHN_DESC");
        String sb = "";
        //===========pangben modify 20110422 start
        if(null!=Operator.getRegion()&&!"".equals(Operator.getRegion()))
            sb= " REGION_CODE='"+Operator.getRegion()+"' ";
        //===========pangben modify 20110422 stop
        // ���������
        if (code != null && code.length() > 0)
            sb += "DEPT_CODE like '" + code + "%'";//======pangben modify 20110422
        if (desc != null && desc.length() > 0) {
            if (sb.length() > 0)
                sb += " AND ";
            sb += "DEPT_CHN_DESC like '" + desc + "%'";
        }
        return sb;
    }

    /**
     * �������ƺͿ��Ҵ���س����¼�
     */
    public void onQuery() {
        onTreeClicked(getTree().getSelectionNode());
    }

    /**
     * �Ƿ�رմ���
     *
     * @return boolean true �ر� false ���ر�
     */
    public boolean onClosing() {
        // ��������ݱ��
        if (CheckChange())
            switch (this.messageBox("��ʾ��Ϣ", "�Ƿ񱣴�", this.YES_NO_CANCEL_OPTION)) {
                // ����
                case 0:
                    if (!onUpdate())
                        return false;
                    break;
                    // ������
                case 1:
                    return true;
                    // ����
                case 2:
                    return false;
            }
        // û�б��������
        return true;

    }

    /**
     * ����Ƿ������ݱ��
     *
     * @return boolean
     */
    public boolean CheckChange() {
        // ������ݱ��
        TTable tableBed = (TTable) callFunction("UI|TABLE|getThis");
        if (tableBed.isModified())
            return true;
        return false;
    }

    /**
     * �õ����ı�� +1
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    public int getMaxSeq(TDataStore dataStore, String columnName,
                         String dbBuffer) {
        if (dataStore == null)
            return 0;
        // ����������
        int count = dataStore.getBuffer(dbBuffer).getCount();
        // ��������
        int max = 0;
        for (int i = 0; i < count; i++) {
            int value = TCM_Transform.getInt(dataStore.getItemData(i,
                columnName, dbBuffer));
            // �������ֵ
            if (max < value) {
                max = value;
                continue;
            }
        }
        // ���ż�1
        max++;
        return max;
    }
    /**
     * ��ʼ������
     */
    public void onPageInit() {
        String s = "";
        //===========pangben modify 20110422 start
        if (null != Operator.getRegion() && !"".equals(Operator.getRegion()))
            s = " REGION_CODE='" + Operator.getRegion() + "' ";
        //===========pangben modify 20110422 stop

        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        table.setFilter(s);
        // ��table���ݼ���������
        table.setSort("DEPT_CODE");
        // table��������¸�ֵ
        table.sort();
        // ִ��table�Ĺ���
        table.filter();
    }
}
