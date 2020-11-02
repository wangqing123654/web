package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.bil.BILCCBPatchTool;
import jdo.bil.BILInvoiceTool;
import jdo.bil.BILREGRecpTool;
import jdo.ins.INSMZConfirmTool;
import jdo.opb.OPB;
import jdo.opb.OPBReceiptTool;
import jdo.reg.PatAdmTool;
import jdo.reg.Reg;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import jdo.util.Manager;
import jdo.ins.INSOpdTJTool;
import jdo.opb.OPBTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import com.tiis.util.TiMath;

/**
 * <p>Title: ����������Ʊ</p>
 *
 * <p>Description: ����������Ʊ</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author zhangp 20120920
 * @version 1.0
 */
public class BILCCBPatchRePrintControl extends TControl{
	
	private static TTable table;
	private static String recpType = "REG";
	private static String admType = "O";
	private static boolean insFlg = false;
	private static String caseNo = "";
	private static String patName = "";
	private static String regionCode = "";
	private static String mrNo = "";
	private static Pat pat;
	
	/**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        table = (TTable)this.getComponent("TABLE");
        //�˵�tableר�õļ���
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                               "onTableComponent");
        initPage();
    }
    
    /**
     * ��ʼ����������
     */
    public void initPage() {
        setValue("REGION_CODE", Operator.getRegion());
        Timestamp today = SystemTool.getInstance().getDate();
    	String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
        String endDate = today.toString();
        endDate = endDate.substring(0, 4)+"/"+endDate.substring(5, 7)+ "/"+endDate.substring(8, 10)+ " 23:59:59";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", endDate);
        onREG();
    }
    /**
     * ��ʼ��Ʊ��
     */
    public void initBil(){
    	TParm selInvoice = new TParm();
        selInvoice.setData("STATUS", "0");
        selInvoice.setData("RECP_TYPE", recpType);
        selInvoice.setData("CASHIER_CODE", Operator.getID());
        selInvoice.setData("TERM_IP", Operator.getIP());
        TParm invoice = BILInvoiceTool.getInstance().selectNowReceipt(
                selInvoice);
        String invNo = invoice.getValue("UPDATE_NO", 0);
        if (invNo == null || invNo.length() == 0) {
            this.messageBox("���ȿ���");
            return;
        }
        setValue("UPDATE_NO", invNo);
    }
    /**
     * �Һŵ���¼�
     */
    public void onREG(){
    	recpType = "REG";
    	initBil();
    	onQuery();
    }
    
    /**
     * �շѵ���¼�
     */
    public void onOPB(){
    	recpType = "OPB";
    	initBil();
    	onQuery();
    }
    
