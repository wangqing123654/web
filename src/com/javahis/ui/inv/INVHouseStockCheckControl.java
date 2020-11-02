package com.javahis.ui.inv;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

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

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

  
/**
 *   
 * @author lit
 * 
 */
public class INVHouseStockCheckControl extends TControl  implements MessageListener{ 
    public INVHouseStockCheckControl() {  
    }  
	/**  
	 * ������Ϣ      
	 */
	public final static String RECEIVER_MSG = "RECEIVER_MSG";
	
	/**
	 * MQ��Ϣ����IP��ַ  
	 */
	public final static String MQ_IP = "MQ_IP";
	/**
	 * ��������
	 */
	public final static String MQ_QUEUE_NAME = "MQ_QUEUE_NAME";

	TLabel tlabTip;
	private TParm tparm=new TParm();
	boolean flg;
    private TTable table_dd;//���ϸ�� 

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
    }
  
    /**
     * ��շ��� 
     */
    public void onClear() {
        getTable("TABLE_DD").removeRowAll();  
        mainMap=new TreeMap<String, Integer>();
        mainParm=new TParm();
        map=new HashMap<String, String>();
    } 

  
    /**  
     * ���淽��
     */  
    public void onSave() { 
    	
    }


    
    

 
    
    Map<String, Map<String,String>>  invMap=new HashMap<String, Map<String,String>>();
    private Map<String, Map<String,String>>  getInvMap() {
    	  String sql = " SELECT B.INV_CODE, B.INV_CHN_DESC ,B.ORDER_CODE, " +
          " B.DESCRIPTION,C.SUP_CHN_DESC,D.MAN_CHN_DESC  "+ 
          " FROM INV_BASE B"+
          " left join SYS_MANUFACTURER D on B.MAN_CODE=D.MAN_CODE"+
          " left join SYS_SUPPLIER C ON C.SUP_CODE=B.UP_SUP_CODE"+
          " WHERE   B.INV_CODE like '08%'";
         TParm  parm = new TParm(TJDODBTool.getInstance().select(sql));
         Map<String, Map<String,String>> mainMap=new HashMap<String, Map<String,String>>();
         
         for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
        	 Map<String, String> map=new HashMap<String, String>();
        	map.put("INV_CHN_DESC", parm.getValue("INV_CHN_DESC", i)); 
        	map.put("ORDER_CODE", parm.getValue("ORDER_CODE", i)); 
        	map.put("DESCRIPTION", parm.getValue("DESCRIPTION", i)); 
        	map.put("SUP_CHN_DESC", parm.getValue("SUP_CHN_DESC", i)); 
        	map.put("MAN_CHN_DESC", parm.getValue("MAN_CHN_DESC", i)); 
        	mainMap.put( parm.getValue("INV_CODE", i), map);
		}
         return mainMap;
	}
    
    private Map<String,String> getRfidMap() {
  	  String sql = " SELECT B.RFID, B.INV_CODE  " +
        " FROM INV_STOCKDD B"+
        " WHERE   B.INV_CODE like '08%' and WAST_FLG='N'";
       TParm  parm = new TParm(TJDODBTool.getInstance().select(sql));
       Map<String, String> map=new HashMap<String, String>();
       for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
      	map.put(parm.getValue("RFID", i), parm.getValue("INV_CODE", i)); 
		}
       return map;
	}
    private String getStockQty() {
    	  String sql = " SELECT count(B.RFID) as QTY" +
          " FROM INV_STOCKDD B"+
          " WHERE   B.CABINET_ID= 'ZNW' and WAST_FLG='N'";
         TParm  parm = new TParm(TJDODBTool.getInstance().select(sql));
         return parm.getRow(0).getValue("QTY");
  	}
   
    
    
    /**
     * ��ʼ��������
     */
    private void initPage() {
    	TButton b=(TButton)getComponent("end");
    	b.setEnabled(false);
    	
    	rfidMap=getRfidMap();
    	invMap=getInvMap();
    	getTextField("QTY").setValue(getStockQty());
    } 
     
    /**
	 * ����EXECL
	 */
	public void onExecl() {
		ExportExcelUtil.getInstance().exportExcel(this.getTable("TABLE_DD"),
				"����ͳ�Ʊ�");
	}
     
    /**
     * ɨ�迪ʼ�¼�
     * ��ʼ�����ܹ��ֶθ�ֵ
     */
    public void onStart(){ 
    	
    	onClear();
    	TButton a=(TButton)getComponent("start");
    	a.setEnabled(false);
    	TButton b=(TButton)getComponent("end");
    	b.setEnabled(true);
    	list=new ArrayList<MessageConsumer>();
		listConnection=new ArrayList<Connection>();
		listSession=new ArrayList<Session>();
		String cabsql  = " SELECT CABINET_ID,CABINET_IP," +
		" ZKRFID_URL,MQ_DESC" + 
		" FROM IND_CABINET " +   
		" where ACTIVE_FLG='Y' AND CABINET_TYPE='03'" ;
		TParm mparm= new TParm(TJDODBTool.getInstance().select(cabsql));
		tparm=new TParm();
		for (int i = 0; i < mparm.getCount("CABINET_ID"); i++) {
			tparm.addData("READER_ID", mparm.getData("CABINET_ID",i)); 
			tparm.addData("TERM_IP", mparm.getData("CABINET_IP",i)); 
			tparm.addData("WS_URL", mparm.getData("ZKRFID_URL",i)); 
			tparm.addData(MQ_QUEUE_NAME, mparm.getData("MQ_DESC",i)); 
			
			String s = mparm.getData("ZKRFID_URL",i).toString();
			s = s.substring(s.indexOf("http://") + 7, s.indexOf(":8080"));
			tparm.addData(MQ_IP,s);
			
		}
	
		
		
		
		for( int i=0;i< tparm.getCount("READER_ID");i++){
			//ִ��ɨ��
			TParm myParm=new TParm();
			myParm.setData("READER_ID", tparm.getData("READER_ID",i));
			myParm.setData("WS_URL",  tparm.getData("WS_URL",i));
				TIOM_AppServer.executeAction("action.device.RFIDAction",
						"purgeQueue", myParm);
					
				
			  
			  if ( TIOM_AppServer.executeAction("action.device.RFIDAction",
						"connect", myParm).getErrCode()==-1) {
					messageBox("�豸�����쳣   ���飡");
					return;
					
				};
		}
	  
	    
	    this.onStartListener();
		
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
				//System.out.println("----epc----" + t.getNodeValue());
				tag.setEpc(t.getNodeValue());
			} catch (Exception e3) {
				tag.setEpc("");
			} 
			//
			NodeList readerid = ss.getElementsByTagName("readerid");
			e = (Element) readerid.item(0);
			try {
				t = e.getFirstChild();
				//System.out.println("----readerid----" + t.getNodeValue());
				tag.setReaderid(t.getNodeValue());
			} catch (Exception e3) {
				tag.setReaderid("");
			}
			//
			NodeList antenna = ss.getElementsByTagName("antenna");
			e = (Element) antenna.item(0);
			try {
				t = e.getFirstChild();
				//System.out.println("----antenna----" + t.getNodeValue());
				tag.setAntenna(t.getNodeValue());
			} catch (Exception e3) {
				tag.setAntenna("");
			}  
			//
			NodeList direction = ss.getElementsByTagName("direction");
			e = (Element) direction.item(0);
			try {
				//System.out.println("----e----" + e);  
				t = e.getFirstChild();
				//System.out.println("----direction----" + t);
				tag.setDirection(t.getTextContent());
			} catch (Exception e3) {
				//System.out.println("----direction not null----");
				tag.setDirection("");
			}
			//
			NodeList time = ss.getElementsByTagName("time");
			e = (Element) time.item(0);
			try {
				t = e.getFirstChild();
				//System.out.println("----time----" + t.getNodeValue());
				tag.setDirection(t.getNodeValue());
			} catch (Exception e3) {
				tag.setDirection("");
			}
			//
			list.add(tag);
		}

		return list;
	}  
	Map<String, String> rfidMap=new HashMap<String, String>();
	Map<String, Integer> mainMap=new TreeMap<String, Integer>();
	Map<String, List<String>> ddMap=new HashMap<String,List<String>>();
	TParm mainParm=new TParm();
	private void tableddChange() {
	
		for (String string:map.keySet()){
			String invcode=rfidMap.get(string);
			if (invcode!=null&&!"".equals(invcode)) {
				if (mainMap.containsKey(invcode)) {
					int m=mainMap.get(invcode);
					mainMap.put(invcode, m+1);
				}else {
					mainMap.put(invcode, 1);
				}
				if (ddMap.containsKey(invcode)) {
					ddMap.get(invcode).add(string);
				}else {
					List<String> list=new ArrayList<String>();
					list.add(string);
					ddMap.put(invcode, list);
				}
			}
		}
		int row=0;
		for (String string : mainMap.keySet()) {
			int qty= mainMap.get(string);
			Map<String, String> map=invMap.get(string);
			mainParm.setData("INV_CHN_DESC", row, map.get("INV_CHN_DESC"));
			mainParm.setData("SUP_CHN_DESC", row,map.get("SUP_CHN_DESC"));
			mainParm.setData("MAN_CHN_DESC", row,map.get("MAN_CHN_DESC"));
			mainParm.setData("DESCRIPTION", row,map.get("DESCRIPTION"));
			mainParm.setData("ORDER_CODE", row,map.get("ORDER_CODE"));
			mainParm.setData("QTY", row,qty);
			mainParm.setData("INV_CODE", row,string);
			row++;
		}
		getTable("TABLE_DD").setParmValue(mainParm);
		
		
	}
    /**  
     * ɨ��ֹͣ�¼�
     * ֹͣ�����ܹ��ֶθ�ֵ
     */
    public void onEnd(){ 
    	
    	TButton a=(TButton)getComponent("start");
    	a.setEnabled(true);
    	TButton b=(TButton)getComponent("end");
    	b.setEnabled(false);
    	
    	tableddChange();   //��Ҫ����
    		for( int i=0;i< tparm.getCount("READER_ID");i++){
    			//ִ��ɨ��
    			TParm myParm=new TParm();
    			myParm.setData("READER_ID", tparm.getData("READER_ID",i));
    			myParm.setData("WS_URL",  tparm.getData("WS_URL",i));
    			TIOM_AppServer.executeAction("action.device.RFIDAction",
    					"disconnect", myParm);
    		}
    	  	try {
    	  		for (MessageConsumer c:list) {
    	  			if (c != null)   c.close();  
    			}
    	  		for (Session c:listSession) {
    	  			if (c != null)   c.close();  
    			}
    	  		for (Connection c:listConnection) {
    	  			if (c != null)   c.close();  
    			}
    			System.out.println("--------�Ͽ��ɹ�----------");
    		} catch (JMSException e) {
    			e.printStackTrace();
    			System.out.println("--------�Ͽ�ʧ��----------");
    		}

    } 

    
    
    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

    /**
     * �õ�TCheckBox����
     * @param tagName String
     * @return TCheckBox
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * �õ�TTextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    
    /**
     * ������������ط���
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return  
     */
    private void compare() {
		// �����parmֵһ��,
		// 1.ȡparamwֵ;
		TParm tableData = getTable("TABLE").getParmValue();
		// 2.ת�� vector����, ��vector ;
		String columnName[] = tableData.getNames("Data");
		String strNames = "";
		for (String tmp : columnName) {
			strNames += tmp + ";";
		}
		strNames = strNames.substring(0, strNames.length() - 1);
		// System.out.println("==strNames=="+strNames);
		Vector vct = getVector(tableData, "Data", strNames, 0);
		// System.out.println("==vct=="+vct);

		// 3.���ݵ������,��vector����
		// System.out.println("sortColumn===="+sortColumn);
		// ������������;
		String tblColumnName = getTable("TABLE").getParmMap(0);
		// ת��parm�е���
		int col = tranParmColIndex(columnName, tblColumnName);
		// System.out.println("==col=="+col);
		Compare compare=new Compare();
		compare.setDes(false);
		compare.setCol(col);
		java.util.Collections.sort(vct, compare);
		// ��������vectorת��parm;
		cloneVectoryParam(vct, new TParm(), strNames);

		// getTMenuItem("save").setEnabled(false);
	}
	
	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTable("TABLE").setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}
		
	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}
	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}
	List<MessageConsumer> list=new ArrayList<MessageConsumer>();
	List<Session> listSession=new ArrayList<Session>();
	List<Connection> listConnection=new ArrayList<Connection>();
	List<ConnectionFactory> listConnectionFactory=new ArrayList<ConnectionFactory>();
    public void onStartListener(){
    	
    	
    	try {
			
			for (int i = 0; i < tparm.getCount(MQ_QUEUE_NAME); i++) {
		    	
		      String mqIP = tparm.getData(MQ_IP,i).toString();
				if (mqIP == null || mqIP.length() == 0) {
					messageBox("MQ������IP���ò�������!");
					return ;
				}
			

				ConnectionFactory myconnectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,ActiveMQConnection.DEFAULT_PASSWORD, "tcp://"+mqIP+":61616");
				Connection myconnection = myconnectionFactory.createConnection();
				listConnection.add(myconnection);
				// ����
				myconnection.start();
				// ��ȡ��������
				 Session mysession = myconnection.createSession(Boolean.FALSE,
						Session.AUTO_ACKNOWLEDGE);
				 listSession.add(mysession);
				ActiveMQQueue topic = new ActiveMQQueue(tparm.getData(MQ_QUEUE_NAME, i).toString());
				MessageConsumer consumer=mysession.createConsumer(topic, "2 > 1", false);//
				list.add(consumer);
				
				
				//��ʼ����
				//���������     ͬʱ����  ����Ҫ���ʱ���ԣ�
				consumer.setMessageListener(this);
				
			}
		
			
			
			
//			//---------------------
//			ActiveMQQueue topic2 = new ActiveMQQueue("amq.D012");
//			MessageConsumer consumer2=this.session.createConsumer(topic2, "2 > 1", false);//
//			
//			//��ʼ����
//			//���������     ͬʱ����  ����Ҫ���ʱ���ԣ�
//			consumer2.setMessageListener(this);
//			
//			System.out.println("--------�����ɹ�----------");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("--------����ʧ��----------");
		}
		};

	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	Map<String,String> map=new HashMap<String, String>();
	
	
	
	 /**
     * ϸ����(TABLE_D)�����¼�
     */
    public void onTableDClicked() {
    	TTable table_d=getTable("TABLE_DD");
    	int row = table_d.getSelectedRow();
    	String codeString=table_d.getItemString(row,"INV_CODE");
    	List<String> list=ddMap.get(codeString);
    	TParm parm=new TParm(); 
    	for (int i = 0; i < list.size(); i++) {
    		parm.setData("INV_CODE",i, codeString);
    		parm.setData("RFID",i, list.get(i));
    		
    		Map<String, String> map=invMap.get(codeString);
    		parm.setData("INV_CHN_DESC", i, map.get("INV_CHN_DESC"));
    		parm.setData("SUP_CHN_DESC", i,map.get("SUP_CHN_DESC")); 
    		parm.setData("MAN_CHN_DESC", i,map.get("MAN_CHN_DESC"));
    		parm.setData("DESCRIPTION", i,map.get("DESCRIPTION"));
    		parm.setData("ORDER_CODE", i,map.get("ORDER_CODE"));
			
    		
		}
    	getTable("TABLE_RFID").setParmValue(parm);
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    }
	
	
	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				String msg = txtMsg.getText();
				
				
			//	System.out.println("Consumer:->Received: " + msg);
				List<RFIDTag> lt=xml2rfidTag(msg);
				if (lt!=null&&lt.size()>0) {
					for (RFIDTag rfidTag : lt) {
						String rifd=rfidTag.getEpc();
						    if (map.containsKey(rifd)) {
								continue;
							}else {
								map.put(rifd,"");
							}
					}
				}
				int ss=0;
