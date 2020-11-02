package com.javahis.ui.spc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import jdo.spc.SPCCabinetTool;
import jdo.spc.SPCContainerTool;
import jdo.spc.SPCOutStoreMLTool;
import jdo.spc.SPCOutStoreTool;
import jdo.spc.SPCToxicTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>Title: ��ʱҽ��/������-סԺҩ���龫�����ܹ�</p>
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
public class SPCOutStoreMLControl extends TControl{

	/**
	 * ����
	 */
	TTable table_D ;
	
	/**
	 * �龫
	 */
	TTable table_M;
	
	String ip ;
	
	String phaDispenseNo ;
	
	String containerId ;
	
	/**
	 * ���ܹ񱾻�����IP
	 */
	String guardIp;
	
	/**
	 * ���ܹ���
	 */
	String orgCode ;
	
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		table_M = getTable("TABLE_M");
		table_D = getTable("TABLE_D");
		
		initData() ;
	 
	}

	/**
	 * ������������
	 * @param parm
	 */
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
			
			inParmAll.setData("DISPENSE_NO",phaDispenseNo);
			inParmAll.setData("DISPENSE_SEQ_NO",seqNo);
			inParmAll.setData("CONTAINER_ID",containerId);
			
			inParmAll.setData("CONTAINER_DESC",parm.getValue("CONTAINER_DESC",k));
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
				inParm2.setData("DISPENSE_NO",count2,phaDispenseNo);
				inParm2.setData("DISPENSE_SEQ_NO",count2,seqNo);
				inParm2.setData("CABINET_ID",count2,getValueString("CABINET_ID")); 
				inParm2.setData("TOXIC_ID",count2,rowParm.getValue("TOXIC_ID"));
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
                    "onInsertMl", inParm);
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
			
	}
	
	
	/**
	 * ��ʼ������
	 */
	public void initData(){
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
		orgCode = result.getValue("ORG_CODE",0);
		setValue("CABINET_ID", result.getValue("CABINET_ID", 0));
		setValue("CABINET_DESC", result.getValue("CABINET_DESC", 0));
		setValue("ORG_CHN_DESC", result.getValue("ORG_CHN_DESC", 0));
		
	}
	
	public void onQuery(){
		
		table_D = getTable("TABLE_D");
		phaDispenseNo = getValueString("PHA_DISPENSE_NO");
		
		TParm parm = new TParm();
		parm.setData("PHA_DISPENSE_NO",phaDispenseNo);
		TParm result = SPCOutStoreMLTool.getInstance().onQuery(parm);
		
		if(result.getCount() < 0 ){
			table_D.setParmValue(new TParm());
			this.messageBox("û�в�ѯ������");
			
			return ;
		}
		//�����Ϊ�գ���ȡ����
		String stationCode = result.getValue("STATION_CODE",0);
		setValue("STATION_CODE", stationCode);
		table_D.setParmValue(result);
		table_D.setSelectedRow(0);
		this.getTextField("CONTAINER_ID").grabFocus();
		
	}
	
	public void onPhaDispenseNoClicked(){
		onQuery();
	}
	
	 
	/**
	 * �����������¼�
	 */
	public void onContainerIdClicked(){
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
				return ;
			}else {
				double toxicQty = result.getDouble("TOXIC_QTY",0);
				setValue("TOXIC_QTY", toxicQty);
				setValue("CONTAINER_DESC", result.getValue("CONTAINER_DESC",0));
			}
		}else {
			this.messageBox("û�и�����!");
			setValue("CONTAINER_ID", "");
			setValue("CONTAINER_DESC","");
			setValue("TOXIC_QTY", "");
			this.getTextField("CONTAINER_ID").grabFocus();
			return ;
		}
		this.getTextField("TOXIC_ID").grabFocus();
	}
	
	/**
	 * �龫��ˮ�ŵ����¼�
	 */
	public void onToxicIdClicked(){
		table_D.acceptText();
		table_M.acceptText() ;
		
		 
		containerId = getValueString("CONTAINER_ID");
		phaDispenseNo = getValueString("PHA_DISPENSE_NO");
		
		//����ʾ��������������
		String containerDesc = getValueString("CONTAINER_DESC");
		String cabinetId = getValueString("CABINET_ID");
		
		 
		if(StringUtil.isNullString(containerId)){
			this.messageBox("����ID����Ϊ��,����ɨ��");
			return ;
		}
		
		int tableDRow = table_D.getSelectedRow() ;
		int dispenseQty  = table_D.getParmValue().getInt("DISPENSE_QTY",tableDRow);
		int acumOutBoundQty  = table_D.getParmValue().getInt("ACUM_OUTBOUND_QTY",tableDRow);
		
		if(dispenseQty == acumOutBoundQty ){
			this.messageBox("�ѳ������");
			return  ;
		}
		
		String toxicId = getValueString("TOXIC_ID");
		if(toxicId != null && !toxicId.equals("")){
			
			
			//�����龫��ˮ����orcer_code��ѯ��Ӧ��
			TParm inParm = new TParm();
			
			//�Ȳ�ѯ�������ܲ���װ
			 
			String orderCode  = table_D.getParmValue().getValue("ORDER_CODE",tableDRow);
			
			/**
			 * ODI_DSPNM �ĸ�����
			 */
			String seqNo =  table_D.getParmValue().getValue("ORDER_SEQ",tableDRow);
			String startDttm = table_D.getParmValue().getValue("START_DTTM",tableDRow);
			String  orderNo = table_D.getParmValue().getValue("ORDER_NO",tableDRow);
			String caseNo = table_D.getParmValue().getValue("CASE_NO",tableDRow);
			inParm.setData("START_DTTM",startDttm);
			inParm.setData("ORDER_NO",orderNo);
			inParm.setData("CASE_NO",caseNo);
			inParm.setData("ORDER_SEQ",seqNo);
			 
			inParm.setData("ORDER_CODE",orderCode);
			inParm.setData("TOXIC_ID",toxicId);
			inParm.setData("CABINET_ID",cabinetId);
			
			TParm toxParm = new TParm();
			toxParm.setData("DISPENSE_NO",phaDispenseNo);
			toxParm.setData("DISPENSE_SEQ_NO",seqNo);
			toxParm.setData("CONTAINER_ID",containerId);
			TParm dParm = SPCToxicTool.getInstance().onQueryDByCount(toxParm);
			int ccout = dParm.getInt("NUM",0) ;
			 
			int toxicQty = getValueInt("TOXIC_QTY");
			if(ccout == toxicQty ){
				this.messageBox("��������!");
				setValue("CONTAINER_ID", "");
				this.getTextField("CONTAINER_ID").grabFocus();
				return  ;
			}
			
			TParm parm = SPCContainerTool.getInstance().onQuery(inParm);
			if(parm.getCount() <=  0){
				this.messageBox("���޸��龫ҩƷ,�����¼�ѡ!");
				return  ;
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
						this.messageBox("�����ܹ�ʧ��");
						return ;
					}else {						
						 
						table_D.setItem(tableDRow, "ACUM_OUTBOUND_QTY", acumOutBoundQty+1);
					}
				}
			}else {
				TParm result = batchSave(containerDesc, inParm);
				if(result.getErrCode() < 0 ){
					this.messageBox("�����ܹ�ʧ��");
					return ;
				}else {
					 
					table_D.setItem(tableDRow, "ACUM_OUTBOUND_QTY", acumOutBoundQty+1);
				}
			}
			
			//��ѯ��������ʾ����
			TParm p = SPCToxicTool.getInstance().onQuery(inParm);
			
			table_M.setParmValue(p);
			setValue("TOXIC_ID", "");
			 
		}
		
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
		inParm.setData("DISPENSE_NO",getValueString("PHA_DISPENSE_NO"));
		inParm.setData("SEQ_NO",parm.getValue("ORDER_SEQ",row));
		inParm.setData("ORDER_CODE",parm.getValue("ORDER_CODE",row));
		
		TParm p = SPCToxicTool.getInstance().onQuery(inParm);
		table_M.setParmValue(p);
		if(acumOutBoundQty < dispenseQty ){
			 
			getTextField("CONTAINER_ID").setEditable(true);
			getTextField("TOXIC_ID").setEditable(true);
		}else {
			//������һ���հ���
			this.messageBox("ȫ���������");
			getTextField("CONTAINER_ID").setEditable(false);
			getTextField("TOXIC_ID").setEditable(false);
			
		}	
		
		setValue("CONTAINER_ID", "");
		setValue("TOXIC_QTY", "");
		setValue("CONTAINER_DESC", "");
		
		table_D.setSelectedRow(row);
	}
	
	public void onSave(){
		
	}
	
	public void onCLear(){
		String controlName = "PHA_DISPENSE_NO;CONTAINER_ID;TOXIC_ID;TOXIC_QTY;CONTAINER_DESC";
		this.clearValue(controlName);
		this.getTextField("CONTAINER_ID").setEditable(true);
		this.getTextField("TOXIC_ID").setEditable(true);
		
		table_M = getTable("TABLE_M");
		table_D = getTable("TABLE_D");
		table_M.acceptText() ;
		table_D.acceptText() ;
		table_D.setParmValue(new TParm());
		table_M.setParmValue(new TParm());
	}
	

	/**
	 * ���ܹ����ѯ
	 */
	public void openSearch(){
	    	
    	TParm parm = new TParm();
    	parm.setData("CABINET_ID",getValueString("CABINET_ID"));
    	Object result = openDialog("%ROOT%\\config\\spc\\SPCContainerStatic.x", parm);

    }
	
	private TParm batchSave(String containerDesc, TParm inParm) {
		int tableDRow = table_D.getSelectedRow() ;
		TParm tableDParm = table_D.getParmValue() ;
		phaDispenseNo = getValueString("PHA_DISPENSE_NO");
		String seqNo = tableDParm.getValue("ORDER_SEQ",tableDRow);
		inParm.setData("DISPENSE_NO",phaDispenseNo);
		inParm.setData("DISPENSE_SEQ_NO",seqNo);
		inParm.setData("SEQ_NO",seqNo);
		inParm.setData("IS_BOXED","Y");
		inParm.setData("BOXED_USER",Operator.getID());
		inParm.setData("CONTAINER_ID",containerId);
		inParm.setData("CONTAINER_DESC",containerDesc);
		inParm.setData("OPT_USER",Operator.getID());
		inParm.setData("OPT_TERM",Operator.getIP());
		inParm.setData("ORG_CODE",orgCode);
		inParm.setData("APP_ORG_CODE",getValue("STATION_CODE"));
		
		//д���ݵ��������ױ���������ϸ��,����ʵ�ʳ�������
		TParm result  = TIOM_AppServer.executeAction("action.spc.SPCOutStoreAction",
		                                    "onInsertMl", inParm);
		return result ;
	}
	
	 
	
	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}
	
	


}
