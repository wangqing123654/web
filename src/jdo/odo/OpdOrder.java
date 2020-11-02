package jdo.odo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.bil.BIL;
import jdo.device.CallNo;
import jdo.hl7.Hl7Communications;
import jdo.opb.OPBTool;
import jdo.opd.ODOTool;
import jdo.opd.OrderTool;
import jdo.opd.TotQtyTool;
import jdo.pha.PhaBaseTool;
import jdo.pha.PhaSysParmTool;
import jdo.spc.INDTool;
import jdo.sys.DeptTool;
import jdo.sys.Operator;
import jdo.sys.SYSCtrlDrugClassTool;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.util.StringTool;
import com.dongyang.util.TSystem;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: ҽ���洢����
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author ehui 2009.2.11
 * @version 1.0
 */
public class OpdOrder extends StoreBase {
	/**
	 * ������
	 */
	private String mrNo;
	/**
	 * �����
	 */
	private String caseNo;
	/**
	 * �ż�ס��
	 */
	private String admType;
	/**
	 * ҽʦ
	 */
	private String drCode;
	/**
	 * ����
	 */
	private String deptCode;
	/**
	 * ����ȼ�
	 */
	private String serviceLevel;
	
	/**
	 * Ԥ�������
	 * caowl
	 * */
	private boolean isPreOrder = false;
	/**
	 * �õ�Ԥ�������
	 * caowl
	 * */
	public boolean isPreOrder() {
		return isPreOrder;
	}
	/**
	 * ����Ԥ�������
	 * caowl
	 * */
	public void setPreOrder(boolean isPreOrder) {
		this.isPreOrder = isPreOrder;
	}
	
	/**
	 * ����A�_�z���̎�����ü����\̖
	 * yanjing 20131217
	 */
	private Map<String, String> preCaseNos;
	

	/**
	 * �ж��Ƿ�ֻ������ҽ�� û���޸ĺ�ɾ��ҽ��
	 */
	boolean isNew = false;
	//private Compare compare = new Compare();
	// ȡ��ҩ����̨��
	private String GET_COUNTER_NO = "SELECT * FROM PHA_COUNTERNO WHERE ORG_CODE='#' AND CHOSEN_FLG='Y' ";
	// ȡ���Ƿ�Ϊ����ҩƷ
	private String GET_PSYCHOPHA2 = "SELECT B.PSYCHOPHA2_FLG FROM PHA_BASE A,SYS_CTRLDRUGCLASS B WHERE A.ORDER_CODE='#' AND A.CTRLDRUGCLASS_CODE=B.CTRLDRUGCLASS_CODE AND B.PSYCHOPHA2_FLG='Y'";
	// ȡ�ü���ҽ��
	private String GET_ORDERSET = "SELECT A.*,B.DOSAGE_QTY,B.HIDE_FLG FROM SYS_FEE A,SYS_ORDERSETDETAIL B WHERE A.ORDER_CODE=B.ORDER_CODE AND B.ORDERSET_CODE='#'";
	// ȡ��SYS_FEE����
	public String GET_SYS_FEE = "SELECT * FROM SYS_FEE WHERE ORDER_CODE='#'";
	// ȡ��ҽ����ִ��״̬
	private String GET_EXEC_FLG = "SELECT BILL_FLG,BILL_TYPE,EXEC_FLG,RECEIPT_FLG,PRINT_FLG,DR_CODE,MEM_PACKAGE_ID FROM OPD_ORDER WHERE CASE_NO='#' AND RX_NO='#' AND SEQ_NO=#";
	//�鿴������ҽ���Ƿ��Ѿ��Ǽ�=====pangben 2013-1-9
	private String GET_STATUS_FLG="SELECT STATUS FROM MED_APPLY WHERE CAT1_TYPE='#' AND APPLICATION_NO='#'";
	
	/**
	 * MED_APPLY_NO���ϣ����桢��ԃ�r���
	 */
	private Map labMap;
	/**
	 * �Ƿ����setItem����ע��
	 */
	public boolean itemNow = false;
	/**
	 * MED_APPLY
	 */
	private MedApply med;
	/**
	 * sys_fee������
	 */
	public TDataStore sysFee = TIOM_Database.getLocalTable("SYS_FEE");

	/**
	 * �õ�������
	 * 
	 * @return String
	 */
	public String getMrNo() {
		return this.mrNo;
	}

	/**
	 * ���ò�����
	 * 
	 * @param mrNo
	 *            String
	 */
	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	/**
	 * �õ������
	 * 
	 * @return String
	 */
	public String getCaseNo() {
		return this.caseNo;
	}

	/**
	 * ���þ����
	 * 
	 * @param caseNo
	 *            String
	 */
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	/**
	 * �����ż�ס��
	 * 
	 * @param admType
	 *            String
	 */
	public void setAdmType(String admType) {
		this.admType = admType;
	}

	/**
	 * �õ��ż�ס��
	 * 
	 * @return String
	 */
	public String getAdmType() {
		return admType;
	}

	/**
	 * ����ҽʦ
	 * 
	 * @param drCode
	 *            String
	 */
	public void setDrCode(String drCode) {
		this.drCode = drCode;
	}

	/**
	 * �õ�ҽʦ
	 * 
	 * @return String
	 */
	public String getDrCode() {
		return drCode;
	}

	/**
	 * ���ò���
	 * 
	 * @param deptCode
	 *            String
	 */
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	/**
	 * �õ�����
	 * 
	 * @return String
	 */
	public String getDeptCode() {
		return deptCode;
	}

	/**
	 * ���÷���ȼ�
	 * 
	 * @param service_level
	 *            String
	 */
	public void setServiceLevel(String service_level) {
		this.serviceLevel = service_level;
	}

	/**
	 * �ȵ�����ȼ�
	 * 
	 * @return String
	 */
	public String getServiceLevel() {
		return this.serviceLevel;
	}

	/**
	 * �õ�MED_APPLY
	 * 
	 * @return
	 */
	public MedApply getMedApply() {
		if (med == null) {
			med = new MedApply();
			med.onQueryByCaseNo(this.getCaseNo());
		}
		return med;
	}

	/**
	 * �؆�MED_APPLY
	 */
	public void resetMedApply() {
		med = new MedApply();
		med.onQueryByCaseNo(this.getCaseNo());
	}

	/**
	 * �õ�MAP
	 * 
	 * @return
	 */
	public Map getLabMap() {
		if (this.labMap == null) {
			labMap = new HashMap();
		}
		return labMap;
	}

	/**
	 * ���MAP
	 */
	public void resetMap() {
		this.labMap = new HashMap();
	}

	/**
	 * �õ�SQL
	 * 
	 * @return String
	 */
	protected String getQuerySQL() {		
		if(!isPreOrder){
			return "SELECT 'Y' FLG ,OPD_ORDER.* FROM OPD_ORDER WHERE CASE_NO = '" + getCaseNo()
			+ "' AND RX_TYPE<>'0' ORDER BY RX_TYPE,RX_NO,SEQ_NO";
		}else{
			//��ѯreg_patadm����case_no
			String nowDate=StringTool.getString((Timestamp)SystemTool.getInstance().getDate() ,
					"yyyy-MM-dd");//��õ�ǰʱ��
			String preCaseNo = "";
			String regSql = "SELECT CASE_NO FROM REG_PATADM WHERE " +
					"MR_NO = '"+mrNo+"' AND (ADM_DATE IS NULL OR ADM_DATE >=TO_DATE('"+nowDate+"','yyyy/MM/dd'))";
			TParm regCaseNo = new TParm(TJDODBTool.getInstance().select(regSql));
			if(regCaseNo.getCount() <= 0){
				preCaseNo = getCaseNo();
			}else{
				for (int j = 0; j < regCaseNo.getCount(); j++) {
					if(j != regCaseNo.getCount() - 1){
						preCaseNo += "'"+regCaseNo.getValue("CASE_NO", j)+"'"+",";
					}else if(j == regCaseNo.getCount() - 1){
						preCaseNo += "'"+regCaseNo.getValue("CASE_NO", j)+"'";	
					}
				}
			}
				return "SELECT 'Y' FLG ,OPD_ORDER.* FROM OPD_ORDER WHERE CASE_NO in (" + preCaseNo
				+ ") AND RX_TYPE<>'0' ORDER BY RX_TYPE,RX_NO,SEQ_NO";
		}
		
	}

	/**
	 * �ж��Ƿ�ֻ������ҽ�� û���޸ĺ�ɾ��ҽ��
	 * 
	 * @return boolean
	 */
	public boolean isNew() {
		return isNew;
	}

	/**
	 * ��������
	 * 
	 * @param row
	 *            int
	 * @return int
	 */
	public int insertRow(int row) {
		int newRow = super.insertRow(row);
		if (newRow == -1)
			return -1;
		// �����ż�ס��
		setItem(newRow, "ADM_TYPE", getAdmType());
		setItem(newRow, "DR_CODE", getDrCode());
		setItem(newRow, "DEPT_CODE", getDeptCode());
		setItem(newRow, "CASE_NO", getCaseNo());
		setItem(newRow, "MR_NO", getMrNo());
		setItem(newRow, "REGION_CODE", Operator.getRegion());
		setItem(newRow, "ORDER_DATE", TJDODBTool.getInstance().getDBTime());
		setItem(newRow, "UPDATE_TIME", SystemTool.getInstance().getUpdateTime());  //add by huangtt 20170323 huangtt		
		return newRow;
	}

	/**
	 * ���ò����û�
	 * 
	 * @param optUser
	 *            String �����û�
	 * @param optDate
	 *            Timestamp ����ʱ��
	 * @param optTerm
	 *            String ����IP
	 * @return boolean true �ɹ� false ʧ��
	 */
	public boolean setOperator(String optUser, Timestamp optDate, String optTerm) {
		String storeName = isFilter() ? FILTER : PRIMARY;
		int rows[] = getNewRows(storeName);
		for (int i = 0; i < rows.length; i++){
			if(getItemData(rows[i], "ORDER_DATE") == null || getItemData(rows[i], "ORDER_DATE").toString().length() == 0){
				setItem(rows[i], "ORDER_DATE", optDate, storeName);
			}

		}
		return super.setOperator(optUser, optDate, optTerm);
	}

	/**
	 * �õ�ȡ��ԭ��������µĴ�����
	 * 
	 * @return String
	 */
	public String getNewRx() {
		String rxNo = SystemTool.getInstance().getNo("ALL", "ODO", "RX_NO",
				"RX_NO");
		if (rxNo.length() == 0) {
			// err("ȡ������ʧ��");
			// //System.out.println("ȡ������ʧ��");
			return null;
		}
		return rxNo;
	}

	/**
	 * Ϊ����ǩ�б�Combo�ṩ����
	 * 
	 * @param RXType
	 * @return String[]
	 */
	public String[] getRx(String RXType) {
		if (RXType == null || RXType.length() == 0)
			return new String[] {};
		ArrayList list = new ArrayList();
		String storeName = isFilter() ? FILTER : PRIMARY;
		TParm data = getBuffer(storeName);
		// System.out.println("data==========="+data);
		int count = data.getCount();
		for (int i = 0; i < count; i++) {
			if (!RXType.equals(data.getValue("RX_TYPE", i)))
				continue;
			String rxNo = data.getValue("RX_NO", i);
			if (list.indexOf(rxNo) < 0) {
				list.add(rxNo);
			}
		}
		String rxName = "";
		if ("en".equalsIgnoreCase(Operator.getLanguage())) {
			rxName = "�� Rx";
		} else {
			rxName = "�� ����ǩ";
		}
		for (int i = 0; i < list.size(); i++) {

			String s = (String) list.get(i);
			// //System.out.println("s=:"+s);
			list.set(i, s + ",��" + (i + 1) + rxName);
		}
		// list.add(0,",");
		// System.out.println("list==="+list);
		return (String[]) list.toArray(new String[] {});
	}

	/**
	 * �õ�����������
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @param column
	 *            String
	 * @return Object
	 */
	public Object getOtherColumnValue(TParm parm, int row, String column) {
		double tot = 0.0;
		// System.out.println("getddddddddddOtherColumnValue.parm==="+parm);
		if ("PAYAMOUNT".equalsIgnoreCase(column)) {

			String setCode = this.getItemString(row, "ORDERSET_CODE");
			String rxNo = this.getItemString(row, "RX_NO");
			int groupNo = this.getItemInt(row, "ORDERSET_GROUP_NO");
			//double own_price = 0.0;
			//double ar_price = 0.0;
			String filterString = getFilter();
			this.setFilter("RX_NO='" + rxNo + "' AND  ORDERSET_CODE='"
					+ setCode + "' AND ORDERSET_GROUP_NO= " + groupNo
					+ " AND SETMAIN_FLG='N'");
			// ����Ǵ��õĻ����п����ǵ���ҽ��
			if ("4".equalsIgnoreCase(this.getItemString(row, "RX_TYPE"))
					&& StringUtil.isNullString(setCode)) {
				this.setFilter("RX_NO='" + rxNo + "' AND ORDER_CODE='"
						+ this.getItemString(row, "ORDER_CODE")
						+ "' AND SEQ_NO = '"
						+ this.getItemString(row, "SEQ_NO") + "'");
			}
			this.filter();
			double ownPriceEach = 0.0;
			double arPriceEach = 0.0;
			for (int i = 0; i < this.rowCount(); i++) {

				ownPriceEach += this.getItemDouble(i, "OWN_AMT");
				arPriceEach += this.getItemDouble(i, "AR_AMT");
			}
			this.setFilter(filterString);
			this.filter();
			return ownPriceEach - arPriceEach;
		}
		if ("OWN_PRICE_MAIN".equalsIgnoreCase(column)
				|| "OWN_AMT_MAIN".equalsIgnoreCase(column)
				|| "AR_AMT_MAIN".equalsIgnoreCase(column)) {
			String setCode = this.getItemString(row, "ORDERSET_CODE");
			String rxNo = this.getItemString(row, "RX_NO");
			int groupNo = this.getItemInt(row, "ORDERSET_GROUP_NO");
			//double mainMedi = this.getItemDouble(row, "MEDI_QTY");// ���������
			String filterString = getFilter();
			this.setFilter("RX_NO='" + rxNo + "' AND  ORDERSET_CODE='"
					+ setCode + "' AND ORDERSET_GROUP_NO= " + groupNo
					+ " AND SETMAIN_FLG='N'");
			// ����Ǵ��õĻ����п����ǵ���ҽ��
			if ("4".equalsIgnoreCase(this.getItemString(row, "RX_TYPE"))
					&& StringUtil.isNullString(setCode)) {
				this.setFilter("RX_NO='" + rxNo + "' AND ORDER_CODE='"
						+ this.getItemString(row, "ORDER_CODE")
						+ "' AND SEQ_NO = '"
						+ this.getItemString(row, "SEQ_NO") + "'");
			}
			this.filter();
			for (int i = 0; i < this.rowCount(); i++) {
				double ownPriceEach = 0.0;
				if ("AR_AMT_MAIN".equalsIgnoreCase(column)) {
					ownPriceEach = this.getItemDouble(i, "AR_AMT");
				} else if ("OWN_AMT_MAIN".equalsIgnoreCase(column)) {
					ownPriceEach = this.getItemDouble(i, "OWN_AMT");
				} else if ("OWN_PRICE_MAIN".equalsIgnoreCase(column)) {
					// ����Ǽ���ҽ�� ��ôϸ��ĵ�����Ҫ�������� ����Ϊ����ĵ�����ʾ
					if (!StringUtil.isNullString(setCode)) {
						double osTot = this.getOrderSetTot(this.getItemString(
								i, "ORDER_CODE"), setCode);
						ownPriceEach = this.getItemDouble(i, "OWN_PRICE")
								* osTot;
					} else
						ownPriceEach = this.getItemDouble(i, "OWN_PRICE");
				}
				tot += ownPriceEach;
			}
			this.setFilter(filterString);
			this.filter();
			return tot;
		}
		if ("ORDER_ENG_DESC".equalsIgnoreCase(column)) {
			TParm sysFeeParm = sysFee
					.getBuffer(sysFee.isFilter() ? sysFee.FILTER
							: sysFee.PRIMARY);
			Vector orderCode = (Vector) sysFeeParm.getData("ORDER_CODE");
			String code = this.getItemString(row, "ORDER_CODE");
			int rowNow = orderCode.indexOf(code);
			if (rowNow < 0)
				return "";
			return sysFeeParm.getValue("GOODS_DESC", rowNow);
		}
		if ("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(column)) {
			TParm sysFeeParm = sysFee
					.getBuffer(sysFee.isFilter() ? sysFee.FILTER
							: sysFee.PRIMARY);
			Vector orderCode = (Vector) sysFeeParm.getData("ORDER_CODE");
			String code = this.getItemString(row, "ORDER_CODE");
			// //System.out.println("row="+row+",code="+code);
			int rowNow = orderCode.indexOf(code);
			if (rowNow < 0) {
				return "";
			}
			String desc = "";
			if ("en".equalsIgnoreCase(Operator.getLanguage())) {
				desc = sysFeeParm.getValue("TRADE_ENG_DESC", rowNow) + " "
						+ sysFeeParm.getValue("SPECIFICATION", rowNow);
			} else {
				desc = sysFeeParm.getValue("ORDER_DESC", rowNow) + " "
						+ sysFeeParm.getValue("SPECIFICATION", rowNow);
			}
			return desc;
		}
		if ("ORDER_DESC1".equalsIgnoreCase(column)) {
			int index = row * 4 + 0;
			return getItemString(index, "ORDER_DESC");
		}
		if ("ORDER_DESC2".equalsIgnoreCase(column)) {
			int index = row * 4 + 1;
			return getItemString(index, "ORDER_DESC");
		}
		if ("ORDER_DESC3".equalsIgnoreCase(column)) {
			int index = row * 4 + 2;
			return getItemString(index, "ORDER_DESC");
		}
		if ("ORDER_DESC4".equalsIgnoreCase(column)) {
			int index = row * 4 + 3;
			return getItemString(index, "ORDER_DESC");
		}
		// ORDER_DESC1;MEDI_QTY1;DCTEXCEP_CODE1;ORDER_DESC2;MEDI_QTY2;DCTEXCEP_CODE2;ORDER_DESC3;MEDI_QTY3;DCTEXCEP_CODE3;ORDER_DESC4;MEDI_QTY4;DCTEXCEP_CODE4
		/*
		 * ORDER_DESC1;MEDI_QTY1;DCTEXCEP_CODE1;
		 * ORDER_DESC2;MEDI_QTY2;DCTEXCEP_CODE2;
		 * ORDER_DESC3;MEDI_QTY3;DCTEXCEP_CODE3;
		 * ORDER_DESC4;MEDI_QTY4;DCTEXCEP_CODE4
		 */
		if ("MEDI_QTY1".equalsIgnoreCase(column)) {
			// System.out.println("here in mediQty1");
			int index = row * 4 + 0;
			// System.out.println("parm.getRow"+parm.getRow(index));
			return getItemDouble(index, "MEDI_QTY");
		}
		if ("MEDI_QTY2".equalsIgnoreCase(column)) {
			int index = row * 4 + 1;
			return getItemDouble(index, "MEDI_QTY");
		}
		if ("MEDI_QTY3".equalsIgnoreCase(column)) {
			int index = row * 4 + 2;
			return getItemDouble(index, "MEDI_QTY");
		}
		if ("MEDI_QTY4".equalsIgnoreCase(column)) {
			int index = row * 4 + 3;
			return getItemDouble(index, "MEDI_QTY");
		}
		if ("DCTEXCEP_CODE1".equalsIgnoreCase(column)) {
			int index = row * 4 + 0;
			return getItemString(index, "DCTEXCEP_CODE");
		}
		if ("DCTEXCEP_CODE2".equalsIgnoreCase(column)) {
			int index = row * 4 + 1;
			return getItemString(index, "DCTEXCEP_CODE");
		}
		if ("DCTEXCEP_CODE3".equalsIgnoreCase(column)) {
			int index = row * 4 + 2;
			return getItemString(index, "DCTEXCEP_CODE");
		}
		if ("DCTEXCEP_CODE4".equalsIgnoreCase(column)) {
			int index = row * 4 + 3;
			return getItemString(index, "DCTEXCEP_CODE");
		}
		return "";
	}

