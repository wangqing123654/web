package jdo.odi;

import com.dongyang.data.*;
import com.dongyang.jdo.*;
import com.javahis.manager.*;

import jdo.sys.*;

import com.dongyang.util.StringTool;

import java.util.Map;
import java.util.HashMap;

import com.javahis.util.StringUtil;

import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;

import com.javahis.util.OrderUtil;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;

/**
 * <p>
 * Title: סԺģ���ܶ���
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: JAVAHIS
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author ZangJH
 * @version 1.0
 */
public class OdiObject extends TDSObject {
	/**
	 * ����������
	 */
	private String actionName = "action.odi.ODIAction";
	/**
	 * ����ODI_ORDER��TDSObject���е�KEYֵ������
	 */
	public static final String ODI_ORDERTABLENAME = "ODI_ORDER";
	/**
	 * ����ODI_ORDER_HISTORY��TDSObject���е�KEYֵ������
	 */
	public static final String ODI_ORDER_HISTORYTABLENAME = "ODI_ORDER_HISTORY";
	/**
	 * ����ODI_DSPNM��TDSObject���е�KEYֵ������
	 */
	public static final String ODI_DSPNMTABLENAME = "ODI_DSPNM";
	/**
	 * ����ODI_DSPND��TDSObject���е�KEYֵ������
	 */
	public static final String ODI_DSPNDTABLENAME = "ODI_DSPND";
	/**
	 * סԺ���ڰ�ҩʱ��
	 */
	public static final String OID_DSPN_TIME = "OID_DSPN_TIME";
	/**
	 * סԺ����ʱ��
	 */
	public static final String ODI_START_TIME = "ODI_START_TIME";
	/**
	 * ��ʱ��ҩԤ��Ƶ��
	 */
	public static final String ODI_UDD_STAT_CODE = "ODI_UDD_STAT_CODE";
	/**
	 * ��ʱ����Ԥ��Ƶ��
	 */
	public static final String ODI_ODI_STAT_CODE = "ODI_ODI_STAT_CODE";
	/**
	 * ���ڴ���Ԥ��Ƶ��
	 */
	public static final String ODI_ODI_DEFA_FREG = "ODI_ODI_DEFA_FREG";
	/**
	 * ����ҩ������������ҩʱ��
	 */
	public static final String OID_IVA_EXPANDTIME = "OID_IVA_EXPANDTIME";
	/**
	 * ��ʿ���ע��
	 */
	public static final String INW_NS_CHECK_FLG = "INW_NS_CHECK_FLG";
	/**
	 * ��ͯ����ǩ�����趨
	 */
	public static final String RX_CHILD_AGE = "RX_CHILD_AGE";
	/**
	 * ����ǩÿҳ������
	 */
	public static final String PAGE_NUM = "PAGE_NUM";
	/**
	 * Ԥ����Ƭ����
	 */
	public static final String DCT_TAKE_DAYS = "DCT_TAKE_DAYS";
	/**
	 * Ԥ����Ƭʹ�ü���
	 */
	public static final String DCT_TAKE_QTY = "DCT_TAKE_QTY";
	/**
	 * ��ҩ����Ƶ�Σ�Ĭ��һ������
	 */
	public static final String G_FREQ_CODE = "G_FREQ_CODE";
	/**
	 * ��ҩ�����÷����ڷ�
	 */
	public static final String G_ROUTE_CODE = "G_ROUTE_CODE";
	/**
	 * ��ҽ���ã��巨
	 */
	public static final String G_DCTAGENT_CODE = "G_DCTAGENT_CODE";
	/**
	 * סԺҽ��վע��
	 */
	private boolean odiFlg = false;

	/**
	 * ������
	 */
	public OdiObject() {
		this.setAttribute("OPT_USER", Operator.getID());
		this.setAttribute("OPT_TERM", Operator.getIP());
		// ��ʿ���ע��
		this.setAttribute(INW_NS_CHECK_FLG, getOdiSysParmData("NS_CHECK_FLG"));
		// סԺ���ڰ�ҩʱ��
		this.setAttribute(OID_DSPN_TIME, getOdiSysParmData("DSPN_TIME"));
		// סԺ����ʱ��
		this.setAttribute(ODI_START_TIME, getOdiSysParmData("START_TIME"));
		// ��ʱ��ҩԤ��Ƶ��
		this.setAttribute(ODI_UDD_STAT_CODE, getOdiSysParmData("UDD_STAT_CODE"));
		// ��ʱ����Ԥ��Ƶ��
		this.setAttribute(ODI_ODI_STAT_CODE, getOdiSysParmData("ODI_STAT_CODE"));
		// ���ڴ���Ԥ��Ƶ��
		this.setAttribute(ODI_ODI_DEFA_FREG, getOdiSysParmData("ODI_DEFA_FREG"));
		// ����ҩ������������ҩʱ��
		this.setAttribute(OID_IVA_EXPANDTIME,
				getOdiSysParmData("IVA_EXPANDTIME"));
		// ��ͯ����ǩ�����趨
		this.setAttribute(RX_CHILD_AGE, getOpdSysParmData("AGE"));
		// ����ǩÿҳ������
		this.setAttribute(PAGE_NUM, getOpdSysParmData("PAGE_NUM"));
		// Ԥ����Ƭ����
		this.setAttribute(DCT_TAKE_DAYS, getOpdSysParmData("DCT_TAKE_DAYS"));
		// Ԥ����Ƭʹ�ü���
		this.setAttribute(DCT_TAKE_QTY, getOpdSysParmData("DCT_TAKE_QTY"));
		// ��ҩ����Ƶ�Σ�Ĭ��һ������
		this.setAttribute(G_FREQ_CODE, getOpdSysParmData("G_FREQ_CODE"));
		// ��ҩ�����÷����ڷ�
		this.setAttribute(G_ROUTE_CODE, getOpdSysParmData("G_ROUTE_CODE"));
		// ��ҽ���ã��巨
		this.setAttribute(G_DCTAGENT_CODE, getOpdSysParmData("G_DCTAGENT_CODE"));
	}

	/**
	 * ����SQL����ʼ��DataStore
	 * 
	 * @param name
	 *            String
	 * @param sqlStr
	 *            String
	 * @param newRowDefaultColumn
	 *            String
	 * @param saveNewRowDefaultColumn
	 *            String
	 * @param saveModifiedRowDefaultColumn
	 *            String
	 */
	public void setSQL(String name, String sqlStr, String newRowDefaultColumn,
			String saveNewRowDefaultColumn, String saveModifiedRowDefaultColumn) {
		// ��ӵ����۲��б�(�Ƿ��й۲������ҵ��������)
		TDS ds = addDS(name);
		// ����DATASTORE������
		ds.setSQL(sqlStr);
		// Ϊ��ʾ���������֡�ר��ʹ�õ�һ������
		String sql = this.getAttributeString("PAT_NAME");
		ds.addColumnSql("PAT_NAME", sql);
		// ��������Ĭ�ϴ�����һ��Ϊ����
		ds.setNewRowDefaultColumn(newRowDefaultColumn);
		// ����ʱ�е�Ĭ�������в�������
		ds.setSaveNewRowDefaultColumn(saveNewRowDefaultColumn);
		// ����ʱ�޸ĵ��б�������
		ds.setSaveModifiedRowDefaultColumn(saveModifiedRowDefaultColumn);
	}

