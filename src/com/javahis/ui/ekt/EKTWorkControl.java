package com.javahis.ui.ekt;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JOptionPane;

import jdo.ekt.EKTIO;
import jdo.ekt.EKTTool;
import jdo.mem.MEMSQL;
import jdo.sid.IdCardO;
import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import jdo.sys.PATLockTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SYSPostTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ҽ�ƿ�����</p>
 *
 * <p>Description: ҽ�ƿ�����</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110928
 * @version 1.0
 */
public class EKTWorkControl extends TControl{
	
	private Pat  pat;//  ������Ϣ
	   private TParm parmMem; //��Ա��Ϣ      huangtt  20140109
	   //private boolean txEKT=false;//ҽ�ƿ���ȡд������
	   private TParm parmEKT;//ҽ�ƿ�����
	   private boolean ektFlg=false;//ҽ�ƿ�����:true:��һ�δ���
	   private TParm EKTTemp;//ҽ�ƿ����ֵ
	   private TParm parmSum;//ִ���˿��������
	   private boolean bankFlg=false;//���п���������
	   
	   /**
	    * zhangp 20121216 ���벡����
	    */
	   private TParm acceptData = new TParm(); //�Ӳ�

	   //20120113 zhangp �½����ж� 1�� 2��
	   private int newFlag = 2;


	   
    public EKTWorkControl() {
    }
    /**
    * ��ʼ������
    */
   public void onInit() {
//	   //add by huangtt 20140217 start
//	   callFunction("UI|MEM_TYPE|setEnabled", false);
//	   callFunction("UI|START_DATE|setEnabled", false);
//	   callFunction("UI|END_DATE|setEnabled", false);
//	   callFunction("UI|MEM_GATHER_TYPE|setEnabled", false);
//	   callFunction("UI|FEEs|setEnabled", false);
//	   callFunction("UI|MEM_DESCRIPTION|setEnabled", false);
//	 //add by huangtt 20140217 end
      setValue("CTZ1_CODE", "99");
      /**
       * zhangp 20121216 ����Ĭ���й�
       */
      setValue("NATION_CODE", "86");
      /**
       * zhangp 20121216 ���벡����
       */
      Object obj = this.getParameter();
      if (obj instanceof TParm) {
          acceptData = (TParm) obj;
          String mrNo = acceptData.getData("MR_NO").toString();
          //zhangp 20120113
          this.setValue("MR_NO", mrNo);
          this.onQueryNO();
      }
      //zhangp 20111227 ������Ĭ��ֵ
            TParm result = EKTTool.getInstance().sendCause();
      if(result.getCount()>0){
    	  setValue("SEND_CAUSE", result.getData("ID", 0));
      }
      //zhangp 20120201 ������
      result = EKTTool.getInstance().factageFee(getValue("SEND_CAUSE").toString());
      if(result.getCount()>0){
    	  setValue("PROCEDURE_PRICE", result.getDouble("FACTORAGE_FEE", 0));
      }
      //=====zhangp 20120224 ֧����ʽ    modify start 
      String id = EKTTool.getInstance().getPayTypeDefault();
      setValue("GATHER_TYPE", id);
      setPassWord();
      //=======zhangp 20120224 modify end
//      setValue("MEM_GATHER_TYPE", id);


  

   }
	   
   /**
    * ��ѯ����
    */
   public void onQuery(){
	   if(this.getValueString("MR_NO").length()==0){
		   String patName = this.getValueString("PAT_NAME");
		   String sexCode = this.getValueString("SEX_CODE");
		   String idNo = this.getValueString("IDNO");
		   if(patName.length() > 0 && sexCode.length() == 0){
			   this.messageBox("����д�Ա���Ϣ,�Ա��ѯ!"); 
			   return;
		   }
		   if(patName.length() == 0 && sexCode.length() > 0){
			   this.messageBox("����д������Ϣ,�Ա��ѯ!"); 
			   return;
		   }
		   if(patName.length() == 0 && sexCode.length() == 0 && idNo.length() == 0){
			   this.messageBox("����д��ѯ��Ϣ");
			   return;
		   }
		   TParm parm = new TParm();
		   parm.setData("PAT_NAME", patName);
		   parm.setData("SEX_CODE", sexCode);
		   parm.setData("IDNO", idNo); 
		   TParm result = EKTTool.getInstance().getPatInfo(parm);
		   if(result.getCount()<0){
			   this.messageBox("û��Ҫ��ѯ������!");
			   return;
		   }
		   if(result.getCount() == 1){
			   this.setValue("MR_NO", result.getValue("MR_NO", 0));
			   
		   }else{
			   Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
					this.setValue("MR_NO", patParm.getValue("MR_NO"));
				}
		   }
		   if(this.getValueString("MR_NO").length() == 0){
			  return;   
		   }
	   }

	   onQueryNO(true); 
       
   }

   /**
    * �������ı����¼�
    */
   public void onQueryNO() {
       onQueryNO(true);
   }
   
   /**
    * �Һ�ҳ��
    */
   public void onReg(){
	   
       TParm sendParm = new TParm();
       sendParm.setData("MR_NO", this.getValue("MR_NO"));
       sendParm.setData("ADM_TYPE", "O");
       sendParm.setCount(sendParm.getCount());
       TParm reParm = (TParm)this.openDialog(
           "%ROOT%\\config\\reg\\REGPatAdm.x", sendParm);
       
   }

   /**
    * ��ѯ����
    */
   public void onQueryNO(boolean flg) {
//       if (pat != null)
//           PatTool.getInstance().unLockPat(pat.getMrNo());
       pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
       if (pat == null) {
           this.messageBox("�޴˲�����!");
           this.grabFocus("PAT_NAME");
           if(!flg){
               this.setValue("MR_NO", "");
               callFunction("UI|MR_NO|setEnabled", false); //�����ſɱ༭
           }
           return;
       }
       
     

       setValue("MR_NO",
                 PatTool.getInstance().checkMrno(TypeTool.getString(
                         getValue("MR_NO"))));
        setValue("PAT_NAME", pat.getName());
        setValue("PAT_NAME1", pat.getName1());
        setValue("FIRST_NAME", pat.getFirstName());
		setValue("LAST_NAME", pat.getLastName());
        setValue("PY1", pat.getPy1());
        setValue("IDNO", pat.getIdNo());
      //add by huangtt 20131106  start
        setValue("ID_TYPE", pat.getIdType());  
        setValue("CURRENT_ADDRESS", pat.getCurrentAddress());
        setValue("REMARKS", pat.getRemarks());
        setValue("SPECIES_CODE", pat.getSpeciesCode());
        setValue("MARRIAGE_CODE", pat.getMarriageCode());
      //add by huangtt 20131106  end
        setValue("NATION_CODE",pat.getNationCode());
      //zhangp 20111223 ����
        if(pat.getNationCode().equals("")||pat.getNationCode()==null){
     	   setValue("NATION_CODE", "86");
        }
//        setValue("FOREIGNER_FLG", pat.isForeignerFlg());
        setValue("BIRTH_DATE", pat.getBirthday());
        setValue("SEX_CODE", pat.getSexCode());
        setValue("CELL_PHONE", pat.getCellPhone());
        setValue("POST_CODE", pat.getPostCode());
        onPost();
        setValue("RESID_ADDRESS", pat.getResidAddress());
        setValue("CTZ1_CODE", pat.getCtz1Code());
        setValue("CTZ2_CODE", pat.getCtz2Code());
        setValue("CTZ3_CODE", pat.getCtz3Code());
//        setValue("REG_CTZ3", getValue("CTZ3_CODE"));
        //=====zhangp 20120227 modify start
//        patLock();
        //=====zhangp 20120227 modify end
        //��������Ϣ
//            this.messageBox_("�����ɹ�!");//����ר��
        this.grabFocus("onFee");
        callFunction("UI|MR_NO|setEnabled", false); //�����Ų��ɱ༭
        

        
        TParm parm=new TParm();
       parm.setData("MR_NO",pat.getMrNo());
       parmEKT= EKTTool.getInstance().selectEKTIssuelog(parm);
       if (parmEKT.getCount() <= 0) {
//           this.messageBox("�˲���û��ҽ�ƿ���Ϣ");
           //�¿�����
           this.setValue("CARD_CODE", pat.getMrNo() + "001");
           //������ҽ�ƿ���д��ʱ��������EKT_MASTER����Ϣ
           ektFlg = true;
       } else {
//           this.messageBox("�˲����Ѿ�����ҽ�ƿ���Ϣ");//=====zhangp 20120225 modify start
           this.setValue("CARD_CODE", parmEKT.getValue("CARD_NO", 0));
           this.setValue("EKT_CARD_NO", parmEKT.getValue("EKT_CARD_NO", 0));
           //zhangp 20111230
           setValue("EKTMR_NO", getValue("MR_NO"));
           setValue("EKTCARD_CODE", getValue("EKT_CARD_NO"));
           this.setValue("CURRENT_BALANCE",StringTool.round(parmEKT.getDouble("CURRENT_BALANCE", 0),2) );
           bankFlg=true;//���п�ִ�й���
            
       }
     //add by huangtt 20140119 start
       parmMem = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemInfo(this.getValueString("MR_NO"))));
       if(!parmMem.getValue("MEM_CODE", 0).equals("")){
    	   this.setValue("MEM_TYPE", parmMem.getValue("MEM_CODE", 0));
       }
