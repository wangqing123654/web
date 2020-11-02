package com.javahis.ui.odo;


import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;
import jdo.odo.OPDAbnormalRegTool;
import jdo.opd.OrderTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;

import com.dongyang.data.TParm;
import com.dongyang.ui.TLabel;

/**
 * 
 * <p>
 * 
 * Title: 门诊医生工作站医疗卡对象
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站医疗卡对象
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODOMainEkt {
	
	public OdoMainControl odoMainControl;
	
	public TParm ektReadParm;// 医疗卡读卡数据
	public TParm ektReadParmBefore;// 医疗卡读卡数据
	public boolean ektExeConcel = false;// 医疗卡弹出界面取消按钮以后操作
	public boolean isReadEKT = false;// 是否已读取过医疗卡
	public TParm ektOrderParmone;
	public TParm ektSumExeParm;
	
	private static final String EKTLABLETAG = "LBL_EKT_MESSAGE";
	private static final String EKTLABLENOCARDSTR = "未读卡";
	private static final int EKT_TYPE_FLG_ENGH = 1;//1.显示病患本次就诊金额 
	private static final int EKT_TYPE_FLG_NENGH = 2;//2.扣款金额不足显示扣款不足金额
	private static final String URLOPDORDERPREVIEWAMT = "%ROOT%\\config\\opd\\OPDOrderPreviewAmt.x";//2.扣款金额不足显示扣款不足金额
	private static final String URLOPDOrderPreviewAmtForPre = "%ROOT%\\config\\opd\\OPDOrderPreviewAmtForPre.x";//2.扣款金额不足显示扣款不足金额
	public TLabel ekt_lable;//yanjing 20130614 医疗卡状态标签
	
	public boolean preDebtFlg = false;
	
	/**
	 * 医疗卡初始化
	 * @param odoMainControl
	 */
	public ODOMainEkt(OdoMainControl odoMainControl){
		this.odoMainControl = odoMainControl;
		preDebtFlg = EKTIO.getInstance().ektAyhSwitch();
	}
	
	/**
	 * 是否读卡
	 * @return
	 * @throws Exception
	 */
	public boolean readEKT() throws Exception {
		if (null == ektReadParm) {
			ektReadParm = odoMainControl.pay.readCard();
			if (ektReadParm.getErrCode() < 0) {
				odoMainControl.messageBox("未确认身份,不可删除医嘱");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 医疗卡清空
	 * @throws Exception
	 */
	public void onEktClear() throws Exception{
		ektReadParm=null;
		ektExeConcel = false;// 医疗卡弹出界面取消按钮以后操作
		odoMainControl.setValue(EKTLABLETAG, EKTLABLENOCARDSTR);//====pangben 2013-3-19 初始化读卡状态
		ekt_lable.setForeground(OdoMainControl.RED);//======yanjing 2013-06-14设置读卡颜色
	}
	
//	/**
//	 * 判断是否已读卡 true 已读，false 未读
//	 */
//	private boolean isReadEKT() {
//		return false;
//	}
	
	
	public void onInit() throws Exception{
		ekt_lable = (TLabel) odoMainControl.getComponent(EKTLABLETAG);//获取显示医疗卡状态标签
		ekt_lable.setForeground(OdoMainControl.RED);//======yanjing 2013-06-14设置读卡颜色
	}
	
	/**
	 * 泰心门诊医疗卡收费 只能操作医疗卡收费操作 
	 * @throws Exception 
	 */
	public void onFee() throws Exception{
//		if (!PatTool.getInstance().isLockPat(odoMainControl.odoMainPat.pat.getMrNo())) {
//			odoMainControl.messageBox("病患已经被其他用户占用!");
//			return;
//		}
		if (null == odoMainControl.caseNo || odoMainControl.caseNo.length() <= 0)
			return;
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CASE_NO", odoMainControl.odoMainReg.reg.caseNo());
		// 获得此次操作医疗卡所有的医嘱 在执行删除所有医嘱时使用
		TParm ektOrderParm = OrderTool.getInstance().selDataForOPBEKT(parm);
		odoMainControl.odoMainOpdOrder.onTempSave(ektOrderParm,0); //modigy by huangtt 20141118  0表示不打印处方签
	}
	
	/**
	 * 医疗卡 删除医嘱操作 点选取消按钮 执行撤销删除医嘱操作
	 * @throws Exception
	 */
	public void unTmpSave() throws Exception{
		if (ektExeConcel) {// 医疗卡 删除医嘱操作 点选取消按钮 执行撤销删除医嘱操作
			TParm parm = new TParm();
			parm.setData("MR_NO", ektReadParm.getValue("MR_NO"));
			TParm regParm = OPDAbnormalRegTool.getInstance().selectRegForOPD(
					parm);
			for (int i = 0; i < regParm.getCount("CASE_NO"); i++) {
				if (regParm.getValue("CASE_NO", i).equals(odoMainControl.odoMainReg.reg.caseNo())) {
					// wc = "W"; // 默认为西医
					odoMainControl.odoMainReg.initOpd(regParm, 0);// 初始化
					ektExeConcel = false;
					break;
				}
			}
		}
	}
	
	//斯巴达
	/**
	 * 查询就诊病患金额
	 * ======pangben 2013-3-28
	 */
	public void onMrSearchFee() throws Exception{
		if (null == odoMainControl.caseNo || odoMainControl.caseNo.length() <= 0)
			return;
		if(!preDebtFlg){
			// 查看此就诊病患是否是医疗卡操作
			if(!readEKT()){
				return ;
			}
			ektReadParm.setData("CASE_NO",odoMainControl.caseNo);
			ektReadParm.setData("EKT_TYPE_FLG",EKT_TYPE_FLG_ENGH);//1.显示病患本次就诊金额 2.扣款金额不足显示扣款不足金额
			TParm result = (TParm) odoMainControl.openDialog(URLOPDORDERPREVIEWAMT,// 调用其他窗体查询CASE_NO
					ektReadParm);
		}
		TParm result = EKTpreDebtTool.getInstance().getMasterAndFee(odoMainControl.odo);
		result = (TParm) odoMainControl.openDialog(URLOPDOrderPreviewAmtForPre,// 调用其他窗体查询CASE_NO
				result);
	}
	
	public boolean isReadedCard() throws Exception{
		if (null == ektReadParm || null == ektReadParm.getValue("MR_NO")
				|| ektReadParm.getValue("MR_NO").length() <= 0){
			odoMainControl.messageBox("请读卡");
			return true;
		}
		return false;
	}
	
}
