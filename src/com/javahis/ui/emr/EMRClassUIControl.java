package com.javahis.ui.emr;

import java.sql.*;

import com.dongyang.control.*;
import com.dongyang.data.*;
import com.dongyang.jdo.*;
import com.dongyang.ui.*;
import com.dongyang.ui.event.*;
import jdo.emr.*;
import jdo.sys.*;
import com.dongyang.util.TMessage;
import com.javahis.util.StringUtil;

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
public class EMRClassUIControl extends TControl {
    /**
    * ������
    * ������
    */
   private static final String TREE = "TREE";
   private static final String TABLE = "TABLE";
   /**
    * ���������
    */
   private static final String TABLEPANEL = "RootPanel";
   /**
    * ����
    */
   private TTreeNode root;
   /**
    * ��ʼ������
    */
   public void onInit() {
       super.onInit();
       //��ʼ����
       loadTree();
       //���ļ����¼�
       addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
       //�����¼�
       callFunction("UI|TABLE|addEventListener",
                    "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");

   }

   /**
    * ��ʼ����
    */
   public void loadTree() {
       //�õ�Tree�Ļ�������
       TParm selectTParm = new TParm();
       TParm result = EMRClassTool.getNewInstance().Select(selectTParm);
       //System.out.println("��ѯ������������:" + result);
       root = this.getTTree(TREE).getRoot();
       //ȡ���ڵ�;
       TParm rootParm = this.getTreeRoot();
       root.setText(rootParm.getValue("CLASS_DESC", 0));
       root.setType("Root");
       root.setID(rootParm.getValue("CLASS_CODE", 0));
       //��ʼ������ǰ���������Ҷ�ӽڵ�
       root.removeAllChildren();
       //��ʼ��ʼ����������
       int count = result.getCount();
       for (int i = 0; i < count; i++) {
           TParm dataParm = result.getRow(i);
           putNodeInTree(dataParm, root);
       }
       this.getTTree(TREE).update();

   }

   private void putNodeInTree(TParm dataParm, TTreeNode root) {
       String noteType = "Path"; //UI
       TTreeNode treeNode = new TTreeNode("EMRCLASS", noteType);
       treeNode.setText(dataParm.getValue("CLASS_DESC"));
       treeNode.setID(dataParm.getValue("CLASS_CODE"));
       String parentID = dataParm.getValue("PARENT_CLASS_CODE");
//        System.out.println("parentID-------------:" + parentID);
       if (root.findNodeForID(dataParm.getValue("CLASS_CODE")) != null) {
//            System.out.println("�Ѿ����д˽ڵ㲻��ִ����Ӳ���");
       }
       else if (root.findNodeForID(parentID) != null) {
           root.findNodeForID(parentID).add(treeNode);
       }
       else {
           TParm parentTparmselect = new TParm();
           parentTparmselect.setData("CLASS_CODE", parentID);
           TParm resultParm = EMRClassTool.getNewInstance().onQuery(
               parentTparmselect);
//            System.out.println("��ѯ���ĸ��ڵ�:" + resultParm);
           if (resultParm.getCount() <= 0) {
               root.add(treeNode);
           }
           else {
               putNodeInTree(resultParm.getRow(0), root);
               root.findNodeForID(parentID).add(treeNode);
           }

       }
   }

   /**
    * �����¼�
    */

   public void onTableClick() {
       int row = this.getTTable(TABLE).getClickedRow();
       TParm data = (TParm) callFunction("UI|Table|getParmValue");
       setValueForParm(
           "CLASS_CODE;CLASS_DESC;PY1;PY2;_DESC;ENG_DESC;DESCRIPTION;LEAF_FLG;CLASS_STYLE;SEQ;",
           data, row);
       ( (TTextField) getComponent("CLASS_CODE")).setEnabled(false);
       
       String createXmlFlg = data.getValue("CREATE_XML_FLG", row);
       if("Y".equals(createXmlFlg)){
    	   ((TCheckBox)getComponent("CREATE_XML_FLG")).setSelected(true);
       }else{
    	   ((TCheckBox)getComponent("CREATE_XML_FLG")).setSelected(false);
       }

   }

   /**
    * �������¼�
    */
   public void onTreeClicked() {
       //����Ƿ�õ����Ľڵ�
       if ( ( (TTree) getComponent("TREE")).getSelectNode() == null) {
           return;
       }
       this.clearValue(
           "CLASS_CODE;CLASS_DESC;PY1;PY2;_DESC;ENG_DESC;DESCRIPTION;LEAF_FLG;CLASS_STYLE;SEQ;CREATE_XML_FLG;");
       ( (TTextField) getComponent("CLASS_CODE")).setEnabled(true);
       this.onQuery();
   }

   /**
    * ���
    */
   public void onClear() {
       TTable table = this.getTTable(TABLE);
       if (table.getRowCount() > 0) {
           table.removeRowAll();
       }
       this.clearValue(
           "CLASS_CODE;CLASS_DESC;PY1;PY2;ENG_DESC;DESCRIPTION;LEAF_FLG;CLASS_STYLE;SEQ;CREATE_XML_FLG;");
       table.removeRowAll();
       ( (TTextField) getComponent("CLASS_CODE")).setEnabled(true);
       this.loadTree();

   }

