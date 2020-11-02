package com.javahis.ui.spc;


import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;  
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import jdo.ind.IndSysParmTool;
import jdo.spc.INDTool;
import jdo.spc.SPCInStoreTool;
import jdo.spc.SPCNarTrunkBindTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.device.PullDriver;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:���ܹ������Ϣά��
 * </p>
 * 
 * <p>
 * Description:���ܹ������Ϣά��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author fuwj 2012.10.23
 * @version 1.0
 */
public class SPCStatioaInStoreControl extends TControl implements MessageListener{

	TTable TABLE_M_N;
	TTable TABLE_D_N;  
	TTable TABLE_M_Y;
	TTable TABLE_D_Y;
	TPanel PANEL_N;
	TPanel PANEL_Y;

	private TimerTask logTask; // log����

	private Timer logTimer;

	private long period = 1 * 1000; // ���ʱ��

	private long delay = 1*1000; // �ӳ�ʱ��								

	private static int i = 0;

	private String ip = ""; // ���ܹ�IP

	private String GUARD_IP = ""; // ���ܹ��Ž�IP

	private String RFID_IP = ""; // ���ܹ�RFID IP

	private String CABINET_ID = ""; // ���ܹ�ID

	private String request_type; // ��������

	private String request_no; // ���뵥��

	private String u_type; // ʹ�õ�λ

	private String out_org_code; // ���ⲿ��

	private String in_org_code; // ��ⲿ��

	private boolean out_flg; // �Ƿ����

	private boolean in_flg; // �Ƿ����

	private String dispense_no; // ��ⵥ��

	int seq = 1;

	private int rtn1;
	
	private TParm tparm=new TParm();
	
	private String WS_URL = "";
	
	private String MQ_QUEUE_NAME = "";
	
	// ConnectionFactory �����ӹ�����JMS ������������
	ConnectionFactory connectionFactory;
	// Connection ��JMS �ͻ��˵�JMS Provider ������
	Connection connection = null;
	// Session�� һ�����ͻ������Ϣ���߳�
	Session session;
	//
	MessageConsumer consumer;
	
	String MQ_IP="";
	
