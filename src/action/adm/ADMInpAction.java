package action.adm;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.adm.ADMAutoBillTool;
import jdo.ibs.IBSNewTool;
import jdo.mem.MEMTool;
import jdo.sys.Operator;

/**
 * <p>Title:住院登记Action </p>
 *
 * <p>Description:住院登记Action </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMInpAction
    extends TAction {
    /**
     * 入院登记保存方法
     * @param parm TParm
     * @return TParm
     */
    public TParm insertADMData(TParm parm){
        TParm result = new TParm();
        TConnection conn = this.getConnection();
        result = ADMInpTool.getInstance().insertADMData(parm,conn);
        if(result.getErrCode()<0){
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    /**
     * 取消住院
     * @param parm TParm
     * @return TParm
     */
    public TParm ADMCanInp(TParm parm) {
        TParm result = new TParm();
        TConnection conn = getConnection();
        result = ADMTool.getInstance().ADM_CANCEL_INP(parm, conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    /**
     *读取参数
     * @param parm TParm
     * @return TParm
     */
    public TParm loadSysBed(TParm parm) {
        TParm result = new TParm();
        result.setData("CASE_NO", parm.getData("CASE_NO"));
        result.setData("SEQ_NO", 1);
        result.setData("IPD_NO", parm.getData("IPD_NO"));
        result.setData("MR_NO", parm.getData("MR_NO"));
        result.setData("CHG_DATE", parm.getData("DATE"));
        result.setData("PSF_KIND", "IN");
        result.setData("PSF_HOSP", "");
        result.setData("CANCEL_FLG", "N");
        result.setData("CANCEL_DATE", "");
        result.setData("CANCEL_USER", "");
        result.setData("DEPT_CODE", parm.getData("DEPT_CODE"));
        result.setData("STATION_CODE", parm.getData("STATION_CODE"));
        result.setData("BED_NO", parm.getData("BED_NO"));
        result.setData("VS_CODE_CODE", parm.getData("VS_DR_CODE"));
        result.setData("ATTEND_DR_CODE", "");
        result.setData("DIRECTOR_DR_CODE", "");
        result.setData("OPT_USER", parm.getData("OPT_USER"));
        result.setData("OPT_TERM", parm.getData("OPT_TERM"));
        return result;
    }

    /**
     * 住院登记修改
     * @param parm TParm
     * @return TParm
     */
	public TParm upDataAdmInp(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		//boolean LumpworkFlg = parm.getBoolean("LumpworkFlg");
		String caseNo = parm.getValue("CASE_NO");
		//=====pangben 2015-6-18 修改套餐操作，修改婴儿套餐
		String oldLumpworkCode=parm.getValue("OLD_LUMPWORK_CODE");
		result=updateAdmInpPackage(parm,oldLumpworkCode, caseNo, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		boolean newbabyFlg=false;
		if (null!=result.getData("newbabyFlg")&&result.getData("newbabyFlg").toString().length()>0) {
			newbabyFlg=result.getBoolean("newbabyFlg");
		}
		result = ADMTool.getInstance().updataAdmInp(parm, conn);
		if (result.getErrCode() < 0) {
			result=new TParm();
			result.setErr(-1,"E0005");
			conn.close();
			return result;
		}
		if (oldLumpworkCode.length() > 0
				|| parm.getValue("LUMPWORK_CODE").length() > 0) {// 操作套餐病人，修改套餐类型
			if (oldLumpworkCode.length() > 0
					&& oldLumpworkCode.equals(parm.getValue("LUMPWORK_CODE"))) {
				// 此次操作不修改套餐情况 不操作
			} else {
				result = ADMInpTool.getInstance().updlumpWorkBill(parm, conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
				conn.commit();
				if (null == oldLumpworkCode || oldLumpworkCode.length() <= 0) {// 原来无套餐情况不执行差异金额修改
				} else {
					// 住院登记去掉套餐，将差异金额医嘱作废
					result = IBSNewTool.getInstance().onReLumpWorkIbsOrddOrder(parm, conn);
					if (result.getErrCode() < 0) {
						conn.rollback();
						conn.close();
						return result;
					}
				}
			}
		}
		conn.commit();
		conn.close();
		result.setData("newbabyFlg",newbabyFlg);
		return result;
	}
	/**
	 * 
	* @Title: updateAdmInpPackage
	* @Description: TODO(住院登记修改套餐类型)
	* @author pangben 2015-7-30
	* @param parm
	* @param caseNo
	* @param conn
	* @return
	* @throws
	 */
	private TParm updateAdmInpPackage(TParm parm,String oldLumpworkCode,String caseNo,TConnection conn){
		boolean newbabyFlg = false;
		TParm result=new TParm();
		if (oldLumpworkCode.length()>0 && "".equals(parm.getValue("LUMPWORK_CODE"))) {// 修改前有套餐，修改后无套餐校验
//			String sql = "SELECT SUM(TOT_AMT) TOT_AMT FROM  IBS_ORDD WHERE CASE_NO ='"
//					+ caseNo + "' AND INCLUDE_FLG='Y'";
//			result = new TParm(TJDODBTool.getInstance().select(sql));
//			if (result.getCount() > 0 && result.getDouble("TOT_AMT", 0) != 0) {
//				result.setErr(-1, "此病患存在套餐外医嘱,不可以修改套餐类型");
//				return result;
//			}
			String sql = "UPDATE ADM_INP SET LUMPWORK_CODE ='' WHERE CASE_NO='"
					+ caseNo + "'";
			result = new TParm(TJDODBTool.getInstance().update(sql, conn));
			if (result.getErrCode() < 0) {
				result.setErr(-1,"E0005");
				return result;
			}
			sql = "SELECT CASE_NO,MR_NO,NEW_BORN_FLG FROM ADM_INP WHERE M_CASE_NO ='"
					+ caseNo + "' AND NEW_BORN_FLG='Y'";
			TParm admDateParm = new TParm(TJDODBTool.getInstance().select(sql));// 查询婴儿数据
			if (admDateParm.getCount() > 0) {// 存在婴儿数据
				newbabyFlg=true;
				for (int i = 0; i < admDateParm.getCount(); i++) {
//					sql = "SELECT SUM(TOT_AMT) TOT_AMT FROM  IBS_ORDD WHERE CASE_NO ='"
//							+ admDateParm.getValue("CASE_NO", i)
//							+ "' AND INCLUDE_FLG='Y'";
//					result = new TParm(TJDODBTool.getInstance().select(sql));
//					if (result.getCount() > 0
//							&& result.getDouble("TOT_AMT", 0) != 0) {
//						result.setErr(-1, "此病患婴儿存在套餐外医嘱,不可以修改套餐类型");
//						return result;
//					}
					sql = "UPDATE ADM_INP SET LUMPWORK_CODE ='' WHERE CASE_NO='"
							+ admDateParm.getValue("CASE_NO", i) + "'";
					result = new TParm(TJDODBTool.getInstance().update(sql,
							conn));
					if (result.getErrCode() < 0) {
						result.setErr(-1,"E0005");
						return result;
					}
				}
			}
			//将当前已经使用的套餐改成未使用状态
			if (parm.getParm("parmOldL") != null
					&& (parm.getParm("parmOldL").getValue("PACKAGE_CODE").length() > 0)) {
				TParm memParm = parm.getParm("parmOldL");
				memParm.setData("USED_FLG","0");
				memParm.setData("CASE_NO","");
				memParm.setData("OPT_USER",parm.getValue("OPT_USER"));
				memParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
				memParm.setData("MR_NO",parm.getValue("MR_NO"));
				result = MEMTool.getInstance().upMemUsedFlg(memParm, conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
			}
		}else if (oldLumpworkCode.length()<=0 && !"".equals(parm.getValue("LUMPWORK_CODE"))
				||oldLumpworkCode.length()>0 &&
				!oldLumpworkCode.equals(parm.getValue("LUMPWORK_CODE"))) {// 1.修改前无套餐，修改后有套餐 2.修改套餐，套餐不相同
//			String sql = "SELECT SUM(TOT_AMT) TOT_AMT FROM  IBS_ORDD WHERE CASE_NO ='"
//					+ caseNo + "' AND INCLUDE_FLG='Y'";
//			result = new TParm(TJDODBTool.getInstance().select(sql));
//			if (result.getCount() > 0 && result.getDouble("TOT_AMT", 0) != 0) {
//				result.setErr(-1, "此病患存在套餐外医嘱,不可以修改套餐类型");
//				return result;
//			}
			String sql = "UPDATE ADM_INP SET LUMPWORK_CODE ='"
					+ parm.getValue("LUMPWORK_CODE") + "',LUMPWORK_RATE="+parm.getDouble("LUMPWORK_RATE")+" WHERE CASE_NO='" + caseNo
					+ "'";
			result = new TParm(TJDODBTool.getInstance().update(sql, conn));
			if (result.getErrCode() < 0) {
				result.setErr(-1,"E0005");
				return result;
			}
			sql = "SELECT CASE_NO,MR_NO,NEW_BORN_FLG FROM ADM_INP WHERE M_CASE_NO ='"
					+ caseNo + "' AND NEW_BORN_FLG='Y'";
			TParm admDateParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (admDateParm.getCount() > 0) {//存在婴儿数据
				newbabyFlg=true;
				for (int i = 0; i < admDateParm.getCount(); i++) {
//					sql = "SELECT SUM(TOT_AMT) TOT_AMT FROM  IBS_ORDD WHERE CASE_NO ='"
//						+ admDateParm.getValue("CASE_NO", i)
//						+ "' AND INCLUDE_FLG='Y'";
//					result = new TParm(TJDODBTool.getInstance().select(sql));
//					if (result.getCount() > 0
//							&& result.getDouble("TOT_AMT", 0) != 0) {
//						result.setErr(-1, "此病患婴儿存在套餐外医嘱,不可以修改套餐类型");
//						return result;
//					}
					sql = "UPDATE ADM_INP SET LUMPWORK_CODE ='"
							+ parm.getValue("LUMPWORK_CODE") + "',LUMPWORK_RATE="+parm.getDouble("LUMPWORK_RATE")+" WHERE CASE_NO='"
							+ admDateParm.getValue("CASE_NO", i) + "'";
					result = new TParm(TJDODBTool.getInstance().update(sql,
							conn));
					if (result.getErrCode() < 0) {
						result.setErr(-1,"E0005");
						return result;
					}
				}
			}
			//将当前已经使用的套餐改成未使用状态
			if (parm.getParm("parmOldL") != null
					&& (parm.getParm("parmOldL").getValue("PACKAGE_CODE").length() > 0)) {//使用状态的套餐改成未使用
				TParm memParm = parm.getParm("parmOldL");
				memParm.setData("USED_FLG","0");
				memParm.setData("CASE_NO","");
				memParm.setData("OPT_USER",parm.getValue("OPT_USER"));
				memParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
				memParm.setData("MR_NO",parm.getValue("MR_NO"));
				result = MEMTool.getInstance().upMemUsedFlg(memParm, conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
			}
			if (parm.getParm("parmL") != null
					&& (parm.getParm("parmL").getValue("PACKAGE_CODE").length() > 0)) {//更新的套餐类型修改状态
				TParm memParm = parm.getParm("parmL");
				memParm.setData("OPT_USER",parm.getValue("OPT_USER"));
				memParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
				result = MEMTool.getInstance().upMemUsedFlg(memParm, conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
			}
		}
		result.setData("newbabyFlg",newbabyFlg);
		return result;
	}
    /**
     * 固定费用自动过账
     * @param parm TParm
     * @return TParm
     */
    public TParm postAutoBill(TParm parm){
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = ADMAutoBillTool.getInstance().postAutoBill(parm,conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    /**
     * 固定费用自动过账（以个人为单位补帐）
     * @param parm TParm
     * @return TParm
     */
    public TParm postAutoBillOfMen(TParm parm){
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = ADMAutoBillTool.getInstance().postAutoBillOfMen(parm,conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 出院患者召回
     * @param parm TParm  需要参数：CASE_NO;MR_NO;BED_NO;OPT_USER;OPT_TERM;
     * @return TParm
     */
    public TParm returnAdm(TParm parm){
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = ADMInpTool.getInstance().returnAdm(parm,conn);
        if (result.getErrCode() < 0) {
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
}