	/**
	 * ����ODI_ORDER�����ݼ�
	 * 
	 * @param sqlStr
	 *            String
	 */
	public void setOdiOrderSQL(TDS ds) {
		TParm parm = this.getAttributeTParm("ORDERWHEREDATA");
		String whereStr = this.creatWhereStr(parm);
		ds.setSQL("SELECT * FROM ODI_ORDER " + whereStr
				+ " ORDER BY ORDER_NO,ORDER_SEQ");
	}

	/**
	 * ����ODI_DSPNM�����ݼ�
	 * 
	 * @param sqlStr
	 *            String
	 */
	public void setOdiDspnmSQL(TDS ds) {
		TParm parm = this.getAttributeTParm("DSPNMWHEREDATA");
		String whereStr = this.creatWhereStr(parm);
		ds.setSQL("SELECT * FROM ODI_DSPNM " + whereStr);
	}

	/**
	 * ����ODI_DSPND�����ݼ�
	 * 
	 * @param sqlStr
	 *            String
	 */
	public void setOdiDspndSQL(TDS ds) {
		TParm parm = this.getAttributeTParm("DSPNDWHEREDATA");
		String whereStr = this.creatWhereStr(parm);
		ds.setSQL("SELECT * FROM ODI_DSPND " + whereStr);
	}

	/**
	 * ����ODI_ORDER�����ݼ�
	 * 
	 * @param sqlStr
	 *            String
	 */
	public void setOdiOrderHistorySQL(TDS ds) {
		TParm parm = this.getAttributeTParm("ORDERWHEREDATA");
		String whereStr = this.creatWhereStr(parm);
		ds.setSQL("SELECT * FROM ODI_ORDER_HISTORY " + whereStr
				+ " ORDER BY ORDER_NO,ORDER_SEQ");
	}

	/**
	 * �Զ�������ODI_ORDER�����ݼ�
	 * 
	 * @param sqlStr
	 *            String
	 */
	public void setOdiOrderSQL(TDS ds, String sqlStr) {
		ds.setSQL(sqlStr);
	}

	/**
	 * �Զ�������ODI_DSPNM�����ݼ�
	 * 
	 * @param sqlStr
	 *            String
	 */
	public void setOdiDspnmSQL(TDS ds, String sqlStr) {
		ds.setSQL(sqlStr);
	}

	/**
	 * �Զ�������ODI_DSPND�����ݼ�
	 * 
	 * @param sqlStr
	 *            String
	 */
	public void setOdiDspndSQL(TDS ds, String sqlStr) {
		ds.setSQL(sqlStr);
	}

	/**
	 * ���WHERE����
	 * 
	 * @param parm
	 *            TParm
	 * @return String
	 */
	public String creatWhereStr(TParm parm) {
		String whereStr = " WHERE ";
		String str[] = parm.getNames();
		if (str.length == 0)
			return "";
		for (String temp : str) {
			// ����Ƿ��ǵ�һ��Ԫ�أ����ӡ�AND����
			if (temp == (str[0])) {
				whereStr += temp + "='" + parm.getData(temp).toString() + "'";
				continue;
			}
			whereStr += " AND " + temp + "='" + parm.getData(temp).toString()
					+ "'";
		}
		return whereStr;
	}

	/**
	 * ִ��DataStore����
	 * 
	 * @return boolean
	 */
	public boolean retrieve() {
		if (!super.retrieve()) {
			return false;
		}
		// ִ�а�ҽ��վ�۲��߶���
		this.bindOdiStationObserverObject();
		return true;
	}

	/**
	 * ע��סԺҽ��վ�۲��߶����
	 */
	public void bindOdiStationObserverObject() {
		// �õ���ʿ���ִ�б��
		boolean nsCheckFlg = this.getAttributeBoolean(INW_NS_CHECK_FLG);
		// סԺҽ��վ�Ĺ۲��߰�(�ж�����:��ʿ���ע��ΪN������ҽ��վʱ)
		if (!nsCheckFlg && odiFlg) {
			// System.out.println("סԺҽ��վ�Ĺ۲��߰�(�ж�����:��ʿ���ע��ΪN������ҽ��վʱ)");
			// �õ�ODI_ORDER����
			TDS odiOrderDs = this.getDS(ODI_ORDERTABLENAME);
			// ��Ϊ�յ�ʱ���Զ�����ǰ̨������parm������TDS
			if (odiOrderDs == null) {
				odiOrderDs = new TDS();
				this.setOdiOrderSQL(odiOrderDs);
				odiOrderDs.retrieve();
				this.addDS(ODI_ORDERTABLENAME, odiOrderDs);
			}
			// �õ�ODI_DSPNM����
			TDS odiDspnmDs = this.getDS(ODI_DSPNMTABLENAME);
			if (odiDspnmDs == null) {
				odiDspnmDs = new TDS();
				this.setOdiDspnmSQL(odiDspnmDs);
				odiDspnmDs.retrieve();
				this.addDS(ODI_DSPNMTABLENAME, odiDspnmDs);
				// ��������Ĭ�ϴ�����һ��Ϊ����
				this.getDS(ODI_DSPNMTABLENAME)
						.setNewRowDefaultColumn("CASE_NO");
				// ����ʱ�е�Ĭ�������в�������
				this.getDS(ODI_DSPNMTABLENAME)
						.setSaveNewRowDefaultColumn(
								"OPT_USER;OPT_DATE:SAVE_TIME;OPT_TERM;ORDER_DATE:SAVE_TIME;ORDER_NO;ORDER_DR_CODE");
				// ����ʱ�޸ĵ��б�������
				this.getDS(ODI_DSPNMTABLENAME).setSaveModifiedRowDefaultColumn(
						"OPT_USER;OPT_DATE:SAVE_TIME;OPT_TERM");
			}
			// �õ�ODI_DSPND����
			TDS odiDspndDs = this.getDS(ODI_DSPNDTABLENAME);
			if (odiDspndDs == null) {
				odiDspndDs = new TDS();
				this.setOdiDspndSQL(odiDspndDs);
				odiDspndDs.retrieve();
				this.addDS(ODI_DSPNDTABLENAME, odiDspndDs);
				// ��������Ĭ�ϴ�����һ��Ϊ����
				this.getDS(ODI_DSPNDTABLENAME)
						.setNewRowDefaultColumn("CASE_NO");
				// ����ʱ�е�Ĭ�������в�������
				this.getDS(ODI_DSPNDTABLENAME)
						.setSaveNewRowDefaultColumn(
								"OPT_USER;OPT_DATE:SAVE_TIME;OPT_TERM;ORDER_DATE;ORDER_NO");
				// ����ʱ�޸ĵ��б�������
				this.getDS(ODI_DSPNDTABLENAME).setSaveModifiedRowDefaultColumn(
						"OPT_USER;OPT_DATE:SAVE_TIME;OPT_TERM");
			}
			// ����ODI_ORDER����۲���
			OdiOrderObserverODI odiOrder = new OdiOrderObserverODI();
			odiOrder.setName("ODI_ORDER");
			// ����ODI_ORDER����仯ʱ��Ҫ��עODI_DSPNM��������ݱ仯
			odiOrder.setDS(odiDspnmDs);
			// ע��ODI_ORDER�۲���
			odiOrderDs.addObserver(odiOrder);
			// ����ODI_DSPNM����۲���
			OdiDspnMObserverODI odiDspnm = new OdiDspnMObserverODI();
			odiDspnm.setName("ODI_DSPNM");
			// ����ODI_DSPNM����仯ʱ��Ҫ��עODI_DSPND��������ݱ仯
			odiDspnm.setDS(odiDspndDs);
			// ע��ODI_DSPNM�۲���
			odiDspnmDs.addObserver(odiDspnm);
		}
		// ��Ҫ��ʿ���
		if (nsCheckFlg && odiFlg) {
			// �õ�ODI_ORDER����
			TDS odiOrderDs = this.getDS(ODI_ORDERTABLENAME);
			// ��Ϊ�յ�ʱ���Զ�����ǰ̨������parm������TDS
			if (odiOrderDs == null) {
				odiOrderDs = new TDS();
				this.setOdiOrderSQL(odiOrderDs);
				odiOrderDs.retrieve();
				this.addDS(ODI_ORDERTABLENAME, odiOrderDs);
			}
			// ���ODI_ORDER�Ĺ۲���
			OdiOtherObserver otherObserver = new OdiOtherObserver();
			otherObserver.setName("OTHEROBSERVER");
			odiOrderDs.addObserver(otherObserver);
			// ��ʷ��¼��
			TDS odiOrderHistoryDs = this.getDS(ODI_ORDER_HISTORYTABLENAME);
			if (odiOrderHistoryDs == null) {
				odiOrderHistoryDs = new TDS();
				this.setOdiOrderHistorySQL(odiOrderHistoryDs);
				odiOrderHistoryDs.retrieve();
				this.addDS(ODI_ORDER_HISTORYTABLENAME, odiOrderHistoryDs);
			}
			// System.out.println("��ʷ��==");
			// odiOrderHistoryDs.showDebug();
		}
	}

