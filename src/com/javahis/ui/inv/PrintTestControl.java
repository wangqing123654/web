package com.javahis.ui.inv;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.javahis.util.RFIDPrintUtils;

/**
 * 
 * @author lixiang
 *
 */
public class PrintTestControl  extends TControl implements MessageListener{
	
	// ConnectionFactory �����ӹ�����JMS ������������
	ConnectionFactory connectionFactory;
	// Connection ��JMS �ͻ��˵�JMS Provider ������
	Connection connection = null;
	// Session�� һ�����ͻ������Ϣ���߳�
	Session session;
	//
	MessageConsumer consumer;
	
	

	
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
    }
    
    /**
     * ��ӡ����
     */
    public void onPrint() {
    	TParm parm = new TParm(); 

		parm.setData(RFIDPrintUtils.PARM_NAME, "����ʽ֧��");

		parm.setData(RFIDPrintUtils.PARM_SPEC, "4*4");

		parm.setData(RFIDPrintUtils.PARM_VALID_DATE, "2013-06-30");

		// ʮ����
		parm.setData(RFIDPrintUtils.PARM_CODE, "130530000002");

		// ��Ҫת��(��һ��Ӧ������ģ���Ƿ��������)
		// ʮ������
		parm.setData(RFIDPrintUtils.PARM_PRFID, "130530000002");

		// ������ӡ
		RFIDPrintUtils.send2LPT(parm);
    }
    
    /**
     * ��ӡ����(��)
     */
    public void  onPrintM(){
    	TParm parm = new TParm();
    	//��ʼֵ
    	for(int i=10;i<=19;i++){
	    	// ʮ������
    		String s1="1305300000"+i;
    		System.out.println("--s1--"+s1);
			parm.setData(RFIDPrintUtils.PARM_PRFID, s1);
			this.messageBox("--2���ִ��--");
			// ������ӡ
			RFIDPrintUtils.send2LPT(parm);	
			try {
				System.out.println("--�ȴ�2��-");
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch blockr
				e.printStackTrace();
			}
			
    	}
    	
    	/*TParm parm = new TParm();

		parm.setData(RFIDPrintUtils.PARM_NAME, "Ʒ������2");

		parm.setData(RFIDPrintUtils.PARM_SPEC, "������215*15");

		parm.setData(RFIDPrintUtils.PARM_VALID_DATE, "2013/04/14");

		// ʮ����
		parm.setData(RFIDPrintUtils.PARM_CODE, "130104000002");

		// ��Ҫת��(��һ��Ӧ������ģ���Ƿ��������)
		// ʮ������
		parm.setData(RFIDPrintUtils.PARM_PRFID, "130104000002");

		// ������ӡ
		RFIDPrintUtils.send2LPT(parm);*/
		
		//2.----------------------------------------------------
		//TParm parm = new TParm();

/*		parm.setData(RFIDPrintUtils.PARM_NAME, "Ʒ������3");

		parm.setData(RFIDPrintUtils.PARM_SPEC, "������3 15*15");

		parm.setData(RFIDPrintUtils.PARM_VALID_DATE, "2013/04/15");

		// ʮ����
		parm.setData(RFIDPrintUtils.PARM_CODE, "130104000003");

		// ��Ҫת��(��һ��Ӧ������ģ���Ƿ��������)
		// ʮ������
		parm.setData(RFIDPrintUtils.PARM_PRFID, "130104000003");

		// ������ӡ
		RFIDPrintUtils.send2LPT(parm);*/
    	
    }	
    
    
    public void onStart(){
    	
    	this.connectionFactory = connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, "tcp://127.0.0.1:61616");
        try {
			this.connection = connectionFactory.createConnection();
			// ����
			connection.start();
			// ��ȡ��������
			session = connection.createSession(Boolean.FALSE,
					Session.AUTO_ACKNOWLEDGE);
			
			ActiveMQQueue topic = new ActiveMQQueue("FirstQueue");
			this.consumer=this.session.createConsumer(topic, "2 > 1", false);//
			
			//��ʼ����
			//���������     ͬʱ����  ����Ҫ���ʱ���ԣ�
			this.consumer.setMessageListener(this);
			
			System.out.println("--------�����ɹ�----------");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("--------����ʧ��----------");
		};
    	
    }
    
    
    public void onEnd(){
   	
    	try {
    		if (consumer != null)   consumer.close();  
    		if (session != null)  this.session.close();
    		if (connection != null) this.connection.close();
			System.out.println("--------�Ͽ��ɹ�----------");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("--------�Ͽ�ʧ��----------");
		}
    }
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PrintTestControl  p=new PrintTestControl();
		 p.onPrintM();
	}

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				String msg = txtMsg.getText();
				System.out.println("Consumer:->Received: " + msg);
			} else {
				System.out.println("Consumer:->Received: " + message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

}
