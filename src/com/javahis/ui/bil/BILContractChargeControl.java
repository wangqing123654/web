package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.bil.BILContractPayTool;
import jdo.bil.BILInvoiceTool;
import jdo.bil.BILTool;
import jdo.bil.BilInvoice;
import jdo.opb.OPBReceipt;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: ��ͬ��λ�ɷ�
 * </p>
 * 
 * <p>
 * Description: ��ͬ��λ�ɷ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright bluecore
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author caowl
 * @version 1.0
 */
public class BILContractChargeControl extends TControl{
	
	

	//action��·��
	private static final String actionName = "action.bil.BILContractAction";
	
	TParm endBillPayParm;
	
    //ѡ�����
	
	// �ϴε�ѡ��ҳǩ����
	private int lastSelectedIndex = 0;
	private TTabbedPane TabbedPane;
	/**
	 * ��������
	 * */ 
	private TComboBox REGION_CODE;
	private TTextFormat CONTRACT_CODE;
	private TTextFormat START_DATE;
	private TTextFormat END_DATE;
	private TComboBox CASHIER_CODE;
	
	int no = 1;
	double arAmt = 0;
	/**
	 * ��һ��ҳǩ�Ŀ���    ��ͬ��λ��Ϣ
	 * */
	private TTable table1;//��ͬ��λ��
	
	/**
	 * �ڶ���ҳǩ�Ŀ���    �����б�
	 * */  
	private TTable table2;//�����б�
    
	/**
	 * ������ҳǩ�Ŀ���   �վ��б�
	 * */
	private TTable table3;//�վݱ�
	private TTextField PRINT_NO;
    
	/**
	 * ���ĸ�ҳǩ�Ŀ���    ������Ϣ
	 * */
	private TTextField COUNT;//�վ���Ŀ
	private TTextField REFUND;//Ԥ�������
	private TTextField CHARGE;//Ӧ�ս��
	private TTextField PAY;//����Ӧ��
	private TTable table4;
	private TButton ADD;
	private TTable table_pay;//Ԥ�����
	/**
	 * �����ҳǩ�Ŀ���   �����б�
	 * */
	private TTable table5;
	private TTable table6;
	
	 double bilPayAmt = 0.00;
	 double enArAmt = 0;
	 String startInvno = "";
	 String printNo ="";
	 
