package com.javahis.ui.ekt;

import java.sql.Timestamp;

import javax.swing.JOptionPane;

import jdo.ekt.EKTIO;
import jdo.ekt.EKTTool;
import jdo.mem.MEMSQL;
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
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ҽ�ƿ�������ʧ</p>
 * 
 *
 * <p>Description: ҽ�ƿ�������ʧ</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 *
 *
 * @author pangben 20111007
 * @version 1.0
 */
public class EKTRenewCardControl extends TControl{
//    public ()EKTRenewCardControl {
//    }
	 private Pat  pat;//  ������Ϣ
	  private TParm parmEKT;//ҽ�ƿ�����
	  private boolean ektFlg=false;//�˲����Ƿ����ҽ�ƿ�
	  private TParm EKTTemp;//ҽ�ƿ����ֵ
	  private TParm parmSum;//ִ�г�ֵ��������
	  private String SPECIES_CODE=""; //���� 
	  private String MARRIAGE_CODE=""; //���� 
	  //private boolean reEktFlg=false;//�˲����Ƿ��Ѿ�����
	//  private boolean bankFlg=false;//���п���������
	  //add by huangtt 20140410 start
	
	   private String memCardNo = "";  //��Ա����

      //add by huangtt 20140410 end
	
    /**
   * ��ʼ������
   */
  public void onInit() {
	  //======yanj20130403
     setValue("CTZ1_CODE", "99");
     setValue("CURRENT_BALANCE", "");
//     setValue("CURRENT_BALANCE1", "");
//     setValue("CURRENT_BALANCE1", "");
     
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
         this.setValue("MR_NO", mrNo);
         this.onQueryNO();
     }
     this.setValue("TOP_UP_PRICE", "");
     //zhangp 20120202 ������Ĭ��ֵ
     		setValue("SEND_CAUSE", 8);
     //yanj ������
     TParm result = EKTTool.getInstance().factageFee(getValue("SEND_CAUSE").toString());
     if(result.getCount()>0){
    	 setValue("PROCEDURE_PRICE", result.getDouble("FACTORAGE_FEE", 0));
    	 setValue("PROCEDURE_PRICE1", result.getDouble("FACTORAGE_FEE", 0));
     }
     //=====zhangp 20120224 ֧����ʽ    modify start 
     String id = EKTTool.getInstance().getPayTypeDefault();
     setValue("GATHER_TYPE", id);
     setValue("GATHER_TYPE1", id);
     //=======zhangp 20120224 modify end
     //=====yanj 20130308 ��ֵ֧����ʽ 
//     String num = EKTTool.getInstance().getPayTypeDefault();
//     setValue("GATHER_TYPE1", num);
     setPassWord();

  }
 
  /**
   * zhangp 20121216 ���벡����
   */
  private TParm acceptData = new TParm(); //�Ӳ�
  private double ektCurrentBalance = 0.00;
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
  public void onQueryNO(){
      onQueryNO(true);
  }
  /**
   * ��ѯ����
   */
  private void onQueryNO(boolean flg) {
      if (pat != null)
          PatTool.getInstance().unLockPat(pat.getMrNo());

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
       setValue("ID_TYPE", pat.getIdType());  //add by huangtt 20131220
       SPECIES_CODE=pat.getSpeciesCode();  //add by huangtt 20140218
       MARRIAGE_CODE=pat.getMarriageCode(); //add by huangtt 20140218
       
       setValue("CURRENT_ADDRESS", pat.getCurrentAddress());
       setValue("REMARKS", pat.getRemarks());
       setValue("NATION_CODE",pat.getNationCode());
     //zhangp 20111223 ����
       if(pat.getNationCode().equals("")||pat.getNationCode()==null){
    	   setValue("NATION_CODE", "86");
       }
       setValue("FOREIGNER_FLG", pat.isForeignerFlg());
       setValue("BIRTH_DATE", pat.getBirthday());
       setValue("SEX_CODE", pat.getSexCode());
       setValue("CELL_PHONE", pat.getCellPhone());
       setValue("POST_CODE", pat.getPostCode());
       onPost();
       setValue("RESID_ADDRESS", pat.getResidAddress());
       setValue("CTZ1_CODE", pat.getCtz1Code());
       setValue("CTZ2_CODE", pat.getCtz2Code());
       setValue("CTZ3_CODE", pat.getCtz3Code());
       //======yanj20130403
       setValue("EKTMR_NO",this.getValue("MR_NO"));
//       setValue("GATHER_TYPE1", );
//       setValue("CTZ3_CODE", pat.getCtz3Code());
       
//        setValue("REG_CTZ3", getValue("CTZ3_CODE"));
       patLock();
       TParm parm=new TParm();
       parm.setData("MR_NO",pat.getMrNo());
       parmEKT= EKTTool.getInstance().selectEKTIssuelog(parm);
       //��������Ϣ
//            this.messageBox_("�����ɹ�!");//����ר�� 
       this.grabFocus("onFee");
       callFunction("UI|MR_NO|setEnabled", false); //�����Ų��ɱ༭
       if(parmEKT.getCount()<=0){
//           this.messageBox("�˲���û��ҽ�ƿ���Ϣ");
           //�¿�����
           this.setValue("CARD_CODE",pat.getMrNo()+"001"); 
           this.setValue("OLD_SEQ","001");
           this.setValue("NEW_SEQ","001");
           //======yanj��Ƭ����
           this.setValue("EKTCARD_CODE", this.getValue("EKT_CARD_NO"));
           this.setValue("CURRENT_BALANCE1", "");
           this.setValue("CURRENT_BALANCE", "");
           
//           setValue("EKTMR_NO", getValue("MR_NO"));      
           //������ҽ�ƿ���д��ʱ��������EKT_MASTER����Ϣ
           ektFlg=true;
       }else{
    	   //add by huangtt 20140414 start ��Ա֮ǰ��ҽ�ƿ�������ִ�л���
//    	   TParm parmMem = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemTrade1(getValueString("MR_NO"))));
//    	   if(parmMem.getCount()>0){
//    		   memCardNo = parmMem.getValue("MEM_CARD_NO", 0);
//    		   if(!parmMem.getValue("MEM_CARD_NO", 0).equals(parmEKT.getValue("CARD_NO", 0))){
//    			   ektCurrentBalance =  parmEKT.getDouble("CURRENT_BALANCE",0); 
//    			   this.setValue("OLD_SEQ",parmEKT.getValue("CARD_SEQ",0));
//    			   this.setValue("NEW_SEQ",parmMem.getValue("MEM_CARD_NO", 0).substring(getValueString("MR_NO").length()));
//    			   this.setValue("CARD_CODE",parmMem.getValue("MEM_CARD_NO", 0));
//    			   this.setValue("EKT_CARD_NO", "");
//    			   setValue("TOP_UP_PRICE", "");
//    			   setValue("EKTCARD_CODE", getValue("EKT_CARD_NO"));
//    	           this.setValue("CURRENT_BALANCE",StringTool.round(parmEKT.getDouble("CURRENT_BALANCE", 0),2) );
//    	           this.setValue("CURRENT_BALANCE1",StringTool.round(parmEKT.getDouble("CURRENT_BALANCE", 0),2));
//    	           this.grabFocus("EKT_CARD_NO");
//    	           return;
//    			   
//    		   }
//    	   }
    	   //add by huangtt 20140414 end
    	   
           ektCurrentBalance =  parmEKT.getDouble("CURRENT_BALANCE",0);
           this.setValue("OLD_SEQ",parmEKT.getValue("CARD_SEQ",0));
           this.setValue("NEW_SEQ",StringTool.addString(this.getValue("OLD_SEQ").toString()));
    	   this.setValue("CARD_CODE", parmEKT.getValue("CARD_NO", 0));
    	   //=========yanj20130402 
    		   this.setValue("EKT_CARD_NO", parmEKT.getValue("EKT_CARD_NO", 0));
//    	   this.setValue("EKT_CARD_NO", "");
//           setValue("EKTMR_NO", getValue("MR_NO"));
           setValue("TOP_UP_PRICE", "");
           //========yanj��Ƭ����
           setValue("EKTCARD_CODE", getValue("EKT_CARD_NO"));
           this.setValue("CURRENT_BALANCE",StringTool.round(parmEKT.getDouble("CURRENT_BALANCE", 0),2) );
           this.setValue("CURRENT_BALANCE1",StringTool.round(parmEKT.getDouble("CURRENT_BALANCE", 0),2));
//           bankFlg=true;//���п�ִ�й���
           //     yanj�س������ƶ�
           this.grabFocus("EKT_CARD_NO");
       }

  }
  /**
   * ��Ƭ��ӡ
   */
  public void onPrint(){}

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
   * ��д��
   */
  public void onRenew(){
	  onRenewT(true);
  }
  /**
   * 
   * @param messageflg ��Ϣ���Ƿ���ʾ��д���ɹ�
   * @param amtFlg �Ƿ��ʼ������combox
   * @return
   */
  private  boolean onRenewT(boolean amtFlg) {
	  /**
       * zhangp 20111219 ���ֶ�
       */
      String ektCardNo= this.getValue("EKT_CARD_NO").toString();
//      if(ektCardNo.length()==0){
//    	  this.messageBox("�����뿨Ƭ����");
//    	  return false;
//      }
//    if (!txReadEKT()) {
//    return;
//}
      //======zhangp 20120225 modify start
//      if(null == parmEKT || parmEKT.getValue("CARD_NO",0).length()<=0){
//          this.messageBox("û�л�ò�����Ϣ");
//          return;
//      }
    //======zhangp 20120225 modify end
      
		// add by huangtt 20140916 start
		String memCardNo1 = pat.getMrNo();
		if (ektFlg) {
			memCardNo1 += this.getValue("OLD_SEQ"); // ���
		} else {
			memCardNo1 += this.getValue("NEW_SEQ"); // ���
		}
		TParm memCardNoParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT MR_NO FROM MEM_TRADE WHERE MEM_CARD_NO = '" + memCardNo1
						+ "' AND STATUS = '0'"));
		if (memCardNoParm.getCount() > 0) {
			this.messageBox("����ɻ�Ա�Ѻ��ٻ�����");
			return false;
		}
		//add by huangtt 20140916 end
      
      
      //����
      if (this.getValue("CARD_PWD").toString().length() <= 0) {
          this.messageBox("����������");
          return false;
      }
      
      //===huangtt 20140122
      if(!passWord()){
    	  return false;
      }
      
      if (((TTextFormat)this.getComponent("SEND_CAUSE")).getText().length() <= 0) {
          this.messageBox("����ԭ�򲻿���Ϊ��ֵ");
          return false;
      }

   // ̩��ҽ�ƿ�д������
      if (this.messageBox("��д��", "�Ƿ�ִ��д������", 0) != 0) {
          return false;
      }
      
      //�ڴ˴������ж��Ƿ����ݿ��Ǯ�Ϳ��� ����ҳ���ϵ� �����������ʾ ��ҽ�ƿ���Ǯ�б䶯�����ʵ���棬����ͬ����ʾ
     
      String sql = "SELECT CARD_NO,MR_NO,CURRENT_BALANCE,OPT_DATE FROM EKT_MASTER WHERE MR_NO = '"+pat.getMrNo()+"' ORDER BY OPT_DATE DESC";
      
      TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql));
      if(this.getValueDouble("CURRENT_BALANCE1") != parm2.getDouble("CURRENT_BALANCE", 0)) {
    	  this.messageBox("��ҽ�ƿ��Ľ���б䶯,���ʵ");
          return false;
      };
     
      
      if (null != pat && null != pat.getMrNo() && pat.getMrNo().length() > 0) {
			TParm parm = new TParm();
			if (ektFlg) {
				parm.setData("SEQ", this.getValue("OLD_SEQ")); // ���
			} else {
				parm.setData("SEQ", this.getValue("NEW_SEQ")); // ���
			}
			parm.setData("CARD_NO", pat.getMrNo() + parm.getValue("SEQ")); // ������
			parm.setData("CURRENT_BALANCE", this.getValue("CURRENT_BALANCE")); // ���
			parm.setData("MR_NO", pat.getMrNo()); // ������
			parm.setData("TYPE", "00"); // ����� huangtt
			TParm parmMemCode = new TParm(TJDODBTool.getInstance().select(
					MEMSQL.getMemInfo(pat.getMrNo())));
			if (!parmMemCode.getValue("MEM_CODE", 0).equals("")) {
				parm.setData("TYPE", parmMemCode.getValue("MEM_CODE", 0));
			}
			
			TParm result = new TParm();
              TParm p=new TParm();
              p.setData("CARD_NO",parm.getValue("CARD_NO"));//����
              p.setData("MR_NO",pat.getMrNo());//������
              p.setData("CARD_SEQ",parm.getValue("SEQ"));//���
              p.setData("ISSUE_DATE", SystemTool.getInstance().getDate());//����ʱ��
              p.setData("ISSUERSN_CODE",this.getValue("SEND_CAUSE"));//����ԭ��
              p.setData("FACTORAGE_FEE", this.getValueDouble("PROCEDURE_PRICE"));//������
              p.setData("PASSWORD", OperatorTool.getInstance().encrypt(this.getValue("CARD_PWD").toString()));//�������
              p.setData("WRITE_FLG", "Y");//д������ע��
              p.setData("OPT_USER", Operator.getID());
              p.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
              p.setData("OPT_TERM", Operator.getIP());
              p.setData("ID_NO", pat.getIdNo());//���֤����
              p.setData("CASE_NO", "none");//�����
              p.setData("NAME", pat.getName());//��������
//              p.setData("CURRENT_BALANCE", ektCurrentBalance);//��Ĭ�Ͻ��
              p.setData("CURRENT_BALANCE", this.getValue("CURRENT_BALANCE"));
//              p.setData("CURRENT_BALANCE", sum);
              p.setData("CREAT_USER", Operator.getID());//������
              p.setData("FLG",ektFlg);
              //zhangp 20121219 ���ֶΣ����ţ�
              p.setData("EKT_CARD_NO",ektCardNo);
//              if(ektFlg){
              String sendCause = getValue("SEND_CAUSE").toString();
              p.setData("CHARGE_FLG", sendCause); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����,6,����,7,�˷�,8,����)