   /**
    * getTTable
    *
    * @param TABLE TTable
    * @return Object
    */
   public TTable getTTable(String tagName) {
       return (TTable)this.getComponent(tagName);
   }

   /**
    * getTTtree
    * @param String tag
    * @return Object
    */

   public TTree getTTree(String tag) {
       return (TTree)this.getComponent(tag);
   }

//��������
   public void onSave() {

           if (!checkData()) {
               return;
           }

           TTable table = this.getTTable(TABLE);
           TParm parm = new TParm();
           Timestamp date = SystemTool.getInstance().getDate();
           parm.setData("CLASS_CODE", this.getValueString("CLASS_CODE"));
           parm.setData("CLASS_DESC", this.getValueString("CLASS_DESC"));
           parm.setData("PY1", this.getValueString("PY1"));
           parm.setData("PY2", this.getValueString("PY2"));
           parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
           parm.setData("SEQ", this.getValueInt("SEQ"));
          //this.messageBox("dddddd"+this.getValueString("LEAF_FLG"));
           if(this.getValueString("LEAF_FLG").equals("N"))
           {
               //this.messageBox("===come in==");
               //callFunction("UI|CLASS_STYLE|setEnabled", false);
               parm.setData("CLASS_STYLE", "ROOT");
           }
           else {
               //callFunction("UI|CLASS_STYLE|setEnabled", true);
               parm.setData("CLASS_STYLE", this.getValueString("CLASS_STYLE"));
           }
           parm.setData("ENG_DESC", this.getValueString("ENG_DESC"));
           parm.setData("LEAF_FLG", this.getValueString("LEAF_FLG"));
           parm.setData("OPT_USER", Operator.getID());
           parm.setData("OPT_DATE", date);
           parm.setData("OPT_TERM", Operator.getIP());
           parm.setData("PARENT_CLASS_CODE",
                        ( (TTree) getComponent("TREE")).getSelectNode().getID());
           
           //add by yangjj 20150806
           parm.setData("CREATE_XML_FLG",this.getValueString("CREATE_XML_FLG"));
           
           TParm result = new TParm();
           if (table.getSelectedRow() < 0) {
               // ��������
               result = EMRClassTool.getNewInstance().Insert(parm);
           }
           else {
               // ��������
               TParm checkparm = new TParm();
               checkparm.setData("PARENT_CLASS_CODE",
                                 this.getValueString("CLASS_CODE"));
               TParm checkflg = EMRClassTool.getNewInstance().onQuery(
                   checkparm);
               //����ڵ����ӽڵ�ʱ����ΪҶ�ڵ�ı�ʶ
               if (this.getValueString("LEAF_FLG").equals('Y')){
                     if (checkflg.getCount() > 0){
                       this.messageBox("ע�����ӽڵ�,��ʶ����");
                       return;
                   }
               }
               else {
                   //this.messageBox("parm====="+parm);
                   result = EMRClassTool.getNewInstance().onUpdate(parm);
               }
           }
           if (result.getErrCode() < 0) {
               this.messageBox("E0001");
           }
           else {
               this.messageBox("P0001");
               loadTree();
               onQuery();
           }

    }

   /**
    * ��������
    */
   public void onNew() {
       TTable table = this.getTTable(TABLE);

       if ( ( (TTree) getComponent("TREE")).getSelectNode() == null) {
           this.messageBox("��ѡ�����ڵ�");
           return;
       }
//        this.setValue("CLASS_CODE",
//                      getMaxSerialNumber( ( (TTree) getComponent("TREE")).
//                                         getSelectNode().getID()));
       this.setValue("SEQ",
                     getMaxSEQNumber( ( (TTree) getComponent("TREE")).
                                     getSelectNode().
                                     getID()));
       this.clearValue(
           "CLASS_CODE;CLASS_DESC;PY1;PY2;ENG_DESC;DESCRIPTION;LEAF_FLG;CLASS_STYLE;CREATE_XML_FLG;");
       
       //add by yangjj 20150806
       ((TCheckBox)getComponent("CREATE_XML_FLG")).setSelected(true);
       
       table.removeRowAll();
   }

//    /**
//     * �õ��������SQL
//     * @param PARENT_CLASS_CODE String
//     * @return String
//     *
//     *     */
//    public static String MaxSerialNumber(String PARENT_CLASS_CODE) {
//        return "SELECT MAX(CLASS_CODE) AS CLASS_CODE FROM EMR_CLASS "
//            + "WHERE PARENT_CLASS_CODE  = '" + PARENT_CLASS_CODE + "'";
//    }
//
   /**
    * �õ������ˮ��SQL
    * @param PARENT_CLASS_CODE String
    * @return int
    *
    *     */