    int selectRow = -1;
    public BILContractChargeControl() {

    }

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        //���ȫ���ؼ�
        getAllComponent();        
        //��ʼ��ʱ��
        initDate();
        this.checkNo();
        //��ѯȫ����Ϣ
        this.onQuery();
        //����Ժ����Ϣ
        this.setValue("REGION_CODE", Operator.getRegion());
        //��ӱ�ļ�����Ϣ
        //��ͬ��λ��Ϣ��
		callFunction("UI|Table1|addEventListener", "Table1->"
				+ TTableEvent.CLICKED, this, "onTableClickedForTable1");		
		//Ԥ�����ļ���
		 getTTable("table_pay").addEventListener(
	                TTableEvent.CHECK_BOX_CLICKED, this, "onBillPayTableComponent");
		 //�����б����
		 getTTable("Table2").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,"onTableClickedForTable2");
		 //�վ��б����
		 getTTable("Table3").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,"onTableClickedForTable3");
		
    }
    
    /**
     * �õ�TTable
     *
     * @param tag
     *            String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    /**
     * ��ͬ��λ������¼�
     * @param obj
     * @return void
     * */
    public void onTableClickedForTable1(int row){
    	if (row < 0) {
			return;
		}
		// ��ȡ���ڱ༭״̬������
		table1.acceptText();
		// ��ͬ���벻�ɱ༭
		CONTRACT_CODE.setEnabled(false);
		
		/*
		 * ��ȡѡ�е����ݣ�����Щ�������õ���������ؼ���
		 */
		REGION_CODE.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("REGION_CODE", row))));
		CONTRACT_CODE.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("CONTRACT_CODE", row))));
		
    }
    
    /**
	 * ��nullת��Ϊ���ַ���
	 */
	public String nullToString(String str) {
		if (str == null || "null".equals(str)) {
			return "";
		} else {
			return str.trim();
		}
	}
    
    /**
     * Ԥ����table�����¼�
     *
     * @param obj
     *            Object
     * @return boolean
     */
    public boolean onBillPayTableComponent(Object obj) {
        TTable billPayTable = (TTable) obj;
        billPayTable.acceptText();
        TParm tableParm = billPayTable.getParmValue();
        endBillPayParm = new TParm();
        int count = tableParm.getCount("RECEIPT_NO");
        //System.out.println("Ԥ������ϸ"+tableParm);
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("PRE_FLG", i)) {
                endBillPayParm.addData("RECEIPT_NO", tableParm.getValue(
                        "RECEIPT_NO", i));
                endBillPayParm.addData("PRE_AMT", tableParm.getValue("PRE_AMT",
                        i));
            }
        }
        bilPayAmt = 0.0;
        int feeCount = endBillPayParm.getCount("PRE_AMT");
       
        for (int j = 0; j < feeCount; j++) {
            bilPayAmt = bilPayAmt + endBillPayParm.getDouble("PRE_AMT", j);
        }
    
        setValue("REFUND", String.valueOf(bilPayAmt));//Ԥ�������
     
        
        if(getValueDouble("CHARGE")>=0){
        	enArAmt = TypeTool.getDouble(getValue("CHARGE")) - bilPayAmt;
        }
        this.setValue("PAY", String.valueOf(enArAmt));//����Ӧ����

        return true;
    }
    /**
     * �����б����
     * */
    public boolean onTableClickedForTable2(Object obj){
    	//System.out.println("�����б����");
    	  TTable Table2 = (TTable) obj;
    	  Table2.acceptText();
          TParm tableParm = Table2.getParmValue();
          endBillPayParm = new TParm();
          int count = tableParm.getCount();
      
          for (int i = 0; i < count; i++) {
              if (tableParm.getBoolean("STATUS", i)) {
                  endBillPayParm.addData("MR_NO", tableParm.getValue(
                          "MR_NO", i));
              }
          }


          return true;
    }
    /**
     * �վ��б����
     * */
    public boolean onTableClickedForTable3(Object obj){
    	
    	  TTable Table3 = (TTable) obj;
    	  Table3.acceptText();
          TParm tableParm = Table3.getParmValue();
          endBillPayParm = new TParm();
          int count = tableParm.getCount();
        
          for (int i = 0; i < count; i++) {
              if (tableParm.getBoolean("FLG", i)) {
                  endBillPayParm.addData("RECEIPT_NO", tableParm.getValue(
                          "RECEIPT_NO", i));
              }
          }


          return true;
    }
    
    /**
     * ��ʼ������
     * */
    public void initDate(){
    	Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		setValue("START_DATE", yesterday);
		setValue("END_DATE", SystemTool.getInstance().getDate());
   }
    
    /**
     * �õ�ȫ���ؼ�
     * */
    public void getAllComponent(){   	
    	/**
		 * ���ѡ��ؼ�
		 */
		this.TabbedPane = (TTabbedPane) this.getComponent("TabbedPane");
		//��������
		START_DATE = (TTextFormat) this.getComponent("START_DATE");
		END_DATE = (TTextFormat) this.getComponent("END_DATE");
		REGION_CODE = (TComboBox)this.getComponent("REGION_CODE");
		CONTRACT_CODE = (TTextFormat)this.getComponent("CONTRACT_CODE");	   
	    CASHIER_CODE = (TComboBox)this.getComponent("CASHIER_CODE");
	    //��ͬ��λ
	    table1 = (TTable) this.getComponent("Table1");
	    //�����б�
		table2 = (TTable) this.getComponent("Table2");
		//�վ��б�
		table3 = (TTable) this.getComponent("Table3");
		PRINT_NO = (TTextField)this.getComponent("PRINT_NO");
		//������Ϣ
		table4 = (TTable) this.getComponent("Table4");
		COUNT =  (TTextField)this.getComponent("COUNT");//�վ���Ŀ
		REFUND= (TTextField)this.getComponent("REFUND"); //Ԥ�������
		CHARGE =  (TTextField)this.getComponent("CHARGE"); //Ӧ�ս��
		PAY =  (TTextField) this.getComponent("PAY");//����Ӧ��		
		ADD =  (TButton)this.getComponent("ADD");//���Ӱ�ť
		table_pay = (TTable)this.getComponent("table_pay");//Ԥ�����
		
		//�����б�
		table5 = (TTable) this.getComponent("Table5");
		table6 = (TTable) this.getComponent("Table6");
    }
   /**
    * �жϺ�ͬ��λ�������ż���Ԥ����+��������Ƿ����֧�����δ��ɷ���
    *   ��������������š���ͬ��λ��š����ɷ��ý��
    *   ���أ�����ֵ �ǻ�� 
    * */ 
    
    public boolean isEnough(TParm parm,int i){
        System.out.println("parm:"+parm);
    	boolean result = false;
    	String mr_no = parm.getValue("MR_NO",i);
    	String contract_code = parm.getValue("CONTRACT_CODE",i);
    	Double ar_amt = parm.getDouble("AR_AMT",i);
    	//1.��ѯ���˺�ͬ��λԤ���������������
    	String sql1 = "SELECT PREPAY_AMT,LIMIT_AMT FROM BIL_CONTRACTM WHERE CONTRACT_CODE = '"+contract_code+"'";
    	TParm selParm1 = new TParm(TJDODBTool.getInstance().select(sql1));
    	Double prepay_amt_contract = selParm1.getDouble("PREPAY_AMT",0);
    	Double limit_amt_contract = selParm1.getDouble("LIMIT_AMT",0);
    	//2.���ߵ�Ԥ���������������
    	String sql2 = "SELECT PREPAY_AMT,LIMIT_AMT FROM BIL_CONTRACTD WHERE CONTRACT_CODE = '"+contract_code+"' AND MR_NO = '"+mr_no+"'";
    	TParm selParm2 = new TParm(TJDODBTool.getInstance().select(sql2));
    	Double prepay_amt = selParm2.getDouble("PREPAY_AMT",0);
    	Double limit_amt = selParm2.getDouble("LIMIT_AMT",0);
    	//3.�ж�
    	if(ar_amt <= prepay_amt_contract+limit_amt_contract+prepay_amt+limit_amt){
    		result = true;
    	}
    	return result;
    }
    /**
     * ��ӡ
     * 
     * */
    public void onPrint(){
    	
    }
    
    /**
     * �����˵�
     * */
    public void onReturn(){
    	
    }
    /**
	 * ���淽���� ִ���޸�OPD_ORDER ���� PRINT_FLG=Y AND PRINT_NO ����ֵ �޸� BIL_REG_RECP ����
	 * PRINT_DATE ����ʱ�� PRINT_NO ����ֵ ��� BIL_INVRCP ������ �޸� BIL_INVOICE ����������Ʊ�ݺŲ���
	 * �޸� BIL_OPB_RECP ���� PRINT_DATE ����ʱ�� PRINT_NO ����ֵ
	 */
	public void onSave() {
		
//		BilInvoice bilInvoice = new BilInvoice();
//		//��ʼ��Ʊ��
//       	if (!initBilInvoice(bilInvoice.initBilInvoice("OPB"))) {//Ʊ������ѡ������Ʊ��
//            return;
//        } 		
		if(!checkNo()){
			return;
		}
		if (null==this.getValue("CASHIER_CODE") || this.getValue("CASHIER_CODE").toString().length()<=0) {
			this.messageBox("��ѡ�������Ա");
			this.grabFocus("CASHIER_CODE");
			return;		
		}	    
		TParm newParm=new TParm();				
		TParm tableParm = table3.getParmValue();
		boolean isY = false;
		int count=0;
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getBoolean("FLG", i)) {
				if ("Y".equals(tableParm.getValue("FLG", i))
						&& "2".equals(tableParm.getValue("BIL_STATUS",
								i))) {
					this.messageBox("�˵���Ϊ:"+tableParm.getValue("RECEIPT_NO",i)+"�Ѿ����,�������޸�");
					continue;
				}
				   //�ж�֧��������������������������	
				if(!isEnough(tableParm,i)){
					this.messageBox("�˵���Ϊ��"+tableParm.getValue("RECEIPT_NO",i)+"�Ĳ��˶�Ȳ��㣡");
					continue;
				}
				isY = true;
				newParm.setRowData(count,tableParm,i);
				count++;
			}
		}
		newParm.setCount(count);
		newParm.getRow(0);
		TParm payParm = new TParm();
		// ��ִ�е�����
		//*************************************
		TParm tablePayParms = table_pay.getParmValue();
		int payCount = tablePayParms.getCount("RECEIPT_NO");
	      
	        for (int i = 0; i < payCount; i++) {
	            if (tablePayParms.getBoolean("PRE_FLG", i)) {
	            	TParm actionPayParm = new TParm(); 
	            	actionPayParm.setData("RECEIPT_NO",tablePayParms.getValue(
	                        "RECEIPT_NO", i));//1	            		            		       	       
	            	actionPayParm =  BILContractPayTool.getInstance().selectData("selectByReceipt",actionPayParm);//����RECEIPT_NO��ѯԤ������Ϣ
	       	      
	            	TParm endParm = new TParm();
	       	        endParm = actionPayParm.getRow(0);
	       	        endParm.setData("BUSINESS_TYPE","3");//����	       	            
	       	        endParm.setData("CASHIER_CODE", Operator.getID());
	       	        endParm.setData("CHARGE_DATE", SystemTool.getInstance().getDate());	       	       	     
	       	        endParm.setData("OPT_USER", Operator.getID());
	       	        endParm.setData("OPT_TERM", Operator.getIP());
	       	        endParm.setData("OPT_DATE",SystemTool.getInstance().getDate());	  	       	    
	       	        payParm.setRowData(payCount,endParm);
	       	        
	            }
	        }
	        payParm.setCount(payCount); 	    		      
		//*************************************
		if (isY) {
			TParm parm = new TParm();
			parm.setData("OPT_USER", this.getValue("CASHIER_CODE"));
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("recodeParm", newParm.getData());
			parm.setData("payParm",payParm.getData());
			//System.out.println("����ʱ�� parm������"+parm);
			TParm result = TIOM_AppServer.executeAction(
					actionName, "onSaveContract", parm);
			if (result.getErrCode() < 0) {
				this.messageBox("ִ��ʧ��");
				return;
			}
			this.messageBox("ִ�гɹ�");
			//��Ʊ	���������֣�1.����Ԥ�����Ʊ   2.����ѵ�Ʊ
	        TParm forPrtParm = new TParm();
	        String bilPayC = StringUtil.getInstance().numberToWord(TypeTool.
	                getDouble(getValue("CHARGE")));
	        Timestamp printDate = (SystemTool.getInstance().getDate());
	        String pDate = StringTool.getString(printDate, "yyyy/MM/dd HH:mm:ss");
	        forPrtParm.setData("COPY", "TEXT", "");
	        forPrtParm.setData("REGION_CHN_DESC", "TEXT",
	                           Operator.getHospitalCHNFullName());
	        forPrtParm.setData("CHECK_NO","TEXT",this.getValue("CHECK_NO")) ;
	        forPrtParm.setData("REMARK","TEXT",this.getValue("REMARK")) ;
	        forPrtParm.setData("REGION_ENG_DESC", "TEXT",
	                           Operator.getHospitalENGFullName());

	        forPrtParm.setData("Data", "TEXT",  pDate);
	        String sql = "SELECT CONTRACT_DESC FROM BIL_CONTRACTM WHERE CONTRACT_CODE = '"+this.getValue("CONTRACT_CODE")+"'";
	        TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
	        String contract_desc = selParm.getValue("CONTRACT_DESC",0);
	        forPrtParm.setData("Name", "TEXT", contract_desc);
	    
	        forPrtParm.setData("TOLL_COLLECTOR", "TEXT", Operator.getID());
	   
	        forPrtParm.setData("MR_N0", "TEXT", this.getValue("CONTRACT_CODE"));
	        forPrtParm.setData("Capital", "TEXT",  bilPayC);
	        DecimalFormat formatObject = new DecimalFormat("###########0.00");

	        forPrtParm.setData("SmallCaps", "TEXT",formatObject.format(
	                           TypeTool.getDouble(this.getValueString("CHARGE"))));
	        forPrtParm.setData("SEQ_NO", "TEXT",
	                           "��ˮ��:" + this.getValueString("RECEIPT_NO"));
	        forPrtParm.setData("PRINT_DATE", "TEXT", "��ӡ����:" + pDate);

	        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILContractPrepayment.jhw",
	                             forPrtParm,true);
	        //=================================
	     // ���ô����ӡ�ķ���
	        TParm receiptParm = new TParm();
			dealPrintData(receiptParm);
		} else
			this.messageBox("��ѡ��Ҫִ�е�����");
		
		onQuery();

	}
	//============================
	/**
	 * �����ӡ����
	 * 
	 * @param receiptNo
	 *            String[]
	 */
	public void dealPrintData(TParm receiptParm) {

		TParm oneReceiptParm =  new TParm();
		receiptParm.setData("OPT_USER", Operator.getName());
		receiptParm.setData("OPT_ID", Operator.getID());
		receiptParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBReceipt.jhw",
				oneReceiptParm);
	
	}	
	//============================
    /*
     * ������ť
     * **/
	public void onAdd(){		
		if(table4.getRowCount()>0){
			this.messageBox("�Ѿ�������");
			return;
		}
		this.getValue("COUNT");//�վ���Ŀ
		this.getValue("REFUND");//Ԥ�������
		this.getValue("CHARGE");//Ӧ�ս��
		this.getValue("PAY");//����Ӧ�� 
		TParm parm = new TParm();
		arAmt += Double.parseDouble(this.getValue("CHARGE").toString());		
		parm.setData("NO",String.valueOf(no));
		parm.setData("DEL_FLG","N");
		parm.setData("PAY_TYPE","�ֽ�");
		parm.setData("AR_AMT",this.getValue("CHARGE").toString());
		parm.setData("PRINT_NO",printNo);
		parm.setData("MARK","");		
		no ++;
		callFunction("UI|Table4|addRow", parm,
        "NO;DEL_FLG;PAY_TYPE;AR_AMT;PRINT_NO;MARK");	
		
	}
    
    /**
     * ��ѯ
     * */
    public void onQuery(){
    	String region_code = this.getValueString("REGION_CODE");
    	String contract_code = this.getValueString("CONTRACT_CODE");
    	String cashier_code = this.getValueString("CASHIER_CODE");
    	String start_date = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmmss");
		String end_date = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss");
		int count = 0 ;
		Integer counts = 0 ;
    	if(start_date==null || start_date.equals("") || end_date==null || end_date.equals("")){
    		this.messageBox("��ѡ��ʼ�ͽ���ʱ�䣡");
    		return ;
    	}
    	// ��ȡҳǩ����
		int selectedIndex = TabbedPane.getSelectedIndex();
		//��ͬ��λ
    	if(selectedIndex == 0){
    		String sql = " SELECT CONTRACT_CODE,CONTRACT_DESC,DESCRIPTION,ADDRESS,TEL1,TEL2,CONTACT,REGION_CODE "+
    		" FROM BIL_CONTRACTM "+
    		" WHERE DEL_FLG = 'N'" ;			
        	if(region_code!=null && !region_code.equals("")){
        		sql += " AND REGION_CODE = '"+region_code+"'";
        	}
        	if(contract_code!=null && !contract_code.equals("")){
        		sql += " AND CONTRACT_CODE = '"+contract_code+"'";
        	}
        	TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
        	table1.setParmValue(selParm);
    	}
    
    	//�����б�
    	if(selectedIndex == 1){
    		String sql1 = " SELECT DISTINCT 'N' AS STATUS ,A.MR_NO,A.RECEIPT_NO,A.CASE_NO,A.CONTRACT_CODE,A.REGION_CODE,B.PAT_NAME,B.CTZ1_CODE,R.REG_DATE"+
						  " FROM BIL_CONTRACT_RECODE A,SYS_PATINFO B,SYS_OPERATOR C,REG_PATADM R "+
						  " WHERE A.MR_NO = B.MR_NO "+
						  " AND A.CASE_NO = R.CASE_NO "+
						  " AND A.CASHIER_CODE = C.USER_ID "+
						  " AND A.CHARGE_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') "+
						  " AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS') "+
						  " AND A.BIL_STATUS = '1'";
    		
		   
    		if(region_code!=null && !region_code.equals("")){
        		sql1 += " AND A.REGION_CODE = '"+region_code+"'";
        	}
        	if(contract_code!=null && !contract_code.equals("")){
        		sql1 += " AND A.CONTRACT_CODE= '"+contract_code+"'";
        	}  
        	sql1 += " ORDER BY A.CASE_NO";
        	// System.out.println("sql1::::"+sql1);	
        	TParm selParm1 = new TParm(TJDODBTool.getInstance().select(sql1));      	
           	table2.setParmValue(selParm1);
    	}
    	//�վ��б�
    	if(selectedIndex == 2){
    		boolean isY = false ;    	
    		TParm newParm = new TParm();
    		TParm tableParm = table2.getParmValue();
    		//System.out.println("tableParm:::::::"+tableParm);   		
    		for (int i = 0; i < tableParm.getCount(); i++) {
    			if (tableParm.getBoolean("STATUS", i)) {
    				isY = true;
    				newParm.setRowData(count,tableParm,i);
    				count++;
    			}
    		}
    		newParm.setCount(count);    
    		// ��ִ�е�����
    		if (isY) {
    			String mrNo= newParm.getValue("MR_NO", 0);	
    			for(int i=1;i<count;i++ ){
    				String mr_no = newParm.getValue("MR_NO", i);
    				mrNo += ","+mr_no;
    			}
    			
    			String sql2 = " SELECT 'N' AS FLG,A.RECEIPT_NO,A.CASE_NO,A.CONTRACT_CODE,A.REGION_CODE,"+
					  " A.CHARGE_DATE,A.RECEIPT_TYPE,A.MR_NO,A.AR_AMT AS AR_AMT,A.AR_AMT AS PAY,A.BIL_STATUS,A.CASHIER_CODE,"+
					  " B.PAT_NAME,C.USER_NAME,B.CTZ1_CODE,A.RECEIPT_FLG "+
				 " FROM BIL_CONTRACT_RECODE A,SYS_PATINFO B,SYS_OPERATOR C "+
				 " WHERE A.MR_NO=B.MR_NO(+) "+
					   " AND A.CHARGE_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') "+
					   " AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS') "+
					   " AND A.BIL_STATUS='1'"+
					   " AND A.CASHIER_CODE=C.USER_ID "+  			
					   " AND A.MR_NO IN ("+mrNo+")";
				if(contract_code!=null && !contract_code.equals("")){
			 		    sql2 += " AND  A.CONTRACT_CODE = '"+contract_code+"'";
			    } 
				if(cashier_code!=null && !cashier_code.equals("")){
						sql2 += " AND A.CASHIER_CODE = '"+cashier_code+"'"; 
				}
//				String print_no = this.getValueString("PRINT_NO");
//				if(print_no != null && !print_no.equals("")){
//						sql2 += " AND A.RECEIPT_NO = '"+print_no+"'";
//				}
				//ѡ��,40,Boolean;�����,100;������,100;�վݺ�,100;������־,80,Boolean;�Ѵ�Ʊ��־,100,Boolean;Ӧ����,80;�Է��ܶ�,80
				TParm selParm2 = new TParm(TJDODBTool.getInstance().select(sql2));				
	    		table3.setParmValue(selParm2); 		    		
			  }    		
    		   		
    	}
    	//������Ϣ
    	if(selectedIndex == 3){
    		boolean isY = false ;
    		double ar_amt = 0;
    		TParm newParm = new TParm();
    		TParm tableParm = table3.getParmValue();    		
    		for (int i = 0; i < tableParm.getCount(); i++) {
    			if (tableParm.getBoolean("FLG", i)) {
    				isY = true;
    				newParm.setRowData(counts,tableParm,i);
    				counts++;   			
    				ar_amt += tableParm.getDouble("AR_AMT",i);
    			}
    		}
    		newParm.setCount(count);   	
    		//�վ���Ŀ    	  
    		COUNT.setValue(String.valueOf(counts));
    		//Ԥ�������
    		REFUND.setValue(String.valueOf(bilPayAmt));
    		//Ӧ�ս��
    		CHARGE.setValue(String.valueOf(ar_amt));
    		enArAmt = ar_amt - bilPayAmt;
    		//����Ӧ��
    		PAY.setValue(String.valueOf(enArAmt));
    		
    		// ��ִ�е�����
    		if (isY) {
    			String mrNo= newParm.getValue("MR_NO", 0);	
    			for(int i=1;i<count;i++ ){
    				String mr_no = newParm.getValue("MR_NO", i);
    				mrNo += ","+mr_no;
    			}
    			String sql2 = " SELECT 'N' AS FLG,A.RECEIPT_NO,A.CASE_NO,A.CONTRACT_CODE,A.REGION_CODE,"+
					  " A.CHARGE_DATE,A.RECEIPT_TYPE,A.MR_NO,A.AR_AMT AS AR_AMT,A.AR_AMT AS PAY,A.BIL_STATUS,A.CASHIER_CODE,"+
					  " B.PAT_NAME,C.USER_NAME,B.CTZ1_CODE,A.RECEIPT_FLG "+
				 " FROM BIL_CONTRACT_RECODE A,SYS_PATINFO B,SYS_OPERATOR C "+
				 " WHERE A.MR_NO=B.MR_NO(+) "+
					   " AND A.CHARGE_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') "+
					   " AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS') "+
					   " AND A.BIL_STATUS='1'"+
					   " AND A.CASHIER_CODE=C.USER_ID "+  			
					   " AND A.MR_NO IN ("+mrNo+")";
				if(contract_code!=null && !contract_code.equals("")){
			 		sql2 += " AND  A.CONTRACT_CODE = '"+contract_code+"'";
			    } 
				if(cashier_code!=null && !cashier_code.equals("")){
						sql2 += " AND A.CASHIER_CODE = '"+cashier_code+"'"; 
				}
				String print_no = this.getValueString("PRINT_NO");
				if(print_no != null && !print_no.equals("")){
						sql2 += " AND A.RECEIPT_NO = '"+print_no+"'";
				}
				//Ԥ����
				//ѡ,50,boolean;Ʊ�ݺ�,100;�շ���Ա,100;�շ�����,100;Ԥ�����,100;֧������,100;��ע,100
				//PRE_FLG;RECEIPT_NO;CASHIER_CODE;CHARGE_DATE;PRE_AMT;PAY_TYPE;REMARK
				String sql3 = "SELECT 'N' AS PRE_FLG , RECEIPT_NO,CASHIER_CODE,CHARGE_DATE,PRE_AMT,PAY_TYPE,REMARK "+
								" FROM BIL_CONTRACT_PAY "+
								" WHERE CONTRACT_CODE = '"+contract_code+"'"+
								 " AND BUSINESS_TYPE = 1 "+
								 " AND REFUND_FLG = 'N' " +
								 " AND OFF_RECP_NO IS NULL";
				TParm selParm3 = new TParm(TJDODBTool.getInstance().select(sql3));
				table_pay.setParmValue(selParm3);
				TParm selParm2 = new TParm(TJDODBTool.getInstance().select(sql2));			  
	    		table6.setParmValue(selParm2); 	
	    		
			  }
    	}
    	//�����б�
    	if(selectedIndex == 4){
    		String contractCode = this.getValueString("CONTRACT_CODE");
    		TParm parm = new TParm();
    		//CONTRACT_CODE;CONTRACT_DESC;PRINT_NO;AR_AMT;CASHIER_CODE
    		String sqls = "SELECT A.CONTRACT_CODE,A.CONTRACT_DESC,'' AS PRINT_NO,CASHIER_CODE " +
    				"FROM BIL_CONTRACTM A,BIL_CONTRACT_RECODE B "+
					" WHERE A.CONTRACT_CODE = B.CONTRACT_CODE "+
					" AND A.CONTRACT_CODE = '"+contract_code+"' "+
					" GROUP BY A.CONTRACT_CODE,A.CONTRACT_DESC,B.CASHIER_CODE ";   		
    		TParm selParm = new TParm(TJDODBTool.getInstance().select(sqls));    		
    		TParm tableParm = new TParm();
    		tableParm.addData("CONTRACT_CODE",selParm.getData("CONTRACT_CODE",0));
    		tableParm.addData("CONTRACT_DESC",selParm.getData("CONTRACT_DESC",0));
    		tableParm.addData("PRINT_NO",printNo);   		
    		tableParm.addData("AR_AMT",enArAmt);
    		tableParm.addData("CASHIER_CODE",selParm.getData("CASHIER_CODE",0));   	
    		//table5.addRow(tableParm); 
    		table5.setParmValue(tableParm);
    	}
    	
    }
    /**
     * ���
     * */
    public void onClear(){
    	this.setValue("REGION_CODE", "");
    	this.setValue("CONTRACT_CODE", "");
    	this.setValue("CASHIER_CODE", "");
    	CONTRACT_CODE.setEditBoard(true);
    	initDate();
    	// ��ȡҳǩ����
		int selectedIndex = TabbedPane.getSelectedIndex();
		
		//��ͬ��λ
    	if(selectedIndex == 0){
    		table1.removeRowAll();
    	}
    	//�����б�
    	if(selectedIndex == 1){
    		table2.removeRowAll();
    		
    	}
    	//�վ��б�
    	if(selectedIndex == 2){
    		table3.removeRowAll();
    		this.setValue("PRINT_NO", "");
 
    	}
    	//������Ϣ
    	if(selectedIndex == 3){
    		table4.removeRowAll();
       		this.setValue("COUNT", "");
    		this.setValue("REFUND", "");
    		this.setValue("CHARGE", "");
    		this.setValue("PAY", "");
    	}
    	//�����б�
    	if(selectedIndex == 4){
    		table5.removeRowAll();
    		table6.removeRowAll();
    	}
    }
   
    /**
	 * ��ѡ��л�ʱ ���ô˷���
	 */
	public void onChange() {
		// ��ȡҳǩ����
		int selectedIndex = TabbedPane.getSelectedIndex();
		// ��ѯ�˵�����
		//menuControl();
		// ����ǰ��ѡ��ҳǩ����Ϊ0����ͬ��λ���������κδ���ֱ�ӷ���
		if (selectedIndex <= 0) {
			//���򲻿ɱ༭
			this.REGION_CODE.setEditable(false);
			// ��¼�˴ε�ѡ��ҳǩ����
			lastSelectedIndex = selectedIndex;
			
			return;
		}
		// ����ǰ��ѡ��ҳǩ����Ϊ1�������б�������֤�Ƿ�ѡ���˺�ͬ��λ
		if (selectedIndex == 1) {
			if (table1.getSelectedRow() < 0) {
				this.messageBox("��ѡ���ͬ��λ��Ϣ��");
				TabbedPane.setSelectedIndex(0);
				
				return;
			}
			this.onQuery();
			//�����ǰ��Ϣ�����²�ѯʱ����Ϣ
			if (lastSelectedIndex == 0) {			
				// ģ����ѯ����
			   this.onQuery();
			}
		}
		//����ǰ��ѡ��ҳǩ����Ϊ2���վ��б�,����֤�Ƿ�ѡ���˲�����Ϣ
		if (selectedIndex == 2) {
			if (table2.getSelectedRow() < 0) {
				this.messageBox("��ѡ��ϲ�����Ϣ��");
				TabbedPane.setSelectedIndex(1);
				return;
			}
			//��ʼ�����ֿؼ�
		    this.onQuery();
			//�����ǰ��Ϣ�����²�ѯʱ����Ϣ
			if (lastSelectedIndex == 1) {
				// ģ����ѯ����
			    this.onQuery();
			}
		}
		//����ǰ��ѡ��ҳǩ����Ϊ3��������Ϣ��,����֤�Ƿ�ѡ�����վ��б�
		if (selectedIndex == 3) {
			if (table3.getSelectedRow() < 0) {
				this.messageBox("��ѡ���վ���Ϣ��");
				TabbedPane.setSelectedIndex(2);
				return;
			}
			//��ʼ�����ֿؼ�
		    this.onQuery();
			//�����ǰ��Ϣ�����²�ѯʱ����Ϣ
			if (lastSelectedIndex == 2) {				
				// ģ����ѯ����
			    this.onQuery();
			}
		}
		//����ǰ��ѡ��ҳǩ����Ϊ4�������б�,����֤�Ƿ�ѡ���˽�����Ϣ
		if (selectedIndex == 4) {
			if (table4.getSelectedRow() < 0) {
				this.messageBox("��ѡ�������Ϣ��");
				TabbedPane.setSelectedIndex(3);
				return;
			}
			//��ʼ�����ֿؼ�
		     this.onQuery();
			//�����ǰ��Ϣ�����²�ѯʱ����Ϣ
			if (lastSelectedIndex == 3) {			
				// ģ����ѯ����
				this.onQuery();
			
			}
		}
	}
	/**
	 * ���÷��������˷���
	 * ���У����ż���Һš��ż����շѡ�������鱨��(����)ʱ����У�
	 * ��֧����ʽΪ���˸ô��շѼ�¼��BIL_CONTRACT_RECODE ����
	 * */
	public TParm onDebit(TParm parm){
		TParm result = new TParm();
    	//�ж��Ƿ�������˼���ȹ���
    	String contract_code = parm.getValue("CONTRACT_CODE");
    	String mr_no = parm.getValue("MR_NO");
    	Double pay = parm.getDouble("AR_AMT");
    	//1.��ѯ�������ƽ��
    	String sql = "SELECT LIMIT_AMT FROM BIL_CONTRACTD WHERE CONTRACT_CODE = '"+contract_code+"' AND MR_NO = '"+mr_no+"'";
    	TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
    	Double limit_amt = selParm.getDouble("LIMIT_AMT");
    	//�������ƽ�Ϊ��
    	//���˿��ý��  = �������ƽ�� �C �ۼƼ��˽��
    	if(limit_amt!=null &&limit_amt !=0){
    		//�ۼƼ��˽��
    		String sql1 = "SELECT SUM(AR_AMT) AS AR_AMT FROM BIL_CONTRACT_RECORD WHERE MR_NO = '"+mr_no+"' AND BIL_STATUS = 1";
    		TParm selParm1 =  new TParm(TJDODBTool.getInstance().select(sql1));
    		Double ar_amt = selParm1.getDouble("AR_AMT");
    		//���˿��ý��
    		Double sum = limit_amt - ar_amt;
    		//Ӧ�ս�� - ���˿��ý�� <= 0 	д�� bil_contract_recode , ����
    		if(pay <= sum){
    			 result = TIOM_AppServer.executeAction(
    					"action.bil.BILContractRecordAction", "insertRecode", parm);
    			if (result.getErrCode() < 0) {
    				err(result.getErrCode() + " " + result.getErrText());
    				this.messageBox("����ʧ��");
    			} 
    		}else{
    			this.messageBox("��Ȳ��㣬���ɼ���");
    		}
    	//�������ƽ��Ϊ��
    	//��ҵ���ö��  = Ԥ������� + ���ö��
    	}else{
    		String sql2 = "SELECT PREPAY_AMT,LIMIT_AMT FROM BIL_CONTRACTM WHERE CONTRACT_CODE = '"+contract_code+"'";
    		TParm selParm2 = new TParm(TJDODBTool.getInstance().select(sql2));
    		Double prepay_amt = selParm2.getDouble("PREPAY_AMT");
    		Double limit_amts = selParm2.getDouble("LIMIT_AMT");
    		Double sum = prepay_amt + limit_amts;
    		if(pay <= sum){
    			result = TIOM_AppServer.executeAction(
    					"action.bil.BILContractRecordAction", "insertRecode", parm);
    			if (result.getErrCode() < 0) {
    				err(result.getErrCode() + " " + result.getErrText());
    				this.messageBox("����ʧ��");
    			} 
    		}else{
    			this.messageBox("��Ȳ��㣬���ɼ���");
    		}
    	}
    	
    	return result;
	}
	