//              }else{
//                  p.setData("CHARGE_FLG", "5"); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����)
//              }
              //zhangp 20111222 EKT_BIL_PAY
              TParm bilParm = new TParm();
              //zhangp 20120116 ����+��ʧ ԭ3 ��2
              //===zhangp 20120322 start
              int accntType = 2;
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
              bilParm.setData("PROCEDURE_AMT", this.getValueDouble("PROCEDURE_PRICE"));	//PROCEDURE_AMT
              p.setData("bilParm", getBilParm(p,bilParm).getData());
              //��ϸ�����
               TParm feeParm=new TParm();
               feeParm.setData("ORIGINAL_BALANCE",ektCurrentBalance);
               feeParm.setData("BUSINESS_AMT",0);
               feeParm.setData("CURRENT_BALANCE",ektCurrentBalance);
               p.setData("businessParm",getBusinessParm(p,feeParm).getData());
              //ִ�в�����д������
               result = TIOM_AppServer.executeAction(
                "action.ekt.EKTAction",
                "TXEKTRenewCard", p); 
				if (result.getErrCode() < 0) {
					this.messageBox("ҽ�ƿ�д��ʧ��");
					return false;
				}else{
					this.messageBox("ҽ�ƿ�" + this.getText("SEND_CAUSE")+ "�����ɹ�");
					// д������
					try {
						EKTIO.getInstance().TXwriteEKT(parm);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						this.messageBox("ҽ�ƿ�д��ʧ��");
						e1.printStackTrace();
						return false;
					}
					// =======yanj
					if (amtFlg && this.getValue("SEND_CAUSE").equals("8")) {
						this.messageBox("����ȡ���������ѣ�");
					}
				
					//======add by huangtt 20140509
					TParm parmMem = new TParm();
					parmMem.setData("MEM_CARD_NO_OLD", memCardNo);
					parmMem.setData("MR_NO",pat.getMrNo());
					parmMem.setData("MEM_CARD_NO",parm.getValue("CARD_NO"));
					result = TIOM_AppServer.executeAction("action.mem.MEMAction","updateMemCardNo", parmMem); 
					if(result.getErrCode()<0){
						this.messageBox("���Ÿ���ʧ��");
					}
					
					this.onBaseClear(amtFlg);
                  
              }