//       System.out.println("��ԱSQL::"+MEMSQL.getMemTrade(getValueString("MR_NO")));
//       parmMem = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemTrade(getValueString("MR_NO"))));
//       if(parmMem.getCount()>0){
//    	   callFunction("UI|MEM_TYPE|setEnabled", true);
//    	   callFunction("UI|START_DATE|setEnabled", true);
//    	   callFunction("UI|END_DATE|setEnabled", true);
//    	   callFunction("UI|MEM_GATHER_TYPE|setEnabled", true);
//    	   callFunction("UI|FEEs|setEnabled", true);
//    	   callFunction("UI|MEM_DESCRIPTION|setEnabled", true);
//    	   
//    	  TCheckBox manageMem = (TCheckBox) this.getComponent("MANAGE_MEM");
//    	  manageMem.setSelected(true);
//    	  this.setValue("START_DATE", parmMem.getData("START_DATE", 0));
////    	  this.setValue("END_DATE", parmMem.getData("END_DATE", 0));
//    	  this.setValue("MEM_FEE", parmMem.getValue("MEM_FEE", 0));
//    	  this.setValue("MEM_TYPE", parmMem.getData("MEM_CODE", 0));
//    	  this.setValue("MEM_DESCRIPTION", parmMem.getData("DESCRIPTION", 0));
//    	  getMemCodeEndDate();
//    	  if(parmEKT.getCount()>0){
//    		  setValue("SEND_CAUSE", 8);
//        	  onSendCause();  //����ԭ�����
//    	  }
//    	 
//       }
       //add by huangtt 20140119 end
   }
   /**
    * �����ע���¼�
    */
   public void onSelForeieignerFlg() {
       if (this.getValue("FOREIGNER_FLG").equals("Y"))
           this.grabFocus("BIRTH_DATE");
       if (this.getValue("FOREIGNER_FLG").equals("N"))
           this.grabFocus("IDNO");
   }

   /**
    * �½�
    */
   public void onNew(){
//       if (!txReadEKT()) {
//           return;
//       }
       TParm parm = new TParm();
       String card= this.getValue("CARD_CODE").toString();
       /**
        * zhangp 20111216 ���ֶ�
        */
       String ektCardNo= this.getValue("EKT_CARD_NO").toString();
       if (pat == null || pat.getMrNo().length()<=0) {
           this.messageBox("���Ȳ�ѯ������Ϣ");
           return;
       }
       //ektFlg=false �������½�ҽ�ƿ���Ϣ������ִ��
       if(!ektFlg){
           this.messageBox("�˲�������ҽ�ƿ���Ϣ,��ִ�й�ʧ/��������");
           return ;
       }
     //����ǻ�Ա  �ƿ�ǰ�ж��Ƿ񽻻�� duzhw add 20140424
       if(!checkMemFee()){
    	   this.messageBox("����ɻ�Ա�Ѻ����ƿ���");
    	   return;
       }

//       if(card.length()!=15){
//           this.messageBox("�����������,�����ַ�����Ҫ��ʮ��λ");
//           return ;
//       }
       if (this.messageBox("�½���", "�Ƿ�ִ���ƿ�����", 0) != 0) {
           return;
       }
       //����
      if (this.getValue("CARD_PWD").toString().length() <= 0) {
          this.messageBox("����������");
          return;
      }
      //===zhangp 20120315 start
      if(!passWord()){
    	  return;
      }
      //===zhangp 20120315 end
       if(((TTextFormat)this.getComponent("SEND_CAUSE")).getText().length()<=0){
           this.messageBox("����ԭ�򲻿���Ϊ��ֵ");
           return;
       }
       TParm result=null;
       //add by huangtt 20140226 start
       TParm parmMem = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemTrade1(getValueString("MR_NO"))));
       if(parmMem.getCount()>0){
    	   card=parmMem.getValue("MEM_CARD_NO", 0);
    	   String seq = card.substring(this.getValueString("MR_NO").length());
    	   parm.setData("SEQ",seq); //��� 3λ
           parm.setData("CURRENT_BALANCE", 0.00); //���
           parm.setData("MR_NO", pat.getMrNo()); //������
           if(this.getValueString("MEM_TYPE").length()==1){
           	parm.setData("TYPE", "0"+this.getValue("MEM_TYPE"));  //�����  huangtt
           }else{
           	parm.setData("TYPE", this.getValue("MEM_TYPE"));  //�����  huangtt
           }
       }else{
    	   parm.setData("SEQ","001"); //��� 3λ
           parm.setData("CURRENT_BALANCE", 0.00); //���
           parm.setData("MR_NO", pat.getMrNo()); //������
           parm.setData("TYPE","00");  //�����  huangtt
       }
       //add by huangtt 20140226 end

//       result = EKTIO.getInstance().TXwriteEKT(parm);
       try {
//    	   System.out.println("parm=="+parm);
    	   EKTIO.getInstance().TXwriteEKT(parm);
       } catch (Exception e) {
    	   // TODO Auto-generated catch block
    	   this.messageBox("�½�ҽ�ƿ�����ʧ��,��鿴�������Ƿ�����");
    	   e.printStackTrace();
    	   return;
       }
//       if (result.getErrCode() < 0) {
//           this.messageBox("�½�ҽ�ƿ�����ʧ��,��鿴�������Ƿ�����");
//       } else{
           TParm p = new TParm();
           p.setData("CARD_NO",card); //����
           p.setData("MR_NO", pat.getMrNo()); //������
           p.setData("CARD_SEQ", parm.getValue("SEQ")); //���
           p.setData("ISSUE_DATE", TJDODBTool.getInstance().getDBTime()); //����ʱ��
           p.setData("ISSUERSN_CODE", this.getValue("SEND_CAUSE")); //����ԭ��
           p.setData("FACTORAGE_FEE", this.getValueDouble("PROCEDURE_PRICE")); //������
           p.setData("PASSWORD",  OperatorTool.getInstance().encrypt(this.getValue("CARD_PWD").toString())); //����
           p.setData("WRITE_FLG", "Y"); //д������ע��
           p.setData("OPT_USER", Operator.getID());
           p.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
           p.setData("OPT_TERM", Operator.getIP());
           p.setData("ID_NO", pat.getIdNo()); //���֤����
           p.setData("CASE_NO", "none"); //�����
           p.setData("NAME", pat.getName()); //��������
           p.setData("CURRENT_BALANCE", 0.00); //��Ĭ�Ͻ��
           p.setData("CREAT_USER", Operator.getID()); //������
           /**
            * zhangp 20111216 ���ֶ�
            */
           p.setData("EKT_CARD_NO", ektCardNo); //���ţ�����ӡ�ĺ��룩
           //zhangp 20120113 ע
//           if(ektFlg){
//               p.setData("CHARGE_FLG", "4"); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����)
//           } else {
//               p.setData("CHARGE_FLG", "5"); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����)
//           }
           //zhangp 20120113 �ж��Ƿ����case_no ������� ���� ������ �ƿ�
//   			String sql = "SELECT * FROM REG_PATADM WHERE MR_NO = '"+pat.getMrNo()+"'";
//   			TParm par = new TParm(TJDODBTool.getInstance().select(sql));
//   			if(par.getErrCode()<0){
//   				messageBox(par.getErrText());
//   			}
//   			if(par.getCount()<=0){
//   				newFlag = 1;
//   			}
           //�ƿ�
           if(newFlag == 1){
        	   p.setData("CHARGE_FLG", getValue("SEND_CAUSE").toString()); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����)
           }
           //����
           if(newFlag == 2){
        	   p.setData("CHARGE_FLG", getValue("SEND_CAUSE").toString()); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����)
           }
           //zhangp 20111222 EKT_BIL_PAY
           TParm bilParm = new TParm();          
           //===zhangp 20120322 start
           int accntType = 1;
           String sendCause = getValue("SEND_CAUSE").toString();
           if(sendCause.equals("4")){//�ƿ�
        	   accntType = 1;
           }
           if(sendCause.equals("5")){//����
        	   accntType = 3;
           }
           if(sendCause.equals("8")){//����
        	   accntType = 2;
           }
           bilParm.setData("ACCNT_TYPE", accntType);	//��ϸ�ʱ�(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�)(EKT_BIL_PAY)
           //===zhangp 20120322 end
           bilParm.setData("CURT_CARDSEQ", parm.getValue("SEQ"));	//��Ƭ���(EKT_BIL_PAY)
           bilParm.setData("GATHER_TYPE", getValue("GATHER_TYPE"));	//��ֵ��ʽ(EKT_BIL_PAY)
           bilParm.setData("AMT", "0");	//AMT(EKT_BIL_PAY)
           //zhangp 20120109 ���ֶ�
           bilParm.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());	//�ۿ�����ʱ��
           bilParm.setData("PRINT_NO", this.getValue("BIL_CODE"));	//���׺�
           bilParm.setData("PROCEDURE_AMT", this.getValueDouble("PROCEDURE_PRICE"));	//PROCEDURE_AMT
           p.setData("bilParm", getBilParm(p,bilParm).getData());
