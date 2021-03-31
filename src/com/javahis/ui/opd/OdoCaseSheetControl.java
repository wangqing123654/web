package com.javahis.ui.opd;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import jdo.odo.ODO;
import jdo.odo.OpdOrder;
import jdo.odo.OpdRxSheetTool;
import jdo.opd.ODOTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWord;
import com.dongyang.util.StringTool;
import com.javahis.ui.emr.EMRTool;

/**
 *
 * <p>Title: 门诊医生工作站补打处方签</p>
 *
 * <p>Description:门诊医生工作站补打处方签控制类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui 20090526
 * @version 1.0
 */
public class OdoCaseSheetControl extends TControl {
    TParm parm;
    String rxNo = "090514000002";
    String caseNo = "090512000006";
    String presrtNo = "";//add by huangjw 20150120
    String mrNo = "000000000174";
    String deptCode = "20201";
    String icdCode = "";
    String icdDesc = "";
    Timestamp admDate;
    String rxType = "";
    String filter = "";
    boolean isFirstPrint = false;
    String patName = "";
    ODO odo;
    TTable table;
    TWord word;
    TWord word1;
    TWord word2;
    TWord word3;
    TWord word4;//外用执行单
    TWord word5;//口服执行单
    //==========================================这块只用在处理处方签那部分
    double amt1 = 0 ; 
	double amt2 = 0 ; 
	boolean dosflg=false;//皮试用法用量不显示
	boolean dosflg1=false;//皮试用法用量不显示
	boolean newpsflg=true;
	boolean newpsflg1=true;
	String PS = "皮试";// 皮试
	String PS1 = "皮试";// 皮试
	String psdesc = "皮试结果(       )批号_____________ ";// 皮试
	String psdesc1 = "皮试结果(       )批号_____________ ";// 皮试
	//==========================================这块只用在处理处方签那部分
//    private boolean printFlg=false;
//    private boolean bottleFlg=false;
    //记录保存的EMR名称
    private String EMRName = "";
    //记录CLASS_CODE
    private String classCode = "";
    private String subClassCode = "";
    private String typeChange = "1";
	private boolean  pflag=false;//判断是否已经做过皮试 caoyong
	private Map<String,Boolean> dfMap ;//用于保存已打印的底方处方签号 RX_NO
	
    public void onInit() {
        super.onInit();
        initParameter();
        if (caseNo == null || caseNo.trim().length() < 1) {
            this.messageBox("E0024");
            return;
        }
        initForm();
        
        
        if(typeChange.equals("1")){
        	this.getRadioButton("tRadioButton_0").setSelected(true);//默认选中西成药的checkBox add by huangjw 20150527
        }else if(typeChange.equals("2")){
        	this.getRadioButton("tRadioButton_1").setSelected(true);//管制药品
        }else if(typeChange.equals("3")){
        	this.getRadioButton("tRadioButton_2").setSelected(true);//中药
        }
        
        
        onTypeChange(typeChange);//初始化选中西药 add by huangjw 20150527
    }

    /**
     * 初始化参数
     */
    public void initParameter() {
        parm = (TParm)this.getParameter();
        if (parm == null)
            return;
        patName = parm.getValue("PAT_NAME");
        caseNo = parm.getValue("CASE_NO");
        mrNo = parm.getValue("MR_NO");
        deptCode = parm.getValue("DEPT_CODE");
        admDate = (Timestamp) parm.getData("ADM_DATE");
        icdCode = parm.getValue("ICD_CODE");
        icdDesc = parm.getValue("ICD_DESC");

        if(parm.getValue("TYPE").length() > 0 ){
        	typeChange = parm.getValue("TYPE");
        }
        
        isFirstPrint = false;
        odo = (ODO) parm.getData("ODO");
        OpdOrder order = (OpdOrder) parm.getData("OPD_ORDER");
        filter = order.getFilter();
        order.setFilter("");
        order.filter();
        table = (TTable)this.getComponent("TABLE_RX");
        word = (TWord)this.getComponent("WORD");
        word1 = (TWord)this.getComponent("WORD1");
        word2 = (TWord)this.getComponent("WORD2");
        word3 = (TWord)this.getComponent("WORD3");
        word4 = (TWord)this.getComponent("WORD4");
        word5 = (TWord)this.getComponent("WORD5");
        
    }

