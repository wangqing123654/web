package com.javahis.ui.bil;

import com.dongyang.control.*;

import java.awt.Font;
import java.math.BigDecimal;
import java.sql.Timestamp;

import jdo.opb.OPBFeeListPrintTool;
import jdo.sys.SystemTool;

import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.bil.BILDailyFeeQueryTool;

import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

import jdo.sys.Operator;

import java.text.DecimalFormat;

import jdo.bil.BILPayTool;
import jdo.sys.Pat;

import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

import jdo.sys.IReportTool;
import jdo.sys.SYSChargeHospCodeTool;
import jdo.sys.DictionaryTool;
import jdo.adm.ADMInpTool;
//import com.javahis.device.NJCityInwDriver;
import jdo.sys.PatTool;


/**
 * <p>Title: 每日费用清单</p>
 *
 * <p>Description: 每日费用清单</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-
 * @version 1.0
 */
public class BILDailyFeeQueryControl extends TControl {
    private String inHosp = ""; //记录查询的是在院病人还是出院病人
    private boolean isPush = false; //记录当前是否是查询欠费病人的状态
    public void onInit() {
        onClear();
    }

    /**
     * 初始化日期
     */
    public void dateInit() {
        inHosp = "";
        isPush = false;
        callFunction("UI|push|setEnabled", isPush);
        String now = StringTool.getString(SystemTool.getInstance().getDate(),
                                          "yyyyMMdd");
        this.setValue("START_DATE",
                      StringTool.getTimestamp(now + "000000", "yyyyMMddHHmmss"));
        this.setValue("END_DATE",
                      StringTool.getTimestamp(now + "235959", "yyyyMMddHHmmss"));
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = new TParm();
        //在院
        if (this.getValueBoolean("IN")) {
//            parm.setData("BILL_DATE_S",this.getValue("START_DATE"));
//            parm.setData("BILL_DATE_E",this.getValue("END_DATE"));
            parm.setData("IN", "Y");
            inHosp = "IN";
        } else if (this.getValueBoolean("OUT")) {
            parm.setData("DS_DATE_S", this.getValue("START_DATE"));
            parm.setData("DS_DATE_E", this.getValue("END_DATE"));
            parm.setData("OUT", "Y");
            inHosp = "OUT";
        }
        if (this.getValueString("DEPT_CODE").length() > 0) {
            parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
        }
        if (this.getValueString("STATION_CODE").length() > 0) {
            parm.setData("STATION_CODE", this.getValue("STATION_CODE"));
        }
        if (this.getValueString("BED_NO").length() > 0) {
            parm.setData("BED_NO", this.getValue("BED_NO"));
        }
        if (this.getValueString("MR_NO").length() > 0) {
            String MR_NO = PatTool.getInstance().checkIpdno(this.getValueString("MR_NO"));
            this.setValue("MR_NO", MR_NO);
            parm.setData("MR_NO", MR_NO);
        }
        if (this.getValueString("IPD_NO").length() > 0) {
            String IPD_NO = PatTool.getInstance().checkIpdno(this.getValueString("IPD_NO"));
            this.setValue("IPD_NO", IPD_NO);
            parm.setData("IPD_NO", this.getValue("IPD_NO"));
        }
        if ("Y".equalsIgnoreCase(this.getValueString("YELLOW_SIGN"))) {
            parm.setData("YELLOW_SIGN", this.getValue("YELLOW_SIGN"));
            isPush = true;
        } else {
            if ("Y".equalsIgnoreCase(this.getValueString("RED_SIGN"))) {
                parm.setData("RED_SIGN", this.getValue("RED_SIGN"));
                isPush = true;
            } else {
                isPush = false;
            }
        }
        if (!"".equals(Operator.getRegion()))
            parm.setData("REGION_CODE", Operator.getRegion());
        TParm result = BILDailyFeeQueryTool.getInstance().selectdata(parm);
        if (result.getErrCode() != 0) {
            this.messageBox("E0005");
            isPush = false;
            return;
        }
        if (result.getCount() == 0) {
            this.messageBox("E0008");
        }
        //==liling start==
        for(int i=0;i<result.getCount();i++){
        if(result.getValue("LUMPWORK_CODE", i).length()>0 && !result.getValue("LUMPWORK_CODE", i).equals("")){
        	result.setDataN("LUMPWORK_CODE", i, "Y");
        }
        }
        //==liling end====
        ((TTable)this.getComponent("TABLE")).setParmValue(result);
        callFunction("UI|push|setEnabled", isPush);
    }

    /**
     * 打印预览
     */
    public void onPrintView() {
        print(false);
    }

    /**
     * 打印
     */
    public void onPrint() {
        print(true);
    }
    /**
     * 查询套餐总金额
     * yanjing 20150304
     */
    private double onSelectLupAmt(String caseNo){
    	TParm result = new TParm();
    	String admSql="SELECT CASE_NO,M_CASE_NO,NEW_BORN_FLG FROM ADM_INP WHERE CASE_NO='"+caseNo+"'";
    	result = new TParm(TJDODBTool.getInstance().select(admSql));
    	String sql ="";
    	if (result.getValue("NEW_BORN_FLG",0).equals("Y")&&
    			null!=result.getValue("M_CASE_NO",0)&&result.getValue("M_CASE_NO",0).length()>0) {
    		 sql = "SELECT A.AR_AMT AS FEE FROM MEM_PACKAGE_TRADE_M A  " +
 					"WHERE A.CASE_NO ='"+result.getValue("M_CASE_NO",0)+"' AND A.USED_FLG='1'";
		}else{
			 sql = "SELECT A.AR_AMT AS FEE FROM MEM_PACKAGE_TRADE_M A  " +
	 				"WHERE A.CASE_NO ='"+caseNo+"' AND A.USED_FLG='1'";
		}
    	result = new TParm(TJDODBTool.getInstance().select(sql));
    	double amt = result.getDouble("FEE",0);    	
    	return amt;
    }

