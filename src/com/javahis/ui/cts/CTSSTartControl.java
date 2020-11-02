package com.javahis.ui.cts;

import java.io.StringReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jdo.cts.CTSTool;
import jdo.sys.Operator;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.ui.inv.RFIDTag;

/**
 * 
 * <p>
 * Title:ϴ�����
 * </p>
 * 
 * <p>
 * Description:ϴ�����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2012
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author zhangp 2012.8.2
 * @version 1.0
 */
public class CTSSTartControl extends TControl implements MessageListener {
	private static TTable tableM;
	private static List<String> cloth_nos = new ArrayList<String>();
	private TParm rfidConfig;
	public final static String MQ_QUEUE_NAME = "MQ_QUEUE_NAME";
	public final static String MQ_IP = "MQ_IP";
	TParm tparm;
	private Set<String> set = new HashSet<String>();
	private String ant_chn_desc = "";
	private String ant_id = "";
	private String rfid_url = "";
	private String mq_desc = "";
	private String rfid_ip = "";
	private String rfid_id = "";
	// ConnectionFactory �����ӹ�����JMS ������������
	ConnectionFactory connectionFactory;
	// Connection ��JMS �ͻ��˵�JMS Provider ������
	Connection connection = null;
	// Session�� һ�����ͻ������Ϣ���߳�
	Session session;
	//
	MessageConsumer consumer;
	
	private boolean disconn = true;   //��������Ƿ�Ͽ����� ����ն���

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		setValue("EVAL_CODE", Operator.getID());
		setValue("REGION_CODE", Operator.getRegion());
		tableM = (TTable) getComponent("TABLE1");
		this.callFunction("UI|END|setEnabled", false);
		setValue("QTY", 0);
		/////////////////////////////////////////////////////////////////////////////////////////////////
		rfidConfig = CTSTool.getInstance().getRfidConfig(Operator.getIP());
		if(rfidConfig.getErrCode()<0){
			messageBox("��д������ʧ��");
			this.callFunction("UI|START|setEnabled", false);
			this.callFunction("UI|END|setEnabled", false);
		}
		ant_chn_desc = rfidConfig.getValue("ANT_CHN_DESC");
		ant_id = rfidConfig.getValue("ANT_ID");
		rfid_url = rfidConfig.getValue("RFID_URL");
		mq_desc = rfidConfig.getValue("MQ_DESC");
		rfid_ip = rfidConfig.getValue("RFID_IP");
		rfid_id = rfidConfig.getValue("RFID_ID");
		
		
		
//		TIOM_AppServer.executeAction("action.device.RFIDAction", "purgeQueue",
//				tparm);
		//////////////////////////////////////////////////////////////////////////////////////////////////
	}

	public void onStart() {
		
		///////////////////////////////////////////////////////////////////////////////////////////////////
		tparm = new TParm();
		tparm.setData("READER_ID", rfid_id);
		tparm.setData("TERM_IP", "");
		tparm.setData("WS_URL", rfid_url);
		tparm.setData(MQ_QUEUE_NAME, mq_desc);
		tparm.setData(MQ_IP, rfid_ip);
		if ( TIOM_AppServer.executeAction("action.device.RFIDAction",
				"connect", tparm).getErrCode()==-1) {
			messageBox("�豸�����쳣   ���飡");
			return;
			
//			TIOM_AppServer.executeAction("action.device.RFIDAction", "connect",
//					tparm);
			
		}
		TIOM_AppServer.executeAction("action.device.RFIDAction", "removeDLQQueue",
				tparm);
		onStartListener();
		////////////////////////////////////////////////////////////////////////////////////////////////////
		this.clearValue("TABLE1");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE", date.toString().substring(0, 19)
				.replaceAll("-", "/"));
		this.callFunction("UI|START|setEnabled", false);
		this.callFunction("UI|END|setEnabled", true);
		this.callFunction("UI|tButton_0|setEnabled", false);
