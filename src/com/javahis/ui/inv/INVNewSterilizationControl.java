package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.inv.INVNewBackDisnfectionTool;
import jdo.inv.INVNewRepackTool;
import jdo.inv.INVNewSterilizationTool;
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
 * @author wangming 2013-7-1
 * @version 1.0
 */
public class INVNewSterilizationControl extends TControl{

	private TTable tableSter;			//�������
	
	private TTable tablePackM;			//����������
	
	private TTable tablePackD;			//��������ϸ
	
	private String sterilizationNo;		//��ʼ���������
	
	private boolean isImport = false;		//�Ƿ��ǵ����������  	true����		false������
	
	private boolean isNew = false;			//�Ƿ����½������	true����		false������
	
	private String repackNo;			//���صĴ������
	
	
	/**
	 * ��ʼ��
	 */
	public void onInit() {
	
		tableSter = (TTable) getComponent("TABLE_STER");
		tablePackM = (TTable) getComponent("TABLE_PACKM");
		tablePackD = (TTable) getComponent("TABLED");
		
		this.onInitDate();
		
		tableSter.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
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
		
		int r = tableSter.getSelectedRow();
		if(r<0){
			messageBox("��ѡ���������");
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
			
			this.addPackage();	//������������������
			this.grabFocus("SCREAM");	//��λ����
		}
		((TTextField)getComponent("SCREAM")).setValue("");
		
	}
	
	/**
	 * ����������������������
	 */
	public void addPackage(){
		
		int r = tableSter.getSelectedRow();
		if(r<0){
			messageBox("��ѡ���������");
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
			messageBox("�������Ѵ���ά��״̬���޷������");
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
			if(this.messageBox("��ʾ��Ϣ", "�����������ڿ�״̬���Ƿ��ٴ�������", this.YES_NO_OPTION)==1){
				return;
			}
		}
		
		Timestamp date = SystemTool.getInstance().getDate();
		
		int valid = Integer.parseInt(result.getData("VALUE_DATE",0).toString()); 	//��Ч����   һ���ǡ��족��λ  
		
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
        cal.add(cal.DATE,valid);//��������������N��
        Date d=cal.getTime(); 
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateString = formatter.format(d);

        
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
		tp.setData("AUDIT_DATE", row, date.toString().substring(0,19));
		tp.setData("AUDIT_USER", row, this.getValueString("AUDIT_USER"));
		tp.setData("STERILIZATION_POTSEQ", row, this.getValueString("STERILIZATION_POTSEQ"));
		tp.setData("STERILLZATION_PROGRAM", row, this.getValueString("STERILIZATION_PROGRAM"));
		tp.setData("STERILLZATION_DATE", row, date.toString().substring(0,19));
		tp.setData("STERILLZATION_USER", row, Operator.getID());
		tablePackM.setParmValue(tp);
		
//		TParm rowRecord = new TParm();
//		rowRecord.setData("PACK_DESC",result.getData("PACK_DESC",0));
//		rowRecord.setData("PACK_CODE",result.getData("PACK_CODE",0));
//		rowRecord.setData("PACK_SEQ_NO",result.getData("PACK_SEQ_NO",0));
//		rowRecord.setData("QTY",result.getData("QTY",0));
//		rowRecord.setData("STATUS","���");
//		rowRecord.setData("STERILLZATION_DATE",date.toString().substring(0,19));
//		rowRecord.setData("STERILLZATION_USER",Operator.getID());
////		rowRecord.setData("PACK_DATE",date.toString().substring(0,19));
////		rowRecord.setData("PACK_USER",Operator.getID());
//		rowRecord.setData("AUDIT_DATE",date.toString().substring(0,19));
//		rowRecord.setData("AUDIT_USER",this.getValueString("AUDIT_USER"));
//		rowRecord.setData("STERILIZATION_POTSEQ",this.getValueString("STERILIZATION_POTSEQ"));
//		rowRecord.setData("STERILLZATION_PROGRAM",this.getValueString("STERILIZATION_PROGRAM"));
//		rowRecord.setData("BARCODE", result.getData("BARCODE",0));
		
		this.grabFocus("PACK_SEQ_NO");	//��λ����
	}
	