    /**
     * 打印
     * @param isView boolean 是否是预览
     */
    public void print(boolean isView) {   	
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        int count = table.getRowCount();
        if (count <= 0) {
            return;
        }
        String startDate = "";
        String endDate = "";
        //判断查询的时间段
        if (this.getValueBoolean("PRINT_DATE")) { //选择打印时段
            if (this.getValueString("START_PD").length() <= 0) {
                this.messageBox_("请选择打印时段的起始日期");
                return;
            }
            if (this.getValueString("END_PD").length() <= 0) {
                this.messageBox_("请选择打印时段的截至日期");
                return;
            }
            startDate = StringTool.getString((Timestamp)this.getValue(
                    "START_PD"), "yyyy/MM/dd");
            endDate = StringTool.getString((Timestamp)this.getValue("END_PD"),
                                           "yyyy/MM/dd");
        }
        for (int i = 0; i < count; i++) {
        	 startDate = "";
             endDate = "";
            if ("N".equals(table.getItemString(i, "FLG"))) {
                continue;
            }
            TParm parm = new TParm();
            if (this.getValueBoolean("FEE_TYPE1")) { //院内费用
                if (this.getValueBoolean("PRINT_TYPE2")) { //每日清单
                    //如果是以“每日费用”的格式打印 那么需要根据BILL_DATE进行排序 为了方便筛选每天的数据
                    parm.setData("ORDER_BY3", "Y");
                } else {
                    //如果是以“汇总清单”的格式打印 那么只根据收据费用代码排序就可以了 不用筛选每一天的
                    parm.setData("ORDER_BY1", "Y");
                }
            } else if (this.getValueBoolean("FEE_TYPE2")) { //收据费用
                if (this.getValueBoolean("PRINT_TYPE2")) { //每日清单
                    //如果是以“每日费用”的格式打印 那么需要根据BILL_DATE进行排序 为了方便筛选每天的数据
                    parm.setData("ORDER_BY4", "Y");
                } else {
                    parm.setData("ORDER_BY2", "Y");
                }
            }
            String CASE_NO = table.getItemString(i, "CASE_NO");
            parm.setData("CASE_NO", CASE_NO);
            //判断查询的时间段
            if (!this.getValueBoolean("PRINT_DATE")) {
                if ("IN".equals(inHosp)) { //在院病人查询打印数据
                    //打印数据的日期以起始日期为准
                    startDate = StringTool.getString((Timestamp)this.getValue(
                            "START_DATE"), "yyyy/MM/dd");
                    //截至日期取当前时间
                    endDate = StringTool.getString(SystemTool.getInstance().
                            getDate(), "yyyy/MM/dd");
                } else if ("OUT".equals(inHosp)) {
                	//caowl 20130722 start
//                    startDate = StringTool.getString(table.getItemTimestamp(i,
//                            "IN_DATE"), "yyyy/MM/dd");
//                    endDate = StringTool.getString(table.getItemTimestamp(i,
//                            "DS_DATE"), "yyyy/MM/dd");
                	//caowl 20130722 end
                }
            }
            parm.setData("DATE_S", startDate);
            parm.setData("DATE_E", endDate);
            parm.setData("REGION_CODE", Operator.getRegion());
            TParm result;
            TParm result1 =new TParm();
            TParm result2 =new TParm();
            TParm result3 =new TParm();
            TParm result4 =new TParm();
            String insSql = 
        		" SELECT CONFIRM_NO FROM INS_ADM_CONFIRM WHERE CASE_NO = '" + parm.getValue("CASE_NO") + "' AND IN_STATUS = '2'";
        	TParm insParm = new TParm(TJDODBTool.getInstance().select(insSql));
        	insParm = new TParm();
            if (this.getValueBoolean("PRINT_TYPE2")) { //每日清单
//================================================================================================================
            	if(insParm.getCount("CONFIRM_NO")>0){
            		String insWhere = "";
                    String where = "";
                    if(parm.getValue("DATE_S").length()>0&&parm.getValue("DATE_E").length()>0){
                        where = " AND A.BILL_DATE BETWEEN TO_DATE('"+parm.getValue("DATE_S")+"'||'000000','YYYY/MM/DDHH24MISS') AND TO_DATE('"+parm.getValue("DATE_E")+"'||'235959','YYYY/MM/DDHH24MISS') ";
                        insWhere = 
                        	"";
//                        " AND A.BILL_DATE BETWEEN H.KSSJ AND H.JSSJ ";
                    }
                    String regionWhere  = "";
                    if(!"".equals(parm.getValue("REGION_CODE")))
                        regionWhere = " AND F.REGION_CODE = '"+parm.getValue("REGION_CODE")+"' ";
                    	regionWhere = " ";
                    String sql = 
                    	//===================================================
                    	"SELECT D.IPD_CHARGE_CODE REXP_CODE,D.CHARGE_HOSP_CODE HEXP_CODE,A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION, "+
//                    	"SELECT A.REXP_CODE,A.HEXP_CODE,A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION, "+
                                   " A.DOSAGE_UNIT,A.PRICE OWN_PRICE,SUM(A.QTY) AS DOSAGE_QTY,SUM(A.TOTAL_AMT) AS TOT_AMT,C.UNIT_CHN_DESC, "+
//                                   " A.DOSAGE_UNIT,A.OWN_PRICE,SUM(A.DOSAGE_QTY) AS DOSAGE_QTY,SUM(A.TOT_AMT) AS TOT_AMT,C.UNIT_CHN_DESC, "+
                                   //===================================================
                                   " D.CHARGE_HOSP_DESC,E.CHN_DESC,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD') AS BILL_DATE,A.EXE_DEPT_CODE,F.COST_CENTER_CHN_DESC AS DEPT_CHN_DESC,B.ORDER_CAT1_CODE,A.BILL_DATE AS DATE_BILL,"+
                                   //=================================================================
                                   " B.NHI_CODE_I,A.HYGIENE_TRADE_CODE PZWH,A.OWN_RATE ZFBL1 " + //add by zhangzc 20120612 每日清单增加医保码、国药准字号、增付比例
//                                   " B.NHI_CODE_I,H.PZWH,H.ZFBL1 " + //add by zhangzc 20120612 每日清单增加医保码、国药准字号、增付比例
                                   //=================================================================
                                   //=====================================
                                   " FROM INS_IBS_ORDER A,SYS_FEE B,SYS_UNIT C,SYS_CHARGE_HOSP D,SYS_DICTIONARY E,SYS_COST_CENTER F "+
//                                   " FROM IBS_ORDD A,SYS_FEE B,SYS_UNIT C,SYS_CHARGE_HOSP D,SYS_DICTIONARY E,SYS_COST_CENTER F ,INS_RULE H "+
                                   //=====================================
                                   " WHERE A.CASE_NO='"+parm.getValue("CASE_NO")+"'" +
                                   where +
                                   //==================================
                                   " AND A.PRICE<>0 "+
//                                   " AND A.OWN_PRICE<>0 "+
                                   //==================================
                                   regionWhere+
                                   " AND A.ORDER_CODE=B.ORDER_CODE "+
                                   " AND B.CHARGE_HOSP_CODE = D.CHARGE_HOSP_CODE" + 
                                   " AND A.DOSAGE_UNIT = C.UNIT_CODE(+) "+
                                   " AND E.GROUP_ID='SYS_CHARGE' "+
                                   " AND D.IPD_CHARGE_CODE=E.ID "+
                                   " AND A.EXE_DEPT_CODE=F.COST_CENTER_CODE(+) "+
                                   //=====================================================================
//                                   " AND B.NHI_CODE_I = H.SFXMBM(+) " +//add by zhangzc 20120612
                                   insWhere+
                                   " GROUP BY D.IPD_CHARGE_CODE,D.CHARGE_HOSP_CODE," +
//                                   " GROUP BY A.REXP_CODE,A.HEXP_CODE," +
                                   //modify by liming 2012/02/06 begin
                                   "A.ORDER_CODE,B.ORDER_DESC,A.DOSAGE_UNIT,A.PRICE,A.EXE_DEPT_CODE,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),C.UNIT_CHN_DESC, "+
//                                   "A.ORDER_CODE,B.ORDER_DESC,A.DOSAGE_UNIT,A.OWN_PRICE,A.EXE_DEPT_CODE,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),C.UNIT_CHN_DESC, "+
                                   //===========================================================
                                   " D.CHARGE_HOSP_DESC,E.CHN_DESC,B.SPECIFICATION, "+
                                   //modify by liming 2012/02/06 end
                                   " F.COST_CENTER_CHN_DESC,B.ORDER_CAT1_CODE,A.BILL_DATE " + 
            				       // modify by zhangzc 2012/06/12 
            				       ",B.NHI_CODE_I ," +
            				       //=============================================================
            				       " A.HYGIENE_TRADE_CODE, A.OWN_RATE ";
//                    			   " H.PZWH,H.ZFBL1 ";
                    //=============================================================
                    String orderBy = "";
                    if(parm.getData("ORDER_BY1")!=null){
                    	//===zhangp 20120316 start
                    	//======================================================================
                        orderBy = " ORDER BY D.CHARGE_HOSP_CODE,A.ORDER_CODE ";
//                        orderBy = " ORDER BY A.HEXP_CODE,A.ORDER_CODE ";
                    }else if(parm.getData("ORDER_BY2")!=null){
                        orderBy = " ORDER BY D.IPD_CHARGE_CODE,A.ORDER_CODE ";
//                        orderBy = " ORDER BY A.REXP_CODE,A.ORDER_CODE ";
                    }else if(parm.getData("ORDER_BY3")!=null){
                        orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),D.CHARGE_HOSP_CODE,A.ORDER_CODE ";
//                        orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.HEXP_CODE,A.ORDER_CODE ";
                    }else if(parm.getData("ORDER_BY4")!=null){
                        orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),D.IPD_CHARGE_CODE,A.ORDER_CODE ";
//                        orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.REXP_CODE,A.ORDER_CODE ";
                        //======================================================================
                        //===zhangp 20120316 end
                    }
//                    result = BILDailyFeeQueryTool.getInstance().selectPrintData(
//                            parm);
//                    System.out.println(sql+orderBy);
                    result = new TParm(TJDODBTool.getInstance().select(sql+orderBy));
                 // ===================每日清单 非医保 liling=====================
            	}else{
                    String where = "";
                    if(parm.getValue("DATE_S").length()>0&&parm.getValue("DATE_E").length()>0){
                        where = " AND A.BILL_DATE BETWEEN TO_DATE('"+parm.getValue("DATE_S")+"'||'000000','YYYY/MM/DDHH24MISS') AND TO_DATE('"+parm.getValue("DATE_E")+"'||'235959','YYYY/MM/DDHH24MISS') ";
                    }
                    String regionWhere  = "";
                    if(!"".equals(parm.getValue("REGION_CODE")))
                        regionWhere = " AND F.REGION_CODE = '"+parm.getValue("REGION_CODE")+"' ";
                    regionWhere = " ";
                    String sql = "SELECT A.REXP_CODE,A.HEXP_CODE,A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION, "+
                                   " A.DOSAGE_UNIT,A.OWN_PRICE,SUM(A.DOSAGE_QTY) AS DOSAGE_QTY,SUM(A.OWN_AMT) AS OWN_AMT,SUM(A.TOT_AMT) AS TOT_AMT,C.UNIT_CHN_DESC, "+
                                   " D.CHARGE_HOSP_DESC,E.CHN_DESC,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD') AS BILL_DATE,A.EXE_DEPT_CODE,F.COST_CENTER_CHN_DESC AS DEPT_CHN_DESC,B.ORDER_CAT1_CODE,"+
                                   " B.NHI_CODE_I,A.CASE_NO " + //add by zhangzc 20120612 每日清单增加医保码、国药准字号、增付比例
                                   //==liling 20140806 add A.EXEC_DATE 执行时间
                                   " FROM IBS_ORDD A,SYS_FEE B,SYS_UNIT C,SYS_CHARGE_HOSP D,SYS_DICTIONARY E,SYS_COST_CENTER F "+
                                   " WHERE A.CASE_NO='"+parm.getValue("CASE_NO")+"'" +
                                   where +
                                   " AND A.OWN_PRICE<>0 "+
                                   regionWhere+
                                   " AND A.ORDER_CODE=B.ORDER_CODE "+
                                   " AND A.DOSAGE_UNIT = C.UNIT_CODE(+) "+
                                   " AND A.HEXP_CODE=D.CHARGE_HOSP_CODE "+
                                   " AND E.GROUP_ID='SYS_CHARGE' "+
                                   " AND A.REXP_CODE=E.ID "+
                                   " AND A.EXE_DEPT_CODE=F.COST_CENTER_CODE(+) "+
                                   " GROUP BY A.REXP_CODE,A.HEXP_CODE," +
                                   //modify by liming 2012/02/06 begin
                                   "A.ORDER_CODE,B.ORDER_DESC,A.DOSAGE_UNIT,A.OWN_PRICE,A.EXE_DEPT_CODE,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),C.UNIT_CHN_DESC, "+
                                   " D.CHARGE_HOSP_DESC,E.CHN_DESC,B.SPECIFICATION, "+
                                   //modify by liming 2012/02/06 end
                                   " F.COST_CENTER_CHN_DESC,B.ORDER_CAT1_CODE " + 
            				       // modify by zhangzc 2012/06/12 
            				       ",B.NHI_CODE_I,a.case_no   ";
                    String orderBy = "";
                    if(parm.getData("ORDER_BY1")!=null){
                    	//===zhangp 20120316 start
                        orderBy = " ORDER BY A.HEXP_CODE,A.ORDER_CODE ";
                    }else if(parm.getData("ORDER_BY2")!=null){
                        orderBy = " ORDER BY A.REXP_CODE,A.ORDER_CODE ";
                    }else if(parm.getData("ORDER_BY3")!=null){
                        orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.HEXP_CODE,A.ORDER_CODE ";
                    }else if(parm.getData("ORDER_BY4")!=null){
                        orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.REXP_CODE,A.ORDER_CODE ";
                        //===zhangp 20120316 end
                    }
                    result = new TParm(TJDODBTool.getInstance().select(sql+orderBy));
            	}
//===============================liling====================================================================================
            } else {
            	if(insParm.getCount("CONFIRM_NO")>0){//用天津医保
            		String where = "";
                    String insWhere = "";
                    if(parm.getValue("DATE_S").length()>0&&parm.getValue("DATE_E").length()>0){
                        where = " AND A.BILL_DATE BETWEEN TO_DATE('"+parm.getValue("DATE_S")+"'||'000000','YYYY/MM/DDHH24MISS') AND TO_DATE('"+parm.getValue("DATE_E")+"'||'235959','YYYY/MM/DDHH24MISS') ";
                        insWhere = 
                        	"";
                    }
                    String regionWhere  = "";
                    if(!"".equals(parm.getValue("REGION_CODE")))
                        regionWhere = " AND F.REGION_CODE = '"+parm.getValue("REGION_CODE")+"' ";
                    regionWhere = " ";
                    String sql = "SELECT D.IPD_CHARGE_CODE REXP_CODE,D.CHARGE_HOSP_CODE HEXP_CODE,A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION, "+
//                    		     "SELECT A.REXP_CODE,A.HEXP_CODE,A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION, "+
                    				//===zhangp 20120521 start
                    			   " A.DOSAGE_UNIT,A.PRICE OWN_PRICE,SUM(A.QTY) AS DOSAGE_QTY,SUM(A.TOTAL_AMT) AS TOT_AMT,C.UNIT_CHN_DESC, "+
//                                   " A.DOSAGE_UNIT,A.OWN_PRICE,SUM(A.DOSAGE_QTY) AS DOSAGE_QTY,SUM(A.TOT_AMT) AS TOT_AMT,C.UNIT_CHN_DESC,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD') AS BILL_DATE, "+
//                                   " D.CHARGE_HOSP_DESC,E.CHN_DESC,A.EXE_DEPT_CODE,F.COST_CENTER_CHN_DESC AS DEPT_CHN_DESC,B.ORDER_CAT1_CODE,A.DR_CODE,G.USER_NAME,A.BILL_DATE AS DATE_BILL "+
                                   //===zhangp 20120521 end
                    			   " D.CHARGE_HOSP_DESC,E.CHN_DESC,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD') AS BILL_DATE,A.EXE_DEPT_CODE,F.COST_CENTER_CHN_DESC AS DEPT_CHN_DESC,B.ORDER_CAT1_CODE, "+
//                                   " D.CHARGE_HOSP_DESC,E.CHN_DESC,A.EXE_DEPT_CODE,F.COST_CENTER_CHN_DESC AS DEPT_CHN_DESC,B.ORDER_CAT1_CODE, "+
                    			   " B.NHI_CODE_I,A.HYGIENE_TRADE_CODE PZWH,A.OWN_RATE ZFBL1 " + //add by zhangzc 20120612 每日清单增加医保码、国药准字号、增付比例
//                                   " B.NHI_CODE_I ,H.PZWH,H.ZFBL1 " + //add by zhangzc 20120612 每日清单增加医保码、国药准字号、增付比例
                    			   " FROM INS_IBS_ORDER A,SYS_FEE B,SYS_UNIT C,SYS_CHARGE_HOSP D,SYS_DICTIONARY E,SYS_COST_CENTER F "+
//                                   " FROM IBS_ORDD A,SYS_FEE B,SYS_UNIT C,SYS_CHARGE_HOSP D,SYS_DICTIONARY E,SYS_COST_CENTER F ,INS_RULE H "+
                                   " WHERE A.CASE_NO='"+parm.getValue("CASE_NO")+"'" +
                                   where +
                                   " AND A.PRICE<>0 "+
//                                   " AND A.OWN_PRICE<>0 "+
                                   regionWhere+
                                   " AND A.ORDER_CODE=B.ORDER_CODE "+
                                   " AND A.DOSAGE_UNIT = C.UNIT_CODE(+) "+
                                   " AND B.CHARGE_HOSP_CODE=D.CHARGE_HOSP_CODE "+
                                   " AND E.GROUP_ID='SYS_CHARGE' "+
                                   " AND D.IPD_CHARGE_CODE=E.ID "+
                                   " AND A.EXE_DEPT_CODE=F.COST_CENTER_CODE(+) "+
//                                   " AND B.NHI_CODE_I = H.SFXMBM(+) " +//add by zhangzc 20120612
                                   insWhere+
                                   " GROUP BY D.IPD_CHARGE_CODE,E.CHN_DESC,D.CHARGE_HOSP_CODE,D.CHARGE_HOSP_DESC," +
//                                   " GROUP BY A.REXP_CODE,A.HEXP_CODE," +
                                   //modify by liming begin
                                   " A.ORDER_CODE,B.ORDER_DESC,A.DOSAGE_UNIT,A.PRICE,A.EXE_DEPT_CODE, "+
//                                   "A.ORDER_CODE,B.ORDER_DESC,A.DOSAGE_UNIT,A.OWN_PRICE,A.EXE_DEPT_CODE,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),C.UNIT_CHN_DESC, "+
                                   " TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),C.UNIT_CHN_DESC,B.SPECIFICATION, "+
                                   //modify by liming end
                   					//===zhangp 20120521 start
//                                   " A.DOSAGE_UNIT,F.COST_CENTER_CHN_DESC,B.ORDER_CAT1_CODE,A.DR_CODE,G.USER_NAME,A.BILL_DATE ";
                    			   " A.DOSAGE_UNIT,F.COST_CENTER_CHN_DESC,B.ORDER_CAT1_CODE " +
                    			   // modify by zhangzc 2012/06/12 
                    			   " ,B.NHI_CODE_I,A.HYGIENE_TRADE_CODE, A.OWN_RATE ";
//            				       ",B.NHI_CODE_I,H.PZWH,H.ZFBL1 ";
                    //===zhangp 20120521 end
                    String orderBy = "";
                    if(parm.getData("ORDER_BY1")!=null){
                    	 orderBy = " ORDER BY D.IPD_CHARGE_CODE,A.ORDER_CODE ";
//                        orderBy = " ORDER BY A.REXP_CODE,A.ORDER_CODE ";
                    }else if(parm.getData("ORDER_BY2")!=null){
                    	orderBy = " ORDER BY D.CHARGE_HOSP_CODE,A.ORDER_CODE ";
//                        orderBy = " ORDER BY A.HEXP_CODE,A.ORDER_CODE ";
                    }else if(parm.getData("ORDER_BY3")!=null){
                    	orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),D.IPD_CHARGE_CODE,A.ORDER_CODE ";
//                        orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.REXP_CODE,A.ORDER_CODE ";
                    }else if(parm.getData("ORDER_BY4")!=null){
                    	orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),D.CHARGE_HOSP_CODE,A.ORDER_CODE ";
//                        orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.HEXP_CODE,A.ORDER_CODE ";
                    }
//                    System.out.println("ssssssssssssssssss" + sql+orderBy);
//                  result = BILDailyFeeQueryTool.getInstance().
//                           selectPrintDataForHZ(parm);
                  result = new TParm(TJDODBTool.getInstance().select(sql+orderBy));
            	}else{
            		String where = "";
                    if(parm.getValue("DATE_S").length()>0&&parm.getValue("DATE_E").length()>0){
                        where = " AND A.BILL_DATE BETWEEN TO_DATE('"+parm.getValue("DATE_S")+"'||'000000','YYYY/MM/DDHH24MISS') AND TO_DATE('"+parm.getValue("DATE_E")+"'||'235959','YYYY/MM/DDHH24MISS') ";
                    }
                    String regionWhere  = "";
                    if(!"".equals(parm.getValue("REGION_CODE")))
                        regionWhere = " AND F.REGION_CODE = '"+parm.getValue("REGION_CODE")+"' ";
                    regionWhere = " ";
                    String sql = "SELECT A.REXP_CODE,A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION, "+
                    				//===zhangp 20120521 start  //===liling add SUM(A.OWN_AMT) AS OWN_AMT应收金额   A.CASE_NO
                                   " A.DOSAGE_UNIT,A.OWN_PRICE,SUM(A.DOSAGE_QTY) AS DOSAGE_QTY,SUM(A.OWN_AMT) AS OWN_AMT,SUM(A.TOT_AMT) AS TOT_AMT,C.UNIT_CHN_DESC,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD') AS BILL_DATE, "+
//                                   " D.CHARGE_HOSP_DESC,E.CHN_DESC,A.EXE_DEPT_CODE,F.COST_CENTER_CHN_DESC AS DEPT_CHN_DESC,B.ORDER_CAT1_CODE,A.DR_CODE,G.USER_NAME,A.BILL_DATE AS DATE_BILL "+
                                   //===zhangp 20120521 end
                                   " D.CHARGE_HOSP_DESC,E.CHN_DESC,A.EXE_DEPT_CODE,F.COST_CENTER_CHN_DESC AS DEPT_CHN_DESC,B.ORDER_CAT1_CODE, "+
                                   " B.NHI_CODE_I,'' PZWH,'' ZFBL1,a.include_flg,A.CASE_NO " + //add by zhangzc 20120612 每日清单增加医保码、国药准字号、增付比例//==liling 20140519 add 包干內字段a.include_flg
                                   " FROM IBS_ORDD A,SYS_FEE B,SYS_UNIT C,SYS_CHARGE_HOSP D,SYS_DICTIONARY E,SYS_COST_CENTER F "+
                                   " WHERE A.CASE_NO='"+parm.getValue("CASE_NO")+"'" +
                                   where +
                                   " AND A.OWN_PRICE<>0 "+
                                   regionWhere+
                                   " AND A.ORDER_CODE=B.ORDER_CODE "+
                                   " AND A.DOSAGE_UNIT = C.UNIT_CODE(+) "+
                                   " AND A.HEXP_CODE=D.CHARGE_HOSP_CODE "+
                                   " AND E.GROUP_ID='SYS_CHARGE' "+
                                   " AND A.REXP_CODE=E.ID "+
                                   " AND A.EXE_DEPT_CODE=F.COST_CENTER_CODE(+) "+
                                   " GROUP BY A.REXP_CODE,E.CHN_DESC,D.CHARGE_HOSP_DESC," +
                                   //modify by liming begin
                                   "A.ORDER_CODE,B.ORDER_DESC,A.DOSAGE_UNIT,A.OWN_PRICE,A.EXE_DEPT_CODE,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),C.UNIT_CHN_DESC, "+
//                                   "A.ORDER_CODE,B.ORDER_DESC,A.DOSAGE_UNIT,A.OWN_PRICE,A.EXE_DEPT_CODE,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),C.UNIT_CHN_DESC, "+
                                   " B.SPECIFICATION, "+
                                   //modify by liming end
                   					//===zhangp 20120521 start
//                                   " A.DOSAGE_UNIT,F.COST_CENTER_CHN_DESC,B.ORDER_CAT1_CODE,A.DR_CODE,G.USER_NAME,A.BILL_DATE ";
                    			   " A.DOSAGE_UNIT,F.COST_CENTER_CHN_DESC,B.ORDER_CAT1_CODE " +
                    			   // modify by zhangzc 2012/06/12 
            				       ",B.NHI_CODE_I,a.include_flg,a.case_no  ";//==liling 20140519 add 包干內字段a.include_flg ,a.case_no
                    String sql1="SELECT   a.rexp_code, a.hexp_code, a.order_code, b.order_desc,b.SPECIFICATION,c.unit_chn_desc,"
                        +" SUM (a.dosage_qty) AS dosage_qty, TO_CHAR (a.bill_date, 'YYYY/MM/DD') AS bill_date,"
                        +" d.charge_hosp_desc, e.chn_desc, a.exe_dept_code, f.cost_center_chn_desc AS dept_chn_desc, b.order_cat1_code,"
                        +" b.nhi_code_i, '' pzwh, '' zfbl1, a.include_flg "
                        +" FROM ibs_ordd a, sys_fee b,  sys_charge_hosp d, sys_dictionary e, sys_cost_center f, sys_unit c "
                        +" WHERE a.case_no = '"+parm.getValue("CASE_NO")+"'" + where + regionWhere
                        // ( A.INCLUDE_FLG IS NULL OR  A.INCLUDE_FLG ='N' )//==liling 20140527   A.INCLUDE_FLG='Y'为包干外项目 
                        +" AND a.own_price <> 0 AND ( A.INCLUDE_FLG IS NULL OR  A.INCLUDE_FLG ='N' )   AND a.order_code = b.order_code "
                        +"  AND a.hexp_code = d.charge_hosp_code AND e.GROUP_ID = 'SYS_CHARGE' "
                        +" AND a.rexp_code = e.ID AND a.exe_dept_code = f.cost_center_code(+) AND a.dosage_unit = c.unit_code(+) "
                        +" GROUP BY a.rexp_code, e.chn_desc,a.hexp_code,d.charge_hosp_desc, a.order_code,  "
                        +" b.order_desc,  a.exe_dept_code, TO_CHAR (a.bill_date, 'YYYY/MM/DD'),b.SPECIFICATION,"
                        +" f.cost_center_chn_desc,  b.order_cat1_code, b.nhi_code_i, a.include_flg,c.unit_chn_desc  ";//套餐内费用清单sql liling  add  20140519
                    String sql2="SELECT   a.rexp_code, a.hexp_code, a.order_code, b.order_desc,b.SPECIFICATION, a.dosage_unit, a.own_price,"
                        +" SUM (a.dosage_qty) AS dosage_qty,SUM(A.OWN_AMT) AS OWN_AMT, SUM (a.tot_amt) AS tot_amt, c.unit_chn_desc, TO_CHAR (a.bill_date, 'YYYY/MM/DD') AS bill_date,"
                        +" d.charge_hosp_desc, e.chn_desc, a.exe_dept_code, f.cost_center_chn_desc AS dept_chn_desc, b.order_cat1_code,"
                        +" b.nhi_code_i, '' pzwh, '' zfbl1, a.include_flg,A.CASE_NO "
                        +" FROM ibs_ordd a, sys_fee b, sys_unit c, sys_charge_hosp d, sys_dictionary e, sys_cost_center f "
                        +" WHERE a.case_no = '"+parm.getValue("CASE_NO")+"'" + where + regionWhere
//                        +" AND a.own_price <> 0 AND (A.INCLUDE_FLG='N' or A.INCLUDE_FLG is null)  AND a.order_code = b.order_code "
                        +" AND a.own_price <> 0 AND A.INCLUDE_FLG='Y'  AND a.order_code = b.order_code "
                        +" AND a.dosage_unit = c.unit_code(+) AND a.hexp_code = d.charge_hosp_code AND e.GROUP_ID = 'SYS_CHARGE' "
                        +" AND a.rexp_code = e.ID AND a.exe_dept_code = f.cost_center_code(+) "
                        +" GROUP BY a.rexp_code,e.chn_desc, d.charge_hosp_desc,  a.hexp_code, " 
                        +" a.order_code, b.order_desc, a.dosage_unit, a.own_price, a.exe_dept_code,"
                        +" TO_CHAR (a.bill_date, 'YYYY/MM/DD'), c.unit_chn_desc,  b.SPECIFICATION, a.dosage_unit,"
                        +" f.cost_center_chn_desc,  b.order_cat1_code, b.nhi_code_i, a.include_flg,A.CASE_NO  ";//套餐外费用清单sql liling  add  20140519
                    String sql3="SELECT   a.rexp_code, a.hexp_code, a.order_code, b.TRADE_ENG_DESC as order_desc," +
                    		" b.SPECIFICATION, a.dosage_unit, a.own_price,SUM (a.dosage_qty) AS dosage_qty, SUM (a.own_amt) AS own_amt," +
                    		" SUM (a.tot_amt) AS tot_amt,TO_CHAR (a.bill_date, 'YYYY/MM/DD') AS bill_date, d.charge_hosp_desc," +
                    		" e.ENG_DESC as chn_desc, a.exe_dept_code, b.nhi_code_i, a.case_no " +
                    		"FROM ibs_ordd a, sys_fee b,sys_charge_hosp d,sys_dictionary e,sys_cost_center f " +
                    		" WHERE a.case_no = '"+parm.getValue("CASE_NO")+"'  AND a.own_price <> 0 AND a.order_code = b.order_code " +
                    		" AND a.hexp_code = d.charge_hosp_code  AND e.GROUP_ID = 'SYS_CHARGE'  AND a.rexp_code = e.ID " +
                    		" AND a.exe_dept_code = f.cost_center_code(+) " +
                    		"GROUP BY a.rexp_code,e.ENG_DESC,a.hexp_code,d.charge_hosp_desc, " +
                    		" a.order_code, a.dosage_unit, a.own_price, a.exe_dept_code," +
                    		" a.case_no,b.TRADE_ENG_DESC, b.SPECIFICATION, b.nhi_code_i,TO_CHAR (a.bill_date, 'YYYY/MM/DD')";//英文费用清单sql liling add 20140625
                    String sql4=" SELECT m.UNIT_ENG_DESC,m.CONTRACTOR_DESC,n.INSURANCE_NUMBER,n.INSURANCE_BILL_NUMBER " +
                    		"FROM MEM_CONTRACTOR m,  MEM_INSURE_INFO n WHERE n.MR_NO='"+table.getItemString(i, "MR_NO")+
                    		"' AND m.CONTRACTOR_CODE=n.CONTRACTOR_CODE  AND n.VALID_FLG='Y'";//保险信息sql liling add 20140625
                    //===zhangp 20120521 end
                    String orderBy = "";
                    if(parm.getData("ORDER_BY1")!=null){
                        orderBy = " ORDER BY A.HEXP_CODE,A.ORDER_CODE ";
                    }else if(parm.getData("ORDER_BY2")!=null){
                        orderBy = " ORDER BY A.REXP_CODE,A.ORDER_CODE ";
                    }else if(parm.getData("ORDER_BY3")!=null){
                        orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.REXP_CODE,A.ORDER_CODE ";
                    }else if(parm.getData("ORDER_BY4")!=null){
                        orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.HEXP_CODE,A.ORDER_CODE ";
                    }
//                    System.out.println("sql33+orderBy======="+sql3+orderBy);
//                    System.out.println("sql44444======="+sql4);
                  result = new TParm(TJDODBTool.getInstance().select(sql+orderBy));
//                  System.out.println("sql + orderBy = = = " + sql + orderBy);
//                  System.out.println("result = = = = = " + result);
                  result1 = new TParm(TJDODBTool.getInstance().select(sql1+orderBy));//套餐内费用清单 liling  add  20140519
                  result2 = new TParm(TJDODBTool.getInstance().select(sql2+orderBy));//套餐外费用清单 liling  add  20140519
                  result3 = new TParm(TJDODBTool.getInstance().select(sql3+orderBy));//英文费用清单 liling add 20140625
                  result4 = new TParm(TJDODBTool.getInstance().select(sql4));//保险信息  liling add 20140625
            	}
