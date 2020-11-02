package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TRadioButton;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.SYSDiagnosisTool;
import jdo.sys.SystemTool;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;

import jdo.bms.BMSSQL;
import jdo.bms.BMSSplrectTool;

import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;
import jdo.sys.PatTool;
import jdo.sys.Pat;
import jdo.util.Manager;
import com.javahis.system.combo.TComboDept;
import com.javahis.system.combo.TComboSYSStationCode;
import com.javahis.system.combo.TComboBMSBldCode;
import com.javahis.system.combo.TComboSYSUnit;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: 输血反应
 * </p>
 *
 * <p>
 * Description: 输血反应
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSSpleractControl
    extends TControl {

    // 外部调用传参
    //private TParm parm;

    private String action = "insert";

    private TTable table;

    public BMSSpleractControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
//        Object obj = this.getParameter();
//        if (obj instanceof TParm) {
//            parm = (TParm) obj;
//            this.setValue("DEPT_CODE", parm.getValue("DEPT_CODE"));
//            this.setValue("STATION_CODE", parm.getValue("STATION_CODE"));
//            this.setValue("MR_NO", parm.getValue("MR_NO"));
//            this.setValue("IPD_NO", parm.getValue("IPD_NO"));
//            case_no = parm.getValue("CASE_NO");
//
//            Pat pat = Pat.onQueryByMrNo(parm.getValue("MR_NO"));
//            this.setValue("PAT_NAME", pat.getName());
//            this.setValue("SEX", pat.getSexCode());
//            Timestamp date = StringTool.getTimestamp(new Date());
//            this.setValue("AGE",
//                          StringUtil.getInstance().showAge(pat.getBirthday(),
//                date));
//            this.setValue("ID_NO", pat.getIdNo());
//            this.setValue("TEST_BLD", pat.getBloodType());
//            this.setValue("BLD_TEXT", pat.getBloodType());
//            if ("+".equals(pat.getBloodRHType())) {
//                this.getRadioButton("RH_A").setSelected(true);
//            }
//            else {
//                this.getRadioButton("RH_B").setSelected(true);
//            }
//        }
        // 初始化画面数据
        initPage();
    }
    
    public void onBloodNoAction(){
    	String bloodNo = this.getValueString("BLOOD_NO") ;
    	if(bloodNo!=null && !"".equals(bloodNo)){
    		String sql = "SELECT  B.BLD_CODE,B.BLDRESU_CODE,B.RH_FLG,B.APPLY_NO,B.BLD_TYPE,B.BLOOD_VOL,B.OUT_DATE," +
    				"A.DIAG_CODE1,A.DIAG_CODE2,A.DIAG_CODE3,A.TRANS_HISTORY,A.PAT_OTH,A.PREGNANCY,A.INFANT " +
    				"FROM BMS_BLOOD  B ,BMS_APPLYM  A WHERE B.APPLY_NO=A.APPLY_NO AND B.BLOOD_NO =  '"+bloodNo+"'" ;
//    		System.out.println("sql====="+sql);
    		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                return;
            }
            if (result.getCount() <= 0) {
                return;
            }  
            String bldCode = result.getValue("BLD_CODE", 0) ;
            String bldResuCode = result.getValue("BLDRESU_CODE",0) ;
            TParm unitparm = new TParm(TJDODBTool.getInstance().select(BMSSQL. getBMSUnit(bldCode)));
            String diag1=result.getValue("DIAG_CODE1", 0) ;
            String diag2=result.getValue("DIAG_CODE2", 0) ;
            String diag3=result.getValue("DIAG_CODE3", 0) ;
            if(diag1 !=null &&  !"".equals(diag1)){
            TParm diagparm1 = SYSDiagnosisTool.getInstance().selectDataWithCode(diag1);
            this.setValue("DIAG_CODE1", diagparm1.getValue("ICD_CHN_DESC", 0)) ;
            }
            if(diag2 !=null &&  !"".equals(diag2)){
            TParm diagparm2 = SYSDiagnosisTool.getInstance().selectDataWithCode(diag2);
            this.setValue("DIAG_CODE2", diagparm2.getValue("ICD_CHN_DESC", 0)) ;
            }
            if(diag3 !=null &&  !"".equals(diag3)){
            TParm diagparm3 = SYSDiagnosisTool.getInstance().selectDataWithCode(diag3);
            this.setValue("DIAG_CODE3", diagparm3.getValue("ICD_CHN_DESC", 0)) ;
            }
            String RH_FLG = result.getValue("RH_FLG", 0) ;
            if ("+".equals(RH_FLG)) {
                this.getRadioButton("RH_A1").setSelected(true);
            }
            else if ("-".equals(RH_FLG)) {
                this.getRadioButton("RH_B1").setSelected(true);
            }
            else {
                this.getRadioButton("RH_A1").setSelected(false);
                this.getRadioButton("RH_B1").setSelected(false);
            }
            // BMS_BLOOD:  BLOOD_NO, BLD_CODE ,SUBCAT_CODE, BLOOD_SOURCE ,RH_FLG ,BLD_TYPE, SUBCAT_CODE,APPLY_NO
            // BMS_APPLYM: APPLY_NO, DIAG_CODE1 DIAG_CODE2 DIAG_CODE3 TRANS_HISTORY PAT_OTH PREGNANCY INFANT TEST_BLD
            this.setValue("BLD_CODE", bldCode) ;
            this.setValue("BLOOD_SOURCE", bldResuCode) ;
//            this.messageBox(result.getValue("BLD_TYPE", 0));
            this.setValue("BLD_TYPE", result.getValue("BLD_TYPE", 0)) ;
            this.setValue("BLOOD_VOL", result.getValue("BLOOD_VOL", 0)) ;
            this.setValue("UNIT_CODE", unitparm.getValue("UNIT_CODE", 0));
            this.setValue("APPLY_NO", result.getValue("APPLY_NO", 0)) ;
            this.setValue("TRANS_HISTORY", result.getValue("TRANS_HISTORY", 0)) ;
            this.setValue("PAT_OTH", result.getValue("PAT_OTH", 0)) ;
            this.setValue("PREGNANCY", result.getValue("PREGNANCY", 0)) ;
            this.setValue("INFANT", result.getValue("INFANT", 0)) ;
            this.setValue("OUT_DATE", result.getValue("OUT_DATE", 0).substring(0, 19).replaceAll("-", "/")) ;
    	}
    }

    /**
     * 保存方法
     */
    public void onSave() {
        if (!CheckData()) {
            return;
        }
        TParm parm = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
        parm.setData("STATION_CODE", this.getValue("STATION_CODE"));
        parm.setData("MR_NO", this.getValue("MR_NO"));
        parm.setData("IPD_NO", this.getValue("IPD_NO"));
        parm.setData("CASE_NO", this.getValue("CASE_NO"));
        parm.setData("BLD_CODE", this.getValue("BLD_CODE"));
        parm.setData("RECAT_SYMPTOM", this.getValue("RECAT_SYMPTOM"));
        parm.setData("BED_NO", this.getValue("BED_NO"));
//        System.out.println(this.getValue("BED_NO"));
//        parm.setData("REACTION_CODE", " ");
        parm.setData("REACTION_DATE", date);
        parm.setData("TEST_USER", Operator.getID());
        parm.setData("MATCH_USER", Operator.getID());
        parm.setData("START_DATE", this.getValue("START_DATE"));
        parm.setData("END_DATE", this.getValue("END_DATE"));
        parm.setData("REACT_CLASS", this.getValue("REACT_CLASS"));
        parm.setData("REACT_OTH", this.getValue("REACT_OTH"));
        parm.setData("TREAT", this.getValue("TREAT"));
        parm.setData("REACT_HIS", this.getValue("REACT_HIS"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("BLOOD_NO",this.getValue("BLOOD_NO")) ;
        parm.setData("R_FEVER",this.getValue("R_FEVER")) ;
        parm.setData("R_SHIVER",this.getValue("R_SHIVER")) ;
        parm.setData("R_URTICARIA",this.getValue("R_URTICARIA")) ;
        parm.setData("R_HEMOLYSIS",this.getValue("R_HEMOLYSIS")) ;
        parm.setData("R_HEMOGLOBINURIA",this.getValue("R_HEMOGLOBINURIA")) ;
        parm.setData("R_OTHER",this.getValue("R_OTHER")) ;
//        System.out.println("parm==="+parm);
        TParm result = new TParm();

        if ("insert".equals(action)) {
            String react_no = SystemTool.getInstance().getNo("ALL",
                "BMS", "REACT_NO", "No");
            parm.setData("REACT_NO", react_no);
            //System.out.println("parm---"+parm);
            // 执行数据新增
            result = TIOM_AppServer.executeAction(
                "action.bms.BMSSplreactAction", "onInsert", parm);
        }
        else if ("update".equals(action)) {
            parm.setData("REACT_NO", this.getValue("REACT_NO"));
            // 执行数据更新
            result = TIOM_AppServer.executeAction(
                "action.bms.BMSSplreactAction", "onUpdate", parm);
        }
        // 主项保存判断
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

    /**
     * 打印方法
     */
    public void onPrint() {
        if (table.getSelectedRow() < 0) {
            this.messageBox("请选择打印项");
            return;//==liling 20140703 add
        }

        // 打印数据
        TParm date = new TParm();
        date.setData("TITLE", "TEXT", Manager.getOrganization(). getHospitalCHNFullName(Operator.getRegion()) + "输血反应记录单");
        // TABLE_1数据
        String rh_type = "";
        if (getRadioButton("RH_A").isSelected()) {
            rh_type = "阳性";
        }
        else if (getRadioButton("RH_B").isSelected()) {
            rh_type = "阴性";
        }
        String birth =table.getParmValue().getTimestamp("BIRTH_DATE", table.getSelectedRow()).toString().substring(0, 10).replaceAll("-", "/");
        date.setData("PAT_NAME", "TEXT", TypeTool.getString(getValueString("PAT_NAME")));
        date.setData("SEX", "TEXT",("1".equals(getValueString("SEX")) ? "男" : "女"));
        date.setData("Birthday", "TEXT",birth);
        date.setData("IPD_NO", "TEXT",TypeTool.getString(getValueString("IPD_NO")));
        date.setData("AGE", "TEXT",TypeTool.getString(getValueString("AGE")));
        date.setData("MR_NO", "TEXT",TypeTool.getString(getValueString("MR_NO")));
        date.setData("DEPT_CODE", "TEXT",this.getText("DEPT_CODE"));
        date.setData("STATION_CODE", "TEXT",this.getText("STATION_CODE"));
        date.setData("BED_NO", "TEXT",this.getText("BED_NO"));
        date.setData("BLD_TYPE", "TEXT",TypeTool.getString(getValueString("BLD_TEXT"))+"型");
        date.setData("RH_FLG", "TEXT",rh_type);
        date.setData("TRANS_HISTORY", "TEXT",this.getText("TRANS_HISTORY"));
        date.setData("PREGNANCY", "TEXT",this.getValueString("PREGNANCY"));
        date.setData("INFANT", "TEXT",this.getValueString("INFANT"));
        date.setData("APPLY_NO", "TEXT",this.getValueString("APPLY_NO"));
        date.setData("BAR_CODE", "TEXT",this.getValueString("REACT_NO"));
        
        TParm table_0 = new TParm();
        String rh_type1 = "";
        if (getRadioButton("RH_A1").isSelected()) {
            rh_type1 = "阳性";
        }
        else if (getRadioButton("RH_B1").isSelected()) {
            rh_type1 = "阴性";
        }
        table_0.addData("TABLE_0_1","其他:" + this.getValueString("PAT_OTH"));
        table_0.addData("TABLE_0_1", "疾病诊断:" + getValueString("DIAG_CODE1")+" "+getValueString("DIAG_CODE2")+" "+getValueString("DIAG_CODE3"));
        table_0.setCount(2);
        table_0.addData("SYSTEM", "COLUMNS", "TABLE_0_1");
        date.setData("TABLE_0", table_0.getData());
      //供血血型 血袋编号 输血量
        TParm table_1 = new TParm();
        table_1.addData("TABLE_1_1", "ABO血型:"+this.getText("BLD_TYPE")+" "+"Rh（D）:" +rh_type1);
        table_1.addData("TABLE_1_2",this.getValueString("BLOOD_NO"));
        table_1.addData("TABLE_1_3",this.getValueString("BLOOD_VOL")+((TComboSYSUnit)this.getComponent("UNIT_CODE")). getSelectedName() );
//        System.out.println("UNIT_CODE===="+this.getText("UNIT_CODE"));
        table_1.setCount(1);
        table_1.addData("SYSTEM", "COLUMNS", "TABLE_1_1");
        table_1.addData("SYSTEM", "COLUMNS", "TABLE_1_2");
        table_1.addData("SYSTEM", "COLUMNS", "TABLE_1_3");
        date.setData("TABLE_1", table_1.getData());
       
        // TABLE_2数据  输血类型 ：血品 血量 单位
        TParm table_2 = new TParm();
//        System.out.println("BLD_CODE===="+this.getText("BLD_CODE"));
//        System.out.println(( (TComboBMSBldCode)this.getComponent("BLD_CODE")). getSelectedName());
        table_2.addData("TABLE_2_1",( (TComboBMSBldCode)this.getComponent("BLD_CODE")). getSelectedName());//( (TComboBMSBldCode)this.getComponent("BLD_CODE")). getSelectedName()
        table_2.addData("TABLE_2_2",this.getValueString("BLOOD_VOL"));
        table_2.addData("TABLE_2_3",((TComboSYSUnit)this.getComponent("UNIT_CODE")). getSelectedName() );
        table_2.setCount(1);
        table_2.addData("SYSTEM", "COLUMNS", "TABLE_2_1");
        table_2.addData("SYSTEM", "COLUMNS", "TABLE_2_2");
        table_2.addData("SYSTEM", "COLUMNS", "TABLE_2_3");
        date.setData("TABLE_2", table_2.getData());
        
        // TABLE_3数据  不良反应：R_FEVER;R_SHIVER;R_URTICARIA;R_HEMOLYSIS;R_HEMOGLOBINURIA;R_OTHER
        TParm table_3 = new TParm();
        table_3.addData("TABLE_3_1", this.getValueString("R_FEVER")+"℃");
        table_3.addData("TABLE_3_2", ((TComboBox)this.getComponent("R_SHIVER")). getSelectedName());
        table_3.addData("TABLE_3_3",((TComboBox)this.getComponent("R_URTICARIA")). getSelectedName());
        table_3.addData("TABLE_3_4",((TComboBox)this.getComponent("R_HEMOLYSIS")). getSelectedName());// TypeTool.getString(getComboBox("REACT_CLASS").getSelectedName())
        table_3.addData("TABLE_3_5",((TComboBox)this.getComponent("R_HEMOGLOBINURIA")). getSelectedName());
        table_3.addData("TABLE_3_6",((TComboBox)this.getComponent("R_OTHER")). getSelectedName());
        table_3.setCount(1);
        table_3.addData("SYSTEM", "COLUMNS", "TABLE_3_1");
        table_3.addData("SYSTEM", "COLUMNS", "TABLE_3_2");
        table_3.addData("SYSTEM", "COLUMNS", "TABLE_3_3");
        table_3.addData("SYSTEM", "COLUMNS", "TABLE_3_4");
        table_3.addData("SYSTEM", "COLUMNS", "TABLE_3_5");
        table_3.addData("SYSTEM", "COLUMNS", "TABLE_3_6");
        date.setData("TABLE_3", table_3.getData());
        
        // 表尾数据 发血时间 填表时间 填报人
        date.setData("OUT_DATE", "TEXT",this.getValueString("OUT_DATE").substring(0, 19).replaceAll("-", "/"));
        date.setData("NOW", "TEXT",SystemTool.getInstance().getDate().toString().substring(0, 19).replaceAll("-", "/"));
        date.setData("OPT_USER", "TEXT",  Operator.getName());//this.getValueString("START_DATE").substring(0, 19).replaceAll("-", "/")
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\BMS\\BMSSpleract_V45.jhw", date);
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        if (!"".equals(this.getValueString("REACT_NO"))) {
            parm.setData("REACT_NO", this.getValue("REACT_NO"));
        }
        if (!"".equals(this.getValueString("DEPT_CODE"))) {
            parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
        }
        if (!"".equals(this.getValueString("STATION_CODE"))) {
            parm.setData("STATION_CODE", this.getValue("STATION_CODE"));
        }
        if (!"".equals(this.getValueString("MR_NO"))) {
            parm.setData("MR_NO", this.getValue("MR_NO"));
        }
        if (!"".equals(this.getValueString("IPD_NO"))) {
            parm.setData("IPD_NO", this.getValue("IPD_NO"));
        }
        if (!"".equals(this.getValueString("CASE_NO"))) {
            parm.setData("CASE_NO", this.getValue("CASE_NO"));//==liling add 
        }
        TParm result = BMSSplrectTool.getInstance().onQueryTransReaction(parm);
        if (result == null || result.getCount("REACT_NO") <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
//        System.out.println("result===="+result);
        table.setParmValue(result);
        
    }


    /**
     * 清空方法
     */
    public void onClear() {
        String clearString =
            "REACT_NO;DEPT_CODE;STATION_CODE;MR_NO;IPD_NO;" +
            "PAT_NAME;AGE;ID_NO;TEST_BLD;VALID_DAY;IN_PRICE;" +
            "SEX;START_DATE;END_DATE;BLD_CODE;REACT_CLASS;" +
            "REACT_OTH;REACT_HIS;RECAT_SYMPTOM;TREAT;BLD_TEXT;CASE_NO;RH_A;RH_B;BLOOD_NO;BLOOD_SOURCE;" +
            "APPLY_NO;BLD_TYPE;BLOOD_VOL;DIAG_CODE1;DIAG_CODE2;DIAG_CODE3;TRANS_HISTORY;PAT_OTH;OUT_DATE;" +
            "PREGNANCY;INFANT;UNIT_CODE;R_FEVER;R_SHIVER;R_URTICARIA;R_HEMOLYSIS;R_HEMOGLOBINURIA;R_OTHER;RH_A1;RH_B1;BED_NO ";
        this.clearValue(clearString);
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("START_DATE", date);
        this.setValue("END_DATE", date);
        //System.out.println("1111");
        //getRadioButton("RH_A").setSelected(false);
        //getRadioButton("RH_B").setSelected(false);
        //System.out.println("2222");
        action = "insert";
        table.removeRowAll();
        table.setSelectionMode(0);
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            this.messageBox("没有删除项");
            return;
        }
        TParm parm = new TParm();
        parm.setData("REACT_NO", this.getValue("REACT_NO"));
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction("action.bms.BMSSplreactAction",
                                              "onDelete", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("删除失败");
            return;
        }
        table.removeRow(row);
        table.setSelectionMode(0);
        this.messageBox("删除成功");
        onClear();
    }

    /**
     * 病案号回车事件
     */
    public void onMrNoAction() {
        String mr_no = PatTool.getInstance().checkMrno(getValueString("MR_NO"));
        TParm parm = new TParm();
        parm.setData("MR_NO", mr_no);
        TParm result = (TParm) openDialog(
            "%ROOT%\\config\\bms\\BMSSplreactQuery.x", parm);
        this.setValue("DEPT_CODE", result.getValue("DEPT_CODE"));
        this.setValue("STATION_CODE", result.getValue("STATION_CODE"));
        this.setValue("MR_NO", result.getValue("MR_NO"));
        this.setValue("CASE_NO", result.getValue("CASE_NO"));
        this.setValue("IPD_NO", result.getValue("IPD_NO"));
        this.setValue("BED_NO", result.getValue("BED_NO"));//==liling 20140703 add
        Pat pat = Pat.onQueryByMrNo(mr_no);
        this.setValue("PAT_NAME", pat.getName());
        this.setValue("SEX", pat.getSexCode());
        Timestamp date = StringTool.getTimestamp(new Date());
//        this.setValue("AGE", StringUtil.getInstance().showAge(pat.getBirthday(),  date));
        this.setValue("AGE",DateUtil.showAge(pat.getBirthday(), date));//==liling modify 20140703

        //this.messageBox(pat.getIdNo());
        this.setValue("ID_NO", pat.getIdNo());
        this.setValue("TEST_BLD", pat.getBloodType());
        this.setValue("BLD_TEXT", pat.getBloodType());
        String rh_type = pat.getBloodRHType();
        if ("+".equals(rh_type)) {
            this.getRadioButton("RH_A").setSelected(true);
        }
        else if ("-".equals(rh_type)) {
            this.getRadioButton("RH_B").setSelected(true);
        }
        else {
            this.getRadioButton("RH_A").setSelected(false);
            this.getRadioButton("RH_B").setSelected(false);
        }
    }

    /**
     * 表格单击事件
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        if (row != -1) {
            this.setValue("REACT_NO",
                          table.getParmValue().getValue("REACT_NO", row));
            this.setValue("DEPT_CODE",
                          table.getParmValue().getValue("DEPT_CODE", row));
            this.setValue("STATION_CODE",
                          table.getParmValue().getValue("STATION_CODE", row));
            this.setValue("BED_NO",
                    table.getParmValue().getValue("BED_NO", row));//==liling add
            this.setValue("MR_NO", table.getParmValue().getValue("MR_NO", row));
            this.setValue("IPD_NO", table.getParmValue().getValue("IPD_NO", row));
            this.setValue("PAT_NAME",
                          table.getParmValue().getValue("PAT_NAME", row));
            this.setValue("SEX", table.getParmValue().getValue("SEX_CODE", row));
            Timestamp date = StringTool.getTimestamp(new Date());
//            this.setValue("AGE", StringUtil.getInstance().showAge(table.getParmValue().getTimestamp("BIRTH_DATE", row), date));
            this.setValue("AGE",DateUtil.showAge(table.getParmValue().getTimestamp("BIRTH_DATE", row), date));//==liling modify 20140703
            this.setValue("ID_NO", table.getParmValue().getValue("IDNO", row));
            this.setValue("TEST_BLD",
                          table.getParmValue().getValue("BLOOD_TYPE", row));
            String rh_type = table.getParmValue().getValue("BLOOD_RH_TYPE", row);
            if ("+".equals(rh_type)) {
                this.getRadioButton("RH_A").setSelected(true);
            }
            else if ("-".equals(rh_type)) {
                this.getRadioButton("RH_B").setSelected(true);
            }
            else {
                this.getRadioButton("RH_A").setSelected(false);
                this.getRadioButton("RH_B").setSelected(false);
            }
            this.setValue("BLD_TEXT",
                          table.getParmValue().getValue("BLOOD_TYPE", row));
            this.setValue("START_DATE",
                          table.getParmValue().getTimestamp("START_DATE", row));
            this.setValue("END_DATE",
                          table.getParmValue().getTimestamp("END_DATE", row));
            this.setValue("BLD_CODE",
                          table.getParmValue().getValue("BLD_CODE", row));
            this.setValue("REACT_CLASS",
                          table.getParmValue().getValue("REACT_CLASS", row));
//            this.setValue("REACTION_CODE",
//                          table.getParmValue().getValue("REACTION_CODE", row));
            this.setValue("REACT_OTH",
                          table.getParmValue().getValue("REACT_OTH", row));
            this.setValue("REACT_HIS",
                          table.getParmValue().getValue("REACT_HIS", row));
            this.setValue("RECAT_SYMPTOM",
                          table.getParmValue().getValue("RECAT_SYMPTOM", row));
            this.setValue("TREAT", table.getParmValue().getValue("TREAT", row));
            this.setValue("CASE_NO", table.getParmValue().getValue("CASE_NO", row));
            this.setValue("BLOOD_NO", table.getParmValue().getValue("BLOOD_NO", row)) ;
            onBloodNoAction();//==LILING ADD
            //this.getComboBox("BLOOD_SOURCE").setSelectedID(table.getParmValue().getValue("BLDRESU_CODE", row)) ;
            this.setValue("BLOOD_SOURCE", table.getParmValue().getValue("BLDRESU_CODE", row)) ;
            //* ==liling add 发热、寒战、荨麻疹、溶血、血红蛋白尿、其他   start==
           // *  R_FEVER,R_SHIVER,R_URTICARIA,R_HEMOLYSIS, R_HEMOGLOBINURIA,R_OTHER
            this.setValue("R_FEVER", table.getParmValue().getValue("R_FEVER", row)) ;
            this.setValue("R_SHIVER", table.getParmValue().getValue("R_SHIVER", row)) ;
            this.setValue("R_URTICARIA", table.getParmValue().getValue("R_URTICARIA", row)) ;
            this.setValue("R_HEMOLYSIS", table.getParmValue().getValue("R_HEMOLYSIS", row)) ;
            this.setValue("R_HEMOGLOBINURIA", table.getParmValue().getValue("R_HEMOGLOBINURIA", row)) ;
            this.setValue("R_OTHER", table.getParmValue().getValue("R_OTHER", row)) ;
          //* ==liling add 发热、寒战、荨麻疹、溶血、血红蛋白尿、其他   end==
            action = "update";
        }
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("START_DATE", date);
        this.setValue("END_DATE", date);
        // 初始化TABLE
        table = getTable("TABLE");
    }

    /**
     * 检核数据
     * @return boolean
     */
    private boolean CheckData() {
        if ("".equals(this.getValueString("MR_NO"))) {
            this.messageBox("病案号不能为空");
            return false;
        }
        if ("".equals(this.getValueString("BLOOD_NO"))) {//add by wanglong 20121219
            this.messageBox("院内条码不能为空");
            return false;
        }
        if ("".equals(this.getValueString("BLD_CODE"))) {
            this.messageBox("反应血品不能为空");
            return false;
        }
        if ("".equals(this.getValueString("REACT_CLASS"))) {
            this.messageBox("反应等级不能为空");
            return false;
        }
//        if ("".equals(this.getValueString("REACTION_CODE"))) {
//            this.messageBox("输血反应不能为空");
//            return false;
//        }
        if ("".equals(this.getValueString("REACT_HIS"))) {
            this.messageBox("过敏反应史不能为空");
            return false;
        }
        return true;
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     *
     * @param tagName String
     * @return TCheckBox
     */
    private TCheckBox getCheckBox(String tagName){
        return (TCheckBox) getComponent(tagName);
    }

    /**
     *
     * @param tagName String
     * @return TComboBox
     */
    private TComboBox getComboBox(String tagName){
        return (TComboBox) getComponent(tagName);
    }


}
