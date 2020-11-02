package com.javahis.ui.opb;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: 门诊对账查询</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangp 2012.3.29
 * @version 1.0
 */
public class OPBUnAccountDaily extends TControl{
	TTable table ;
	/**
     * 初始化
     */
    public void onInit() {
        Timestamp today = SystemTool.getInstance().getDate();
        String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
        String endDate=startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 23:59:59";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", endDate);//结束时间设为23:59:59
    	setValue("REGION_CODE",Operator.getRegion());
    	setValue("CASH_CODE",Operator.getID());
    	setValue("ADM_TYPE","O");
    	table = (TTable) getComponent("TABLE");
    }
    /**
     * 查询
     */
    public void onQuery(){
//    	table.removeRowAll();
    	String startDate = getValueString("START_DATE")+getValueString("S_TIME");
    	String endDate = getValueString("END_DATE")+getValueString("E_TIME");
    	if(startDate.equals("")||endDate.equals("")){
    		messageBox("请选择时间段");
    		return;
    	}else{
    		startDate = startDate.substring(0, 4)+startDate.substring(5, 7)+startDate.substring(8, 10)
    		+startDate.substring(11, 13)+startDate.substring(14, 16)+startDate.substring(17, 19);
    		endDate = endDate.substring(0, 4)+endDate.substring(5, 7)+endDate.substring(8, 10)
    		+endDate.substring(11, 13)+endDate.substring(14, 16)+endDate.substring(17, 19);
    	}
    	String sql = getSql(startDate, endDate);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		table.removeRowAll();
    		return;
    	}
    	if(result.getCount()<0){
    		messageBox("查无数据");
    		table.setParmValue(new TParm());
    		return;
    	}
    	result = getTableParm(result);
//    	System.out.println("result result is ::"+result);
    	table.setParmValue(result);
