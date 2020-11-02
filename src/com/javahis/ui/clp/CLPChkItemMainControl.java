package com.javahis.ui.clp;

import com.dongyang.control.*;
import jdo.clp.ChkItemMainTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTreeEvent;
import java.awt.Component;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

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
public class CLPChkItemMainControl extends TControl {
    public CLPChkItemMainControl() {
    }

    /** ���˻�����Ϣ */
    private TTextFormat DEPT_CODE; // ����
    private TTextFormat STATION_CODE; // ����
    private TTextFormat BED_NO; // ����
    private TTextField IPD_NO; // ID��
    private TTextField MR_NO; // ������
    private TTextField PAT_NAME; // ����
    private TComboBox SEX_CODE; // �Ա�
    private TTextFormat VS_DR_CODE; // ����ҽʦ
    private TTextFormat CLNCPATH_CODE; // �ٴ�·��
    private TTextField STAYHOSP_DAYS; // Ԥ��סԺ����
    private TTextField AVERAGECOST; // Ԥ��סԺ����
    private TTextFormat IN_DATE; // ��Ժ����
    private TTextFormat DS_DATE; // ��Ժ����
    private TCheckBox EXECUTE;
    /** ���ؿؼ� */
    private TTextField CASE_NO;
    private TTextField SCHD_DAY;
    /** ����ʱ�����νṹ */
    private TTree TREE; // ����ʱ�����νṹ
    /** ��ѯ���� */
    // ��ִ
    private TCheckBox EXEC_FLG;
    // ִ����Ա
    private TTextFormat CHKUSER_CODE;
    // ����״̬
    private TRadioButton PROGRESS_CODE1;
    private TRadioButton PROGRESS_CODE2;
    private TRadioButton PROGRESS_CODE3;
    /** CLP_MANAGERD */
    private TTable CLP_MANAGERD; // ʵ�����׼���ܶԱ�
    /** �������ڸ�ʽ������� */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    //�������
    private String case_no;
    //�ٴ�·��
    private String clncPathCode;
    /**
     * ��ʼ������
     * ��ʼ����ѯȫ��
     */
    public void onInit() {
        super.onInit();
        initInParm();
        // ��ȡȫ�������ؼ�
        getAllComponent();
        /** �������� */
        TParm parm = new TParm();
        // ����CASE_NOȡ��������Ϣ���ٴ�·��
        parm.setData("CASE_NO",this.case_no);
        TParm result = ChkItemMainTool.getInstance().queryPatientInfo(parm).
                       getRow(0);
        // ��ʼ������ʱ�����νṹ
        initTree(result);
        // ע������¼�
        initControler();
        // ���õ������ϵĿؼ�
        this.setValueForParm("DEPT_CODE;STATION_CODE;BED_NO;IPD_NO;MR_NO;PAT_NAME;SEX_CODE;VS_DR_CODE;CLNCPATH_CODE;STAYHOSP_DAYS;AVERAGECOST;IN_DATE;DS_DATE;CASE_NO",
                             result);
        STAYHOSP_DAYS.setValue(result.getValue("STAYHOSP_DAYS"));
        AVERAGECOST.setValue(result.getValue("AVERAGECOST"));
    }
    /**
     * ��ʼ���������
     */
    private void initInParm(){
        TParm inParm=(TParm)this.getParameter();
        case_no=inParm.getValue("CLP","CASE_NO");
        clncPathCode=inParm.getValue("CLP","CLNCPATH_CODE");
    }
    /**
     * ��ȡȫ�������ؼ�
     */
    public void getAllComponent() {
//        // ���˻�����Ϣ
//        DEPT_CODE = (TTextFormat)this.getComponent("DEPT_CODE");
//        DEPT_CODE.setEnabled(false);
//        STATION_CODE = (TTextFormat)this.getComponent("STATION_CODE");
//        STATION_CODE.setEnabled(false);
//        BED_NO = (TTextFormat)this.getComponent("BED_NO");
//        BED_NO.setEnabled(false);
//        IPD_NO = (TTextField)this.getComponent("IPD_NO");
//        IPD_NO.setEditable(false);
//        MR_NO = (TTextField)this.getComponent("MR_NO");
//        MR_NO.setEditable(false);
//        PAT_NAME = (TTextField)this.getComponent("PAT_NAME");
//        PAT_NAME.setEditable(false);
//        SEX_CODE = (TComboBox)this.getComponent("SEX_CODE");
//        SEX_CODE.setEnabled(false);
//        VS_DR_CODE = (TTextFormat)this.getComponent("VS_DR_CODE");
//        VS_DR_CODE.setEnabled(false);
//        CLNCPATH_CODE = (TTextFormat)this.getComponent("CLNCPATH_CODE");
//        CLNCPATH_CODE.setEnabled(false);
//        STAYHOSP_DAYS = (TTextField)this.getComponent("STAYHOSP_DAYS");
//        STAYHOSP_DAYS.setEditable(false);
//        AVERAGECOST = (TTextField)this.getComponent("AVERAGECOST");
//        AVERAGECOST.setEditable(false);
//        IN_DATE = (TTextFormat)this.getComponent("IN_DATE");
//        IN_DATE.setEnabled(false);
//        DS_DATE = (TTextFormat)this.getComponent("DS_DATE");
//        DS_DATE.setEnabled(false);
        EXECUTE = (TCheckBox)this.getComponent("EXECUTE");
        // ��ѯ����
        EXEC_FLG = (TCheckBox)this.getComponent("EXEC_FLG");
        CHKUSER_CODE = (TTextFormat)this.getComponent("CHKUSER_CODE");
        PROGRESS_CODE1 = (TRadioButton)this.getComponent("PROGRESS_CODE1");
        PROGRESS_CODE2 = (TRadioButton)this.getComponent("PROGRESS_CODE2");
        PROGRESS_CODE3 = (TRadioButton)this.getComponent("PROGRESS_CODE3");

        // ���ؿؼ�
//        CASE_NO = (TTextField)this.getComponent("CASE_NO");
        SCHD_DAY = (TTextField)this.getComponent("SCHD_DAY");
        // ����ʱ�����νṹ
        TREE = (TTree)this.getComponent("TREE");
        // ʵ�����׼���ܶԱ�
        CLP_MANAGERD = (TTable)this.getComponent("CLP_MANAGERD");
    }

