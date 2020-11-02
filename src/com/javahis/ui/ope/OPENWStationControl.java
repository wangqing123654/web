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
 * <p>Title: 手术护士站 </p>
 *
 * <p>Description: 手术护士站 </p>
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
     * 当前子页面TAG
     */
    public String workPanelTag = "";
    private TTable table ;
    private BILComparator compare = new BILComparator();//比较器（排序功能）
	private boolean ascending = false; //正序，反序（排序功能）
	private int sortColumn = -1;//操作列（排序功能）

    /**
     * 初始化
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
     * 界面初始化
     */
    private void initUI() {
        table = (TTable) this.getComponent("Table");
        addSortListener(table);// table加排序监听
        OpList opList = new OpList();
        DiagList diagList = new DiagList();
        table.addItem("OpList", opList);
        table.addItem("DiagList", diagList);
        callFunction("UI|Table|addEventListener", "Table->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
    }

    /**
     * 数据初始化
     */
    private void initData() {
        // 获取当前时间
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
     * 手术间选择查询
     */
    public void onOpRoomQuery() {
        TParm parm = new TParm();
        if (this.getValueString("ADM_TYPE").equals("")) {
            this.messageBox("门急住别不能为空");
            return;
        }
        parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
        if (this.getValue("OP_START_DATE") == null || this.getValue("OP_END_DATE") == null) {
            this.messageBox("时间不能为空");
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
     * 病案号回车查询
     */
    public void onMrNoQuery() {
        String mrNo = this.getValueString("MR_NO").trim();
        if (!mrNo.equals("")) {
            mrNo = PatTool.getInstance().checkMrno(mrNo);
            TParm sysPatInfo = PatTool.getInstance().getInfoForMrno(mrNo);
            if (sysPatInfo.getCount() > 0) {
                this.setValue("MR_NO", mrNo);// 病案号
                this.setValue("PAT_NAME", sysPatInfo.getValue("PAT_NAME", 0));// 姓名
            } else {
                return;
            }
        } else {
            return;
        }
        TParm parm = new TParm();
        if (this.getValueString("ADM_TYPE").equals("")) {
            this.messageBox("门急住别不能为空");
            return;
        }
        parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
        if (this.getValue("OP_START_DATE") == null || this.getValue("OP_END_DATE") == null) {
            this.messageBox("时间不能为空");
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
     * 查询
     */
    public void onQuery() {
        TParm parm=new TParm();
        if (this.getValueString("ADM_TYPE").equals("")) {
            this.messageBox("门急住别不能为空");
            return;
        }
        parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
        if (this.getValue("OP_START_DATE") == null || this.getValue("OP_END_DATE") == null) {
            this.messageBox("时间不能为空");
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
                this.setValue("MR_NO", mrNo);// 病案号
                this.setValue("PAT_NAME", sysPatInfo.getValue("PAT_NAME", 0));// 姓名
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
     * 执行查询
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
                        + "   AND A.STATE IN ('1', '2', '3', '4', '5', '6') "//0,申请;1,排程完毕;2,接患者;3,手术室交接;4,手术等待;5,手术开始;6,关胸;7,手术结束;8,返回病房
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
            this.messageBox("E0005");//执行失败
            return;
        }
        if (result.getCount() < 1) {
            this.messageBox("E0008");// 查无资料
        }
        table.setParmValue(result);
    }
    
    /**
     * 手术排程
     */
    public void onAsg() {
        int row = table.getSelectedRow();// 选中行
        if (row < 0) {
            return;
        }
        TParm data = table.getParmValue();
        String OPBOOK_SEQ = data.getValue("OPBOOK_SEQ", row);
        this.openDialog("%ROOT%/config/ope/OPEPersonnel.x", OPBOOK_SEQ);
    }

    /**
     * 手术信息
     */
    public void onOpInfo() {
        int row = table.getSelectedRow();// 选中行
        TParm parmValue = table.getParmValue();
        String OPBOOK_SEQ = parmValue.getValue("OPBOOK_SEQ", row);
        TParm parm = new TParm();
        parm.setData("FLG", "update");
        parm.setData("OPBOOK_SEQ", OPBOOK_SEQ);
        parm.setData("ADM_TYPE", parmValue.getValue("ADM_TYPE", row));
        this.openDialog("%ROOT%/config/ope/OPEOpBook.x", parm);
    }

    /**
     * 手术记录
     */
    public void onOpRecord() {
        int row = table.getSelectedRow();// 选中行
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
     * 双击事件
     * 
     * @param row
     *            int
     */
    public void onTableDoubleClicked(int row) {
        if (row < 0) {
            return;
        }
        if (!OPETool.getInstance().isOpDept(Operator.getDept())) {
            this.messageBox("非手术科室登录，不允许进行操作");
            return;
        }
        TParm parmRow = table.getParmValue().getRow(row);
        this.setValue("ROOM_NO", parmRow.getValue("ROOM_NO"));
        this.setValue("MR_NO", parmRow.getValue("MR_NO"));
        this.setValue("IPD_NO", parmRow.getValue("IPD_NO"));
        this.setValue("PAT_NAME", parmRow.getValue("PAT_NAME"));
        if (!parmRow.getValue("CIRCULE_USER1").equals("")) {//巡回护士
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
                        + "   AND B.DS_DATE IS NULL        "// 在院
                        + "   AND A.ALLO_FLG = 'Y'         "
                        + "   AND B.CANCEL_FLG <> 'Y'      "
                        + "   AND A.BED_STATUS = '1'       "
                        + "   AND B.REGION_CODE = 'H01'    "
                        + "   AND B.MAINDIAG = D.ICD_CODE(+) "
                        + "   AND B.CASE_NO = '#'              " + "ORDER BY B.CASE_NO DESC";
        sql = sql.replaceFirst("#", parmRow.getValue("CASE_NO"));
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("查询用户信息失败");
            return;
        }
        // 拿到预交金余额如果不足给提示不强制
        double rPrice = result.getDouble("CUR_AMT");
        // 黄色警戒
        double yellowPrice = result.getDouble("YELLOW_SIGN");
        if (rPrice <= yellowPrice) {
            if (this.messageBox("提示信息 Tips", "预交金余额不足！\n Paying insufficient balance gold!",
                                this.YES_NO_OPTION) != 0) return;
        }
        table.setVisible(false);
        TParm action = new TParm();
        action.setData("INW", "CASE_NO", result.getValue("CASE_NO", 0));
        action.setData("INW", "IPD_NO", result.getValue("IPD_NO", 0)); // 住院号
        action.setData("INW", "MR_NO", result.getValue("MR_NO", 0)); // 病案号
        action.setData("INW", "CTZ1_CODE", result.getValue("CTZ1_CODE", 0)); // 身份1
        action.setData("INW", "CTZ2_CODE", result.getValue("CTZ2_CODE", 0)); // 身份2
        action.setData("INW", "CTZ3_CODE", result.getValue("CTZ3_CODE", 0)); // 身份3
        action.setData("INW", "PAT_NAME", result.getValue("PAT_NAME", 0)); // 姓名
        action.setData("INW", "DEPT_CODE", result.getValue("DEPT_CODE", 0));// 科室
        action.setData("INW", "STATION_CODE", result.getValue("STATION_CODE", 0)); // 病区
        action.setData("INW", "BED_NO", result.getValue("BED_NO", 0)); // 床号
        action.setData("INW", "VS_DR_CODE", result.getValue("VS_DR_CODE", 0));// 经治医生
        action.setData("INW", "ADM_DATE", result.getData("IN_DATE", 0)); // 入院时间
        action.setData("INW", "SAVE_FLG", true); // 保存权限注记
        boolean isICU = SYSBedTool.getInstance().checkIsICU(parmRow.getValue("CASE_NO"));
        action.setData("INW", "ICU_FLG", isICU); // ICU病患注记
        action.setData("INW", "CLNCPATH_CODE", result.getValue("CLNCPATH_CODE", 0));// 临床路径
        action.setData("INW", "OPE_FLG", true); // 手术医生站注记/////////////////
        action.setData("INW", "OP_DEPT_CODE", parmRow.getValue("OP_DEPT_CODE"));// 手术科室/////////////////
        action.setData("INW", "OPBOOK_SEQ", parmRow.getValue("OPBOOK_SEQ"));// 手术申请单号/////////////////
        ((TPanel) getComponent("PANEL")).addItem("INWSTATIONCHECK",
                                                 "%ROOT%\\config\\inw\\INWOrderCheckMain.x",
                                                 action, false);
        workPanelTag = "INWSTATIONCHECK";
        this.setUIEnabled(false);
    }

    /**
     * 启用/禁用界面组件
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
     * 显示当前TOOLBAR
     */
    public void onShowWindowsEvent() {
        if (workPanelTag == null || workPanelTag.length() == 0) {
            // 显示UIshowTopMenu
            callFunction("UI|showTopMenu");
            return;
        }
        TPanel p = (TPanel) getComponent(workPanelTag);
        p.getControl().callFunction("onShowWindowsFunction");
    }
    
    /**
     * 关闭工作页面
     * 
     * @return boolean
     */
    public boolean onClosePanel() {
        TPanel p = (TPanel) getComponent("INWSTATIONCHECK");
        if (!p.getControl().onClosing()) return false;
        // 移除当前子UI
        callFunction("UI|PANEL|removeItem", "INWSTATIONCHECK");
        // 移除子UIMenuBar
        callFunction("UI|removeChildMenuBar");
        // 移除子UIToolBar
        callFunction("UI|removeChildToolBar");
        // 显示UIshowTopMenu
        callFunction("UI|showTopMenu");
        // 得到TabbedPane控件
        // TTabbedPane tabPane = (TTabbedPane) this.callFunction("UI|TablePane|getThis");
        // 可以编辑
        // tabPane.setEnabled(true);
        this.setUIEnabled(true);
        // 显示TABLE
        table.setVisible(true);
        return true;
    }
    
    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("ROOM_NO;MR_NO;PAT_NAME;CIRCULE_USER;OP_DEPT_CODE");
        table.setDSValue();
        initData();
    }
    
    /**
     * 拿到对应药房
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
     * 初始化合理用药
     * 
     * @return boolean
     */
    public boolean initReasonbledMed() {
        try {
            if (PassDriver.init() != 1) {
                return false;
            }
            // 合理用药初始化
            if (PassDriver.PassInit(Operator.getName(), Operator.getDept(), 10) != 1) {
                return false;
            }
            // 合理用药控制参数
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
     * 诊断CODE替换中文 模糊查询（内部类）
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
     * 手术CODE替换中文 模糊查询（内部类）
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
    
	// ====================排序功能begin======================
	/**
	 * 加入表格排序监听方法
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// 点击相同列，翻转排序
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// 取得表单中的数据
				String columnName[] = tableData.getNames("Data");// 获得列名
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
				int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * 根据列名数据，将TParm转为Vector
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
	 * 返回指定列在列名数组中的index
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
	 * 根据列名数据，将Vector转成Parm
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
	// ====================排序功能end======================

	 
}
