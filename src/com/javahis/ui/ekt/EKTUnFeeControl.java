package com.javahis.ui.ekt;

import jdo.sys.Operator;
//import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.Pat;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;

import java.sql.Timestamp;
import com.dongyang.control.TControl;
import jdo.ekt.EKTTool;

import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import jdo.sys.PatTool;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ҽ�ƿ��˷�</p>
 *
 * <p>Description: ҽ�ƿ��˷�</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20111008
 * @version 1.0
 */
public class EKTUnFeeControl extends TControl {
    public EKTUnFeeControl() {
    }

    private Pat pat; //������Ϣ
    private TParm parmEKT;
    private boolean ektFlg = false;
    private boolean printBil=false;//��ӡƱ��ʱʹ��
    private TParm parmSum;//ִ���˿��������
    //zhangp 20111227
    private int row = -1;//ѡ����
    private double reEktFee = 0.00;//�������
    private double execAmt = 0; //���ڶ����Ǯ
    
   /**
    * ��ʼ������
    */
   public void onInit() {
       //=====zhangp 20120224 ֧����ʽ    modify start 
       String id = EKTTool.getInstance().getPayTypeDefault();
       setValue("GATHER_TYPE", id);
       //=======zhangp 20120224 modify end
   }

   /**
    * ��ҽ�ƿ�
    */
   public void onReadEKT() {
       //��ȡҽ�ƿ�
       parmEKT = EKTIO.getInstance().TXreadEKT();
       if (null == parmEKT || parmEKT.getErrCode() < 0 ||
           parmEKT.getValue("MR_NO").length() <= 0) {
           this.messageBox(parmEKT.getErrText());
           parmEKT = null;
           return;
       }
       //��Ƭ���
       this.setValue("OLD_EKTFEE", parmEKT.getDouble("CURRENT_BALANCE"));
       
       //���˽�� duzhw add 20140408
       double payOther3 = EKTpreDebtTool.getInstance().getPayOther(parmEKT.getValue("MR_NO"), 
    		   					EKTpreDebtTool.PAY_TOHER3);
       double payOther4 = EKTpreDebtTool.getInstance().getPayOther(parmEKT.getValue("MR_NO"), 
								EKTpreDebtTool.PAY_TOHER4);
       reEktFee = parmEKT.getDouble("CURRENT_BALANCE") - payOther3 - payOther4;
       this.setValue("RE_EKTFEE", reEktFee);
     //�˿���
       this.setValue("UN_FEE", reEktFee);
       this.setValue("RE_LPK", payOther3);
       this.setValue("RE_DJQ", payOther4);
       //��Ƭ���
       this.setValue("SEQ", parmEKT.getValue("SEQ"));
       //callFunction("UI|CARD_CODE|setEnabled", false); //���Ų��ɱ༭
       this.setValue("MR_NO", parmEKT.getValue("MR_NO"));

       onQuery();
      
       //====201202026 zhangp modify start
       grabFocus("UN_FEE");
       //====201202026 zhangp modify end
   }

