package com.javahis.ui.mem;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.bil.PaymentTool;
import jdo.ekt.EKTIO; 
import jdo.mem.MEMSQL;
import jdo.opb.OPBTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
*
* <p>Title:会员售卡</p>
*
* <p>Description: 会员售卡</p>
*
* <p>Copyright: Copyright (c) /p>
*
* <p>Company: BlueCore</p>
*
* @author huangtt 2013140220
* @version 1.0
*/

public class MEMMarketCardControl extends TControl {
	private static TTable table;
	private Pat  pat;//  病患信息
	private TParm parmMem; //会员卡信息
	TParm parmSum; //打印数据
	PaymentTool paymentTool;
	private boolean crmFlg = true; //crm接口开关
	private TParm mem=new TParm(); //会员参数    huangtt
	private String reason = "";
	private String reasonZW = "";
	private TParm parmEKT; // 医疗卡信息
	
	 /**
     * 初始化
     */
    public void onInit(){
    	super.onInit();
    	table = (TTable) getComponent("TABLE"); 
    	
    	TPanel p = (TPanel) getComponent("tPanel_1");
    	try {
			paymentTool = new PaymentTool(p, this);
		} catch (Exception e) {
			e.printStackTrace();
		}

    	Object obj = this.getParameter();
        if (obj instanceof TParm) {
            TParm acceptData = (TParm) obj;
            String mrNo = acceptData.getData("MR_NO").toString();
            this.setValue("MR_NO", mrNo);
            this.onQueryNO();
        }
        crmFlg = StringTool.getBoolean(TConfig.getSystemValue("crm.switch"));
        
       
        ((TNumberTextField) this.getComponent("MEM_FEE")).addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				chargePayAmt();
			}    		
    	});
    }
    
    /**
	 * 读医疗卡
	 */
	public void onReadEKT() {
		// 读取医疗卡
		parmEKT = EKTIO.getInstance().TXreadEKT();
		if (null == parmEKT || parmEKT.getErrCode() < 0
				|| parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmEKT.getErrText());
			parmEKT = null;
			return;
		}
		this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
		this.onQueryNO();
	}
    
    
    public void onQuery(){
    	onQueryNO();
    }
    
    public void onQueryNO(){
    	pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
        if (pat == null) {
            this.messageBox("无此病案号!");
            this.grabFocus("PAT_NAME");
            return;
        }
        String mrNo=PatTool.getInstance().checkMrno(TypeTool.getString(
                getValue("MR_NO")));
        parmMem = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemTrade(mrNo)));
        mem = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemInfoAll(mrNo)));
//		memTrade =new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemTradeCrm(mrNo))); 
//        System.out.println("parmMem=="+parmMem);
        if(parmMem.getCount()>0){
        	
        	 this.setValue("MEM_FEE", parmMem.getValue("MEM_FEE", 0));
             this.setValue("FEEs", parmMem.getValue("MEM_FEE", 0));
             this.setValue("MEM_CODE", parmMem.getData("MEM_CODE", 0));
             String sql = "SELECT A.MEM_CARD, A.MEM_IN_REASON, B.CHN_DESC" +
             		" FROM MEM_MEMBERSHIP_INFO A, SYS_DICTIONARY B" +
             		" WHERE A.MEM_IN_REASON = B.ID AND B.GROUP_ID = 'MEM_IN_REASON' AND MEM_CODE = '"+ parmMem.getData("MEM_CODE", 0)+"'";
             TParm parmMemCard = new TParm(TJDODBTool.getInstance().select(sql));
             this.setValue("MEM_TYPE", parmMemCard.getData("MEM_CARD", 0));
             reason = parmMemCard.getData("MEM_IN_REASON", 0).toString();
             reasonZW = parmMemCard.getData("CHN_DESC", 0).toString();
             this.setValue("MEM_DESCRIPTION", parmMem.getData("DESCRIPTION", 0));
             this.setValue("INTRODUCER1", parmMem.getData("INTRODUCER1", 0));
             this.setValue("INTRODUCER3", parmMem.getData("INTRODUCER3", 0));
             this.setValue("INTRODUCER2", parmMem.getData("INTRODUCER2", 0));
        	if(parmMem.getValue("START_DATE", 0).equals("") ){
        		Timestamp date = SystemTool.getInstance().getDate();
        		this.setValue("START_DATE", date.toString().substring(0, 10).replace('-', '/'));
        		 getMemCodeEndDate();
        	}else{
 
        		this.setValue("START_DATE", parmMem.getValue("START_DATE", 0).substring(0, 10).replace('-', '/'));
        		if(parmMem.getValue("END_DATE", 0).equals("")){
        			getMemCodeEndDate();
        		}else{
        			this.setValue("END_DATE", parmMem.getValue("END_DATE", 0).substring(0, 10).replace('-', '/'));
        		}	
        	}
           
			parmMem.setData("OPER", 0, "UPDATE");//交易表有数据-修改操作
            
        }
