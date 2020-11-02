package action.device;


import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.xml.ws.BindingProvider;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import action.device.client.DoRun;
import action.device.client.DoRunServiceName;
import action.device.client.RfidControllerService;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

/**
 * RFIDAction �豸���ܽӿ���
 * 
 * @author lixiang
 * 
 */
public class RFIDAction extends TAction {
	// ���Կ���
	public final boolean isDebug = true;
	/**
	 * ����Ĳ��� ��д��ID
	 */
	public final static String READER_ID = "READER_ID";
	/**
	 * ������Ϣ
	 */
	public final static String RECEIVER_MSG = "RECEIVER_MSG";

	/**
	 * ��������
	 */
	public final static String MQ_QUEUE_NAME = "MQ_QUEUE_NAME";

	/**
	 * RFID ��Ӧwebservice��ַ
	 */
	public final static String RFID_WS_URL = "WS_URL";
	
	/**
	 * ɾ��ȱʡ���Ŷ���
	 */
	public final static String DEFAULD_DLQ_QUEUE="ActiveMQ.DLQ";

	/**
	 * MQ��Ϣ����IP��ַ
	 */
	public final static String MQ_IP = "MQ_IP";
	public TParm onRotate(TParm tparm) {
		TParm rtn = new TParm();
	for (int i = 0; i < tparm.getCount(MQ_QUEUE_NAME); i++) {
	    	
			String mq  =tparm.getData(MQ_QUEUE_NAME,i).toString();
		      String mqIP = tparm.getData(MQ_IP,i).toString();
		      String url="http://"+mqIP+":8080/CoatHanger/chWebService";
				if (mqIP == null || mqIP.length() == 0) {
					
					return rtn;
				}
				try {
//					URL url=new URL("http://"+mqIP+":8080/CoatHanger/chWebService?wsdl");
//					doRunServiceName = new DoRunServiceName(url,new QName("http://www.webservice.com", "DoRunServiceName"));
//					DoRun doRun=doRunServiceName.getDoRunServicePort();
					
					JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
					factory.setAddress(url);
					// �ӿ�
					factory.setServiceClass(DoRun.class);
					DoRun port = (DoRun) factory
							.create();
					Map<String, Object> ctxt = ((BindingProvider) port)  
			        .getRequestContext();  
			   ctxt.put("com.sun.xml.internal.ws.connect.timeout", 3000);  
			   ctxt.put("com.sun.xml.internal.ws.request.timeout", 2000);  
				ctxt.put("sun.net.client.defaultConnectTimeout", 3000);  
				ctxt.put("sun.net.client.defaultReadTimeout", 2000); 
				ctxt.put("javax.xml.ws.client.connectionTimeout", 3000);  
				ctxt.put("javax.xml.ws.client.receiveTimeout", 2000); 
				port.doRun(mq);
					
				} catch (Exception e) {

                    System.out.println("ws���ӳ�ʱ======"+e.toString());
                    rtn.setErr(-1, "����RFID�豸ʧ��.");
				} 
				
		}
	return rtn;
		
		
	}

	/**
	 * ���Ӷ�д��ָ��
	 * 
	 * @param readerId
	 */
	
	public TParm connect(TParm parm) {
		Timestamp date = StringTool.getTimestamp(new Date());
		if (isDebug) {
			System.out.println("=======connect start=========="+date.toString()+"======"+parm.getValue(READER_ID));
		}
		String readerId = parm.getValue(READER_ID);
		TParm rtn = new TParm();
		try {
			// ����ws readerId��RFID���ݲɼ������������õĶ�д����Ψһ�Ա�ʶ��
			// String
			// url="http://192.168.1.212:8080/rfidcontroller/services/rfidcontrollerservice";
			String url = parm.getValue(RFID_WS_URL);
			if (isDebug) {
				System.out.println("=======url=========" + url);
			}

			JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
			factory.setAddress(url);
			// �ӿ�
			factory.setServiceClass(RfidControllerService.class);
			RfidControllerService port = (RfidControllerService) factory
					.create();
			
			
			Map<String, Object> ctxt = ((BindingProvider) port)  
	        .getRequestContext();  
	   ctxt.put("com.sun.xml.internal.ws.connect.timeout", 3000);  
	   ctxt.put("com.sun.xml.internal.ws.request.timeout", 2000);  
		ctxt.put("sun.net.client.defaultConnectTimeout", 3000);  
		ctxt.put("sun.net.client.defaultReadTimeout", 2000); 
		ctxt.put("javax.xml.ws.client.connectionTimeout", 3000);  
		ctxt.put("javax.xml.ws.client.receiveTimeout", 2000); 
			//
			port.connect(readerId);
			port.startScan(readerId);
		} catch (Exception e) {
			e.printStackTrace();
			rtn.setErr(-1, "����RFID�豸ʧ��.");
		}
		if (isDebug) {
			System.out.println("=======connect end==========");
		}
		return rtn;

	}
	

