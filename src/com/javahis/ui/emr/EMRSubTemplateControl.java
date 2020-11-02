package com.javahis.ui.emr;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTreeEvent;
import com.sun.awt.AWTUtilities;

/**
 * <p>Title:��ģ�嵯�� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright:Copyright (c) 2014 </p>
 *
 * <p>Company:Bluecore </p>
 *
 * @author whaosoft
 * @version 1.0
 */
public class EMRSubTemplateControl extends TControl {


    private TTree tree;
    private TTreeNode treeNode;
    private TWord word;

    /**
	 * ��ʼ��
	 */
    public void onInit() {
        super.onInit();
        TParm inParm = this.getInputParm();
        tree = (TTree) this.getComponent("TREE");
        treeNode = tree.getRoot();
        word = (TWord) inParm.getData("TWORD");
        // ����ѡ������Ŀ
        addEventListener(tree.getTag() + "->" + TTreeEvent.DOUBLE_CLICKED, "onTreeDoubled");
        this.loadSubTemplate(inParm);
    }

    /**
     * 
     * @param inParm
     */
    private void loadSubTemplate(TParm inParm) {
        TTreeNode root = tree.getRoot();
        root.removeAllChildren();
        TParm rootParam = this.getSubTemplateRootNode();
        treeNode.setText(rootParam.getValue("PHRASE_CODE", 0));
        treeNode.setType("Root");
        treeNode.setID(rootParam.getValue("CLASS_CODE", 0));
        TParm nodesParam = this.getSubTemplateChildrenNodes();
        if (nodesParam == null) return;
        int nodesCount = nodesParam.getCount();
        for (int i = 0; i < nodesCount; i++) {
            TParm temp = nodesParam.getRow(i);
            // ���ݽڵ����������Ƿ�ΪĿ¼�ڵ�
            String noteType = "1";
            // Ҷ�ڵ�(�ǽṹ��Ƭ��)
            if (nodesParam.getValue("LEAF_FLG", i).equals("Y")) {
                noteType = "4";
            }
            // �����½ڵ�
            TTreeNode PhraseClass = new TTreeNode("PHRASECLASS" + i, noteType);
            // ��ERM������Ϣ���õ��ڵ㵱��
            PhraseClass.setText(nodesParam.getValue("PHRASE_CODE", i));
            PhraseClass.setID(nodesParam.getValue("CLASS_CODE", i));
            // ������������
            PhraseClass.setData(temp);
            // ��һ���Ľڵ��������
            if (nodesParam.getValue("PARENT_CLASS_CODE", i).equals("ROOT")) {
                treeNode.addSeq(PhraseClass);
            }
            // ��������Ľڵ������Ӧ�ĸ��ڵ�����
            else {
                if (nodesParam.getValue("PARENT_CLASS_CODE", i).length() != 0) {
                    // ����Ҷ�ڵ㣨Ƭ��ڵ㣩,�ǵ�ǰ���ҵļ��룻
                    // ��Ҷ�ڵ�
                    if (nodesParam.getValue("LEAF_FLG", i).equals("Y")) {
                        // ����Ƭ�����ڵ㣬�����Ƭ���ڴ˲�����
                        if (nodesParam.getValue("MAIN_FLG", i).equals("Y")) {
                            treeNode.findNodeForID(nodesParam.getValue("PARENT_CLASS_CODE", i))
                                    .add(PhraseClass);
                        }
                        //
                    } else {
                        treeNode.findNodeForID(nodesParam.getValue("PARENT_CLASS_CODE", i))
                                .add(PhraseClass);
                    }
                }
            }
        }
        tree.update();
    }

    /**
     * �õ���ģ����ڵ�����
     * @return
     */
    private TParm getSubTemplateRootNode() {
        String sql =
                "SELECT CLASS_CODE,PHRASE_CODE,LEAF_FLG,MAIN_FLG,PARENT_CLASS_CODE,FILE_PATH,FILE_NAME "
                        + " FROM OPD_COMTEMPLATE_PHRASE WHERE PARENT_CLASS_CODE IS NULL";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * �õ���ģ���ӽڵ�����
     * @return
     */
    private TParm getSubTemplateChildrenNodes() {
        String sql =
                "SELECT CLASS_CODE,PHRASE_CODE,LEAF_FLG,MAIN_FLG,PARENT_CLASS_CODE,FILE_PATH,FILE_NAME "
                        + " FROM OPD_COMTEMPLATE_PHRASE ORDER BY CLASS_CODE,SEQ";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * �����
     * 
     * @param parm
     *            Object
     */
    public void onTreeDoubled(Object parm) {
        TTreeNode node = (TTreeNode) parm;
        if (null == node) {
            return;
        }
        if ("4".equals(node.getType())) {
            TParm dataParm = (TParm) node.getData();
            TParm param = new TParm();
            param.setData("NODE", dataParm);
            param.setData("TWORD", word);
            param.addListener("onReturnContent", this, "onReturnContent");
            TWindow window = (TWindow) openWindow("%ROOT%\\config\\emr\\EMRSubTemplatePreview.x", param, true);
            window.setBounds(290,135,601,351);
            window.setVisible(true);
            AWTUtilities.setWindowOpacity(window, 0.7f);
        }
    }

}
