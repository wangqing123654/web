package jdo.ins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>
 * Title: סԺ���ý������
 * </p>
 * 
 * <p>
 * Description: סԺ���ý������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author pangben 2012-2-1
 * @version 1.0
 */
public class INSIbsTool extends TJDOTool{
	/**
	 * ʵ��
	 */
	public static INSIbsTool instanceObject;
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * �õ�ʵ��
	 * 
	 * @return
	 */
	public static INSIbsTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSIbsTool();
		return instanceObject;
	}
	  /**
     * ������
     */
    public INSIbsTool() {
    	setModuleName("ins\\INSIbsModule.x");
        onInit();
    }
	/**
	 * �޸��ʸ�ȷ������ 
	 * סԺ�ʸ�ȷ�������ؿ�������
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateINSIbsConfirmNo(TParm parm,TConnection conn){
		TParm result = update("updateINSIbsConfirmNo",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ��IBS_OrdD��ȡ����   ��ȡסԺ��������
	 * @param parm
	 * @return
	 */
	public TParm queryIbsOrdd(TParm parm){
		TParm result = query("queryIbsOrdd",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 *  ��ȡסԺ�������� ��Ϊҽ���������⣬
	 *  ҽ��ʱ���������û�в�ѯ����ҽ���������»��δ��ѯ������
	 * @param parm
	 * @return
	 */
	public TParm queryIbsOrddOther(TParm parm){
		TParm result = query("queryIbsOrddOther",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * �������
	 * @param parm
	 * @return
	 */
	public TParm insertInsIbs(TParm parm){
		TParm result = update("insertInsIbs",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * �޸����� ���÷ָ����ת������Ϣʹ��
	 * @param parm
	 * @return
	 */
	public TParm updateINSIbs(TParm parm){
		TParm result = update("updateINSIbs",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ���÷ָ��������
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateSplitByIns(TParm parm,TConnection connection){
		TParm result = update("updateSplitByIns",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ���÷ָ������ ���ý�������޸� ҽ���ز����ݱ���
	 * @param parm
	 * @return
	 */
	public TParm updateInsIbsAmt(TParm parm,TConnection connection){
		TParm result = update("updateInsIbsAmt",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * ��ѯ�����ȫ�ֶ�
	 * @param parm
	 * @return
	 */
	public TParm queryIbsSum(TParm parm){
		TParm result = query("queryIbsSum",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ���÷ָ� ����ʹ�ò�ѯҽ����������
	 * @param parm
	 * @return
	 */
	public TParm queryRetrunInsAmt(TParm parm){
		TParm result = query("queryRetrunInsAmt",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ��ѯADM_SEQ
	 * @param parm
	 * @return
	 */
	public TParm queryAdmSeq(TParm parm){
		TParm result = query("queryAdmSeq",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * �޸ķ��÷ָ��������
	 * @param parm
	 * @return
	 */
	public TParm updateIbsOther(TParm parm){
		TParm result = update("updateIbsOther",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * �޸Ĵ�λ���������ҽ�ò��Ϸ�������
	 * @param parm
	 * @return
	 */
	public TParm updateIbsBedFee(TParm parm){
		TParm result = update("updateIbsBedFee",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ��ѯҽ����Ŀ�ֵ�����ҩ����
	 * @param parm
	 * @return
	 */
	public TParm queryInsRuleET(TParm parm){
		TParm result = query("queryInsRuleET",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ���ý���ʹ��  ���ҽ�� ���� ����INS_IBS ����
	 * @param parm
	 * @return
	 */
	public TParm queryInsAmt(TParm parm){
		TParm result = query("queryInsAmt",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ���÷ָ������ ���ý�������޸� ҽ���ز����ݱ���--������
	 * @param parm
	 * @return
	 */
	public TParm updateInsIbsSingleAmt(TParm parm,TConnection connection){
		TParm result = update("updateInsIbsSingleAmt",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * �����ַ��÷ָ� ������ҳ �б������
	 * @param parm
	 * @return
	 */
	public TParm updateInsIbsMro(TParm parm){
		TParm result = update("updateInsIbsMro",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ��ѯҽ������
	 * @param parm
	 * @return
	 */
	public TParm queryInsIbsOrderByInsRule(TParm parm){
		TParm result = query("queryInsIbsOrderByInsRule",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
}