    /**
     * 初始化基础信息值
     */
    public void initForm() {
        this.setValue("NAME", patName);
        this.setValue("MR_NO", mrNo);
        this.setValue("ADM_DATE", admDate);
        this.setValue("ICD_CODE", icdCode);
        this.setValue("ICD_DESC", icdDesc);
        this.setValue("DEPT_CODE", deptCode);
        if ("en".equals(this.getLanguage()))
            this.setValue("DR_NAME", Operator.getEngName());
        else
            this.setValue("DR_NAME", Operator.getName());

    }
    /**
     * TABLE点击事件
     */
    public void onTableClick() {
    	//==========================================这块只用在处理处方签那部分
		amt1 = 0;
		amt2 = 0;
		dosflg = false;// 皮试用法用量不显示
		dosflg1 = false;// 皮试用法用量不显示
		newpsflg = true;
		newpsflg1 = true;
		PS = "皮试";// 皮试
		PS1 = "皮试";// 皮试
		psdesc = "皮试结果(       )批号_____________ ";// 皮试
		psdesc1 = "皮试结果(       )批号_____________ ";// 皮试
    	//==========================================这块只用在处理处方签那部分
    	
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        rxNo = table.getParmValue().getValue("RX_NO", row);
        presrtNo = table.getParmValue().getValue("PRESRT_NO",row);//add by huangjw 20150120
        String caseNoRow = table.getParmValue().getValue("CASE_NO", row);//yanjing 预开检查
        if(caseNoRow!=null&&!caseNoRow.equals("")){
        	caseNo=caseNoRow;
        	
        }
//        System.out.println("caseNocaseNocaseNo::"+caseNo);
        if (rxNo == null || rxNo.trim().length() < 1) {
            this.messageBox("E0034");
            return;
        }
       
        TParm inParam = new TParm();
        
        String filterString = odo.getOpdOrder().getFilter();
        odo.getOpdOrder().setFilter("RX_NO='" + rxNo + "'");
        odo.getOpdOrder().filter();
//        System.out.println("=== OpdRxSheetTool begin date 1==="+new Date()); 
        inParam = OpdRxSheetTool.getInstance().getOrderPrintParm(deptCode,
                rxType, odo, rxNo, odo.getOpdOrder().getItemString(0, "PSY_FLG"));
        
        if (inParam == null || inParam.getErrCode() != 0) {
            this.messageBox("E0116");
            return;
        }
       
        
    	TParm data2 = new TParm();
		DecimalFormat df = new DecimalFormat("############0.00");
    	double pageAmt = 0.00 ;
//        System.out.println("=== OpdRxSheetTool stop date 1==="+new Date());
        int rxTypeInt = StringTool.getInt(rxType);
        String sql =
                "SELECT A.MED_APPLY_NO,B.OWN_PRICE,A.ORDER_CODE,CASE WHEN A.BILL_FLG='Y' THEN '√' ELSE '' END AS BILL_FLG,"
        				+ " E.CHN_DESC,"//执行地点从成本中心获取modify by huangjw 20140912
                        + "      D.DEPT_CHN_DESC,B.AR_AMT,A.ORDER_DESC,A.MEDI_QTY,CASE WHEN A.URGENT_FLG='Y' THEN '√' ELSE '' END AS URGENT_FLG,"
                        + "      A.DR_NOTE, A.DISCOUNT_RATE,A.INFLUTION_RATE " 
//                        + " C.chn_desc "
                        + " FROM OPD_ORDER A,"
                        + "      (SELECT CASE_NO,RX_NO,ORDERSET_GROUP_NO, SUM(AR_AMT) AS AR_AMT,SUM(DISPENSE_QTY*OWN_PRICE) AS OWN_PRICE "
                        + "         FROM OPD_ORDER "
                        + "        WHERE CASE_NO = '#' "
                        + "          AND RX_NO IN ('@') "
                        + "          AND SETMAIN_FLG = 'N' "
                        + "     GROUP BY CASE_NO, RX_NO, ORDERSET_GROUP_NO) B, SYS_DEPT D,"//==, SYS_FEE C
                        //+ "      (SELECT ID, CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'EXAADDRESS') E "
//                        + " (SELECT ID, CHN_DESC,COST_CENTER_CODE FROM SYS_COST_CENTER A,SYS_DICTIONARY B WHERE B.GROUP_ID = 'EXAADDRESS' AND A.EXE_PLACE=B.ID) E "//add caoyong 执行地点 2014、5、30
//                        + " SYS_DICTIONARY E "//modify liling 执行地点 可直接通过sys_fee 与 sys_dictionary关联获得2014、8、07
                      //+ " (select distinct a.order_code,a.order_desc,b.chn_desc from sys_fee a, (select * from sys_dictionary where  group_id='EXAADDRESS') b where a.exe_place=b.id(+) ) C "//modify liling 执行地点
                      //执行地点从成本中心获取modify by huangjw 20140912 start
                      + " SYS_DICTIONARY E, "
                      + " SYS_COST_CENTER F "
                      //执行地点从成本中心获取modify by huangjw 20140912 end
                        + " WHERE A.CASE_NO = '#' "
                        + "  AND A.RX_NO IN ('@') "
                        + "  AND A.SETMAIN_FLG = 'Y' "
                        + "  AND A.CAT1_TYPE = 'LIS' "
                        + "  AND A.CASE_NO = B.CASE_NO "
                        + "  AND A.RX_NO = B.RX_NO "
                        + "  AND A.ORDERSET_GROUP_NO = B.ORDERSET_GROUP_NO "
                        + "  AND A.EXEC_DEPT_CODE = D.DEPT_CODE "
//                        + "  AND A.ORDER_CODE = C.ORDER_CODE "
                        + "  AND E.GROUP_ID='EXAADDRESS' AND F.EXE_PLACE=E.ID AND A.EXEC_DEPT_CODE=F.COST_CENTER_CODE "//执行地点从成本中心获取modify by huangjw 20140912
//                        + "  AND C.EXE_PLACE = E.ID(+) "//modify liling 20140807
//                        + " AND E.GROUP_ID = 'EXAADDRESS' "//modify liling 20140807
//                        + " AND A.EXEC_DEPT_CODE=E.COST_CENTER_CODE(+) " //add caoyong 执行地点 2014、5、30//==modify liling 20140807
                        + "ORDER BY A.SEQ_NO";
        sql = sql.replaceAll("#", caseNo);
        sql = sql.replaceAll("@", inParam.getValue("RX_NO"));
      // 过敏史
 		if (!"".equals(getAllerg()) && getAllerg() != null) {
 			inParam.setData("ALLERGY", "TEXT", getAllerg());

 		}

		DecimalFormat df2 = new DecimalFormat("############0.00"); 
		switch (rxTypeInt) {
        case 1: //西药        
        	 //====================根据处方类型 来显示 口服、外用、注射、输液 的checkBox start
        	 viewCheckBox1(table.getParmValue(),row);
        	//====================根据处方类型 来显示 口服、外用、注射、输液 的checkBox end
//        	 printFlg=true;//处方底方打印标记
        	 TParm newParm= new TParm();//处方非底方
        	 newParm = OpdRxSheetTool.getInstance().getOrderPrintParm(deptCode,
        			 rxType, odo, rxNo, odo.getOpdOrder().getItemString(0, "PSY_FLG"));  
             if (newParm == null || newParm.getErrCode() != 0) {
             	this.messageBox("E0116");
             	return;
             }
             if(!"".equals(getAllerg())&&getAllerg()!=null){//非底方过敏史
            	 newParm.setData("ALLERGY", "TEXT",getAllerg());
 			    }
             
        	String westsql = getWestSql(inParam);//西药处方查询
        	String westsql1 = getWestSql(inParam);//西药底方
        	if(!"".equals(presrtNo) && presrtNo!=null){
        		westsql=westsql.replaceFirst("@", " AND PRESRT_NO='"+presrtNo+"' ");//add by huangjw 20150120
        	}else{
        		westsql=westsql.replaceFirst("@", " AND PRESRT_NO IS NULL ");//add by huangjw 20150120
        	}
        	westsql1=westsql1.replaceFirst("@", "");//add by huangjw 20150120
        	TParm westResult = new TParm(TJDODBTool.getInstance().select(westsql));
        	System.out.println("22222:"+westsql);
        	System.out.println("11111:"+westResult);
    		if(westResult.getErrCode()<0){
    			this.messageBox("查询失败"); 
    			return ;
    		}
    		if(westResult.getCount()<0){
    			this.messageBox("没有处方签数据.") ;
    			return ; 
    		}
    		TParm westResult1 = new TParm(TJDODBTool.getInstance().select(westsql1));
    		TParm westParm = new TParm() ;//处方使用
    		TParm westParm1 = new TParm();//底方使用
    		
    		//String psDesc=getPs(westResult)[0];
    		getWestParm(westResult,westParm,true );
    		getWestParm(westResult1,westParm1,false);
			inParam.setData("ORDER_TABLE", westParm.getData());
			newParm.setData("ORDER_TABLE", westParm1.getData());
			inParam.setData("TOT_AMT", "TEXT", df2.format(amt1));// 药品金额
			inParam.setData("SKINTEST", "TEXT", psdesc);
			newParm.setData("TOT_AMT", "TEXT", df2.format(amt2));
			newParm.setData("SKINTEST", "TEXT",psdesc1);
			if (newpsflg) {
				inParam.setData("SKINTESTM", "TEXT", getPs(westResult)[1]);// 皮号
				inParam.setData("SKINTESTB", "TEXT", getPs(westResult)[2]);// 皮试结果
				newParm.setData("SKINTESTM", "TEXT", getPs(westResult1)[1]);
				newParm.setData("SKINTESTB", "TEXT", getPs(westResult1)[2]);
			}
			String execDept = table.getParmValue().getValue("EXEC_DEPT_CODE", row);// 执行科室
			TParm resultParm=getRxSheeDataForPre(rxNo,presrtNo);
			inParam.setData("COUNTER_NO","TEXT",resultParm.getValue("DEPT_CHN_DESC", 0));//处方药房
			inParam.setData("RXNO","TEXT","处方号："+rxNo+presrtNo);//拼接处方号modify by huangjw 20150120
//			inParam.setData("SIDE", "TEXT", "处方笺");//非底方
			inParam.setData("SIDE", "TEXT", "030708".equals(execDept)?"保健品申请单":"处方笺");//非底方
			word1.setWordParameter(inParam);
			word1.setPreview(true);
			word1.setFileName("%ROOT%\\config\\prt\\OPD\\OpdOrderSheet_V45.jhw");
			
			
			//两张处方签
//			newParm.setData("SIDE", "TEXT", "处方笺【底方】");//底方
			newParm.setData("SIDE", "TEXT", "030708".equals(execDept)?"保健品申请单【底方】":"处方笺【底方】");//底方
			word.setWordParameter(newParm);
			word.setPreview(true);
			word.setFileName("%ROOT%\\config\\prt\\OPD\\OpdOrderSheet_V45.jhw");
	            

			
			//输液或注射执行单
			TParm bottParm=ODOTool.getInstance().getNewBottleLabel(this.caseNo, rxNo,presrtNo);//查询sql
			TParm newbottParm_I=new TParm();//注射
			TParm newbottParm_E=new TParm();//外用
			TParm newbottParm=new TParm();//输液
			TParm newbottParm_O=new TParm();//口服
			String newName="";
			String name="";   
			if(bottParm.getCount()>0){
				
				for(int k=0;k<bottParm.getCount();k++){
					if("I".equals(bottParm.getValue("CLASSIFY_TYPE", k))){
						newbottParm_I.addRowData(bottParm, k);
					}else if("E".equals(bottParm.getValue("CLASSIFY_TYPE", k))){
						newbottParm_E.addRowData(bottParm, k);
					}else if("F".equals(bottParm.getValue("CLASSIFY_TYPE", k))){
						newbottParm.addRowData(bottParm, k);
					}if("O".equals(bottParm.getValue("CLASSIFY_TYPE", k))){
						newbottParm_O.addRowData(bottParm, k);
					}
					
				}
				if(newbottParm_I.getCount()>0){
					String psno="";
					String psresult="";
					if(pflag){
						psno="         批号："+getPs(westResult)[1];
						psresult=getPs(westResult)[2];
					}
					name="门（急）诊药物治疗执行单";
					this.getPrintBottleLabel_I(rxNo, inParam,newbottParm_I,name,psno,psresult);//注射执行单
				}
				if(newbottParm.getCount()>0){
					newName="门（急）诊药物治疗执行单";
					this.getPrintBottleLabel(rxNo, inParam,newbottParm,newName);//输液执行单
				}
				if(newbottParm_E.getCount()>0){
					newName="门（急）诊药物治疗执行单";
					this.getPrintBottleLabel_E(rxNo, inParam,newbottParm_E,newName);//外用执行单
				}if(newbottParm_O.getCount()>0){
					newName="门（急）诊药物治疗执行单";
					this.getPrintBottleLabel_O(rxNo, inParam,newbottParm_O,newName);//口服执行单
				}
			}
        	//=============modify by lim end 
			
            EMRName = "西药处方签_" + rxNo;
            this.classCode = "EMR030001";
            this.subClassCode = "EMR03000101";
//            System.out.println("=== 显示 end date 2==="+new Date());
            break;
        case 2:
//        	TParm Parm_1=new TParm();
//        	TParm Parm_2=new TParm();
        	//add caoy 管制药品
        	//this.getDrugParm(inParam, df2);
        	
        	 TParm Parm_1=new TParm();//管制药品非底方
         	TParm Parm_2=new TParm();//管制药品底方
             Parm_1=OpdRxSheetTool.getInstance().getOrderPrintParm(deptCode,
                     rxType, odo, rxNo, odo.getOpdOrder().getItemString(0, "PSY_FLG"));
             Parm_2=OpdRxSheetTool.getInstance().getOrderPrintParm(deptCode,
                     rxType, odo, rxNo, odo.getOpdOrder().getItemString(0, "PSY_FLG"));

             if (Parm_1 == null || Parm_1.getErrCode() != 0) {
                 this.messageBox("E0116");
                 return;
             }
             if (Parm_2 == null || Parm_2.getErrCode() != 0) {
                 this.messageBox("E0116");
                 return;
             }
     		// 过敏史
     		if (!"".equals(getAllerg()) && getAllerg() != null) {
     			Parm_1.setData("ALLERGY", "TEXT", getAllerg());
     			Parm_2.setData("ALLERGY", "TEXT", getAllerg());
     		}

        	this.getDrugParm(Parm_1, df2);
        	this.getDrugParm(Parm_2, df2);
        		
        	Parm_1.setData("SIDE", "TEXT", "处方笺");//非底方
			word1.setWordParameter(Parm_1);
			word1.setPreview(true);
			word1.setFileName("%ROOT%\\config\\prt\\OPD\\OpdDrugSheet_V45.jhw");
			
			Parm_2.setData("SIDE", "TEXT", "处方笺【底方】");//底方
			word.setWordParameter(Parm_2);
			word.setPreview(true);
			word.setFileName("%ROOT%\\config\\prt\\OPD\\OpdDrugSheet_V45.jhw");
		    
            EMRName = "管制药品处方签_" + rxNo;
            this.classCode = "EMR030001";
            this.subClassCode = "EMR030001";
            break;
        case 3:
//        	printFlg=false;
//        	bottleFlg=false;
            word1.setWordParameter(inParam);
            word1.setPreview(true);
            word1.setFileName("%ROOT%\\config\\prt\\OPD\\OpdChnOrderSheet.jhw");
            
            TParm dealParm = OpdRxSheetTool.getInstance().getOrderPrintParm(deptCode,
                    rxType, odo, rxNo, odo.getOpdOrder().getItemString(0, "PSY_FLG"));
            
            dealParm.setData("TITLE","TEXT","中药处方签【底方】 ");
            word.setWordParameter(dealParm);
			word.setPreview(true);
			word.setFileName("%ROOT%\\config\\prt\\OPD\\OpdChnOrderSheet.jhw");
			
            EMRName = "中药处方签_" + rxNo;
            this.classCode = "EMR030002";
            this.subClassCode = "EMR03000201";
            break;
        case 4:
//        	printFlg=false;
//        	bottleFlg=false;
            //modify by liming 2012/02/23 begin
//        	this.messageBox("处置");
    		TParm dataParm = new TParm() ;
    		if( inParam.getValue("RX_NO") != null &&  inParam.getValue("RX_NO").trim().length() > 0){
    		      String sql1 = // modify by wanglong 20140402
                          "SELECT A.ORDER_CODE,A.DOSAGE_QTY,B.OWN_PRICE,CASE WHEN A.BILL_FLG='Y' THEN '√' ELSE '' END AS BILL_FLG,D.DEPT_CHN_DESC, "
                                  + "  A.ORDER_DESC||CASE WHEN A.SPECIFICATION IS NOT NULL THEN '/'||A.SPECIFICATION ELSE '' END AS ORDER_DESC,"
                                  + "  A.MEDI_QTY,CASE WHEN A.URGENT_FLG='Y' THEN '√' ELSE '' END AS URGENT_FLG,C.DESCRIPTION,"
                                  + "  CASE WHEN A.DISCOUNT_RATE=0 THEN 1 ELSE A.DISCOUNT_RATE END AS DISCOUNT_RATE,B.AR_AMT "
                                  + "  FROM OPD_ORDER A, ( "
                                  + "  SELECT MIN(A.SEQ_NO) AS SEQ_NO,A.CASE_NO,A.RX_NO,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,SUM(A.OWN_PRICE) OWN_PRICE,SUM(A.AR_AMT) AR_AMT "
                                  + "  FROM OPD_ORDER A "
                                  + "  WHERE A.CASE_NO = '@' "
                                  + "  AND A.RX_NO IN ('#') "
                                  + "  AND A.ORDERSET_CODE IS NOT NULL "
                                  + "  GROUP BY A.CASE_NO, A.RX_NO, A.ORDERSET_GROUP_NO, A.ORDERSET_CODE "
                                  + "  UNION "
                                  + "  SELECT A.SEQ_NO, A.CASE_NO, A.RX_NO, A.ORDERSET_GROUP_NO, A.ORDER_CODE AS ORDERSET_CODE, A.OWN_PRICE, A.AR_AMT "
                                  + "  FROM OPD_ORDER A "
                                  + "  WHERE A.CASE_NO = '@' "
                                  + "  AND A.RX_NO IN ('#') "//modify caoyong (#) 改为('#')
                                  + "  AND A.ORDERSET_CODE IS NULL) B,SYS_FEE C,SYS_DEPT D "
                                  + "  WHERE A.CASE_NO = B.CASE_NO "
                                  + "  AND A.RX_NO = B.RX_NO "
                                  + "  AND A.SEQ_NO = B.SEQ_NO "
                                  + "  AND A.ORDER_CODE = B.ORDERSET_CODE "
                                  + "  AND A.ORDER_CODE = C.ORDER_CODE "
                                  + "  AND A.EXEC_DEPT_CODE = D.DEPT_CODE "
                                  + "  ORDER BY A.SEQ_NO";
                    
                    sql1 = sql1.replaceAll("@", this.caseNo);
                    sql1 = sql1.replaceAll("#", inParam.getValue("RX_NO"));
                    dataParm = new TParm(TJDODBTool.getInstance().select(sql1));
    		}
    		if(dataParm.getErrCode()<0){
    			this.messageBox("E0001"); 
    			return ;
    		}
    		if(dataParm.getCount()<0){
    			this.messageBox("没有处置通知单数据.") ;
    			return ; 
    		}

    		TParm myParm = new TParm() ;
    		boolean flg1 = false ;
    		int blankRow1 = 0 ;
    		double pageAmt1 = 0 ; 
    		DecimalFormat df1 = new DecimalFormat("############0.00"); 

    		for (int i = 0; i < dataParm.getCount(); i++) {
    			String orderDesc = dataParm.getValue("ORDER_DESC", i) ;
    			/*if(orderDesc.length()<=29){
    				StringBuilder temp = new StringBuilder() ;
    				for (int j = 1; j <= 58 - orderDesc.length(); j++) {
						temp.append(" ") ;
					}
    				orderDesc = dataParm.getValue("ORDER_DESC", i)+temp.toString() ; 
    			} */
    			myParm.addData("BILL_FLG", dataParm.getData("BILL_FLG", i)) ;
    			myParm.addData("DEPT_CHN_DESC", dataParm.getData("DEPT_CHN_DESC", i)) ;        	
    			myParm.addData("ORDER_DESC", orderDesc) ;
//    			myParm.addData("MEDI_QTY", dataParm.getData("MEDI_QTY", i)) ;
    			myParm.addData("MEDI_QTY", dataParm.getData("DOSAGE_QTY", i));// 从MEDI_QTY改为DOSAGE_QTY liling 20140825 modify
    			myParm.addData("URGENT_FLG", dataParm.getData("URGENT_FLG", i)) ;
    			myParm.addData("DESCRIPTION", dataParm.getData("DESCRIPTION", i)) ; 
            	//累计
    			pageAmt1 += dataParm.getDouble("AR_AMT", i);//modify by wanglong 20140415
				pageAmt1 = StringTool.round(pageAmt1, 2);//add by wanglong 20121226
            	//TODO:“###########”处需要被每页计算的金额替代.用获得的数量*getEveryAmt(ORDERCODE)计算出每条记录的金额。
            	int num = i+blankRow1+1 ;//行数（i）+ 空白行(blankRow)+1
            	if(!flg1){//第一页
            		if(i == 8 || i == (dataParm.getCount() - 1)){
            			myParm.addData("BILL_FLG", "") ;
            			myParm.addData("DEPT_CHN_DESC", "") ;
            			myParm.addData("ORDER_DESC", "") ;
            			myParm.addData("MEDI_QTY", "") ;
            			myParm.addData("URGENT_FLG","处方金额:") ;
            			myParm.addData("DESCRIPTION",df1.format(pageAmt1)) ;
        	        	flg1 = true ;
        	        	blankRow1 ++ ;
        	        	pageAmt1 = 0 ;
            		}

            	}else{//其他页.//第5行显示金额
            		if(i == dataParm.getCount() - 1 ){
            			myParm.addData("BILL_FLG", "") ;
            			myParm.addData("DEPT_CHN_DESC", "") ;
            			myParm.addData("ORDER_DESC", "") ;
            			myParm.addData("MEDI_QTY", "") ;
            			myParm.addData("URGENT_FLG","处方金额:") ;
            			myParm.addData("DESCRIPTION",df1.format(pageAmt1)) ;
        	        	blankRow1 ++ ;
        	        	pageAmt1 = 0 ;
            		}else if(i != dataParm.getCount() - 1 && ((num % 10) + 1) == 10){
            			myParm.addData("BILL_FLG", "") ;
            			myParm.addData("DEPT_CHN_DESC", "") ;
            			myParm.addData("ORDER_DESC", "") ;
            			myParm.addData("MEDI_QTY", "") ;
            			myParm.addData("URGENT_FLG","处方金额:") ;
            			myParm.addData("DESCRIPTION",df1.format(pageAmt1)) ;
        	        	blankRow1 ++ ;
        	        	pageAmt1 = 0 ;
            		}
            	}    			
    		}
    		myParm.setCount(myParm.getCount("ORDER_DESC")) ;
    		myParm.addData("SYSTEM", "COLUMNS", "BILL_FLG"); 
    		myParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");        
    		myParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
    		myParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
    		myParm.addData("SYSTEM", "COLUMNS", "URGENT_FLG");
    		myParm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");	
    		inParam.setData("ORDER_TABLE", myParm.getData()) ; 	
    		//modify by liming 2012/02/23 end         	
            word1.setWordParameter(IReportTool.getInstance().getReportParm("OpdNewHandleSheet_V45.class",inParam));
            word1.setPreview(true);
            word1.setFileName(IReportTool.getInstance().getReportPath("OpdNewHandleSheet_V45.jhw"));
            EMRName = "处置通知单_" + inParam.getValue("RX_NO");
            this.classCode = "EMR040002";
            this.subClassCode = "EMR04000203";
            break;
        case 5:
//        	this.messageBox("检验");
//        	printFlg=false;
//        	bottleFlg=false;
    		//modify by wanglong 20121226
            //====== liling 20140328	备注修改，由原来的获得执行地点改成医生备注
            String sql12=sql.replace("C.DESCRIPTION","A.DR_NOTE");
            //System.out.println("sql12::"+sql12);
    		TParm result = new TParm(TJDODBTool.getInstance().select(sql12));
//    		System.out.println("sql==="+sql);
//    		System.out.println("sql12==="+sql12);
//    		System.out.println("===result==="+result);
    		if(result.getErrCode()<0){
    			this.messageBox(result.getErrText());
    			return ;
    		}
    		if(result.getCount()<0 ){
    			return ;
    		}
    		
    		boolean flg = false ;
    		int blankRow = 0 ;
    		if(result.getCount() > 0){
    	        int pageCount = 11 ;
    	    	data2.addData("BILL_FLG", "") ;
    	    	data2.addData("DEPT_CHN_DESC","检验通知单") ;
    	    	data2.addData("ORDER_DESC", "") ;
    	    	data2.addData("MEDI_QTY", "") ;
    	    	data2.addData("URGENT_FLG", "") ;
    	    	data2.addData("DESCRIPTION", "") ;
    	    	data2.addData("MED_APPLY_NO", "") ;
	        	//======pangben 2014-3-20
    	    	//======liling 20140328去掉时限列值显示
	        	//data2.addData("TIME_LIMIT", "") ;//===时限
	        	data2.addData("CHN_DESC", "") ;//执行地点
    	        for (int i = 0; i < result.getCount(); i++) {
    	        	data2.addData("BILL_FLG", result.getData("BILL_FLG", i)) ;
    	        	data2.addData("DEPT_CHN_DESC", result.getData("DEPT_CHN_DESC", i)) ;
    	        	data2.addData("ORDER_DESC", result.getData("ORDER_DESC", i)) ;
    	        	DecimalFormat df3 = new DecimalFormat("############0.000"); 
    	        	double own_price = StringTool.round(result.getDouble("OWN_PRICE", i), 3);  
    	        	String mediQty = result.getValue("MEDI_QTY", i).substring(0, result.getValue("MEDI_QTY", i).length()-2);
    				String ownPrice = df3.format(own_price);
    	        	data2.addData("MEDI_QTY", StringTool.fillLeft(mediQty, 4-mediQty.length(), " ")+StringTool.fill(" ", 14-ownPrice.length())+ownPrice) ;
    	        	data2.addData("URGENT_FLG", result.getData("URGENT_FLG", i)) ;
    	       	    //======liling 20140328	备注修改，由原来的获得执行地点改成医生备注
//    	        	data2.addData("DESCRIPTION", result.getData("DESCRIPTION", i)) ;
    	        	data2.addData("DESCRIPTION", result.getData("DR_NOTE", i)) ;
    	        	data2.addData("MED_APPLY_NO", result.getData("MED_APPLY_NO", i)) ;
    	        	//======pangben 2014-3-20
    	        	//======liling 20140328去掉时限列值显示
    	        	//data2.addData("TIME_LIMIT", result.getData("TIME_LIMIT", i)) ;//===时限
    	        	data2.addData("CHN_DESC", result.getData("CHN_DESC", i)) ;//执行地点
    	        	//累计
//					pageAmt += (result.getDouble("MEDI_QTY", i)
//							* this.getEveryAmt(result.getValue("ORDER_CODE",i))
//							* result.getDouble("DISCOUNT_RATE", i));//modify by wanglong 20121226
					pageAmt += StringTool.round(result.getDouble("AR_AMT", i), 2);//add by wanglong 20121226
    	        	//TODO:“###########”处需要被每页计算的金额替代.用获得的数量*getEveryAmt(ORDERCODE)计算出每条记录的金额。
    	        	int num = i+blankRow+1+1 ;//行数（i）+ 空白行(blankRow)+第一行检验通知单(1)+1
    	        	if(!flg){//第一页
    	        		if(i == 8 || i == (result.getCount() - 1)){
    	    	        	data2.addData("BILL_FLG", "") ;
    	    	        	data2.addData("DEPT_CHN_DESC", "") ;
    	    	        	data2.addData("ORDER_DESC", "") ;
    	    	        	data2.addData("MEDI_QTY", "") ;
    	    	        	data2.addData("URGENT_FLG","") ;
    	    	        	data2.addData("DESCRIPTION","处方金额:") ;
    	    	        	data2.addData("MED_APPLY_NO", df.format(pageAmt)) ;
    	    	        	//======pangben 2014-3-20
    	    	        	//======liling 20140328去掉时限列值显示
    	    	        	//data2.addData("TIME_LIMIT", "") ;//===时限
    	    	        	data2.addData("CHN_DESC", "") ;//执行地点
    	    	        	flg = true ;
    	    	        	blankRow ++ ;
    	    	        	pageAmt = 0 ;
    	        		}

    	        	}else{//其他页.//第11行显示金额
    	        		if(i == result.getCount() - 1 ){
    	    	        	data2.addData("BILL_FLG", "") ;
    	    	        	data2.addData("DEPT_CHN_DESC", "") ;
    	    	        	data2.addData("ORDER_DESC", "") ;
    	    	        	data2.addData("MEDI_QTY", "") ;
    	    	        	data2.addData("URGENT_FLG","") ;
    	    	        	data2.addData("DESCRIPTION","处方金额:") ;
    	    	        	data2.addData("MED_APPLY_NO", df.format(pageAmt)) ;
    	    	        	//======pangben 2014-3-20
    	    	        	//======liling 20140328去掉时限列值显示
    	    	        	//data2.addData("TIME_LIMIT", "") ;//===时限
    	    	        	data2.addData("CHN_DESC", "") ;//执行地点
    	    	        	blankRow ++ ;
    	    	        	pageAmt = 0 ;
    	        		}else if(i != result.getCount() - 1 && ((num % 11) + 1) == 11){
    	    	        	data2.addData("BILL_FLG", "") ;
    	    	        	data2.addData("DEPT_CHN_DESC", "") ;
    	    	        	data2.addData("ORDER_DESC", "") ;
    	    	        	data2.addData("MEDI_QTY", "") ;
    	    	        	data2.addData("URGENT_FLG","") ;
    	    	        	data2.addData("DESCRIPTION","处方金额:") ;
    	    	        	data2.addData("MED_APPLY_NO", df.format(pageAmt)) ;
    	    	        	//======pangben 2014-3-20
    	    	        	//======liling 20140328去掉时限列值显示
    	    	        	//data2.addData("TIME_LIMIT", "") ;//===时限
    	    	        	data2.addData("CHN_DESC", "") ;//执行地点
    	    	        	blankRow ++ ;
    	    	        	pageAmt = 0 ;
    	        		}
    	        	}
    			}
    	        int resultLen1 = result.getCount()+1+blankRow ;
    	        int len = (resultLen1<=pageCount)? (pageCount - resultLen1):((resultLen1%pageCount == 0) ? 0 : (((resultLen1/pageCount)+1)*pageCount-resultLen1)) ;

    	        for (int i = 1; i <=len; i++) {
    	        	data2.addData("BILL_FLG", "") ;
    	        	data2.addData("DEPT_CHN_DESC", "") ;
    	        	data2.addData("ORDER_DESC", "") ;
    	        	data2.addData("MEDI_QTY", "") ;
    	        	data2.addData("URGENT_FLG", "") ;
    	        	data2.addData("DESCRIPTION", "") ;
    	        	data2.addData("MED_APPLY_NO", "") ;
    	        	//======pangben 2014-3-20
    	        	//======liling 20140328去掉时限列值显示
    	        	//data2.addData("TIME_LIMIT", "") ;//===时限
    	        	data2.addData("CHN_DESC", "") ;//执行地点
    			}
    		}
            
            data2.setCount(data2.getCount("ORDER_DESC")) ;
            data2.addData("SYSTEM", "COLUMNS", "BILL_FLG");
            data2.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
            data2.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            data2.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
            data2.addData("SYSTEM", "COLUMNS", "URGENT_FLG");
            data2.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
            data2.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");
            //======liling 20140328去掉时限列值显示
            // data2.addData("SYSTEM", "COLUMNS", "TIME_LIMIT");
            data2.addData("SYSTEM", "COLUMNS", "CHN_DESC");
            inParam.setData("ORDER_TABLE", data2.getData()) ;
            //modify by lim 2012/02/23 begin
//            System.out.println("==inParam==="+inParam);
            word1.setWordParameter(inParam);
            word1.setPreview(true);
            word1.setFileName("%ROOT%\\config\\prt\\OPD\\OpdNewExaSheet.jhw");
            EMRName = "检验检查通知单_" + inParam.getValue("RX_NO");
            this.classCode = "EMR040001";
            this.subClassCode = "EMR04000141";
            break;
        case 6:
        	//modify by pangben 20130417
//        	printFlg=false;
//        	bottleFlg=false;
//        	System.out.println("sql:::::::::::::"+sql);
//        	this.messageBox("检查");
        	String sql2 = sql.replace("LIS", "RIS");
        	sql2=sql2.replace("C.DESCRIPTION","A.DR_NOTE");//modify caoyong 2014/4/9
//          	System.out.println("sql2====6666=====>"+sql2);
    		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
    		if(result2.getErrCode()<0){
    			return ;
    		}
    		if(result2.getCount()<0 ){
    			return ;
    		}
    		 if(result2.getCount()>0){
             	data2.addData("BILL_FLG", "") ;
             	data2.addData("DEPT_CHN_DESC","检查通知单") ;
             	data2.addData("ORDER_DESC", "") ;
             	data2.addData("MEDI_QTY", "") ;
             	data2.addData("URGENT_FLG", "") ;
             	data2.addData("DESCRIPTION", "") ;
             	data2.addData("MED_APPLY_NO", "") ;
	        	//======pangben 2014-3-20
             	//======liling 20140328去掉时限列值显示
	        	//data2.addData("TIME_LIMIT", "") ;//===时限
	        	data2.addData("CHN_DESC", "") ;//执行地点
             }
         	blankRow = 0 ;
         	flg = false ;
             for(int i = 0; i < result2.getCount(); i++){
             	data2.addData("BILL_FLG", result2.getData("BILL_FLG", i)) ;
             	data2.addData("DEPT_CHN_DESC", result2.getData("DEPT_CHN_DESC", i)) ;
             	data2.addData("ORDER_DESC", result2.getData("ORDER_DESC", i)) ;
             	DecimalFormat df3 = new DecimalFormat("############0.000"); 
	        	double own_price = StringTool.round(result2.getDouble("OWN_PRICE", i), 3);
	        	
	        	String mediQty = result2.getValue("MEDI_QTY", i).substring(0, result2.getValue("MEDI_QTY", i).length()-2); //add by huangtt 20131218
				String ownPrice = df3.format(own_price); //add by huangtt 20131218
             	data2.addData("MEDI_QTY", StringTool.fillLeft(mediQty, 4-mediQty.length(), " ")+StringTool.fill(" ", 14-ownPrice.length())+ownPrice) ; //add by huangtt 20131218
             	data2.addData("URGENT_FLG", result2.getData("URGENT_FLG", i)) ;
             	//======liling 20140328	备注修改，由原来的获得执行地点改成医生备注
//	        	data2.addData("DESCRIPTION", result.getData("DESCRIPTION", i)) ;
	        	data2.addData("DESCRIPTION", result2.getData("DR_NOTE", i)) ;
             	data2.addData("MED_APPLY_NO", result2.getData("MED_APPLY_NO", i)) ;
             	//======pangben 2014-3-20
             	//======liling 20140328去掉时限列值显示
	        	//data2.addData("TIME_LIMIT", result2.getData("TIME_LIMIT", i)) ;//===时限
	        	data2.addData("CHN_DESC", result2.getData("CHN_DESC", i)) ;//执行地点
// 				pageAmt += (result2.getDouble("MEDI_QTY", i)
// 						* this.getEveryAmt(result2.getValue("ORDER_CODE", i)) 
// 						* result2.getDouble("DISCOUNT_RATE", i));//modify by wanglong 20121226
 				pageAmt += StringTool.round(result2.getDouble("AR_AMT", i), 2);//add by wanglong 20121226
             	int num = i+blankRow+1+1 ;//行数（i）+ 空白行(blankRow)+第一行检验通知单(1)+1
             	if(!flg){//第一页
             		if(i == 6 || i == (result2.getCount() - 1)){
         	        	data2.addData("BILL_FLG", "") ;
         	        	data2.addData("DEPT_CHN_DESC", "") ;
         	        	data2.addData("ORDER_DESC", "") ;
         	        	data2.addData("MEDI_QTY", "") ;
         	        	data2.addData("URGENT_FLG","") ;
         	        	data2.addData("DESCRIPTION","处方金额:") ;
         	        	data2.addData("MED_APPLY_NO", df.format(pageAmt)) ;
        	        	//======pangben 2014-3-20
         	        	//======liling 20140328去掉时限列值显示
        	        	//data2.addData("TIME_LIMIT", "") ;//===时限
        	        	data2.addData("CHN_DESC", "") ;//执行地点
         	        	flg = true ;
         	        	blankRow ++ ;
         	        	pageAmt = 0 ;
             		}

             	}else{//其他页.//第9行显示金额
             		if(i == result2.getCount() - 1 ){
         	        	data2.addData("BILL_FLG", "") ;
         	        	data2.addData("DEPT_CHN_DESC", "") ;
         	        	data2.addData("ORDER_DESC", "") ;
         	        	data2.addData("MEDI_QTY", "") ;
         	        	data2.addData("URGENT_FLG","") ;
         	        	data2.addData("DESCRIPTION","处方金额:") ;
         	        	data2.addData("MED_APPLY_NO", df.format(pageAmt)) ;
        	        	//======pangben 2014-3-20
         	        	//======liling 20140328去掉时限列值显示
        	        	//data2.addData("TIME_LIMIT", "") ;//===时限
        	        	data2.addData("CHN_DESC", "") ;//执行地点
         	        	blankRow ++ ;
         	        	pageAmt = 0 ;
             		}else if(i != result2.getCount() - 1 && ((num % 9) + 1) == 9){
         	        	data2.addData("BILL_FLG", "") ;
         	        	data2.addData("DEPT_CHN_DESC", "") ;
         	        	data2.addData("ORDER_DESC", "") ;
         	        	data2.addData("MEDI_QTY", "") ;
         	        	data2.addData("URGENT_FLG","") ;
         	        	data2.addData("DESCRIPTION","处方金额:") ;
         	        	data2.addData("MED_APPLY_NO", df.format(pageAmt)) ;
        	        	//======pangben 2014-3-20
         	        	//======liling 20140328去掉时限列值显示
        	        	//data2.addData("TIME_LIMIT", "") ;//===时限
        	        	data2.addData("CHN_DESC", "") ;//执行地点
         	        	blankRow ++ ;
         	        	pageAmt = 0 ;
             		}
             	}
             }
             data2.setCount(data2.getCount("ORDER_DESC")) ;
             data2.addData("SYSTEM", "COLUMNS", "BILL_FLG");
             data2.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
             data2.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
             data2.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
             data2.addData("SYSTEM", "COLUMNS", "URGENT_FLG");
             data2.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
             data2.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");
             //======liling 20140328去掉时限列值显示
             // data2.addData("SYSTEM", "COLUMNS", "TIME_LIMIT");
             data2.addData("SYSTEM", "COLUMNS", "CHN_DESC");
             inParam.setData("ORDER_TABLE", data2.getData()) ;
             //modify by lim 2012/02/23 begin
             word1.setWordParameter(inParam);
             word1.setPreview(true);
             word1.setFileName("%ROOT%\\config\\prt\\OPD\\OpdNewExaSheet.jhw");
             EMRName = "检验检查通知单_" + inParam.getValue("RX_NO");
             this.classCode = "EMR040001";
             this.subClassCode = "EMR04000141";
             break;
        }
        odo.getOpdOrder().setFilter(filterString);
        odo.getOpdOrder().filter();
    }
//	
//	private double getEveryAmt(String opdOrderCode){
//		String sql = "SELECT SUM(O.DOSAGE_QTY * F.OWN_PRICE) AS AMT FROM SYS_ORDERSETDETAIL O,SYS_FEE F WHERE O.ORDER_CODE = F.ORDER_CODE AND O.ORDERSET_CODE = '"+opdOrderCode+"'" ;
//		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql));
//		if(result2.getErrCode() < 0){
//			messageBox("单页处方金额计算错误.") ;
//			return 0 ;
//		}
//		if(result2.getCount()<=0){
//			return 0 ;
//		}
//		return result2.getDouble("AMT", 0);
//	}
//	
    public void getWestParm(TParm westResult,TParm westParm,boolean flg){
    	for (int i = 0; i < westResult.getCount(); i++) {
			if (PS.equals(westResult.getData("DD", i))) {// 是否是皮试用药
				if(flg){//处方
					psdesc="";
					newpsflg=false;
					dosflg=true;
				}else{//底方
					psdesc1="";
					newpsflg1=false;
					dosflg1=true;
				}
			}else{
				if(flg){//处方
					dosflg=false;
				}else{//底方
					dosflg1=false;
				}
			}

			westParm.addData("AA", "");
			westParm.addData("BB", westResult.getData("BB", i)+"  "+westResult.getData("ER", i));
			westParm.addData("CC", "");
//			if(!"".equals(psDesc)){//皮试用法不显示发药量
//				westParm.addData("CC", "");
//			}else{
				//westParm.addData("CC", westResult.getData("ER", i));
//			}
			westParm.addData("AA", "");
			if(flg){//处方
				if(dosflg){
					westParm.addData("BB","                   "+westResult.getData("FF", i) + "  "
							+ westResult.getData("DD", i)+"  "+westResult.getData("DR", i) + "天数:"+westResult.getData("TAKE_DAYS", i)+"天");
				}else{
					westParm.addData("BB", "    " + "用法：每次"
							+ westResult.getData("HH", i)+"  "+westResult.getData("FF", i) + "  "
							+ westResult.getData("DD", i)+"  "+westResult.getData("DR", i)
							+ "  "+westResult.getData("GG", i) + "天数:"+westResult.getData("TAKE_DAYS", i)+"天"+"  "+"应付金额:"+westResult.getData("AR", i));//huangjw 添加“自备药”标识 20160530
					//去掉医师备注 modify by huangjw 20141222//加上医师备注 modify by huangjw 20150320
					/*westParm.addData("BB", "    " + "用法：每次"
							+ westResult.getData("HH", i)+"  "+westResult.getData("FF", i) + "  "+ westResult.getData("DD", i));*/
				}
			}else{//底方
				if(dosflg1){
					westParm.addData("BB","                   "+westResult.getData("FF", i) + "  "
							+ westResult.getData("DD", i)+"  "+westResult.getData("DR", i));
				}else{
					westParm.addData("BB", "    " + "用法：每次"
							+ westResult.getData("HH", i)+"  "+westResult.getData("FF", i) + "  "
							+ westResult.getData("DD", i)+"  "+westResult.getData("DR", i)
							+ "  "+westResult.getData("GG", i) + "天数:"+westResult.getData("TAKE_DAYS", i)+"天"+"  "+"应付金额:"+westResult.getData("AR", i));//huangjw 添加“自备药”标识 20160530
					//去掉医师备注 modify by huangjw 20141222//加上医师备注 modify by huangjw 20150320
					/*westParm.addData("BB", "    " + "用法：每次"
							+ westResult.getData("HH", i)+"  "+westResult.getData("FF", i) + "  "+ westResult.getData("DD", i));*/
				}
			}
			westParm.addData("CC", "");
//			westParm.addData("CC",westResult.getData("FF", i) + "  "
//					+ westResult.getData("DD", i) );
			if(!"Y".equals(westResult.getValue("RELEASE_FLG",i))){//自备药品金额不统计
				if(flg){
					amt1 += (StringTool.round(westResult.getDouble("AR", i), 2));
				}else{
					amt2 += (StringTool.round(westResult.getDouble("AR", i), 2));
				}
			}
			
		}
		westParm.setCount(westParm.getCount("AA"));
		westParm.addData("SYSTEM", "COLUMNS", "AA");
		westParm.addData("SYSTEM", "COLUMNS", "BB");
		westParm.addData("SYSTEM", "COLUMNS", "CC");
    }
	