//===============================================================================================================
            }
            
            if (result.getErrCode() < 0) {
                this.messageBox("E0005");
                return;
            }
            TParm print = new TParm();
            //判断打印清单的格式
            Boolean dFlg=false;
            if (this.getValueBoolean("PRINT_TYPE1")) { //汇总清单
                print = getSumPrintData(result);
                dFlg=false;
            } else if (this.getValueBoolean("PRINT_TYPE2")) { //每日清单
                print = getDayPrintData(result);
                dFlg=true;
 //====================liling  add  20140519   start==================
			} else if (this.getValueBoolean("PRINT_TYPE3")) { // 套餐费用清单
				TParm parm1 = new TParm(TJDODBTool.getInstance().select(
						"SELECT M.PACKAGE_DESC AS LUMPWORK_DESC,A.CASE_NO FROM MEM_PACKAGE M,ADM_INP A WHERE A.CASE_NO='"
								+ CASE_NO
								+ "' AND A.LUMPWORK_CODE=M.PACKAGE_CODE "));
				String lumpDesc = parm1.getValue("LUMPWORK_DESC", 0);
				// this.messageBox(lumpDesc);
				if (lumpDesc.length() == 0 || lumpDesc.equals("")) {
					this.messageBox("此病患不存在套餐信息");
					return;
				}
				TParm selectParm = (TParm) this
						.openDialog("%ROOT%\\config\\bil\\BILDaily.x");
				// System.out.println("selectParm==="+selectParm);
				String includeFlg = selectParm.getValue("INCLUDE_FLG")
						.substring(1, 2);
				String outsideFlg = selectParm.getValue("OUTSIDE_FLG")
						.substring(1, 2);
				// System.out.println("includeFlg==="+includeFlg);
				// System.out.println("outsideFlg==="+outsideFlg);
				// if (this.getValueBoolean("IN")) {
				// this.messageBox("此病患尚未出院");
				// return;
				// }
				// TParm parm2=new
				// TParm(TJDODBTool.getInstance().select("SELECT * FROM BIL_IBS_RECPM WHERE CASE_NO='"+CASE_NO+"' "));
				// if(parm2.getCount()<=0){
				// this.messageBox("此病患尚未打票");
				// return;
				// }
				TParm print1 = getSumPrintData1(result1);// 套餐内明细
				TParm print2 = getSumPrintData(result2);// 套餐外明细 调用汇总清单返回值方法
				// getLumpworkPrintData(result);
				double yjj = getYJJ(CASE_NO); // 预交金
				TParm inpData = BILDailyFeeQueryTool.getInstance()
						.selectInpInfo(CASE_NO);
				Pat pat = Pat.onQueryByMrNo(table.getItemString(i, "MR_NO"));
				TParm printData = new TParm();
				
				printData.setData("MR_NO", "TEXT", table.getItemString(i,
						"MR_NO"));// liling Operator.getHospitalCHNShortName() +
				printData.setData("IPD_NO", "TEXT", table.getItemString(i,
						"IPD_NO"));
				printData.setData("filePatName", "TEXT", table.getItemString(i,
						"PAT_NAME"));
				printData.setData("SEX", "TEXT", pat.getSexString());
				String birthday = pat.getBirthday().toString().substring(0, 10)
						.replace("-", "/");
				printData.setData("Birthday", "TEXT", birthday);
				printData.setData("PAT_NAME", "TEXT", table.getItemString(i,
						"PAT_NAME"));
				printData.setData("OPTER", "TEXT", Operator.getID());// liling
																		// filePatName
				printData.setData("PRICE", yjj);
				printData.setData("AGE", DateUtil.showAge(pat.getBirthday(),
						inpData.getTimestamp("IN_DATE", 0)));
				printData.setData("DEPT", "TEXT", inpData.getValue(
						"DEPT_CHN_DESC", 0));
				printData.setData("STATION", "TEXT", inpData.getValue(
						"STATION_DESC", 0));
				printData.setData("BED","TEXT", inpData.getValue("BED_NO_DESC", 0));
				printData.setData("CTZ", inpData.getValue("CTZ_DESC", 0));
				printData.setData("LUMPWORK_DESC", lumpDesc);
				int Count = result.getCount("TOT_AMT");
				DecimalFormat df = new DecimalFormat("0.00");
				double totFee = 0.00;
				double outTotFee = 0.00;
				double outOwnFee = 0.00;
				for (int j = 0; j < Count; j++) {
					if (result.getValue("INCLUDE_FLG", j).equals("Y")) {
						// totFee =add( totFee, result.getDouble("TOT_AMT", j));
						outTotFee += Double.valueOf(df.format(result.getDouble(
								"TOT_AMT", j)));
						outOwnFee += Double.valueOf(df.format(result.getDouble(
								"OWN_AMT", j)));
					} else {
						// outFee=add(outFee,result.getDouble("TOT_AMT", j));
						totFee += Double.valueOf(df.format(result.getDouble(
								"TOT_AMT", j)));

					}
				}// dosageQty += Double.valueOf(df.format());DecimalFormat df =
					// new DecimalFormat("0.00");
				// printData.setData("TOT_AMT", df.format(totFee));//套餐总金额
				// printData.setData("OUT_AMT",df.format(outTotFee));//套餐外实收金额
				// printData.setData("OWN_AMT",df.format(outOwnFee));//套餐外应收金额
				// printData.setData("TITLE1", "套餐内项目清单");

				if (startDate.length() == 0 || startDate.equals("")
						|| endDate.length() == 0 || endDate.equals("")) {
					startDate = StringTool.getString(table.getItemTimestamp(i,
							"IN_DATE"), "yyyy/MM/dd");
					endDate = StringTool.getString(table.getItemTimestamp(i,
							"DS_DATE"), "yyyy/MM/dd");
				}
				printData.setData("DATE", "TEXT", startDate + " 至 " + endDate);
				printData.setData("NOW", "TEXT", StringTool.getString(
						SystemTool.getInstance().getDate(),
						"yyyy/MM/dd HH:mm:ss"));
				if (includeFlg.equals("Y")) {
					printData.setData("TITLE", "TEXT", "住院套餐费用清单（套内）");// liling
					printData.setData("TABLEIN", print1.getData());// 套餐内明细
					printData.setData("DESC_I", "合计:");
					printData.setData("TOT_AMT", df.format(totFee));// 套餐总金额
					printData.setData("TOT_AMT", df.format(onSelectLupAmt(parm
							.getValue("CASE_NO"))));// 套餐总金额yanjing 20150304
				} else {
					TParm s = new TParm();
					s.setData("Visible", false);
					printData.setData("TABLEIN", s.getData());
				}
				if (outsideFlg.equals("Y")) {
					printData.setData("TITLE", "TEXT", "住院套餐费用清单（套外）");// liling
					printData.setData("TITLE3", "其他消费");
					printData.setData("TABLEOUT", print2.getData());// 套餐外明细
					printData.setData("DESC_O", "合计:");
					String sql="SELECT  RECEIPT_NO,REDUCE_AMT FROM BIL_IBS_RECPM WHERE REFUND_CODE IS NULL  AND AR_AMT >0 AND CASE_NO = '" +CASE_NO+"' ORDER BY RECEIPT_NO DESC";
			    	TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql));
					//===start==== modify by kangy 20161009	套外费用清单总实收金额需减去减免金额
					if(parm2.getDouble("REDUCE_AMT", 0) != 0){
						printData.setData("OUT_AMT", df.format(print2
								.getDouble("L_TOT_AMT")-parm2.getDouble("REDUCE_AMT", 0)));// 套餐外实收金额
					}else{
						printData.setData("OUT_AMT", df.format(print2
								.getDouble("L_TOT_AMT")));// 套餐外实收金额
					}
					//===end==== modify by kangy 20161009	套外费用清单总实收金额需减去减免金额
					/*printData.setData("OUT_AMT", df.format(print2
							.getDouble("L_TOT_AMT")));// 套餐外实收金额
*/					printData.setData("OWN_AMT", df.format(print2
							.getDouble("L_OWN_AMT")));// 套餐外应收金额

				} else {
					TParm s = new TParm();
					s.setData("Visible", false);
					printData.setData("TITLE3", "");
					printData.setData("TABLEOUT", s.getData());
				}
				if (outsideFlg.equals("Y")&&includeFlg.equals("Y")) {
					printData.setData("TITLE", "TEXT", "住院套餐费用清单（全部）");// pangben 2015-8-4
				}
				if (this.getValueBoolean("PRINT_SUM")) {
					if (this.getValueBoolean("FEE_TYPE1")) {
						printData.setData("SUM", getFeeType1Sum(result)
								.getData());
					} else if (this.getValueBoolean("FEE_TYPE2")) {
						printData.setData("SUM", getFeeType2Sum(result)
								.getData());
					}
					printData.setData("TITLE2", "住院费用清单汇总");// Operator.getHospitalCHNShortName()
															// +
					printData.setData("DATE2", "日期：" + startDate + "至"
							+ endDate);
				} else {
					TParm s = new TParm();
					s.setData("Visible", false);
					printData.setData("SUM", s.getData());
				}
				// this.openPrintDialog(
				// "%ROOT%\\config\\prt\\BIL\\BILLumpworkFee_V45.jhw",
				// printData,
				// isView);
				// 20141223 wangjingchun add start 854
				String patinfo_sql = "SELECT VALID_FLG, DEPT_FLG FROM MEM_INSURE_INFO WHERE MR_NO = '"
						+ table.getItemString(i, "MR_NO") + "' ";
				TParm patinfo_parm = new TParm(TJDODBTool.getInstance().select(
						patinfo_sql));
				int i1 = 1;
				if (patinfo_parm.getValue("DEPT_FLG", 0).equals("N")
						&& patinfo_parm.getValue("VALID_FLG", 0).equals("Y")) {
					i1 = 2;
				}
				for (int j = 0; j < i1; j++) {
					this.openPrintDialog(IReportTool.getInstance()
							.getReportPath("BILLumpworkFee.jhw"), IReportTool
							.getInstance().getReportParm(
									"BILDailyFeeQuery.class", printData),
							isView);
				}
				// 20141223 wangjingchun add end 854
				return;
			}else if (this.getValueBoolean("PRINT_TYPE4")) {//英文费用清单
//    	((TCheckBox) getComponent("PRINT_SUM")).setEnabled(false);//汇总复选框不可勾选
    	 print = getSumPrintData2(result3);
    	 TParm inpData = BILDailyFeeQueryTool.getInstance().selectInpInfo( CASE_NO);
         Pat pat = Pat.onQueryByMrNo(table.getItemString(i, "MR_NO"));
         TParm printData = new TParm();
         printData.setData("TABLE", print.getData());
         printData.setData("InsNum","TEXT", result4.getValue("INSURANCE_NUMBER", 0));//保险卡号  
         printData.setData("InsCom","TEXT", result4.getValue("UNIT_ENG_DESC", 0));//保险公司  
         printData.setData("MR_NO","TEXT", table.getItemString(i, "MR_NO"));//liling 
//         printData.setData("SEX","TEXT", pat.getSexString());
         String birthday=pat.getBirthday().toString().substring(0, 10).replace("-", "/");
         printData.setData("Birthday","TEXT",birthday);
         printData.setData("PAT_NAME","TEXT", table.getItemString(i, "PAT_NAME"));
         printData.setData("AGE", DateUtil.showAge(pat.getBirthday(),inpData.getTimestamp("IN_DATE", 0)));
//         printData.setData("DEPT","TEXT", inpData.getValue("DEPT_CHN_DESC", 0));
//         printData.setData("STATION","TEXT", inpData.getValue("STATION_DESC", 0));
//         printData.setData("BED", inpData.getValue("BED_NO_DESC", 0));
//         printData.setData("CTZ", inpData.getValue("CTZ_DESC", 0));
         //=======liling start========
         int Count = result.getCount("TOT_AMT");
         DecimalFormat df = new DecimalFormat("#############0.00");
 		double totFee = 0.00;//实收金额
 		double ownFee = 0.00;//应收金额
 		String sql0="SELECT  RECEIPT_NO,REDUCE_AMT FROM BIL_IBS_RECPM WHERE REFUND_CODE IS NULL  AND AR_AMT >0 AND CASE_NO = '" +CASE_NO+
						"' ORDER BY RECEIPT_NO DESC";
 		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql0));
 		for (int  j= 0; j < Count; j++) {
 			totFee += Double.valueOf(df.format( result.getDouble("TOT_AMT", j)));
 			ownFee += Double.valueOf(df.format( result.getDouble("OWN_AMT", j)));
 		}
 		if (parm1.getDouble("REDUCE_AMT", 0) != 0){
 			totFee =totFee - parm1.getDouble("REDUCE_AMT", 0);
 		}
 		printData.setData("TOT_AMT", df.format(totFee));//实收金额  宏
 		printData.setData("OWN_AMT",df.format(ownFee));//应收金额
 		printData.setData("TOT_AMT1","TEXT", new java.text.DecimalFormat("###,##0.00").format(totFee));//实收金额    图层整数部分每三位加一逗号隔开的形式
         if(startDate.length()==0 || startDate.equals("") || endDate.length()==0 || endDate.equals("")){
         	startDate = StringTool.getString(table.getItemTimestamp(i,"IN_DATE"), "yyyy/MM/dd");
             endDate = StringTool.getString(table.getItemTimestamp(i,"DS_DATE"), "yyyy/MM/dd");
         }           
         printData.setData("DATE","TEXT", "From "+startDate + " Through " + endDate);
         printData.setData("NOW","TEXT", StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd"));//"yyyy/MM/dd HH:mm:ss"
//         if (this.getValueBoolean("PRINT_SUM")) {
//             if (this.getValueBoolean("FEE_TYPE1")) {
//                 printData.setData("SUM", getFeeType1Sum(result).getData());
//             } else if (this.getValueBoolean("FEE_TYPE2")) {
//                 printData.setData("SUM", getFeeType2Sum(result).getData());
//             }
//             printData.setData("TITLE2","住院费用清单汇总");//Operator.getHospitalCHNShortName() +
//             printData.setData("DATE2", "日期：" + startDate + " 至 " + endDate);
//         } else {
//             TParm s = new TParm();
//             s.setData("Visible", false);
//             printData.setData("SUM", s.getData());
//         }
//         System.out.println("printData========"+printData);
       //20141223 wangjingchun add start 854
 		String patinfo_sql = "SELECT VALID_FLG, DEPT_FLG FROM MEM_INSURE_INFO WHERE MR_NO = '"+table.getItemString(i, "MR_NO")+"' ";
 		TParm patinfo_parm = new TParm(TJDODBTool.getInstance().select(patinfo_sql));
 		int i2 = 1;
 		if(patinfo_parm.getValue("DEPT_FLG",0).equals("N")&&patinfo_parm.getValue("VALID_FLG",0).equals("Y")){
 			i2=2;
 		}
 		for(int j=0;j<i2;j++){
 			this.openPrintDialog(IReportTool.getInstance().getReportPath("BILDailyFeeQueryEN.jhw"),
 					IReportTool.getInstance().getReportParm("BILDailyFeeQuery.class", printData),
 					isView);
 		}
 		//20141223 wangjingchun add end 854
          return;
    }
