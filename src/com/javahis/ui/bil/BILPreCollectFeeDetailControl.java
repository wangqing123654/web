package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: 住院预收款余额明细查询</p>
 *
 * <p>Description: 住院预收款余额明细查询</p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 2017-11-7
 * @version 1.0
 */
public class BILPreCollectFeeDetailControl extends TControl{
	private TTable table;
	private Pat pat;
	/**
     * 初始化方法
     */
    public void onInit() {
    	table = (TTable) getComponent("TABLE");
        this.setValue("REGION_CODE", Operator.getRegion());
        initPage();
        //权限添加
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
    }
    /**
     * 初始画面数据
     */
    private void initPage() {
        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("S_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        
    }
    public void onMrNo(){
    	pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("无此病案号!");
			this.setValue("MR_NO", "");
			return;
		}
		String mrNo = PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
		setValue("MR_NO", mrNo);
		onQuery();
    }
    
    public void onQuery(){
    	String where="";
    	String where1="";
    	String where2="";
    	if (null != pat && pat.getMrNo().length()>0) {
    		where =" AND A.MR_NO ='"+pat.getMrNo()+"' ";
    		where1=" AND A.MR_NO ='"+pat.getMrNo()+"' ";
		}
    	 String date_s = getValueString("S_DATE");
         if (null == date_s || date_s.length() <= 0) {
             this.messageBox("请输入需要查询的时间范围");
             return;
         }
         String regionCode="";
       
         date_s = SystemTool.getInstance().getDateReplace(date_s,true).toString();
         if (null != this.getValueString("REGION_CODE") && this.getValueString("REGION_CODE").length() > 0)
        	 where+= " AND D.REGION_CODE='"+ this.getValueString("REGION_CODE")+"' ";
         	regionCode+= " AND A.REGION_CODE='"+ this.getValueString("REGION_CODE")+"' ";
 
         if(!"".equals(date_s)){
        	 where+=" AND D.IN_DATE < TO_DATE('"+date_s+"','YYYYMMDDHH24MISS') ";
        	 where2+=" AND A.DS_DATE < TO_DATE('"+date_s+"','YYYYMMDDHH24MISS') ";
         }
         //在院病患入院时间内，预交金明细
    	 String sql="SELECT MR_NO,PAT_NAME,DEPT_DESC,PACKAGE_AMT,OUT_LUMPWORK_CASH_AMT,"+
    			"OUT_LUMPWORK_CHECK_AMT,OUT_LUMPWORK_WX_AMT,OUT_LUMPWORK_ZFB_AMT,OUT_LUMPWORK_EKT_AMT FROM (SELECT MR_NO,PAT_NAME,DEPT_DESC,SUM(PACKAGE_AMT) PACKAGE_AMT,SUM(OUT_LUMPWORK_CASH_AMT) OUT_LUMPWORK_CASH_AMT,"+
    			"SUM(OUT_LUMPWORK_CHECK_AMT) OUT_LUMPWORK_CHECK_AMT,"
    			+ "SUM(OUT_LUMPWORK_WX_AMT) OUT_LUMPWORK_WX_AMT,SUM(OUT_LUMPWORK_ZFB_AMT) OUT_LUMPWORK_ZFB_AMT,SUM(OUT_LUMPWORK_EKT_AMT) OUT_LUMPWORK_EKT_AMT "
    			+ "FROM ( SELECT A.MR_NO,B.PAT_NAME,C.DEPT_CHN_DESC DEPT_DESC,"+
    			"CASE WHEN A.PAY_TYPE = 'TCJZ' THEN A.PRE_AMT ELSE 0.00 END PACKAGE_AMT,"+
    			"CASE WHEN A.PAY_TYPE = 'C0' THEN A.PRE_AMT ELSE 0.00 END OUT_LUMPWORK_CASH_AMT,"+
    			"CASE WHEN A.PAY_TYPE = 'C1' THEN A.PRE_AMT ELSE 0.00 END OUT_LUMPWORK_CHECK_AMT, "+
    			"CASE WHEN A.PAY_TYPE = 'WX' THEN A.PRE_AMT ELSE 0.00 END OUT_LUMPWORK_WX_AMT, "+
    			"CASE WHEN A.PAY_TYPE = 'ZFB' THEN A.PRE_AMT ELSE 0.00 END OUT_LUMPWORK_ZFB_AMT, "+
    			"CASE WHEN A.PAY_TYPE = 'EKT' THEN A.PRE_AMT ELSE 0.00 END OUT_LUMPWORK_EKT_AMT "+
              "FROM BIL_PAY A,SYS_PATINFO B,SYS_DEPT C,ADM_INP D "+
               " WHERE A.MR_NO=B.MR_NO AND A.CASE_NO=D.CASE_NO AND D.NEW_BORN_FLG='N' AND "+
               " D.IN_DEPT_CODE=C.DEPT_CODE AND D.DS_DATE IS NULL"+where+" ) A "+
          "  GROUP BY MR_NO,PAT_NAME,DEPT_DESC ) WHERE PACKAGE_AMT+OUT_LUMPWORK_CASH_AMT+OUT_LUMPWORK_CHECK_AMT+OUT_LUMPWORK_WX_AMT+OUT_LUMPWORK_ZFB_AMT+OUT_LUMPWORK_EKT_AMT<>0.00";
    	 
    	 //出院未结算病患出院时间内预交金明细
    	 String sql1 ="  UNION ALL SELECT A.MR_NO,B.PAT_NAME,C.DEPT_CHN_DESC AS DEPT_DESC , PACKAGE_AMT, OUT_LUMPWORK_CASH_AMT,OUT_LUMPWORK_CHECK_AMT,OUT_LUMPWORK_WX_AMT,OUT_LUMPWORK_ZFB_AMT,OUT_LUMPWORK_EKT_AMT "
    	 		+ "FROM  (SELECT  MR_NO,IN_DEPT_CODE,CASE_NO ,SUM(PACKAGE_AMT) PACKAGE_AMT, SUM(OUT_LUMPWORK_CASH_AMT) OUT_LUMPWORK_CASH_AMT, "+
    			 "SUM(OUT_LUMPWORK_CHECK_AMT) OUT_LUMPWORK_CHECK_AMT,"
    			 + "SUM(OUT_LUMPWORK_WX_AMT) OUT_LUMPWORK_WX_AMT,SUM(OUT_LUMPWORK_ZFB_AMT) OUT_LUMPWORK_ZFB_AMT,SUM(OUT_LUMPWORK_EKT_AMT) OUT_LUMPWORK_EKT_AMT "
    			 + " FROM ( SELECT A.MR_NO,A.IN_DEPT_CODE,A.CASE_NO,PACKAGE_AMT,"+
    					" OUT_LUMPWORK_CASH_AMT,OUT_LUMPWORK_CHECK_AMT,OUT_LUMPWORK_WX_AMT,OUT_LUMPWORK_ZFB_AMT,OUT_LUMPWORK_EKT_AMT "+
    					" FROM (SELECT A.MR_NO,A.IN_DEPT_CODE,A.CASE_NO,B.CASE_NO AS CASE_NO_BIL , "+
                "CASE WHEN C.PAY_TYPE = 'TCJZ' THEN C.PRE_AMT ELSE 0.00 END PACKAGE_AMT, "+
                "CASE WHEN C.PAY_TYPE = 'C0' THEN C.PRE_AMT ELSE 0.00 END OUT_LUMPWORK_CASH_AMT, "+
                "CASE WHEN C.PAY_TYPE = 'C1' THEN C.PRE_AMT ELSE 0.00 END OUT_LUMPWORK_CHECK_AMT, "+
    			"CASE WHEN C.PAY_TYPE = 'WX' THEN C.PRE_AMT ELSE 0.00 END OUT_LUMPWORK_WX_AMT, "+
    			"CASE WHEN C.PAY_TYPE = 'ZFB' THEN C.PRE_AMT ELSE 0.00 END OUT_LUMPWORK_ZFB_AMT, "+
    			"CASE WHEN C.PAY_TYPE = 'EKT' THEN C.PRE_AMT ELSE 0.00 END OUT_LUMPWORK_EKT_AMT "
    			+ " FROM  ADM_INP A,(SELECT CASE_NO FROM BIL_IBS_RECPM A WHERE AR_AMT>=0 AND RESET_RECEIPT_NO IS NULL AND CHARGE_DATE<=TO_DATE('"+date_s+"','YYYYMMDDHH24MISS') GROUP BY CASE_NO)B,BIL_PAY C WHERE A.CASE_NO=B.CASE_NO(+) AND A.CASE_NO=C.CASE_NO  "+
                " AND A.NEW_BORN_FLG='N' AND A.DS_DATE IS NOT NULL "+regionCode+where2+") A WHERE CASE_NO_BIL IS NULL ) A "+
                " GROUP BY MR_NO,IN_DEPT_CODE,CASE_NO ) A,SYS_PATINFO B, SYS_DEPT C WHERE A.MR_NO=B.MR_NO "+
                " AND A.IN_DEPT_CODE=C.DEPT_CODE AND PACKAGE_AMT +OUT_LUMPWORK_CASH_AMT+OUT_LUMPWORK_CHECK_AMT+OUT_LUMPWORK_WX_AMT+OUT_LUMPWORK_ZFB_AMT+OUT_LUMPWORK_EKT_AMT<>0 "+where1;
    	 //查询根据结算时间查询出当前结算点未打票的数据
    	 String sql2 ="  UNION ALL SELECT  A.MR_NO,B.PAT_NAME,C.DEPT_CHN_DESC AS DEPT_DESC, SUM(PACKAGE_AMT) PACKAGE_AMT,SUM(OUT_LUMPWORK_CASH_AMT) OUT_LUMPWORK_CASH_AMT "+
    			 ",SUM(OUT_LUMPWORK_CHECK_AMT) OUT_LUMPWORK_CHECK_AMT ,"
    			 + "SUM(OUT_LUMPWORK_WX_AMT) OUT_LUMPWORK_WX_AMT,SUM(OUT_LUMPWORK_ZFB_AMT) OUT_LUMPWORK_ZFB_AMT,SUM(OUT_LUMPWORK_EKT_AMT) OUT_LUMPWORK_EKT_AMT "
    			 + "FROM (SELECT A.CASE_NO,A.MR_NO,A.IN_DEPT_CODE, "+
    			 "CASE WHEN B.PAY_TYPE = 'TCJZ' THEN B.PRE_AMT ELSE 0.00 END PACKAGE_AMT,"+
    			 "CASE WHEN B.PAY_TYPE = 'C0' THEN B.PRE_AMT ELSE 0.00 END OUT_LUMPWORK_CASH_AMT,"+
    			 "CASE WHEN B.PAY_TYPE = 'C1' THEN B.PRE_AMT ELSE 0.00 END OUT_LUMPWORK_CHECK_AMT, "+
     			 "CASE WHEN B.PAY_TYPE = 'WX' THEN B.PRE_AMT ELSE 0.00 END OUT_LUMPWORK_WX_AMT, "+
     			 "CASE WHEN B.PAY_TYPE = 'ZFB' THEN B.PRE_AMT ELSE 0.00 END OUT_LUMPWORK_ZFB_AMT, "+
     			 "CASE WHEN B.PAY_TYPE = 'EKT' THEN B.PRE_AMT ELSE 0.00 END OUT_LUMPWORK_EKT_AMT "+
     				" FROM (SELECT A.MR_NO,A.IN_DEPT_CODE,A.CASE_NO "+
     				" FROM  ADM_INP A,BIL_IBS_RECPM B WHERE A.CASE_NO=B.CASE_NO AND B.AR_AMT>=0 AND B.RESET_RECEIPT_NO IS NULL "+
     				"  AND A.NEW_BORN_FLG='N' AND A.DS_DATE IS NOT NULL AND B.CHARGE_DATE>=TO_DATE('"+date_s+"','YYYYMMDDHH24MISS') "+regionCode+" ) A ,BIL_PAY B "+
               "  WHERE  B.CASE_NO=A.CASE_NO AND B.CHARGE_DATE<TO_DATE('"+date_s+"','YYYYMMDDHH24MISS') ) A,SYS_PATINFO B,SYS_DEPT C "+
                " WHERE A.MR_NO=B.MR_NO AND A.IN_DEPT_CODE=C.DEPT_CODE "+where1+
               "  GROUP BY A.MR_NO,B.PAT_NAME,C.DEPT_CHN_DESC" ;
    	//System.out.println("sql::444444:"+sql+sql1+sql2);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql+sql1+sql2));
        if(result.getErrCode()<0){
       	    this.messageBox("查询失败");
       	    return;
        }
        if(result.getCount()<=0){
            this.messageBox("查无数据");
            table.setParmValue(new TParm());
            return;
        }
        double packAmt=0.00;
        double cashAmt=0.00;
        double checkAmt=0.00;
        double wxAmt=0.00;
        double zfbAmt=0.00;
        double ektAmt=0.00;
        for (int i = 0; i < result.getCount(); i++) {
        	packAmt+=result.getDouble("PACKAGE_AMT",i);
        	cashAmt+=result.getDouble("OUT_LUMPWORK_CASH_AMT",i);
        	checkAmt+=result.getDouble("OUT_LUMPWORK_CHECK_AMT",i);
        	wxAmt+=result.getDouble("OUT_LUMPWORK_WX_AMT",i);
        	zfbAmt+=result.getDouble("OUT_LUMPWORK_ZFB_AMT",i);
        	ektAmt+=result.getDouble("OUT_LUMPWORK_EKT_AMT",i);
        	
		}
        result.addData("MR_NO", "");
        result.addData("PAT_NAME", "");
        result.addData("DEPT_DESC", "汇总:");
        result.addData("PACKAGE_AMT", packAmt);
        result.addData("OUT_LUMPWORK_CASH_AMT", cashAmt);
        result.addData("OUT_LUMPWORK_CHECK_AMT", checkAmt);
        result.addData("OUT_LUMPWORK_WX_AMT", wxAmt);
        result.addData("OUT_LUMPWORK_ZFB_AMT", zfbAmt);
        result.addData("OUT_LUMPWORK_EKT_AMT", ektAmt);
        result.setCount(result.getCount()+1);
        table.setParmValue(result);
    }
    public void onClear(){
    	 table.setParmValue(new TParm());
    	 pat = null;
    	 this.setValue("MR_NO", "");
    	 initPage();
    }
    /**
     * 汇出Excel
     */
    public void onExport() {
        //得到UI对应控件对象的方法
        TParm parm = table.getParmValue();
        if (null == parm || parm.getCount() <= 0) {
            this.messageBox("没有需要导出的数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "住院预收款余额明细");
    }

}