//				for(int i=0;i<table_dd.getRowCount();i++){
//					//����PFID��Str���         
//					String rfidString=table_dd.getItemData(i, "RFID").toString();
//					if(map.containsKey(rfidString)){     
//					//�������
//					//Toolkit.getDefaultToolkit().beep();     
//					//�����ɫ
//					table_dd.setItem(i, 0, "Y"); 
//					table_dd.setItem(i, "CABINET_ID", map.get(rfidString));
//					ss++;
//					}    
//				} 
				getTextField("total").setValue(String.valueOf(map.size()));
			} else {
				System.out.println("Consumer:->Received: " + message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		
		
		
		
	}
	public void onRotate() {
		String cabsql  = " SELECT CABINET_ID,CABINET_IP," +
		" ZKRFID_URL,MQ_DESC" + 
		" FROM IND_CABINET " +   
		" where ACTIVE_FLG='Y' AND CABINET_TYPE='03'" ;
		TParm mparm= new TParm(TJDODBTool.getInstance().select(cabsql));
		tparm=new TParm();
		for (int i = 0; i < mparm.getCount("CABINET_ID"); i++) {
			tparm.addData("READER_ID", mparm.getData("CABINET_ID",i)); 
			tparm.addData("TERM_IP", mparm.getData("CABINET_IP",i)); 
			tparm.addData("WS_URL", mparm.getData("ZKRFID_URL",i)); 
			tparm.addData(MQ_QUEUE_NAME, mparm.getData("MQ_DESC",i)); 
			
			String s = mparm.getData("ZKRFID_URL",i).toString();
			s = s.substring(s.indexOf("http://") + 7, s.indexOf(":8080"));
			tparm.addData(MQ_IP,s);
			
		}
	
		
		messageBox("��ʼ��ת");
	   	
		if (TIOM_AppServer.executeAction("action.device.RFIDAction","onRotate", tparm).getErrCode()==-1) {
			messageBox("���ӳ�ʱ");
			
		};
		
	}

}