//	/**
//	 * ��ʼ��Ʊ��
//	 * 
//	 * ===zhangp 20120731
//	 * 
//	 * @param bilInvoice
//	 *            BilInvoice
//	 * @return boolean
//	 */
//	private boolean initBilInvoice(BilInvoice bilInvoice) {
//		// ��˿�����
//		if (bilInvoice == null) {
//			this.messageBox_("����δ����!");
//			return false;
//		}
//		// ��˵�ǰƱ��
//		if (bilInvoice.getUpdateNo().length() == 0
//				|| bilInvoice.getUpdateNo() == null) {
//			this.messageBox_("�޿ɴ�ӡ��Ʊ��!");
//			return false;
//		}
//		// ��˵�ǰƱ��
//		if (bilInvoice.getUpdateNo().equals(bilInvoice.getEndInvno())) {
//			this.messageBox_("���һ��Ʊ��!");
//		}
//		
//		if (BILTool.getInstance().compareUpdateNo("HPAY", Operator.getID(),
//				Operator.getRegion(), bilInvoice.getUpdateNo())) {
//			printNo= bilInvoice.getUpdateNo();	
//			System.out.println("printNo:::----->"+printNo);
//			startInvno = bilInvoice.getStartInvno();
//			System.out.println("startInvno:::------->"+startInvno);
//		} else {
//			messageBox("Ʊ��������");
//			return false;
//		}
//		return true;
//	}
	
	/**
     * У�鿪����
     *
     * @return boolean
     */
    public boolean checkNo() {
        TParm parm = new TParm();
        parm.setData("RECP_TYPE", "OPB");
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("STATUS", "0");
        parm.setData("TERM_IP", Operator.getIP());
        TParm noParm = BILInvoiceTool.getInstance().selectNowReceipt(parm);
        printNo = noParm.getValue("UPDATE_NO", 0);
        startInvno = "";
        startInvno = noParm.getValue("START_INVNO", 0);
        if (printNo == null || printNo.length() == 0) {
            return false;
        }
        // ��ʼ����һƱ��
        BilInvoice invoice = new BilInvoice();
        invoice = invoice.initBilInvoice("OPB");
        if (invoice.getUpdateNo().compareTo(invoice.getEndInvno()) > 0) {
            this.messageBox("Ʊ��������!");
            return false;
        }
        setValue("PRINT_NO", printNo);
        return true;
    }
 
//	/**
//	 * ȫѡ��ť
//	 */
//	public void onSelectAll() {
//		TParm parm = table.getParmValue();
//		if (parm.getCount() <= 0) {
//			return;
//		}
//		// ȫѡ��ѡ
//		if (this.getValueBoolean("CHK_ALL")) {
//			for (int i = 0; i < parm.getCount(); i++) {
//				parm.setData("FLG",i,"Y");
//				
//			}
//		} else {
//			for (int i = 0; i < parm.getCount(); i++) {
//				parm.setData("FLG",i,"N");	
//			}
//			
//		}
//		table.setParmValue(parm);
//	}
}
