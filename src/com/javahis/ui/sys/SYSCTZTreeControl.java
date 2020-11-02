package com.javahis.ui.sys;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.sys.StatusDetailsTool;
import com.dongyang.ui.TTreeNode;
import jdo.sys.CTZTool;
import com.dongyang.ui.event.TTreeEvent;
import jdo.sys.Operator;
import jdo.sys.FeeCodeOperateTool;
import com.dongyang.ui.TTableNode;
import jdo.sys.ChargeDetailList;

/**
 * <p>Title:�����ϸ </p>
 *
 * <p>Description:�����ϸ </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */
public class SYSCTZTreeControl
    extends TControl {
    TParm data = new TParm();
    TParm Treedata = new TParm();
    int selectrow = -1; //table��ѡ��
    int treeselect = -1; //����ѡ��
    private static final String TREE = "Tree";
    TTreeNode treeRoot;

    /**
     * ��ʼ������
     *  @return TParm
     */
    public void onInit() {
        super.onInit();
       //����ѡ������Ŀ
        addEventListener(TREE + "->" + TTreeEvent.CLICKED, "onTreeClicked");
        callFunction("UI|save|setEnabled", false);
        callFunction("UI|CTZ_CODE|setEnabled", false);
        this.onInitTree();
        callFunction("UI|panelempty|addItem","SYSCTZ","%ROOT%\\config\\bil\\SYSCTZ.x");
    }

    /**
     * ��ѯ����
     * @return TParm
     */
    public void onQuery() {
        this.callFunction("UI|Table|removeRowAll"); //���table
        data = StatusDetailsTool.getInstance().selectalldata();
        this.callFunction("UI|TABLE|setParmValue", data); //��table���
//        this.onClear(); //������շ���
    }

    /**
     * ѭ������Ժ�ڷ��ô���
     */
    public void updatedata(TParm parm) {
        //���ղ����ڵķ��ô���
        int ex = 0;
        //��ѯ���е�����
        TParm hadcode = StatusDetailsTool.getInstance().selectalldata(parm);
        //��ѯȫ��Ժ�ڷ��ô���
        TParm allcode = FeeCodeOperateTool.getInstance().selectalldata();
        //�ж���δ���������
        for (int i = 0; i < allcode.getCount(); i++) {
            String alcode = allcode.getValue("CHARGE_HOSP_CODE", i);
            //�жϵ�ǰalcode��hadcode���Ƿ����
            for (int j = 0; j <= hadcode.getCount(); j++) {
                String excode = hadcode.getValue("CHARGE_HOSP_CODE", j);
                if (alcode.equals(excode)) {
                    ex = 0;
                    break;
                }
                ex = 1;
            }
            if (ex > 0) {
                //�����������
                parm.setData("CHARGE_HOSP_CODE", alcode);
                parm.setData("OWN_RATE", 1);
                parm.setData("DESCRITPION", "");
                parm.setData("SEQ", 0);
                parm.setData("OPT_USER", Operator.getID());
                parm.setData("OPT_TERM", Operator.getIP());
                //���ò������ݷ���
                StatusDetailsTool.getInstance().insertData(parm);
            }
        }
    }
    /**
     * ��ʼ����
     */
    public void onInitTree() {
        out("begin");
        //ȡ�������Ķ���
        treeRoot = (TTreeNode) callMessage("UI|" + TREE + "|getRoot");
        if (treeRoot == null)
            return;
        //����������ʾ������
        treeRoot.setText("��ݷ���");
        //��
        //treeRoot.setGroup("ROOT");
        //��Žڵ�ı�� ���������дROOT
        treeRoot.setID("ROOT");
        //Ϊ�˳�ͼ���õ�
        treeRoot.setType("ROOT");
        //�渽��������
        //treeRoot.setValue("ROOT:GROUP");
        //��������µ�������������
        treeRoot.removeAllChildren();
        //װ���������ݺ�NOOD
        downloadRootTree(treeRoot);
        //����ҳ��
        callMessage("UI|" + TREE + "|update");
        out("end");
    }

    /**
     * ����������
     * @param parentNode TTreeNode
     */
    public void downloadRootTree(TTreeNode parentNode) {
        if (parentNode == null)
            return;
        TParm parm = CTZTool.getInstance().selecTtreeData();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            String id = parm.getValue("CTZ_CODE", i);
            String name = parm.getValue("CTZ_DESC", i);
            //String type = parm.getValue("TYPE",i);
            TTreeNode node = new TTreeNode(name, "CTZ");
            node.setID(id);
            TParm rowParm = new TParm();
            rowParm.setRowData( -1, parm, i);
            node.setData(rowParm);
            parentNode.add(node);
        }
    }

//    /**
//     *���
//     */
//    public void onClear() {
//        //�����������
//        clearValue("CHARGE_HOSP_CODE;OWN_RATE;CTZ_CODE;SEQ;DESCRITPION");
//        callFunction("UI|TABLE|learSelectc", true);
//        selectrow = -1;
//        treeselect = -1;
//        //ɾ�����ܱ��༭
//        callFunction("UI|save|setEnabled", false);
//        callFunction("UI|TABLE|clearSelection");
//    }

    /**
     * ���������
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
//        this.onClear();
        treeselect = 1;
        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        if ("ROOT".equals(node.getType())){
            callFunction("UI|panelempty|addItem", "SYSCTZ",
                         "%ROOT%\\config\\bil\\SYSCTZ.x");
//            callFunction("UI|panelempty|removeItem", "SYSCTZDetails");
            return;
        }
        callFunction("UI|panelempty|addItem", "SYSCTZDetails",
                     "%ROOT%\\config\\bil\\SYSCTZDetails.x");
//        callFunction("UI|panelempty|removeItem", "SYSCTZ");

        TParm ID = new TParm();
        ID.setData("CTZ_CODE", node.getID());
        //�Զ�������ô��벻�ڵ�
        this.updatedata(ID);
        //��ѯϸ��
        this.selectdetail(ID);
        //setValueForParm("UI|CTZ_CODE|setParmValue",ID,0);
        this.callFunction("UI|TABLE|acceptText");
        this.setValue("CTZ_CODE", node.getID());
        //�����Ա���
        callFunction("UI|save|setEnabled", false);
    }

    /**
     * ���������ϸ������ CTZ_CODE
     * @return TParm
     */
    public void selectdetail(TParm parm) {
        //ϸ������
        data = StatusDetailsTool.getInstance().selectalldata(parm);
        this.callFunction("UI|TABLE|setParmValue", data); //��table���
    }

}
