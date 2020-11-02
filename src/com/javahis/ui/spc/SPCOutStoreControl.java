package com.javahis.ui.spc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import jdo.spc.SPCCabinetTool;
import jdo.spc.SPCContainerTool;
import jdo.spc.SPCInStoreTool;
import jdo.spc.SPCOutStoreTool;
import jdo.spc.SPCToxicTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
 
/**
 * 
 * <p>Title: ��������-ҩ���龫�����ܹ�</p>
 *
 * <p>Description:TODO </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author Yuanxm
 * @version 1.0
 */
public class SPCOutStoreControl extends TControl {
	
	/**
	 * �龫��ϸҩƷTable
	 */
	TTable table_N ,table_D;
	
	
	/**
	 * ���ⵥ��
	 */
	String dispenseNo ;
	
	/**
	 * ��ת��
	 */
	String boxEslId ;
	
	/**
	 * ����ID
	 */
	String containerId ;
	
	String ip ;
	
	
	/**
	 * ���ܹ񱾻�����IP
	 */
	String guardIp;
	
	/**
	 * ��ʼ����
	 */
	public void onInit() {
		super.onInit();
		
		table_D = (TTable)getComponent("TABLE_D");
		table_N = (TTable)getComponent("TABLE_N");
		
		
		this.onInitData();
		
		//�龫��ˮֵ�¼�
		/**
		table_N.addEventListener("TABLE_N->"+TTableEvent.CHANGE_VALUE, this,
		"onTableNChange");*/
 
	}
	
	 /***-----------------------------------�Ž�---------------------------------------------------------------*/
 

 

	
	public void onTestRfid(TParm parm){
		
		
		table_D.acceptText() ;
		
		//���ݿ����������ܹ����ݶԱȵõ����ߵ�����
		TParm tableDParm = table_D.getParmValue() ;
		String seqNo = "";
		int count2 = 0 ;
		for(int k = 0 ; k < parm.getCount("CONTAINER_ID") ; k++ ){
			TParm inParmAll = new TParm();
			String containerId = parm.getValue("CONTAINER_ID",k);
			TParm inParm = new TParm();
			//��������ID��ѯ��Ӧ���龫��Ϣ
			String cabinetId = getValueString("CABINET_ID");
			inParm.setData("CONTAINER_ID",containerId);
			inParm.setData("CABINET_ID",cabinetId);
			
			//һ��������Ӧһ���龫ҩƷ
			TParm result = SPCOutStoreTool.getInstance().onQueryContainerDetail(inParm);
			String orderCode1 = result.getValue("ORDER_CODE",0);
			for(int i = 0 ; i < tableDParm.getCount("ORDER_CODE"); i++ ){
				String orderCode2 = tableDParm.getValue("ORDER_CODE",i) ; 
				if(orderCode1.equals(orderCode2)){
					seqNo = tableDParm.getValue("SEQ_NO",i);
					break;
				}
			}
			
			//�ô�ҩ
			if(seqNo == null || seqNo.equals("")) {
				seqNo = "999" ;
			}
			
			inParmAll.setData("DISPENSE_NO",dispenseNo);
			inParmAll.setData("DISPENSE_SEQ_NO",seqNo);
			inParmAll.setData("CONTAINER_ID",containerId);
			
			inParmAll.setData("CONTAINER_DESC",parm.getValue("CONTAINER_DESC",k));
			inParmAll.setData("IS_BOXED","Y");
			inParmAll.setData("BOXED_USER",Operator.getID());
			inParmAll.setData("BOX_ESL_ID",boxEslId);
			inParmAll.setData("OPT_USER",Operator.getID());
			inParmAll.setData("OPT_TERM",Operator.getIP());
			inParmAll.setData("CABINET_ID",getValueString("CABINET_ID"));
			inParmAll.setData("ORDER_CODE",orderCode1);
			TParm inParm2 = new TParm() ;
			for(int i = 0 ; i < result.getCount() ; i++ ){
				TParm rowParm = (TParm)result.getRow(i) ;
				inParm2.setData("ORDER_CODE",count2,orderCode1);
				inParm2.setData("BATCH_NO",count2 ,rowParm.getValue("BATCH_NO"));
				inParm2.setData("VERIFYIN_PRICE",count2,rowParm.getValue("VERIFYIN_PRICE"));
				inParm2.setData("VALID_DATE",count2,rowParm.getValue("VALID_DATE"));
				inParm2.setData("BATCH_SEQ",count2,rowParm.getValue("BATCH_SEQ"));
				inParm2.setData("DISPENSE_NO",count2,dispenseNo);
				inParm2.setData("DISPENSE_SEQ_NO",count2,seqNo);
				inParm2.setData("CABINET_ID",count2,getValueString("CABINET_ID")); 
				inParm2.setData("IS_BOXED",count2,"Y");
				inParm2.setData("TOXIC_ID",count2,rowParm.getValue("TOXIC_ID"));
				inParm2.setData("BOXED_USER",count2,Operator.getID());
				inParm2.setData("BOX_ESL_ID",count2,boxEslId);
				inParm2.setData("CONTAINER_ID",count2,rowParm.getValue("CONTAINER_ID"));
				inParm2.setData("CONTAINER_DESC",count2,rowParm.getValue("CONTAINER_DESC"));
				inParm2.setData("OPT_USER",count2,Operator.getID());
				inParm2.setData("OPT_TERM",count2,Operator.getIP());
				count2++ ;
			}
			
			if(inParm2 != null ){
				inParmAll.setData("OUT_D",inParm2.getData());  
			}
			
			//д���ݵ��������ױ���������ϸ��,����ʵ�ʳ�������
			TParm result2  = TIOM_AppServer.executeAction("action.spc.SPCOutStoreAction",
			                                    "onInsertBatch", inParmAll);
			if(result2.getErrCode() != -1  ){
				if(seqNo != null && !seqNo.equals("") && !seqNo.equals("999")){
					for(int i = 0 ; i < tableDParm.getCount("ORDER_CODE"); i++ ){
						String orderCode2 = tableDParm.getValue("ORDER_CODE",i) ;
						if(orderCode1.equals(orderCode2)){
							table_D.setItem(i, "ACTUAL_QTY", tableDParm.getInt("ACTUAL_QTY",i) +result.getCount());
							break;
						}
					}
				}
			}
		}

		/**
    	//д��־
		TParm logParm = new TParm();
		logParm.setData("TASK_TYPE","5");
		logParm.setData("EVENT_TYPE","1");
		logParm.setData("GUARD_ID","");
		builderLog(logParm);
		*/
			
	}

	 
    
