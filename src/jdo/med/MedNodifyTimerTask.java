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
	 * ������
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
	 * ��ʱ����ʼ
	 */
	public void doRun() {

		// ����һ��ScheduledThreadPoolExecutor���󣬲���������������Ϊ1��
		stpe = new ScheduledThreadPoolExecutor(1);
		// ��5���Ӻ�ʼִ�����񣬲�������һ������ʼ���5������ִ��һ�Σ�
		// MyTask task = new MyTask();
		try {
			stpe.scheduleWithFixedDelay(new Runnable() {
				public void run() {
					MedNodifyTask.getInstance().sendNodify();
					/*System.out  
                    .println(new Date()+"===������Ϣ==="); */ 
				}
			}, 10, 60, TimeUnit.SECONDS);
		} catch (Exception e) {
			System.out.println("err::" + e.toString());
		}

	}

	/**
	 * ����������
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
				System.out.println("������û�����ã���������Ϣ");
				return;
			}
			sendMedMessages(result);

		}

	}

	*//**
	 * ������Ϣ
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
	 * �ر�
	 */
	public void close() {
		stpe.shutdown();
	}
}
