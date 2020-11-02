package com.javahis.ui.odo;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jdo.ins.INSTJTool;
import jdo.odo.OpdOrder;
import jdo.opd.ODOTool;
import jdo.pha.PhaBaseTool;
import jdo.sys.Operator;
import jdo.sys.SYSAntibioticTool;
import jdo.sys.SYSCtrlDrugClassTool;
import jdo.sys.SYSSQL;
 
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * 
 * Title: ����ҽ������վ����ǩ�ӿ���ҩ����ǩʵ����
 * </p>
 * 
 * <p>
 * Description:����ҽ������վ����ǩ�ӿ���ҩ����ǩʵ����
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODORxMed implements IODORx{
	
	OdoMainControl odoMainControl;
	ODOMainOpdOrder odoMainOpdOrder;
	ODOMainPat odoMainPat;
	
	public TTable table;
	public static final String TABLE_MED = "TABLEMED"; // ����ҩ��
	public final static String MED_RX = "MED_RX";
	public static final String MED = "1";
	public static final int MED_INT = 1;
	public static final int TABBEDPANE_INDEX = 2;
	public static final String ORDERCAT1TYPE = "PHA_W";
	public static final String AMT_TAG = "MED_AMT";
	
	public static final String URLSYSFEEPOPUP = "%ROOT%\\config\\sys\\SYSFeePopup.x";
	public static final String URLPHAREDRUGMSG = "%ROOT%\\config\\pha\\PHAREDrugMsg.x";
	public static final String URLODOMAINDELETERX = "%ROOT%\\config\\odo\\ODOMainDeleteRx.x";
	
	private static final String NULLSTR = "";
	
	private Map<String, String> routeDoseMap;
	
	/**
	 * ��ʼ����ҩ
	 */
	public void initMed() throws Exception{
		this.odoMainPat = odoMainControl.odoMainPat;  //add by huangtt 20141120
		boolean isInit = odoMainOpdOrder.isTableInit(TABLE_MED);
		String rxNo = NULLSTR;
		if (!isInit) {
			rxNo = odoMainOpdOrder.initRx(MED_RX, MED);
			odoMainOpdOrder.setTableInit(TABLE_MED, true);
		} else {
			rxNo = odoMainControl.getValueString(MED_RX);
		}
		if (!odoMainOpdOrder.initNoSetTable(rxNo, TABLE_MED, isInit))
			odoMainControl.messageBox("E0028"); // ��ʼ������ҩʧ��
		odoMainOpdOrder.onChangeRx(MED);
		routeDoseMap = ODOTool.getInstance().getRouteDoseMap();
	}

	@Override
	public void init() throws Exception{
		// TODO Auto-generated method stub
		initMed();
	}
	
	/**
	 * ���SYS_FEE��������
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onMedCreateEditComponent(Component com, int row, int column) throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(TABLE_MED);
		// �����ǰ�к�
		column = table.getColumnModel().getColumnIndex(column);
		String columnName = table.getParmMap(column);
		// ============xueyf modify 20120309 
		if (!("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName) || "ORDER_ENG_DESC"
				.equalsIgnoreCase(columnName))) {
			return;
		}
		// ����ҽ�����ɸ���
		int selRow = table.getSelectedRow();
		TParm existParm = table.getDataStore().getRowParm(selRow);
		if (odoMainOpdOrder.isOrderSet(existParm)) {
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			return;
		}
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		odoMainOpdOrder.tableName = TABLE_MED;
		odoMainOpdOrder.rxName = MED_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 1);
		parm.setData("ORDER_DEPT_CODE", odoMainOpdOrder.orderDeptCode);
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ORDER", odoMainControl.getConfigParm().newConfig(
				URLSYSFEEPOPUP), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popOrderReturn");
	}

	/**
	 * ����ҩTABLE���ҩƷ���Ƶ���������ҩ��ѯ����
	 */
	public void onQueryRsDrug() throws Exception{
		odoMainControl.messageBox("onQueryRsDrug�޷���");
	}
	
	/**
	 * ��ҩֵ�ı��¼������Ǯ
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 * @throws Exception 
	 */
	public boolean onMedValueChange(TTableNode tNode) throws Exception {
		TTable table = (TTable) odoMainControl.getComponent(TABLE_MED);
		int column = tNode.getColumn();
		int row = table.getSelectedRow();
		String columnName = table.getParmMap(column);
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		//�������޸�
		TParm spcReturn=null;
		boolean flg=true;//�ܿ�������ɾ�����޸�ҽ������
		if (Operator.getSpcFlg().equals("Y")) {
			TParm spcParm = new TParm();
			spcParm.setData("CASE_NO", odoMainControl.caseNo);
			String rxNo = order.getRowParm(row).getValue("RX_NO");
			String seqNo = order.getRowParm(row).getValue("SEQ_NO");
			spcParm.setData("RX_NO",rxNo);
			spcParm.setData("SEQ_NO", seqNo);
			spcReturn = TIOM_AppServer.executeAction(//��ô�ҽ����ҩƷ״̬
					"action.opb.OPBSPCAction", "getPhaStateReturn", spcParm);
			if (!this.checkDrugCanUpdate(order, "MED", row, flg,spcReturn)) { // �ж��Ƿ�����޸ģ���û�н�����,��,����
				if(spcReturn.getValue("PhaRetnCode").length()>0)
					odoMainControl.messageBox("�Ѿ���ҩ,��ɾ������ǩ����");//===pangben 2013-7-17 �޸���������ʾ��Ϣ
				else
					odoMainControl.messageBox("E0189");
				return true;
			}
		}
		//�������޸�
		if (checkPha(order, row, "MED",flg))
			return true;
		if (!odoMainOpdOrder.deleteOrder(order, row, ODOMainOpdOrder.MEGBILLED, ODOMainOpdOrder.MEGBILLED2)) {// pangben ====2012
			// 2-28//pangben2012 2-28
			return true;
		}
		if ("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName)) {
			tNode.setValue(NULLSTR);
			return true;
		}
		if ("ROUTE_CODE".equalsIgnoreCase(columnName)) {
			//Ƥ��ҩƷ���÷���Ƥ���÷�ʱ��������ź�Ƥ�Խ��,yanjing,20140404
			String orderCode = order.getRowParm(row).getValue("ORDER_CODE");
			String psSql = "SELECT SKINTEST_FLG FROM PHA_BASE WHERE ORDER_CODE = '"+orderCode+"'";
			TParm psResult = new TParm(TJDODBTool.getInstance().select(psSql));
			if(psResult.getValue("SKINTEST_FLG",0).equals("Y")){//Ƥ��ҩƷ
				if(tNode.getValue().equals("PS")){//�÷�ΪƤ���÷���������ź�Ƥ�Խ��
					order.setItem(row, "BATCH_NO", "");
					order.setItem(row, "SKINTEST_FLG", "");
				}else{
					//��ѯ���ź�Ƥ�Խ��
					String psResultSql = "SELECT BATCH_NO,SKINTEST_FLG FROM OPD_ORDER WHERE CASE_NO = '"+odoMainControl.caseNo+"' " +
							"AND ORDER_CODE = '"+orderCode+"' AND ROUTE_CODE = 'PS' AND BATCH_NO IS NOT NULL " +
									"AND SKINTEST_FLG IS NOT NULL ORDER BY ORDER_DATE DESC";
					TParm psResultParm = new TParm(TJDODBTool.getInstance().select(psResultSql));
					String batchNo = psResultParm.getValue("BATCH_NO", 0);
					String skinTestFlg = psResultParm.getValue("SKINTEST_FLG", 0);
					order.setItem(row, "BATCH_NO", batchNo);
					order.setItem(row, "SKINTEST_FLG",skinTestFlg);
				}
				
			}
		}
		//========pangben 2012-7-18 ����ҩƷ�Ѿ��շѲ������޸�
		if ("RELEASE_FLG".equalsIgnoreCase(columnName)) {
			TParm parm=order.getRowParm(row);
			if (odoMainControl.odo.getOpdOrder().getOrderCodeIsBillFlg(parm)) {
				odoMainControl.messageBox("���շ�,�������޸ı�ҩ����");
				return true;	
			}
		}
		order.getRowParm(row);
		String orderCode = order.getItemString(row, "ORDER_CODE");
		double mediQty = TypeTool.getDouble(tNode.getValue());
		final int inRow = row;
//		final String execDept = odoMainControl.getValueString("MED_RBORDER_DEPT_CODE");
		final String execDept = order.getItemString(row, "EXEC_DEPT_CODE");
		final String orderCodeFinal = order.getItemString(row, "ORDER_CODE");
		final String columnNameFinal = columnName;
		// �շ� ��
		if ("TAKE_DAYS".equalsIgnoreCase(columnName)) {
//			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
//				return true;
//			}
			int day = SYSAntibioticTool.getInstance().getAntibioticTakeDays(
					orderCode);
			if (day > 0 && TypeTool.getInt(tNode.getValue()) > day) {
				if (odoMainControl.messageBox(
						"��ʾ��Ϣ/Tip",
						"��������������������,�Ƿ��������?\r\nThe days you ordered is more than the standard days of antiVirus.Do you proceed anyway?",
						odoMainControl.YES_NO_OPTION) == 1) {
					table.setDSValue(row);
					return true;
				}
			}
			
			setOnMedValueChange(row, execDept, orderCodeFinal, columnNameFinal, order.getItemDouble(row, "DOSAGE_QTY"));
		} else if ("EXEC_DEPT_CODE".equalsIgnoreCase(columnName)) { // ִ�п��� ��
			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
				return true;
			}
			order.itemNow=false;
			final String execDeptFinal = TypeTool.getString(tNode.getValue());
			setOnMedValueChange(inRow, execDeptFinal, orderCodeFinal, columnNameFinal, order.getItemDouble(row, "DOSAGE_QTY"));
		} else if ("MEDI_QTY".equalsIgnoreCase(columnName)) { // ����
//			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
//				return true;
//			}
			if (SYSCtrlDrugClassTool.getInstance().getOrderCtrFlg(orderCode)) {
				if (!SYSCtrlDrugClassTool.getInstance().getCtrOrderMaxDosage(
						orderCode, mediQty)) {
					if (odoMainControl.messageBox(
							"��ʾ��Ϣ/Tip",
							"��������ҩƷĬ������,�Ƿ��������?\r\nQty of this order is over-gived.Do you proceed anyway?",
							odoMainControl.YES_NO_OPTION) == 1) {
						table.setDSValue(row);
						return true;
					}
				}
			}
			order.itemNow=false;
			setOnMedValueChange(row, execDept, orderCodeFinal, columnNameFinal, order.getItemDouble(row, "DOSAGE_QTY"));
		} else if ("FREQ_CODE".equalsIgnoreCase(columnName)) { // Ƶ��
//			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
//				return true;
//			}
			if (!"Y".equals(order.getItemString(row, "LINKMAIN_FLG")) && order.getItemString(row, "LINK_NO").length() > 0) {
				tNode.setValue(tNode.getOldValue());
			}
			setOnMedValueChange(row, execDept, orderCodeFinal, columnNameFinal, order.getItemDouble(row, "DOSAGE_QTY"));
		} else if ("LINKMAIN_FLG".equalsIgnoreCase(columnName)) { // ����ҽ��
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
			
			String freqCode = order.getItemString(tNode.getRow(), "FREQ_CODE");
			String routeCode = order.getItemString(tNode.getRow(), "ROUTE_CODE");
			if ("Y".equals(tNode.getValue() + NULLSTR)) {
				String linkNO = order.getItemString(tNode.getRow(), "LINK_NO"); // �����
				String rxNo = order.getItemString(tNode.getRow(), "RX_NO"); // ����ǩ��
				// ѭ����������
				for (int i = tNode.getRow(); i < order.rowCount(); i++) {
					if (linkNO.equals(order.getItemString(i, "LINK_NO"))
							&& rxNo.equals(order.getItemString(i, "RX_NO"))
							&& !"".equals(order.getItemString(i, "ORDER_CODE"))) {
						order.setItem(i, "FREQ_CODE", freqCode);
						order.setItem(i, "ROUTE_CODE", routeCode);
					}
				}
				table.setDSValue();
			}
			
		} else if ("LINK_NO".equalsIgnoreCase(columnName)) { // �����
			int value = TypeTool.getInt(tNode.getValue());
			String link_main_flg = order.getItemString(row, "LINKMAIN_FLG");
			// if(oldValue==0&&value>0){
			// return true;
			// }
			if ("0".equals(tNode.getValue().toString())) {
				return true;
			}
			if ("Y".equals(link_main_flg) || value < 0) {
				return true;
			}

			if (!StringUtil
					.isNullString(order.getItemString(row, "ORDER_CODE"))) {
				int linkNo = order.getMaxLinkNo();
				if (value >= 0 && value <= linkNo) {
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		} else if ("ROUTE_CODE".equalsIgnoreCase(columnName)) { // �÷�
//			if (StringUtil.isNullString(orderCode)) {
//				return true;
//			}
			String routeCode = tNode.getValue() + NULLSTR;
			
			int result = order.isMedRoute(routeCode);
			if (result == -1) {
				odoMainControl.messageBox("E0019"); // ȡ���÷�ʧ��
				return true;
			} else if (result != 1) {
				odoMainControl.messageBox("E0020"); // ����ҩ����ʹ�ø��÷�
				return true;
			}
			// ����ҽ����������������
			if ("Y".equals(order.getItemString(tNode.getRow(), "LINKMAIN_FLG"))) {
				String linkNO = order.getItemString(tNode.getRow(), "LINK_NO"); // �����
				String rxNo = order.getItemString(tNode.getRow(), "RX_NO"); // ����ǩ��
				// ѭ����������
				for (int i = 0; i < order.rowCount(); i++) {
					if (linkNO.equals(order.getItemString(i, "LINK_NO"))
							&& rxNo.equals(order.getItemString(i, "RX_NO"))
							&& !NULLSTR.equals(order.getItemString(i, "ORDER_CODE"))
							&& !routeCode.equals(order.getItemString(i,
									"ROUTE_CODE"))) {
						order.setItem(i, "ROUTE_CODE", routeCode);
					}
				}
				table.setDSValue();
			}else{
				//��ɾ��
//				if(order.getItemString(tNode.getRow(), "LINK_NO").length() > 0){
//					tNode.setValue(tNode.getOldValue());
//				}
			}
			return false;
		} else {
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
		}
		odoMainOpdOrder.calculateCash(TABLE_MED, AMT_TAG);
		return false;
	}

	/**
	 * ���ҩƷ���ͷ��� �Ƿ���Կ���ͬһ����ǩ��
	 * 
	 * @param order
	 *            OpdOrder
	 * @param DOSE_TYPE
	 *            String
	 * @return boolean
	 */
	public boolean checkDOSE_TYPE(OpdOrder order, String DOSE_TYPE) throws Exception{
		boolean flg = true;
		// ORDER_CODEΪ�ձ�ʾ��һ���ǿ�ҽ�� ���´���ǩ�Ŀ�ʼ ���Է���true
		if (order.getItemString(0, "ORDER_CODE").length() <= 0) {
			return flg;
		}
		// ============pangben 2012-2-29 ��ӹܿ�
		int count = order.rowCount();
		if (count <= 0) {
			return false;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!odoMainOpdOrder.deleteOrder(order, i, ODOMainOpdOrder.MEGBILLED, ODOMainOpdOrder.MEGBILLED2)) {
				return false;
			}
		}
		// ============pangben 2012-2-29 stop
		// �ڷ�����ǩ
		if (order.getItemString(0, "DOSE_TYPE").equalsIgnoreCase("O")) {
			if ("I".equalsIgnoreCase(DOSE_TYPE)
					|| "F".equalsIgnoreCase(DOSE_TYPE)) {
				odoMainControl.messageBox("E0194"); // ��ͬ���ͷ����ҩƷ���ɿ�����ͬһ�Ŵ���ǩ��
				flg = false;
			}
			if ("E".equalsIgnoreCase(DOSE_TYPE)) {
				odoMainControl.messageBox("E0194"); // ��ͬ���ͷ����ҩƷ���ɿ�����ͬһ�Ŵ���ǩ��
				flg = false;
			}
		}
		// �������δ���ǩ
		if (order.getItemString(0, "DOSE_TYPE").equalsIgnoreCase("I")
				|| order.getItemString(0, "DOSE_TYPE").equalsIgnoreCase("F")) {
			if ("E".equalsIgnoreCase(DOSE_TYPE)) {
				odoMainControl.messageBox("E0194"); // ��ͬ���ͷ����ҩƷ���ɿ�����ͬһ�Ŵ���ǩ��
				flg = false;
			}
			if ("O".equalsIgnoreCase(DOSE_TYPE)) {
				odoMainControl.messageBox("E0194"); // ��ͬ���ͷ����ҩƷ���ɿ�����ͬһ�Ŵ���ǩ��
				flg = false;
			}
		}
		// ����ҩ����ǩ
		if (order.getItemString(0, "DOSE_TYPE").equalsIgnoreCase("E")) {
			if ("I".equalsIgnoreCase(DOSE_TYPE)
					|| "F".equalsIgnoreCase(DOSE_TYPE)) {
				odoMainControl.messageBox("E0194"); // ��ͬ���ͷ����ҩƷ���ɿ�����ͬһ�Ŵ���ǩ��
				flg = false;
			}
			if ("O".equalsIgnoreCase(DOSE_TYPE)) {
				odoMainControl.messageBox("E0194"); // ��ͬ���ͷ����ҩƷ���ɿ�����ͬһ�Ŵ���ǩ��
				flg = false;
			}
		}
		return flg;
	}
	
	/**
	 * �ж��Ƿ��˿��
	 * 
	 * @param orderCode
	 *            String
	 * @return boolean
	 */
	private boolean isCheckKC(String orderCode) throws Exception{
		String sql = SYSSQL.getSYSFee(orderCode);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getBoolean("IS_REMARK", 0)) // �����ҩƷ��ע��ô�Ͳ���˿��
			return false;
		else
			// ����ҩƷ��ע�� Ҫ��˿��
			return true;
	}
	
	/**
	 * У���������ǩ��ȫ��ҩƷ�Ƿ��Ѿ���ҩ
	 * 
	 * @param order
	 * @param row
	 * @return
	 */
	public boolean checkPha(OpdOrder order, int row, String name,
			boolean spcFlg) throws Exception{
//		if (null == odoMainControl.odoMainEkt.ektReadParm || odoMainControl.odoMainEkt.ektReadParm.getValue("MR_NO").length() <= 0) {
//		} else {
//			if (order.getItemData(row, "BILL_FLG").equals("Y")) {
		for (int i = 0; i < order.rowCount(); i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_CODE"))) {
				continue;
			}
			if (order.getItemData(row, "RX_NO").equals(
					order.getItemData(i, "RX_NO")) && order.getItemData(row, "PRESRT_NO").equals(
							order.getItemData(i, "PRESRT_NO"))) {
				// �������޸�
				if (Operator.getSpcFlg().equals("Y")) {// ====pangben 2013-4-17
														// У��������ע��
					if (!this.checkDrugCanUpdate(order, name, i, spcFlg, null)) { // �ж��Ƿ�����޸ģ���û�н�����,��,����
						odoMainControl.messageBox("�˴���ǩ��ҩƷ����˻�ҩ,����ɾ�����޸�ҽ��!");
						return true;
					}
					// �������޸�
				} else {
					TParm parm = new TParm();
					if (!order.checkDrugCanUpdate(name, i, parm, true)) { // �ж��Ƿ�����޸ģ���û�н�����,��,����
						if (null != parm.getValue("MESSAGE_FLG")
								&& parm.getValue("MESSAGE_FLG").equals("Y")) {
							if (parm.getValue("MESSAGE_INDEX").equals("Y")) {
								odoMainControl.messageBox(parm.getValue("MESSAGE"));
							}else{
								odoMainControl.messageBox("�˴���ǩ��ҩƷ�ѼƷ�,�������޸Ļ�ɾ������!");
							}
						} else {
							odoMainControl.messageBox("�˴���ǩ��ҩƷ����˻�ҩ,����ɾ�����޸�ҽ��!");
						}
						return true;
					}
				}
			}
		}