    /**
     * ע������¼�
     */
    public void initControler() {
        // ������¼�����
        callFunction("UI|CLP_MANAGERD|addEventListener",
                     "CLP_MANAGERD->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        // ��TREE��Ӽ����¼�
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        // ʵ����Ŀ�м����¼�
        CLP_MANAGERD.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                      "onCreateEditComoponent");
    }

    /**
     * ��ʼ�����νṹ
     * @param parm TParm
     */
    private void initTree(TParm parm) {
        // ����CLNCPATH_CODE��ѯʱ��
        TParm result = ChkItemMainTool.getInstance().queryByClncPathCode(parm);
        TTreeNode root = (TTreeNode) TREE.getRoot();
        root.setText("����ʱ��");
        root.setType("Root");
        root.setID("");
        root.removeAllChildren();
        // ��ʼ���ڵ�
        initNode(result, root);
        TREE.update();
    }

    private void initNode(TParm result, TTreeNode root) {
        //��ʼ��ʼ����������
        for (int i = 0; i < result.getCount(); i++) {
            TParm rowParm = result.getRow(i);
            putNodeInTree(rowParm, root);
        }
    }

    /**
     * ���ڵ�������νṹ
     * @param rowParm TParm
     * @param root TTreeNode
     */
    private void putNodeInTree(TParm rowParm, TTreeNode root) {
        TTreeNode treeNode = new TTreeNode("KPILEAVE", "Path");
        treeNode.setText(rowParm.getValue("DURATION_CHN_DESC"));
        treeNode.setID(rowParm.getValue("DURATION_CODE"));
        String parentID = rowParm.getValue("PARENT_CODE");
        if (root.findNodeForID(rowParm.getValue("DURATION_CODE")) != null) {

        } else if (root.findNodeForID(parentID) != null) {
            root.findNodeForID(parentID).add(treeNode);
        } else {
            TParm parm = new TParm();
            parm.setData("DURATION_CODE", parentID);
            // ����DURATION_CODE��PARENT_CODE��ѯʱ��
            TParm result = ChkItemMainTool.getInstance().queryByDurationCode(
                    parm);
            if (result.getCount() <= 0) {
                root.add(treeNode);
            } else {
                putNodeInTree(result.getRow(0), root);
                root.findNodeForID(parentID).add(treeNode);
            }
        }
    }