	// �� byteת�ɷ���
	private static String byte2Str(byte[] Data) {
		String Total_Data = "";
		for (int i = 0; i < Data.length; i++) {
			Character CWord = new Character((char) Data[i]);
			Total_Data = Total_Data + CWord.toString();
		}
		return Total_Data;
	}
    /**-----------------------------------------------------*/
	
	/**
	 * ��ʼ������
	 */
	public void onInitData(){
		 
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			ip=addr.getHostAddress();//��ñ���IP
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		TParm parm = new TParm();
		parm.setData("IP",ip);
		TParm result = SPCCabinetTool.getInstance().onQuery(parm);
		guardIp = result.getValue("GUARD_IP",0);
		setValue("CABINET_ID", result.getValue("CABINET_ID", 0));
		setValue("CABINET_DESC", result.getValue("CABINET_DESC", 0));
		setValue("ORG_CHN_DESC", result.getValue("ORG_CHN_DESC", 0));
		setValue("OPT_USER", Operator.getName());
		
	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		
		table_D = (TTable)getComponent("TABLE_D");
		table_N = (TTable)getComponent("TABLE_N");
		
		//�����ܹ��()
		dispenseNo = getValueString("DISPENSE_NO");
		TParm parm = new TParm();
		parm.setData("DISPENSE_NO",dispenseNo);
		TParm result = SPCOutStoreTool.getInstance().onQuery(parm);
		
		 
		if(result.getCount() > 0 ){
			table_D.setParmValue(result);			
			setValue("OUT_ORG_CHN_DESC", result.getValue("ORG_CHN_DESC",0));
			setValue("REQTYPE_CODE", getReqtype(result.getValue("REQTYPE_CODE",0)));
			table_D.setSelectedRow(0);
			
			/**
			double qty = StringTool.round(result.getDouble("QTY",0),2);
			double actualQty = StringTool.round(result.getDouble("ACTUAL_QTY",0), 2);
			String orderCode = result.getValue("ORDER_CODE",0);*/
			
			//��ѯ�Ƿ����г�����龫
			parm.setData("SEQ_NO",result.getValue("SEQ_NO",0));
			
			TParm p = SPCToxicTool.getInstance().onQuery(parm);
			table_N.setParmValue(p);
			
		}else{
			this.messageBox("û�в�ѯ������");
		}
		
	}
	
