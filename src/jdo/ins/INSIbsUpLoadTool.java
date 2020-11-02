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
 * Description: סԺ���ý������סԺ������ϸ�ϴ�
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
 * @author pangben 2012-2-6
 * @version 1.0
 */
public class INSIbsUpLoadTool extends TJDOTool{
	/**
	 * ʵ��
	 */
	public static INSIbsUpLoadTool instanceObject;
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * �õ�ʵ��
	 * 
	 * @return
	 */
	public static INSIbsUpLoadTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSIbsUpLoadTool();
		return instanceObject;
	}
	 /**
     * ������
     */
    public INSIbsUpLoadTool() {
        setModuleName("ins\\INSIbsUpLoadModule.x");
        onInit();
    }
	/**
	 * ɾ����ϸ������
	 * @param parm
	 * @return
	 */
	public TParm deleteINSIbsUpload(TParm parm,TConnection connection){
		TParm result = update("deleteINSIbsUpload",parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ɾ����ϸ������
	 * @param parm
	 * @return
	 */
	public TParm deleteINSIbsUpload(TParm parm){
		TParm result = update("deleteINSIbsUpload",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ɾ����ϸ��ϸ����������
	 * @param parm
	 * @return
	 */
	public TParm deleteINSIbsUploadSeq(TParm parm){
		TParm result = update("deleteINSIbsUploadSeq",parm);
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
	public TParm insertINSIbsUpload(TParm parm,TConnection connection){
		TParm result = update("insertINSIbsUpload",parm,connection);
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
	public TParm insertINSIbsUpload(TParm parm){
		TParm result = update("insertINSIbsUpload",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ��ѯ�˾�����ܽ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm querySumIbsUpLoad(TParm parm){
		TParm result = query("querySumIbsUpLoad",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ��ѯ���SEQ_NO �ۼ�����ʹ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm queryMaxIbsUpLoad(TParm parm){
		TParm result = query("queryMaxIbsUpLoad",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * �ۼ������������
	 * @param parm
	 * @return
	 */
	public TParm insertINSIbsUploadOne(TParm parm,TConnection connection){
		TParm result = update("insertINSIbsUploadOne",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * �ۼ������޸�����
	 * @param parm
	 * @return
	 */
	public TParm updateINSIbsUploadOne(TParm parm,TConnection connection){
		TParm result = update("updateINSIbsUploadOne",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ��ѯ�Ƿ�����ۼ���������
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm queryIbsUploadAdd(TParm parm){
		TParm result = query("queryIbsUploadAdd",parm);
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
	public TParm queryCheckSumIbsUpLoad(TParm parm){
		TParm result = query("queryCheckSumIbsUpLoad",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ���÷ָ� ��ѯ�ָ����ϸ����
	 * @param parm
	 * @return
	 */
	public TParm queryNewSplit(TParm parm){
		TParm result = query("queryNewSplit",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ���÷ָ� ��ѯ���÷ָ����ϸ���ϴ�������ʾ
	 * @param parm
	 * @return
	 */
	public TParm queryNewSplitUpLoad(TParm parm){
		TParm result = query("queryNewSplitUpLoad",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * �������ȫ���ݲ�ѯ
	 * @param parm
	 * @return
	 */
	public TParm queryAllSumAmt(TParm parm){
		TParm result = query("queryAllSumAmt",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ���÷ָ���޸�����
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm updateSplit(TParm parm,TConnection connection){
		TParm result = update("updateSplit",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * �����ֲ�����ѯ��λ�����������ҽ�ò��Ϸ�������
	 * @param parm
	 * @return
	 */
	public TParm queryBedAndMaterial(TParm parm){
		TParm result = query("queryBedAndMaterial",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * �ۼ��������� ɾ�� NHI_ORDER_CODE=��***018��������
	 * @param parm
	 * @return
	 */
	public TParm deleteAddIbsUpload(TParm parm,TConnection connection){
		TParm result = update("deleteAddIbsUpload",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
}
