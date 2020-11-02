package com.javahis.ui.spc;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import jdo.ind.Constant;
import jdo.ind.ElectronicTagsImpl;
import jdo.ind.ElectronicTagsInf;
import jdo.spc.SPCGenDrugPutDownTool;
import jdo.spc.SPCMaterialLocTool;
import jdo.sys.Operator;

import org.apache.http.message.BasicNameValuePair;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:ҩ����ҩ�¼�control
 * </p>
 * 
 * <p>
 * Description: ҩ����ҩ�¼�control
 * </p>
 * 
 * <p>
 * Copyright (c) BlueCore 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author Yuanxm 20121016
 * @version 1.0
 */
public class SPCGenDrugPutDownControl extends TControl {
	TTable table_N;
	TTable table_Y;
	
	TPanel N_PANEL;
	TPanel Y_PANEL;
	
	String boxEslId;// ��ת��
	TParm parm1 = new TParm();
	int k = 0;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.init();
		
		table_N = this.getTable("TABLE_N");
		table_Y = this.getTable("TABLE_Y");
		
		/**
		 TPanel tPanel = (TPanel)getComponent();
	 
		 TFrame tFrame = (TFrame)tPanel.getParentComponent().getParentComponent() ;
			
			
		final TTextField dispenseNo = this.getTextField("DISPENSE_NO");
		 dispenseNo.setFocusable(true);
		tFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				 dispenseNo.requestFocus(true);
			}
		});
		*/
	}
	
	public void onQuery(){
		String dispenseNo = (String)getValue("DISPENSE_NO");
		N_PANEL = (TPanel)getComponent("N_PANEL");
		Y_PANEL = (TPanel)getComponent("Y_PANEL");
		
		if(!StringUtil.isNullString(dispenseNo)){
			TParm parm = new TParm();
			parm.setData("DISPENSE_NO",dispenseNo);
			
			if(N_PANEL.isShowing()){
				parm.setData("IS_BOXED","N");
			}else if(Y_PANEL.isShowing()){
				parm.setData("IS_BOXED","Y");
			}
			
			TParm result = SPCGenDrugPutDownTool.getInstance().onQuery(parm);
			if(result.getCount() <  0 ){
				if(N_PANEL.isShowing()){
					table_N.setParmValue(new TParm());
				}
				if(Y_PANEL.isShowing()){
					table_Y.setParmValue(new TParm());
				}
				return ;
			}
			
			String orgChnDesc = (String)result.getData( "ORG_CHN_DESC",0);
			setValue("ORG_CHN_DESC", orgChnDesc);
			this.getTextField("ORG_CHN_DESC").setEditable(false);
			this.getTextField("DISPENSE_NO").setEditable(false);
			
			if(N_PANEL.isShowing()){
				table_N.setParmValue(result);
			}else if(Y_PANEL.isShowing()){
				table_Y.setParmValue(result);
			}
		}
		
	}
	
	
	public void onAgain(){
		this.clearValue("BOX_ESL_ID");
		this.getTextField("BOX_ESL_ID").grabFocus();
	}

	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}
	

	/**
	 * ���ⵥ�Ļس��¼�
	 * */
	public void onMonByCKD() {
				
		onQuery();
		this.getTextField("BOX_ESL_ID").grabFocus();
	}

	/**
	 * ��ת��Ļس��¼�
	 * */
	public void onMonByZZX() {
		boxEslId = this.getValueString("BOX_ESL_ID");
		
		table_N = this.getTable("TABLE_N");
		table_N.acceptText();

		N_PANEL = (TPanel)getComponent("N_PANEL");
		if(N_PANEL.isShowing()) {
			String boxEslId = getValueString("BOX_ESL_ID");
			String dispenseNo = getValueString("DISPENSE_NO");
			if(StringUtil.isNullString(boxEslId)){
				this.messageBox("��ת��Ϊ��!");
				return ;
			}
			
			TParm parm  = table_N.getParmValue() ;
			int count = parm.getCount() ;
			if(count <= 0 ){
				return ;
			}
			
			//�ж��Ƿ�ѡ����û��Ҫ�¼�װ���
			String orgChnDesc = (String)getValue("ORG_CHN_DESC");
			
			login();
			sendEleTag(boxEslId, orgChnDesc, dispenseNo+ "��ҩ", "", 0);
			
			for(int i = 0 ; i < count; i++ ){
				TParm rowParm = parm.getRow(i);
				
				if (rowParm.getBoolean("CHOOSE")) {
					rowParm.setData("BOX_ESL_ID",boxEslId);
					rowParm.setData("DISPENSE_NO",dispenseNo);
					rowParm.setData("BOXED_USER",Operator.getID());
					rowParm.setData("IS_BOXED","Y");
					SPCGenDrugPutDownTool.getInstance().updateINDDispensed(rowParm);
					TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(rowParm);
					String spec = outParm.getValue("SPECIFICATION");
					EleTagControl.getInstance().login();
					EleTagControl.getInstance().sendEleTag(rowParm.getValue("ELETAG_CODE"), rowParm.getValue("ORDER_DESC"), spec, outParm.getValue("QTY",0), 0);
					table_N.removeRow(i);
				}
			}
			 
		}
		this.getTextField("ELETAG_CODE").grabFocus();
	}

	/**
	 * ���ӱ�ǩ�Ļس��¼�
	 * */
	public void onMonByDianzbq() {
		Y_PANEL = (TPanel)getComponent("Y_PANEL");
		if(!Y_PANEL.isShowing()){
			String eletagCode=this.getValueString("ELETAG_CODE");
			boxEslId = this.getValueString("BOX_ESL_ID");
			String dispenseNo = (String)getValue("DISPENSE_NO");
			
			if(StringUtil.isNullString(eletagCode) || StringUtil.isNullString(boxEslId)){
				return;
			}
			table_N.acceptText();
			
			String labelNo = "";
			String productName = "";
			String orgCode = "";
			String orderCode = "";
			String spec = "";
			TParm parm = table_N.getParmValue();
			int count = table_N.getParmValue().getCount();
	
			boolean b = false;
			for (int i = 0; i < count; i++) {
				TParm rowParm = parm.getRow(i);
				String eleCode = rowParm.getValue("ELETAG_CODE");
				if(eletagCode.equals(eleCode)){
					rowParm.setData("BOX_ESL_ID",boxEslId);
					rowParm.setData("DISPENSE_NO",dispenseNo);
					rowParm.setData("IS_BOXED","Y");
					labelNo = eletagCode;
					productName = rowParm.getValue("ORDER_DESC");
					orgCode = rowParm.getValue("ORG_CODE");
					orderCode= rowParm.getValue("ORDER_CODE");
					rowParm.setData("BOXED_USER",Operator.getID());
					SPCGenDrugPutDownTool.getInstance().updateINDDispensed(rowParm);
					table_N.removeRow(i);
					b = true;
					break;
				}
			}
			
			//�ǵģ����õ��ӱ�ǩ�ӿ���ʾ�������˸
			if(b){
				TParm inParm = new TParm();
				inParm.setData("ORDER_CODE",orderCode);
				inParm.setData("ORG_CODE",orgCode);
				inParm.setData("ELETAG_CODE",eletagCode);
				TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(inParm);
				spec = outParm.getValue("SPECIFICATION");
				EleTagControl.getInstance().login();
				EleTagControl.getInstance().sendEleTag(labelNo, productName, spec, outParm.getValue("QTY",0), 0);
				 
			}
			this.setValue("ELETAG_CODE", "");
			return;
		}
	}

	public void onChange() {
		 onQuery();
	}
	
	public void onSave(){
		
		table_N = this.getTable("TABLE_N");
		table_N.acceptText();

		N_PANEL = (TPanel)getComponent("N_PANEL");
		if(N_PANEL.isShowing()) {
			String boxEslId = getValueString("BOX_ESL_ID");
			String dispenseNo = getValueString("DISPENSE_NO");
			if(StringUtil.isNullString(boxEslId)){
				this.messageBox("��ת��Ϊ��!");
				return ;
			}
			
			TParm parm  = table_N.getParmValue() ;
			int count = parm.getCount() ;
			if(count <= 0 ){
				return ;
			}
			
			for(int i = 0 ; i < count; i++ ){
				TParm rowParm = parm.getRow(i);
				
				if (rowParm.getBoolean("CHOOSE")) {
					rowParm.setData("BOX_ESL_ID",boxEslId);
					rowParm.setData("DISPENSE_NO",dispenseNo);
					rowParm.setData("BOXED_USER",Operator.getID());
					rowParm.setData("IS_BOXED","Y");
					SPCGenDrugPutDownTool.getInstance().updateINDDispensed(rowParm);
					 
					TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(rowParm);
					String spec = outParm.getValue("SPECIFICATION");
					EleTagControl.getInstance().login();
					EleTagControl.getInstance().sendEleTag(rowParm.getValue("ELETAG_CODE"), rowParm.getValue("ORDER_DESC"), spec, outParm.getValue("QTY",0), 0);
					table_N.removeRow(i);
				}
			}
			//onQuery();
		}
		
		
	}

	/**
	 * ��ղ���
	 * */
	public void onClear() {
		String controlName = "DISPENSE_NO;ORG_CHN_DESC;BOX_ESL_ID;ELETAG_CODE";
		this.clearValue(controlName);
	 
		this.getTextField("ORG_CHN_DESC").setEditable(true);
		this.getTextField("DISPENSE_NO").setEditable(true); 
		this.getTextField("DISPENSE_NO").grabFocus();
		table_N.removeRowAll();
		table_Y.removeRowAll();
	}
	
	public static void login(){
		ElectronicTagsInf eti = new ElectronicTagsImpl() ;
		Map<String, Object> map = eti.login("admin", "123");
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it
					.next();
			Constant.parameters.clear();
		}
		 
		Constant.parameters.add(0,new BasicNameValuePair("Token", (String)map.get("Token")));
		Constant.parameters.add(1,new BasicNameValuePair("UserId", (String)map.get("UserId")));
		Constant.parameters.add(2,new BasicNameValuePair("RoleId", (String)map.get("RoleId")));
	}
	
    /**
     * ���µ��ӱ�ǩ
     * @param bastkId  ���ӱ�ǩID
     * @param nameInfo 
     * @param spec
     * @param num
     * @param lightNum TODO
     * @return
     * @author liyh
     * @date 20120919
     */
	  public boolean sendEleTag(String bastkId,String nameInfo,String spec ,String num, int lightNum){
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		UUID uuid = UUID.randomUUID();
		if(null == uuid )
			uuid = UUID.randomUUID();
		
		m.put("ObjectId", uuid.toString());
		m.put("ObjectType", 3);
		m.put("ObjectName", "medBasket");
		// m.put("LabelNo", "01048A");
		// ���ӱ�ǩid
		m.put("LabelNo", bastkId);
		// ��վid
		m.put("StationID", "2");
		// ��һ�� ��ʾ�û���������
		m.put("ProductName", nameInfo);
		// �ڶ��� ��ʾ������ ���Ա�
		m.put("Spec", spec + " " + num);
		// ������ ���ӱ�ǩ��ά���뺬��
		 
		m.put("ShelfNo", bastkId);
	 
		// ��˸����
		m.put("Light", lightNum);
		// �Ƿ����ƣ�true:��
		m.put("Enabled", true);
		
		Iterator it = m.entrySet().iterator();
		System.out.println("------------��ҩ--���ӱ�ǩ��������---------start----------");
		ElectronicTagsInf eti = new ElectronicTagsImpl();
		// ���õ��ӱ�ǩ�ӿ�
		Map<String, Object> map = eti.cargoUpdate(m);
		
		
		if(null != map){
			it = map.entrySet().iterator();
			 
	    	String status = (String) map.get("Status");
	    	if(null != status && "10000".equals(status)){//���µ��ӱ�ǩ״̬�ɹ�
	    		return true;
	    	}else{
	    		return false;
	    	}			
		}else{
			return false;
		}
		
		
    }
}
