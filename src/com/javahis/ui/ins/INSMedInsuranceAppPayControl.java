package com.javahis.ui.ins;

import java.text.DecimalFormat;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.ins.INSTool;
import jdo.ins.InsManager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>Title: �ż���ҽ�Ʒ�����֧����</p>
 *
 * <p>Description:�ż���ҽ�Ʒ�����֧����</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author lim  
 * @version 1.0
 */
public class INSMedInsuranceAppPayControl extends TControl {
	
	private TTable tTable ;
	
	private TRadioButton cz ;
	
	private TRadioButton cj ;
	
	private String nhi_no ;
	
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		this.tTable = (TTable) this.getComponent("TTABLE") ;
		this.cz = (TRadioButton)this.getComponent("CZ") ;
		this.cj = (TRadioButton)this.getComponent("CJ") ;
		this.cz.setValue(true) ;
		String sql = "SELECT * FROM SYS_REGION WHERE REGION_CODE = '"+Operator.getRegion()+"'" ;
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql));
		this.nhi_no = result2.getValue("NHI_NO",0) ;
	}
	
	public void onQuery(){
		onDownload() ;
	}
	/**
	 * ����
	 */
	public void onDownload() { 
		
		// ��֤�û�����ĺϷ���.
		String beginTime = this.getValueString("BEGIN_TIME");
		String endTime = this.getValueString("END_TIME");
		boolean czValue = this.getValueBoolean("CZ");

		if ("".equals(beginTime) || "".equals(endTime)) {
			messageBox("��ѯ���ڲ���Ϊ��");
			return;
		}
		String[] beginTimeArray = beginTime.split("-");
		String newBeginTime = beginTimeArray[0].concat(beginTimeArray[1]);

		String[] endTimeArray = endTime.split("-");
		String newEndTime = endTimeArray[0].concat(endTimeArray[1]);

		if (Integer.parseInt(newBeginTime) > Integer.parseInt(newEndTime)) {
			messageBox("��ѯ���ڲ���");
			return;
		}
		
		TParm parm = new TParm(); 
		if (czValue) {// ��ְ
			parm.setData("PIPELINE", "DataDown_sp");
			parm.setData("PLOT_TYPE", "J");
		} else {// �Ǿ�
			parm.setData("PIPELINE", "DataDown_cmtd");  
			parm.setData("PLOT_TYPE", "F");
		}
		
		parm.addData("HOSP_NHI_NO", this.nhi_no);
		parm.addData("START_CODE", newBeginTime);
		parm.addData("END_CODE", newEndTime);
		parm.addData("PARM_COUNT", 3);

		TParm resultParm = InsManager.getInstance().safe(parm,null);

		if (resultParm == null) {
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			return;
		}
		
		int count = resultParm.getCount("REPORT_CODE");
		if (count <= 0) {
			messageBox("����������!");
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			return;
		}
		this.callFunction("UI|TTABLE|setParmValue", resultParm);
	}

	/**
	 * ��ӡ
	 */
	public void onPrint(){
		int selectedRow = this.tTable.getSelectedRow() ;

		if(selectedRow<0){
			messageBox("��ѡ��һ�ʱ������Ž�������") ;
			return ;
		}
		boolean czValue = this.getValueBoolean("CZ");
		if(czValue){//��ְ��ѡ��.
			proCZ(selectedRow) ;
		}else{//�Ǿӱ�ѡ��.
			proCJ(selectedRow) ;
		}
	}
	
	  /**
	   * ����ǾӴ�ӡ
	   */
	  private void proCJ(int selectedRow)
	  {
		  String reportNo = (String) this.tTable.getValueAt_(selectedRow, 0) ;
		  String beginTime = this.getValueString("BEGIN_TIME");
		  String endTime = this.getValueString("END_TIME");		  
		  TParm parm = new TParm() ;
		  parm.setData("PIPELINE", "DataDown_cmtd") ;
		  parm.setData("PLOT_TYPE", "G") ;
		  parm.addData("REPORT_CODE", reportNo) ;
		  parm.addData("HOSP_NHI_NO", this.nhi_no) ;
		  parm.addData("PARM_COUNT", 2);
		  TParm resultParm = InsManager.getInstance().safe(parm,null) ;
		  
		  if(resultParm == null){
			  messageBox("����������ʧ��!") ;
			  return ;
		  }
		 
		 
		  resultParm.addData("REPORT_CODE", reportNo) ;
		  resultParm.addData("OPT_USER", Operator.getID()) ;
		  resultParm.addData("OPT_DATE", SystemTool.getInstance().getDate()) ;
		  resultParm.addData("OPT_TERM", Operator.getIP()) ;
		  resultParm.addData("REGION_CODE",  Operator.getRegion()) ;
		  resultParm.addData("BEGIN_DATE", beginTime) ;
		  resultParm.addData("END_DATE", endTime) ;
		  
		  TParm inputParm = new TParm() ;
		  inputParm.setData("REGION_CODE", Operator.getRegion()) ;
		  TParm result1 = INSTool.getInstance().getSysRegionInfo(inputParm) ;
	      if (result1.getErrCode() < 0) {
	    	  messageBox("���ݴ��� " + result1.getErrText());
	    	  return ;
	        }		  
//		  String bank = result1.getValue("BANK",0);
//		  String account = result1.getValue("ACCOUNT",0);
		  String company_name = result1.getValue("REGION_CHN_ABN",0);
		  String nhi_hosp_code =result1.getValue("NHI_NO",0);
		  
		//�ӻ��������н�ȡ��������ֹʱ��
		  String startDate = "" ;
		  String endDate = "" ;
		  
		  if(reportNo.length()>=23){
			  startDate = reportNo.substring(8,12)+"��"+
			  			  reportNo.substring(12,14)+"��"+
			              reportNo.substring(14,16)+"��";
			  endDate = reportNo.substring(16,20)+"��"+
		                reportNo.substring(20,22)+"��"+
		                reportNo.substring(22,24)+"��";
		  }
		  
		  int row = resultParm.getCount("CTZ1_CODE") ;
		 
		  DecimalFormat df2 = new DecimalFormat("############0.00");
		  DecimalFormat df0 = new DecimalFormat("######0");
		  
		  int BQACCOM_PERTIME=0,DROP_PERTIME=0,REFOSE_PERTIME=0;
		  double BQACCOM_AMT=0.0,DROP_AMT=0.0,REFOSE_AMT=0.0;
		  double AD_PAY_AMT_ADD=0.0;
		  double AD_PAY_AMT_REDUCE=0.0;
		  double APPLY_AMT_MZ=0.0,APPLY_AMT_MT=0.0,FLG_AGENT_AMT=0.0;
		   //�����˴Σ����
		   int mzPerTime = 0;// ,mzMzPerTime = 0;
		   double mzMtPerAmt = 0,mzMzPerAmt = 0;
		   //�Ÿ��˴Σ����
		   int yfPerTime = 0;//, yfMzPerTime = 0;
		   double yfMtPerAmt = 0,yfMzPerAmt = 0;
		   //�ǵ��˴Σ����
		   int fdPerTime = 0;//, fdMzPerTime = 0;
		   double fdMtPerAmt = 0,fdMzPerAmt = 0;
		   //�����渶�˴Σ����
		   int lwdfPerTime = 0;//, lwdfMzPerTime = 0;
		   double lwdfPerAmt = 0;//,lwdfMzPerAmt = 0;
		   //������֧���˴Ρ�������֧���˴�
		   int AD_PAY_AMT_ADD_PERTIME =0;
		   int AD_PAY_AMT_REDUCE_PERTIME=0;
		   double FUND_AMT=0.00;//����֧�����
		   String sunCode = "" ;
		   for(int i=0; i<row; i++){
			   sunCode = resultParm.getValue("SUN_CODE", i).trim() ;//��������ֲ���Ϊ  05 ����ҽ��  10 ��������  11 �Ÿ�����  
			                                                         //12 �ǵ䲹��
			   BQACCOM_PERTIME+=Integer.valueOf(resultParm.getValue("BQACCOM_PERTIME", i));
	           DROP_PERTIME+=Integer.valueOf(resultParm.getValue("DROP_PERTIME", i));
	           REFOSE_PERTIME+=Integer.valueOf(resultParm.getValue("REFOSE_PERTIME", i));
	           
	           BQACCOM_AMT+=resultParm.getDouble("BQACCOM_AMT", i);
	           DROP_AMT+=resultParm.getDouble("DROP_AMT", i);
	           REFOSE_AMT+=resultParm.getDouble("REFOSE_AMT", i);
	           
	           AD_PAY_AMT_ADD+=resultParm.getDouble("AD_PAY_AMT_ADD",i);
	           AD_PAY_AMT_REDUCE+=resultParm.getDouble("AD_PAY_AMT_REDUCE",i);
	           FLG_AGENT_AMT+=resultParm.getDouble("FLG_AGENT_AMT",i);
			   AD_PAY_AMT_ADD_PERTIME =Integer.valueOf(resultParm.getValue("ADD_NUM", i));
	           AD_PAY_AMT_REDUCE_PERTIME =Integer.valueOf(resultParm.getValue("REDUCE_NUM", i));
	           if(resultParm.getValue("PAY_TYPE",i).equals("11")){
	        	   mzMzPerAmt  += resultParm.getDouble("MZ_AGENT_AMT",i);	
	        	   yfMzPerAmt   += resultParm.getDouble("YF_AGENT_AMT",i);
	        	   fdMzPerAmt   += resultParm.getDouble("FD_AGENT_AMT",i);
	           }else if(resultParm.getValue("PAY_TYPE",i).equals("41")){
	        	   mzMtPerAmt  += resultParm.getDouble("MZ_AGENT_AMT",i);	
	        	   yfMtPerAmt   += resultParm.getDouble("YF_AGENT_AMT",i);
	        	   fdMtPerAmt   += resultParm.getDouble("FD_AGENT_AMT",i);
	           }
	           lwdfPerTime += resultParm.getDouble("DF_NUM",i);
	           lwdfPerAmt += resultParm.getDouble("DF_AMT",i);
	           FUND_AMT+=resultParm.getDouble("FUND_AMT",i);
			   if("05".equals(sunCode)){	  //05 ����ҽ��          	           
				   if(resultParm.getValue("PAY_TYPE",i).equals("11")){
		        	   APPLY_AMT_MZ+=resultParm.getDouble("APPLY_AMT",i);
				   }else if(resultParm.getValue("PAY_TYPE",i).equals("41")){
					   APPLY_AMT_MT+=resultParm.getDouble("APPLY_AMT",i);
				   }
//		           FLG_AGENT_AMT+=resultParm.getDouble("FLG_AGENT_AMT",i);
//		           mzMtPerTime +=  resultParm.getDouble("MZ_AGENT_PERTIME",i);
//		           mzMtPerAmt  += resultParm.getDouble("MZ_AGENT_AMT",i);
//		           yfMtPerTime  += resultParm.getDouble("YF_AGENT_PERTIME",i);
//		           yfMtPerAmt   += resultParm.getDouble("YF_AGENT_AMT",i);   		          
		           
			   }else if("10".equals(sunCode)){// 10 ��������
		           mzPerTime +=  resultParm.getDouble("MZ_AGENT_PERTIME",i);		          			   
			   }else if("11".equals(sunCode)){//11 �Ÿ�����
		           yfPerTime  += resultParm.getDouble("YF_AGENT_PERTIME",i);		         
			   }else if("12".equals(sunCode)){//12 �ǵ䲹��
				   fdPerTime  += resultParm.getDouble("FD_AGENT_PERTIME",i);     
			   }
		   }
		   
		   TParm reportParm = new TParm() ;
		   reportParm.setData("START_DATE","TEXT", startDate) ;//��������
		   reportParm.setData("END_DATE","TEXT", endDate) ;//��������
		   reportParm.setData("COMPANY_CODE","TEXT", nhi_hosp_code) ;//��λ����
		   reportParm.setData("ACCOUNT_NAME","TEXT", company_name) ;//��������
		   reportParm.setData("BANK","TEXT", "��������һ֧��(0902)") ;//��������
		   reportParm.setData("ACCOUNT_NO","TEXT", "0302090209300020489") ;//�����ʺ�
		   reportParm.setData("REPORT_CODE","TEXT", reportNo) ;//��������
		   
		   reportParm.setData("BQACCOM_PERTIME_SUM","TEXT", BQACCOM_PERTIME==0?"":df0.format(BQACCOM_PERTIME));//��������˴�
		   reportParm.setData("BQACCOM_AMT_SUM","TEXT",      BQACCOM_AMT==0.00?"":df2.format(Math.abs(BQACCOM_AMT)));//������ɽ��
		   reportParm.setData("DROP_PERTIME_SUM","TEXT",     DROP_PERTIME==0?"":df0.format(DROP_PERTIME));//�˷��˴�
		   reportParm.setData("DROP_AMT_SUM","TEXT",         DROP_AMT==0.00?"":df2.format(Math.abs(DROP_AMT)));//�˷ѽ��
		   reportParm.setData("REFOSE_PERTIME_SUM","TEXT",   REFOSE_PERTIME==0?"":df0.format(REFOSE_PERTIME));//�ܸ��˴�
		   reportParm.setData("REFOSE_AMT_SUM","TEXT",       REFOSE_AMT==0.00?"":df2.format(Math.abs(REFOSE_AMT)));//�ܸ����
		   reportParm.setData("AD_PAY_AMT_ADD_SUM_PERTIME","TEXT", AD_PAY_AMT_ADD_PERTIME==0.00?"":df2.format(AD_PAY_AMT_ADD_PERTIME) );//������֧���˴�
		   reportParm.setData("AD_PAY_AMT_ADD_SUM","TEXT",   AD_PAY_AMT_ADD==0.00?"":df2.format(Math.abs(AD_PAY_AMT_ADD)) );//������֧�����
		   reportParm.setData("AD_PAY_AMT_REDUCE_SUM_PERTIME","TEXT",AD_PAY_AMT_REDUCE_PERTIME==0.00?"":df2.format(AD_PAY_AMT_REDUCE_PERTIME) );//������֧���˴�
		   reportParm.setData("AD_PAY_AMT_REDUCE_SUM","TEXT",AD_PAY_AMT_REDUCE==0.00?"":df2.format(Math.abs(AD_PAY_AMT_REDUCE)) );//������֧�����
		   reportParm.setData("MJZHJ_PERTIME_SUM","TEXT",   BQACCOM_PERTIME-DROP_PERTIME-REFOSE_PERTIME+AD_PAY_AMT_ADD_PERTIME-AD_PAY_AMT_REDUCE_PERTIME==0?"":
               df0.format(BQACCOM_PERTIME-DROP_PERTIME-REFOSE_PERTIME+AD_PAY_AMT_ADD_PERTIME-AD_PAY_AMT_REDUCE_PERTIME));//�ϼ��˴�
		   reportParm.setData("MJZHJ_AMT_SUM","TEXT",       BQACCOM_AMT-DROP_AMT-REFOSE_AMT+AD_PAY_AMT_ADD-AD_PAY_AMT_REDUCE==0.00?"":
               df2.format(BQACCOM_AMT-DROP_AMT-REFOSE_AMT+AD_PAY_AMT_ADD-AD_PAY_AMT_REDUCE));//�ϼƽ��
		   
		   reportParm.setData("MZRC","TEXT",mzPerTime);//�����˴�
		   reportParm.setData("MZJE","TEXT",mzMzPerAmt+mzMtPerAmt);//�������
		   reportParm.setData("YFRC","TEXT",yfPerTime);//�Ÿ��˴�С��
		   reportParm.setData("YFJE","TEXT",yfMzPerAmt+yfMtPerAmt);//�Ÿ����С��
		   reportParm.setData("FDRC","TEXT",fdPerTime);//�ǵ��˴�С��
		   reportParm.setData("FDJE","TEXT",fdMzPerAmt+fdMtPerAmt);//�ǵ���С��
		   reportParm.setData("MTLWRC","TEXT",lwdfPerTime);//���������渶�˴�С��
		   reportParm.setData("MTLWJE","TEXT",lwdfPerAmt);	//���������渶���С��	 
		   
		   double amt = BQACCOM_AMT-DROP_AMT-REFOSE_AMT+AD_PAY_AMT_ADD-AD_PAY_AMT_REDUCE;
		   reportParm.setData("SJZFJE","TEXT",StringTool.round(amt+mzMtPerAmt+mzMzPerAmt+yfMtPerAmt+yfMzPerAmt+fdMtPerAmt+fdMzPerAmt-lwdfPerAmt, 2));//ʵ��֧�����
		   reportParm.setData("SJZFJEDX","TEXT",StringUtil.getInstance().numberToWord(amt+mzMtPerAmt+mzMzPerAmt+yfMtPerAmt+yfMzPerAmt+fdMtPerAmt+fdMzPerAmt-lwdfPerAmt));//ʵ��֧������д
		   
		   //�������ⲡ
		   reportParm.setData("TCMTJE","TEXT",APPLY_AMT_MT);//ͳ�����ؽ��
		   reportParm.setData("MZMTJE","TEXT",mzMtPerAmt);//�������ؽ��
		   reportParm.setData("YFMTJE","TEXT",yfMtPerAmt);//�Ÿ����ؽ��
		   reportParm.setData("FDMEJE","TEXT",fdMtPerAmt);//�ǵ����ؽ��
		   
		   //����
		   reportParm.setData("TCMZJE","TEXT",APPLY_AMT_MZ);//ͳ��������
		   reportParm.setData("MZMZJE","TEXT",mzMzPerAmt);//����������
		   reportParm.setData("YFMZJE","TEXT",yfMzPerAmt);//�Ÿ�������
		   reportParm.setData("FDMZJE","TEXT",fdMzPerAmt);//�ǵ�������
		  
		   this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSMedInsuranceAppPayCJ.jhw",reportParm);
	  }  
	  /**
	   * �����ְ��ӡ
	   */
	  private void proCZ(int selectedRow)
	  {
		  String reportNo = (String) this.tTable.getValueAt_(selectedRow, 0) ;
		  String beginTime = this.getValueString("BEGIN_TIME");
		  String endTime = this.getValueString("END_TIME");		  
		  TParm parm = new TParm() ;
		  parm.setData("PIPELINE", "DataDown_sp") ;
		  parm.setData("PLOT_TYPE", "K") ;
		  parm.addData("REPORT_CODE", reportNo) ;
		  parm.addData("HOSP_NHI_NO", this.nhi_no) ;
		  parm.addData("PARM_COUNT", 2);
		  TParm resultParm = InsManager.getInstance().safe(parm,null);
		 
		  if (resultParm == null) {
		      this.messageBox("����������ʧ�ܣ�");
		      return;
		    }	
		  resultParm.addData("REPORT_CODE", reportNo) ;
		  resultParm.addData("OPT_USER", Operator.getID()) ;
		  resultParm.addData("OPT_DATE", SystemTool.getInstance().getDate()) ;
		  resultParm.addData("OPT_TERM", Operator.getIP()) ;
		  resultParm.addData("REGION_CODE",  Operator.getRegion()) ;
		  resultParm.addData("BEGIN_DATE", beginTime) ;
		  resultParm.addData("END_DATE", endTime) ;
		  
		    //��ӡ����֧����
		    printCz(resultParm);		  
	  }
	  
	  private void printCz(TParm resultParm){
		    TParm inputParm = new TParm() ;
		    inputParm.setData("REGION_CODE", Operator.getRegion()) ;
//		    TParm result1 = INSTool.getInstance().getSysRegionInfo(inputParm) ;
//	        if (result1.getErrCode() < 0) {
//	            messageBox("���ݴ��� " + result1.getErrText());
//	            return ;
//	        }		
		    TParm result1  = SYSRegionTool.getInstance().selectdata(Operator.getRegion());//���ҽ���������
//		    String bank = result1.getValue("BANK",0);
//		    String acc = result1.getValue("ACCOUNT",0);
		    String hosp_name = result1.getValue("REGION_CHN_ABN",0);
		    String nhi_hosp_code =result1.getValue("NHI_NO",0);		  
		    
		    int count = resultParm.getCount("SUM_CODE");
		    
		    //�����ں�,ҽԺ����,�������,֧�����,��������˴�,������ɽ��,�����˴�,���˽��,�ܸ��˴�,�ܸ����,������֧���˴�,������֧�����,������֧���˴�,������֧�����,������
		    //���ڷ����˴�,�����˴�...
		    int bqfsrc = 0, htrc = 0, jfrc = 0, tzbzfrc = 0, tzjzfrc = 0,lwdfrc = 0;
		    //���ڷ������,���˽��,��Ŀ���Ѿ����ջؽ��,��Ŀ���ѿ���Ԥ����...
		    double bqfsje = 0, htje = 0, jfje = 0, tzbzfje = 0, tzjzfje = 0, xjje = 0,
		        cbje = 0, jjzfje = 0, grzhzf = 0, zfjehj = 0, xmffjsshje = 0, xmffkhylj = 0,lwdf = 0,lwdfje = 0 ;
		    //������������ͳ��
		    double bqfs = 0, ht = 0, jf = 0, tzbzf = 0, tzjzf = 0, xj = 0;
		    //�����˻�
		    double grbqfs = 0, grht = 0, grjf = 0, grtzbzf = 0, grtzjzf = 0, grxj = 0,grlwdf = 0;
		    //����: ����,����...
		    double jb = 0, kn = 0, kt = 0, nmg = 0, gb = 0, jc = 0, md = 0, de = 0,mz = 0,fy = 0, fd  = 0;
		    //�������,֧�����
		    String hzlb = "", zflb = "";
		    double fund_amt=0.00;//����֧�����
		    //ѭ������ÿ�ֻ������
		    for (int i = 0; i < count; i++) {
		      hzlb = resultParm.getValue("SUM_CODE", i);
		      //֧�����: 11,����,12,����
		      zflb = resultParm.getValue("PAY_TYPE", i);
		      //09:���,����ͳ��
		      if ("09".equals(hzlb))
		        continue;
		      //ͳ��01-05������
		      //��������˴�
		      bqfsrc += Integer.valueOf(resultParm.getValue("BQACCOM_PERTIME", i));
		      //�����˴�
		      htrc += Integer.valueOf(resultParm.getValue("DROP_PERTIME", i));
		      //�ܸ��˴�
		      jfrc += Integer.valueOf(resultParm.getValue("REFOSE_PERTIME", i));
		      //������֧���˴�
		      tzbzfrc += Integer.valueOf(resultParm.getValue("TZBZ_PERTIME", i));
		      //������֧���˴�
		      tzjzfrc += Integer.valueOf(resultParm.getValue("TZBJ_PERTIME", i));
		      //�����渶�˴�
		      lwdfrc += Integer.valueOf(resultParm.getValue("DF_NUM", i));
		      //����֧��
		      
		      //ȡ�����������Ľ��	
		      if("01".equals(hzlb))
		      //������ɽ��
		      {
		         grbqfs = resultParm.getDouble("BQACCOM_AMT", i);
		         //���˽��
		         grht = resultParm.getDouble("DROP_AMT", i);
		         //�ܸ����
		         grjf = resultParm.getDouble("REFOSE_AMT", i);
		         //������֧�����
		         grtzbzf = resultParm.getDouble("TZBZ_AMT", i);
		         //������֧�����
		         grtzjzf = resultParm.getDouble("TZBJ_AMT", i);
		         //�����渶���
		         grlwdf  = resultParm.getDouble("DF_AMT", i);
		         grxj = grbqfs - grht - grjf + grtzbzf - grtzjzf;
		       }else
		       {
		         bqfs = resultParm.getDouble("BQACCOM_AMT", i);
		         //���˽��
		         ht = resultParm.getDouble("DROP_AMT", i);
		         //�ܸ����
		         jf = resultParm.getDouble("REFOSE_AMT", i);
		         //������֧�����
		         tzbzf = resultParm.getDouble("TZBZ_AMT", i);
		         //������֧�����
		         tzjzf = resultParm.getDouble("TZBJ_AMT", i);
		         //�����渶���
		         lwdf =  resultParm.getDouble("DF_AMT", i);
		         xj = bqfs - ht - jf + tzbzf - tzjzf;
		        
		       }
		      fund_amt=resultParm.getDouble("FUND_AMT", i);
		      //��Ŀ���Ѿ����ջؽ��,��Ŀ���ѿ���Ԥ����
		      xmffjsshje = resultParm.getDouble("XMFFJS_AMT", i);
		      xmffkhylj  = resultParm.getDouble("XMKHYL_AMT", i);
		      //ͳ��01-05�Ľ��
		      bqfsje += bqfs;
		      htje += ht;
		      jfje += jf;
		      tzbzfje += tzbzf;
		      tzjzfje += tzjzf;
		      //�����渶���
		      lwdfje += lwdf;
		      //С�ƽ��
		      xjje += xj;
		      //������
		      cbje += resultParm.getDouble("OVER_AMT", i);	
		     
		      //�������: 01,�����ʻ�,02,�ż���,03,���в���,04,����Ա����,05,����,06,����,07,����,08,ũ��,09,���,10,��������
		      if ("01".equals(hzlb))
		        grzhzf += grxj;
		      else if ("02".equals(hzlb))
		        md += fund_amt;
		      else if ("03".equals(hzlb))
		        jc += fund_amt;
		      else if ("04".equals(hzlb))
		        gb += fund_amt;
		      else if ("05".equals(hzlb))
		        jb += fund_amt;
		      else if ("06".equals(hzlb))
		        kn += fund_amt;
		      else if ("07".equals(hzlb))
		        kt += fund_amt;
		      else if ("08".equals(hzlb))
		        nmg += fund_amt;
		      else if ("09".equals(hzlb))
		        de += fund_amt;
		      else if ("10".equals(hzlb))
		        mz += fund_amt;
		      else if("11".equals(hzlb))
		        fy += fund_amt;
		      else if("12".equals(hzlb))
		        fd += fund_amt;
		    }
		    //����֧����� = С�� - ������ - ��Ŀ���Ѿ����ջؽ�� - ��Ŀ���ѿ���Ԥ����-�����渶���, ֧�����ϼ� = ����֧����� + �����˻�֧��
		    jjzfje = xjje - cbje- xmffjsshje -xmffkhylj-lwdfje;
		    zfjehj = jjzfje + grzhzf;
		    //�������Ÿ�
		    double jiben = 0;
		    double youfu = 0;
		    jiben = jb + kn + kt + nmg + md + fd;
		    youfu = fy + jc;

		    TParm parm = new TParm() ;
		    //��ͷ����
		    
			String[] beginTimeArray = resultParm.getValue("BEGIN_DATE",0).split("-");
			String newBeginTime = beginTimeArray[0].concat(beginTimeArray[1]);

			String[] endTimeArray = resultParm.getValue("END_DATE",0).split("-");
			String newEndTime = endTimeArray[0].concat(endTimeArray[1]);	
			
		    parm.setData("BEGIN_DATE","TEXT", newBeginTime);//��������
		    parm.setData("END_DATE","TEXT", newEndTime);//������ֹ
		    parm.setData("HOSP_CODE","TEXT", nhi_hosp_code);//��λ����
		    parm.setData("HOSP_NAME","TEXT", hosp_name);//��������
		    parm.setData("BANK","TEXT", "��������һ֧��(0902)");//��������
		    parm.setData("ACCOUNT","TEXT", "0302090209300020489");//�����ʺ�
		    
		    parm.setData("BQFSRC","TEXT", bqfsrc);//���ڷ����˴�
		    parm.setData("BQFSJE","TEXT", round2(bqfsje));//���ڷ������
		    parm.setData("HTRC","TEXT", htrc);//�����˴�
		    parm.setData("HTJE","TEXT", round2(htje));//���˽��
		    parm.setData("JFRC","TEXT", jfrc);//�ܸ��˴�
		    parm.setData("JFJE","TEXT", round2(jfje));//�ܸ����
		    parm.setData("TZBZFRC","TEXT", tzbzfrc);//������֧���˴�
		    parm.setData("TZBZFJE","TEXT", round2(tzbzfje));//������֧�����
		    parm.setData("TZJZFRC","TEXT", tzjzfrc);//������֧���˴�
		    parm.setData("TZJZFJE","TEXT", round2(tzjzfje));//������֧�����
		    parm.setData("XJJE","TEXT", this.round2(xjje));//С�ƽ��
		    parm.setData("LWDFRC","TEXT", lwdfrc);//�����渶�˴�
		    parm.setData("LWDFJE","TEXT", round2(lwdfje));//�����渶���		    
		    parm.setData("CBJE","TEXT", this.round2(cbje));//������
		    parm.setData("XMFFJSSHJE","TEXT", this.round2(xmffjsshje));//��Ŀ���Ѿ����ջؽ��
		    parm.setData("XMFFKHYLJ","TEXT", this.round2(xmffkhylj));//��Ŀ���ѿ���Ԥ����
		    
		    parm.setData("JJZFJE1","TEXT", StringTool.round(jjzfje, 3));//����֧�����

		    parm.setData("JJZFJE","TEXT", StringUtil.getInstance().numberToWord(StringTool.round(jjzfje, 2)));//����֧�����

		    parm.setData("JBEN","TEXT", this.round2(jiben));//����
		    parm.setData("GWYBZ","TEXT", this.round2(gb));//����Ա����
		    parm.setData("YFBZ","TEXT", this.round2(youfu));//�Ÿ�����
		    parm.setData("CXJZ","TEXT", this.round2(mz));//�������
		    parm.setData("GRZHZF","TEXT", StringTool.round(grzhzf, 2));//�����˻�֧��
		    parm.setData("ZFJEHJ","TEXT", StringTool.round(zfjehj, 2));//֧�����ϼ�
		    parm.setData("GRZHZFDX","TEXT", StringUtil.getInstance().numberToWord(StringTool.round(grzhzf, 2)));//�����˻�֧����д
		    parm.setData("ZFJEHJDX","TEXT", StringUtil.getInstance().numberToWord(StringTool.round(zfjehj, 2)));//֧�����ϼƴ�д		 
		    
		    this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSMedInsuranceAppPayCZ.jhw",parm);
	  }
	
	/**
	 * ���
	 */
	public void onClear(){
		this.setValue("BEGIN_TIME", "") ;
		this.setValue("END_TIME", "");
		this.callFunction("UI|TTABLE|setParmValue", new TParm());
	}
	
	/**
	 * ��ʽ��
	 */
	private String round2(double d){
		 return new DecimalFormat("###########0.00").format(StringTool.round(d, 2));
	}

	public TTable gettTable() {
		return tTable;
	}

	public void settTable(TTable tTable) {
		this.tTable = tTable;
	}

	public TRadioButton getCz() {
		return cz;
	}

	public void setCz(TRadioButton cz) {
		this.cz = cz;
	}

	public TRadioButton getCj() {
		return cj;
	}

	public void setCj(TRadioButton cj) {
		this.cj = cj;
	}

	public String getNhi_no() {
		return nhi_no;
	}

	public void setNhi_no(String nhiNo) {
		nhi_no = nhiNo;
	}
	
}
