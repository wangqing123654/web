package com.javahis.ui.aci;

import java.sql.Timestamp;
import jdo.aci.ACIADRTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;


/**
 * <p> Title: 药品不良事件查询 </p>
 *
 * <p> Description: 药品不良事件查询 </p>
 *
 * <p> Copyright: Copyright (c) 2013 </p>
 *
 * <p> Company: BlueCore </p>
 *
 * @author  WangLong 2013.09.30
 * @version 1.0
 */
public class ACIADRQueryControl extends TControl {
	
    TTextFormat reportDept;// 上报科室
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            TParm parm = (TParm) obj;
            if (parm.getValue("POPEDEM").length() != 0) {
                if ("1".equals(parm.getValue("POPEDEM"))) { // 一般权限
                    this.setPopedem("NORMAL", true);
                    this.setPopedem("SYSOPERATOR", false);
                }
                if ("2".equals(parm.getValue("POPEDEM"))) { // 角色权限
                    this.setPopedem("NORMAL", false);
                    this.setPopedem("SYSOPERATOR", true);
                }
            }
        }
        reportDept = (TTextFormat) this.getComponent("REPORT_DEPT");// 上报科室
        callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        initUI();
    }

    /**
     * 初始化界面信息
     */
    public void initUI() {
        Timestamp oneWeekAgo = StringTool.rollDate(SystemTool.getInstance().getDate(), -7);
        Timestamp today = SystemTool.getInstance().getDate();
        this.setValue("START_DATE", oneWeekAgo);
        this.setValue("END_DATE", today);
        this.setValue("UPLOAD_DATE", today);// 审核日期
        this.clearValue("REPORT_DEPT");// 去掉，上报科室会有默认值，灵异事件！解决方法：初始化时就清空或者赋空值，或者将tag改名
        this.callFunction("UI|report|setEnabled", false);
        this.callFunction("UI|report|setVisible", false);
        this.callFunction("UI|unReport|setVisible", false);
        this.callFunction("UI|UPLOAD_DATE|setEnabled", false);
        this.callFunction("UI|UPLOAD_FLG|setEnabled", false);
        if (this.getPopedem("NORMAL")) {
            String deptSql =
                    "SELECT A.DEPT_CODE AS ID, A.DEPT_ABS_DESC AS NAME, A.PY1 FROM SYS_DEPT A, SYS_OPERATOR_DEPT B "
                            + " WHERE A.DEPT_CODE = B.DEPT_CODE AND B.USER_ID = '#' AND A.ACTIVE_FLG = 'Y' ORDER BY A.DEPT_CODE, A.SEQ";
            deptSql = deptSql.replaceFirst("#", Operator.getID());
            reportDept.setPopupMenuSQL(deptSql);// 普通用户限定只能查询自己所在科室的记录
            reportDept.setHisOneNullRow(false);// 普通用户不能进行全部查询
            reportDept.onQuery();
            String deptCode =
                    StringUtil.getDesc("SYS_OPERATOR", "COST_CENTER_CODE",
                                       "USER_ID='" + Operator.getID() + "'");
            reportDept.setValue(deptCode);
        }
        if (this.getPopedem("SYSOPERATOR")) {
            this.callFunction("UI|report|setVisible", true);
            this.callFunction("UI|UPLOAD_FLG|setEnabled", true);
        }
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = new TParm();
        Timestamp startDate = (Timestamp) this.getValue("START_DATE");
        Timestamp endDate = (Timestamp) this.getValue("END_DATE");
        endDate = new Timestamp(endDate.getTime() + 24 * 60 * 60 * 1000 - 1);
        parm.setData("START_DATE", startDate);
        parm.setData("END_DATE", endDate);
        if (this.getValue("UNREPORTED").equals("Y")) {
            parm.setData("UNREPORTED", "Y");// 未上报
        }
        if (this.getValue("REPORTED").equals("Y")) {
            parm.setData("REPORTED", "Y");// 已上报
        }
        if (!this.getValueString("REPORT_DEPT").equals("")) {
            parm.setData("REPORT_DEPT", this.getValue("REPORT_DEPT"));// 上报科室
        }
        if (!this.getValueString("REPORT_TYPE").equals("")) {
            parm.setData("REPORT_TYPE", this.getValue("REPORT_TYPE"));// 报告类型
        }
        if (!this.getValueString("ADR_ID").equals("")) {
            parm.setData("ADR_ID", this.getValue("ADR_ID"));// 不良事件名称
        }
        if (!this.getValueString("PHA_RULE").equals("")) {
            parm.setData("PHA_RULE", this.getValue("PHA_RULE"));// 药品分类
        }
        TParm result = ACIADRTool.getInstance().queryADRData(parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + "" + result.getErrText());
        }
        clearValue("COUNT");// 结果数量
        this.callFunction("UI|TABLE|setParmValue", new TParm());
        if (result.getCount() <= 0) {
            messageBox("E0008");// 查无资料
            return;
        }
        ((TTextField) getComponent("COUNT")).setValue(result.getCount() + "");// 结果数量
        this.callFunction("UI|TABLE|setParmValue", result);
    }

    /**
     * 删除
     */
    public void onDelete() {
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            messageBox("请选择一条记录");
            return;
        }
        TParm data = table.getParmValue();
        if (!this.getPopedem("SYSOPERATOR")) {
            if (!Operator.getID().equals(data.getValue("REPORT_USER", selRow))) {
                this.messageBox("需高级权限或者上报人自己才能删除记录");
                return;
            }
        }
        boolean uploadFlg = data.getBoolean("UPLOAD_FLG", selRow);
        if (uploadFlg == true) {
            messageBox("不能删除已完成上报的记录");
            return;
        }
        if (this.messageBox("询问", "是否删除", 2) == 0) {
            TParm parm = new TParm();
            parm.setData("ACI_NO", data.getValue("ACI_NO", selRow));
            TParm result =
                    TIOM_AppServer.executeAction("action.aci.ACIADRAction", "onDelete", parm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            this.callFunction("UI|TABLE|removeRow", selRow);
            this.clearValue("REPORT_DEPT;REPORT_TYPE;ADR_ID;PHA_RULE");
            this.setValue("COUNT", StringTool.addString(this.getValueString("COUNT")));
            table.clearSelection();
            this.messageBox("P0003");// 删除成功
        }
    }
    
    /**
     * 已上报
     */
    public void onReport(){
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            messageBox("请选择一条记录");
            return;
        }
        TParm data = table.getParmValue();
        if (!checkUIData(data.getRow(selRow))) {
            return;
        }
        String aciNo = data.getValue("ACI_NO", selRow);
        TParm parm = getParmForTag("UPLOAD_FLG;UPLOAD_DATE:Timestamp");
        if (!parm.getValue("UPLOAD_FLG").equals("Y")) {
            parm.setData("UPLOAD_DATE", "");
        }
        parm.setData("ACI_NO", aciNo);
        TParm result = ACIADRTool.getInstance().updateADRReportState(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        data.setData("UPLOAD_FLG", selRow, parm.getData("UPLOAD_FLG"));
        data.setData("UPLOAD_DATE", selRow, parm.getData("UPLOAD_DATE"));
        callFunction("UI|TABLE|setParmValue", data);
        callFunction("UI|TABLE|setSelectedRow", selRow);
        this.messageBox("P0001");// 保存成功
        if (parm.getValue("UPLOAD_FLG").equals("Y")) {
            this.callFunction("UI|report|setVisible", false);
            this.callFunction("UI|unReport|setVisible", true);
            this.callFunction("UI|REPORTED|setSelected", true);
        } else {
            this.callFunction("UI|report|setVisible", true);
            this.callFunction("UI|unReport|setVisible", false);
            this.callFunction("UI|UNREPORTED|setSelected", true);
            this.callFunction("UI|UPLOAD_FLG|setSelected", false);
            onChooseR();
        }
    }

    /**
     * 取消已上报
     */
    public void onUnReport(){
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            messageBox("请选择一条记录");
            return;
        }
        TParm data = table.getParmValue();
        String aciNo = data.getValue("ACI_NO", selRow);
        TParm parm = new TParm();
        parm.setData("ACI_NO", aciNo);
        parm.setData("UPLOAD_FLG", "");
        parm.setData("UPLOAD_DATE", "");
        TParm result = ACIADRTool.getInstance().updateADRReportState(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        data.setData("UPLOAD_FLG", selRow, "");
        data.setData("UPLOAD_DATE", selRow, "");
        callFunction("UI|TABLE|setParmValue", data);
        callFunction("UI|TABLE|setSelectedRow", selRow);
        this.messageBox("P0001");// 保存成功
        if (parm.getValue("UPLOAD_FLG").equals("Y")) {
            this.callFunction("UI|report|setVisible", false);
            this.callFunction("UI|unReport|setVisible", true);
            this.callFunction("UI|REPORTED|setSelected", true);
        } else {
            this.callFunction("UI|report|setVisible", true);
            this.callFunction("UI|unReport|setVisible", false);
            this.callFunction("UI|UNREPORTED|setSelected", true);
            this.callFunction("UI|UPLOAD_FLG|setSelected", false);
            onChooseR();
        }
    }
    
    

    /**
     * 检查界面输入的有效性
     */
    public boolean checkUIData(TParm parm) {
        if (parm.getValue("MR_NO").equals("")) {
            this.messageBox("信息不完整：未填写病案号");
            return false;
        }
        if (parm.getValue("PAT_NAME").equals("")) {
            this.messageBox("信息不完整：未填写病患姓名");
            return false;
        }
        if (parm.getValue("SEX_CODE").equals("")) {
            this.messageBox("信息不完整：未选择性别");
            return false;
        }
        if (parm.getValue("SPECIES_CODE").equals("")) {
            this.messageBox("信息不完整：未选择民族");
            return false;
        }
        if (parm.getData("BIRTH_DATE") == null
                || parm.getData("BIRTH_DATE").equals(new TNull(Timestamp.class))) {
            this.messageBox("信息不完整：未填写出生日期");
            return false;
        }
        if (parm.getValue("WEIGHT").equals("")) {
            this.messageBox("信息不完整：未填写体重");
            return false;
        }
        if (parm.getValue("TEL").equals("")) {
            this.messageBox("信息不完整：未填写病患联系方式");
            return false;
        }
        if (parm.getValue("DIAG_CODE1").equals("")) {
            this.messageBox("信息不完整：未选择原患疾病");
            return false;
        }
        if(parm.getValue("ALLERGY_FLG").equals("Y")&&parm.getValue("ALLERGY_REMARK").equals("")){
            this.messageBox("信息不完整：未填写过敏史相关信息");
            return false;
        }
        if(parm.getValue("OTHER_FLG").equals("Y")&&parm.getValue("OTHER_REMARK").equals("")){
            this.messageBox("信息不完整：未填写过其他相关信息");
            return false;
        }
        if (parm.getValue("ADR_ID1").equals("")) {
            this.messageBox("信息不完整：未选择事件名称");
            return false;
        }
        if (parm.getData("EVENT_DATE") == null
                || parm.getData("EVENT_DATE").equals(new TNull(Timestamp.class))) {
            this.messageBox("信息不完整：未填写事件发生日期");
            return false;
        }
        if (parm.getValue("EVENT_DESC").equals("")) {
            this.messageBox("信息不完整：未填写事件描述");
            return false;
        }
        if (parm.getValue("EVENT_RESULT").equals("3") && parm.getValue("RESULT_REMARK").equals("")) {
            this.messageBox("信息不完整：未填写后遗症的表现");
            return false;
        }
        if (parm.getValue("EVENT_RESULT").equals("4") && parm.getValue("RESULT_REMARK").equals("")) {
            this.messageBox("信息不完整：未填写死因");
            return false;
        }
        if (parm.getValue("EVENT_RESULT").equals("4")
                && (parm.getData("DIED_DATE") == null || parm.getData("DIED_DATE")
                        .equals(new TNull(Timestamp.class)))) {
            this.messageBox("信息不完整：未填写死亡时间");
            return false;
        }
        if (parm.getValue("REPORT_USER").equals("")) {
            this.messageBox("信息不完整：未填写上报科室");
            return false;
        }
        if (parm.getValue("REPORT_OCC").equals("")) {
            this.messageBox("信息不完整：未选择上报人职业");
            return false;
        } else if (parm.getValue("REPORT_OCC").equals("6")) {// 其他
            if (parm.getValue("OCC_REMARK").equals("")) {
                this.messageBox("信息不完整：未填写上报人职业备注");
                return false;
            }
        }
        if (parm.getValue("REPORT_TEL").equals("")) {
            this.messageBox("信息不完整：未填写报告人联系电话");
            return false;
        }
        if (parm.getValue("REPORT_EMAIL").equals("")) {
            this.messageBox("信息不完整：未填写联系人电子邮箱");
            return false;
        }
        if (parm.getValue("REPORT_DEPT").equals("")) {
            this.messageBox("信息不完整：未选择报告科室");
            return false;
        }
        // if (parm.getValue("REPORT_STATION").equals("")) {
        // this.messageBox("请选择报告病区");
        // return false;
        // }
        if (parm.getValue("REPORT_UNIT").equals("")) {
            this.messageBox("信息不完整：未选择报告单位");
            return false;
        }
        if (parm.getValue("UNIT_CONTACTS").equals("")) {
            this.messageBox("信息不完整：未选择联系人");
            return false;
        }
        if (parm.getValue("UNIT_TEL").equals("")) {
            this.messageBox("信息不完整：未填写联系人电话");
            return false;
        }
        if (parm.getData("REPORT_DATE") == null
                || parm.getData("REPORT_DATE").equals(new TNull(Timestamp.class))) {
            this.messageBox("信息不完整：未填写报告日期");
            return false;
        }
        if (parm.getTimestamp("REPORT_DATE").getTime() < parm.getTimestamp("EVENT_DATE").getTime()) {
            this.messageBox("信息不完整：报告日期早于事件发生日期！");
            return false;
        }
        if (parm.getValue("ORDER_DESC").equals("")) {
            this.messageBox("信息不完整：未填写怀疑药品信息");
            return false;
        }
        return true;
    }
    
    
    /**
     * 汇出Excel
     */
    public void onExport() {
        if (((Integer) this.callFunction("UI|TABLE|getRowCount")) <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        TParm parmValue = (TParm) this.callFunction("UI|TABLE|getParmValue");
        String aciNoList = "";
        for (int i = 0; i < parmValue.getCount(); i++) {
            aciNoList += "'" + parmValue.getValue("ACI_NO", i) + "',";
        }
        aciNoList = aciNoList.substring(0, aciNoList.length() - 1);
 
        String sql =
                "SELECT "// A.ACI_NO,
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.FIRST_FLG = C.ID AND C.GROUP_ID = 'ADR_FIRSTFLG') AS FIRST_FLG,"
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.REPORT_TYPE = C.ID AND C.GROUP_ID = 'ADR_REPORTTYPE') AS REPORT_TYPE,"
                        + "WM_CONCAT(B.ORDER_DESC) ORDER_LIST, A.MR_NO, A.PAT_NAME,"
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.SEX_CODE = C.ID AND C.GROUP_ID = 'SYS_SEX') AS SEX_CODE,"
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.SPECIES_CODE = C.ID AND C.GROUP_ID = 'SYS_SPECIES') AS SPECIES_CODE,"
                        + "TO_CHAR(A.BIRTH_DATE, 'YYYY-MM-DD') AS BIRTH_DATE, TO_CHAR(A.WEIGHT) || 'kg' AS WEIGHT, A.TEL,"
                        + "('[' || (SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE1 = C.ICD_CODE) || ']' "
                        + "     || CASE WHEN ( SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE2 = C.ICD_CODE) IS NOT NULL "
                        + "             THEN '[' || ( SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE2 = C.ICD_CODE) || ']'"
                        + "             ELSE '' END "
                        + "     || CASE WHEN (SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE3 = C.ICD_CODE) IS NOT NULL"
                        + "             THEN '[' || (SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE3 = C.ICD_CODE) || ']'"
                        + "             ELSE '' END "
                        + "     || CASE WHEN (SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE4 = C.ICD_CODE) IS NOT NULL"
                        + "             THEN '[' || (SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE4 = C.ICD_CODE) || ']'"
                        + "             ELSE '' END"
                        + "     || CASE WHEN (SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE5 = C.ICD_CODE) IS NOT NULL"
                        + "             THEN '[' || (SELECT C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C WHERE A.DIAG_CODE5 = C.ICD_CODE) || ']'"
                        + "             ELSE '' END) AS DIAG_CODE,"
                        + "(CASE WHEN A.PERSON_HISTORY = '1' THEN A.PERSON_REMARK"
                        + "      ELSE (SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.PERSON_HISTORY = C.ID AND C.GROUP_ID = 'ADR_HISTORYSTATE') END)"
                        + "      AS PERSON_HISTORY,"
                        + "(CASE WHEN A.FAMILY_HISTORY = '1' THEN A.FAMILY_REMARK"
                        + "      ELSE (SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.FAMILY_HISTORY = C.ID AND C.GROUP_ID = 'ADR_HISTORYSTATE') END)"
                        + "      AS FAMILY_HISTORY,"
                        + "(CASE WHEN A.SMOKE_FLG = 'Y' THEN '[吸烟史]' ELSE '' END "
                        + "||CASE WHEN A.DRINK_FLG = 'Y' THEN '[饮酒史]' ELSE '' END "
                        + "||CASE WHEN A.PREGNANT_FLG = 'Y' THEN '[妊娠期]' ELSE '' END "
                        + "||CASE WHEN A.HEPATOPATHY_FLG = 'Y' THEN '[肝病史]' ELSE '' END "
                        + "||CASE WHEN A.NEPHROPATHY_FLG = 'Y' THEN '[肾病史]' ELSE '' END "
                        + "||CASE WHEN A.ALLERGY_FLG = 'Y' THEN '[过敏史：' || A.ALLERGY_REMARK || ']' ELSE '' END "
                        + "||CASE WHEN A.OTHER_FLG = 'Y' THEN '[其他：' || A.OTHER_REMARK || ']' ELSE '' END)  AS OTHER_CASE,"
                        + " ('[' || A.ADR_DESC1 || ']' "
                        + "      || CASE WHEN (SELECT C.ADR_DESC FROM ACI_ADRNAME C WHERE A.ADR_ID2 = C.ADR_ID) IS NOT NULL"
                        + "              THEN '[' || (SELECT C.ADR_DESC FROM ACI_ADRNAME C WHERE A.ADR_ID2 = C.ADR_ID) || ']' ELSE '' END "
                        + "      || CASE WHEN (SELECT C.ADR_DESC FROM ACI_ADRNAME C WHERE A.ADR_ID3 = C.ADR_ID) IS NOT NULL"
                        + "              THEN '[' || (SELECT C.ADR_DESC FROM ACI_ADRNAME C WHERE A.ADR_ID3 = C.ADR_ID) || ']' ELSE '' END "
                        + "      || CASE WHEN (SELECT C.ADR_DESC FROM ACI_ADRNAME C WHERE A.ADR_ID4 = C.ADR_ID) IS NOT NULL"
                        + "              THEN '[' || (SELECT C.ADR_DESC FROM ACI_ADRNAME C WHERE A.ADR_ID4 = C.ADR_ID) || ']' ELSE '' END) AS ADR_DESC,"
                        + "TO_CHAR(A.EVENT_DATE, 'YYYY/MM/DD HH24:MI') AS EVENT_DATE, A.EVENT_DESC,"
                        + "(CASE WHEN A.EVENT_RESULT = '3' THEN '有后遗症：' || A.RESULT_REMARK"
                        + "      WHEN A.EVENT_RESULT = '4' THEN '死亡[直接死因：' || A.RESULT_REMARK || '][死亡时间：' || TO_CHAR(A.DIED_DATE, 'YYYY/MM/DD HH24:MI:SS') || ']'"
                        + "      ELSE (SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.EVENT_RESULT = C.ID AND C.GROUP_ID = 'ADR_RESULT')"
                        + "      END) AS EVENT_RESULT,"
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.STOP_REDUCE = C.ID AND C.GROUP_ID = 'ADR_STOPRESULT') AS STOP_REDUCE,"
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.AGAIN_APPEAR = C.ID AND C.GROUP_ID = 'ADR_AGAINRESULT') AS AGAIN_APPEAR,"
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.DISE_DISTURB = C.ID AND C.GROUP_ID = 'ADR_DISTURBDISE') AS DISE_DISTURB,"
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.USER_REVIEW = C.ID AND C.GROUP_ID = 'ADR_REVIEW') AS USER_REVIEW,"
                        + "(SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.UNIT_REVIEW = C.ID AND C.GROUP_ID = 'ADR_REVIEW') AS UNIT_REVIEW,"
                        + "(SELECT C.USER_NAME FROM SYS_OPERATOR C WHERE A.REPORT_USER = C.USER_ID) AS REPORT_USER, A.REPORT_TEL,"
                        + "(CASE WHEN A.REPORT_OCC = '6' THEN A.OCC_REMARK"
                        + "      ELSE (SELECT C.CHN_DESC FROM SYS_DICTIONARY C WHERE A.REPORT_OCC = C.ID AND C.GROUP_ID = 'SYS_POSITION')"
                        + "      END) AS REPORT_OCC, A.REPORT_EMAIL,"
                        + "(SELECT C.REGION_CHN_DESC FROM SYS_REGION C WHERE A.REPORT_UNIT = C.REGION_CODE) AS REPORT_UNIT,"
                        + "(SELECT C.USER_NAME FROM SYS_OPERATOR C WHERE A.UNIT_CONTACTS = C.USER_ID) AS UNIT_CONTACTS,A.UNIT_TEL,"
                        + "TO_CHAR(A.REPORT_DATE, 'YYYY/MM/DD HH24:MI') AS REPORT_DATE "
                        + " FROM ACI_ADRM A, ACI_ADRD B "
                        + "WHERE A.ACI_NO = B.ACI_NO "
                        + "  AND B.TYPE = '0' "
                        + "  AND A.ACI_NO IN (#) "// ///////
                        + "GROUP BY A.ACI_NO, A.FIRST_FLG, A.REPORT_TYPE, A.MR_NO, A.PAT_NAME, A.SEX_CODE, A.SPECIES_CODE, A.BIRTH_DATE,"
                        + "         A.WEIGHT, A.TEL, A.DIAG_CODE1, A.DIAG_CODE2, A.DIAG_CODE3, A.DIAG_CODE4, A.DIAG_CODE5, A.PERSON_HISTORY,"
                        + "         A.PERSON_REMARK, A.FAMILY_HISTORY, A.FAMILY_REMARK, A.SMOKE_FLG, A.DRINK_FLG, A.PREGNANT_FLG,"
                        + "         A.HEPATOPATHY_FLG, A.NEPHROPATHY_FLG, A.ALLERGY_FLG, A.ALLERGY_REMARK, A.OTHER_FLG, A.OTHER_REMARK,"
                        + "         A.ADR_ID1, A.ADR_DESC1, A.ADR_ID2, A.ADR_ID3, A.ADR_ID4, A.EVENT_DATE, A.EVENT_DESC, A.EVENT_RESULT,"
                        + "         A.RESULT_REMARK, A.DIED_DATE, A.STOP_REDUCE, A.AGAIN_APPEAR, A.DISE_DISTURB, A.USER_REVIEW,A.UNIT_REVIEW,"
                        + "         A.REPORT_USER, A.REPORT_OCC, A.OCC_REMARK, A.REPORT_TEL, A.REPORT_EMAIL, A.REPORT_DEPT, A.REPORT_STATION,"
                        + "         A.REPORT_UNIT, A.UNIT_CONTACTS, A.UNIT_TEL, A.REPORT_DATE";
        sql = sql.replaceFirst("#", aciNoList);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        String titleHead =
                "首次/跟踪报告,100;报告类型,110;怀疑药品,200;病案号,130;姓名,90;"
                        + "性别,65;民族,80;出生日期,120;体重,60;联系方式,125;原患疾病,180;"
                        + "既往药品不良反应/事件,155;家族药品不良反应/事件,150;其他相关信息,250;药品不良反应/事件名称,180;发生时间,170;"
                        + "不良反应/事件描述,250;不良反应/事件后果,220;停药或减量后，反应是否消失或减轻,190;再次使用可疑药品后是否再次出现同样反应,185;"
                        + "对原患疾病的影响,145;报告人评价,95;报告科室评价,110;报告人,90;联系电话,125;"
                        + "职业,100;电子邮件,140;报告单位名称,160;联系人,90;联系方式,125;" + "报告日期,170";
        String[] parmMap =
                new String[]{"FIRST_FLG", "REPORT_TYPE", "ORDER_LIST", "MR_NO", "PAT_NAME",
                        "SEX_CODE", "SPECIES_CODE", "BIRTH_DATE", "WEIGHT", "TEL", "DIAG_CODE",
                        "PERSON_HISTORY", "FAMILY_HISTORY", "OTHER_CASE", "ADR_DESC", "EVENT_DATE",
                        "EVENT_DESC", "EVENT_RESULT", "STOP_REDUCE", "AGAIN_APPEAR",
                        "DISE_DISTURB", "USER_REVIEW", "UNIT_REVIEW", "REPORT_USER", "REPORT_TEL",
                        "REPORT_OCC", "REPORT_EMAIL", "REPORT_UNIT", "UNIT_CONTACTS", "UNIT_TEL",
                        "REPORT_DATE" };
        for (int i = 0; i < parmMap.length; i++) {
            result.addData("SYSTEM", "COLUMNS", parmMap[i]);
        }
        result.setCount(result.getCount());
        result.setData("TITLE", "药品不良反应明细表");
        result.setData("HEAD", titleHead);
        TParm[] parm = new TParm[]{result };
        ExportExcelUtil.getInstance().exeSaveExcel(parm, "药品不良反应明细表");
    }

    /**
     * 清空
     */
    public void onClear() {
        ((TRadioButton) this.getComponent("UNREPORTED")).setSelected(true);
        this.callFunction("UI|UPLOAD_FLG|setSelected", false);
        onChooseR();
        clearValue("REPORT_DEPT;DEPT_IN_CHARGE;REPORT_TYPE;ADR_ID;PHA_RULE;COUNT");
        callFunction("UI|TABLE|setParmValue", new TParm());
        initUI();
    }

	  /**
     * 选择“未上报/已上报”RadioButton
     */
    public void onReportBtn() {
        if (((TRadioButton) this.getComponent("UNREPORTED")).isSelected()) {
            if (this.getPopedem("SYSOPERATOR")) {
                this.callFunction("UI|report|setVisible", true);
                this.callFunction("UI|unReport|setVisible", false);
            }
        } else if (((TRadioButton) this.getComponent("REPORTED")).isSelected()) {
            if (this.getPopedem("SYSOPERATOR")) {
                this.callFunction("UI|report|setVisible", false);
                this.callFunction("UI|unReport|setVisible", true);
            }
        }// UNREPORTED
        callFunction("UI|TABLE|setParmValue", new TParm());
    }
	
    /**
     * 选择“已上报”
     */
    public void onChooseR() {
        if ((Boolean) this.callFunction("UI|UPLOAD_FLG|isSelected")) {
            this.callFunction("UI|UPLOAD_DATE|setEnabled", true);
            this.callFunction("UI|report|setEnabled", true);
        } else {
            this.callFunction("UI|UPLOAD_DATE|setEnabled", false);
            this.callFunction("UI|report|setEnabled", false);
        }
    }

	/**
	 * TABLE单击事件
	 */
    public void onTableClicked(int row) {
        TTable table = (TTable) this.getComponent("TABLE");
        if (row < 0) {
            return;
        }
        TParm parm = table.getParmValue().getRow(row);
        setValueForParm("UPLOAD_FLG;UPLOAD_DATE", parm);
        if (((TRadioButton) this.getComponent("UNREPORTED")).isSelected()) {
  
            setValue("UPLOAD_DATE", SystemTool.getInstance().getDate());
        }
        if (this.getPopedem("SYSOPERATOR")) {
 
            boolean isReported = parm.getBoolean("UPLOAD_FLG");
            if (isReported) {
                this.callFunction("UI|report|setVisible", false);
                this.callFunction("UI|unReport|setVisible", true);
            } else {
                this.callFunction("UI|report|setVisible", true);
                this.callFunction("UI|unReport|setVisible", false);
            }
        }
    }
	
    /**
     * 双击表格，调用“不良事件登记”界面查看事件详细记录
     */
    public void onTableDoubleClicked() {
        TTable table = (TTable) this.getComponent("TABLE");
        int row = table.getSelectedRow();
        if (row < 0) {
            messageBox("请选择一条记录");
            return;
        }
        if (!this.getPopedem("SYSOPERATOR")
                && !table.getParmValue().getValue("REPORT_USER", row).equals(Operator.getID())) {// 只有审核人，上报人能查看登记信息
            this.messageBox("您没有该权限");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ACI_NO", table.getParmValue().getRow(row).getValue("ACI_NO"));
        parm.setData("ADM_TYPE", table.getParmValue().getRow(row).getValue("ADM_TYPE"));
        parm.setData("CASE_NO", table.getParmValue().getRow(row).getValue("CASE_NO"));
        parm.setData("MR_NO", table.getParmValue().getRow(row).getValue("MR_NO"));
//        parm.setData("ADR_DATA", table.getParmValue().getRow(row));
        parm.addListener("onReturnContent", this, "onReturnContent");
        Object obj = this.openDialog("%ROOT%\\config\\aci\\ACIADRRecord.x", parm);
        if (obj != null) {
            TParm result = (TParm) obj;
            onQuery();
        }
    }
    
    /**
     * 回传
     * @param obj
     */
    public void onReturnContent(Object obj) {
        if (obj != null) {
            TParm parm = (TParm) obj;
            String aciNo = parm.getValue("ACI_NO");
            this.onQuery();
        }
    }
}
