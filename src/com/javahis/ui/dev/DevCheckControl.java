package com.javahis.ui.dev;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jdo.dev.DevCheckTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.alien.enterpriseRFID.reader.AlienClass1Reader;
import com.alien.enterpriseRFID.reader.AlienReaderException;
import com.alien.enterpriseRFID.tags.Tag;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.device.Uitltool;

/** 
 * <p>Title: �豸ɨ��</p>
 *
 * <p>Description: �豸ɨ��</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company:javahis </p>
 *
 * @author yuhb   
 * @version 1.0
 */
public class DevCheckControl extends TControl{
	private static List<String> dev_nos = new ArrayList<String>();
	// �Զ����涨ʱ��
	private Timer timer;
	// �Զ���������
	private TimerTask task;
	// ��ʱ��ʱ����
	private long period = 1000;
	// ��ʱ����ʱʱ��
	private long delay = 1000;
	// IP��ַ
	private String ip = "192.168.1.100";
	// RFID��ǩ��������
	AlienClass1Reader reader; 
	//FLG ���
	Boolean flg;
	
	/**
	 * ��ʼ��
	 */
	public void onInit() {  
		getTTable("SEQCHECK_TABLE").addEventListener
		(TTableEvent.CREATE_EDIT_COMPONENT, this,"onCreateEditComoponent");
		super.init();
	}
	   /**  
	    * �����豸¼���¼�
	    * @param com Component
	    * @param row int
	    * @param column int
	    */      
	   public void onCreateEditComoponent(Component com,int row,int column){
	       //״̬����ʾ
	       callFunction("UI|setSysStatus","");
	       if(!(com instanceof TTextField))
 	           return;            
	       TTextField textFilter = (TTextField)com;
	       textFilter.onInit();         
	       TParm parm = new TParm();     
	       //���õ����˵�        
	       textFilter.setPopupMenuParameter("DEV_STARTCODE",getConfigParm().newConfig("%ROOT%\\config\\sys\\DEVBASEPopupUI.x"),parm);
	       //������ܷ���ֵ����         
	       textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE,this,"popReturn");
	   } 
	   /**
	    * �����豸¼�뷵�ز���
	    * @param tag String
	    * @param obj Object
	    */                 
	   public void popReturn(String tag,Object obj){
	       //�ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
	       if (obj == null && !(obj instanceof TParm)) {
	           return;
	       }  
	       //����ת����TParm  
	       TParm parm = (TParm) obj;    
	       TParm tableParm = new TParm();   
	       tableParm.setData("DEV_CODE",parm.getData("DEV_CODE"));  
	       tableParm.setData("DEV_CHN_DESC",parm.getData("DEV_CHN_DESC"));
	  }


	/**
	 * �Զ������ʼ��
	 */
	public void schedule() {
		this.task = new TimerTask() {
			@Override
			public void run() {
				try {
				//	autoCheck();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		this.timer.schedule(task, this.delay, this.period);
	}
	
	
	/**
	 * ��ʼ����ʱ��
	 */
	public void initTimer() {
		this.timer = new Timer();
		this.delay = this.period;
		this.task = new TimerTask() {
			@Override
			public void run() {
			}
		};
	}
	
	/**
	 * ȡ���Զ���ʱ����
	 */
	public void cancel() {
		this.timer.cancel();
	}
	
	/**
	 * ��ʼ
	 * @throws AlienReaderException
	 */
	public void onStart() throws AlienReaderException{
		this.clearValue("SEQCHECK_TABLE");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE", date.toString().substring(0, 19)
				.replaceAll("-", "/"));
		this.callFunction("UI|START|setEnabled", false);
		this.callFunction("UI|END|setEnabled", true);
		reader = new AlienClass1Reader(ip, 23);
		reader.open();
		reader.setFactorySettings();
		reader.setDHCP(0);
		reader.setRFAttenuation(20);
		initTimer();
		schedule();
	}  
	
	/**
	 * ����
	 */
	public void onEnd() {
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("END_DATE", date.toString().substring(0, 19).replaceAll(
				"-", "/"));
		reader.close();
		if (dev_nos.size() > 0) {
			TTable table = (TTable)getComponent("SEQCHECK_TABLE");
			TParm parm = table.getParmValue();
			String dev_no;
			for(int i=0; i<dev_nos.size(); i++) {
				dev_no = dev_nos.get(i);
				for(int j=0; j<parm.getCount(); j++) {
					if (dev_no.equals(parm.getRow(j).getValue("RFID"))) {
						parm.getRow(j).setData("REAL_NUM",1);
						table.setParmValue(parm);
						break;
					}
				}
			}
		}
		cancel();
		this.callFunction("UI|START|setEnabled", true);
		this.callFunction("UI|END|setEnabled", false);
	}
	
	
	public void onEnter() {
		String dev_no = getValueString("DEV_NOS");
		if (null == dev_no) {
			return;
		}
		for (int i = 0; i < dev_nos.size(); i++) {
			if (dev_no.equals(dev_nos.get(i))) {
				return;
			}
			TTable table = (TTable)getComponent("SEQCHECK_TABLE");
			TParm parm = table.getParmValue();
			for (int j = 0; j < parm.getCount(); j++) {
				if (dev_no.equals(parm.getRow(i).getData("RFID"))) {
					parm.getRow(j).setData("REAL_NUM",1);
					table.setParmValue(parm);
					break;
				}
			}
		}
	}
	
	/**
	 * ��ȡRFID
	 * @throws AlienReaderException
	 */
	public void autoCheck() throws AlienReaderException{
		reader.clearTagList();
		Tag[]  tagList = reader.getTagList();
		if (null != tagList) {
			for(int i=0; i<tagList.length; i++) {
				setValue("DEV_NOS", Uitltool.decode(tagList[i].getTagID().replace(
						" ", "")));
				onEnter();
			}
		}
		else {
			setValue("DEV_NOS", "");
		}
	}
	
	/**
	 * �����̵���
	 */
	public void onSave() {
		TTable table = (TTable)getComponent("SEQCHECK_TABLE");
		TParm  parm = table.getParmValue();
		List<TParm> parmList = new ArrayList<TParm>();  // ��Ҫ���µ�parm����
		TParm tempParm = null; // ÿһ��parm
		String tempNo = null; // ÿһ���е� REAL_NOֵ
		int tempQTY = 0;  // ÿһ�п������ֵ
		for (int i = 0, n=parm.getCount(); i < n; i++) {
			tempParm = parm.getRow(i);   
//			  ORG_CODE          
//			  DEV_CODE             
//			  BATCH_SEQ             
//			  CHECKREASON_CODE      
//			  CHECK_TYPE         
//			  DOSAGE_UNIT          
//			  RETAIL_PRICE        
//			  STOCK_QTY           
//			  MODI_QTY             
//			  ACTUAL_CHECKQTY_DATE  
//			  ACTUAL_CHECK_QTY    
//			  ACTUAL_CHECKQTY_USER  
//			  OPT_USER             
//			  OPT_DATE              
//			  OPT_TERM   
			
			String devCode = tempParm.getValue("DEV_CODE");
			String sql = " SELECT B.DEPT_CODE AS ORG_CODE " +
					" A.DEV_CODE,B.BATCH_SEQ,A.UNIT_PRICE AS DOSAGE_UNIT" +  
					" FROM DEV_BASE A,DEV_STOCKD B " +
					" WHEER A.DEV_CODE = '"+devCode+"'" + 
					" AND A.DEV_CODE = B.DEV_CODE " +
					"";      
			TParm tparm = new TParm(TJDODBTool.getInstance().select("sql"));
			tempParm.setData("ORG_CODE",tparm.getData(""));
			tempParm.setData("",tparm.getData(""));
			tempParm.setData("",tparm.getData(""));
			tempParm.setData("",tparm.getData(""));
			tempParm.setData("OPT_USER", Operator.getID()); 
			tempParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
			tempParm.setData("OPT_TERM", Operator.getIP()); 
			//DEV_CODE;DEV_CHN_DESC;SPECIFICATION;QTY;
			//REAL_NO;OPT_DATE;GUAREP_DATE

//			  ORG_CODE              
//			  DEV_CODE              
//			  BATCH_SEQ             
//			  CHECKREASON_CODE      
//			  CHECK_TYPE            
//			  DOSAGE_UNIT          
//			  RETAIL_PRICE          
//			  STOCK_QTY             
//			  MODI_QTY             
//			  ACTUAL_CHECKQTY_DATE  
//			  ACTUAL_CHECK_QTY      
//			  ACTUAL_CHECKQTY_USER  
//			  OPT_USER               ����         
//			  OPT_DATE               ���� 
//			  OPT_TERM               ���� 
			tempNo = tempParm.getValue("REAL_NO");
			// ����̵�ֵΪ��
			if (0 == tempNo.length()) {
				messageBox("��" + i + "�У���¼���̵�������");
				return;
			}else { // �̵�ֵ��Ϊ�գ�����̵����Ϳ������ȣ���������������ӽ������б��С�
				int no = tempNo.matches("\\d+") ? Integer.parseInt(tempNo) : -1;
				tempQTY = Integer.parseInt(tempParm.getValue("QTY"));
				switch (no) {
				case 0:
				case 1:
					if (no == tempQTY) {
						parmList.add(tempParm);
					}
					continue;
				case -1:
					messageBox("��" + i + "�У����������֣�");
					return;
				default:
					messageBox("��" + i + "�У�ֻ������0����1��");
					return;
				}
			}
		}
		TParm updateParm = new TParm();
		updateParm.setData("update", parmList);
		parm = TIOM_AppServer.executeAction(
		           "action.dev.DevAction","updateStockByCheck", updateParm);
       if(parm.getErrCode() < 0){
           messageBox("����ʧ��");
           return;
       }
       messageBox("����ɹ�");
	}
	
	
	
	/**
	 * ��ѯǰ�ж�
	 * @return
	 */
	public boolean onQueryCheck() {
		String strStartCode = getValueString("DEV_STARTCODE");
		String strEndCode = getValueString("DEV_ENDCODE");
		// ��ֹ�豸������ȫ����д����ȫ����д
		if (0 == strStartCode.length() && 
				0 == strEndCode.length()) {
			return false;
		}
		if (0 != strStartCode.length() && 
				0 != strEndCode.length()) {
			return false;
		}
		return true;
	}
	
	/**
	 * ��ѯ 
	 */
	public void onQuery() {
		if (onQueryCheck()) {
			return;
		}  
		String strQueryParm = "DEV_KEEPDEPT;DEV_LOCATIONDEPT;DEV_STARTCODE;DEV_ENDCODE";
		TParm parm = getQueryParm(strQueryParm);
		if ("Y".equals(getValue("QTY"))) {
			parm.setData("QTY",0);
		}
		parm = DevCheckTool.getInstance().selectSeqDevInf(parm);
		if (parm.getErrCode() < 0) {
			messageBox("��ѯʧ��");
			return;
		}
		TTable table = (TTable)getComponent("SEQCHECK_TABLE");
		table.setParmValue(parm);
	//	table.setmo  
	}
	
	/**
	 * ���
	 */
	public void onClear() {
		// ��ղ�ѯ����
		String str = "DEV_KEEPDEPT;DEV_LOCATIONDEPT;DEV_CHECKDATE;DEV_STARTCODE;DEV_ENDCODE;QTY";
		clear(str);
		
		// ���table
		((TTable)getComponent("SEQCHECK_TABLE")).removeRowAll();
	}
	/**
	 * �����ѯparm
	 * @param strParm
	 * @return
	 */
	public TParm getQueryParm(String strQueryParm) {
		String[] strParms = strQueryParm.split(";");
		TParm parm = new TParm();
		for (String string : strParms) {
			if(0 != getValueString(string).length()) {
				parm.setData(string,getValue(string));
			}
		}
		return parm;
	}
	
	/**
	 * �滻parm�е�0��1ֵΪ�쳣������
	 * @param parm
	 * @param strs
	 * @param strName
	 * @return
	 */
	public TParm replaceParm(TParm parm, String[] strs, String strName) {
		if (0 != parm.getValue(strName).length()) {
			String[] dataStrings = parm.getValue(strName).replaceAll("\\[|\\]|\\s", "").split(","); 
			parm.removeData(strName);
			for (String string : dataStrings) {
				parm.addData(strName, strs[Integer.parseInt(string)]);
			}
		}
		return parm;
	}
	
	/**
	 * ��������ַ������ֶ���� 
	 * @param str
	 */
	public void clear(String str) {
		String[] strs = str.split(";");
		for (String string : strs) {
			setValue(string, "");
		}
	}
	   /**
	    * �õ������λ��
	    * @param tableTag String
	    * @param column int
	    * @return String
	    */
	   public String getFactColumnName(String tableTag,int column){
	        int col = getThisColumnIndex(column);
	        return getTTable(tableTag).getDataStoreColumnName(col);
	    }
	   /**  
	     * �õ������λ����
	     * @param column int
	     * @return int
	     */
	    public int getThisColumnIndex(int column){  
	        return getTTable("DEV_REQUEST").getColumnModel().getColumnIndex(column);
	    }
	   /**
	    * �õ�TTable
	    * @param tag String
	    * @return TTable
	    */
	   public TTable getTTable(String tag){
	       return (TTable)getComponent(tag);
	    }
}