//			}
//		}
		return false;
	}

	/**
	 * ���ҩƷ�Ƿ��Ѿ����䷢ �Ƿ������ҩ
	 * 
	 * @param type
	 *            String "EXA":��ҩ "CHN":��ҩ
	 * @param row
	 *            int
	 *            flg =true���鵱ǰ�������޸�ҽ������ɾ��ҽ�����޸�ҽ��������ҩ�������޸ĵ��ǿ���ɾ��
	 * @return boolean
	 */
	public boolean checkDrugCanUpdate(OpdOrder order, String type, int row,
			boolean flg,TParm spcParm) throws Exception{
		return odoMainControl.odoMainSpc.checkDrugCanUpdate(order, type, row, flg, spcParm);
	}
	
	/**
	 * �ж��Ƿ��ǹ���ʷ�е�ҩƷ
	 * 
	 * @param orderCode
	 *            String
	 * @return boolean
	 */
	public boolean isAllergy(String orderCode) throws Exception{
		for (int i = 0; i < odoMainControl.odo.getDrugAllergy().rowCount(); i++) {
			if (orderCode.equalsIgnoreCase(odoMainControl.odo.getDrugAllergy().getItemString(
					i, "DRUGORINGRD_CODE"))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ����������ҩ
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popOrderReturn(String tag, Object obj) throws Exception{
		TParm parm = (TParm) obj;
		// ============xueyf modify 20120331
		if (parm.getValue("CAT1_TYPE") != null
				&& !parm.getValue("CAT1_TYPE").equals("PHA")) {
			odoMainControl.messageBox("��������������ҩҽ����");
			return;
		}
		// add by lx 2012/05/07 �ż�������У��
		// ����
		if (ODOMainReg.O.equalsIgnoreCase(odoMainControl.odoMainReg.admType)) {
			// �ж��Ƿ�סԺ����ҽ��
			if (!("Y".equals(parm.getValue("OPD_FIT_FLG")))) {
				// ������������ҽ����
				odoMainControl.messageBox("������������ҽ����");
				return;
			}
		}
		// ����
		if (ODOMainReg.E.equalsIgnoreCase(odoMainControl.odoMainReg.admType)) {
			if (!("Y".equals(parm.getValue("EMG_FIT_FLG")))) {
				// ������������ҽ����
				odoMainControl.messageBox("���Ǽ�������ҽ����");
				return;
			}
		}
		// $$===========add by lx 2012/05/07 �ż�������У��
		String rxNo;
		TTabbedPane tTabbedPane = (TTabbedPane) odoMainControl.getComponent(ODOMainOpdOrder.TAGTTABPANELORDER);
		int index = tTabbedPane.getSelectedIndex();
		if (!TABLE_MED.equalsIgnoreCase(odoMainOpdOrder.tableName)
				|| !ODORxCtrl.TABLE_CTRL
						.equalsIgnoreCase(odoMainOpdOrder.tableName)
				|| !ODORxChnMed.TABLE_CHN
						.equalsIgnoreCase(odoMainOpdOrder.tableName)
				|| !ODORxExa.TABLE_EXA
						.equalsIgnoreCase(odoMainOpdOrder.tableName)
				|| !ODORxOp.TABLE_OP
						.equalsIgnoreCase(odoMainOpdOrder.tableName)) {
			switch (index) {
			case TABBEDPANE_INDEX:
				odoMainOpdOrder.tableName = TABLE_MED;
				break;
			case ODORxCtrl.TABBEDPANE_INDEX:
				odoMainOpdOrder.tableName = ODORxCtrl.TABLE_CTRL;
				break;
			default:
				odoMainOpdOrder.tableName = ODORxOp.TABLE_OP;
				break;
			}
		}
		TTable table = (TTable) odoMainControl.getComponent(odoMainOpdOrder.tableName);
		table.acceptText();
		int column = table.getSelectedColumn();
		table.acceptText();
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		// ===========pangben 2012-6-15 start ע�� ȡ��5��ҽ����ʾ��Ϣ
		int row = order.rowCount() - 1;
		int oldRow = row;
		String amtTag;
		if (TABLE_MED.equalsIgnoreCase(odoMainOpdOrder.tableName)) {
			odoMainOpdOrder.rxType = MED;
			amtTag = AMT_TAG;
		} else if (ODORxCtrl.TABLE_CTRL.equalsIgnoreCase(odoMainOpdOrder.tableName)) {
			odoMainOpdOrder.rxType = ODORxCtrl.CTRL;
			amtTag = ODORxCtrl.AMT_TAG;
		} else {
			odoMainOpdOrder.rxType = ODORxOp.OP;
			amtTag = ODORxOp.AMT_TAG;
		}
		if (StringUtil.isNullString(odoMainOpdOrder.tableName)) {
			odoMainControl.messageBox("E0034"); // ȡ�����ݴ���
			return;
		}
		// �ж��Ƿ��Ѿ���������ʱ��
		if (!odoMainControl.odoMainReg.canEdit()) {
			table.setDSValue(row);
			odoMainControl.messageBox_("�ѳ�������ʱ�䲻���޸�");
			return;
		}
		if (!StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
			odoMainControl.messageBox("E0040"); // ������������ݣ�������������ɾ��������
			table.setDSValue(row);
			return;
		}
		if (!OdoUtil.isHavingLiciense(parm.getValue("ORDER_CODE"), Operator
				.getID())) {
			odoMainControl.messageBox("E0051"); // û��֤��Ȩ��
			table.setValueAt(NULLSTR, row, column);
			return;
		}
		//=======================yanjing �ż���Ƥ�������Ƿ��޸�У��
		TParm opdResult = new TParm();
		opdResult = ismodifyBatchNo(odoMainControl.caseNo,parm);
		if(opdResult.getValue("SKINTEST_FLG", 0).equals("1")){
			 String batchNo = opdResult.getValue("BATCH_NO",0);
			 String order_desc = parm.getValue("ORDER_DESC");
			  if(odoMainControl.messageBox("��ʾ��Ϣ/Tip",
						order_desc+ "������Ϊ:"+batchNo+"��Ƥ�Խ��Ϊ:(+)����,��������?",0) != 0){
				  return;
			  }
	} 
		
		//====================yanjing �ż���Ƥ�������Ƿ��޸�У��
		if (order.isSameOrder(parm.getValue("ORDER_CODE"))) {
			if (odoMainControl.messageBox("��ʾ��Ϣ/Tip",
					"��ҽ���Ѿ��������Ƿ������\r\nThis order exist,Do you give it again?",
					0) == 1) {
				table.setValueAt(NULLSTR, oldRow, column);
				return;
			}
		}
		if (!isAllergy(parm.getValue("ORDER_CODE"))) {
			if (odoMainControl.messageBox(
							"��ʾ��Ϣ/Tip",
							"���˶Ը�ҩƷ��ɷֹ������Ƿ����������\r\nThe Pat is allergic to this order.Do you proceed anyway?",
							0) == 1) {
				table.setDSValue(row);
				return;
			}
		}
		//���ҽ������У��
		//yanjing ���ҽ���Է�У�� 20130719
		TParm insCheckParm=INSTJTool.getInstance().orderCheck(parm.getValue("ORDER_CODE"), odoMainControl.odoMainReg.reg.getCtz1Code(), odoMainControl.odoMainReg.admType, odoMainControl.odoMainReg.reg.getInsPatType());
		if (insCheckParm.getErrCode() < 0&&!(insCheckParm.getErrCode()==-6) &&!(insCheckParm.getErrCode()==-7)) {
			if(odoMainControl.messageBox("��ʾ","��ҩƷ" + insCheckParm.getErrText()+",�Ƿ����",0)== 1){
				table.setValueAt(NULLSTR, row, column);
				return;
			}
			}else if(insCheckParm.getErrCode()==-6||insCheckParm.getErrCode()==-7){//-6:ҽ���Էѱ�� -7��ҽ���Է��������
				odoMainControl.messageBox("��ҩƷΪ"+insCheckParm.getErrText());		
		}
//		if (!ODORxOp.OP.equalsIgnoreCase(odoMainOpdOrder.rxType)
//				&& !order.isDrug(parm.getValue("ORDER_CODE"), odoMainOpdOrder.rxType)) {
		if (!order.isDrug(parm.getValue("ORDER_CODE"), odoMainOpdOrder.rxType)) {
			odoMainControl.messageBox("E0113");
			return;
		}
		if (ODORxCtrl.CTRL.equalsIgnoreCase(order.getItemString(row, "RX_TYPE"))) {
			if (!order.isSameDrug(parm.getValue("ORDER_CODE"), row)) {
				odoMainControl.messageBox("E0114");
				return;
			}
		}
		// �ж�Ҫ���Ƿ��ܿ����ô���ǩ�ϣ��Ƿ���ר�ô���ҩƷ��
		int re = order.isPrnRx(order.getItemString(row, "RX_NO"), parm
				.getValue("ORDER_CODE"));
		if (re == 1) {
			odoMainControl.messageBox("E0190");
			return;
		} else if (re == 2) {
			odoMainControl.messageBox("E0191");
			return;
		} else if (re == 3) {
			odoMainControl.messageBox("E0192");
			return;
		}
		rxNo = (String) odoMainControl.getValue(odoMainOpdOrder.rxName);
		TParm parmBase = PhaBaseTool.getInstance().selectByOrderRoute(
				parm.getValue("ORDER_CODE")); //modify by huangtt 20141104 
		if (parmBase.getErrCode() < 0) {
			odoMainControl.messageBox("E0034");
			return;
		}
//		if (!checkDOSE_TYPE(order, parmBase.getValue("DOSE_TYPE", 0))) {
//			return;
//		}
		String execDept = parm.getValue("EXEC_DEPT_CODE");
		int tabbedIndex = ((TTabbedPane) odoMainControl.getComponent("TTABPANELORDER"))
				.getSelectedIndex();
		switch (tabbedIndex) {
		case 1:
			if (StringUtil.isNullString(execDept)) {
				execDept = odoMainControl.getValueString("OP_EXEC_DEPT");
			}
			break;
		case 2:
			execDept = odoMainControl.getValueString("MED_RBORDER_DEPT_CODE");
			break;
		case 3:
			execDept = odoMainControl.getValueString("CHN_EXEC_DEPT_CODE");
			break;
		case 4:
			execDept = odoMainControl.getValueString("CTRL_RBORDER_DEPT_CODE");
			break;
		}
		if ("PHA".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))) {
			if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 У��������ע��
				if (checkPha(order, row, "MED",true))//����ҽ��У�鴦��ǩ�Ƿ���Կ���
					return ;
//				if (isCheckKC(parm.getValue("ORDER_CODE"))) // �ж��Ƿ��ǡ�ҩƷ��ע��
					// ������
//					if (!INDTool.getInstance().inspectIndStock(execDept,
//							parm.getValue("ORDER_CODE"), 0.0)) {
//						// $$==========add by lx 2012-06-19�����ⲻ�㣬���ҩ��ʾ
//						TParm inParm = new TParm();
//						inParm
//								.setData("orderCode", parm
//										.getValue("ORDER_CODE"));
//						odoMainControl.openDialog(URLPHAREDRUGMSG,
//								inParm);
//						// $$==========add by lx 2012-06-19�����ⲻ�㣬���ҩ��ʾ
//						order.setActive(row, false);
//						return;
//					}
			}else{
				if (!odoMainControl.odoMainSpc.checkSpcPha(order)) {
					return;
				}
			}
		}
		order.setActive(row, true);
		parm.setData("EXEC_DEPT_CODE", execDept);
		// ���ݸ���������ʼ��һ��order
//		System.out.println("parmBase is :"+parmBase);
		odoMainOpdOrder.initOrder(order, row, parm, parmBase);
		if ("TRT".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))
				|| "PLN".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))) {
			order.setItem(row, "FREQ_CODE", "STAT");
		}
		boolean flgInf = false;
		String inf = "";
		int num = 0;
		if (!odoMainControl.odo.getOpdOrder().isNullOrder(odoMainOpdOrder.rxType, rxNo)) {
			int newRow = order.newOrder(odoMainOpdOrder.rxType, rxNo);
			if (order.getItemInt(row, "LINK_NO") > 0) {
				// //����ҽ�� �鿴�Ƿ��� ��ҽ����ע�� ����� �򲻼�������
				order.itemNow = true;
				order.setItem(newRow, "LINK_NO", order.getItemInt(row,
						"LINK_NO"));
				TParm linkMainParm = order.getLinkMainParm(order.getItemInt(
						row, "LINK_NO"));
				order.itemNow = false;
				order.setItem(row, "TAKE_DAYS", linkMainParm
						.getData("TAKE_DAYS"));
				order.itemNow = false;
				order.setItem(row, "FREQ_CODE", linkMainParm
						.getData("FREQ_CODE"));
				order.setItem(row, "EXEC_DEPT_CODE", linkMainParm
						.getData("EXEC_DEPT_CODE"));
				order.setItem(row, "ROUTE_CODE", linkMainParm
						.getData("ROUTE_CODE"));
				if (null!=parm.getValue("SKINTEST_FLG")
						&&parm.getValue("SKINTEST_FLG").length()>0) {
					order.setItem(row, "EXEC_DATE", parm.getValue("EXEC_DATE"));//Ƥ��ִ��ʱ��add by huangjw 20141031
					order.setItem(row, "SKINTEST_FLG", parm.getValue("SKINTEST_FLG"));//Ƥ�Խ��
					order.setItem(row, "BATCH_NO", parm.getValue("BATCH_NO"));//����
				}
				order.itemNow = true;
				inf = linkMainParm.getValue("INFLUTION_RATE");
				num = row;
				flgInf =true;
			}
		}
		if(flgInf){
			TDataStore dst = table.getDataStore();
			dst.setItem(num, "INFLUTION_RATE", inf);
			table.setDSValue();
		}else{
			TDataStore dst = table.getDataStore();
			dst.setItem(table.getSelectedRow(), "INFLUTION_RATE", 0);
			table.setDSValue();
		}
