package com.javahis.ui.testOpb;

import com.dongyang.data.TParm;
/**
 * ∑—”√≤È—Ø
 * @author Administrator
 *
 */
public class OPBChargeSelFee {
	public OPBChargeControl control;
	private OPBChargeReg opbChargeReg;
	private OPBChargePat opbChargePat;
	private static final String URL_SYSFEE_ORDSETOPTION = "%ROOT%\\config\\opb\\OPBSelOrderm.x";
	private static final String URL_OPBSELRETURN = "%ROOT%\\config\\opb\\OPBSelReturnOrderm.x";
	
	public OPBChargeSelFee(OPBChargeControl opbChargeControl){
		control = opbChargeControl;
		opbChargeReg = control.opbChargeReg;
		opbChargePat = control.opbChargePat;
	}
	
	public void onSelFee(){
		if(opbChargeReg.reg != null){
			TParm parm = new TParm();
			parm.setData("CASE_NO", opbChargeReg.reg.caseNo());
			control.openDialog(URL_SYSFEE_ORDSETOPTION, parm, false);
		}
	}
	
	public void onSelReturnFee(){
		TParm parm = new TParm();
		if(opbChargeReg.reg == null){
			parm.setData("CASE_NO", "");
		}else{
			parm.setData("CASE_NO", opbChargeReg.reg.caseNo());
			parm.setData("MR_NO", opbChargePat.pat.getMrNo());
			parm.setData("PAT_NAME", opbChargePat.pat.getName());
		}

		control.openDialog(URL_OPBSELRETURN, parm, false);
		
	}

}
