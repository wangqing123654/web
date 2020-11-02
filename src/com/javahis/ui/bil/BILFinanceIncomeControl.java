package com.javahis.ui.bil;

import java.util.Date;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import javax.swing.JOptionPane;

import org.apache.commons.lang.time.DateUtils;

import jdo.bil.BILFinanceTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.ctc.wstx.util.DataUtil;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.DateUtil;
import com.javahis.util.ExportExcelUtil;

/**
 *
 * <p>Title: �������뱨��</p>
 *
 * <p><b>Description:</b>
 * <br>�����ѷ�������
 * </p>
 *
 * <p>Copyright: </p>
 *
 * <p>Company: bluecore </p>
 *
 * @alias�������뱨��
 * @author design: pangben 2014-4-22
 * <br> coding:
 * @version 4.0
 */
public class BILFinanceIncomeControl  extends TControl {
	TTable table;
	private TTabbedPane tabbedPane; // ҳǩ
	DateFormat df3 = new SimpleDateFormat("yyyyMMddhhmmss");
	DateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
	private TParm tableResult=null;
	private String tableName="";
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		// �̳�
		super.onInit();
		onPage();
		tabbedPane = (TTabbedPane) this.getComponent("TABBEDPANE"); // ҳǩ
		tabbedPane.setEnabledAt(3, false);
		TTable table6 = (TTable) this.getComponent("TABLE6");
		table6.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		this.setValue("MEM_TYPE", "3");
		this.setValue("PRINT_TYPE", "2");
		callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
				"%ROOT%\\config\\sys\\SYSFeePopup.x");// ҽ������
		callFunction("UI|ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE"));
		this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
	}
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		boolean isDedug=true; //add by huangtt 20160505 ��־���
		try {
			
		
		if (this.getValue("TYPE").toString().length()<=0) {
			this.messageBox("���Ͳ���Ϊ��");
			return;
		}
		if (tabbedPane.getSelectedIndex()==0) {
			this.setValue("TOT_AMT",0.00);
		}
		if (tabbedPane.getSelectedIndex()==1) {
			this.setValue("SS_TOT_AMT",0.00);
		}
		if (tabbedPane.getSelectedIndex()==2) {
			this.setValue("YJJ_TOT_AMT", 0.00);
		}
		TParm parm=new TParm();
		TParm billTypeParm=new TParm();
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd");
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd");
		String sTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_TIME")), "HHmmss");
		String eTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_TIME")), "HHmmss");
		if(tabbedPane.getSelectedIndex()==5 && !sDate.equals(eDate)){
			this.messageBox("ֻ�ܲ�ѯһ�������");
			return;
		}
		parm.setData("START_DATE",sDate+sTime);
		parm.setData("END_DATE",eDate+eTime);
		parm.setData("TYPE",this.getValue("TYPE"));
		parm.setData("BATCHNAME",eDate);//���ڣ�ɾ�������ڵ�����
		int dataType=0;
		if (this.getValue("TYPE").equals("1")) {
			parm.setData("BUSITYPENAME","����");
			dataType=13;
		}else{
			parm.setData("BUSITYPENAME","סԺ");
			dataType=14;
		}
		TParm checkParm=null;
		TParm checkLog=new TParm();
		switch (tabbedPane.getSelectedIndex()) {
		case 0:
			parm.setData("BATCHNAME",StringTool.getString(TypeTool.getTimestamp(getValue(
	        "START_DATE")), "yyyyMMdd"));
			parm.setData("EXPTABLENAME","DI_INCOMEEXP".toLowerCase());
			parm.setData("SOURCESYSTEM",dataType);
			parm.setData("MR_NO", this.getValue("MR_NO"));
			parm.setData("PRINT_TYPE", this.getValue("PRINT_TYPE"));
			parm.setData("REXP_CODE", this.getValue("REXP_CODE"));
			parm.setData("MEM_TYPE", this.getValue("MEM_TYPE"));
			parm.setData("OPD_TYPE", this.getValue("OPD_TYPE_Y"));
			tableResult=BILFinanceTool.getInstance().onQueryByAccounts(parm);
			table=(TTable)this.getComponent("TABLE");
			tableName="Ӧ������";
			break;
		case 1:
			parm.setData("BATCHNAME",StringTool.getString(TypeTool.getTimestamp(getValue(
	        "START_DATE")), "yyyyMMdd"));
			parm.setData("EXPTABLENAME","DI_INCOMEREAL".toLowerCase());
			parm.setData("SOURCESYSTEM",dataType);
			parm.setData("BIL_PAYTYPE",this.getValue("BIL_PAYTYPE"));
			parm.setData("MR_NO",this.getValue("MR_NO_S"));
			parm.setData("OPD_TYPE", this.getValue("OPD_TYPE_SD"));
			parm.setData("MEM_TYPE", this.getValue("MEM_TYPE_S"));
			tableResult=BILFinanceTool.getInstance().onQueryByPaid(parm);
			String id="";
			String sql="";
			for(int i=0;i<tableResult.getCount();i++){// add by kangy 20170711 ҽԺ�渶������ֱ��  �����͸�֧����ʽһ�𴫻�
				if("C4".equals(tableResult.getValue("BILL_ID",i))||"BXZF".equals(tableResult.getValue("BILL_ID",i))){
					if("C4".equals(tableResult.getValue("BILL_ID",i))&&tableResult.getValue("REMARK04",i).length()>0&&tableResult.getValue("REMARK04",i).indexOf("#")>=0){// ҽԺ�渶
						id=tableResult.getValue("REMARK04",i).substring(0, tableResult.getValue("REMARK04",i).indexOf("#"));
						sql="SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CARDTYPE' AND ID='"+id+"'";
						billTypeParm=new TParm(TJDODBTool.getInstance().select(sql));
						tableResult.setData("BILL_TYPE",i,tableResult.getValue("BILL_TYPE",i)+billTypeParm.getValue("CHN_DESC",0));
					}else if("BXZF".equals(tableResult.getValue("BILL_ID",i))&&tableResult.getValue("REMARK08",i).length()>0&&tableResult.getValue("REMARK08",i).indexOf("#")>=0){//����ֱ��
						id=tableResult.getValue("REMARK08",i).substring(0, tableResult.getValue("REMARK08",i).indexOf("#"));
						sql="SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CARDTYPE' AND ID='"+id+"'";
						billTypeParm=new TParm(TJDODBTool.getInstance().select(sql));
						tableResult.setData("BILL_TYPE",i,tableResult.getValue("BILL_TYPE",i)+billTypeParm.getValue("CHN_DESC",0));
					}
				}
			}
			table=(TTable)this.getComponent("TABLE2");
			tableName="ʵ������";
			break;
		case 2:
			parm.setData("BATCHNAME",StringTool.getString(TypeTool.getTimestamp(getValue(
	        "START_DATE")), "yyyyMMdd"));
			parm.setData("EXPTABLENAME","DI_INCOMEPRE".toLowerCase());
			parm.setData("SOURCESYSTEM",dataType);
			parm.setData("PAY_TYPE",this.getValue("PAY_TYPE"));
			parm.setData("MR_NO",this.getValue("MR_NO_Y"));
			tableResult=BILFinanceTool.getInstance().onQueryByTypePay(parm);
			for(int i=0;i<tableResult.getCount();i++){
				if("C4".equals(tableResult.getValue("BILL_ID",i))||"BXZF".equals(tableResult.getValue("BILL_ID",i))||"C1".equals(tableResult.getValue("BILL_ID",i))){
				if("Ԥ���ײ�".equals(tableResult.getValue("TYPE",i))){
					if("C4".equals(tableResult.getValue("BILL_ID",i))&&tableResult.getValue("MEMO4",i).length()>0){
						id=tableResult.getValue("MEMO4",i).substring(0, tableResult.getValue("MEMO4",i).indexOf("#"));
						sql="SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CARDTYPE' AND ID='"+id+"'";
						billTypeParm=new TParm(TJDODBTool.getInstance().select(sql));
						tableResult.setData("BILL_TYPE",i,tableResult.getValue("BILL_TYPE",i)+billTypeParm.getValue("CHN_DESC",0));
					}else if("BXZF".equals(tableResult.getValue("BILL_ID",i))&&tableResult.getValue("MEMO8",i).length()>0){
						id=tableResult.getValue("MEMO8",i).substring(0, tableResult.getValue("MEMO8",i).indexOf("#"));
						sql="SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CARDTYPE' AND ID='"+id+"'";
						billTypeParm=new TParm(TJDODBTool.getInstance().select(sql));
						tableResult.setData("BILL_TYPE",i,tableResult.getValue("BILL_TYPE",i)+billTypeParm.getValue("CHN_DESC",0));
					}else if("C1".equals(tableResult.getValue("BILL_ID",i))&&tableResult.getValue("MEMO2",i).length()>0&&tableResult.getDouble("TOT_AMT",i)<0){
						id=tableResult.getValue("MEMO2",i).substring(0, tableResult.getValue("MEMO2",i).indexOf("#"));
						sql="SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CARDTYPE' AND ID='"+id+"'";
						billTypeParm=new TParm(TJDODBTool.getInstance().select(sql));
						tableResult.setData("BILL_TYPE",i,tableResult.getValue("BILL_TYPE",i)+billTypeParm.getValue("CHN_DESC",0));
					}
				
				}else {	
					tableResult.setData("BILL_TYPE",i,tableResult.getValue("BILL_TYPE",i)+tableResult.getValue("CARD_TYPE",i));
				}
				}
			}
			table=(TTable)this.getComponent("TABLE3");
			tableName="Ԥ����";
			break;
		case 3://�ɱ�
			eDate=StringTool.getString(TypeTool.getTimestamp(getValue("MONTH_DATE")), "yyyyMM");
			int year = Integer.parseInt(eDate.substring(0, 4));
			int month = Integer.parseInt(eDate.substring(4, 6));
			TParm dateParm = getMonthDate(year, month);
			parm.setData("MONTH_DATE",eDate);
			parm.setData("DAY",dateParm.getValue("LAST_DATE"));
			tableResult=BILFinanceTool.getInstance().onQueryCost(parm);
			table=(TTable)this.getComponent("TABLE4");
			tableName="�ɱ�����";
			break;
		case 4://��ֵ�Ĳ�
			parm.setData("BATCHNAME",StringTool.getString(TypeTool.getTimestamp(getValue(
	        "START_DATE")), "yyyyMMdd"));
			parm.setData("EXPTABLENAME","HIS_FEEDETAIL".toLowerCase());
			parm.setData("SOURCESYSTEM",dataType);
			tableResult=BILFinanceTool.getInstance().onQureyByBillDate(parm);
			table=(TTable)this.getComponent("TABLE6");
			tableName="��ֵ�Ĳ�";
			break;
		case 5://�ɱ�����
			parm.setData("BATCHNAME",StringTool.getString(TypeTool.getTimestamp(getValue(
	        "START_DATE")), "yyyyMMdd"));
			parm.setData("EXPTABLENAME","DI_SERVICEVOLEXP".toLowerCase());
			parm.setData("SOURCESYSTEM",dataType);
			tableResult=BILFinanceTool.getInstance().onQueryCostAcount(parm);
			table=(TTable)this.getComponent("TABLE7");
			tableName="�ɱ�����";
			break;
		case 6://ҩƷ���
			if(null==this.getValue("BUSITYPE_NAME")||this.getValue("BUSITYPE_NAME").toString().length()<=0){
				this.messageBox("��ѡ��ҵ������");
				this.grabFocus("BUSITYPE_NAME");
				return;
			}
			parm.setData("BATCHNAME",StringTool.getString(TypeTool.getTimestamp(getValue(
	        "START_DATE")), "yyyyMMdd"));
			parm.setData("EXPTABLENAME","DI_DRUGS".toLowerCase());
			parm.setData("BUSITYPE_NAME",this.getValue("BUSITYPE_NAME"));
			String []valueName={"TYPE_CODE","ORDER_CODE","ORG_CODE","DEPT_CODE","EXEC_DEPT_CODE","SUP_CODE"};
			for (int i = 0; i < valueName.length; i++) {
				if(null!=this.getValue(valueName[i]) && this.getValueString(valueName[i]).length()>0){
					parm.setData(valueName[i],this.getValue(valueName[i]));
				}
			}
			tableResult=BILFinanceTool.getInstance().onQueryPhaData(parm);
			table=(TTable)this.getComponent("PHA_TABLE");
			tableName="ҩƷ���";
			break;
		default:
			break;
		}
		if (tableResult.getErrCode()<0) {
			this.messageBox("��ѯ���ִ���"+tableResult.getErrText());
			return;
		}
		if(tabbedPane.getSelectedIndex()!=5){//�ǳɱ�����
			if (tableResult.getCount("ACCOUNT_DATE")<=0) {//Ϊ��ͳһ���ݣ���TYPE_ID��ΪACCOUNT_DATE  modify by huangjw 20141114
				this.messageBox("û�в�ѯ������");
				table.setParmValue(new TParm());
				return;
			}
		}else{//�ɱ�����
			if (tableResult.getCount("TID")<=0) {
				this.messageBox("û�в�ѯ������");
				table.setParmValue(new TParm());
				return;
			}
		}
		/*if (tabbedPane.getSelectedIndex() == 0
				|| tabbedPane.getSelectedIndex() == 1
				|| tabbedPane.getSelectedIndex() == 2
				|| tabbedPane.getSelectedIndex() == 5) {
*/		
		checkLog = BILFinanceTool.getInstance().checkLogDrlogAyh(parm);
		if (checkLog.getErrCode() < 0) {
			System.out
					.println("��ѯ�м��LOG����ִ���:::::" + checkLog.getErrText());
			this.messageBox("��ѯ�м��LOG����ִ���");
			return;
		}
		//}//��ʱ����
		switch (tabbedPane.getSelectedIndex()) {
		case 0://Ӧ��
			checkParm=BILFinanceTool.getInstance().checkDiIncomeexp(parm);
			break;
		case 1://ʵ��
			checkParm=BILFinanceTool.getInstance().checkDiIncomereal(parm);
			break;
		case 2://Ԥ����
			if(parm.getValue("BUSITYPENAME").equals("����")){
				checkParm=BILFinanceTool.getInstance().checkDiIncomepreO(parm);
//				System.out.println("checkParm==="+checkParm);
				break;
			}
			checkParm=BILFinanceTool.getInstance().checkDiIncomepre(parm);		
			break;
		case 4://��ֵ�Ĳ�add by huangjw 20141112
			checkParm=BILFinanceTool.getInstance().checkDiIncomeHigh(parm);
			break;
		case 5://�ɱ�����
			checkParm=BILFinanceTool.getInstance().checkDiIncomeCostAcount(parm);
			break;
		case 6://ҩƷ���
			parm.setData("BUSITYPENAME",this.getText("BUSITYPE_NAME"));
			checkParm=BILFinanceTool.getInstance().checkDidrugs(parm);
			break;
		default:
			break;
		}
		/*if (tabbedPane.getSelectedIndex()==0||
				tabbedPane.getSelectedIndex()==1||
				tabbedPane.getSelectedIndex()==2||tabbedPane.getSelectedIndex()==5) {*/
		if (checkParm.getErrCode()<0) {
			System.out.println("��ѯ�м����ִ���:::::"+checkParm.getErrText());
			this.messageBox("2222��ѯ�м����ִ���");
			return;
		}
		//}
//		if (checkLog.getCount()>0&&checkParm.getCount()>0) {
//			for (int i = 0; i < tableResult.getCount("TYPE_ID"); i++) {
//				tableResult.setData("LOAD_DOWN",i,"�����");
//				tableResult.setData("UP_LOAD",i,"���ϴ�");
//			}
//		}else 
		/*if (checkLog.getCount()>0 && tabbedPane.getSelectedIndex()!=5 ) {//�ǳɱ�����
			for (int i = 0; i < tableResult.getCount("TID"); i++) {//Ϊ��ͳһ���ݣ���TYPE_ID��ΪACCOUNT_DATE  modify by huangjw 20141114
				if (tableResult.getValue("UP_LOAD",i).equals("δ�ϴ�")) {
					tableResult.setData("LOAD_DOWN",i,"δ���");
				}else{
					tableResult.setData("LOAD_DOWN",i,"�����");
				}
			}
		}else{//�ɱ�����
		
	*/	double arAmt=0.00;
		double phaAmt=0.00;
		if(tabbedPane.getSelectedIndex()!=4){
		  if(checkParm.getCount()>0){
				if(checkLog.getCount()>0){
					for (int i = 0; i < tableResult.getCount("TID"); i++) {
						tableResult.setData("UP_LOAD",i,"���ϴ�");
						tableResult.setData("LOAD_DOWN",i,"�����");
						if (tabbedPane.getSelectedIndex()==0||tabbedPane.getSelectedIndex()==1
								||tabbedPane.getSelectedIndex()==2) {
							arAmt+=tableResult.getDouble("TOT_AMT",i);
						}else if (tabbedPane.getSelectedIndex()==6){
							phaAmt+=tableResult.getDouble("AR_AMT",i);
						}
					}
				}else{
					for (int i = 0; i < tableResult.getCount("TID"); i++) {
						tableResult.setData("UP_LOAD",i,"���ϴ�");
						tableResult.setData("LOAD_DOWN",i,"δ���");
						if (tabbedPane.getSelectedIndex()==0||tabbedPane.getSelectedIndex()==1
								||tabbedPane.getSelectedIndex()==2) {
							arAmt+=tableResult.getDouble("TOT_AMT",i);
						}else if (tabbedPane.getSelectedIndex()==6){
							phaAmt+=tableResult.getDouble("AR_AMT",i);
						}
					}
				}
			}else{
				for (int i = 0; i < tableResult.getCount("TID"); i++) {
					tableResult.setData("UP_LOAD",i,"δ�ϴ�");
					tableResult.setData("LOAD_DOWN",i,"δ���");
					if (tabbedPane.getSelectedIndex()==0||tabbedPane.getSelectedIndex()==1
							||tabbedPane.getSelectedIndex()==2) {
						arAmt+=tableResult.getDouble("TOT_AMT",i);
					}else if (tabbedPane.getSelectedIndex()==6){
						phaAmt+=tableResult.getDouble("AR_AMT",i);
					}
				}
			}
		}
		//}
		if (tabbedPane.getSelectedIndex()==4) {//�Ĳ�У���Ƿ��Ѿ����
			if(checkParm.getCount()>0&&checkParm.getValue("BISPROC",0).equals("Y")){
				for (int i = 0; i < tableResult.getCount("ACCOUNT_DATE"); i++) {
					if (tableResult.getValue("UP_LOAD",i).equals("δ�ϴ�")) {
						tableResult.setData("LOAD_DOWN",i,"δ���");
					}else{
						tableResult.setData("LOAD_DOWN",i,"�����");
					}
				}
			}
			for(int i=0;i<tableResult.getCount();i++){//���䴦��
				tableResult.setData("PAT_AGE",i,this.patAge(tableResult.getTimestamp("BIRTH_DATE",i)));
			}
		}
//		}else if (checkParm.getCount()>0&&checkLog.getCount()<=0) {
//			for (int i = 0; i < tableResult.getCount("TYPE_ID"); i++) {
//				tableResult.setData("UP_LOAD",i,"���ϴ�");
//			}
//		}
//		if (tabbedPane.getSelectedIndex()==1&&this.getValueInt("TYPE")==3) {
//			for (int i = 0; i < tableResult.getCount("TYPE_ID"); i++) {
//				tableResult.setData("TID",i,""+(i+1));
//			}
//		}
		if (tabbedPane.getSelectedIndex()==0) {
			this.setValue("TOT_AMT", arAmt);
			this.setValue("REDUCE_AMT_Y", tableResult.getDouble("REDUCE_AMT"));
			this.setValue("YS_AMT", arAmt-tableResult.getDouble("REDUCE_AMT"));
		}
		if (tabbedPane.getSelectedIndex()==1) {
			this.setValue("SS_TOT_AMT", arAmt);
			this.setValue("REDUCE_AMT_S", tableResult.getDouble("REDUCE_AMT"));
			this.setValue("SS_AMT", arAmt-tableResult.getDouble("REDUCE_AMT"));
		}
		if (tabbedPane.getSelectedIndex()==2) {
			this.setValue("YJJ_TOT_AMT", arAmt);
		}
		if(tabbedPane.getSelectedIndex()==6){
			this.setValue("PHA_SUM_TOT_AMT", phaAmt);
		}
		table.setParmValue(tableResult);
		
		} catch (Exception e) {
			// TODO: handle exception
			if(isDedug){  
				System.out.println(" come in class: BILFinanceIncomeControl.class ��method ��onQuery");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ��������
	 * 
	 * @param date
	 * @return
	 */
	private String patAge(Timestamp date) {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp temp = date == null ? sysDate : date;
		String age = "0";
		age = DateUtil.showAge(temp, sysDate);
		return age;
	}
	/**
	 * ��ʼ������
	 */
	public void onPage(){
		//this.setValue("START_DATE", StringTool.rollDate(SystemTool.getInstance().getDate(), -1));
		this.setValue("START_DATE",SystemTool.getInstance().getDate());
		this.setValue("END_DATE", SystemTool.getInstance().getDate());
		this.setValue("START_TIME", "00:00:00");
		this.setValue("END_TIME", "23:59:59");
		this.setValue("TYPE", "1");
		this.setValue("MONTH_DATE", SystemTool.getInstance().getDate());
		this.setValue("PHA_SUM_TOT_AMT", 0.00);
	}
	/**
	 * ҩƷ���
	 * @param type
	 * @return
	 */
	private TParm diDrugsParm(){
		String date="";
		TParm parmValue=new TParm();
		TParm parm=table.getParmValue();
		int row=parm.getCount();
		if (row<=0) {
			this.messageBox("û����Ҫ����������");
			return null;
		}
		parm=tableResult;
		int sum=0;
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd");
		String eDateT= StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyy-MM-dd");
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd");
		String sTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_TIME")), "HHmmss");
		String eTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_TIME")), "HHmmss");
		parmValue.setData("START_DATE",sDate+sTime);
		parmValue.setData("END_DATE",eDate+eTime);
		String uid=getUUID();//��־������
		//String checkDate="";
		for (int i = 0; i <row; i++) {
			date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			parmValue.addData("PK_DRUGS",getUUID());//�ⲿϵͳ���������
			//parmValue.addData("DATA_TYPE","C");
			parmValue.addData("AMOUNT",  parm.getDouble("AR_AMT",i));//���
			parmValue.addData("PK_EXPLOG", uid);//������־������
			parmValue.addData("BATCHNAME",sDate);//����ǽ�����"YYYYMMDD"
			parmValue.addData("BILLCLERKNAME",parm.getValue("USER_NAME",i));//�Ƶ���
			parmValue.addData("BUSITYPENAME",parm.getValue("BILL_TYPE",i));//ҵ����������
			parmValue.addData("CDAY",parm.getValue("ACCOUNT_DATE",i));//����
			parmValue.addData("CMONTH", date.substring(4,6));//�·�
			parmValue.addData("CYEAR", date.substring(0,4));//���
			//parmValue.addData("EXPTIME", SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//����ʱ�佨���á�HHMMSS��
			parmValue.addData("CORPNAME", "��������������ҽԺ");//��˾����--��ѡ���룬���ƣ�pk,�Ƽ�����
			parmValue.addData("BILLDEPTCODE", parm.getValue("DEPT_CODE",i));//�������Ҵ���
			parmValue.addData("BILLDEPTNAME", parm.getValue("DEPT_DESC",i));//������������
			parmValue.addData("DDATE", parm.getValue("ACCOUNT_DATE",i));//����
			parmValue.addData("PK_CORP", "001");//��˾����--��ѡ���룬���ƣ�pk,�Ƽ�����
			parmValue.addData("EXECDEPTCODE", parm.getValue("EXE_DEPT_CODE",i));//ִ�п��Ҵ���
			parmValue.addData("EXECDEPTNAME", parm.getValue("EXE_DEPT_DESC",i));//ִ�е���������
			parmValue.addData("DURGCLCODE", parm.getValue("PHA_TYPE",i));//ҩƷ������
			parmValue.addData("DURGCLNAME", parm.getValue("PHA_TYPE_DESC",i));//ҩƷ�������
			parmValue.addData("CUSTCODE", parm.getValue("SUP_CODE",i));//�����̴���
			parmValue.addData("CUSTNAME", parm.getValue("SUP_CHN_DESC",i));//����������
			parmValue.addData("PHARMACYCODE", parm.getValue("PHA_CODE",i));//ҩ������
			parmValue.addData("PHARMACYNAME", parm.getValue("PHA_DESC",i));//ҩ������		
			parmValue.addData("VDEF1", parm.getValue("VDEF1",i));//����סԺ���
			parmValue.addData("INOUT", parm.getValue("INOUT",i));//��/��
			parmValue.addData("OPT_USER", Operator.getID());//
			parmValue.addData("OPT_TERM", Operator.getIP());//
			sum++;
		}
		//parmValue.setData("TYPE_SUM",this.getValue("PHA_DEPT"));
		parmValue.setCount(parmValue.getCount("PK_EXPLOG"));
		parmValue.setData("CHEKC_DATE",sDate);//����
		//====================================================��ӵ���־��add by huangjw 20150414
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//����
		logParm.setData("EXPTABLENAME","DI_DRUGS".toLowerCase());//��������
		logParm.setData("BATCHNAME",sDate);
		logParm.setData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//����ʱ�佨���á�HHMMSS��
		logParm.setData("EXPOPERATOR",Operator.getName());//��������Ա����
		logParm.setData("EXPSTATE",0);//����״̬
		logParm.setData("TAOTALNUM",sum);//������¼����
		logParm.setData("SOURCESYSTEM",parm.getValue("BILL_NAME",0));
		//logParm.setData("ADM_TYPE",type);
		//logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		//=====================================================
		return parmValue;
	}
	/**
	 * Ԥ����
	 * @return
	 */
	private TParm diIncomepreParm(String type){
		String date="";
		TParm parmValue=new TParm();
		TParm parm=table.getParmValue();
		int row=parm.getCount();
		if (row<=0) {
			this.messageBox("û����Ҫ����������");
			return null;
		}
		parm=tableResult;
		int sum=0;
		int dataType=0;
		//String dataMessage="";
		if (this.getValue("TYPE").toString().equals("1")) {
			dataType=13;
			//dataMessage="����";
		}else if (this.getValue("TYPE").toString().equals("2")) {
			dataType=14;
			//dataMessage="סԺ";
		}
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd");
		String eDateT= StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyy-MM-dd");
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd");
		String sTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_TIME")), "HHmmss");
		String eTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_TIME")), "HHmmss");
		parmValue.setData("START_DATE",sDate+sTime);
		parmValue.setData("END_DATE",eDate+eTime);
		String uid=getUUID();//��־������
		String bilDate="";
		//String checkDate="";
		for (int i = 0; i <row; i++) {
			date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			bilDate=SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE",i),true).toString();
			//checkDate=date.substring(0,8);
			parmValue.addData("PK_INCOMEPRE",getUUID());//�ⲿϵͳ���������
			parmValue.addData("DATA_TYPE","C");
			parmValue.addData("PK_EXPLOG", uid);//������־������
			parmValue.addData("BATCHNAME",sDate);//����ǽ�����"YYYYMMDD"
			parmValue.addData("EXPTIME", SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//����ʱ�佨���á�HHMMSS��
			parmValue.addData("CYEAR", date.substring(0,4));//���
			parmValue.addData("CMONTH", date.substring(4,6));//�·�
			parmValue.addData("CDAY",bilDate.substring(0,4)+"-"+bilDate.substring(4,6)+"-"+bilDate.substring(6,8));//����
			parmValue.addData("DDATE", parm.getValue("ACCOUNT_DATE",i).substring(0,10));//����
			parmValue.addData("PK_CORP", "001");//��˾����--��ѡ���룬���ƣ�pk,�Ƽ�����
			parmValue.addData("CORPNAME", "��������������ҽԺ");//��˾����--��ѡ���룬���ƣ�pk,�Ƽ�����
			parmValue.addData("SOURCESYSTEM", 1);//�ⲿ������Դϵͳ
			parmValue.addData("PK_OUTERSYSTEM", "HIS");//��ϵͳ����
			parmValue.addData("REALDATE",  SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE",i),true));//��������--�м��
			parmValue.addData("VNUMBER", parm.getValue("TID",i));//���--�м��
			parmValue.addData("BUSITYPENAME", parm.getValue("TYPE",i));//ҵ����������
			parmValue.addData("PK_INCOMETYPE", parm.getValue("BILL_ID",i));//�����������
			parmValue.addData("INCOMETYPENAME", parm.getValue("BILL_TYPE",i));//�����������
			parmValue.addData("AMOUNT",  parm.getDouble("TOT_AMT",i));//���
			parmValue.addData("CPAYMENT", parm.getValue("BILL_TYPE",i));//֧����ʽ--�м��
			parmValue.addData("CPAYITEM", "");//֧����ϸ--�м��
			parmValue.addData("CASHBACK", parm.getDouble("TOT_AMT",i)<0?"��":"��");//����
			parmValue.addData("BILLCLERKNAME",parm.getValue("PRINT_NAME",i));//�շ�Ա����
			parmValue.addData("PK_BILLCLERK",parm.getValue("PRINT_USER",i));//�շ�Ա����
			parmValue.addData("CPAYMENTNUM", parm.getValue("BILL_ID",i));//֧����ʽ����--�м��
			parmValue.addData("CPAYITEMNUM", "");//֧����ϸ����--�м��
			parmValue.addData("PK_PATIENT", parm.getValue("MR_NO",i));//��������
			parmValue.addData("PATIENTNAME", parm.getValue("PAT_NAME",i));//��������
			parmValue.addData("ADM_TYPE", type);//����
			parmValue.addData("OPT_USER", Operator.getID());//
			parmValue.addData("OPT_TERM", Operator.getIP());//
			parmValue.addData("VDEF2", parm.getValue("PACKAGE_CODE",i));//�ײͱ��� add by huangtt 20160324
			parmValue.addData("VDEF3", parm.getValue("PACKAGE_DESC",i));//�ײ����� add by huangtt 20160324
			sum++;
		}
		parmValue.setData("TYPE_SUM",type);
		parmValue.setCount(parmValue.getCount("PK_INCOMEPRE"));
		parmValue.setData("CHEKC_DATE",sDate);
		//====================================================��ӵ���־��add by huangjw 20150414
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//����
		logParm.setData("EXPTABLENAME","DI_INCOMEPRE".toLowerCase());//��������
		logParm.setData("BATCHNAME",sDate);
		logParm.setData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//����ʱ�佨���á�HHMMSS��
		logParm.setData("EXPOPERATOR",Operator.getName());//��������Ա����
		logParm.setData("EXPSTATE",0);//����״̬
		logParm.setData("TAOTALNUM",sum);//������¼����
		logParm.setData("SOURCESYSTEM",dataType);
		//logParm.setData("ADM_TYPE",type);
		//logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		//=====================================================
		return parmValue;
	}
	/**
	 * Ӧ��\ʵ��
	 * @return
	 */
	private TParm diIncomeexpAndDiIncomerealParm(int index,String type){
		String date="";
		TParm parmValue=new TParm();
		TParm parm=table.getParmValue();
		int row=parm.getCount();
		if (row<=0) {
			this.messageBox("û����Ҫ����������");
			return null;
		}
		parm=tableResult;
		int sum=0;
		int dataType=0;
		//String dataMessage="";
		if (this.getValue("TYPE").toString().equals("1")) {
			dataType=13;
			//dataMessage="����";
		}else if (this.getValue("TYPE").toString().equals("2")) {
			dataType=14;
			//dataMessage="סԺ";
		}
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd");
		String eDateT= StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyy-MM-dd");
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd");
		String sTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_TIME")), "HHmmss");
		String eTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_TIME")), "HHmmss");
		parmValue.setData("START_DATE",sDate+sTime);
		parmValue.setData("END_DATE",eDate+eTime);
		String uid=getUUID();//��Ϊ��־������  add by huangjw 20150414
		String bilDate="";
		//String checkDate="";
		for (int i = 0; i <row; i++) {
			date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			bilDate=SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE",i),true).toString();
			//checkDate=date.substring(0,8);
			if (index==1) {
				parmValue.addData("PK_INCOMEEXP",getUUID());//�ⲿϵͳ���������
				parmValue.addData("DATA_TYPE","A");
			}else if(index==2){
				parmValue.addData("DATA_TYPE","B");
				parmValue.addData("PK_INCOMEREAL",getUUID());//�ⲿϵͳ���������
			}
			parmValue.addData("PK_EXPLOG", uid);//������־������
			parmValue.addData("BATCHNAME", sDate);//����ǽ�����"YYYYMMDD"
			parmValue.addData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//����ʱ�佨���á�HHMMSS��
			parmValue.addData("CYEAR", date.substring(0,4));//���
			parmValue.addData("CMONTH", date.substring(4,6));//�·�
			parmValue.addData("CDAY", bilDate.substring(0,4)+"-"+bilDate.substring(4,6)+"-"+bilDate.substring(6,8));//����
			parmValue.addData("PK_CORP", "001");//��˾����--��ѡ���룬���ƣ�pk,�Ƽ�����
			parmValue.addData("CORPNAME", "��������������ҽԺ");//��˾����--��ѡ���룬���ƣ�pk,�Ƽ�����
			parmValue.addData("PK_ITEM", parm.getValue("BILL_ID",i));//��������
			parmValue.addData("ITEMNAME", parm.getValue("BILL_TYPE",i));//��������
			parmValue.addData("PK_INCOMETYPE", parm.getValue("BILL_ID",i));//�����������
			parmValue.addData("INCOMETYPENAME", parm.getValue("BILL_TYPE",i));//�����������
			parmValue.addData("PK_BUSITYPE",parm.getValue("TYPE_ID",i));//ҵ����������
			parmValue.addData("BUSITYPENAME", parm.getValue("TYPE",i));//ҵ����������
			parmValue.addData("PK_DIAGNOSISDEPT", parm.getValue("DEPT_CODE",i));//������������
			parmValue.addData("DIAGNOSISDEPTNAME", parm.getValue("DEPT_DESC",i));//������������
			parmValue.addData("PK_DIAGNOSTICIAN", parm.getValue("DR_CODE",i));//����ҽ������
			parmValue.addData("DIAGNOSTICIANNAME",parm.getValue("DR_DESC",i));//����ҽ������
			parmValue.addData("PK_EXEDEPT", parm.getValue("EXE_DEPT_CODE",i));//ִ�п�������
			parmValue.addData("EXEDEPTNAME", parm.getValue("EXE_DEPT_DESC",i));//ִ�п�������
			parmValue.addData("PK_EXECUTOR", Operator.getID());//ִ��������
			parmValue.addData("EXECUTORNAME",Operator.getName());//ִ��������
			parmValue.addData("PK_BILLCLERK",parm.getValue("PRINT_USER",i));//�շ�Ա����
			parmValue.addData("BILLCLERKNAME",parm.getValue("PRINT_NAME",i));//�շ�Ա����
			parmValue.addData("PK_PATIENT", parm.getValue("MR_NO",i));//��������
			parmValue.addData("PATIENTNAME", parm.getValue("PAT_NAME",i));//��������
			parmValue.addData("CUSTCODE", parm.getValue("MR_NO",i));//�ͻ�����--�м��
			parmValue.addData("CUSTNAME", parm.getValue("PAT_NAME",i));//�ͻ�����--�м��
			parmValue.addData("CPAYMENT", parm.getValue("BILL_TYPE",i));//֧����ʽ--�м��
			parmValue.addData("CPAYITEM", "");//֧����ϸ--�м��
			parmValue.addData("DDATE", parm.getValue("ACCOUNT_DATE",i).substring(0,10));//��������--�м��
			parmValue.addData("REALDATE",  SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE",i),true));//��������--�м��
			parmValue.addData("VNUMBER", parm.getValue("TID",i));//���--�м��
			parmValue.addData("CPAYMENTNUM", parm.getValue("BILL_ID",i));//֧����ʽ����--�м��
			parmValue.addData("CPAYITEMNUM", "");//֧����ϸ����--�м��
			parmValue.addData("PK_CINPATIENTAREA", parm.getValue("CLINICAREA_CODE",i));//��������
			parmValue.addData("CINPATIENTAREA", parm.getValue("CLINIC_DESC",i));//����
			parmValue.addData("ISINSURANCE", parm.getValue("INS_FLG",i));//�Ƿ�ҽ��Y:��ҽ��;N:��ҽ��
			parmValue.addData("DISEASE", "");//������
			parmValue.addData("DISEASEGROUP", "");//������
			parmValue.addData("AMOUNT",  parm.getDouble("TOT_AMT",i));//���
			parmValue.addData("SOURCESYSTEM", 1);//�ⲿ������Դϵͳ
			parmValue.addData("PK_OUTERSYSTEM", "HIS");//��ϵͳ����
			parmValue.addData("INPRICE", "");//���뵥��
			parmValue.addData("CASHBACK", "");//����
			parmValue.addData("ADM_TYPE", type);//����
			parmValue.addData("OPT_USER", Operator.getID());//
			parmValue.addData("OPT_TERM", Operator.getIP());//
			sum++;
		}
		parmValue.setData("TYPE_SUM",type);
		parmValue.setCount(parmValue.getCount("PK_EXPLOG"));
		parmValue.setData("CHEKC_DATE",sDate);
		//====================================================��ӵ���־��add by huangjw 20150414
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//����
		if(index==1){//Ӧ�ձ�
			logParm.setData("EXPTABLENAME","DI_INCOMEEXP".toLowerCase());//��������
		}else{//ʵ�ձ�
			logParm.setData("EXPTABLENAME","DI_INCOMEREAL".toLowerCase());//��������
		}
		
		logParm.setData("BATCHNAME",sDate);
		logParm.setData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//����ʱ�佨���á�HHMMSS��
		logParm.setData("EXPOPERATOR",Operator.getName());//��������Ա����
		logParm.setData("EXPSTATE",0);//����״̬
		logParm.setData("TAOTALNUM",sum);//������¼����
		logParm.setData("SOURCESYSTEM",dataType);
		//logParm.setData("ADM_TYPE",type);
		//logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		//=====================================================
		return parmValue;
	}
	
	/**
	 * ��ֵ�Ĳ�����add by huangjw 20141112
	 * @param type
	 * @return
	 */
	public TParm highPriceDataParm(int index,String type){
		String date="";
		TParm parmValue=new TParm();
		table.acceptText();
		TParm parm=table.getParmValue();
		
		int row=parm.getCount();
		if (row<=0) {
			this.messageBox("û����Ҫ����������");
			return null;
		}
		String uid= getUUID();
		int sum=0;
		int dataType=0;
		String dataMessage="";
		if (this.getValue("TYPE").toString().equals("1")) {
			dataType=13;
			dataMessage="����";
		}else if (this.getValue("TYPE").toString().equals("2")) {
			dataType=14;
			dataMessage="סԺ";
		}
		//parm=tableResult;
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd");
		String eDateT= StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyy-MM-dd");
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd");
		String sTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_TIME")), "HHmmss");
		String eTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_TIME")), "HHmmss");
		parmValue.setData("START_DATE",sDate+sTime);
		parmValue.setData("END_DATE",eDate+eTime);
		for (int i = 0; i <row; i++) {
			if (parm.getValue("FLG",i).equals("N")) {
				continue;
			}
			parmValue.addData("FLG", "Y");
			//System.out.println("BILL_DATE::::"+parm.getValue("ACCOUNT_DATE",i));
			//System.out.println("BILL_DATE::ddd::"+StringTool.getString(TypeTool.getTimestamp(parm.getValue("ACCOUNT_DATE",i)), "yyyyMMdd"));
			//date=df2.format(parm.getValue("ACCOUNT_DATE",i));
			date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			parmValue.addData("PK_EXPLOG",uid);//������־������
			parmValue.addData("BATCHNAME", sDate);//����ǽ�����"YYYYMMDD"
			parmValue.addData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));
			parmValue.addData("SOURCESYSTEM", dataType);//�ⲿ������Դϵͳ
			
			parmValue.addData("PK_FEEDETAIL",getUUID());//�ⲿϵͳ���������
			parmValue.addData("BISCHARGE",parm.getValue("BILL_FLG",i));
			//parmValue.addData("BISPROC", parm.getValue("HRP_FLG",i));
			parmValue.addData("VHISBUSID",parm.getValue("RECEIPT_NO",i));
			parmValue.addData("VBARCODE",parm.getValue("INV_CODE",i));
			parmValue.addData("VITEMCODE",parm.getValue("ORDER_CODE",i));
			parmValue.addData("VITEMNAME",parm.getValue("ORDER_DESC",i));
			parmValue.addData("VRECEIPTCODE",parm.getValue("REXP_CODE",i));
			parmValue.addData("VRECEIPTNAME",parm.getValue("REXP_DESC",i));
			parmValue.addData("PK_CORP","001");
			parmValue.addData("NCHARGEMNY",parm.getValue("AR_AMT",i));
			parmValue.addData("NCHARGENUMBER",parm.getValue("DOSAGE_QTY",i));
			parmValue.addData("DCHARGEDATE",date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6,8));
			parmValue.addData("VBILLCLERKCODE",parm.getValue("BILL_USER",i));
			parmValue.addData("VBILLCKERKNAME",parm.getValue("BILL_NAME",i));
			parmValue.addData("VBILLDEPTCODE",parm.getValue("DEPT_CODE",i));
			parmValue.addData("VBILLDEPTNAME",parm.getValue("DEPT_NAME",i));
			parmValue.addData("VEXECDEPTCODE",parm.getValue("EXEC_DEPT_CODE",i));
			parmValue.addData("VEXECDEPTNAME",parm.getValue("EXEC_DEPT_NAME",i));
			parmValue.addData("VADMNUMBER",parm.getValue("MR_NO",i));
			parmValue.addData("VPATIENTNAME",parm.getValue("PAT_NAME",i));
			parmValue.addData("VSEX",parm.getValue("SEX_CODE",i));
			parmValue.addData("VOPNAME",parm.getValue("OP_DESC",i));
			parmValue.addData("DOPDATE",parm.getValue("OP_DATE",i));
			parmValue.addData("BUSITYPENAME",type);
			parmValue.addData("VNAMEPHYSICIAN",parm.getValue("DR_NAME",i));
			parmValue.addData("IPATIENTAGE",getYear(parm.getTimestamp("BIRTH_DATE",i)));
			parmValue.addData("VFAMILYMEMBERS",parm.getValue("PAT_FAMLIY",i));
			parmValue.addData("VTEL",parm.getValue("PAT_TEL",i));
			parmValue.addData("VUSERCODE",parm.getValue("BILL_USER",i));//Operator.getID() ��Ϊ parm.getValue("BILL_USER",i)
			parmValue.addData("UNITCODE","001");
			//parmValue.addData("VHISBUSID",parm.getValue("RECEIPT_NO",i));
			parmValue.addData("BISPROC","N");
			//=================================סԺ��
			parmValue.addData("CASE_NO_SEQ", parm.getValue("CASE_NO_SEQ",i));//
			parmValue.addData("CASE_NO", parm.getValue("CASE_NO",i));//�����¼
			parmValue.addData("SEQ_NO", parm.getValue("SEQ_NO",i));//���
			//=================================סԺ��
			sum++;
		}
		parmValue.setData("TYPE_SUM",type);
		parmValue.setCount(parmValue.getCount("PK_EXPLOG"));
		parmValue.setData("CHEKC_DATE",sDate);
		//====================================================��ӵ���־��add by huangjw 20150414
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//����
		logParm.setData("EXPTABLENAME","his_feedetail");//��������
		logParm.setData("BATCHNAME",sDate);
		logParm.setData("EXPTIME",SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", ""));//����ʱ�佨���á�HHMMSS��
		logParm.setData("EXPOPERATOR",Operator.getName());//��������Ա����
		logParm.setData("EXPSTATE",0);//����״̬
		logParm.setData("TAOTALNUM",sum);//������¼����
		logParm.setData("SOURCESYSTEM",dataType);
		//logParm.setData("ADM_TYPE",type);
		//logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		return parmValue;
	}
	private String getYear(Timestamp newDate){
		//SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = new Date();
		long day = (date.getTime() - newDate.getTime()) / (24 * 60 * 60 * 1000)
				+ 1;
		String year=new java.text.DecimalFormat("#.00").format(day/365f);
		return year;
	}
	/**
	 * �ɱ�����
	 * @param type
	 * @return
	 */
	private TParm costDataParm(String type){
		TParm parmValue=new TParm();
		TParm parm=table.getParmValue();
		int row=parm.getCount();
		if (row<=0) {
			this.messageBox("û����Ҫ����������");
			return null;
		}
		parm=tableResult;
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "MONTH_DATE")), "yyyyMM");
		int year = Integer.parseInt(eDate.substring(0, 4));
		int month = Integer.parseInt(eDate.substring(4, 6));
		TParm dateParm = getMonthDate(year, month);
		String uid= getUUID();
		int sum=0;
		int dataType=0;
		String dataMessage="";
		if (this.getValue("TYPE").toString().equals("1")) {
			dataType=2;
			dataMessage="�Һ�";
		}else if (this.getValue("TYPE").toString().equals("2")) {
			dataType=3;
			dataMessage="����";
		}else if (this.getValue("TYPE").toString().equals("3")) {
			dataType=4;
			dataMessage="סԺ";
		}
		for (int i = 0; i <row; i++) {
			//date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			//checkDate=date.substring(0,8);
			parmValue.addData("PK_INCOMEEXP",getUUID());//�ⲿϵͳ���������
			parmValue.addData("DATA_TYPE","D");
			parmValue.addData("PK_EXPLOG", uid);//������־������
			parmValue.addData("BATCHNAME", eDate+dateParm.getValue("LAST_DATE"));//����ǽ�����"YYYYMMDD"
			parmValue.addData("EXPTIME","000000");//����ʱ�佨���á�HHMMSS��
			parmValue.addData("CYEAR", eDate.substring(0,4));//���
			parmValue.addData("CMONTH", eDate.substring(4,6));//�·�
			parmValue.addData("CDAY", eDate+dateParm.getValue("LAST_DATE")+"000000");//����
			parmValue.addData("PK_CORP", "001");//��˾����--��ѡ���룬���ƣ�pk,�Ƽ�����
			parmValue.addData("CORPNAME", "������ͨ��ѧ�ڶ�����ҽԺ");//��˾����--��ѡ���룬���ƣ�pk,�Ƽ�����
			parmValue.addData("PK_ITEM", parm.getValue("BILL_ID",i));//��������
			parmValue.addData("ITEMNAME", parm.getValue("BILL_TYPE",i));//��������
			parmValue.addData("PK_INCOMETYPE", parm.getValue("BILL_ID",i));//�����������
			parmValue.addData("INCOMETYPENAME", parm.getValue("BILL_TYPE",i));//�����������
			parmValue.addData("PK_BUSITYPE",parm.getValue("TYPE_ID",i));//ҵ����������
			parmValue.addData("BUSITYPENAME", parm.getValue("TYPE",i));//ҵ����������
			parmValue.addData("PK_DIAGNOSISDEPT", parm.getValue("DEPT_CODE",i));//������������
			parmValue.addData("DIAGNOSISDEPTNAME", parm.getValue("DEPT_DESC",i));//������������
			parmValue.addData("PK_DIAGNOSTICIAN", "");//����ҽ������
			parmValue.addData("DIAGNOSTICIANNAME","");//����ҽ������
			parmValue.addData("PK_EXEDEPT", parm.getValue("DEPT_CODE",i));//ִ�п�������
			parmValue.addData("EXEDEPTNAME", parm.getValue("DEPT_DESC",i));//ִ�п�������
			parmValue.addData("PK_EXECUTOR", Operator.getID());//ִ��������
			parmValue.addData("EXECUTORNAME",Operator.getName());//ִ��������
			parmValue.addData("PK_BILLCLERK","");//�շ�Ա����
			parmValue.addData("BILLCLERKNAME","");//�շ�Ա����
			parmValue.addData("PK_PATIENT", "");//��������
			parmValue.addData("PATIENTNAME", "");//��������
			parmValue.addData("CUSTCODE", "");//�ͻ�����--�м��
			parmValue.addData("CUSTNAME","");//�ͻ�����--�м��
			parmValue.addData("CPAYMENT", parm.getValue("BILL_TYPE",i));//֧����ʽ--�м��
			parmValue.addData("CPAYITEM", "");//֧����ϸ--�м��
			parmValue.addData("DDATE", eDate+dateParm.getValue("LAST_DATE")+"000000");//��������--�м��
			parmValue.addData("REALDATE", eDate+dateParm.getValue("LAST_DATE")+"000000");//��������--�м��
			parmValue.addData("VNUMBER", parm.getValue("TID",i));//���--�м��
			parmValue.addData("CPAYMENTNUM", parm.getValue("BILL_ID",i));//֧����ʽ����--�м��
			parmValue.addData("CPAYITEMNUM", "");//֧����ϸ����--�м��
			parmValue.addData("PK_CINPATIENTAREA","");//��������
			parmValue.addData("CINPATIENTAREA", "");//����
			parmValue.addData("ISINSURANCE","");//�Ƿ�ҽ��Y:��ҽ��;N:��ҽ��
			parmValue.addData("DISEASE", "");//������
			parmValue.addData("DISEASEGROUP", "");//������
			parmValue.addData("AMOUNT",  parm.getDouble("TOT_AMT",i));//���
			parmValue.addData("SOURCESYSTEM", dataType);//�ⲿ������Դϵͳ
			parmValue.addData("PK_OUTERSYSTEM", dataMessage);//��ϵͳ����
			parmValue.addData("INPRICE", "");//���뵥��
			parmValue.addData("CASHBACK", "");//����
			parmValue.addData("ADM_TYPE", type);//����
			sum+=parm.getInt("NUM",i);
		}
		parmValue.setData("TYPE_SUM",type);
		parmValue.setCount(parmValue.getCount("PK_EXPLOG"));
		parmValue.setData("CHEKC_DATE",eDate+dateParm.getValue("LAST_DATE"));
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//����
		logParm.setData("EXPTABLENAME","");//��������
		logParm.setData("BATCHNAME",eDate+dateParm.getValue("LAST_DATE"));
		logParm.setData("EXPTIME","000000");//����ʱ�佨���á�HHMMSS��
		logParm.setData("EXPOPERATOR",Operator.getName());//��������Ա����
		logParm.setData("EXPSTATE",1);//����״̬
		logParm.setData("TAOTALNUM",sum);//������¼����
		logParm.setData("SOURCESYSTEM",2);
		logParm.setData("ADM_TYPE",type);
		logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		return parmValue; 
	}
	/**
	 * �ɱ��������� add by huangjw 20150409
	 * @param type
	 * @return
	 */
	public TParm costAcountParm(String type){
		TParm parmValue=new TParm();
		table=(TTable) this.getComponent("TABLE7");
		TParm parm=table.getParmValue();
		int row=parm.getCount();
		if (row<=0) {
			this.messageBox("û����Ҫ����������");
			return null;
		}
		parm=tableResult;
		String exptime=SystemTool.getInstance().getDate().toString().substring(0,19);
		String exptime1=SystemTool.getInstance().getDate().toString().substring(11,19).replaceAll(":", "");
		
		String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd");
		String eDateT= StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyy-MM-dd");
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd");
		String sTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_TIME")), "HHmmss");
		String eTime=StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_TIME")), "HHmmss");
		String cyear=eDateT.substring(0,4);
		String cmonth=eDateT.substring(5,7);
		String cday=eDateT;
		parmValue.setData("START_DATE",sDate+sTime);
		parmValue.setData("END_DATE",eDate+eTime);
		String uid= getUUID();
		int sum=0;
		int dataType=0;
		String dataMessage="";
		if (this.getValue("TYPE").toString().equals("1")) {
			dataType=13;
			dataMessage="����";
		}else if (this.getValue("TYPE").toString().equals("2")) {
			dataType=14;
			dataMessage="סԺ";
		}
		for (int i = 0; i <row; i++) {
			parmValue.addData("PK_SERVICEVOLEXP", getUUID());//�ⲿϵͳ���������������
			parmValue.addData("PK_EXPLOG", uid);//������־������
			parmValue.addData("BATCHNAME", sDate);//���������
			if("O".equals(type)){
				parmValue.addData("TYPE", "1");
			}else if("I".equals(type)){
				parmValue.addData("TYPE", "2");
			}
			
			parmValue.addData("BUSITYPENAME", parm.getValue("TYPE",i));
			parmValue.addData("EXPTIME", exptime1);//����ʱ��
			parmValue.addData("CYEAR", cyear);//���
			parmValue.addData("CMONTH", cmonth);//�·�
			parmValue.addData("CDAY", cday);//ҵ������
			parmValue.addData("PK_CORP", "001");//��˾����
			parmValue.addData("CORPNAME", "��������������ҽԺ");//��˾����
			parmValue.addData("PK_ITEM", parm.getValue("PK_ITEM",i));//������Ŀ����
			parmValue.addData("ITEMNAME", parm.getValue("ITEMNAME",i));//������Ŀ����
			parmValue.addData("VOLUME", parm.getDouble("VOLUME",i));//������
			parmValue.addData("PK_DIAGNOSISDEPT", parm.getValue("PK_DIAGNOSISDEPT",i));//��������
			parmValue.addData("DIAGNOSISDEPTNAME", parm.getValue("DIAGNOSISDEPTNAME",i));//��������
			parmValue.addData("PK_OUTERSYSTEM", dataMessage);//
			parmValue.addData("SOURCESYSTEM", dataType);//
			sum+=parm.getDouble("VOLUME",i);
		}
		parmValue.setData("CHEKC_DATE",sDate);
		parmValue.setCount(parmValue.getCount("PK_EXPLOG"));
		 
		TParm logParm=new TParm();
		logParm.setData("PK_EXPLOG",uid);//����
		logParm.setData("EXPTABLENAME","DI_SERVICEVOLEXP".toLowerCase());//��������
		logParm.setData("BATCHNAME",sDate);
		logParm.setData("EXPTIME",exptime1);//����ʱ�佨���á�HHMMSS��
		logParm.setData("EXPOPERATOR",Operator.getName());//��������Ա����
		logParm.setData("EXPSTATE",0);//����״̬
		logParm.setData("TAOTALNUM",sum);//������¼����
		logParm.setData("SOURCESYSTEM",dataType);
		//logParm.setData("ADM_TYPE",type);
		//logParm.setData("DATA_TYPE","D");
		parmValue.setData("LOG_PARM",logParm.getData());
		parmValue.setData("LOCAL_LOG_FLG",parm.getBoolean("LOCAL_LOG_FLG"));
		return parmValue;
	}
	
	
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		// ȥ����-������
		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18)
				+ s.substring(19, 23);
   } 
	/**
	 * �������
	 */
	public void onSave(){
		boolean isDebug = true;
		try{
			TParm result=new TParm();
			TParm checkParm=null;
			TParm checkLog=null;
			int index=0;
			String type="";
			String type4="";
			if (this.getValue("TYPE").toString().equals("1")) {
				type="O";
				type4="����";
			}else if (this.getValue("TYPE").toString().equals("2")) {
				type="I";
				type4="סԺ";
			}
			//���ýӿڣ���ѯHRP�Ƿ��Ѿ����� û�����ؿ��Բ������빦��
			switch (tabbedPane.getSelectedIndex()) {
			case 0://��һ��ҳǩ Ӧ��
				
				table=(TTable)this.getComponent("TABLE");
				result=diIncomeexpAndDiIncomerealParm(1,type);
				index=1;
				break;
			case 1://�ڶ���ҳǩ  ʵ��
				table=(TTable)this.getComponent("TABLE2");
				result=diIncomeexpAndDiIncomerealParm(2,type);
				index=2;
				break;
			case 2://������ҳǩ Ԥ����
				index=3;
				table=(TTable)this.getComponent("TABLE3");
				result=diIncomepreParm(type);
				break;
			case 3://���ĸ�ҳǩ
				index=4;
				table=(TTable)this.getComponent("TABLE4");
				result=costDataParm(type);
				break;
			case 4://�����ҳǩ ��ֵ�Ĳ� add by huangjw 20141112
				index=5;
				table=(TTable)this.getComponent("TABLE6");
				result=highPriceDataParm(index,type4);
				break;
			case 5://������ҳǩ �ɱ����� add by huangjw 20150409
				index=6;
				table=(TTable)this.getComponent("TABLE6");
				result=this.costAcountParm(type);
				break;
			case 6://ҩƷ���
				index=7;
				table=(TTable)this.getComponent("PHA_TABLE");
				if(null==this.getValue("BUSITYPE_NAME")||this.getValue("BUSITYPE_NAME").toString().length()<=0){
					this.messageBox("��ѡ��ҵ������");
					this.grabFocus("BUSITYPE_NAME");
					return;
				}
				result=diDrugsParm();
				break;
			default:
				break;
			}
			if (result.getCount()<=0) {
				this.messageBox("û����Ҫ���������");
				return;
			}
			String funation="";
			if (index>0) {
				TParm parm=new TParm();
				parm.setData("BATCHNAME",StringTool.getString(TypeTool.getTimestamp(getValue(
		        "START_DATE")), "yyyyMMdd"));//���ڣ�ɾ�������ڵ�����
				if (this.getValue("TYPE").equals("1")) {
					parm.setData("BUSITYPENAME","����");
					result.setData("BUSITYPENAME_SUM","����");
				}else{
					parm.setData("BUSITYPENAME","סԺ");
					result.setData("BUSITYPENAME_SUM","סԺ");
				}
				if (index!=5) {
					switch(index){
					case 1: parm.setData("EXPTABLENAME","DI_INCOMEEXP".toLowerCase());//Ӧ��
						break;
					case 2: parm.setData("EXPTABLENAME","DI_INCOMEREAL".toLowerCase());//ʵ��
						break;
					case 3: parm.setData("EXPTABLENAME","DI_INCOMEPRE".toLowerCase());//Ԥ����
						break;
					//case 4: parm.setData("EXPTABLENAME",);
						//break;
					case 6: parm.setData("EXPTABLENAME","DI_SERVICEVOLEXP".toLowerCase());//�ɱ������
						break;
					case 7: 
						parm.setData("EXPTABLENAME","DI_DRUGS".toLowerCase());//ҩƷ���
						parm.setData("BUSITYPENAME",this.getText("BUSITYPE_NAME"));
						result.setData("BUSITYPENAME_SUM",this.getText("BUSITYPE_NAME"));
						break;
					}
					checkLog=BILFinanceTool.getInstance().checkLogDrlogAyh(parm);
					if (checkLog.getErrCode()<0) {
						System.out.println("��ѯ�м��LOG����ִ���:::::"+checkLog.getErrText());
						this.messageBox("3333��ѯ�м��LOG����ִ���");
						return;
					}
				}                                                                                                                                          //��ʱ����
				if (index==1) {			
					//parm.setData("SOURCESYSTEM","1");//�ⲿϵͳ��1.���� 2.�ɱ�
					//result.setData("SOURCESYSTEM_SUM","1");//�ⲿϵͳ��1.���� 2.�ɱ�
					checkParm=BILFinanceTool.getInstance().checkDiIncomeexp(parm);
					funation="onSaveDiIncomeexp";//Ӧ��
				}else if (index==2) {
					checkParm=BILFinanceTool.getInstance().checkDiIncomereal(parm);
					funation="onSaveDiIncomereal";//ʵ��
				}else if(index ==3){
					if(parm.getValue("BUSITYPENAME").equals("����")){
						checkParm=BILFinanceTool.getInstance().checkDiIncomepreO(parm);
						
					}else{
						checkParm=BILFinanceTool.getInstance().checkDiIncomepre(parm);
					}
					
					funation="onSaveDiIncomepre";//Ԥ����
				}else if(index ==4){//�ɱ�
					//result.setData("SOURCESYSTEM_SUM","2");//�ⲿϵͳ��1.���� 2.�ɱ�
					checkParm=BILFinanceTool.getInstance().checkDiIncomeexp(parm);
					funation="onSaveCost";//�ɱ�
				}else if(index==5){//��ֵ�Ĳ�
					checkParm=BILFinanceTool.getInstance().checkDiIncomeHigh(parm);
					funation="onSaveDiIncomeHigh";//��ֵ�Ĳ�
				}else if(index==6){//�ɱ�����
					checkParm=BILFinanceTool.getInstance().checkDiIncomeCostAcount(parm);
					funation="onsaveDiIncomeCostAcount";
				}else if(index==7){//ҩƷ���
					checkParm=BILFinanceTool.getInstance().checkDidrugs(parm);
					funation="onSaveDidrugs";
				}
				if (index!=5) {
					if (checkParm.getErrCode()<0) {
						this.messageBox("��ѯ����");
						return;
					}
					if (checkParm.getCount()>0) {
						if(tableResult.getValue("LOAD_DOWN",0).endsWith("�����")){
							this.messageBox("���������,���������ϴ�");
							return;
						}
						if(JOptionPane.showConfirmDialog(null, "�������ϴ�,�Ƿ������ϴ���", "��Ϣ",JOptionPane.YES_NO_OPTION) == 0){
								result.setData("CHECK_FLG","Y");
						}else{
							return;
						}
					}
				}
			}else{
				return;
			}
			//System.out.println("result:555555::"+result);
			result = TIOM_AppServer.executeAction(
					"action.bil.BILFinanceAction", funation, result);
			if (result.getErrCode()<0) {
				System.out.println("result::ERROR:::"+result.getErrText());
				this.messageBox("����ʧ��");
			}else{
				this.messageBox("����ɹ�");
				//onClear();
			}
			this.onQuery();
		}catch(Exception e){
			if(isDebug){
				System.out.println("come in class: BILFinanceIncomeControl ��method ��onSave");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ѡ������
	 */
	public void onSelect(){
		callFunction("UI|TYPE|setEnabled", true);
		switch (tabbedPane.getSelectedIndex()) {
		case 0://��һ��ҳǩ Ӧ��
			table=(TTable)this.getComponent("TABLE");
			if (this.getValue("TYPE").toString().equals("1")) {
				table.setHeader("���,60;ҵ������,80,ADM_TYPE;�������,80;������,80;��������,80;��������,80;����ҽ��,80;ִ�п���,80;����,80;Ʊ�ݺ���,100;�ܽ��,80; ������,100;����ʱ��,80;�շ���Ա,80,DR_CODE;�����,100;�ϴ�״̬,80;���״̬,80");
				callFunction("UI|OPD_TYPE_Y|setEnabled", true);	
				callFunction("UI|OPD_TYPE_S|setEnabled", true);	
				this.setValue("OPD_TYPE_Y", "");
				//table.setParmMap("TYPE;CASE_NO;MR_NO;PAT_NAME;DEPT_DESC;DR_DESC;EXE_DEPT_DESC;CLINIC_DESC;PRINT_NO;AR_AMT;ACCOUNT_SEQ;ACCOUNT_DATE;PRINT_USER");
			}else if(this.getValue("TYPE").toString().equals("2")){
				table.setHeader("���,60;ҵ������,80;�������,80;������,80;��������,80;��������,80;����ҽ��,80;ִ�п���,80;����,80;Ʊ�ݺ���,100;�ܽ��,80; ������,100;����ʱ��,80;�շ���Ա,80,DR_CODE;�����,100;�ϴ�״̬,80;���״̬,80");
				//table.setParmMap("TYPE;CASE_NO;MR_NO;PAT_NAME;DEPT_DESC;DR_DESC;EXE_DEPT_DESC;CLINIC_DESC;PRINT_NO;AR_AMT;ACCOUNT_SEQ;ACCOUNT_DATE;PRINT_USER");
				callFunction("UI|OPD_TYPE_Y|setEnabled", false);	
				callFunction("UI|OPD_TYPE_S|setEnabled", false);	
			}
			this.setValue("OPD_TYPE_Y", "");
			this.setValue("OPD_TYPE_S", "");
			break;
		case 1://�ڶ���ҳǩ  ʵ��
			table=(TTable)this.getComponent("TABLE2");
			if (this.getValue("TYPE").toString().equals("1")) {
				table.setHeader("���,60;ҵ������,80,ADM_TYPE;֧����ʽ,80;������,80;��������,80;��������,80;����ҽ��,80;ִ�п���,80;����,80;Ʊ�ݺ���,100;�ܽ��,80;�ս����,100;�ս�ʱ��,80;�շ���Ա,80,DR_CODE;�����,100;�ϴ�״̬,80;���״̬,80");
				callFunction("UI|OPD_TYPE_Y|setEnabled", true);	
				callFunction("UI|OPD_TYPE_S|setEnabled", true);	
				//table.setParmMap("TYPE;CASE_NO;MR_NO;PAT_NAME;DEPT_DESC;DR_DESC;EXE_DEPT_DESC;CLINIC_DESC;PRINT_NO;AR_AMT;ACCOUNT_SEQ;ACCOUNT_DATE;PRINT_USER");
			}else if(this.getValue("TYPE").toString().equals("2")){
				table.setHeader("���,60;ҵ������,80;֧����ʽ,80;������,80;��������,80;��������,80;����ҽ��,80;ִ�п���,80;����,80;Ʊ�ݺ���,100;�ܽ��,80;�ս����,100;�ս�ʱ��,80;�շ���Ա,80,DR_CODE;�����,100;�ϴ�״̬,80;���״̬,80");
				callFunction("UI|OPD_TYPE_Y|setEnabled", false);	
				callFunction("UI|OPD_TYPE_S|setEnabled", false);	
				//table.setParmMap("TYPE;CASE_NO;MR_NO;PAT_NAME;DEPT_DESC;DR_DESC;EXE_DEPT_DESC;CLINIC_DESC;PRINT_NO;AR_AMT;ACCOUNT_SEQ;ACCOUNT_DATE;PRINT_USER");
			}
			this.setValue("OPD_TYPE_Y", "");
			this.setValue("OPD_TYPE_S", "");
			break;
		case 2://������ҳǩ Ԥ����
//			if (!this.getValue("TYPE").equals("3")) {
//				this.messageBox("��ѡ��סԺ����");
//				this.setValue("TYPE", "3");
//				//table.setHeader("ҵ������,80;֧����ʽ,80;�����,100;������,80;��������,80;��������,80;����ҽ��,80;ִ�п���,80;����,80;Ʊ�ݺ���,80;�ܽ��,80;������,80;����ʱ��,80;�շ���Ա,80");
//			}
			break;
		case 4://�����ҳǩ  ��ֵ�Ĳ�
			table=(TTable)this.getComponent("TABLE6");
			if(this.getValue("TYPE").toString().equals("1")){
				table.setHeader("ѡ,30,boolean;���,60;�շ�,30,boolean;HRP��ʶ,50,boolean;���ݺ�,100;����,100;�շ���Ŀ����,100;�շ���Ŀ����,100;�վݷ������,100;�վݷ�������;��˾����,60;�շѽ��,60,double;�շ�����,60,double;�շ�����,80;�շ�Ա����,80;�շ�Ա,60;�������ұ���,100;������������,100;ִ�п��ұ���,100;ִ�п�������,100;������,80;����,80;�Ա�,60;����,60;���߼���,80;��ϵ�绰,100;ҽ������,80;�ϴ�״̬,80;���״̬,80");
				table.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28");
			}else{
				table.setHeader("ѡ,30,boolean;���,60;�շ�,30,boolean;HRP��ʶ,50,boolean;���ݺ�,100;����,100;�շ���Ŀ����,100;�շ���Ŀ����,100;�վݷ������,100;�վݷ�������;��˾����,60;�շѽ��,60,double;�շ�����,60,double;�շ�����,80;�շ�Ա����,80;�շ�Ա,60;�������ұ���,100;������������,100;ִ�п��ұ���,100;ִ�п�������,100;������,80;����,80;�Ա�,60;����,60;���߼���,80;��ϵ�绰,100;ҽ������,80;�ϴ�״̬,80;���״̬,80;��������,120;��������,100");
				table.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30");
			}
			break;
		case 6:
			callFunction("UI|TYPE|setEnabled", false);	
			break;
		default:
			break;
		}
	}
	/**
	 * ���
	 */
	public void onClear(){
		onPage();
		table=(TTable)this.getComponent("TABLE");
		table.setParmValue(new TParm());
		table=(TTable)this.getComponent("TABLE2");
		table.setParmValue(new TParm());
		table=(TTable)this.getComponent("TABLE3");
		table.setParmValue(new TParm());
		table=(TTable)this.getComponent("TABLE6");		
		table.setParmValue(new TParm());
		table=(TTable)this.getComponent("PHA_TABLE");
		table.setParmValue(new TParm());
		tabbedPane.setEnabledAt(3, false);
	}
	/**
	 * ҳǩ����¼�
	 */
	public void onChangeTab() {
		onSelect();
	}
	/**
	 * ����xml�ļ�
	 */
//	public void onSaveXml(){
////		table=(TTable)this.getComponent("TABLE4");
////		TParm parm=table.getParmValue();
//		TParm parm=new TParm();
//		String eDate=StringTool.getString(TypeTool.getTimestamp(getValue("MONTH_DATE")), "yyyyMM");
//		int year = Integer.parseInt(eDate.substring(0, 4));
//		int month = Integer.parseInt(eDate.substring(4, 6));
//		TParm dateParm = SystemTool.getInstance().getMonthDate(year, month);
//		parm.setData("MONTH_DATE",eDate);
//		parm.setData("DAY",dateParm.getValue("LAST_DATE"));
//		//parm.setData("TYPE",this.getValue("TYPE"));
//		TParm result=BILFinanceTool.getInstance().onQueryCostXml(parm);
//		
//		if (result.getCount()<=0) {
//			this.messageBox("û����Ҫ����������");
//			return;
//		}
////		String type="";
////		switch (this.getValueInt("TYPE")){
////		case 1:
////			type="-�Һ�";
////			break;
////		case 2:
////			type="-����";
////			break;
////		case 3:
////			type="-סԺ";
////			break;
////		}
//		TParm headParm=new TParm();
//		headParm.addData("corp","001");
//		headParm.addData("billnodate",StringTool.getString(SystemTool.getInstance().getDate(), "yyyy-MM-dd"));//
//		headParm.addData("operator",Operator.getID());
//		if (!XMLCityInwDriver.createXMLFile(headParm, result,  "c:/��̯����.xml")) {
//			this.messageBox("��̯����XML�ļ�����ʧ��!");
//			return;
//		}
//		this.messageBox("��̯����XML�ļ����ɳɹ�,��鿴C�̸�Ŀ¼");
//	}
	/**
	 * ��̯
	 */
	public void onApportion(){
		if (this.getValue("TYPE").toString().equals("1")) {
			this.messageBox("��ѡ�������סԺ����");
			return;
		}
		TParm parm=new TParm();
		String eDate=StringTool.getString(TypeTool.getTimestamp(getValue("MONTH_DATE")), "yyyyMM");
		int year = Integer.parseInt(eDate.substring(0, 4));
		int month = Integer.parseInt(eDate.substring(4, 6));
		TParm dateParm = getMonthDate(year, month);
		parm.setData("MONTH_DATE",eDate);
		parm.setData("DAY",dateParm.getValue("LAST_DATE"));
		parm.setData("TYPE",this.getValue("TYPE"));
		TParm result=BILFinanceTool.getInstance().onApportion(parm);
		if (result.getCount()<=0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		String dataType="";
		String type="";
		String message="";
		if (this.getValue("TYPE").toString().equals("1")) {
			dataType="�Һ�";
		}else if (this.getValue("TYPE").toString().equals("2")) {
			dataType="����";
			type="O";
			message="13";
		}else if (this.getValue("TYPE").toString().equals("3")) {
			dataType="סԺ";
			type="I";
			message="14";
		}
		parm.setData("BATCHNAME",eDate+dateParm.getValue("LAST_DATE"));
		parm.setData("TYPE",type);
		TParm checkParm=BILFinanceTool.getInstance().checkApportion(parm);
		TParm parmValue=new TParm();
		if (checkParm.getErrCode()<0) {
			this.messageBox("��ѯʧ��");
			System.out.println("checkParm;;;;;:"+checkParm);
			return;
		}
		if (checkParm.getCount()>0) {
			if(this.messageBox("��ʾ","����"+eDate+"����,�Ƿ����µ���",2)!=0){
				return;
			}else{
				parmValue.setData("CHECK_FLG","Y");
			}
		}
		for (int i = 0; i <result.getCount(); i++) {
			//date=SystemTool.getInstance().getDateReplace(parm.getValue("ACCOUNT_DATE",i), true).toString();
			//checkDate=date.substring(0,8);
			parmValue.addData("PK_SERVICEVOLEXP",getUUID());//�ⲿϵͳ���������
			//parmValue.addData("DATA_TYPE","A");
			parmValue.addData("PK_EXPLOG", "");//������־������
			parmValue.addData("BATCHNAME", eDate+dateParm.getValue("LAST_DATE"));//����ǽ�����"YYYYMMDD"
			parmValue.addData("EXPTIME","000000");//����ʱ�佨���á�HHMMSS��
			parmValue.addData("CYEAR", eDate.substring(0,4));//���
			parmValue.addData("CMONTH", eDate.substring(4,6));//�·�
			parmValue.addData("CDAY", eDate+dateParm.getValue("LAST_DATE")+"000000");//����
			parmValue.addData("PK_CORP", "001");//��˾����--��ѡ���룬���ƣ�pk,�Ƽ�����
			parmValue.addData("CORPNAME", "������ͨ��ѧ�ڶ�����ҽԺ");//��˾����--��ѡ���룬���ƣ�pk,�Ƽ�����
			parmValue.addData("PK_ITEM", result.getValue("TYPE_ID",i));//��������
			parmValue.addData("ITEMNAME", result.getValue("TYPE",i));//��������
			parmValue.addData("PK_DIAGNOSISDEPT_TWO", result.getValue("DEPT_CODE",i));//������������
			parmValue.addData("DIAGNOSISDEPTNAME_TWO", result.getValue("DEPT_DESC",i));//������������
			parmValue.addData("VOLUME",  result.getDouble("NUM",i));//������
			parmValue.addData("SOURCESYSTEM", message);//�ⲿ������Դϵͳ
			parmValue.addData("PK_OUTERSYSTEM", dataType);//��ϵͳ����
			parmValue.addData("PK_MEASURE", "1");
			parmValue.addData("MEASURENAME", "�˴�");
			parmValue.addData("TYPE", type);//����
		}
		parmValue.setData("CHEKC_DATE",eDate+dateParm.getValue("LAST_DATE"));
		parmValue.setData("TYPE_SUM",type);
		parmValue.setCount(result.getCount());
		result = TIOM_AppServer.executeAction(
				"action.bil.BilAction", "onSaveApportion", parmValue);
		if (result.getErrCode()<0) {
			this.messageBox("����ʧ��");
		}else{
			this.messageBox("����ɹ�");
		}
	}
	/**
     * ���Excel
     */
    public void onExcel() {
    	if(table.getRowCount()<=0){
    		this.messageBox("û�л������");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, tableName);
    }
    /**
     * ���һ���µ����һ�켸��\���ڼ� \��һ������
     * @param year
     * @param month
     * @return parm ���� ����ֵ 1.LAST_DATE���һ�켸�� 2.LAST_DAY ���һ������ 3.FIRST_DAY ��һ������
     */
  	private TParm getMonthDate(int year, int month) {
  		// ��������2�У�����ȡ��ǰʱ��ǰһ���µĵ�һ�켰���һ��
  		Calendar cal = Calendar.getInstance();
  		cal.set(Calendar.YEAR, year);
  		cal.set(Calendar.MONTH, month);
  		cal.set(Calendar.DAY_OF_MONTH, 1);
  		cal.add(Calendar.DAY_OF_MONTH, -1);
  		Date lastDate = cal.getTime();
  		cal.set(Calendar.DAY_OF_MONTH, 1);
  		Date firstDate = cal.getTime();
  		TParm parm = new TParm();
  		parm.setData("LAST_DATE", lastDate.getDate());// ���һ�켸��
  		parm.setData("LAST_DAY", lastDate.getDay());// ���һ������
  		parm.setData("FIRST_DAY", firstDate.getDay());// ��һ������
  		return parm;
  	}
  	public void onSel(){
  		TTable table=(TTable)this.getComponent("TABLE6");
  		TParm tableParm =table.getParmValue();
  		if (tableParm.getCount()<=0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
  		TCheckBox chkSel=(TCheckBox)this.getComponent("CHK_SEL");
  		if (chkSel.isSelected()) {
  			for (int i = 0; i < tableParm.getCount(); i++) {
  				if (tableParm.getValue("UP_LOAD",i).equals("δ�ϴ�")) {
  					tableParm.setData("FLG",i,"Y");
  				}
  			}
		}else{
			for (int i = 0; i < tableParm.getCount(); i++) {
  				if (tableParm.getValue("UP_LOAD",i).equals("δ�ϴ�")) {
  					tableParm.setData("FLG",i,"N");
  				}
  			}
		}
  		
  		table.setParmValue(tableParm);
  	}
  	
  	/**
	 * table checkbox�����¼�
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onCheckBox(Object obj) {
		TTable chargeTable = (TTable) obj;
		chargeTable.acceptText();
		TTable table=(TTable)this.getComponent("TABLE6");
		int col = table.getSelectedColumn();
		String columnName = table.getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		if ("FLG".equals(columnName)) {
			if (chargeTable.getParmValue().getValue("UP_LOAD",row).equals("δ�ϴ�")) {
				if(chargeTable.getParmValue().getValue("FLG",row).equals("Y")){
					chargeTable.getParmValue().setData("FLG",row,"Y");
				}else{
					chargeTable.getParmValue().setData("FLG",row,"N");
				}
			}else{
				this.messageBox("���ϴ������Բ���");
				chargeTable.getParmValue().setData("FLG",row,"N");
			}
		}
		table.setParmValue(chargeTable.getParmValue());
		return true;
	}
	public void onBusitype(){
		this.setValue("DEPT_CODE", "");
		this.setValue("SUP_CODE", "");
		if(this.getText("BUSITYPE_NAME").equals("ҩƷ�ɹ����")){
			callFunction("UI|DEPT_CODE|setEnabled", false);	
			callFunction("UI|SUP_CODE|setEnabled", true);
		}else if(this.getText("BUSITYPE_NAME").equals("ҩƷ����")){
			callFunction("UI|DEPT_CODE|setEnabled", true);	
			callFunction("UI|SUP_CODE|setEnabled", false);	
		}else if(this.getText("BUSITYPE_NAME").equals("��������")){
			callFunction("UI|DEPT_CODE|setEnabled", false);	
			callFunction("UI|SUP_CODE|setEnabled", false);	
		}
	}
}