    /**
     * 获取药房
     * @param rxNo
     * @param presrtNo
     * @return
     */
    private TParm getRxSheeDataForPre(String rxNo,String presrtNo){
    	String sql="SELECT b.DEPT_CHN_DESC,C.CTZ_DESC, D.PAT_NAME, D.IDNO, I.CHN_DESC SEX_CODE,";
               sql+="TO_CHAR(D.BIRTH_DATE,'YYYY/MM/DD') BIRTH_DATE, E.DEPT_ABS_DESC, G.CLINICROOM_DESC,";
               sql+="TO_CHAR(F.REG_DATE,'YYYY/MM/DD') REG_DATE, F.REALDR_CODE,F.ADM_DATE,F.WEIGHT,M.CHN_DESC AS EXE_PLACE  ";
               sql+=" FROM OPD_ORDER A,";
               sql+="SYS_DEPT B,";
               sql+="SYS_CTZ C,";
               sql+="SYS_PATINFO D,";
               sql+="SYS_DEPT E,";
               sql+="REG_PATADM F,";
               sql+=" REG_CLINICROOM G,";
               sql+=" (SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX') I, ";
               sql+=" (SELECT ID, CHN_DESC,COST_CENTER_CODE FROM SYS_COST_CENTER A,SYS_DICTIONARY B WHERE B.GROUP_ID = 'EXAADDRESS' AND A.EXE_PLACE=B.ID) M " ;//add caoy 添加执行地点 2014、6、11
               sql+=" WHERE  A.RX_NO = '"+rxNo+"'";
               sql+=" @";
               sql+=" AND A.EXEC_DEPT_CODE = B.DEPT_CODE";
               sql+=" AND A.CTZ1_CODE = C.CTZ_CODE";
               sql+=" AND A.MR_NO = D.MR_NO";
               sql+=" AND F.DEPT_CODE = E.DEPT_CODE(+)";
               sql+=" AND F.CASE_NO = A.CASE_NO";
               sql+=" AND F.CLINICROOM_NO = G.CLINICROOM_NO(+) ";
               sql+=" AND D.SEX_CODE=I.ID";
               sql+=" AND A.EXEC_DEPT_CODE=M.COST_CENTER_CODE(+) ";
        if(!"".equals(presrtNo)){
        	sql=sql.replaceAll("@", " AND A.PRESRT_NO='"+presrtNo+"'");
        }else{
        	sql=sql.replaceAll("@", " AND A.PRESRT_NO IS NULL");
        }
        sql+=" ORDER BY A.RX_NO,A.SEQ_NO";
//               System.out.println("欲开检查  sql is ：："+sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if (result.getErrCode() != 0) {
		
            System.out.println("err:" + result.getErrText());
            return null;
        }
    	String realDrCode = "";
    	for (int i = 0; i < result.getCount(); i++) {
    		realDrCode = result.getValue("REALDR_COD", i);
			if(realDrCode.length() > 0 ){
				break;
			}
		}
    	if(realDrCode.length()>0){
    		sql = 
        		" SELECT USER_ID, USER_NAME" +
        		" FROM SYS_OPERATOR" +
        		" WHERE USER_ID = '" + realDrCode + "'";
    		
    		TParm opParm = new TParm(TJDODBTool.getInstance().select(sql));
    		String userName = opParm.getValue("USER_NAME", 0);
    		for (int i = 0; i < result.getCount(); i++) {
				result.setData("USER_NAME", i, userName);
				result.setData("FOOT_DR", i, userName);
			}
    	}
        return result;
    }
	

    /**
     * 处方类型点选事件
     * @param rxType
     */
    public void onTypeChange(String rxType) {
    	this.setValue("CHECK_ALL", "Y");
    	table.acceptText();
    	viewCheckBox(false,false);
        TParm tableParm = getParmRxType(rxType);
        if (tableParm == null || tableParm.getErrCode() != 0) {
            this.messageBox("E0116");
            return;
        }
        int count = tableParm.getCount("RX_NO");
        //==liling add start   20140721====
        int index =Integer.parseInt(rxType);
        if(index==1){//西药
        	viewCheckBox(true,false);//显示处方、底方这两个Checkbox
        	this.getCheckBox("tCheckBox_0").setSelected(true);//默认勾选处方
        	this.getCheckBox("tCheckBox_1").setSelected(true);//默认勾选底方
        	String doseType="";
        	String rxNo="";
        	String rxNo1="";
        	String presrtNo="";
        	String presrtNo1="";
        	int n=-1;
        	String sql="SELECT A.RX_NO,A.PRESRT_NO,A.LINKMAIN_FLG,A.LINK_NO,S.CLASSIFY_TYPE AS  DOSE_TYPE " +
        			"FROM OPD_ORDER A ,SYS_PHAROUTE S WHERE  S.ROUTE_CODE= A.ROUTE_CODE  " +
        			"AND a.case_no = '" + caseNo + "'  AND rx_type = '" +rxType+
        			"' ORDER BY a.rx_no";
        	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        	//System.out.println("parm===="+sql);
        	for (int i = 0; i < count; i++) {
        		tableParm.setData("K_FLG", i, "N");//处方
				//tableParm.setData("D_FLG", i, "N");//底方
				tableParm.setData("S_FLG", i, "N");//输液
				tableParm.setData("Z_FLG", i, "N");//注射
				tableParm.setData("W_FLG", i, "N");//外用
				tableParm.setData("FLG", i, "Y");//选 
        	}
        	for (int j = 0; j < parm.getCount("RX_NO"); j++) {
        			doseType=parm.getValue("DOSE_TYPE", j);
        			rxNo1=parm.getValue("RX_NO", j);
        			presrtNo1=parm.getValue("PRESRT_NO",j);
//        			System.out.println("DOSE_TYPE==="+doseType);
        			for (int i = 0; i < count; i++) {
                		rxNo=tableParm.getValue("RX_NO", i);
                		presrtNo=tableParm.getValue("PRESRT_NO", i);
                		if(rxNo1.equals(rxNo) && presrtNo1.equals(presrtNo)){
                			n=i;
                			break;
                		}
                	}
        			if(doseType.equals("O")){//口服
         				tableParm.setData("K_FLG", n, "Y");//处方
//         				//tableParm.setData("D_FLG", n, "Y");//底方
//         				tableParm.setData("S_FLG", n, "N");//输液
//         				tableParm.setData("Z_FLG", n, "N");//注射
//         				tableParm.setData("W_FLG", n, "N");//外用
//            			System.out.println("=null==n==O=="+n);
            		}else if(doseType.equals("F")){//大量点滴
            			//tableParm.setData("C_FLG", n, "Y");
         				//tableParm.setData("D_FLG", n, "Y");
         				tableParm.setData("S_FLG", n, "Y"); 
//            			System.out.println("=null==n==F=="+n);
            		}else if(doseType.equals("I")){//针剂
            			//tableParm.setData("C_FLG", n, "Y");
         				//tableParm.setData("D_FLG", n, "Y");
         				tableParm.setData("Z_FLG", n, "Y");
//            			System.out.println("=null==n==I=="+n);
            		}else if(doseType.equals("E")){//外用
            			//tableParm.setData("C_FLG", n, "Y");
         				//tableParm.setData("D_FLG", n, "Y");
         				tableParm.setData("W_FLG", n, "Y");
            		}
        		}
        }else if(index==3){ //中药
        	viewCheckBox(true,false);//显示处方、底方这两个Checkbox
        	this.getCheckBox("tCheckBox_0").setSelected(true);//默认勾选处方
        	this.getCheckBox("tCheckBox_1").setSelected(true);//默认勾选底方
        }     
       String sFlg="";//输液
       String zFlg="";//注射
       String wFlg="";//外用
       String kFlg="";//口服
       //==liling add end 20140721====
        //判断目前语种是否是英文
        boolean isEN = false;
        if ("en".equals(this.getLanguage()))
            isEN = true;
        for (int i = 0; i < count; i++) {
        	
        	tableParm.setData("FLG", i, "Y");//选 
        	
            if (isEN)
                tableParm.addData("RX_DESC", "【" + (i + 1) + "】 Rx");
            else
                tableParm.addData("RX_DESC", "第" + (i + 1) + "张处方");
            sFlg=tableParm.getValue("S_FLG", i);//输液
            zFlg=tableParm.getValue("Z_FLG", i);//注射
            wFlg=tableParm.getValue("W_FLG", i);//外用
            kFlg=tableParm.getValue("K_FLG", i);//口服
            if(sFlg.equals("N"))table.setLockCell(i, "S_FLG", true);//锁列           
            if(zFlg.equals("N"))table.setLockCell(i, "Z_FLG", true);//锁列 
            if(wFlg.equals("N"))table.setLockCell(i, "W_FLG", true);//锁列           
            if(kFlg.equals("N"))table.setLockCell(i, "K_FLG", true);//锁列           
        
           // table.setLockColumns("all");
        }
        this.rxType = rxType;
//        System.out.println("tableParm==="+tableParm);
        table.setParmValue(tableParm);

    }
