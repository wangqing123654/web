package action.mem;

import jdo.mem.MEMTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

public class MEMAction extends TAction{
	
	/**
	 * 修改礼品卡现金券交易表
	 * @param parm
	 * @return
	 */
	public TParm updateGiftSalesCardMD(TParm parm){
		TParm result = new TParm();
		TConnection conn = this.getConnection();
		TParm parmM = parm.getParm("parmM");
		TParm parmD = parm.getParm("parmD");
		TParm parmMH = parm.getParm("parmMH");
		TParm parmDH = parm.getParm("parmDH");
		TParm payCashParm=parm.getParm("payCashParm");//微信支付宝添加交易号码
//		TParm parmMH = MEMTool.getInstance().selectGiftSalesCardM(parmM, conn);
//		if (parmMH.getErrCode() < 0) {
//			conn.rollback();
//			conn.close();
//			return result;
//		}
		result = MEMTool.getInstance().insertGiftSalesCardMH(parmMH, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
//		TParm parmDH = MEMTool.getInstance().selectGiftSalesCardD(parmM, conn);
//		if (parmDH.getErrCode() < 0) {
//			conn.rollback();
//			conn.close();
//			return result;
//		}
		int countDH = parmDH.getCount("ID");
		
		for(int i=0;i<countDH;i++){
			result = MEMTool.getInstance().insertGiftSalesCardDH(parmDH.getRow(i), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		
		result = MEMTool.getInstance().updateGiftSalesCardM(parmM, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		//微信支付宝保存交易号码
    	if(null!=payCashParm){
    		TParm tempParm=new TParm();
    		tempParm.setData("TRADE_NO",parmM.getValue("TRADE_NO"));
    		if(null!=payCashParm.getValue("WX")&&payCashParm.getValue("WX").length()>0){
    			tempParm.setData("WX_BUSINESS_NO",payCashParm.getValue("WX"));
			}else{
				tempParm.setData("WX_BUSINESS_NO","");
			}
			if(null!=payCashParm.getValue("ZFB")&&payCashParm.getValue("ZFB").length()>0){
				tempParm.setData("ZFB_BUSINESS_NO",payCashParm.getValue("ZFB"));
			}else{
				tempParm.setData("ZFB_BUSINESS_NO","");
			}
    		result = MEMTool.getInstance().updateCashTypeMemGiftcardTradeMBusinessNo(tempParm, conn);
        	if (result.getErrCode() < 0) {
        		conn.rollback();
        		conn.close();
    			return result;
    		}
    	}
		int countD = parmD.getCount("ID");
		for(int i=0;i<countD;i++){
			result=MEMTool.getInstance().updateGiftSalesCardD(parmD.getRow(i), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		conn.commit();
		conn.close();
		return result;
		
	}
	
	/**
	 * 插入礼品卡现金券交易表
	 * @param parm
	 * @return
	 */
	public TParm insertGiftSalesCardMD(TParm parm){
		TParm result = new TParm();
		TConnection conn = this.getConnection();
		TParm parmM = parm.getParm("parmM");
		TParm parmD = parm.getParm("parmD");
		TParm payCashParm=parm.getParm("payCashParm");//微信支付宝添加交易号码
		result = MEMTool.getInstance().insertGiftSalesCardM(parmM, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		int countD = parmD.getCount("ID");
		for(int i=0;i<countD;i++){
			result=MEMTool.getInstance().insertGiftSalesCardD(parmD.getRow(i), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		//微信支付宝保存交易号码
    	if(null!=payCashParm){
    		TParm tempParm=new TParm();
    		tempParm.setData("TRADE_NO",parmM.getValue("TRADE_NO"));
    		if(null!=payCashParm.getValue("WX")&&payCashParm.getValue("WX").length()>0){
    			tempParm.setData("WX_BUSINESS_NO",payCashParm.getValue("WX"));
			}else{
				tempParm.setData("WX_BUSINESS_NO","");
			}
			if(null!=payCashParm.getValue("ZFB")&&payCashParm.getValue("ZFB").length()>0){
				tempParm.setData("ZFB_BUSINESS_NO",payCashParm.getValue("ZFB"));
			}else{
				tempParm.setData("ZFB_BUSINESS_NO","");
			}
    		result = MEMTool.getInstance().updateCashTypeMemGiftcardTradeMBusinessNo(tempParm, conn);
        	if (result.getErrCode() < 0) {
        		conn.rollback();
        		conn.close();
    			return result;
    		}
    	}
		conn.commit();
		conn.close();
		return result;
		
	}
	/**
	 * 更新关于身份的表
	 * @param parm
	 * @return
	 */
	public TParm updateCTZS(TParm parm){
		TParm result = new TParm();
		TConnection conn = this.getConnection();
		TParm patInfoCtz = parm.getParm("patInfoCtz");
    	TParm regPatadm = parm.getParm("regPatadm");
//    	TParm opdOrder = parm.getParm("opdOrder");
    	TParm parmMem = parm.getParm("parmMem");
    	TParm feeDepreciation = parm.getParm("feeDepreciation");
    	TParm updateMemTradeEndDate = parm.getParm("updateMemTradeEndDate");
    	TParm updateMemPatInfoEndDate = parm.getParm("updateMemPatInfoEndDate");
    	TParm payCashParm=parm.getParm("payCashParm");
    	result = MEMTool.getInstance().updatePatInfoCtz(patInfoCtz, conn);
    	if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
    	for(int i=0;i<regPatadm.getCount("CASE_NO");i++){
    		result = MEMTool.getInstance().updateRegPatadmCtz(regPatadm.getRow(i), conn);
    		if (result.getErrCode() < 0) {
    			conn.rollback();
    			conn.close();
    			return result;
    		}
    	}
    	
//    	for(int i=0;i<opdOrder.getCount("CASE_NO");i++){
//    		result = MEMTool.getInstance().updateOdpOrderCtz(opdOrder.getRow(i), conn);
//    		if (result.getErrCode() < 0) {
//    			conn.rollback();
//    			conn.close();
//    			return result;
//    		}
//    	}
    	result = MEMTool.getInstance().updateMemPatInfoEndDate(updateMemPatInfoEndDate, conn);
    	if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}result = MEMTool.getInstance().updateMemTradeEndDate(updateMemTradeEndDate, conn);
    	if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
    	result = MEMTool.getInstance().insertMemTrade(parmMem, conn);
    	if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
    	
    	result = MEMTool.getInstance().insertMemFeeDepreciation(feeDepreciation, conn);
    	if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
    	//微信支付宝保存交易号码
    	if(null!=payCashParm){
    		TParm tempParm=new TParm();
    		tempParm.setData("TRADE_NO",parmMem.getValue("TRADE_NO"));
    		if(null!=payCashParm.getValue("WX")&&payCashParm.getValue("WX").length()>0){
    			tempParm.setData("WX_BUSINESS_NO",payCashParm.getValue("WX"));
			}else{
				tempParm.setData("WX_BUSINESS_NO","");
			}
			if(null!=payCashParm.getValue("ZFB")&&payCashParm.getValue("ZFB").length()>0){
				tempParm.setData("ZFB_BUSINESS_NO",payCashParm.getValue("ZFB"));
			}else{
				tempParm.setData("ZFB_BUSINESS_NO","");
			}
    		result = MEMTool.getInstance().updateCashTypeMemTradeBusinessNo(tempParm, conn);
        	if (result.getErrCode() < 0) {
        		conn.rollback();
        		conn.close();
    			return result;
    		}
    	}
    	conn.commit();
		conn.close();
		return result;
	}
	
	public TParm updateMemTradeInfo(TParm parm){
		TParm result = new TParm();
		TConnection connection = this.getConnection();
		TParm parmMemTrade = parm.getParm("parmMemTrade");
    	TParm parmMemInfo = parm.getParm("parmMemInfo");
    	TParm parmMemFeeD = parm.getParm("parmMemFeeD");
    	TParm parmSysPatInfo = parm.getParm("parmSysPatInfo");
    	String oper = parmMemTrade.getValue("OPER");
    	TParm payCashParm=parm.getParm("payCashParm");//微信支付宝交易号保存
    	if("UPDATE".equals(oper)){
    		result = MEMTool.getInstance().updateMemTrade(parmMemTrade, connection);
        	if (result.getErrCode() < 0) {
        		connection.rollback();
    			connection.close();
    			return result;
    		}
    	}else if("NEW".equals(oper)){
    		result = MEMTool.getInstance().addMemTrade(parmMemTrade, connection);
        	if (result.getErrCode() < 0) {
        		connection.rollback();
    			connection.close();
    			return result;
    		}
    	}
    	//微信支付宝保存交易号码
    	if(null!=payCashParm){
    		TParm tempParm=new TParm();
    		tempParm.setData("TRADE_NO",parmMemTrade.getValue("TRADE_NO"));
    		if(null!=payCashParm.getValue("WX")&&payCashParm.getValue("WX").length()>0){
    			tempParm.setData("WX_BUSINESS_NO",payCashParm.getValue("WX"));
			}else{
				tempParm.setData("WX_BUSINESS_NO","");
			}
			if(null!=payCashParm.getValue("ZFB")&&payCashParm.getValue("ZFB").length()>0){
				tempParm.setData("ZFB_BUSINESS_NO",payCashParm.getValue("ZFB"));
			}else{
				tempParm.setData("ZFB_BUSINESS_NO","");
			}
    		result = MEMTool.getInstance().updateCashTypeMemTradeBusinessNo(tempParm, connection);
        	if (result.getErrCode() < 0) {
        		connection.rollback();
    			connection.close();
    			return result;
    		}
    	}
    	result = MEMTool.getInstance().updateMemInfo(parmMemInfo, connection);
    	if (result.getErrCode() < 0) {
    		connection.rollback();
			connection.close();
			return result;
		}
    	if("".equals(parmMemFeeD.getValue("REASON"))){
    		result = MEMTool.getInstance().insertMemFeeDepreciation(parmMemFeeD, connection);
        	if (result.getErrCode() < 0) {
        		connection.rollback();
    			connection.close();
    			return result;
    		}  
    	}
    	if(parmSysPatInfo.getValue("CTZ1_CODE").length() > 0){
    		result = MEMTool.getInstance().updateSysPatInfo(parmSysPatInfo, connection);
        	if (result.getErrCode() < 0) {
        		connection.rollback();
    			connection.close();
    			return result;
    		} 
    	}
    	
    	
		connection.commit();
		connection.close();
		return result;
		
	}
	
	/**
	 * 修改礼品卡现金券交易表
	 * @param parm
	 * @return
	 */
	public TParm deleteGiftSalesCardMD(TParm parm){
		TParm result = new TParm();
		TParm parmMH = new TParm();
		TParm parmDH = new TParm();
		TConnection conn = this.getConnection();
		for(int j=0;j<parm.getCount("TRADE_NO");j++){
			TParm parmMlog = MEMTool.getInstance().selectGiftSalesCardM(parm.getRow(j));
			if (parmMlog.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			parmMH.setData("ACTION_DATE", TJDODBTool.getInstance().getDBTime());
			parmMH.setData("ACTION_TYPE", "2");
	    	parmMH.setData("TRADE_NO", parmMlog.getValue("TRADE_NO", 0));
	    	parmMH.setData("PURCHASER_NAME", parmMlog.getValue("PURCHASER_NAME", 0));
	    	parmMH.setData("CERTIFICATE_TYPE", parmMlog.getValue("CERTIFICATE_TYPE", 0));
	    	parmMH.setData("CERTIFICATE_NO",parmMlog.getValue("CERTIFICATE_NO", 0));
	    	parmMH.setData("PHONE", parmMlog.getValue("PHONE", 0));
	    	parmMH.setData("AR_AMT", parmMlog.getValue("AR_AMT", 0));
	    	parmMH.setData("DESCRIPTION", parmMlog.getValue("DESCRIPTION", 0));
	    	parmMH.setData("OPT_USER", parmMlog.getValue("OPT_USER", 0));
	    	parmMH.setData("OPT_DATE", parmMlog.getData("OPT_DATE", 0));
	    	parmMH.setData("OPT_TERM", parmMlog.getValue("OPT_TERM", 0));
	    	parmMH.setData("INTRODUCER1", parmMlog.getValue("INTRODUCER1", 0));
	    	parmMH.setData("INTRODUCER2", parmMlog.getValue("INTRODUCER2", 0));
	    	parmMH.setData("INTRODUCER3", parmMlog.getValue("INTRODUCER3", 0));
	    	parmMH.setData("PAY_TYPE01", parmMlog.getValue("PAY_TYPE01", 0));
        	parmMH.setData("PAY_TYPE02", parmMlog.getValue("PAY_TYPE02", 0));
        	parmMH.setData("PAY_TYPE03", parmMlog.getValue("PAY_TYPE03", 0));
        	parmMH.setData("PAY_TYPE04", parmMlog.getValue("PAY_TYPE04", 0));
        	parmMH.setData("PAY_TYPE05", parmMlog.getValue("PAY_TYPE05", 0));
        	parmMH.setData("PAY_TYPE06", parmMlog.getValue("PAY_TYPE06", 0));
        	parmMH.setData("PAY_TYPE07", parmMlog.getValue("PAY_TYPE07", 0));
        	parmMH.setData("PAY_TYPE08", parmMlog.getValue("PAY_TYPE08", 0));
        	parmMH.setData("PAY_TYPE09", parmMlog.getValue("PAY_TYPE09", 0));
        	parmMH.setData("PAY_TYPE10", parmMlog.getValue("PAY_TYPE10", 0));
	    	TParm parmDlog = MEMTool.getInstance().selectGiftSalesCardD(parm.getRow(j));
	    	if (parmDlog.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
	    	for(int i=0;i<parmDlog.getCount();i++){
	    		parmDH.addData("ACTION_DATE", TJDODBTool.getInstance().getDBTime());
	    		parmDH.addData("ID", parmDlog.getValue("ID", i));
	    		parmDH.addData("TRADE_NO", parmDlog.getValue("TRADE_NO", i));
	    		parmDH.addData("GIFTCARD_CODE", parmDlog.getValue("GIFTCARD_CODE", i));
	    		parmDH.addData("RETAIL_PRICE", parmDlog.getValue("RETAIL_PRICE", i));
	    		parmDH.addData("FACE_VALUE", parmDlog.getValue("FACE_VALUE", i));
	    		parmDH.addData("GIFTCARD_NUM", parmDlog.getValue("GIFTCARD_NUM", i));
	    		parmDH.addData("AR_AMT", parmDlog.getValue("AR_AMT", i));
	    		parmDH.addData("DESCRIPTION", parmDlog.getValue("DESCRIPTION", i));
	    	}
			
			
			result = MEMTool.getInstance().insertGiftSalesCardMH(parmMH, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			
			int countDH = parmDH.getCount("ID");
			for(int i=0;i<countDH;i++){
				result = MEMTool.getInstance().insertGiftSalesCardDH(parmDH.getRow(i), conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			
			
			
			result = MEMTool.getInstance().deleteGiftSalesCardD(parm.getRow(j), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			result = MEMTool.getInstance().deleteGiftSalesCardM(parm.getRow(j), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		
		
		
		conn.commit();
		conn.close();
		return result;
		
	}
	
	/**
	 * 更新会员折旧
	 * @param parm
	 * @return
	 */
	public TParm updateMemFeeDepreciation(TParm parm){
		TParm result = new TParm();
		TConnection conn = this.getConnection();
		TParm parmMemTrade = parm.getParm("parmMemTrade");
		TParm parmMemFeeD = parm.getParm("parmMemFeeD");
		
		for(int i=0;i<parmMemFeeD.getCount("MR_NO");i++){
//			System.out.println("会  员：=="+parmMemFeeD.getRow(i));
			result = MEMTool.getInstance().insertMemFeeDepreciation(parmMemFeeD.getRow(i), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		for(int i=0;i<parmMemTrade.getCount("MR_NO");i++){
			result = MEMTool.getInstance().updateMemTradeLD(parmMemTrade.getRow(i), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		conn.commit();
		conn.close();
		return result;	
	}
	/**
	 * 停卡
	 * @param parm
	 * @return
	 */
	public TParm stopCardMemTrade(TParm parm){
		TParm result = new TParm();
		TConnection connection = this.getConnection();
		TParm parmMemInfo = parm.getParm("parmMemInfo");
		TParm parmMemTrade = parm.getParm("parmMemTrade");
		TParm payCashParm= parm.getParm("payCashParm");
		result = MEMTool.getInstance().stopCardUpdateMemInfo(parmMemInfo, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		result = MEMTool.getInstance().stopCardUpdateMemTrade(parmMemTrade, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		result = MEMTool.getInstance().stopCardInsertMemTrade(parmMemTrade, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		//微信支付宝保存交易号码
    	if(null!=payCashParm){
    		TParm tempParm=new TParm();
    		tempParm.setData("TRADE_NO",parmMemTrade.getValue("TRADE_NO"));
    		if(null!=payCashParm.getValue("WX")&&payCashParm.getValue("WX").length()>0){
    			tempParm.setData("WX_BUSINESS_NO",payCashParm.getValue("WX"));
			}else{
				tempParm.setData("WX_BUSINESS_NO","");
			}
			if(null!=payCashParm.getValue("ZFB")&&payCashParm.getValue("ZFB").length()>0){
				tempParm.setData("ZFB_BUSINESS_NO",payCashParm.getValue("ZFB"));
			}else{
				tempParm.setData("ZFB_BUSINESS_NO","");
			}
    		result = MEMTool.getInstance().updateCashTypeMemTradeBusinessNo(tempParm, connection);
        	if (result.getErrCode() < 0) {
        		connection.rollback();
    			connection.close();
    			return result;
    		}
    	}
		connection.commit();
		connection.close();
		return result;	
	}
	/**
	 * 撤消停卡
	 * @param parm
	 * @return
	 */
	public TParm revokeCardMemTrade(TParm parm){
		TParm result = new TParm();
		TConnection connection = this.getConnection();

		result = MEMTool.getInstance().stopCardUpdateMemInfo(parm, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		
		result = MEMTool.getInstance().revokeCardUpdateMemTrade(parm, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		
		result = MEMTool.getInstance().revokeCardUpdateNewMemTrade(parm, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		
		
		connection.commit();
		connection.close();
		return result;	
	}
	
	/**
	 * 换卡时更新卡号
	 * @param parm
	 * @return
	 */
	public TParm updateMemCardNo(TParm parm){
		TParm result = new TParm();
		TConnection connection = this.getConnection();
		result = MEMTool.getInstance().updateMemTradeCardNo(parm, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		result = MEMTool.getInstance().updateMemFeeDepreciationCardNo(parm, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		
		connection.commit();
		connection.close();
		return result;
	}
	
}