	/**
	 * ���ⵥ�Żس��¼�
	 */
	public void onDispenseNoClick(){
		
		onQuery();
		this.getTextField("BOX_ESL_ID").grabFocus();
				
		//д��־
		/**
		TParm parm = new TParm();
		parm.setData("TASK_TYPE","5");
		parm.setData("EVENT_TYPE","1");
		parm.setData("GUARD_ID","");
		builderLog(parm);
		//initTimer();
		*/
		 
	}

	/**
	 * ��װд��־����
	 */
	/**
	private void builderLog(TParm p) {
		//д��С�ŵ���־
		TParm parm = new TParm();
		parm.setData("CABINET_ID", getValueString("CABINET_ID"));
		parm.setData("LOG_TIME", SystemTool.getInstance().getDate());
		parm.setData("TASK_TYPE", p.getValue("TASK_TYPE"));
		parm.setData("EVENT_TYPE", p.getValue("EVENT_TYPE")); //1���ţ�������
		parm.setData("GUARD_ID", p.getValue("GUARD_ID"));   //
		
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM",Operator.getIP());
		
		onSaveLog(parm);
	}*/
	
	/**
	 * ������Table�����¼�
	 */
	public void onTableDClicked(){
		table_D.acceptText();
		table_N.acceptText() ;
		int row = table_D.getSelectedRow() ;
		TParm parm = table_D.getParmValue() ;
		double qty = StringTool.round(parm.getDouble("QTY",row),2);
		double actualQty = StringTool.round(parm.getDouble("ACTUAL_QTY",row), 2);
		String orderCode = parm.getValue("ORDER_CODE",row);
		
		//�Ȳ�ѯ�Ƿ��г���ģ�����ʾ����ģ������������һ���հ���
		TParm inParm = new TParm();
		inParm.setData("DISPENSE_NO",getValueString("DISPENSE_NO"));
		inParm.setData("SEQ_NO",parm.getValue("SEQ_NO",row));
		inParm.setData("ORDER_CODE",orderCode);
		
		TParm p = SPCToxicTool.getInstance().onQuery(inParm);
		table_N.setParmValue(p);
		if(actualQty < qty ){
			 
			getTextField("BOX_ESL_ID").setEditable(true);
			getTextField("CONTAINER_ID").setEditable(true);
		}else {
			//������һ���հ���
			this.messageBox("ȫ���������");
			getTextField("BOX_ESL_ID").setEditable(false);
			getTextField("CONTAINER_ID").setEditable(false);
			
		}	
		
		setValue("BOX_ESL_ID", "");
		setValue("CONTAINER_ID", "");
		setValue("TOXIC_QTY", "");
		setValue("CONTAINER_DESC", "");
		setValue("TOXIC_ID", "");
		table_D.setSelectedRow(row);
	}
	