   /**
    * ����
    */
   public void onSave() {
	   
//	   if(execAmt > 0){
//		   this.messageBox("��δ���˵����ݣ������˷�");
//		   return;
//	   }
	   
	   
       //TParm parm=new TParm();
       if (null == parmEKT || parmEKT.getErrCode() < 0 ||
           parmEKT.getValue("MR_NO").length() <= 0) {
           this.messageBox("��ȡҽ�ƿ�ʧ��");
           parmEKT = null;
           return;
       }
       if(!ReadKET()){
            this.messageBox("��鿴ҽ�ƿ��Ƿ�����");
             return;
       }
       TParm parm=new TParm();
       parm.setData("MR_NO", pat.getMrNo());
       parm = EKTTool.getInstance().selectEKTIssuelog(parm);
       if (parm.getCount() <= 0) {
           this.messageBox("�˲���û��ҽ�ƿ���Ϣ,�������ƿ�");
           return;
       }

       if (this.getValueDouble("UN_FEE") <=0) {
           this.messageBox("�˿����ȷ");
           return;
       }
       
       if (((TTextFormat)this.getComponent("GATHER_TYPE")).getText().length() <= 0) {
          this.messageBox("�˷ѷ�ʽ������Ϊ��ֵ");
          return;
       }
       
       if(tempFee()==-1)
           return;
       
       
      
       
       //���֧����ʽΪˢ��ʱ������Ϊ������add by huangjw 20150115
		if("C1".equals(this.getValue("GATHER_TYPE"))){
			if("".equals(this.getValue("CARD_TYPE"))||this.getValue("CARD_TYPE")==null){
				this.messageBox("�����Ͳ���Ϊ��");
				return;
			}
			if("".equals(this.getValue("DESCRIPTION"))){
				this.messageBox("��ע����Ϊ��");
				return;
			}
		}
		 //���֧����ʽΪ΢�Ż�֧���������׺ű���
			if("WX".equals(this.getValue("GATHER_TYPE"))||"ZFB".equals(this.getValue("GATHER_TYPE"))){
				if(this.getValue("BIL_CODE").toString().length()<=0){
				this.messageBox("΢�Ż�֧��������Ʊ�ݺ��봦��д���׺ţ�");
				return;
			}
			}
		
       TParm r = (TParm) openDialog(
               "%ROOT%\\config\\ekt\\EKTPassWordSure.x", parmEKT);
       if (r == null || null== r.getValue("FLG")) {
           return;
       }


       if (r.getValue("FLG").equals("Y"))
           onFEE();
       //parm.setData("CARD_NO", parmEKT.getValue("MR_NO")+parmEKT.getValue("SEQ"));
   }

   /**
    * ��ѯ������Ϣ
    */
   public void onQuery() {
       pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
       if (pat == null) {
           this.messageBox("�޴˲�����Ϣ");
           this.grabFocus("PAT_NAME");
           this.setValue("MR_NO", "");
           callFunction("UI|MR_NO|setEnabled", true); //�����ſɱ༭
           return;
       }
       //������
       this.setValue("MR_NO", pat.getMrNo());
       //����
       this.setValue("PAT_NAME", pat.getName());
       //��������
       setValue("BIRTH_DATE", pat.getBirthday());
       //�Ա�
       setValue("SEX_CODE", pat.getSexCode());
       callFunction("UI|MR_NO|setEnabled", false); //�����ſɱ༭
     //===zhangp 20120328 start
       TParm parm=new TParm();
       parm.setData("MR_NO",pat.getMrNo());
       TParm EKTparm= EKTTool.getInstance().selectEKTIssuelog(parm);
       if (EKTparm.getCount() <= 0) {
       	messageBox("��ҽ�ƿ�");
       	return;
       }
       this.setValue("OLD_EKTFEE",StringTool.round(EKTparm.getDouble("CURRENT_BALANCE", 0),2) );
       //duzhw
       //���˽�� duzhw add 20140408
       double payOther3 = EKTpreDebtTool.getInstance().getPayOther(this.getValueString("MR_NO"), 
    		   					EKTpreDebtTool.PAY_TOHER3);
       double payOther4 = EKTpreDebtTool.getInstance().getPayOther(this.getValueString("MR_NO"), 
								EKTpreDebtTool.PAY_TOHER4);
       
//       execAmt = EKTpreDebtTool.getInstance().getExecAmt(this.getValueString("MR_NO"));
       
       reEktFee = this.getValueDouble("OLD_EKTFEE") - payOther3 - payOther4;
       this.setValue("RE_EKTFEE", reEktFee);
       //===zhangp 20120328 end
       
       //20120104 zhangp
       if(this.getValueString("MR_NO")!=null&&!this.getValueString("MR_NO").equals("")){
    	   onTable();
    	   onTable1();
       }
   }

