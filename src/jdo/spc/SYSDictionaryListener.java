package jdo.spc;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * <p>
 * Title: ��������HISͬ��
 * </p>
 * 
 * <p>
 * Description: ��������HISͬ��,�����������������Ϳ�ʼ�����߳�
 * </p>
 * 
 * @author zhangyiwu 2013.7.10
 * @version 1.0
 */
public class SYSDictionaryListener implements ServletContextListener {
	SYSDictionaryTask task = null;

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

		sce.getServletContext().log("��ʱ������");

	}

	@Override
	//�������������Ϳ�ʼִ�д˷���
	public void contextInitialized(ServletContextEvent sce) {
		//�����߳�
		ConectionThread thread = new ConectionThread();
		Thread aa = new Thread(thread);
		aa.start();

		sce.getServletContext().log("��ʱ��������");
	}

}