	/**
	 * �����龫��ˮ���¼�
	 * @param tNode
	 * @return
	 */
	public void onToxicIdClicked(){
		
		table_D.acceptText();
		table_N.acceptText() ;
		
		boxEslId = getValueString("BOX_ESL_ID");
		containerId = getValueString("CONTAINER_ID");
		String toxicId = getValueString("TOXIC_ID");
		
		//���ж��Ƿ�������
		int tableDrow = table_D.getSelectedRow() ;
		TParm tableD_parm = table_D.getParmValue() ;
		double tableD_qty = StringTool.round(tableD_parm.getDouble("QTY",tableDrow),2);
		double tableD_actualQty = StringTool.round(tableD_parm.getDouble("ACTUAL_QTY",tableDrow), 2);
		if(tableD_actualQty >= tableD_qty){
			this.messageBox("����ҩƷ�ѳ������");
			return ;
		}
		
		//����ʾ��������������
		String containerDesc = getValueString("CONTAINER_DESC");
		String cabinetId = getValueString("CABINET_ID");
		
		if(StringUtil.isNullString(boxEslId)){
			this.messageBox("��ת�䲻��Ϊ�գ�����ɨ��!");
			return ;
		}
		if(StringUtil.isNullString(containerId)){
			this.messageBox("��������Ϊ��,����ɨ��");
			return ;
		}
		
		if(StringUtil.isNullString(toxicId)){
			this.messageBox("�龫��ˮ��Ϊ�գ�");
			return ;
		}
		
		 
		TParm tableDParm = table_D.getParmValue() ;
		String orderCode = tableDParm.getValue("ORDER_CODE",tableDrow);
		 
		//�����龫��ˮ����orcer_code��ѯ��Ӧ��
		TParm inParm = new TParm();
		inParm.setData("ORDER_CODE",orderCode);
		inParm.setData("TOXIC_ID",toxicId);
		inParm.setData("CABINET_ID",cabinetId);
	
		//�Ȳ�ѯ�������ܲ���װ
		 
		
		dispenseNo = tableDParm.getValue("DISPENSE_NO",tableDrow);
		String seqNo = tableDParm.getValue("SEQ_NO",tableDrow);
		TParm toxParm = new TParm();
		toxParm.setData("DISPENSE_NO",dispenseNo);
		toxParm.setData("DISPENSE_SEQ_NO",seqNo);
		toxParm.setData("CONTAINER_ID",containerId);
		TParm dParm = SPCToxicTool.getInstance().onQueryDByCount(toxParm);
		int ccout = dParm.getInt("NUM",0) ;
		 
		int toxicQty = getValueInt("TOXIC_QTY");
		if(ccout == toxicQty ){
			this.messageBox("��������!");
			setValue("CONTAINER_ID", "");
			return ;
		}
		
		TParm parm = SPCContainerTool.getInstance().onQuery(inParm);
		if(parm.getCount() <=  0){
			this.messageBox("���޸��龫ҩƷ,�����¼�ѡ!");
			return ;
		}
		Timestamp validDate = parm.getTimestamp("VALID_DATE",0);
	 
		//��ѯ��Ӧ��Ч���Ƿ��������
		TParm returnParm = SPCToxicTool.getInstance().onQueryByValidDate(inParm);
		Timestamp  minValidDate = returnParm.getTimestamp("VALID_DATE",0);
		
		inParm.setData("BATCH_NO",parm.getValue("BATCH_NO",0));
		inParm.setData("VERIFYIN_PRICE",parm.getValue("VERIFYIN_PRICE",0));
		inParm.setData("VALID_DATE",parm.getValue("VALID_DATE",0));
		inParm.setData("BATCH_SEQ",parm.getValue("BATCH_SEQ",0));
		inParm.setData("UNIT_CODE",parm.getValue("UNIT_CODE",0));
		
		//����ʱ�������ÿ�
		inParm.setData("CABINET_ID",""); 
		if(validDate.getTime() >  minValidDate.getTime()){
			if (this.messageBox("ȷ��", "ȡ�ò�������Ч��!", 2) == 0) {
				TParm result = batchSave(containerDesc, inParm);
				if(result.getErrCode() < 0 ){
					this.messageBox("�������");
					return;
				}else {
					table_D.setItem(tableDrow, "ACTUAL_QTY", tableD_actualQty+1);
				}
			}
		}else {
			TParm result = batchSave(containerDesc, inParm);
			if(result.getErrCode() < 0 ){
				this.messageBox("�������");
				return ;
			}else {
				table_D.setItem(tableDrow, "ACTUAL_QTY", tableD_actualQty+1);
			}
		}
		
		//��ѯ��������ʾ����
		TParm p = SPCToxicTool.getInstance().onQuery(inParm);
		
		table_N.setParmValue(p);
		setValue("TOXIC_ID", "");
		//setValue("CONTAINER_ID", "");
		this.getTextField("CONTAINER_ID").grabFocus();

	}