   /**
    * ��ֵ�ı���س��¼�
    */
   public void addFee() {
       if (this.getValueDouble("UN_FEE") < 0) {
           this.messageBox("�˿������Ϊ��ֵ");
           return;
       }
       double tempFee=tempFee();
       this.setValue("SUM_EKTFEE",
    		   tempFee==-1?0.00:tempFee);
      
//       double tempFee2=tempFee2();
//       this.setValue("RE_EKTFEE",
//    		   tempFee2==-1?0.00:tempFee2);
       
       
   }
   /**
    * �˷ѽ����Դ��ڿ������
    * @return boolean
    */
   private double tempFee2() {
       
       //duzhw
       double reEktFee2 = reEktFee -
       					 this.getValueDouble("UN_FEE");
       
       if (reEktFee2 < 0) {
           this.messageBox("�˿�����Գ������˽��");
           return -1;
       }
       this.setValue("RE_EKTFEE", reEktFee2);
       return reEktFee2;
   }
   
   /**
    * �˷ѽ����Դ��ڿ������
    * @return boolean
    */
   private double tempFee() {
       double tempFee = this.getValueDouble("OLD_EKTFEE") -
                        this.getValueDouble("UN_FEE");
       
       if(EKTpreDebtTool.PAY_TOHER3.equals(this.getValue("GATHER_TYPE"))){
    	   
    	   if(this.getValueDouble("UN_FEE") > this.getValueDouble("RE_LPK")){
    		   this.messageBox("�˿�����Գ�����Ʒ�����˽��");
    	       return -1;
    	   }
    		   
       }else if(EKTpreDebtTool.PAY_TOHER4.equals(this.getValue("GATHER_TYPE"))){
    	   
    	   if(this.getValueDouble("UN_FEE") > this.getValueDouble("RE_DJQ")){
    		   this.messageBox("�˿�����Գ�������ȯ���˽��");
    	       return -1;
    	   }
    	   
       }else {
    	   
    	   if(this.getValueDouble("UN_FEE") > this.getValueDouble("RE_EKTFEE")){
    		   this.messageBox("�˿�����Գ������˽��");
    	       return -1;
    	   }
    	   
       }
     
       
       if (tempFee < 0) {
           this.messageBox("�˿�����Գ������ڽ��");
           return -1;
       }
       return tempFee;
   }
   /**
    * �˷ѷ���
    */
   private void onFEE() {
       TParm result = null;
//       TTable table = (TTable)callFunction("UI|TABLE|getThis");//add by sunqy 20140611
//		int selectrow = table.getSelectedRow();//add by sunqy 20140611
           parmSum = new TParm();
           parmSum.setData("CARD_NO", pat.getMrNo() + this.getValue("SEQ"));
           parmSum.setData("ACOUNT_NO",parmEKT.getValue("CARD_NO"));//add by sunqy 20140611
           parmSum.setData("CURRENT_BALANCE", StringTool.round(parmEKT.getDouble("CURRENT_BALANCE"),
                   2)-
                   StringTool.round(this.getValueDouble("UN_FEE"), 2));
           parmSum.setData("CASE_NO", "none");
           parmSum.setData("NAME", pat.getName());
           parmSum.setData("MR_NO", pat.getMrNo());
           parmSum.setData("ID_NO",
                     null != pat.getIdNo() && pat.getIdNo().length() > 0 ?
                     pat.getIdNo() : "none");
           parmSum.setData("OPT_USER", Operator.getID());
           parmSum.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
           parmSum.setData("OPT_TERM", Operator.getIP());
           parmSum.setData("FLG", ektFlg);
           parmSum.setData("ISSUERSN_CODE", "�˷�"); //����ԭ��
           parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE")); //֧����ʽ
           parmSum.setData("GATHER_TYPE_NAME", this.getText("GATHER_TYPE")); //֧����ʽ����
           parmSum.setData("BUSINESS_AMT",
                           StringTool.round(this.getValueDouble("UN_FEE"), 2)); //��ֵ���
           parmSum.setData("SEX_TYPE", this.getValue("SEX_CODE")); //�Ա�
           parmSum.setData("CARD_TYPE",this.getValue("CARD_TYPE"));//������ add by huangjw 20150303
           parmSum.setData("DESCRIPTION", this.getValue("DESCRIPTION")); //��ע
          // parmSum.setData("BIL_CODE", this.getValue("BIL_CODE")); //Ʊ�ݺ�
           parmSum.setData("PRINT_NO", this.getValue("BIL_CODE")); //Ʊ�ݺ�
           parmSum.setData("CREAT_USER", Operator.getID()); //ִ����Ա//=====yanjing
           //��ϸ�����
           TParm feeParm = new TParm();
           feeParm.setData("ORIGINAL_BALANCE",
                           StringTool.round(parmEKT.getDouble("CURRENT_BALANCE"),2)); //ԭ���
           feeParm.setData("BUSINESS_AMT",StringTool.round(this.getValueDouble("UN_FEE"), 2)); //�˷ѽ��
           feeParm.setData("CURRENT_BALANCE",
                           StringTool.round(parmEKT.
                                            getDouble("CURRENT_BALANCE"), 2) -
                           StringTool.round(this.getValueDouble("UN_FEE"),
                                            2));
           parmSum.setData("businessParm", getBusinessParm(parmSum, feeParm).getData());
           //zhangp 20120109 EKT_BIL_PAY ���ֶ�
           parmSum.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());	//�ۿ�����ʱ��
           parmSum.setData("PROCEDURE_AMT", 0.00);	//PROCEDURE_AMT
           //bil_pay ��ֵ������
           parmSum.setData("billParm", getBillParm(parmSum, feeParm).getData());

