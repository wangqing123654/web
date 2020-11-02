package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import jdo.inv.INVNewBackDisnfectionTool;
import jdo.inv.INVNewRepackTool;
import jdo.inv.INVNewSterilizationTool;
import jdo.inv.INVRepackHelpTool;
import jdo.inv.InvPackStockMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;


/**
 * 
 * <p>
 * Title:���
 * </p>
 * 
 * <p>
 * Description: ���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wangming 2013-8-2
 * @version 1.0
 */
public class INVNewRepackControl extends TControl{

	private TTable tableRepack;			//�������
	
	private TTable tablePackM;			//����������
	
	private TTable tablePackD;			//��������ϸ
	
	private String repackNo;			//��ʼ���������
	
	private boolean isImport = false;		//�Ƿ��ǵ�����յ����  	true����		false������
	
	private boolean isNew = false;			//�Ƿ����½����յ�	true����		false������
	
	private String recycleNo;			//���صĻ��յ���
	
	
	/**
	 * ��ʼ��
	 */
	public void onInit() {
	
		tableRepack = (TTable) getComponent("TABLE_REPACK");
		tablePackM = (TTable) getComponent("TABLE_PACKM");
		tablePackD = (TTable) getComponent("TABLED");
		
		this.onInitDate();
		
		tableRepack.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
		"onCheckBoxClicked");
		
