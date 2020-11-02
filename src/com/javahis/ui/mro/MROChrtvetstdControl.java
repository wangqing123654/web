package com.javahis.ui.mro;

import com.dongyang.control.*;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import jdo.mro.MROChrtvetstdTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.TMessage;
import com.dongyang.manager.TCM_Transform;
import jdo.sys.SYSRuleTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TTree;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title:������˱�׼ </p>
 *
 * <p>Description: ������˱�׼</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author zhangk 2009-4-29
 * @version 1.0
 */
public class MROChrtvetstdControl extends TControl {
    TParm data;
    int selectRow = -1;
    TParm  selParm ;//�����ӽڵ��Ĭ��ֵ
    /**
     * ����
     */
    private TTreeNode treeRoot;
    /**
     * ��Ź�����𹤾�
     */
    SYSRuleTool ruleTool;
    //�������֮����ʾ�����͵�����Ÿ��ڵ�
     private String id = null;
    /**
     * �������ݷ���datastore���ڶ��������ݹ���
     */
//
    TDataStore treeDataStore = new TDataStore();
    public void onInit() {
        super.onInit();
        ((TTable) getComponent("Table")).addEventListener("Table->"
                + TTableEvent.CLICKED, this, "onTableClicked");
        // ��ʼ����
        onInitSelectTree();
        // ��tree��Ӽ����¼�
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        // ��Table��������¼�
        // ����
        onCreatTree();
        onClear();
        showTable();
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
        treeRoot.setText("����������׼");
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
        StringBuffer sql=new StringBuffer();
        sql.append("SELECT RULE_TYPE, CATEGORY_CODE,"
                             + " CATEGORY_CHN_DESC, CATEGORY_ENG_DESC,"
                             + " PY1, PY2, SEQ, DESCRIPTION, DETAIL_FLG,"
                             + " OPT_USER, OPT_DATE, OPT_TERM"
                             +
                             " FROM SYS_CATEGORY WHERE RULE_TYPE='MRO_CHRTVETSTD'");
        treeDataStore.setSQL(sql.toString());
        sql.append(" AND LENGTH(CATEGORY_CODE)=3");
        selParm = new TParm(TJDODBTool.getInstance().select(sql.toString()));
        // �����dataStore���õ�������С��0
        if (treeDataStore.retrieve() <= 0)
            return;
        // ��������,������������׼����
        ruleTool = new SYSRuleTool("MRO_CHRTVETSTD");

        if (ruleTool.isLoad()) { // �����۽ڵ����:datastore���ڵ����,�ڵ���ʾ����,,�ڵ�����
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                    "CATEGORY_CODE", "CATEGORY_CHN_DESC", "Path", "SEQ");
            // ѭ����������ڵ�

            for (int i = 0; i < node.length; i++) {
                treeRoot.addSeq(node[i]);
            }
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
        //callFunction("UI|new|setEnabled", false);
        // �õ�������Ľڵ����
        onClear();
        showTable();
        TTreeNode node = (TTreeNode) parm;

        if (node == null)
            return;
        // �õ�table����

        // table�������иı�ֵ
        //  table.acceptText();
        // �������������ĸ����
        if (node.getType().equals("Root")) {
        }
        else { // �����Ĳ��Ǹ����
            // �õ���ǰѡ�еĽڵ��idֵ
            String scode="";
            id = node.getID();
            for(int i=0;i<selParm.getCount();i++ ){
                if(id.equals(selParm.getValue("CATEGORY_CODE",i))){
                    scode=selParm.getValue("DESCRIPTION",i);//��õ�ǰ�ڵ�ķ���ֵ
                    break;
                }
            }

            //Ĭ�Ϸ�ֵ��ʾ
            this.setValue("SUMPRICE", scode);
        }
       showExamine(id);
    }
    /**
     * ���������ʾ�����͵������
     */
    public void showExamine(String id){
        // ���ݹ�����������Tablet�ϵ�����
           data = MROChrtvetstdTool.getInstance().selectdata(id);
           TTable table = (TTable) callFunction("UI|Table|getThis");
           table.setParmValue(data);
           // �õ���ID
           String maxCode = getMaxCode(data, "EXAMINE_CODE");
           String parentID =id;
           int classify = 1;
           if (parentID.length() > 0)
               classify = ruleTool.getNumberClass(parentID) + 1;
           // �������С�ڵ�,��������һ��
           if (classify > ruleTool.getClassifyCurrent()) {
               String no = ruleTool.getNewCode(maxCode, classify);
               callFunction("UI|EXAMINE_CODE|setEnabled", false);
               this.setValue("EXAMINE_CODE", parentID + no);//��ʾ�����
           }

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

    public String getMaxCode(TParm parm, String columnName) {
        if (parm == null)
            return "";
        int count = parm.getCount();
        String s = "";
        for (int i = 0; i < count; i++) {
            String value = parm.getValue(columnName, i);
            if (StringTool.compareTo(s, value) < 0)
                s = value;
        }
        return s;
    }

    /**
     * ���Ӷ�Table�ļ���
     *
     * @param row
     *            int
     */
    public void onTableClicked(int row) {
        // ѡ����
        if (row < 0)
            return;
        setValueForParm(
                "EXAMINE_CODE;TYPE_CODE;EXAMINE_DESC;PY1;PY2;SCORE;SPCFY_DEPT;DESCRIPTION;CHECK_FLG;METHOD_CODE;METHOD_PARM;CHECK_SQL",
                data, row);
        selectRow = row;
        //�Ƿ񼱼�
        if (data.getValue("URG_FLG", row).equals("Y"))
            ((TRadioButton)this.getComponent("URG_YES")).setSelected(true);
        else
            ((TRadioButton)this.getComponent("URG_NO")).setSelected(true);
        //��Ժ/��Ժ��ѡ
        if (data.getValue("CHECK_RANGE", row).equals("1"))
            ((TRadioButton)this.getComponent("CHECK_RANGE_1")).setSelected(true);
        else if (data.getValue("CHECK_RANGE", row).equals("2"))
            ((TRadioButton)this.getComponent("CHECK_RANGE_2")).setSelected(true);
        else {
        }
        //�ż���/סԺ��ѡ
        if (data.getValue("CHECK_RANGE1", row).equals("1"))
            ((TRadioButton)this.getComponent("CHECK_RANGE1_1")).setSelected(true);
        else if (data.getValue("CHECK_RANGE1", row).equals("2"))
            ((TRadioButton)this.getComponent("CHECK_RANGE1_2")).setSelected(true);

        // ���ɱ༭
        ((TTextField) getComponent("EXAMINE_CODE")).setEnabled(false);
        // ����ɾ����ť״̬
        ((TMenuItem) getComponent("delete")).setEnabled(true);

        onCheckFlg();
    }

    /**
     * ����
     */
    public boolean onInsert() {
        if (this.getText("EXAMINE_CODE").trim().length() <= 0 ||
            this.getText("EXAMINE_DESC").trim().length() <= 0) {
            this.messageBox("������˹����������Ʋ���Ϊ�գ�");
            return false;
        }
        if (((TComboBox)this.getComponent("TYPE_CODE")).getSelectedID().trim().
            length() <= 0) {
            this.messageBox("��ѡ���������");
            return false;
        }

        TParm parm = this.getParmForTag("EXAMINE_CODE;TYPE_CODE;EXAMINE_DESC;PY1;PY2;SCORE;SPCFY_DEPT;DESCRIPTION;CHECK_FLG;METHOD_CODE;METHOD_PARM;CHECK_SQL");
        //�Ƿ񼱼�
        if (((TRadioButton)this.getComponent("URG_YES")).isSelected())
            parm.setData("URG_FLG", "Y");
        else
            parm.setData("URG_FLG", "N");
        //��Ժ/��Ժ��ѡ
        if (((TRadioButton)this.getComponent("CHECK_RANGE_1")).isSelected())
            parm.setData("CHECK_RANGE", "1");
        else
            parm.setData("CHECK_RANGE", "2");
        //�ż���/סԺ��ѡ
        if (((TRadioButton)this.getComponent("CHECK_RANGE1_1")).isSelected())
            parm.setData("CHECK_RANGE1", "1");
        else
            parm.setData("CHECK_RANGE1", "2");

        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = MROChrtvetstdTool.getInstance().insertdata(parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return false;
        }
        // ��ʾ��������
        int row = ((TTable) getComponent("TABLE"))
                  .addRow(
                          parm,
                          "EXAMINE_CODE;TYPE_CODE;EXAMINE_DESC;PY1;PY2;URG_FLG;SCORE;SPCFY_DEPT;CHECK_FLG;CHECK_RANGE;METHOD_CODE;METHOD_PARM;OPT_USER;OPT_DATE;OPT_TERM;CHECK_SQL");

//         int row = data.insertRow();
        data.setRowData(row, parm);

        this.messageBox("��ӳɹ���");
        return true;
    }

    /**
     * ����
     */
    public boolean onUpdate() {
        if (this.getText("EXAMINE_CODE").trim().length() <= 0 ||
            this.getText("EXAMINE_DESC").trim().length() <= 0) {
            this.messageBox("������˹����������Ʋ���Ϊ�գ�");
            return false;
        }
        if (((TComboBox)this.getComponent("TYPE_CODE")).getSelectedID().trim().
            length() <= 0) {
            this.messageBox("��ѡ���������");
            return false;
        }
        TParm parm = this.getParmForTag("EXAMINE_CODE;TYPE_CODE;EXAMINE_DESC;PY1;PY2;SCORE;SPCFY_DEPT;DESCRIPTION;CHECK_FLG;METHOD_CODE;METHOD_PARM;CHECK_SQL");
        //�Ƿ񼱼�
        if (((TRadioButton)this.getComponent("URG_YES")).isSelected())
            parm.setData("URG_FLG", "Y");
        else
            parm.setData("URG_FLG", "N");
        //��Ժ/��Ժ��ѡ
        if (((TRadioButton)this.getComponent("CHECK_RANGE_1")).isSelected())
            parm.setData("CHECK_RANGE", "1");
        else
            parm.setData("CHECK_RANGE", "2");
        //�ż���/סԺ��ѡ
        if (((TRadioButton)this.getComponent("CHECK_RANGE1_1")).isSelected())
            parm.setData("CHECK_RANGE1", "1");
        else
            parm.setData("CHECK_RANGE1", "2");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());

        TParm result = MROChrtvetstdTool.getInstance().updatedata(parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return false;
        }
        // ѡ����
        int row = ((TTable) getComponent("Table")).getSelectedRow();
        if (row < 0)
            return false;
        // ˢ�£�����ĩ��ĳ�е�ֵ
        data.setRowData(row, parm);
        ((TTable) getComponent("Table")).setRowParmValue(row, data);
        this.messageBox("�޸ĳɹ���");
        return true;
    }