//          }
      }else{
          this.messageBox("��д������ʧ��,û�в�����Ϣ");
          return false;
      }
      return true;
//      txReadEKT();
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
       bilParm.setData("CHARGE_FLG",p.getValue("CHARGE_FLG")); //״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����)
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
       billParm.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());	//�ۿ�����ʱ��
       billParm.setData("PROCEDURE_AMT", 0.00);	//PROCEDURE_AMT
       return billParm;
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
    */
   public void onSave() {
       if (pat != null)
           PatTool.getInstance().unLockPat(pat.getMrNo());
       //���������ֵ
       if (getValue("BIRTH_DATE") == null) {
           this.messageBox("�������ڲ���Ϊ��!");
           return;
       }
     //add by huangtt 20140320
		if (getValueString("PAT_NAME").equals("")) {
			if (getValueString("FIRST_NAME").equals("")
					&& !getValueString("LAST_NAME").equals("")) {
				this.messageBox("������firstName!");
				this.grabFocus("FIRST_NAME");
				return;
			} else if (!getValueString("FIRST_NAME").equals("")
					&& getValueString("LAST_NAME").equals("")) {
				this.messageBox("������lastName!");
				this.grabFocus("LAST_NAME");
				return;
			} else if (!getValueString("FIRST_NAME").equals("")
					&& !getValueString("LAST_NAME").equals("")) {
				this.setValue("PAT_NAME1", getValueString("FIRST_NAME") + " "
						+ getValueString("LAST_NAME"));
				if (this.messageBox("������ֵ", "�Ƿ�firstName��lastName�ϲ���ֵ��������", 0) != 0) {
					this.messageBox("��������Ϊ��!");
					return;
				}
				this.setValue("PAT_NAME", getValueString("FIRST_NAME") + " "
						+ getValueString("LAST_NAME"));

			} else if (getValueString("FIRST_NAME").equals("")
					&& getValueString("LAST_NAME").equals("")) {
				this.messageBox("��������Ϊ��!");
				this.grabFocus("PAT_NAME");
				return;
			}
		} else {
			if (getValueString("FIRST_NAME").equals("")
					&& !getValueString("LAST_NAME").equals("")) {
				this.messageBox("������firstName!");
				this.grabFocus("FIRST_NAME");
				return;
			} else if (!getValueString("FIRST_NAME").equals("")
					&& getValueString("LAST_NAME").equals("")) {
				this.messageBox("������lastName!");
				this.grabFocus("LAST_NAME");
				return;
			} else if (!getValueString("FIRST_NAME").equals("")
					&& !getValueString("LAST_NAME").equals("")) {
				this.setValue("PAT_NAME1", getValueString("FIRST_NAME") + " "
						+ getValueString("LAST_NAME"));
			}
		}
       if (!this.emptyTextCheck("SEX_CODE,CTZ1_CODE"))
           return;
       
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
       pat.setTelHome(TypeTool.getString(getValue("TEL_HOME")));
       //�ʱ�
       pat.setPostCode(TypeTool.getString(getValue("POST_CODE")));
       //��ַ
       pat.setAddress(TypeTool.getString(getValue("CURRENT_ADDRESS")));
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
       pat.setRemarks(TypeTool.getString(getValue("REMARKS")));
       pat.setSpeciesCode(TypeTool.getString(""));
       pat.setFirstName(TypeTool.getString(getValue("FIRST_NAME")));
	   pat.setLastName(TypeTool.getString(getValue("LAST_NAME")));
       
       if (this.messageBox("������Ϣ", "�Ƿ񱣴�", 0) != 0)
           return;
       
       TParm patParm = new TParm();
       patParm.setData("MR_NO", getValue("MR_NO"));
       patParm.setData("PAT_NAME", getValue("PAT_NAME"));
       patParm.setData("PAT_NAME1", getValue("PAT_NAME1"));
       patParm.setData("LAST_NAME", getValue("LAST_NAME"));
	   patParm.setData("FIRST_NAME", getValue("FIRST_NAME"));
       patParm.setData("PY1", getValue("PY1"));
       patParm.setData("IDNO", getValue("IDNO"));
       patParm.setData("BIRTH_DATE", getValue("BIRTH_DATE"));
       patParm.setData("TEL_HOME", getValue("TEL_HOME"));
       patParm.setData("SEX_CODE", getValue("SEX_CODE"));
       patParm.setData("POST_CODE", getValue("POST_CODE"));
       patParm.setData("RESID_ADDRESS", getValue("RESID_ADDRESS"));
       patParm.setData("CTZ1_CODE", getValue("CTZ1_CODE"));
       patParm.setData("CTZ2_CODE", getValue("CTZ2_CODE"));
       patParm.setData("CTZ3_CODE", getValue("CTZ3_CODE"));
       //add by huangtt 20140218 start
       patParm.setData("NATION_CODE",getValueString("NATION_CODE"));
	   patParm.setData("SPECIES_CODE",SPECIES_CODE);
	   patParm.setData("MARRIAGE_CODE",MARRIAGE_CODE);
	   //add by huangtt 20140218 end
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
       if (StringUtil.isNullString(getValueString("IDNO"))) {
           patParm.setData("IDNO", new TNull(String.class));
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
       if (StringUtil.isNullString(getValueString("BIRTH_DATE"))) {
           patParm.setData("BIRTH_DATE", new TNull(Timestamp.class));
       }
       if (StringUtil.isNullString(getValueString("TEL_HOME"))) {
           patParm.setData("TEL_HOME", new TNull(String.class));
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
		if (StringUtil.isNullString(getValueString("REMARKS"))) {
			patParm.setData("REMARKS", new TNull(String.class));
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
           }else{
               setValue("MR_NO", pat.getMrNo());
               setValue("CARD_CODE", pat.getMrNo() + "001");
               setValue("NEW_SEQ", "001");
               ektFlg = true; //�½�ҽ�ƿ�����
           }
       }
       if (result.getErrCode() != 0) {
           this.messageBox("E0005");
       } else {
           this.messageBox("P0005");
       }
//        if (ektCard != null || ektCard.length() != 0) {
//            EKTIO.getInstance().saveMRNO(this.getValueString("MR_NO"), this);
//        }
      // onClear();
   }
/**
 * ��ջ�����Ϣ
 */
   public void onBaseClear(boolean flg) {
	   //=======yanj
	   clearValue("MR_NO;PAT_NAME;PAT_NAME1;PY1;IDNO;FOREIGNER_FLG;ID_TYPE;" +
               "BIRTH_DATE;SEX_CODE;CELL_PHONE;POST_CODE;STATE;CITY;ADDRESS;" +
               "CTZ2_CODE;CTZ3_CODE;CARD_CODE;EKT_CARD_NO;CARD_PWD;" +
               "CURRENT_BALANCE;OLD_SEQ;NEW_SEQ;CARD_PWD;RE_CARD_PWD;NATION_CODE;" +
               "EKTMR_NO;CURRENT_BALANCE1;EKTCARD_CODE;CURRENT_ADDRESS;REMARKS;" +
               "FIRST_NAME;LAST_NAME;CELL_PHONE;RESID_ADDRESS");


       //callFunction("UI|FOREIGNER_FLG|setEnabled", true); //����֤���ɱ༭======pangben modify 20110808
       callFunction("UI|MR_NO|setEnabled", true); //�����ſɱ༭
       callFunction("UI|CARD_CODE|setEnabled", true); //ҽ�ƿ��ſ��Ա༭
       setValue("CTZ1_CODE", "99");
       //����Ĭ�Ϸ���ȼ�
       ektFlg=false;
//       parmEKT=null;//ҽ�ƿ���Ϣ
       EKTTemp=null;
       parmSum=null;
       //===zhangp 20120724 start
       ektCurrentBalance = 0;
       //===zhangp 20120724 end
       //��������
       if (pat != null)
           PatTool.getInstance().unLockPat(pat.getMrNo());
       setValue("SEND_CAUSE", 8);
       if(flg)
    	   onSendCause();
       TParm result = EKTTool.getInstance().factageFee(getValue("SEND_CAUSE").toString());
       if(result.getCount()>0){
      	 setValue("PROCEDURE_PRICE", result.getDouble("FACTORAGE_FEE", 0));
       }
       this.setPassWord();
   }
   /**
    *���
    */
   public void onClear() {
	   //=======yanj
       clearValue("MR_NO;PAT_NAME;PAT_NAME1;PY1;IDNO;FOREIGNER_FLG;ID_TYPE;" +
                  "BIRTH_DATE;SEX_CODE;POST_CODE;STATE;CITY;ADDRESS;" +
                  "CTZ2_CODE;CTZ3_CODE;CARD_CODE;EKT_CARD_NO;CARD_PWD;BIL_CODE;" +
                  "CURRENT_BALANCE;OLD_SEQ;NEW_SEQ;CARD_PWD;RE_CARD_PWD;NATION_CODE;DESCRIPTION;" +
                  "EKTMR_NO;CURRENT_BALANCE1;EKTCARD_CODE;GATHER_PRICE;TOP_UP_PRICE;CURRENT_ADDRESS;REMARKS;" +
                  "FIRST_NAME;LAST_NAME;CELL_PHONE;RESID_ADDRESS;CARD_TYPE");

       //callFunction("UI|FOREIGNER_FLG|setEnabled", true); //����֤���ɱ༭======pangben modify 20110808
       callFunction("UI|MR_NO|setEnabled", true); //�����ſɱ༭
       callFunction("UI|CARD_CODE|setEnabled", true); //ҽ�ƿ��ſ��Ա༭
       setValue("CTZ1_CODE", "99");
       setValue("NATION_CODE", "86");
       String sum = EKTTool.getInstance().getPayTypeDefault();
       setValue("GATHER_TYPE1", sum);
       //����Ĭ�Ϸ���ȼ�
       ektFlg=false;
       //======yanj
//       parmEKT=null;//ҽ�ƿ���Ϣ
       EKTTemp=null;
       parmSum=null;
       //===zhangp 20120724 start
       ektCurrentBalance = 0;
       //===zhangp 20120724 end
       //��������
       if (pat != null)
           PatTool.getInstance().unLockPat(pat.getMrNo());
       setValue("SEND_CAUSE", 8);
       onSendCause();
       TParm result = EKTTool.getInstance().factageFee(getValue("SEND_CAUSE").toString());
       if(result.getCount()>0){
      	 setValue("PROCEDURE_PRICE", result.getDouble("FACTORAGE_FEE", 0));
       }
       setPassWord();
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
			this.messageBox("����ʧ�ܣ�");
			e.printStackTrace();
			return false;
		}
       if (null == EKTTemp || EKTTemp.getValue("MR_NO").length() <= 0) {
           this.messageBox(EKTTemp.getErrText());
           return false;
       }
       return true;
   }
  //======yanj
   /**
    * �շѷ���
    */
   public void onFEE(){
//	   ektFlg = false;
	   topUpFee();//add by sunqy 20140728�Զ�����Ӧ�շ��� ���ⲻ�س��͵���շѵ����
	   //��������
//       if (!txReadEKT()) {
//           return;
//       }
//		String parm_MR_NO = EKTTemp.getValue("MR_NO");// ��Ƭ������
//		TParm parmss = new TParm();
//		parmss.setData("MR_NO", pat.getMrNo());
//		parmEKT= EKTTool.getInstance().selectEKTIssuelog(parmss);//���ݿⲡ����
	   
	   if (this.getValue("EKTMR_NO").equals("")) {
			this.messageBox("�����뻻����Ϣ��");
			return;
		}
		if (this.getValueDouble("TOP_UP_PRICE") <= 0) {
			this.messageBox("�������ֵ��");
			return;
		}
		String sendCause = getValue("SEND_CAUSE").toString();
		if(sendCause.equals("8")){//�������������ֵ��ť ����С��������
			if (this.getValueDouble("CURRENT_BALANCE1")+this.getValueDouble("TOP_UP_PRICE") < this.getValueDouble("PROCEDURE_PRICE1")) {
				this.messageBox("��ֵ�Ľ���С��"+this.getValueDouble("PROCEDURE_PRICE1")+"Ԫ");
				return;
			}
		}
		if (((TTextFormat) this.getComponent("GATHER_TYPE1")).getText()
				.length() <= 0) {
			this.messageBox("֧����ʽ������Ϊ��ֵ");
			return;
		}
		//���֧����ʽΪˢ��ʱ�������ͺͿ���Ϊ������add by huangjw 20150115
		if("C1".equals(this.getValue("GATHER_TYPE1"))){
			if("".equals(this.getValue("CARD_TYPE"))||this.getValue("CARD_TYPE")==null){
				this.messageBox("�����Ͳ���Ϊ��");
				return;
			}
			if("".equals(this.getValue("DESCRIPTION"))){
				this.messageBox("��ע����Ϊ��");
				return;
			}
		}
		//���֧����ʽΪ΢�Ż�֧����ʱ�����׺ű���add by huangjw 20150115
		if("WX".equals(this.getValue("GATHER_TYPE1"))||"ZFB".equals(this.getValue("GATHER_TYPE1"))){
			if("".equals(this.getValue("BIL_CODE"))){
				this.messageBox("΢�Ż�֧������Ʊ�ݺ�������д���׺ţ�");
				return;
			}
		}
//		if (!(parmEKT.getValue("MR_NO", 0).equals(parm_MR_NO))) {//��Ƭ������ϵĲ����Ų���
//			if (!this.onRenewT(false))
//			return;
//		// yanj20130316����д��֮�����
//		if (!txReadEKT()) {
//			return;
//		}
//		}else{//������Ϣ��ͬ��ֻ������ֵ����ȥ����������������
//			this.setValue("PROCEDURE_PRICE1",0.00);
//		}

		if (!this.onRenewT(false))
			return;
		// yanj20130316����д��֮�����
		if (!txReadEKT()) {
			return;
		}
		TParm parmll = new TParm();
		parmll.setData("MR_NO", pat.getMrNo());
		parmEKT = EKTTool.getInstance().selectEKTIssuelog(parmll);
		this.setValue("CURRENT_BALANCE1", parmEKT.getDouble("CURRENT_BALANCE",0));
		
		if (parmEKT.getCount("MR_NO") > 1) {
			this.messageBox("�˲�����Ϣ����");
			return;
		}

		 //add by huangtt 20140228
        if (this.messageBox("��ֵ", "�Ƿ��ֵ"+(this.getValueDouble("TOP_UP_PRICE")-this.getValueDouble("PROCEDURE_PRICE1"))+"Ԫ", 0) != 0) {
            return;
        }
		
       if (parmEKT == null || parmEKT.getCount() <= 0||parmEKT.getValue("MR_NO").length() <= 0) {
           this.messageBox("û�л��ҽ�ƿ���Ϣ");
           return;
       }
       double cardAmt=StringTool.round(this.getValueDouble("CURRENT_BALANCE1") +
	   				this.getValueDouble("TOP_UP_PRICE" )-this.getValueDouble("PROCEDURE_PRICE1"),2);//�������+��ֵ���-������
           TParm result =null;
    	   parmSum = new TParm();
           parmSum.setData("CARD_NO", pat.getMrNo() + parmEKT.getValue("CARD_SEQ",0));
           parmSum.setData("CURRENT_BALANCE", cardAmt);//ҽ�ƿ���ǰ���
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
           parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE1")); //֧����ʽ
           parmSum.setData("GATHER_TYPE_NAME", this.getText("GATHER_TYPE1")); //֧����ʽ����
           parmSum.setData("BUSINESS_AMT",StringTool.round(this.getValueDouble("TOP_UP_PRICE")-this.getValueDouble("PROCEDURE_PRICE1"),2)); //��ֵ���
           parmSum.setData("SEX_TYPE", this.getValue("SEX_CODE")); //�Ա�
           parmSum.setData("DESCRIPTION", this.getValue("DESCRIPTION")); //��עDESCRIPTION
           parmSum.setData("PRINT_NO", this.getValue("BIL_CODE")); //
           parmSum.setData("BIL_CODE", this.getValue("BIL_CODE")); //Ʊ�ݺ�
           parmSum.setData("CREAT_USER", Operator.getID()); //ִ����Ա//=====yanjing
            //��ϸ�����
            TParm feeParm=new TParm();
//            feeParm.setData("ORIGINAL_BALANCE",
//                    StringTool.round(parmEKT.getDouble("CURRENT_BALANCE"),2));
            feeParm.setData("ORIGINAL_BALANCE",
                  this.getValue("CURRENT_BALANCE1"));
            feeParm.setData("BUSINESS_AMT",StringTool.round(this.getValueDouble("TOP_UP_PRICE" ),2) - StringTool.round(this.getValueDouble("PROCEDURE_PRICE1"),2));
//            feeParm.setData("CURRENT_BALANCE","99");
            feeParm.setData("CURRENT_BALANCE",StringTool.round(this.getValueDouble("CURRENT_BALANCE1"),2)+StringTool.round(this.getValueDouble("TOP_UP_PRICE"),2)- StringTool.round(this.getValueDouble("PROCEDURE_PRICE1"),2));
            parmSum.setData("businessParm",getBusinessParm(parmSum,feeParm).getData());
            parmSum.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());	//�ۿ�����ʱ��
            parmSum.setData("PROCEDURE_AMT", 0.00);	//PROCEDURE_AMT
            parmSum.setData("CARD_TYPE",this.getValueString("CARD_TYPE"));
            //bil_pay ��ֵ������
            parmSum.setData("billParm",getBillParm(parmSum,feeParm).getData());
           //�������
            result = TIOM_AppServer.executeAction(
                "action.ekt.EKTAction",
                "TXEKTonFee", parmSum);
           if (result.getErrCode() < 0) {
               this.messageBox("ҽ�ƿ���ֵʧ��");
               } else{
               this.messageBox("��ֵ�ɹ�");
               String bil_business_no = result.getValue("BIL_BUSINESS_NO"); //�վݺ�
               onPrint(bil_business_no);//��ӡƱ��
               //=======yanj
               this.setValue("EKTMR_NO","");
               this.setValue("CURRENT_BALANCE1","" );
               this.setValue("TOP_UP_PRICE", "");
               this.setValue("GATHER_PRICE","");
               onClear();
           }
   }
   
   /**
    * ��ⲡ����ͬ����
    */
   public void onPatName() {
//       String patName = this.getValueString("PAT_NAME");
//       if (StringUtil.isNullString(patName)) {
//           return;
//       }
       //REPORT_DATE;PAT_NAME;IDNO;SEX_CODE;BIRTH_DATE;POST_CODE;ADDRESS
//       String selPat =
//               " SELECT OPT_DATE AS REPORT_DATE,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE," +
//               "        POST_CODE,ADDRESS,MR_NO " +
//               "   FROM SYS_PATINFO " +
//               "  WHERE PAT_NAME = '" + patName + "' " +
//               "  ORDER BY OPT_DATE ";
//       TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
//       if (same.getErrCode() != 0) {
//           this.messageBox_(same.getErrText());
//       }
//       setPatName1();
       //ѡ�񲡻���Ϣ
//       if (same.getCount("MR_NO") > 0) {
//           int sameCount = this.messageBox("��ʾ��Ϣ", "������ͬ����������Ϣ,�Ƿ�������������Ϣ", 0);
//           if (sameCount != 1) {
//               this.grabFocus("PY1");
//               return;
//           }
//           Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x",
//                                   same);
//           TParm patParm = new TParm();
//           if (obj != null) {
//               patParm = (TParm) obj;
//               this.setValue("MR_NO",patParm.getValue("MR_NO"));
//               onQueryNO();
//               return;
//           }
//       }
   	//yuml 20141023
		String patName = this.getValueString("PAT_NAME");
		if (StringUtil.isNullString(patName)) {	
			return;
		}
       this.grabFocus("PY1");
       onQueryPat();
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
      double price=this.getValueDouble("TOP_UP_PRICE");
      double fee = this.getValueDouble("PROCEDURE_PRICE1");
      price -= fee; 
      this.setValue("GATHER_PRICE",price);
      //yanj �س������ƶ�
      this.grabFocus("onFee");
      
  }

  /**
   * ҽ�ƿ��޸�����
   */
  public void updateEKTPwd() {
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
                    Operator.getHospitalCHNFullName() : "����ҽԺ") + "(COPY)");
      parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); //������
      parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); //����
      parm.setData("GATHER_TYPE", "TEXT", parmSum.getValue("GATHER_TYPE_NAME")); //�տʽ
      parm.setData("GATHER_NAME", "TEXT", "�� ��"); //�տʽ
      parm.setData("TYPE", "TEXT", "Ԥ ��"); //�ı�Ԥ�ս��
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
      hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
              getDBTime()), "hh:mm"); //ʱ����
