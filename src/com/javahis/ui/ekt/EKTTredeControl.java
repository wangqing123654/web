package com.javahis.ui.ekt;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import jdo.ekt.EKTIO;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: 医疗卡交易记录</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.9.16
 * @version 1.0
 */
public class EKTTredeControl
    extends TControl {

    private TTable table;
    
    //zhangp 20120130 是否读卡
    private boolean readCardFlg = false;

    public EKTTredeControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        table = getTable("TABLE");
//        setValue("USER_ID", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", today);
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        String sql = getSQL();
//        System.out.println("sql---" + sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount("BUSINESS_NO") <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        table.setParmValue(parm);
    }

    /**
     * 取得查询的SQL语句
     * @return String
     */
    private String getSQL() {
        String where1 = "";
        String where2 = "";
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
            String start_date = this.getValueString("START_DATE").substring(0, 19);
            start_date = start_date.substring(0, 4) + start_date.substring(5, 7) +
                start_date.substring(8, 10) + start_date.substring(11, 13) +
                start_date.substring(14, 16) + start_date.substring(17, 19);
            String end_date = this.getValueString("END_DATE").substring(0, 19);
            end_date = end_date.substring(0, 4) + end_date.substring(5, 7) +
                end_date.substring(8, 10) + end_date.substring(11, 13) +
                end_date.substring(14, 16) + end_date.substring(17, 19);
            where1 += " AND A.OPT_DATE BETWEEN TO_DATE('" + start_date +
                "','YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS')";
            where2 += " AND A.OPT_DATE BETWEEN TO_DATE('" + start_date +
            "','YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
            "','YYYYMMDDHH24MISS')";
        }
        if (!"".equals(this.getValueString("USER_ID"))) {
            where1 += " AND A.OPT_USER = '" + getValueString("USER_ID") + "'";
            where2 += " AND A.OPT_USER = '" + getValueString("USER_ID") + "'";
        }