//====================liling  add  20140519   end==================
            double yjj = getYJJ(CASE_NO); //预交金
            TParm inpData = BILDailyFeeQueryTool.getInstance().selectInpInfo(
                    CASE_NO);
            Pat pat = Pat.onQueryByMrNo(table.getItemString(i, "MR_NO"));
            TParm printData = new TParm();
//            printData.setData("TABLE", print.getData());
//            printData.setData("TITLE","TEXT", "住院费用清单");//liling 
            printData.setData("MR_NO","TEXT", table.getItemString(i, "MR_NO"));//liling   Operator.getHospitalCHNShortName() + 
            printData.setData("IPD_NO","TEXT", table.getItemString(i, "IPD_NO"));
           // printData.setData("filePatName","TEXT", table.getItemString(i, "PAT_NAME"));
            printData.setData("SEX","TEXT", pat.getSexString());
            String birthday=pat.getBirthday().toString().substring(0, 10).replace("-", "/");
            printData.setData("Birthday","TEXT",birthday);
            printData.setData("PAT_NAME","TEXT", table.getItemString(i, "PAT_NAME"));
            printData.setData("OPTER","TEXT", Operator.getID());//liling  filePatName
//            printData.setData("AGE",
//                              StringUtil.showAge(pat.getBirthday(),
//                                                 inpData.
//                                                 getTimestamp("IN_DATE", 0)));
            printData.setData("AGE", DateUtil.showAge(pat.getBirthday(),inpData.getTimestamp("IN_DATE", 0)));
            printData.setData("DEPT","TEXT", inpData.getValue("DEPT_CHN_DESC", 0));
            printData.setData("STATION","TEXT", inpData.getValue("STATION_DESC", 0));
            printData.setData("BED", "TEXT",inpData.getValue("BED_NO_DESC", 0));	//床号
            printData.setData("CTZ", inpData.getValue("CTZ_DESC", 0));
            //=======liling start========
            int Count = result.getCount("TOT_AMT");
            DecimalFormat df = new DecimalFormat("#############0.00");
    		double totFee = 0.00;//实收金额
    		double ownFee = 0.00;//应收金额
    		String sql0="SELECT  RECEIPT_NO,REDUCE_AMT FROM BIL_IBS_RECPM WHERE REFUND_CODE IS NULL  AND AR_AMT >0 AND CASE_NO = '" +CASE_NO+
						"' ORDER BY RECEIPT_NO DESC";//查询减免总金额
    		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql0));
    		for (int  j= 0; j < Count; j++) {
    			totFee += Double.valueOf(df.format( result.getDouble("TOT_AMT", j)));
    			ownFee += Double.valueOf(df.format( result.getDouble("OWN_AMT", j)));
    		}
    		if (parm1.getDouble("REDUCE_AMT", 0) != 0){
    			totFee =totFee - parm1.getDouble("REDUCE_AMT", 0);
    		}
    		printData.setData("TOT_AMT", df.format(totFee));//实收金额
    		printData.setData("OWN_AMT",df.format(ownFee));//应收金额
    		//=========liling end===============
            //caowl 20130615 start
            if(startDate.length()==0 || startDate.equals("") || endDate.length()==0 || endDate.equals("")){
            	startDate = StringTool.getString(table.getItemTimestamp(i,
                "IN_DATE"), "yyyy/MM/dd");
                endDate = StringTool.getString(table.getItemTimestamp(i,
                "DS_DATE"), "yyyy/MM/dd");
            }           
            //caowl 20130615 end
            printData.setData("DATE","TEXT", startDate + " 至 " + endDate);
            printData.setData("NOW","TEXT", StringTool.getString(SystemTool.getInstance().
                    getDate(), "yyyy/MM/dd HH:mm:ss"));
            if (this.getValueBoolean("PRINT_SUM")) {
                if (this.getValueBoolean("FEE_TYPE1")) {
                    printData.setData("SUM", getFeeType1Sum(result).getData());
                } else if (this.getValueBoolean("FEE_TYPE2")) {
                    printData.setData("SUM", getFeeType2Sum(result).getData());
                }
                printData.setData("TITLE2","住院费用清单汇总");//Operator.getHospitalCHNShortName() +
                printData.setData("DATE2", "日期：" + startDate + " 至 " + endDate);
            } else {
                TParm s = new TParm();
                s.setData("Visible", false);
                printData.setData("SUM", s.getData());
            }
//            this.openPrintDialog(
//                    "%ROOT%\\config\\prt\\BIL\\BILDailyFeeQuery.jhw", printData,
//                    isView);
            //===liling 20140805 modify start=========
            if (dFlg) { //每日清单
//            	this.messageBox("每日清单11111");
            	printData.setData("TABLE", print.getData());
            	printData.setData("TITLE","TEXT", "住院费用每日清单");//liling 
            	//20141223 wangjingchun add start 854
         		String patinfo_sql = "SELECT VALID_FLG, DEPT_FLG FROM MEM_INSURE_INFO WHERE MR_NO = '"+table.getItemString(i, "MR_NO")+"' ";
         		TParm patinfo_parm = new TParm(TJDODBTool.getInstance().select(patinfo_sql));
         		int i3 = 1;
         		if(patinfo_parm.getValue("DEPT_FLG",0).equals("N")&&patinfo_parm.getValue("VALID_FLG",0).equals("Y")){
         			i3=2;
         		}
         		for(int j=0;j<i3;j++){
         			this.openPrintDialog(IReportTool.getInstance().getReportPath("BILDailyFeeQueryD.jhw"),
         					IReportTool.getInstance().getReportParm("BILDailyFeeQuery.class", printData),
         					isView);            	
         		}
         		//20141223 wangjingchun add end 854
            	
            } else  { //汇总清单
//            	this.messageBox("汇总清单2222");
            	printData.setData("TABLE", print.getData());
            	printData.setData("TITLE","TEXT", "住院费用清单");//liling 
            	//20141223 wangjingchun add start 854
         		String patinfo_sql = "SELECT VALID_FLG, DEPT_FLG FROM MEM_INSURE_INFO WHERE MR_NO = '"+table.getItemString(i, "MR_NO")+"' ";
         		TParm patinfo_parm = new TParm(TJDODBTool.getInstance().select(patinfo_sql));
         		int i4 = 1;
         		if(patinfo_parm.getValue("DEPT_FLG",0).equals("N")&&patinfo_parm.getValue("VALID_FLG",0).equals("Y")){
         			i4=2;
         		}
         		for(int j=0;j<i4;j++){
         			this.openPrintDialog(IReportTool.getInstance().getReportPath("BILDailyFeeQuery.jhw"),
         					IReportTool.getInstance().getReportParm("BILDailyFeeQuery.class", printData),
         					isView);
         		}
         		//20141223 wangjingchun add end 854
            }
