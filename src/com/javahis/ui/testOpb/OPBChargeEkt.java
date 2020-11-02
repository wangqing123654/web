package com.javahis.ui.testOpb;

import java.util.List;

import jdo.ekt.EKTIO;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.ui.testOpb.bean.OpdOrder;

/**
 * 补充计费医疗卡
 * @author zhangp
 *
 */
public class OPBChargeEkt {

	public OPBChargeControl control;
	
	public TParm ektReadParm;// 医疗卡读卡数据
	
	private static final String TAG_TOT_MASTER = "TOT_MASTER";
	private static final String TAG_MASTER = "MASTER";
	private static final String TAG_ACCOUNT = "ACCOUNT";
	private static final String TAG_NO_ACCOUNT = "NO_ACCOUNT";
	private static final String TAG_TOT_ACCOUNT = "TOT_ACCOUNT";
	
	public OPBChargeEkt(OPBChargeControl opbChargeControl) {
		this.control = opbChargeControl;
	}
	
	/**
	 * 读卡
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public boolean readCard() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		ektReadParm = EKTIO.getInstance().TXreadEKT();
		if (null == ektReadParm || ektReadParm.getErrCode() < 0
				|| ektReadParm.getValue("MR_NO").length() <= 0) {
			control.messageBox(ektReadParm.getErrText());
			return true;
		}
		control.setValue(TAG_TOT_MASTER, ektReadParm.getDouble("CURRENT_BALANCE"));
		control.pay = new PayEkt();
		return false;
	}
	
	/**
	 * 根据病案号查询医疗卡信息
	 * @param mrNo
	 * @return
	 */
	public boolean onQueryEkt(String mrNo){
		String sql="SELECT B.SEX_CODE, C.CURRENT_BALANCE, A.EKT_CARD_NO AS CARD_NO," +
				" A.CARD_NO AS PK_CARD_NO, A.MR_NO, A.CARD_SEQ AS SEQ, A.BANK_CARD_NO," +
				" B.PAT_NAME, B.IDNO, B.BIRTH_DATE, A.CARD_TYPE" +
				" FROM EKT_ISSUELOG A, SYS_PATINFO B, EKT_MASTER C" +
				" WHERE A.MR_NO = '"+mrNo+"'" +
				" AND A.MR_NO = B.MR_NO" +
				" AND A.CARD_NO = C.CARD_NO" +
				" AND WRITE_FLG = 'Y'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		ektReadParm = parm.getRow(0);
		if (null == ektReadParm || ektReadParm.getErrCode() < 0
				|| ektReadParm.getValue("MR_NO").length() <= 0) {
			return true;
		}
		control.setValue(TAG_TOT_MASTER, ektReadParm.getDouble("CURRENT_BALANCE"));
		control.pay = new PayEkt();
		return false;
	}
	
	/**
	 * 清空
	 */
	public void onClear(){
		ektReadParm = null;
		control.clearValue(TAG_TOT_MASTER);
		control.clearValue(TAG_MASTER);
		control.clearValue(TAG_ACCOUNT);
		control.clearValue(TAG_NO_ACCOUNT);
		control.clearValue(TAG_TOT_ACCOUNT);
	}
	
	public void setMaster(List<OpdOrder> list){
		control.setValue(TAG_MASTER, control.pay.getMasterForOpb(ektReadParm
				.getDouble("CURRENT_BALANCE"), list));
		
		//ADD BY HUANGTT 20141021
		control.setValue(TAG_ACCOUNT, control.pay.getAccountForOpb(list));
		control.setValue(TAG_NO_ACCOUNT, control.pay.getNoAccountForOpb(list));
		control.setValue(TAG_TOT_ACCOUNT, control.pay.getTotAccountForOpb(list));
		
	}
	
}
