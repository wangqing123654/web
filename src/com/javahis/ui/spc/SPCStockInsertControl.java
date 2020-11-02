package com.javahis.ui.spc;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import jdo.spc.SPCSysFeeTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;
                                           
/**
 * <p>
 * Title:ҩƷ����ȶ�
 * </p>
 * 
 * <p>
 * Description:ҩƷ����ȶ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author fuwj 2012.12.20
 * @version 1.0
 */
public class SPCStockInsertControl extends TControl {

	private TTable talbe;

	private String action = "save";

	/**
	 * ������
	 */
	public SPCStockInsertControl() {
		super();
		// this.messageBox("2222");
		/*
		 * talbe = this.getTable("TABLE_M"); talbe.addEventListener("TABLE_M->"
		 * + TTableEvent.CHANGE_VALUE, this, "onOrderValueChange");
		 */
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ��ʼ��������
		initPage();
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		// ���õ����˵�
		callFunction("UI|DELETE|setEnabled", false);
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
	 * ����ҩƷ���ձ�
	 */
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
				int count = 0;
				for (int i = 1; i < row; i++) {
//////////////////////////////////////////////////////////////////////////////////////////////
					String orderCode = st.getCell(5, i).getContents().trim();
					String lwdm = st.getCell(2, i).getContents().trim();
					String lwmc =  st.getCell(2, i).getContents().trim();
					String dzbq = st.getCell(4, i).getContents().trim();					
					parm.addData("ORDER_CODE", orderCode);				
					parm.addData("TAG_CODE", lwdm);						
				//	parm.addData("MATERIAL_LOC_DESC", lwmc);      
					parm.addData("ELETAG_CODE", dzbq);							  							
/////////////////////////////////////////////////////////////////////////////////////////////	
					/*String orderCode = st.getCell(0, i).getContents().trim();
					String maxQtyStr = st.getCell(3, i).getContents();
					String safeQtyStr = st.getCell(3, i).getContents();*/
/*					String minQtyStr = st.getCell(4, i).getContents();
					String jjQtyStr = st.getCell(5, i).getContents();
*/				/*	int maxQty =0;					
					if(!"".equals(maxQtyStr)) {
						maxQty= Integer.valueOf(maxQtyStr);
					}
					int safeQty=0;
					if(!"".equals(safeQtyStr)) {
						safeQty= Integer.valueOf(safeQtyStr);
					}*/
/*					int minQty = 0;
					if(!"".equals(minQtyStr)) {
						minQty =  Integer.valueOf(minQtyStr);
					}
					int jjQty=0;
					if(!"".equals(jjQtyStr)) {
						jjQty = Integer.valueOf(jjQtyStr);
					}
*/					
/*	System.out.println("ORDER_CODE:"+orderCode+"---"+"MAX_QTY:"+maxQty+"---"+"SAFE_QTY:"+safeQty);
					parm.addData("ORDER_CODE", orderCode);
					parm.addData("MAX_QTY", maxQty);
					parm.addData("SAFE_QTY", safeQty);*/
				//	parm.addData("MIN_QTY", minQty);
				//	parm.addData("ECONOMICBUY_QTY", jjQty);	
					   
//////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
				   /* String orderCode = st.getCell(0, i).getContents().trim();
				    String tagCode = st.getCell(1, i).getContents().trim();
				    parm.addData("ORDER_CODE", orderCode);
					parm.addData("TAG_CODE", tagCode);	*/			
				/*	TParm result = SPCSysFeeTool.getInstance().searchTag(parm);
					if(result.getCount()<=0) {
						System.out.println(orderCode);				
					}*/
//////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////
				/*	String orderCode = st.getCell(0, i).getContents().trim();
					String packUnit = st.getCell(3, i).getContents().trim();
					parm.addData("ORDER_CODE", orderCode);
					parm.addData("PACK_UNIT", packUnit);*/
					count++;                                            
				}
				parm.setCount(count);
				// int count = parm.getCount();
				if (count < 1) {
					this.messageBox("��������ʧ��");
					return;
				}					
				/*TParm result = TIOM_AppServer.executeAction(
						"action.spc.SPCSysFeeAction", "indStockInsert", parm);
				if (result.getErrCode() < 0) {
					this.messageBox("����ʧ��");
					return;
				}	*/						
/////////////////////////////////////////////////////////////////////////////////////////
				/*TParm result = TIOM_AppServer.executeAction(
						"action.spc.SPCSysFeeAction", "updateQty", parm);					
				if (result.getErrCode() < 0) {
					this.messageBox("����ʧ��");      													                                  
					return;
				} */
				TParm result = TIOM_AppServer.executeAction(
						"action.spc.SPCSysFeeAction", "updateTag", parm);					
				if (result.getErrCode() < 0) {		
					this.messageBox("����ʧ��");      													                                  
					return;
				} 
				/*TParm result = TIOM_AppServer.executeAction(
						"action.spc.SPCSysFeeAction", "updatePackUnit", parm);								
				if (result.getErrCode() < 0) {				
					this.messageBox("����ʧ��");      													                                  
					return;
				}*/
				this.messageBox("���³ɹ�");
			} catch (BiffException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void onQuery() {
		TParm tparm = new TParm();
		String ORDER_CODE = this.getValueString("ORDER_CODE");
		if (!"".equals(ORDER_CODE) && ORDER_CODE != null) {
			tparm.setData("ORDER_CODE", ORDER_CODE);
		}
		tparm.setData("REGION_CODE", "H01");
		TParm result = SPCSysFeeTool.getInstance().querySpcSysFee(tparm);
		TTable talbe = this.getTable("TABLE_M");
		talbe.setParmValue(result);
	}

	/**
	 * �õ�Table����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
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
	}

	/**
	 * �������¼�
	 */
	public void onTableClicked() {
		TTable t = this.getTable("TABLE_M");
		int row = t.getSelectedRow();
		if (row != -1) {
			TParm parm = t.getParmValue();
			this.setValue("ORDER_CODE", parm.getData("ORDER_CODE", row));
			this.setValue("ORDER_DESC", parm.getData("ORDER_DESC", row));
			this.setValue("GOODS_DESC", parm.getData("GOODS_DESC", row));				
			this.setValue("SPECIFICATION", parm.getData("SPECIFICATION", row));
			this
					.setValue("HIS_ORDER_CODE", parm.getData("HIS_ORDER_CODE",
							row));
			callFunction("UI|DELETE|setEnabled", true);
			action = "update";
		}
	}
	
	/**
	 * �������
	 */
	public void onSave() {
		TParm parm = new TParm();
		parm.setData("REGION_CODE","H01");   
		parm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
		parm.setData("ORDER_DESC", getValueString("ORDER_DESC"));
		parm.setData("GOODS_DESC", getValueString("GOODS_DESC"));
		parm.setData("SPECIFICATION", getValueString("SPECIFICATION"));
		parm.setData("HIS_ORDER_CODE", getValueString("HIS_ORDER_CODE"));
		parm.setData("OPT_USER", Operator.getID());  
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM", Operator.getIP());
		TParm result;
		if ("save".equals(action)) {
			if (!CheckSaveData()) {
				return;  
			}
			result = SPCSysFeeTool.getInstance().insertSysFeeSpc(parm);
			if(result.getErrCode()<0) {
				this.messageBox("�������ʧ��");   
				return;
			}
			this.setValue("ORDER_CODE", "");
			this.setValue("ORDER_DESC", "");
			this.setValue("GOODS_DESC", "");
			this.setValue("SPECIFICATION", "");
			this.setValue("HIS_ORDER_CODE", "");
			callFunction("UI|DELETE|setEnabled", false);
			this.messageBox("����ɹ�");
			onQuery();
		} else if ("update".equals(action)) {
			if (!CheckUpdateData()) {
				return;
			}
			result = SPCSysFeeTool.getInstance().updateSysFeeSpc(parm);
			if(result.getErrCode()<0) {
				this.messageBox("��������ʧ��");  
				return;
			}
			this.setValue("ORDER_CODE", "");
			this.setValue("ORDER_DESC", "");
			this.setValue("GOODS_DESC", "");
			this.setValue("SPECIFICATION", "");
			this.setValue("HIS_ORDER_CODE", "");
			callFunction("UI|DELETE|setEnabled", false);
			this.messageBox("���³ɹ�");
			onQuery();
		}
	}
	
	/**
	 * ����������
	 * @return
	 */
	private boolean CheckSaveData() {
		if ("".equals(getValueString("ORDER_CODE"))) {
			this.messageBox("������ҩƷ���벻��Ϊ��");
			return false;
		}
		if ("".equals(getValueString("HIS_ORDER_CODE"))) {
			this.messageBox("HISҩƷ���벻��Ϊ��");
			return false;
		}
		TParm searchParm = new TParm();
		searchParm.setData("REGION_CODE","H01");
		searchParm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
		TParm result = SPCSysFeeTool.getInstance().querySpcSysFee(searchParm);
		if (result.getErrCode() < 0 || result.getCount() > 0) {
			this.messageBox("�������������Ѿ�����");
			return false;
		}      
		TParm searchParm1 = new TParm();
		searchParm1.setData("REGION_CODE","H01");
		searchParm1.setData("HIS_ORDER_CODE", getValueString("HIS_ORDER_CODE"));
		result = SPCSysFeeTool.getInstance().querySpcSysFee(searchParm1);
		if (result.getErrCode() < 0 || result.getCount() > 0) {
			this.messageBox("��HIS�����Ѿ�����");
			return false;
		}
		return true;  
	}
	
	/**
	 * ��˸�������
	 * @return
	 */
	private boolean CheckUpdateData() {
		if ("".equals(getValueString("ORDER_CODE"))) {
			this.messageBox("������ҩƷ���벻��Ϊ��");
			return false;
		}
		if ("".equals(getValueString("HIS_ORDER_CODE"))) {
			this.messageBox("HISҩƷ���벻��Ϊ��");
			return false;
		}
	/*	TParm searchParm = new TParm();
		searchParm.setData("REGION_CODE","H01");
		searchParm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
		searchParm.setData("HIS_ORDER_CODE", getValueString("HIS_ORDER_CODE"));
		TParm result = SPCSysFeeTool.getInstance().querySpcSysFee(searchParm);
		if (result.getErrCode() < 0 || result.getCount() > 0) {
			this.messageBox("�ñ����Ӧ��ϵ�Ѵ���");
			return false;
		}    */
		return true;
	}
	
	/**
	 * ��ղ���
	 */
	public void onClear() {
		this.setValue("ORDER_CODE", "");
		this.setValue("ORDER_DESC", "");
		this.setValue("GOODS_DESC", "");
		this.setValue("SPECIFICATION", "");
		this.setValue("HIS_ORDER_CODE", "");
		TTable t = this.getTable("TABLE_M");
		t.removeRowAll();
		action = "save";
	}
	
	/**
	 * ����ɾ������
	 */
	public void onDelete() {
		if (this.messageBox("ѯ��", "ȷ��ɾ��?", 0) == 1) {
			return;
		}
		if (this.getValue("ORDER_CODE").equals("")
				|| this.getValue("ORDER_CODE").equals(null)) {
			this.messageBox("��ѡ��Ҫɾ�����");
			return;
		}
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
		parm.setData("HIS_ORDER_CODE", getValueString("HIS_ORDER_CODE"));
		TParm result = SPCSysFeeTool.getInstance().deleteSysFeeSpc(parm);
		if(result.getErrCode()<0) {
			err(result.getErrCode() + " " + result.getErrText());
			this.messageBox("ɾ������ʧ��");
			return;
		}
		this.setValue("ORDER_CODE", "");
		this.setValue("ORDER_DESC", "");
		this.setValue("GOODS_DESC", "");
		this.setValue("SPECIFICATION", "");
		this.setValue("HIS_ORDER_CODE", "");
		callFunction("UI|DELETE|setEnabled", false);
		this.messageBox("ɾ���ɹ���");
		onQuery();
	}

}