//            this.openPrintDialog(IReportTool.getInstance().getReportPath("BILDailyFeeQuery.jhw"),
//                                 IReportTool.getInstance().getReportParm("BILDailyFeeQuery.class", printData),
//                                 isView);//报表合并modify by wanglong 20130730
        }
    }
    /**
     *返回 英文费用清单明细       
     *liling    
     */
    private TParm getSumPrintData2(TParm parm) {
    	String sql="SELECT  RECEIPT_NO,REDUCE_AMT FROM BIL_IBS_RECPM WHERE REFUND_CODE IS NULL  AND AR_AMT >0 AND CASE_NO = '" +parm.getValue("CASE_NO", 0)+
		"' ORDER BY RECEIPT_NO DESC";
    	TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
        TParm result = new TParm();
        String colunmName = ""; //记录指定的列名
        String descName = ""; //记录指定的列名
        //根据“院内费用”打印
        if (this.getValueBoolean("FEE_TYPE1")) {
            colunmName = "HEXP_CODE";
            descName = "CHARGE_HOSP_DESC"; //院内费用代码中文的列名
        }
        //根据“收据费用”打印
        else if (this.getValueBoolean("FEE_TYPE2")) {
            colunmName = "REXP_CODE";
            descName = "CHN_DESC"; //收据费用中文的列名
        }
        DecimalFormat df = new DecimalFormat("0.00");
        String code = ""; //记录每条数据的 收据类型代码 或者 院内费用代码
        double tot = 0; //记录总价格 实收
        double own = 0; //记录总价格 应收  liling add 
        int count = parm.getCount();
        int printCount = 0;
        //modify by liming 2012/03/06 begin
        String orderCode = "" ;
        double ownPrice = 0 ;//存储相同类型药品的价格合计
        double dosageQty = 0 ;//存储数量
        double ownAmt = 0 ;//===liling add 应收金额
        double totAmt = 0 ;//实收金额
        double everyOwnPrice = 0 ;//存储每一个药品的单价
        String feeTypeDesc = "" ;
        String cName ="" ;
        String specification = "" ;
        String orderDesc = "" ;
//        String unitChnDesc = "" ;
//        String execDept = "" ;
        String dosageUnit = "" ;
        String execDeptCode = "" ;
        String billDate = "" ;
        //住院医保医嘱代码
        String nhi_code_i = "";
        //国药准字号
//        String pzwh = "";
//        //自付比例
//        double zfbl1 = 0;
        
        double totDay =0;
        double ownDay =0; //==liling add 
        if(parm.getCount()>0){
        	//初始化第一行
        	orderCode = parm.getValue("ORDER_CODE",0) ;
			feeTypeDesc = parm.getValue(descName, 0) ;
			cName = parm.getValue(colunmName, 0) ;
			specification = parm.getValue("SPECIFICATION", 0) ;
			nhi_code_i = parm.getValue("NHI_CODE_I", 0) ;
//			pzwh = parm.getValue("PZWH", 0) ;
//			zfbl1 = parm.getDouble("ZFBL1", 0) ;
			orderDesc = parm.getValue("ORDER_DESC", 0) ;
//			unitChnDesc = parm.getValue("UNIT_CHN_DESC", 0) ;
			//execDept = parm.getValue("DEPT_CHN_DESC", 0) ;//==liling 屏蔽执行科室 
			ownPrice = Double.valueOf(df.format(parm.getDouble("OWN_PRICE", 0))) ;
			dosageQty = Double.valueOf(df.format(parm.getDouble("DOSAGE_QTY", 0))) ;
			ownAmt =  Double.valueOf(df.format(parm.getDouble("OWN_AMT", 0))) ;//==liling add 应收金额
			totAmt = Double.valueOf(df.format(parm.getDouble("TOT_AMT", 0))) ;
			dosageUnit = parm.getValue("DOSAGE_UNIT", 0) ;
			everyOwnPrice = ownPrice ;
			execDeptCode = parm.getValue("EXE_DEPT_CODE", 0) ;
			billDate = parm.getValue("BILL_DATE",0) ;

			//补充最后一行
			parm.addData(descName, "") ;
			parm.addData("SPECIFICATION", "") ;
			parm.addData("ORDER_DESC", "") ;
//			parm.addData("UNIT_CHN_DESC", "") ;
//			parm.addData("DEPT_CHN_DESC", "") ;
			parm.addData("OWN_PRICE", 0) ;
			parm.addData("DOSAGE_QTY", 0) ;
			parm.addData("OWN_AMT", 0) ;//==liling add
			parm.addData("TOT_AMT", 0) ;
        }

        TParm tempParm = new TParm() ;

        for (int i = 1; i < parm.getCount("ORDER_DESC"); i++) {
        	//===zhangp 20120521 start
//        		if(billDate.equals(parm.getValue("BILL_DATE",i)) && execDeptCode.equals(parm.getValue("EXE_DEPT_CODE", i)) && orderCode.equals(parm.getValue("ORDER_CODE",i)) && dosageUnit.equals(parm.getValue("DOSAGE_UNIT", i)) && everyOwnPrice == Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i)))){
        		if(orderCode.equals(parm.getValue("ORDER_CODE",i)) && dosageUnit.equals(parm.getValue("DOSAGE_UNIT", i)) && everyOwnPrice == Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i))) && execDeptCode.equals(parm.getValue("EXE_DEPT_CODE", i))){
        			//===zhangp 20120521 end
        			ownPrice = Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i))) ;
    				dosageQty += Double.valueOf(df.format(parm.getDouble("DOSAGE_QTY", i))) ;
    				ownAmt += Double.valueOf(df.format(parm.getDouble("OWN_AMT", i))) ;//==liling add 
    				totAmt += Double.valueOf(df.format(parm.getDouble("TOT_AMT", i))) ;
        		}else
        		{
    				tempParm.addData(descName, feeTypeDesc) ;
    				tempParm.addData(colunmName, cName) ;
    				tempParm.addData("SPECIFICATION", specification) ;
    				tempParm.addData("NHI_CODE_I", nhi_code_i);
//    				tempParm.addData("PZWH", pzwh);
//    				tempParm.addData("ZFBL1", zfbl1);
    				tempParm.addData("ORDER_DESC", orderDesc) ;
//    				tempParm.addData("UNIT_CHN_DESC", unitChnDesc) ;
//    				tempParm.addData("DEPT_CHN_DESC", execDept) ;

    				tempParm.addData("OWN_PRICE", ownPrice) ;
    				tempParm.addData("DOSAGE_QTY", dosageQty) ;
//    				tempParm.addData("TOTAL", "") ;//==liling add
    				tempParm.addData("OWN_AMT", ownAmt) ;//==liling add
    				tempParm.addData("TOT_AMT", totAmt) ;

    				orderCode = parm.getValue("ORDER_CODE",i) ;
    				ownPrice = 0 ;
    				dosageQty = 0 ;
    				totAmt = 0 ;
    				ownAmt = 0;//==liling add

    				feeTypeDesc = parm.getValue(descName, i) ;
    				cName = parm.getValue(colunmName, i) ;
    				specification = parm.getValue("SPECIFICATION", i) ;
    				nhi_code_i = parm.getValue("NHI_CODE_I", i);
//    				pzwh = parm.getValue("PZWH", i);
//    				zfbl1 = parm.getDouble("ZFBL1", i);
    				orderDesc = parm.getValue("ORDER_DESC", i) ;
//    				unitChnDesc = parm.getValue("UNIT_CHN_DESC", i) ;
    				//execDept = parm.getValue("DEPT_CHN_DESC", i) ;//==liling 屏蔽执行科室 
    				dosageUnit = parm.getValue("DOSAGE_UNIT", i) ;
    				everyOwnPrice = Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i))) ;
    				execDeptCode = parm.getValue("EXE_DEPT_CODE", i) ;
    				billDate = parm.getValue("BILL_DATE",i) ;

    				ownPrice = Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i))) ;
    				dosageQty += Double.valueOf(df.format(parm.getDouble("DOSAGE_QTY", i))) ;
    				ownAmt += Double.valueOf(df.format(parm.getDouble("OWN_AMT", i))) ;//===liling add 
    				totAmt += Double.valueOf(df.format(parm.getDouble("TOT_AMT", i))) ;
        		}

		}

        parm = tempParm ;
        count = parm.getCount("ORDER_DESC") ;
        //modify by liming 2012/03/06 end

        for (int i = 0; i < count; i++) {
            if (parm.getDouble("TOT_AMT", i) == 0)
                continue;
            //如果代码与数据中的代码不同 那么记录最新的代码，并且在报表中打印出相应的中文 作为一个分组的开始行
            if (!code.equals(parm.getValue(colunmName, i))) {
//            	if(i!=0){
//            		result.addData("FEE_TYPE_DESC", "");
//            		result.addData("ORDER_DESC", "");
//            		result.addData("UNIT_CHN_DESC", "");
//            		result.addData("OWN_PRICE", "");
//            		result.addData("DOSAGE_QTY", "");
//            		result.addData("TOTAL", "小计:") ;//==liling add
//            		result.addData("OWN_AMT", df.format(ownDay));
//            		result.addData("TOT_AMT", df.format(totDay));
//            		//result.addData("EXE_DEPT", "");//==liling 屏蔽执行科室
//            		result.addData(".TableRowLineShow", false);
//            		ownDay = 0;//==liling add 
//                    totDay = 0;
//                    printCount++;
//            	}
                code = parm.getValue(colunmName, i);
                result.addData("FEE_TYPE_DESC", parm.getValue(descName, i));
//                if (printCount > 0){
//                	result.setData(".TableRowLineShow", printCount - 1, true); //将上一行的线改为显示
//                }
            } else {
                result.addData("FEE_TYPE_DESC", ""); //只有一组数据的首行显示类型名称 其他行不显示
            }
            String SPECIFICATION = parm.getValue("SPECIFICATION", i).length() <=
                                   0 ? "" :
                                   "(" + parm.getValue("SPECIFICATION", i) +
                                   ")";
//            result.addData("ORDER_DESC",
//                           parm.getValue("ORDER_DESC", i) + SPECIFICATION);
            /*******add by zhangzc 20120612 每日费用清单增加标识**************/
            String mark = "";
//            Double addRate = Double.parseDouble(parm.getValue("ZFBL1", i).equals("")?"0":parm.getValue("ZFBL1", i));
//            if(addRate == 1)
//            	mark = "☆";
//            else if (addRate > 0.00)
//            	mark = "#";
			result.addData("ORDER_DESC", mark +  ""
					+ parm.getValue("ORDER_DESC", i) + SPECIFICATION + " ");//==liling 去掉parm.getValue("NHI_CODE_I", i) +  + parm.getValue("PZWH", i) + " " + parm.getDouble("ZFBL1", i) * 100 + "%" 
//            result.addData("UNIT_CHN_DESC", parm.getValue("UNIT_CHN_DESC", i));
            result.addData("OWN_PRICE", df.format(parm.getDouble("OWN_PRICE", i)));
            result.addData("DOSAGE_QTY",
                           df.format(parm.getDouble("DOSAGE_QTY", i)));
//            result.addData("TOTAL", "") ;//==liling add
            result.addData("OWN_AMT", df.format(parm.getDouble("OWN_AMT", i)));//==liling add 
            result.addData("TOT_AMT", df.format(parm.getDouble("TOT_AMT", i)));
           // result.addData("EXE_DEPT", parm.getValue("DEPT_CHN_DESC", i));//==liling 屏蔽执行科室
//            if (i + 1 == count)
//                result.addData(".TableRowLineShow", false); //如果是数据的最后一行 那么加横线
//            else
//                result.addData(".TableRowLineShow", false);
            own += StringTool.round(parm.getDouble("OWN_AMT", i),2);
            tot += StringTool.round(parm.getDouble("TOT_AMT", i),2);
            
            printCount++;
            ownDay += parm.getDouble("OWN_AMT", i);//==liling add
            totDay += parm.getDouble("TOT_AMT", i);
            
        }
//        result.addData("FEE_TYPE_DESC", "");
//		result.addData("ORDER_DESC", "");
//		result.addData("UNIT_CHN_DESC", "");
//		result.addData("OWN_PRICE", "");
//		result.addData("DOSAGE_QTY", "");
//		result.addData("TOTAL", "小计:") ;//==liling add
//		result.addData("OWN_AMT", df.format(ownDay));//==liling add 
//		result.addData("TOT_AMT", df.format(totDay));
//		//result.addData("EXE_DEPT", "");//==liling 屏蔽执行科室
//		result.addData(".TableRowLineShow", true);
//		printCount++;
		//==liling add 减免行
		if (parm1.getDouble("REDUCE_AMT", 0) != 0){
			 result.addData("FEE_TYPE_DESC", "Other Charges");
		     result.addData("ORDER_DESC", "Other");
//		     result.addData("UNIT_CHN_DESC", "");
		     result.addData("OWN_PRICE", "");
		     result.addData("DOSAGE_QTY", "");
//     		 result.addData("TOTAL", "减免:") ;//==liling add
		     result.addData("OWN_AMT", df.format(-parm1.getDouble("REDUCE_AMT", 0)));//==liling add 
		     result.addData("TOT_AMT", df.format(-parm1.getDouble("REDUCE_AMT", 0)));
		     printCount++;
//		     //总计行
//		        result.addData("FEE_TYPE_DESC", "");
//		        result.addData("ORDER_DESC", "");
//		        result.addData("UNIT_CHN_DESC", "");
//		        result.addData("OWN_PRICE", "");
//		        result.addData("DOSAGE_QTY", "");
//        		result.addData("TOTAL", "合计:") ;//==liling add
//		        result.addData("OWN_AMT", df.format(own));//==liling add 
//		        result.addData("TOT_AMT", df.format(tot - parm1.getDouble("REDUCE_AMT", 0)));
		}
//		else{   
//        //总计行
//        result.addData("FEE_TYPE_DESC", "");
//        result.addData("ORDER_DESC", "");
//        result.addData("UNIT_CHN_DESC", "");
//        result.addData("OWN_PRICE", "");
//        result.addData("DOSAGE_QTY", "");
//		result.addData("TOTAL", "合计:") ;//==liling add
//        result.addData("OWN_AMT", df.format(own));//==liling add 
//        result.addData("TOT_AMT", df.format(tot));
//        //result.addData("EXE_DEPT", "");//==liling 屏蔽执行科室
//		}
//        result.addData(".TableRowLineShow", false);
        result.setCount(printCount );
        result.addData("SYSTEM", "COLUMNS", "FEE_TYPE_DESC");
        result.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//        result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        result.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        result.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
//        result.addData("SYSTEM", "COLUMNS", "TOTAL");//==liling add 
        result.addData("SYSTEM", "COLUMNS", "OWN_AMT");
        result.addData("SYSTEM", "COLUMNS", "TOT_AMT");
       // result.addData("SYSTEM", "COLUMNS", "EXE_DEPT");//==liling 屏蔽执行科室
       // System.out.println("result=========="+result);
        return result;
    
	}

	/**
     *返回 套餐内明细      
     *liling     
     */
    private TParm getSumPrintData1(TParm parm) {
        TParm result = new TParm();
        String colunmName = ""; //记录指定的列名
        String descName = ""; //记录指定的列名
        //根据“院内费用”打印
        if (this.getValueBoolean("FEE_TYPE1")) {
            colunmName = "HEXP_CODE";
            descName = "CHARGE_HOSP_DESC"; //院内费用代码中文的列名
        }
        //根据“收据费用”打印
        else if (this.getValueBoolean("FEE_TYPE2")) {
            colunmName = "REXP_CODE";
            descName = "CHN_DESC"; //收据费用中文的列名
        }
        DecimalFormat df = new DecimalFormat("0.00");
        String code = ""; //记录每条数据的 收据类型代码 或者 院内费用代码
 //       double tot = 0; //记录总价格
        int count = parm.getCount();
        int printCount = 0;
        String orderCode = "" ;
//        double ownPrice = 0 ;//存储相同类型药品的价格合计
        double dosageQty = 0 ;//存储数量
//        double totAmt = 0 ;
//        double everyOwnPrice = 0 ;//存储每一个药品的单价
        String feeTypeDesc = "" ;
        String cName ="" ;
        String specification = "" ;
        String orderDesc = "" ;
        String unitChnDesc = "" ;
  //      String execDept = "" ;
//        String dosageUnit = "" ;
        String execDeptCode = "" ;
        String billDate = "" ;
        //住院医保医嘱代码
        String nhi_code_i = "";
        //国药准字号
        String pzwh = "";
        //自付比例
        double zfbl1 = 0;
        
//        double totDay =0;

//        if(parm.getCount()>0){
//        	//初始化第一行
//        	orderCode = parm.getValue("ORDER_CODE",0) ;
//			feeTypeDesc = parm.getValue(descName, 0) ;
//			cName = parm.getValue(colunmName, 0) ;
//			specification = parm.getValue("SPECIFICATION", 0) ;
//			nhi_code_i = parm.getValue("NHI_CODE_I", 0) ;
//			pzwh = parm.getValue("PZWH", 0) ;
//			zfbl1 = parm.getDouble("ZFBL1", 0) ;
//			orderDesc = parm.getValue("ORDER_DESC", 0) ;
////			unitChnDesc = parm.getValue("UNIT_CHN_DESC", 0) ;
//			execDept = parm.getValue("DEPT_CHN_DESC", 0) ;
////			ownPrice = Double.valueOf(df.format(parm.getDouble("OWN_PRICE", 0))) ;
//			dosageQty = Double.valueOf(df.format(parm.getDouble("DOSAGE_QTY", 0))) ;
////			totAmt = Double.valueOf(df.format(parm.getDouble("TOT_AMT", 0))) ;
////			dosageUnit = parm.getValue("DOSAGE_UNIT", 0) ;
////			everyOwnPrice = ownPrice ;
//			execDeptCode = parm.getValue("EXE_DEPT_CODE", 0) ;
//			billDate = parm.getValue("BILL_DATE",0) ;
//
////			//补充最后一行
////			parm.addData(descName, "") ;
////			parm.addData("SPECIFICATION", "") ;
////			parm.addData("ORDER_DESC", "") ;
////			parm.addData("UNIT_CHN_DESC", "") ;
////			parm.addData("DEPT_CHN_DESC", "") ;
////			parm.addData("OWN_PRICE", 0) ;
////			parm.addData("DOSAGE_QTY", 0) ;
////			parm.addData("TOT_AMT", 0) ;
//        }

        TParm tempParm = new TParm() ;

        for (int i = 0; i < parm.getCount("ORDER_DESC"); i++) {
////        		if(orderCode.equals(parm.getValue("ORDER_CODE",i)) && dosageUnit.equals(parm.getValue("DOSAGE_UNIT", i)) && everyOwnPrice == Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i))) && execDeptCode.equals(parm.getValue("EXE_DEPT_CODE", i))){
//    		if(orderCode.equals(parm.getValue("ORDER_CODE",i)) &&  execDeptCode.equals(parm.getValue("EXE_DEPT_CODE", i))){
//        		//	ownPrice = Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i))) ;
//    				dosageQty += Double.valueOf(df.format(parm.getDouble("DOSAGE_QTY", i))) ;
//    			//	totAmt += Double.valueOf(df.format(parm.getDouble("TOT_AMT", i))) ;
//        		}else
//        		{	orderCode = parm.getValue("ORDER_CODE",i) ;
			//	ownPrice = 0 ;
			dosageQty = 0 ;
	//		totAmt = 0 ;

			feeTypeDesc = parm.getValue(descName, i) ;
			cName = parm.getValue(colunmName, i) ;
			specification = parm.getValue("SPECIFICATION", i) ;
			nhi_code_i = parm.getValue("NHI_CODE_I", i);
			pzwh = parm.getValue("PZWH", i);
			zfbl1 = parm.getDouble("ZFBL1", i);
			orderDesc = parm.getValue("ORDER_DESC", i) ;
			unitChnDesc = parm.getValue("UNIT_CHN_DESC", i) ;
//			execDept = parm.getValue("DEPT_CHN_DESC", i) ;
		//	dosageUnit = parm.getValue("DOSAGE_UNIT", i) ;
		//	everyOwnPrice = Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i))) ;
			execDeptCode = parm.getValue("EXE_DEPT_CODE", i) ;
			billDate = parm.getValue("BILL_DATE",i) ;

		//	ownPrice = Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i))) ;
			dosageQty = Double.valueOf(df.format(parm.getDouble("DOSAGE_QTY", i))) ;
		//	totAmt += Double.valueOf(df.format(parm.getDouble("TOT_AMT", i))) ;
            tempParm.addData(descName, feeTypeDesc) ;
			tempParm.addData(colunmName, cName) ;
			tempParm.addData("SPECIFICATION", specification) ;
			tempParm.addData("NHI_CODE_I", nhi_code_i);
			tempParm.addData("PZWH", pzwh);
			tempParm.addData("ZFBL1", zfbl1);
			tempParm.addData("ORDER_DESC", orderDesc) ;
			tempParm.addData("UNIT_CHN_DESC", unitChnDesc) ;
//    				tempParm.addData("DEPT_CHN_DESC", execDept) ;

		//	tempParm.addData("OWN_PRICE", ownPrice) ;
			tempParm.addData("DOSAGE_QTY", dosageQty) ;
    			//	tempParm.addData("TOT_AMT", totAmt) ;
  //      		}

		}

        parm = tempParm ;
        count = parm.getCount("ORDER_DESC") ;
        for (int i = 0; i < count; i++) {
            if (parm.getDouble("DOSAGE_QTY", i) <= 0)
                continue;
            //如果代码与数据中的代码不同 那么记录最新的代码，并且在报表中打印出相应的中文 作为一个分组的开始行
//            System.out.println("code:::::"+code);
//            System.out.println("colunmName:::::"+parm.getValue(colunmName, i));
//            System.out.println("ORDER_DESC::::"+parm.getValue("ORDER_DESC", i));
//            System.out.println("printCount::::SDFSDFSD:::"+printCount);
            if (!code.equals(parm.getValue(colunmName, i))) {
//            	if(i!=0){
//            		result.addData("FEE_TYPE_DESC", "");
//            		result.addData("ORDER_DESC", "");
//            		result.addData("UNIT_CHN_DESC", "");
//            		result.addData("OWN_PRICE", "");
//            		result.addData("DOSAGE_QTY", "小计:");
//            		result.addData("TOT_AMT", df.format(totDay));
//            		result.addData("EXE_DEPT", "");
//            		result.addData(".TableRowLineShow", false);
//                    totDay = 0;
//                    printCount++;
//            	}
            	//System.out.println(i+"::::::"+parm.getValue(colunmName, i));
                code = parm.getValue(colunmName, i);
                //System.out.println("printCount::::"+printCount);
                //System.out.println("descName::::::"+ parm.getValue(descName, i));
                result.addData("FEE_TYPE_DESC", parm.getValue(descName, i));
//                System.out.println("printCountfhfhhfhfhfh::::"+printCount);
                if (printCount > 0){
                	result.setData(".TableRowLineShow", printCount - 1, true); //将上一行的线改为显示
                }
            } else {
                result.addData("FEE_TYPE_DESC",""); //只有一组数据的首行显示类型名称 其他行不显示
            }
            String SPECIFICATION = parm.getValue("SPECIFICATION", i).length() <=
                                   0 ? "" :
                                   "(" + parm.getValue("SPECIFICATION", i) +
                                   ")";
//            result.addData("ORDER_DESC",
//                           parm.getValue("ORDER_DESC", i) + SPECIFICATION);
            /*******add by zhangzc 20120612 每日费用清单增加标识**************/
            String mark = "";
            Double addRate = Double.parseDouble(parm.getValue("ZFBL1", i).equals("")?"0":parm.getValue("ZFBL1", i));
            if(addRate == 1)
            	mark = "☆";
            else if (addRate > 0.00)
            	mark = "#";
			result.addData("ORDER_DESC", mark + parm.getValue("NHI_CODE_I", i) + " "
					+ parm.getValue("ORDER_DESC", i) + SPECIFICATION + " "
					+ parm.getValue("PZWH", i) + " ");//==liling 去掉增付比例显示 + parm.getDouble("ZFBL1", i) * 100 + "%"
			/*******add by zhangzc 20120612 每日费用清单增加标识**************/
            result.addData("UNIT_CHN_DESC", parm.getValue("UNIT_CHN_DESC", i));
//            result.addData("OWN_PRICE", df.format(parm.getDouble("OWN_PRICE", i)));
            result.addData("DOSAGE_QTY",
                           df.format(parm.getDouble("DOSAGE_QTY", i)));
//            result.addData("TOT_AMT", df.format(parm.getDouble("TOT_AMT", i)));
//            result.addData("EXE_DEPT", parm.getValue("DEPT_CHN_DESC", i));
            if (i == count-1)
                result.addData(".TableRowLineShow", true); //如果是数据的最后一行 那么加横线
            else
                result.addData(".TableRowLineShow", false);
//            tot += StringTool.round(parm.getDouble("TOT_AMT", i),2);
            printCount++;
//            totDay += parm.getDouble("TOT_AMT", i);
        }
//        result.addData("FEE_TYPE_DESC", "");
//		result.addData("ORDER_DESC", "");
//		result.addData("UNIT_CHN_DESC", "");
//		result.addData("OWN_PRICE", "");
//		result.addData("DOSAGE_QTY", "小计:");
//		result.addData("TOT_AMT", df.format(totDay));
//		result.addData("EXE_DEPT", "");
		//result.addData(".TableRowLineShow", true);
	//	printCount++;//==liling
//        //总计行
//        result.addData("FEE_TYPE_DESC", "");
//        result.addData("ORDER_DESC", "");
//        result.addData("UNIT_CHN_DESC", "");
//        result.addData("OWN_PRICE", "");
//        result.addData("DOSAGE_QTY", "合计：");
//        result.addData("TOT_AMT", df.format(tot));
//        result.addData("EXE_DEPT", "");
        //result.addData(".TableRowLineShow", true);
        result.setCount(printCount);
        result.addData("SYSTEM", "COLUMNS", "FEE_TYPE_DESC");
        result.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
      //  result.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        result.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
      //  result.addData("SYSTEM", "COLUMNS", "TOT_AMT");
      //  result.addData("SYSTEM", "COLUMNS", "EXE_DEPT");
        return result;

	}