	/**
	 * 
	 * @param containerDesc
	 * @param inParm  ��װ�õĲ���
	 * @return
	 */
	private TParm batchSave(String containerDesc, TParm inParm) {
		int tableDRow = table_D.getSelectedRow() ;
		TParm tableDParm = table_D.getParmValue() ;
		dispenseNo = tableDParm.getValue("DISPENSE_NO",tableDRow);
		String seqNo = tableDParm.getValue("SEQ_NO",tableDRow);
		inParm.setData("DISPENSE_NO",dispenseNo);
		inParm.setData("DISPENSE_SEQ_NO",seqNo);
		inParm.setData("SEQ_NO",seqNo);
		inParm.setData("IS_BOXED","Y");
		inParm.setData("BOXED_USER",Operator.getID());
		inParm.setData("BOX_ESL_ID",boxEslId);
		inParm.setData("CONTAINER_ID",containerId);
		inParm.setData("CONTAINER_DESC",containerDesc);
		inParm.setData("OPT_USER",Operator.getID());
		inParm.setData("OPT_TERM",Operator.getIP());
		
		//д���ݵ��������ױ���������ϸ��,����ʵ�ʳ�������
		TParm result  = TIOM_AppServer.executeAction("action.spc.SPCOutStoreAction",
		                                    "onInsert", inParm);
		return result ;
	}
	
	/**
	 * ��ת��س��¼�
	 */
	public void onBoxEslIdClick(){
		boxEslId = getValueString("BOX_ESL_ID");
		if(boxEslId.length() <= 0 ){
			
			this.messageBox("��ת�䲻��Ϊ��");
			this.getTextField("BOX_ESL_ID").grabFocus();
			return ;
		}
		this.getTextField("CONTAINER_ID").grabFocus();
		this.getTextField("BOX_ESL_ID").setEditable(false);
		EleTagControl.getInstance().login();
		EleTagControl.getInstance().sendEleTag(boxEslId, getValueString("ORG_CHN_DESC"), "", "", 0);
		
		return ;
	}
	
	public void bindBoxEslId(){
		this.getTextField("BOX_ESL_ID").grabFocus();
		this.getTextField("BOX_ESL_ID").setEditable(true);
	}
	
	/**
	 * �����س��¼�
	 */
	public void onContainerIdClick(){
		String containerId = getValueString("CONTAINER_ID");
		if(StringUtil.isNullString(containerId)){
			this.messageBox("����Ϊ��!");
			return ;
		}
		table_D.acceptText();
		TParm inParm = new TParm();
		inParm.setData("CONTAINER_ID",containerId);
		TParm result = SPCContainerTool.getInstance().onQueryBy(inParm);
		if(result.getCount() >  0 ){
			String orderCode = result.getValue("ORDER_CODE",0);
			int tableDselectRow =  table_D.getSelectedRow();
			TParm  parm = table_D.getParmValue();
			
			String orderCodeD = parm.getValue("ORDER_CODE",tableDselectRow);
			if(!orderCodeD.equals(orderCode)){
				this.messageBox("���������龫Ʒ���뵱ǰҪ�����Ʒ�ֲ�ͬ,�����²���!");
				setValue("CONTAINER_ID", "");
				this.getTextField("CONTAINER_ID").grabFocus();
			}else {
				double toxicQty = result.getDouble("TOXIC_QTY",0);
				setValue("TOXIC_QTY", toxicQty);
				setValue("CONTAINER_DESC", result.getValue("CONTAINER_DESC",0));
			}
		}else {
			this.messageBox("û�и�����!");
			setValue("CONTAINER_ID", "");
			setValue("CONTAINER_DESC","");
			this.getTextField("CONTAINER_ID").grabFocus();
		}
			
		this.getTextField("TOXIC_ID").grabFocus();
		
		/**��ѯ������Ӧ�龫����(containerId)
		String cabinetId = getValueString("CABINET_ID");
		inParm.setData("CABINET_ID",cabinetId);
		TParm resultSec = SPCContainerTool.getInstance().onQueryByContainerId(inParm);
		if(resultSec.getCount() > 0 ){
			
			if (this.messageBox("ȷ��", "ȷ����������������!", 2) == 0) {
				TParm returnParm = table_N.getParmValue();
				returnParm.addParm(resultSec);
				table_N.setParmValue(returnParm);
				
				int tableDrow = table_D.getSelectedRow() ;
				TParm tableDParm = table_D.getParmValue() ;
				String orderCode = tableDParm.getValue("ORDER_CODE",tableDrow);
				 
				//�����龫��ˮ����orcer_code��ѯ��Ӧ��
				inParm.setData("ORDER_CODE",orderCode);
				inParm.setData("CABINET_ID",cabinetId);
				//ѭ������ �൱������ɨ�龫ҩÿ֧����
				for(int i = 0 ; i <  resultSec.getCount() ; i++ ){
					
				}
			}
		}*/
		
		
	}
	
