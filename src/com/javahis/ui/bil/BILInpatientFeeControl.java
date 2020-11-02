package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: 住院减免费用明细表</p>
 *
 * <p>Description: 住院减免费用明细表</p>
 *
 * <p>Copyright: Copyright (c)bluecore 2016</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author kangy 2016/6/12
 * @version 1.0
 */
public class BILInpatientFeeControl extends TControl{
	private TTable table;
	private TTextField mrNo;
	private TRadioButton yzjm,qtjm;
	 private String head="",mapParm="",send="",start="",end="";
     public void onInit() {
        super.onInit();
        table=(TTable)this.getComponent("TABLE");
        yzjm=(TRadioButton) this.getComponent("YZJM");
       qtjm=(TRadioButton) this.getComponent("QTJM");
       mrNo=(TTextField) this.getComponent("MR_NO");
        initPage();
    }
    /**
     * 初始化界面
     */
    public void initPage() {
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(),-1);
        String startTime = StringTool.getString(yesterday, "yyyy/MM/dd");
        setValue("START_DATE", startTime+" 00:00:00");
        setValue("END_DATE",  StringTool.getString(SystemTool.getInstance().
                getDate(), "yyyy/MM/dd")+" 23:59:59");
   	 table.setHeader("");
   	table.setParmMap("");
   	table.setColumnHorizontalAlignmentData("");
        table.setParmValue(new TParm());
		
        
    }
    /**
     * 查询方法
     */ 
    public void onQuery(){
    	SimpleDateFormat sf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	 start = StringTool.getString(TypeTool
  				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmmss");
  		 end = StringTool.getString(TypeTool
  				.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss");
     	TParm tableParm=new TParm();
     	String sql="";
     	String where="WHERE 1=1 ";
     	if(this.getValue("MR_NO").toString().length()>0 || !"".equals(this.getValue("MR_NO"))){
     		where+="AND A.MR_NO='"+this.getValue("MR_NO")+"'";
     	}
        if(yzjm.isSelected()){
        	double sum_ar_amt=0.00;
        	double sum_reduce_amt=0.00;
        	head="序号,30;就诊号,100;病案号,80;姓名,60;性别,40;就诊身份,120;减免日期,150;" +
    		"就诊科室,100;医嘱代码,80;医嘱名称,200;减免金额,80,double,########0.00;减免原因,120;" +
    		"项目类别,80;申请人,80,REDUCE_USER;经办人,80,OPT_USER;备注,80";
        	mapParm="TID;CASE_NO;MR_NO;PAT_NAME;SEX_CODE;CTZ_DESC;OPT_DATE;DEPT_CHN_DESC;DATA_CODE;DATA_NAME;" +
            		"REDUCE_AMT;REDUCE_NOTE;REDUCE_TYPE;REDUCE_USER;OPT_USER;LOCATION";
        	send="0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,right;11,left;12,left;13,left;" +
        			"14,left;15,left";
       	 table.setHeader(head);
      	table.setParmMap(mapParm);
      	table.setColumnHorizontalAlignmentData(send);
        	sql="SELECT DISTINCT ROW_NUMBER() OVER(ORDER BY A.CASE_NO,B.DATA_CODE) AS TID, A.CASE_NO,A.MR_NO,C.PAT_NAME," +
        			" CASE WHEN C.SEX_CODE = '1' THEN '男' WHEN C.SEX_CODE = '2'" +
        			" THEN '女' ELSE '未知' END SEX_CODE,E.CTZ_DESC,A.OPT_DATE,F.DEPT_CHN_DESC,B.AR_AMT, B.REDUCE_AMT," +
        			" B.DATA_CODE, B.DATA_NAME,A.REDUCE_NOTE,'医嘱减免' AS REDUCE_TYPE,A.REDUCE_USER,A.OPT_USER,NULL AS LOCATION " +
        			"FROM BIL_REDUCEM A, BIL_REDUCED B, SYS_PATINFO C, ADM_INP D, SYS_CTZ E, SYS_DEPT F " +
        			 where+
        			" AND A.MR_NO=D.MR_NO" +
        			" AND B.REDUCE_AMT<>0" +
        			" AND A.MR_NO=C.MR_NO AND E.CTZ_CODE=D.CTZ1_CODE" +
        			" AND A.MR_NO=D.MR_NO AND D.DEPT_CODE=F.DEPT_CODE" +
        			" AND A.REDUCE_AMT>0 AND A.ADM_TYPE='I'" +
        			" AND B.REDUCE_AMT<>0" +
        			" AND  A.REDUCE_NO=B.REDUCE_NO AND B.REDUCE_TYPE='2' AND B.DATA_TYPE='2'" +
        			" AND A.CASE_NO=D.CASE_NO AND A.RESET_REDUCE_NO IS  NULL" +
        			" AND A.OPT_DATE BETWEEN TO_DATE ('"+start+"','YYYYMMDDHH24MISS') " +
        			" AND TO_DATE ('"+end+"','YYYYMMDDHH24MISS') ORDER BY A.CASE_NO,B.DATA_CODE,A.OPT_DATE";
	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
	 if (parm == null || parm.getCount("CASE_NO") <= 0) {
         this.messageBox("没有查询到数据");
         onInit();
         return;
     }
	 tableParm.addData("SYSTEM", "COLUMNS", "TID");
	 tableParm.addData("SYSTEM", "COLUMNS", "CASE_NO");
	 tableParm.addData("SYSTEM", "COLUMNS", "MR_NO");
	 tableParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
	 tableParm.addData("SYSTEM", "COLUMNS", "SEX_CODE");
	 tableParm.addData("SYSTEM", "COLUMNS", "CTZ_DESC");
	 tableParm.addData("SYSTEM", "COLUMNS", "OPT_DATE");
	 tableParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
	 //tableParm.addData("SYSTEM", "COLUMNS", "AR_AMT");
	 tableParm.addData("SYSTEM", "COLUMNS", "REDUCE_AMT");
	 tableParm.addData("SYSTEM", "COLUMNS", "DATA_CODE");
	 tableParm.addData("SYSTEM", "COLUMNS", "DATA_NAME");
	 tableParm.addData("SYSTEM", "COLUMNS", "REDUCE_NOTE");
	 tableParm.addData("SYSTEM", "COLUMNS", "REDUCE_TYPE");
	 tableParm.addData("SYSTEM", "COLUMNS", "REDUCE_USER");
	 tableParm.addData("SYSTEM", "COLUMNS", "OPT_USER");
	 tableParm.addData("SYSTEM", "COLUMNS", "LOCATION");
	 for(int i=0;i<parm.getCount("CASE_NO");i++){
		 tableParm.addData("TID",parm.getValue("TID", i));
	 tableParm.addData("CASE_NO",parm.getValue("CASE_NO", i));
	 tableParm.addData("MR_NO",parm.getValue("MR_NO", i));
	 tableParm.addData("PAT_NAME",parm.getValue("PAT_NAME", i));
	 tableParm.addData("SEX_CODE",parm.getValue("SEX_CODE", i));
	 tableParm.addData("CTZ_DESC",parm.getValue("CTZ_DESC", i));

		try {
			tableParm.addData("OPT_DATE",
			       sf.format(sd.parse(parm.getValue("OPT_DATE", i))));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 tableParm.addData("DEPT_CHN_DESC",parm.getValue("DEPT_CHN_DESC", i));
	// tableParm.addData("AR_AMT",parm.getDouble("AR_AMT", i));
	 //sum_ar_amt+=parm.getDouble("AR_AMT", i);
	 tableParm.addData("REDUCE_AMT",parm.getDouble("REDUCE_AMT", i));
	 sum_reduce_amt+=parm.getDouble("REDUCE_AMT", i);
	 tableParm.addData("DATA_CODE",parm.getValue("DATA_CODE", i));
	 tableParm.addData("DATA_NAME",parm.getValue("DATA_NAME", i));
	 tableParm.addData("REDUCE_NOTE",parm.getValue("REDUCE_NOTE", i));
	 tableParm.addData("REDUCE_TYPE",parm.getValue("REDUCE_TYPE", i));
	 tableParm.addData("REDUCE_USER",parm.getValue("REDUCE_USER", i));
	 tableParm.addData("OPT_USER",parm.getValue("OPT_USER", i));
	 tableParm.addData("LOCTION",parm.getValue("LOCATION", i));
	 }
	 tableParm.setCount(tableParm.getCount("CASE_NO"));
	 
	 tableParm.addData("TID","");
	 tableParm.addData("CASE_NO","");
	 tableParm.addData("MR_NO","");
	 tableParm.addData("PAT_NAME","");
	 tableParm.addData("SEX_CODE","");
	 tableParm.addData("CTZ_DESC","");
	 tableParm.addData("OPT_DATE","");
	 tableParm.addData("DEPT_CHN_DESC","");
	 tableParm.addData("DATA_CODE","");
	 tableParm.addData("DATA_NAME","合计:");
	// tableParm.addData("AR_AMT",sum_ar_amt);
	 tableParm.addData("REDUCE_AMT",sum_reduce_amt);
	 tableParm.addData("REDUCE_NOTE","");
	 tableParm.addData("REDUCE_TYPE","");
	 tableParm.addData("REDUCE_USER","");
	 tableParm.addData("OPT_USER","");
	 tableParm.addData("LOCTION","");
	table.setParmValue(tableParm);
        }
    	if(qtjm.isSelected()){
    		double sum_ar_amt=0.00;
    		double sum_reduce_amt=0.00;
    		double[] sum=new double[21];
    		 head="序号,30;就诊号,100;病案号,80;姓名,60;性别,40;就诊身份,120;减免日期,150;" +
     		"就诊科室,100;减免金额,80,double,########0.00;床位费,80,double,########0.00;诊疗费,80,double,########0.00;抗菌费,80,double,########0.00;" +
     		"非抗菌费,80,double,########0.00;中成药费,80,double,########0.00;" +
     		"中草药费,80,double,########0.00;常规检查,80,double,########0.00;治疗费,80,double,########0.00;放射费,80,double,########0.00;" +
     		"手术费,80,double,########0.00;检验费,80,double,########0.00;输血费,80,double,########0.00;" +
     		"输氧费,80,double,########0.00;膳食费,80,double,########0.00;护理费,80,double,########0.00;B超费,80,double,########0.00;" +
     		"CT费,80,double,########0.00;核磁费,80,double,########0.00;"+
     		"麻醉费,80,double,########0.00;材料费,80,double,########0.00;其他费,80,double,########0.00;减免原因,180;项目类别,80;申请人,80,REDUCE_USER;经办人,80,OPT_USER;备注,80";
            
              mapParm="TID;CASE_NO;MR_NO;PAT_NAME;SEX_CODE;CTZ_DESC;OPT_DATE;DEPT_CHN_DESC;" +
             		"REDUCE_AMT;CHARGE01;CHARGE02;CHARGE03;CHARGE04;CHARGE05;CHARGE06;CHARGE07;CHARGE08;" +
             		"CHARGE09;CHARGE10;CHARGE11;CHARGE12;CHARGE13;CHARGE14;CHARGE15;CHARGE16;" +
             		"CHARGE17;CHARGE18;CHARGE19;CHARGE20;CHARGE21;REDUCE_NOTE;"+
             		"REDUCE_TYPE;REDUCE_USER;OPT_USER;LOCATION";
              send="0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,right;9,right;10,right;11,right;12,right;13,right;" +
     		"14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,right;24,right;25,right;26,right;" +
     		"27,right;28,right;29,right;30,left;31,left;32,left;33,left;34,left";
         	 table.setHeader(head);
          	table.setParmMap(mapParm);
          	table.setColumnHorizontalAlignmentData(send);
              sql="SELECT ROW_NUMBER() OVER(ORDER BY OPT_DATE) AS TID, CASE_NO,MR_NO,PAT_NAME,SEX_CODE," +
              		"CTZ_DESC,OPT_DATE,DEPT_CHN_DESC,AR_AMT,REDUCE_AMT,REDUCE_NO,CHARGE01,CHARGE02,CHARGE03,CHARGE04," +
              		"CHARGE05,CHARGE06,CHARGE07,CHARGE08,CHARGE09,CHARGE10,CHARGE11,CHARGE12,CHARGE13," +
              		" CHARGE14,CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20,CHARGE21," +
              		"REDUCE_NOTE,REDUCE_TYPE,REDUCE_USER,OPT_USER,LOCATION FROM(" +
              		"SELECT DISTINCT A.CASE_NO,A.MR_NO,C.PAT_NAME,CASE WHEN C.SEX_CODE = '1' THEN '男' WHEN C.SEX_CODE = '2'" +
              		" THEN '女' ELSE '未知' END SEX_CODE,E.CTZ_DESC,A.OPT_DATE,F.DEPT_CHN_DESC,A.AR_AMT, A.REDUCE_AMT," +
              		" G.*,A.REDUCE_NOTE,'其他减免' AS REDUCE_TYPE,A.REDUCE_USER,A.OPT_USER,NULL AS LOCATION " +
              		"FROM BIL_REDUCEM A,BIL_REDUCED B,SYS_PATINFO C,ADM_INP D,SYS_CTZ E,SYS_DEPT F," +
              		"(SELECT * FROM(SELECT A.REDUCE_NO, B.REXP_CODE,B.REDUCE_AMT " +
              		"FROM BIL_REDUCEM A,BIL_REDUCED B " +
              		"WHERE A.REDUCE_NO=B.REDUCE_NO AND (B.REDUCE_TYPE<>'2' OR B.DATA_TYPE<>'2')" +
              		" GROUP BY A.REDUCE_NO,B.REXP_CODE,B.REDUCE_AMT)" +
              		" PIVOT(MAX(REDUCE_AMT) FOR REXP_CODE " +
              		"IN ('201' CHARGE01,'202' CHARGE02,'215.1' CHARGE03,'215.2' CHARGE04,'216' CHARGE05,'217' CHARGE06,'207' CHARGE07," +
              		"'209' CHARGE08,'203' CHARGE09,'212' CHARGE10,'208' CHARGE11,'211' CHARGE12," +
              		"'210' CHARGE13,'219' CHARGE14,'213' CHARGE15,'205' CHARGE16,'204' CHARGE17,'206' CHARGE18," +
              		"'218' CHARGE19,'214' CHARGE20,'220' CHARGE21)))G  " +
              		where+
              		" AND A.MR_NO=D.MR_NO AND A.MR_NO=C.MR_NO" +
              		" AND E.CTZ_CODE=D.CTZ1_CODE AND A.MR_NO=D.MR_NO" +
              		" AND D.DEPT_CODE=F.DEPT_CODE AND A.REDUCE_NO=G.REDUCE_NO" +
              		" AND A.REDUCE_AMT>0 AND A.ADM_TYPE='I'" +
              		" AND A.CASE_NO=D.CASE_NO" +
              		" AND A.RESET_REDUCE_NO IS  NULL AND A.OPT_DATE BETWEEN TO_DATE ('"+start+"','YYYYMMDDHH24MISS')" +
              		" AND TO_DATE ('"+end+"','YYYYMMDDHH24MISS') ORDER BY A.OPT_DATE)";
    		System.out.println(sql);
              TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    		 if (parm == null || parm.getCount("CASE_NO") <= 0) {
    	         this.messageBox("没有查询到数据");
    	         onInit();
    	         return;
    	     }
    		 tableParm.addData("SYSTEM", "COLUMNS", "TID");
    		 tableParm.addData("SYSTEM", "COLUMNS", "CASE_NO");
    		 tableParm.addData("SYSTEM", "COLUMNS", "MR_NO");
    		 tableParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
    		 tableParm.addData("SYSTEM", "COLUMNS", "SEX_CODE");
    		 tableParm.addData("SYSTEM", "COLUMNS", "CTZ_DESC");
    		 tableParm.addData("SYSTEM", "COLUMNS", "OPT_DATE");
    		 tableParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
    		 //tableParm.addData("SYSTEM", "COLUMNS", "AR_AMT");
    		 tableParm.addData("SYSTEM", "COLUMNS", "REDUCE_AMT");
    		 for(int i=1;i<21;i++){
    			 if(i<=9)
    			 tableParm.addData("SYSTEM", "COLUMNS", "CHARGE0"+i);
    			 else
    			 tableParm.addData("SYSTEM", "COLUMNS", "CHARGE"+i);
    			 }
    		 tableParm.addData("SYSTEM", "COLUMNS", "REDUCE_NOTE");
    		 tableParm.addData("SYSTEM", "COLUMNS", "REDUCE_TYPE");
    		 tableParm.addData("SYSTEM", "COLUMNS", "REDUCE_USER");
    		 tableParm.addData("SYSTEM", "COLUMNS", "OPT_USER");
    		 tableParm.addData("SYSTEM", "COLUMNS", "LOCATION");
    		 for(int i=0;i<parm.getCount("CASE_NO");i++){
    			 tableParm.addData("TID",parm.getValue("TID", i));
        		 tableParm.addData("CASE_NO",parm.getValue("CASE_NO", i));
        		 tableParm.addData("MR_NO",parm.getValue("MR_NO", i));
        		 tableParm.addData("PAT_NAME",parm.getValue("PAT_NAME", i));
        		 tableParm.addData("SEX_CODE",parm.getValue("SEX_CODE", i));
        		 tableParm.addData("CTZ_DESC",parm.getValue("CTZ_DESC", i));
        			try {
						tableParm.addData("OPT_DATE",
						       sf.format(sd.parse(parm.getValue("OPT_DATE", i))));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		 tableParm.addData("DEPT_CHN_DESC",parm.getValue("DEPT_CHN_DESC", i));
        		 sum_ar_amt+=parm.getDouble("AR_AMT", i);
        		 tableParm.addData("AR_AMT",parm.getDouble("AR_AMT", i));
        		 sum_reduce_amt+=parm.getDouble("REDUCE_AMT", i);
        		 tableParm.addData("REDUCE_AMT",parm.getDouble("REDUCE_AMT", i));
        		for(int j=1;j<22;j++){
        			if(j<10){
        				tableParm.addData("CHARGE0"+j,parm.getDouble("CHARGE0"+j, i));
        				sum[j-1]+=parm.getDouble("CHARGE0"+j, i);
        			}else{
        				tableParm.addData("CHARGE"+j,parm.getDouble("CHARGE"+j, i));
        				sum[j-1]+=parm.getDouble("CHARGE"+j, i);
        			}
        		}
        		 tableParm.addData("REDUCE_NOTE",parm.getValue("REDUCE_NOTE", i));
        		 tableParm.addData("REDUCE_TYPE",parm.getValue("REDUCE_TYPE", i));
        		 tableParm.addData("REDUCE_USER",parm.getValue("REDUCE_USER", i));
        		 tableParm.addData("OPT_USER",parm.getValue("OPT_USER", i));
        		 tableParm.addData("LOCTION",parm.getValue("LOCATION", i));
        		 }
        		 tableParm.setCount(tableParm.getCount("CASE_NO"));
        		 
        		 tableParm.addData("TID","");
        		 tableParm.addData("CASE_NO","");
        		 tableParm.addData("MR_NO","");
        		 tableParm.addData("PAT_NAME","");
        		 tableParm.addData("SEX_CODE","");
        		 tableParm.addData("CTZ_DESC","");		 
        		 tableParm.addData("OPT_DATE","");
        		 tableParm.addData("DEPT_CHN_DESC","合计:");
        		// tableParm.addData("AR_AMT",sum_ar_amt);
        		 tableParm.addData("REDUCE_AMT",sum_reduce_amt);
        			for(int j=1;j<22;j++){
            			if(j<10)
            				tableParm.addData("CHARGE0"+j,sum[j-1]);
            			else
            				tableParm.addData("CHARGE"+j,sum[j-1]);
        			}
        			 tableParm.addData("REDUCE_NOTE","");
            		 tableParm.addData("REDUCE_TYPE","");
            		 tableParm.addData("REDUCE_USER","");
            		 tableParm.addData("OPT_USER","");
            		 tableParm.addData("LOCTION","");
        		table.setParmValue(tableParm);
    	}

    }
    
    /**
     * 清空方法
     */ 
    public void onClear(){
    	  this.clearValue("MR_NO");
    	initPage();
    }
    
    /**
	 * 汇出Excel
	 */
	public void onExcel() {
		// 得到UI对应控件对象的方法
		TParm parm = table.getShowParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		if(yzjm.isSelected()){
		ExportExcelUtil.getInstance().exportExcel(table, "住院医嘱减免表");
		}
		if(qtjm.isSelected()){
			ExportExcelUtil.getInstance().exportExcel(table, "住院其他减免表");
		}
		}
	  public void onMrno(){
	    	Pat pat = Pat.onQueryByMrNo(PatTool.getInstance().checkMrno(getValueString("MR_NO")));
			if (pat == null) {
				messageBox_("查无此病案号");
				this.setValue("MR_NO", "");
			}
			this.setValue("MR_NO", pat.getMrNo());
	    }
}
