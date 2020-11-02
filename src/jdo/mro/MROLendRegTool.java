package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: �������ĵǼ�</p>
 *
 * <p>Description: �������ĵǼ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-11
 * @version 1.0
 */
public class MROLendRegTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static MROLendRegTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static MROLendRegTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROLendRegTool();
        return instanceObject;
    }

    public MROLendRegTool() {
        this.setModuleName("mro\\MROLendRegModule.x");
        onInit();
    }
    /**
     * ��ȡָ��������ԭ�򡱵Ľ�������
     * @param code String
     * @return TParm
     */
    public TParm queryLendDays(String code){
        TParm parm = new TParm();
        parm.setData("LEND_CODE",code);
        TParm result = this.query("selectLendDay",parm);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���벡���������
     * @param parm TParm
     * @return TParm
     */
    public TParm insertQueue(TParm parm){
        TParm result =this.update("insertQueue",parm);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ��ѯ�����ڳ������ˮ����ÿ���Ƿ���Ψһ��¼
     * @param parm TParm
     * @return TParm
     */
	public TParm selectQueue(TParm parm) {
		TParm result = this.query("selectQueue", parm);
		if(result.getErrCode() < 0){
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			return result;
		}
		return result;
	}
}