           //�������
           result = TIOM_AppServer.executeAction(
                   "action.ekt.EKTAction",
                   "TXEKTonFee", parmSum); //
           if (result.getErrCode() < 0) {
               this.messageBox("ҽ�ƿ��˷�ʧ��");
		} else {
			//add by huangtt 20131225 start
        /*	TParm parmAmt = new TParm();
        	parmAmt.setData("MR_NO", pat.getMrNo());
        	parmAmt.setData("SEQ", this.getValue("SEQ"));
        	parmAmt.setData("CURRENT_BALANCE", feeParm.getData("CURRENT_BALANCE"));
        	try {
				EKTIO.getInstance().TXwriteEKTATM(parmAmt, pat.getMrNo());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				this.messageBox("ҽ�ƿ��˷�ʧ��");
				e1.printStackTrace();
				return;
			}*/
        	//add by huangtt 20131225 end
			this.messageBox("�˷ѳɹ�");
			onReadEKT();
			TTable table = (TTable) this.callFunction("UI|TABLE|getThis");
			
			table.setSelectedRow(table.getRowCount()-1);
			printBil = true;
			String bil_business_no = result.getValue("BIL_BUSINESS_NO");// �վݺ�
			try {
				onPrint(bil_business_no, "");
			} catch (Exception e) {
				this.messageBox("��ӡ��������,��ִ�в�ӡ����");
				// TODO: handle exception
			}
		}
       //zhangp 20120131 �˷���ɺ����²�ѯ�˷Ѽ�¼
       onTable();
       onClear();
   }

   /**
    * ҽ�ƿ���ϸ���������
    * @param p TParm
    * @param feeParm TParm
    * @return TParm
    */
   private TParm getBusinessParm(TParm p, TParm feeParm) {
       // ��ϸ������
       TParm bilParm = new TParm();
       bilParm.setData("BUSINESS_SEQ", 0);
       bilParm.setData("CARD_NO", p.getValue("CARD_NO"));
       bilParm.setData("MR_NO", pat.getMrNo());
       bilParm.setData("CASE_NO", "none");
       bilParm.setData("ORDER_CODE", p.getValue("ISSUERSN_CODE"));
       bilParm.setData("RX_NO", p.getValue("ISSUERSN_CODE"));
       bilParm.setData("SEQ_NO", 0);
       bilParm.setData("CHARGE_FLG", "7"); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����,6���ϣ�7��ҽ�ƿ��˷�)
       bilParm.setData("ORIGINAL_BALANCE", feeParm.getValue("ORIGINAL_BALANCE")); //�շ�ǰ���
       bilParm.setData("BUSINESS_AMT", feeParm.getValue("BUSINESS_AMT"));
       bilParm.setData("CURRENT_BALANCE", feeParm.getValue("CURRENT_BALANCE"));
       bilParm.setData("CASHIER_CODE", Operator.getID());
       bilParm.setData("BUSINESS_DATE", TJDODBTool.getInstance().getDBTime());
       //1������ִ�����
       //2��˫��ȷ�����
       bilParm.setData("BUSINESS_STATUS", "1");
       //1��δ����
       //2�����˳ɹ�
       //3������ʧ��
       bilParm.setData("ACCNT_STATUS", "1");
       bilParm.setData("ACCNT_USER", new TNull(String.class));
       bilParm.setData("ACCNT_DATE", new TNull(Timestamp.class));
       bilParm.setData("OPT_USER", Operator.getID());
       bilParm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
       bilParm.setData("OPT_TERM", Operator.getIP());
       // p.setData("bilParm",bilParm.getData());
       return bilParm;
   }

   /**
    * ��ֵ��������ݲ���
    * @param parm TParm
    * @return TParm
    */
   private TParm getBillParm(TParm parm, TParm feeParm) {
       TParm billParm = new TParm();
       billParm.setData("CARD_NO", parm.getValue("CARD_NO")); //����
       billParm.setData("CURT_CARDSEQ", 0); //���
       billParm.setData("ACCNT_TYPE", "6"); //��ϸ�ʱ�(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�)
       billParm.setData("MR_NO", parm.getValue("MR_NO")); //������
       billParm.setData("ID_NO", parm.getValue("ID_NO")); //���֤��
       billParm.setData("NAME", parm.getValue("NAME")); //��������
       billParm.setData("AMT", feeParm.getValue("BUSINESS_AMT")); //�˷ѽ��
       billParm.setData("CREAT_USER", Operator.getID()); //ִ����Ա
       billParm.setData("OPT_USER", Operator.getID()); //������Ա
       billParm.setData("OPT_TERM", Operator.getIP()); //ִ��ip
       billParm.setData("GATHER_TYPE", parm.getValue("GATHER_TYPE")); //֧����ʽ
	   //zhangp 20120109
       billParm.setData("STORE_DATE", parm.getData("STORE_DATE"));
       billParm.setData("PROCEDURE_AMT", parm.getData("PROCEDURE_AMT"));
       return billParm;
   }

   private boolean ReadKET() {
       //��ȡҽ�ƿ�
       TParm parm = EKTIO.getInstance().TXreadEKT();
       if (null == parm || parm.getErrCode() < 0 ||
           parm.getValue("MR_NO").length() <= 0) {
           return false;
       }
       return true;
   }
   /**
    *���
    */
   public void onClear() {
       clearValue(" MR_NO;PAT_NAME;SEQ; " +
                  " BIRTH_DATE;SEX_CODE;UN_FEE; " +
                  " BIL_CODE;DESCRIPTION; " +
                  " OLD_EKTFEE;SUM_EKTFEE;RE_EKTFEE;RE_LPK;RE_DJQ");
       //����Ĭ�Ϸ���ȼ�
      // txEKT = false; //̩��ҽ�ƿ�д���ܿ�
       parmEKT = null; //ҽ�ƿ�����parm
       ektFlg=false;
       printBil=false;
       printBil=false;//��ӡƱ��ʱʹ��
       parmSum=null;//ִ���˿��������
       //��������
       if (pat != null)
           PatTool.getInstance().unLockPat(pat.getMrNo());
       String id = EKTTool.getInstance().getPayTypeDefault();
       setValue("GATHER_TYPE", id);
       //===zhangp 20120328 start
       callFunction("UI|MR_NO|setEnabled", true); //�����ſɱ༭
       //===zhangp 20120328 end
       TTable table = (TTable) getComponent("TABLE");    
       table.removeRowAll();
       TTable table1 = (TTable) getComponent("TABLE1");    
       table1.removeRowAll();
   }
   /**
    * �˿��ӡ
    */
   private void onPrint(String bil_business_no,String copy) {
	   /**
       if (!printBil) {
           this.messageBox("����ҽ�ƿ���ֵ�����ſ��Դ�ӡ");
           return;
       }
       TParm parm = new TParm();
       parm.setData("TITLE", "TEXT",
                    (Operator.getRegion() != null &&
                     Operator.getRegion().length() > 0 ?
                     Operator.getHospitalCHNFullName() : "����ҽԺ"));
       parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); //������
       parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); //����
       //====zhangp 20120525 start
//       parm.setData("GATHER_NAME", "TEXT", "�� ��"); //�˿ʽ
       parm.setData("GATHER_NAME", "TEXT", "��"); //�տʽ
     //====zhangp 20120525 end
       parm.setData("TYPE", "TEXT", "Ԥ ��"); //�ı�Ԥ�˽��
       parm.setData("GATHER_TYPE", "TEXT", parmSum.getValue("GATHER_TYPE_NAME")); //�տʽ
       parm.setData("AMT", "TEXT", StringTool.round(parmSum.getDouble("BUSINESS_AMT"),2)); //���
       parm.setData("SEX_TYPE", "TEXT", parmSum.getValue("SEX_TYPE").equals("1")?"��":"Ů"); //�Ա�
       parm.setData("AMT_AW", "TEXT", StringUtil.getInstance().numberToWord(parmSum.getDouble("BUSINESS_AMT"))); //��д���
       parm.setData("TOP1", "TEXT", "EKTRT001 FROM "+Operator.getID()); //̨ͷһ
       String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
               getInstance().getDBTime()), "yyyyMMdd"); //������
       String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
               getInstance().getDBTime()), "hhmmss"); //ʱ����
       parm.setData("TOP2", "TEXT", "Send On " + yMd + " At " + hms); //̨ͷ��
       yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
               getDBTime()), "yyyy/MM/dd"); //������
       hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "HH:mm"); //ʱ����
       parm.setData("DESCRIPTION", "TEXT", parmSum.getValue("DESCRIPTION")); //��ע
       parm.setData("BILL_NO", "TEXT", parmSum.getValue("BIL_CODE")); //Ʊ�ݺ�
       if(null == bil_business_no)
            bil_business_no=  EKTTool.getInstance().getBillBusinessNo();//��ӡ����
       parm.setData("ONFEE_NO", "TEXT", bil_business_no); //�վݺ�
       parm.setData("PRINT_DATE", "TEXT", yMd); //��ӡʱ��
       parm.setData("DATE", "TEXT", yMd + "    " + hms); //����
       parm.setData("USER_NAME", "TEXT", Operator.getID()); //�տ���
       parm.setData("COPY", "TEXT", copy); //��ӡע��
       //===zhangp 20120525 start
       parm.setData("O", "TEXT", "o"); 
//       this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_UNFEE.jhw", parm,true);
       this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm ,true);
       //===zhangp 20120525 end
       */
	 //modify by sunqy 20140611 ----start----
		if (!printBil) { 
			 this.messageBox("����ҽ�ƿ���ֵ�����ſ��Դ�ӡ"); 
			 return; 
		 } 
		TTable table = (TTable) this.callFunction("UI|TABLE|getThis");
		int row = table.getSelectedRow();// �õ�ѡ����
		parmSum.setData("BIL_BUSINESS_NO",table.getParmValue().getValue("BIL_BUSINESS_NO",row));
//		if(row<0){
//			this.messageBox("û��ѡ������!");
//			return;
//		}
//		EKTReceiptPrint print = new EKTReceiptPrint();
		boolean flg = true;
		parmSum.setData("UnFeeFLG", "Y");//����Ƿ�Ϊ�˷���ز���
		parmSum.setData("TITLE", "�����˷��վ�");
		EKTReceiptPrintControl.getInstance().onPrint(table, parmSum, copy, row, pat, flg, this);
//		print.onPrint(table, parmSum, copy, row, pat,flg);
		//modify by sunqy 20140611 ----end----
   }
   /**
    * ��ӡ 
    * 20111228 zhangp
    */
   public void onRePrint(String copy){
	   /**
	   TTable table = (TTable)this.callFunction("UI|TABLE|getThis");
 		row = table.getSelectedRow();
	   if(row==-1){
		   messageBox("��ѡ��Ҫ��ӡ�ļ�¼");
		   return;
	   }else{
	  		String bilBusinessNo = table.getValueAt(row, 1).toString();
	  		String sql = "SELECT MR_NO FROM EKT_BIL_PAY WHERE BIL_BUSINESS_NO = '"+bilBusinessNo+"'";
	  		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
	  		String mrNo = result.getData("MR_NO", 0).toString();
	  		pat = pat.onQueryByMrNo(mrNo);
		   TParm parm = new TParm();
	       parm.setData("TITLE", "TEXT",
	                    (Operator.getRegion() != null &&
	                     Operator.getRegion().length() > 0 ?
	                     Operator.getHospitalCHNFullName() : "����ҽԺ"));
	       parm.setData("MR_NO", "TEXT", mrNo); //������
	       parm.setData("PAT_NAME", "TEXT", pat.getName()); //����
	       //====zhangp 20120525 start
//	       parm.setData("GATHER_NAME", "TEXT", "�� ��"); //�˿ʽ
	       parm.setData("GATHER_NAME", "TEXT", "��"); //�տʽ
	       //====zhangp 20120525 end
	       parm.setData("TYPE", "TEXT", "Ԥ ��"); //�ı�Ԥ�˽��
	       String gatherType = table.getValueAt(row, 4).toString();
	 	      String sqlgt =
	 	            " SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_DICTIONARY WHERE GROUP_ID='GATHER_TYPE' AND ID = '"+gatherType+"' ORDER BY SEQ,ID ";
	 	      TParm resultgt = new TParm(TJDODBTool.getInstance().select(sqlgt));
	 	       parm.setData("GATHER_TYPE", "TEXT", resultgt.getData("NAME", 0).toString()); //�տʽ
	       parm.setData("AMT", "TEXT", StringTool.round((Double)table.getValueAt(row, 3),2)); //���
	       parm.setData("SEX_TYPE", "TEXT", pat.getSexString()); //�Ա�
	       parm.setData("AMT_AW", "TEXT", StringUtil.getInstance().numberToWord((Double)table.getValueAt(row, 3))); //��д���
	       parm.setData("TOP1", "TEXT", "EKTRT001 FROM "+Operator.getID()); //̨ͷһ
	       String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
	               getInstance().getDBTime()), "yyyyMMdd"); //������
	       String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
	               getInstance().getDBTime()), "hhmmss"); //ʱ����
	       parm.setData("TOP2", "TEXT", "Send On " + yMd + " At " + hms); //̨ͷ��
	       yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
	               getDBTime()), "yyyy/MM/dd"); //������
	       hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
	               getDBTime()), "hh:mm"); //ʱ����
	       parm.setData("DESCRIPTION", "TEXT", ""); //��ע
	       parm.setData("BILL_NO", "TEXT", ""); //Ʊ�ݺ�
	       parm.setData("ONFEE_NO", "TEXT", bilBusinessNo); //�վݺ�
	       parm.setData("PRINT_DATE", "TEXT", yMd); //��ӡʱ��
	       parm.setData("DATE", "TEXT", yMd + "    " + hms); //����
	       parm.setData("USER_NAME", "TEXT", Operator.getID()); //�տ���
	       parm.setData("COPY", "TEXT", "(copy)"); //��ӡע��
	       //===zhangp 20120525 start
	       parm.setData("O", "TEXT", "o"); 
//	       this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_UNFEE.jhw", parm,true);
	       this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm ,true);
	       //===zhangp 20120525 end
	       */
	 //add by sunqy 20140611 ----start----
		TTable table = (TTable) this.callFunction("UI|TABLE|getThis");
		int row = table.getSelectedRow();// �õ�ѡ����
		if(row<0){
			this.messageBox("û��ѡ������!");
			return;
		}
		copy = "(copy)";
		String bilBusinessNo = table.getValueAt(row, 1).toString();
		String sql = "SELECT A.*,B.PAT_NAME NAME,C.EKT_CARD_NO ACOUNT_NO " +
				"FROM EKT_BIL_PAY A, SYS_PATINFO B,EKT_ISSUELOG C " +
				"WHERE A.BIL_BUSINESS_NO = '" + bilBusinessNo 
				+"' AND A.MR_NO = B.MR_NO AND A.CARD_NO=C.CARD_NO";
//		this.messageBox("֧����ʽ=="+table.getValueAt(row, 4));
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		result = result.getRow(0);
//		EKTReceiptPrint print = new EKTReceiptPrint();
		boolean flg = true;
		result.setData("UnFeeFLG", "Y");//����Ƿ�Ϊ�˷���ز���
		result.setData("TITLE", "�����˷��վ�");
		EKTReceiptPrintControl.getInstance().onPrint(table, result, copy, row, pat, flg, this);
//		print.onPrint(table, result, copy, row, pat, flg);
		//add by sunqy 20140611 ----end----
//	   }
    }
   /**
    *ҽ�ƿ���ֵ�˷Ѽ�¼table
    *zhangp 20111228
    */
   public void onTable(){
	   String mrNo = getValueString("MR_NO");
	   StringBuilder sql = new StringBuilder();
	   String select = "SELECT B.EKT_CARD_NO,A.BIL_BUSINESS_NO," +
	   		"A.OPT_DATE,A.AMT,A.GATHER_TYPE,A.CREAT_USER,A.ACCNT_TYPE FROM EKT_BIL_PAY A, " +
	   		"EKT_ISSUELOG B where A.CARD_NO = B.CARD_NO AND " +
	   		"A.ACCNT_TYPE = '6'";
	   sql.append(select);
	   if(!mrNo.equals("")&&mrNo!=null){
		   sql.append(" AND A.MR_NO = '"+mrNo+"'");
	   }
	   sql.append(" ORDER BY A.OPT_DATE");
	   TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
       if (result.getErrCode() < 0) {
         messageBox(result.getErrText());
         return;
       }
       ((TTable)getComponent("TABLE")).setParmValue(result);
   }
   
   /**
	 *ҽ�ƿ���ֵ��¼table zhangp 20111228
	 */
	public void onTable1() {
		String mrNo = getValueString("MR_NO");
		StringBuilder sql = new StringBuilder();
		String select = "SELECT B.EKT_CARD_NO,A.BIL_BUSINESS_NO,"
				+ "A.OPT_DATE,A.AMT,A.GATHER_TYPE,A.CREAT_USER,A.ACCNT_TYPE, "
				+" A.DESCRIPTION"//wangjingchun add 860
				+" FROM EKT_BIL_PAY A, "
				+ "EKT_ISSUELOG B where A.CARD_NO = B.CARD_NO AND "
				+ "A.ACCNT_TYPE = '4'";
		sql.append(select);
		if (!mrNo.equals("") && mrNo != null) {
			sql.append(" AND A.MR_NO = '" + mrNo + "'");
		}
		sql.append(" ORDER BY A.OPT_DATE DESC");
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		((TTable) getComponent("TABLE1")).setParmValue(result);
	}
   
   /**
    * ��ѯ��������
    * =====zhangp 20120328
    */
   public void onQueryPatName(){
   	TParm sendParm = new TParm();
       sendParm.setData("PAT_NAME", this.getValue("PAT_NAME"));
       TParm reParm = (TParm)this.openDialog(
           "%ROOT%\\config\\adm\\ADMPatQuery.x", sendParm);
       if(reParm==null)
           return;
       this.setValue("MR_NO", reParm.getValue("MR_NO"));
       this.onQuery();
   }
   
}
