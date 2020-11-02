package jdo.ope;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: ��������Tool</p>
 *
 * <p>Description: ��������Tool</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-10-8
 * @version 4.0
 */
public class OPETool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static OPETool instanceObject;

    /**
     * �õ�ʵ��
     * @return SYSRegionTool
     */
    public static OPETool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPETool();
        return instanceObject;
    }

    public OPETool() {
    }
    /**
     * ��ȡ������¼��Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm getOPEDetail(TParm parm){
        TParm result = OPEOpDetailTool.getInstance().selectData(parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����������Ϣת�루������ҳʹ�ã�
     * @return TParm
     */
    public TParm intoOPEDataForMRO(String CASE_NO){
        TParm result = OPEOpDetailTool.getInstance().intoOPEDataForMRO(CASE_NO);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �Ƿ�Ϊ��������
     * 
     * @param deptCode
     * @return
     */
    public boolean isOpDept(String deptCode) {// wanglong add 20140707
        String sql =
                "SELECT DEPT_CODE FROM SYS_DEPT WHERE DEPT_CODE = '#' AND OP_FLG = 'Y'"
                        .replaceFirst("#", deptCode);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
