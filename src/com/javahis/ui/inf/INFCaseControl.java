package com.javahis.ui.inf;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import java.awt.Component;
import java.sql.Timestamp;
import com.dongyang.ui.TTextFormat;
import jdo.sys.Operator;
import com.dongyang.ui.TTabbedPane;
import jdo.inf.INFCaseTool;
import jdo.sys.PatTool;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.system.textFormat.TextFormatSYSStation;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTableNode;
import jdo.adm.ADMTool;

/**
 * <p>
 * Title: 感染控制感染病例筛选界面控制
 * </p>
 * 
 * <p>
 * Description: 感染控制感染病例筛选界面控制
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author sundx
 * @version 1.0
 */
public class INFCaseControl extends TControl {

    // 入院诊断1
    TParm inDiag1 = new TParm();
    // 入院诊断2
    TParm inDiag2 = new TParm();
    // 出院诊断1
    TParm outDiag1 = new TParm();
    // 出院诊断2
    TParm outDiag2 = new TParm();
    // 出院诊断3
    TParm outDiag3 = new TParm();
    // 感控诊断2
//  TParm infDiag1 = new TParm();//delete by wanglong 20140217
    // 感控诊断3
//  TParm infDiag2 = new TParm();
    // 记录页签编号
    int taddedPaneNo;

