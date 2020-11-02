package com.javahis.ui.bil;


import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;

import com.dongyang.util.StringTool;

import com.javahis.util.ExportExcelUtil;

public class BILFinanceShouldFeeControl extends TControl{
	
	String[] chargName = {"CHARGE01", "CHARGE02", "CHARGE03", "CHARGE04",
            "CHARGE05", "CHARGE06", "CHARGE07", "CHARGE08",
            "CHARGE09",
            "CHARGE10", "CHARGE11", "CHARGE12", "CHARGE13",
            "CHARGE14",
            //===zhangp 20120307 modify start
            "CHARGE15", "CHARGE16", "CHARGE17", "CHARGE18",
            "CHARGE19", "CHARGE20"};
	
	private TParm parmName; //费用名称
	private TParm parmCode; //费用代码
	
	private TTable table;
	private TParm result;
	
	private String startTime;
	private String endTime;
//	private TTextFormat startDate;
//	private TTextFormat endDate;
	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		initPage();
//		onQuery();
	}
	/**
     * 初始化界面
     */
	 public void initPage() {
//	        out("begin");
	        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
	                                                  getDate(), -1);
	        setValue("S_DATE", yesterday);
	        setValue("E_DATE", SystemTool.getInstance().getDate());
	        String sql = getBillRecpparm(); //获得费用代码
	        setValue("ADM_TYPE", 1);
	        
//	        sql += " WHERE ADM_TYPE='O'";
	        
	        this.callFunction("UI|Table|removeRowAll");
	        parmCode = new TParm(TJDODBTool.getInstance().select(sql));
	        if (parmCode.getErrCode() < 0 || parmCode.getCount() <= 0) {
	            this.messageBox("设置费用字典有问题");
	            return;
	        }
	        //获得费用名称
	        sql =
	                "SELECT ID ,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE'";
	        parmName = new TParm(TJDODBTool.getInstance().select(sql));
			// ==========modify-begin (by wanglong 20120710)===============
	        //TTable table = (TTable)this.getComponent("Table");
			 table = (TTable) this.getComponent("Table");
