package jdo.util;

/**
 * 
 * <p>
 * Title: ���ù�����
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author lzk 2009.6.2
 * @version JavaHis 1.0
 */
public class Manager {
	/**
	 * �õ�ϵͳ����
	 * 
	 * @return Systems
	 */
	public static Systems getSystem() {
		return new Systems();
	}

	/**
	 * �õ�ҽ������
	 * 
	 * @return Order
	 */
	public static Order getOrder() {
		return new Order();
	}

	/**
	 * �õ���֯����
	 * 
	 * @return Organization
	 */
	public static Organization getOrganization() {
		return new Organization();
	}

	/**
	 * �õ�ҩƷ����
	 * 
	 * @return Medicine
	 */
	public static Medicine getMedicine() {
		return new Medicine();
	}

	/**
	 * �õ����ù���
	 * 
	 * @return Charge
	 */
	public static Charge getCharge() {
		return new Charge();
	}

	/**
	 * �õ���Ա����
	 * 
	 * @return Personal
	 */
	public static Personal getPersonal() {
		return new Personal();
	}

	/**
	 * �õ���������
	 * 
	 * @return Other
	 */
	public static Other getOther() {
		return new Other();
	}

	public static void main(String args[]) {
		com.javahis.util.JavaHisDebug.initClient();
		com.dongyang.manager.TIOM_AppServer.resetAction();
		System.out.println(Manager.getPersonal().getLockParmString("1"));
	}
}
