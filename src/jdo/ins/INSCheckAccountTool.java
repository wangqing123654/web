package jdo.ins;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>
 * Title: ҽ���������
 * </p>
 * 
 * <p>
 * Description: ���㱣����� 
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: bluecore 
 * </p>
 * 
 * @author pangben 2012-1-11
 * @version 1.0
 */
public class INSCheckAccountTool  extends TJDOTool{
	/**
	 * ʵ��
	 */
	public static INSCheckAccountTool instanceObject;
	/**
	 * �õ�ʵ��
	 * 
	 * @return INSTJTool
	 */
	public static INSCheckAccountTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSCheckAccountTool();
		return instanceObject;
	}
	/**
	 * ������
	 */
	public INSCheckAccountTool() {
		setModuleName("ins\\INSCheckAccountModule.x");
		onInit();
	}
	/**
	 * ��Ӷ��˽�������
	 * @param parm
	 * @return
	 */
	public TParm insertInsCheckAccount(TParm parm){
		TParm result = update("insertInsCheckAccount", parm);
		return result;
	}
	/**
	 * ��ѯ�Ƿ�������� �������ڲ�ѯ
	 * @param parm
	 * @return
	 */
	public TParm queryInsCheckAccount(TParm parm){
		TParm result = query("queryInsCheckAccount", parm);
		return result;
	}
}
