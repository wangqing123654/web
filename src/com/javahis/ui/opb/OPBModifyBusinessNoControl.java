package com.javahis.ui.opb;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.sys.Pat;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
/**
 * <p>Title: 微信支付宝交易号修改</p>
 *
 * <p>Description: 微信支付宝交易号修改</p>
 *
 * <p>Copyright: Copyright (c) 2016</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author kangy 20160815
 * @version 1.0
 */
public class OPBModifyBusinessNoControl extends TControl{
	private TTable tableM;
	private TTable tableD;
	private String header="";
	private String parmMap="";
	private String columnHorizontalAlignmentData="";
	private TParm  tableDParm;
	private TParm tableMParm;
	private String sql="";
	private String s_date="";
	private String e_date="";
	private String where="";
	private int lpkRow ;
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		 /**
	     * 初始化
	     */
	    public void onInit() {
	        super.onInit();
	        initPage();
	        onRadioChange();
	        }
	    /**
	     * 查询
	     */
	    public void onQuery(){
	    	 s_date=sdf.format(this.getValue("START_DATE"));
	    	 e_date=sdf.format(this.getValue("END_DATE"));
	    	 //计费打票
	    if(this.getRadioButton("JFDP").isSelected()){
	    	if(this.getValue("ADM_TYPE").toString().length()<=0){
	    		this.messageBox("请选择门急住别！");
	    		return;
	    	}
	    		onJFDP();
	    		return;
	    	}
	    	//预交金
	    	if(this.getRadioButton("YJJ").isSelected()){
	    		onYJJ();
	    		return;
	    	}
	    	//医疗卡
	        if(this.getRadioButton("YLK").isSelected()){
	     		onYLK();
	     		return;
	     	}
	        //套餐
	    	if(this.getRadioButton("TC").isSelected()){
	    		onTC();	
	    		return;
	    	}
	    	//会员卡
	    	if(this.getRadioButton("HYK").isSelected()){
	    		onHYK();
	    		return;
	    	}
	    	//礼品卡
	    	if(this.getRadioButton("LPK").isSelected()){
	    		onLPK();
	    		return;
	    	}
	    }
	    