//           System.out.println("!!!!!!"+getBilParm(p,bilParm).getData());
           // ��ϸ������
           TParm feeParm = new TParm();
           feeParm.setData("ORIGINAL_BALANCE", 0.00);
           feeParm.setData("BUSINESS_AMT", 0.00);
           feeParm.setData("CURRENT_BALANCE", 0.00);
           p.setData("businessParm", getBusinessParm(p, feeParm).getData());
//           this.messageBox("!!!!!");
           result = TIOM_AppServer.executeAction(
                   "action.ekt.EKTAction",
                   "TXEKTRenewCard", p); //
           if (result.getErrCode() < 0) {
               this.messageBox("�½�ҽ�ƿ�����ʧ��");
               parm = new TParm();
               parm.setData("SEQ", "000"); //���
               parm.setData("CURRENT_BALANCE",0.00); //���
               parm.setData("MR_NO", "000000000000"); //������
               parm.setData("TYPE","00");  //�����  huangtt
               try {
				EKTIO.getInstance().TXwriteEKT(parm);
			} catch (Exception e) {
				// TODO Auto-generated catch block
//				System.out.println("��дҽ�ƿ�ʧ��");
				this.messageBox("��дҽ�ƿ�ʧ��");
				e.printStackTrace();
				return;
			}
//               parm = EKTIO.getInstance().TXwriteEKT(parm);
//               if (parm.getErrCode() < 0) {
//                 System.out.println("��дҽ�ƿ�ʧ��");
//             }

           } else {
               this.messageBox("�½�ҽ�ƿ������ɹ�");
               onClear();
           }
           //txEKT = true;