//	/** 
//	* 精确的加法运算. 
//	*/ 
//	public static double add(double v1, double v2) { 
//	BigDecimal b1 = new BigDecimal(v1); 
//	BigDecimal b2 = new BigDecimal(v2); 
//	return b1.add(b2).doubleValue(); 
//	} 
	
    /**
     * 返回汇总清单数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getSumPrintData(TParm parm) {
    	String sql="SELECT  RECEIPT_NO,REDUCE_AMT FROM BIL_IBS_RECPM WHERE REFUND_CODE IS NULL  AND AR_AMT >0 AND CASE_NO = '" +parm.getValue("CASE_NO", 0)+
    			"' ORDER BY RECEIPT_NO DESC";
    	TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
        TParm result = new TParm();
        String colunmName = ""; //记录指定的列名
        String descName = ""; //记录指定的列名
        
        //根据“院内费用”打印
        if (this.getValueBoolean("FEE_TYPE1")) {
            colunmName = "HEXP_CODE";
            descName = "CHARGE_HOSP_DESC"; //院内费用代码中文的列名
        }
        //根据“收据费用”打印
        else if (this.getValueBoolean("FEE_TYPE2")) {
            colunmName = "REXP_CODE";
            descName = "CHN_DESC"; //收据费用中文的列名
        }
        DecimalFormat df = new DecimalFormat("0.00");
        String code = ""; //记录每条数据的 收据类型代码 或者 院内费用代码
        double tot = 0; //记录总价格 实收
        double own = 0; //记录总价格 应收  liling add 
        int count = parm.getCount();
        int printCount = 0;
        //modify by liming 2012/03/06 begin
        String orderCode = "" ;
        double ownPrice = 0 ;//存储相同类型药品的价格合计
        double dosageQty = 0 ;//存储数量
        double ownAmt = 0 ;//===liling add 应收金额
        double totAmt = 0 ;//实收金额
        double everyOwnPrice = 0 ;//存储每一个药品的单价
        String feeTypeDesc = "" ;
        String cName ="" ;
        String specification = "" ;
        String orderDesc = "" ;
        String unitChnDesc = "" ;
        String execDept = "" ;
        String dosageUnit = "" ;
        String execDeptCode = "" ;
        String billDate = "" ;
        //住院医保医嘱代码
        String nhi_code_i = "";
        //国药准字号
        String pzwh = "";
        //自付比例
        double zfbl1 = 0;
        
        double totDay =0;
        double ownDay =0; //==liling add 
        double outTotFee=0;
        double outOwnFee=0;
        if(parm.getCount()>0){
        	//初始化第一行
        	orderCode = parm.getValue("ORDER_CODE",0) ;
			feeTypeDesc = parm.getValue(descName, 0) ;
			cName = parm.getValue(colunmName, 0) ;
			specification = parm.getValue("SPECIFICATION", 0) ;
			nhi_code_i = parm.getValue("NHI_CODE_I", 0) ;
			pzwh = parm.getValue("PZWH", 0) ;
			zfbl1 = parm.getDouble("ZFBL1", 0) ;
			orderDesc = parm.getValue("ORDER_DESC", 0) ;
			unitChnDesc = parm.getValue("UNIT_CHN_DESC", 0) ;
			//execDept = parm.getValue("DEPT_CHN_DESC", 0) ;//==liling 屏蔽执行科室 
			ownPrice = Double.valueOf(df.format(parm.getDouble("OWN_PRICE", 0))) ;
			dosageQty = Double.valueOf(df.format(parm.getDouble("DOSAGE_QTY", 0))) ;
			ownAmt =  Double.valueOf(df.format(parm.getDouble("OWN_AMT", 0))) ;//==liling add 应收金额
			totAmt = Double.valueOf(df.format(parm.getDouble("TOT_AMT", 0))) ;
			dosageUnit = parm.getValue("DOSAGE_UNIT", 0) ;
			everyOwnPrice = ownPrice ;
			execDeptCode = parm.getValue("EXE_DEPT_CODE", 0) ;
			billDate = parm.getValue("BILL_DATE",0) ;

			//补充最后一行
			parm.addData(descName, "") ;
			parm.addData("SPECIFICATION", "") ;
			parm.addData("ORDER_DESC", "") ;
			parm.addData("UNIT_CHN_DESC", "") ;
			parm.addData("DEPT_CHN_DESC", "") ;
			parm.addData("OWN_PRICE", 0) ;
			parm.addData("DOSAGE_QTY", 0) ;
			parm.addData("OWN_AMT", 0) ;//==liling add
			parm.addData("TOT_AMT", 0) ;
        }

        TParm tempParm = new TParm() ;

        for (int i = 1; i < parm.getCount("ORDER_DESC"); i++) {
        	//===zhangp 20120521 start
//        		if(billDate.equals(parm.getValue("BILL_DATE",i)) && execDeptCode.equals(parm.getValue("EXE_DEPT_CODE", i)) && orderCode.equals(parm.getValue("ORDER_CODE",i)) && dosageUnit.equals(parm.getValue("DOSAGE_UNIT", i)) && everyOwnPrice == Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i)))){
//        		if(orderCode.equals(parm.getValue("ORDER_CODE",i)) && dosageUnit.equals(parm.getValue("DOSAGE_UNIT", i)) && everyOwnPrice == Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i))) && execDeptCode.equals(parm.getValue("EXE_DEPT_CODE", i))){
        			//===zhangp 20120521 end
        		if(orderCode.equals(parm.getValue("ORDER_CODE",i)) && dosageUnit.equals(parm.getValue("DOSAGE_UNIT", i)) && everyOwnPrice == Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i)))){//不考虑执行科室不同情况，只统计医嘱相同的累加合并 modify by lich 
        			ownPrice = Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i))) ;
    				dosageQty += Double.valueOf(df.format(parm.getDouble("DOSAGE_QTY", i))) ;
    				ownAmt += Double.valueOf(df.format(parm.getDouble("OWN_AMT", i))) ;//==liling add 
    				totAmt += Double.valueOf(df.format(parm.getDouble("TOT_AMT", i))) ;
        		}else
        		{
    				tempParm.addData(descName, feeTypeDesc) ;
    				tempParm.addData(colunmName, cName) ;
    				tempParm.addData("SPECIFICATION", specification) ;
    				tempParm.addData("NHI_CODE_I", nhi_code_i);
    				tempParm.addData("PZWH", pzwh);
    				tempParm.addData("ZFBL1", zfbl1);
    				tempParm.addData("ORDER_DESC", orderDesc) ;
    				tempParm.addData("UNIT_CHN_DESC", unitChnDesc) ;
    				tempParm.addData("DEPT_CHN_DESC", execDept) ;

    				tempParm.addData("OWN_PRICE", ownPrice) ;
    				tempParm.addData("DOSAGE_QTY", dosageQty) ;
    				tempParm.addData("TOTAL", "") ;//==liling add
    				tempParm.addData("OWN_AMT",df.format(ownAmt)) ;//==liling add
    				tempParm.addData("TOT_AMT", df.format(totAmt)) ;//===pangben 2015-10-23

    				orderCode = parm.getValue("ORDER_CODE",i) ;
    				ownPrice = 0 ;
    				dosageQty = 0 ;
    				totAmt = 0 ;
    				ownAmt = 0;//==liling add

    				feeTypeDesc = parm.getValue(descName, i) ;
    				cName = parm.getValue(colunmName, i) ;
    				specification = parm.getValue("SPECIFICATION", i) ;
    				nhi_code_i = parm.getValue("NHI_CODE_I", i);
    				pzwh = parm.getValue("PZWH", i);
    				zfbl1 = parm.getDouble("ZFBL1", i);
    				orderDesc = parm.getValue("ORDER_DESC", i) ;
    				unitChnDesc = parm.getValue("UNIT_CHN_DESC", i) ;
    				//execDept = parm.getValue("DEPT_CHN_DESC", i) ;//==liling 屏蔽执行科室 
    				dosageUnit = parm.getValue("DOSAGE_UNIT", i) ;
    				everyOwnPrice = Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i))) ;
    				execDeptCode = parm.getValue("EXE_DEPT_CODE", i) ;
    				billDate = parm.getValue("BILL_DATE",i) ;

    				ownPrice = Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i))) ;
    				dosageQty += Double.valueOf(df.format(parm.getDouble("DOSAGE_QTY", i))) ;
    				ownAmt += Double.valueOf(df.format(parm.getDouble("OWN_AMT", i))) ;//===liling add 
    				totAmt += Double.valueOf(df.format(parm.getDouble("TOT_AMT", i))) ;
        		}

		}

        parm = tempParm ;
        count = parm.getCount("ORDER_DESC") ;
        //modify by liming 2012/03/06 end

        for (int i = 0; i < count; i++) {
            if (parm.getDouble("TOT_AMT", i) == 0)
                continue;
            //如果代码与数据中的代码不同 那么记录最新的代码，并且在报表中打印出相应的中文 作为一个分组的开始行
            if (!code.equals(parm.getValue(colunmName, i))) {
            	if(i!=0){
            		result.addData("FEE_TYPE_DESC", "");
            		result.addData("ORDER_DESC", "");
            		result.addData("UNIT_CHN_DESC", "");
            		result.addData("OWN_PRICE", "");
            		result.addData("DOSAGE_QTY", "");
            		result.addData("TOTAL", "小计:") ;//==liling add
            		result.addData("OWN_AMT", df.format(ownDay));
            		result.addData("TOT_AMT", df.format(totDay));
            		//result.addData("EXE_DEPT", "");//==liling 屏蔽执行科室
            		result.addData(".TableRowLineShow", false);
            		ownDay = 0;//==liling add 
                    totDay = 0;
                    printCount++;
            	}
                code = parm.getValue(colunmName, i);
                result.addData("FEE_TYPE_DESC", parm.getValue(descName, i));
                if (printCount > 0){
                	result.setData(".TableRowLineShow", printCount - 1, true); //将上一行的线改为显示
                }
            } else {
                result.addData("FEE_TYPE_DESC", ""); //只有一组数据的首行显示类型名称 其他行不显示
            }
            String SPECIFICATION = parm.getValue("SPECIFICATION", i).length() <=
                                   0 ? "" :
                                   "(" + parm.getValue("SPECIFICATION", i) +
                                   ")";
//            result.addData("ORDER_DESC",
//                           parm.getValue("ORDER_DESC", i) + SPECIFICATION);
            /*******add by zhangzc 20120612 每日费用清单增加标识**************/
            String mark = "";
            Double addRate = Double.parseDouble(parm.getValue("ZFBL1", i).equals("")?"0":parm.getValue("ZFBL1", i));
            if(addRate == 1)
            	mark = "☆";
            else if (addRate > 0.00)
            	mark = "#";
			result.addData("ORDER_DESC", mark + parm.getValue("NHI_CODE_I", i) + " "
					+ parm.getValue("ORDER_DESC", i) + SPECIFICATION + " "
					+ parm.getValue("PZWH", i) + " ");//==liling 去掉 + parm.getDouble("ZFBL1", i) * 100 + "%"
			/*******add by zhangzc 20120612 每日费用清单增加标识**************/
            result.addData("UNIT_CHN_DESC", parm.getValue("UNIT_CHN_DESC", i));
            result.addData("OWN_PRICE", df.format(parm.getDouble("OWN_PRICE", i)));
            result.addData("DOSAGE_QTY",
                           df.format(parm.getDouble("DOSAGE_QTY", i)));
            result.addData("TOTAL", "") ;//==liling add
            result.addData("OWN_AMT", df.format(parm.getDouble("OWN_AMT", i)));//==liling add 
            result.addData("TOT_AMT", df.format(parm.getDouble("TOT_AMT", i)));
           // result.addData("EXE_DEPT", parm.getValue("DEPT_CHN_DESC", i));//==liling 屏蔽执行科室
            if (i + 1 == count)
                result.addData(".TableRowLineShow", false); //如果是数据的最后一行 那么加横线
            else
                result.addData(".TableRowLineShow", false);
            own += StringTool.round(parm.getDouble("OWN_AMT", i),2);
            tot += StringTool.round(parm.getDouble("TOT_AMT", i),2);
            
            printCount++;
            ownDay += parm.getDouble("OWN_AMT", i);//==liling add
            totDay += parm.getDouble("TOT_AMT", i);
            outTotFee+= parm.getDouble("TOT_AMT", i);
            outOwnFee+= parm.getDouble("OWN_AMT", i);
        }
        result.addData("FEE_TYPE_DESC", "");
		result.addData("ORDER_DESC", "");
		result.addData("UNIT_CHN_DESC", "");
		result.addData("OWN_PRICE", "");
		result.addData("DOSAGE_QTY", "");
		result.addData("TOTAL", "小计:") ;//==liling add
		result.addData("OWN_AMT", df.format(ownDay));//==liling add 
		result.addData("TOT_AMT", df.format(totDay));
		//result.addData("EXE_DEPT", "");//==liling 屏蔽执行科室
		result.addData(".TableRowLineShow", true);
		result.setData("L_OWN_AMT",outOwnFee);
		result.setData("L_TOT_AMT",outTotFee);
		printCount++;
		//==liling add 减免行
		if (parm1.getDouble("REDUCE_AMT", 0) != 0){
			 result.addData("FEE_TYPE_DESC", "");
		     result.addData("ORDER_DESC", "");
		     result.addData("UNIT_CHN_DESC", "");
		     result.addData("OWN_PRICE", "");
		     result.addData("DOSAGE_QTY", "");
     		 result.addData("TOTAL", "减免:") ;//==liling add
		     result.addData("OWN_AMT", "");//==liling add 
		     result.addData("TOT_AMT", df.format(-parm1.getDouble("REDUCE_AMT", 0)));
		     printCount++;
//		     //总计行
//		        result.addData("FEE_TYPE_DESC", "");
//		        result.addData("ORDER_DESC", "");
//		        result.addData("UNIT_CHN_DESC", "");
//		        result.addData("OWN_PRICE", "");
//		        result.addData("DOSAGE_QTY", "");
//        		result.addData("TOTAL", "合计:") ;//==liling add
//		        result.addData("OWN_AMT", df.format(own));//==liling add 
//		        result.addData("TOT_AMT", df.format(tot - parm1.getDouble("REDUCE_AMT", 0)));
		}