//        if (!"".equals(this.getValueString("TREDE_NO"))) {     
//            where += " AND A.TREDE_NO = '" + getValueString("TREDE_NO") + "'";
//        }
        if (!"".equals(this.getValueString("CARD_NO"))) {
            where1 += " AND A.CARD_NO = '" + getValueString("CARD_NO") + "'";
            where2 += " AND A.CARD_NO = '" + getValueString("CARD_NO") + "'";
        }
        if (!"".equals(this.getValueString("MR_NO"))) {
            where1 += " AND A.MR_NO = '" + getValueString("MR_NO") + "'";
            where2 += " AND A.MR_NO = '" + getValueString("MR_NO") + "'";
        }
        if (!"".equals(this.getValueString("CASE_NO"))) {
            where1 += " AND A.CASE_NO = '" + getValueString("CASE_NO") + "'";
            where2 += " AND A.CASE_NO = '" + getValueString("CASE_NO") + "'";
        }
        if (!"".equals(this.getValueString("BUSINESS_NO"))) {
//            where1 += " AND A.BUSINESS_NO = '" + getValueString("BUSINESS_NO") +
//                "'";
            where2 += " AND A.BUSINESS_NO = '" + getValueString("BUSINESS_NO") +
            "'";
        }
        if (!"".equals(this.getValueString("STATE"))) {
            where1 += " AND A.STATE = '" + getValueString("STATE") + "'";
            where2 += " AND A.CHARGE_FLG = '" + getValueString("STATE") + "'";
        }
        if (!"".equals(this.getValueString("BUSINESS_TYPE"))) {
            where1 += " AND A.BUSINESS_TYPE = '" +
                getValueString("BUSINESS_TYPE") + "'";
            where2 += " AND A.BUSINESS_TYPE = '" +
            	getValueString("BUSINESS_TYPE") + "'";
        }
        if (!"".equals(this.getValueString("PAT_NAME"))) {
        	where1 += " AND B.PAT_NAME = '" +
        	getValueString("PAT_NAME") + "'";
        	where2 += " AND B.PAT_NAME = '" +
        	getValueString("PAT_NAME") + "'";
        }
        return 
        " SELECT   A.MR_NO, A.CARD_NO, A.CASE_NO, A.TRADE_NO BUSINESS_NO, B.PAT_NAME," +
        " A.OLD_AMT, A.AMT, " +
        " A.STATE, A.BUSINESS_TYPE, A.OPT_USER, A.OPT_DATE," +
        " A.OPT_TERM" +
        " FROM EKT_TRADE A, SYS_PATINFO B" +
        " WHERE A.MR_NO = B.MR_NO" +
        where1 +
        " UNION ALL" +
        " SELECT   A.MR_NO, A.CARD_NO, CASE WHEN A.CASE_NO = 'none' THEN '' ELSE A.CASE_NO END AS CASE_NO, A.BUSINESS_NO, B.PAT_NAME," +
        " A.ORIGINAL_BALANCE, A.BUSINESS_AMT, " +
        " CASE" +
        " WHEN A.CHARGE_FLG = '3'" +
        " THEN '0'" +
        " ELSE A.CHARGE_FLG" +
        " END STATE, '', A.OPT_USER," +
        " A.OPT_DATE, A.OPT_TERM" +
        " FROM EKT_ACCNTDETAIL A, SYS_PATINFO B" +
        " WHERE A.MR_NO = B.MR_NO AND A.CHARGE_FLG IN ('3', '4', '5', '7', '8')" +
        where2 +
        " ORDER BY OPT_DATE";
    }
    
    /**
     * 清空方法
     */
    public void onClear() {
        String clear =
            "START_DATE;END_DATE;USER_ID;TREDE_NO;CARD_NO;MR_NO;CASE_NO;BUSINESS_NO;STATE;BUSINESS_TYPE;PAT_NAME";
        this.clearValue(clear);
        table.removeRowAll();
        //zhangp 20120130
        readCardFlg = false;
        TTextField mrNoTextField = (TTextField) getComponent("MR_NO");
        mrNoTextField.setEnabled(true);
        TTextField cardNoTextField = (TTextField) getComponent("CARD_NO");
        cardNoTextField.setEnabled(true);
        Timestamp today = SystemTool.getInstance().getDate();
        String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", today);
    }

    /**
     * 汇出Excel
     */
    public void onExport() {
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "医疗卡交易记录表");
    }

    /**
     * 病案号回车事件
     */
    public void onMrNoAction() {
		String mrNo = ""+getValue("MR_NO");
		Pat pat = Pat.onQueryByMrNo(mrNo);
		mrNo = pat.getMrNo();
        this.setValue("MR_NO", mrNo);
        this.setValue("PAT_NAME", pat.getName());
    }

    /**
     * 读医疗卡
     */
    public void onCardNoAction() {
    	//zhangp 20111230
//        TParm parm = EKTIO.getInstance().getPat();
    	TParm parm = EKTIO.getInstance().TXreadEKT();
        //System.out.println("parm==="+parm);
    	if (null == parm || parm.getValue("MR_NO").length() <= 0) {
            this.messageBox("此卡无效");
            return;
        }
    	//zhangp 20120130
    	if(parm.getErrCode()<0){
    		messageBox(parm.getErrText());
    	}
    	String cardNo = parm.getValue("MR_NO")+parm.getValue("SEQ");
        this.setValue("CARD_NO", cardNo);
        //zhangp 20120130 加管控，不读卡不能打印
        readCardFlg = true;
        //zhangp 20120130 加病案号
        this.setValue("MR_NO", parm.getValue("MR_NO"));
        //zhangp 20120130 
        TTextField mrNoTextField = (TTextField) getComponent("MR_NO");
        mrNoTextField.setEnabled(false);
        TTextField cardNoTextField = (TTextField) getComponent("CARD_NO");
        cardNoTextField.setEnabled(false);
        //===zhangp 20120319 start
        Pat pat = Pat.onQueryByMrNo(parm.getValue("MR_NO"));
        setValue("PAT_NAME", pat.getName());
        //===zhangp 20120319 end
        //zhangp 20120131
        onQuery();
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
     * 打印
     * zhangp 20120129
     */
    public void onPrint(){
    	//zhangp 20120130 加读卡验证
    	if(!readCardFlg){
    		messageBox("请读取医疗卡");
    		return;
    	}
    	String cardno = getValueString("CARD_NO");
    	String mrno = getValueString("MR_NO");
    	TParm result = getParm(mrno,cardno);
//    	TParm result = getParm("000000400598","000000400598001");
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return;
    	}
    	//=============modify by lim 2012/02/24 begin
    	this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTTrede.jhw",result);
    	//=============modify by lim 2012/02/24 end
    }
    /**
     * 获取打印数据
     * zhangp 20120129
     * @param mrno
     * @param cardno
     * @return
     */
    public TParm getParm(String mrno,String cardno){
    	Pat pat = Pat.onQueryByMrNo(mrno);
    	//得到病患年龄
        String[] AGE =  StringTool.CountAgeByTimestamp(pat.getBirthday(),SystemTool.getInstance().getDate());
    	String sql =
    		"SELECT A.ISSUE_DATE,A.CARD_NO,B.USER_NAME" +
    		" FROM EKT_ISSUELOG A,SYS_OPERATOR B " +
    		" WHERE A.CARD_NO = '"+cardno+"' AND A.OPT_USER = B.USER_ID ";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return result;
    	}
		TParm data = new TParm();// 打印的数据
//		TParm parm = new TParm();// 表格数据
		data.setData("TITLE1", "TEXT", Operator.getHospitalCHNFullName());
		data.setData("MR_NO", "TEXT", "病案号: "+mrno);
		data.setData("SEX", "TEXT", "性别: "+pat.getSexString());
		data.setData("NAME", "TEXT", "姓名: "+pat.getName());
