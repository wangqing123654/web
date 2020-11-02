package com.javahis.ui.testOpb;

import java.sql.Timestamp;

import com.dongyang.data.TParm;
import com.javahis.system.root.RootClientListener;
import com.javahis.util.DateUtil;
import com.javahis.util.OdiUtil;

import jdo.sys.Operator;
import jdo.sys.PATLockTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

/**
 * 补充计费病患
 * @author zhangp
 *
 */
public class OPBChargePat {

	public Pat pat;
	public OPBChargeControl control;
	
	private static final String MSG_NO_PAT = "查无此病案号";
	
	public final static String TAG_MR_NO = "MR_NO";
	private final static String TAG_PAT_NAME = "PAT_NAME";
	private final static String TAG_SEX_CODE = "SEX_CODE";
	public final static String TAG_AGE = "AGE";
	
	private final static String URL_SYSPATLCOKMESSAGE = "%ROOT%\\config\\sys\\SYSPatLcokMessage.x";
	
	public OPBChargePat(OPBChargeControl opbChargeControl){
		control = opbChargeControl;
	}
	
	/**
	 * 查询病患
	 */
	public void onQueryPat(String mrNo){
//		if (pat != null) {
//			this.unLockPat();
//		}
		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			control.messageBox_(MSG_NO_PAT);
			return;
		}
		/*String age = OdiUtil.showAge(pat.getBirthday(),
				SystemTool.getInstance().getDate());*/
		String age = patAge(pat.getBirthday());//modify by huangjw 20150106
		control.setValue(TAG_MR_NO, pat.getMrNo());
		control.setValue(TAG_PAT_NAME, pat.getName());
		control.setValue(TAG_SEX_CODE, pat.getSexCode());
		control.setValue(TAG_AGE, age);
	}
	
	/**
    * 计算年龄
    * @param date add by huangjw 20150106
    * @return
    */
   private String patAge(Timestamp date){
	   Timestamp sysDate = SystemTool.getInstance().getDate();
       Timestamp temp = date == null ? sysDate : date;
       String age = "0";
       age = DateUtil.showAge(temp, sysDate);
       return age;
   }
	
	/**
	 * 病患加锁
	 * 
	 * @return boolean true 成功 false 失败
	 */
	public boolean lockPat() {
		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
		TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
		// 判断是否加锁
		if (parm != null && parm.getCount() > 0) {
			if (isMyPat(parm)) {
				return true;
			}
			if (RootClientListener.getInstance().isClient()) {
				parm.setData("PRGID_U", "OPB");
				parm.setData("MR_NO", pat.getMrNo());
				String prgId = parm.getValue("PRG_ID", 0);
				if ("ODO".equals(prgId)) {
					parm.setData("WINDOW_ID", "OPD01");
				} else if ("ODE".equals(prgId)) {
					parm.setData("WINDOW_ID", "ERD01");
				} else if ("OPB".equals(prgId)) {
					parm.setData("WINDOW_ID", "OPB0101");
				}
				//斯巴达
				//else if ("ONW".equals(prgId))//====pangben 2013-5-14 添加护士站解锁管控:门诊
					//parm.setData("WINDOW_ID", "ONW01");
				//else if ("ENW".equals(prgId))//====pangben 2013-5-14 添加护士站解锁管控:急诊
					//parm.setData("WINDOW_ID", "ONWE");
				String flg = (String) control.openDialog(
						URL_SYSPATLCOKMESSAGE, parm);
				if ("UNLOCKING".equals(flg)) {
//					this.onQuery();
					return false;
				}
				if ("LOCKING".equals(flg)) {
					control.onClear();
					return false;
				}
				if ("OK".equals(flg)) {
					PatTool.getInstance().unLockPat(pat.getMrNo());
					PATLockTool.getInstance().log(
							"ODO->" + SystemTool.getInstance().getDate() + " "
									+ Operator.getID() + " "
									+ Operator.getName() + " 强制解锁[" + aa
									+ " 病案号：" + pat.getMrNo() + "]");
				} else {
					control.onClear();
					return false;
				}
			} else {
				if (control.messageBox("是否解锁", PatTool.getInstance()
						.getLockParmString(pat.getMrNo()), 0) == 0) {
					PatTool.getInstance().unLockPat(pat.getMrNo());
					PATLockTool.getInstance().log(
							"ODO->" + SystemTool.getInstance().getDate() + " "
									+ Operator.getID() + " "
									+ Operator.getName() + " 强制解锁[" + aa
									+ " 病案号：" + pat.getMrNo() + "]");
				} else {
					control.onClear();
					return false;
				}
			}
		}
		// 锁病患信息
		if (!PatTool.getInstance().lockPat(pat.getMrNo(), checklockPat())) {
			control.onClear();
			return false;
		}
		return true;
	}
	
	
	/**
	 * 病患解锁
	 */
	public void unLockPat() {
		if (pat == null) {
			return;
		}
		// 判断是否加锁
		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
			TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
			if (Operator.getIP().equals(parm.getValue("OPT_TERM", 0))
					&&Operator.getID().equals(parm.getValue("OPT_USER", 0))) {
				if ("OPB".equals(parm.getValue("PRG_ID", 0))
						||"ONW".equals(parm.getValue("PRG_ID", 0))
								||"ENW".equals(parm.getValue("PRG_ID", 0))) {
					PatTool.getInstance().unLockPat(pat.getMrNo());
				}
			}
		}
		pat = null;
	}
	
	/**
	 * 是否正在本人手中锁住病患
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	private boolean isMyPat(TParm parm) {
		if (!checklockPat().equals(parm.getValue("PRG_ID", 0))
				|| !(Operator.getIP().equals(parm.getValue("OPT_TERM", 0)))
				|| !(Operator.getID().equals(parm.getValue("OPT_USER", 0)))) {
			return false;
		}
		return true;
	}
	
	//斯巴达
	/**
	 * 护士站解锁功能
	 * @return
	 */
	private String checklockPat(){
		String type = "OPB";
//			if(null!=onwType && onwType.equals("O"))
//				type = "ONW";
//			else{
//				type = "ENW";
//			}
		return type;
	}
	
	public void onClear(){
		pat = null;
		control.clearValue(TAG_MR_NO);
		control.clearValue(TAG_PAT_NAME);
		control.clearValue(TAG_SEX_CODE);
		control.clearValue(TAG_AGE);
	}
	
}
