package jdo.sys;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import jdo.med.MedSmsTimerTask;

public class SYSUnifiedCodeListener implements ServletContextListener {
	SYSUnifiedCodeTask task = null;

	public void contextInitialized(ServletContextEvent event) {
		String configDir = event.getServletContext().getRealPath("/");
		/**
		 * ������ʱ�� ��1���ִ�д�����,ÿ�μ��1Сʱ
		 */

		task = new SYSUnifiedCodeTask(configDir);
		task.doJob();
		event.getServletContext().log("��ʱ��������");
	}

	public void contextDestroyed(ServletContextEvent event) {
		/**
		 * ���ٶ�ʱ��
		 */
		task.close();
		event.getServletContext().log("��ʱ������");
	}
}