//		data.setData("AGE", "TEXT", "年龄: "+AGE[0]+"岁"+AGE[1]+"个月"+AGE[2]+"天");
		data.setData("AGE", "TEXT", "年龄: "+AGE[0]+"岁");
		data.setData("IDNO", "TEXT", "身份证号: "+pat.getIdNo());
		data.setData("COMPANY", "TEXT", "单位名称: "+pat.getCompanyDesc());
		data.setData("COMPANYCALL", "TEXT", "单位电话: "+pat.getTelCompany());
		data.setData("CELLPHONE", "TEXT", "电话: "+pat.getCellPhone());
		data.setData("ISSUE_DATE", "TEXT", "售卡日期: "+result.getValue("ISSUE_DATE", 0).replace("-", "/").substring(0, result.getValue("ISSUE_DATE", 0).length()-2));
		data.setData("USER_NAME", "TEXT", "售卡人员: "+result.getData("USER_NAME", 0));
		String date = SystemTool.getInstance().getDate().toString();
		data.setData("PRINT_DATE", "TEXT", "打印日期: "+date.substring(0, 4)+
    			"/"+date.substring(5, 7)+"/"+date.substring(8, 10));
		sql = 
			"SELECT A.BUSINESS_DATE,A.CHARGE_FLG,B.GATHER_TYPE,A.ACCNT_STATUS,A.ORIGINAL_BALANCE,A.BUSINESS_AMT,A.CURRENT_BALANCE "+
            " FROM EKT_ACCNTDETAIL A,EKT_BIL_PAY B WHERE A.MR_NO = '"+
            mrno+"' AND A.CHARGE_FLG IN (3,4,5,7) AND A.BUSINESS_NO = B.BIL_BUSINESS_NO "+
            " UNION "+
            " SELECT A.BUSINESS_DATE,A.CHARGE_FLG,'' AS GATHER_TYPE,A.ACCNT_STATUS,A.ORIGINAL_BALANCE,A.BUSINESS_AMT,A.CURRENT_BALANCE "+
            " FROM EKT_ACCNTDETAIL A WHERE A.MR_NO = '"+
            mrno+"' AND A.CHARGE_FLG IN (1,2)";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return result;
    	}
		double businessAmt = 0.0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//设置日期格式
		DecimalFormat   df1 = new DecimalFormat("#0.00"); //设置数据格式
		for (int i = 0; i < result.getCount(); i++) {
			if(result.getInt("CHARGE_FLG", i)==1){
				result.setData("CHARGE_FLG", i, "扣款");
			}
			if(result.getInt("CHARGE_FLG", i)==2){
				result.setData("CHARGE_FLG", i, "退款");
				businessAmt = -result.getDouble("BUSINESS_AMT", i);
				result.setData("BUSINESS_AMT", i, businessAmt);
			}
			if(result.getInt("CHARGE_FLG", i)==3){
				result.setData("CHARGE_FLG", i, "医疗卡充值");
			}
			if(result.getInt("CHARGE_FLG", i)==4){
				result.setData("CHARGE_FLG", i, "制卡");
			}
			if(result.getInt("CHARGE_FLG", i)==5){
				result.setData("CHARGE_FLG", i, "补卡");
			}
			if(result.getInt("CHARGE_FLG", i)==7){
				result.setData("CHARGE_FLG", i, "退费");
				businessAmt = -result.getDouble("BUSINESS_AMT", i);
				result.setData("BUSINESS_AMT", i, businessAmt);
			}
			if(result.getInt("ACCNT_STATUS",i)==1){
				result.setData("ACCNT_STATUS", i, "未对账");
			}
			if(result.getInt("ACCNT_STATUS",i)==2){
				result.setData("ACCNT_STATUS", i, "已对账");
			}
			if(result.getData("GATHER_TYPE", i).equals("C0")){
				result.setData("GATHER_TYPE", i, "现金");
			}
			if(result.getData("GATHER_TYPE", i).equals("C1")){
				result.setData("GATHER_TYPE", i, "刷卡");
			}
			if(result.getData("GATHER_TYPE", i).equals("C2")){
				result.setData("GATHER_TYPE", i, "汇票");
			}
			if(result.getData("GATHER_TYPE", i).equals("C4")){
				result.setData("GATHER_TYPE", i, "应收款");
			}
			if(result.getData("GATHER_TYPE", i).equals("T0")){
				result.setData("GATHER_TYPE", i, "支票");
			}
			if(result.getData("GATHER_TYPE", i).equals("Z")){
				result.setData("GATHER_TYPE", i, "工商圈存机");
			}
			
			result.setData("BUSINESS_DATE", i, df.format(result.getTimestamp("BUSINESS_DATE", i)));			
			result.setData("ORIGINAL_BALANCE",i, df1.format(result.getData("ORIGINAL_BALANCE", i)));
			result.setData("BUSINESS_AMT",i, df1.format(result.getData("BUSINESS_AMT", i)));
			result.setData("CURRENT_BALANCE",i, df1.format(result.getData("CURRENT_BALANCE", i)));
		}
		data.setData("TABLE", result.getData());
    	return data;
    }


}
