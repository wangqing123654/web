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
 * Title: ����ҽ������վҽ�ƿ�����
 * </p>
 * 
 * <p>
 * Description:����ҽ������վҽ�ƿ�����
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
	
	public TParm ektReadParm;// ҽ�ƿ���������
	public TParm ektReadParmBefore;// ҽ�ƿ���������
	public boolean ektExeConcel = false;// ҽ�ƿ���������ȡ����ť�Ժ����
	public boolean isReadEKT = false;// �Ƿ��Ѷ�ȡ��ҽ�ƿ�
	public TParm ektOrderParmone;
	public TParm ektSumExeParm;
	
	private static final String EKTLABLETAG = "LBL_EKT_MESSAGE";
	private static final String EKTLABLENOCARDSTR = "δ����";
	private static final int EKT_TYPE_FLG_ENGH = 1;//1.��ʾ�������ξ����� 
	private static final int EKT_TYPE_FLG_NENGH = 2;//2.�ۿ������ʾ�ۿ����
	private static final String URLOPDORDERPREVIEWAMT = "%ROOT%\\config\\opd\\OPDOrderPreviewAmt.x";//2.�ۿ������ʾ�ۿ����
	private static final String URLOPDOrderPreviewAmtForPre = "%ROOT%\\config\\opd\\OPDOrderPreviewAmtForPre.x";//2.�ۿ������ʾ�ۿ����
	public TLabel ekt_lable;//yanjing 20130614 ҽ�ƿ�״̬��ǩ
	
	public boolean preDebtFlg = false;
	
	/**
	 * ҽ�ƿ���ʼ��
	 * @param odoMainControl
	 */
	public ODOMainEkt(OdoMainControl odoMainControl){
		this.odoMainControl = odoMainControl;
		preDebtFlg = EKTIO.getInstance().ektAyhSwitch();
	}
	
	/**
	 * �Ƿ����
	 * @return
	 * @throws Exception
	 */
	public boolean readEKT() throws Exception {
		if (null == ektReadParm) {
			ektReadParm = odoMainControl.pay.readCard();
			if (ektReadParm.getErrCode() < 0) {
				odoMainControl.messageBox("δȷ�����,����ɾ��ҽ��");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ҽ�ƿ����
	 * @throws Exception
	 */
	public void onEktClear() throws Exception{
		ektReadParm=null;
		ektExeConcel = false;// ҽ�ƿ���������ȡ����ť�Ժ����
		odoMainControl.setValue(EKTLABLETAG, EKTLABLENOCARDSTR);//====pangben 2013-3-19 ��ʼ������״̬
		ekt_lable.setForeground(OdoMainControl.RED);//======yanjing 2013-06-14���ö�����ɫ
	}
	
//	/**
//	 * �ж��Ƿ��Ѷ��� true �Ѷ���false δ��
//	 */
//	private boolean isReadEKT() {
//		return false;
//	}
	
	
	public void onInit() throws Exception{
		ekt_lable = (TLabel) odoMainControl.getComponent(EKTLABLETAG);//��ȡ��ʾҽ�ƿ�״̬��ǩ
		ekt_lable.setForeground(OdoMainControl.RED);//======yanjing 2013-06-14���ö�����ɫ
	}
	
	/**
	 * ̩������ҽ�ƿ��շ� ֻ�ܲ���ҽ�ƿ��շѲ��� 
	 * @throws Exception 
	 */
	public void onFee() throws Exception{
//		if (!PatTool.getInstance().isLockPat(odoMainControl.odoMainPat.pat.getMrNo())) {
//			odoMainControl.messageBox("�����Ѿ��������û�ռ��!");
//			return;
//		}
		if (null == odoMainControl.caseNo || odoMainControl.caseNo.length() <= 0)
			return;
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CASE_NO", odoMainControl.odoMainReg.reg.caseNo());
		// ��ô˴β���ҽ�ƿ����е�ҽ�� ��ִ��ɾ������ҽ��ʱʹ��
		TParm ektOrderParm = OrderTool.getInstance().selDataForOPBEKT(parm);
		odoMainControl.odoMainOpdOrder.onTempSave(ektOrderParm,0); //modigy by huangtt 20141118  0��ʾ����ӡ����ǩ
	}
	
	/**
	 * ҽ�ƿ� ɾ��ҽ������ ��ѡȡ����ť ִ�г���ɾ��ҽ������
	 * @throws Exception
	 */
	public void unTmpSave() throws Exception{
		if (ektExeConcel) {// ҽ�ƿ� ɾ��ҽ������ ��ѡȡ����ť ִ�г���ɾ��ҽ������
			TParm parm = new TParm();
			parm.setData("MR_NO", ektReadParm.getValue("MR_NO"));
			TParm regParm = OPDAbnormalRegTool.getInstance().selectRegForOPD(
					parm);
			for (int i = 0; i < regParm.getCount("CASE_NO"); i++) {
				if (regParm.getValue("CASE_NO", i).equals(odoMainControl.odoMainReg.reg.caseNo())) {
					// wc = "W"; // Ĭ��Ϊ��ҽ
					odoMainControl.odoMainReg.initOpd(regParm, 0);// ��ʼ��
					ektExeConcel = false;
					break;
				}
			}
		}
	}
	
	//˹�ʹ�
	/**
	 * ��ѯ���ﲡ�����
	 * ======pangben 2013-3-28
	 */
	public void onMrSearchFee() throws Exception{
		if (null == odoMainControl.caseNo || odoMainControl.caseNo.length() <= 0)
			return;
		if(!preDebtFlg){
			// �鿴�˾��ﲡ���Ƿ���ҽ�ƿ�����
			if(!readEKT()){
				return ;
			}
			ektReadParm.setData("CASE_NO",odoMainControl.caseNo);
			ektReadParm.setData("EKT_TYPE_FLG",EKT_TYPE_FLG_ENGH);//1.��ʾ�������ξ����� 2.�ۿ������ʾ�ۿ����
			TParm result = (TParm) odoMainControl.openDialog(URLOPDORDERPREVIEWAMT,// �������������ѯCASE_NO
					ektReadParm);
		}
		TParm result = EKTpreDebtTool.getInstance().getMasterAndFee(odoMainControl.odo);
		result = (TParm) odoMainControl.openDialog(URLOPDOrderPreviewAmtForPre,// �������������ѯCASE_NO
				result);
	}
	
	public boolean isReadedCard() throws Exception{
		if (null == ektReadParm || null == ektReadParm.getValue("MR_NO")
				|| ektReadParm.getValue("MR_NO").length() <= 0){
			odoMainControl.messageBox("�����");
			return true;
		}
		return false;
	}
	
}
