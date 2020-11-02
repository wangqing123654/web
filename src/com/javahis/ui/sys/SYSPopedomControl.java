package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTextField;
import jdo.sys.DictionaryTool;
import com.dongyang.data.TParm;
import com.dongyang.util.TMessage;
import jdo.sys.Operator;
import jdo.sys.SYSDictionaryTool;
import com.dongyang.manager.TIOM_AppServer;
import action.sys.SYSPopedomAction;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SYSPopedomControl
    extends TControl {

    /**
     * ����
     */
    private TTreeNode treeRoot;
    /**
     * �ϼ����
     */
    private String parentID;
    /**
     * �ϼ�����
     */
    private String groupID;

    private TTable table_m;

    private TTable table_d;

    private String action;

    public SYSPopedomControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        //��ʼ����
        onInitTree();
        //����ѡ������Ŀ
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        table_m = this.getTable("TABLE_M");
        table_d = this.getTable("TABLE_D");
        onClear();
    }

    public void onSave() {
        TParm parmM = new TParm();
        parmM.setData("GROUP_ID", parentID);
        parmM.setData("ID", this.getValueString("PRG_ID"));
        parmM.setData("NAME", this.getValueString("PRG_NAME"));
        parmM.setData("ENG_DESC", "");
        parmM.setData("PY1", this.getValueString("PRG_PY1"));
        parmM.setData("PY2", this.getValueString("PRG_PY2"));
        parmM.setData("SEQ", this.getValue("PRG_SEQ"));
        parmM.setData("DESCRIPTION", this.getValueString("PRG_DESCRIPTION"));
        parmM.setData("TYPE", "PRG");
        parmM.setData("OPT_USER", Operator.getID());
        parmM.setData("OPT_TERM", Operator.getIP());
        parmM.setData("PARENT_ID", "");
        parmM.setData("STATE", this.getValue("PRG_STATE"));
        parmM.setData("DATA", this.getValue("PRG_DATA"));

        int row_m = table_m.getSelectedRow();
        int row_d = table_d.getSelectedRow();
        TParm parmD = new TParm();
        if (row_m > -1) {
            parmD.setData("GROUP_ID", table_m.getItemString(row_m, "ID"));
        }
        else if (row_d > -1) {
            parmD.setData("GROUP_ID",
                          table_d.getParmValue().getValue("GROUP_ID", row_d));
        }
        parmD.setData("ID", this.getValueString("POPEDEM_ID"));
        parmD.setData("NAME", this.getValueString("POPEDEM_NAME"));
        parmD.setData("ENG_DESC", "");
        parmD.setData("PY1", "");
        parmD.setData("PY2", "");
        parmD.setData("SEQ", this.getValue("POPEDEM_SEQ"));
        parmD.setData("DESCRIPTION", this.getValueString("POPEDEM_DESCRIPTION"));
        parmD.setData("TYPE", "POPEDOM");
        parmD.setData("OPT_USER", Operator.getID());
        parmD.setData("OPT_TERM", Operator.getIP());
        parmD.setData("PARENT_ID", "");
        parmD.setData("STATE", "");
        parmD.setData("DATA", "");

        TParm result = new TParm();

        if ("INSERT_M".equals(action)) {
            // ��������
            if (parentID.length() == 0) {
                this.messageBox("��ѡ�����ڵ�");
                return;
            }
            if ("".equals(this.getValueString("PRG_ID"))) {
                this.messageBox("�����Ų���Ϊ��");
                return;
            }
            if ("".equals(this.getValueString("PRG_NAME"))) {
                this.messageBox("�������Ʋ���Ϊ��");
                return;
            }
            if ("".equals(this.getValueString("PRG_DATA"))) {
                this.messageBox("����·������Ϊ��");
                return;
            }
            result = SYSDictionaryTool.getInstance().select(parmM);
            if (result.getCount() > 0) {
                this.messageBox("�����Ѵ���");
                return;
            }
            result = SYSDictionaryTool.getInstance().insert(parmM);
            if (result.getErrCode() < 0) {
                this.messageBox("E0002");
                return;
            }
            String id = this.getValueString("PRG_ID");
            String name = getValueString("PRG_NAME");
            String type = "PRG";
            TTreeNode node = treeRoot.findNodeForValue(parentID + ":" + id);
            if (node != null) {
                TTreeNode newNode = new TTreeNode(name, type);
                newNode.setGroup(parentID);
                newNode.setID(id);
                newNode.setValue(parentID + ":" + id);
                node.add(newNode);
                callMessage("UI|TREE|update");
            }
            messageBox("P0002");
            callFunction("UI|PRG_ID|setEnabled", false);
            callFunction("UI|delete|setEnabled", true);
            action = "UPDATE_M";
            onInitTree();
        }
        else if ("UPDATE_D".equals(action)) {
            // ����Ȩ��
            if ("".equals(this.getValueString("POPEDEM_ID"))) {
                this.messageBox("Ȩ�ޱ�Ų���Ϊ��");
                return;
            }
            if ("".equals(this.getValueString("POPEDEM_NAME"))) {
                this.messageBox("Ȩ�����Ʋ���Ϊ��");
                return;
            }
            result = SYSDictionaryTool.getInstance().update(parmD);
            if (result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            this.onClear();
        }
        else {
            // ���³��������Ȩ��
            if ("".equals(this.getValueString("PRG_NAME"))) {
                this.messageBox("�������Ʋ���Ϊ��");
                return;
            }
            if ("".equals(this.getValueString("PRG_DATA"))) {
                this.messageBox("����·������Ϊ��");
                return;
            }
            TParm parm = new TParm();
            parm.setData("PARM_M", parmM.getData());
            if (!"".equals(this.getValueString("POPEDEM_ID")) &&
                !"".equals(this.getValueString("POPEDEM_NAME"))) {
                result = SYSDictionaryTool.getInstance().select(parmD);
                if (result.getCount() > 0) {
                    this.messageBox("Ȩ���Ѵ���");
                    return;
                }
                parm.setData("PARM_D", parmD.getData());
            }
            else if (!"".equals(this.getValueString("POPEDEM_ID")) &&
                     "".equals(this.getValueString("POPEDEM_NAME"))) {
                this.messageBox("Ȩ�����Ʋ���Ϊ��");
                return;
            }
            else if ("".equals(this.getValueString("POPEDEM_ID")) &&
                     !"".equals(this.getValueString("POPEDEM_NAME"))) {
                this.messageBox("Ȩ�ޱ�Ų���Ϊ��");
                return;
            }
            result = TIOM_AppServer.executeAction("action.sys.SYSPopedomAction",
                                                  "onUpdatePopedom", parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            messageBox("P0001");
            this.onClear();
            onInitTree();
        }
    }

    /**
     * ���
     */
    public void onClear() {
        clearValue("PRG_ID;PRG_NAME;PRG_PY1;PRG_PY2;PRG_SEQ;PRG_DESCRIPTION;"
                   + "PRG_STATE;PRG_DATA;POPEDEM_ID;POPEDEM_NAME;POPEDEM_SEQ;"
                   + "POPEDEM_DESCRIPTION");
        table_m.setSelectionMode(0);
        table_m.clearSelection();
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.clearSelection();
        table_d.removeRowAll();
        callFunction("UI|PRG_ID|setEnabled", true);
        callFunction("UI|POPEDEM_ID|setEnabled", true);
        callFunction("UI|save|setEnabled", true);
        callFunction("UI|delete|setEnabled", false);
        action = "INSERT_M";
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        int row_m = table_m.getSelectedRow();
        int row_d = table_d.getSelectedRow();
        TParm result = new TParm();
        if (row_m >= 0) {
            if (this.messageBox("��ʾ", "ȷ���Ƿ�ɾ������", 2) == 0) {
                TParm parm = new TParm();
                TParm prgParm = table_m.getParmValue().getRow(row_m);
                parm.setData("PRG_PARM", prgParm.getData());
                if (table_d.getRowCount() > 0) {
                    TParm popedomParm = table_d.getParmValue();
                    parm.setData("POPEDOM_PARM", popedomParm.getData());
                    TParm roleParm = new TParm();
                    roleParm.setData("GROUP_CODE",
                                     popedomParm.getValue("GROUP_ID", 0));
                    roleParm.setData("CODE",
                                     popedomParm.getValue("GROUP_ID", 0));
                    parm.setData("ROLE_PARM", roleParm.getData());
                }
                result = TIOM_AppServer.executeAction(
                    "action.sys.SYSPopedomAction",
                    "onDeletePrg", parm);
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("ɾ��ʧ��");
                    return;
                }
                messageBox("ɾ���ɹ�");
                this.onClear();
                onInitTree();
            }
        }
        else if (row_d >= 0) {
            if (this.messageBox("��ʾ", "ȷ���Ƿ�ɾ��Ȩ��", 2) == 0) {
                TParm parm = new TParm();
                TParm popedomParm = table_d.getParmValue().getRow(row_d);
                parm.setData("POPEDOM_PARM", popedomParm.getData());
                TParm roleParm = new TParm();
                roleParm.setData("GROUP_CODE", popedomParm.getValue("GROUP_ID"));
                roleParm.setData("CODE", popedomParm.getValue("ID"));
                parm.setData("ROLE_PARM", roleParm.getData());
                result = TIOM_AppServer.executeAction(
                    "action.sys.SYSPopedomAction",
                    "onDeletePopedom", parm);
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("ɾ��ʧ��");
                    return;
                }
                messageBox("ɾ���ɹ�");
                this.onClear();
                onInitTree();
            }
        }
        else {
            this.messageBox("û��ѡ����");
            return;
        }
    }

    /**
     * Ŀ¼���ƻس��¼�
     */
    public void onNameAction() {
        String py = TMessage.getPy(this.getValueString("PRG_NAME"));
        setValue("PRG_PY1", py);
        getTextField("PRG_PY1").grabFocus();
    }


    /**
     *
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
        this.onClear();
        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        String group = node.getGroup();
        String id = node.getID();
        groupID = group;
        parentID = id;
        TParm result = getDicTool().getListAll(parentID);
        table_m.setParmValue(result);
        this.setValue("PRG_SEQ", table_m.getRowCount() + 1);
    }

    /**
     *
     */
    public void onTableMClicked() {
        clearValue("PRG_ID;PRG_NAME;PRG_PY1;PRG_PY2;PRG_SEQ;PRG_DESCRIPTION;"
                   + "PRG_STATE;PRG_DATA;POPEDEM_ID;POPEDEM_NAME;POPEDEM_SEQ;"
                   + "POPEDEM_DESCRIPTION");
        table_d.setSelectionMode(0);
        table_d.clearSelection();
        table_d.removeRowAll();

        int row = table_m.getSelectedRow();
        if ("PRG".equals(table_m.getParmValue().getValue("TYPE", row))) {
            this.setValue("PRG_ID", table_m.getParmValue().getValue("ID", row));
            this.setValue("PRG_NAME",
                          table_m.getParmValue().getValue("NAME", row));
            this.setValue("PRG_DESCRIPTION",
                          table_m.getParmValue().getValue("DESCRIPTION", row));
            this.setValue("PRG_PY1", table_m.getParmValue().getValue("PY1", row));
            this.setValue("PRG_PY2", table_m.getParmValue().getValue("PY2", row));
            this.setValue("PRG_SEQ", table_m.getParmValue().getData("SEQ", row));
            this.setValue("PRG_DATA",
                          table_m.getParmValue().getValue("DATA", row));
            this.setValue("PRG_STATE",
                          table_m.getParmValue().getValue("STATE", row));
            callFunction("UI|delete|setEnabled", true);
            callFunction("UI|PRG_ID|setEnabled", false);
            TParm result = getDicTool().getListAll(table_m.getParmValue().
                getValue("ID", row));
            table_d.setParmValue(result);
            this.setValue("POPEDEM_SEQ", table_d.getRowCount() + 1);
            action = "UPDATE_M";
        }
        else {
            callFunction("UI|delete|setEnabled", false);
            callFunction("UI|PRG_ID|setEnabled", true);
            action = "INSERT_M";
        }
    }

    /**
     *
     */
    public void onTableDClicked() {
        clearValue("POPEDEM_ID;POPEDEM_NAME;POPEDEM_SEQ;POPEDEM_DESCRIPTION");
        table_m.setSelectionMode(0);
        table_m.clearSelection();
        int row = table_d.getSelectedRow();
        this.setValue("POPEDEM_ID", table_d.getParmValue().getValue("ID", row));
        this.setValue("POPEDEM_NAME",
                      table_d.getParmValue().getValue("NAME", row));
        this.setValue("POPEDEM_SEQ", table_d.getParmValue().getValue("SEQ", row));
        this.setValue("POPEDEM_DESCRIPTION",
                      table_d.getParmValue().getValue("DESCRIPTION", row));
        callFunction("UI|POPEDEM_ID|setEnabled", false);
        action = "UPDATE_D";
    }


    /**
     * ��ʼ����
     */
    public void onInitTree() {
        //�õ�����
        treeRoot = getTree("TREE").getRoot();
        if (treeRoot == null)
            return;
        treeRoot.setText("ϵͳ�ṹ");
        treeRoot.setGroup("POPEDOM");
        treeRoot.setID("SYS_SUBSYSTEM");
        treeRoot.setType("PATH");
        treeRoot.setValue("POPEDOM:SYS_SUBSYSTEM");
        treeRoot.removeAllChildren();
        downloadRootTree(treeRoot, "SYS_SUBSYSTEM");
        //���������ʼ������
        getTree("TREE").update();
    }

    /**
     * ����������
     * @param parentNode TTreeNode
     * @param group String
     */
    public void downloadRootTree(TTreeNode parentNode, String group) {
        if (parentNode == null)
            return;
        TParm parm = getDicTool().getGroupList(group);
        for (int i = 0; i < parm.getCount("ID"); i++) {
            String id = parm.getValue("ID", i);
            String name = parm.getValue("NAME", i);
            String type = parm.getValue("TYPE", i);
            String data = parm.getValue("DATA", i);
            TTreeNode node = new TTreeNode(name, type);
            node.setGroup(group);
            node.setID(id);
            node.setValue(group + ":" + id);
            node.setData(data);
            parentNode.add(node);
            downloadRootTree(node, id);
        }
    }


    /**
     * �õ��ֵ乤����
     * @return DictionaryTool
     */
    public DictionaryTool getDicTool() {
        return DictionaryTool.getInstance();
    }

    /**
     *
     * @param tag String
     * @return TTree
     */
    private TTree getTree(String tag) {
        return (TTree)this.getComponent(tag);
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

}
