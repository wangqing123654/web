package com.javahis.ui.testOpb;

import java.sql.Timestamp;

import com.dongyang.data.TParm;

import jdo.reg.PatAdmTool;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * 补充计费挂号
 * @author zhangp
 *
 */
public class OPBChargeReg {
	
	public Reg reg;
	public OPBChargeControl control;
	private OPBChargePat opbChargePat;
	
	private final static String TAG_ADM_DATE = "ADM_DATE";
	private final static String TAG_SERVICE_LEVEL = "SERVICE_LEVEL";
	public final static String TAG_DEPT_CODE = "DEPT_CODE";
	private final static String TAG_DR_CODE = "DR_CODE";
	
	private final static String URL_OPBCHOOSEVISIT = "%ROOT%\\config\\opb\\OPBChooseVisit.x";
	
	private static final String MSG_ERR_REG = "挂号信息错误!";
	
	public OPBChargeReg(OPBChargeControl chargeControl){
		control = chargeControl;
		opbChargePat = control.opbChargePat;
		
		Timestamp today = SystemTool.getInstance().getDate();
		control.setValue(TAG_ADM_DATE, today);
	}
	
	/**
	 * 查询reg
	 */
	public void onQueryReg(){
		
		String regionCode = Operator.getRegion();
		TParm parm = PatAdmTool.getInstance().selDateByMrNo(opbChargePat.pat.getMrNo(),
				(Timestamp) control.getValue(TAG_ADM_DATE),
				(Timestamp) control.getValue(TAG_ADM_DATE), regionCode);
		String caseNo = "";
		if (parm.getCount() == 0) {
			caseNo = onRecord();
		}else if (parm.getCount() == 1) {
			// 初始化reg
			caseNo = parm.getValue("CASE_NO", 0);
		}else if (parm.getCount() > 1){
			caseNo = onRecord();
		}
		
		reg = Reg.onQueryByCaseNo(opbChargePat.pat, caseNo);
		if (reg == null) {
			control.messageBox(MSG_ERR_REG);
			return;
		}
		
		control.setValue(TAG_ADM_DATE, reg.getAdmDate());  //add by hangtt 20141022
		control.setValue(TAG_SERVICE_LEVEL, reg.getServiceLevel());
		control.setValue(TAG_DEPT_CODE, reg.getDeptCode());
		control.setValue(TAG_DR_CODE, reg.getDrCode());
	}
	
	
	
	/**
	 * 就诊记录选择
	 */
	private String onRecord() {
		TParm parm = new TParm();
		parm.setData("MR_NO", opbChargePat.pat.getMrNo());
		parm.setData("PAT_NAME", opbChargePat.pat.getName());
		parm.setData("SEX_CODE", opbChargePat.pat.getSexCode());
		parm.setData("AGE", control.getValue(OPBChargePat.TAG_AGE));
		// 判断是否从明细点开的就诊号选择
		parm.setData("count", "0");
		String caseNo = (String) control.openDialog(
				URL_OPBCHOOSEVISIT, parm);
		return caseNo;
	}
	
	public void onClear(){
		reg = null;
		Timestamp today = SystemTool.getInstance().getDate();
		control.setValue(TAG_ADM_DATE, today);
		control.clearValue(TAG_SERVICE_LEVEL);
		control.clearValue(TAG_DEPT_CODE);
		control.clearValue(TAG_DR_CODE);
	}
	
}