//		else{   
//        //总计行
//        result.addData("FEE_TYPE_DESC", "");
//        result.addData("ORDER_DESC", "");
//        result.addData("UNIT_CHN_DESC", "");
//        result.addData("OWN_PRICE", "");
//        result.addData("DOSAGE_QTY", "");
//		result.addData("TOTAL", "合计:") ;//==liling add
//        result.addData("OWN_AMT", df.format(own));//==liling add 
//        result.addData("TOT_AMT", df.format(tot));
//        //result.addData("EXE_DEPT", "");//==liling 屏蔽执行科室
//		}
        result.addData(".TableRowLineShow", false);
        result.setCount(printCount );
        result.addData("SYSTEM", "COLUMNS", "FEE_TYPE_DESC");
        result.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        result.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        result.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
        result.addData("SYSTEM", "COLUMNS", "TOTAL");//==liling add 
        result.addData("SYSTEM", "COLUMNS", "OWN_AMT");
        result.addData("SYSTEM", "COLUMNS", "TOT_AMT");
       // result.addData("SYSTEM", "COLUMNS", "EXE_DEPT");//==liling 屏蔽执行科室
        
        return result;
    }

    /**
     * 返回每日清单数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getDayPrintData(TParm parm) {
    	String sql="SELECT  RECEIPT_NO,REDUCE_AMT FROM BIL_IBS_RECPM WHERE REFUND_CODE IS NULL  AND AR_AMT >0 AND CASE_NO = '" +parm.getValue("CASE_NO", 0)+
		"' ORDER BY RECEIPT_NO DESC";
    	TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
        TParm result = new TParm();
        String colunmName = ""; //记录指定的列名
        String descName = ""; //记录指定的列名
        //根据“收据费用”打印
        if (this.getValueBoolean("FEE_TYPE1")) {
            colunmName = "HEXP_CODE";
            descName = "CHARGE_HOSP_DESC"; //院内费用代码中文的列名
        }
        //根据“院内费用”打印
        else if (this.getValueBoolean("FEE_TYPE2")) {
            colunmName = "REXP_CODE";
            descName = "CHN_DESC"; //收据费用中文的列名
        }
        DecimalFormat df = new DecimalFormat("0.00");
        String code = ""; //记录每条数据的 收据类型代码 或者 院内费用代码
        int count = parm.getCount();
        String billDate = "";
        double tot = 0; //记录总价格
        int rowCount = 0; //记录行数
        double ownDay =0;//==liling add 20140709 应收金额
        //===zhangp 20120317 start
        double totDay = 0;//实收
        //String execDate="";//执行时间
        //===zhangp 20120317 end
        for (int i = 0; i < count; i++) {
            if (parm.getDouble("TOT_AMT", i) == 0)
                continue;
            //判断是否是一天的数据 如果不是那么加入一个只有日期的空行
            if (!billDate.equals(parm.getValue("BILL_DATE", i))) {
            	//===zhangp 20120317 start
            	if(i!=0){
            		result.addData("FEE_TYPE_DESC", "");
                    result.addData("ORDER_DESC", "");
                    result.addData("UNIT_CHN_DESC", "");
                    result.addData("OWN_PRICE", "");
                    result.addData("DOSAGE_QTY", "");
                    result.addData("TOTAL", "小计:");//liling add 20140709
                    result.addData("OWN_AMT", df.format(ownDay));//liling add 20140709 应收
                    result.addData("TOT_AMT", df.format(totDay));//实收
                    //result.addData("EXEC_DATE", "");//==liling 20140806 add 执行时间
//                    result.addData("EXE_DEPT", "");//==liling 20140709 屏蔽执行科室
                    result.addData(".TableRowLineShow", false);
                    ownDay =0;//==liling add 20140709
                    totDay = 0;
                    rowCount++;
            	}
            	//===zhangp 20120317 end
                billDate = parm.getValue("BILL_DATE", i);
                result.addData("FEE_TYPE_DESC", "日期:" + billDate);
                result.addData("ORDER_DESC", "");
                result.addData("UNIT_CHN_DESC", "");
                result.addData("OWN_PRICE", "");
                result.addData("DOSAGE_QTY", "");
                result.addData("TOTAL", "");//liling add 20140709 小计
                result.addData("OWN_AMT", "");//liling add 20140709 应收
                result.addData("TOT_AMT", "");//实收
                result.addData("EXEC_DATE", "");//==liling 20140806 add 执行时间
//                result.addData("EXE_DEPT", "");//==liling 20140709 屏蔽执行科室
                result.addData(".TableRowLineShow", false);
                if (rowCount > 0)
                    result.setData(".TableRowLineShow", rowCount - 1, true); //将上一行的线改为显示
                rowCount++;
            }
            //如果代码与数据中的代码不同 那么记录最新的代码，并且在报表中打印出相应的中文 作为一个分组的开始行
            if (!code.equals(parm.getValue(colunmName, i))) {
                code = parm.getValue(colunmName, i);
                result.addData("FEE_TYPE_DESC", parm.getValue(descName, i));
            } else {
                result.addData("FEE_TYPE_DESC", ""); //只有一组数据的首行显示类型名称 其他行不显示
            }
            String SPECIFICATION = parm.getValue("SPECIFICATION", i).length() <=
                                   0 ? "" :
                                   "(" + parm.getValue("SPECIFICATION", i) +
                                   ")";
//            result.addData("ORDER_DESC",
//                           parm.getValue("ORDER_DESC", i) + SPECIFICATION);
            /*******add by zhangzc 20120612 每日费用清单增加标识**************/
            String mark = "";
            Double addRate = Double.parseDouble(parm.getValue("ZFBL1", i).equals("")?"0":parm.getValue("ZFBL1", i));
            if(addRate == 1)
            	mark = "☆";
            else if (addRate > 0.00)
            	mark = "#";
//			result.addData("ORDER_DESC", mark + parm.getValue("NHI_CODE_I", i) + " "
//					+ parm.getValue("ORDER_DESC", i) + SPECIFICATION + " "
//					+ parm.getValue("PZWH", i) + " "
//					+ parm.getDouble("ZFBL1", i) * 100 + "%");
			result.addData("ORDER_DESC", mark + parm.getValue("NHI_CODE_I", i) + " "
					+ parm.getValue("ORDER_DESC", i) + SPECIFICATION + " "
					+ parm.getValue("PZWH", i) + " ");//==liling 去掉 + parm.getDouble("ZFBL1", i) * 100 + "%"
			/*******add by zhangzc 20120612 每日费用清单增加标识**************/
            result.addData("UNIT_CHN_DESC", parm.getValue("UNIT_CHN_DESC", i));
            result.addData("OWN_PRICE", df.format(parm.getDouble("OWN_PRICE", i)));
            result.addData("DOSAGE_QTY",
                           df.format(parm.getDouble("DOSAGE_QTY", i)));
            result.addData("TOTAL", "");//liling add 20140709 小计
            result.addData("OWN_AMT", df.format(parm.getDouble("OWN_AMT", i)));//liling add 20140709 应收
            result.addData("TOT_AMT", df.format(parm.getDouble("TOT_AMT", i)));//实收
            //execDate=parm.getValue("EXEC_DATE", i).replaceAll("-", "/"); 
//            if(null !=execDate && !"".equals(execDate)){
//            	execDate=execDate.substring(0, execDate.indexOf("."));}
          //  result.addData("EXEC_DATE",execDate );//==liling 20140806 add 执行时间
//          result.addData("EXE_DEPT", parm.getValue("DEPT_CHN_DESC", i));//==liling 20140709 屏蔽执行科室
            if (i + 1 == count)
                result.addData(".TableRowLineShow", true); //如果是数据的最后一行 那么加横线
            else
                result.addData(".TableRowLineShow", false);
            rowCount++;
            ownDay += parm.getDouble("OWN_AMT", i);//==liling add 20140709 
            totDay += parm.getDouble("TOT_AMT", i);
            tot += StringTool.round(parm.getDouble("TOT_AMT", i),2);
        }
        //总计行
        //===zhangp 20120317 start
        result.addData("FEE_TYPE_DESC", "");
        result.addData("ORDER_DESC", "");
        result.addData("UNIT_CHN_DESC", "");
        result.addData("OWN_PRICE", "");
        result.addData("DOSAGE_QTY", "");
        result.addData("TOTAL", "小计:");//liling add 20140709
        result.addData("OWN_AMT", df.format(ownDay));//liling add 20140709 应收
        result.addData("TOT_AMT", df.format(totDay));//实收
        //result.addData("EXEC_DATE", "");//==liling 20140806 add 执行时间
//        result.addData("EXE_DEPT", "");//==liling 20140709 屏蔽执行科室
        result.addData(".TableRowLineShow", false);
        //===zhangp 20120317 end
        rowCount++;
        if (parm1.getDouble("REDUCE_AMT", 0) != 0){
        result.addData("FEE_TYPE_DESC", "");
        result.addData("ORDER_DESC", "");
        result.addData("UNIT_CHN_DESC", "");
        result.addData("OWN_PRICE", "");
        result.addData("DOSAGE_QTY", "");
//        result.addData("TOTAL", "合计:");//liling add 20140709
//        result.addData("OWN_AMT", "");//liling add 20140709 应收
//        result.addData("TOT_AMT", df.format(tot));//实收
        result.addData("TOTAL", "减免:") ;//==liling add
	    result.addData("OWN_AMT", "");//==liling add 
	    result.addData("TOT_AMT", df.format(-parm1.getDouble("REDUCE_AMT", 0)));
	    //result.addData("EXEC_DATE", "");//==liling 20140806 add 执行时间
//        result.addData("EXE_DEPT", "");//==liling 20140709 屏蔽执行科室
        result.addData(".TableRowLineShow", false);
        rowCount++;
        }
        //===zhangp 20120317 start
        result.setCount(rowCount );
        //===zhangp 20120317 end
        result.addData("SYSTEM", "COLUMNS", "FEE_TYPE_DESC");
        result.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        result.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        result.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
        result.addData("SYSTEM", "COLUMNS", "TOTAL");//liling add 20140709
        result.addData("SYSTEM", "COLUMNS", "OWN_AMT");//liling add 20140709
        result.addData("SYSTEM", "COLUMNS", "TOT_AMT");
        //result.addData("SYSTEM", "COLUMNS", "EXEC_DATE");//==liling 20140806 add 执行时间
