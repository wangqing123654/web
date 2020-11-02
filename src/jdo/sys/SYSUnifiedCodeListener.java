package jdo.sys;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import jdo.med.MedSmsTimerTask;

public class SYSUnifiedCodeListener implements ServletContextListener {
	SYSUnifiedCodeTask task = null;

	public void contextInitialized(ServletContextEvent event) {
		String configDir = event.getServletContext().getRealPath("/");
		/**
		 * 启动定时器 在1秒后执行此任务,每次间隔1小时
		 */

		task = new SYSUnifiedCodeTask(configDir);
		task.doJob();
		event.getServletContext().log("定时器已启用");
	}

	public void contextDestroyed(ServletContextEvent event) {
		/**
		 * 销毁定时器
		 */
		task.close();
		event.getServletContext().log("定时器销毁");
	}
}
