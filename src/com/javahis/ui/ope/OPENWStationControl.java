package com.javahis.ui.ope;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;
import javax.swing.SwingUtilities;
import jdo.bil.BILComparator;
import jdo.ope.OPETool;
import jdo.sys.PatTool;
import jdo.sys.SYSBedTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import device.PassDriver;


/**
 * <p>Title: ������ʿվ </p>
 *
 * <p>Description: ������ʿվ </p>
 *
 * <p>Copyright: Copyright (c) 2014 </p>
 *
 * <p>Company: Bluecore </p>
 *
 * @author wanglong 2014-7-1
 * @version 1.0
 */
public class OPENWStationControl extends TControl {
    /**
     * ��ǰ��ҳ��TAG
     */
    public String workPanelTag = "";
    private TTable table ;
    private BILComparator compare = new BILComparator();//�Ƚ����������ܣ�
	private boolean ascending = false; //���򣬷��������ܣ�
	private int sortColumn = -1;//�����У������ܣ�

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        if (this.getParameter() != null) {
            String paramStr = this.getParameter().toString();
            if ("OEI".indexOf(paramStr) == -1) {
                paramStr = "I";
            }
            this.setValue("ADM_TYPE", paramStr);
        }
        initUI();
        initData();
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    onQuery();
                }
                catch (Exception e) {}
            }
        });
    }

    /**
     * �����ʼ��
     */
    private void initUI() {
        table = (TTable) this.getComponent("Table");
        addSortListener(table);// table���������
        OpList opList = new OpList();
        DiagList diagList = new DiagList();
        table.addItem("OpList", opList);
        table.addItem("DiagList", diagList);
        callFunction("UI|Table|addEventListener", "Table->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
    }

    /**
     * ���ݳ�ʼ��
     */
    private void initData() {
        // ��ȡ��ǰʱ��
        Timestamp now = SystemTool.getInstance().getDate();
        String date = StringTool.getString(now, "yyyyMMdd");
        this.setValue("OP_START_DATE", StringTool.getTimestamp(date + "000000", "yyyyMMddhhmmss"));
        this.setValue("OP_END_DATE", StringTool.getTimestamp(date + "235959", "yyyyMMddhhmmss"));
        String roomSql = "SELECT * FROM OPE_IPROOM WHERE IP = '#'".replaceFirst("#", Operator.getIP());
        TParm roomParm = new TParm(TJDODBTool.getInstance().select(roomSql));
        if (roomParm.getCount() > 0) {
            this.setValue("ROOM_NO", roomParm.getValue("ROOM_NO", 0));
        }
        this.setValue("CIRCULE_USER", Operator.getID());
    }

    /**
     * ������ѡ���ѯ
     */
    public void onOpRoomQuery() {
        TParm parm = new TParm();
        if (this.getValueString("ADM_TYPE").equals("")) {
            this.messageBox("�ż�ס����Ϊ��");
            return;
        }
        parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
        if (this.getValue("OP_START_DATE") == null || this.getValue("OP_END_DATE") == null) {
            this.messageBox("ʱ�䲻��Ϊ��");
            return;
        }
        parm.setData("OP_START_DATE", StringTool.getString((Timestamp) this
                .getValue("OP_START_DATE"), "yyyyMMddHHmmss"));
        parm.setData("OP_END_DATE", StringTool.getString((Timestamp) this.getValue("OP_END_DATE"),
                                                         "yyyyMMddHHmmss"));
        if (!this.getValueString("ROOM_NO").equals("")) {
            parm.setData("ROOM_NO", this.getValueString("ROOM_NO"));
        }
        if (!this.getValueString("MR_NO").equals("")) {
            String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
            parm.setData("MR_NO", mrNo);
        }
        exeQuery(parm);
    }

    /**
     * �����Żس���ѯ
     */
    public void onMrNoQuery() {
        String mrNo = this.getValueString("MR_NO").trim();
        if (!mrNo.equals("")) {
            mrNo = PatTool.getInstance().checkMrno(mrNo);
            TParm sysPatInfo = PatTool.getInstance().getInfoForMrno(mrNo);
            if (sysPatInfo.getCount() > 0) {
                this.setValue("MR_NO", mrNo);// ������
                this.setValue("PAT_NAME", sysPatInfo.getValue("PAT_NAME", 0));// ����
            } else {
                return;
            }
        } else {
            return;
        }
        TParm parm = new TParm();
        if (this.getValueString("ADM_TYPE").equals("")) {
            this.messageBox("�ż�ס����Ϊ��");
            return;
        }
        parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
        if (this.getValue("OP_START_DATE") == null || this.getValue("OP_END_DATE") == null) {
            this.messageBox("ʱ�䲻��Ϊ��");
            return;
        }
        parm.setData("OP_START_DATE", StringTool.getString((Timestamp) this
                .getValue("OP_START_DATE"), "yyyyMMddHHmmss"));
        parm.setData("OP_END_DATE", StringTool.getString((Timestamp) this.getValue("OP_END_DATE"),
                                                         "yyyyMMddHHmmss"));
        if (!this.getValueString("ROOM_NO").equals("")) {
            parm.setData("ROOM_NO", this.getValueString("ROOM_NO"));
        }
        if (!this.getValueString("MR_NO").equals("")) {
            parm.setData("MR_NO", this.getValueString("MR_NO"));
        }
        exeQuery(parm);
    }
    
    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm=new TParm();
        if (this.getValueString("ADM_TYPE").equals("")) {
            this.messageBox("�ż�ס����Ϊ��");
            return;
        }
        parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
        if (this.getValue("OP_START_DATE") == null || this.getValue("OP_END_DATE") == null) {
            this.messageBox("ʱ�䲻��Ϊ��");
            return;
        }
        parm.setData("OP_START_DATE", StringTool.getString((Timestamp) this
                .getValue("OP_START_DATE"), "yyyyMMddHHmmss"));
        parm.setData("OP_END_DATE", StringTool.getString((Timestamp) this.getValue("OP_END_DATE"),
                                                         "yyyyMMddHHmmss"));
        if (!this.getValueString("ROOM_NO").equals("")) {
            parm.setData("ROOM_NO", this.getValueString("ROOM_NO"));
        }
        if (!this.getValueString("MR_NO").trim().equals("")) {
            String mrNo = this.getValueString("MR_NO").trim();
            mrNo = PatTool.getInstance().checkMrno(mrNo);
            TParm sysPatInfo = PatTool.getInstance().getInfoForMrno(mrNo);
            if (sysPatInfo.getCount() > 0) {
                this.setValue("MR_NO", mrNo);// ������
                this.setValue("PAT_NAME", sysPatInfo.getValue("PAT_NAME", 0));// ����
            }
            parm.setData("MR_NO", mrNo);
        }
        if (!this.getValueString("CIRCULE_USER").equals("")) {
            parm.setData("CIRCULE_USER", this.getValueString("CIRCULE_USER"));
        }
        if (!this.getValueString("OP_DEPT_CODE").equals("")) {
            parm.setData("OP_DEPT_CODE", this.getValueString("OP_DEPT_CODE"));
        }
        exeQuery(parm);
    }

    /**
     * ִ�в�ѯ
     */
    public void exeQuery(TParm parm) {
        String sql =
                "SELECT A.OP_DATE, A.ROOM_NO, A.MR_NO, A.CASE_NO, A.IPD_NO, B.PAT_NAME, "
                        + "       A.OP_CODE1 OP_CODE, A.DIAG_CODE1 DIAG_CODE, A.MAIN_SURGEON, A.ANA_USER1 ANA_USER, "
                        + "       A.URGBLADE_FLG, A.TF_FLG, A.TIME_NEED, A.STATE, A.APROVE_DATE, A.OPBOOK_SEQ, A.ADM_TYPE, A.OP_DEPT_CODE, "
                        + "       A.CIRCULE_USER1, A.CIRCULE_USER2, A.CIRCULE_USER3, A.CIRCULE_USER4 "
                        + "  FROM OPE_OPBOOK A, SYS_PATINFO B, ADM_INP C        "
                        + " WHERE A.MR_NO = B.MR_NO                  "
                        + "   AND A.CANCEL_FLG <> 'Y'                "
                        + "   AND A.STATE IN ('1', '2', '3', '4', '5', '6') "//0,����;1,�ų����;2,�ӻ���;3,�����ҽ���;4,�����ȴ�;5,������ʼ;6,����;7,��������;8,���ز���
                        + "   AND A.ADM_TYPE = '#'                   "
                        + "   AND A.CASE_NO = C.CASE_NO              "// wanglong add 20150408
                        + "   AND C.DS_DATE IS NULL                  "
                        + "   AND (C.CANCEL_FLG <> 'Y' OR C.CANCEL_FLG IS NULL) "
                        + "   AND A.OP_DATE BETWEEN TO_DATE( '#', 'YYYYMMDDHH24MISS') "
                        + "                     AND TO_DATE( '#', 'YYYYMMDDHH24MISS') "
                        + "   #   #   #    #    ORDER BY A.OP_DATE";
        sql = sql.replaceFirst("#", parm.getValue("ADM_TYPE"));
        sql = sql.replaceFirst("#", parm.getValue("OP_START_DATE"));
        sql = sql.replaceFirst("#", parm.getValue("OP_END_DATE"));
        if (!parm.getValue("ROOM_NO").equals("")) {
            sql =
                    sql.replaceFirst("#",
                                     " AND A.ROOM_NO='@' ".replaceFirst("@",
                                                                        parm.getValue("ROOM_NO")));
        } else {
            sql = sql.replaceFirst("#", " ");
        }
        if (!parm.getValue("MR_NO").equals("")) {
            sql =
                    sql.replaceFirst("#",
                                     " AND A.MR_NO='@' ".replaceFirst("@", parm.getValue("MR_NO")));
        } else {
            sql = sql.replaceFirst("#", " ");
        }
        if (!parm.getValue("CIRCULE_USER").equals("")) {
            sql =
                    sql.replaceFirst("#",
                                     " AND (A.CIRCULE_USER1='@' OR A.CIRCULE_USER2='@' OR A.CIRCULE_USER3='@' OR A.CIRCULE_USER4='@') "
                                             .replaceAll("@", parm.getValue("CIRCULE_USER")));
        } else {
            sql = sql.replaceFirst("#", " ");
        }
        if (!parm.getValue("OP_DEPT_CODE").equals("")) {
            sql =
                    sql.replaceFirst("#", " AND A.OP_DEPT_CODE='@' ".replaceFirst("@", parm
                            .getValue("OP_DEPT_CODE")));
        } else {
            sql = sql.replaceFirst("#", " ");
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("E0005");//ִ��ʧ��
            return;
        }
        if (result.getCount() < 1) {
            this.messageBox("E0008");// ��������
        }
        table.setParmValue(result);
    }
    
    /**
     * �����ų�
     */
    public void onAsg() {
        int row = table.getSelectedRow();// ѡ����
        if (row < 0) {
            return;
        }
        TParm data = table.getParmValue();
        String OPBOOK_SEQ = data.getValue("OPBOOK_SEQ", row);
        this.openDialog("%ROOT%/config/ope/OPEPersonnel.x", OPBOOK_SEQ);
    }

    /**
     * ������Ϣ
     */
    public void onOpInfo() {
        int row = table.getSelectedRow();// ѡ����
        TParm parmValue = table.getParmValue();
        String OPBOOK_SEQ = parmValue.getValue("OPBOOK_SEQ", row);
        TParm parm = new TParm();
        parm.setData("FLG", "update");
        parm.setData("OPBOOK_SEQ", OPBOOK_SEQ);
        parm.setData("ADM_TYPE", parmValue.getValue("ADM_TYPE", row));
        this.openDialog("%ROOT%/config/ope/OPEOpBook.x", parm);
    }

    /**
     * ������¼
     */
    public void onOpRecord() {
        int row = table.getSelectedRow();// ѡ����
        if (row < 0) {
            return;
        }
        TParm parm = new TParm();
        TParm parmValue = table.getParmValue();
        String OPBOOK_SEQ = parmValue.getValue("OPBOOK_SEQ", row);
        parm.setData("OPBOOK_SEQ", OPBOOK_SEQ);
        parm.setData("MR_NO", parmValue.getValue("MR_NO", row));
        parm.setData("ADM_TYPE", parmValue.getValue("ADM_TYPE", row));
        this.openDialog("%ROOT%/config/ope/OPEOpDetail.x", parm);
    }

    /**
     * ˫���¼�
     * 
     * @param row
     *            int
     */
    public void onTableDoubleClicked(int row) {
        if (row < 0) {
            return;
        }
        if (!OPETool.getInstance().isOpDept(Operator.getDept())) {
            this.messageBox("���������ҵ�¼����������в���");
            return;
        }
        TParm parmRow = table.getParmValue().getRow(row);
        this.setValue("ROOM_NO", parmRow.getValue("ROOM_NO"));
        this.setValue("MR_NO", parmRow.getValue("MR_NO"));
        this.setValue("IPD_NO", parmRow.getValue("IPD_NO"));
        this.setValue("PAT_NAME", parmRow.getValue("PAT_NAME"));
        if (!parmRow.getValue("CIRCULE_USER1").equals("")) {//Ѳ�ػ�ʿ
            this.setValue("CIRCULE_USER", parmRow.getValue("CIRCULE_USER1"));
        } else if (!parmRow.getValue("CIRCULE_USER2").equals("")) {
            this.setValue("CIRCULE_USER", parmRow.getValue("CIRCULE_USER2"));
        } else if (!parmRow.getValue("CIRCULE_USER3").equals("")) {
            this.setValue("CIRCULE_USER", parmRow.getValue("CIRCULE_USER3"));
        } else if (!parmRow.getValue("CIRCULE_USER4").equals("")) {
            this.setValue("CIRCULE_USER", parmRow.getValue("CIRCULE_USER4"));
        }
        this.setValue("OP_DEPT_CODE", parmRow.getValue("OP_DEPT_CODE"));
        String sql =
                "SELECT B.CLNCPATH_CODE, A.BED_NO_DESC, C.PAT_NAME, C.SEX_CODE, C.BIRTH_DATE, B.IN_DATE, B.DS_DATE, "
                        + "       D.ICD_CHN_DESC AS MAINDIAG, B.CTZ1_CODE, B.MR_NO, B.IPD_NO, B.TOTAL_AMT, B.TOTAL_BILPAY, "
                        + "       B.GREENPATH_VALUE, B.STATION_CODE, B.RED_SIGN, B.YELLOW_SIGN, B.STOP_BILL_FLG, A.BED_NO, "
                        + "       B.CTZ2_CODE, B.CTZ3_CODE, B.VS_DR_CODE, B.DEPT_CODE, B.HEIGHT, B.WEIGHT, B.CASE_NO, "
                        + "       B.CUR_AMT, C.POST_CODE, C.ADDRESS, C.COMPANY_DESC, C.CELL_PHONE, C.TEL_HOME, C.IDNO, C.PAT_NAME1, "
                        + "       B.NURSING_CLASS, B.PATIENT_STATUS, D.ICD_CODE, E.CHECK_FLG AS MRO_CHAT_FLG, A.ENG_DESC, "
                        + "       B.SERVICE_LEVEL, B.BILL_STATUS, B.DISE_CODE "
                        + "  FROM SYS_BED A, ADM_INP B, SYS_PATINFO C, SYS_DIAGNOSIS D, MRO_MRV_TECH E "
                        + " WHERE A.BED_NO = B.BED_NO(+)   "
                        + "   AND A.CASE_NO = B.CASE_NO(+) "
                        + "   AND A.MR_NO = B.MR_NO(+)     "
                        + "   AND A.MR_NO = C.MR_NO(+)     "
                        + "   AND A.ACTIVE_FLG = 'Y'       "
                        + "   AND A.CASE_NO = E.CASE_NO(+) "
                        + "   AND A.MR_NO = E.MR_NO(+)     "
                        + "   AND B.DS_DATE IS NULL        "// ��Ժ
                        + "   AND A.ALLO_FLG = 'Y'         "
                        + "   AND B.CANCEL_FLG <> 'Y'      "
                        + "   AND A.BED_STATUS = '1'       "
                        + "   AND B.REGION_CODE = 'H01'    "
                        + "   AND B.MAINDIAG = D.ICD_CODE(+) "
                        + "   AND B.CASE_NO = '#'              " + "ORDER BY B.CASE_NO DESC";
        sql = sql.replaceFirst("#", parmRow.getValue("CASE_NO"));
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("��ѯ�û���Ϣʧ��");
            return;
        }
        // �õ�Ԥ�����������������ʾ��ǿ��
        double rPrice = result.getDouble("CUR_AMT");
        // ��ɫ����
        double yellowPrice = result.getDouble("YELLOW_SIGN");
        if (rPrice <= yellowPrice) {
            if (this.messageBox("��ʾ��Ϣ Tips", "Ԥ�������㣡\n Paying insufficient balance gold!",
                                this.YES_NO_OPTION) != 0) return;
        }
        table.setVisible(false);
        TParm action = new TParm();
        action.setData("INW", "CASE_NO", result.getValue("CASE_NO", 0));
        action.setData("INW", "IPD_NO", result.getValue("IPD_NO", 0)); // סԺ��
        action.setData("INW", "MR_NO", result.getValue("MR_NO", 0)); // ������
        action.setData("INW", "CTZ1_CODE", result.getValue("CTZ1_CODE", 0)); // ���1
        action.setData("INW", "CTZ2_CODE", result.getValue("CTZ2_CODE", 0)); // ���2
        action.setData("INW", "CTZ3_CODE", result.getValue("CTZ3_CODE", 0)); // ���3
        action.setData("INW", "PAT_NAME", result.getValue("PAT_NAME", 0)); // ����
        action.setData("INW", "DEPT_CODE", result.getValue("DEPT_CODE", 0));// ����
        action.setData("INW", "STATION_CODE", result.getValue("STATION_CODE", 0)); // ����
        action.setData("INW", "BED_NO", result.getValue("BED_NO", 0)); // ����
        action.setData("INW", "VS_DR_CODE", result.getValue("VS_DR_CODE", 0));// ����ҽ��
        action.setData("INW", "ADM_DATE", result.getData("IN_DATE", 0)); // ��Ժʱ��
        action.setData("INW", "SAVE_FLG", true); // ����Ȩ��ע��
        boolean isICU = SYSBedTool.getInstance().checkIsICU(parmRow.getValue("CASE_NO"));
        action.setData("INW", "ICU_FLG", isICU); // ICU����ע��
        action.setData("INW", "CLNCPATH_CODE", result.getValue("CLNCPATH_CODE", 0));// �ٴ�·��
        action.setData("INW", "OPE_FLG", true); // ����ҽ��վע��/////////////////
        action.setData("INW", "OP_DEPT_CODE", parmRow.getValue("OP_DEPT_CODE"));// ��������/////////////////
        action.setData("INW", "OPBOOK_SEQ", parmRow.getValue("OPBOOK_SEQ"));// �������뵥��/////////////////
        ((TPanel) getComponent("PANEL")).addItem("INWSTATIONCHECK",
                                                 "%ROOT%\\config\\inw\\INWOrderCheckMain.x",
                                                 action, false);
        workPanelTag = "INWSTATIONCHECK";
        this.setUIEnabled(false);
    }

    /**
     * ����/���ý������
     * 
     * @param flag
     */
    public void setUIEnabled(boolean flag) {
        this.callFunction("UI|OP_START_DATE|setEnabled", flag);
        this.callFunction("UI|OP_END_DATE|setEnabled", flag);
        this.callFunction("UI|ROOM_NO|setEnabled", flag);
        this.callFunction("UI|MR_NO|setEnabled", flag);
        this.callFunction("UI|CIRCULE_USER|setEnabled", flag);
        this.callFunction("UI|OP_DEPT_CODE|setEnabled", flag);
    }

    /**
     * ��ʾ��ǰTOOLBAR
     */
    public void onShowWindowsEvent() {
        if (workPanelTag == null || workPanelTag.length() == 0) {
            // ��ʾUIshowTopMenu
            callFunction("UI|showTopMenu");
            return;
        }
        TPanel p = (TPanel) getComponent(workPanelTag);
        p.getControl().callFunction("onShowWindowsFunction");
    }
    
    /**
     * �رչ���ҳ��
     * 
     * @return boolean
     */
    public boolean onClosePanel() {
        TPanel p = (TPanel) getComponent("INWSTATIONCHECK");
        if (!p.getControl().onClosing()) return false;
        // �Ƴ���ǰ��UI
        callFunction("UI|PANEL|removeItem", "INWSTATIONCHECK");
        // �Ƴ���UIMenuBar
        callFunction("UI|removeChildMenuBar");
        // �Ƴ���UIToolBar
        callFunction("UI|removeChildToolBar");
        // ��ʾUIshowTopMenu
        callFunction("UI|showTopMenu");
        // �õ�TabbedPane�ؼ�
        // TTabbedPane tabPane = (TTabbedPane) this.callFunction("UI|TablePane|getThis");
        // ���Ա༭
        // tabPane.setEnabled(true);
        this.setUIEnabled(true);
        // ��ʾTABLE
        table.setVisible(true);
        return true;
    }
    
    /**
     * ���
     */
    public void onClear() {
        this.clearValue("ROOM_NO;MR_NO;PAT_NAME;CIRCULE_USER;OP_DEPT_CODE");
        table.setDSValue();
        initData();
    }
    
    /**
     * �õ���Ӧҩ��
     * 
     * @param stationCode
     *            String
     * @param deptCode
     *            String
     * @return String
     */
    public String getOrgCode(String stationCode, String deptCode) {
        TParm parm =
                new TParm(TJDODBTool.getInstance()
                        .select("SELECT ORG_CODE FROM SYS_STATION WHERE STATION_CODE='"
                                        + stationCode + "'"));
        if (parm.getCount() == 0) {
            return "";
        }
        return parm.getValue("ORG_CODE", 0);
    }

    /**
     * ��ʼ��������ҩ
     * 
     * @return boolean
     */
    public boolean initReasonbledMed() {
        try {
            if (PassDriver.init() != 1) {
                return false;
            }
            // ������ҩ��ʼ��
            if (PassDriver.PassInit(Operator.getName(), Operator.getDept(), 10) != 1) {
                return false;
            }
            // ������ҩ���Ʋ���
            if (PassDriver.PassSetControlParam(1, 2, 0, 2, 1) != 1) {
                return false;
            }
        }
        catch (UnsatisfiedLinkError e1) {
            e1.printStackTrace();
            return false;
        }
        catch (NoClassDefFoundError e2) {
            e2.printStackTrace();
            return false;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * ���CODE�滻���� ģ����ѯ���ڲ��ࣩ
     */
    public class DiagList extends TLabel {
        TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("ICD_CODE");
            Vector d = (Vector) parm.getData("ICD_CHN_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i)))
                    return "" + d.get(i);
            }
            return s;
        }
    }
    
    /**
     * ����CODE�滻���� ģ����ѯ���ڲ��ࣩ
     */
    public class OpList extends TLabel {
        TDataStore dataStore = new TDataStore();
        public OpList(){
            dataStore.setSQL("SELECT * FROM SYS_OPERATIONICD");
            dataStore.retrieve();
        }
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("OPERATION_ICD");
            Vector d = (Vector) parm.getData("OPT_CHN_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i)))
                    return "" + d.get(i);
            }
            return s;
        }
    }
    
	// ====================������begin======================
	/**
	 * �����������������
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// �����ͬ�У���ת����
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// ȡ�ñ��е�����
				String columnName[] = tableData.getNames("Data");// �������
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // ������������;
				int col = tranParmColIndex(columnName, tblColumnName); // ����ת��parm�е�������
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * �����������ݣ���TParmתΪVector
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * ����ָ���������������е�index
	 * @param columnName
	 * @param tblColumnName
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * �����������ݣ���Vectorת��Parm
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
	// ====================������end======================

	 
}