    /**
     * ����
     */
    public void onSave() {
        boolean istrue = false;
        if (selectRow == -1) {
            istrue = onInsert();
            if (istrue)
                onClear();
            showExamine(id);
            return;
        }
        istrue=onUpdate();
        if (istrue)
            onClear();
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        if (!"Y".equals(this.getValueString("CHECK_FLG"))) {
            data = MROChrtvetstdTool.getInstance().selectdata(getText(
                    "EXAMINE_CODE").trim());
        } else {
            data = MROChrtvetstdTool.getInstance().selectdata(getText(
                    "EXAMINE_CODE").trim(),
                    this.getRadioButton("CHECK_RANGE_1").isSelected() ? "1" :
                    "2",
                    this.getValueString("METHOD_CODE"));
        }

        // �жϴ���ֵ
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ((TTable) getComponent("Table")).setParmValue(data);
    }

    /**
     * ���
     */
    public void onClear() {
        this.clearValue("EXAMINE_CODE;TYPE_CODE;EXAMINE_DESC;PY1;PY2;SCORE;SPCFY_DEPT;DESCRIPTION;METHOD_CODE;METHOD_PARM;CHECK_SQL");
        this.setValue("CHECK_FLG", "Y");
        this.getRadioButton("CHECK_RANGE_1").setSelected(true);
        this.getRadioButton("CHECK_RANGE1_1").setSelected(true);
        ((TTable) getComponent("Table")).clearSelection();
        selectRow = -1;
        // ����ɾ����ť״̬
        ((TRadioButton)this.getComponent("URG_NO")).setSelected(true);
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        ((TTextField) getComponent("EXAMINE_CODE")).setEnabled(true);
    }
    public void showTable(){

        data = MROChrtvetstdTool.getInstance().selectdata(getText(
                "EXAMINE_CODE").
                trim());
        // �жϴ���ֵ
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ((TTable) getComponent("Table")).setParmValue(data);

}
    /**
     * ɾ��
     */
    public void onDelete() {
        if (this.messageBox("��ʾ", "�Ƿ�ɾ��", 2) == 0) {
            if (selectRow == -1)
                return;
            String code = getValue("EXAMINE_CODE").toString();
            TParm result = MROChrtvetstdTool.getInstance().deletedata(code);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            TTable table = ((TTable) getComponent("Table"));
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            this.messageBox("ɾ���ɹ���");
            onClear();
            showTable();
        } else {
            return;
        }
    }

