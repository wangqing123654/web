package jdo.adm;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.db.TConnection;
import jdo.sys.SYSBedTool;

/**
 * <p>Title:预约住院 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMResvTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static ADMResvTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static ADMResvTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMResvTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ADMResvTool() {
        setModuleName("adm\\ADMResvModule.x");
        onInit();
    }

    /**
     * 查询adm_resv全字段
     * @param parm TParm
     * @return TParm
     */
    public TParm selectAll(TParm parm) {
        TParm result = new TParm();
        result = query("selectall", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询adm_resv全字段多次预约住院使用
     * @param parm TParm
     * @return TParm
     * ======pangben 2014-7-30 
     */
    public TParm selectallByPatInfo(TParm parm) {
        TParm result = new TParm();
        result = query("selectallByPatInfo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询某一病患未入住的 预约信息
     * @param MR_NO String
     * @return TParm
     */
    public TParm selectNotIn(String MR_NO){
        TParm parm = new TParm();
        parm.setData("MR_NO",MR_NO);
        TParm result = this.query("selectNotIn",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增预约住院表
     * @param  String
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 修改病患信息
     * @param parm TParm
     * @return TParm
     */
    public TParm upDate(TParm parm, TConnection conn) {
        TParm result = update("update", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
    /**
     * 修改预约住院包干套餐就诊号码
     * @param parm
     * @param conn
     * @return
     * =============pangben 2014-7-30
     */
    public TParm queryLumpworkCaseNoByResvNo(TParm parm){
    	 TParm result = query("queryLumpworkCaseNoByResvNo", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
    }
    /**
     * 住院登记时更新adm_resv字段
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm upDateForInp(TParm parm, TConnection conn) {
        TParm result = update("updateForInp", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;

    }
    //===========================   chenxi modify  20130311
    public TParm upDateSysBed(TParm parm, TConnection conn) {
        TParm result = update("upDateSysBed", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;

    }
    //=========================    chenxi modify 20130311

    /**
     * 取消预约
     * @param parm TParm
     * @return TParm
     */
    public TParm updateForAdmCanResv(TParm parm, TConnection conn) {
        TParm result = update("updateForCanResv", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 预约住院，通知
     * @return TParm
     */
    public TParm updataResvNotify(TParm parm) {
        TParm result = update("updataResvNotify", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 住院登记时更新预约信息包干套餐使用
     * @param parm
     * @param conn
     * @return
     *  pangben 2014-7-31
     */
    public TParm updateAdmResvLumpworkCaseNo(TParm parm,TConnection conn) {
        TParm result = update("updateAdmResvLumpworkCaseNo", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 取消预约
     * 1,修改adm_resv 取消人员，取消状态，取消时间 字段
     * 2,清除取消床位
     * @return TParm
     */
    public TParm cancelResv(TParm parm,TConnection conn){
        TParm bed = new TParm();
        bed.setData("APPT_FLG","N");
        bed.setData("OPT_USER",parm.getValue("OPT_USER"));
        bed.setData("OPT_TERM",parm.getValue("OPT_TERM"));
        bed.setData("BED_NO",parm.getValue("BED_NO"));
        TParm result = new TParm();
        result = ADMResvTool.getInstance().updateForAdmCanResv(parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = SYSBedTool.getInstance().upDateForResv(bed,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        if (parm.getValue("LUMPWORK_CASE_NO").length()>0) {//pangben 2014-8-1包干套餐已经充值操作
        	result = ADMInpTool.getInstance().deleteAdmInpbyCaseNo(parm.getValue("LUMPWORK_CASE_NO"), conn);
        	if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;
    }
    /**
     * 查询预约入住的科室，病区，医师(打印住院证使用)
     * @param parm TParm
     * @return TParm
     */
    public TParm selectFroPrint(TParm parm){
        TParm result = this.query("selectFroPrint",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 修改资格确认书编号
     * @param parm
     * @param conn
     * @return
     */
    public TParm updateResvConfirmNo(TParm parm , TConnection conn){
    	 TParm result = update("updateResvConfirmNo", parm,conn);
    	 if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
    	 return result;
    }
    /**
	   * 查询未结案数据
	   * @param parm
	   * @return
	   */
	  public TParm queryResvNClose(TParm parm){
		  TParm result = query("queryResvNClose", parm);
	      return result;
	  }
	  /**
	   * 查询跨年度数据
	   * @return
	   */
	  public TParm overYearNHIPatInfo(TParm parm){
		  TParm result = query("overYearNHIPatInfo", parm);
	      return result;
	  }
	  
	/**
	 * 查询预约住院病患统计
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm queryADMResvStatistics(TParm parm) {// add by wanglong 20120921
		TParm result = this.query("selectADMResvStatistics", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 查询预约住院病患统计预约
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm queryADMResvStatistics0(TParm parm) {// add by wanglong 20120921
		TParm result = this.query("selectADMResvStatistics0", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 查询预约住院病患统计取消
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm queryADMResvStatistics1(TParm parm) {// add by wanglong 20120921
		TParm result = this.query("selectADMResvStatistics1", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 查询预约住院病患统计入院
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm queryADMResvStatistics2(TParm parm) {// add by wanglong 20120921
		TParm result = this.query("selectADMResvStatistics2", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 查询主诊断数据
	 * @param parm TParm
	 * @return TParm
	 * @author wangbin 2015/1/7
	 */
	public TParm queryDiagnosisInfo(TParm parm) {
		String sql = "SELECT ICD_TYPE, ICD_CODE, ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE = '"
				+ parm.getValue("ICD_CODE") + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}

		return result;
	}
}
