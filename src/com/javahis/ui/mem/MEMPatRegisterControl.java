package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import jdo.ekt.EKTIO;
import jdo.mem.MEMSQL;
import jdo.sid.IdCardO;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.device.NJSMCardDriver;
import com.javahis.device.NJSMCardYYDriver;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

/**
 *
 * <p>Title: ����ע��ҳ��</p>
 *
 * <p>Description: ����ע��ҳ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author duzhw 20140114
 * @version 4.5
 */
public class MEMPatRegisterControl extends TControl {
	Pat pat;
	//String oper = "";//����   NEW:���� ��UPDATE���޸�
	//�໤�˵�ѡ��ť
	//	TRadioButton rubtton1;
	//	TRadioButton rubtton2;

	private TParm p3; // ҽ��������
	private boolean txEKT = false;  // ̩��ҽ�ƿ�����ִ��ֱ��д������


	TParm parmPrint; //��ӡ����
	private boolean crmFlg = true; //crm�ӿڿ���
	//	private boolean ifTradeCard = false;//�Ƿ����Ԥ�쿨
	private boolean bookFlg = false;
	Timestamp birthDay=null;//��������
	TParm returnParm;
	boolean modifyFlg=false;//�޸�Ȩ��
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		//��ʼ������Ȩ��
		onInitPopemed();
//		System.out.println(Operator.getID()+"LEADER--����Ȩ��-"+this.getPopedem("LEADER"));
//		System.out.println(Operator.getID()+"MODIFY--�޸�Ȩ��-"+this.getPopedem("MODIFY"));
		if (this.getPopedem("LEADER")) { //����Ȩ��
			bookFlg = true; 
		}
		if(this.getPopedem("MODIFY")){//�޸�Ȩ��
			modifyFlg=true;
		}
		onInitAuth();//����Ȩ�����ý�����ʾ
		TTextField mrNo = (TTextField)this.getComponent("MR_NO");
		mrNo.grabFocus();
		initComponent();
		initData();
//		if(!bookFlg){
//			lockName();
//		}

	}
	/**
	 * ����Ȩ�����ý�����ʾ
	 */
	public void onInitAuth(){
		//ֻ�н���Ȩ��
		if( (! modifyFlg) && bookFlg  ){
			callFunction("UI|save|setEnabled", false); 		// ���水ť�û�
		}
		
		//ֻ���޸�Ȩ��
		if( (! bookFlg) && modifyFlg  ){
			callFunction("UI|new|setEnabled", false); 		// ������ť�û�
		}
		
		if( (! modifyFlg) && (! bookFlg)  ){
			callFunction("UI|save|setEnabled", false);
			callFunction("UI|new|setEnabled", false);
		}
	}
	
	/**
	 * ��ʼ������Ȩ��
	 */
	public void onInitPopemed(){
		TParm parm = SYSOperatorTool.getUserPopedem(Operator.getID(), getUITag());
		for (int i = 0; i < parm.getCount(); i++) {
			this.setPopedem(parm.getValue("AUTH_CODE", i), true);
		}
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initComponent(){
		callFunction("UI|save|setEnabled", false); 		// ���水ť�û�
		callFunction("UI|buycard|setEnabled", false); 	// ������ť�û�
		callFunction("UI|makecard|setEnabled", false); 	// �ƿ���ť�û�
		//    	rubtton1 = (TRadioButton) this.getComponent("RBUTTON1");
		//    	rubtton2 = (TRadioButton) this.getComponent("RBUTTON2");
		callFunction("UI|IDNO|setEnabled", false); //֤�����û�
		callFunction("UI|MEMprint|setEnabled", false); 	// ��ӡ��ť�û�
		callFunction("UI|hl|setEnabled", false); 		// ��ʷ���װ�ť�û�
		callFunction("UI|TRADE_HIS|setEnabled", false); // ��Ա��ʷ��ѯ��ť�û�
		callFunction("UI|Wrist|setEnabled", false); // ��ӡ�����ť�û�


	}
	/**
	 * ��ʼ������
	 */
	public void initData(){
		//Ԥ�쿨
		Timestamp now = StringTool.getTimestamp(new Date());
		//   	 	this.setValue("START_DATE_TRADE",
		//		 		now.toString().substring(0, 10).replace('-', '/'));// Ԥ��Ч����-Ĭ�ϵ���
		this.setValue("IN_DATE",now.toString().substring(0, 10).replace('-', '/'));//��ǰʱ��

		//����
		//this.setValue("NATION_CODE", "86");//Ĭ���й�
		//����
		//this.setValue("NATION_CODE2", "1");//Ĭ�Ϻ���
		//�쿨���� Ĭ��1
		//   	 	this.setValue("CARD_YEAR", 1);
		crmFlg = StringTool.getBoolean(TConfig.getSystemValue("crm.switch"));
		TParm parm = new TParm();

		parm.addData("ID", "");
		parm.addData("NAME", "");
		parm.addData("ID", "01");
		parm.addData("NAME", "��");
		parm.addData("ID", "02");
		parm.addData("NAME", "��");		
		parm.addData("SYSTEM", "COLUMNS", "ID");
		parm.addData("SYSTEM", "COLUMNS", "NAME");		
		parm.setCount(3);		
		TTextFormat memCombo = (TTextFormat) getComponent("BOOK_BUILD");
		try {
			memCombo.setHorizontalAlignment(2);
			memCombo.setPopupMenuHeader("����,75;����,100");
			memCombo.setPopupMenuWidth(180);
			memCombo.setPopupMenuHeight(100);
			memCombo.setFormatType("combo");
			memCombo.setShowColumnList("NAME");
			memCombo.setValueColumn("ID");
			memCombo.setPopupMenuData(parm);
		} catch (Exception e) {
			// TODO: handle exception
		}


	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		onMrno();
	}

	/**
	 * ����
	 * --�ж��޸Ļ�������--
	 * �ϣ��������ûң��޸�  δ�ûң�����
	 * �£��������û������ݣ��޸�  �û������ݣ�����
	 */
	public void onSave() {
		TParm parm = new TParm();
		TParm result = new TParm();
		String oper = "";
		TTextField mrNo = (TTextField)this.getComponent("MR_NO");
		boolean flag = mrNo.isEnabled();
		String mrNo2 = this.getValue("MR_NO").toString();
		if(!flag){
			if(mrNo2.length()==0){//û����-��������
				oper = "ADD";
			}else if(mrNo2.length()>0){//������-�޸Ĳ���
				oper = "UPDATE";
			}
		}
		//System.out.println("-------oper"+oper);
		//��ȡҳ��໤������
		//    	String deGuardian = "";
		//    	if (rubtton1.isSelected()){
		//    		deGuardian = "1";
		//    	}else if(rubtton2.isSelected()){
		//    		deGuardian = "2";
		//    	}
		//this.messageBox(oper);
		//У������
		if(!checkData(oper)){
			return;
		}

		//��ȡҳ������
		parm = getData();
		parmPrint = getData();//��ӡparm
		if(birthDay!=null && birthDay.toString().length()>0){//�����������
			parm.setData("BIRTH_DATE", birthDay.toString().substring(0,19).replace("-", "/"));
		}
		//���ݴ���
		//1:�������������-����SYS_PATINFO������MEM_PATINFO��
		if("ADD".equals(oper)){
			//    		pat = new Pat();
			//            pat = this.readModifyPat(pat);
			//            if (!pat.onNew()) {
			//                this.messageBox("E0005"); // ʧ��
			//                return ;
			//            } else {
			//            	
			//                setValue("MR_NO", pat.getMrNo());
			//                callFunction("UI|MR_NO|setEnabled", false); // ������
			//                return;
			//            }
			String newMrNo = SystemTool.getInstance().getMrNo();
			if (newMrNo == null || newMrNo.length() == 0) {
				err("-1 ȡ�����Ŵ���!");
				return;
			}
			parm.setData("MR_NO", newMrNo);
			result = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
					"newSysPatinfo", parm);
			//�ж��Ƿ����Ԥ�쿨ҵ�� �ǣ����뽻�ױ�
			boolean ifTrade = false;
			if(result.getErrCode()>=0){//д���ױ�
				if(parm.getValue("MEM_CODE").toString().length()>0 ){//�����Ͳ�Ϊ��&&����Ԥ�쿨
					String tradeNo = getMEMTradeNo();
					parm.setData("TRADE_NO", tradeNo);
					//��ȡ��Ա�����
					String memCardNo = getMemCardNo(newMrNo);
					parm.setData("MEM_CARD_NO", memCardNo);
					result = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
							"insertMemTradeData", parm);
					ifTrade = true;
				}

			}

			if(result.getErrCode()<0){
				this.messageBox("����ʧ�ܣ�");
				return;
			}else{
				setValue("MR_NO", newMrNo);
				callFunction("UI|MR_NO|setEnabled", false);  // ������
				callFunction("UI|buycard|setEnabled", true); // ������ť����Ч
				callFunction("UI|makecard|setEnabled", true); // ������ť����Ч

				//this.messageBox("����ע��ɹ���");

				//add by huangtt 20140401 CRM----start
				if(crmFlg){
					TParm parmCRM = TIOM_AppServer.executeAction("action.reg.REGCRMAction","createMember1",parm);
					if(!parmCRM.getBoolean("flg", 0)){
						this.messageBox("CRM��Ϣ����ͬ��ʧ�ܣ�");

					}
				}

				if(ifTrade){//�������Ԥ�쿨���ӡƾ��
					onPrint(newMrNo);//��ӡ��Աע��ƾ��
				}
				//add by huangtt 20140401 CRM----end
				if (JOptionPane.showConfirmDialog(null, "����ע��ɹ����Ƿ����ҽ�ƿ��ƿ���", "��Ϣ",
						JOptionPane.YES_NO_OPTION) == 0) {
					onEKTBuy();
				}
			}

		}else if("UPDATE".equals(oper)){//2:������޸�����-����MEM_PATINFO��ͬʱ����SYS_PATINFO
			TParm regParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT * FROM SYS_PATINFO WHERE MR_NO = '" + this.getValueString("MR_NO")
					+ "'"));
			//this.messageBox(""+modifyFlg);
			//parm.setData("MODIFY", modifyFlg);
			//�жϻ�Ա�����Ƿ�������-�У��޸� �ޣ�����
			TParm mrNoParm = new TParm();
			mrNoParm.addData("MR_NO", this.getValueString("MR_NO"));
			//System.out.println("�����޸�-MR_NO="+this.getValueString("MR_NO"));
			TParm memParm = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
					"onQueryMem", mrNoParm);
			//System.out.println("memParm-count="+memParm.getCount());
			if(memParm.getCount()>0){
				//�޸Ļ�Ա��Ϣ
				//System.out.println("ҳ��parm="+parm);
				result = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
						"updateMemData", parm);
				//System.out.println("result.getErrCode()="+result.getErrCode());
				//д�뽻�ױ�
				if(parm.getValue("MEM_CODE").toString().length()>0 ){//�����Ͳ�Ϊ��&&����Ԥ�쿨
					//�жϽ��ױ��Ƿ����
					//�жϽ��ױ��Ƿ��������-���ڣ��޸ġ������ڣ�����
					if(exitsMemTrade(this.getValueString("MR_NO"))){//����
						result = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
								"updateMemTrade", parm);
					}else{//�������ױ�
						//��ȡ��ǰ���׺�
						String tradeNo = getMEMTradeNo();
						//            			int tradeNo = getMaxSeq("TRADE_NO","MEM_TRADE","","","","");
						//            			System.out.println("��ǰ���׺�1��"+tradeNo);
						parm.setData("TRADE_NO", tradeNo);
						//��ȡ��Ա�����
						String memCardNo = getMemCardNo(this.getValueString("MR_NO"));
						//System.out.println("��Ա�����1������"+memCardNo);
						parm.setData("MEM_CARD_NO", memCardNo);
						result = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
								"insertMemTradeData", parm);
					}

				}
				if(result.getErrCode()<0){
					this.messageBox("����ʧ�ܣ�");

				}
			}else{
				//��Ա��ϢУ��
				//    			if(!checkMemData()){
				//    	    		return;
				//    	    	}
				//������Ա��Ϣ
				result = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
						"insertMemData", parm);

				//д�뽻�ױ�
				if(parm.getValue("MEM_CODE").toString().length()>0){
					//�жϽ��ױ��Ƿ��������-���ڣ��޸ġ������ڣ�����
					if(exitsMemTrade(this.getValueString("MR_NO"))){//����
						result = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
								"updateMemTrade", parm);
					}else{//�������ױ�
						//��ȡ��ǰ���׺�
						String tradeNo = getMEMTradeNo();
						//            			int tradeNo = getMaxSeq("TRADE_NO","MEM_TRADE","","","","");
						//            			System.out.println("��ǰ���׺�2��"+tradeNo);
						parm.setData("TRADE_NO", tradeNo);
						//��ȡ��Ա�����
						String memCardNo = getMemCardNo(this.getValueString("MR_NO"));
						//System.out.println("��Ա�����2������"+memCardNo);
						parm.setData("MEM_CARD_NO", memCardNo);
						//����������Ϣ
						result = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
								"insertMemTradeData", parm);
					}

				}


				if(result.getErrCode()<0){
					this.messageBox("����ʧ�ܣ�");
				}
			}
			//����ɹ�
			if(result.getErrCode()>=0){
				this.messageBox("����ɹ���");
				writeLog(mrNoParm.getValue("MR_NO",0), regParm.getRow(0), "UPDATE");
				//add by huangtt 20140401 CRM----start
				if(crmFlg){
					System.out.println("CRM��Ϣ����ͬ��==="+parm);
					TParm memTrade =new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemTradeCrm(getValueString("MR_NO")))); 
					parm.setData("MEM_CODE", memTrade.getValue("MEM_CODE", 0));
					parm.setData("REASON", memTrade.getValue("REASON", 0));
					parm.setData("START_DATE_TRADE", "".equals(memTrade.getValue("START_DATE", 0))? "": memTrade.getValue("START_DATE", 0).substring(0, 10));
					parm.setData("END_DATE_TRADE", "".equals(memTrade.getValue("END_DATE", 0))? "": memTrade.getValue("END_DATE", 0).substring(0, 10));
					parm.setData("MEM_FEE", memTrade.getValue("MEM_FEE", 0));
					parm.setData("INTRODUCER1", memTrade.getValue("INTRODUCER1", 0));
					parm.setData("INTRODUCER2", memTrade.getValue("INTRODUCER2", 0));
					parm.setData("INTRODUCER3", memTrade.getValue("INTRODUCER3", 0));
					parm.setData("DESCRIPTION", memTrade.getValue("DESCRIPTION", 0));
					TParm parmCRM = TIOM_AppServer.executeAction("action.reg.REGCRMAction","updateMemberByMrNo1",parm);
					if(!parmCRM.getBoolean("flg", 0)){
						this.messageBox("CRM��Ϣ����ͬ��ʧ�ܣ�");
					}
				}

				//add by huangtt 20140401 CRM----end
				String sql = "select CARD_NO from EKT_ISSUELOG where mr_no = '"
						+ parm.getValue("MR_NO") + "'";
				TParm ekt = new TParm(TJDODBTool.getInstance().select(sql));
				if(ekt.getCount()< 0){
					if (messageBox("��ʾ", "�ò���δ����ҽ�ƿ�,�Ƿ����ҽ�ƿ�", 0) == 0) {
						onEKTBuy(); // �ƿ�
						// ====zhangp 20120227 modify start
					}
				}
			}

		}
		if(returnParm!=null){//���²��������� �������أ�������
			String sql="UPDATE SYS_PATINFO SET GESTATIONAL_WEEKS='"+returnParm.getValue("GESTATIONAL_WEEKS")+"', " +
					" NEW_BODY_WEIGHT='"+returnParm.getValue("NEW_BODY_WEIGHT")+"'," +
					" NEW_BODY_HEIGHT='"+returnParm.getValue("NEW_BODY_HEIGHT")+"'" +
					" WHERE MR_NO='"+this.getValueString("MR_NO")+"'";
			System.out.println("parm:::"+sql);
			new TParm(TJDODBTool.getInstance().update(sql));
		}
	}

	/**
	 * ��������
	 */
	public void onNew() {
		onClear();
		callFunction("UI|MR_NO|setEnabled", false); 	// �������û�
		callFunction("UI|new|setEnabled", false); 		// ������ť�û�
		callFunction("UI|save|setEnabled", true); 		// ���水ť�ÿ���
		callFunction("UI|buycard|setEnabled", false);   // ������ť�û�
		callFunction("UI|makecard|setEnabled", false);   // ������ť�û�
		this.setValue("BOOK_BUILD", "01");  //add by huangtt 20140928
	}

	/**
	 * ��������
	 */
	public void onBuy() {
		TParm parm = new TParm();
		//��ȡҳ������
		parm = getData();
		TParm reParm = (TParm)this.openDialog(
				"%ROOT%\\config\\mem\\MEMMarketCard.x", parm);
		String mr_no = this.getValueString("MR_NO");
		this.onClear();
		this.setValue("MR_NO", mr_no);
		this.onMrno();
	}

	/**
	 * ��ѯ���MEM_FEE

    public void queryFee(){
    	//String menFee = "";
    	String MemCode = this.getValueString("MEM_CODE");
    	//System.out.println("MemCode="+MemCode);
    	if(MemCode.length()>0){
    		String sql = "SELECT MEM_FEE FROM MEM_MEMBERSHIP_INFO WHERE MEM_CODE = '"+MemCode+"'";
    		//System.out.println("sql="+sql);
    		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    		//System.out.println("--"+parm.getValue("MEM_FEE", 0));
    		if(parm.getCount("MEM_FEE")>0){
    			this.setValue("MEM_FEE", parm.getValue("MEM_FEE", 0));
    		}
    	}
    } */

	/**
	 * ���
	 */
	public void onClear() {
		callFunction("UI|MR_NO|setEnabled", true); 	  // �����ſ���
		callFunction("UI|new|setEnabled", true); 	  // ������ť�ÿ���
		callFunction("UI|save|setEnabled", false); 	  // ���水ť�û�
		callFunction("UI|buycard|setEnabled", false); // ������ť�û�
		callFunction("UI|makecard|setEnabled", false); // ������ť�û�

		callFunction("UI|IDNO|setEnabled", false); //֤�����û�
		callFunction("UI|MEMprint|setEnabled", false); 	// ��ӡ��ť�û�
		callFunction("UI|hl|setEnabled", false); 		// ��ʷ���װ�ť�û�
		callFunction("UI|TRADE_HIS|setEnabled", false); // ��Ա��ʷ��ѯ��ť�û�
		callFunction("UI|Wrist|setEnabled", false); // ��ӡ�����ť�û�

		this.clearValue("MR_NO;PAT_NAME;PAT_NAME1;LAST_NAME;FIRST_NAME;PY1;ID_TYPE;IDNO;OLD_NAME;SEX_CODE;BIRTH_DATE;AGE;" +
				"NATION_CODE;NATION_CODE2;RELIGION;MARRIAGE;ADDRESS;CURRENT_ADDRESS;POST_CODE;" +
				"RESID_ADDRESS;RESID_POST_CODE;HOMEPLACE_CODE;BIRTH_HOSPITAL;CELL_PHONE;SCHOOL_NAME;SCHOOL_TEL;" +
				"TEL_HOME;E_MAIL;SOURCE;INSURANCE_COMPANY1_CODE;INSURANCE_NUMBER1;INSURANCE_COMPANY2_CODE;INSURANCE_NUMBER2;" +
				"GUARDIAN1_NAME;GUARDIAN1_RELATION;GUARDIAN1_TEL;GUARDIAN1_PHONE;GUARDIAN1_COM;GUARDIAN1_ID_TYPE;GUARDIAN1_ID_CODE;GUARDIAN1_EMAIL;" +
				"GUARDIAN2_NAME;GUARDIAN2_RELATION;GUARDIAN2_TEL;GUARDIAN2_PHONE;GUARDIAN2_COM;GUARDIAN2_ID_TYPE;GUARDIAN2_ID_CODE;GUARDIAN2_EMAIL;" +
				"CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;SPECIAL_DIET;MEM_TYPE;FAMILY_DOCTOR;ACCOUNT_MANAGER_CODE;START_DATE;END_DATE;BUY_MONTH_AGE;HAPPEN_MONTH_AGE;" +
				"MEM_CODE;END_DATE_TRADE;MEM_FEE;INTRODUCER1;INTRODUCER2;INTRODUCER3;DESCRIPTION;START_DATE_TRADE;COMPANY_DESC;BOOK_BUILD;REMARKS;CUSTOMER_SOURCE;INCOME;GUARDIAN1_AGE;GUARDIAN2_AGE");
		//����
		//this.setValue("NATION_CODE", "86");//Ĭ���й�
		//����
		//this.setValue("NATION_CODE2", "1");//Ĭ�Ϻ���
		//Ԥ�쿨
		//    	Timestamp now = StringTool.getTimestamp(new Date());
		//   	 	this.setValue("START_DATE_TRADE",
		//		 		now.toString().substring(0, 10).replace('-', '/'));// Ԥ��Ч����-Ĭ�ϵ���
		birthDay=null;//add by huangjw 20150423
//		if(!bookFlg){
//			lockName();
//		}
		this.onInitAuth();
	}

	/**
	 * У��ҳ������
	 */
	public boolean checkData(String oper) {
		boolean flg = true;
		//У���Ƿ������ֵ,��������ʾ��Ϣ
		if("UPDATE".equals(oper)){
			if (getValue("MR_NO") == null || getValue("MR_NO").toString().length()<=0) {
				this.messageBox("�����Ų���Ϊ��!");
				this.grabFocus("MR_NO");
				flg = false;
				return flg;
			}
		}

		//add by huangtt 20140320
		if(getValueString("PAT_NAME").equals("")){
			if(getValueString("FIRST_NAME").equals("") && !getValueString("LAST_NAME").equals("")){
				this.messageBox("������firstName!");
				this.grabFocus("FIRST_NAME");
				flg = false;
				return flg;
			}else if(!getValueString("FIRST_NAME").equals("") && getValueString("LAST_NAME").equals("")){
				this.messageBox("������lastName!");
				this.grabFocus("LAST_NAME");
				flg = false;
				return flg;
			}else if(!getValueString("FIRST_NAME").equals("") && !getValueString("LAST_NAME").equals("")){
				this.setValue("PAT_NAME1", getValueString("FIRST_NAME")+" "+getValueString("LAST_NAME"));
				if(this.messageBox("������ֵ", "�Ƿ�firstName��lastName�ϲ���ֵ��������", 0) != 0) {
					this.messageBox("��������Ϊ��!");
					flg = false;
					return flg;
				} 
				this.setValue("PAT_NAME", getValueString("FIRST_NAME")+" "+getValueString("LAST_NAME"));

			}else if(getValueString("FIRST_NAME").equals("") && getValueString("LAST_NAME").equals("")){
				//if(this.modifyFlg || oper.equals("ADD")){//�С�MODIFY��Ȩ����У�飬����У��
				this.messageBox("��������Ϊ��!");
				this.grabFocus("PAT_NAME");
				flg = false;
				return flg;
				//}
		}
			
			
			
			
		}else{
			if(getValueString("FIRST_NAME").equals("") && !getValueString("LAST_NAME").equals("")){
				this.messageBox("������firstName!");
				this.grabFocus("FIRST_NAME");
				flg = false;
				return flg;
			}else if(!getValueString("FIRST_NAME").equals("") && getValueString("LAST_NAME").equals("")){
				this.messageBox("������lastName!");
				this.grabFocus("LAST_NAME");
				flg = false;
				return flg;
			}else if(!getValueString("FIRST_NAME").equals("") && !getValueString("LAST_NAME").equals("")){
				this.setValue("PAT_NAME1", getValueString("FIRST_NAME")+" "+getValueString("LAST_NAME"));	
			}
		}

		if (getValue("ID_TYPE") == null || getValue("ID_TYPE").toString().length()<=0) {
			this.messageBox("֤�����Ͳ���Ϊ��!");
			this.grabFocus("ID_TYPE");
			flg = false;
			return flg;
		}
		if(!getValue("ID_TYPE").toString().equals("99")){
			if (getValue("IDNO") == null || getValue("IDNO").toString().length()<=0) {
				this.messageBox("֤���Ų���Ϊ��!");
				this.grabFocus("IDNO");
				flg = false;
				return flg;
			}
		}
		
		
		
		//-------------modefied by wangqing at 2016.11.16 start-----------------
		//�޸ķ�����ע������  ������λ��Ϊ�Ǳ�ѡ��
		
		//        //������λ�Ǳ�������û���ɻ�����"��"   sunqy  20140509
		//    	if(getValue("COMPANY_DESC") == null || getValue("COMPANY_DESC").toString().length()<=0){
		//    		this.messageBox("������λ�Ǳ�����,���û����'��'");
		//    		flg = false;
		//    		return flg;
		//    	}
		//-------------modefied by wangqing at 2016.11.16 end-----------------
		
		//���һУ��      add by sunqy 20140512
		if(getValue("CTZ1_CODE") == null || getValue("CTZ1_CODE").toString().length()<=0){
			this.messageBox("���һ����Ϊ��!");
			flg = false;
			return flg;
		}
		//֤�����������֤ʱ��֤������Ҫ�������֤��У���ʽ
		if(getValue("ID_TYPE").toString().equals("01")){
			if(!isCard(getValue("IDNO").toString())){
				this.messageBox("¼������֤�Ų�����Ҫ��");
				this.grabFocus("IDNO");
				flg = false;
				return flg;
			}
		}
		//if(this.modifyFlg || oper.equals("ADD")){//�С�MODIFY��Ȩ����У�飬����У��
		if (getValue("SEX_CODE") == null || getValue("SEX_CODE").toString().length()<=0) {
			this.messageBox("�Ա���Ϊ��!");
			this.grabFocus("SEX_CODE");
			flg = false;
			return flg;
		}
		//}
		//if(this.modifyFlg || oper.equals("ADD")){//�С�MODIFY��Ȩ����У�飬����У��
		if (getValue("BIRTH_DATE") == null || getValue("BIRTH_DATE").toString().length()<=0) {
			this.messageBox("�������ڲ���Ϊ��!");
			this.grabFocus("BIRTH_DATE");
			flg = false;
			return flg;
		}else{
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
			String sTime = getValueString("BIRTH_DATE").substring(0,10);
			  try {
				Date bt=sdf.parse(sTime);
				Date now = new Date();
		        if(bt.after(now)){
		        	this.messageBox("�������ڲ��ܳ�����ǰ����!");
		        	this.grabFocus("BIRTH_DATE");
					flg = false;
					return flg;
		        }
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		//}
		if (getValue("NATION_CODE") == null || getValue("NATION_CODE").toString().length()<=0) {
			this.messageBox("��������Ϊ��!");
			this.grabFocus("NATION_CODE");
			flg = false;
			return flg;
		}
		if (getValue("NATION_CODE2") == null || getValue("NATION_CODE2").toString().length()<=0) {
			this.messageBox("���岻��Ϊ��!");
			this.grabFocus("NATION_CODE2");
			flg = false;
			return flg;
		}
		if (getValue("MARRIAGE") == null || getValue("MARRIAGE").toString().length()<=0) {
			this.messageBox("��������Ϊ��!");
			this.grabFocus("MARRIAGE");
			flg = false;
			return flg;
		}
		//        //������ַ������
		//        if(getValue("RESID_POST_CODE") == null || getValue("RESID_POST_CODE").toString().length()<=0){
		//        	this.messageBox("������ַ����Ϊ��!");
		//        	this.grabFocus("RESID_POST_CODE");
		//        	flg = false;
		//            return flg;
		//        }
		//        //������ַ-��ϸ��ַ
		//        if(getValue("RESID_ADDRESS") == null || getValue("RESID_ADDRESS").toString().length()<=0){
		//        	this.messageBox("������ַ-��ϸ��ַ����Ϊ��!");
		//        	this.grabFocus("RESID_ADDRESS");
		//        	flg = false;
		//            return flg;
		//        }
		//��סַ������
		if(getValue("POST_CODE") == null || getValue("POST_CODE").toString().length()<=0){
			this.messageBox("��סַ����Ϊ��!");
			this.grabFocus("POST_CODE");
			flg = false;
			return flg;
		}
		//��סַ-��ϸ��ַ
		if (getValue("CURRENT_ADDRESS") == null || getValue("CURRENT_ADDRESS").toString().length()<=0) {
			this.messageBox("��סַ-��ϸ��ַ����Ϊ��!");
			this.grabFocus("CURRENT_ADDRESS");
			flg = false;
			return flg;
		}
		//        //���տ���1���ܳ���50
		//        if(this.getValueString("INSURANCE_NUMBER1").length()>0){
		//        	if(this.getValueString("INSURANCE_NUMBER1").length()>50){
		//        		this.messageBox("���տ���1���ܳ���50λ��");
		//        		this.grabFocus("INSURANCE_NUMBER1");
		//                flg = false;
		//                return flg;
		//        	}
		//        }
		//        //���տ���2���ܳ���50
		//        if(this.getValueString("INSURANCE_NUMBER2").length()>0){
		//        	if(this.getValueString("INSURANCE_NUMBER2").length()>50){
		//        		this.messageBox("���տ���2���ܳ���50λ��");
		//        		this.grabFocus("INSURANCE_NUMBER2");
		//                flg = false;
		//                return flg;
		//        	}
		//        }
		//�ֻ�
		if (getValue("CELL_PHONE") == null || getValue("CELL_PHONE").toString().length()<=0) {
			this.messageBox("�ֻ�����Ϊ��!");
			this.grabFocus("CELL_PHONE");
			flg = false;
			return flg;
		}
		if(this.getValueString("CELL_PHONE").length()>0){

			if(!isNumeric(this.getValueString("CELL_PHONE"))){
				this.messageBox("����д��ȷ���ֻ���");
				this.grabFocus("CELL_PHONE");
				flg = false;
				return flg;
			}
			if(this.getValueString("CELL_PHONE").length() != 11){
				this.messageBox("����д��ȷ���ֻ���");
				this.grabFocus("CELL_PHONE");
				flg = false;
				return flg;
			}
		}
		//����
		//        if (getValue("TEL_HOME") == null || getValue("TEL_HOME").toString().length()<=0) {
		//            this.messageBox("��������Ϊ��!");
		//            this.grabFocus("TEL_HOME");
		//            flg = false;
		//            return flg;
		//        }
		//�໤��У��
		if (getValue("GUARDIAN1_NAME") == null || getValue("GUARDIAN1_NAME").toString().length()<=0) {
			this.messageBox("�໤��1��������Ϊ��!");
			this.grabFocus("GUARDIAN1_NAME");
			flg = false;
			return flg;
		}
		if (getValue("GUARDIAN1_RELATION") == null || getValue("GUARDIAN1_RELATION").toString().length()<=0) {
			this.messageBox("�໤��1��ϵ����Ϊ��!");
			this.grabFocus("GUARDIAN1_RELATION");
			flg = false;
			return flg;
		}
		if (getValue("GUARDIAN1_PHONE") == null || getValue("GUARDIAN1_PHONE").toString().length()<=0) {
			this.messageBox("�໤��1�ֻ�����Ϊ��!");
			this.grabFocus("GUARDIAN1_PHONE");
			flg = false;
			return flg;
		}
		if (this.getValueString("GUARDIAN1_PHONE").length() > 0) {

			if (!isNumeric(this.getValueString("GUARDIAN1_PHONE"))) {
				this.messageBox("����д��ȷ���ֻ���");
				this.grabFocus("GUARDIAN1_PHONE");
				flg = false;
				return flg;
			}
			if (this.getValueString("GUARDIAN1_PHONE").length() != 11) {
				this.messageBox("����д��ȷ���ֻ���");
				this.grabFocus("GUARDIAN1_PHONE");
				flg = false;
				return flg;
			}
		}

		if (getValue("SOURCE") == null || getValue("SOURCE").toString().length()<=0) {
			this.messageBox("��֪��ʽ����Ϊ��!");
			this.grabFocus("SOURCE");
			flg = false;
			return flg;
		}


		if (getValue("BOOK_BUILD") == null || getValue("BOOK_BUILD").toString().length()<=0) {
			this.messageBox("����¼�벻��Ϊ��!");
			this.grabFocus("BOOK_BUILD");
			flg = false;
			return flg;
		}

		//add by huangtt 20140425
		TTextFormat t = this.getTextFormat("REASON");
		String reason =t.getText();
		if(reason.equals("����")){
			if(!this.getValueString("MEM_TYPE").equals(this.getValueString("MEM_TYPE1"))){
				this.messageBox("��Ա����뷢�����Ͳ�һ�£�������������");
				flg = false;
				return flg;
			}
		}

		//Ԥ�쿨���У��
		//        if(this.getValue("MEM_CODE").toString().length() > 0){
		//        	flg = onQueryFee();
		//        }

		return flg;
	}
	//    /**
	//     * ��Ա��ϢУ��
	//     */
	//    public boolean checkMemData() {
	//    	boolean flg = true;
	//    	//У���Ƿ������ֵ,��������ʾ��Ϣ
	//    	if (getValue("MEM_TYPE") == null || getValue("MEM_TYPE").toString().length()<=0) {
	//            this.messageBox("��Ա��ݲ���Ϊ��!");
	//            this.grabFocus("MEM_TYPE");
	//            flg = false;
	//            return flg;
	//        }
	//    	if (getValue("MEM_CODE") == null || getValue("MEM_CODE").toString().length()<=0) {
	//            this.messageBox("�������Ͳ���Ϊ��!");
	//            this.grabFocus("MEM_CODE");
	//            flg = false;
	//            return flg;
	//        }
	//    	
	//    	return flg;
	//    }

	/**
	 * �����Żس���ѯ�¼�
	 */
	public void onMrno() {

		boolean flg = false;
		//SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		//String date = df.format(SystemTool.getInstance().getDate());
		//setValue("IN_DATE", StringTool.getTimestamp(date, "yyyyMMddHH")); // ��������
		pat = new Pat();
		String mrno = getValue("MR_NO").toString().trim();
		if (!this.queryPat(mrno))
			return;
		pat = Pat.onQueryByMrNo(mrno);
		//System.out.println("pat="+pat.getParm());
		if (pat == null || "".equals(getValueString("MR_NO"))) {
			this.messageBox_("���޲���! ");
			this.onClear(); // ���
			return;
		} else {
			callFunction("UI|save|setEnabled", true); 		// ���水ť�ÿ���
			callFunction("UI|buycard|setEnabled", true);	// ������ť�ÿ���
			callFunction("UI|makecard|setEnabled", true);	// ������ť�ÿ���
			callFunction("UI|MR_NO|setEnabled", false); 	// ������
			callFunction("UI|IDNO|setEnabled", true); 		//֤��������Ч
			callFunction("UI|MEMprint|setEnabled", true); 	// ��ӡ��ť����Ч
			callFunction("UI|hl|setEnabled", true); 		// ��ʷ���װ�ť����Ч
			callFunction("UI|TRADE_HIS|setEnabled", true);  // ��Ա��ʷ��ѯ��ť����Ч
			callFunction("UI|Wrist|setEnabled", true); 		// ��ӡ�����ť����Ч
			flg = true;
			//MR_NO = pat.getMrNo();
//			if(!bookFlg){
//				lockName();
//			}
			this.onInitAuth();
		}
		this.setPatForUI(pat);

		if(flg){
			//��ѯMEM_PATINFO������
			TParm mrNoParm = new TParm();
			mrNoParm.addData("MR_NO", this.getValueString("MR_NO"));
			TParm memParm = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
					"onQueryMem", mrNoParm);
			//        	System.out.println("��Ա��Ϣ��"+memParm);
			//        	System.out.println("������"+memParm.getCount());
			if(memParm.getCount()>0){
				//������set��ҳ��
				this.setMemPatinfoForUI(memParm);
				//��ѯ��Ա���ױ�����-Ԥ�쿨��Ϣ
				TParm memTradeParm = TIOM_AppServer.executeAction("action.mem.MEMPatRegisterAction",
						"onQueryTrade", mrNoParm);
				if(memTradeParm.getCount()>0){
					//������set��ҳ��
					this.setMemTradeForUI(memTradeParm);
				}
			}
		}

		if(!modifyFlg){//û���޸�Ȩ��ʱ�����水ťҪ�û� add by huangjw 20150727
			TMenuItem save=(TMenuItem) this.getComponent("save");
			save.setEnabled(false);
		}
	}
	/**
	 * ��ȡ�������ڣ���ȷ��ʱ���룩
	 * @param mrNO
	 */
	public Timestamp onQueryBirthDateByMrNO(String mrNO){
		String sql="SELECT BIRTH_DATE FROM SYS_PATINFO WHERE MR_NO='"+mrNO+"'";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		Timestamp birth=parm.getTimestamp("BIRTH_DATE",0);
		return birth;
	}
	/**
	 * ��ѯ������Ϣ
	 * 
	 * @param mrNo
	 *            String
	 */
	public void onQueryNO(String mrNo) {
		if (pat != null)
			PatTool.getInstance().unLockPat(pat.getMrNo());

		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.messageBox("�޴˲�����!");
			return;
		}
		setValue("MR_NO", mrNo);
		setValue("PAT_NAME", pat.getName());
		setValue("PAT_NAME1", pat.getName1());
		setValue("PY1", pat.getPy1());
		setValue("IDNO", pat.getIdNo());
		//setValue("FOREIGNER_FLG", pat.isForeignerFlg());
		setValue("BIRTH_DATE", pat.getBirthday());
		setValue("SEX_CODE", pat.getSexCode());
		setValue("TEL_HOME", pat.getTelHome());
		setValue("POST_CODE", pat.getPostCode());
		//onPost();
		setValue("ADDRESS", pat.getAddress());
		setValue("CTZ1_CODE", pat.getCtz1Code());
		//setValue("REG_CTZ1", getValue("CTZ1_CODE"));
		setValue("CTZ2_CODE", pat.getCtz2Code());
		//setValue("REG_CTZ2", getValue("CTZ2_CODE"));
		setValue("CTZ3_CODE", pat.getCtz3Code());
		// setValue("REG_CTZ3", getValue("CTZ3_CODE"));
		//String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
		onMrno();

	}
	/**
	 * ���֤�ŵõ�������-���֤�Żس��¼�--��ʱ�ֶ�¼��
	 */
	public void onIdNo() {
		String homeCode = "";
		String idNo = this.getValueString("IDNO");
		homeCode = StringUtil.getIdNoToHomeCode(idNo);//������
		setValue("HOMEPLACE_CODE", homeCode);
	}

	/**
	 * �������ͺŲ���
	 * ���֤�ŵõ�������-���֤�Żس��¼�--��ʱ�ֶ�¼��
	 */
	public void onBirthday() {
		try{
			String idNo = this.getValueString("IDNO");
			if(getValue("ID_TYPE").toString().equals("01")){//֤������Ϊ���֤
				if(idNo.length()>0){
					if(!isCard(idNo)){
						this.messageBox("¼������֤�Ų�����Ҫ��");
						return;
					}else{
						if(idNo.length()>14){
							String time = idNo.substring(6, 14);
							String time2 = time.substring(0, 4)+"/"+time.substring(4, 6)+"/"+time.substring(6, 8);
							//System.out.println("time2="+time2);
							//��ʾ��������
							//if(getValue("BIRTH_DATE")==null){//�������������ֵ�򲻸���
							this.setValue("BIRTH_DATE", time2);
							//��������
							setBirth(true);
							//}
							String sexCode = idNo.substring(idNo.length()-2);
							sexCode = sexCode.substring(0, 1);
							//System.out.println("�Ա�"+sexCode);
							//�����Ա�
							setSexCode(sexCode);


						}

					}
				}


			}
			//��Ӽ���Ƿ�����ͬ����
			String selPat = "SELECT   A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
					+ " POST_CODE, RESID_ADDRESS,CURRENT_ADDRESS, A.MR_NO,ID_TYPE "
					+ " FROM SYS_PATINFO A "
					+ " WHERE A.IDNO = '"
					+ idNo
					+ "'  "
					+ " ORDER BY A.OPT_DATE";
			// ===zhangp 20120319 end
			TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
			if (same.getErrCode() != 0) {
				this.messageBox_(same.getErrText());
			}
			// ѡ�񲡻���Ϣ
			if (same.getCount("MR_NO") > 0) {
				//			int sameCount = this.messageBox("��ʾ��Ϣ", "������֤ͬ������Ĳ�����Ϣ,�Ƿ�������������Ϣ", 0);
				//add by huangtt 20140926 start
				Object[] possibilities = { "  ��   ", "  ��   " };
				int sameCount = JOptionPane.showOptionDialog(null,
						"������֤ͬ������Ĳ�����Ϣ,�Ƿ�������������Ϣ", "��ʾ��Ϣ",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
						null, possibilities, possibilities[1]);
				//add by huangtt 20140926 end
				if (sameCount != 1) {
					this.grabFocus("SEX_CODE");
					return;
				}
				Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
					onQueryNO(patParm.getValue("MR_NO"));
					//onMrno();
					this.grabFocus("SEX_CODE");
					return;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			this.grabFocus("SEX_CODE");
		}


	}

	/**
	 * ֤�����͸ı��¼�
	 */
	public void onClickIdType() {
		//    	String idType = getValue("ID_TYPE").toString();
		//    	if("99".equals(idType)){
		//    		callFunction("UI|IDNO|setEnabled", false); //֤�����û�
		//    		//���֤��������
		//    		this.setValue("IDNO", "");
		//    	}else{
		//    		callFunction("UI|IDNO|setEnabled", true); //֤��������Ч
		//    	}
		callFunction("UI|IDNO|setEnabled", true); //֤��������Ч
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
		// ������,����,�Ա�,����,���֤��,�绰
		this.setValueForParm(
				"MR_NO;PAT_NAME;PAT_NAME1;FIRST_NAME;LAST_NAME;PY1;IDNO;ID_TYPE;SEX_CODE;BIRTH_DATE;POST_CODE;ADDRESS;RESID_ADDRESS;CURRENT_ADDRESS;" +
						"NATION_CODE;RESID_POST_CODE;HOMEPLACE_CODE;CELL_PHONE;TEL_HOME;E_MAIL;SPECIAL_DIET;OLD_NAME;COMPANY_DESC;REMARKS;CUSTOMER_SOURCE",patInfo.getParm());
		this.setValue("NATION_CODE2", patInfo.getSpeciesCode());//����
		this.setValue("RELIGION", patInfo.getReligionCode());//�ڽ�
		this.setValue("MARRIAGE", patInfo.getMarriageCode());//����
		this.setValue("OLD_NAME", patInfo.getOldName());//������
		this.setValue("CTZ2_CODE", patInfo.getCtz2Code());
		this.setValue("CTZ1_CODE", patInfo.getCtz1Code());
		this.setValue("CTZ3_CODE", patInfo.getCtz3Code());
		if(patInfo.isBookBuild()){
			this.setValue("BOOK_BUILD", "01");
		}else{
			this.setValue("BOOK_BUILD", "02");
		}
		//this.setValue("CTZ3_CODE", patInfo.getCtz3Code());


		//this.setValue("IPD_NO", pat.getIpdNo());
		birthDay=onQueryBirthDateByMrNO(getValue("MR_NO").toString().trim());
		setBirth(false); // ��������
	}
	/**
	 * ������Ա��Ϣ��ֵ
	 */
	public void setMemPatinfoForUI(TParm parm) {
		this.setValue("CUSTOMER_SOURCE", parm.getValue("CUSTOMER_SOURCE", 0));
		this.setValue("BIRTH_HOSPITAL", parm.getValue("BIRTH_HOSPITAL", 0));
		this.setValue("SCHOOL_NAME", parm.getValue("SCHOOL_NAME", 0));
		this.setValue("SCHOOL_TEL", parm.getValue("SCHOOL_TEL", 0));
		//this.setValue("TEL_HOME", parm.getValue("TEL_HOME", 0));
		//this.setValue("EMAIL", parm.getValue("EMAIL", 0));
		//this.setValue("WEIXIN_ACCOUNT", parm.getValue("WEIXIN_ACCOUNT", 0));
		this.setValue("SOURCE", parm.getValue("SOURCE", 0));
		this.setValue("INSURANCE_COMPANY1_CODE", parm.getValue("INSURANCE_COMPANY1_CODE", 0));
		this.setValue("INSURANCE_NUMBER1", parm.getValue("INSURANCE_NUMBER1", 0));
		this.setValue("INSURANCE_COMPANY2_CODE", parm.getValue("INSURANCE_COMPANY2_CODE", 0));
		this.setValue("INSURANCE_NUMBER2", parm.getValue("INSURANCE_NUMBER2", 0));
		this.setValue("GUARDIAN1_NAME", parm.getValue("GUARDIAN1_NAME", 0));
		this.setValue("GUARDIAN1_RELATION", parm.getValue("GUARDIAN1_RELATION", 0));
		this.setValue("GUARDIAN1_TEL", parm.getValue("GUARDIAN1_TEL", 0));
		this.setValue("GUARDIAN1_PHONE", parm.getValue("GUARDIAN1_PHONE", 0));
		this.setValue("GUARDIAN1_COM", parm.getValue("GUARDIAN1_COM", 0));
		this.setValue("GUARDIAN1_ID_TYPE", parm.getValue("GUARDIAN1_ID_TYPE", 0));
		this.setValue("GUARDIAN1_ID_CODE", parm.getValue("GUARDIAN1_ID_CODE", 0));
		this.setValue("GUARDIAN1_EMAIL", parm.getValue("GUARDIAN1_EMAIL", 0));
		this.setValue("GUARDIAN2_NAME", parm.getValue("GUARDIAN2_NAME", 0));
		this.setValue("GUARDIAN2_RELATION", parm.getValue("GUARDIAN2_RELATION", 0));
		this.setValue("GUARDIAN2_TEL", parm.getValue("GUARDIAN2_TEL", 0));
		this.setValue("GUARDIAN2_PHONE", parm.getValue("GUARDIAN2_PHONE", 0));
		this.setValue("GUARDIAN2_COM", parm.getValue("GUARDIAN2_COM", 0));
		this.setValue("GUARDIAN2_ID_TYPE", parm.getValue("GUARDIAN2_ID_TYPE", 0));
		this.setValue("GUARDIAN2_ID_CODE", parm.getValue("GUARDIAN2_ID_CODE", 0));
		this.setValue("GUARDIAN2_EMAIL", parm.getValue("GUARDIAN2_EMAIL", 0));
		//    	this.setValue("REG_CTZ1_CODE", parm.getValue("REG_CTZ1_CODE", 0));//���� �Һ����һ
		//    	this.setValue("REG_CTZ2_CODE", parm.getValue("REG_CTZ2_CODE", 0));//���� �Һ���ݶ� 
		//    	this.setValue("CTZ1_CODE", parm.getValue("CTZ1_CODE", 0));
		//    	this.setValue("CTZ2_CODE", parm.getValue("CTZ2_CODE", 0));
		//    	this.setValue("CTZ3_CODE", parm.getValue("CTZ3_CODE", 0));
		this.setValue("MEM_TYPE", parm.getValue("MEM_CODE", 0));
		this.setValue("FAMILY_DOCTOR", parm.getValue("FAMILY_DOCTOR", 0));
		this.setValue("ACCOUNT_MANAGER_CODE", parm.getValue("ACCOUNT_MANAGER_CODE", 0));
		//    	this.setValue("REASON", parm.getValue("REASON", 0));
		String sDate = parm.getValue("START_DATE", 0);
		String eDate = parm.getValue("END_DATE", 0);
		if(sDate.length()>0){
			this.setValue("START_DATE", sDate.substring(0, 10).replaceAll("-", "/"));
		}
		if(eDate.length()>0){
			this.setValue("END_DATE", eDate.substring(0, 10).replaceAll("-", "/"));
		}
		if(sDate.length()>0 && eDate.length()>0){
			//���㹺������
			int buyMonthAge = getBuyMonth(sDate.substring(0, 10).replaceAll("-", ""),
					eDate.substring(0, 10).replaceAll("-", ""));

			//��������
			int currMonthAge = getBuyMonth(sDate.substring(0, 10).replaceAll("-", ""),
					this.getValue("IN_DATE").toString().substring(0, 10).replaceAll("-", ""));
			this.setValue("BUY_MONTH_AGE", String.valueOf(buyMonthAge));
			this.setValue("HAPPEN_MONTH_AGE", String.valueOf(currMonthAge));
			//System.out.println("�����·ݣ�"+buyMonthAge);
			//System.out.println("�����·ݣ�"+currMonthAge);
		}
		//�໤��Ĭ�ϰ�ť
		//    	String deGuardian = parm.getValue("DEFAULT_GUARDIAN", 0);
		//    	if("1".equals(deGuardian)){
		//    		rubtton1.setSelected(true);
		//    	}else if("2".equals(deGuardian)){
		//    		rubtton2.setSelected(true);
		//    	}
		this.setValue("INCOME", parm.getValue("INCOME", 0));
		this.setValue("GUARDIAN1_AGE", parm.getValue("GUARDIAN1_AGE", 0));
		this.setValue("GUARDIAN2_AGE", parm.getValue("GUARDIAN2_AGE", 0));



	}
	/**
	 * Ԥ�쿨��Ϣ��ֵ
	 */
	public void setMemTradeForUI(TParm parm) {
		this.setValue("MEM_CODE", parm.getValue("MEM_CODE", 0));
		//this.setValue("REASON", parm.getValue("REASON", 0));
		this.setValue("MEM_FEE", parm.getValue("MEM_FEE", 0));
		this.setValue("DESCRIPTION", parm.getValue("DESCRIPTION", 0));
		this.setValue("INTRODUCER1", parm.getValue("INTRODUCER1", 0));
		this.setValue("INTRODUCER2", parm.getValue("INTRODUCER2", 0));
		this.setValue("INTRODUCER3", parm.getValue("INTRODUCER3", 0));
		//this.setValue("REASON", parm.getValue("REASON", 0));
		String sDate = parm.getValue("START_DATE", 0);
		if(sDate.length()>0){
			sDate = sDate.substring(0, 10).replaceAll("-", "/");
			this.setValue("START_DATE_TRADE", sDate);
		}else{//��ʼ������ǰ����
			Timestamp now = StringTool.getTimestamp(new Date());
			this.setValue("START_DATE_TRADE",
					now.toString().substring(0, 10).replace('-', '/'));// Ԥ��Ч����-Ĭ�ϵ���
		}
		String eDate = parm.getValue("END_DATE", 0);
		if(eDate.length()>0){
			eDate = eDate.substring(0, 10).replaceAll("-", "/");
			this.setValue("END_DATE_TRADE", eDate);
		}


	}

	/**
	 * ��ȡҳ������
	 */
	public TParm getData(){
		TParm parm = new TParm();
		//    	if (rubtton1.isSelected()){
		//    		//parm.addData("DEFAULT_GUARDIAN", "1");
		//    		//parm.setData("DEFAULT_GUARDIAN", "1");
		//    		this.setValue("DEFAULT_GUARDIAN", "1");
		//    	}else if(rubtton2.isSelected()){
		//    		//parm.addData("DEFAULT_GUARDIAN", "2");
		//    		//parm.setData("DEFAULT_GUARDIAN", "2");
		//    		this.setValue("DEFAULT_GUARDIAN", "2");
		//    	}

		parm = getParmForTag("MR_NO;PAT_NAME;PAT_NAME1;FIRST_NAME;LAST_NAME;PY1;ID_TYPE;REMARKS;"+	//������Ϣ
				"IDNO;OLD_NAME;SEX_CODE;BIRTH_DATE:Timestamp;NEW_BODY_DATE;"+
				"AGE;NATION_CODE;NATION_CODE2;RELIGION;MARRIAGE;"+
				"ADDRESS;CURRENT_ADDRESS;POST_CODE;RESID_ADDRESS;RESID_POST_CODE;"+
				"HOMEPLACE_CODE;BIRTH_HOSPITAL;SCHOOL_NAME;SCHOOL_TEL;CELL_PHONE;TEL_HOME;E_MAIL;SOURCE;"+
				"INSURANCE_COMPANY1_CODE;INSURANCE_NUMBER1;INSURANCE_COMPANY2_CODE;INSURANCE_NUMBER2;"+
				"GUARDIAN1_NAME;GUARDIAN1_RELATION;GUARDIAN1_TEL;GUARDIAN1_PHONE;DEFAULT_GUARDIAN;"+
				"GUARDIAN1_COM;GUARDIAN1_ID_TYPE;GUARDIAN1_ID_CODE;GUARDIAN1_EMAIL;"+	//�໤��1��Ϣ
				"GUARDIAN2_NAME;GUARDIAN2_RELATION;GUARDIAN2_TEL;GUARDIAN2_PHONE;"+
				"GUARDIAN2_COM;GUARDIAN2_ID_TYPE;GUARDIAN2_ID_CODE;GUARDIAN2_EMAIL;"+	//�໤��2��Ϣ
				"CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;SPECIAL_DIET;COMPANY_DESC;CUSTOMER_SOURCE;"+	//������λ
				"MEM_TYPE;FAMILY_DOCTOR;ACCOUNT_MANAGER_CODE;" + //��Ա��Ϣ
				"START_DATE;END_DATE;BUY_MONTH_AGE;HAPPEN_MONTH_AGE;"+ 
				"MEM_CODE;START_DATE_TRADE;END_DATE_TRADE;MEM_FEE;INTRODUCER1;INTRODUCER2;INTRODUCER3;DESCRIPTION;BOOK_BUILD;"//Ԥ�쿨��Ϣ
				// add by wangqing 20201001 
				+ "INCOME;GUARDIAN1_AGE;GUARDIAN2_AGE"); // ��ͥ���֧�����롢�໤��/��ϵ��1 �����䡢�໤��/��ϵ��2 ������

		String birthDate = parm.getValue("BIRTH_DATE");
		//if(this.modifyFlg){//�С�MODIFY��Ȩ����У�飬����У��
		if(birthDate.length()>0){//����
			birthDate = birthDate.substring(0, 10).replaceAll("-", "/");
			parm.setData("BIRTH_DATE", birthDate);
		}
		//}
		String sDate = parm.getValue("START_DATE");
		String eDate = parm.getValue("END_DATE");
		if(sDate.length()>0){//��Ч����
			sDate = sDate.substring(0, 10).replaceAll("-", "/");
			parm.setData("START_DATE", sDate);
		}
		if(eDate.length()>0){//ʧЧ����
			eDate = eDate.substring(0, 10).replaceAll("-", "/");
			parm.setData("END_DATE", eDate);
		}
		String sDateTrade = parm.getValue("START_DATE_TRADE");
		String eDateTrade = parm.getValue("END_DATE_TRADE");
		if(sDateTrade.length()>0){//Ԥ��Ч����
			sDateTrade = sDateTrade.substring(0, 10).replaceAll("-", "/");
			parm.setData("START_DATE_TRADE", sDateTrade);
		}
		if(eDateTrade.length()>0){//ԤʧЧ����
			eDateTrade = eDateTrade.substring(0, 10).replaceAll("-", "/");
			parm.setData("END_DATE_TRADE", eDateTrade);
		}
		//System.out.println("sDate="+sDate+" eDate="+eDate);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());

		parm.setData("MEM_DESC", this.getTextFormat("MEM_CODE").getText());
		parm.setData("ACCOUNT_MANAGER_NAME", this.getTextFormat("ACCOUNT_MANAGER_CODE").getText());
		parm.setData("INSURANCE_COMPANY1_NAME", "");
		parm.setData("INSURANCE_COMPANY2_NAME", "");
		parm.setData("COMPANY_DESC", this.getValue("COMPANY_DESC"));
		parm.setData("REASON", "");
		parm.setData("OLDNAME", this.getValue("OLD_NAME"));
		parm.setData("CHECK_BOX", "N");//add by sunqy 20140527
		//��ӡ����
		System.out.println("ҳ�����ݣ�"+parm);

		String bookBuild = parm.getValue("BOOK_BUILD");
		if("01".equals(bookBuild)){
			parm.setData("BOOK_BUILD", "Y");
		}
		if("02".equals(bookBuild)){
			parm.setData("BOOK_BUILD", "N");
		}

		return parm;
	}

	/**
	 * �޸Ĳ�����Ϣ��ȡ����
	 *
	 * @param modifyPat
	 *            Pat
	 * @return Pat
	 */
	public Pat readModifyPat(Pat modifyPat) {
		modifyPat.modifyName(getValueString("PAT_NAME")); // ����
		modifyPat.modifySexCode(getValueString("SEX_CODE")); // �Ա�
		modifyPat.modifyBirthdy(TCM_Transform
				.getTimestamp(getValue("BIRTH_DATE"))); // ��������
				//        modifyPat.modifyBirthdy(TCM_Transform
		//                .getTimestamp(getValue("BIRTH_DATE"))); // ��������
		modifyPat.modifyBirthdy(birthDay); // ��������
		modifyPat.modifyCtz1Code(""); // ���ʽ1
		modifyPat.modifyhomePlaceCode(""); // ������
		modifyPat.modifyOccCode(""); // ְҵ
		modifyPat.modifyIdNo(getValueString("IDNO")); // ���֤��
		modifyPat.modifySpeciesCode(getValueString("NATION_CODE2")); // ����SPECIES_CODE
		modifyPat.modifyNationCode(getValueString("NATION_CODE")); // ����
		modifyPat.modifyMarriageCode(getValueString("MARRIAGE")); // ����״̬MARRIAGE_CODE
		modifyPat.modifyCompanyDesc(""); // ��λ COMPANY_DESC
		modifyPat.modifyTelCompany(""); // ��˾�绰
		modifyPat.modifyPostCode(getValueString("POST_CODE")); // �ʱ�
		modifyPat.modifyResidAddress(getValueString("RESID_ADDRESS")); // ������ַ
		modifyPat.modifyResidPostCode("");
		modifyPat.modifyContactsName("");
		modifyPat.modifyRelationCode("");
		modifyPat.modifyContactsTel("");
		modifyPat.modifyContactsAddress("");
		modifyPat.modifyTelHome(getValueString("TEL_HOME")); // ��ͥ�绰
		modifyPat.modifyAddress(getValueString("ADDRESS")); // ��ͥסַ
		modifyPat.modifyAddress(getValueString("CURRENT_ADDRESS")); // ��סַ
		modifyPat.modifyForeignerFlg(false); // �����ע��
		modifyPat.modifyBirthPlace(""); // ����
		modifyPat.modifyCompanyAddress(""); // ��λ��ַ
		modifyPat.modifyCompanyPost(""); // ��λ��ַ
		return modifyPat;
	}
	/**
	 * ��������
	 */
	public void setBirth(boolean flg) {
		/*if (getValue("BIRTH_DATE") == null || "".equals(getValue("BIRTH_DATE")))
            return;
//        String AGE = com.javahis.util.StringUtil.showAge(
//                (Timestamp) getValue("BIRTH_DATE"),
//                (Timestamp) getValue("IN_DATE"));
        birthDay=onQueryBirthDateByMrNO(getValue("MR_NO").toString().trim());
        String AGE = com.javahis.util.DateUtil.showAge(
        		(Timestamp) getValue("BIRTH_DATE"),
                (Timestamp) getValue("IN_DATE"));
        this.messageBox("111::"+AGE);*/
		if(flg){
			birthDay=(Timestamp) getValue("BIRTH_DATE");
		}
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp temp = birthDay == null ? sysDate : birthDay;
		String age = "0";
		age = DateUtil.showAge(temp, sysDate);
		setValue("AGE", age);
	}
	/**
	 * �����Ա�
	 */
	public void setSexCode(String sexCode) {
		try {
			int a = Integer.parseInt(sexCode);
			int b = a%2;
			if(b==0){//Ů
				this.setValue("SEX_CODE", 2);
			}else if(b==1){//��
				this.setValue("SEX_CODE", 1);
			}
			this.grabFocus("NATION_CODE");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * ��ѯԤʧЧ����-��Ԥ��Ч���ڱ仯ʱ��
	 */
	public void queryValidDays(){
		String validDays = "";
		//Timestamp now = StringTool.getTimestamp(new Date());
		String MemCode = this.getValueString("MEM_CODE");
		String sDate = this.getValueString("START_DATE_TRADE");
		if(sDate.length()>0){
			sDate = sDate.substring(0, 10);
			//��ѯ��Ч����
			String sql = "SELECT VALID_DAYS FROM MEM_MEMBERSHIP_INFO WHERE  MEM_CODE = '"+MemCode+"'";
			//System.out.println("sql="+sql);
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if(parm.getCount()>0){
				validDays = parm.getValue("VALID_DAYS", 0);
				//getNextDay
				String eTradeDate = getNextDay2(sDate,validDays);
				this.setValue("END_DATE_TRADE", eTradeDate.replaceAll("-", "/"));
			}
		}
	}

	/**
	 * �õ�һ��ʱ���Ӻ��ǰ�Ƽ����ʱ��,nowdateΪʱ��,delayΪǰ�ƻ���ӵ�����
	 * 
	 * @param date
	 *            String
	 * @return String
	 */
	public String getNextDay(String nowdate, String delay) {
		try {
			//System.out.println("nowdate="+nowdate+" delay="+delay);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String mdate = "";
			Date d = strToDate(nowdate);
			long myTime = (d.getTime() / 1000) + (Integer.parseInt(delay)-1) * 24
					* 60 * 60;
			d.setTime(myTime * 1000);
			mdate = format.format(d);
			return mdate;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * �õ�һ��ʱ���Ӻ�n���ʱ���
	 */
	public String getNextDay2(String nowdate, String delay) {
		try {
			SimpleDateFormat formateDate=new SimpleDateFormat("yyyy-MM-dd");
			Date d = strToDate(nowdate);
			Calendar canlandar = Calendar.getInstance();
			canlandar.setTime(d);
			canlandar.add(Calendar.YEAR, +Integer.parseInt(delay));
			canlandar.add(Calendar.DAY_OF_MONTH, -1);
			String a = formateDate.format(canlandar.getTime());
			//System.out.println("--------->"+a);
			return a;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * ����ʱ���ʽ�ַ���ת��Ϊʱ�� yyyy-MM-dd
	 * 
	 * @param strDate
	 *            String
	 * @return Date
	 */
	public Date strToDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}
	/**
	 * ��ȡ��������
	 */
	public int getBuyMonth(String s, String s1){
		//System.out.println("s="+s+" s1="+s1);
		Date d = null;
		Date d1 = null;
		DateFormat df=new SimpleDateFormat("yyyyMMdd");
		try {  
			d = df.parse(s);
			d1=df.parse(s1);
			//�Ƚ����ڴ�С
			//		    if(d.getTime()>d1.getTime()){
			//		      System.out.println(df.format(d));
			//		    }else{
			//		      System.out.println(df.format(d1));
			//		     	  }
		}catch (ParseException e){    
			e.printStackTrace(); }
		Calendar c = Calendar.getInstance();

		c.setTime(d);
		//c.add(Calendar.YEAR, 1);  //��ݼ�һ��
		//System.out.println(df.format(c.getTime()));//����yyyyMMdd��ʽ���

		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);

		c.setTime(d1);
		int year1 = c.get(Calendar.YEAR);
		int month1 = c.get(Calendar.MONTH);

		int result;
		if(year==year1){
			result=month1-month;//�������������£����·ݲ�
		}else{
			result=12*(year1-year)+month1-month;//�������������£����·ݲ�
		}
		return result;
	}
	//    /**
	//     * �õ����ı�� +1
	//     *
	//     * @param dataStore
	//     *            TDataStore
	//     * @param columnName
	//     *            String
	//     * @return String
	//     */
	//    public int getMaxSeq(String maxValue, String tableName,
	//                         String where1,String value1,String where2,String value2) {
	//    	String sql = "SELECT MAX("+maxValue+") AS "+maxValue+" FROM "+tableName+" WHERE 1=1 ";
	//    	if(where1.trim().length()>0){
	//    		sql += " AND "+where1+" ='"+value1+"'";
	//    	}
	//    	if(where2.trim().length()>0){
	//    		sql += " AND "+where2+" ='"+value2+"'";
	//    	}
	//    	System.out.println("���ı��sql="+sql);
	//    	// ��������
	//        int max = 0;
	//    	//��ѯ������
	//    	TParm seqParm = new TParm(TJDODBTool.getInstance().select(sql));
	//    	String seq = seqParm.getValue(maxValue,0).toString().equals("")?"0"
	//    			:seqParm.getValue(maxValue,0).toString();
	//    	System.out.println("seq="+seq);
	//    	int value = Integer.parseInt(seq);
	//    	System.out.println("value="+value);
	//    	// �������ֵ
	//        if (max < value) {
	//            max = value;
	//        }
	//        // ���ż�1
	//        max++;
	//        System.out.println("���ı�� +1="+max);
	//        return max;
	//        
	//    }
	/**
	 * ���ɽ��׺ű�Ļ�Ա�����
	 */
	public String getMemCardNo(String mrNo) {
		String result = "";
		String sql = "SELECT MEM_CARD_NO,MEM_CODE FROM MEM_TRADE WHERE MR_NO = '"+mrNo+"' AND STATUS = '1' ORDER BY MEM_CARD_NO DESC";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String memCardNo = parm.getValue("MEM_CARD_NO",0).toString().equals("")?"0"
				:parm.getValue("MEM_CARD_NO",0).toString();
		if(memCardNo.length()>0 && !"0".equals(memCardNo)){
			String reason = this.getText("REASON");
			if("����".equals(reason)){
				result = memCardNo;
			}else{
				//ȡ����λת��int+1����ƴ�ϲ�����
				if(memCardNo.length()>3){
					String no = memCardNo.substring(memCardNo.length()-3);
					//System.out.println("����λ��"+no);
					int intNo = Integer.parseInt(no)+1;
					result = StringTool.fillLeft(String.valueOf(intNo) ,3 ,"0" );
					result = mrNo + result;
				}
			}

		}else{
			//    		//������+001
			//    		result = mrNo + "001";

			sql = "SELECT MAX(CARD_NO) CARD_NO FROM EKT_ISSUELOG WHERE MR_NO='"+mrNo+"' ";
			parm = new TParm(TJDODBTool.getInstance().select(sql));
			memCardNo = parm.getValue("CARD_NO",0).toString().equals("")?"0"
					:parm.getValue("CARD_NO",0).toString();
			if(memCardNo.length()>0 && !"0".equals(memCardNo)){
				//ȡ����λת��int+1����ƴ�ϲ�����
				if(memCardNo.length()>3){
					String no = memCardNo.substring(memCardNo.length()-3);
					//System.out.println("����λ��"+no);
					int intNo = Integer.parseInt(no)+1;
					result = StringTool.fillLeft(String.valueOf(intNo) ,3 ,"0" );
					result = mrNo + result;
				}
			}else{
				//������+001
				result = mrNo + "001";

			}

		}
		//System.out.println("���ɻ�Ա�����"+result);
		return result;
	}
	/**
	 * �жϽ��ױ��Ƿ����
	 */
	public boolean exitsMemTrade(String mrNo) {
		boolean flg = false;//������
		String sql = "SELECT * FROM MEM_TRADE WHERE MR_NO = '"+mrNo+"' AND STATUS = '0' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
			flg = true;
		}
		//System.out.println("���ױ��Ƿ����"+flg);
		return flg;
	}
	/**
	 * ���ݴ���
	 */
	public TParm handleData(TParm parm) {
		TParm result = new TParm();

		return result;
	}
	/**
	 * ȡ��ԭ��:mem_trade
	 */
	public String getMEMTradeNo() {
		return SystemTool.getInstance().getNo("ALL", "EKT", "MEM_TRADE_NO",
				"MEM_TRADE_NO");
	}

	/**
	 * ���֤�����ش�ֵadd by huangjw 20141110
	 * @param parm
	 */
	public void setValueByQueryIdNo(TParm parm){
		if(this.getValueString("MR_NO").equals(parm.getValue("MR_NO"))){
			//TParm p = this.getParmForTag("MR_NO;PY1");
			clearValue("PAT_NAME;SEX_CODE;BRITHPLACE;BIRTH_DATE;IDNO;RESID_ADDRESS");
			this.setValueForParm("PAT_NAME;SEX_CODE;BRITHPLACE;BIRTH_DATE;IDNO;RESID_ADDRESS", parm);
		}else{
			this.setValue("MR_NO",parm.getValue("MR_NO"));
			onMrno();
			clearValue("PAT_NAME;SEX_CODE;BRITHPLACE;BIRTH_DATE;IDNO;RESID_ADDRESS");
			this.setValueForParm("PAT_NAME;SEX_CODE;BRITHPLACE;BIRTH_DATE;IDNO;RESID_ADDRESS", parm);
		}
		setBirth(true);//���֤����ʱ ���䴦�� modify by  huangjw
	}
	/**
	 * ������������� ==============pangben 2013-3-18
	 */
	public void onIdCard() {
		if (JOptionPane.showConfirmDialog(null, "�Ƿ񸲸ǣ�", "��Ϣ",
				JOptionPane.YES_NO_OPTION) == 0) {//�����֤ʱ������ʾ��Ϣ��add by huangjw 20141119
			TParm idParm = IdCardO.getInstance().readIdCard();
			if (idParm.getErrCode() < 0) {
				this.messageBox(idParm.getErrText());
				return;
			}
			if (idParm.getCount() > 0) {// ����������ʾ
				if (idParm.getCount() == 1) {// pangben 2013-8-8 ֻ����һ������
					// this.setValue("MR_NO", idParm.getValue("MR_NO",0));
					// onMrno();
					setValueByQueryIdNo(idParm);// modify by huangjw 20141110
				} else {
					Object obj = openDialog(
							"%ROOT%\\config\\sys\\SYSPatChoose.x", idParm);
					TParm patParm = new TParm();
					if (obj != null) {
						patParm = (TParm) obj;
						// this.setValue("MR_NO", patParm.getValue("MR_NO"));
						// onMrno();
						setValueByQueryIdNo(patParm);// modify by huangjw
						// 20141110
					} else {
						return;
					}
				}
				// setValue("VISIT_CODE_F", "Y"); // ����
				this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
						TypeTool.getString(getValue("PAT_NAME"))));// ��ƴ
				// setPatName1();// ����Ӣ��
			} else {
				String sql = "SELECT MR_NO,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS,CURRENT_ADDRESS,RESID_ADDRESS FROM SYS_PATINFO WHERE PAT_NAME LIKE '"
						+ idParm.getValue("PAT_NAME") + "%'";
				TParm infoParm = new TParm(TJDODBTool.getInstance().select(sql));
				if(!checkPatIsExist()){
					this.onNew(); // add by huangtt 20141022
				}
				if (infoParm.getCount() <= 0) {
					this.messageBox(idParm.getValue("MESSAGE"));
					// setValue("VISIT_CODE_C", "Y"); // Ĭ�ϳ���
					// callFunction("UI|MR_NO|setEnabled", false); //
					// �����Ų��ɱ༭--�������
				} else {
					this.messageBox("������ͬ�����Ĳ�����Ϣ");
					this.grabFocus("PAT_NAME");// Ĭ��ѡ��
				}
				this.setValue("PAT_NAME", idParm.getValue("PAT_NAME"));
				this.setValue("ID_TYPE", "01"); // add by huangtt 20141022
				this.setValue("IDNO", idParm.getValue("IDNO"));
				this.setValue("BIRTH_DATE", idParm.getValue("BIRTH_DATE"));
				this.setValue("SEX_CODE", idParm.getValue("SEX_CODE"));
				sql = "SELECT ID FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SPECIES' AND CHN_DESC LIKE '"
						+ idParm.getValue("BRITHPLACE") + "%'";
				TParm species = new TParm(TJDODBTool.getInstance().select(sql));
				if (species.getCount() > 0) {
					this.setValue("NATION_CODE2", species.getValue("ID", 0));
				}
				this.setValue("NATION_CODE", "86"); // add by huangtt 20141022
				// this.setValue("ADDRESS", idParm.getValue("ADDRESS"));// ��ͥ��ַ
				this.setValue("CURRENT_ADDRESS", idParm
						.getValue("RESID_ADDRESS"));// �ֵ�ַ add by huangtt
				// 20141110
				this
				.setValue("RESID_ADDRESS", idParm
						.getValue("RESID_ADDRESS"));// ������ַ
				this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
						TypeTool.getString(getValue("PAT_NAME"))));// ��ƴ
				// setPatName1();// ����Ӣ��

			}
		}
	}

	/**
	 * ����CRM���صĲ����ţ���ѯ�˲����Ƿ��Ѵ���
	 * @return
	 */
	public boolean checkPatIsExist(){
		boolean flg=false;
		String mr_no=this.getValueString("MR_NO");
		String sql="select * from sys_patinfo where mr_no='"+mr_no+"'";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
			flg=true;
		}
		return flg;
	}

	/**
	 * ̩��ҽ�ƿ����� =========================
	 */
	public void onEKTcard() {
		// �Ͼ�ҽ������������
		// ̩��ҽ�ƿ�����
		p3 = EKTIO.getInstance().TXreadEKT();
		// System.out.println("P3=================" + p3);
		// 6.�ͷŶ����豸
		// int ret99 = NJSMCardDriver.FreeReader(ret0);
		// 7.ע��TFReader.dll
		// int ret100 = NJSMCardDriver.close();
		StringBuffer sql = new StringBuffer();
		int typeEKT = -1; // ҽ�ƿ�����
		if (null != p3 && p3.getValue("identifyNO").length() > 0) {
			sql.append("SELECT * FROM SYS_PATINFO WHERE MR_NO in (select max(MR_NO) from SYS_PATINFO");
			typeEKT = 1; // �Ͼ�ҽ����
			sql.append(" WHERE IDNO='" + p3.getValue("identifyNO").trim()
					+ "' ) ");
		} else if (null != p3 && p3.getValue("MR_NO").length() > 0) {
			// sql
			// .append("SELECT A.MR_NO,A.NHI_NO,B.BANK_CARD_NO FROM SYS_PATINFO A,EKT_ISSUELOG B WHERE A.MR_NO = B.MR_NO AND B.CARD_NO ='"
			// + p3.getValue("MR_NO")
			// + p3.getValue("SEQ")
			// + "' AND WRITE_FLG='Y'");
			typeEKT = 2; // ̩��ҽ�ƿ�
			//this.setValue("PAY_WAY", "PAY_MEDICAL_CARD"); // ֧����ʽ�޸�
			//this.setValue("CONTRACT_CODE", "");
			//callFunction("UI|CONTRACT_CODE|setEnabled", false); // ���˵�λ���ɱ༭
		}
		// ͨ�����֤�Ų����Ƿ���ڴ˲�����Ϣ
		// callFunction("UI|FOREIGNER_FLG|setEnabled", false);//����֤�����ɱ༭
		if (typeEKT > 0) {
			onReadTxEkt(p3, typeEKT);
		} else {
			this.messageBox("��ҽ�ƿ���Ч");
			return;
		}
		// �Ͼ�ҽ��������
		if (typeEKT == 1) {
			NJSMCardDriver.close();
			NJSMCardYYDriver.close();
		}
		//setValue("EKT_CURRENT_BALANCE", p3.getDouble("CURRENT_BALANCE"));
		// ===zhangp 20120318 end
	}

	/**
	 * ҽ�ƿ���������
	 * 
	 * @param IDParm
	 *            TParm
	 * @param typeEKT
	 *            int
	 */
	private void onReadTxEkt(TParm IDParm, int typeEKT) {
		// TParm IDParm = new TParm(TJDODBTool.getInstance().select(sql));
		// ͨ�����֤�Ų����Ƿ���ڴβ���
		if (IDParm.getValue("MR_NO").length() > 0) {
			setValue("MR_NO", IDParm.getValue("MR_NO")); // ���ڽ���������ʾ
			onMrno(); // ִ�и�ֵ����
			//setValue("NHI_NO", IDParm.getValue("NHI_NO")); // ==-============pangben
			//tjINS = true; // ���ҽ��ʹ�ã��ж��Ƿ�ִ����ҽ�ƿ�����
			//callFunction("UI|PAY_WAY|setEnabled", false); // ֧�����
		} else {
			this.messageBox("��ҽ�ƿ���Ч"); // ��������ʾ�����ϵ���Ϣ�����֤�š����ơ�ҽ����
			switch (typeEKT) {
			// �Ͼ�ҽ���� û�д˲�����Ϣʱִ�и�ֵ����
			case 1:
				this.setValue("IDNO", p3.getValue("identifyNO")); // ���֤��
				//this.setValue("NHI_NO", p3.getValue("siNO")); // ҽ����
				this.setValue("PAT_NAME", p3.getValue("patientName").trim()); // ����
				break;
				// ̩��ҽ�ƿ�û�д˲�����Ϣʱִ�и�ֵ����
			case 2:

				// this.setValue("MR_NO",p3.getValue("MR_NO"));
				txEKT = true; // ̩��ҽ�ƿ�д�������ܿ�
				break;
			}
			// this.setValue("VISIT_CODE_C","N");
			callFunction("UI|MR_NO|setEnabled", false); // �����Ų��ɱ༭
			this.grabFocus("PAT_NAME");
			//setValue("VISIT_CODE_C", "Y"); // Ĭ�ϳ���
		}
	}

	/**
	 * ��ַ��д����
	 */
	public void onAddress() {
		String rePostCode = this.getValueString("RESID_POST_CODE");
		String reAddress = this.getValueString("RESID_ADDRESS");
		if(reAddress.length()>0){
			this.setValue("CURRENT_ADDRESS", reAddress);
		}
		if(rePostCode.length()>0){
			this.setValue("POST_CODE", rePostCode);
		}
		this.grabFocus("SPECIAL_DIET");
	}

	/**
	 * ���֤��У��
	 */
	public boolean isCard(String idcard)
	{
		return idcard == null || "".equals(idcard) ? false : Pattern.matches(   
				"(^\\d{15}$)|(\\d{17}(?:\\d|x|X)$)", idcard);
	}



	/**
	 * ������״̬

	public void onClickRadioButton() {
		if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_C"))) {
			this.onClear();
	    	callFunction("UI|MR_NO|setEnabled", false); 	// �������û�
	    	callFunction("UI|new|setEnabled", false); 		// ������ť�û�
	    	callFunction("UI|save|setEnabled", true); 		// ���水ť�ÿ���
	    	callFunction("UI|buycard|setEnabled", false);   // ������ť�û�
	    	this.grabFocus("PAT_NAME");
		}
		if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_F"))) {
			this.onClear();
			callFunction("UI|MR_NO|setEnabled", true); 		// ����������Ч
	    	callFunction("UI|new|setEnabled", false); 		// ������ť�û�
	    	callFunction("UI|save|setEnabled", true); 		// ���水ť�ÿ���
	    	callFunction("UI|buycard|setEnabled", false);   // ������ť�û�
			this.grabFocus("MR_NO");
		}

	}  */

	/**
	 * ��ⲡ����ͬ����
	 */
	public void onPatName(String type) {
		String py1 = this.getValueString("PY1");
		String patName = this.getValueString("PAT_NAME");
		if (StringUtil.isNullString(patName)) {	
			return;
		}
		String sexCode = this.getValueString("SEX_CODE");
		if (StringUtil.isNullString(sexCode)) {
			this.grabFocus("PY1");
			return;
		}
		try {
			String selPat = "SELECT   A.MR_NO, A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
					+ " POST_CODE, RESID_ADDRESS,CURRENT_ADDRESS,ID_TYPE"
					+ " FROM SYS_PATINFO A "
					+ " WHERE PAT_NAME = '"
					+ patName
					+ "' AND  SEX_CODE='"
					+ sexCode    // add by huangtt 20131126
					+ "' "
					+ " ORDER BY A.OPT_DATE,A.BIRTH_DATE";
			// ===zhangp 20120319 end
			TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
			if (same.getErrCode() != 0) {
				this.messageBox_(same.getErrText());
			}
			//			setPatName1();
			// ѡ�񲡻���Ϣ
			if (same.getCount("MR_NO") > 0) {
				//				int sameCount = this.messageBox("��ʾ��Ϣ", "������ͬ����������Ϣ,�Ƿ�������������Ϣ", 0);
				//				int sameCount = JOptionPane.showConfirmDialog(null, "������ͬ����������Ϣ,�Ƿ�������������Ϣ", "��ʾ��Ϣ", JOptionPane.YES_OPTION);
				//add by huangtt 20140926 start
				Object[] possibilities = { "  ��   ", "  ��   " };
				int sameCount = JOptionPane.showOptionDialog(null,
						"������ͬ�����Ĳ�����Ϣ,�Ƿ�������������Ϣ", "��ʾ��Ϣ",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
						null, possibilities, possibilities[1]);
				//add by huangtt 20140926 end
				if (sameCount != 1) {
					if("name".equals(type)){
						this.grabFocus("FIRST_NAME");
					}
					if("sex".equals(type)){
						this.grabFocus("BIRTH_DATE");
					}

					return;
				}
				Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
					onQueryNO(patParm.getValue("MR_NO"));
					return;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if("name".equals(type)){
			this.grabFocus("FIRST_NAME");
		}
		if("sex".equals(type)){
			this.grabFocus("BIRTH_DATE");
		}

	}

	/**
	 * ����Ӣ����
	 */
	public void setPatName1() {
		String patName1 = SYSHzpyTool.getInstance().charToAllPy(
				TypeTool.getString(getValue("PAT_NAME")));
		setValue("PAT_NAME1", patName1);
	}

	/**
	 * ��Աע���ӡ
	 */
	public void onPrint(String newMrNo) {
		if(this.getValueString("MEM_CODE").equals("")){
			return;
		}
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT",
				(Operator.getRegion() != null &&
				Operator.getRegion().length() > 0 ?
						Operator.getHospitalCHNFullName() : "����ҽԺ") );
		parm.setData("MR_NO", "TEXT", newMrNo); //������
		parm.setData("MEM_NAME", "TEXT", parmPrint.getValue("PAT_NAME")); //��Ա����
		parm.setData("SEX_TYPE", "TEXT", parmPrint.getValue("SEX_CODE").equals("1") ? "��" : "Ů"); //�Ա�
		parm.setData("MEM_TYPE", "TEXT", this.getTextFormat("MEM_CODE").getText()); //��������
		parm.setData("FEE", "TEXT", StringTool.round(parmPrint.getDouble("MEM_FEE"), 2)); //����
		parm.setData("FEE_NAME", "TEXT",
				StringUtil.getInstance().numberToWord(
						parmPrint.getDouble("MEM_FEE"))); //��д���

		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
				getInstance().getDBTime()), "yyyy/MM/dd"); //������
		String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "HH:mm"); //ʱ��

		parm.setData("OPT_DATE", "TEXT", yMd); //��ӡʱ��
		parm.setData("OPT_MINUTE", "TEXT", hms); //����ʱ�䣺ʱ��
		parm.setData("OPT_USER", "TEXT", Operator.getName()); //������
		//��ӡ
		this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMRegister.jhw", parm ,true);


	}

	/**
	 * ��ӡ
	 */
	public void onRePrint() {
		if(parmPrint==null){
			parmPrint = getData();//��ӡparm
		}
		String newMrNo = this.getValueString("MR_NO");
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT",
				(Operator.getRegion() != null &&
				Operator.getRegion().length() > 0 ?
						Operator.getHospitalCHNFullName() : "����ҽԺ") );
		parm.setData("MR_NO", "TEXT", newMrNo); //������
		parm.setData("MEM_NAME", "TEXT", parmPrint.getValue("PAT_NAME")); //��Ա����
		parm.setData("SEX_TYPE", "TEXT", parmPrint.getValue("SEX_CODE").equals("1") ? "��" : "Ů"); //�Ա�
		parm.setData("MEM_TYPE", "TEXT", this.getTextFormat("MEM_CODE").getText()); //��������
		parm.setData("FEE", "TEXT", StringTool.round(parmPrint.getDouble("MEM_FEE"), 2)); //����
		parm.setData("FEE_NAME", "TEXT",
				StringUtil.getInstance().numberToWord(
						parmPrint.getDouble("MEM_FEE"))); //��д���

		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
				getInstance().getDBTime()), "yyyy/MM/dd"); //������
		String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "HH:mm"); //ʱ��

		parm.setData("OPT_DATE", "TEXT", yMd); //��ӡʱ��
		parm.setData("OPT_MINUTE", "TEXT", hms); //����ʱ�䣺ʱ��
		parm.setData("OPT_USER", "TEXT", Operator.getName()); //������
		//��ӡ
		this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMRegister2.jhw", parm ,true);
	}

	/**
	 * �õ�TextFormat����
	 * @param tagName String
	 * @return TTextFormat
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}

	public void onCRM() throws ParseException{
		TParm parm = new TParm();
		//		parm.addData("MR_NO", this.getValueString("MR_NO"));
		TParm result = (TParm) this.openDialog("%ROOT%\\config\\reg\\REGCRM.x",parm,false);
		if(result != null){
			this.setValue("MR_NO", result.getData("MR_NO",0));
			this.onMrno();
			this.setValue("BOOK_BUILD", "01");  //add by huangtt 20150603
			callFunction("UI|save|setEnabled", true); 		// ���水ť�ÿ���
			this.grabFocus("PAT_NAME");
		}

	}

	/**
	 * �����˿���ʷ���ײ�ѯ
	 */
	public void onHisInfo(){
		TParm parm = new TParm();
		//��ȡҳ������
		parm = getData();
		TParm reParm = (TParm)this.openDialog(
				"%ROOT%\\config\\mem\\MEMMarketCardQuery.x", parm);
	}

	/**
	 * ����רԱ¼����Ϣ��ť�¼�      add by sunqy 2014/05/14
	 */
	public void onInsureInfo(){
		TParm parm = new TParm();
		parm.setData("MR_NO",this.getValueString("MR_NO"));
		parm.setData("EDIT", "Y");
		//    	System.out.println("````````````parm=="+parm);
		TParm reParm = (TParm)this.openDialog(
				"%ROOT%\\config\\mem\\MEMInsureInfo.x", parm);
	}

	/**
	 * ��Ѳ�ѯ��
	 * 1,���ݻ�Ա������+�쿨��ʽ+�����Զ�����(����)
	 * 2,���ݻ�ѱ��MEM_CODE��ѯ
	 */
	//    public boolean onQueryFee(){
	//    	boolean flg = false;
	//    	if(this.getValueString("MEM_CODE").length() >0 &&
	//    			this.getValueString("CARD_YEAR").length() > 0 &&
	//    			this.getValueString("REASON").length() > 0){
	//    		flg = onFee();
	//    	}else{
	//    		flg = false;
	//    		return flg;
	//    	}
	//    	return flg;
	//    }
	public void onQueryFee(){
		String memCode = this.getValueString("MEM_CODE");
		String sql = "SELECT MEM_FEE,MEM_IN_REASON,MEM_CARD FROM MEM_MEMBERSHIP_INFO WHERE MEM_CODE = '"+memCode+"' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
			this.setValue("MEM_FEE", parm.getValue("MEM_FEE", 0));
			this.setValue("REASON", parm.getValue("MEM_IN_REASON", 0));
			this.setValue("MEM_TYPE1", parm.getValue("MEM_CARD", 0));

			try {
				this.onContinuationCard();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * ��Ѳ�ѯ2:û�鵽��ʾû�ж�Ӧ�Ļ�Ա����
	 */
	//    public boolean onFee() {
	//    	boolean flg = false;
	//    	String memCode = this.getValueString("MEM_CODE");
	//    	String cardYear = this.getValueString("CARD_YEAR");
	//    	String reason = this.getValueString("REASON");
	//    	String sql = "SELECT MEM_FEE FROM MEM_MEMBERSHIP_INFO WHERE MEM_CODE = '"+memCode+"' " +
	//    			" AND VALID_DAYS = '"+cardYear+"' AND MEM_IN_REASON = '"+reason+"'";
	//    	System.out.println("sql-------->"+sql);
	//    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
	//    	if(parm.getCount()>0){
	//    		this.setValue("MEM_FEE", parm.getValue("MEM_FEE", 0));
	//    		//�ж��������-�Ƿ�ͻ�Ա���һ��
	//    		if(this.getValueString("REASON").equals("02")){
	//    			if(!this.getValueString("MEM_CODE").equals(this.getValueString("MEM_TYPE"))){
	//    				this.messageBox("�����������Ա��ݲ�һ��,�޷�������");
	//    				ifTradeCard = false;//������Ԥ�쿨
	//    				flg = false;
	//    				return flg;
	//    			}
	//    		}
	//    		flg = true;
	//    		ifTradeCard = true;//����Ԥ�쿨
	//    	}else{
	//    		this.messageBox("û�ж�Ӧ�Ļ�Ա���ͣ�");
	//    		//���
	//    		this.setValue("MEM_FEE", "");
	//    		flg = false;
	//    		ifTradeCard = false;//������Ԥ�쿨
	//    		return flg;
	//    	}
	//    	return flg;
	//    }

	/**
	 * ҽ�ƿ�����
	 */
	public void onEKTBuy() {
		TParm parm = new TParm();
		//��ȡҳ������
		parm = getData();
		TParm reParm = (TParm)this.openDialog(
				"%ROOT%\\config\\ekt\\EKTWorkUI.x", parm);
		String mr_no = this.getValueString("MR_NO");
		this.onClear();
		this.setValue("MR_NO", mr_no);
		this.onMrno(); 
	}

	/**
	 * �����ж�
	 * @throws ParseException 
	 */
	public void onContinuationCard() throws ParseException{
		String reason = this.getText("REASON");
		//    	TTextFormat t = this.getTextFormat("REASON");
		if("����".equals(reason)){
			String memType = this.getValueString("MEM_TYPE");
			if(memType.equals("")){
				this.messageBox("�ò���û�л�Ա����������������");
				this.clearValue("REASON;MEM_CODE;MEM_FEE");
				return;
			}else{
				String date =this.getValueString("END_DATE");
				Timestamp  ts = Timestamp.valueOf(date);
				this.setValue("START_DATE_TRADE", StringTool.rollDate(ts, +1).toString()
						.substring(0, 10).replace('-', '/'));
				queryValidDays();
			}
		}
		if("����".equals(reason)){
			String sql = "SELECT * FROM MEM_TRADE WHERE STATUS = '1' AND REMOVE_FLG = 'N' AND MR_NO='"+this.getValueString("MR_NO")+"'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if(parm.getCount()>0){
				this.messageBox("�ÿ�û��ͣ��������������");
				this.clearValue("REASON;MEM_CODE;MEM_FEE");
				return;
			}else{
				Timestamp date = SystemTool.getInstance().getDate();
				this.setValue("START_DATE_TRADE", date);
				queryValidDays();
			}

		}
		if("�°�".equals(reason)){
			TParm parm = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemTrade1(this.getValueString("MR_NO"))));
			if(parm.getCount()>0){
				this.messageBox("�ò����а쿨��¼���������°�");
				this.clearValue("REASON;MEM_CODE;MEM_FEE");
				return;
			}
		}
	}

	/**
	 * ��ӡ���
	 */
	public void onWrist() {
		if (this.getValueString("MR_NO").length() == 0) {
			return;
		}
		String birthday = this.getValueString("BIRTH_DATE");
		if(birthday.length()>8){
			birthday = birthday.substring(0, 10).replaceAll("-", "/");
		}
		TParm print = new TParm();
		print.setData("Barcode", "TEXT", this.getValueString("MR_NO"));
		print.setData("PatName", "TEXT", this.getValueString("PAT_NAME"));
		String sexName=this.getTextFormat("SEX_CODE").getText();//modify by huangjw 20140715
		print.setData("Sex", "TEXT", sexName);
		print.setData("BirthDay", "TEXT", birthday);
		//	        this.openPrintDialog(IReportTool.getInstance().getReportPath("MEMWrist.jhw"),
		//	                             IReportTool.getInstance().getReportParm("MEMWrist.class", print));
		this.openPrintDialog("%ROOT%\\config\\prt\\MEM\\MEMWrist.jhw",
				print, true); 
	}


	/**
	 * �ж��Ƿ�Ϊ����
	 * @param str
	 * @return
	 */
	public  boolean isNumeric(String str){ 
		Pattern pattern = Pattern.compile("[0-9]*"); 
		return pattern.matcher(str).matches();    
	} 

	/**
	 * д��LOG��Ϣ
	 * 
	 * @return boolean
	 */
	private void writeLog(String mr_no, TParm patParm, String action) {
		String insert_sql = "INSERT INTO "
				+ "SYS_PATLOG(MR_NO, OPT_DATE, MODI_ITEM, ITEM_OLD, ITEM_NEW, OPT_USER, OPT_TERM ) "
				+ "VALUES('" + mr_no + "', SYSDATE, '#', '#', '#', '"
				+ Operator.getID() + "', '" + Operator.getIP() + "')";
		TParm parm = new TParm();

		String columns[] = {
				"PAT_NAME",
				"PY1",
				"FIRST_NAME",
				"LAST_NAME",
				"OLD_NAME", // 1-5
				"ID_TYPE",
				"IDNO",
				"SEX_CODE",
				"BIRTH_DATE",
				"NATION_CODE", // 6-10
				"NATION_CODE2",
				"MARRIAGE",
				"CELL_PHONE",
				"TEL_HOME",
				"E_MAIL", // 11-15
				"RESID_POST_CODE",
				"RESID_ADDRESS",
				"SPECIAL_DIET",
				"RELIGION",
				"POST_CODE", // 16-20
				"CURRENT_ADDRESS",
				"COMPANY_DESC",
				"CTZ1_CODE",
				"CTZ2_CODE",
				"CTZ3_CODE", // 21-25
				"BOOK_BUILD",
				"GUARDIAN1_NAME", // 26-30
				"GUARDIAN1_RELATION",
				"GUARDIAN1_PHONE"
		};

		String columnNames[] = { "����", "��ƴ", "FirstName", "FamilyName", "������", // 1-5
				"֤�����ʹ���", "֤����", "�Ա�", "��������", "����/��������", // 6-10
				"�� �����", "��������", "�� ��", "�� ��", "����", // 11-15
				"�����ʱ�", "������ַ", "������ʳ", "�ڽ̴���", "��סַ�ʱ�", // 16-20
				"��סַ��ַ", "������λ", "���һ", "��ݶ�", "�����", // 21-25
				"����¼��", "������ϵ��", // 26-30
				"������ϵ�˹�ϵ","������ϵ�˵绰"
		};

		if ("UPDATE".equals(action)) {
			for (int i = 0; i < columns.length; i++) {

				if ("BIRTH_DATE".equals(columns[i])) {
					String new_date_time = this.getValueString(columns[i]);
					String old_date_time = TypeTool.getString(patParm
							.getData(columns[i]));
					if (old_date_time != null && old_date_time.length() >= 10) {
						new_date_time = new_date_time.substring(0, 10);
						old_date_time = old_date_time.substring(0, 10);
						if (!old_date_time.equals(new_date_time)) {
							parm.addData("MODI_ITEM", columnNames[i]);
							parm.addData("ITEM_OLD", old_date_time);
							parm.addData("ITEM_NEW", new_date_time);
						} else {
							continue;
						}
					} else {
						if (new_date_time != null
								&& new_date_time.length() > 10) {
							parm.addData("MODI_ITEM", columnNames[i]);
							parm.addData("ITEM_OLD", "");
							parm.addData("ITEM_NEW",
									new_date_time.substring(0, 10));
						} else {
							continue;
						}
					}
				} else if("NATION_CODE2".equals(columns[i])) {
					String old_date = patParm.getData("SPECIES_CODE")+"";
					String new_date = this.getValueString(columns[i]);
					if(!old_date.equals(new_date)){
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", old_date);
						parm.addData("ITEM_NEW",new_date);
					}
				} else if("MARRIAGE".equals(columns[i])) {
					String old_date = patParm.getData("MARRIAGE_CODE")+"";
					String new_date = this.getValueString(columns[i]);
					if(!old_date.equals(new_date)){
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", old_date);
						parm.addData("ITEM_NEW",new_date);
					}
				} else if("RELIGION".equals(columns[i])) {
					String old_date = patParm.getData("RELIGION_CODE")+"";
					String new_date = this.getValueString(columns[i]);
					if(!old_date.equals(new_date)){
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", old_date);
						parm.addData("ITEM_NEW",new_date);
					}
				} else if("GUARDIAN1_NAME".equals(columns[i])) {
					String old_date = patParm.getData("CONTACTS_NAME")+"";
					String new_date = this.getValueString(columns[i]);
					if(!old_date.equals(new_date)){
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", old_date);
						parm.addData("ITEM_NEW",new_date);
					}
				} else if("GUARDIAN1_RELATION".equals(columns[i])) {
					String old_date = patParm.getData("RELATION_CODE")+"";
					String new_date = this.getValueString(columns[i]);
					if(!old_date.equals(new_date)){
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", old_date);
						parm.addData("ITEM_NEW",new_date);
					}
				} else if("GUARDIAN1_PHONE".equals(columns[i])) {
					String old_date = patParm.getData("CONTACTS_TEL")+"";
					String new_date = this.getValueString(columns[i]);
					if(!old_date.equals(new_date)){
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", old_date);
						parm.addData("ITEM_NEW",new_date);
					}
				} else if("BOOK_BUILD".equals(columns[i])) {
					String old_date = patParm.getData("BOOK_BUILD")+"";
					String new_date = "";
					if("01".equals(this.getValueString(columns[i]))){
						new_date = "Y";
					}else{
						new_date = "N";
					}
					if(!old_date.equals(new_date)){
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", old_date);
						parm.addData("ITEM_NEW",new_date);
					}
				}else {
					if (!this.getValueString(columns[i]).equals(
							patParm.getData(columns[i]))) {
						parm.addData("MODI_ITEM", columnNames[i]);
						parm.addData("ITEM_OLD", patParm.getData(columns[i]));
						parm.addData("ITEM_NEW",
								this.getValueString(columns[i]));
					}
				}
			}
		} 
		parm.setData("SQL", insert_sql);
		if (parm == null || parm.getCount("MODI_ITEM") <= 0) {
			return;
		} else {
			// System.out.println("parm----"+parm);
			// ִ����������
			TParm result = TIOM_AppServer.executeAction(
					"action.sys.SYSWriteLogAction", "onSYSPatLog", parm);
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("LOGд��ʧ�ܣ�");
			} else {
				// this.messageBox("LOGд��ɹ���");
			}
		}
	}

	/**
	 * ���˲����ź��Ƿ񽨵��������������������������޸�
	 * add by huangtt 20150227
	 */
	public void lockName(){

		String name = "PAT_NAME;PY1;FIRST_NAME;LAST_NAME;OLD_NAME;ID_TYPE;IDNO;SEX_CODE;" +
				"BIRTH_DATE;AGE;NATION_CODE;NATION_CODE2;MARRIAGE;CELL_PHONE;TEL_HOME;E_MAIL;" +
				"RESID_POST_CODE;RESID_ADDRESS;SPECIAL_DIET;RELIGION;POST_CODE;CURRENT_ADDRESS;INSURE_INFO;" +
				"COMPANY_DESC;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;SOURCE;SCHOOL_NAME;SCHOOL_TEL;HOMEPLACE_CODE;" +
				"BIRTH_HOSPITAL;GUARDIAN1_NAME;GUARDIAN1_RELATION;GUARDIAN1_PHONE;GUARDIAN1_TEL;GUARDIAN1_COM;" +
				"GUARDIAN1_ID_TYPE;GUARDIAN1_ID_CODE;GUARDIAN1_EMAIL;GUARDIAN2_NAME;GUARDIAN2_RELATION;" +
				"GUARDIAN2_PHONE;GUARDIAN2_TEL;GUARDIAN2_COM;GUARDIAN2_ID_TYPE;GUARDIAN2_ID_CODE;GUARDIAN2_EMAIL;" +
				"MEM_TYPE;FAMILY_DOCTOR;ACCOUNT_MANAGER_CODE;START_DATE;END_DATE;BUY_MONTH_AGE;HAPPEN_MONTH_AGE;" +
				"MEM_CODE;MEM_FEE;START_DATE_TRADE;END_DATE_TRADE;INTRODUCER1;INTRODUCER2;INTRODUCER3;DESCRIPTION";
		String [] lock = name.split(";");
		for (int i = 0; i < lock.length; i++) {
			callFunction("UI|"+lock[i]+"|setEnabled", false);
		}

		callFunction("UI|save|setEnabled", true); 		// ���水ť�û�
		callFunction("UI|query|setEnabled", true); 		// ���水ť�û�
		callFunction("UI|EKTcard|setEnabled", true); 		// ���水ť�û�
		callFunction("UI|idcard|setEnabled", true); 		// ���水ť�û�
		callFunction("UI|new|setEnabled", false); 	// ������ť�û�
		callFunction("UI|buycard|setEnabled", false); 	// ������ť�û�
		callFunction("UI|makecard|setEnabled", false); 	// �ƿ���ť�û�
		callFunction("UI|MEMprint|setEnabled", false); 	// ��ӡ��ť�û�
		callFunction("UI|hl|setEnabled", false); 		// ��ʷ���װ�ť�û�
		callFunction("UI|Wrist|setEnabled", false);  
		callFunction("UI|crmreg|setEnabled", false);  
	}

	/**
	 * �޸ĳ�������
	 */
	public void onUpdateBirthDate(){
		if(!this.getValueString("MR_NO").contains("-")){
			TParm parm=new TParm();
			parm.setData("MR_NO",this.getValueString("MR_NO"));
			returnParm=(TParm) this.openDialog("%ROOT%\\config\\mem\\MEMUpdateBirthDate.x",parm);
			birthDay=returnParm.getTimestamp("BIRTH_DATE");
			this.setValue("BIRTH_DATE", birthDay);
			setBirth(false); // ��������
		}else{
			String motherMrNo=this.getValueString("MR_NO").substring(0,this.getValueString("MR_NO").indexOf("-"));
			String sql="SELECT A.IPD_NO,A.CASE_NO,A.IN_DATE,B.SEX_CODE,A.DEPT_CODE,A.STATION_CODE " +
					" FROM ADM_INP A,SYS_PATINFO B WHERE A.MR_NO='"+motherMrNo+"' AND A.MR_NO=B.MR_NO ";
			TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
			TParm actionParm=new TParm();
			actionParm.setData("ADM", "CASE_NO", parm.getValue("CASE_NO",0));
			actionParm.setData("ADM", "MR_NO", this.getValueString("MR_NO"));
			actionParm.setData("ADM", "IPD_NO", parm.getData("IPD_NO",0));
			actionParm.setData("ADM", "IN_DATE", parm.getData("IN_DATE",0));
			actionParm.setData("ADM", "ADM_FLG", "N");
			actionParm.setData("ODI", "SEX_CODE", parm.getData("SEX_CODE",0));
			actionParm.setData("ODI", "DEPT_CODE", parm.getData("DEPT_CODE",0));
			actionParm.setData("ODI", "STATION_CODE", parm.getData("STATION_CODE",0));
			actionParm.setData("MEM_FLG", "Y");
			this.openDialog("%ROOT%\\config\\adm\\ADMNewBodyRegister.x",actionParm);
			this.onClear();
		}
	}

}
