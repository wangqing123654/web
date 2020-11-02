package jdo.mem;



import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class MEMTool extends TJDOTool{
	
	/**
     * 实例
     */
    private static MEMTool instanceObject;
    
    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MEMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MEMTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public MEMTool() {
        setModuleName("mem\\MEMModule.x");
        onInit();
    }
    /**
     * 查询礼品现金卡交易主档
     * @param regMethod String
     * @return TParm
     */
	public TParm selectGiftSalesCardM(TParm parm){
		TParm result = new TParm();
		result = query("selectGiftSalesCardM", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
    /**
     * 插入礼品现金卡交易主档
     * @param regMethod String
     * @return TParm
     */
    public TParm insertGiftSalesCardM(TParm parm, TConnection conn) {
    	TParm result = new TParm();
		result = update("insertGiftSalesCardM", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
    }
    /**
     * 修改礼品现金卡交易主档
     * @param regMethod String
     * @return TParm
     */
    public TParm updateGiftSalesCardM(TParm parm, TConnection conn) {
    	TParm result = new TParm();
		result = update("updateGiftSalesCardM", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
    }
    /**
     * 删除礼品现金卡交易主档
     * @param regMethod String
     * @return TParm
     */
    public TParm deleteGiftSalesCardM(TParm parm, TConnection conn) {
    	TParm result = new TParm();
		result = update("deleteGiftSalesCardM", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
    }
    
    
    /**
     * 查询礼品现金卡交易明细
     * @param regMethod String
     * @return TParm
     */
	public TParm selectGiftSalesCardD(TParm parm){
		TParm result = new TParm();
		result = query("selectGiftSalesCardD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
    /**
     * 插入礼品现金卡交易明细
     * @param regMethod String
     * @return TParm
     */
    public TParm insertGiftSalesCardD(TParm parm, TConnection conn) {
    	TParm result = new TParm();
		result = update("insertGiftSalesCardD", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
    }
    /**
     * 修改礼品现金卡交易明细
     * @param regMethod String
     * @return TParm
     */
    public TParm updateGiftSalesCardD(TParm parm, TConnection conn) {
    	TParm result = new TParm();
		result = update("updateGiftSalesCardD", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
    }
    /**
     * 删除礼品现金卡交易明细
     * @param regMethod String
     * @return TParm
     */
    public TParm deleteGiftSalesCardD(TParm parm, TConnection conn) {
    	TParm result = new TParm();
		result = update("deleteGiftSalesCardD", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
    }
    
    /**
	 * 得到礼品卡交易号
	 * 
	 * @return String
	 */
	public String getGIFTTradeNo() {
		return SystemTool.getInstance().getNo("ALL", "EKT", "GIFT_TRADE_NO",
				"GIFT_TRADE_NO");
	}
	
	/**
	 * 得到会员卡交易号
	 * 
	 * @return String
	 */
	public String getMEMTradeNo() {
		return SystemTool.getInstance().getNo("ALL", "EKT", "MEM_TRADE_NO",
				"MEM_TRADE_NO");
	}
	
	/**
	 * 更新病患信息表的身份
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updatePatInfoCtz(TParm parm, TConnection conn){
		TParm result = new TParm();
		result = update("updateSysPatInfo", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
	
	/**
	 * 更新挂号信息表中的身份
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateRegPatadmCtz(TParm parm, TConnection conn){
		TParm result = new TParm();
		result = update("updateRegPatadmCtz", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
	
	/**
	 * 更新opd_order表中身份的打折价钱
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateOdpOrderCtz(TParm parm, TConnection conn){
		TParm result = new TParm();
		result = update("updateOdpOrderCtz", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
	
	/**
	 * 插入会员卡交易表
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm insertMemTrade(TParm parm, TConnection conn){
		TParm result = new TParm();
		result = update("insertMemTrade", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
	
	/**
	 * 更新会员卡交易表中的结束时间
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateMemTradeEndDate(TParm parm, TConnection conn){
		TParm result = new TParm();
		result = update("updateMemTradeEndDate", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
	
	/**
	 * 更新会员卡表中的结束时间
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateMemPatInfoEndDate(TParm parm, TConnection conn){
		TParm result = new TParm();
		result = update("updateMemPatInfoEndDate", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
	
	
	/**
	 * 查询会员信息
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm selectMemPatInfo(TParm parm){
		TParm result = new TParm();
		result = query("selectMemPatInfo", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
	
	/**
	 * 在挂号页面插入会员信息
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm insertMemPatInfo(TParm parm){
		TParm result = new TParm();
		result = update("insertMemPatInfo", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
	
	/**
	 * 更新交易表中的状态
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateMemTrade(TParm parm, TConnection conn){
		TParm result = new TParm();
		if(parm.getValue("PAY_TYPE11") == null){
			parm.setData("PAY_TYPE11", 0);
		}
		if(parm.getValue("MEMO11") == null){
			parm.setData("MEMO11", "");
		}
		result = update("updateMemTrade", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
	/**
	 * 微信支付宝更新交易号码操作,会员卡销售表
	 * @param parm
	 * @return
	 */
	public TParm updateCashTypeMemTradeBusinessNo(TParm parm, TConnection conn){
		TParm result = new TParm();
		result = update("updateCashTypeMemTradeBusinessNo", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
	/**
	 * 微信支付宝更新交易号码操作,礼品卡销售表
	 * @param parm
	 * @return
	 */
	public TParm updateCashTypeMemGiftcardTradeMBusinessNo(TParm parm, TConnection conn){
		TParm result = new TParm();
		result = update("updateCashTypeMemGiftcardTradeMBusinessNo", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
	
	/**
	 * 微信支付宝更新交易号码操作,套餐销售表
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateCashTypeMemPackTradeMBusinessNo(TParm parm, TConnection conn){
		TParm result = new TParm();
		result = update("updateCashTypeMemPackTradeMBusinessNo", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
	/**
	 * 更新交易表中更新交易表折旧时间
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateMemTradeLD(TParm parm, TConnection conn){
		TParm result = new TParm();
		result = update("updateMemTradeLD", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
	
	/**
	 * 更新会员信息表
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateMemInfo(TParm parm, TConnection conn){
		TParm result = new TParm();
		result = update("updateMemPatInfo", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
	
	/**
	 * 更新病患信息表中的身份
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateSysPatInfo(TParm parm, TConnection conn){
		TParm result = new TParm();
		result = update("updateSysPatInfo1", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
	
	
	
	/**
     * 插入礼品现金卡交易明细历史
     * @param regMethod String
     * @return TParm
     */
    public TParm insertGiftSalesCardDH(TParm parm, TConnection conn) {
    	TParm result = new TParm();
		result = update("insertGiftSalesCardDH", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
    }
    /**
     * 插入礼品现金卡交易主档历史
     * @param regMethod String
     * @return TParm
     */
    public TParm insertGiftSalesCardMH(TParm parm, TConnection conn) {
    	TParm result = new TParm();
		result = update("insertGiftSalesCardMH", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
    }
    
    /**
     * 添加会员费折旧记录
     * @param regMethod String
     * @return TParm
     */
    public TParm insertMemFeeDepreciation(TParm parm, TConnection conn) {
    	TParm result = new TParm();
		result = update("insertMemFeeDepreciation", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
    }
    /**
     * 更新会员费折旧记录
     * @param regMethod String
     * @return TParm
     */
    public TParm updateMemFeeDepreciation(TParm parm, TConnection conn) {
    	TParm result = new TParm();
		result = update("updateMemFeeDepreciation", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
    }

	 /**
	 * 新增交易表数据-duzhw add 20140403
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm addMemTrade(TParm parm, TConnection conn){
		TParm result = new TParm();
		if(parm.getValue("PAY_TYPE11") == null){
			parm.setData("PAY_TYPE11", 0);
		}
		if(parm.getValue("MEMO11") == null){
			parm.setData("MEMO11", "");
		}
		
		result = update("addMemTrade", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;	
	}
	
	/**
	 * 停卡更新会员结束日期
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm stopCardUpdateMemInfo(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("updateMemPatInfoEndDateNew", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 停卡更新交易表
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm stopCardUpdateMemTrade(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("stopCardUpdateMemTrade", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 停卡在交易表中新增一条记录
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm stopCardInsertMemTrade(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("stopCardInsertMemTrade", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 取消停卡更新交易表
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm revokeCardUpdateMemTrade(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("revokeCardUpdateMemTrade", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 将停卡记录状态更新为作废
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm revokeCardUpdateNewMemTrade(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("revokeCardUpdateNewMemTrade", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 换卡时更新卡号
	 * @param parm
	 * @return
	 */
	public  TParm updateMemTradeCardNo(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("updateMemTradeCardNo", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 更新折旧表中的卡号
	 * @param parm
	 * @param conn
	 * @return
	 */
	public  TParm updateMemFeeDepreciationCardNo(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("updateMemFeeDepreciationCardNo", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 更新会员表中的监护人和监护关系
	 * @param parm
	 * @param conn
	 * @return
	 */
	public  TParm updateMemPatInfoGuardian(TParm parm){
		TParm result = new TParm();
		result = update("updateMemPatInfoGuardian",parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 日结会员卡数据
	 * yanjing 20140527
	 */
	public  TParm updateMemTradeData(TParm parm,TConnection connection){
		TParm result = new TParm();
		result = update("updateMemTradeData",parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 日结套餐数据
	 * yanjing 20140527
	 */
	public  TParm updateMemPackageData(TParm parm,TConnection connection){
		TParm result = new TParm();
		result = update("updateMemPackageData",parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 日结礼品卡数据
	 * yanjing 20140527
	 */
	public  TParm updateMemGiftCardData(TParm parm,TConnection connection){
		TParm result = new TParm();
		result = update("updateMemGiftCardData",parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 查询是否有日结会员卡数据
	 * yanjing 20140527
	 */
	public  TParm selectMemTradeData(TParm parm){
		TParm result = new TParm();
		result = query("selectMemTradeData",parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 查询是否有日结套餐数据
	 * yanjing 20140527
	 */
	public  TParm selectMemPackageData(TParm parm){
		TParm result = new TParm();
		result = query("selectMemPackageData",parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 查询是否有日结礼品卡数据
	 * yanjing 20140527
	 */
	public  TParm selectMemGiftCardData(TParm parm){
		TParm result = new TParm();
		result = query("selectMemGiftCardData",parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	
	/**
	 * 更新opd_order表中与套餐相同的医嘱
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm updateOpdOrder(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("updateOpdOrder", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	
	/**
	 * 与已开立医嘱对比后更新套餐细表数据
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updatePaceageSectionD(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("updatePaceageSectionD", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	

	/**
	 * 与已开立医嘱对比后更新套餐主表数据
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updatePaceageSection(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("updatePaceageSection", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 更新套餐主档、时程和明细医嘱的套餐使用状态(住院登记使用)
	 * @author xiongwg20150703
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm upMemUsedFlg(TParm parm,TConnection conn){
		TParm result = new TParm();
		//更新套餐时程主表
		result = update("updateADMPackageSection", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		//更新套餐时程细表
		result = update("updateADMPackageSectionD", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		//更新套餐主档
		result = update("updateADMPackageTradeM", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 套餐项目变更时，删除除旧集合医嘱的细项
	 */
	public TParm delMemPackageSectionDPrice(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("delMemPackageSectionDPrice", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 套餐项目变更时，删除除旧集合医嘱的细项
	 */
	public TParm delMemPackageSectionD(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("delMemPackageSectionD", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 套餐项目变更时，添加新集合医嘱的细项
	 */
	public TParm insertMemPackageSectionD(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("insertMemPackageSectionD", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 套餐项目变更时，将新医嘱更新对旧医嘱上面
	 */
	public TParm updateMemPackageSectionD(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("updateMemPackageSectionD", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 套餐项目变更时，更新MEM_PACKAGE_SECTION表中原价
	 */
	public TParm updateMemPackageSection(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("updateMemPackageSection", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 套餐项目变更时，更新MEM_PACKAGE_SECTION_D_PRICE表中的折扣率与原价
	 */
	public TParm updateMemPackageSectionDPrice(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("updateMemPackageSectionDPrice", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 套餐项目变更时，更新MEM_PACKAGE_SECTION_D_PRICE表中的折扣率与原价
	 */
	public TParm updateMemPackageSectionPrice(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("updateMemPackageSectionPrice", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	
	/**
	 * 套餐项目变更时，插入历史表
	 */
	public TParm insertMemPacageSectionDHistory(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("insertMemPacageSectionDHistory", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 套餐项目变更时，插入MEM_PACKAGE_SECTION_D_PRICE表（细项价钱）
	 */
	public TParm insertMemPackageSectionDPrice(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("insertMemPackageSectionDPrice", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 套餐项目变更时，将新医嘱更到已卖出的时程上(MEM_PAT_PACKAGE_SECTION_D)除了细项之外的医嘱进行更新
	 */
	public TParm updateMemPatPackageSectionD(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("updateMemPatPackageSectionD", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 套餐项目变更时，删除已经卖给病人的没有使用的旧集合医嘱的细项
	 */
	public TParm delMemPatPackageSectionD(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("delMemPatPackageSectionD", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 套餐项目变更时，对已购买的时程添加新医嘱的细项
	 */
	public TParm insertMemPatPackageSectionD(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("insertMemPatPackageSectionD", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * //更新MEM_PACKAGE_SECTION_D表中的的原价UNIT_PRICE
	 */
	public TParm updateMemPackageSectionDUnitPrice(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("updateMemPackageSectionDUnitPrice", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	
	/**
	 * //更新MEM_PACKAGE_SECTION_D_PRICE表中的原价
	 */
	public TParm updateMemPackageSectionDPriceOriginal(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("updateMemPackageSectionDPriceOriginal", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	
	/**
	 * //更新MEM_PACKAGE_SECTION_PRICE表中的原价
	 */
	public TParm updateMemPackageSectionPriceOriginal(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("updateMemPackageSectionPriceOriginal", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	

	/**
	 * //更新套餐中的比列MEM_PACKAGE_PRICE
	 */
	public TParm updateMemPackagePrice(TParm parm,TConnection conn){
		TParm result = new TParm();
		result = update("updateMemPackagePrice", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 住院套餐通过就诊号查询套餐字典数据
	 * =======pangben 2016-8-18
	 * @param parm
	 * @return
	 */
	public TParm selectMemPackageSectionDByCaseNo(TParm parm){
		TParm result = new TParm();
		result = query("selectMemPackageSectionDByCaseNo", parm);
		return result;
	}
	/**
	 * 查询套餐金额
	 * @param caseNo
	 * @return
	 */
	public TParm selectMemPackTradeM(String caseNo){
		String sql="SELECT M_CASE_NO,NEW_BORN_FLG FROM ADM_INP WHERE CASE_NO='"+caseNo+"'";
    	TParm newBobyParm = new TParm(TJDODBTool.getInstance().select(sql));
    	String caseNoNew=caseNo;
    	if (newBobyParm.getValue("NEW_BORN_FLG",0).equals("Y")) {
    		caseNoNew=newBobyParm.getValue("M_CASE_NO",0);
		}
		sql="SELECT A.CASE_NO,A.NEW_BORN_FLG,B.AR_AMT AS FEE,A.DEPT_CODE,A.STATION_CODE," +
		"A.BED_NO,A.VS_DR_CODE,A.CLNCPATH_CODE,A.DS_DATE,D.PACKAGE_DESC,A.INCLUDE_FLG,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE FROM ADM_INP A,MEM_PACKAGE_TRADE_M B," +
		"MEM_PAT_PACKAGE_SECTION C,MEM_PACKAGE D WHERE B.TRADE_NO=C.TRADE_NO AND C.PACKAGE_CODE=D.PACKAGE_CODE" +
		" AND B.USED_FLG='1' AND D.ADM_TYPE='I' AND A.CASE_NO=B.CASE_NO AND A.LUMPWORK_CODE=C.PACKAGE_CODE AND A.CASE_NO='"+caseNoNew+
		"' AND A.LUMPWORK_CODE IS NOT NULL GROUP BY A.CASE_NO,A.NEW_BORN_FLG,B.AR_AMT,A.DEPT_CODE,A.STATION_CODE," +
		"A.BED_NO,A.VS_DR_CODE,A.CLNCPATH_CODE,A.DS_DATE,D.PACKAGE_DESC,A.INCLUDE_FLG,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
	}
	/**
	 * 查询套餐金额
	 * @param mrNo
	 * @return
	 */
	public TParm selectMemPackTradeM(String mrNo, String packCode) {
		String sql = " SELECT C.PACKAGE_CODE AS ID, C.PACKAGE_DESC AS NAME,A.AR_AMT AS FEE, "
				+ " A.TRADE_NO,C.PACKAGE_ENG_DESC AS ENNAME, C.PY1, C.PY2,C.SEQ  "
				+ " FROM MEM_PACKAGE_TRADE_M A , MEM_PAT_PACKAGE_SECTION B, MEM_PACKAGE C  "
				+ " WHERE A.TRADE_NO = B.TRADE_NO  "
				+ " AND B.PACKAGE_CODE = C.PACKAGE_CODE AND C.PARENT_PACKAGE_CODE IS NOT NULL AND B.REST_TRADE_NO IS NULL";
		String sql1 = " GROUP BY C.SEQ,C.PACKAGE_CODE, C.PACKAGE_DESC,A.AR_AMT, "
				+ " A.TRADE_NO,C.PACKAGE_ENG_DESC,C.PY1,C.PY2 "
				+ " ORDER BY C.SEQ, C.PACKAGE_CODE ";
		StringBuffer sb = new StringBuffer();
		if (mrNo != null && mrNo.length() > 0) {
			sb.append(" AND A.MR_NO = '" + mrNo + "' ");
		}
		sb.append(" AND A.USED_FLG = '0' ").append(" AND C.ADM_TYPE = 'I' ").append(
				" AND B.PACKAGE_CODE = '" + packCode + "' ");
		if (sb.length() > 0)
			sql += sb.toString() + sql1;
		else
			sql = sql + sql1;
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	/**
	 * 住院补充计费界面套餐病患添加集合医嘱操作
	 * @param caseNo
	 * @param lumpworkCode
	 * @param orderCode
	 * @return
	 */
	public TParm getLumpWorkOrderSetParm(String caseNo,String lumpworkCode,String orderCode){
		//重新获得折扣后的金额
    	String querySql = "SELECT A.ORDER_CODE,B.ORDER_CAT1_CODE,B.CAT1_TYPE,A.ORDERSET_CODE,B.CHARGE_HOSP_CODE ," +
    			"A.ORDER_NUM MEDI_QTY,A.UNIT_CODE,'' DOSE_CODE,A.ORDER_NUM TOTQTY,A.UNIT_CODE DOSAGE_UNIT," +
    			"C.IPD_CHARGE_CODE REXP_CODE,A.ORDER_DESC,A.UNIT_PRICE AS OWN_PRICE, A.UNIT_PRICE AS OWN_PRICE2," +
    			"A.UNIT_PRICE AS OWN_PRICE3 ,A.UNIT_PRICE AS NHI_PRICE,B.OPTITEM_CODE,B.INSPAY_TYPE,B.RPTTYPE_CODE,B.DEGREE_CODE " +
    			"FROM MEM_PAT_PACKAGE_SECTION_D A ,SYS_FEE B,SYS_CHARGE_HOSP C " +
    			"WHERE A.ORDER_CODE=B.ORDER_CODE AND A.SETMAIN_FLG='N' AND B.CHARGE_HOSP_CODE=C.CHARGE_HOSP_CODE  AND A.CASE_NO='" +caseNo
		+ "' AND A.PACKAGE_CODE = '"+lumpworkCode+"' AND A.ORDERSET_CODE='"+orderCode+"'";
    	TParm parmIbsOrddNew = new TParm(TJDODBTool.getInstance().select(querySql));
    	return parmIbsOrddNew;
	}
}