//		this.callFunction("UI|clear|setEnabled", false);
		grabFocus("CLOTH_NO");
		disconn = true;
		
	}

	public void onEnd() {
//		for (String s : set) {
//			setValue("CLOTH_NO", s);
//			System.out.println(s);
//			onEnter();
//		}
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("END_DATE", date.toString().substring(0, 19).replaceAll(
				"-", "/"));
		if (cloth_nos.size() > 0) {
			TParm parm = new TParm();
			String clothNos = "";
			for (int i = 0; i < cloth_nos.size(); i++) {
				clothNos += "'" + cloth_nos.get(i) + "',";
			}
			clothNos = clothNos.substring(0, clothNos.length() - 1);
			parm.setData("CLOTHNOS", clothNos);
			TParm result = CTSTool.getInstance().selectClothByIn(parm);
			for (int i = 0; i < result.getCount(); i++) {
				result.addData("AA", i+1);
			}
			tableM.setParmValue(result);
			setValue("QTY", tableM.getParmValue().getCount("CLOTH_NO"));
			// onSave();
		}
		
//		if(tableM.getParmValue().getCount("CLOTH_NO")>0){
//			setValue("QTY", tableM.getParmValue().getCount("CLOTH_NO"));
//		}else{
//			setValue("QTY", 0);
//		}
		
		
		//////////////////////////////////////////////////////////////////////////////
		
		TIOM_AppServer.executeAction("action.device.RFIDAction", "disconnect",
				tparm);
		TIOM_AppServer.executeAction("action.device.RFIDAction", "purgeQueue",
				tparm);
		disconn = false;
		
		///////////////////////////////////////////////////////////////////////////
		
		try {
			if (consumer != null)
				consumer.close();
			if (session != null)
				this.session.close();
			if (connection != null)
				this.connection.close();
//			System.out.println("--------�Ͽ��ɹ�----------");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			System.out.println("--------�Ͽ�ʧ��----------");
		}
		this.callFunction("UI|END|setEnabled", false);
		this.callFunction("UI|START|setEnabled", true);
		this.callFunction("UI|tButton_0|setEnabled", true);
//		this.callFunction("UI|clear|setEnabled", true);
		
	}

	public void onEnter() {
		String clothNo = getValueString("CLOTH_NO");
		if(cloth_nos.contains(clothNo)){
			return;
		}
//		for (int i = 0; i < cloth_nos.size(); i++) {
//			if (clothNo.equals(cloth_nos.get(i))) {
//				return;
//			}
//		}
		tableM.acceptText();
		TParm tmpParm = tableM.getParmValue();
		if (tmpParm == null) {
			tmpParm = new TParm();
		}
		tmpParm.addData("CLOTH_NO", clothNo);
		tmpParm.addData("INV_CODE", "");
		tmpParm.addData("OWNER", "");
		tmpParm.addData("DEPT_CODE", "");
		tmpParm.addData("STATION_CODE", "");
		tmpParm.addData("STATE", "");
		tmpParm.addData("ACTIVE_FLG", "");
		tmpParm.addData("PAT_FLG", "");
		tmpParm.addData("NEW_FLG", "");
		tmpParm.addData("AA", tmpParm.getCount("CLOTH_NO"));
		cloth_nos.add(clothNo);
		tableM.setParmValue(tmpParm);
		// Toolkit.getDefaultToolkit().beep();
//		onQty();
	}

	/**
	 * ����
	 */
	public void onSave() {
		tableM.acceptText();
		if (tableM.getRowCount() < 1) {
			return;
		}
		TParm parmM = tableM.getParmValue();
//		System.out.println(parmM);
		
		TParm parmDD=new TParm();
		for(int i=0;i<parmM.getCount();i++){
			parmDD.setData("RFID", parmM.getValue("CLOTH_NO", i));
			parmDD.setData("STATE", "0");
			TParm resultDD = CTSTool.getInstance().updateStockDD(parmDD);
			if (resultDD.getErrCode() < 0) {
				// messageBox("����ʧ��");
				return;
			}
			
		}
	
		
		TParm parm = new TParm();
		parm.setData("WASH", parmM.getData());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("START_DATE", getValue("START_DATE"));
		parm.setData("STATION_CODE", getValue("STATION_CODE")); 
//		parm.setData("TURN_POINT", getValue("TURN_POINT"));
		// parm.setData("END_DATE", getValue("END_DATE"));
//		System.out.println("onSave="+parm);
		TParm result = TIOM_AppServer.executeAction("action.cts.CTSAction",
				"saveWash", parm);
		if (result.getErrCode() < 0) {
			// messageBox("����ʧ��");
			return;
		}
		messageBox("����ɹ�");
		
//		TParm washNoParm = result.getParm("WASH_NO");
//		for (int i = 0; i < washNoParm.getCount("WASH_NO"); i++) {
//			onPrint(washNoParm.getValue("WASH_NO", i));
//		}
		
		
		this.onClear();
		
		
	}

	// /**
	// * ���Excel
	// */
	// public void onExport() {
	// tableM.acceptText();
	// // �õ�UI��Ӧ�ؼ�����ķ���
	// TParm parm = tableM.getParmValue();
	// if (null == parm || parm.getCount() <= 0) {
	// this.messageBox("û����Ҫ����������");
	// return;
	// }
	// ExportExcelUtil.getInstance().exportExcel(tableM, "�������������б�");
	// }

	/**
	 *   ���
	 */
	public void onClear() {
		TParm parm = new TParm();
		tableM.setParmValue(parm);
		this.callFunction("UI|START|setEnabled", true);
		this.callFunction("UI|END|setEnabled", false);
		this.clearValue("CLOTH_NO;START_DATE;END_DATE;QTY;STATION_CODE;TURN_POINT");
		setValue("QTY", 0);
		cloth_nos = new ArrayList<String>();
		set = new HashSet<String>();
		/////////////////////////////////////////////////////////////////////////////////////////
		
		
		if(disconn){
			TIOM_AppServer.executeAction("action.device.RFIDAction", "disconnect",
					tparm);
			TIOM_AppServer.executeAction("action.device.RFIDAction", "purgeQueue",
					tparm);
			
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////
	}
	
	 public boolean onClosing(){
		try {
			onClear();
				/////////////////////////////////////////////////////////////////////////////////////////
		} catch (Exception e) {
			// TODO: handle exception
		}
		 /////////////////////////////////////////////////////////////////////////////////////////
		super.onClosing();
		return true;
	 }

	public void onPrint(String wash_no) {
		String sql = " SELECT  B.RFID CLOTH_NO, B.INV_CODE, '' DEPT_CODE, D.STATION_CODE, D.QTY, D.OUT_QTY,"
			+ " E.USER_NAME WASH_CODE, C.INV_CHN_DESC, D.PAT_FLG, '' DEPT_CHN_DESC,"
			+ " G.ORG_DESC STATION_DESC, A.OUT_FLG, H.USER_NAME OWNER, D.START_DATE, D.END_DATE, H.USER_ID, A.TURN_POINT"
			+ " FROM CTS_WASHD A,"
			+ " INV_STOCKDD B,"
			+ " INV_BASE C,"
			+ " CTS_WASHM D,"
			+ " SYS_OPERATOR E,"
			+ " INV_ORG G,"
			+ " SYS_OPERATOR H"
			+ " WHERE A.CLOTH_NO = B.RFID"
			+ " AND B.INV_CODE = C.INV_CODE"
			+ " AND A.WASH_NO = D.WASH_NO"
			+ " AND D.WASH_CODE = E.USER_ID"
			+ " AND  B.CTS_COST_CENTRE = G.ORG_CODE"
			+ " AND B.OWNER = H.USER_ID(+)"
			+ " AND D.WASH_NO = '"
			+ wash_no + "'" + " ORDER BY B.RFID";
	
	
//	System.out.println(sql);
	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//	System.out.println("result==="+result);
	TParm printParm = new TParm();
	Timestamp date = StringTool.getTimestamp(new Date());
	Timestamp start_date = result.getTimestamp("START_DATE", 0);
	Timestamp end_date = result.getTimestamp("END_DATE", 0);
	printParm.setData("TITLE1", "TEXT", Operator.getHospitalCHNFullName()+"ϴ�µǼ���ⵥ");
//	if (result.getValue("PAT_FLG", 0).equals("Y")) {
//		printParm.setData("TITLE2", "TEXT", "����ϴ�µǼǵ�");
//	} else {
//		printParm.setData("TITLE2", "TEXT", "Ա��ϴ�µǼǵ�");
//	}
	printParm.setData("PROGRAM", "TEXT", "ϴ�����");
	printParm.setData("PRINT_DATE", "TEXT", date.toString()
			.substring(0, 10).replaceAll("-", "/"));
	printParm.setData("PRINT_NO", "TEXT", wash_no);
	printParm.setData("DEPT_CHN_DESC", "TEXT", result.getValue(
			"DEPT_CHN_DESC", 0));
	
	TTextFormat station = (TTextFormat) getComponent("STATION_CODE");
//	printParm.setData("STATION_DESC", "TEXT", ""+station.getText());
	
	TTextFormat turnPoint = (TTextFormat) getComponent("TURN_POINT");
	printParm.setData("STATION_DESC", "TEXT", ""+turnPoint.getText());
	
//	printParm.setData("QTY", "TEXT", result.getValue("QTY", 0));
	printParm.setData("OUT_QTY", "TEXT", "�������: "+result.getValue("QTY", 0)+"��");
//	messageBox(""+start_date);
//	printParm.setData("START_DATE", "TEXT", start_date.toString()
//			.substring(0, 19).replaceAll("-", "/"));
	String str = start_date.toString()
			.substring(0, 19).replaceAll("-", "/");
	printParm.setData("END_DATE", "TEXT", "����ʱ�䣺 "+str);
	printParm.setData("WASH_CODE", "TEXT", result.getValue("WASH_CODE", 0));

	TParm clothParm = new TParm();

	int count = 1;
	String clothNo="";
	int resultCount=result.getCount("INV_CHN_DESC")-1;
//	System.out.println("resultCount==="+resultCount);
	for(int i=0; i<resultCount; i++){
//		System.out.println("CLOTH_NO1==="+result.getValue("CLOTH_NO", i));
//		System.out.println("CLOTH_NO2==="+result.getData("CLOTH_NO", i));
		if(result.getBoolean("PAT_FLG", i)){
			clothParm.addData("CLOTH_NO", result.getValue("CLOTH_NO", i));
			clothParm.addData("INV_CHN_DESC", result.getValue("INV_CHN_DESC", i));
			clothParm.addData("OWNER", result.getValue("USER_ID", i)+" "+result.getValue("OWNER", i));
			clothParm.addData("STATION_DESC", result.getValue("STATION_DESC", i));
			clothParm.addData("QTY", 1+"��");
			clothParm.addData("COMMON","");
		}else{
			String inv_code=result.getValue("INV_CHN_DESC", i);
			String station_desc=result.getValue("STATION_DESC", i);
			String turn_point=result.getValue("TURN_POINT", i);
			if(result.getValue("INV_CHN_DESC", i+1).equals(inv_code) && result.getValue("TURN_POINT", i+1).equals(turn_point)){
				count++;
				clothNo = clothNo + result.getValue("CLOTH_NO", i)+",";
			}else{
				clothParm.addData("CLOTH_NO", "");
				clothParm.addData("INV_CHN_DESC", result.getValue("INV_CHN_DESC", i));
				clothParm.addData("OWNER", "");
				clothParm.addData("STATION_DESC", result.getValue("STATION_DESC", i));
				clothParm.addData("QTY", count+"��");
				clothParm.addData("COMMON",clothNo+result.getValue("CLOTH_NO", i));
				count=1;
				clothNo="";
			}
		}
	}
	if(result.getBoolean("PAT_FLG", resultCount)){
		clothParm.addData("CLOTH_NO", result.getValue("CLOTH_NO", resultCount));
		clothParm.addData("INV_CHN_DESC", result.getValue("INV_CHN_DESC", resultCount));
		clothParm.addData("OWNER", result.getValue("USER_ID", resultCount)+" "+result.getValue("OWNER", resultCount));
		clothParm.addData("STATION_DESC", result.getValue("STATION_DESC", resultCount));
		clothParm.addData("QTY", 1+"��");
		clothParm.addData("COMMON","");
	}else{
		if(result.getValue("INV_CHN_DESC", resultCount).equals(result.getValue("INV_CHN_DESC", resultCount-1)) &&  result.getValue("TURN_POINT", resultCount).equals(result.getValue("TURN_POINT", resultCount-1))){
			clothParm.addData("CLOTH_NO", "");
			clothParm.addData("INV_CHN_DESC", result.getValue("INV_CHN_DESC", resultCount));
			clothParm.addData("OWNER", "");
			clothParm.addData("STATION_DESC", result.getValue("STATION_DESC", resultCount));
			clothParm.addData("QTY", count+"��");
			clothParm.addData("COMMON",clothNo+result.getValue("CLOTH_NO", resultCount));
		}else{
			clothParm.addData("CLOTH_NO", "");
			clothParm.addData("INV_CHN_DESC", result.getValue("INV_CHN_DESC", resultCount));
			clothParm.addData("OWNER", "");
			clothParm.addData("STATION_DESC", result.getValue("STATION_DESC", resultCount));
			clothParm.addData("QTY", 1+"��");
			clothParm.addData("COMMON",result.getValue("CLOTH_NO", resultCount));
		}
	}
	
	
	
	clothParm.setCount(clothParm.getCount("INV_CHN_DESC"));
	clothParm.addData("SYSTEM", "COLUMNS", "CLOTH_NO");
	clothParm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
	clothParm.addData("SYSTEM", "COLUMNS", "OWNER");
	clothParm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
	clothParm.addData("SYSTEM", "COLUMNS", "QTY");
	clothParm.addData("SYSTEM", "COLUMNS", "COMMON");
	printParm.setData("TABLE", clothParm.getData());

	this.openPrintWindow("%ROOT%\\config\\prt\\CTS\\CTSRegList1.jhw",
			printParm);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				String msg = txtMsg.getText();
//				System.out.println("Consumer:->Received: " + msg + "       "+System.currentTimeMillis());
				List<RFIDTag> lt = xml2rfidTag(msg);
				if (lt != null && lt.size() > 0) {
					for (RFIDTag rfidTag : lt) {
						String rifd = rfidTag.getEpc();
						// messageBox(rifd);
						rifd = rfidTag.getEpc();
						setValue("CLOTH_NO", rifd);
						onEnter();
//						if (set == null) {
//							set = new HashSet<String>();
//						}
//						if (set.contains(rifd)) {
//							continue;
//						} else {
//							set.add(rifd);
//						}
					}

				}
//				for (String s : set) {
//					setValue("CLOTH_NO", s);
//					System.out.println(s);
//					onEnter();
//				}
			} else {
				System.out.println("Consumer:->Received: " + message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

	/**
	 * ��xmlת��tag
	 * 
	 * @param temp
	 * @return
	 */
	private List<RFIDTag> xml2rfidTag(String temp) {
		List<RFIDTag> list = new ArrayList<RFIDTag>();
		/**
		 * <?xml version="1.0" encoding="UTF-8"
		 * ?><taglist><tag><epc>120034118801010865003102
		 * </epc><readerid>D001</readerid
		 * ><antenna>1</antenna><direction></direction><time>2007-08-31
		 * 18:20:34.863</time></tag></taglist>
		 */
		// temp =
		// "<?xml version='1.0' encoding='UTF-8' ?><taglist><tag><epc>120034118801010865003102</epc><readerid>D001</readerid><antenna>1</antenna><direction></direction><time>2007-08-31 18:20:34.863</time></tag></taglist>";

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		NodeList tags = null;
		try {
			builder = dbf.newDocumentBuilder();

			Document doc = builder
					.parse(new InputSource(new StringReader(temp))); // ��ȡ��xml�ļ�
			Element root = doc.getDocumentElement(); // ��ȡ��Ԫ��
			tags = root.getElementsByTagName("tag");
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//
		for (int i = 0; i < tags.getLength(); i++) {
			RFIDTag tag = new RFIDTag();
			// һ��ȡ��ÿһ��ѧ��Ԫ��
			Element ss = (Element) tags.item(i);
			NodeList names = ss.getElementsByTagName("epc");
			Element e = (Element) names.item(0);
			Node t = null;

			try {
				t = e.getFirstChild();
				// System.out.println("----epc----" + t.getNodeValue());
				tag.setEpc(t.getNodeValue());
			} catch (Exception e3) {
				tag.setEpc("");
			}
			//
			NodeList readerid = ss.getElementsByTagName("readerid");
			e = (Element) readerid.item(0);
			try {
				t = e.getFirstChild();
				// System.out.println("----readerid----" + t.getNodeValue());
				tag.setReaderid(t.getNodeValue());
			} catch (Exception e3) {
				tag.setReaderid("");
			}
			//
			NodeList antenna = ss.getElementsByTagName("antenna");
			e = (Element) antenna.item(0);
			try {
				t = e.getFirstChild();
				// System.out.println("----antenna----" + t.getNodeValue());
				tag.setAntenna(t.getNodeValue());
			} catch (Exception e3) {
				tag.setAntenna("");
			}
			//
			NodeList direction = ss.getElementsByTagName("direction");
			e = (Element) direction.item(0);
			try {
				// System.out.println("----e----" + e);
				t = e.getFirstChild();
				// System.out.println("----direction----" + t);
				tag.setDirection(t.getTextContent());
			} catch (Exception e3) {
				// System.out.println("----direction not null----");
				tag.setDirection("");
			}
			//
			NodeList time = ss.getElementsByTagName("time");
			e = (Element) time.item(0);
			try {
				t = e.getFirstChild();
				// System.out.println("----time----" + t.getNodeValue());
				tag.setDirection(t.getNodeValue());
			} catch (Exception e3) {
				tag.setDirection("");
			}
			//
			list.add(tag);
		}

		return list;
	}

	public void onStartListener() {
		final String mqIP = tparm.getValue(MQ_IP);
		if (mqIP == null || mqIP.length() == 0) {
			messageBox("MQ������IP���ò�������!");
			return;
		}

		this.connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, "tcp://" + mqIP + ":61616");
		try {
			this.connection = connectionFactory.createConnection();
			// ����
			connection.start();
			// ��ȡ��������
			session = connection.createSession(Boolean.FALSE,
					Session.AUTO_ACKNOWLEDGE);

			ActiveMQQueue topic = new ActiveMQQueue(mq_desc);
			this.consumer = this.session.createConsumer(topic, "2 > 1", false);//

			// ��ʼ����
			// ��������� ͬʱ���� ����Ҫ���ʱ���ԣ�
			this.consumer.setMessageListener(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			System.out.println("--------����ʧ��----------");
		}
		;

	}
	
	public void onQty(){
		
			setValue("QTY", cloth_nos.size());
		
		
	}
}
