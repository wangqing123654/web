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
 * <p>Title: 医疗卡补卡挂失</p>
 * 
 *
 * <p>Description: 医疗卡补卡挂失</p>
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
	 private Pat  pat;//  病患信息
	  private TParm parmEKT;//医疗卡集合
	  private boolean ektFlg=false;//此病患是否存在医疗卡
	  private TParm EKTTemp;//医疗卡最初值
	  private TParm parmSum;//执行充值操作参数
	  private String SPECIES_CODE=""; //民族 
	  private String MARRIAGE_CODE=""; //婚姻 
	  //private boolean reEktFlg=false;//此病患是否已经补卡
	//  private boolean bankFlg=false;//银行卡关联设置
	  //add by huangtt 20140410 start
	
	   private String memCardNo = "";  //会员卡号

      //add by huangtt 20140410 end
	
    /**
   * 初始化方法
   */
  public void onInit() {
	  //======yanj20130403
     setValue("CTZ1_CODE", "99");
     setValue("CURRENT_BALANCE", "");
//     setValue("CURRENT_BALANCE1", "");
//     setValue("CURRENT_BALANCE1", "");
     
     /**
      * zhangp 20121216 国籍默认中国
      */
     setValue("NATION_CODE", "86");
     /**
      * zhangp 20121216 传入病案号
      */
     Object obj = this.getParameter();
     if (obj instanceof TParm) {
         acceptData = (TParm) obj;
         String mrNo = acceptData.getData("MR_NO").toString();
         this.setValue("MR_NO", mrNo);
         this.onQueryNO();
     }
     this.setValue("TOP_UP_PRICE", "");
     //zhangp 20120202 下拉框默认值
     		setValue("SEND_CAUSE", 8);
     //yanj 手续费
     TParm result = EKTTool.getInstance().factageFee(getValue("SEND_CAUSE").toString());
     if(result.getCount()>0){
    	 setValue("PROCEDURE_PRICE", result.getDouble("FACTORAGE_FEE", 0));
    	 setValue("PROCEDURE_PRICE1", result.getDouble("FACTORAGE_FEE", 0));
     }
     //=====zhangp 20120224 支付方式    modify start 
     String id = EKTTool.getInstance().getPayTypeDefault();
     setValue("GATHER_TYPE", id);
     setValue("GATHER_TYPE1", id);
     //=======zhangp 20120224 modify end
     //=====yanj 20130308 充值支付方式 
//     String num = EKTTool.getInstance().getPayTypeDefault();
//     setValue("GATHER_TYPE1", num);
     setPassWord();

  }
 
  /**
   * zhangp 20121216 传入病案号
   */
  private TParm acceptData = new TParm(); //接参
  private double ektCurrentBalance = 0.00;
  /**
   * 查询方法
   */
  public void onQuery(){
	  if(this.getValueString("MR_NO").length()==0){
		   String patName = this.getValueString("PAT_NAME");
		   String sexCode = this.getValueString("SEX_CODE");
		   String idNo = this.getValueString("IDNO");
		   if(patName.length() > 0 && sexCode.length() == 0){
			   this.messageBox("请填写性别信息,以便查询!"); 
			   return;
		   }
		   if(patName.length() == 0 && sexCode.length() > 0){
			   this.messageBox("请填写姓名信息,以便查询!"); 
			   return;
		   }
		   if(patName.length() == 0 && sexCode.length() == 0 && idNo.length() == 0){
			   this.messageBox("请填写查询信息");
			   return;
		   }
		   TParm parm = new TParm();
		   parm.setData("PAT_NAME", patName);
		   parm.setData("SEX_CODE", sexCode);
		   parm.setData("IDNO", idNo); 
		   TParm result = EKTTool.getInstance().getPatInfo(parm);
		   if(result.getCount()<0){
			   this.messageBox("没有要查询的数据!");
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
   * 病案号文本框事件
   */
  public void onQueryNO(){
      onQueryNO(true);
  }
  /**
   * 查询方法
   */
  private void onQueryNO(boolean flg) {
      if (pat != null)
          PatTool.getInstance().unLockPat(pat.getMrNo());

      pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
      if (pat == null) {
          this.messageBox("无此病案号!");
          this.grabFocus("PAT_NAME");
          if(!flg){
              this.setValue("MR_NO", "");
              callFunction("UI|MR_NO|setEnabled", false); //病案号可编辑
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
     //zhangp 20111223 国籍
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
       //锁病患信息
//            this.messageBox_("加锁成功!");//测试专用 
       this.grabFocus("onFee");
       callFunction("UI|MR_NO|setEnabled", false); //病案号不可编辑
       if(parmEKT.getCount()<=0){
//           this.messageBox("此病患没有医疗卡信息");
           //新卡创建
           this.setValue("CARD_CODE",pat.getMrNo()+"001"); 
           this.setValue("OLD_SEQ","001");
           this.setValue("NEW_SEQ","001");
           //======yanj卡片号码
           this.setValue("EKTCARD_CODE", this.getValue("EKT_CARD_NO"));
           this.setValue("CURRENT_BALANCE1", "");
           this.setValue("CURRENT_BALANCE", "");
           
//           setValue("EKTMR_NO", getValue("MR_NO"));      
           //不存在医疗卡，写卡时操作创建EKT_MASTER表信息
           ektFlg=true;
       }else{
    	   //add by huangtt 20140414 start 会员之前有医疗卡，现在执行换卡
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
           //========yanj卡片号码
           setValue("EKTCARD_CODE", getValue("EKT_CARD_NO"));
           this.setValue("CURRENT_BALANCE",StringTool.round(parmEKT.getDouble("CURRENT_BALANCE", 0),2) );
           this.setValue("CURRENT_BALANCE1",StringTool.round(parmEKT.getDouble("CURRENT_BALANCE", 0),2));
//           bankFlg=true;//银行卡执行关联
           //     yanj回车焦点移动
           this.grabFocus("EKT_CARD_NO");
       }

  }
  /**
   * 卡片打印
   */
  public void onPrint(){}

  /**
   * 强制加锁方法
   */
  private void patLock() {
      String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
      //判断是否加锁
      if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
          if (this.messageBox("是否解锁",
                              PatTool.getInstance().getLockParmString(pat.
                  getMrNo()), 0) == 0) {
              PatTool.getInstance().unLockPat(pat.getMrNo());
              PATLockTool.getInstance().log("ODO->" +
                                            SystemTool.getInstance().getDate() +
                                            " " +
                                            Operator.getID() + " " +
                                            Operator.getName() +
                                            " 强制解锁[" + aa + " 病案号：" +
                                            pat.getMrNo() + "]");
          } else {
              pat = null;
              return;
          }
      }

  }
  /**
   * 补写卡
   */
  public void onRenew(){
	  onRenewT(true);
  }
  /**
   * 
   * @param messageflg 消息框是否提示补写卡成功
   * @param amtFlg 是否初始化换卡combox
   * @return
   */
  private  boolean onRenewT(boolean amtFlg) {
	  /**
       * zhangp 20111219 加字段
       */
      String ektCardNo= this.getValue("EKT_CARD_NO").toString();
//      if(ektCardNo.length()==0){
//    	  this.messageBox("请输入卡片号码");
//    	  return false;
//      }
//    if (!txReadEKT()) {
//    return;
//}
      //======zhangp 20120225 modify start
//      if(null == parmEKT || parmEKT.getValue("CARD_NO",0).length()<=0){
//          this.messageBox("没有获得病患信息");
//          return;
//      }
    //======zhangp 20120225 modify end
      
		// add by huangtt 20140916 start
		String memCardNo1 = pat.getMrNo();
		if (ektFlg) {
			memCardNo1 += this.getValue("OLD_SEQ"); // 编号
		} else {
			memCardNo1 += this.getValue("NEW_SEQ"); // 编号
		}
		TParm memCardNoParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT MR_NO FROM MEM_TRADE WHERE MEM_CARD_NO = '" + memCardNo1
						+ "' AND STATUS = '0'"));
		if (memCardNoParm.getCount() > 0) {
			this.messageBox("需缴纳会员费后再换卡！");
			return false;
		}
		//add by huangtt 20140916 end
      
      
      //密码
      if (this.getValue("CARD_PWD").toString().length() <= 0) {
          this.messageBox("请输入密码");
          return false;
      }
      
      //===huangtt 20140122
      if(!passWord()){
    	  return false;
      }
      
      if (((TTextFormat)this.getComponent("SEND_CAUSE")).getText().length() <= 0) {
          this.messageBox("发卡原因不可以为空值");
          return false;
      }

   // 泰心医疗卡写卡操作
      if (this.messageBox("补写卡", "是否执行写卡操作", 0) != 0) {
          return false;
      }
      
      //在此处操作判断是否数据库的钱和卡号 等于页面上的 如果不等于提示 此医疗卡的钱有变动，请核实保存，若相同则不提示
     
      String sql = "SELECT CARD_NO,MR_NO,CURRENT_BALANCE,OPT_DATE FROM EKT_MASTER WHERE MR_NO = '"+pat.getMrNo()+"' ORDER BY OPT_DATE DESC";
      
      TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql));
      if(this.getValueDouble("CURRENT_BALANCE1") != parm2.getDouble("CURRENT_BALANCE", 0)) {
    	  this.messageBox("此医疗卡的金额有变动,请核实");
          return false;
      };
     
      
      if (null != pat && null != pat.getMrNo() && pat.getMrNo().length() > 0) {
			TParm parm = new TParm();
			if (ektFlg) {
				parm.setData("SEQ", this.getValue("OLD_SEQ")); // 编号
			} else {
				parm.setData("SEQ", this.getValue("NEW_SEQ")); // 编号
			}
			parm.setData("CARD_NO", pat.getMrNo() + parm.getValue("SEQ")); // 病案号
			parm.setData("CURRENT_BALANCE", this.getValue("CURRENT_BALANCE")); // 金额
			parm.setData("MR_NO", pat.getMrNo()); // 病案号
			parm.setData("TYPE", "00"); // 卡类别 huangtt
			TParm parmMemCode = new TParm(TJDODBTool.getInstance().select(
					MEMSQL.getMemInfo(pat.getMrNo())));
			if (!parmMemCode.getValue("MEM_CODE", 0).equals("")) {
				parm.setData("TYPE", parmMemCode.getValue("MEM_CODE", 0));
			}
			
			TParm result = new TParm();
              TParm p=new TParm();
              p.setData("CARD_NO",parm.getValue("CARD_NO"));//卡号
              p.setData("MR_NO",pat.getMrNo());//病案号
              p.setData("CARD_SEQ",parm.getValue("SEQ"));//序号
              p.setData("ISSUE_DATE", SystemTool.getInstance().getDate());//发卡时间
              p.setData("ISSUERSN_CODE",this.getValue("SEND_CAUSE"));//发卡原因
              p.setData("FACTORAGE_FEE", this.getValueDouble("PROCEDURE_PRICE"));//手续费
              p.setData("PASSWORD", OperatorTool.getInstance().encrypt(this.getValue("CARD_PWD").toString()));//密码加密
              p.setData("WRITE_FLG", "Y");//写卡操作注记
              p.setData("OPT_USER", Operator.getID());
              p.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
              p.setData("OPT_TERM", Operator.getIP());
              p.setData("ID_NO", pat.getIdNo());//身份证号码
              p.setData("CASE_NO", "none");//就诊号
              p.setData("NAME", pat.getName());//病患名称
//              p.setData("CURRENT_BALANCE", ektCurrentBalance);//金额，默认金额
              p.setData("CURRENT_BALANCE", this.getValue("CURRENT_BALANCE"));
//              p.setData("CURRENT_BALANCE", sum);
              p.setData("CREAT_USER", Operator.getID());//操作人
              p.setData("FLG",ektFlg);
              //zhangp 20121219 加字段（卡号）
              p.setData("EKT_CARD_NO",ektCardNo);
//              if(ektFlg){
              String sendCause = getValue("SEND_CAUSE").toString();
              p.setData("CHARGE_FLG", sendCause); //状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡,6,作废,7,退费,8,换卡)
//              }else{
//                  p.setData("CHARGE_FLG", "5"); //状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡)
//              }
              //zhangp 20111222 EKT_BIL_PAY
              TParm bilParm = new TParm();
              //zhangp 20120116 换卡+挂失 原3 现2
              //===zhangp 20120322 start
              int accntType = 2;
              if(sendCause.equals("4")){//制卡
           	   accntType = 1;
              }
              if(sendCause.equals("5")){//补卡
           	   accntType = 3;
              }
              if(sendCause.equals("8")){//换卡
           	   accntType = 2;
              }
              bilParm.setData("ACCNT_TYPE", accntType);	//明细帐别(1:购卡,2:换卡,3:补卡,4:充值,5:扣款,6:退费)(EKT_BIL_PAY)
              //===zhangp 20120322 end
              bilParm.setData("CURT_CARDSEQ", parm.getValue("SEQ"));	//卡片序号(EKT_BIL_PAY)
              bilParm.setData("GATHER_TYPE", getValue("GATHER_TYPE"));	//充值方式(EKT_BIL_PAY)
              bilParm.setData("AMT", "0");	//AMT(EKT_BIL_PAY)
              //zhangp 20120109 加字段
              bilParm.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());	//售卡操作时间
              bilParm.setData("PROCEDURE_AMT", this.getValueDouble("PROCEDURE_PRICE"));	//PROCEDURE_AMT
              p.setData("bilParm", getBilParm(p,bilParm).getData());
              //明细表参数
               TParm feeParm=new TParm();
               feeParm.setData("ORIGINAL_BALANCE",ektCurrentBalance);
               feeParm.setData("BUSINESS_AMT",0);
               feeParm.setData("CURRENT_BALANCE",ektCurrentBalance);
               p.setData("businessParm",getBusinessParm(p,feeParm).getData());
              //执行补卡的写卡操作
               result = TIOM_AppServer.executeAction(
                "action.ekt.EKTAction",
                "TXEKTRenewCard", p); 
				if (result.getErrCode() < 0) {
					this.messageBox("医疗卡写卡失败");
					return false;
				}else{
					this.messageBox("医疗卡" + this.getText("SEND_CAUSE")+ "操作成功");
					// 写卡操作
					try {
						EKTIO.getInstance().TXwriteEKT(parm);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						this.messageBox("医疗卡写卡失败");
						e1.printStackTrace();
						return false;
					}
					// =======yanj
					if (amtFlg && this.getValue("SEND_CAUSE").equals("8")) {
						this.messageBox("请收取换卡手续费！");
					}
				
					//======add by huangtt 20140509
					TParm parmMem = new TParm();
					parmMem.setData("MEM_CARD_NO_OLD", memCardNo);
					parmMem.setData("MR_NO",pat.getMrNo());
					parmMem.setData("MEM_CARD_NO",parm.getValue("CARD_NO"));
					result = TIOM_AppServer.executeAction("action.mem.MEMAction","updateMemCardNo", parmMem); 
					if(result.getErrCode()<0){
						this.messageBox("卡号更新失败");
					}
					
					this.onBaseClear(amtFlg);
                  
              }
//          }
      }else{
          this.messageBox("补写卡操作失败,没有病患信息");
          return false;
      }
      return true;
