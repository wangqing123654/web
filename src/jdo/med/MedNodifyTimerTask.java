package jdo.med;

import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.root.client.SocketLink;

public class MedNodifyTimerTask {
	private ScheduledThreadPoolExecutor stpe = null;
/*	private SocketLink client1;
	private TSocket socket = new TSocket("127.0.0.1", 8080, "web");*/

	private static jdo.med.MedNodifyTimerTask medNodifyTimerTask = null;

	/**
	 * 构造器
	 */
	private MedNodifyTimerTask() {

	}

	public static jdo.med.MedNodifyTimerTask getSingle() {
		if (medNodifyTimerTask == null) {
			medNodifyTimerTask = new jdo.med.MedNodifyTimerTask();
		}
		return medNodifyTimerTask;
	}

	/**
	 * 定时任务开始
	 */
	public void doRun() {

		// 构造一个ScheduledThreadPoolExecutor对象，并且设置它的容量为1个
		stpe = new ScheduledThreadPoolExecutor(1);
		// 隔5分钟后开始执行任务，并且在上一次任务开始后隔5分钟再执行一次；
		// MyTask task = new MyTask();
		try {
			stpe.scheduleWithFixedDelay(new Runnable() {
				public void run() {
					MedNodifyTask.getInstance().sendNodify();
					/*System.out  
                    .println(new Date()+"===发送消息==="); */ 
				}
			}, 10, 60, TimeUnit.SECONDS);
		} catch (Exception e) {
			System.out.println("err::" + e.toString());
		}

	}

	/**
	 * 任务主方法
	 * 
	 * @author Administrator  implements Runnable
	 * 
	 */
/*	private class MyTask  {
		private MyTask() {
		}

		private static MyTask instance = new MyTask();
		
		public static MyTask getInstance() {
			return instance;
		}

		public void run() {
			TParm result = TIOM_AppServer.executeAction(socket,
					"action.med.MedNodifyAction", "queryAll", new TParm());

			if (result.getCount() <= 0) {
				System.out.println("参数档没有配置，不发送消息");
				return;
			}
			sendMedMessages(result);

		}

	}

	*//**
	 * 发送消息
	 *//*
	public void sendMedMessages(TParm parm) {
		TParm result=null;
		for (int i = 0; i < parm.getCount(); i++) {
			result = TIOM_AppServer.executeAction(socket,
					"action.med.MedNodifyAction", "selectResultOut", parm
							.getRow(i));
			if (result.getCount() <= 0) {
				continue;
			}
			client1 = SocketLink.running(parm.getValue("SKT_USER", i), parm
					.getValue("PASSWORD", i));
			if (client1.isClose()) {
				continue;
			}
			client1.sendMessage(parm.getValue("SKT_USER", i), "Message:off");

			//
			if (client1 == null) {
				continue;
			} else {
				client1.close();
				continue;
			}

		}

	}*/

	/**
	 * 关闭
	 */
	public void close() {
		stpe.shutdown();
	}
}