//			// 为表单添加监听器，为排序做准备。
//			addListener(table);
			// ==========modify-end========================================
	        table.setParmValue(getHeard());
	    }
	
	private TParm getHeard() {
		TParm heardParm = new TParm();
//	    heardParm.addData("STATION_DESC", "病区");
	    heardParm.addData("OWN_AMT", "自费金额");
		heardParm.addData("AR_AMT", "收费金额");
		heardParm.addData("MR_NO", "病案号");
		heardParm.addData("PAT_NAME", "姓名");
		heardParm.addData("CTZ_DESC", "身份");
		heardParm.addData("DEPT_CHN_DESC", "科室名称");
		heardParm.addData("USER_NAME", "医生");
		heardParm.addData("ADM_DATE", "就诊日期");
//		heardParm.addData("DEPT_CODE", "科室名称");
	    for (int i = 0; i < chargName.length; i++) {
	    	heardParm.addData(chargName[i],
	    			getChargeName(parmName,parmCode.getValue(chargName[i], 0)));
	    }
	    heardParm.setCount(1);
	    return heardParm;
	}
	/**
	 * 获得费用代码
	 * @return
	 */
    private static String getBillRecpparm() {
        return "SELECT ADM_TYPE, RECP_TYPE, PY1, PY2, SEQ,"
                + " DESCRIPTION, CHARGE01, CHARGE02, CHARGE03, CHARGE04,"
                + " CHARGE05, CHARGE06, CHARGE07, CHARGE08, CHARGE09,"
                + " CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14,"
                + " CHARGE15, CHARGE16, CHARGE17, CHARGE18, CHARGE19,"
                + " CHARGE20, OPT_USER, OPT_DATE, OPT_TERM "
                + " FROM BIL_RECPPARM WHERE ADM_TYPE='O'";
    }

    /**
     * 获得费用名称
     * @param parmName TParm
     * @param chargeCode String
     * @return String
     */
    private String getChargeName(TParm parmName, String chargeCode) {
        for (int i = 0; i < parmName.getCount(); i++) {
            if (parmName.getValue("ID", i).equals(chargeCode)) {
                return parmName.getValue("CHN_DESC", i);
            }
        }
        return "";
    }
	
    //应收sql
    private String getYSPrintDate(String startTime, String endTime) {
        String sql = "SELECT  a.mr_no, b.dept_code, b.adm_date, a.rexp_code, SUM (a.ar_amt) AS AR_AMT , "+
        			"SUM (a.own_amt) AS OWN_AMT, b.ctz1_code, b.dr_code, c.pat_name,b.CASE_NO "+
        	"FROM opd_order a, reg_patadm b, sys_patinfo c "+
        	"WHERE a.case_no = b.case_no "+
       				" AND b.mr_no = c.mr_no "+ 
       				" AND a.exec_flg = 'Y' AND A.BILL_FLG='N' AND B.REGCAN_USER IS NULL AND A.RECEIPT_NO IS NULL "+ 
       				" AND a.exec_date BETWEEN TO_DATE ('"+startTime+"','yyyy/MM/dd hh24:mi:ss') "+
       				" AND TO_DATE ('"+endTime+"', 'yyyy/MM/dd hh24:mi:ss') ";
        	
        return sql;
        
    }
    
    private String getWJSPrintDate(String startTime, String endTime){
    String sql = "SELECT   a.mr_no, b.dept_code, b.adm_date,a.rexp_code, SUM (a.ar_amt) AS AR_AMT, " +
    		"SUM (a.own_amt) AS OWN_AMT, b.ctz1_code, b.dr_code, c.pat_name,b.CASE_NO "+
       " FROM opd_order a, reg_patadm b, sys_patinfo c "+
       " WHERE a.case_no = b.case_no "+
         		" AND b.mr_no = c.mr_no  "+
         		" AND a.exec_flg = 'N' AND A.BILL_FLG='N' AND B.REGCAN_USER IS NULL AND A.RECEIPT_NO IS NULL "+
         		" AND A.ORDER_DATE BETWEEN TO_DATE ('"+startTime+"', 'yyyy/MM/dd hh24:mi:ss') "+
                " AND TO_DATE ('"+endTime+"', 'yyyy/MM/dd hh24:mi:ss') ";
    	return sql;
    }
    /**
     * 
     * 根据身份代码得到身份
     * 根据CTZ1_CODE得到CTZ_DESC
     * @return
     */
    private String getCTZ(String ctz1Code){
    	String sql = "SELECT CTZ_DESC FROM SYS_CTZ WHERE CTZ_CODE = '"+ctz1Code+"'";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	String ctzDesc = parm.getValue("CTZ_DESC",0);
    	return ctzDesc;
    }
    
    /**
     * 
     * 根据科室代码得到科室
     * 根据Dept_CODE得到DEPT_CHN_DESC
     * @return
     */
    private String getDept(String deptCode){
    	String sql = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE = '"+deptCode+"'";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	String deptChnDesc = parm.getValue("DEPT_CHN_DESC",0);
    	return deptChnDesc;
    }
    
    /**
     * 
     * 根据医生代码得到医生
     * 根据DR_CODE得到USER_NAME
     * @return
     */
    private String getUserName(String drCode){
    	String sql = "SELECT user_name FROM  sys_operator  WHERE user_id = '"+drCode+"'";
//    	System.out.println("drCode----"+drCode);
//    	System.out.println("sql::::"+sql);
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	String userName = parm.getValue("USER_NAME",0);
//    	System.out.println("userName==========="+userName);
    	return userName;
    }
    
	/**
	 * 条件查询
	 */
	public void onQuery(){
		
		startTime = StringTool.getString(TCM_Transform.getTimestamp(this.getValue("S_DATE")),"yyyy/MM/dd HH:mm:ss");
//		System.out.println("startTime:::"+startTime);
		endTime = StringTool.getString(TCM_Transform.getTimestamp(this.getValue("E_DATE")),"yyyy/MM/dd HH:mm:ss");
		//String endTime = this.getValueString("E_DATE");
		if(startTime == null ||"".equals(startTime)||endTime == null ||"".equals(endTime)){
			messageBox("请输入正确的日期");
			return;
		}
		String adm_type = this.getValueString("ADM_TYPE");
		if(adm_type == null || "".equals(adm_type)){
			messageBox("请选择门急别");
			return;
		}
		String admType = "";
		//String chargeSql = //getBillRecpparm();
//		switch (Integer.valueOf(adm_type)){
//		
//		//1说明用户选择是全部
////		case 1: 
//			
//			//admType = "WHERE ADM_TYPE='O' or ADM_TYPE='E";
//			//chargeSql += admType;
////			break;
//		
//		case 2:
//		
//			admType = " AND B.ADM_TYPE='O'";
//			//chargeSql += admType;
//			break;
//		//2说明用户选择是急诊
//		case 3:
//		
//			admType = " AND B.ADM_TYPE='E'";
//			//chargeSql += admType;
//			break;
//		
//		}
		//TParm chargeResult = new TParm(TJDODBTool.getInstance().select(chargeSql));
//		System.out.println(chargeResult);
		
		
		if("2".equals(adm_type)){
			admType = " AND B.ADM_TYPE='O'";
			
			
		}else if("3".equals(adm_type)){
			admType = " AND B.ADM_TYPE='E'";
		}
		
		
		
		
		//应收
		if(getRadioButton("YS").isSelected()){
			
			String sqlYS =  getYSPrintDate(startTime, endTime);
			result = new TParm(TJDODBTool.getInstance().select(sqlYS+admType+" GROUP BY a.rexp_code, "+
        			" a.mr_no, "+
        			" c.pat_name, "+
        			" b.adm_date, "+
        			" b.dept_code, "+
        			" b.dr_code, "+
        			" b.ctz1_code,B.CASE_NO "+
        	" ORDER BY B.CASE_NO"));
//			System.out.println("sqlYS======"+sqlYS+admType+"GROUP BY a.rexp_code, a.mr_no,c.pat_name,  b.adm_date, b.dept_code, b.dr_code, b.ctz1_code,B.CASE_NO ORDER BY B.CASE_NO");
//			System.out.println("result:::"+result);
			if (result.getErrCode()<0) {
				this.messageBox("查询失败");
				onInit();
				return;
			}
			if (result.getCount()<=0) {
				this.messageBox("没有查询的数据");
				table.removeRowAll();
				onInit();
				return;
			}
			result = getTable();
			
			}
		//未结算
		if(getRadioButton("WJS").isSelected()){
			
			String sqlWJS = getWJSPrintDate(startTime, endTime);
			
			result = new TParm(TJDODBTool.getInstance().select(sqlWJS + admType +
				       "GROUP BY a.rexp_code, a.mr_no, c.pat_name, b.adm_date, b.dept_code, b.dr_code, b.ctz1_code,b.case_no  "+
				       "ORDER BY b.case_no"));
//			System.out.println("sqlYS======"+sqlWJS+admType+"GROUP BY a.rexp_code, a.mr_no, c.pat_name, b.adm_date, b.dept_code, b.dr_code, b.ctz1_code,b.case_no "+
//				       "ORDER BY b.case_no");
			if (result.getErrCode()<0) {
				this.messageBox("查询失败");
				onInit();
				return;
			}
			if (result.getCount()<=0) {
				this.messageBox("没有查询的数据");
				table.removeRowAll();
				onInit();
				return;
			}
			result = getTable();
		
		}
		
		table.setParmValue(result);
	}
	
	private TParm getTable(){
		TParm sumParm = getHeard();
		String caseNo="";
		String mrNo="";
		String patName="";
		String ctzCode="";
		String deptCode="";
		String drCode="";
		Timestamp admDate=null;
		if (result.getCount()>0) {
			caseNo=result.getValue("CASE_NO",0);
			mrNo=result.getValue("MR_NO",0);
			patName=result.getValue("PAT_NAME",0);
			ctzCode=result.getValue("CTZ1_CODE",0);
			deptCode=result.getValue("DEPT_CODE",0);
			drCode = result.getValue("DR_CODE",0);
//			System.out.println("drCode:::::"+drCode);
			admDate=result.getTimestamp("ADM_DATE",0);
		}
		double ownAmt = 0.00;
		double arAmt = 0.00;		
		TParm temp = new TParm();
		DecimalFormat df = new DecimalFormat("##########0.00");
		/**
		 * 循环result结果集 ，如果当前caseNo与查询结果中的某一条数据的caseNo相同，说明是同一次就诊 
		 * 将自费金额与全部费用分别作累加，
		 */
		for(int i = 0;i < result.getCount();i++){
			
			if (caseNo.equals(result.getValue("CASE_NO",i))) {
				ownAmt += result.getDouble("OWN_AMT",i);
//				System.out.println("i="+i);
//				System.out.println("ownAmt=========="+ownAmt);
				arAmt += result.getDouble("AR_AMT",i);
		/**
		 * 循环chargeName数组，如果结果集中的某一条rexpCode 与费用代码parmCode中相同
		 * 将该条数据的费用，放在临时parm  temp 中		
		 */
				for (int j = 0; j < chargName.length; j++) {
					if (result.getValue("REXP_CODE",i).equals(parmCode.getValue(chargName[j], 0))) {
						//flg=true;
						temp.setData(chargName[j],df.format(result.getDouble("AR_AMT",i)));
//						System.out.println("i="+i+";j="+j);
//						System.out.println("temp=========="+temp);
						//sumParm.addData(chargName[j],result.getDouble("AR_AMT",i));
						break;
					}
				}
			}else{
		/**
		 * 如果当前caseNo与查询结果中的数据caseNo不同，说明不是同义词就诊，
		 * 1、将临时Parm  temp 中的数据放入 头Parm中，置空 temp parm，并将当前数据中的
		 * rexpCode 与费用代码parmCode相同 的 费用放入 temp parm中，防止遗漏数据
		 * 2、将其他数据放入 头表sumParm中
		 */
				for (int j = 0; j < chargName.length; j++) {
					if (temp.getDouble(chargName[j])>0) {
						sumParm.addData(chargName[j], df.format(temp.getDouble(chargName[j])));
					}else{
						sumParm.addData(chargName[j],df.format(0));
					}
				}
				temp = new TParm();
				for (int j = 0; j < chargName.length; j++) {
					if (result.getValue("REXP_CODE",i).equals(parmCode.getValue(chargName[j], 0))) {
						//flg=true;
						temp.setData(chargName[j],df.format(result.getDouble("AR_AMT",i)));
//						System.out.println("i="+i+";j="+j);
//						System.out.println("temp=========="+temp);
						//sumParm.addData(chargName[j],result.getDouble("AR_AMT",i));
						break;
					}
				}
				
				
				sumParm.addData("MR_NO", mrNo);
				sumParm.addData("PAT_NAME", patName);
//				System.out.println("PAT_NAME"+patName);
				sumParm.addData("CTZ_DESC", getCTZ(ctzCode));
				sumParm.addData("DEPT_CHN_DESC", getDept(deptCode));
				sumParm.addData("USER_NAME", getUserName(drCode));
//				System.out.println("drCode:"+drCode);
//				System.out.println("USER_NAME===="+getUserName(drCode));
				sumParm.addData("ADM_DATE", admDate);
				sumParm.addData("OWN_AMT", df.format(ownAmt));
				sumParm.addData("AR_AMT", df.format(arAmt));
				
				ownAmt=result.getDouble("OWN_AMT",i);
				arAmt=result.getDouble("AR_AMT",i);
				caseNo=result.getValue("CASE_NO",i);
				mrNo=result.getValue("MR_NO",i);
				patName=result.getValue("PAT_NAME",i);
				ctzCode=result.getValue("CTZ1_CODE",i);
				deptCode=result.getValue("DEPT_CODE",i);
				drCode = result.getValue("DR_CODE",i);
				admDate=result.getTimestamp("ADM_DATE",i);
			}
			if (i == result.getCount() - 1) {
				sumParm.addData("MR_NO", mrNo);
				sumParm.addData("PAT_NAME", patName);
				sumParm.addData("CTZ_DESC", getCTZ(ctzCode));
				sumParm.addData("DEPT_CHN_DESC", getDept(deptCode));
				sumParm.addData("USER_NAME", getUserName(drCode));
				sumParm.addData("ADM_DATE", admDate);
				sumParm.addData("OWN_AMT", df.format(ownAmt));
				sumParm.addData("AR_AMT", df.format(arAmt));
				for (int j = 0; j < chargName.length; j++) {
					if (temp.getDouble(chargName[j])>0) {
						sumParm.addData(chargName[j], df.format(temp.getDouble(chargName[j])));
					}else{
						sumParm.addData(chargName[j],df.format(0));
					}
				}
			}
					
		}
//		System.out.println("sumParm::::"+sumParm);
		sumParm.setCount(sumParm.getCount("MR_NO"));
//		result = sumParm;
//		table.setParmValue(result);
		return sumParm;
	}
	
	/**
	 * 打印
	 */
	public void onPrint(){
		
		TParm tableParm = table.getParmValue();
		
		if (tableParm == null && tableParm.getCount() <= 0) {
			
			messageBox("无数据");			
			onInit();
			return;
		}
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//		startTime = sdf.format(startTime);
//		endTime = sdf.format(endTime);
		
		TParm data = new TParm();
		String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
         "yyyy/MM/dd HH:mm:ss");
		TParm table = new TParm();
		String admDate = "";
		String temp = "";
		for (int i = 1; i < tableParm.getCount(); i++) {
			table.addData("MR_NO", tableParm.getValue("MR_NO", i));	
			table.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
			table.addData("CTZ_DESC", tableParm.getValue("CTZ_DESC", i));
			table.addData("DEPT_CHN_DESC", tableParm.getValue("DEPT_CHN_DESC", i));
			table.addData("USER_NAME", tableParm.getValue("USER_NAME", i));
			temp = tableParm.getValue("ADM_DATE",i);
//			System.out.println("temp-------------"+temp);
			admDate = temp.substring(0, temp.length()-2);
			table.addData("ADM_DATE", admDate);
			
//			
			
			table.addData("OWN_AMT", tableParm.getValue("OWN_AMT", i));
			table.addData("AR_AMT", tableParm.getValue("AR_AMT", i));
			table.addData("CHARGE01", tableParm.getValue("CHARGE01", i));
			table.addData("CHARGE02", tableParm.getValue("CHARGE02", i));
			table.addData("CHARGE03", tableParm.getValue("CHARGE03", i));
			table.addData("CHARGE04", tableParm.getValue("CHARGE04", i));
			table.addData("CHARGE05", tableParm.getValue("CHARGE05", i));
			table.addData("CHARGE06", tableParm.getValue("CHARGE06", i));
			table.addData("CHARGE07", tableParm.getValue("CHARGE07", i));
			table.addData("CHARGE08", tableParm.getValue("CHARGE08", i));
			table.addData("CHARGE09", tableParm.getValue("CHARGE09", i));
			table.addData("CHARGE10", tableParm.getValue("CHARGE10", i));
			table.addData("CHARGE11", tableParm.getValue("CHARGE11", i));
			table.addData("CHARGE12", tableParm.getValue("CHARGE12", i));
			table.addData("CHARGE13", tableParm.getValue("CHARGE13", i));
			table.addData("CHARGE14", tableParm.getValue("CHARGE14", i));
			table.addData("CHARGE15", tableParm.getValue("CHARGE15", i));
			table.addData("CHARGE16", tableParm.getValue("CHARGE16", i));
			table.addData("CHARGE17", tableParm.getValue("CHARGE17", i));
			table.addData("CHARGE18", tableParm.getValue("CHARGE18", i));
			table.addData("CHARGE19", tableParm.getValue("CHARGE19", i));
			table.addData("CHARGE20", tableParm.getValue("CHARGE20", i));
			
		}
		table.setCount(tableParm.getCount());
		
		table.addData("SYSTEM", "COLUMNS", "MR_NO");
		table.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		table.addData("SYSTEM", "COLUMNS", "CTZ_DESC");
		table.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		table.addData("SYSTEM", "COLUMNS", "USER_NAME");
		table.addData("SYSTEM", "COLUMNS", "ADM_DATE");
		table.addData("SYSTEM", "COLUMNS", "OWN_AMT");
		table.addData("SYSTEM", "COLUMNS", "AR_AMT");
		table.addData("SYSTEM", "COLUMNS", "CHARGE01");
		table.addData("SYSTEM", "COLUMNS", "CHARGE02");
		table.addData("SYSTEM", "COLUMNS", "CHARGE03");
		table.addData("SYSTEM", "COLUMNS", "CHARGE04");
		table.addData("SYSTEM", "COLUMNS", "CHARGE05");
		table.addData("SYSTEM", "COLUMNS", "CHARGE06");
		table.addData("SYSTEM", "COLUMNS", "CHARGE07");
		table.addData("SYSTEM", "COLUMNS", "CHARGE08");
		table.addData("SYSTEM", "COLUMNS", "CHARGE09");
		table.addData("SYSTEM", "COLUMNS", "CHARGE10");
		table.addData("SYSTEM", "COLUMNS", "CHARGE11");
		table.addData("SYSTEM", "COLUMNS", "CHARGE12");
		table.addData("SYSTEM", "COLUMNS", "CHARGE13");
		table.addData("SYSTEM", "COLUMNS", "CHARGE14");
		table.addData("SYSTEM", "COLUMNS", "CHARGE15");
		table.addData("SYSTEM", "COLUMNS", "CHARGE16");
		table.addData("SYSTEM", "COLUMNS", "CHARGE17");
		table.addData("SYSTEM", "COLUMNS", "CHARGE18");
		table.addData("SYSTEM", "COLUMNS", "CHARGE19");
		table.addData("SYSTEM", "COLUMNS", "CHARGE20");
		
		data.setData("TABLE", table.getData());
		
		if(getRadioButton("YS").isSelected()){
			data.setData("Title", "应收费用明细表");	
		}
        if(getRadioButton("WJS").isSelected()){
        	data.setData("Title","未结算费用明细表");
        }
		data.setData("S_DATE", startTime);
        data.setData("E_DATE", endTime);
        data.setData("OPT_USER", Operator.getName());
        data.setData("OPT_DATE", sysDate);
		
		openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILFinanceShouldFee.jhw",
				data);
		
	}
	/**
	 * 导出报表
	 */
	public void onExcel() {
	
		if (table.getRowCount() > 0) {
	
			ExportExcelUtil.getInstance().exportExcel(table, "信息表格");
	
		} else {

			messageBox("没有信息");
		}
	}
	/**
	 * 清空
	 */
	public void onClear(){
		this.clearValue("ADM_TYPE;S_DATE;E_DATE;YS;WJS");
		table.removeRowAll();
		onInit();
	}

	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}
}
