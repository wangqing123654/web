package com.javahis.ui.ins;

import jdo.ins.INSRuleTXTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>
 * Title:��Ŀ�ֵ�
 * </p>
 * 
 * <p>
 * Description: ҽ����Ŀ�ֵ��Ӧ��ҩƷ
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS 2.0 (c) 2011
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author pangb 2012-1-20
 */
public class INSOrderJoinPHAControl extends TControl {
	private TTable tableFee;// SYS_FEE����
	private TParm regionParm;// ҽ���������
	private TTable tableRule;// ҽ������
	private TParm ruleParm;// ������е�ҽ������
	private TParm tableNewFee;// ����ƥ�䱣������
	// ���һ���������� ����ƥ���ѯ���ݲ���ʹ�� ��SYS_FEE��������һ����������
	private TParm tempSysFeeParm;

	public void onInit() { // ��ʼ������
		super.onInit();
		tableFee = (TTable) this.getComponent("TABLE_FEE");// SYS_FEE�շ�����
		tableRule = (TTable) this.getComponent("TABLE_RULE");// ҽ������
		regionParm  = SYSRegionTool.getInstance().selectdata(Operator.getRegion());//���ҽ���������
		String sql="SELECT 'N' FLG,SFDLBM, SFXMBM, XMBM, "+
		  " XMMC, XMRJ, TXBZ, "+
		  " XMLB, JX, GG, "+
		  " DW, YF, YL, "+
		  " SL, PZWH, BZJG, "+
		  " SJJG, ZGXJ, ZFBL1, "+
		  " ZFBL2, ZFBL3, BPXE,"+
		  " BZ, TJDM, FLZB1, "+
		   "FLZB2, FLZB3, FLZB4, "+
		  " FLZB5, FLZB6, FLZB7, "+
		  " SPMC, SPMCRJ, LJZFBZ, "+
		  " YYZJBZ, YYSMBM, FPLB, "+
		  " KSSJ, JSSJ, SYFW, "+
		  " SCQY, FCFYBS, MZYYBZ," +
		  " XMBZ, FYMGL, YKD228," +
		  " YKD241, AZBBZ, YKD242," +
		  " TXXMBZ,INS_ORDER_CODE FROM INS_RULE ";
	 //	TParm temParm=new TParm(TJDODBTool.getInstance().select(sql));
		
		//ruleParm = INSRuleTXTool.getInstance().selectINSRule(new TParm());
		tableNewFee = new TParm();// ����ƥ�䱣������
		// ҽ�����ݸ�ѡ���¼�
		tableRule.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		// SYS_FEEҽ�����ݸ�ѡ���¼�
		tableFee.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		getTempSysParm();
		callFunction("UI|save|setEnabled", false);// �������
	}

	/**
	 * ����ƥ������combox����ִ�в���
	 */
	public void onFactor() {
		if (getRadioButton("SELECT_DATA").isSelected()) {
			getComboBox("CMB_FACTOR").setEnabled(true);
			onExeEnabled(false);// ����ִ��
		} else {
			getComboBox("CMB_FACTOR").setEnabled(false);// �����б���ѡ
			onExeEnabled(true);// ������ִ��

		}
		callFunction("UI|query|setEnabled", true);// ��ѯ����
		this.setValue("CMB_FACTOR", "");
		checkEnabled(true);
		removeTable();
	}

	/**
	 * �����ӿ�������
	 */
	private void getTempSysParm() {
		String[] tempSysFee = { "FLG", "ORDER_CODE", "ORDER_DESC",
				"NHI_CODE_I", "NHI_CODE_O", "NHI_CODE_E", "NHI_FEE_DESC",
				"NHI_PRICE", "INSPAY_TYPE", "OWN_PRICE", "DOSE_CODE",
				"SPECIFICATION", "MAN_CODE" };
		tempSysFeeParm = new TParm();
		for (int i = 0; i < tempSysFee.length; i++) {
			if ("FLG".equals(tempSysFee[i])) {
				tempSysFeeParm.addData(tempSysFee[i], "N");
			} else {
				tempSysFeeParm.addData(tempSysFee[i], "");
			}

		}
		tempSysFeeParm.setCount(tempSysFee.length);
	}

