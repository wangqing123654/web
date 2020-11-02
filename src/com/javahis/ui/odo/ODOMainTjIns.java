package com.javahis.ui.odo;

import com.dongyang.data.TParm;
import com.javahis.util.StringUtil;

import jdo.ins.INSMZConfirmTool;
import jdo.ins.INSTJTool;
import jdo.sys.Operator;

/**
 * 
 * <p>
 * 
 * Title: 门诊医生工作站天津医保对象
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站天津医保对象
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODOMainTjIns {
	public OdoMainControl odoMainControl;
	ODOMainReg odoMainReg;
	ODOMainPat odoMainPat;
	
	private static final String URLINSDRQUERYLIST = "%ROOT%\\config\\ins\\INSDrQueryList.x";
	private static final String URLINSMTREG = "%ROOT%\\config\\ins\\INSMTReg.x";
	private static final String URLINSSPCMEMODIAG = "%ROOT%\\config\\ins\\InsSpcMemoDiag.x";
	
	/**
	 * 是否医保身份 true 医保身份 false 费医保身份
	 */
	private boolean isINSPat = false;
	/**
	 * 医保对象
	 */
	private INSTJTool insTool = null;

	/**
	 * 病患身份
	 */
	private String ctzCode = "";
	// 是否启用医保
	public boolean whetherCallInsItf = false;
	
	public ODOMainTjIns(OdoMainControl odoMainControl){
		this.odoMainControl = odoMainControl;
	}
	
	public void onInit() throws Exception{
		odoMainReg = odoMainControl.odoMainReg;
		odoMainPat = odoMainControl.odoMainPat;
	}
	
	/**
	 * xueyf 2012-02-28 医保门特处方查询
	 */
	public void onINSDrQuery() throws Exception{
		String CASE_NO = odoMainControl.odo.getCaseNo();
		if (StringUtil.isNullString(CASE_NO)) {
			odoMainControl.messageBox("请选择病患。");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		parm.setData("INS_TYPE", odoMainReg.reg.getInsPatType());
		odoMainControl.openDialog(URLINSDRQUERYLIST, parm);
	}
	
	/**
	 * 门特登记
	 */
	public void onMTRegister() throws Exception{
		TParm parm = new TParm();
		parm.setData("MR_NO", odoMainPat.pat.getMrNo());
		parm.setData("CASE_NO", odoMainReg.reg.caseNo());
		odoMainControl.openDialog(URLINSMTREG, parm);
	}
	
	/**
	 * 医保处方查询
	 */
	public void onINSDrQueryList() throws Exception{
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CASE_NO", odoMainReg.reg.caseNo());
		// 是否存在病种
		TParm result = INSMZConfirmTool.getInstance().selectSpcMemo(parm);
		if (result.getErrCode() < 0) {
			odoMainControl.err(result.getErrName() + "：" + result.getErrText());
			return;
		}
		if (result.getCount() <= 0) {
			odoMainControl.messageBox("此就诊病患非联网医保病人");
			return;
		}
		odoMainControl.openDialog(URLINSDRQUERYLIST, result
				.getData());
	}
	
	/**
	 * 初始化医保接口
	 * 
	 * @param ctzCode
	 */
	public void initINS()throws Exception {
		if (insTool == null) {
			insTool = new INSTJTool();
		}
	}
	
	/**
	 * 门特特殊情况使用
	 */
	public void onSpecialCase() throws Exception{
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CASE_NO", odoMainReg.reg.caseNo());
		// 查询是否可以存在特殊情况:挂号医保门特操作可以使用
		TParm result = INSMZConfirmTool.getInstance().selectSpcMemo(parm);
		if (result.getErrCode() < 0) {
			odoMainControl.err(result.getErrName() + "：" + result.getErrText());
			return;
		}
		if (result.getCount() <= 0) {
			odoMainControl.messageBox("此就诊病患非联网医保病人");
			return;
		}
		TParm spcParm = (TParm) odoMainControl.openDialog(
				URLINSSPCMEMODIAG, result.getData());
		if (null == spcParm || null == spcParm.getValue("SPECIAL_CASE")) {
			return;
		}
		parm.setData("SPC_MEMO", spcParm.getValue("SPECIAL_CASE"));
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("OPT_USER", Operator.getID());
		result = INSMZConfirmTool.getInstance().updateInsMZConfirmSpcMemo(parm);
		if (result.getErrCode() < 0) {
			odoMainControl.err(result.getErrName() + "：" + result.getErrText());
			odoMainControl.messageBox("E0005");
			return;
		}
		odoMainControl.messageBox("P0005");
	}

}
