package com.javahis.ui.testOpb;

import java.math.BigDecimal;
import java.util.List;

import jdo.ekt.EKTpreDebtTool;

import com.javahis.ui.testOpb.bean.OpdOrder;

/**
 * 支付方式
 * @author zhangp
 *
 */
public abstract class Pay {

	/**
	 * 校验余额
	 * @param caseNo
	 * @param mrNo
	 * @param list
	 * @param updateList
	 * @param deletList
	 * @return
	 */
	public String checkMaster(String caseNo, String mrNo, List<OpdOrder> list){
		return EKTpreDebtTool.getInstance().checkMasterForOpb(caseNo, mrNo, list);
	}
	
	/**
	 * 
	 * @param caseNo
	 * @param master
	 * @param list
	 * @return
	 */
	public double getMasterForOpb(double master, List<com.javahis.ui.testOpb.bean.OpdOrder> list) {
		
		BigDecimal opbAmt = new BigDecimal(0);//门诊金额
		
		for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder : list) {
			if((opdOrder.memPackageId == null || opdOrder.memPackageId.length() == 0) && "N".equals(opdOrder.billFlg) && "Y".equals(opdOrder.execFlg)){
				opbAmt = opbAmt.add(opdOrder.showAmt);
			}
		}
		
		return master - opbAmt.doubleValue();
	}
	
	
	public double getAccountForOpb( List<com.javahis.ui.testOpb.bean.OpdOrder> list){
		BigDecimal opbAmt = new BigDecimal(0);//门诊金额
		for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder : list) {
			if((opdOrder.memPackageId == null || opdOrder.memPackageId.length() == 0) && "N".equals(opdOrder.billFlg) && "Y".equals(opdOrder.execFlg)){
				opbAmt = opbAmt.add(opdOrder.showAmt);
			}
		}
		return opbAmt.doubleValue();
	}
	
	public double getNoAccountForOpb( List<com.javahis.ui.testOpb.bean.OpdOrder> list){
		BigDecimal opbAmt = new BigDecimal(0);//门诊金额
		for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder : list) {
			if((opdOrder.memPackageId == null || opdOrder.memPackageId.length() == 0) && "N".equals(opdOrder.billFlg) && "N".equals(opdOrder.execFlg)){
				opbAmt = opbAmt.add(opdOrder.showAmt);
			}
		}
		return opbAmt.doubleValue();
	}
	
	public double getTotAccountForOpb( List<com.javahis.ui.testOpb.bean.OpdOrder> list){
		BigDecimal opbAmt = new BigDecimal(0);//门诊金额
		for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder : list) {
			if((opdOrder.memPackageId == null || opdOrder.memPackageId.length() == 0) ){
				opbAmt = opbAmt.add(opdOrder.showAmt);
			}
		}
		return opbAmt.doubleValue();
	}
	
	
	
	
	public abstract void pay();
}