//      txReadEKT();
}
  /**
    * 医疗卡明细表插入数据
    * @param p TParm
    * @param feeParm TParm
    * @return TParm
    */
   private TParm getBusinessParm(TParm p, TParm feeParm) {
       // 明细档数据
       TParm bilParm = new TParm();
       bilParm.setData("BUSINESS_SEQ", 0);
       bilParm.setData("CARD_NO", p.getValue("CARD_NO"));
       bilParm.setData("MR_NO", pat.getMrNo());
       bilParm.setData("CASE_NO", "none");
       bilParm.setData("ORDER_CODE", p.getValue("ISSUERSN_CODE"));
       bilParm.setData("RX_NO", p.getValue("ISSUERSN_CODE"));
       bilParm.setData("SEQ_NO", 0);
       bilParm.setData("CHARGE_FLG",p.getValue("CHARGE_FLG")); //状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡)
       bilParm.setData("ORIGINAL_BALANCE", feeParm.getValue("ORIGINAL_BALANCE")); //收费前余额
       bilParm.setData("BUSINESS_AMT", feeParm.getValue("BUSINESS_AMT"));
       bilParm.setData("CURRENT_BALANCE", feeParm.getValue("CURRENT_BALANCE"));
       bilParm.setData("CASHIER_CODE", Operator.getID());
       bilParm.setData("BUSINESS_DATE", TJDODBTool.getInstance().getDBTime());
       //1：交易执行完成
       //2：双方确认完成
       bilParm.setData("BUSINESS_STATUS", "1");
       //1：未对帐
       //2：对账成功
       //3：对账失败
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
    * 充值档添加数据参数
    * @param parm TParm
    * @return TParm
    */
   private TParm getBillParm(TParm parm,TParm feeParm){
       TParm billParm=new TParm();
       billParm.setData("CARD_NO", parm.getValue("CARD_NO")); //卡号
       billParm.setData("CURT_CARDSEQ", 0); //序号
       billParm.setData("ACCNT_TYPE", "4"); //明细帐别(1:购卡,2:换卡,3:补卡,4:充值,5:扣款,6:退费)
       billParm.setData("MR_NO", parm.getValue("MR_NO"));//病案号
       billParm.setData("ID_NO", parm.getValue("ID_NO"));//身份证号
       billParm.setData("NAME", parm.getValue("NAME"));//病患名称
       billParm.setData("AMT", feeParm.getValue("BUSINESS_AMT"));//充值金额
       billParm.setData("CREAT_USER", Operator.getID());//执行人员
       billParm.setData("OPT_USER", Operator.getID());//操作人员
       billParm.setData("OPT_TERM", Operator.getIP());//执行ip
       billParm.setData("GATHER_TYPE",parm.getValue("GATHER_TYPE"));//支付方式
       billParm.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());	//售卡操作时间
       billParm.setData("PROCEDURE_AMT", 0.00);	//PROCEDURE_AMT
       return billParm;
   }
  /**
   * 通信邮编的得到省市
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
      * 通过城市带出邮政编码
      */
     public void selectCode() {
         this.setValue("POST_CODE", this.getValue("CITY"));
   }

   /**
    * 保存病患信息
    */
   public void onSave() {
       if (pat != null)
           PatTool.getInstance().unLockPat(pat.getMrNo());
       //不能输入空值
       if (getValue("BIRTH_DATE") == null) {
           this.messageBox("出生日期不能为空!");
           return;
       }
     //add by huangtt 20140320
		if (getValueString("PAT_NAME").equals("")) {
			if (getValueString("FIRST_NAME").equals("")
					&& !getValueString("LAST_NAME").equals("")) {
				this.messageBox("请输入firstName!");
				this.grabFocus("FIRST_NAME");
				return;
			} else if (!getValueString("FIRST_NAME").equals("")
					&& getValueString("LAST_NAME").equals("")) {
				this.messageBox("请输入lastName!");
				this.grabFocus("LAST_NAME");
				return;
			} else if (!getValueString("FIRST_NAME").equals("")
					&& !getValueString("LAST_NAME").equals("")) {
				this.setValue("PAT_NAME1", getValueString("FIRST_NAME") + " "
						+ getValueString("LAST_NAME"));
				if (this.messageBox("姓名赋值", "是否将firstName和lastName合并赋值给姓名？", 0) != 0) {
					this.messageBox("姓名不能为空!");
					return;
				}
				this.setValue("PAT_NAME", getValueString("FIRST_NAME") + " "
						+ getValueString("LAST_NAME"));

			} else if (getValueString("FIRST_NAME").equals("")
					&& getValueString("LAST_NAME").equals("")) {
				this.messageBox("姓名不能为空!");
				this.grabFocus("PAT_NAME");
				return;
			}
		} else {
			if (getValueString("FIRST_NAME").equals("")
					&& !getValueString("LAST_NAME").equals("")) {
				this.messageBox("请输入firstName!");
				this.grabFocus("FIRST_NAME");
				return;
			} else if (!getValueString("FIRST_NAME").equals("")
					&& getValueString("LAST_NAME").equals("")) {
				this.messageBox("请输入lastName!");
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
       //病患姓名
       pat.setName(TypeTool.getString(getValue("PAT_NAME")));
       //英文名
       pat.setName1(TypeTool.getString(getValue("PAT_NAME1")));
       //姓名拼音
       pat.setPy1(TypeTool.getString(getValue("PY1")));
     //证件类型===add by huangtt 20131106
       pat.setIdType(TypeTool.getString(getValue("ID_TYPE")));
       //身份证号
       pat.setIdNo(TypeTool.getString(getValue("IDNO")));
       //国籍
       pat.setNationCode(TypeTool.getString(getValue("NATION_CODE")));
       //外国人注记
       pat.setForeignerFlg(TypeTool.getBoolean(getValue("FOREIGNER_FLG")));
       //出生日期
       pat.setBirthday(TypeTool.getTimestamp(getValue("BIRTH_DATE")));
       //性别
       pat.setSexCode(TypeTool.getString(getValue("SEX_CODE")));
       //电话TEL_HOME
       pat.setTelHome(TypeTool.getString(getValue("TEL_HOME")));
       //邮编
       pat.setPostCode(TypeTool.getString(getValue("POST_CODE")));
       //地址
       pat.setAddress(TypeTool.getString(getValue("CURRENT_ADDRESS")));
       pat.setResidAddress(TypeTool.getString(getValue("RESID_ADDRESS")));
       //身份1
       pat.setCtz1Code(TypeTool.getString(getValue("CTZ1_CODE")));
       //身份2
       pat.setCtz2Code(TypeTool.getString(getValue("CTZ2_CODE")));
       //身份3
       pat.setCtz3Code(TypeTool.getString(getValue("CTZ3_CODE")));
       //医保卡市民卡
       pat.setNhiNo(TypeTool.getString(""));
       pat.setCurrentAddress(TypeTool.getString(getValue("CURRENT_ADDRESS")));
       pat.setRemarks(TypeTool.getString(getValue("REMARKS")));
       pat.setSpeciesCode(TypeTool.getString(""));
       pat.setFirstName(TypeTool.getString(getValue("FIRST_NAME")));
	   pat.setLastName(TypeTool.getString(getValue("LAST_NAME")));
       
       if (this.messageBox("病患信息", "是否保存", 0) != 0)
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
       patParm.setData("NHI_NO", new TNull(String.class));//医保卡号
       if (StringUtil.isNullString(getValueString("CURRENT_ADDRESS"))) {
			patParm.setData("CURRENT_ADDRESS", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValueString("REMARKS"))) {
			patParm.setData("REMARKS", new TNull(String.class));
		}
       TParm result = new TParm();
       if (getValue("MR_NO").toString().length() != 0) {
           //更新病患
           result = PatTool.getInstance().upDateForReg(patParm);
           setValue("MR_NO", getValue("MR_NO"));
           pat.setMrNo(getValue("MR_NO").toString());

       } else {
           //新增病患
           //pat.setTLoad(StringTool.getBoolean("" + getValue("tLoad")));
           if(!pat.onNew()){
               result.setErr(-1,"新建病患信息失败");
           }else{
               setValue("MR_NO", pat.getMrNo());
               setValue("CARD_CODE", pat.getMrNo() + "001");
               setValue("NEW_SEQ", "001");
               ektFlg = true; //新建医疗卡数据
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
 * 清空基本信息
 */
   public void onBaseClear(boolean flg) {
	   //=======yanj
	   clearValue("MR_NO;PAT_NAME;PAT_NAME1;PY1;IDNO;FOREIGNER_FLG;ID_TYPE;" +
               "BIRTH_DATE;SEX_CODE;CELL_PHONE;POST_CODE;STATE;CITY;ADDRESS;" +
               "CTZ2_CODE;CTZ3_CODE;CARD_CODE;EKT_CARD_NO;CARD_PWD;" +
               "CURRENT_BALANCE;OLD_SEQ;NEW_SEQ;CARD_PWD;RE_CARD_PWD;NATION_CODE;" +
               "EKTMR_NO;CURRENT_BALANCE1;EKTCARD_CODE;CURRENT_ADDRESS;REMARKS;" +
               "FIRST_NAME;LAST_NAME;CELL_PHONE;RESID_ADDRESS");


       //callFunction("UI|FOREIGNER_FLG|setEnabled", true); //其他证件可编辑======pangben modify 20110808
       callFunction("UI|MR_NO|setEnabled", true); //病案号可编辑
       callFunction("UI|CARD_CODE|setEnabled", true); //医疗卡号可以编辑
       setValue("CTZ1_CODE", "99");
       //设置默认服务等级
       ektFlg=false;
//       parmEKT=null;//医疗卡信息
       EKTTemp=null;
       parmSum=null;
       //===zhangp 20120724 start
       ektCurrentBalance = 0;
       //===zhangp 20120724 end
       //解锁病患
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
    *清空
    */
   public void onClear() {
	   //=======yanj
       clearValue("MR_NO;PAT_NAME;PAT_NAME1;PY1;IDNO;FOREIGNER_FLG;ID_TYPE;" +
                  "BIRTH_DATE;SEX_CODE;POST_CODE;STATE;CITY;ADDRESS;" +
                  "CTZ2_CODE;CTZ3_CODE;CARD_CODE;EKT_CARD_NO;CARD_PWD;BIL_CODE;" +
                  "CURRENT_BALANCE;OLD_SEQ;NEW_SEQ;CARD_PWD;RE_CARD_PWD;NATION_CODE;DESCRIPTION;" +
                  "EKTMR_NO;CURRENT_BALANCE1;EKTCARD_CODE;GATHER_PRICE;TOP_UP_PRICE;CURRENT_ADDRESS;REMARKS;" +
                  "FIRST_NAME;LAST_NAME;CELL_PHONE;RESID_ADDRESS;CARD_TYPE");

       //callFunction("UI|FOREIGNER_FLG|setEnabled", true); //其他证件可编辑======pangben modify 20110808
       callFunction("UI|MR_NO|setEnabled", true); //病案号可编辑
       callFunction("UI|CARD_CODE|setEnabled", true); //医疗卡号可以编辑
       setValue("CTZ1_CODE", "99");
       setValue("NATION_CODE", "86");
       String sum = EKTTool.getInstance().getPayTypeDefault();
       setValue("GATHER_TYPE1", sum);
       //设置默认服务等级
       ektFlg=false;
       //======yanj
//       parmEKT=null;//医疗卡信息
       EKTTemp=null;
       parmSum=null;
       //===zhangp 20120724 start
       ektCurrentBalance = 0;
       //===zhangp 20120724 end
       //解锁病患
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
    * 无身份注记事件
    */
   public void onSelForeieignerFlg() {
       if (this.getValue("FOREIGNER_FLG").equals("Y"))
           this.grabFocus("BIRTH_DATE");
       if (this.getValue("FOREIGNER_FLG").equals("N"))
           this.grabFocus("IDNO");
   }

   /**
    * 读卡操作
    * @return boolean
    */
   private boolean txReadEKT(){
       //读取医疗卡操作
       if (EKTTemp == null)
		try {
			EKTTemp = EKTIO.getInstance().readEkt();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.messageBox("读卡失败！");
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
    * 收费方法
    */
   public void onFEE(){
//	   ektFlg = false;
	   topUpFee();//add by sunqy 20140728自动计算应收费用 避免不回车就点击收费的情况
	   //读卡操作
//       if (!txReadEKT()) {
//           return;
//       }
//		String parm_MR_NO = EKTTemp.getValue("MR_NO");// 卡片病案号
//		TParm parmss = new TParm();
//		parmss.setData("MR_NO", pat.getMrNo());
//		parmEKT= EKTTool.getInstance().selectEKTIssuelog(parmss);//数据库病案号
	   
	   if (this.getValue("EKTMR_NO").equals("")) {
			this.messageBox("请输入换卡信息！");
			return;
		}
		if (this.getValueDouble("TOP_UP_PRICE") <= 0) {
			this.messageBox("请输入充值金额！");
			return;
		}
		String sendCause = getValue("SEND_CAUSE").toString();
		if(sendCause.equals("8")){//换卡操作点击充值按钮 金额不能小于手续费
			if (this.getValueDouble("CURRENT_BALANCE1")+this.getValueDouble("TOP_UP_PRICE") < this.getValueDouble("PROCEDURE_PRICE1")) {
				this.messageBox("充值的金额不能小于"+this.getValueDouble("PROCEDURE_PRICE1")+"元");
				return;
			}
		}
		if (((TTextFormat) this.getComponent("GATHER_TYPE1")).getText()
				.length() <= 0) {
			this.messageBox("支付方式不可以为空值");
			return;
		}
		//如果支付方式为刷卡时，卡类型和卡号为必填项add by huangjw 20150115
		if("C1".equals(this.getValue("GATHER_TYPE1"))){
			if("".equals(this.getValue("CARD_TYPE"))||this.getValue("CARD_TYPE")==null){
				this.messageBox("卡类型不可为空");
				return;
			}
			if("".equals(this.getValue("DESCRIPTION"))){
				this.messageBox("备注不可为空");
				return;
			}
		}
		//如果支付方式为微信或支付宝时，交易号必填add by huangjw 20150115
		if("WX".equals(this.getValue("GATHER_TYPE1"))||"ZFB".equals(this.getValue("GATHER_TYPE1"))){
			if("".equals(this.getValue("BIL_CODE"))){
				this.messageBox("微信或支付需在票据号码栏填写交易号！");
				return;
			}
		}
//		if (!(parmEKT.getValue("MR_NO", 0).equals(parm_MR_NO))) {//卡片与界面上的病案号不符
//			if (!this.onRenewT(false))
//			return;
//		// yanj20130316重新写卡之后读卡
//		if (!txReadEKT()) {
//			return;
//		}
//		}else{//病患信息相同，只操作充值，不去操作换卡收手续费
//			this.setValue("PROCEDURE_PRICE1",0.00);
//		}

		if (!this.onRenewT(false))
			return;
		// yanj20130316重新写卡之后读卡
		if (!txReadEKT()) {
			return;
		}
		TParm parmll = new TParm();
		parmll.setData("MR_NO", pat.getMrNo());
		parmEKT = EKTTool.getInstance().selectEKTIssuelog(parmll);
		this.setValue("CURRENT_BALANCE1", parmEKT.getDouble("CURRENT_BALANCE",0));
		
		if (parmEKT.getCount("MR_NO") > 1) {
			this.messageBox("此病患信息错误");
			return;
		}

		 //add by huangtt 20140228
        if (this.messageBox("充值", "是否充值"+(this.getValueDouble("TOP_UP_PRICE")-this.getValueDouble("PROCEDURE_PRICE1"))+"元", 0) != 0) {
            return;
        }
		
       if (parmEKT == null || parmEKT.getCount() <= 0||parmEKT.getValue("MR_NO").length() <= 0) {
           this.messageBox("没有获得医疗卡信息");
           return;
       }
       double cardAmt=StringTool.round(this.getValueDouble("CURRENT_BALANCE1") +
	   				this.getValueDouble("TOP_UP_PRICE" )-this.getValueDouble("PROCEDURE_PRICE1"),2);//卡内余额+充值金额-手续费
           TParm result =null;
    	   parmSum = new TParm();
           parmSum.setData("CARD_NO", pat.getMrNo() + parmEKT.getValue("CARD_SEQ",0));
           parmSum.setData("CURRENT_BALANCE", cardAmt);//医疗卡当前余额
           parmSum.setData("CASE_NO", "none");
           parmSum.setData("NAME", pat.getName());
           parmSum.setData("MR_NO", pat.getMrNo());
           parmSum.setData("ID_NO",
                     null != pat.getIdNo() && pat.getIdNo().length() > 0 ?
                     pat.getIdNo() : "none");
           parmSum.setData("ISSUERSN_CODE", this.getText("SEND_CAUSE")); //发卡原因
           parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE")); //支付方式
           parmSum.setData("OPT_USER", Operator.getID());
           parmSum.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
           parmSum.setData("OPT_TERM", Operator.getIP());
           parmSum.setData("FLG", ektFlg);
           parmSum.setData("CHARGE_FLG", "3"); //状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡)
           parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE1")); //支付方式
           parmSum.setData("GATHER_TYPE_NAME", this.getText("GATHER_TYPE1")); //支付方式名称
           parmSum.setData("BUSINESS_AMT",StringTool.round(this.getValueDouble("TOP_UP_PRICE")-this.getValueDouble("PROCEDURE_PRICE1"),2)); //充值金额
           parmSum.setData("SEX_TYPE", this.getValue("SEX_CODE")); //性别
           parmSum.setData("DESCRIPTION", this.getValue("DESCRIPTION")); //备注DESCRIPTION
           parmSum.setData("PRINT_NO", this.getValue("BIL_CODE")); //
           parmSum.setData("BIL_CODE", this.getValue("BIL_CODE")); //票据号
           parmSum.setData("CREAT_USER", Operator.getID()); //执行人员//=====yanjing
            //明细表参数
            TParm feeParm=new TParm();
//            feeParm.setData("ORIGINAL_BALANCE",
//                    StringTool.round(parmEKT.getDouble("CURRENT_BALANCE"),2));
            feeParm.setData("ORIGINAL_BALANCE",
                  this.getValue("CURRENT_BALANCE1"));
            feeParm.setData("BUSINESS_AMT",StringTool.round(this.getValueDouble("TOP_UP_PRICE" ),2) - StringTool.round(this.getValueDouble("PROCEDURE_PRICE1"),2));
//            feeParm.setData("CURRENT_BALANCE","99");
            feeParm.setData("CURRENT_BALANCE",StringTool.round(this.getValueDouble("CURRENT_BALANCE1"),2)+StringTool.round(this.getValueDouble("TOP_UP_PRICE"),2)- StringTool.round(this.getValueDouble("PROCEDURE_PRICE1"),2));
            parmSum.setData("businessParm",getBusinessParm(parmSum,feeParm).getData());
            parmSum.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime());	//售卡操作时间
            parmSum.setData("PROCEDURE_AMT", 0.00);	//PROCEDURE_AMT
            parmSum.setData("CARD_TYPE",this.getValueString("CARD_TYPE"));
            //bil_pay 充值表数据
            parmSum.setData("billParm",getBillParm(parmSum,feeParm).getData());
           //更新余额
            result = TIOM_AppServer.executeAction(
                "action.ekt.EKTAction",
                "TXEKTonFee", parmSum);
           if (result.getErrCode() < 0) {
               this.messageBox("医疗卡充值失败");
               } else{
               this.messageBox("充值成功");
               String bil_business_no = result.getValue("BIL_BUSINESS_NO"); //收据号
               onPrint(bil_business_no);//打印票据
               //=======yanj
               this.setValue("EKTMR_NO","");
               this.setValue("CURRENT_BALANCE1","" );
               this.setValue("TOP_UP_PRICE", "");
               this.setValue("GATHER_PRICE","");
               onClear();
           }
   }
   
   /**
    * 检测病患相同姓名
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
       //选择病患信息
//       if (same.getCount("MR_NO") > 0) {
//           int sameCount = this.messageBox("提示信息", "已有相同姓名病患信息,是否继续保存此人信息", 0);
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
    * 设置英文名
    */
   public void setPatName1() {
       String patName1 = SYSHzpyTool.getInstance().charToAllPy(TypeTool.
               getString(getValue("PAT_NAME")));
       setValue("PAT_NAME1", patName1);
   }
  /**
   * 充值金额回车事件
   */
  public void topUpFee(){
      //将充值金额显示在应收金额中
      double price=this.getValueDouble("TOP_UP_PRICE");
      double fee = this.getValueDouble("PROCEDURE_PRICE1");
      price -= fee; 
      this.setValue("GATHER_PRICE",price);
      //yanj 回车焦点移动
      this.grabFocus("onFee");
      
  }

  /**
   * 医疗卡修改密码
   */
  public void updateEKTPwd() {
      TParm sendParm = new TParm();
      TParm reParm = (TParm)this.openDialog(
              "%ROOT%\\config\\ekt\\EKTUpdatePassWord.x", sendParm);
  }
  /**
   * 充值打印
   */
  private void onPrint(String bil_business_no) {
	  /**
      TParm parm = new TParm();
      parm.setData("TITLE", "TEXT",
                   (Operator.getRegion() != null &&
                    Operator.getRegion().length() > 0 ?
                    Operator.getHospitalCHNFullName() : "所有医院") + "(COPY)");
      parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); //病案号
      parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); //姓名
      parm.setData("GATHER_TYPE", "TEXT", parmSum.getValue("GATHER_TYPE_NAME")); //收款方式
      parm.setData("GATHER_NAME", "TEXT", "收 款"); //收款方式
      parm.setData("TYPE", "TEXT", "预 收"); //文本预收金额
      parm.setData("AMT", "TEXT", StringTool.round(parmSum.getDouble("BUSINESS_AMT"),2)); //金额
      parm.setData("SEX_TYPE", "TEXT", parmSum.getValue("SEX_TYPE").equals("1")?"男":"女"); //性别
      parm.setData("AMT_AW", "TEXT", StringUtil.getInstance().numberToWord(parmSum.getDouble("BUSINESS_AMT"))); //大写金额
      parm.setData("TOP1", "TEXT", "EKTRT001 FROM "+Operator.getID()); //台头一
      String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
              getInstance().getDBTime()), "yyyyMMdd"); //年月日
      String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
              getInstance().getDBTime()), "hhmmss"); //时分秒
      parm.setData("TOP2", "TEXT", "Send On " + yMd + " At " + hms); //台头二
      yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
              getDBTime()), "yyyy/MM/dd"); //年月日
      hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
              getDBTime()), "hh:mm"); //时分秒
//      parm.setData("DESCRIPTION", "TEXT", parmSum.getValue("DESCRIPTION")); //备注
      parm.setData("BILL_NO", "TEXT", parmSum.getValue("BIL_CODE")); //票据号
      if(null == bil_business_no)
           bil_business_no=  EKTTool.getInstance().getBillBusinessNo();//补印操作
      parm.setData("ONFEE_NO", "TEXT", bil_business_no); //收据号
      parm.setData("PRINT_DATE", "TEXT", yMd); //打印时间
      parm.setData("DATE", "TEXT", yMd + "    " + hms); //日期
      parm.setData("USER_NAME", "TEXT", Operator.getID()); //收款人
      parm.setData("O", "TEXT", ""); 
      this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm);
      */
	  	//modify by sunqy 20140611 ----start----
		boolean flg = false;
		parmSum.setData("TITLE", "门诊充值收据");
		parmSum.setData("BIL_BUSINESS_NO",bil_business_no);
		EKTReceiptPrintControl.getInstance().onPrint(null, parmSum, "", -1, pat, flg, this);
		//modify by sunqy 20140611 ----end----
  }
  /**
   * 医疗卡充值退款档插入数据
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
	 * 查询病患 ===========zhangp 20111223
	 */
	public void onQueryPat() {
		TParm sendParm = new TParm();
		sendParm.setData("PAT_NAME", this.getValue("PAT_NAME"));
		//yuml  start   20141022
        sendParm.setData("SEX_CODE", this.getValue("SEX_CODE"));
        sendParm.setData("BIRTH_DATE", this.getValue("BIRTH_DATE"));
        TParm result = PatTool.getInstance().queryPat(sendParm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
     if (result.getCount("MR_NO") > 0) {
     	Object[] possibilities = { "  是   ", "  否   " };
     	int sameCount = JOptionPane.showOptionDialog(null,
     			"已有相同姓名的病患信息,是否继续保存此人信息", "提示信息",
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
	 * 发卡原因监听 ====zhangp 20120202
	 */
	public void onSendCause() {
		switch (this.getValueInt("SEND_CAUSE")) {// 补卡选择 ，充值按钮变化
		case 5:
			this.setText("onFee", "补卡并充值");
			break;
		case 8:
			this.setText("onFee", "换卡并充值");
			break;
		default:
			this.setValue("SEND_CAUSE", 8);
			this.messageBox("不可以选择");
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
   * 密码确认
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
   * 密码确认
   * ===zhangp 20120315
   * @return
   */
  public boolean passWord(){
	   String passWord = getValueString("CARD_PWD");
	   String repassWord = getValueString("RE_CARD_PWD");
	   if(!passWord.equals(repassWord)){
		   messageBox("密码不一致");
		   return false;
	   }
	   return true;
  }
  /**
   * 6位密码
   * ===zhangp 20120319
   * @return
   */
  public boolean passWord4(){
	   String passWord = getValueString("CARD_PWD");
	   if(passWord.length()!=6){
		   messageBox("密码为6位");
		   return false;
	   }
	   grabFocus("RE_CARD_PWD");
	   return true;
  }
  
  /**
   * 初始密码 
   */
  private void setPassWord(){
	   this.setValue("CARD_PWD", "123456");
	   this.setValue("RE_CARD_PWD", "123456");
  }
  
  /**
	 * 读医疗卡
	 */
	public void onReadEKT() {
		// 读取医疗卡
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