//		odoMainControl.messageBox(flgInf+"");
//		odoMainControl.messageBox(inf+"");
		odoMainOpdOrder.initNoSetTable(rxNo, odoMainOpdOrder.tableName, false);
//		odoMainControl.messageBox("123456");
		
		
		
		
		odoMainOpdOrder.calculateCash(odoMainOpdOrder.tableName, amtTag);
		table.getTable().grabFocus();
		table.setSelectedRow(oldRow);
		table.setSelectedColumn(3);
		order.itemNow = false;
		Map inscolor = OdoUtil.getInsColor(odoMainControl.odoMainPat.ctz, odoMainControl.odo.getOpdOrder(),
				odoMainControl.odoMainTjIns.whetherCallInsItf);
		Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odoMainControl.odo.getOpdOrder());
		table.setRowTextColorMap(ctrlcolor);
	}

	/**
	 * ɾ��һ������ҩҽ��
	 * ==========pangben 2013-4-24
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 */
	private boolean deleteRowMed(OpdOrder order ,int row,TTable table)throws Exception{
		if (!odoMainOpdOrder.checkSendPah(order, row))// ����ѷ�ҩ�ѵ���
			return false;
		table.acceptText();//====pangben 2013-1-6 ɾ����ҩ��꽹������������,��ӡ�Ĵ���ǩ�����ҽ����������ɾ����ҽ������
		if(!odoMainOpdOrder.deleteRowMedCtrlComm(order, row, table)){
			return false;
		}
		return true;
	}
	
	/**
	 * ����ҩƷģ��
	 * 
	 * @param parm
	 *            TParm
	 */
	private void insertOrdPack(TParm parm,boolean onFechFlg) throws Exception{
		int count = parm.getCount("ORDER_CODE");
		if (count < 0) {
			return;
		}
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String tableName = TABLE_MED;
		odoMainOpdOrder.rxType = MED;
		tableName = TABLE_MED;
		String rxNo = odoMainControl.getValueString(MED_RX);
		String execDept = odoMainControl.getValueString("MED_RBORDER_DEPT_CODE");
		if (StringUtil.isNullString(rxNo)) {
			odoMainOpdOrder.initRx(MED_RX, MED);
		}
		rxNo = odoMainControl.getValueString(MED_RX);
		order.setFilter("RX_NO='" + rxNo + "'");
		if (!order.filter()) {
			return;
		}
		int row = StringUtil.isNullString(order.getItemString(
				order.rowCount() - 1, "ORDER_CODE")) ? order.rowCount() - 1
				: (order.newOrder(MED, rxNo));		
		for (int i = 0; i < count; i++) {
			//yanjing20130428 ����У�飬����
			TParm parmBase = new TParm();
			if (onFechFlg) {
				parmBase = odoMainOpdOrder.initParmBase(parm, i);
			}else {
				parmBase = PhaBaseTool.getInstance().selectByOrderRoute(
						parm.getValue("ORDER_CODE", i));
			}	
			TParm parmRow = parm.getRow(i);
			boolean memPackageFlg = "Y".equals(parmRow.getValue("MEM_PACKAGE_FLG"));//�ײ�
			TParm sysFee = new TParm(TJDODBTool.getInstance().select(
					SYSSQL.getSYSFee(parmRow.getValue("ORDER_CODE"))));
			sysFee = sysFee.getRow(0);
			sysFee.setData("EXEC_DEPT_CODE", execDept);
			sysFee.setData("CAT1_TYPE", "PHA");
			odoMainOpdOrder.initOrder(order, row, sysFee, parmBase);			
			order.setItem(row, "MEDI_QTY", parmRow.getDouble("MEDI_QTY"));
			order.setItem(row, "MEDI_UNIT", parmRow.getValue("MEDI_UNIT"));
			order.setItem(row, "FREQ_CODE", parmRow.getValue("FREQ_CODE"));
			order.setItem(row, "TAKE_DAYS", parmRow.getInt("TAKE_DAYS"));			
			order.setItem(row, "ROUTE_CODE", parmRow.getValue("ROUTE_CODE"));
			order.setItem(row, "DR_NOTE", parmRow.getValue("DESCRIPTION"));  //add by huangtt 20141114
			if(memPackageFlg){
				order.itemNow = true;
				order.setItem(row, "MEM_PACKAGE_FLG", "Y"); //add by huangtt 20150130
				
				//add by huangtt 20150410 start  �÷���Ƶ��
				if(parmBase.getCount()>0){
					order.setItem(row, "FREQ_CODE", parmBase.getValue("FREQ_CODE",0));
					order.setItem(row, "ROUTE_CODE", parmBase.getValue("ROUTE_CODE",0));
					order.setItem(row, "MEDI_UNIT", parmBase.getValue("MEDI_UNIT",0));			
				}				
				//add by huangtt 20150410 end	
				
//				order.setItem(row, "MEDI_UNIT", ""); 
			    order.setItem(row, "MEDI_QTY", 0);
				order.setItem(row, "DOSAGE_UNIT", parmRow.getValue("MEDI_UNIT"));
				order.setItem(row, "DOSAGE_QTY", parmRow.getDouble("MEDI_QTY"));
				order.setItem(row, "AR_AMT", parmRow.getValue("AR_AMT"));	
				order.setItem(row, "MEM_PACKAGE_ID", parmRow.getValue("ID"));
				order.setItem(row, "GIVEBOX_FLG", "N");
				order.setItem(row, "DISPENSE_UNIT", parmRow.getValue("MEDI_UNIT"));
				order.setItem(row, "DISPENSE_QTY", parmRow.getDouble("MEDI_QTY"));
				
//				if(order.getItemString(row, "DOSAGE_UNIT").equals(order.getItemString(row, "DISPENSE_UNIT"))){
//					order.setItem(row, "DISPENSE_QTY", order.getItemData(row, "DOSAGE_QTY"));
//				}else{
//					TParm unitParm = new TParm(TJDODBTool.getInstance().select(
//							SYSSQL.getPhaTransUnit(parmRow.getValue("ORDER_CODE"))));
//					double stockTrans = unitParm.getDouble("DOSAGE_QTY", 0); // ��ҩ��λ�Կ�浥λת����  
//					int stockQty = Integer.parseInt(Math.round(order.getItemDouble(row, "DOSAGE_QTY") / stockTrans) + "");
//					order.setItem(row, "DISPENSE_QTY", stockQty == 0 ? 1:stockQty);
//
//				}
				double ownAmt = ODOTool.getInstance().roundAmt(order.getItemDouble(row, "OWN_PRICE")
						* order.getItemDouble(row, "DOSAGE_QTY"));
				order.setItem(row, "OWN_AMT", ownAmt);
				order.setItem(row, "PAYAMOUNT",ownAmt-ODOTool.getInstance().roundAmt(order.getItemDouble(row, "AR_AMT")));
				
				order.itemNow = false;
				
			}
			
			order.setItem(row, "LINKMAIN_FLG", parmRow.getValue("LINKMAIN_FLG"));
			order.setItem(row, "LINK_NO", parmRow.getValue("LINK_NO"));			
			order.setActive(row, true);
			row = order.newOrder(MED, rxNo);
		}		
		if (!StringUtil.isNullString(order.getItemString(order.rowCount() - 1,
				"ORDER_CODE"))) {
			order.newOrder(MED, rxNo);
		}
		odoMainOpdOrder.initNoSetTable(rxNo, tableName, false);
		odoMainOpdOrder.calculateCash(tableName, AMT_TAG);
		order.itemNow = false;				
		Map inscolor = OdoUtil.getInsColor(odoMainPat.ctz, odoMainControl.odo.getOpdOrder(),
				odoMainControl.odoMainTjIns.whetherCallInsItf);
		Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odoMainControl.odo.getOpdOrder());
		table.setRowTextColorMap(ctrlcolor);
	}

	@Override
	public void insertPack(TParm parm, boolean flg) throws Exception{
		// TODO Auto-generated method stub
		this.insertOrdPack(parm, flg);
	}
	
	@Override
	public void onInitEvent(ODOMainOpdOrder odoMainOpdOrder) throws Exception{
		// TODO Auto-generated method stub
		this.odoMainOpdOrder = odoMainOpdOrder;
		this.odoMainControl = odoMainOpdOrder.odoMainControl;
		// ��ҩTABLE
		odoMainOpdOrder.tblMed = (TTable) odoMainControl.getComponent(TABLE_MED);
		this.table = odoMainOpdOrder.tblMed;
		table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onMedCreateEditComponent");
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		table.addEventListener(TABLE_MED + "->" + TTableEvent.CHANGE_VALUE,
				this, "onMedValueChange");
		
		
	}
	
	/**
	 * ��ҩ��checkBox�¼�
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onCheckBox(Object obj)throws Exception{
		return odoMainOpdOrder.onCheckBox_influne(obj);
	}

	@Override
	public void onChangeRx(String rxType) throws Exception{
		// TODO Auto-generated method stub
		String rxNo = NULLSTR;
		rxNo = odoMainControl.getValueString(MED_RX);
		if (StringUtil.isNullString(rxNo)) {
			odoMainControl.odo.getOpdOrder().setFilter(
					"RX_TYPE='" + MED + "' AND ORDER_CODE <>''");
			odoMainControl.odo.getOpdOrder().filter();
			table.setDSValue();
			odoMainOpdOrder.calculateCash(TABLE_MED, AMT_TAG);
			return;
		}
		if (!odoMainControl.odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
			odoMainControl.odo.getOpdOrder().newOrder(rxType, rxNo);
		}
		if (!odoMainOpdOrder.initNoSetTable(rxNo, TABLE_MED, false))
			odoMainControl.messageBox("E0036"); // ��ʾ��ҩʧ��
		odoMainControl.setValue("MED_RBORDER_DEPT_CODE", odoMainOpdOrder.phaCode);
		odoMainOpdOrder.calculateCash(TABLE_MED, AMT_TAG);
	}

	@Override
	public boolean medAppyCheckDate(OpdOrder order, int row) throws Exception{
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * ��ҩֵ�ı��¼� ����
	 * @param inRow
	 * @param execDept
	 * @param orderCodeFinal
	 * @param columnNameFinal
	 */
	private void setOnMedValueChange(final int inRow,final String execDept,
			final String orderCodeFinal,final String columnNameFinal,final double oldDosageQty) throws Exception{
		
		if(!odoMainOpdOrder.indOrgIsExinvs.contains(execDept)){
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						if (!odoMainOpdOrder.checkStoreQty(inRow, execDept, orderCodeFinal,
								columnNameFinal,oldDosageQty)) {
							return ;
						}
					} catch (Exception e) {
					}
				}
			});
			
		}
	}

	@Override
	public TTable getTable() throws Exception{
		// TODO Auto-generated method stub
		return table;
	}

	@Override
	public boolean initTable(String rxNo) throws Exception{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void insertOrder(TParm parm) throws Exception{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCheckRxNoSum(TParm parm) throws Exception{
		// TODO Auto-generated method stub
		// ҩƷ
		TParm orderResult = ((TParm) parm.getData("ORDER"));
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			String rxNo = odoMainControl.getValueString(MED_RX);
			if (null==rxNo || rxNo.length()<=0) {
				return "����ҩû�г�ʼ������ǩ";
			}
			if (!order.isExecutePrint(odoMainControl.odo.getCaseNo(), rxNo)) {
				return "�Ѿ���Ʊ������ҩ����ǩ���������ҽ��";
			}
		}
		return null;
	}

	@Override
	public void getTempSaveParm() throws Exception{
		// TODO Auto-generated method stub
		odoMainControl.odo.getOpdOrder().setFilter(
						"RX_TYPE='"+ MED+ "' AND ORDER_CODE <>'' AND #NEW#='Y'  AND #ACTIVE#='Y'");
		odoMainControl.odo.getOpdOrder().filter();
	}

	@Override
	public void setopdRecipeInfo(OpdOrder order) throws Exception{
		// TODO Auto-generated method stub
		order.setFilter("RX_TYPE='" + MED + "'");
		order.filter();
	}
	
	@Override
	public void onDeleteOrderList(int rxType) throws Exception{
		String rxNo = NULLSTR;
		String tableName = NULLSTR;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String oldfilter = order.getFilter();
		
		odoMainControl.openDialog(
				URLODOMAINDELETERX, odoMainControl, false);
		
		tableName = TABLE_MED;
		odoMainControl.setValue(AMT_TAG, NULLSTR);
		if (StringUtil.isNullString(tableName)) {
			odoMainControl.messageBox("E0034"); // ȡ�����ݴ���
			return;
		}
		table = (TTable) odoMainControl.getComponent(tableName);
		order.setFilter(oldfilter);
		order.filter();
		table.setDSValue();
	}

	@Override
	public boolean deleteRow(OpdOrder order, int row, TTable table)
			throws Exception {
		// TODO Auto-generated method stub
		return deleteRowMed(order, row, table);
	}

	@Override
	public void deleteorderAuto(int row) throws Exception {
		// TODO Auto-generated method stub
		TTable table = null;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
		String rx_no = NULLSTR;
		table = (TTable) odoMainControl.getComponent(TABLE_MED);
		rx_no = odoMainControl.getValueString(MED_RX);
		order.deleteRow(row);
		order.setFilter("RX_TYPE='" + MED
				+ "' AND HIDE_FLG='N' AND RX_NO='" + rx_no + "'");
		table.filter();
		table.setDSValue();
	}

	@Override
	public boolean check(OpdOrder order, int row, String name,
			boolean spcFlg) throws Exception {
		// TODO Auto-generated method stub
		return checkPha(order, row, name, spcFlg);
	}

	@Override
	public void setOnValueChange(int inRow, String execDept,
			String orderCodeFinal, String columnNameFinal,final double oldDosageQty) throws Exception {
		// TODO Auto-generated method stub
		this.setOnMedValueChange(inRow, execDept, orderCodeFinal, columnNameFinal, oldDosageQty);
	}

	@Override
	public void insertExa(TParm parm, int row, int column) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * �ж�ҩƷ�����Ƿ��޸�
	 * yanjing 20140327
	 * @param orderCode
	 *            String
	 * @return boolean
	 */
		private TParm ismodifyBatchNo(String caseNo,TParm parm) throws Exception{
		  String orderCode = parm.getValue("ORDER_CODE");
		  String order_desc = parm.getValue("ORDER_DESC");
		TParm opdResult = new TParm();
		//�Ƿ�����Ƥ��========================//Ƥ��ִ��ʱ��add by huangjw 20141031
		String opdSql = "SELECT A.ORDER_DESC,A.EXEC_DATE, A.BATCH_NO,A.SKINTEST_FLG FROM OPD_ORDER A,PHA_BASE B WHERE A.ORDER_CODE = B.ORDER_CODE " +
		"AND A.CASE_NO = '"+caseNo+"' AND B.SKINTEST_FLG = 'Y' AND A.ROUTE_CODE = 'PS' AND A.ORDER_CODE='"+orderCode +
				"' AND A.CAT1_TYPE = 'PHA' AND A.SKINTEST_FLG IS NOT NULL ORDER BY A.OPT_DATE DESC" ;

		opdResult = new TParm(TJDODBTool.getInstance().select(opdSql));
		if (opdResult.getCount()>0) {
			parm.setData("BATCH_NO", opdResult.getValue("BATCH_NO",0));
			parm.setData("SKINTEST_FLG", opdResult.getValue("SKINTEST_FLG",0));
			parm.setData("EXEC_DATE",opdResult.getValue("NS_EXEC_DATE",0));//Ƥ��ִ��ʱ��add by huangjw 20141031
//			if(opdResult.getValue("SKINTEST_FLG", 0).equals("1")){
//				 String batchNo = opdResult.getValue("BATCH_NO",0);
//				  if(odoMainControl.messageBox("��ʾ��Ϣ/Tip",
//							order_desc+ "������Ϊ:"+batchNo+"���Ϊ:(+)����,��������?",0) != 0){
//					  return opdResult;
//				  }
//		} 
		}else{
			parm.setData("BATCH_NO","");
			parm.setData("SKINTEST_FLG", "");
			parm.setData("EXEC_DATE","");//Ƥ��ִ��ʱ��add by huangjw 20141031
		}
	   return opdResult;
	}

	@Override
	public void onSortRx(boolean flg) throws Exception {
		// TODO Auto-generated method stub
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String lastFilter = order.getFilter();
		order.setFilter("RX_TYPE='1'");
		order.filter();
		List<String> list = new ArrayList<String>();
		String rxNo;
		for (int i = 0; i < order.rowCount(); i++) {
			rxNo = order.getItemString(i, "RX_NO");
			if(!list.contains(rxNo)){
				list.add(rxNo);
			}
		}
		
		for (String string : list) {
			order.setFilter("RX_NO = '" + string + "'");
			order.filter();
			sortRx(flg, order);
		}
		
		order.setFilter(lastFilter);
		order.filter();
		
		if(flg){
			odoMainOpdOrder.onTempSave(null, 0);
			odoMainControl.messageBox("�ַ��ɹ�");
		}
	}
	
	/**
	 * ��������
	 * @param finalList
	 * @param pha_link_all
	 */
	private void sortRxFinally(List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> finalList, List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> pha_link_all ){
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> lltemp = new ArrayList<List<com.javahis.ui.testOpb.bean.OpdOrder>>();
		List<com.javahis.ui.testOpb.bean.OpdOrder> ltemp;
		List<com.javahis.ui.testOpb.bean.OpdOrder> lf = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		for (List<List<com.javahis.ui.testOpb.bean.OpdOrder>> pha_link : pha_link_all) {
			
			for (int i = 0; i < pha_link.size(); i++) {
				ltemp = pha_link.get(i);
				if(ltemp.size()+lf.size()<=5){
					for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder : ltemp) {
						lf.add(opdOrder);
					}
				}else if(ltemp.size()<=5){
					lltemp.add(lf);
					lf = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
					for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder : ltemp) {
						lf.add(opdOrder);
					}
				}else{
					for (int j = 0; j < ltemp.size(); j++) {
						com.javahis.ui.testOpb.bean.OpdOrder opdOrder = ltemp.get(j);
						if( j % 5 == 0){
							lltemp.add(lf);
							lf = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
						}
						lf.add(opdOrder);
						if(j == ltemp.size() - 1){
							lltemp.add(lf);
							lf = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
						}
					}
				}
			}
			lltemp.add(lf);
			lf = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		}
		finalList.add(lltemp);
	}
	
	/**
	 * ͨ������ҩ����
	 * @param order
	 * @param i
	 * @param pha
	 * @param pha_dept
	 * @param pha_link
	 */
	private void sortRxByCw(OpdOrder order, int i, List<com.javahis.ui.testOpb.bean.OpdOrder> pha, List<String> pha_dept, List<String> pha_link){
		com.javahis.ui.testOpb.bean.OpdOrder opdOrder = new com.javahis.ui.testOpb.bean.OpdOrder();
		opdOrder.caseNo = order.getItemString(i,"CASE_NO");
		opdOrder.rxNo = order.getItemString(i,"RX_NO");
		opdOrder.seqNo = new BigDecimal(order.getItemString(i,"SEQ_NO"));
		opdOrder.routeCode = order.getItemString(i,"ROUTE_CODE");
		opdOrder.doseType = routeDoseMap.get(opdOrder.routeCode);
		opdOrder.execDeptCode = order.getItemString(i,"EXEC_DEPT_CODE");
		if(order.getItemString(i,"LINK_NO").length() > 0){
			opdOrder.linkNo = order.getItemString(i,"LINK_NO");
		}else{
			opdOrder.linkNo = "#";
		}
		pha.add(opdOrder);
		if(!pha_dept.contains(opdOrder.execDeptCode)){
			pha_dept.add(opdOrder.execDeptCode);
		}
		if(!pha_link.contains(opdOrder.linkNo)){
			pha_link.add(opdOrder.linkNo);
		}
	}
	
	/**
	 * ����ͨ��OEIF
	 * @param pha
	 * @param pha_o
	 * @param pha_if
	 * @param pha_e
	 */
	private void sortRxByOEIF(List<com.javahis.ui.testOpb.bean.OpdOrder> pha, 
			List<com.javahis.ui.testOpb.bean.OpdOrder> pha_o, 
			List<com.javahis.ui.testOpb.bean.OpdOrder> pha_if,
			List<com.javahis.ui.testOpb.bean.OpdOrder> pha_e){
		for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder : pha) {
			if("O".equals(opdOrder.doseType)){
				pha_o.add(opdOrder);
			}else if("I".equals(opdOrder.doseType)){
				pha_if.add(opdOrder);
			}else if("F".equals(opdOrder.doseType)){
				pha_if.add(opdOrder);
			}else if("E".equals(opdOrder.doseType)){
				pha_e.add(opdOrder);
			}
		}
	}
	
	/**
	 * ���ݸ�������ϸ����
	 * @param pha_dept
	 * @param pha_o
	 * @param pha_if
	 * @param pha_e
	 * @param pha_o_dept
	 * @param pha_if_dept
	 * @param pha_e_dept
	 */
	private void sortRxByKind(List<String> pha_dept,
			List<com.javahis.ui.testOpb.bean.OpdOrder> pha_o,
			List<com.javahis.ui.testOpb.bean.OpdOrder> pha_if,
			List<com.javahis.ui.testOpb.bean.OpdOrder> pha_e,
			List<List<com.javahis.ui.testOpb.bean.OpdOrder>> pha_o_dept,
			List<List<com.javahis.ui.testOpb.bean.OpdOrder>> pha_if_dept,
			List<List<com.javahis.ui.testOpb.bean.OpdOrder>> pha_e_dept){
		List<com.javahis.ui.testOpb.bean.OpdOrder> lt;
		for (String execDeptCode : pha_dept) {
			lt = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
			for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder_o : pha_o) {
				if(execDeptCode.equals(opdOrder_o.execDeptCode)){
					lt.add(opdOrder_o);
				}
			}
			pha_o_dept.add(lt);
			lt = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
			for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder_if : pha_if) {
				if(execDeptCode.equals(opdOrder_if.execDeptCode)){
					lt.add(opdOrder_if);
				}
			}
			pha_if_dept.add(lt);
			lt = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
			for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder_e : pha_e) {
				if(execDeptCode.equals(opdOrder_e.execDeptCode)){
					lt.add(opdOrder_e);
				}
			}
			pha_e_dept.add(lt);
		}
	}
	
	/**
	 * ȡ������ҩOEIF���Ӻ�����
	 * @param pha_dept
	 * @param pha_link
	 * @return
	 */
	private List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> getPhaCwOEIFLinkList(List<List<com.javahis.ui.testOpb.bean.OpdOrder>> pha_dept,
			List<String> pha_link){
		List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> phacw_oeif_link = new ArrayList<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>>();
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> lltemp;
		List<com.javahis.ui.testOpb.bean.OpdOrder> lt;
		for (List<com.javahis.ui.testOpb.bean.OpdOrder> pcd : pha_dept) {
			lltemp = new ArrayList<List<com.javahis.ui.testOpb.bean.OpdOrder>>();
			for (String linkNo : pha_link) {
				lt = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
				for (com.javahis.ui.testOpb.bean.OpdOrder opdOrderPcd : pcd) {
					if(linkNo.equals(opdOrderPcd.linkNo)){
						lt.add(opdOrderPcd);
					}
				}
				lltemp.add(lt);
			}
			phacw_oeif_link.add(lltemp);
		}
		return phacw_oeif_link;	
	}
	
	private void sortRx(boolean flg, OpdOrder order){
		List<com.javahis.ui.testOpb.bean.OpdOrder> phac = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		List<com.javahis.ui.testOpb.bean.OpdOrder> phaw = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		List<com.javahis.ui.testOpb.bean.OpdOrder> phac_o = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		List<com.javahis.ui.testOpb.bean.OpdOrder> phac_if = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		List<com.javahis.ui.testOpb.bean.OpdOrder> phac_e = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		List<com.javahis.ui.testOpb.bean.OpdOrder> phaw_o = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		List<com.javahis.ui.testOpb.bean.OpdOrder> phaw_if = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		List<com.javahis.ui.testOpb.bean.OpdOrder> phaw_e = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> phac_o_dept = new ArrayList<List<com.javahis.ui.testOpb.bean.OpdOrder>>();
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> phac_if_dept = new ArrayList<List<com.javahis.ui.testOpb.bean.OpdOrder>>();
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> phac_e_dept = new ArrayList<List<com.javahis.ui.testOpb.bean.OpdOrder>>();
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> phaw_o_dept = new ArrayList<List<com.javahis.ui.testOpb.bean.OpdOrder>>();
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> phaw_if_dept = new ArrayList<List<com.javahis.ui.testOpb.bean.OpdOrder>>();
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> phaw_e_dept = new ArrayList<List<com.javahis.ui.testOpb.bean.OpdOrder>>();
		List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> phac_o_link;
		List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> phac_if_link;
		List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> phac_e_link;
		List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> phaw_o_link;
		List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> phaw_if_link;
		List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> phaw_e_link;
		
		List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> finalList = new ArrayList<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>>();
		
		List<String> phac_dept = new ArrayList<String>();
		List<String> phaw_dept = new ArrayList<String>();
		List<String> phac_link = new ArrayList<String>();
		List<String> phaw_link = new ArrayList<String>();
		List<Integer> presrtNoList = new ArrayList<Integer>();
		
		int presrtNo = 0;
		
		boolean sortFlg = true;
		for (int i = 0; i < order.rowCount(); i++) {
			if(!StringUtil.isNullString(order.getItemString(i,
					 "ORDER_CODE"))){
				if(!flg){
					sortFlg = order.getItemString(i, "PRESRT_NO").length() == 0;
				}
				sortFlg = sortFlg && !"Y".equals(order.getItemString(i, "EXEC_FLG")) 
					&& order.getItemString(i, "PHA_CHECK_CODE").length() == 0
					&& order.getItemString(i, "PHA_DOSAGE_CODE").length() == 0
					&& order.getItemString(i, "PHA_DISPENSE_CODE").length() == 0
					&& order.getItemString(i, "PHA_RETN_CODE").length() == 0;
				if("PHA_C".equals(order.getItemString(i, "ORDER_CAT1_CODE")) && sortFlg){
					if(!presrtNoList.contains(order.getItemInt(i, "PRESRT_NO"))){
						presrtNoList.add(order.getItemInt(i, "PRESRT_NO"));
					}
					sortRxByCw(order, i, phac, phac_dept, phac_link);
				}else if("PHA_W".equals(order.getItemString(i, "ORDER_CAT1_CODE")) && sortFlg){
					if(!presrtNoList.contains(order.getItemInt(i, "PRESRT_NO"))){
						presrtNoList.add(order.getItemInt(i, "PRESRT_NO"));
					}
					sortRxByCw(order, i, phaw, phaw_dept, phaw_link);
				}
			}
			if(presrtNo < order.getItemInt(i, "PRESRT_NO")){
				presrtNo = order.getItemInt(i, "PRESRT_NO");
			}
		}
		
		if(flg){
			if(JOptionPane.showConfirmDialog(null, "���·ַ������� " + presrtNoList.size() + " ������ǩ\r\n�Ƿ����", "�Ƿ����·ַ�", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
				return;
			}
		}
		
		sortRxByOEIF(phac, phac_o, phac_if, phac_e);
		sortRxByOEIF(phaw, phaw_o, phaw_if, phaw_e);
		
		sortRxByKind(phac_dept, phac_o, phac_if, phac_e, phac_o_dept, phac_if_dept, phac_e_dept);
		sortRxByKind(phaw_dept, phaw_o, phaw_if, phaw_e, phaw_o_dept, phaw_if_dept, phaw_e_dept);
		
		List<com.javahis.ui.testOpb.bean.OpdOrder> lt;
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> lltemp;
		
		phac_o_link = getPhaCwOEIFLinkList(phac_o_dept, phac_link);
		phac_if_link = getPhaCwOEIFLinkList(phac_if_dept, phac_link);
		phac_e_link = getPhaCwOEIFLinkList(phac_e_dept, phac_link);
		phaw_o_link = getPhaCwOEIFLinkList(phaw_o_dept, phaw_link);
		phaw_if_link = getPhaCwOEIFLinkList(phaw_if_dept, phaw_link);
		phaw_e_link = getPhaCwOEIFLinkList(phaw_e_dept, phaw_link);
		
		sortRxFinally(finalList, phac_o_link);
		sortRxFinally(finalList, phac_if_link);
		sortRxFinally(finalList, phac_e_link);
		sortRxFinally(finalList, phaw_o_link);
		sortRxFinally(finalList, phaw_if_link);
		sortRxFinally(finalList, phaw_e_link);
		
		List<com.javahis.ui.testOpb.bean.OpdOrder> temp;
		for (List<List<com.javahis.ui.testOpb.bean.OpdOrder>> ol : finalList) {
			for (int i = 0; i < ol.size(); i++) {
				presrtNo++;
				temp = ol.get(i);
				for (com.javahis.ui.testOpb.bean.OpdOrder opdOrderTemp : temp) {
					for (int j = 0; j < order.rowCount(); j++) {
						if(opdOrderTemp.caseNo.equals(order.getItemString(j,"CASE_NO"))
								&& opdOrderTemp.rxNo.equals(order.getItemString(j,"RX_NO"))
								&& opdOrderTemp.seqNo.compareTo(new BigDecimal(order.getItemString(j,"SEQ_NO"))) == 0){
							order.setItem(j, "PRESRT_NO", presrtNo);
						}
					}
				}
			}
		}
	}

	@Override
	public void onChnChange(String fieldName, String type) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