	/**
	 * ��õ�ѡ�ؼ�
	 * 
	 * @param name
	 * @return
	 */
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}

	/**
	 * ��������б�ؼ�
	 * 
	 * @param name
	 * @return
	 */
	private TComboBox getComboBox(String name) {
		return (TComboBox) this.getComponent(name);
	}

	/**
	 * ��ѡδƥ���¼�
	 */
	public void onClickNoMate() {
		if (getRadioButton("RDO_NO_MATE").isSelected()) {// δƥ�䵥ѡ��ť
			onExeEnabledOne(true);
			if (getRadioButton("SAME_DATA").isSelected()) {// ��ȫƥ�����ݵ�ѡ��ťѡ�в�����ִ�в�ѯ����
				onExeEnabled(true);// ������ִ��
				getComboBox("CMB_FACTOR").setEnabled(false);// ����ƥ�������б�
			} else {
				onExeEnabled(false);// ����ִ��
				getComboBox("CMB_FACTOR").setEnabled(true);// ����ƥ�������б�

			}
		} else {// ��ƥ�䵥ѡ��ť
			onExeEnabledOne(false);
			onExeEnabled(true);// ����ִ��
			callFunction("UI|save|setEnabled", false);// �������
		}
		checkEnabled(true);
		removeTable();
	}

	/**
	 * ִ�п�ѡ��ť����
	 * 
	 * @param flg
	 */
	private void onExeEnabledOne(boolean flg) {
		getRadioButton("SAME_DATA").setEnabled(flg);// ��ȫƥ�����ݵ�ѡ��ť
		getRadioButton("SELECT_DATA").setEnabled(flg);// ������ѯִ��
		getComboBox("CMB_FACTOR").setEnabled(flg);// ����ƥ�������б�
		this.setValue("CMB_FACTOR", "");
	}

	/**
	 * �Ƴ��������
	 */
	private void removeTable() {
		tableFee.setParmValue(new TParm());
		tableRule.setParmValue(new TParm());
		tableFee.removeRowAll();
		tableRule.removeRowAll();
	}

	/**
	 * ִ����ȫƥ�����ݵ�ѡ��ť�����ܿ�
	 * 
	 * @param flg
	 */
	private void onExeEnabled(boolean flg) {
		// callFunction("UI|query|setEnabled", flg);// ��ѯ����
		callFunction("UI|removeUpdate|setEnabled", flg);// ��������
		callFunction("UI|save|setEnabled", flg);// �������
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		TParm result = new TParm();
		int type = -1;
		// ��ƥ��ѡ��
		if (getRadioButton("RDO_MATE").isSelected()) {
			result = onQueryMate();
			type = 1;
		} else {
			// δƥ��ѡ��
			if (getRadioButton("SAME_DATA").isSelected()) {// ��ȫƥ������ѡ��
				result = onQueryMate();
				type = 1;
			} else {
				result = onQueryNoMate();
				type = 2;
			}
		}
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (result.getCount() <= 0) {
			removeTable();
			this.messageBox("û��Ҫ��ѯ������");
			return;
		}
		TParm newRuleParm = null;
		switch (type) {// ��ѯ���� 1.��ƥ�� 2.δƥ��
		case 1:
			newRuleParm = getNewRuleParm(result, true);
			break;
		case 2:
			newRuleParm = getNewRuleParm(result, false);
			break;
		}
		if (type == 2) {// δƥ��
			if (null == newRuleParm) {
				this.messageBox("û��Ҫ��ѯ������");
				callFunction("UI|save|setEnabled", false);// �������
				checkEnabled(true);
				removeTable();
				return;
			}
			callFunction("UI|save|setEnabled", true);// �������
			checkEnabled(false);
			tableFee.setParmValue(tableNewFee);// SYS_FEE ����
			tableRule.setParmValue(newRuleParm);// ҽ������
		} else if (type == 1) {// ��ƥ��
			tableFee.setParmValue(result);// SYS_FEE ����
			tableRule.setParmValue(newRuleParm);// ҽ������
		}

	}

	/**
	 * ҽ��������ʾ
	 * 
	 * @param result
	 *            flg ִ�в�ѯ���� true:��ƥ�����ݲ�ѯ false ������ƥ�����ݲ�ѯ
	 * @return
	 */
	private TParm getNewRuleParm(TParm result, boolean flg) {
		String xmbm = null;
		TParm newRuleParm = new TParm();// ��ѯ��ֵҽ������
		int index = 0;// �ۼƸ���
		// int indexNew = 0;// SYS_FEE����������
		int ruleDescIndex = -1;// ��Ŀ�ֵ�������Ƴ���
		int type = -1;// �������ƶ�
		StringBuffer sameName = null;// �ۼ���ͬ��ҽ������ ����ƥ�����ݲ���ʱ�����SYS_FEE�����������ӿ���
		// ҽ�����ֵ
		for (int i = 0; i < result.getCount(); i++) {
			String orderDesc = result.getValue("ORDER_DESC", i);// ҽ������
			int descIndex = orderDesc.length();// ҽ�����Ƹ���
			sameName = new StringBuffer();
			for (int j = 0; j < ruleParm.getCount(); j++) {
				if (flg) {
					xmbm = getIsNull(result, i);// ���ҽ��������Ŀ����
					if (xmbm.equals(ruleParm.getValue("XMBM", j))) {
						newRuleParm.setRowData(index, ruleParm, j);
						index++;
						break;
					}
				} else {

					type = this.getValueInt("CMB_FACTOR");// �������ƶ� ����
					// ������Ŀ����1��100%��2��80%��3��50%��4��50%����
					switch (type) {
					case 1:
						if (result.getValue("ORDER_DESC", i).equals(
								ruleParm.getValue("XMMC", j))) {// ��Ŀ������ͬ
							tableNewFee.setRowData(index, result, i);
							newRuleParm.setRowData(index, ruleParm, j);
							index++;
						}
						break;
					case 2:// 80%��ͬ

						// 
						ruleDescIndex = ruleParm.getValue("XMMC", j).length() * 4 / 5;// ��Ŀ�ֵ��ȡ80%
						// ��ͬ
						if (descIndex >= ruleDescIndex) {
							// �ж��Ƿ�����ҽ������80%��ͬ
							if (orderDesc.contains(ruleParm.getValue("XMMC", j)
									.substring(0, ruleDescIndex))) {
								if (sameName.toString().contains(orderDesc)) {
									tableNewFee.setRowData(index,
											tempSysFeeParm, 0);// ���һ������
								} else {
									tableNewFee.setRowData(index, result, i);// ���һ������
									sameName.append(orderDesc + ",");// �ۼ���ͬҽ����������
								}

								// System.out.println("sameName:::"+sameName);
								newRuleParm.setRowData(index, ruleParm, j);// ��Ŀ�ֵ�����ʾ����
								index++;
							}
						}
						break;
					case 3:// 50%��ͬ
						ruleDescIndex = ruleParm.getValue("XMMC", j).length() * 1 / 2;// ��Ŀ�ֵ��ȡ50%
						// ��ͬ
						if (descIndex >= ruleDescIndex) {
							// �ж��Ƿ�����ҽ������50%��ͬ
							if (orderDesc.contains(ruleParm.getValue("XMMC", j)
									.substring(0, ruleDescIndex))) {
								if (sameName.toString().contains(orderDesc)) {
									tableNewFee.setRowData(index,
											tempSysFeeParm, 0);// ���һ������
								} else {
									tableNewFee.setRowData(index, result, i);
									sameName.append(orderDesc + ",");// �ۼ���ͬҽ����������
								}
								// tableNewFee.setRowData(indexNew, result, i);
								newRuleParm.setRowData(index, ruleParm, j);
								index++;
							}
						}

						break;
					case 4:// 50%���� ��ʾ����û��ִ�е�����
						tableNewFee = result;
						newRuleParm = ruleParm;
						break;
					}
				}

			}
		}
		// ����ƥ�����ݲ���
		if (!flg && (type == 1 || type == 2 || type == 3)) {
			if (index == 0) {
				return null;
			}
			tableNewFee.setCount(index);
		}
		if (type != 4) {// ����ƥ������δִ�е�����
			newRuleParm.setCount(index);
		}
		return newRuleParm;
	}

	/**
	 * ��ƥ�����ݲ�ѯ
	 * 
	 * @return
	 */
	private TParm onQueryMate() {
		TParm result = INSRuleTXTool.getInstance()
				.selectMateSysFee(new TParm());
		return result;
	}

	/**
	 * δƥ�����ݲ�ѯ
	 * 
	 * @return
	 */
	private TParm onQueryNoMate() {
		TParm result = null;
		// ����ƥ�����ݲ���
		if (getRadioButton("SELECT_DATA").isSelected()) {
			if (!this.emptyTextCheck("CMB_FACTOR")) {
				return null;
			}
			result = INSRuleTXTool.getInstance().selectNotMateSysFee(
					new TParm());// ��ѯ��Ҫƥ������� SYS_FEE ����
		}

		return result;

	}

	/**
	 * �����¼�
	 */
	public void onTableDoubleClick() {
		int row = tableFee.getSelectedRow();
		if (row < 0) {
			return;
		}
		TParm tableParm = tableFee.getParmValue();
		TParm parm = new TParm();
		//parm.setData("SFDLBM", regionParm.getValue("NHI_NO", 0));
		String xmbm = getIsNull(tableParm, row);// ���ҽ��������Ŀ����
		if (null != xmbm) {
			parm.setData("SFXMBM", xmbm);
		}
		TParm result = INSRuleTXTool.getInstance().selectINSRule(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		tableRule.setParmValue(result);
	}

	/**
	 * ȫѡ��ť����
	 * 
	 * @param flg
	 */
	private void checkEnabled(boolean flg) {
		callFunction("UI|SELECT_ALL|setEnabled", flg);// ȫѡ
	}

	/**
	 * ���ҽ��������Ŀ����
	 * 
	 * @param parm
	 *            �������
	 * @param row
	 *            ��ǰ��
	 * @return
	 */
	public String getIsNull(TParm parm, int row) {
		String xmbm = null;
		if (null != parm.getValue("NHI_CODE_I", row)
				&& parm.getValue("NHI_CODE_I", row).length() > 0) {
			xmbm = parm.getValue("NHI_CODE_I", row);// סԺҽ��
		}
		if (null != parm.getValue("NHI_CODE_O", row)
				&& parm.getValue("NHI_CODE_O", row).length() > 0) {
			xmbm = parm.getValue("NHI_CODE_O", row);// ����ҽ��
		}
		if (null != parm.getValue("NHI_CODE_E", row)
				&& parm.getValue("NHI_CODE_E", row).length() > 0) {
			xmbm = parm.getValue("NHI_CODE_E", row);// ����ҽ��
		}
		return xmbm;
	}

	/**
	 * �������
	 */
	public void onSave() {
		if (getRadioButton("SAME_DATA").isSelected()) {// ��ȫƥ�����ݵ�ѡ��ťѡ�в���
			onExeSameSave();
			onQuery();
		}
		if (getComboBox("CMB_FACTOR").isEnabled()) {// ����ƥ�����ݸ�ѡ��ѡ�����{
			// ����ƥ����� �޸�ҽ����Ϣ
			onExeUpdate();
		}
	}

	/**
	 * ����ƥ������ �޸�ҽ������
	 */
	private void onExeUpdate() {
		boolean flg = false;
		TParm parm = tableFee.getParmValue();// ���ҽ����Ϣ
		TParm updateParm = new TParm();
		int index = 0;// �ۼ��޸����ݸ���
		for (int i = 0; i < parm.getCount(); i++) {
			// У���Ƿ�ѡ��
			if (parm.getBoolean("FLG", i)
					&& (null != parm.getValue("NHI_CODE_I", i)
							&& parm.getValue("NHI_CODE_I", i).length() > 0
							|| null != parm.getValue("NHI_CODE_O", i)
							&& parm.getValue("NHI_CODE_O", i).length() > 0 || null != parm
							.getValue("NHI_CODE_E", i)
							&& parm.getValue("NHI_CODE_E", i).length() > 0)) {
				flg = true;
				updateParm.setRowData(index, parm, i);// �ۼ�����
				index++;
			}
		}
		if (!flg) {
			this.messageBox("��ѡ��Ҫ�޸ĵ�����");
			return;
		}
		updateParm.setData("OPT_USER", Operator.getID());
		updateParm.setData("OPT_TERM", Operator.getIP());
		updateParm.setCount(index);
		// �޸�ҽ������
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSRuleTXAction", "updateSameSave", updateParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// ִ��ʧ��
		} else {
			this.messageBox("P0005");// ִ�гɹ�
		}
	}

	/**
	 * ��ȫƥ�����ݵ�ѡ��ťѡ�в���
	 */
	private void onExeSameSave() {
		TParm sysFeeParm = INSRuleTXTool.getInstance().selectSumSame(
				new TParm());
		if (sysFeeParm.getErrCode() < 0) {
			this.messageBox("E0005");// ִ��ʧ��
			return;
		}
		TParm parm = new TParm();
		int index = 0;
		for (int i = 0; i < sysFeeParm.getCount(); i++) {// ִ�н���Ŀ������ͬ�������޸�ҽ����Ŀ����
			for (int j = 0; j < ruleParm.getCount(); j++) {
				if (null != sysFeeParm.getValue("HYGIENE_TRADE_CODE", i)
						&& sysFeeParm.getValue("HYGIENE_TRADE_CODE", i)
								.length() > 0
						&& null != ruleParm.getValue("PZWH", j) && ruleParm.getValue("PZWH", j).length()>0
						&& sysFeeParm.getValue("HYGIENE_TRADE_CODE", i).equals(
								ruleParm.getValue("PZWH", j))) {

					parm.addData("ORDER_CODE", sysFeeParm.getValue(
							"ORDER_CODE", i));// ҽ������
					parm.addData("NHI_CODE_O", ruleParm.getValue("XMBM", j));// ����ҽ������
					parm.addData("NHI_CODE_E", ruleParm.getValue("XMBM", j));// ����ҽ������
					parm.addData("NHI_CODE_I", ruleParm.getValue("XMBM", j));// סԺҽ������
					parm.addData("NHI_FEE_DESC", ruleParm.getValue("XMMC", j));// ҽ������
					parm.addData("NHI_PRICE", ruleParm.getValue("SJJG", j));// ҽ������
					parm.addData("INSPAY_TYPE", ruleParm.getValue("XMLB", j));// ҽ���������A��ҽ��
					// B������C���Է�
					index++;
					break;
					// parm.addData("ADDPAY_RATE", ruleParm.getValue("ZFBL1",
					// i));// ��������
				}
			}
		}
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setCount(index);
		// �޸�ҽ������
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSRuleTXAction", "updateSameSave", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// ִ��ʧ��
		} else {
			this.messageBox("P0005");// ִ�гɹ�
		}
	}

	/**
	 * SYS_FEEȫѡ����
	 */
	public void onSelectAll() {
		boolean flg = false;// �ж��Ƿ�ѡ��
		if (this.getValueBoolean("SELECT_ALL")) {
			flg = true;
		} else {
			flg = false;
		}
		TParm parm = tableFee.getParmValue();// �����������
		for (int i = 0; i < parm.getCount(); i++) {
			parm.setData("FLG", i, flg);
		}
		tableFee.setParmValue(parm);
	}

	/**
	 * ��ѡ��ѡ���¼�
	 * 
	 * @param obj
	 * @return
	 */
	public boolean onCheckBox(Object obj) {
		TTable table = (TTable) obj;
		// TParm oldTempParm = new TParm();
		// TParm oldParm = table.getParmValue();
		// int index = 0;
		table.acceptText();
		int ruleRow = table.getSelectedRow();// ҽ����ǰѡ����
		TParm ruleParm = table.getParmValue();// ҽ������
		if (getComboBox("CMB_FACTOR").isEnabled()) {// ����ƥ�����ݸ�ѡ��ѡ�����
			int row = tableFee.getSelectedRow();// SYS_FEEҽ�����ݻ�õ�ǰ��
			if (row < 0) {
				this.messageBox("��ѡ��Ҫ�޸ĵ�ҽ��");
				ruleParm.setData("FLG", ruleRow, "N");// ִ�д�������״̬
				tableRule.setParmValue(ruleParm);
				return false;
			}

			TParm sysFeeParm = tableFee.getParmValue();// SYS_FEEҽ��
			// ѡ�п��в�ִ��
			if (null == sysFeeParm.getValue("ORDER_CODE", row)
					|| sysFeeParm.getValue("ORDER_CODE", row).trim().length() <= 0) {
				this.messageBox("��ѡ��Ҫ�޸ĵ�ҽ��");
				ruleParm.setData("FLG", ruleRow, "N");// ִ�д�������״̬
				tableRule.setParmValue(ruleParm);
				return false;
			}
			if (ruleParm.getBoolean("FLG", ruleRow)) {// ҽ��ѡ�е��и�ѡ��״̬
				sysFeeParm.setData("FLG", row, "Y");// ѡ��
				sysFeeParm.setData("NHI_CODE_O", row, ruleParm.getValue("XMBM",
						ruleRow));// ����ҽ������
				sysFeeParm.setData("NHI_CODE_E", row, ruleParm.getValue("XMBM",
						ruleRow));// ����ҽ������
				sysFeeParm.setData("NHI_CODE_I", row, ruleParm.getValue("XMBM",
						ruleRow));// סԺҽ������
				sysFeeParm.setData("NHI_FEE_DESC", row, ruleParm.getValue(
						"XMMC", ruleRow));// ҽ������
				sysFeeParm.setData("NHI_PRICE", row, ruleParm.getValue("SJJG",
						ruleRow));// ҽ������
				sysFeeParm.setData("INSPAY_TYPE", row, ruleParm.getValue(
						"XMLB", ruleRow));// ҽ���������A��ҽ��
			} else {
				sysFeeParm.setData("FLG", row, "N");// ѡ��
				sysFeeParm.setData("NHI_CODE_O", row, "");// ����ҽ������
				sysFeeParm.setData("NHI_CODE_E", row, "");// ����ҽ������
				sysFeeParm.setData("NHI_CODE_I", row, "");// סԺҽ������
				sysFeeParm.setData("NHI_FEE_DESC", row, "");// ҽ������
				sysFeeParm.setData("NHI_PRICE", row, "");// ҽ������
				sysFeeParm.setData("INSPAY_TYPE", row, "");// ҽ���������A��ҽ��
			}

			tableFee.setParmValue(sysFeeParm);// ���¸�ֵ
			tableFee.setSelectedRow(row);// ѡ�е�ǰ��
		}
		return false;

	}

	/**
	 * �Ƴ�ѡ�е�ҽ�� ҽ������
	 */
	public void onRemoveUpdate() {
		if (getRadioButton("RDO_MATE").isSelected()
				|| getRadioButton("SAME_DATA").isSelected()) {// ��ƥ��������Ƴ�����
			TParm parm = tableFee.getParmValue();// ��ƥ��ҽ������
			if (null == parm) {
				this.messageBox("��ѡ��Ҫִ�е�����");
				return;
			}
			boolean flg = false;
			// TParm exeParm=new TParm();//ִ�е�����
			StringBuffer exeOrderCode = new StringBuffer();// ִ�е�����
			for (int i = 0; i < parm.getCount(); i++) {
				if (parm.getBoolean("FLG", i)) {// ѡ������
					flg = true;
					exeOrderCode.append("'" + parm.getValue("ORDER_CODE", i)
							+ "',");
				}
			}
			if (!flg) {
				this.messageBox("��ѡ��Ҫִ�е�����");
				return;
			}
			if (this.messageBox("��ʾ", "�Ƿ�ִ���Ƴ�����", 2) != 0) {
				return;
			}
			String exeOrder = exeOrderCode.toString().substring(0,
					exeOrderCode.toString().lastIndexOf(","));// ִ�е�����ȥ��ĩβ","
			// ִ��sql
			String sql = " UPDATE SYS_FEE SET NHI_CODE_O=NULL,NHI_CODE_E=NULL,NHI_CODE_I=NULL,NHI_FEE_DESC=NULL,"
					+ "NHI_PRICE = NULL,INSPAY_TYPE = NULL, OPT_USER='"
					+ Operator.getID()
					+ "',OPT_DATE=SYSDATE"
					+ ", OPT_TERM='"
					+ Operator.getIP()
					+ "' WHERE  ORDER_CODE IN ("
					+ exeOrder
					+ ")";
			TParm result = new TParm(TJDODBTool.getInstance().update(sql));// �޸��Ƴ�����
			if (result.getErrCode() < 0) {
				this.messageBox("E0005");// ִ��ʧ��
			} else {
				this.messageBox("P0005");// ִ�гɹ�
				onQuery();
			}
		}
	}

	/**
	 * ��ղ���
	 */
	public void onClear() {
		removeTable();
		onExeEnabledOne(false);
		callFunction("UI|removeUpdate|setEnabled", true);// ��������
		callFunction("UI|query|setEnabled", true);// ��ѯ����
		callFunction("UI|save|setEnabled", false);// �������
		checkEnabled(true);// ȫѡ
		callFunction("UI|SELECT_ALL|setSelectd", false);// ȫѡ
		// ((TCheckBox)this.getComponent("SELECT_ALL")).setSelected(false);
		getRadioButton("RDO_MATE").setSelected(true);// ��ƥ��
		getRadioButton("SAME_DATA").setSelected(true);// ��ȫƥ������
	}
}