		//��������������ѡ�񴰿�
		TParm parm = new TParm();
		((TTextField)getComponent("PACK_CODE")).setPopupMenuParameter("UD",
	            getConfigParm().newConfig(
	                "%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
	    // ������ܷ���ֵ����
		((TTextField)getComponent("PACK_CODE")).addEventListener(TPopupMenuEvent.
	            RETURN_VALUE, this, "popReturn");
		
		//����Ĭ�Ͽ���
		TTextFormat tf = (TTextFormat)getComponent("ORG_CODE");
        tf.setValue(Operator.getDept());
		
	}
	
	
	
	/**
	 * ɨ������
	 */
	public void onScream() {
		
		if(isImport){
			messageBox("������յ�״̬�²��������������");
			return ;
		}
		
		int r = tableRepack.getSelectedRow();
		if(r<0){
			messageBox("��ѡ��������");
			return ;
		}
		
		if(isNew){
			String packageCode = getValueString("SCREAM");
			if (packageCode == null || packageCode.length() == 0) {
				return;
			}
//			setValue("PACK_CODE", packageCode.substring(0, packageCode.length() - 4));
//			setValue("PACK_SEQ_NO",
//					packageCode.substring((packageCode.length() - 4), packageCode.length()));
			
			this.addPackage();	//�����������������
			this.grabFocus("SCREAM");	//��λ����
		}
		((TTextField)getComponent("SCREAM")).setValue("");
		
	}
	
	/**
	 * ����յ����������������
	 */
	public void addPackage(){
		
		int r = tableRepack.getSelectedRow();
		if(r<0){
			messageBox("��ѡ��������");
			return ;
		}
		
		boolean tag = false;
		tag = this.checkConditions();
		if(!tag){		//��������������
			return;
		}
		
		TParm parm = new TParm();
		parm.setData("BARCODE", this.getValueString("SCREAM"));
//		parm.setData("PACK_CODE", this.getValueString("PACK_CODE"));
//		parm.setData("PACK_SEQ_NO", this.getValueString("PACK_SEQ_NO"));
		
		TParm result = new TParm();
		result = INVNewBackDisnfectionTool.getInstance().queryPackageInfoByBarcode(parm);	//��ѯ��������Ϣ���������룩
		
		if(result.getCount()<=0){
			return;
		}
		
		if(result.getData("STATUS",0).equals("5")){
			messageBox("�������Ѵ���ά��״̬���޷����գ�");
			return;
		}
//		if(result.getData("STATUS",0).equals("3")){
//			messageBox("�������Ѵ������״̬��");
//			return;
//		}
//		if(result.getData("STATUS",0).equals("2")){
//			messageBox("�������Ѵ��ڻ���״̬��");
//			return;
//		}
		if(result.getData("STATUS",0).equals("0")){
			if(this.messageBox("��ʾ��Ϣ", "�����������ڿ�״̬���Ƿ��ٴδ����", this.YES_NO_OPTION)==1){
				return;
			}
		}
		
		Timestamp date = SystemTool.getInstance().getDate();
		
		int valid = Integer.parseInt(result.getData("VALUE_DATE",0).toString()); 	//��Ч����   ��λ����
		
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
        cal.add(cal.DATE,valid);//����������������Ӧ������
        Date d=cal.getTime(); 
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateString = formatter.format(d);

//		TParm rowRecord = new TParm();
//		rowRecord.setData("BARCODE",result.getData("BARCODE",0));
//		rowRecord.setData("PACK_DESC",result.getData("PACK_DESC",0));
//		rowRecord.setData("PACK_CODE",result.getData("PACK_CODE",0));
//		rowRecord.setData("PACK_SEQ_NO",result.getData("PACK_SEQ_NO",0));
//		rowRecord.setData("QTY",result.getData("QTY",0));
//		rowRecord.setData("STATUS","���");
//		rowRecord.setData("AUDIT_DATE",date);				//�������
//		rowRecord.setData("AUDIT_USER",this.getValueString("AUDIT_USER"));	//�����Ա
//		rowRecord.setData("REPACK_DATE",date);				//�������		
//		rowRecord.setData("REPACK_USER",Operator.getID());	//�����Ա
//		tablePackM.addRow(rowRecord);
		
		TParm tp = new TParm();
		tp = tablePackM.getParmValue();
		int row = -1;
		if(tp == null){
			tp = new TParm();
			row = 0;
		}else{
			row = tp.getCount();
		}
		
		tp.setData("BARCODE", row, result.getData("BARCODE",0));
		tp.setData("PACK_DESC", row, result.getData("PACK_DESC",0));
		tp.setData("PACK_CODE", row, result.getData("PACK_CODE",0));
		tp.setData("PACK_SEQ_NO", row, result.getData("PACK_SEQ_NO",0));
		tp.setData("QTY", row, result.getData("QTY",0));
		tp.setData("STATUS", row, "���");
		tp.setData("AUDIT_DATE", row, date);
		tp.setData("AUDIT_USER", row, this.getValueString("AUDIT_USER"));
		tp.setData("REPACK_DATE", row, date);
		tp.setData("REPACK_USER", row, Operator.getID());
		tablePackM.setParmValue(tp);
		
		
		this.grabFocus("PACK_SEQ_NO");	//��λ����
	}
	
	/**
	 * �������յ�����
	 * */
	public void onNew(){
		
		if(isImport||isNew){
			messageBox("����ͬʱ�½��������յ���");
			return;
		}

		if (this.getValueString("ORG_CODE").length() == 0) {
			messageBox("��Ӧ�Ҳ��Ų���Ϊ�գ�");
			return;
		}
		
		isNew = true;				//������½�״̬
		this.clearTable(tableRepack);
		this.clearTable(tablePackM);
		this.clearTable(tablePackD);
		
		repackNo = this.getRepackNo();
		this.createNewRepack();    //����һ���������¼
		
	}
	
	public void onSave(){
		
		if(isImport){	//������յ�
			boolean tag = true;	//tagΪtrueʱ˵����������������ɹ�
			
			TParm repackTable = tableRepack.getParmValue();
			TParm packageMTable = tablePackM.getParmValue();
						
			repackTable.setData("OPT_USER",0, Operator.getID());


		///////ԭstart
			TParm condition = new TParm();
			String barcodes = "";
			for(int i=0;i<packageMTable.getCount("BARCODE");i++){
				barcodes = barcodes + packageMTable.getRow(i).getValue("BARCODE") + ",";
				barcodes = barcodes.substring(0,barcodes.lastIndexOf(","));
				condition.setData("BARCODE",0, barcodes);
			}
			TParm allMaterial = new TParm();
			allMaterial = INVRepackHelpTool.getInstance().queryHighOnceMaterial(condition.getRow(0));//����������һ�������ʵ�����������
		///////ԭend
			
			
			
			
			TParm parm = new TParm();
			for(int i=0;i<packageMTable.getCount();i++){
				parm = new TParm();
				TParm temp = new TParm();
				temp.addData("RECYCLE_NO", recycleNo);
				
				parm.setData("REPACK", repackTable.getData());
				parm.setData("PACKAGEMAINTABLE", packageMTable.getRow(i).getData());
				parm.setData("RECYCLENO", temp.getData());
				
				
				
				//���������һ��������start
				boolean isHO = false;//�Ƿ��������һ��������
				if(allMaterial!=null){
					for(int j=0;j<allMaterial.getCount();j++){
						String barcode = packageMTable.getRow(i).getValue("BARCODE");
						if(allMaterial.getRow(j).getValue("BARCODE").equals(barcode)){
							isHO = true;
						}
					}
				}
				TParm highOnce = new TParm();	//��ŵ��δ����Ҫ�������һ����������Ϣ
				if(isHO){//�������һ�������ʵ����������ʱ����ѡ�������һ��������
					TParm hoParm = INVRepackHelpTool.getInstance().queryHighOnceMaterial(packageMTable.getRow(i));
					if(hoParm!=null){
						for(int j=0;j<hoParm.getCount("BARCODE");j++){
							Object obj = openDialog("%ROOT%\\config\\inv\\INVRepackStockDD.x",hoParm.getRow(j));
							if (obj != null) {
					            TParm materialRFID = (TParm) obj;
					            for(int m=0;m<materialRFID.getCount("RFID");m++){
//					            	highOnce.addParm(materialRFID.getRow(m));
					            	int rowC = -1;
					            	if(highOnce==null){
					            		rowC = 0;
					            	}else if(highOnce.getCount("RFID") == -1){
					            		rowC = 0;
					            	}
					            	highOnce.setData("RFID", rowC,  materialRFID.getRow(m).getValue("RFID"));
					            	highOnce.setData("PACK_CODE", rowC, hoParm.getRow(j).getValue("PACK_CODE"));
					            	highOnce.setData("PACK_SEQ_NO", rowC, hoParm.getRow(j).getValue("PACK_SEQ_NO"));
					            }
					        }else{
					        	tag = false;	//�в�������û�п��
					        }
						}
						parm.setData("HOMATERIAL", highOnce.getData());	//��������������ѡ�е������һ��������
					}
				}
				
				
				//���������һ��������end
				
				
				TParm result = TIOM_AppServer.executeAction("action.inv.INVNewRepackAction",
			            "onInsert", parm);
				if (result.getErrCode() < 0) { 
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					messageBox("����ʧ�ܣ�");
				}
				if(result.getData("ENOUGH").equals("NO")){
					tag = false;
				}
			}
			if(!tag){
				messageBox("�в���������δ����ɹ�����ȷ�ϣ�");
			}else{
				messageBox("����ɹ���");
			}
			isImport = false;
			recycleNo = "";
			this.onClear();	//��ս��棬��Ǹ���ʼֵ
		}
		if(isNew){
			boolean tag = true;	//tagΪtrueʱ˵����������������ɹ�
			
			TParm repackTable = tableRepack.getParmValue();
			TParm packageMTable = tablePackM.getParmValue();
			
			if(packageMTable.getCount("BARCODE") == 0){
				messageBox("�������ϸ���Ϊ�գ�");
				return;
			}
			
			repackTable.setData("OPT_USER",0, Operator.getID());

			
			///////ԭstart
			TParm condition = new TParm();
			String barcodes = "";
			for(int i=0;i<packageMTable.getCount("BARCODE");i++){
				barcodes = barcodes + packageMTable.getRow(i).getValue("BARCODE") + ",";
				barcodes = barcodes.substring(0,barcodes.lastIndexOf(","));
				condition.setData("BARCODE",0, barcodes);
			}
			TParm allMaterial = new TParm();
			allMaterial = INVRepackHelpTool.getInstance().queryHighOnceMaterial(condition.getRow(0));//����������һ�������ʵ�����������
			
//			List rfidList = new ArrayList();
//			if(allMaterial!=null){
//				for(int j=0;j<allMaterial.getCount();j++){
//					TParm diaParm = new TParm();
//					diaParm.setData("Material", allMaterial.getRow(j));
//					diaparm.setData("List",rfidList);
//					Object obj = openDialog("%ROOT%\\config\\inv\\INVRepackStockDD.x",allMaterial.getRow(j));
//					if (obj != null) {
//			            TParm temp = (TParm) obj;
//			        }
//				}
//			}
			///////ԭend
			
			TParm parm = new TParm();
			for(int i=0;i<packageMTable.getCount("BARCODE");i++){
				parm = new TParm();
				TParm temp = new TParm();
				temp.addData("RECYCLE_NO", recycleNo);
				
				parm.setData("REPACK", repackTable.getData());
				parm.setData("PACKAGEMAINTABLE", packageMTable.getRow(i).getData());
				parm.setData("RECYCLENO", temp.getData());
				
				
				//���������һ��������start
				boolean isHO = false;//�Ƿ��������һ��������
				if(allMaterial!=null){
					for(int j=0;j<allMaterial.getCount();j++){
						String barcode = packageMTable.getRow(i).getValue("BARCODE");
						if(allMaterial.getRow(j).getValue("BARCODE").equals(barcode)){
							isHO = true;
						}
					}
				}
				TParm highOnce = new TParm();	//��ŵ��δ����Ҫ�������һ����������Ϣ
				if(isHO){//�������һ�������ʵ����������ʱ����ѡ�������һ��������
					TParm hoParm = INVRepackHelpTool.getInstance().queryHighOnceMaterial(packageMTable.getRow(i));
					if(hoParm!=null){
						for(int j=0;j<hoParm.getCount("BARCODE");j++){
							Object obj = openDialog("%ROOT%\\config\\inv\\INVRepackStockDD.x",hoParm.getRow(j));
							if (obj != null) {
					            TParm materialRFID = (TParm) obj;
					            for(int m=0;m<materialRFID.getCount("RFID");m++){
//					            	highOnce.addParm(materialRFID.getRow(m));
					            	int rowC = -1;
					            	if(highOnce==null){
					            		rowC = 0;
					            	}else if(highOnce.getCount("RFID") == -1){
					            		rowC = 0;
					            	}
					            	highOnce.setData("RFID", rowC,  materialRFID.getRow(m).getValue("RFID"));
					            	highOnce.setData("PACK_CODE", rowC, hoParm.getRow(j).getValue("PACK_CODE"));
					            	highOnce.setData("PACK_SEQ_NO", rowC, hoParm.getRow(j).getValue("PACK_SEQ_NO"));
					            }
					        }else{
					        	tag = false;	//�в�������û�п��
					        }
						}
						parm.setData("HOMATERIAL", highOnce.getData());	//��������������ѡ�е������һ��������
					}
				}
				
				
				//���������һ��������end
				
			
				TParm result = TIOM_AppServer.executeAction("action.inv.INVNewRepackAction",
			            "onInsert", parm);
				if (result.getErrCode() < 0) { 
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					messageBox("����ʧ�ܣ�");
				}
				if(result.getData("ENOUGH").equals("NO")){
					tag = false;
				}
			}
			if(!tag){
				messageBox("�в���������δ����ɹ�����ȷ�ϣ�");
			}else{
				messageBox("����ɹ���");
			}
			isNew = false;
			recycleNo = "";
			this.onClear();	//��ս��棬��Ǹ���ʼֵ
		}
		
		
	}
	
	/**
	 * �������յ�
	 */
	public void onSelectBill(){
		
		if(isNew||isImport){
			messageBox("����ͬʱ�½������������");
			return;
		}
		
		if (this.getValueString("ORG_CODE").length() == 0) {
			messageBox("�����빩Ӧ�Ҳ��ţ�");
			return;
		}
		if (this.getValueString("AUDIT_USER").length() == 0) {
			messageBox("�����������Ա���ţ�");
			return;
		}
		
		Object obj = openDialog("%ROOT%\\config\\inv\\INVChooseRecycleBill.x");
		TParm parm = (TParm)obj;
		
		if(parm.getErrCode() < 0){
			this.messageBox("�޴������ݣ�");
			return;
		}
		
		recycleNo = parm.getData("RECYCLE_NO").toString();
		
		TParm result = INVNewRepackTool.getInstance().queryDisnfectionByNo(parm);
		
		if(result.getErrCode() < 0){
			this.messageBox("�޲�ѯ���ݣ�");
			return;
		}
		
		Timestamp date = SystemTool.getInstance().getDate();
		
		
		this.clearTable(tableRepack);
		this.clearTable(tablePackM);
		this.clearTable(tablePackD);
		repackNo = this.getRepackNo();
		this.createNewRepack();
		
		
		for(int i=0;i<result.getCount();i++){
			result.setData("STATUS", i, "���");
			result.setData("AUDIT_DATE", i, date);
			result.setData("AUDIT_USER", i, this.getValueString("AUDIT_USER"));
			result.setData("REPACK_DATE", i, date);
			result.setData("REPACK_USER", i, Operator.getID());
			
			result.setData("OPT_DATE",i,date);
			result.setData("OPT_USER",i,Operator.getID());
			result.setData("OPT_TERM",i,Operator.getIP());
			
			result.setData("ORG_CODE",i,this.getValueString("ORG_CODE"));
		}
		tablePackM.setParmValue(result);
		isImport = true;
	}
	
	/**
	 * �������¼�
	 */
	public void onTableMClicked() {
		
		int row = tablePackM.getSelectedRow();
		String packCode = tablePackM.getShowParmValue().getValue("PACK_CODE", row);
		String packSeqNo = tablePackM.getShowParmValue().getValue("PACK_SEQ_NO", row);
		
		TParm parm = new TParm();
		parm.setData("PACK_CODE", packCode);
		parm.setData("PACK_SEQ_NO", packSeqNo);
		
		TParm result = new TParm();
		result = INVNewSterilizationTool.getInstance().queryPackageDetailInfo(parm);
		
		
		tablePackD.setParmValue(result);
	}
	
	public void onTableRepackClicked(){
		
		if(!isImport&&!isNew){
			int row = tableRepack.getSelectedRow();
			String repackNo = "";
			if(row>=0){
				repackNo = tableRepack.getItemData(row, "REPACK_NO").toString();
			}else{
				return;
			}
			
			TParm parm = new TParm();
			parm.setData("REPACK_NO", repackNo);
			
			if(this.getRadioButton("radioYes").isSelected()){
				parm.setData("FINISH_FLG", "Y");
			}else if(this.getRadioButton("radioNo").isSelected()){
				parm.setData("FINISH_FLG", "N");
			}
			
			TParm result = INVNewRepackTool.getInstance().queryRepackByNo(parm);
			
			if(result.getCount()>0){
				for(int i=0;i<result.getCount();i++){
					result.setData("STATUS", i, "���");
				}
			}
			tablePackM.removeRowAll();
			tablePackM.setParmValue(result);
			tablePackD.removeRowAll();
		}
		
	}
	
	public void onQuery(){
		
		if(isImport){
			int count = tablePackM.getRowCount();
			if(count>0){
				if(this.messageBox("��ʾ��Ϣ", "�½��������Ϣ��δ���棬�Ƿ񱣴棿", this.YES_NO_OPTION)==0){
					//����
					this.onSave();
				}else{
					this.onClear();
				}
			}
		}

		String startDate = getValueString("START_STER_DATE");	//��ʼʱ��
		String endDate = getValueString("END_STER_DATE");		//��ֹʱ��

		if (startDate == null || startDate.length() == 0 || endDate == null || endDate.length() == 0) {
			messageBox("��������ֹʱ��!");
			return;
		}
		
		
		TParm parm = new TParm();			//��ѯ����
		if(this.getRadioButton("radioYes").isSelected()){
			parm.setData("FINISH_FLG", "Y");
		}else if(this.getRadioButton("radioNo").isSelected()){
			parm.setData("FINISH_FLG", "N");
		}
		if(!getValueString("REPACK_NO").equals("")){
			parm.setData("REPACK_NO", getValueString("REPACK_NO"));
		}
		parm.setData("START_DATE", startDate.substring(0, 10)+" 00:00:00");
		parm.setData("END_DATE", endDate.substring(0, 10)+" 23:59:59");
		
		TParm result = INVNewRepackTool.getInstance().queryRepack(parm);
		
		if(!result.equals(null)){
			for(int i=0;i<result.getCount();i++){
				result.setData("DELETE_FLG", i, "N");
			}
		}
		
		tableRepack.setParmValue(result);
		
	}
	
	
	public void onDelete(){
		
		if(isImport){	//������յ�״̬ʱ����
			TParm disTable = tableRepack.getShowParmValue();
			String tag = disTable.getData("DELETE_FLG", 0).toString();
			if(tag.equals("N")){//�ж��������ɾ�����ձ��Ƿ���ɾ����������Ϣ
				
				messageBox("��ѡ��������");
				return;
			}else{//�����ɾ�����ձ�
				this.onClear();
			}
		}else if(isNew){	//��������յ�״̬ʱ����
			TParm disTable = tableRepack.getShowParmValue();
			String tag = disTable.getData("DELETE_FLG", 0).toString();
			if(tag.equals("N")){//�ж��������ɾ��������Ƿ���ɾ����������Ϣ

				int row = tablePackM.getSelectedRow();
				if(row>=0){
					tablePackM.removeRow(row);
					clearTable(tablePackD);
					return;
				}
				
				messageBox("��ѡ��������");
				return;
			}else{//�����ɾ�������
				this.onClear();
			}
		}else{
			TParm disTable = tableRepack.getShowParmValue();
			String tag = disTable.getData("DELETE_FLG", 0).toString();
			if(tag.equals("Y")){
				messageBox("����ɵĴ��������ɾ����");
				return;
			}
			int row = tablePackM.getSelectedRow();
			if(row>=0){
				if(tablePackM.getParmValue().getRow(row).getData("DELETE_FLG").equals("Y")){
					messageBox("����ɵĴ������ϸ����ɾ����");
					return;
				}
			}
		}
	}
	/**
	 * ��ӡ�����
	 */
	public void onPrint(){
		
		int row = tableRepack.getSelectedRow();
		if (row < 0){
			messageBox("��ѡ��������");
			return;
		}
		
		
		int rowM = tablePackM.getSelectedRow();
		if (rowM < 0){
			messageBox("��ѡ����������");
			return;
		}
		
		String RepackNo = tableRepack.getItemString(row, "REPACK_NO");
		String RepackNoOrgCode = tableRepack.getItemString(row, "ORG_CODE");
		String RepackNoOptUser = tableRepack.getItemString(row, "OPT_USER");
		
		String barcode = tablePackM.getItemString(rowM, "BARCODE");//ѡ��������������
		String packCode= tablePackM.getItemString(rowM, "PACK_CODE");//����������
		
		TParm conditions = new TParm();
		conditions.setData("BARCODE",barcode);
		conditions.setData("REPACK_NO", RepackNo);
		conditions.setData("PACK_CODE", packCode);
		
		
		TParm packList = new TParm();
		packList = INVNewRepackTool.getInstance().queryPackList(conditions);
		
		
		
		TParm parm = new TParm();
		parm.setData("ORG_CODE", RepackNoOrgCode);
		//��ò�������
		TParm deptInfo = INVNewBackDisnfectionTool.getInstance().queryDeptName(parm);
		
		parm = new TParm();
		parm.setData("USER_ID", RepackNoOptUser);
		
		//����û�����
		TParm userInfo = INVNewBackDisnfectionTool.getInstance().queryUserName(parm);
//		//��������
//		String optDate = tableRepack.getItemString(row, "OPT_DATE").substring(0, 10);
		
		TParm reportTParm = new TParm();
		reportTParm.setData("PACK_DESC", "TEXT", tablePackM.getItemString(rowM, "PACK_DESC"));	//����
		reportTParm.setData("PACK_BARCODE", "TEXT", barcode);	//����
		reportTParm.setData("PACK_USER", "TEXT", userInfo.getData("USER_NAME", 0));	//�����Ա
		reportTParm.setData("PACK_DATE", "TEXT", tableRepack.getItemString(row, "OPT_DATE").substring(0, 10));	//�������
		
		int tag=packList.getCount("QTY")%2;
		//�������
        TParm tableParm = new TParm();
		if(packList.getCount("QTY") == 1){
			tableParm.addData("SEQ_F", 1);
			tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", 0));
			tableParm.addData("QTY_F", packList.getData("QTY", 0));
			tableParm.addData("SEQ_S", "");
			tableParm.addData("CHN_DESC_S", "");
			tableParm.addData("QTY_S", "");
		}else if(packList.getCount("QTY") > 1){
			
			if(tag == 0){
				for(int i=0;i<packList.getCount("QTY");){
					tableParm.addData("SEQ_F", i+1);
					tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", i));
					tableParm.addData("QTY_F", packList.getData("QTY", i));
					tableParm.addData("SEQ_S", i+2);
					tableParm.addData("CHN_DESC_S", packList.getData("INV_CHN_DESC", i+1));
					tableParm.addData("QTY_S", packList.getData("QTY", i+1));
					i=i+2;
				}
			}
			if(tag == 1){
				for(int i=0;i<packList.getCount("QTY")-1;){
					tableParm.addData("SEQ_F", i+1);
					tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", i));
					tableParm.addData("QTY_F", packList.getData("QTY", i));
					tableParm.addData("SEQ_S", i+2);
					tableParm.addData("CHN_DESC_S", packList.getData("INV_CHN_DESC", i+1));
					tableParm.addData("QTY_S", packList.getData("QTY", i+1));
					i=i+2;
				}
				tableParm.addData("SEQ_F", packList.getCount("QTY"));
				tableParm.addData("CHN_DESC_F", packList.getData("INV_CHN_DESC", packList.getCount("QTY")-1));
				tableParm.addData("QTY_F", packList.getData("QTY", packList.getCount("QTY")-1));
				tableParm.addData("SEQ_S", "");
				tableParm.addData("CHN_DESC_S", "");
				tableParm.addData("QTY_S", "");
			}
		}
		
		
		
		tableParm.setCount(tableParm.getCount("SEQ_F"));
		
	    tableParm.addData("SYSTEM", "COLUMNS", "SEQ_F");
	    tableParm.addData("SYSTEM", "COLUMNS", "CHN_DESC_F");
	    tableParm.addData("SYSTEM", "COLUMNS", "QTY_F");
	    tableParm.addData("SYSTEM", "COLUMNS", "SEQ_S");
	    tableParm.addData("SYSTEM", "COLUMNS", "CHN_DESC_S");
	    tableParm.addData("SYSTEM", "COLUMNS", "QTY_S");

	    reportTParm.setData("TABLE", tableParm.getData());
		
//	    Timestamp date = SystemTool.getInstance().getDate();
//	    reportTParm.setData("OPTUSER", "TEXT", Operator.getName());
//	    reportTParm.setData("OPTDATE", "TEXT", date.toString().substring(0, 10));
		
		
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVNewPackageList.jhw",
				reportTParm);
		
		
		
	}
	
	
	
	
	
	
	/**
	 * ��ӡ����
	 */
	public void onBarcode(){
		
		TParm parm = new TParm();
		int row = tablePackM.getSelectedRow();
		if (row < 0){
			return;
		}
		String packCode = tablePackM.getItemString(row, "PACK_CODE");
		if (packCode == null || packCode.length() == 0) {
			messageBox("������ѡ�����!");
			return;
		}
		// ���������
		String strPackSeqNo = tablePackM.getItemString(row, "PACK_SEQ_NO");
		
		String packCodeSeq = strPackSeqNo;
		for (int i = packCodeSeq.length(); i < 4; i++) {
			packCodeSeq = "0" + packCodeSeq;
		}
		packCodeSeq = "" + packCodeSeq;
		
		int sterRow = tableRepack.getSelectedRow();
		if(sterRow<0){
			return;
		}
		
		String sterilizationNo = tableRepack.getItemString(sterRow, "REPACK_NO");
		TParm conditions = new TParm();
		conditions.setData("PACK_CODE",0, packCode);
		conditions.setData("PACK_SEQ_NO",0, Integer.parseInt(strPackSeqNo));
		conditions.setData("REPACK_NO",0, sterilizationNo);
		
		//��ѯ����������Ϣ
		TParm result = INVNewRepackTool.getInstance().queryBarcodeInfo(conditions.getRow(0));
		
		// �������    �������	
		String sterilizationDate = StringTool.getString((Timestamp)result.getData("VALID_DATE",0),"yyyy/MM/dd");
		String packageDate = StringTool.getString((Timestamp)result.getData("REPACK_DATE",0),"yyyy/MM/dd");
		
		
		TParm vParm = InvPackStockMTool.getInstance().getPackDate(packCode,Integer.parseInt(strPackSeqNo));
		int valid = Integer.parseInt(vParm.getData("VALUE_DATE",0).toString());
        Calendar cal = new GregorianCalendar();
        cal.set(Integer.parseInt(packageDate.substring(0, 4)), Integer.parseInt(packageDate.substring(5, 7))-1, Integer.parseInt(packageDate.substring(8,10)));
        cal.add(cal.DATE,valid);//��������������N��
        Date d=cal.getTime(); 
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(d);
		
		
        TParm reportParm = new TParm();
        reportParm.setData("PACK_CODE_SEQ","TEXT", vParm.getData("BARCODE", 0));
        reportParm.setData("PACK_DESC","TEXT",tablePackM.getItemString(row, "PACK_DESC"));
        reportParm.setData("PACK_DEPT","TEXT","(" + result.getData("ORG_DESC",0) + ")");
        reportParm.setData("POTSEQ","TEXT",tablePackM.getItemString(row, "STERILIZATION_POTSEQ"));
        reportParm.setData("PROGRAM","TEXT",tablePackM.getItemString(row, "STERILLZATION_PROGRAM"));
        reportParm.setData("STERILIZATION_DATE","TEXT",sterilizationDate);
        reportParm.setData("VALUE_DATE","TEXT",dateString);
        reportParm.setData("OPT_USER","TEXT",result.getData("USER_NAME",0));
	

        reportParm.setData("PACK_DATE","TEXT",packageDate);
        reportParm.setData("PACK_CODE_SEQ_SEC","TEXT", vParm.getData("BARCODE", 0));
		// ���ô�ӡ����
//		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVNewSterilizationBarcode.jhw", reportParm);
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVGeneralPackageBarcode.jhw", reportParm);
	}
	
	
	