    /**
     * CLP_MANAGERD����¼�
     * @param row int
     */
    public void onTableClicked(int row) {
        if (row < 0) {
            return;
        }
        int column = CLP_MANAGERD.getSelectedColumn();
        String orderSeq = CLP_MANAGERD.getParmValue().getValue("ORDER_SEQ", row);
        String orderChnDesc = CLP_MANAGERD.getParmValue().getValue(
                "ORDER_CHN_DESC", row);
        // ʵ����Ŀ���ؼ�������Ŀ��
        // ԭ������
        if (checkInputString(orderSeq)) {
            if (column == 0 || column == 7 || column == 8 || column == 9) {
                setTableEnabled(CLP_MANAGERD, row, column);
            } else {
                return;
            }
        }
        // ��������
        else {
            if (!checkInputString(orderChnDesc)) {
                if (column == 4) {
                    setTableEnabled(CLP_MANAGERD, row, column);
                } else {
                    return;
                }
            } else {
                if (column == 0) {
                    return;
                } else {
                    setTableEnabled(CLP_MANAGERD, row, column);
                }
            }

        }
        // ִ���кͽ���״̬������
        resetProgress(CLP_MANAGERD, row, column);
    }

    public TParm treeDataFormat(TTreeNode node) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", this.case_no);
        parm.setData("CLNCPATH_CODE", this.clncPathCode);
        parm.setData("SCHD_CODE", node.getID());
        parm.setData("ORDER_FLG", "Y");
        return parm;
    }

    /**
     * ���νṹ����¼�
     */
    public void onTreeClicked(Object obj) {
        TTreeNode node = (TTreeNode) obj;
        TParm parm = treeDataFormat(node);
        if ("Y".equals(EXEC_FLG.getValue())) {
            parm.setData("EXEC_FLG", EXEC_FLG.getValue());
        }
        parm.setData("CHKUSER_CODE", CHKUSER_CODE.getValue());
        if (PROGRESS_CODE1.isSelected()) {
            parm.setData("PROGRESS_CODE", "");
        }
        if (PROGRESS_CODE2.isSelected()) {
            parm.setData("PROGRESS_CODE", "A");
        }
        if (PROGRESS_CODE3.isSelected()) {
            parm.setData("PROGRESS_CODE", "B");
        }
        TParm result = ChkItemMainTool.getInstance().queryManagerDBySchdCode(
                parm);
        CLP_MANAGERD.setParmValue(result, CLP_MANAGERD.getParmMap());
        // ����ʱ������
        setSchdDay(node.getID());
        insertNewRow(CLP_MANAGERD);
        //
        EXECUTE.setSelected(false);
    }

    /**
     * ʵ����Ŀ �ؼ�������Ŀ
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateEditComoponent(Component com, int row, int column) {
        int rowCount = CLP_MANAGERD.getRowCount();
        if (row < 0) {
            return;
        }
        if (column == 4) {
            // ʵ����Ŀ�д������ؼ�������Ŀ��
            if (!(com instanceof TTextField)) {
                return;
            }
            TTextField textFilter = (TTextField) com;
            textFilter.onInit();
            // �ؼ�������Ŀ
            TParm parm = new TParm();
            parm.setData("ORDER_FLG", "Y");
            this.putBasicSysInfoIntoParm(parm);
            textFilter.setPopupMenuParameter("IG",
                                             getConfigParm().newConfig(
                    "%ROOT%\\config\\clp\\CLPChkItemPopup.x"),
                                             parm);
            //������ܷ���ֵ����
            textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                        "popReturn");
        }
    }

    /**
     * �ؼ�������Ŀ��Ϣ
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        // �ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
        if (obj == null && !(obj instanceof TParm)) {
            return;
        }
        // ����ת����TParm
        TParm parm = (TParm) obj;
        // ���عؼ�������Ŀ��Ϣ
        int row = CLP_MANAGERD.getSelectedRow();
        setManagerD(CLP_MANAGERD, parm, row);
    }

    /**
     * �����б�ָ����Ԫ��ɱ༭
     * @param table TTable
     * @param row int
     * @param column int
     */
    public void setTableEnabled(TTable table, int row, int column) {
        int rowCount = table.getRowCount();
        String lockRows = "";
        int columnCount = table.getColumnCount();
        String lockColumns = "";
        if (row < 0 || row >= rowCount) {
            return;
        }
        if (column < 0 || column >= columnCount) {
            return;
        }
        for (int i = 0; i < rowCount; i++) {
            if (i != row) {
                lockRows = lockRows + i + ",";
            }
        }
        for (int i = 0; i < columnCount; i++) {
            if (i != column) {
                lockColumns = lockColumns + i + ",";
            }
        }
        if (lockRows.endsWith(",")) {
            lockRows = lockRows.substring(0, lockRows.length() - 1);
        }
        table.setLockRows(lockRows);
        if (lockColumns.endsWith(",")) {
            lockColumns = lockColumns.substring(0, lockColumns.length() - 1);
        }
        table.setLockColumns(lockColumns);
    }

    /**
     * �ַ����ǿ���֤
     * @param str String
     * @return boolean
     */
    public boolean checkInputString(String str) {
        if (str == null) {
            return false;
        } else if ("".equals(str.trim())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * ����һ��
     * @param table TTable
     */
    public void insertNewRow(TTable table) {
        String tableTagName = table.getTag();
        int rowID = table.addRow();
//        table.setItem(rowID, "MAINTOT", "1.0");
    }

    /**
     * EXECUTEִ���кͽ���״̬����
     * @param table TTable
     * @param row int
     * @param column int
     */
    public void resetProgress(TTable table, int row, int column) {
        // ִ���д���
        if (column == 7) {
            String execute = String.valueOf(table.getItemData(row,
                    column));
            if ("Y".equals(execute)) {
                table.setItem(row, "EXECUTE", "N");
                table.setItem(row, "PROGRESS_CODE", "");
            }
            if ("N".equals(execute)) {
                table.setItem(row, "EXECUTE", "Y");
                table.setItem(row, "PROGRESS_CODE", "A01");
            }
        }
    }

    /**
     * ���عؼ�������Ŀ��Ϣ
     * @param parm TParm
     */
    public void setManagerD(TTable table, TParm parm, int row) {
        table.acceptText();
        TParm result = new TParm();
        // ѡ��
        result.setData("SEL_FLG", "N");
        // ��ִ
        result.setData("EXEC_FLG", "N");
        // ִ����
        result.setData("CHKUSER_CODE", "");
        // ���
        result.setData("CHKTYPE_CODE", parm.getValue("CHKTYPE_CODE"));
        // ʵ����Ŀ
        result.setData("ORDER_CHN_DESC", parm.getValue("ORDER_CHN_DESC"));
        // ������
        //¬���޸�
        result.setData("MAINTOT", parm.getValue("CLP_QTY"));
        // ��λ
        result.setData("MAINDISPENSE_UNIT", parm.getValue("CLP_UNIT"));
        // ִ��
        result.setData("EXECUTE", "N");
        // ����״̬
        result.setData("PROGRESS_CODE", "");
        // ��ע
        result.setData("MANAGE_NOTE", "");
        /** ������Ϣ */
        //
        result.setData("CASE_NO", this.case_no);
        //
        result.setData("CLNCPATH_CODE", this.clncPathCode);
        // ʱ�̴���
        result.setData("SCHD_CODE", TREE.getSelectNode().getID());
        result.setData("ORDER_NO", "");
        //
        result.setData("ORDER_SEQ", "");
        //
        result.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
        // ʱ������
        result.setData("SCHD_DAY", SCHD_DAY.getValue());
        /** ������Ϣ */
        TParm data = table.getParmValue();
        data.setRowData(row, result);
        table.setParmValue(data, table.getParmMap());
        if (row >= table.getRowCount() - 1) {
            insertNewRow(table);
        }
    }

    public void setSchdDay(String schdCode) {
        String sql =
                " SELECT SCHD_DAY FROM CLP_THRPYSCHDM WHERE CLNCPATH_CODE = '"
                + this.clncPathCode + "'"
                + " AND SCHD_CODE = '" + schdCode + "'";
        TParm tmpParm = new TParm(TJDODBTool.getInstance().select(sql));
        SCHD_DAY.setValue(tmpParm.getValue("SCHD_DAY", 0));
    }

    public void onSelect() {
        if ("Y".equals(EXECUTE.getValue())) {
            int count = CLP_MANAGERD.getRowCount();
            for (int i = 0; i < count - 1; i++) {
                CLP_MANAGERD.setItem(i, 7, "N");
                resetProgress(CLP_MANAGERD, i, 7);
            }
        }
        if ("N".equals(EXECUTE.getValue())) {
            int count = CLP_MANAGERD.getRowCount();
            for (int i = 0; i < count - 1; i++) {
                CLP_MANAGERD.setItem(i, "EXECUTE", "Y");
                resetProgress(CLP_MANAGERD, i, 7);
            }
        }
    }

    public void onQuery() {
        onTreeClicked(TREE.getSelectNode());
    }

    public void onClear() {
        EXEC_FLG.setSelected(false);
        CHKUSER_CODE.setValue("");
        PROGRESS_CODE1.setSelected(true);
        EXECUTE.setSelected(false);
    }

    public boolean check(TParm data) {
        int count = data.getCount("CASE_NO");
        for (int i = 0; i < count - 1; i++) {
            String chkUserCode = data.getValue("CHKUSER_CODE", i);
            if (!checkInputString(chkUserCode)) {
                this.messageBox("ִ���˲���Ϊ�գ�");
                return false;
            }
            String chkTypeCode = data.getValue("CHKTYPE_CODE", i);
            if (!checkInputString(chkTypeCode)) {
                this.messageBox("�����Ϊ�գ�");
                return false;
            }
            String orderChnDesc = data.getValue("ORDER_CHN_DESC", i);
            if (!checkInputString(orderChnDesc)) {
                this.messageBox("ʵ����Ŀ����Ϊ�գ�");
                return false;
            }
            String mainTot = data.getValue("MAINTOT", i);
            if (!checkInputString(mainTot)) {
                this.messageBox("����������Ϊ�գ�");
                return false;
            }
//            String mainDispenseUnit = data.getValue("MAINDISPENSE_UNIT", i);
//            if (!checkInputString(mainDispenseUnit)) {
//                this.messageBox("��λ����Ϊ�գ�");
//                return false;
//            }
        }
        return true;
    }

    public void checkData(TParm parm, int i) {
        if (!checkInputString(parm.getValue("CASE_NO", i))) {
            parm.setData("CASE_NO", i, "");
        }
        if (!checkInputString(parm.getValue("CLNCPATH_CODE", i))) {
            parm.setData("CLNCPATH_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("SCHD_CODE", i))) {
            parm.setData("SCHD_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("ORDER_NO", i))) {
            parm.setData("ORDER_NO", i, "");
        }
        if (!checkInputString(parm.getValue("ORDER_SEQ", i))) {
            parm.setData("ORDER_SEQ", i, "");
        }
        if (!checkInputString(parm.getValue("REGION_CODE", i))) {
            parm.setData("REGION_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("ORDER_CODE", i))) {
            parm.setData("ORDER_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("CHKTYPE_CODE", i))) {
            parm.setData("CHKTYPE_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("START_DAY", i))) {
            parm.setData("START_DAY", i, "");
        }
        if (!checkInputString(parm.getValue("STANDING_DTTM", i))) {
            parm.setData("STANDING_DTTM", i, "");
        }
        if (!checkInputString(parm.getValue("CHKUSER_CODE", i))) {
            parm.setData("CHKUSER_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("EXEC_FLG", i))) {
            parm.setData("EXEC_FLG", i, "");
        }
        if (!checkInputString(parm.getValue("TOT", i))) {
            parm.setData("TOT", i, "");
        }
        if (!checkInputString(parm.getValue("DISPENSE_UNIT", i))) {
            parm.setData("DISPENSE_UNIT", i, "");
        }
        if (!checkInputString(parm.getValue("STANDARD", i))) {
            parm.setData("STANDARD", i, "");
        }
        if (!checkInputString(parm.getValue("ORDER_FLG", i))) {
            parm.setData("ORDER_FLG", i, "");
        }
        if (!checkInputString(parm.getValue("SCHD_DESC", i))) {
            parm.setData("SCHD_DESC", i, "");
        }
        if (!checkInputString(parm.getValue("CHANGE_FLG", i))) {
            parm.setData("CHANGE_FLG", i, "");
        }
        if (!checkInputString(parm.getValue("STANDARD_FLG", i))) {
            parm.setData("STANDARD_FLG", i, "");
        }
        if (!checkInputString(parm.getValue("MAINORD_CODE", i))) {
            parm.setData("MAINORD_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("MAINTOT", i))) {
            parm.setData("MAINTOT", i, "");
        }
        if (!checkInputString(parm.getValue("MAINDISPENSE_UNIT", i))) {
            parm.setData("MAINDISPENSE_UNIT", i, "");
        }
        if (!checkInputString(parm.getValue("CFM_DTTM", i))) {
            parm.setData("CFM_DTTM", i, "");
        }
        if (!checkInputString(parm.getValue("CFM_USER", i))) {
            parm.setData("CFM_USER", i, "");
        }
        if (!checkInputString(parm.getValue("PROGRESS_CODE", i))) {
            parm.setData("PROGRESS_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("MEDICAL_MONCAT", i))) {
            parm.setData("MEDICAL_MONCAT", i, "");
        }
        if (!checkInputString(parm.getValue("MEDICAL_VARIANCE", i))) {
            parm.setData("MEDICAL_VARIANCE", i, "");
        }
        if (!checkInputString(parm.getValue("MEDICAL_NOTE", i))) {
            parm.setData("MEDICAL_NOTE", i, "");
        }
        if (!checkInputString(parm.getValue("MANAGE_MONCAT", i))) {
            parm.setData("MANAGE_MONCAT", i, "");
        }
        if (!checkInputString(parm.getValue("MANAGE_VARIANCE", i))) {
            parm.setData("MANAGE_VARIANCE", i, "");
        }
        if (!checkInputString(parm.getValue("MANAGE_NOTE", i))) {
            parm.setData("MANAGE_NOTE", i, "");
        }
        if (!checkInputString(parm.getValue("MANAGE_DTTM", i))) {
            parm.setData("MANAGE_DTTM", i, "");
        }
        if (!checkInputString(parm.getValue("MANAGE_USER", i))) {
            parm.setData("MANAGE_USER", i, "");
        }
        if (!checkInputString(parm.getValue("R_DEPT_CODE", i))) {
            parm.setData("R_DEPT_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("R_USER", i))) {
            parm.setData("R_USER", i, "");
        }
        if (!checkInputString(parm.getValue("TOT_AMT", i))) {
            parm.setData("TOT_AMT", i, "");
        }
        if (!checkInputString(parm.getValue("MAIN_AMT", i))) {
            parm.setData("MAIN_AMT", i, "");
        }
        if (!checkInputString(parm.getValue("MAINCFM_USER", i))) {
            parm.setData("MAINCFM_USER", i, "");
        }
        if (!checkInputString(parm.getValue("ORDTYPE_CODE", i))) {
            parm.setData("ORDTYPE_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("DEPT_CODE", i))) {
            parm.setData("DEPT_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("EXE_DEPT_CODE", i))) {
            parm.setData("EXE_DEPT_CODE", i, "");
        }
        if (!checkInputString(parm.getValue("OPT_USER", i))) {
            parm.setData("OPT_USER", i, "");
        }
        if (!checkInputString(parm.getValue("OPT_DATE", i))) {
            parm.setData("OPT_DATE", i, "");
        }
        if (!checkInputString(parm.getValue("OPT_TERM", i))) {
            parm.setData("OPT_TERM", i, "");
        }
    }

    public TParm dataFormat(TParm data) {
        int rowCount = CLP_MANAGERD.getRowCount();
        for (int i = 0; i < rowCount - 1; i++) {
            if (!checkInputString(data.getValue("ORDER_NO", i))) {
                // ������ʶ ����
                data.setData("OPT_FLG", i, "I");
                // ҽ�����
                data.setData("ORDER_NO", i,
                             SystemTool.getInstance().getNo("ALL", "CLP", "CLP",
                        "ORDERNO"));
                // ѭ�����
                data.setData("ORDER_SEQ", i, i);
                // ��׼��
                data.setData("STANDARD", i, "0");
                // ��׼ע��
                data.setData("STANDARD_FLG", i, "N");
                // �쳣״̬ע��
                data.setData("CHANGE_FLG", i, "N");
                // �ж��Ƿ�Ϊҽ��
                data.setData("ORDER_FLG", i, "N");
                // չ������
                data.setData("STANDING_DTTM", i,
                             dateFormat.format(SystemTool.getInstance().getDate()));

                // ����
                data.setData("REGION_CODE", i, Operator.getRegion());

                // ��׼��Ŀ->ʵ����Ŀ
                data.setData("MAINORD_CODE", i, data.getValue("ORDER_CODE", i));
                // ʵ������->��׼����
                data.setData("TOT", i, data.getValue("MAINTOT", i));
            } else {
                // ������ʶ ����
                data.setData("OPT_FLG", i, "U");
            }
            // ������Ա
            data.setData("OPT_USER", i, Operator.getID());
            // ����ʱ��
            data.setData("OPT_DATE", i,
                         dateFormat.format(SystemTool.getInstance().getDate()));
            // �����ն�
            data.setData("OPT_TERM", i, Operator.getIP());
            // ���ݼ��
            checkData(data, i);
        }
        return data;
    }

    public void onSave() {
        CLP_MANAGERD.acceptText();
        TParm data = CLP_MANAGERD.getParmValue();
        if (!check(data)) {
            return;
        }
        if (this.messageBox("ѯ��", "�Ƿ񸲸�ԭ�����ݣ�", 2) != 0) {
            return;
        }
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPChkItemMainAction", "saveManagerD",
                dataFormat(data));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            this.messageBox("����ʧ�ܣ�");
            return;
        } else {
            this.messageBox("����ɹ���");
            this.onTreeClicked(TREE.getSelectNode());
            // ���һ��ѡ��״̬
            if (CLP_MANAGERD.getRowCount() > 0) {
                CLP_MANAGERD.setSelectedRow(CLP_MANAGERD.getRowCount() - 1);
            }
            return;
        }

    }

    public void onDelete() {
        CLP_MANAGERD.acceptText();
        TParm parm = new TParm();
        TParm data = CLP_MANAGERD.getParmValue();
        for (int i = 0; i < CLP_MANAGERD.getRowCount() - 1; i++) {
            String selFlg = data.getValue("SEL_FLG", i);
            if ("Y".equals(selFlg)) {
                parm.addData("CASE_NO", this.case_no);
                parm.addData("CLNCPATH_CODE",this.clncPathCode);
                parm.addData("SCHD_CODE", TREE.getSelectNode().getID());
                parm.addData("ORDER_NO", data.getValue("ORDER_NO", i));
                parm.addData("ORDER_SEQ", data.getValue("ORDER_SEQ", i));
            }
        }
        if (parm.getCount("CASE_NO") <= 0) {
            return;
        }
        if (this.messageBox("ѯ��", "�Ƿ�ɾ����", 2) != 0) {
            return;
        }
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPChkItemMainAction", "deleteManagerD", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            this.messageBox("ɾ��ʧ�ܣ�");
            return;
        } else {
            this.messageBox("ɾ���ɹ���");
            this.onTreeClicked(TREE.getSelectNode());
            // ���һ��ѡ��״̬
            if (CLP_MANAGERD.getRowCount() > 0) {
                CLP_MANAGERD.setSelectedRow(CLP_MANAGERD.getRowCount() - 1);
            }
            return;
        }
    }

    /**
     * �ٴ�·��ģ��
     */
    public void onPreview() {
        TTreeNode node = TREE.getSelectNode();
        if (node == null || !checkInputString(node.getID())) {
            this.messageBox("��ѡ��ʱ�̣�");
            return;
        }
        TParm parm = treeDataFormat(node);
        parm.setData("SCHD_DAY", SCHD_DAY.getValue());
        Object result = openDialog("%ROOT%\\config\\clp\\CLPChkItemMainPopup.x",
                                   parm);
        if (result == null) {
            return;
        }
        int start = CLP_MANAGERD.getRowCount() - 1;
        TParm data = (TParm) result;
        for (int i = 0; i < data.getCount("SEL_FLG"); i++) {
            setManagerD(CLP_MANAGERD, data.getRow(i), start + i);
        }
    }

    /**
     * ����ǰTOOLBAR����ҳ��ʹ���˹����Ĳ���ѡ��ҳ��
     * ��ѡ�������˵��ٴη��ظ�ҳ��ʱ��ֹ�˵���Ϊ����ѡ��ҳ��,�����˷���
     */
    public void onShowWindowsFunction() {
        //��ʾUIshowTopMenu
        callFunction("UI|showTopMenu");
    }

    /**
     * ��TParm�м���ϵͳĬ����Ϣ
     * @param parm TParm
     */
    private void putBasicSysInfoIntoParm(TParm parm) {
        int total = parm.getCount();
//        System.out.println("total" + total);
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM", Operator.getIP());
    }

}