//      parm.setData("DESCRIPTION", "TEXT", parmSum.getValue("DESCRIPTION")); //��ע
      parm.setData("BILL_NO", "TEXT", parmSum.getValue("BIL_CODE")); //Ʊ�ݺ�
      if(null == bil_business_no)
           bil_business_no=  EKTTool.getInstance().getBillBusinessNo();//��ӡ����
      parm.setData("ONFEE_NO", "TEXT", bil_business_no); //�վݺ�
      parm.setData("PRINT_DATE", "TEXT", yMd); //��ӡʱ��
      parm.setData("DATE", "TEXT", yMd + "    " + hms); //����
      parm.setData("USER_NAME", "TEXT", Operator.getID()); //�տ���
      parm.setData("O", "TEXT", ""); 
      this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm);
      */
	  	//modify by sunqy 20140611 ----start----
		boolean flg = false;
		parmSum.setData("TITLE", "�����ֵ�վ�");
		parmSum.setData("BIL_BUSINESS_NO",bil_business_no);
		EKTReceiptPrintControl.getInstance().onPrint(null, parmSum, "", -1, pat, flg, this);
		//modify by sunqy 20140611 ----end----
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
	   bilParm.setData("CARD_TYPE", "");
	   
	   return bilParm;
  }
  
	/**
	 * ��ѯ���� ===========zhangp 20111223
	 */
	public void onQueryPat() {
		TParm sendParm = new TParm();
		sendParm.setData("PAT_NAME", this.getValue("PAT_NAME"));
		//yuml  start   20141022
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
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMPatQuery.x", result);
		if (reParm == null)
			return;
		this.setValue("MR_NO", reParm.getValue("MR_NO"));
		this.onQueryNO();
		// zhangp 20111223
		this.grabFocus("PY1");
     }
}

	/**
	 * ����ԭ����� ====zhangp 20120202
	 */
	public void onSendCause() {
		switch (this.getValueInt("SEND_CAUSE")) {// ����ѡ�� ����ֵ��ť�仯
		case 5:
			this.setText("onFee", "��������ֵ");
			break;
		case 8:
			this.setText("onFee", "��������ֵ");
			break;
		default:
			this.setValue("SEND_CAUSE", 8);
			this.messageBox("������ѡ��");
			break;
		}
		TParm result = EKTTool.getInstance().factageFee(
				getValue("SEND_CAUSE").toString());
		// ======yanj
		setValue("PROCEDURE_PRICE1", result.getDouble("FACTORAGE_FEE", 0));
		if (result.getCount() > 0) {
			setValue("PROCEDURE_PRICE", result.getDouble("FACTORAGE_FEE", 0));
			this.setValue("EKT_CARD_NO", "");
			this.setValue("EKTCARD_CODE", "");
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
		  grabFocus("TOP_UP_PRICE");
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
	 * ��ҽ�ƿ�
	 */
	public void onReadEKT() {
		// ��ȡҽ�ƿ�
		TParm parmEKT = EKTIO.getInstance().TXreadEKT();
		if (null == parmEKT || parmEKT.getErrCode() < 0
				|| parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmEKT.getErrText());
			parmEKT = null;
			return;
		}
		this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
		this.onQueryNO();
	}


}