	/**
	 * ������ѡ�񷵻����ݴ���
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String pack_code = parm.getValue("PACK_CODE");
        if (!StringUtil.isNullString(pack_code))
        	this.getTextField("PACK_CODE").setValue(pack_code);
        String pack_desc = parm.getValue("PACK_DESC");
        if (!StringUtil.isNullString(pack_code))
        	this.getTextField("PACK_DESC").setValue(pack_desc);
        this.grabFocus("PACK_SEQ_NO");	//��λ����
	}
	
	
	
	/**
	 * ���յ����и�ѡ��ֵ�ı��¼�
	 */
	public void onCheckBoxClicked(Object obj){
		TTable table = (TTable) obj;
		int row = table.getSelectedRow();
		String tag = table.getItemString(row, "DELETE_FLG");
		
		if(tag.equals("Y")){
			table.setItem(row, "DELETE_FLG", "N");
		}else{
			table.setItem(row, "DELETE_FLG", "Y");
		}
		
	}
	
	/**
	 * ������table����һ���������¼
	 */
	private void createNewRepack(){
		TParm parm = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		
		parm.setData("DELETE_FLG",0, "N");
		parm.setData("REPACK_NO",0, repackNo);
		parm.setData("OPT_DATE",0,date);
		parm.setData("OPT_USER",0,Operator.getID());
		parm.setData("OPT_TERM",0,Operator.getIP());
		parm.setData("ORG_CODE",0,this.getValueString("ORG_CODE"));
		
		tableRepack.setParmValue(parm);
		tableRepack.setSelectedRow(0);
	}
	
