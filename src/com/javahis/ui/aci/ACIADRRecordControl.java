package com.javahis.ui.aci;

import java.awt.Component;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TNull;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import jdo.aci.ACIADRM;
import jdo.aci.ACIADRTool;
import jdo.aci.ACIADRD;
import jdo.sys.PatTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p> Title: 药品不良事件登记 </p>
 * 
 * <p> Description: 药品不良事件登记 </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: BlueCore </p>
 * 
 * @author WangLong 2013.09.30
 * @version 1.0
 */
public class ACIADRRecordControl extends TControl {

    TTable doubtTable;// 怀疑药品表
    TTable mergeTable;// 并用药品表
    TTextField diagDesc1;// 原患疾病1
    TTextField diagDesc2;// 原患疾病2
    TTextField diagDesc3;// 原患疾病3
    TTextField diagDesc4;// 原患疾病4
    TTextField diagDesc5;// 原患疾病5
    TTextField adrDesc1;// 不良反应1
    TTextField adrDesc2;// 不良反应2
    TTextField adrDesc3;// 不良反应3
    TTextField adrDesc4;// 不良反应4
    private ACIADRM adeInfo;// 事件信息DataStore
    private ACIADRD doubtOrder;// 怀疑药品对象DataStore
    private ACIADRD mergeOrder;// 并用药品对象DataStore
    private boolean NEW_FLG = true;// 新增标记（true新增;false更新）
    private String ACI_NO="";//不良事件编号
    private TParm PARM;//不良事件详细信息
    private String ADM_TYPE="";//门健住别
    private String CASE_NO="";//就诊号
    private String MR_NO="";//病案号
    private String APPLICATION_NO;//条码号
    private String MONITER_CODE;//监测项目编码
    private String RPDTL_SEQ;//检验序号
    private Timestamp ORDER_DATE;
    private String ORDER_CODE;//药嘱代码
    private TParm patOrders;//病患的医嘱

    /**
     * 初始化
     */
    public void onInit() {
        initComponent();
        if (this.getParameter() == null || StringUtil.isNullString(this.getParameter().toString())) {
            initUI();// 无参数，正常初始化
        } else {
            TParm parm = (TParm) this.getParameter();
            // System.out.println("=======入参parm==========="+parm);
            if (!parm.getValue("ACI_NO").equals("")) {// 有ADR_NO参数，执行更新流程
                NEW_FLG = false;
                ACI_NO = parm.getValue("ACI_NO");
                ADM_TYPE = parm.getValue("ADM_TYPE");
                CASE_NO = parm.getValue("CASE_NO");
                MR_NO = parm.getValue("MR_NO");
                initUIByAciNo(ACI_NO);
            } else if (!parm.getValue("CASE_NO").equals("")) {// 有CASE_NO参数，执行新建流程
                ADM_TYPE = parm.getValue("ADM_TYPE");
                CASE_NO = parm.getValue("CASE_NO");
                MR_NO = parm.getValue("MR_NO");
                if (!parm.getValue("APPLICATION_NO").equals("")) {// 为不良事件监测预留，暂时没用
                    APPLICATION_NO = parm.getValue("APPLICATION_NO");
                    RPDTL_SEQ = parm.getValue("RPDTL_SEQ");
                    MONITER_CODE = parm.getValue("MONITER_CODE");
                    ORDER_DATE = parm.getTimestamp("ORDER_DATE");
                }
                if (!parm.getValue("ORDER_CODE").equals("")) {
                    ORDER_CODE = parm.getValue("ORDER_CODE");
                }
                initUIByCaseNo(CASE_NO);
            } else {// 手动进入登记界面，无传参，正常初始化
                initUI();
            }
        }
    }

