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
 * <p>Title:ԤԼסԺ </p>
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
     * ʵ��
     */
    public static ADMResvTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SchWeekTool
     */
    public static ADMResvTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMResvTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ADMResvTool() {
        setModuleName("adm\\ADMResvModule.x");
        onInit();
    }

    /**
     * ��ѯadm_resvȫ�ֶ�
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
     * ��ѯadm_resvȫ�ֶζ��ԤԼסԺʹ��
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
     * ��ѯĳһ����δ��ס�� ԤԼ��Ϣ
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
     * ����ԤԼסԺ��
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
     * �޸Ĳ�����Ϣ
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
     * �޸�ԤԼסԺ�����ײ;������
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
     * סԺ�Ǽ�ʱ����adm_resv�ֶ�
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
     * ȡ��ԤԼ
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
     * ԤԼסԺ��֪ͨ
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
     * סԺ�Ǽ�ʱ����ԤԼ��Ϣ�����ײ�ʹ��
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
     * ȡ��ԤԼ
     * 1,�޸�adm_resv ȡ����Ա��ȡ��״̬��ȡ��ʱ�� �ֶ�
     * 2,���ȡ����λ
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
        if (parm.getValue("LUMPWORK_CASE_NO").length()>0) {//pangben 2014-8-1�����ײ��Ѿ���ֵ����
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
     * ��ѯԤԼ��ס�Ŀ��ң�������ҽʦ(��ӡסԺ֤ʹ��)
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
     * �޸��ʸ�ȷ������
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
	   * ��ѯδ�᰸����
	   * @param parm
	   * @return
	   */
	  public TParm queryResvNClose(TParm parm){
		  TParm result = query("queryResvNClose", parm);
	      return result;
	  }
	  /**
	   * ��ѯ���������
	   * @return
	   */
	  public TParm overYearNHIPatInfo(TParm parm){
		  TParm result = query("overYearNHIPatInfo", parm);
	      return result;
	  }
	  
	/**
	 * ��ѯԤԼסԺ����ͳ��
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
	 * ��ѯԤԼסԺ����ͳ��ԤԼ
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
	 * ��ѯԤԼסԺ����ͳ��ȡ��
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
	 * ��ѯԤԼסԺ����ͳ����Ժ
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
	 * ��ѯ���������
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