	/**
	 * ��ʼ����ҩ
	 * 
	 * @param row
	 * @param parm
	 */
	public void initOrder(int row, TParm parm) {
		setItem(row, "PRESRT_NO", row + 1);
		setItem(row, "REGION_CODE", Operator.getRegion());
		setItem(row, "RELEASE_FLG", "N");
		setItem(row, "LINKMAIN_FLG", "N");
		setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
		setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
		setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC").replaceFirst(
				"(" + parm.getValue("SPECIFICATION") + ")", ""));
		setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION"));
		setItem(row, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE"));
		setItem(row, "OWN_PRICE", parm.getValue("OWN_PRICE"));
		setItem(row, "CHARGE_HOSP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
		setItem(row, "REXP_CODE", BIL.getRexpCode(parm
				.getValue("CHARGE_HOSP_CODE"), this.getAdmType()));
		setItem(row, "SETMAIN_FLG", "N");
		setItem(row, "ORDERSET_GROUP_NO", 0);
		double TAKE_QTY = parm.getDouble("MEDI_QTY", 0);
		setItem(row, "MEDI_QTY", 1.0);
		setItem(row, "DISPENSE_QTY", 1.0);
		setItem(row, "DOSAGE_QTY", 1.0);
		setItem(row, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
		setItem(row, "DISPENSE_UNIT", parm.getValue("UNIT_CODE"));
		setItem(row, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
		TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
				parm.getValue("ORDER_CODE"));
		if (parmBase.getErrCode() < 0) {
			return;
		}

		//double takeQty = (TAKE_QTY < 1.0) ? 1.0 : TAKE_QTY;
		setItem(row, "FREQ_CODE", parmBase.getValue("FREQ_CODE", 0));
		setItem(row, "ROUTE_CODE", parmBase.getValue("ROUTE_CODE", 0));
		String TAKE_DAY = parmBase.getValue("TAKE_DAYS", 0);
		int takedays = (TAKE_DAY == null || TAKE_DAY.trim().length() < 1 || "0"
				.equalsIgnoreCase(TAKE_DAY)) ? 1 : Integer.valueOf(TAKE_DAY);
		setItem(row, "TAKE_DAYS", takedays);
		setItem(row, "CTRLDRUGCLASS_CODE", parmBase.getValue(
				"CTRLDRUGCLASS_CODE", 0));
		setItem(row, "GIVEBOX_FLG", parmBase.getValue("GIVEBOX_FLG", 0));
		if ("Y".equalsIgnoreCase(parmBase.getValue("GIVEBOX_FLG", 0))) {
			setItem(row, "DOSAGE_UNIT", parmBase.getValue("STOCK_UNIT", 0));
			setItem(row, "DISPENSE_UNIT", parmBase.getValue("STOCK_UNIT", 0));
			setItem(row, "MEDI_UNIT", parmBase.getValue("STOCK_UNIT", 0));
		} else {
			setItem(row, "DOSAGE_UNIT", parmBase.getValue("DOSAGE_UNIT", 0));
			setItem(row, "DISPENSE_UNIT", parmBase.getValue("DOSAGE_UNIT", 0));
			setItem(row, "MEDI_UNIT", parmBase.getValue("MEDI_UNIT", 0));
		}

	}

	/**
	 * 
	 * @param rxType
	 *            ��������
	 * @return String �´���ǩ��
	 */
	public String newPrsrp(String rxType) {
		String rxNo = getNewRx();
		if (rxNo == null)
			return "";
		if (newOrder(rxType, rxNo) == -1)
			return "";
		return rxNo;
	}

	public boolean deletePrsrp(String rxType) {

		return true;
	}

	/**
	 * ����һ��ҽ��
	 * 
	 * @param rxType
	 *            ��������
	 * @param rxNo
	 *            ������
	 * @return int �����к�
	 */
	public int newOrder(String rxType, String rxNo) {
		int row = this.insertRow(-1);
		setItem(row, "RX_NO", rxNo);
		setItem(row, "RX_TYPE", rxType);
		setItem(row, "SETMAIN_FLG", "");
		setItem(row, "BILL_TYPE", "C");// Ĭ�����ֽ�����
		if ("3".equalsIgnoreCase(rxType)) {
			setItem(row, "PHA_TYPE", "G");
		}

		int max = 0;
		for (int i = 0; i < this.rowCount(); i++) {
			if (!rxNo.equals(getItemString(i, "RX_NO")))
				continue;
			if (!rxType.equals(getItemData(i, "RX_TYPE")))
				continue;
			int seqNo = getItemInt(i, "SEQ_NO");
			if (seqNo > max)
				max = seqNo;
		}
		max++;
		setItem(row, "SEQ_NO", max);
		// this.setFilter(filterString);
		// this.filter();
		return row;
	}

	/**
	 * ����һ�����ã���ô���Ϊ����ҽ��������뼯��ҽ������Ϊ�������뵥��
	 * 
	 * @param rxNo
	 * @param orderCode
	 * @return
	 */
	public int newOpOrder(String rxNo, String orderCode, String[] ctz, int row, boolean memFlg, String memId, String memTradeNo) {
		if (StringUtil.isNullString(rxNo) || StringUtil.isNullString(orderCode)) {
			this.deleteRow(row);
			return -1;
		}
		String sql = GET_ORDERSET.replaceFirst("#", orderCode);
		TParm orderSet = new TParm(TJDODBTool.getInstance().select(sql));
		if (orderSet.getErrCode() != 0) {
			// System.out.println("newOpOrder.getErrSql="+orderSet.getErrText());
			// System.out.println("wrong Sql="+sql);
			this.deleteRow(row);
			return -1;
		}
		sql = GET_SYS_FEE.replaceFirst("#", orderCode);
		TParm sysFee = new TParm(TJDODBTool.getInstance().select(sql));
		if (sysFee.getErrCode() != 0 || sysFee.getCount() != 1) {
			this.deleteRow(row);
			return -1;
		}
		sysFee = sysFee.getRow(0);
		int count = orderSet.getCount();
		//TParm sys = new TParm();
		if (count <= 0) {// �Ǽ���ҽ��
			if (!insertRowParm(sysFee, row, ctz)) {
				this.deleteRow(row);
				return -1;
			}
			this.setItem(row, "SETMAIN_FLG", "N");
			this.setItem(row, "HIDE_FLG", "N");// ����ע��
			this.setActive(row, true);
		} else {// ����ҽ��
			if (!insertRowParm(sysFee, row, ctz)) {
				this.deleteRow(row);
				return -1;
			}
			this.setActive(row, true);
			int groupNo = getMaxGroupNo();
			this.setItem(row, "SETMAIN_FLG", "Y");
			this.setItem(row, "HIDE_FLG", "N");// ����ע��
			String orderSetCode = orderCode;
			this.setItem(row, "ORDERSET_CODE", orderSetCode);
			this.setItem(row, "ORDERSET_GROUP_NO", groupNo);
			//zhangp
			TParm memPackgeParm = new TParm();
			if(memFlg){
				memPackgeParm = ODOTool.getInstance().selectByOrderSetCodeMemPackage(memId, memTradeNo);
			}
			for (int i = 0; i < count; i++) {
				row = this.newOrder("4", rxNo);
				orderCode = orderSet.getValue("ORDER_CODE", i);
				sql = GET_SYS_FEE.replaceFirst("#", orderCode);
				sysFee = new TParm(TJDODBTool.getInstance().select(sql));
				if (sysFee.getErrCode() != 0 || sysFee.getCount() != 1) {
					this.deleteRow(row);
					return -1;
				}
				sysFee = sysFee.getRow(0);
				insertRowParm(sysFee, row, ctz);
				this.setItem(row, "SETMAIN_FLG", "N");
				this.setItem(row, "HIDE_FLG", orderSet.getData("HIDE_FLG", i));
				this.setItem(row, "ORDERSET_CODE", orderSetCode);
				this.setItem(row, "ORDERSET_GROUP_NO", groupNo);
				this
						.setItem(row, "MEDI_QTY", orderSet.getData(
								"DOSAGE_QTY", i));
				this.setItem(row, "DOSAGE_QTY", orderSet.getData("DOSAGE_QTY",
						i));
				this.setItem(row, "DISPENSE_QTY", orderSet.getData(
						"DOSAGE_QTY", i));
				double ownAmt = roundAmt(StringTool.round(this.getItemDouble(
						row, "OWN_PRICE")
						* this.getItemDouble(row, "DOSAGE_QTY"), 2));
				double arAmt = roundAmt(BIL
						.chargeTotCTZ(ctz[0], ctz[1], ctz[2], sysFee
								.getValue("ORDER_CODE"), this.getItemDouble(
								row, "DOSAGE_QTY"), serviceLevel));
				// DISCOUNT_RATE
				this.setItem(row, "DISCOUNT_RATE", BIL.getOwnRate(ctz[0],
						ctz[1], ctz[2], sysFee
								.getValue("CHARGE_HOSP_CODE"), sysFee.getValue("ORDER_CODE")));
				this.setItem(row, "OWN_AMT", ownAmt);
				// System.out.println("ownAmt="+ownAmt);
				this.setItem(row, "AR_AMT", arAmt);
				if(memFlg){
					for (int j = 0; j < memPackgeParm.getCount(); j++) {
						if(orderCode.equals(memPackgeParm.getValue("ORDER_CODE", j))){
							this.setItem(row, "AR_AMT", memPackgeParm.getDouble("RETAIL_PRICE", j));	
							this.setItem(row, "MEM_PACKAGE_ID", memId);
							this.setItem(row, "MEM_PACKAGE_FLG", "Y"); //add by huangtt 20150130  
						}
					}
				}
				// System.out.println("arAmt="+arAmt);
				this.setItem(row, "PAYAMOUNT", ownAmt - arAmt);
				this.setActive(row, true);
			}
		}
		return row;
	}

	/**
	 * ������󼯺�ҽ�����+1
	 * 
	 * @return int
	 */
	public int getMaxGroupNo() {
		int count = this.rowCount();
		int groupNo = 0;
		int oldNo = 0;
		for (int i = 0; i < count; i++) {
			if (!"Y".equalsIgnoreCase(this.getItemString(i, "SETMAIN_FLG")))
				continue;

			groupNo = this.getItemInt(i, "ORDERSET_GROUP_NO");
			if (groupNo > oldNo)
				oldNo = groupNo;

		}
		oldNo += 1;
		return oldNo;
	}

	/**
	 * ���ݸ��������޸�һ������
	 * 
	 * @param sysFee
	 * @param row
	 * @return
	 */
	private boolean insertRowParm(TParm sysFee, int row, String[] ctz) {
		System.out.println("������Ŀ������sysFee sysFee is����"+sysFee);
		if (row < 0) {
			return false;
		}
		if (sysFee == null
				|| StringUtil.isNullString(sysFee.getValue("ORDER_CODE"))) {
			return false;
		}
		Timestamp now = this.getDBTime();
		// ORDER_CODE
		this.setItem(row, "ORDER_CODE", sysFee.getValue("ORDER_CODE"));
		// ORDER_DESC
		this.setItem(row, "ORDER_DESC", sysFee.getValue("ORDER_DESC"));
		// GOODS_DESC
		this.setItem(row, "GOODS_DESC", sysFee.getValue("GOODS_DESC"));
		// SPECIFICATION
		this.setItem(row, "SPECIFICATION", sysFee.getValue("SPECIFICATION"));
		// ORDER_CAT1_CODE
		this.setItem(row, "ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
		// CAT1_TYPE
		this.setItem(row, "CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
		// DESCRIPTION
		this.setItem(row, "DR_NOTE", sysFee.getValue("DESCRIPTION"));
		this.setItem(row, "CTZ1_CODE", ctz[0]);
		this.setItem(row, "CTZ2_CODE", ctz[1]);
		this.setItem(row, "CTZ3_CODE", ctz[2]);
		this.itemNow = true;
		// MEDI_QTY
		this.setItem(row, "MEDI_QTY", 1);
		// MEDI_UNIT
		this.setItem(row, "MEDI_UNIT", sysFee.getValue("UNIT_CODE"));
		this.itemNow = true;
		// FREQ_CODE
		this.setItem(row, "FREQ_CODE", "PRN");
		// ROUTE_CODE
		this.setItem(row, "ROUTE_CODE", "");
		this.itemNow = true;
		// TAKE_DAYS
		this.setItem(row, "TAKE_DAYS", 1);
		this.itemNow = true;
		// DOSAGE_QTY
		this.setItem(row, "DOSAGE_QTY", 1);
		// DOSAGE_UNIT
		this.setItem(row, "DOSAGE_UNIT", sysFee.getValue("UNIT_CODE"));
		this.itemNow = true;
		// DISPENSE_QTY
		this.setItem(row, "DISPENSE_QTY", 1);
		// DISPENSE_UNIT
		this.setItem(row, "DISPENSE_UNIT", sysFee.getValue("UNIT_CODE"));
		// OWN_PRICE
		if ("2".equals(serviceLevel)) {
			this.setItem(row, "OWN_PRICE", sysFee.getData("OWN_PRICE2"));
		} else if ("3".equals(serviceLevel)) {
			this.setItem(row, "OWN_PRICE", sysFee.getData("OWN_PRICE3"));
		} else
			this.setItem(row, "OWN_PRICE", sysFee.getData("OWN_PRICE"));
		// NHI_PRICE
		this.setItem(row, "GOODS_DESC", sysFee.getData("GOODS_DESC"));
		// System.out.println("chargeHospCode="+sysFee.getValue("CHARGE_HOSP_CODE"));
		// System.out.println("rexpCode="+BIL.getRexpCode(sysFee
		// .getValue("CHARGE_HOSP_CODE")));
		this.setItem(row, "HEXP_CODE", sysFee.getValue("CHARGE_HOSP_CODE"));
		this.setItem(row, "REXP_CODE", BIL.getRexpCode(sysFee
				.getValue("CHARGE_HOSP_CODE"), this.getAdmType()));

		// DR_CODE
		this.setItem(row, "DR_CODE", Operator.getID());
		// ORDER_DATE
		this.setItem(row, "ORDER_DATE", now);
		String dept = sysFee.getValue("EXEC_DEPT_CODE");
		if (StringUtil.isNullString(dept)) {
			dept = Operator.getDept();
		}
		//EXEC_FLG zhangp 20140108
		String execFlg = "N";
		if(sysFee.getBoolean("EXEC_ORDER_FLG")){
			execFlg = sysFee.getValue("EXEC_ORDER_FLG");
			this.setItem(row, "EXEC_DATE", this.getDBTime());
			this.setItem(row, "EXEC_DR_CODE", Operator.getID());
			dept = Operator.getCostCenter();//====yanjing ������ִ�У�ִ�п���ȡ�ɱ����ĵ�ֵ
		}else if(sysFee.getValue("CAT1_TYPE").equals("TRT")){//�ǿ�����ִ�е�������Ŀ��ִ�п���ȡ���Ҷ�Ӧ������ִ�п���
			dept = this.setOpexecDept(caseNo);
		}
		this.setItem(row, "EXEC_FLG", execFlg);
		// EXEC_DEPT_CODE
		this.setItem(row, "EXEC_DEPT_CODE", dept);
		// zhangyong20110616
		// COST_CENTER_CODE
		this.setItem(row, "COST_CENTER_CODE", dept);
		// DEGREE_CODE
		this.setItem(row, "DEGREE_CODE", sysFee.getValue("DEGREE_CODE"));
		// INSPAY_TYPE
		this.setItem(row, "INSPAY_TYPE", "C");
		// EXPENSIVE_FLG
		// REXP_CODE
		this.setItem(row, "GOODS_DESC", sysFee.getValue("ORDER_DESC"));
		// HEXP_CODE
		this.setItem(row, "GOODS_DESC", sysFee.getValue("GOODS_DESC"));
		// CAT1_TYPE
		this.setItem(row, "CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
		// TRADE_ENG_DESC
		this.setItem(row, "TRADE_ENG_DESC", sysFee.getValue("TRADE_ENG_DESC"));
		// System.out.println("dosageQty="+this.getItemDouble(row,
		// "DOSAGE_QTY"));
		// System.out.println("ownPrice="+this.getItemDouble(row, "OWN_PRICE"));
		double ownAmt = roundAmt(StringTool.round(this.getItemDouble(row,
				"OWN_PRICE")
				* this.getItemDouble(row, "DOSAGE_QTY"), 2));
		double arAmt = roundAmt(BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2], sysFee
				.getValue("ORDER_CODE"), this.getItemDouble(row, "DOSAGE_QTY"),
				serviceLevel));
		// DISCOUNT_RATE
		this.setItem(row, "DISCOUNT_RATE", BIL.getOwnRate(ctz[0], ctz[1],
				ctz[2], sysFee.getValue("CHARGE_HOSP_CODE"), sysFee
				.getValue("ORDER_CODE")));
		this.setItem(row, "OWN_AMT", ownAmt);
		// System.out.println("ownAmt="+ownAmt);
		this.setItem(row, "AR_AMT", arAmt);
		// System.out.println("arAmt="+arAmt);
		this.setItem(row, "PAYAMOUNT", ownAmt - arAmt);

		return true;

	}
	private String setOpexecDept(String caseNo){
		String opExecdept = "";
		 //���ݾ���Ų�ѯ������ִ�п���
 	   String selectSql = "SELECT A.CLINICAREA_CODE,B.MED_EXEC_DEPT FROM REG_PATADM A ,REG_CLINICROOM B " +
 	   		"WHERE A.CLINICAREA_CODE = B.CLINICAREA_CODE AND A.CLINICROOM_NO = B.CLINICROOM_NO AND A.CASE_NO = '"+caseNo+"' ";
// 	   System.out.println("��������� sql sql is����"+selectSql);
 	   TParm parm = new TParm(TJDODBTool.getInstance().select(selectSql));
 	  opExecdept = parm.getValue("MED_EXEC_DEPT",0);
 	   return opExecdept;
	}

	/**
	 * ɾ��һ��ҽ��
	 * 
	 * @param rxType
	 *            ��������
	 * @param rxNo
	 *            ������
	 * @return
	 */
	public boolean deleteOrder(String rxType, String rxNo) {

		return true;
	}

	/**
	 * �ж�ҽ���Ƿ���order_code���Դ����жϴ���ҽ���Ƿ��Ѿ�������
	 * 
	 * @param rxType
	 *            ��������
	 * @param rxNo
	 *            ������
	 * @return boolean true:��ҽ����false:�Ѿ���������ҽ��
	 */
	public boolean isNullOrder(String rxType, String rxNo) {
		String filterString = "RX_NO='" + rxNo + "' AND RX_TYPE='" + rxType
				+ "' ";
		this.setFilter(filterString);
		if (!this.filter()) {
			// //System.out.println("isNullOrder.filter failed");
			return false;
		}

		String orderCode = this
				.getItemString(this.rowCount() - 1, "ORDER_CODE");
		boolean result = (orderCode == null || orderCode.trim().length() < 1);
		return result;
	}

	/**
	 * ��ȡ����ǩ��Ӧҽ����Ŀ
	 * 
	 * @param rxType
	 *            ��������
	 * @param rxNo
	 *            ������
	 * @return boolean true:��ҽ����false:�Ѿ���������ҽ��
	 */
	public int getCountByRXNo(String rxType, String rxNo) {
		String filterString = "RX_NO='" + rxNo + "' AND RX_TYPE='" + rxType
				+ "' ";
		this.setFilter(filterString);
		if (!this.filter()) {
			// //System.out.println("isNullOrder.filter failed");
			return 0;
		}

		return this.rowCount() - 1;
		// String filterString = "RX_NO='" + rxNo + "' AND RX_TYPE='" + rxType
		// + "' AND CASE_NO='" + caseNo
		// + "' ";
		// TParm result = new
		// TParm(TJDODBTool.getInstance().select("SELECT COUNT(*) COUNT FROM OPD_ORDER WHERE "+filterString));
		//		
		//		
		// return result.getInt(0, 0);
	}

	/**
	 * �е�ֵ�ı�
	 */
	public boolean setItem(int row, String column, Object value) {
		super.setItem(row, column, value);
		if (itemNow)
			return true;
		if ("MEDI_QTY".equalsIgnoreCase(column)
				|| "TAKE_DAYS".equalsIgnoreCase(column)
				|| "FREQ_CODE".equalsIgnoreCase(column)
				|| "GIVEBOX_FLG".equalsIgnoreCase(column)) {
			TParm parm = this.getRowParm(row);
			//add by huangtt 20150415 start
			if("Y".equals(this.getItemString(row, "MEM_PACKAGE_FLG"))){
				return true;
			}
			//end by huangtt 20150415 end
			TotQtyTool qty = new TotQtyTool();
			if (!TCM_Transform
					.getBoolean(this.getItemData(row, "LINKMAIN_FLG"))) {
				TParm qtyParm = qty.getTotQty(parm);// ��������//ǰ̨����
				// TParm qtyParm = new
				// TParm(PHAStrike.getInstance().getTotQty(parm.getData()));//��̨����
				itemNow = true;
				if (("GIVEBOX_FLG".equalsIgnoreCase(column) && StringTool
						.getBoolean((String) value))
						|| (!"GIVEBOX_FLG".equalsIgnoreCase(column) && StringTool
								.getBoolean(this.getItemString(row,
										"GIVEBOX_FLG")))) {
					itemNow = true;
					// System.out.println("����1��"+this.getItemData(row,
					// "ORDER_DESC"));
					// System.out.println("TOT_QTY:"+qtyParm.getDouble("TOT_QTY"));
					// System.out.println("STOCK_UNIT:"+qtyParm.getDouble("STOCK_UNIT"));
					this.setItem(row, "DISPENSE_QTY", qtyParm
							.getDouble("QTY_FOR_STOCK_UNIT"));
					this.setItem(row, "DISPENSE_UNIT", qtyParm
							.getValue("STOCK_UNIT"));
					itemNow = true;
					this.setItem(row, "DOSAGE_QTY", qtyParm
							.getDouble("TOT_QTY"));
					this.setItem(row, "DOSAGE_UNIT", qtyParm
							.getValue("DOSAGE_UNIT"));

				} else {
					itemNow = true;
					// System.out.println("����2��" + this.getItemData(row,
					// "ORDER_DESC"));
					// System.out.println("TOT_QTY:" +
					// qtyParm.getDouble("TOT_QTY"));
					// System.out.println("DOSAGE_UNIT:" +
					// qtyParm.getDouble("DOSAGE_UNIT"));
					this.setItem(row, "DOSAGE_QTY", qtyParm.getDouble("QTY"));
					this.setItem(row, "DOSAGE_UNIT", qtyParm
							.getValue("DOSAGE_UNIT"));
					this.setItem(row, "DISPENSE_QTY", qtyParm.getDouble("QTY"));
					this.setItem(row, "DISPENSE_UNIT", qtyParm
							.getValue("DOSAGE_UNIT"));
					// �ж��Ƿ��Ǽ���ҽ������ �����������ô���������������
					if (TypeTool.getBoolean(this
							.getItemData(row, "SETMAIN_FLG"))) {
						String orderSetCode = this.getItemString(row,
								"ORDER_CODE");
						String groupID = this.getItemString(row,
								"ORDERSET_GROUP_NO");
						String rxNo = this.getItemString(row, "RX_NO");
						// ѭ���������������
						for (int k = 0; k < this.rowCountFilter(); k++) {
							// �ж��Ƿ��Ǹü���ҽ��������
							if (orderSetCode.equals(TypeTool.getString(this
									.getItemData(k, "ORDERSET_CODE",
											this.FILTER)))
									&& groupID.equals(TypeTool.getString(this
											.getItemData(k,
													"ORDERSET_GROUP_NO",
													this.FILTER)))
									&& rxNo.equals(TypeTool.getString(this
											.getItemData(k, "RX_NO",
													this.FILTER)))
									&& !TypeTool.getBoolean(this.getItemData(k,
											"SETMAIN_FLG", this.FILTER))) {
								// ϸ���Ĭ������
								double osTot = this.getOrderSetTot(TypeTool
										.getString(this.getItemData(k,
												"ORDER_CODE", this.FILTER)),
										TypeTool.getString(this.getItemData(k,
												"ORDERSET_CODE", this.FILTER)));
								this
										.setItem(k, "DOSAGE_QTY", osTot
												* qtyParm.getDouble("QTY"),
												this.FILTER);
								this.setItem(k, "DOSAGE_UNIT", qtyParm
										.getValue("DOSAGE_UNIT"), this.FILTER);
								this
										.setItem(k, "DISPENSE_QTY", osTot
												* qtyParm.getDouble("QTY"),
												this.FILTER);
								this.setItem(k, "DISPENSE_UNIT", qtyParm
										.getValue("DOSAGE_UNIT"), this.FILTER);
								// ����޸ĵ������� ��ôϸ�������ҲҪ�����������һ���޸�
								if ("MEDI_QTY".equalsIgnoreCase(column)) {
									this.setItem(k, "MEDI_QTY", TypeTool
											.getInt(value)
											* osTot, this.FILTER);
								}
							}
						}
					}
				}
				// �ж��Ƿ��Ǽ���ҽ������ �����������ô����������ļ۸�ϼ�
				if (TypeTool.getBoolean(this.getItemData(row, "SETMAIN_FLG"))) {
					String orderSetCode = this.getItemString(row, "ORDER_CODE");
					String groupID = this.getItemString(row,
							"ORDERSET_GROUP_NO");
					String rxNo = this.getItemString(row, "RX_NO");
					// ѭ����������ķ���֮��
					for (int k = 0; k < this.rowCountFilter(); k++) {
						// �ж��Ƿ��Ǹü���ҽ��������
						if (orderSetCode.equals(TypeTool.getString(this
								.getItemData(k, "ORDERSET_CODE", this.FILTER)))
								&& groupID.equals(TypeTool.getString(this
										.getItemData(k, "ORDERSET_GROUP_NO",
												this.FILTER)))
								&& rxNo.equals(TypeTool.getString(this
										.getItemData(k, "RX_NO", this.FILTER)))
								&& !TypeTool.getBoolean(this.getItemData(k,
										"SETMAIN_FLG", this.FILTER))) {
							this.setItem(k, "OWN_AMT", roundAmt(TypeTool
									.getDouble(this.getItemData(k, "OWN_PRICE",
											this.FILTER))
									* TypeTool.getDouble(this.getItemData(k,
											"DOSAGE_QTY", this.FILTER))),
									this.FILTER);
							this.setItem(k, "AR_AMT", roundAmt(BIL
									.chargeTotCTZ(this.getItemString(row,
											"CTZ1_CODE"), this.getItemString(
											row, "CTZ2_CODE"), this
											.getItemString(row, "CTZ3_CODE"),
											TypeTool.getString(this
													.getItemData(k,
															"ORDER_CODE",
															this.FILTER)),
											TypeTool.getDouble(this
													.getItemData(k,
															"DOSAGE_QTY",
															this.FILTER)),
											serviceLevel)), this.FILTER);
						}
					}
				} else {// ���Ǽ���ҽ�� ��ô��ȡ����ļ۸�
					// System.out.println("DOSAGE_QTY:"+this.getItemDouble(row,
					// "DOSAGE_QTY"));
					// System.out.println("OWN_PRICE:"+this.getItemDouble(row,
					// "OWN_PRICE"));
					this.setItem(row, "OWN_AMT", roundAmt(this.getItemDouble(
							row, "OWN_PRICE")
							* this.getItemDouble(row, "DOSAGE_QTY")));
					this.setItem(row, "AR_AMT", roundAmt(BIL.chargeTotCTZ(this
							.getItemString(row, "CTZ1_CODE"), this
							.getItemString(row, "CTZ2_CODE"), this
							.getItemString(row, "CTZ3_CODE"), this
							.getItemString(row, "ORDER_CODE"), this
							.getItemDouble(row, "DOSAGE_QTY"), serviceLevel)));
				}
				// System.out.println("�鿴�ܼ�:");
				// this.showDebug();
				itemNow = false;
				return true;
			} else {
				itemNow = true;
				// String rxNo=this.getItemString(row, "RX_NO");
				int linkNo = StringTool.getInt(this.getItemString(row,
						"LINK_NO"));
				for (int i = 0; i < this.rowCount(); i++) {
					int temp = StringTool.getInt(this.getItemString(i,
							"LINK_NO"));
					// boolean
					// linkM=TCM_Transform.getBoolean(this.getItemData(i,
					// "LINKMAIN_FLG"));
					if (linkNo != temp) {
						continue;
					}
					if ("TAKE_DAYS".equalsIgnoreCase(column)) {
						this.setItem(i, "TAKE_DAYS", TCM_Transform
								.getInt(value));
					} else if ("FREQ_CODE".equalsIgnoreCase(column)) {
						this.setItem(i, "FREQ_CODE", TCM_Transform
								.getString(value));
					}
					TParm parm1 = this.getRowParm(i);
					// System.out.println("parm1~~~~~~~~~~~~~~~~:"+parm1);
					TParm qtyParm = qty.getTotQty(parm1);// ǰ̨����
					// System.out.println("qtyParm~~~~~~~~~~~~~~~:"+qtyParm);
					// TParm qtyParm = new
					// TParm(PHAStrike.getInstance().getTotQty(parm1.getData()));//��̨����
					if (StringTool.getBoolean(this.getItemString(i,
							"GIVEBOX_FLG"))) {
						this.setItem(i, "DISPENSE_QTY", qtyParm
								.getDouble("QTY_FOR_STOCK_UNIT"));
						this.setItem(i, "DISPENSE_UNIT", qtyParm
								.getValue("STOCK_UNIT"));
						this.setItem(i, "DOSAGE_QTY", qtyParm
								.getDouble("TOT_QTY"));
						this.setItem(i, "DOSAGE_UNIT", qtyParm
								.getValue("DOSAGE_UNIT"));
					} else {
						this.setItem(i, "DOSAGE_QTY", qtyParm.getDouble("QTY"));
						this.setItem(i, "DOSAGE_UNIT", qtyParm
								.getValue("DOSAGE_UNIT"));
						this.setItem(i, "DISPENSE_QTY", qtyParm
								.getDouble("QTY"));
						this.setItem(i, "DISPENSE_UNIT", qtyParm
								.getValue("DOSAGE_UNIT"));
					}
					this.setItem(i, "OWN_AMT", roundAmt(this.getItemDouble(i,
							"OWN_PRICE")
							* this.getItemDouble(i, "DOSAGE_QTY")));
					this.setItem(i, "AR_AMT", roundAmt(BIL.chargeTotCTZ(this
							.getItemString(i, "CTZ1_CODE"), this.getItemString(
							i, "CTZ2_CODE"),
							this.getItemString(i, "CTZ3_CODE"), this
									.getItemString(i, "ORDER_CODE"), this
									.getItemDouble(i, "DOSAGE_QTY"),
							serviceLevel)));
					this.addOtherShowRowList(i);
				}
				itemNow = false;
				return true;
			}

		}
		// ������������
		if ("DISPENSE_QTY".equalsIgnoreCase(column)) {
			if(opdSysParm == null){
				opdSysParm = new TParm(TJDODBTool.getInstance().select(SQL_OPD_SYSPARM));
				opdSysParm = opdSysParm.getRow(0);
			}
			boolean isboxDispense = false;
			if(opdSysParm.getBoolean("BOX_DISPENSE_FLG") && !"Y".equals(getItemData(row, "GIVEBOX_FLG"))){
				isboxDispense = true;
			}
			itemNow = true;
			TParm parm = this.getRowParm(row);
			parm.setData("DOSAGE_QTY", value);
			TotQtyTool qty = new TotQtyTool();
			TParm qtyParm = qty.getTakeQty(parm);
			double qtyDb = qtyParm.getDouble("QTY");
			// System.out.println("����ҩƷ����=="+qtyParm);
			
			itemNow = true;
			double dosageQty = TypeTool.getDouble(value);
			if (qtyParm.getData("SUM_QTY") != null) {
				dosageQty = qtyParm.getDouble("SUM_QTY");
			}
			// �ж������Ƿ����޸�
			boolean isMedi = false;
			if (qtyDb != this.getItemDouble(row, "MEDI_QTY")) {
				isMedi = true;
			}
			if(isboxDispense){
				this.setItem(row, "MEDI_QTY", qtyDb);
			}
			this.setItem(row, "DOSAGE_QTY", dosageQty);
			double totQty = TypeTool.getDouble(dosageQty);
			// �ж��Ƿ��Ǽ���ҽ������ �����������ô����������ļ۸�ϼ�
			if (TypeTool.getBoolean(this.getItemData(row, "SETMAIN_FLG"))) {
				String orderSetCode = this.getItemString(row, "ORDER_CODE");
				String groupID = this.getItemString(row, "ORDERSET_GROUP_NO");
				String rxNo = this.getItemString(row, "RX_NO");
				// ѭ����������ķ���֮��
				for (int k = 0; k < this.rowCountFilter(); k++) {
					// �ж��Ƿ��Ǹü���ҽ��������
					if (orderSetCode.equals(TypeTool.getString(this
							.getItemData(k, "ORDERSET_CODE", this.FILTER)))
							&& groupID.equals(TypeTool.getString(this
									.getItemData(k, "ORDERSET_GROUP_NO",
											this.FILTER)))
											&& rxNo.equals(TypeTool.getString(this.getItemData(
													k, "RX_NO", this.FILTER)))
													&& !TypeTool.getBoolean(this.getItemData(k,
															"SETMAIN_FLG", this.FILTER))) {
						// ϸ���Ĭ������
						double osTot = this.getOrderSetTot(TypeTool
								.getString(this.getItemData(k, "ORDER_CODE",
										this.FILTER)), TypeTool.getString(this
												.getItemData(k, "ORDERSET_CODE", this.FILTER)));
						// �޸����������
						this.setItem(k, "DOSAGE_QTY", osTot * dosageQty,
								this.FILTER);
						this.setItem(k, "DOSAGE_UNIT", qtyParm
								.getValue("DOSAGE_UNIT"), this.FILTER);
						this.setItem(k, "DISPENSE_QTY", osTot * dosageQty,
								this.FILTER);
						this.setItem(k, "DISPENSE_UNIT", qtyParm
								.getValue("DOSAGE_UNIT"), this.FILTER);
						if (isMedi) {// ����������޸������޸�����
							this.setItem(k, "MEDI_QTY", osTot * qtyDb,
									this.FILTER); // �޸���������
						}
						// �޸�����ļ۸�
						this.setItem(k, "OWN_AMT", roundAmt(TypeTool
								.getDouble(this.getItemData(k, "OWN_PRICE",
										this.FILTER))
										* TypeTool.getDouble(this.getItemData(k,
												"DOSAGE_QTY", this.FILTER))),
												this.FILTER);
						this.setItem(k, "AR_AMT", roundAmt(BIL.chargeTotCTZ(
								this.getItemString(row, "CTZ1_CODE"), this
								.getItemString(row, "CTZ2_CODE"), this
								.getItemString(row, "CTZ3_CODE"),
								TypeTool.getString(this.getItemData(k,
										"ORDER_CODE", this.FILTER)), TypeTool
										.getDouble(this.getItemData(k,
												"DOSAGE_QTY", this.FILTER)),
												serviceLevel)), this.FILTER);
					}
				}
			} else {
				this.setItem(row, "OWN_AMT", roundAmt(this.getItemDouble(row,
				"OWN_PRICE")
				* totQty));
				this.setItem(row, "AR_AMT", roundAmt(BIL.chargeTotCTZ(this
						.getItemString(row, "CTZ1_CODE"), this.getItemString(
								row, "CTZ2_CODE"),
								this.getItemString(row, "CTZ3_CODE"), this
								.getItemString(row, "ORDER_CODE"), totQty,
								serviceLevel)));
			}
			itemNow = false;
			return true;
		}
		// ����ҽ������
		if ("LINKMAIN_FLG".equalsIgnoreCase(column)) {
			itemNow = true;
			boolean isMain = TypeTool.getBoolean(value);
			if (isMain) {
				int maxLinkNo = getMaxLinkNo();
				// ////System.out.println("maxLinkNo========="+maxLinkNo);
				int linkNo = getMaxLinkNo() + 1;
				this.setItem(row, "LINKMAIN_FLG", "Y");
				itemNow = true;
				String doseType = ""+getItemData(row, "DOSE_TYPE");
				for (int i = row; i < this.rowCount(); i++) {
					// if(!StringUtil.isNullString(this.getItemString(i,
					// "ORDER_CODE"))){
					if(doseType.equals(""+getItemData(i, "DOSE_TYPE")) || getItemString(i, "ORDER_CODE").length() == 0){
						this.setItem(i, "LINK_NO", linkNo);
					}
					itemNow = true;
					// }
				}
			} else {
				if (this.getItemInt(row, "LINK_NO") > 0) {
					int linktemp = this.getItemInt(row, "LINK_NO");
					for (int i = 0; i < this.rowCount(); i++) {
						if (linktemp == this.getItemInt(i, "LINK_NO")) {
							this.setItem(i, "LINK_NO", "");
							itemNow = true;
							this.setItem(i, "LINKMAIN_FLG", "N");
							itemNow = true;
						}
					}
				}
			}
			itemNow = false;
			return true;

		}
		// �������������޸� ��ô��������
		// if("LINK_NO".equalsIgnoreCase(column)){
		// System.out.println("111111");
		// this.setSort("LINK_NO,SEQ_NO");
		// this.sort();
		// System.out.println("orderby:"+this.getOrderBy());
		// System.out.println("sort:"+this.getSort());
		// this.showDebug();
		// }
		// zhangyong20110616
		if ("EXEC_DEPT_CODE".equalsIgnoreCase(column)) {
			itemNow = true;
			// �ж��Ƿ��Ǽ���ҽ������ �����������ô����������ļ۸�ϼ�
			if (TypeTool.getBoolean(this.getItemData(row, "SETMAIN_FLG"))) {
				String orderSetCode = this.getItemString(row, "ORDER_CODE");
				String groupID = this.getItemString(row, "ORDERSET_GROUP_NO");
				String rxNo = this.getItemString(row, "RX_NO");
				this.setItem(row, "EXEC_DEPT_CODE", value);
				this.setItem(row, "COST_CENTER_CODE", DeptTool.getInstance()
						.getCostCenter(value.toString(), ""));
				for (int k = 0; k < this.rowCountFilter(); k++) {
					// �ж��Ƿ��Ǹü���ҽ��������
					if (orderSetCode.equals(TypeTool.getString(this
							.getItemData(k, "ORDERSET_CODE", this.FILTER)))
							&& groupID.equals(TypeTool.getString(this
									.getItemData(k, "ORDERSET_GROUP_NO",
											this.FILTER)))
							&& rxNo.equals(TypeTool.getString(this.getItemData(
									k, "RX_NO", this.FILTER)))
							&& !TypeTool.getBoolean(this.getItemData(k,
									"SETMAIN_FLG", this.FILTER))) {
						this.setItem(k, "EXEC_DEPT_CODE", value, this.FILTER);
						this.setItem(k, "COST_CENTER_CODE", DeptTool
								.getInstance().getCostCenter(value.toString(),
										""), this.FILTER);
						// System.out.println("-------"+this.getRowParm(k,this.FILTER));
						itemNow = true;
					}
				}
			}
		}
		
		if("IS_PRE_ORDER".equalsIgnoreCase(column)){
			TParm parm = this.getRowParm(row);
			itemNow = true;
			//
			this.setItem(row, "IS_PRE_ORDER", value);
			
//			this.setItem(row, "CASE_NO", value);
			/*med.deleteRowBy((String) appNo.get(labMapKey), this
					.getItemString(i, "RX_NO"), this
					.getItemInt(i, "SEQ_NO"), this.getItemString(i,
					"CAT1_TYPE"));*/
			
			itemNow = false;
			return true;
		}
		
		if("PRE_DATE".equalsIgnoreCase(column)){
			TParm parm = this.getRowParm(row);
			itemNow = true;
			//
			this.setItem(row, "PRE_DATE", value);
			
//			this.setItem(row, "CASE_NO", value);
			/*med.deleteRowBy((String) appNo.get(labMapKey), this
					.getItemString(i, "RX_NO"), this
					.getItemInt(i, "SEQ_NO"), this.getItemString(i,
					"CAT1_TYPE"));*/
			
			itemNow = false;
			return true;
		}
		
		// itemNow = false;
		// return true;
		// }

		return true;
	}

	/**
	 * ȡ��󴦷�ǩ�ڵ����Ӻ�
	 * 
	 * @param order
	 * @return
	 */
	public int getMaxLinkNo() {
		int linkNo = 0;
		for (int i = 0; i < this.rowCount(); i++) {
			if (StringUtil.isNullString(this.getItemString(i, "ORDER_CODE")))
				continue;
			int temp = this.getItemInt(i, "LINK_NO");
			if (temp > linkNo)
				linkNo = temp;
		}
		return linkNo;
	}

	/**
	 * ���ؼ���ҽ��ϸ���TParm��ʽ
	 * 
	 * @return result TParm
	 */
	public TParm getOrderSetDetails(int groupNo, String orderSetCode) {
		TParm result = new TParm();

		if (groupNo < 0) {
			// //System.out.println("OpdOrder->getOrderSetDetails->groupNo is invalie");
			return result;
		}
		if (StringUtil.isNullString(orderSetCode)) {
			// //System.out.println("OpdOrder->getOrderSetDetails->orderSetCode is invalie");
			return result;
		}
		TParm parm = this.getBuffer(this.FILTER);

		int count = parm.getCount();
		if (count < 0) {
			// //System.out.println("OpdOrder->getOrderSetDetails->count <  0");
			return result;
		}
		String tempCode;
		int tempNo;
		for (int i = 0; i < count; i++) {
			tempCode = parm.getValue("ORDERSET_CODE", i);
			tempNo = parm.getInt("ORDERSET_GROUP_NO", i);
			if (tempCode.equalsIgnoreCase(orderSetCode) && tempNo == groupNo
					&& !parm.getBoolean("SETMAIN_FLG", i)) {
				// ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
				result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
				result.addData("SPECIFICATION", parm.getValue("SPECIFICATION",
						i));
				result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
				result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				result.addData("OWN_PRICE", parm.getValue("OWN_PRICE", i));
				result.addData("OWN_AMT", parm.getValue("OWN_AMT", i));
				result.addData("EXEC_DEPT_CODE", parm.getValue(
						"EXEC_DEPT_CODE", i));
				// zhangyong20110616
				result.addData("COST_CENTER_CODE", parm.getValue(
						"COST_CENTER_CODE", i));
				result
						.addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE",
								i));
				result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
			}
		}
		return result;
	}

	/**
	 * �жϸ����ORDER_CODE�Ƿ��Ѿ�����
	 * 
	 * @param orderCode
	 *            String �����ORDER_CODE
	 * @return boolean true:����,false:û����
	 */
	public boolean isSameOrder(String orderCode) {
		// //System.out.println("isSameOrder-��orderCode="+orderCode);
		TParm parm = this.getBuffer(this.isFilter() ? this.FILTER
				: this.PRIMARY);
		Vector code = (Vector) parm.getData("ORDER_CODE");
		// //System.out.println("code="+code);
		// //System.out.println("result="+(code.indexOf(orderCode)>0));
		return code.indexOf(orderCode) > -1;
	}

	/**
	 * ���������ҽ���Ļ�ϸ���Ƶ�Σ����������ű���ֵ���������
	 * 
	 * @param linkNo
	 *            int ���Ӻ�
	 * @return
	 */
	public TParm getLinkMainParm(int linkNo) {
		TParm result = new TParm();
		if (linkNo < 1)
			return result;
		TParm parm = this.getBuffer(this.PRIMARY);
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			if (StringUtil.isNullString(parm.getValue("ORDER_CODE", i)))
				continue;
			if (parm.getBoolean("LINKMAIN_FLG", i)
					&& parm.getInt("LINK_NO", i) == linkNo) {
				result.setData("FREQ_CODE", parm.getValue("FREQ_CODE", i));
				result.setData("TAKE_DAYS", parm.getValue("TAKE_DAYS", i));
				result.setData("EXEC_DEPT_CODE", parm.getValue(
						"EXEC_DEPT_CODE", i));
				// zhangyong20110616
				result.addData("COST_CENTER_CODE", parm.getValue(
						"COST_CENTER_CODE", i));
				result.setData("ROUTE_CODE", parm.getValue("ROUTE_CODE", i));
				result.setData("INFLUTION_RATE", parm.getValue("INFLUTION_RATE", i));
			}
		}
		return result;
	}

	/**
	 * ȡ���޸ģ������������ļ����鴦����
	 * 
	 * @return
	 */
	public TParm getModifiedExaRx() {
		int mdfRow[] = new int[] {};
		List rx = new ArrayList();
		TParm result = new TParm();
		if (!this.isModified()) {
			return result;
		}
		String filter = this.getFilter();
		this.setFilter("");
		this.filter();
		// ===========pangben 2012-6-28 start ��ӻ�ɾ����������ǩ��ʾ
		TParm parm = this.getBuffer(this.PRIMARY);
		TParm delParm = this.getBuffer(this.DELETE);//======pangben 2013-4-9 
		int count = parm.getCount();
		ArrayList list = new ArrayList();
		// System.out.println("parm:"+parm);
		for (int i = 0; i < count; i++) {
			if (parm.getBoolean("#NEW#", i)||parm.getBoolean("#MODIFY#", i))//=====pangben 2013-3-20 �����޸Ĵ���ǩ��ʾ
				list.add(new Integer(i));
		}

		mdfRow = getIntArray(list);
		// ===========pangben 2012-6-28 stop
		// System.out.println("----------------------------end");
		for (int row : mdfRow) {
			// System.out.println("row111111="+row);
			String tempRx = this.getItemString(row, "RX_NO");
			// ���û�����鲻���ٱ���ʱ��ӡ
			if (!"5".equalsIgnoreCase(this.getItemString(row, "RX_TYPE"))) {
				continue;
			}
			if (!this.isActive(row)) {
				continue;
			}
			if ("5".equalsIgnoreCase(this.getItemString(row, "RX_TYPE"))
					&& !TypeTool.getBoolean(this
							.getItemData(row, "SETMAIN_FLG"))) {
				continue;
			}

			if (rx.indexOf(tempRx) < 0) {
				rx.add(tempRx);
				result.addData("RX_NO", tempRx);
				result.addData("RX_TYPE", this.getItemString(row, "RX_TYPE"));
				result.addData("MED_APPLY_NO", this.getItemString(row,
						"MED_APPLY_NO"));
				result.addData("SEQ_NO", this.getItemInt(row, "SEQ_NO"));
				result.addData("CAT1_TYPE", this
						.getItemString(row, "CAT1_TYPE"));
				result.addData("CASE_NO", this.getCaseNo());
				result.addData("ORDER_CAT1_CODE", this.getItemString(row,
						"ORDER_CAT1_CODE"));
				result.setCount(result.getCount("RX_NO"));
			}
		}
		//==========pangben 2013-4-9 ���ɾ����ҽ������ӡ��ǰ����ǩ����������˴���ǩ�е�ҽ���ͻ��ӡ
		int index=0;
		for (int i = 0; i <delParm.getCount("RX_NO"); i++) {
			// ���û�����鲻���ٱ���ʱ��ӡ
			if (!"5".equalsIgnoreCase(delParm.getValue("RX_TYPE", i))) {
				continue;
			}
			index++;
			printAddParm(result, delParm, i);
			result.addData("MED_APPLY_NO",  delParm.getValue("MED_APPLY_NO",i));
			result.addData("SEQ_NO", delParm.getValue("SEQ_NO",i));
			result.addData("CAT1_TYPE",  delParm.getValue("CAT1_TYPE",i));
			result.addData("CASE_NO", this.getCaseNo());
			result.addData("ORDER_CAT1_CODE",  delParm.getValue("ORDER_CAT1_CODE",i));
		}
		result.setCount(result.getCount("RX_NO")+index);
		this.setFilter(filter);
		this.filter();
		return result;
	}
	/**
	 * ====pangben 2013-4-9 ��ӡ����ǩɾ����ҽ�����ڵĴ���ǩҲҪ��ӡ
	 * @param result
	 * @param delParm
	 * @param i
	 */
	private void printAddParm(TParm result,TParm delParm,int i){
		result.addData("RX_NO", delParm.getValue("RX_NO",i));
		result.addData("RX_TYPE", delParm.getValue("RX_TYPE",i));
		
		
	}
	/**
	 * ȡ���޸ģ������������ļ����鴦����
	 * 
	 * @return
	 */
	public TParm getModifiedExaOrder() {
		int mdfRow[] = new int[] {};
		List rx = new ArrayList();
		TParm result = new TParm();
		if (!this.isModified()) {
			return result;
		}
		String filter = this.getFilter();
		this.setFilter("");
		this.filter();
		mdfRow = this.getNewRows();
		for (int row : mdfRow) {
			// System.out.println("row="+row);
			TParm parm = this.getRowParm(row);
			// System.out.println("parm===="+parm);
			String tempRx = this.getItemString(row, "RX_NO");
			String orderCode = this.getItemString(row, "ORDER_CODE")
					+ this.getItemInt(row, "SEQ_NO");
			// ���û�����鲻���ٱ���ʱ��ӡ
			if (!"5".equalsIgnoreCase(this.getItemString(row, "RX_TYPE"))) {
				// System.out.println("not 5");
				continue;
			}
			if (!this.isActive(row)) {
				// System.out.println("is not active");
				continue;
			}
			if ("5".equalsIgnoreCase(this.getItemString(row, "RX_TYPE"))
					&& !TypeTool.getBoolean(this
							.getItemData(row, "SETMAIN_FLG"))) {
				// System.out.println("not main");
				continue;
			}
			if (StringTool.getBoolean(this.getItemString(row, "BILL_FLG"))) {
				// System.out.println("is billed");
				continue;
			}
			if (rx.indexOf(orderCode) < 0) {
				rx.add(orderCode);
				result.addData("RX_NO", tempRx);
				result.addData("RX_TYPE", this.getItemString(row, "RX_TYPE"));
				result.addData("MED_APPLY_NO", this.getItemString(row,
						"MED_APPLY_NO"));
				result.addData("SEQ_NO", this.getItemInt(row, "SEQ_NO"));
				result.addData("CAT1_TYPE", this
						.getItemString(row, "CAT1_TYPE"));
				result.setCount(result.getCount("RX_NO"));
			}
		}
		this.setFilter(filter);
		this.filter();
		return result;
	}

	/**
	 * ȡ���޸ģ������������Ĵ��ô�����
	 * 
	 * @return
	 */
	public TParm getModifiedOpRx() {
		int mdfRow[] = new int[] {};
		List rx = new ArrayList();
		TParm result = new TParm();
		if (!this.isModified()) {
			return result;
		}
		String filter = this.getFilter();
		TParm delParm = this.getBuffer(this.DELETE);//======pangben 2013-4-9 
		this.setFilter("");
		this.filter();
		mdfRow = this.getNewRows();
		for (int row : mdfRow) {
			String tempRx = this.getItemString(row, "RX_NO");
			// ���û�����鲻���ٱ���ʱ��ӡ
			if (!"4".equalsIgnoreCase(this.getItemString(row, "RX_TYPE"))) {
				continue;
			}
			if (rx.indexOf(tempRx) < 0) {
				rx.add(tempRx);
				result.addData("RX_NO", tempRx);
				result.addData("RX_TYPE", this.getItemString(row, "RX_TYPE"));
			}
		}
		//==========pangben 2013-4-9 ���ɾ����ҽ������ӡ��ǰ����ǩ����������˴���ǩ�е�ҽ���ͻ��ӡ
		int index=0;
		for (int i = 0; i <delParm.getCount("RX_NO"); i++) {
			// ���û�����鲻���ٱ���ʱ��ӡ
			if (!"4".equalsIgnoreCase(delParm.getValue("RX_TYPE",i))) {
				continue;
			}
			index++;
			printAddParm(result, delParm, i);
		}
		result.setCount(result.getCount("RX_NO")+index);
		this.setFilter(filter);
		this.filter();
		return result;
	}

	/**
	 * ȡ���޸ģ������������Ĵ�����
	 * 
	 * @return
	 */
	public TParm getModifiedOrderRx() {
		int mdfRow[] = new int[] {};
		List rx = new ArrayList();
		TParm result = new TParm();
		if (!this.isModified()) {
			return result;
		}
		String filter = this.getFilter();
		this.setFilter("");
		this.filter();
		// ===========pangben 2012-7-17 start ��ӻ�ɾ����������ǩ��ʾ
		TParm parm = this.getBuffer(this.PRIMARY);
		TParm delParm = this.getBuffer(this.DELETE);//======pangben 2013-4-9 
		int count = parm.getCount();
		ArrayList list = new ArrayList();
		// System.out.println("parm:"+parm);
		for (int i = 0; i < count; i++) {
			// System.out.println("ACTIVE::::"+parm.getBoolean("#ACTIVE#",i));
			if(parm.getValue("ORDER_CODE",i).length()<=0)
				continue;
			if (parm.getBoolean("#NEW#", i)||parm.getBoolean("#MODIFY#", i))//=====pangben 2013-3-20 �����޸Ĵ���ǩ��ʾ
				list.add(new Integer(i));
		}

		mdfRow = getIntArray(list);
		// ===========pangben 2012-7-17 stop 
		// mdfRow = this.getModifiedRows();��
		for (int row : mdfRow) {
			String tempRx = this.getItemString(row, "RX_NO");
			// ���û�����鲻���ٱ���ʱ��ӡ
			if (!"1".equalsIgnoreCase(this.getItemString(row, "RX_TYPE")))
				continue;
			if (rx.indexOf(tempRx) < 0) {
				rx.add(tempRx);
				result.addData("RX_NO", tempRx);
				result.addData("RX_TYPE", this.getItemString(row, "RX_TYPE"));
				result.setCount(result.getCount("RX_NO"));
			}
		}
		//==========pangben 2013-4-9 ���ɾ����ҽ������ӡ��ǰ����ǩ����������˴���ǩ�е�ҽ���ͻ��ӡ
		int index=0;
		for (int i = 0; i <delParm.getCount("RX_NO"); i++) {
			if (!"1".equalsIgnoreCase(delParm.getValue("RX_TYPE",i)))
				continue;
			index++;
			printAddParm(result, delParm, i);
		}
		result.setCount(result.getCount("RX_NO")+index);
		onCallNo(rx);
		this.setFilter(filter);
		this.filter();
		return result;
	}

	/**
	 * ȡ���޸ģ������������Ĵ�����
	 * ����ҩƷ
	 * @return
	 */
	public TParm getModifiedCtrlRx() {
		int mdfRow[] = new int[] {};
		List rx = new ArrayList();
		TParm result = new TParm();
		if (!this.isModified()) {
			return result;
		}
		String filter = this.getFilter();
		this.setFilter("");
		this.filter();
		mdfRow = this.getModifiedRows();
		for (int row : mdfRow) {
			String tempRx = this.getItemString(row, "RX_NO");
			// ���û�����鲻���ٱ���ʱ��ӡ
			if (!"2".equalsIgnoreCase(this.getItemString(row, "RX_TYPE")))
				continue;
			if (rx.indexOf(tempRx) < 0) {
				rx.add(tempRx);
				result.addData("RX_NO", tempRx);
				result.addData("RX_TYPE", this.getItemString(row, "RX_TYPE"));
			}
		}
		onCallNo(rx);
		this.setFilter(filter);
		this.filter();
		return result;
	}

	/**
	 * ��ҩ�������Ŷӽк�
	 * 
	 * @param rx
	 * @return
	 */
	public boolean onCallNo(List rx) {
		int count = rx.size();
		CallNo callNoUtil = new CallNo();
		// System.out.println();
		if (!"N".equals(TConfig.getSystemValue("ServerIsReady"))) {
			callNoUtil.init();
		}
		/**
		 * @param VISIT_DATE
		 *            String �������� NOT NULL,
		 * @param VISIT_NO
		 *            String ����� CASE_NO
		 * @param SERIAL_NO
		 *            String PRINT_NO
		 * @param PATIENT_ID
		 *            String ���˿��� MR_NO
		 * @param PNAME
		 *            String �������� PAT_NAME
		 * @param PSEX
		 *            String �Ա�
		 * @param IDENTITY
		 *            String ���(��)
		 * @param DEPTID
		 *            String ҩ������ ORG_DESC
		 * @param REGISTERING_DATE
		 *            String ��ҩʱ�䣨2010-2-8 11:11:11�� OPT_DATE
		 * @param DOCTOR
		 *            String �������� COUNTER_DESC
		 * @param CTYPE
		 *            String 0 ��ҩ 1 ��ҩ��� 0
		 * @param OPTYPE
		 *            String �������� 2 ������߸��� 2
		 */

		if (count <= 0) {
			return false;
		}
		String caseNo = this.getCaseNo();
		String mrNo = this.getMrNo();
		String orgDesc = "";
		String counterDesc = "";
		String optDate = StringTool.getString(this.getDBTime(),
				"yyyy-MM-dd HH:mm:ss");
		String opType = "2";
		String printNo = "";
		String patName = StringUtil.getDesc("SYS_PATINFO", "PAT_NAME",
				"MR_NO='" + mrNo + "'");
		for (int i = 0; i < count; i++) {
			this.setFilter("RX_NO='" + rx.get(i) + "'");
			this.filter();
			printNo = this.getItemString(0, "PRINT_NO");
			orgDesc = StringUtil.getDesc("IND_ORG", "ORG_CHN_DESC",
					" ORG_CODE='" + this.getItemString(0, "EXEC_DEPT_CODE")
							+ "'");
			counterDesc = StringUtil.getDesc("PHA_COUNTERNO", "COUNTER_DESC",
					"COUNTER_NO='" + this.getItemString(0, "COUNTER_NO") + "'"
							+ " AND ORG_CODE='"
							+ this.getItemString(0, "EXEC_DEPT_CODE") + "'");
			// System.out.println("orderDesc="+counterDesc);
			callNoUtil.SyncDrug("", caseNo, printNo, mrNo, patName, "", "",
					orgDesc, optDate, counterDesc, "0", opType);
		}
		return true;
	}

	/**
	 * ȡ���޸ģ������������Ĵ�����
	 * 
	 * @return
	 */
	public TParm getModifiedChnRx() {
		int mdfRow[] = new int[] {};
		List rx = new ArrayList();
		TParm result = new TParm();
		if (!this.isModified()) {
			return result;
		}
		String filter = this.getFilter();
		this.setFilter("");
		this.filter();
		mdfRow = this.getModifiedRows();
		for (int row : mdfRow) {
			String tempRx = this.getItemString(row, "RX_NO");
			// ���û�����鲻���ٱ���ʱ��ӡ
			if (!"3".equalsIgnoreCase(this.getItemString(row, "RX_TYPE")))
				continue;
			if (rx.indexOf(tempRx) < 0) {
				rx.add(tempRx);
				result.addData("RX_NO", tempRx);
				result.addData("RX_TYPE", this.getItemString(row, "RX_TYPE"));
			}
		}
		onCallNo(rx);
		this.setFilter(filter);
		this.filter();
		return result;
	}

	/**
	 * ����Ƽ۸����ֶ�
	 * 
	 * @param caseNo
	 * @param rxNo
	 * @param seq
	 * @param requestNo
	 * @param requestFlg
	 * @param conn
	 * @return
	 */
	public static TParm updateRequest(String caseNo, String rxNo, long seq,
			String requestNo, String requestFlg, TConnection conn) {
		TParm result = new TParm();
		String sql = "UPDATE OPD_ORDER SET REQUEST_NO='" + requestNo
				+ "' ,REQUEST_FLG='" + requestFlg + "' WHERE CASE_NO='"
				+ caseNo + "' AND RX_NO='" + rxNo + "' AND SEQ=" + seq;
		result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		return result;
	}

	/**
	 * �õ�labNo,��]�Єt�½�һ�l���K����һ��MED_APPLY�Ĕ���
	 * 
	 * @param row
	 * @param odo
	 * @param clinicRoom
	 * @return
	 */
	public String getLabNo(int row, ODO odo) {
		String labNo = "";
		if (odo == null || row < 0) {
			// System.out.println("odo==null:"+odo==null);
			// System.out.println("row<0:"+(row<0));
			// System.out.println("clinicRoom:"+StringUtil.isNullString(clinicRoom));
			// System.out.println("no labNo");
			return labNo;
		}
		//odo.getOpdOrder().getItemData(row, "IS_PRE_ORDER", "");
		//this.setItem(row, "IS_PRE_ORDER", "N");
		
		String s=this.getItemString(row, "IS_PRE_ORDER");
		String labMapKey1 = this.getItemString(row, "DEV_CODE");
		if(s.equalsIgnoreCase("Y")){
			
			return labNo;
		}
		
		String labMapKey = this.getItemString(row, "DEV_CODE")
				+ this.getItemString(row, "OPTITEM_CODE")
				+ this.getItemString(row, "RPTTYPE_CODE");
		String cat1Type = this.getItemString(row, "CAT1_TYPE");
		if ("RIS".equalsIgnoreCase(cat1Type)) {
			labNo = SystemTool.getInstance().getNo("ALL", "MED", "LABNO",
					"LABNO");
			labMap.put(labMapKey, labNo);
		} else {
			if (labMap.get(labMapKey) == null) {
				labNo = SystemTool.getInstance().getNo("ALL", "MED", "LABNO",
						"LABNO");
				if (StringUtil.isNullString(labNo)) {
					return labNo;
				}
				labMap.put(labMapKey, labNo);
			} else {
				labNo = (String) labMap.get(labMapKey);
			}
		}

		MedApply med = this.getMedApply();
		int medRow = med.insertRow();
		// value.add(labNo);//0
		med.setItem(medRow, "APPLICATION_NO", labNo);
		// value.add(cat1Type);//1
		med.setItem(medRow, "CAT1_TYPE", this.getItemData(row, "CAT1_TYPE"));
		// // ORDER_NO
		med.setItem(medRow, "ORDER_NO", this.getItemData(row, "RX_NO"));
		// value.add(this.getItemString(row, "RX_NO"));//2
		// //SEQ_NO
		// System.out.println("med_apply~~~~~~~~~~:"+this.getItemData(row,
		// "SEQ_NO"));
		med.setItem(medRow, "SEQ_NO", this.getItemData(row, "SEQ_NO"));
		// value.add(this.getItemData(row, "SEQ_NO"));//3
		// //ORDER_CODE
		med.setItem(medRow, "ORDER_CODE", this.getItemData(row, "ORDER_CODE"));
		// value.add(this.getItemData(row, "ORDER_CODE"));//4
		// //ORDER_DESC
		med.setItem(medRow, "ORDER_DESC", this.getItemData(row, "ORDER_DESC"));
		// value.add(this.getItemData(row, "ORDER_DESC"));
		// //ORDER_DR_CODE
		med.setItem(medRow, "ORDER_DR_CODE", this.getItemData(row, "DR_CODE"));
		// value.add(Operator.getID());
		Timestamp now = this.getDBTime();
		// //ORDER_DATE
		med.setItem(medRow, "ORDER_DATE", now);
		// value.add(now);
		// //ORDER_DEPT_CODE
		med.setItem(medRow, "ORDER_DEPT_CODE", odo.getRegPatAdm()
				.getItemString(0, "REALDEPT_CODE"));
		// value.add(Operator.getDept());
		// //START_DTTM
		med.setItem(medRow, "START_DTTM", now);
		// value.add(now);
		// //EXEC_DEPT_CODE
		med.setItem(medRow, "EXEC_DEPT_CODE", this.getItemData(row,
				"EXEC_DEPT_CODE"));
		// value.add(this.getItemData(row, "EXEC_DEPT_CODE"));
		// //EXEC_DR_CODE
		med.setItem(medRow, "EXEC_DR_CODE", "");
		// value.add("");
		// //OPTITEM_CODE
		med.setItem(medRow, "OPTITEM_CODE", this.getItemData(row,
				"OPTITEM_CODE"));
		// value.add(this.getItemData(row, "OPTITEM_CODE"));
		String optitemDesc = StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC",
				" GROUP_ID = 'SYS_OPTITEM' AND ID='"
						+ this.getItemData(row, "OPTITEM_CODE") + "'");
		// //OPTITEM_CHN_DESC
		med.setItem(medRow, "OPTITEM_CHN_DESC", optitemDesc);
		// value.add(optitemDesc);
		// //ORDER_CAT1_CODE
		med.setItem(medRow, "ORDER_CAT1_CODE", this.getItemData(row,
				"ORDER_CAT1_CODE"));
		// URGENT_FLG
		med
				.setItem(medRow, "URGENT_FLG", this.getItemString(row,
						"URGENT_FLG"));
		// value.add(this.getItemData(row, "ORDER_CAT1_CODE"));
		String dealSystem = StringUtil.getDesc("SYS_OPTITEM",
				"OPTITEM_CHN_DESC", "OPTITEM_CODE='"
						+ this.getItemData(row, "OPTITEM_CODE") + "'");
		// //DEAL_SYSTEM
		med.setItem(medRow, "DEAL_SYSTEM", dealSystem);
		// value.add(OrderUtil.getInstance().getDealSystem(this.getItemString(row,
		// "ORDER_CAT1_CODE")));
		// //RPTTYPE_CODE
		med.setItem(medRow, "RPTTYPE_CODE", this.getItemData(row,
				"RPTTYPE_CODE"));
		// value.add(this.getItemData(row, "RPTTYPE_CODE"));
		// //DEV_CODE
		med.setItem(medRow, "DEV_CODE", this.getItemData(row, "DEV_CODE"));
		// value.add(this.getItemData(row, "DEV_CODE"));
		// //REMARK
		med.setItem(medRow, "REMARK", this.getItemData(row, "DR_NOTE"));
		// value.add(this.getItemData(row, "DR_NOTE"));
		// OWN_AMT
		med.setItem(medRow, "OWN_AMT", this.getItemData(row, "OWN_AMT"));
		// AR_AMT
		med.setItem(medRow, "AR_AMT", this.getItemData(row, "AR_AMT"));

		// CASE_NO
		med.setItem(medRow, "CASE_NO", this.getCaseNo());
		// MR_NO
		med.setItem(medRow, "MR_NO", this.getMrNo());
		// ADM_TYPE
		// System.out.println("MED_APPLY~~~~~~~~~~~~~~~~�ż�����ʾ��"+getAdmType());
		med.setItem(medRow, "ADM_TYPE", getAdmType());
		// PAT_NAME
		med.setItem(medRow, "PAT_NAME", odo.getPatInfo().getItemString(0,
				"PAT_NAME"));
		// PAT_NAME1
		med.setItem(medRow, "PAT_NAME1", odo.getPatInfo().getItemString(0,
				"PAT_NAME1"));
		// BIRTH_DATE
		med.setItem(medRow, "BIRTH_DATE", odo.getPatInfo().getItemData(0,
				"BIRTH_DATE"));
		// SEX_CODE
		med.setItem(medRow, "SEX_CODE", odo.getPatInfo().getItemData(0,
				"SEX_CODE"));
		// POST_CODE
		med.setItem(medRow, "POST_CODE", odo.getPatInfo().getItemData(0,
				"POST_CODE"));
		// ADDRESS
		med.setItem(medRow, "ADDRESS", odo.getPatInfo().getItemData(0,
				"ADDRESS"));
		// COMPANY
		med.setItem(medRow, "COMPANY", odo.getPatInfo().getItemData(0,
				"COMPANY"));
		// TEL
		med.setItem(medRow, "TEL", odo.getPatInfo().getItemData(0, "TEL"));
		// IDNO
		med.setItem(medRow, "IDNO", odo.getPatInfo().getItemData(0, "IDNO"));
		// DEPT_CODE
		med.setItem(medRow, "DEPT_CODE", odo.getRegPatAdm().getItemString(0,
				"REALDEPT_CODE"));
		// REGION_CODE
		med.setItem(medRow, "REGION_CODE", Operator.getRegion());
		// CLINICAREA_CODE
		med.setItem(medRow, "CLINICAREA_CODE", odo.getRegPatAdm()
				.getItemString(0, "CLINICAREA_CODE"));
		// CLINICROOM_NO CLINICROOM_NO
		med.setItem(medRow, "CLINICROOM_NO", odo.getRegPatAdm().getItemString(
				0, "CLINICROOM_NO"));
		// ICD_TYPE
		int main = odo.getDiagrec().getMainDiag();
		med.setItem(medRow, "ICD_TYPE", odo.getDiagrec().getItemData(main,
				"ICD_TYPE"));
		// ICD_CODE
		med.setItem(medRow, "ICD_CODE", odo.getDiagrec().getItemData(main,
				"ICD_CODE"));
		// STATUS
		med.setItem(medRow, "STATUS", "0");
		// BILL_FLG
		med.setItem(medRow, "BILL_FLG", "N");
		// SEND_FLG
		med.setItem(medRow, "SEND_FLG", "0");
		// OPT_USER
		med.setItem(medRow, "OPT_USER", Operator.getID());
		// OPT_DATE
		med.setItem(medRow, "OPT_DATE", now);
		// OPT_TERM
		med.setItem(medRow, "OPT_TERM", Operator.getIP());
		// PRINT_FLG
		med.setItem(medRow, "PRINT_FLG", "N");
		// ORDER_ENG_DESC
		med.setItem(medRow, "ORDER_ENG_DESC", this.getItemData(row,
				"TRADE_ENG_DESC"));

		med.setActive(medRow, true);
		return labNo;
	}

	/**
	 * �����o��׃���h��һ�l�����t��
	 * 
	 * @param rxNo
	 *            String
	 * @param orderSetCode
	 *            String
	 * @param groupNo
	 *            int
	 * @param ORDER_CODE
	 *            String
	 * @return boolean
	 */
	public boolean deleteOrderSet (String rxNo, String orderSetCode,
			int groupNo, String ORDER_CODE, String seqNo) throws Exception{
		if (groupNo < 0 || StringUtil.isNullString(rxNo)) {
			return false;
		}
		String filter = this.getFilter();
		String fil = "RX_NO='" + rxNo + "' AND ORDERSET_GROUP_NO=" + groupNo
				+ "";
		// �ж��Ƿ��� ����ҽ����루�ж��Ƿ��Ǽ���ҽ����
		if (!StringUtil.isNullString(orderSetCode))// ����Ǽ���ҽ����ôҪ���ݡ�����ҽ����롱����ɾ��
			fil += " AND ORDERSET_CODE='" + orderSetCode + "' ";
		else
			// ������Ǽ���ҽ�� ��ô����ORDER_CODE���е���ɾ��
			fil += " AND ORDER_CODE = '" + ORDER_CODE + "' AND SEQ_NO='"
					+ seqNo + "'";
		this.setFilter(fil);
		this.filter();
		int count = this.rowCount();
		// ��ȡ�����е����� med_apply��Ϣ�� APPLICATION_NO
		Map appNo = new HashMap();
		for (int i = 0; i < med.rowCount(); i++) {
			String key = med.getItemString(i, "ORDER_NO")
					+ med.getItemString(i, "SEQ_NO")
					+ med.getItemString(i, "CAT1_TYPE");
			appNo.put(key, med.getItemString(i, "APPLICATION_NO"));
		}
		for (int i = count - 1; i > -1; i--) {
			// ɾ��med_apply�Ķ�Ӧ��Ϣ
			// �ж��Ǽ�����ҽ�������� med_apply��¼�������� ɾ������
			if (("LIS".equals(this.getItemString(i, "CAT1_TYPE")) || "RIS"
					.equals(this.getItemString(i, "CAT1_TYPE")))
					&& "Y".equals(this.getItemString(i, "SETMAIN_FLG"))) {
				String labMapKey = this.getItemString(i, "RX_NO")
						+ this.getItemString(i, "SEQ_NO")
						+ this.getItemString(i, "CAT1_TYPE");
				med.deleteRowBy((String) appNo.get(labMapKey), this
						.getItemString(i, "RX_NO"), this
						.getItemInt(i, "SEQ_NO"), this.getItemString(i,
						"CAT1_TYPE"));
			}
			this.deleteRow(i);
		}
		this.setFilter(filter);
		this.filter();
		return true;
	}
	public boolean checkRout(TParm p){
		System.out.println("111111:"+p);
		int count = p.getCount("LINK_NO");
		for(int i = 0;i<count;i++){
			String linkNo = p.getValue("LINK_NO", i);
			String inf = p.getValue("INFLUTION_RATE",i);
			if(i+1 < count){
				String linkNoNext = p.getValue("LINK_NO", i+1);
				String infNext = p.getValue("INFLUTION_RATE",i+1);
				if(linkNo.equals(linkNoNext)){
					if(!inf.equals(infNext)){
						return false;
					}
				}else{
					continue;
				}
			}
		}
		return true;
	}
	/**
	 * ��Ᵽ�棨OPD_ORDERҽ����
	 * 
	 * @return boolean
	 */
	public boolean checkSave() {
		// System.out.println("start checkSave");
		// System.out.println("before checkSave");
		String filterString = this.getFilter();
		// System.out.println("filterString="+filterString);
		this.setFilter("");
		this.filter();
		// System.out.println("after checkSave");
		int count = this.rowCount();
		TParm newParm = new TParm() ;
		int seq =  0 ;

		//TParm parmResult = new TParm();
		for (int i = 0; i < count; i++) {
			if (!this.isActive(i)) {
				continue;
			}
//			if(!StringUtil.isNullString(this.getItemString(i, "LINK_NO"))){
//				parmResult.addData("LINK_NO", this.getItemString(i, "LINK_NO"));
//				parmResult.addData("INFLUTION_RATE", this.getItemString(i, "INFLUTION_RATE"));
//			}
//			System.out.println(i+"--------:"+this.getItemString(i, "INFLUTION_RATE"));
			// xueyf ��У�� ����Ƽ� start
			if ("7".equalsIgnoreCase(this.getItemString(i, "RX_TYPE"))) {
				continue;
			}
			// xueyf ��У�� ����Ƽ� end	
			if (StringUtil
					.isNullString(this.getItemString(i, "EXEC_DEPT_CODE"))) {
				if ("en".equals((String) TSystem.getObject("Language")))
					this.setErrText(this.getItemString(i, "TRADE_ENG_DESC")
							+ " ExecDept is empty, can not save");
				else
					this.setErrText(this.getItemString(i, "ORDER_DESC")
							+ " ִ�п���Ϊ�գ����ܱ���");
				this.setFilter(filterString);
				this.filter();
				return false;
			}
			
			if("IVP".equalsIgnoreCase(this.getItemString(i, "ROUTE_CODE"))){
				if(this.getItemDouble(i,"INFLUTION_RATE")==0.000 && this.getItemDouble(i,"INFLUTION_RATE")<=0) {
					this.setErrText("����ע����ע���ʲ���С�ڵ���0");
				this.setFilter(filterString);
				this.filter();
				return false;
				}
			}

			if (!"5".equalsIgnoreCase(this.getItemString(i, "RX_TYPE"))
					&& TypeTool.getDouble(this.getItemData(i, "MEDI_QTY")) <= 0) {
				if ("en".equals((String) TSystem.getObject("Language")))
					this.setErrText(this.getItemString(i, "TRADE_ENG_DESC")
							+ " Qty can not be 0");
				else
					this.setErrText(this.getItemString(i, "ORDER_DESC")
							+ " ��������Ϊ0");
				this.setFilter(filterString);
				this.filter();
				return false;
			}
			if (TypeTool.getDouble(this.getItemData(i, "DISPENSE_QTY")) <= 0) {
				if ("en".equals((String) TSystem.getObject("Language")))
					this.setErrText(this.getItemString(i, "TRADE_ENG_DESC")
							+ " TotQty can not be 0");
				else
					this.setErrText(this.getItemString(i, "ORDER_DESC")
							+ "��������Ϊ0");
				this.setFilter(filterString);
				this.filter();
				return false;
			}
			// ��ҩ�ж��Ƿ���д��"��ҩ��ʽ"
			if ("3".equalsIgnoreCase(this.getItemString(i, "RX_TYPE"))) {
				if (this.getItemString(i, "DCTAGENT_CODE").length() == 0) {
					this.setErrText("��ѡ���ҩ��ʽ");
					this.setFilter(filterString);
					this.filter();
					return false;
				}
			}
			// System.out.println("mien cat1Type="+this.getItemString(i,
			// "CAT1_TYPE"));
			if (!"PHA".equalsIgnoreCase(this.getItemString(i, "CAT1_TYPE"))) {
				continue;
			}
			
			//add by huangtt 20171218 #5883 20171101-��bug����������������ҩƷ�����շѷ�ҩ�������ң����� start

			TParm parm = this.getRowParm(i);
//			System.out.println("pha---"+parm);
			parm.setData("DOSAGE_QTY", this.getItemData(i,"DISPENSE_QTY"));
			TotQtyTool qty = new TotQtyTool();
			TParm qtyParm = qty.getTakeQty(parm);
			
			double dosageQty1 = this.getItemDouble(i,"DISPENSE_QTY");
			double dosageQty2 = this.getItemDouble(i, "DOSAGE_QTY");
			
			if (qtyParm.getData("SUM_QTY") != null) {
				 dosageQty1 = qtyParm.getDouble("SUM_QTY");
			}
			
			
			if(dosageQty1 != dosageQty2){
				System.out.println(this.getItemData(i, "CASE_NO")+"="+this.getItemData(i, "ORDER_CODE")+"=="+dosageQty1+"--"+dosageQty2);

				this.setItem(i, "DISPENSE_QTY", this.getItemData(i,"DISPENSE_QTY"));
			}
			
			//add by huangtt 20171218 #5883 20171101-��bug����������������ҩƷ�����շѷ�ҩ�������ң����� end
			
			double dispenseQty = TypeTool.getDouble(this.getItemData(i,
					"DOSAGE_QTY"));
			TParm parmQty = new TParm();
			String orderCode = this.getItemString(i, "ORDER_CODE");
			String orgCode = this.getItemString(i, "EXEC_DEPT_CODE");
			if (!TypeTool.getBoolean(this.getItemData(i, "RELEASE_FLG"))) {// ���ж��Ƿ����Ա�ҩ
				// �Ա�ҩ�������
				String orderType = "";
				// ����ҩ
				if ("W".equalsIgnoreCase(this.getItemString(i, "PHA_TYPE"))
						|| "C".equalsIgnoreCase(this.getItemString(i,
								"PHA_TYPE"))) {
					orderType = "EXA";
				} else if ("G".equalsIgnoreCase(this.getItemString(i,
						"PHA_TYPE"))) {// ��ҩ
					orderType = "CHN";
				}
				if (checkDrugCanUpdate(orderType, i,parmQty,true)) {// �ж��Ƿ�����޸ģ�û�н�����,��,����
					String sql = "SELECT PHA_STOCK_FLG FROM IND_ORG WHERE ORG_CODE = '"+orgCode+"'";
					TParm pha = new TParm(TJDODBTool.getInstance().select(sql));
					if(!orderType.equals("CHN")|| pha.getBoolean("PHA_STOCK_FLG", 0) ){  // add by  huangtt 20131120  ��ҩ�����п��У�� 
						if (isCheckKC(orderCode)){
							newParm.setData("ORDER_CODE",seq, orderCode);
							newParm.setData("ORG_CODE", seq,orgCode);
							newParm.setData("EXEC_DEPT_CODE", seq,orgCode);
							newParm.setData("CAT1_TYPE", seq,"PHA");
							newParm.setData("CASE_NO", seq,this.getItemString(i, "CASE_NO"));
							newParm.setData("RX_NO", seq,this.getItemString(i, "RX_NO"));
							newParm.setData("DOSAGE_QTY",seq,dispenseQty);
							newParm.setData("ORDER_DESC",seq,this.getItemString(i, "ORDER_DESC"));
							seq++ ;
						}
					}	
				}
			}
		}
		
//		if(!checkRout(parmResult)){
//			this.setErrText("��ͬ�����,����д��ͬ������");
//			return false;
//		}
		/**yuanxm add �����ϲ����������  begin*/
		int newCount = newParm.getCount("ORDER_CODE");
		for(int i = 0 ; i < newCount ; i++) {
			TParm rowParm  = newParm.getRow(i);
			String orderCode = rowParm.getValue("ORDER_CODE");
			String orgCode = rowParm.getValue("EXEC_DEPT_CODE");
			double dosageQty = rowParm.getDouble("DOSAGE_QTY");
			for(int j = i+1 ;j <newCount; j++  ){
				TParm rowParm2 = newParm.getRow(j) ;
				String orderCode1 = rowParm2.getValue("ORDER_CODE");
				String orgCode1 = rowParm2.getValue("EXEC_DEPT_CODE");
				double dosageQty1 = rowParm2.getDouble("DOSAGE_QTY");
				
				if(orgCode.equals(orgCode1) && orderCode.equals(orderCode1)){
					dosageQty += dosageQty1 ;
					newParm.removeRow(j);
					j--;
					newCount--;
				}
			}
			newParm.setData("DOSAGE_QTY",i,dosageQty );
		}
		
		List<String> indOrgIsExinvs = ODOTool.getInstance().getIndOrgIsExinv();
				
		for(int i = 0 ; i < newParm.getCount("ORDER_CODE");i++){
			TParm rowParm = newParm.getRow(i);
			double dispenseQty  = rowParm.getDouble("DOSAGE_QTY");
			String orderCode = rowParm.getValue("ORDER_CODE");
			String orgCode = rowParm.getValue("EXEC_DEPT_CODE");
			String orderDesc = rowParm.getValue("ORDER_DESC");
			if (!Operator.getSpcFlg().equals("Y") && !indOrgIsExinvs.contains(orgCode)) {//��������У����
				if (Operator.getLockFlg().equals("Y")) {//====pangben 2013-11-7 ����湦��
					 
					TParm orderParm=OrderTool.getInstance().selectLockQtyCheckSumQty(rowParm);
					double oldDosageQty=0.00;
					if (orderParm.getCount("QTY")>0) {//���ݿ�������ݣ��۳����=��ǰ����������-���ݿ��������
						oldDosageQty=orderParm.getDouble("QTY",0);
					}
					boolean flg=true;
					
					if (dispenseQty>oldDosageQty) {//����
						flg=INDTool.getInstance().inspectIndStockQty(orderCode
								,orgCode,
								dispenseQty,oldDosageQty, true);
					}else if(dispenseQty < oldDosageQty){//����
						flg=INDTool.getInstance().inspectIndStockQty(orderCode
								,orgCode,
								dispenseQty,oldDosageQty, false);
					}
					if (!flg) {
						this.setErrText(orderDesc
						+ " ��治��;"
						+ orderCode);
						this.setFilter(filterString);
						this.filter();
						return false;
					}
				}else{	
					if (!INDTool.getInstance().inspectIndStock(orgCode,
							orderCode, dispenseQty)) {// ����
						if ("en".equals((String) TSystem
								.getObject("Language")))
							this.setErrText(this.getItemString(i,
									"TRADE_ENG_DESC")
									+ " Inventory shortage");
						else
							this.setErrText(orderDesc
									+ " ��治��;"
									+ orderCode);
						this.setFilter(filterString);
						this.filter();
						return false;
					}
				}
				
				//add by huangtt 20150610 startУ��ind_stockm���п��
				String stockMSql ="SELECT COUNT(*) AS QTY FROM IND_STOCKM WHERE ACTIVE_FLG='N' " +
						" AND ORG_CODE='"+orgCode+"' AND ORDER_CODE='"+orderCode+"'";
                 TParm stockM = new TParm(TJDODBTool.getInstance().select(stockMSql));
                 if (stockM.getInt("QTY", 0) == 0){
                	 this.setErrText(orderDesc
     						+ " ��治��;"
     						+ orderCode);
     						this.setFilter(filterString);
     						this.filter();
     						return false;
                 }
				//add by huangtt 20150610 end
				
			}
		}
		/**yuanxm �����ϲ����������  end*/
		
		this.setFilter(filterString);
		this.filter();
		
		// System.out.println("last filterString="+filterString);
		// System.out.println("at the end");
		return true;
	}

	/**
	 * ȡ�����д���ǩ���
	 * 
	 * @return
	 */
	public double getAllAmt() {
		double amt = 0.0;
		String filterString = this.getFilter();
		this.setFilter("");
		this.filter();
		int count = this.rowCount();
		for (int i = 0; i < count; i++) {
			if (!TypeTool.getBoolean(this.getItemData(i, "BILL_FLG"))) {
				amt += this.getItemDouble(i, "AR_AMT");
			}
		}
		this.setFilter(filterString);
		this.filter();
		return amt;
	}

	/**
	 * �Ƿ�Ϊ��ҽ���÷�
	 * 
	 * @param routeCode
	 * @return int 1:����ҽ�ã�0������ҽ�ã�-1������
	 */
	public int isMedRoute(String routeCode) {
		if (StringUtil.isNullString(routeCode)) {
			return -1;
		}
		String sql = "SELECT WESMED_FLG,CHIMED_FLG FROM SYS_PHAROUTE WHERE ROUTE_CODE='"
				+ routeCode + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() != 0) {
			System.out.println("isMedRoute.err=" + parm.getErrText());
			return -1;
		}
		if (TypeTool.getBoolean(parm.getData("CHIMED_FLG", 0))) {
			return 0;
		}
		return 1;
	}

	/**
	 * ȡ����ҩ��
	 * 
	 * @param orgCode
	 * @return
	 */
	public String getPrintNo(String orgCode) {
		String printNo = "";
		if (StringUtil.isNullString(orgCode)) {
			return printNo;
		}
		printNo = SystemTool.getInstance().getNo("ALL", "PHA", "PRINT_NO",
				"PRINT_NO");
		return printNo;
	}

	/**
	 * ȡ��ҩ����̨�ţ���ҩ�ŵ���������ȡ�÷��������Ĺ�̨���ϣ�����ҩ��%��̨���������õ���̨�����иý��Ϊ�±�Ĺ�̨��
	 * 
	 * @param orgCode
	 *            String ҩ������
	 * @param printNo
	 *            String ��ҩ��
	 * @param rxType
	 *            String ��������
	 * @return counterNo int ��̨��
	 */
	public int getCounterNo(String orgCode, String printNo, String rxType) {
		int counterNo = -1;
		if (StringUtil.isNullString(orgCode)
				|| StringUtil.isNullString(printNo)
				|| StringUtil.isNullString(rxType)) {
			// System.out.println("getCounterNo.param is null");
			return counterNo;
		}
		String sql = this.GET_COUNTER_NO.replaceFirst("#", orgCode);
		if ("2".equalsIgnoreCase(rxType)) {
			sql += " AND CTRL_FLG='Y' ORDER BY COUNTER_NO";
		} else {
			sql += " AND COMMON_FLG='Y' ORDER BY COUNTER_NO";
		}
		int printNoInt = StringTool.getInt(printNo);
		TParm countNos = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("countNos="+countNos);
		if (countNos.getErrCode() != 0) {
			System.out.println("getCounterNo.countNos.err"
					+ countNos.getErrText());
			return counterNo;
		}
		int count = countNos.getCount();
		// System.out.println("getCounterNo.count="+count);
		if (count <= 0) {
			// System.out.println("getCounterNo.countNos.count<=0");
			return counterNo;
		}
		int index = printNoInt % count;
		// System.out.println("index="+index);
		// System.out.println("counterNo="+TypeTool.getInt(countNos.getData("COUNTER_NO",index)));
		counterNo = TypeTool.getInt(countNos.getData("COUNTER_NO", index));
		// System.out.println("counterNo="+counterNo);
		return counterNo;
	}

	/**
	 * ���ݴ���ҽ���ж��Ƿ�Ϊ�����ҽ�����Ϳɿ�����ҽ��
	 * 
	 * @param orderCode
	 * @return
	 */
	public boolean isDrug(String orderCode, String rxType) {
		if ("1".equalsIgnoreCase(rxType)) {
			if (SYSCtrlDrugClassTool.getInstance().getOrderCtrFlg(orderCode)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * ���ݸ���ҽ�������жϸ�ҽ���Ƿ�Ϊ����������ǣ���˴���ǩ�����г�����֮�������ҽ��
	 * 
	 * @param orderCode
	 * @param row
	 * @return
	 */
	public boolean isSameDrug(String orderCode, int row) {
		int count = this.rowCount();
		String sql = GET_PSYCHOPHA2.replaceFirst("#", orderCode);
		// System.out.println("isSameDrug.sql="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() != 0) {
			System.out.println("isSameDrug.result is wrong :"
					+ result.getErrText());
			return false;
		}
		int resultCnt = result.getCount();
		String phsyFlg = "1";
		if (resultCnt == 1) {
			phsyFlg = "2";
		}
		this.setItem(row, "PSY_FLG", phsyFlg);
		if (count <= 0) {
			// System.out.println("isSameDrug.count<=0");
			return true;
		}
		for (int i = 0; i < count; i++) {
			// System.out.println(this.getItemString(i,
			// "ORDER_DESC")+"in order.psy_flg="+this.getItemString(i,
			// "PSY_FLG"));
			if (StringUtil.isNullString(this.getItemString(i, "ORDER_CODE"))) {
				continue;
			}
			if (!this.getItemString(i, "PSY_FLG").equalsIgnoreCase(phsyFlg)) {
				// System.out.println("isSameDrug is not same");
				// System.out.println("in order="+this.getItemString(i,
				// "PSY_FLG"));
				// System.out.println("value="+phsyFlg);
				return false;
			}
		}

		return true;
	}
	/**
	 * �޸�ҽ��
	 * @param updateParm
	 * @param result
	 * @param i
	 */
	private void setUpdateParm(TParm updateParm,TParm result,int i){
		updateParm.addData("CAT1_TYPE", result.getValue(
				"CAT1_TYPE", i));
		OPBTool.getInstance().setNewParm(updateParm, result, i,"Y","E");
	}
	/**
	 * ����ҽ��������Ϣ--ҩ����������ʹ��
	 * @param orderOldParm
	 * @return
	 * ������ʹ��
	 * ======pangben 2013-12-18
	 */
	public TParm getSendParam(TParm orderOldParm){
		TParm result = new TParm();
		int[] newRows = this.getNewRows();
		//������������
		if (newRows != null && newRows.length > 0) {
			isNew = true;
		}
		//�����޸ĵ�����
		TParm moduf = this.getBuffer(this.MODIFY);
		if (moduf != null && moduf.getCount() > 0) {
			isNew = false;
		}
		//����ɾ��������
		if (this.getDeleteCount() > 0) {
			isNew = false;
		}
		TParm parm=new TParm();
		StringBuffer phaRxNo = new StringBuffer();//==pangben 2013-5-15����ҩ��ʹ�ã���˽��������ִ�в����Ĵ���ǩ
		//���� �޸�����״̬
		result =this.getBuffer(this.isFilter() ? this.FILTER : this.PRIMARY);
		int count = result.getCount();
		for (int i = 0; i < count; i++) {
			if (StringUtil.isNullString(result.getValue("ORDER_CODE", i))) {
				continue;
			}
			if (result.getBoolean("RELEASE_FLG", i)) {// ��ҩ���շ�
				continue;
			}
			if (result.getBoolean("#MODIFY#", i)
					&& result.getBoolean("#ACTIVE#", i)) {
				if (null != result.getValue("CAT1_TYPE", i) && // ==pangben2013-5-15���ҩ����ҩ��ʾ���������
						result.getValue("CAT1_TYPE", i).equals("PHA")) {
					if (!phaRxNo.toString().contains(result.getValue("RX_NO", i))) {
						phaRxNo.append(result.getValue("RX_NO", i)).append(",");
					}
				}
			}
			// û�շѵ�ҽ��
			if (!result.getBoolean("BILL_FLG", i)&&result.getBoolean("#NEW#", i)) {//=====pangben 2014-3-19
				if (null != result.getValue("CAT1_TYPE", i) && // ==pangben2013-5-15���ҩ����ҩ��ʾ���������
						result.getValue("CAT1_TYPE", i).equals("PHA")) {
					if (!phaRxNo.toString().contains(result.getValue("RX_NO", i))) {
						phaRxNo.append(result.getValue("RX_NO", i)).append(",");
					}
				}
			}
		}
		TParm delParm = this.getBuffer(this.DELETE);
		for (int i = 0; i < delParm.getCount(); i++) {
			if (StringUtil.isNullString(delParm.getValue("ORDER_CODE", i))) {
				continue;
			}
			if (delParm.getBoolean("RELEASE_FLG", i)) {// ��ҩ���շ�
				continue;
			}
			if (null != delParm.getValue("CAT1_TYPE", i) && // ==pangben2013-5-15���ҩ����ҩ��ʾ���������
					delParm.getValue("CAT1_TYPE", i).equals("PHA")) {
				if (!phaRxNo.toString().contains(delParm.getValue("RX_NO", i))) {
					phaRxNo.append(delParm.getValue("RX_NO", i)).append(",");
				}
			}
		}
		parm.setData("PHA_RX_NO", phaRxNo.length()>0? phaRxNo.toString().substring(0,
				phaRxNo.toString().lastIndexOf(",")):"");//=pangben2013-5-15���ҩ����ҩ��ʾ���������
		return parm;
	}
	/**
	 * ȡ��ҽ�ƿ�������
	 * 
	 * @return
	 */
	public TParm getEktParam(TParm orderOldParm) {
		TParm result = new TParm();
		int[] newRows = this.getNewRows();
		//������������
		if (newRows != null && newRows.length > 0) {
			isNew = true;
		}
		//�����޸ĵ�����
		TParm moduf = this.getBuffer(this.MODIFY);
		if (moduf != null && moduf.getCount() > 0) {
			isNew = false;
		}
		//����ɾ��������
		if (this.getDeleteCount() > 0) {
			isNew = false;
		}
		//��ǰ�����շѺ����ݿ�ҽ��״̬��������ɾ������  
		//���� �޸�����״̬
		result =this.getBuffer(this.isFilter() ? this.FILTER : this.PRIMARY);
		int count = result.getCount();
		TParm parm = new TParm();
		double sum=0.00;//�ۼƽ��
		TParm newParm=new TParm();//�˴β�����ҽ������
		TParm updateParm=new TParm();//�޸ĵ�ҽ������
		TParm hl7Parm =new TParm();//hl7����
		TParm delExeParm =new TParm();//�շѵ�ҽ��ִ��ɾ�����Ի���
		StringBuffer rxNo=new StringBuffer();//��¼��β������ڲ����׺��� UPDATE EKT_TRADE ���Ѿ��շѵļ�¼
		StringBuffer tempTradeNo=new StringBuffer();//������β�����ҽ�����ڵĴ���ǩҪִ�е�״̬
		StringBuffer phaRxNo = new StringBuffer();//==pangben 2013-5-15����ҩ��ʹ�ã���˽��������ִ�в����Ĵ���ǩ
		//����ҽ���Ĵ���ǩ���Ѿ��շѵ��ڲ����׺���ҲҪ�ۼ�
		StringBuffer tradeNo=new StringBuffer();
		double showAmt=0.00;//ҽ�ƿ��ۿ������ʾ���
		//���շѵ�ҽ���ݴ���� ������ݿ�������ҽ��
		if (null == orderOldParm) {
			boolean updateFlg=false;
			for (int i = 0; i < count; i++) {
				if (result.getBoolean("#MODIFY#", i)&& result.getBoolean("#ACTIVE#", i)) {
					// �շѵ���Ҫ��¼����ǩ��û���շ�ֻ���޸Ĳ��ü�¼ ,������ε�ҽ��ֻ���¼�շ����ݵ��ڲ����׺���,
					// ��ѯ�������ڲ����׺������ڵ�����ҽ������
					if (result.getBoolean("BILL_FLG", i)) {
						updateFlg = true;
						break;
					}
				}
			}
			if(updateFlg){
				TParm parmOne = new TParm();
				parmOne.setData("CASE_NO", result.getValue("CASE_NO", 0));
				// ��ô˴β���ҽ�ƿ����е�ҽ�� ��ִ��ɾ������ҽ��ʱʹ��
				orderOldParm = OrderTool.getInstance()
						.selDataForOPBEKT(parmOne);
				if (orderOldParm.getErrCode() < 0) {
					return orderOldParm;
				}
			}
		}
		for (int i = 0; i < count; i++) {
			if (StringUtil.isNullString(result.getValue("ORDER_CODE", i))) {
				continue;
			}
			if (result.getBoolean("RELEASE_FLG", i)) {// ��ҩ���շ�
				continue;
			}
			//��û�иĺã���Ҫ������������, ͨ��ѡ��״̬ ִ�д˴β���������
			parm.addData("EKT_TRADE_FLG", "Y");//ѡ��ע�� ִ���շѲ���
			if (result.getBoolean("#MODIFY#", i)
					&& result.getBoolean("#ACTIVE#", i)) {
				// �շѵ���Ҫ��¼����ǩ��û���շ�ֻ���޸Ĳ��ü�¼ ,������ε�ҽ��ֻ���¼�շ����ݵ��ڲ����׺���,
				// ��ѯ�������ڲ����׺������ڵ�����ҽ������
				if (result.getBoolean("BILL_FLG", i)) {
					// // ���ܴ���ǩ
					if (!rxNo.toString().contains(result.getValue("RX_NO", i))) {
						rxNo.append(result.getValue("RX_NO", i)).append(",");
					}
					if (null != result.getValue("CAT1_TYPE", i) && // ==pangben2013-5-15���ҩ����ҩ��ʾ���������
							result.getValue("CAT1_TYPE", i).equals("PHA")) {
						if (!phaRxNo.toString().contains(result.getValue("RX_NO", i))) {
							phaRxNo.append(result.getValue("RX_NO", i)).append(",");
						}
					}
					if (!tempTradeNo.toString().contains(result.getValue("BUSINESS_NO", i))) {
						tempTradeNo.append(result.getValue("BUSINESS_NO", i)).append(",");// ������β�����ҽ��ʹ��
						tradeNo.append("'").append(result.getValue("BUSINESS_NO", i)).append("',");// UPDATE EKT_TRADE��ʹ���޸��Ѿ��ۿ�����ݳ帺ʹ��
					}
					setUpdateParm(updateParm, result, i);
					showAmt += getUpdateOrderAmt(result.getRow(i), orderOldParm);// �ۼƿۿ������ʾ���
					// setNewParm(newParm, parm, i);
				}
			}
			// û�շѵ�ҽ��
			if (result.getValue("BUSINESS_NO", i).length() <= 0
					&& !result.getBoolean("BILL_FLG", i)) {
				// ���ܴ���ǩ
				if (!rxNo.toString().contains(result.getValue("RX_NO", i))) {
					rxNo.append(result.getValue("RX_NO", i)).append(",");
				}
				if (null != result.getValue("CAT1_TYPE", i) && // ==pangben2013-5-15���ҩ����ҩ��ʾ���������
						result.getValue("CAT1_TYPE", i).equals("PHA")) {
					if (!phaRxNo.toString().contains(result.getValue("RX_NO", i))) {
						phaRxNo.append(result.getValue("RX_NO", i)).append(",");
					}
				}
				OPBTool.getInstance().setNewParm(newParm, result, i, "Y", "E");
				sum += result.getDouble("AR_AMT", i);// �ۼƽ��
				showAmt += result.getDouble("AR_AMT", i);// �ۼƿۿ������ʾ���
				// HL7���ݼ��� ������� �ļ���ҽ������ ���ͽӿ�ʹ��
				OPBTool.getInstance().setHl7TParm(hl7Parm, result, i, "Y");
			}
			setReslutParm(parm, result, i, "Y");
		}
		String delFlg="N";
		//�˴�ɾ����ҽ��
		TParm delParm = this.getBuffer(this.DELETE);
		for (int i = 0; i < delParm.getCount(); i++) {
			if (StringUtil.isNullString(delParm.getValue("ORDER_CODE", i))) {
				continue;
			}
			if (delParm.getBoolean("RELEASE_FLG", i)) {// ��ҩ���շ�
				continue;
			}
			if(!delParm.getBoolean("BILL_FLG", i))
				continue;
			// HL7���ݼ��� ������� �ļ���ҽ������ ���ͽӿ�ʹ��
			OPBTool.getInstance().setHl7TParm(hl7Parm, delParm, i,"N");
			//���ܴ���ǩ
			if(!rxNo.toString().contains(delParm.getValue("RX_NO", i))){
				rxNo.append(delParm.getValue("RX_NO", i)).append(",");
			}
			if (null != delParm.getValue("CAT1_TYPE", i) && // ==pangben2013-5-15���ҩ����ҩ��ʾ���������
					delParm.getValue("CAT1_TYPE", i).equals("PHA")) {
				if (!phaRxNo.toString().contains(delParm.getValue("RX_NO", i))) {
					phaRxNo.append(delParm.getValue("RX_NO", i)).append(",");
				}
			}
			if(!tempTradeNo.toString().contains(delParm.getValue("BUSINESS_NO", i))){
				tempTradeNo.append(delParm.getValue("BUSINESS_NO", i)).append(",");//������β�����ҽ��ʹ��
				tradeNo.append("'").append(delParm.getValue("BUSINESS_NO", i)).append("',");//UPDATE EKT_TRADE ��ʹ�� �޸��Ѿ��ۿ������ �帺ʹ��
			}
			//ɾ����ҽ�����ȡ����ť ���Ի��˲���
			OPBTool.getInstance().setNewParm(delExeParm, delParm, i,"N","E");
			if (delParm.getValue("CAT1_TYPE", i).equals("LIS")
					|| delParm.getValue("CAT1_TYPE", i).equals("RIS")) {
				delFlg = "Y";
			}
			showAmt-=delParm.getDouble("AR_AMT", i);// �ۼƿۿ������ʾ���
			parm.addData("EKT_TRADE_FLG", "Y");//��û�иĺã���Ҫ������������, ͨ��ѡ��״̬ ִ�д˴β���������
			setReslutParm(parm, delParm, i,"N");
		}
		if(rxNo.toString().length()<=0){
			parm.setData("OP_FLG",5);//û�пۿ������ҽ�� 
			return parm;
		}
		//������Ҫ������ҽ�� (ͨ���ڲ����׺����ѯ���� ,������������ҽ�� ������Ĳ������Ѿ���ӵ������� 
		//���ô˴β�����ҽ��״̬ EKT_TRADE_FLG=Y ѡ���ҽ������
		String [] tempTradeNames=new String[0];
		if(tempTradeNo.length()>0){
			tempTradeNames=tempTradeNo.toString().substring(0,tempTradeNo.lastIndexOf(",")).split(",");
		}
		for (int i = 0; i < result.getCount("ORDER_CODE"); i++) {
			for (int j = 0; j < tempTradeNames.length; j++) {
				// ѡ���ҽ��
				// EKT_TRADE ���ڲ����׺�
				if (result.getValue("BUSINESS_NO", i).equals(tempTradeNames[j])) {
					OPBTool.getInstance().setNewParm(newParm, result, i, "Y","E");
					sum += result.getDouble("AR_AMT", i);// �ۼƽ��
				}
			}
		}
		String exeTradeNo = "";
		// ����ڲ����׺��� ���˴β�����ҽ���ۿ�������Ҫ�˻���ҽ��
		if (tradeNo.length() > 0) {
			exeTradeNo = tradeNo.toString().substring(0,
					tradeNo.toString().lastIndexOf(","));
		}
		parm.setData("MR_NO", this.getMrNo());// ע����set��ȥMR_NO
		parm.setData("IS_NEW", isNew);// �Ƿ�ֻ����������ҽ��
		parm.setData("BUSINESS_TYPE", "ODO");
		parm.setData("DEL_FLG", delFlg);//����ɾ��ҽ��
		parm.setData("newParm", newParm.getData());//����ҽ������ɾ��
		parm.setData("EXE_AMT",sum);//EKT_TRADE �д˴� �����Ľ��
		parm.setData("hl7Parm",hl7Parm.getData());//HL7���ͽӿڼ���
		parm.setData("updateParm",updateParm.getData());//�޸ĵ�ҽ��
		parm.setData("TRADE_SUM_NO",exeTradeNo);//UPDATE EKT_TRADE �帺����,ҽ�ƿ��ۿ��ڲ����׺���,��ʽ'xxx','xxx'
		parm.setData("SHOW_AMT",showAmt);//�ۿ������ʾ��� 	
		parm.setData("delExeParm", delExeParm.getData());//ɾ��ҽ��������ʹ��
		parm.setData("PHA_RX_NO", phaRxNo.length()>0? phaRxNo.toString().substring(0,
				phaRxNo.toString().lastIndexOf(",")):"");//=pangben2013-5-15���ҩ����ҩ��ʾ���������
		return parm;
	}
	/**
	 * ��ֵ
	 * @param parm
	 * @param reslutParm
	 */
	private void setReslutParm(TParm parm,TParm resultParm,int i,String billFlg){
		parm.addData("CAT1_TYPE", resultParm.getValue("CAT1_TYPE", i));// ���˼����飬ҽ���޸�ҽ��ҽ�ƿ��շ�ʱʹ��
		parm.addData("RX_NO", resultParm.getValue("RX_NO", i));//����ǩ
		parm.addData("MED_APPLY_NO", resultParm.getValue("MED_APPLY_NO", i));// ������
		parm.addData("ORDER_CODE", resultParm.getValue("ORDER_CODE", i));
		parm.addData("SETMAIN_FLG", resultParm.getValue("SETMAIN_FLG", i));// ����ҽ��
		parm.addData("SEQ_NO", resultParm.getInt("SEQ_NO", i));//���
		parm.addData("AMT", resultParm.getDouble("AR_AMT", i));//���
		parm.addData("CASE_NO", resultParm.getValue("CASE_NO", i));//����� 
		parm.addData("EXEC_FLG", resultParm.getData("EXEC_FLG", i));//ִ��״̬ 
		parm.addData("RECEIPT_FLG", resultParm.getData("RECEIPT_FLG", i));//�վ�״̬ 
		parm.addData("DOSAGE_QTY", resultParm.getDouble("DOSAGE_QTY", i));//��ҩ�� 
		parm.addData("TAKE_DAYS", resultParm.getInt("TAKE_DAYS", i));//��ҩ���� 
		parm.addData("FREQ_CODE", resultParm.getData("FREQ_CODE", i));//Ƶ�� 
		parm.addData("BILL_FLG",billFlg);//�շ�ע�� 
		parm.addData("PRINT_NO", resultParm.getValue("PRINT_NO", i));//��̨���� 
		parm.addData("COUNTER_NO", resultParm.getValue("COUNTER_NO", i));
		parm.addData("COST_CENTER_CODE", resultParm.getValue(
				"COST_CENTER_CODE", i));//�ɱ�����
		parm.addData("ORDERSET_CODE", resultParm.getValue("ORDERSET_CODE", i));//����ҽ��
		parm.addData("BUSINESS_NO", resultParm.getValue("BUSINESS_NO", i));//�ڲ����׺��� 
		parm.addData("TRADE_ENG_DESC", resultParm.getValue(
						"TRADE_ENG_DESC", i));
		parm.addData("HIDE_FLG", resultParm.getValue("HIDE_FLG", i));
		parm.addData("ADM_TYPE", resultParm.getValue("ADM_TYPE", i));//�������� :�ż��� 
		parm.addData("ORDER_CAT1_CODE", resultParm.getValue("ORDER_CAT1_CODE",
				i));
		parm.addData("TEMPORARY_FLG", resultParm.getValue("TEMPORARY_FLG", i));
	}
	/**
	 * д���ʼֵ
	 * 
	 * @param billType
	 * @return
	 */
	public boolean setBillSets(TParm parm, List hl7List) {
		// System.out.println("parmL=============>"+parm);
		String filterString = this.getFilter();
		this.setFilter("CASE_NO='" + getCaseNo() + "'");
		this.filter();
		int count = this.rowCount();
		Timestamp now = this.getDBTime();
		int type = parm.getInt("OP_TYPE");
		List list = (ArrayList) parm.getData("RX_LIST");

		// System.out.println("type=============="+type);
		// �ɹ�
		if (type == 1 || type == 4) {
			// System.out.println("type1=============="+type);
			for (int i = 0; i < count; i++) {
				if (!this.isActive(i)) {
					continue;
				}
				if (StringTool.getBoolean(this.getItemString(i, "RELEASE_FLG"))) {
					continue;
				}
				if (StringTool.getBoolean(this.getItemString(i, "BILL_FLG"))) {
					continue;
				}
				if (type == 1
						&& list.indexOf(this.getItemString(i, "RX_NO")) < 0) {
					continue;
				}
				if ("5".equalsIgnoreCase(this.getItemString(i, "RX_TYPE"))
						&& StringTool.getBoolean(this.getItemString(i,
								"SETMAIN_FLG"))) {
					hl7List.add(getHl7Parm(i, true));
				}
				this.setItem(i, "EXEC_FLG", "N");
				this.setItem(i, "RECEIPT_FLG", "N");
				this.setItem(i, "BILL_TYPE", "E");
				this.setItem(i, "BILL_FLG", "Y");
				this.setItem(i, "BILL_USER", Operator.getID());
				this.setItem(i, "BILL_DATE", now);
				// �жϸ�ҽ���Ƿ��Ǽ����� ����MED_APPLY����շ�ע��
				if (("LIS".equals(this.getItemString(i, "CAT1_TYPE")) || "RIS"
						.equals(this.getItemString(i, "CAT1_TYPE")))
						&& "Y".equals(this.getItemString(i, "SETMAIN_FLG"))) {
					String filter = med.getFilter();
					String fill = " ORDER_NO = '"
							+ this.getItemString(i, "RX_NO")
							+ "' AND SEQ_NO = '"
							+ this.getItemString(i, "SEQ_NO")
							+ "' AND APPLICATION_NO = '"
							+ this.getItemString(i, "MED_APPLY_NO")
							+ "' AND CAT1_TYPE = '"
							+ this.getItemString(i, "CAT1_TYPE") + "'";
					// System.out.println("fill:"+fill);
					med.setFilter(fill);
					med.filter();
					if (med.rowCount() > 0) {
						med.setItem(0, "BILL_FLG", "Y");
					}
					med.setFilter(filter);
					med.filter();
				}
			}
			setDischargeList(hl7List);
		}
		// ֻ�˷ѣ��ѻش��Ĵ������շ�ע����ΪN��
		else if (type == 2) {
			// System.out.println("type1=============="+type);
			list = (ArrayList) parm.getData("RX_LIST");
			this.setFilter("CASE_NO='" + getCaseNo() + "'");
			this.filter();
			count = this.rowCount();
			for (int i = 0; i < count; i++) {
				if (list.indexOf(this.getItemString(i, "RX_NO")) < 0) {
					continue;
				}
				if (!StringTool.getBoolean(this.getItemString(i, "BILL_FLG"))) {
					continue;
				}
				if (StringTool.getBoolean(this.getItemString(i, "RECEIPT_NO"))) {
					continue;
				}
				if (StringTool.getBoolean(this.getItemString(i, "EXEC_FLG"))) {
					continue;
				}
				if ("5".equalsIgnoreCase(this.getItemString(i, "RX_TYPE"))
						&& StringTool.getBoolean(this.getItemString(i,
								"SETMAIN_FLG"))) {
					hl7List.add(getHl7Parm(i, false));
				}
				this.setItem(i, "EXEC_FLG", "N");
				this.setItem(i, "RECEIPT_FLG", "N");
				this.setItem(i, "BILL_TYPE", "C");
				this.setItem(i, "BILL_FLG", "N");
				this.setItem(i, "BILL_USER", "");
				this.setItem(i, "BILL_DATE", null);
			}
			setDischargeList(hl7List);
		}
		// ��ʹ��ҽ�ƿ������շѴ���
		else if (type == 3) {
			this.setFilter(filterString);
			this.filter();
			// System.out.println("type=-3");
			return false;
		} else {
			this.setFilter(filterString);
			this.filter();
			// System.out.println("type=-2"+type);
			return false;
		}
		this.setFilter(filterString);
		this.filter();
		return true;
	}

	/**
	 * 
	 * @param row
	 * @return
	 */
	public boolean isExecute(int row) {
		if (row < 0) {
			return true;
		}
		String caseNo = this.getItemString(row, "CASE_NO");
		String rxNo = this.getItemString(row, "RX_NO");
		int seq = this.getItemInt(row, "SEQ_NO");
		String sql = GET_EXEC_FLG.replaceFirst("#", caseNo).replaceFirst("#",
				rxNo).replaceFirst("#", seq + "");
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() != 0) {
			// System.out.println("isExecute.sql="+sql);
			return true;
		}
		return result.getBoolean("EXEC_FLG", 0)
				|| result.getBoolean("RECEIPT_FLG", 0);
	}
	/**
	 * У��˴���ǩ�Ƿ��Ѿ���Ʊ
	 * pangben 2012-7-24
	 * @param row
	 * @return
	 */
	public boolean isExecutePrint(String caseNo ,String rxNo){
		//У��ҽ�������Ƿ��Ѿ���Ʊ״̬=======pangben 2012-7-24
		String GET_PRINT_FLG = "SELECT ORDER_CODE  FROM OPD_ORDER WHERE CASE_NO='"+caseNo+"' AND RX_NO='"+rxNo+"' AND PRINT_FLG='Y'";
		TParm result = new TParm(TJDODBTool.getInstance().select(GET_PRINT_FLG));
		if (result.getErrCode() != 0) {
			// System.out.println("isExecute.sql="+sql);
			return true;
		}
		if (result.getCount()>0) {
			return false;
		}else{
			return true;
		}
	}
	/**
	 * ��HL7������Ϣ
	 * 
	 * @return
	 */
	public boolean sendHl7() {
		TParm exaParm = this.getModifiedExaOrder();
		// System.out.println("exaParm=="+exaParm);
		int count = exaParm.getCount();
		if (count <= 0) {
			// System.out.println("sendHl7.count<=0");
			return true;
		}

		List orderList = new ArrayList();
		for (int i = 0; i < count; i++) {
			TParm parm = new TParm();
			parm.setData("ADM_TYPE", getAdmType());
			parm.setData("CAT1_TYPE", exaParm.getValue("CAT1_TYPE", i));
			parm.setData("PAT_NAME", OpdRxSheetTool.getInstance().getPatName(
					this.getMrNo()));
			parm.setData("CASE_NO", this.getCaseNo());
			parm.setData("LAB_NO", exaParm.getValue("MED_APPLY_NO", i));
			parm.setData("ORDER_NO", exaParm.getValue("RX_NO", i));
			parm.setData("SEQ_NO", exaParm.getData("SEQ_NO", i));
			parm.setData("FLG", "0");
			// System.out.println("parm for charge=="+parm);
			orderList.add(parm);
		}
		TParm result = Hl7Communications.getInstance().Hl7Message(orderList);
		if (result == null || result.getErrCode() < 0) {
			System.out.println("����HL7ʧ�ܣ�" + result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * �˷�ʱȡ��
	 * 
	 * @return
	 */
	public TParm getDischargeExaParm() {
		TParm parm = new TParm();

		return parm;
	}

	/**
	 * ���ݸ���ҩ����ѯ�Ƿ��п��õĴ���
	 * 
	 * @param orgCode
	 * @return
	 */
	public boolean isOrgAvalible(String orgCode) {
		if (StringUtil.isNullString(orgCode)) {
			return false;
		}
		String sql = this.GET_COUNTER_NO.replaceFirst("#", orgCode);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() != 0) {
			System.out.println(result.getErrText());
			return false;
		}
		int count = result.getCount();
		if (count <= 0) {
			return false;
		}

		return true;
	}

	/**
	 * �ж�ҽ���Ƿ��ɾ��
	 * 
	 * @param row
	 *            boolean flg ҽ�ƿ�����ʹ�ùܿ�
	 * @return =========pangben 2012-2-21 ���ҽ�ƿ�ɾ���ܿ�
	 */
	public boolean isRemovable(int row, boolean flg) {
		String caseNo = this.getItemString(row, "CASE_NO");
		String rxNo = this.getItemString(row, "RX_NO");
		int seq = this.getItemInt(row, "SEQ_NO");
		String sql = GET_EXEC_FLG.replaceFirst("#", caseNo).replaceFirst("#",
				rxNo).replaceFirst("#", seq + "");
		// System.out.println("isRemovable.sql============="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (flg) {
			if (result.getBoolean("PRINT_FLG", 0)) {
				return false;
			}
		} else {
			
			//�ж������ҽ��Ϊ�ײ���ҽ�����Ѵ�Ʊ�󽫲���ɾ��
			//add by huangtt 20160316
			if(result.getValue("MEM_PACKAGE_ID", 0).length() > 0){
				if (result.getBoolean("PRINT_FLG", 0)) {
					return false;
				}
			}
			
			
			if (result.getBoolean("BILL_FLG", 0)) {
				return false;
				// if("C".equalsIgnoreCase(result.getValue("BILL_TYPE",0))){
				// return false;
				// }
				// if(result.getBoolean("EXEC_FLG",0)||result.getBoolean("PRINT_FLG",0)){
				// return false;
				// }
			}
		}
//		if (result.getBoolean("EXEC_FLG", 0)) {
//			return false;
//		}
		
		if (result.getBoolean("EXEC_FLG", 0)) {
			String orderCode = this.getItemString(row, "ORDER_CODE");
			sql = GET_SYS_FEE.replaceFirst("#", orderCode);
			TParm feeParm = new TParm(TJDODBTool.getInstance().select(sql));
			if(feeParm.getBoolean("EXEC_ORDER_FLG", 0)){
				if (!Operator.getID().equals(result.getValue("DR_CODE", 0))) {
					return false;
				}
			}else{
				return false;
			}
			
		}
		
		return true;
	}
	/**
	 * У�� �������Ѿ��Ǽǵ����ݲ���ɾ������
	 * STATUS=2 �ѵǼ�
	 * @param row
	 * @return
	 * ===========pangben 2013-1-29
	 */
	public boolean isRemoveMedAppCheckDate(int row){
		String orderCat1Code = this.getItemString(row, "ORDER_CAT1_CODE");
		String medApplyNo = this.getItemString(row, "MED_APPLY_NO");
		if(null==medApplyNo || medApplyNo.length()<=0)
			return true;
		String sql = GET_STATUS_FLG.replaceFirst("#", orderCat1Code).replaceFirst("#",
				medApplyNo);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()<=0){
			return true;
		}
		if (result.getInt("STATUS", 0)==2) {
			return false;
		}
		return true;
	}
	/**
	 * �����кŷ����շ�HL7��Ϣ��TParm
	 * 
	 * @param row
	 * @return
	 */
	public TParm getHl7Parm(int row, boolean send) {
		if (row < 0) {
			return null;
		}
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", getAdmType());
		parm.setData("CAT1_TYPE", this.getItemString(row, "CAT1_TYPE"));
		parm.setData("PAT_NAME", OpdRxSheetTool.getInstance().getPatName(
				this.getMrNo()));
		parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("LAB_NO", this.getItemString(row, "MED_APPLY_NO"));
		parm.setData("ORDER_NO", this.getItemString(row, "RX_NO"));
		parm.setData("SEQ_NO", this.getItemData(row, "SEQ_NO"));
		if (send) {
			parm.setData("FLG", "0");
		} else {
			parm.setData("FLG", "1");
		}

		// System.out.println("parm for charge=="+parm);
		return parm;
	}

	/**
	 * �ж�ɾ�����ļ������Ƿ���Ҫ����ȡ������Ϣ�������Ҫ����ŵ��ӿ�������
	 * 
	 * @param hl7List
	 */
	public void setDischargeList(List hl7List) {
		TParm deleteParm = this.getBuffer(this.DELETE);
		if (deleteParm == null || deleteParm.getCount() <= 0) {
			return;
		}
		int count = deleteParm.getCount();
		for (int i = 0; i < count; i++) {
			if (!StringTool.getBoolean(deleteParm.getValue("BILL_FLG", i))) {
				continue;
			}
			if ("5".equalsIgnoreCase(deleteParm.getValue("RX_TYPE", i))
					&& StringTool.getBoolean(deleteParm.getValue("SETMAIN_FLG",
							i))) {
				TParm exaDisParm = new TParm();
				exaDisParm.setData("ADM_TYPE", getAdmType());
				exaDisParm.setData("CAT1_TYPE", deleteParm.getValue(
						"CAT1_TYPE", i));
				exaDisParm.setData("PAT_NAME", OpdRxSheetTool.getInstance()
						.getPatName(this.getMrNo()));
				exaDisParm.setData("CASE_NO", this.getCaseNo());
				exaDisParm.setData("LAB_NO", deleteParm.getValue(
						"MED_APPLY_NO", i));
				exaDisParm.setData("ORDER_NO", deleteParm.getValue("RX_NO", i));
				exaDisParm.setData("SEQ_NO", deleteParm.getInt("SEQ_NO", i));
				exaDisParm.setData("FLG", "1");
				hl7List.add(exaDisParm);
			}
		}
	}

	/**
	 * ���ҩƷ�Ƿ��Ѿ����䷢ �Ƿ������ҩ
	 * 
	 * @param type
	 *            String "EXA":��ҩ "CHN":��ҩ
	 * @param row
	 *            int
	 *            flg У��ɾ�����޸Ĳ�����ɾ��������ɾ��һ�����ݣ�ֻ��ɾ������ǩ
	 *            flg true ɾ��һ�� �����޸Ķ���ʹ��   false  ɾ������ǩʹ��
	 * @return boolean 
	 */
	public boolean checkDrugCanUpdate(String type, int row,TParm parm,boolean flg) {
		boolean needExamineFlg = false;
		// �������ҩ ��˻���ҩ��Ͳ������ٽ����޸Ļ���ɾ��
		if ("MED".equals(type)) {
			// �ж��Ƿ����
			needExamineFlg = PhaSysParmTool.getInstance().needExamine();
		}
		// �������ҩ ��˻���ҩ��Ͳ������ٽ����޸Ļ���ɾ��
		if ("CHN".equals(type)) {
			// �ж��Ƿ����
			needExamineFlg = PhaSysParmTool.getInstance().needExamineD();
		}
		// ��������Ա���� ��������ҩ��Ա ��ô��ʾҩƷ����� ���������޸�
		// if (this.getItemString(row, "PHA_CHECK_CODE").length() > 0
		// && this.getItemString(row, "PHA_RETN_CODE").length() == 0) {
		// return false;
		// }
		// ������������ ��ô�ж����ҽʦ�Ƿ�Ϊ��
		//====pangben 2014-1-1 �޸�ʵʱУ��ҽ��״̬
//		if (needExamineFlg) {
//			// ��������Ա���� ��������ҩ��Ա ��ô��ʾҩƷ����� ���������޸�
//			if (this.getItemString(row, "PHA_CHECK_CODE").length() > 0
//					&& this.getItemString(row, "PHA_RETN_CODE").length() == 0) {
//				return false;
//			}
//		} else {// û��������� ֱ����ҩ
//			// �ж��Ƿ�����ҩҩʦ
//			if (this.getItemString(row, "PHA_DOSAGE_CODE").length() > 0
//					&& this.getItemString(row, "PHA_RETN_CODE").length() == 0) {
//				return false;// �Ѿ���ҩ���������޸�
//			}
//		}
		TParm order=this.getRowParm(row);
		int reIndex=OrderTool.getInstance().checkPhaIsExe(needExamineFlg, order,flg);
		switch (reIndex) {// 0���� 1û�����䷢  2.û���˷�
		case 1:
			parm.setData("MESSAGE","E0189");
			return false;
		case 2:
			parm.setData("MESSAGE","�ѼƷѲ������޸Ļ�ɾ������");
			parm.setData("MESSAGE_FLG","Y");
			return false;
		case 3:
			//true:ɾ��һ��  false ɾ������ǩ ���շѵ����Ѿ���ҩ״̬
			parm.setData("MESSAGE","����ɾ�����������˷�");
			parm.setData("MESSAGE_FLG","Y");
			parm.setData("MESSAGE_INDEX","Y");
			return false;
		case 4://true:ɾ��һ��  false ɾ������ǩ ���շѵ����Ѿ���ҩ״̬
			parm.setData("MESSAGE","��������ҩ�����޸ģ���ɾ������");
			parm.setData("MESSAGE_FLG","Y");
			parm.setData("MESSAGE_INDEX","Y");
			return false;
		case 5://����ҩ  δ�շ�
			parm.setData("MESSAGE","��������ҩ�����޸ģ���ɾ������");
			parm.setData("MESSAGE_FLG","Y");
			parm.setData("MESSAGE_INDEX","Y");
			return false;
		}
		return true;
	}
	/**
	 * ��ȡ����ҽ��ϸ���Ĭ������
	 * 
	 * @param order_code
	 *            String ϸ��CODE
	 * @param orderset_code
	 *            String ����CODE
	 * @return double
	 */
	public double getOrderSetTot(String order_code, String orderset_code) {
		String sql = "SELECT DOSAGE_QTY FROM SYS_ORDERSETDETAIL WHERE ORDERSET_CODE='"
				+ orderset_code + "' AND ORDER_CODE = '" + order_code + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result.getDouble("DOSAGE_QTY", 0);
	}

	/**
	 * �жϸô���ǩ�Ƿ���ר�ô���ǩ ��������ܿ���ͨҩƷ ���������ô���ܿ�ר�ô���ǩҩƷ
	 * 
	 * @param rxNo
	 *            String
	 * @param orderCode
	 *            String
	 * @return int 0��orderCode���ڸô���ǩ���� 1���ô�����ר�ô���ǩ ��ҽ������ר�ô���ҩƷ���ܿ��� 2���ô���ǩ����ר�ô���ǩ
	 *         ��ҽ����ר�ô���ǩҩƷ���ܿ��� 3�����ǹ���ҩƷ ������Order�Ĺ���ҩƷ�ȼ���ô���ǩ�����������ڴ˴���ǩ�Ͽ���
	 */
	public int isPrnRx(String rxNo, String orderCode) {
		String oldFilter = this.getFilter();
		this.setFilter("RX_NO = '" + rxNo + "'");
		this.filter();
		int count = this.rowCount();
		String oldOrder = this.getItemString(count - 2, "ORDER_CODE");// ��һ��order
		this.setFilter(oldFilter);
		this.filter();
		// �����������0��˵���ô���ǩ��û�п�ҩ ���Կ���ͨҩҲ���Կ�ר�ô���ǩҩƷ
		if (count <= 0 || oldOrder.length() <= 0) {
			return 0;
		}
		// ��ѯ��ORDER�Ƿ���ר�ô���ǩҩƷ
		String sql = "SELECT B.PRNSPCFORM_FLG,B.CTRLDRUGCLASS_CODE "
				+ " FROM PHA_BASE A,SYS_CTRLDRUGCLASS B "
				+ " WHERE A.CTRLDRUGCLASS_CODE = B.CTRLDRUGCLASS_CODE(+) AND A.ORDER_CODE='"
				+ orderCode + "' ";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
		String newOrderFlg = result1.getBoolean("PRNSPCFORM_FLG", 0) ? "Y"
				: "N";
		// ��ѯ�ô�������һ��ҽ���Ƿ���ר�ô���ǩҩƷ
		String sql2 = "SELECT B.PRNSPCFORM_FLG,B.CTRLDRUGCLASS_CODE "
				+ " FROM PHA_BASE A,SYS_CTRLDRUGCLASS B "
				+ " WHERE A.CTRLDRUGCLASS_CODE = B.CTRLDRUGCLASS_CODE(+) AND A.ORDER_CODE='"
				+ oldOrder + "' ";
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		String oldOrderFlg = result2.getBoolean("PRNSPCFORM_FLG", 0) ? "Y"
				: "N";
		// ��order����һ��Order����ͬ���� ��ô���Կ���ͬһ�ִ���ǩ��
		if (oldOrderFlg.equals(newOrderFlg)
				&& (result1.getValue("CTRLDRUGCLASS_CODE", 0).equals(
						result2.getValue("CTRLDRUGCLASS_CODE", 0))
						|| result1.getValue("CTRLDRUGCLASS_CODE", 0).length() == 0 || result2
						.getValue("CTRLDRUGCLASS_CODE", 0).length() == 0)) {
			return 0;// ��order���Կ���
		} else {// ��order���ɿ���
			// ����ר������ǩ ���ǹ��Ƶȼ���ͬ
			if (oldOrderFlg.equals(newOrderFlg)
					&& !result1.getValue("CTRLDRUGCLASS_CODE", 0).equals(
							result2.getValue("CTRLDRUGCLASS_CODE", 0))) {
				return 3;
			}
			if ("Y".equals(oldOrderFlg)) {// ר�ô���
				return 1;
			} else {// ����ר�ô���
				return 2;
			}
		}
	}

	/**
	 * �������� ������λС��
	 * 
	 * @param value
	 *            double
	 * @return double
	 */
	public double roundAmt(double value) {
		double result = 0;
		if (value > 0)
			result = ((int) (value * 100.0 + 0.5)) / 100.0;
		else if (value < 0)
			result = ((int) (value * 100.0 - 0.5)) / 100.0;
		return result;
	}

	/**
	 * �ж��Ƿ��˿��
	 * 
	 * @param orderCode
	 *            String
	 */
	private boolean isCheckKC(String orderCode) {
		String sql = SYSSQL.getSYSFee(orderCode);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getBoolean("IS_REMARK", 0))// �����ҩƷ��ע��ô�Ͳ���˿��
			return false;
		else
			// ����ҩƷ��ע�� Ҫ��˿��
			return true;
	}

	public void updateMED(ODO odo) {
		TParm orderParm = odo.getOpdOrder().getBuffer("Modify!");
		// odo.getOpdOrder().setFilter("CAT1_TYPE in('LIS','RIS')");
		// odo.getOpdOrder().filter();
		// TParm modifyParm = odo.getOpdOrder().getModifiedExaOrder();

		TParm primaryParm = odo.getOpdOrder().getBuffer("Filter!");
		int primaryCount = primaryParm.getCount("SEQ_NO");

		for (int i = 0; i < orderParm.getCount("MED_APPLY_NO"); i++) {
			String APPLICATION_NO = (String) orderParm.getData("MED_APPLY_NO",
					i);

			if (StringUtil.isNullString(APPLICATION_NO)) {
				continue;
			}
			Integer SEQ_NO = Integer.valueOf(orderParm.getData("SEQ_NO", i)
					+ "");
			String CASE_NO = (String) orderParm.getData("CASE_NO", i);
			String ORDER_CODE = (String) orderParm.getData("ORDER_CODE", i);
			med.setFilter("APPLICATION_NO='" + APPLICATION_NO + "' and SEQ_NO="
					+ SEQ_NO + " and ORDER_CODE='" + ORDER_CODE + "'");
			med.filter();
			for (int primaryIndex = 0; primaryIndex < primaryCount; primaryIndex++) {
				TParm tempParm = primaryParm.getRow(primaryIndex);
				if (tempParm.getValue("CAT1_TYPE").equals(
						orderParm.getData("CAT1_TYPE", i))
						&& tempParm.getValue("RX_NO").equals(
								orderParm.getData("RX_NO", i))
						&& tempParm.getValue("SEQ_NO").equals(
								orderParm.getData("SEQ_NO", i) + "")
						&& tempParm.getValue("ORDER_CODE").equals(
								orderParm.getData("ORDER_CODE", i))) {

					// //////////////////////////////////////////////////////////////////////////////////////////////////
					// xueyf
					// value.add(labNo);//0
					// value.add(cat1Type);//1
					int medRow = 0;
					med.setItem(medRow, "CAT1_TYPE", tempParm
							.getData("CAT1_TYPE"));
					// // ORDER_NO
					med.setItem(medRow, "ORDER_NO", tempParm.getData("RX_NO"));
					// value.add(this.getItemString(row, "RX_NO"));//2
					// //SEQ_NO
					// System.out.println("med_apply~~~~~~~~~~:"+this.getItemData(row,
					// "SEQ_NO"));
					med.setItem(medRow, "SEQ_NO", tempParm.getData("SEQ_NO"));
					// value.add(this.getItemData(row, "SEQ_NO"));//3
					// //ORDER_CODE
					med.setItem(medRow, "ORDER_CODE", tempParm
							.getData("ORDER_CODE"));
					// value.add(this.getItemData(row, "ORDER_CODE"));//4
					// //ORDER_DESC
					med.setItem(medRow, "ORDER_DESC", tempParm
							.getData("ORDER_DESC"));
					// value.add(this.getItemData(row, "ORDER_DESC"));
					// //ORDER_DR_CODE
					med.setItem(medRow, "ORDER_DR_CODE", tempParm
							.getData("DR_CODE"));
					// value.add(Operator.getID());
					Timestamp now = this.getDBTime();
					// //ORDER_DATE
					med.setItem(medRow, "ORDER_DATE", now);
					// value.add(now);
					// //ORDER_DEPT_CODE
					med.setItem(medRow, "ORDER_DEPT_CODE", odo.getRegPatAdm()
							.getItemString(0, "REALDEPT_CODE"));
					// value.add(Operator.getDept());
					// //START_DTTM
					med.setItem(medRow, "START_DTTM", now);
					// value.add(now);
					// //EXEC_DEPT_CODE
					med.setItem(medRow, "EXEC_DEPT_CODE", tempParm
							.getData("EXEC_DEPT_CODE"));
					// value.add(this.getItemData(row, "EXEC_DEPT_CODE"));
					// //EXEC_DR_CODE
					med.setItem(medRow, "EXEC_DR_CODE", "");
					// value.add("");
					// //OPTITEM_CODE
					med.setItem(medRow, "OPTITEM_CODE", tempParm
							.getData("OPTITEM_CODE"));
					// value.add(this.getItemData(row, "OPTITEM_CODE"));
					String optitemDesc = StringUtil.getDesc("SYS_DICTIONARY",
							"CHN_DESC", " GROUP_ID = 'SYS_OPTITEM' AND ID='"
									+ tempParm.getData("OPTITEM_CODE") + "'");
					// //OPTITEM_CHN_DESC
					med.setItem(medRow, "OPTITEM_CHN_DESC", optitemDesc);
					// value.add(optitemDesc);
					// //ORDER_CAT1_CODE
					med.setItem(medRow, "ORDER_CAT1_CODE", tempParm
							.getData("ORDER_CAT1_CODE"));
					// URGENT_FLG
					med.setItem(medRow, "URGENT_FLG", tempParm
							.getData("URGENT_FLG"));
					// value.add(this.getItemData(row, "ORDER_CAT1_CODE"));
					String dealSystem = StringUtil.getDesc("SYS_OPTITEM",
							"OPTITEM_CHN_DESC", "OPTITEM_CODE='"
									+ tempParm.getData("OPTITEM_CODE") + "'");
					// //DEAL_SYSTEM
					med.setItem(medRow, "DEAL_SYSTEM", dealSystem);
					// value.add(OrderUtil.getInstance().getDealSystem(this.getItemString(row,
					// "ORDER_CAT1_CODE")));
					// //RPTTYPE_CODE
					med.setItem(medRow, "RPTTYPE_CODE", tempParm
							.getData("RPTTYPE_CODE"));
					// value.add(this.getItemData(row, "RPTTYPE_CODE"));
					// //DEV_CODE
					med.setItem(medRow, "DEV_CODE", tempParm
							.getData("DEV_CODE"));
					// value.add(this.getItemData(row, "DEV_CODE"));
					// //REMARK
					med.setItem(medRow, "REMARK", tempParm.getData("DR_NOTE"));
					// value.add(this.getItemData(row, "DR_NOTE"));
					// OWN_AMT
					med.setItem(medRow, "OWN_AMT", tempParm.getData("OWN_AMT"));
					// AR_AMT
					med.setItem(medRow, "AR_AMT", tempParm.getData("AR_AMT"));

					// CASE_NO
					med.setItem(medRow, "CASE_NO", this.getCaseNo());
					// MR_NO
					med.setItem(medRow, "MR_NO", this.getMrNo());
					// ADM_TYPE
					// System.out.println("MED_APPLY~~~~~~~~~~~~~~~~�ż�����ʾ��"+getAdmType());
					med.setItem(medRow, "ADM_TYPE", getAdmType());
					// PAT_NAME
					med.setItem(medRow, "PAT_NAME", odo.getPatInfo()
							.getItemString(0, "PAT_NAME"));
					// PAT_NAME1
					med.setItem(medRow, "PAT_NAME1", odo.getPatInfo()
							.getItemString(0, "PAT_NAME1"));
					// BIRTH_DATE
					med.setItem(medRow, "BIRTH_DATE", odo.getPatInfo()
							.getItemData(0, "BIRTH_DATE"));
					// SEX_CODE
					med.setItem(medRow, "SEX_CODE", odo.getPatInfo()
							.getItemData(0, "SEX_CODE"));
					// POST_CODE
					med.setItem(medRow, "POST_CODE", odo.getPatInfo()
							.getItemData(0, "POST_CODE"));
					// ADDRESS
					med.setItem(medRow, "ADDRESS", odo.getPatInfo()
							.getItemData(0, "ADDRESS"));
					// COMPANY
					med.setItem(medRow, "COMPANY", odo.getPatInfo()
							.getItemData(0, "COMPANY"));
					// TEL
					med.setItem(medRow, "TEL", odo.getPatInfo().getItemData(0,
							"TEL"));
					// IDNO
					med.setItem(medRow, "IDNO", odo.getPatInfo().getItemData(0,
							"IDNO"));
					// DEPT_CODE
					med.setItem(medRow, "DEPT_CODE", odo.getRegPatAdm()
							.getItemString(0, "REALDEPT_CODE"));
					// REGION_CODE
					med.setItem(medRow, "REGION_CODE", Operator.getRegion());
					// CLINICAREA_CODE
					med.setItem(medRow, "CLINICAREA_CODE", odo.getRegPatAdm()
							.getItemString(0, "CLINICAREA_CODE"));
					// CLINICROOM_NO CLINICROOM_NO
					med.setItem(medRow, "CLINICROOM_NO", odo.getRegPatAdm()
							.getItemString(0, "CLINICROOM_NO"));
					// ICD_TYPE
					int main = odo.getDiagrec().getMainDiag();
					med.setItem(medRow, "ICD_TYPE", odo.getDiagrec()
							.getItemData(main, "ICD_TYPE"));
					// ICD_CODE
					med.setItem(medRow, "ICD_CODE", odo.getDiagrec()
							.getItemData(main, "ICD_CODE"));
					// STATUS
					med.setItem(medRow, "STATUS", "0");
					// BILL_FLG
					med.setItem(medRow, "BILL_FLG", "N");
					// SEND_FLG
					med.setItem(medRow, "SEND_FLG", "0");
					// OPT_USER
					med.setItem(medRow, "OPT_USER", Operator.getID());
					// OPT_DATE
					med.setItem(medRow, "OPT_DATE", now);
					// OPT_TERM
					med.setItem(medRow, "OPT_TERM", Operator.getIP());
					// PRINT_FLG
					med.setItem(medRow, "PRINT_FLG", "N");
					// ORDER_ENG_DESC
					med.setItem(medRow, "ORDER_ENG_DESC", tempParm
							.getData("TRADE_ENG_DESC"));

					med.setActive(medRow, true);
				}
			}
		}
	}

	/**
	 * �жϴ���ҽ���Ƿ��Ѿ��շѣ��ż���ҽ��վ �Ѿ��շ�ҩƷҽ�� �����Ըı�ע ����
	 * 
	 * @return
	 * ============pangben 2012-7-19
	 */
	public boolean getOrderCodeIsBillFlg(TParm parm) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ORDER_CODE,BILL_FLG FROM OPD_ORDER WHERE CASE_NO='")
				.append(parm.getValue("CASE_NO")).append("' AND RX_NO='")
				.append(parm.getValue("RX_NO")).append("' AND SEQ_NO='")
				.append(parm.getValue("SEQ_NO")).append("' AND ORDER_CODE='")
				.append(parm.getValue("ORDER_CODE")).append("'");
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return false;
		} else {
			if (null!=result.getValue("BILL_FLG",0)&&"Y".equals(result.getValue("BILL_FLG",0))) {
				return true;
			}else
				return false;
		}
	}

	/**
	 * ��ô˴��޸ĵĽ�� EKT�ۿ���ʾʹ��
	 */
	private double getUpdateOrderAmt(TParm orderParm, TParm orderOldParm) {
		for (int j = 0; j < orderOldParm.getCount("ORDER_CODE"); j++) {
			if (orderParm.getValue("RX_NO").equals(
					orderOldParm.getValue("RX_NO", j))
					&& orderParm.getValue("SEQ_NO").equals(
							orderOldParm.getValue("SEQ_NO", j))) {
				if (orderParm.getDouble("AR_AMT") == orderOldParm.getDouble(
						"AR_AMT", j)) {
					return 0;
				}
				return orderParm.getDouble("AR_AMT")
						- orderOldParm.getDouble("AR_AMT", j);
			}
		}
		return 0;
	}
	public Map<String, String> getPreCaseNos() {
		return preCaseNos;
	}
	public void setPreCaseNos(Map<String, String> preCaseNos) {
		this.preCaseNos = preCaseNos;
	}
	
	private final String SQL_OPD_SYSPARM = 
		" SELECT " +
		" AGE, PAGE_NUM, DCT_TAKE_DAYS, " +
		" DCT_TAKE_QTY, PREGNANT_WEEKS, SAVERDU_FLG, " +
		" W_NHICHECK_FLG, W_TYPE_NUM, W_TAKE_DAYS, " +
		" W_TOT_AMT, G_NHICHECK_FLG, G_TYPE_NUM, " +
		" G_TAKE_DAYS, G_TOT_AMT, OPT_USER, " +
		" OPT_DATE, OPT_TERM, G_DCTAGENT_CODE, " +
		" G_FREQ_CODE, G_ROUTE_CODE, E_DAYS, " +
		" DS_MED_DAY, CH_AGE, MED_FLG, " +
		" BOX_DISPENSE_FLG" +
		" FROM OPD_SYSPARM";
	
	private TParm opdSysParm;
	
	
	/**
	 * �ж�ҽ���Ƿ��ɾ��������ִ��ҽ����ӣ�
	 * 
	 * @param row
	 *            
	 * @return 
	 */
	public boolean isRemovableOp(int row, boolean flg) {
		String caseNo = this.getItemString(row, "CASE_NO");
		String rxNo = this.getItemString(row, "RX_NO");
		int seq = this.getItemInt(row, "SEQ_NO");
		String sql = GET_EXEC_FLG.replaceFirst("#", caseNo).replaceFirst("#",
				rxNo).replaceFirst("#", seq + "");
		// System.out.println("isRemovable.sql============="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (flg) {
			if (result.getBoolean("PRINT_FLG", 0)) {
				return false;
			}
		} else {
			if (result.getBoolean("BILL_FLG", 0)) {
				return false;
				// if("C".equalsIgnoreCase(result.getValue("BILL_TYPE",0))){
				// return false;
				// }
				// if(result.getBoolean("EXEC_FLG",0)||result.getBoolean("PRINT_FLG",0)){
				// return false;
				// }
			}
		}

		if (result.getBoolean("EXEC_FLG", 0)) {
			String orderCode = this.getItemString(row, "ORDER_CODE");
			sql = GET_SYS_FEE.replaceFirst("#", orderCode);
			TParm feeParm = new TParm(TJDODBTool.getInstance().select(sql));
			if(feeParm.getBoolean("EXEC_ORDER_FLG", 0)){
				if (!Operator.getID().equals(result.getValue("DR_CODE", 0))) {
					return false;
				}
			}else{
				return false;
			}
			
		}
		
		return true;
	}
	
}