	/**
	 * �����հ׵�һ��
	 */
	public TParm addTableDRow(TParm parm,String orderCode){
		if(parm.getCount() < 0 ){
			int count = table_N.getRowCount() ;
			
		 
			/**
			 * ���
			 */
			parm.addData("ROW_NUM",count+1);
		}else{
			parm.addData("ROW_NUM", parm.getCount()+1);
		}
			
		/**
		 * ҩƷ����
		 */
		parm.addData("ORDER_CODE",orderCode);
		
		/**
		 * �龫��ˮ��
		 */
		parm.addData("TOXIC_ID","");
		
		/**
		 * ����
		 */
		parm.addData("BATCH_NO","");
		
		/**
		 * Ч�ڡ�
		 */
		parm.addData("VALID_DATE","");
		
		/**
		 * ��������
		 */
		parm.addData("CONTAINER_ID","");
		
		return parm;
		
	}
	/**
	 * �õ��������͡�
	 * @param reqtype
	 * @return
	 */
	private String getReqtype(String reqtype){
		if(StringUtil.isNullString(reqtype)){
			return "";
		}
		if(reqtype.equals("DEP")){
			return "��������";
		}else if(reqtype.equals("TEC")){
			return "��ҩ����";
		}else if(reqtype.equals("EXM")){
			return "����Ʒ�";
		}else if(reqtype.equals("GIF")){
			return "ҩ������";
		}else if(reqtype.equals("RET")){
			return "�˿�";
		}else if(reqtype.equals("WAS")){
			return "���";
		}else if(reqtype.equals("THO")){
			return "��������";
		}else if(reqtype.equals("COS")){
			return "���Ĳ�����";
		}else if(reqtype.equals("THI")){
			return "�������";
		}else if(reqtype.equals("EXM")){
			return "���ұ�ҩ";
			
		}
		return "";
	}
	
	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}

	
	 /**
     * ��շ���
     */
    public void onClear() {
    	table_D = (TTable)getComponent("TABLE_D");
		table_N = (TTable)getComponent("TABLE_N");
		
		table_N.acceptText();
		table_N.setParmValue(new TParm());
		   	
    	String controlName = "DISPENSE_NO;OUT_ORG_CHN_DESC;REQTYPE_CODE;BOX_ESL_ID;CONTAINER_ID;CONTAINER_DESC";
		this.clearValue(controlName);
		this.getTextField("BOX_ESL_ID").setEditable(true);
		table_D.removeRowAll();
		
		
    }
    
    
    
    /**
     * �رմ���
     */
    /**
    public void onCloseF(){
    	
    	//д��־
		TParm parm = new TParm();
		parm.setData("TASK_TYPE","5");
		parm.setData("EVENT_TYPE","1");
		parm.setData("GUARD_ID","");
		builderLog(parm);
		
		//5.�ز�����
		PullDriver.Disconnect(rtn1);
		
		//ע��dll
		PullDriver.free();
		task.cancel();
	
    	this.onClosing();
    }*/
    
    /**
    public void onOpen(){
    	openDor(0,Operator.getIP());
    	
    	//д��־
		TParm parm = new TParm();
		parm.setData("TASK_TYPE","5");
		parm.setData("EVENT_TYPE","1");
		parm.setData("GUARD_ID","");
		builderLog(parm);
    }*/
    
    
    public void onSaveLog(TParm parm){
    	SPCInStoreTool.getInstance().insertLog(parm);
    }
   
   
    
    public void openSearch(){
    	
    	TParm parm = new TParm();
    	parm.setData("CABINET_ID",getValueString("CABINET_ID"));
    	Object result = openDialog("%ROOT%\\config\\spc\\SPCContainerStatic.x", parm);

    }
    
   

}