	/**
	 * �������յ�����
	 * */
	public void onNew(){
		
		if(isImport||isNew){
			messageBox("����ͬʱ�½������������");
			return;
		}

		if (this.getValueString("ORG_CODE").length() == 0) {
			messageBox("��Ӧ�Ҳ��Ų���Ϊ�գ�");
			return;
		}
		if (this.getValueString("AUDIT_USER").length() == 0) {
			messageBox("�����Ա����Ϊ�գ�");
			return;
		}
		
		isNew = true;				//������½�״̬
		this.clearTable(tableSter);
		this.clearTable(tablePackM);
		this.clearTable(tablePackD);
		
		sterilizationNo = this.getSterilizationNo();
		this.createNewSterilization();    //����һ�����յ���¼
		
	}
	
	public void onSave(){
		
		if(isImport){	//������յ�
			boolean tag = true;	//tagΪtrueʱ˵����������������ɹ�
			
			TParm sterTable = tableSter.getShowParmValue();
			TParm packageMTable = tablePackM.getParmValue();
			
			for(int i=0;i<packageMTable.getCount();i++){
				String potseq = packageMTable.getData("STERILIZATION_POTSEQ", i).toString();
				String program = packageMTable.getData("STERILLZATION_PROGRAM", i).toString();
				if(potseq == null || program == null || potseq.length() == 0 || program.length() == 0){
					messageBox("���κͳ�����Ϣ���벻������");
					return;
				}
			}
			
			sterTable.setData("OPT_USER",0, Operator.getID());
			for(int i=0;i<packageMTable.getCount();i++){
				packageMTable.setData("STERILLZATION_USER", i, Operator.getID());
				packageMTable.setData("PACK_USER", i, Operator.getID());
			}
			TParm parm = new TParm();
			for(int i=0;i<packageMTable.getCount();i++){
				parm = new TParm();
				TParm temp = new TParm();
				temp.addData("REPACK_NO", repackNo);
				
				parm.setData("STERILIZATIONTABLE", sterTable.getData());
				parm.setData("PACKAGEMAINTABLE", packageMTable.getRow(i).getData());
				parm.setData("REPACK_NO", temp.getData());
				
				TParm result = TIOM_AppServer.executeAction("action.inv.INVNewSterilizationAction",
			            "onInsert", parm);
				if (result.getErrCode() < 0) { 
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					tag = false;
					messageBox("����ʧ�ܣ�");
				}
//				if(result.getData("ENOUGH").equals("NO")){
//					tag = false;
//				}
			}
			if(tag){
				messageBox("����ɹ���");
			}else{
				messageBox("����ʧ�ܣ�");
			}
//��ʱ			if(!tag){
//				messageBox("�в���������δ����ɹ�����ȷ�ϣ�");
//			}else{
//			messageBox("����ɹ���");
//			}
			isImport = false;
			repackNo = "";
			this.onClear();	//��ս��棬��Ǹ���ʼֵ
		}
		if(isNew){
			boolean tag = true;	//tagΪtrueʱ˵����������������ɹ�
			
			TParm sterTable = tableSter.getShowParmValue();
			TParm packageMTable = tablePackM.getParmValue();
			
			if(packageMTable.getCount("BARCODE") == 0){
				messageBox("�������ϸ���Ϊ�գ�");
				return;
			}
			
			sterTable.setData("OPT_USER",0, Operator.getID());
			for(int i=0;i<packageMTable.getCount();i++){
				packageMTable.setData("STERILLZATION_USER", i, Operator.getID());
				packageMTable.setData("PACK_USER", i, Operator.getID());
			}
			TParm parm = new TParm();
			for(int i=0;i<packageMTable.getCount("BARCODE");i++){
				parm = new TParm();
				TParm temp = new TParm();
				temp.addData("REPACK_NO", repackNo);
				
				parm.setData("STERILIZATIONTABLE", sterTable.getData());
				parm.setData("PACKAGEMAINTABLE", packageMTable.getRow(i).getData());
				parm.setData("REPACK_NO", temp.getData());
				
				TParm result = TIOM_AppServer.executeAction("action.inv.INVNewSterilizationAction",
			            "onInsert", parm);
				if (result.getErrCode() < 0) { 
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					tag = false;
					messageBox("����ʧ�ܣ�");
				}
//				if(result.getData("ENOUGH").equals("NO")){
//					tag = false;
//				}
			}
			if(tag){
				messageBox("����ɹ���");
			}else{
				messageBox("����ʧ�ܣ�");
			}
//��ʱ			if(!tag){
//				messageBox("�в���������δ����ɹ�����ȷ�ϣ�");
//			}else{
//				messageBox("����ɹ���");
//			}
			isNew = false;
			repackNo = "";
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
			messageBox("�����Ա����Ϊ�գ�");
			return;
		}
		Object obj = openDialog("%ROOT%\\config\\inv\\INVChooseRepackBill.x");
		TParm parm = (TParm)obj;
		
		if(parm.getErrCode() < 0){
			this.messageBox("�޴������ݣ�");
			return;
		}
		
		repackNo = parm.getData("REPACK_NO").toString();
		
		TParm result = INVNewSterilizationTool.getInstance().queryRepackByRepackNo(parm);
		
		if(result.getErrCode() < 0){
			this.messageBox("�޲�ѯ���ݣ�");
			return;
		}
		
		Timestamp date = SystemTool.getInstance().getDate();
		
		
		this.clearTable(tableSter);
		this.clearTable(tablePackM);
		this.clearTable(tablePackD);
		sterilizationNo = this.getSterilizationNo();
		this.createNewSterilization();
		
		
		for(int i=0;i<result.getCount();i++){
			result.setData("STERILIZATION_POTSEQ", i, "");
			result.setData("STERILLZATION_PROGRAM", i, "");
			result.setData("STATUS", i, "���");
			result.setData("STERILLZATION_DATE", i, date.toString().substring(0,19));
			result.setData("STERILLZATION_USER", i, Operator.getID());
//			result.setData("PACK_DATE", i, date.toString().substring(0,19));
//			result.setData("PACK_USER", i, Operator.getID());
			result.setData("AUDIT_DATE", i, date.toString().substring(0,19));
			result.setData("AUDIT_USER", i, this.getValueString("AUDIT_USER"));
			result.setData("OPT_DATE",i,date.toString().substring(0,19));
			result.setData("OPT_USER",i,Operator.getID());
			result.setData("OPT_TERM",i,Operator.getIP());
			
			result.setData("ORG_CODE",i,this.getValueString("ORG_CODE"));
//			result.setData("STA", i, "2");		//���յ�״̬Ϊ2
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
	
	public void onTableSterClicked(){
		
		if(!isImport&&!isNew){
			int row = tableSter.getSelectedRow();
			String steriliaztionNo = "";
			if(row>=0){
				steriliaztionNo = tableSter.getItemData(row, "STERILIZATION_NO").toString();
			}else{
				return;
			}
			
			TParm parm = new TParm();
			parm.setData("STERILIZATION_NO", steriliaztionNo);
			
			if(this.getRadioButton("radioYes").isSelected()){
				parm.setData("FINISH_FLG", "Y");
			}else if(this.getRadioButton("radioNo").isSelected()){
				parm.setData("FINISH_FLG", "N");
			}
			
			TParm result = INVNewSterilizationTool.getInstance().querySterilizationByNo(parm);
			
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
		if(!getValueString("STERILIZATION_NO").equals("")){
			parm.setData("STERILIZATION_NO", getValueString("STERILIZATION_NO"));
		}
		parm.setData("START_DATE", startDate.substring(0, 10)+" 00:00:00");
		parm.setData("END_DATE", endDate.substring(0, 10)+" 23:59:59");
		
		TParm result = INVNewSterilizationTool.getInstance().querySterilization(parm);
		
		if(!result.equals(null)){
			for(int i=0;i<result.getCount();i++){
				result.setData("DELETE_FLG", i, "N");
			}
		}
		
		tableSter.setParmValue(result);
		
	}
	
	
	public void onDelete(){
		
		if(isImport){	//������յ�״̬ʱ����
			TParm disTable = tableSter.getShowParmValue();
			String tag = disTable.getData("DELETE_FLG", 0).toString();
			if(tag.equals("N")){//�ж��������ɾ�����ձ��Ƿ���ɾ����������Ϣ
				
				messageBox("��ѡ���������");
				return;
			}else{//�����ɾ�����ձ�
				this.onClear();
			}
		}else if(isNew){	//��������յ�״̬ʱ����
			TParm disTable = tableSter.getShowParmValue();
			String tag = disTable.getData("DELETE_FLG", 0).toString();
			if(tag.equals("N")){//�ж��������ɾ��������Ƿ���ɾ����������Ϣ

				int row = tablePackM.getSelectedRow();
				if(row>=0){
					tablePackM.removeRow(row);
					clearTable(tablePackD);
					return;
				}
				
				messageBox("��ѡ���������");
				return;
			}else{//�����ɾ�������
				this.onClear();
			}
		}else{
			TParm disTable = tableSter.getShowParmValue();
			String tag = disTable.getData("DELETE_FLG", 0).toString();
			if(tag.equals("Y")){
				messageBox("����ɵ����������ɾ����");
				return;
			}
			int row = tablePackM.getSelectedRow();
			if(row>=0){
				if(tablePackM.getParmValue().getRow(row).getData("DELETE_FLG").equals("Y")){
					messageBox("����ɵ��������ϸ����ɾ����");
					return;
				}
			}
		}
	}
	/**
	 * ��ӡ�����
	 */
	public void onPrint(){
		
		int row = tableSter.getSelectedRow();
		if (row < 0){
			messageBox("��ѡ���������");
			return;
		}
		String SterilizationNo = tableSter.getItemString(row, "STERILIZATION_NO");
		String SterilizationOrgCode = tableSter.getItemString(row, "ORG_CODE");
		String SterilizationOptUser = tableSter.getItemString(row, "OPT_USER");
		
		TParm parm = new TParm();
		parm.setData("ORG_CODE", SterilizationOrgCode);
		
		//��ò�������
		TParm deptInfo = INVNewBackDisnfectionTool.getInstance().queryDeptName(parm);
		
		parm = new TParm();
		parm.setData("USER_ID", SterilizationOptUser);
		
		//����û�����
		TParm userInfo = INVNewBackDisnfectionTool.getInstance().queryUserName(parm);
		//��������
		String optDate = tableSter.getItemString(row, "OPT_DATE").substring(0, 10);
		
		TParm reportTParm = new TParm();
		reportTParm.setData("STERILIZATIONNO", "TEXT", SterilizationNo);	//�������
		reportTParm.setData("STERILIZATIONDEPT", "TEXT", deptInfo.getData("ORG_DESC",0));	//�����������
		reportTParm.setData("STERILIZATIONDATE", "TEXT", optDate);	//��������
		
		//�������
        TParm tableParm = new TParm();
		for(int i=0;i<tablePackM.getRowCount();i++){
			tableParm.addData("BARCODE", tablePackM.getItemString(i, "BARCODE"));
			tableParm.addData("PACK_DESC", tablePackM.getItemString(i, "PACK_DESC"));
			
//			String packCode = tablePackM.getItemString(i, "PACK_CODE");
//			String strPackSeqNo = tablePackM.getItemString(i, "PACK_SEQ_NO");
//			String packCodeSeq = strPackSeqNo;
//			for (int m = packCodeSeq.length(); m < 4; m++) {
//				packCodeSeq = "0" + packCodeSeq;
//			}
//			packCodeSeq = "" + packCodeSeq;
			
			
			tableParm.addData("PACKAGENNO",  tablePackM.getItemString(i, "PACK_CODE"));
			tableParm.addData("STERILLZATION_DATE", tablePackM.getItemString(i, "STERILLZATION_DATE").substring(0, 10));
			tableParm.addData("STERILIZATION_POTSEQ", tablePackM.getItemString(i, "STERILIZATION_POTSEQ"));
			tableParm.addData("STERILLZATION_PROGRAM", tablePackM.getItemString(i, "STERILLZATION_PROGRAM"));
			
		}
		
		tableParm.setCount(tableParm.getCount("PACK_DESC"));
		
		tableParm.addData("SYSTEM", "COLUMNS", "BARCODE");
	    tableParm.addData("SYSTEM", "COLUMNS", "PACK_DESC");
	    tableParm.addData("SYSTEM", "COLUMNS", "PACKAGENNO");
	    tableParm.addData("SYSTEM", "COLUMNS", "STERILLZATION_DATE");

	    tableParm.addData("SYSTEM", "COLUMNS", "STERILIZATION_POTSEQ");
	    tableParm.addData("SYSTEM", "COLUMNS", "STERILLZATION_PROGRAM");

	    reportTParm.setData("TABLE", tableParm.getData());
		
	    Timestamp date = SystemTool.getInstance().getDate();
	    
	    reportTParm.setData("OPTUSER", "TEXT", Operator.getName());
	    reportTParm.setData("OPTDATE", "TEXT", date.toString().substring(0, 10));
		
		
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVSterilization.jhw",
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
		
		int sterRow = tableSter.getSelectedRow();
		if(sterRow<0){
			return;
		}
		
		String sterilizationNo = tableSter.getItemString(sterRow, "STERILIZATION_NO");
		TParm conditions = new TParm();
		conditions.setData("PACK_CODE", packCode);
		conditions.setData("PACK_SEQ_NO", Integer.parseInt(strPackSeqNo));
		conditions.setData("STERILIZATION_NO", sterilizationNo);
		
		//��ѯ����������Ϣ
		TParm result = INVNewSterilizationTool.getInstance().queryBarcodeInfo(conditions);
//		TParm result = INVNewRepackTool.getInstance().queryBarcodeInfo(conditions.getRow(0));
		
		// �������    �������	
		String sterilizationDate = StringTool.getString((Timestamp)result.getData("STERILLZATION_DATE",0),"yyyy/MM/dd");
		String packageDate = StringTool.getString((Timestamp)result.getData("STERILLZATION_DATE",0),"yyyy/MM/dd");
		
		
		TParm vParm = InvPackStockMTool.getInstance().getPackDate(packCode,Integer.parseInt(strPackSeqNo));
		int valid = Integer.parseInt(vParm.getData("VALUE_DATE",0).toString());
        Calendar cal = new GregorianCalendar();
        cal.set(Integer.parseInt(packageDate.substring(0, 4)), Integer.parseInt(packageDate.substring(5, 7))-1, Integer.parseInt(packageDate.substring(8)));
        cal.add(cal.DATE,valid);//��������������N��
        Date d=cal.getTime(); 
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(d);
		
		
        TParm reportParm = new TParm();
        reportParm.setData("PACK_CODE_SEQ","TEXT", packCode + packCodeSeq);
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
	 * �������table����һ���������¼
	 */
	private void createNewSterilization(){
		TParm parm = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		
		parm.setData("DELETE_FLG",0, "N");
		parm.setData("STERILIZATION_NO",0, sterilizationNo);
		parm.setData("OPT_DATE",0,date.toString().substring(0,19));
		parm.setData("OPT_USER",0,Operator.getID());
		parm.setData("OPT_TERM",0,Operator.getIP());
		parm.setData("ORG_CODE",0,this.getValueString("ORG_CODE"));
		
		tableSter.setParmValue(parm);
		tableSter.setSelectedRow(0);
	}
	
	/** 
	 * ����������� 
	 *  */
	private String getSterilizationNo() {
		String recyleNo = SystemTool.getInstance().getNo("ALL", "INV",
				"INV_STERILIZATION", "No");
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
//		if (this.getValueString("PACK_CODE").length() == 0) {
//			messageBox("�������������ţ�");
//			return false;
//		}
//		if (this.getValueString("PACK_SEQ_NO").length() == 0) {
//			messageBox("��������������ţ�");
//			return false;
//		}
		if (this.getValueString("STERILIZATION_POTSEQ").length() == 0) {
			messageBox("��������Σ�");
			return false;
		}
		if (this.getValueString("STERILIZATION_PROGRAM").length() == 0) {
			messageBox("���������");
			return false;
		}
		if (this.getValueString("AUDIT_USER").length() == 0) {
			messageBox("�����������Ա��");
			return false;
		}
		return true;
	}
	/**
	 * ��շ���
	 */
	public void onClear() {
		// �������
		clearText();
		// �������
		clearTable(tableSter);
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
		this.clearValue("STERILIZATION_NO;PACK_CODE;PACK_SEQ_NO;PACK_DESC;SCREAM;STERILIZATION_POTSEQ;STERILIZATION_PROGRAM;AUDIT_USER");
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
