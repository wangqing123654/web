package jdo.med;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MedNodifyListener implements ServletContextListener{

	private MedNodifyTimerTask  task = MedNodifyTimerTask.getSingle() ; 

	public void contextDestroyed(ServletContextEvent event) {
		//task = new MedNodifyTimerTask() ;
		task.close();  
		event.getServletContext().log("��ʱ��ע��");   
		
	}

	public void contextInitialized(ServletContextEvent event) {
		//task = new MedNodifyTimerTask() ;
		task.doRun() ;
		event.getServletContext().log("��ʱ������");
		
	}

}
