package jdo.med;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MedSmsListener implements ServletContextListener {
	
	 
	MedSmsTimerTask task = null;
	public void contextInitialized(ServletContextEvent event){
		String configDir = event.getServletContext().getRealPath("/");  
		/**
		 * ������ʱ��
		 * ��1���ִ�д�����,ÿ�μ��30��.
		 */
		 
		task =  new MedSmsTimerTask(configDir);
		task.doJob() ;
		event.getServletContext().log("��ʱ��������");
	}
	
	public void contextDestroyed(ServletContextEvent event){
		/**
		 * ���ٶ�ʱ��
		 */
		task.close();
		event.getServletContext().log("��ʱ������");
	}

}
