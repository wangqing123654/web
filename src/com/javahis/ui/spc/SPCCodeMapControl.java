package com.javahis.ui.spc;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JFileChooser;

import jdo.spc.SPCCodeMapTool;
import jdo.sys.Operator;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatSYSUnit;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:��������Ӧ��ҩƷ����ȶ�
 * </p>
 * 
 * <p>
 * Description:��������Ӧ��ҩƷ����ȶ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>                   
 * Company: bluecore
 * </p>  
 * 
 * @author liuzhen 2013.1.16
 * @version 1.0
 */
public class SPCCodeMapControl extends TControl {
	
	private TTable table;  //table
	private TTextField order_code;//������ҩƷ����
	private TTextField order_desc;//������ҩƷ����
	private TTextField order_spe;//������ҩƷ���
	
	private TTextFormat sup_code;//��Ӧ�̴���
	private TTextField sup_order_code;//��Ӧ��ҩƷ����
	
	
	/**������   */   
	public SPCCodeMapControl() {
		super();             	
	} 
	
    /**��ʼ������*/
    public void onInit() {
    	table = (TTable) getComponent("TABLE_M");
    	
    	order_code = (TTextField) getComponent("ORDER_CODE");//������ҩƷ����
    	order_desc = (TTextField) getComponent("ORDER_DESC");;//������ҩƷ����
    	order_spe = (TTextField) getComponent("SPECIFICATION");;//������ҩƷ���
    	
    	sup_code = (TTextFormat) getComponent("SUP_CODE");;//��Ӧ�̴���
    	sup_order_code = (TTextField) getComponent("SUP_ORDER_CODE");;//��Ӧ��ҩƷ����
    	TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
    	getTextField("ORDER_CODE")
		.setPopupMenuParameter(
				"UD",
				getConfigParm().newConfig(
						"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
    	// ������ܷ���ֵ����
    	getTextField("ORDER_CODE").addEventListener(
		TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
    					
    }
    
    
    /**��ѯ����*/
	public void onQuery() {
		TParm parm = new TParm();
		parm.setData("ORDER_CODE",order_code.getValue());
		
		parm.setData("SUP_CODE",sup_code.getValue());
		
		parm.setData("SUP_ORDER_CODE",sup_order_code.getValue());
		
		TParm result = SPCCodeMapTool.getInstance().query(parm);
		if(result.getCount()<=0){
			messageBox("��ѯ�޽����");
		}
		table.setParmValue(result);			
	} 
	
	
	/**
	 * �����޸Ĳ���
	 */
	public void onSave() {

		TParm parm = this.getParmForTag("ORDER_CODE;SUP_CODE;SUP_ORDER_CODE;SUPPLY_UNIT_CODE");
		if (!checkData(parm)) return;
		// <------ IND_CODE_MAP �����ֶ� OPT_USER,OPT_DATE,OPT_TERM identity by shendr 20131107
		parm.setData("OPT_USER",Operator.getID());
		parm.setData("OPT_TERM",Operator.getIP());
		double conversion_ratio = this.getValueDouble("CONVERSION_RATIO");
		parm.setData("CONVERSION_RATIO",conversion_ratio);
		// ------>
		//���ϵͳ���Ƿ��и�ҩƷ
		boolean queryFlg = SPCCodeMapTool.getInstance().queryBase(parm);
		if(!queryFlg){
			messageBox("ϵͳ��û�и�ҩƷ��");
			return;
		}

		// ����֮ǰ�Ȳ�ѯ,���ݿ����Ƿ��иü�¼
		TParm result = SPCCodeMapTool.getInstance().updateQuery(parm);

		if (result.getCount("ORDER_CODE") > 0) {
			boolean flg = SPCCodeMapTool.getInstance().update(parm);
			if (flg) {
				messageBox("����ɹ�");
				onClear();
				onQuery();
				return;
			} else {
				messageBox("����ʧ��");
				return;
			}
		} else {
			boolean flg = SPCCodeMapTool.getInstance().save(parm);
			if (flg) {
				messageBox("����ɹ�");
				onClear();
				onQuery();
				return;
			}
			messageBox("����ʧ��");
		}
	}
	
	
	/**ɾ������*/
	public void onDelete() {

		table.acceptText();
		int rowno = table.getSelectedRow();

		if (rowno < 0) {
			messageBox("��ѡ��Ҫɾ������Ϣ");
			return;
		}

		TParm parm = table.getParmValue().getRow(rowno);

		if (this.messageBox("��ʾ", "�Ƿ�ɾ���ü�¼", 2) == 0) {
			boolean flg = SPCCodeMapTool.getInstance().delete(parm);

			if (flg) {
				messageBox("ɾ���ɹ�");
				onClear();
				onQuery();
				return;
			}
			messageBox("ɾ��ʧ��");
		}

	}
	
	/** table �����¼� */
	public void onTableClick() {

		table.acceptText();
		int rowno = table.getSelectedRow();

		setValueForParm("ORDER_CODE;ORDER_DESC;SPECIFICATION;SUP_CODE;SUP_ORDER_CODE;SUPPLY_UNIT_CODE;CONVERSION_RATIO",
						table.getParmValue(), 
						rowno);
		TParm result = SPCCodeMapTool.getInstance().queryPhaUnit(this.getValueString("ORDER_CODE"));
		String unit_chn_desc = result.getValue("UNIT_CHN_DESC",0);
		this.setValue("UNIT_CHN_DESC", unit_chn_desc);
		order_code.setEnabled(false);
		if(null==sup_code.getValue() || "".equals((sup_code.getValue()).toString().trim())){
			sup_code.setEnabled(true);
		}else{
			sup_code.setEnabled(false);
		}
	}

	
	/** ����ҩƷ���ձ�*/
	public void onInsertPatByExl() {
		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showOpenDialog(null);
		
		TParm parm = new TParm();
		
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				Workbook wb = Workbook.getWorkbook(file);
				Sheet st = wb.getSheet(0);
				int row = st.getRows();
				int column = st.getColumns();		
				StringBuffer wrongMsg = new StringBuffer();
				String id = "";
				int count=0;
				for (int i = 1; i < row; i++) {
						String orderCode = st.getCell(0, i).getContents().trim();
						String supCode = st.getCell(1, i).getContents().trim();
						String supOrderCode = st.getCell(2, i).getContents().trim();
						
						parm.addData("ORDER_CODE", orderCode);							
						parm.addData("SUP_CODE", supCode);
						parm.addData("SUP_ORDER_CODE", supOrderCode);
						
						count++;
					}		    
				    
					parm.setCount(count);			
					if (count < 1) {  
						this.messageBox("��������ʧ��");			
						return;
					}
					
					TParm result = TIOM_AppServer.executeAction(
							"action.spc.SPCCodeMapAction", "importMap",
							parm);

					String flg = result.getValue("FLG");
					if ("Y".equals(flg)) {
						this.messageBox("���³ɹ���");
						onQuery();
						
					}else if("N".equals(flg)){
						this.messageBox("����ʧ�ܣ�");
						return;
					}else{
						this.messageBox(flg);
					}				
					
			} catch (BiffException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}         
	}
	
	/**��ղ���*/
	public void onClear() {
		String values = "ORDER_CODE;ORDER_DESC;SPECIFICATION;SUP_CODE;SUP_ORDER_CODE;SUPPLY_UNIT_CODE;CONVERSION_RATIO;UNIT_CHN_DESC";
		this.clearValue(values);
		order_code.setEnabled(true);
		sup_code.setEnabled(true);
	}
	
	/**���������ѵ��¼*/
	private boolean checkData(TParm parm) {
		//ORDER_CODE;SUP_CODE;SUP_ORDER_CODE
		if (null==parm.getValue("ORDER_CODE") || "".equals(parm.getValue("ORDER_CODE"))) {
			messageBox("������ҩƷ���벻��Ϊ�գ�");
			return false;
		}
		if (null==parm.getValue("SUP_CODE") || "".equals(parm.getValue("SUP_CODE").toString())) {
			messageBox("��Ӧ�̲���Ϊ�գ�");
			return false;
		}
		if (null==parm.getValue("SUP_ORDER_CODE") || "".equals(parm.getValue("SUP_ORDER_CODE"))) {
			messageBox("��Ӧ��ҩƷ���벻��Ϊ�գ�");
			return false;
		}
		return true;
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
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code)) {
			getTextField("ORDER_CODE").setValue(order_code);
		}
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc)) {
			getTextField("ORDER_DESC").setValue(order_desc);
		}
		String SPECIFICATION = parm.getValue("SPECIFICATION");
		if (!StringUtil.isNullString(SPECIFICATION)) {                                    
			getTextField("SPECIFICATION").setValue(SPECIFICATION);			
		}
		TParm result = SPCCodeMapTool.getInstance().queryPhaUnit(order_code);
		String unit_chn_desc = result.getValue("UNIT_CHN_DESC",0);
		if (!StringUtil.isNullString(unit_chn_desc)) {                                    
			((TextFormatSYSUnit)getComponent("UNIT_CHN_DESC")).setValue(unit_chn_desc);			
		}
	}
	
	
}
