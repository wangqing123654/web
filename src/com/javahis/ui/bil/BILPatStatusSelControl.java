package com.javahis.ui.bil;

import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.control.TControl;

import jdo.adm.ADMInpTool;
import jdo.bil.BILComparator;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.table.TableModel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.util.Compare;
import com.javahis.util.OdiUtil;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title:病患状态查询 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BILPatStatusSelControl extends TControl {
    private BILComparator compare = new BILComparator();//modify by wanglong 20130828
    private boolean ascending = false;
    private int sortColumn =-1;
    /**
     * 1
     */
    private static String TABLE1 = "TABLE1";
    /**
     * 2
     */
    private static String TABLE2 = "TABLE2";
    /**
     * 3
     */
    private static String TABLE3 = "TABLE3";
    /**
     * 4
     */
    private static String TABLE4 = "TABLE4";
    /**
     * 住院天数>=85,<90()
     */
    Color nsNodeColor = new Color(128, 0, 128);
    // 住院天数>=90为红色
    Color red = new Color(255, 0, 0);
    
    /**
     * 初始化参数
     */
    public void onInitParameter() {
//        this.setPopedem("SYSDBA",true);
    }

    public void onInit() {
        super.onInit();
        this.initPage();
    }

    /**
     * 得到TTabbedPane
     * @param tag String
     * @return TTabbedPane
     */
    public TTabbedPane getTTabbedPane(String tag) {
        return (TTabbedPane)this.getComponent(tag);
    }

    /**
     * 得到TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    /**
     * 初始化界面
     */
    public void initPage() {
        // 权限
        onInitPopeDem();
        // 当前时间
        Timestamp today = SystemTool.getInstance().getDate();
        // 获取选定日期的前一天的日期
        Timestamp yesterday = StringTool.rollDate(today, -1);
        this.setValue("START_DATE", yesterday);
        this.setValue("END_DATE", today);
        callFunction("UI|save|setEnabled", false);
        // 排序监听
        addListener(this.getTTable(TABLE1));
        addListener(this.getTTable(TABLE2));
        addListener(this.getTTable(TABLE4));
    }

    /**
     * 初始化权限
     */
    public void onInitPopeDem() {
        // 普通
        if (this.getPopedem("NORMAL")) {
            this.setValue("DEPT_CODE1", Operator.getDept());
            this.setValue("DEPT_CODE2", Operator.getDept());
            this.setValue("DEPT_CODE3", Operator.getDept());
            this.setValue("STATION_CODE1", Operator.getStation());
            this.setValue("STATION_CODE2", Operator.getStation());
            this.setValue("STATION_CODE3", Operator.getStation());
            getTTabbedPane("TTABBEDPANE").setEnabledAt(1, false);
            this.callFunction("UI|DEPT_CODE1|setEnabled", false);
            this.callFunction("UI|DEPT_CODE2|setEnabled", false);
            this.callFunction("UI|DEPT_CODE3|setEnabled", false);
            this.callFunction("UI|STATION_CODE1|setEnabled", false);
            this.callFunction("UI|STATION_CODE2|setEnabled", false);
            this.callFunction("UI|STATION_CODE3|setEnabled", false);
            return;
        }
        // 一般
        if (this.getPopedem("SYSOPER")) {
            this.setValue("DEPT_CODE1", Operator.getDept());
            this.setValue("DEPT_CODE2", Operator.getDept());
            this.setValue("DEPT_CODE3", Operator.getDept());
            this.setValue("STATION_CODE1", Operator.getStation());
            this.setValue("STATION_CODE2", Operator.getStation());
            this.setValue("STATION_CODE3", Operator.getStation());
            getTTabbedPane("TTABBEDPANE").setEnabledAt(1, false);
            this.callFunction("UI|DEPT_CODE1|setEnabled", true);
            this.callFunction("UI|DEPT_CODE2|setEnabled", true);
            this.callFunction("UI|DEPT_CODE3|setEnabled", true);
            this.callFunction("UI|STATION_CODE1|setEnabled", true);
            this.callFunction("UI|STATION_CODE2|setEnabled", true);
            this.callFunction("UI|STATION_CODE3|setEnabled", true);
            return;
        }
        // 最高
        if (this.getPopedem("SYSDBA")) {
            this.setValue("DEPT_CODE1", Operator.getDept());
            this.setValue("DEPT_CODE2", Operator.getDept());
            this.setValue("DEPT_CODE3", Operator.getDept());
            this.setValue("STATION_CODE1", Operator.getStation());
            this.setValue("STATION_CODE2", Operator.getStation());
            this.setValue("STATION_CODE3", Operator.getStation());
            getTTabbedPane("TTABBEDPANE").setEnabledAt(1, true);
            this.callFunction("UI|DEPT_CODE1|setEnabled", true);
            this.callFunction("UI|DEPT_CODE2|setEnabled", true);
            this.callFunction("UI|DEPT_CODE3|setEnabled", true);
            this.callFunction("UI|STATION_CODE1|setEnabled", true);
            this.callFunction("UI|STATION_CODE2|setEnabled", true);
            this.callFunction("UI|STATION_CODE3|setEnabled", true);
            return;
        }
        getTTabbedPane("TTABBEDPANE").setEnabledAt(0, false);
        getTTabbedPane("TTABBEDPANE").setEnabledAt(1, false);
        getTTabbedPane("TTABBEDPANE").setEnabledAt(2, false);
        getTTabbedPane("TTABBEDPANE").setEnabledAt(3, false);
        callFunction("UI|query|setEnabled", false);
        callFunction("UI|print|setEnabled", false);
        callFunction("UI|clear|setEnabled", false);
        this.messageBox("未设置权限！");
    }

    /**
     * 查询
     */
    public void onQuery() {
        // TTable table = this.getTTable(TABLE1);
        // int rowCount1 = table.getDataStore().rowCount();
        // Map m = new HashMap();
        // for (int i = 0; i < rowCount1; i++) {
        // m.put(i, new Color(255, 255, 0));
        // }
        // table.setRowColorMap(m);
        // this.getTTable(TABLE1).removeRowAll();
        // // ===huangtt20130428 start
        // TTable table4 = this.getTTable(TABLE4);
        // int rowCount4 = table.getDataStore().rowCount();
        // Map m4 = new HashMap();
        // for (int i = 0; i < rowCount4; i++) {
        // m4.put(i, new Color(255, 255, 0));
        // }
        // table.setRowColorMap(m4);
        // this.getTTable(TABLE4).removeRowAll();
        // // ===huangtt20130428 end
        int selTTabbendPane = getTTabbedPane("TTABBEDPANE").getSelectedIndex();
        TParm queryParm = new TParm();
        switch (selTTabbendPane) {
            case 0:
            	if(this.getValue("BILL_STATUS1").toString().length()<=0){
            		this.messageBox("结账状态不能为空");
            		return;
            	}
                queryParm = new TParm(this.getDBTool().select(getQuerySql(0)));
                // System.out.println("病患状态查询" + queryParm);
                // System.out.println("人数" + queryParm.getCount("CASE_NO"));
                // 设置人数
                //============pangben 2014-8-4 修改已经操作预交金退费的病患也可以查询出数据
                TParm bilParm=new TParm();
                TParm admParm=new TParm();
                String sql="";
                Timestamp sysDate = SystemTool.getInstance().getDate();
                for (int i = 0; i < queryParm.getCount(); i++) {
                	sql="SELECT sum(nvl(PRE_AMT, 0)) TOTAL_BILPAY  FROM BIL_PAY  D WHERE CASE_NO='"+queryParm.getValue("CASE_NO",i)+"' "
                        + "       AND REFUND_FLG = 'N' "
                        + "       AND TRANSACT_TYPE IN ('01', '04') "
                        + "       AND RESET_RECP_NO IS NULL";
                	bilParm= new TParm(this.getDBTool().select(sql));
                	if (bilParm.getCount()>0) {
                		queryParm.setData("TOTAL_BILPAY",i,bilParm.getDouble("TOTAL_BILPAY",0));
					}
                	// 计算住院天数
                    Timestamp tp = queryParm.getTimestamp("DS_DATE", i);
                    if (tp == null) {
                        int days = 0;
                        if (queryParm.getTimestamp("IN_DATE", i) == null) {
                            queryParm.addData("DAYNUM", "");
                        } else {
                            days =
                                    StringTool.getDateDiffer(StringTool.setTime(sysDate, "00:00:00"), 
                                                             StringTool.setTime(queryParm.getTimestamp("IN_DATE", i), "00:00:00"));
                            queryParm.addData("DAYNUM", days == 0 ? 1 : days);
                        }
                    } else {
                        int days = 0;
                        if (queryParm.getTimestamp("IN_DATE", i) == null) {
                            queryParm.addData("DAYNUM", "");
                        } else {
                            days =
                                    StringTool.getDateDiffer(StringTool.setTime(queryParm.getTimestamp("DS_DATE", i), "00:00:00"), 
                                                             StringTool.setTime(queryParm.getTimestamp("IN_DATE", i), "00:00:00"));
                            queryParm.addData("DAYNUM", days == 0 ? 1 : days);
                        }
                    }
                    //==start====add by kangy 20170901 套餐病人预交金余额=预交金总额-【套餐结转】-套外金额
                  	sql="SELECT CASE_NO FROM ADM_INP WHERE CASE_NO='"+queryParm.getValue("CASE_NO",i)+"' AND LUMPWORK_CODE IS NOT NULL ";
            	admParm= new TParm(this.getDBTool().select(sql));
            	if (admParm.getCount()>0) {
            		queryParm.setData("CUR_AMT",i,bilParm.getDouble("TOTAL_BILPAY",0)-queryParm.getDouble("TCJZ",i)-queryParm.getDouble("OUT_AMT",i)+queryParm.getDouble("REDUCE_AMT",i));
				}
                    //==end======add by kangy 20170901 套餐病人预交金余额=预交金总额-【套餐结转】-套外金额
				}
                this.setValue("ALLPERSON", queryParm.getCount("CASE_NO") < 0 ? 0 : queryParm.getCount("CASE_NO"));
                //int row = queryParm.getCount();
                break;
            case 1:
                queryParm = new TParm(this.getDBTool().select(getQuerySql(1)));
                break;
            case 2:
                queryParm = new TParm(this.getDBTool().select(getQuerySql(2)));
                break;
            case 3:
                queryParm = new TParm(this.getDBTool().select(getQuerySql(3)));
                int rowCount = queryParm.getCount();
                for (int i = 0; i < rowCount; i++) {
                    OdiUtil.getInstance();
                    queryParm.setData("BIRTH_DATE", i, OdiUtil.showAge(queryParm.getTimestamp("BIRTH_DATE", i), queryParm.getTimestamp("IN_DATE", i)));
                }
                this.setValue("ONLYCOUNT", rowCount);
                break;
        }

        this.getTTable("TABLE" + (selTTabbendPane + 1)).setParmValue(queryParm);
        // 医保病人住院天数超过85天或90天的颜色设置
        this.setColor(queryParm);
    }
    
    /**
     * 设置颜色
     */ 
    public void setColor(TParm queryParm) {
        int selTTabbendPane = getTTabbedPane("TTABBEDPANE").getSelectedIndex();
        switch (selTTabbendPane) {
            case 0:
                TTable table = this.getTTable(TABLE1);
                // System.out.println("22222===");
                int Count = queryParm.getCount();
                // System.out.println("33333==="+Count);
                for (int i = 0; i < Count; i++) {
                    if (queryParm.getInt("DAYNUM", i) >= 85 && queryParm.getInt("DAYNUM", i) < 90
                            && !queryParm.getData("CTZ1_CODE", i).equals("99")) {
                        table.setRowColor(i, nsNodeColor);
                    } else if (queryParm.getInt("DAYNUM", i) >= 90
                            && !queryParm.getData("CTZ1_CODE", i).equals("99")) {
                        table.setRowColor(i, red);
                    }
                }
                break;
        }
    }
    
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * 返回查询语句
     * @param tableIndex int
     * @return String
     */
    public String getQuerySql(int tableIndex) {
        String sql = "";
        //==========pangben modify 20110704 start
        StringBuffer region = new StringBuffer();
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            region.append(" AND A.REGION_CODE='" + Operator.getRegion() + "' ");
        } else
            region.append("");
        //==========pangben modify 20110704 stop
        switch (tableIndex) {
        case 0:
            //================modify by wanglong 20121229========================
            sql = "SELECT B.PAT_NAME,A.MR_NO,A.IPD_NO,A.CASE_NO,B.SEX_CODE,B.BIRTH_DATE,A.DEPT_CODE,"
                    + "  A.STATION_CODE,C.BED_NO_DESC,A.IN_DATE,A.CTZ1_CODE,A.CUR_AMT,NVL(D.REDUCE_AMT,0) REDUCE_AM,NVL(E.OUT_AMT,0) OUT_AMT," +
                    		"NVL(F.TCJZ,0) TCJZ,A.TOTAL_AMT,A.BILL_STATUS, A.DS_DATE,0 TOTAL_BILPAY"
                    + "        FROM ADM_INP A,SYS_PATINFO B,SYS_BED C," +
                    		"(SELECT CASE WHEN  REDUCE_AMT IS NULL THEN 0 ELSE REDUCE_AMT END REDUCE_AMT,CASE_NO FROM BIL_IBS_RECPM WHERE REFUND_CODE IS NULL  AND AR_AMT >0)D," +
                    		"(SELECT  SUM(TOT_AMT) OUT_AMT,CASE_NO  FROM IBS_ORDD WHERE OWN_PRICE <> 0 AND INCLUDE_FLG = 'Y' GROUP BY CASE_NO)E," +
                    		"(SELECT CASE_NO, SUM(PRE_AMT) TCJZ FROM BIL_PAY WHERE PAY_TYPE='TCJZ' GROUP BY CASE_NO)F "
                    + "       WHERE A.MR_NO = B.MR_NO "
                    + "         AND A.BED_NO = C.BED_NO "
                    + "         AND A.CANCEL_FLG = 'N' "
                    + region.toString()
                    + getWhereStr(0)
                    +"AND A.CASE_NO=D.CASE_NO(+)  AND A.CASE_NO=E.CASE_NO(+) AND A.CASE_NO=F.CASE_NO(+)";
            //================modify end=========================================
            break;
        case 1:
            //================modify by wanglong 20121229========================
            sql = "  SELECT STOP_BILL_FLG,PAT_NAME,CASE_NO,MR_NO,IPD_NO,sum(nvl(PRE_AMT, 0))-TCJZ-OUT_AMT+REDUCE_AMT CUR_AMT,REDUCE_AMT,OUT_AMT,TCJZ,DEPT_CODE,STATION_CODE,SEX_CODE,"
                    + "     BIRTH_DATE,BED_NO_DESC,IN_DATE,CTZ1_CODE,sum(nvl(PRE_AMT, 0)) TOTAL_BILPAY,'N' AS SUM_FLG "
                    + "FROM ("
                    + "SELECT A.STOP_BILL_FLG,B.PAT_NAME,A.CASE_NO,A.MR_NO,A.IPD_NO,A.CUR_AMT, NVL(E.REDUCE_AMT,0) REDUCE_AMT," 
                    + " NVL(F.OUT_AMT,0) OUT_AMT, NVL(G.TCJZ,0) TCJZ,A.DEPT_CODE,"
                    + "             A.STATION_CODE,B.SEX_CODE,B.BIRTH_DATE,C.BED_NO_DESC,A.IN_DATE,A.CTZ1_CODE,"
                    + "             D.RECEIPT_NO,D.REFUND_FLG,D.TRANSACT_TYPE,D.RESET_RECP_NO,D.PRE_AMT ,A.CANCEL_FLG"
                    + "        FROM ADM_INP A, SYS_PATINFO B, SYS_BED C, BIL_PAY D," 
                    + "( SELECT CASE WHEN  REDUCE_AMT IS NULL THEN 0 ELSE REDUCE_AMT END REDUCE_AMT,CASE_NO FROM BIL_IBS_RECPM WHERE REFUND_CODE IS NULL  AND AR_AMT >0) E," 
                    + "(SELECT  SUM(TOT_AMT) OUT_AMT,CASE_NO  FROM IBS_ORDD WHERE OWN_PRICE <> 0 AND INCLUDE_FLG = 'Y' GROUP BY CASE_NO) F," 
                    + "(SELECT CASE_NO, SUM(PRE_AMT) TCJZ FROM BIL_PAY WHERE PAY_TYPE='TCJZ' GROUP BY CASE_NO) G "
                    + "       WHERE A.MR_NO = B.MR_NO "
                    + "         AND A.BED_NO = C.BED_NO "
                    + "         AND A.DS_DATE IS NULL "
                    + region.toString()
                    + getWhereStr(1)
                    + "         AND A.CASE_NO = D.CASE_NO(+) "
                    +"           AND A.CANCEL_FLG = 'N' "      //取消住院的病患过滤  chenxi
                    +" AND A.CASE_NO=E.CASE_NO(+) " 
                    +" AND A.CASE_NO=F.CASE_NO(+) " 
                    +" AND A.CASE_NO=G.CASE_NO(+)"
                    + "     ) "
                    + "WHERE (RECEIPT_NO IS NOT NULL "
                    + "       AND REFUND_FLG = 'N' "
                    + "       AND TRANSACT_TYPE IN ('01', '04') )"
                    //+ "       AND RESET_RECP_NO IS NULL ) "//=====pangben 2014-5-14 添加注释作废的预交金数据也要显示病患信息
                    + "   OR RECEIPT_NO IS NULL "
                    + "GROUP BY STOP_BILL_FLG,PAT_NAME,CASE_NO,MR_NO,IPD_NO,CUR_AMT,DEPT_CODE,STATION_CODE,"
                    + "         SEX_CODE,BIRTH_DATE,BED_NO_DESC,IN_DATE,CTZ1_CODE, REDUCE_AMT, OUT_AMT,TCJZ";
            //System.out.println("sql--->"+sql);
            //================modify end=========================================
            break;
        case 2:
            sql =
                    "SELECT A.DEPT_CODE,A.STATION_CODE,A.BED_NO,B.PAT_NAME,A.MR_NO,A.IPD_NO," +
                    "B.SEX_CODE,A.CTZ1_CODE,A.IN_DATE,A.PATIENT_STATUS FROM ADM_INP A,SYS_PATINFO B,SYS_BED C" +
                    " WHERE A.MR_NO=B.MR_NO AND A.BED_NO=C.BED_NO AND DS_DATE IS NULL AND PATIENT_STATUS IN ('S0','S1') " +
                    region.toString() + getWhereStr(2);
            break;
        case 3:
            sql = "SELECT A.DEPT_CODE,A.STATION_CODE,C.BED_NO_DESC,B.PAT_NAME,A.MR_NO,A.IPD_NO,D.ICD_CHN_DESC,A.NURSING_CLASS,B.BIRTH_DATE,B.SEX_CODE,A.IN_DATE,A.VS_DR_CODE FROM ADM_INP A,SYS_PATINFO B,SYS_BED C,SYS_DIAGNOSIS D WHERE A.MR_NO=B.MR_NO AND A.BED_NO=C.BED_NO AND A.MAINDIAG=D.ICD_CODE(+) AND A.DS_DATE IS NULL " +
                  region.toString() + getWhereStr(3);
            break;
        }
        //System.out.println("sql=============DDDDD=======" + sql); 
        return sql;
    }

    /**
     * 得到条件
     * @param tableIndex int
     * @return String
     */
    public String getWhereStr(int tableIndex) {
        String whereStr = "";
        switch (tableIndex) {
        case 0:
            String billStatus = this.getValueString("BILL_STATUS1");
            String deptCode = this.getValueString("DEPT_CODE1");
            String stationCode = this.getValueString("STATION_CODE1");
            if (billStatus.length() > 0) {
                if ("0".equals(this.getValueString("BILL_STATUS1"))) {
                    whereStr += " AND A.DS_DATE IS NULL ";
                } else {
                    whereStr += " AND A.BILL_STATUS='" + billStatus + "' ";
                }
            }
            if (deptCode.length() > 0) {
                whereStr += " AND A.DEPT_CODE='" + deptCode + "' ";
            }
            if (stationCode.length() > 0) {
                whereStr += " AND A.STATION_CODE='" + stationCode + "' ";
            }
            if ("1".equals(this.getValueString("BILL_STATUS1"))) {
                String sDate = StringTool.getString((Timestamp)this.getValue(
                        "START_DATE"), "yyyyMMdd");
                String eDate = StringTool.getString((Timestamp)this.getValue(
                        "END_DATE"), "yyyyMMdd");
                whereStr += " AND A.DS_DATE BETWEEN TO_DATE('" + sDate +"000000"+
                        "','YYYYMMDDHH24MISS') AND TO_DATE('" + eDate +"235959"+ "','YYYYMMDDHH24MISS') ";
            }

            if ("2".equals(this.getValueString("BILL_STATUS1"))) {
                String sDate = StringTool.getString((Timestamp)this.getValue(
                        "START_DATE"), "yyyyMMdd");
                String eDate = StringTool.getString((Timestamp)this.getValue(
                        "END_DATE"), "yyyyMMdd");
                whereStr += " AND A.DS_DATE BETWEEN TO_DATE('" + sDate +"000000"+
                        "','YYYYMMDDHH24MISS') AND TO_DATE('" + eDate +"235959"+ "','YYYYMMDDHH24MISS') ";
            }

            if ("3".equals(this.getValueString("BILL_STATUS1"))) {
                String sDate = StringTool.getString((Timestamp)this.getValue(
                        "START_DATE"), "yyyyMMdd");
                String eDate = StringTool.getString((Timestamp)this.getValue(
                        "END_DATE"), "yyyyMMdd");
                whereStr += " AND A.DS_DATE BETWEEN TO_DATE('" + sDate +"000000"+
                        "','YYYYMMDDHH24MISS') AND TO_DATE('" + eDate +"235959"+ "','YYYYMMDDHH24MISS') ";
            }
            if ("4".equals(this.getValueString("BILL_STATUS1"))) {
                String sDate = StringTool.getString((Timestamp)this.getValue(
                        "START_DATE"), "yyyyMMdd");
                String eDate = StringTool.getString((Timestamp)this.getValue(
                        "END_DATE"), "yyyyMMdd");
                whereStr += " AND A.DS_DATE BETWEEN TO_DATE('" + sDate +"000000"+
                        "','YYYYMMDDHH24MISS') AND TO_DATE('" + eDate +"235959"+ "','YYYYMMDDHH24MISS') ";
            }
            break;
        case 1:
            String deptCode2 = this.getValueString("DEPT_CODE2");
            String stationCode2 = this.getValueString("STATION_CODE2");
            if (deptCode2.length() > 0) {
                whereStr += " AND A.DEPT_CODE='" + deptCode2 + "' ";
            }
            if (stationCode2.length() > 0) {
                whereStr += " AND A.STATION_CODE='" + stationCode2 + "' ";
            }
            break;
        case 2:
            String deptCode3 = this.getValueString("DEPT_CODE3");
            String stationCode3 = this.getValueString("STATION_CODE3");
            if (deptCode3.length() > 0) {
                whereStr += " AND A.DEPT_CODE='" + deptCode3 + "' ";
            }
            if (stationCode3.length() > 0) {
                whereStr += " AND A.STATION_CODE='" + stationCode3 + "' ";
            }
            break;
        case 3:
            //AND IN_DATE <=TO_DATE('20100927184700','YYYYMMDDHH24MISS')
            String payType = this.getValueString("PAYTYPE");
            if (payType.length() > 0) {
                whereStr += " AND A.CTZ1_CODE='" + payType + "' ";
            }
            Timestamp sysDate = SystemTool.getInstance().getDate();
            int days = this.getValueInt("ZYCOUNT");
            if (days != 0) {
                String queryDate = StringTool.getString(StringTool.rollDate(
                        sysDate, -days), "yyyyMMddHHmmss");
                whereStr += "AND IN_DATE <=TO_DATE('" + queryDate +
                        "','YYYYMMDDHH24MISS') ";
            }
            break;
        }
        return whereStr;
    }

    /**
     * 账务状态
     */
    public void onSel() {
        if ("0".equals(this.getValueString("BILL_STATUS1"))) {
            ((TTextFormat) this.getComponent("START_DATE")).setEnabled(false);
            ((TTextFormat) this.getComponent("END_DATE")).setEnabled(false);
        } else {
            ((TTextFormat) this.getComponent("START_DATE")).setEnabled(true);
            ((TTextFormat) this.getComponent("END_DATE")).setEnabled(true);
        }
        if (("1".equals(this.getValueString("BILL_STATUS1"))
                || "2".equals(this.getValueString("BILL_STATUS1")) || "3".equals(this
                .getValueString("BILL_STATUS1"))) && this.getPopedem("SYSDBA")) {
            callFunction("UI|save|setEnabled", true);
        } else {
            callFunction("UI|save|setEnabled", false);
        }
        // TTable table = this.getTTable(TABLE1);
        // int rowCount = table.getDataStore().rowCount();
        // Map m = new HashMap();
        // for (int i = 0; i < rowCount; i++) {
        // m.put(i, new Color(255, 255, 0));
        // }
        // table.setRowColorMap(m);
        // this.getTTable(TABLE1).removeRowAll();
    }

    /**
     * 清空
     */
    public void onClear() {
        int selTTabbendPane = getTTabbedPane("TTABBEDPANE").getSelectedIndex();
        switch (selTTabbendPane) {
            case 0:
                this.clearValue("BILL_STATUS1;DEPT_CODE1;STATION_CODE1;ALLPERSON");
                // 当前时间
                Timestamp today = SystemTool.getInstance().getDate();
                // 获取选定日期的前一天的日期
                Timestamp yesterday = StringTool.rollDate(today, -1);
                this.setValue("START_DATE", yesterday);
                this.setValue("END_DATE", today);
                // TTable table = this.getTTable(TABLE1);
                // int rowCount = table.getDataStore().rowCount();
                // Map m = new HashMap();
                // for (int i = 0; i < rowCount; i++) {
                // m.put(i, new Color(255, 255, 0));
                // }
                // table.setRowColorMap(m);
                this.getTTable(TABLE1).removeRowAll();
                break;
            case 1:
                this.clearValue("DEPT_CODE2;STATION_CODE2");
                this.getTTable(TABLE2).removeRowAll();
                break;
            case 2:
                this.clearValue("DEPT_CODE3;STATION_CODE3");
                this.getTTable(TABLE3).removeRowAll();
                break;
            case 3:
                this.clearValue("ZYCOUNT;PAYTYPE;ONLYCOUNT");
                this.getTTable(TABLE4).removeRowAll();
                break;
        }
    }

    /**
     * 停止划价
     */
    public void onStop() {
        int row = this.getTTable(TABLE2).getSelectedRow();
        if (row < 0) {
            this.messageBox("请选择停用病患！");
            return;
        }
        TParm parm = this.getTTable(TABLE2).getParmValue().getRow(row);
        String flg = parm.getBoolean("STOP_BILL_FLG") ? "N" : "Y";
        TParm saveParm=null;
        if (parm.getValue("SUM_FLG").equals("N")) {//不存在新生儿数据==pangben 2014-4-14
        	saveParm =
                new TParm(this.getDBTool().update("UPDATE ADM_INP SET STOP_BILL_FLG='" + flg
                                                          + "' WHERE CASE_NO='"
                                                          + parm.getValue("CASE_NO") + "'"));
        	if (saveParm.getErrCode() != 0) {
                this.messageBox("停用失败！");
                return;
            } else {
                this.messageBox("停用成功！");
            }
		}else{
			//=======新增新生儿停止划价逻辑pangben 2014-4-14
			TParm result=new TParm(this.getDBTool().select("SELECT CASE_NO FROM ADM_INP WHERE IPD_NO='"
					+parm.getValue("IPD_NO")+"' AND DS_DATE IS NULL AND CANCEL_FLG='N' "));
			for (int i = 0; i < result.getCount(); i++) {
				saveParm =
	                new TParm(this.getDBTool().update("UPDATE ADM_INP SET STOP_BILL_FLG='" + flg
	                                                          + "' WHERE CASE_NO='"
	                                                          + result.getValue("CASE_NO",i) + "'"));
				 if (saveParm.getErrCode() != 0) {
					 this.messageBox("停用失败！");
			            return;
				 }
			}
			 this.messageBox("停用成功！");
		}
        this.onQuery();
        ((TCheckBox) this.getComponent("NEW_BABY_FLG")).setSelected(false);
    }

    /**
     * 导出EXECL
     */
    public void onExecl() {
        int selTTabbendPane = getTTabbedPane("TTABBEDPANE").getSelectedIndex();
        String execlName = "";
        switch (selTTabbendPane) {
            case 0:
                execlName = "在院状态";
                break;
            case 1:
                execlName = "病区浏览";
                break;
            case 2:
                execlName = "危重病人列表";
                break;
            case 3:
                execlName = "在院患者信息查询";
                break;
        }
        ExportExcelUtil.getInstance().exportExcel(this.getTTable("TABLE" + (selTTabbendPane + 1)),
                                                  execlName);
    }

    /**
     * 出院无账单召回动作
     */
    public void onSave() {
        int row = getTTable("TABLE1").getSelectedRow();
        if (row < 0) {
            this.messageBox("请选择一条信息");
            return;
        }
        TParm tableParm = getTTable("TABLE1").getParmValue();
        TParm parm = new TParm();
        parm.setData("CASE_NO", tableParm.getData("CASE_NO", row));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm actionParm = new TParm();
        //====pangben 2016-1-21 套餐病患校验
        if(!ADMInpTool.getInstance().onCheckLumWorkReturn(tableParm.getValue("CASE_NO", row))){
        	this.messageBox("套餐患者作废账单,先将母亲召回");
        	return;
        }
        actionParm.setData("DATA", parm.getData());
        TParm result =
                TIOM_AppServer.executeAction("action.adm.ADMWaitTransAction", "admReturn",
                                             actionParm);
        if (result.getErrCode() < 0) {
            this.messageBox("召回失败！");
            return;
        } else {
            this.messageBox("召回成功！");
            this.getTTable(TABLE1).removeRowAll();
        }
    }
    
    // ====================排序功能begin======================
    /**
     * 加入表格排序监听方法
     * @param table
     */
    public void addListener(final TTable table) {
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
    /**
     * 母婴金额合并操作
     * ============pangben 2014-4-14
     */
	public void checkMatherAndBabyPay() {
		if (((TCheckBox) this.getComponent("NEW_BABY_FLG")).isSelected()) {
			StringBuffer region = new StringBuffer();
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region.append(" AND A.REGION_CODE='" + Operator.getRegion()
						+ "' ");
			} else
				region.append("");
			String sql = "  SELECT STOP_BILL_FLG,PAT_NAME,CASE_NO,MR_NO,IPD_NO,CUR_AMT,DEPT_CODE,STATION_CODE,SEX_CODE,"
					+ "     BIRTH_DATE,BED_NO_DESC,IN_DATE,CTZ1_CODE,sum(nvl(PRE_AMT, 0)) TOTAL_BILPAY,NEW_BORN_FLG "
					+ "FROM ("
					+ "      SELECT A.STOP_BILL_FLG,B.PAT_NAME,A.CASE_NO,A.MR_NO,A.IPD_NO,A.CUR_AMT,A.DEPT_CODE,"
					+ "             A.STATION_CODE,B.SEX_CODE,B.BIRTH_DATE,C.BED_NO_DESC,A.IN_DATE,A.CTZ1_CODE,"
					+ "             D.RECEIPT_NO,D.REFUND_FLG,D.TRANSACT_TYPE,D.RESET_RECP_NO,D.PRE_AMT ,A.CANCEL_FLG,A.NEW_BORN_FLG"
					+ "        FROM ADM_INP A, SYS_PATINFO B, SYS_BED C, BIL_PAY D "
					+ "       WHERE A.MR_NO = B.MR_NO "
					+ "         AND A.BED_NO = C.BED_NO "
					+ "         AND A.DS_DATE IS NULL "
					+ region.toString()
					+ getWhereStr(1)
					+ "         AND A.CASE_NO = D.CASE_NO(+) "
					+ "           AND A.CANCEL_FLG = 'N' " // 取消住院的病患过滤 chenxi
					+ "     ) "
					+ "WHERE (RECEIPT_NO IS NOT NULL "
					+ "       AND REFUND_FLG = 'N' "
					+ "       AND TRANSACT_TYPE IN ('01', '04') "
					+ "       AND RESET_RECP_NO IS NULL ) "
					+ "   OR RECEIPT_NO IS NULL "
					+ "GROUP BY STOP_BILL_FLG,PAT_NAME,CASE_NO,MR_NO,IPD_NO,CUR_AMT,DEPT_CODE,STATION_CODE,"
					+ "         SEX_CODE,BIRTH_DATE,BED_NO_DESC,IN_DATE,CTZ1_CODE,NEW_BORN_FLG ORDER BY IPD_NO,NEW_BORN_FLG ";
			//System.out.println("sdfasdf::::sql:::"+sql);
			TParm queryParm = new TParm(this.getDBTool().select(sql));
			if (queryParm.getCount() <= 0) {
				this.messageBox("没有需要操作的数据");
				return;
			}
			String ipd_no=queryParm.getValue("IPD_NO", 0);
			//String newBornFlg="";
			TParm tableParm=new TParm();
			double sumCurAmt=queryParm.getDouble("CUR_AMT",0);//医嘱金额
			double sumTotalBilPay=queryParm.getDouble("TOTAL_BILPAY",0);//预交金金额
			String stop_bill_flg=queryParm.getValue("STOP_BILL_FLG", 0);
			String pat_name=queryParm.getValue("PAT_NAME", 0);
			String case_no=queryParm.getValue("CASE_NO", 0);
			String mr_no=queryParm.getValue("MR_NO", 0);
			String dept_code=queryParm.getValue("DEPT_CODE", 0);
			String station_code=queryParm.getValue("STATION_CODE", 0);
			String sex_code=queryParm.getValue("SEX_CODE", 0);
			Timestamp birth_date=null;
			Timestamp in_date=null;
			if (null!=queryParm.getTimestamp("BIRTH_DATE", 0)) {
				birth_date=queryParm.getTimestamp("BIRTH_DATE", 0);
			}
			if (null!=queryParm.getTimestamp("IN_DATE", 0)) {
				in_date=queryParm.getTimestamp("IN_DATE", 0);
			}
			String bed_no_desc=queryParm.getValue("BED_NO_DESC", 0);
			String ctz1_code=queryParm.getValue("CTZ1_CODE",0);
			boolean matherFlg=false;//记录是否存在母亲数据，如果存在显示母亲的信息，孩子的信息不显示
			boolean newBabyFlg=false;//记录是否存在婴儿的信息
			TParm tempParm=new TParm();
			for (int i = 0; i < queryParm.getCount(); i++) {
				if (ipd_no.equals(queryParm.getValue("IPD_NO", i))) {
					if (i!=0) {
						sumCurAmt+=queryParm.getDouble("CUR_AMT",i);
						sumTotalBilPay+=queryParm.getDouble("TOTAL_BILPAY",i);
					}
					if (queryParm.getValue("NEW_BORN_FLG", i).equals("Y")) {
						newBabyFlg=true;
						tempParm.addRowData(queryParm, i);
					}else{
						stop_bill_flg=queryParm.getValue("STOP_BILL_FLG", i);
						pat_name=queryParm.getValue("PAT_NAME", i);
						case_no=queryParm.getValue("CASE_NO", i);
						mr_no=queryParm.getValue("MR_NO", i);
						ipd_no=queryParm.getValue("IPD_NO", i);
						dept_code=queryParm.getValue("DEPT_CODE", i);
						station_code=queryParm.getValue("STATION_CODE", i);
						sex_code=queryParm.getValue("SEX_CODE", i);
						birth_date=queryParm.getTimestamp("BIRTH_DATE", i);
						bed_no_desc=queryParm.getValue("BED_NO_DESC", i);
						in_date=queryParm.getTimestamp("IN_DATE", i);
						ctz1_code=queryParm.getValue("CTZ1_CODE", i);
						matherFlg=true;
					}
				}else{
					if (matherFlg&&newBabyFlg||matherFlg&&!newBabyFlg) {//存在新生儿数据，母亲没有结算打票
						tableNewBabyParmOne(tableParm, stop_bill_flg, pat_name, case_no,
								ipd_no, mr_no, dept_code, station_code, sex_code, birth_date,
								bed_no_desc, in_date, ctz1_code, sumCurAmt, sumTotalBilPay, "Y");
					}else if (newBabyFlg&&!matherFlg) {//存在新生儿数据，母亲已经结算打票，显示数据为婴儿的数据
						for (int j = 0; j < tempParm.getCount("CASE_NO"); j++) {
							tableNewBabyParm(tableParm, tempParm, j);
						}
					}else{
						tableNewBabyParmOne(tableParm, stop_bill_flg, pat_name, case_no,
								ipd_no, mr_no, dept_code, station_code, sex_code, birth_date,
								bed_no_desc, in_date, ctz1_code, sumCurAmt, sumTotalBilPay, "N");
					}
					if (i!=queryParm.getCount()-1) {
						matherFlg=false;
						newBabyFlg=false;
					}
					sumCurAmt=queryParm.getDouble("CUR_AMT",i);
					sumTotalBilPay=queryParm.getDouble("TOTAL_BILPAY",i);
					stop_bill_flg=queryParm.getValue("STOP_BILL_FLG", i);
					pat_name=queryParm.getValue("PAT_NAME", i);
					case_no=queryParm.getValue("CASE_NO", i);
					mr_no=queryParm.getValue("MR_NO", i);
					ipd_no=queryParm.getValue("IPD_NO", i);
					dept_code=queryParm.getValue("DEPT_CODE", i);
					station_code=queryParm.getValue("STATION_CODE", i);
					sex_code=queryParm.getValue("SEX_CODE", i);
					birth_date=queryParm.getTimestamp("BIRTH_DATE", i);
					bed_no_desc=queryParm.getValue("BED_NO_DESC", i);
					in_date=queryParm.getTimestamp("IN_DATE", i);
					ctz1_code=queryParm.getValue("CTZ1_CODE", i);
				}
				if (i==queryParm.getCount()-1) {//最后一条数据
					if (!matherFlg&&newBabyFlg) {//存在新生儿数据，母亲没有结算打票
						tableNewBabyParmOne(tableParm, stop_bill_flg, pat_name, case_no,
								ipd_no, mr_no, dept_code, station_code, sex_code, birth_date,
								bed_no_desc, in_date, ctz1_code, sumCurAmt, sumTotalBilPay, "Y");
					}else{
						tableNewBabyParmOne(tableParm, stop_bill_flg, pat_name, case_no,
								ipd_no, mr_no, dept_code, station_code, sex_code, birth_date,
								bed_no_desc, in_date, ctz1_code, sumCurAmt, sumTotalBilPay, "N");
					}
				}
			}
			tableParm.setCount(tableParm.getCount("CASE_NO"));
			this.getTTable("TABLE2").setParmValue(tableParm);
		} else {
			TParm queryParm = new TParm(this.getDBTool().select(getQuerySql(1)));
			this.getTTable("TABLE2").setParmValue(queryParm);
		}
	}
	/**
	 * 新生儿显示使用
	 * @param tableParm
	 * @param queryParm
	 * @param i
	 * ====pangben 2014-4-14
	 */
	private void tableNewBabyParm(TParm tableParm,TParm queryParm,int i){
		tableParm.addData("STOP_BILL_FLG", queryParm.getValue("STOP_BILL_FLG", i));
		tableParm.addData("PAT_NAME", queryParm.getValue("PAT_NAME", i));
		tableParm.addData("CASE_NO", queryParm.getValue("CASE_NO", i));
		tableParm.addData("IPD_NO", queryParm.getValue("IPD_NO", i));
		tableParm.addData("MR_NO", queryParm.getValue("MR_NO", i));
		tableParm.addData("DEPT_CODE", queryParm.getValue("DEPT_CODE", i));
		tableParm.addData("STATION_CODE", queryParm.getValue("STATION_CODE", i));
		tableParm.addData("SEX_CODE", queryParm.getValue("SEX_CODE", i));
		tableParm.addData("BIRTH_DATE", queryParm.getTimestamp("BIRTH_DATE", i));
		tableParm.addData("BED_NO_DESC", queryParm.getValue("BED_NO_DESC", i));
		tableParm.addData("IN_DATE", queryParm.getTimestamp("IN_DATE", i));
		tableParm.addData("CTZ1_CODE", queryParm.getValue("CTZ1_CODE", i));
		tableParm.addData("CUR_AMT", queryParm.getDouble("CUR_AMT", i));
		tableParm.addData("TOTAL_BILPAY", queryParm.getDouble("CUR_AMT", i));
		tableParm.addData("SUM_FLG", "N");
	}
	/**
	 * 新生儿显示使用
	 * @param tableParm
	 * @param queryParm
	 * @param i
	 * ====pangben 2014-4-14
	 */
	private void tableNewBabyParmOne(TParm tableParm,
			String stop_bill_flg,String pat_name,String case_no,String ipd_no,
			String mr_no,String dept_code,String station_code,String sex_code,
			Timestamp birth_date,String bed_no_desc,Timestamp in_date,String ctz1_code,
			double sumCurAmt,double sumTotalBilPay,String sumFlg){
		tableParm.addData("STOP_BILL_FLG", stop_bill_flg);
		tableParm.addData("PAT_NAME", pat_name);
		tableParm.addData("CASE_NO", case_no);
		tableParm.addData("IPD_NO", ipd_no);
		tableParm.addData("MR_NO", mr_no);
		tableParm.addData("DEPT_CODE", dept_code);
		tableParm.addData("STATION_CODE", station_code);
		tableParm.addData("SEX_CODE", sex_code);
		tableParm.addData("BIRTH_DATE", birth_date);
		tableParm.addData("BED_NO_DESC", bed_no_desc);
		tableParm.addData("IN_DATE", in_date);
		tableParm.addData("CTZ1_CODE", ctz1_code);
		tableParm.addData("CUR_AMT", sumCurAmt);
		tableParm.addData("TOTAL_BILPAY", sumTotalBilPay);
		tableParm.addData("SUM_FLG", sumFlg);
	}
}