	/**
	 * ���·���
	 * 
	 * @return boolean
	 */
	public boolean update() {
		// �õ���ʿ���ִ�б��
		boolean nsCheckFlg = this.getAttributeBoolean(INW_NS_CHECK_FLG);
		boolean falg = true;
		if (odiFlg) {
			TParm parm = new TParm();
			// ҽ��վ
			odiUpdateData();
			// ������Ҫ�����޸���ӵ���������
			String sqlArray[] = getUpdateSQL();
			// ������ҽ������
			String medSql[] = getMedSql();
			if (medSql != null) {
				sqlArray = StringUtil.getInstance().copyArray(sqlArray, medSql);
			}
			// ����
			/**
			 * for(String temp:sqlArray){ System.out.println("��ǰSQL:"+temp); }
			 **/
			parm.setData("ARRAY", sqlArray);
			TParm actionParm = TIOM_AppServer.executeAction(actionName,
					"saveOrder", parm);
			if (actionParm.getErrCode() < 0)
				falg = false;
			// ɾ���۲���
			if (!nsCheckFlg && odiFlg) {
				this.getDS(ODI_ORDERTABLENAME).removeObserver("ODI_ORDER");
				this.getDS(ODI_DSPNMTABLENAME).removeObserver("ODI_DSPNM");
			} else {
				this.getDS(ODI_ORDERTABLENAME).removeObserver("OTHEROBSERVER");
			}
		}
		return falg;
	}

	/**
	 * �õ�������ӿڸ������
	 * 
	 * @return String[]
	 */
	public String[] getMedSql() {
		String[] sqlArray;
		List list = new ArrayList();
		int arraySize = 0;
		int upArraySize = 0;
		TDS ds = this.getDS(this.ODI_ORDERTABLENAME);
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		int newOrder[] = ds.getNewRows(buff);
		int modifOrder[] = ds.getOnlyModifiedRows(buff);
		for (int i : newOrder) {
			if (!ds.isActive(i, buff))
				continue;
			TParm temp = ds.getRowParm(i, buff);
			// ����
			if ("LIS".equals(temp.getValue("CAT1_TYPE"))
					&& "Y".equals(temp.getValue("SETMAIN_FLG"))) {
				arraySize++;
				String sql = creatLISRISSQL(temp);
				// System.out.println("-=----------------------------"+sql);
				list.add(sql);
			}
			// ���
			if ("RIS".equals(temp.getValue("CAT1_TYPE"))
					&& "Y".equals(temp.getValue("SETMAIN_FLG"))) {
				arraySize++;
				String sql = creatLISRISSQL(temp);
				list.add(sql);
			}
		}
		for (int i : modifOrder) {
			TParm temp = ds.getRowParm(i, buff);
			// ����
			if ("LIS".equals(temp.getValue("CAT1_TYPE"))
					&& "Y".equals(temp.getValue("SETMAIN_FLG"))) {
				upArraySize++;
				String sql = creatLISRISUpdateSQL(temp);
				list.add(sql);
			}
			// ���
			if ("RIS".equals(temp.getValue("CAT1_TYPE"))
					&& "Y".equals(temp.getValue("SETMAIN_FLG"))) {
				upArraySize++;
				String sql = creatLISRISUpdateSQL(temp);
				list.add(sql);
			}
		}
		int delArraySize = 0; // SHIBL 20130111 add
		// ɾ����ҽ������
		int delCount = ds.getDeleteCount() < 0 ? 0 : ds.getDeleteCount();
		if (delCount > 0) {
			TParm delParm = ds.getBuffer(ds.DELETE);
			int delRowCount = delParm.getCount("ORDER_CODE");
			for (int i = 0; i < delRowCount; i++) {
				TParm temp = delParm.getRow(i);
				// ����
				if ("LIS".equals(temp.getValue("CAT1_TYPE"))
						&& "Y".equals(temp.getValue("SETMAIN_FLG"))) {
					delArraySize++;
					String sql = creatLISRISdelSQL(temp);
					list.add(sql);
				}
				// ���
				if ("RIS".equals(temp.getValue("CAT1_TYPE"))
						&& "Y".equals(temp.getValue("SETMAIN_FLG"))) {
					delArraySize++;
					String sql = creatLISRISdelSQL(temp);
					list.add(sql);
				}
			}
		}
		sqlArray = (String[]) list.toArray(new String[arraySize + upArraySize
				+ delArraySize]);
		// for(String temp:sqlArray)
		// System.out.println("��Ҫѭ����SQL��"+temp);
		if (sqlArray.length != 0)
			return sqlArray;
		else
			return null;
	}

	/**
	 * ɾ����� SHIBL 20130111 add
	 * 
	 * @param parm
	 * @return
	 */
	private String creatLISRISdelSQL(TParm parm) {
		String sqlStr = "DELETE FROM  MED_APPLY  WHERE  CAT1_TYPE='"
				+ parm.getValue("CAT1_TYPE") + "' AND APPLICATION_NO='"
				+ parm.getValue("MED_APPLY_NO") + "' AND ORDER_NO='"
				+ parm.getValue("ORDER_NO") + "' AND SEQ_NO='"
				+ parm.getValue("ORDER_SEQ") + "'";
		return sqlStr;
	}