//        result.addData("SYSTEM", "COLUMNS", "EXE_DEPT");//==liling 20140709 屏蔽执行科室
        //System.out.println("result = = = = = " + result);
        return result;
    }

    /**
     * 院内费用清单汇总表数据
     * @param parm TParm
     * @return TParm
     */
    public TParm getFeeType1Sum(TParm parm) {
        TParm result = new TParm();
        //查询院内费用代码
        TParm hospCode = SYSChargeHospCodeTool.getInstance().selectalldata();
        int count = hospCode.getCount();
        int rowCount = 0;
        for (int i = 0; i < count; ) {
            //第一列
            result.addData("DESC_1", hospCode.getValue("CHARGE_HOSP_DESC", i));
            result.addData("AMT_1",
                           getAmt(parm, hospCode.getValue("CHARGE_HOSP_CODE", i),
                                  "HEXP_CODE"));
            i++;
            //第二列
            result.addData("DESC_2", hospCode.getValue("CHARGE_HOSP_DESC", i));
            result.addData("AMT_2",
                           getAmt(parm, hospCode.getValue("CHARGE_HOSP_CODE", i),
                                  "HEXP_CODE"));
            i++;
            //第三列
            result.addData("DESC_3", hospCode.getValue("CHARGE_HOSP_DESC", i));
            result.addData("AMT_3",
                           getAmt(parm, hospCode.getValue("CHARGE_HOSP_CODE", i),
                                  "HEXP_CODE"));
            i++;
            //第四列
            result.addData("DESC_4", hospCode.getValue("CHARGE_HOSP_DESC", i));
            result.addData("AMT_4",
                           getAmt(parm, hospCode.getValue("CHARGE_HOSP_CODE", i),
                                  "HEXP_CODE"));
            i++;
            //第五列
            result.addData("DESC_5", hospCode.getValue("CHARGE_HOSP_DESC", i));
            result.addData("AMT_5",
                           getAmt(parm, hospCode.getValue("CHARGE_HOSP_CODE", i),
                                  "HEXP_CODE"));
            i++;
            rowCount++;
        }
        result.setCount(rowCount);
        result.addData("SYSTEM", "COLUMNS", "DESC_1");
        result.addData("SYSTEM", "COLUMNS", "AMT_1");
        result.addData("SYSTEM", "COLUMNS", "DESC_2");
        result.addData("SYSTEM", "COLUMNS", "AMT_2");
        result.addData("SYSTEM", "COLUMNS", "DESC_3");
        result.addData("SYSTEM", "COLUMNS", "AMT_3");
        result.addData("SYSTEM", "COLUMNS", "DESC_4");
        result.addData("SYSTEM", "COLUMNS", "AMT_4");
        result.addData("SYSTEM", "COLUMNS", "DESC_5");
        result.addData("SYSTEM", "COLUMNS", "AMT_5");
        return result;
    }

    /**
     * 收据费用汇总表数据
     * @param parm TParm
     * @return TParm
     */
    public TParm getFeeType2Sum(TParm parm) {
        TParm result = new TParm();
        //查询院内费用代码
        TParm hospCode = DictionaryTool.getInstance().getListAll("SYS_CHARGE");
        //===zhangp 20120306 modify start
        String sql =
			"SELECT CHARGE01,CHARGE02,CHARGE03,CHARGE04,CHARGE05,CHARGE06," +
			"CHARGE07,CHARGE08,CHARGE09,CHARGE10,CHARGE11,CHARGE12,CHARGE13," +
			"CHARGE14,CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20," +
			"CHARGE21,CHARGE22,CHARGE23,CHARGE24,CHARGE25,CHARGE26," +
			"CHARGE27,CHARGE28,CHARGE29,CHARGE30 " +
			"FROM BIL_RECPPARM WHERE ADM_TYPE = 'I'";
		TParm temp = new TParm(TJDODBTool.getInstance().select(sql));
		TParm hospCode2 = new TParm();
		for (int i = 1; i < 31; i++) {
			if(i==5){
				hospCode2.addData("NAME", "西药费");
				hospCode2.addData("ID", "");
			}
			for (int j = 0; j < hospCode.getCount(); j++) {
				if(i<10){
					if(hospCode.getData("ID", j).equals(temp.getData("CHARGE0"+i, 0))){
						hospCode2.addData("NAME", hospCode.getData("NAME", j));
						hospCode2.addData("ID", hospCode.getData("ID", j));
					}
				}else{
					if(hospCode.getData("ID", j).equals(temp.getData("CHARGE"+i, 0))){
						hospCode2.addData("NAME", hospCode.getData("NAME", j));
						hospCode2.addData("ID", hospCode.getData("ID", j));
					}
				}
			}
		}
//		System.out.println("hospCode2==="+hospCode2);
        int count = hospCode2.getCount("ID");
        int rowCount = 0;
        for (int i = 0; i < count; ) {
            //第一列
            result.addData("DESC_1", hospCode2.getValue("NAME", i));
            result.addData("AMT_1",
                           getAmt(parm, hospCode2.getValue("ID", i), "REXP_CODE"));
            i++;
            //第二列
            result.addData("DESC_2", hospCode2.getValue("NAME", i));
            result.addData("AMT_2",
                           getAmt(parm, hospCode2.getValue("ID", i), "REXP_CODE"));
            i++;
            //第三列
            result.addData("DESC_3", hospCode2.getValue("NAME", i));
            result.addData("AMT_3",
                           getAmt(parm, hospCode2.getValue("ID", i), "REXP_CODE"));
            i++;
            //第四列
            result.addData("DESC_4", hospCode2.getValue("NAME", i));
            result.addData("AMT_4",
                           getAmt(parm, hospCode2.getValue("ID", i), "REXP_CODE"));
            i++;
            //第五列
            if(i==4){
            	result.addData("DESC_5", hospCode2.getValue("NAME", i));
            	result.addData("AMT_5",result.getDouble("AMT_4", 0)+result.getDouble("AMT_3", 0));
            }else{
            	result.addData("DESC_5", hospCode2.getValue("NAME", i));
                result.addData("AMT_5",
                               getAmt(parm, hospCode2.getValue("ID", i), "REXP_CODE"));
            }
            i++;
            rowCount++;
        }
//        System.out.println("result===="+result);
      //===zhangp 20120306 modify end
        result.setCount(rowCount);
        result.addData("SYSTEM", "COLUMNS", "DESC_1");
        result.addData("SYSTEM", "COLUMNS", "AMT_1");
        result.addData("SYSTEM", "COLUMNS", "DESC_2");
        result.addData("SYSTEM", "COLUMNS", "AMT_2");
        result.addData("SYSTEM", "COLUMNS", "DESC_3");
        result.addData("SYSTEM", "COLUMNS", "AMT_3");
        result.addData("SYSTEM", "COLUMNS", "DESC_4");
        result.addData("SYSTEM", "COLUMNS", "AMT_4");
        result.addData("SYSTEM", "COLUMNS", "DESC_5");
        result.addData("SYSTEM", "COLUMNS", "AMT_5");
        result.setData("Visible", true);
        return result;
    }

    /**
     * 根据CODE计算每种费用的总计
     * @param parm TParm
     * @param code String
     * @param colunm String
     * @return double
     */
    private String getAmt(TParm parm, String code, String colunm) {
        double amt = 0;
        DecimalFormat df = new DecimalFormat("0.00");
        int count = parm.getCount();
        for (int j = count - 1; j >= 0; j--) {
            if (code.equals(parm.getValue(colunm, j))) {
                amt += parm.getDouble("TOT_AMT", j);
                parm.removeRow(j);
            }
        }
        return df.format(amt);
    }

    /**
     * 取得病人的预交金总额
     * @param CASE_NO String
     * @return double
     */
    private double getYJJ(String CASE_NO) {
        TParm yjParm = new TParm();
        yjParm.setData("CASE_NO", CASE_NO);
        TParm yjj = BILPayTool.getInstance().selAllDataByRecpNo(yjParm);
        if (yjj.getErrCode() < 0) {
            return 0.0;
        }
        double atm = 0;
        for (int i = 0; i < yjj.getCount(); i++) {
            atm += yjj.getDouble("PRE_AMT", i);
        }
        return atm;
    }

    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("START_DATE;END_DATE;DEPT_CODE;STATION_CODE;BED_NO;MR_NO;IPD_NO;START_PD;END_PD;PRINT_SUM;PRINT_DATE");
        this.callFunction("UI|START_PD|setEnabled", false);
        this.callFunction("UI|END_PD|setEnabled", false);
        this.callFunction("UI|STATION_CODE|onQuery");
        this.callFunction("UI|BED_NO|onQuery");
        this.clearValue("YELLOW_SIGN;RED_SIGN");
        dateInit();
        ((TTable)this.getComponent("TABLE")).removeRowAll();
    }

    /**
     * 科室选择事件
     */
    public void onDEPT_CODE() {
        this.clearValue("STATION_CODE;BED_NO");
        this.callFunction("UI|STATION_CODE|onQuery");
        this.callFunction("UI|BED_NO|onQuery");
    }

    /**
     * 病区选择事件
     */
    public void onSTATION_CODE() {
        this.clearValue("BED_NO");
        this.callFunction("UI|BED_NO|onQuery");
    }

    /**
     * 选择打印时段
     */
    public void onPRINT_DATE() {
        if (this.getValueBoolean("PRINT_DATE")) {
            this.callFunction("UI|START_PD|setEnabled", true);
            this.callFunction("UI|END_PD|setEnabled", true);
        } else {
            this.callFunction("UI|START_PD|setEnabled", false);
            this.callFunction("UI|END_PD|setEnabled", false);
        }
    }

    /**
     * 全选事件
     */
    public void onSEECLTALL() {
        TTable table = (TTable)this.getComponent("TABLE");
        int count = table.getRowCount();
        if (this.getValueBoolean("SELECTALL")) {
            for (int i = 0; i < count; i++) {
                table.setItem(i, "FLG", "Y");
            }
        } else if (!this.getValueBoolean("SELECTALL")) {
            for (int i = 0; i < count; i++) {
                table.setItem(i, "FLG", "N");
            }
        }
    }

    /**
     * 打印催账单
     */
    public void onPush() {
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
//        int row = table.getSelectedRow();
//        if (row < 0) {
//            this.messageBox_("请选择一行数据");
//            return;
//        }
        //=========pangben 2014-4-15 修改勾选打印崔账单
        TParm tableParm =table.getParmValue();
        boolean flg=false;
        for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getBoolean("FLG",i)) {
				flg=true;
				break;
			}
		}
        if (!flg) {
			this.messageBox("请选择要打印的数据");
			return;
		}
        for (int i = 0; i <tableParm.getCount(); i++) {
        	if (!tableParm.getBoolean("FLG",i)) {
        		continue;
        	}
            TParm parm = new TParm();
            parm.setData("CASE_NO", tableParm.getValue("CASE_NO",i));
            TParm admInp = ADMInpTool.getInstance().selectall(parm);
            if (admInp.getErrCode() != 0) {
                this.messageBox("E0005");
                return;
            }
            Pat pat = Pat.onQueryByMrNo(admInp.getValue("MR_NO", 0));
            TParm print = new TParm();
            print.setData("patName", "TEXT", pat.getName());
            print.setData("hosp", "TEXT", Operator.getHospitalCHNFullName());
            print.setData("printDate", "TEXT",
                          StringTool.getString(SystemTool.getInstance().getDate(),
                                               "yyyy年MM月dd日"));
            print.setData("inDate", "TEXT",
                          StringTool.getString(admInp.getTimestamp("IN_DATE", 0),
                                               "yyyy年MM月dd日"));
            String dept = StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC",
                                             " DEPT_CODE='" +
                                             admInp.getValue("DEPT_CODE", 0) +
                                             "'") +
                          " (床号：" +
                          StringUtil.getDesc("SYS_BED", "BED_NO_DESC",
                                             "BED_NO='" +
                                             admInp.getValue("BED_NO", 0) + "'") +
                          "  病案号：" + pat.getMrNo() + ")";
            print.setData("dept", "TEXT", dept);
            print.setData("TOTAL_AMT", "TEXT", admInp.getValue("TOTAL_AMT", 0));
            print.setData("BILPAY", "TEXT", admInp.getDouble("TOTAL_BILPAY", 0));
            print.setData("CUR_AMT", "TEXT", admInp.getValue("CUR_AMT", 0));
            this.openPrintDialog("%ROOT%\\config\\prt\\BIL\\BILReminder.jhw",
                                 print);

        }
    }

    /**
     * 住院费用明细
     * =========================pangben modify 20110815
     */
    public void fyXML() {
        //1.构造数据
        TParm inparm = new TParm();
        int count = 0;
        String Time = StringTool.getString(SystemTool.getInstance().getDate(),
                                           "mmss");
        TTable table = (TTable)this.getComponent("TABLE");
        int row=table.getSelectedRow();
        TParm parm = parmData();
        if(parm.getCount()<=0){
            this.messageBox("没有生成的数据");
            return;
        }
        for (int i = 0; i < parm.getCount(); i++) {
            inparm.insertData("ID", i, Time + (i < 10 ? "0" + i : i)); //行号
            if (parm.getValue("ORDER_CAT1_CODE", i).contains("PHA"))
                inparm.insertData("BZ", i, 0); //标志
            else
                inparm.insertData("BZ", i, 1); //标志
            inparm.insertData("XH", i, table.getParmValue().getValue("CASE_NO",row)); //入院序号
            inparm.insertData("SJ", i,
                              StringTool.getString(parm.getTimestamp(
                    "DATE_BILL",
                    i),
                    "yyyyMMdd")); //费用发生时间 YYYYMMDD
            inparm.insertData("ZBM", i, parm.getValue("ORDER_CODE", i)); //药品项目自编码
            inparm.insertData("SL", i, parm.getValue("DOSAGE_QTY", i)); //数量
            inparm.insertData("DJ", i, parm.getValue("OWN_PRICE", i)); //单价
            inparm.insertData("YSM", i, parm.getValue("DR_CODE", i)); //主治医生编码
            inparm.insertData("YS", i, parm.getValue("USER_NAME", i)); //主治医生姓名
            inparm.insertData("YHLB", i, "0"); //优惠类别
            inparm.insertData("YHJ", i, "0"); //优惠价
            count++;
        }
        inparm.addData("SYSTEM", "COLUMNS", "ID");
        inparm.addData("SYSTEM", "COLUMNS", "BZ");
        inparm.addData("SYSTEM", "COLUMNS", "XH");
        inparm.addData("SYSTEM", "COLUMNS", "SJ");
        inparm.addData("SYSTEM", "COLUMNS", "ZBM");
        inparm.addData("SYSTEM", "COLUMNS", "SL");
        inparm.addData("SYSTEM", "COLUMNS", "DJ");
        inparm.addData("SYSTEM", "COLUMNS", "YSM");
        inparm.addData("SYSTEM", "COLUMNS", "YS");
        inparm.addData("SYSTEM", "COLUMNS", "YHLB");
        inparm.addData("SYSTEM", "COLUMNS", "YHJ");
        inparm.setCount(count);
//        System.out.println("=======inparm=============" + inparm);
//        //2.生成文件
//                NJCityInwDriver.createXMLFile(inparm, "c:/NGYB/zyfymx.xml");
//        this.messageBox("生成成功");
    }
    /**
     * 获得xml文件数据
     * @return TParm
     * =========================pangben modify 20110815
     */
    private TParm parmData() {
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        int count = table.getRowCount();
        if (count <= 0) {
            return null;
        }
        int row = table.getSelectedRow();
        if(row<=0){
            this.messageBox("请选择一条病患信息");
            return null;
        }
        String startDate = "";
        String endDate = "";
        //判断查询的时间段
        if (this.getValueBoolean("PRINT_DATE")) { //选择打印时段
            if (this.getValueString("START_PD").length() <= 0) {
                this.messageBox_("请选择打印时段的起始日期");
                return null;
            }
            if (this.getValueString("END_PD").length() <= 0) {
                this.messageBox_("请选择打印时段的截至日期");
                return null;
            }
            startDate = StringTool.getString((Timestamp)this.getValue(
                    "START_PD"), "yyyy/MM/dd");
            endDate = StringTool.getString((Timestamp)this.getValue("END_PD"),
                                           "yyyy/MM/dd");
        }
        TParm parm = new TParm();
        if (this.getValueBoolean("FEE_TYPE1")) { //收据费用
            if (this.getValueBoolean("PRINT_TYPE2")) { //每日清单
                //如果是以“每日费用”的格式打印 那么需要根据BILL_DATE进行排序 为了方便筛选每天的数据
                parm.setData("ORDER_BY3", "Y");
            } else {
                //如果是以“汇总清单”的格式打印 那么只根据收据费用代码排序就可以了 不用筛选每一天的
                parm.setData("ORDER_BY1", "Y");
            }
        } else if (this.getValueBoolean("FEE_TYPE2")) { //院内费用
            if (this.getValueBoolean("PRINT_TYPE2")) { //每日清单
                //如果是以“每日费用”的格式打印 那么需要根据BILL_DATE进行排序 为了方便筛选每天的数据
                parm.setData("ORDER_BY4", "Y");
            } else {
                parm.setData("ORDER_BY2", "Y");
            }
        }
        String CASE_NO = table.getItemString(row, "CASE_NO");
        parm.setData("CASE_NO", CASE_NO);
        parm.setData("DATE_S", startDate);
        parm.setData("DATE_E", endDate);
        parm.setData("REGION_CODE", Operator.getRegion());
        TParm result;
        if (this.getValueBoolean("PRINT_TYPE2")) { //每日清单
            result = BILDailyFeeQueryTool.getInstance().selectPrintData(
                    parm);
        } else {
            result = BILDailyFeeQueryTool.getInstance().
                     selectPrintDataForHZ(parm);
        }
        return result;
    }
    /**
	 * 
	* @Title: onChnEnFill
	* @Description: TODO(中英文费用清单)
	* @author pangben 2015-7-23 
	* @throws
	 */
	public void onChnEnFill(){
		TTable table = (TTable)this.getComponent("TABLE");
		int row = table.getSelectedRow();
        if(row<0){
            this.messageBox("请选择一条病患信息");
            return;
        }
        String caseNo = table.getItemString(row, "CASE_NO");
		TParm print=OPBFeeListPrintTool.getInstance().getSumPrintData(caseNo, Operator.getID(), "I");
		boolean unPackFlg = this.getValueBoolean("CHN_EN_FLG");
		if (unPackFlg) {
			this.openPrintDialog(IReportTool.getInstance().getReportPath("OPDFeeListChn_EnV45_1.jhw"),
					IReportTool.getInstance().getReportParm("OPDFeeListChn_EnV45.class", print));
		}else{
			this.openPrintDialog(IReportTool.getInstance().getReportPath("OPDFeeListChn_EnV45.jhw"),
					IReportTool.getInstance().getReportParm("OPDFeeListChn_EnV45.class", print));
		}

	}
}



