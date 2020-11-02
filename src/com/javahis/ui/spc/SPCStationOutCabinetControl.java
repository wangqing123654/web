package com.javahis.ui.spc;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.spc.SPCCabinetTool;
import jdo.spc.SPCContainerTool;
import jdo.spc.SPCInStoreTool;
import jdo.spc.SPCStationOutCabinetTool;
import jdo.spc.SPCToxicTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.device.PullDriver;

/**
 * 
 * <p>
 * Title:��ʱҽ��/������-������ҩ���龫�����ܹ�
 * </p>
 * 
 * <p>
 * Description:TODO
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author Yuanxm
 * @version 1.0
 */
public class SPCStationOutCabinetControl extends TControl {

	/**
	 *D ��ҩ M �龫
	 */
	TPanel panel_D;
	TPanel panel_M;

	TTable table_D;
	TTable table_M;
	
	//δ��⣬������б�
	TTable table_List; 

	// ��ҩTable
	TTable table;

	/**
	 * ȡҩ����
	 */
	String takemedNo;

	TParm result;

	String containerId;
	String toxicId;

	String org_code;
	String cabinetId;
	
	/**
	 * ���ܹ񱾻�����IP
	 */
	String guardIp;
	
	/**
	 * �龫���Ž�ID
	 */
	String guardId;
	
	//�����û�ID
	String userId ;
	
	private String ip = ""; // ���ܹ�IP
	
	private int rtn1;
	
	Set<String> guardSet = new HashSet<String>();

	
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		panel_D = (TPanel) getComponent("PANEL_D");
		panel_M = (TPanel) getComponent("PANEL_M");
		table_D = getTable("TABLE_D");
		table_M = getTable("TABLE_M");
		table = getTable("TABLE");
		
		table_List = getTable("TABLE_LIST");
		
		onInitData();
		
		addListener(getTable("TABLE_LIST"));
		
