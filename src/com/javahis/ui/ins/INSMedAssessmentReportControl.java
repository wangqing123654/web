package com.javahis.ui.ins;

import java.text.DecimalFormat;
import java.util.Vector;

import jdo.ins.INSTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ҽ�����˱���</p>
 *
 * <p>Description:ҽ�����˱���</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author lim  
 * @version 1.0
 */
public class INSMedAssessmentReportControl extends TControl {
	
	private TRadioButton controlIndexStatistic ;//ĳ��ҽ��סԺ���˿���ָ��ͳ�Ʊ�
	
	private TRadioButton controlIndexFinish ;//����ҽ��סԺ���˿���ָ��������ͳ�Ʊ�
	
	private TRadioButton controlIndexDetail ;//ҽ��סԺ���˿��˿���ָ����ϸ��
	
	private TTextFormat year ;
	
	private TTextFormat start1 ;
	
	private TTextFormat end1 ;
	
	private TTextFormat start2 ;
	
	private TTextFormat end2 ;
	
	private TTable tTable ;
	
	private int mark = 1 ;
	
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		this.controlIndexStatistic = (TRadioButton) getComponent("CONTROLINDEXSTATISTIC") ;
		this.controlIndexFinish = (TRadioButton) getComponent("CONTROLINDEXFINISH") ;
		this.controlIndexDetail = (TRadioButton) getComponent("CONTROLINDEXDETAIL") ;
		
		this.year = (TTextFormat) getComponent("YEAR") ;
		this.start1 = (TTextFormat) getComponent("START1") ;
		this.end1 = (TTextFormat) getComponent("ENDDATE1") ;
		this.start2 = (TTextFormat) getComponent("START2") ;
		this.end2 = (TTextFormat) getComponent("ENDDATE2") ;
		
		this.tTable = (TTable) getComponent("TTABLE") ;
		
