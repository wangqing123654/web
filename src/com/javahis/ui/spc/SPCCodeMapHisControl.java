package com.javahis.ui.spc;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import jdo.spc.SPCCodeMapHisTool;
import jdo.spc.SPCSQL;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:������ҽԺ��ҩƷ����ȶ�
 * </p>
 * 
 * <p>
 * Description:������ҽԺ��ҩƷ����ȶ�
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
public class SPCCodeMapHisControl extends TControl {

	private TTable table; // table
	private TTextField order_code;// ������ҩƷ����
	private TTextField order_desc;// ������ҩƷ����
	private TTextField order_spe;// ������ҩƷ���

	private TComboBox region_code;// ��Ӧ�̴���
	private TTextField his_order_code;// ��Ӧ��ҩƷ����

	/** ������ */
	public SPCCodeMapHisControl() {
		super();
	}

	/** ��ʼ������ */
	public void onInit() {
		table = (TTable) getComponent("TABLE_M");

		order_code = (TTextField) getComponent("ORDER_CODE");// ������ҩƷ����
		order_desc = (TTextField) getComponent("ORDER_DESC");
		;// ������ҩƷ����
		order_spe = (TTextField) getComponent("SPECIFICATION");
		;// ������ҩƷ���

		region_code = (TComboBox) getComponent("REGION_CODE");
		;// ��Ӧ�̴���
		his_order_code = (TTextField) getComponent("HIS_ORDER_CODE");
		;// ��Ӧ��ҩƷ����
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

	/** ��ѯ���� */
	public void onQuery() {
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", order_code.getValue());

		parm.setData("REGION_CODE", region_code.getValue());
		
		parm.setData("HIS_ORDER_CODE", his_order_code.getValue());

		TParm result = SPCCodeMapHisTool.getInstance().query(parm);

		if (result.getCount() <= 0) {
			messageBox("��ѯ�޽����");
		}
		table.setParmValue(result);
	}

	/**
	 * �����޸Ĳ���
	 */
	public void onSave() {
		//identify by shendr 2013-08-06
		String orderCodeForSync = this.getValueString("ORDER_CODE");
		TParm parm = this
				.getParmForTag("ORDER_CODE;REGION_CODE;HIS_ORDER_CODE;ACTIVE_FLG");

		// �ж��Ƿ�һ��order_code��Ӧ���his_order_code
		TParm backParm = new TParm();
		backParm = SPCCodeMapHisTool.getInstance().queryCount(parm);
		if (backParm.getCount() > 1) {
			messageBox("��ͬ���������벻�ö�Ӧ���ҽԺ����");
			return;
		}
		backParm = SPCCodeMapHisTool.getInstance().queryCounts(backParm);
		if(backParm.getInt("COUNTS") == 1 && "Y".equals(parm.getValue("ACTIVE_FLG"))){
			messageBox("������ͬʱ����������ͬ��ҽԺҩƷ����");
			return;
		}

		if (!checkData(parm))
			return;

		// ���ϵͳ���Ƿ��и�ҩƷ
		boolean queryFlg = SPCCodeMapHisTool.getInstance().queryBase(parm);
		if (!queryFlg) {
			messageBox("ϵͳ��û�и�ҩƷ��");
			return;
		}

		// ����֮ǰ�Ȳ�ѯ,���ݿ����Ƿ��иü�¼
		int row1 = table.getSelectedRow();
		if (row1 >= 0) {
			parm.setData("ACTIVE_FLG", table.getItemString(table
					.getSelectedRow(), "ACTIVE_FLG"));
		}
		TParm result = SPCCodeMapHisTool.getInstance().updateQuery(parm);
		parm.setData("ACTIVE_FLG", getValueString("ACTIVE_FLG"));
		if (result.getCount("ORDER_CODE") > 0) {
			int row = table.getSelectedRow();
			if (row >= 0) {
				parm.setData("ACTIVE_FLG_UPDATE", table.getItemString(row,
						"ACTIVE_FLG"));
			}
			boolean flg = SPCCodeMapHisTool.getInstance().update(parm);
			if (flg) {
				messageBox("����ɹ�");
				updatePhaSync(orderCodeForSync);
				onClear();
				onQuery();
				return;
			} else {
				messageBox("����ʧ��");
				return;
			}
		} else {
			boolean flg = false;
			// �ж���������������Ƿ��ж�Ӧ������
			TParm resultSave = SPCCodeMapHisTool.getInstance().queryLastOrder(parm);
			if(resultSave.getCount()>0){
				// ��ѯ��һ����¼�Ƿ��п����
				TParm resultQty = SPCCodeMapHisTool.getInstance().queryQtyByOrderCode(parm);
				if(resultQty.getCount()>0){
					String str = "";
					for (int i = 0; i < resultQty.getCount(); i++) {
						str += ""+resultQty.getData("ORG_CHN_DESC",i) + "��ҩƷ�����Ϊ��";
						str += resultQty.getData("STOCK_QTY",i);
						str += "\n";
					}
					messageBox(str+"����ִ�л������!");
					return;
				}else{
					SPCCodeMapHisTool.getInstance().updateActiveFlg(parm);
					flg = SPCCodeMapHisTool.getInstance().save(parm);
				}
			} else {
				flg = SPCCodeMapHisTool.getInstance().save(parm);
			}
			if (flg) {
				messageBox("����ɹ�");
				updatePhaSync(orderCodeForSync);
				onClear();
				onQuery();
				return;
			}
			messageBox("����ʧ��");
		}
	}
	
	/**
     * ͬ��PHA_BASE��PHA_TRANSUNIT
     * @author shendr 2013.09.02
     */
    public void updatePhaSync(String order_code){
    	TParm tParm = new TParm();
    	tParm.setData("ORDER_CODE", order_code);
    	TParm parm = SPCCodeMapHisTool.getInstance().queryPha(tParm);
    	String flg = "";
    	int row = table.getSelectedRow();
    	if(row <0)
    		flg = "insert";
    	else
    		flg = "update";
    	parm.setData("FLG", flg);
    	TParm hisParm = SPCSQL.getOrderCode(order_code);
    	// ���û��HIS���룬����ͬ��
    	if(hisParm.getCount()<=0)
    		return;
    	else{
    		String hisOrderCode =hisParm.getValue("HIS_ORDER_CODE");
        	TParm isExist = SPCSQL.queryOrderCodeIsExistPha(hisOrderCode);
        	// ���HIS������PHA_BASE��PHA_TRANSUNIT��û�У�����ͬ��
        	if(isExist.getCount()<=0)
        		return;
        	else{
        		parm.setData("ORDER_CODE", hisOrderCode);
        		// ����HIS�ӿ�ִ��ͬ������
            	TParm result = TIOM_AppServer.executeAction(
                        "action.spc.SPCPhaBaseSyncExecute", "executeSync", parm);
            	this.messageBox("ͬ��ִ�����!");
            	System.out.println(result.getData("RESULT_MEG"));
        	}
    	}
    }

	/** ɾ������ */
	public void onDelete() {

		table.acceptText();
		int rowno = table.getSelectedRow();

		if (rowno < 0) {
			messageBox("��ѡ��Ҫɾ������Ϣ");
			return;
		}

		TParm parm = table.getParmValue().getRow(rowno);

		if (this.messageBox("��ʾ", "�Ƿ�ɾ���ü�¼", 2) == 0) {
			boolean flg = SPCCodeMapHisTool.getInstance().delete(parm);

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

		setValueForParm(
				"ORDER_CODE;ORDER_DESC;SPECIFICATION;REGION_CODE;HIS_ORDER_CODE;ACTIVE_FLG",
				table.getParmValue(), rowno);
		order_code.setEnabled(false);

		if (null == region_code.getValue()
				|| "".equals(region_code.getValue().trim())) {
			region_code.setEnabled(true);
		} else {
			region_code.setEnabled(false);
		}

	}

	/** ����ҩƷ���ձ� */
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
					String orderCode = st.getCell(0, i).getContents().trim();
					String regionCode = st.getCell(1, i).getContents().trim();
					String hisOrderCode = st.getCell(2, i).getContents().trim();
					parm.addData("ACTIVE_FLG_UPDATE",
							getValueString("ACTIVE_FLG"));
					int row1 = table.getSelectedRow();
					if (row1 >= 0) {
						parm.addData("ACTIVE_FLG", table.getItemString(row1,
								"ACTIVE_FLG"));
					}
					parm.addData("ORDER_CODE", orderCode);
					parm.addData("REGION_CODE", regionCode);
					parm.addData("HIS_ORDER_CODE", hisOrderCode);

					count++;
				}

				parm.setCount(count);
				if (count < 1) {
					this.messageBox("��������ʧ��");
					return;
				}

				TParm result = TIOM_AppServer.executeAction(
						"action.spc.SPCCodeMapHisAction", "importMap", parm);

				String flg = result.getValue("FLG");
				if ("Y".equals(flg)) {
					this.messageBox("���³ɹ���");
					onQuery();

				} else if ("N".equals(flg)) {
					this.messageBox("����ʧ�ܣ�");
					return;
				} else {
					this.messageBox(flg);
				}

			} catch (BiffException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/** ��ղ��� */
	public void onClear() {
		String values = "ORDER_CODE;ORDER_DESC;SPECIFICATION;REGION_CODE;HIS_ORDER_CODE";
		this.clearValue(values);
		((TCheckBox)getComponent("ACTIVE_FLG")).setSelected(true);
		order_code.setEnabled(true);
		region_code.setEnabled(true);
	}

	/** ���������ѵ��¼ */
	private boolean checkData(TParm parm) {
		// REGION_CODE;HIS_ORDER_CODE
		if (null == parm.getValue("ORDER_CODE")
				|| "".equals(parm.getValue("ORDER_CODE"))) {
			messageBox("������ҩƷ���벻��Ϊ�գ�");
			return false;
		}
		if (null == parm.getValue("REGION_CODE")
				|| "".equals(parm.getValue("REGION_CODE").toString())) {
			messageBox("Ժ������Ϊ�գ�");
			return false;
		}
		if (null == parm.getValue("HIS_ORDER_CODE")
				|| "".equals(parm.getValue("HIS_ORDER_CODE"))) {
			messageBox("Ժ��ҩƷ���벻��Ϊ�գ�");
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
	}

}
