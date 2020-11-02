package com.javahis.ui.emr;


import java.awt.event.KeyEvent;
import javax.swing.tree.TreeNode;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.javahis.util.StringUtil;
import jdo.emr.EMRClassTool;

/**
 * 
 * <p>
 * Title: EMR_CLASS ����ѡ���
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author wanglong 2014.8.15
 * @version 1.0
 */
public class EMRClassPopupControl
        extends TControl {

    private TTree tree;
    private TTreeNode treeRoot;
    private String oldText = "";
    private boolean letterKeyFlg=true;//��ĸ�������
    
    // ��������ҽ�����ͣ�ӦȫΪ����ҽ��������LIS/RIS/OTH�Ȳ���
    private String hrmType;

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        tree = (TTree) callMessage("UI|TREE|getThis");
        callFunction("UI|EDIT|addEventListener", "EDIT->" + TKeyListener.KEY_PRESSED, this,
                     "onKeyPressed");
        callFunction("UI|EDIT|addEventListener", TTextFieldEvent.KEY_RELEASED, this,
                     "onKeyReleased");
//        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
//        // ����ѡ������Ŀ
        addEventListener("TREE->" + TTreeEvent.DOUBLE_CLICKED, "onTreeDoubleClicked");
        Object obj = getParameter();
        if (obj == null) return;
        if (!(obj instanceof TParm)) return;
        TParm parm = (TParm) obj;
        System.out.println("-------------���------parm-------" + parm);
        String text = parm.getValue("TEXT");
        setEditText(text);
        hrmType = parm.getValue("HRM_TYPE");
        onInitTree();
    }

    /**
     * ���¼���
     */
    public void onInitReset() {
        Object obj = getParameter();
        if (obj == null) return;
        if (!(obj instanceof TParm)) return;
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        String oldText = (String) callFunction("UI|EDIT|getText");
        if (oldText.equals(text)) {
            return;
        }
        setEditText(text);
        onInitTree();
    }

    /**
     * ��ʼ����
     */
    public void onInitTree() {
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");// �õ�����
        if (treeRoot == null) return;
        // ȡ���ڵ�;
        TParm rootParm = this.getTreeRoot();
        treeRoot.setText(rootParm.getValue("CLASS_DESC", 0));// �����ڵ����������ʾ
        treeRoot.setType("Root");// �����ڵ㸳tag
        treeRoot.setID(rootParm.getValue("CLASS_CODE", 0));
        treeRoot.removeAllChildren();// ������нڵ������
        // ��ʼ��ʼ����������
        TParm result = this.getTreeChildren("");
        int count = result.getCount();
        for (int i = 0; i < count; i++) {
            TParm dataParm = result.getRow(i);
            putNodeInTree(dataParm, treeRoot);
        }
        tree.update();// ������
        tree.setSelectNode(treeRoot);// ��������Ĭ��ѡ�нڵ�
    }

    /**
     * ���ӽڵ���뵽����
     * 
     * @param childParm
     *            TParm
     * @param root
     *            TTreeNode
     */
    private void putNodeInTree(TParm childParm, TTreeNode root) {
        String noteType = "Path"; // UI
        if (childParm.getValue("LEAF_FLG").equals("Y")) {
            noteType = "Leaf"; // UI
        }
        TTreeNode node = new TTreeNode("EMRCLASS", noteType);
        node.setText(childParm.getValue("CLASS_DESC"));
        node.setID(childParm.getValue("CLASS_CODE"));
        String parentID = childParm.getValue("PARENT_CLASS_CODE");
        // System.out.println("parentID-------------:" + parentID);
        if (root.findNodeForID(childParm.getValue("CLASS_CODE")) != null) {
            // System.out.println("�Ѿ����д˽ڵ㲻��ִ����Ӳ���");
        } else if (root.findNodeForID(parentID) != null) {
            root.findNodeForID(parentID).add(node);
        } else {
            TParm parentIDParm = new TParm();
            parentIDParm.setData("CLASS_CODE", parentID);
            TParm parentParm = EMRClassTool.getNewInstance().onQuery(parentIDParm);
            // System.out.println("��ѯ���ĸ��ڵ�:" + resultParm);
            if (parentParm.getCount() <= 0) {
                root.add(node);
            } else {
                putNodeInTree(parentParm.getRow(0), root);
                root.findNodeForID(parentID).add(node);
            }
        }
    }

    /**
     * �������¼�
     * 
     * @param parm
     */
    public void onTreeClicked(Object parm) {
        TTreeNode node = (TTreeNode) parm;
        letterKeyFlg = false;
        if (node == null) return;
        if (!node.isNodeChild(node)) {
            for (int i = 0; i < node.getChildCount(); i++) {
                TreeNode childNode = node.getChildAt(i);
                if (childNode.isLeaf() && childNode instanceof TTreeNode) {
                    TTreeNode tChildNode = (TTreeNode) childNode;
                    tChildNode.setValue(node.getValue());
                }
            }
        }
    }

    /**
     * ����ѡ�����¼�
     * 
     * @param obj
     *            Object
     */
    public void onTreeDoubleClicked(Object obj) {
        if (obj == null) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("KEYINPUT_FLG", false);
        parm.setData("CLASS_CODE", tree.getSelectNode().getID());
        setReturnValue(parm);
        callFunction("UI|setVisible", false);
    }

    /**
     * ������������
     * 
     * @param s
     *            String
     */
    public void setEditText(String s) {
        callFunction("UI|EDIT|setText", s);
        int x = s.length();
        callFunction("UI|EDIT|select", x, x);
        onKeyReleased(s);
    }

    /**
     * �����ͷ��¼�
     * 
     * @param s
     *            String
     */
    public void onKeyReleased(String s) {
        s = s.toUpperCase();
        if (oldText.equals(s)) return;
        oldText = s;
        treeRoot.removeAllChildren();// ������нڵ������
        TParm childrenParm = this.getTreeChildren(s);
        for (int i = 0; i < childrenParm.getCount(); i++) {
            TParm childParm = childrenParm.getRow(i);
            putNodeInTree(childParm, treeRoot);
        }
        tree.update();// ������

    }

    /**
     * �����¼�
     * 
     * @param e
     *            KeyEvent
     */
    public void onKeyPressed(KeyEvent e) {
        TTreeNode node = tree.getSelectNode();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                this.callFunction("UI|setVisible", false);
                letterKeyFlg = false;
                break;
            case KeyEvent.VK_UP:
                tree.getTree().setSelectionRow(tree.getTree().getRowForNode(node) - 1);
                tree.getTree().scrollRowToVisible(tree.getTree().getRowForNode(node) - 1);
                letterKeyFlg = false;
                break;
            case KeyEvent.VK_DOWN:
                tree.getTree().setSelectionRow(tree.getTree().getRowForNode(node) + 1);
                tree.getTree().scrollRowToVisible(tree.getTree().getRowForNode(node) + 1);
                letterKeyFlg = false;
                break;
            case KeyEvent.VK_LEFT:
                tree.getTree().collapseRow(tree.getTree().getRowForNode(node));
                letterKeyFlg = false;
                break;
            case KeyEvent.VK_RIGHT:
                tree.getTree().expandRow(tree.getTree().getRowForNode(node));
                letterKeyFlg = false;
                break;
            case KeyEvent.VK_ENTER:
                callFunction("UI|setVisible", false);
                TParm parm = new TParm();
                if (letterKeyFlg) {
                    parm.setData("KEYINPUT_FLG", true);//���̼���ע�ǣ�false��ʾ������ѡ�������CLASS_CODE
                    parm.setData("CLASS_CODE", this.getValueString("EDIT").trim());
                } else {
                    parm.setData("KEYINPUT_FLG", false);
                    parm.setData("CLASS_CODE", tree.getSelectNode().getID());
                }
                setReturnValue(parm);
                break;
            default:
                letterKeyFlg = true;
        }
    }


    /**
     * ��ǰ�������ƶ�Ӧ�Ľڵ�
     * 
     * @return TParm
     */
    public TParm getTreeChildren(String letters) {
        String sql =
                "SELECT CLASS_CODE, CLASS_DESC, ENG_DESC, PY1, PY2, DESCRIPTION, "
                        + "SEQ, CLASS_STYLE, LEAF_FLG, PARENT_CLASS_CODE "
                        // + " FROM EMR_CLASS WHERE LEAF_FLG='N' ";
                        + " FROM EMR_CLASS WHERE 1=1  #  ORDER BY CLASS_CODE,PARENT_CLASS_CODE,SEQ";
        String classSql="";
        if (!StringUtil.isNullString(letters)) {
            letters = letters.toUpperCase();
            classSql =
                    sql.replaceFirst("#",
                                     "AND (CLASS_CODE LIKE '@%' OR CLASS_DESC LIKE '@%' OR ENG_DESC LIKE '@%' OR PY1 LIKE '@%' OR PY2 LIKE '@%')");
            classSql = classSql.replaceAll("@", letters);
        } else {
            classSql = sql.replaceFirst("#", "");
        }
//        System.out.println("===================" + classSql);
        TParm result = new TParm(TJDODBTool.getInstance().select(classSql));
        if(result.getCount()<=0){
            result = new TParm(TJDODBTool.getInstance().select(sql.replaceFirst("#", "")));
        }
         
        return result;
    }

    /**
     * ���ڵ�
     * 
     * @return TParm
     */
    public TParm getTreeRoot() {
        String sql =
                "SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE,SEQ FROM EMR_CLASS WHERE PARENT_CLASS_CODE IS NULL";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
}