    /**
     * 初始化方法
     */
    public void onInit() {
        onInitPanelOne();
        onInitPanelThree();
        onInitPanelFour();
        onInitPanelFive();// add by wanglong 20131105
    
        //add by wanglong 20140217
        getTable("TABLE_5_2").addEventListener("TABLE_5_2->" + TTableEvent.CHANGE_VALUE, this,
                                               "onTableChangeValue");
        getTable("TABLE_5_2").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                               "onCheckBoxClicked");
        getTable("TABLE_5_2").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                               "onCreateEditComponent");
        getTable("TABLE_6_2").addEventListener("TABLE_5_2->" + TTableEvent.CHANGE_VALUE, this,
                                               "onTableChangeValue");
        getTable("TABLE_6_2").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                               "onCheckBoxClicked");
        //add end

        // 设置弹出菜单
        callFunction("UI|DIAG_CODE_1|setPopupMenuParameter", "ICD",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");// add by wanglong 20131105
        // 定义接受返回值方法
        callFunction("UI|DIAG_CODE_1|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "diagCode1");
        // 设置弹出菜单
        callFunction("UI|ORDER_CODE_1|setPopupMenuParameter", "ORDER",
                     "%ROOT%\\config\\sys\\SYSFeePopup.x");// add by wanglong 20131105
        // 定义接受返回值方法
        callFunction("UI|ORDER_CODE_1|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "orderCode1");
                
        // 设置弹出菜单
        getTextField("IN_DIAG1_2")
                .setPopupMenuParameter(
                        "UD",
                        getConfigParm().newConfig(
                                "%ROOT%\\config\\sys\\SYSICDPopup.x"));
        // 定义接受返回值方法
        getTextField("IN_DIAG1_2").addEventListener(
                TPopupMenuEvent.RETURN_VALUE, this, "inDiag1And2");
        // 设置弹出菜单
        getTextField("IN_DIAG2_2")
                .setPopupMenuParameter(
                        "UD",
                        getConfigParm().newConfig(
                                "%ROOT%\\config\\sys\\SYSICDPopup.x"));
        // 定义接受返回值方法
        getTextField("IN_DIAG2_2").addEventListener(
                TPopupMenuEvent.RETURN_VALUE, this, "inDiag2And2");
        // 设置弹出菜单
        getTextField("OUT_DIAG3_2")
                .setPopupMenuParameter(
                        "UD",
                        getConfigParm().newConfig(
                                "%ROOT%\\config\\sys\\SYSICDPopup.x"));
        // 定义接受返回值方法
        getTextField("OUT_DIAG3_2").addEventListener(
                TPopupMenuEvent.RETURN_VALUE, this, "outDiag3And2");
        // 设置弹出菜单
        getTextField("OUT_DIAG2_2")
                .setPopupMenuParameter(
                        "UD",
                        getConfigParm().newConfig(
                                "%ROOT%\\config\\sys\\SYSICDPopup.x"));
        // 定义接受返回值方法
        getTextField("OUT_DIAG2_2").addEventListener(
                TPopupMenuEvent.RETURN_VALUE, this, "outDiag2And2");
        // 设置弹出菜单
        getTextField("OUT_DIAG1_2")
                .setPopupMenuParameter(
                        "UD",
                        getConfigParm().newConfig(
                                "%ROOT%\\config\\sys\\SYSICDPopup.x"));
        // 定义接受返回值方法
        getTextField("OUT_DIAG1_2").addEventListener(
                TPopupMenuEvent.RETURN_VALUE, this, "outDiag1And2");
//      // 设置弹出菜单
//      getTextField("INF_DIAG1_2")//delete by wanglong 20140217
//              .setPopupMenuParameter(
//                      "UD",
//                      getConfigParm().newConfig(
//                              "%ROOT%\\config\\sys\\SYSICDPopup.x"));
//      // 定义接受返回值方法
//      getTextField("INF_DIAG1_2").addEventListener(
//              TPopupMenuEvent.RETURN_VALUE, this, "infDiag1And2");
//      // 设置弹出菜单
//      getTextField("INF_DIAG2_2")
//              .setPopupMenuParameter(
//                      "UD",
//                      getConfigParm().newConfig(
//                              "%ROOT%\\config\\sys\\SYSICDPopup.x"));
//      // 定义接受返回值方法
//      getTextField("INF_DIAG2_2").addEventListener(
//              TPopupMenuEvent.RETURN_VALUE, this, "infDiag2And2");
        // 默认第0个页签按钮管控
        ((TMenuItem) getComponent("save")).setEnabled(false);
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        ((TMenuItem) getComponent("query")).setEnabled(true);
        ((TMenuItem) getComponent("clear")).setEnabled(true);
        ((TMenuItem) getComponent("close")).setEnabled(true);
        ((TMenuItem) getComponent("print")).setEnabled(false);
        ((TMenuItem) getComponent("report")).setEnabled(false);
        ((TMenuItem) getComponent("temperature")).setEnabled(false);
        ((TMenuItem) getComponent("showcase")).setEnabled(false);
        ((TMenuItem) getComponent("export")).setEnabled(false);
        taddedPaneNo = 0;
    }

    /**
     * 新增试验药物
     */
    public void onNewExmResult() {
        // if(getTable("TABLE_4_2").getParmValue() == null ||
        // getTable("TABLE_4_2").getRowCount() == 0){
        // getTable("TABLE_4_2").setParmValue(new TParm());
        // getTable("TABLE_4_2").addRow();
        // return;
        // }
        // int row = getTable("TABLE_4_2").getParmValue().getCount() - 1;
        // if(!getTable("TABLE_4_2").getParmValue().getData("DEL_FLG",row).equals("Y")
        // &&
        // (getTable("TABLE_4_2").getParmValue().getValue("PATHOGEN_CODE",row).length()
        // == 0 ||
        // getTable("TABLE_4_2").getParmValue().getValue("EXM_PHA",row).length()
        // == 0 ||
        // getTable("TABLE_4_2").getParmValue().getValue("RESULT",row).length()
        // == 0))
        // return;
        // getTable("TABLE_4_2").addRow();
        // 病案号
        String mrNo = this.getValueString("MR_NO_2");
        // 就诊号
        String caseNo = this.getValueString("CASE_NO_2");
        if (caseNo.equals("")) {
            this.messageBox("就诊号为空,无住院就诊信息");
            return;
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        parm.setData("MR_NO", mrNo);
        Object obj = this.openDialog("%ROOT%\\config\\inf\\INFMedCaseUI.x",
                parm);
        if (obj != null) {
            TParm temp = (TParm) obj;
            this.getTable("TABLE_4_2").setParmValue(temp);
        }
    }

    /**
     * 取得界面TTextField
     * 
     * @param tag
     *            String
     * @return TTextField
     */
    public TTextField getTextField(String tag) {
        return (TTextField) this.getComponent(tag);
    }
    
    /**
     * 启用/禁用WBC数量输入
     */
//    public void onEnableWBCNum() {// add by wanglong 20131105
//        if (this.getValueString("WBC_FLG_1").equals("Y")) {
//            callFunction("UI|WBC_NUM|setEnabled", true);
//        } else {
//            callFunction("UI|WBC_NUM|setEnabled", false);
//        }
//    }
    
    /**
     * 启用/禁用诊断代码
     */
    public void onEnableDiagCode() {// add by wanglong 20131105
        if (this.getValueString("SUSDIAG_FLG").equals("Y")) {
            callFunction("UI|DIAG_CODE_1|setEnabled", true);
        } else {
            callFunction("UI|DIAG_CODE_1|setEnabled", false);
        }
    }
    
    /**
     * 启用/禁用医嘱编码
     */
    public void onEnableOrderCode() {// add by wanglong 20131105
        if (this.getValueString("INVOPT_FLG").equals("Y")) {
            callFunction("UI|ORDER_CODE_1|setEnabled", true);
        } else {
            callFunction("UI|ORDER_CODE_1|setEnabled", false);
        }
    }

    /**
     * 初始化第一个页签数据
     */
    public void onInitPanelOne() {
        Timestamp timestamp = SystemTool.getInstance().getDate();
        ((TTextFormat) getComponent("START_DATE_1")).setValue(timestamp);
        ((TTextFormat) getComponent("END_DATE_1")).setValue(timestamp);
        ((TTextFormat) getComponent("DEPT_CODE_1"))
                .setValue(Operator.getDept());
        setValue("START_TEMP_1", "");//add by wanglong 20131105
        setValue("END_TEMP_1", "");
        setValue("WBC_NUM", 0);
        TextFormatSYSStation stationCode1 = ((TextFormatSYSStation) getComponent("STATION_CODE_1"));//add by wanglong 20131105
        stationCode1.setDeptCode("");
        stationCode1.onQuery();
        stationCode1.setValue(Operator.getStation());
        stationCode1.setDeptCode(Operator.getDept());
        stationCode1.onQuery();
        
    }

    /**
     * 初始化第三个页签数据
     */
    public void onInitPanelThree() {
        Timestamp timestamp = SystemTool.getInstance().getDate();
        ((TTextFormat) getComponent("START_DATE_3")).setValue(timestamp);
        ((TTextFormat) getComponent("END_DATE_3")).setValue(timestamp);
    }

    /**
     * 初始化第四个页签数据
     */
    public void onInitPanelFour() {
        Timestamp timestamp = SystemTool.getInstance().getDate();
        ((TTextFormat) getComponent("START_DATE_4")).setValue(timestamp);
        ((TTextFormat) getComponent("END_DATE_4")).setValue(timestamp);
    }

    /**
     * 初始化第五个页签数据
     */
    public void onInitPanelFive() {// add by wanglong 20131105
        Timestamp timestamp = SystemTool.getInstance().getDate();
        ((TTextFormat) getComponent("START_DATE_5")).setValue(timestamp);
        ((TTextFormat) getComponent("END_DATE_5")).setValue(timestamp);
    }
    
    /**
     * 查询动作
     */
    public void onQuery() {
        int index = ((TTabbedPane) getComponent("TabbedPane_1"))
                .getSelectedIndex();
        switch (index) {
        case 0:
            onQueryOne();
            break;
        case 1:
            onQueryTwo();
            break;
        case 2:
            onQueryThree();
            break;
        case 3:
            onQueryFour();
            break;
        case 4://add by wanglong 20131105
            onQueryFive();
            break;
        }
    }

    /**
     * 第一个页签查询动作
     */
    public void onQueryOne() {
        if (getValueString("START_DATE_1").length() == 0
                || getValueString("END_DATE_1").length() == 0) {
            messageBox("请录入开始结束日期");
            return;
        }
        if (StringTool.getDateDiffer((Timestamp) getValue("START_DATE_1"),
                (Timestamp) getValue("END_DATE_1")) > 0) {
            messageBox("录入的日期不合法");
            return;
        }
        // ===============modefy chenxi start 2012.05.10
        // if((getValueString("START_TEMP_1").length() != 0 &&
        // getValueString("START_TEMP_1").length() != 2) ||
        // (getValueString("END_TEMP_1").length() != 0 &&
        // getValueString("END_TEMP_1").length() != 2)){
        // messageBox("体温为两位数");
        // return;
        // }
        // ===================chenxi stop 2012.05.10
        String SQLTemperature = "";
        // 查询条件中温度不为空
        getTable("TABLE_1").removeRowAll();
        if (getValueString("START_TEMP_1").length() != 0
                && getValueString("END_TEMP_1").length() != 0
                && (getValueDouble("START_TEMP_1") != 0 || getValueDouble("END_TEMP_1") != 0)) {
            SQLTemperature = " SELECT A.CASE_NO, B.MR_NO FROM SUM_VTSNTPRDTL A, ADM_INP B "
                    + " WHERE A.ADM_TYPE = 'I' " 
                    + " AND A.CASE_NO = B.CASE_NO ? / "
                    + " AND A.EXAMINE_DATE BETWEEN '#' AND '@' "
                    + " AND A.TEMPERATURE BETWEEN '$' AND '&' "
                    + " UNION "
                    + " SELECT A.CASE_NO, B.MR_NO FROM SUM_NEWARRIVALSIGNDTL A, ADM_INP B "
                    + " WHERE A.ADM_TYPE = 'I' "
                    + " AND A.CASE_NO = B.CASE_NO ? / "
                    + " AND A.EXAMINE_DATE BETWEEN '#' AND '@' "
                    + " AND A.TEMPERATURE BETWEEN '$' AND '&' ";
            if (getValueString("DEPT_CODE_1").length() == 0) {// add by wanglong 20131105
                SQLTemperature = SQLTemperature.replaceAll("\\?", "");
            } else {
                SQLTemperature = SQLTemperature.replaceAll("\\?", " AND B.DEPT_CODE = '" + getValueString("DEPT_CODE_1") + "' ");
            }
            if (getValueString("STATION_CODE_1").length() == 0) {// add by wanglong 20131105
                SQLTemperature = SQLTemperature.replaceAll("/", "");
            } else {
                SQLTemperature = SQLTemperature.replaceAll("/", " AND B.STATION_CODE = '" + getValueString("STATION_CODE_1") + "' ");
            }
            SQLTemperature = SQLTemperature.replaceAll("#", getDateString("START_DATE_1"));//modify by wanglong 20131105
            SQLTemperature = SQLTemperature.replaceAll("@", getDateString("END_DATE_1"));
            SQLTemperature = SQLTemperature.replaceAll("\\$", getValueString("START_TEMP_1"));
            SQLTemperature = SQLTemperature.replaceAll("&", getValueString("END_TEMP_1"));
        }
        String SQLDrug = "";
        // 使用抗生素注记选中
        if (getValue("ANTIBIOTRCD_FLG_1").equals("Y")) {
            SQLDrug = " SELECT DISTINCT A.CASE_NO, A.MR_NO "
                    + " FROM ODI_ORDER A,PHA_BASE B "
                    + " WHERE A.EFF_DATE BETWEEN TO_DATE('#000000','YYYYMMDDHH24MISS') "
                    + "                      AND TO_DATE('#235959','YYYYMMDDHH24MISS') "
                    + "  @  &  AND B.ORDER_CODE = A.ORDER_CODE  # ";
            SQLDrug = SQLDrug.replaceFirst("#", getDateString("START_DATE_1"));// modify by wanglong 20131105
            SQLDrug = SQLDrug.replaceFirst("#", getDateString("END_DATE_1"));
            if (getValueString("DEPT_CODE_1").length() == 0) {// add by wanglong 20131105
                SQLDrug = SQLDrug.replaceFirst("@", "");
            } else {
                SQLDrug = SQLDrug.replaceFirst("@", " AND A.DEPT_CODE = '" + getValueString("DEPT_CODE_1") + "' ");
            }
            if (getValueString("STATION_CODE_1").length() == 0) {// add by wanglong 20131105
                SQLDrug = SQLDrug.replaceFirst("&", "");
            } else {
                SQLDrug = SQLDrug.replaceFirst("&", " AND A.STATION_CODE = '" + getValueString("STATION_CODE_1") + "' ");
            }
            if (getValueString("ANTIBIOTIC_CODE_1").length() == 0) {
                SQLDrug = SQLDrug.replaceFirst("#", " AND B.ANTIBIOTIC_CODE IS NOT NULL");
            } else {
                SQLDrug = SQLDrug.replaceFirst("#", " AND B.ANTIBIOTIC_CODE = '" + getValueString("ANTIBIOTIC_CODE_1") + "'");
            }
        }
        String SQLWBC = "";
        // WBC注记选中
        if (getValueString("WBC_FLG_1").equals("Y")){
            SQLWBC = " SELECT DISTINCT A.CASE_NO, A.MR_NO "
                   + " FROM MED_APPLY A, MED_LIS_RPT B,INF_SYSPARM C "
                   + " WHERE A.STATUS = '1'  @   &   " 
                   + " AND   A.CAT1_TYPE = 'LIS'"
                   + " AND   A.START_DTTM BETWEEN TO_DATE('#000000','YYYYMMDDHH24MISS') "
                   + "                        AND TO_DATE('#235959','YYYYMMDDHH24MISS') "
                   + " AND   A.APPLICATION_NO = B.APPLICATION_NO "
                   + " AND   B.TESTITEM_CODE = C.WBC "
                   + " AND   B.TEST_VALUE > '10' ";// modify by wanglong 20131202
            if (getValueString("DEPT_CODE_1").length() == 0) {// add by wanglong 20131105
                SQLWBC = SQLWBC.replaceFirst("@", "");
            } else {
                SQLWBC = SQLWBC.replaceFirst("@", " AND A.DEPT_CODE = '" + getValueString("DEPT_CODE_1") + "' ");
            }
            if (getValueString("STATION_CODE_1").length() == 0) {// add by wanglong 20131105
                SQLWBC = SQLWBC.replaceFirst("&", "");
            } else {
                SQLWBC = SQLWBC.replaceFirst("&", " AND A.STATION_CODE = '" + getValueString("STATION_CODE_1") + "' ");
            }
            SQLWBC = SQLWBC.replaceFirst("#", getDateString("START_DATE_1"));
            SQLWBC = SQLWBC.replaceFirst("#", getDateString("END_DATE_1"));
//          SQLWBC = SQLWBC.replaceFirst("#", this.getValueString("WBC_NUM"));// add by wanglong 20131105 
        }
    
        // ========================add by wanglong 20131105
        String SQLSUS = "";
        // 拟诊诊断注记选中
        if (getValueString("SUSDIAG_FLG").equals("Y")) {
            SQLSUS = " SELECT DISTINCT A.CASE_NO, A.MR_NO "
                   + "  FROM ADM_INPDIAG A,ADM_INP B "
                   + " WHERE A.CASE_NO = B.CASE_NO "
                   + "   AND A.MAINDIAG_FLG = 'Y' & / "
                   + "   AND A.IO_TYPE = 'Z'  @  "
                   + "   AND A.OPT_DATE BETWEEN TO_DATE( '#000000', 'YYYYMMDDHH24MISS') "
                   + "                      AND TO_DATE( '#235959', 'YYYYMMDDHH24MISS') ";
            if (getValueString("DEPT_CODE_1").length() == 0) {// add by wanglong 20131105
                SQLSUS = SQLSUS.replaceFirst("&", "");
            } else {
                SQLSUS = SQLSUS.replaceFirst("&", " AND B.DEPT_CODE = '" + getValueString("DEPT_CODE_1") + "' ");
            }
            if (getValueString("STATION_CODE_1").length() == 0) {// add by wanglong 20131105
                SQLSUS = SQLSUS.replaceFirst("/", "");
            } else {
                SQLSUS = SQLSUS.replaceFirst("/", " AND B.STATION_CODE = '" + getValueString("STATION_CODE_1") + "' ");
            }
            SQLSUS = SQLSUS.replaceFirst("#", getDateString("START_DATE_1"));
            SQLSUS = SQLSUS.replaceFirst("#", getDateString("END_DATE_1"));
            if (!getValueString("DIAG_CODE_1").equals("")) {
                SQLSUS = SQLSUS.replaceFirst("@", " AND ICD_CODE = '" + getValueString("DIAG_CODE_1") + "'");
            } else {
                SQLSUS = SQLSUS.replaceFirst("@", "");
            }
        }
        String SQLINV = "";
        // 介入操作注记选中
        if (getValueString("INVOPT_FLG").equals("Y")) {
            SQLINV = " SELECT DISTINCT A.CASE_NO, A.MR_NO "
                   + "  FROM ODI_ORDER A, SYS_FEE B "
                   + " WHERE A.EFF_DATE BETWEEN TO_DATE('#000000', 'YYYYMMDDHH24MISS') "
                   + "                      AND TO_DATE('#235959', 'YYYYMMDDHH24MISS') "
                   + "   AND B.IN_OPFLG = 'Y'  @   &  /  "
                   + "   AND B.ORDER_CODE = A.ORDER_CODE ";
            SQLINV = SQLINV.replaceFirst("#", getDateString("START_DATE_1"));
            SQLINV = SQLINV.replaceFirst("#", getDateString("END_DATE_1"));
            if (!getValueString("ORDER_CODE_1").equals("")) {
                SQLINV = SQLINV.replaceFirst("@", " AND A.ORDER_CODE = '" + getValueString("ORDER_CODE_1") + "'");
            } else {
                SQLINV = SQLINV.replaceFirst("@", "");
            }
            if (getValueString("DEPT_CODE_1").length() == 0) {// add by wanglong 20131105
                SQLINV = SQLINV.replaceFirst("&", "");
            } else {
                SQLINV = SQLINV.replaceFirst("&", " AND A.DEPT_CODE = '" + getValueString("DEPT_CODE_1") + "' ");
            }
            if (getValueString("STATION_CODE_1").length() == 0) {// add by wanglong 20131105
                SQLINV = SQLINV.replaceFirst("/", "");
            } else {
                SQLINV = SQLINV.replaceFirst("/", " AND A.STATION_CODE = '" + getValueString("STATION_CODE_1") + "' ");
            }
        }
        //========================add end
        String SQLUNION = "";
        if (SQLTemperature.length() != 0)
            SQLUNION = SQLTemperature;
        if (SQLDrug.length() != 0)
            if (SQLUNION.length() == 0)
                SQLUNION = SQLDrug;
            else
                SQLUNION += " UNION " + SQLDrug;
        if (SQLWBC.length() != 0)
            if (SQLUNION.length() == 0)
                SQLUNION = SQLWBC;
            else
                SQLUNION += " UNION " + SQLWBC;
        //===========add by wanglong 20131105
        if (SQLSUS.length() != 0)
            if (SQLUNION.length() == 0)
                SQLUNION = SQLSUS;
            else
                SQLUNION += " UNION " + SQLSUS;
        if (SQLINV.length() != 0)
            if (SQLUNION.length() == 0)
                SQLUNION = SQLINV;
            else
                SQLUNION += " UNION " + SQLINV;
        //===========add end
        // 体温,使用抗生素注记,WBC注记，拟诊诊断，介入操作不可都为空
        if (SQLUNION.length() == 0) {
//            messageBox("请至少录入一个查询条件");
//            return;
        	//不录入条件也可查询 addBy Zhangz
        	String SQLNOTerm="";
        	
                SQLNOTerm = " SELECT A.CASE_NO, B.MR_NO FROM SUM_VTSNTPRDTL A, ADM_INP B "
                        + " WHERE A.ADM_TYPE = 'I' " 
                        + " AND A.CASE_NO = B.CASE_NO ? / "
                        + " AND A.EXAMINE_DATE BETWEEN '#' AND '@' "
                        + " AND A.TEMPERATURE BETWEEN '0' AND '0' "
                        + " UNION "
                        + " SELECT A.CASE_NO, B.MR_NO FROM SUM_NEWARRIVALSIGNDTL A, ADM_INP B "
                        + " WHERE A.ADM_TYPE = 'I' "
                        + " AND A.CASE_NO = B.CASE_NO ? / "
                        + " AND A.EXAMINE_DATE BETWEEN '#' AND '@' "
                        + " AND A.TEMPERATURE BETWEEN '0' AND '0' ";
                if (getValueString("DEPT_CODE_1").length() == 0) {
                	SQLNOTerm = SQLNOTerm.replaceAll("\\?", "");
                } else {
                	SQLNOTerm = SQLNOTerm.replaceAll("\\?", " AND B.DEPT_CODE = '" + getValueString("DEPT_CODE_1") + "' ");
                }
                if (getValueString("STATION_CODE_1").length() == 0) {
                	SQLNOTerm = SQLNOTerm.replaceAll("/", "");
                } else {
                	SQLNOTerm = SQLNOTerm.replaceAll("/", " AND B.STATION_CODE = '" + getValueString("STATION_CODE_1") + "' ");
                }
                SQLNOTerm = SQLNOTerm.replaceAll("#", getDateString("START_DATE_1"));
                SQLNOTerm = SQLNOTerm.replaceAll("@", getDateString("END_DATE_1"));
//                SQLNOTerm = SQLNOTerm.replaceAll("\\$", getValueString("START_TEMP_1"));
//                SQLNOTerm = SQLNOTerm.replaceAll("&", getValueString("END_TEMP_1"));
                SQLUNION=SQLNOTerm;
          
        	
        }
        // ==========pangben modify 20110624 start 添加区域条件
        StringBuffer region = new StringBuffer();
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            region.append(" AND A.REGION_CODE='" + Operator.getRegion() + "' ");
        } else
            region.append("");
        // ==========pangben modify 20110624 stop
        TParm result = new TParm(getDBTool().select(SQLUNION));// add by wanglong 20131105
        if (result.getErrCode() < 0) {// add by wanglong 20131105
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount() < 1) {// add by wanglong 20131105
            getTable("TABLE_1").setParmValue(new TParm());
            return;
        }
        String SQL = " SELECT DISTINCT A.DEPT_CODE,A.IN_DATE,A.MR_NO,C.PAT_NAME,B.STATION_CODE,A.BED_NO,A.IN_DATE, "
                + "           A.VS_DR_CODE VS_DR,A.CASE_NO,C.BIRTH_DATE,C.SEX_CODE,B.BED_NO_DESC,C.IPD_NO "
                + "      FROM ADM_INP A,SYS_BED B,SYS_PATINFO C "
                + "     WHERE (#) AND (#)"
                + "       AND A.BED_NO = B.BED_NO "
                + "       @  @                  "
                + "       AND A.MR_NO = C.MR_NO "
                + region
                + "  ORDER BY B.STATION_CODE ";
        String caseNoWhere = getInStatement(result, "CASE_NO", "A.CASE_NO");// add by wanglong 20131105
        String mrNoWhere = getInStatement(result, "MR_NO", "C.MR_NO");
        SQL = SQL.replaceFirst("#", caseNoWhere);
        SQL = SQL.replaceFirst("#", mrNoWhere);
        if (getValueString("DEPT_CODE_1").length() == 0) {// modify by wanglong 20131105
            SQL = SQL.replaceFirst("@", "");
        } else {
            SQL = SQL.replaceFirst("@", " AND A.DEPT_CODE = '" + getValueString("DEPT_CODE_1") + "'");
        }
        if (getValueString("STATION_CODE_1").length() == 0) {
            SQL = SQL.replaceFirst("@", "");
        } else {
            SQL = SQL.replaceFirst("@", " AND A.STATION_CODE = '" + getValueString("STATION_CODE_1") + "'");
        }
       System.out.println("SQL语句是：：：：：：：：：："+SQL);
        TParm parm = new TParm(getDBTool().select(SQL));
        if (parm.getCount() <= 0)
            return;
        getTable("TABLE_1").setParmValue(parm);
    }

    /**
     * 第二个页签查询动作
     */
    public void onQueryTwo() {
        // 病患基本信息
        if (getValueString("MR_NO_2").trim().length() <= 0) {
            this.messageBox("请输入病案号");
            return;
        }
        TParm parmPat = new TParm();
        parmPat.setData("MR_NO",
                PatTool.getInstance().checkMrno(getValueString("MR_NO_2")));
        // ==============pangben modify 20110624 start
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            parmPat.setData("REGION_CODE", Operator.getRegion());
        }
        // ==============pangben modify 20110624 stop
        setValue("MR_NO_2",
                PatTool.getInstance().checkMrno(getValueString("MR_NO_2")));
        ((TTextField) getComponent("MR_NO_2")).setEnabled(false);
        parmPat = INFCaseTool.getInstance().caseRegisterPatInfo(parmPat);
        if (parmPat.getCount() <= 0)
            return;
        ((TButton) getComponent("ADD_BUTTON_2")).setEnabled(true);
        setValue("INF_DR_2", parmPat.getData("VS_DR_CODE", 0));
        setValue("REGISTER_DATE_2", SystemTool.getInstance().getDate());
        setValue("CASE_NO_2", parmPat.getData("CASE_NO", 0));
        setValue("PAT_NAME_2", parmPat.getData("PAT_NAME", 0));
        setValue("AGE_2",
                StringTool.CountAgeByTimestamp(
                        (Timestamp) parmPat.getData("BIRTH_DATE", 0),
                        SystemTool.getInstance().getDate())[0]);
        setValue("IN_DATE_2", parmPat.getData("IN_DATE", 0));
        setValue("SEX_2", parmPat.getData("SEX_CODE", 0));
        setValue("OUT_DATE_2", parmPat.getData("DS_DATE", 0));
        setValue("IPD_NO_2", parmPat.getData("IPD_NO", 0));
        setValue("CHARGE_FEE_2", parmPat.getData("TOTAL_AMT", 0));
        setValue("DEPT_CODE_2", parmPat.getData("DEPT_CODE", 0));
        // 住院天数可更改
        setValue("ADM_DAYS_2",
                ADMTool.getInstance()
                        .getAdmDays(parmPat.getValue("CASE_NO", 0)));
        // 病患诊断信息
        TParm parmDiag = new TParm();
        parmDiag.setData("CASE_NO", parmPat.getData("CASE_NO", 0));
        parmDiag = INFCaseTool.getInstance().caseRegisterDiag(parmDiag);
        setAdmDiagInf(parmDiag);
        // 病患感控记录信息
        TParm parmCase = new TParm();
        parmCase.setData("CASE_NO", parmPat.getData("CASE_NO", 0));
        parmCase = INFCaseTool.getInstance().caseRegisterCase(parmCase);
        int seqNum = parmCase.getCount() - 1;
        String infNo = parmCase.getCount() <= 0 ? "" : parmCase.getValue(
                "INF_NO", seqNum);
        setTable2And2ByCaseInfNo(parmPat.getValue("CASE_NO", 0), infNo);
        setTable3And2ByCaseInfNo(parmPat.getValue("CASE_NO", 0), infNo);
        setTable4And2ByCaseInfNo(parmPat.getValue("CASE_NO", 0), infNo);
        setTable5And2ByCaseInfNo(parmPat.getValue("CASE_NO", 0), infNo);//add by wanglong 20140217
        setTable6And2ByCaseInfNo(parmPat.getValue("CASE_NO", 0), infNo);//add by wanglong 20140217
        if (parmCase.getCount() <= 0)
            return;
        clearTParmNull(parmCase);
        setValue("INF_NO_2", parmCase.getData("INF_NO", seqNum));
        setValue("REGISTER_DATE_2", parmCase.getData("REGISTER_DATE", seqNum));
        setValue("INF_DR_2", parmCase.getData("INF_DR", seqNum));
        // 住院天数可更改
        setValue("ADM_DAYS_2", parmCase.getData("ADM_DAYS", seqNum));
        // 诊断信息可以修改
        setValue("IN_DIAG1_2", parmCase.getData("IN_DIAG1_DESC", seqNum));
        setValue("IN_DIAG2_2", parmCase.getData("IN_DIAG2_DESC", seqNum));
        setValue("OUT_DIAG1_2", parmCase.getData("OUT_DIAG1_DESC", seqNum));
        setValue("OUT_DIAG2_2", parmCase.getData("OUT_DIAG2_DESC", seqNum));
        setValue("OUT_DIAG3_2", parmCase.getData("OUT_DIAG3_DESC", seqNum));
        setValue("INF_DATE_2", parmCase.getData("INF_DATE", seqNum));
//      setValue("INFPOSITION_CODE_2", parmCase.getData("INFPOSITION_CODE", seqNum));//delete by wanglong 20140217
//      setValue("INFPOSITION_DTL_2", parmCase.getData("INFPOSITION_DTL", seqNum));
//      setValue("INF_DIAG1_2", parmCase.getData("INF_DIAG1_DESC", seqNum));
//      setValue("INF_DIAG2_2", parmCase.getData("INF_DIAG2_DESC", seqNum));
        setInfDiagInf(parmCase, seqNum);
        setValue("DIEINFLU_CODE_2", parmCase.getData("DIEINFLU_CODE", seqNum));
        setValue("INFRETN_CODE_2", parmCase.getData("INFRETN_CODE", seqNum));
        setValue("OP_CODE_2", parmCase.getData("OP_CODE", seqNum));
        setValue("OP_DATE_2", parmCase.getData("OP_DATE", seqNum));
        setValue("OPCUT_TYPE_2", parmCase.getData("OPCUT_TYPE", seqNum));
        setValue("ANA_TYPE_2", parmCase.getData("ANA_TYPE", seqNum));
        setValue("ANA_TYPE_2", parmCase.getData("ANA_TYPE", seqNum));

        if (parmCase.getValue("URGTOP_FLG", seqNum).equals("Y"))
            setValue("URGTOP_FLG_A_2", "Y");
        else if (parmCase.getValue("URGTOP_FLG", seqNum).equals("N"))
            setValue("URGTOP_FLG_B_2", "Y");

        if (parmCase.getValue("OP_TIME", seqNum).length() >= 4) {
            setValue("OP_TIME_HH_2", parmCase.getValue("OP_TIME", seqNum)
                    .substring(0, 2));
            setValue("OP_TIME_MM_2", parmCase.getValue("OP_TIME", seqNum)
                    .substring(2, 4));
        }

        setValue("OP_DR_2", parmCase.getData("OP_DR", seqNum));
        setValue("INICU_DATE_2", parmCase.getData("INICU_DATE", seqNum));
        setValue("OUTICU_DATE_2", parmCase.getData("OUTICU_DATE", seqNum));

        if (parmCase.getValue("ETIOLGEXM_FLG", seqNum).equals("Y")) {
            setValue("ETIOLGEXM_FLG_Y_2", "Y");
            onEtiolgexmFlgYTwo();
        } else if (parmCase.getValue("ETIOLGEXM_FLG", seqNum).equals("N")) {
            setValue("ETIOLGEXM_FLG_N_2", "Y");
            onEtiolgexmFlgNTwo();
        }
        setValue("SPECIMEN_CODE_2", parmCase.getData("SPECIMEN_CODE", seqNum));
        setValue("EXAM_DATE_2", parmCase.getData("EXAM_DATE", seqNum));

        if (parmCase.getValue("LABWAY", seqNum).equals("1"))
            setValue("LABWAY_1_2", "Y");
        if (parmCase.getValue("LABWAY", seqNum).equals("2"))
            setValue("LABWAY_2_2", "Y");
        if (parmCase.getValue("LABWAY", seqNum).equals("3"))
            setValue("LABWAY_3_2", "Y");

        if (parmCase.getValue("LABPOSITIVE", seqNum).equals("Y"))
            setValue("LABPOSITIVE_A_2", "Y");
        if (parmCase.getValue("LABPOSITIVE", seqNum).equals("N"))
            setValue("LABPOSITIVE_B_2", "Y");
        setValue("PATHOGEN1_CODE_2", parmCase.getData("PATHOGEN1_CODE", seqNum));
        setValue("PATHOGEN2_CODE_2", parmCase.getData("PATHOGEN2_CODE", seqNum));
        setValue("PATHOGEN3_CODE_2", parmCase.getData("PATHOGEN3_CODE", seqNum));

        if (parmCase.getValue("ANTIBIOTEST_FLG", seqNum).equals("Y"))
            setValue("ANTIBIOTEST_FLG_Y_2", "Y");
        if (parmCase.getValue("ANTIBIOTEST_FLG", seqNum).equals("N"))
            setValue("ANTIBIOTEST_FLG_N_2", "Y");

        setTable1And2(parmCase);
        getTable("TABLE_1_2").setSelectedRow(seqNum);
        getAntiTestInfo(parmPat);
    }

    /**
     * 取得试验结果信息
     * 
     * @param parmPat
     *            TParm
     */
    private void getAntiTestInfo(TParm parmPat) {
        getTable("TABLE_4_2").removeRowAll();
        TParm parmTable12 = getTable("TABLE_1_2").getParmValue();
        String SQL = " SELECT CULURE_CODE AS CULTURE_CODE,ANTI_CODE,SENS_LEVEL"
                + " FROM INF_ANTIBIOTEST "
                + " WHERE  CASE_NO = '"
                + parmPat.getData("CASE_NO", 0)
                + "'"
                + " AND    INF_NO = '"
                + getValue("INF_NO_2")
                + "'"
                + " AND    INFCASE_SEQ = '"
                + parmTable12.getValue("INFCASE_SEQ", getTable("TABLE_1_2")
                        .getSelectedRow()) + "'";
        TParm parm = new TParm(getDBTool().select(SQL));
        if (parm.getCount("CULTURE_CODE") <= 0)
            return;
        getTable("TABLE_4_2").setParmValue(parm);
    }

    /**
     * 第三个页签查询动作
     */
    public void onQueryThree() {
        if (getValueString("START_DATE_3").length() == 0
                || getValueString("END_DATE_3").length() == 0) {
            messageBox("请输入开始时间及结束时间");
            return;
        }
        if (StringTool.getDateDiffer((Timestamp) getValue("START_DATE_3"),
                (Timestamp) getValue("END_DATE_3")) > 0) {
            messageBox("录入的日期不合法");
            return;
        }
        TParm parm = new TParm();
        if (getValueString("DEPT_3").length() != 0)
            parm.setData("DEPT_CODE", getValue("DEPT_3"));
        if (getValueString("STATION_3").length() != 0)
            parm.setData("STATION_CODE", getValue("STATION_3"));
        if (getValueString("DR_3").length() != 0)
            parm.setData("VS_DR_CODE", getValue("DR_3"));
        if (getValueString("IPD_NO_3").length() != 0)
            parm.setData("IPD_NO",
                    PatTool.getInstance().checkMrno(getValueString("IPD_NO_3")));
        if (getValueString("MR_NO_3").length() != 0) {
            parm.setData("MR_NO",
                    PatTool.getInstance().checkMrno(getValueString("MR_NO_3")));
            setValue("MR_NO_3",
                    PatTool.getInstance().checkMrno(getValueString("MR_NO_3")));
        }
        parm.setData("START_DATE", getValue("START_DATE_3"));
        String endDate = StringTool.getString(
                (Timestamp) getValue("END_DATE_3"), "yyyyMMddHHmmss")
                .substring(0, 8)
                + "235959";
        parm.setData("END_DATE",
                StringTool.getTimestamp(endDate, "yyyyMMddHHmmss"));
        if (getValueString("ANTIBIOTIC_CODE_3").length() != 0)
            parm.setData("ANTIBIOTIC_CODE", getValue("ANTIBIOTIC_CODE_3"));
        // =========pangben modify 20110624 start
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            parm.setData("REGION_CODE", Operator.getRegion());
        // =========pangben modify 20110624 stop
        parm = INFCaseTool.getInstance().selectAntibiotrcd(parm);
        getTable("TABLE_1_3").removeRowAll();
        if (parm.getErrCode() < 0)
            return;
        if (parm.getCount() <= 0)
            return;
        setValue("MR_NO_3", parm.getData("MR_NO", 0));
        setValue("IPD_NO_3", parm.getData("IPD_NO", 0));
        setValue("DEPT_3", parm.getData("DEPT_CODE", 0));
        setValue("STATION_3", parm.getData("STATION_CODE", 0));
        setValue("DR_3", parm.getData("VS_DR_CODE", 0));
        setValue("PAT_NAME_3", parm.getData("PAT_NAME", 0));
        setValue(
                "AGE_3",
                StringTool.CountAgeByTimestamp((Timestamp) parm.getData(
                        "BIRTH_DATE", 0), SystemTool.getInstance().getDate())[0]);
        setValue("ANTIBIOTIC_CODE_3", parm.getData("ANTIBIOTIC_CODE", 0));
        getTable("TABLE_1_3").setParmValue(parm);
        getTable("TABLE_1_3").setSelectedRow(0);
    }

    /**
     * 第四个页签查询动作
     */
    public void onQueryFour() {
        TParm parm = new TParm();
        if (getValueString("START_DATE_4").length() != 0)
            parm.setData("START_DATE", getValue("START_DATE_4"));
        if (getValueString("END_DATE_4").length() != 0)
            parm.setData("END_DATE", getValue("END_DATE_4"));
        //===liling 20140407 start
        if (getValueString("DEPT_4").length() != 0){
            parm.setData("DEPT_CODE", getValue("DEPT_4"));}else{
            	this.messageBox("未选择科室！");
            	return;
            }
      //=====end
        if (getValueString("MR_NO_4").length() != 0)
            parm.setData("MR_NO", getValue("MR_NO_4"));
        if (getValueString("REPORT_DATE_4").length() != 0)
            parm.setData("REPORT_DATE", getValue("REPORT_DATE_4"));
        if (getValueString("REPORT_NO_4").length() != 0)
            parm.setData("REPORT_NO", getValue("REPORT_NO_4"));
        if (parm.getNames().length == 0) {
            messageBox("请输入至少一个查询条件");
            return;
        }
        if (getValueString("START_DATE_4").length() != 0
                && getValueString("END_DATE_4").length() != 0
                && (StringTool.getDateDiffer(
                        (Timestamp) getValue("START_DATE_4"),
                        (Timestamp) getValue("END_DATE_4")) > 0)) {
            messageBox("录入的日期不合法");
            return;
        }
        if (getValue("REP_Y_4").equals("Y"))
            parm.setData("REPORT_DATE_NOT_NULL", "Y");
        else if (getValue("REP_N_4").equals("Y"))
            parm.setData("REPORT_DATE_NULL", "Y");
        if (getValue("IN_Y_4").equals("Y"))
            parm.setData("DS_DATE_NULL", "Y");
        else if (getValue("IN_N_4").equals("Y"))
            parm.setData("DS_DATE_NOT_NULL", "Y");
        // ==========pangben modify 20110624 start
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            parm.setData("REGION_CODE", Operator.getRegion());
        // ==========pangben modify 20110624 stop
        parm = INFCaseTool.getInstance().selectInfCaseReport(parm);
     // ==========liling 20140407 modify 性别显示
        for(int i=0;i<parm.getCount();i++){
        	String sex=parm.getValue("SEX", i);
        	if(sex.equals("1"))
        	{sex="男";
        	}else{sex="女";}
        	parm.setData("SEX", i, sex);
        }
     // ==========  
        getTable("TABLE_1_4").removeRowAll();
        if (parm.getErrCode() < 0)
            return;
        if (parm.getCount() <= 0)
            return;
        getTable("TABLE_1_4").setParmValue(parm);
    }
    /**
	 * 登记表打印
	 * 第四个页签查询结果打印=====liling 20140402 新增
	 */
	public void onPrintINFCase() {
        if (getTable("TABLE_1_4").getRowCount() < 0) {
        	this.messageBox("没有可打印的数据");
        	return;
        }
//        String dept_code=getValueString("DEPT_4");
//        TParm parm1=new TParm(TJDODBTool.getInstance().
//        		select("select DEPT_CHN_DESC from SYS_DEPT where DEPT_CODE= '"+dept_code+"'"));
//        String dept=parm1.getValue("DEPT_CODE", 0);
        TParm parm=getTable("TABLE_1_4").getParmValue();
        TParm data = new TParm();// 打印数据
		data.setData("科室", "TEXT",this.getText("DEPT_4"));
		//data.setData("记录文件编号", "TEXT", "");
        TParm printParm = new TParm();//需要打印的表格数据
        for(int i=0;i<parm.getCount();i++){
        	String date1=parm.getValue("INF_DATE", i);
        	if(date1.length()==0||"".equals(date1)){
        	}else{
        		date1=date1.substring(0, 10);
        	}
        	String date2=parm.getValue("REPORT_DATE", i);
        	if(date2.length()==0||"".equals(date2)){
        	}else{
        		date2=date2.substring(0, 10);
        	}
        	printParm.addData("REPORT_NO",  parm.getValue("REPORT_NO", i));
        	printParm.addData("PAT_NAME",  parm.getValue("PAT_NAME", i));
        	printParm.addData("SEX",  parm.getValue("SEX", i));
        	printParm.addData("AGE", parm.getValue("AGE", i));
        	printParm.addData("MR_NO",   parm.getValue("MR_NO", i));
        	printParm.addData("IN_DIAG1",   parm.getValue("IN_DIAG1", i));
        	printParm.addData("INF_DIAG1",  parm.getValue("INF_DIAG1", i));
        	printParm.addData("INF_DATE",   date1);
        	printParm.addData("REPORT_DATE",  date2);
//        	String inf_code=parm.getValue("INF_DR", i);
//        	TParm parm2=new TParm(TJDODBTool.getInstance().
//         		select("select USER_NAME from SYS_OPERATOR where USER_ID= '"+inf_code+"'"));
//        	printParm.addData("INF_DR", parm2.getValue("USER_NAME", 0) );
        	printParm.addData("INF_DR", parm.getValue("INF_DR", i) );
        	printParm.addData("NOTE",  "  ");
        }
        printParm.setCount(printParm.getCount("REPORT_NO"));
        printParm.addData("SYSTEM", "COLUMNS", "REPORT_NO");
        printParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        printParm.addData("SYSTEM", "COLUMNS", "SEX");
        printParm.addData("SYSTEM", "COLUMNS", "AGE");
        printParm.addData("SYSTEM", "COLUMNS", "MR_NO");
        printParm.addData("SYSTEM", "COLUMNS", "IN_DIAG1");
        printParm.addData("SYSTEM", "COLUMNS", "INF_DIAG1");
        printParm.addData("SYSTEM", "COLUMNS", "INF_DATE");
        printParm.addData("SYSTEM", "COLUMNS", "REPORT_DATE");
        printParm.addData("SYSTEM", "COLUMNS", "INF_DR");
        printParm.addData("SYSTEM", "COLUMNS", "NOTE");
		data.setData("TABLE1", printParm.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\INF\\INFCaseMaintain.jhw", data);
	}
    /**
     * 第五个页签查询动作
     */
    public void onQueryFive() {//add by wanglong 20131105
        if (getValueString("START_DATE_5").length() == 0
                || getValueString("END_DATE_5").length() == 0) {
            messageBox("请输入开始时间及结束时间");
            return;
        }
//        if (StringTool.getDateDiffer((Timestamp) getValue("START_DATE_5"),
//                                     (Timestamp) getValue("END_DATE_5")) > 0) {
//            messageBox("录入的日期不合法");
//            return;
//        }
        TParm parm = new TParm();
        if (getValueString("DEPT_5").length() != 0) {
            parm.setData("DEPT_CODE", getValue("DEPT_5"));
        }
        if (getValueString("STATION_5").length() != 0) {
            parm.setData("STATION_CODE", getValue("STATION_5"));
        }
        if (getValueString("DR_5").length() != 0) {
            parm.setData("VS_DR_CODE", getValue("DR_5"));
        }
        if (getValueString("IPD_NO_5").length() != 0) {
            parm.setData("IPD_NO", PatTool.getInstance().checkMrno(getValueString("IPD_NO_5")));
        }
        if (getValueString("MR_NO_5").length() != 0) {
            parm.setData("MR_NO", PatTool.getInstance().checkMrno(getValueString("MR_NO_5")));
            setValue("MR_NO_5", PatTool.getInstance().checkMrno(getValueString("MR_NO_5")));
        }
        parm.setData("START_DATE", getValue("START_DATE_5"));
        String endDate = StringTool.getString((Timestamp) getValue("END_DATE_5"), "yyyyMMddHHmmss").substring(0, 8) + "235959";
        parm.setData("END_DATE", StringTool.getTimestamp(endDate, "yyyyMMddHHmmss"));
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            parm.setData("REGION_CODE", Operator.getRegion());
        }
        parm = INFCaseTool.getInstance().selectInvOpt(parm);
        getTable("TABLE_1_5").removeRowAll();
        if (parm.getErrCode() < 0) return;
        if (parm.getCount() <= 0) return;
        this.setValue("MR_NO_5", parm.getData("MR_NO", 0));
        this.setValue("IPD_NO_5", parm.getData("IPD_NO", 0));
        this.setValue("DEPT_5", parm.getData("DEPT_CODE", 0));
        this.setValue("STATION_5", parm.getData("STATION_CODE", 0));
        this.setValue("DR_5", parm.getData("VS_DR_CODE", 0));
        this.setValue("PAT_NAME_5", parm.getData("PAT_NAME", 0));
        this.setValue("AGE_5", StringTool.CountAgeByTimestamp((Timestamp) parm
                .getData("BIRTH_DATE", 0), SystemTool.getInstance().getDate())[0]);
        getTable("TABLE_1_5").setParmValue(parm);
        getTable("TABLE_1_5").setSelectedRow(0);
    }

    /**
     * 设置诊断信息
     * 
     * @param parmDiag
     *            TParm
     */
    private void setAdmDiagInf(TParm parmDiag) {
        for (int i = 0; i < parmDiag.getCount("ICD_CODE"); i++) {
            if (parmDiag.getData("IO_TYPE", i).equals("M")
                    && parmDiag.getData("MAINDIAG_FLG", i).equals("Y")) {
                inDiag1.setData("ICD_CODE", parmDiag.getData("ICD_CODE", i));
                inDiag1.setData("ICD_CHN_DESC",
                        parmDiag.getData("ICD_CHN_DESC", i));
            } else if (parmDiag.getData("IO_TYPE", i).equals("M")
                    && parmDiag.getData("MAINDIAG_FLG", i).equals("N")
                    && inDiag2.getValue("ICD_CODE").length() == 0
                    && inDiag2.getValue("ICD_CHN_DESC").length() == 0) {
                inDiag2.setData("ICD_CODE", parmDiag.getData("ICD_CODE", i));
                inDiag2.setData("ICD_CHN_DESC",
                        parmDiag.getData("ICD_CHN_DESC", i));
            } else if (parmDiag.getData("IO_TYPE", i).equals("O")
                    && parmDiag.getData("MAINDIAG_FLG", i).equals("Y")) {
                outDiag1.setData("ICD_CODE", parmDiag.getData("ICD_CODE", i));
                outDiag1.setData("ICD_CHN_DESC",
                        parmDiag.getData("ICD_CHN_DESC", i));
            } else if (parmDiag.getData("IO_TYPE", i).equals("O")
                    && parmDiag.getData("MAINDIAG_FLG", i).equals("N")
                    && outDiag2.getValue("ICD_CODE").length() == 0
                    && outDiag2.getValue("ICD_CHN_DESC").length() == 0) {
                outDiag2.setData("ICD_CODE", parmDiag.getData("ICD_CODE", i));
                outDiag2.setData("ICD_CHN_DESC",
                        parmDiag.getData("ICD_CHN_DESC", i));
            } else if (parmDiag.getData("IO_TYPE", i).equals("O")
                    && parmDiag.getData("MAINDIAG_FLG", i).equals("N")
                    && outDiag3.getValue("ICD_CODE").length() == 0
                    && outDiag3.getValue("ICD_CHN_DESC").length() == 0) {
                outDiag3.setData("ICD_CODE", parmDiag.getData("ICD_CODE", i));
                outDiag3.setData("ICD_CHN_DESC",
                        parmDiag.getData("ICD_CHN_DESC", i));
            }
        }
        setValue("IN_DIAG1_2", inDiag1.getData("ICD_CHN_DESC"));
        setValue("IN_DIAG2_2", inDiag2.getData("ICD_CHN_DESC"));
        setValue("OUT_DIAG1_2", outDiag1.getData("ICD_CHN_DESC"));
        setValue("OUT_DIAG2_2", outDiag2.getData("ICD_CHN_DESC"));
        setValue("OUT_DIAG3_2", outDiag3.getData("ICD_CHN_DESC"));
    }

    /**
     * 设置诊断信息
     * 
     * @param parmCase
     *            TParm
     * @param seqNum
     *            int
     */
    private void setInfDiagInf(TParm parmCase, int seqNum) {
        inDiag1.setData("ICD_CODE", parmCase.getData("IN_DIAG1", seqNum));
        inDiag1.setData("ICD_CHN_DESC",
                parmCase.getData("IN_DIAG1_DESC", seqNum));

        inDiag2.setData("ICD_CODE", parmCase.getData("IN_DIAG2", seqNum));
        inDiag2.setData("ICD_CHN_DESC",
                parmCase.getData("IN_DIAG2_DESC", seqNum));

        outDiag1.setData("ICD_CODE", parmCase.getData("OUT_DIAG1", seqNum));
        outDiag1.setData("ICD_CHN_DESC",
                parmCase.getData("OUT_DIAG1_DESC", seqNum));

        outDiag2.setData("ICD_CODE", parmCase.getData("OUT_DIAG2", seqNum));
        outDiag2.setData("ICD_CHN_DESC",
                parmCase.getData("OUT_DIAG2_DESC", seqNum));

        outDiag3.setData("ICD_CODE", parmCase.getData("OUT_DIAG3", seqNum));
        outDiag3.setData("ICD_CHN_DESC",
                parmCase.getData("OUT_DIAG3_DESC", seqNum));

//      infDiag1.setData("ICD_CODE", parmCase.getData("INF_DIAG1", seqNum));//delete by wanglong 20140217
//      infDiag1.setData("ICD_CHN_DESC",
//              parmCase.getData("INF_DIAG1_DESC", seqNum));
//
//      infDiag2.setData("ICD_CODE", parmCase.getData("INF_DIAG2", seqNum));
//      infDiag2.setData("ICD_CHN_DESC",
//              parmCase.getData("INF_DIAG2_DESC", seqNum));

    }

    /**
     * 设置TABLE_1_2数据
     * 
     * @param parmCase
     *            TParm
     */
    private void setTable1And2(TParm parmCase) {
        TParm tableParmCase = new TParm();
        for (int i = 0; i < parmCase.getCount(); i++) {
            if (parmCase.getData("REPORT_DATE", i) == null
                    || parmCase.getValue("REPORT_DATE", i).length() == 0)
                tableParmCase.addData("RPT_FLG", "N");
            else
                tableParmCase.addData("RPT_FLG", "Y");
            tableParmCase.addData("INFCASE_SEQ",
                    parmCase.getData("INFCASE_SEQ", i));
            tableParmCase.addData("INF_DATE", parmCase.getData("INF_DATE", i));
            tableParmCase.addData("INF_NO", parmCase.getData("INF_NO", i));
        }
        getTable("TABLE_1_2").setParmValue(tableParmCase);
    }

    /**
     * 根据病患感染序号住院序号设置感染原因表格
     * 
     * @param caseNo
     *            String
     * @param infNo
     *            String
     */
    private void setTable2And2ByCaseInfNo(String caseNo, String infNo) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        parm.setData("INF_NO", infNo);
        TParm result = INFCaseTool.getInstance().selectInfReasonByCaseInfNo(
                parm);
        getTable("TABLE_2_2").setParmValue(result);
    }

    /**
     * 根据病患感染序号住院序号设置感染原因表格
     * 
     * @param caseNo
     *            String
     * @param infNo
     *            String
     */
    private void setTable3And2ByCaseInfNo(String caseNo, String infNo) {
        // 介入信息是否被拿掉待确认
        if (true)
            return;
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        parm.setData("INF_NO", infNo);
        TParm result = INFCaseTool.getInstance().selectInfIntvoprecByCaseInfNo(
                parm);
        getTable("TABLE_3_2").setParmValue(result);
    }

    /**
     * 根据病患感染序号住院序号设置检实验结果表格
     * 
     * @param caseNo
     *            String
     * @param infNo
     *            String
     */
    private void setTable4And2ByCaseInfNo(String caseNo, String infNo) {
        // 表结构有改动待确认
        if (true)
            return;
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        parm.setData("INF_NO", infNo);
        TParm result = INFCaseTool.getInstance().selectInfResultByCaseInfNo(
                parm);
        getTable("TABLE_4_2").setParmValue(result);
    }
    
    /**
     * 根据病患感染序号住院序号设置感染部位诊断表格
     * 
     * @param caseNo
     *            String
     * @param infNo
     *            String
     */
    private void setTable5And2ByCaseInfNo(String caseNo, String infNo) {//add by wanglong 20140217
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        parm.setData("INF_NO", infNo);
        TParm result = new TParm();
        result = INFCaseTool.getInstance().selectInfICDPartByCaseInfNo(parm);
        getTable("TABLE_5_2").setParmValue(result);
        int row = getTable("TABLE_5_2").addRow();
        getTable("TABLE_5_2").getParmValue().setData("MAIN_FLG", row, "N");
        getTable("TABLE_5_2").getParmValue().setData("PART_CODE", row, "");
        getTable("TABLE_5_2").getParmValue().setData("ICD_CODE", row, "");
        getTable("TABLE_5_2").getParmValue().setData("ICD_DESC", row, "");
        getTable("TABLE_5_2").getParmValue().setData("UM_CODE", row, "");
        getTable("TABLE_5_2").getParmValue().setData("UM_DESC", row, "");
        if (!infNo.equals("")) {
            getTable("TABLE_5_2").getParmValue().setData("SEQ", row,
                                                         result.getInt("SEQ", row - 1) + 1);
        } else {
            getTable("TABLE_5_2").getParmValue().setData("SEQ", row, 1);
        }
    }
    
    /**
     * 根据病患感染序号住院序号设置侵入性操作表格
     * 
     * @param caseNo
     *            String
     * @param infNo
     *            String
     */
    private void setTable6And2ByCaseInfNo(String caseNo, String infNo) {//add by wanglong 20140217
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        TParm result = new TParm();
        if (!infNo.equals("")) {
            parm.setData("INF_NO", infNo);
            result = INFCaseTool.getInstance().selectInfIoByCaseInfNo(parm);
        } else {
            result = INFCaseTool.getInstance().selectInfIoFromOdiByCase(parm);
        }
        getTable("TABLE_6_2").setParmValue(result);
    }

    /**
     * 取得Table控件
     * 
     * @param tableTag
     *            String
     * @return TTable
     */
    private TTable getTable(String tableTag) {
        return ((TTable) getComponent(tableTag));
    }

    /**
     * 使用抗生素注记
     */
    public void onChangeAntibiotrcdFlg() {
        ((TComboBox) getComponent("ANTIBIOTIC_CODE_1")).setValue("");
        if (getValueString("ANTIBIOTRCD_FLG_1").equals("N"))
            ((TComboBox) getComponent("ANTIBIOTIC_CODE_1")).setEnabled(false);
        else
            ((TComboBox) getComponent("ANTIBIOTIC_CODE_1")).setEnabled(true);
    }

    /**
     * 取得时间日期字符串格式
     * 
     * @param dateTag
     *            String
     * @return String
     */
    private String getDateString(String dateTag) {
        return getValueString(dateTag).substring(0, 10).replace("-", "");
    }

    /**
     * 取得数据库访问类
     * 
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * 保存动作
     */
    public void onSave() {
        int index = ((TTabbedPane) getComponent("TabbedPane_1"))
                .getSelectedIndex();
        switch (index) {
        case 0:
            return;
        case 1:
            onSaveTwo();
            break;
        case 2:
            onSaveThree();
            break;
        case 3:
            onSaveFour();
            break;
        }
    }

    /**
     * 第二个页签保存动作检核
     * 
     * @return boolean
     */
    private boolean onSaveTwoCheck() {
        if (getValueString("INF_DATE_2").length() == 0) {
            messageBox("感染日期不可为空");
            return true;
        }
//      if (getValueString("INF_DIAG1_2").length() == 0) {//delete by wanglong 20140217
//          messageBox("感染诊断不可为空");
//          return true;
//      }
//      if (getValueString("INFPOSITION_CODE_2").length() == 0) {
//          messageBox("感染部位不可为空");
//          return true;
//      }
        if (getValueString("REGISTER_DATE_2").length() == 0) {
            messageBox("登记时间不可为空");
            return true;
        }
        for (int i = 0; i < getTable("TABLE_1_2").getRowCount(); i++) {
            if (getValueString("INF_NO_2").length() == 0
                    && getValue("INF_DATE_2").equals(
                            getTable("TABLE_1_2").getValueAt(i, 2))) {
                messageBox("本日已有感染案件登记");
                return true;
            }
        }
        TParm parm = getTable("TABLE_2_2").getParmValue();
        for (int i = 0; i < parm.getCount("SEL_FLG"); i++) {
            if (parm.getData("SEL_FLG", i).equals("N"))
                continue;
            if (parm.getValue("INFREASON_NOTE", i).length() > 100) {
                messageBox("发生感染相关因素备注长度不可超过100");
                return true;
            }
        }
        
        //add by wanglong 20140217
        TParm parm5 = getTable("TABLE_5_2").getParmValue();
        if (parm5.getCount() < 1
                || (parm5.getCount() == 1 && (StringUtil.isNullString(parm5.getValue("PART_CODE", 0))
                || StringUtil.isNullString(parm5.getValue("ICD_DESC", 0))
                || StringUtil.isNullString(parm5.getValue("UM_DESC", 0))))) {
            messageBox("感染部位与诊断表不能为空");
            return true;
        }
        int j = 0;
        for (; j < parm5.getCount("MAIN_FLG"); j++) {
            if (parm5.getData("MAIN_FLG", j).equals("N")) continue;
            else break;
        }
        if (j == parm5.getCount("MAIN_FLG")) {
            messageBox("感染部位与诊断表需要设置主记录");
            return true;
        }
        TParm parm6 = getTable("TABLE_6_2").getParmValue();
        for (int k = 0; k < parm6.getCount("SEL_FLG"); k++) {
            if (parm6.getData("SEL_FLG", k).equals("N")) continue;
            if (parm6.getValue("IO_NOTE", k).length() > 100) {
                messageBox("侵入性操作表的备注长度不可超过100");
                return true;
            }
        }
        //add end
        
        if (chkAntiTest())
            return true;
        return false;
    }

    /**
     * 第二个页签保存动作
     */
    private void onSaveTwo() {
        getTable("TABLE_1_2").acceptText();
        getTable("TABLE_2_2").acceptText();
        getTable("TABLE_4_2").acceptText();
        getTable("TABLE_5_2").acceptText();//add by wanglong 20140217
        getTable("TABLE_6_2").acceptText();//add end
        if (onSaveTwoCheck())
            return;
        TParm parm = new TParm();
        parm.setData("INF_NO", getValue("INF_NO_2"));
        parm.setData("CASE_NO", getValue("CASE_NO_2"));
        if (getValueString("INF_NO_2").length() > 0
                && getTable("TABLE_1_2").getSelectedRow() >= 0)
            parm.setData(
                    "INFCASE_SEQ",
                    getTable("TABLE_1_2").getValueAt(
                            getTable("TABLE_1_2").getSelectedRow(), 1));
        else
            parm.setData("INFCASE_SEQ", getTable("TABLE_1_2").getRowCount() + 1);
        parm.setData("IPD_NO", getValue("IPD_NO_2"));
        parm.setData("MR_NO", getValue("MR_NO_2"));
        parm.setData("INF_DATE", getValue("INF_DATE_2"));
        parm.setData("ADM_DAYS", getValue("ADM_DAYS_2"));
        parm.setData("DEPT_CODE", getValue("DEPT_CODE_2"));
        TParm parmAdm = getAdmInpInf(getValueString("CASE_NO_2"));
        if (parmAdm != null) {
            parm.setData("STATION_CODE", parmAdm.getData("STATION_CODE", 0));
            parm.setData("BED_NO", parmAdm.getData("BED_NO", 0));
            parm.setData("VS_DR", parmAdm.getData("VS_DR_CODE", 0));
        } else {
            parm.setData("STATION_CODE", "");
            parm.setData("BED_NO", "");
            parm.setData("VS_DR", "");
        }
//      if (getValueString("INF_DIAG1_2").length() != 0)//delete by wanglong 20140217
//          parm.setData("INF_DIAG1", infDiag1.getData("ICD_CODE"));
//      else {
//          parm.setData("INF_DIAG1", "");
//          infDiag1.setData("ICD_CODE", "");
//          infDiag1.setData("ICD_CHN_DESC", "");
//      }
//      if (getValueString("INF_DIAG2_2").length() != 0)
//          parm.setData("INF_DIAG2", infDiag2.getData("ICD_CODE"));
//      else {
//          parm.setData("INF_DIAG2", "");
//          infDiag2.setData("ICD_CODE", "");
//          infDiag2.setData("ICD_CHN_DESC", "");
//      }
//      parm.setData("INFPOSITION_CODE", getValue("INFPOSITION_CODE_2"));
//      parm.setData("INFPOSITION_DTL", getValue("INFPOSITION_DTL_2"));
        parm.setData("INF_DIAG1", "");//add by wanglong 20140217
        parm.setData("INF_DIAG2", "");
        parm.setData("INFPOSITION_CODE", "");
        parm.setData("INFPOSITION_DTL", "");//add end
        parm.setData("DIEINFLU_CODE", getValue("DIEINFLU_CODE_2"));
        parm.setData("INFRETN_CODE", getValue("INFRETN_CODE_2"));
        parm.setData("OP_CODE", getValue("OP_CODE_2"));
        parm.setData("OP_DATE", getValue("OP_DATE_2"));
        parm.setData("OPCUT_TYPE", getValue("OPCUT_TYPE_2"));
        parm.setData("ANA_TYPE", getValue("ANA_TYPE_2"));
        if (getValueString("URGTOP_FLG_A_2").equals("Y"))
            parm.setData("URGTOP_FLG", "Y");
        else if (getValueString("URGTOP_FLG_B_2").equals("Y"))
            parm.setData("URGTOP_FLG", "N");
        parm.setData("OP_TIME", addZreo(getValueString("OP_TIME_HH_2"), 2)
                + addZreo(getValueString("OP_TIME_MM_2"), 2));
        parm.setData("OP_DR", getValueString("OP_DR_2"));
        if (getValueString("IN_DIAG1_2").length() != 0)
            parm.setData("IN_DIAG1", inDiag1.getData("ICD_CODE"));
        else {
            parm.setData("IN_DIAG1", "");
            inDiag1.setData("ICD_CODE", "");
            inDiag1.setData("ICD_CHN_DESC", "");
        }
        if (getValueString("IN_DIAG2_2").length() != 0)
            parm.setData("IN_DIAG2", inDiag2.getData("ICD_CODE"));
        else {
            parm.setData("IN_DIAG2", "");
            inDiag2.setData("ICD_CODE", "");
            inDiag2.setData("ICD_CHN_DESC", "");
        }
        if (getValueString("OUT_DIAG1_2").length() != 0)
            parm.setData("OUT_DIAG1", outDiag1.getData("ICD_CODE"));
        else {
            parm.setData("OUT_DIAG1", "");
            outDiag1.setData("ICD_CODE", "");
            outDiag1.setData("ICD_CHN_DESC", "");
        }
        if (getValueString("OUT_DIAG2_2").length() != 0)
            parm.setData("OUT_DIAG2", outDiag2.getData("ICD_CODE"));
        else {
            parm.setData("OUT_DIAG2", "");
            outDiag2.setData("ICD_CODE", "");
            outDiag2.setData("ICD_CHN_DESC", "");
        }
        if (getValueString("OUT_DIAG3_2").length() != 0)
            parm.setData("OUT_DIAG3", outDiag3.getData("ICD_CODE"));
        else {
            parm.setData("OUT_DIAG3", "");
            outDiag3.setData("ICD_CODE", "");
            outDiag3.setData("ICD_CHN_DESC", "");
        }
        parm.setData("CHARGE_FEE", getValueString("CHARGE_FEE_2"));
        if (getValueString("ETIOLGEXM_FLG_Y_2").equals("Y"))
            parm.setData("ETIOLGEXM_FLG", "Y");
        else if (getValueString("ETIOLGEXM_FLG_N_2").equals("Y"))
            parm.setData("ETIOLGEXM_FLG", "N");
        parm.setData("EXAM_DATE", getValue("EXAM_DATE_2"));
        parm.setData("SPECIMEN_CODE", getValueString("SPECIMEN_CODE_2"));
        if (getValueString("LABWAY_1_2").equals("Y"))
            parm.setData("LABWAY", "1");
        else if (getValueString("LABWAY_2_2").equals("Y"))
            parm.setData("LABWAY", "2");
        else if (getValueString("LABWAY_3_2").equals("Y"))
            parm.setData("LABWAY", "3");
        else
            parm.setData("LABWAY", "");
        if (getValueString("LABPOSITIVE_A_2").equals("Y"))
            parm.setData("LABPOSITIVE", "Y");
        else if (getValueString("LABPOSITIVE_B_2").equals("Y"))
            parm.setData("LABPOSITIVE", "N");
        else
            parm.setData("LABPOSITIVE", "");
        parm.setData("PATHOGEN1_CODE", getValue("PATHOGEN1_CODE_2"));
        parm.setData("PATHOGEN2_CODE", getValue("PATHOGEN2_CODE_2"));
        parm.setData("PATHOGEN3_CODE", getValue("PATHOGEN3_CODE_2"));
        if (getValueString("ANTIBIOTEST_FLG_Y_2").equals("Y"))
            parm.setData("ANTIBIOTEST_FLG", "Y");
        else if (getValueString("ANTIBIOTEST_FLG_N_2").equals("Y"))
            parm.setData("ANTIBIOTEST_FLG", "N");
        parm.setData("REGISTER_DATE", getValue("REGISTER_DATE_2"));
        parm.setData("INF_DR", getValueString("INF_DR_2"));
        parm.setData("CLINICAL_SYMP", getValue("CLINICAL_SYMP_2"));
        // 目前采用的控制措施
        parm.setData("INF_PLAN", "");
        // 上报日期
        parm.setData("REPORT_DATE", "");
        // 上报编号
        parm.setData("REPORT_NO", "");
        // 检核术后ICH进出时间是否合法
        if (getValueString("INICU_DATE_2").length() == 0
                && getValueString("OUTICU_DATE_2").length() != 0) {
            messageBox("请录入术后进ICU时间");
            return;
        }
        if (getValueString("INICU_DATE_2").length() != 0
                && getValueString("OUTICU_DATE_2").length() == 0) {
            messageBox("请录入术后出ICU时间");
            return;
        }
        if (getValueString("INICU_DATE_2").length() != 0
                && getValueString("INICU_DATE_2").length() != 0
                && StringTool.getDateDiffer(
                        (Timestamp) getValue("INICU_DATE_2"),
                        (Timestamp) getValue("OUTICU_DATE_2")) > 0) {
            messageBox("录入的术后进入ICU,出ICU时间不合法");
            return;
        }
        parm.setData("INICU_DATE", getValue("INICU_DATE_2"));
        parm.setData("OUTICU_DATE", getValue("OUTICU_DATE_2"));
        parm.setData("CANCEL_FLG", "N");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("REGION_CODE", Operator.getRegion());// ========pangben
                                                            // modify 20110628
        clearTParmNullSingle(parm);
        TParm transParm = new TParm();
        transParm.setData("INF_CASE", parm.getData());
        transParm.setData("INF_REASON", setInfReason().getData());
        transParm.setData("INF_ANTIBIOTEST", setInfAntibioTest().getData());
        //add by wanglong 20140217
        TParm infICDpartParm = setInfICDPart();
        for (int i = 0; i < infICDpartParm.getCount("MAIN_FLG"); i++) {
            if (infICDpartParm.getValue("MAIN_FLG", i).equals("Y")) {
                parm.setData("INF_DIAG1", infICDpartParm.getValue("ICD_CODE", i));//感染诊断
                parm.setData("INFPOSITION_CODE", infICDpartParm.getValue("PART_CODE", i));//感染部位
                break;
            }
        }
        transParm.setData("INF_ICDPART", infICDpartParm.getData());
        transParm.setData("INF_IO", setInIO().getData());
        //add end
        transParm.setData("REGION_CODE", Operator.getRegion()); // ========pangben
                                                                // modify
                                                                // 20110624
        transParm = TIOM_AppServer.executeAction("action.inf.InfAction",
                "onSaveInfCase", transParm);
        if (transParm.getErrCode() < 0) {
            messageBox("保存失败");
            return;
        }
        messageBox("保存成功");
        onQueryTwo();
    }

    /**
     * 检核病原体试验数据是否合理
     * 
     * @return boolean
     */
    private boolean chkAntiTest() {
        TParm parm = getTable("TABLE_4_2").getParmValue();
        for (int i = 0; i < getTable("TABLE_4_2").getRowCount(); i++) {
            // if (parm.getData("DEL_FLG",i).equals("Y"))
            // continue;
            if (parm.getValue("CULTURE_CODE", i).length() == 0
                    || parm.getValue("ANTI_CODE", i).length() == 0
                    || parm.getValue("SENS_LEVEL", i).length() == 0) {
                messageBox("有数据的病原体、检测药物或试验结果为空");
                return true;
            }
            for (int j = 0; j < getTable("TABLE_4_2").getRowCount(); j++) {
                if (i == j)
                    continue;
                if (parm.getData("CULTURE_CODE", i).equals(
                        parm.getData("CULTURE_CODE", j))
                        && parm.getData("ANTI_CODE", i).equals(
                                parm.getData("ANTI_CODE", j))) {
                    messageBox("有重复的病原体与检测药物");
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * 补零
     * 
     * @param str
     *            String
     * @param length
     *            int
     * @return String
     */
    private String addZreo(String str, int length) {
        for (int i = 0; i < length - str.length(); i++) {
            str = "0" + str;
        }
        return str;
    }

    /**
     * 第三个页签保存动作
     */
    private void onSaveThree() {
        getTable("TABLE_1_3").acceptText();
        TParm parmTable = getTable("TABLE_1_3").getParmValue();
        int row = getTable("TABLE_1_3").getRowCount();
        if (row <= 0) {
            messageBox("无保存数据");
            return;
        }
        TParm parm = new TParm();
        for (int i = 0; i < row; i++) {
            if (parmTable.getValue("ILLEGIT_REMARK", i).length() > 100) {
                messageBox("不合理原因长度不可大于100");
                return;
            }
            if (parmTable.getValue("MEDALLERG_SYMP", i).length() > 200) {
                messageBox("不良反应长度不可大于200");
                return;
            }
            parm.addData("ORDER_NO", parmTable.getValue("ORDER_NO", i));
            parm.addData("ORDER_SEQ", parmTable.getData("ORDER_SEQ", i));
            parm.addData("ORDER_CODE", parmTable.getValue("ORDER_CODE", i));
            parm.addData("CASE_NO", parmTable.getValue("CASE_NO", i));
            parm.addData("IPD_NO", parmTable.getValue("IPD_NO", i));
            parm.addData("MR_NO", parmTable.getValue("MR_NO", i));
            parm.addData("ILLEGIT_FLG", parmTable.getData("ILLEGIT_FLG", i));
            parm.addData("ILLEGIT_REMARK",
                    parmTable.getValue("ILLEGIT_REMARK", i));
            parm.addData("MEDALLERG_SYMP",
                    parmTable.getValue("MEDALLERG_SYMP", i));
            parm.addData("OPT_USER", Operator.getID());
            parm.addData("OPT_DATE", SystemTool.getInstance().getDate());
            parm.addData("OPT_TERM", Operator.getIP());
        }
        parm = TIOM_AppServer.executeAction("action.inf.InfAction",
                "onSaveInfAntibiotrcd", parm);
        if (parm.getErrCode() < 0) {
            messageBox(parm.getErrText());
            return;
        }
        messageBox("保存成功");
    }

    /**
     * 第四个页签保存动作
     */
    private void onSaveFour() {
        int row = getTable("TABLE_1_4").getSelectedRow();
        if (row < 0) {
            messageBox("请选择保存信息");
            return;
        }
        if (getValueString("REPORT_NO_4").length() == 0
                || getValueString("REPORT_DATE_4").length() == 0) {
            messageBox("请同时输入上报日期以及上报编号");
            return;
        }
        TParm tableParm = getTable("TABLE_1_4").getParmValue();
        TParm parm = new TParm();
        parm.setData("INF_NO", tableParm.getData("INF_NO", row));
        parm.setData("CASE_NO", tableParm.getData("CASE_NO", row));
        parm.setData("INFCASE_SEQ", tableParm.getData("INFCASE_SEQ", row));
        parm.setData("REPORT_DATE", getValue("REPORT_DATE_4"));
        parm.setData("REPORT_NO", getValue("REPORT_NO_4"));
        parm = TIOM_AppServer.executeAction("action.inf.InfAction",
                "updateInfCaseReport", parm);
        if (parm.getErrCode() < 0) {
            messageBox("保存失败");
            return;
        }
        messageBox("保存成功");
        onQueryFour();
    }
    
    /**
     * 设置感染因素保存数据
     * 
     * @return TParm
     */
    private TParm setInfReason() {
        TParm parm = getTable("TABLE_2_2").getParmValue();
        TParm parmReason = new TParm();
        for (int i = 0; i < getTable("TABLE_2_2").getRowCount(); i++) {
            if (parm.getData("SEL_FLG", i).equals("N"))
                continue;
            parmReason.addData("INF_NO", getValue("INF_NO_2"));
            parmReason.addData("INFREASON_CODE", parm.getData("ID", i));
            parmReason.addData("CASE_NO", getValue("CASE_NO_2"));
            parmReason.addData("IPD_NO", getValue("IPD_NO_2"));
            parmReason.addData("MR_NO", getValue("MR_NO_2"));
            if (getValueString("INF_NO_2").length() > 0
                    && getTable("TABLE_1_2").getSelectedRow() >= 0)
                parmReason.addData("INFCASE_SEQ", getTable("TABLE_1_2")
                        .getValueAt(getTable("TABLE_1_2").getSelectedRow(), 1));
            else
                parmReason.addData("INFCASE_SEQ", getTable("TABLE_1_2")
                        .getRowCount() + 1);
            parmReason.addData("INFREASON_NOTE",
                    parm.getData("INFREASON_NOTE", i));
            parmReason.addData("OPT_USER", Operator.getID());
            parmReason.addData("OPT_DATE", SystemTool.getInstance().getDate());
            parmReason.addData("OPT_TERM", Operator.getIP());
        }
        clearTParmNull(parmReason);
        return parmReason;
    }
    
    /**
     * 设置感染部位保存数据
     * 
     * @return TParm
     */
    private TParm setInfICDPart() {//add by wanglong 20140217
        TParm parm = getTable("TABLE_5_2").getParmValue();
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount("MAIN_FLG"); i++) {
            if (StringUtil.isNullString(parm.getValue("PART_CODE", i))
                    && StringUtil.isNullString(parm.getValue("ICD_DESC", i))) {
                continue;
            }
            result.addData("INF_NO", this.getValue("INF_NO_2"));
            if (getValueString("INF_NO_2").length() > 0
                    && getTable("TABLE_1_2").getSelectedRow() >= 0) {
                result.addData("INFCASE_SEQ",
                               getTable("TABLE_1_2").getValueAt(getTable("TABLE_1_2")
                                                                        .getSelectedRow(), 1));
            } else {
                result.addData("INFCASE_SEQ", getTable("TABLE_1_2").getRowCount() + 1);
            }
            result.addData("SEQ", parm.getData("SEQ", i));
            result.addData("CASE_NO", this.getValue("CASE_NO_2"));
            result.addData("IPD_NO", this.getValue("IPD_NO_2"));
            result.addData("MR_NO", this.getValue("MR_NO_2"));
            if (parm.getData("MAIN_FLG", i).equals("Y")) {
                result.addData("MAIN_FLG", "Y");
            } else {
                result.addData("MAIN_FLG", "N");
            }
            result.addData("PART_CODE", parm.getData("PART_CODE", i));
            result.addData("ICD_CODE", parm.getData("ICD_CODE", i));
            result.addData("UM_CODE", parm.getData("UM_CODE", i));
            result.addData("OPT_USER", Operator.getID());
            result.addData("OPT_DATE", SystemTool.getInstance().getDate());
            result.addData("OPT_TERM", Operator.getIP());
        }
        clearTParmNull(result);
        return result;
    }
    
    /**
     * 设置侵入性操作保存数据
     * 
     * @return TParm
     */
    private TParm setInIO() {//add by wanglong 20140217
        TParm parm = getTable("TABLE_6_2").getParmValue();
        TParm result = new TParm();
        for (int i = 0; i < getTable("TABLE_6_2").getRowCount(); i++) {
            if (parm.getData("SEL_FLG", i).equals("N")) continue;
            result.addData("INF_NO", this.getValue("INF_NO_2"));
            if (getValueString("INF_NO_2").length() > 0
                    && getTable("TABLE_1_2").getSelectedRow() >= 0) {
                result.addData("INFCASE_SEQ",
                               getTable("TABLE_1_2").getValueAt(getTable("TABLE_1_2")
                                                                        .getSelectedRow(), 1));
            } else {
                result.addData("INFCASE_SEQ", getTable("TABLE_1_2").getRowCount() + 1);
            }
            result.addData("CASE_NO", this.getValue("CASE_NO_2"));
            result.addData("IPD_NO", this.getValue("IPD_NO_2"));
            result.addData("MR_NO", this.getValue("MR_NO_2"));
            result.addData("IO_CODE", parm.getData("IO_CODE", i));
            result.addData("IO_NOTE", parm.getData("IO_NOTE", i));
            result.addData("OPT_USER", Operator.getID());
            result.addData("OPT_DATE", SystemTool.getInstance().getDate());
            result.addData("OPT_TERM", Operator.getIP());
        }
        clearTParmNull(result);
        return result;
    }
    
    /**
     * 设置检验结果数据
     * 
     * @return TParm
     */
    private TParm setInfAntibioTest() {
        TParm infAntibioTest = new TParm();
        TParm parm = getTable("TABLE_4_2").getParmValue();
        for (int i = 0; i < getTable("TABLE_4_2").getRowCount(); i++) {
            // if(parm.getData("DEL_FLG",i).equals("Y"))
            // continue;
            // if(!parm.getData("DEL_FLG",i).equals("Y") &&
            // (parm.getValue("PATHOGEN_CODE",i).length() == 0 ||
            // parm.getValue("EXM_PHA",i).length() == 0 ||
            // parm.getValue("RESULT",i).length() == 0))
            // continue;
            infAntibioTest.addData("INF_NO", getValue("INF_NO_2"));
            if (getValueString("INF_NO_2").length() > 0
                    && getTable("TABLE_1_2").getSelectedRow() >= 0)
                infAntibioTest.addData("INFCASE_SEQ", getTable("TABLE_1_2")
                        .getValueAt(getTable("TABLE_1_2").getSelectedRow(), 1));
            else
                infAntibioTest.addData("INFCASE_SEQ", getTable("TABLE_1_2")
                        .getRowCount() + 1);
            infAntibioTest.addData("HOSP_AREA", "HIS");
            infAntibioTest.addData("CULURE_CODE",
                    parm.getData("CULTURE_CODE", i));
            infAntibioTest.addData("ANTI_CODE", parm.getData("ANTI_CODE", i));
            infAntibioTest.addData("SENS_LEVEL", parm.getData("SENS_LEVEL", i));
            infAntibioTest.addData("CASE_NO", getValue("CASE_NO_2"));

            infAntibioTest.addData("INFECTLEVEL", "");
            infAntibioTest.addData("GRAMSTAIN", "");
            infAntibioTest.addData("COLONYCOUNT", "");
            infAntibioTest.addData("CANCEL_FLG", "N");

            infAntibioTest.addData("OPT_USER", Operator.getID());
            infAntibioTest.addData("OPT_DATE", SystemTool.getInstance()
                    .getDate());
            infAntibioTest.addData("OPT_TERM", Operator.getIP());
        }
        clearTParmNull(infAntibioTest);
        return infAntibioTest;
    }

    /**
     * 清理TParm中的空值(单列)
     * 
     * @param parm
     *            TParm
     */
    private void clearTParmNullSingle(TParm parm) {
        String names[] = parm.getNames();
        for (int i = 0; i < names.length; i++) {
            if (parm.getData(names[i]) != null)
                continue;
            parm.setData(names[i], "");
        }
    }

    /**
     * 清理TParm中的空值(多列)
     * 
     * @param parm
     *            TParm
     */
    private void clearTParmNull(TParm parm) {
        String names[] = parm.getNames();
        for (int i = 0; i < names.length; i++) {
            for (int j = 0; j < parm.getCount(names[i]); j++) {
                if (parm.getData(names[i], j) != null)
                    continue;
                parm.setData(names[i], j, "");
            }
        }
    }

    /**
     * 取得病患住院基本信息
     * 
     * @param caseNo
     *            String
     * @return TParm
     */
    private TParm getAdmInpInf(String caseNo) {
        String SQL = "SELECT STATION_CODE,BED_NO,VS_DR_CODE FROM ADM_INP WHERE CASE_NO = '"
                + caseNo + "'";
        TParm parm = new TParm(getDBTool().select(SQL));
        if (parm.getCount() <= 0)
            return null;
        return parm;
    }

    /**
     * 新增动作
     */
    public void onNewAction() {
        setValue("INF_NO_2", "");
        setValue("REGISTER_DATE_2", "");
        setValue("INF_DR_2", "");
        // 住院天数不清空

        setValue("INF_DATE_2", "");
//      setValue("INFPOSITION_CODE_2", "");//delete by wanglong 20140217
//      setValue("INFPOSITION_DTL_2", "");
        setValue("IN_DIAG1_2", "");
        setValue("IN_DIAG2_2", "");
        setValue("OUT_DIAG1_2", "");
        setValue("OUT_DIAG2_2", "");
        setValue("OUT_DIAG3_2", "");
//      setValue("INF_DIAG1_2", "");//delete by wanglong 20140217
//      setValue("INF_DIAG2_2", "");
        setValue("DIEINFLU_CODE_2", "");
        setValue("INFRETN_CODE_2", "");
        setValue("OP_CODE_2", "");
        setValue("OP_DATE_2", "");
        setValue("OPCUT_TYPE_2", "");
        setValue("ANA_TYPE_2", "");
        setValue("ANA_TYPE_2", "");
        setValue("URGTOP_FLG_A_2", "N");
        setValue("URGTOP_FLG_B_2", "Y");
        setValue("OP_TIME_HH_2", "");
        setValue("OP_TIME_MM_2", "");
        setValue("OP_DR_2", "");
        setValue("INICU_DATE_2", "");
        setValue("OUTICU_DATE_2", "");
        setValue("ETIOLGEXM_FLG_Y_2", "N");
        setValue("ETIOLGEXM_FLG_N_2", "Y");
        onEtiolgexmFlgNTwo();
        setValue("SPECIMEN_CODE_2", "");
        setValue("EXAM_DATE_2", "");
        setValue("LABWAY_1_2", "N");
        setValue("LABWAY_2_2", "N");
        setValue("LABWAY_3_2", "N");
        setValue("LABPOSITIVE_A_2", "N");
        setValue("LABPOSITIVE_B_2", "Y");
        setValue("PATHOGEN1_CODE_2", "");
        setValue("PATHOGEN2_CODE_2", "");
        setValue("PATHOGEN3_CODE_2", "");
        setValue("ANTIBIOTEST_FLG_Y_2", "N");
        setValue("ANTIBIOTEST_FLG_N_2", "Y");
        // 感控记录表格初始化
        getTable("TABLE_1_2")
                .setParmValue(getTable("TABLE_1_2").getParmValue());
        // 感染原因表格初始化
        for (int i = 0; i < getTable("TABLE_2_2").getRowCount(); i++) {
            getTable("TABLE_2_2").setValueAt("N", i, 0);
            getTable("TABLE_2_2").getParmValue().setData("SEL_FLG", i, "N");
            getTable("TABLE_2_2").setValueAt("", i, 2);
            getTable("TABLE_2_2").getParmValue().setData("INFREASON_NOTE", i,
                    "");
        }
        // 试验结果表格初始化
        getTable("TABLE_4_2").removeRowAll();
        //add by wanglong 20140217
        getTable("TABLE_5_2").removeRowAll();
        //侵入性操作表格初始化
        for (int i = 0; i < getTable("TABLE_6_2").getRowCount(); i++) {
            getTable("TABLE_6_2").setValueAt("N", i, 0);
            getTable("TABLE_6_2").getParmValue().setData("SEL_FLG", i, "N");
            getTable("TABLE_6_2").setValueAt("", i, 2);
            getTable("TABLE_6_2").getParmValue().setData("IO_NOTE", i,
                    "");
        }
        //add end
        // 入院诊断1
        inDiag1 = new TParm();
        // 入院诊断2
        inDiag2 = new TParm();
        // 出院诊断1
        outDiag1 = new TParm();
        // 出院诊断2
        outDiag2 = new TParm();
        // 出院诊断3
        outDiag3 = new TParm();
        // 感控诊断2
//      infDiag1 = new TParm();//delete by wanglong 20140217
        // 感控诊断3
//      infDiag2 = new TParm();
        TParm parmDiag = new TParm();
        parmDiag.setData("CASE_NO", getValue("CASE_NO_2"));
        parmDiag = INFCaseTool.getInstance().caseRegisterDiag(parmDiag);
        setAdmDiagInf(parmDiag);
    }

    /**
     * 第四个页签表格单击事件
     */
    public void onTableOneAndFour() {
        int row = getTable("TABLE_1_4").getSelectedRow();
        TParm tableParm = getTable("TABLE_1_4").getParmValue();
        setValue("DEPT_4", tableParm.getData("DEPT_CODE", row));
        setValue("STATION_4", tableParm.getData("STATION_CODE", row));
        setValue("IPD_NO_4", tableParm.getData("IPD_NO", row));
        setValue("MR_NO_4", tableParm.getData("MR_NO", row));
        setValue("PAT_NAME_4", tableParm.getData("PAT_NAME", row));
        setValue("REPORT_DATE_4", tableParm.getData("REPORT_DATE", row));
        setValue("REPORT_NO_4", tableParm.getData("REPORT_NO", row));
    }

    /**
     * 第二个页签感控记录表格单击事件
     */
    public void onTableOneAndTwo() {
        int row = getTable("TABLE_1_2").getSelectedRow();
        TParm parmTable = getTable("TABLE_1_2").getParmValue();
        // 病患感控记录信息
        TParm parmCase = new TParm();
        parmCase.setData("CASE_NO", getValue("CASE_NO_2"));
        parmCase.setData("INF_NO", parmTable.getData("INF_NO", row));
        parmCase.setData("INFCASE_SEQ", parmTable.getData("INFCASE_SEQ", row));
        parmCase = INFCaseTool.getInstance().caseRegisterCase(parmCase);
        int seqNum = parmCase.getCount() - 1;
        String infNo = parmCase.getCount() <= 0 ? "" : parmCase.getValue(
                "INF_NO", seqNum);
        setTable2And2ByCaseInfNo(getValueString("CASE_NO_2"), infNo);
        setTable3And2ByCaseInfNo(getValueString("CASE_NO_2"), infNo);
        setTable4And2ByCaseInfNo(getValueString("CASE_NO_2"), infNo);
        setTable5And2ByCaseInfNo(getValueString("CASE_NO_2"), infNo);//add by wanglong 20140217
        setTable6And2ByCaseInfNo(getValueString("CASE_NO_2"), infNo);//add end
        if (parmCase.getCount() <= 0)
            return;
        clearTParmNull(parmCase);
        setValue("INF_NO_2", parmCase.getData("INF_NO", seqNum));
        setValue("REGISTER_DATE_2", parmCase.getData("REGISTER_DATE", seqNum));
        setValue("INF_DR_2", parmCase.getData("INF_DR", seqNum));
        // 住院天数可更改
        setValue("ADM_DAYS_2", parmCase.getData("ADM_DAYS", seqNum));
        // 诊断信息可以修改
        setValue("IN_DIAG1_2", parmCase.getData("IN_DIAG1_DESC", seqNum));
        setValue("IN_DIAG2_2", parmCase.getData("IN_DIAG2_DESC", seqNum));
        setValue("OUT_DIAG1_2", parmCase.getData("OUT_DIAG1_DESC", seqNum));
        setValue("OUT_DIAG2_2", parmCase.getData("OUT_DIAG2_DESC", seqNum));
        setValue("OUT_DIAG3_2", parmCase.getData("OUT_DIAG3_DESC", seqNum));
        setInfDiagInf(parmCase, seqNum);
        setValue("INF_DATE_2", parmCase.getData("INF_DATE", seqNum));
//      setValue("INFPOSITION_CODE_2", parmCase.getData("INFPOSITION_CODE", seqNum));//delete by wanglong 20140217
//      setValue("INFPOSITION_DTL_2", parmCase.getData("INFPOSITION_DTL", seqNum));
//      setValue("INF_DIAG1_2", parmCase.getData("INF_DIAG1_DESC", seqNum));
//      setValue("INF_DIAG2_2", parmCase.getData("INF_DIAG2_DESC", seqNum));
        setValue("DIEINFLU_CODE_2", parmCase.getData("DIEINFLU_CODE", seqNum));
        setValue("INFRETN_CODE_2", parmCase.getData("INFRETN_CODE", seqNum));
        setValue("OP_CODE_2", parmCase.getData("OPT_CHN_DESC", seqNum));
        setValue("OP_DATE_2", parmCase.getData("OP_DATE", seqNum));
        setValue("OPCUT_TYPE_2", parmCase.getData("OPCUT_TYPE", seqNum));
        setValue("ANA_TYPE_2", parmCase.getData("ANA_TYPE", seqNum));
        setValue("ANA_TYPE_2", parmCase.getData("ANA_TYPE", seqNum));

        if (parmCase.getValue("URGTOP_FLG", seqNum).equals("Y"))
            setValue("URGTOP_FLG_A_2", "Y");
        else if (parmCase.getValue("URGTOP_FLG", seqNum).equals("N"))
            setValue("URGTOP_FLG_B_2", "Y");

        if (parmCase.getValue("OP_TIME", seqNum).length() >= 4) {
            setValue("OP_TIME_HH_2", parmCase.getValue("OP_TIME", seqNum)
                    .substring(0, 2));
            setValue("OP_TIME_MM_2", parmCase.getValue("OP_TIME", seqNum)
                    .substring(2, 4));
        }

        setValue("OP_DR_2", parmCase.getData("OP_DR", seqNum));
        setValue("INICU_DATE_2", parmCase.getData("INICU_DATE", seqNum));
        setValue("OUTICU_DATE_2", parmCase.getData("OUTICU_DATE", seqNum));

        if (parmCase.getValue("ETIOLGEXM_FLG", seqNum).equals("Y"))
            setValue("ETIOLGEXM_FLG_Y_2", "Y");
        else if (parmCase.getValue("ETIOLGEXM_FLG", seqNum).equals("N"))
            setValue("ETIOLGEXM_FLG_N_2", "Y");

        setValue("SPECIMEN_CODE_2", parmCase.getData("SPECIMEN_CODE", seqNum));
        setValue("EXAM_DATE_2", parmCase.getData("EXAM_DATE", seqNum));

        if (parmCase.getValue("LABWAY", seqNum).equals("1"))
            setValue("LABWAY_1_2", "Y");
        if (parmCase.getValue("LABWAY", seqNum).equals("2"))
            setValue("LABWAY_2_2", "Y");
        if (parmCase.getValue("LABWAY", seqNum).equals("3"))
            setValue("LABWAY_3_2", "Y");

        if (parmCase.getValue("LABPOSITIVE", seqNum).equals("Y"))
            setValue("LABPOSITIVE_A_2", "Y");
        if (parmCase.getValue("LABPOSITIVE", seqNum).equals("N"))
            setValue("LABPOSITIVE_B_2", "Y");

        setValue("PATHOGEN1_CODE_2", parmCase.getData("PATHOGEN1_CODE", seqNum));
        setValue("PATHOGEN2_CODE_2", parmCase.getData("PATHOGEN2_CODE", seqNum));
        setValue("PATHOGEN3_CODE_2", parmCase.getData("PATHOGEN3_CODE", seqNum));

        if (parmCase.getValue("ANTIBIOTEST_FLG", seqNum).equals("Y"))
            setValue("ANTIBIOTEST_FLG_Y_2", "Y");
        if (parmCase.getValue("ANTIBIOTEST_FLG", seqNum).equals("N"))
            setValue("ANTIBIOTEST_FLG_N_2", "Y");
        TParm parm = new TParm();
        parm.addData("CASE_NO", getValueString("CASE_NO_2"));
        getAntiTestInfo(parm);
    }
    
    /**
     * 接受返回值方法
     * @param tag String
     * @param obj Object
     */
    public void diagCode1(String tag, Object obj) {//add by wanglong 20131105
        TParm parm = (TParm) obj;
        String icdCode = parm.getValue("ICD_CODE");
        if (!StringUtil.isNullString(icdCode)) {
            getTextField("DIAG_CODE_1").setValue(icdCode);
        }
        String icdDesc = parm.getValue("ICD_CHN_DESC");
        if (!StringUtil.isNullString(icdDesc)) {
            getTextField("DIAG_DESC_1").setValue(icdDesc);
        }
    }

    /**
     * 接受返回值方法
     * @param tag String
     * @param obj Object
     */
    public void orderCode1(String tag, Object obj) {//add by wanglong 20131105
        TParm parm = (TParm) obj;
        String icdCode = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(icdCode)) {
            getTextField("ORDER_CODE_1").setValue(icdCode);
        }
        String icdDesc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(icdDesc)) {
            getTextField("ORDER_DESC_1").setValue(icdDesc);
        }
    }
    
    
    /**
     * 接受返回值方法
     * 
     * @param tag
     *            String
     * @param obj
     *            Object
     */
    public void inDiag1And2(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String icdCode = parm.getValue("ICD_CODE");
        if (!StringUtil.isNullString(icdCode))
            inDiag1.setData("ICD_CODE", icdCode);
        String icdDesc = parm.getValue("ICD_CHN_DESC");
        if (!StringUtil.isNullString(icdDesc)) {
            getTextField("IN_DIAG1_2").setValue(icdDesc);
            inDiag1.setData("ICD_CHN_DESC", icdDesc);
        }
    }

    /**
     * 接受返回值方法
     * 
     * @param tag
     *            String
     * @param obj
     *            Object
     */
    public void inDiag2And2(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String icdCode = parm.getValue("ICD_CODE");
        if (!StringUtil.isNullString(icdCode))
            inDiag2.setData("ICD_CODE", icdCode);
        String icdDesc = parm.getValue("ICD_CHN_DESC");
        if (!StringUtil.isNullString(icdDesc)) {
            getTextField("IN_DIAG2_2").setValue(icdDesc);
            inDiag2.setData("ICD_CHN_DESC", icdDesc);
        }
    }

    /**
     * 接受返回值方法
     * 
     * @param tag
     *            String
     * @param obj
     *            Object
     */
    public void outDiag3And2(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String icdCode = parm.getValue("ICD_CODE");
        if (!StringUtil.isNullString(icdCode))
            outDiag3.setData("ICD_CODE", icdCode);
        String icdDesc = parm.getValue("ICD_CHN_DESC");
        if (!StringUtil.isNullString(icdDesc)) {
            getTextField("OUT_DIAG3_2").setValue(icdDesc);
            outDiag3.setData("ICD_CHN_DESC", icdDesc);
        }
    }

    /**
     * 接受返回值方法
     * 
     * @param tag
     *            String
     * @param obj
     *            Object
     */
    public void outDiag2And2(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String icdCode = parm.getValue("ICD_CODE");
        if (!StringUtil.isNullString(icdCode))
            outDiag2.setData("ICD_CODE", icdCode);
        String icdDesc = parm.getValue("ICD_CHN_DESC");
        if (!StringUtil.isNullString(icdDesc)) {
            getTextField("OUT_DIAG2_2").setValue(icdDesc);
            outDiag2.setData("ICD_CHN_DESC", icdDesc);
        }
    }

    /**
     * 接受返回值方法
     * 
     * @param tag
     *            String
     * @param obj
     *            Object
     */
    public void outDiag1And2(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String icdCode = parm.getValue("ICD_CODE");
        if (!StringUtil.isNullString(icdCode))
            outDiag1.setData("ICD_CODE", icdCode);
        String icdDesc = parm.getValue("ICD_CHN_DESC");
        if (!StringUtil.isNullString(icdDesc)) {
            getTextField("OUT_DIAG1_2").setValue(icdDesc);
            outDiag1.setData("ICD_CHN_DESC", icdDesc);
        }
    }

    /**
     * 接受返回值方法
     * 
     * @param tag
     *            String
     * @param obj
     *            Object
     */
//  public void infDiag1And2(String tag, Object obj) {//delete by wanglong 20140217
//      TParm parm = (TParm) obj;
//      String icdCode = parm.getValue("ICD_CODE");
//      if (!StringUtil.isNullString(icdCode))
//          infDiag1.setData("ICD_CODE", icdCode);
//      String icdDesc = parm.getValue("ICD_CHN_DESC");
//      if (!StringUtil.isNullString(icdDesc)) {
//          getTextField("INF_DIAG1_2").setValue(icdDesc);
//          infDiag1.setData("ICD_CHN_DESC", icdDesc);
//      }
//  }

    /**
     * 接受返回值方法
     * 
     * @param tag
     *            String
     * @param obj
     *            Object
     */
//  public void infDiag2And2(String tag, Object obj) {//delete by wanglong 20140217
//      TParm parm = (TParm) obj;
//      String icdCode = parm.getValue("ICD_CODE");
//      if (!StringUtil.isNullString(icdCode))
//          infDiag2.setData("ICD_CODE", icdCode);
//      String icdDesc = parm.getValue("ICD_CHN_DESC");
//      if (!StringUtil.isNullString(icdDesc)) {
//          getTextField("INF_DIAG2_2").setValue(icdDesc);
//          infDiag2.setData("ICD_CHN_DESC", icdDesc);
//      }
//  }

    /**
     * 多页签切换事件
     */
    public void onTabPanel() {
        int index = ((TTabbedPane) getComponent("TabbedPane_1"))
                .getSelectedIndex();
        switch (index) {
        case 0:
            ((TMenuItem) getComponent("save")).setEnabled(false);
            ((TMenuItem) getComponent("delete")).setEnabled(false);
            ((TMenuItem) getComponent("query")).setEnabled(true);
            ((TMenuItem) getComponent("clear")).setEnabled(true);
            ((TMenuItem) getComponent("close")).setEnabled(true);
            ((TMenuItem) getComponent("print")).setEnabled(false);
            ((TMenuItem) getComponent("print1")).setEnabled(false);//====liling 20140402 添加
            ((TMenuItem) getComponent("report")).setEnabled(false);
            ((TMenuItem) getComponent("temperature")).setEnabled(false);
            ((TMenuItem) getComponent("showcase")).setEnabled(false);
            ((TMenuItem) getComponent("export")).setEnabled(false);
            taddedPaneNo = index;
            break;
        case 1:
            ((TMenuItem) getComponent("save")).setEnabled(true);
            ((TMenuItem) getComponent("delete")).setEnabled(true);
            ((TMenuItem) getComponent("query")).setEnabled(true);
            ((TMenuItem) getComponent("clear")).setEnabled(true);
            ((TMenuItem) getComponent("close")).setEnabled(true);
            ((TMenuItem) getComponent("print")).setEnabled(true);
            ((TMenuItem) getComponent("print1")).setEnabled(false);//====liling 20140402 添加
            ((TMenuItem) getComponent("report")).setEnabled(true);
            ((TMenuItem) getComponent("temperature")).setEnabled(true);
            ((TMenuItem) getComponent("showcase")).setEnabled(true);
            ((TMenuItem) getComponent("export")).setEnabled(false);
            onTabPanelTwo();
            taddedPaneNo = index;
            break;
        case 2:
            ((TMenuItem) getComponent("save")).setEnabled(true);
            ((TMenuItem) getComponent("delete")).setEnabled(false);
            ((TMenuItem) getComponent("query")).setEnabled(true);
            ((TMenuItem) getComponent("clear")).setEnabled(true);
            ((TMenuItem) getComponent("close")).setEnabled(true);
            ((TMenuItem) getComponent("print")).setEnabled(false);
            ((TMenuItem) getComponent("print1")).setEnabled(false);//====liling 20140402 添加
            ((TMenuItem) getComponent("report")).setEnabled(false);
            ((TMenuItem) getComponent("temperature")).setEnabled(false);
            ((TMenuItem) getComponent("showcase")).setEnabled(false);
            ((TMenuItem) getComponent("export")).setEnabled(false);
            onTabPanelThree();
            taddedPaneNo = index;
            break;
        case 3:
            ((TMenuItem) getComponent("save")).setEnabled(true);
            ((TMenuItem) getComponent("delete")).setEnabled(false);
            ((TMenuItem) getComponent("query")).setEnabled(true);
            ((TMenuItem) getComponent("clear")).setEnabled(true);
            ((TMenuItem) getComponent("close")).setEnabled(true);
            ((TMenuItem) getComponent("print")).setEnabled(false);
            ((TMenuItem) getComponent("print1")).setEnabled(true);//====liling 20140402 添加
            ((TMenuItem) getComponent("report")).setEnabled(true);
            ((TMenuItem) getComponent("temperature")).setEnabled(false);
            ((TMenuItem) getComponent("showcase")).setEnabled(false);
            ((TMenuItem) getComponent("export")).setEnabled(true);
            taddedPaneNo = index;
            break;
         case 4://add by wanglong 20131105
            ((TMenuItem) getComponent("save")).setEnabled(false);
            ((TMenuItem) getComponent("delete")).setEnabled(false);
            ((TMenuItem) getComponent("query")).setEnabled(true);
            ((TMenuItem) getComponent("clear")).setEnabled(true);
            ((TMenuItem) getComponent("close")).setEnabled(true);
            ((TMenuItem) getComponent("print")).setEnabled(false);
            ((TMenuItem) getComponent("print1")).setEnabled(false);//====liling 20140402 添加
            ((TMenuItem) getComponent("report")).setEnabled(false);
            ((TMenuItem) getComponent("temperature")).setEnabled(false);
            ((TMenuItem) getComponent("showcase")).setEnabled(false);
            ((TMenuItem) getComponent("export")).setEnabled(false);
            onTabPanelFive();
            taddedPaneNo = index;
            break;
        }
    }
    /**
     * 第二个页签事件
     */
    private void onTabPanelTwo() {
        switch (taddedPaneNo) {
        case 0:
            onClearTwo();
            int row0 = getTable("TABLE_1").getSelectedRow();
            if (row0 < 0)
                return;
            TParm parmTable0 = getTable("TABLE_1").getParmValue();
            setValue("MR_NO_2", parmTable0.getData("MR_NO", row0));
            break;
        case 3:
            onClearTwo();
            int row4 = getTable("TABLE_1_4").getSelectedRow();
            if (row4 < 0)
                return;
            TParm parmTable4 = getTable("TABLE_1_4").getParmValue();
            setValue("MR_NO_2", parmTable4.getData("MR_NO", row4));
            break;
        }
        onQueryTwo();
    }

    /**
     * 第三个页签事件
     */
    private void onTabPanelThree() {
        int row = getTable("TABLE_1").getSelectedRow();
        if (row < 0)
            return;
        TParm parmTable = getTable("TABLE_1").getParmValue();
        setValue("START_DATE_3", getValue("START_DATE_1"));
        setValue("END_DATE_3", getValue("END_DATE_1"));
        setValue("MR_NO_3", parmTable.getData("MR_NO", row));
        setValue("IPD_NO_3", parmTable.getData("IPD_NO", row));
        setValue("DEPT_3", parmTable.getData("DEPT_CODE", row));
        setValue("STATION_3", parmTable.getData("STATION_CODE", row));
        setValue("DR_3", parmTable.getData("VS_DR", row));
        setValue("PAT_NAME_3", parmTable.getData("PAT_NAME", row));
        setValue("AGE_3",
                StringTool.CountAgeByTimestamp(
                        (Timestamp) parmTable.getData("BIRTH_DATE", row),
                        SystemTool.getInstance().getDate())[0]);
        onQueryThree();
    }

    /**
     * 第五个页签事件
     */
    private void onTabPanelFive() {// add by wanglong 20131105
        int row = getTable("TABLE_1").getSelectedRow();
        if (row < 0) return;
        TParm parmTable = getTable("TABLE_1").getParmValue();
        setValue("START_DATE_5", getValue("START_DATE_1"));
        setValue("END_DATE_5", getValue("END_DATE_1"));
        setValue("MR_NO_5", parmTable.getData("MR_NO", row));
        setValue("IPD_NO_5", parmTable.getData("IPD_NO", row));
        setValue("DEPT_5", parmTable.getData("DEPT_CODE", row));
        setValue("STATION_5", parmTable.getData("STATION_CODE", row));
        setValue("DR_5", parmTable.getData("VS_DR", row));
        setValue("PAT_NAME_5", parmTable.getData("PAT_NAME", row));
        setValue("AGE_5", StringTool.CountAgeByTimestamp((Timestamp) parmTable
                .getData("BIRTH_DATE", row), SystemTool.getInstance().getDate())[0]);
        onQueryFive();
    }

    
    /**
     * 删除动作
     */
    public void onDelete() {
        int index = ((TTabbedPane) getComponent("TabbedPane_1"))
                .getSelectedIndex();
        switch (index) {
        case 1:
            onDeleteTwo();
            break;

        }
    }

    /**
     * 第二个页签删除动作
     */
    private void onDeleteTwo() {
        if (getTable("TABLE_1_2").getSelectedRow() < 0) {
            messageBox("无删除记录");
            return;
        }
        TParm parm = new TParm();
        parm.setData("INF_NO", getValue("INF_NO_2"));
        parm.setData("CASE_NO", getValue("CASE_NO_2"));
        parm.setData(
                "INFCASE_SEQ",
                getTable("TABLE_1_2").getValueAt(
                        getTable("TABLE_1_2").getSelectedRow(), 1));
        parm = TIOM_AppServer.executeAction("action.inf.InfAction",
                "deleteInfCase", parm);
        if (parm.getErrCode() < 0) {
            messageBox("保存失败");
            return;
        }
        messageBox("保存成功");
        String mrNo = getValueString("MR_NO_2");
        onClearTwo();
        setValue("MR_NO_2", mrNo);
        onQueryTwo();
    }

    /**
     * 清空方法
     */
    public void onClear() {
        int index = ((TTabbedPane) getComponent("TabbedPane_1"))
                .getSelectedIndex();
        switch (index) {
        case 0:
            onClearOne();
            break;
        case 1:
            onClearTwo();
            break;
        case 2:
            onClearThree();
            break;
        case 3:
            onClearFour();
            break;
        case 4://add by wanglong 20131105
            onClearFive();
            break;
        }
    }

    /**
     * 第一个页签清空方法
     */
    private void onClearOne() {
        onInitPanelOne();
        setValue("START_TEMP_1", "");
        setValue("END_TEMP_1", "");
        setValue("ANTIBIOTRCD_FLG_1", "N");
        setValue("ANTIBIOTIC_CODE_1", "");
        ((TComboBox) getComponent("ANTIBIOTIC_CODE_1")).setEnabled(false);
        setValue("WBC_FLG_1", "N");
        //=====add by wanglong 20131105
        callFunction("UI|WBC_NUM|setEnabled", false);
        setValue("WBC_NUM", 0);
        setValue("SUSDIAG_FLG", "N");
        callFunction("UI|DIAG_CODE_1|setEnabled", false);
        setValue("DIAG_CODE_1", "");
        setValue("DIAG_DESC_1", "");
        setValue("INVOPT_FLG", "N");
        callFunction("UI|ORDER_CODE_1|setEnabled", false);
        setValue("ORDER_CODE_1", "");
        setValue("ORDER_DESC_1", "");
        //=====add end
        getTable("TABLE_1").removeRowAll();
    }

    /**
     * 第二个页签清空方法
     */
    private void onClearTwo() {
        setValue("INF_NO_2", "");
        setValue("REGISTER_DATE_2", "");
        setValue("INF_DR_2", "");
        setValue("MR_NO_2", "");
        setValue("IPD_NO_2", "");
        setValue("CASE_NO_2", "");
        setValue("DEPT_CODE_2", "");
        setValue("PAT_NAME_2", "");
        setValue("AGE_2", "");
        setValue("SEX_2", "");
        setValue("ADM_DAYS_2", "");
        setValue("IN_DATE_2", "");
        setValue("IN_DIAG1_2", "");
        setValue("IN_DIAG2_2", "");
        setValue("CHARGE_FEE_2", "");
        setValue("OUT_DATE_2", "");
        setValue("OUT_DIAG1_2", "");
        setValue("OUT_DIAG2_2", "");
        setValue("OUT_DIAG3_2", "");
        setValue("INF_DATE_2", "");
//      setValue("INFPOSITION_CODE_2", "");//delete by wanglong 20140217
//      setValue("INFPOSITION_DTL_2", "");
//      setValue("INF_DIAG1_2", "");
//      setValue("INF_DIAG2_2", "");
        setValue("DIEINFLU_CODE_2", "");
        setValue("INFRETN_CODE_2", "");
        setValue("OP_CODE_2", "");
        setValue("OP_DATE_2", "");
        setValue("OPCUT_TYPE_2", "");
        setValue("ANA_TYPE_2", "");
        setValue("URGTOP_FLG_A_2", "N");
        setValue("URGTOP_FLG_B_2", "Y");
        setValue("OP_TIME_HH_2", "");
        setValue("OP_TIME_MM_2", "");
        setValue("OP_DR_2", "");
        setValue("INICU_DATE_2", "");
        setValue("OUTICU_DATE_2", "");
        setValue("ETIOLGEXM_FLG_Y_2", "N");
        setValue("ETIOLGEXM_FLG_N_2", "Y");
        setValue("SPECIMEN_CODE_2", "");
        setValue("EXAM_DATE_2", "");
        setValue("LABWAY_1_2", "N");
        setValue("LABWAY_2_2", "N");
        setValue("LABWAY_3_2", "N");
        setValue("LABPOSITIVE_A_2", "N");
        setValue("LABPOSITIVE_B_2", "Y");
        setValue("PATHOGEN1_CODE_2", "");
        setValue("PATHOGEN2_CODE_2", "");
        setValue("PATHOGEN3_CODE_2", "");
        setValue("ANTIBIOTEST_FLG_Y_2", "N");
        setValue("ANTIBIOTEST_FLG_N_2", "Y");
        setValue("CLINICAL_SYMP_2", "");
        getTable("TABLE_1_2").removeRowAll();
        getTable("TABLE_2_2").removeRowAll();
        getTable("TABLE_3_2").removeRowAll();
        getTable("TABLE_4_2").removeRowAll();
        getTable("TABLE_5_2").setDSValue();//add by wanglong 20140217
        getTable("TABLE_6_2").setDSValue();//add end
        ((TButton) getComponent("ADD_BUTTON_2")).setEnabled(false);
        ((TTextField) getComponent("MR_NO_2")).setEnabled(true);
        // 入院诊断1
        inDiag1 = new TParm();
        // 入院诊断2
        inDiag2 = new TParm();
        // 出院诊断1
        outDiag1 = new TParm();
        // 出院诊断2
        outDiag2 = new TParm();
        // 出院诊断3
        outDiag3 = new TParm();
        // 感控诊断2
//      infDiag1 = new TParm();//delete by wanglong 20140217
        // 感控诊断3
//      infDiag2 = new TParm();
    }

    /**
     * 第三个页签清空事件
     */
    private void onClearThree() {
        Timestamp timestamp = SystemTool.getInstance().getDate();
        ((TTextFormat) getComponent("START_DATE_3")).setValue(timestamp);
        ((TTextFormat) getComponent("END_DATE_3")).setValue(timestamp);
        setValue("DEPT_3", "");
        setValue("STATION_3", "");
        setValue("DR_3", "");
        setValue("IPD_NO_3", "");
        setValue("MR_NO_3", "");
        setValue("PAT_NAME_3", "");
        setValue("AGE_3", "");
        setValue("ANTIBIOTIC_CODE_3", "");
        getTable("TABLE_1_3").removeRowAll();
    }

    /**
     * 第四个页签清空动作
     */
    private void onClearFour() {
        setValue("REP_ALL_4", "Y");
        setValue("REP_Y_4", "N");
        setValue("REP_N_4", "N");
        setValue("IN_ALL_4", "N");
        setValue("IN_Y_4", "N");
        setValue("IN_N_4", "N");
        Timestamp timestamp = SystemTool.getInstance().getDate();
        ((TTextFormat) getComponent("START_DATE_4")).setValue(timestamp);
        ((TTextFormat) getComponent("END_DATE_4")).setValue(timestamp);
        setValue("DEPT_4", "");
        setValue("STATION_4", "");
        setValue("IPD_NO_4", "");
        setValue("MR_NO_4", "");
        setValue("PAT_NAME_4", "");
        setValue("REPORT_DATE_4", "");
        setValue("REPORT_NO_4", "");
        getTable("TABLE_1_4").removeRowAll();
    }
    
    /**
     * 第五个页签清空事件
     */
    private void onClearFive() {// add by wanglong 20131105
        Timestamp timestamp = SystemTool.getInstance().getDate();
        ((TTextFormat) getComponent("START_DATE_5")).setValue(timestamp);
        ((TTextFormat) getComponent("END_DATE_5")).setValue(timestamp);
        setValue("DEPT_5", "");
        setValue("STATION_5", "");
        setValue("DR_5", "");
        setValue("IPD_NO_5", "");
        setValue("MR_NO_5", "");
        setValue("PAT_NAME_5", "");
        setValue("AGE_5", "");
        getTable("TABLE_1_5").removeRowAll();
    }

    /**
     * 医院感染病例报告卡
     */
    public void onPrint() {
        int index = ((TTabbedPane) getComponent("TabbedPane_1"))
                .getSelectedIndex();
        switch (index) {
        case 1:
            onPrintInfCaseCard();
            break;
        }
    }

    /**
     * 打印感染病历报告卡
     */
    private void onPrintInfCaseCard() {
        if (getTable("TABLE_1_2").getSelectedRow() < 0) return;
        TParm printParm = new TParm();
        printParm.setData("HOSP_DESC", Operator.getHospitalCHNShortName());
        printParm.setData("USER_DEPT",
                          StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC",
                                             "DEPT_CODE='" + Operator.getDept() + "'"));
        printParm.setData("FILE_NO", "TEXT", this.getValue("INF_NO_2"));
        printParm.setData("INF_NO", this.getValue("INF_NO_2"));
        printParm.setData("MR_NO", "TEXT", this.getValue("MR_NO_2"));
        TParm sysPatInfo = PatTool.getInstance().getInfoForMrno(this.getValueString("MR_NO_2"));
        printParm.setData("PAT_NAME", "TEXT", this.getValue("PAT_NAME_2"));
        printParm.setData("SEX_CODE", this.getText("SEX_2"));
        printParm.setData("BIRTH_DATE", sysPatInfo.getTimestamp("BIRTH_DATE", 0));
        String[] ageStr =
                StringTool.CountAgeByTimestamp(printParm.getTimestamp("BIRTH_DATE"), SystemTool
                        .getInstance().getDate());
        printParm.setData("AGE", "TEXT", ageStr[2] + "天" + ageStr[1] + "月" + ageStr[0] + "岁");
        printParm.setData("DEPT_CODE",
                          "TEXT",
                          StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC",
                                             "DEPT_CODE='" + this.getValue("DEPT_CODE_2") + "'"));
        printParm.setData("IN_DATE", StringTool.getString((Timestamp) this.getValue("IN_DATE_2"),
                                                          "yyyy/MM/dd"));
        printParm.setData("OUT_DATE", StringTool.getString((Timestamp) this.getValue("OUT_DATE_2"),
                                                           "yyyy/MM/dd"));
        printParm.setData("IN_DIAG", this.getValue("IN_DIAG1_2"));
        printParm.setData("OUT_DIAG", this.getValue("OUT_DIAG1_2"));
        printParm.setData("INF_DATE", StringTool.getString((Timestamp) this.getValue("INF_DATE_2"),
                                                           "yyyy/MM/dd"));
        printParm.setData("REAL_STAY_DAYS", this.getValue("ADM_DAYS_2"));
        printParm.setData("INFRETN_CODE", this.getValue("INFRETN_CODE_2"));// 愈合
        printParm.setData("DIEINFLU_CODE", this.getValue("DIEINFLU_CODE_2"));// 对死亡影响
        TParm infICDPart = getTable("TABLE_5_2").getParmValue();
        TParm icdPartParm = new TParm();
        for (int i = 0; i < infICDPart.getCount(); i++) {
            if (!StringUtil.isNullString(infICDPart.getValue("PART_CODE", i))
                    && !StringUtil.isNullString(infICDPart.getValue("ICD_DESC", i))) {
                icdPartParm.addData(infICDPart.getValue("PART_CODE", i),
                                    infICDPart.getValue("ICD_DESC", i));
                icdPartParm.addData(infICDPart.getValue("PART_CODE", i),
                                    infICDPart.getValue("UM_DESC", i));
            }
        }
        printParm.setData("INF_ICDPART", icdPartParm.getData());
        TParm infIO = getTable("TABLE_6_2").getParmValue();
        TParm ioParm = new TParm();
        for (int i = 0; i < infIO.getCount(); i++) {
            if (infIO.getValue("SEL_FLG", i).equals("Y")) {
                if (!infIO.getValue("IO_NOTE", i).equals("")) {
                    ioParm.setData(infIO.getValue("IO_CODE", i), infIO.getValue("IO_NOTE", i));
                } else {
                    ioParm.setData(infIO.getValue("IO_CODE", i), "");
                }
                // ioParm.setData(infIO.getValue("IO_CODE", i), "Y");
            }
        }
        printParm.setData("INF_IO", ioParm.getData());
        TParm infReason = getTable("TABLE_2_2").getParmValue();
        TParm reasonParm = new TParm();
        for (int i = 0; i < infReason.getCount(); i++) {
            if (infReason.getValue("SEL_FLG", i).equals("Y")) {
                if (!infReason.getValue("INFREASON_NOTE", i).equals("")) {
                    reasonParm.setData(infReason.getValue("ID", i),
                                       infReason.getValue("INFREASON_NOTE", i));
                } else {
                    reasonParm.setData(infReason.getValue("ID", i), "");
                }
            }
        }
        printParm.setData("INF_REASON", reasonParm.getData());
        String sql =
                "SELECT A.ORDER_DESC, B.ROUTE_CHN_DESC ROUTE_DESC,"
                        + "       TO_CHAR(A.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') EFF_DATE,"
                        + "       TO_CHAR(A.DC_DATE, 'YYYY/MM/DD HH24:MI:SS') DC_DATE,"
                        + "       CASE WHEN A.ANTIBIOTIC_WAY = '01' THEN '是' ELSE '' END CURE,"
                        + "       CASE WHEN A.ANTIBIOTIC_WAY = '02' THEN '是' ELSE '' END PRE "
                        + "  FROM ODI_ORDER A, SYS_PHAROUTE B "
                        + " WHERE A.ROUTE_CODE = B.ROUTE_CODE "
                        + "   AND A.ANTIBIOTIC_CODE IS NOT NULL     "
                        + "   AND CASE_NO = '#'          ";
        sql = sql.replaceFirst("#", this.getValueString("CASE_NO_2"));
        TParm orderParm = new TParm(TJDODBTool.getInstance().select(sql));
        // add by wangb 2016/3/22 没上抗菌药系统，留出空白手动填写 START
        String sqlAnti = "SELECT * FROM PHA_ANTI";
        TParm antiResult = new TParm(TJDODBTool.getInstance().select(sqlAnti));
        if (antiResult.getCount() < 1) {
        	orderParm.setCount(3);
        }
        // add by wangb 2016/3/22 没上抗菌药系统，留出空白手动填写 END
        orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        orderParm.addData("SYSTEM", "COLUMNS", "ROUTE_DESC");
        orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE");
        orderParm.addData("SYSTEM", "COLUMNS", "DC_DATE");
        orderParm.addData("SYSTEM", "COLUMNS", "CURE");
        orderParm.addData("SYSTEM", "COLUMNS", "PRE");
        printParm.setData("TABLE", orderParm.getData());
        printParm.setData("SPECIMEN_CODE", this.getValue("SPECIMEN_CODE_2"));// 送检标本
        TParm culture = getTable("TABLE_4_2").getShowParmValue();
        String cultureStr = "";
        for (int i = 0; i < culture.getCount(); i++) {
            cultureStr += culture.getValue("CULTURE_CODE", i) + ";";
        }
        printParm.setData("CULTURE_CODE", "TEXT", cultureStr);
        printParm.setData("EXAM_DATE", StringTool.getString((Timestamp) this
                .getValue("EXAM_DATE_2"), "yyyy/MM/dd"));// 送检日期
        printParm.setData("USER_ID", Operator.getName());// 填表人
        printParm.setData("REPORT_DATE",
                          StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd"));// 填表日期
       // System.out.println("-------------打印报表-------------" + printParm);
        openPrintDialog("%ROOT%\\config\\prt\\INF\\INFCaseReport.jhw", printParm, false);
    }

    /**
     * 拷贝TParm
     * 
     * @param from
     *            TParm
     * @param to
     *            TParm
     * @param row
     *            int
     */
    private void cloneTParm(TParm from, TParm to, int row) {
        String names[] = from.getNames();
        for (int i = 0; i < names.length; i++) {
            Object obj = from.getData(names[i], row);
            if (obj == null)
                obj = "";
            to.setData(names[i], obj);
        }
    }

    /**
     * 检验报告
     */
    public void onReport() {
        String mrNo = "";
        int index = ((TTabbedPane) getComponent("TabbedPane_1"))
                .getSelectedIndex();
        switch (index) {
        case 1:
            if (getValueString("MR_NO_2").length() == 0)
                return;
            else
                mrNo = getValueString("MR_NO_2");
            SystemTool.getInstance().OpenLisWeb(mrNo);
            break;
        case 3:
            if (getValueString("MR_NO_4").length() == 0)
                return;
            else
                mrNo = getValueString("MR_NO_4");
            SystemTool.getInstance().OpenLisWeb(mrNo);
            break;
        }
    }

    /**
     * 检查报告
     */
    public void onTestrep() {
        String mrNo = "";
        int index = ((TTabbedPane) getComponent("TabbedPane_1"))
                .getSelectedIndex();
        switch (index) {
        case 1:
            if (getValueString("MR_NO_2").length() == 0)
                return;
            else
                mrNo = getValueString("MR_NO_2");
            SystemTool.getInstance().OpenRisWeb(mrNo);
            break;
        case 3:
            if (getValueString("MR_NO_4").length() == 0)
                return;
            else
                mrNo = getValueString("MR_NO_4");
            SystemTool.getInstance().OpenRisWeb(mrNo);
            break;
        }
    }

    /**
     * 体温单
     */
    public void onTemperature() {
        int index = ((TTabbedPane) getComponent("TabbedPane_1"))
                .getSelectedIndex();
        switch (index) {
        case 1:
            TParm parm = new TParm();
            parm.setData("SUM", "CASE_NO", getValue("CASE_NO_2"));
            parm.setData("SUM", "ADM_TYPE", "I");
            openDialog("%ROOT%\\config\\sum\\SUMVitalSign.x", parm);
            break;
        }
    }

    /**
     * 病历浏览
     */
    public void onShowCase() {
        int index = ((TTabbedPane) getComponent("TabbedPane_1"))
                .getSelectedIndex();
        switch (index) {
        case 1:
            TParm parm = new TParm();
            parm.setData("SYSTEM_TYPE", "ODI");
            parm.setData("ADM_TYPE", "I");
            parm.setData("CASE_NO", getValue("CASE_NO_2"));
            parm.setData("PAT_NAME", getValue("PAT_NAME_2"));
            parm.setData("MR_NO", getValue("MR_NO_2"));
            parm.setData("IPD_NO", getValue("IPD_NO_2"));
            parm.setData("ADM_DATE", getValue("IN_DATE_2"));
            parm.setData("DEPT_CODE", getValue("DEPT_CODE_2"));
            parm.setData("RULETYPE", "1");
            parm.setData("EMR_DATA_LIST", new TParm());
            parm.addListener("EMR_LISTENER", this, "emrListener");
            parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
            openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
            break;
        }
    }

    /**
     * EMR监听
     * 
     * @param parm
     *            TParm
     */
    public void emrListener(TParm parm) {
        parm.runListener("setMicroData", "S", "A");
    }

    /**
     * EMR保存监听
     * 
     * @param parm
     *            TParm
     */
    public void emrSaveListener(TParm parm) {
    }

    /**
     * 病原学检查是
     */
    public void onEtiolgexmFlgYTwo() {
        ((TTextFormat) getComponent("SPECIMEN_CODE_2")).setEnabled(true);
        ((TTextFormat) getComponent("EXAM_DATE_2")).setEnabled(true);
        ((TCheckBox) getComponent("LABWAY_1_2")).setEnabled(true);
        ((TCheckBox) getComponent("LABWAY_2_2")).setEnabled(true);
        ((TCheckBox) getComponent("LABWAY_3_2")).setEnabled(true);
        ((TRadioButton) getComponent("LABPOSITIVE_A_2")).setEnabled(true);
        ((TRadioButton) getComponent("LABPOSITIVE_B_2")).setEnabled(true);
    }

    /**
     * 病原学检查否
     */
    public void onEtiolgexmFlgNTwo() {
        setValue("SPECIMEN_CODE_2", "");
        ((TTextFormat) getComponent("SPECIMEN_CODE_2")).setEnabled(false);
        setValue("EXAM_DATE_2", "");
        ((TTextFormat) getComponent("EXAM_DATE_2")).setEnabled(false);
        setValue("LABWAY_1_2", "N");
        ((TCheckBox) getComponent("LABWAY_1_2")).setEnabled(false);
        setValue("LABWAY_2_2", "N");
        ((TCheckBox) getComponent("LABWAY_2_2")).setEnabled(false);
        setValue("LABWAY_3_2", "N");
        ((TCheckBox) getComponent("LABWAY_3_2")).setEnabled(false);
        setValue("LABPOSITIVE_A_2", "N");
        ((TRadioButton) getComponent("LABPOSITIVE_A_2")).setEnabled(false);
        setValue("LABPOSITIVE_B_2", "N");
        ((TRadioButton) getComponent("LABPOSITIVE_B_2")).setEnabled(false);
    }

    /**
     * 抗菌药物敏感试验是
     */
    public void onAntibioTestFlgY() {
        ((TButton) getComponent("EXPORT_BUTTON")).setEnabled(true);
    }

    /**
     * 抗菌药物敏感试验否
     */
    public void onAntibioTestFlgN() {
        ((TButton) getComponent("EXPORT_BUTTON")).setEnabled(false);
    }

    /**
     * 第三个页签鼠标单击事件
     */
    public void onTabke1And3() {
        TParm parm = getTable("TABLE_1_3").getParmValue();
        int row = getTable("TABLE_1_3").getSelectedRow();
        setValue("MR_NO_3", parm.getData("MR_NO", row));
        setValue("IPD_NO_3", parm.getData("IPD_NO", row));
        setValue("DEPT_3", parm.getData("DEPT_CODE", row));
        setValue("STATION_3", parm.getData("STATION_CODE", row));
        setValue("DR_3", parm.getData("VS_DR_CODE", row));
        setValue("PAT_NAME_3", parm.getData("PAT_NAME", row));
        setValue(
                "AGE_3",
                StringTool.CountAgeByTimestamp((Timestamp) parm.getData(
                        "BIRTH_DATE", row), SystemTool.getInstance().getDate())[0]);
        setValue("ANTIBIOTIC_CODE_3", parm.getData("ANTIBIOTIC_CODE", row));
    }

    /**
     * 第五个页签鼠标单击事件
     */
    public void onTabke1And5() {// add by wanglong 20131105
        TParm parm = getTable("TABLE_1_5").getParmValue();
        int row = getTable("TABLE_1_5").getSelectedRow();
        setValue("MR_NO_5", parm.getData("MR_NO", row));
        setValue("IPD_NO_5", parm.getData("IPD_NO", row));
        setValue("DEPT_5", parm.getData("DEPT_CODE", row));
        setValue("STATION_5", parm.getData("STATION_CODE", row));
        setValue("DR_5", parm.getData("VS_DR_CODE", row));
        setValue("PAT_NAME_5", parm.getData("PAT_NAME", row));
        setValue("AGE_5", StringTool.CountAgeByTimestamp((Timestamp) parm
                .getData("BIRTH_DATE", row), SystemTool.getInstance().getDate())[0]);
    }
    
    public static void main(String[] args) {
    }

    /**
     * 病案号查询
     */
    public void onMrNo(String tag) {//add by wanglong 20131105
        String IPD_NO = "";
        String PAT_NAME = "";
        String AGE = "";
        if (tag.equals("MR_NO_3")) {
            IPD_NO = "IPD_NO_3";
            PAT_NAME = "PAT_NAME_3";
            AGE = "AGE_3";
        } else if (tag.equals("MR_NO_5")) {
            IPD_NO = "IPD_NO_5";
            PAT_NAME = "PAT_NAME_5";
            AGE = "AGE_5";
        }
        String mrNo = this.getValueString(tag).trim();
        Pat pat = Pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            this.messageBox("E0008");//查无资料
            this.setValue(tag, "");
            this.setValue(IPD_NO, "");
            this.setValue(PAT_NAME, "");
            this.setValue(AGE, "");
            return;
        }
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue(tag, pat.getMrNo());
        this.setValue(IPD_NO, pat.getIpdNo());
        this.setValue(PAT_NAME, pat.getName());
        this.setValue(AGE, StringUtil.showAge(pat.getBirthday(), date));
    }
    
    /**
     * 将上一个SQL查出的字段信息生成 IN()语句，为下一个SQL服务
     * @param sqlName
     * @param Nos
     * @return
     */
    public static String getInStatement(TParm Nos, String columnName, String sqlName) {//add by wanglong 20131105
        if (Nos.getCount(columnName) < 1) {
            return " 1=1 ";
        }
        StringBuffer inStr = new StringBuffer();
        inStr.append(sqlName + " IN ('");
        for (int i = 0; i < Nos.getCount(columnName) ; i++) {
            inStr.append(Nos.getValue(columnName, i));
            if ((i + 1) != Nos.getCount(columnName)) {
                if ((i + 1) % 999 != 0) {
                    inStr.append("','");
                } else if (((i + 1) % 999 == 0)) {
                    inStr.append("') OR " + sqlName + " IN ('");
                }
            }
        }
        inStr.append("')");
        return inStr.toString();
    }
    
    /**
     * CHECK_BOX勾选事件
     * @param obj
     * @return
     */
    public boolean onCheckBoxClicked(Object obj) {// add by wanglong 20140217
        TTable table = (TTable) obj;
        table.acceptText();
        return false;
    }
        
    /**
     * 单元格值改变事件
     * @param node
     * @return
     */
    public boolean onTableChangeValue(TTableNode node) {// add by wanglong 20140217
        if (node == null) return false;
        if (node.getValue().equals(node.getOldValue())) return true;
        int row = node.getRow();
        String colName = node.getTable().getParmMap(node.getColumn());
        if (node.getTable() == getTable("TABLE_5_2")) {
            TParm parmValue = getTable("TABLE_5_2").getParmValue();
            if (colName.equals("MAIN_FLG")) {
                if (node.getValue().equals("Y")) {
                    if (StringUtil.isNullString(getTable("TABLE_5_2").getItemString(row, "PART_CODE"))
                            || StringUtil.isNullString(getTable("TABLE_5_2").getItemString(row, "ICD_DESC"))) {
                        this.messageBox("主记录的感染部位及诊断两项不能为空");
                        return true;
                    }
                    for (int i = 0; i < parmValue.getCount(); i++) {
                        if (i != row) {
                            parmValue.setData(colName, i, "N");
                        }
                    }
                }
            } else if (colName.equals("PART_CODE")) {
                int maxRow = parmValue.getCount("MAIN_FLG");
                if (!StringUtil.isNullString(node.getValue() + "")
                        && !StringUtil.isNullString(parmValue.getValue("ICD_DESC", maxRow - 1))
                // || !StringUtil.isNullString(parmValue.getValue("UM_DESC", maxRow - 1))
                ) {
                    parmValue.addData("MAIN_FLG", "N");
                    parmValue.addData("PART_CODE", "");
                    parmValue.addData("ICD_CODE", "");
                    parmValue.addData("ICD_DESC", "");
                    parmValue.addData("UM_CODE", "");
                    parmValue.addData("UM_DESC", "");
                    parmValue.addData("SEQ", parmValue.getInt("SEQ", row) + 1);
                    parmValue.setCount(maxRow + 1);
                }
            }
            getTable("TABLE_5_2").setParmValue(parmValue);
        } 
        return false;
    }

    /**
     * 诊断弹出界面 ICD10
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComponent(Component com, int row, int column) {// add by wanglong 20140217
        column = getTable("TABLE_5_2").getColumnModel().getColumnIndex(column);
        String colName = getTable("TABLE_5_2").getParmMap(column);
        if (!"ICD_DESC".equalsIgnoreCase(colName)&&!"UM_DESC".equalsIgnoreCase(colName)) {
            return;
        }
        if (!(com instanceof TTextField)) return;
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        // 给table上的新text增加ICD10弹出窗口
        textfield.setPopupMenuParameter(colName,
                                        getConfigParm()
                                                .newConfig("%ROOT%\\config\\sys\\SYSICDPopup.x"));
        // 给新text增加接受ICD10弹出窗口的回传值
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popICDReturn");
    }

    /**
     * 取得ICD10返回值
     * @param tag String
     * @param obj Object
     */
    public void popICDReturn(String tag, Object obj) {// add by wanglong 20140217
        TTable table = getTable("TABLE_5_2");
        TParm parm = (TParm) obj;
        String orderCode = parm.getValue("ICD_CODE");
        String orderDesc = parm.getValue("ICD_CHN_DESC");
        int row=table.getSelectedRow();
        table.setItem(row, tag, orderDesc);
        TParm parmValue = table.getParmValue();
        if (tag.equals("ICD_DESC")) {
            parmValue.setData("ICD_CODE", row, orderCode);
            parmValue.setData("ICD_DESC", row, orderDesc);
        } else if (tag.equals("UM_DESC")) {
            parmValue.setData("UM_CODE", row, orderCode);
            parmValue.setData("UM_DESC", row, orderDesc);
        }
        int maxRow = parmValue.getCount("MAIN_FLG");
        if (!StringUtil.isNullString(parmValue.getValue("PART_CODE", maxRow - 1))
                && !StringUtil.isNullString(parmValue.getValue("ICD_DESC", maxRow - 1))) {
            parmValue.addData("MAIN_FLG", "N");
            parmValue.addData("PART_CODE", "");
            parmValue.addData("ICD_CODE", "");
            parmValue.addData("ICD_DESC", "");
            parmValue.addData("UM_CODE", "");
            parmValue.addData("UM_DESC", "");
            parmValue.addData("SEQ", parmValue.getInt("SEQ", row) + 1);
            parmValue.setCount(maxRow + 1);
        }
        table.setParmValue(parmValue);
    }

    /**add caoyong 20140313
     * 汇出Excel
     */
    public void onExport() {
        if(getTable("TABLE_1_4").getRowCount()<=0){
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(getTable("TABLE_1_4"), "医院感染登记表");
    }

}
