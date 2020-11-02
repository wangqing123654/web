package com.javahis.ui.emr;

import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;



/**
 * <p>Title:�������������� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright:Copyright (c) 2014 </p>
 *
 * <p>Company:Bluecore </p>
 *
 * @author wanglong 2014.8.15
 * @version 1.0
 */
public class EMRNewClassUIControl extends TControl {

    private TTree tree;
    private TTable table;
    private TTreeNode root;// ����
    private String emrView;

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        tree = (TTree) this.getComponent("TREE");
        table = (TTable) this.getComponent("TABLE");
        this.setValue("EMR_VIEW", "2");// Ĭ��ѡ��ҽ�ƹ��̷���
        this.onChooseView();
        // this.initTree(); // ��ʼ��������
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked"); // ���ļ����¼�
        callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this,
                     "onTableClicked"); // �����¼�
        callFunction("UI|CLASS_CODE|setPopupMenuParameter", "CLASS_CODE",
                     "%ROOT%\\config\\emr\\EMRClassPopup.x");// ��������������
        callFunction("UI|CLASS_CODE|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popClassReturn");
    }

    /**
     * �������������򷵻�ֵ
     * 
     * @param tag
     * @param obj
     */
    public void popClassReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String sql = "SELECT * FROM EMR_CLASS WHERE CLASS_CODE='#'";
        sql = sql.replaceFirst("#", parm.getValue("CLASS_CODE"));
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getCount() > 0) {
            this.setValue("CLASS_DESC", result.getValue("CLASS_DESC", 0));
        }
        this.setValue("CLASS_CODE", parm.getValue("CLASS_CODE"));
    }

    /**
     * ��ʼ����
     */
    public void initTree() {
        emrView = this.getValueString("EMR_VIEW");
        if (emrView.equals("1")) {// ����������
            this.setTagState("save;new;query;clear;CLASS_CODE;CLASS_DESC;PY1;PY2;ENG_DESC;DESCRIPTION;CLASS_STYLE;SEQ;LEAF_FLG",
                             false);
        } else if (emrView.equals("2") || emrView.equals("3")) {// 2.ҽ�ƹ��� 3.���ҷ���
            this.setTagState("save;new;query;clear;CLASS_CODE;CLASS_DESC;PY1;PY2;ENG_DESC;DESCRIPTION;CLASS_STYLE;SEQ;LEAF_FLG",
                             true);
        }
        root = tree.getRoot();
        TParm rootParm = this.getTreeRoot(emrView); // ȡ���ڵ�����
        root.setText(rootParm.getValue("CLASS_DESC", 0));
        root.setType("Root");// �ڵ�����
        root.setID(rootParm.getValue("CLASS_CODE", 0));
        root.removeAllChildren(); // ���������Ҷ�ӽڵ�
        TParm childrenParm = this.getTreeChildren(emrView); // �õ�Tree�Ļ�������
        // System.out.println("��ѯ������������:" + children);
        // ��ʼ��ʼ����������
        for (int i = 0; i < childrenParm.getCount(); i++) {
            TParm childParm = childrenParm.getRow(i);
            this.putNodeInTree(childParm, root);
        }
        tree.update();
//        tree.getTree().getRowForNode( )
//       tree.getSelectionNode().getLastChild()
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
        String noteType = "Path"; // �ڵ�����
        if (childParm.getValue("LEAF_FLG").equals("Y")) {
            noteType = "Leaf";
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
            TParm parentParm = this.getTreeChildren(emrView, parentID);
            // System.out.println("��ѯ���ĸ��ڵ�:" + resultParm);
            if (parentParm.getCount() <= 0) {
                root.add(node);
            } else {
                this.putNodeInTree(parentParm.getRow(0), root);
                root.findNodeForID(parentID).add(node);
            }
        }
    }

    /**
     * ѡ����������ͼ�¼�
     */
    public void onChooseView() {
        this.onClear();
        emrView = this.getValueString("EMR_VIEW");
        this.initTree();
    }

    /**
     * ��񵥻��¼�
     */
    public void onTableClick() {
        int row = table.getClickedRow();
        TParm data = table.getParmValue();
        setValueForParm("CLASS_CODE;CLASS_DESC;PY1;PY2;_DESC;ENG_DESC;DESCRIPTION;LEAF_FLG;CLASS_STYLE;SEQ;",
                        data, row);
        this.setTagState("CLASS_CODE", false);
    }

    /**
     * ���ڵ㵥���¼�
     */
    public void onTreeClicked() {
        // ����Ƿ�õ����Ľڵ�
        if (tree.getSelectNode() == null) {
            return;
        }
        this.clearValue("CLASS_CODE;CLASS_DESC;PY1;PY2;_DESC;ENG_DESC;DESCRIPTION;LEAF_FLG;CLASS_STYLE;SEQ;");
        this.setTagState("CLASS_CODE", true);
        this.onQuery();
    }

    /**
     * ����
     */
    public void onSave() {
        String classCode = this.getValueString("CLASS_CODE").trim();
        if (StringUtil.isNullString(classCode)) {
            this.messageBox("���������Ϊ��");
            return;
        }
        String classDesc = this.getValueString("CLASS_DESC").trim();
        if (StringUtil.isNullString(classDesc)) {
            this.messageBox("�������Ʋ���Ϊ��");
            return;
        }
        TParm result = new TParm();
        if (table.getSelectedRow() < 0) { // ��������
            if (emrView.equals("2") || emrView.equals("3")) {
                String sql = "SELECT * FROM EMR_CLASS WHERE CLASS_CODE='#' AND LEAF_FLG='Y'";
                sql = sql.replaceFirst("#", classCode);
                TParm check = new TParm(TJDODBTool.getInstance().select(sql));
                if (check.getCount() > 0 && this.getValueString("LEAF_FLG").equals("N")) {
                    this.messageBox("�����������е�Ҷ�ӽڵ���Ҫѡ��Ҷ�ڵ���");
                    return;
                }
                if (check.getCount() < 1 && this.getValueString("LEAF_FLG").equals("Y")) {
                    this.messageBox("ֻ�����������������е�Ҷ�ӽڵ�");
                    return;
                }
                String sql2 = "SELECT * FROM EMR_CLASS_OTHER WHERE EMR_VIEW='#' AND CLASS_CODE='@'";
                sql2 = sql2.replaceFirst("#", emrView);
                sql2 = sql2.replaceFirst("@", classCode);
                TParm check2 = new TParm(TJDODBTool.getInstance().select(sql2));
                if (check2.getCount() > 0) {
                    this.messageBox("�ýڵ��������г��ֹ���һ���ڵ�����������ֻ�������һ�Σ�");
                    return;
                }
            }
            TDataStore tds = new TDataStore();
            if (emrView.equals("1")) {
                tds.setSQL("SELECT * FROM EMR_CLASS");
            } else if (emrView.equals("2") || emrView.equals("3")) {
                tds.setSQL("SELECT * FROM EMR_CLASS_OTHER");
            }
            tds.retrieve();
            int row = tds.insertRow(-1);
            Timestamp date = SystemTool.getInstance().getDate();
            if (emrView.equals("2") || emrView.equals("3")) {
                tds.setItem(row, "EMR_VIEW", emrView);
            }
            tds.setItem(row, "CLASS_CODE", classCode);
            tds.setItem(row, "CLASS_DESC", classDesc);
            tds.setItem(row, "PY1", this.getValueString("PY1"));
            tds.setItem(row, "PY2", this.getValueString("PY2"));
            tds.setItem(row, "DESCRIPTION", this.getValueString("DESCRIPTION"));
            tds.setItem(row, "SEQ", this.getValueInt("SEQ"));
            if (this.getValueString("LEAF_FLG").equals("N")) {
                tds.setItem(row, "CLASS_STYLE", "ROOT");
            } else {
                tds.setItem(row, "CLASS_STYLE", this.getValueString("CLASS_STYLE"));
            }
            tds.setItem(row, "ENG_DESC", this.getValueString("ENG_DESC"));
            tds.setItem(row, "LEAF_FLG", this.getValueString("LEAF_FLG"));
            tds.setItem(row, "OPT_USER", Operator.getID());
            tds.setItem(row, "OPT_DATE", date);
            tds.setItem(row, "OPT_TERM", Operator.getIP());
            root = tree.getRoot();
            if(StringUtil.isNullString( root.getID())  ){
                tds.setItem(row, "PARENT_CLASS_CODE", "");
            } else {
                tds.setItem(row, "PARENT_CLASS_CODE", tree.getSelectNode().getID());
                tds.setItem(row, "SEQ", this.getNextSeq(tree.getSelectNode().getID()));
            }
        
            tds.setActive(row, true);
            result = new TParm(this.getDBTool().update(tds.getUpdateSQL()));
        } else { // ��������
            TParm parm = new TParm();
            parm.setData("PARENT_CLASS_CODE", classCode);
            TParm checkParm = this.getTreeChildren(emrView, parm);
            // ����ڵ����ӽڵ�ʱ����ΪҶ�ڵ�ı�ʶ
            if (this.getValueString("LEAF_FLG").equals("Y")) {
                if (checkParm.getCount() > 0) {
                    this.messageBox("ע�����ӽڵ�,��ʶ����");
                    return;
                }
            }
            TDataStore tds = new TDataStore();
            if (emrView.equals("1")) {
                tds.setSQL("SELECT * FROM EMR_CLASS WHERE CLASS_CODE='#'".replaceFirst("#",classCode));
            } else if (emrView.equals("2") || emrView.equals("3")) {
                tds.setSQL("SELECT * FROM EMR_CLASS_OTHER WHERE EMR_VIEW = '#' AND CLASS_CODE='@'"
                        .replaceFirst("#", emrView).replaceFirst("@",
                                                                 classCode));
            }
            tds.retrieve();
            Timestamp date = SystemTool.getInstance().getDate();
            tds.setItem(0, "EMR_VIEW", emrView);
            tds.setItem(0, "CLASS_CODE",classCode);
            tds.setItem(0, "CLASS_DESC", classDesc);
            tds.setItem(0, "PY1", this.getValueString("PY1"));
            tds.setItem(0, "PY2", this.getValueString("PY2"));
            tds.setItem(0, "DESCRIPTION", this.getValueString("DESCRIPTION"));
            tds.setItem(0, "SEQ", this.getValueInt("SEQ"));
            if (this.getValueString("LEAF_FLG").equals("N")) {
                tds.setItem(0, "CLASS_STYLE", "ROOT");
            } else {
                tds.setItem(0, "CLASS_STYLE", this.getValueString("CLASS_STYLE"));
            }
            tds.setItem(0, "ENG_DESC", this.getValueString("ENG_DESC"));
            tds.setItem(0, "LEAF_FLG", this.getValueString("LEAF_FLG"));
            tds.setItem(0, "OPT_USER", Operator.getID());
            tds.setItem(0, "OPT_DATE", date);
            tds.setItem(0, "OPT_TERM", Operator.getIP());
            tds.setItem(0, "PARENT_CLASS_CODE", tree.getSelectNode().getID());
            result = new TParm(this.getDBTool().update(tds.getUpdateSQL()));
        }
        if (result.getErrCode() < 0) {
            this.messageBox("E0001");
        } else {
            this.messageBox("P0001");
            this.initTree();
            this.onQuery();
        }
    }


    /**
     * ��������
     */
    public void onNew() {
        if (tree.getSelectNode() == null) {
            this.messageBox("��ѡ�����ڵ�");
            return;
        }
        this.setValue("SEQ", this.getMaxSEQNumber(emrView, tree.getSelectNode().getID()));
        this.clearValue("CLASS_CODE;CLASS_DESC;PY1;PY2;ENG_DESC;DESCRIPTION;LEAF_FLG;CLASS_STYLE;");
        table.removeRowAll();
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = new TParm();
        String CLASS_CODE = this.getValueString("CLASS_CODE");
        if (!StringUtil.isNullString(CLASS_CODE)) {
            parm.setData("CLASS_CODE", CLASS_CODE);
        }
        parm.setData("PARENT_CLASS_CODE", tree.getSelectNode().getID());
        TParm result = this.getTreeChildren(emrView, parm);
        table.setParmValue(result);
    }

    /**
     * ɾ��
     * @author wanglong 20150106
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ�����е�һ��");
            return;
        }
        TParm parm = table.getParmValue();
        String classCode = parm.getValue("CLASS_CODE", row);
        TParm result = new TParm();
        if (emrView.equals("1")) {//����������
            String sql1 = "SELECT * FROM EMR_TEMPLET WHERE CLASS_CODE = '#'";
            sql1 = sql1.replaceFirst("#", classCode);
            result = new TParm(TJDODBTool.getInstance().select(sql1));
            if (result.getErrCode() < 0) {
                this.messageBox(result.getErrText());
                return;
            }
            if (result.getCount() > 0) {
                this.messageBox("����ɾ���ý���µĲ���ģ��");
                return;
            } else {
                String sql2 = "SELECT * FROM EMR_CLASS WHERE PARENT_CLASS_CODE = '#'";
                sql2 = sql2.replaceFirst("#", classCode);
                result = new TParm(TJDODBTool.getInstance().select(sql2));
                if (result.getErrCode() < 0) {
                    this.messageBox(result.getErrText());
                    return;
                }
                if (result.getCount() > 0) {
                    this.messageBox("����ɾ�����²���");
                    return;
                }
            }
            String deleteSql = "DELETE FROM EMR_CLASS WHERE CLASS_CODE = '#'";
            deleteSql = deleteSql.replaceFirst("#", classCode);
            result = new TParm(TJDODBTool.getInstance().update(deleteSql));
            if (result.getErrCode() < 0) {
                this.messageBox(result.getErrText());
                return;
            }
        } else {// ��������
            String sql2 = "SELECT * FROM EMR_CLASS_OTHER WHERE PARENT_CLASS_CODE = '#'";
            sql2 = sql2.replaceFirst("#", classCode);
            result = new TParm(TJDODBTool.getInstance().select(sql2));
            if (result.getErrCode() < 0) {
                this.messageBox(result.getErrText());
                return;
            }
            if (result.getCount() > 0) {
                this.messageBox("����ɾ�����²���");
                return;
            }
            String deleteSql = "DELETE FROM EMR_CLASS_OTHER WHERE CLASS_CODE = '#'";
            deleteSql = deleteSql.replaceFirst("#", classCode);
            result = new TParm(TJDODBTool.getInstance().update(deleteSql));
            if (result.getErrCode() < 0) {
                this.messageBox(result.getErrText());
                return;
            }
        }
        this.messageBox("P0005");
        this.onClear();
    }
    
    /**
     * ���
     */
    public void onClear() {
        this.clearValue("CLASS_CODE;CLASS_DESC;PY1;PY2;ENG_DESC;DESCRIPTION;LEAF_FLG;CLASS_STYLE;SEQ");
        this.setTagState("CLASS_CODE", true);
        if (table.getRowCount() > 0) {
            table.removeRowAll();
        }
        table.removeRowAll();
        this.initTree();
    }

    /**
     * �õ������ˮ��
     * 
     * @param parentClassCode
     *            String
     * @return int
     */
    private int getMaxSEQNumber(String emrView, String parentClassCode) {
        String sql = "";
        if (emrView.equals("1")) {
            sql = "SELECT MAX(SEQ) AS SEQ FROM EMR_CLASS WHERE PARENT_CLASS_CODE = '#'";
        } else if (emrView.equals("2")) {
            sql = "SELECT MAX(SEQ) AS SEQ FROM EMR_CLASS_OTHER WHERE PARENT_CLASS_CODE = '#'";
        } else if (emrView.equals("3")) {
            sql = "SELECT MAX(SEQ) AS SEQ FROM EMR_CLASS_OTHER WHERE PARENT_CLASS_CODE = '#'";
        }
        sql = sql.replaceFirst("#", parentClassCode);
        TParm parm = new TParm(this.getDBTool().select(sql));
        int num = 0;
        num = parm.getInt("SEQ", 0);
        int seq = 0;
        if (parm.getCount() > 0) {
            seq = num + 1;
        } else {
            seq = 1;
        }
        return seq;
    }

    /**
     * ȡ�ø��ڵ�����
     * 
     * @param emrView
     *            String
     * @return TParm
     */
    private TParm getTreeRoot(String emrView) {
        String sql = "";
        if (emrView.equals("1")) {
            sql =
                    "SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE,SEQ "
                            + " FROM EMR_CLASS WHERE PARENT_CLASS_CODE IS NULL";
        } else if (emrView.equals("2")) {
            sql =
                    "SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE,SEQ "
                            + " FROM EMR_CLASS_OTHER WHERE EMR_VIEW = '2' AND PARENT_CLASS_CODE IS NULL";
        } else if (emrView.equals("3")) {
            sql =
                    "SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE,SEQ "
                            + " FROM EMR_CLASS_OTHER WHERE EMR_VIEW = '3' AND PARENT_CLASS_CODE IS NULL";
        }
        TParm result = new TParm(this.getDBTool().select(sql));
        return result;
    }

    /**
     * ȡ���ӽڵ�����
     * 
     * @param emrView
     *            String
     * @return TParm
     */
    private TParm getTreeChildren(String emrView) {
        return this.getTreeChildren(emrView, new TParm());
    }

    /**
     * ȡ���ӽڵ�����
     * 
     * @param emrView
     *            String
     * @param classCode
     *            String
     * @return TParm
     */
    private TParm getTreeChildren(String emrView, String classCode) {
        TParm parm = new TParm();
        parm.setData("CLASS_CODE", classCode);
        return this.getTreeChildren(emrView, parm);
    }

    /**
     * ȡ���ӽڵ�����
     * 
     * @param emrView
     *            String
     * @param parm
     *            TParm
     * @return TParm
     */
    private TParm getTreeChildren(String emrView, TParm parm) {
        String sql = "";
        if (emrView.equals("1")) {
            sql =
                    "SELECT CLASS_CODE, CLASS_DESC, ENG_DESC, PY1, PY2, DESCRIPTION, "
                            + "SEQ, CLASS_STYLE, LEAF_FLG, PARENT_CLASS_CODE "
                            + " FROM EMR_CLASS WHERE 1=1 # & ORDER BY CLASS_CODE,PARENT_CLASS_CODE,SEQ";
        } else if (emrView.equals("2")) {
            sql =
                    "SELECT CLASS_CODE, CLASS_DESC, ENG_DESC, PY1, PY2, DESCRIPTION, "
                            + "SEQ, CLASS_STYLE, LEAF_FLG, PARENT_CLASS_CODE "
                            + " FROM EMR_CLASS_OTHER WHERE EMR_VIEW = '2' # & ORDER BY CLASS_CODE,PARENT_CLASS_CODE,SEQ";
        } else if (emrView.equals("3")) {
            sql =
                    "SELECT CLASS_CODE, CLASS_DESC, ENG_DESC, PY1, PY2, DESCRIPTION, "
                            + "SEQ, CLASS_STYLE, LEAF_FLG, PARENT_CLASS_CODE "
                            + " FROM EMR_CLASS_OTHER WHERE EMR_VIEW = '3' # & ORDER BY CLASS_CODE,PARENT_CLASS_CODE,SEQ";
        } else return new TParm();
        if (!StringUtil.isNullString(parm.getValue("CLASS_CODE"))) {
            sql =
                    sql.replaceFirst("#", " AND CLASS_CODE = '@' ".replaceFirst("@", parm
                            .getValue("CLASS_CODE")));
        } else {
            sql = sql.replaceFirst("#", "");
        }
        if (!StringUtil.isNullString(parm.getValue("PARENT_CLASS_CODE"))) {
            sql =
                    sql.replaceFirst("&", " AND PARENT_CLASS_CODE = '@' ".replaceFirst("@", parm
                            .getValue("PARENT_CLASS_CODE")));
        } else {
            sql = sql.replaceFirst("&", "");
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * �õ��ڵ���Ϣ
     * @param emrView
     * @param classCode
     * @return
     */
//    private TParm getNodeInfo(String emrView, String classCode) {
//        String sql = "";
//        if (emrView.equals("1")) {
//            sql = "SELECT * FROM EMR_CLASS WHERE CLASS_CODE = '#'";
//        } else if (emrView.equals("2") || emrView.equals("3")) {
//            sql = "SELECT * FROM EMR_CLASS_OTHER WHERE EMR_VIEW = '@' AND CLASS_CODE = '#'";
//            sql = sql.replaceFirst("@", emrView);
//        } else return new TParm();
//        sql = sql.replaceFirst("#", classCode);
//        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//        return result;
//    }
    
    /**
     * �õ���һ���
     */
    private int getNextSeq(String parentClassCode) {
        String sql = "SELECT MAX(SEQ) AS SEQ FROM EMR_CLASS_OTHER WHERE PARENT_CLASS_CODE = '#'";
        sql = sql.replaceFirst("#", parentClassCode);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            return 0;
        } else return result.getInt("SEQ", 0) + 1;
    }

    /**
     * �������״̬
     * 
     * @param tags
     *            String
     * @param flag
     *            boolean
     */
    public void setTagState(String tags, boolean flag) {
        String[] names = StringTool.parseLine(tags, ";");
        if (names.length == 0) return;
        for (int i = 0; i < names.length; i++) {
            this.callFunction("UI|" + names[i] + "|setEnabled", flag);
        }
    }


    /**
     * �õ����ݿ�����
     * 
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
}