//        else{
//        	callFunction("UI|save|setEnabled", false);
//        }
        TParm memPatinfo = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemInfoAll(mrNo)));
         setValue("MR_NO",
                  PatTool.getInstance().checkMrno(TypeTool.getString(
                          getValue("MR_NO"))));
         setValue("PAT_NAME", pat.getName());
         setValue("CURRENT_ADDRESS", pat.getCurrentAddress());
         setValue("BIRTH_DATE", pat.getBirthday());
         setValue("SEX_CODE", pat.getSexCode());        
         setValue("FIRST_NAME", pat.getFirstName());        
         setValue("LAST_NAME", pat.getLastName());        
         setValue("FAMILY_DOCTOR", memPatinfo.getValue("FAMILY_DOCTOR", 0));        
         setValue("ACCOUNT_MANAGER_CODE", memPatinfo.getValue("ACCOUNT_MANAGER_CODE", 0));   
         if(this.getValueString("MEM_TYPE").equals("")){
        	 setValue("MEM_TYPE", memPatinfo.getValue("MEM_CODE", 0));      
         }
         if("续卡".equals(reasonZW)){
        	 callFunction("UI|START_DATE|setEnabled", false);
         }else{
        	 callFunction("UI|START_DATE|setEnabled", true);
         }
         paymentTool.setAmt(this.getValueDouble("MEM_FEE"));
         onQueryMarketCard();
        
    }
    /**
     * 售卡记录
     */
    public void onQueryMarketCard(){
    	
    	String mrNo = this.getValueString("MR_NO");
//        String cardNo = parmMem.getValue("MEM_CARD_NO", 0);
        
    	String sql="SELECT A.TRADE_NO, A.MEM_CODE,A.MEM_DESC, B.MR_NO," +
    			" C.PAT_NAME,  A.MEM_FEE, A.START_DATE, A.END_DATE,A.GATHER_TYPE," +
    			" A.PAY_TYPE01,A.PAY_TYPE02,A.PAY_TYPE03,A.PAY_TYPE04,A.PAY_TYPE05," +
    			" A.PAY_TYPE06,A.PAY_TYPE07,A.PAY_TYPE08,A.PAY_TYPE09,A.PAY_TYPE10," +
    			" A.MEMO1,A.MEMO2,A.MEMO3,A.MEMO4,A.MEMO5,A.MEMO6,A.MEMO7,A.MEMO8,A.MEMO9,A.MEMO10" +
    			" FROM MEM_TRADE A, MEM_PATINFO B, SYS_PATINFO C" +
    			" WHERE A.MR_NO = B.MR_NO" +
    			" AND A.STATUS = 1" +
    			" AND B.MR_NO = C.MR_NO" +
    			" AND A.MR_NO='"+mrNo+"'" +
    			" ORDER BY A.START_DATE" ;
//    			" AND A.MEM_CARD_NO='"+cardNo+"'";
    	//System.out.println("tttt:"+sql);
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	//备注信息 银行卡号
    	for(int i=0;i<parm.getCount();i++){
    		String description="";
    		for(int j=1;j<11;j++){
    			if(!"".equals(parm.getValue("MEMO"+j,i))&&!"#".equals(parm.getValue("MEMO"+j,i))){
    				String[] str=parm.getValue("MEMO"+j,i).split("#");
    				if(str.length>1){
    					description=str[1];
    				}
    			}
    		}
    		parm.setData("DESCRIPTION",i,description);
    	}
    	//备注信息银行卡号
//    	System.out.println(sql);
    	table.setParmValue(parm);   	
    }
    
    @SuppressWarnings("static-access")
	public void onSave() throws ParseException{
    	TParm payTypeTParm=paymentTool.table.getParmValue();
    	TParm parm = null;
		try {
			parm = paymentTool.getAmts();
		} catch (Exception e) {
			e.printStackTrace();
			messageBox(e.getMessage());
			return;
		}
//    	System.out.println(parm);
		
		changeTradeData();
    	TParm parmMemTrade = new TParm();
    	String START_DATE = this.getValueString("START_DATE").replace("-", "").replace("/", "");
    	String END_DATE = this.getValueString("END_DATE").replace("-", "").replace("/", "");
    	if(START_DATE.length() == 0){
    		this.messageBox("生效日期不能为空");
    		return;
    	}
    	START_DATE=START_DATE.substring(0,8)+"000000";
    	END_DATE = END_DATE.substring(0,8)+"235959";
    	parmMemTrade.setData("TRADE_NO", parmMem.getValue("TRADE_NO", 0));
		parmMemTrade.setData("STATUS", "1");
		parmMemTrade.setData("LAST_DEPRECIATION_END_DATE", this.getValue("START_DATE"));
		parmMemTrade.setData("GATHER_TYPE", "");
		parmMemTrade.setData("MEM_CODE", this.getValueString("MEM_CODE"));
		parmMemTrade.setData("MEM_DESC", this.getText("MEM_CODE"));
		parmMemTrade.setData("START_DATE", StringTool.getTimestamp(START_DATE, "yyyyMMddHHmmss"));
		parmMemTrade.setData("END_DATE", StringTool.getTimestamp(END_DATE, "yyyyMMddHHmmss"));
		parmMemTrade.setData("MEM_FEE", this.getValue("MEM_FEE"));
		parmMemTrade.setData("OPER", parmMem.getValue("OPER", 0));
		parmMemTrade.setData("MR_NO", parmMem.getValue("MR_NO", 0));
		parmMemTrade.setData("MEM_CARD_NO", parmMem.getValue("MEM_CARD_NO", 0));
		parmMemTrade.setData("DESCRIPTION", parmMem.getValue("DESCRIPTION", 0));
		parmMemTrade.setData("OPT_USER", parmMem.getValue("OPT_USER", 0));
		parmMemTrade.setData("OPT_TERM", parmMem.getValue("OPT_TERM", 0));
		parmMemTrade.setData("SALE_USER", Operator.getID());
		parmMemTrade.setData("SALE_DATE", TJDODBTool.getInstance().getDBTime());
		parmMemTrade.setData("INTRODUCER1", this.getValueString("INTRODUCER1"));
		parmMemTrade.setData("INTRODUCER2", this.getValueString("INTRODUCER2"));
		parmMemTrade.setData("INTRODUCER3", this.getValueString("INTRODUCER3"));
//		for (int i = 1; i < 11; i++) {//add by sunqy 20140714将10个字段赋为空字符串
//			parmMemTrade.setData("MEMO"+i, "");
//		}
//		for (int j = 0; j < paymentTool.table.getRowCount(); j++) {//add by sunqy 20140714添加刷卡时的备注(记录银行卡号)
//			if("C1".equals(paymentTool.table.getItemData(j, "PAY_TYPE"))){
//				parmMemTrade.setData("MEMO"+(j+1), paymentTool.table.getItemData(j, "REMARKS"));
//			}
//		}
		for(int j=1;j<12;j++){
			if(j<10){
				parmMemTrade.setData("PAY_TYPE0"+j, "");
			}else{
				parmMemTrade.setData("PAY_TYPE"+j, "");
			}			
		}
		String cardType;//add by sunqy 20140729用于记录各个卡类型//将卡类型和卡号存到一个字段中 modify by huangjw 20150104
		String cardTypeKey;
		String v;
		//----start-------add by kangy 20160718------微信支付宝支付需要添加交易号
		boolean flg2=paymentTool.onCheckPayType(payTypeTParm);
	    if (flg2) {
	    } else {
			this.messageBox("不允许出现相同的支付方式！");
			return;
		}

		for(int j=1;j<12;j++){
			cardTypeKey="MEMO"+j;
			if(j<10){
				v="PAY_TYPE0"+j;
			}else{
				v="PAY_TYPE"+j;
			}
			cardType = "";
			for(int i=0;i<parm.getCount("PAY_TYPE");i++){
				if(v.equals(parm.getValue("PAY_TYPE", i))){
					parmMemTrade.setData(parm.getValue("PAY_TYPE", i), parm.getData("AMT", i));
					if("PAY_TYPE02".equals(parm.getValue("PAY_TYPE", i))){
						cardType = parm.getValue("CARD_TYPE", i)+"#"+parm.getValue("REMARKS",i);
					}else{
						cardType = parm.getValue("REMARKS",i);
					}
					break;
				}
	   			//----end-----add by kangy 20160718------微信支付宝支付需要添加交易号
			}
//			if(cardType.length()>1){
//				cardType = cardType.substring(1, cardType.length());
//			}
			parmMemTrade.setData(cardTypeKey, cardType);
		}
//		System.out.println("--------------parmMemTrade:"+parmMemTrade);
		parmMemTrade.setData("CARD_TYPE", "");//该字段已经不保存数据，但是要给他插入一个空字符串，否则会报错
		
		TParm parmMemInfo = new TParm();
		parmMemInfo.setData("MR_NO", this.getValueString("MR_NO"));
		parmMemInfo.setData("MEM_CODE", this.getValueString("MEM_TYPE"));
//		TTextFormat memType = (TTextFormat) this.getComponent("MEM_TYPE");
		parmMemInfo.setData("MEM_DESC", this.getText("MEM_TYPE"));
		parmMemInfo.setData("START_DATE", StringTool.getTimestamp(START_DATE, "yyyyMMddHHmmss"));
		parmMemInfo.setData("END_DATE", StringTool.getTimestamp(END_DATE, "yyyyMMddHHmmss"));
		
		TParm parmMemFeeD = new TParm();
		String sDate = this.getValueString("START_DATE").replace("-", "/").substring(0, 10);
		String eDate = this.getValueString("END_DATE").replace("-", "/").substring(0, 10);
		int month = this.getMonthSpace(sDate, eDate);
		double memFee = this.getValueDouble("MEM_FEE");
		double dFee =Math.round(memFee/month*100)/100.0 ;
		parmMemFeeD.setData("MR_NO",this.getValueString("MR_NO"));
		parmMemFeeD.setData("MEM_CARD_NO",parmMem.getValue("MEM_CARD_NO", 0));
		parmMemFeeD.setData("DEPRECIATION_FEE",dFee); //折旧费
		parmMemFeeD.setData("DEPRECIATION_START_DATE",this.getValue("START_DATE")); //折旧区间开始时间
		parmMemFeeD.setData("DEPRECIATION_END_DATE",this.getValue("START_DATE")); //折旧区间结束时间
		parmMemFeeD.setData("REMAINING_AMOUNT",memFee-dFee); //该会员剩余会费
		parmMemFeeD.setData("BEFORE_AMOUNT",this.getValueDouble("MEM_FEE")); //折旧前会费
		parmMemFeeD.setData("STATUS",1); //状态：1折旧，2折旧抵冲
		parmMemFeeD.setData("OPT_DATE",TJDODBTool.getInstance().getDBTime());
		parmMemFeeD.setData("OPT_USER",Operator.getID());
		parmMemFeeD.setData("REASON", "");
		parmMemFeeD.setData("OPT_TERM",Operator.getIP());
		if(!reason.equals("")){
			String sql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'MEM_IN_REASON' AND ID='"+reason+"'";
			TParm parmR = new TParm(TJDODBTool.getInstance().select(sql));
			if("续卡".equals(parmR.getValue("CHN_DESC", 0))){
				TParm memPat = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemInfoAll(this.getValueString("MR_NO"))));
				parmMemInfo.setData("START_DATE", StringTool.getTimestamp(memPat.getValue("START_DATE", 0).substring(0, 10).replace("/", "").replace("-", "")+"000000", "yyyyMMddHHmmss"));
				parmMemFeeD.setData("REASON", "续卡");
			}
		}
		TParm parmSysPatInfo = new TParm();
		parmSysPatInfo.setData("MR_NO", this.getValueString("MR_NO"));
		
		String sql="SELECT CTZ_CODE FROM SYS_CTZ WHERE MEM_CODE='"+this.getValueString("MEM_TYPE")+"' AND DEPT_CODE IS NULL";
		TParm ctz = new TParm(TJDODBTool.getInstance().select(sql));
		if(ctz.getCount() > 0){
			parmSysPatInfo.setData("CTZ1_CODE", ctz.getValue("CTZ_CODE", 0));
		}else{
			parmSysPatInfo.setData("CTZ1_CODE", "");
		}
		
		TParm parmAll = new TParm();
		parmAll.setData("parmMemTrade", parmMemTrade.getData());
		parmAll.setData("parmMemInfo", parmMemInfo.getData());
		parmAll.setData("parmMemFeeD", parmMemFeeD.getData());
		parmAll.setData("parmSysPatInfo", parmSysPatInfo.getData());
		//add by sunqy 20140710 判断是否有未填写支付方式的金额----start----
		TParm payParm = paymentTool.table.getParmValue();
    	int payCount = payParm.getCount();
    	String payType = "";
    	double amt = 0.00;
    	for (int i = 0; i <= payCount; i++) {
    		payType = paymentTool.table.getItemString(i, "PAY_TYPE");
			amt = paymentTool.table.getItemDouble(i, "AMT");
			if(amt > 0 && ("".equals(payType)||payType==null)){
				this.messageBox("存在未设定支付方式的金额,请填写！");
				return;
			}
		}
    	//现金打票操作，校验是否存在支付宝或微信金额
		TParm checkCashTypeParm=OPBTool.getInstance().checkCashTypeOther(payTypeTParm);
		TParm payCashParm=null;
		if(null!=checkCashTypeParm.getValue("WX_FLG")&&
				checkCashTypeParm.getValue("WX_FLG").equals("Y")||null!=checkCashTypeParm.getValue("ZFB_FLG")&&
				checkCashTypeParm.getValue("ZFB_FLG").equals("Y")){
			Object result = this.openDialog(
    	            "%ROOT%\\config\\bil\\BILPayTypeOnFeeTransactionNo.x", checkCashTypeParm, false);
			if(null==result){
				return ;
			}
			payCashParm=(TParm)result;
		}
		if(null!=payCashParm){
			parmAll.setData("payCashParm",payCashParm.getData());
		}
    	//add by sunqy 20140710 判断是否有未填写支付方式的金额----end----