	/** 
	 * ���ɴ������ 
	 *  */
	private String getRepackNo() {
		String recyleNo = SystemTool.getInstance().getNo("ALL", "INV",
				"INV_REPACK", "No");
		return recyleNo;
	}
	
	
	/**
	 *	��֤�������� 
	 */
	private boolean checkConditions(){
		if (this.getValueString("ORG_CODE").length() == 0) {
			messageBox("�����빩Ӧ�Ҳ��ţ�");
			return false;
		}
		if (this.getValueString("AUDIT_USER").length() == 0) {
			messageBox("�����������Ա��");
			return false;
		}
//		if (this.getValueString("PACK_CODE").length() == 0) {
//			messageBox("�������������ţ�");
//			return false;
//		}
//		if (this.getValueString("PACK_SEQ_NO").length() == 0) {
//			messageBox("��������������ţ�");
//			return false;
//		}
//		if (this.getValueString("STERILIZATION_POTSEQ").length() == 0) {
//			messageBox("��������Σ�");
//			return false;
//		}
//		if (this.getValueString("STERILIZATION_PROGRAM").length() == 0) {
//			messageBox("���������");
//			return false;
//		}
		return true;
	}
	/**
	 * ��շ���
	 */
	public void onClear() {
		// �������
		clearText();
		// �������
		clearTable(tableRepack);
		// ���������
		clearTable(tablePackM);
		// �����ϸ��
		clearTable(tablePackD);
		isImport = false;
		isNew = false;
	}
	/**
	 * ��տؼ�ֵ
	 */
	private void clearText() {
		this.clearValue("REPACK_NO;PACK_CODE;PACK_SEQ_NO;PACK_DESC;SCREAM;AUDITUSER");
	}
	/** ���TTextField���Ϳؼ� **/
	private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
	/** ���TRadioButton���Ϳؼ� **/
	private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }
	/**���table**/
	private void clearTable(TTable table) {
		table.removeRowAll();
	}
	/** ��ʼ�����ڿؼ����� **/
	private void onInitDate() {
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_STER_DATE", date);
		this.setValue("END_STER_DATE", date);
	}
}