		// ��ʼ�����ܹ��Ž�
		/**
		int i = PullDriver.init();
		String params = "protocol=TCP,ipaddress=" + guardIp
				+ ",port=4370,timeout=2000,passwd=";
		rtn1 = PullDriver.Connect(params);
		if (rtn1 <= 0) {
			this.messageBox("���ܹ��ʼ��ʧ��");
			// getTextField("DISPENSE_NO").setEnabled(false);
			// getTextField("BOX_ESL_ID").setEnabled(false);
		}*/
	
		
		
	}
	
	/**---------------------------------------------------------------*/

	/**
	 * ��ʼ������
	 */
	public void onInitData() {

		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();// ��ñ���IP
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		ip = "192.168.1.210";
		TParm parm = new TParm();
		parm.setData("IP", ip);
		TParm result = SPCCabinetTool.getInstance().onQuery(parm);
		org_code = result.getValue("ORG_CODE", 0);
		cabinetId = result.getValue("CABINET_ID", 0);

		/**
		parm.setData("CABINET_ID",cabinetId);
		TParm cabinetParm = SPCInStoreTool.getInstance().queryCabinetGuard(parm);
		guardId = (String) cabinetParm.getData("GUARD_ID",0);
		 */
		
		guardIp = result.getValue("GUARD_IP",0);
		
		setValue("CABINET_ID", result.getValue("CABINET_ID", 0));
		setValue("CABINET_DESC", result.getValue("CABINET_DESC", 0));
		setValue("ORG_CHN_DESC", result.getValue("ORG_CHN_DESC", 0));
		
		
		String sql =  " UPDATE IND_CABDSPN A SET TOXIC_ID1='' , A.ACUM_OUTBOUND_QTY= 0, A.TAKEMED_USER='' "+
		  " WHERE A.CASE_NO='130923000914' AND A.ORDER_NO='1310131243' "+
      "  AND A.ORDER_SEQ=3   AND A.START_DTTM='201310132100' ";
		TParm resultParm = new TParm(TJDODBTool.getInstance().update(sql));
		
		sql = " UPDATE IND_CONTAINERD SET CABINET_ID='"+cabinetId+"' ,CONTAINER_ID='A13000049438'  " + " WHERE TOXIC_ID='10000000'  ";
		
		resultParm = new TParm(TJDODBTool.getInstance().update(sql));
		sql = " DELETE FROM IND_TOXICD WHERE TOXIC_ID='10000000' ";
		resultParm = new TParm(TJDODBTool.getInstance().update(sql));
		
		sql = " DELETE FROM IND_TOXICM WHERE CONTAINER_ID='A13000049438' ";
		resultParm = new TParm(TJDODBTool.getInstance().update(sql));
		
		
		
		  // ��ʼ������ʱ��
        // ��������
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -1).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
		//setValue("OPT_USER", Operator.getName());
		
		//Timestamp startDate = SystemTool.getInstance().getDate();
		//String startDateStr = sdf.format(startDate) ;
		//��ѯ������ܹ��Ӧ��������δ�������ҩ���龫
		parm.setData("ORG_CODE",org_code);
		parm.setData("STATUS","N");
		
		//1��ҩ��2�龫
	   if(getRadioButton("G_DRUGS").isSelected()){
        	parm.setData("DRUG_CATEGORY","1");
        }else if(getRadioButton("N_DRUGS").isSelected()){
        	parm.setData("DRUG_CATEGORY","2");
        }
		//parm.setData("START_DATE",startDateStr+"0000");
		//parm.setData("END_DATE",startDateStr+"2359");
		TParm odiParm = SPCStationOutCabinetTool.getInstance().onQueryIndCabDspn(parm);
		

		
		
		if(odiParm.getCount() > 0 ){
			table_List.setParmValue(odiParm);
		}else{
			
			this.messageBox("û�в��ҵ�����");
			table_List.setParmValue(new TParm());
		}
	}
	
	/**
	 * ��ѯ����δ��������ѳ��ⵥ���б�
	 */
	public void onQueryList(){
		takemedNo = getValueString("TAKEMED_NO");
		
		String status = "";
		//UPDATE_Y �ѳ��⣬UPDATE_N δ����
        if(getRadioButton("UPDATE_Y").isSelected()){
        	status = "Y";
        }else if(getRadioButton("UPDATE_N").isSelected()){
        	status = "N";
        }
        
        //��ʼʱ��
        String startDate = getValueString("START_DATE");
        startDate = startDate.substring(0,16).replaceAll("-", "").replaceAll(":","").replaceAll(" ", "");
        
        //����ʱ��
        String endDate = getValueString("END_DATE");
        endDate = endDate.substring(0,16).replaceAll("-", "").replaceAll(":","").replaceAll(" ", "");
        
        TParm parm = new TParm() ;
        parm.setData("ORG_CODE",org_code);
        parm.setData("STATUS",status);
        parm.setData("START_DATE",startDate);
        parm.setData("END_DATE",endDate);
        parm.setData("TAKEMED_NO",takemedNo);
       
        
        TParm odiParm = SPCStationOutCabinetTool.getInstance().onQueryIndCabDspn(parm);
		if(odiParm.getCount() > 0 ){
			table_List.setParmValue(odiParm);
		}else{
			this.messageBox("û�в��ҵ�����");
			table_List.setParmValue(new TParm());
			table.setParmValue(new TParm());
			table_D.setParmValue(new TParm());
			table_M.setParmValue(new TParm());
		}
		
		
        
		
	}
	
	//δ�����б��񵥻��¼�
	public void onTableListClick(){
		
		int row = table_List.getSelectedRow() ;
		if (row < 0) {
			this.messageBox_("û��ѡ�г�����Ϣ");
			return;
		}
		 
		String takemedNo = table_List.getItemString(row, "TAKEMED_NO");
		if (takemedNo == null || takemedNo.equals("")) {
			this.messageBox_("ȡ�ó�����Ϣʧ��");
			return;
		}
		
		TParm parm = new TParm();
		parm.setData("TAKEMED_NO", takemedNo);
		TParm result = new TParm();
		if (panel_D.isShowing()) {
			result = SPCStationOutCabinetTool.getInstance().onQueryD(parm);
			if (result.getCount() < 0) {
				this.messageBox("��������");
				table.setParmValue(new TParm());
				return ;
			}
			
			//�Ƿ�������
			boolean isOut = false;
			for(int i = 0 ; i < result.getCount() ; i++ ){
				TParm rowParm = (TParm)result.getRow(i);
				double dosageQty = rowParm.getDouble("DOSAGE_QTY");
				double acumOutBoundQty = rowParm.getDouble("ACUM_OUTBOUND_QTY");
				
				//��Ҫ�������Ƿ�����ѳ�������� 
				if(dosageQty > acumOutBoundQty ){
					isOut = true;
					break;
				}
			}
			this.setValue("TAKEMED_NO", takemedNo);
			table.setParmValue(result);
			//table.setSelectedRow(0);
			 
			/**
			if(isOpen){
				procesClose() ;
			}*/
			/**
			if(isOut){
				//δ����ˢ����
				
				if(getRadioButton("UPDATE_N").isSelected()){
					// �����ж�
					if (!checkPW()) {
						return ;
					}
				}
				//doJob() ;
			}*/
			
		} else if (panel_M.isShowing()) {
			
			result = SPCStationOutCabinetTool.getInstance().onQueryM(parm);
			if (result.getCount() < 0) {
				this.messageBox("��������");
				table_D.setParmValue(new TParm());
				return ;
			}
			 
			//�Ƿ�������
			boolean isOut = false;
			for(int i = 0 ; i < result.getCount() ; i++ ){
				TParm rowParm = (TParm)result.getRow(i);
				double dosageQty = rowParm.getDouble("DOSAGE_QTY");
				double acumOutBoundQty = rowParm.getDouble("ACUM_OUTBOUND_QTY");
				
				//��Ҫ�������Ƿ�����ѳ�������� 
				if(dosageQty > acumOutBoundQty ){
					isOut = true;
					break;
				}
			}
			
			if(isOut){
				//δ����ˢ����
				
				if(getRadioButton("UPDATE_N").isSelected()){
					// �����ж�
					if (!checkPW()) {
						return ;
					}
				}
				//doJob() ;
			}
			
			this.setValue("TAKEMED_NO", takemedNo);
			table_D.setParmValue(result);
			//table_D.setSelectedRow(0);
			 
			
			/**
			if(isOpen){
				procesClose() ;
			}
			if(isOut){
				doJob() ;
			}*/
			
		}

		
		setValue("BED_NO", result.getValue("BED_NO_DESC", 0));
		setValue("MR_NO", result.getValue("MR_NO", 0));
		setValue("PAT_NAME", result.getValue("PAT_NAME", 0));
		 
		
	}

	public boolean onQuery() {
		takemedNo = getValueString("TAKEMED_NO");
		if(takemedNo == null || takemedNo.equals("")){
			return  false;
		}
		TParm parm = new TParm();
		parm.setData("TAKEMED_NO", takemedNo);
		
		TParm result = new TParm();
		if (panel_D.isShowing()) {
			result = SPCStationOutCabinetTool.getInstance().onQueryD(parm);
			if (result.getCount() < 0) {
				this.messageBox("��������");
				table.setParmValue(new TParm());
				setValue("TAKEMED_NO", "");
				setValue("BED_NO", "");
				setValue("MR_NO", "");
				setValue("PAT_NAME", "");
				return false;
			}
			
			//�Ƿ�������
			boolean isOut = false;
			for(int i = 0 ; i < result.getCount() ; i++ ){
				TParm rowParm = (TParm)result.getRow(i);
				double dosageQty = rowParm.getDouble("DOSAGE_QTY");
				double acumOutBoundQty = rowParm.getDouble("ACUM_OUTBOUND_QTY");
				
				//��Ҫ�������Ƿ�����ѳ�������� 
				if(dosageQty > acumOutBoundQty ){
					isOut = true;
					break;
				}
			}
			/**
			if(isOut){
				onOpen(1);
			}*/
			
			table.setParmValue(result);
			//table.setSelectedRow(0);
		} else if (panel_M.isShowing()) {
			
			result = SPCStationOutCabinetTool.getInstance().onQueryM(parm);
			if (result.getCount() < 0) {
				this.messageBox("��������");
				setValue("TAKEMED_NO", "");
				setValue("BED_NO", "");
				setValue("MR_NO", "");
				setValue("PAT_NAME", "");
				table_D.setParmValue(new TParm());
				return false;
			}
			

			// �����ж�
			if (!checkPW()) {
				return false;
			}
			
			//�Ƿ�������
			boolean isOut = false;
			for(int i = 0 ; i < result.getCount() ; i++ ){
				TParm rowParm = (TParm)result.getRow(i);
				double dosageQty = rowParm.getDouble("DOSAGE_QTY");
				double acumOutBoundQty = rowParm.getDouble("ACUM_OUTBOUND_QTY");
				
				//��Ҫ�������Ƿ�����ѳ�������� 
				if(dosageQty > acumOutBoundQty ){
					isOut = true;
					break;
				}
			}
			/**
			if(isOut){
				onOpen(1);
			}*/
			
			table_D.setParmValue(result);
			//table_D.setSelectedRow(0);
		}

		
		setValue("BED_NO", result.getValue("BED_NO_DESC", 0));
		setValue("MR_NO", result.getValue("MR_NO", 0));
		setValue("PAT_NAME", result.getValue("PAT_NAME", 0));
		return true;

	}

	public boolean onTaskmedNoClicked() {
		
		/**
		if(panel_M.isShowing()){
			// �����ж�
			if (!checkPW()) {
				return false;
			}
		}*/
		onQuery();
		return true;
	}
	
	/**
	 * ����������֤
	 * 
	 * @return boolean
	 */
	public boolean checkPW() {
		String inwCheck = "station";
		TParm parm = (TParm) this.openDialog(
				"%ROOT%\\config\\spc\\passWordCheck.x", inwCheck);
		if (parm == null) {
			return false;
		}
		String value = parm.getValue("OK");
		userId = parm.getValue("USER_ID");
		return value.equals("OK");
	}
	
	
	/**
	 * �����¼�
	 */
	public void onCode(){
		//�������龫��ˮ��ͬһ���ؼ�
		containerId = getValueString("CONTAINER_ID");
		TParm sysParm = SPCContainerTool.getInstance().onQuerySysParm();
		int length = sysParm.getInt("TOXIC_LENGTH");
		
		//���containerId�ĳ������龫����һ��,�����龫�����ǣ�����
		if(containerId.length() == length){
			onToxicIdClicked(containerId);
		}else{
			onContainerIdClicked(containerId);
		}
	}

	/**
	 * �龫ҳǩ �����س��¼�
	 * 
	 */
	public void onContainerIdClicked(String containerId) {

		if (panel_M.isShowing()) {
			table_D = getTable("TABLE_D");
			table_D.acceptText();
			table_M = getTable("TABLE_M");
			 
			TParm inParm = new TParm();
			inParm.setData("CONTAINER_ID", containerId);
			TParm result = SPCContainerTool.getInstance().onQueryBy(inParm);
			if (result.getCount() > 0) {
				String orderCode = result.getValue("ORDER_CODE", 0);
				int tableDselectRow = table_D.getSelectedRow();
				TParm parm = table_D.getParmValue();

				String orderCodeD = parm
						.getValue("ORDER_CODE", tableDselectRow);
				if (!orderCodeD.equals(orderCode)) {
					this.messageBox("���������龫Ʒ���뵱ǰҪ�����Ʒ�ֲ�ͬ,�����²���!");
					setValue("CONTAINER_ID", "");
					this.getTextField("CONTAINER_ID").grabFocus();
					return;
				} else {
					
					//��:����������˵ˢ�������ϳ�
					cabinetId  = getValueString("CABINET_ID");
					inParm.setData("CABINET_ID",cabinetId);
					
					//��ѯ�����ܹ�������Ӧ�������龫����
					TParm containerToxicParm = SPCContainerTool.getInstance().onQueryByContainerId(inParm);
					int count = containerToxicParm.getCount() ;
					if(count < 0 ){
						this.messageBox("������");
						setValue("CONTAINER_ID", "");
						return ;
					}
					
					
					int tableDRow = table_D.getSelectedRow();
					int acumOutBoundQty = table_D.getParmValue().getInt(
							"ACUM_OUTBOUND_QTY", tableDRow);
					int dispenseQty = table_D.getParmValue().getInt(
							"DISPENSE_QTY", tableDRow);
					
					//count �����ܹ����������ж���֧�龫
					if(    acumOutBoundQty+count > dispenseQty ){
						this.messageBox("����ҩƷ����������ʵ��Ҫ�������������ܳ�");
						return ;
					}
					
					takemedNo = getValueString("TAKEMED_NO"); 
					
					TParm inParmAll = new TParm();
					String seqNo = table_D.getParmValue().getValue("ORDER_SEQ",tableDRow);
					
					inParmAll.setData("DISPENSE_NO",takemedNo);
					inParmAll.setData("DISPENSE_SEQ_NO",seqNo);
					inParmAll.setData("CONTAINER_ID",containerId);
					
					inParmAll.setData("CONTAINER_DESC",result.getValue("CONTAINER_DESC",0));
					inParmAll.setData("IS_BOXED","Y");
					inParmAll.setData("BOXED_USER",Operator.getID());
					inParmAll.setData("BOX_ESL_ID","");
					
					if(userId == null || userId.equals("")){
						userId = Operator.getID() ;
					}
					inParmAll.setData("OPT_USER",userId);
					inParmAll.setData("OPT_TERM",Operator.getIP());
					inParmAll.setData("CABINET_ID",getValueString("CABINET_ID"));
					inParmAll.setData("ORDER_CODE",orderCode);
					
					
					/**
					 * ODI_DSPNM �ĸ�����
					 */
					String startDttm = table_D.getParmValue().getValue("START_DTTM",
							tableDRow);
					String orderNo = table_D.getParmValue().getValue("ORDER_NO",
							tableDRow);
					String caseNo = table_D.getParmValue().getValue("CASE_NO",
							tableDRow);
					inParmAll.setData("START_DTTM", startDttm);
					inParmAll.setData("ORDER_NO", orderNo);
					inParmAll.setData("CASE_NO", caseNo);
					inParmAll.setData("ORDER_SEQ", seqNo);
					
					//ȡҩ��
					inParmAll.setData("TAKEMED_USER",userId);
					
					int count2  = 0 ;
					TParm inParm2 = new TParm() ;
					for(int i = 0 ; i < count ; i++ ){
						TParm rowParm = (TParm)containerToxicParm.getRow(i) ;
						inParm2.setData("ORDER_CODE",count2,rowParm.getValue("ORDER_CODE"));
						inParm2.setData("BATCH_NO",count2 ,rowParm.getValue("BATCH_NO"));
						inParm2.setData("VERIFYIN_PRICE",count2,rowParm.getValue("VERIFYIN_PRICE"));
						inParm2.setData("VALID_DATE",count2,rowParm.getValue("VALID_DATE"));
						inParm2.setData("BATCH_SEQ",count2,rowParm.getValue("BATCH_SEQ"));
						inParm2.setData("UNIT_CODE",count2,rowParm.getValue("UNIT_CODE"));
						inParm2.setData("DISPENSE_NO",count2,takemedNo);
						inParm2.setData("DISPENSE_SEQ_NO",count2,seqNo);
						inParm2.setData("CABINET_ID",count2,getValueString("CABINET_ID")); 
						inParm2.setData("IS_BOXED",count2,"Y");
						inParm2.setData("TOXIC_ID",count2,rowParm.getValue("TOXIC_ID"));
						inParm2.setData("BOXED_USER",count2,Operator.getID());
						inParm2.setData("BOX_ESL_ID",count2,"");
						inParm2.setData("CONTAINER_ID",count2,rowParm.getValue("CONTAINER_ID"));
						inParm2.setData("CONTAINER_DESC",count2,rowParm.getValue("CONTAINER_DESC"));
						inParm2.setData("OPT_USER",count2,userId);
						inParm2.setData("OPT_TERM",count2,Operator.getIP());
						count2++ ;
					}
					inParm2.setCount(count2);
					if(inParm2 != null ){
						inParmAll.setData("OUT_D",inParm2.getData());  
					}
										
					//д���ݵ��������ױ���������ϸ��,����ʵ�ʳ�������
					TParm result2  = TIOM_AppServer.executeAction("action.spc.SPCOutStoreAction",
					                                    "onInsertMlStationBatch", inParmAll);
				
					if(result2.getErrCode() < 0 ){
						this.messageBox("�龫�����ܹ�ʧ��!");
						return ;
					}else{
					
						int acturalQty = acumOutBoundQty + count;
						table_D.setItem(tableDRow, "ACUM_OUTBOUND_QTY",acturalQty);
					}
					
					// ��ѯ��������ʾ����
					TParm searchParm = new TParm();
					searchParm.setData("DISPENSE_NO",takemedNo);
					searchParm.setData("SEQ_NO",seqNo);
					searchParm.setData("ORDER_CODE",orderCode);
					 
					TParm p = SPCToxicTool.getInstance().onQuery(searchParm);
					table_M.setParmValue(p);
					double toxicQty = result.getDouble("TOXIC_QTY", 0);
					setValue("TOXIC_QTY", toxicQty);
					setValue("CONTAINER_DESC", result.getValue(
							"CONTAINER_DESC", 0)+"����");
					 
				
				}
			} else {
				this.messageBox("û�и�����!");
				setValue("CONTAINER_ID", "");
				setValue("CONTAINER_DESC", "");
				setValue("TOXIC_QTY", "");
				this.getTextField("CONTAINER_ID").grabFocus();
				return;
			}
			setValue("CONTAINER_ID", "");
			this.getTextField("CONTAINER_ID").grabFocus();
		}
	}

	/**
	 * �龫��ˮ�ŵ����¼�
	 */
	public void onToxicIdClicked(String toxicId) {
		table_D.acceptText();
		table_M.acceptText();

		//containerId = getValueString("CONTAINER_ID");
		takemedNo = getValueString("TAKEMED_NO");

		// ����ʾ��������������
		
		String cabinetId = getValueString("CABINET_ID");

		int tableDRow = table_D.getSelectedRow();
		int dispenseQty = table_D.getParmValue().getInt("DISPENSE_QTY",
				tableDRow);
		int acumOutBoundQty = table_D.getParmValue().getInt(
				"ACUM_OUTBOUND_QTY", tableDRow);

		if (dispenseQty == acumOutBoundQty) {
			this.messageBox("�ѳ������");
			return;
		}

		 
		if (toxicId != null && !toxicId.equals("")) {

			// �����龫��ˮ����orcer_code��ѯ��Ӧ��
			TParm inParm = new TParm();

			// �Ȳ�ѯ�������ܲ���װ

			String orderCode = table_D.getParmValue().getValue("ORDER_CODE",
					tableDRow);

			/**
			 * ODI_DSPNM �ĸ�����
			 */
			String seqNo = table_D.getParmValue().getValue("ORDER_SEQ",
					tableDRow);
			String startDttm = table_D.getParmValue().getValue("START_DTTM",
					tableDRow);
			String orderNo = table_D.getParmValue().getValue("ORDER_NO",
					tableDRow);
			String caseNo = table_D.getParmValue().getValue("CASE_NO",
					tableDRow);
			inParm.setData("START_DTTM", startDttm);
			inParm.setData("ORDER_NO", orderNo);
			inParm.setData("CASE_NO", caseNo);
			inParm.setData("ORDER_SEQ", seqNo);

			inParm.setData("ORDER_CODE", orderCode);
			inParm.setData("TOXIC_ID", toxicId);
			inParm.setData("CABINET_ID", cabinetId);

			
			/**
			TParm toxParm = new TParm();
			toxParm.setData("DISPENSE_NO", orderNo);
			toxParm.setData("DISPENSE_SEQ_NO", seqNo);
			toxParm.setData("CONTAINER_ID", containerId);
			TParm dParm = SPCToxicTool.getInstance().onQueryDByCount(toxParm);
			int ccout = dParm.getInt("NUM", 0);

			int toxicQty = getValueInt("TOXIC_QTY");
			if (ccout == toxicQty) {
				this.messageBox("��������!");
				setValue("CONTAINER_ID", "");

				return;
			}*/

			
			
			TParm parm = SPCContainerTool.getInstance().onQuery(inParm);
			if (parm.getCount() <= 0) {
				this.messageBox("���޸��龫ҩƷ,�����¼�ѡ!");
				return;
			}
			
			containerId = parm.getValue("CONTAINER_ID",0);
			String containerDesc = parm.getValue("CONTAINER_DESC",0);
			Timestamp validDate = parm.getTimestamp("VALID_DATE", 0);
			
			inParm.setData("CONTAINER_ID",containerId);
			inParm.setData("CONTAINER_DESC",containerDesc);

			// ��ѯ��Ӧ��Ч���Ƿ��������
			TParm returnParm = SPCToxicTool.getInstance().onQueryByValidDate(
					inParm);
			Timestamp minValidDate = returnParm.getTimestamp("VALID_DATE", 0);

			inParm.setData("BATCH_NO", parm.getValue("BATCH_NO", 0));
			inParm
					.setData("VERIFYIN_PRICE", parm.getValue("VERIFYIN_PRICE",
							0));
			inParm.setData("VALID_DATE", parm.getValue("VALID_DATE", 0));
			inParm.setData("BATCH_SEQ", parm.getValue("BATCH_SEQ", 0));
			inParm.setData("UNIT_CODE",parm.getValue("UNIT_CODE",0));
			// ����ʱ�������ÿ�
			inParm.setData("CABINET_ID", "");
			Timestamp nowDate = SystemTool.getInstance().getDate();
			if(validDate.getTime() < nowDate.getTime() ){
				this.messageBox("�ѹ�Ч�ڻ�Ч��,ҩƷ���ܳ���");
				return ;
			}
			if (validDate.getTime() > minValidDate.getTime()) {
				if (this.messageBox("ȷ��", "ȡ�ò�������Ч��!", 2) == 0) {
					TParm result = batchSave(containerDesc, inParm);
					if (result.getErrCode() < 0) {
						this.messageBox("�������");
					} else {

						int dselectRow = table_D.getSelectedRow();
						 
						table_D.setItem(dselectRow, "ACTUAL_QTY",
								acumOutBoundQty + 1);
					}
				}
			} else {
				TParm result = batchSave(containerDesc, inParm);
				if (result.getErrCode() < 0) {
					this.messageBox("�������");
					return ;
				} else {
					int dselectRow = table_D.getSelectedRow();
					table_D.setItem(dselectRow, "ACUM_OUTBOUND_QTY",
							acumOutBoundQty + 1);
				}
			}

			// ��ѯ��������ʾ����
			TParm p = SPCToxicTool.getInstance().onQuery(inParm);
			table_M.setParmValue(p);
			
			setValue("TOXIC_QTY", 1);
			setValue("CONTAINER_DESC", "��֧�龫");
			setValue("CONTAINER_ID", "");
			this.getTextField("CONTAINER_ID").grabFocus();

		}

	}

	private TParm batchSave(String containerDesc, TParm inParm) {
		int tableDRow = table_D.getSelectedRow();
		TParm tableDParm = table_D.getParmValue();
		takemedNo = getValueString("TAKEMED_NO");
		String seqNo = tableDParm.getValue("ORDER_SEQ", tableDRow);
		inParm.setData("DISPENSE_NO", takemedNo);
		inParm.setData("DISPENSE_SEQ_NO", seqNo);
		inParm.setData("SEQ_NO", seqNo);
		inParm.setData("IS_BOXED", "Y");
		inParm.setData("BOXED_USER", Operator.getID());
		inParm.setData("CONTAINER_ID", containerId);
		inParm.setData("CONTAINER_DESC", containerDesc);
		if(userId == null || userId.equals("")){
			userId = Operator.getID() ;
		}
		inParm.setData("OPT_USER", userId);
		inParm.setData("TAKEMED_USER",userId);
		inParm.setData("OPT_TERM", Operator.getIP());

		// д���ݵ��������ױ���������ϸ��,����ʵ�ʳ�������
		TParm result = TIOM_AppServer.executeAction(
				"action.spc.SPCOutStoreAction", "onInsertMlStation", inParm);
		return result;
	}

	public boolean onSave() {

		/**
		 * ��ҩ��Ҫ����һ�����
		 */

		TParm parm = new TParm();
		if (panel_D.isShowing()) {
			
			int  tableCount = table.getRowCount() ;
			if(tableCount <= 0 ){
				this.messageBox("û��Ҫ�����ܹ����ҩ����");
				return false;
			}
		
			TParm parmOrder = new TParm();
			TParm checkOrder = new TParm();
			TParm tableParm = table.getParmValue();

			int count = 0;
			
			/**
			if(userId == null || userId.equals("")){
				this.messageBox("���ȴ����ܹ�ȡҩ���ٱ���");
				return false;
				//userId = Operator.getID() ;
			}*/
			for (int i = 0; i < tableParm.getCount(); i++) {
				
				double dispenseQty = tableParm.getDouble("DISPENSE_QTY",i);
				double acumOutBoundQty = tableParm.getDouble("ACUM_OUTBOUND_QTY",i);
				if(acumOutBoundQty >= dispenseQty ){
					continue ;
				}
				count++;
			}
			if(count == 0 ){
				this.messageBox("��ȫ���������");
				return false;
			}
			count = 0 ;
			
			// �����ж�
			if (!checkPW()) {
				return false;
			}
			
			for (int i = 0; i < tableParm.getCount(); i++) {
				
				double dispenseQty = tableParm.getDouble("DISPENSE_QTY",i);
				double acumOutBoundQty = tableParm.getDouble("ACUM_OUTBOUND_QTY",i);
				if(acumOutBoundQty >= dispenseQty ){
					continue ;
				}
				
				parmOrder.setData("ORDER_CODE", count, tableParm.getValue(
						"ORDER_CODE", i));
				parmOrder.setData("OUT_ORG_CODE", count, org_code);
				parmOrder.setData("ACTUAL_QTY", count, tableParm.getValue(
						"DISPENSE_QTY", i));
				
				
				//ODI_DSPNM����� ����ACUM_OUTBOUND_QTY
				parmOrder.setData("CASE_NO", count, tableParm.getValue("CASE_NO",i));
				parmOrder.setData("ORDER_NO", count,tableParm.getValue("ORDER_NO",i));
				parmOrder.setData("ORDER_SEQ", count, tableParm.getValue("ORDER_SEQ",i));
				parmOrder.setData("START_DTTM", count, tableParm.getValue("START_DTTM",i));
				
				parmOrder.setData("OPT_USER",count,userId);
				parmOrder.setData("OPT_DATE",count,SystemTool.getInstance().getDate());
				parmOrder.setData("OPT_TERM",count,Operator.getIP());
				
				//ҩƷ����  �������
				parmOrder.setData("ORDER_DESC",count,tableParm.getValue(
						"ORDER_DESC", i));
				parmOrder.setData("TAKEMED_USER",count,userId);
				
				count++;
			}
			if (parmOrder != null) {
				parm.setData("OUT_ORDER", parmOrder.getData());
			}
			
			int count1 = 0;
			for (int i = 0; i < tableParm.getCount(); i++) {
				
				double dispenseQty = tableParm.getDouble("DISPENSE_QTY",i);
				double acumOutBoundQty = tableParm.getDouble("ACUM_OUTBOUND_QTY",i);
				if(acumOutBoundQty >= dispenseQty ){
					continue ;
				}
				
				checkOrder.setData("ORDER_CODE", count1, tableParm.getValue(
						"ORDER_CODE", i));
				checkOrder.setData("OUT_ORG_CODE", count1, org_code);
				checkOrder.setData("ACTUAL_QTY", count1, tableParm.getValue(
						"DISPENSE_QTY", i));
				
				
				//ODI_DSPNM����� ����ACUM_OUTBOUND_QTY
				checkOrder.setData("CASE_NO", count1, tableParm.getValue("CASE_NO",i));
				checkOrder.setData("ORDER_NO", count1,tableParm.getValue("ORDER_NO",i));
				checkOrder.setData("ORDER_SEQ", count1, tableParm.getValue("ORDER_SEQ",i));
				checkOrder.setData("START_DTTM", count1, tableParm.getValue("START_DTTM",i));
				
				checkOrder.setData("OPT_USER",count1,userId);
				checkOrder.setData("OPT_DATE",count1,SystemTool.getInstance().getDate());
				checkOrder.setData("OPT_TERM",count1,Operator.getIP());
				
				//ҩƷ����  �������
				checkOrder.setData("ORDER_DESC",count1,tableParm.getValue(
						"ORDER_DESC", i));
				checkOrder.setData("TAKEMED_USER",count1,userId);
				
				count1++;
			}
			if (checkOrder != null) {
				parm.setData("CHECK_ORDER", checkOrder.getData());
			}

			

			parm.setData("CABINET_ID", cabinetId);
			parm.setData("ORG_CODE",org_code);
			
			// ִ����������
			TParm result = TIOM_AppServer.executeAction(
					"action.spc.SPCOutStoreAction", "onInsertIndStock", parm);
			if (result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return false;
			}
			this.messageBox("����ɹ�");
			setValue("TAKEMED_NO", "");
			this.onQueryList();
		}
		
		return true;
		
		
	}

	public void onClear() {
	
		table_D = (TTable)getComponent("TABLE_D");
		table_M = (TTable)getComponent("TABLE_M");
		
		table_D.acceptText();
		table_M.acceptText() ;
		
		table_D.setParmValue( new TParm());
		table_M.setParmValue(new TParm());
		   	
    	String controlName = "CONTAINER_ID;TAKEMED_NO;BED_NO;MR_NO;PAT_NAME";
		this.clearValue(controlName);
		this.getTextField("CONTAINER_ID").setEditable(true);
		this.getTextField("TAKEMED_NO").grabFocus(); 
	 
		table = (TTable)getComponent("TABLE");
		table.acceptText();
		this.clearValue("TAKEMED_NO");
		table.setParmValue( new TParm());
		
		table_List = getTable("TABLE_LIST");
		table_List.acceptText();
		table_List.setParmValue(new TParm());
		
		userId = "" ;
		 
	}
	
	/**
	 * ����Table�����¼���û����ȫ����ģ���ʾ����ļ�һ�пհ���
	 */
	public void onTableDClicked(){
		table_D.acceptText();
		table_M.acceptText() ;
		int row = table_D.getSelectedRow() ;
		TParm parm = table_D.getParmValue() ;
		int dispenseQty  = parm.getInt("DISPENSE_QTY",row);
		int acumOutBoundQty  = parm.getInt("ACUM_OUTBOUND_QTY",row);

		TParm inParm = new TParm();
		inParm.setData("DISPENSE_NO",getValueString("TAKEMED_NO"));
		inParm.setData("SEQ_NO",parm.getValue("ORDER_SEQ",row));
		inParm.setData("ORDER_CODE",parm.getValue("ORDER_CODE",row));
		
		TParm p = SPCToxicTool.getInstance().onQuery(inParm);
		table_M.setParmValue(p);
		if(acumOutBoundQty < dispenseQty ){
			 
			getTextField("CONTAINER_ID").setEditable(true);
			//getTextField("TOXIC_ID").setEditable(true);
		}else {
			//������һ���հ���
			this.messageBox("ȫ���������");
			getTextField("CONTAINER_ID").setEditable(false);
			//getTextField("TOXIC_ID").setEditable(false);
			
		}	
		
		setValue("CONTAINER_ID", "");
		setValue("TOXIC_QTY", "");
		setValue("CONTAINER_DESC", "");
		
		table_D.setSelectedRow(row);
	}
	
	
	 public void openSearch(){
	    	
    	TParm parm = new TParm();
    	parm.setData("CABINET_ID",getValueString("CABINET_ID"));
    	Object result = openDialog("%ROOT%\\config\\spc\\SPCContainerStatic.x", parm);
	}
	 

	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}
	
	/**
	 * ���ܹ���
	 * @param type  0 
	 */
	public void onOpen(int type) {
		
		String cabinetSwitch = getCabinetSwitch();
		
		if(cabinetSwitch != null && cabinetSwitch.equals("Y")) {
			// 5.���Ų��� ��־���������豸������ (�ɹ�)
			int ret = 0;
			int operid = 1;
			int doorid = type;
			int outputadr = 1;
			int doorstate = 6;
			ret = PullDriver.ControlDevice(rtn1, operid, doorid, outputadr,
					doorstate, 0, "");
		}
	}
	
	
	
	private static  TConfig getProp() {
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
        return config;
	}
 
	public  static String getCabinetSwitch(){
		 TConfig config = getProp() ;
		 String cabinetSwitch = config.getString("", "CABINET_SWITCH");
		 return cabinetSwitch;
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
	
	
	 
	//�龫
	static  String  DOOR_DRUG = "1"; 
	static  String  DOOR_NOT_DRUG = "0";    
	/**
	 * ��־����
	 */				
	public void onLog() {																	
		String logStr = this.getLog();	
		String[] log = logStr.split(",");
		
		userId = log[2];
		String dispenseNo = getValueString("TAKEMED_NO");
		if ("8".equals(log[4])) {     
			TParm logParm = new TParm();
			logParm.setData("CABINET_ID", cabinetId);
			logParm.setData("LOG_TIME", SystemTool.getInstance().getDate());
			logParm.setData("TASK_TYPE", "6");
			logParm.setData("TASK_NO", dispenseNo);
			logParm.setData("EVENT_TYPE", "1");		
			logParm.setData("GUARD_ID", log[3]);
			logParm.setData("OPT_USER", Operator.getID());
			logParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
			logParm.setData("OPT_TERM", Operator.getIP());
			SPCInStoreTool.getInstance().insertLog(logParm);
			
			//���ѿ����
			if(log[3].equals(guardId)) {
				guardSet.clear();
			}

		}		
		
		 	
		
		//���ѹر�
		if ("201".equals(log[4])) {
			TParm logParm = new TParm();
			logParm.setData("CABINET_ID", cabinetId);
			logParm.setData("LOG_TIME", SystemTool.getInstance().getDate());
			logParm.setData("TASK_TYPE", "6");
			logParm.setData("TASK_NO", dispenseNo);
			logParm.setData("EVENT_TYPE", "2");
			logParm.setData("GUARD_ID", log[3]);
			logParm.setData("OPT_USER", userId);
			logParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
			logParm.setData("OPT_TERM", Operator.getIP());
			SPCInStoreTool.getInstance().insertLog(logParm);		
			 					
		}
		
		System.out.println(logStr);
		//21:ˢ��     14��ָ��
		if("21".equals(log[4]) ) {
			int ret = 0;
			int operid = 1;
			int outputadr = 1;			
			int doorstate = 6;
			if(log[3].equals(guardId)) {
				System.out.println(logStr);
				guardSet.add(log[2]);
				if(guardSet.size()==2) {
					ret=PullDriver.ControlDevice(rtn1, operid, Integer.valueOf(log[3]), outputadr, doorstate,0, "");
					if(ret<0){
						this.messageBox("����ʧ��");			
					}
				}
			}
			else {
				ret=PullDriver.ControlDevice(rtn1, operid, Integer.valueOf(log[3]), outputadr, doorstate,0, "");
				if(ret<0){
					this.messageBox("����ʧ��");			
				}
			}


		}
		
		//ָ��
		/**
		if(("21".equals(log[4])||"14".equals(log[4]))&&log[3].equals(DOOR_DRUG)) {				
			int ret = 0;
			int operid = 1;
			int doorid = 1;
			int outputadr = 1;
			int doorstate = 6;
			ret=PullDriver.ControlDevice(rtn1, operid, doorid, outputadr, doorstate, 1, "");
			if(ret<0){
				this.messageBox("����ʧ��");
			}
		}*/
		
		if("35".equals(log[4])) {							
			int ret = 0;
			int operid = 1;
			int outputadr = 1;
			int doorstate = 6;
			if(log[3].equals(guardId)) {
				guardSet.add(log[2]);
				if(guardSet.size()==2) {
					ret=PullDriver.ControlDevice(rtn1, operid, Integer.valueOf(log[3]), outputadr, doorstate,0, "");
					if(ret<0){
						this.messageBox("����ʧ��");			
					}
				}
			}
			else {
				ret=PullDriver.ControlDevice(rtn1, operid, Integer.valueOf(log[3]), outputadr, doorstate,0, "");
				if(ret<0){
					this.messageBox("����ʧ��");			
				}
			}
		}	
													
		/*onStart();              //�����������¼���ʼ��RFID����
		logTimer.cancel();
		logTask.cancel();*/		 		
	}
	

	
	private TimerTask logTask; // log����

	private Timer logTimer;

	private long period = 1 * 1000; // ���ʱ��

	private long delay = 1*1000; // �ӳ�ʱ��		
	
	 boolean  isOpen = false ;
	 
	public void doJob() {
		this.logTimer = new Timer();
		this.logTask = new TimerTask() {
			public void run() {
				onLog();
				isOpen = true;
			}
		};
		this.logTimer.schedule(this.logTask, this.delay, this.period);
	}
	
	public void procesClose(){
		logTimer.cancel();
		logTask.cancel();	
		isOpen = false;
	}
	
	
	public void onWindowClose(){
				
		//PullDriver.Disconnect(rtn1);
		// ע��dll
		//PullDriver.free();
		if(logTask != null ){
			logTask.cancel();	
		}
		
		if(logTimer != null ){
			logTimer.cancel();
		}
		
		isOpen = false;
		this.closeWindow();	
	}
	
	private BILComparator compare=new BILComparator();
	
	private boolean ascending = false;
	private int sortColumn = -1;
	/**
	 * �����������������
	 * @param table
	 */
	public void addListener(final TTable table) {

		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);

				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� 
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = getTable("TABLE_LIST").getParmValue();
				//TParm tableData = getTTable("TABLE_M").getShowParmValue();
				//System.out.println("tableData:"+tableData);
				tableData.removeGroupData("SYSTEM");

				// ==========modify-end========================================
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
				String tblColumnName = getTable("TABLE_LIST").getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				TParm lastResultParm=new TParm();//��¼���ս��
				lastResultParm=cloneVectoryParam(vct, new TParm(), strNames);//�����м�����
			
				table.setParmValue(lastResultParm);
				// ==========modify-end========================================

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}	
	
	
	/**
	 * ����������
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
	 * @author liyh
	 * @date 20120710
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

	/**
	 * vectoryת��param
	 */
	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
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
		return parmTable;

	}
	
}