	    /**
	     * 门急住别改变
	     */
	    public void onChangeAdmType(){
	    	if(this.getValue("ADM_TYPE").toString().length()<=0){
	    		this.setValue("DATE", "就诊时间:");
	    	}
	    	if("O".equals(this.getValue("ADM_TYPE").toString())){
	    		this.setValue("NO", "病案号:");
	    		 header="门急住别,100,ADM_TYPE;就诊号,100;病案号,100;姓名,100;性别,30;就诊时间,160,Timestamp,yyyy/MM/ss HH:mm:ss;经治医生,100,DR_CODE";
		    	 parmMap="ADM_TYPE;CASE_NO;MR_NO;PAT_NAME;SEX_CODE;ADM_DATE;REALDR_CODE";
		    	 columnHorizontalAlignmentData="0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left";
		    	 tableM.setHeader(header);
		    	tableM.setParmMap(parmMap);
		    	tableM.setItem("ADM_TYPE;DR_CODE");
		    	tableM.setColumnHorizontalAlignmentData(columnHorizontalAlignmentData);
	    		this.setValue("DATE", "就诊时间:");
	    	}
	    	if("I".equals(this.getValue("ADM_TYPE").toString())){
	    		this.setValue("DATE", "入院时间:");
	        	header="门急住别,100,ADM_TYPE;就诊号,100;病案号,100;姓名,100;性别,30;入院时间,160,Timestamp,yyyy/MM/ss HH:mm:ss;出院时间,160,Timestamp,yyyy/MM/ss HH:mm:ss;床位,100;科室,100,DEPT_CODE;病区,100,STATION_CODE;经治医生,100,DR_CODE";
		    	 parmMap="ADM_TYPE;CASE_NO;MR_NO;PAT_NAME;SEX_CODE;IN_DATE;DS_DATE;BED_NO;DEPT_CODE;STATION_CODE;VS_DR_CODE";
		    	 columnHorizontalAlignmentData="0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left";
		    	 tableM.setHeader(header);
		    	tableM.setParmMap(parmMap);
		    	tableM.setItem("ADM_TYPE;DEPT_CODE;STATION_CODE;DR_CODE");
		    	tableM.setColumnHorizontalAlignmentData(columnHorizontalAlignmentData);
	    	}
	    }
	    /**
	     * 病案号回车
	     */
	    public void onMrNo(){
	        Pat pat = new Pat();
	    	String mrno = getValue("MR_NO").toString().trim();
	    	if(this.getRadioButton("LPK").isSelected()){
	    		onQuery();
	    	}else{
	    	pat = Pat.onQueryByMrNo(mrno);
	    	this.setValue("MR_NO", pat.getMrNo());
	          if (pat == null) {
	              this.messageBox("查无病患！");
	              return;
	          }
	    	onQuery();
	    	}
	    }
	    /**
	     * 清空
	     */
	    public void onClear(){
	    	tableM.removeRowAll();
	    	tableD.removeRowAll();
	    	this.setValue("MR_NO", "");
	    	onInit();
	    	//initPage();
	    }
	    /**
	     * 初始化页面
	     */
	    public void initPage(){
	    	this.getRadioButton("JFDP").setSelected(true);
	    	callFunction("UI|ADM_TYPE|setEnabled", true);
	    	  tableM=(TTable) this.getComponent("TABLEM");
	          tableD=(TTable) this.getComponent("TABLED");
	          
	    	Timestamp date = StringTool.getTimestamp(new Date());
	    	
	    	//设置时间
	    	this.setValue("START_DATE",
					date.toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
			this.setValue("END_DATE",
					date.toString().substring(0, 10).replace('-', '/')+ " 23:59:59");
			this.setValue("DATE","就诊时间:");
			this.setValue("ADM_TYPE","O");
			this.callFunction("UI|I|SELECTED", true);
	    }

	    /**
	     * 交易类型改变
	     */
	    public void onRadioChange(){
	    	
	   	 //计费打票
		    if(this.getRadioButton("JFDP").isSelected()){
		    	this.callFunction("UI|ADM_TYPE|Enabled",true);
		    	this.callFunction("UI|ADM_TYPE|setValue","O");
	    		if(this.getValue("ADM_TYPE").equals("I")){
	    			this.setValue("DATE","入院时间:");
	    		}else{
	    			this.setValue("DATE", "就诊时间:");
	    		}
		    	this.setValue("NO", "病案号:");
		    	if(this.getValue("ADM_TYPE").equals("I")){//住院
		    	header="门急住别,100,ADM_TYPE;就诊号,100;病案号,100;姓名,100;性别,30;入院时间,160,Timestamp,yyyy/MM/ss HH:mm:ss;出院时间,160,Timestamp,yyyy/MM/ss HH:mm:ss;床位,100;科室,100,DEPT_CODE;病区,100,STATION_CODE;经治医生,100,DR_CODE";
		    	 parmMap="ADM_TYPE;CASE_NO;MR_NO;PAT_NAME;SEX_CODE;IN_DATE;DS_DATE;BED_NO;DEPT_CODE;STATION_CODE;VS_DR_CODE";
		    	 columnHorizontalAlignmentData="0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left";
		    	 tableM.setHeader(header);
		    	tableM.setParmMap(parmMap);
		    	tableM.setItem("ADM_TYPE;DEPT_CODE;STATION_CODE;DR_CODE");
		    	tableM.setColumnHorizontalAlignmentData(columnHorizontalAlignmentData);
		    		return;
		    	}else{//门诊
		    		this.setValue("NO", "病案号:");
		    		 header="门急住别,100,ADM_TYPE;就诊号,100;病案号,100;姓名,100;性别,30;就诊时间,160,Timestamp,yyyy/MM/ss HH:mm:ss;经治医生,100,DR_CODE";
			    	 parmMap="ADM_TYPE;CASE_NO;MR_NO;PAT_NAME;SEX_CODE;ADM_DATE;REALDR_CODE";
			    	 columnHorizontalAlignmentData="0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left";
			    	 tableM.setHeader(header);
			    	tableM.setParmMap(parmMap);
			    	tableM.setItem("ADM_TYPE;DR_CODE");
			    	tableM.setColumnHorizontalAlignmentData(columnHorizontalAlignmentData);
		    	}
		    	}
		    	//预交金
		    	if(this.getRadioButton("YJJ").isSelected()){
		    		this.setValue("NO", "病案号:");
		    		this.callFunction("UI|ADM_TYPE|setValue","");
		    		this.callFunction("UI|ADM_TYPE|Enabled",false);
		    		this.setValue("DATE","充值时间:");
		       	 header="就诊号,100;病案号,100;姓名,100;性别,30;入院时间,160,Timestamp,yyyy/MM/ss HH:mm:ss;出院时间,160,Timestamp,yyyy/MM/ss HH:mm:ss;床位,100;科室,100,DEPT_CODE;病区,100,STATION_CODE;经治医生,100,DR_CODE";
		    	 parmMap="CASE_NO;MR_NO;PAT_NAME;SEX_CODE;IN_DATE;DS_DATE;BED_NO;DS_DEPT_CODE;STATION_CODE;VS_DR_CODE";
		    	 columnHorizontalAlignmentData="0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left";
		    	tableM.setHeader(header);
		    	tableM.setParmMap(parmMap);
		    	tableM.setItem("DEPT_CODE;STATION_CODE;DR_CODE");
		    	tableM.setColumnHorizontalAlignmentData(columnHorizontalAlignmentData);
		    		return;
		    	}
		    	//医疗卡
		        if(this.getRadioButton("YLK").isSelected()){
		        	this.callFunction("UI|ADM_TYPE|setValue","");
		    		this.callFunction("UI|ADM_TYPE|Enabled",false);
		    		this.setValue("DATE","充值时间:");
		        	this.setValue("NO", "病案号:");
		        	 header="医疗卡号,100;病案号,100;姓名,100;身份证号,160;性别,30;出生日期,160;现住址,200";
			    	 parmMap="CARD_NO;MR_NO;PAT_NAME;ID_NO;SEX_CODE;BIRTH_DATE;CURRENT_ADDRESS";
			    	 columnHorizontalAlignmentData="0,left;1,left;2,left;3,left;4,left;5,left;6,left";
			    	 tableM.setHeader(header);
			    	tableM.setParmMap(parmMap);
			    	tableM.setColumnHorizontalAlignmentData(columnHorizontalAlignmentData);
		     		return;
		     	}
		        //套餐
		    	if(this.getRadioButton("TC").isSelected()){
		    		this.callFunction("UI|ADM_TYPE|setValue","");
		    		this.callFunction("UI|ADM_TYPE|Enabled",false);
		    		this.setValue("DATE","充值时间:");
		    		this.setValue("NO", "病案号:");
			    	 header="套餐号码,100;套餐名称,120;病案号,100;姓名,100;身份证号,160;性别,30;出生日期,160;现住址,200";
			    	 parmMap="PACKAGE_CODE;PACKAGE_DESC;MR_NO;PAT_NAME;IDNO;SEX_CODE;BIRTH_DATE;CURRENT_ADDRESS";
			    	 columnHorizontalAlignmentData="0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left";
			    	 tableM.setHeader(header);
			    	tableM.setParmMap(parmMap);
			    	tableM.setColumnHorizontalAlignmentData(columnHorizontalAlignmentData);
		    		return;
		    	}
		    	//会员卡
		    	if(this.getRadioButton("HYK").isSelected()){
		    		this.callFunction("UI|ADM_TYPE|setValue","");
		    		this.callFunction("UI|ADM_TYPE|Enabled",false);
		    		this.setValue("DATE","充值时间:");
		    		this.setValue("NO", "病案号:");
		    		header="会员卡号码,100; 病案号,100;姓名,100;身份证号,160;性别,30;出生日期,160;现住址,200";
			    	 parmMap="MEM_CARD_NO;MR_NO;PAT_NAME;IDNO;SEX_CODE;BIRTH_DATE;ADDRESS;TRADE_NO";
			    	 columnHorizontalAlignmentData="0,left;1,left;2,left;3,left;4,left;5,left;6,left";
			    	 tableM.setHeader(header);
			    	tableM.setParmMap(parmMap);
			    	tableM.setColumnHorizontalAlignmentData(columnHorizontalAlignmentData);
		    		return;
		    	}
		    	//礼品卡
		    	if(this.getRadioButton("LPK").isSelected()){
		    		this.callFunction("UI|ADM_TYPE|setValue","");
		    		this.callFunction("UI|ADM_TYPE|Enabled",false);
		    		this.setValue("DATE","充值时间:");
		    		this.setValue("NO", "礼品卡号:");
		    		 header="礼品卡号码,100;姓名,100;证件类型,100,CERTIFICATE_TYPE;证件号码,120";
			    	 parmMap="TRADE_NO;PURCHASER_NAME;CERTIFICATE_TYPE;CERTIFICATE_NO";
			    	 columnHorizontalAlignmentData="0,left;1,left;2,left;3,left;4,left;5,left;6,left";
			    	 tableM.setHeader(header);
			    	tableM.setParmMap(parmMap);
			    	tableM.setItem("CERTIFICATE_TYPE");
			    	tableM.setColumnHorizontalAlignmentData(columnHorizontalAlignmentData);
		    		return;
		    	}
	    }
	    /**
	     * 主表单击事件
	     */
	    public void onClickM(int row1){
	    	 TParm parm = (TParm) callFunction("UI|TABLEM|getParmValue");
	    	 int row = (Integer) callFunction("UI|TABLEM|getSelectedRow");
	    	 s_date=sdf.format(this.getValue("START_DATE"));
	    	 e_date=sdf.format(this.getValue("END_DATE"));
	    	  /**
	    	     * 计费打票明细
	    	     */
	    	 if(this.getRadioButton("JFDP").isSelected()){
	    		  String sql="";
	    		  if("O".equals(parm.getValue("ADM_TYPE",row))){// 门诊
	    			   sql="SELECT A.PRINT_NO,A.RECEIPT_NO TRADE_NO,A.OPT_DATE," +
	    			   	" CASE WHEN AR_AMT>0 THEN '收费' " +
	    				" WHEN  AR_AMT<0 THEN '退费' " +
	    				" END OPT_TYPE,A.AR_AMT TOT_AMT," +
	    			  	" A.PAY_TYPE09 WX_AMT,A.PAY_TYPE10 ZFB_AMT,A.WX_BUSINESS_NO," +
	    			  	" A.ZFB_BUSINESS_NO  FROM BIL_OPB_RECP A,REG_PATADM B WHERE A.CASE_NO=B.CASE_NO " +
	    			  	" AND A.MR_NO=B.MR_NO AND A.ADM_TYPE='O' AND " +
	    				" (A.PAY_TYPE09<>0 OR A.PAY_TYPE10<>0)" +
	    			  	" AND (A.PAY_TYPE09 IS NOT NULL OR A.PAY_TYPE10 IS NOT NULL)" +
	    			  	" AND B.ADM_DATE BETWEEN TO_DATE('"+s_date+"', 'YYYYMMDDHH24MISS') "+
	    			  	" AND TO_DATE('"+e_date+"', 'YYYYMMDDHH24MISS')";
	    		  }
	    		  if("I".equals(parm.getValue("ADM_TYPE",row))){//住院
	    			   sql="SELECT A.PRINT_NO,A.RECEIPT_NO TRADE_NO,A.OPT_DATE,CASE WHEN  AR_AMT>0 THEN '收费' " +
	    				" WHEN  AR_AMT<0 THEN '退费' " +
	    				" END OPT_TYPE,A.AR_AMT TOT_AMT," +
	    			  	" A.PAY_TYPE09 WX_AMT,A.PAY_TYPE10 ZFB_AMT,A.WX_BUSINESS_NO," +
	    			  	" A.ZFB_BUSINESS_NO  FROM BIL_IBS_RECPM A,ADM_INP B WHERE A.CASE_NO=B.CASE_NO " +
	    			  	" AND A.MR_NO=B.MR_NO AND A.ADM_TYPE='I' AND " +
	    			  	" (A.PAY_TYPE09<>0 OR PAY_TYPE10<>0) " +
	    			  	" AND (A.PAY_TYPE09 IS NOT NULL OR PAY_TYPE10 IS NOT NULL) "+ 
	    			  	" AND B.IN_DATE BETWEEN TO_DATE('"+s_date+"', 'YYYYMMDDHH24MISS') "+
	    			  	" AND TO_DATE('"+e_date+"', 'YYYYMMDDHH24MISS') " ;
	    						
	    		  }
	    		  if(parm.getValue("MR_NO",row).toString().length()>0){
	    			 sql+= " AND A.MR_NO='"+parm.getValue("MR_NO",row)+"'";
	    		  }
	          		tableDParm=new TParm(TJDODBTool.getInstance().select(sql));
	          		tableD.setParmValue(tableDParm);
	      		return;
	      	}
	    	 
	    	 /**
	 	     * 预交金明细
	 	     */
	       	if(this.getRadioButton("YJJ").isSelected()){
	      		String sql="SELECT RECEIPT_NO,PRINT_NO,RECEIPT_NO TRADE_NO,OPT_DATE," +
	      				"CASE WHEN PRE_AMT>0 THEN '收费' " +
	    				" WHEN PRE_AMT<0 THEN '退费'" +
	    				" END OPT_TYPE," +
	      				" PRE_AMT TOT_AMT,BUSINESS_NO,PAY_TYPE" +
	      				" FROM BIL_PAY WHERE (PAY_TYPE='WX' OR PAY_TYPE='ZFB') AND CHARGE_DATE BETWEEN TO_DATE('"+s_date+"', 'YYYYMMDDHH24MISS') "+
	    				" AND TO_DATE('"+e_date+"', 'YYYYMMDDHH24MISS')" +
	      				" AND MR_NO='"+parm.getValue("MR_NO",row)+"'" +
	      				" AND CASE_NO='"+parm.getValue("CASE_NO",row)+"'";
	      		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
	      		tableDParm=result;
	      		for(int i=0;i<result.getCount();i++){
	      		if("WX".equals(result.getValue("PAY_TYPE",i))){
	      			tableDParm.setData("WX_AMT",i,result.getValue("TOT_AMT",i));
	      			tableDParm.setData("WX_BUSINESS_NO",i,result.getValue("BUSINESS_NO",i));
	      		}else{
	      			tableDParm.setData("WX_AMT",i,"");
	      			tableDParm.setData("WX_BUSINESS_NO",i,"");
	      		}
	      		if("ZFB".equals(result.getValue("PAY_TYPE",i))){
	      			tableDParm.setData("ZFB_AMT",i,result.getValue("TOT_AMT",i));
	      			tableDParm.setData("ZFB_BUSINESS_NO",i,result.getValue("BUSINESS_NO",i));
	      		}else{
	      			tableDParm.setData("ZFB_AMT",i,"");
	      			tableDParm.setData("ZFB_BUSINESS_NO",i,"");
	      		}
	      		}
	      		
	      		tableD.setParmValue(tableDParm);
	      		return;
	      	}
	      	
	        /**
		     * 医疗卡明细
		     */
	          if(this.getRadioButton("YLK").isSelected()){
	        	  
	       		String sql="SELECT PRINT_NO,BIL_BUSINESS_NO TRADE_NO,OPT_DATE," +
	       				" CASE WHEN  ((ACCNT_TYPE='4' OR ACCNT_TYPE='2') AND AMT>0) THEN '收费' " +
	    				" WHEN (ACCNT_TYPE='6' AND AMT>0) THEN '退费' "+
	    				" END OPT_TYPE," +
	       				" AMT TOT_AMT,GATHER_TYPE,ACCNT_TYPE " +
	       				" FROM EKT_BIL_PAY WHERE (GATHER_TYPE='WX' OR GATHER_TYPE='ZFB') AND CARD_NO='"+parm.getValue("CARD_NO",row)+"'" +
	       				" AND OPT_DATE BETWEEN TO_DATE('"+s_date+"', 'YYYYMMDDHH24MISS')" +
	      				" AND TO_DATE('"+e_date+"', 'YYYYMMDDHH24MISS')";
	      
	      	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
	       	tableDParm=result;
	       	for(int i=0;i<result.getCount();i++){
	       	
	       	if("WX".equals(result.getValue("GATHER_TYPE",i))){
	       		if("6".equals(result.getValue("ACCNT_TYPE",i))){
	       		tableDParm.setData("TOT_AMT",i,-result.getDouble("TOT_AMT",i));
	       		tableDParm.setData("WX_AMT",i,result.getDouble("TOT_AMT",i));
	       		tableDParm.setData("WX_BUSINESS_NO",i,result.getData("PRINT_NO",i));
	       		}else{
	       		tableDParm.setData("TOT_AMT",i,result.getDouble("TOT_AMT",i));	
	       		tableDParm.setData("WX_AMT",i,result.getDouble("TOT_AMT",i));
		       	tableDParm.setData("WX_BUSINESS_NO",i,result.getData("PRINT_NO",i));
	       		}
	       		
	       	}else{
	       		tableDParm.setData("WX_AMT",i,"0.0");
	       		tableDParm.setData("WX_BUSINESS_NO",i,"");
	       	}
	       	
	       	if("ZFB".equals(result.getValue("GATHER_TYPE",i))){
	       		if("6".equals(result.getValue("ACCNT_TYPE",i))){
	       		tableDParm.setData("TOT_AMT",i,-result.getDouble("TOT_AMT",i));
	       		tableDParm.setData("ZFB_AMT",i,result.getDouble("TOT_AMT",i));
	       		tableDParm.setData("ZFB_BUSINESS_NO",i,result.getData("PRINT_NO",i));
	       		}else{
	       			tableDParm.setData("TOT_AMT",i,result.getDouble("TOT_AMT",i));
	       			tableDParm.setData("ZFB_AMT",i,result.getData("TOT_AMT",i));
		       		tableDParm.setData("ZFB_BUSINESS_NO",i,result.getData("PRINT_NO",i));
	       		}
	       		}else{
	       		tableDParm.setData("ZFB_AMT",i,"0.0");
	       		tableDParm.setData("ZFB_BUSINESS_NO",i,"");
	       	}
	    	tableDParm.setData("PRINT_NO",i,"");
	       	}
	      		tableD.setParmValue(tableDParm);
	      		return;
	       	}
	        
	          /**
	  	     * 套餐明细
	  	     */
	      	if(this.getRadioButton("TC").isSelected()){
	      		String sql="SELECT A.TRADE_NO,A.OPT_DATE," +
	      				"  CASE WHEN  A.AR_AMT>0 THEN '收费' " +
	    				" WHEN  A.AR_AMT<0 THEN '退费' "+
	    				"  END OPT_TYPE," +
	      				" A.AR_AMT TOT_AMT,A.PAY_TYPE09 WX_AMT,A.PAY_TYPE10 ZFB_AMT,A.WX_BUSINESS_NO,A.ZFB_BUSINESS_NO" +
	      				" FROM MEM_PACKAGE_TRADE_M A,MEM_PAT_PACKAGE_SECTION B WHERE (A.PAY_TYPE09<>0 OR A.PAY_TYPE10<>0)" +
	      				" AND (A.PAY_TYPE09 IS NOT NULL OR A.PAY_TYPE10 IS NOT NULL)" +
	      				" AND A.MR_NO='"+parm.getValue("MR_NO",row)+"'" +
	      				" AND A.OPT_DATE BETWEEN TO_DATE('"+s_date+"', 'YYYYMMDDHH24MISS')" +
	      				" AND TO_DATE('"+e_date+"', 'YYYYMMDDHH24MISS') " +
	      				" AND (A.TRADE_NO=B.TRADE_NO OR A.TRADE_NO=B.REST_TRADE_NO)" +
	      				"AND B.PACKAGE_CODE='"+parm.getValue("PACKAGE_CODE",row)+"' AND A.MR_NO=B.MR_NO" +
	      						"  and b.rest_trade_no is not null";
	      		System.out.println(sql);
	      		tableDParm=new TParm(TJDODBTool.getInstance().select(sql));
	      		for(int i=0;i<tableDParm.getCount();i++){
	      		tableDParm.setData("PRINT_NO",i,"");
	      		}
	      		tableD.setParmValue(tableDParm);
	      		return;
	      	}
	      	
	      	 /**
		     * 会员卡明细
		     */
	      	if(this.getRadioButton("HYK").isSelected()){
	      		String sql="SELECT TRADE_NO,OPT_DATE," +
	      				"CASE WHEN  STATUS='1' THEN '购卡收费' " +
	    				" WHEN  STATUS='2' THEN '退卡退费' "+
	    				" WHEN  STATUS='3' THEN '停卡退费' "+
	    				"  END OPT_TYPE, " +
	      				"MEM_FEE TOT_AMT,PAY_TYPE09 WX_AMT,PAY_TYPE10 ZFB_AMT,WX_BUSINESS_NO,ZFB_BUSINESS_NO " +
	      				" FROM MEM_TRADE WHERE  " +
	      				"  (PAY_TYPE09<>0 OR PAY_TYPE10<>0) " +
	      				" AND (PAY_TYPE09 IS NOT NULL OR PAY_TYPE10 IS NOT NULL)" +
	      				" AND MR_NO='"+parm.getValue("MR_NO",row)+"'" +
	      				" AND OPT_DATE BETWEEN TO_DATE('"+s_date+"', 'YYYYMMDDHH24MISS')" +
	      				" AND TO_DATE('"+e_date+"', 'YYYYMMDDHH24MISS')" +
	      				" AND MEM_CARD_NO='"+tableM.getParmValue().getValue("MEM_CARD_NO",row)+"'";
	      		tableDParm=new TParm(TJDODBTool.getInstance().select(sql));
	      		for(int i=0;i<tableDParm.getCount();i++){
	      		tableDParm.setData("PRINT_NO",i,"");
	      		}
	      		tableD.setParmValue(tableDParm);
	      		return;
	      	}
	      	
	      	 /**
		     * 礼品卡明细
		     */
	      	if(this.getRadioButton("LPK").isSelected()){
	      		String sql="SELECT  OPT_DATE,'收费' OPT_TYPE,AR_AMT TOT_AMT,PAY_TYPE09 WX_AMT,PAY_TYPE10 ZFB_AMT,WX_BUSINESS_NO,ZFB_BUSINESS_NO " +
	      				" FROM MEM_GIFTCARD_TRADE_M WHERE (PAY_TYPE09>0 OR PAY_TYPE10>0) AND TRADE_NO='"+parm.getValue("TRADE_NO",row)+"'";
	      		lpkRow=tableM.getSelectedRow();
	      		tableDParm=new TParm(TJDODBTool.getInstance().select(sql));
	      		tableDParm.addData("PRINT_NO","");
	      		tableD.setParmValue(tableDParm);
	      		return;
	      	
	      	}
	    	 
	    }

	    
	    
	    /**
	     * 修改交易号
	     */
	    public void onModify(){
	    	TParm parm =new TParm();
			int row =tableD.getSelectedRow();
			if(row==-1){
				this.messageBox("请选择要修改的数据");
				return;
			}
			
			parm=tableD.getParmValue().getRow(row);
			  if(this.getRadioButton("JFDP").isSelected()){
				  parm.setData("TRADE_TYPE", "JFDP");
				  parm.setData("ADM_TYPE",this.getValue("ADM_TYPE"));
		      	}
		    	  
		      	if(this.getRadioButton("YJJ").isSelected()){
		      		 parm.setData("TRADE_TYPE", "YJJ");
		      	}
		      	
		          if(this.getRadioButton("YLK").isSelected()){
		        	  
		        	  parm.setData("TRADE_TYPE", "YLK");
		       	}
		          
		      	if(this.getRadioButton("TC").isSelected()){
		      		 parm.setData("TRADE_TYPE", "TC");
		      	}
		      	
		      	if(this.getRadioButton("HYK").isSelected()){
		      		 parm.setData("TRADE_TYPE", "HYK");
		      	}
		      	if(this.getRadioButton("LPK").isSelected()){
		      		parm.setData("TRADE_NO",tableM.getValueAt(lpkRow, 0));
		      		parm.setData("TRADE_TYPE", "LPK");
		      	}
		      	if(parm.getDouble("WX_AMT")!=0||parm.getDouble("ZFB_AMT")!=0){
			 openDialog("%ROOT%\\config\\opb\\OPBModifyPrintNo.x",
					parm); 
			
				onClickM(row);
			
		      	}
	    }
	    /**
	     * 计费打票主表信息
	     */
	    public void onJFDP(){
	    	
	    	//住院
			if(this.getValue("ADM_TYPE").equals("I")){
			  	sql="SELECT DISTINCT A.CASE_NO,A.MR_NO,C.PAT_NAME,CASE WHEN C.SEX_CODE = '1' THEN '男' " +
	    				" WHEN C.SEX_CODE = '2' THEN  '女' ELSE  '未知' END SEX_CODE,B.IN_DATE," +
	    				" B.DS_DATE,B.BED_NO,B.DEPT_CODE,B.STATION_CODE,B.VS_DR_CODE" +
	    				" FROM BIL_IBS_RECPM A ,ADM_INP B,SYS_PATINFO C " +
	    				" WHERE (A.PAY_TYPE09<>0 OR A.PAY_TYPE10<>0) AND (A.PAY_TYPE09 IS NOT NULL OR A.PAY_TYPE10 IS NOT NULL) " +
	    				" AND A.CASE_NO=B.CASE_NO " +
	    				" AND A.MR_NO=B.MR_NO  AND A.ADM_TYPE='I' AND A.MR_NO=C.MR_NO " +
	    				" AND B.IN_DATE BETWEEN TO_DATE('"+s_date+"', 'YYYYMMDDHH24MISS') "+
	    				" AND TO_DATE('"+e_date+"', 'YYYYMMDDHH24MISS') ";
			  	if(this.getValue("MR_NO").toString().length()>0){
			  	sql+=" AND A.MR_NO='"+this.getValue("MR_NO")+"'";	
			  	}
		    	tableMParm=new TParm(TJDODBTool.getInstance().select(sql));
		    	if(tableMParm.getCount()<=0){
		    		this.messageBox("没有查询到数据");
		    		tableM.removeRowAll();
		    		tableD.removeRowAll();
		    	}
		    	for(int i=0;i<tableMParm.getCount();i++){
		    	tableMParm.setData("ADM_TYPE",i,this.getValue("ADM_TYPE"));
		    	}
		    	
			}else{//门诊
			  	sql="SELECT DISTINCT A.CASE_NO,A.MR_NO,C.PAT_NAME,CASE WHEN C.SEX_CODE = '1' THEN '男' " +
	    				" WHEN C.SEX_CODE = '2' THEN  '女' ELSE  '未知' END SEX_CODE, B.ADM_DATE,B.REALDR_CODE" +
	    				" FROM BIL_OPB_RECP A,REG_PATADM B,SYS_PATINFO C " +
	    				"WHERE (A.PAY_TYPE09<>0 OR A.PAY_TYPE10<>0) " +
	    				" AND (A.PAY_TYPE09 IS NOT NULL OR A.PAY_TYPE10 IS NOT NULL) "+
	    				" AND A.CASE_NO=B.CASE_NO AND A.MR_NO=B.MR_NO AND A.MR_NO=C.MR_NO" +
	    				" AND B.ADM_DATE BETWEEN TO_DATE('"+s_date+"', 'YYYYMMDDHH24MISS') "+
	    				" AND TO_DATE('"+e_date+"', 'YYYYMMDDHH24MISS')";
			  			if(this.getValue("MR_NO").toString().length()>0){
			  				sql+=" AND A.MR_NO='"+this.getValue("MR_NO")+"'";
			  			}
		    	tableMParm=new TParm(TJDODBTool.getInstance().select(sql));
		    	if(tableMParm.getCount()<=0){
		    		this.messageBox("没有查询到数据");
		    		tableM.removeRowAll();
		    		tableD.removeRowAll();
		    	}
		    	for(int i=0;i<tableMParm.getCount();i++){
		    		tableMParm.setData("ADM_TYPE",i,this.getValue("ADM_TYPE"));
		    	}
		    	
			}
			tableM.setParmValue(tableMParm);
	    }
	    /**
	     * 预交金主表信息
	     */
	    public void onYJJ(){
	    	if(this.getValue("MR_NO").toString().length()>0){
	    		sql=" SELECT DISTINCT A.CASE_NO,A.MR_NO,B.PAT_NAME,CASE WHEN B.SEX_CODE = '1' THEN '男' " +
	    				" WHEN B.SEX_CODE = '2' THEN  '女' ELSE  '未知' END SEX_CODE,IN_DATE," +
	    				"DS_DATE,BED_NO,DS_DEPT_CODE,STATION_CODE,VS_DR_CODE FROM BIL_PAY A," +
	    				"SYS_PATINFO B,ADM_INP C  WHERE A.MR_NO=B.MR_NO AND A.CASE_NO=C.CASE_NO " +
	    				" AND (A.PAY_TYPE='WX' OR A.PAY_TYPE='ZFB')" +
	    				" AND A.MR_NO='"+this.getValue("MR_NO")+"'";
	    	tableMParm=new TParm(TJDODBTool.getInstance().select(sql));
	    	if(tableMParm.getCount()<=0){
	    		this.messageBox("没有查询到数据");
	    		tableM.removeRowAll();
	    		tableD.removeRowAll();
	    	}
	    	tableM.setParmValue(tableMParm);
	    	}
	    	else{
	    		this.messageBox("请填写病案号");
	    	}
	    }
	    /**
	     * 医疗卡主表信息
	     */
	    public void onYLK(){
	    	if(this.getValue("MR_NO").toString().length()>0){
	    	sql="SELECT DISTINCT A.CARD_NO,A.MR_NO,B.PAT_NAME,A.ID_NO,CASE WHEN B.SEX_CODE = '1' THEN '男'" +
	    			" WHEN B.SEX_CODE = '2' THEN  '女' ELSE  '未知' END SEX_CODE,B.BIRTH_DATE,B.CURRENT_ADDRESS " +
	    			" FROM EKT_MASTER A,SYS_PATINFO B,EKT_BIL_PAY C  WHERE A.MR_NO=B.MR_NO " +
	                " AND A.MR_NO='"+this.getValue("MR_NO")+"'" +
	                " AND (C.GATHER_TYPE='WX' OR C.GATHER_TYPE='ZFB') " +
	                " AND A.CARD_NO=C.CARD_NO ";
	    	tableMParm=new TParm(TJDODBTool.getInstance().select(sql+where));
	    	if(tableMParm.getCount()<=0){
	    		this.messageBox("没有查询到数据");
	    		tableM.removeRowAll();
	    		tableD.removeRowAll();
	    	}
	    	tableM.setParmValue(tableMParm);
	    	}
	    	else{
	    		this.messageBox("请填写病案号");
	    	}
	    }
	    /**
	     * 套餐主表信息
	     */
	    public void onTC(){
	    	if(this.getValue("MR_NO").toString().length()>0){
	    	sql="SELECT DISTINCT  B.PACKAGE_CODE,B.PACKAGE_DESC, A.MR_NO,C.PAT_NAME,CASE WHEN C.SEX_CODE = '1' THEN '男' " +
	    			" WHEN C.SEX_CODE = '2' THEN  '女' ELSE  '未知' END SEX_CODE,C.IDNO," +
	    			"C.BIRTH_DATE,C.CURRENT_ADDRESS  FROM MEM_PACKAGE_TRADE_M A," +
	    			"MEM_PAT_PACKAGE_SECTION B,SYS_PATINFO C " +
	    			" WHERE (A.TRADE_NO=B.TRADE_NO OR A.TRADE_NO=B.REST_TRADE_NO) AND A.MR_NO=C.MR_NO " +
	    			" AND (PAY_TYPE09 IS NOT NULL OR PAY_TYPE10 IS NOT NULL) " +
	    			" AND (PAY_TYPE09<>0 OR PAY_TYPE10<>0) " +
	    			" AND A.MR_NO='"+this.getValue("MR_NO")+"'" +
	    			" GROUP BY A.MR_NO, B.PACKAGE_CODE,B.PACKAGE_DESC, " +
	    			"A.MR_NO,C.PAT_NAME,SEX_CODE,C.IDNO,C.BIRTH_DATE," +
	    			"C.CURRENT_ADDRESS ";
	    	tableMParm=new TParm(TJDODBTool.getInstance().select(sql));
	    	if(tableMParm.getCount()<=0){
	    		this.messageBox("没有查询到数据");
	    		tableM.removeRowAll();
	    		tableD.removeRowAll();
	    	}
	    	tableM.setParmValue(tableMParm);
	    	}
	    	else{
	    		this.messageBox("请填写病案号");
	    	}
	    }
	    /**
	     * 会员卡主表信息
	     */
	    public void onHYK(){
	    	if(this.getValue("MR_NO").toString().length()>0){
	    	sql="SELECT DISTINCT A.MEM_CARD_NO,A.MR_NO,B.PAT_NAME,CASE WHEN B.SEX_CODE = '1' THEN '男'" +
	    			" WHEN B.SEX_CODE = '2' THEN  '女' ELSE  '未知' END SEX_CODE," +
	    			" B.IDNO,B.BIRTH_DATE,B.CURRENT_ADDRESS " +
	    			" FROM MEM_TRADE A,SYS_PATINFO B WHERE A.MR_NO=B.MR_NO " +
	    			" AND (PAY_TYPE09 IS NOT NULL OR PAY_TYPE10 IS NOT NULL)" +
	    			" AND (PAY_TYPE09<>0 OR PAY_TYPE10<>0) " +
	    			" AND A.MR_NO='"+this.getValue("MR_NO")+"'";
	    	tableMParm=new TParm(TJDODBTool.getInstance().select(sql));
	    	if(tableMParm.getCount()<=0){
	    		this.messageBox("没有查询到数据");
	    		tableM.removeRowAll();
	    		tableD.removeRowAll();
	    	}
	    	tableM.setParmValue(tableMParm);
	    	}
	    	else{
	    		this.messageBox("请填写病案号");
	    	}
	    }
	    /**
	     * 礼品卡主表信息
	     */ 
	    public void onLPK(){
	    	if(this.getValue("MR_NO").toString().length()>0){		
	    		sql=" SELECT TRADE_NO,PURCHASER_NAME,CERTIFICATE_TYPE,CERTIFICATE_NO FROM MEM_GIFTCARD_TRADE_M " +
	    		" WHERE TRADE_NO='"+this.getValue("MR_NO")+"'";
	    	tableMParm= new TParm(TJDODBTool.getInstance().select(sql));
	    	if(tableMParm.getCount()<=0){
	    		this.messageBox("没有查询到数据");
	    		tableM.removeRowAll();
	    		tableD.removeRowAll();
	    	}
	    	tableM.setParmValue(tableMParm);
	    	}else{
	    		this.messageBox("请填写礼品卡号");
	    	}
	    }

	    
	    //得到radiobuttom控件
	    public TRadioButton getRadioButton(String tag){
	    	return (TRadioButton) getComponent(tag);
	    }
	    //得到下拉列表控件
	    public TComboBox getTcomboBox(String tag){
	    	return (TComboBox) getComponent(tag);
	    }
	}



