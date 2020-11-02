package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.ekt.EKTIO;
import jdo.ekt.EKTTool;
import jdo.mem.MEMSQL;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: ��Ա��ͣ��
 * </p>
 * 
 * <p>
 * Description: ��Ա��ͣ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author huangtt 20140424
 * @version 4.5
 */
public class MEMStopCardControl extends TControl {
	private TParm parmEKT; // ҽ�ƿ���Ϣ
	private Pat pat; // ������Ϣ
	private TParm parmMEM; // ��Ա����Ϣ
	private TTable table;
	private TTable tableMem;
	private String removeFlg=""; // ͣ�����
	private int selectRow= -1;
	private String memGatherType = "";

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		table = (TTable) getComponent("TABLE");
		tableMem = (TTable) getComponent("MEM_TABLE");
		
		//֧����ʽ
		memGatherType = EKTTool.getInstance().getPayTypeDefault();
    	setValue("MEM_GATHER_TYPE", memGatherType);
	}

	/**
	 * ��ҽ�ƿ�
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
		this.onQuery();
	}

	public void onQuery() {
		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("�޴˲�����!");
			return;
		}
		String mrNo = PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
		parmMEM = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemInfoAll(mrNo)));
//		System.out.println("parmMEM==" + parmMEM);
		setValue("MR_NO", PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO"))));
		setValue("PAT_NAME", pat.getName());
		setValue("SEX_CODE", pat.getSexCode());
		setValue("FIRST_NAME", pat.getFirstName());
		setValue("LAST_NAME", pat.getLastName());
		setValue("CURRENT_ADDRESS", pat.getCurrentAddress());
		setValue("BIRTH_DATE", pat.getBirthday());
		setValue("MEM_TYPE", parmMEM.getValue("MEM_CODE", 0));
		setValue("START_DATE",
				parmMEM.getValue("START_DATE", 0).equals("") ? "" : parmMEM
						.getValue("START_DATE", 0).replace("-", "/").substring(
								0, 10));
		setValue("END_DATE", parmMEM.getValue("END_DATE", 0).equals("") ? ""
				: parmMEM.getValue("END_DATE", 0).replace("-", "/").substring(
						0, 10));

		getTable();
	}

	public void getTable() {
		// MR_NO;PAT_NAME;MEM_CODE;MEM_FEE;GATHER_TYPE;START_DATE;END_DATE;DESCRIPTION;REMOVE_FLG
		TParm parmTrade = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getRevokeMemInfo(this.getValueString("MR_NO"))));
		TParm parmRevoke = new TParm(TJDODBTool.getInstance().select(MEMSQL.getRevoke(this.getValueString("MR_NO"))));
		for(int i=0;i<parmTrade.getCount();i++){
			String tradeNo = parmTrade.getValue("TRADE_NO", i);
			for(int j=0;j<parmRevoke.getCount();j++){
				if(tradeNo.equals(parmRevoke.getValue("RETURN_TRADE_NO", j))){
					parmTrade.setData("REVOKE_FEE", i, parmRevoke.getValue("MEM_FEE", j));
					parmTrade.setData("DESCRIPTION", i, parmRevoke.getValue("DESCRIPTION", j));
					parmTrade.setData("GATHER_TYPE", i, parmRevoke.getValue("GATHER_TYPE", j));
					parmTrade.setData("STOP_CARD_DESCRIPTION", i, parmRevoke.getValue("STOP_CARD_DESCRIPTION", j));
				}
			}
		}
		table.setParmValue(parmTrade);
	}

	public void onTableClicked() {
		table.acceptText();
		int row = table.getSelectedRow();
		TParm parm = table.getParmValue();
		Timestamp date = SystemTool.getInstance().getDate();
		String endDate = parm.getValue("END_DATE", row);
		removeFlg = parm.getValue("REMOVE_FLG", row);
		String status = parm.getValue("STATUS", row);
		
		tableMem.setParmValue(MEMSQL.getMemType(parm.getRow(row)));
		
		if (endDate.equals("") || status.equals("0")) {
			this.messageBox("�ü�¼��û���ۿ���������ͣ����������ѡ��");
			return;
		}

		if (Timestamp.valueOf(endDate).before(date) && !removeFlg.equals("Y")) {
			this.messageBox("�ü�¼������Ч���ڣ�������ͣ��������ѡ��");
			return;
		}
		selectRow = row;
		if(parm.getBoolean("REMOVE_FLG", row)){
			this.setValue("STOP_REASON", parm.getValue("DESCRIPTION", row));
			this.setValue("STOP_CARD_DESCRIPTION", parm.getValue("STOP_CARD_DESCRIPTION", row));
			this.setValue("MEM_GATHER_TYPE", parm.getValue("GATHER_TYPE", row));
			this.setValue("FEEs", Math.abs(parm.getDouble("REVOKE_FEE", row)));
		}else{
			this.setValue("MEM_GATHER_TYPE", memGatherType);
			this.setValue("FEEs", 0);
			this.setValue("STOP_REASON", "");
			this.setValue("STOP_CARD_DESCRIPTION", "");
		}
		

	}
	
	

	/**
	 * ͣ������
	 */
	public void onSave() {
		if(selectRow == -1){
			this.messageBox("��ѡ��ͣ�����ݣ�");
			return;
		}
		
		
		if (removeFlg.equals("Y")) {
			this.messageBox("�ü�¼��ͣ����������ѡ��");
			return;
		}
		if (this.getValueString("MEM_GATHER_TYPE").equals("")) {
			this.messageBox("֧����ʽ������Ϊ�գ�");
			return;
		}
		
		
		TParm parm = new TParm();
		TParm tableParm = table.getParmValue();
		double feeS = this.getValueDouble("FEEs");
		double memFee =0.00;
		for (int i = 0; i < tableParm.getCount("MEM_CARD_NO"); i++) {
			if(tableParm.getValue("MEM_CARD_NO", selectRow).equals(tableParm.getValue("MEM_CARD_NO", i))){
				memFee += tableParm.getDouble("MEM_FEE", i);
			}
		}
		Timestamp dateNow = SystemTool.getInstance().getDate();
		Timestamp date =StringTool.getTimestamp(tableParm.getValue("START_DATE", selectRow), "yyyyMMddHHmmss") ;
		if(date.after(dateNow)){
			this.messageBox("��ʼʱ����ڽ��죬��ѡ����ȷ����");
			return;
		}
		if (this.getValueString("FEEs").equals("") ) {
			this.messageBox("�˻�ʣ���ѽ�����Ϊ�գ�");
			return;
		}
		if (this.getValueString("STOP_REASON").equals("")) {
			this.messageBox("ͣ��ԭ������Ϊ�գ�");
			return;
		}
		if( feeS <= 0 || feeS >  memFee){
			this.messageBox("�˻�ʣ���ѽ���ȷ");
			return;
		}
		TParm payCashParm=null;
		if("WX".equals(this.getValue("MEM_GATHER_TYPE"))||"ZFB".equals(this.getValue("MEM_GATHER_TYPE"))){
			TParm checkCashTypeParm=new TParm();
			if("WX".equals(this.getValue("MEM_GATHER_TYPE"))){
				checkCashTypeParm.setData("WX_AMT",feeS);
				checkCashTypeParm.setData("WX_FLG", "Y");
			}
			if("ZFB".equals(this.getValue("MEM_GATHER_TYPE"))){
				checkCashTypeParm.setData("ZFB_AMT", feeS);
				checkCashTypeParm.setData("ZFB_FLG", "Y");
			}
			if(null!=checkCashTypeParm.getValue("WX_FLG")&&
					checkCashTypeParm.getValue("WX_FLG").equals("Y")||null!=checkCashTypeParm.getValue("ZFB_FLG")&&
					checkCashTypeParm.getValue("ZFB_FLG").equals("Y")){
				Object result = this.openDialog(
	    	            "%ROOT%\\config\\bil\\BILPayTypeTransactionNo.x", checkCashTypeParm, false);
				if(null==result){
					return ;
				}
				payCashParm=(TParm)result;
			}
			if(null!=payCashParm){
				parm.setData("payCashParm",payCashParm.getData());
			}
		}
		TParm parmMemInfo = new TParm();
		TParm parmMemTrade = new TParm();
		parmMemInfo.setData("MR_NO", this.getValueString("MR_NO"));
		parmMemInfo.setData("END_DATE", TJDODBTool.getInstance().getDBTime());
		parmMemTrade.setData("MR_NO", this.getValueString("MR_NO"));
		parmMemTrade.setData("RETURN_USER", Operator.getID());
		parmMemTrade.setData("RETURN_DATE", TJDODBTool.getInstance().getDBTime());
		parmMemTrade.setData("OPT_USER", Operator.getID());
		parmMemTrade.setData("OPT_TERM", Operator.getIP());
		parmMemTrade.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		TTextFormat t = (TTextFormat) this.getComponent("STOP_REASON");
		parmMemTrade.setData("DESCRIPTION", t.getText());
		parmMemTrade.setData("STOP_CARD_DESCRIPTION", this.getValueString("STOP_CARD_DESCRIPTION"));
		parmMemTrade.setData("MEM_CODE", tableParm.getValue("MEM_CODE",
				selectRow));
		parmMemTrade.setData("MEM_FEE", 0 - this.getValueDouble("FEEs"));
		//===start====add by kangy	20160822====
		for(int i=1;i<11;i++){
			if(i<10){
			parmMemTrade.setData("PAY_TYPE0"+i,"");
			}else if(i>9){
				parmMemTrade.setData("PAY_TYPE"+i,"");
			}
		}
		if("C0".equals(this.getValueString("MEM_GATHER_TYPE"))){//�ֽ�
			parmMemTrade.setData("PAY_TYPE01", -feeS);
		}
		if("C1".equals(this.getValueString("MEM_GATHER_TYPE"))){//ˢ��
			parmMemTrade.setData("PAY_TYPE02", -feeS);
		}
		if("T0".equals(this.getValueString("MEM_GATHER_TYPE"))){//֧Ʊ
			parmMemTrade.setData("PAY_TYPE03", -feeS);
		}
		if("C4".equals(this.getValueString("MEM_GATHER_TYPE"))){//ҽԺ�渶
			parmMemTrade.setData("PAY_TYPE04", -feeS);
		}
		if("LPK".equals(this.getValueString("MEM_GATHER_TYPE"))){//��Ʒ��
			parmMemTrade.setData("PAY_TYPE05", -feeS);
		}
		if("XJZKQ".equals(this.getValueString("MEM_GATHER_TYPE"))){//����ȯ
			parmMemTrade.setData("PAY_TYPE06", -feeS);
		}
		if("TCJZ".equals(this.getValueString("MEM_GATHER_TYPE"))){//�ײͽ�ת
			parmMemTrade.setData("PAY_TYPE07", -feeS);
		}
		if("BXZF".equals(this.getValueString("MEM_GATHER_TYPE"))){//����֧��
			parmMemTrade.setData("PAY_TYPE08", -feeS);
		}
		if("WX".equals(this.getValueString("MEM_GATHER_TYPE"))){//΢��
			parmMemTrade.setData("PAY_TYPE09", -feeS);
		}
		if("ZFB".equals(this.getValueString("MEM_GATHER_TYPE"))){//֧����
			parmMemTrade.setData("PAY_TYPE10", -feeS);
		}
		//===end====add by kangy	20160822====
		
		
		
		parmMemTrade.setData("RETURN_TRADE_NO", tableParm.getValue("TRADE_NO",
				selectRow));
		parmMemTrade.setData("STATUS", "3");
		parmMemTrade.setData("REMOVE_FLG", "N");
		parmMemTrade.setData("TRADE_NO", getMEMTradeNo());
		parmMemTrade.setData("MEM_CARD_NO", tableParm.getValue("MEM_CARD_NO",
				selectRow));
		parmMemTrade.setData("GATHER_TYPE", this
				.getValueString("MEM_GATHER_TYPE"));

		String sDate = tableParm.getValue("START_DATE",selectRow).replace("-", "").replace("/", "").substring(0, 8)+"000000";
		String eDate = tableParm.getValue("END_DATE",selectRow).replace("-", "").replace("/", "").substring(0, 8)+"235959";
		parmMemTrade.setData("START_DATE", StringTool.getTimestamp(sDate,"yyyyMMddHHmmss"));
		parmMemTrade.setData("END_DATE", StringTool.getTimestamp(eDate,"yyyyMMddHHmmss"));
		
		parm.setData("parmMemInfo", parmMemInfo.getData());
		parm.setData("parmMemTrade", parmMemTrade.getData());
		TParm result = TIOM_AppServer.executeAction("action.mem.MEMAction",
				"stopCardMemTrade", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("ͣ��ʧ�ܣ�");
			return;
		}
		this.messageBox("ͣ���ɹ���");
		
		
		TParm parmSum = new TParm();
		parmSum.setData("MR_NO", this.getValueString("MR_NO"));
		parmSum.setData("NAME", this.getValueString("PAT_NAME"));
		parmSum.setData("GATHER_TYPE_NAME", this.getText("MEM_GATHER_TYPE"));
		parmSum.setData("BUSINESS_AMT", this.getValueDouble("FEEs"));
		parmSum.setData("SEX_TYPE", this.getValueString("SEX_CODE"));
		parmSum.setData("COPY", "");
		onPrint(parmSum);
		onQuery();
		this.clearValue("STOP_CARD_DESCRIPTION;STOP_REASON;FEEs;MEM_GATHER_TYPE");
		setValue("MEM_GATHER_TYPE", memGatherType);
		getTable();
		

	}

	/**
	 * ����ͣ��
	 */
	public void onRevoke() {
		if (!removeFlg.equals("Y")) {
			this.messageBox("�ü�¼����ͣ����¼��������ѡ��");
			return;
		}
		
		TParm parmTrade = new TParm();
		TParm tableParm = table.getParmValue();
		Timestamp dateNow = SystemTool.getInstance().getDate();
		Timestamp date =StringTool.getTimestamp(tableParm.getValue("START_DATE", selectRow), "yyyyMMddHHmmss") ;
		if(date.after(dateNow)){
			this.messageBox("��ʼʱ����ڽ��죬��ѡ����ȷ����");
			return;
		}
		
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemInfo(this.getValueString("MR_NO"))));
		if (parm1.getCount() > 0) {
			this.messageBox("�ѽ����������������ܳ���ͣ��!");
			return;
		} else {
			TParm parm2 = new TParm(TJDODBTool.getInstance().select(
					MEMSQL.getMemTrade(this.getValueString("MR_NO"))));
			if (parm2.getCount() > 0) {
				this.messageBox("�ѽ����������������ܳ���ͣ��!");
				return;
			}
		}
		//add by huangtt 20140928 start
		
		
		double memFee =0.00;
		for (int i = 0; i < tableParm.getCount("MEM_CARD_NO"); i++) {
			if(tableParm.getValue("MEM_CARD_NO", selectRow).equals(tableParm.getValue("MEM_CARD_NO", i))){
				memFee += tableParm.getDouble("REVOKE_FEE", i);
			}
		}
		double removeFee = Math.abs(memFee);