//    	System.out.println("银行卡号111111111="+parmAll.getParm("parmMemTrade"));
		TParm result = TIOM_AppServer.executeAction("action.mem.MEMAction","updateMemTradeInfo",parmAll);
		if(result.getErrCode()<0){
    		this.messageBox("保存失败！");
    		return;
    	}
    		this.messageBox("保存成功！");
    		callFunction("UI|save|setEnabled", false);
    		onQueryMarketCard();
    		 String bil_business_no = ""; //收据号
    		 parmSum = new TParm();
    		 parmSum.setData("MR_NO", this.getValueString("MR_NO"));
    		 parmSum.setData("NAME", this.getValueString("PAT_NAME"));
    		 parmSum.setData("GATHER_TYPE_NAME", "");
    		 parmSum.setData("BUSINESS_AMT", this.getValueDouble("MEM_FEE"));
    		 parmSum.setData("SEX_TYPE", this.getValueString("SEX_CODE"));
    		 parmSum.setData("DESCRIPTION", this.getValueString("MEM_DESCRIPTION"));
    		 parmSum.setData("BIL_CODE", "");
             onPrint(bil_business_no);//打印票据
             
//             //add by huangtt 20140401 CRM----start
				if(crmFlg){
					parm = new TParm();					
					parm.setData("MR_NO", pat.getMrNo());
					parm.setData("PAT_NAME", pat.getName());
					parm.setData("PY1", pat.getPy1());
					parm.setData("FIRST_NAME", pat.getFirstName());
					parm.setData("LAST_NAME", pat.getLastName());
					parm.setData("OLDNAME", pat.getOldName());
					parm.setData("ID_TYPE", pat.getIdType());
					parm.setData("IDNO", pat.getIdNo());
					parm.setData("SEX_CODE", pat.getSexCode());
					parm.setData("BIRTH_DATE", pat.getBirthday());
					parm.setData("NATION_CODE", pat.getNationCode());
					parm.setData("NATION_CODE2", pat.getSpeciesCode());
					parm.setData("MARRIAGE", pat.getMarriageCode());
					parm.setData("RESID_POST_CODE", pat.getResidPostCode());
					parm.setData("RESID_ADDRESS", pat.getResidAddress());
					parm.setData("POST_CODE", pat.getPostCode());
					parm.setData("CURRENT_ADDRESS", pat.getCurrentAddress());
					parm.setData("CELL_PHONE", pat.getCellPhone());
					parm.setData("SPECIAL_DIET", pat.getSpecialDiet().getValue());
					parm.setData("E_MAIL", pat.getEmail());
					parm.setData("TEL_HOME", pat.getTelHome());
					parm.setData("CTZ1_CODE", parmSysPatInfo.getValue("CTZ1_CODE"));
					parm.setData("CTZ2_CODE", pat.getCtz2Code());
					parm.setData("CTZ3_CODE", pat.getCtz3Code());
					parm.setData("HOMEPLACE_CODE", pat.gethomePlaceCode());
					parm.setData("RELIGION", pat.getReligionCode());
					parm.setData("BIRTH_HOSPITAL", mem.getValue("BIRTH_HOSPITAL", 0));
					parm.setData("SCHOOL_NAME", mem.getValue("SCHOOL_NAME", 0));
					parm.setData("SCHOOL_TEL", mem.getValue("SCHOOL_TEL", 0));
					parm.setData("SOURCE", mem.getValue("SOURCE", 0));
					parm.setData("INSURANCE_COMPANY1_CODE", mem.getValue("INSURANCE_COMPANY1_CODE", 0));
					parm.setData("INSURANCE_COMPANY2_CODE", mem.getValue("INSURANCE_COMPANY2_CODE", 0));
					parm.setData("INSURANCE_NUMBER1", mem.getValue("INSURANCE_NUMBER1", 0));
					parm.setData("INSURANCE_NUMBER2", mem.getValue("INSURANCE_NUMBER2", 0));
					parm.setData("GUARDIAN1_NAME", mem.getValue("GUARDIAN1_NAME", 0));
					parm.setData("GUARDIAN1_RELATION", mem.getValue("GUARDIAN1_RELATION", 0));
					parm.setData("GUARDIAN1_TEL", mem.getValue("GUARDIAN1_TEL", 0));
					parm.setData("GUARDIAN1_PHONE", mem.getValue("GUARDIAN1_PHONE", 0));
					parm.setData("GUARDIAN1_COM", mem.getValue("GUARDIAN1_COM", 0));
					parm.setData("GUARDIAN1_ID_TYPE", mem.getValue("GUARDIAN1_ID_TYPE", 0));
					parm.setData("GUARDIAN1_ID_CODE", mem.getValue("GUARDIAN1_ID_CODE", 0));
					parm.setData("GUARDIAN1_EMAIL", mem.getValue("GUARDIAN1_EMAIL", 0));
					parm.setData("GUARDIAN2_NAME", mem.getValue("GUARDIAN2_NAME", 0));
					parm.setData("GUARDIAN2_RELATION", mem.getValue("GUARDIAN2_RELATION", 0));
					parm.setData("GUARDIAN2_TEL", mem.getValue("GUARDIAN2_TEL", 0));
					parm.setData("GUARDIAN2_PHONE", mem.getValue("GUARDIAN2_PHONE", 0));
					parm.setData("GUARDIAN2_COM", mem.getValue("GUARDIAN2_COM", 0));
					parm.setData("GUARDIAN2_ID_TYPE", mem.getValue("GUARDIAN2_ID_TYPE", 0));
					parm.setData("GUARDIAN2_ID_CODE", mem.getValue("GUARDIAN2_ID_CODE", 0));
					parm.setData("GUARDIAN2_EMAIL", mem.getValue("GUARDIAN2_EMAIL", 0));
					parm.setData("REG_CTZ1_CODE", mem.getValue("REG_CTZ1_CODE", 0));
					parm.setData("REG_CTZ2_CODE", mem.getValue("REG_CTZ2_CODE", 0));
					parm.setData("FAMILY_DOCTOR", mem.getValue("FAMILY_DOCTOR", 0));
					parm.setData("ACCOUNT_MANAGER_CODE", mem.getValue("ACCOUNT_MANAGER_CODE", 0));
					parm.setData("MEM_TYPE", parmMemInfo.getValue("MEM_CODE"));
					if("续卡".equals(reasonZW)){
						parm.setData("START_DATE", mem.getValue("START_DATE", 0).substring(0, 10));
						sDate = mem.getValue("START_DATE", 0).substring(0, 10);
					}else{
						parm.setData("START_DATE", this.getValueString("START_DATE").substring(0, 10));
						sDate = this.getValueString("START_DATE").substring(0, 10);
					}
					parm.setData("END_DATE",  this.getValueString("END_DATE").substring(0, 10));
					
			    	eDate = this.getValueString("END_DATE").substring(0, 10);
			    	Timestamp date = SystemTool.getInstance().getDate();
			    	if(sDate.length()>0 && eDate.length()>0){
			    		//计算购买月龄
			    		int buyMonthAge = getBuyMonth(sDate.substring(0, 10).replaceAll("-", ""),
			    				eDate.substring(0, 10).replaceAll("-", ""));
			    		
			    		//发生月龄
			    		int currMonthAge = getBuyMonth(sDate.substring(0, 10).replaceAll("-", ""),
			    				date.toString().substring(0, 10).replaceAll("-", ""));
			    		
			    		parm.setData("BUY_MONTH_AGE", String.valueOf(buyMonthAge));
			    		parm.setData("HAPPEN_MONTH_AGE", String.valueOf(currMonthAge));
			    	}else{
			    		parm.setData("BUY_MONTH_AGE", "");
			    		parm.setData("HAPPEN_MONTH_AGE", "");
			    	}
			    	parm.setData("MEM_CODE", parmMem.getValue("MEM_CODE", 0));
			    	parm.setData("REASON", parmMem.getValue("REASON", 0));
			    	parm.setData("START_DATE_TRADE", sDate);
			    	parm.setData("END_DATE_TRADE", eDate);
			    	parm.setData("MEM_FEE", parmMem.getValue("MEM_FEE", 0));
			    	parm.setData("INTRODUCER1", parmMem.getValue("INTRODUCER1", 0));
			    	parm.setData("INTRODUCER2", parmMem.getValue("INTRODUCER2", 0));
			    	parm.setData("INTRODUCER3", parmMem.getValue("INTRODUCER3", 0));
			    	parm.setData("DESCRIPTION", parmMem.getValue("DESCRIPTION", 0));	
					
					System.out.println("CRM信息更新同步==="+parm);
					TParm parmCRM = TIOM_AppServer.executeAction("action.reg.REGCRMAction","updateMemberByMrNo1",parm);
					if(!parmCRM.getBoolean("flg", 0)){
						this.messageBox("CRM信息更新同步失败！");
					}
				}
 				//add by huangtt 20140401 CRM----end
             
    }
    
    public void onClear(){
		table.removeRowAll();
		clearValue(" MR_NO;PAT_NAME;BIRTH_DATE;SEX_CODE;MEM_DESCRIPTION; "
				+ " CURRENT_ADDRESS;MEM_TYPE;FEEs;MEM_FEE;END_DATE;START_DATE;MEM_GATHER_TYPE"
				+ " ;FAMILY_DOCTOR;ACCOUNT_MANAGER_CODE;MEM_CODE;REASON;INTRODUCER1;INTRODUCER2;INTRODUCER3;FIRST_NAME;LAST_NAME");
		parmEKT = null;
		callFunction("UI|save|setEnabled", true);
		callFunction("UI|START_DATE|setEnabled", true);
		paymentTool.onClear();
    	 
    }
    
    /**
     * 售卡打印    
     */
    private void onPrint(String bil_business_no) {
    	/**
        TParm parm = new TParm();
        parm.setData("TITLE", "TEXT",
                     (Operator.getRegion() != null &&
                      Operator.getRegion().length() > 0 ?
                      Operator.getHospitalCHNFullName() : "所有医院") );
        parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); //病案号
        parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); //姓名
      //====zhangp 20120525 start
//      parm.setData("GATHER_NAME", "TEXT", "收 款"); //收款方式
      parm.setData("GATHER_NAME", "TEXT", ""); //收款方式
      //====zhangp 20120525 end
        parm.setData("TYPE", "TEXT", "预 收"); //文本预收金额
        parm.setData("GATHER_TYPE", "TEXT", parmSum.getValue("GATHER_TYPE_NAME")); //收款方式
        parm.setData("AMT", "TEXT",
                     StringTool.round(parmSum.getDouble("BUSINESS_AMT"), 2)); //金额
        parm.setData("SEX_TYPE", "TEXT",
                     parmSum.getValue("SEX_TYPE").equals("1") ? "男" : "女"); //性别
        parm.setData("AMT_AW", "TEXT",
                     StringUtil.getInstance().numberToWord(
                     parmSum.getDouble("BUSINESS_AMT"))); //大写金额
        parm.setData("TOP1", "TEXT", "EKTRT001 FROM " + Operator.getID()); //台头一
        String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
                getInstance().getDBTime()), "yyyyMMdd"); //年月日
        String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
                getInstance().getDBTime()), "hhmmss"); //时分秒
        parm.setData("TOP2", "TEXT", "Send On " + yMd + " At " + hms); //台头二
        yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
                getDBTime()), "yyyy/MM/dd"); //年月日
        hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
 				.getInstance().getDBTime()), "HH:mm"); //时分秒
        parm.setData("DESCRIPTION", "TEXT", parmSum.getValue("DESCRIPTION")); //备注
        parm.setData("BILL_NO", "TEXT", parmSum.getValue("BIL_CODE")); //票据号
        if (null == bil_business_no)
            bil_business_no = EKTTool.getInstance().getBillBusinessNo(); //补印操作
        parm.setData("ONFEE_NO", "TEXT", bil_business_no); //收据号
        parm.setData("PRINT_DATE", "TEXT", yMd); //打印时间
        parm.setData("DATE", "TEXT", yMd + "    " + hms); //日期
        parm.setData("USER_NAME", "TEXT", Operator.getID()); //收款人
        //=========modify by lim 2012/02/24 begin
        //===zhangp 20120525 start
        parm.setData("O", "TEXT", ""); 
//        this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_ONFEE.jhw", parm,true);
        this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm ,true);
        //===zhangp 20120525 end
      //=========modify by lim 2012/02/24 begin
    	 */
    	//modify by sunqy 20140611 ----start----
    	String copy = "";
    	int row = table.getSelectedRow();// 得到选中行
//    	if(row<0){
//			this.messageBox("没有选中数据!");
//			return;
//		}
    	boolean flg = false;
    	parmSum.setData("TITLE", "会员费交款收据");
    	//add by lich 20150227 -------start 从界面上得到售卡类型，将参数出入收据打印Parm
    	TTextFormat tf = (TTextFormat) this.getComponent("MEM_CODE");
    	parmSum.setData("TYPE",tf.getText());
    	//add by lic 20150227 ------- end
//    	parmSum.setData("PAY_TYPE", this.getText("GATHER_TYPE"));
    	flg=onCheck();
		if(!flg){
			return;
		}
		MEMFeeReceiptPrintControl.getInstance().onPrint(table, parmSum, copy, row, pat, flg, this, paymentTool,"1");//1是购卡票据，2是退卡票据，因为购卡票据和退卡票据的支付方式、卡类型、卡号都不一样
		//modify by sunqy 20140611 ----end----
    }
    
    public void onRePrint(){
    	/**
    	table.acceptText();
  		int row = table.getSelectedRow();
 	   if(row==-1){
 		   messageBox("请选择要补印的记录");
 		   return;
 	   }else{
 		  TParm parm = new TParm();
 	        parm.setData("TITLE", "TEXT",
 	                     (Operator.getRegion() != null &&
 	                      Operator.getRegion().length() > 0 ?
 	                      Operator.getHospitalCHNFullName() : "所有医院") );
 	        parm.setData("MR_NO", "TEXT", table.getItemData(row, "MR_NO")); //病案号
 	        parm.setData("PAT_NAME", "TEXT", table.getItemData(row, "PAT_NAME")); //姓名
 	      //====zhangp 20120525 start
// 	      parm.setData("GATHER_NAME", "TEXT", "收 款"); //收款方式
 	      parm.setData("GATHER_NAME", "TEXT", ""); //收款方式
 	      //====zhangp 20120525 end
 	        parm.setData("TYPE", "TEXT", "预 收"); //文本预收金额
 	        parm.setData("GATHER_TYPE", "TEXT", ""); //收款方式
 	        parm.setData("AMT", "TEXT",
 	                     StringTool.round(table.getItemDouble(row, "MEM_FEE"), 2)); //金额
 	        parm.setData("SEX_TYPE", "TEXT",
 	                     getValue("SEX_CODE").equals("1") ? "男" : "女"); //性别
 	        parm.setData("AMT_AW", "TEXT",
 	                     StringUtil.getInstance().numberToWord(
 	                    		table.getItemDouble(row, "MEM_FEE"))); //大写金额
 	        parm.setData("TOP1", "TEXT", "EKTRT001 FROM " + Operator.getID()); //台头一
 	        String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
 	                getInstance().getDBTime()), "yyyyMMdd"); //年月日
 	        String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
 	                getInstance().getDBTime()), "hhmmss"); //时分秒
 	        parm.setData("TOP2", "TEXT", "Send On " + yMd + " At " + hms); //台头二
 	        yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
 	                getDBTime()), "yyyy/MM/dd"); //年月日
 	        hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
 	 				.getInstance().getDBTime()), "HH:mm"); //时分秒
 	        parm.setData("DESCRIPTION", "TEXT", table.getItemData(row, "DESCRIPTION")); //备注
 	        parm.setData("BILL_NO", "TEXT", ""); //票据号
 	        parm.setData("ONFEE_NO", "TEXT", ""); //收据号
 	        parm.setData("PRINT_DATE", "TEXT", yMd); //打印时间
 	        parm.setData("DATE", "TEXT", yMd + "    " + hms); //日期
 	        parm.setData("USER_NAME", "TEXT", Operator.getID()); //收款人
 	       parm.setData("COPY", "TEXT", "(copy)"); //补印注记
 	        //=========modify by lim 2012/02/24 begin
 	        //===zhangp 20120525 start
 	        parm.setData("O", "TEXT", ""); 
// 	        this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_ONFEE.jhw", parm,true);
 	        this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm ,true); 
 	   }
 	   */
    	
    	//add by sunqy 20140611 ----start----
    	DecimalFormat df=new DecimalFormat("#0.00");
		int row = table.getSelectedRow();// 得到选中行
		if(row<0){
			this.messageBox("没有选中数据!");
			return;
		}
		TParm selParm = table.getParmValue().getRow(row);
		
		String copy = "(copy)";

		boolean flg = false;
		flg=onCheck();
		if(!flg){
			return;
		}
		
		String gatherTypeSql ="SELECT A.PAYTYPE, A.GATHER_TYPE,B.CHN_DESC FROM BIL_GATHERTYPE_PAYTYPE A ,SYS_DICTIONARY B" +
		" WHERE A.GATHER_TYPE= B.ID AND B.GROUP_ID = 'GATHER_TYPE'"; 
		TParm gatherParm = new TParm(TJDODBTool.getInstance().select(gatherTypeSql));
		Map<String, String> gatherMap = new HashMap<String, String>();
		List<String> payList = new ArrayList<String>();
		for (int i = 0; i < gatherParm.getCount(); i++) {
			gatherMap.put(gatherParm.getValue("PAYTYPE", i), gatherParm.getValue("CHN_DESC", i));
			if(selParm.getDouble(gatherParm.getValue("PAYTYPE", i)) > 0){
				payList.add(gatherParm.getValue("CHN_DESC", i)+":"+df.format(selParm.getDouble(gatherParm.getValue("PAYTYPE", i)))+"元");
			}
		}		
		
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT", "会员费交款收据");
		parm.setData("TYPE", "TEXT", selParm.getValue("MEM_DESC")); //类别
		parm.setData("MR_NO", "TEXT", selParm.getValue("MR_NO")); // 病案号
		parm.setData("RecNO", "TEXT", SystemTool.getInstance().getNo("ALL", "EKT", "MEM_NO", "MEM_NO")); //票据号
		parm.setData("DEPT_CODE", "TEXT", "");// 科别
		parm.setData("PAT_NAME", "TEXT", selParm.getValue("PAT_NAME")); // 姓名
		if(payList.size() > 2){
			
			parm.setData("PAY_TYPE2", "TEXT", payList.get(0));
			parm.setData("PAY_TYPE3", "TEXT", payList.get(1)+";"+payList.get(2));
			
		}else{
			String payType = "";
			for (int i = 0; i < payList.size(); i++) {
				payType = payType + payList.get(i) +";";
				
			}
			payType =payType.substring(0, payType.length()-1);
			parm.setData("PAY_TYPE", "TEXT", payType);// 支付方式
		}
		
		
		
		parm.setData("MONEY", "TEXT", (df.format(StringTool.round(selParm.getDouble("MEM_FEE"), 2))+"元")); // 金额
		parm.setData("CAPITAL", "TEXT", StringUtil.getInstance().numberToWord(selParm.getDouble("MEM_FEE"))); // 大写金额
		parm.setData("CTZ_CODE", "TEXT", selParm.getValue("MEM_DESC"));// 产品
		parm.setData("REASON", "TEXT", "");// 折扣原因
		String date = StringTool.getTimestamp(new Date()).toString().substring(
				0, 19).replace('-', '/');
		parm.setData("DATE", "TEXT", date);// 日期
		parm.setData("OP_NAME", "TEXT", Operator.getID()); // 收款人
		parm.setData("COPY", "TEXT", copy); // 补印注记
		parm.setData("HOSP_NAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalCHNFullName() : "所有医院");
        parm.setData("HOSP_ENAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalENGFullName() : "ALL HOSPITALS");
		this.openPrintDialog(IReportTool.getInstance().getReportPath("MEMFeeReceiptV45.jhw"),
				IReportTool.getInstance().getReportParm("MEMFeeReceiptV45.class", parm));//合并报表
		
		
		//add by sunqy 20140611 ----end----
    }
    /**
     * 检验病案号是否为当前病患 add by sunqy 20140623
     */
    private boolean onCheck(){
    	boolean flg=false;
    	String sql = "SELECT PAT_NAME FROM SYS_PATINFO WHERE MR_NO = '"+this.getValue("MR_NO")+"'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	result = result.getRow(0);
    	String patName = this.getValueString("PAT_NAME");
    	if(!patName.equals(result.getValue("PAT_NAME"))){
    		this.messageBox("病案号与病患信息不符");
    		this.grabFocus("MR_NO");
    		//flg = true;
    		return flg;
    	}
    	return true;
    }
    
    /**
	 * 根据卡片类型，得到会费
	 */
	public void onQueryMemFee(){
		String memCode = this.getValueString("MEM_TYPE");
		TParm parm = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemFee(memCode)));
		this.setValue("MEM_FEE", parm.getValue("MEM_FEE", 0));
		paymentTool.setAmt(parm.getDouble("MEM_FEE", 0));
		this.setValue("FEEs", parm.getValue("MEM_FEE", 0));
		
	}
	
	public void getMemCodeEndDate(){

		String validDays = "";
    	//Timestamp now = StringTool.getTimestamp(new Date());
    	String MemCode = this.getValueString("MEM_CODE");
    	String sDate = this.getValueString("START_DATE");
    	if(sDate.length()>0){
    		sDate = sDate.substring(0, 10);
    		//查询有效天数
    		String sql = "SELECT VALID_DAYS FROM MEM_MEMBERSHIP_INFO WHERE  MEM_CODE = '"+MemCode+"'";
    		//System.out.println("sql="+sql);
    		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    		if(parm.getCount()>0){
    			validDays = parm.getValue("VALID_DAYS", 0);
    			//getNextDay
    			String eTradeDate = getNextDay2(sDate,validDays);
    			this.setValue("END_DATE", eTradeDate.replaceAll("-", "/"));
    		}
    	}
		
		
	}
	
	/**
	 * 得到一个时间延后n年的时间点
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
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd
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
	
	public void compareDate() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date  eDate = format.parse(this.getValueString("END_DATE"));
		Date  sDate = format.parse(this.getValueString("START_DATE"));
		if(sDate.after(eDate)){	
			callFunction("UI|save|setEnabled", false);
			this.messageBox("结束日期不能小于开始日期！");
		}else{
			callFunction("UI|save|setEnabled", true);
		}

	}
	
	/**
	 * 计算两个时间内相差的月数
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
	 * 校验交易表是否数据
	 */
	public void changeTradeData() {
		if(parmMem.getCount()<=0){
			parmMem = new TParm();
			//新生成交易号
			String tradeNo = getMEMTradeNo();
			//新生成卡号
			String cardNo = getMemCardNo(this.getValue("MR_NO").toString());
			parmMem.setData("TRADE_NO", 0, tradeNo);
			parmMem.setData("MEM_CARD_NO", 0, cardNo);
			parmMem.setData("MR_NO", 0, this.getValue("MR_NO"));
			parmMem.setData("DESCRIPTION", 0, this.getValue("MEM_DESCRIPTION"));
			parmMem.setData("OPT_USER", 0, Operator.getID());
			parmMem.setData("OPT_TERM", 0, Operator.getIP());
			
			parmMem.setData("OPER", 0, "NEW");//交易表无数据-新增操作
			//System.out.println("parmMem=="+parmMem);
			
		}
	}
	
	/**
     * 取号原则:mem_trade
     */
    public String getMEMTradeNo() {
 		return SystemTool.getInstance().getNo("ALL", "EKT", "MEM_TRADE_NO",
		"MEM_TRADE_NO");
     }
    
    /**
     * 生成交易号表的会员卡编号
     */
    public String getMemCardNo(String mrNo) {
    	String result = "";
    	String sql = "SELECT MEM_CARD_NO FROM MEM_TRADE WHERE MR_NO = '"+mrNo+"' AND STATUS = '1' ";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	String memCardNo = parm.getValue("MEM_CARD_NO",0).toString().equals("")?"0"
    			:parm.getValue("MEM_CARD_NO",0).toString();
    	if(memCardNo.length()>0 && !"0".equals(memCardNo)){
    		//取后三位转成int+1，再拼上病案号
    		if(memCardNo.length()>3){
    			String no = memCardNo.substring(memCardNo.length()-3);
    			//System.out.println("后三位："+no);
    			int intNo = Integer.parseInt(no)+1;
    			result = StringTool.fillLeft(String.valueOf(intNo) ,3 ,"0" );
    			result = mrNo + result;
    		}
    	}else{
    		//病案号+001
    		result = mrNo + "001";
    	}
    	//System.out.println("生成会员卡编号"+result);
    	return result;
    }

	/**
     * 数据校验
     */
    public boolean checkData() {
    	boolean flg = true;
    	//会员折旧表是否有记录
    	String sql = "SELECT * FROM MEM_FEE_DEPRECIATION WHERE MR_NO = '"+this.getValue("MR_NO").toString()+"'";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()>0){
    		this.messageBox("该会员已办卡，不可办多张会员卡！");
    		flg = false;
    	}
    	
    	return flg;
    }
    
    /**
	 * 获取购买月龄
	 */
	public int getBuyMonth(String s, String s1){
//		Date m=new Date();
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
		    	result=month1-month;//两个日期相差几个月，即月份差
		    }else{
		    	result=12*(year1-year)+month1-month;//两个日期相差几个月，即月份差
		    }
		return result;
	}
	
	/**
	 * 查询新注册患者
	 */
	public void onNewPat(){
		TParm parm = (TParm) this.openDialog(
				"%ROOT%\\config\\mem\\MEMNewPatQuery.x", false);
		this.setValue("MR_NO", parm.getValue("MR_NO"));
		this.onQueryNO();
	}
	
	public void onQueryFee(){
		String memCode = this.getValueString("MEM_CODE");
		String sql = "SELECT MEM_FEE,MEM_IN_REASON,MEM_CARD FROM MEM_MEMBERSHIP_INFO WHERE MEM_CODE = '"+memCode+"' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
			this.setValue("MEM_FEE", parm.getValue("MEM_FEE", 0));
			this.setValue("REASON", parm.getValue("MEM_IN_REASON", 0));
			this.setValue("MEM_TYPE", parm.getValue("MEM_CARD", 0));

			try {
				this.onContinuationCard();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		paymentTool.setAmt(this.getValueDouble("MEM_FEE"));
		//生效日期
		this.setValue("START_DATE", new Date());
		this.getMemCodeEndDate();
		

	}
	
	public void chargePayAmt(){
		paymentTool.setAmt(this.getValueDouble("MEM_FEE"));
	}
	
	/**
	 * 查询预失效日期-【预生效日期变化时】
	 */
	public void queryValidDays(){
		String validDays = "";
		//Timestamp now = StringTool.getTimestamp(new Date());
		String MemCode = this.getValueString("MEM_CODE");
		String sDate = this.getValueString("START_DATE_TRADE");
		if(sDate.length()>0){
			sDate = sDate.substring(0, 10);
			//查询有效天数
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
	 * 续卡判断
	 * @throws ParseException 
	 */
	public void onContinuationCard() throws ParseException{
		String reason = this.getText("REASON");
		//    	TTextFormat t = this.getTextFormat("REASON");
		if("续卡".equals(reason)){
			String memType = this.getValueString("MEM_TYPE");
			if(memType.equals("")){
				this.messageBox("该病患没有会员卡，不允许续卡！");
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
		if("升级".equals(reason)){
			String sql = "SELECT * FROM MEM_TRADE WHERE STATUS = '1' AND REMOVE_FLG = 'N' AND MR_NO='"+this.getValueString("MR_NO")+"'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if(parm.getCount()>0){
				this.messageBox("该卡没有停卡，不允许升级");
				this.clearValue("REASON;MEM_CODE;MEM_FEE");
				return;
			}else{
				Timestamp date = SystemTool.getInstance().getDate();
				this.setValue("START_DATE_TRADE", date);
				queryValidDays();
			}

		}
		if("新办".equals(reason)){
			TParm parm = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemTrade1(this.getValueString("MR_NO"))));
			if(parm.getCount()>0){
				this.messageBox("该病患有办卡记录，不允许新办");
				this.clearValue("REASON;MEM_CODE;MEM_FEE");
				return;
			}
		}
	}
	
	public void setPayAmt(){
		paymentTool.setAmt(this.getValueDouble("MEM_FEE"));
	}

}
