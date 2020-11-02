package jdo.inw;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import jdo.ibs.IBSTool;
import jdo.opd.TotQtyTool;
import com.dongyang.jdo.TJDODBTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.data.TNull;
import java.util.Map;
import com.dongyang.util.TypeTool;
import jdo.bil.BIL;
import jdo.adm.ADMInpTool;
import jdo.sys.SystemTool;

/**
 * <p>
 * Title: סԺ��ʿվ�����Tool
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
public class InwOrderCheckTool extends TJDOTool {

	/**
	 * ʵ��
	 */
	private static InwOrderCheckTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PatTool
	 */
	public static InwOrderCheckTool getInstance() {
		if (instanceObject == null)
			instanceObject = new InwOrderCheckTool();
		return instanceObject;
	}

	public InwOrderCheckTool() {
	}

	/**
	 * ����ȡҩ����,ȡҩ�������� ͳҩ���Ź�11λ,��ŷ�ʽΪ��Ԫ��2λ+��2λ+��2λ+��ˮ��5λ,
	 * 
	 * @return String
	 */
	static public String getTakeMedNo() {
		return SystemTool.getInstance().getNo("ALL", "UDD", "TAKEMED_NO",
				"TAKEMED_NO");
	}

	/**
	 * ȡ�����
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUndoCheck(TParm parm, TConnection connection) {
		TParm result = new TParm();
		Timestamp now = SystemTool.getInstance().getDate();
		// ǰ̨��������
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			// ȡ������
			TParm undoData = parm.getRow(i);
			undoData.setData("DC_NS_CHECK_DATE", now);
			undoData.setData("NS_CHECK_DATE", new TNull(Timestamp.class));
			undoData.setData("NS_CHECK_CODE", new TNull(String.class));
			result = undoData(undoData, connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * ���������ύ
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	private TParm undoData(TParm parm, TConnection connection) {
		TParm undoData = new TParm();
		TParm result = new TParm();
		undoData.setData("CASE_NO", parm.getData("CASE_NO"));
		undoData.setData("ORDER_NO", parm.getData("ORDER_NO"));
		undoData.setData("ORDER_SEQ", parm.getData("ORDER_SEQ"));
		undoData.setData("OPT_USER", parm.getData("OPT_USER"));
		undoData.setData("OPT_TERM", parm.getData("OPT_TERM"));
		undoData.setData("OPT_DATE", parm.getData("OPT_DATE"));
		undoData.setData("DC_NS_CHECK_DATE", parm.getData("DC_NS_CHECK_DATE"));
		result = InwForOdiTool.getInstance().delOdiDspndForUndoCk(undoData,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = InwForOdiTool.getInstance().delOdiDspnmForUndoCk(undoData,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// ȡ����˵�ʱ��ֻ�����˱��
		undoData.setData("NS_CHECK_DATE", parm.getData("NS_CHECK_DATE"));
		undoData.setData("NS_CHECK_CODE", parm.getData("NS_CHECK_CODE"));
		result = InwForOdiTool.getInstance().updateOdiOrderForUndoCk(undoData,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����TakeMedOrg
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	private TParm upDateTakeMedOrg(TParm parm, TConnection connection) {
		TParm doData = new TParm();
		TParm result = new TParm();
		doData.setData("CASE_NO", parm.getData("CASE_NO"));
		doData.setData("ORDER_NO", parm.getData("ORDER_NO"));
		doData.setData("ORDER_SEQ", parm.getData("ORDER_SEQ"));
		doData.setData("OPT_USER", parm.getData("OPT_USER"));
		doData.setData("OPT_TERM", parm.getData("OPT_TERM"));
		doData.setData("OPT_DATE", parm.getData("OPT_DATE"));
		doData.setData("DC_NS_CHECK_DATE", parm.getData("DC_NS_CHECK_DATE"));
		doData.setData("TAKEMED_ORG", parm.getData("TAKEMED_ORG"));
		doData.setData("TAKEMED_NO", parm.getData("TAKEMED_NO"));
		doData.setData("START_DTTM", parm.getData("START_DTTM"));
		doData.setData("END_DTTM", parm.getData("END_DTTM"));
		result = InwForOdiTool.getInstance().updateOdiOrdertakeMedOrg(doData,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = InwForOdiTool.getInstance().updateOdiDspnmtakeMedOrg(doData,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = InwForOdiTool.getInstance().updateOdiDspnDtakeMedOrg(doData,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm CASE_NO OPT_USER OPT_DATE OPT_TERM
	 */
	public TParm onCheck(TParm parm, TConnection connection) {
		TParm result = new TParm();
		// ǰ̨��������
		TParm dataParm = parm.getParm("dataParm");
		String optUser = (String) parm.getData("OPT_USER");
		Timestamp optDate = (Timestamp) parm.getData("OPT_DATE");
		String optTerm = (String) parm.getData("OPT_TERM");
		String takeMedOrg = "";
		// shibl 20121022 modify ����caseNo����ʿ��˵�ҽ�����վ���ŷ���
		Map pat = InwOrderExecTool.getInstance().groupByPatParm(dataParm);
		Iterator it = pat.values().iterator();
		while (it.hasNext()) {
			// ȡҩ��
			String takeMedNo = "";
			Map takeMedNoMap = new HashMap();
			TParm patParm = (TParm) it.next();
			for (int i = 0; i < patParm.getCount(); i++) {
				// ֧�ֶ������
				// ѭ��1--ҽ����ѭ����Ҫ��˵����� PS:��Ϊ����ҽ����ʱ�򷵻���ϸ��
				// ����ǰ̨��CASE_NO,ORDER_NO,ORDER_SEQ�ҳ�һ��ҽ��������SETMAIN_FLG�����Ƿ�Ϊ����ҽ��
				TParm orderPram = getAnOrder(
						patParm.getData("CASE_NO", i) + "", patParm.getData(
								"ORDER_NO", i)
								+ "", patParm.getData("ORDER_SEQ", i) + "",
						patParm.getData("SETMAIN_FLG", i) + "", patParm
								.getData("ORDERSETGROUP_NO", i)
								+ "");
				// ȡҩע��
				takeMedOrg = patParm.getValue("TAKEMED_ORG", i);
				// ��¼��ǰUI��ʾ�����ʱ��--���ڻ��ֶ��޸ĸ�ʱ��
				Timestamp checkDate = patParm.getTimestamp("CHECK_DATETIME", i);
				// ѭ��ÿ��������ÿһ�����ڴ������ݣ�����UDD�ӿ�������Ҫչ�������ݲ���ODI_DSPND ODI_DSPNM
				int countToM = orderPram.getCount();
				// System.out.println("==��Ҫ����M�����������===" + countToM);
				// �ò���û�г��ڴ���
				if (countToM <= 0)
					continue;
				TParm parmCase = new TParm();
				parmCase.setData("CASE_NO", patParm.getData("CASE_NO") + "");
				parmCase = ADMInpTool.getInstance().selectall(parmCase);
				String serviceLevel = parmCase.getValue("SERVICE_LEVEL", 0);
				// ѭ��2--����ҽ��/��ͨҽ����(��M��1:1)��ѭ������ÿһ��ҽ����UDD�ӿڣ�����Ӧ��չ��������
				for (int row_M = 0; row_M < countToM; row_M++) {
					// ���ݿ��е�һ������--orderPram
					String caseNo = orderPram.getData("CASE_NO", row_M) + "";
					// System.out.println("=ҽ��==ĳ�����˵�ҽ��====>"+caseNo);
					String orderNo = orderPram.getData("ORDER_NO", row_M) + "";
					String orderSeq = orderPram.getData("ORDER_SEQ", row_M)
							+ "";
					// ��¼��ҽ����RX_KIND��UD-���ڣ�ST-��ʱ������UDD�ӿڴ���󷵻أ�/��DS-��Ժ��ҩ��ֱ��1��1��1չ����
					String orderRxKind = orderPram.getData("RX_KIND", row_M)
							+ "";
					// PS����֧����(1)UD/ST (2)DS (3)IG
					if ("UD".equals(orderRxKind) || "ST".equals(orderRxKind)) {
						// orderToUddParm��¼��UDD�ӿڵ�����Ҫ�Ĳ���
						TParm orderToUddParm = new TParm();
						orderToUddParm = orderPram.getRow(row_M); // һ��ODI_ORDER������
						// System.out.println("==orderToUddParm111111=="
						// + orderToUddParm);

						// ����UDD�Ľӿڵõ���Ӧ�Ĳ���:�����ʱ��û���룬������ʱ������
						String effDate = StringTool.getString(
								(Timestamp) orderToUddParm.getData("EFF_DATE"),
								"yyyyMMddHHmm");
						/******************* SHIBL ADD ******************************/
						// ����ʱ�����UDD�ӿڵõ���һ����ҩʱ��ʹε�
						List effdispenseDttm = TotQtyTool.getInstance()
								.getNextDispenseDttm(
										(Timestamp) orderToUddParm
												.getData("EFF_DATE"));
						// ͣ��ʱ�����UDD�ӿڵõ���һ����ҩʱ��ʹε�
						Timestamp Dcdate = (Timestamp) orderToUddParm
								.getData("DC_DATE");
						List DCdispenseDttm = null;
						if (orderToUddParm.getData("DC_DATE") != null) {
							DCdispenseDttm = TotQtyTool.getInstance()
									.getNextDispenseDttm(Dcdate);
						}
						/******************* SHIBL ADD ******************************/
						Timestamp sysDate = TJDODBTool.getInstance()
								.getDBTime();
						// ����UDD�ӿڵõ���һ����ҩʱ��ʹε�
						List dispenseDttm = TotQtyTool.getInstance()
								.getNextDispenseDttm(checkDate);
						// ���û����һ��ʱ���ͼ���ѭ��
						if (dispenseDttm.size() == 0) {
							System.out.println(" WARNING��There is not the 'next DispenseDttm' here ");
							continue;
						}
						// ODI_ORDER���е�LAST_DSPN_DATE
						Timestamp lastDspnDateForOrder;
						// �ж���ҩ�����Ǵ���-----false
						String cat1Type = (String) orderToUddParm
								.getData("CAT1_TYPE");
						// �Ƿ�Ϊ�����ֻ�ҩ�����λ
						boolean isOperation = "TRT".equalsIgnoreCase(cat1Type)
								|| "PLN".equalsIgnoreCase(cat1Type)
								|| "OTH".equalsIgnoreCase(cat1Type);
						// dataFromUdd�������մ�UDD��������������Ҫչ����
						List dataFromUdd;
						String startDttmInDspnm = ""; // ����ODI_DSPNM�е�START_DTTM
						String endDttmInDspnm = ""; // ����ODI_DSPNM�е�END_DTTM
						// ����ֵ��������[(1)list--��size����D��ò����������(2)Map--����Ҫ������]--PS:����Ǵ��ò���3:effDate��ǰ8λ+0000
						// ����4:effDate��ǰ8λ+235959
						if (!isOperation) { // ҩƷ
							// ��ǰ����ʱ��
							// startDttmInDspnm = StringTool.getString(optDate,
							// "yyyyMMddHHmm");
							// ȡҩ����ȡ��ԭ�� shibl 20121121 add
							if ((orderRxKind.equals("UD") || orderRxKind
									.equals("ST"))
									&& takeMedOrg.equals("1")) {
								if (takeMedNo.equals("")) {
									takeMedNo = this.getTakeMedNo();
									takeMedNoMap.put(caseNo + orderNo
											+ orderSeq, takeMedNo);
								} else {
									takeMedNoMap.put(caseNo + orderNo
											+ orderSeq, takeMedNo);
								}
							}
							/******************* SHIBL MODIFY ******************************/
							if ("UD".equals(orderRxKind)) {
								String str1 = effdispenseDttm.get(0).toString();
								String Str2 = dispenseDttm.get(0).toString();
								String optStr = StringTool.getString(optDate,
										"yyyyMMddHHmm");
								String dcStr = StringTool.getString(
										(Timestamp) orderToUddParm
												.getData("DC_DATE"),
										"yyyyMMddHHmm");
								// δ��˵�ҽ��
								if (orderToUddParm.getValue("NS_CHECK_CODE")
										.equals("")) {
									dataFromUdd = TotQtyTool
											.getInstance()
											.getOdiStQty(
													effDate, // EFF_DATE
													StringTool
															.getString(
																	(Timestamp) orderToUddParm
																			.getData("DC_DATE"),
																	"yyyyMMddHHmm"), // DC_DATE
													dispenseDttm.get(0)
															.toString(), // ��һ�ΰ�ҩʱ��START_DTTM
													dispenseDttm.get(1)
															.toString(), // ��һ�ΰ�ҩʱ��END_DTTM
													orderToUddParm,
													serviceLevel); // ��������
									// �ײ�ʱ��
									startDttmInDspnm = effDate;
									// ����ڰ�ҩ֮��
									if (str1.compareTo(Str2) < 0) {
										// ��һ�ΰ�ҩʱ��
										lastDspnDateForOrder = StringTool
												.getTimestamp(str1,
														"yyyyMMddHHmm");
									} else {
										lastDspnDateForOrder = optDate;
									}
								}
								// ���DCҽ��
								else {
									dataFromUdd = TotQtyTool
											.getInstance()
											.getOdiStQty(
													effDate, // EFF_DATE
													StringTool
															.getString(
																	(Timestamp) orderToUddParm
																			.getData("DC_DATE"),
																	"yyyyMMddHHmm"), // DC_DATE
													DCdispenseDttm.get(0)
															.toString(), // ��һ�ΰ�ҩʱ��START_DTTM
													DCdispenseDttm.get(1)
															.toString(), // ��һ�ΰ�ҩʱ��END_DTTM
													orderToUddParm,
													serviceLevel); // ��������
									startDttmInDspnm = effDate;
									// DCʱ��С������ʱ�����һ�ΰ�ҩʱ��
									// if (dcStr.compareTo(str1) < 0) {
									// startDttmInDspnm = effDate;
									// } else {
									// // ��ҩ�ײ�ʱ��ȡ��һ�ΰ�ҩʱ��
									// startDttmInDspnm = StringTool.getString(
									// (Timestamp) orderToUddParm
									// .getData("LAST_DSPN_DATE"),
									// "yyyyMMddHHmm");
									// }
									lastDspnDateForOrder = optDate;
								}
								/******************* SHIBL MODIFY ******************************/
								// ��һ��ҩʱ��
								endDttmInDspnm = dispenseDttm.get(0).toString();
							} else {
								dataFromUdd = TotQtyTool
										.getInstance()
										.getOdiStQty(
												effDate, // EFF_DATE
												StringTool
														.getString(
																(Timestamp) orderToUddParm
																		.getData("DC_DATE"),
																"yyyyMMddHHmm"), // DC_DATE
												dispenseDttm.get(0).toString(), // ��һ�ΰ�ҩʱ��START_DTTM
												dispenseDttm.get(1).toString(), // ��һ�ΰ�ҩʱ��END_DTTM
												orderToUddParm, serviceLevel); // ��������
								// �ײ�ʱ��
								startDttmInDspnm = effDate;
								// ��һ��ҩʱ��
								endDttmInDspnm = effDate;
								lastDspnDateForOrder = optDate;
							}
						} else { // ����
							// ���õ����µ�UDD�ӿڷ���
							// 20120511 shibl modify �����ʱ��֧
							if ("UD".equals(orderRxKind)) {
								dataFromUdd = TotQtyTool.getInstance()
										.getOdiTrtStQty(
												orderToUddParm,
												StringTool.getString(optDate,
														"yyyyMMddHHmm"),
												serviceLevel);
								// System.out.println("����-==----dataFromUdd--------------"
								// + dataFromUdd);
								if (dataFromUdd == null
										|| dataFromUdd.get(2) == null) {
									result.setErrCode(-1);
									err("ERR:" + result.getErrCode()
											+ "�������ҽ����Ƶ������");
									return result;
								}
								if (((List) dataFromUdd.get(2)).size() > 0)
									startDttmInDspnm = ((List) dataFromUdd
											.get(2)).get(0).toString();
								// �����̣���DC��������ʱ�䡪��start end
								if (((List) dataFromUdd.get(2)).size() > 1) {
									endDttmInDspnm = ((List) dataFromUdd.get(2))
											.get(1).toString();
									// ��һ�ΰ�ҩʱ��
									lastDspnDateForOrder = (Timestamp) ((Map) dataFromUdd
											.get(1))
											.get("ORDER_LAST_DSPN_DATE");
								} else {
									lastDspnDateForOrder = new Timestamp(0L);
								}
							} else {
								dataFromUdd = TotQtyTool
										.getInstance()
										.getOdiTrtStQty(
												orderToUddParm,
												StringTool
														.getString(
																(Timestamp) orderToUddParm
																		.getData("EFF_DATE"),
																"yyyyMMddHHmm"),
												serviceLevel);
								// �ײ�ʱ��
								startDttmInDspnm = effDate;
								// ��һ��ҩʱ��
								endDttmInDspnm = effDate;
								lastDspnDateForOrder = optDate;
							}
						}
						// Ӧ�ò���ODI_DSPND��������UDD���ص�list��SIZE
						List orderDateTimeFromUdd = (List) dataFromUdd.get(0);
						int insertDCount = orderDateTimeFromUdd.size(); // ��˵�ʱ���ǲ���D��������������DC��ʱ���Ǹ���D�������
						// System.out.println("insertDCount = " + insertDCount);
						// ��û����������ʱ�򣬼�D��չ��������Ϊ0�Ͳ���M/D��
						if (insertDCount != 0) {
							// PS:�����ʱ����M/D��������
							// �õ���Ҫ�嵽���е����ݣ�(PS:order���е����ݶ���ODI�в���)
							// ORDER���LASTDSPN_QTY ORDER_LASTDSPN_QTY
							// ORDER���ACUMDSPN_QTY ORDER_ACUMDSPN_QTY
							// ORDER���ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
							// M/ORDER���dispenseQty M_DISPENSE_QTY
							// M/ORDER���dispenseUnit M_DISPENSE_UNIT
							// M/ORDER���dosageQty M_DOSAGE_QTY
							// M/ORDER���dosageUnit M_DOSAGE_UNIT
							// M���OWN_PRICE OWN_PRICE
							// M���NHI_PRICE NHI_PRICE
							// M���OWN_AMT OWN_AMT
							// M���TOT_AMT TOT_AMTM
							// M���DISCOUNT_RATE DISCOUNT_RATE
							// D���TOT_AMT TOT_AMTD
							// D���MediQty D_MEDI_QTY
							// D���MediUnit D_MEDI_UNIT
							// D���dosageQty D_DOSAGE_QTY
							// D���dosageUnit D_DOSAGE_UNIT
							// �õ����ص�UDD���ص�ֵ
							Map otherData = (Map) dataFromUdd.get(1);
							double OlastDspnQty = (Double) otherData
									.get("ORDER_LASTDSPN_QTY") == null ? 0.0
									: (Double) otherData
											.get("ORDER_LASTDSPN_QTY");
							double OacumDspnQty = (Double) otherData
									.get("ORDER_ACUMDSPN_QTY") == null ? 0.0
									: (Double) otherData
											.get("ORDER_ACUMDSPN_QTY");
							double OacumMediQty = (Double) otherData
									.get("ORDER_ACUMMEDI_QTY") == null ? 0.0
									: (Double) otherData
											.get("ORDER_ACUMMEDI_QTY");
							double MdispenseQty = (Double) otherData
									.get("M_DISPENSE_QTY") == null ? 0.0
									: (Double) otherData.get("M_DISPENSE_QTY");
							String MdispenseUnit = (String) otherData
									.get("M_DISPENSE_UNIT");
							double MdosageQty = (Double) otherData
									.get("M_DOSAGE_QTY") == null ? 0.0
									: (Double) otherData.get("M_DOSAGE_QTY");
							String MdosageUnit = (String) otherData
									.get("M_DOSAGE_UNIT");
							double ownPrice = (Double) otherData
									.get("OWN_PRICE") == null ? 0.0
									: (Double) otherData.get("OWN_PRICE");
							double nhiPrice = (Double) otherData
									.get("NHI_PRICE") == null ? 0.0
									: (Double) otherData.get("NHI_PRICE");
							double ownAmt = (Double) otherData.get("OWN_AMT") == null ? 0.0
									: (Double) otherData.get("OWN_AMT");
							double totAmtM = (Double) otherData.get("TOT_AMTM") == null ? 0.0
									: (Double) otherData.get("TOT_AMTM");
							double totAmtD = (Double) otherData.get("TOT_AMTD") == null ? 0.0
									: (Double) otherData.get("TOT_AMTD");
							double discountRate = (Double) otherData
									.get("DISCOUNT_RATE") == null ? 0.0
									: (Double) otherData.get("DISCOUNT_RATE");
							double DmediQty = (Double) otherData
									.get("D_MEDI_QTY") == null ? 0.0
									: (Double) otherData.get("D_MEDI_QTY");
							String DmediUnit = (String) otherData
									.get("D_MEDI_UNIT");
							double DdosageQty = (Double) otherData
									.get("D_DOSAGE_QTY") == null ? 0.0
									: (Double) otherData.get("D_DOSAGE_QTY");
							String DdosageUnit = (String) otherData
									.get("D_DOSAGE_UNIT");
							// �Ƿ���������ҽ��(��DC) SHIBL MODIFY 20120424
							if (orderToUddParm.getValue("NS_CHECK_CODE")
									.equals("")) {
								// �ж��Ƿ����ȡ����˼�¼
								if (checkIsNSDCExistDspnm(caseNo, orderNo,
										orderSeq)) {
									// ��˶���
									TParm doData = new TParm();
									doData.setData("CASE_NO", caseNo);
									doData.setData("ORDER_NO", orderNo);
									doData.setData("ORDER_SEQ", orderSeq);
									doData.setData("OPT_USER", optUser);
									doData.setData("OPT_TERM", optTerm);
									doData.setData("OPT_DATE", optDate);
									doData.setData("DC_NS_CHECK_DATE",
											new TNull(Timestamp.class));
									doData.setData("NS_CHECK_DATE", optDate);
									doData.setData("NS_CHECK_CODE", optUser);
									result = undoData(doData, connection);
									if (result.getErrCode() < 0) {
										err("ERR:" + result.getErrCode()
												+ result.getErrText()
												+ result.getErrName());
										return result;
									}
									doData.setData("TAKEMED_ORG", takeMedOrg);
									String MedNo = takeMedNoMap.get(caseNo
											+ orderNo + orderSeq) == null ? ""
											: (String) takeMedNoMap.get(caseNo
													+ orderNo + orderSeq);
									doData.setData("TAKEMED_NO", MedNo);
									doData.setData("START_DTTM",
											startDttmInDspnm);
									doData.setData("END_DTTM", endDttmInDspnm);
									if (("UD".equals(orderRxKind) || "ST"
											.equals(orderRxKind))
											&& cat1Type.equals("PHA")) {
										result = upDateTakeMedOrg(doData,
												connection);
										if (result.getErrCode() < 0) {
											err("ERR:" + result.getErrCode()
													+ result.getErrText()
													+ result.getErrName());
											return result;
										}
									}
								}
								// �ж��Ƿ���ڼ�¼
								else if (!this.checkIsExistDspnm(caseNo,
										orderNo, orderSeq, startDttmInDspnm)) {
									String Mdcflg = "N";
									// ѭ��3--����ϸ�(����D���ÿһ������)
									for (int row_D = 0; row_D < insertDCount; row_D++) {
										// �����Ҫ����ϸ�������
										TParm finalDataToD = new TParm();
										String orderDateTime = (String) orderDateTimeFromUdd
												.get(row_D);
										if(!orderToUddParm.getValue("DC_DATE").equals("")&&StringTool
												.getTimestamp(orderDateTime,"yyyyMMddHHmm").compareTo(Dcdate) > 0)
											Mdcflg = "Y";
										finalDataToD = getDataDspnd_(
												dataFromUdd, orderToUddParm,
												orderDateTime, row_D, DmediQty,
												DmediUnit, DdosageQty,
												DdosageUnit, totAmtD, optUser,
												optDate, optTerm, takeMedOrg);
										// ����ҽ��֪�����
										if (!this.checkIsExistDspnd(caseNo,
												orderNo, orderSeq,
												orderDateTime.substring(0, 8),
												orderDateTime.substring(8))) {
											// ����ODI_DSPND
											result = InwForOdiTool
													.getInstance()
													.insertOdiDspnd(
															finalDataToD,
															connection);
										} else {
										}
										if (result.getErrCode() < 0) {
											err("ERR:" + result.getErrCode()
													+ result.getErrText()
													+ result.getErrName());
											return result;
										}
									}
									String MedNo = takeMedNoMap.get(caseNo
											+ orderNo + orderSeq) == null ? ""
											: (String) takeMedNoMap.get(caseNo
													+ orderNo + orderSeq);
									// ����ODI_DSPNM
									TParm finalDataToM = new TParm();
									finalDataToM = getDataDspnm(dataFromUdd,
											orderToUddParm, row_M,
											endDttmInDspnm, startDttmInDspnm,
											endDttmInDspnm, MdispenseQty,
											MdispenseUnit, MdosageQty,
											MdosageUnit, ownPrice, nhiPrice,
											ownAmt, totAmtM, discountRate,
											optUser, optDate, optTerm,
											takeMedOrg, MedNo, Mdcflg);
									if ((finalDataToM.getData("DC_DATE") instanceof TNull)
											&& finalDataToM.getValue(
													"DSPN_KIND").equals("UD")
											&& !isOperation) {
										finalDataToM.setData("DSPN_KIND", "F");
									}
									result = InwForOdiTool.getInstance()
											.insertOdiDspnm(finalDataToM,
													connection);
									if (result.getErrCode() < 0) {
										err("ERR:" + result.getErrCode()
												+ result.getErrText()
												+ result.getErrName());
										return result;
									}
									// ����ODI_ORDER
									TParm finalDataToOrder = new TParm();
									String caseNoTemp = (String) orderToUddParm
											.getData("CASE_NO");
									String orderNoTemp = (String) orderToUddParm
											.getData("ORDER_NO");
									int orderSeqTemp = Integer
											.parseInt(orderToUddParm
													.getData("ORDER_SEQ")
													+ "");

									// ����ODI_ORDER���ֶ�LASTDSPN_QTY LAST_DSPN_DATE
									finalDataToOrder = getDataOrder(
											orderToUddParm, caseNoTemp,
											orderNoTemp, orderSeqTemp,
											lastDspnDateForOrder, OlastDspnQty,
											OacumDspnQty, OacumMediQty,
											MdispenseQty, MdispenseUnit,
											MdosageQty, MdosageUnit, optUser,
											optDate, optTerm, checkDate,
											takeMedOrg);
									result = InwForOdiTool.getInstance()
											.updateOdiOrder(finalDataToOrder,
													connection);
									if (result.getErrCode() < 0) {
										err("ERR:" + result.getErrCode()
												+ result.getErrText()
												+ result.getErrName());
										return result;
									}
									if (result.getInt("RETURN") == 0) {// ���û�и��µ�����
										result.setErrCode(-100);
										result.setErrText("���������"
												+ orderToUddParm
														.getValue("ORDER_DESC")
												+ "�Ѳ�����,�����²�ѯ");
										return result;
									}
								}
							} // ����ODI_ORDER����DC�׶�
							else {
								String dateTimeDspnm = "";
								// ѭ��3--����ϸ�(����D���ÿһ������)
								for (int row_D = 0; row_D < insertDCount; row_D++) {
									String orderDateTime = (String) orderDateTimeFromUdd
											.get(row_D);
									// ����ODI_DSPND���ֶ�
									TParm DCDspnd = new TParm();
									Timestamp dcDate = orderToUddParm
											.getTimestamp("DC_DATE");
									String strDCTime = StringTool.getString(
											dcDate, "yyyyMMddHHmmss");
									String enddcTime = DCdispenseDttm.get(0)
											.toString();
									// shibl 20120413 modify
									if (!isOperation) {// ҩƷ
										if (orderDateTime.compareTo(strDCTime) < 0
												|| orderDateTime
														.compareTo(enddcTime) > 0)
											continue;
										if (dateTimeDspnm.length() == 0)
											dateTimeDspnm = orderDateTime
													.substring(0, 12);
									} else {
										if (orderDateTime.compareTo(strDCTime) < 0)
											continue;
										if (dateTimeDspnm.length() == 0)
											dateTimeDspnm = orderDateTime;
									}
									DCDspnd.setData("CASE_NO", caseNo);
									DCDspnd.setData("ORDER_NO", orderNo);
									DCDspnd.setData("ORDER_SEQ", orderSeq);
									DCDspnd.setData("DC_DATE", dcDate);
									DCDspnd.setData("ORDER_DATE", orderDateTime
											.substring(0, 8));
									DCDspnd.setData("ORDER_DATETIME",
											orderDateTime.substring(8));
									// DCDspnd.setData("NS_EXEC_DC_CODE",
									// optUser);
									// DCDspnd.setData("NS_EXEC_DC_DATE",
									// optDate);
									DCDspnd.setData("OPT_USER", optUser);
									DCDspnd.setData("OPT_DATE", optDate);
									DCDspnd.setData("OPT_TERM", optTerm);
									result = InwForOdiTool.getInstance()
											.updateDCToDspnD(DCDspnd,
													connection);
									if (result.getErrCode() < 0) {
										err("ERR:" + result.getErrCode()
												+ result.getErrText()
												+ result.getErrName());
										return result;
									}
								}
								// ����ODI_DSPNM���ֶ�(Ŀǰ��֧�ָ���DC)
								TParm DCDspnm = new TParm();
								Timestamp dcDate = orderToUddParm
										.getTimestamp("DC_DATE");
								String daDrCode = orderToUddParm
										.getValue("DC_DR_CODE");
								DCDspnm.setData("CASE_NO", caseNo);
								DCDspnm.setData("ORDER_NO", orderNo);
								DCDspnm.setData("ORDER_SEQ", orderSeq);
								DCDspnm.setData("DC_DATE", dcDate);
								DCDspnm.setData("DC_DR_CODE", daDrCode);
								DCDspnm.setData("START_DTTM", startDttmInDspnm);
								// DCDspnm.setData("NS_EXEC_DC_CODE", optUser);
								// DCDspnm.setData("NS_EXEC_DC_DATE", optDate);
								DCDspnm.setData("OPT_USER", optUser);
								DCDspnm.setData("OPT_DATE", optDate);
								DCDspnm.setData("OPT_TERM", optTerm);
								DCDspnm.setData("DATETIME", dateTimeDspnm);
								result = InwForOdiTool.getInstance()
										.updateDCToDspnM(DCDspnm, connection);
								if (result.getErrCode() < 0) {
									err("ERR:" + result.getErrCode()
											+ result.getErrText()
											+ result.getErrName());
									return result;
								}
								// ����ODI_ORDER���ֶ�
								TParm DCData = new TParm();
								DCData.setData("CASE_NO", caseNo);
								DCData.setData("ORDER_NO", orderNo);
								DCData.setData("ORDER_SEQ", orderSeq);
								DCData.setData("DC_NS_CHECK_CODE", optUser);
								DCData.setData("DC_NS_CHECK_DATE", optDate);
								if (orderToUddParm.getValue("NS_CHECK_CODE")
										.equals("")) {
									DCData.setData("NS_CHECK_CODE", optUser);
									DCData.setData("NS_CHECK_DATE", optDate);
								} else {
									DCData.setData("NS_CHECK_CODE",
											orderToUddParm
													.getValue("NS_CHECK_CODE"));
									DCData
											.setData(
													"NS_CHECK_DATE",
													orderToUddParm
															.getTimestamp("NS_CHECK_DATE"));
								}
								DCData.setData("OPT_USER", optUser);
								DCData.setData("OPT_DATE", optDate);
								DCData.setData("OPT_TERM", optTerm);
								DCData.setData("TAKEMED_ORG", takeMedOrg);
								result = InwForOdiTool.getInstance()
										.updateDCToOrder(DCData, connection);
								if (result.getErrCode() < 0) {
									err("ERR:" + result.getErrCode()
											+ result.getErrText()
											+ result.getErrName());
									return result;
								}
							}
						} // û��������ֻ����ODI_ORDER
						else {
							// �ж��Ƿ����ȡ����˼�¼ 20121030 shibl add
							if (checkIsNSDCExistDspnm(caseNo, orderNo, orderSeq)) {
								// ��˶���
								TParm doData = new TParm();
								doData.setData("CASE_NO", caseNo);
								doData.setData("ORDER_NO", orderNo);
								doData.setData("ORDER_SEQ", orderSeq);
								doData.setData("OPT_USER", optUser);
								doData.setData("OPT_TERM", optTerm);
								doData.setData("OPT_DATE", optDate);
								doData.setData("DC_NS_CHECK_DATE", new TNull(
										Timestamp.class));
								doData.setData("NS_CHECK_DATE", optDate);
								doData.setData("NS_CHECK_CODE", optUser);
								result = undoData(doData, connection);
								if (result.getErrCode() < 0) {
									err("ERR:" + result.getErrCode()
											+ result.getErrText()
											+ result.getErrName());
									return result;
								}
							} else if (startDttmInDspnm.length() != 0
									&& !this.checkIsExistDspnm(caseNo, orderNo,
											orderSeq, startDttmInDspnm)) {
								// ����ODI_ORDER
								TParm finalDataToOrder = new TParm();
								String caseNoTemp = (String) orderToUddParm
										.getData("CASE_NO");
								String orderNoTemp = (String) orderToUddParm
										.getData("ORDER_NO");
								int orderSeqTemp = Integer
										.parseInt(orderToUddParm
												.getData("ORDER_SEQ")
												+ "");
								// ����ODI_ORDER���ֶ�LASTDSPN_QTY LAST_DSPN_DATE
								// ACUMDSPN_QTY ACUMMEDI_QTY
								finalDataToOrder = getDataOrder(orderToUddParm,
										caseNoTemp, orderNoTemp, orderSeqTemp,
										lastDspnDateForOrder, 0, 0, 0, 0, "",
										0, "", optUser, optDate, optTerm,
										checkDate, takeMedOrg);
								result = InwForOdiTool.getInstance()
										.updateOdiOrder(finalDataToOrder,
												connection);
								if (result.getErrCode() < 0) {
									err("ERR:" + result.getErrCode()
											+ result.getErrText()
											+ result.getErrName());
									return result;
								}
							} else {
								// ����ODI_ORDER���ֶ�
								TParm DCData = new TParm();
								DCData.setData("CASE_NO", caseNo);
								DCData.setData("ORDER_NO", orderNo);
								DCData.setData("ORDER_SEQ", orderSeq);
								DCData.setData("DC_NS_CHECK_CODE", optUser);
								DCData.setData("DC_NS_CHECK_DATE", optDate);
								// �Ƿ���������ҽ��(��DC) SHIBL MODIFY 20121030
								if (orderToUddParm.getValue("NS_CHECK_CODE")
										.equals("")) {
									DCData.setData("NS_CHECK_CODE", optUser);
									DCData.setData("NS_CHECK_DATE", optDate);
								} else {
									DCData.setData("NS_CHECK_CODE",
											orderToUddParm
													.getValue("NS_CHECK_CODE"));
									DCData
											.setData(
													"NS_CHECK_DATE",
													orderToUddParm
															.getTimestamp("NS_CHECK_DATE"));
								}
								DCData.setData("OPT_USER", optUser);
								DCData.setData("OPT_DATE", optDate);
								DCData.setData("OPT_TERM", optTerm);
								DCData.setData("TAKEMED_ORG", takeMedOrg);
								result = InwForOdiTool.getInstance()
										.updateDCToOrder(DCData, connection);
								if (result.getErrCode() < 0) {
									err("ERR:" + result.getErrCode()
											+ result.getErrText()
											+ result.getErrName());
									return result;
								}
							}
						}
					} // ����ǳ�Ժ��ҩ
					else if ("DS".equals(orderRxKind)) {
						// �����Ҫ����ϸ�������
						TParm finalDataToD = new TParm();
						String orderDateTime = StringTool.getString(optDate,
								"yyyyMMddHHmmss");
						// �ж��Ƿ����ȡ����˼�¼
						if (checkIsNSDCExistDspnm(caseNo, orderNo, orderSeq)) {
							// System.out.println("-------�ж��Ƿ����ȡ����˼�¼----------");
							// ��˶���
							TParm doData = new TParm();
							doData.setData("CASE_NO", caseNo);
							doData.setData("ORDER_NO", orderNo);
							doData.setData("ORDER_SEQ", orderSeq);
							doData.setData("OPT_USER", optUser);
							doData.setData("OPT_TERM", optTerm);
							doData.setData("OPT_DATE", optDate);
							doData.setData("DC_NS_CHECK_DATE", new TNull(
									Timestamp.class));
							doData.setData("NS_CHECK_DATE", optDate);
							doData.setData("NS_CHECK_CODE", optUser);
							result = undoData(doData, connection);
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}
						} else {
							finalDataToD = getDataDspnd(orderPram,
									orderDateTime, optUser, optDate, optTerm,
									takeMedOrg);
							// ����ҽ��֪�����
							if (!this.checkIsExistDspnd(caseNo, orderNo,
									orderSeq, orderDateTime.substring(0, 8),
									orderDateTime.substring(8))) {
								// ����ODI_DSPND
								result = InwForOdiTool.getInstance()
										.insertOdiDspnd(finalDataToD,
												connection);
							} else {

							}
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}
							String MedNo = takeMedNoMap.get(caseNo + orderNo
									+ orderSeq) == null ? ""
									: (String) takeMedNoMap.get(caseNo
											+ orderNo + orderSeq);
							// ����ODI_DSPNM
							TParm finalDataToM = new TParm();
							finalDataToM = getDataDspnm(orderPram, optUser,
									optDate, optTerm, serviceLevel, takeMedOrg,
									MedNo);
							result = InwForOdiTool.getInstance()
									.insertOdiDspnm(finalDataToM, connection);
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}
							// ����ODI_ORDER
							TParm finalDataToOrder = new TParm();
							// ����ODI_ORDER���ֶ�LASTDSPN_QTY LAST_DSPN_DATE
							// ACUMDSPN_QTY
							// ACUMMEDI_QTY
							finalDataToOrder = getDataOrder(orderPram, optUser,
									optDate, optTerm, checkDate, takeMedOrg);
							result = InwForOdiTool.getInstance()
									.updateOdiOrder(finalDataToOrder,
											connection);
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}
						}
					} // סԺ��ҩ
					else if ("IG".equals(orderRxKind)) {
						// System.out.println("this is IG process");
						// ����UDD�ӿڷ��������͵�λ
						// DOSAGE_QTY(PS:�����޸Ĳ�ҩ��������ʱ��ֻ�Ƕ෵�����ݣ�������Ӧ�ز���)
						TParm getIGInfoFromUDD = TotQtyTool.getInstance()
								.getTotQty(orderPram.getRow(0));
						double dosageQtyIG = getIGInfoFromUDD.getDouble("QTY");
						// �÷���ֵ����ԭ������ֵ
						orderPram.setData("DOSAGE_QTY", 0, dosageQtyIG);
						orderPram.setData("DISPENSE_QTY", 0, dosageQtyIG); // ĿǰDOSAGE_QTY=DISPENSE_QTY
						// �����Ҫ����ϸ�������
						TParm finalDataToD = new TParm();
						String orderDateTime = StringTool.getString(optDate,
								"yyyyMMddHHmmss");
						finalDataToD = getDataDspnd(orderPram, orderDateTime,
								optUser, optDate, optTerm, takeMedOrg);
						// �ж��Ƿ����ȡ����˼�¼
						if (checkIsNSDCExistDspnm(caseNo, orderNo, orderSeq)) {
							// ��˶���
							TParm doData = new TParm();
							doData.setData("CASE_NO", caseNo);
							doData.setData("ORDER_NO", orderNo);
							doData.setData("ORDER_SEQ", orderSeq);
							doData.setData("OPT_USER", optUser);
							doData.setData("OPT_TERM", optTerm);
							doData.setData("OPT_DATE", optDate);
							doData.setData("DC_NS_CHECK_DATE", new TNull(
									Timestamp.class));
							doData.setData("NS_CHECK_DATE", optDate);
							doData.setData("NS_CHECK_CODE", optUser);
							result = undoData(doData, connection);
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}
						} else {
							// ����ҽ��֪�����
							if (!this.checkIsExistDspnd(caseNo, orderNo,
									orderSeq, orderDateTime.substring(0, 8),
									orderDateTime.substring(8))) {
							} else {
							}
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}
							// ����ODI_DSPNM
							String MedNo = takeMedNoMap.get(caseNo + orderNo
									+ orderSeq) == null ? ""
									: (String) takeMedNoMap.get(caseNo
											+ orderNo + orderSeq);
							TParm finalDataToM = new TParm();
							finalDataToM = getDataDspnm(orderPram, optUser,
									optDate, optTerm, serviceLevel, takeMedOrg,
									MedNo);
							result = InwForOdiTool.getInstance()
									.insertOdiDspnm(finalDataToM, connection);
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}

							// ����ODI_ORDER
							TParm finalDataToOrder = new TParm();
							// ����ODI_ORDER���ֶ�LASTDSPN_QTY LAST_DSPN_DATE
							// ACUMDSPN_QTY
							// ACUMMEDI_QTY
							finalDataToOrder = getDataOrder(orderPram, optUser,
									optDate, optTerm, checkDate, takeMedOrg);
							result = InwForOdiTool.getInstance()
									.updateOdiOrder(finalDataToOrder,
											connection);
							if (result.getErrCode() < 0) {
								err("ERR:" + result.getErrCode()
										+ result.getErrText()
										+ result.getErrName());
								return result;
							}
						}
					}
				}
			}
			if("Y".equals(parm.getValue("OPE"))){
				String date = SystemTool.getInstance().getDate().toString().substring(0, 19);
				for(int i = 0 ; i < dataParm.getCount() ; i++){
					String sql = "UPDATE ODI_DSPNM SET NS_EXEC_CODE = '"+parm.getValue("OPT_USER")+"' , NS_EXEC_DATE = TO_DATE('"+date+"','yyyy-MM-dd HH24:mi:ss')" +
					" WHERE ORDER_NO = '"+dataParm.getValue("ORDER_NO", i)+"' AND ORDER_SEQ = '"+dataParm.getValue("ORDER_SEQ", i)+"' AND CASE_NO = '"+dataParm.getValue("CASE_NO", i)+"'";
				
					
					result = new TParm(TJDODBTool.getInstance().update(sql,connection));
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode()
								+ result.getErrText()
								+ result.getErrName());
						return result;
					}
				}
			}
			connection.commit();
		}
		return result;
	}

	/**
	 * ������Ҫ����ODI_DSPNM������
	 * 
	 * @param fromUddData
	 *            List ��UDD���ص���Ҫ��ֵ
	 * @param orderParm
	 *            TParm ����Ļ�������
	 * @return TParm
	 */
	private TParm getDataDspnm(TParm orderParm, String optUser,
			Timestamp optDate, String optTerm, String serviceLevel,
			String takeMedOrg, String takeMedNo) {
		TParm priceForODI = getPrice(orderParm.getValue("CASE_NO", 0),
				orderParm.getValue("ORDER_CODE", 0), orderParm.getDouble(
						"DISPENSE_QTY", 0), serviceLevel);
		TParm result = new TParm();
		result.setData("CASE_NO", orderParm.getData("CASE_NO", 0));
		result.setData("ORDER_NO", orderParm.getData("ORDER_NO", 0));
		result.setData("ORDER_SEQ", orderParm.getData("ORDER_SEQ", 0));
		result
				.setData("ANTIBIOTIC_WAY", orderParm.getData("ANTIBIOTIC_WAY",
						0));// add by wanglong 20121210
		result.setData("START_DTTM", StringTool.getString(optDate, "yyyyMMdd")
				+ "0000");
		result.setData("END_DTTM", StringTool.getString(optDate, "yyyyMMdd")
				+ "2359");
		// =========== add by wukai on 20160601 start
		result.setData(
				"INFLUTION_RATE", 
				orderParm.getData("INFLUTION_RATE",0) == null ? "" : orderParm
						.getData("INFLUTION_RATE",0));
		// =========== add by wukai on 20160601 end
		result.setData("REGION_CODE",
				orderParm.getData("REGION_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("REGION_CODE", 0));
		result.setData("STATION_CODE",
				orderParm.getData("STATION_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("STATION_CODE", 0));
		result.setData("DEPT_CODE",
				orderParm.getData("DEPT_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("DEPT_CODE", 0));
		result.setData("VS_DR_CODE",
				orderParm.getData("VS_DR_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("VS_DR_CODE", 0));
		result
				.setData("BED_NO",
						orderParm.getData("BED_NO", 0) == null ? new TNull(
								String.class) : orderParm.getData("BED_NO", 0));

		result
				.setData("IPD_NO",
						orderParm.getData("IPD_NO", 0) == null ? new TNull(
								String.class) : orderParm.getData("IPD_NO", 0));
		result.setData("MR_NO",
				orderParm.getData("MR_NO", 0) == null ? new TNull(String.class)
						: orderParm.getData("MR_NO", 0));
		result.setData("DSPN_KIND",
				orderParm.getData("RX_KIND", 0) == null ? new TNull(
						String.class) : orderParm.getData("RX_KIND", 0));
		result.setData("DSPN_DATE",
				orderParm.getData("DSPN_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("DSPN_DATE", 0));
		result.setData("DSPN_USER",
				orderParm.getData("DSPN_USER", 0) == null ? new TNull(
						String.class) : orderParm.getData("DSPN_USER", 0));

		result.setData("DISPENSE_EFF_DATE", optDate);
		result.setData("RX_NO",
				orderParm.getData("RX_NO", 0) == null ? new TNull(String.class)
						: orderParm.getData("RX_NO", 0));
		result.setData("ORDER_CAT1_CODE", orderParm.getData("ORDER_CAT1_CODE",
				0) == null ? new TNull(String.class) : orderParm.getData(
				"ORDER_CAT1_CODE", 0));
		result.setData("CAT1_TYPE",
				orderParm.getData("CAT1_TYPE", 0) == null ? new TNull(
						String.class) : orderParm.getData("CAT1_TYPE", 0));
		result.setData("DISPENSE_END_DATE", optDate);

		result.setData("EXEC_DEPT_CODE",
				orderParm.getData("EXEC_DEPT_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("EXEC_DEPT_CODE", 0));
		result.setData("DISPENSE_FLG",
				orderParm.getData("DISPENSE_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("DISPENSE_FLG", 0));
		result.setData("AGENCY_ORG_CODE", orderParm.getData("AGENCY_ORG_CODE",
				0) == null ? new TNull(String.class) : orderParm.getData(
				"AGENCY_ORG_CODE", 0));
		result.setData("PRESCRIPT_NO",
				orderParm.getData("PRESCRIPT_NO", 0) == null ? new TNull(
						String.class) : orderParm.getData("PRESCRIPT_NO", 0));
		result.setData("LINKMAIN_FLG",
				orderParm.getData("LINKMAIN_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("LINKMAIN_FLG", 0));

		result.setData("LINK_NO",
				orderParm.getData("LINK_NO", 0) == null ? new TNull(
						String.class) : orderParm.getData("LINK_NO", 0));
		result.setData("ORDER_CODE",
				orderParm.getData("ORDER_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("ORDER_CODE", 0));
		result.setData("ORDER_DESC",
				orderParm.getData("ORDER_DESC", 0) == null ? new TNull(
						String.class) : orderParm.getData("ORDER_DESC", 0));
		result.setData("GOODS_DESC",
				orderParm.getData("GOODS_DESC", 0) == null ? new TNull(
						String.class) : orderParm.getData("GOODS_DESC", 0));
		result.setData("SPECIFICATION",
				orderParm.getData("SPECIFICATION", 0) == null ? new TNull(
						String.class) : orderParm.getData("SPECIFICATION", 0));

		result.setData("MEDI_QTY",
				orderParm.getData("MEDI_QTY", 0) == null ? 0.00 : orderParm
						.getData("MEDI_QTY", 0));
		result.setData("MEDI_UNIT",
				orderParm.getData("MEDI_UNIT", 0) == null ? new TNull(
						String.class) : orderParm.getData("MEDI_UNIT", 0));
		result.setData("FREQ_CODE",
				orderParm.getData("FREQ_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("FREQ_CODE", 0));
		result.setData("ROUTE_CODE",
				orderParm.getData("ROUTE_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("ROUTE_CODE", 0));
		result.setData("TAKE_DAYS",
				orderParm.getData("TAKE_DAYS", 0) == null ? new TNull(
						Integer.class) : orderParm.getData("TAKE_DAYS", 0));

		result.setData("DOSAGE_QTY",
				orderParm.getData("DOSAGE_QTY", 0) == null ? 0.00 : orderParm
						.getData("DOSAGE_QTY", 0));
		result.setData("DOSAGE_UNIT",
				orderParm.getData("DOSAGE_UNIT", 0) == null ? new TNull(
						String.class) : orderParm.getData("DOSAGE_UNIT", 0));
		result.setData("DISPENSE_QTY",
				orderParm.getData("DISPENSE_QTY", 0) == null ? 0.00 : orderParm
						.getData("DISPENSE_QTY", 0));
		result.setData("DISPENSE_UNIT",
				orderParm.getData("DISPENSE_UNIT", 0) == null ? new TNull(
						String.class) : orderParm.getData("DISPENSE_UNIT", 0));
		result.setData("GIVEBOX_FLG",
				orderParm.getData("GIVEBOX_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("GIVEBOX_FLG", 0));

		// ���üƷѽӿڵõ�
		result.setData("OWN_PRICE",
				priceForODI.getData("OWN_PRICE") == null ? 0.00 : priceForODI
						.getData("OWN_PRICE"));
		result.setData("NHI_PRICE",
				priceForODI.getData("NHI_PRICE") == null ? 0.00 : priceForODI
						.getData("NHI_PRICE"));
		result.setData("DISCOUNT_RATE",
				priceForODI.getData("RATE") == null ? 0.00 : priceForODI
						.getData("RATE")); // Ŀǰ����
		result.setData("OWN_AMT", priceForODI.getData("OWN_AMT") == null ? 0.00
				: priceForODI.getData("OWN_AMT")); // ���÷���
		result.setData("TOT_AMT", priceForODI.getData("TOT_AMT") == null ? 0.00
				: priceForODI.getData("TOT_AMT"));

		// M���е�ORDER_DATE��¼����ORDER���е�EFF_DATE
		result.setData("ORDER_DATE",
				orderParm.getData("EFF_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("EFF_DATE", 0));
		result.setData("ORDER_DEPT_CODE", orderParm.getData("ORDER_DEPT_CODE",
				0) == null ? new TNull(String.class) : orderParm.getData(
				"ORDER_DEPT_CODE", 0));
		result.setData("ORDER_DR_CODE",
				orderParm.getData("ORDER_DR_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("ORDER_DR_CODE", 0));
		result.setData("DR_NOTE",
				orderParm.getData("DR_NOTE", 0) == null ? new TNull(
						String.class) : orderParm.getData("DR_NOTE", 0));
		result.setData("ATC_FLG",
				orderParm.getData("ATC_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("ATC_FLG", 0));

		result.setData("SENDATC_FLG",
				orderParm.getData("SENDATC_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("SENDATC_FLG", 0));
		result
				.setData(
						"SENDATC_DTTM",
						orderParm.getData("SENDATC_DTTM", 0) == null ? new TNull(
								Timestamp.class)
								: orderParm.getData("SENDATC_DTTM", 0));
		result.setData("INJPRAC_GROUP",
				orderParm.getData("INJPRAC_GROUP", 0) == null ? new TNull(
						String.class) : orderParm.getData("INJPRAC_GROUP", 0));
		result.setData("DC_DATE",
				orderParm.getData("DC_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("DC_DATE", 0));
		result.setData("DC_TOT", orderParm.getData("DC_TOT", 0) == null ? 0.00
				: orderParm.getData("DC_TOT", 0));

		result
				.setData("RTN_NO",
						orderParm.getData("RTN_NO", 0) == null ? new TNull(
								String.class) : orderParm.getData("RTN_NO", 0));
		result.setData("RTN_NO_SEQ",
				orderParm.getData("RTN_NO_SEQ", 0) == null ? new TNull(
						Integer.class) : orderParm.getData("RTN_NO_SEQ", 0));
		result.setData("RTN_DOSAGE_QTY",
				orderParm.getData("RTN_DOSAGE_QTY", 0) == null ? 0.00
						: orderParm.getData("RTN_DOSAGE_QTY", 0));
		result.setData("RTN_DOSAGE_UNIT", orderParm.getData("RTN_DOSAGE_UNIT",
				0) == null ? new TNull(String.class) : orderParm.getData(
				"RTN_DOSAGE_UNIT", 0));
		result.setData("CANCEL_DOSAGE_QTY", orderParm.getData(
				"CANCEL_DOSAGE_QTY", 0) == null ? 0.00 : orderParm.getData(
				"CANCEL_DOSAGE_QTY", 0));

		result.setData("CANCELRSN_CODE",
				orderParm.getData("CANCELRSN_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("CANCELRSN_CODE", 0));
		result.setData("TRANSMIT_RSN_CODE", orderParm.getData(
				"TRANSMIT_RSN_CODE", 0) == null ? new TNull(String.class)
				: orderParm.getData("TRANSMIT_RSN_CODE", 0));
		result.setData("PHA_RETN_CODE",
				orderParm.getData("PHA_RETN_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("PHA_RETN_CODE", 0));
		result.setData("PHA_RETN_DATE",
				orderParm.getData("PHA_RETN_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm
						.getData("PHA_RETN_DATE", 0));
		result.setData("PHA_CHECK_CODE",
				orderParm.getData("PHA_CHECK_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("PHA_CHECK_CODE", 0));

		result.setData("PHA_CHECK_DATE",
				orderParm.getData("PHA_CHECK_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("PHA_CHECK_DATE",
						0));
		result.setData("PHA_DISPENSE_NO", orderParm.getData("PHA_DISPENSE_NO",
				0) == null ? new TNull(String.class) : orderParm.getData(
				"PHA_DISPENSE_NO", 0));
		result.setData("PHA_DOSAGE_CODE", orderParm.getData("PHA_DOSAGE_CODE",
				0) == null ? new TNull(String.class) : orderParm.getData(
				"PHA_DOSAGE_CODE", 0));
		result.setData("PHA_DOSAGE_DATE", orderParm.getData("PHA_DOSAGE_DATE",
				0) == null ? new TNull(Timestamp.class) : orderParm.getData(
				"PHA_DOSAGE_DATE", 0));
		result.setData("PHA_DISPENSE_CODE", orderParm.getData(
				"PHA_DISPENSE_CODE", 0) == null ? new TNull(String.class)
				: orderParm.getData("PHA_DISPENSE_CODE", 0));

		result.setData("PHA_DISPENSE_DATE", orderParm.getData(
				"PHA_DISPENSE_DATE", 0) == null ? new TNull(Timestamp.class)
				: orderParm.getData("PHA_DISPENSE_DATE", 0));
		result.setData("NS_EXEC_CODE",
				orderParm.getData("NS_EXEC_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("NS_EXEC_CODE", 0));
		result
				.setData(
						"NS_EXEC_DATE",
						orderParm.getData("NS_EXEC_DATE", 0) == null ? new TNull(
								Timestamp.class)
								: orderParm.getData("NS_EXEC_DATE", 0));
		result.setData("NS_EXEC_DC_CODE", orderParm.getData("NS_EXEC_DC_CODE",
				0) == null ? new TNull(String.class) : orderParm.getData(
				"NS_EXEC_DC_CODE", 0));
		result.setData("NS_EXEC_DC_DATE", orderParm.getData("NS_EXEC_DC_DATE",
				0) == null ? new TNull(Timestamp.class) : orderParm.getData(
				"NS_EXEC_DC_DATE", 0));

		result.setData("NS_USER",
				orderParm.getData("NS_USER", 0) == null ? new TNull(
						String.class) : orderParm.getData("NS_USER", 0));
		result.setData("CTRLDRUGCLASS_CODE", orderParm.getData(
				"CTRLDRUGCLASS_CODE", 0) == null ? new TNull(String.class)
				: orderParm.getData("CTRLDRUGCLASS_CODE", 0));
		result.setData("PHA_TYPE",
				orderParm.getData("PHA_TYPE", 0) == null ? new TNull(
						String.class) : orderParm.getData("PHA_TYPE", 0));
		result.setData("DOSE_TYPE",
				orderParm.getData("DOSE_TYPE", 0) == null ? new TNull(
						String.class) : orderParm.getData("DOSE_TYPE", 0));
		result.setData("DCTAGENT_CODE",
				orderParm.getData("DCTAGENT_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("DCTAGENT_CODE", 0));

		result.setData("DCTEXCEP_CODE",
				orderParm.getData("DCTEXCEP_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("DCTEXCEP_CODE", 0));
		result.setData("DCT_TAKE_QTY",
				orderParm.getData("DCT_TAKE_QTY", 0) == null ? new TNull(
						Integer.class) : orderParm.getData("DCT_TAKE_QTY", 0));
		result.setData("PACKAGE_AMT",
				orderParm.getData("PACKAGE_AMT", 0) == null ? new TNull(
						Integer.class) : orderParm.getData("PACKAGE_AMT", 0));
		result.setData("DCTAGENT_FLG",
				orderParm.getData("DCTAGENT_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("DCTAGENT_FLG", 0));
		result.setData("PRESRT_NO",
				orderParm.getData("PRESRT_NO", 0) == null ? new TNull(
						String.class) : orderParm.getData("PRESRT_NO", 0));

		result.setData("DECOCT_CODE",
				orderParm.getData("DECOCT_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("DECOCT_CODE", 0));
		result.setData("URGENT_FLG",
				orderParm.getData("URGENT_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("URGENT_FLG", 0));
		result.setData("SETMAIN_FLG",
				orderParm.getData("SETMAIN_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("SETMAIN_FLG", 0));
		result.setData("ORDERSET_GROUP_NO", orderParm.getData(
				"ORDERSET_GROUP_NO", 0) == null ? new TNull(String.class)
				: orderParm.getData("ORDERSET_GROUP_NO", 0));
		result.setData("ORDERSET_CODE",
				orderParm.getData("ORDERSET_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("ORDERSET_CODE", 0));

		result.setData("RPTTYPE_CODE",
				orderParm.getData("RPTTYPE_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("RPTTYPE_CODE", 0));
		result.setData("OPTITEM_CODE",
				orderParm.getData("OPTITEM_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("OPTITEM_CODE", 0));
		result.setData("HIDE_FLG",
				orderParm.getData("HIDE_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("HIDE_FLG", 0));
		result.setData("DEGREE_CODE",
				orderParm.getData("DEGREE_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("DEGREE_CODE", 0));
		result.setData("BILL_FLG",
				orderParm.getData("BILL_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("BILL_FLG", 0));

		result.setData("CASHIER_USER",
				orderParm.getData("CASHIER_USER", 0) == null ? new TNull(
						String.class) : orderParm.getData("CASHIER_USER", 0));
		result
				.setData(
						"CASHIER_DATE",
						orderParm.getData("CASHIER_DATE", 0) == null ? new TNull(
								Timestamp.class)
								: orderParm.getData("CASHIER_DATE", 0));
		result.setData("IBS_CASE_NO_SEQ", orderParm.getData("IBS_CASE_NO_SEQ",
				0) == null ? new TNull(Integer.class) : orderParm.getData(
				"IBS_CASE_NO_SEQ", 0));
		result.setData("IBS_SEQ_NO",
				orderParm.getData("IBS_SEQ_NO", 0) == null ? new TNull(
						Integer.class) : orderParm.getData("IBS_SEQ_NO", 0));
		result.setData("ANTIBIOTIC_CODE", orderParm.getData("ANTIBIOTIC_CODE",
				0) == null ? new TNull(String.class) : orderParm.getData(
				"ANTIBIOTIC_CODE", 0));
		result.setData("DC_DR_CODE",
				orderParm.getData("DC_DR_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("DC_DR_CODE", 0));

		result.setData("OPT_USER", optUser);
		result.setData("OPT_DATE", optDate);
		result.setData("OPT_TERM", optTerm);
		// ��ҩע��Ĭ��Ϊ2
		result.setData("TAKEMED_ORG", takeMedOrg);
		result.setData("TAKEMED_NO", takeMedNo);
		return result;
	}

	/**
	 * ������Ҫ����ODI_ORDER������
	 * 
	 * @param fromUddData
	 *            List ��UDD���ص���Ҫ��ֵ
	 * @param orderParm
	 *            TParm ����Ļ�������
	 * @return TParm
	 */
	private TParm getDataOrder(TParm orderParm, String caseNo, String orderNo,
			int orderSeq, Timestamp lastDspnDate, double lastDspnQty,
			double acumDspnQty, double acumMediQty, double dispenseQty,
			String dispenseUnit, double dosageQty, String dosageUnit,
			String optUser, Timestamp optDate, String optTerm,
			Timestamp checkDate, String takeMedOrg) {
		TParm result = new TParm();
		result.setData("CASE_NO", caseNo);
		result.setData("ORDER_NO", orderNo);
		result.setData("ORDER_SEQ", orderSeq);
		result.setData("LASTDSPN_QTY", lastDspnQty);
		result.setData("LAST_DSPN_DATE", lastDspnDate);
		result.setData("ACUMDSPN_QTY", acumDspnQty);
		result.setData("ACUMMEDI_QTY", acumMediQty);
		result.setData("NS_CHECK_CODE", optUser);
		// ���ڸ�����ֶ��޸����Ժ�OPT_DATE��ͬ
		result.setData("NS_CHECK_DATE", checkDate);
		result.setData("DOSAGE_QTY", dosageQty);
		result.setData("DOSAGE_UNIT", dosageUnit);
		result.setData("DISPENSE_QTY", dispenseQty);
		result.setData("DISPENSE_UNIT", dispenseUnit);
		result.setData("OPT_DATE", optDate);
		result.setData("OPT_USER", optUser);
		result.setData("OPT_TERM", optTerm);

		// ��û��DC_DATE��ʱ�򲻸���DC_NS_CHECK_CODE DC_NS_CHECK_DATE
		Timestamp daDate = (Timestamp) orderParm.getData("DC_DATE");
		if (orderParm.getValue("NS_CHECK_CODE").equals("")) {
			result.setData("DC_NS_CHECK_CODE", new TNull(String.class));
			result.setData("DC_NS_CHECK_DATE", new TNull(Timestamp.class));
		} else {
			result.setData("DC_NS_CHECK_CODE", optUser);
			result.setData("DC_NS_CHECK_DATE", optDate);
		}
		result.setData("TAKEMED_ORG", takeMedOrg);
		return result;
	}

	private TParm getDataOrder(TParm orderParm, String optUser,
			Timestamp optDate, String optTerm, Timestamp checkDate,
			String takeMedOrg) {
		TParm result = new TParm();
		result.setData("CASE_NO", orderParm.getData("CASE_NO", 0));
		result.setData("ORDER_NO", orderParm.getData("ORDER_NO", 0));
		result.setData("ORDER_SEQ", orderParm.getData("ORDER_SEQ", 0));
		result.setData("LASTDSPN_QTY", 0.00);
		result.setData("LAST_DSPN_DATE", new TNull(Timestamp.class));
		result.setData("ACUMDSPN_QTY", 0.00);
		result.setData("ACUMMEDI_QTY", 0.00);
		result.setData("NS_CHECK_CODE", optUser);
		// ���ڸ�����ֶ��޸����Ժ�OPT_DATE��ͬ
		result.setData("NS_CHECK_DATE", checkDate);
		result.setData("DOSAGE_QTY",
				orderParm.getData("DOSAGE_QTY", 0) == null ? 0.00 : orderParm
						.getData("DOSAGE_QTY", 0));
		result.setData("DOSAGE_UNIT",
				orderParm.getData("DOSAGE_UNIT", 0) == null ? new TNull(
						String.class) : orderParm.getData("DOSAGE_UNIT", 0));
		result.setData("DISPENSE_QTY",
				orderParm.getData("DISPENSE_QTY", 0) == null ? 0.00 : orderParm
						.getData("DISPENSE_QTY", 0));
		result.setData("DISPENSE_UNIT",
				orderParm.getData("DISPENSE_UNIT", 0) == null ? new TNull(
						String.class) : orderParm.getData("DISPENSE_UNIT", 0));
		result.setData("OPT_DATE", optDate);
		result.setData("OPT_USER", optUser);
		result.setData("OPT_TERM", optTerm);

		// ��û��DC_DATE��ʱ�򲻸���DC_NS_CHECK_CODE DC_NS_CHECK_DATE
		Timestamp daDate = (Timestamp) orderParm.getData("DC_DATE", 0);
		if (daDate == null) {
			result.setData("DC_NS_CHECK_CODE", new TNull(String.class));
			result.setData("DC_NS_CHECK_DATE", new TNull(Timestamp.class));
		} else {
			result.setData("DC_NS_CHECK_CODE", optUser);
			result.setData("DC_NS_CHECK_DATE", optDate);
		}
		result.setData("TAKEMED_ORG", takeMedOrg);
		return result;
	}

	/**
	 * ������Ҫ����ODI_DSPNM������
	 * 
	 * @param fromUddData
	 *            List ��UDD���ص���Ҫ��ֵ
	 * @param orderParm
	 *            TParm ����Ļ�������
	 * @return TParm
	 */
	private TParm getDataDspnm(List fromUddData, TParm orderParm, int row,
			String endDttmInDspnm, String startDttm, String endDttm,
			double dispenseQty, String dispenseUnit, double dosageQty,
			String dosageUnit, double ownPrice, double nhiPrice, double ownAmt,
			double totAmtM, double discountRate, String optUser,
			Timestamp optDate, String optTerm, String takeMedOrg,
			String takeMedNo, String Mdcflg) {
		TParm result = new TParm();
		result.setData("CASE_NO", orderParm.getData("CASE_NO"));
		result.setData("ORDER_NO", orderParm.getData("ORDER_NO"));
		result.setData("ORDER_SEQ", orderParm.getData("ORDER_SEQ"));
		result.setData("ANTIBIOTIC_WAY", orderParm.getData("ANTIBIOTIC_WAY"));// add
		// by
		// wanglong
		// 20121210
		// =========== add by wukai on 20160601 start
		result.setData(
				"INFLUTION_RATE",
				orderParm.getData("INFLUTION_RATE") == null ? new TNull(String.class) : orderParm.getData("INFLUTION_RATE"));
		// =========== add by wukai on 20160601 end
		
		result.setData("START_DTTM", startDttm);
		result.setData("END_DTTM", endDttm);

		result.setData("REGION_CODE",
				orderParm.getData("REGION_CODE") == null ? new TNull(
						String.class) : orderParm.getData("REGION_CODE"));
		result.setData("STATION_CODE",
				orderParm.getData("STATION_CODE") == null ? new TNull(
						String.class) : orderParm.getData("STATION_CODE"));
		result
				.setData("DEPT_CODE",
						orderParm.getData("DEPT_CODE") == null ? new TNull(
								String.class) : orderParm.getData("DEPT_CODE"));
		result.setData("VS_DR_CODE",
				orderParm.getData("VS_DR_CODE") == null ? new TNull(
						String.class) : orderParm.getData("VS_DR_CODE"));
		result.setData("BED_NO",
				orderParm.getData("BED_NO") == null ? new TNull(String.class)
						: orderParm.getData("BED_NO"));

		result.setData("IPD_NO",
				orderParm.getData("IPD_NO") == null ? new TNull(String.class)
						: orderParm.getData("IPD_NO"));
		result.setData("MR_NO", orderParm.getData("MR_NO") == null ? new TNull(
				String.class) : orderParm.getData("MR_NO"));
		result.setData("DSPN_KIND",
				orderParm.getData("RX_KIND") == null ? new TNull(String.class)
						: orderParm.getData("RX_KIND"));
		result.setData("DSPN_DATE",
				orderParm.getData("DSPN_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("DSPN_DATE"));
		result
				.setData("DSPN_USER",
						orderParm.getData("DSPN_USER") == null ? new TNull(
								String.class) : orderParm.getData("DSPN_USER"));

		result.setData("DISPENSE_EFF_DATE", optDate);
		result.setData("RX_NO", orderParm.getData("RX_NO") == null ? new TNull(
				String.class) : orderParm.getData("RX_NO"));
		result.setData("ORDER_CAT1_CODE",
				orderParm.getData("ORDER_CAT1_CODE") == null ? new TNull(
						String.class) : orderParm.getData("ORDER_CAT1_CODE"));
		result
				.setData("CAT1_TYPE",
						orderParm.getData("CAT1_TYPE") == null ? new TNull(
								String.class) : orderParm.getData("CAT1_TYPE"));
		result.setData("DISPENSE_END_DATE", StringTool.getTimestamp(
				endDttmInDspnm, "yyyyMMddHHmm"));

		result.setData("EXEC_DEPT_CODE",
				orderParm.getData("EXEC_DEPT_CODE") == null ? new TNull(
						String.class) : orderParm.getData("EXEC_DEPT_CODE"));
		//��ҩ��Ϊ0ʱ����Ҫ��ҩ
		if(dosageQty==0.0){
			orderParm.setData("DISPENSE_FLG", "Y");
		}
		result.setData("DISPENSE_FLG",
				orderParm.getData("DISPENSE_FLG") == null ? new TNull(
						String.class) : orderParm.getData("DISPENSE_FLG"));
		result.setData("AGENCY_ORG_CODE",
				orderParm.getData("AGENCY_ORG_CODE") == null ? new TNull(
						String.class) : orderParm.getData("AGENCY_ORG_CODE"));
		result.setData("PRESCRIPT_NO",
				orderParm.getData("PRESCRIPT_NO") == null ? new TNull(
						String.class) : orderParm.getData("PRESCRIPT_NO"));
		result.setData("LINKMAIN_FLG",
				orderParm.getData("LINKMAIN_FLG") == null ? new TNull(
						String.class) : orderParm.getData("LINKMAIN_FLG"));

		result.setData("LINK_NO",
				orderParm.getData("LINK_NO") == null ? new TNull(String.class)
						: orderParm.getData("LINK_NO"));
		result.setData("ORDER_CODE",
				orderParm.getData("ORDER_CODE") == null ? new TNull(
						String.class) : orderParm.getData("ORDER_CODE"));
		result.setData("ORDER_DESC",
				orderParm.getData("ORDER_DESC") == null ? new TNull(
						String.class) : orderParm.getData("ORDER_DESC"));
		result.setData("GOODS_DESC",
				orderParm.getData("GOODS_DESC") == null ? new TNull(
						String.class) : orderParm.getData("GOODS_DESC"));
		result.setData("SPECIFICATION",
				orderParm.getData("SPECIFICATION") == null ? new TNull(
						String.class) : orderParm.getData("SPECIFICATION"));

		result.setData("MEDI_QTY", orderParm.getData("MEDI_QTY") == null ? 0.00
				: orderParm.getData("MEDI_QTY"));
		result
				.setData("MEDI_UNIT",
						orderParm.getData("MEDI_UNIT") == null ? new TNull(
								String.class) : orderParm.getData("MEDI_UNIT"));
		result
				.setData("FREQ_CODE",
						orderParm.getData("FREQ_CODE") == null ? new TNull(
								String.class) : orderParm.getData("FREQ_CODE"));
		result.setData("ROUTE_CODE",
				orderParm.getData("ROUTE_CODE") == null ? new TNull(
						String.class) : orderParm.getData("ROUTE_CODE"));
		result.setData("TAKE_DAYS",
				orderParm.getData("TAKE_DAYS") == null ? new TNull(
						Integer.class) : orderParm.getData("TAKE_DAYS"));

		result.setData("DOSAGE_QTY", dosageQty);
		// �жϿ�� 20121115 shibl add ȡҩע��
		result.setData("TAKEMED_ORG", takeMedOrg);
		result.setData("TAKEMED_NO", takeMedNo);

		result.setData("DOSAGE_UNIT", dosageUnit);
		result.setData("DISPENSE_QTY", dispenseQty);
		result.setData("DISPENSE_UNIT", dispenseUnit);
		result.setData("GIVEBOX_FLG",
				orderParm.getData("GIVEBOX_FLG") == null ? new TNull(
						String.class) : orderParm.getData("GIVEBOX_FLG"));

		// ���ýӿڷ��÷���
		result.setData("OWN_PRICE", ownPrice);
		result.setData("NHI_PRICE", nhiPrice);
		result.setData("DISCOUNT_RATE", discountRate);
		result.setData("OWN_AMT", ownAmt);
		result.setData("TOT_AMT", totAmtM);

		// M���е�ORDER_DATE��¼����ORDER���е�EFF_DATE
		result.setData("ORDER_DATE",
				orderParm.getData("EFF_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("EFF_DATE"));
		result.setData("ORDER_DEPT_CODE",
				orderParm.getData("ORDER_DEPT_CODE") == null ? new TNull(
						String.class) : orderParm.getData("ORDER_DEPT_CODE"));
		result.setData("ORDER_DR_CODE",
				orderParm.getData("ORDER_DR_CODE") == null ? new TNull(
						String.class) : orderParm.getData("ORDER_DR_CODE"));
		result.setData("DR_NOTE",
				orderParm.getData("DR_NOTE") == null ? new TNull(String.class)
						: orderParm.getData("DR_NOTE"));
		result.setData("ATC_FLG",
				orderParm.getData("ATC_FLG") == null ? new TNull(String.class)
						: orderParm.getData("ATC_FLG"));
		result.setData("SENDATC_FLG",
				orderParm.getData("SENDATC_FLG") == null ? new TNull(
						String.class) : orderParm.getData("SENDATC_FLG"));
		result.setData("SENDATC_DTTM",
				orderParm.getData("SENDATC_DTTM") == null ? new TNull(
						Timestamp.class) : orderParm.getData("SENDATC_DTTM"));
		result.setData("INJPRAC_GROUP",
				orderParm.getData("INJPRAC_GROUP") == null ? new TNull(
						String.class) : orderParm.getData("INJPRAC_GROUP"));
		if (Mdcflg.equals("Y")) {
			result.setData("DC_DATE",
					orderParm.getData("DC_DATE") == null ? new TNull(
							Timestamp.class) : orderParm.getData("DC_DATE"));
			result.setData("DC_DR_CODE",
					orderParm.getData("DC_DR_CODE") == null ? new TNull(
							String.class) : orderParm.getData("DC_DR_CODE"));
		}else{
			result.setData("DC_DATE", new TNull(Timestamp.class));
			result.setData("DC_DR_CODE",new TNull(String.class));
		}
		result.setData("DC_TOT", orderParm.getData("DC_TOT") == null ? 0.00
				: orderParm.getData("DC_TOT"));

		result.setData("RTN_NO",
				orderParm.getData("RTN_NO") == null ? new TNull(String.class)
						: orderParm.getData("RTN_NO"));
		result.setData("RTN_NO_SEQ",
				orderParm.getData("RTN_NO_SEQ") == null ? new TNull(
						Integer.class) : orderParm.getData("RTN_NO_SEQ"));
		result.setData("RTN_DOSAGE_QTY",
				orderParm.getData("RTN_DOSAGE_QTY") == null ? 0.00 : orderParm
						.getData("RTN_DOSAGE_QTY"));
		result.setData("RTN_DOSAGE_UNIT",
				orderParm.getData("RTN_DOSAGE_UNIT") == null ? new TNull(
						String.class) : orderParm.getData("RTN_DOSAGE_UNIT"));
		result.setData("CANCEL_DOSAGE_QTY", orderParm
				.getData("CANCEL_DOSAGE_QTY") == null ? 0.00 : orderParm
				.getData("CANCEL_DOSAGE_QTY"));

		result.setData("CANCELRSN_CODE",
				orderParm.getData("CANCELRSN_CODE") == null ? new TNull(
						String.class) : orderParm.getData("CANCELRSN_CODE"));
		result.setData("TRANSMIT_RSN_CODE", orderParm
				.getData("TRANSMIT_RSN_CODE") == null ? new TNull(String.class)
				: orderParm.getData("TRANSMIT_RSN_CODE"));
		result.setData("PHA_RETN_CODE",
				orderParm.getData("PHA_RETN_CODE") == null ? new TNull(
						String.class) : orderParm.getData("PHA_RETN_CODE"));
		result.setData("PHA_RETN_DATE",
				orderParm.getData("PHA_RETN_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("PHA_RETN_DATE"));
		result.setData("PHA_CHECK_CODE",
				orderParm.getData("PHA_CHECK_CODE") == null ? new TNull(
						String.class) : orderParm.getData("PHA_CHECK_CODE"));

		result.setData("PHA_CHECK_DATE",
				orderParm.getData("PHA_CHECK_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("PHA_CHECK_DATE"));
		result.setData("PHA_DISPENSE_NO",
				orderParm.getData("PHA_DISPENSE_NO") == null ? new TNull(
						String.class) : orderParm.getData("PHA_DISPENSE_NO"));
		result.setData("PHA_DOSAGE_CODE",
				orderParm.getData("PHA_DOSAGE_CODE") == null ? new TNull(
						String.class) : orderParm.getData("PHA_DOSAGE_CODE"));
		result
				.setData("PHA_DOSAGE_DATE", orderParm
						.getData("PHA_DOSAGE_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("PHA_DOSAGE_DATE"));
		result.setData("PHA_DISPENSE_CODE", orderParm
				.getData("PHA_DISPENSE_CODE") == null ? new TNull(String.class)
				: orderParm.getData("PHA_DISPENSE_CODE"));

		result.setData("PHA_DISPENSE_DATE", orderParm
				.getData("PHA_DISPENSE_DATE") == null ? new TNull(
				Timestamp.class) : orderParm.getData("PHA_DISPENSE_DATE"));
		result.setData("NS_EXEC_CODE",
				orderParm.getData("NS_EXEC_CODE") == null ? new TNull(
						String.class) : orderParm.getData("NS_EXEC_CODE"));
		result.setData("NS_EXEC_DATE",
				orderParm.getData("NS_EXEC_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("NS_EXEC_DATE"));
		result.setData("NS_EXEC_DC_CODE",
				orderParm.getData("NS_EXEC_DC_CODE") == null ? new TNull(
						String.class) : orderParm.getData("NS_EXEC_DC_CODE"));
		result
				.setData("NS_EXEC_DC_DATE", orderParm
						.getData("NS_EXEC_DC_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("NS_EXEC_DC_DATE"));

		result.setData("NS_USER",
				orderParm.getData("NS_USER") == null ? new TNull(String.class)
						: orderParm.getData("NS_USER"));
		result
				.setData("CTRLDRUGCLASS_CODE", orderParm
						.getData("CTRLDRUGCLASS_CODE") == null ? new TNull(
						String.class) : orderParm.getData("CTRLDRUGCLASS_CODE"));
		result.setData("PHA_TYPE",
				orderParm.getData("PHA_TYPE") == null ? new TNull(String.class)
						: orderParm.getData("PHA_TYPE"));
		result
				.setData("DOSE_TYPE",
						orderParm.getData("DOSE_TYPE") == null ? new TNull(
								String.class) : orderParm.getData("DOSE_TYPE"));
		result.setData("DCTAGENT_CODE",
				orderParm.getData("DCTAGENT_CODE") == null ? new TNull(
						String.class) : orderParm.getData("DCTAGENT_CODE"));

		result.setData("DCTEXCEP_CODE",
				orderParm.getData("DCTEXCEP_CODE") == null ? new TNull(
						String.class) : orderParm.getData("DCTEXCEP_CODE"));
		result.setData("DCT_TAKE_QTY",
				orderParm.getData("DCT_TAKE_QTY") == null ? new TNull(
						Integer.class) : orderParm.getData("DCT_TAKE_QTY"));
		result.setData("PACKAGE_AMT",
				orderParm.getData("PACKAGE_AMT") == null ? new TNull(
						Integer.class) : orderParm.getData("PACKAGE_AMT"));
		result.setData("DCTAGENT_FLG",
				orderParm.getData("DCTAGENT_FLG") == null ? new TNull(
						String.class) : orderParm.getData("DCTAGENT_FLG"));
		result
				.setData("PRESRT_NO",
						orderParm.getData("PRESRT_NO") == null ? new TNull(
								String.class) : orderParm.getData("PRESRT_NO"));

		result.setData("DECOCT_CODE",
				orderParm.getData("DECOCT_CODE") == null ? new TNull(
						String.class) : orderParm.getData("DECOCT_CODE"));
		result.setData("URGENT_FLG",
				orderParm.getData("URGENT_FLG") == null ? new TNull(
						String.class) : orderParm.getData("URGENT_FLG"));
		result.setData("SETMAIN_FLG",
				orderParm.getData("SETMAIN_FLG") == null ? new TNull(
						String.class) : orderParm.getData("SETMAIN_FLG"));
		result.setData("ORDERSET_GROUP_NO", orderParm
				.getData("ORDERSET_GROUP_NO") == null ? new TNull(String.class)
				: orderParm.getData("ORDERSET_GROUP_NO"));
		result.setData("ORDERSET_CODE",
				orderParm.getData("ORDERSET_CODE") == null ? new TNull(
						String.class) : orderParm.getData("ORDERSET_CODE"));

		result.setData("RPTTYPE_CODE",
				orderParm.getData("RPTTYPE_CODE") == null ? new TNull(
						String.class) : orderParm.getData("RPTTYPE_CODE"));
		result.setData("OPTITEM_CODE",
				orderParm.getData("OPTITEM_CODE") == null ? new TNull(
						String.class) : orderParm.getData("OPTITEM_CODE"));
		result.setData("HIDE_FLG",
				orderParm.getData("HIDE_FLG") == null ? new TNull(String.class)
						: orderParm.getData("HIDE_FLG"));
		result.setData("DEGREE_CODE",
				orderParm.getData("DEGREE_CODE") == null ? new TNull(
						String.class) : orderParm.getData("DEGREE_CODE"));
		result.setData("BILL_FLG",
				orderParm.getData("BILL_FLG") == null ? new TNull(String.class)
						: orderParm.getData("BILL_FLG"));

		result.setData("CASHIER_USER",
				orderParm.getData("CASHIER_USER") == null ? new TNull(
						String.class) : orderParm.getData("CASHIER_USER"));
		result.setData("CASHIER_DATE",
				orderParm.getData("CASHIER_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("CASHIER_DATE"));
		result.setData("IBS_CASE_NO_SEQ",
				orderParm.getData("IBS_CASE_NO_SEQ") == null ? new TNull(
						Integer.class) : orderParm.getData("IBS_CASE_NO_SEQ"));
		result.setData("IBS_SEQ_NO",
				orderParm.getData("IBS_SEQ_NO") == null ? new TNull(
						Integer.class) : orderParm.getData("IBS_SEQ_NO"));
		result.setData("ANTIBIOTIC_CODE",
				orderParm.getData("ANTIBIOTIC_CODE") == null ? new TNull(
						String.class) : orderParm.getData("ANTIBIOTIC_CODE"));
		result.setData("OPT_USER", optUser);
		result.setData("OPT_DATE", optDate);
		result.setData("OPT_TERM", optTerm);
		System.out.println("5555555"+result);
		return result;
	}

	/**
	 * ������Ҫ����ODI_DSPND������
	 * 
	 * @param fromUddData
	 *            List ��UDD���ص���Ҫ��ֵ
	 * @param orderParm
	 *            TParm ����Ļ�������
	 * @param orderDateTime
	 *            String �ʹ�ʱ��
	 * @param row
	 *            int ����
	 * @param mediQty
	 *            double Ҫ����ODI_DSPND�������
	 * @param mediUnit
	 *            String Ҫ����ODI_DSPND�������
	 * @param dosageQty
	 *            double Ҫ����ODI_DSPND�������
	 * @param dosageUnit
	 *            String Ҫ����ODI_DSPND�������
	 * @param optUser
	 *            String
	 * @param optDate
	 *            Timestamp
	 * @param optTerm
	 *            String
	 * @return TParm
	 */
	private TParm getDataDspnd_(List fromUddData, TParm orderParm,
			String orderDateTime, int row, double mediQty, String mediUnit,
			double dosageQty, String dosageUnit, double totAmtD,
			String optUser, Timestamp optDate, String optTerm, String takeMedOrg) {

		TParm result = new TParm();
		result.setData("CASE_NO", orderParm.getData("CASE_NO"));
		result.setData("ORDER_NO", orderParm.getData("ORDER_NO"));
		result.setData("ORDER_SEQ", orderParm.getData("ORDER_SEQ"));
		result.setData("ANTIBIOTIC_WAY", orderParm.getData("ANTIBIOTIC_WAY"));// add
		// by
		// wanglong
		// 20121210
		result.setData("ORDER_DATE", orderDateTime.substring(0, 8));
		result.setData("ORDER_DATETIME", orderDateTime.substring(8, 12));

		result.setData("BATCH_CODE",
				orderParm.getData("BATCH_CODE") == null ? new TNull(
						String.class) : orderParm.getData("BATCH_CODE"));
		result.setData("TREAT_START_TIME", orderParm
				.getData("TREAT_START_TIME") == null ? new TNull(String.class)
				: orderParm.getData("TREAT_START_TIME"));
		result.setData("TREAT_END_TIME",
				orderParm.getData("TREAT_END_TIME") == null ? new TNull(
						String.class) : orderParm.getData("TREAT_END_TIME"));
		result
				.setData("NURSE_DISPENSE_FLG", orderParm
						.getData("NURSE_DISPENSE_FLG") == null ? new TNull(
						String.class) : orderParm.getData("NURSE_DISPENSE_FLG"));
		result.setData("BAR_CODE",
				orderParm.getData("BAR_CODE") == null ? new TNull(String.class)
						: orderParm.getData("BAR_CODE"));

		result.setData("ORDER_CODE",
				orderParm.getData("ORDER_CODE") == null ? new TNull(
						String.class) : orderParm.getData("ORDER_CODE"));
		
		result.setData("SKIN_RESULT",
				orderParm.getData("SKIN_RESULT") == null ? new TNull(
						String.class) : orderParm.getData("SKIN_RESULT"));
		
		result.setData("MEDI_QTY", mediQty);
		result.setData("MEDI_UNIT", mediUnit);
		result.setData("DOSAGE_QTY", dosageQty);
		// shibl 20121116 add ȡҩ��Դ
		result.setData("TAKEMED_ORG", takeMedOrg);

		result.setData("DOSAGE_UNIT", dosageUnit);
		result.setData("TOT_AMT", totAmtD);
		if(!orderParm.getValue("DC_DATE").equals("")&&StringTool.getTimestamp(orderDateTime, "yyyyMMddHHmm").compareTo(orderParm.getTimestamp("DC_DATE"))>0){
			result.setData("DC_DATE",
					orderParm.getData("DC_DATE") == null ? new TNull(
							Timestamp.class) : orderParm.getData("DC_DATE"));
		}else{
			result.setData("DC_DATE",new TNull(Timestamp.class));
		}
		result.setData("PHA_DISPENSE_NO",
				orderParm.getData("PHA_DISPENSE_NO") == null ? new TNull(
						String.class) : orderParm.getData("PHA_DISPENSE_NO"));
		result.setData("PHA_DOSAGE_CODE",
				orderParm.getData("PHA_DOSAGE_CODE") == null ? new TNull(
						String.class) : orderParm.getData("PHA_DOSAGE_CODE"));
		result
				.setData("PHA_DOSAGE_DATE", orderParm
						.getData("PHA_DOSAGE_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("PHA_DOSAGE_DATE"));

		result.setData("PHA_DISPENSE_CODE",
				orderParm.getData("NS_EXEC_CODE") == null ? new TNull(
						String.class) : orderParm.getData("NS_EXEC_CODE"));
		result.setData("PHA_DISPENSE_DATE",
				orderParm.getData("NS_EXEC_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("NS_EXEC_DATE"));
		result.setData("NS_EXEC_CODE",
				orderParm.getData("NS_EXEC_CODE") == null ? new TNull(
						String.class) : orderParm.getData("NS_EXEC_CODE"));
		result.setData("NS_EXEC_DATE",
				orderParm.getData("NS_EXEC_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("NS_EXEC_DATE"));
		result.setData("NS_EXEC_DC_CODE",
				orderParm.getData("NS_EXEC_DC_CODE") == null ? new TNull(
						String.class) : orderParm.getData("NS_EXEC_DC_CODE"));

		result
				.setData("NS_EXEC_DC_DATE", orderParm
						.getData("NS_EXEC_DC_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("NS_EXEC_DC_DATE"));
		result.setData("NS_USER",
				orderParm.getData("NS_USER") == null ? new TNull(String.class)
						: orderParm.getData("NS_USER"));
		result
				.setData("EXEC_NOTE",
						orderParm.getData("EXEC_NOTE") == null ? new TNull(
								String.class) : orderParm.getData("EXEC_NOTE"));
		result.setData("EXEC_DEPT_CODE",
				orderParm.getData("EXEC_DEPT_CODE") == null ? new TNull(
						String.class) : orderParm.getData("EXEC_DEPT_CODE"));
		result.setData("BILL_FLG",
				orderParm.getData("BILL_FLG") == null ? new TNull(String.class)
						: orderParm.getData("BILL_FLG"));

		result.setData("CASHIER_CODE",
				orderParm.getData("CASHIER_CODE") == null ? new TNull(
						String.class) : orderParm.getData("CASHIER_CODE"));
		result.setData("CASHIER_DATE",
				orderParm.getData("CASHIER_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("CASHIER_DATE"));
		result.setData("PHA_RETN_CODE",
				orderParm.getData("PHA_RETN_CODE") == null ? new TNull(
						String.class) : orderParm.getData("PHA_RETN_CODE"));
		result.setData("PHA_RETN_DATE",
				orderParm.getData("PHA_RETN_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("PHA_RETN_DATE"));
		result.setData("TRANSMIT_RSN_CODE", orderParm
				.getData("TRANSMIT_RSN_CODE") == null ? new TNull(String.class)
				: orderParm.getData("TRANSMIT_RSN_CODE"));

		result.setData("STOPCHECK_USER",
				orderParm.getData("STOPCHECK_USER") == null ? new TNull(
						String.class) : orderParm.getData("STOPCHECK_USER"));
		result.setData("STOPCHECK_DATE",
				orderParm.getData("STOPCHECK_DATE") == null ? new TNull(
						Timestamp.class) : orderParm.getData("STOPCHECK_DATE"));
		result.setData("IBS_CASE_NO",
				orderParm.getData("IBS_CASE_NO") == null ? new TNull(
						String.class) : orderParm.getData("IBS_CASE_NO"));
		result.setData("IBS_CASE_NO_SEQ",
				orderParm.getData("IBS_CASE_NO_SEQ") == null ? new TNull(
						String.class) : orderParm.getData("IBS_CASE_NO_SEQ"));

		result.setData("OPT_USER", optUser);
		result.setData("OPT_DATE", optDate);
		result.setData("OPT_TERM", optTerm);

		return result;
	}

	/**
	 * ��Ժ��ҩʹ��ֱ�Ӵ�ODI_ORDER��COPY���ݵ�ODI_DSPND
	 * 
	 * @param orderParm
	 *            TParm
	 * @param optUser
	 *            String
	 * @param optDate
	 *            Timestamp
	 * @param optTerm
	 *            String
	 * @return TParm
	 */
	private TParm getDataDspnd(TParm orderParm, String orderDateTime,
			String optUser, Timestamp optDate, String optTerm, String takeMedOrg) {
		TParm result = new TParm();
		result.setData("CASE_NO", orderParm.getData("CASE_NO", 0));
		result.setData("ORDER_NO", orderParm.getData("ORDER_NO", 0));
		result.setData("ORDER_SEQ", orderParm.getData("ORDER_SEQ", 0));
		result
				.setData("ANTIBIOTIC_WAY", orderParm.getData("ANTIBIOTIC_WAY",
						0));// add by wanglong 20121210
		result.setData("ORDER_DATE", orderDateTime.substring(0, 8));
		result.setData("ORDER_DATETIME", orderDateTime.substring(8));

		result.setData("BATCH_CODE",
				orderParm.getData("BATCH_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("BATCH_CODE", 0));
		result.setData("TREAT_START_TIME", orderParm.getData(
				"TREAT_START_TIME", 0) == null ? new TNull(String.class)
				: orderParm.getData("TREAT_START_TIME", 0));
		result.setData("TREAT_END_TIME",
				orderParm.getData("TREAT_END_TIME", 0) == null ? new TNull(
						String.class) : orderParm.getData("TREAT_END_TIME", 0));
		result.setData("NURSE_DISPENSE_FLG", orderParm.getData(
				"NURSE_DISPENSE_FLG", 0) == null ? new TNull(String.class)
				: orderParm.getData("NURSE_DISPENSE_FLG", 0));
		result.setData("BAR_CODE",
				orderParm.getData("BAR_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("BAR_CODE", 0));

		result.setData("SKIN_RESULT",
				orderParm.getData("SKIN_RESULT") == null ? new TNull(
						String.class) : orderParm.getData("SKIN_RESULT",0));
		
		result.setData("ORDER_CODE",
				orderParm.getData("ORDER_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("ORDER_CODE", 0));
		result.setData("MEDI_QTY",
				orderParm.getData("MEDI_QTY", 0) == null ? 0.00 : orderParm
						.getData("MEDI_QTY", 0));
		result.setData("MEDI_UNIT",
				orderParm.getData("MEDI_UNIT", 0) == null ? new TNull(
						String.class) : orderParm.getData("MEDI_UNIT", 0));
		result.setData("DOSAGE_QTY",
				orderParm.getData("DOSAGE_QTY", 0) == null ? 0.00 : orderParm
						.getData("DOSAGE_QTY", 0));
		result.setData("DOSAGE_UNIT",
				orderParm.getData("DOSAGE_UNIT", 0) == null ? new TNull(
						String.class) : orderParm.getData("DOSAGE_UNIT", 0));

		result.setData("TOT_AMT",
				orderParm.getData("TOT_AMT", 0) == null ? 0.00 : orderParm
						.getData("TOT_AMT", 0));
		result.setData("DC_DATE",
				orderParm.getData("DC_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("DC_DATE", 0));
		result.setData("PHA_DISPENSE_NO", orderParm.getData("PHA_DISPENSE_NO",
				0) == null ? new TNull(String.class) : orderParm.getData(
				"PHA_DISPENSE_NO", 0));
		result.setData("PHA_DOSAGE_CODE", orderParm.getData("PHA_DOSAGE_CODE",
				0) == null ? new TNull(String.class) : orderParm.getData(
				"PHA_DOSAGE_CODE", 0));
		result.setData("PHA_DOSAGE_DATE", orderParm.getData("PHA_DOSAGE_DATE",
				0) == null ? new TNull(Timestamp.class) : orderParm.getData(
				"PHA_DOSAGE_DATE", 0));

		result.setData("PHA_DISPENSE_CODE", orderParm
				.getData("NS_EXEC_CODE", 0) == null ? new TNull(String.class)
				: orderParm.getData("NS_EXEC_CODE", 0));
		result
				.setData("PHA_DISPENSE_DATE", orderParm.getData("NS_EXEC_DATE",
						0) == null ? new TNull(Timestamp.class) : orderParm
						.getData("NS_EXEC_DATE", 0));
		result.setData("NS_EXEC_CODE",
				orderParm.getData("NS_EXEC_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("NS_EXEC_CODE", 0));
		result
				.setData(
						"NS_EXEC_DATE",
						orderParm.getData("NS_EXEC_DATE", 0) == null ? new TNull(
								Timestamp.class)
								: orderParm.getData("NS_EXEC_DATE", 0));
		result.setData("NS_EXEC_DC_CODE", orderParm.getData("NS_EXEC_DC_CODE",
				0) == null ? new TNull(String.class) : orderParm.getData(
				"NS_EXEC_DC_CODE", 0));

		result.setData("NS_EXEC_DC_DATE", orderParm.getData("NS_EXEC_DC_DATE",
				0) == null ? new TNull(Timestamp.class) : orderParm.getData(
				"NS_EXEC_DC_DATE", 0));
		result.setData("NS_USER",
				orderParm.getData("NS_USER", 0) == null ? new TNull(
						String.class) : orderParm.getData("NS_USER", 0));
		result.setData("EXEC_NOTE",
				orderParm.getData("EXEC_NOTE", 0) == null ? new TNull(
						String.class) : orderParm.getData("EXEC_NOTE", 0));
		result.setData("EXEC_DEPT_CODE",
				orderParm.getData("EXEC_DEPT_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("EXEC_DEPT_CODE", 0));
		result.setData("BILL_FLG",
				orderParm.getData("BILL_FLG", 0) == null ? new TNull(
						String.class) : orderParm.getData("BILL_FLG", 0));

		result.setData("CASHIER_CODE",
				orderParm.getData("CASHIER_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("CASHIER_CODE", 0));
		result
				.setData(
						"CASHIER_DATE",
						orderParm.getData("CASHIER_DATE", 0) == null ? new TNull(
								Timestamp.class)
								: orderParm.getData("CASHIER_DATE", 0));
		result.setData("PHA_RETN_CODE",
				orderParm.getData("PHA_RETN_CODE", 0) == null ? new TNull(
						String.class) : orderParm.getData("PHA_RETN_CODE", 0));
		result.setData("PHA_RETN_DATE",
				orderParm.getData("PHA_RETN_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm
						.getData("PHA_RETN_DATE", 0));
		result.setData("TRANSMIT_RSN_CODE", orderParm.getData(
				"TRANSMIT_RSN_CODE", 0) == null ? new TNull(String.class)
				: orderParm.getData("TRANSMIT_RSN_CODE", 0));

		result.setData("STOPCHECK_USER",
				orderParm.getData("STOPCHECK_USER", 0) == null ? new TNull(
						String.class) : orderParm.getData("STOPCHECK_USER", 0));
		result.setData("STOPCHECK_DATE",
				orderParm.getData("STOPCHECK_DATE", 0) == null ? new TNull(
						Timestamp.class) : orderParm.getData("STOPCHECK_DATE",
						0));
		result.setData("IBS_CASE_NO",
				orderParm.getData("IBS_CASE_NO", 0) == null ? new TNull(
						String.class) : orderParm.getData("IBS_CASE_NO", 0));
		result.setData("IBS_CASE_NO_SEQ", orderParm.getData("IBS_CASE_NO_SEQ",
				0) == null ? new TNull(String.class) : orderParm.getData(
				"IBS_CASE_NO_SEQ", 0));

		result.setData("OPT_USER", optUser);
		result.setData("OPT_DATE", optDate);
		result.setData("OPT_TERM", optTerm);
		// ��ҩע��Ĭ��Ϊ2
		result.setData("TAKEMED_ORG", takeMedOrg);
		return result;
	}

	/**
	 * ����caseNo�õ��ò��˵ĳ��ڴ��� PS:����֧�ֶಡ����˵�ʱ��ÿ��ֻ��һ��caseNo
	 */
	private TParm getAnOrder(String caseNo, String orderNo, String orderSeq,
			String setMainFlg, String orderSetGroupNo) {
		TParm result = new TParm();
		String SelSql = "";
		// �Ƿ��Ǽ���ҽ������
		boolean isSetOrder = "Y".equals(setMainFlg);
		// ------------������ҽ��--------------start----------------------------
		// �������Ϊ����ҽ���ҳ���������ϸ��һ�𱣴�
		if (isSetOrder) {
			SelSql = "SELECT * FROM ODI_ORDER WHERE CASE_NO='" + caseNo
					+ "' AND ORDER_NO='" + orderNo + "' AND ORDERSET_GROUP_NO="
					+ Integer.parseInt(orderSetGroupNo);
		} // ----------------------------------end--------------------------------
		else { // ��ͨҽ��
			SelSql = "SELECT * FROM ODI_ORDER WHERE CASE_NO='" + caseNo
					+ "' AND ORDER_NO='" + orderNo + "' AND ORDER_SEQ='"
					+ orderSeq + "'";

		}
		// �õ��ò������и�ִ��չ���Ĵ���
		result = new TParm(TJDODBTool.getInstance().select(SelSql));

		return result;
	}

	/**
	 * ����ҽ���Ƿ����
	 * 
	 * @param caseNo
	 *            String
	 * @param orderNo
	 *            String
	 * @param orderSeq
	 *            String
	 * @param startDttm
	 *            String
	 * @return boolean
	 */
	private boolean checkIsExistDspnd(String caseNo, String orderNo,
			String orderSeq, String orderDate, String orderDateTime) {
		String checkSql = "SELECT COUNT(OPT_USER) AS EXIST FROM ODI_DSPND WHERE CASE_NO='"
				+ caseNo
				+ "' AND ORDER_NO='"
				+ orderNo
				+ "' AND ORDER_SEQ='"
				+ orderSeq
				+ "' AND ORDER_DATE='"
				+ orderDate
				+ "' AND ORDER_DATETIME='" + orderDateTime + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(checkSql));
		return TypeTool.getBoolean(result.getData("EXIST", 0));
	}

	// ��ʿ�����¼
	private boolean checkIsExistDspnm(String caseNo, String orderNo,
			String orderSeq, String startDttm) {
		String checkSql = "SELECT COUNT(OPT_USER) AS EXIST FROM ODI_DSPNM WHERE CASE_NO='"
				+ caseNo
				+ "' AND ORDER_NO='"
				+ orderNo
				+ "' AND ORDER_SEQ='"
				+ orderSeq + "' AND START_DTTM='" + startDttm + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(checkSql));
		return TypeTool.getBoolean(result.getData("EXIST", 0));

	}

	// ��ʿȡ����¼(����չ����)
	private boolean checkIsNSDCExistDspnm(String caseNo, String orderNo,
			String orderSeq) {
		String checkSql = "SELECT COUNT(OPT_USER) AS EXIST FROM ODI_DSPNM WHERE CASE_NO='"
				+ caseNo
				+ "' AND ORDER_NO='"
				+ orderNo
				+ "' AND ORDER_SEQ='"
				+ orderSeq + "' AND DC_NS_CHECK_DATE IS NOT NULL";
		TParm result = new TParm(TJDODBTool.getInstance().select(checkSql));
		return TypeTool.getBoolean(result.getData("EXIST", 0));

	}

	/**
	 * ��ҽ����ORDER_CODE����ҩ����
	 * 
	 * @param orderCode
	 *            String
	 * @param doseageQty
	 *            double
	 * @return double
	 */
	private TParm getPrice(String caseNo, String orderCode, double doseageQty,
			String serviceLevel) {
		return BIL.getPriceForODI(caseNo, orderCode, doseageQty, serviceLevel);
	}

	/**
	 * ��ѯδִ��ҽ��
	 * 
	 * @param caseNo
	 * @param orderNo
	 * @param orderSeq
	 * @return
	 */
	public TParm queryUnExecOrder(String caseNo, String orderNo, String orderSeq) {// add
																					// by
																					// wanglong
																					// 20130710
		String sql = "SELECT COUNT(*) NUM FROM ODI_DSPNM "
				+ " WHERE CASE_NO = '#' AND ORDER_NO = '#' "
				+ " AND ORDER_SEQ = #  AND NS_EXEC_CODE IS NULL AND DC_NS_CHECK_DATE IS NULL";
		sql = sql.replaceFirst("#", caseNo);
		sql = sql.replaceFirst("#", orderNo);
		sql = sql.replaceFirst("#", orderSeq);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

    /**
     * ����ҽ�������������
     * @param parm
     * @param connection
     * @return
     */
    public TParm onCheckOPOrder(TParm parm, TConnection connection) {// wanglong add 20140707
        TParm result = new TParm();
        // ǰ̨��������
        TParm dataParm = parm.getParm("dataParm");
        String optUser = (String) parm.getData("OPT_USER");
        Timestamp optDate = (Timestamp) parm.getData("OPT_DATE");
        String optTerm = (String) parm.getData("OPT_TERM");
        String takeMedOrg = "";
        // shibl 20121022 modify ����caseNo����ʿ��˵�ҽ�����վ���ŷ���
        Map pat = InwOrderExecTool.getInstance().groupByPatParm(dataParm);
        Iterator it = pat.values().iterator();
        while (it.hasNext()) {
            TParm patParm = (TParm) it.next();
            for (int i = 0; i < patParm.getCount(); i++) {
                // ֧�ֶ������
                // ѭ��1--ҽ����ѭ����Ҫ��˵����� PS:��Ϊ����ҽ����ʱ�򷵻���ϸ��
                // ����ǰ̨��CASE_NO,ORDER_NO,ORDER_SEQ�ҳ�һ��ҽ��������SETMAIN_FLG�����Ƿ�Ϊ����ҽ��
                TParm orderParm =
                        getAnOrder(patParm.getValue("CASE_NO", i), patParm.getValue("ORDER_NO", i),
                                   patParm.getValue("ORDER_SEQ", i),
                                   patParm.getValue("SETMAIN_FLG", i),
                                   patParm.getValue("ORDERSETGROUP_NO", i));
                // ȡҩע��
                takeMedOrg = patParm.getValue("TAKEMED_ORG", i);
                // ��¼��ǰUI��ʾ�����ʱ��--���ڻ��ֶ��޸ĸ�ʱ��
                Timestamp checkDate = patParm.getTimestamp("CHECK_DATETIME", i);
                // ѭ��ÿ��������ÿһ�����ڴ������ݣ�����UDD�ӿ�������Ҫչ�������ݲ���ODI_DSPND ODI_DSPNM
                int orderCount = orderParm.getCount();
                if (orderCount <= 0) continue;
                TParm parmCase = new TParm();
                parmCase.setData("CASE_NO", patParm.getValue("CASE_NO"));
                parmCase = ADMInpTool.getInstance().selectall(parmCase);
                String serviceLevel = parmCase.getValue("SERVICE_LEVEL", 0);
                // ѭ��2--����ҽ��/��ͨҽ����(��M��1:1)��ѭ������ÿһ��ҽ����UDD�ӿڣ�����Ӧ��չ��������
                for (int row = 0; row < orderCount; row++) {
                    // ���ݿ��е�һ������--orderPram
                    // ��¼��ҽ����RX_KIND��UD-���ڣ�ST-��ʱ������UDD�ӿڴ���󷵻أ�/��DS-��Ժ��ҩ��ֱ��1��1��1չ����
                    String orderRxKind = orderParm.getValue("RX_KIND", row);
                    // PS����֧����(1)UD/ST (2)DS (3)IG
                    if ("ST".equals(orderRxKind)) {
                        // orderToUddParm��¼��UDD�ӿڵ�����Ҫ�Ĳ���
                        TParm orderRow = new TParm();
                        orderRow = orderParm.getRow(row); // һ��ODI_ORDER������
                        // System.out.println("==orderToUddParm111111=="
                        // + orderToUddParm);
                        // ����UDD�Ľӿڵõ���Ӧ�Ĳ���:�����ʱ��û���룬������ʱ������
                        String effDate =
                                StringTool.getString((Timestamp) orderRow.getData("EFF_DATE"),
                                                     "yyyyMMddHHmm");
                        Timestamp sysDate = TJDODBTool.getInstance().getDBTime();
                        // ����UDD�ӿڵõ���һ����ҩʱ��ʹε�
                        List dispenseDttm = TotQtyTool.getInstance().getNextDispenseDttm(checkDate);
                        // ���û����һ��ʱ���ͼ���ѭ��
                        if (dispenseDttm.size() == 0) {
                            System.out
                                    .println(" WARNING��There is not the 'next DispenseDttm' here ");
                            continue;
                        }
                        // �ж���ҩ�����Ǵ���-----false
                        String cat1Type = (String) orderRow.getData("CAT1_TYPE");
                        // �Ƿ�Ϊ�����ֻ�ҩ�����λ
                        boolean isOperation =
                                "TRT".equalsIgnoreCase(cat1Type)
                                        || "PLN".equalsIgnoreCase(cat1Type)
                                        || "OTH".equalsIgnoreCase(cat1Type);
                        // dataFromUdd�������մ�UDD��������������Ҫչ����
                        List dataFromUdd = new ArrayList();
                        // ����ֵ��������[(1)list--��size����D��ò����������(2)Map--����Ҫ������]--PS:����Ǵ��ò���3:effDate��ǰ8λ+0000
                        // ����4:effDate��ǰ8λ+235959
                        if (!isOperation) { // ҩƷ
                            dataFromUdd =
                                    TotQtyTool
                                            .getInstance()
                                            .getOdiStQty(effDate, // EFF_DATE
                                                         StringTool.getString((Timestamp) orderRow
                                                                                      .getData("DC_DATE"),
                                                                              "yyyyMMddHHmm"), // DC_DATE
                                                         dispenseDttm.get(0).toString(), // ��һ�ΰ�ҩʱ��START_DTTM
                                                         dispenseDttm.get(1).toString(), // ��һ�ΰ�ҩʱ��END_DTTM
                                                         orderRow, serviceLevel); // ��������
                        } else { // ����
                            dataFromUdd =
                                    TotQtyTool
                                            .getInstance()
                                            .getOdiTrtStQty(orderRow,
                                                            StringTool.getString((Timestamp) orderRow
                                                                                         .getData("EFF_DATE"),
                                                                                 "yyyyMMddHHmm"),
                                                            serviceLevel);
                        }
                        // �õ���Ҫ�嵽���е����ݣ�(PS:order���е����ݶ���ODI�в���)
                        // ORDER���LASTDSPN_QTY ORDER_LASTDSPN_QTY
                        // ORDER���ACUMDSPN_QTY ORDER_ACUMDSPN_QTY
                        // ORDER���ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
                        // M/ORDER���dispenseQty M_DISPENSE_QTY
                        // M/ORDER���dispenseUnit M_DISPENSE_UNIT
                        // M/ORDER���dosageQty M_DOSAGE_QTY
                        // M/ORDER���dosageUnit M_DOSAGE_UNIT
                        // �õ����ص�UDD���ص�ֵ
                        Map otherData = (Map) dataFromUdd.get(1);
                        double OlastDspnQty =
                                (Double) otherData.get("ORDER_LASTDSPN_QTY") == null ? 0.0
                                        : (Double) otherData.get("ORDER_LASTDSPN_QTY");
                        double OacumDspnQty =
                                (Double) otherData.get("ORDER_ACUMDSPN_QTY") == null ? 0.0
                                        : (Double) otherData.get("ORDER_ACUMDSPN_QTY");
                        double OacumMediQty =
                                (Double) otherData.get("ORDER_ACUMMEDI_QTY") == null ? 0.0
                                        : (Double) otherData.get("ORDER_ACUMMEDI_QTY");
                        double MdispenseQty =
                                (Double) otherData.get("M_DISPENSE_QTY") == null ? 0.0
                                        : (Double) otherData.get("M_DISPENSE_QTY");
                        String MdispenseUnit = (String) otherData.get("M_DISPENSE_UNIT");
                        double MdosageQty =
                                (Double) otherData.get("M_DOSAGE_QTY") == null ? 0.0
                                        : (Double) otherData.get("M_DOSAGE_QTY");
                        String MdosageUnit = (String) otherData.get("M_DOSAGE_UNIT");
                        // �Ƿ���������ҽ��(��DC) SHIBL MODIFY 20120424
                        if (orderRow.getValue("NS_CHECK_CODE").equals("")) {
                            // ����ODI_ORDER
                            TParm finalDataToOrder = new TParm();
                            String caseNoTemp = (String) orderRow.getData("CASE_NO");
                            String orderNoTemp = (String) orderRow.getData("ORDER_NO");
                            int orderSeqTemp = Integer.parseInt(orderRow.getData("ORDER_SEQ") + "");
                            // ODI_ORDER���е�LAST_DSPN_DATE
                            Timestamp lastDspnDateForOrder = optDate;
                            // ����ODI_ORDER���ֶ�LASTDSPN_QTY LAST_DSPN_DATE
                            finalDataToOrder =
                                    getDataOrder(orderRow, caseNoTemp, orderNoTemp, orderSeqTemp,
                                                 lastDspnDateForOrder, OlastDspnQty, OacumDspnQty,
                                                 OacumMediQty, MdispenseQty, MdispenseUnit,
                                                 MdosageQty, MdosageUnit, optUser, optDate,
                                                 optTerm, checkDate, takeMedOrg);
                            result =
                                    InwForOdiTool.getInstance().updateOdiOrder(finalDataToOrder,
                                                                               connection);
                            if (result.getErrCode() < 0) {
                                err("ERR:" + result.getErrCode() + result.getErrText()
                                        + result.getErrName());
                                return result;
                            }
                            if (result.getInt("RETURN") == 0) {// ���û�и��µ�����
                                result.setErrCode(-100);
                                result.setErrText("���������" + orderRow.getValue("ORDER_DESC")
                                        + "�Ѳ�����,�����²�ѯ");
                                return result;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * ����ҽ����ȡ�����������
     * @param parm
     * @param connection
     * @return
     */
    public TParm onUndoCheckOPOrder(TParm parm, TConnection connection) { // wanglong add 20140707
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm undoData = parm.getRow(i); // ǰ̨��������
            // ȡ����˵�ʱ��ֻ�����˱��
            undoData.setData("NS_CHECK_DATE", new TNull(Timestamp.class));
            undoData.setData("NS_CHECK_CODE", new TNull(String.class));
            result = InwForOdiTool.getInstance().updateOdiOrderForUndoCk(undoData, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
                return result;
            }
        }
        return result;
    }
    
    /**
     * ����ҽ�����Ʒѷ���
     * @param orderParm
     * @param connection
     * @return
     */
    public TParm onFee(TParm orderParm, TConnection connection) { // wanglong add 20140707
        TParm forIBSParm1 = new TParm();
        String SelSql =
                "SELECT * FROM ADM_INP WHERE CASE_NO='#'".replaceFirst("#", orderParm
                        .getValue("CASE_NO", 0));
        TParm ctzParm = new TParm(TJDODBTool.getInstance().select(SelSql));
        forIBSParm1.setData("CTZ1_CODE", ctzParm.getData("CTZ1_CODE", 0));
        forIBSParm1.setData("CTZ2_CODE", ctzParm.getData("CTZ2_CODE", 0));
        forIBSParm1.setData("CTZ3_CODE", ctzParm.getData("CTZ3_CODE", 0));
        forIBSParm1.setData("BED_NO", ctzParm.getData("BED_NO", 0));
        forIBSParm1.setData("FLG", orderParm.getData("FLG"));
        forIBSParm1.setData("M", orderParm.getData());
        TParm resultFromIBS = IBSTool.getInstance().getIBSOrderData(forIBSParm1);
        resultFromIBS.setData("EXE_DEPT_CODE", 0, orderParm.getData("EXE_DEPT_CODE", 0));
        resultFromIBS.setData("BED_NO", 0, forIBSParm1.getData("BED_NO"));
        TParm forIBSParm2 = new TParm();
        forIBSParm2.setData("DATA_TYPE", orderParm.getValue("DATA_TYPE")); // �ķѼ�¼���ñ��5
        forIBSParm2.setData("M", resultFromIBS.getData());
        forIBSParm2.setData("FLG", orderParm.getData("FLG"));
        TParm result = IBSTool.getInstance().insertIBSOrder(forIBSParm2, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * У�鲡������Ծ�����ʱҽ����ȡҩ����
     * @param parm
     * @return
     * @author wangbin 2015/1/22
     */
	public TParm getIndQty(TParm parm) {
		String stationCode = parm.getValue("STATION_CODE");
		String regionCode = parm.getValue("REGION_CODE");
		String orderCode = parm.getValue("ORDER_CODE");

		// ���췵�ؽ��
		TParm result = new TParm();
		result.setData("STATION_CODE", stationCode);
		result.setData("REGION_CODE", regionCode);
		TParm searchParm = new TParm();
		StringBuilder sbSql = new StringBuilder();

		// ��ҩƷ��Ӧ�Ŀ����
		sbSql = new StringBuilder();
		sbSql.append("SELECT C.ORDER_CODE, SUM (C.TOT_QTY) QTY FROM (");
		sbSql.append(" SELECT A.ORDER_CODE, SUM (A.STOCK_QTY) AS TOT_QTY");
		sbSql.append(" FROM IND_STOCK A");
		sbSql.append(" WHERE A.ORG_CODE = '");
		sbSql.append(stationCode);
		sbSql.append("' AND A.ORDER_CODE = '");
		sbSql.append(orderCode);
		sbSql.append("' GROUP BY A.ORDER_CODE");
		sbSql.append(" UNION ALL ");
		sbSql.append(" SELECT B.ORDER_CODE, COUNT (B.TOXIC_ID) AS TOT_QTY");
		sbSql.append(" FROM IND_CABINET A, IND_CONTAINERD B");
		sbSql.append(" WHERE B.CABINET_ID = A.CABINET_ID ");
		sbSql.append("AND A.ORG_CODE = '");
		sbSql.append(stationCode);
		sbSql.append("' AND B.ORDER_CODE = '");
		sbSql.append(orderCode);
		sbSql.append("' GROUP BY B.ORDER_CODE");
		sbSql.append(") C GROUP BY C.ORDER_CODE");

		searchParm = new TParm(TJDODBTool.getInstance()
				.select(sbSql.toString()));
		
		if (searchParm.getErrCode() < 0) {
//			err("ERR:" + searchParm.getErrCode() + " "
//					+ searchParm.getErrText());
			result.setErrCode(searchParm.getErrCode());
			result.setErrText(searchParm.getErrText());
			return result;
		}

		result.setData("QTY", searchParm.getDouble("QTY", 0));
		result.setData("ORDER_CODE", orderCode);

		return result;
	}
}
