package com.javahis.ui.inf;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TNull;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import jdo.inf.INFICUD;
import jdo.inf.INFICUM;
import jdo.sys.PatTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p> Title: ICUʹ�����ľ����ùܡ���������������ܵǼǱ� </p>
 * 
 * <p> Description: ICUʹ�����ľ����ùܡ���������������ܵǼǱ� </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: BlueCore </p>
 * 
 * @author WangLong 2014.02.24
 * @version 1.0
 */
public class INFICUMaterialsRecordControl
        extends TControl {

    TTable recordTable;// ����ҩƷ��
    TTable orderTable;// ����ҩƷ��
    TTextFormat IN_DEPT ;
    TTextFormat OUT_DEPT ;
 
    private INFICUM icuM;// �¼���ϢDataStore
    private INFICUD icuD;// ����ҩƷ����DataStore
    boolean isDeliver = false;//�д���
    private boolean NEW_FLG = true;// ������ǣ�true����;false���£�
    private String RE_NO;//�����¼����
 //   private TParm PARM;//�����¼���ϸ��Ϣ
    
    Timestamp regDate;
    String regUser;

    private String MR_NO;//������
    private String CASE_NO;//�����
    private String IPD_NO;//סԺ��
   TParm PARAM ;

    /**
     * ��ʼ��
     */
    public void onInit() {
        initComponent();
        if (this.getParameter() instanceof TParm) {
            PARAM = (TParm) this.getParameter();
            if (!PARAM.getValue("CASE_NO").equals("")) {// ��CASE_NO������ִ���½�����
                isDeliver = true;
                MR_NO = PARAM.getValue("MR_NO");
                CASE_NO = PARAM.getValue("CASE_NO");
                IPD_NO = PARAM.getValue("IPD_NO");
            } else {
                this.messageBox("E0024");// ��ʼ������ʧ��
            }
        } else if (this.getParameter() instanceof String) {
            // this.messageBox("���Ϊ�� "+this.getParameter());
        }
    }

    /**
     * ��ʼ������ؼ�
     */ 
    public void initComponent(){  
        IN_DEPT = ((TTextFormat) this.getComponent("IN_DEPT"));
        OUT_DEPT = ((TTextFormat) this.getComponent("OUT_DEPT"));
        recordTable = (TTable) this.getComponent("RECORD_TABLE");
        callFunction("UI|RECORD_TABLE|addEventListener", "RECORD_TABLE->" + TTableEvent.DOUBLE_CLICKED, this, "onTableDoubleClicked");// ˫���¼�
        orderTable = (TTable) this.getComponent("ORDER_TABLE");
        orderTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this, "onOrderEditComponent");
        callFunction("UI|ICD_DESC1|setPopupMenuParameter", "ICD_DESC1",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        callFunction("UI|ICD_DESC1|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popICDReturn");
        callFunction("UI|ICD_DESC2|setPopupMenuParameter", "ICD_DESC2",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        callFunction("UI|ICD_DESC2|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popICDReturn");
        callFunction("UI|ICD_DESC3|setPopupMenuParameter", "ICD_DESC3",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        callFunction("UI|ICD_DESC3|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popICDReturn");
        callFunction("UI|DIAG1_DESC|setPopupMenuParameter", "DIAG1_DESC",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        callFunction("UI|DIAG1_DESC|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popICDReturn");
        callFunction("UI|DIAG2_DESC|setPopupMenuParameter", "DIAG2_DESC",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        callFunction("UI|DIAG2_DESC|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popICDReturn");
        callFunction("UI|DIAG3_DESC|setPopupMenuParameter", "DIAG3_DESC",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        callFunction("UI|DIAG3_DESC|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popICDReturn");
        Timestamp now = SystemTool.getInstance().getDate();
        this.setValue("IN_DATE", now);
        this.setValue("LASTICU_DATE", now);
        this.setValue("INICU_DATE", now);
        this.setValue("OUTICU_DATE", now);
        this.setValue("EXAM_DATE", now);
        recordTable.retrieve();
        initComboBox();
        enableUI(false);
    }

    /**
     * ���ݴ���ֵ��ʼ��������Ϣ
     */
    public void writeUIByReNo(String reNo) {
        icuM = new INFICUM();
        icuM.onQueryByReNo(reNo);
        TParm parm = icuM.getRowParm(0);
        initUIByCase(parm.getValue("CASE_NO"));
        //MR_NO;PAT_NAME;SEX_CODE;AGE;IN_DATE;DEPT_CODE;STATION_CODE;LASTICU_DATE;IN_DEPT;INICU_DATE;OUT_DEPT;OUTICU_DATE;ICD_DESC1;ICD_DESC2;ICD_DESC3;ICD_CODE1;ICD_CODE2;ICD_CODE3;DIAG1_DESC;DIAG2_DESC;DIAG3_DESC;DIAG1;DIAG2;DIAG3;WP1_FLG_Y;WP1_FLG_N;WP2_FLG_Y;WP2_FLG_N;SYS_SYMPTOM;PART_ SYMPTOM;EXAM_DATE;SPECIMEN_TYPE;LABPOSITIVE_Y;LABPOSITIVE_N;PATHOGEN_CODE;WBC_NUM;INF_NOTE;
        this.setValueForParm("RE_NO;REGISTER_DATE;REGISTER_CODE;CASE_NO;IPD_NO;MR_NO;DEPT_CODE;STATION_CODE;"
                                     + "IN_DEPT;OUT_DEPT;LASTICU_DATE;INICU_DATE;OUTICU_DATE;ICU_DAY;"
                                     + "ICD_CODE1;ICD_CODE2;ICD_CODE3;DIAG1;DIAG2;DIAG3;"
                                    // + "WP1_FLG;WP2_FLG;"
                                     + "SYS_SYMPTOM;PART_SYMPTOM;"
                                     + "EXAM_DATE;SPECIMEN_TYPE;" +
                                    // "LABPOSITIVE;" +
                                     "PATHOGEN_CODE;WBC_NUM;INF_NOTE",
                             parm);
        
        this.setValue("WBC_NUM", parm.getValue("WBC_NUM"));
        this.setInDeptValue(parm.getValue("IN_DEPT"), parm.getTimestamp("INICU_DATE"));
        this.setOutDeptValue(parm.getValue("OUT_DEPT"), parm.getTimestamp("OUTICU_DATE"));
        this.setValue("ICD_DESC1", StringUtil.getDesc("SYS_DIAGNOSIS", "ICD_CHN_DESC", "ICD_CODE='" + parm.getValue("ICD_CODE1") + "'"));
        this.setValue("ICD_DESC2", StringUtil.getDesc("SYS_DIAGNOSIS", "ICD_CHN_DESC", "ICD_CODE='" + parm.getValue("ICD_CODE2") + "'"));
        this.setValue("ICD_DESC3", StringUtil.getDesc("SYS_DIAGNOSIS", "ICD_CHN_DESC", "ICD_CODE='" + parm.getValue("ICD_CODE3") + "'"));
        this.setValue("DIAG1_DESC", StringUtil.getDesc("SYS_DIAGNOSIS", "ICD_CHN_DESC", "ICD_CODE='" + parm.getValue("DIAG1") + "'"));
        this.setValue("DIAG2_DESC", StringUtil.getDesc("SYS_DIAGNOSIS", "ICD_CHN_DESC", "ICD_CODE='" + parm.getValue("DIAG2") + "'"));
        this.setValue("DIAG3_DESC", StringUtil.getDesc("SYS_DIAGNOSIS", "ICD_CHN_DESC", "ICD_CODE='" + parm.getValue("DIAG3") + "'"));
        if (parm.getValue("WP1_FLG").equals("Y")) {//ת��ICU�����ܣ�
            this.callFunction("UI|WP1_FLG_Y|setSelected", true);
        } else if (parm.getValue("WP1_FLG").equals("N")) {//ת��ICU�������ܣ�
            this.callFunction("UI|WP1_FLG_N|setSelected", true);
        }
        if (parm.getValue("WP2_FLG").equals("Y")) {//ת��ICU�ڶ��죨���ܣ�
            this.callFunction("UI|WP2_FLG_Y|setSelected", true);
        } else if (parm.getValue("WP2_FLG").equals("N")) {//ת��ICU�ڶ��죨�����ܣ�
            this.callFunction("UI|WP2_FLG_N|setSelected", true);
        }
        if (parm.getValue("LABPOSITIVE").equals("Y")) {// ������:����
            this.callFunction("UI|LABPOSITIVE_Y|setSelected", true);
        } else if (parm.getValue("LABPOSITIVE").equals("N")) {// ������:����
            this.callFunction("UI|LABPOSITIVE_N|setSelected", true);
        }
        icuD = new INFICUD();
        icuD.onQueryByReNo(reNo);
//        int row = icuD.insertRow();
//        icuD.setItem(row, "RE_NO", "");
//        icuD.setItem(row, "OPT_USER", Operator.getID());
//        icuD.setItem(row, "OPT_DATE", icuD.getDBTime());
//        icuD.setItem(row, "OPT_TERM", Operator.getIP());
//        icuD.setActive(row, false);
        orderTable.setDataStore(icuD);
        orderTable.setDSValue();
    }

    /**
     * ��ʼ�����棨ͨ������ţ�
     */
    public void initUIByCase(String caseNo) {
        String sql =
                "SELECT A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.IN_DATE,FLOOR(MONTHS_BETWEEN(A.IN_DATE,B.BIRTH_DATE)/12)||'��' AS AGE,A.DEPT_CODE,A.STATION_CODE "
                        + "  FROM ADM_INP A, SYS_PATINFO B "
                        + " WHERE A.MR_NO = B.MR_NO AND A.CASE_NO = '#' ";
        sql = sql.replaceFirst("#", caseNo);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        this.setValueForParm("MR_NO;PAT_NAME;SEX_CODE;AGE;IN_DATE;DEPT_CODE", result, 0);
        onChooseStation("");
        this.setValueForParm("STATION_CODE", result, 0);
        initComboBoxByCase(caseNo);
        String diagSql =
                "SELECT A.ICD_CODE, B.ICD_CHN_DESC AS ICD_DESC FROM ADM_INPDIAG A, SYS_DIAGNOSIS B "
                        + " WHERE A.ICD_CODE = B.ICD_CODE AND A.MAINDIAG_FLG = 'Y' "
                        + "   AND A.IO_TYPE = 'M' AND A.CASE_NO = '#' ORDER BY A.SEQ_NO";
        diagSql = diagSql.replaceFirst("#", caseNo);
        TParm diagParm = new TParm(TJDODBTool.getInstance().select(diagSql));
        if (diagParm.getErrCode() < 0) {
            this.messageBox(diagParm.getErrText());
            return;
        }
        if (diagParm.getCount() >= 1) {
            this.setValue("ICD_CODE1", diagParm.getValue("ICD_CODE", 0));
            this.setValue("ICD_DESC1", diagParm.getValue("ICD_DESC", 0));
        }
        if (diagParm.getCount() >= 2) {
            this.setValue("ICD_CODE2", diagParm.getValue("ICD_CODE", 1));
            this.setValue("ICD_DESC2", diagParm.getValue("ICD_DESC", 1));
        }
        if (diagParm.getCount() >= 3) {
            this.setValue("ICD_CODE3", diagParm.getValue("ICD_CODE", 2));
            this.setValue("ICD_DESC3", diagParm.getValue("ICD_DESC", 2));
        }
        icuD = new INFICUD();
        icuD.onQuery();
//        int row = icuD.insertRow();
//        icuD.setItem(row, "RE_NO", "");
//        icuD.setItem(row, "SEQ", "");
//        icuD.setItem(row, "OPT_USER", Operator.getID());
//        icuD.setItem(row, "OPT_DATE", icuD.getDBTime());
//        icuD.setItem(row, "OPT_TERM", Operator.getIP());
//        icuD.setActive(row, false);
        orderTable.setDataStore(icuD);
        orderTable.setDSValue();
    }

    /**
     * ��ʼ��������
     * @param caseNo
     */
    public void initComboBox() {
        String DEFAULT_DEPT_SQL =
                "SELECT DEPT_CODE AS ID, DEPT_CHN_DESC AS NAME, PY1 FROM SYS_DEPT "
                        + " WHERE FINAL_FLG = 'Y' AND CLASSIFY = '0' AND IPD_FIT_FLG = 'Y' ORDER BY DEPT_CODE";
        IN_DEPT.setPopupMenuSQL(DEFAULT_DEPT_SQL);
        IN_DEPT.onQuery();
        IN_DEPT.getTablePopupMenu().setWidth(666);
        IN_DEPT.getTablePopupMenu().setHeader("����,100;����,120");
        IN_DEPT.getComboPopupMenu().setSize(229, 250);
        IN_DEPT.popupMenuShowData();
        OUT_DEPT.setPopupMenuSQL(DEFAULT_DEPT_SQL);
        OUT_DEPT.onQuery();
        OUT_DEPT.getTablePopupMenu().setHeader("����,100;����,120");
        OUT_DEPT.getComboPopupMenu().setSize(229, 250);
        OUT_DEPT.popupMenuShowData();
    }

    /**
     * ��ʼ��������
     * @param caseNo
     */
    public void initComboBoxByCase(String caseNo) {
        String INIT_IN_DEPT_SQL =
                "SELECT C.DEPT_CHN_DESC AS NAME, TO_DATE( A.IN_DATE, 'YYYYMMDD HH24MISS') AS IN_DATE, "
                        + " D.DEPT_CHN_DESC AS OUT_DEPT, B.OUT_DATE, A.IN_DEPT_CODE, B.OUT_DEPT_CODE "
                        + "  FROM ADM_TRANS_LOG A, ADM_TRANS_LOG B, SYS_DEPT C, SYS_DEPT D "
                        + " WHERE A.CASE_NO = B.CASE_NO(+)           "
                        + "   AND A.OUT_DATE < B.OUT_DATE(+)         "
                        + "   AND A.OUT_DEPT_CODE = B.IN_DEPT_CODE(+) "
                        + "   AND A.IN_DEPT_CODE = C.DEPT_CODE(+)  "
                        + "   AND B.OUT_DEPT_CODE = D.DEPT_CODE(+) "
                        + "   AND A.PSF_KIND <> 'OUT' AND A.CASE_NO = '#'";
        String INIT_OUT_DEPT_SQL =
                "SELECT D.DEPT_CHN_DESC AS NAME, B.OUT_DATE, C.DEPT_CHN_DESC AS IN_DEPT, "
                        + " TO_DATE( A.IN_DATE, 'YYYYMMDD HH24MISS') AS IN_DATE, A.IN_DEPT_CODE, B.OUT_DEPT_CODE "
                        + "  FROM ADM_TRANS_LOG A, ADM_TRANS_LOG B, SYS_DEPT C, SYS_DEPT D "
                        + " WHERE A.CASE_NO = B.CASE_NO(+)           "
                        + "   AND A.OUT_DATE < B.OUT_DATE(+)         "
                        + "   AND A.OUT_DEPT_CODE = B.IN_DEPT_CODE(+) "
                        + "   AND A.IN_DEPT_CODE = C.DEPT_CODE(+)  "
                        + "   AND B.OUT_DEPT_CODE = D.DEPT_CODE(+) "
                        + "   AND A.PSF_KIND <> 'OUT' AND A.CASE_NO = '#'";
        INIT_IN_DEPT_SQL = INIT_IN_DEPT_SQL.replaceFirst("#", caseNo);
        IN_DEPT.setPopupMenuSQL(INIT_IN_DEPT_SQL);
        IN_DEPT.onQuery();
        IN_DEPT.getTablePopupMenu().setHeader("��Դ����,120;ת��ʱ��,140,Timestamp,yyyy/MM/dd HH:mm:ss;ȥ�����,120;ת��ʱ��,140,Timestamp,yyyy/MM/dd HH:mm:ss");
        IN_DEPT.getComboPopupMenu().setSize(530, 250);
        IN_DEPT.popupMenuShowData();
        INIT_OUT_DEPT_SQL = INIT_OUT_DEPT_SQL.replaceFirst("#", caseNo);
        OUT_DEPT.setPopupMenuSQL(INIT_OUT_DEPT_SQL);
        OUT_DEPT.onQuery();
        OUT_DEPT.getTablePopupMenu().setHeader("ȥ�����,120;ת��ʱ��,140,Timestamp,yyyy/MM/dd HH:mm:ss;��Դ����,120;ת��ʱ��,140,Timestamp,yyyy/MM/dd HH:mm:ss");
        OUT_DEPT.getComboPopupMenu().setSize(530, 250);
        OUT_DEPT.popupMenuShowData();
    }

    /**
     * ����/ֹͣ���
     */
    public void enableUI(boolean flag) {
        String itemStr =
                "MR_NO;PAT_NAME;SEX_CODE;IN_DATE;DEPT_CODE;STATION_CODE;LASTICU_DATE;"
                        + "IN_DEPT;INICU_DATE;OUT_DEPT;OUTICU_DATE;"
                        + "ICD_DESC1;ICD_DESC2;ICD_DESC3;DIAG1_DESC;DIAG2_DESC;DIAG3_DESC;"
                        + "WP1_FLG_Y;WP1_FLG_N;WP2_FLG_Y;WP2_FLG_N;SYS_SYMPTOM;PART_SYMPTOM;"
                        + "EXAM_DATE;SPECIMEN_TYPE;LABPOSITIVE_Y;LABPOSITIVE_N;PATHOGEN_CODE;WBC_NUM;INF_NOTE;"
                        + "tButton_0;tButton_1;tButton_2";
        String[] items = itemStr.split(";");
        for (int i = 0; i < items.length; i++) {
            this.callFunction("UI|" + items[i] + "|setEnabled", flag);
        }
    }
    
    /**
     * �����Ų�ѯ
     */
    public void onMrNo() {
        String mrNo = "";
        String caseNo = "";
        mrNo = this.getValueString("MR_NO").trim();
        if (mrNo.equals("")) {
            return;
        } else {
            mrNo = PatTool.getInstance().checkMrno(mrNo);
            MR_NO = mrNo;
        }
        String sql =
                "SELECT A.CASE_NO,A.IPD_NO,B.PAT_NAME,B.SEX_CODE,FLOOR(MONTHS_BETWEEN(SYSDATE,B.BIRTH_DATE)/12) AS AGE "
                        + "  FROM ADM_INP A, SYS_PATINFO B"
                        + " WHERE A.MR_NO = B.MR_NO "
                        + "  AND A.CANCEL_FLG <> 'Y'"
                        + "  AND B.MR_NO = '#'      "
                        + "ORDER BY CASE_NO DESC";
        sql = sql.replaceFirst("#", mrNo);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("��þ�����Ϣʧ��");
            return;
        }
        if (result.getCount() < 1) {
            this.messageBox("�ò���δס��Ժ");
            return;
        } else {
            caseNo = result.getValue("CASE_NO", 0);
            CASE_NO = caseNo;
            IPD_NO = result.getValue("IPD_NO", 0);
            initUIByCase(caseNo);
        }
    }

    /**
     * ����
     */
    public void onNew(){
        onClear();
        int row = recordTable.addRow();
        recordTable.setSelectedRow(row);
        Timestamp now = SystemTool.getInstance().getDate();
        recordTable.setItem(row, "REGISTER_DATE", now);
        recordTable.setItem(row, "REGISTER_USER", Operator.getID());
        recordTable.setItem(row, "OPT_USER", Operator.getID());
        recordTable.setItem(row, "OPT_DATE", now);
        recordTable.setItem(row, "OPT_TERM", Operator.getIP());
        regDate = now;
        regUser = Operator.getID();
        this.setValue("MR_NO", MR_NO);
        onMrNo();
        enableUI(true);
    }

    /**
     * ����
     */
    public void onSave() {
        TParm parm =
                getParmForTag("MR_NO;PAT_NAME;SEX_CODE;AGE;IN_DATE:Timestamp;DEPT_CODE;STATION_CODE;LASTICU_DATE:Timestamp;"
                        + "IN_DEPT;INICU_DATE:Timestamp;OUT_DEPT;OUTICU_DATE:Timestamp;"
                        + "ICD_DESC1;ICD_DESC2;ICD_DESC3;ICD_CODE1;ICD_CODE2;ICD_CODE3;"
                        + "DIAG1_DESC;DIAG2_DESC;DIAG3_DESC;DIAG1;DIAG2;DIAG3;"
                        + "WP1_FLG_Y;WP1_FLG_N;WP2_FLG_Y;WP2_FLG_N;SYS_SYMPTOM;PART_SYMPTOM;"
                        + "EXAM_DATE:Timestamp;SPECIMEN_TYPE;LABPOSITIVE_Y;LABPOSITIVE_N;PATHOGEN_CODE;WBC_NUM;INF_NOTE");
        String mrNo = parm.getValue("MR_NO");
        mrNo = PatTool.getInstance().checkMrno(mrNo);// �����Ų��볤��
        parm.setData("MR_NO", mrNo);
        parm.setData("IN_DEPT", this.getInDeptValue());
        parm.setData("OUT_DEPT", this.getOutDeptValue());
        if (((TRadioButton) this.getComponent("WP1_FLG_Y")).isSelected()) {// ת��ICU������
            parm.setData("WP1_FLG", "Y");
        } else   if (((TRadioButton) this.getComponent("WP1_FLG_N")).isSelected()) {//ת��ICU��������
            parm.setData("WP1_FLG", "N");
        }else {
            parm.setData("WP1_FLG", "");
        }
        if (((TRadioButton) this.getComponent("WP2_FLG_Y")).isSelected()) {//ת��ICU�ڶ��죺����
            parm.setData("WP2_FLG", "Y");
        } else   if (((TRadioButton) this.getComponent("WP2_FLG_N")).isSelected()) {//ת��ICU�ڶ��죺������
            parm.setData("WP2_FLG", "N");
        }else {
            parm.setData("WP2_FLG", "");
        }
        if (((TRadioButton) this.getComponent("LABPOSITIVE_Y")).isSelected()) {//������������
            parm.setData("LABPOSITIVE", "Y");
        } else   if (((TRadioButton) this.getComponent("LABPOSITIVE_N")).isSelected()) {//������������
            parm.setData("LABPOSITIVE", "N");
        }else {
            parm.setData("LABPOSITIVE", "");
        }
        if (!checkUIData(parm)) {//���������Ч��
            return;
        }
        parm.setData("RE_NO", RE_NO);
        parm.setData("REGISTER_DATE", regDate);
        parm.setData("REGISTER_USER", regUser);
        parm.setData("CASE_NO", CASE_NO);
        parm.setData("IPD_NO", IPD_NO);
        int row = -1;
        if (NEW_FLG == true) {// �½�
            this.messageBox("�½�");
            icuM = new INFICUM();
            icuM.onQuery();
            row = icuM.insertRow();
            icuM.initData(row, parm);
        } else {// ����
            this.messageBox("����");
            row = icuM.onQueryByReNo(RE_NO);
            icuM.updateData(row, parm);
        }  
        String[] sql = icuM.getUpdateSQL();
        if (orderTable.getRowCount() > 0) {
            for (int i = orderTable.getRowCount() - 1; i >= 0; i--) {
                if (orderTable.getItemString(i, "ORDER_DESC").equals("")) {
                    orderTable.removeRow(i);
                }
            }
        }
        for (int i = 0; i < icuD.rowCount(); i++) {
            if (!icuD.getItemString(i, "ORDER_DESC").equals("")) {
                icuD.setActive(i, true);
            }
        }
        for (int i = 0; i < icuD.getModifiedRows().length; i++) {
            icuD.setItem(icuD.getModifiedRows()[i], "RE_NO", icuM.getItemData(row, "RE_NO"));
        }
        sql = StringTool.copyArray(sql, icuD.getUpdateSQL());
        TParm inParm = new TParm();
        Map<String, String[]> inMap = new HashMap<String, String[]>();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result = TIOM_AppServer.executeAction("action.inf.InfAction", "onSave", inParm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        } else {
            if (NEW_FLG == true) {// �½�
                this.messageBox("P0002");// �����ɹ�
            } else {
                this.messageBox("P0001");// ����ɹ�
            }
        }
    }
    /**
     * �������������Ч��
     */
    public boolean checkUIData(TParm parm) {
        if (parm.getValue("MR_NO").equals("")) {
            this.messageBox("����д������");
            return false;
        }
        if (parm.getValue("PAT_NAME").equals("")) {
            this.messageBox("����д��������");
            return false;
        }
        if (parm.getValue("SEX_CODE").equals("")) {
            this.messageBox("��ѡ���Ա�");
            return false;
        }
        if (parm.getData("IN_DATE") == null
                || parm.getData("IN_DATE").equals(new TNull(Timestamp.class))) {
            this.messageBox("����д����");
            return false;
        }
        if (parm.getValue("DEPT_CODE").equals("")) {
            this.messageBox("��ѡ�����");
            return false;
        }
        if (parm.getValue("STATION_CODE").equals("")) {
            this.messageBox("��ѡ����");
            return false;
        }
//        if (parm.getData("LASTICU_DATE") == null
//                || parm.getData("LASTICU_DATE").equals(new TNull(Timestamp.class))) {
//            this.messageBox("����д�ϴ�ICUʱ��");
//            return false;
//        }
        if (parm.getValue("IN_DEPT").equals("")) {
            this.messageBox("��ѡ����Դ����");
            return false;
        }
        if (parm.getData("INICU_DATE") == null
                || parm.getData("INICU_DATE").equals(new TNull(Timestamp.class))) {
            this.messageBox("����дת��ʱ��");
            return false;
        }
        if (parm.getValue("OUT_DEPT").equals("")) {
            this.messageBox("����дȥ�����");
            return false;
        }
        if (parm.getData("OUTICU_DATE") == null
                || parm.getData("OUTICU_DATE").equals(new TNull(Timestamp.class))) {
            this.messageBox("����дת��ʱ��");
            return false;
        }
        
        
        if (parm.getValue("SYS_SYMPTOM").equals("")) {
            this.messageBox("����дϵͳ֢״");
            return false;
        }
//        if (parm.getValue("PART_SYMPTOM").equals("")) {
//            this.messageBox("����д�ֲ�֢״");
//            return false;
//        }
  
        if (parm.getData("EXAM_DATE") == null
                || parm.getData("EXAM_DATE").equals(new TNull(Timestamp.class))) {
            this.messageBox("����д�ͼ�����");
            return false;
        }
        if (parm.getValue("SPECIMEN_TYPE").equals("")) {
            this.messageBox("��ѡ��걾����");
            return false;
        }
        if (parm.getValue("PATHOGEN_CODE").equals("")) {
            this.messageBox("����д��ԭ������");
            return false;
        }
//        if (parm.getValue("WBC_NUM").equals("")) {
//            this.messageBox("����д���ܰ�ϸ����");
//            return false;
//        }
//        if (parm.getValue("INF_NOTE").equals("")) {
//            this.messageBox("����д�пؼ��");
//            return false;
//        }
        return true;
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        if (NEW_FLG == true) {
            return;
        }
        String delDSql = "DELETE FROM INF_ICURGSD WHERE RE_NO='#'";
        String delMSql = "DELETE FROM INF_ICURGSM WHERE RE_NO='#'";
        if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 2) == 0) {
            String[] sql = new String[]{delDSql.replaceFirst("#", RE_NO) };
            sql = StringTool.copyArray(sql, new String[]{delMSql.replaceFirst("#", RE_NO) });
            TParm inParm = new TParm();
            Map<String, String[]> inMap = new HashMap<String, String[]>();
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm result = TIOM_AppServer.executeAction("action.inf.InfAction", "onSave", inParm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            onClear();
            this.messageBox("P0003");// ɾ���ɹ�
        }
    }

    /**
     * ��ӡҽ��
     */
    public void onPrint() {
        TParm printParm = new TParm();
        printParm
                .setGroupData(
                              "Data",
                              getTextParm(
                                          "MR_NO,PAT_NAME,SEX_CODE,AGE,IN_DATE,DEPT_CODE,STATION_CODE,LASTICU_DATE,"
                                                  + "IN_DEPT,INICU_DATE,OUT_DEPT,OUTICU_DATE,"
                                                  + "ICD_DESC1,ICD_DESC2,ICD_DESC3,ICD_CODE1,ICD_CODE2,ICD_CODE3,"
                                                  + "DIAG1_DESC,DIAG2_DESC,DIAG3_DESC,DIAG1,DIAG2,DIAG3,"
                                                  + "WP1_FLG_Y,WP1_FLG_N,WP2_FLG_Y,WP2_FLG_N,SYS_SYMPTOM,PART_SYMPTOM,"
                                                  + "EXAM_DATE,SPECIMEN_TYPE,LABPOSITIVE_Y,LABPOSITIVE_N,PATHOGEN_CODE,WBC_NUM,INF_NOTE")
                                      .getGroupData("RETURN"));
        String mrNo = printParm.getValue("MR_NO");
        mrNo = PatTool.getInstance().checkMrno(mrNo);// �����Ų��볤��
        printParm.setData("MR_NO", mrNo);
        printParm.setData("SEX_CODE", ((TComboBox) this.getComponent("SEX_CODE")).getSelectedName());
        TParm patInfo = PatTool.getInstance().getInfoForMrno(mrNo).getRow(0);
        printParm.setData("BIRTH_DATE", patInfo.getData("BIRTH_DATE"));
        Timestamp now = SystemTool.getInstance().getDate();
        String[] ageStr = StringTool.CountAgeByTimestamp(patInfo.getTimestamp("BIRTH_DATE"), now);
        printParm.setData("AGE", ageStr[2] + "��" + ageStr[1] + "��" + ageStr[0] + "��");
        printParm.setData("ICU_STAYTIME", StringTool.getDateDiffer( (Timestamp)this.getValue("OUTICU_DATE"),(Timestamp)this.getValue("INICU_DATE"))+"��");
        if (((TRadioButton) this.getComponent("WP1_FLG_Y")).isSelected()) {// ת��ICU������
            printParm.setData("WP1_FLG", "����");
        } else if (((TRadioButton) this.getComponent("WP1_FLG_N")).isSelected()) {// ת��ICU��������
            printParm.setData("WP1_FLG", "������");
        } else {
            printParm.setData("WP1_FLG", "");
        }
        if (((TRadioButton) this.getComponent("WP2_FLG_Y")).isSelected()) {// ת��ICU�ڶ��죺����
            printParm.setData("WP2_FLG", "����");
        } else if (((TRadioButton) this.getComponent("WP2_FLG_N")).isSelected()) {// ת��ICU�ڶ��죺������
            printParm.setData("WP2_FLG", "������");
        } else {
            printParm.setData("WP2_FLG", "");
        }
        if (((TRadioButton) this.getComponent("LABPOSITIVE_Y")).isSelected()) {// ������������
            printParm.setData("LABPOSITIVE", "����");
        } else if (((TRadioButton) this.getComponent("LABPOSITIVE_N")).isSelected()) {// ������������
            printParm.setData("LABPOSITIVE", "����");
        } else {
            printParm.setData("LABPOSITIVE", "");
        }  
        if (!checkUIData(printParm)) {// ���������Ч��
            return;
        }
        printParm.setData("RE_NO", RE_NO);
        printParm.setData("HOSP_DESC", Operator.getHospitalCHNFullName());
        printParm.setData("DEPT_DESC", StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC",
                                                          "DEPT_CODE='#'".replaceFirst("#", Operator.getDept())));
        printParm.setData("USER_ID", Operator.getName());
        printParm.setData("Date", StringTool.getString(now, "yyyy-MM-dd"));
        TParm parm = orderTable.getShowParmValue();
        for (int i = 0; i < parm.getCount("INF_FLG"); i++) {
            if (parm.getValue("INF_FLG", i).equals("Y")) {
                parm.setData("INF_FLG", i, "��");
            } else {
                parm.setData("INF_FLG", i, "��");
            }
        }
        parm.addData("SYSTEM", "COLUMNS", "ORDER_KIND");
        parm.addData("SYSTEM", "COLUMNS", "ORDER_QTY");
        parm.addData("SYSTEM", "COLUMNS", "PART");
        parm.addData("SYSTEM", "COLUMNS", "IN_DATE");
        parm.addData("SYSTEM", "COLUMNS", "OUT_DATE");
        parm.addData("SYSTEM", "COLUMNS", "INVS_CODE");
        parm.addData("SYSTEM", "COLUMNS", "IN_PORT");
        parm.addData("SYSTEM", "COLUMNS", "INF_FLG");
        parm.addData("SYSTEM", "COLUMNS", "INF_DATE");
        printParm.setData("TABLE", parm.getData());
        openPrintDialog("%ROOT%\\config\\prt\\INF\\INFICUReport.jhw", printParm, false);
    }

    /**
     * ���
     */
    public void onClear() {
        recordTable.retrieve();
        recordTable.clearSelection();
        regDate = null;
        regUser = "";
        NEW_FLG = true;// ������ǣ�true����;false���£�
        RE_NO = "";
        if (!isDeliver) {
            MR_NO = "";
            CASE_NO = "";
            IPD_NO = "";
        }
        clearValue("MR_NO;PAT_NAME;SEX_CODE;AGE;IN_DATE;DEPT_CODE;STATION_CODE;LASTICU_DATE;"
                + "IN_DEPT;INICU_DATE;OUT_DEPT;OUTICU_DATE;"
                + "ICD_DESC1;ICD_DESC2;ICD_DESC3;ICD_CODE1;ICD_CODE2;ICD_CODE3;"
                + "DIAG1_DESC;DIAG2_DESC;DIAG3_DESC;DIAG1;DIAG2;DIAG3;"
                + "WP1_FLG_Y;WP1_FLG_N;WP2_FLG_Y;WP2_FLG_N;SYS_SYMPTOM;PART_SYMPTOM;"
                + "EXAM_DATE;SPECIMEN_TYPE;LABPOSITIVE_Y;LABPOSITIVE_N;PATHOGEN_CODE;WBC_NUM;INF_NOTE");
        this.callFunction("UI|WP1_FLG_Y|setSelected", false);
        this.callFunction("UI|WP1_FLG_N|setSelected", false);
        this.callFunction("UI|WP2_FLG_Y|setSelected", false);
        this.callFunction("UI|WP2_FLG_N|setSelected", false);
        this.callFunction("UI|LABPOSITIVE_Y|setSelected", false);
        this.callFunction("UI|LABPOSITIVE_N|setSelected", false);
//        orderTable.setParmValue(new TParm());
//        int row = orderTable.addRow();
//        orderTable.setSelectedRow(row);
        Timestamp now = SystemTool.getInstance().getDate();
//        orderTable.setItem(row, "RE_NO", "");
//        orderTable.setItem(row, "SEQ", "");
//        orderTable.setItem(row, "OPT_USER", Operator.getID());
//        orderTable.setItem(row, "OPT_DATE", now);
//        orderTable.setItem(row, "OPT_TERM", Operator.getIP());
        icuD = new INFICUD();
        icuD.onQuery();
//        int row = icuD.insertRow();
//        icuD.setItem(row, "RE_NO", "");
//        icuD.setItem(row, "SEQ", "");
//        icuD.setItem(row, "OPT_USER", Operator.getID());
//        icuD.setItem(row, "OPT_DATE", icuD.getDBTime());
//        icuD.setItem(row, "OPT_TERM", Operator.getIP());
//        icuD.setActive(row, false);
        orderTable.setDataStore(icuD);
        orderTable.setDSValue();
        this.setValue("IN_DATE", now);
        this.setValue("LASTICU_DATE", now);
        this.setValue("INICU_DATE", now);
        this.setValue("OUTICU_DATE", now);
        this.setValue("EXAM_DATE", now);
        initComboBox();
        enableUI(false);
    }

    /**
     * ˫���¼�
     * @param row
     */
    public void onTableDoubleClicked(int row) {
        recordTable.acceptText();
        onClear();
        TDS tds = (TDS) recordTable.getDataStore();
//        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
//        TParm parm = recordTable.getParmValue();
//        int row = recordTable.getSelectedRow();
        TParm parm = tds.getRowParm(row);
        String reNo = parm.getValue("RE_NO");
        regDate =  parm.getTimestamp("REGISTER_DATE") ;    
        regUser = parm.getValue("REGISTER_USER")  ;   
        NEW_FLG = false;
        RE_NO = reNo;
        MR_NO =  parm.getValue("MR_NO")  ; 
        CASE_NO = parm.getValue("CASE_NO") ;   
        IPD_NO =  parm.getValue("IPD_NO") ;  
        writeUIByReNo(reNo);
        enableUI(true);
    }
    
    /**
     * ����ѡ���¼�
     */
    public void onChooseStation(String deptCode) {
        this.clearValue("STATION_CODE");
//        String deptCode = (String) this.callFunction("UI|DEPT_CODE|getValue");
        TTextFormat stationCode = (TTextFormat) this.getComponent("STATION_CODE");
        String sql =
                "SELECT STATION_CODE AS ID,STATION_DESC AS NAME,PY1 FROM SYS_STATION WHERE 1=1 # ORDER BY SEQ,STATION_CODE";
        if (deptCode.equals("")) {
            sql = sql.replaceFirst("#", "");
        } else {
            sql = sql.replaceFirst("#", " AND DEPT_CODE = '" + deptCode + "' ");
        }
        stationCode.setPopupMenuSQL(sql);
        stationCode.onQuery();
    }
    
    /**
     * ��Դ�����������¼�
     */
   public void  onInDeptAction(){
        TParm parm = IN_DEPT.getPopupMenuData();
        if (parm.getNames().length < 3) {// ����ֻ�����еĻ�
            return;
        }
        int row = IN_DEPT.getTablePopupMenu().getSelectedRow();
        Timestamp inDate = parm.getTimestamp("IN_DATE", row);
        this.setValue("INICU_DATE", inDate);
        String outDeptDesc = parm.getValue("OUT_DEPT", row);
//        String outDeptCode = parm.getValue("OUT_DEPT_CODE", row);
        OUT_DEPT.getTablePopupMenu().setSelectedRow(row);
        Timestamp outDate = parm.getTimestamp("OUT_DATE", row);
        this.setValue("OUT_DEPT", outDeptDesc);
        this.setValue("OUTICU_DATE", outDate);
    }
   
    /**
     * ȥ������������¼�
     */
    public void onOutDeptAction() {
        TParm parm = OUT_DEPT.getPopupMenuData();
        int row = OUT_DEPT.getTablePopupMenu().getSelectedRow();
        Timestamp outDate = parm.getTimestamp("OUT_DATE", row);
        this.setValue("OUTICU_DATE", outDate);
        String inDeptDesc = parm.getValue("IN_DEPT", row);
//        String inDeptCode = parm.getValue("IN_DEPT_CODE", row);
        IN_DEPT.getTablePopupMenu().setSelectedRow(row);
        Timestamp inDate = parm.getTimestamp("IN_DATE", row);
        this.setValue("IN_DEPT", inDeptDesc);
        this.setValue("INICU_DATE", inDate);
    }
   
    /**
     * �����Դ����value
     */
    public String getInDeptValue() {
        TParm parm = IN_DEPT.getPopupMenuData();
        if (parm.getNames().length < 3) {// ����ֻ�����еĻ�
            return this.getValueString("IN_DEPT");
        }
        int row = IN_DEPT.getTablePopupMenu().getSelectedRow();
        return parm.getValue("IN_DEPT_CODE", row);
    }
    
    /**
     * �����Դ����value
     */
    public String getOutDeptValue() {
        TParm parm = OUT_DEPT.getPopupMenuData();
        if (parm.getNames().length < 3) {// ����ֻ�����еĻ�
            return this.getValueString("OUT_DEPT");
        }
        int row = OUT_DEPT.getTablePopupMenu().getSelectedRow();
        return parm.getValue("OUT_DEPT_CODE", row);
    }
    
    /**
     * ������Դ��������������
     * @param deptCode
     * @param inDate
     */
    public void setInDeptValue(String deptCode, Timestamp inDate) {
        TParm parm = IN_DEPT.getPopupMenuData();
        if (parm.getNames().length < 3) {// ����ֻ�����еĻ�
            this.setValue("IN_DEPT", deptCode);
        }
        int i = 0;
        for (; i < parm.getCount(); i++) {
            if (parm.getValue("IN_DEPT_CODE", i).equals(deptCode)
                    && StringTool.getString(parm.getTimestamp("IN_DATE", i), "yyyy/MM/dd HH:mm")
                            .equals(StringTool.getString(inDate, "yyyy/MM/dd HH:mm"))) {
                this.setText("IN_DEPT", parm.getValue("NAME", i));
                IN_DEPT.getTablePopupMenu().setSelectedRow(i);
                break;
            }
        }
        if (i == parm.getCount()) {
            this.setValue("IN_DEPT", deptCode);
        }
    }
    
    /**
     * ����ȥ���������������
     * @param deptCode
     * @param outDate
     */
    public void setOutDeptValue(String deptCode, Timestamp outDate) {
        TParm parm = OUT_DEPT.getPopupMenuData();
        if (parm.getNames().length < 3) {// ����ֻ�����еĻ�
            this.setValue("OUT_DEPT", deptCode);
        }
        int i = 0;
        for (; i < parm.getCount(); i++) {
            if (parm.getValue("OUT_DEPT_CODE", i).equals(deptCode)
                    && StringTool.getString(parm.getTimestamp("OUT_DATE", i), "yyyy/MM/dd HH:mm")
                            .equals(StringTool.getString(outDate, "yyyy/MM/dd HH:mm"))) {
                this.setText("OUT_DEPT", parm.getValue("NAME", i));
                OUT_DEPT.getTablePopupMenu().setSelectedRow(i);
                break;
            }
        }
        if (i == parm.getCount()) {
            this.setValue("OUT_DEPT", deptCode);
        }
    }
    
    /**
     * ��������򷵻�ֵ
     * @param tag
     * @param obj
     */
    public void popICDReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (tag.equals("ICD_DESC1")) {
            if (parm.getValue("ICD_CHN_DESC").equals(this.getValue("ICD_DESC1"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("ICD_DESC2"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("ICD_DESC3"))) {
                this.messageBox("�������ظ�");
                this.setValue(tag, "");
                return;
            } else {
                this.setValue(tag, parm.getValue("ICD_CHN_DESC"));
                this.setValue("ICD_CODE1", parm.getValue("ICD_CODE"));
            }
        } else if (tag.equals("ICD_DESC2")) {
            if (parm.getValue("ICD_CHN_DESC").equals(this.getValue("ICD_DESC2"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("ICD_DESC2"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("ICD_DESC2"))) {
                this.messageBox("�������ظ�");
                this.setValue(tag, "");
                return;
            } else {
                this.setValue(tag, parm.getValue("ICD_CHN_DESC"));
                this.setValue("ICD_CODE2", parm.getValue("ICD_CODE"));
            }
        } else if (tag.equals("ICD_DESC3")) {
            if (parm.getValue("ICD_CHN_DESC").equals(this.getValue("ICD_DESC3"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("ICD_DESC3"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("ICD_DESC3"))) {
                this.messageBox("�������ظ�");
                this.setValue(tag, "");
                return;
            } else {
                this.setValue(tag, parm.getValue("ICD_CHN_DESC"));
                this.setValue("ICD_CODE3", parm.getValue("ICD_CODE"));
            }
        } else if (tag.equals("DIAG1_DESC")) {
            if (parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG1_DESC"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG1_DESC"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG1_DESC"))) {
                this.messageBox("�������ظ�");
                this.setValue(tag, "");
                return;
            } else {
                this.setValue(tag, parm.getValue("ICD_CHN_DESC"));
                this.setValue("DIAG1", parm.getValue("ICD_CODE"));
            }
        } else if (tag.equals("DIAG2_DESC")) {
            if (parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG2_DESC"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG2_DESC"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG2_DESC"))) {
                this.messageBox("�������ظ�");
                this.setValue(tag, "");
                return;
            } else {
                this.setValue(tag, parm.getValue("ICD_CHN_DESC"));
                this.setValue("DIAG2", parm.getValue("ICD_CODE"));
            }
        } else if (tag.equals("DIAG3_DESC")) {
            if (parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG3_DESC"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG3_DESC"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG3_DESC"))) {
                this.messageBox("�������ظ�");
                this.setValue(tag, "");
                return;
            } else {
                this.setValue(tag, parm.getValue("ICD_CHN_DESC"));
                this.setValue("DIAG3", parm.getValue("ICD_CODE"));
            }
        } 
    }
    
    /**
     * ����ҽ��
     */
    public void onInitOrders(){
        if(CASE_NO.equals("")){
            return;
        }
        String sql =
                "SELECT B.ORDER_CODE, B.ORDER_DESC, B.ORD_SUPERVISION "
                        + "  FROM ODI_ORDER A, SYS_FEE B " + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                        + "   AND B.ORD_SUPERVISION IS NOT NULL " + "   AND A.CASE_NO = '#' "
                        + "ORDER BY A.ORDER_NO, A.ORDER_SEQ";
        sql = sql.replaceFirst("#", CASE_NO);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//        int row = icuD.rowCount();
//        if (icuD.getItemString(row - 1, "ORDER_DESC").equals("")) {
//            icuD.deleteRow(row - 1);
//        }
        for (int i = 0; i < result.getCount(); i++) {
            int row = icuD.insertRow();
            icuD.setItem(row, "RE_NO", "");
            icuD.setItem(row, "SEQ", icuD.getNextSeqNo());
            icuD.setItem(row, "ORDER_KIND", result.getValue("ORD_SUPERVISION", i));
            icuD.setItem(row, "ORDER_CODE", result.getValue("ORDER_CODE", i));
            icuD.setItem(row, "ORDER_DESC", result.getValue("ORDER_DESC", i));

            icuD.setItem(row, "OPT_USER", Operator.getID());///////////////////////////////////�ŵ����������ã���������������
            icuD.setItem(row, "OPT_DATE", icuD.getDBTime());
            icuD.setItem(row, "OPT_TERM", Operator.getIP());
            icuD.setActive(row, false);
            
        }
        orderTable.setDataStore(icuD);
        orderTable.setDSValue();
        orderTable.getTable().grabFocus();
        orderTable.setSelectedRow(orderTable.getRowCount() - 1);
        orderTable.setSelectedColumn(1);
    }
    
    /**
     * �������һ��
     */
    public void onNewRow() {
        if (CASE_NO.equals("")) {
            return;
        }
        if (orderTable.getRowCount() < 1
                || (orderTable.getRowCount() > 0 && !orderTable
                        .getItemString(orderTable.getRowCount() - 1, "ORDER_DESC").equals(""))) {
//            int row = orderTable.addRow();
//            orderTable.setSelectedRow(row);
//            Timestamp now = SystemTool.getInstance().getDate();
//            orderTable.setItem(row, "RE_NO", "");
//            orderTable.setItem(row, "SEQ", "");
//            orderTable.setItem(row, "OPT_USER", Operator.getID());
//            orderTable.setItem(row, "OPT_DATE", now);
//            orderTable.setItem(row, "OPT_TERM", Operator.getIP()); 
//            icuD = new INFICUD();
//            icuD.onQuery();
            int row = icuD.insertRow();
            icuD.setItem(row, "RE_NO", "");
            icuD.setItem(row, "SEQ", "");
            icuD.setItem(row, "OPT_USER", Operator.getID());
            icuD.setItem(row, "OPT_DATE", icuD.getDBTime());
            icuD.setItem(row, "OPT_TERM", Operator.getIP());
            icuD.setActive(row, false);
            orderTable.setDataStore(icuD);
            orderTable.setDSValue();
        }
    }

    /**
     * �ӱ��ɾ��һ��
     */
    public void onDeleteRow() {
        if(CASE_NO.equals("")){
            return;
        }
        if (orderTable.getSelectedRow() < 0) {
            return;
        }
        String filter = icuD.getFilter();
        int row = -1;
        row = orderTable.getSelectedRow();
        icuD.deleteRow(row);// ɾ����
//        if (icuD.rowCount() < 1) {
//            row = icuD.insertRow();
//            icuD.setItem(row, "RE_NO", "");
//            icuD.setItem(row, "SEQ", "");
//            icuD.setItem(row, "OPT_USER", Operator.getID());
//            icuD.setItem(row, "OPT_DATE", icuD.getDBTime());
//            icuD.setItem(row, "OPT_TERM", Operator.getIP());
//            icuD.setActive(row, false);
//        }
        icuD.setFilter(filter);
        icuD.filter();
        orderTable.setDSValue();
    }
    
    /**
     * ���SYS_FEE��������
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onOrderEditComponent(Component com, int row, int column) {
        // �����ǰ�к�
        column = orderTable.getColumnModel().getColumnIndex(column);
        String columnName = orderTable.getParmMap(column);
        if (!"ORDER_DESC".equalsIgnoreCase(columnName)) {
            return;
        }
        if (!(com instanceof TTextField)) {
            return;
        }
        if (!StringUtil.isNullString(icuD.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");

        // ��table�ϵ���text����sys_fee��������
        textfield.setPopupMenuParameter("ORDER",
                                        getConfigParm()
                                                .newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
                                        parm);
        // ����text���ӽ���sys_fee�������ڵĻش�ֵ
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popOrderReturn");
    }

    /**
     * ҽ�������򷵻�ֵ
     * 
     * @param tag
     * @param obj
     */
    public void popOrderReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        int row = orderTable.getSelectedRow();
        if (!StringUtil.isNullString(orderTable.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        orderTable.acceptText();
        icuD.setItem(row, "RE_NO", "");
        icuD.setItem(row, "SEQ", icuD.getNextSeqNo());
        icuD.setItem(row, "ORDER_KIND", parm.getData("ORD_SUPERVISION"));
        icuD.setItem(row, "ORDER_CODE", parm.getData("ORDER_CODE"));
        icuD.setItem(row, "ORDER_DESC", parm.getData("ORDER_DESC"));
//        icuD.setItem(row, "ORDER_QTY", parm.getData("GOODS_DESC"));
//        icuD.setItem(row, "PART", parm.getData("DOSE_CODE", 0));
//        icuD.setItem(row, "IN_DATE", parm.getData("MAN_CHN_DESC", 0));
//        icuD.setItem(row, "OUT_DATE", parm.getData("FREQ_CODE", 0));
//        icuD.setItem(row, "INVS_CODE", parm.getData("ROUTE_CODE", 0));
//        icuD.setItem(row, "IN_PORT", parm.getData("UNIT_CODE", 0));
//        icuD.setItem(row, "INF_FLG", parm.getDouble("MEDI_QTY", 0));
//        icuD.setItem(row, "INF_DATE", parm.getData("MEDI_UNIT", 0));
        icuD.setItem(row, "OPT_USER", Operator.getID());
        icuD.setItem(row, "OPT_DATE", icuD.getDBTime());
        icuD.setItem(row, "OPT_TERM", Operator.getIP());
        icuD.setActive(row, true);
//        row = icuD.insertRow();
//        icuD.setItem(row, "RE_NO", "");
//        icuD.setItem(row, "SEQ", "");// ����ҩƷ
//        icuD.setItem(row, "OPT_USER", Operator.getID());
//        icuD.setItem(row, "OPT_DATE", icuD.getDBTime());
//        icuD.setItem(row, "OPT_TERM", Operator.getIP());
//        icuD.setActive(row, false);
        orderTable.setDSValue();
        orderTable.getTable().grabFocus();
        orderTable.setSelectedRow(orderTable.getRowCount() - 1);
        orderTable.setSelectedColumn(2);
    }

}
