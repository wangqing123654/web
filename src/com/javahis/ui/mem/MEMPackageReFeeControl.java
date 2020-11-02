package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jdo.bil.PaymentTool;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;
import jdo.mem.MEMSQL;
import jdo.opb.OPBTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
/**
*
* <p>Title: �ײ��˷�</p>
*
* <p>Description: �ײ��˷�</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author duzhw 20140303
* @version 4.5
*/
public class MEMPackageReFeeControl extends TControl {
	//�ײͽ��ױ�;ʱ���ײͱ�;�ײ���ϸ��
	private TTable tradeTable,sectionTable,detailTable;
	//���ڸ�ʽ��
	private SimpleDateFormat formateDate=new SimpleDateFormat("yyyy/MM/dd");
	
	Pat pat;
	private TParm parmEKT; // ҽ�ƿ���Ϣ add by sunqy 20140707
	PaymentTool paymentTool;
	PaymentTool paymentTool2;
	TParm payParm;
	
	 private DecimalFormat df = new DecimalFormat("##########0.00");
	
	 /**
     * ��ʼ��
     */
    public void onInit() { // ��ʼ������
        super.onInit();
        initComponent();
        initData();
        
    }
    
    /**
	 * ��ʼ���ؼ�
	 */
    private void initComponent() {
    	TPanel p = (TPanel) getComponent("PanelCashT");
    	TPanel p2 = (TPanel) getComponent("PanelCash");
    	try {
    		paymentTool = new PaymentTool(p, this);
			paymentTool2 = new PaymentTool(p2, this);
			paymentTool2.table.setLockRows("0");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	tradeTable = getTable("TRADE_TABLE");
    	sectionTable = getTable("SECTION_TABLE");
    	detailTable = getTable("DETAIL_TABLE");
    }
    
    /**
     * ��ʼ������   
     */
    private void initData(){
    	//��ѯʱ��Ĭ�ϵ���
    	Timestamp now = StringTool.getTimestamp(new Date());
//   	 	this.setValue("START_DATE",
//		 		now.toString().substring(0, 10).replace('-', '/'));
   	 	this.setValue("END_DATE",
		 		now.toString().substring(0, 10).replace('-', '/'));
   	 	//��ʼʱ��Ϊ�ϸ���-Ĭ��
   	 	Calendar cd = Calendar.getInstance();
   	 	cd.add(Calendar.MONTH, -1);
   	 	String format = formateDate.format(cd.getTime());
		this.setValue("START_DATE", formateDate.format(cd.getTime()));
		
   	 	//����������Ч
   	 	callFunction("UI|MR_NO|setEnabled", true);  //������
   	 	//��������
   	 	Object obj = this.getParameter();
   	 	if (obj instanceof TParm) {
   	 		TParm acceptData = (TParm) obj;
   	 		String mrNo = acceptData.getData("MR_NO").toString();
   	 		this.setValue("MR_NO", mrNo);
   	 		this.onMrno();
   	 	}
    }
    
    /**
     * ��ѯ
     */
    public void onQuery() {
    	//���ݼ��--�������ûҲ���������
    	if(!checkData()){
    		return;
    	}
    	String mrNo = this.getValue("MR_NO").toString();
    	String sqlMinBillDate = "SELECT MIN(BILL_DATE) AS BILL_DATE FROM MEM_PACKAGE_TRADE_M WHERE MR_NO = '"+mrNo+"'";
    	TParm MinBillDateParm = new TParm(TJDODBTool.getInstance().select(sqlMinBillDate));
    	String MinBillDate = MinBillDateParm.getValue("BILL_DATE",0);
    	if( null != MinBillDate && !"".equals(MinBillDate)){
    		MinBillDate = MinBillDate.substring(0, 10).replace('-', '/');
    	}
    	this.setValue("START_DATE", MinBillDate);
    	String startDate = this.getValueString("START_DATE");
    	if(startDate.length()>0){
    		startDate = startDate.toString().replaceAll("-", "/").substring(0, 10);
    	}
    	String endDate = this.getValueString("END_DATE");
    	if(endDate.length()>0){
    		endDate = endDate.toString().replaceAll("-", "/").substring(0, 10);
    	}
    	
    	String sql = "SELECT ROWNUM AS ID, A.*, B.PACKAGE_CODE FROM MEM_PACKAGE_TRADE_M A," +
    			" (SELECT TRADE_NO,PACKAGE_CODE FROM MEM_PAT_PACKAGE_SECTION GROUP BY TRADE_NO ,PACKAGE_CODE) B " +
    			" WHERE A.TRADE_NO=B.TRADE_NO AND A.MR_NO = '"+mrNo+"' ";
    	if(startDate.length()>0 && endDate.length()>0){
    		sql += " AND A.BILL_DATE >= TO_DATE('"+startDate+" 000000"+"','YYYY/MM/DD HH24MISS') AND " +
    				" A.BILL_DATE <= TO_DATE('"+endDate+" 235959"+"','YYYY/MM/DD HH24MISS')";
    	}
    	//System.out.println("sql=========="+sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	tradeTable.setParmValue(result);
    	
    }
    
    /**
     * �����ײͱ����ױ������¼�
     */
    public void onClickTradeTable() {
    	int selectedIndx=tradeTable.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
    	//�����ϸ��
    	TParm parm = new TParm();
    	detailTable.setParmValue(parm);
    	//����˷ѽ��
    	this.setValue("RE_FEE", "0.00");
    	
    	TParm tradeTableParm=tradeTable.getParmValue();
    	String tradeNO = tradeTableParm.getValue("TRADE_NO",selectedIndx);
    	String packageCode = tradeTableParm.getValue("PACKAGE_CODE",selectedIndx);
    	String mrNo = tradeTableParm.getValue("MR_NO",selectedIndx);
    	//ҳ�潻����Ϣ��ֵ
    	String sqlTrade = "SELECT * FROM MEM_PACKAGE_TRADE_M WHERE TRADE_NO = '"+tradeNO+"'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sqlTrade));
    	if(result.getCount()>0){
    		//ҳ����Ϣ
    		setDataForTrade(result);
    		if(result.getValue("BILL_TYPE", 0).equals("C")){
    			callFunction("UI|PanelCash|setVisible", true);  
    			callFunction("UI|PanelCashT|setVisible", true);  
    			callFunction("UI|PanelEkt|setVisible", false);  
    			callFunction("UI|PanelEktT|setVisible", false); 
    			this.clearValue("PAY_OTHER4_T;PAY_OTHER3_T;NO_PAY_OTHER_T;PAY_OTHER4;PAY_OTHER3;NO_PAY_OTHER;EKT_AMT;EKT_PAY_OTHER4;EKT_PAY_OTHER3;EKT_NO_PAY_OTHER;EKT_TOT_AMT");
    			//add by huangtt 20141225 �����ײ�ʱ��֧����ʽ
        		getMarketPackPayType(result.getRow(0));
    		}else{
    			callFunction("UI|PanelCash|setVisible", false);  
    			callFunction("UI|PanelCashT|setVisible", false);  
    			callFunction("UI|PanelEkt|setVisible", true);  
    			callFunction("UI|PanelEktT|setVisible", true);
    			paymentTool.onClear();
    			String ektSql = "SELECT TRADE_NO,AMT,PAY_OTHER3,PAY_OTHER4 FROM EKT_TRADE WHERE MR_NO='"+this.getValueString("MR_NO")+"' AND CASE_NO='"+tradeNO+"' AND BUSINESS_TYPE='MEM'";
    			System.out.println("ektsql----"+ektSql);
    			TParm ektParm = new TParm(TJDODBTool.getInstance().select(ektSql));
    			if(ektParm.getCount() > 0){
    				this.setValue("EKT_PAY_OTHER4", ektParm.getDouble("PAY_OTHER4", 0));
    				this.setValue("EKT_PAY_OTHER3", ektParm.getDouble("PAY_OTHER3", 0));
    				this.setValue("EKT_TOT_AMT", ektParm.getDouble("AMT", 0));
    				this.setValue("EKT_NO_PAY_OTHER", ektParm.getDouble("AMT", 0)-ektParm.getDouble("PAY_OTHER3", 0)-ektParm.getDouble("PAY_OTHER4", 0));
    				this.setValue("EKT_TRADE_NO", ektParm.getValue("TRADE_NO", 0));
    			}
    			double payOther3 = EKTpreDebtTool.getInstance().getPayOther(pat.getMrNo(), EKTpreDebtTool.PAY_TOHER3);
    			double payOther4 = EKTpreDebtTool.getInstance().getPayOther(pat.getMrNo(), EKTpreDebtTool.PAY_TOHER4);
    			setValue("PAY_OTHER3", payOther3);
    			setValue("PAY_OTHER4", payOther4);
    			setValue("NO_PAY_OTHER", getValueDouble("EKT_CURRENT_BALANCE") - payOther3 - payOther4);
    			setValue("EKT_AMT", getValueDouble("EKT_CURRENT_BALANCE"));
    		}
    		
    	}
    	//��ѯʱ���ײ�sql
    	String sql = "SELECT 'N' AS EXEC,CASE USED_FLG WHEN '0' THEN 'N' WHEN '1' THEN 'Y' END USED_FLG,ROWNUM AS ID1," +
    			" A.ID,A.TRADE_NO,A.PACKAGE_CODE,A.SECTION_CODE,A.CASE_NO,A.MR_NO,A.PACKAGE_DESC,A.SECTION_DESC," +
    			" A.ORIGINAL_PRICE,A.SECTION_PRICE,A.AR_AMT,A.DESCRIPTION,A.REST_TRADE_NO FROM MEM_PAT_PACKAGE_SECTION A " +
    			" WHERE  A.TRADE_NO = '"+tradeNO+"' AND A.MR_NO = '"+mrNo+"' " +
    			" AND A.PACKAGE_CODE = '"+packageCode+"' " +
    			" AND REST_TRADE_NO IS NULL";
    	result = new TParm(TJDODBTool.getInstance().select(sql));
    	sectionTable.setParmValue(result);
    	
    }
    
    /**
     * �ײ�ʱ�̱����¼�
     */
    public void onClickSectionTable() {
    	int selectedIndx=sectionTable.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
    	TParm sectionTableParm=sectionTable.getParmValue();
    	String tradeNO = sectionTableParm.getValue("TRADE_NO",selectedIndx);
    	String packageCode = sectionTableParm.getValue("PACKAGE_CODE",selectedIndx);
    	String sectionCode = sectionTableParm.getValue("SECTION_CODE", selectedIndx);
    	String mrNo = sectionTableParm.getValue("MR_NO",selectedIndx);
    	//��ѯ��ϸ�ײ�sql
    	String sql = "SELECT 'N' AS EXEC,CASE USED_FLG WHEN '0' THEN 'N' WHEN '1' THEN 'Y' END USED_FLG,ROWNUM AS ID1," +
    			" A.ID,A.TRADE_NO,A.PACKAGE_CODE,A.SECTION_CODE,A.PACKAGE_DESC,A.SECTION_DESC,A.CASE_NO,A.MR_NO,A.SEQ," +
    			" A.ORDER_CODE,A.ORDER_DESC,A.ORDER_NUM,A.UNIT_CODE," +
    			" B.UNIT_PRICE,B.RETAIL_PRICE,A.RETAIL_PRICE AS PAY_PRICE,A.DESCRIPTION,A.OPT_DATE," +
    			" A.OPT_USER,A.OPT_TERM,A.SETMAIN_FLG,A.ORDERSET_CODE,A.ORDERSET_GROUP_NO,A.HIDE_FLG,A.REST_TRADE_NO " +
    			" FROM MEM_PAT_PACKAGE_SECTION_D A,MEM_PACKAGE_SECTION_D B " +
    			" WHERE A.TRADE_NO = '"+tradeNO+"' AND A.PACKAGE_CODE = '"+packageCode+"' " +
    			" AND A.PACKAGE_CODE = B.PACKAGE_CODE(+) AND A.SECTION_CODE = B.SECTION_CODE(+) " +
//    			" AND A.UNIT_PRICE = B.UNIT_PRICE(+) " +
    			" AND A.ORDERSET_GROUP_NO=B.ORDERSET_GROUP_NO(+) " +
    			" AND A.SECTION_CODE = '"+sectionCode+"' AND A.MR_NO = '"+mrNo+"' " +
    			" AND A.HIDE_FLG = 'N' AND B.HIDE_FLG(+) = 'N' " +
    			" ORDER BY A.ID ";
    	System.out.println("-�ײ��˷�-�ײ�ʱ�̱����¼�--sql="+sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	detailTable.setParmValue(result);
    	
    	//��ѡ"ѡ"��ť��ֵ����
    	if(sectionTable.getSelectedColumn() == 0){//��һ��"ѡ"
    		sectionTable.acceptText();
    		int row = sectionTable.getSelectedRow();
    		TParm parm = sectionTable.getParmValue();
    		parm.setData("EXEC",row, parm.getValue("EXEC", row).equals("Y")?"N":"Y");
    		sectionTable.setParmValue(parm);
    		sectionTable.setSelectedRow(row);
    		sectionTable.acceptText();
    		//�۸�ͳ��
        	PriceStatistics();
    		
    	}
    	
    }
    
    /**
     * ���棨�˷ѣ�
     */
    public void onSave() {
    	
    	//add by huangtt 20141225 start �˷�֧����ʽ
    	payParm = null;
    	TParm payTypeTParm= new TParm();
//    	if(this.getValueString("BILL_TYPE").equals("C")){
//    		
//    	}
    	//----start-------add by kangy 20160718------΢��֧����֧����Ҫ��ӽ��׺�
        payTypeTParm=paymentTool.table.getParmValue();	
    	boolean flg2=paymentTool.onCheckPayType(payTypeTParm);
	    if (flg2) {
	    } else {
			this.messageBox("�����������ͬ��֧����ʽ��");
			return;
		}
		//----end-----add by kangy 20160718------΢��֧����֧����Ҫ��ӽ��׺�
		try {
			payParm = paymentTool.getAmts();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			messageBox(e.getMessage());
			return;
		}
    	//add by huangtt 20141125 end
    	
    	TParm result = new TParm();
    	TParm update = new TParm();
    	
    	//���ݼ��
    	if(!checkSectionData()){
    		return;
    	}
    	//��ù�ѡʱ���ײ�
    	TParm sectionParm = getSectionParm();
    	//System.out.println("sectionParm="+sectionParm);
    	//���������ײͷ��ã�����ȡ���׺�
    	TParm tradeParm = getTradeParm(sectionParm);
    	
    	update.setData("TRADEDATA", tradeParm.getData());
    	update.setData("SECTIONDATA", sectionParm.getData());
    	//�ֽ��Ʊ������У���Ƿ����֧������΢�Ž��
		TParm checkCashTypeParm=OPBTool.getInstance().checkCashTypeOther(payTypeTParm);
		if(this.getValueString("BILL_TYPE").equals("C")){
			TParm payCashParm=null;
			if(null!=checkCashTypeParm.getValue("WX_FLG")&&
					checkCashTypeParm.getValue("WX_FLG").equals("Y")||null!=checkCashTypeParm.getValue("ZFB_FLG")&&
					checkCashTypeParm.getValue("ZFB_FLG").equals("Y")){
				Object resultParm = this.openDialog(
	    	            "%ROOT%\\config\\bil\\BILPayTypeTransactionNo.x", checkCashTypeParm, false);
				if(null==resultParm){
					return ;
				}
				payCashParm=(TParm)resultParm;
			}
			if(null!=payCashParm){
				update.setData("payCashParm",payCashParm.getData());
			}
		}else{
			if(this.onPayOther()){//δ�շ�
				return ;
			}
    		
    		if(parmEKT == null){
    			this.messageBox("���ȡҽ�ƿ���Ϣ");
    			return ;
    		}
    		if (!EKTIO.getInstance().ektSwitch()) {
    			messageBox_("ҽ�ƿ�����û������!");
    			return ;
    		}
    		TParm parm = new TParm();
    		parm.setData("READ_CARD", parmEKT.getData());
    		parm.setData("PAY_OTHER3",this.getValueDouble("PAY_OTHER3_T"));
    		parm.setData("PAY_OTHER4",this.getValueDouble("PAY_OTHER4_T"));
    		parm.setData("EXE_AMT",-this.getValueDouble("RE_FEE"));
    		parm.setData("MR_NO",this.getValueString("MR_NO"));
    		parm.setData("BUSINESS_TYPE","MEM");
    		parm.setData("CASE_NO",tradeParm.getValue("TRADE_NO", 0));//���׺�
    		parm.setData("RESET_TRADE_NO", this.getValueString("EKT_TRADE_NO"));
    		Object r =  this.openDialog("%ROOT%\\config\\ekt\\EKTChageOtherUI.x",parm);
    		if(r == null){
    			this.messageBox("ҽ�ƿ��ۿ�ȡ������ִ�б���");
    			return;
    		}
    		TParm rParm = (TParm) r;
    		if(rParm.getErrCode() < 0){
    			this.messageBox(rParm.getErrText());
    			return;
    		}else if(rParm.getValue("OP_TYPE").equals("2")){
    			return;
    		}else{
    			update.setData("ektSql", rParm.getData("ektSql"));
    		}
		}
		
    	//��ʼ�˷�
    	result = TIOM_AppServer.executeAction("action.mem.MEMPackageReFeeAction",
				"onSave", update);
		if(result.getErrCode()<0){
			this.messageBox("����ʧ�ܣ�");
			return;
		}
    	this.messageBox("����ɹ���");
    	
    	//add by sunqy 20140616  ----start----
		TParm parm = new TParm();
		DecimalFormat df=new DecimalFormat("#0.00");
		parm.setData("TITLE", "TEXT", "�ײ��˷��վ�");
		parm.setData("TYPE", "TEXT", ""); //���
		parm.setData("MR_NO", "TEXT", this.getValue("MR_NO")); // ������
		String sql = "SELECT PACKAGE_DESC CTZ_CODE FROM MEM_PACKAGE WHERE PACKAGE_CODE = '"+sectionTable.getValueAt(0, 2)+"'";
		TParm sqlResult = new TParm(TJDODBTool.getInstance().select(sql));
		sqlResult = sqlResult.getRow(0);
		parm.setData("RecNO", "TEXT", SystemTool.getInstance().getNo("ALL", "EKT", "MEM_NO", "MEM_NO")); //Ʊ�ݺ�
		parm.setData("DEPT_CODE", "TEXT", "");// �Ʊ�
		parm.setData("PAT_NAME", "TEXT", this.getValue("PAT_NAME")); // ����
		String payType = "";
		
		//add by huangtt 20141226 start 
		TParm teadeM = new TParm(TJDODBTool.getInstance().select("SELECT * FROM MEM_PACKAGE_TRADE_M WHERE TRADE_NO='"+tradeParm.getValue("TRADE_NO",0)+"'"));
		TParm payTypeParm = MEMSQL.getMemType(teadeM.getRow(0));
		if(payTypeParm.getCount() < 3){
			for (int i = 0; i < payTypeParm.getCount(); i++) {
				payType += payTypeParm.getValue("PAYTYPE_DESC", i)+":"+df.format(payTypeParm.getDouble("PAYTYPE_AMT", i))+"Ԫ ";
			}
			parm.setData("PAY_TYPE", "TEXT", payType);// ֧����ʽ
		}else{
			String payType2="";
			String payType3="";
			payType2 += payTypeParm.getValue("PAYTYPE_DESC", 0)+":"+df.format(payTypeParm.getDouble("PAYTYPE_AMT", 0))+"Ԫ ";
			for (int i = 1; i < 3; i++) {
				payType3 += payTypeParm.getValue("PAYTYPE_DESC", i)+":"+df.format(payTypeParm.getDouble("PAYTYPE_AMT", i))+"Ԫ ";
			}
			parm.setData("PAY_TYPE2", "TEXT", payType2);// ֧����ʽ
			parm.setData("PAY_TYPE3", "TEXT", payType3);// ֧����ʽ
		}
		
//		String bankNo = "";
//		for (int i = 1; i < 11; i++) {
//			if(!"".equals(teadeM.getValue("MEMO"+i,0))){
//				bankNo += teadeM.getValue("MEMO"+i,0)+";";
//			}
//		}
//		if(bankNo.length()>0){
//			bankNo = bankNo.substring(0, bankNo.length()-1);
//		}
//		System.out.println("bankNo=="+bankNo);
//		parm.setData("ACOUNT_NO", "TEXT", bankNo);// �˺�
		
//		payType += "�ֽ�" + df.format(Double.parseDouble((this.getValue("RE_FEE"))+""))+"Ԫ";
		//add by huangtt 20141226 end
		
		
		
		parm.setData("MONEY", "TEXT", (df.format(StringTool.round(this.getValueDouble("RE_FEE"), 2))+"Ԫ")); // ���
		parm.setData("CAPITAL", "TEXT", StringUtil.getInstance().numberToWord(this.getValueDouble("RE_FEE"))); // ��д���
//		parm.setData("ACOUNT_NO", "TEXT", "");// �˺�
		
		//����Ʒ�����߸���ʱ����������ʾ modify by huangjw 20140105 start
		//String ctz_code=this.getText("PACKAGE_CODE");
		String ctz_code=tradeTable.getShowParmValue().getValue("PACKAGE_CODE",tradeTable.getSelectedRow());
		if(ctz_code.length()<=7){
			parm.setData("CTZ_CODE0", "TEXT", ctz_code);// ��Ʒ
		}else{
			parm.setData("CTZ_CODE","TEXT",ctz_code.substring(0,7));
			parm.setData("CTZ_CODE1","TEXT",ctz_code.substring(7,ctz_code.length()));
		}
		//����Ʒ�����߸���ʱ����������ʾ modify by huangjw 20150105 end 
		//�վ�����������շѣ�Ҫ��ʾ�����ͺͿ��� add by huanjw 20150105 start
		//start---modify by kangy 20171019 ΢��֧������ӿ����ͱ�ע���׺�
		String memo2="";
		String memo9="";
		String memo10="";
		memo2=teadeM.getValue("MEMO2",0);
		memo9=teadeM.getValue("MEMO9",0);
		memo10=teadeM.getValue("MEMO10",0);
		String cardtypeString="";
		if(!"".equals(memo2)&&!"#".equals(memo2)){
			String [] str=memo2.split("#");
			String [] str1=str[0].split(";");
//			String [] str2=str[1].split(";");
			String [] str2=null;
			if(str.length == 2){
				str2=str[1].split(";");
			}
			for(int m=0;m<str1.length;m++){
				String cardsql= "select CHN_DESC from sys_dictionary where id='"+str1[m]+"' and group_id='SYS_CARDTYPE'";
				TParm cardParm=new TParm(TJDODBTool.getInstance().select(cardsql));
				cardtypeString+=";"+cardParm.getValue("CHN_DESC",0)+" ";				
				if(str2 != null){
					if(m < str2.length ){
						cardtypeString=cardtypeString+str2[m]+" ";
					}
				}
				
			}
		}
		//�վ�����������շѣ�Ҫ��ʾ�����ͺͿ��� add by huanjw 20150105 end
		if(!"".equals(memo9)&&!"#".equals(memo9)){
			String [] str=memo9.split("#");
			String [] str1=str[0].split(";");
			String [] str2=null;
			if(str.length == 2){
				str2=str[1].split(";");
			}
			for(int m=0;m<str1.length;m++){
				String cardsql= "select CHN_DESC from sys_dictionary where id='"+str1[m]+"' and group_id='SYS_CARDTYPE'";
				TParm cardParm=new TParm(TJDODBTool.getInstance().select(cardsql));
				cardtypeString+=";"+cardParm.getValue("CHN_DESC",0)+" ";				
				if(str2 != null){
					if(m < str2.length ){
						cardtypeString+="��ע:"+str2[m]+" ";
					}
				}
				if(teadeM.getValue("WX_BUSINESS_NO",0).length()>0){
					cardtypeString+=" ���׺�:"+teadeM.getValue("WX_BUSINESS_NO",0);
				}
			}
		}
		if(!"".equals(memo10)&&!"#".equals(memo10)){
			String [] str=memo10.split("#");
			String [] str1=str[0].split(";");
//			String [] str2=str[1].split(";");
			String [] str2=null;
			if(str.length == 2){
				str2=str[1].split(";");
			}
			for(int m=0;m<str1.length;m++){
				String cardsql= "select CHN_DESC from sys_dictionary where id='"+str1[m]+"' and group_id='SYS_CARDTYPE'";
				TParm cardParm=new TParm(TJDODBTool.getInstance().select(cardsql));
				cardtypeString+=";"+cardParm.getValue("CHN_DESC",0)+" ";				
				if(str2 != null){
					if(m < str2.length ){
						cardtypeString+="��ע:"+str2[m]+" ";
					}
				}
				if(teadeM.getValue("ZFB_BUSINESS_NO",0).length()>0){
					cardtypeString+=" ���׺�:"+teadeM.getValue("ZFB_BUSINESS_NO",0);
				}
				
			}
		}
		if (cardtypeString.length()>1) {
			cardtypeString = cardtypeString.substring(1, cardtypeString.length());
		}
		if(cardtypeString.length()<=50){
			parm.setData("ACOUNT_NO1", "TEXT", cardtypeString);// ��Ʒ
		}else{
			parm.setData("ACOUNT_NO2","TEXT",cardtypeString.substring(0,50));
			parm.setData("ACOUNT_NO3","TEXT",cardtypeString.substring(50,cardtypeString.length()));
		}
		//parm.setData("CTZ_CODE", "TEXT", sqlResult.getValue("CTZ_CODE"));// ��Ʒ
		parm.setData("REASON", "TEXT", ((TComboBox)getComponent("DISCOUNT_REASON")).getSelectedName());// �ۿ�ԭ��
		String date = StringTool.getTimestamp(new Date()).toString().substring(
				0, 19).replace('-', '/');
		parm.setData("DATE", "TEXT", date);// ����
		parm.setData("OP_NAME", "TEXT", Operator.getID()); // �տ���
		parm.setData("RETURN", "TEXT", "��"); // ��
		parm.setData("o", "TEXT", "o");// ��
		parm.setData("COPY", "TEXT", ""); // ��ӡע��
		parm.setData("EXPLAIN", "TEXT", "˵�������ײ��轻��ԭ�ײ��վ�"); //˵�� add by huangtt 201500924
		
		parm.setData("HOSP_NAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalCHNFullName() : "����ҽԺ");
        parm.setData("HOSP_ENAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalENGFullName() : "ALL HOSPITALS");
//		this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMPackReceiptV45.jhw",parm, true);
		this.openPrintDialog(IReportTool.getInstance().getReportPath("MEMPackReceiptV45.jhw"),
				IReportTool.getInstance().getReportParm("MEMPackReceiptV45.class", parm));//����ϲ� 
		//add by sunqy 20140616  ----end----
    	//�������table����
    	String mrNo = this.getValueString("MR_NO");
    	onClear();
    	this.setValue("MR_NO", mrNo);
    	onMrno();
    	
    	
    }
    /**
     * ��ù�ѡʱ��parm
     */
    public TParm getSectionParm() {
    	TParm parm = new TParm();
    	TParm result = new TParm();
    	TParm sectionParm = sectionTable.getParmValue();
    	int count = sectionParm.getCount();
    	if(count>0){
    		int j = 0;
    		for (int i = 0; i < count; i++) {
    			boolean exec = sectionParm.getBoolean("EXEC", i);
    			boolean used = parm.getBoolean("USED_FLG", i);
    			if(exec && !used){
					//��ӵ�parm��
					parm.addData("TRADE_NO", sectionParm.getValue("TRADE_NO", i));
					parm.addData("PACKAGE_CODE", sectionParm.getValue("PACKAGE_CODE", i));
					parm.addData("SECTION_CODE", sectionParm.getValue("SECTION_CODE", i));
					parm.addData("MR_NO", sectionParm.getValue("MR_NO", i));
					parm.addData("ORIGINAL_PRICE", sectionParm.getValue("ORIGINAL_PRICE", i));
					parm.addData("RETAIL_PRICE", sectionParm.getValue("SECTION_PRICE", i));
					parm.addData("AR_AMT", sectionParm.getValue("AR_AMT", i));
					
					result.addRowData(parm, j);
					j++;
					
				}
    		}
    	}
    	return result;
    }
    /**
     * ��ù�ѡϸ��parm
     
    public TParm getdetailParm() {
    	TParm parm = new TParm();
    	TParm result = new TParm();
    	result.setCount(0);
    	TParm detailTableParm = detailTable.getParmValue();
		int count = detailTableParm.getCount();
		if(count>0){
			int j = 0;
			for (int i = 0; i < count; i++) {
				boolean exec = detailTableParm.getBoolean("EXEC", i);
				System.out.println("exec="+exec);
				if(exec){
					//��ӵ�sectionParm��
					parm.addData("ID", detailTableParm.getValue("ID", i));
					parm.addData("TRADE_NO", detailTableParm.getValue("TRADE_NO", i));
					parm.addData("ORDER_CODE", detailTableParm.getValue("ORDER_CODE", i));
					parm.addData("ORDER_DESC", detailTableParm.getValue("ORDER_DESC", i));
					
					result.addRowData(parm, j);
					j++;
					
				}
			}
		}
		return result;
    }  */
    
    /**
     * ��ȡ��������parm
     
    public TParm getSendBackParm(TParm parm) {
    	TParm result = new TParm();
    	TParm dparm1 = new TParm();
    	TParm dparm2 = new TParm();
    	int count = parm.getCount("TRADE_NO");
    	int k = 0;
    	for (int i = 0; i < count; i++) {
			String tradeNo = parm.getValue("TRADE_NO", i);
			String orderCode = parm.getValue("ORDER_CODE", i);
			//��ȡ��ϸ������
			String sql = getSendBackSql(tradeNo, orderCode);
			dparm1 = new TParm(TJDODBTool.getInstance().select(sql));
			if(dparm1.getCount()>0){
				for (int j = 0; j < dparm1.getCount(); j++) {
					dparm2.addData("ID", dparm1.getValue("ID", j));
					dparm2.addData("TRADE_NO", dparm1.getValue("TRADE_NO", j));
					dparm2.addData("PACKAGE_CODE", dparm1.getValue("PACKAGE_CODE", j));
					dparm2.addData("SECTION_CODE", dparm1.getValue("SECTION_CODE", j));
					dparm2.addData("PACKAGE_DESC", dparm1.getValue("PACKAGE_DESC", j));
					dparm2.addData("SECTION_DESC", dparm1.getValue("SECTION_DESC", j));
					dparm2.addData("MR_NO", dparm1.getValue("MR_NO", j));
					dparm2.addData("ORDER_CODE", dparm1.getValue("ORDER_CODE", j));
					dparm2.addData("ORDER_DESC", dparm1.getValue("ORDER_DESC", j));
					dparm2.addData("ORDER_NUM", dparm1.getValue("ORDER_NUM", j));
					dparm2.addData("UNIT_CODE", dparm1.getValue("UNIT_CODE", j));
					dparm2.addData("UNIT_PRICE", dparm1.getValue("UNIT_PRICE", j));
					dparm2.addData("RETAIL_PRICE", dparm1.getValue("RETAIL_PRICE", j));
					dparm2.addData("DESCRIPTION", dparm1.getValue("DESCRIPTION", j));
					dparm2.addData("USED_FLG", dparm1.getValue("USED_FLG", j));
					dparm2.addData("SETMAIN_FLG", dparm1.getValue("SETMAIN_FLG", j));
					dparm2.addData("ORDERSET_CODE", dparm1.getValue("ORDERSET_CODE", j));
					dparm2.addData("ORDERSET_GROUP_NO", dparm1.getValue("ORDERSET_GROUP_NO", j));
					dparm2.addData("HIDE_FLG", dparm1.getValue("HIDE_FLG", j));
					
					dparm2.addData("ORDER_CAT1_CODE", dparm1.getValue("ORDER_CAT1_CODE", j));
					dparm2.addData("CTRL_FLG", dparm1.getValue("CTRL_FLG", j));
					dparm2.addData("CAT1_TYPE", dparm1.getValue("CAT1_TYPE", j));
					dparm2.addData("ORDERSET_FLG", dparm1.getValue("ORDERSET_FLG", j));
					
					result.addRowData(dparm2, k);
					k++;
				}
			}
			
		}
    	return result;
    } */
    
    /**
     * ���
     */
    public void onClear() {
    	//����������Ч
   	 	callFunction("UI|MR_NO|setEnabled", true);  //������
   	 	this.clearValue("MR_NO;PAT_NAME;INTRODUCER1;INTRODUCER2;INTRODUCER3;DISCOUNT_REASON;" +
   	 			"DISCOUNT_APPROVER;DISCOUNT_TYPE;ORIGINAL_PRICE;RETAIL_PRICE;AR_AMT;DESCRIPTION;DESCRIPTION_RE");
   	 	//��ʼ����ʱ��
   	 	Timestamp now = StringTool.getTimestamp(new Date());
   	 	this.setValue("START_DATE",
   	 			now.toString().substring(0, 10).replace('-', '/'));
   	 	this.setValue("END_DATE",
   	 			now.toString().substring(0, 10).replace('-', '/'));
   	 	//��ձ�
   	 	tradeTable.removeRowAll();
   	 	sectionTable.removeRowAll();
   	 	detailTable.removeRowAll();
   	 	paymentTool2.onClear();
   	 	paymentTool.onClear();
   		//����˷ѽ��
    	this.setValue("RE_FEE", "0.00");
    	
    	callFunction("UI|PanelCash|setVisible", true);  
		callFunction("UI|PanelCashT|setVisible", true);  
		callFunction("UI|PanelEkt|setVisible", false);  
		callFunction("UI|PanelEktT|setVisible", false); 
		this.clearValue("PAY_OTHER4_T;PAY_OTHER3_T;NO_PAY_OTHER_T;PAY_OTHER4;PAY_OTHER3;NO_PAY_OTHER;EKT_AMT;EKT_PAY_OTHER4;EKT_PAY_OTHER3;EKT_NO_PAY_OTHER;EKT_TOT_AMT");
		if(this.getValueString("BILL_TYPE").equals("E")){
			this.onReadEKT();
		}
    }
    
    /**
     * �����Żس���ѯ�¼�
     */
    public void onMrno() {
   	 	
        pat = new Pat();
        String mrno = getValue("MR_NO").toString().trim();
        if (!this.queryPat(mrno))
            return;
        pat = pat.onQueryByMrNo(mrno);
        if (pat == null || "".equals(getValueString("MR_NO"))) {
            this.messageBox_("���޲���! ");
            this.onClear(); //���
            return;
        } else {
            callFunction("UI|MR_NO|setEnabled", false); // ������
            //MR_NO = pat.getMrNo();
        }
        double payOther3 = EKTpreDebtTool.getInstance().getPayOther(pat.getMrNo(), EKTpreDebtTool.PAY_TOHER3);
		double payOther4 = EKTpreDebtTool.getInstance().getPayOther(pat.getMrNo(), EKTpreDebtTool.PAY_TOHER4);
		setValue("PAY_OTHER3", payOther3);
		setValue("PAY_OTHER4", payOther4);
		setValue("NO_PAY_OTHER", getValueDouble("EKT_CURRENT_BALANCE") - payOther3 - payOther4);

        this.setPatForUI(pat);
       
    }
    
    /**
     * ��ѯ������Ϣ
     * @param mrNo String
     * @return boolean
     */
    public boolean queryPat(String mrNo) {
        //this.setMenu(false); //MENU ��ʾ����
        pat = new Pat();
        pat = Pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            //this.setMenu(false); //MENU ��ʾ����
            this.messageBox("E0081");
            return false;
        }
        String allMrNo = PatTool.getInstance().checkMrno(mrNo);
        if (mrNo != null && !allMrNo.equals(pat.getMrNo())) {
            //============xueyf modify 20120307 start
            messageBox("������" + allMrNo + " �Ѻϲ���" + pat.getMrNo());
            //============xueyf modify 20120307 stop
        }
        																														
        return true;
    }
    
    /**
     * ������Ϣ��ֵ
     *
     * @param patInfo
     *            Pat
     */
    public void setPatForUI(Pat patInfo) {
        // ������,����
        this.setValueForParm(
                "MR_NO;PAT_NAME",patInfo.getParm());
        //���ò�ѯ����
        this.onQuery();
    }
    
    /**
     * ���ݼ��
     */
    public boolean checkData() {
    	TTextField mrNo = (TTextField)this.getComponent("MR_NO");
    	boolean flag = mrNo.isEnabled();
    	String mrNo2 = this.getValue("MR_NO").toString();
    	if(mrNo2.length()==0){
    		this.messageBox("�����Ų���Ϊ�գ�");
    		this.grabFocus("MR_NO");
    		return false;
    	}
    	if(flag){
    		this.messageBox("���벡��������");
    		this.grabFocus("MR_NO");
    		return false;
    	}
    	
    	return true;
    }
    
    /**
     * ��ѡ���ݼ��
     */
    public boolean checkSectionData() {
    	//�����Ƿ�ѡ������
    	TParm parm = sectionTable.getParmValue();
		int count = parm.getCount();
		if(count<=0){
			this.messageBox("û��ʱ���ײ����ݣ��޷��˷ѣ�");
			return false;
		}else{
			boolean flg = false;
			boolean flg2 = false;
			for (int i = 0; i < count; i++) {
				//System.out.println("exec="+parm.getBoolean("EXEC", i));
				boolean exec = parm.getBoolean("EXEC", i);
				boolean used = parm.getBoolean("USED_FLG", i);
				if(exec){
					flg = true;
					if(used){
						flg2 = true;
						String sectionDesc = parm.getValue("SECTION_DESC");
//						this.messageBox("��ʱ���ײͣ���"+sectionDesc+"��ʹ�ù����޷��˷ѣ�");
						this.messageBox("��ѡ����ײ�ʱ�̰�����ʹ�ù���ҽ�����޷��˷ѣ�");
						onClear();
						//this.clearValue("ORIGINAL_PRICE;RETAIL_PRICE;AR_AMT;SECTION_TABLE;GATHER_TYPE2");
						return false;
					}

				}
			}
			if(!flg){
				this.messageBox("û��ѡ��ʱ���ײͣ�");
				return false;
			}
		}
		//�����ײ�ʱ�̣�����ϸ��Ƿ�ʹ��
//		boolean flg2 = false;
//		for (int j = 0; j < count; j++){
//			boolean used = parm.getBoolean("USED_FLG", j);
//			if(used){
//				flg2 = true;
//			}
//			if(flg2){
//				String sectionDesc = parm.getValue("SECTION_DESC");
//				this.messageBox("��ʱ���ײͣ���"+sectionDesc+"��ʹ�ù����޷��˷ѣ�");
//				return false;
//			}
//		}
		
		return true;
    }
    
    /**
     * ����ҳ�潻����Ϣ
     */
    public void setDataForTrade(TParm parm) {
    	this.setValue("INTRODUCER1", parm.getValue("INTRODUCER1", 0));
    	this.setValue("INTRODUCER2", parm.getValue("INTRODUCER2", 0));
    	this.setValue("INTRODUCER3", parm.getValue("INTRODUCER3", 0));
    	this.setValue("DISCOUNT_REASON", parm.getValue("DISCOUNT_REASON", 0));
    	this.setValue("DISCOUNT_APPROVER", parm.getValue("DISCOUNT_APPROVER", 0));
    	this.setValue("DISCOUNT_TYPE", parm.getValue("DISCOUNT_TYPE", 0));
    	this.setValue("ORIGINAL_PRICE", parm.getValue("ORIGINAL_PRICE", 0));
    	this.setValue("RETAIL_PRICE", parm.getValue("RETAIL_PRICE", 0));
    	this.setValue("AR_AMT", parm.getValue("AR_AMT", 0));
    	this.setValue("DESCRIPTION", parm.getValue("DESCRIPTION", 0));
    	this.setValue("BILL_TYPE", parm.getValue("BILL_TYPE", 0));
    }

    
    /**
     * ϸ������¼�
     
    public void onMainTableClick(){
    	int selectedIndx=detailTable.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
    	if(detailTable.getSelectedColumn() == 0){//��һ��"ѡ"
    		detailTable.acceptText();
    		int row = detailTable.getSelectedRow();
    		TParm parm = detailTable.getParmValue();
    		parm.setData("EXEC",row, parm.getValue("EXEC", row).equals("Y")?"N":"Y");
    		detailTable.setParmValue(parm);
    		detailTable.setSelectedRow(row);
    		detailTable.acceptText();
    		
    	}
    }
    */
    
    /**
     * ��ȡ�˷ѽ�����Ϣ
     */
    public TParm getTradeParm(TParm parm) {
    	TParm result = new TParm();
    	double originalPrice = 0.00;
    	double retailPrice = 0.00;
    	double arAmt = 0.00;
    	for (int i = 0; i < parm.getCount(); i++) {
			double originalPrice2 = parm.getDouble("ORIGINAL_PRICE", i);
			double retailPrice2 = parm.getDouble("RETAIL_PRICE", i);
			double arAmt2 = parm.getDouble("AR_AMT", i);
			
			originalPrice += originalPrice2;
			retailPrice += retailPrice2;
			arAmt += arAmt2;
		}
    	//arAmt�óɸ�
    	
    	//��ȡ���׺�
    	String tradeNo = SystemTool.getInstance().getNo("ALL", "MEM", "TRADENO","TRADENO");
    	result.setData("TRADE_NO", 0, tradeNo);
    	result.setData("MR_NO", 0, this.getValue("MR_NO").toString());
    	result.setData("ORIGINAL_PRICE", 0, originalPrice);
    	result.setData("RETAIL_PRICE", 0, retailPrice);
    	result.setData("AR_AMT", 0, -arAmt);
    	result.setData("OPT_USER", 0, Operator.getID());
    	result.setData("OPT_TERM", 0, Operator.getIP());
    	result.setData("REST_FLAG", 0, 'Y');//��ʾ�˷�
    	result.setData("DESCRIPTION", 0, this.getValueString("DESCRIPTION_RE"));//��ʾ�˷�ԭ��
    	result.setData("BILL_TYPE", 0, this.getValueString("BILL_TYPE"));//֧����ʽ 
    	if(this.getValueString("BILL_TYPE").equals("E")){
    		result.setData("PAY_MEDICAL_CARD", 0, -arAmt);
    	}else{
    		result.setData("PAY_MEDICAL_CARD", 0, 0);
    	}
    	
		TParm payCountParm = new TParm(TJDODBTool.getInstance().select("SELECT COUNT(PAYTYPE) PAYTYPE FROM BIL_GATHERTYPE_PAYTYPE"));

    	//add by huangtt 20141225 start
    	for (int j = 1; j <= payCountParm.getInt("PAYTYPE", 0); j++) {
			if (j < 10) {
				result.setData("PAY_TYPE0" + j, 0 ,0);
			} else {
				result.setData("PAY_TYPE" + j, 0, 0);
			}
			result.setData("MEMO" + j, 0, "");
		}
    	result.setData("CARD_TYPE", 0, "");
    	String cardType;//�������ͺͿ��Ŵ浽һ���ֶ��� modify by huangjw 20150104
		String cardTypeKey;
		double v;
		String key;
		for(int j=0;j<= payCountParm.getInt("PAYTYPE", 0);j++){
			cardTypeKey="MEMO"+j;
			if(j<10){
				key="PAY_TYPE0"+j;
			}else{
				key="PAY_TYPE"+j;
			}
			cardType = "";
			v=0;
			for (int i = 0; i < payParm.getCount(); i++) {
				if(key.equals(payParm.getValue("PAY_TYPE", i))){
					 v=-payParm.getDouble("AMT", i);
					//int row =Integer.parseInt(payParm.getValue("PAY_TYPE", i).substring(payParm.getValue("PAY_TYPE", i).length()-2)) ;
					cardType=payParm.getValue("CARD_TYPE", i)+"#"+payParm.getValue("REMARKS", i);
					//result.setData("MEMO"+row,0, payParm.getData("REMARKS", i));
					
//					if("PAY_TYPE02".equals(payParm.getValue("PAY_TYPE", i))){
//						result.setData("CARD_TYPE", 0, payParm.getData("CARD_TYPE", i));
//					}
					break;
				}
	
			}
			result.setData(key,0,v);
			result.setData(cardTypeKey,0,cardType);
		}
		//add by huangtt 20141225 end
    	
    	System.out.println("�˷ѽ�����Ϣ��"+result);
    	return result;
    }
    
    /**
	 * �˷Ѽ۸�ͳ��
	 */
	public void PriceStatistics(){
		String sectionPrice = "";
		String arAmt = "";
		double allSectionPrice = 0.00;
		double allArAmt = 0.00;
		sectionTable.acceptText();
		TParm parm = sectionTable.getParmValue();
		int count = parm.getCount();
		//System.out.println("count="+count);
		if(count>0){
			for (int i = 0; i < count; i++) {
				boolean exec = parm.getBoolean("EXEC", i);
				double sectionPrice1 = parm.getDouble("SECTION_PRICE", i);//�ײͼ�
				double arAmt1 = parm.getDouble("AR_AMT", i);//�����
				if(exec){
					allSectionPrice += sectionPrice1;
					allArAmt += arAmt1;
				}
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		sectionPrice = df.format(allSectionPrice);
		arAmt = df.format(allArAmt);
		//this.setValue("", sectionPrice);
		this.setValue("RE_FEE", arAmt);
		if(this.getValueString("BILL_TYPE").equals("C")){
			paymentTool.setAmt(allArAmt);
		}else{
			this.setValue("EKT_AMT", df.format(this.getValueDouble("EKT_CURRENT_BALANCE")+allArAmt));
			String ektSql = "SELECT SUM(PAY_OTHER3) AS PAY_OTHER3,SUM(PAY_OTHER4) AS PAY_OTHER4 FROM EKT_TRADE WHERE RESET_TRADE_NO='"+this.getValueString("EKT_TRADE_NO")+"'  AND BUSINESS_TYPE='MEM'";
			TParm ektParm = new TParm(TJDODBTool.getInstance().select(ektSql));
//			System.out.println("r----"+ektSql);
			double payO3 = 0;
			double payO4 = 0;
			if(ektParm.getCount() > 0){
				payO3 = ektParm.getDouble("PAY_OTHER3", 0);
				payO4 = ektParm.getDouble("PAY_OTHER4", 0);
			}
			
//			this.setValue("PAY_OTHER4", df.format(this.getValueDouble("EKT_PAY_OTHER4")+payO4));
//			this.setValue("PAY_OTHER3", df.format(this.getValueDouble("EKT_PAY_OTHER3")+payO3));
//			this.setValue("NO_PAY_OTHER", df.format(this.getValueDouble("EKT_CURRENT_BALANCE")-this.getValueDouble("PAY_OTHER4")-this.getValueDouble("PAY_OTHER3")));

		}
		
	}
    
    /**
	 * �õ�ҳ����Table����
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tag) {
		return (TTable) callFunction("UI|" + tag + "|getThis");
	}
	
	/**
	 * ��ȡ��ϸ��sql
	 */
	public String getSendBackSql(String tradeNo, String orderCode) {
		String sql = "SELECT B.ORDER_CAT1_CODE,B.CTRL_FLG,B.CAT1_TYPE,B.ORDERSET_FLG,A.* " +
				" FROM MEM_PAT_PACKAGE_SECTION_D A,SYS_FEE B " +
				" WHERE A.ORDER_CODE = B.ORDER_CODE  AND A.TRADE_NO = '"+tradeNo+"' AND (A.ORDER_CODE = '"+orderCode+"' " +
				" OR A.ORDERSET_CODE = '"+orderCode	+"') AND B.ACTIVE_FLG = 'Y'  ORDER BY A.SEQ ";
		System.out.println("sql="+sql);
		return sql;
	}
	
	/**
	 * ��ҽ�ƿ�  add by sunqy 20140707
	 */
	public void onReadEKT() {
		// ��ȡҽ�ƿ�
		parmEKT = EKTIO.getInstance().TXreadEKT();
		if (null == parmEKT || parmEKT.getErrCode() < 0
				|| parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmEKT.getErrText());
			parmEKT = null;
			return;
		}
		this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
		this.setValue("EKT_CURRENT_BALANCE", parmEKT.getDouble("CURRENT_BALANCE"));
		this.setValue("EKT_AMT", parmEKT.getDouble("CURRENT_BALANCE"));
		this.onMrno();
	}
	
	/**
	 * �ײ͹���ʱ��֧����ʽ
	 * add by huangtt 20141225
	 * @param parm
	 */
	public void getMarketPackPayType(TParm parm){
		TParm result = MEMSQL.getMemType(parm);
		TParm tableParm = new TParm();
		String lock = "";
		for (int i = 0; i < result.getCount(); i++) {
			//PAY_TYPE;AMT;CARD_TYPE;REMARKS
			tableParm.addData("PAY_TYPE", result.getValue("PAYTYPE_CODE", i));
			tableParm.addData("AMT", result.getDouble("PAYTYPE_AMT", i));
			//�����ͺͿ��ŷֿ���ʾ modify by huangjw 20150112 start
			if(!"".equals(result.getValue("REMARKS", i))&&!"#".equals(result.getValue("REMARKS", i))){
				String[] str=result.getValue("REMARKS", i).split("#");
				tableParm.addData("CARD_TYPE", getCardType(str[0]));
				if(str.length>1){
					tableParm.addData("REMARKS", str[1]);	
				}else{
					tableParm.addData("REMARKS", "");	
				}
				
			}else{
				tableParm.addData("CARD_TYPE", "");
				tableParm.addData("REMARKS", "");	
			}
			//�����ͺͿ��ŷֿ���ʾ modify by huangjw 20150112 start
			lock += i + ",";
		}
		if(lock.length() > 0){
			lock = lock.substring(0, lock.length()-1);
		}
		
		paymentTool2.table.setParmValue(tableParm);
		paymentTool2.table.setLockRows(lock);
		
		
	}
	
	/**
	 * ��������add by huangjw 20150319
	 * @param str
	 */
	public String getCardType(String str){
		String cardType="";
		if(str.indexOf(";")>0){
			String [] allCardType=str.split(";");
			if(allCardType.length>0){
				TParm cardParm=new TParm(TJDODBTool.getInstance().select("SELECT id,CHN_DESC name from sys_dictionary where group_id = 'SYS_CARDTYPE' "));
				Map<String,String> map=new HashMap<String,String>();
				for(int k=0;k<cardParm.getCount();k++){
					map.put(cardParm.getValue("ID",k), cardParm.getValue("NAME",k));
				}
				for(int j=0;j<allCardType.length;j++){
					cardType=cardType+map.get(allCardType[j])+";";
				}
				cardType=cardType.substring(0,cardType.length()-1);
			}
			return cardType;
		}else{
			return str;
		}
	}
	/**
	 * ȫѡ��ť add by lich 20141107
	 */
	public void allSelect(){
		TTable table = (TTable) getComponent("SECTION_TABLE");
		int count = table.getRowCount();
		for (int i = 0; i < count; i++) {
			if("Y".equals(this.getValue("ALL_SELECT"))){
				table.setItem(i, "EXEC", "Y");
			}else if("N".equals(this.getValue("ALL_SELECT"))){
				table.setItem(i, "EXEC", "N");
			}
		}
		PriceStatistics();
	}
	
	public void onPayOther3(){	
		double payOther3 = getValueDouble("PAY_OTHER3_T");
		double payOther4 = 0;
		double payOtherTop3 = getValueDouble("EKT_PAY_OTHER3");
		double payOtherTop4 = getValueDouble("EKT_PAY_OTHER4");
		double arAmt = getValueDouble("RE_FEE");
		if(getValueDouble("PAY_OTHER4_T") == 0){
			payOther4 = arAmt - payOther3;
			setValue("PAY_OTHER4_T", df.format(payOther4) );
		}
		TParm result = EKTpreDebtTool.getInstance().checkPayOther(payOther3, payOther4, arAmt, payOtherTop3, payOtherTop4);
		if(result.getErrCode() == -3){
			setValue("PAY_OTHER4_T", df.format(payOtherTop4) );
		}
		onPayOther();
	}

	public void onPayOther4(){		
		double payOther3 = 0;
		double payOther4 = getValueDouble("PAY_OTHER4_T");
		double payOtherTop3 = getValueDouble("EKT_PAY_OTHER3");
		double payOtherTop4 = getValueDouble("EKT_PAY_OTHER4");
		double arAmt = getValueDouble("RE_FEE");
		if(getValueDouble("PAY_OTHER3_T") == 0){
			payOther3 = arAmt - payOther4;
			setValue("PAY_OTHER3_T", df.format(payOther3) );
		}
		TParm result = EKTpreDebtTool.getInstance().checkPayOther(payOther3, payOther4, arAmt, payOtherTop3, payOtherTop4);
		if(result.getErrCode() == -2){
			setValue("PAY_OTHER3_T", df.format(payOtherTop3));
		}
		onPayOther();
	}

	public boolean onPayOther(){
		double payOther3 = getValueDouble("PAY_OTHER3_T");
		double payOther4 = getValueDouble("PAY_OTHER4_T");
		double payOtherTop3 = getValueDouble("EKT_PAY_OTHER3");
		double payOtherTop4 = getValueDouble("EKT_PAY_OTHER4");
		double payCashTop = getValueDouble("EKT_NO_PAY_OTHER");
		double arAmt = getValueDouble("RE_FEE");
		double payCash = Double.parseDouble(df.format(arAmt - payOther3 - payOther4));
		TParm result = EKTpreDebtTool.getInstance().checkPayOther(payOther3, payOther4, arAmt, payOtherTop3, payOtherTop4);
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			setValue("PAY_OTHER3_T", 0);
			setValue("PAY_OTHER4_T", 0);
			return true;
		}
		if(payCash > payCashTop){
			messageBox("�˷��ֽ𳬳�֧���ֽ�");
			System.out.println(pat.getMrNo()+"-----payCash===="+payCash+"-----payCashTop==="+payCashTop);
			setValue("PAY_OTHER3_T", 0);
			setValue("PAY_OTHER4_T", 0);
			return true;
		}
		setValue("NO_PAY_OTHER_T", payCash);
		return false;
	}
	
	
}
