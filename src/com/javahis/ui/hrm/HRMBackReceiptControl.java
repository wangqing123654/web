package com.javahis.ui.hrm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.bil.BilInvoice;
import jdo.hl7.Hl7Communications;
import jdo.hrm.HRMInvRcp;
import jdo.hrm.HRMOpbReceipt;
import jdo.hrm.HRMOrder;
import jdo.opb.OPBReceiptTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
/**
 * <p> Title: ���������ò�ѯ������ </p>
 *
 * <p> Description: ���������ò�ѯ������ </p>
 *
 * <p> Copyright: javahis 20090922 </p>
 *
 * <p> Company:JavaHis </p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMBackReceiptControl extends TControl {
	//�ַ�������
	private StringUtil util;
	//Ʊ�ݶ���
	private HRMOpbReceipt receipt;
	//ҽ������
	private HRMOrder order;
	//����ϸ��TABLE
	private TTable tableM,tableD;
	//CASE_NO
	private String caseNo;
	//�վݶ���
	private HRMInvRcp invRcp;
	//Ʊ�Ź������
	private BilInvoice bilInvoice;
	//��ƱTABLE������
	private int receiptRow;
	//Ĭ��֧����ʽ
	private String payType;
	//����HL7��Ϣ��TPARM
	private List disList;
	 /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        initComponent();
        if(!onInitData()){
        	this.messageBox_("ȡ������ʧ��");
        }
    }
    /**
     * ��ʼ���ؼ�
     */
    private void initComponent(){
    	tableM=(TTable)this.getComponent("TABLEM");
    	tableD=(TTable)this.getComponent("TABLED");
		//�ײ�����ֵ�ı��¼�
    	tableD.addEventListener("TABLED->"+TTableEvent.CHANGE_VALUE, this,
		"onValueChanged");
    	tableD.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
		"onCheckBox");
    }
    /**
     * ��ʼ������
     */
    public boolean onInitData(){
    	//ȡ�ô������
    	TParm parm=(TParm)this.getParameter();
    	//ȡ�ô���CASE_NO
    	caseNo=parm.getValue("CASE_NO");
    	if(util.isNullString(caseNo)){
    		return false;
    	}
    	//ȡ�ò�����Ϣ
    	TParm patParm=(TParm)parm.getData("PAT_PARM");
    	this.setValueForParm("PAT_NAME;MR_NO;IDNO;SEX_CODE", patParm);
    	receipt=new HRMOpbReceipt();
    	order=new HRMOrder();
    	receipt.onQuryByCaseNo(caseNo);
    	invRcp=new HRMInvRcp();
    	tableM.setDataStore(receipt);
    	tableM.setDSValue();
    	receiptRow=-1;
//    	payType=REGSysParmTool.getInstance().selPayWay().getValue("DEFAULT_PAY_WAY",0);;
        payType = "C0";
    	return true;
    }
    /**
     * ��TABLE�����¼�
     */
    public void onClickTableM(){
    	receiptRow=tableM.getSelectedRow();
    	if(receiptRow<0){
    		return;
    	}
    	//ȡ��Ʊ�ݺ�
    	String receiptNo=receipt.getItemString(receiptRow, "RECEIPT_NO");
    	if(StringUtil.isNullString(receiptNo)){
    		return;
    	}
    	order.onQueryByReceiptNo(receiptNo);
    	order.setFilter("CASE_NO='" +caseNo+"' AND SETMAIN_FLG='Y'");
    	order.filter();
    	//// System.out.println("after click");
    	//order.showDebug();
    	tableD.setDataStore(order);
    	tableD.setDSValue();
    }
    /**
     * ��ӡOPB_RECEIPT����PRINT_NO��PRINT_DATE��BIL_INVRCP����ԭ�ȵ�CANCEL_FLGΪ1��CANCEL_USER,CANCEL_DATE�����²���һ��
     */
    public void onPrint(){
    	bilInvoice=new BilInvoice("OPB");
    	String updateNo=getNextUpdateNo();
    	if(StringUtil.isNullString(updateNo)){
    		return;
    	}
    	String oldPrintNo=receipt.getItemString(this.receiptRow, "PRINT_NO");
    	String[] receiptNo=new String[]{receipt.getItemString(tableM.getSelectedRow(), "RECEIPT_NO")};
    	String patName=this.getValueString("PAT_NAME");
    	String sexCode=this.getValueString("SEX_CODE");
    	if(!receipt.onRePrint(caseNo, order.getItemString(0, "RECEIPT_NO"), updateNo)){
    		this.messageBox_("��дƱ��ʧ��");
    		return;
    	}
    	if(!invRcp.onRePrint(oldPrintNo,updateNo)){
    		this.messageBox_("��д��Ʊʧ��");
    		return;
    	}
    	String[] sql=receipt.getUpdateSQL();
    	sql=StringTool.copyArray(sql, invRcp.getUpdateSQL());
    	String[] tempSql=new String[]{getBilInvoiceUpdate()};
    	sql=StringTool.copyArray(sql, tempSql);
    	TParm inParm = new TParm();

		Map inMap = new HashMap();
                for(String temp:sql){
                    // System.out.println("RECEIPT=="+temp);
                }
		inMap.put("SQL", sql);
		inParm.setData("IN_MAP", inMap);
		TParm result = TIOM_AppServer.executeAction(
				"action.hrm.HRMPersonReportAction", "onSave", inParm);
		if (result.getErrCode() != 0) {
			this.messageBox_(result.getErrText());
			this.messageBox("E0001");
			return;
		}
		dealPrintData(receiptNo,patName,Operator.getDept(),sexCode);
		this.closeWindow();
    }
    /**
     * �����ӡ����
     * @param receiptNo String[]
     */
    public void dealPrintData(String[] receiptNo,String patName,String deptCode,String sexCode){
        int size=receiptNo.length;
        for(int i=0;i<size;i++){
            //ȡ��һ��Ʊ�ݺ�
            String recpNo=receiptNo[i];
            if(recpNo==null||recpNo.length()==0)
                return ;
            //���ô�ӡһ��Ʊ�ݵķ���
//            onPrint(new OPBReceipt().getOneReceipt(recpNo),patName,deptCode,sexCode);
            onPrint(OPBReceiptTool.getInstance().getOneReceipt(recpNo),patName,deptCode,sexCode,recpNo);
        }
    }
    /**
     * ��ӡƱ��
     * @param receiptOne OPBReceipt
     */
    public void onPrint(TParm recpParm,String patName,String deptCode,String sexCode,String receiptNo){
        if(recpParm==null)
            return;
        TParm oneReceiptParm = new TParm();
          //Ʊ����Ϣ
          oneReceiptParm.setData("CASE_NO",recpParm.getData("CASE_NO",0));
          oneReceiptParm.setData("RECEIPT_NO",receiptNo);
          oneReceiptParm.setData("MR_NO",recpParm.getData("MR_NO",0));
          oneReceiptParm.setData("BILL_DATE",recpParm.getData("BILL_DATE",0));
          oneReceiptParm.setData("CHARGE_DATE",recpParm.getData("CHARGE_DATE",0));
          oneReceiptParm.setData("CHARGE01",recpParm.getData("CHARGE01",0));
          oneReceiptParm.setData("CHARGE02",recpParm.getData("CHARGE02",0));
          oneReceiptParm.setData("CHARGE03",recpParm.getData("CHARGE03",0));
          oneReceiptParm.setData("CHARGE04",recpParm.getData("CHARGE04",0));
          oneReceiptParm.setData("CHARGE05",recpParm.getData("CHARGE05",0));
          oneReceiptParm.setData("CHARGE06",recpParm.getData("CHARGE06",0));
          oneReceiptParm.setData("CHARGE07",recpParm.getData("CHARGE07",0));
          oneReceiptParm.setData("CHARGE08",recpParm.getData("CHARGE08",0));
          oneReceiptParm.setData("CHARGE09",recpParm.getData("CHARGE09",0));
          oneReceiptParm.setData("CHARGE10",recpParm.getData("CHARGE10",0));
          oneReceiptParm.setData("CHARGE11",recpParm.getData("CHARGE11",0));
          oneReceiptParm.setData("CHARGE12",recpParm.getData("CHARGE12",0));
          oneReceiptParm.setData("CHARGE13",recpParm.getData("CHARGE13",0));
          oneReceiptParm.setData("CHARGE14",recpParm.getData("CHARGE14",0));
          oneReceiptParm.setData("CHARGE15",recpParm.getData("CHARGE15",0));
          oneReceiptParm.setData("CHARGE16",recpParm.getData("CHARGE16",0));
          oneReceiptParm.setData("CHARGE17",recpParm.getData("CHARGE17",0));
          oneReceiptParm.setData("CHARGE18",recpParm.getData("CHARGE18",0));
          oneReceiptParm.setData("CHARGE19",recpParm.getData("CHARGE19",0));
          oneReceiptParm.setData("CHARGE20",recpParm.getData("CHARGE20",0));
          oneReceiptParm.setData("CHARGE21",recpParm.getData("CHARGE21",0));
          oneReceiptParm.setData("CHARGE22",recpParm.getData("CHARGE22",0));
          oneReceiptParm.setData("CHARGE23",recpParm.getData("CHARGE23",0));
          oneReceiptParm.setData("CHARGE24",recpParm.getData("CHARGE24",0));
          oneReceiptParm.setData("CHARGE25",recpParm.getData("CHARGE25",0));
          oneReceiptParm.setData("CHARGE26",recpParm.getData("CHARGE26",0));
          oneReceiptParm.setData("CHARGE27",recpParm.getData("CHARGE27",0));
          oneReceiptParm.setData("CHARGE28",recpParm.getData("CHARGE28",0));
          oneReceiptParm.setData("CHARGE29",recpParm.getData("CHARGE29",0));
          oneReceiptParm.setData("CHARGE30",recpParm.getData("CHARGE30",0));
          oneReceiptParm.setData("TOT_AMT",StringTool.round(recpParm.getDouble("TOT_AMT",0), 2));
          oneReceiptParm.setData("AR_AMT",
                                 StringTool.round(recpParm.getDouble("AR_AMT", 0), 2));
          oneReceiptParm.setData("CASHIER_CODE",recpParm.getData("CASHIER_CODE",0));

          oneReceiptParm.setData("PAT_NAME", patName);
          oneReceiptParm.setData("DEPT_CODE", deptCode);
          oneReceiptParm.setData("SEX_CODE", sexCode);
          oneReceiptParm.setData("OPT_USER", Operator.getName());
          oneReceiptParm.setData("OPT_ID", Operator.getID());
          oneReceiptParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
          oneReceiptParm.setData("PRINT_DATE", StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd HH:mm:ss"));
          oneReceiptParm.setData("PRINT_NO", recpParm.getData("PRINT_NO",0));
          TParm patParm = new TParm(TJDODBTool.getInstance().select("SELECT B.DEPT_CHN_DESC FROM HRM_PATADM A,SYS_DEPT B WHERE A.CASE_NO='"+this.caseNo+"' AND A.DEPT_CODE=B.DEPT_CODE"));
          oneReceiptParm.setData("MR_NO","TEXT", "������:"+recpParm.getData("MR_NO",0));
          oneReceiptParm.setData("DEPT_CODE","TEXT", "����:"+patParm.getValue("DEPT_CHN_DESC",0));
          oneReceiptParm.setData("CLINICROOM_DESC","TEXT","");
          oneReceiptParm.setData("DR_CODE","TEXT","");
          oneReceiptParm.setData("QUE_NO","TEXT","");
          oneReceiptParm.setData("PAT_NAME","TEXT", patName);
          oneReceiptParm.setData("HOSP_DESC", "TEXT",
                                 Manager.getOrganization().
                                 getHospitalCHNFullName(Operator.getRegion()));
          oneReceiptParm.setData("RECEIPT_NO","TEXT",receiptNo);
          oneReceiptParm.setData("PRINT_NO","TEXT", recpParm.getData("PRINT_NO",0));
          oneReceiptParm.setData("TOT_AMT","TEXT",StringTool.round(recpParm.getDouble("TOT_AMT",0), 2));
          oneReceiptParm.setData("AMT_TO_WORD", "TEXT",
                                 "" +
                                 StringUtil.getInstance().
                                 numberToWord(StringTool.
                                              round(recpParm.getDouble("TOT_AMT", 0),
                                                    2)));
          oneReceiptParm.setData("OPT_USER","TEXT","����Ա:"+ Operator.getName());
          String printDate =  StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd HH:mm:ss");
          oneReceiptParm.setData("PRINT_DATE","TEXT",printDate);
          oneReceiptParm.setData("YEAR","TEXT",printDate.substring(0,4));
          oneReceiptParm.setData("MONTH","TEXT",printDate.substring(5,7));
          oneReceiptParm.setData("DAY","TEXT",printDate.substring(8,10));
          oneReceiptParm.setData("OPT_ID","TEXT", Operator.getID());

          oneReceiptParm.setData("CHARGE01","TEXT",recpParm.getData("CHARGE01",0));
          oneReceiptParm.setData("CHARGE02","TEXT",recpParm.getData("CHARGE02",0));
          oneReceiptParm.setData("CHARGE03","TEXT",recpParm.getData("CHARGE03",0));
          oneReceiptParm.setData("CHARGE04","TEXT",recpParm.getData("CHARGE04",0));
          oneReceiptParm.setData("CHARGE05","TEXT",recpParm.getData("CHARGE05",0));
          oneReceiptParm.setData("CHARGE06","TEXT",recpParm.getData("CHARGE06",0));
          oneReceiptParm.setData("CHARGE07","TEXT",recpParm.getData("CHARGE07",0));
          oneReceiptParm.setData("CHARGE08","TEXT",recpParm.getData("CHARGE08",0));
          oneReceiptParm.setData("CHARGE09","TEXT",recpParm.getData("CHARGE09",0));
          oneReceiptParm.setData("CHARGE10","TEXT",recpParm.getData("CHARGE10",0));
          oneReceiptParm.setData("CHARGE11","TEXT",recpParm.getData("CHARGE11",0));
          oneReceiptParm.setData("CHARGE12","TEXT",recpParm.getData("CHARGE12",0));
          oneReceiptParm.setData("CHARGE13","TEXT",recpParm.getData("CHARGE13",0));
          oneReceiptParm.setData("CHARGE14","TEXT",recpParm.getData("CHARGE14",0));
          oneReceiptParm.setData("CHARGE15","TEXT",recpParm.getData("CHARGE15",0));
          oneReceiptParm.setData("CHARGE16","TEXT",recpParm.getData("CHARGE16",0));
          oneReceiptParm.setData("CHARGE17","TEXT",recpParm.getData("CHARGE17",0));
          oneReceiptParm.setData("CHARGE18","TEXT",recpParm.getData("CHARGE18",0));
          oneReceiptParm.setData("CHARGE19","TEXT",recpParm.getData("CHARGE19",0));
          oneReceiptParm.setData("CHARGE20","TEXT",recpParm.getData("CHARGE20",0));
          oneReceiptParm.setData("CHARGE21","TEXT",recpParm.getData("CHARGE21",0));
          oneReceiptParm.setData("CHARGE22","TEXT",recpParm.getData("CHARGE22",0));
          oneReceiptParm.setData("CHARGE23","TEXT",recpParm.getData("CHARGE23",0));
          oneReceiptParm.setData("CHARGE24","TEXT",recpParm.getData("CHARGE24",0));
          oneReceiptParm.setData("CHARGE25","TEXT",recpParm.getData("CHARGE25",0));
          oneReceiptParm.setData("CHARGE26","TEXT",recpParm.getData("CHARGE26",0));
          oneReceiptParm.setData("CHARGE27","TEXT",recpParm.getData("CHARGE27",0));
          oneReceiptParm.setData("CHARGE28","TEXT",recpParm.getData("CHARGE28",0));
          oneReceiptParm.setData("CHARGE29","TEXT",recpParm.getData("CHARGE29",0));
          oneReceiptParm.setData("CHARGE30","TEXT",recpParm.getData("CHARGE30",0));
        this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBReceipt.jhw", oneReceiptParm);

  }
    /**
     * ȡ����һƱ̖
     * @return
     */
    private String getNextUpdateNo(){
    	String updateNo=bilInvoice.getUpdateNo();
		this.setValue("UPDATE_NO", updateNo);
		if (updateNo.equalsIgnoreCase(bilInvoice.getEndInvno())) {
			this.messageBox_("�޿ɴ�ӡƱ��");
			return "";
		}
		if (StringTool.addString(updateNo).equalsIgnoreCase(
				bilInvoice.getEndInvno())) {
			this.messageBox_("��ʣ��һ��Ʊ��");
		}
		return updateNo;
    }
    /**
     * ��ӡʱ����Ʊ�ݵ��ĸ������
     * @return
     */
    private String getBilInvoiceUpdate(){
		String sqlUpdate = "UPDATE BIL_INVOICE SET UPDATE_NO='#' WHERE RECP_TYPE='OPB' AND START_INVNO='#'";
		sqlUpdate = sqlUpdate.replaceFirst("#", StringTool.addString(bilInvoice
				.getUpdateNo())).replaceFirst("#", bilInvoice.getStartInvno());
		//// System.out.println("sqlUpdate=" + sqlUpdate);
		return sqlUpdate;
    }
	/**
	 * TABLE��checkBox�¼�
	 * @param obj
	 * @return
	 */
	public boolean onCheckBox(Object obj){
		TTable table=(TTable)obj;
		table.acceptText();
		return false;

	}
    /**
	 * ֵ�ı��¼�
	 * @param tNode
	 * @return
	 */
	public boolean onValueChanged(TTableNode tNode){
		int row=tNode.getRow();
		int column=tNode.getColumn();
		String colName=tNode.getTable().getParmMap(column);
		if(!"PRINT_FLG".equalsIgnoreCase(colName)){
			this.messageBox_("not printFlg");
			return true;
		}
		order.setItem(row, "PRINT_FLG", tNode.getValue());
		tableD.setDSValue();
		return false;
	}
	/**
	 * �˷ѣ����˷�ҽ��ɾ������BIL_OPB_RECEIPT�����ݱ�ɸ�������һ���µ����ݣ����¾ɵ�BIL_INVRCP���ݵ�CANCEL_FLGΪ2��CANCEL_USER,CANCEL_DATE
	 */
	public void onDisCharge(){
		bilInvoice=new BilInvoice("OPB");
		if(this.receiptRow<0){
			this.messageBox_("��ѡ��һ���վ��˷�");
			return;
		}
		int count=order.rowCount();
                //// System.out.println("count"+count);
		if(count<=0){
			return;
		}
		if(!isOrderRemovable()){
			this.messageBox_("���վ��Ѿ���ִ�е�ҽ���������˷�");
			return;
		}
		String oldPrintNo=receipt.getItemString(this.receiptRow, "PRINT_NO");
                //// System.out.println("oldPrintNo"+oldPrintNo);
		if(!StringUtil.isNullString(invRcp.getItemString(0, "CANCEL_FLG"))&&!"0".equalsIgnoreCase(invRcp.getItemString(0, "CANCEL_FLG"))){
			this.messageBox_("���˷�ҽ�������ظ��˷�");
			return;
		}
		receipt.onDisCharge(this.receiptRow, oldPrintNo,payType);
		receipt.setItem(receiptRow, "RESET_RECEIPT_NO", receipt.getItemString(receipt.rowCount()-1, "RECEIPT_NO"));
		invRcp.onQueryByInvNo(oldPrintNo);
		if(StringTool.getBoolean(invRcp.getItemString(0, "ACCOUNT_FLG"))){
			this.messageBox_("���սᷢƱ�����˷�");
			return;
		}
		invRcp.setItem(0, "CANCEL_FLG", "1");
		invRcp.setItem(0, "CANCEL_USER", Operator.getID());
		invRcp.setItem(0, "CANCEL_DATE",invRcp.getDBTime());
		invRcp.setActive(0,true);
//		if(!invRcp.onDisCharge(printNo, newReceiptNo, "2", amt)){
//			this.messageBox_("����Ʊ��ʧ��");
//			return ;
//		}
		if(!removeOrder()){
			this.messageBox_("ɾ��ҽ��ʧ��");
			return;
		}
		String tempSql=getBilInvoiceUpdate();
		String[] sql=receipt.getUpdateSQL();
		sql=StringTool.copyArray(sql, invRcp.getUpdateSQL());
		sql=StringTool.copyArray(sql, order.getUpdateSQL());

		TParm inParm = new TParm();

		Map inMap = new HashMap();
		inMap.put("SQL", sql);
		inParm.setData("IN_MAP", inMap);
//		inParm.setData("DIS_LIST",disList);
		TParm result = TIOM_AppServer.executeAction(
				"action.hrm.HRMPersonReportAction", "onSave", inParm);
		if (result.getErrCode() != 0) {
			this.messageBox_(result.getErrText());
			this.messageBox("E0001");
			return;
		}
		receipt.resetModify();
		invRcp.resetModify();
		order.resetModify();
		order.resetMedApply();
		bilInvoice=new BilInvoice("OPB");
//		onFee(bilInvoice.getUpdateNo(),order.getItemString(0, "PAY_TYPE"));
		TParm sendHl7= Hl7Communications.getInstance().Hl7Message(disList);
		if(sendHl7.getErrCode()<0){
			this.messageBox_(sendHl7.getErrText());
			this.messageBox_("������Ϣʧ��");
		}
		this.messageBox("P0001");
		this.closeWindow();
	}
	public void onFee(String printNo,String payType){
		// order.supplementCaseNo(caseNo);
		String filterString=order.getFilter();
		order.setFilter("CASE_NO='" +caseNo+"'");
		order.filter();
		// �շѽӿ�
		receipt.onQuery();
		TParm receiptParm = new TParm();
		receiptParm.setData("CASE_NO", caseNo);
		receiptParm.setData("MR_NO", order.getItemData(0, "MR_NO"));
		receiptParm.setData("PRINT_NO", printNo);
		receiptParm.setData("ORDER_PARM",order.getBuffer(order.PRIMARY));
		receiptParm.setData("PAY_TYPE",payType);
		receipt.insert(receiptParm,1);
		order.setFilter(filterString);
		order.filter();

		invRcp.onQuery();
		TParm invRcpParm = new TParm();
		invRcpParm.setData("RECP_TYPE", "OPB");
		invRcpParm.setData("INV_NO", printNo);
		String receiptNo=receipt.getItemString(receipt
				.rowCount() - 1, "RECEIPT_NO");
		invRcpParm.setData("RECEIPT_NO", receiptNo);
		invRcpParm.setData("AR_AMT", receipt.getItemDouble(
				receipt.rowCount() - 1, "TOT_AMT"));
		invRcp.insert(invRcpParm);

		order.updateReceiptNo(receipt.getItemString(receipt
				.rowCount() - 1, "RECEIPT_NO"),Operator.getDept());
		String[] sql = new String[] {};
		sql = StringTool.copyArray(sql, order.getUpdateSQL());
		sql = StringTool.copyArray(sql, receipt.getUpdateSQL());
		sql = StringTool.copyArray(sql, invRcp.getUpdateSQL());

		String sqlUpdate = "UPDATE BIL_INVOICE SET UPDATE_NO='#' WHERE RECP_TYPE='OPB' AND START_INVNO='#'";
		sqlUpdate = sqlUpdate.replaceFirst("#", StringTool.addString(printNo)).replaceFirst("#", bilInvoice.getStartInvno());
		// System.out.println("sqlUpdate=" + sqlUpdate);
		String[] sqlUpdateInvoice = new String[] { sqlUpdate };
		sql = StringTool.copyArray(sql, sqlUpdateInvoice);
		String[] sqlMed=order.getMedApply().getUpdateSQL();
		sql=StringTool.copyArray(sql, order.getMedApply().getUpdateSQL());
		for(String temp:sql){
			// System.out.println("temp======"+temp);
		}
		TParm inParm = new TParm();

		Map inMap = new HashMap();
		inMap.put("SQL", sql);
		inParm.setData("IN_MAP", inMap);
		TParm result = TIOM_AppServer.executeAction(
				"action.hrm.HRMPersonReportAction", "onSave", inParm);
		if (result.getErrCode() != 0) {
			this.messageBox_(result.getErrText());
			this.messageBox("E0001");
			return;
		}
//		this.messageBox("P0001");
		String[] receipt=new String[]{receiptNo};
		dealPrintData(receipt,this.getValueString("PAT_NAME"),this.getValueString("SEX_CODE"),this.getValueString("DEPT_CODE_COM"));
	}
	/**
	 * �ж�ORDER�������Ƿ����Ѿ�ִ�е�ҽ�������������˷�
	 * @return
	 */
	private boolean isOrderRemovable(){
		//// System.out.println("in isOrderRemovable");
		//order.showDebug();
		int count=order.rowCount();
		if(count<=0){
			return false;
		}
		for(int i=0;i<count;i++){
			if(!order.isOrderRemovable(i)){
				return false;
			}
		}
		return true;
	}
	/**
	 * ������
	 */
	public void onReportPrint(){
		reportPrint();
	}
	/**
	 * ȡ�õ��������ݲ���ӡ
	 */
	private void reportPrint(){
		String mrNo=this.getValueString("MR_NO");
		if(StringUtil.isNullString(mrNo)||StringUtil.isNullString(caseNo)){
			this.messageBox_("ȡ������ʧ��");
			return;
		}
		TParm parm=order.getReportTParm(mrNo, caseNo);
		if(parm==null){
			this.messageBox_("ȡ������ʧ��");
			return;
		}
		if(parm.getErrCode()!=0){
			this.messageBox_("ȡ������ʧ��");
			return;
		}
		openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMReportSheetNew.jhw",parm,false);
	}
	/**
	 * �˷�ɾ��PRINT_FLGΪN��ҽ��
	 * @return
	 */
	private boolean removeOrder(){
		disList=new ArrayList();
		int count=order.rowCount();
		if(count<=0){
			return false;
		}
		String filterString=order.getFilter();
		order.setFilter("CASE_NO='" +caseNo+"'");
		order.filter();
		//// System.out.println("after first filter");
		//order.showDebug();
		count=order.rowCount();

		for(int i=count-1;i>-1;i--){
			if(StringTool.getBoolean(order.getItemString(i, "EXEC_FLG"))){
				this.messageBox_("�Ѿ���ִ�е�ҽ��,�����˷�");
				return false;
			}
                        order.deleteRow(i);
//			if(!order.isDischargableSet(i)){
//				TParm parm=new TParm();
//				parm.setData("ADM_TYPE","H");
//				parm.setData("CAT1_TYPE",order.getItemString(i, "CAT1_TYPE"));
//				String patName=this.getValueString("PAT_NAME");
//				parm.setData("PAT_NAME",patName);
//				parm.setData("CASE_NO",order.getItemString(i, "CASE_NO"));
//				parm.setData("ORDER_NO",order.getItemString(i, "CASE_NO"));
//				parm.setData("SEQ_NO",order.getItemData(i, "SEQ_NO"));
//				parm.setData("LAB_NO",order.getItemString(i,"MED_APPLY_NO"));
//				parm.setData("FLG", "1");
////				disList.add(parm);
//			}
		}
                TParm delOrderParm = order.getBuffer(order.DELETE);
                int rowDelOrderCount = delOrderParm.getCount();
                for(int i=0;i<rowDelOrderCount;i++){
                    TParm hl7Parm = new TParm();
                    TParm temp = delOrderParm.getRow(i);
                    if("LIS".equals(temp.getValue("CAT1_TYPE"))||"RIS".equals(temp.getValue("CAT1_TYPE"))){
                        if("Y".equals(temp.getValue("SETMAIN_FLG"))){
                            hl7Parm.setData("ADM_TYPE","H");
                            hl7Parm.setData("CAT1_TYPE",temp.getValue("CAT1_TYPE"));
                            String patName=this.getValueString("PAT_NAME");
                            hl7Parm.setData("PAT_NAME",patName);
                            hl7Parm.setData("CASE_NO",temp.getValue("CASE_NO"));
                            hl7Parm.setData("ORDER_NO",temp.getValue("CASE_NO"));
                            hl7Parm.setData("SEQ_NO",temp.getValue("SEQ_NO"));
                            hl7Parm.setData("LAB_NO",temp.getValue("MED_APPLY_NO"));
                            hl7Parm.setData("FLG","1");
                            disList.add(hl7Parm);
                        }
                    }
                }
		order.setFilter(filterString);
		order.filter();
		//// System.out.println("after orderDelete");
		//order.showDebug();
		return true;
	}
	/**
	 * �����嵥
	 */
	public void onFill(){
		if(tableM==null){
			return;
		}
		int row=tableM.getSelectedRow();
		if(row<0){
			this.messageBox_("��ѡ��һ���վݽ��в���");
			return;
		}
		if(receipt==null||receipt.rowCount()<=0){
			this.messageBox_("ȡ������ʧ��");
			return;
		}
		String receiptNo=receipt.getItemString(row, "RECEIPT_NO");
		if(StringUtil.isNullString(receiptNo)){
			this.messageBox_("ȡ���վݺ�ʧ��");
			return;
		}
		TParm parm=order.getParmByReceiptNo(receiptNo);
		//// System.out.println("parm="+parm);

		if(parm==null){
			this.messageBox_("��ѯ����ʧ��");
			return;
		}
		TParm result=new TParm();
		Timestamp now=order.getDBTime();
		String data="��ӡʱ�䣺"+StringTool.getString(now,"yyyy/MM/dd");
		result.setData("PRINT_TIME","TEXT",data);
		result.setData("PRINT_USER","TEXT","�Ʊ��ˣ�"+Operator.getName());
		result.setData("BILL_TABLE",parm.getData());
		openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMBillDetail.jhw",result,false);
	}
}