		this.year.setValue(StringTool.getString(SystemTool.getInstance().getDate(), "yyyy")) ;
	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		TParm parm = new TParm() ;
        if(this.mark == 1){//ĳ��ҽ��סԺ���˿���ָ��ͳ�Ʊ�
        	String year = this.getValueString("YEAR") ;
        	if("".equals(year)){
        		messageBox("�������ѯ����.") ;
        		return ;
        	}
        	String[] yearArray = year.split("-") ;
        	//��ռ����
            String sql1 = "SELECT   AA.YEAR_MON, COUNT (AA.ADM_SEQ) AS PER,"+
						         "SUM (AA.TOTAL_AMT) / COUNT (AA.ADM_SEQ) AS TOTAL_AMT,"+
						         "SUM (TOTAL_NHI_AMT) / COUNT (AA.ADM_SEQ) AS TOTAL_NHI_AMT,"+
						         "SUM (AA.OWN_AMT) / COUNT (AA.ADM_SEQ) AS OWN_AMT,"+
						         "SUM (AA.APPLY_AMT) / COUNT (AA.ADM_SEQ) AS APPLY_AMT,"+
						         "SUM (AA.HOSP_APPLY_AMT) / COUNT (AA.ADM_SEQ) AS HOSP_APPLY_AMT,"+
						         "SUM (AA.INDAY) / COUNT (AA.ADM_SEQ) AS DAYS, SUM (AA.TOTAL_AMT) T_TOTAL_AMT"+
					      " FROM (" +
					            "SELECT   B.YEAR_MON, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT,"+
                                          "SUM (A.TOTAL_NHI_AMT) AS TOTAL_NHI_AMT,"+
                                          "SUM (A.OWN_AMT) AS OWN_AMT, B.APPLY_AMT, B.HOSP_APPLY_AMT,"+
                                          " TO_CHAR (B.DS_DATE, 'YYYYMMDD')- TO_CHAR (B.IN_DATE, 'YYYYMMDD') AS INDAY"+
                                " FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B"+
                                " WHERE A.REGION_CODE = B.REGION_CODE"+
                                       " AND A.ADM_SEQ = B.ADM_SEQ"+
                                       " AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
                                       " AND SUBSTR (B.YEAR_MON, 1, 4) = '"+yearArray[0]+"'"+
                                       " GROUP BY B.YEAR_MON,B.IN_DATE,B.DS_DATE,A.ADM_SEQ,B.APPLY_AMT,HOSP_APPLY_AMT) AA"+
                          " GROUP BY AA.YEAR_MON"	 ;
            TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
    		if (result1.getErrCode() < 0) {
    			messageBox(result1.getErrText());
    			return;
    		}
    		if (result1.getCount() <= 0) {
    			messageBox("��������");
    			this.callFunction("UI|TTABLE|setParmValue", parm);
    			return;
    		}    
    		//סԺ�˴�
    		String sql2 = "SELECT   AA.YEAR_MON, COUNT (AA.ADM_SEQ) AS ADM_SEQ_NUM"+
    					  " FROM (SELECT   B.YEAR_MON, A.ADM_SEQ, B.SOURCE_CODE"+
    					  			" FROM INS_IBSORDER_DOWNLOAD A,INS_IBS B"+
    					  			" WHERE A.REGION_CODE = B.REGION_CODE"+
    					  			" AND A.ADM_SEQ = B.ADM_SEQ"+
    					  			" AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
    					  			" AND SUBSTR (B.YEAR_MON, 1, 4) = '"+yearArray[0]+"'"+
    					  			" AND B.SOURCE_CODE IN ('1', '2')"+
    					  			" GROUP BY B.YEAR_MON, A.ADM_SEQ, B.SOURCE_CODE) AA "+
    					 " GROUP BY AA.YEAR_MON" ;
    		 TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
    		 //ҩƷ���ϼ�
    		 String sql3 = " SELECT   AA.YEAR_MON, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM "+
    		 			   " FROM (SELECT   B.YEAR_MON, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT"+
                                   " FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B"+
                                   " WHERE A.REGION_CODE = B.REGION_CODE"+
                                   " AND A.ADM_SEQ = B.ADM_SEQ"+
                                   " AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
                                   " AND SUBSTR (B.YEAR_MON, 1, 4) = '"+yearArray[0]+"'"+
                                   " AND A.NHI_ORD_CLASS_CODE = '01'"+
                                   " GROUP BY B.YEAR_MON, A.ADM_SEQ) AA "+
                           " GROUP BY AA.YEAR_MON" ;
    		 TParm result3 = new TParm(TJDODBTool.getInstance().select(sql3));    	
    		 
    		 //����ҩƷ���ϼ�
    		 String sql4 = "SELECT   AA.YEAR_MON, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM" +
    		 			   " FROM (SELECT   B.YEAR_MON, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT" +
                                "  FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B " +
                                " WHERE A.REGION_CODE = B.REGION_CODE " +
                                "  AND A.ADM_SEQ = B.ADM_SEQ " +
                                "  AND A.REGION_CODE = '"+Operator.getRegion()+"'" +
                                "  AND SUBSTR (B.YEAR_MON, 1, 4) = '"+yearArray[0]+"'" +
                                "  AND A.NHI_ORD_CLASS_CODE = '01'" +
                                "  AND A.PRICE >= 100" +
                                " GROUP BY B.YEAR_MON, A.ADM_SEQ) AA" +
                           " GROUP BY AA.YEAR_MON" ;
    		 TParm result4 = new TParm(TJDODBTool.getInstance().select(sql4));  

    		 //���Ϸѽ��ϼ�
    		 String sql5 = "SELECT   AA.YEAR_MON, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM"+
    		 				" FROM (SELECT   B.YEAR_MON, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT "+
    		 					   "  FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B "+
    		 					   " WHERE A.REGION_CODE = B.REGION_CODE "+
                                   "  AND A.ADM_SEQ = B.ADM_SEQ "+
                                   "  AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
                                   "  AND SUBSTR (B.YEAR_MON, 1, 4) = '"+yearArray[0]+"' "+
                                   "  AND A.NHI_ORD_CLASS_CODE = '06'"+
                                   "  GROUP BY B.YEAR_MON, A.ADM_SEQ) AA"+
                           " GROUP BY AA.YEAR_MON" ;

    		 TParm result5 = new TParm(TJDODBTool.getInstance().select(sql5));  
    		 
    		 //�߼۲��Ϸѽ��ϼ�
    		 String sql6 = "SELECT   AA.YEAR_MON, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM "+
                           " FROM (SELECT   B.YEAR_MON, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT "+
                                  "  FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B "+
                                  " WHERE A.REGION_CODE = B.REGION_CODE "+
                                    "  AND A.ADM_SEQ = B.ADM_SEQ "+
                                    "  AND A.REGION_CODE = '"+Operator.getRegion()+"' "+
                                    "  AND SUBSTR (B.YEAR_MON, 1, 4) = '"+yearArray[0]+"'"+
                                    "  AND A.NHI_ORD_CLASS_CODE = '06'"+
                                    "  AND A.PRICE >= 500"+
                                 "  GROUP BY B.YEAR_MON, A.ADM_SEQ) AA "+
                           " GROUP BY AA.YEAR_MON" ;

    		 TParm result6 = new TParm(TJDODBTool.getInstance().select(sql6)); 
    		 
    		 //���ѽ��ϼ�
    		 String sql7 = "SELECT   AA.YEAR_MON, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM "+
                           " FROM (SELECT   B.YEAR_MON, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT "+
                                    " FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B "+
                                    " WHERE A.REGION_CODE = B.REGION_CODE "+
                                     " AND A.ADM_SEQ = B.ADM_SEQ "+
                                     " AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
                                     " AND SUBSTR (B.YEAR_MON, 1, 4) = '"+yearArray[0]+"' "+
                                     " AND A.NHI_ORD_CLASS_CODE = '02' "+
                                     " GROUP BY B.YEAR_MON, A.ADM_SEQ) AA"+
                         " GROUP BY AA.YEAR_MON" ;

    		 TParm result7 = new TParm(TJDODBTool.getInstance().select(sql7)); 
    		 DecimalFormat df = new DecimalFormat("##########0.00"); 
    		 for (int i = 0; i < result1.getCount(); i++) {
    			 String yearMonth = result1.getValue("YEAR_MON", i) ;
    			 int per = result1.getInt("PER",i) ;//���˴�
    			 int zyrc = 0 ;
    			 double ypje = 0 ;
    			 double gzhypje = 0 ;//����ҩƷ���
    			 double clf = 0 ;//���Ϸ�
    			 double gjclf = 0 ;//�߼۲��Ϸ�
    			 double jcf = 0 ;//����
    			 double tTotalAmt= result1.getDouble("T_TOTAL_AMT",i) ;//�ܶ�
    			 
    			 //����סԺ�˴�
    			 for(int j = 0 ;i<result2.getCount() ;i++){
    				 if(result2.getValue("YEAR_MON",i).equals(yearMonth)){
    					 zyrc = result2.getInt("ADM_SEQ_NUM", i) ;
    					 break ;
    				 }
    			 }
    			 //ҩƷ���
    			 for(int j = 0 ;i<result3.getCount() ;i++){
    				 if(result3.getValue("YEAR_MON",i).equals(yearMonth)){
    					 ypje = result3.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 }   
    			 //����ҩƷ���
    			 for(int j = 0 ;i<result4.getCount() ;i++){
    				 if(result4.getValue("YEAR_MON",i).equals(yearMonth)){
    					 gzhypje = result4.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 } 
    			 //���Ϸ�
    			 for(int j = 0 ;i<result5.getCount() ;i++){
    				 if(result5.getValue("YEAR_MON",i).equals(yearMonth)){
    					 clf = result5.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 }    			 
    			 //�߼۲��Ϸ�
    			 for(int j = 0 ;i<result6.getCount() ;i++){
    				 if(result6.getValue("YEAR_MON",i).equals(yearMonth)){
    					 gjclf = result6.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 }  
    			 //����
    			 for(int j = 0 ;i<result7.getCount() ;i++){
    				 if(result7.getValue("YEAR_MON",i).equals(yearMonth)){
    					 jcf = result7.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 }   			 
    			 
    			 //����������ת��
    			 double zyhzhlv = (per == 0) ? 0 : (per - zyrc)/per ;
    			 //ҩƷ��ռҽ���ܷ��ñ���
    			 double ypf = (tTotalAmt == 0) ? 0 : ypje/tTotalAmt ;
    			 //����ҩƷռҩƷ�ѱ���
    			 double gzhyp = (tTotalAmt == 0) ? 0 : gzhypje/tTotalAmt ;
    			 //���Ϸ�ռҽ�Ʒѱ���
    			 double clfbl = (tTotalAmt == 0) ? 0 : clf/tTotalAmt ;
    			 //�߼۲��Ϸ�ռҽ�Ʒѱ���
    			 double gjclfbl = (tTotalAmt == 0) ? 0 : gjclf/tTotalAmt ;
    			 //����ռҽ�Ʒѱ���
    			 double jcfbl = (tTotalAmt == 0) ? 0 : jcf/tTotalAmt ;
    			 
    			 parm.addData("YEAR_MON", yearMonth.substring(0,4)) ;//�·�
    			 parm.addData("ADM_SEQ_NUM", zyrc) ;//סԺ�˴�
    			 parm.addData("TOTAL_AMT", df.format(result1.getDouble("TOTAL_AMT", i))) ;//�ξ�������
    			 parm.addData("TOTAL_NHI_AMT",df.format(result1.getDouble("TOTAL_NHI_AMT", i))) ;//�ξ��걨��
    			 parm.addData("OWN_AMT", df.format(result1.getDouble("OWN_AMT", i))) ;//�ξ��Ը���
    			 parm.addData("APPLY_AMT",df.format(result1.getDouble("APPLY_AMT", i))) ;//�ξ�ͳ����
    			 parm.addData("HOSP_APPLY_AMT", df.format(result1.getDouble("HOSP_APPLY_AMT", i))) ;//�ξ�������
    			 parm.addData("DAYS", result1.getData("DAYS", i)) ;//�ξ�סԺ����.
    			 
    			 parm.addData("ZHYHZHLV", df.format(zyhzhlv)) ;//������ת��
    			 parm.addData("YPFYL", df.format(ypf)) ;//ҩƷ��ռҽ���ܷ��ñ���
    			 parm.addData("GZHYP", df.format(gzhyp)) ;//����ҩƷռҩƷ�ѱ���
    			 parm.addData("CLFYP", df.format(clfbl)) ;//���Ϸ�ռҽ�Ʒѱ���
    			 parm.addData("GJCLFBL", df.format(gjclfbl)) ;//�߼۲��Ϸ�ռҽ�Ʒѱ���
    			 parm.addData("JCFBL", df.format(jcfbl)) ;//����ռҽ�Ʒѱ��� 
			}
    		 
        }else if(this.mark == 2){//����ҽ��סԺ���˿���ָ��������ͳ�Ʊ�
        	
        	String startDate = this.getValueString("START1") ;
        	String endDate = this.getValueString("ENDDATE1") ;
        	if("".equals(startDate)){
        		messageBox("�������ѯ����.") ;
        		return ;
        	}
        	if("".equals(endDate)){
        		messageBox("�������ѯ����.") ;
        		return ;       		
        	}    
        	
        	String sql1 = "SELECT   AA.DEPT_CHN_DESC, COUNT (AA.ADM_SEQ) PER,"+
        							"SUM (AA.TOTAL_AMT) / COUNT (AA.ADM_SEQ) TOTAL_AMT,"+
        							"SUM (TOTAL_NHI_AMT) / COUNT (AA.ADM_SEQ) TOTAL_NHI_AMT,"+
        							"SUM (AA.OWN_AMT) / COUNT (AA.ADM_SEQ) OWN_AMT,"+
        							"SUM (AA.APPLY_AMT) / COUNT (AA.ADM_SEQ) APPLY_AMT,"+
        							"SUM (AA.HOSP_APPLY_AMT) / COUNT (AA.ADM_SEQ) HOSP_APPLY_AMT,"+
        							"SUM (AA.INDAY) / COUNT (AA.ADM_SEQ) AS DAYS, SUM (AA.TOTAL_AMT) T_TOTAL_AMT "+
        				 " FROM (SELECT   D.DEPT_CHN_DESC, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT,"+
        				 				"  SUM (A.TOTAL_NHI_AMT) AS TOTAL_NHI_AMT,"+
        				 				"  SUM (A.OWN_AMT) AS OWN_AMT, B.APPLY_AMT, B.HOSP_APPLY_AMT,"+
        				 				"   TO_CHAR (B.DS_DATE, 'YYYYMMDD') - TO_CHAR (B.IN_DATE, 'YYYYMMDD') AS INDAY"+
        				 		" FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B, ADM_INP C,  SYS_DEPT D "+
        				 		" WHERE A.REGION_CODE = B.REGION_CODE "+
        				 			" AND A.ADM_SEQ = B.ADM_SEQ"+
        				 			" AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
        				 			" AND TO_CHAR (B.IN_DATE, 'YYYYMMDD') BETWEEN '"+transferDate(startDate)+"' AND '"+transferDate(endDate)+"'"+
        				 			" AND B.REGION_CODE = C.REGION_CODE"+
        				 			" AND B.CASE_NO = C.CASE_NO"+
        				 			" AND C.DS_DEPT_CODE = D.DEPT_CODE"+
        				 		" GROUP BY D.DEPT_CHN_DESC,B.IN_DATE,B.DS_DATE,A.ADM_SEQ,B.APPLY_AMT,HOSP_APPLY_AMT) AA"+
        				 " GROUP BY AA.DEPT_CHN_DESC" ;
            TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
    		if (result1.getErrCode() < 0) {
    			messageBox(result1.getErrText());
    			return;
    		}
    		if (result1.getCount() <= 0) {
    			messageBox("��������");
    			this.callFunction("UI|TTABLE|setParmValue", parm);
    			return;
    		} 
    		//סԺ�˴�
    		String sql2 = "SELECT   AA.DEPT_CHN_DESC, COUNT (AA.ADM_SEQ) AS ADM_SEQ_NUM"+
    					  " FROM (SELECT   D.DEPT_CHN_DESC, A.ADM_SEQ, B.SOURCE_CODE"+
    					  		 " FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B, ADM_INP C,  SYS_DEPT D"+
    					  		 " WHERE A.REGION_CODE = B.REGION_CODE"+
    					  		 "  AND A.ADM_SEQ = B.ADM_SEQ"+
    					  		 "  AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
    					  		 "  AND TO_CHAR (B.IN_DATE, 'YYYYMMDD') BETWEEN '"+transferDate(startDate)+"' AND '"+transferDate(endDate)+"'"+
    					  		 "  AND B.REGION_CODE = C.REGION_CODE"+
    					  		 "  AND B.CASE_NO = C.CASE_NO"+
    					  		 "  AND C.DS_DEPT_CODE = D.DEPT_CODE"+
    					  		 "  AND B.SOURCE_CODE IN ('1', '2')"+
    					  		 " GROUP BY D.DEPT_CHN_DESC, A.ADM_SEQ, B.SOURCE_CODE) AA"+
    					  " GROUP BY AA.DEPT_CHN_DESC" ;
    		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
    		
    		//ҩƷ���ϼ�
    		String sql3 = "SELECT   AA.DEPT_CHN_DESC, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM "+
    					  " FROM (SELECT   D.DEPT_CHN_DESC, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT"+
    					  	      "  FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B, ADM_INP C,SYS_DEPT D"+
    					  	      " WHERE A.REGION_CODE = B.REGION_CODE"+
    					  	        " AND A.ADM_SEQ = B.ADM_SEQ"+
    					  	        " AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
    					  	        " AND TO_CHAR (B.IN_DATE, 'YYYYMMDD') BETWEEN '"+transferDate(startDate)+"' AND '"+transferDate(endDate)+"'"+
    					  	        " AND B.REGION_CODE = C.REGION_CODE"+
    					  	        " AND B.CASE_NO = C.CASE_NO"+
    					  	        " AND C.DS_DEPT_CODE = D.DEPT_CODE"+
    					  	        " AND A.NHI_ORD_CLASS_CODE = '01'"+
    					  	      " GROUP BY D.DEPT_CHN_DESC, A.ADM_SEQ) AA"+
    					 " GROUP BY AA.DEPT_CHN_DESC;" ;
    		TParm result3 = new TParm(TJDODBTool.getInstance().select(sql3));
    		
    		//����ҩƷ���ϼ�
    		String sql4 = "SELECT   AA.DEPT_CHN_DESC, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM"+
    					  " FROM (SELECT   D.DEPT_CHN_DESC, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT"+
    					  			" FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B, ADM_INP C, SYS_DEPT D"+
    					  			" WHERE A.REGION_CODE = B.REGION_CODE"+
    					  			  " AND A.ADM_SEQ = B.ADM_SEQ"+
    					  			  " AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
    					  			  " AND TO_CHAR (B.IN_DATE, 'YYYYMMDD') BETWEEN '"+transferDate(startDate)+"' AND '"+transferDate(endDate)+"'"+
    					  			  " AND B.REGION_CODE = C.REGION_CODE"+
    					  			  " AND B.CASE_NO = C.CASE_NO"+
    					  			  " AND C.DS_DEPT_CODE = D.DEPT_CODE"+
    					  			  " AND A.NHI_ORD_CLASS_CODE = '01'"+
    					  			  " AND A.PRICE >= 100"+
    					  			" GROUP BY D.DEPT_CHN_DESC, A.ADM_SEQ) AA"+
    					  " GROUP BY AA.DEPT_CHN_DESC" ;
    		TParm result4 = new TParm(TJDODBTool.getInstance().select(sql4));
    		
    		//.���Ϸѽ��ϼ�
    		String sql5 = "SELECT   AA.DEPT_CHN_DESC, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM "+
    					  " FROM (SELECT   D.DEPT_CHN_DESC, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT"+
    					  		  " FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B, ADM_INP C,SYS_DEPT D"+
    					  		  " WHERE A.REGION_CODE = B.REGION_CODE"+
    					  		  	" AND A.ADM_SEQ = B.ADM_SEQ"+
    					  		  	" AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
    					  		  	" AND TO_CHAR (B.IN_DATE, 'YYYYMMDD') BETWEEN '"+transferDate(startDate)+"' AND '"+transferDate(endDate)+"'"+
    					  		  	" AND B.REGION_CODE = C.REGION_CODE"+
    					  		  	" AND B.CASE_NO = C.CASE_NO"+
    					  		  	" AND C.DS_DEPT_CODE = D.DEPT_CODE"+
    					  		  	" AND A.NHI_ORD_CLASS_CODE = '06'"+
    					  		  " GROUP BY D.DEPT_CHN_DESC, A.ADM_SEQ) AA"+
    					  " GROUP BY AA.DEPT_CHN_DESC" ;
    		TParm result5 = new TParm(TJDODBTool.getInstance().select(sql5));
    		//�߼۲��Ϸѽ��ϼ�
    		String sql6 = "SELECT   AA.DEPT_CHN_DESC, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM " +
    					  " FROM (SELECT   D.DEPT_CHN_DESC, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT" +
    					  		  " FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B, ADM_INP C,SYS_DEPT D" +
    					  		  " WHERE A.REGION_CODE = B.REGION_CODE" +
    					  		  	" AND A.ADM_SEQ = B.ADM_SEQ" +
    					  		  	" AND A.REGION_CODE = '"+Operator.getRegion()+"'" +
    					  		  	" AND TO_CHAR (B.IN_DATE, 'YYYYMMDD') BETWEEN '"+transferDate(startDate)+"' AND '"+transferDate(endDate)+"'" +
    					  		  	" AND B.REGION_CODE = C.REGION_CODE" +
    					  		  	" AND B.CASE_NO = C.CASE_NO" +
    					  		  	" AND C.DS_DEPT_CODE = D.DEPT_CODE" +
    					  		  	" AND A.NHI_ORD_CLASS_CODE = '06'" +
    					  		  	" AND A.PRICE >= 500" +
    					  		 " GROUP BY D.DEPT_CHN_DESC, A.ADM_SEQ) AA" +
    					 " GROUP BY AA.DEPT_CHN_DESC" ;	
    		TParm result6 = new TParm(TJDODBTool.getInstance().select(sql6));
    		
    		//���ѽ��ϼ�
    		String sql7 = "SELECT   AA.DEPT_CHN_DESC, SUM (AA.TOTAL_AMT) AS TOTAL_AMT_SUM "+
    					  "  FROM (   SELECT   D.DEPT_CHN_DESC, A.ADM_SEQ, SUM (A.TOTAL_AMT) AS TOTAL_AMT "+
    					  			"  FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B, ADM_INP C, SYS_DEPT D "+
    					  			"  WHERE A.REGION_CODE = B.REGION_CODE "+
    					  				"  AND A.ADM_SEQ = B.ADM_SEQ "+
    					  				"  AND A.REGION_CODE = '"+Operator.getRegion()+"' "+
    					  				"  AND TO_CHAR (B.IN_DATE, 'YYYYMMDD') BETWEEN '"+transferDate(startDate)+"' AND '"+transferDate(endDate)+"' "+
    					  				"  AND B.REGION_CODE = C.REGION_CODE "+
    					  				"  AND B.CASE_NO = C.CASE_NO "+
    					  				"  AND C.DS_DEPT_CODE = D.DEPT_CODE "+
    					  				"  AND A.NHI_ORD_CLASS_CODE = '02'"+
    					  			" GROUP BY D.DEPT_CHN_DESC, A.ADM_SEQ) AA"+
    					  " GROUP BY AA.DEPT_CHN_DESC" ;
    		TParm result7 = new TParm(TJDODBTool.getInstance().select(sql7));
    		DecimalFormat df = new DecimalFormat("##########0.00"); 
    		for (int i = 0; i < result1.getCount(); i++) {
    			 String deptChnDesc = result1.getValue("DEPT_CHN_DESC", i) ;
    			 
    			 int per = result1.getInt("PER",i) ;//���˴�
    			 int zyrc = 0 ;
    			 
    			 double ypje = 0 ;
    			 double gzhypje = 0 ;//����ҩƷ���
    			 double clf = 0 ;//���Ϸ�
    			 double gjclf = 0 ;//�߼۲��Ϸ�
    			 double jcf = 0 ;//����
    			 double tTotalAmt= result1.getDouble("T_TOTAL_AMT",i) ;//�ܶ�    			 
    			 
    			 //����סԺ�˴�
    			 for(int j = 0 ;i<result2.getCount() ;i++){
    				 if(result2.getValue("DEPT_CHN_DESC",i).equals(deptChnDesc)){
    					 zyrc = result2.getInt("ADM_SEQ_NUM", i) ;
    					 break ;
    				 }
    			 }  
    			 //ҩƷ���
    			 for(int j = 0 ;i<result3.getCount() ;i++){
    				 if(result3.getValue("DEPT_CHN_DESC",i).equals(deptChnDesc)){
    					 ypje = result3.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 } 
    			 //����ҩƷ���
    			 for(int j = 0 ;i<result4.getCount() ;i++){
    				 if(result4.getValue("DEPT_CHN_DESC",i).equals(deptChnDesc)){
    					 gzhypje = result4.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 } 
    			 //���Ϸ�
    			 for(int j = 0 ;i<result5.getCount() ;i++){
    				 if(result5.getValue("DEPT_CHN_DESC",i).equals(deptChnDesc)){
    					 clf = result5.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 }
    			 //�߼۲��Ϸ�
    			 for(int j = 0 ;i<result6.getCount() ;i++){
    				 if(result6.getValue("DEPT_CHN_DESC",i).equals(deptChnDesc)){
    					 gjclf = result6.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 }
    			 //����
    			 for(int j = 0 ;i<result7.getCount() ;i++){
    				 if(result7.getValue("DEPT_CHN_DESC",i).equals(deptChnDesc)){
    					 jcf = result7.getDouble("TOTAL_AMT_SUM", i) ;
    					 break ;
    				 }
    			 }     			 
    			 
    			 //����������ת��
    			 double zyhzhlv = (per == 0) ? 0 : (per - zyrc)/per ;
    			 //ҩƷ��ռҽ���ܷ��ñ���
    			 double ypf = (tTotalAmt == 0) ? 0 : ypje/tTotalAmt ;
    			 //����ҩƷռҩƷ�ѱ���
    			 double gzhyp = (tTotalAmt == 0) ? 0 : gzhypje/tTotalAmt ;
    			 //���Ϸ�ռҽ�Ʒѱ���
    			 double clfbl = (tTotalAmt == 0) ? 0 : clf/tTotalAmt ; 
    			 //�߼۲��Ϸ�ռҽ�Ʒѱ���
    			 double gjclfbl = (tTotalAmt == 0) ? 0 : gjclf/tTotalAmt ;  
    			 //����ռҽ�Ʒѱ���
    			 double jcfbl = (tTotalAmt == 0) ? 0 : jcf/tTotalAmt ;    			 
    			 
    			 //DEPT_CHN_DESC;ADM_SEQ_NUM;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;APPLY_AMT;HOSP_APPLY_AMT;DAYS;ZHYHZHLV;YPFYL;GZHYP;CLFYP;GJCLFBL;JCFBL
    			 parm.addData("DEPT_CHN_DESC", result1.getValue("DEPT_CHN_DESC",i)) ;//����
    			 parm.addData("ADM_SEQ_NUM", zyrc) ;//סԺ�˴�
    			 parm.addData("TOTAL_AMT", df.format(result1.getDouble("TOTAL_AMT", i))) ;//�ξ�������
    			 parm.addData("TOTAL_NHI_AMT", df.format(result1.getDouble("TOTAL_NHI_AMT", i))) ;//�ξ��걨��
    			 parm.addData("OWN_AMT", df.format(result1.getDouble("OWN_AMT", i))) ;//�ξ��Ը���
    			 parm.addData("APPLY_AMT",df.format(result1.getDouble("APPLY_AMT", i))) ;//�ξ�ͳ����
    			 parm.addData("HOSP_APPLY_AMT", df.format(result1.getDouble("HOSP_APPLY_AMT", i))) ;//�ξ�������
    			 parm.addData("DAYS", result1.getData("DAYS", i)) ;//�ξ�סԺ����.
    			 
    			 parm.addData("ZHYHZHLV", df.format(zyhzhlv)) ;//������ת��
    			 parm.addData("YPFYL", df.format(ypf)) ;//ҩƷ��ռҽ���ܷ��ñ���
    			 parm.addData("GZHYP", df.format(gzhyp)) ;//����ҩƷռҩƷ�ѱ���
    			 parm.addData("CLFYP", df.format(clfbl)) ;//���Ϸ�ռҽ�Ʒѱ���
    			 parm.addData("GJCLFBL", df.format(gjclfbl)) ;//�߼۲��Ϸ�ռҽ�Ʒѱ���
    			 parm.addData("JCFBL", df.format(jcfbl)) ;//����ռҽ�Ʒѱ��� 
    			 
    		}
    		
        }else if(this.mark == 3){//ҽ��סԺ���˿��˿���ָ����ϸ��
        	String startDate = this.getValueString("START2") ;
        	String endDate = this.getValueString("ENDDATE2") ;
        	if("".equals(startDate)){
        		messageBox("�������ѯ����.") ;
        		return ;
        	}
        	if("".equals(endDate)){
        		messageBox("�������ѯ����.") ;
        		return ;       		
        	}
        	//PAT_NAME;CASE_NO;DEPT_CHN_DESC;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;APPLY_AMT;HOSP_APPLY_AMT;INDAY INDAY;TOT_PHA_AMT;TOT_PHA_HPRICE;TOT_MATERIAL_AMT;TOT_MATERIAL_HPRICE;TOT_EXM_AMT
        	String sql = "SELECT   AA.PAT_NAME, AA.CASE_NO, AA.DEPT_CHN_DESC, SUM (AA.TOTAL_AMT) TOTAL_AMT,"+
        						"SUM (AA.TOTAL_NHI_AMT) TOTAL_NHI_AMT, SUM (AA.OWN_AMT) OWN_AMT,"+
        						"SUM (AA.APPLY_AMT) APPLY_AMT, SUM (AA.HOSP_APPLY_AMT) HOSP_APPLY_AMT,"+
        						"AA.INDAY INDAY, SUM (AA.TOT_PHA_AMT) TOT_PHA_AMT,"+
        						"SUM (AA.TOT_PHA_HPRICE) TOT_PHA_HPRICE,"+
        						"SUM (AA.TOT_MATERIAL_AMT) TOT_MATERIAL_AMT,"+
        						"SUM (AA.TOT_MATERIAL_HPRICE) TOT_MATERIAL_HPRICE,"+
        						"SUM (AA.TOT_EXM_AMT) TOT_EXM_AMT"+
        						" FROM (SELECT   B.PAT_NAME, B.CASE_NO, D.DEPT_CHN_DESC,"+
        									" SUM (A.TOTAL_AMT) AS TOTAL_AMT,"+
        									" SUM (A.TOTAL_NHI_AMT) AS TOTAL_NHI_AMT,"+
        									" SUM (A.OWN_AMT) AS OWN_AMT, B.APPLY_AMT, B.HOSP_APPLY_AMT,"+
        									"  TO_CHAR (B.DS_DATE, 'YYYYMMDD') - TO_CHAR (B.IN_DATE, 'YYYMMDD') INDAY,"+
        									" CASE A.NHI_ORD_CLASS_CODE"+
        									"   WHEN '01'"+
        									"     THEN SUM (A.TOTAL_AMT)"+
        									"  ELSE 0"+
        									" END TOT_PHA_AMT,"+
        									" CASE"+
        									"   WHEN A.NHI_ORD_CLASS_CODE = '01'"+
        									"   AND A.PRICE >= 100"+
        									"     THEN SUM (A.TOTAL_AMT)"+
        									" ELSE 0"+
        									" END TOT_PHA_HPRICE,"+
        									" CASE"+
        									"  WHEN A.NHI_ORD_CLASS_CODE = '06'"+
        									"    THEN SUM (A.TOTAL_AMT)"+
        									" ELSE 0"+
        									" END TOT_MATERIAL_AMT,"+
        									" CASE"+
        									"   WHEN A.NHI_ORD_CLASS_CODE = '06'"+
        									"  AND A.PRICE >= 500"+
        									"   THEN SUM (A.TOTAL_AMT)"+
        									" ELSE 0"+
        									" END TOT_MATERIAL_HPRICE,"+
        									" CASE"+
        									"  WHEN A.NHI_ORD_CLASS_CODE = '02'"+
        									"   THEN SUM (A.TOTAL_AMT)"+
        									" ELSE 0"+
        									" END TOT_EXM_AMT"+
        							" FROM INS_IBSORDER_DOWNLOAD A, INS_IBS B, ADM_INP C, SYS_DEPT D"+
        							" WHERE A.REGION_CODE = B.REGION_CODE"+
        								" AND A.ADM_SEQ = B.ADM_SEQ"+
        								" AND A.REGION_CODE = '"+Operator.getRegion()+"'"+
        								" AND TO_CHAR (B.IN_DATE, 'YYYYMMDD') BETWEEN '"+transferDate(startDate)+"' AND '"+transferDate(endDate)+"'"+
        								" AND B.REGION_CODE = C.REGION_CODE"+
        								" AND B.CASE_NO = C.CASE_NO"+
        								" AND C.DS_DEPT_CODE = D.DEPT_CODE"+
        							" GROUP BY B.PAT_NAME, B.CASE_NO, D.DEPT_CHN_DESC, A.ADM_SEQ, B.IN_DATE, B.DS_DATE, B.APPLY_AMT,HOSP_APPLY_AMT,  A.NHI_ORD_CLASS_CODE, A.PRICE) AA"+
        				" GROUP BY AA.PAT_NAME, AA.INDAY, AA.DEPT_CHN_DESC, AA.CASE_NO" ;
        	parm = new TParm(TJDODBTool.getInstance().select(sql)); 
        	
    		if (parm.getErrCode() < 0) {
    			messageBox(parm.getErrText());
    			return;
    		}
    		if (parm.getCount() <= 0) {
    			messageBox("��������");
    			this.callFunction("UI|TTABLE|setParmValue", parm);
    			return;
    		}        	
        }	
        this.callFunction("UI|TTABLE|setParmValue",parm);
	}
	
	/**
	 * 
	 * ת��ʱ��.
	 * @param date
	 * @return
	 */
	private String transferDate(String date){
		String[] dateArray = date.split(" ") ;
		String[] newDateArray = dateArray[0].split("-") ;
		return newDateArray[0]+newDateArray[1]+newDateArray[2] ;
	}	
	
	/**
	 * ���
	 */
	public void onClear(){
		this.callFunction("UI|TTABLE|setParmValue", new TParm());
		this.year.setValue(StringTool.getString(SystemTool.getInstance().getDate(), "yyyy")) ;
		this.start1.setValue("") ;
		this.end1.setValue("") ;
		this.start2.setValue("") ;
		this.end2.setValue("") ;	

		this.year.setEnabled(true) ;
		this.start1.setEnabled(false) ;
		this.end1.setEnabled(false) ;
		this.start2.setEnabled(false) ;
		this.end2.setEnabled(false) ;
		this.mark = 1 ;
	}
	
	/**
	 * ���
	 */
	public void onExport(){
    	TTable table = (TTable)this.getComponent("TTABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        } 
        String title = null ;
        if(this.mark == 1){
        	title = "ĳ��ҽ��סԺ���˿���ָ��ͳ�Ʊ�" ;
        }else if(this.mark == 2){
        	title = "����ҽ��סԺ���˿���ָ��������ͳ�Ʊ�" ;
        }else if(this.mark == 3){
        	title = "ҽ��סԺ���˿��˿���ָ����ϸ��" ;
        }
        ExportExcelUtil.getInstance().exportExcel(table, title);		
	}
	
	/**
	 * ������ѡť.
	 */
	public void onRadioClick(){
		
		if(controlIndexStatistic.hasFocus()){//ĳ��ҽ��סԺ���˿���ָ��ͳ�Ʊ�
			this.year.setEnabled(true) ;
			this.start1.setEnabled(false) ;
			this.end1.setEnabled(false) ;
			this.start2.setEnabled(false) ;
			this.end2.setEnabled(false) ;
			this.mark = 1 ;
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			
			this.year.setValue(StringTool.getString(SystemTool.getInstance().getDate(), "yyyy")) ;
			this.start1.setValue("") ;
			this.end1.setValue("") ;
			this.start2.setValue("") ;
			this.end2.setValue("") ;			
			this.tTable.setHeader("�·�,120;סԺ�˴�,120;�ξ�������,120,double,#########0.00;�ξ��걨��,120,double,#########0.00;�ξ��Ը����,120,double,#########0.00;�ξ�ͳ����,120,double,#########0.00;�ξ��������,120,double,#########0.00;�ξ�סԺ����,120;������ת��,120,double,#########0.00;ҩƷ��ռҽ���ܷ��ñ���,150,double,#########0.00;����ҩƷռҩƷ�ѱ���,150,double,#########0.00;���Ϸ�ռҽ�Ʒѱ���,150,double,#########0.00;�߼۲��Ϸ�ռҽ�Ʒѱ���,150,double,#########0.00;����ռҽ�Ʒѱ���,150,double,#########0.00") ;
			this.tTable.setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right") ;
			this.tTable.setParmMap("YEAR_MON;ADM_SEQ_NUM;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;APPLY_AMT;HOSP_APPLY_AMT;DAYS;ZHYHZHLV;YPFYL;GZHYP;CLFYP;GJCLFBL;JCFBL") ;
		}else if(this.controlIndexFinish.hasFocus()){//����ҽ��סԺ���˿���ָ��������ͳ�Ʊ�
			this.year.setEnabled(false) ;
			this.start1.setEnabled(true) ;
			this.end1.setEnabled(true) ;
			this.start2.setEnabled(false) ;
			this.end2.setEnabled(false) ;
			this.mark = 2 ;
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			
			this.year.setValue("") ;
			this.start2.setValue("") ;
			this.end2.setValue("") ;
			this.start1.setValue(StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd")) ;
			this.end1.setValue(StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd")) ;
			this.tTable.setHeader("����,120;סԺ�˴�,120;�ξ�������,120,double,#########0.00;�ξ��걨��,120,double,#########0.00;�ξ��Ը����,120,double,#########0.00;�ξ�ͳ����,120,double,#########0.00;�ξ��������,120,double,#########0.00;�ξ�סԺ����,120;������ת��,120,double,#########0.00;ҩƷ��ռҽ���ܷ��ñ���,150,double,#########0.00;����ҩƷռҩƷ�ѱ���,150,double,#########0.00;���Ϸ�ռҽ�Ʒѱ���,150,double,#########0.00;�߼۲��Ϸ�ռҽ�Ʒѱ���,150,double,#########0.00;����ռҽ�Ʒѱ���,150,double,#########0.00") ;
			this.tTable.setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right") ;
			this.tTable.setParmMap("DEPT_CHN_DESC;ADM_SEQ_NUM;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;APPLY_AMT;HOSP_APPLY_AMT;DAYS;ZHYHZHLV;YPFYL;GZHYP;CLFYP;GJCLFBL;JCFBL") ;
		}else if(this.controlIndexDetail.hasFocus()){//ҽ��סԺ���˿��˿���ָ����ϸ��
			this.year.setEnabled(false) ;
			this.start1.setEnabled(false) ;
			this.end1.setEnabled(false) ;
			this.start2.setEnabled(true) ;
			this.end2.setEnabled(true) ;
			this.mark = 3 ;
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			
			this.year.setValue("") ;
			this.start1.setValue("") ;
			this.end1.setValue("") ;
			this.start2.setValue(StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd")) ;
			this.end2.setValue(StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd")) ;
			
			this.tTable.setHeader("����,120;סԺ��,120;����,120;�������,120,double,#########0.00;�걨���,120,double,#########0.00;�Ը����,120,double,#########0.00;ͳ����,120,double,#########0.00;�������,120,double,#########0.00;סԺ����,120;ҩƷ��ռҽ���ܷ��ñ���,150,double,#########0.00;����ҩƷռҩƷ�ѱ���,150,double,#########0.00;���Ϸ�ռҽ�Ʒѱ���,150,double,#########0.00;�߼۲��Ϸ�ռҽ�Ʒѱ���,150,double,#########0.00;����ռҽ�Ʒѱ���,150,double,#########0.00") ;
			this.tTable.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right") ;
		    this.tTable.setParmMap("PAT_NAME;CASE_NO;DEPT_CHN_DESC;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;APPLY_AMT;HOSP_APPLY_AMT;INDAY INDAY;TOT_PHA_AMT;TOT_PHA_HPRICE;TOT_MATERIAL_AMT;TOT_MATERIAL_HPRICE;TOT_EXM_AMT") ;
		}
		
	}

	public TRadioButton getControlIndexStatistic() {
		return controlIndexStatistic;
	}

	public void setControlIndexStatistic(TRadioButton controlIndexStatistic) {
		this.controlIndexStatistic = controlIndexStatistic;
	}

	public TRadioButton getControlIndexFinish() {
		return controlIndexFinish;
	}

	public void setControlIndexFinish(TRadioButton controlIndexFinish) {
		this.controlIndexFinish = controlIndexFinish;
	}

	public TRadioButton getControlIndexDetail() {
		return controlIndexDetail;
	}

	public void setControlIndexDetail(TRadioButton controlIndexDetail) {
		this.controlIndexDetail = controlIndexDetail;
	}

	public TTextFormat getYear() {
		return year;
	}

	public void setYear(TTextFormat year) {
		this.year = year;
	}

	public TTextFormat getStart1() {
		return start1;
	}

	public void setStart1(TTextFormat start1) {
		this.start1 = start1;
	}

	public TTextFormat getEnd1() {
		return end1;
	}

	public void setEnd1(TTextFormat end1) {
		this.end1 = end1;
	}

	public TTextFormat getStart2() {
		return start2;
	}

	public void setStart2(TTextFormat start2) {
		this.start2 = start2;
	}

	public TTextFormat getEnd2() {
		return end2;
	}

	public void setEnd2(TTextFormat end2) {
		this.end2 = end2;
	}
	
	public TTable gettTable() {
		return tTable;
	}

	public void settTable(TTable tTable) {
		this.tTable = tTable;
	}
}