    /**
     * �����Զ��ʿ�
     */
    public void onCheckFlg() {
        if ("Y".equals(this.getValueString("CHECK_FLG"))) {
//            //��Ժ/��Ժ��ѡ
//            getRadioButton("CHECK_RANGE_1").setEnabled(true);
//            getRadioButton("CHECK_RANGE_2").setEnabled(true);
//            //�ż���/סԺ��ѡ
//            getRadioButton("CHECK_RANGE1_1").setEnabled(true);
//            getRadioButton("CHECK_RANGE1_2").setEnabled(true);
            getComboBox("METHOD_CODE").setEnabled(true);
            getTextField("METHOD_PARM").setEnabled(true);
            getTextField("CHECK_SQL").setEnabled(true);
        } else {
//            //��Ժ/��Ժ��ѡ
//            getRadioButton("CHECK_RANGE_1").setEnabled(false);
//            getRadioButton("CHECK_RANGE_2").setEnabled(false);
//            //�ż���/סԺ��ѡ
//            getRadioButton("CHECK_RANGE1_1").setEnabled(false);
//            getRadioButton("CHECK_RANGE1_2").setEnabled(false);
            getComboBox("METHOD_CODE").setEnabled(false);
            getTextField("METHOD_PARM").setEnabled(false);
            getTextField("CHECK_SQL").setEnabled(false);
        }
    }

    /**
     * ���ݺ������ƴ������ĸ
     *
     * @return Object
     */
    public Object onCode() {
        if (TCM_Transform.getString(this.getValue("EXAMINE_DESC")).length() <
            1) {
            return null;
        }
        String value = TMessage.getPy(this.getValueString("EXAMINE_DESC"));
        if (null == value || value.length() < 1) {
            return null;
        }
        this.setValue("PY1", value);
        // �������
        ((TTextField) getComponent("PY1")).grabFocus();
        return null;
    }

    /**
     *
     * @param tag String
     * @return TRadioButton
     */
    public TRadioButton getRadioButton(String tag) {
        return (TRadioButton)this.getComponent(tag);
    }

    /**
     *
     * @param tag String
     * @return TTextFormat
     */
    public TComboBox getComboBox(String tag) {
        return (TComboBox)this.getComponent(tag);
    }

    /**
     *
     * @param tag String
     * @return TTextField
     */
    public TTextField getTextField(String tag) {
        return (TTextField)this.getComponent(tag);
    }
}
