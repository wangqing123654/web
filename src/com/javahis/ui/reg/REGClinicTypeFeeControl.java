package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTreeNode;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTreeEvent;
import jdo.reg.PanelTypeTool;
import jdo.reg.PanelTypeFeeTool;
import com.dongyang.ui.TTree;
/**
 *
 * <p>Title:�ű�����ܿ����� </p>
 *
 * <p>Description:�ű�����ܿ����� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.16
 * @version 1.0
 */
public class REGClinicTypeFeeControl
    extends TControl {
    public static String TREE = "Tree";
    TTreeNode treeRoot;
    TParm treedata;
    //TParm TABLETYPEFEEDATA = new TParm();
    TParm TABLETYPEDATA = new TParm();
    public void onInit() {
        super.onInit();
        //��ʼ����
        onInitTree();
        //����ѡ������Ŀ
        addEventListener(TREE + "->" + TTreeEvent.CLICKED, "onTreeClicked");
        callFunction("UI|TypeFeePanel|addItem", "REGPanelType",
             "%ROOT%\\config\\reg\\REGPanelType.x");

//        //�ñ����ɾ����Ϊ��
//        callFunction("UI|save|setEnabled", false);
//        callFunction("UI|delete|setEnabled", false);

    }

    /**
     * ��ʼ����
     */
    public void onInitTree() {
        out("begin");
        treeRoot = (TTreeNode) callMessage("UI|" + TREE + "|getRoot");
        if (treeRoot == null)
            return;
        //----------------------//
        treeRoot.setText("�ű����");
        treeRoot.setID("ROOT");
        treeRoot.setType("ROOT");
        treeRoot.removeAllChildren();
        initClinicType(treeRoot);
        //----------------------//
        callMessage("UI|" + TREE + "|update");
        out("end");
    }

    /**
     * �����
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        if ("ROOT".equals(node.getType())) {
            onInitTree();
//        callFunction("UI|DIAPANEL|addItem", node.getName(),"%ROOT%\\config\\opd\\"+node.getName());
            callFunction("UI|TypeFeePanel|addItem", "REGPanelType",
                         "%ROOT%\\config\\reg\\REGPanelType.x");
            return;
        }
        if ("CLINICTYPE".equals(node.getType())) {
            String id = node.getID();
            String admType = node.getName();
            callFunction("UI|TypeFeePanel|addItem", "REGPanelTypeFee",
                         "%ROOT%\\config\\reg\\REGPanelTypeFee.x",new String[]{id,admType});
            //typeNodeClick(id);
           return;
        }
        //if ("CLINICTYPEFEE".equals(node.getType())) {
            //selecttypefeefortable(node.getID());
        //    return;
        //}

    }

    /**
     * ��ѯ��ǰ�ű𣬸��ű����table��ֵ��Tree��
     * @param clinicTypeCode String
     */
    public void typeNodeClick(String clinicTypeCode) {
        //��ʾ��ǰ�ű� table ����
        TParm TABLETYPEFEEDATA = PanelTypeFeeTool.getInstance().selectdata(clinicTypeCode);
        if (TABLETYPEFEEDATA.getErrCode() < 0) {
            messageBox(TABLETYPEFEEDATA.getErrText());
            return;
        }
        this.callFunction("UI|TABLETYPEFEE|setParmValue", TABLETYPEFEEDATA);

    }
//    /**
//     * ��ѯ��ǰ�ű𣬸��ű����table��ֵ��Table��
//     * @param clinicTypeCode String
//     */
//    public void selecttypefeefortable(String clinicTypeCode) {
//        //��ʾ��ǰ�ű� table ����
//        TParm TABLETYPEFEEDATA = PanelTypeFeeTool.getInstance().queryTree(clinicTypeCode);
//        if (TABLETYPEFEEDATA.getErrCode() < 0) {
//            messageBox(TABLETYPEFEEDATA.getErrText());
//            return;
//        }
//        if (TABLETYPEFEEDATA.getCount() == 0) {
//            //��ʾ��ʾ��Ϣ���������ϡ�
//            this.messageBox("E0008");
//            return;
//        }
//        setTextForParm(
//            "ADM_TYPE;CLINICTYPE_CODE;ORDER_CODE;RECEIPT_TYPE",
//            TABLETYPEFEEDATA, 0);
//
//
//    }

    /**
     * װ�غű�
     * @param parentNode TTreeNode
     */
    public void initClinicType(TTreeNode parentNode) {
        TABLETYPEDATA = PanelTypeTool.getInstance().queryTree();
        if (TABLETYPEDATA == null)
            return;
        for (int i = 0; i < TABLETYPEDATA.getCount(); i++) {
            String id = TABLETYPEDATA.getValue("CLINICTYPE_CODE", i);
            String name = TABLETYPEDATA.getValue("CLINICTYPE_DESC", i);
            String admType = TABLETYPEDATA.getValue("ADM_TYPE", i);
            String type = "CLINICTYPE";
            TTreeNode node = new TTreeNode(name, type);
            node.setName(admType);
            node.setID(id);
            parentNode.add(node);
            //initClinicTypeFee(node, id);
        }
    }
    /**
     * װ�غű����
     * @param parentNode TTreeNode
     * @param clinicTypeCode String
     */
    public void initClinicTypeFee(TTreeNode parentNode, String clinicTypeCode) {
        TParm parm = PanelTypeFeeTool.getInstance().queryTree(clinicTypeCode);

        if (parm == null) {
            this.messageBox("non");
            return;
        }

        for (int i = 0; i < parm.getCount(); i++) {
            String id = parm.getValue("CLINICTYPE_CODE", i);
            String name = parm.getValue("ORDER_CODE", i);
            String type = "CLINICTYPEFEE";
            TTreeNode node = new TTreeNode(name, type);
            node.setID(id);
            parentNode.add(node);
        }

    }
    /**
     * ͬ��������(����)
     * @param id String
     * @param name String
     */
    public void addNode(String id,String name) {
        //�õ�������
        TTree tree = (TTree) callFunction("UI|TREE|getThis");
        //�ҵ����ڵ�
        TTreeNode root = tree.getRoot();
        //������Ϊ��
        if (root == null)
            return;
        //�������ֽڵ�
        String type = "CLINICTYPE";
        //�½�һ�����
        TTreeNode nodeNew = new TTreeNode(name, type);
        //����㸳����
        nodeNew.setText(name);
        //��id
        nodeNew.setID(id);
        //��ӵ�root��
        root.add(nodeNew);
        //ˢ����
        tree.update();

    }
    /**
     * ͬ��������(ɾ��)
     * @param id String
     */
    public void deleteNode(String id){
        //�õ�������
        TTree tree = (TTree) callFunction("UI|TREE|getThis");
        //�ҵ����ڵ�
        TTreeNode root = tree.getRoot();
        //ɾ�����ϵĽڵ�
        TTreeNode treenodeChild = root.findNodeForID(id);
        if(treenodeChild != null)
            root.remove(treenodeChild);
        //ˢ����
        tree.update();
    }
}
