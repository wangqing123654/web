package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import jdo.inv.INVNewBackDisnfectionTool;
import jdo.inv.INVPackMTool;
import jdo.inv.INVPublicTool;
import jdo.inv.InvPackStockMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.manager.INVPackOberver;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:��ϴ,����,����
 * </p>
 * 
 * <p>
 * Description: ��ϴ,����,����
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
 * @author wangming 2013-6-19
 * @version 1.0
 */
public class INVNewBackDisnfectionControl extends TControl {
	
	private TTable table_packm;		//����������

	private TTable table_dis;		//���յ���ʾ��

	private TTable tableD;			//������ϸ��

	private String recycleNo;		//��ʼ�����յ���
	
	private boolean isNew = false;	//�Ƿ����½����յ����  	true����		false������

	private Map detailInfo = new HashMap();		//��ʱ�������������Ϣ
	
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		// ���ռ�¼table 
		table_dis = (TTable) getComponent("TABLE_DIS");
		// �����������
		table_packm = (TTable) getComponent("TABLE_PACKM");
		// ���������ϸ
		tableD = (TTable) getComponent("TABLED");
		// tableDֵ�ı��¼�
		this.addEventListener("TABLED->" + TTableEvent.CHANGE_VALUE,
				"onTableDChangeValue");
		//table_dis��checkboxֵ�����ı��¼�
		table_dis.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
		"onCheckBoxClicked");

		//��������������ѡ�񴰿�
		TParm parm = new TParm();
		((TTextField)getComponent("PACK_CODE")).setPopupMenuParameter("UD",
	            getConfigParm().newConfig(
	                "%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
	    // ������ܷ���ֵ����
		((TTextField)getComponent("PACK_CODE")).addEventListener(TPopupMenuEvent.
	            RETURN_VALUE, this, "popReturn");
		this.onInitDate();
		//����Ĭ�Ͽ���
		TTextFormat tf = (TTextFormat)getComponent("ORG_CODE");
        tf.setValue(Operator.getDept());
		
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
	 * �������յ�����
	 * */
	public void onNew(){
		
		if(isNew){
			messageBox("����ͬʱ�½��������յ���");
			return;
		}
		
		if (this.getValueString("ORG_CODE").length() == 0) {
			messageBox("��Ӧ�Ҳ��Ų���Ϊ�գ�");
			return;
		}
		
		isNew = true;				//���յ��½�״̬
		this.clearTable(table_dis);
		this.clearTable(table_packm);
		this.clearTable(tableD);
		
		recycleNo = this.getRecycleNo();
		this.createNewDisinfection();    //����һ�����յ���¼
		
	}
	
	/**
	 * ����յ�table����һ�����յ���¼
	 */
	private void createNewDisinfection(){
		TParm parm = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		
		parm.setData("DELETE_FLG",0, "N");
		parm.setData("RECYCLE_NO",0, recycleNo);
		parm.setData("OPT_DATE",0,date.toString().substring(0,19));
		parm.setData("OPT_USER",0,Operator.getID());
		parm.setData("OPT_TERM",0,Operator.getIP());
		parm.setData("ORG_CODE",0,this.getValueString("ORG_CODE"));
		
		
		table_dis.setParmValue(parm);
		table_dis.setSelectedRow(0);
	}
	
	/**
	 * ɨ������
	 */
	public void onScream() {
		
		int r = table_dis.getSelectedRow();
		if(r<0){
			messageBox("��ѡ����յ���");
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
			
			this.addPackage();	//����յ������������
			this.grabFocus("SCREAM");	//��λ����
		}
		((TTextField)getComponent("SCREAM")).setValue("");
		
	}
	
	/**
	 * ����յ����������������
	 */
	public void addPackage(){
		
		int r = table_dis.getSelectedRow();
		if(r<0){
			messageBox("��ѡ����յ���");
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
		
		if(result.getData("STATUS",0).equals("4")){
			messageBox("�������Ѵ���ά��״̬���޷����գ�");
			return;
		}
//		if(result.getData("STATUS",0).equals("3")){
//			messageBox("�������Ѵ������״̬���޷����գ�");
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

		TParm rowRecord = new TParm();
		rowRecord.setData("PACK_DESC",result.getData("PACK_DESC",0));
		rowRecord.setData("PACK_CODE",result.getData("PACK_CODE",0));
		rowRecord.setData("PACK_SEQ_NO",result.getData("PACK_SEQ_NO",0));
		rowRecord.setData("QTY",result.getData("QTY",0));
		rowRecord.setData("STATUS","����");
		rowRecord.setData("RECYCLE_DATE",date.toString().substring(0,19));
		rowRecord.setData("RECYCLE_USER",Operator.getID());
		rowRecord.setData("WASH_DATE",date.toString().substring(0,19));
		rowRecord.setData("WASH_USER",Operator.getID());
		rowRecord.setData("DISINFECTION_DATE",date.toString().substring(0,19));
		rowRecord.setData("DISINFECTION_VALID_DATE",dateString);
		rowRecord.setData("DISINFECTION_USER",Operator.getID());
		rowRecord.setData("DISINFECTION_POTSEQ",this.getValueString("DISINFECTION_POTSEQ"));
		rowRecord.setData("DISINFECTION_PROGRAM",this.getValueString("DISINFECTION_PROGRAM"));
		rowRecord.setData("BARCODE",result.getData("BARCODE",0));
		table_packm.addRow(rowRecord);
		
		this.grabFocus("PACK_SEQ_NO");	//��λ����
	}
	
	/**
	 * �������¼�
	 */
	public void onTableMClicked() {
		
		int row = table_packm.getSelectedRow();
		String packCode = table_packm.getShowParmValue().getValue("PACK_CODE", row);
		String packSeqNo = table_packm.getShowParmValue().getValue("PACK_SEQ_NO", row);
		
		TParm parm = new TParm();
		parm.setData("PACK_CODE", packCode);
		parm.setData("PACK_SEQ_NO", packSeqNo);
		
		TParm result = new TParm();
		result = INVNewBackDisnfectionTool.getInstance().queryPackageDetailInfo(parm);
		
		if(result.getCount()>0&&detailInfo.size()>0){
			for(int i=0;i<result.getCount();i++){
				String tPackCode = result.getRow(i).getValue("PACK_CODE");
				String tPackSeqNo = result.getRow(i).getValue("PACK_SEQ_NO");
				String tInvCode = result.getRow(i).getValue("INV_CODE");
				String tInvSeqNo = result.getRow(i).getValue("INVSEQ_NO");
				
				String key = tPackCode + "-" + tPackSeqNo + "-" + tInvCode + "-" + tInvSeqNo;
				
				Object obj = detailInfo.get(key);
				if(obj!=null){
					result.setDataN("RECOUNT_TIME", i, obj);
				}
			}
		}
		tableD.setParmValue(result);
	}
	
	/**
	 * TableDis�����¼�
	 */
	public void onTableDisClicked(){
		if(!isNew){
			int row = table_dis.getSelectedRow();
			String recycleNo = "";
			if(row>=0){
				recycleNo = table_dis.getItemData(row, "RECYCLE_NO").toString();
			}else{
				return;
			}
			
			TParm parm = new TParm();
			parm.setData("RECYCLE_NO", recycleNo);
			TParm result = INVNewBackDisnfectionTool.getInstance().queryDisnfectionByNo(parm); 
			
			if(result.getCount()>0){
				for(int i=0;i<result.getCount();i++){
					result.setData("STATUS", i, "����");
				}
			}
			table_packm.removeRowAll();
			table_packm.setParmValue(result);
			tableD.removeRowAll();
		}
	}
	
	
	/**
	 * ϸ��ֵ�ı��¼�
	 */
	public void onTableDChangeValue(Object obj) {

		// ֵ�ı�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null){
            return;
        }
        if (node.getValue().equals(node.getOldValue())){
        	return;
        }
		if(!isNew){	//��������½����յ����� 		StringTool.fillLeft(" dsfsfd ", 4, "0");

		}else{
			int row = tableD.getSelectedRow();
			
			String packCode = tableD.getParmValue().getValue("PACK_CODE", row);
			String packSeqNo = tableD.getParmValue().getValue("PACK_SEQ_NO", row);
			String invCode = tableD.getParmValue().getValue("INV_CODE",row);
			String invSeqNo = tableD.getParmValue().getValue("INVSEQ_NO",row);

			String recountTime = node.getValue().toString();
			String key = packCode + "-" + packSeqNo + "-" + invCode + "-" + invSeqNo;
			
			this.fillDetailMap(key, recountTime);
		}
		
	}
	
	//��¼�������
	private void fillDetailMap(String key,String value){
		
		if(detailInfo.size()>0){
			Object obj = "";
			obj = detailInfo.get(key);
			if(obj!=null){
				detailInfo.remove(key);
				detailInfo.put(key, value);
			}else{
				detailInfo.put(key, value);
			}
		}else{
			detailInfo.put(key, value);
		}
		
	}
	
	
	public void onSave(){
		if(isNew){
			
			TParm parm = new TParm();
			
			TParm disTable = table_dis.getShowParmValue();
			TParm packageMTable = table_packm.getShowParmValue();
			
			if(packageMTable.getCount() == 0){
				messageBox("���յ���ϸ���Ϊ�գ�");
				return;
			}
			
			disTable.setData("OPT_USER",0, Operator.getID());
			for(int i=0;i<packageMTable.getCount();i++){
				packageMTable.setData("RECYCLE_USER", i, Operator.getID());
				packageMTable.setData("WASH_USER", i, Operator.getID());
				packageMTable.setData("DISINFECTION_USER", i, Operator.getID());
			}
			
			parm.setData("DISNFECTIONTABLE", disTable.getData());
			parm.setData("PACKAGEMAINTABLE", packageMTable.getData());
			parm.setData("RECOUNTTIME",detailInfo);
			
			TParm result = TIOM_AppServer.executeAction("action.inv.INVNewBackDisnfectionAction",
		            "onInsert", parm);
			if (result.getErrCode() < 0) { 
	            err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	            messageBox("����ʧ�ܣ�");
	        }else{
	        	messageBox("����ɹ���");
	        }
			
			isNew = false;
			this.onClear();	//��ս��棬��Ǹ���ʼֵ
		}
	}
	
	public void onDelete(){
		
		if(isNew){	//�½�״̬ʱ����
			TParm disTable = table_dis.getShowParmValue();
			String tag = disTable.getData("DELETE_FLG", 0).toString();
			if(tag.equals("N")){//�ж��������ɾ�����ձ��Ƿ���ɾ����������Ϣ

				int row = table_packm.getSelectedRow();
				if(row>=0){
					table_packm.removeRow(row);
					clearTable(tableD);
					return;
				}
				
				messageBox("��ѡ����յ���");
				return;
			}else{//�����ɾ�����ձ�
				this.onClear();
			}
		}else{	//���½�״̬ʱ����
			
		}
	}
	
	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		
		if(isNew){
			int count = table_packm.getRowCount();
			if(count>0){
				if(this.messageBox("��ʾ��Ϣ", "�½����յ���Ϣ��δ���棬�Ƿ񱣴棿", this.YES_NO_OPTION)==0){
					//����
					this.onSave();
				}else{
					this.onClear();
				}
			}
		}

		String startDate = getValueString("START_RECYCLE_DATE");	//��ʼʱ��
		String endDate = getValueString("END_RECYCLE_DATE");		//��ֹʱ��

		if (startDate == null || startDate.length() == 0 || endDate == null || endDate.length() == 0) {
			messageBox("��������ֹʱ��!");
			return;
		}

		TParm parm = new TParm();			//��ѯ����
		if(!getValueString("SEL_RECYCLE_NO").equals("")){
			parm.setData("RECYCLE_NO", getValueString("SEL_RECYCLE_NO"));
		}
		parm.setData("START_DATE", startDate.substring(0, 10)+" 00:00:00");
		parm.setData("END_DATE", endDate.substring(0, 10)+" 23:59:59");
		
		TParm result = INVNewBackDisnfectionTool.getInstance().queryBackDisnfection(parm);
		
		if(!result.equals(null)){
			for(int i=0;i<result.getCount();i++){
				result.setData("DELETE_FLG", i, "N");
			}
		}
		
		table_dis.setParmValue(result);
	}
	
	/** 
	 * ���ɻ��յ��� 
	 *  */
	private String getRecycleNo() {
		String recyleNo = SystemTool.getInstance().getNo("ALL", "INV",
				"INV_DISINFECTION", "No");
		return recyleNo;
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
	 * ��շ���
	 */
	public void onClear() {
		
		// �������
		clearText();
		// �������
		clearTable(table_dis);
		// ���������
		clearTable(table_packm);
		// �����ϸ��
		clearTable(tableD);
		isNew = false;
		detailInfo = new HashMap();
	}
	/**
	 * ��տؼ�ֵ
	 */
	private void clearText() {
		this.clearValue("PACK_CODE;PACK_SEQ_NO;PACK_DESC;SCREAM;DISINFECTION_POTSEQ;DISINFECTION_PROGRAM");
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
		if (this.getValueString("DISINFECTION_POTSEQ").length() == 0) {
			messageBox("��������Σ�");
			return false;
		}
		if (this.getValueString("DISINFECTION_PROGRAM").length() == 0) {
			messageBox("���������");
			return false;
		}
		return true;
	}
	/**���table**/
	private void clearTable(TTable table) {
		table.removeRowAll();
	}
	/** ��ʼ�����ڿؼ����� **/
	private void onInitDate() {
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_RECYCLE_DATE", date);
		this.setValue("END_RECYCLE_DATE", date);
	}
	/** ���TTextField���Ϳؼ� **/
	private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
	/**
	 * ��ӡ����
	 */
	public void onBarcode() {
		TParm parm = new TParm();
		int row = table_packm.getSelectedRow();
		if (row < 0){
			return;
		}
		String packCode = table_packm.getItemString(row, "PACK_CODE");
		if (packCode == null || packCode.length() == 0) {
			messageBox("������ѡ�����!");
			return;
		}
		// ���������
		String strPackSeqNo = table_packm.getItemString(row, "PACK_SEQ_NO");
		String packCodeSeq = strPackSeqNo;
		for (int i = packCodeSeq.length(); i < 4; i++) {
			packCodeSeq = "0" + packCodeSeq;
		}
		packCodeSeq = "" + packCodeSeq;

		int recycleRow = table_dis.getSelectedRow();
		if(recycleRow<0){
			return;
		}
		String recycleNo = table_dis.getItemString(recycleRow, "RECYCLE_NO");
		TParm conditions = new TParm();
		conditions.setData("PACK_CODE", packCode);
		conditions.setData("PACK_SEQ_NO", Integer.parseInt(strPackSeqNo));
		conditions.setData("RECYCLE_NO", recycleNo);
		
		//��ѯ����������Ϣ
		TParm result = INVNewBackDisnfectionTool.getInstance().queryBarcodeInfo(conditions);
		
		// ��������    ʧЧ����	
		String disinfectionDate = StringTool.getString((Timestamp)result.getData("DISINFECTION_DATE",0),"yyyy/MM/dd");
		String disinfectionValidDate = StringTool.getString((Timestamp)result.getData("DISINFECTION_VALID_DATE",0),"yyyy/MM/dd");
		
		
		TParm reportParm = new TParm();
        reportParm.setData("PACK_CODE_SEQ","TEXT", packCode + packCodeSeq);
        reportParm.setData("PACK_DESC","TEXT",table_packm.getItemString(row, "PACK_DESC"));
        reportParm.setData("PACK_DEPT","TEXT","(" + result.getData("ORG_DESC",0) + ")");
        reportParm.setData("POTSEQ","TEXT",table_packm.getItemString(row, "DISINFECTION_POTSEQ"));
        reportParm.setData("PROGRAM","TEXT",table_packm.getItemString(row, "DISINFECTION_PROGRAM"));
        reportParm.setData("DISNFECTION_DATE","TEXT",disinfectionDate);
        reportParm.setData("VALUE_DATE","TEXT",disinfectionValidDate);
        reportParm.setData("OPT_USER","TEXT",result.getData("USER_NAME",0));

		// ���ô�ӡ����
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVNewDisinfectionBarcode.jhw", reportParm);
	}
	
	/**
	 * ��ӡ���յ�
	 */
	public void onPrint(){
		
		int row = table_dis.getSelectedRow();
		if (row < 0){
			messageBox("��ѡ����յ���");
			return;
		}
		String recycleNo = table_dis.getItemString(row, "RECYCLE_NO");
		String recycleOrgCode = table_dis.getItemString(row, "ORG_CODE");
		String recycleOptUser = table_dis.getItemString(row, "OPT_USER");
		
		TParm parm = new TParm();
		parm.setData("ORG_CODE", recycleOrgCode);
		
		//��ò�������
		TParm deptInfo = INVNewBackDisnfectionTool.getInstance().queryDeptName(parm);
		
		parm = new TParm();
		parm.setData("USER_ID", recycleOptUser);
		
		//����û�����
		TParm userInfo = INVNewBackDisnfectionTool.getInstance().queryUserName(parm);
		//��������
		String optDate = table_dis.getItemString(row, "OPT_DATE").substring(0, 10);
		
		TParm reportTParm = new TParm();
		reportTParm.setData("RECYCLENO", "TEXT", recycleNo);	//���յ���
		reportTParm.setData("RECYCLEDEPT", "TEXT", deptInfo.getData("ORG_DESC",0));	//���ղ�������
		reportTParm.setData("RECYCLEDATE", "TEXT", optDate);	//��������
		
		//�������
        TParm tableParm = new TParm();
		for(int i=0;i<table_packm.getRowCount();i++){
			tableParm.addData("PACK_DESC", table_packm.getItemString(i, "PACK_DESC"));
			
			String packCode = table_packm.getItemString(i, "PACK_CODE");
			String strPackSeqNo = table_packm.getItemString(i, "PACK_SEQ_NO");
			String packCodeSeq = strPackSeqNo;
			for (int m = packCodeSeq.length(); m < 4; m++) {
				packCodeSeq = "0" + packCodeSeq;
			}
			packCodeSeq = "" + packCodeSeq;
			
			
			tableParm.addData("PACKAGENNO", packCode+packCodeSeq);
			tableParm.addData("RECYCLE_DATE", table_packm.getItemString(i, "RECYCLE_DATE").substring(0,10));
			tableParm.addData("DISINFECTION_DATE", table_packm.getItemString(i, "DISINFECTION_DATE").substring(0, 10));
			tableParm.addData("DISINFECTION_VALID_DATE", table_packm.getItemString(i, "DISINFECTION_VALID_DATE").substring(0, 10));
			tableParm.addData("DISINFECTION_POTSEQ", table_packm.getItemString(i, "DISINFECTION_POTSEQ"));
			tableParm.addData("DISINFECTION_PROGRAM", table_packm.getItemString(i, "DISINFECTION_PROGRAM"));
			
		}
		
		
		tableParm.setCount(tableParm.getCount("PACK_DESC"));
		
	    tableParm.addData("SYSTEM", "COLUMNS", "PACK_DESC");
	    tableParm.addData("SYSTEM", "COLUMNS", "PACKAGENNO");
	    tableParm.addData("SYSTEM", "COLUMNS", "RECYCLE_DATE");
	    tableParm.addData("SYSTEM", "COLUMNS", "DISINFECTION_DATE");
	    tableParm.addData("SYSTEM", "COLUMNS", "DISINFECTION_VALID_DATE");
	    tableParm.addData("SYSTEM", "COLUMNS", "DISINFECTION_POTSEQ");
	    tableParm.addData("SYSTEM", "COLUMNS", "DISINFECTION_PROGRAM");

	    reportTParm.setData("TABLE", tableParm.getData());
		
	    Timestamp date = SystemTool.getInstance().getDate();
	        
	    reportTParm.setData("OPTUSER", "TEXT", Operator.getName());
	    reportTParm.setData("OPTDATE", "TEXT", date.toString().substring(0, 10));
		
		
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVDisinfection.jhw",
				reportTParm);
		                       
	}

}
