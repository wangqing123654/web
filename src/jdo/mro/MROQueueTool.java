package jdo.mro;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: 病案出入库管理</p>
 *
 * <p>Description: 病案出入库管理</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-12
 * @version 1.0
 */
public class MROQueueTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static MROQueueTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MROQueueTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROQueueTool();
        return instanceObject;
    }

    public MROQueueTool() {
        this.setModuleName("mro\\MROQueueModule.x");
        this.onInit();
    }

    /**
     * 查询待出库病案
     * @return TParm
     */
    public TParm queryQueue(TParm parm) {
        TParm result = this.query("selectOut", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 修改Queue表 病历出入库状态
     * @param parm TParm
     * @return TParm
     */
    public TParm updateISSUE(TParm parm, TConnection conn) {
    	TParm result = new TParm();
    	if (null == conn) {
    		result = this.update("updateOut", parm);
    	} else {
    		if (StringUtils.equals(parm.getValue("TYPE"), "IN")) {
    			result = this.update("updateIn", parm, conn);
    		} else {
    			result = this.update("updateOut", parm, conn);
    		}
    	}
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 修改病历主表 病历在库状态
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateIn_flg(TParm parm, TConnection conn) {
        TParm result = this.update("updateIn_flg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 病历出入库 方法
     * @param parm TParm
     * @return TParm
     */
    public TParm updateOUT(TParm parm, TConnection conn) {
        TParm p;
        TParm result = new TParm();
        if (parm.getData("MRV") != null) {
            p = parm.getParm("MRV");
            result = this.updateIn_flg(p, conn);

            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        else{
            result.setErr(-1,"缺少MRV表参数");
            return result;
        }
        if (parm.getData("Queue") != null) {
            p = parm.getParm("Queue");
            result = this.updateISSUE(p, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }else{
            result.setErr(-1,"缺少Queue表参数");
            return result;
        }
        if (parm.getData("Tranhis") != null) {
            p = parm.getParm("Tranhis");
            result = this.insertTRANHIS(p, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            return result;
        }else{
            result.setErr(-1,"缺少Tranhis表参数");
            return result;
        }

    }

    /**
     * 插入病历借阅历史记录表
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertTRANHIS(TParm parm, TConnection conn) {
        TParm result = this.update("insertTRANHIS", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 病历借阅历史记录表 查询
     * @param parm TParm
     * @return TParm
     */
    public TParm selectTRANHIS(TParm parm){
        TParm result = this.query("selectTRANHIS",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 病历待入库信息查询
     * @return TParm
     */
    public TParm selectIn(TParm parm){
        TParm result = this.query("selectIn",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询出院病历归档（页签3使用）
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOutHp(TParm parm){
        TParm result = this.query("selectOutHp",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询出库病历信息（页签3使用）
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOutQueue(TParm parm){
        TParm result = this.query("selectOutQueue",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 插入病历主档
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertMRO_MRV(TParm parm){
        TParm result = this.update("insertMRO_MRV",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 根据CASE_NO取消待出库病历(住院登记取消时使用)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm cancelQueueByCASE_NO(TParm parm,TConnection conn){
        TParm result = this.update("cancelQueueByCASE_NO",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询病历主档
     * @param parm TParm
     * @return TParm
     */
    public TParm selectMRO_MRV(TParm parm){
        TParm result = this.query("selectMRO_MRV",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 检查是否已经存在病历主档
     * @param MR_NO String
     * @return boolean 返回结果：true：该病案号的病历主档已存在存在  false：该病案号的病历主档不存在
     */
    public boolean checkHasMRO_MRV(String MR_NO){
        TParm parm = new TParm();
        parm.setData("MR_NO",MR_NO);
        TParm result = this.selectMRO_MRV(parm);
        boolean flg = false;
        if(result.getCount()>0){
            flg = true;
        }
        return flg;
    }

    /**
     * 查询病历是否归档
     * @param parm TParm
     * @return TParm
     */
	public TParm selectMRO_MRV_TECH(TParm parm) {
		TParm result = this.query("selectMRO_MRV_TECH", parm);
		if(result.getErrCode() < 0){
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			return result;
		}
		return result;
	}
	
    /**
     * 查询出入库病历
     * @return TParm
     * @author wangbin 20140821
     */
    public TParm selectMroOutIn(TParm parm) {
        TParm result = this.query("selectMroOutIn", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询出入库病历详情
     * @return TParm
     * @author wangbin 20140822
     */
    public TParm selectMroOutInDetail(TParm parm) {
    	TParm result = new TParm();
    	
		// 病历出入库详情
		result = this.query("selectMroOutInDetail", parm);
        
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
	/**
	 * 病历出入库 方法
	 * 
	 * @param parm TParm
	 * @param TConnection conn
	 * @return TParm
	 * @author wangbin 20140822
	 */
	public TParm updateMroOutIn(TParm parm, TConnection conn) {
		TParm p;
		TParm result = new TParm();
		// 修改病案主档表的在库状态
		if (parm.getData("MRV") != null) {
			p = parm.getParm("MRV");
			result = this.updateMroMrvInfo(p, conn);

			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		} else {
			result.setErr(-1, "缺少MRV表参数");
			return result;
		}
		
		// 修改病案借阅表的出库状态
		if (parm.getData("Queue") != null) {
			p = parm.getParm("Queue");
			result = this.updateISSUE(p, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		} else {
			result.setErr(-1, "缺少Queue表参数");
			return result;
		}

		return result;
	}
	
    /**
     * 查询新建归档数据
     * @return TParm
     * @author wangbin 20140826
     */
    public TParm selectMroNew(TParm parm) {
        TParm result = this.query("selectMroNew", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询新建归档数据(用于打印)
     * @return TParm
     * @author wangbin 20140828
     */
    public TParm selectMroNewToPrint(TParm parm) {
        TParm result = this.query("selectMroNewToPrint", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 取得最大的案卷号
     * @return TParm
     * @author wangbin 20140826
     */
    public TParm selectMaxBoxCode(TParm parm) {
		String sql = "SELECT MAX(BOX_CODE) AS MAX_BOX_CODE FROM MRO_MRV WHERE BOX_CODE IS NOT NULL AND ADM_TYPE = '"
				+ parm.getValue("ADM_TYPE") + "'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 新建病历归档分配案卷号
     * @return TParm
     * @author wangbin 20140826
     */
    public TParm updateMroMrvBoxCode(TParm parm) {
        TParm result = this.update("updateMroMrv", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 清空客服建档状态标记
     * @return TParm
     * @author wangbin 20140826
     */
    public TParm updateSysPatInfoBookBuild(TParm parm) {
        TParm result = this.update("updateSysPatInfoBookBuild", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
	/**
	 * 回更住院病案借阅表的归还时间
	 * 
	 * @param parm TParm
	 * @return TParm
	 * @author wangbin 20140909
	 */
	public TParm updateMroQueueRntDate(TParm parm) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MRO_QUEUE SET OPT_USER = '");
		sql.append(parm.getValue("OPT_USER"));
		sql.append("',OPT_DATE=SYSDATE");
		sql.append(",OPT_TERM='");
		sql.append(parm.getValue("OPT_TERM"));
		sql.append("',RTN_DATE=TO_DATE('");
		sql.append(parm.getValue("RTN_DATE"));
		sql.append("', 'YYYY/MM/DD')");
		sql.append(" WHERE CASE_NO='");
		sql.append(parm.getValue("CASE_NO"));
		sql.append("'");
		
		TParm result = new TParm(TJDODBTool.getInstance().update(sql.toString()));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
    /**
     * 查询用于打印病案夹的数据
     * @return TParm
     * @author wangbin 20140911
     */
    public TParm selectMroPrintData(TParm parm) {
        TParm result = this.query("selectMroPrintData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
	/**
	 * 借阅病历页面的诊区病区
	 * 
	 * @param parm TParm
	 * @return TParm
	 * @author wangbin 20140919
	 */
	public TParm selectMroLendArea() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM (SELECT T.CLINICAREA_CODE AS ID,T.CLINIC_DESC AS NAME, T.PY1 FROM REG_CLINICAREA T ORDER BY T.CLINICAREA_CODE)");
		sql.append(" UNION ALL ");
		sql.append("SELECT * FROM (SELECT T.STATION_CODE AS ID, T.STATION_DESC AS NAME, T.PY1 FROM SYS_STATION T ORDER BY T.STATION_CODE)");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 取消出库
	 * 
	 * @param parm TParm
	 * @return TParm
	 * @author wangbin 20141011
	 */
	public TParm deleteMroOut(TParm parm) {
		String sql = "UPDATE MRO_QUEUE T SET T.CAN_FLG = 'Y',T.ISSUE_CODE = '0' WHERE T.QUE_SEQ = '" + parm.getValue("QUE_SEQ") + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 查询借阅信息用于验证当前登记是否与已存在的借阅数据冲突
	 * 
	 * @param parm TParm
	 * @return TParm
	 * @author wangbin 20141014
	 */
	public TParm selectMroQueueInfo(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.QUE_SEQ,A.MR_PERSON,A.QUE_DATE,A.RTN_DATE,A.ISSUE_CODE,A.LEND_CODE,B.PRIORITY,C.IN_FLG ");
		sbSql.append(" FROM MRO_QUEUE A, MRO_LEND B, MRO_MRV C ");
		sbSql.append(" WHERE A.LEND_CODE = B.LEND_CODE AND A.MR_NO = C.MR_NO ");
		sbSql.append(" AND A.ADM_TYPE = C.ADM_TYPE AND A.LEND_BOX_CODE = C.BOX_CODE AND A.LEND_BOOK_NO = C.BOOK_NO ");
		sbSql.append(" AND A.ADM_TYPE = '");
		sbSql.append(parm.getValue("ADM_TYPE"));
		sbSql.append("' AND A.ISSUE_CODE <> '");
		sbSql.append(parm.getValue("ISSUE_CODE"));
		sbSql.append("' AND A.MR_NO = '");
		sbSql.append(parm.getValue("MR_NO"));
		sbSql.append("' AND A.LEND_BOX_CODE = '");
		sbSql.append(parm.getValue("BOX_CODE"));
		sbSql.append("' AND A.LEND_BOOK_NO = '");
		sbSql.append(parm.getValue("BOOK_NO"));
		sbSql.append("' AND A.CAN_FLG = 'N' ");
		if (StringUtils.isNotEmpty(parm.getValue("QUE_DATE"))) {
			sbSql.append(" AND A.QUE_DATE = TO_DATE('");
			sbSql.append(parm.getValue("QUE_DATE"));
			sbSql.append("', 'YYYY/MM/DD')");
		}
		sbSql.append(" ORDER BY A.QUE_DATE");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 针对住院出库数据中已出库后再取消住院的数据进行归还日期设定
	 * 
	 * @param parm TParm
	 * @return TParm
	 * @author wangbin 20141017
	 */
	public TParm updateRtnDateByCaseNo(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("UPDATE MRO_QUEUE SET RTN_DATE = TO_DATE(TO_CHAR(SYSDATE, 'YYYY/MM/DD'), 'YYYY/MM/DD')");
		sbSql.append(" WHERE ISSUE_CODE = '1' AND CASE_NO = '");
		sbSql.append(parm.getValue("CASE_NO"));
		sbSql.append("'");
		
		TParm result = new TParm(TJDODBTool.getInstance().update(sbSql.toString()));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
    /**
     * 更新病历主档表的在库状态
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * @author wangbin 2014/10/22
     */
    public TParm updateMroMrvInfo(TParm parm, TConnection conn) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("UPDATE MRO_MRV SET IN_FLG='");
		sbSql.append(parm.getValue("IN_FLG"));
		sbSql.append("',CURT_LEND_DEPT_CODE='");
		sbSql.append(parm.getValue("CURT_LEND_DEPT_CODE"));
		sbSql.append("',CURT_LEND_DR_CODE='");
		sbSql.append(parm.getValue("CURT_LEND_DR_CODE"));
		sbSql.append("',OPT_USER ='");
		sbSql.append(parm.getValue("OPT_USER"));
		sbSql.append("',OPT_DATE=SYSDATE,OPT_TERM='");
		sbSql.append(parm.getValue("OPT_TERM"));
		sbSql.append("' WHERE MR_NO='");
		sbSql.append(parm.getValue("MR_NO"));
		sbSql.append("' AND ADM_TYPE='");
		sbSql.append(parm.getValue("ADM_TYPE"));
		sbSql.append("' ");
		if (StringUtils.isNotEmpty(parm.getValue("BOOK_NO"))) {
			sbSql.append(" AND BOOK_NO='");
			sbSql.append(parm.getValue("BOOK_NO"));
			sbSql.append("' ");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().update(sbSql.toString(), conn));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
