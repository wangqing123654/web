package com.javahis.ui.aci;

import java.sql.Timestamp;
import com.dongyang.control.TControl;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTree;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;
import jdo.sys.Operator;
import jdo.sys.SYSRuleTool;
import jdo.sys.SystemTool;


/**
 *
 * <p> Title: 不良事件类型字典维护 </p>
 *
 * <p> Description: 不良事件类型字典维护 </p>
 *
 * <p> Copyright: Copyright (c) 2014 </p>
 *
 * <p> Company: BlueCore </p>
 *
 * @author wanglong 2014.02.20
 * @version 1.0
 */
public class ACIBadEventTypeControl extends TControl {
    private TTree tree;//树
    private TTreeNode treeRoot;// 树根
    private TTable table;
    private SYSRuleTool ruleTool;//编号规则类别工具
    private TDataStore dataStore;

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        tree = (TTree) callFunction("UI|TREE|getThis");
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        table = (TTable) callFunction("UI|TABLE|getThis");
        dataStore = table.getDataStore();
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE, "onTableChangeValue");
        onInitTree();
        onCreatTree();
    }

    /**
     * 初始化树
     */
    public void onInitTree() {
        if (treeRoot == null) {
            return;
        }
        treeRoot.setText("异常事件类型");
        treeRoot.setType("Root");
        treeRoot.setID("");
        treeRoot.removeAllChildren();
        callMessage("UI|TREE|update");
    }

    /**
     * 初始化树节点
     */
    public void onCreatTree() {
        ruleTool = new SYSRuleTool("ACI_BADEVENT");// 编码规则
        if (ruleTool.isLoad()) {
            TTreeNode node[] =
                    ruleTool.getTreeNode(dataStore, "TYPE_CODE", "TYPE_DESC", "Path", "SEQ");
            for (int i = 0; i < node.length; i++) { // 安插节点
                treeRoot.addSeq(node[i]);
            }
            int length = ruleTool.getClassNumber(ruleTool.getClassifyCurrent());
            setTreeNodeIcon(treeRoot, length);
        }
        tree.update();// 更新树
        tree.setSelectNode(treeRoot); // 默认选中树根
        onTreeClicked(treeRoot);
    }

    /**
     * 设置叶子节点的图标（只设置指定编码长度的节点图标）
     * @param treeRoot TTreeNode
     * @param length int
     */
    public void setTreeNodeIcon(TTreeNode treeRoot, int length) {//add by wanglong 20140221
        if (!treeRoot.isLeaf()) {
            for (int i = 0; i < treeRoot.getChildCount(); i++) {
                setTreeNodeIcon((TTreeNode) treeRoot.getChildAt(i), length);
            }
        } else {
            if (treeRoot.getID().length() == length) {
                treeRoot.setType("Table");
            }
        }
    }
    
    /**
     * 树单击事件
     * @param obj Object
     */
    public void onTreeClicked(Object obj) {
        TTreeNode node = (TTreeNode) obj;
        if (node == null) {
            return;
        }
        table.acceptText();
        if (node.getType().equals("Root")) {
            table.setFilter(" LENGTH(TYPE_CODE)=" + ruleTool.getClassify(0));
        } else {
            String id = node.getID();
            int length = ruleTool.getClassNumber(ruleTool.getNumberClass(id) + 1);
            table.setFilter(" LENGTH(TYPE_CODE)=" + length + " AND TYPE_CODE like '" + id + "%' ");
        }
        table.filter();
        table.setSort("SEQ");
        table.sort();
    }

    /**
     * Table单元格值改变事件
     * 
     * @param obj
     *            Object
     * @return boolean
     */
    public boolean onTableChangeValue(Object obj) {
        TTableNode node = (TTableNode) obj;
        if (node == null) {
            return false;
        }
        String newValue = (String) node.getValue();
        String oldValue = (String) node.getOldValue();
        if (newValue.equals(oldValue)) {
            return false;
        }
        int row = node.getRow();
        String colName = table.getDataStoreColumnName(node.getColumn());
        String typeCode = table.getItemString(row, "TYPE_CODE");
        TTreeNode treeNode = tree.getSelectionNode();
        if (treeNode == null) {
            return false;
        }
        if ("TYPE_DESC".equals(colName)) {
            TTreeNode treenode1 = treeNode.findNodeForID(typeCode);
            if (treenode1 != null) {
                treenode1.setText(newValue);
                tree.update();
            }
            String py = TMessage.getPy(newValue);
            table.setItem(node.getRow(), "PY", py);
            return false;
        }
        if ("TYPE_CODE".equals(colName)) {
            String id = treeNode.getID();
            if ((id.length() > 0) && !newValue.startsWith(id)) {
                this.messageBox("编号" + newValue + "必须包含上级编号" + id);
                return true;
            }
            int classify = 1;
            if (id.length() > 0) classify = ruleTool.getNumberClass(id) + 1;
            int length = ruleTool.getClassNumber(classify);
            if (newValue.length() != length) { // 效验编号长度
                this.messageBox("编号" + newValue + "长度必须等于" + length);
                return true;
            }
            if (!checkDuplicate(newValue, node.getRow())) {// 查重
                this.messageBox("编号" + newValue + "重复");
                return true;
            }
            TTreeNode treenode1 = treeNode.findNodeForID(oldValue);
            if (treenode1 != null) {
                treenode1.setID(newValue);
            }
            int count = dataStore.rowCountFilter();
            for (int i = 0; i < count; i++) {
                String code =
                        TCM_Transform.getString(dataStore.getItemData(i, "TYPE_CODE",
                                                                      TDataStore.FILTER));
                if (code.startsWith(oldValue)) {
                    String v = code.substring(newValue.length());
                    dataStore.setItem(i, "TYPE_CODE", newValue + v, TDataStore.FILTER);
                    treenode1 = treeNode.findNodeForID(code);
                    if (treenode1 != null) {
                        treenode1.setID(newValue + v);
                    }
                }
            }
            return false;
        }
        if ("SEQ".equals(colName)) {
            TTreeNode treenode1 = treeNode.findNodeForID(typeCode);
            if (treenode1 != null) {
                treenode1.setSeq(TCM_Transform.getInt(newValue));
                treeNode.remove(treenode1);
                treeNode.addSeq(treenode1);
                tree.update();
            }
            return false;
        }
        return false;
    }

    /**
     * 验证编号是否重复
     * 
     * @param code String
     * @param row int
     * @return boolean
     */
    public boolean checkDuplicate(String code, int row) {
        int count = dataStore.rowCount();
        for (int i = 0; i < count; i++) {
            if (i == row) {
                continue;
            }
            if (dataStore.getItemString(i, "TYPE_CODE").equals(code)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 保存
     * 
     * @return boolean true成功 false失败
     */
    public boolean onUpdate() {
        table.acceptText();
        if (!table.update()) {
            messageBox("保存失败");
            return false;
        }
        messageBox("保存成功");
        return true;
    }

    /**
     * 新增
     */
    public void onNew() {
        table.acceptText();
        TTreeNode node = tree.getSelectionNode();
        if (node == null) {
            return;
        }
        String maxCode = getMaxCode(dataStore, "TYPE_CODE");
        String id = node.getID();
        
        int classify = 1;
        if (id.length() > 0) {
            classify = ruleTool.getNumberClass(id) + 1;
        }
        if (classify > ruleTool.getClassifyCurrent()) {
            this.messageBox("超过最大层数,无法新增");
            return;
        }
        String nextCode = ruleTool.getNewCodeByString(maxCode, classify);
        int row = table.addRow();
        table.setSelectedRow(row);
        table.setItem(row, "TYPE_CODE", id + nextCode);
        table.setItem(row, "TYPE_DESC", "(新建分类)");
        int seq = getMaxSeq(dataStore, "SEQ");
        table.setItem(row, "SEQ", seq);
        Timestamp now = SystemTool.getInstance().getDate();
        table.setItem(row, "OPT_USER", Operator.getID());
        table.setItem(row, "OPT_DATE", now);
        table.setItem(row, "OPT_TERM", Operator.getIP());
        if (classify == ruleTool.getClassifyCurrent()) {
            table.setItem(row, "FINAL_FLG", "Y");
        } else {
            table.setItem(row, "FINAL_FLG", "N");
        }
        TTreeNode newNode = new TTreeNode("(新建分类)", "Path");
        newNode.setID(id + nextCode);
        newNode.setSeq(seq);
        setTreeNodeIcon(newNode, ruleTool.getClassNumber(ruleTool.getClassifyCurrent()));
        node.addSeq(newNode);
        tree.update();
    }

    /**
     * 得到最大的编号
     * 
     * @param ds TDataStore
     * @param colName String
     * @return String
     */
    public String getMaxCode(TDataStore ds, String colName) {
        if (ds == null) {
            return "";
        }
        int count = ds.rowCount();
        String s = "";
        for (int i = 0; i < count; i++) {
            String value = ds.getItemString(i, colName);
            if (StringTool.compareTo(s, value) < 0) s = value;
        }
        return s;
    }

    /**
     * 删除
     */
    public void onDelete() {
        table.acceptText();
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        String typeCode = table.getItemString(row, "TYPE_CODE");
        TTreeNode treeNode = tree.getSelectionNode();
        int count = dataStore.rowCountFilter();
        table.removeRow(row);
        TTreeNode treenode1 = treeNode.findNodeForID(typeCode);
        if (treenode1 != null) {
            treeNode.remove(treenode1);
        }
        for (int i = count - 1; i >= 0; i--) {
            String id =
                    TCM_Transform.getString(dataStore
                            .getItemData(i, "TYPE_CODE", TDataStore.FILTER));
            if (id.startsWith(typeCode)) dataStore.deleteRow(i, TDataStore.FILTER);
        }
        tree.update();
    }

    /**
     * 是否关闭窗口
     * 
     * @return boolean true 关闭 false 不关闭
     */
    public boolean onClosing() {
        table.acceptText();
        if (table.getModifiedRows().length == 0 && table.getModifiedRowsFilter().length == 0) {
            return true;
        }
        switch (this.messageBox("提示信息", "是否保存?", YES_NO_CANCEL_OPTION)) {
            case 0: // 保存
                if (onUpdate()) {
                    return true;
                }
                break;
            case 1: // 不保存
                return true;
        }
        return false;
    }

    /**
     * 得到最大的序号
     * 
     * @param ds TDataStore
     * @param colName String
     * @return String
     */
    public int getMaxSeq(TDataStore ds, String colName) {
        if (ds == null) {
            return 0;
        }
        int count = ds.rowCount();
        int s = 0;
        for (int i = 0; i < count; i++) {
            int value = ds.getItemInt(i, colName);
            if (s < value) {
                s = value;
                continue;
            }
        }
        s++;
        return s;
    }
}