    /**
     * �˵�table�����¼�
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable printTable = (TTable) obj;
        printTable.acceptText();
        return true;
    }
    
    /**
     * ȫѡ�¼�
     */
    public void onSelectAll() {
        String select = getValueString("SELECT");
        TParm parm = table.getParmValue();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            parm.setData("FLG", i, select);
        }
        table.setParmValue(parm);
    }
    
    /**
     * ��ѯ
     */
    public void onQuery() {
    	String startDate = "";
		String endDate = "";
		if (!"".equals(this.getValueString("START_DATE")) &&
	            !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
			startDate.substring(8, 10) + startDate.substring(11, 13) +
			startDate.substring(14, 16) + startDate.substring(17, 19);
		endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
			endDate.substring(8, 10) + endDate.substring(11, 13) +
			endDate.substring(14, 16) + endDate.substring(17, 19);
		}
    	TParm parm = new TParm();
    	parm.setData("RECP_TYPE", recpType);
    	parm.setData("CASHIER_CODE", getValue("USER"));
    	parm.setData("START_DATE", startDate);
    	parm.setData("END_DATE", endDate);
    	TParm result = BILCCBPatchTool.getInstance().queryPatchReprint(parm);
    	table.setParmValue(result);
    }
    
    /**
     * ��ӡ
     */
    public void onPrint(){
    	table.acceptText();
    	TParm tableParm = table.getParmValue();
    	if(recpType.equals("REG")){
    		for (int i = 0; i < table.getRowCount(); i++) {
				if(tableParm.getValue("FLG", i).equals("Y")){
					String caseNo = tableParm.getValue("CASE_NO", i);
					admType = tableParm.getValue("ADM_TYPE", i);
					String confirmNo = tableParm.getValue("CONFIRM_NO", i);
					TParm onREGReprintParm = new TParm();
		    		onREGReprintParm.setData("CASE_NO", caseNo);
		    		onREGReprintParm.setData("OPT_USER", Operator.getID());
		    		onREGReprintParm.setData("OPT_TERM", Operator.getIP());
		    		onREGReprintParm.setData("ADM_TYPE", admType);
		    		TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
		    				"onREGReprint", onREGReprintParm);
		    		if (result.getErrCode() < 0) {
		    			System.out.println("��ӡʧ��case_no = " + caseNo);
		    		}else{
		    			result = PatAdmTool.getInstance().getRegPringDate(caseNo, "COPY");
		    			result.setData("PRINT_NO", "TEXT", this.getValue("NEXT_NO"));
		    			TParm parm = new TParm();
		    			parm.setData("CASE_NO", caseNo);
		    			parm.setData("CONFIRM_NO", confirmNo);
		    			TParm mzConfirmParm = INSMZConfirmTool.getInstance().queryMZConfirm(
		    					parm); // �жϴ˴β����Ƿ���ҽ������
		    			if (mzConfirmParm.getErrCode() < 0) {
		    				return;
		    			}
		    			TParm printParm = null;
		    			if (mzConfirmParm.getCount() > 0) {
		    				printParm = BILREGRecpTool.getInstance().selForRePrint(caseNo);
		    				insFlg = true;
		    			}
		    			onRePrint(result, mzConfirmParm, printParm);
		    		}
				}
			}
    	}
    	if(recpType.equals("OPB")){
    		for (int i = 0; i < table.getRowCount(); i++) {
				if(tableParm.getValue("FLG", i).equals("Y")){
					if (!onSaveRePrint(i)) {
						messageBox("��ӡʧ��!");
						return;
					}
					messageBox("����ɹ�");
					TParm saveParm = table.getParmValue();
					TParm actionParm = saveParm.getRow(i);
					String receiptNo = actionParm.getValue("RECEIPT_NO");
					TParm recpParm = null;
					// �����վݵ�����:ҽ�ƿ��շѴ�Ʊ\�ֽ��շѴ�Ʊ
					recpParm = OPBReceiptTool.getInstance().getOneReceipt(receiptNo);
					caseNo = tableParm.getValue("CASE_NO", i);
					patName = tableParm.getValue("PAT_NAME", i);
					admType = tableParm.getValue("ADM_TYPE", i);
					regionCode = tableParm.getValue("REGION_CODE", i);
					mrNo = tableParm.getValue("MR_NO", i);
					pat = Pat.onQueryByMrNo(mrNo);
					onPrint(recpParm, false);
				}
    		}
    	}
    }
    
	/**
	 * Ʊ�ݲ���
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean onSaveRePrint(int row) {
		TParm saveRePrintParm = getRePrintData(row);
		if (saveRePrintParm == null)
			return false;
		// ����opbaction
		TParm result = null;
		saveRePrintParm.setData("COUNT", -1);
		result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"saveOPBRePrint", saveRePrintParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}

		return true;
	}
	/**
	 * �õ���ӡ����
	 * 
	 * @param row
	 *            int
	 * @return TParm
	 */
	public TParm getRePrintData(int row) {
		TParm saveParm = table.getParmValue();
		TParm actionParm = saveParm.getRow(row);
		actionParm.setData("OPT_USER", Operator.getID());
		actionParm.setData("OPT_TERM", Operator.getIP());
		actionParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		actionParm.setData("ADM_TYPE", saveParm.getValue("ADM_TYPE", row));
		return actionParm;
	}
    /**
	 * ��ӡ
	 * 
	 * @param parm
	 *            TParm
	 * @param mzConfirmParm
	 *            TParm
	 * @param printParm
	 *            TParm
	 */
	private void onRePrint(TParm parm, TParm mzConfirmParm, TParm printParm) {
		parm.setData("DEPT_NAME", "TEXT", parm.getValue("DEPT_CODE_OPB")
				+ "   (" + parm.getValue("CLINICROOM_DESC_OPB") + ")"); // ������������
		// ��ʾ��ʽ:����(����)
		parm.setData("CLINICTYPE_NAME", "TEXT", this.getText("CLINICTYPE_CODE")
				+ "   (" + parm.getValue("QUE_NO_OPB") + "��)"); // �ű�
		// ��ʾ��ʽ:�ű�(���)
		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyy/MM/dd"); // ������
		parm.setData("BALANCE_NAME", "TEXT", "�� ��"); // �������
		parm.setData("CURRENT_BALANCE", "TEXT", "�� " + "0.00" ); // ҽ�ƿ�ʣ����

		if (insFlg) {
			parm.setData("PAY_DEBIT", "TEXT", "ҽ��֧��:"
					+ StringTool.round(printParm.getDouble("PAY_INS_CARD", 0),
							2)); // ҽ��֧��
			parm.setData("PAY_CASH", "TEXT", "�ֽ�֧��:"
					+ StringTool.round(
							(parm.getDouble("TOTAL", "TEXT") - printParm
									.getDouble("PAY_INS_CARD", 0)), 2)); // �ֽ�
			String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SP_PRESON_TYPE' AND ID='"
					+ mzConfirmParm.getValue("SPECIAL_PAT", 0) + "'";// ҽ��������Ա�����ʾ
			TParm insPresonParm = new TParm(TJDODBTool.getInstance()
					.select(sql));
			if (insPresonParm.getErrCode() < 0) {

			} else {
				parm.setData("SPC_PERSON", "TEXT", insPresonParm.getValue(
						"CHN_DESC", 0));
			}

		}
		parm.setData("DATE", "TEXT", yMd); // ����
		parm.setData("USER_NAME", "TEXT", Operator.getID()); // �տ���
		if ("1".equals(mzConfirmParm.getValue("INS_CROWD_TYPE", 0))) {
			parm.setData("TEXT_TITLE", "TEXT", "�Ŵ������ѽ���");
			if (admType.equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
		} else if ("2".equals(mzConfirmParm.getValue("INS_CROWD_TYPE", 0))) {
			parm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			if (admType.equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
		}
//		this.openPrintDialog("%ROOT%\\config\\prt\\REG\\REGRECPPrint.jhw",
//				parm, true);
	    //parm��ȱ��RECEIPT_NO�����ĳ���ջ�Ҳʹ�ý��д�Ʊ��Ʊ���Ͻ�����ʾRECEIPT_NO��������룩
	    this.openPrintDialog(IReportTool.getInstance().getReportPath("REGRECPPrint.jhw"),
	                         IReportTool.getInstance().getReportParm("REGRECPPrint.class", parm), true);//����ϲ�modify by wanglong 20130730
	}
	/**
	 * ��ӡƱ�ݷ�װ===================pangben 20111014
	 * 
	 * @param recpParm
	 *            TParm
	 * @param flg
	 *            boolean
	 */
	private void onPrint(TParm recpParm, boolean flg) {
		DecimalFormat df = new DecimalFormat("0.00");
		TParm oneReceiptParm = new TParm();
		TParm insOpdInParm = new TParm();
		String confirmNo = "";
		String cardNo = "";
		String insCrowdType = "";
		String insPatType = "";
		// ������Ա������
		String spPatType = "";
		// ������Ա���
		String spcPerson = "";
		double startStandard = 0.00; // �𸶱�׼
		double accountPay = 0.00; // ����ʵ���ʻ�֧��
		double gbNhiPay = 0.00; // ҽ��֧��
		String reimType = ""; // �������
		double gbCashPay = 0.00; // �ֽ�֧��
		double agentAmt = 0.00; // �������
		double unreimAmt = 0.00;// ����δ�������
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("INV_NO", recpParm.getValue("PRINT_NO", 0));
		parm.setData("RECP_TYPE", "OPB");// �շ�����
		// ��ѯ�Ƿ�ҽ�� �˷�
		TParm result = INSOpdTJTool.getInstance().selectInsInvNo(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		// if (result.getCount("CASE_NO") <= 0) {// ����ҽ���˷�
		// return ;
		// }
		// ҽ����Ʊ����
		if (null != result && null != result.getValue("CONFIRM_NO", 0)
				&& result.getValue("CONFIRM_NO", 0).length() > 0) {
			parm.setData("CONFIRM_NO", result.getValue("CONFIRM_NO", 0));
			TParm mzConfirmParm = INSMZConfirmTool.getInstance()
					.queryMZConfirm(parm);
			confirmNo = result.getValue("CONFIRM_NO", 0);
			cardNo = mzConfirmParm.getValue("INSCARD_NO", 0);// ҽ������
			insOpdInParm.setData("CASE_NO", caseNo);
			insOpdInParm.setData("CONFIRM_NO", confirmNo);
			TParm insOpdParm = INSOpdTJTool.getInstance().queryForPrint(
					insOpdInParm);
			unreimAmt = insOpdParm.getDouble("UNREIM_AMT", 0);// ����δ����
			TParm insPatparm = INSOpdTJTool.getInstance().selPatDataForPrint(
					insOpdInParm);
			insCrowdType = insOpdParm.getValue("INS_CROWD_TYPE", 0); // 1.��ְ
			// 2.�Ǿ�
			insPatType = insOpdParm.getValue("INS_PAT_TYPE", 0); // 1.��ͨ
			// ������Ա������
			spPatType = insPatparm.getValue("SPECIAL_PAT", 0);
			// ������Ա���
			spcPerson = getSpPatDesc(spPatType);
			//�������
			reimType = insOpdInParm.getValue("REIM_TYPE", 0);
			// ��ְ��ͨ
			if (insCrowdType.equals("1") && insPatType.equals("1")) {
				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);

				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				if (reimType.equals("1"))

					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							;
				else

					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ARMY_AI_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							;
				gbNhiPay = TiMath.round(gbNhiPay, 2);

				gbCashPay = insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);

				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
			}
			// ��ְ����
			if (insCrowdType.equals("1") && insPatType.equals("2")) {
				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);

				if (reimType.equals("1"))
					// ҽ��֧��
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							;
				else
					// ҽ��֧��
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ARMY_AI_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							;
				gbNhiPay = TiMath.round(gbNhiPay, 2);
				// ����ʵ���ʻ�֧��
				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				// �ֽ�֧��
				gbCashPay = insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				// �������
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
			}
			// �Ǿ�����
			if (insCrowdType.equals("2") && insPatType.equals("2")) {
				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);

				// ����ʵ���ʻ�֧��
				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				if (reimType.equals("1"))
					// ҽ��֧��
					gbNhiPay = insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
							+ insOpdParm.getDouble("ARMY_AI_AMT", 0)
							+ insOpdParm.getDouble("FLG_AGENT_AMT", 0)
							//===ZHANGP 20120712 START
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							//===ZHANGP 20120712 END
							;
				else
					// ҽ��֧��
					gbNhiPay = insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
							+ insOpdParm.getDouble("FLG_AGENT_AMT", 0)
							//===ZHANGP 20120712 START
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							//===ZHANGP 20120712 END
							;

				gbNhiPay = TiMath.round(gbNhiPay, 2);
				// �ֽ�֧��
				gbCashPay = insOpdParm.getDouble("TOT_AMT", 0)
						- insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
						- insOpdParm.getDouble("FLG_AGENT_AMT", 0)
						- insOpdParm.getDouble("ARMY_AI_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				gbCashPay = TiMath.round(gbCashPay, 2);
				// �������
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
			}
		}
		// INS_CROWD_TYPE, INS_PAT_TYPE
		// Ʊ����Ϣ
		// ����
		oneReceiptParm.setData("PAT_NAME", "TEXT", patName);
		// ������Ա���
		oneReceiptParm.setData("SPC_PERSON", "TEXT",
				spcPerson.length() == 0 ? "" : spcPerson);
		// ��ᱣ�Ϻ�
		oneReceiptParm.setData("Social_NO", "TEXT", cardNo);
		// ��Ա���
		oneReceiptParm.setData("CTZ_DESC", "TEXT", "ְ��ҽ��");
		oneReceiptParm.setData("COPY", "TEXT", "(COPY)");
		// �������
		// ======zhangp 20120228 modify start
		if ("1".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "�Ŵ������ѽ���");
			if (admType.equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
			// oneReceiptParm.setData("Cost_class", "TEXT", "��ͳ");
		} else if ("2".equals(insPatType) || "3".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			if (admType.equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
			// oneReceiptParm.setData("Cost_class", "TEXT", "����");
		}
		// =====zhangp 20120228 modify end
		// ҽ�ƻ�������
		oneReceiptParm.setData("HOSP_DESC", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(regionCode));
		// �𸶽��
		oneReceiptParm.setData("START_AMT", "TEXT", df.format(startStandard));
		//===ZHANGP 20120712 START
		//����δ������ʾ����======pangben 2012-7-12
		oneReceiptParm.setData("MAX_DESC", "TEXT", unreimAmt == 0 ? "" : "����δ�������:");
		//===ZHANGP 20120712 END
		// ����޶����
		oneReceiptParm.setData("MAX_AMT", "TEXT", unreimAmt == 0 ? "--" : df
				.format(unreimAmt));
		//====zhangp 20120925 start
		//�����渶�������걨
		oneReceiptParm.setData("MAX_DESC2", "TEXT", unreimAmt == 0 ? "" : "�����渶�������걨");
		//====zhangp 20120925 end
		// �˻�֧��
		oneReceiptParm.setData("DA_AMT", "TEXT", df.format(accountPay));

		// ���úϼ�
		oneReceiptParm.setData("TOT_AMT", "TEXT", df.format(recpParm.getDouble(
				"TOT_AMT", 0)));
		// ������ʾ��д���
		oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
				.numberToWord(recpParm.getDouble("TOT_AMT", 0)));

		// ͳ��֧��
		oneReceiptParm.setData("Overall_pay", "TEXT", StringTool.round(recpParm
				.getDouble("Overall_pay", 0), 2));
		// ����֧��
		oneReceiptParm.setData("Individual_pay", "TEXT", df.format(recpParm
				.getDouble("TOT_AMT", 0)));
		// �ֽ�֧��= ҽ�ƿ����+�ֽ�+��ɫͨ��
		double payCash = StringTool.round(recpParm.getDouble("PAY_CASH", 0), 2)
				+ StringTool
						.round(recpParm.getDouble("PAY_MEDICAL_CARD", 0), 2)
				+ StringTool.round(recpParm.getDouble("PAY_OTHER1", 0), 2);
		// �ֽ�֧��
		oneReceiptParm.setData("Cash", "TEXT", gbCashPay == 0 ? payCash : df
				.format(gbCashPay));

		// �˻�֧��---ҽ�ƿ�֧��
		oneReceiptParm.setData("Recharge", "TEXT", 0.00);
		// ҽ�ƾ������
		// oneReceiptParm.setData("AGENT_AMT", "TEXT", df.format(agentAmt));
		// =====zhangp 20120229 modify start
		if (agentAmt != 0) {
			oneReceiptParm.setData("AGENT_NAME", "TEXT", "ҽ�ƾ���֧��");
			// ҽ�ƾ������
			oneReceiptParm.setData("AGENT_AMT", "TEXT", df.format(agentAmt));
		}
		oneReceiptParm.setData("MR_NO", "TEXT", mrNo);
		// =====zhangp 20120229 modify end
		// ��ӡ����
		oneReceiptParm.setData("OPT_DATE", "TEXT", StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy/MM/dd"));
		// ҽ�����
		//===zhangp 20120703 start
//		oneReceiptParm.setData("PAY_DEBIT", "TEXT", gbNhiPay == 0 ? StringTool
//				.round(recpParm.getDouble("PAY_INS_CARD", 0), 2) : df
//				.format(gbNhiPay));
		//===zhangp 20120703 end
		oneReceiptParm.setData("PAY_DEBIT", "TEXT", df
				.format(gbNhiPay));
		if (recpParm.getDouble("PAY_OTHER1", 0) > 0) {
			// ��ɫͨ�����
			oneReceiptParm.setData("GREEN_PATH", "TEXT", "��ɫͨ��֧��");
			// ��ɫͨ�����
			oneReceiptParm.setData("GREEN_AMT", "TEXT", StringTool.round(
					recpParm.getDouble("PAY_OTHER1", 0), 2));

		}
		// ҽ������
		oneReceiptParm.setData("DR_NAME", "TEXT", recpParm.getValue(
				"CASHIER_CODE", 0));

		// ��ӡ��
		oneReceiptParm.setData("OPT_USER", "TEXT", Operator.getName());
		oneReceiptParm.setData("USER_NAME", "TEXT", Operator.getID());
		oneReceiptParm.setData("TEXT_TITLE1", "TEXT", "(��������嵥)");
		// =====20120229 zhangp modify start
		if (cardNo.equals("")) {
			oneReceiptParm.setData("CARD_CODE", "TEXT", pat.getIdNo());// �������ҽ��
			// ��ʾ���֤��
		} else {
			oneReceiptParm.setData("CARD_CODE", "TEXT", cardNo);// ���� ��ʾҽ������
		}
		// =====20120229 zhangp modify end
		for (int i = 1; i <= 30; i++) {
			if (i < 10) {
				oneReceiptParm.setData("CHARGE0" + i, "TEXT", recpParm
						.getDouble("CHARGE0" + i, 0) == 0 ? "" : recpParm
						.getData("CHARGE0" + i, 0));
			} else {
				oneReceiptParm.setData("CHARGE" + i, "TEXT", recpParm
						.getDouble("CHARGE" + i, 0) == 0 ? "" : recpParm
						.getData("CHARGE" + i, 0));
			}
		}
		// =================20120219 zhangp modify start
		oneReceiptParm.setData("CHARGE01", "TEXT", df.format(recpParm
				.getDouble("CHARGE01", 0)
				+ recpParm.getDouble("CHARGE02", 0)));
		TParm dparm = new TParm();
		dparm.setData("CASE_NO", caseNo);
		dparm.setData("ADM_TYPE", admType);
		onPrintCashParm(oneReceiptParm, recpParm, dparm);
//		this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBRECTPrint.jhw",
//				oneReceiptParm, true);
		//oneReceiptParm��ȱ��RECEIPT_NO�����ĳ���ջ�Ҳʹ�ý��д�Ʊ��Ʊ���Ͻ�����ʾRECEIPT_NO��������룩
	    this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
                             IReportTool.getInstance().getReportParm("OPBRECTPrint.class", oneReceiptParm), true);//����ϲ�modify by wanglong 20130730
		return;

	}
	/**
	 * ������Ա���
	 * 
	 * @param type
	 *            String
	 * @return String
	 */
	private String getSpPatDesc(String type) {
		if (type == null || type.length() == 0 || type.equals("null"))
			return "";
		if ("04".equals(type))
			return "�˲о���";
		if ("06".equals(type))
			return "����Ա";
		if ("07".equals(type))
			return "����������Ա";
		if ("08".equals(type))
			return "�Ÿ�����";
		return "";
	}
	/**
	 * �ֽ��Ʊ��ϸ���
	 */
	private void onPrintCashParm(TParm oneReceiptParm, TParm recpParm,
			TParm dparm) {
		String receptNo = recpParm.getData("RECEIPT_NO", 0).toString();
		dparm.setData("NO", receptNo);
		TParm tableresultparm = OPBTool.getInstance().getReceiptDetail(dparm);
		if (oneReceiptParm.getCount() > 10) {
			tableresultparm.setData("DETAIL", "TEXT", "(���������ϸ��)");
		}
		oneReceiptParm.setData("TABLE", tableresultparm.getData());
	}
}