//    /**
//     * TCheckBox改变事件
//     */
//    public void onChangeCheckBox(){
//    	String flg=this.getCheckBox("tCheckBox_0").getValue();
//    	//String flg2=this.getCheckBox("tCheckBox_1").getText();
//    	TParm parmX=table.getParmValue();
//    	if(!"".equals(flg)){
//	    	for(int i=0;i<parmX.getCount();i++){
//	    		parmX.setData("C_FLG",i,flg);
//	    	}
//    	}
//    	table.setParmValue(parmX);
//    }
    /**
     * 西药才显示处方、底方的Checkbox
     */
    public void viewCheckBox(boolean flg1,boolean flg2){
		this.getCheckBox("tCheckBox_0").setEnabled(flg1);
		this.getCheckBox("tCheckBox_1").setEnabled(flg1);
		this.getCheckBox("tCheckBox_K").setEnabled(flg2);
		this.getCheckBox("tCheckBox_W").setEnabled(flg2);
		this.getCheckBox("tCheckBox_S").setEnabled(flg2);
		this.getCheckBox("tCheckBox_Z").setEnabled(flg2);
		this.getCheckBox("tCheckBox_0").setSelected(false);
		this.getCheckBox("tCheckBox_1").setSelected(false);
		this.getCheckBox("tCheckBox_K").setSelected(false);
		this.getCheckBox("tCheckBox_W").setSelected(false);
		this.getCheckBox("tCheckBox_S").setSelected(false);
		this.getCheckBox("tCheckBox_Z").setSelected(false);
		
    }
    /**
     * 处理 口服、外用、输液、注射 checkBox控件
     * @param flg
     */
    public void viewCheckBox1(TParm parm,int row){
    	if("Y".equals(parm.getValue("K_FLG",row))){
    		this.getCheckBox("tCheckBox_K").setEnabled(true);
    		this.setValue("tCheckBox_K", "N");
    		//this.setValue("tCheckBox_K", "Y");
    	}else{
    		this.getCheckBox("tCheckBox_K").setEnabled(false);
    		this.setValue("tCheckBox_K", "N");
    	}
    	if("Y".equals(parm.getValue("W_FLG",row))){
    		this.getCheckBox("tCheckBox_W").setEnabled(true);
    		this.setValue("tCheckBox_W", "N");
    		//this.setValue("tCheckBox_W", "Y");
    	}else{
    		this.getCheckBox("tCheckBox_W").setEnabled(false);
    		this.setValue("tCheckBox_W", "N");
    	}
    	if("Y".equals(parm.getValue("S_FLG",row))){
    		this.getCheckBox("tCheckBox_S").setEnabled(true);
    		this.setValue("tCheckBox_S", "Y");
    	}else{
    		this.getCheckBox("tCheckBox_S").setEnabled(false);
    		this.setValue("tCheckBox_S", "N");
    	}
    	if("Y".equals(parm.getValue("Z_FLG",row))){
    		this.getCheckBox("tCheckBox_Z").setEnabled(true);
    		this.setValue("tCheckBox_Z", "Y");
    	}else{
    		this.getCheckBox("tCheckBox_Z").setEnabled(false);
    		this.setValue("tCheckBox_Z", "N");
    	}
    }
    /**
     * 打印
     */
    public void onPrint(boolean allPrintFlg) {
        if (table == null || table.getParmValue().getCount() <= 0) {
            this.messageBox_("没有数据");
            return;
        }
        table.acceptText();
        int rxTypeInt = StringTool.getInt(rxType);
        int row=table.getSelectedRow();
        if(row < 0){
        	this.messageBox("请选择处方");
        	return;
        }
		//
		if (this.checkReturn()) {
			return;
		}
		//
        /*String kFlg=table.getItemString(row, "K_FLG");//口服
        //String dFlg=table.getItemString(row, "D_FLG");
        String sFlg=table.getItemString(row, "S_FLG");//输液
        String zFlg=table.getItemString(row, "Z_FLG");//注射
        String wFlg=table.getItemString(row, "W_FLG");//外用
*/
			//int printNum = OpdRxSheetTool.getInstance().getPrintNum(caseNo, rxNo);
			//$$=========Modified by lx 2012/07/02 改成直接打印 START========$$//
//			if(printFlg){//add caoy 处方底方//==liling 20140721 屏蔽
        	if((rxTypeInt==1  || rxTypeInt==3) && this.getCheckBox("tCheckBox_0").isSelected()){//add liling 处方
				word1.getWordText().getPM().getPageManager().print();
			}
			TParm bottParm=ODOTool.getInstance().getNewBottleLabel(this.caseNo, rxNo,presrtNo);//查询sql
			TParm newbottParm_I=new TParm();
			TParm newbottParm_E=new TParm();
			TParm newbottParm_O=new TParm();
			TParm newbottParm=new TParm();
			if(bottParm.getCount()>0){
				for(int k=0;k<bottParm.getCount();k++){
					if("I".equals(bottParm.getValue("CLASSIFY_TYPE", k))){
						newbottParm_I.addRowData(bottParm, k);
					}else if("F".equals(bottParm.getValue("CLASSIFY_TYPE", k))){
						newbottParm.addRowData(bottParm, k);
					}else if("E".equals(bottParm.getValue("CLASSIFY_TYPE", k))){
						newbottParm_E.addRowData(bottParm, k);
					}else if("O".equals(bottParm.getValue("CLASSIFY_TYPE", k))){
						newbottParm_O.addRowData(bottParm, k);
					}
					
				}
			}
//			if(bottleFlg){//add caoy 输液执行单//==liling 20140721 屏蔽
			if(!allPrintFlg){//多条打印按钮时，口服、外用 不打印
				if(this.getCheckBox("tCheckBox_W").isSelected()){//add yanjing 外用
					if(newbottParm_E.getCount()>0){
//							boolean flg3=word3.getWordText().getPM().getPageManager().print();//==liling 20140721 屏蔽
						word4.getWordText().getPM().getPageManager().print();
//							System.out.println("注射");
					}
				}
				if(this.getCheckBox("tCheckBox_K").isSelected()){//add liling 口服
						if(newbottParm_O.getCount()>0){
							word5.getWordText().getPM().getPageManager().print();
						}
				}
			}
			if(this.getCheckBox("tCheckBox_S").isSelected()){//add liling 输液
				if(newbottParm.getCount()>0){
//					boolean flg2=word2.getWordText().getPM().getPageManager().print();//==liling 20140721 屏蔽
					word2.getWordText().getPM().getPageManager().print();
//					System.out.println("输液");
				}
			}
//			if(bottleFlg){//add huangjw 注射执行单//==liling 20140721 屏蔽
			if(this.getCheckBox("tCheckBox_Z").isSelected()){//add liling 注射
				if(newbottParm_I.getCount()>0){
//					boolean flg3=word3.getWordText().getPM().getPageManager().print();//==liling 20140721 屏蔽
					word3.getWordText().getPM().getPageManager().print();
//					System.out.println("注射");
				}
			}
			
			 boolean flg=false;
			 if(!allPrintFlg){//单条打印按钮事件
				 if((rxTypeInt==1 || rxTypeInt==3) && this.getCheckBox("tCheckBox_1").isSelected()){
					 flg=word.getWordText().getPM().getPageManager().print();
				 }
			 }else{//多条打印按钮事件
				 if((rxTypeInt==1 || rxTypeInt==3) && this.getCheckBox("tCheckBox_1").isSelected()){
					 String rxNo = table.getParmValue().getValue("RX_NO",table.getSelectedRow());
					 //System.out.println("rxno:::"+rxNo);
					 if(dfMap.get(rxNo) == null || !dfMap.get(rxNo)){
						 flg=word.getWordText().getPM().getPageManager().print();
						 dfMap.put(rxNo, true);
					 }
	//				 System.out.println("非处方");
				 }
			 }
			 
			 if(rxTypeInt==2 ){//add by huangjw 20140730 管制药品增打底方        
				 flg= word.getWordText().getPM().getPageManager().print();
				 flg= word1.getWordText().getPM().getPageManager().print();       
			 }
			 
			//$$=========Modified by lx 2012/07/02 改成直接打印 END========$$//
			 if(rxTypeInt !=1 && rxTypeInt != 2  && rxTypeInt != 3){
				 flg=word1.getWordText().getPM().getPageManager().print(); 
//				 System.out.println("非处方");
			 }
			 
			 //add by huangtt 20170825 更新处方打印标记 start
			 if(rxTypeInt == 1 || rxTypeInt == 2 || rxTypeInt == 3 ){
				 String sql = "UPDATE OPD_ORDER SET MED_PRINT_FLG='Y' WHERE CASE_NO='"+caseNo+"' AND RX_NO='"+rxNo+"' ";
				 
				 if(!"".equals(presrtNo)&&presrtNo!=null){
					sql+= "AND PRESRT_NO = '"+presrtNo+"' ";
				 }else{
					sql+= "AND PRESRT_NO IS NULL ";
				 }
//				 System.out.println("sql----"+sql);
				 TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
				 if(parm.getErrCode() < 0){
					 this.messageBox("更新处方打印标记失败");
				 }
				 
			 }
			 //add by huangtt 20170825 更新处方打印标记 end
			
			 
	        /**boolean flg=word.getWordText().getPM().getPageManager().printDialog(printNum > 0 ?
	                printNum : 1);**/
	        
//			printFlg=false;
	        //System.out.println("======flg========"+flg);
	        if(flg){
	        	//保存EMR (保存，写文件)
	        	this.saveEMR(this.EMRName, this.classCode,this.subClassCode);
	        }
        
    }
    /**
     * 全部打印
     */
    public void allPrint(){
    	table.acceptText();
    	TParm tableParm = table.getParmValue();
    	if (table == null || tableParm.getCount() <= 0) {
            this.messageBox_("没有数据");
            return;
        }
    	boolean flg = false;
    	dfMap = new HashMap<String,Boolean>();
    	for(int i = 0; i < tableParm.getCount(); i++){
    		if(tableParm.getValue("FLG",i).equals("Y")){
    			table.setSelectedRow(i);//光标选中行
    			this.onTableClick();//触发点击事件
    			this.onPrint(true);//打印
    			flg = true;
    		}
    	}
    	if(!flg){
    		this.messageBox("没有勾选数据");
    		return;
    	}
    }
    /**
     * 取得根据给入rxType得到处方签数据
     * @param rxType
     * @return
     */
    private TParm getParmRxType(String rxType) {
        TParm result = new TParm();
        if (rxType == null || rxType.trim().length() < 1) {
            return result;
        }
        int index =Integer.parseInt(rxType);
        String sql = "";
        switch (index){
        case 1:
        	//== liling start ====
        	/*table
            .setHeader("处方签号,140;执行地点,100;总金额,100;处方,50,boolean;底方,50,boolean;输液,50,boolean;注射,50,boolean");                                                                                                                                                                                                                                                                                             // 20140319
        	table
            .setParmMap("RX_DESC;DEPT;AR_AMT;C_FLG;D_FLG;S_FLG;Z_FLG;CASE_NO");*/
        	table
            .setHeader("处方签号,140;开立时间,150,Timestamp,yyyy/MM/dd HH:mm:ss;执行地点,100;总金额,100;口服,50,boolean;外用,50,boolean;注射,50,boolean;输液,50,boolean;选,30,boolean");                                                                                                                                                                                                                                                                                             // 20140319
        	table
            .setParmMap("RX_DESC;ORDER_DATE;DEPT;AR_AMT;K_FLG;W_FLG;Z_FLG;S_FLG;FLG;CASE_NO;PRESRT_NO");
        	 sql = "SELECT A.EXEC_DEPT_CODE,   SUM (CASE A.RELEASE_FLG WHEN 'Y' THEN 0.00 ELSE A.AR_AMT END) AR_AMT,A.RX_NO,A.PRESRT_NO" +//,A.DOSE_TYPE 
             "  FROM   OPD_ORDER A" +
//             ",SYS_DICTIONARY S " +
             " WHERE   A.CASE_NO = '" + caseNo +
             "' AND RX_TYPE='" + rxType +
             "'"  +
//             " S.GROUP_ID = 'SYS_DOSETYPE' AND S.ID=A.DOSE_TYPE "+
             //" AND RELEASE_FLG <> 'Y' " +//注销自备标记  modify by huangjw 20140911
             " GROUP BY A.RX_NO,A.PRESRT_NO,A.EXEC_DEPT_CODE " +//,A.DOSE_TYPE//huangjw 添加A.PRESRT_NO 字段 20150119
             " ORDER BY A.RX_NO,A.PRESRT_NO";//huangjw 添加A.PRESRT_NO 字段 20150119
        	 //System.out.println("::"+sql);
    		break;
    		//== liling end ====
        case 2:
//        	table.setHeader("处方签号,140;执行地点,100;总金额,100");                                                                                                                                                                                                                                                                                             // 20140319
//        	table.setParmMap("RX_DESC;DEPT;AR_AMT;CASE_NO");
        	
        	table.setHeader("处方签号,140;开立时间,150,Timestamp,yyyy/MM/dd HH:mm:ss;执行地点,100;总金额,100;选,30,boolean");                                                                                                                                                                                                                                                                                             // 20140319
        	table.setParmMap("RX_DESC;ORDER_DATE;DEPT;AR_AMT;FLG;CASE_NO");
        	 sql = "SELECT   SUM (CASE A.RELEASE_FLG WHEN 'Y' THEN 0.00 ELSE A.AR_AMT END) AR_AMT,A.RX_NO,B.DEPT_ABS_DESC DEPT,B.DEPT_ENG_DESC" +
             "  FROM   OPD_ORDER A, SYS_DEPT B" +
             " WHERE   A.CASE_NO = '" + caseNo +
             "' AND A.EXEC_DEPT_CODE = B.DEPT_CODE AND RX_TYPE='" + rxType +
             "'" +
             //zhangyong20110308
             //" AND RELEASE_FLG <> 'Y' " +//注销自备标记  modify by huangjw 20140911
             " GROUP BY A.RX_NO,B.DEPT_ABS_DESC,B.DEPT_ENG_DESC" +
             " ORDER BY A.RX_NO";
        	 break;
        	
        case 3:
        	table.setHeader("处方签号,140;开立时间,150,Timestamp,yyyy/MM/dd HH:mm:ss;执行地点,100;总金额,100;选,30,boolean");                                                                                                                                                                                                                                                                                             // 20140319
        	table.setParmMap("RX_DESC;ORDER_DATE;DEPT;AR_AMT;FLG;CASE_NO");
        	 sql = "SELECT   SUM (CASE A.RELEASE_FLG WHEN 'Y' THEN 0.00 ELSE A.AR_AMT END) AR_AMT,A.RX_NO,A.PRESRT_NO,B.DEPT_ABS_DESC DEPT,B.DEPT_ENG_DESC" +
             "  FROM   OPD_ORDER A, SYS_DEPT B" +
             " WHERE   A.CASE_NO = '" + caseNo +
             "' AND A.EXEC_DEPT_CODE = B.DEPT_CODE AND RX_TYPE='" + rxType +
             "'" +
             //zhangyong20110308
             //" AND RELEASE_FLG <> 'Y' " +//注销自备标记  modify by huangjw 20140911
             " GROUP BY A.RX_NO,A.PRESRT_NO,B.DEPT_ABS_DESC,B.DEPT_ENG_DESC" +
             " ORDER BY A.RX_NO,A.PRESRT_NO";
        	 break;
        case 4:
        	table.setHeader("处方签号,140;开立时间,150,Timestamp,yyyy/MM/dd HH:mm:ss;执行地点,100;总金额,100;选,30,boolean");                                                                                                                                                                                                                                                                                             // 20140319
        	table.setParmMap("RX_DESC;ORDER_DATE;DEPT;AR_AMT;FLG;CASE_NO");
        	 sql = "SELECT   SUM (A.AR_AMT) AR_AMT,A.RX_NO,'' DEPT" +
             "  FROM   OPD_ORDER A" +
             " WHERE   A.CASE_NO = '" + caseNo + "' AND RX_TYPE='" +
             rxType + "'" +
             " GROUP BY A.RX_NO" +
             " ORDER BY A.RX_NO";
        	 break;
        case 5:
        	table.setHeader("处方签号,140;开立时间,150,Timestamp,yyyy/MM/dd HH:mm:ss;执行地点,100;总金额,100;选,30,boolean");                                                                                                                                                                                                                                                                                             // 20140319
        	table.setParmMap("RX_DESC;ORDER_DATE;DEPT;AR_AMT;FLG;CASE_NO");
        	//根据就诊号查询预开检查的就诊号
        	String selRegSql = "SELECT CASE_NO FROM REG_PATADM " +
        			"WHERE OLD_CASE_NO = '" + caseNo + "' AND IS_PRE_ORDER = 'Y'";
        	TParm selRegSqlParm = new TParm(TJDODBTool.getInstance().select(selRegSql));
        	if(selRegSqlParm.getCount()>0){
        		String preCaseNo = "'"+caseNo+"'";
        		for (int i = 0; i < selRegSqlParm.getCount(); i++) {
        			preCaseNo += ","+"'"+selRegSqlParm.getValue("CASE_NO", i)+"'";
				}
        		 sql = "SELECT   SUM (A.AR_AMT) AR_AMT,A.RX_NO,'' DEPT,CASE_NO" +
                 "  FROM   OPD_ORDER A" +
                 " WHERE   A.CASE_NO IN ("+preCaseNo+") AND RX_TYPE='" +
                 rxType + "' AND CAT1_TYPE='LIS'" + 
                 " GROUP BY A.RX_NO,CASE_NO" +
                 " ORDER BY A.RX_NO";
        	}else{
        		String oldCaseNo = "SELECT OLD_CASE_NO FROM REG_PATADM WHERE CASE_NO = '"+caseNo+"' " +
        				" AND IS_PRE_ORDER = 'Y'";
        		TParm selOldPreParm = new TParm(TJDODBTool.getInstance().select(oldCaseNo));
        		String  oldPreCaseNo = "'"+caseNo+"'";
        		if(selOldPreParm.getCount()>0){
        			for (int i = 0; i < selOldPreParm.getCount(); i++) {
        			oldPreCaseNo += ","+"'"+selOldPreParm.getValue("OLD_CASE_NO", i)+"'";
        			}
        		}
        	 sql = "SELECT   SUM (A.AR_AMT) AR_AMT,A.RX_NO,'' DEPT" +
             "  FROM   OPD_ORDER A" +
             " WHERE   A.CASE_NO IN ("+oldPreCaseNo+") AND RX_TYPE='" +
             rxType + "' AND CAT1_TYPE='LIS'" + 
             " GROUP BY A.RX_NO" +
             " ORDER BY A.RX_NO";
        	}
        	 break;
        case 6:
        	table.setHeader("处方签号,140;开立时间,150,Timestamp,yyyy/MM/dd HH:mm:ss;执行地点,100;总金额,100;选,30,boolean");                                                                                                                                                                                                                                                                                             // 20140319
        	table.setParmMap("RX_DESC;ORDER_DATE;DEPT;AR_AMT;FLG;CASE_NO");
        	//根据就诊号查询预开检查的就诊号
        	String selRegSql1 = "SELECT CASE_NO FROM REG_PATADM " +
        			"WHERE OLD_CASE_NO = '" + caseNo + "' AND IS_PRE_ORDER = 'Y'";
        	TParm selRegSqlParm1 = new TParm(TJDODBTool.getInstance().select(selRegSql1));
        	if(selRegSqlParm1.getCount()>0){
        		String preCaseNo = "'"+caseNo+"'";
        		for (int i = 0; i < selRegSqlParm1.getCount(); i++) {
        			preCaseNo += ","+"'"+selRegSqlParm1.getValue("CASE_NO", i)+"'";
				}
        		 sql = "SELECT   SUM (A.AR_AMT) AR_AMT,A.RX_NO,'' DEPT,CASE_NO" +
                 "  FROM   OPD_ORDER A" +
                 " WHERE   A.CASE_NO IN ("+preCaseNo+") AND RX_TYPE='" +(index-1)  + "' AND CAT1_TYPE<>'LIS' " + 
                 " GROUP BY A.RX_NO,CASE_NO" +
                 " ORDER BY A.RX_NO";
        	}else{
        		String oldCaseNo = "SELECT OLD_CASE_NO FROM REG_PATADM WHERE CASE_NO = '"+caseNo+"' " +
				" AND IS_PRE_ORDER = 'Y'";
		TParm selOldPreParm = new TParm(TJDODBTool.getInstance().select(oldCaseNo));
		String  oldPreCaseNo = "'"+caseNo+"'";
		if(selOldPreParm.getCount()>0){
			for (int i = 0; i < selOldPreParm.getCount(); i++) {
			oldPreCaseNo += ","+"'"+selOldPreParm.getValue("OLD_CASE_NO", i)+"'";
			}
		}
        	 sql = "SELECT   SUM (A.AR_AMT) AR_AMT,A.RX_NO,'' DEPT" +//modify by wanglong 20140411
             "  FROM   OPD_ORDER A" +
             " WHERE   A.CASE_NO IN ("+oldPreCaseNo+") AND RX_TYPE='" +(index-1) + "' AND CAT1_TYPE <>'LIS'" + 
             " GROUP BY A.RX_NO" +
             " ORDER BY A.RX_NO";
        	}
        	 break;
        }
        result = new TParm(TJDODBTool.getInstance().select(sql));
        switch (index){
        case 1:
//        	AR_AMT,RX_NO,DEPT,DEPT_ENG_DESC,EXINV_FLG
        	String rxNo;
        	final String orgSql =
        		" SELECT * FROM (SELECT DISTINCT A.EXEC_DEPT_CODE, B.ORG_CHN_DESC, B.ORG_ENG_DESC,A.RX_NO,A.SEQ_NO " +
        		" FROM OPD_ORDER A, IND_ORG B" +
        		" WHERE     A.RX_NO = '#'" +
        		" @"+
        		" AND A.EXEC_DEPT_CODE = B.ORG_CODE &) ORDER BY RX_NO,SEQ_NO ";
        	TParm p;
        	for (int i = 0; i < result.getCount("RX_NO"); i++) {
        		rxNo = result.getValue("RX_NO", i);
        		sql = orgSql.replace("#", rxNo);
        		if(!"".equals(result.getValue("PRESRT_NO", i)) && result.getValue("PRESRT_NO", i)!=null){
        			sql=sql.replace("@", " AND PRESRT_NO='"+result.getValue("PRESRT_NO", i)+"'");
        		}else{
        			sql=sql.replace("@", " AND PRESRT_NO IS NULL");
        		}
        		sql=sql.replace("&", " AND B.EXINV_FLG = 'N'");
        		p = new TParm(TJDODBTool.getInstance().select(sql));
        		if(p.getCount()<0){
        			sql = orgSql.replace("#", rxNo);
        			if(!"".equals(result.getValue("PRESRT_NO", i)) && result.getValue("PRESRT_NO", i)!=null){
            			sql=sql.replace("@", " AND PRESRT_NO='"+result.getValue("PRESRT_NO", i)+"'");
            		}else{
            			sql=sql.replace("@", " AND PRESRT_NO IS NULL");
            		}
        			sql=sql.replace("&", "");
            		p = new TParm(TJDODBTool.getInstance().select(sql));
        		}
        		//System.out.println(":::"+sql);
        		result.setData("DEPT", i, p.getValue("ORG_CHN_DESC", 0));
        		result.setData("DEPT_ENG_DESC", i, p.getValue("ORG_ENG_DESC", 0));
        		
			}
        	
			break;
		default:
			break;
		}
        //获取开立时间
        for(int i=0;i<result.getCount("RX_NO");i++){
        	rxNo = result.getValue("RX_NO", i);
        	String presrtNo=result.getValue("PRESRT_NO", i);
        	String dateSql="SELECT ORDER_DATE FROM OPD_ORDER WHERE RX_NO='"+rxNo+"'";
        	if(presrtNo.length() > 0){
        		dateSql+=" AND PRESRT_NO='"+presrtNo+"'";
        	}
        	TParm dateParm=new TParm(TJDODBTool.getInstance().select(dateSql));
        	result.setData("ORDER_DATE",i,dateParm.getTimestamp("ORDER_DATE",0));
        }
        return result;
    }

    
    /**
     * 关闭事件，将odo对象的过滤回置
     */
    public boolean onClosing() {
    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
        odo.getOpdOrder().setFilter(filter);
        odo.getOpdOrder().filter();
			}});
        return true;
    }
    /**
     * 上传EMR
     * @param obj Object
     */
    private void saveEMR(String fileName, String classCode,String subClassCode) {
        EMRTool emrTool = new EMRTool(odo.getCaseNo(), odo.getMrNo(), this);
        emrTool.saveEMR(this.word, fileName,classCode,subClassCode);
    }
    /**
     * 过敏史 
     * add caoyong 2014/5/8
     * @return
     */
	public String getAllerg() {
		boolean aflag = false;
		TParm Aresult = ODOTool.getInstance().getAllergyData(mrNo);// 过敏药品
		StringBuffer buf = new StringBuffer();
		String allerg = "";
		if (Aresult.getCount() > 0) {
			for (int j = 0; j < Aresult.getCount(); j++) {
				buf.append(",").append(Aresult.getValue("ORDER_DESC", j))
						.append(" ")
						.append(Aresult.getValue("ALLERGY_NOTE", j));
			}
			aflag = true;
		}
		if (aflag) {
			allerg = buf.toString();
			allerg = "过敏史:" + allerg.substring(1, allerg.length());
		}
		return allerg;
	}
    /**
     * 皮试结果
     * add caoyong 2014/5/8
     * @param westResult
     * @return
     */
    
    public String[] getPs(TParm westResult ){
    	
		StringBuffer bufB=new StringBuffer();
		StringBuffer bufM=new StringBuffer();
		String psdesc="";//皮试
		String PS="皮试";//皮试
		String psresult[]=new String[3];
		for (int i = 0; i < westResult.getCount(); i++) {
		 	  if( PS.equals(westResult.getData("DD", i))){
		           psdesc="皮试结果(       )批号_____________";
			      }
		 	  if( PS.equals(westResult.getData("DD", i))&&!"".equals(westResult.getData("SKINTEST_FLG", i))){
				   bufB.append(",").append(westResult.getData("BATCH_NO", i));
				   bufM.append(",").append(westResult.getData("SKINTEST_FLG", i));
				   pflag=true;
			    }
       }
		 psresult[0]=psdesc;
		 if(pflag){
			if(!bufB.equals("")&&bufB.toString().length()>0) {
				psresult[1]=bufB.toString().substring(1,bufB.toString().length());
			}
			if(!bufM.equals("")&&bufB.toString().length()>0) {
				psresult[2]=bufM.toString().substring(1,bufM.toString().length());
			}
		 }
	 	 return psresult;
    }
    /**
     * modify caoy
     * 
     * @param inParam
     * @return
     */
    public String getWestSql(TParm inParam) {
		String westsql = "  SELECT   CASE WHEN   OPD_ORDER.BILL_FLG='Y' THEN '√' ELSE '' END||'  '||OPD_ORDER.LINK_NO aa , "
				+ " CASE WHEN SYS_FEE.IS_REMARK = 'Y' THEN OPD_ORDER.DR_NOTE ELSE  (CASE PHA_BASE.PRESCRIPTION_FLG WHEN 'N' THEN" 
				+ " '★'||OPD_ORDER.ORDER_DESC ||' '|| OPD_ORDER.SPECIFICATION ELSE OPD_ORDER.ORDER_DESC ||' '|| OPD_ORDER.SPECIFICATION END)  END bb , "
				+ " OPD_ORDER.SPECIFICATION cc, "
				+ " PHA_BASE.PRESCRIPTION_FLG PP,"
				+ " OPD_ORDER.DR_NOTE DR,OPD_ORDER.AR_AMT AR,"//add 'AR_AMT' by huangjw 20150205
				+ " OPD_ORDER.TAKE_DAYS,"
				+ " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '皮试' ELSE SYS_PHAROUTE.ROUTE_CHN_DESC  END dd,"
				+ " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '' ELSE RTRIM(RTRIM(TO_CHAR(OPD_ORDER.MEDI_QTY,'fm9999999990.000'),'0'),'.')||''||A.UNIT_CHN_DESC  END ee,"
				+ "  SYS_PHAFREQ.FREQ_CHN_DESC||' '  ff,"
				//+ " OPD_ORDER.FREQ_CODE||' '  ff,"//频次显示代码，不显示名称modify by huangjw 20150309
				+ " CASE WHEN OPD_ORDER.DISPENSE_QTY<1 THEN TO_CHAR(OPD_ORDER.DISPENSE_QTY,'fm9999999990.00') ELSE "
				+ " TO_CHAR(OPD_ORDER.DISPENSE_QTY) END||''|| B.UNIT_CHN_DESC " +
				" ||CASE " +
				" WHEN " +
				" OPD_ORDER.EXEC_DEPT_CODE IN (SELECT ORG_CODE FROM IND_ORG WHERE ORG_FLG = 'Y' AND ORG_TYPE = 'C' AND EXINV_FLG = 'Y') " +
				" THEN '  (发药点:'||IND_ORG.ORG_CHN_DESC||')'" +
				" ELSE ''" +
				" END "+
				" ER,"+
				" OPD_ORDER.RELEASE_FLG, "//add  by huangjw 20160602
				//+ " CASE WHEN OPD_ORDER.RELEASE_FLG = 'Y' THEN '自备  '|| OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.DR_NOTE END gg ,OPD_ORDER.DOSAGE_QTY,OPD_ORDER.OWN_PRICE,OPD_ORDER.DISCOUNT_RATE, "
				+ " CASE WHEN OPD_ORDER.RELEASE_FLG = 'Y' THEN '自备药  '  ELSE  '' END gg ,OPD_ORDER.DOSAGE_QTY,OPD_ORDER.OWN_PRICE,OPD_ORDER.DISCOUNT_RATE, "
				+ " OPD_ORDER.BATCH_NO, "
				+ " CASE WHEN  OPD_ORDER.SKINTEST_FLG='0' THEN '(-)阴性' WHEN OPD_ORDER.SKINTEST_FLG='1' THEN '(+)阳性'  END SKINTEST_FLG, "
				+ " OPD_ORDER.DOSAGE_QTY || C.UNIT_CHN_DESC || '/'|| B.UNIT_CHN_DESC AS TT, "
				+ " RTRIM (RTRIM (TO_CHAR (OPD_ORDER.MEDI_QTY,'FM9999999990.000' ),'0'),'.')|| ''|| A.UNIT_CHN_DESC AS HH" 
				//modigy by huangtt 20141031  start 
//				+ "trunc( (OPD_ORDER.MEDI_QTY / PHA_TRANSUNIT.MEDI_QTY),2)" 
				/*+ "|| '(' ||CASE WHEN TRUNC ((OPD_ORDER.MEDI_QTY / PHA_TRANSUNIT.MEDI_QTY), 2) < 1" +
						" THEN  '0' || TRUNC ((OPD_ORDER.MEDI_QTY / PHA_TRANSUNIT.MEDI_QTY), 2)" +
						" ELSE TO_CHAR(TRUNC ((OPD_ORDER.MEDI_QTY / PHA_TRANSUNIT.MEDI_QTY), 2)) END"
				//modigy by huangtt 20141031  end 
				+ " || C.UNIT_CHN_DESC || ')') AS HH "*/
				+ " FROM   "
				+ "OPD_ORDER, "
				+ "SYS_PHAFREQ, "
				+ "SYS_PHAROUTE,"
				+ "SYS_UNIT A, "
				+ "SYS_UNIT B,"
				+ "SYS_FEE,SYS_UNIT C, "
				+ "PHA_TRANSUNIT , IND_ORG ,PHA_BASE"
				+ " WHERE CASE_NO = '"
				+ this.caseNo
				+ "'"
				+ "  AND RX_NO = '"
				+ inParam.getValue("RX_NO")
				+ "' @ "
				+ "  AND SYS_PHAROUTE.ROUTE_CODE(+) = OPD_ORDER.ROUTE_CODE "
				+ "  AND SYS_PHAFREQ.FREQ_CODE(+) = OPD_ORDER.FREQ_CODE "
				+ "  AND A.UNIT_CODE(+) =  OPD_ORDER.MEDI_UNIT "
				+ "  AND B.UNIT_CODE(+) =  OPD_ORDER.DISPENSE_UNIT "
				+ "  AND OPD_ORDER.ORDER_CODE = SYS_FEE.ORDER_CODE "
				+ "  AND OPD_ORDER.DOSAGE_UNIT = C.UNIT_CODE "
				+ "  AND OPD_ORDER.ORDER_CODE = PHA_TRANSUNIT.ORDER_CODE "
				+ "  AND OPD_ORDER.ORDER_CODE = PHA_BASE.ORDER_CODE "
				+ " AND OPD_ORDER.EXEC_DEPT_CODE = IND_ORG.ORG_CODE(+) "
				+ "  ORDER BY   LINK_NO, LINKMAIN_FLG DESC, SEQ_NO";
		return westsql;

	}
    
    
    /**
     * add caoy 输液执行单
     * @param rxNo
     * @param inParam
     */
    	public void getPrintBottleLabel(String rxNo, TParm inParam,TParm bottParm,String opdNewName) {
    		TParm parmData=new TParm();
    		Timestamp today = SystemTool.getInstance().getDate();
    		SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    		DecimalFormat df = new DecimalFormat("#####0.000");
    		if(bottParm.getCount()>0){
//    			bottleFlg=true;
    			String flg="";
    			for(int k=0;k<bottParm.getCount();k++){
    				if(!"".equals(bottParm.getValue("FLG", k))){
						flg=bottParm.getValue("FLG", k);
					}else{
						flg="  ";
					}
					parmData.addData("FLG", flg+" "+bottParm.getValue("LINK_NO",k));
    				parmData.addData("ORDER_DESC", bottParm.getValue("ORDER_DESC", k));
    		        parmData.addData("DISPENSE_QTY",bottParm.getValue("DISPENSE_QTY", k)+" "+df.format(bottParm.getDouble("INFLUTION_RATE", k)));
    		        parmData.addData("FREQ_CHN_DESC",bottParm.getValue("FREQ_CHN_DESC", k));
    		        parmData.addData("ROUTE_CHN_DESC", bottParm.getValue("ROUTE_CHN_DESC", k));
    		        parmData.addData("DR_NOTE", bottParm.getValue("DR_NOTE",k));//添加医师备注 add by huangjw 20141222
    		        parmData.addData("EXEC_DR_DESC",bottParm.getValue("EXEC_DR_DESC", k));
//    		        if(bottParm.getValue("EXEC_DATE", k).length()>=19){
//    		        	parmData.addData("EXEC_DATE",bottParm.getValue("EXEC_DATE", k).substring(0, 19));	
//    		        }else{
//    		        	parmData.addData("EXEC_DATE",bottParm.getValue("EXEC_DATE", k));
//    		        }
    		        
    		        parmData.addData("EXEC_DATE","");
    		        parmData.addData("EXEC_END_DATE","");
    		        
    		        parmData.addData("TAKE_DAYS",bottParm.getValue("TAKE_DAYS", k));
//    		        if(k!=bottParm.getCount()-1&&
//			        		!(bottParm.getValue("LINK_NO",k).equals(bottParm.getValue("LINK_NO",k+1)))){
//				        parmData.addData(".TableRowLineShow", true);
//			        }else{
//			        	parmData.addData(".TableRowLineShow", false);
//			        }
    		        if(k!=bottParm.getCount()-1){
			        	if("".equals(bottParm.getValue("LINK_NO",k))){
			        		parmData.addData(".TableRowLineShow", true);
			        	}else if(!(bottParm.getValue("LINK_NO",k).equals(bottParm.getValue("LINK_NO",k+1)))){
			        		parmData.addData(".TableRowLineShow", true);
			        	}else{
			        		parmData.addData(".TableRowLineShow", false);
			        	}
			        	
			        }
    		}
    			parmData.setCount(bottParm.getCount());
    			parmData.addData("SYSTEM", "COLUMNS", "FLG");
    			parmData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
    			parmData.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
    			parmData.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
    			parmData.addData("SYSTEM", "COLUMNS", "TAKE_DAYS");
    			parmData.addData("SYSTEM", "COLUMNS", "ROUTE_CHN_DESC");
//    			parmData.addData("SYSTEM", "COLUMNS", "DR_NOTE");//添加医师备注 add by huangjw 20141222
    			parmData.addData("SYSTEM", "COLUMNS", "EXEC_DATE");//yanjing 20150226 交换执行护士和执行时间
    			parmData.addData("SYSTEM", "COLUMNS", "EXEC_END_DATE");
    			parmData.addData("SYSTEM", "COLUMNS", "EXEC_DR_DESC");
    			inParam.setData("TABLE1", parmData.getData());
    			inParam.setData("DATE", "TEXT", format.format(today));
    			inParam.setData("OpdNewExaName_I", "TEXT", opdNewName);
    			inParam.setData("INFLUTION_RATE_TEXT", "TEXT", "滴速");
    			word2.setWordParameter(IReportTool.getInstance().getReportParm("OpdBottleLabelOrder_I_V45.class",inParam));
    			word2.setPreview(true);
    			word2.setFileName(IReportTool.getInstance().getReportPath("OpdBottleLabelOrder_I_V45.jhw"));
    		}
    	}
    	/**
         * add yanjing 外用执行单
         * @param rxNo
         * @param inParam
         */
        	public void getPrintBottleLabel_E(String rxNo, TParm inParam,TParm bottParm,String opdNewName) {
        		TParm parmData=new TParm();
        		Timestamp today = SystemTool.getInstance().getDate();
        		SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        		if(bottParm.getCount()>0){
//        			bottleFlg=true;
        			String flg="";
        			for(int k=0;k<bottParm.getCount();k++){
        				if(!"".equals(bottParm.getValue("FLG", k))){
    						flg=bottParm.getValue("FLG", k);
    					}else{
    						flg="  ";
    					}
    					parmData.addData("FLG", flg+" "+bottParm.getValue("LINK_NO",k));
        				parmData.addData("ORDER_DESC", bottParm.getValue("ORDER_DESC", k));
        		        parmData.addData("DISPENSE_QTY",bottParm.getValue("DISPENSE_QTY", k));
        		        parmData.addData("FREQ_CHN_DESC",bottParm.getValue("FREQ_CHN_DESC", k));
        		        parmData.addData("ROUTE_CHN_DESC", bottParm.getValue("ROUTE_CHN_DESC", k));
        		        parmData.addData("DR_NOTE", bottParm.getValue("DR_NOTE",k));//添加医师备注 add by huangjw 20141222
        		        parmData.addData("EXEC_DR_DESC",bottParm.getValue("EXEC_DR_DESC", k));
//        		        if(bottParm.getValue("EXEC_DATE", k).length()>=19){
//        		        	parmData.addData("EXEC_DATE",bottParm.getValue("EXEC_DATE", k).substring(0, 19));	
//        		        }else{
//        		        	parmData.addData("EXEC_DATE",bottParm.getValue("EXEC_DATE", k));
//        		        }
        		        parmData.addData("EXEC_DATE","");
        		        parmData.addData("EXEC_END_DATE","");
        		        parmData.addData("TAKE_DAYS",bottParm.getValue("TAKE_DAYS", k));
//        		        if(k!=bottParm.getCount()-1&&
//    			        		!(bottParm.getValue("LINK_NO",k).equals(bottParm.getValue("LINK_NO",k+1)))){
//    				        parmData.addData(".TableRowLineShow", true);
//    			        }else{
//    			        	parmData.addData(".TableRowLineShow", false);
//    			        }
        		        if(k!=bottParm.getCount()-1){
    			        	if("".equals(bottParm.getValue("LINK_NO",k))){
    			        		parmData.addData(".TableRowLineShow", true);
    			        	}else if(!(bottParm.getValue("LINK_NO",k).equals(bottParm.getValue("LINK_NO",k+1)))){
    			        		parmData.addData(".TableRowLineShow", true);
    			        	}else{
    			        		parmData.addData(".TableRowLineShow", false);
    			        	}
    			        	
    			        }
        		}
        			parmData.setCount(bottParm.getCount());
        			parmData.addData("SYSTEM", "COLUMNS", "FLG");
        			parmData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        			parmData.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
        			parmData.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
        			parmData.addData("SYSTEM", "COLUMNS", "TAKE_DAYS");
        			parmData.addData("SYSTEM", "COLUMNS", "ROUTE_CHN_DESC");
//        			parmData.addData("SYSTEM", "COLUMNS", "DR_NOTE");//添加医师备注 add by huangjw 20141222
        			parmData.addData("SYSTEM", "COLUMNS", "EXEC_DATE");//yanjing 20150226 交换执行护士和执行时间
        			parmData.addData("SYSTEM", "COLUMNS", "EXEC_END_DATE");
        			
        			parmData.addData("SYSTEM", "COLUMNS", "EXEC_DR_DESC");
        			inParam.setData("TABLE1", parmData.getData());
        			inParam.setData("DATE", "TEXT", format.format(today));
        			inParam.setData("OpdNewExaName_I", "TEXT", opdNewName);
        			
        			word4.setWordParameter(IReportTool.getInstance().getReportParm("OpdBottleLabelOrder_I_V45.class",inParam));
        			word4.setPreview(true);
        			word4.setFileName(IReportTool.getInstance().getReportPath("OpdBottleLabelOrder_I_V45.jhw"));
        		}
        	}
        	/**
             * add huangjw 口服执行单
             * @param rxNo
             * @param inParam
             */
            	public void getPrintBottleLabel_O(String rxNo, TParm inParam,TParm bottParm,String opdNewName) {
            		TParm parmData=new TParm();
            		Timestamp today = SystemTool.getInstance().getDate();
            		SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            		if(bottParm.getCount()>0){
//            			bottleFlg=true;
            			String flg="";
            			for(int k=0;k<bottParm.getCount();k++){
            				if(!"".equals(bottParm.getValue("FLG", k))){
        						flg=bottParm.getValue("FLG", k);
        					}else{
        						flg="  ";
        					}
        					parmData.addData("FLG", flg+" "+bottParm.getValue("LINK_NO",k));
            				parmData.addData("ORDER_DESC", bottParm.getValue("ORDER_DESC", k));
            		        parmData.addData("DISPENSE_QTY",bottParm.getValue("DISPENSE_QTY", k));
            		        parmData.addData("FREQ_CHN_DESC",bottParm.getValue("FREQ_CHN_DESC", k));
            		        parmData.addData("ROUTE_CHN_DESC", bottParm.getValue("ROUTE_CHN_DESC", k));
            		        parmData.addData("DR_NOTE", bottParm.getValue("DR_NOTE",k));//添加医师备注 add by huangjw 20141222
            		        parmData.addData("EXEC_DR_DESC",bottParm.getValue("EXEC_DR_DESC", k));
//            		        if(bottParm.getValue("EXEC_DATE", k).length()>=19){
//            		        	parmData.addData("EXEC_DATE",bottParm.getValue("EXEC_DATE", k).substring(0, 19));	
//            		        }else{
//            		        	parmData.addData("EXEC_DATE",bottParm.getValue("EXEC_DATE", k));
//            		        }
            		        parmData.addData("EXEC_DATE","");
            		        parmData.addData("EXEC_END_DATE","");
            		        
            		        parmData.addData("TAKE_DAYS",bottParm.getValue("TAKE_DAYS", k));
            		        if(k!=bottParm.getCount()-1){
        			        	if("".equals(bottParm.getValue("LINK_NO",k))){
        			        		parmData.addData(".TableRowLineShow", true);
        			        	}else if(!(bottParm.getValue("LINK_NO",k).equals(bottParm.getValue("LINK_NO",k+1)))){
        			        		parmData.addData(".TableRowLineShow", true);
        			        	}else{
        			        		parmData.addData(".TableRowLineShow", false);
        			        	}
        			        	
        			        }
            		}
            			parmData.setCount(bottParm.getCount());
            			parmData.addData("SYSTEM", "COLUMNS", "FLG");
            			parmData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            			parmData.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
            			parmData.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
            			parmData.addData("SYSTEM", "COLUMNS", "TAKE_DAYS");
            			parmData.addData("SYSTEM", "COLUMNS", "ROUTE_CHN_DESC");
//            			parmData.addData("SYSTEM", "COLUMNS", "DR_NOTE");//添加医师备注 add by huangjw 20141222
            			parmData.addData("SYSTEM", "COLUMNS", "EXEC_DATE");//yanjing 20150226 交换执行护士和执行时间            			
            			parmData.addData("SYSTEM", "COLUMNS", "EXEC_END_DATE");
            			
            			parmData.addData("SYSTEM", "COLUMNS", "EXEC_DR_DESC");
            			
            			inParam.setData("TABLE1", parmData.getData());
            			inParam.setData("DATE", "TEXT", format.format(today));
            			inParam.setData("OpdNewExaName_I", "TEXT", opdNewName);
            			word5.setWordParameter(IReportTool.getInstance().getReportParm("OpdBottleLabelOrder_I_V45.class",inParam));
            			word5.setPreview(true);
            			word5.setFileName(IReportTool.getInstance().getReportPath("OpdBottleLabelOrder_I_V45.jhw"));
            		}
            	}
    	
    	/**
         * add huangjw 注射执行单
         * @param rxNo
         * @param inParam
         */
        	public void getPrintBottleLabel_I(String rxNo, TParm inParam,TParm bottParm,String opdNewName,String psno,String psresult) {
        		TParm parmData=new TParm();
        		Timestamp today = SystemTool.getInstance().getDate();
        		SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        		if(bottParm.getCount()>0){
//        			bottleFlg=true;
        			String flg="";
        			for(int k=0;k<bottParm.getCount();k++){
        				if(!"".equals(bottParm.getValue("FLG", k))){
    						flg=bottParm.getValue("FLG", k);
    					}else{
    						flg="  ";
    					}
    					parmData.addData("FLG", flg+" "+bottParm.getValue("LINK_NO",k));
        				parmData.addData("ORDER_DESC", bottParm.getValue("ORDER_DESC", k));        		        
        		        // 显示输注速率
        		        DecimalFormat df = new DecimalFormat("#####0.000");
        		        parmData.addData("DISPENSE_QTY",bottParm.getValue("DISPENSE_QTY", k)+" "+df.format(bottParm.getDouble("INFLUTION_RATE", k)));
        		        //     
        		        parmData.addData("FREQ_CHN_DESC",bottParm.getValue("FREQ_CHN_DESC", k));
        		        parmData.addData("ROUTE_CHN_DESC", bottParm.getValue("ROUTE_CHN_DESC", k));
        		        parmData.addData("DR_NOTE", bottParm.getValue("DR_NOTE",k));//添加医师备注 add by huangjw 20141222
        		        parmData.addData("EXEC_DR_DESC",bottParm.getValue("EXEC_DR_DESC", k));
//        		        if(bottParm.getValue("EXEC_DATE", k).length()>=19){
//        		        	parmData.addData("EXEC_DATE",bottParm.getValue("EXEC_DATE", k).substring(0, 19));	
//        		        }else{
//        		        	parmData.addData("EXEC_DATE",bottParm.getValue("EXEC_DATE", k));
//        		        }
        		        
        		        parmData.addData("EXEC_DATE","");
        		        parmData.addData("EXEC_END_DATE","");
        		        
        		        parmData.addData("TAKE_DAYS",bottParm.getValue("TAKE_DAYS", k));
//        		        if(k!=bottParm.getCount()-1&&
//    			        		!(bottParm.getValue("LINK_NO",k).equals(bottParm.getValue("LINK_NO",k+1)))){
//    				        parmData.addData(".TableRowLineShow", true);
//    			        }else{
//    			        	parmData.addData(".TableRowLineShow", false);
//    			        }
        		        if(k!=bottParm.getCount()-1){
    			        	if("".equals(bottParm.getValue("LINK_NO",k))){
    			        		parmData.addData(".TableRowLineShow", true);
    			        	}else if(!(bottParm.getValue("LINK_NO",k).equals(bottParm.getValue("LINK_NO",k+1)))){
    			        		parmData.addData(".TableRowLineShow", true);
    			        	}else{
    			        		parmData.addData(".TableRowLineShow", false);
    			        	}
    			        	
    			        }
        		}
        			parmData.setCount(bottParm.getCount());
        			parmData.addData("SYSTEM", "COLUMNS", "FLG");
        			parmData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        			parmData.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
        			parmData.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
        			parmData.addData("SYSTEM", "COLUMNS", "TAKE_DAYS");
        			parmData.addData("SYSTEM", "COLUMNS", "ROUTE_CHN_DESC");
//        			parmData.addData("SYSTEM", "COLUMNS", "DR_NOTE");//添加医师备注 add by huangjw 20141222
        			parmData.addData("SYSTEM", "COLUMNS", "EXEC_DATE");//yanjing 20150226 交换执行护士和执行时间
        			parmData.addData("SYSTEM", "COLUMNS", "EXEC_END_DATE");
        			parmData.addData("SYSTEM", "COLUMNS", "EXEC_DR_DESC");
//        			parmData.addData("SYSTEM", "COLUMNS", "NS_NOTE");
        			inParam.setData("TABLE1", parmData.getData());
        			inParam.setData("DATE", "TEXT", format.format(today));
        			inParam.setData("OpdNewExaName_I", "TEXT", opdNewName);
        			inParam.setData("SKINTESTM","TEXT",psno);
        			inParam.setData("SKINTESTB","TEXT",psresult);
        			//
        			word3.setWordParameter(IReportTool.getInstance().getReportParm("OpdBottleLabelOrder_I_V45.class",inParam));
        			word3.setPreview(true);
        			word3.setFileName(IReportTool.getInstance().getReportPath("OpdBottleLabelOrder_I_V45.jhw"));
        		}
        	}
        	
    	/**
    	 * add caoy 管制药品
    	 * @param inParam
    	 * @param df2
    	 */
    	public void getDrugParm(TParm inParam, DecimalFormat df2) {
//    		printFlg=false;
//    		bottleFlg=false;
    		TParm drugParm=new TParm();
    		double totAmt=0;
    		String drugsql = getWestSql(inParam);//管制药品处方查询
    		drugsql=drugsql.replaceFirst("@", "");
    		TParm drugResult = new TParm(TJDODBTool.getInstance().select(drugsql));
    		for (int i = 0; i < drugResult.getCount(); i++) {

    			drugParm.addData("AA", "");
    			drugParm.addData("BB", drugResult.getData("BB", i));
    			drugParm.addData("CC", drugResult.getData("ER", i));

    			drugParm.addData("AA", "");
    			drugParm.addData("BB", "    " + "用法：每次"
    					+ drugResult.getData("HH", i));
    			drugParm.addData("CC", drugResult.getData("FF", i) + "  "
    					+ drugResult.getData("DD", i)+"  "+drugResult.getData("GG", i));//增加自备药标识
    			if(!"Y".equals(drugResult.getValue("RELEASE_FLG",i))){//自备药品不统计金额
	    			totAmt += (StringTool.round(drugResult.getDouble("DOSAGE_QTY",
	    					i)
	    					* drugResult.getDouble("OWN_PRICE", i)
	    					* drugResult.getDouble("DISCOUNT_RATE", i), 2));
    			}
    		}
    		drugParm.setCount(drugParm.getCount("AA"));
    		drugParm.addData("SYSTEM", "COLUMNS", "AA");
    		drugParm.addData("SYSTEM", "COLUMNS", "BB");
    		drugParm.addData("SYSTEM", "COLUMNS", "CC");
    		inParam.setData("ORDER_TABLE", drugParm.getData());
    		inParam.setData("TOT_AMT", "TEXT", df2.format(totAmt));
    	}
    	/**
    	 * 得到TCheckBox对象
    	 * @param tagName
    	 * @return
    	 */
    	private TCheckBox getCheckBox(String tagName){
    		return (TCheckBox)getComponent(tagName);
    	}
    	/**
    	 * 获得TRadioButton对象
    	 * @param tagName
    	 * @return
    	 */
    	private TRadioButton getRadioButton(String tagName){
    		return (TRadioButton)getComponent(tagName);
    	}
    	/**
    	 * 全选
    	 */
    	public void checkAll(){
    		TParm tableParm = table.getParmValue();
    		if(this.getCheckBox("CHECK_ALL").isSelected()){
    			for(int i = 0; i < tableParm.getCount(); i++){
    				tableParm.setData("FLG",i,"Y");
        		}
    		}else{
    			for(int i = 0; i < table.getParmValue().getCount(); i++){
    				tableParm.setData("FLG",i,"N");
        		}
    		}
    		table.setParmValue(tableParm);
    	}
    	
	/**
	 * 门急诊医生工作站中选择补打处方时（包括打印与多条打印）， 如果处方为退药已完成的状态，已退药的处方不进行处方打印操作，
	 * 并提示“药房已完成第n张处方药品的退药操作，请创建新处方后再进行打印操作。
	 * 
	 * @return
	 */
	private boolean checkReturn() {
		int row = table.getSelectedRow();
		if (rxType == "1" || rxType == "3") {// 西药、中药
			String rxNo = table.getParmValue().getValue("RX_NO", row);
			String rxDesc = table.getParmValue().getValue("RX_DESC", row);
			String sql = "SELECT RX_NO, PHA_RETN_DATE FROM OPD_ORDER WHERE RX_NO = '" + rxNo
					+ "' AND PHA_RETN_DATE IS NOT NULL AND RX_TYPE = '" + rxType + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getCount("RX_NO") > 0) {
				this.messageBox("药房已完成" + rxDesc + "药品的退药操作，请创建新处方后再进行打印操作");
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	
}
