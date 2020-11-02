package com.javahis.ui.inv;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;

import jdo.inv.INVCodeMapTool;
import jdo.spc.SPCCodeMapHisTool;
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
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:��������Ӧ�����ʱ���ȶ� 
 * </p>
 * 
 * <p>   
 * Description:��������Ӧ�����ʱ���ȶ�
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
 * @author fux 2013.6.6
 * @version 1.0
 */
public class INVCodeMapControl extends TControl {
	                                 
	private TTable table;  //table
	private TTextField inv_code;//���ʴ���
	private TTextField inv_desc;//��������
	private TTextField inv_spe;//������ҩƷ���
	
	private TTextFormat sup_code;//��Ӧ�̴���
	private TTextField sup_inv_code;//��Ӧ��ҩƷ����
	
	 
	/**������   */   
	public INVCodeMapControl() {
		super();             	
	}  
	
    /**��ʼ������*/  
    public void onInit() {
    	table = (TTable) getComponent("TABLE_M");
    	
    	inv_code = (TTextField) getComponent("INV_CODE");//������ҩƷ����
    	inv_desc = (TTextField) getComponent("INV_CHN_DESC");;//������ҩƷ����
    	inv_spe = (TTextField) getComponent("DESCRIPTION");;//������ҩƷ���
    	
    	sup_code = (TTextFormat) getComponent("SUP_CODE");;//��Ӧ�̴���
    	sup_inv_code = (TTextField) getComponent("SUP_INV_CODE");;//��Ӧ��ҩƷ����
    	TParm parm = new TParm();
    	parm.setData("SUP_CODE", getValueString("SUP_CODE"));
    	getTextField("INV_CODE")
		.setPopupMenuParameter("UI", getConfigParm().newConfig(
        "%ROOT%\\config\\inv\\INVBasePopup.x"), parm);
    	// ������ܷ���ֵ���� 
    	getTextField("INV_CODE").addEventListener(
		TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
    					
    }
    
    
    /**��ѯ����*/
	public void onQuery() {
		TParm parm = new TParm();
		parm.setData("INV_CODE",inv_code.getValue());
		 
		parm.setData("SUP_CODE",sup_code.getValue());
		  
		TParm result = INVCodeMapTool.getInstance().query(parm);
		if(result.getCount()<=0){
			messageBox("��ѯ�޽����"); 
		}
		table.setParmValue(result);			
	} 
	
	
	/**
	 * �����޸Ĳ���
	 */
	public void onSave() {

		TParm parm = this.getParmForTag("INV_CODE;SUP_CODE;SUP_INV_CODE");
		if (!checkData(parm)) return;
		
		//���ϵͳ���Ƿ��и�ҩƷ
		boolean queryFlg = INVCodeMapTool.getInstance().queryBase(parm);
		if(!queryFlg){
			messageBox("ϵͳ��û�и����ʣ�");
			return;
		}

		// ����֮ǰ�Ȳ�ѯ,���ݿ����Ƿ��иü�¼
		TParm result = INVCodeMapTool.getInstance().updateQuery(parm);

		if (result.getCount("INV_CODE") > 0) {
			boolean flg = INVCodeMapTool.getInstance().update(parm);
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
			boolean flg = INVCodeMapTool.getInstance().save(parm);
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
  
		setValueForParm("INV_CODE;INV_CHN_DESC;DESCRIPTION;SUP_CODE;SUP_INV_CODE",
						table.getParmValue(), 
						rowno);
		inv_code.setEnabled(false);
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
						String invCode = st.getCell(0, i).getContents().trim();
						String supCode = st.getCell(1, i).getContents().trim();
						String supInvCode = st.getCell(2, i).getContents().trim();
						 
						parm.addData("INV_CODE", invCode);							
						parm.addData("SUP_CODE", supCode);
						parm.addData("SUP_INV_CODE", supInvCode);
						
						count++;
					}		    
				    
					parm.setCount(count);			
					if (count < 1) {  
						this.messageBox("��������ʧ��");			
						return;
					}
					TParm result = TIOM_AppServer.executeAction(
							"action.inv.INVCodeMapAction", "importMap",
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
		String values = "INV_CODE;INV_CHN_DESC;DESCRIPTION;SUP_CODE;SUP_INV_CODE";
		this.clearValue(values);
		inv_code.setEnabled(true);
		sup_code.setEnabled(true);
	}
	
	/**���������ѵ��¼*/  
	private boolean checkData(TParm parm) {
		//INV_CODE;SUP_CODE;SUP_INV_CODE
		if (null==parm.getValue("INV_CODE") || "".equals(parm.getValue("INV_CODE"))) {
			messageBox("���ʱ��벻��Ϊ�գ�");
			return false;
		}
		if (null==parm.getValue("SUP_CODE") || "".equals(parm.getValue("SUP_CODE").toString())) {
			messageBox("��Ӧ�̲���Ϊ�գ�");
			return false;
		}
		if (null==parm.getValue("SUP_INV_CODE") || "".equals(parm.getValue("SUP_INV_CODE"))) {
			messageBox("��Ӧ�����ʱ��벻��Ϊ�գ�");
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
		String inv_code = parm.getValue("INV_CODE");
		if (!StringUtil.isNullString(inv_code)) {
			getTextField("INV_CODE").setValue(inv_code);
		}  
		String inv_desc = parm.getValue("INV_CHN_DESC");
		if (!StringUtil.isNullString(inv_desc)) {
			getTextField("INV_CHN_DESC").setValue(inv_desc);
		}
		String SPECIFICATION = parm.getValue("DESCRIPTION");
		if (!StringUtil.isNullString(SPECIFICATION)) {                                    
			getTextField("DESCRIPTION").setValue(SPECIFICATION);			
		}
	}
	
	
}
