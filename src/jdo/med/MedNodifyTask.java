package jdo.med;

import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.root.client.SocketLink;

/**
 * 
 * 
 * @author Administrator
 *
 */
public class MedNodifyTask {
	//
	private SocketLink client1;
	private TSocket socket = new TSocket("127.0.0.1", 8080, "web");

	//
	private MedNodifyTask() {
	}

	private static MedNodifyTask instance = new MedNodifyTask();

	public static MedNodifyTask getInstance() {
		return instance;
	}

	public void sendNodify() {
		TParm result = TIOM_AppServer.executeAction(socket,
				"action.med.MedNodifyAction", "queryAll", new TParm());

		if (result.getCount() <= 0) {
			System.out.println("参数档没有配置，不发送消息");
			return;
		}
		sendMedMessages(result);

	}

	/**
	 * 发送消息
	 */
	public void sendMedMessages(TParm parm) {
		TParm result = null;
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

	}
}