//       }
//       onEKTcard();
   }
   /**
    * ��Ƭ��ӡ
    */
   public void onPrint(){}
   /**
    * ҽ��������
    */
   public void onMRcard(){

   }
   /**
    * ҽ�ƿ�����
    */
   public void onEKTcard(){
       //��ȡҽ�ƿ�
       parmEKT = EKTIO.getInstance().TXreadEKT();
       if (null == parmEKT || parmEKT.getErrCode() < 0 ||
           parmEKT.getValue("MR_NO").length() <= 0) {
           this.messageBox(parmEKT.getErrText());
           parmEKT = null;
           return;
       }
       //��Ƭ���
       //this.setValue("CURRENT_BALANCE", parmEKT.getDouble("CURRENT_BALANCE"));
       //��Ƭ����
       this.setValue("CARD_CODE",
                     parmEKT.getValue("MR_NO") + parmEKT.getValue("SEQ"));
       //callFunction("UI|CARD_CODE|setEnabled", false); //���Ų��ɱ༭
       this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
       onQueryNO(false);
     //  txEKT = true;

   }

   /**
    * ǿ�Ƽ�������
    */
   private void patLock() {
       String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
       //�ж��Ƿ����
       if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
           if (this.messageBox("�Ƿ����",
                               PatTool.getInstance().getLockParmString(pat.
                   getMrNo()), 0) == 0) {
               PatTool.getInstance().unLockPat(pat.getMrNo());
               PATLockTool.getInstance().log("ODO->" +
                                             SystemTool.getInstance().getDate() +
                                             " " +
                                             Operator.getID() + " " +
                                             Operator.getName() +
                                             " ǿ�ƽ���[" + aa + " �����ţ�" +
                                             pat.getMrNo() + "]");
           } else {
               pat = null;
               return;
           }
       }

   }
   /**
    * �������֤
    * ============pangben 2013-3-18
    */
   public void onIdCard(){
	   TParm idParm=IdCardO.getInstance().readIdCard();
		this.messageBox(idParm.getValue("MESSAGE"));
		if(idParm.getCount()>0){//����������ʾ
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", idParm);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				this.setValue("MR_NO", patParm.getValue("MR_NO"));
				onQueryNO(true);
				this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
						TypeTool.getString(getValue("PAT_NAME"))));//��ƴ
				setPatName1();//����Ӣ��
			}

		}else{
			this.setValue("PAT_NAME", idParm.getValue("PAT_NAME"));
			this.setValue("IDNO", idParm.getValue("IDNO"));
			this.setValue("BIRTH_DATE", idParm.getValue("BIRTH_DATE"));
			this.setValue("SEX_CODE", idParm.getValue("SEX_CODE"));
			this.setValue("RESID_ADDRESS", idParm.getValue("RESID_ADDRESS"));//��ַ
			this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
					TypeTool.getString(getValue("PAT_NAME"))));//��ƴ
			setPatName1();//����Ӣ��
		}
   }
   /**
    * ��д��
    */
   public void onRenew(){
       //===pangben modify 20110916 ̩��ҽ�ƿ�д������
//       if(this.messageBox("��д��", "�Ƿ�ִ��д������", 0) != 0){
//           return;
//       }
//       if (null != pat && null != pat.getMrNo() && pat.getMrNo().length() > 0 && txEKT) {
//           TParm parm = new TParm();
//           String card = this.getValue("CARD_CODE").toString();
//           if (card.length() != 15) {
//               this.messageBox("�����������,�����ַ�����Ҫ��ʮ��λ");
//               return;
//           }
//           parm.setData("SEQ", card.substring(13, card.length())); //���
//           parm.setData("CURRENT_BALANCE", parmEKT.getValue("CURRENT_BALANCE")); //���
//           parm.setData("MR_NO", card.substring(0, 13)); //������
//
//           parm = EKTIO.getInstance().TXwriteEKT(parm);
//           if (parm.getErrCode() < 0) {
//               this.messageBox("ҽ�ƿ�д��ʧ��");
//           } else
//               this.messageBox("ҽ�ƿ�д�������ɹ�");
//       }else{
//           this.messageBox("��д������ʧ��,û�в�����Ϣ");
//       }
	   
	   /**
	    * zhangp 20121216
	    * ������벡����
	    */
        TParm sendParm = new TParm();
        sendParm.setData("MR_NO", this.getValue("MR_NO"));
        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\ekt\\EKTRenewCard_M.x", sendParm);

   }
   /**
    * ͨ���ʱ�ĵõ�ʡ��
    */
   public void onPost() {
       String post = getValueString("POST_CODE");
       TParm parm = SYSPostTool.getInstance().getProvinceCity(post);
       if (parm.getErrCode() != 0 || parm.getCount() == 0) {
           return;
       }
       setValue("STATE",
                parm.getData("POST_CODE", 0).toString().substring(0, 2));
       setValue("CITY", parm.getData("POST_CODE", 0).toString());
       this.grabFocus("RESID_ADDRESS");
   }
   /**
       * ͨ�����д�����������
       */
      public void selectCode() {
          this.setValue("POST_CODE", this.getValue("CITY"));
    }

    /**
     * ���没����Ϣ
     * @throws ParseException 
     */
    public void onSavePat() throws ParseException {

    	
    	
//        if(!txEKT){
//            this.messageBox("����ҽ�ƿ���Ϣ");
//            return;
//        }
        if (pat != null)
            PatTool.getInstance().unLockPat(pat.getMrNo());
        //���������ֵ
        if (getValue("BIRTH_DATE") == null) {
            this.messageBox("�������ڲ���Ϊ��!");
            return;
        }
        
      //add by huangtt 20140320
    	if(getValueString("PAT_NAME").equals("")){
    		if(getValueString("FIRST_NAME").equals("") && !getValueString("LAST_NAME").equals("")){
    			this.messageBox("������firstName!");
    			this.grabFocus("FIRST_NAME");
                return ;
    		}else if(!getValueString("FIRST_NAME").equals("") && getValueString("LAST_NAME").equals("")){
    			this.messageBox("������lastName!");
    			this.grabFocus("LAST_NAME");
                return ;
    		}else if(!getValueString("FIRST_NAME").equals("") && !getValueString("LAST_NAME").equals("")){
    			this.setValue("PAT_NAME1", getValueString("FIRST_NAME")+" "+getValueString("LAST_NAME"));
    			 if(this.messageBox("������ֵ", "�Ƿ�firstName��lastName�ϲ���ֵ��������", 0) != 0) {
    				 this.messageBox("��������Ϊ��!");
    	             return;
    		     } 
    			this.setValue("PAT_NAME", getValueString("FIRST_NAME")+" "+getValueString("LAST_NAME"));
    			
    		}else if(getValueString("FIRST_NAME").equals("") && getValueString("LAST_NAME").equals("")){
    			this.messageBox("��������Ϊ��!");
                this.grabFocus("PAT_NAME");
                return ;
    		}
    	}else{
    		if(getValueString("FIRST_NAME").equals("") && !getValueString("LAST_NAME").equals("")){
    			this.messageBox("������firstName!");
    			this.grabFocus("FIRST_NAME");
                return ;
    		}else if(!getValueString("FIRST_NAME").equals("") && getValueString("LAST_NAME").equals("")){
    			this.messageBox("������lastName!");
    			this.grabFocus("LAST_NAME");
                return ;
    		}else if(!getValueString("FIRST_NAME").equals("") && !getValueString("LAST_NAME").equals("")){
    			this.setValue("PAT_NAME1", getValueString("FIRST_NAME")+" "+getValueString("LAST_NAME"));	
    		}
    	}
        if (!this.emptyTextCheck("SEX_CODE,CTZ1_CODE"))
            return;
        //add by huangtt 20140404
        if(this.getValueString("EKT_CARD_NO").equals("")){
        	this.messageBox("�����뿨Ƭ���룡");
        	return;
        }
        
        pat = new Pat();
        //��������
        pat.setName(TypeTool.getString(getValue("PAT_NAME")));
        //Ӣ����
        pat.setName1(TypeTool.getString(getValue("PAT_NAME1")));
        //����ƴ��
        pat.setPy1(TypeTool.getString(getValue("PY1")));
        //֤������===add by huangtt 20131106
        pat.setIdType(TypeTool.getString(getValue("ID_TYPE")));
        //���֤��
        pat.setIdNo(TypeTool.getString(getValue("IDNO")));
        //����
        pat.setNationCode(TypeTool.getString(getValue("NATION_CODE")));
        //�����ע��
        pat.setForeignerFlg(TypeTool.getBoolean(getValue("FOREIGNER_FLG")));
        //��������
        pat.setBirthday(TypeTool.getTimestamp(getValue("BIRTH_DATE")));
        //�Ա�
        pat.setSexCode(TypeTool.getString(getValue("SEX_CODE")));
        //�绰TEL_HOME
        pat.setCellPhone(TypeTool.getString(getValue("CELL_PHONE")));
        //�ʱ�
        pat.setPostCode(TypeTool.getString(getValue("POST_CODE")));
        //��ַ
        pat.setResidAddress(TypeTool.getString(getValue("RESID_ADDRESS")));
        //���1
        pat.setCtz1Code(TypeTool.getString(getValue("CTZ1_CODE")));
        //���2
        pat.setCtz2Code(TypeTool.getString(getValue("CTZ2_CODE")));
        //���3
        pat.setCtz3Code(TypeTool.getString(getValue("CTZ3_CODE")));
        //ҽ��������
        pat.setNhiNo(TypeTool.getString(""));
        pat.setCurrentAddress(TypeTool.getString(getValue("CURRENT_ADDRESS")));
        pat.setAddress(TypeTool.getString(getValue("CURRENT_ADDRESS")));
        pat.setRemarks(TypeTool.getString(getValue("REMARKS")));
        pat.setFirstName(TypeTool.getString(getValue("FIRST_NAME")));
		pat.setLastName(TypeTool.getString(getValue("LAST_NAME")));
        //====zhangp 20120309 modify start
//        if (this.messageBox("������Ϣ", "�Ƿ񱣴�", 0) != 0)
//            return;
        //=====zhangp 20120309 modify end
        TParm patParm = new TParm();
        patParm.setData("MR_NO", getValue("MR_NO"));
        patParm.setData("PAT_NAME", getValue("PAT_NAME"));
        patParm.setData("PAT_NAME1", getValue("PAT_NAME1"));
        patParm.setData("LAST_NAME", getValue("LAST_NAME"));
		patParm.setData("FIRST_NAME", getValue("FIRST_NAME"));
        patParm.setData("PY1", getValue("PY1"));
        patParm.setData("ID_TYPE", getValue("ID_TYPE"));  //add by huangtt 20131106
        patParm.setData("IDNO", getValue("IDNO"));
        patParm.setData("BIRTH_DATE", getValue("BIRTH_DATE"));
        patParm.setData("CELL_PHONE", getValue("CELL_PHONE"));
        patParm.setData("SEX_CODE", getValue("SEX_CODE"));
        patParm.setData("POST_CODE", getValue("POST_CODE"));
        patParm.setData("RESID_ADDRESS", getValue("RESID_ADDRESS"));
        patParm.setData("ADDRESS", getValue("CURRENT_ADDRESS"));
        patParm.setData("CTZ1_CODE", getValue("CTZ1_CODE"));
        patParm.setData("CTZ2_CODE", getValue("CTZ2_CODE"));
        patParm.setData("CTZ3_CODE", getValue("CTZ3_CODE"));
        patParm.setData("NHI_NO", ""); // =============pangben
		patParm.setData("CURRENT_ADDRESS",getValue("CURRENT_ADDRESS"));  //add by huangtt 20131106
		patParm.setData("REMARKS",getValue("REMARKS"));   //add by huangtt 20131106
		patParm.setData("NATION_CODE",getValueString("NATION_CODE"));
		patParm.setData("SPECIES_CODE",getValueString("SPECIES_CODE"));
		patParm.setData("MARRIAGE_CODE",getValueString("MARRIAGE_CODE"));
        
        if (StringUtil.isNullString(getValueString("MR_NO"))) {
            patParm.setData("MR_NO", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValueString("PAT_NAME"))) {
            patParm.setData("PAT_NAME", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValueString("PAT_NAME1"))) {
            patParm.setData("PAT_NAME1", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValueString("PY1"))) {
            patParm.setData("PY1", new TNull(String.class));
        }
        //add by huangtt start 20131106
        if (StringUtil.isNullString(getValueString("LAST_NAME"))) {
			patParm.setData("LAST_NAME", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValueString("FIRST_NAME"))) {
			patParm.setData("FIRST_NAME", new TNull(String.class));
		}
        if (StringUtil.isNullString(getValueString("ID_TYPE"))) {
            patParm.setData("ID_TYPE", new TNull(String.class));
        }
        //add by huangtt end 20131106
        if (StringUtil.isNullString(getValueString("IDNO"))) {
            patParm.setData("IDNO", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValueString("BIRTH_DATE"))) {
            patParm.setData("BIRTH_DATE", new TNull(Timestamp.class));
        }
        if (StringUtil.isNullString(getValueString("CELL_PHONE"))) {
            patParm.setData("CELL_PHONE", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValueString("SEX_CODE"))) {
            patParm.setData("SEX_CODE", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValueString("POST_CODE"))) {
            patParm.setData("POST_CODE", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValueString("RESID_ADDRESS"))) {
            patParm.setData("RESID_ADDRESS", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValueString("CTZ1_CODE"))) {
            patParm.setData("CTZ1_CODE", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValueString("CTZ2_CODE"))) {
            patParm.setData("CTZ2_CODE", new TNull(String.class));
        }
        if (StringUtil.isNullString(getValueString("CTZ3_CODE"))) {
            patParm.setData("CTZ3_CODE", new TNull(String.class));
        }
        patParm.setData("NHI_NO", new TNull(String.class));//ҽ������
        
		if (StringUtil.isNullString(getValueString("CURRENT_ADDRESS"))) {
			patParm.setData("CURRENT_ADDRESS", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValueString("CURRENT_ADDRESS"))) {
			patParm.setData("ADDRESS", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValueString("REMARKS").toString())) {
			patParm.setData("REMARKS", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValueString("NATION_CODE"))) {
			patParm.setData("NATION_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValueString("SPECIES_CODE"))) {
			patParm.setData("SPECIES_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValueString("MARRIAGE_CODE"))) {
			patParm.setData("MARRIAGE_CODE", new TNull(String.class));
		}
		
        TParm result = new TParm();
        if (getValue("MR_NO").toString().length() != 0) {
            //���²���
            result = PatTool.getInstance().upDateForReg(patParm);
            setValue("MR_NO", getValue("MR_NO"));
            pat.setMrNo(getValue("MR_NO").toString());                       
            
        } else {
            //��������
            //pat.setTLoad(StringTool.getBoolean("" + getValue("tLoad")));
            if(!pat.onNew()){
                result.setErr(-1,"�½�������Ϣʧ��");
                ektFlg = true; //ҽ�ƿ������ܿ�
            }else{
                ektFlg=true;
                setValue("MR_NO", pat.getMrNo());
                callFunction("UI|MR_NO|setEnabled", false); //�����ſɱ༭
                setValue("CARD_CODE", pat.getMrNo() + "001");
                         
            }
        }
        //===zhangp 20120309 modify start
//        if (result.getErrCode() != 0) {
//            this.messageBox("E0005");
//        } else {
//            this.messageBox("P0005");
//        }
        //=====zhangp 20120309 modify end
//        if (ektCard != null || ektCard.length() != 0) {
//            EKTIO.getInstance().saveMRNO(this.getValueString("MR_NO"), this);
//        }
       // onClear();
        //zhangp 20111230 �����ƿ�
        
//        if(this.getValueBoolean("MANAGE_MEM")){
//    		if(this.getValueDouble("FEEs")<this.getValueDouble("MEM_FEE") ){
//    			this.messageBox("��������ȷ��ʵ�ս�");
//    			return;
//    		}
//    		
//    		TParm parm = new TParm();
//    		parm.setData("CARD_NO", parmMem.getValue("MEM_CARD_NO", 0)); //������
//            parm.setData("CURRENT_BALANCE", this.getValue("CURRENT_BALANCE")); //���
//            parm.setData("MR_NO", this.getValueString("MR_NO")); //������
//            String seq = parm.getValue("CARD_NO").substring(this.getValueString("MR_NO").length());
//            parm.setData("SEQ", seq);
//            if(this.getValueString("MEM_TYPE").length()==1){
//            	parm.setData("TYPE", "0"+this.getValue("MEM_TYPE"));  //�����  huangtt
//            }else{
//            	parm.setData("TYPE", this.getValue("MEM_TYPE"));  //�����  huangtt
//            }
//           
//
//            //д������
////            try {
////  			EKTIO.getInstance().TXwriteEKT(parm);
////            } catch (Exception e1) {
////  			// TODO Auto-generated catch block
////  			this.messageBox("��Ա������ʧ��");
////  			e1.printStackTrace();
////  			return;
////            }
//            
//    		TParm parmMemTrade = new TParm();
//    		parmMemTrade.setData("TRADE_NO", parmMem.getValue("TRADE_NO", 0));
//    		parmMemTrade.setData("LAST_DEPRECIATION_END_DATE", this.getValue("START_DATE"));
//    		TParm parmMemInfo = new TParm();
//    		parmMemInfo.setData("MR_NO", this.getValueString("MR_NO"));
//    		parmMemInfo.setData("MEM_CODE", this.getValueString("MEM_TYPE"));
//    		TTextFormat memType = (TTextFormat) this.getComponent("MEM_TYPE");
//    		parmMemInfo.setData("MEM_DESC", memType.getText());
//    		parmMemInfo.setData("START_DATE", this.getValue("START_DATE"));
//    		parmMemInfo.setData("END_DATE", this.getValue("END_DATE"));
//    		TParm parmMemFeeD = new TParm();
//    		String sDate = this.getValueString("START_DATE").replace("-", "/").substring(0, 10);
//    		String eDate = this.getValueString("END_DATE").replace("-", "/").substring(0, 10);
//    		int month = this.getMonthSpace(sDate, eDate);
//    		double memFee = this.getValueDouble("MEM_FEE");
//    		double dFee =Math.round(memFee/month*100)/100.0 ;
//    		parmMemFeeD.setData("MR_NO",this.getValueString("MR_NO"));
//    		parmMemFeeD.setData("MEM_CARD_NO",parm.getValue("CARD_NO"));
//    		parmMemFeeD.setData("DEPRECIATION_FEE",dFee); //�۾ɷ�
//    		parmMemFeeD.setData("DEPRECIATION_START_DATE",this.getValue("START_DATE")); //�۾����俪ʼʱ��
//    		parmMemFeeD.setData("DEPRECIATION_END_DATE",this.getValue("START_DATE")); //�۾��������ʱ��
//    		parmMemFeeD.setData("REMAINING_AMOUNT",memFee-dFee); //�û�Աʣ����
//    		parmMemFeeD.setData("BEFORE_AMOUNT",this.getValueDouble("MEM_FEE")); //�۾�ǰ���
//    		parmMemFeeD.setData("STATUS",1); //״̬��1�۾ɣ�2�۾ɵֳ�
//    		parmMemFeeD.setData("OPT_DATE",TJDODBTool.getInstance().getDBTime());
//    		parmMemFeeD.setData("OPT_USER",Operator.getID());
//    		parmMemFeeD.setData("OPT_TERM",Operator.getIP());
//    		
//    		TParm p=new TParm();
//            p.setData("CARD_NO",parm.getValue("CARD_NO"));//����
//            p.setData("MR_NO",this.getValueString("MR_NO"));//������          
//            p.setData("CARD_SEQ",seq);//���
//            p.setData("ISSUE_DATE", SystemTool.getInstance().getDate());//����ʱ��
//            p.setData("ISSUERSN_CODE",this.getValue("SEND_CAUSE"));//����ԭ��
//            p.setData("FACTORAGE_FEE", this.getValueDouble("PROCEDURE_PRICE"));//������
//            p.setData("PASSWORD", OperatorTool.getInstance().encrypt(this.getValue("CARD_PWD").toString()));//�������
//            p.setData("WRITE_FLG", "Y");//д������ע��
//            p.setData("OPT_USER", Operator.getID());
//            p.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
//            p.setData("OPT_TERM", Operator.getIP());
//            p.setData("ID_NO", this.getValueString("IDNO").length()==0 ? "none" : pat.getIdNo());//���֤����
//            p.setData("CASE_NO", "none");//�����
//            p.setData("NAME", TypeTool.getString(getValue("PAT_NAME")));//��������
//            p.setData("CURRENT_BALANCE", this.getValueDouble("CURRENT_BALANCE"));
//            p.setData("CREAT_USER", Operator.getID());//������
//            p.setData("FLG",ektFlg);
//            //zhangp 20121219 ���ֶΣ����ţ�
//            String ektCardNo= this.getValue("EKT_CARD_NO").toString();
//            p.setData("EKT_CARD_NO",ektCardNo);
//            String sendCause = getValue("SEND_CAUSE").toString();
//            p.setData("CHARGE_FLG", sendCause); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����,6,����,7,�˷�,8,����)
//            //zhangp 20111222 EKT_BIL_PAY
//            TParm bilParm = new TParm();
//            //zhangp 20120116 ����+��ʧ ԭ3 ��2
//            //===zhangp 20120322 start
//            int accntType = 2;
//            if(sendCause.equals("4")){//�ƿ�
//         	   accntType = 1;
//            }
//            if(sendCause.equals("5")){//����
//         	   accntType = 3;
//            }
//            if(sendCause.equals("8")){//����
//         	   accntType = 2;
//            }
//            bilParm.setData("ACCNT_TYPE", accntType);	//��ϸ�ʱ�(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�)(EKT_BIL_PAY)
//            //===zhangp 20120322 end
//            bilParm.setData("CURT_CARDSEQ", seq);	//��Ƭ���(EKT_BIL_PAY)
//            bilParm.setData("GATHER_TYPE", getValue("GATHER_TYPE"));	//��ֵ��ʽ(EKT_BIL_PAY)
//            bilParm.setData("AMT", "0");	//AMT(EKT_BIL_PAY)
//            //zhangp 20120109 ���ֶ�
//            bilParm.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());	//�ۿ�����ʱ��
//            bilParm.setData("PROCEDURE_AMT", this.getValueDouble("PROCEDURE_PRICE"));	//PROCEDURE_AMT
//            p.setData("bilParm", getBilParm(p,bilParm).getData());
//            //��ϸ�����
//             TParm feeParm=new TParm();
//             feeParm.setData("ORIGINAL_BALANCE",this.getValueDouble("CURRENT_BALANCE"));
//             feeParm.setData("BUSINESS_AMT",0);
//             feeParm.setData("CURRENT_BALANCE",this.getValueDouble("CURRENT_BALANCE"));
//             p.setData("businessParm",getBusinessParm(p,feeParm).getData());
//    		TParm parmAll = new TParm();
//    		parmAll.setData("parmMemTrade", parmMemTrade.getData());
//    		parmAll.setData("parmMemInfo", parmMemInfo.getData());
//    		parmAll.setData("parmMemFeeD", parmMemFeeD.getData());
//    		parmAll.setData("parmEkt", p.getData());
//
//    		result = TIOM_AppServer.executeAction("action.mem.MEMAction","updateMemTradeInfo",parmAll);
//    		if(result.getErrCode()<0){
//        		this.messageBox("����ʧ�ܣ�");
//        		
//        	}else{
//        		this.messageBox("����ɹ���");
//        		 String bil_business_no = result.getValue("BIL_BUSINESS_NO"); //�վݺ�
//        		 parmSum = new TParm();
//        		 parmSum.setData("MR_NO", this.getValueString("MR_NO"));
//        		 parmSum.setData("NAME", this.getValueString("PAT_NAME"));
//        		 parmSum.setData("GATHER_TYPE_NAME", this.getText("MEM_GATHER_TYPE"));
//        		 parmSum.setData("BUSINESS_AMT", this.getValueDouble("FEEs"));
//        		 parmSum.setData("SEX_TYPE", this.getValueString("SEX_CODE"));
//        		 parmSum.setData("DESCRIPTION", this.getValueString("MEM_DESCRIPTION"));
//        		 parmSum.setData("BIL_CODE", "");
//                 onPrint(bil_business_no);//��ӡƱ��
//        	}
//    		
//    		
//    		return;
//
//    	}
        
        onNew();
    }

    public void onSave(){
    	//add by huangtt 20140404
//        if(this.getValueString("EKT_CARD_NO").equals("")){
//        	this.messageBox("�����뿨Ƭ���룡");
//        	return;
//        }
    	onNew();
    }
    /**
     *���
     */
    public void onClear() {
        clearValue(" MR_NO;PAT_NAME;PAT_NAME1;PY1;IDNO;FOREIGNER_FLG; " +
                   " BIRTH_DATE;SEX_CODE;POST_CODE;STATE;CITY;RESID_ADDRESS; " +
                   " CTZ2_CODE;CTZ3_CODE;CARD_CODE;CARD_PWD;BIL_CODE;MEM_DESCRIPTION; " +
                   " CURRENT_BALANCE;TOP_UP_PRICE;PROCEDURE_PRICE;GATHER_PRICE;NATION_CODE;DESCRIPTION;EKT_CARD_NO;" +
                   "EKTMR_NO;EKTCARD_CODE;BANK_CARD_NO;RE_CARD_PWD;CURRENT_ADDRESS;REMARKS;MEM_TYPE;FEEs");
        clearValue("FIRST_NAME;LAST_NAME;CELL_PHONE;ID_TYPE;");
        //callFunction("UI|FOREIGNER_FLG|setEnabled", true); //����֤���ɱ༭======pangben modify 20110808
        callFunction("UI|MR_NO|setEnabled", true); //�����ſɱ༭
        callFunction("UI|CARD_CODE|setEnabled", true); //ҽ�ƿ��ſ��Ա༭
        setValue("CTZ1_CODE", "99");
      //add by huangtt 20140118
 	 
        /**
         * zhangp 20121216 ����Ĭ���й�
         */
        setValue("NATION_CODE", "86");
        //����Ĭ�Ϸ���ȼ�
       // txEKT = false; //̩��ҽ�ƿ�д���ܿ�
        parmEKT = null; //ҽ�ƿ�����parm
        ektFlg=false;
        EKTTemp=null;
        parmSum=null;//ִ�г�ֵ��������
        bankFlg=false;
        //��������
//        if (pat != null)
//            PatTool.getInstance().unLockPat(pat.getMrNo());
        //=====zhangp 20120224 ֧����ʽ    modify start 
        String id = EKTTool.getInstance().getPayTypeDefault();
        setValue("GATHER_TYPE", id);
        //=======zhangp 20120224 modify end
        setValue("MEM_GATHER_TYPE", id);
        setPassWord();
        
//        callFunction("UI|MEM_TYPE|setEnabled", false);
//        callFunction("UI|START_DATE|setEnabled", false);
//        callFunction("UI|END_DATE|setEnabled", false);
//        callFunction("UI|MEM_GATHER_TYPE|setEnabled", false);
//        callFunction("UI|FEEs|setEnabled", false);
//        callFunction("UI|MEM_DESCRIPTION|setEnabled", false);
    }

    /**
    * ��������
    * @return boolean
    */
   private boolean txReadEKT(){
       //��ȡҽ�ƿ�����
       if (EKTTemp == null)
		try {
			EKTTemp = EKTIO.getInstance().readEkt();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       if (null == EKTTemp || EKTTemp.getValue("MR_NO").length() <= 0) {
           this.messageBox("��ҽ�ƿ���Ч");
           return false;
       }
       return true;
   }
   /**
    * ��Ա���ۿ�
    * yanjing
    * 20140422
    */
   
   public void onMEMcard(){
	   TParm parm = new TParm();
	   if((!"".equals(this.getValue("MR_NO")))&&(!this.getValue("MR_NO").equals(null))){
		   parm.setData("MR_NO", this.getValue("MR_NO"));
		   parm.setCount(1);
	   }
	  if(parm.getCount()<=0){
    	   this.openDialog("%ROOT%\\config\\mem\\MEMMarketCard.x");
       }else{
    	   this.openDialog("%ROOT%\\config\\mem\\MEMMarketCard.x",parm);
       }	 
	  this.onQueryNO();
   }
	  	

    /**
     * �շѷ���
     */
    public void onFEE(){
    	
    	if(this.getValue("GATHER_TYPE").equals("WX")||this.getValue("GATHER_TYPE").equals("ZFB")){
    		if(this.getValue("BIL_CODE").toString().length()<=0){
    			this.messageBox("΢�Ż�֧������Ҫ��Ʊ�ݺ�������д���׺ţ�");
    			return;
    		}
    	}
    	topUpFee();//add by sunqy 20140728�Զ�����Ӧ�շ��� ���ⲻ�س��͵���շѵ����
//        if(!txEKT){
//            this.messageBox("���ȡҽ�ƿ���Ϣ");
//            return;
//        }
    	//zhangp 20121227 �շѽ���һ��
//    	if(ektFlg==true){
//    		onNew();
//    		ektFlg=false;
//    	}
    	ektFlg = false;
        if (!txReadEKT()) {
            return;
        }
        if (ektFlg || parmEKT == null || parmEKT.getCount() <= 0) {
            this.messageBox("û�л��ҽ�ƿ���Ϣ");
            return;
        }
        //����
//        if (this.getValue("CARD_PWD").toString().length() <= 0) {
//            this.messageBox("����������");
//            return;
//        }

        if (this.getValue("TOP_UP_PRICE").toString().length() <= 0) {
            this.messageBox("������Ҫ��ֵ�Ľ��");
            return;
        }
        if(this.getValueDouble("TOP_UP_PRICE")<=0){
            this.messageBox("��ֵ����ȷ");
            return;
        }
        if (((TTextFormat)this.getComponent("GATHER_TYPE")).getText().length() <= 0) {
            this.messageBox("֧����ʽ������Ϊ��ֵ");
            return;
        }
        //���֧����ʽΪˢ��ʱ�������ͺͿ���Ϊ������add by huangjw 20150115
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
        //add by huangtt 20140228
        if (this.messageBox("��ֵ", "�Ƿ��ֵ"+this.getValueDouble("TOP_UP_PRICE")+"Ԫ", 0) != 0) {
            return;
        }
       
        //zhangp 20111230 ע
//        if (this.messageBox("�շ�", "�Ƿ�ִ�г�ֵ����", 0) != 0) {
//            return;
//        }

//        //����У��
//        if (!this.getValue("CARD_PWD").toString().trim().equals(parmEKT.
//                getValue("PASSWORD", 0).trim())) {
//            this.messageBox("���벻��,����������");
//            this.setValue("CARD_PWD", "");
//            this.grabFocus("CARD_PWD");
//            return;
//        }
        TParm result =null;
            parmSum = new TParm();
            parmSum.setData("CARD_NO", pat.getMrNo() + parmEKT.getValue("CARD_SEQ",0));
            parmSum.setData("CURRENT_BALANCE", StringTool.round(parmEKT.getDouble("CURRENT_BALANCE", 0),2) + StringTool.round(this.getValueDouble("TOP_UP_PRICE"),2));
            parmSum.setData("CASE_NO", "none");
            parmSum.setData("NAME", pat.getName());
            parmSum.setData("MR_NO", pat.getMrNo());
            parmSum.setData("ID_NO",
                      null != pat.getIdNo() && pat.getIdNo().length() > 0 ?
                      pat.getIdNo() : "none");
            parmSum.setData("ISSUERSN_CODE", this.getText("SEND_CAUSE")); //����ԭ��
            parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE")); //֧����ʽ
            parmSum.setData("OPT_USER", Operator.getID());
            parmSum.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
            parmSum.setData("OPT_TERM", Operator.getIP());
            parmSum.setData("FLG", ektFlg);
            parmSum.setData("CHARGE_FLG", "3"); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����)
            parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE")); //֧����ʽ
            parmSum.setData("GATHER_TYPE_NAME", this.getText("GATHER_TYPE")); //֧����ʽ����
            parmSum.setData("BUSINESS_AMT",
                            StringTool.round(this.getValueDouble("TOP_UP_PRICE"), 2)); //��ֵ���
            parmSum.setData("SEX_TYPE", this.getValue("SEX_CODE")); //�Ա�
            parmSum.setData("DESCRIPTION", this.getValue("DESCRIPTION")); //��עDESCRIPTION
              //parmSum.setData("BIL_CODE", this.getValue("BIL_CODE")); //Ʊ�ݺ�
            parmSum.setData("PRINT_NO", this.getValue("BIL_CODE")); //Ʊ�ݺ�
            System.out.println(this.getValue("BIL_CODE"));
            parmSum.setData("CREAT_USER", Operator.getID()); //ִ����Ա//=====yanjing

            //��ϸ�����
            TParm feeParm=new TParm();
            //==liling 20140725 add Card_type ������  start====
            String cardType="";
            if(((TTextFormat)this.getComponent("CARD_TYPE")).getText().length()>0){
         	   cardType=this.getValue("CARD_TYPE").toString();        	  
            }
            parmSum.setData("CARD_TYPE", cardType);
            //==liling 20140725 add Card_type ������  start====
            feeParm.setData("ORIGINAL_BALANCE",StringTool.round(parmEKT.getDouble("CURRENT_BALANCE", 0),2));
            feeParm.setData("BUSINESS_AMT",StringTool.round(this.getValueDouble("TOP_UP_PRICE"), 2));
            feeParm.setData("CURRENT_BALANCE",StringTool.round(parmEKT.getDouble("CURRENT_BALANCE", 0),2)+StringTool.round(this.getValueDouble("TOP_UP_PRICE"),2));
            parmSum.setData("businessParm",getBusinessParm(parmSum,feeParm).getData());
            //zhangp 20120109 EKT_BIL_PAY ���ֶ�
            parmSum.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());	//�ۿ�����ʱ��
            parmSum.setData("PROCEDURE_AMT", 0.00);	//PROCEDURE_AMT
            //bil_pay ��ֵ������
            parmSum.setData("billParm",getBillParm(parmSum,feeParm).getData());
            //�������
            result = TIOM_AppServer.executeAction(
                    "action.ekt.EKTAction",
                    "TXEKTonFee", parmSum); //
            if (result.getErrCode() < 0) {
               this.messageBox("ҽ�ƿ���ֵʧ��");
           } else{
        	   //add by huangtt 20131225 start
        	   TParm parmAmt = new TParm();
        	   parmAmt.setData("MR_NO", pat.getMrNo());
        	   parmAmt.setData("SEQ", parmEKT.getValue("CARD_SEQ",0));
        	   parmAmt.setData("CURRENT_BALANCE", feeParm.getData("CURRENT_BALANCE"));
        	   try {
				EKTIO.getInstance().TXwriteEKTATM(parmAmt, pat.getMrNo());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
           	   //add by huangtt 20131225 end
               this.messageBox("��ֵ�ɹ�");
               String bil_business_no = result.getValue("BIL_BUSINESS_NO"); //�վݺ�
               onPrint(bil_business_no);//��ӡƱ��
               onClear();
           }
    }
    /**
    * ��ֵ��������ݲ���
    * @param parm TParm
    * @return TParm
    */
   private TParm getBillParm(TParm parm,TParm feeParm){
       TParm billParm=new TParm();
       billParm.setData("CARD_NO", parm.getValue("CARD_NO")); //����
       billParm.setData("CURT_CARDSEQ", 0); //���
       billParm.setData("ACCNT_TYPE", "4"); //��ϸ�ʱ�(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�)
       billParm.setData("MR_NO", parm.getValue("MR_NO"));//������
       billParm.setData("ID_NO", parm.getValue("ID_NO"));//���֤��
       billParm.setData("NAME", parm.getValue("NAME"));//��������
       billParm.setData("AMT", feeParm.getValue("BUSINESS_AMT"));//��ֵ���
       billParm.setData("CREAT_USER", Operator.getID());//ִ����Ա
       billParm.setData("OPT_USER", Operator.getID());//������Ա
       billParm.setData("OPT_TERM", Operator.getIP());//ִ��ip
       billParm.setData("GATHER_TYPE",parm.getValue("GATHER_TYPE"));//֧����ʽ
	   //zhangp 20120109
       billParm.setData("STORE_DATE", parm.getData("STORE_DATE"));
       billParm.setData("PROCEDURE_AMT", parm.getData("PROCEDURE_AMT"));
       billParm.setData("PRINT_NO", parm.getData("PRINT_NO"));
       return billParm;
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
        bilParm.setData("CHARGE_FLG", p.getValue("CHARGE_FLG")); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����)
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
     * ��ⲡ����ͬ����
     */
    public void onPatName() {
//        String patName = this.getValueString("PAT_NAME");
//        if (StringUtil.isNullString(patName)) {
//            return;
//        }
//        //REPORT_DATE;PAT_NAME;IDNO;SEX_CODE;BIRTH_DATE;POST_CODE;ADDRESS
//        String selPat =
//                " SELECT OPT_DATE AS REPORT_DATE,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE," +
//                "        POST_CODE,ADDRESS,MR_NO " +
//                "   FROM SYS_PATINFO " +
//                "  WHERE PAT_NAME = '" + patName + "' " +
//                "  ORDER BY OPT_DATE ";
//        TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
//        if (same.getErrCode() != 0) {
//            this.messageBox_(same.getErrText());
//        }
//        setPatName1();
        //ѡ�񲡻���Ϣ
//        if (same.getCount("MR_NO") > 0) {
//            int sameCount = this.messageBox("��ʾ��Ϣ", "������ͬ����������Ϣ,�Ƿ�������������Ϣ", 0);
//            if (sameCount != 1) {
//                this.grabFocus("PY1");
//                return;
//            }
//            Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x",
//                                    same);
//            TParm patParm = new TParm();
//            if (obj != null) {
//                patParm = (TParm) obj;
//                this.setValue("MR_NO",patParm.getValue("MR_NO"));
//                onQueryNO();
//                return;
//            }
//        }
    	//yuml 20141023
		String patName = this.getValueString("PAT_NAME");
		if (StringUtil.isNullString(patName)) {	
			return;
		}
        this.grabFocus("PY1");
        this.onQueryPat();
    }

    /**
     * ����Ӣ����
     */
    public void setPatName1() {
        String patName1 = SYSHzpyTool.getInstance().charToAllPy(TypeTool.
                getString(getValue("PAT_NAME")));
        setValue("PAT_NAME1", patName1);
    }
   /**
    * ��ֵ���س��¼�
    */
   public void topUpFee(){
       //����ֵ�����ʾ��Ӧ�ս����
       double price=this.getValueDouble("TOP_UP_PRICE")+this.getValueDouble("PROCEDURE_PRICE");
       this.setValue("GATHER_PRICE",price);
       //zhangp 20111223
       this.grabFocus("GATHER_PRICE");
   }
   /**
    * ҽ�ƿ��޸�����
    */
   public void updateEKTPwd(){
       TParm sendParm = new TParm();
           TParm reParm = (TParm)this.openDialog(
               "%ROOT%\\config\\ekt\\EKTUpdatePassWord.x", sendParm);
   }

   /**
    * ��ֵ��ӡ
    */
   private void onPrint(String bil_business_no) {
	  /**
       TParm parm = new TParm();
       parm.setData("TITLE", "TEXT",
                    (Operator.getRegion() != null &&
                     Operator.getRegion().length() > 0 ?
                     Operator.getHospitalCHNFullName() : "����ҽԺ") );
       parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); //������
       parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); //����
     //====zhangp 20120525 start
//     parm.setData("GATHER_NAME", "TEXT", "�� ��"); //�տʽ
     parm.setData("GATHER_NAME", "TEXT", ""); //�տʽ
     //====zhangp 20120525 end
       parm.setData("TYPE", "TEXT", "Ԥ ��"); //�ı�Ԥ�ս��
       parm.setData("GATHER_TYPE", "TEXT", parmSum.getValue("GATHER_TYPE_NAME")); //�տʽ
       parm.setData("AMT", "TEXT",
                    StringTool.round(parmSum.getDouble("BUSINESS_AMT"), 2)); //���
       parm.setData("SEX_TYPE", "TEXT",
                    parmSum.getValue("SEX_TYPE").equals("1") ? "��" : "Ů"); //�Ա�
       parm.setData("AMT_AW", "TEXT",
                    StringUtil.getInstance().numberToWord(
                    parmSum.getDouble("BUSINESS_AMT"))); //��д���
       parm.setData("TOP1", "TEXT", "EKTRT001 FROM " + Operator.getID()); //̨ͷһ
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
       if (null == bil_business_no)
           bil_business_no = EKTTool.getInstance().getBillBusinessNo(); //��ӡ����
       parm.setData("ONFEE_NO", "TEXT", bil_business_no); //�վݺ�
       parm.setData("PRINT_DATE", "TEXT", yMd); //��ӡʱ��
       parm.setData("DATE", "TEXT", yMd + "    " + hms); //����
       parm.setData("USER_NAME", "TEXT", Operator.getID()); //�տ���
       //=========modify by lim 2012/02/24 begin
       //===zhangp 20120525 start
       parm.setData("O", "TEXT", ""); 
//       this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_ONFEE.jhw", parm,true);
       this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm ,true);
       //===zhangp 20120525 end
     //=========modify by lim 2012/02/24 begin
      */
	   	//modify by sunqy 20140611 ----start----
//		TTable table = new TTable();
//		int row = 0;
//		String copy ="";
		boolean flg = false;
		parmSum.setData("TITLE", "�����ֵ�վ�");
		parmSum.setData("BIL_BUSINESS_NO",bil_business_no);
		EKTReceiptPrintControl.getInstance().onPrint(null, parmSum, "", -1, pat, flg, this);
		//modify by sunqy 20140611 ----end----
   }
   /**
    * ���п���������
    */
   public void onBankCard(){
       //��ȡ���п���Ϣ
       //��������
       this.setValue("BANK_CARD_NO","111111111111111");

   }
   /**
    * ���п�ִ�в���
    */
   public void onBankSave(){
       if(bankFlg){
           if(this.getValue("BANK_CARD_NO").toString().length()<=0){
               this.messageBox("�ȶ�ȡ���п���Ϣ");
               return;
           }
           TParm parm = new TParm();
           parm.setData("CARD_NO", this.getValue("CARD_CODE"));
           parm.setData("BANK_CARD_NO", this.getValue("BANK_CARD_NO"));
           TParm result=EKTIO.getInstance().updateEKTAndBank(parm);
           if(result.getErrCode()<0){
               this.messageBox("���п�����ʧ��");
           }else{
               this.messageBox("���п������ɹ�");
           }
       }
       else{
           this.messageBox("ֻ�д���ҽ�ƿ���Ϣ�ſ���ִ�����п�����");
       }


   }
   
   /**
    * ��ѯ����
    * ===========zhangp 20111216
    */
   public void onQueryPat(){
	        TParm sendParm = new TParm();
	        sendParm.setData("PAT_NAME", this.getValue("PAT_NAME"));
	        //yuml   20141023  start
	        sendParm.setData("SEX_CODE", this.getValue("SEX_CODE"));
	        sendParm.setData("BIRTH_DATE", this.getValue("BIRTH_DATE"));
	        TParm result = PatTool.getInstance().queryPat(sendParm);
	         // �жϴ���ֵ
	         if (result.getErrCode() < 0) {
	             messageBox(result.getErrText());
	             return;
	         }
	    if (result.getCount("MR_NO") > 0) {
	      	Object[] possibilities = { "  ��   ", "  ��   " };
	      	int sameCount = JOptionPane.showOptionDialog(null,
	      			"������ͬ�����Ĳ�����Ϣ,�Ƿ�������������Ϣ", "��ʾ��Ϣ",
	      			JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
	      			null, possibilities, possibilities[0]);
	      	if (sameCount != 1) {
	      		if("".equals(String.valueOf(sendParm.getData("SEX_CODE")))){
	      			this.grabFocus("SEX_CODE");
	      		}else if(null==sendParm.getData("BIRTH_DATE")){
	      			this.grabFocus("BIRTH_DATE");
	      		}
	      		return;
	      	}
	      //yuml   20141023  end
	        TParm reParm = (TParm)this.openDialog(
	            "%ROOT%\\config\\adm\\ADMPatQuery.x", result);
	        if(reParm==null)
	            return;
	        this.setValue("MR_NO", reParm.getValue("MR_NO"));
	        this.onQueryNO();
	        //zhangp 20111223
	        this.grabFocus("PY1");
	    }
   }
   /**
    * ҽ�ƿ���ֵ�˿��������
    * zhangp 20111222
    */
   public TParm getBilParm(TParm parm,TParm bparm){
	   TParm bilParm = new TParm();
	   bilParm.setData("CARD_NO", parm.getData("CARD_NO"));
	   bilParm.setData("CURT_CARDSEQ", bparm.getData("CURT_CARDSEQ"));
	   bilParm.setData("ACCNT_TYPE", bparm.getData("ACCNT_TYPE"));
	   bilParm.setData("MR_NO", parm.getData("MR_NO"));
	   bilParm.setData("ID_NO", parm.getData("ID_NO"));
	   bilParm.setData("NAME", parm.getData("NAME"));
	   bilParm.setData("CREAT_USER", parm.getData("CREAT_USER"));
	   bilParm.setData("OPT_USER", parm.getData("OPT_USER"));
	   bilParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
	   bilParm.setData("GATHER_TYPE", bparm.getData("GATHER_TYPE"));
	   bilParm.setData("AMT", bparm.getData("AMT"));
	   //zhangp 20120109
	   bilParm.setData("STORE_DATE", bparm.getData("STORE_DATE"));
	   bilParm.setData("PROCEDURE_AMT", bparm.getData("PROCEDURE_AMT"));
	   bilParm.setData("DESCRIPTION", "");
	   bilParm.setData("CARD_TYPE","");
	   bilParm.setData("PRINT_NO",bparm.getData("PRINT_NO"));
	   return bilParm;
   }
   /**
    * ����ԭ�����
    * ====zhangp 20120202
    */
   public void onSendCause(){
	   TParm result = EKTTool.getInstance().factageFee(getValue("SEND_CAUSE").toString());
	      if(result.getCount()>0){
	    	  setValue("PROCEDURE_PRICE", result.getDouble("FACTORAGE_FEE", 0));
	      }
   }
   /**
    * ����ȷ��
    * ===zhangp 20120315
    */
   public void onPassWord(){
	   if(!passWord4()){
		   return;
	   }
	   if(getValueString("RE_CARD_PWD").length()==0){
		   
	   }else{
		   if(!passWord()){
			   return;
		   }
		   grabFocus("SAVEBUTTON");
	   }
   }
   /**
    * ����ȷ��
    * ===zhangp 20120315
    * @return
    */
   public boolean passWord(){
	   String passWord = getValueString("CARD_PWD");
	   String repassWord = getValueString("RE_CARD_PWD");
	   if(!passWord.equals(repassWord)){
		   messageBox("���벻һ��");
		   return false;
	   }
	   return true;
   }
   /**
    * 6λ����
    * ===zhangp 20120319
    * @return
    */
   public boolean passWord4(){
	   String passWord = getValueString("CARD_PWD");
	   if(passWord.length()!=6){
		   messageBox("����Ϊ6λ");
		   return false;
	   }
	   grabFocus("RE_CARD_PWD");
	   return true;
   }
   /**
    * ��ʼ���� 
    */
   private void setPassWord(){
	   this.setValue("CARD_PWD", "123456");
	   this.setValue("RE_CARD_PWD", "123456");
   }
   
   /**
	 * ���ݿ�Ƭ���ͣ��õ����
	 */
	public void onQueryMemFee(){
		String memCode = this.getValueString("MEM_TYPE");
		TParm parm = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemFee(memCode)));
		this.setValue("MEM_FEE", parm.getValue("MEM_FEE", 0));
		
	}
	
	public void getMemCodeEndDate(){
		String memCode = this.getValueString("MEM_TYPE");
		TParm parm = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemFee(memCode)));
		int validDays = parm.getInt("VALID_DAYS", 0)-1;
		Timestamp startDate = (Timestamp) this.getValue("START_DATE");
		this.setValue("END_DATE", StringTool.rollDate(startDate, +validDays).toString()
				.substring(0, 10).replace('-', '/'));
		this.grabFocus("FEEs");
		
	}
	/**
	 * ��������ʱ������������
	 * @param begin
	 * @param end
	 * @return
	 * @throws ParseException
	 */
	
	@SuppressWarnings("deprecation")
	public static int getMonthSpace(String begin, String end)
	throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		Date beginDate = df.parse(begin);
		Date endDate = df.parse(end);
		int beginYear = beginDate.getYear();
		int beginMonth = beginDate.getMonth();
		int endYear = endDate.getYear();
		int endMonth = endDate.getMonth();
		int difMonth = (endYear-beginYear)*12+(endMonth-beginMonth);
		return difMonth; 
	}
	
	/**
	 * ��ȡ��������
	 */
	public int getBuyMonth(String s, String s1){
		Date d = null;
		Date d1 = null;
		DateFormat df=new SimpleDateFormat("yyyyMMdd");
		try {  
		    d = df.parse(s);
		    d1=df.parse(s1);
		    }catch (ParseException e){    
		    	e.printStackTrace(); }
		    Calendar c = Calendar.getInstance();		    
		    c.setTime(d);		  
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

	/**
	 * �жϻ�Ա�Ļ� �Ƿ���ɻ��
	 */
	public boolean checkMemFee() {
		boolean flg = false;
		String sql = "SELECT MR_NO,STATUS FROM MEM_TRADE WHERE MR_NO = '"+this.getValueString("MR_NO")+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
			String sql2 = "SELECT MR_NO,STATUS FROM MEM_TRADE " +
					" WHERE MR_NO = '"+this.getValueString("MR_NO")+"' AND STATUS = '1'";
			TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
			if(parm2.getCount()>0){
				flg = true;
			}else{
				flg = false;
			}
			
		}else{
			flg = true;
			return flg;
		}
		return flg;
		
	}
	/**
	 * ��ѯ��ע�Ỽ��
	 */
	public void onNewPat(){
		TParm parm = (TParm) this.openDialog(
				"%ROOT%\\config\\mem\\MEMNewPatQuery.x", false);
		System.out.println("ekt==="+parm.getValue("MR_NO"));
		this.setValue("MR_NO", parm.getValue("MR_NO"));
		onQueryNO(false);
	}
}