	/**
	 * ������Ϣ        
	 */
	public final static String RECEIVER_MSG = "RECEIVER_MSG";

	
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		TABLE_M_N = getTable("TABLE_M_N");
		TABLE_D_N = getTable("TABLE_D_N");
		TABLE_M_Y = getTable("TABLE_M_Y");
		TABLE_D_Y = getTable("TABLE_D_Y");
		initPage();
		// ��ʼ�����ܹ��Ž�
		int i = PullDriver.init();
		String params = "protocol=TCP,ipaddress=" + GUARD_IP
				+ ",port=4370,timeout=2000,passwd=";
		rtn1 = PullDriver.Connect(params);
		if (rtn1 <= 0) {
			this.messageBox("���ܹ��ʼ��ʧ��");
			// getTextField("DISPENSE_NO").setEnabled(false);
			// getTextField("BOX_ESL_ID").setEnabled(false);				
		}
	}
       
	/**
	 * ��ʼ��ҳ��
	 */
	public void initPage() {
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();// ��ñ���IP
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if ("".equals(ip)) {
			this.messageBox("��ȡ���ܹ���Ϣʧ��");
			return;
		}
		TParm parm = new TParm();   
		parm.setData("CABINET_IP", ip);  
		TParm result = SPCInStoreTool.getInstance().queryCabinet(parm);					
		CABINET_ID = (String) result.getData("CABINET_ID", 0);
		RFID_IP = (String) result.getData("RFID_IP", 0);
		GUARD_IP = (String) result.getData("GUARD_IP", 0);
		WS_URL = (String)result.getData("ZKRFID_URL",0);
		MQ_QUEUE_NAME = (String)result.getData("MQ_DESC",0);
		this.setValue("CABINET_ID", result.getData("CABINET_ID", 0));
		this.setValue("CABINET_DESC", result.getData("CABINET_DESC", 0));
		this.setValue("CABINET_IP", ip);	
			
		String mqIp = WS_URL.substring(WS_URL.indexOf("http://") + 7, WS_URL.indexOf(":8080"));
		MQ_IP = mqIp;
		tparm.setData("MQ_IP",mqIp);
		tparm.setData("READER_ID",CABINET_ID);
		tparm.setData("WS_URL",WS_URL);
		tparm.setData("MQ_QUEUE_NAME",MQ_QUEUE_NAME);
	}

	/**
	 * ������
	 */
	public SPCStatioaInStoreControl() {
		super();
	}


	/**
	 * ������־�߳�
	 */
	public void logSchedule() {
		this.logTimer = new Timer();
		this.logTask = new TimerTask() {
			public void run() {
				onLog();
			}
		};
		this.logTimer.schedule(this.logTask, this.delay, this.period);
	}
				
	/**
	 * ��־����
	 */				
	public void onLog() {																	
		String logStr = this.getLog();	
		String[] log = logStr.split(",");
		// ���������¼�
		if ("200".equals(log[4])) {     																			
			TParm logParm = new TParm();
			String dispenseNo = getValueString("DISPENSE_NO");
			logParm.setData("CABINET_ID", CABINET_ID);
			logParm.setData("LOG_TIME", SystemTool.getInstance().getDate());
			logParm.setData("TASK_TYPE", "4");
			logParm.setData("TASK_NO", dispenseNo);
			logParm.setData("EVENT_TYPE", "1");
			logParm.setData("GUARD_ID", log[3]);
			logParm.setData("OPT_USER", Operator.getID());
			logParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
			logParm.setData("OPT_TERM", Operator.getIP());
			SPCInStoreTool.getInstance().insertLog(logParm);			
		}				
		if ("201".equals(log[4])) {
			TParm logParm = new TParm();
			String dispenseNo = getValueString("DISPENSE_NO");
			logParm.setData("CABINET_ID", CABINET_ID);
			logParm.setData("LOG_TIME", SystemTool.getInstance().getDate());
			logParm.setData("TASK_TYPE", "4");
			logParm.setData("TASK_NO", dispenseNo);
			logParm.setData("EVENT_TYPE", "2");
			logParm.setData("GUARD_ID", log[3]);
			logParm.setData("OPT_USER", Operator.getID());
			logParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
			logParm.setData("OPT_TERM", Operator.getIP());
			SPCInStoreTool.getInstance().insertLog(logParm);
			onStart();              //�����������¼���ʼ��RFID����			
			logTimer.cancel();
			logTask.cancel();						
		}		
													
		/*onStart();              //�����������¼���ʼ��RFID����
		logTimer.cancel();
		logTask.cancel();*/		 		
	}
	
	private Set<String> set = new HashSet<String>();
	/**
	 * �龫ҩ�����ܹ�
	 */
	public void onInstore() {
						
		// ��ȡ��ȡ��RFID�ļ���
		TParm talbleparm = TABLE_M_N.getParmValue();
		TParm inserParm = new TParm();
		inserParm.setData("IS_STORE", "Y");
		String dispenseNo = getValueString("DISPENSE_NO");
		inserParm.setData("DISPENSE_NO", dispenseNo);
		inserParm.setData("CABINET_ID", CABINET_ID);
		int count = TABLE_M_N.getRowCount();
		// ���ݳ��ⵥ�Ų�ѯIND_DISPENSEM����Ϣ

		for (int i = 0; i < count; i++) {
			String containerId = (String) talbleparm.getData("CONTAINER_ID", i);
			inserParm.setData("CONTAINER_ID", containerId);
			if (set.contains(containerId)) {
				TParm result = TIOM_AppServer.executeAction(
						"action.spc.INDDispenseAction", "onInsertToxicStation", inserParm);
				if (result == null || result.getErrCode() < 0) {
					return;
				}
				TABLE_M_N.removeRow(i);
				TABLE_D_N.removeRowAll();
				if (TABLE_M_N.getRowCount() == 0) {
					onEnd();										
				}
				break;
			}
		}

	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		String dispenseNo = (String) getValue("DISPENSE_NO");
		String boxId = (String) getValue("BOX_ESL_ID");
		PANEL_N = (TPanel) getComponent("PANEL_N");
		PANEL_Y = (TPanel) getComponent("PANEL_Y");
		if (!StringUtil.isNullString(dispenseNo)
				|| !StringUtil.isNullString(boxId)) {
			TParm parm = new TParm();
			if (!StringUtil.isNullString(dispenseNo)) {
				parm.setData("DISPENSE_NO", dispenseNo);
			}
			if (!StringUtil.isNullString(boxId)) {
				parm.setData("BOX_ESL_ID", boxId);
			}
			if (PANEL_N.isShowing()) {
				parm.setData("IS_STORE", "N");
			} else if (PANEL_Y.isShowing()) {
				parm.setData("IS_STORE", "Y");
			}
			parm.setData("DRUG_CATEGORY", "1");
			TParm result = SPCInStoreTool.getInstance().queryInfo(parm);
			if (result.getCount() <= 0) {
				this.messageBox("û�в�ѯ������");
				if (PANEL_N.isShowing()) {
					TABLE_M_N.setParmValue(new TParm());
				}
				if (PANEL_Y.isShowing()) {
					TABLE_M_Y.setParmValue(new TParm());
				}
				return;
			}
			if (PANEL_N.isShowing()) {
				TABLE_M_N.setParmValue(result);
			} else if (PANEL_Y.isShowing()) {
				TABLE_M_Y.setParmValue(result);
			}
			if (!StringUtil.isNullString(boxId)) {
				this.setValue("DISPENSE_NO", result.getData("DISPENSE_NO", 0));
			}
			parm.setData("DISPENSE_NO", result.getData("DISPENSE_NO", 0));
			result = SPCInStoreTool.getInstance().queryOrg(parm);
			this.setValue("ORG_CODE", result.getData("ORG_CHN_DESC", 0));
			String type = (String) result.getData("REQTYPE_CODE", 0);
			String reqtypeCode = this.getReqtype(type);
			this.setValue("ORDER_TYPE", reqtypeCode);
			//getTextField("DISPENSE_NO").setEnabled(false);
			//getTextField("BOX_ESL_ID").setEnabled(false);
		}
		//logSchedule();
	}

	/**
	 * ��ѯ�������ܹ�����
	 */
	public void onStockInQuery() {
		String dispenseNo = (String) getValue("DISPENSE_NO");
		String boxId = (String) getValue("BOX_ESL_ID");
		PANEL_N = (TPanel) getComponent("PANEL_N");
		PANEL_Y = (TPanel) getComponent("PANEL_Y");
		if (!StringUtil.isNullString(dispenseNo)
				|| !StringUtil.isNullString(boxId)) {
			TParm parm = new TParm();
			if (!StringUtil.isNullString(dispenseNo)) {
				parm.setData("DISPENSE_NO", dispenseNo);
			}
			if (!StringUtil.isNullString(boxId)) {
				parm.setData("BOX_ESL_ID", boxId);
			}
			if (PANEL_N.isShowing()) {
				parm.setData("IS_STORE", "N");
			} else if (PANEL_Y.isShowing()) {
				parm.setData("IS_STORE", "Y");
			}
			parm.setData("DRUG_CATEGORY", "1");
			TParm result = SPCInStoreTool.getInstance().queryInfo(parm);
			if (result.getCount() <= 0) {
				this.messageBox("û�в�ѯ������");
				if (PANEL_N.isShowing()) {
					TABLE_M_N.setParmValue(new TParm());
				}
				if (PANEL_Y.isShowing()) {
					TABLE_M_Y.setParmValue(new TParm());
				}
				return;
			}
			if (PANEL_N.isShowing()) {
				TABLE_M_N.setParmValue(result);
			} else if (PANEL_Y.isShowing()) {
				TABLE_M_Y.setParmValue(result);
			}
			if (!StringUtil.isNullString(boxId)) {
				this.setValue("DISPENSE_NO", result.getData("DISPENSE_NO", 0));
			}
			parm.setData("DISPENSE_NO", result.getData("DISPENSE_NO", 0));
			result = SPCInStoreTool.getInstance().queryOrg(parm);
			this.setValue("ORG_CODE", result.getData("ORG_CHN_DESC", 0));
			String type = (String) result.getData("REQTYPE_CODE", 0);
			String reqtypeCode = this.getReqtype(type);
			this.setValue("ORDER_TYPE", reqtypeCode);
		}
	}

	/**
	 * ��ȡtable����
	 * 
	 * @param tableName
	 * @return
	 */
	public TTable getTable(String tableName) {
		return (TTable) this.getComponent(tableName);
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.setValue("DISPENSE_NO", "");
		this.setValue("BOX_ESL_ID", "");
		this.setValue("ORG_CODE", "");
		this.setValue("ORDER_TYPE", ""); 
		this.getTTable("TABLE_M_N").removeRowAll();
		this.getTTable("TABLE_D_N").removeRowAll();
		this.getTTable("TABLE_M_Y").removeRowAll();
		this.getTTable("TABLE_D_Y").removeRowAll();
	}

	/**
	 * �õ�TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * ɨ����ⵥ
	 */
	public void DisClick() {
		onQuery();
	}

	/**
	 * �����龫ҩ����
	 * 
	 * @param type
	 * @param ip
	 * @return
	 */
	private int openZNDor(int type, String ip) {
		int ret = 0;
		int operid = 1;
		int doorid = type;
		int outputadr = 1;
		int doorstate = 6;
		ret = PullDriver.ControlDevice(rtn1, operid, doorid, outputadr,
				doorstate, 0, "");
		if (ret < 0) {
			return 0;
		}
		return 1;
	}
	
	/**
	 * δ�����ܹ���table����¼�
	 */
	public void onTableMClicked() {
		int row = TABLE_M_N.getSelectedRow();
		TParm rowParm = TABLE_M_N.getParmValue();
		int seqNo = rowParm.getInt("DISPENSE_SEQ_NO", row);
		String disNo = (String) rowParm.getData("DISPENSE_NO", row);
		String containerNo = (String) rowParm.getData("CONTAINER_ID", row);
		TParm parm = new TParm();
		parm.setData("DISPENSE_SEQ_NO", seqNo);
		parm.setData("DISPENSE_NO", disNo);
		parm.setData("CONTAINER_ID", containerNo);
		TParm result = SPCNarTrunkBindTool.getInstance().queryNarTrunkBindMX(
				parm);
		TABLE_D_N.removeRowAll();
		TABLE_D_N.setParmValue(result);
	}
	
	/**
	 * �������ܹ���table����¼�
	 */
	public void onTableMClicked1() {
		int row = TABLE_M_Y.getSelectedRow();
		TParm rowParm = TABLE_M_Y.getParmValue();
		int seqNo = rowParm.getInt("DISPENSE_SEQ_NO", row);
		String disNo = (String) rowParm.getData("DISPENSE_NO", row);
		String containerNo = (String) rowParm.getData("CONTAINER_ID", row);
		TParm parm = new TParm();
		parm.setData("DISPENSE_SEQ_NO", seqNo);
		parm.setData("DISPENSE_NO", disNo);
		parm.setData("CONTAINER_ID", containerNo);
		TParm result = SPCNarTrunkBindTool.getInstance().queryNarTrunkBindMX(
				parm);
		TABLE_D_Y.removeRowAll();
		TABLE_D_Y.setParmValue(result);
	}

	/**
	 * �õ��������͡�
	 * 
	 * @param reqtype
	 * @return
	 */
	private String getReqtype(String reqtype) {
		if (StringUtil.isNullString(reqtype)) {
			return "";
		}
		if (reqtype.equals("DEP")) {
			return "��������";
		} else if (reqtype.equals("TEC")) {
			return "��ҩ����";
		} else if (reqtype.equals("EXM")) {
			return "����Ʒ�";
		} else if (reqtype.equals("GIF")) {
			return "ҩ������";
		} else if (reqtype.equals("RET")) {
			return "�˿�";
		} else if (reqtype.equals("WAS")) {
			return "���";
		} else if (reqtype.equals("THO")) {
			return "��������";
		} else if (reqtype.equals("COS")) {
			return "���Ĳ�����";
		} else if (reqtype.equals("THI")) {
			return "�������";
		} else if (reqtype.equals("EXM")) {
			return "���ұ�ҩ";
		}
		return "";
	}
	
	/**
	 * ���ܹ���
	 * @param type
	 */
	public void onOpen(int type) {
		// 5.���Ų��� ��־���������豸������ (�ɹ�)
		int ret = 0;
		int operid = 1;
		int doorid = type;
		int outputadr = 1;
		int doorstate = 6;
		ret = PullDriver.ControlDevice(rtn1, operid, doorid, outputadr,
				doorstate, 0, "");
	}
	
	/**
	 * ����־
	 * @return
	 */
	private String getLog() {

		byte[] data = new byte[256];
		int rtn2 = PullDriver.GetRTLog(rtn1, data, 256);
		if (rtn1 < 0) {
		}
		String strData = byte2Str(data);
		return strData;
	}

	/**
	 * �� byteת�ɷ���
	 */
	private static String byte2Str(byte[] Data) {
		String Total_Data = "";
		for (int i = 0; i < Data.length; i++) {
			Character CWord = new Character((char) Data[i]);
			Total_Data = Total_Data + CWord.toString();
		}
		return Total_Data;
	}
  
	/**
	 * ���淽��  
	 */
	public void onSave() {	
	//	onEnd();
/*		TABLE_M_N = getTable("TABLE_M_N");
		int num = TABLE_M_N.getParmValue().getCount();
		onEnd();
		if (num <= 0) {
			this.messageBox("ҩƷȫ�������ܹ�ɹ�");
			return;
		}
		String msg = "����" + num + "������δ�����ܹ�";
		this.messageBox(msg);			
*/		
		// ��ȡ��ȡ��RFID�ļ���
		TParm talbleparm = TABLE_M_N.getParmValue();
		TParm inserParm = new TParm();
		inserParm.setData("IS_STORE", "Y");
		String dispenseNo = getValueString("DISPENSE_NO");
		inserParm.setData("DISPENSE_NO", dispenseNo);
		inserParm.setData("CABINET_ID", CABINET_ID);
		int count = TABLE_M_N.getRowCount();
		// ���ݳ��ⵥ�Ų�ѯIND_DISPENSEM����Ϣ

		for (int i = 0; i < count; i++) {
			String containerId = (String) talbleparm.getData("CONTAINER_ID", i);
			inserParm.setData("CONTAINER_ID", containerId);
				TParm result = TIOM_AppServer.executeAction(
						"action.spc.INDDispenseAction", "onInsertToxicStation", inserParm);
				if (result == null || result.getErrCode() < 0) {
					return;
				}			
														
				TABLE_M_N.removeRow(i);
				TABLE_D_N.removeRowAll();
				if (TABLE_M_N.getRowCount() == 0) {								
				}   
				break;
			}
		this.messageBox("����ɹ�");
	}

	
	/**
	 * �ر���Դ
	 */
	public void onCloseCabinet() {
	//	PullDriver.Disconnect(rtn1);
		// ע��dll
	//	PullDriver.free();									
	//	onEnd();							
		this.closeWindow();				
	}     
	
	/**
	 * ��ѯ���ܹ�ҩƷ��Ϣ
	 */
	public void onSearch() {
		TParm parm = new TParm();
		parm.setData("CABINET_ID", CABINET_ID);
		Object result = openDialog("%ROOT%\\config\\spc\\SPCContainerStatic.x",
				parm);
	}




	/**
	 * �����ҵ
	 * 
	 * @param out_org_code
	 * @param in_org_code
	 * @param batchvalid
	 */
	private void getDispenseOutIn(String out_org_code, String in_org_code,
			String batchvalid, boolean out_flg, boolean in_flg, TParm mParm,
			TParm dParm, TParm insertParm) {

		TParm parm = new TParm();

		parm.setData("DISPENSE_NO", insertParm.getData("DISPENSE_NO"));
		parm.setData("CONTAINER_ID", insertParm.getData("CONTAINER_ID"));
		parm.setData("CABINET_ID", insertParm.getData("CABINET_ID"));
		parm.setData("IS_STORE", insertParm.getData("IS_STORE"));
		// ������Ϣ(OUT_M)
		parm = getDispenseMParm(parm, "3", mParm, dParm);

		// ϸ����Ϣ(OUT_D)
		if (!CheckDataD(dParm)) {
			return;
		}
		parm = getDispenseDParm(parm, dParm);

		if (!"".equals(in_org_code)) {
			// ��ⲿ��(IN_ORG)
			parm.setData("IN_ORG", in_org_code);
			// �Ƿ����(IN_FLG)
			parm.setData("IN_FLG", in_flg);
		}
		// ִ����������
		TParm result = TIOM_AppServer.executeAction(
				"action.spc.INDDispenseAction", "onInsertToxic", parm);

		// �����ж�
		if (result == null || result.getErrCode() < 0) {
			//this.messageBox("E0001");
			return;
		}
	}

	/**
	 * ���������Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	private TParm getDispenseMParm(TParm parm, String update_flg, TParm mParm,
			TParm dparm) {
		// ҩ�������Ϣ
		TParm sysParm = getSysParm();
		// �Ƿ��д����۸�
		String reuprice_flg = sysParm.getValue("REUPRICE_FLG", 0);
		parm.setData("REUPRICE_FLG", reuprice_flg);

		TParm parmM = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		TNull tnull = new TNull(Timestamp.class);

		parmM.setData("DISPENSE_NO", mParm.getValue("DISPENSE_NO"));

		// ���뷽ʽ--ȫ��:APP_ALL,�˹�:APP_ARTIFICIAL,���콨��:APP_PLE,�Զ��β�:APP_AUTO
		parmM.setData("REQTYPE_CODE", mParm.getValue("REQTYPE_CODE"));
		parmM.setData("REQUEST_NO", mParm.getValue("REQUEST_NO"));
		parmM.setData("REQUEST_DATE", mParm.getValue("REQUEST_DATE"));
		parmM.setData("APP_ORG_CODE", mParm.getValue("APP_ORG_CODE"));
		parmM.setData("TO_ORG_CODE", mParm.getValue("TO_ORG_CODE"));
		parmM.setData("URGENT_FLG", mParm.getValue("URGENT_FLG"));
		parmM.setData("DESCRIPTION", mParm.getValue("DESCRIPTION"));
		parmM.setData("DISPENSE_DATE", mParm.getValue("WAREHOUSING_DATE"));
		parmM.setData("DISPENSE_USER", Operator.getID());

		if (!"1".equals(update_flg)) {
			parmM.setData("WAREHOUSING_DATE", mParm
					.getValue("WAREHOUSING_DATE"));
			parmM.setData("WAREHOUSING_USER", Operator.getID());
		} else {
			parmM.setData("WAREHOUSING_DATE", tnull);
			parmM.setData("WAREHOUSING_USER", "");
		}
		// ҩƷ����--��ҩ:1,�龫��2
		parmM.setData("DRUG_CATEGORY", "1");

		parmM.setData("REASON_CHN_DESC", "");
		parmM.setData("UNIT_TYPE", mParm.getValue("UNIT_TYPE"));
		if (dparm.getDouble("OUT_QTY") + dparm.getDouble("INSTORE_QTY") >= dparm
				.getDouble("ACTUAL_QTY")
				&& mParm.getInt("ORDERCOUNT") == 1) {
			update_flg = "3";
		} else {
			update_flg = "1";
		}
		parmM.setData("UPDATE_FLG", update_flg);
		parmM.setData("OPT_USER", Operator.getID());
		parmM.setData("OPT_DATE", date);
		parmM.setData("OPT_TERM", Operator.getIP());
		parmM.setData("REGION_CODE", Operator.getRegion());
		if (parmM != null) {
			parm.setData("OUT_M", parmM.getData());
		}
		return parm;
	}

	/**
	 * �����ϸ��Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	private TParm getDispenseDParm(TParm parm, TParm rowParm) {
		TParm parmD = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		TNull tnull = new TNull(Timestamp.class);
		String batch_no = "";
		int count = 0;
		String order_code = "";
		String valid_date = "";

		parmD.setData("DISPENSE_NO", count, rowParm.getValue("DISPENSE_NO"));
		parmD.setData("SEQ_NO", count, rowParm.getValue("SEQ_NO"));
		parmD.setData("REQUEST_SEQ", count, rowParm.getInt("REQUEST_SEQ"));
		order_code = rowParm.getValue("ORDER_CODE");
		parmD.setData("ORDER_CODE", count, order_code);
		parmD.setData("QTY", count, rowParm.getDouble("QTY"));
		parmD.setData("UNIT_CODE", count, rowParm.getValue("UNIT_CODE"));
		parmD.setData("RETAIL_PRICE", count, rowParm.getDouble("RETAIL_PRICE"));
		parmD.setData("STOCK_PRICE", count, rowParm.getDouble("STOCK_PRICE"));
		parmD.setData("ACTUAL_QTY", count, rowParm.getDouble("ACTUAL_QTY"));
		parmD.setData("PHA_TYPE", count, rowParm.getValue("PHA_TYPE"));

		parmD.setData("BATCH_SEQ", count, rowParm.getInt("BATCH_SEQ"));
		
		parmD.setData("SUP_CODE", count, rowParm.getValue("SUP_CODE"));
		
		parmD.setData("REGION_CODE", count, Operator.getRegion());

		batch_no = rowParm.getValue("BATCH_NO");
		parmD.setData("BATCH_NO", count, batch_no);
		valid_date = rowParm.getValue("VALID_DATE");
		if ("".equals(valid_date)) {
			parmD.setData("VALID_DATE", count, tnull);
		} else {
			parmD.setData("VALID_DATE", count, rowParm
					.getTimestamp("VALID_DATE"));
		}
		parmD.setData("DOSAGE_QTY", count, rowParm.getDouble("DOSAGE_QTY"));
		parmD.setData("INSTORE_QTY", count, rowParm.getDouble("OUT_QTY"));
		parmD.setData("OLD_INSTORE_QTY", count, rowParm
				.getDouble("INSTORE_QTY"));
		parmD.setData("STOCK_QTY", count, rowParm.getDouble("STOCK_QTY"));
		parmD.setData("OPT_USER", count, Operator.getID());
		parmD.setData("OPT_DATE", count, date);
		parmD.setData("OPT_TERM", count, Operator.getIP());
		parmD.setData("SUP_CODE", count, rowParm.getValue("SUP_CODE"));
		parmD.setData("INVENT_PRICE", count, rowParm.getDouble("INVENT_PRICE"));								
		count++;
		if (parmD != null) {
			parm.setData("OUT_D", parmD.getData());
		}
		return parm;
	}

	/**
	 * ҩ�������Ϣ
	 * 
	 * @return TParm
	 */
	private TParm getSysParm() {
		return IndSysParmTool.getInstance().onQuery();
	}

	/**
	 * ���ݼ���
	 * 
	 * @return
	 */
	private boolean CheckDataD(TParm dParm) {
		double qty = dParm.getDouble("OUT_QTY");
		if (qty <= 0) {
			this.messageBox("�������������С�ڻ����0");
			return false;
		}
		double price = dParm.getDouble("STOCK_PRICE");
		if (price <= 0) {
			this.messageBox("�ɱ��۲���С�ڻ����0");
			return false;
		}

		return true;
	}

	/**
	 * ���������ҵ
	 * 
	 * @param in_org_code
	 *            String
	 * @param flg
	 *            boolean
	 */
	private void getDispenseOtherIn(String in_org_code, boolean in_flg,
			TParm mParm, TParm dParm) {

		TParm parm = new TParm();
		parm.setData("ORG_CODE", in_org_code);
		// ʹ�õ�λ
		parm.setData("UNIT_TYPE", u_type);
		// ���뵥����
		parm.setData("REQTYPE_CODE", request_type);

		parm = getDispenseMParm(parm, "3", mParm, dParm);
		// ϸ����Ϣ(OUT_D)
		if (!CheckDataD(dParm)) {
			return;
		}
		parm = getDispenseDParm(parm, dParm);

		if (!"".equals(in_org_code)) {
			// ��ⲿ��(IN_ORG)
			parm.setData("IN_ORG", in_org_code);
			// �Ƿ����(IN_FLG)
			parm.setData("IN_FLG", in_flg);
		}
		// ִ����������
		parm = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
				"onInsertOtherIn", parm);
		// �����ж�
		if (parm == null || parm.getErrCode() < 0) {
			this.messageBox("E0001");
			return;
		}
		this.messageBox("P0001");
		onClear();
	}

	/**
	 * ������͸�ֵ
	 * 
	 * @param mParm
	 */
	public void setValueAll(TParm mParm) {
		request_type = mParm.getValue("REQTYPE_CODE");	
		if ("TEC".equals(request_type) || "EXM".equals(request_type)
				|| "COS".equals(request_type)) {
			u_type = "1";
		} else if ("DEP".equals(request_type)) {
			u_type = IndSysParmTool.getInstance().onQuery().getValue(
					"UNIT_TYPE", 0);
		} else {
			u_type = "0";
		}
		if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
			out_org_code = mParm.getValue("TO_ORG_CODE");
			out_flg = true;
			in_org_code = mParm.getValue("APP_ORG_CODE");
			in_flg = true;
		} else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
			out_org_code = mParm.getValue("APP_ORG_CODE");
			out_flg = true;	
			in_org_code = mParm.getValue("TO_ORG_CODE");
			in_flg = true;
		} else if ("EXM".equals(request_type) || "COS".equals(request_type)) {
			out_org_code = mParm.getValue("TO_ORG_CODE");
			out_flg = true;
			in_org_code = mParm.getValue("APP_ORG_CODE");
			in_flg = false;
		} else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
			out_org_code = mParm.getValue("APP_ORG_CODE");
			out_flg = true;
			in_org_code = mParm.getValue("TO_ORG_CODE");
			in_flg = false;
		} else if ("THI".equals(request_type)) {
			out_org_code = mParm.getValue("TO_ORG_CODE");
			out_flg = false;
			in_org_code = mParm.getValue("APP_ORG_CODE");
			in_flg = true;
		}
	}

	/**
	 * �õ�TextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}
	
	/**
	 * ��ʼɨ��
	 */
    public void onStart(){	
    	TIOM_AppServer.executeAction("action.device.RFIDAction",
				"connect", tparm);
    	connectionFactory  = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, "tcp://"+MQ_IP+":61616");
        try {				
			this.connection = connectionFactory.createConnection();
			// ����
			connection.start();
			// ��ȡ��������
			session = connection.createSession(Boolean.FALSE,	
					Session.AUTO_ACKNOWLEDGE);
			
			ActiveMQQueue topic = new ActiveMQQueue(MQ_QUEUE_NAME);										
			this.consumer=this.session.createConsumer(topic, "2 > 1", false);//
			
			//��ʼ����
			//���������     ͬʱ����  ����Ҫ���ʱ���ԣ�
			this.consumer.setMessageListener(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
    	
    } 
	
	/**
	 * ֹͣ����
	 */
    public void onEnd(){
    	try {
    		if (consumer != null)   consumer.close();  
    		if (session != null)  this.session.close();
    		if (connection != null) this.connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		TIOM_AppServer.executeAction("action.device.RFIDAction",
				"disconnect", tparm);
		PullDriver.Disconnect(rtn1);
		// ע��dll
		PullDriver.free();
		
    }
	
	/**
	 * ��xmlת��tag
	 * 
	 * @param temp
	 * @return
	 */
	private List<RFIDTag> xml2rfidTag(String temp) {
		List<RFIDTag> list = new ArrayList<RFIDTag>();
		//temp = "<?xml version='1.0' encoding='UTF-8' ?><taglist><tag><epc>120034118801010865003102</epc><readerid>D001</readerid><antenna>1</antenna><direction></direction><time>2007-08-31 18:20:34.863</time></tag></taglist>";
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
				tag.setEpc(t.getNodeValue());
			} catch (Exception e3) {
				tag.setEpc("");
			} 
			//
			NodeList readerid = ss.getElementsByTagName("readerid");
			e = (Element) readerid.item(0);
			try {
				t = e.getFirstChild();
				tag.setReaderid(t.getNodeValue());
			} catch (Exception e3) {
				tag.setReaderid("");
			}
			//
			NodeList antenna = ss.getElementsByTagName("antenna");
			e = (Element) antenna.item(0);
			try {
				t = e.getFirstChild();
				tag.setAntenna(t.getNodeValue());
			} catch (Exception e3) {
				tag.setAntenna("");
			}  
			NodeList direction = ss.getElementsByTagName("direction");
			e = (Element) direction.item(0);
			try {
				t = e.getFirstChild();
				tag.setDirection(t.getTextContent());
			} catch (Exception e3) {
				tag.setDirection("");
			}
			NodeList time = ss.getElementsByTagName("time");
			e = (Element) time.item(0);
			try {
				t = e.getFirstChild();
				tag.setDirection(t.getNodeValue());
			} catch (Exception e3) {
				tag.setDirection("");
			}
			list.add(tag);
		}

		return list;
	}
	
	/**
	 * MQ��������
	 */
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		try {
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				String msg = txtMsg.getText();
				List<RFIDTag> tagList = this.xml2rfidTag(msg);
				for(RFIDTag tag : tagList){   
					   String c = tag.getEpc();					
					    if (set.contains(c)) {
							continue;				
						}else {
							set.add(c);
						}
				} 
				onInstore();		  
			} else {				   
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
					
}
