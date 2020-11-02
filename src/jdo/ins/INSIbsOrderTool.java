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
 * Description: סԺ���ý������סԺ������ϸ
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
public class INSIbsOrderTool extends TJDOTool{
	/**
	 * ʵ��
	 */
	public static INSIbsOrderTool instanceObject;
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * �õ�ʵ��
	 * 
	 * @return
	 */
	public static INSIbsOrderTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSIbsOrderTool();
		return instanceObject;
	}
	  /**
     * ������
     */
    public INSIbsOrderTool() {
    	setModuleName("ins\\INSIbsOrderModule.x");
        onInit();
    }
	/**
	 * ɾ����ϸ������
	 * @param parm
	 * @return
	 */
	public TParm deleteINSIbsOrder(TParm parm){
		TParm result = update("deleteINSIbsOrder",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * �����ϸ������
	 * @param parm
	 * @return
	 */
	public TParm insertINSIbsOrder(TParm parm){
		TParm result = update("insertINSIbsOrder",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ��ѯ�ϴ�����
	 * @param parm
	 * @return
	 */
	public TParm queryInsIbsDUnion(TParm parm){
		TParm result = query("queryInsIbsDUnion",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * У����÷ָ�����Ƿ���ͬ
	 * @param parm
	 * @return
	 */
	public TParm queryCheckSumIbsOrder(TParm parm){
		TParm result = query("queryCheckSumIbsOrder",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ���÷ָ� ��ѯ�ָ�ǰ����
	 * @param parm
	 * @return
	 */
	public TParm queryOldSplit(TParm parm){
		TParm result = query("queryOldSplit",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
}
