package com.javahis.ui.mem;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.bil.PaymentTool;
import jdo.mem.MEMInsureQueryTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>
 * Title: ���ջ�����Ϣ��ѯ
 * </p>
 * <p>
 * Company:Javahis
 * </p> 
 * @author liling
 * 2014-07-09
 */
public class MEMInsureQueryControl extends TControl{
	private static TTable table;
	PaymentTool paymentTool;
	private Pat  pat;//  ������Ϣ
	int selectRow = -1;
	
	private BILComparator comparator=new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;
	
	public MEMInsureQueryControl(){
		super();
		selectRow = -1;
	}
	 /**
     * ��ʼ��
     */
    public void onInit(){
    	 super.onInit();
    	table = (TTable) getComponent("TABLE");     	
    	onInitTable();    	
    	table.addEventListener("TABLE->"
	            + TTableEvent.CLICKED, this, "onTABLEClicked");
    	addListener(table);
    	TPanel p = (TPanel) getComponent("tPanel_1");
    	try {
			paymentTool = new PaymentTool(p, this);
			paymentTool.setHasAmt(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//onQuery();
    }
    
    /**
     * ��ʼ�����  add by huangtt 20150521
     */
    public void onInitTable(){
    	this.setValue("ADM_TYPE", "O");
    	admTypeAction();
		
		
		
	}
    
    public void admTypeAction(){
    	table.removeRowAll();
    	String admType=this.getValueString("ADM_TYPE");
    	String header="";
    	String column="";
    	String parmMap="";
    	TLabel tLabel = (TLabel) this.getComponent("tLabel_6");
    	if(!admType.equals("I")){
    		tLabel.setText("��������:");
    		header = "Ƿ��,50,boolean;Ƿ�ѽ��,100,double,#########0.00;������,80;����,100;" +
			"�Ա�,40,SEX_CODE;��������,120,Timestamp,yyyy/MM/dd;���չ�˾,100,CONTRACTOR_CODE;���տ���,100;���յ���,100;" +
			"֧������,90,INSURE_PAY_TYPE;��Ч��,90,Timestamp,yyyy/MM/dd;ʧЧ��,90,Timestamp,yyyy/MM/dd;" +
			"��������,90,Timestamp,yyyy/MM/dd;�������,100,REALDEPT_CODE;����ҽ��,100,REALDR_CODE;�����,150,ICD_CODE;" +
			"��������,150,Timestamp,yyyy/MM/dd HH:mm:ss;������,100,double,#########0.00";
	    	column="1,right;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,left;17,right";
	    	parmMap ="DEPT_FLG;AMT;MR_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;CONTRACTOR_CODE;INSURANCE_NUMBER;" +
					"INSURANCE_BILL_NUMBER;INSURE_PAY_TYPE;START_DATE;END_DATE;ADM_DATE;REALDEPT_CODE;REALDR_CODE;ICD_CODE;" +
					"BILL_DATE;AR_AMT";
			
			String sql = "SELECT A.GATHER_TYPE,B.CHN_DESC,A.PAYTYPE FROM BIL_GATHERTYPE_PAYTYPE A,SYS_DICTIONARY B" +
					" WHERE B.GROUP_ID = 'GATHER_TYPE' AND A.GATHER_TYPE = B.ID";
			TParm payType = new TParm(TJDODBTool.getInstance().select(sql));
			for (int i = 0; i < payType.getCount(); i++) {
				header =header + ";" +payType.getValue("CHN_DESC", i)+",100,double,#########0.00";
				column = column + ";"+(18+i)+",right";
				parmMap = parmMap + ";" + payType.getValue("PAYTYPE", i);
			}
    	}else{
    		tLabel.setText("��Ժ����:");
    		header = "Ƿ��,50,boolean;Ƿ�ѽ��,100,double,#########0.00;������,80;����,100;" +
			"�Ա�,40,SEX_CODE;��������,120,Timestamp,yyyy/MM/dd;���չ�˾,100,CONTRACTOR_CODE;���տ���,100;���յ���,100;" +
			"֧������,90,INSURE_PAY_TYPE;��Ч��,90,Timestamp,yyyy/MM/dd;ʧЧ��,90,Timestamp,yyyy/MM/dd;" +
			"��Ժ����,90,Timestamp,yyyy/MM/dd;��Ժ����,100,REALDEPT_CODE;����ҽ��,100,REALDR_CODE;��Ժ���,150,ICD_CODE;" +
			"��������,150,Timestamp,yyyy/MM/dd HH:mm:ss;סԺ����,60;������,100,double,#########0.00;�ֽ�,100,double,#########0.00;" +
			"ҽ�ƿ�,100,double,#########0.00;���п�,100,double,#########0.00;ҽ����,100,double,#########0.00;" +
			"֧Ʊ,100,double,#########0.00;Ӧ�ս��,100,double,#########0.00;Ԥ����,100,double,#########0.00;" +
			"���ڽ��,100,double,#########0.00;������,100,double,#########0.00;������,100,double,#########0.00";
			column="1,right;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,left;" +
					"17,right;18,right;19,right;20,right;21,right;22,right;23,right;24,right;25,right;26,right;27,right;28,right";
			parmMap ="DEPT_FLG;AMT;MR_NO;PAT_NAME;" +
					"SEX_CODE;BIRTH_DATE;CONTRACTOR_CODE;INSURANCE_NUMBER;INSURANCE_BILL_NUMBER;" +
					"INSURE_PAY_TYPE;START_DATE;END_DATE;" +
					"IN_DATE;REALDEPT_CODE;REALDR_CODE;ICD_CODE;" +
					"BILL_DATE;ADM_DAYS;AR_AMT;PAY_CASH;" +
					"PAY_MEDICAL_CARD;PAY_BANK_CARD;PAY_INS_CARD;" +
					"PAY_CHECK;PAY_DEBIT;PAY_BILPAY;" +
					"LUMPWORK_AMT;LUMPWORK_OUT_AMT;REDUCE_AMT";
    	}
    	
    	table.setHeader(header);
		table.setItem("CONTRACTOR_CODE;SEX_CODE;INSURE_PAY_TYPE;REALDEPT_CODE;REALDR_CODE");
		table.setParmMap(parmMap);
		table.setColumnHorizontalAlignmentData(column);
		
		onQuery();
    }
    
  
    /**
	 * ��ѯ
	 */
	public void onQuery(){
		if(this.getValue("ADM_TYPE").toString().equals("")){
			this.messageBox("�ż�ס�𲻿�Ϊ��");
			return;
		}
		table.acceptText();
		 TParm parm = getParmForTag("START_DATE;END_DATE;ADM_TYPE;CONTRACTOR_CODE;INSURANCE_NUMBER;MR_NO;DEPT_FLG", true);
//		 TParm data = MEMInsureQueryTool.getInstance().selectdata(parm);
//		 System.out.println("parm=="+parm);
		 TParm data=new TParm();
		 if(!this.getValue("ADM_TYPE").toString().equals("I")){
	     	 data = select(parm);
		 }else{
			 data = selectI(parm);
		 }
	        if (data.getErrCode() < 0) {
	            messageBox(data.getErrText());
	            return;
	        }
	        
	     table.setParmValue(data);
	       
//	     ((TTextField) getComponent("LUMPWORK_CODE")).setEnabled(true);	
	}
	public void onQueryNO(){
    	pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
        if (pat == null) {
            this.messageBox("�޴˲�����!");
            this.grabFocus("PAT_NAME");
            return;
        }
        String mrNo=PatTool.getInstance().checkMrno(TypeTool.getString(getValue("MR_NO")));
        this.setValue("MR_NO", mrNo);
        onQuery();
        }
	/**
	 * ����
	 */
	public void onSave(){
		TParm parm = getParmForTag("CONTRACTOR_CODE;INSURANCE_NUMBER;MR_NO;DEPT_FLG");		
		if(selectRow == -1){
			this.messageBox("û��ѡ������");
			return;
		}
		//System.out.println(selectRow );
		TParm parm11= new TParm();
		try {
			parm11 = paymentTool.getAmts();
//			System.out.println("parm11======="+parm11);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int j=1;j<11;j++){
				parm.setData("PAY_TYPE0"+j, "");
				parm.setData("MEMO"+j, "");
		}		
		for(int i=0;i<parm11.getCount();i++){
			parm.setData(parm11.getValue("PAY_TYPE", i), parm11.getData("AMT", i));
			String dex=parm11.getValue("PAY_TYPE", i).substring(9, parm11.getValue("PAY_TYPE", i).length());
			parm.setData("MEMO"+dex, parm11.getData("REMARKS", i));
		}
		TParm payParm = paymentTool.table.getParmValue();
    	int payCount = payParm.getCount();
    	String payType = "";
    	String memo ="";
    	double amt = 0.00;
    	double tot =0.00;
    	double totamt= 0.00;  	
    	for (int i = 0; i <= payCount; i++) {
    		payType = paymentTool.table.getItemString(i, "PAY_TYPE");
			amt = paymentTool.table.getItemDouble(i, "AMT");
			if(amt > 0 && ("".equals(payType)||payType==null)){
				this.messageBox("����δ�趨֧����ʽ�Ľ��,����д��");
				return;
			}
			tot += paymentTool.table.getItemDouble(i, "AMT");
//			totamt= this.getValueDouble("AMT")-tot;
//			 if(totamt<0){
//	        	 this.messageBox("����Ӧ�ս��");
//	        	 paymentTool.table.setItem(i, "AMT", amt+totamt);
//	        	 return;
//	         }
//			for(int j=0;j<parm0.getCount();j++){
//				payType0=parm0.getValue("GATHER_TYPE", j);
//				if(payType.equals(payType0)){
//					parm.setData(parm0.getValue("PAYTYPE", j),amt);
//					parm.setData("MEMO"+j,memo);					
//			}
//		}   
			 }
//    	if(tot<=0){
//    		this.messageBox("û�нɷ�");
//    		return;
//    	}
    	 totamt= this.getValueDouble("AMT")-tot;    	
    	 TParm parm1 = new TParm(TJDODBTool.getInstance().select("SELECT COUNT(*) AS COUNT FROM MEM_INSURANCE_BILL_LOG"));
    	 parm.setData("ID",Integer.parseInt(parm1.getValue("COUNT", 0)));
    	 parm.setData("OPT_USER", Operator.getID());
         parm.setData("OPT_DATE", SystemTool.getInstance().getDate().toString().substring(0, 19));
         parm.setData("OPT_TERM", Operator.getIP());
         parm.setData("BILL_AMT",tot);        
         TParm result = MEMInsureQueryTool.getInstance().insertdata(parm);
         if (result.getErrCode() < 0) {
             messageBox(result.getErrText());
             return;
         }        
         this.messageBox("P0001");
         memo=this.getValueString("MEMO");
         String sql1="";
         String sql2="";
         if(totamt>0){
	          sql1="UPDATE MEM_INSURE_INFO SET DEPT_FLG='"+parm.getValue("DEPT_FLG")+"', MEMO='"
				 +memo+"',AMT="+totamt+" WHERE MR_NO='"+parm.getValue("MR_NO")+
				 "' AND CONTRACTOR_CODE='"+parm.getValue("CONTRACTOR_CODE")+"' " +
				 "AND INSURANCE_NUMBER='"+parm.getValue("INSURANCE_NUMBER")+"'";
	          sql2="UPDATE SYS_PATINFO SET  DEPT_FLG='"+parm.getValue("DEPT_FLG")+"' WHERE MR_NO='"+parm.getValue("MR_NO")+"'";
         }else{
        	  sql1="UPDATE MEM_INSURE_INFO SET DEPT_FLG='N', MEMO='"
			 +memo+"',AMT="+totamt+" WHERE MR_NO='"+parm.getValue("MR_NO")+
			 "' AND CONTRACTOR_CODE='"+parm.getValue("CONTRACTOR_CODE")+"' " +
			 "AND INSURANCE_NUMBER='"+parm.getValue("INSURANCE_NUMBER")+"'";
          sql2="UPDATE SYS_PATINFO SET  DEPT_FLG='N' WHERE MR_NO='"+parm.getValue("MR_NO")+"'";
         }
         TJDODBTool.getInstance().update(sql1);
         TJDODBTool.getInstance().update(sql2);
         onClear();
         this.setValue("ADM_TYPE", "O");
         onQuery();
	}
	/**
	 * ���Excel
	 */
	public void onExport() {
        if(table.getRowCount()<=0){
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "���ջ�����Ϣ��");
    }
	/**
	 * ���
	 */
	public void onClear() {//;DEPT_FLG
		this.clearValue("CONTRACTOR_CODE;INSURANCE_NUMBER;MR_NO;PAT_NAME;AMT;MEMO;GATHER_TYPE;START_DATE;END_DATE;ADM_TYPE");
		paymentTool.onClear();
		((TTable)this.getComponent("TABLE")).removeRowAll();
		getCheckBox("DEPT_FLG").setSelected(true);
		selectRow = -1;
	}
	 /**
     * ���TCheckBox
     */
    private TCheckBox getCheckBox(String tagName){
    	return (TCheckBox)getComponent(tagName);
    }
    /**
	 * ��񵥻��¼�
	 */
	public void onTABLEClicked(int row) {
		 // ѡ����
        if (row < 0)
            return;
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        setValueForParm(
            "CONTRACTOR_CODE;INSURANCE_NUMBER;MR_NO;PAT_NAME;DEPT_FLG;AMT;MEMO",
            data, row);    
        paymentTool.setAmt(this.getValueDouble("AMT"));
        selectRow = row;
	}
	
	/**
	 * �ż���
	 * @param parm
	 * @return
	 */
	public TParm select(TParm parm){
		String sql = "SELECT M.DEPT_FLG,M.AMT,M.MR_NO,S.PAT_NAME,S.SEX_CODE,S.BIRTH_DATE, M.CONTRACTOR_NAME," +
				" M.INSURANCE_NUMBER,M.INSURANCE_BILL_NUMBER,M.INSURE_PAY_TYPE,M.START_DATE," +
				" M.END_DATE,M.MEMO, M.CONTRACTOR_CODE,M.OPT_USER,M.OPT_TERM,M.OPT_DATE,A.ADM_DATE,A.REALDEPT_CODE,A.REALDR_CODE,A.ICD_CHN_DESC ICD_CODE,A.BILL_DATE," +
				" A.PAY_TYPE01,A.PAY_TYPE02,A.PAY_TYPE03,A.PAY_TYPE04,A.PAY_TYPE05,A.PAY_TYPE06," +
				" A.PAY_TYPE07,A.PAY_TYPE08,A.PAY_TYPE09,A.PAY_TYPE10,A.AR_AMT " +
				" FROM MEM_INSURE_INFO M,SYS_PATINFO S," +
				" (  SELECT A.INSURE_INFO CONTRACTOR_CODE, A.MR_NO, A.ADM_DATE,A.REALDEPT_CODE,A.REALDR_CODE,E.ICD_CHN_DESC,B.BILL_DATE,SUM (B.AR_AMT) AR_AMT,SUM (B.PAY_TYPE01) PAY_TYPE01," +
				" SUM (B.PAY_TYPE02) PAY_TYPE02,SUM (B.PAY_TYPE03) PAY_TYPE03,SUM (B.PAY_TYPE04) PAY_TYPE04," +
				" SUM (B.PAY_TYPE05) PAY_TYPE05, SUM (B.PAY_TYPE06) PAY_TYPE06, SUM (B.PAY_TYPE07) PAY_TYPE07," +
				" SUM (B.PAY_TYPE08) PAY_TYPE08, SUM (B.PAY_TYPE09) PAY_TYPE09, SUM (B.PAY_TYPE10) PAY_TYPE10" +
				" FROM REG_PATADM A, BIL_OPB_RECP B,OPD_DIAGREC D,SYS_DIAGNOSIS E" +
				" WHERE     A.CASE_NO = B.CASE_NO(+) AND A.REGCAN_USER IS NULL   " +
				" AND A.CASE_NO = D.CASE_NO AND A.SEEN_DR_TIME IS NOT NULL AND D.MAIN_DIAG_FLG='Y' " +
				" AND D.ICD_CODE=E.ICD_CODE AND D.ICD_TYPE=E.ICD_TYPE  " ;
		if(!("".equals(parm.getValue("MR_NO")) || "null".equals(parm.getValue("MR_NO")))){
			sql += " AND A.MR_NO = '"+parm.getValue("MR_NO")+"'";
		}
		//=========================��Ӳ�ѯ���� add by huangjw 20150803 start
		if(!(("".equals(parm.getValue("START_DATE")) || "null".equals(parm.getValue("START_DATE"))) && ("".equals(parm.getValue("END_DATE")) || "null".equals(parm.getValue("END_DATE"))))){
			sql += " AND A.ADM_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE").toString().substring(0,10).replaceAll("-", "/")+"','yyyy/MM/dd') " +
					" AND TO_DATE('"+parm.getValue("END_DATE").toString().substring(0,10).replaceAll("-", "/")+"','yyyy/MM/dd') ";
		}
		if(!("".equals(parm.getValue("ADM_TYPE")) || "null".equals(parm.getValue("ADM_TYPE")))){
			sql += " AND A.ADM_TYPE = '"+parm.getValue("ADM_TYPE")+"'";
		}
		
		//=========================��Ӳ�ѯ���� add by huangjw 20150803 end 
		sql +=	" GROUP BY A.MR_NO, A.ADM_DATE,A.REALDEPT_CODE,A.REALDR_CODE,E.ICD_CHN_DESC,B.BILL_DATE,A.INSURE_INFO ORDER BY A.ADM_DATE) A " +
				" WHERE M.CONTRACTOR_CODE = A.CONTRACTOR_CODE AND M.MR_NO = S.MR_NO" +
				//" AND M.VALID_FLG = 'Y'" +				
				" AND M.MR_NO = A.MR_NO" ;
		if(!("".equals(parm.getValue("MR_NO")) || "null".equals(parm.getValue("MR_NO")))){
			sql += " AND M.MR_NO = '"+parm.getValue("MR_NO")+"'";
		}
		if(!("".equals(parm.getValue("DEPT_FLG")) || "null".equals(parm.getValue("DEPT_FLG")))){
			sql += " AND M.DEPT_FLG = '"+parm.getValue("DEPT_FLG")+"'";//Ƿ��
		}
		if(!("".equals(parm.getValue("CONTRACTOR_CODE")) || "null".equals(parm.getValue("CONTRACTOR_CODE")))){
			sql += " AND M.CONTRACTOR_CODE = '"+parm.getValue("CONTRACTOR_CODE")+"'";
		}
		if(!("".equals(parm.getValue("INSURANCE_NUMBER")) || "null".equals(parm.getValue("INSURANCE_NUMBER")))){
			sql += " AND M.INSURANCE_NUMBER = '"+parm.getValue("INSURANCE_NUMBER")+"'";
		}
		
		sql +=	" ORDER BY M.START_DATE";
		//System.out.println("sql===="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public TParm selectI(TParm parm){
		String sql="SELECT M.DEPT_FLG,M.AMT,M.MR_NO,S.PAT_NAME,S.SEX_CODE,S.BIRTH_DATE, M.CONTRACTOR_NAME," +
				" M.INSURANCE_NUMBER,M.INSURANCE_BILL_NUMBER,M.INSURE_PAY_TYPE,M.START_DATE," +
				" M.END_DATE,M.MEMO, M.CONTRACTOR_CODE,M.OPT_USER,M.OPT_TERM,M.OPT_DATE,A.IN_DATE,A.REALDEPT_CODE,A.REALDR_CODE,A.ICD_CHN_DESC ICD_CODE,A.BILL_DATE,A.ADM_DAYS," +
				" A.PAY_CASH,A.PAY_MEDICAL_CARD,A.PAY_BANK_CARD,A.PAY_INS_CARD,A.PAY_CHECK,A.PAY_DEBIT," +
				" A.PAY_BILPAY,A.LUMPWORK_AMT,A.LUMPWORK_OUT_AMT,A.REDUCE_AMT,A.AR_AMT " +
				" FROM MEM_INSURE_INFO M,SYS_PATINFO S," +
				" (SELECT A.INSURE_INFO CONTRACTOR_CODE,A.MR_NO, A.IN_DATE,A.VS_DR_CODE REALDR_CODE,A.IN_DEPT_CODE REALDEPT_CODE,E.ICD_CHN_DESC,A.BILL_DATE,A.ADM_DAYS, SUM (B.AR_AMT) AR_AMT,SUM (B.PAY_CASH) PAY_CASH," +
				" SUM (B.PAY_MEDICAL_CARD) PAY_MEDICAL_CARD,SUM (B.PAY_BANK_CARD) PAY_BANK_CARD,SUM (B.PAY_INS_CARD) PAY_INS_CARD," +
				" SUM (B.PAY_CHECK) PAY_CHECK, SUM (B.PAY_DEBIT) PAY_DEBIT, SUM (B.PAY_BILPAY) PAY_BILPAY," +
				" SUM (B.LUMPWORK_AMT) LUMPWORK_AMT, SUM (B.LUMPWORK_OUT_AMT) LUMPWORK_OUT_AMT, SUM (B.REDUCE_AMT) REDUCE_AMT" +
				" FROM ADM_INP A, BIL_IBS_RECPM B,ADM_INPDIAG D,SYS_DIAGNOSIS E" +
				" WHERE     A.CASE_NO = B.CASE_NO(+) AND A.DS_DATE IS NOT NULL  " +
				" AND A.CASE_NO=D.CASE_NO AND D.MAINDIAG_FLG='Y' AND D.IO_TYPE='I' " +
				" AND D.ICD_CODE=E.ICD_CODE AND D.ICD_TYPE=E.ICD_TYPE";
		
		if(!("".equals(parm.getValue("MR_NO")) || "null".equals(parm.getValue("MR_NO")))){
			sql += " AND A.MR_NO = '"+parm.getValue("MR_NO")+"'";
		}
		
		if(!(("".equals(parm.getValue("START_DATE")) || "null".equals(parm.getValue("START_DATE"))) && ("".equals(parm.getValue("END_DATE")) || "null".equals(parm.getValue("END_DATE"))))){
			sql += " AND A.IN_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE").toString().substring(0,10).replaceAll("-", "/")+"','yyyy/MM/dd') " +
					" AND TO_DATE('"+parm.getValue("END_DATE").toString().substring(0,10).replaceAll("-", "/")+"','yyyy/MM/dd') ";
		}
		
		sql +=	" GROUP BY A.MR_NO, A.IN_DATE ,A.IN_DEPT_CODE,A.VS_DR_CODE,E.ICD_CHN_DESC,A.BILL_DATE,A.ADM_DAYS,A.INSURE_INFO ORDER BY A.IN_DATE) A " +
		" WHERE M.CONTRACTOR_CODE = A.CONTRACTOR_CODE AND M.MR_NO = S.MR_NO" +
		//" AND M.VALID_FLG = 'Y'" +				
		" AND M.MR_NO = A.MR_NO" ;
		if(!("".equals(parm.getValue("MR_NO")) || "null".equals(parm.getValue("MR_NO")))){
			sql += " AND M.MR_NO = '"+parm.getValue("MR_NO")+"'";
		}
		if(!("".equals(parm.getValue("DEPT_FLG")) || "null".equals(parm.getValue("DEPT_FLG")))){
			sql += " AND M.DEPT_FLG = '"+parm.getValue("DEPT_FLG")+"'";//Ƿ��
		}
		if(!("".equals(parm.getValue("CONTRACTOR_CODE")) || "null".equals(parm.getValue("CONTRACTOR_CODE")))){
			sql += " AND M.CONTRACTOR_CODE = '"+parm.getValue("CONTRACTOR_CODE")+"'";
		}
		if(!("".equals(parm.getValue("INSURANCE_NUMBER")) || "null".equals(parm.getValue("INSURANCE_NUMBER")))){
			sql += " AND M.INSURANCE_NUMBER = '"+parm.getValue("INSURANCE_NUMBER")+"'";
		}
		sql +=	" ORDER BY M.START_DATE";
		//System.out.println(""+sql);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	/**
	 * �����������������
	 * @param table TTable
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				int i = table.getTable().columnAtPoint(me.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = table.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);
				comparator.setDes(ascending);
				comparator.setCol(col);
				java.util.Collections.sort(vct, comparator);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);
				//getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * �õ� Vector ֵ
	 * @param parm TParm
	 * @param group String
	 * @param names String
	 * @param size int
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}
	/**
	 * ת��parm�е���
	 * @param columnName String[]
	 * @param tblColumnName String
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}
		return index;
	}
	
	/**
	 * vectoryת��param
	 * @param vectorTable Vector
	 * @param parmTable TParm
	 * @param columnNames String
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
}