//		double removeFee = -tableParm.getDouble("REVOKE_FEE", selectRow);
		
		if(removeFee > 0){
			this.messageBox("���ջ�ͣ��ʱ���˵�"+removeFee+"Ԫ���");
		}
		//add by huangtt 20140928 end
		parmTrade.setData("MR_NO", this.getValueString("MR_NO"));
		parmTrade.setData("TRADE_NO", tableParm.getValue("TRADE_NO", selectRow));
		parmTrade.setData("MEM_CARD_NO", tableParm.getValue("MEM_CARD_NO", selectRow)); 
		String endDate = tableParm.getValue("END_DATE", selectRow).replace("-", "").substring(0, 8)+"235959";
		parmTrade.setData("END_DATE", StringTool.getTimestamp(endDate,"yyyyMMddHHmmss"));
		TParm result = TIOM_AppServer.executeAction("action.mem.MEMAction",
				"revokeCardMemTrade", parmTrade);
		if (result.getErrCode() < 0) {
			this.messageBox("����ͣ��ʧ�ܣ�");
			return;
		}
		this.messageBox("����ͣ���ɹ���");
		onQuery(); 
		this.clearValue("STOP_CARD_DESCRIPTION;STOP_REASON;FEEs;MEM_GATHER_TYPE");
		setValue("MEM_GATHER_TYPE", memGatherType);
		getTable();
	}
	
	public void onPrint(TParm parmSum){
		/**
		TParm parm = new TParm();
		 parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); //������
	       parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); //����
	       parm.setData("GATHER_NAME", "TEXT", "��"); //�տʽ
	       parm.setData("GATHER_TYPE", "TEXT", parmSum.getValue("GATHER_TYPE_NAME")); //�տʽ
	       parm.setData("AMT", "TEXT", StringTool.round(parmSum.getDouble("BUSINESS_AMT"),2)); //���
	       parm.setData("SEX_TYPE", "TEXT", parmSum.getValue("SEX_TYPE").equals("1")?"��":"Ů"); //�Ա�
	       parm.setData("AMT_AW", "TEXT", StringUtil.getInstance().numberToWord(parmSum.getDouble("BUSINESS_AMT"))); //��д���
	       String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
	               getDBTime()), "yyyy/MM/dd"); //������
	       String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
					.getInstance().getDBTime()), "HH:mm"); //ʱ����
	       parm.setData("DATE", "TEXT", yMd + "    " + hms); //����
	       parm.setData("USER_NAME", "TEXT", Operator.getID()); //�տ���
	       parm.setData("COPY", "TEXT", ""); //��ӡע��
	       parm.setData("O", "TEXT", "o"); 
	       this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEM_FEE.jhw", parm ,true);
	       */
		
		//add by sunqy 20140613 ----start----
		DecimalFormat df=new DecimalFormat("#0.00");
		TTable setTable = (TTable)callFunction("UI|TABLE|getThis");
		int selRow = setTable.getSelectedRow();
		String sql = "SELECT MEM_DESC CTZ_CODE FROM MEM_TRADE WHERE MEM_CODE = '" + 
							setTable.getValueAt(selRow, setTable.getColumnIndex("MEM_CODE")) +
							"' AND MR_NO = '"+ parmSum.getValue("MR_NO") +"' ORDER BY OPT_DATE DESC";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		result = result.getRow(0);
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT", "��Ա��ͣ���վ�");
		parm.setData("TYPE", "TEXT", ""); //���
		parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); //������
		parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); //����
		parm.setData("RecNO", "TEXT", SystemTool.getInstance().getNo("ALL", "EKT", "MEM_NO", "MEM_NO")); //Ʊ�ݺ�
		parm.setData("DEPT_CODE", "TEXT", "");// �Ʊ�
		parm.setData("MONEY", "TEXT", df.format(-parmSum.getDouble("BUSINESS_AMT"))+"Ԫ"); // ���
		parm.setData("CAPITAL", "TEXT", StringUtil.getInstance().numberToWord(-parmSum.getDouble("BUSINESS_AMT"))); // ��д���
		parm.setData("ACOUNT_NO", "TEXT", "");// �˺�
		String payType = "";
		payType += parmSum.getValue("GATHER_TYPE_NAME")+"��"+df.format(-parmSum.getDouble("BUSINESS_AMT"))+"Ԫ";// ���õ���֧����ʽ��֧�����ϲ�
		parm.setData("PAY_TYPE", "TEXT", payType);// ֧����ʽ
		parm.setData("CTZ_CODE", "TEXT", result.getData("CTZ_CODE"));// ��Ʒ
		parm.setData("REASON", "TEXT", this.getText("STOP_REASON"));// �ۿ�ԭ��
		String date = StringTool.getTimestamp(new Date()).toString().substring(
				0, 19).replace('-', '/');
		parm.setData("DATE", "TEXT", date);// ����
		parm.setData("OP_NAME", "TEXT", Operator.getID()); // �տ���
		parm.setData("RETURN", "TEXT", "��"); // ��
		parm.setData("o", "TEXT", "o");// ��
		parm.setData("COPY", "TEXT", parmSum.getValue("COPY")); // ��ӡע��
		parm.setData("HOSP_NAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalCHNFullName() : "����ҽԺ");
        parm.setData("HOSP_ENAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalENGFullName() : "ALL HOSPITALS");
//		this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMFeeReceiptV45.jhw",parm, true);
		this.openPrintDialog(IReportTool.getInstance().getReportPath("MEMFeeReceiptV45.jhw"),
				IReportTool.getInstance().getReportParm("MEMFeeReceiptV45.class", parm));//�ϲ�����
		//add by sunqy 20140613 ----end----
	}

	public void onClear() {
		this.clearValue("MR_NO;PAT_NAME;FIRST_NAME;LAST_NAME;CURRENT_ADDRESS;BIRTH_DATE;SEX_CODE;" +
				"MEM_TYPE;START_DATE;END_DATE;STOP_REASON;FEEs;MEM_GATHER_TYPE;STOP_CARD_DESCRIPTION");
		table.removeRowAll();
		tableMem.removeRowAll();
		selectRow = -1;
		parmEKT = null;
		parmMEM = null;
		pat = null;
		removeFlg="";


		//֧����ʽ
    	setValue("MEM_GATHER_TYPE", memGatherType);
	}

	/**
	 * ȡ��ԭ��:mem_trade
	 */
	public String getMEMTradeNo() {
		return SystemTool.getInstance().getNo("ALL", "EKT", "MEM_TRADE_NO",
				"MEM_TRADE_NO");
	}
	
	/**
	 * ��ӡ����====add by huangtt 20140928 
	 */
	public void onRePrint(){
		if (!removeFlg.equals("Y")) {
			this.messageBox("�ü�¼����ͣ����¼��������ѡ��");
			return;
		}
		TParm tableParm = table.getParmValue();
		String gatherType = tableParm.getValue("GATHER_TYPE", selectRow);
		String sql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'GATHER_TYPE' AND ID='"+gatherType+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		String gatherTypeDesc = result.getValue("CHN_DESC", 0);
		TParm parmSum = new TParm();
		parmSum.setData("MR_NO", this.getValueString("MR_NO"));
		parmSum.setData("NAME", this.getValueString("PAT_NAME"));
		parmSum.setData("GATHER_TYPE_NAME", gatherTypeDesc);
		parmSum.setData("BUSINESS_AMT", Math.abs(tableParm.getDouble("REVOKE_FEE", selectRow)) );
		parmSum.setData("SEX_TYPE", this.getValueString("SEX_CODE"));
		parmSum.setData("COPY", "(COPY)");
		onPrint(parmSum);
		
	}
	
}