//    	USER_NAME;PRINT_NO;RESET_TYPE;ADM_TYPE;AR_AMT;
//    	PAY_CASH;PAY_BANK_CARD;PAY_CHECK;PAY_MEDICAL_CARD;PAY_INS_CARD;
//    	OTHER_FEE1;PRINT_DATE;ACCOUNT_SEQ;ACCOUNT_DATE;ACCOUNT_USER;
//    	PAT_NAME
    }
    /**
     * 获得sql
     * @param startDate
     * @param endDate
     * @return
     */
    public String getSql(String startDate,String endDate){
    	StringBuilder sql = new StringBuilder();
    	sql.append("SELECT B.USER_NAME,A.MR_NO, A.PRINT_NO," +
    			" CASE" +
    			" WHEN A.RESET_RECEIPT_NO IS NULL" +
    			" THEN '未作废'" +
    			" WHEN A.RESET_RECEIPT_NO IS NOT NULL" +
    			" THEN '作废'" +
    			" END AS RESET_TYPE," +
    			" CASE" +
    			" WHEN A.ADM_TYPE = 'O'" +
    			" THEN '门诊'" +
    			" WHEN A.ADM_TYPE = 'E'" +
    			" THEN '急诊'" +
    			" END AS ADM_TYPE," +
    			" A.AR_AMT, A.PAY_CASH, A.PAY_BANK_CARD , A.PAY_CHECK, A.PAY_MEDICAL_CARD," +
    			" A.PAY_OTHER3,A.PAY_OTHER4,A.PAY_TYPE04,A.PAY_TYPE05,A.PAY_TYPE07, A.PAY_TYPE01,A.PAY_TYPE02,A.PAY_TYPE06," +
    			" A.PAY_INS_CARD, A.PAY_OTHER1 AS OTHER_FEE1,  A.PAY_TYPE08,A.PAY_TYPE09,A.PAY_TYPE10,A.PAY_TYPE11,A.PRINT_DATE,A.ACCOUNT_SEQ,A.ACCOUNT_DATE," + //add by huangtt A.PAY_TYPE08 20150519
    			" A.ACCOUNT_USER,C.PAT_NAME,CASE A.MEM_PACK_FLG WHEN 'Y' THEN A.TOT_AMT ELSE 0 END MEM_PACK, " +
    			" D.CTZ_DESC PAT_CTZ,F.CTZ_DESC REG_CTZ,J.DEPT_CHN_DESC DEPT_DESC,H.USER_NAME DR_DESC," + //add by huangtt 20150519 添加客户身份，挂号身份，科室，医生
    			" (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX' AND ID = C.SEX_CODE) AS SEX" +
    			" FROM BIL_OPB_RECP A, SYS_OPERATOR B, SYS_PATINFO C" +
    			" ,SYS_CTZ D,REG_PATADM E,SYS_CTZ F,SYS_DEPT J,SYS_OPERATOR H" + //add by huangtt 20150519
    			" WHERE A.CASHIER_CODE = B.USER_ID AND A.MR_NO = C.MR_NO " +
    			" AND C.CTZ1_CODE = D.CTZ_CODE" + //add by huangtt 20150519 start
    			" AND A.CASE_NO = E.CASE_NO" +
    			" AND E.CTZ1_CODE = F.CTZ_CODE" +
    			" AND E.REALDEPT_CODE  = J.DEPT_CODE" +
    			" AND E.REALDR_CODE = H.USER_ID"); //add by huangtt 20150519 end
    	if(!getValueString("REGION_CODE").equals("")){
    		sql.append(" AND A.REGION_CODE = '"+getValueString("REGION_CODE")+"' ");
    	}
    	if(!getValueString("CASH_CODE").equals("")){
    		sql.append(" AND A.CASHIER_CODE = '"+getValueString("CASH_CODE")+"' ");
    	}
    	if(!getValueString("ADM_TYPE").equals("")){
    		sql.append(" AND A.ADM_TYPE = '"+getValueString("ADM_TYPE")+"' ");
    	}
    	if(!startDate.equals("")&&!endDate.equals("")){
    		sql.append(" AND A.BILL_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')");
    	}
    	if(this.getValue("MR_NO")!=null && !this.getValue("MR_NO").equals("")){
    		sql.append(" AND A.MR_NO = '"+this.getValue("MR_NO")+"'");
    	}
    	if(this.getValue("DEPT_CODE")!=null && !this.getValue("DEPT_CODE").equals("")){
    		sql.append(" AND E.REALDEPT_CODE = '"+this.getValue("DEPT_CODE")+"'");
    	}
    	sql.append(" ORDER BY A.BILL_DATE DESC");
    	System.out.println("输出sql is：："+sql.toString());
    	return sql.toString();
    }
    /**
     * 清空
     */
    public void onClear(){
//    	table.removeRowAll();
    	setValue("REGION_CODE",Operator.getRegion());
    	setValue("CASH_CODE",Operator.getID());
    	Timestamp today = SystemTool.getInstance().getDate();
    	String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", today);
    	setValue("ADM_TYPE","O");
    	TParm parm = new TParm();
    	table.setParmValue(parm);
    }
    
    /**
     * 汇出Excel
     */
    public void onExport() {
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "门诊对账表");
    }
    /**
     * 加总计
     * @param parm
     * @return
     */
    public TParm getTableParm(TParm parm){
    	double ar_amt_tot = 0;
    	double cash_tot = 0;
    	double bank_tot = 0;
    	double check_tot = 0;
    	double ekt_tot = 0;
    	double ins_tot = 0;
    	double other_tot = 0;
    	double lpk_tot = 0;
    	double xjzkq_tot = 0;
    	double yydf_tot = 0;
    	double bxzf_tot = 0;
    	double tcjz_tot = 0;
    	double wx_tot = 0;
    	double zfb_tot = 0;
    	for (int i = 0; i < parm.getCount(); i++) {
//    			AR_AMT;PAY_CASH;PAY_BANK_CARD;PAY_CHECK;PAY_MEDICAL_CARD;PAY_INS_CARD;OTHER_FEE1
    			ar_amt_tot += parm.getDouble("AR_AMT", i);
    			cash_tot += parm.getDouble("PAY_TYPE01", i);//现金
    			bank_tot += parm.getDouble("PAY_TYPE02", i);//刷卡
    			check_tot += parm.getDouble("PAY_TYPE06", i);//支票
    			ekt_tot += parm.getDouble("PAY_MEDICAL_CARD", i)+parm.getDouble("PAY_TYPE11", i);//医疗卡
    			ins_tot += parm.getDouble("PAY_INS_CARD", i);//医保卡
    			wx_tot += parm.getDouble("PAY_TYPE09", i);//微信
    			zfb_tot += parm.getDouble("PAY_TYPE10", i);//支付宝
    			yydf_tot += parm.getDouble("PAY_TYPE04", i);//医院垫付
    			other_tot += parm.getDouble("OTHER_FEE1", i);
    			lpk_tot += parm.getDouble("PAY_OTHER3", i)+parm.getDouble("PAY_TYPE05", i);//礼品卡
    			xjzkq_tot += parm.getDouble("PAY_OTHER4", i)+parm.getDouble("PAY_TYPE07", i);//现金折扣券
    			parm.setData("PAY_OTHER3", i, parm.getDouble("PAY_OTHER3", i)+parm.getDouble("PAY_TYPE05", i));
    			parm.setData("PAY_OTHER4", i, parm.getDouble("PAY_OTHER4", i)+parm.getDouble("PAY_TYPE07", i));
    			parm.setData("PAY_CASH", i, parm.getDouble("PAY_TYPE01", i));//现金
    			parm.setData("PAY_BANK_CARD", i, parm.getDouble("PAY_TYPE02", i));//刷卡
    			parm.setData("PAY_CHECK", i, parm.getDouble("PAY_TYPE06", i));//支票
    			parm.setData("PAY_TYPE09", i, parm.getDouble("PAY_TYPE09", i));//微信
    			parm.setData("PAY_TYPE10", i, parm.getDouble("PAY_TYPE10", i));//支付宝
    			parm.setData("PAY_MEDICAL_CARD", i, parm.getDouble("PAY_MEDICAL_CARD", i)+parm.getDouble("PAY_TYPE11", i));//医疗卡
    			bxzf_tot += parm.getDouble("PAY_TYPE08", i); //保险直付
    			tcjz_tot += parm.getDouble("MEM_PACK",i);//套餐结转
		}
//    	USER_NAME;PRINT_NO;MR_NO;PAT_NAME;RESET_TYPE;ADM_TYPE;AR_AMT;PAY_CASH;PAY_BANK_CARD;PAY_CHECK;PAY_MEDICAL_CARD;
//    	PAY_INS_CARD;OTHER_FEE1;PRINT_DATE;ACCOUNT_SEQ;ACCOUNT_DATE;ACCOUNT_USER
    	parm.addData("USER_NAME", "总计");
    	parm.addData("PRINT_NO", "");
    	parm.addData("MR_NO", "");
    	parm.addData("PAT_NAME", parm.getCount()+"人次");
    	parm.addData("RESET_TYPE", "");
    	parm.addData("ADM_TYPE", "");
    	parm.addData("AR_AMT", ar_amt_tot);
    	parm.addData("PAY_CASH", cash_tot);
    	parm.addData("PAY_BANK_CARD", bank_tot);
    	parm.addData("MEM_PACK", tcjz_tot);
    	parm.addData("PAY_CHECK", check_tot);
    	parm.addData("PAY_MEDICAL_CARD", ekt_tot);
    	parm.addData("PAY_INS_CARD", ins_tot);//医保卡
    	parm.addData("PAY_TYPE04", yydf_tot);//医院垫付
    	parm.addData("PAY_OTHER3", lpk_tot);//礼品卡
    	parm.addData("PAY_OTHER4", xjzkq_tot);//现金折扣券
    	parm.addData("OTHER_FEE1", other_tot);
    	parm.addData("PAY_TYPE08", bxzf_tot);
    	parm.addData("PAY_TYPE09", wx_tot);
    	parm.addData("PAY_TYPE10", zfb_tot);
    	parm.addData("PRINT_DATE", "");
    	parm.addData("ACCOUNT_SEQ", "");
    	parm.addData("ACCOUNT_DATE", "");
    	parm.addData("ACCOUNT_USER", "");
    	return parm;
    }
    
    /**
     * 病案号回车
     */
    public void onMrNo(){
    	Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("查无此病案号");
			return;
		}
		this.setValue("MR_NO", pat.getMrNo());
        this.onQuery();
    }
    
}
