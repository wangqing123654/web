 package com.javahis.ui.mem;
 
 import com.dongyang.control.TControl;
 import com.dongyang.data.TParm;
 import com.dongyang.jdo.TJDODBTool;
 import com.dongyang.ui.TComboBox;
 import com.dongyang.ui.TRadioButton;
 import com.dongyang.ui.TTable;
 import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
 import com.javahis.util.DateUtil;
 import java.sql.Timestamp;

import jdo.sys.IReportTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
 
 public class MEMPatInfoPrintControl extends TControl{
   String first_flg;
   TTable table;
   Pat pat;
   public void onInit(){
     super.onInit();
     Timestamp date = SystemTool.getInstance().getDate();
     
     // ��ʼ����ѯ����
     this.setValue("END_DATE_Q",
                   date.toString().substring(0, 10).replace('-', '/') +
                   " 23:59:59");
     this.setValue("END_DATE",
             date.toString().substring(0, 10).replace('-', '/') +
             " 23:59:59");
     this.setValue("START_DATE_Q",
                   StringTool.rollDate(date, -7).toString().substring(0, 10).
                   replace('-', '/') + " 00:00:00");
     this.setValue("START_DATE",
             StringTool.rollDate(date, -7).toString().substring(0, 10).
             replace('-', '/') + " 00:00:00");
     
     getRadioButton("UPDATE_FLG").setEnabled(false);
     getRadioButton("MODIFY_FLG").setEnabled(false);
     getTextFormat("START_DATE").setEnabled(false);
     getTextFormat("END_DATE").setEnabled(false);
     table = getTable("TABLE");
   }
 
  
  /**
   * ��ѯ
   */
  public void onQuery(){
     
	 
     table.removeRowAll();
     String mr_no = this.getValueString("MR_NO").trim();
     String allMrNo = PatTool.getInstance().checkMrno(mr_no);
     if (getRadioButton("IS_FIRST").isSelected())//���ջ�����Ϣ��ѯ
     {		
    	   if(isEmpty())//У���ż�ס�Ƿ�Ϊ��
			 return;
	       //�ż���ס��
	       String adm_type=getComboBox("ADM_TYPE").getText();
	       String org_code = getValueString("ORG_CODE");
	       String sql;
	       if (getRadioButton("FIRST").isSelected())//����
	    	   first_flg = "0";
	       else {//����
	    	   first_flg = "1";
	       }
	       if(isAdm()){//סԺ
	    	   sql = "SELECT DISTINCT A.MR_NO,'N' AS XUAN, B.PAT_NAME, B.SEX_CODE," +
		       		" B.BIRTH_DATE, B.IDNO, B.NATION_CODE,B.SPECIES_CODE, B.CTZ1_CODE, " +
		       		" B.TEL_HOME, B.CURRENT_ADDRESS  FROM ADM_INP A,SYS_PATINFO B WHERE  A.MR_NO=B.MR_NO ";
	       }else{//�ż���
	    	   sql = "SELECT DISTINCT A.MR_NO,'N' AS XUAN, B.PAT_NAME, B.SEX_CODE," +
	       		" B.BIRTH_DATE, B.IDNO, B.NATION_CODE,B.SPECIES_CODE, B.CTZ1_CODE, " +
	       		" B.TEL_HOME, B.CURRENT_ADDRESS  FROM REG_PATADM A,SYS_PATINFO B WHERE  A.MR_NO=B.MR_NO AND A.VISIT_CODE='"+first_flg+"'" +
	       		" AND A.ADM_TYPE='" + adm_type + "'";
	       }
	       StringBuffer sql1=new StringBuffer(sql);
	       //�жϲ�ѯ��ʱ���Ƿ�Ϊ��
	       if("".equals(getValueString("START_DATE_Q"))||"".equals(getValueString("END_DATE_Q"))){
	    	   this.messageBox("��ѡ���ѯ����");
	    	   return;
	       }
	       else{
	    	   String start_date = getValueString("START_DATE_Q").toString().substring(0, 10).replace('-', '/');
	           String end_date = getValueString("END_DATE_Q").toString().substring(0, 10).replace('-', '/');
	           if(isAdm()){//��Ժʱ������
	        	   sql1.append(" AND A.IN_DATE BETWEEN TO_DATE('" + start_date + "','yyyy/MM/dd') AND TO_DATE('" + end_date + "','yyyy/MM/dd')");
	           }else{//�Һ�ʱ������
	        	   sql1.append(" AND A.ADM_DATE BETWEEN TO_DATE('" + start_date + "','yyyy/MM/dd') AND TO_DATE('" + end_date + "','yyyy/MM/dd')");
	           }
	       }
	       //�жϿ����Ƿ�Ϊ��
	       if(!"".equals(org_code)){
	    	   if(isAdm()){
	    		   sql1.append(" AND A.IN_DEPT_CODE='" + org_code + "'");
	    	   }else{
	    		   sql1.append(" AND A.DEPT_CODE='" + org_code + "'");
	    	   }
	       }
	       //�жϲ������Ƿ�Ϊ��
	       if(!"".equals(mr_no)){
	    	   this.setValue("MR_NO", allMrNo);
	    	   sql1.append(" AND A.MR_NO='"+allMrNo+"'");
	       }
	       TParm parm = new TParm(TJDODBTool.getInstance().select(sql1.toString()));
	       if (parm.getErrCode() < 0) {
	    	   messageBox(parm.getErrText());
	    	   return;
	       }
	       //�ж�����ѯ�Ľ�����Ƿ�������
	       if(parm.getCount()<=0){
	    	   messageBox("û������");
	    	   table.removeRowAll();
	    	   return;
	       }
	       
	       table.setParmValue(parm);
     } else {//���ղ���ʱ���ѯ
       
	       if (getRadioButton("UPDATE_FLG").isSelected()) {//�޸�
	         String sql = "SELECT DISTINCT 'N' XUAN,B.MR_NO, B.PAT_NAME, B.SEX_CODE, B.BIRTH_DATE,  B.IDNO, B.NATION_CODE,B.SPECIES_CODE, B.CTZ1_CODE, B.TEL_HOME, B.CURRENT_ADDRESS FROM SYS_PATINFO B, SYS_PATLOG A  WHERE A.MR_NO=B.MR_NO ";
	         StringBuffer sql1=new StringBuffer(sql);
	         //�ж�ʱ���Ƿ�Ϊ��
	         if("".equals(getValueString("START_DATE"))||"".equals(getValueString("END_DATE"))){//Ϊ��
	        	 this.messageBox("��ѡ���ѯ����");
	        	 return;
	         }else{//��Ϊ��
	        	 String start_date = getValueString("START_DATE").toString().substring(0, 10).replace('-', '/');
	             String end_date = getValueString("END_DATE").toString().substring(0, 10).replace('-', '/');
	        	 sql1.append("AND A.OPT_DATE BETWEEN TO_DATE('" + start_date + " 00:00:00','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('" + end_date + " 23:59:59','YYYY/MM/DD HH24:MI:SS')");
	         }
	         if(!"".equals(mr_no)){
	        	 this.setValue("MR_NO", allMrNo);
		    	 sql1.append(" AND B.MR_NO='"+allMrNo+"'");
		       }
	         TParm parm = new TParm(TJDODBTool.getInstance().select(sql1.toString()));
	         if (parm.getErrCode() < 0) {
		           messageBox(parm.getErrText());
		           return;
	         }
	         //�ж�����ѯ�Ľ�����Ƿ�������
	         if(parm.getCount()<=0){
		      	   messageBox("û������");
		      	   table.removeRowAll();
		      	   return;
	         }
	         for (int i = 0; i < parm.getCount(); i++) {
	        	 parm.setData("BIRTH_DATE", i, parm.getValue("BIRTH_DATE", i).toString().substring(0, 10).replace('-', '/'));
	         }
	         table.removeRowAll();
	         table.setParmValue(parm);
	       } else {//����
		         String sql = "SELECT * FROM SYS_PATINFO ";
		         StringBuffer sql1=new StringBuffer(sql);
		         if("".equals(getValueString("START_DATE"))||"".equals(getValueString("END_DATE"))){
		        	 this.messageBox("��ѡ���ѯ����");
		        	 return;
		         }else{
		        	 String start_date = getValueString("START_DATE").toString().substring(0, 10).replace('-', '/');
		             String end_date = getValueString("END_DATE").toString().substring(0, 10).replace('-', '/');
		        	 sql1.append("WHERE OPT_DATE BETWEEN TO_DATE('" + start_date + " 00:00:00','YYYY/MM/DD HH24:MI:SS') " + " AND TO_DATE('" + end_date + " 23:59:59','YYYY/MM/DD HH24:MI:SS')");
		         }
		         //�жϲ������Ƿ�Ϊ��
			     if(!"".equals(mr_no)){
			    	 this.setValue("MR_NO", allMrNo);
			    	 sql1.append(" AND MR_NO='"+allMrNo+"'");
			       }
		         TParm parm = new TParm(TJDODBTool.getInstance().select(sql1.toString()));
		         if (parm.getErrCode() < 0) {
			           messageBox(parm.getErrText());
			           return;
		         }
		         if(parm.getCount()<=0){
			      	   messageBox("û������");
			      	   table.removeRowAll();
			      	   return;
		         }
		         TParm inParm = new TParm();
		         for (int i = 0; i < parm.getCount(); i++) {
			           inParm.setData("XUAN", i, Character.valueOf('N'));
			           inParm.setData("MR_NO", i, parm.getValue("MR_NO", i));
			           inParm.setData("PAT_NAME", i, parm.getValue("PAT_NAME", i));
			           inParm.setData("SEX_CODE", i, parm.getValue("SEX_CODE", i));
			           if(!"".equals(parm.getValue("BIRTH_DATE", i))){
			        	   inParm.setData("BIRTH_DATE", i, parm.getValue("BIRTH_DATE", i).toString().substring(0, 10).replace('-', '/'));
			           }else{
			        	   inParm.setData("BIRTH_DATE",i,parm.getValue("BIRTH_DATE", i));
			           }
			           inParm.setData("IDNO", i, parm.getValue("IDNO", i));
			           inParm.setData("NATION_CODE", i, parm.getValue("NATION_CODE", i));
			           inParm.setData("SPECIES_CODE", i, parm.getValue("SPECIES_CODE", i));
			           inParm.setData("CTZ1_CODE", i, parm.getValue("CTZ1_CODE", i));
			           inParm.setData("TEL_HOME", i, parm.getValue("TEL_HOME", i));
			           inParm.setData("CURRENT_ADDRESS", i, parm.getValue("CURRENT_ADDRESS", i));
		         }
 
         table.removeRowAll();
         table.setParmValue(inParm);
       }
     }
   }
  
  /**
   * �ж��ż�ס�Ƿ�Ϊ��
   */
  public boolean isEmpty(){
	  if("".equals(getComboBox("ADM_TYPE").getText())){
		  this.messageBox("�ż�ס����Ϊ��");
		  return true;
	  }
	  return false;
  }
 /**
  * ���
  */
   public void onClear()
   {
     String clearString = "START_DATE_Q;END_DATE_Q;ADM_TYPE;ORG_CODE;MR_NO;";
     clearValue(clearString);
     Timestamp date = SystemTool.getInstance().getDate();
     
     // ��ʼ����ѯ����
     this.setValue("END_DATE_Q",
                   date.toString().substring(0, 10).replace('-', '/') +
                   " 23:59:59");
     this.setValue("END_DATE",
	               date.toString().substring(0, 10).replace('-', '/') +
	               " 23:59:59");
     this.setValue("START_DATE_Q",
                   StringTool.rollDate(date, -7).toString().substring(0, 10).
                   replace('-', '/') + " 00:00:00");
     this.setValue("START_DATE",
             	   StringTool.rollDate(date, -7).toString().substring(0, 10).
                   replace('-', '/') + " 00:00:00");
     table.removeRowAll();
   }
 
   /**
    * ��ӡ
    */
   public void onPrint(){
	  TTable table = getTable("TABLE");
	  table.acceptText();
	    if (table.getRowCount() <= 0) {
	       messageBox("û�д�ӡ����");
	       return;
	    }
	    TParm tableParm = table.getShowParmValue();
	    int count = 0;
     if("I".equals(getComboBox("ADM_TYPE").getText())){
    	 executePrint2(tableParm,count);
     }else{
    	 executePrint1(tableParm,count);
     }
     
   }
   
   /**
    * 
    * סԺ��ӡ
    */
   public void executePrint2(TParm tableParm ,int count){
	     for (int i = 0; i < tableParm.getCount(); i++){
	       if (tableParm.getBoolean("XUAN", i)){
	         count++;
	         
	         String mr_no = tableParm.getValue("MR_NO", i);
	         String syssql = getSys(mr_no);
	         //String memsql = getMem(mr_no);
	         String memins= getMemIns(mr_no);
	         String admsql=getAdm(mr_no);
	         TParm parm = new TParm();
	         //���ݲ����Ŵ�SYS_PATINFO�л������
	         TParm sysParm = new TParm(TJDODBTool.getInstance().select(syssql));
	         //���ݲ����Ŵ�MEM_PATINFO�л������
	         //TParm memParm = new TParm(TJDODBTool.getInstance().select(memsql));
	         //���ݲ����Ŵ�MEM_INSURE_INFO�л������
	         TParm meminsParm=new TParm(TJDODBTool.getInstance().select(memins));
	         //���ݲ����Ŵ�ADM_INP�л�ȡ����
	         TParm admParm=new TParm(TJDODBTool.getInstance().select(admsql));
	         //ģ������
	         //-------------------------------------------------------------------------------------------------
	         parm.setData("filePatName", "TEXT", tableParm.getValue("PAT_NAME", i));//����
	         parm.setData("fileSex", "TEXT", tableParm.getValue("SEX_CODE", i));//�Ա�
	         parm.setData("fileBirthday", "TEXT", getBirth(sysParm.getValue("BIRTH_DATE", 0)));//��������
	         parm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mr_no);//������
	         parm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", sysParm.getValue("IPD_NO", 0));//סԺ��
	         //-------------------------------------------------------------------------------------------------
	         //��������
	         parm.setData("MR_NO", "TEXT", mr_no);//������
	         parm.setData("IPD_NO", "TEXT", sysParm.getValue("IPD_NO", 0));//סԺ��
	         parm.setData("PAT_NAME", "TEXT", tableParm.getValue("PAT_NAME", i));//����
	         parm.setData("TEL_PHONE", "TEXT", sysParm.getValue("TEL_HOME", 0));//����
			 parm.setData("ID_TYPE", "TEXT", sysParm.getValue("ID_TYPE", 0));//֤������
			 parm.setData("ID_NO", "TEXT", tableParm.getValue("IDNO", i));//֤����
	         parm.setData("SEX", "TEXT", tableParm.getValue("SEX_CODE", i));//�Ա�
	         parm.setData("BIRTH_DATE", "TEXT", getBirth(sysParm.getValue("BIRTH_DATE", 0)));//��������
	         parm.setData("AGE", "TEXT", patAge(sysParm.getTimestamp("BIRTH_DATE", 0)));//����
	         parm.setData("PAY_TYPE", "TEXT", sysParm.getValue("CTZ1_CODE", 0));//���ʽ
	         parm.setData("HOMEPLACE_CODE", "TEXT", sysParm.getValue("HOMEPLACE_CODE", 0));//������
	         parm.setData("BIRTHPLACE", "TEXT", sysParm.getValue("BIRTHPLACE", 0));//����
			 parm.setData("NATION", "TEXT", tableParm.getValue("NATION_CODE", i));//����
			 parm.setData("SPECIES", "TEXT", tableParm.getValue("SPECIES_CODE", i));//����
	         parm.setData("MARRIAGE", "TEXT", sysParm.getValue("MARRIAGE_CODE", 0));//����
	         parm.setData("OCC_CODE", "TEXT", sysParm.getValue("OCC_CODE", 0));//ְҵ
	         parm.setData("COMPANY_DESC", "TEXT", sysParm.getValue("COMPANY_DESC", 0));//������λ
	         parm.setData("TEL_COMPANY", "TEXT", sysParm.getValue("TEL_COMPANY", 0));//��˾�绰
	         parm.setData("CELL_PHONE", "TEXT", sysParm.getValue("CELL_PHONE", 0));//�ֻ�
	         parm.setData("NOW_ADDRESS", "TEXT", sysParm.getValue("CURRENT_ADDRESS", 0));//��סַ
	         parm.setData("NOW_DETAIL_ADDRESS", "TEXT", sysParm.getValue("POST_CODE", 0));//��ס�ʱ�
	         parm.setData("ADDRESS", "TEXT", sysParm.getValue("RESID_ADDRESS", 0));//������ַ
	         parm.setData("DETAIL_ADDRESS", "TEXT", sysParm.getValue("RESID_POST_CODE", 0));//�����ʱ�
	         parm.setData("ADDRESS_COMPANY", "TEXT", sysParm.getValue("ADDRESS_COMPANY", 0));//��λסַ
	         parm.setData("POST_COMPANY", "TEXT", sysParm.getValue("POST_COMPANY", 0));//��λ�ʱ�
			 parm.setData("SPECIAL", "TEXT", sysParm.getValue("SPECIAL_DIET", 0));//������ʳ
	         parm.setData("CONTACTS_NAME", "TEXT", sysParm.getValue("CONTACTS_NAME", 0));//��ϵ��
	         parm.setData("RELATION_CODE", "TEXT", sysParm.getValue("RELATION_CODE", 0));//��ϵ
	         parm.setData("CONTACTS_TEL", "TEXT", sysParm.getValue("CONTACTS_TEL", 0));//��ϵ�˵绰
	         parm.setData("CONTACTS_ADDRESS", "TEXT", sysParm.getValue("CONTACTS_ADDRESS", 0));//��ϵ�˵�ַ
	         parm.setData("CTZ1", "TEXT", sysParm.getValue("CTZ1_CODE", 0));//���һ
	         parm.setData("CTZ2", "TEXT", sysParm.getValue("CTZ2_CODE", 0));//��ݶ�
	         parm.setData("CTZ3", "TEXT", sysParm.getValue("CTZ3_CODE", 0));//�����
	         parm.setData("CODE", "TEXT", mr_no);//����
	         //-----------------------------------------------------------------------------------------
	         parm.setData("ADM_SOURCE", "TEXT", admParm.getValue("ADM_SOURCE",0));//������Դ
	         parm.setData("OPD_DEPT_CODE", "TEXT", admParm.getValue("OPD_DEPT_CODE",0));//�ż������
	         parm.setData("OPD_DR_CODE", "TEXT", admParm.getValue("OPD_DR_CODE",0));//�ż���ҽ��
	         parm.setData("TOTAL_BILPAY", "TEXT", admParm.getValue("TOTAL_BILPAY",0));//����Ԥ����
	         parm.setData("LUMPWORK_CODE", "TEXT", admParm.getValue("LUMPWORK_DESC",0));//�ײ�����
	         parm.setData("PATIENT_CONDITION", "TEXT", admParm.getValue("PATIENT_CONDITION",0));//��Ժ״̬
	         parm.setData("SERVICE_LEVEL", "TEXT", admParm.getValue("SERVICE_LEVEL",0));//����ȼ�
	         parm.setData("MED_ID", "TEXT", admParm.getValue("MED_ID",0));//�������
	         parm.setData("IN_COUNT", "TEXT", admParm.getValue("IN_COUNT",0));//סԺ����
	         parm.setData("DEPT_CODE", "TEXT", admParm.getValue("DEPT_CODE",0));//סԺ�Ʊ�
	         parm.setData("STATION_CODE", "TEXT", admParm.getValue("STATION_CODE",0));//סԺ����
	         if(!"".equals(admParm.getValue("IN_DATE",0))){
	        	 parm.setData("IN_DATE", "TEXT", admParm.getValue("IN_DATE",0).replace('-', '/').substring(0, 10));//��Ժ����
	         }
	         else{
	        	 parm.setData("IN_DATE", "TEXT", "");//��Ժ����
	         }
	         parm.setData("VS_DR_CODE", "TEXT", admParm.getValue("VS_DR_CODE",0));//����ҽ��
	         parm.setData("BED_NO", "TEXT", admParm.getValue("BED_NO",0));//��λ����
	         parm.setData("YELLOW_SIGN", "TEXT", admParm.getValue("YELLOW_SIGN",0));//��ɫ��ʾ
	         parm.setData("RED_SIGN", "TEXT", admParm.getValue("RED_SIGN",0));//��ɫ��ʾ
	         parm.setData("AGN_CODE", "TEXT", admParm.getValue("AGN_INTENTION",0));//�ط��ȼ�
	         parm.setData("AGN_INTENTION", "TEXT", admParm.getValue("",0));//�ط�ԭ��
	         parm.setData("DISE_CODE", "TEXT", admParm.getValue("DISE_CODE",0));//������
	         //------------------------------------------------------------------------------------------
	         //������Ϣ
	         TParm result=new TParm();
	         if(meminsParm.getCount()>0){
		         for(int j=0;j<meminsParm.getCount();j++){
		        	 String start_date="";
		        	 String end_date="";
		        	 result.addData("CONTRACTOR_DESC",meminsParm.getValue("CONTRACTOR_DESC",j));//���յ�λ
		        	 result.addData("INSURANCE_NUMBER",meminsParm.getValue("INSURANCE_NUMBER",j));//���տ���
		        	 result.addData("INSURANCE_BILL_NUMBER",meminsParm.getValue("INSURANCE_BILL_NUMBER",j));//���յ���
		        	 result.addData("INSURE_PAY_TYPE",meminsParm.getValue("INSURE_PAY_TYPE",j));//֧������
		        	 //��Ч����
		        	 if(!"".equals(meminsParm.getValue("START_DATE",j)))
		        		 start_date=meminsParm.getValue("START_DATE",j).toString().substring(0,10).replace('-', '/');
		        	 result.addData("START_DATE",start_date);
		        	 //ʧЧ����
		        	 if(!"".equals(meminsParm.getValue("END_DATE",j)))
		        		 end_date=meminsParm.getValue("END_DATE",j).toString().substring(0,10).replace('-', '/');
		        	 result.addData("END_DATE",end_date);
		         }
		         result.setCount(result.getCount("CONTRACTOR_DESC"));
	        	 result.addData("SYSTEM", "COLUMNS","CONTRACTOR_DESC");//���յ�λ
	        	 result.addData("SYSTEM", "COLUMNS","INSURANCE_NUMBER");//���տ���
	        	 result.addData("SYSTEM", "COLUMNS","INSURANCE_BILL_NUMBER");//���յ���
	        	 result.addData("SYSTEM", "COLUMNS","INSURE_PAY_TYPE");//֧������
	        	 result.addData("SYSTEM", "COLUMNS","START_DATE");//��Ч����
	        	 result.addData("SYSTEM", "COLUMNS","END_DATE");//ʧЧ����
	         }else{
	        	 result.addData("CONTRACTOR_DESC","��");//���յ�λ
	        	 result.addData("INSURANCE_NUMBER","��");//���տ���
	        	 result.addData("INSURANCE_BILL_NUMBER","��");//���յ���
	        	 result.addData("INSURE_PAY_TYPE","��");//֧������
	        	 result.addData("START_DATE","��");//��Ч����
	        	 result.addData("END_DATE","��");//ʧЧ����
	        	 result.setCount(result.getCount("CONTRACTOR_DESC"));
	        	 result.addData("SYSTEM", "COLUMNS","CONTRACTOR_DESC");//���յ�λ
	        	 result.addData("SYSTEM", "COLUMNS","INSURANCE_NUMBER");//���տ���
	        	 result.addData("SYSTEM", "COLUMNS","INSURANCE_BILL_NUMBER");//���յ���
	        	 result.addData("SYSTEM", "COLUMNS","INSURE_PAY_TYPE");//֧������
	        	 result.addData("SYSTEM", "COLUMNS","START_DATE");//��Ч����
	        	 result.addData("SYSTEM", "COLUMNS","END_DATE");//ʧЧ����
	         }
	         parm.setData("TABLE",result.getData());
	         String previewSwitch=IReportTool.getInstance().getPrintSwitch("MEMPatInfoPrint_I_V45.previewSwitch");
	         if(previewSwitch.equals(IReportTool.ON)){
	        	 this.openPrintWindow(IReportTool.getInstance().getReportPath("MEMPatInfoPrint_I_V45.jhw"), 
	        			 IReportTool.getInstance().getReportParm("MEMPatInfoPrint_I_V45.class",parm));
	         }else{
	        	 this.openPrintWindow(IReportTool.getInstance().getReportPath("MEMPatInfoPrint_I_V45.jhw"), 
	        			 IReportTool.getInstance().getReportParm("MEMPatInfoPrint_I_V45.class",parm),true);
	         }
	       }
	     }
	     if (count == 0) {
		       this.messageBox("�빴ѡ");
		       return;
		 }
   }
   /**
    * �ż����ӡ
    */
   public void executePrint1(TParm tableParm, int count){
	     for (int i = 0; i < tableParm.getCount(); i++){
	       if (tableParm.getBoolean("XUAN", i)){
	         count++;
	         String mr_no = tableParm.getValue("MR_NO", i);
	         String syssql = getSys(mr_no);
	         String memsql = getMem(mr_no);
	         //String date=StringTool.getTimestamp(new Date()).toString().substring(0,19);
	         String memins= getMemIns(mr_no);
	         		//" AND TO_DATE('"+date+"','YYYY/MM/DD HH24:MI:SS') BETWEEN B.START_DATE AND B.END_DATE";
	         //System.out.println("-----------------------------------------789-----------"+memins);
	         TParm parm = new TParm();
	         //���ݲ����Ŵ�SYS_PATINFO�л������
	         TParm sysParm = new TParm(TJDODBTool.getInstance().select(syssql));
	         //���ݲ����Ŵ�MEM_PATINFO�л������
	         TParm memParm = new TParm(TJDODBTool.getInstance().select(memsql));
	         //���ݲ����Ŵ�MEM_INSURE_INFO�л������
	         TParm meminsParm=new TParm(TJDODBTool.getInstance().select(memins));
	         //ģ������
	         //----------------------------------------------------------------------------------------
	         parm.setData("filePatName", "TEXT", tableParm.getValue("PAT_NAME", i));//����
	         parm.setData("fileSex", "TEXT", tableParm.getValue("SEX_CODE", i));//�Ա�
	         parm.setData("fileBirthday", "TEXT",getBirth(sysParm.getValue("BIRTH_DATE", 0)));//��������
	         parm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mr_no);//������
	         parm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", sysParm.getValue("IPD_NO", 0));//סԺ��
	         //----------------------------------------------------------------------------------------
	         //��������
	         //----------------------------------------------------------------------------------------
	         parm.setData("MR_NO", "TEXT", mr_no);//������
	         parm.setData("PAT_NAME", "TEXT", tableParm.getValue("PAT_NAME", i));//����
	         parm.setData("SEX", "TEXT", tableParm.getValue("SEX_CODE", i));//�Ա�
	         parm.setData("FirstName", "TEXT", sysParm.getValue("FIRST_NAME", 0));//FirstName
	         parm.setData("LastName", "TEXT", sysParm.getValue("LAST_NAME", 0));//LastName
	         parm.setData("BIRTH_DATE", "TEXT", getBirth(sysParm.getValue("BIRTH_DATE", 0)));//��������
	         parm.setData("AGE", "TEXT", patAge(sysParm.getTimestamp("BIRTH_DATE", 0)));//����
			 parm.setData("KNOW_WAY", "TEXT", memParm.getValue("SOURCE", 0));//��÷�ʽ
			 parm.setData("SPECIAL", "TEXT", sysParm.getValue("SPECIAL_DIET", 0));//������ʳ
			 parm.setData("ID_TYPE", "TEXT", sysParm.getValue("ID_TYPE", 0));//֤������
			 parm.setData("ID_NO", "TEXT", tableParm.getValue("IDNO", i));//֤����
			 parm.setData("NATION", "TEXT", tableParm.getValue("NATION_CODE", i));//����
			 parm.setData("SPECIES", "TEXT", tableParm.getValue("SPECIES_CODE", i));//����
			 parm.setData("RELIGION", "TEXT", sysParm.getValue("RELIGION_CODE", 0));//�ڽ�
	         parm.setData("MARRIAGE", "TEXT", sysParm.getValue("MARRIAGE_CODE", 0));//����
	         parm.setData("BIRTHPLACE", "TEXT", sysParm.getValue("HOMEPLACE_CODE", 0));//������
	         parm.setData("ADDRESS", "TEXT", sysParm.getValue("RESID_POST_CODE_O", 0));//������ַ
	         parm.setData("DETAIL_ADDRESS", "TEXT", sysParm.getValue("RESID_ADDRESS", 0));//��ϸ��ַ
	         parm.setData("NOW_ADDRESS", "TEXT", sysParm.getValue("POST_CODE_O", 0));//��סַ
	         parm.setData("NOW_DETAIL_ADDRESS", "TEXT", sysParm.getValue("CURRENT_ADDRESS", 0));//��ϸ��ַ
	         parm.setData("CELL_PHONE", "TEXT", sysParm.getValue("CELL_PHONE", 0));//�ֻ�
	         parm.setData("TEL_PHONE", "TEXT", sysParm.getValue("TEL_HOME", 0));//����
	         parm.setData("EMAIL", "TEXT", sysParm.getValue("E_MAIL", 0));//Email
	         parm.setData("BIRTH_HOSPITAL", "TEXT", memParm.getValue("BIRTH_HOSPITAL", 0));//����ҽԺ
	         parm.setData("SCHOOL", "TEXT", memParm.getValue("SCHOOL_NAME", 0));//ѧУ
	         parm.setData("SCHOOL_PHONE", "TEXT", memParm.getValue("SCHOOL_TEL", 0));//У���绰
	         TParm result=new TParm();
	         if(meminsParm.getCount()>0){
		         for(int j=0;j<meminsParm.getCount();j++){
		        	 String start_date="";
		        	 String end_date="";
		        	 result.addData("CONTRACTOR_DESC",meminsParm.getValue("CONTRACTOR_DESC",j));//���յ�λ
		        	 result.addData("INSURANCE_NUMBER",meminsParm.getValue("INSURANCE_NUMBER",j));//���տ���
		        	 result.addData("INSURANCE_BILL_NUMBER",meminsParm.getValue("INSURANCE_BILL_NUMBER",j));//���յ���
		        	 result.addData("INSURE_PAY_TYPE",meminsParm.getValue("INSURE_PAY_TYPE",j));//֧������
		        	 //��Ч����
		        	 if(!"".equals(meminsParm.getValue("START_DATE",j)))
		        		 start_date=meminsParm.getValue("START_DATE",j).toString().substring(0,10).replace('-', '/');
		        	 result.addData("START_DATE",start_date);
		        	 //ʧЧ����
		        	 if(!"".equals(meminsParm.getValue("END_DATE",j)))
		        		 end_date=meminsParm.getValue("END_DATE",j).toString().substring(0,10).replace('-', '/');
		        	 result.addData("END_DATE",end_date);
		         }
		         result.setCount(result.getCount("CONTRACTOR_DESC"));
	        	 result.addData("SYSTEM", "COLUMNS","CONTRACTOR_DESC");//���յ�λ
	        	 result.addData("SYSTEM", "COLUMNS","INSURANCE_NUMBER");//���տ���
	        	 result.addData("SYSTEM", "COLUMNS","INSURANCE_BILL_NUMBER");//���յ���
	        	 result.addData("SYSTEM", "COLUMNS","INSURE_PAY_TYPE");//֧������
	        	 result.addData("SYSTEM", "COLUMNS","START_DATE");//��Ч����
	        	 result.addData("SYSTEM", "COLUMNS","END_DATE");//ʧЧ����
	         }else{
	        	 result.addData("CONTRACTOR_DESC","��");//���յ�λ
	        	 result.addData("INSURANCE_NUMBER","��");//���տ���
	        	 result.addData("INSURANCE_BILL_NUMBER","��");//���յ���
	        	 result.addData("INSURE_PAY_TYPE","��");//֧������
	        	 result.addData("START_DATE","��");//��Ч����
	        	 result.addData("END_DATE","��");//ʧЧ����
	        	 result.setCount(result.getCount("CONTRACTOR_DESC"));
	        	 result.addData("SYSTEM", "COLUMNS","CONTRACTOR_DESC");//���յ�λ
	        	 result.addData("SYSTEM", "COLUMNS","INSURANCE_NUMBER");//���տ���
	        	 result.addData("SYSTEM", "COLUMNS","INSURANCE_BILL_NUMBER");//���յ���
	        	 result.addData("SYSTEM", "COLUMNS","INSURE_PAY_TYPE");//֧������
	        	 result.addData("SYSTEM", "COLUMNS","START_DATE");//��Ч����
	        	 result.addData("SYSTEM", "COLUMNS","END_DATE");//ʧЧ����
	         }
	         parm.setData("TABLE",result.getData());
	         parm.setData("CONTACT1", "TEXT", memParm.getValue("GUARDIAN1_NAME", 0));//�໤��/��ϵ��1
	         parm.setData("RELATION1", "TEXT", memParm.getValue("GUARDIAN1_RELATION", 0));//��ϵ
	         parm.setData("CONTACT_WAY1", "TEXT", memParm.getValue("GUARDIAN1_TEL", 0));//��ϵ��ʽ
	         parm.setData("CELL_NO1", "TEXT", memParm.getValue("GUARDIAN1_PHONE", 0));//�ֻ�
	         parm.setData("WORK_COMPANY1", "TEXT", memParm.getValue("GUARDIAN1_COM", 0));//������λ
	         parm.setData("ID_TYPE1", "TEXT", memParm.getValue("GUARDIAN1_ID_TYPE", 0));//֤������
	         parm.setData("ID_NO1", "TEXT", memParm.getValue("GUARDIAN1_ID_CODE", 0));//֤������
	         parm.setData("Email_1", "TEXT", memParm.getValue("GUARDIAN1_EMAIL", 0));//Email
	         parm.setData("CONTACT2", "TEXT", memParm.getValue("GUARDIAN2_NAME", 0));//�໤��/��ϵ��2
	         parm.setData("RELATION2", "TEXT", memParm.getValue("GUARDIAN2_RELATION", 0));//��ϵ
	         parm.setData("CONTACT_WAY2", "TEXT", memParm.getValue("GUARDIAN2_TEL", 0));//��ϵ��ʽ
	         parm.setData("CELL_NO2", "TEXT", memParm.getValue("GUARDIAN2_PHONE", 0));//�ֻ�
	         parm.setData("WORK_COMPANY2", "TEXT", memParm.getValue("GUARDIAN2_COM", 0));//������λ
	         parm.setData("ID_TYPE2", "TEXT", memParm.getValue("GUARDIAN2_ID_TYPE", 0));//֤������
	         parm.setData("ID_NO2", "TEXT", memParm.getValue("GUARDIAN2_ID_CODE", 0));//֤������
	         parm.setData("Email_2", "TEXT", memParm.getValue("GUARDIAN2_EMAIL", 0));//Email
	         parm.setData("CTZ1", "TEXT", sysParm.getValue("CTZ1_CODE", 0));//���һ
	         parm.setData("CTZ2", "TEXT", sysParm.getValue("CTZ2_CODE", 0));//��ݶ�
	         parm.setData("CTZ3", "TEXT", sysParm.getValue("CTZ3_CODE", 0));//�����
	         parm.setData("MEMBER", "TEXT", memParm.getValue("MEM_DESC", 0));//��Ա����
	         parm.setData("MEM_FEE", "TEXT", memParm.getValue("MEM_FEE", 0));//��Ա����
	         //��Ч����
	         String start_date = "";
	         if (!"".equals(memParm.getValue("START_DATE", 0)))
	           start_date = memParm.getValue("START_DATE", 0).toString().substring(0, 10).replace('-', '/');
	         parm.setData("START_DATE", "TEXT", start_date);
	         //ʧЧ����
	         String end_date = "";
	         if (!"".equals(memParm.getValue("END_DATE", 0)))
	           end_date = memParm.getValue("END_DATE", 0).toString().substring(0, 10).replace('-', '/');
	         parm.setData("END_DATE", "TEXT", end_date);
	         parm.setData("STAFF1", "TEXT", memParm.getValue("INTRODUCER1", 0));//������Ա1
	         parm.setData("STAFF2", "TEXT", memParm.getValue("INTRODUCER2", 0));//������Ա2
	         parm.setData("HOME_DOCTOR", "TEXT", memParm.getValue("FAMILY_DOCTOR", 0));//��ͥҽ��
	         parm.setData("ACCOUNT_MANAGER", "TEXT", memParm.getValue("ACCOUNT_MANAGER_NAME", 0));//�ͻ�����
	         parm.setData("CODE", "TEXT", mr_no);//����

//	         //2, 11  .IMAGE_TYPE
//	         word.setWordParameter(null);
//	 		 word.getWordText().getPM().setStyleManager(new MStyle(true));
//	 		 word.getWordText().getPM().getFileManager().onNewFile();
//	 		 TParm prtParm = new TParm();
//	 		 // ����TWord
//	 		 word.setWordParameter(prtParm);
//	 		 word.setPreview(true);
//	        
//	         word.setWordParameter(parm);
//	         word.setFileName("%ROOT%\\config\\prt\\MEM\\MEMPatInfoPrintControl.jhw");
	         
	         
	        /* VPic pic =(VPic)word.findObject("photo", EComponent.IMAGE_TYPE);
	         System.out.println("------word-----"+word);
	         System.out.println("------pic------"+pic);
	         pic.setPictureName("%PATPIC%"+no);*/
	         String previewSwitch=IReportTool.getInstance().getPrintSwitch("MEMPatInfoPrint_V45.previewSwitch");
	         if(previewSwitch.equals(IReportTool.ON)){
	        	 this.openPrintWindow(IReportTool.getInstance().getReportPath("MEMPatInfoPrint_V45.jhw"), 
	        			 IReportTool.getInstance().getReportParm("MEMPatInfoPrint_V45.class",parm));
	         }else{
	        	 this.openPrintWindow(IReportTool.getInstance().getReportPath("MEMPatInfoPrint_V45.jhw"), 
	        			 IReportTool.getInstance().getReportParm("MEMPatInfoPrint_V45.class",parm),true);
	         }
	       }
	     }
	     if (count == 0) {
		       this.messageBox("�빴ѡ");
		       return;
		 }
   }
   /**
    * ��������
    * @param date
    * @return
    */
   private String patAge(Timestamp date){
	   Timestamp sysDate = SystemTool.getInstance().getDate();
       Timestamp temp = date == null ? sysDate : date;
       String age = "0";
       age = DateUtil.showAge(temp, sysDate);
       return age;
   }
   /**
    * ������������
    * @param birth
    * @return
    */
   private String getBirth(String birth){
	   String birth_date="";
       if(!"".equals(birth))
      	 birth_date=birth.toString().substring(0, 10).replace('-', '/');
       return birth_date;
       
   }
   /**
    * ��ȡsql
    * @param mrno
    * @return
    */
   private String getAdm(String mrno){
	   String admsql="SELECT (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='ADM_SOURCE' AND ID=A.ADM_SOURCE) AS ADM_SOURCE," +
		" (SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE=A.OPD_DEPT_CODE) AS OPD_DEPT_CODE," +
 		" (SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID=A.OPD_DR_CODE) AS OPD_DR_CODE,A.TOTAL_BILPAY," +
 		" (SELECT LUMPWORK_DESC FROM MEM_LUMPWORK WHERE A.LUMPWORK_CODE=LUMPWORK_CODE) AS LUMPWORK_DESC," +
 		" (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='ADM_CONDITION' AND ID=A.PATIENT_CONDITION) AS PATIENT_CONDITION," +
 		" (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SERVICE_LEVEL' AND ID=A.SERVICE_LEVEL) AS SERVICE_LEVEL,A.IN_COUNT," +
 		" (SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE=A.DEPT_CODE) AS DEPT_CODE," +
 		" (SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE=A.STATION_CODE) AS STATION_CODE," +
 		" IN_DATE,(SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID=A.VS_DR_CODE) AS VS_DR_CODE,A.BED_NO,A.YELLOW_SIGN," +
 		" A.RED_SIGN,(SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='AGN_CODE' AND ID=A.AGN_CODE) AS AGN_CODE,A.AGN_INTENTION," +
 		" (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='CLP_SIN_DISE' AND ID=A.DISE_CODE) AS DISE_CODE, " +
 		" (SELECT CTZ_DESC FROM SYS_CTZ WHERE CTZ_CODE=A.CTZ1_CODE) AS MED_ID" +
 		" FROM ADM_INP A" +
 		" WHERE MR_NO='"+mrno+"'";
	   //System.out.println("----admsql----"+admsql);
	   return admsql;
   }
   /**
    * ��ȡsql
    * @param mrno
    * @return
    */
   private String getSys(String mrno){
	   String syssql="SELECT A.IPD_NO,A.FIRST_NAME,A.LAST_NAME,A.BIRTH_DATE,(SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SPECIALDIET' AND ID=A.SPECIAL_DIET) AS SPECIAL_DIET, " +
		" (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELIGION' AND ID=A.RELIGION_CODE) AS RELIGION_CODE, " +
 		" (select CHN_DESC from SYS_DICTIONARY WHERE GROUP_ID='SYS_IDTYPE' AND ID=A.ID_TYPE) AS ID_TYPE ," +
 		"  A.IDNO,(SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELIGION' AND ID=A.SPECIES_CODE) AS SPECIES_CODE,  " +
 		" (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_MARRIAGE' AND ID=A.MARRIAGE_CODE) AS MARRIAGE_CODE, " +
 		" (SELECT HOMEPLACE_DESC FROM SYS_HOMEPLACE WHERE HOMEPLACE_CODE=A.HOMEPLACE_CODE) AS HOMEPLACE_CODE, " +
 		" (SELECT HOMEPLACE_DESC FROM SYS_HOMEPLACE WHERE HOMEPLACE_CODE=A.BIRTHPLACE) AS BIRTHPLACE, " +
 		" (SELECT POST_DESCRIPTION FROM SYS_POSTCODE WHERE POST_CODE=A.RESID_POST_CODE) AS RESID_POST_CODE_O,A.RESID_ADDRESS, " +
 		" (SELECT POST_DESCRIPTION FROM SYS_POSTCODE WHERE POST_CODE=A.POST_CODE) AS POST_CODE_O,A.CURRENT_ADDRESS,A.CELL_PHONE,A.TEL_HOME, " +
 		" (SELECT CTZ_DESC FROM SYS_CTZ WHERE CTZ_CODE=A.CTZ1_CODE) AS CTZ1_CODE,  (SELECT CTZ_DESC FROM SYS_CTZ WHERE CTZ_CODE=A.CTZ2_CODE) AS CTZ2_CODE, " +
 		" (SELECT CTZ_DESC FROM SYS_CTZ WHERE CTZ_CODE=A.CTZ3_CODE) AS CTZ3_CODE,   A.E_MAIL, " +
 		" (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_OCCUPATION' AND ID=A.OCC_CODE) AS OCC_CODE,A.COMPANY_DESC,A.TEL_COMPANY," +
 		" A.RESID_POST_CODE,A.POST_CODE,A.ADDRESS_COMPANY, A.POST_COMPANY, " +
 		" A.CONTACTS_NAME,A.CONTACTS_ADDRESS,A.CONTACTS_TEL,(SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELATIONSHIP' AND ID=A.RELATION_CODE) AS RELATION_CODE"+
 		" FROM SYS_PATINFO A" +
 		" WHERE A.MR_NO='" + mrno + "'" ;
	   //System.out.println("-----sql==="+syssql);
	   return syssql;
   }
   
   /**
    * ��ȡsql
    * @param mrno
    * @return
    */
   private String getMem(String mrno){
	   String memsql= "SELECT A.SCHOOL_NAME,A.SCHOOL_TEL,A.INSURANCE_COMPANY1_NAME  INSURANCE_NUMBER1,A.INSURANCE_COMPANY2_NAME,A.INSURANCE_NUMBER2, " +
		" A.GUARDIAN1_NAME, (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELATIONSHIP' AND ID=A.GUARDIAN1_RELATION) AS GUARDIAN1_RELATION," +
 		" A.GUARDIAN1_TEL,A.GUARDIAN1_PHONE,A.GUARDIAN1_COM," +
 		" (select CHN_DESC from SYS_DICTIONARY WHERE GROUP_ID='SYS_IDTYPE' AND ID=A.GUARDIAN1_ID_TYPE) AS GUARDIAN1_ID_TYPE," +
 		" A.GUARDIAN1_ID_CODE,A.GUARDIAN1_EMAIL, A.GUARDIAN2_NAME, " +
 		" (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELATIONSHIP' AND ID=A.GUARDIAN2_RELATION) AS GUARDIAN2_RELATION, " +
 		" A.GUARDIAN2_TEL,A.GUARDIAN2_PHONE,A.GUARDIAN2_COM," +
 		" (select CHN_DESC from SYS_DICTIONARY WHERE GROUP_ID='SYS_IDTYPE' AND ID=A.GUARDIAN2_ID_TYPE) AS GUARDIAN2_ID_TYPE," +
 		" A.GUARDIAN2_ID_CODE,A.GUARDIAN2_EMAIL, " +
 		" A.MEM_DESC,A.START_DATE,A.END_DATE," +
 		" (SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID=A.FAMILY_DOCTOR) AS FAMILY_DOCTOR," +
 		" A.ACCOUNT_MANAGER_NAME," +
 		" (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='MEM_SOURCE' AND ID=A.SOURCE) AS SOURCE," +
 		" (SELECT MEM_FEE FROM MEM_MEMBERSHIP_INFO WHERE MEM_CODE =A.MEM_CODE ) AS MEM_FEE, " +
 		" (SELECT C.USER_NAME FROM MEM_TRADE B,SYS_OPERATOR C WHERE B.MR_NO=A.MR_NO AND B.START_DATE=A.START_DATE AND B.END_DATE=A.END_DATE AND B.INTRODUCER1=C.USER_ID) AS INTRODUCER1, " +
 		" (SELECT C.USER_NAME FROM MEM_TRADE B,SYS_OPERATOR C WHERE B.MR_NO=A.MR_NO AND B.START_DATE=A.START_DATE AND B.END_DATE=A.END_DATE AND B.INTRODUCER2=C.USER_ID) AS INTRODUCER2 " +
 		" FROM MEM_PATINFO A, MEM_TRADE B, SYS_OPERATOR C WHERE A.MR_NO='" + mrno + "'";
	   return memsql;
   }
   
   /**
    * ��ȡsql
    * @param mrno
    * @return
    */
   private String getMemIns(String mrno){
	   String memins="SELECT A.CONTRACTOR_DESC,B.INSURANCE_NUMBER," +
		" (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='MEM_INSURE_PAY_TYPE' AND ID=B.INSURE_PAY_TYPE ) AS INSURE_PAY_TYPE ," +
 		" B.START_DATE,B.END_DATE,B.INSURANCE_BILL_NUMBER FROM MEM_CONTRACTOR A, " +
 		" MEM_INSURE_INFO B WHERE MR_NO='"+mrno+"' AND A.CONTRACTOR_CODE=B.CONTRACTOR_CODE" +
 		" AND VALID_FLG='Y'" ;
	   return memins;
   }
   
 /**
  * ��ѡ��ı��¼�
  */
   public void onChangeRadioButton() {
     if (getRadioButton("IS_FIRST").isSelected()) {
	       getTextFormat("START_DATE_Q").setEnabled(true);
	       getTextFormat("END_DATE_Q").setEnabled(true);
	       getComboBox("ADM_TYPE").setEnabled(true);
	       getTextFormat("ORG_CODE").setEnabled(true);
	       getRadioButton("FIRST").setEnabled(true);
	       getRadioButton("DOUBLE").setEnabled(true);
	       getRadioButton("UPDATE_FLG").setEnabled(false);
	       getRadioButton("MODIFY_FLG").setEnabled(false);
	       getTextFormat("START_DATE").setEnabled(false);
	       getTextFormat("END_DATE").setEnabled(false);
	       onClear();
	       
     }
     else {
	       getTextFormat("START_DATE").setEnabled(true);
	       getTextFormat("END_DATE").setEnabled(true);
	       getRadioButton("UPDATE_FLG").setEnabled(true);
	       getRadioButton("MODIFY_FLG").setEnabled(true);
	       getTextFormat("START_DATE_Q").setEnabled(false);
	       getTextFormat("END_DATE_Q").setEnabled(false);
	       getComboBox("ADM_TYPE").setEnabled(false);
	       getTextFormat("ORG_CODE").setEnabled(false);
	       getRadioButton("FIRST").setEnabled(false);
	       getRadioButton("DOUBLE").setEnabled(false);
	       onClear();
	       
     }
   }
	 /**
	  * ��ѡ��ı��¼�
	  */
	   public void onChangeRadioButton1(){
		   	table.removeRowAll();
	   }
	   
	 /**
	  * ���ѡ��סԺ������Ҫ�ı��ѯ��ʱ��
	  * 
	  */
	   public boolean isAdm(){
		   if("I".equals(getComboBox("ADM_TYPE").getText())){
			   this.setValue("SELECT_TIME", "��Ժʱ�䣺");
			   callFunction("UI|FIRST|setEnabled",false);
			   callFunction("UI|DOUBLE|setEnabled",false);
			   return true;
		   }else{
			   this.setValue("SELECT_TIME", "�Һ�ʱ�䣺");
			   callFunction("UI|FIRST|setEnabled",true);
			   callFunction("UI|DOUBLE|setEnabled",true);
			   return false;
		   }
			   
	   }
	   
	 /**
	  * ���TRadioButton
	  * @param tagName
	  * @return
	  */
	   public TRadioButton getRadioButton(String tagName){
	     return (TRadioButton)getComponent(tagName);
	  }
	 /**
	  * ���TComboBox
	  * @param tagName
	  * @return
	  */
	   public TComboBox getComboBox(String tagName){
	     return (TComboBox)getComponent(tagName);
	   }
	 /**
	  * ���TTextFormat
	  * @param tagName
	  * @return
	  */
	   public TTextFormat getTextFormat(String tagName){
	     return (TTextFormat)getComponent(tagName);
	   }
	 /**
	  * ���table
	  * @param tagName
	  * @return
	  */
	   public TTable getTable(String tagName){
	     return (TTable)getComponent(tagName);
	  }
	}
	 