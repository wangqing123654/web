package jdo.dev;

import java.util.List;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:�豸�½�</p>
 *
 * <p>Description:�豸�½� </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company:bluecore </p>  
 *
 * @author fux
 * @version 1.0
 */
public class DevMMTool extends TJDOTool{
	/**
	 * ������
	 */
	public DevMMTool() {
		setModuleName("dev\\DevMMModule.x");
		onInit();
	}
	
	/**
	 * ʵ��
	 */
	private static DevMMTool instanceObject;
	
	/**
	 * �õ�ʵ��
	 * @return
	 */
	public static DevMMTool getInstance() {
		if (null == instanceObject) {
			instanceObject = new DevMMTool();
		}
		return instanceObject;
	}
	/**
	 * ��ѯ��Ӧ�·��½�����
	 * @param parm
	 * @return  
	 */
	public TParm queryDevMMStock(TParm parm) {
		parm = query("queryDevMMStock", parm);
		return parm;
	}
	
	
	
	 
	/**
	 * ��ѯ��Ӧ�·��Ƿ����½�����
	 * @param parm
	 * @return 
	 */
	public TParm queryDevMMStockCount(TParm parm) {
		parm = query("queryDevMMStockCount", parm);
		return parm;
	}
	
	
	
	
	
	/**
	 * ��ѯ�½��������
	 * @param parm
	 * @return
	 */
	public TParm queryDevMonthRKData(TParm parm) {
		parm = query("queryDevMonthRKData", parm);
		return parm;
	}
	
	/**
	 * ��ѯ�½����������� 
	 * @param parm
	 * @return
	 */
	public TParm queryDevMonthCKTRData(TParm parm) {
		parm = query("queryDevMonthCKTRData", parm);
		return parm;
	}
	

	
	/**
	 * ��ѯ�½�����������
	 * @param parm
	 * @return
	 */
	public TParm queryDevMonthCKTCData(TParm parm) {
		parm = query("queryDevMonthCKTCData", parm);
		return parm;
	}
	
	
	
	/**
	 * ��ѯ�½��˻�����
	 * @param parm
	 * @return
	 */
	public TParm queryDevMonthTHData(TParm parm) {
		parm = query("queryDevMonthTHData", parm);
		return parm;
	}
	
	
	
	/**
	 * ��ѯ��ĳ�ʼ��
	 * @param parm
	 * @return
	 */
	public TParm queryDevMonthSHData(TParm parm) {
		parm = query("queryDevMonthSHData", parm);
		return parm;
	}
	
	
	
	/**
	 * ��ѯ�̵��ʼ��
	 * @param parm
	 * @return
	 */
	public TParm queryDevMonthPDData(TParm parm) {
		parm = query("queryDevMonthPDData", parm);
		return parm;
	}
	
 
	
	/**
	 * ����DEV_MMSTOCK
	 * @param parm
	 * @return
	 */
	public TParm updateMMStock(TParm parm) {
		parm = update("updateMMStock",parm);
		return parm;
	}
	
	
	/**
	 * ����DEV_MMSTOCK
	 * @param parm
	 * @return  
	 */ 
	public TParm insertMMStock(TParm parm) {
		System.out.println("����DEV_MMSTOCK");
		parm = update("insertMMStock",parm);
		return parm;   
	}
	 
	
	/**
	 * ɾ��DEV_MMSTOCK 
	 * @param parm
	 * @return
	 */
	public TParm deleteMMStock(TParm parm) {
		parm = update("deleteMMStock",parm);
		return parm;
	}
	
	
	
}
