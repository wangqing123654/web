package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import jdo.sys.SYSRuleTool;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import java.sql.Timestamp;
import com.dongyang.ui.TTableNode;
import jdo.sys.Operator;
import com.dongyang.util.TMessage;
import com.dongyang.ui.event.TTreeEvent;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTree;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTreeNode;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title:�ɱ����Ĺ��� </p>
 *
 * <p>Description:�ɱ����Ĺ��� </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author pangben 2011-06-01
 * @version 1.0
 */
public class SYSCostCenterControl extends TControl{
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
         treeRoot.setText("�ɱ�����");
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
                              + " FROM SYS_CATEGORY WHERE RULE_TYPE='SYS_COST_CENTER'");
         // �����dataStore���õ�������С��0
         if (treeDataStore.retrieve() <= 0)
             return;
         // ��������,�Ǳ�������еĳɱ���������
         ruleTool = new SYSRuleTool("SYS_COST_CENTER");
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
             s += "COST_CENTER_CODE like '" + id + "%'";
             // table�е�datastore�й��������������
             table.setFilter(s);
         }
         // ִ��table�Ĺ���
         table.filter();
         // ��table���ݼ���������
         table.setSort("COST_CENTER_CODE");
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
      * @param obj Object
      * @return boolean
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
         // ��������Ƹı���ƴ��1�Զ�����,���ҳɱ��������Ʋ���Ϊ��
         if ("COST_CENTER_CHN_DESC".equals(columnName)) {
             if (value.equals("") || value == null) {
                 messageBox_("�ɱ��������Ʋ���Ϊ��!");
                 return true;
             }
             String py = TMessage.getPy(value);
             node.getTable().setItem(node.getRow(), "PY1", py);
             return false;
         }
         // ����Ǽ�Ƹı���ƴ��1�Զ�����,���ҳɱ��������Ʋ���Ϊ��
         if ("COST_CENTER_ABS_DESC".equals(columnName)) {
             if (value.equals("") || value == null) {
                 messageBox_("�ɱ����ļ�Ʋ���Ϊ��!");
                 return true;
             }
         }

         // ����ǳɱ����Ĵ���ı���Ҫ���м���У��
         if ("COST_CENTER_CODE".equals(columnName)) {
             // ��������������ݳ���
             int length = ruleTool.getTot();
             // ����ı��ĳ��Ȳ����ڱ�������еĳ��ȷ���
             if (length != value.length()) {
                 messageBox_("���" + node.getValue() + "���Ȳ�����" + length + "!");
                 return true;
             }
             // �Գɱ����Ĵ�����м��...����������ظ�
             if (node.getTable().getDataStore().exist(
                 "COST_CENTER_CODE='" + value + "'")) {
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
         String maxCode = getMaxCode(dataStore, "COST_CENTER_CODE");
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
             table.setItem(row, "COST_CENTER_CODE", parentID + no);
             // Ĭ�ϳɱ���������
             table.setItem(row, "COST_CENTER_CHN_DESC", "(�½��ɱ�����)");
             // Ĭ�ϳɱ����ļ��
             table.setItem(row, "COST_CENTER_ABS_DESC", "(�½��ɱ���������)");
             // Ĭ������
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
         this.setValue("COST_CENTER_CODE", "");
         this.setValue("COST_CENTER_CHN_DESC", "");
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
      * @return boolean
      */
     public boolean onUpdate() {
         Timestamp date = SystemTool.getInstance().getDate();
         TTable table = (TTable) callFunction("UI|TABLE|getThis");
//         System.out.println("table��ʾ����"+table.getShowParmValue());
         // �����ı�
         table.acceptText();
         // ���ȫ���Ķ����к�
         int rows[] = table.getModifiedRowsFilter();
         //��ѯͣ�õĳɱ����Ĵ���
         String sql =
             " SELECT COST_CENTER_CODE FROM SYS_COST_CENTER WHERE ACTIVE_FLG = 'N' ";
         TParm selCostCenterParm = new TParm(TJDODBTool.getInstance().select(sql));
         int costCenterCount = selCostCenterParm.getCount("COST_CENTER_CODE");
         StringBuffer allCostCenter = new StringBuffer();
         for (int j = 0; j < costCenterCount; j++) {
         String costCenter="";
             costCenter = selCostCenterParm.getValue("COST_CENTER_CODE", j);
             if (allCostCenter.length() > 0)
                 allCostCenter.append(",");
             allCostCenter.append(costCenter);
         }
         String allCostCenterStr = allCostCenter.toString();
         // ���̶�����������
         for (int i = 0; i < rows.length; i++) {
             table.setItemFilter(rows[i], "OPT_USER", Operator.getID());
             table.setItemFilter(rows[i], "OPT_DATE", date);
             table.setItemFilter(rows[i], "OPT_TERM", Operator.getIP());
         }
//         System.out.println("����sql��������"+table.getSQL());
         if (!table.update()) {
             messageBox("E0001");
             return false;
         }
         //���¿��ұ��гɱ����Ĵ���Ϊ��
         String upSql =
             " UPDATE SYS_COST_CENTER SET COST_CENTER_CODE = '' WHERE SYS_COST_CENTER IN ("+allCostCenterStr+") ";
         if (allCostCenterStr.length() > 0) {
             TParm upCostCenter = new TParm(TJDODBTool.getInstance().update(
                 upSql));
             if (upCostCenter.getErrCode() < 0) {
                 err("ERR:" + upCostCenter.getErrCode() +
                     upCostCenter.getErrText() +
                     upCostCenter.getErrName());
                 return false;
             }
         }
         messageBox("P0001");
         return true;
     }

     /**
      * ��������
      *
      * @return String
      */
     private String getQuerySrc() { // �õ��ɱ����Ĵ���
         String code = getText("COST_CENTER_CODE");
         // �õ��ɱ���������
         String desc = getText("COST_CENTER_CHN_DESC");
         String sb = "";
         if(null!=Operator.getRegion()&&!"".equals(Operator.getRegion()))
             sb= " REGION_CODE='"+Operator.getRegion()+"' ";
         // ���������
         if (code != null && code.length() > 0)
             sb += "COST_CENTER_CODE like '" + code + "%'";
         if (desc != null && desc.length() > 0) {
//             if (sb.length() > 0)
//                 sb += " AND ";
             sb += "COST_CENTER_CHN_DESC like '" + desc + "%'";
         }
         return sb;
     }

     /**
      * �ɱ��������ƺͳɱ����Ĵ���س����¼�
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
      * @param dataStore TDataStore
      * @param columnName String
      * @param dbBuffer String
      * @return int
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
         if (null != Operator.getRegion() && !"".equals(Operator.getRegion()))
             s = " REGION_CODE='" + Operator.getRegion() + "' ";

         TTable table = (TTable) callFunction("UI|TABLE|getThis");
         table.setFilter(s);
         // ��table���ݼ���������
         table.setSort("COST_CENTER_CODE");
         // table��������¸�ֵ
         table.sort();
         // ִ��table�Ĺ���
         table.filter();
     }

}
