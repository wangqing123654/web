package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.bil.BILContractPayTool;
import jdo.bil.BILPayTool;
import jdo.bil.BILTool;
import jdo.bil.BilInvoice;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.javahis.util.StringUtil;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;




/**
 * 
 * <p>
 * Title: ��ͬ��λԤ������������ά��
 * </p>
 * 
 * <p>
 * Description: ��ͬ��λԤ������������ά��
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
public class BILContractPayControl extends TControl {

	// action��·��
	private static final String actionName = "action.bil.BILContractAction";


	private TTable table;
	String startInvno = "";
	String printNo ="";
	public BILContractPayControl() {

	}

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		//�õ���
		table =(TTable) this.getComponent("Table");
		// ��ѯȫ����Ϣ
		this.onQuery();
		// ��ӱ�ļ�����Ϣ
		callFunction("UI|Table|addEventListener", "Table->"
				+ TTableEvent.CLICKED, this, "onTableClickedForTable");
	}

	/**
	 * ��ͬ��λ������ѯ
	 * */
	public void onContractCompany(){
		String contract_code = this.getValueString("CONTRACT_CODE");
		//��ѯ��ͬ��λ������Ϣ,������Ϣ��ʾ��������
		String sql = "SELECT REGION_CODE,CONTRACT_CODE,CONTACT,TEL2,LIMIT_AMT,PREPAY_AMT"+
						" FROM BIL_CONTRACTM"+
						" WHERE CONTRACT_CODE = '"+contract_code+"'";			
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if(selParm.getCount()<=0){
			this.onClear();
			return;
		}
		this.setValue("REGION_CODE", selParm.getData("REGION_CODE",0));
		this.setValue("CONTRACT_CODE", selParm.getData("CONTRACT_CODE",0));
		this.setValue("CONTACT", selParm.getData("CONTACT",0));
		this.setValue("TEL", selParm.getData("TEL2",0));
		this.setValue("LIMIT_AMT", selParm.getData("LIMIT_AMT",0)==null ? 0.0 : selParm.getData("LIMIT_AMT",0).toString());//���ö��		
		this.setValue("PRE_AMT", selParm.getData("PREPAY_AMT",0)==null ? 0.0 : selParm.getData("PREPAY_AMT",0).toString());//Ԥ�������							
		onQuery();
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
	 * ������
	 * */
	public void onTableClickedForTable(int row){
		if (row < 0) {
			return;
		}
		// ��ȡ���ڱ༭״̬������
		table.acceptText();		
		
		/*
		 * ��ȡѡ�е����ݣ�����Щ�������õ���������ؼ���
		 */
		String receipt_no = nullToString(String.valueOf(table.getParmValue().getData("RECEIPT_NO",row)));
		String sql = "SELECT CONTRACT_CODE FROM BIL_CONTRACT_PAY WHERE RECEIPT_NO = '"+receipt_no+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()<=0){
			return;
		}
		String contract_code = parm.getValue("CONTRACT_CODE",0);
		
		String sql1 = " SELECT A.PAY_TYPE AS PAY_TYPE,A.REMARK AS REMARK,A.RECEIPT_NO AS RECEIPT_NO,A.CHARGE_DATE AS CHARGE_DATE,A.PRE_AMT AS PRE_PAY,A.BUSINESS_TYPE," +
				" B.REGION_CODE AS REGION_CODE,B.CONTRACT_CODE AS CONTRACT_CODE ,B.TEL2 AS TEL,B.LIMIT_AMT AS LIMIT_AMT,B.PREPAY_AMT AS PRE_AMT,B.CONTACT"+
				" FROM BIL_CONTRACT_PAY A,BIL_CONTRACTM B "+
		        " WHERE A.CONTRACT_CODE = B.CONTRACT_CODE "+
		        " AND A.CONTRACT_CODE = '"+contract_code+"'" +
        		" AND A.RECEIPT_NO = '"+receipt_no+"' ";
		
		TParm parms = new TParm(TJDODBTool.getInstance().select(sql1));		
		this.setValue("REGION_CODE",parms.getData("REGION_CODE", 0));
		this.setValue("CONTRACT_CODE", parms.getData("CONTRACT_CODE", 0));
		this.setValue("CONTACT", parms.getData("CONTACT", 0));
		this.setValue("TEL", parms.getData("TEL", 0));
		this.setValue("LIMIT_AMT",parms.getValue("LIMIT_AMT", 0).toString());
		this.setValue("PRE_AMT",parms.getValue("PRE_AMT",0).toString());
		//System.out.println(parms.getData("PRE_PAY", 0));
		this.setValue("PRE_PAY", parms.getValue("PRE_PAY",0).toString());
		this.setValue("PAY_TYPE",parms.getData("PAY_TYPE", 0));
		this.setValue("REMARK",parms.getData("REMARK", 0));
		
	}
	/**
	 * ��ѯ
	 * */
	public TParm onQuery() {
		//��ѯĳ����ͬ��λ�µ�����Ԥ������Ϣ
		String contract_code = this.getValueString("CONTRACT_CODE");
		String sql = " SELECT A.PAY_TYPE AS PAY_TYPE,A.REMARK AS REMARK,A.RECEIPT_NO AS RECEIPT_NO,A.CHARGE_DATE AS CHARGE_DATE,A.PRE_AMT AS PRE_PAY,A.BUSINESS_TYPE," +
						" B.REGION_CODE AS REGION_CODE,B.CONTRACT_CODE AS CONTRACT_CODE ,B.TEL2 AS TEL,B.LIMIT_AMT AS LIMIT_AMT,B.PREPAY_AMT AS PRE_AMT,A.REFUND_CODE,TO_CHAR(A.REFUND_DATE,'yyyy/MM/dd') AS REFUND_DATE,A.OFF_RECP_NO"+
						" FROM BIL_CONTRACT_PAY A,BIL_CONTRACTM B "+
                        " WHERE A.CONTRACT_CODE = B.CONTRACT_CODE "+
                        " AND A.CONTRACT_CODE = '"+contract_code+"'";
		 
		 TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("selParm:"+sql);
		 this.callFunction("UI|Table|setParmValue", selParm);	
		 return selParm;
	}
	/**
	 * ����
	 * */
	public void onSave(){
		BilInvoice bilInvoice = new BilInvoice();
		//��ʼ��Ʊ��
       	if (!initBilInvoice(bilInvoice.initBilInvoice("HPAY"))) {//Ʊ�����ͻ���Ҫȷ��
            return;
        }      
        if (this.getValueString("CONTRACT_CODE") == null ||
            this.getValueString("CONTRACT_CODE").length() == 0) {
            this.messageBox("�������ͬ��λ");
            return;
        }
        if (getValue("PRE_PAY") == null ||
            TypeTool.getDouble(getValue("PRE_PAY")) <= 0) {
            this.messageBox("����������!");
            return;
        }
        //���������ֵ
        if (!this.emptyTextCheck("PAY_TYPE")){       	
        	return;
        }
        //֧����ʽ����Ϊ����
        if(this.getValue("PAY_TYPE").equals("PAY_DEBIT")){
        	this.messageBox("֧����ʽ����Ϊ����");
        	return;
        }
     
       //��ϲ���    
            TParm parm = new TParm();
            //2-15Ϊbil_contract_pay�Ĳ���
            parm.setData("CONTRACT_CODE",this.getValue("CONTRACT_CODE"));//2
            parm.setData("BUSINESS_TYPE","1");//3
            parm.setData("CASHIER_CODE", Operator.getID());//4
            parm.setData("CHARGE_DATE", SystemTool.getInstance().getDate());//5
            parm.setData("PRE_PAY",this.getValue("PRE_PAY"));//6
            parm.setData("OFF_RECP_NO","");//10
            parm.setData("REFUND_DATE","");//9
            parm.setData("REFUND_CODE","");//8         
            parm.setData("REFUND_FLG", "N");//7
            parm.setData("OPT_DATE", SystemTool.getInstance().getDate());//13
            parm.setData("OPT_USER", Operator.getID());//12
            parm.setData("OPT_TERM", Operator.getIP());//14            
            parm.setData("REMARK",this.getValue("REMARK"));//11
            parm.setData("PAY_TYPE",getValue("PAY_TYPE"));//15
            parm.setData("TRANSACT_TYPE", "01");          
            parm.setData("START_INVNO", startInvno);          
            parm.setData("INV_NO",printNo); 
            System.out.println("startInvno:::"+startInvno);
            System.out.println("printNo:::"+printNo);
            TParm result = TIOM_AppServer.executeAction(actionName,
                "onContractBillPay", parm);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return;
            }  
            this.messageBox("��ͬ��λԤ���ɹ���");
            parm.setData("RECEIPT_NO",result.getValue("RECEIPT_NO",0));
            parm.setData("REGION_CODE",this.getValue("REGION_CODE"));
            parm.setData("TEL",this.getValue("TEL"));
            parm.setData("LIMIT_AMT",this.getValue("LIMIT_AMT"));
            parm.setData("PRE_AMT",this.getValueDouble("LIMIT_AMT")+this.getValueDouble("PRE_PAY"));
            parm.setData("REFUND_CODE","");
            parm.setData("REFUND_DATE","");
            parm.setData("OFF_RECP_NO","");
            //table�ϼ���������������ʾ
           //CHARGE_DATE;RECEIPT_NO;PAY_TYPE;PRE_PAY;REMARK;REGION_CODE;CONTRACT_CODE;TEL;LIMIT_AMT;PRE_AMT 
            callFunction("UI|Table|addRow", parm,
                         "CHARGE_DATE;RECEIPT_NO;PAY_TYPE;BUSINESS_TYPE;PRE_PAY;REMARK;REFUND_CODE;REFUND_DATE;OFF_RECP_NO;REGION_CODE;CONTRACT_CODE;TEL;LIMIT_AMT;PRE_AMT");
            //��ӡԤ�����վ�
            TParm forPrtParm = new TParm();
            String bilPayC = StringUtil.getInstance().numberToWord(TypeTool.
                    getDouble(getValue("PRE_PAY")));
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
                               TypeTool.getDouble(this.getValueString("PRE_PAY"))));
            forPrtParm.setData("SEQ_NO", "TEXT",
                               "��ˮ��:" + this.getValueString("RECEIPT_NO"));
            forPrtParm.setData("PRINT_DATE", "TEXT", "��ӡ����:" + pDate);
            if (this.getValueString("PAY_TYPE").equals("PAY_CASH")) {

            	forPrtParm.setData("WAY","TEXT","�ֽ�") ;
            }
            else if (this.getValueString("PAY_TYPE").equals("PAY_BANK_CARD")) {

                    forPrtParm.setData("WAY","TEXT","���п�") ;
            }
            else if (this.getValueString("PAY_TYPE").equals("PAY_CHECK")) {

                    forPrtParm.setData("WAY","TEXT","֧Ʊ") ;
            }
            else {

            	forPrtParm.setData("WAY","TEXT","����") ;
            }

            this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILContractPrepayment.jhw",
                                 forPrtParm,true);
            this.onClear();
            
	}
	
	
	/**
	 * ��ʼ��Ʊ��
	 * 
	 * ===zhangp 20120731
	 * 
	 * @param bilInvoice
	 *            BilInvoice
	 * @return boolean
	 */
	private boolean initBilInvoice(BilInvoice bilInvoice) {
		// ��˿�����
		if (bilInvoice == null) {
			this.messageBox_("����δ����!");
			return false;
		}
		// ��˵�ǰƱ��
		if (bilInvoice.getUpdateNo().length() == 0
				|| bilInvoice.getUpdateNo() == null) {
			this.messageBox_("�޿ɴ�ӡ��Ʊ��!");
			return false;
		}
		// ��˵�ǰƱ��
		if (bilInvoice.getUpdateNo().equals(bilInvoice.getEndInvno())) {
			this.messageBox_("���һ��Ʊ��!");
		}
		
		if (BILTool.getInstance().compareUpdateNo("HPAY", Operator.getID(),
				Operator.getRegion(), bilInvoice.getUpdateNo())) {
			printNo= bilInvoice.getUpdateNo();			
			startInvno = bilInvoice.getStartInvno();
		} else {
			messageBox("Ʊ��������");
			return false;
		}
		return true;
	}
	
	/**
	 * �˷�
	 * */
	public void onReturnFee(){
		 int row = (Integer) callFunction("UI|Table|getSelectedRow");
	        if (row < 0)
	            return;
	        TParm parm = (TParm) callFunction("UI|Table|getParmValue");
	        String receiptNo = parm.getValue("RECEIPT_NO", row);
	        TParm actionParm = new TParm();
	        actionParm.setData("RECEIPT_NO",receiptNo);
	        actionParm =  BILContractPayTool.getInstance().selectData("selectByReceipt",actionParm);//???????????????
	        TParm endParm = new TParm();
	        endParm = actionParm.getRow(0);
	        endParm.setData("RESET_BIL_PAY_NO", parm.getData("RECEIPT_NO", row));
	        endParm.setData("REFUND_CODE", Operator.getID());	      
	        endParm.setData("CASHIER_CODE", Operator.getID());
	        endParm.setData("CHARGE_DATE", SystemTool.getInstance().getDate());	       
	        endParm.setData("REFUND_DATE", SystemTool.getInstance().getDate());
	        endParm.setData("OPT_USER", Operator.getID());
	        endParm.setData("OPT_TERM", Operator.getIP());
	        endParm.setData("OPT_DATE",SystemTool.getInstance().getDate());
	        endParm.setData("PAY_TYPE",parm.getData("PAY_TYPE", row));
	        endParm.setData("PRINT_NO",parm.getData("PRINT_NO",row));
	        TParm endDate = TIOM_AppServer.executeAction(actionName,
	            "onReturnBillContractPay", endParm);
	        if (endDate.getErrCode() < 0) {
	            err(endDate.getErrCode() + " " + endDate.getErrText());
	            return;
	        }
	        this.messageBox("P0005");
	        //��ѯ����
	        TParm allParm = new TParm();
	        allParm = this.onQuery();
	        //��������
	        //��table���
	        this.callFunction("UI|Table|setParmValue", allParm);
	}
	
	/**
	 * ��ӡ
	 * */
	public void onPrint(){
		TTable table = (TTable)this.getComponent("Table");
		
        if (table.getSelectedRow() < 0) {
            this.messageBox("���ѡ����");
            return;
        }
        String sql = "SELECT CONTRACT_DESC FROM BIL_CONTRACTM WHERE CONTRACT_CODE = '"+this.getValue("CONTRACT_CODE")+"'";
        TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
        String contract_desc = selParm.getValue("CONTRACT_DESC",0);
        //��ӡԤ�����վ�
        TParm forPrtParm = new TParm();
        String bilPayC = StringUtil.getInstance().numberToWord(TypeTool.
            getDouble(getValue("PRE_PAY")));
        Timestamp printDate = (SystemTool.getInstance().getDate());
        String pDate = StringTool.getString(printDate, "yyyy/MM/dd HH:mm:ss");
        forPrtParm.setData("COPY", "TEXT", "");
        forPrtParm.setData("REGION_CHN_DESC", "TEXT",
                           Operator.getHospitalCHNFullName());
        forPrtParm.setData("REGION_ENG_DESC", "TEXT",
                           Operator.getHospitalENGFullName());
        int row = (Integer) callFunction("UI|Table|getSelectedRow");
        if (row < 0)
            return;
        TParm parm = (TParm) callFunction("UI|Table|getParmValue");
        forPrtParm.setData("Data", "TEXT",StringTool.getString(parm.
                                                getTimestamp("CHARGE_DATE", row),
                                                "yyyy/MM/dd HH:mm:ss"));
        forPrtParm.setData("CHECK_NO","TEXT",this.getValue("CHECK_NO")) ;
        forPrtParm.setData("REMARK","TEXT",this.getValue("REMARK")) ;
        forPrtParm.setData("Name", "TEXT", contract_desc);
        forPrtParm.setData("TOLL_COLLECTOR", "TEXT", Operator.getID());
        forPrtParm.setData("MR_N0", "TEXT", this.getValue("CONTRACT_CODE"));     
        forPrtParm.setData("Capital", "TEXT", bilPayC);
        DecimalFormat formatObject = new DecimalFormat("###########0.00");

        forPrtParm.setData("SmallCaps", "TEXT",formatObject.format(
                           TypeTool.getDouble(this.getValueString("PRE_PAY"))));
        forPrtParm.setData("SEQ_NO", "TEXT",
                           "��ˮ��:" + this.getValueString("RECEIPT_NO"));
        forPrtParm.setData("PRINT_DATE", "TEXT", "��ӡ����:" + pDate);
        if (this.getValueString("PAY_TYPE").equals("PAY_CASH")) {
        	forPrtParm.setData("WAY","TEXT","�ֽ�") ;
        }
        else if (this.getValueString("PAY_TYPE").equals("PAY_BANK_CARD")) {
            forPrtParm.setData("WAY","TEXT","���п�") ;
        }
        else if (this.getValueString("PAY_TYPE").equals("PAY_CHECK")) {
                forPrtParm.setData("WAY","TEXT","֧Ʊ") ;
        }
        else {
        	forPrtParm.setData("WAY","TEXT","����") ;
        }

        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILContractPrepayment.jhw",
                             forPrtParm,true);
        this.onClear();
	}
	/**
	 * ���
	 * */
	public void onClear(){
		this.setValue("REGION_CODE", "");
		this.setValue("CONTRACT_CODE", "");
		this.setValue("CONTACT", "");
		this.setValue("TEL", "");
		this.setValue("LIMIT_AMT", "");
		this.setValue("PRE_AMT", "");
		this.setValue("PRE_PAY", "");
		this.setValue("PAY_TYPE", "");
		this.setValue("REMARK", "");		
	}
	

}