   public static String MaxSEQNumber(String PARENT_CLASS_CODE) {
       return "SELECT MAX(SEQ) AS SEQ FROM EMR_CLASS "
           + "WHERE PARENT_CLASS_CODE  = '" + PARENT_CLASS_CODE + "'";
   }


//    /**
//     * �õ��������
//     * @param PARENT_CLASS_CODE String
//     * @return String
//     *
//     *     */
//    private String getMaxSerialNumber(String PARENT_CLASS_CODE) {
//        TParm parm = new TParm(TJDODBTool.getInstance().select(
//            MaxSerialNumber(PARENT_CLASS_CODE)));
//        String code = " ";
//        code = parm.getValue("CLASS_CODE", 0);
////        this.messageBox(PARENT_CLASS_CODE);
////        this.messageBox(code);
//        String numstring = "";
//        String charing = "";
//        String classcode = "";
//        String first = String.valueOf(0);
//        String second = String.valueOf(1);
//        if (PARENT_CLASS_CODE.equals("ROOT")) {
//            charing = code.substring(0, PARENT_CLASS_CODE.length());
//            numstring = code.substring(PARENT_CLASS_CODE.length(),
//                                       parm.getValue("CLASS_CODE", 0).length());
//            int num = Integer.parseInt(numstring) + 1;
//            numstring = num + "";
//            classcode = charing + numstring;
//        }
//        else if (code.length() > 0) {
//            charing = code.substring(0, PARENT_CLASS_CODE.length() +1);
//            numstring =getMaxSEQNumber( ( (TTree) getComponent("TREE")).
//                                      getSelectNode().
//                                      getID())+ "";
//            classcode = charing + numstring;
//
//        }
//        else {
//            classcode = PARENT_CLASS_CODE + first + second;
//        }
//        return classcode;
//
//    }

   /**
    * �õ������ˮ��
    * @param PARENT_CLASS_CODE String
    * @return int
    *
    *     */
   private int getMaxSEQNumber(String PARENT_CLASS_CODE) {
       TParm parm = new TParm(TJDODBTool.getInstance().select(
           MaxSEQNumber(PARENT_CLASS_CODE)));
       int num = 0;
       num = parm.getInt("SEQ", 0);
       int seq = 0;
       if (parm.getCount() > 0) {
           seq = num + 1;
       }

       else {
           seq = 1;
       }
       return seq;
   }

   /**
    * checkData
    *
    * @return boolean
    */
   public boolean checkData() {
       String code = this.getValueString("CLASS_CODE");
       if (code == null || code.length() <= 0) {
           this.messageBox("���������Ϊ��");
           return false;
       }
       return true;

   }
   /**
    * getTextField
    *
    * @param string String
    * @return Object
    */
   public TTextField getTextField(String tagName) {
       return (TTextField) getComponent(tagName);
   }
   /**
    * getNumberTextField
    *
    * @param string String
    * @return Object
    */

   /**
    *
    * @��ѯ
    */
   public void onQuery() {
       TParm parm = new TParm();
       String CLASS_CODE = this.getValueString("CLASS_CODE");
       if (CLASS_CODE != null && CLASS_CODE.length() > 0) {
           parm.setData("CLASS_CODE", CLASS_CODE);
       }
       parm.setData("PARENT_CLASS_CODE",
                    ( (TTree) getComponent("TREE")).getSelectNode().getID());
       TParm result = EMRClassTool.getNewInstance().onQuery(parm);
       this.getTTable(TABLE).setParmValue(result);
       ( (TTable) getComponent("TABLE")).setParmValue(result);
   }

    /**
     * ɾ��
     * @author wanglong 20150106
     */
    public void onDelete() {
        TTable table = this.getTTable(TABLE);
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ�����е�һ��");
            return;
        }
        TParm parm = table.getParmValue();
        String classCode = parm.getValue("CLASS_CODE", row);
        String sql1 = "SELECT * FROM EMR_TEMPLET WHERE CLASS_CODE = '#'";
        sql1 = sql1.replaceFirst("#", classCode);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql1));
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
        this.messageBox("P0005");
        this.onClear();
    }
   
   /**
    * ʵ����
    */
   public TJDODBTool getDBTool() {
       return TJDODBTool.getInstance();
   }

   /**
    * ���ڵ�
    * @return TParm
    */
   public TParm getTreeRoot() {
       TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE,SEQ FROM EMR_CLASS WHERE PARENT_CLASS_CODE IS NULL"));
       return result;
   }

   /**add by lx
    * �ҵ�����
    * @return TParm
    */
   public TParm getMainRootTree() {
       TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE,SEQ FROM EMR_CLASS ORDER BY CLASS_CODE,SEQ"));
       return result;
   }

   /**
    * �����ӽڵ�
    * @param systemCode String
    * @return TParm
    */
   public TParm getChildTreeType(String classCode) {
       TParm result = new TParm(this.getDBTool().select("SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE,IPD_FLG,HRM_FLG,OPD_FLG,EMG_FLG,OIDR_FLG,NSS_FLG,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE,PY1,SEQ,SYSTEM_CODE FROM EMR_TEMPLET WHERE CLASS_CODE='" +
           classCode + "' ORDER BY SUBCLASS_CODE,CLASS_CODE,SEQ"));
       return result;
   }

}