	/**
	 * ��ɼ������SQL
	 * 
	 * @param parm
	 *            TParm
	 * @return String
	 */
	public String creatLISRISSQL(TParm parm) {
		// ��ѯ��������
		String optItemDesc = OrderUtil.getInstance().queryOptItem(
				parm.getValue("OPTITEM_CODE"));
		// �õ�������
		String icdType = OrderUtil.getInstance().queryDiagType(
				"" + this.getAttribute("MAINDIAG"));
		// �õ��Է��ܼ۸�
		double ownAmt = OrderUtil.getInstance().getOrderSetList(
				parm.getValue("ORDER_CODE"));
		// �ײ�ʱ��
		String startDttm = StringTool.getString(
				parm.getTimestamp("START_DTTM"), "yyyyMMddHHmmss");
		// �ӿڳ�������
		String dealSystem = OrderUtil.getInstance().getDealSystem(
				parm.getValue("ORDER_CAT1_CODE"));
		// Ӧ�����
		double arAmt = ownAmt
				* OrderUtil.getInstance().getOwnRate(
						"" + this.getAttribute("CTZ_CODE"),
						parm.getValue("ORDER_CODE"));
		String sqlStr = "INSERT INTO MED_APPLY"
				+ " (CAT1_TYPE, APPLICATION_NO, CASE_NO, IPD_NO, MR_NO,"
				+ "  ADM_TYPE, PAT_NAME, PAT_NAME1, BIRTH_DATE, SEX_CODE,"
				+ "  POST_CODE, ADDRESS,COMPANY,TEL,IDNO,"
				+ "  DEPT_CODE, REGION_CODE, CLINICROOM_NO, STATION_CODE, BED_NO,"
				+ " ORDER_NO,SEQ_NO,ORDER_CODE,ORDER_DESC, ORDER_DR_CODE,"
				+ " ORDER_DATE, ORDER_DEPT_CODE, START_DTTM, EXEC_DEPT_CODE,EXEC_DR_CODE,"
				+ " OPTITEM_CODE, OPTITEM_CHN_DESC, ORDER_CAT1_CODE, DEAL_SYSTEM, RPTTYPE_CODE,"
				+ " DEV_CODE, REMARK, ICD_TYPE, ICD_CODE, STATUS,"
				+ " XML_DATE, NEW_READ_FLG, DC_DR_CODE, DC_ORDER_DATE, DC_DEPT_CODE,"
				+ " DC_READ_FLG,REJECTRSN_CODE, RESERVED_DATE, REGISTER_DATE, INSPECT_DATE,"
				+ " INSPECT_TOTTIME,WAIT_TOTTIME, REPORT_DR, REPORT_DATE, EXAMINE_DR,"
				+ " EXAMINE_DATE,DIAGNOSIS_QUALITY,TECHNOLOGY_QUALITY, SERVICE_QUALITY, SEND_FLG,"
				+ " BILL_FLG,OWN_AMT, AR_AMT, OPT_USER, OPT_DATE,"
				+ " OPT_TERM,ORDER_ENG_DESC,URGENT_FLG,DR_NOTE)" + " VALUES"
				+ "('"
				+ parm.getValue("CAT1_TYPE")
				+ "', '"
				+ parm.getValue("MED_APPLY_NO")
				+ "', '"
				+ parm.getValue("CASE_NO")
				+ "', '"
				+ parm.getValue("IPD_NO")
				+ "', '"
				+ parm.getValue("MR_NO")
				+ "',"
				+ " 'I', '"
				+ this.getAttribute("PAT_NAME")
				+ "', '"
				+ this.getAttribute("PAT_NAME1")
				+ "', TO_DATE('"
				+ StringTool.getString(
						(Timestamp) this.getAttribute("BIRTH_DATE"),
						"yyyyMMddHHmmss")
				+ "','YYYYMMDDHH24MISS'), '"
				+ this.getAttribute("SEX_CODE")
				+ "',"
				+ " '"
				+ this.getAttribute("POST_CODE")
				+ "', '"
				+ this.getAttribute("ADDRESS")
				+ "', '"
				+ this.getAttribute("COMPANY_DESC")
				+ "', '"
				+ this.getAttribute("TEL")
				+ "', '"
				+ this.getAttribute("IDNO")
				+ "',"
				+ " '"
				+ parm.getValue("DEPT_CODE")
				+ "', '"
				+ parm.getValue("REGION_CODE")
				+ "', '', '"
				+ parm.getValue("STATION_CODE")
				+ "', '"
				+ parm.getValue("BED_NO")
				+ "',"
				+ " '"
				+ parm.getValue("ORDER_NO")
				+ "', '"
				+ parm.getValue("ORDER_SEQ")
				+ "', '"
				+ parm.getValue("ORDER_CODE")
				+ "', '"
				+ parm.getValue("ORDER_DESC")
				+ "', '"
				+ parm.getValue("ORDER_DR_CODE")
				+ "',"
				+ " TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("EFF_DATE"),
						"yyyyMMddHHmmss")
				+ "','YYYYMMDDHH24MISS'), '"
				+ parm.getValue("ORDER_DEPT_CODE")
				+ "',TO_DATE('"
				+ startDttm
				+ "','YYYYMMDDHH24MISS'), '"
				+ parm.getValue("EXEC_DEPT_CODE")
				+ "', '"
				+ parm.getValue("EXEC_DR_CODE")
				+ "',"
				+ " '"
				+ parm.getValue("OPTITEM_CODE")
				+ "', '"
				+ optItemDesc
				+ "', '"
				+ parm.getValue("ORDER_CAT1_CODE")
				+ "', '"
				+ dealSystem
				+ "', '"
				+ parm.getValue("RPTTYPE_CODE")
				+ "',"
				+ " '"
				+ parm.getValue("DEV_CODE")
				+ "', '"
				+ parm.getValue("REMARK")
				+ "', '"
				+ icdType
				+ "', '"
				+ this.getAttribute("MAINDIAG")
				+ "', '0',"
				+ " '', 'N', '', '', '',"
				+ " '', '', '', '', '',"
				+ " '', '', '', '', '',"
				+ " '', '', '', '', '0',"
				+ " 'N', '"
				+ StringTool.round(ownAmt, 2)
				+ "', '"
				+ StringTool.round(arAmt, 2)
				+ "', '"
				+ Operator.getID()
				+ "', SYSDATE,"
				+ " '"
				+ Operator.getIP()
				+ "','"
				+ parm.getValue("ORDER_ENG_DESC")
				+ "','"
				+ parm.getValue("URGENT_FLG")
				+ "','"
				+ parm.getValue("DR_NOTE") + "')";
		return sqlStr;
	}

	/**
	 * DC���MED��ĸ���
	 * 
	 * @param parm
	 *            TParm
	 * @return String
	 */
	public String creatLISRISUpdateSQL(TParm parm) {
		String sqlStr = "UPDATE MED_APPLY SET DC_DR_CODE='"
				+ parm.getValue("DC_DR_CODE")
				+ "', DC_ORDER_DATE=SYSDATE, DC_DEPT_CODE='"
				+ parm.getValue("DC_DEPT_CODE") + "',URGENT_FLG='"
				+ parm.getValue("URGENT_FLG") + "',OPT_DATE=SYSDATE,OPT_USER='"
				+ Operator.getID() + "',OPT_TERM='" + Operator.getIP()
				+ "',DR_NOTE='" + parm.getValue("DR_NOTE")
				+ "' WHERE CAT1_TYPE='" + parm.getValue("CAT1_TYPE")
				+ "' AND APPLICATION_NO='" + parm.getValue("MED_APPLY_NO")
				+ "'";
		return sqlStr;
	}

	/**
	 * ������Ҫ�����޸ĵ���������
	 * 
	 * @return String[]
	 */
	public String[] getUpdateSQL() {
		boolean nsCheckFlg = this.getAttributeBoolean(INW_NS_CHECK_FLG);
		String sqlArray[] = this.getDS(this.ODI_ORDERTABLENAME).getUpdateSQL();
		if (sqlArray.length > 0) {
			sqlArray = getDeleteHistoryData(sqlArray);
		}
		sqlArray = StringUtil.getInstance().copyArray(sqlArray,
				this.getDS(this.ODI_ORDER_HISTORYTABLENAME).getUpdateSQL());

		// ��ʿ����Ҫ��˶�����ҽ��վ
		if (!nsCheckFlg && odiFlg) {
			if (this.getDS(this.ODI_DSPNMTABLENAME).getUpdateSQL().length != 0) {
				sqlArray = StringUtil.getInstance().copyArray(sqlArray,
						this.getDS(this.ODI_DSPNMTABLENAME).getUpdateSQL());
			}
			if (this.getDS(this.ODI_DSPNDTABLENAME).getUpdateSQL().length != 0) {
				sqlArray = StringUtil.getInstance().copyArray(sqlArray,
						this.getDS(this.ODI_DSPNDTABLENAME).getUpdateSQL());
			}
		}
		return sqlArray;
	}

	private String[] getDeleteHistoryData(String[] sqlArray) {
		String data[] = new String[sqlArray.length];

		int insertIndex = 0;
		for (int i = 0; i < sqlArray.length; i++) {
			if (sqlArray[i].startsWith("DELETE FROM ODI_ORDER ")) {
				data[insertIndex++] = sqlArray[i].replaceFirst("ODI_ORDER",
						"ODI_ORDER_HISTORY");
			}
		}
		String returnData[] = new String[sqlArray.length + insertIndex];
		for (int i = 0; i < sqlArray.length; i++) {
			returnData[i] = sqlArray[i];
		}
		for (int i = 0, j = 0; i < data.length; i++) {
			if (!StringUtil.isNullString(data[i])) {
				returnData[sqlArray.length + j] = data[i];
				j++;
			}
		}
		return returnData;
	}

	/**
	 * ҽ��վ
	 */
	public void odiUpdateData() {
		boolean nsCheckFlg = this.getAttributeBoolean(INW_NS_CHECK_FLG);
		Timestamp sysdate = TJDODBTool.getInstance().getDBTime();
		TDS ds = this.getDS(ODI_ORDERTABLENAME);
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		// ����������ODI_ORDER
		int newRowOrderId[] = this.getDS(ODI_ORDERTABLENAME).getNewRows(buff);
		// System.out.println("�������������" + newRowOrderId.length);
		// �����������ʱ���ô洢�����õ�ORDER_NO
		if (newRowOrderId.length > 0) {
			// ���ô洢�����õ�ORDER_NO
			String orderNo = SystemTool.getInstance().getNo("ALL", "ODI",
					"ORDER_NO", "ORDER_NO");
			this.setAttribute("ORDER_NO", orderNo);
			this.setAttribute("ORDER_DR_CODE", Operator.getID());
			this.setAttribute("ORDER_DATE", StringTool.getString(TJDODBTool
					.getInstance().getDBTime(), "yyyyMMddHHmmss"));
			TParm parm = new TParm();
			parm.setData("ORDER_NO", orderNo);
			parm.setData("ORDER_DR_CODE", Operator.getID());
			parm.setData("ORDER_DATE",
					StringTool.getString(sysdate, "yyyyMMddHHmmss"));
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_DATE",
					StringTool.getString(sysdate, "yyyyMMddHHmmss"));
			parm.setData("OPT_TERM", Operator.getIP());
			if (!nsCheckFlg && odiFlg) {
				// ����������ֵ
				otherParmSetSave(parm);
			} else {
				for (int i : newRowOrderId) {
					this.setItem(this.getDS(ODI_ORDERTABLENAME), i, parm, buff);

				}
			}
			// ���������
			this.getLabNo();
		}
		// ��ʿ����Ҫ��˲�����ҽ��վ
		if (!nsCheckFlg && odiFlg) {
			this.setSeqNo();
		} else {
			Integer rowId = 0;
			// ��ֵSEQ_NO
			for (int temp : newRowOrderId) {
				ds.setItem(temp, "ORDER_SEQ", rowId += 1, buff);
				// ds.setItem(temp, "FRST_QTY",
				// 0,buff);
				if (!ds.getItemString(temp, "RX_KIND").equals("DS")) {
					if (ds.getItemString(temp, "CAT1_TYPE").equals("PHA")) {
						ds.setItem(temp, "LASTDSPN_QTY", 0, buff);
						ds.setItem(temp, "ACUMDSPN_QTY", 0, buff);
						ds.setItem(temp, "ACUMMEDI_QTY", 0, buff);
						ds.setItem(temp, "DISPENSE_QTY", 0, buff);
						ds.setItem(temp, "DISPENSE_UNIT", "", buff);
					}
				}
				// ds.setItem(temp, "DOSAGE_QTY",
				// 0,buff);
				// ds.setItem(temp, "DOSAGE_UNIT",
				// "",buff);
			}
		}
		onOperateOrderSetDetail();
	}

	/**
	 * ����������ֵ
	 * 
	 * @param parm
	 *            TParm
	 */
	public void otherParmSetSave(TParm parm) {
		TDS dsOrder = this.getDS(ODI_ORDERTABLENAME);
		String buffOrder = dsOrder.isFilter() ? dsOrder.FILTER
				: dsOrder.PRIMARY;
		int newRowOrder[] = dsOrder.getNewRows(buffOrder);
		for (int temp : newRowOrder) {
			if (!dsOrder.isActive(temp, buffOrder))
				continue;
			dsOrder.setItem(temp, "ORDER_NO", parm.getData("ORDER_NO"),
					buffOrder);
			dsOrder.setItem(temp, "ORDER_DR_CODE",
					parm.getData("ORDER_DR_CODE"), buffOrder);
			dsOrder.setItem(temp, "ORDER_DATE", parm.getData("ORDER_DATE"),
					buffOrder);
			dsOrder.setItem(temp, "OPT_USER", parm.getData("OPT_USER"),
					buffOrder);
			dsOrder.setItem(temp, "OPT_DATE", parm.getData("OPT_DATE"),
					buffOrder);
			dsOrder.setItem(temp, "OPT_TERM", parm.getData("OPT_TERM"),
					buffOrder);
		}
		TDS dsDspnm = this.getDS(ODI_DSPNMTABLENAME);
		String buffDspnm = dsDspnm.isFilter() ? dsDspnm.FILTER
				: dsDspnm.PRIMARY;
		int newRowDspnm[] = dsDspnm.getNewRows(buffDspnm);
		for (int temp : newRowDspnm) {
			if (!dsDspnm.isActive(temp, buffDspnm))
				continue;
			dsDspnm.setItem(temp, "ORDER_NO", parm.getData("ORDER_NO"),
					buffDspnm);
			dsDspnm.setItem(temp, "ORDER_DR_CODE",
					parm.getData("ORDER_DR_CODE"), buffDspnm);
			dsDspnm.setItem(temp, "ORDER_DATE", parm.getData("ORDER_DATE"),
					buffDspnm);
			dsDspnm.setItem(temp, "OPT_USER", parm.getData("OPT_USER"),
					buffDspnm);
			dsDspnm.setItem(temp, "OPT_DATE", parm.getData("OPT_DATE"),
					buffDspnm);
			dsDspnm.setItem(temp, "OPT_TERM", parm.getData("OPT_TERM"),
					buffDspnm);
		}
		TDS dsDspnd = this.getDS(ODI_DSPNDTABLENAME);
		String buffDspnd = dsDspnd.isFilter() ? dsDspnd.FILTER
				: dsDspnd.PRIMARY;
		int newRowDspnd[] = dsDspnd.getNewRows(buffDspnd);
		for (int temp : newRowDspnd) {
			if (!dsDspnd.isActive(temp, buffDspnd))
				continue;
			dsDspnd.setItem(temp, "ORDER_NO", parm.getData("ORDER_NO"),
					buffDspnd);
			dsDspnd.setItem(temp, "ORDER_DATE", parm.getData("ORDER_DATE"),
					buffDspnd);
			dsDspnd.setItem(temp, "OPT_USER", parm.getData("OPT_USER"),
					buffDspnd);
			dsDspnd.setItem(temp, "OPT_DATE", parm.getData("OPT_DATE"),
					buffDspnd);
			dsDspnd.setItem(temp, "OPT_TERM", parm.getData("OPT_TERM"),
					buffDspnd);
		}
	}

	/**
	 * ����SEQ_NO����󱣴�ʱ
	 */
	public void setSeqNo() {
		Integer rowIdOrder = 0;
		Integer rowIdDspnm = 0;
		Integer rowIdDspnd = 0;
		TDS dsOrder = this.getDS(ODI_ORDERTABLENAME);
		String buffOrder = dsOrder.isFilter() ? dsOrder.FILTER
				: dsOrder.PRIMARY;
		int newRowOrder[] = dsOrder.getNewRows(buffOrder);
		for (int temp : newRowOrder) {
			if (!dsOrder.isActive(temp, buffOrder))
				continue;
			dsOrder.setItem(temp, "ORDER_SEQ", rowIdOrder += 1, buffOrder);
		}
		TDS dsDspnm = this.getDS(ODI_DSPNMTABLENAME);
		String buffDspnm = dsDspnm.isFilter() ? dsDspnm.FILTER
				: dsDspnm.PRIMARY;
		int newRowDspnm[] = dsDspnm.getNewRows(buffDspnm);
		for (int temp : newRowDspnm) {
			if (!dsDspnm.isActive(temp, buffOrder))
				continue;
			dsDspnm.setItem(temp, "ORDER_SEQ", rowIdDspnm += 1, buffDspnm);
		}
		TDS dsDspnd = this.getDS(ODI_DSPNDTABLENAME);
		String buffDspnd = dsDspnd.isFilter() ? dsDspnd.FILTER
				: dsDspnd.PRIMARY;
		int newRowDspnd[] = dsDspnd.getNewRows(buffDspnd);
		for (int temp : newRowDspnd) {
			if (!dsDspnd.isActive(temp, buffOrder))
				continue;
			dsDspnd.setItem(temp, "ORDER_SEQ", rowIdDspnd += 1, buffDspnd);
		}
	}

	/**
	 * �õ������ @ alias ���������Ŀ(LAB)
	 * 
	 * @param order
	 *            IOrder
	 * @param operator
	 *            IOperator
	 * @param ododitto_one
	 *            IODODitto_One
	 * @return String
	 */
	private boolean getLabNo() {
		boolean falg = true;
		Map labMap = new HashMap();
		Map risNoMap = new HashMap();

		TDS ds = this.getDS(ODI_ORDERTABLENAME);
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		// ����������ODI_ORDER
		int newRowOrderId[] = this.getDS(ODI_ORDERTABLENAME).getNewRows(buff);
		// System.out.println("�������������" + newRowOrderId.length);
		String labNo = "";
		String risNo = "";
		for (int i : newRowOrderId) {
			// System.out.println("��:"+i);
			if (!ds.isActive(i, buff)) {
				// System.out.println("����Ч��:"+i);
				continue;
			}
			// �ӵ�ǰ�������õ���Ӧ������
			TParm parm = ds.getRowParm(i, buff);
			if (!parm.getBoolean("SETMAIN_FLG")) {
				continue;
			}
			if (parm.getValue("CAT1_TYPE").equals("LIS")) {
				String labMapKey = parm.getValue("DEV_CODE")
						+ parm.getValue("OPTITEM_CODE")
						+ parm.getValue("RPTTYPE_CODE");
				// ����о͸���ǰLISҽ����ֵLAB_NO
				if (labMap.get(labMapKey) != null) {
					TParm actionParm = new TParm();
					actionParm.setData("MED_APPLY_NO", labMap.get(labMapKey));
					this.setItem(ds, i, actionParm, buff);
					// ������ҽ��ϸ������
					setOrderSetListLabNo(parm.getValue("ORDERSET_CODE"),
							parm.getValue("ORDERSET_GROUP_NO"),
							labMap.get(labMapKey).toString());
					continue;
				}
				labNo = SystemTool.getInstance().getNo("ALL", "MED", "LABNO",
						"LABNO");
				// �����µ�LAB_NO
				labMap.put(labMapKey, labNo);
				TParm actionLab = new TParm();
				actionLab.setData("MED_APPLY_NO", labNo);
				this.setItem(ds, i, actionLab, buff);
				// ������ҽ��ϸ������
				setOrderSetListLabNo(parm.getValue("ORDERSET_CODE"),
						parm.getValue("ORDERSET_GROUP_NO"), labNo);
			}
			if (parm.getValue("CAT1_TYPE").equals("RIS")) {
				String risMapKey = parm.getValue("ORDERSET_CODE")
						+ parm.getValue("ORDERSET_GROUP_NO");
				// ����о͸���ǰLISҽ����ֵLAB_NO
				if (risNoMap.get(risMapKey) != null) {
					// TParm actionParm = new TParm();
					// actionParm.setData("MED_APPLY_NO",
					// labMap.get(risMapKey));
					// this.setItem(ds,i,actionParm,buff);
					// //������ҽ��ϸ������
					// setOrderSetListLabNo(parm.getValue("ORDERSET_CODE"),parm.getValue("ORDERSET_GROUP_NO"),labMap.get(risMapKey).toString());
					continue;
				}
				risNo = SystemTool.getInstance().getNo("ALL", "MED", "LABNO",
						"LABNO");
				// �����µ�LAB_NO
				risNoMap.put(risMapKey, risNo);
				TParm actionRis = new TParm();
				actionRis.setData("MED_APPLY_NO", risNo);
				this.setItem(ds, i, actionRis, buff);
				// ������ҽ��ϸ������
				setOrderSetListLabNo(parm.getValue("ORDERSET_CODE"),
						parm.getValue("ORDERSET_GROUP_NO"), risNo);
			}
		}
		return falg;
	}

	/**
	 * ������ҽ��ϸ�ֵ
	 * 
	 * @param orderSetCode
	 *            String
	 * @param groupNo
	 *            String
	 */
	public void setOrderSetListLabNo(String orderSetCode, String groupNo,
			String labNo) {
		TDS ds = this.getDS(ODI_ORDERTABLENAME);
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		// ����������ODI_ORDER
		int newRowOrderId[] = this.getDS(ODI_ORDERTABLENAME).getNewRows(buff);
		for (int i : newRowOrderId) {
			if (!ds.isActive(i, buff)) {
				continue;
			}
			// �ӵ�ǰ�������õ���Ӧ������
			TParm parm = ds.getRowParm(i, buff);
			// System.out.println("parm+"+parm);
			String risMapKey = parm.getValue("ORDERSET_CODE")
					+ parm.getValue("ORDERSET_GROUP_NO");
			// ��ͬ�ļ���ҽ����ֵ
			if (risMapKey.equals(orderSetCode + groupNo)) {
				// �����ų�
				if (parm.getBoolean("SETMAIN_FLG"))
					continue;
				TParm actionLab = new TParm();
				actionLab.setData("MED_APPLY_NO", labNo);
				// System.out.println(""+i+"===="+labNo);
				this.setItem(ds, i, actionLab, buff);
			}
		}
	}

	/**
	 * ��������ļ���ҽ��ϸ��
	 * 
	 * @param labMap
	 * @param risMap
	 */
	public void onOperateOrderSetDetail() {
		TDS ds = this.getDS(ODI_ORDERTABLENAME);
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		// ����������ODI_ORDER
		int newRowOrderId[] = this.getDS(ODI_ORDERTABLENAME).getNewRows(buff);
		//����ҽ��list
		List orderSetlist = new ArrayList();
		List arrayListSet = new ArrayList();
		Map setIdMap = new HashMap();
		//ѭ�����ܼ���ҽ������list
		for (int i : newRowOrderId) {
			if (!ds.isActive(i, buff)) {
				continue;
			}
			// �ӵ�ǰ�������õ���Ӧ������
			TParm parm = ds.getRowParm(i, buff);
			//ϸ���ų�
			if (!parm.getBoolean("SETMAIN_FLG")) {
				continue;
			}
//			else if(parm.getValue("ORDERSET_CODE").equals("XEA07009")){
//				int delRowSetId = (Integer) ds.getItemData(i, "#ID#", buff);
//				ds.setAttribute("DELROW", delRowSetId);
//				ds.deleteRow(i);
//				continue;
//			}
			String MapKey = parm.getValue("ORDERSET_CODE")
					+ parm.getValue("ORDERSET_GROUP_NO");
			orderSetlist.add(MapKey);
		}
		//ѭ����������û�������ϸ��list
		for (int i : newRowOrderId) {
			if (!ds.isActive(i, buff)) {
				continue;
			}
			// �ӵ�ǰ�������õ���Ӧ������
			TParm parm = ds.getRowParm(i, buff);
			// ��ʾ��Ŀ�ų�
			if (!parm.getBoolean("HIDE_FLG"))
				continue;
			String MapKey = parm.getValue("ORDERSET_CODE")
					+ parm.getValue("ORDERSET_GROUP_NO");
			// �ж��Ƿ���ڴ�ҽ������
			if (!orderSetlist.contains(MapKey)) {
				int delRowSetId = (Integer) ds.getItemData(i, "#ID#", buff);
				arrayListSet.add(delRowSetId);
				setIdMap.put(delRowSetId, i);
			}
		}		
		// ɾ��ϸ��
		for (int i = arrayListSet.size() - 1; i >= 0; i--) {
			ds.setAttribute("DELROW", arrayListSet.get(i));
			deleteRow(ds, (Integer) setIdMap.get(arrayListSet.get(i)));
		}
	}

	/**
	 * ������ֵ
	 * 
	 * @param ds
	 *            TDS
	 * @param row
	 *            int
	 * @param column
	 *            String
	 * @param value
	 *            Object
	 * @return boolean
	 */
	public boolean setItem(TDS ds, int row, Object value) {
		// �����ҽ��վ
		if (odiFlg) {
			// ����ҽ��վֵ
			setOdiStationItem(ds, row, value);
		}
		return true;
	}

	/**
	 * ������ֵ(����������)
	 * 
	 * @param ds
	 *            TDS
	 * @param row
	 *            int
	 * @param column
	 *            String
	 * @param value
	 *            Object
	 * @return boolean
	 */
	public boolean setItem(TDS ds, int row, Object value, String buff) {
		// �����ҽ��վ
		if (odiFlg) {
			// ����ҽ��վֵ
			setOdiStationItem(ds, row, value, buff);
		}
		return true;
	}

	/**
	 * ������
	 * 
	 * @param odiOrderDs
	 *            TDS
	 * @param row
	 *            int
	 * @return int
	 */
	public TParm insertRow(TDS odiOrderDs, TParm order, String buff) {
		TParm result = new TParm();
		// �����ҽ��վ
		if (odiFlg) {
			// ����ҽ��վҽ����
			result = this.setOdiStationInserRow(odiOrderDs, order, buff);
		}
		return result;
	}

	/**
	 * ����ҽ��վ��
	 * 
	 * @param ds
	 *            TDS
	 * @param row
	 *            int
	 */
	public TParm setOdiStationInserRow(TDS ds, TParm order, String buff) {
		TParm result = new TParm();
		boolean nsCheckFlg = this.getAttributeBoolean(INW_NS_CHECK_FLG);
		String columnArr[] = ds.getColumns();
		// ��ʿ���ΪN������ҽ��վ
		if (!nsCheckFlg && odiFlg) {
			// �۲�������
			int newRow = ds.insertRow();
			this.getDS(ODI_ORDERTABLENAME).setItem(newRow, "RX_KIND",
					this.getAttribute("RX_KIND"));
			this.setItem(ds, newRow, order, buff);
			// �����пɲ���
			ds.setActive(newRow, true);
			// ��ʿ���ΪY������ҽ��վ
		} else {
			int newRow = ds.insertRow();
			for (String temp : columnArr) {
				if (order.getData(temp) == null) {
					continue;
				}
				// ������ͬ�е�ֵ
				ds.setItem(newRow, temp, order.getData(temp));
			}
			// �����пɲ���
			ds.setActive(newRow, true);
		}
		return result;
	}

	/**
	 * ɾ����
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean deleteRow(TDS ds, int row) {
		if (odiFlg) {
			TParm parm = ds.getRowParm(row);
			insertOrderHisTory(parm);
			ds.deleteRow(row);
		}
		return true;
	}

	/**
	 * ������ʷ��
	 * 
	 * @param parm
	 *            TParm
	 */
	public void insertOrderHisTory(TParm parm) {
		if (parm.getValue("ORDER_NO") == null
				|| parm.getValue("ORDER_NO").length() == 0
				|| parm.getValue("ORDER_NO").equals("999999999999"))
			return;
		// System.out.println("parm=="+parm);
		TDS ds = this.getDS(ODI_ORDER_HISTORYTABLENAME);
		String columnArr[] = ds.getColumns();
		int newRow = ds.insertRow();
		for (String temp : columnArr) {
			// System.out.println(""+parm.getData(temp));
			if (parm.getData(temp) == null) {
				continue;
			}
			// ������ͬ�е�ֵ
			ds.setItem(newRow, temp, parm.getData(temp));
		}
		ds.setItem(newRow, "DC_DATE", StringTool.getString(TJDODBTool
				.getInstance().getDBTime(), "yyyyMMddHHmmss"));
		// �����пɲ���
		ds.setActive(newRow, true);
	}

	/**
	 * ɾ����
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean deleteRow(TDS ds, int row, String buff) {
		if (odiFlg) {
			ds.deleteRow(row);
		}
		return true;
	}

	/**
	 * ����ҽ��վֵ
	 * 
	 * @param ds
	 *            TDS
	 * @param row
	 *            int
	 * @param columnStrArr
	 *            String
	 * @param value
	 *            Object
	 */
	private void setOdiStationItem(TDS ds, int row, Object value) {
		boolean nsCheckFlg = this.getAttributeBoolean(INW_NS_CHECK_FLG);
		String columnArr[] = ds.getColumns();
		// ��ʿ���ΪN������ҽ��վ
		if (!nsCheckFlg && odiFlg) {
			if (value instanceof TParm) {
				TParm parm = (TParm) value;
				// ������ͬ�е�ֵ
				ds.setAttribute("CHANGE_FLG", true);
				ds.setItem(row, "", parm);
			}
			// ��ʿ���ΪY������ҽ��վ
		} else {
			if (value instanceof TParm) {
				TParm parm = (TParm) value;
				for (String temp : columnArr) {
					if (parm.getData(temp) == null) {
						continue;
					}
					// ������ͬ�е�ֵ
					Object obj = parm.getData("RX_FLG");
					if (obj != null) {
						if ("Y".equals(obj)) {
							if ("RX_KIND".equals(temp)) {
								continue;
							}
						}
					}
					// System.out.println("temp="+temp+"parm.getData(temp)=="+parm.getData(temp));
					ds.setItem(row, temp, parm.getData(temp));
				}
			}
		}
	}

	/**
	 * ����ҽ��վֵ(����������)
	 * 
	 * @param ds
	 *            TDS
	 * @param row
	 *            int
	 * @param columnStrArr
	 *            String
	 * @param value
	 *            Object
	 */
	private void setOdiStationItem(TDS ds, int row, Object value, String buff) {
		boolean nsCheckFlg = this.getAttributeBoolean(INW_NS_CHECK_FLG);
		String columnArr[] = ds.getColumns();
		// ��ʿ���ΪN������ҽ��վ
		if (!nsCheckFlg && odiFlg) {
			if (value instanceof TParm) {
				TParm parm = (TParm) value;
				// ������ͬ�е�ֵ
				ds.setAttribute("CHANGE_FLG", true);
				ds.setItem(row, "", parm);
			}
			// ��ʿ���ΪY������ҽ��վ
		} else {
			if (value instanceof TParm) {
				TParm parm = (TParm) value;
				for (String temp : columnArr) {
					if (parm.getData(temp) == null) {
						continue;
					}
					// ������ͬ�е�ֵ
					Object obj = parm.getData("RX_FLG");
					if (obj != null) {
						if ("Y".equals(obj)) {
							if ("RX_KIND".equals(temp)) {
								continue;
							}
						}
					}
					// System.out.println("row:"+row+"temp:"+temp+":"+parm.getData(temp));
					ds.setItem(row, temp, parm.getData(temp), buff);
				}
			}
		}
	}

	/**
	 * �õ�סԺ����
	 * 
	 * @param key
	 *            String
	 * @return Object
	 */
	public Object getOdiSysParmData(String key) {
		return OdiMainTool.getInstance().getOdiSysParmData(key);
	}

	/**
	 * �õ��������
	 * 
	 * @param key
	 *            String
	 * @return Object
	 */
	public Object getOpdSysParmData(String key) {
		return OdiMainTool.getInstance().getOpdSysParmData(key);
	}

	/**
	 * �õ�ҽ��վע��
	 * 
	 * @return boolean
	 */
	public boolean isOdiFlg() {
		return odiFlg;
	}

	/**
	 * ����ҽ��վע��
	 * 
	 * @param odiFlg
	 *            boolean
	 */
	public void setOdiFlg(boolean odiFlg) {
		this.odiFlg = odiFlg;
	}

	/**
	 * �����ҩ��Ƭҽ��
	 * 
	 * @param tab
	 *            TTable
	 * @param rxKind
	 *            String
	 * @return int
	 */
	public int addChnRow(TDataStore tab, String rxKind) {
		return 0;
	}

	/**
	 * ����
	 * 
	 * @return boolean
	 */
	public boolean filter(TTable table, TDS ds, boolean stopFalg) {
		boolean falg = true;
		if (!ds.filter())
			falg = false;
		int rowPrimaryCount = ds.rowCount();
		// System.out.println("��������"+rowPrimaryCount);
		// ��ɾ��δʹ����
		for (int i = rowPrimaryCount - 1; i >= 0; i--) {
			if (!ds.isActive(i)) {
				// System.out.println("i====="+i);
				ds.deleteRow(i);
			}
		}
		// ֹͣ����ע��
		if (stopFalg) {
			falg = false;
			return falg;
		}
		TParm parm = ds.getBuffer(ds.PRIMARY);
		// System.out.println("����������:"+parm);
		// table.setParmValue(this.getIGTableParm(parm));
		TParm action = this.getIGTableParm(parm, ds);
		// System.out.println("TABLE��:"+action);
		table.setParmValue(action);
		return falg;
	}

	/**
	 * �õ�סԺ��ҽҽ��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getIGTableParm(TParm parm, TDS ds) {
		TParm result = new TParm();
		// System.out.println("�õ�סԺ��ҽҽ��"+parm);
		int rowCount = parm.getCount() == -1 ? 0 : parm.getCount();
		// System.out.println("����:"+rowCount);
		// ORDER_DESC_1;MEDI_QTY_1;DCTAGENT_CODE_1;ORDER_DESC_2;MEDI_QTY_2;DCTAGENT_CODE_2;ORDER_DESC_3;MEDI_QTY_3;DCTAGENT_CODE_3;ORDER_DESC_4;MEDI_QTY_4;DCTAGENT_CODE_4
		int row = rowCount % 4 == 0 ? rowCount / 4 : Math
				.abs((rowCount + 4) / 4);
		// System.out.println("TABLE����:"+row);
		int k = 0;
		for (int i = 0; i < row; i++) {
			for (int j = 1; j < 5; j++) {
				if (k < rowCount) {
					result.addData("ORDER_DESC_" + j,
							parm.getData("ORDER_DESC", k));
					result.addData("MEDI_QTY_" + j, parm.getData("MEDI_QTY", k));
					result.addData("DCTEXCEP_CODE_" + j,
							parm.getData("DCTEXCEP_CODE", k));
					result.addData("ROW_ID_" + j, parm.getData("#ID#", k));
				} else {
					int insertRow = ds.insertRow();
					ds.setItem(insertRow, "RX_KIND", "IG");
					ds.setActive(insertRow, false);
					result.addData("ORDER_DESC_" + j, "");
					result.addData("MEDI_QTY_" + j, 0);
					result.addData("DCTEXCEP_CODE_" + j, "");
					result.addData("ROW_ID_" + j,
							ds.getItemData(insertRow, "#ID#"));
				}
				k++;
			}
		}
		// ��������
		int addRowCount = rowCount % 4;
		// �ж���Ҫ��ӵ�����
		if (addRowCount == 0) {
			for (int i = 1; i < 5; i++) {
				int insertRow = ds.insertRow();
				ds.setItem(insertRow, "RX_KIND", "IG");
				ds.setActive(insertRow, false);
				result.addData("ORDER_DESC_" + i, "");
				result.addData("MEDI_QTY_" + i, 0);
				result.addData("DCTEXCEP_CODE_" + i, "");
				result.addData("ROW_ID_" + i, ds.getItemData(insertRow, "#ID#"));
			}
			row += 1;
		}
		result.setData("ACTION", "COUNT", row);
		return result;
	}
}
