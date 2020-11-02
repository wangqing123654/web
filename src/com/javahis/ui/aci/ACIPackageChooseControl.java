package com.javahis.ui.aci;

import javax.swing.tree.TreeNode;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.event.TTreeEvent;

/**
 * <p> Title: �ײ���Աѡ�� </p>
 *
 * <p> Description: �ײ���Աѡ�� </p>
 *
 * <p> Copyright: Copyright (c) 2014 </p>
 *
 * <p> Company: BlueCore </p>
 *
 * @author WangLong 2014.01.07
 * @version 1.0
 */
public class ACIPackageChooseControl
        extends TControl {

    private TTreeNode treeRoot;

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        onInitTree();
    }

    /**
     * ���¼�������
     */
    public void onInitReset() {
        onInitTree();
    }

    /**
     * ��ʼ����
     */
    public void onInitTree() {
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");// �õ�����
        if (treeRoot == null) return;
        treeRoot.setText("�ײ�");// �����ڵ����������ʾ
        treeRoot.setType("Root");// �����ڵ㸳tag
        treeRoot.removeAllChildren();// ������нڵ������
        String sql =
                "SELECT PACKAGE_CODE, PACKAGE_DESC FROM ACI_SMSPACKAGEM WHERE ACTIVE_FLG = 'Y'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode()<0||result.getCount() <= 0) {
            return;
        }
        for (int i = 0; i < result.getCount(); i++) {
            String packageCode = result.getValue("PACKAGE_CODE", i);
            TTreeNode node = new TTreeNode(result.getValue("PACKAGE_DESC", i));
            node.setType("Path");
            node.setShowType("checkbox");// ���ýڵ�Ϊcheckbox���
            node.setData(packageCode);
            treeRoot.add(node);
            initChildrenNode(node, packageCode);// ��ʼ�����ӽڵ�
        }
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        tree.update();// ������
        tree.setSelectNode(treeRoot);// ��������Ĭ��ѡ�нڵ�
    }

    /**
     * ��ʼ�����ӽڵ�
     * @param parentNode
     * @param packageCode
     */
    public void initChildrenNode(TTreeNode parentNode, String packageCode) {
        if (parentNode == null) return;
        String sql =
                "SELECT A.USER_ID, B.USER_NAME, A.TEL FROM ACI_SMSPACKAGED A, SYS_OPERATOR B "
                        + " WHERE A.USER_ID = B.USER_ID AND A.PACKAGE_CODE = '#'";
        sql = sql.replaceFirst("#", packageCode);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0 || result.getCount() <= 0) {
            return;
        }
        for (int i = 0; i < result.getCount(); i++) {
            TTreeNode node = new TTreeNode(result.getValue("USER_NAME", i));
            node.setShowType("checkbox");
            node.setID (result.getValue("USER_ID", i));
            node.setData (result.getValue("TEL", i));
            parentNode.add(node);
        }
    }

   /**
    * �������¼�
    * @param parm
    */
    public void onTreeClicked(Object parm) {
        TTreeNode node = (TTreeNode) parm;
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
     * ����
     */
    public void onReturn() {
        this.closeWindow();
    }
    
    /**
    * ���ڹر��¼�
    * @return boolean
    */
   public boolean onClosing(){
       this.setReturnValue(getData());
       return true;
   }
   
    /**
     * ����û�ѡ������
     * @return
     */
    public TParm getData(){
        TParm parm = new TParm();
        if (!treeRoot.isNodeChild(treeRoot)) {
            for (int i = 0; i < treeRoot.getChildCount(); i++) {
                TreeNode childNode = treeRoot.getChildAt(i);
                if (!childNode.isLeaf()) {//�Ƿ�ΪҶ�ӽڵ�
                    for (int j = 0; j < childNode.getChildCount(); j++) {
                        TreeNode grandNode = childNode.getChildAt(j);
                        if (grandNode.isLeaf() && grandNode instanceof TTreeNode) {
                            TTreeNode tGrandNode = (TTreeNode) grandNode;
                            if (TCM_Transform.getBoolean(tGrandNode.getValue())) {
                                parm.addData("USER_ID", tGrandNode.getID());
                                parm.addData("USER_NAME", tGrandNode.getText());
                                parm.addData("TEL", tGrandNode.getData());
                            }
                        }
                    }
                }
            }
        }
        parm.setCount(parm.getCount("USER_ID"));
        return parm;
    }

}