    /**
     * 初始化界面控件
     */ 
    public void initComponent(){  
        doubtTable = (TTable) this.getComponent("DOUBT_TABLE");
        mergeTable = (TTable) this.getComponent("MERGE_TABLE");
        doubtTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                    "onDoubtOrderEditComponent");
        mergeTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                    "onBesideOrderEditComponent");
        callFunction("UI|DIAG_DESC1|setPopupMenuParameter", "DIAG_DESC1",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        callFunction("UI|DIAG_DESC1|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popICDReturn");
        callFunction("UI|DIAG_DESC2|setPopupMenuParameter", "DIAG_DESC2",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        callFunction("UI|DIAG_DESC2|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popICDReturn");
        callFunction("UI|DIAG_DESC3|setPopupMenuParameter", "DIAG_DESC3",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        callFunction("UI|DIAG_DESC3|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popICDReturn");
        callFunction("UI|DIAG_DESC4|setPopupMenuParameter", "DIAG_DESC4",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        callFunction("UI|DIAG_DESC4|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popICDReturn");
        callFunction("UI|DIAG_DESC5|setPopupMenuParameter", "DIAG_DESC5",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        callFunction("UI|DIAG_DESC5|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popICDReturn");
        callFunction("UI|ADR_DESC1|setPopupMenuParameter", "ADR_DESC1",
                     "%ROOT%\\config\\sys\\SYSADRPopup.x");
        callFunction("UI|ADR_DESC1|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popADRReturn");
        callFunction("UI|ADR_DESC2|setPopupMenuParameter", "ADR_DESC2",
                     "%ROOT%\\config\\sys\\SYSADRPopup.x");
        callFunction("UI|ADR_DESC2|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popADRReturn");
        callFunction("UI|ADR_DESC3|setPopupMenuParameter", "ADR_DESC3",
                     "%ROOT%\\config\\sys\\SYSADRPopup.x");
        callFunction("UI|ADR_DESC3|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popADRReturn");
        callFunction("UI|ADR_DESC4|setPopupMenuParameter", "ADR_DESC4",
                     "%ROOT%\\config\\sys\\SYSADRPopup.x");
        callFunction("UI|ADR_DESC4|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popADRReturn");
    }
    
    /**
     * 初始化界面信息
     */
    public void initUI() {
        initUIByCaseNo("");
    }
    
    /**
     * 通过CASE_NO初始化界面信息
     */
    public void initUIByCaseNo(String caseNo) {
        this.callFunction("UI|delete|setEnabled", false);
        ((TTabbedPane) this.getComponent("TABBEDPANE")).setSelectedComponent((TPanel) this
                .getComponent("DOUBT_PANEL"));
        Timestamp today = SystemTool.getInstance().getDate();
        setValue("REPORT_USER", Operator.getID());// 报告人
        setValue("REPORT_DEPT", Operator.getDept());// 报告科室
        setValue("REPORT_STATION", Operator.getStation());// 报告病区
        setValue("EVENT_DATE", today);// 发生日期
        setValue("DIED_DATE", today);// 死亡日期
        setValue("REPORT_DATE", today);// 上报日期
        initReportUserInfo(Operator.getID());//初始化报告人信息
        this.setValue("PERSON_HISTORY", "0");
        this.setValue("FAMILY_HISTORY", "0");
//        this.setValue("EVENT_RESULT", "0");
//        this.setValue("STOP_REDUCE", "1");
//        this.setValue("AGAIN_APPEAR", "1");
//        this.setValue("DISE_DISTURB", "0");
//        this.setValue("USER_REVIEW", "0");
//        this.setValue("UNIT_REVIEW", "0");
        if (!caseNo.equals("")) {
            onMrNo(MR_NO);// 初始化姓名等基本信息
            initPatInfo(CASE_NO);// 初始化诊断和既往史等基本信息
            if (!StringUtil.isNullString(APPLICATION_NO)) {
                initOrderTable(CASE_NO, APPLICATION_NO, MONITER_CODE);// 初始化怀疑用药和并用药品
            }
        } else {
            this.setValue("MR_NO", MR_NO);
            onMrNo("");
        }
        adeInfo = new ACIADRM();
        adeInfo.onQuery();
        if (caseNo.equals("")) {
            doubtOrder = new ACIADRD();
            doubtOrder.onQuery();
        }
        int doubtRow = doubtOrder.insertRow();
        doubtOrder.setItem(doubtRow, "ACI_NO", "");
        doubtOrder.setItem(doubtRow, "TYPE", "0");// 怀疑药品
        doubtOrder.setItem(doubtRow, "SEQ", doubtOrder.getItemInt(doubtRow, "#ID#"));
        doubtOrder.setItem(doubtRow, "OPT_USER", Operator.getID());
        doubtOrder.setItem(doubtRow, "OPT_DATE", doubtOrder.getDBTime());
        doubtOrder.setItem(doubtRow, "OPT_TERM", Operator.getIP());
        doubtOrder.setActive(doubtRow, false);
        doubtTable.setDataStore(doubtOrder);
        doubtTable.setDSValue();
        if (caseNo.equals("")) {
            mergeOrder = new ACIADRD();
            mergeOrder.onQuery();
        }
        int besideRow = mergeOrder.insertRow();
        mergeOrder.setItem(besideRow, "ACI_NO", "");
        mergeOrder.setItem(besideRow, "TYPE", "1");// 并用药品
        mergeOrder.setItem(besideRow, "SEQ", mergeOrder.getItemInt(besideRow, "#ID#"));
        mergeOrder.setItem(besideRow, "OPT_USER", Operator.getID());
        mergeOrder.setItem(besideRow, "OPT_DATE", mergeOrder.getDBTime());
        mergeOrder.setItem(besideRow, "OPT_TERM", Operator.getIP());
        mergeOrder.setActive(besideRow, false);
        mergeTable.setDataStore(mergeOrder);
        mergeTable.setDSValue();
    }
    
    /**
     * 根据传入值初始化界面信息
     */
    public void initUIByAciNo(String aciNo) {
        adeInfo = new ACIADRM();
        adeInfo.onQueryByACINo(aciNo);
        PARM = adeInfo.getDataParm();
        this.setValueForParm("MR_NO;PAT_NAME;SEX_CODE;SPECIES_CODE;BIRTH_DATE;WEIGHT;TEL;"
                                     + "DIAG_CODE1;DIAG_CODE2;DIAG_CODE3;DIAG_CODE4;DIAG_CODE5;"
                                     + "PERSON_HISTORY;PERSON_REMARK;FAMILY_HISTORY;FAMILY_REMARK;"
                                     + "SMOKE_FLG;DRINK_FLG;PREGNANT_FLG;HEPATOPATHY_FLG;NEPHROPATHY_FLG;ALLERGY_FLG;ALLERGY_REMARK;OTHER_FLG;OTHER_REMARK;"
                                     + "ADR_ID1;ADR_DESC1;ADR_ID2;ADR_ID3;ADR_ID4;EVENT_DATE;EVENT_DESC;EVENT_RESULT;"
                                     + "STOP_REDUCE;AGAIN_APPEAR;DISE_DISTURB;"
                                     + "USER_REVIEW;UNIT_REVIEW;"
                                     + "REPORT_USER;REPORT_OCC;OCC_REMARK;REPORT_TEL;REPORT_EMAIL;"
                                     + "REPORT_DEPT;REPORT_STATION;REPORT_UNIT;UNIT_CONTACTS;UNIT_TEL;REPORT_DATE;REMARK", PARM, 0);
        this.setValue("DIAG_DESC1", StringUtil.getDesc("SYS_DIAGNOSIS", "ICD_CHN_DESC", "ICD_CODE='" + PARM.getValue("DIAG_CODE1", 0) + "'"));
        this.setValue("DIAG_DESC2", StringUtil.getDesc("SYS_DIAGNOSIS", "ICD_CHN_DESC", "ICD_CODE='" + PARM.getValue("DIAG_CODE2", 0) + "'"));
        this.setValue("DIAG_DESC3", StringUtil.getDesc("SYS_DIAGNOSIS", "ICD_CHN_DESC", "ICD_CODE='" + PARM.getValue("DIAG_CODE3", 0) + "'"));
        this.setValue("DIAG_DESC4", StringUtil.getDesc("SYS_DIAGNOSIS", "ICD_CHN_DESC", "ICD_CODE='" + PARM.getValue("DIAG_CODE4", 0) + "'"));
        this.setValue("DIAG_DESC5", StringUtil.getDesc("SYS_DIAGNOSIS", "ICD_CHN_DESC", "ICD_CODE='" + PARM.getValue("DIAG_CODE5", 0) + "'"));
        if(PARM.getValue("ADR_DESC1", 0).equals("")){
            this.setValue("ADR_ID1", "");
        }
        this.setValue("ADR_DESC2", StringUtil.getDesc("ACI_ADRNAME", "ADR_DESC", "ADR_ID='" + PARM.getValue("ADR_ID2", 0) + "'"));
        this.setValue("ADR_DESC3", StringUtil.getDesc("ACI_ADRNAME", "ADR_DESC", "ADR_ID='" + PARM.getValue("ADR_ID3", 0) + "'"));
        this.setValue("ADR_DESC4", StringUtil.getDesc("ACI_ADRNAME", "ADR_DESC", "ADR_ID='" + PARM.getValue("ADR_ID4", 0) + "'"));
        
        PARM = PARM.getRow(0);
        if (PARM.getValue("FIRST_FLG").equals("0")) {// 首次报告
            this.callFunction("UI|FIRST_FLG_0|setSelected", true);
        } else if (PARM.getValue("FIRST_FLG").equals("1")) {// 跟踪报告
            this.callFunction("UI|FIRST_FLG_1|setSelected", true);
        }
        if (PARM.getValue("REPORT_TYPE").equals("0")) {// 报告类型:新的
            this.callFunction("UI|REPORT_TYPE_0|setSelected", true);
        } else if (PARM.getValue("REPORT_TYPE").equals("1")) {// 一般
            this.callFunction("UI|REPORT_TYPE_1|setSelected", true);
        } else if (PARM.getValue("REPORT_TYPE").equals("2")) {// 严重
            this.callFunction("UI|REPORT_TYPE_2|setSelected", true);
        }
        if (!this.getValue("DIAG_DESC1").equals("")) {// 原患疾病1
            this.callFunction("UI|DIAG_DESC2|setEnabled", true);
        }
        if (!this.getValue("DIAG_DESC2").equals("")) {// 原患疾病2
            this.callFunction("UI|DIAG_DESC2|setEnabled", true);
            this.callFunction("UI|DIAG_DESC3|setEnabled", true);
        }
        if (!this.getValue("DIAG_DESC3").equals("")) {// 原患疾病3
            this.callFunction("UI|DIAG_DESC3|setEnabled", true);
            this.callFunction("UI|DIAG_DESC4|setEnabled", true);
        }
        if (!this.getValue("DIAG_DESC4").equals("")) {// 原患疾病4
            this.callFunction("UI|DIAG_DESC4|setEnabled", true);
            this.callFunction("UI|DIAG_DESC5|setEnabled", true);
        }
        if (!this.getValue("DIAG_DESC5").equals("")) {// 原患疾病5
            this.callFunction("UI|DIAG_DESC5|setEnabled", true);
        }
        if (PARM.getValue("PERSON_HISTORY").equals("1")) {// 既往不良反应:有
            this.callFunction("UI|PERSON_REMARK|setEnabled", true);
        }
        if (PARM.getValue("FAMILY_HISTORY").equals("1")) {// 家族不良反应:有
            this.callFunction("UI|FAMILY_REMARK|setEnabled", true);
        }
        if (this.getValue("ALLERGY_FLG").equals("Y")) {// 过敏史:有
            this.callFunction("UI|ALLERGY_REMARK|setEnabled", true);
        }
        if (this.getValue("OTHER_FLG").equals("Y")) {// 其他情况:有
            this.callFunction("UI|OTHER_REMARK|setEnabled", true);
        }
        if (!PARM.getValue("ADR_DESC1").equals("")) {// 不良反应2
            this.callFunction("UI|ADR_DESC2|setEnabled", true);
        }
        if (!this.getValue("ADR_DESC2").equals("")) {// 不良反应2
            this.callFunction("UI|ADR_DESC2|setEnabled", true);
            this.callFunction("UI|ADR_DESC3|setEnabled", true);
        }
        if (!this.getValue("ADR_DESC3").equals("")) {// 不良反应3
            this.callFunction("UI|ADR_DESC3|setEnabled", true);
            this.callFunction("UI|ADR_DESC4|setEnabled", true);
        }
        if (!this.getValue("ADR_DESC4").equals("")) {// 不良反应4
            this.callFunction("UI|ADR_DESC4|setEnabled", true);
        }
        if (PARM.getValue("EVENT_RESULT").equals("3")) {// 有后遗症
            this.callFunction("UI|RESULT_LABEL|setVisible", true);
            this.callFunction("UI|RESULT_LABEL|setValue", "表现");
            this.callFunction("UI|RESULT_REMARK|setVisible", true);
            this.setValue("RESULT_REMARK", PARM.getValue("RESULT_REMARK"));
        } else if (PARM.getValue("EVENT_RESULT").equals("4")) {// 死亡
            this.callFunction("UI|RESULT_LABEL|setVisible", true);
            this.callFunction("UI|RESULT_LABEL|setValue", "直接死因");
            this.callFunction("UI|RESULT_REMARK|setVisible", true);
            this.setValue("RESULT_REMARK", PARM.getValue("RESULT_REMARK"));
            this.callFunction("UI|DIED_LABEL|setVisible", true);
            this.callFunction("UI|DIED_DATE|setVisible", true);
            this.setValue("DIED_DATE", PARM.getData("DIED_DATE"));
        }
        if (PARM.getValue("REPORT_OCC").equals("6")) {// 职业:其他注记
            this.callFunction("UI|OCC_REMARK|setEnabled", true);
        }
        if (PARM.getValue("UPLOAD_FLG").equals("Y")) {// 已上报不允许更新
            this.callFunction("UI|insert|setEnabled", false);
            this.callFunction("UI|delete|setEnabled", false);
        }
        doubtOrder = new ACIADRD();
        doubtOrder.onQueryByACINoAndType(aciNo, "0");// 怀疑药品
        int doubtRow = doubtOrder.insertRow();
        doubtOrder.setItem(doubtRow, "ACI_NO", "");
        doubtOrder.setItem(doubtRow, "TYPE", "0");// 怀疑药品
        doubtOrder.setItem(doubtRow, "OPT_USER", Operator.getID());
        doubtOrder.setItem(doubtRow, "OPT_DATE", doubtOrder.getDBTime());
        doubtOrder.setItem(doubtRow, "OPT_TERM", Operator.getIP());
        doubtOrder.setActive(doubtRow, false);
        doubtTable.setDataStore(doubtOrder);
        doubtTable.setDSValue();
        mergeOrder = new ACIADRD();
        mergeOrder.onQueryByACINoAndType(aciNo, "1");// 并用药品
        int besideRow = mergeOrder.insertRow();
        mergeOrder.setItem(besideRow, "ACI_NO", "");
        mergeOrder.setItem(besideRow, "TYPE", "1");// 并用药品
        mergeOrder.setItem(besideRow, "OPT_USER", Operator.getID());
        mergeOrder.setItem(besideRow, "OPT_DATE", mergeOrder.getDBTime());
        mergeOrder.setItem(besideRow, "OPT_TERM", Operator.getIP());
        mergeOrder.setActive(besideRow, false);
        mergeTable.setDataStore(mergeOrder);
        mergeTable.setDSValue();
    }

    /**
     * 获得病患的医嘱信息
     * @param admType
     * @param CaseNo
     * @return
     */
    public TParm getPatOrders(String admType, String CaseNo) {
        String sql = "";
        if (admType.equals("O") || admType.equals("E")) {
            sql = "SELECT * FROM OPD_ORDER WHERE CASE_NO = '#' AND CAT1_TYPE = 'PHA'";
        } else if (admType.equals("I")) {
            sql = "SELECT * FROM ODI_ORDER WHERE CASE_NO = '#' AND CAT1_TYPE = 'PHA'";
        } else if (admType.equals("H")) {
            sql = "SELECT * FROM HRM_ORDER WHERE CASE_NO = '#' AND CAT1_TYPE = 'PHA'";
        }
        sql = sql.replaceFirst("#", CaseNo);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("获得病患的医嘱信息失败");
            return null;
        }
        return result;
    }
    
    /**
     * 初始化病患信息
     * @param caseNo
     */
    public void initPatInfo(String caseNo) {
        String diagSql =
                "SELECT MIN(SEQ_NO), ICD_CODE, ICD_DESC FROM MRO_RECORD_DIAG "
                        + " WHERE MAIN_FLG = 'Y' AND CASE_NO = '#' "
                        + " GROUP BY ICD_CODE, ICD_DESC ORDER BY MIN(SEQ_NO)";
        diagSql = diagSql.replaceFirst("#", caseNo);
        TParm diagParm = new TParm(TJDODBTool.getInstance().select(diagSql));
        if (diagParm.getCount() >= 1) {
            this.setValue("DIAG_CODE1", diagParm.getValue("ICD_CODE", 0));
            this.setValue("DIAG_DESC1", diagParm.getValue("ICD_DESC", 0));
            this.callFunction("UI|DIAG_DESC2|setEnabled", true);
        }
        if (diagParm.getCount() >= 2) {
            this.setValue("DIAG_CODE2", diagParm.getValue("ICD_CODE", 1));
            this.setValue("DIAG_DESC2", diagParm.getValue("ICD_DESC", 1));
            this.callFunction("UI|DIAG_DESC3|setEnabled", true);
        }
        if (diagParm.getCount() >= 3) {
            this.setValue("DIAG_CODE3", diagParm.getValue("ICD_CODE", 2));
            this.setValue("DIAG_DESC3", diagParm.getValue("ICD_DESC", 2));
            this.callFunction("UI|DIAG_DESC4|setEnabled", true);
        }
        if (diagParm.getCount() >= 4) {
            this.setValue("DIAG_CODE4", diagParm.getValue("ICD_CODE", 3));
            this.setValue("DIAG_DESC4", diagParm.getValue("ICD_DESC", 3));
            this.callFunction("UI|DIAG_DESC5|setEnabled", true);
        }
        if (diagParm.getCount() >= 5) {
            this.setValue("DIAG_CODE5", diagParm.getValue("ICD_CODE", 4));
            this.setValue("DIAG_DESC5", diagParm.getValue("ICD_DESC", 4));
        }
        String lastCaseNoSql = "SELECT DISTINCT ALLEGIC FROM MRO_RECORD WHERE CASE_NO = '#'";// 查询过敏史
        lastCaseNoSql = lastCaseNoSql.replaceFirst("#", caseNo);
        TParm mroParm = new TParm(TJDODBTool.getInstance().select(lastCaseNoSql));
        if (mroParm.getCount() > 0) {
            if (!mroParm.getValue("ALLEGIC", 0).equals("")) {
                this.setValue("ALLERGY_FLG", "Y");
                this.setValue("ALLERGY_REMARK", mroParm.getValue("ALLEGIC"));
            }
        }
    }
    
    /**
     * 设置上报人的相关信息
     */
    public void initReportUserInfo(String userID) {
        TParm optUser = ACIADRTool.getInstance().getOperatorInfo(userID);
        if (optUser.getErrCode() < 0) {
            this.messageBox_(optUser.getErrText());
        } else {
            String posCode = optUser.getValue("POS_CODE", 0);
            String posType =
                    StringUtil.getDesc("SYS_POSITION", "POS_TYPE", "POS_CODE='" + posCode + "'");
            setValue("REPORT_OCC", posType);// 报告人职别
            if (posType.equals("6")) {
                String posDesc =
                        StringUtil.getDesc("SYS_POSITION", "POS_CHN_DESC", "POS_CODE='" + posCode
                                + "'");
                this.setValue("OCC_REMARK", posDesc);
            }
            setValue("REPORT_TEL", optUser.getData("TEL1", 0));// 报告人电话
            setValue("REPORT_EMAIL", optUser.getData("E_MAIL", 0));// 报告人E-MAIL
        }
    }
    
    /**
     * 初始化药嘱Table
     * @param caseNo
     * @param applicationNo
     * @param moniterCode
     */
    public void initOrderTable(String caseNo, String applicationNo, String moniterCode) {
        if (ADM_TYPE.equals("I")) {
            String doubtOrderSql =
                    "SELECT DISTINCT A.*, C.HYGIENE_TRADE_CODE, D.DOSE_CODE, D.MAN_CHN_DESC, C.UNIT_CODE "
                            + "  FROM ODI_ORDER A, ODI_ORDER B, SYS_FEE C, PHA_BASE D "
                            + " WHERE A.CASE_NO = B.CASE_NO "
                            + "   AND A.ORDER_CODE = C.ORDER_CODE "
                            + "   AND C.ORDER_CODE = D.ORDER_CODE "
                            + "   AND A.CAT1_TYPE = 'PHA' "
                            + "   AND A.NS_CHECK_CODE IS NOT NULL "
                            + "   AND A.EFF_DATE < B.ORDER_DATE "
                            + "   AND A.CASE_NO = '#' "
                            + "   AND B.MED_APPLY_NO = '#' ";
            doubtOrderSql = doubtOrderSql.replaceFirst("#", caseNo);
            doubtOrderSql = doubtOrderSql.replaceFirst("#", applicationNo);
            String susOrdersSQL = "SELECT DISTINCT ORDER_CODE FROM ACI_MNTORDER WHERE 1 = 1 ";
            if (!StringUtil.isNullString(moniterCode)) {
                susOrdersSQL += " AND MONITER_CODE = '#'".replaceFirst("#", moniterCode);
            }
            doubtOrderSql += " AND C.ORDER_CODE IN (#) ".replaceFirst("#", susOrdersSQL);
            TParm doubtOrderParm = new TParm(TJDODBTool.getInstance().select(doubtOrderSql));
            if (doubtOrderParm.getErrCode() < 0) {
                return;
            }
            int doubtRrow = -1;
            doubtOrder = new ACIADRD();
            doubtOrder.onQuery();
            for (int i = 0; i < doubtOrderParm.getCount(); i++) {
                doubtRrow = doubtOrder.insertRow();
                doubtTable.acceptText();
                doubtOrder.setItem(doubtRrow, "ACI_NO", "");
                doubtOrder.setItem(doubtRrow, "TYPE", "0");// 怀疑药品
                doubtOrder.setItem(doubtRrow, "SEQ", doubtOrder.getNextSeqNo());
                doubtOrder.setItem(doubtRrow, "HYGIENE_TRADE_CODE", doubtOrderParm.getData("HYGIENE_TRADE_CODE",i));
                doubtOrder.setItem(doubtRrow, "ORDER_CODE", doubtOrderParm.getData("ORDER_CODE",i));
                doubtOrder.setItem(doubtRrow, "ORDER_DESC", doubtOrderParm.getData("ORDER_DESC",i));
                doubtOrder.setItem(doubtRrow, "GOODS_DESC", doubtOrderParm.getData("GOODS_DESC",i));
                doubtOrder.setItem(doubtRrow, "DOSE_CODE", doubtOrderParm.getData("DOSE_CODE",i));
                doubtOrder.setItem(doubtRrow, "MAN_DESC", doubtOrderParm.getData("MAN_CHN_DESC",i));
                doubtOrder.setItem(doubtRrow, "FREQ_CODE", doubtOrderParm.getData("FREQ_CODE",i));
                doubtOrder.setItem(doubtRrow, "ROUTE_CODE", doubtOrderParm.getData("ROUTE_CODE",i));
                doubtOrder.setItem(doubtRrow, "UNIT_CODE", doubtOrderParm.getData("UNIT_CODE", i));
                doubtOrder.setItem(doubtRrow, "MEDI_QTY", doubtOrderParm.getDouble("MEDI_QTY", i));
                doubtOrder.setItem(doubtRrow, "MEDI_UNIT", doubtOrderParm.getData("MEDI_UNIT", i));
//                TTextFormat BATCH_NO = ((TTextFormat) doubtTable.getItem("BATCH_NO"));
//                String batchNoSql =
//                        " SELECT BATCH_NO,VALID_DATE FROM IND_VERIFYIND WHERE ORDER_CODE = '#' ORDER BY SEQ_NO ";
//                batchNoSql = batchNoSql.replaceFirst("#", doubtOrderParm.getValue("ORDER_CODE",i));
//                TParm result = new TParm(TJDODBTool.getInstance().select(batchNoSql));
//
//                BATCH_NO.setPopupMenuSQL(batchNoSql);
//                BATCH_NO.onQuery();
//                doubtOrder.setItem(doubtRrow, "BATCH_NO", result.getData("BATCH_NO", 0));
                doubtOrder.setItem(doubtRrow, "BATCH_NO", "");
                doubtOrder.setItem(doubtRrow, "MEDI_START_DATE", doubtOrderParm.getData("ORDER_DATE",i));
                doubtOrder.setItem(doubtRrow, "MEDI_END_DATE", doubtOrderParm.getData("DC_DATE", i) == null ? 
                         doubtOrderParm.getData("ORDER_DATE", i) : doubtOrderParm.getData("DC_DATE", i));
                doubtOrder.setItem(doubtRrow, "MEDI_REMARK", "");
                doubtOrder.setItem(doubtRrow, "OPT_USER", Operator.getID());
                doubtOrder.setItem(doubtRrow, "OPT_DATE", doubtOrder.getDBTime());
                doubtOrder.setItem(doubtRrow, "OPT_TERM", Operator.getIP());
                doubtOrder.setActive(doubtRrow, true);
                doubtTable.setDataStore(doubtOrder);
                doubtTable.setDSValue();
            }
            String mergeOrderSql =
                    "SELECT DISTINCT A.*, D.HYGIENE_TRADE_CODE, E.DOSE_CODE, E.MAN_CHN_DESC, D.UNIT_CODE "
                            + "  FROM ODI_ORDER A, ODI_ORDER B, ODI_ORDER C, SYS_FEE D, PHA_BASE E "
                            + " WHERE A.CASE_NO = B.CASE_NO "
                            + "   AND A.CAT1_TYPE = B.CAT1_TYPE "
                            + "   AND B.CASE_NO = C.CASE_NO "
                            + "   AND B.ORDER_CODE = D.ORDER_CODE "
                            + "   AND D.ORDER_CODE = E.ORDER_CODE "
                            + "   AND A.LINK_NO = B.LINK_NO "// 连接医嘱
                            + "   AND B.CAT1_TYPE = 'PHA' "
                            + "   AND B.NS_CHECK_CODE IS NOT NULL "
                            + "   AND B.EFF_DATE < C.ORDER_DATE "
                            + "   AND B.CASE_NO = '#' "
                            + "   AND C.MED_APPLY_NO = '#' ";
            mergeOrderSql = mergeOrderSql.replaceFirst("#", caseNo);
            mergeOrderSql = mergeOrderSql.replaceFirst("#", applicationNo);
            mergeOrderSql += " AND B.ORDER_CODE IN (#) ".replaceFirst("#", susOrdersSQL);
            mergeOrderSql += " AND A.ORDER_CODE NOT IN (#) ".replaceFirst("#", susOrdersSQL);
            TParm mergeOrderParm = new TParm(TJDODBTool.getInstance().select(mergeOrderSql));
            if (mergeOrderParm.getErrCode() < 0) {
                return;
            }
            int mergeRow = -1;
            mergeOrder = new ACIADRD();
            mergeOrder.onQuery();
            for (int i = 0; i < mergeOrderParm.getCount(); i++) {
                mergeRow = mergeOrder.insertRow();
                mergeTable.acceptText();
                mergeOrder.setItem(mergeRow, "ACI_NO", "");
                mergeOrder.setItem(mergeRow, "TYPE", "1");// 并用药品
                mergeOrder.setItem(mergeRow, "SEQ", doubtOrder.getNextSeqNo());
                mergeOrder.setItem(mergeRow, "HYGIENE_TRADE_CODE", mergeOrderParm.getData("HYGIENE_TRADE_CODE",i));
                mergeOrder.setItem(mergeRow, "ORDER_CODE", mergeOrderParm.getData("ORDER_CODE",i));
                mergeOrder.setItem(mergeRow, "ORDER_DESC", mergeOrderParm.getData("ORDER_DESC",i));
                mergeOrder.setItem(mergeRow, "GOODS_DESC", mergeOrderParm.getData("GOODS_DESC",i));
                mergeOrder.setItem(mergeRow, "DOSE_CODE", mergeOrderParm.getData("DOSE_CODE",i));
                mergeOrder.setItem(mergeRow, "MAN_DESC", mergeOrderParm.getData("MAN_CHN_DESC",i));
                mergeOrder.setItem(mergeRow, "FREQ_CODE", mergeOrderParm.getData("FREQ_CODE",i));
                mergeOrder.setItem(mergeRow, "ROUTE_CODE", mergeOrderParm.getData("ROUTE_CODE",i));
                mergeOrder.setItem(mergeRow, "UNIT_CODE", mergeOrderParm.getData("UNIT_CODE", i));
                mergeOrder.setItem(mergeRow, "MEDI_QTY", mergeOrderParm.getDouble("MEDI_QTY", i));
                mergeOrder.setItem(mergeRow, "MEDI_UNIT", mergeOrderParm.getData("MEDI_UNIT", i));
//                TTextFormat BATCH_NO = ((TTextFormat) mergeTable.getItem("BATCH_NO"));
//                String batchNoSql =
//                        " SELECT BATCH_NO,VALID_DATE FROM IND_VERIFYIND WHERE ORDER_CODE = '#' ORDER BY SEQ_NO ";
//                batchNoSql = batchNoSql.replaceFirst("#", mergeOrderParm.getValue("ORDER_CODE",i));
//                TParm result = new TParm(TJDODBTool.getInstance().select(batchNoSql));
//                BATCH_NO.setPopupMenuSQL(batchNoSql);
//                BATCH_NO.onQuery();
//                mergeOrder.setItem(mergeRow, "BATCH_NO", result.getData("BATCH_NO", 0));
                mergeOrder.setItem(mergeRow, "BATCH_NO", "");
                mergeOrder.setItem(mergeRow, "MEDI_START_DATE", mergeOrderParm.getData("ORDER_DATE",i));
                mergeOrder.setItem(mergeRow, "MEDI_END_DATE", mergeOrderParm.getData("DC_DATE", i) == null ? 
                        mergeOrderParm.getData("ORDER_DATE", i) : mergeOrderParm.getData("DC_DATE", i));
                mergeOrder.setItem(mergeRow, "MEDI_REMARK", "");
                mergeOrder.setItem(mergeRow, "OPT_USER", Operator.getID());
                mergeOrder.setItem(mergeRow, "OPT_DATE", mergeOrder.getDBTime());
                mergeOrder.setItem(mergeRow, "OPT_TERM", Operator.getIP());
                mergeOrder.setActive(mergeRow, true);
            } 
        }
    }

    /**
     * 获得界面上的数据
     * @return
     */
    public TParm getUIData() {
        TParm parm =
                getParmForTag("MR_NO;PAT_NAME;SEX_CODE;SPECIES_CODE;BIRTH_DATE:Timestamp;WEIGHT:int;TEL;"
                       // + "DIAG_CODE1;DIAG_CODE2;DIAG_CODE3;DIAG_CODE4;DIAG_CODE5;"
                        + "PERSON_HISTORY;PERSON_REMARK;FAMILY_HISTORY;FAMILY_REMARK;"
                        + "SMOKE_FLG;DRINK_FLG;PREGNANT_FLG;HEPATOPATHY_FLG;NEPHROPATHY_FLG;ALLERGY_FLG;ALLERGY_REMARK;OTHER_FLG;OTHER_REMARK;"
                        + "EVENT_DATE:Timestamp;EVENT_DESC;EVENT_RESULT;"
                       // + "ADR_ID1;ADR_DESC1;ADR_ID2;ADR_ID3;ADR_ID4;"
                        + "STOP_REDUCE;AGAIN_APPEAR;DISE_DISTURB;"
                        + "USER_REVIEW;UNIT_REVIEW;"
                        + "REPORT_USER;REPORT_OCC;OCC_REMARK;REPORT_TEL;REPORT_EMAIL;"
                        + "REPORT_DEPT;REPORT_STATION;REPORT_UNIT;UNIT_CONTACTS;UNIT_TEL;REPORT_DATE:Timestamp;REMARK");
        String mrNo = parm.getValue("MR_NO");
        mrNo = PatTool.getInstance().checkMrno(mrNo);// 病案号补齐长度
        parm.setData("MR_NO", mrNo);
        if (((TRadioButton) this.getComponent("FIRST_FLG_0")).isSelected()) {// 首次报告
            parm.setData("FIRST_FLG", "0");
        } else {// 跟踪报告
            parm.setData("FIRST_FLG", "1");
        }
        if (((TRadioButton) this.getComponent("REPORT_TYPE_0")).isSelected()) {// 报告类型:新的
            parm.setData("REPORT_TYPE", "0");
        } else if (((TRadioButton) this.getComponent("REPORT_TYPE_1")).isSelected()) {// 一般
            parm.setData("REPORT_TYPE", "1");
        } else if (((TRadioButton) this.getComponent("REPORT_TYPE_2")).isSelected()) {// 严重
            parm.setData("REPORT_TYPE", "2");
        }
        ArrayList<String> diagList = new ArrayList<String>();
        if (this.getValueString("DIAG_DESC1").trim().equals("")) {
            this.setValue("DIAG_CODE1", "");
        } else {
            diagList.add(this.getValueString("DIAG_CODE1"));
        }
        if (this.getValueString("DIAG_DESC2").trim().equals("")) {
            this.setValue("DIAG_CODE2", "");
        } else {
            diagList.add(this.getValueString("DIAG_CODE2"));
        }
        if (this.getValueString("DIAG_DESC3").trim().equals("")) {
            this.setValue("DIAG_CODE3", "");
        } else {
            diagList.add(this.getValueString("DIAG_CODE3"));
        }
        if (this.getValueString("DIAG_DESC4").trim().equals("")) {
            this.setValue("DIAG_CODE4", "");
        } else {
            diagList.add(this.getValueString("DIAG_CODE4"));
        }
        if (this.getValueString("DIAG_DESC5").trim().equals("")) {
            this.setValue("DIAG_CODE5", "");
        } else {
            diagList.add(this.getValueString("DIAG_CODE5"));
        }
        String[] diagArr = (String[]) diagList.toArray(new String[0]);
        if (diagArr.length > 0) {
            for (int i = 0; i < 5; i++) {
                if (i < diagArr.length) {
                    parm.setData("DIAG_CODE" + (i + 1), diagArr[i]);
                } else {
                    parm.setData("DIAG_CODE" + (i + 1), "");
                }
            }
        }
        if (!parm.getValue("PERSON_HISTORY").equals("1")) {// 既往药品不良反应
            parm.setData("PERSON_REMARK", "");
        }
        if (!parm.getValue("FAMILY_HISTORY").equals("1")) {// 家族药品不良反应
            parm.setData("FAMILY_REMARK", "");
        }
        if (parm.getValue("ALLERGY_FLG").equals("N")) {// 过敏史
            parm.setData("ALLERGY_REMARK", "");
        }
        if (parm.getValue("OTHER_FLG").equals("N")) {// 其他情况
            parm.setData("OTHER_REMARK", "");
        }
        ArrayList<String> adrIdList = new ArrayList<String>();
        ArrayList<String> adrDescList = new ArrayList<String>();
        if (this.getValueString("ADR_DESC1").trim().equals("")) {
            this.setValue("ADR_ID1", "");
        } else {
            adrIdList.add(this.getValueString("ADR_ID1"));
            adrDescList.add(this.getValueString("ADR_DESC1"));
        }
        if (this.getValueString("ADR_DESC2").trim().equals("")) {
            this.setValue("ADR_ID2", "");
        } else {
            adrIdList.add(this.getValueString("ADR_ID2"));
            adrDescList.add(this.getValueString("ADR_DESC2"));
        }
        if (this.getValueString("ADR_DESC3").trim().equals("")) {
            this.setValue("ADR_ID3", "");
        } else {
            adrIdList.add(this.getValueString("ADR_ID3"));
            adrDescList.add(this.getValueString("ADR_DESC3"));
        }
        if (this.getValueString("ADR_DESC4").trim().equals("")) {
            this.setValue("ADR_ID4", "");
        } else {
            adrIdList.add(this.getValueString("ADR_ID4"));
            adrDescList.add(this.getValueString("ADR_DESC4"));
        }
        String[] adrIdArr = (String[]) adrIdList.toArray(new String[0]);
        String[] adrDescArr = (String[]) adrDescList.toArray(new String[0]);
        if (adrIdArr.length > 0) {
            for (int i = 0; i < 4; i++) {
                if (i < adrIdArr.length) {
                    parm.setData("ADR_ID" + (i + 1), adrIdArr[i]);
                } else {
                    parm.setData("ADR_ID" + (i + 1), "");
                }
            }
            parm.setData("ADR_DESC1", adrDescArr[0]);
        }
        if (parm.getValue("EVENT_RESULT").equals("3")) {// 有后遗症
            parm.setData("RESULT_REMARK", this.getValue("RESULT_REMARK"));
        } else if (parm.getValue("EVENT_RESULT").equals("4")) {// 死亡
            parm.setData("DIED_DATE", this.getValue("DIED_DATE"));
            parm.setData("RESULT_REMARK", this.getValue("RESULT_REMARK"));
        }
        return parm;
    }
    
    /**
     * 检查界面输入的有效性
     */
    public boolean checkUIData(TParm parm) {
        if (parm.getValue("MR_NO").equals("")) {
            this.messageBox("请填写病案号");
            return false;
        }
        if (parm.getValue("PAT_NAME").equals("")) {
            this.messageBox("请填写病患姓名");
            return false;
        }
        if (parm.getValue("SEX_CODE").equals("")) {
            this.messageBox("请选择性别");
            return false;
        }
//        if (parm.getValue("SPECIES_CODE").equals("")) {
//            this.messageBox("请选择民族");
//            return false;
//        }
//        if (parm.getData("BIRTH_DATE") == null
//                || parm.getData("BIRTH_DATE").equals(new TNull(Timestamp.class))) {
//            this.messageBox("请填写出生日期");
//            return false;
//        }
//        if (parm.getValue("WEIGHT").equals("")) {
//            this.messageBox("请填写体重");
//            return false;
//        }
//        if (parm.getValue("TEL").equals("")) {
//            this.messageBox("请填写病患联系方式");
//            return false;
//        }
//        if (parm.getValue("DIAG_CODE1").equals("")) {
//            this.messageBox("请选择原患疾病");
//            return false;
//        }
        if (countStrLength(parm.getValue("PERSON_REMARK")) > 100) {
            this.messageBox("既往史备注填写过长");
            return false;
        }
        if (countStrLength(parm.getValue("FAMILY_REMARK")) > 100) {
            this.messageBox("家族史备注填写过长");
            return false;
        }
//        if(parm.getValue("ALLERGY_FLG").equals("Y")&&parm.getValue("ALLERGY_REMARK").equals("")){
//            this.messageBox("请填写过敏史相关信息");
//            return false;
//        }
//        if(parm.getValue("OTHER_FLG").equals("Y")&&parm.getValue("OTHER_REMARK").equals("")){
//            this.messageBox("请填写过其他相关信息");
//            return false;
//        }
        if (countStrLength(parm.getValue("ALLERGY_REMARK")) > 100) {
            this.messageBox("“过敏史”备注填写过长");
            return false;
        }
        if (countStrLength(parm.getValue("OTHER_REMARK")) > 100) {
            this.messageBox("“其他”备注填写过长");
            return false;
        }
        if (parm.getValue("ADR_ID1").equals("") && parm.getValue("ADR_ID2").equals("")
                && parm.getValue("ADR_ID3").equals("") && parm.getValue("ADR_ID4").equals("")) {
            this.messageBox("请选择事件名称");
            return false;
        }
        if (parm.getData("EVENT_DATE") == null
                || parm.getData("EVENT_DATE").equals(new TNull(Timestamp.class))) {
            this.messageBox("请填写事件发生日期");
            return false;
        }
        if (parm.getValue("EVENT_DESC").equals("")) {
            this.messageBox("请填写事件描述");
            return false;
        }
        if (countStrLength(parm.getValue("EVENT_DESC")) > 200) {
            this.messageBox("事件描述填写过长");
            return false;
        }
//        if (parm.getValue("EVENT_RESULT").equals("3") && parm.getValue("RESULT_REMARK").equals("")) {
//            this.messageBox("请填写后遗症的表现");
//            return false;
//        }
//        if (parm.getValue("EVENT_RESULT").equals("4") && parm.getValue("RESULT_REMARK").equals("")) {
//            this.messageBox("请填写死因");
//            return false;
//        }
        if (countStrLength(parm.getValue("RESULT_REMARK")) > 100) {
            this.messageBox("事件后果备注填写过长");
            return false;
        }
//        if (parm.getValue("EVENT_RESULT").equals("4")
//                && (parm.getData("DIED_DATE") == null || parm.getData("DIED_DATE")
//                        .equals(new TNull(Timestamp.class)))) {
//            this.messageBox("请填写死亡时间");
//            return false;
//        }
//        if (parm.getValue("REPORT_USER").equals("")) {
//            this.messageBox("请填写上报科室");
//            return false;
//        }
//        if (parm.getValue("REPORT_OCC").equals("")) {
//            this.messageBox("请选择上报人职业");
//            return false;
//        } else if (parm.getValue("REPORT_OCC").equals("6")) {// 其他
//            if (parm.getValue("OCC_REMARK").equals("")) {
//                this.messageBox("请填写上报人职业备注");
//                return false;
//            }
//        }
//        if (parm.getValue("REPORT_TEL").equals("")) {
//            this.messageBox("请填写报告人联系电话");
//            return false;
//        }
//        if (parm.getValue("REPORT_EMAIL").equals("")) {
//            this.messageBox("请填写联系人电子邮箱");
//            return false;
//        }
//        if (parm.getValue("REPORT_DEPT").equals("")) {
//            this.messageBox("请选择报告科室");
//            return false;
//        }
//         if (parm.getValue("REPORT_STATION").equals("")) {
//         this.messageBox("请选择报告病区");
//         return false;
//         }
//        if (parm.getData("REPORT_DATE") == null
//                || parm.getData("REPORT_DATE").equals(new TNull(Timestamp.class))) {
//            this.messageBox("请填写报告日期");
//            return false;
//        }
        if (parm.getTimestamp("REPORT_DATE").getTime() < parm.getTimestamp("EVENT_DATE").getTime()) {
            this.messageBox("报告日期不能早于事件发生日期");
            return false;
        }
//        if (parm.getValue("REPORT_SECTION").equals("")) {
//            this.messageBox("请选择报送部门");
//            return false;
//        }
//        if (parm.getValue("DEPT_IN_CHARGE").equals("")) {
//            this.messageBox("请填写责任科室");
//            return false;
//        }
//        if (parm.getValue("USER_IN_CHARGE").equals("")) {
//            this.messageBox("请选择责任人");
//            return false;
//        }
//        if (parm.getValue("UNIT_TEL").equals("")) {
//            this.messageBox("请填写责任人电话");
//            return false;
//        }
        if (countStrLength(parm.getValue("REMARK")) > 200) {
            this.messageBox("备注填写过长");
            return false;
        }
        if (doubtTable.getRowCount() < 1) {
            this.messageBox("请填写怀疑药品信息");
            return false;
        }
        return true;
    }

    /**
     * 保存
     */
    public void onSave() {
        TParm parm = getUIData();//获取界面上的数据
        if (!checkUIData(parm)) {//检查数据有效性
            return;
        }
        parm.setData("ACI_NO", ACI_NO);
        parm.setData("ADM_TYPE", ADM_TYPE);
        parm.setData("CASE_NO", CASE_NO);
        parm.setData("REGION_CODE", Operator.getRegion());
        int row = -1;
        if (NEW_FLG == true) {// 新建
            adeInfo = new ACIADRM();
            adeInfo.onQuery();
            row = adeInfo.insertRow(-1);
            adeInfo.initData(row, parm);
        } else {// 更新
        
            parm.setData("UPLOAD_FLG", PARM.getValue("UPLOAD_FLG"));
            parm.setData("UPLOAD_DATE", PARM.getData("UPLOAD_DATE"));
            row = adeInfo.onQueryByACINo(ACI_NO);
            adeInfo.updateData(row, parm);
        }
        String[] sql = adeInfo.getUpdateSQL();
        if (doubtTable.getRowCount() > 0) {
            if (doubtTable.getItemString(0, "ORDER_DESC").equals("")) {
                this.messageBox("请填写怀疑药品");
                return;
            }
            for (int i = 0; i < doubtOrder.getModifiedRows().length; i++) {
                doubtOrder.setItem(doubtOrder.getModifiedRows()[i], "ACI_NO",
                                   adeInfo.getItemData(row, "ACI_NO"));
            }
        }
        if (mergeTable.getRowCount() > 0) {
            for (int i = 0; i < mergeOrder.getModifiedRows().length; i++) {
                mergeOrder.setItem(mergeOrder.getModifiedRows()[i], "ACI_NO",
                                    adeInfo.getItemData(row, "ACI_NO"));
            }
        }
        for (int i = 0; i < doubtOrder.getNewRows().length; i++) {
            doubtOrder.setItem(doubtOrder.getNewRows()[i], "ACI_NO",
                               adeInfo.getItemData(row, "ACI_NO"));
        }
        for (int i = 0; i < mergeOrder.getNewRows().length; i++) {
            mergeOrder.setItem(mergeOrder.getNewRows()[i], "ACI_NO",
                                adeInfo.getItemData(row, "ACI_NO"));
        }
        sql = StringTool.copyArray(sql, doubtOrder.getUpdateSQL());
        sql = StringTool.copyArray(sql, mergeOrder.getUpdateSQL());
    
        TParm inParm = new TParm();
        Map<String, String[]> inMap = new HashMap<String, String[]>();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result = TIOM_AppServer.executeAction("action.aci.ACIADRAction", "onSave", inParm);
        if (result.getErrCode() < 0) {
            this.messageBox("保存失败 " + result.getErrText());
            return;
        } else {
            if (NEW_FLG == true) {// 新建
                this.messageBox("P0002");// 新增成功
                if (!StringUtil.isNullString(APPLICATION_NO)) {
                    ((TParm) this.getParameter()).runListener("onReturnContent", APPLICATION_NO + ";" + RPDTL_SEQ);//
                    this.closeWindow();
                }
                onClear();
            } else {
                this.messageBox("P0001");// 保存成功
                TParm returnParm = new TParm();
                if (NEW_FLG == false) {
                    returnParm.setData("ACI_NO", ACI_NO);
                }
                this.setReturnValue(returnParm);
            }
        }
    }

    /**
     * 删除
     */
    public void onDelete() {
        if (NEW_FLG == true) {
            return;
        }
        ACIADRM adeInfo = new ACIADRM();
        adeInfo.onQueryByACINo(ACI_NO);
        if (!this.getPopedem("SYSOPERATOR")) {
            if (!Operator.getID().equals(adeInfo.getItemString(0, "REPORT_USER"))) {
                this.messageBox("需高级权限或者上报人自己才能删除记录");
                return;
            }
        }
        boolean uploadFlg =
                StringUtil.isNullString(adeInfo.getItemString(0, "UPLOAD_FLG")) ? false : true;
        if (uploadFlg == true) {
            messageBox("不能删除已完成上报国家的记录");
            return;
        }

        if (this.messageBox("询问", "是否删除", 2) == 0) {
            TParm parm = new TParm();
            parm.setData("ACI_NO", ACI_NO);
            TParm result =
                    TIOM_AppServer.executeAction("action.aci.ACIADRAction", "onDelete", parm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            onClear();
            this.messageBox("P0003");// 删除成功
            TParm returnParm = new TParm();
            returnParm.setData("OPT_TYPE", "DELETE");
            this.setReturnValue(returnParm);
            this.closeWindow();
        }
    }

    /**
     * 打印
     */
    public void onPrint() {
        TParm parm =
                getParmForTag("MR_NO;PAT_NAME;SEX_CODE;SPECIES_CODE;BIRTH_DATE:Timestamp;WEIGHT:int;TEL;"
                        + "DIAG_CODE1;DIAG_CODE2;DIAG_CODE3;DIAG_CODE4;DIAG_CODE5;"
                        + "PERSON_HISTORY;PERSON_REMARK;FAMILY_HISTORY;FAMILY_REMARK;"
                        + "SMOKE_FLG;DRINK_FLG;PREGNANT_FLG;HEPATOPATHY_FLG;NEPHROPATHY_FLG;ALLERGY_FLG;ALLERGY_REMARK;OTHER_FLG;OTHER_REMARK;"
                        + "EVENT_DATE:Timestamp;EVENT_DESC;EVENT_RESULT;"
                        + "ADR_ID1;ADR_DESC1;ADR_ID2;ADR_ID3;ADR_ID4;"
                        + "STOP_REDUCE;AGAIN_APPEAR;DISE_DISTURB;"
                        + "USER_REVIEW;UNIT_REVIEW;"
                        + "REPORT_USER;REPORT_OCC;OCC_REMARK;REPORT_TEL;REPORT_EMAIL;"
                        + "REPORT_DEPT;REPORT_STATION;"
                        + "REPORT_UNIT;UNIT_CONTACTS;UNIT_TEL;REPORT_DATE:Timestamp;REMARK");
        if (!checkUIData(parm)) {// 检查数据有效性
            return;
        }
        String mrNo = parm.getValue("MR_NO");
        mrNo = PatTool.getInstance().checkMrno(mrNo);// 病案号补齐长度
        parm.setData("MR_NO", mrNo);
        if (((TRadioButton) this.getComponent("FIRST_FLG_0")).isSelected()) {// 首次报告
            parm.setData("FIRST_FLG_Y", "TEXT", "■");
        } else {// 跟踪报告
            parm.setData("FIRST_FLG_N", "TEXT", "■");
        }
        if (((TRadioButton) this.getComponent("REPORT_TYPE_0")).isSelected()) {// 报告类型:新的
            parm.setData("REPORT_TYPE_0", "TEXT", "■");
        } else if (((TRadioButton) this.getComponent("REPORT_TYPE_1")).isSelected()) {// 一般
            parm.setData("REPORT_TYPE_1", "TEXT", "■");
        } else if (((TRadioButton) this.getComponent("REPORT_TYPE_2")).isSelected()) {// 严重
            parm.setData("REPORT_TYPE_2", "TEXT", "■");
        }
        if (parm.getValue("SEX_CODE").equals("1")) {// 性别
            parm.setData("SEX_CODE_1", "TEXT", "■");
        } else if (parm.getValue("SEX_CODE").equals("2")) {
            parm.setData("SEX_CODE_2", "TEXT", "■");
        }
        Timestamp birthday = parm.getTimestamp("BIRTH_DATE");
        parm.setData("BIRTH_DATE_YEAR", StringTool.getString(birthday, "yyyy"));// 出生年月日
        parm.setData("BIRTH_DATE_MONTH", StringTool.getString(birthday, "M"));
        parm.setData("BIRTH_DATE_DAY", StringTool.getString(birthday, "d"));
        parm.setData("SPECIES_CODE", this.getText("SPECIES_CODE"));// 民族
        String diagCode = this.getValueString("DIAG_DESC1");
        if (!this.getValue("DIAG_DESC2").equals("")) {
            diagCode += "；" + this.getValueString("DIAG_DESC2");
        }
        if (!this.getValue("DIAG_DESC3").equals("")) {
            diagCode += "；" + this.getValueString("DIAG_DESC3");
        }
        if (!this.getValue("DIAG_DESC4").equals("")) {
            diagCode += "；" + this.getValueString("DIAG_DESC4");
        }
        if (!this.getValue("DIAG_DESC5").equals("")) {
            diagCode += "；" + this.getValueString("DIAG_DESC5");
        }
        parm.setData("DIAG_CODE", diagCode);
        parm.setData("HOSP_DESC", Operator.getHospitalCHNFullName());
        if (parm.getValue("PERSON_HISTORY").equals("0")) {// 无（既往药品不良反应）
            parm.setData("PERSON_HISTORY_0", "TEXT", "■");
        } else if (parm.getValue("PERSON_HISTORY").equals("1")) {// 有
            parm.setData("PERSON_HISTORY_1", "TEXT", "■");
            parm.setData("PERSON_REMARK", "TEXT", parm.getValue("PERSON_REMARK"));
        } else if (parm.getValue("PERSON_HISTORY").equals("9")) {// 不详
            parm.setData("PERSON_HISTORY_1", "TEXT", "■");
        }
        if (parm.getValue("FAMILY_HISTORY").equals("0")) {// 无（家族药品不良反应）
            parm.setData("FAMILY_HISTORY_0", "TEXT", "■");
        } else if (parm.getValue("FAMILY_HISTORY").equals("1")) {// 有
            parm.setData("FAMILY_HISTORY_1", "TEXT", "■");
            parm.setData("FAMILY_REMARK", "TEXT", parm.getValue("FAMILY_REMARK"));
        } else if (parm.getValue("FAMILY_HISTORY").equals("9")) {// 不详
            parm.setData("FAMILY_HISTORY_9", "TEXT", "■");
        }
        if (parm.getValue("SMOKE_FLG").equals("Y")) {// 吸烟史
            parm.setData("SMOKE_FLG", "TEXT", "■");
        }
        if (parm.getValue("DRINK_FLG").equals("Y")) {// 饮酒史
            parm.setData("DRINK_FLG", "TEXT", "■");
        }
        if (parm.getValue("PREGNANT_FLG").equals("Y")) {// 妊娠期
            parm.setData("PREGNANT_FLG", "TEXT", "■");
        }
        if (parm.getValue("HEPATOPATHY_FLG").equals("Y")) {// 肝病史
            parm.setData("HEPATOPATHY_FLG", "TEXT", "■");
        }
        if (parm.getValue("NEPHROPATHY_FLG").equals("Y")) {// 肾病史
            parm.setData("NEPHROPATHY_FLG", "TEXT", "■");
        }
        if (parm.getValue("ALLERGY_FLG").equals("Y")) {// 过敏史
            parm.setData("ALLERGY_FLG", "TEXT", "■");
            parm.setData("ALLERGY_REMARK", "TEXT", parm.getValue("ALLERGY_REMARK"));
        }
        if (parm.getValue("OTHER_FLG").equals("Y")) {// 其他
            parm.setData("OTHER_FLG", "TEXT", "■");
            parm.setData("OTHER_REMARK", "TEXT", parm.getValue("OTHER_REMARK"));
        }
        String adrDesc = this.getValueString("ADR_DESC1");// 不良反应/事件名称
        if (!this.getValue("ADR_DESC2").equals("")) {
            adrDesc += "；" + this.getValueString("ADR_DESC2");
        }
        if (!this.getValue("ADR_DESC3").equals("")) {
            adrDesc += "；" + this.getValueString("ADR_DESC3");
        }
        if (!this.getValue("ADR_DESC4").equals("")) {
            adrDesc += "；" + this.getValueString("ADR_DESC4");
        }
        parm.setData("ADR_DESC", adrDesc);// 不良反应/事件名称
        Timestamp eventDate = parm.getTimestamp("EVENT_DATE");
        parm.setData("EVENT_DATE_YEAR", StringTool.getString(eventDate, "yyyy"));// 不良反应/事件发生日期（年月日）
        parm.setData("EVENT_DATE_MONTH", StringTool.getString(eventDate, "M"));
        parm.setData("EVENT_DATE_DAY", StringTool.getString(eventDate, "d"));
        if (parm.getValue("EVENT_RESULT").equals("0")) {// 痊愈（不良反应/事件的结果）
            parm.setData("EVENT_RESULT_0", "TEXT", "■");
        } else if (parm.getValue("EVENT_RESULT").equals("1")) {// 好转
            parm.setData("EVENT_RESULT_1", "TEXT", "■");
        } else if (parm.getValue("EVENT_RESULT").equals("2")) {// 未好转
            parm.setData("EVENT_RESULT_2", "TEXT", "■");
        } else if (parm.getValue("EVENT_RESULT").equals("3")) {// 有后遗症
            parm.setData("EVENT_RESULT_3", "TEXT", "■");
            parm.setData("RESULT_REMARK", "TEXT", this.getValue("RESULT_REMARK"));
        } else if (parm.getValue("EVENT_RESULT").equals("4")) {// 死亡
            parm.setData("EVENT_RESULT_4", "TEXT", "■");
            parm.setData("DIED_REMARK", "TEXT", this.getValue("RESULT_REMARK"));
            Timestamp diedDate = parm.getTimestamp("DIED_DATE");
            parm.setData("DIED_DATE_YEAR", StringTool.getString(diedDate, "yyyy"));// 不良反应/事件发生日期（年月日）
            parm.setData("DIED_DATE_MONTH", StringTool.getString(diedDate, "M"));
            parm.setData("DIED_DATE_DAY", StringTool.getString(diedDate, "d"));
        } else if (parm.getValue("EVENT_RESULT").equals("9")) {// 不详
            parm.setData("EVENT_RESULT_9", "TEXT", "■");
        }
        if (parm.getValue("STOP_REDUCE").equals("0")) {// 否（停药或减量后，反应/事件是否小时或减轻？）
            parm.setData("STOP_REDUCE_0", "TEXT", "■");
        } else if (parm.getValue("STOP_REDUCE").equals("1")) {// 是
            parm.setData("STOP_REDUCE_1", "TEXT", "■");
        } else if (parm.getValue("STOP_REDUCE").equals("2")) {// 未停药或未减量
            parm.setData("STOP_REDUCE_2", "TEXT", "■");
        } else if (parm.getValue("STOP_REDUCE").equals("9")) {// 不明
            parm.setData("STOP_REDUCE_9", "TEXT", "■");
        }
        if (parm.getValue("AGAIN_APPEAR").equals("0")) {// 否（再次使用可疑药品后是否再次出现同样反应/事件？）
            parm.setData("AGAIN_APPEAR_0", "TEXT", "■");
        } else if (parm.getValue("AGAIN_APPEAR").equals("1")) {// 是
            parm.setData("AGAIN_APPEAR_1", "TEXT", "■");
        } else if (parm.getValue("AGAIN_APPEAR").equals("2")) {// 未再使用
            parm.setData("AGAIN_APPEAR_2", "TEXT", "■");
        } else if (parm.getValue("AGAIN_APPEAR").equals("9")) {// 不明
            parm.setData("AGAIN_APPEAR_9", "TEXT", "■");
        }
        if (parm.getValue("DISE_DISTURB").equals("0")) {// 不明显（对原患疾病的影响）
            parm.setData("DISE_DISTURB_0", "TEXT", "■");
        } else if (parm.getValue("DISE_DISTURB").equals("1")) {// 病程延长
            parm.setData("DISE_DISTURB_1", "TEXT", "■");
        } else if (parm.getValue("DISE_DISTURB").equals("2")) {// 病情加重
            parm.setData("DISE_DISTURB_2", "TEXT", "■");
        } else if (parm.getValue("DISE_DISTURB").equals("3")) {// 导致后遗症
            parm.setData("DISE_DISTURB_3", "TEXT", "■");
        } else if (parm.getValue("DISE_DISTURB").equals("4")) {// 导致死亡
            parm.setData("DISE_DISTURB_4", "TEXT", "■");
        }
        if (parm.getValue("USER_REVIEW").equals("0")) {// 肯定（报告人评价）
            parm.setData("USER_REVIEW_0", "TEXT", "■");
        } else if (parm.getValue("USER_REVIEW").equals("1")) {// 很可能
            parm.setData("USER_REVIEW_1", "TEXT", "■");
        } else if (parm.getValue("USER_REVIEW").equals("2")) {// 可能
            parm.setData("USER_REVIEW_2", "TEXT", "■");
        } else if (parm.getValue("USER_REVIEW").equals("3")) {// 可能无关
            parm.setData("USER_REVIEW_3", "TEXT", "■");
        } else if (parm.getValue("USER_REVIEW").equals("4")) {// 待评价
            parm.setData("USER_REVIEW_4", "TEXT", "■");
        } else if (parm.getValue("USER_REVIEW").equals("5")) {// 无法评价
            parm.setData("USER_REVIEW_5", "TEXT", "■");
        }
        if (parm.getValue("UNIT_REVIEW").equals("0")) {// 肯定（报告单位评价）
            parm.setData("UNIT_REVIEW_0", "TEXT", "■");
        } else if (parm.getValue("UNIT_REVIEW").equals("1")) {// 很可能
            parm.setData("UNIT_REVIEW_1", "TEXT", "■");
        } else if (parm.getValue("UNIT_REVIEW").equals("2")) {// 可能
            parm.setData("UNIT_REVIEW_2", "TEXT", "■");
        } else if (parm.getValue("UNIT_REVIEW").equals("3")) {// 可能无关
            parm.setData("UNIT_REVIEW_3", "TEXT", "■");
        } else if (parm.getValue("UNIT_REVIEW").equals("4")) {// 待评价
            parm.setData("UNIT_REVIEW_4", "TEXT", "■");
        } else if (parm.getValue("UNIT_REVIEW").equals("5")) {// 无法评价
            parm.setData("UNIT_REVIEW_5", "TEXT", "■");
        }
        parm.setData("REPORT_USER", this.getText("REPORT_USER"));// 上报人
        if (parm.getValue("REPORT_OCC").equals("1")) {// 医生（报告人职业）
            parm.setData("REPORT_OCC_1", "TEXT", "■");
        } else if (parm.getValue("REPORT_OCC").equals("2")) {// 药师
            parm.setData("REPORT_OCC_2", "TEXT", "■");
        } else if (parm.getValue("REPORT_OCC").equals("3")) {// 护士
            parm.setData("REPORT_OCC_3", "TEXT", "■");
        } else if (parm.getValue("REPORT_OCC").equals("6")) {// 其他
            parm.setData("REPORT_OCC_6", "TEXT", "■");
            parm.setData("OCC_REMARK", "TEXT", this.getValue("OCC_REMARK"));
        }
        parm.setData("UNIT_CONTACTS", this.getText("UNIT_CONTACTS"));// 联系人
        Timestamp reportDate = parm.getTimestamp("REPORT_DATE");
        parm.setData("REPORT_DATE_YEAR", StringTool.getString(reportDate, "yyyy"));// 报告日期（年月日）
        parm.setData("REPORT_DATE_MONTH", StringTool.getString(reportDate, "M"));
        parm.setData("REPORT_DATE_DAY", StringTool.getString(reportDate, "d"));
        TParm doubtParm = doubtTable.getShowParmValue();
        if (doubtParm.getValue("ORDER_DESC", doubtParm.getCount()).equals("")) {
            doubtParm.removeRow(doubtParm.getCount() - 1);
        }
        for (int i = 0; i < doubtParm.getCount(); i++) {
            if (!doubtParm.getValue("GOODS_DESC", i).equals("")) {
                doubtParm.setData("GOODS_DESC", i, doubtParm.getValue("GOODS_DESC", i) + "\r\n剂型："
                        + doubtParm.getValue("DOSE_CODE", i));
            } else if (!doubtParm.getValue("DOSE_CODE", i).equals("")) {
                doubtParm.setData("GOODS_DESC", i, "剂型：" + doubtParm.getValue("DOSE_CODE", i));
            }
            String freqStr = doubtParm.getValue("FREQ_CODE", i);
            if (freqStr.length() > 0 && !doubtParm.getValue("ROUTE_CODE", i).equals("")) {
                freqStr += "，" + doubtParm.getValue("ROUTE_CODE", i);
            } else if (!doubtParm.getValue("ROUTE_CODE", i).equals("")) {
                freqStr += doubtParm.getValue("ROUTE_CODE", i);
            }
            if (freqStr.length() > 0 && !doubtParm.getValue("MEDI_QTY", i).equals("")) {
                freqStr +=
                        "，" + doubtParm.getValue("MEDI_QTY", i)
                                + doubtParm.getValue("MEDI_UNIT", i);
            } else if (!doubtParm.getValue("ROUTE_CODE", i).equals("")) {
                freqStr += doubtParm.getValue("MEDI_QTY", i) + doubtParm.getValue("MEDI_UNIT", i);
            }
            doubtParm.setData("FREQ_CODE", i, freqStr);
            String dateStr = "";
            if (!doubtParm.getValue("MEDI_START_DATE", i).equals("")) {
                dateStr += "开始时间：\r\n" + doubtParm.getValue("MEDI_START_DATE", i);
            }
            if (dateStr.length() > 0 && !doubtParm.getValue("MEDI_END_DATE", i).equals("")) {
                dateStr += "\r\n结束时间：\r\n" + doubtParm.getValue("MEDI_END_DATE", i);
            }
            doubtParm.setData("MEDI_START_DATE", i, dateStr);
        }
        if (doubtParm.getCount() < 3) {
            for (int i = doubtParm.getCount(); i < 3; i++) {
                for (int j = 0; j < doubtParm.getNames().length; j++) {
                    doubtParm.addData(doubtParm.getNames()[j], "");
                }
            }
        }
        doubtParm.setCount(doubtParm.getCount("ORDER_DESC"));
        String doubtStr = "怀疑药品";
        if (doubtParm.getCount() > 3) {
            for (int i = 0; i < doubtParm.getCount() - 3; i++) {
                doubtStr = " " + doubtStr;
            }
        }
        parm.setData("DOUBT_TEXT", "TEXT", doubtStr);
        doubtParm.addData("SYSTEM", "COLUMNS", "HYGIENE_TRADE_CODE");
        doubtParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        doubtParm.addData("SYSTEM", "COLUMNS", "GOODS_DESC");
        doubtParm.addData("SYSTEM", "COLUMNS", "MAN_DESC");
        doubtParm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        doubtParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
        doubtParm.addData("SYSTEM", "COLUMNS", "MEDI_START_DATE");
        doubtParm.addData("SYSTEM", "COLUMNS", "MEDI_REMARK");
        parm.setData("DOUBT_TABLE", doubtParm.getData());
        TParm mergeParm = mergeTable.getShowParmValue();
        if (mergeParm.getValue("ORDER_DESC", mergeParm.getCount()).equals("")) {
            mergeParm.removeRow(mergeParm.getCount() - 1);
        }
        for (int i = 0; i < mergeParm.getCount(); i++) {
            if (!mergeParm.getValue("GOODS_DESC", i).equals("")) {
                mergeParm.setData("GOODS_DESC", i, mergeParm.getValue("GOODS_DESC", i) + "\r\n剂型："
                        + mergeParm.getValue("DOSE_CODE", i));
            } else if (!mergeParm.getValue("DOSE_CODE", i).equals("")) {
                mergeParm.setData("GOODS_DESC", i, "剂型：" + mergeParm.getValue("DOSE_CODE", i));
            }
            String freqStr = mergeParm.getValue("FREQ_CODE", i);
            if (freqStr.length() > 0 && !mergeParm.getValue("ROUTE_CODE", i).equals("")) {
                freqStr += "，" + mergeParm.getValue("ROUTE_CODE", i);
            } else if (!mergeParm.getValue("ROUTE_CODE", i).equals("")) {
                freqStr += mergeParm.getValue("ROUTE_CODE", i);
            }
            if (freqStr.length() > 0 && !mergeParm.getValue("MEDI_QTY", i).equals("")) {
                freqStr +=
                        "，" + mergeParm.getValue("MEDI_QTY", i)
                                + mergeParm.getValue("MEDI_UNIT", i);
            } else if (!mergeParm.getValue("ROUTE_CODE", i).equals("")) {
                freqStr += mergeParm.getValue("MEDI_QTY", i) + mergeParm.getValue("MEDI_UNIT", i);
            }
            mergeParm.setData("FREQ_CODE", i, freqStr);
            String dateStr = "";
            if (!mergeParm.getValue("MEDI_START_DATE", i).equals("")) {
                dateStr += "开始时间：\r\n" + mergeParm.getValue("MEDI_START_DATE", i);
            }
            if (dateStr.length() > 0 && !mergeParm.getValue("MEDI_END_DATE", i).equals("")) {
                dateStr += "\r\n结束时间：\r\n" + mergeParm.getValue("MEDI_END_DATE", i);
            }
            mergeParm.setData("MEDI_START_DATE", i, dateStr);
        }
        if (mergeParm.getCount() < 3) {
            for (int i = mergeParm.getCount(); i < 3; i++) {
                for (int j = 0; j < mergeParm.getNames().length; j++) {
                    mergeParm.addData(mergeParm.getNames()[j], "");
                }
            }
        }
        mergeParm.setCount(mergeParm.getCount("ORDER_DESC"));
        String mergeStr = "并用药品";
        if (mergeParm.getCount() > 3) {
            for (int i = 0; i < mergeParm.getCount() - 3; i++) {
                mergeStr = " " + mergeStr;
            }
        }
        parm.setData("MERGE_TEXT", "TEXT", mergeStr);
        mergeParm.addData("SYSTEM", "COLUMNS", "HYGIENE_TRADE_CODE");
        mergeParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        mergeParm.addData("SYSTEM", "COLUMNS", "GOODS_DESC");
        mergeParm.addData("SYSTEM", "COLUMNS", "MAN_DESC");
        mergeParm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        mergeParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
        mergeParm.addData("SYSTEM", "COLUMNS", "MEDI_START_DATE");
        mergeParm.addData("SYSTEM", "COLUMNS", "MEDI_REMARK");
        parm.setData("MERGE_TABLE", mergeParm.getData());
        openPrintDialog("%ROOT%\\config\\prt\\ACI\\ACIADRReport.jhw", parm, false);
    }
    
    /**
     * 病案号查询
     */
    public void onMrNo(String MR_NO) {
        String mrNo = "";
        String caseNo="";
        if (!MR_NO.equals("")) {
            mrNo = MR_NO;
            caseNo=CASE_NO;
        } else {
            mrNo = this.getValueString("MR_NO").trim();
            if (mrNo.equals("")) {
                return;
            } else {
               
                mrNo = PatTool.getInstance().checkMrno(mrNo);
                MR_NO=mrNo;
            }
        }
        TParm sysPatInfo = PatTool.getInstance().getInfoForMrno(mrNo);
        if (sysPatInfo.getCount() > 0) {
            this.setValue("MR_NO", mrNo);// 病案号
            this.setValue("PAT_NAME", sysPatInfo.getValue("PAT_NAME", 0));// 姓名
            this.setValue("SEX_CODE", sysPatInfo.getValue("SEX_CODE", 0));// 性别 SEX
            this.setValue("SPECIES_CODE", sysPatInfo.getValue("SPECIES_CODE", 0));// 民族 FOLK
            this.setValue("BIRTH_DATE", sysPatInfo.getTimestamp("BIRTH_DATE", 0));// 出生年月 BIRTH_DATE
            this.setValue("WEIGHT", sysPatInfo.getValue("WEIGHT", 0));// 体重
            this.setValue("TEL", sysPatInfo.getValue("TEL_HOME", 0));// 联系方式 TEL
        }
        if(CASE_NO.equals("")){
            caseNo=getLastCaseNo(mrNo);
            CASE_NO=caseNo;
        }
        patOrders = getPatOrders("I", caseNo);// 查询
    }

    /**
     * 获得最后一次就诊的CASE_NO
     * @param mrNo
     * @return
     */
    public String getLastCaseNo(String mrNo) {
        String sql =
                "SELECT CASE_NO FROM ADM_INP WHERE MR_NO = '#' AND CANCEL_FLG <> 'Y' ORDER BY CASE_NO DESC";
        sql = sql.replaceFirst("#", mrNo);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("获得就诊信息失败");
            return "";
        }
        return result.getValue("CASE_NO", 0);
    }
    
    /**
     * 科室选择事件
     */
    public void onDEPT() {
        this.clearValue("REPORT_STATION");
        String deptCode = (String) this.callFunction("UI|REPORT_DEPT|getValue");
        TTextFormat stationCode = (TTextFormat) this.getComponent("REPORT_STATION");
        String sql =
                "SELECT STATION_CODE AS ID,STATION_DESC AS NAME,PY1 FROM SYS_STATION WHERE DEPT_CODE = '"
                        + deptCode + "' ORDER BY SEQ,STATION_CODE";
        stationCode.setPopupMenuSQL(sql);
        stationCode.onQuery();
    }
    
    /**
     * 既往史/家族史单选按钮事件
     */
    public void onChooseHistory() {
        if (this.getValue("PERSON_HISTORY").equals("1")) {
            this.callFunction("UI|PERSON_REMARK|setEnabled", true);
        } else {
            this.callFunction("UI|PERSON_REMARK|setEnabled", false);
        }
        if (this.getValue("FAMILY_HISTORY").equals("1")) {
            this.callFunction("UI|FAMILY_REMARK|setEnabled", true);
        } else {
            this.callFunction("UI|FAMILY_REMARK|setEnabled", false);
        }
    }

    /**
     * 过敏史复选框事件
     */
    public void onChooseAllergy() {
        if (((TCheckBox) this.getComponent("ALLERGY_FLG")).isSelected()) {
            this.callFunction("UI|ALLERGY_REMARK|setEnabled", true);
        } else {
            this.callFunction("UI|ALLERGY_REMARK|setEnabled", false);
        }
    }

    /**
     * 其他复选框事件
     */
    public void onChooseOther() {
        if (((TCheckBox) this.getComponent("OTHER_FLG")).isSelected()) {
            this.callFunction("UI|OTHER_REMARK|setEnabled", true);
        } else {
            this.callFunction("UI|OTHER_REMARK|setEnabled", false);
        }
    }

    /**
     * 不良事件后果单选按钮事件
     */
    public void onChooseResult() {
        if (this.getValue("EVENT_RESULT").equals("3")) {
            this.callFunction("UI|RESULT_LABEL|setVisible", true);
            this.setValue("RESULT_LABEL", "表    现");
            this.callFunction("UI|RESULT_REMARK|setVisible", true);
            this.callFunction("UI|DIED_LABEL|setVisible", false);
            this.callFunction("UI|DIED_DATE|setVisible", false);
        } else if (this.getValue("EVENT_RESULT").equals("4")) {
            this.callFunction("UI|RESULT_LABEL|setVisible", true);
            this.setValue("RESULT_LABEL", "直接死因");
            this.callFunction("UI|RESULT_REMARK|setVisible", true);
            this.callFunction("UI|DIED_LABEL|setVisible", true);
            this.callFunction("UI|DIED_DATE|setVisible", true);
        } else {
            this.callFunction("UI|RESULT_LABEL|setVisible", false);
            this.callFunction("UI|RESULT_REMARK|setVisible", false);
            this.callFunction("UI|DIED_LABEL|setVisible", false);
            this.callFunction("UI|DIED_DATE|setVisible", false);
        }
    }

    /**
     * 上报人员选择
     */
    public void onChooseReportUser() {
        String reportUserID=this.getValueString("REPORT_USER");
        initReportUserInfo(reportUserID);
    }

    /**
     * 职业下拉框选择事件
     */
    public void onChooseOcc() {
        if (this.getValue("REPORT_OCC").equals("6")) {// 6=其他注记
            this.callFunction("UI|OCC_REMARK|setEnabled", true);
        } else {
            this.callFunction("UI|OCC_REMARK|setEnabled", false);
        }
    }

    /**
     * 清空
     */
    public void onClear() {
        NEW_FLG = true;// 新增标记（true新增;false更新）
        ACI_NO = "";
        PARM = new TParm();
        ADM_TYPE = "";
        CASE_NO = "";
        MR_NO = "";
        APPLICATION_NO = "";
        MONITER_CODE = "";
        RPDTL_SEQ = "";
        ORDER_DATE = null;
        ORDER_CODE = "";
        patOrders = new TParm();
        this.callFunction("UI|FIRST_FLG_0|setSelected", true);
        this.callFunction("UI|REPORT_TYPE_0|setSelected", true);
        clearValue("MR_NO;PAT_NAME;SEX_CODE;SPECIES_CODE;BIRTH_DATE;WEIGHT;TEL;"
                + "DIAG_DESC1;DIAG_DESC2;DIAG_DESC3;DIAG_DESC4;DIAG_DESC5;"
                + "PERSON_REMARK;FAMILY_REMARK;"
                + "SMOKE_FLG;DRINK_FLG;PREGNANT_FLG;HEPATOPATHY_FLG;NEPHROPATHY_FLG;ALLERGY_FLG;ALLERGY_REMARK;OTHER_FLG;OTHER_REMARK;"
                + "ADR_DESC1;ADR_DESC2;ADR_DESC3;ADR_DESC4;"
                + "EVENT_DATE;EVENT_DESC;RESULT_REMARK;DIED_DATE;"
                + "REPORT_USER;REPORT_OCC;OCC_REMARK;REPORT_TEL;REPORT_EMAIL;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_UNIT;UNIT_CONTACTS;UNIT_TEL;REPORT_DATE;REMARK");
        this.callFunction("UI|PERSON_REMARK|setEnabled", false);
        this.callFunction("UI|FAMILY_REMARK|setEnabled", false);
        this.callFunction("UI|ALLERGY_REMARK|setEnabled", false);
        this.callFunction("UI|OTHER_REMARK|setEnabled", false);
        this.callFunction("UI|RESULT_LABEL|setVisible", false);
        this.callFunction("UI|RESULT_REMARK|setVisible", false);
        this.callFunction("UI|DIED_LABEL|setVisible", false);
        this.callFunction("UI|DIED_DATE|setVisible", false);
        doubtTable.setParmValue(new TParm());
        mergeTable.setParmValue(new TParm());
        initUI();
    }

    /**
     * 诊断下拉框返回值
     * 
     * @param tag
     * @param obj
     */
    public void popICDReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (tag.equals("DIAG_DESC1")) {
            if (parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC2"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC3"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC4"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC5"))) {
                this.messageBox("不允许重复");
                this.setValue(tag, "");
                return;
            }
        } else if (tag.equals("DIAG_DESC2")) {
            if (parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC1"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC3"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC4"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC5"))) {
                this.messageBox("不允许重复");
                this.setValue(tag, "");
                return;
            }
        } else if (tag.equals("DIAG_DESC3")) {
            if (parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC2"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC1"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC4"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC5"))) {
                this.messageBox("不允许重复");
                this.setValue(tag, "");
                return;
            }
        } else if (tag.equals("DIAG_DESC4")) {
            if (parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC2"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC3"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC1"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC5"))) {
                this.messageBox("不允许重复");
                this.setValue(tag, "");
                return;
            }
        } else if (tag.equals("DIAG_DESC5")) {
            if (parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC2"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC3"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC4"))
                    || parm.getValue("ICD_CHN_DESC").equals(this.getValue("DIAG_DESC1"))) {
                this.messageBox("不允许重复");
                this.setValue(tag, "");
                return;
            }
        }
        this.setValue(tag, parm.getValue("ICD_CHN_DESC"));
        this.setValue("DIAG_CODE" + tag.charAt(tag.length() - 1), parm.getValue("ICD_CODE"));
        if (StringUtil.isNullString(tag)) {
            return;
        } else if (tag.equals("DIAG_DESC1")) {
            if (!this.getValue(tag).equals("")) {
                this.callFunction("UI|DIAG_DESC2|setEnabled", true);
            }
        } else if (tag.equals("DIAG_DESC2")) {
            if (!this.getValue(tag).equals("")) {
                this.callFunction("UI|DIAG_DESC3|setEnabled", true);
            }
        } else if (tag.equals("DIAG_DESC3")) {
            if (!this.getValue(tag).equals("")) {
                this.callFunction("UI|DIAG_DESC4|setEnabled", true);
            }
        } else if (tag.equals("DIAG_DESC4")) {
            if (!this.getValue(tag).equals("")) {
                this.callFunction("UI|DIAG_DESC5|setEnabled", true);
            }
        }
    }
    
    /**
     * 添加SYS_FEE弹出窗口
     * 
     * @param com
     *            Component
     * @param row
     *            int
     * @param column
     *            int
     */
    public void onDoubtOrderEditComponent(Component com, int row, int column) {
        // 求出当前列号
        column = doubtTable.getColumnModel().getColumnIndex(column);
        String columnName = doubtTable.getParmMap(column);
        if (!"ORDER_DESC".equalsIgnoreCase(columnName)) {
            return;
        }
        if (!(com instanceof TTextField)) {
            return;
        }
        if (!StringUtil.isNullString(doubtOrder.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // 给table上的新text增加sys_fee弹出窗口
        textfield.setPopupMenuParameter("ORDER",
                                        getConfigParm()
                                                .newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
                                        parm);
        // 给新text增加接受sys_fee弹出窗口的回传值
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popOrderReturn");
    }

    /**
     * 添加SYS_FEE弹出窗口
     * 
     * @param com
     *            Component
     * @param row
     *            int
     * @param column
     *            int
     */
    public void onBesideOrderEditComponent(Component com, int row, int column) {
        // 求出当前列号
        column = mergeTable.getColumnModel().getColumnIndex(column);
        String columnName = mergeTable.getParmMap(column);
        if (!"ORDER_DESC".equalsIgnoreCase(columnName)) {
            return;
        }
        if (!(com instanceof TTextField)) {
            return;
        }
        if (!StringUtil.isNullString(mergeOrder.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // 给table上的新text增加sys_fee弹出窗口
        textfield.setPopupMenuParameter("ORDER",
                                        getConfigParm()
                                                .newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
                                        parm);
        // 给新text增加接受sys_fee弹出窗口的回传值
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popOrderReturn");
    }

    /**
     * 医嘱下拉框返回值
     * 
     * @param tag
     * @param obj
     */
    public void popOrderReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        TParm patOrderRow=new TParm();//记录病患医嘱中和这个返回值匹配的行数据
        if(patOrders!=null&&patOrders.getCount()>0){
            int count=0;
            for (int i = 0; i < patOrders.getCount(); i++) {
                if(patOrders.getValue("ORDER_CODE", i).equals(parm.getValue("ORDER_CODE"))){
                    patOrderRow.addRowData(patOrders, i);
                    count++;
                }
            }
            if(count==0){

                
                if (this.messageBox("提示", "该病患不存在该医嘱，是否继续？", 0) != 0) {
                    return;
                }
             
            } else if (count > 1) {//this.messageBox("多行选择");
                Object obj1 = this.openDialog("%ROOT%\\config\\aci\\ACIADROrderChoose.x", patOrderRow);
                if (obj1 != null) {
                    patOrderRow = (TParm) obj1;
                } else {
                    return;
                }
            }
        }
        if (((TPanel) this.getComponent("DOUBT_PANEL")).isShowing()) {
            int row = doubtTable.getSelectedRow();
            if (!StringUtil.isNullString(doubtTable.getItemString(row, "ORDER_CODE"))) {
                return;
            }
            doubtTable.acceptText();
            doubtOrder.setItem(row, "ACI_NO", "");
            doubtOrder.setItem(row, "TYPE", "0");// 怀疑药品
            doubtOrder.setItem(row, "SEQ", doubtOrder.getNextSeqNo());
            doubtOrder.setItem(row, "HYGIENE_TRADE_CODE", parm.getData("HYGIENE_TRADE_CODE"));
            doubtOrder.setItem(row, "ORDER_CODE", parm.getData("ORDER_CODE"));
            doubtOrder.setItem(row, "ORDER_DESC", parm.getData("ORDER_DESC"));
            doubtOrder.setItem(row, "GOODS_DESC", parm.getData("GOODS_DESC"));
            TParm phaParm = doubtOrder.getPhaOtherData(row);
            if (phaParm.getErrCode() < 0) {
                err("ERR:" + phaParm.getErrCode() + phaParm.getErrText() + phaParm.getErrName());
                this.messageBox("查询药品信息失败");
                return;
            }
            doubtOrder.setItem(row, "DOSE_CODE", phaParm.getData("DOSE_CODE", 0));
            doubtOrder.setItem(row, "MAN_DESC", phaParm.getData("MAN_CHN_DESC", 0));
            doubtOrder.setItem(row, "FREQ_CODE", phaParm.getData("FREQ_CODE", 0));
            doubtOrder.setItem(row, "ROUTE_CODE", phaParm.getData("ROUTE_CODE", 0));
            doubtOrder.setItem(row, "UNIT_CODE", phaParm.getData("UNIT_CODE", 0));
            doubtOrder.setItem(row, "MEDI_QTY", phaParm.getDouble("MEDI_QTY", 0));
            doubtOrder.setItem(row, "MEDI_UNIT", phaParm.getData("MEDI_UNIT", 0));
//            TTextFormat BATCH_NO = ((TTextFormat) doubtTable.getItem("BATCH_NO"));
//            String sql =
//                    " SELECT BATCH_NO,VALID_DATE FROM IND_VERIFYIND WHERE ORDER_CODE = '#' ORDER BY SEQ_NO ";
//            sql = sql.replaceFirst("#", parm.getValue("ORDER_CODE"));
//            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//            BATCH_NO.setPopupMenuSQL(sql);
//            BATCH_NO.onQuery();
//            doubtOrder.setItem(row, "BATCH_NO", result.getData("BATCH_NO", 0));
            doubtOrder.setItem(row, "BATCH_NO", "");
            if(!patOrderRow.getValue("ORDER_CODE").equals("")){
                doubtOrder.setItem(row, "MEDI_START_DATE", patOrderRow.getData("START_DTTM"));
                doubtOrder.setItem(row, "MEDI_END_DATE", patOrderRow.getData("DC_DATE")==null?patOrderRow.getData("START_DTTM"):patOrderRow.getData("DC_DATE"));
           
            }else{
              doubtOrder.setItem(row, "MEDI_START_DATE", "");
              doubtOrder.setItem(row, "MEDI_END_DATE", "");
            }
            doubtOrder.setItem(row, "MEDI_REMARK", "");
            doubtOrder.setItem(row, "OPT_USER", Operator.getID());
            doubtOrder.setItem(row, "OPT_DATE", doubtOrder.getDBTime());
            doubtOrder.setItem(row, "OPT_TERM", Operator.getIP());
            doubtOrder.setActive(row, true);
            row = doubtOrder.insertRow();
            doubtOrder.setItem(row, "ACI_NO", "");
            doubtOrder.setItem(row, "TYPE", "0");// 怀疑药品
            doubtOrder.setItem(row, "OPT_USER", Operator.getID());
            doubtOrder.setItem(row, "OPT_DATE", doubtOrder.getDBTime());
            doubtOrder.setItem(row, "OPT_TERM", Operator.getIP());
            doubtOrder.setActive(row, false);
            doubtTable.setDSValue();
            doubtTable.getTable().grabFocus();
            doubtTable.setSelectedRow(doubtTable.getRowCount() - 1);
            doubtTable.setSelectedColumn(2);
            //如果link_no不为空，将相应的并用药品填到并用药品table
            if(!patOrderRow.getValue("ORDER_CODE").equals("")&&!patOrderRow.getValue("LINK_NO").equals("")){
                for (int i = 0; i < patOrders.getCount(); i++) {
                    if (patOrderRow.getValue("LINK_NO").equals(patOrders.getValue("LINK_NO", i))
                            && !patOrderRow.getValue("ORDER_CODE").equals(patOrders.getValue("ORDER_CODE", i))) {
                        int row1 = mergeTable.getRowCount() - 1;
                        if (!StringUtil.isNullString(mergeTable.getItemString(row1, "ORDER_CODE"))) {
                            return;
                        }
                        String phaSql =
                                "SELECT * FROM SYS_FEE WHERE ORDER_CODE='#'".replaceFirst("#", patOrders.getValue("ORDER_CODE", i));
                        TParm sysfee = new TParm(TJDODBTool.getInstance().select(phaSql));
                        if (sysfee.getErrCode() < 0) {
                            continue;
                        }
                        mergeTable.acceptText();
                        mergeOrder.setItem(row, "ACI_NO", "");
                        mergeOrder.setItem(row, "TYPE", "1");// 并用药品
                        mergeOrder.setItem(row, "SEQ", mergeOrder.getItemInt(row, "#ID#"));
                        mergeOrder.setItem(row, "HYGIENE_TRADE_CODE", sysfee.getData("HYGIENE_TRADE_CODE",0));
                        mergeOrder.setItem(row, "ORDER_CODE", sysfee.getData("ORDER_CODE",0));
                        mergeOrder.setItem(row, "ORDER_DESC", sysfee.getData("ORDER_DESC",0));
                        mergeOrder.setItem(row, "GOODS_DESC", sysfee.getData("GOODS_DESC",0));
                        TParm phaParm1 = mergeOrder.getPhaOtherData(row1);
                        if (phaParm1.getErrCode() < 0) {
                            err("ERR:" + phaParm1.getErrCode() + phaParm1.getErrText() + phaParm1.getErrName());
                            this.messageBox("查询药品信息失败");
                            return;
                        }
                        mergeOrder.setItem(row, "DOSE_CODE", phaParm1.getData("DOSE_CODE", 0));
                        mergeOrder.setItem(row, "MAN_DESC", phaParm1.getData("MAN_CHN_DESC", 0));
                        mergeOrder.setItem(row, "FREQ_CODE", phaParm1.getData("FREQ_CODE", 0));
                        mergeOrder.setItem(row, "ROUTE_CODE", phaParm1.getData("ROUTE_CODE", 0));
                        mergeOrder.setItem(row, "UNIT_CODE", phaParm1.getData("UNIT_CODE", 0));
                        mergeOrder.setItem(row, "MEDI_QTY", phaParm1.getDouble("MEDI_QTY", 0));
                        mergeOrder.setItem(row, "MEDI_UNIT", phaParm1.getData("MEDI_UNIT", 0));
//                        TTextFormat BATCH_NO = (TTextFormat) mergeTable.getItem("BATCH_NO");
//                        String sql =
//                                " SELECT BATCH_NO,VALID_DATE FROM IND_VERIFYIND WHERE ORDER_CODE = '#' ORDER BY SEQ_NO ";
//                        sql = sql.replaceFirst("#", parm.getValue("ORDER_CODE"));
//                        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//                        BATCH_NO.setPopupMenuSQL(sql);
//                        BATCH_NO.onQuery();
//                        mergeOrder.setItem(row, "BATCH_NO", result.getData("BATCH_NO", 0));
                        mergeOrder.setItem(row, "BATCH_NO", "");
                        mergeOrder.setItem(row, "MEDI_START_DATE", patOrders.getData("START_DTTM",i));
                        mergeOrder.setItem(row, "MEDI_END_DATE", patOrders.getData("DC_DATE", i) == null ? 
                                patOrders.getData("START_DTTM", i) : patOrders.getData("DC_DATE", i));
                        mergeOrder.setItem(row, "MEDI_REMARK", "");
                        mergeOrder.setItem(row, "OPT_USER", Operator.getID());
                        mergeOrder.setItem(row, "OPT_DATE", mergeOrder.getDBTime());
                        mergeOrder.setItem(row, "OPT_TERM", Operator.getIP());
                        mergeOrder.setActive(row, true);
                        row = mergeOrder.insertRow();
                        mergeOrder.setItem(row, "ACI_NO", "");
                        mergeOrder.setItem(row, "TYPE", "1");// 并用药品
                        mergeOrder.setItem(row, "OPT_USER", Operator.getID());
                        mergeOrder.setItem(row, "OPT_DATE", mergeOrder.getDBTime());
                        mergeOrder.setItem(row, "OPT_TERM", Operator.getIP());
                        mergeOrder.setActive(row, false);
                        mergeTable.setDSValue();
                        mergeTable.getTable().grabFocus();
                        mergeTable.setSelectedRow(mergeTable.getRowCount() - 1);
                        mergeTable.setSelectedColumn(2);
                    }
                }
            }
        } else {
            int row = mergeTable.getSelectedRow();
            if (!StringUtil.isNullString(mergeTable.getItemString(row, "ORDER_CODE"))) {
                return;
            }
            mergeTable.acceptText();
            mergeOrder.setItem(row, "ACI_NO", "");
            mergeOrder.setItem(row, "TYPE", "1");// 并用药品
            mergeOrder.setItem(row, "SEQ", mergeOrder.getItemInt(row, "#ID#"));
            mergeOrder.setItem(row, "HYGIENE_TRADE_CODE", parm.getData("HYGIENE_TRADE_CODE"));
            mergeOrder.setItem(row, "ORDER_CODE", parm.getData("ORDER_CODE"));
            mergeOrder.setItem(row, "ORDER_DESC", parm.getData("ORDER_DESC"));
            mergeOrder.setItem(row, "GOODS_DESC", parm.getData("GOODS_DESC"));
            TParm phaParm = mergeOrder.getPhaOtherData(row);
            if (phaParm.getErrCode() < 0) {
                err("ERR:" + phaParm.getErrCode() + phaParm.getErrText() + phaParm.getErrName());
                this.messageBox("查询药品信息失败");
                return;
            }
            mergeOrder.setItem(row, "DOSE_CODE", phaParm.getData("DOSE_CODE", 0));
            mergeOrder.setItem(row, "MAN_DESC", phaParm.getData("MAN_CHN_DESC", 0));
            mergeOrder.setItem(row, "FREQ_CODE", phaParm.getData("FREQ_CODE", 0));
            mergeOrder.setItem(row, "ROUTE_CODE", phaParm.getData("ROUTE_CODE", 0));
            mergeOrder.setItem(row, "UNIT_CODE", phaParm.getData("UNIT_CODE", 0));
            mergeOrder.setItem(row, "MEDI_QTY", phaParm.getDouble("MEDI_QTY", 0));
            mergeOrder.setItem(row, "MEDI_UNIT", phaParm.getData("MEDI_UNIT", 0));
//            TTextFormat BATCH_NO = (TTextFormat) mergeTable.getItem("BATCH_NO");
//            String sql =
//                    " SELECT BATCH_NO,VALID_DATE FROM IND_VERIFYIND WHERE ORDER_CODE = '#' ORDER BY SEQ_NO ";
//            sql = sql.replaceFirst("#", parm.getValue("ORDER_CODE"));
//            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//            BATCH_NO.setPopupMenuSQL(sql);
//            BATCH_NO.onQuery();
//            mergeOrder.setItem(row, "BATCH_NO", result.getData("BATCH_NO", 0));
            mergeOrder.setItem(row, "BATCH_NO", "");
            if(!patOrderRow.getValue("ORDER_CODE").equals("")){
                mergeOrder.setItem(row, "MEDI_START_DATE", patOrderRow.getData("START_DTTM"));
                mergeOrder.setItem(row, "MEDI_END_DATE", patOrderRow.getData("DC_DATE") == null
                        ? patOrderRow.getData("START_DTTM") : patOrderRow.getData("DC_DATE"));
            }else{
              mergeOrder.setItem(row, "MEDI_START_DATE", "");
              mergeOrder.setItem(row, "MEDI_END_DATE", "");   
            }
            mergeOrder.setItem(row, "MEDI_REMARK", "");
            mergeOrder.setItem(row, "OPT_USER", Operator.getID());
            mergeOrder.setItem(row, "OPT_DATE", mergeOrder.getDBTime());
            mergeOrder.setItem(row, "OPT_TERM", Operator.getIP());
            mergeOrder.setActive(row, true);
            row = mergeOrder.insertRow();
            mergeOrder.setItem(row, "ACI_NO", "");
            mergeOrder.setItem(row, "TYPE", "1");// 并用药品
            mergeOrder.setItem(row, "OPT_USER", Operator.getID());
            mergeOrder.setItem(row, "OPT_DATE", mergeOrder.getDBTime());
            mergeOrder.setItem(row, "OPT_TERM", Operator.getIP());
            mergeOrder.setActive(row, false);
            mergeTable.setDSValue();
            mergeTable.getTable().grabFocus();
            mergeTable.setSelectedRow(mergeTable.getRowCount() - 1);
            mergeTable.setSelectedColumn(2);
        }
    }
    
    /**
     * 不良反应名称下拉框返回值
     * 
     * @param tag
     * @param obj
     */
    public void popADRReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (tag.equals("ADR_DESC1")) {
            if (parm.getValue("ADR_DESC").equals(this.getValue("ADR_DESC2"))
                    || parm.getValue("ADR_DESC").equals(this.getValue("ADR_DESC3"))
                    || parm.getValue("ADR_DESC").equals(this.getValue("ADR_DESC4"))) {
                this.messageBox("不允许重复");
                this.setValue(tag, "");
                return;
            }
        } else if (tag.equals("ADR_DESC2")) {
            if (parm.getValue("ADR_DESC").equals(this.getValue("ADR_DESC1"))
                    || parm.getValue("ADR_DESC").equals(this.getValue("ADR_DESC3"))
                    || parm.getValue("ADR_DESC").equals(this.getValue("ADR_DESC4"))) {
                this.messageBox("不允许重复");
                this.setValue(tag, "");
                return;
            }
        } else if (tag.equals("ADR_DESC3")) {
            if (parm.getValue("ADR_DESC").equals(this.getValue("ADR_DESC2"))
                    || parm.getValue("ADR_DESC").equals(this.getValue("ADR_DESC1"))
                    || parm.getValue("ADR_DESC").equals(this.getValue("ADR_DESC4"))) {
                this.messageBox("不允许重复");
                this.setValue(tag, "");
                return;
            }
        } else if (tag.equals("ADR_DESC4")) {
            if (parm.getValue("ADR_DESC").equals(this.getValue("ADR_DESC2"))
                    || parm.getValue("ADR_DESC").equals(this.getValue("ADR_DESC3"))
                    || parm.getValue("ADR_DESC").equals(this.getValue("ADR_DESC1"))) {
                this.messageBox("不允许重复");
                this.setValue(tag, "");
                return;
            }
        }
        this.setValue(tag, parm.getValue("ADR_DESC"));
        this.setValue("ADR_ID" + tag.charAt(tag.length() - 1), parm.getValue("ADR_ID"));
        if (StringUtil.isNullString(tag)) {
            return;
        } else if (tag.equals("ADR_DESC1")) {
            if (!this.getValue(tag).equals("")) {
                this.callFunction("UI|ADR_DESC2|setEnabled", true);
            }
        } else if (tag.equals("ADR_DESC2")) {
            if (!this.getValue(tag).equals("")) {
                this.callFunction("UI|ADR_DESC3|setEnabled", true);
            }
        } else if (tag.equals("ADR_DESC3")) {
            if (!this.getValue(tag).equals("")) {
                this.callFunction("UI|ADR_DESC4|setEnabled", true);
            }
        }
    }

    /**
     * 从表格删除药品
     */
    public void onDeleteOrder() {
        if (((TPanel) this.getComponent("DOUBT_PANEL")).isShowing()) {
            if (doubtTable.getSelectedRow() < 0) {
                return;
            }
            String filter = doubtOrder.getFilter();
            int row = -1;
            row = doubtTable.getSelectedRow();
            doubtOrder.deleteRow(row);// 删除行
            if (doubtOrder.rowCount() < 1) {
                row = doubtOrder.insertRow();
                doubtOrder.setItem(row, "ACI_NO", "");
                doubtOrder.setItem(row, "TYPE", "0");// 怀疑药品
                doubtOrder.setItem(row, "OPT_USER", Operator.getID());
                doubtOrder.setItem(row, "OPT_DATE", doubtOrder.getDBTime());
                doubtOrder.setItem(row, "OPT_TERM", Operator.getIP());
                doubtOrder.setActive(row, false);
            }
            doubtOrder.setFilter(filter);
            doubtOrder.filter();
            doubtTable.setDSValue();
        } else {
            if (mergeTable.getSelectedRow() < 0) {
                return;
            }
            String filter = mergeOrder.getFilter();
            int row = -1;
            row = mergeTable.getSelectedRow();
            mergeOrder.deleteRow(row);// 删除行
            if (mergeOrder.rowCount() < 1) {
                row = mergeOrder.insertRow();
                mergeOrder.setItem(row, "ACI_NO", "");
                mergeOrder.setItem(row, "TYPE", "1");// 并用药品
                mergeOrder.setItem(row, "OPT_USER", Operator.getID());
                mergeOrder.setItem(row, "OPT_DATE", doubtOrder.getDBTime());
                mergeOrder.setItem(row, "OPT_TERM", Operator.getIP());
                mergeOrder.setActive(row, false);
            }
            mergeOrder.setFilter(filter);
            mergeOrder.filter();
            mergeTable.setDSValue();
        }
    }
    
    /**
     * 将上一个SQL查出的字段信息生成 IN()语句，为下一个SQL服务
     * @param sqlColumn
     * @param Nos
     * @return
     */
    public static String getInStatement(TParm Nos, String columnName, String sqlColumn) {//add by wanglong 20131105
        if (Nos.getCount(columnName) < 1) {
            return " 1=1 ";
        }
        StringBuffer inStr = new StringBuffer();
        inStr.append(sqlColumn + " IN ('");
        for (int i = 0; i < Nos.getCount(columnName) ; i++) {
            inStr.append(Nos.getValue(columnName, i));
            if ((i + 1) != Nos.getCount(columnName)) {
                if ((i + 1) % 999 != 0) {
                    inStr.append("','");
                } else if (((i + 1) % 999 == 0)) {
                    inStr.append("') OR " + sqlColumn + " IN ('");
                }
            }
        }
        inStr.append("')");
        return inStr.toString();
    }
    
    
    /**
     * 计算字符串长度（中英数字混合），中文算两个字符
     * 
     * @param str
     * @return
     */
    public int countStrLength(String str) {//wanglong add 20140730
        try {
            str = new String(str.getBytes("GBK"), "ISO8859_1");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str.length();
    }
}
