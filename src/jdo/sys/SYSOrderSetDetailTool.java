package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 *
 * <p>Title: �û�����</p>
 *
 * <p>Description:�û�����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author ehui 20090106
 * @version 1.0
 */
public class SYSOrderSetDetailTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static SYSOrderSetDetailTool instanceObject;

	/**
	 * �õ�ʵ��
	 * @return SYSOrderSetDetailTool
	 */
	public static SYSOrderSetDetailTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSOrderSetDetailTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SYSOrderSetDetailTool() {
		setModuleName("sys\\SYSOrderSetDetailModule.x");
		onInit();
	}
	/**
	 * ���ݼ���ҽ���������������sys_fee������
	 * @param orderSetCode ����ҽ������
	 * @return
	 */
        public TParm selectByOrderSetCode(String orderSetCode) {
                TParm parm = new TParm();
                parm.setData("ORDERSET_CODE", orderSetCode);
                TParm result = new TParm();
                result = query("selectByOrderSetCode", parm);
                if (result.getErrCode() < 0) {
                        err("ERR:" + result.getErrCode() + result.getErrText()
                                        + result.getErrName());
                }
                //System.out.println("result====="+result);
                return result;

	}
        /**
         * ���ݼ���ҽ���������������sys_fee������
         * @param parm TParm
         * @return TParm
         */
        public TParm selSyeFeeData(TParm parm) {
            TParm result = new TParm();
            result = query("selSyeFeeData", parm);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
            }
            return result;

        }

}
