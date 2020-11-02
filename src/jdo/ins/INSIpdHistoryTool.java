package jdo.ins;

import java.sql.Timestamp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>
 * Title: ��Ŀ�ֵ�ͬ������ҽ������
 * </p>
 * 
 * <p>
 * Description: ����շ�ҽ�������շ�ʱ����ҽ���ָ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author pangben 2012-9-11
 * @version 4.0
 */
public class INSIpdHistoryTool extends TJDOTool{
	/**
	 * ʵ��
	 */
	public static INSIpdHistoryTool instanceObject;
	/**
	 * �õ�ʵ��
	 * 
	 * @return
	 */
	public static INSIpdHistoryTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSIpdHistoryTool();
		return instanceObject;
	}
	  /**
     * ������
     */
    public INSIpdHistoryTool() {
    	setModuleName("ins\\INSIpdHistoryModule.x");
        onInit();
    }
    public TParm selectINSIpdHistory(TParm parm){
    	TParm result = query("selectINSIpdHistory",parm);
        return result;
    }
    /**
     * ����շ�ҽ�����ʱ���ҽ���������
     * @param parm
     * @param nhiInsCode
     * @param bilTime
     * @return
     */
    public TParm getNhiInsParm(TParm parm, Timestamp bilTime){
    	TParm temp=new TParm();
    	temp.setData("ORDER_CODE",parm.getValue("ORDER_CODE"));
    	TParm result=selectINSIpdHistory(temp);
    	if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return null;
        }
    	if (result.getCount()<=0) {
			return parm;
		}
    	for (int i = 0; i < result.getCount(); i++) {
			if (result.getTimestamp("START_DAY",i).before(bilTime)) {
				return result.getRow(i);
			}
		}
    	return parm;
    }
}
