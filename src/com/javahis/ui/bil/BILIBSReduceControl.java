package com.javahis.ui.bil;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jdo.bil.PaymentCashTool;
import jdo.opb.OPBTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: סԺ����
 * </p>
 * 
 * <p>
 * Description: סԺ����
 * </p>
 * 
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author shibl
 * @version 1.0
 */
public class BILIBSReduceControl extends TControl {
	/**
	 * �����
	 */
	private String caseNo;
	/**
	 * ������ע��
	 */
	private String newBabyFlg;
	/**
	 * ������TParm
	 */
	private TParm newBabyTParm = new TParm();
	/**
	 * ҳǩ
	 */
	private TTabbedPane tab;
	/**
	 * �ܱ�
	 */
	private TTable tableM;
	/**
	 * ��Ŀ��
	 */
	private TTable tableD;
	/**
	 * �����
	 */
	private TTable tableDD;
	private Map rexpMap;
	private PaymentCashTool paymentTool;
	private String billNoSum;
	private TNumberTextField numberTextField;// yanjing 20141223 ���ʧȥ�����¼�
	private String lumpworkCode = "";// �ײʹ���LUMPWORK_CODE
	private TParm payTypeTParm;//΢��֧����ʹ��
	/**
	 * ��ʼ��
	 * */
	public void onInit() {
		TParm initParm = new TParm();
		// ===yanjing 20141223 modify start
		numberTextField = (TNumberTextField) this.getComponent("REDUCE_AMT");
		numberTextField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				// getRate();
			}

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				onShare();

			}
		});
		// ===yanjing 20141223 modify end
		Object obj = this.getParameter();
		if (obj == null || obj.equals("")) {
			return;
		}
		TPanel p = (TPanel) getComponent("tPanel_1");
		TParm parm = null;

		if (obj != null || obj != "") {
			initParm = (TParm) obj;
			parm = initParm.getParm("TYPE_PARM");
			caseNo = initParm.getValue("CASE_NO");
			billNoSum = initParm.getValue("BILL_NO_SUM");// ��Ҫ�������˵�����
			if (null != billNoSum && billNoSum.length() > 0) {
				billNoSum = billNoSum.substring(0, billNoSum.lastIndexOf(","));
			}
			payTypeTParm=initParm.getParm("payTypeTParm");//΢��֧����ʹ��
			// ��ʼ��������Ϣ
			this.setValue("MR_NO", initParm.getValue("MR_NO"));
			this.setValue("IPD_NO", initParm.getValue("IPD_NO"));
			this.setValue("PAT_NAME", initParm.getValue("PAT_NAME"));
			this.setValue("SEX_CODE", initParm.getValue("SEX_CODE"));
			this.setValue("CTZ1_CODE", initParm.getValue("CTZ1_CODE"));
			this.setValue("PRINT_NO", initParm.getValue("PRINT_NO"));
			this.setValue("ADM_DATE", initParm.getTimestamp("ADM_DATE"));
			this.setValue("NEW_BABY_FLG", initParm.getValue("NEW_BABY_FLG"));
			this.setValue("CASE_NO", caseNo);
			lumpworkCode = initParm.getValue("LUMPWORK_CODE");// ==pangben
			this.setValue("LUMPWORK_CODE", lumpworkCode);
			// 2014-6-9
			// �����ײ�����ⲻ����ͬʱʹ��
		}
		try {
			paymentTool = new PaymentCashTool(p, this, parm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initComponent();
		// if (lumpworkCode.trim().length() > 0) {// ===pangben 2014-6-9
		// ((TTabbedPane) this.getComponent("tTabbedPane_1")).setEnabledAt(1,
		// false);
		// this.callFunction("UI|REDUCE_AMT|SetEnabled", false);
		// callFunction("UI|REDUCE_PRINT|setEnabled", false);
		// }
		String apsql = "SELECT B.NHI_CTZ_FLG, C.IN_STATUS"
				+ " FROM ADM_INP A, SYS_CTZ B, INS_ADM_CONFIRM C"
				+ " WHERE A.CASE_NO = '" + caseNo + "'"
				+ " AND C.IN_STATUS <> '5'" + " AND A.CASE_NO = C.CASE_NO"
				+ " AND A.CTZ1_CODE = B.CTZ_CODE";// ҽ�������Բ�������
		TParm rstParm = new TParm(TJDODBTool.getInstance().select(apsql));
		if (rstParm.getCount() > 0) {
			callFunction("UI|REDUCE_PRINT|setEnabled", false);
		}
		paymentTool.setAmt(this.getValueDouble("OWN_AMT"));
	}

	/**
	 * ��ʼ���ؼ�
	 * */
	public void initComponent() {
		tab = this.getTTabbedPane("tTabbedPane_1");
		tab.setSelectedIndex(0);
		tableD = (TTable) this.getComponent("TABLED");
		tableDD = (TTable) this.getComponent("TABLEDD");
		tableM = (TTable) this.getComponent("TABLEM");
		callFunction("UI|ORDER_CODE|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSFeePopup.x");
		// textfield���ܻش�ֵ
		callFunction("UI|ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		tableD.addEventListener(tableD.getTag() + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onTableReduceAmt");
		rexpMap = this.getRexpMap();
		newBabyFlg = this.getValueString("NEW_BABY_FLG");
		if (newBabyFlg.equals("Y")) {
			String sql = " SELECT CASE_NO FROM ADM_INP WHERE M_CASE_NO='"
					+ this.caseNo + "' AND NEW_BORN_FLG='Y' "
					+ " AND CANCEL_FLG='N' AND  DS_DATE IS NOT NULL ";
			newBabyTParm = new TParm(TJDODBTool.getInstance().select(sql));
		}
		// ��Ĭ��ֵ
		this.setValue("REDUCE_USER", Operator.getID());
		this.onSelect();
	}

	private void setReduceAmt(double amt) {
		this.setValue("REDUCE_AMT", amt);
		paymentTool.setReduceAmt(amt);
		paymentTool.getAmts();
	}

	/**
	 * ҽ�����ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
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
		if (!order_code.equals("")) {
			onQueryOrderCode(order_code);
		}
	}

	/**
	 * ����ҽ����ѯ
	 */
	public void onQueryOrderCode(String orderCode) {
		String sql = this.getOrderCodeSql(orderCode);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() < 0) {
			this.tableD.removeRowAll();
			this.messageBox("���ò�ѯ����");
			return;
		}
		this.tableD.setParmValue(parm);
	}

	/**
	 * ��ʾȫ��
	 */
	public void onQueryAll() {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				this.getOrderCodeSql("")));
		// System.out.println("======="+this.getOrderCodeSql(""));//==liling
		// 20140711
		if (parm.getErrCode() < 0) {
			this.tableD.removeRowAll();
			this.messageBox("���ò�ѯ����");
			return;
		}
		this.tableD.setParmValue(parm);
		this.setValue("ORDER_CODE", "");
		this.setValue("ORDER_DESC", "");
	}

	/**
	 * ҳǩ�л�����
	 */
	public void onSelect() {
		if (tab.getSelectedIndex() == 0) {
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					getRexpTypeSql()));
			if (parm.getErrCode() < 0) {
				this.messageBox("���ò�ѯ����");
				tableM.removeRowAll();
				return;
			}
			this.tableM.setParmValue(parm);
			callFunction("UI|REDUCE_AMT|setEnabled", true);
			onSumAMT(parm);
		} else if (tab.getSelectedIndex() == 1) {
			this.setValue("TYPE", "1");// Ĭ�� 1
			onChange();
			this.tableDD.removeRowAll();
			callFunction("UI|REDUCE_AMT|setEnabled", false);
			onSumReduceAMT();
		}
		this.setValue("REDUCE_NOTE", "");
		// onReducePrintEable();

	}

	/**
	 * ����������Զ���̯
	 */
	public void onShare() {
		if (tab.getSelectedIndex() == 0) {
			double reduceAmt = this.getValueDouble("REDUCE_AMT");
			double arAmt = this.getValueDouble("OWN_AMT");
			if (reduceAmt < 0) {
				this.messageBox("�����ܶ��С��0��");
				setReduceAmt(0.00);
				return;
			}
			if (reduceAmt > arAmt) {
				this.messageBox("�����ܶ�ܴ��ڷ��÷����ܶ");
				setReduceAmt(0.00);
				return;
			}
			DecimalFormat sfd = new DecimalFormat("######0.00");
			BigDecimal sum = new BigDecimal(0);
			BigDecimal reduceAmtBig = new BigDecimal(reduceAmt);
			tableM.acceptText();
			TParm tableParm = tableM.getParmValue();
			for (int i = 0; i < tableParm.getCount(); i++) {
				double ar_amt = tableParm.getDouble("AR_AMT", i);
				double reduceAmts = (ar_amt / arAmt) * reduceAmt;
				BigDecimal sf = new BigDecimal(String.valueOf(reduceAmts));
				BigDecimal data = sf.setScale(2, RoundingMode.HALF_UP);
				if (i == tableParm.getCount() - 1) {
					data = reduceAmtBig.subtract(sum);
				} else {
					sum = sum.add(data);
				}
				tableParm.setData("REDUCE_AMT", i, sfd.format(data
						.doubleValue()));
			}
			tableM.setParmValue(tableParm);
			setReduceAmt(reduceAmt);
		}
		// onReducePrintEable();
	}

	/**
	 * ����ѡ���¼�
	 */
	public void onChange() {
		// �վ�����
		if (this.getTComboBox("TYPE").getSelectedID().equals("1")) {
			tableD
					.setHeader("ѡ,30,boolean;��,30,boolean;�վ����,150,REXP_CODE;�������,80;������,80,double,#########0.00");
			tableD.setLockColumns("1,2,3");
			tableD.setParmMap("FLG;NEW;REXP_CODE;AR_AMT;REDUCE_AMT");
			tableD.setColumnHorizontalAlignmentData("2,left;3,right;4,right");
			tableD.setItem("REXP_CODE");
			this.setValue("ORDER_CODE", "");
			this.setValue("ORDER_DESC", "");
			callFunction("UI|ORDER_CODE|setEnabled", false);
			callFunction("UI|ORDER_DESC|setEnabled", false);
			callFunction("UI|QueryAll|setEnabled", false);
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					this.getRexpTypeSql()));
			if (parm.getErrCode() < 0) {
				this.tableD.removeRowAll();
				this.messageBox("���ò�ѯ����");
				return;
			}
			this.tableD.setParmValue(parm);
			onSumAMT(parm);
		}
		// ������ϸ
		if (this.getTComboBox("TYPE").getSelectedID().equals("2")) {
			tableD
					.setHeader("ѡ,30,boolean;��,30,boolean;��������,150;���,80;��λ,50,UNIT;����,60;����,40;�������,80;������,80,double,#########0.00");
			tableD.setLockColumns("1,2,3,4,5,6,7");
			tableD.setItem("UNIT");
			tableD
					.setParmMap("FLG;NEW;ORDER_DESC;SPECIFICATION;DOSAGE_UNIT;OWN_PRICE;DOSAGE_QTY;AR_AMT;REDUCE_AMT;ORDER_CODE");
			tableD
					.setColumnHorizontalAlignmentData("2,left;3,left;4,left;5,right;6,right;7,right;8,right");
			this.setValue("ORDER_CODE", "");
			this.setValue("ORDER_DESC", "");
			callFunction("UI|ORDER_CODE|setEnabled", true);
			callFunction("UI|ORDER_DESC|setEnabled", true);
			callFunction("UI|QueryAll|setEnabled", true);
			// System.out.println("hhhha;;;;;;;;;"+this.getOrderCodeSql(""));
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					this.getOrderCodeSql("")));
			DecimalFormat sfd = new DecimalFormat("######0.00");
			if (parm.getErrCode() < 0) {
				this.tableD.removeRowAll();
				this.messageBox("���ò�ѯ����");
				return;
			}
			for (int i = 0; i < parm.getCount(); i++) {
				String orderSetflg = parm.getValue("ORDERSET_FLG", i);
				if (orderSetflg.equals("Y")) {
					TParm parmRow = this.getOrderSetAmt(parm.getRow(i));
					parm.setData("OWN_PRICE", i, sfd.format(parmRow
							.getDouble("OWN_PRICE")
							/ parm.getDouble("DOSAGE_QTY", i)));
					parm.setData("AR_AMT", i, parmRow.getDouble("AR_AMT"));
				}
			}
			this.tableD.setParmValue(parm);
			onSumAMT(parm);
		}
	}

	/**
	 * tabled���������
	 * */
	public void onTableReduceAmt(TTableNode cell) {
		tableD.acceptText();
		int col = tableD.getSelectedColumn();
		int row = tableD.getSelectedRow();
		String columnName = tableD.getParmMap(col);
		if (columnName.equals("REDUCE_AMT")) {
			double oldValue = TypeTool.getDouble(cell.getOldValue());
			double reduceAmt = TypeTool.getDouble(cell.getValue());
			if (reduceAmt < 0) {
				this.messageBox("�������С��0");
				cell.setValue(oldValue);
				return;
			}
			// $$--------------- add caoy 2014/6/16 start
			double arAmt = tableD.getParmValue().getDouble("AR_AMT", row);
			// String orderCode=tableD.getParmValue().getValue("ORDER_CODE",
			// row);
			// String caseNO=tableD.getParmValue().getValue("CASE_NO", row);
			// String orderSetFlg=tableD.getParmValue().getValue("ORDERSET_FLG",
			// row);
			if (reduceAmt > arAmt) {
				this.messageBox("������ܴ��ڷ������");
				cell.setValue(oldValue);
				return;
			}
			// boolean
			// flag=this.checkOrderSave(orderCode,caseNO,reduceAmt);//�Ǽ���ҽ��У��
			// ����ҽ��
			// if("Y".equals(orderSetFlg)){
			//				
			// }else{//�Ǽ���ҽ��
			// if(!flag){
			// this.messageBox("������ܴ��ڷ������");
			// cell.setValue(oldValue);
			// return;
			// }
			// }
			// $$--------------- add caoy 2014/6/16 end

		}
	}

	/**
	 * ���
	 * */
	public void onAdd() {
		tableD.acceptText();
		tableDD.acceptText();
		TParm parmd = tableD.getParmValue();
		if (parmd.getCount() < 0) {
			this.messageBox("��ѡ������");
			return;
		}
		int count = 0;
		TParm rowParm = new TParm();
		if (tableDD.getParmValue() != null)
			rowParm = tableDD.getParmValue();
		for (int i = 0; i < parmd.getCount(); i++) {
			if ("Y".equals(parmd.getValue("FLG", i))) {
				TParm errorParm = this.onCheck(parmd.getRow(i), rowParm);
				if (errorParm.getErrCode() < 0) {
					this.messageBox(errorParm.getErrText());
					return;
				}
				count++;
			}
		}
		if (count <= 0) {
			this.messageBox("�޿���ӵ�����");
			return;
		}
		for (int i = 0; i < parmd.getCount(); i++) {
			if ("Y".equals(parmd.getData("FLG", i))) {
				// �վ���Ŀ
				if (this.getTComboBox("TYPE").getSelectedID().equals("1")) {
					rowParm.addData("FLG", "Y");
					rowParm.addData("DATA_DESC", rexpMap.get(parmd.getData(
							"REXP_CODE", i)));
					rowParm.addData("SPECIFICATION", "");
					rowParm.addData("UNIT", "");
					rowParm.addData("OWN_PRICE", "");
					rowParm.addData("QTY", "1");
					rowParm.addData("AR_AMT", parmd.getData("AR_AMT", i));
					rowParm.addData("REDUCE_AMT", parmd
							.getData("REDUCE_AMT", i));
					rowParm.addData("DATA_CODE", parmd.getData("REXP_CODE", i));
					rowParm.addData("DATA_TYPE", parmd.getData("DATA_TYPE", i));
					rowParm.addData("REXP_CODE", parmd.getData("REXP_CODE", i));
					rowParm.addData("NEW", parmd.getData("NEW", i));
					rowParm.addData("CASE_NO", parmd.getData("CASE_NO", i));

				}
				// ��Ŀ��ϸ
				if (this.getTComboBox("TYPE").getSelectedID().equals("2")) {
					rowParm.addData("FLG", "Y");
					rowParm
							.addData("DATA_DESC", parmd
									.getData("ORDER_DESC", i));
					rowParm.addData("SPECIFICATION", parmd.getData(
							"SPECIFICATION", i));
					rowParm.addData("UNIT", parmd.getData("DOSAGE_UNIT", i));
					rowParm.addData("OWN_PRICE", parmd.getData("OWN_PRICE", i));
					rowParm.addData("QTY", parmd.getData("DOSAGE_QTY", i));
					rowParm.addData("AR_AMT", parmd.getData("AR_AMT", i));
					rowParm.addData("REDUCE_AMT", parmd
							.getData("REDUCE_AMT", i));
					rowParm
							.addData("DATA_CODE", parmd
									.getData("ORDER_CODE", i));
					rowParm.addData("DATA_TYPE", parmd.getData("DATA_TYPE", i));
					rowParm.addData("REXP_CODE", parmd.getData("REXP_CODE", i));
					rowParm.addData("NEW", parmd.getData("NEW", i));
					rowParm.addData("CASE_NO", parmd.getData("CASE_NO", i));
				}
			}
		}
		rowParm.setCount(rowParm.getCount("DATA_CODE"));
		tableDD.setParmValue(rowParm);
		// ��������ܽ��
		onSumReduceAMT();
	}

	/**
	 * У������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onCheck(TParm parm, TParm tableParm) {
		this.tableDD.acceptText();
		String dataType = parm.getValue("DATA_TYPE");
		String dataCode = parm.getValue("ORDER_CODE");
		String rexpCode = parm.getValue("REXP_CODE");
		String caseNo = parm.getValue("CASE_NO");
		BigDecimal sum = new BigDecimal(parm.getDouble("REDUCE_AMT"));
		TParm result = new TParm();
		for (int i = 0; i < tableParm.getCount(); i++) {
			TParm parmRow = tableParm.getRow(i);
			if (dataType.equals("2")
					&& dataCode.equals(parmRow.getValue("DATA_CODE"))
					&& caseNo.equals(parmRow.getValue("CASE_NO"))) {// ��ϸ����
				BigDecimal sf = new BigDecimal(String.valueOf(parmRow
						.getDouble("REDUCE_AMT")));
				BigDecimal data = sf.setScale(2, RoundingMode.HALF_UP);
				sum = sum.add(data);
			}
		}
		if (dataType.equals("2")) {
			result = this.getOrderCodeSumAmt(parm, sum.doubleValue());
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		TParm parmValue = tableDD.getParmValue();
		if (parmValue == null) {
			return result;
		}
		if (parmValue.getCount() <= 0)
			return result;
		for (int i = 0; i < parmValue.getCount(); i++) {
			TParm parmRow = parmValue.getRow(i);
			if (dataType.equals("1")) {// ��Ŀ���ⲻ���ظ�
				if (parmRow.getValue("DATA_TYPE").equals(dataType)
						&& parmRow.getValue("DATA_CODE").equals(dataCode)
						&& caseNo.equals(parmRow.getValue("CASE_NO"))) {
					result.setErr(-1, parmRow.getValue("DATA_DESC")
							+ "�Ѽ���,�����²�����");
					return result;
				}
			}
			if ((!parmRow.getValue("DATA_TYPE").equals(dataType))
					&& parmRow.getValue("REXP_CODE").equals(rexpCode)
					&& caseNo.equals(parmRow.getValue("CASE_NO"))) {
				result.setErr(-1, parmRow.getValue("DATA_DESC") + "�Ѽ���,�����²�����");
				return result;
			}
		}
		return result;
	}

	/**
	 * ���������Ŀ���ܽ��
	 * 
	 * @param parm
	 */
	public TParm getOrderCodeSumAmt(TParm parm, double reduceAmt) {
		TParm result = new TParm();
		String caseNo = parm.getValue("CASE_NO");
		if (lumpworkCode.trim().length() > 0) {// �ײ�������ѯ�������
			if (parm.getValue("ORDERSET_FLG").equals("Y")) {
				String sql = "SELECT SUM(A.TOT_AMT) AR_AMT FROM IBS_ORDD A,IBS_BILLM C WHERE "
						+ "A.BILL_NO=C.BILL_NO AND A.INCLUDE_FLG='Y' AND C.BILL_NO IN ("
						+ billNoSum
						+ ") AND C.REFUND_FLG='N' AND C.RECEIPT_NO IS NULL  AND A.CASE_NO='"
						+ caseNo
						+ "' AND A.ORDERSET_CODE='"
						+ parm.getValue("ORDER_CODE")
						+ "' GROUP BY A.ORDERSET_CODE";
				TParm Reparm = new TParm(TJDODBTool.getInstance().select(sql));
				if (Reparm.getDouble("AR_AMT", 0) - reduceAmt < 0) {
					result.setErr(-1, "ҽ��:" + parm.getValue("ORDER_DESC")
							+ "�ܶ�����Ϊ��ֵ,��������д�����");
					return result;
				}
			} else {
				String sql = "SELECT SUM(A.TOT_AMT) AR_AMT FROM IBS_ORDD A,IBS_BILLM C WHERE  "
						+ "A.BILL_NO=C.BILL_NO AND A.INCLUDE_FLG='Y' AND C.BILL_NO IN ("
						+ billNoSum
						+ ") AND C.REFUND_FLG='N' AND C.RECEIPT_NO IS NULL AND A.CASE_NO='"
						+ caseNo
						+ "' AND A.ORDER_CODE='"
						+ parm.getValue("ORDER_CODE")
						+ "' GROUP BY A.ORDER_CODE";
				TParm Reparm = new TParm(TJDODBTool.getInstance().select(sql));
				if (Reparm.getDouble("AR_AMT", 0) - reduceAmt < 0) {
					result.setErr(-1, "ҽ��:" + parm.getValue("ORDER_DESC")
							+ "�ܶ�����Ϊ��ֵ,��������д�����");
					return result;
				}
			}
		} else {
			if (parm.getValue("ORDERSET_FLG").equals("Y")) {
				String sql = "SELECT SUM(A.TOT_AMT) AR_AMT FROM IBS_ORDD A,IBS_BILLM C WHERE "
						+ "A.BILL_NO=C.BILL_NO AND C.BILL_NO IN ("
						+ billNoSum
						+ ") AND C.REFUND_FLG='N' AND C.RECEIPT_NO IS NULL  AND A.CASE_NO='"
						+ caseNo
						+ "' AND A.ORDERSET_CODE='"
						+ parm.getValue("ORDER_CODE")
						+ "' GROUP BY A.ORDERSET_CODE";
				TParm Reparm = new TParm(TJDODBTool.getInstance().select(sql));
				if (Reparm.getDouble("AR_AMT", 0) - reduceAmt < 0) {
					result.setErr(-1, "ҽ��:" + parm.getValue("ORDER_DESC")
							+ "�ܶ�����Ϊ��ֵ,��������д�����");
					return result;
				}
			} else {
				String sql = "SELECT SUM(A.TOT_AMT) AR_AMT FROM IBS_ORDD A,IBS_BILLM C WHERE  "
						+ "A.BILL_NO=C.BILL_NO AND C.BILL_NO IN ("
						+ billNoSum
						+ ") AND C.REFUND_FLG='N' AND C.RECEIPT_NO IS NULL AND A.CASE_NO='"
						+ caseNo
						+ "' AND A.ORDER_CODE='"
						+ parm.getValue("ORDER_CODE")
						+ "' GROUP BY A.ORDER_CODE";
				TParm Reparm = new TParm(TJDODBTool.getInstance().select(sql));
				if (Reparm.getDouble("AR_AMT", 0) - reduceAmt < 0) {
					result.setErr(-1, "ҽ��:" + parm.getValue("ORDER_DESC")
							+ "�ܶ�����Ϊ��ֵ,��������д�����");
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * �Ƴ�
	 * */
	public void onDelete() {
		tableDD.acceptText();
		TParm parmdd = tableDD.getParmValue();
		if (tableDD.getRowCount() <= 0) {
			this.messageBox("û�п��Ƴ�������");
			return;
		}
		boolean flg = false;
		for (int i = 0; i < parmdd.getCount(); i++) {
			if ("Y".equals(parmdd.getValue("FLG", i))) {
				flg = true;
				break;
			}
		}
		if (!flg) {
			this.messageBox("û�п��Ƴ�������");
			return;
		}
		int result = this.messageBox("�Ƴ�ȷ��", "ȷ��Ҫ�Ƴ���", 1);
		// ȷ��ɾ��
		if (result == 0) {
			for (int i = tableDD.getRowCount() - 1; i >= 0; i--) {
				if ("Y".equals(parmdd.getData("FLG", i))) {
					// �Ƴ�
					tableDD.removeRow(i);
				}
			}
		} else {
			return;
		}
		// ��������ܶ�
		onSumReduceAMT();

	}

	/**
	 * �����Ը����
	 * 
	 * @param parm
	 */
	public void onSumAMT(TParm parm) {
		if (parm.getCount() < 0) {
			this.setValue("OWN_AMT", "0");
		}
		BigDecimal sum = new BigDecimal(0);
		for (int i = 0; i < parm.getCount(); i++) {
			BigDecimal sf = new BigDecimal(String.valueOf(parm.getDouble(
					"AR_AMT", i)));
			BigDecimal data = sf.setScale(2, RoundingMode.HALF_UP);
			sum = sum.add(data);
		}
		this.setValue("OWN_AMT", String.valueOf(sum.doubleValue()));
	}

	/**
	 * ���ܼ�����
	 */
	public void onSumReduceAMT() {
		this.tableDD.acceptText();
		TParm parm = tableDD.getParmValue();
		if (null == parm || parm.getCount() <= 0)
			setReduceAmt(0.00);
		BigDecimal sum = new BigDecimal(0);
		if (null != parm) {
			for (int i = 0; i < parm.getCount(); i++) {
				BigDecimal sf = new BigDecimal(String.valueOf(parm.getDouble(
						"REDUCE_AMT", i)));
				BigDecimal data = sf.setScale(2, RoundingMode.HALF_UP);
				sum = sum.add(data);
			}
			setReduceAmt(sum.doubleValue());
		}
		// onReducePrintEable();

	}

	/**
     * 
     */
	// public void onReducePrintEable() {
	// if (this.getValueDouble("REDUCE_AMT") > 0) {
	// callFunction("UI|REDUCE_PRINT|setEnabled", true);
	// } else {
	// callFunction("UI|REDUCE_PRINT|setEnabled", false);
	// }
	// }
	private TParm onCheckCashType(){
		TParm checkCashTypeParm=new TParm();
		for (int i = 0; i < payTypeTParm.getCount("PAY_TYPE"); i++) {
			
			if(payTypeTParm.getValue("PAY_TYPE",i).equals("WX")&&payTypeTParm.getDouble("AMT", i)>0){
				checkCashTypeParm.setData("WX_AMT", payTypeTParm.getDouble("AMT", i));
				checkCashTypeParm.setData("WX_FLG", "Y");
			}
			if(payTypeTParm.getValue("PAY_TYPE",i).equals("ZFB")&&payTypeTParm.getDouble("AMT", i)>0){
				checkCashTypeParm.setData("ZFB_AMT", payTypeTParm.getDouble("AMT", i));
				checkCashTypeParm.setData("ZFB_FLG", "Y");
			}
		}
		return checkCashTypeParm;
	}
	/**
	 * �����ӡ
	 */
	public void onReducePrint() {
		if (this.getValueDouble("REDUCE_AMT") <= 0) {
			this.messageBox("������С�ڻ����0�������Բ�����Ʊ");
			return;
		}
		if (this.getValueDouble("REDUCE_AMT") > this.getValueDouble("OWN_AMT")) {
			this.messageBox("������ܴ��ڷ����ܶ�");
			return;
		}
		
		TParm paymentParm = paymentTool.table.getParmValue();
		double payMentAmt = 0.00;
		double amt = 0.00;
		for (int i = 0; i < paymentParm.getCount(); i++) {
			payMentAmt += paymentParm.getDouble("REDUCE_AMT", i);
			if (paymentParm.getValue("PAY_TYPE", i).equals("LPK")
					&& paymentParm.getDouble("REDUCE_AMT", i) != 0
					|| paymentParm.getValue("PAY_TYPE", i).equals("XJZKQ")
					&& paymentParm.getDouble("REDUCE_AMT", i) != 0) {// ֧����ʽΪ�ֽ���������Ͳ����Բ�������
				this.messageBox("��Ʒ�������ȯ֧����ʽ�����Բ�������");
				return;
			}
			if (paymentParm.getDouble("REDUCE_AMT", i) > paymentParm.getDouble(
					"AMT", i)) {
				this.messageBox("������ܴ���֧�����");
				return;
			}
			amt += paymentParm.getDouble("AMT", i);
		}
		if (this.getValueDouble("REDUCE_AMT") != payMentAmt) {
			this.messageBox("��������֧����ʽ���ۼƼ����ܶ��");
			return;
		}
		if (this.getValueDouble("OWN_AMT") != StringTool.round(amt, 2)) {
			this.messageBox("�����ܶ���֧����ʽ���ۼ�֧���ܶ��");
			return;
		}
		String reduce_no = SystemTool.getInstance().getNo("ALL", "OPB",
				"REDUCE_NO", "REDUCE_NO");
		TParm result = new TParm();
		if(!onReturnCashType(result)){
			return;
		}
		result.setData("REDUCEFLG", "Y");
		result.setData("REDUCE_AMT", this.getValueDouble("REDUCE_AMT"));
		result.setData("REDUCE_NO", reduce_no);
		TParm ReduceMParm = new TParm();
		ReduceMParm.setData("REDUCE_NO", reduce_no);
		ReduceMParm.setData("ADM_TYPE", "I");
		ReduceMParm.setData("CASE_NO", this.getValue("CASE_NO"));
		ReduceMParm.setData("MR_NO", this.getValue("MR_NO"));
		int selectedIndex = tab.getSelectedIndex();
		if (selectedIndex <= 0) {
			ReduceMParm.setData("REDUCE_TYPE", "1");
		}
		if (selectedIndex == 1) {
			ReduceMParm.setData("REDUCE_TYPE", "2");
		}
		ReduceMParm.setData("AR_AMT", this.getValueDouble("OWN_AMT"));
		ReduceMParm.setData("REDUCE_AMT", this.getValueDouble("REDUCE_AMT"));
		ReduceMParm.setData("REDUCE_USER", this.getValueString("REDUCE_USER"));
		ReduceMParm.setData("REDUCE_NOTE", this.getValueString("REDUCE_NOTE"));
		ReduceMParm.setData("REDUCE_FLG", "Y");
		ReduceMParm.setData("RESET_USER", "");
		ReduceMParm.setData("RESET_DATE", "");
		ReduceMParm.setData("OPT_USER", Operator.getID());
		ReduceMParm.setData("OPT_TERM", Operator.getIP());
		ReduceMParm.setData("RESET_REDUCE_NO", "");
		ReduceMParm.setData("BUSINESS_NO", "");
		ReduceMParm.setData("ACCOUNT_FLG", "");
		ReduceMParm.setData("ACCOUNT_SEQ", "");
		ReduceMParm.setData("ACCOUNT_USER", "");
		ReduceMParm.setData("ACCOUNT_DATE", "");
		result.setData("ReduceMParm", ReduceMParm.getData());
		TParm parmForReduceD = new TParm();
		int seq = 1;
		if (selectedIndex <= 0) {
			// �ܶ����
			tableM.acceptText();
			int count = tableM.getRowCount();
			for (int i = 0; i < count; i++) {
				TParm parmRow = tableM.getParmValue().getRow(i);
				parmForReduceD.addData("REDUCE_NO", reduce_no);
				parmForReduceD.addData("SEQ", seq);
				parmForReduceD.addData("REDUCE_TYPE", "1");
				parmForReduceD.addData("DATA_TYPE", "1");
				parmForReduceD.addData("DATA_CODE", parmRow
						.getData("REXP_CODE"));
				parmForReduceD.addData("DATA_NAME", rexpMap.get(parmRow
						.getData("REXP_CODE")));
				parmForReduceD.addData("DESCRIPTION", "");
				parmForReduceD.addData("UNIT_CODE", "");
				parmForReduceD.addData("OWN_PRICE", "");
				parmForReduceD.addData("QTY", "1");
				parmForReduceD.addData("AR_AMT", parmRow.getDouble("AR_AMT"));
				parmForReduceD.addData("REDUCE_AMT", parmRow
						.getDouble("REDUCE_AMT"));
				parmForReduceD.addData("RESET_REDUCE_NO", "");
				parmForReduceD.addData("OPT_USER", Operator.getID());
				parmForReduceD.addData("OPT_TERM", Operator.getIP());
				parmForReduceD.addData("REXP_CODE", parmRow
						.getData("REXP_CODE"));// ====yanjing 20141223 add
				seq++;
			}
		}
		if (selectedIndex == 1) {
			// �������
			tableDD.acceptText();
			int count = tableDD.getRowCount();
			for (int i = 0; i < count; i++) {
				TParm parmRow = tableDD.getParmValue().getRow(i);
				parmForReduceD.addData("REDUCE_NO", reduce_no);
				parmForReduceD.addData("SEQ", seq);
				parmForReduceD.addData("REDUCE_TYPE", "2");
				parmForReduceD.addData("DATA_TYPE", parmRow
						.getData("DATA_TYPE"));
				parmForReduceD.addData("DATA_NAME", parmRow
						.getData("DATA_DESC"));
				parmForReduceD.addData("DESCRIPTION", parmRow
						.getData("SPECIFICATION"));
				parmForReduceD.addData("UNIT_CODE", parmRow.getData("UNIT"));
				parmForReduceD.addData("OWN_PRICE", parmRow
						.getData("OWN_PRICE"));
				parmForReduceD.addData("QTY", parmRow.getDouble("QTY"));
				parmForReduceD.addData("AR_AMT", parmRow.getDouble("AR_AMT"));
				parmForReduceD.addData("REDUCE_AMT", parmRow
						.getDouble("REDUCE_AMT"));
				parmForReduceD.addData("OPT_USER", Operator.getID());
				parmForReduceD.addData("OPT_TERM", Operator.getIP());
				parmForReduceD.addData("DATA_CODE", parmRow
						.getData("DATA_CODE"));
				parmForReduceD.addData("REXP_CODE", parmRow
						.getData("REXP_CODE"));
				parmForReduceD.addData("RESET_REDUCE_NO", "");
				seq++;
			}
		}
		result.setData("ReduceDParm", parmForReduceD.getData());
		result.setData("PAYMENT_PARM", paymentParm.getData());// ����֧����ʽ
		// ���ظ����ý��������
		this.setReturnValue(result);
		this.closeWindow();
	}
	/**
	 * ΢��֧������ӽ��׺���
	 * @param result
	 * @return
	 */
	private boolean onReturnCashType(TParm result){
		//�ֽ��Ʊ������У���Ƿ����֧������΢�Ž��
		TParm checkCashTypeParm=onCheckCashType();
		TParm payCashParm=null;
		if(null!=checkCashTypeParm.getValue("WX_FLG")&&
				checkCashTypeParm.getValue("WX_FLG").equals("Y")||null!=checkCashTypeParm.getValue("ZFB_FLG")&&
				checkCashTypeParm.getValue("ZFB_FLG").equals("Y")){
			Object res = this.openDialog(
    	            "%ROOT%\\config\\bil\\BILPayTypeOnFeeTransactionNo.x", checkCashTypeParm, false);
			if(null==res){
				return false;
			}
			payCashParm=(TParm)res;
		}
		if(null!=payCashParm){
			result.setData("payCashParm",payCashParm.getData());
		}
		return true;
	}
	/**
	 * ��ӡ
	 * */
	public void onPrint() {
		TParm result = new TParm();
		result.setData("REDUCEFLG", "N");
		if(!onReturnCashType(result)){
			return;
		}
		// ���ظ����ý��������
		this.setReturnValue(result);
		this.closeWindow();

	}

	/**
	 * ȡ��
	 * */
	public void onCancel() {
		// switch (messageBox("��ʾ��Ϣ", "ȷ��ȡ��ѡ��,Ĭ�ϴ�ӡ", this.YES_NO_OPTION)) {
		// case 0:
		// TParm result = new TParm();
		// result.setData("REDUCEFLG", "N");
		// this.setReturnValue(result);
		// this.closeWindow();
		// case 1:
		// break;
		// }
		this.setReturnValue(null);
		this.closeWindow();
		return;
	}

	/**
	 * ȫѡ
	 */
	public void onSelAllD() {
		tableD.acceptText();
		TParm parm = tableD.getParmValue();
		if (parm.getCount() <= 0) {
			return;
		}
		// ȫѡ��ѡ
		String flg = this.getValueString("ALLD");
		for (int i = 0; i < parm.getCount(); i++) {
			parm.setData("FLG", i, flg);

		}
		tableD.setParmValue(parm);
	}

	/**
	 * ȫѡ
	 */
	public void onSelAllDD() {
		tableDD.acceptText();
		TParm parm = tableDD.getParmValue();
		if (tableDD.getRowCount() <= 0) {
			return;
		}

		// ȫѡ��ѡ
		String flg = this.getValueString("ALLDD");
		for (int i = 0; i < parm.getCount(); i++) {
			parm.setData("FLG", i, flg);

		}
		tableDD.setParmValue(parm);

	}

	/**
	 * �õ�rexp_code����
	 * 
	 * @return
	 */
	private String getRexpTypeSql() {
		StringBuffer line = new StringBuffer();
		if (lumpworkCode.trim().length() > 0) {// ��ѯ������
			String sql = " SELECT  'N' FLG,'N' NEW,A.REXP_CODE,SUM(A.TOT_AMT) AS AR_AMT,'' REDUCE_AMT,'1' DATA_TYPE,A.REXP_CODE AS ORDER_CODE,A.CASE_NO "
					+ " FROM IBS_ORDD A ,IBS_ORDM B,IBS_BILLM C "
					+ " WHERE A.CASE_NO=B.CASE_NO AND A.CASE_NO_SEQ=B.CASE_NO_SEQ AND A.BILL_NO IS NOT NULL "
					+ " AND A.CASE_NO=C.CASE_NO AND A.BILL_NO=C.BILL_NO AND B.CASE_NO='"
					+ caseNo
					+ "' AND C.REFUND_FLG='N' AND C.BILL_NO IN ("
					+ billNoSum
					+ ") AND C.RECEIPT_NO IS NULL AND A.INCLUDE_FLG='Y' AND A.TOT_AMT<>0 GROUP BY A.REXP_CODE,A.CASE_NO ";
			line.append(sql);
			if (this.newBabyFlg.equals("Y") && newBabyTParm.getCount() > 0) {
				for (int i = 0; i < newBabyTParm.getCount(); i++) {
					line.append(" UNION ");
					String NewcaseNo = newBabyTParm.getValue("CASE_NO", i);
					String NewSql = " SELECT  'N' FLG,'Y' NEW,A.REXP_CODE,SUM(A.TOT_AMT) AS AR_AMT,'' REDUCE_AMT,'1' DATA_TYPE,A.REXP_CODE AS ORDER_CODE,A.CASE_NO "
							+ " FROM IBS_ORDD A ,IBS_ORDM B,IBS_BILLM C "
							+ " WHERE A.CASE_NO=B.CASE_NO AND A.CASE_NO_SEQ=B.CASE_NO_SEQ AND A.BILL_NO IS NOT NULL "
							+ " AND A.CASE_NO=C.CASE_NO AND A.BILL_NO=C.BILL_NO  "
							+ " AND B.CASE_NO='"
							+ NewcaseNo
							+ "' AND C.REFUND_FLG='N' AND C.BILL_NO IN ("
							+ billNoSum
							+ ") AND  C.RECEIPT_NO IS NULL AND A.INCLUDE_FLG='Y' AND A.TOT_AMT<>0 GROUP BY A.REXP_CODE,A.CASE_NO ";

					line.append(NewSql);
				}
			}
		} else {
			String sql = " SELECT  'N' FLG,'N' NEW,A.REXP_CODE,SUM(A.AR_AMT) AS AR_AMT,'' REDUCE_AMT,'1' DATA_TYPE,A.REXP_CODE AS ORDER_CODE,B.CASE_NO "
					+ " FROM IBS_BILLD A ,IBS_BILLM B "
					+ " WHERE A.BILL_NO=B.BILL_NO AND A.BILL_SEQ=B.BILL_SEQ "
					+ " AND B.CASE_NO='"
					+ caseNo
					+ "' AND A.REFUND_FLG='N' AND B.BILL_NO IN ("
					+ billNoSum
					+ ") AND B.RECEIPT_NO IS NULL AND A.AR_AMT<>0 GROUP BY A.REXP_CODE,B.CASE_NO ";
			line.append(sql);
			if (this.newBabyFlg.equals("Y") && newBabyTParm.getCount() > 0) {
				for (int i = 0; i < newBabyTParm.getCount(); i++) {
					line.append(" UNION ");
					String NewcaseNo = newBabyTParm.getValue("CASE_NO", i);
					String NewSql = " SELECT  'N' FLG,'Y' NEW,A.REXP_CODE,SUM(A.AR_AMT) AS AR_AMT,'' REDUCE_AMT,'1' DATA_TYPE,A.REXP_CODE AS ORDER_CODE,B.CASE_NO "
							+ " FROM IBS_BILLD A ,IBS_BILLM B "
							+ " WHERE A.BILL_NO=B.BILL_NO AND A.BILL_SEQ=B.BILL_SEQ "
							+ " AND B.CASE_NO='"
							+ NewcaseNo
							+ "' AND A.REFUND_FLG='N' AND B.BILL_NO IN ("
							+ billNoSum
							+ ") AND  B.RECEIPT_NO IS NULL AND A.AR_AMT<>0 GROUP BY A.REXP_CODE,B.CASE_NO ";

					line.append(NewSql);
				}
			}

		}
		line.append("ORDER BY REXP_CODE");
		return line.toString();
	}

	/**
	 * �õ�orderCode����
	 * 
	 * @return
	 */
	private String getOrderCodeSql(String orderCode) {
		StringBuffer lineStr = new StringBuffer();
		String line = "";
		if (!orderCode.equals("")) {
			line = " AND A.ORDER_CODE='" + orderCode + "'";
		}
		if (lumpworkCode.trim().length() > 0) {// ��ѯ������
			String sql = " SELECT 'N' FLG,'N' NEW,A.ORDER_CODE,B.ORDER_DESC,A.OWN_PRICE,A.DOSAGE_UNIT,B.SPECIFICATION "
					+ ", SUM (DOSAGE_QTY) AS DOSAGE_QTY,SUM (TOT_AMT) AR_AMT,'' REDUCE_AMT,A.REXP_CODE,'2' DATA_TYPE, "
					// + " A.ORDER_NO,"//A.BEGIN_DATE,A.END_DATE," //ע��caoy
					// 2014/6/16//==liling 20140711 ע�� A.ORDER_NO
					+ "B.ORDERSET_FLG,A.CASE_NO "// ,A.CASE_NO_SEQ "//ע��caoy
					// 2014/6/16
					+ " FROM IBS_ORDD A,SYS_FEE B ,IBS_BILLM C WHERE A.CASE_NO='"
					+ caseNo
					+ "'"
					+ line
					+ " AND A.ORDER_CODE=B.ORDER_CODE  AND A.BILL_NO=C.BILL_NO AND C.BILL_NO IN ("
					+ billNoSum
					+ ") AND C.REFUND_FLG='N' AND C.RECEIPT_NO IS NULL AND A.INCLUDE_FLG='Y' AND (A.INDV_FLG='N' OR A.INDV_FLG IS NULL) "
					+ " GROUP BY  A.ORDER_CODE, B.ORDER_DESC, A.OWN_PRICE,A.DOSAGE_UNIT, B.SPECIFICATION, "
					+ "A.REXP_CODE, B.ORDERSET_FLG, A.CASE_NO";
			lineStr.append(sql);
			if (this.newBabyFlg.equals("Y") && newBabyTParm.getCount() > 0) {
				for (int i = 0; i < newBabyTParm.getCount(); i++) {
					lineStr.append(" UNION ");
					String NewcaseNo = newBabyTParm.getValue("CASE_NO", i);
					String Newsql = " SELECT 'N' FLG,'Y' NEW,A.ORDER_CODE,B.ORDER_DESC,A.OWN_PRICE,A.DOSAGE_UNIT,B.SPECIFICATION "
							+ ", SUM (DOSAGE_QTY) AS DOSAGE_QTY,SUM (TOT_AMT) AR_AMT,'' REDUCE_AMT,A.REXP_CODE,'2' DATA_TYPE, "
							// + " A.ORDER_NO, "//==liling 20140711 ע��
							// A.ORDER_NO
							// "A.BEGIN_DATE,A.END_DATE,//ע��caoy 2014/6/16
							+ " B.ORDERSET_FLG,A.CASE_NO"// ,A.CASE_NO_SEQ
							// "//ע��caoy
							// 2014/6/16
							+ " FROM IBS_ORDD A,SYS_FEE B,IBS_BILLM C WHERE A.CASE_NO='"
							+ NewcaseNo
							+ "'"
							+ line
							+ " AND A.ORDER_CODE=B.ORDER_CODE  AND A.BILL_NO=C.BILL_NO AND A.INCLUDE_FLG='Y' AND C.BILL_NO IN ("
							+ billNoSum
							+ ") AND C.REFUND_FLG='N' AND C.RECEIPT_NO IS NULL  AND (A.INDV_FLG='N' OR A.INDV_FLG IS NULL) "
							+ " GROUP BY  A.ORDER_CODE, B.ORDER_DESC, A.OWN_PRICE,A.DOSAGE_UNIT, B.SPECIFICATION, "
							+ "A.REXP_CODE, B.ORDERSET_FLG, A.CASE_NO";
					lineStr.append(Newsql);
				}
			}
		} else {
			String sql = " SELECT 'N' FLG,'N' NEW,A.ORDER_CODE,B.ORDER_DESC,A.OWN_PRICE,A.DOSAGE_UNIT,B.SPECIFICATION "
					+ ", SUM (DOSAGE_QTY) AS DOSAGE_QTY,SUM (TOT_AMT) AR_AMT,'' REDUCE_AMT,A.REXP_CODE,'2' DATA_TYPE, "
					// + " A.ORDER_NO,"//A.BEGIN_DATE,A.END_DATE," //ע��caoy
					// 2014/6/16//==liling 20140711 ע�� A.ORDER_NO
					+ "B.ORDERSET_FLG,A.CASE_NO "// ,A.CASE_NO_SEQ "//ע��caoy
					// 2014/6/16
					+ " FROM IBS_ORDD A,SYS_FEE B ,IBS_BILLM C WHERE A.CASE_NO='"
					+ caseNo
					+ "'"
					+ line
					+ " AND A.ORDER_CODE=B.ORDER_CODE  AND A.BILL_NO=C.BILL_NO AND C.BILL_NO IN ("
					+ billNoSum
					+ ") AND C.REFUND_FLG='N' AND C.RECEIPT_NO IS NULL AND (A.INDV_FLG='N' OR A.INDV_FLG IS NULL) "
					+ " GROUP BY  A.ORDER_CODE, B.ORDER_DESC, A.OWN_PRICE,A.DOSAGE_UNIT, B.SPECIFICATION, "
					+ "A.REXP_CODE, B.ORDERSET_FLG, A.CASE_NO";
			lineStr.append(sql);
			if (this.newBabyFlg.equals("Y") && newBabyTParm.getCount() > 0) {
				for (int i = 0; i < newBabyTParm.getCount(); i++) {
					lineStr.append(" UNION ");
					String NewcaseNo = newBabyTParm.getValue("CASE_NO", i);
					String Newsql = " SELECT 'N' FLG,'Y' NEW,A.ORDER_CODE,B.ORDER_DESC,A.OWN_PRICE,A.DOSAGE_UNIT,B.SPECIFICATION "
							+ ", SUM (DOSAGE_QTY) AS DOSAGE_QTY,SUM (TOT_AMT) AR_AMT,'' REDUCE_AMT,A.REXP_CODE,'2' DATA_TYPE, "
							// + " A.ORDER_NO, "//==liling 20140711 ע��
							// A.ORDER_NO
							// "A.BEGIN_DATE,A.END_DATE,//ע��caoy 2014/6/16
							+ " B.ORDERSET_FLG,A.CASE_NO"// ,A.CASE_NO_SEQ
							// "//ע��caoy
							// 2014/6/16
							+ " FROM IBS_ORDD A,SYS_FEE B,IBS_BILLM C WHERE A.CASE_NO='"
							+ NewcaseNo
							+ "'"
							+ line
							+ " AND A.ORDER_CODE=B.ORDER_CODE  AND A.BILL_NO=C.BILL_NO AND C.BILL_NO IN ("
							+ billNoSum
							+ ") AND C.REFUND_FLG='N' AND C.RECEIPT_NO IS NULL   AND (A.INDV_FLG='N' OR A.INDV_FLG IS NULL) "
							+ " GROUP BY  A.ORDER_CODE, B.ORDER_DESC, A.OWN_PRICE,A.DOSAGE_UNIT, B.SPECIFICATION, "
							+ "A.REXP_CODE, B.ORDERSET_FLG, A.CASE_NO";
					lineStr.append(Newsql);
				}
			}
		}
		lineStr.append(" ORDER BY ORDER_CODE  ");
		// System.out.println("+++5455555+++++++++"+lineStr.toString());

		return lineStr.toString();
	}

	/**
	 * ����ҽ���������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getOrderSetAmt(TParm parm) {
		// String orderNo = parm.getValue("ORDER_NO");
		// String beginDate = parm.getValue("BEGIN_DATE");
		String orderSetCode = parm.getValue("ORDER_CODE");
		// String orderSetGroupNo = parm.getValue("ORDERSET_GROUP_NO");
		String caseNo = parm.getValue("CASE_NO");
		// String caseNoSeq = parm.getValue("CASE_NO_SEQ");
		StringBuffer line = new StringBuffer();
		String sql = " SELECT SUM(A.OWN_PRICE) AS OWN_PRICE ,SUM(A.TOT_AMT) AR_AMT "
				+ " FROM IBS_ORDD A,IBS_BILLM C WHERE  A.BILL_NO=C.BILL_NO AND C.BILL_NO IN ("
				+ billNoSum
				+ ") AND C.REFUND_FLG='N' AND C.RECEIPT_NO IS NULL  AND  A.CASE_NO='"
				+ caseNo
				+ "' "
				// "AND CASE_NO_SEQ='" + caseNoSeq + "'" +//ע��caoy 2014/6/16
				+ " AND A.ORDERSET_CODE='"
				+ orderSetCode
				+ "'"
				+ line.toString();
		TParm reparm = new TParm(TJDODBTool.getInstance().select(sql));
		return reparm.getRow(0);
	}

	/**
	 * �õ��վ�����
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private Map<String, String> getRexpMap() {
		Map<String, String> RexpMap = new HashMap<String, String>();
		String sql = "SELECT  ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < parm.getCount(); i++) {
			String id = parm.getValue("ID", i);
			String chn = parm.getValue("CHN_DESC", i);
			RexpMap.put(id, chn);
		}
		return RexpMap;
	}

	/**
	 * �õ�TTabbedPane
	 * 
	 * @param tag
	 *            String
	 * @return TTabbedPane
	 */
	public TTabbedPane getTTabbedPane(String tag) {
		return (TTabbedPane) this.getComponent(tag);
	}

	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * �õ�TCheckBox
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

	/**
	 * �õ�TComboBox
	 * 
	 * @param tag
	 *            String
	 * @return TComboBox
	 */
	public TComboBox getTComboBox(String tag) {
		return (TComboBox) this.getComponent(tag);
	}

	/**
	 * �õ�TTextField����
	 * 
	 * @param tagName
	 *            String
	 * @return TTextField
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * add caoyУ�������ܴ����ܽ��
	 * 
	 * @param orderCode
	 * @param caseNo
	 * @param arAmt
	 * @return
	 */
	public boolean checkOrderSave(String orderCode, String caseNo, double arAmt) {
		String selQtySql = " SELECT SUM(A.TOT_AMT) AS TOT_AMT,A.ORDER_CODE "
				+ "   FROM IBS_ORDD A,IBS_BILLM C WHERE  A.BILL_NO=C.BILL_NO AND C.BILL_NO IN ("
				+ billNoSum
				+ ") AND C.REFUND_FLG='N' AND C.RECEIPT_NO IS NULL "
				+ "   AND A.CASE_NO = '" + caseNo + "' AND A.ORDER_CODE = '"
				+ orderCode + "'  GROUP BY A.ORDER_CODE ";
		TParm Parm = new TParm(TJDODBTool.getInstance().select(selQtySql));
		double dosageQtyTot = Parm.getDouble("TOT_AMT", 0);
		if (arAmt > dosageQtyTot) {
			return false;
		}
		return true;
	}
}
