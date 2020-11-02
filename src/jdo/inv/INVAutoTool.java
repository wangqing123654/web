package jdo.inv;

import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: �����Զ�����</p>
 *
 * <p>Description:�����Զ����� </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author yuhb
 * @version 1.0
 */
public class INVAutoTool extends TJDOTool{
	/**
	 * ʵ��
	 */
	public static INVAutoTool instanceObject;
	
	/**
	 * ������
	 */
	public INVAutoTool() {
		super.setModuleName("inv\\INVAutoModule.x");
		super.onInit();
	}
	
	/**
	 * �õ�ʵ��
	 * @return
	 */
	public static INVAutoTool getInstance() {
		if (null == instanceObject) {
			instanceObject = new INVAutoTool();
		}
		return instanceObject;
	}
	
	
}