	/**
	 * �Ͽ�RFID�豸����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm disconnect(TParm parm) {
		Timestamp date = StringTool.getTimestamp(new Date());
		if (isDebug) {
			System.out.println("=======disconnect start=========="+date.toString()+"======"+parm.getValue(READER_ID));
		}
		String readerId = parm.getValue(READER_ID);
		TParm rtn = new TParm();
		try {
			// String
			// url="http://192.168.1.212:8080/rfidcontroller/services/rfidcontrollerservice";
			String url = parm.getValue(RFID_WS_URL);
			JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
			factory.setAddress(url);
			factory.setServiceClass(RfidControllerService.class);
			RfidControllerService port = (RfidControllerService) factory
					.create();
			//
			Map<String, Object> ctxt = ((BindingProvider) port)  
	        .getRequestContext();  
	   ctxt.put("com.sun.xml.internal.ws.connect.timeout", 3000);  
	   ctxt.put("com.sun.xml.internal.ws.request.timeout", 2000);  
		ctxt.put("sun.net.client.defaultConnectTimeout", 3000);  
		ctxt.put("sun.net.client.defaultReadTimeout", 2000); 
		ctxt.put("javax.xml.ws.client.connectionTimeout", 3000);  
		ctxt.put("javax.xml.ws.client.receiveTimeout", 2000); 
			port.stopScan(readerId);
			port.disconnect(readerId);
		} catch (Exception e) {
			e.printStackTrace();
			rtn.setErr(-1, "�Ͽ�RFID�豸ʧ��.");

		}
		if (isDebug) {
			System.out.println("=======disconnect end==========");
		}
		return rtn;

	}

	/**
	 * ��ʼɨ��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm startScan(TParm parm) {
		Timestamp date = StringTool.getTimestamp(new Date());
		if (isDebug) {
			System.out.println("=======startScan start=========="+date.toString()+"======"+parm.getValue(READER_ID));
		}
		String readerId = parm.getValue(READER_ID);
		TParm rtn = new TParm();
		/*
		 * try { //Stringurl=
		 * "http://192.168.1.212:8080/rfidcontroller/services/rfidcontrollerservice"
		 * ; String url=parm.getValue(RFID_WS_URL); JaxWsProxyFactoryBean
		 * factory = new JaxWsProxyFactoryBean(); factory.setAddress(url);
		 * factory.setServiceClass(RfidControllerService.class);
		 * RfidControllerService port = (RfidControllerService)
		 * factory.create(); //port.startScan("D001"); port.startScan(readerId);
		 * } catch (Exception e) { e.printStackTrace(); rtn.setErr(-1,
		 * "��ʼɨ��ʧ��.");
		 * 
		 * } if(isDebug){ System.out.println("=======startScan end==========");
		 * }
		 */
		return rtn;
	}

	/**
	 * ����ɨ��ָ��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm stopScan(TParm parm) {
		Timestamp date = StringTool.getTimestamp(new Date());
		if (isDebug) {
			System.out.println("=======stopScan start=========="+date.toString()+"======"+parm.getValue(READER_ID));
		}
		String readerId = parm.getValue(READER_ID);

		TParm rtn = new TParm();
		/*
		 * try { //Stringurl=
		 * "http://192.168.1.212:8080/rfidcontroller/services/rfidcontrollerservice"
		 * ; String url=parm.getValue(RFID_WS_URL); JaxWsProxyFactoryBean
		 * factory = new JaxWsProxyFactoryBean(); factory.setAddress(url);
		 * factory.setServiceClass(RfidControllerService.class);
		 * RfidControllerService port = (RfidControllerService)
		 * factory.create(); //port.stopScan("D001"); port.stopScan(readerId); }
		 * catch (Exception e) { e.printStackTrace(); rtn.setErr(-1, "����ɨ��ʧ��.");
		 * } if(isDebug){ System.out.println("=======stopScan end=========="); }
		 */
		return rtn;
	}

	/**
	 * ����MQ��Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm receiver(TParm parm) {
		TParm msg = new TParm();
		final String mqIP = parm.getValue(MQ_IP);
		if (mqIP == null || mqIP.length() == 0) {
			msg.setErr(-1, "MQ������IP���ò�������!");
			return msg;
		}

		final String queueName = parm.getValue(MQ_QUEUE_NAME);
		if (isDebug) {
			System.out.println("=======queueName==========" + queueName);
			System.out.println("=======receiver start==========");
		}

		/*
		 * <?xml version="1.0" encoding="UTF-8" ?> <taglist> <tag> <epc>
		 * 120034118801010865003102 </epc> <readerid> D001 </readerid> <antenna>
		 * 1 </antenna> <direction> </direction> <time> 2007-08-31 18:20:34.863
		 * </time> </tag> </taglist>
		 */
		String rtnStr = "";
		// ConnectionFactory �����ӹ�����JMS ������������
		ConnectionFactory connectionFactory;
		// Connection ��JMS �ͻ��˵�JMS Provider ������
		Connection connection = null;
		// Session�� һ�����ͻ������Ϣ���߳�
		Session session;
		// Destination ����Ϣ��Ŀ�ĵ�;��Ϣ���͸�˭.
		Destination destination;
		// �����ߣ���Ϣ������
		MessageConsumer consumer;
		connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, "tcp://" + mqIP + ":61616");
		try {
			// ����ӹ����õ����Ӷ���
			connection = connectionFactory.createConnection();
			// ����
			connection.start();
			// ��ȡ��������
			session = connection.createSession(Boolean.FALSE,
					Session.AUTO_ACKNOWLEDGE);
			// ��ȡsessionע�����ֵxingbo.xu-queue��һ����������queue��������ActiveMq��console����
			destination = session.createQueue(queueName);
			consumer = session.createConsumer(destination);

			//
			int i = 0;
			// while (true) {
			// ���ý����߽�����Ϣ��ʱ�䣬Ϊ�˱��ڲ��ԣ�����˭��Ϊ
			TextMessage message = (TextMessage) consumer.receive(1000);
			if (message instanceof TextMessage) {
				// ��Ӧ��XML
				rtnStr = message.getText();
				msg.addData(RECEIVER_MSG, rtnStr);
				i++;
				msg.setCount(i);
			} else {

			}

			/*
			 * if (null != message) { rtnStr =message.getText();
			 * msg.addData(RECEIVER_MSG, rtnStr); i++; msg.setCount(i); } else {
			 * break; }
			 */
			// }
		} catch (Exception e) {
			e.printStackTrace();
			msg.setErr(-1, "MQ ������Ϣʧ��");

		} finally {
			try {
				if (null != connection)
					connection.close();
			} catch (Throwable ignore) {
			}
		}
		if (isDebug) {
			System.out.println("=======receiver end==========");
		}
		// ����ֵ
		return msg;
	}
	
	/**
	 * ��ն���(�������ݵķ�ʽ)
	 * @param parm
	 * @return
	 */
	public TParm purge(TParm parm){
		//System.out.println("=======come in==========");
		TParm msg = new TParm();
		final String mqIP = parm.getValue(MQ_IP);
		if (mqIP == null || mqIP.length() == 0) {
			msg.setErr(-1, "MQ������IP���ò�������!");
			return msg;
		}

		final String queueName = parm.getValue(MQ_QUEUE_NAME);
		if (isDebug) {
			System.out.println("=======queueName==========" + queueName);
			System.out.println("=======receiver start==========");
		}
		//String rtnStr = "";
		// ConnectionFactory �����ӹ�����JMS ������������
		ConnectionFactory connectionFactory;
		// Connection ��JMS �ͻ��˵�JMS Provider ������
		Connection connection = null;
		// Session�� һ�����ͻ������Ϣ���߳�
		Session session;
		// Destination ����Ϣ��Ŀ�ĵ�;��Ϣ���͸�˭.
		Destination destination;
		// �����ߣ���Ϣ������
		MessageConsumer consumer;
		connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, "tcp://" + mqIP + ":61616");
		try {
			// ����ӹ����õ����Ӷ���
			connection = connectionFactory.createConnection();
			// ����
			connection.start();
			// ��ȡ��������
			session = connection.createSession(Boolean.FALSE,
					Session.AUTO_ACKNOWLEDGE);
			// ��ȡsessionע�����ֵxingbo.xu-queue��һ����������queue��������ActiveMq��console����
			destination = session.createQueue(queueName);
			consumer = session.createConsumer(destination);
			while (true) {
				// ���ý����߽�����Ϣ��ʱ��
				TextMessage message = (TextMessage) consumer.receive(1000);
				if (null != message) {
					message.getText();		
					//System.out.println("----rtnStr----"+rtnStr);
				} else {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.setErr(-1, "MQ ȡ����Ϣʧ��");

		} finally {
			try {
				if (null != connection)
					connection.close();
			} catch (Throwable ignore) {
			}
		}
		if (isDebug) {
			System.out.println("=======purgeQueue end==========");
		}
		// ����ֵ
		return msg;
	}
	
	
	/**
	 * ǰ��Ҫ������mq���񣬴�JMX
	 * �����������
	 * @param parm
	 * @return
	 */
	public TParm purgeQueue(TParm parm){
		
		TParm msg = new TParm();
		
		final String mqIP = parm.getValue(MQ_IP);
		if (mqIP == null || mqIP.length() == 0) {
			msg.setErr(-1, "MQ������IP���ò�������!");
			return msg;
		}
		final String qName = parm.getValue(MQ_QUEUE_NAME);
		
		if (isDebug) {
			System.out.println("=======mqIP==========" + mqIP);
			System.out.println("=======queueName==========" + qName);
		}
		if(qName==null || qName.length() == 0){
			msg.setErr(-1, "��Ϣ�Զ������Ʋ���Ϊ��!");
			return msg;
		}
		JMXServiceURL url=null;
		JMXConnector connector=null;
		MBeanServerConnection connection=null;
		ObjectName name=null;
		//
		try {
			url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+mqIP+":2011/jmxrmi");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		//
		try {
			/*Map map = new HashMap();
			String[] credentials=new String[]   {"admin","activemq"};
			map.put("jmx.remote.credentials",credentials);*/
			connector = JMXConnectorFactory.connect(url,null);
			connector.connect();
			connection = connector.getMBeanServerConnection();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		//
		try {
			//localhost
			name = new ObjectName("my-broker:BrokerName=rfid,Type=Broker");
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		//
		BrokerViewMBean mBean =  (BrokerViewMBean)MBeanServerInvocationHandler.newProxyInstance(connection,name, BrokerViewMBean.class, true);
		if (isDebug) {
			System.out.println("-----getBrokerName------"+mBean.getBrokerName());
		}
		//
		for(ObjectName queueName : mBean.getQueues()) {
			QueueViewMBean queueMBean =(QueueViewMBean)MBeanServerInvocationHandler.newProxyInstance(connection, queueName, QueueViewMBean.class, true);
			if(queueMBean.getName().equalsIgnoreCase(qName)){
				try {
					queueMBean.purge();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}		
		}
		//
		if(connector!=null){			
			try {
				connector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}			
			connection=null;		
		}
		
		// ����ֵ
		return msg;
	}
	
	/**
	 * ����ʱ������������Ŷ��У����
	 * ɾ��ȱʡ�����Ŷ���
	 * @return
	 */
	public TParm removeDLQQueue(TParm parm){
		//DEFAULD_DLQ_QUEUE		
		TParm msg = new TParm();
		
		final String mqIP = parm.getValue(MQ_IP);
		if (mqIP == null || mqIP.length() == 0) {
			msg.setErr(-1, "MQ������IP���ò�������!");
			return msg;
		}
		//
		final String qName = DEFAULD_DLQ_QUEUE;
		
		if (isDebug) {
			System.out.println("=======mqIP==========" + mqIP);
			System.out.println("=======queueName==========" + qName);
		}
		if(qName==null || qName.length() == 0){
			msg.setErr(-1, "��Ϣ�Զ������Ʋ���Ϊ��!");
			return msg;
		}
		JMXServiceURL url=null;
		JMXConnector connector=null;
		MBeanServerConnection connection=null;
		ObjectName name=null;
		//
		try {
			url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+mqIP+":2011/jmxrmi");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		//
		try {
			/*Map map = new HashMap();
			String[] credentials=new String[]   {"admin","activemq"};
			map.put("jmx.remote.credentials",credentials);*/
			connector = JMXConnectorFactory.connect(url,null);
			connector.connect();
			connection = connector.getMBeanServerConnection();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		//
		try {
			//localhost
			name = new ObjectName("my-broker:BrokerName=rfid,Type=Broker");
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		//
		BrokerViewMBean mBean =  (BrokerViewMBean)MBeanServerInvocationHandler.newProxyInstance(connection,name, BrokerViewMBean.class, true);
		if (isDebug) {
			System.out.println("-----getBrokerName------"+mBean.getBrokerName());
		}
		//�ж��Ƿ������Ŷ���
		boolean flg=false;
		for(ObjectName queueName : mBean.getQueues()) {
			QueueViewMBean queueMBean =(QueueViewMBean)MBeanServerInvocationHandler.newProxyInstance(connection, queueName, QueueViewMBean.class, true);
			if(queueMBean.getName().equalsIgnoreCase(qName)){
				flg=true;
				break;
			}		
		}
		//����
		if(flg){
			if (isDebug) {
				System.out.println("-----removeDLQQueue------"+qName);
			}
			//
			try {				
				mBean.removeQueue(qName);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		
		}
		//
		if(connector!=null){			
			try {
				connector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}			
			connection=null;		
		}
		
		// ����ֵ
		return msg;
		
	}
	

	/**
	 * ����MQ��Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm sender(TParm parm) {
		TParm msg = new TParm();
		return msg;

	}
	

	public static void main(String args[]) {
		/*
		 * Stringurl=
		 * "http://192.168.1.212:8080/rfidcontroller/services/rfidcontrollerservice"
		 * ; //String url=parm.getValue(RFID_WS_URL); JaxWsProxyFactoryBean
		 * factory = new JaxWsProxyFactoryBean(); factory.setAddress(url);
		 * factory.setServiceClass(RfidControllerService.class);
		 * RfidControllerService port = (RfidControllerService)
		 * factory.create();
		 * 
		 * 
		 * port.connect("D003");
		 * 
		 * port.startScan("D003");
		 * 
		 * RFIDAction r=new RFIDAction(); TParm parm=new TParm();
		 * parm.setData(MQ_QUEUE_NAME, "amq.D003"); r.receiver(parm);
		 */

		/*
		 * port.stopScan("D003"); port.disconnect("D003");
		 */

		/*String s = "http://192.168.1.212:8080/rfidcontroller/services/rfidcontrollerservice";
		s = s.substring(s.indexOf("http://") + 7, s.indexOf(":8080"));
		System.out.println("-----s--------" + s);*/
		
		 RFIDAction r=new RFIDAction();
		 TParm parm=new TParm();
		 parm.setData(RFIDAction.MQ_IP, "172.20.116.214");
		 parm.setData(RFIDAction.MQ_QUEUE_NAME, "amq.X001");
		 r.purgeQueue(parm);
	}

}
