package jdo.opd;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.bil.BIL;
import jdo.pha.PhaBaseTool;
import jdo.pha.PhaTransUnitTool;
import jdo.sys.SYSPhaFreqTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import jdo.sys.SystemTool;

/**
 * 
 * <p>
 * Title: ��������tool
 * 
 * <p>
 * Description: ��������tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * 
 * @author ehui 20081005
 * @version 1.0
 */
public class TotQtyTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static TotQtyTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return TotQtyTool
	 */
	public static TotQtyTool getInstance() {
		if (instanceObject == null)
			instanceObject = new TotQtyTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public TotQtyTool() {
		setModuleName("opd\\OPDMainModule.x");
		onInit();
	}

	/**
	 * ��ʱҩƷ�������ǲ������case_no�����order_code����Ժ���� (CASE_NO is not null Or CASE_NO
	 * <>'') "+//�մ������� " And BED_OCCU_FLG='N' "+
	 */
	private final static String GETOLD_QTY = " SELECT SUM(A.DISPENSE_QTY) AS DISPENSE_QTY ,SUM(A.MEDI_QTY) AS MEDI_QTY"
			+ " FROM ODI_ORDER A "
			+ "	WHERE A.RX_KIND='ST' AND A.NS_CHECK_CODE IS NOT NULL ";
	private final static String GET_TIMES = " SELECT FREQ_TIMES FROM SYS_PHAFREQ ";

	/**
	 * ��������
	 * 
	 * @alias ��������
	 * @param TParm
	 *            parm ORDER ����������������
	 * @param TParm
	 *            parm OPERATOR ������Ա��
	 * @result TParm result QTY ���� ����˵����<br>
	 *         1. check�������� <br>
	 *         2. ����ҩƷ���� <br>
	 *         3. �ش����� <br>
	 */
	public TParm getTotQty(TParm parm) {
		TParm result = new TParm();
		// 1. check��������
		if (parm == null)
			return result;
		// �ش���ֵ
		result.setData("QTY", 0.0);
		// ��ҩ
		if ("G".equals(parm.getValue("PHA_TYPE"))
				|| !"PHA".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))) {
			double tot = 0;
			if ("G".equals(parm.getValue("PHA_TYPE"))) {
				tot = parm.getDouble("MEDI_QTY") * parm.getInt("TAKE_DAYS");
			} else {
				TParm trn = this.setFreq(parm.getValue("FREQ_CODE"));
				if (TypeTool.getBoolean(trn.getData("NOCOMPUTE_FLG"))) {
					result.setData("QTY", parm.getDouble("DOSAGE_QTY"));
					return result;
				}
				int times = trn.getInt("FREQ_TIMES");
				tot = parm.getDouble("MEDI_QTY") * parm.getInt("TAKE_DAYS")
						* times;
			}
			result.setData("QTY", StringTool.round(tot, 1));
			// 
			result.setData("DOSAGE_UNIT", parm.getValue("DOSAGE_UNIT"));
			result.setData("DISPENSE_UNIT", parm.getValue("DISPENSE_UNIT"));
			return result;
		}

		// System.out.println("-------------ORDER_CODE-----------------");
		// ԭ������ ITotQtyBaseData ���ݰ�����pha_base�е����ԣ�����order��û�и�����Ⱥ��Ӧ�����ѯһ��pha_base
		TParm phabase = PhaBaseTool.getInstance().selectByOrder(
				parm.getValue("ORDER_CODE"));
		if (phabase.getErrCode() < 0) {
			err("ERR:" + phabase.getErrCode() + phabase.getErrText()
					+ phabase.getErrName());
			return result;
		}
		// System.out.println("-------------MEDI_QTY-----------------");
		// 2. ����ҩƷ����
		// if (parm.getDouble("MEDI_QTY") > 0.0) {
		result = getPHAtotQty(parm, phabase);
		if (result.getErrCode() < 0)
			return result;
		// }
		// System.out.println("-------------GIVEBOX_FLG-----------------");

		// �����ҩ��ѡ�����ҩ������
		if ("Y".equalsIgnoreCase(parm.getValue("GIVEBOX_FLG"))) {
			TParm qtyParm = getTotQtyForStockUnit(parm, TCM_Transform
					.getDouble(result.getData("QTY")), phabase);
			// 3. �ش�����ҩ��ҩ������ 2��(QTY_FOR_STOCK_UNIT) = 20Ƭ(TOT_QTY)
			result.setData("TOT_QTY", StringTool.round(TCM_Transform
					.getDouble(qtyParm.getData("TOT_QTY")), 2));
			result.setData("QTY_FOR_STOCK_UNIT", StringTool.round(TCM_Transform
					.getDouble(qtyParm.getData("QTY_FOR_STOCK_UNIT")), 2));
			result.setData("DOSAGE_UNIT", qtyParm.getData("DOSAGE_UNIT"));
			result.setData("STOCK_UNIT", qtyParm.getData("STOCK_UNIT"));
		} else {
			// 3. �ش�����
			result.setData("QTY", StringTool.round(TCM_Transform
					.getDouble(result.getData("QTY")), 2));
		}
		return result;
	}

	/**
	 * * �����浥λ��ҩʱ������
	 * 
	 * @param stockUnit
	 *            String
	 * @param totQty
	 *            double ����(��ҩ��λ)
	 * @return TParm
	 */
	public TParm getTotQtyForStockUnit(TParm parm, double totQty, TParm phaBase) {
		TParm parmtrans = this.setTrn(parm);
		double totQtyForStockUnit = 0; // ��浥λ����
		double tot = 0; // ����浥λ��ҩʱ�ķ�ҩ��λ����
		// System.out.println("he yao totqty===" + totQty);
		// System.out.println("he yao dosageQty==="
		// + StringTool.getDouble(parmtrans.getValue("DOSAGE_QTY")));
		// ��ҩ��λ����/��浥λת���� = ��浥λ��ҩ����
		totQtyForStockUnit = Math.ceil(totQty
				/ StringTool.getDouble(parmtrans.getValue("DOSAGE_QTY")));
		// System.out
		// .println("totQtyForStockUnit===========" + totQtyForStockUnit);
		tot = totQtyForStockUnit
				* StringTool.getDouble(parmtrans.getValue("DOSAGE_QTY"));
		// System.out.println("tot============" + tot);
		// System.out.println("totQty:"+totQty);
		TParm result = new TParm();
		result.setData("TOT_QTY", tot);
		result.setData("QTY_FOR_STOCK_UNIT", totQtyForStockUnit);
		result.setData("DOSAGE_UNIT", parmtrans.getValue("DOSAGE_UNIT"));
		// System.out.println("getTotQtyForStockUnit=========STOCK_UNIT"
		// + parmtrans.getValue("STOCK_UNIT"));
		result.setData("STOCK_UNIT", parmtrans.getValue("STOCK_UNIT"));
		return result;

	}

	/**
	 * ��������
	 * 
	 * @alias ��������
	 * @param TParm
	 *            parm ORDER ����������������
	 * @param TParm
	 *            parm OPERATOR ������Ա��
	 * @result TParm result QTY ���� ����˵����<br>
	 *         1. check�������� <br>
	 *         2. ����ҩƷ���� <br>
	 *         3. �ش����� <br>
	 */
	public TParm getTakeQty(TParm parm) {
		TParm result = new TParm();
		// 1. check��������
		if (parm == null)
			return result;
		TParm qty = new TParm(); // �ش���ֵ
		qty.setData("QTY", 0.0);
		// ��ҩ
		if ("G".equals(parm.getValue("PHA_TYPE"))) {
			double takeQty = parm.getDouble("MEDI_QTY")
					/ parm.getInt("TAKE_DAYS");
			qty.setData("QTY", StringTool.round(takeQty, 1));
			return qty;
		}

		// ��ҩƷ
		if (!"PHA".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))) {
			double tot = 0;
			TParm trn = this.setFreq(parm.getValue("FREQ_CODE"));
			if (TypeTool.getBoolean(trn.getData("NOCOMPUTE_FLG"))) {
				result.setData("QTY", parm.getDouble("MEDI_QTY"));
				result.setData("SUM_QTY", parm.getDouble("DOSAGE_QTY"));
				return result;
			}
			int times = trn.getInt("FREQ_TIMES");
			tot = parm.getDouble("DOSAGE_QTY") / parm.getInt("TAKE_DAYS")
					/ times;
			result.setData("QTY", tot);
			result.setData("SUM_QTY", parm.getDouble("DOSAGE_QTY"));
			return result;
		}

		// 2. ����ҩƷ����
		if (parm.getDouble("DOSAGE_QTY") > 0.0) {
			// System.out.println("����ҩƷ����---" + parm);
			qty = getPHAtakeQty(parm);
			if (qty.getErrCode() < 0)
				return qty;
		}
		// 3. �ش�����
		result.setData("QTY", StringTool.round(TCM_Transform.getDouble(qty
				.getData("QTY")), 3));
		if (qty.existData("SUM_QTY")) {
			result.setData("SUM_QTY", StringTool.round(TCM_Transform
					.getDouble(qty.getData("SUM_QTY")), 2));
		}
		// System.out.println("qty=" + qty);
		// System.out.println("before" + TCM_Transform.getDouble(qty
		// .getData("QTY")));
		// System.out.println("after" +
		// StringTool.round(TCM_Transform.getDouble(qty
		// .getData("QTY")), 3));
		return result;
	}

	/**
	 * ����ҩƷ����
	 * 
	 * @alias ����ҩƷ����
	 * @param Order
	 *            parm ���������������� ����˵����<br>
	 *            1. �ж��Ƿ�Ϊ����Ͷҩ,��Ϊ����Ͷҩ,������=����֮���� <br>
	 *            2. �趨����������,��ȡ�ô���,������=0,��ش�����=0 <br>
	 *            3. �ж��Ƿ��뿪ҩ��λ<>���뷢ҩ��λ,�����뿪ҩ��λ<>���뷢ҩ��λ,��������� (�����֮���� =
	 *            TiMath.round(���� / ת����ʼ����,4) * ת����������) <br>
	 *            4.
	 *            �ж��Ƿ�Ϊ������������������ҩ�Ҽ����֮����>����֮��������ش�����Ϊ����֮����/����������ش�����Ϊ�����֮����/����
	 *            5. �����뿪ҩ��λ=���뷢ҩ��λ,��ش�����Ϊ����/����
	 */
	public TParm getPHAtakeQty(TParm parm) {
		TParm result = new TParm();
		double qty = 0.0; // �ش���ֵ
		double totQty = parm.getDouble("DOSAGE_QTY"); // ����
		// ԭ������ ITotQtyBaseData ���ݰ�����pha_base�е����ԣ�����order��û�и�����Ⱥ��Ӧ�����ѯһ��pha_base
		TParm phabase = PhaBaseTool.getInstance().selectByOrder(
				parm.getValue("ORDER_CODE"));
		if (phabase.getErrCode() < 0) {
			err("ERR:" + phabase.getErrCode() + phabase.getErrText()
					+ phabase.getErrName());
			return result;
		}
		// 0.���ҩ���򲻼�������
		String dips = parm.getValue("DISPENSE_UNIT");
		String medi = parm.getValue("MEDI_UNIT");
		if (!isNullString(dips)
				&& dips.equalsIgnoreCase(phabase.getValue("STOCK_UNIT"))) {
			return result;
		}
		// 1.�ж��Ƿ�Ϊ����Ͷҩ,��Ϊ����Ͷҩ,��ش�����=����֮����
		if ("Y".equalsIgnoreCase(phabase.getValue("DSPNSTOTDOSE_FLG"))) {
			qty = totQty; // ����
			result.setData("QTY", qty);
			return result;
		}

		// 2. �趨����������,��ȡ�ô���,������=0,��ش�����=0//end if(����Ͷҩע��ΪY������Ϊ0)
		TParm vFreq = setFreq(parm.getValue("FREQ_CODE")); // �趨����������
		if (vFreq.getErrCode() < 0) {
			return vFreq;
		}
		// ����������ʱֱ�ӷ�������
		if ("Y".equalsIgnoreCase(vFreq.getValue("NOCOMPUTE_FLG"))) {
			// �ж��ǲ��Ǻз�ҩ
			if ("Y".equals(parm.getValue("GIVEBOX_FLG"))) {
				TParm vTrn = setTrn(parm); // ȡ�ÿ�ҩ�Է�ҩת��������
				if (vTrn.getErrCode() < 0) {
					return vTrn;
				}
				// System.out.println("qty---" + parm.getDouble("MEDI_QTY"));
				// System.out.println("totQty---" + totQty);
				// System.out.println("totQty111---" +
				// TCM_Transform.getDouble(
				// vTrn.getValue("MEDI_QTY")));
				// System.out.println("totQty222---" +
				// TCM_Transform.getDouble(
				// vTrn.getValue("DOSAGE_QTY")));
				result.setData("QTY", parm.getDouble("MEDI_QTY"));
				result.setData("SUM_QTY", totQty
						* TCM_Transform.getDouble(vTrn.getValue("DOSAGE_QTY")));
				return result;
			}
			if (parm.getDouble("MEDI_QTY") != 0) {
				qty = parm.getDouble("MEDI_QTY");
				result.setData("QTY", qty);
				return result;
			}
		}
		TParm vTrn = setTrn(parm); // ȡ�ÿ�ҩ�Է�ҩת��������
		if (vTrn.getErrCode() < 0) {
			return vTrn;
		}
		// 3. �ж��Ƿ��뿪ҩ��λ<>���뷢ҩ��λ,�����뿪ҩ��λ<>���뷢ҩ��λ,��������� (�����֮���� = TiMath.round(����
		// / ת����ʼ����,4) * ת����������)
		double startqty = TCM_Transform.getDouble(vTrn.getValue("MEDI_QTY"));
		int iFactor = getFactor(vFreq, parm); // ����
		if (iFactor == 0) {
			//=======shibl 20141201modify=====��������Ϊ0ʱ�Ժз�ҩ�������ҩ��������=====
			if ("Y".equals(parm.getValue("GIVEBOX_FLG"))) {
				double dosageqty = TCM_Transform.getDouble(vTrn
						.getValue("DOSAGE_QTY"));
				result.setData("SUM_QTY", totQty * dosageqty);
			}
			//==========================================================================
			qty = 0.0; // ����
			result.setData("QTY", qty);
			return result;
		}

		// �з�ҩ
		if ("Y".equals(parm.getValue("GIVEBOX_FLG"))) {
			double dosageqty = TCM_Transform.getDouble(vTrn
					.getValue("DOSAGE_QTY"));
			double d_m_totqty = ((double) StringTool.round(totQty * dosageqty
					* startqty, 4)); // �����֮����
			qty = d_m_totqty / iFactor;
			// System.out.println("totQty---" + totQty);
			// System.out.println("dosageqty---" + dosageqty);
			// System.out.println("iFactor---" + iFactor);
			// System.out.println("QTY---" + qty);
			// System.out.println("SUM_QTY---" + totQty * dosageqty);
			result.setData("SUM_QTY", totQty * dosageqty);
			result.setData("QTY", qty);
			return result;
		}

		if (!isNullString(medi)
				&& !isNullString(dips)
				&& !parm.getValue("MEDI_UNIT").equals(
						parm.getValue("DISPENSE_UNIT"))) {

			double d_m_totqty = ((double) StringTool
					.round(totQty * startqty, 4)); // �����֮����
			// 4. �ж��Ƿ�Ϊ������������������ҩ�Ҽ����֮����>����֮��������ش�����Ϊ����֮����/����������ش�����Ϊ�����֮����/����
			qty = getTotQty(vFreq, d_m_totqty, (d_m_totqty > totQty), totQty)
					/ iFactor;
			//
			// if ( !"Y".equalsIgnoreCase(phabase.getValue("HALF_USE_FLG")) &&
			// qty % startqty != 0) {
			// result.setErr(-1,"��ҩƷ���ɲ���.");
			// return result;
			// }
			// 5. �����뿪ҩ��λ=���뷢ҩ��λ,��ش�����Ϊ����/����
		} else {
			qty = totQty / iFactor; // ��������������
			// //����Ƿ�ɲ��� �磺���ɲ����Ҹ���������Ϳ�ʼ�������,���ȡ�಻Ϊ"0" ��ʾ
			// if ( !"Y".equalsIgnoreCase(phabase.getValue("HALF_USE_FLG")) &&
			// qty % endqty != 0) {
			// result.setErr(-1,"��ҩƷ���ɲ���.");
			// return result;
			// }
		}

		result.setData("QTY", qty);
		return result;
	}

	/**
	 * ����ҩƷ����
	 * 
	 * @alias ����ҩƷ����
	 * @param ITotQtyBaseData
	 *            totQtyBaseData ����������������
	 * @param String
	 *            hospArea Ժ�� ����˵����<br>
	 *            1. �趨���������� <br>
	 *            2. ȡ�ÿ�ҩ�Է�ҩת�������� <br>
	 *            3. ������������ע��ΪY,���趨����,����ȡ�ô��� <br>
	 *            4. ����������ʹ��,��������� (�����֮���� = Math.ceil(TiMath.round(���� /
	 *            ת����ʼ����),4) * ת���������� * ����) <br>
	 *            5. ��������ʹ�õ�������������,��������� (�����֮���� =
	 *            Math.ceil(TiMath.round(TiMath.round(���� * ����,4) / ת����ʼ����,4)) *
	 *            ת����������) <br>
	 *            6. ��������ʹ���ҿ��������������뿪ҩ��λ<>���뷢ҩ��λ,��������� (�����֮���� = TiMath.round(����
	 *            * ����,4) / ת����ʼ����, * ת����������) <br>
	 *            7. ��������ʹ���ҿ����������Ҵ��뿪ҩ��λ==���뷢ҩ��λ,��������� (�����֮���� = ���� * ���� ) <br>
	 * 
	 */
	public TParm getPHAtotQty(TParm parm, TParm phabase) {

		double qty = 0.0; // �ش���ֵ
		double totQty = parm.getDouble("DOSAGE_QTY"); // ����
		// System.out.println("getPHAtotQty=============" + totQty);
		double takeQty = parm.getDouble("MEDI_QTY"); // ����
		TParm result = new TParm();
		result.setData("QTY", 0.0);
		// System.out.println("phabase.getValue(========="
		// + phabase.getValue("FREQ_CODE", 0));
		// 1. �趨����������
		TParm vFreq = setFreq(parm.getValue("FREQ_CODE"));
		// System.out.println("vFreq==" + vFreq);

		if (vFreq.getErrCode() < 0)
			return result;
		// if(TypeTool.getBoolean(vFreq.getData("NOCOMPUTE_FLG"))){
		// return result;
		// }
		// 2. ȡ�ÿ�ҩ�Է�ҩת��������
		TParm vTrn = setTrn(parm);
		// System.out.println("vTrn====" + vTrn);
		if (vTrn.getErrCode() < 0)
			return result;

		int iFactor = 0; // ����
		// 3. ����������ע��ΪY,���趨����,����ȡ�ô���
		if (TypeTool.getBoolean(vFreq.getData("NOCOMPUTE_FLG"))) {
			// System.out.println("vFreq=" + vFreq);
			iFactor = TCM_Transform.getInt(vFreq.getData("FREQ_TIMES", 0)); // ����
			if (totQty != 0) {
				qty = totQty; // ����
				result.setData("QTY", qty);
				result.setData("STOCK_UNIT", vTrn.getData("STOCK_UNIT"));
				result.setData("DOSAGE_UNIT", vTrn.getData("DOSAGE_UNIT"));
				// System.out.println(qty + "=========NOCOMPUTE");
				return result;
			}
		} else {
			// System.out.println(vFreq.getValue("TUE_FLG", 0));
			iFactor = getFactor(vFreq, parm);
		}

		// ���ٴ���Ϊ1
		iFactor = (iFactor == 0) ? 1 : iFactor;
		double d_m_totqty = 0.0; // �����֮����

		// 4. ����������ʹ��,��������� (�����֮���� = Math.ceil(TiMath.round(���� / ת����ʼ����),4) *
		// ת���������� * ����)
		// System.out.println("takeQty" + takeQty);
		// System.out.println("iFactor" + iFactor);
		// System.out.println("takeQty * iFactor" + takeQty * iFactor);
		// System.out.println("START_QTY" + vTrn.getData("MEDI_QTY"));
		double lTot_QtyTmp = StringTool.round(takeQty * iFactor, 4);
		double startqty = TCM_Transform.getDouble(vTrn.getData("MEDI_QTY"));
		String oddflg = TCM_Transform.getString(phabase.getData("ODD_FLG", 0));
		// System.out.println("--zhangyong--" + phabase);
		if ("Y".equalsIgnoreCase(TCM_Transform.getString(phabase.getValue(
				"REUSE_FLG", 0)))) {
			// System.out.println("in 4");
			// ����(ǿ�ƽ�λ)
			d_m_totqty = ((double) Math.ceil(StringTool.round(
					(double) (takeQty / startqty), 4)))
					* iFactor;
			qty = getTotQty(vFreq, d_m_totqty, (d_m_totqty < totQty), totQty);
			result.setData("QTY", qty);
			result.setData("STOCK_UNIT", vTrn.getData("STOCK_UNIT"));
			result.setData("DOSAGE_UNIT", vTrn.getData("DOSAGE_UNIT"));
			// System.out.println(qty + "4");
			return result;
		}

		// 5. ��������ʹ�õ�������������,��������� (�����֮���� =
		// Math.ceil(TiMath.round(TiMath.round(���� * ����,4) / ת����ʼ����,4)) * ת����������)
		if ("N".equalsIgnoreCase(oddflg)) {
			// System.out.println("in 5");
			d_m_totqty = Math.ceil(StringTool.round(
					(double) (lTot_QtyTmp / startqty), 4));
			qty = getTotQty(vFreq, d_m_totqty, (d_m_totqty < totQty), totQty);
			result.setData("QTY", qty);
			result.setData("STOCK_UNIT", vTrn.getData("STOCK_UNIT"));
			result.setData("DOSAGE_UNIT", vTrn.getData("DOSAGE_UNIT"));
			// System.out.println(qty + "5");
			return result;
		}

		// 6 If(�ɲ���ע��ʹ��=Y)
		if ("Y".equalsIgnoreCase(phabase.getValue("HALF_USE_FLG", 0))) {
			// System.out.println("in 6");
			d_m_totqty = StringTool.round((double) (takeQty / startqty), 4)
					* iFactor;
			qty = getTotQty(vFreq, d_m_totqty, (d_m_totqty > totQty), totQty);
			result.setData("QTY", qty);
			result.setData("STOCK_UNIT", vTrn.getData("STOCK_UNIT"));
			result.setData("DOSAGE_UNIT", vTrn.getData("DOSAGE_UNIT"));
			// System.out.println(qty + "6");
			return result;
		}

		// 7.
		try {
			d_m_totqty = Math.ceil(StringTool.round(
					(double) (takeQty / startqty), 4)
					* iFactor);
		} catch (Exception e) {
			// System.out.println("takeQty="+takeQty);
			// System.out.println("startqty="+startqty);
		}

		qty = getTotQty(vFreq, d_m_totqty, (d_m_totqty < totQty), totQty);
		// System.out.println(qty + "last");
		result.setData("QTY", qty);
		result.setData("STOCK_UNIT", vTrn.getData("STOCK_UNIT"));
		result.setData("DOSAGE_UNIT", vTrn.getData("DOSAGE_UNIT"));
		return result;
	}

	/**
	 * �жϻش�����
	 * 
	 * @alias �жϻش�����
	 * @param ActionParm
	 *            vFreq ����������
	 * @param double d_m_totqty �����֮����
	 * @param boolean check �жϼ����֮�����������Ĺ�ϵ
	 * @return double ���� ����˵����<br>
	 *         �ж��Ƿ�Ϊ������������������ҩ�Ҽ����֮����<����֮��������ش�����Ϊ����֮����������ش�����Ϊ�����֮����
	 */
	private double getTotQty(TParm vFreq, double d_m_totqty, boolean check,
			double totQty) {
		if (TCM_Transform.getString(vFreq.getData("NOCOMPUTE_FLG")).trim()
				.equals("Y")
				&& // ����������ע��
				!TCM_Transform.getString(vFreq.getData("STAT_FLG")).trim()
						.equals("Y") && // ��������ҩע��
				check) {
			return totQty;
		} else {
			return d_m_totqty;
		}
	}

	/**
	 * ȡ�ô���
	 * 
	 * @alias ȡ�ô���
	 * @param ActionParm
	 *            vFreq ����������
	 * @param ITotQtyBaseData
	 *            totQtyBaseData ����������������
	 * @param String
	 *            hospArea Ժ��
	 * @return int iFactor ���� ����˵����<br>
	 *         1. if(����>1) iFactor=(�շ�/����)*���� <br>
	 *         2. if(����==1) iFactor=�շ�*���� <br>
	 *         3. if(����==0) <br>
	 *         for(�շ�){ <br>
	 *         if((����һ && ��һ����) || (���ڶ� && �ܶ�����) || (������ && ��������) || (������ &&
	 *         ���ķ���) || <br>
	 *         (������ && �������) || (������ && ��������) || (������ && ���շ��� )){ <br>
	 *         iFactor=iFactor+����; <br>
	 * <br>
	 * <br>
	 */
	public int getFactor(TParm vFreq, TParm parm) {
		// System.out.println("in getFactor" + vFreq);
		// System.out.println("vFreq.getData(" + vFreq.getData("FREQ_TIMES",
		// 0));
		int iFactor;
		if (!isNullString(parm.getValue("ORDER_CAT1_CODE"))
				&& !"PHA".equalsIgnoreCase(parm.getValue("ORDER_CAT1_CODE"))) {
			TParm inParm = new TParm();
			inParm.setData("FREQ_CODE", parm.getValue("FREQ_CODE"));
			iFactor = SYSPhaFreqTool.getInstance().existSYSTRTFREQTIME(inParm);
			// System.out.println("���õ�Ƶ��" + iFactor);
		} else {
			iFactor = TCM_Transform.getInt(vFreq.getValue("FREQ_TIMES")); // ����
		}

		// System.out.println("iFactor======" + iFactor);

		int cycle = TCM_Transform.getInt(vFreq.getValue("CYCLE"));

		// System.out.println("cycle======" + cycle);

		int takedays = parm.getInt("TAKE_DAYS");
		// 1. if(����>1) iFactor=(�շ�/����)*����
		if (cycle > 1) {

			iFactor = ((int) Math.ceil((double) takedays / cycle)) * iFactor;
			return iFactor;
		}

		// 2. if(����==1) iFactor=�շ�*����
		if (cycle == 1) {

			iFactor = takedays * iFactor;
			// System.out.println("takedays *iFactor;" + iFactor);
			return iFactor;
		}
		iFactor = 0;
		// 3. if(����==0) iFactor=iFactor+����;
		if (cycle == 0) {

			for (int i = 0; i < takedays; i++) {
				// ȡ�����������ڼ�=Integer.parseInt(Date_Proc1.DayofWeek(Date_Proc1.AddDay(��������,i)));
				/**
				 * Integer.parseInt(Date_Proc1.DayofWeek(
				 * Date_Proc1.AddDay(totQtyBaseData
				 * .getOrderTime().substring(0,8),i)));
				 */
				// //System.out.println("parm.getTimestamp"+parm
				// .getTimestamp("ORDER_DATE"));
				int iDow = StringTool.getWeek(StringTool.rollDate(parm
						.getTimestamp("ORDER_DATE"), new Long(i)));

				/*
				 * if((����һ && ��һ����) || (���ڶ� && �ܶ�����) || (������ && ��������) || (������ &&
				 * ���ķ���) || (������ && �������) || (������ && ��������) || (������ && ���շ��� )){
				 */
				// System.out.println("MON_FLG" + vFreq.getData("MON_FLG"));
				// System.out.println(vFreq);

				if ((iDow == 1 && "Y".equalsIgnoreCase(vFreq
						.getValue("MON_FLG")))
						|| (iDow == 2 && "Y".equalsIgnoreCase(vFreq
								.getValue("TUE_FLG")))
						|| (iDow == 3 && "Y".equalsIgnoreCase(vFreq
								.getValue("WED_FLG")))
						|| (iDow == 4 && "Y".equalsIgnoreCase(vFreq
								.getValue("THUR_FLG")))
						|| (iDow == 5 && "Y".equalsIgnoreCase(vFreq
								.getValue("FRI_FLG")))
						|| (iDow == 6 && "Y".equalsIgnoreCase(vFreq
								.getValue("STA_FLG")))
						|| (iDow == 0 && "Y".equalsIgnoreCase(vFreq
								.getValue("SUN_FLG")))) {
					iFactor = iFactor
							+ TCM_Transform
									.getInt(vFreq.getValue("FREQ_TIMES")); // iFactor=iFactor+����;
				}
			} // end for
			return iFactor;
		}

		return iFactor;

	}

	/**
	 * �趨����������
	 * 
	 * @alias �趨����������
	 * @param ITotQtyBaseData
	 *            totQtyBaseData ����������������
	 * @return ActionParm result "CYCLE ����(��),FREQ_TIMES Ƶ�δ���,NOCOMPUTE_FLG
	 *         ��Call��������ע��,STAT_FLG ������ҩע��,MON_FLG ��һ����,TUE_FLG �ܶ�����,WED_FLG
	 *         ��������,THUR_FLG ���ķ���,FRI_FLG �������,STA_FLG ��������,SUN_FLG ���շ���"
	 */
	public TParm setFreq(String freqCode) {
		TParm result = new TParm();
		// result.setReturnData("CYCLE", 0); //����
		// result.setReturnData("FREQ_TIMES", 0); //����
		// result.setReturnData("NOCOMPUTE_FLG", "N"); //����������ע��(N-Ҫ����;Y-������)
		// result.setReturnData("STAT_FLG", "N"); //������ҩע��
		// result.setReturnData("MON_FLG", "N"); //����һ
		// result.setReturnData("TUE_FLG", "N"); //���ڶ�
		// result.setReturnData("WED_FLG", "N"); //������
		// result.setReturnData("THUR_FLG", "N"); //������
		// result.setReturnData("FRI_FLG", "N"); //������
		// result.setReturnData("STA_FLG", "N"); //������
		// result.setReturnData("SUN_FLG", "N"); //������
		// ����(��),����,����������ע��,������ҩע��,����
		// System.out.println("freq code============" + freqCode);
		TParm vFreq = SYSPhaFreqTool.getInstance().queryForAmount(freqCode);
		// System.out.println("vFreq:" + vFreq);
		if (vFreq.getErrCode() < 0) {
			err("ERR:" + vFreq.getErrCode() + vFreq.getErrText()
					+ vFreq.getErrName());
			result.setErrCode(-1);
			return result;
		}
		if (vFreq.getData("CYCLE", 0) != null
				&& !vFreq.getData("CYCLE", 0).equals("")) {
			// System.out.println("CYCLE"
			// + TCM_Transform.getInt(vFreq.getData("CYCLE", 0)));
			result.setData("CYCLE", TCM_Transform.getInt(vFreq.getData("CYCLE",
					0))); // ����
		}
		if (vFreq.getData("FREQ_TIMES", 0) != null
				&& !vFreq.getData("FREQ_TIMES", 0).equals("")) {
			result.setData("FREQ_TIMES", TCM_Transform.getInt(vFreq.getData(
					"FREQ_TIMES", 0))); // ����
		}
		if (vFreq.getData("NOCOMPUTE_FLG", 0) != null
				&& !vFreq.getData("NOCOMPUTE_FLG", 0).equals("")) {
			result.setData("NOCOMPUTE_FLG", vFreq.getData("NOCOMPUTE_FLG", 0)); // ����������ע��
		}
		if (vFreq.getData("STAT_FLG", 0) != null
				&& !vFreq.getData("STAT_FLG", 0).equals("")) {
			result.setData("STAT_FLG", vFreq.getData("STAT_FLG", 0)); // ������ҩע��
		}
		// ����
		if (vFreq.getData("MON_FLG", 0) != null
				&& !vFreq.getData("MON_FLG", 0).equals("")) {
			result.setData("MON_FLG", vFreq.getData("MON_FLG", 0));
		}
		if (vFreq.getData("TUE_FLG", 0) != null
				&& !vFreq.getData("TUE_FLG", 0).equals("")) {
			result.setData("TUE_FLG", vFreq.getData("TUE_FLG", 0));
		}
		if (vFreq.getData("WED_FLG", 0) != null
				&& !vFreq.getData("WED_FLG", 0).equals("")) {
			result.setData("WED_FLG", vFreq.getData("WED_FLG", 0));
		}
		if (vFreq.getData("THUR_FLG", 0) != null
				&& !vFreq.getData("THUR_FLG", 0).equals("")) {
			result.setData("THUR_FLG", vFreq.getData("THUR_FLG", 0));
		}
		if (vFreq.getData("FRI_FLG", 0) != null
				&& !vFreq.getData("FRI_FLG", 0).equals("")) {
			result.setData("FRI_FLG", vFreq.getData("FRI_FLG", 0));
		}
		if (vFreq.getData("STAT_FLG", 0) != null
				&& !vFreq.getData("STAT_FLG", 0).equals("")) {
			result.setData("STAT_FLG", vFreq.getData("STAT_FLG", 0));
		}
		if (vFreq.getData("SUN_FLG", 0) != null
				&& !vFreq.getData("SUN_FLG", 0).equals("")) {
			result.setData("SUN_FLG", vFreq.getData("SUN_FLG", 0));
		}

		return result;
	}

	/**
	 * �趨��ҩ�Է�ҩת��������
	 * 
	 * @alias �趨��ҩ�Է�ҩת��������
	 * @param ITotQtyBaseData
	 *            totQtyBaseData ����������������
	 * @param String
	 *            hospArea Ժ��
	 * @return TParm result "START_QTY ��ʼ���� ,END_QTY �������� ,END_UNIT ������λ"
	 */
	private TParm setTrn(TParm parm) {
		TParm result = new TParm();
		// result.setCommitData("START_QTY", 1); //��ʼ����
		// result.setCommitData("END_QTY", 1); //��������
		// result.setCommitData("END_UNIT", totQtyBaseData.getDispenseUnit());
		// //������λ
		TParm paramtrans = new TParm();
		paramtrans.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
		TParm vTrn = PhaTransUnitTool.getInstance().queryForAmount(paramtrans);
		if (vTrn.getErrCode() < 0)
			return result;

		if (vTrn.getData("DOSAGE_QTY", 0) != null
				&& !vTrn.getData("DOSAGE_QTY", 0).equals("")) {
			// System.out.println("DOSAGE_QTY======"
			// + TCM_Transform.getDouble(vTrn.getData("DOSAGE_QTY", 0)));
			result.setData("DOSAGE_QTY", TCM_Transform.getDouble(vTrn.getData(
					"DOSAGE_QTY", 0)));
		}
		if (vTrn.getData("DOSAGE_UNIT", 0) != null
				&& !vTrn.getData("DOSAGE_UNIT", 0).equals("")) {

			result.setData("DOSAGE_UNIT", vTrn.getData("DOSAGE_UNIT", 0));
		}

		if (vTrn.getData("MEDI_QTY", 0) != null
				&& !vTrn.getData("MEDI_QTY", 0).equals("")) {
			// System.out.println("MEDI_QTY======="
			// + TCM_Transform.getDouble(vTrn.getData("MEDI_QTY", 0)));
			result.setData("MEDI_QTY", TCM_Transform.getDouble(vTrn.getData(
					"MEDI_QTY", 0)));
		}

		if (vTrn.getData("MEDI_UNIT", 0) != null
				&& !vTrn.getData("MEDI_UNIT", 0).equals("")) {
			result.setData("MEDI_UNIT", vTrn.getData("MEDI_UNIT", 0));
		}
		// System.out.println("TotQtytool->================"
		// + vTrn.getData("STOCK_UNIT", 0));
		// System.out.println("TotQtytool->================"
		// + vTrn.getData("STOCK_UNIT"));
		if (vTrn.getData("STOCK_QTY", 0) != null
				&& !vTrn.getData("STOCK_QTY", 0).equals("")) {
			result.setData("STOCK_QTY", TCM_Transform.getDouble(vTrn.getData(
					"STOCK_QTY", 0)));
		}
		if (vTrn.getData("STOCK_UNIT", 0) != null
				&& !vTrn.getData("STOCK_UNIT", 0).equals("")) {
			result.setData("STOCK_UNIT", vTrn.getData("STOCK_UNIT", 0));
		}

		return result;
	}

	/**
	 * סԺҩ����ҩʱ���
	 * 
	 * @return startTime
	 */
	public String[] arrageTime() {
		TJDODBTool dbTool = new TJDODBTool();
		String[] result = new String[0];
		TParm parm = new TParm(dbTool.select("SELECT * FROM ODI_SYSPARM"));
		String dspnTime = parm.getValue("DSPN_TIME", 0); // ��ҩʱ��14��00
		String startTime = parm.getValue("START_TIME", 0); // ����ʱ��14��00

		if (isNullString(startTime) || isNullString(dspnTime))
			return null;
		result = new String[2];
		result[0] = dspnTime;
		result[1] = startTime;
		return result;
	}

	/**
	 * סԺ��ҩʱ������趨
	 * 
	 * @param startTime
	 *            ��ҩ������ʱ�� yyyyMMddHH24MI
	 * @param medStartDate
	 *            ��ҩ���������� yyyyMMddHH24MI
	 * @param medEndDate
	 *            ��ҩ���������� yyyyMMddHH24MI
	 * @return String[] 0:��ҩ���� 1:��ҩ��ʼ���� 2:��ҩ�������� 3:��ҩ��ʼ����ʱ�� 4:��ҩ��������ʱ��
	 * 
	 */
	public String[] assignMedTime(String effTime, String medStartDate,
			String medEndDate) {
		String[] result = new String[5];
		if (isNullString(effTime)) {
			return null;
		}

		String[] arrTime = arrageTime(); // ��ҩ����ʱ�� 14��00 ��14:00 ,��Ϊ��ҩʱ���Ѿ��ͻ���ʱ����ͬ
		if (arrTime == null) {
			return null;
		}
		// ��ҩ����
		// ȡ�ý�����磺��Timestamp��2009-03-30��2009-03-31����������ȡ��7���ڣ�edit:Ӧ���޶���7��������ڡ����ģ�
		// System.out.println("sql=============="+" SELECT * FROM ODI_DISTDATE WHERE SCH_DATE>=TO_DATE('"+effTime+"','YYYYMMDD') AND  DIST_FLG='Y' ORDER BY SCH_DATE");
		TParm parm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								" SELECT * FROM ODI_DISTDATE WHERE SCH_DATE>=TO_DATE('"
										+ effTime
										+ "','YYYYMMDDHH24MI') AND  DIST_FLG='Y' ORDER BY SCH_DATE"));
		if (isNullString(arrTime[0])) {
			return null;
		}
		// (ҩƷ����ʱ�� >= ��ҩʱ��)
		if (Long.parseLong(effTime.substring(8, 12)) >= Long
				.parseLong(arrTime[0])) {
			result[0] = String.valueOf(StringTool.getDateDiffer(StringTool
					.getTimestamp(medEndDate, "yyyyMMddHHmm"), StringTool
					.getTimestamp(medStartDate, "yyyyMMddHHmm")));
			result[1] = medStartDate.substring(0, 8);
			result[2] = String.valueOf(Integer.parseInt(medStartDate.substring(
					0, 8)) + 1);
			result[3] = medStartDate.substring(0, 8) + arrTime[0];
			result[4] = result[2] + arrTime[1];
		} else {
			result[0] = String.valueOf(StringTool.getDateDiffer(StringTool
					.getTimestamp(medEndDate, "yyyyMMdd"), StringTool
					.getTimestamp(medStartDate, "yyyyMMdd"))) + 1;
			result[1] = medStartDate.substring(0, 8);
			result[2] = medEndDate.substring(0, 8);
			result[3] = medStartDate.substring(0, 8) + arrTime[0];
			result[4] = result[2] + arrTime[1];
		}
		return result;
	}

	/**
	 * ȡ��Ƶ�ε�ʱ���
	 * 
	 * @param k
	 * @param std
	 * @param date
	 * @return
	 */
	public Vector UDD_GetStandingTime(int k, String std[], String date) {
		Vector result = new Vector();
		int j = 0;
		Vector stand = new Vector();
		for (int i = 0; i < k; i++) {
			if (std[i].substring(0, 8).equals(date)) {
				stand.add(std[i]);
				j++;
			}
		}
		result.add(stand);
		result.add(String.valueOf(j));
		return result;
	}

	public String[] bubbleSort(String data[], int n) {
		int numSorted = 0;
		int index;
		while (numSorted < n) {
			for (index = 1; index < n - numSorted; index++) {
				if (StringTool.getDateDiffer(StringTool.getTimestamp(
						data[index], "yyyyMMdd"), StringTool.getTimestamp(
						data[index - 1], "yyyyMMdd")) > 0) {
					swap(data, index, index - 1);
				}
			}
			numSorted++;
		}
		return data;
	}

	public void swap(String data[], int i, int j) {
		String temp;
		temp = data[i];
		data[i] = data[j];
		data[j] = temp;
	}

	/**
	 * ȡ�ó���ҩƷ����������������ҩ�ʹ�ʱ�伯��
	 * 
	 * @param effDate
	 *            ҩƷ�������� ������200904011530��
	 * @param dcDate
	 *            ҩƷͣ������ ������200904011530��
	 * @param sdttm
	 *            סԺҩ����ҩ���գ�����200904011400��
	 * @param edttm
	 *            סԺҩ����ҩ�����գ�����200904051400��
	 * @param freqCode
	 *            ҩƷƵ�δ���
	 * @param isOperation
	 *            �Ƿ�Ϊ����
	 * @return dispenseArrangeList List [200904010800��200904011200��200904011600]
	 */
	public List getDispenseArrangeDttm(String effDate, String dcDate,
			String sdttm, String edttm, String freqCode, boolean isOperation,
			TParm order) {
		List dispenseArrangeList = new ArrayList();
		// 1.�������
		// 1)�ǿ��ж�
		if (isNullString(effDate) || isNullString(sdttm) || isNullString(edttm)
				|| isNullString(freqCode)) {
			// System.out.println("1");
			// System.out.println("effDate"+effDate);
			// System.out.println("sdttm"+sdttm);
			// System.out.println("edttm"+edttm);
			// System.out.println("freqCode"+freqCode);
			return dispenseArrangeList;
		}
		// 2)ҵ���ж�
		// ҩƷ��������������ҩ���գ�Ϊ������������ʱ���������������β���ҩ������
		if (StringTool.getInt(effDate) > StringTool.getInt(sdttm)
				|| StringTool.getInt(dcDate) > StringTool.getInt(edttm)
				|| (!isNullString(dcDate) && StringTool.getInt(dcDate) < StringTool
						.getInt(effDate))) {
			// System.out.println("2");
			return dispenseArrangeList;
		}
		// //ҩƷͣ������������ҩ���գ��Ѿ�����ҩ��ʼʱ��֮ǰ�Ͱ����ҩ��ͣ���ˣ����β���ҩ������
		// if(StringTool.getInt(effDate)<StringTool.getInt(sdttm)){
		// return dispenseArrangeList;
		// }
		// 2.ȡ��������ҩ��ʼ�ʹ�
		// 1��ȡ����������ҩֹͣ����ʱ�䡣��Ҫ�Ƚ�dcDate��edttm,ȥ���������Ϊ������Ч��ͣ������
		String realEndDateTime;
		if (!isNullString(dcDate))
			realEndDateTime = (StringTool.getTimestamp(dcDate, "yyyyMMddHHmm")
					.compareTo(StringTool.getTimestamp(edttm, "yyyyMMddHHmm")) > 0) ? edttm
					: dcDate;
		else
			realEndDateTime = edttm;
		// System.out.println("realEndDateTime->"+realEndDateTime);
		// 2)ȡ����ҩ�������ڼ��ϣ�����20090401��20090402��20090403��
		int takeDay = StringTool.getDateDiffer(StringTool.getTimestamp(
				realEndDateTime.substring(0, 8), "yyyyMMdd"), StringTool
				.getTimestamp(sdttm.substring(0, 8), "yyyyMMdd"));
		// System.out.println("takeDay->" + takeDay);
		List medDays = new ArrayList();
		medDays.add(sdttm);
		for (int i = 1; i <= takeDay; i++) {
			// System.out.println("sdttm->in takeDay loop"+sdttm);
			Timestamp temp = StringTool.getTimestamp(sdttm, "yyyyMMddHHmm");
			temp = StringTool.rollDate(temp, Long.parseLong(i + ""));
			String tempDate = StringTool.getString(temp, "yyyyMMddHHmmss");
			// System.out.println("tempDate"+tempDate);
			tempDate = tempDate.substring(0, 12);
			medDays.add(tempDate);
		}
		// System.out.println("medDays=" + medDays);
		// ���ô�һ���00��00��һ���23��59
		if (takeDay == 0 && isOperation) {
			medDays.add(realEndDateTime);
		}
		// 3)ȡ�ø�ҩƷ��Ƶ������
		List Standingtimes = new ArrayList(); // Ƶ��ʱ�㼯�ϣ�0800,1600����
		Standingtimes = getStandingTime(freqCode, isOperation); // ͨ�����ù��÷���ȡ��Ƶ��ʱ��
		// System.out.println("ͨ�����ù��÷���ȡ��Ƶ��ʱ��---"+Standingtimes);
		if (Standingtimes == null || Standingtimes.size() < 1) {
			// System.out.println("3");
			return dispenseArrangeList;
		}

		/*
		 * 4)ѭ����ҩ���ڼ��ϣ���2�������ļ���medDays����
		 * ���ü��ϵ�ÿ����ʽΪString�����ڱ���Ϊ����20090401�İ�λ��String tempdate��
		 * ��ѭ��Ƶ��ʱ�㼯�ϣ���Standingtimes��ȡ������ÿһ��Ƶ��ʱ����0800,1600�ȣ�����Ϊtime���ٶ���String
		 * tempdttm=tempdate+time �ж�tempdttm >= sdttm && tempdttm <=
		 * realEndDateTime,�����������������tempdttm��ӵ�Ҫ���صļ����У���Ϊ��������ҩ�ʹ�ʱ��㡣
		 */
		TParm freq = this.setFreq(freqCode);
		// System.out.println("freq========="+freq);
		// System.out.println("freq="+freq);
		if (freq.getErrCode() != 0) {
			// System.out.println("4");
			return dispenseArrangeList;
		}
		int cycle = TCM_Transform.getInt(freq.getData("CYCLE"));
		// System.out.println("cycle " + cycle);
		// ���󲿷�ҩƷ��Ƶ��
		if (cycle == 1) {
			// System.out.println("------------------------");
			// System.out.println("medDays---"+medDays);
			// System.out.println("Standingtimes---"+Standingtimes);
			// System.out.println("sdttm---"+sdttm);
			// System.out.println("realEndDateTime---"+realEndDateTime);
			// System.out.println("------------------------");
			dispenseArrangeList = this.getDttm(medDays, Standingtimes, sdttm,
					realEndDateTime);
			// System.out.println("5");
			return dispenseArrangeList;
		}
		// ����QOD,Q3D��
		else {
			// zhangyong20101124 begin ע��
			// System.out.println("effDate======" + effDate);
			// System.out.println("realEndDateTime=" + realEndDateTime);
			// System.out.println("cycle===========" + cycle);
			// System.out.println("StandingTime====" + Standingtimes);
			// System.out.println("sdttm====" + sdttm);
			//
			// dispenseArrangeList = getMutilCycleDttm(effDate, realEndDateTime,
			// cycle, Standingtimes, sdttm);
			// System.out.println("dispenseArrangeList===" +
			// dispenseArrangeList);
			// zhangyong20101124 end ע��
			// zhangyong20101124 begin
			String caee_no = order.getValue("CASE_NO");
			String order_no = order.getValue("ORDER_NO");
			String order_seq = order.getValue("ORDER_SEQ");
			String sql = "SELECT ORDER_DATE, ORDER_DATETIME FROM ODI_DSPND WHERE CASE_NO = '"
					+ caee_no
					+ "' AND ORDER_NO = '"
					+ order_no
					+ "' AND ORDER_SEQ='"
					+ order_seq
					+ "' ORDER BY ORDER_DATE, ORDER_DATETIME DESC";
			TParm dspndParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (dspndParm != null && dspndParm.getCount() > 0) {
				effDate = dspndParm.getValue("ORDER_DATE", 0)
						+ dspndParm.getValue("ORDER_DATETIME", 0);
			} else {
				effDate = effDate;
			}
			if (order.getTimestamp("EFF_DATE").compareTo(
					StringTool.getTimestamp(sdttm, "yyyyMMddHHmm")) > 0) { // 20131231
				// modify
				// shibl
				dispenseArrangeList = getMutilCycleStDttm(effDate,
						realEndDateTime, cycle, Standingtimes);
			} else {
				dispenseArrangeList = getMutilCycleDttm(effDate,
						realEndDateTime, cycle, Standingtimes, sdttm);
			}
			// zhangyong20101124 end
		}
		// System.out.println("6");
		return dispenseArrangeList;
	}

	/**
	 * 
	 * @param medDays
	 *            List ���ڼ���
	 * @param Standingtimes
	 *            List ʱ�伯��
	 * @param freq
	 *            TParm Ƶ������
	 * @param sdttm
	 *            ��ʼʱ��
	 * @param realEndDateTime
	 *            ����ʱ��
	 * @return
	 */
	public List getWeekDttm(List medDays, List Standingtimes, TParm freq,
			String sdttm, String realEndDateTime) {
		List result = new ArrayList();
		int dayOfWeek = 0;
		if (medDays == null || medDays.size() < 1 || Standingtimes == null
				|| Standingtimes.size() < 1 || isNullString(sdttm)
				|| isNullString(realEndDateTime))
			return result;
		if (freq == null)
			return result;
		// ȡ�����ڼ���������Ϊ1��������Ϊ7
		if (TCM_Transform.getBoolean(freq.getValue("SUN_FLG"))) {
			dayOfWeek = 1;
		} else if (TCM_Transform.getBoolean(freq.getValue("MON_FLG"))) {
			dayOfWeek = 2;
		} else if (TCM_Transform.getBoolean(freq.getValue("TUE_FLG"))) {
			dayOfWeek = 3;
		} else if (TCM_Transform.getBoolean(freq.getValue("WED_FLG"))) {
			dayOfWeek = 4;
		} else if (TCM_Transform.getBoolean(freq.getValue("THUR_FLG"))) {
			dayOfWeek = 5;
		} else if (TCM_Transform.getBoolean(freq.getValue("FRI_FLG"))) {
			dayOfWeek = 6;
		} else if (TCM_Transform.getBoolean(freq.getValue("STA_FLG"))) {
			dayOfWeek = 7;
		}
		int dayWeekInMedDays = 0;
		for (int i = 0; i < medDays.size(); i++) {
			String day = (String) medDays.get(i);
			GregorianCalendar t = new GregorianCalendar();
			t.setTime(StringTool.getTimestamp(day, "yyyyMMdd"));
			dayWeekInMedDays = t.get(t.DAY_OF_WEEK);
			if (dayWeekInMedDays != dayOfWeek) {
				continue;
			}
			for (int j = 0; j < Standingtimes.size(); j++) {
				int dttm = StringTool.getInt(day
						+ (String) Standingtimes.get(i));
				int startDate = StringTool.getInt(sdttm);
				int endDate = StringTool.getInt(realEndDateTime);
				if (dttm > startDate && dttm <= endDate) {
					result.add(dttm + "");
				}
			}
		}
		return result;
	}

	/**
	 * ȡ��Ƶ��ʱ���
	 * 
	 * @param freqCode
	 *            String freqCode
	 * @param isOperation
	 *            boolean �Ƿ�Ϊ���ã�trueΪ���ã�false ΪҩƷ
	 * @return
	 */
	public List getStandingTime(String freqCode, boolean isOperation) {
		List result = new ArrayList();
		if (isNullString(freqCode)) {
			// System.out.println("TotQtyTool->getStandingTime->freqCode is null");
			return result;
		}
		// ҩƷƵ��
		if (!isOperation) {
			// ȡ�ô�ҩƷƵ�ε�48ʱ���
			// System.out.println("SELECT FREQ_UNIT_48 FROM SYS_PHAFREQ WHERE FREQ_CODE='"+freqCode.trim().toUpperCase()+"'");
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					"SELECT FREQ_UNIT_48 FROM SYS_PHAFREQ WHERE FREQ_CODE='"
							+ freqCode + "'"));
			if (parm.getErrCode() != 0) {
				// System.out.println("TotQtyTool->getStandingTime->parm is null");
				// System.out.println("TotQtyTool->getStandingTime->parm->"+parm);
				return result;
			}
			// shibl 20120321 modify
			String sFreq_Unit48 = parm.getValue("FREQ_UNIT_48", 0);
			// System.out.println("TotQtyTool->getStandingTime->parm->"+parm);
			// System.out.println("TotQtyTool->getStandingTime->parm->"+parm.getValue("FREQ_UNIT_48"));
			// System.out.println("TotQtyTool->getStandingTime->parm->"+parm.getValue("FREQ_UNIT_48",0));
			if (isNullString(sFreq_Unit48)) {
				// System.out.println("TotQtyTool->getStandingTime->sFreq_Unit48 is null");
				return result;
			}

			char[] freq48 = sFreq_Unit48.toCharArray();
			for (int i = 0; i < freq48.length; i++) {
				String cnt = "";
				if ("Y".equalsIgnoreCase(String.valueOf(freq48[i]))) {
					// �����ж� shibl 20120321 modify
					if ((i + 1) % 2 != 0) {
						cnt = String.valueOf((i + 1) % 2 == 0 ? (i + 1) / 2 - 1
								: (i + 1) / 2);
						if (cnt.length() == 0)
							cnt = "00";
						if (cnt.length() == 1)
							cnt = "0" + cnt;
						cnt += "00";
					} else {
						cnt = String.valueOf((i + 1) % 2 == 0 ? (i + 1) / 2 - 1
								: (i + 1) / 2);
						if (cnt.length() == 0)
							cnt = "00";
						if (cnt.length() == 1)
							cnt = "0" + cnt;
						cnt += "30";
					}
					result.add(cnt);
				}

			}
		} else {
			String sql = " SELECT STANDING_TIME FROM SYS_TRTFREQTIME WHERE FREQ_CODE='"
					+ freqCode + "' ORDER BY STANDING_TIME";
			// System.out.println("sql======="+sql);
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			// System.out.println("standing_time="+parm);
			if (parm.getErrCode() != 0) {
				return result;
			}
			int count = parm.getCount();
			for (int i = 0; i < count; i++) {
				String temp = parm.getValue("STANDING_TIME", i);
				if (isNullString(temp))
					return new ArrayList();
				if (temp.trim().length() != 4)
					return new ArrayList();
				result.add(temp);
			}
		}

		return result;
	}

	/**
	 * ���������պ�����֮�������ʱ�� sdttm<tempDttm<=edttm
	 * 
	 * @param days
	 *            List ���ڼ���
	 * @param freqTimes
	 *            List Ƶ��ʱ�㼯��
	 * @param sdttm
	 *            String ���� 200904011400
	 * @param edttm
	 *            ���� 200904021400
	 * @return List
	 */
	public List getDttm(List days, List freqTimes, String sdttm, String edttm) {
		List result = new ArrayList();
		if (days == null || freqTimes == null || days.size() < 1
				|| freqTimes.size() < 1) {
			// System.out.println("1");
			// System.out.println("days==null"+(days==null));
			// System.out.println("freqTimes==null"+(freqTimes==null));
			// System.out.println("days.size"+days.size());
			// System.out.println("freqTimes.size"+freqTimes.size());
			return result;
		}
		// System.out.println("1");
		if (isNullString(sdttm) || isNullString(edttm)) {
			// System.out.println("sdttm");
			return result;
		}

		String tempDate, tempDttm = "";

		// if(sdttm!=null&&sdttm.trim().length()<14){
		// sdttm=sdttm+"00";
		// }
		// System.out.println("getDttm.sdttm" + sdttm);
		// System.out.println("getDttm.edttm" + edttm);
		long sdttmInt = StringTool.getLong(sdttm);
		long edttmInt = StringTool.getLong(edttm);
		// ȡ��days��������β֮�䣨������β�����е�����
		List loopDays = new ArrayList();
		// System.out.println("2");
		try {
			String daysStart = TCM_Transform.getString(days.get(0));
			String daysEnd = TCM_Transform.getString(days.get(days.size() - 1));
			daysStart = daysStart.substring(0, 8);
			// System.out.println("daysStart=" + daysStart);
			daysEnd = daysEnd.substring(0, 8);
			// System.out.println("dayEnd=" + daysEnd);
			int differ = StringTool.getDateDiffer(StringTool.getTimestamp(
					daysEnd, "yyyyMMdd"), StringTool.getTimestamp(daysStart,
					"yyyyMMdd"));
			// System.out.println("differ=" + differ);
			loopDays.add(daysStart);
			Timestamp start = StringTool.getTimestamp(daysStart, "yyyyMMdd");
			// System.out.println("start=" + start);
			for (int i = 1; i <= differ; i++) {
				String tempDay = StringTool.getString(StringTool.rollDate(
						start, i), "yyyyMMdd");
				// System.out.println("tempDay=" + tempDay);
				loopDays.add(tempDay);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		// System.out.println("3");
		for (int i = 0; i < loopDays.size(); i++) {
			tempDate = TCM_Transform.getString(loopDays.get(i));
			// System.out.println("days.get[i]" + loopDays.get(i));
			// System.out.println("tempDate==-------" + tempDate);
			if (tempDate.trim().length() == 8) {
				tempDate = StringTool.getString(StringTool.getDate(tempDate,
						"yyyyMMdd"), "yyyyMMdd");
			} else if (tempDate.trim().length() == 12) {
				tempDate = StringTool.getString(StringTool.getDate(tempDate,
						"yyyyMMddHHmm"), "yyyyMMddHHmm");
			}
			// System.out.println("tempDate==-------" + tempDate);
			if (isNullString(tempDate)) {
				// System.out.println("tempDate");
				return new ArrayList();
			}
			for (int j = 0; j < freqTimes.size(); j++) {
				if (tempDate.length() == 8)
					tempDttm = tempDate
							+ TCM_Transform.getString(freqTimes.get(j));
				else if (tempDate.length() == 12)
					tempDttm = tempDate.substring(0, 8)
							+ TCM_Transform.getString(freqTimes.get(j));
				// tempDttm=StringTool.getString(StringTool.getDate(tempDttm,"yyyyMMddHHmm"),"yyyyMMddHHmmss");
				// System.out.println("tempDttm===================" + tempDttm);
				long tempDttmInt = StringTool.getLong(tempDttm);
				// System.out.println("tempDttmInt" + tempDttmInt);
				// System.out.println("sdttmInt===" + sdttmInt);
				// System.out.println("edttmInt===" + edttmInt);
				// System.out.println("tempDttmInt>=sdttmInt"
				// + (tempDttmInt >= sdttmInt));
				// System.out.println("tempDttmInt<=edttmInt"
				// + (tempDttmInt <= edttmInt));
				if (tempDttmInt >= sdttmInt && tempDttmInt <= edttmInt) {
					// System.out.println("in if==============" + tempDttm);
					result.add(tempDttm);
				}
			}
		}
		// System.out.println("mission impossible .result" + result);
		List temp = new ArrayList();
		if (result.size() > 0) {
			for (int i = result.size() - 1; i > -1; i--) {
				if (temp.indexOf(result.get(i)) < 0) {
					temp.add(result.get(i));
				}
			}
		}

		// System.out.println("result_>"+temp);
		return temp;
	}

	/**
	 * 
	 * @param order
	 *            TParm һ��order��Ϣ
	 * @param dispenseTimes
	 *            List
	 * @return //order���LASTDSPN_QTY ORDER_LASTDSPN_QTY //order���ACUMDSPN_QTY
	 *         ORDER_ACUMDSPN_QTY //order���ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
	 *         //M���dispenseQty M_DISPENSE_QTY //M���dispenseUnit M_DISPENSE_UNIT
	 *         //M���dosageQty M_DOSAGE_QTY //M���dosageUnit M_DOSAGE_UNIT
	 *         //D���MediQty D_MEDI_QTY //D���MediUnit D_MEDI_UNIT //D���dosageQty
	 *         D_DOSAGE_QTY //D���dosageUnit D_DOSAGE_UNIT
	 */
	public Map getDispenseQty(TParm order, List dispenseTimes) {
		// System.out.println("order==="+order);
		// ��Ϊpha_transunit�е�dosageUnitͬsysFee��dosageUnit�����Դ�pha_transunitȡ
		// Order:DOSAGE_QTY,DISPENSE_QTY,������������д��ACUMMEDI_QTY,LASTDSPN_QTY,ACUMDSPN_QTY(*��ʹ�)
		// DspnM:DOSAGE_QTY��2ƿ��,DISPENSE_QTY��2ƿ��
		// DspnD:DOSAGE_QTY,�������εģ�����
		Map result = new HashMap();
		// order ���е��ۼư�ҩ������󴫳���Ҫ������ǰ���ۼƷ�ҩ����,
		double orderAccumMediQty = 0.0;
		// order ���е�����ҩ��
		double orderLastDspnQty = 0.0;
		// order ���е�������
		double orderAccumDspnQty = 0.0;
		// order ���е�������
		double orderFstQty = 0.0;
		// dspnM ���еĳ���������DosageQty
		double dspnMDosageQty = 0.0;
		// dspnM ���еĳ���������DispenseQty
		double dspnMDispenseQty = 0.0;
		// dspnD ���еĲ��˴�����DosageQty
		double dspnDDosageQty = 0.0;
		// dspnD ���еĲ��˴�����MediQty
		double dspnDMediQty = 0.0;
		if (order == null || dispenseTimes == null || dispenseTimes.size() < 1) {
			// System.out.println("order is null"+order==null);
			// System.out.println("dispenseTimes is null"+dispenseTimes ==
			// null);
			// System.out.println("dispenseTimes:"+dispenseTimes.size());
			return result;
		}
		TParm parmTrn = this.setTrn(order);
		// System.out.println("parmTrn===================="+parmTrn);
		double startqty = 1; //
		double dosageUnitQty = 1;
		double dispenseUnitQty = 1;
		String startUnit = "", dosageUnit = "", dispenseUnit = "";
		if (parmTrn.getErrCode() != 0) {
			// System.out.println("TotQtyTool->getDispenseQty->parmTrn err");
			return result;
		}
		startqty = parmTrn.getDouble("MEDI_QTY");
		if (startqty == 0.0)
			startqty = 1.0;
		dosageUnitQty = parmTrn.getDouble("DOSAGE_QTY");
		if (dosageUnitQty == 0.0)
			dosageUnitQty = 1.0;
		dispenseUnitQty = parmTrn.getDouble("STOCK_QTY");
		if (dispenseUnitQty == 0.0)
			dispenseUnitQty = 1.0;
		startUnit = parmTrn.getValue("MEDI_UNIT");
		if (order.getBoolean("GIVEBOX_FLG")) {
			dosageUnit = parmTrn.getValue("STOCK_UNIT");
		} else {
			dosageUnit = parmTrn.getValue("DOSAGE_UNIT");
		}

		dispenseUnit = dosageUnit;
		if (dosageUnit == null || "".equalsIgnoreCase(dosageUnit)) {
			dispenseUnit = order.getValue("DISPENSE_UNIT");
			dosageUnit = dispenseUnit;
			startUnit = dispenseUnit;
		}

		// ԭʼҽ����Ϣ���������ʹε���������ֵ
		int times = dispenseTimes.size();
		// System.out.println("times->"+times);

		// �鵽��ͬCASE_NOû��DC����ͬҩƷ�����ÿ�ҩ�������÷�ҩ��
		String sql = "SELECT SUM(A.MEDI_QTY) ACUMMEDI_QTY,SUM(A.DOSAGE_QTY) ACUMDSPN_QTY "
				+ "	FROM ODI_DSPND A,ODI_ORDER B "
				+ "	WHERE A.CASE_NO=B.CASE_NO " 
				+ " AND A.ORDER_NO=B.ORDER_NO "
				+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
				+ " AND A.DC_DATE IS NULL AND A.DC_NS_CHECK_DATE IS NULL "
				+" AND A.CASE_NO='"
				+ order.getValue("CASE_NO")
				+ "' AND A.ORDER_CODE='" + order.getValue("ORDER_CODE") + "'"
				// zhangyong20110412
				+ " AND B.RX_KIND = '" + order.getValue("RX_KIND") + "' ";
		// System.out.println("���������ۼƷ�ҩ��sql=======" + sql);
		TParm accumParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (accumParm.getErrCode() != 0) {
			return result;
		}
		if (accumParm.getCount() != 1) {
			return result;
		}
		double orderAccumMediQtyTemp = accumParm.getDouble("ACUMMEDI_QTY", 0);
		// System.out.println("==accumParm=="
		// + accumParm.getData("ACUMMEDI_QTY", 0));
		// System.out.println("==orderAccumMediQtyTemp==" +
		// orderAccumMediQtyTemp);
		double orderAccumDspnQtyTemp = accumParm.getDouble("ACUMDSPN_QTY", 0);
		orderAccumMediQty = StringTool.round(orderAccumMediQtyTemp, 4); // tqty
		// System.out.println("==orderAccumMediQty==" + orderAccumMediQty);
		orderLastDspnQty = StringTool.round(times * order.getDouble("MEDI_QTY")
				/ startqty, 4);
		orderAccumDspnQty = StringTool.round(orderAccumDspnQtyTemp, 4);
		dspnDMediQty = StringTool.round(order.getDouble("MEDI_QTY"), 4);
		dspnDDosageQty = orderLastDspnQty;
		orderFstQty = orderLastDspnQty;
		dspnMDispenseQty = orderLastDspnQty;
		dspnMDosageQty = orderLastDspnQty;
		// ������
		// orderAccumDspnQty = orderLastDspnQty;//tqty1
		// Ҫд��M���еĳ���ʹδ�����dosageQty��Ҫд��D���е�dosageQty
		double dosageQtyAll, dosageQty;
		String accum = "N";
		// System.out.println("orderAccumMediQty="+orderAccumMediQty);
		// order���LASTDSPN_QTY
		result.put("ORDER_LASTDSPN_QTY", 1.0);
		// order���ACUMDSPN_QTY
		result.put("ORDER_ACUMDSPN_QTY", 1 + order.getDouble("ACUMDSPN_QTY"));
		// order���ACUMMEDI_QTY
		// System.out.println("acummedi_Qty11111====="
		// + order.getDouble("ACUMMEDI_QTY"));
		result.put("ORDER_ACUMMEDI_QTY", 1 + order.getDouble("ACUMMEDI_QTY"));
		// M���dispenseQty
		result.put("M_DISPENSE_QTY", 1.0);
		// M���dispenseUnit
		result.put("M_DISPENSE_UNIT", dispenseUnit);
		// M���dosageQty
		result.put("M_DOSAGE_QTY", 1.0);
		// M���dosageUnit
		result.put("M_DOSAGE_UNIT", dosageUnit);
		// D���MediQty
		result.put("D_MEDI_QTY", 1.0);
		// D���MediUnit
		result.put("D_MEDI_UNIT", startUnit);
		// D���dosageQty
		result.put("D_DOSAGE_QTY", 1.0);
		// D���dosageUnit
		result.put("D_DOSAGE_UNIT", dosageUnit);
		// System.out.println("before return result is ===="+result);
		// System.out.println("order======================="+order);
		// System.out.println("order.cat1Type=============="+order.getValue("CAT1_TYPE"));
		TParm parmBase = new TParm();
		if ("PHA".equalsIgnoreCase(order.getValue("CAT1_TYPE"))) {
			parmBase = new TParm(TJDODBTool.getInstance().select(
					"SELECT * FROM PHA_BASE WHERE ORDER_CODE='"
							+ order.getValue("ORDER_CODE") + "'"));
			// ȡ��phaBase

			if (parmBase == null) {
				return result;
			}
			if (parmBase.getErrCode() != 0) {
				return result;
			}
			if (parmBase.getCount() != 1) {
				return result;
			}
			parmBase = parmBase.getRow(0);
		} else {
			// System.out.println("TypeTool.getDouble(1.0*times)"+TypeTool.getDouble(1.0*times));
			// System.out.println("unit========================="+order.getValue("DOSAGE_UNIT"));

			// order���LASTDSPN_QTY
			double qty = 1;
			if (order.getDouble("MEDI_QTY") > 1) {
				qty = order.getDouble("MEDI_QTY");
			}
			//
			if (order.getDouble("DOSAGE_QTY") > 1) {
				qty = order.getDouble("DOSAGE_QTY");
			}

			result.put("ORDER_LASTDSPN_QTY", TypeTool.getDouble(qty * times));
			// order���ACUMDSPN_QTY
			result.put("ORDER_ACUMDSPN_QTY", TypeTool.getDouble(qty * times));
			// order���ACUMMEDI_QTY
			// System.out.println("acummedi_Qty2222====="
			// + order.getDouble("ACUMMEDI_QTY"));
			result.put("ORDER_ACUMMEDI_QTY", TypeTool.getDouble(qty * times));
			// M���dispenseQty
			result.put("M_DISPENSE_QTY", TypeTool.getDouble(qty * times));
			// M���dispenseUnit
			result.put("M_DISPENSE_UNIT", order.getValue("DOSAGE_UNIT"));
			// M���dosageQty
			// zhangyong20101202 begin
			result.put("M_DOSAGE_QTY", TypeTool.getDouble(qty * times));
			// zhangyong20101202 begin
			// M���dosageUnit
			result.put("M_DOSAGE_UNIT", order.getValue("DOSAGE_UNIT"));
			// D���MediQty
			result.put("D_MEDI_QTY", TypeTool.getDouble(qty * times));
			// D���MediUnit
			result.put("D_MEDI_UNIT", order.getValue("DOSAGE_UNIT"));
			// D���dosageQty
			// zhangyong20101202 begin
			result.put("D_DOSAGE_QTY", TypeTool.getDouble(qty * times));
			// zhangyong20101202 begin
			// D���dosageUnit
			result.put("D_DOSAGE_UNIT", order.getValue("DOSAGE_UNIT"));
			// System.out.println("return===="+result);
			return result;
		}

		// System.out.println("parmBase==============="+parmBase);
		// System.out.println("DSPNSTOTDOSE_FLG="+parmBase.getBoolean("DSPNSTOTDOSE_FLG"));
		// System.out.println("order============"+order);
		// ����Ͷҩ��Ʃ����ҩˮ��ҩ���ҩ����ȷ��ҩ��û�������
		if (parmBase.getBoolean("DSPNSTOTDOSE_FLG")) {
			// �������Ͷҩ�����Ƿ�ҩ��λ�Ϳ�浥λ�ֲ�ͬ������ɼ�Ǯ�����⣬����ֱ�ӷ���
			if (!dosageUnit.equalsIgnoreCase(dispenseUnit)) {
				return result;
			}
			// order���LASTDSPN_QTY
			result.put("ORDER_LASTDSPN_QTY", 1.0);
			// order���ACUMDSPN_QTY
			result.put("ORDER_ACUMDSPN_QTY", 1 + order
					.getDouble("ACUMDSPN_QTY"));
			// order���ACUMMEDI_QTY
			result.put("ORDER_ACUMMEDI_QTY", 1 + order
					.getDouble("ACUMMEDI_QTY"));
			/*
			 * System.out.println("acummedi_Qty33333=====" +
			 * order.getDouble("ACUMMEDI_QTY"));
			 */

			// M���dispenseQty
			result.put("M_DISPENSE_QTY", 1.0);
			// M���dispenseUnit
			result.put("M_DISPENSE_UNIT", dispenseUnit);
			// M���dosageQty
			result.put("M_DOSAGE_QTY", 1.0);
			// M���dosageUnit
			result.put("M_DOSAGE_UNIT", dosageUnit);
			// D���MediQty
			result.put("D_MEDI_QTY", 1.0);
			// D���MediUnit
			result.put("D_MEDI_UNIT", startUnit);
			// D���dosageQty
			result.put("D_DOSAGE_QTY", 1.0);
			// D���dosageUnit
			result.put("D_DOSAGE_UNIT", dosageUnit);
			// System.out.println("before return result is ===="+result);
			return result;
		}
		// System.out.println("parmBase->"+parmBase);
		// Ҫ���ж��Ƿ�������ã���������ã��������жϵ���2����
		// �����������
		// System.out.println("order.continous_flg="+order.getBoolean("CONTINUOUS_FLG"));
		// System.out.println("reuse_flg=----------"+parmBase.getBoolean("REUSE_FLG"));
		// System.out.println("first==============="+("ST".equalsIgnoreCase(order.getValue("RX_KIND"))&&!order.getBoolean("CONTINUOUS_FLG")));
		// System.out.println("��������ҩ============"+parmBase.getBoolean("UDCARRY_FLG"));
		// System.out.println("half================"+TypeTool.getBoolean(parmBase.getData("HALF_USE_FLG")));
		// ��ʱû�����õģ���ʱ���õġ�����ҩƷ��4�����ԣ�����Ͷҩ���ɲ��롢���⼰��������������ҩΪ�������ԡ�����ʱ�Ƿ����õķ�֧�ж�Ҫ���ж�.���������4�����ԣ��ٷֱ�ȥ�жϱ�����ҩ��+��ʷ��ҩ���ĺͣ�������ҩ��
		// ��ʱû����
		if (("ST".equalsIgnoreCase(order.getValue("RX_KIND")) && !order
				.getBoolean("CONTINUOUS_FLG"))) {
			// System.out.println("22222222222222=" +
			// TypeTool.getBoolean(parmBase.
			// getData("HALF_USE_FLG")));
			// ��������ʹ��ע��ʹ��(��⼴��)�����±�����ֵ˳�����в�ͬ��ֵ���������ı丳ֵ��˳��
			if (parmBase.getBoolean("REUSE_FLG")) {
				// System.out.println("mediQty==="+order.getDouble("MEDI_QTY"));
				// System.out.println("startQty=="+startqty);
				// System.out.println("///======="+StringTool.round(order.getDouble("MEDI_QTY")/startqty,
				// 4));
				// System.out.println("ceil======"+Math.ceil(StringTool.round(order.getDouble("MEDI_QTY")/startqty,
				// 4)));
				// System.out.println("times====="+times);

				// ����ʵ�ʰ�ҩ��������DispenseUnit��
				orderLastDspnQty = Math.ceil(StringTool.round(order
						.getDouble("MEDI_QTY")
						/ startqty, 4))
						* times;
				// System.out.println("***======="+orderLastDspnQty);
				// �ۼƿ�ҩ��������MEDI_UNIT��
				orderAccumMediQty = StringTool.round(orderLastDspnQty
						* startqty, 4)
						+ orderAccumMediQtyTemp;
				// System.out.println("==orderAccumMediQty8888=="
				// + orderAccumMediQty);

				// M���е�dispenseQty
				dspnMDispenseQty = orderLastDspnQty;
				// M���е�DosageQty
				dspnMDosageQty = dspnMDispenseQty;
				// D���е�DosageQty
				dspnDDosageQty = dspnMDispenseQty;
				// ����������
				orderAccumDspnQty = orderLastDspnQty + orderAccumDspnQtyTemp;
			}
			// סԺ��������ҩע�ǣ����±�����ֵ˳�����в�ͬ��ֵ���������ı丳ֵ��˳��
			// else if (parmBase.getBoolean("UDCARRY_FLG")) {
			// // ����ʵ�ʰ�ҩ��������DispenseUnit��
			// orderLastDspnQty = Math.ceil(StringTool.round( (order.getDouble(
			// "MEDI_QTY") * times / startqty), 4));
			// // M���е�dispenseQty
			// dspnMDispenseQty = orderLastDspnQty;
			// // M���е�DosageQty
			// dspnMDosageQty = orderLastDspnQty;
			// // D���е�DosageQty
			// dspnDDosageQty = dspnMDosageQty;
			// // ����������
			// orderAccumDspnQty = orderAccumDspnQtyTemp + orderLastDspnQty;
			// // �����ۼƿ�ҩ��
			// orderAccumMediQty = orderLastDspnQty * startqty +
			// orderAccumMediQtyTemp;
			// }
			// �ɲ���
			else if (TypeTool.getBoolean(parmBase.getData("HALF_USE_FLG"))) {
				// System.out.println("here");
				// System.out.println("mediQty="+order.getDouble("MEDI_QTY"));
				// System.out.println("times==="+times);

				// ����ʵ�ʰ�ҩ��������DispenseUnit��
				orderLastDspnQty = StringTool.round((order
						.getDouble("MEDI_QTY")
						* times / startqty), 4);
				// System.out.println("orderLastDspnQty==="+orderLastDspnQty);
				// M���е�dispenseQty
				dspnMDispenseQty = orderLastDspnQty;
				// M���е�DosageQty
				dspnMDosageQty = orderLastDspnQty;
				// D���е�DosageQty
				dspnDDosageQty = orderLastDspnQty;
				// ����������
				orderAccumDspnQty = orderLastDspnQty+ orderAccumDspnQtyTemp;
				// �����ۼƿ�ҩ��
				// $$ modified by lx ����test
				orderAccumMediQty = StringTool.round(orderLastDspnQty
						* startqty, 4)+ orderAccumMediQtyTemp;
				// System.out.println("==orderAccumMediQty9999=="
				// + orderAccumMediQty);
			}
			// ����Ͷҩ
			else if (TypeTool.getBoolean(parmBase.getData("DSPNSTOTDOSE_FLG"))) {

			}
			// ��ҩע�ǣ���浥λ��ҩ��
			else if (TypeTool.getBoolean(order.getData("GIVEBOX_FLG"))) {
				// TODO:Ԥ���з�ҩ�ӿ�
			}
			// û���κ�����
			else {

				// ����ʵ�ʰ�ҩ��������DispenseUnit��
				orderLastDspnQty = Math.ceil(StringTool.round((order
						.getDouble("MEDI_QTY")
						* times / startqty), 4));
				// M���е�dispenseQty
				dspnMDispenseQty = orderLastDspnQty;
				// M���е�DosageQty
				dspnMDosageQty = orderLastDspnQty;
				// D���е�DosageQty
				dspnDDosageQty = orderLastDspnQty;
				// ����������
				orderAccumDspnQty = orderLastDspnQty+orderAccumDspnQtyTemp;
				// �����ۼƿ�ҩ��
				// modified by lx ����test
				orderAccumMediQty = StringTool.round(order
						.getDouble("MEDI_QTY")
						* times, 4)+orderAccumMediQtyTemp;
				// System.out.println("==orderAccumMediQty3333=="
				// + orderAccumMediQty);

			}
		}
		// ��ʱ������
		else if ("ST".equalsIgnoreCase(order.getValue("RX_KIND"))
				&& order.getBoolean("CONTINUOUS_FLG")) {
			// System.out.println("reuseFlg==="+parmBase.getBoolean("REUSE_FLG"));
			// System.out.println("udcarryFlg="+(!parmBase.getBoolean("UDCARRY_FLG")));
			// System.out.println("HALF_USE_FLG"+TypeTool.getBoolean(parmBase.getData("HALF_USE_FLG")));
			// System.out.println("DSPNSTOTDOSE_FLG"+TypeTool.getBoolean(parmBase.getData("DSPNSTOTDOSE_FLG")));
			// ��������ʹ��ע��ʹ��(��⼴��)�����±�����ֵ˳�����в�ͬ��ֵ���������ı丳ֵ��˳��
			if (parmBase.getBoolean("REUSE_FLG")) {
				// System.out.println("mediQty==="+order.getDouble("MEDI_QTY"));
				// System.out.println("startQty=="+startqty);
				// System.out.println("///======="+StringTool.round(order.getDouble("MEDI_QTY")/startqty,
				// 4));
				// System.out.println("ceil======"+Math.ceil(StringTool.round(order.getDouble("MEDI_QTY")/startqty,
				// 4)));
				// System.out.println("times====="+times);

				// ����ʵ�ʰ�ҩ��������DispenseUnit��
				orderLastDspnQty = Math.ceil(StringTool.round(order
						.getDouble("MEDI_QTY")
						/ startqty, 4))
						* times;
				// System.out.println("***======="+orderLastDspnQty);
				// �ۼƿ�ҩ��������MEDI_UNIT��
				orderAccumMediQty = StringTool.round(orderLastDspnQty
						* startqty, 4)+orderAccumMediQtyTemp;
				// System.out.println("==orderAccumMediQty4444=="
				// + orderAccumMediQty);

				// M���е�dispenseQty
				dspnMDispenseQty = orderLastDspnQty;
				// M���е�DosageQty
				dspnMDosageQty = dspnMDispenseQty;
				// D���е�DosageQty
				dspnDDosageQty = dspnMDispenseQty;
				// ����������
				orderAccumDspnQty = orderLastDspnQty+orderAccumDspnQtyTemp;
			} // סԺ��������ҩע�ǣ����±�����ֵ˳�����в�ͬ��ֵ���������ı丳ֵ��˳��
			// else if (parmBase.getBoolean("UDCARRY_FLG")) {
			// //System.out.println("11111111111111111111");
			// // ����ʵ�ʰ�ҩ��������DispenseUnit��
			// // orderAccumMediQty =
			// // StringTool.round(times*order.getDouble("MEDI_QTY"),4);
			// orderLastDspnQty = Math.ceil(StringTool.round(
			// (order.getDouble(
			// "MEDI_QTY") * times / startqty), 4));
			//
			// // M���е�dispenseQty
			// dspnMDispenseQty = orderLastDspnQty;
			// // M���е�DosageQty
			// dspnMDosageQty = orderLastDspnQty;
			// // D���е�DosageQty
			// dspnDDosageQty = dspnMDosageQty;
			// // ����������
			// orderAccumDspnQty = orderLastDspnQty;
			// // �����ۼƿ�ҩ��
			// orderAccumMediQty = orderLastDspnQty * startqty;
			// }
			// �ɲ���
			else if (TypeTool.getBoolean(parmBase.getData("HALF_USE_FLG"))) {
				// System.out.println("22222222222222222");
				// ����ʵ�ʰ�ҩ��������DispenseUnit��
				orderLastDspnQty = StringTool.round((order
						.getDouble("MEDI_QTY")
						* times / startqty), 4);
				// M���е�dispenseQty
				dspnMDispenseQty = orderLastDspnQty;
				// M���е�DosageQty
				dspnMDosageQty = orderLastDspnQty;
				// D���е�DosageQty
				dspnDDosageQty = orderLastDspnQty;
				// ����������
				orderAccumDspnQty = orderLastDspnQty+orderAccumDspnQtyTemp;
				// �����ۼƿ�ҩ��
				// modified by lx ����test
				orderAccumMediQty = StringTool.round(orderLastDspnQty
						* startqty, 4)+orderAccumMediQtyTemp;
				// System.out.println("==orderAccumMediQty5555=="
				// + orderAccumMediQty);

			}
			// ����Ͷҩ
			else if (TypeTool.getBoolean(parmBase.getData("DSPNSTOTDOSE_FLG"))) {
				// System.out.println("3333333333333333333333");
			}
			// ��ҩע�ǣ���浥λ��ҩ��
			else if (TypeTool.getBoolean(order.getData("GIVEBOX_FLG"))) {
				// TODO:Ԥ���з�ҩ�ӿ�
				// System.out.println("44444444444444444444");
			}
			// û������
			else {
				// System.out.println("-----����------");
				// ����ʵ�ʰ�ҩ��������DispenseUnit��
				double temp = StringTool.round(
						(order.getDouble("MEDI_QTY") * times), 4);
				// System.out.println("temp->"+temp);
				// System.out.println("orderAccumDspnQtyTemp="+orderAccumDspnQtyTemp);
				// System.out.println("*====================="+(orderAccumDspnQtyTemp*startqty));
				// System.out.println("orderAccumMediQtyTemp="+orderAccumMediQtyTemp);
				// System.out.println("temp=================="+temp);
				// System.out.println("before="+((
				// temp-(orderAccumDspnQtyTemp*startqty-orderAccumMediQtyTemp))/startqty));
				orderLastDspnQty = Math.ceil((temp - (orderAccumDspnQtyTemp
						* startqty - orderAccumMediQtyTemp))
						/ startqty);
				// System.out.println("orderAccumDspnQtyTemp->" +
				// orderAccumDspnQtyTemp);
				// System.out.println("startqty->" + startqty);
				// System.out.println("orderAccumMediQtyTemp->" +
				// orderAccumMediQtyTemp);
				// System.out.println("orderLastDspnQty->" + orderLastDspnQty);
				// System.out.println("orderLastDspnQty="+orderLastDspnQty);
				// M���е�dispenseQty
				dspnMDispenseQty = orderLastDspnQty;
				// System.out.println("dspnMDispenseQty->"+dspnMDispenseQty);
				// M���е�DosageQty
				dspnMDosageQty = orderLastDspnQty;
				// System.out.println("dspnMDosageQty->"+dspnMDosageQty);
				// D���е�DosageQty
				dspnDDosageQty = orderLastDspnQty;
				// System.out.println("dspnDDosageQty->"+dspnDDosageQty);
				// ����������
				orderAccumDspnQty = orderLastDspnQty+orderAccumDspnQtyTemp;
				// System.out.println("orderAccumDspnQty->"+orderAccumDspnQty);
				// System.out.println("orginal========="+(order.getDouble("MEDI_QTY")*times));
				// System.out.println("orderAccumMediQtyTemp"+orderAccumMediQtyTemp);
				// �����ۼƿ�ҩ��
				orderAccumMediQty = (order.getDouble("MEDI_QTY") * times)+orderAccumMediQtyTemp;
				// System.out.println("orderAccumMediQty1111111->"
				// + orderAccumMediQty);
			}
		}
		// ���ڣ��Ƿ�������ǰ�滹ʣ����ҩ����
		else if ("UD".equalsIgnoreCase(order.getValue("RX_KIND"))) {
			// �鵽��ͬCASE_NOû��DC����ͬҩƷ�����ÿ�ҩ�������÷�ҩ��
			sql = "SELECT A.ACUMMEDI_QTY,A.ACUMDSPN_QTY "
				+ "	FROM ODI_ORDER A "
				+ "	WHERE  A.CASE_NO='"
				+ order.getValue("CASE_NO")
				+ "' AND A.ORDER_NO='" + order.getValue("ORDER_NO") + "'"
				+ " AND A.ORDER_SEQ='" + order.getValue("ORDER_SEQ") + "'";
			// System.out.println("���ÿ�ҩ�������÷�ҩ��.sql=======" + sql);
			// System.out.println("parmBase=" + parmBase);
			// System.out.println("�ɲ���" + parmBase.getData("HALF_USE_FLG"));
			// System.out.println("��⼴��" + parmBase.getData("REUSE_FLG"));
			// System.out.println("������ҩ" +
			// (!parmBase.getBoolean("UDCARRY_FLG")));
			// System.out.println("����Ͷҩ" +
			// TypeTool.getBoolean(parmBase.getData("DSPNSTOTDOSE_FLG")));
			accumParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (accumParm.getErrCode() != 0) {
				// System.out.println("accumParm is wrong" +
				// accumParm.getErrCode());
				return result;
			}
			if (accumParm.getCount() != 1) {
				// System.out.println("accumParm.getCount=" +
				// accumParm.getCount());
				return result;
			}
			orderAccumMediQtyTemp = TypeTool.getDouble(accumParm.getData(
					"ACUMMEDI_QTY", 0));
			orderAccumDspnQtyTemp = TypeTool.getDouble(accumParm.getData(
					"ACUMDSPN_QTY", 0));

			// �ɲ���
			if (TypeTool.getBoolean(parmBase.getData("HALF_USE_FLG"))) {
				// ����ʵ�ʰ�ҩ��������DispenseUnit��

				orderLastDspnQty = StringTool.round((order
						.getDouble("MEDI_QTY")
						* times / startqty), 4);
				// System.out.println("MEDI_QTY=" +
				// order.getDouble("MEDI_QTY"));
				// System.out.println("startQty=" + startqty);
				// System.out.println("times" + times);

				// System.out.println("***=======�ɲ���." +
				// order.getValue("ORDER_DESC") +
				// orderLastDspnQty);
				// M���е�dispenseQty
				dspnMDispenseQty = orderLastDspnQty;
				// M���е�DosageQty
				dspnMDosageQty = orderLastDspnQty;
				// D���е�DosageQty
				dspnDDosageQty = orderLastDspnQty;

				// ����������
				orderAccumDspnQty = orderLastDspnQty + orderAccumDspnQtyTemp;

				// �����ۼƿ�ҩ��
				orderAccumMediQty = orderLastDspnQty * startqty
						+ orderAccumMediQtyTemp;
				// System.out.println("orderAccumDspnQtyTemp=" +
				// orderAccumDspnQtyTemp);
				// System.out.println("orderAccumDspnQty=====" +
				// orderAccumDspnQty);
				// System.out.println("orderAccumMediQtyTemp=" +
				// orderAccumMediQtyTemp);
				// System.out.println("orderAccumMediQty2222====="
				// + orderAccumMediQty);
			}
			// ��������ʹ��ע��ʹ��(��⼴��)�����±�����ֵ˳�����в�ͬ��ֵ���������ı丳ֵ��˳��
			else if (parmBase.getBoolean("REUSE_FLG")) {
				// System.out.println("mediQty==="+order.getDouble("MEDI_QTY"));
				// System.out.println("startQty=="+startqty);
				// System.out.println("///======="+StringTool.round(order.getDouble("MEDI_QTY")/startqty,
				// 4));
				// System.out.println("ceil======"+Math.ceil(StringTool.round(order.getDouble("MEDI_QTY")/startqty,
				// 4)));
				// System.out.println("times====="+times);
				// System.out.println("����");
				// ����ʵ�ʰ�ҩ��������DispenseUnit��
				orderLastDspnQty = Math.ceil(StringTool.round(order
						.getDouble("MEDI_QTY")
						/ startqty, 4))
						* times;
				// System.out.println("MEDI_QTY=" +
				// order.getDouble("MEDI_QTY"));
				// System.out.println("startQty=" + startqty);
				// System.out.println("times" + times);
				//
				// System.out.println("***=======���⼰��." +
				// order.getValue("ORDER_DESC") +
				// orderLastDspnQty);
				// �ۼƿ�ҩ��������MEDI_UNIT��
				orderAccumMediQty = orderLastDspnQty * startqty
						+ orderAccumMediQtyTemp;
				// System.out.println("==orderAccumMediQty3333=="
				// + orderAccumMediQty);

				// M���е�dispenseQty
				dspnMDispenseQty = orderLastDspnQty;
				// M���е�DosageQty
				dspnMDosageQty = dspnMDispenseQty;
				// D���е�DosageQty
				dspnDDosageQty = dspnMDispenseQty;
				// ����������
				orderAccumDspnQty = orderLastDspnQty + orderAccumDspnQtyTemp;
				// System.out.println("orderAccumDspnQtyTemp=" +
				// orderAccumDspnQtyTemp);
				// System.out.println("orderAccumDspnQty=====" +
				// orderAccumDspnQty);
				// System.out.println("orderAccumMediQtyTemp=" +
				// orderAccumMediQtyTemp);
				// System.out.println("orderAccumMediQty=====" +
				// orderAccumMediQty);
			} // סԺ��������ҩע�ǣ����±�����ֵ˳�����в�ͬ��ֵ���������ı丳ֵ��˳��
			else if (parmBase.getBoolean("UDCARRY_FLG")) {
				// System.out.println("orderAccumMediQtyTemp" +
				// orderAccumMediQtyTemp);
				// System.out.println("orderAccumDspnQtyTemp" +
				// orderAccumDspnQtyTemp);
				// �����ۼƿ�ҩ��
				double temp = order.getDouble("MEDI_QTY") * times;
				// �ۼƿ�ҩ��
				orderAccumMediQty = temp + orderAccumMediQtyTemp;
				// shibl 20121108 modify ���ο�ҩ��+�ϴ��ۼ���ҩ��
				orderAccumDspnQty = Math.ceil(StringTool.round(
						((temp / startqty) + orderAccumDspnQtyTemp), 4));

				// ����ʵ����ҩ��
				orderLastDspnQty = orderAccumDspnQty - orderAccumDspnQtyTemp;
				// System.out.println("orderAccumMediQty" + orderAccumMediQty);
				// System.out.println("orderAccumDspnQty" + orderAccumDspnQty);
				// // orderLastDspnQty =
				// Math.ceil(StringTool.round((order.getDouble("MEDI_QTY")*times
				// / startqty), 4));
				// System.out.println("MEDI_QTY=" +
				// order.getDouble("MEDI_QTY"));
				// System.out.println("startQty=" + startqty);
				// System.out.println("times" + times);
				// System.out.println("***=======������ҩ." +
				// order.getValue("ORDER_DESC") +
				// orderLastDspnQty);
				// M���е�dispenseQty
				dspnMDispenseQty = orderLastDspnQty;
				// M���е�DosageQty
				dspnMDosageQty = orderLastDspnQty;
				// D���е�DosageQty
				dspnDDosageQty = dspnMDosageQty;
			}

			// ����Ͷҩ
			else if (TypeTool.getBoolean(parmBase.getData("DSPNSTOTDOSE_FLG"))) {

			}
			// ��ҩע�ǣ���浥λ��ҩ��
			else if (TypeTool.getBoolean(parmBase.getData("GIVEBOX_FLG"))) {
				// TODO:Ԥ���з�ҩ�ӿ�
				double giveBoxQty=parmTrn.getDouble("MEDI_QTY")*parmTrn.getDouble("DOSAGE_QTY");
				// �����ۼƿ�ҩ��
				double temp = order.getDouble("MEDI_QTY") * times;
				// �ۼƿ�ҩ��
				orderAccumMediQty = temp + orderAccumMediQtyTemp;
				// �ۼ���ҩ��
				int number=0;
				while(number*giveBoxQty<orderAccumMediQty){
					number++;
				}
				orderAccumDspnQty = Math.ceil(StringTool.round(number*giveBoxQty/startqty, 4));
				// ����ʵ����ҩ��
				orderLastDspnQty = orderAccumDspnQty - orderAccumDspnQtyTemp;
				// M���е�dispenseQty
				dspnMDispenseQty = orderLastDspnQty;
				// M���е�DosageQty
				dspnMDosageQty = orderLastDspnQty;
				// D���е�DosageQty
				dspnDDosageQty = dspnMDosageQty;
			}
			// û������
			else {
				// System.out.println("here");

				// ����ʵ�ʰ�ҩ��������DispenseUnit��
				// System.out.println("mediQty=" + order.getDouble("MEDI_QTY"));
				// System.out.println("times===" + times);
				double temp = StringTool.round(
						(order.getDouble("MEDI_QTY") * times), 4);
				// System.out.println("û������" + temp);
				// System.out.println("orderAccumDspnQtyTemp=" +
				// orderAccumDspnQtyTemp);
				// System.out.println("*=====================" +
				// (orderAccumDspnQtyTemp * startqty));
				// System.out.println("orderAccumMediQtyTemp=" +
				// orderAccumMediQtyTemp);
				// System.out.println("temp==================" + temp);
				// System.out.println("before=" +
				// ( (temp -
				// (orderAccumDspnQtyTemp * startqty - orderAccumMediQtyTemp)) /
				// startqty));
				orderLastDspnQty = Math.ceil(temp / startqty);
				// System.out.println("orderLastDspnQty="+orderLastDspnQty);
				// M���е�dispenseQty
				dspnMDispenseQty = orderLastDspnQty;
				// M���е�DosageQty
				dspnMDosageQty = orderLastDspnQty;
				// D���е�DosageQty
				dspnDDosageQty = orderLastDspnQty;
				// ����������
				orderAccumDspnQty = orderLastDspnQty + orderAccumDspnQtyTemp;
				// System.out.println("orginal========="+(order.getDouble("MEDI_QTY")*times));
				// System.out.println("orderAccumMediQtyTemp"+orderAccumMediQtyTemp);
				// �����ۼƿ�ҩ��
				orderAccumMediQty = orderLastDspnQty * startqty
						+ orderAccumMediQtyTemp;

				// System.out.println("==orderAccumMediQty==" +
				// orderAccumMediQty);
			}
		}
		// order���LASTDSPN_QTY
		result.put("ORDER_LASTDSPN_QTY", orderLastDspnQty);
		// order���ACUMDSPN_QTY
		result.put("ORDER_ACUMDSPN_QTY", orderAccumDspnQty);
		// order���ACUMMEDI_QTY
		result.put("ORDER_ACUMMEDI_QTY", orderAccumMediQty);
		// System.out.println("acummedi_Qty444444=====" + orderAccumMediQty);
		// M���dispenseQty
		result.put("M_DISPENSE_QTY", dspnMDispenseQty);
		// M���dispenseUnit
		result.put("M_DISPENSE_UNIT", dispenseUnit);
		// M���dosageQty
		result.put("M_DOSAGE_QTY", dspnMDosageQty);
		// M���dosageUnit
		result.put("M_DOSAGE_UNIT", dosageUnit);
		// D���MediQty
		result.put("D_MEDI_QTY", dspnDMediQty);
		// D���MediUnit
		result.put("D_MEDI_UNIT", startUnit);
		// D���dosageQty
		// zhangyong20101214 begin
		result.put("D_DOSAGE_QTY", dspnDDosageQty);
		// zhangyong20101214 end
		// D���dosageUnit
		result.put("D_DOSAGE_UNIT", dosageUnit);

		// System.out.println("result-----"+result);
		return result;
	}

	/**
	 * סԺҩ������ҩƷ�������������ļ�����������
	 * 
	 * @param effDate
	 *            String ���磺200904011530 ��������
	 * @param dcDate
	 *            String ���磺200904051530 ͣ������
	 * @param sdttm
	 *            String ���磺200904021400 ��ҩ����
	 * @param edttm
	 *            String ���磺200904031400 ��ҩ����
	 * @param order
	 *            TParm ODI_ORDER��ȫ�ֶ����ݣ���TParmΪ����
	 * @return result List. List standTime=(List)
	 *         result.get(0);standTime=[200904010800
	 *         ,200904011200,200904011600];Map qty=(Map)result.get(1);
	 *         //order���LASTDSPN_QTY ORDER_LASTDSPN_QTY //order���ACUMDSPN_QTY
	 *         ORDER_ACUMDSPN_QTY //order���ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
	 *         //M���dispenseQty M_DISPENSE_QTY //M���dispenseUnit M_DISPENSE_UNIT
	 *         //M���dosageQty M_DOSAGE_QTY //M���dosageUnit M_DOSAGE_UNIT
	 *         //D���MediQty D_MEDI_QTY //D���MediUnit D_MEDI_UNIT //D���dosageQty
	 *         D_DOSAGE_QTY //D���dosageUnit D_DOSAGE_UNIT
	 */
	public List getOdiUdQty(TParm order, String effDate, String sdttm,
			String edttm) {
		List result = new ArrayList();
		String dcDate = "";
		if (isNullString(effDate) || effDate.length() < 12) {
			return result;
		}
		effDate = effDate.substring(0, 12);
		if (order.getTimestamp("DC_DATE") != null) {
			dcDate = StringTool.getString(order.getTimestamp("DC_DATE"),
					"yyyyMMddHHmmss");
			if (!isNullString(dcDate) && dcDate.length() >= 12)
				dcDate = dcDate.substring(0, 12);
		} else {
			dcDate = "";
		}
		String cat1Type = order.getValue("CAT1_TYPE");
		if (isNullString(cat1Type)) {
			return result;
		}
		// System.out.println("order:" + order);
		// System.out.println("effDate:" + effDate);
		boolean isOperation = !("PHA".equalsIgnoreCase(cat1Type));
		List effRange = new ArrayList();
		// System.out.println("!isOperator="+(!isOperation));

		// ҩƷ
		if (!isOperation) {
			// zhangyong20101123 begin
			TParm freq = this.setFreq(order.getValue("FREQ_CODE"));
			int cycle = TCM_Transform.getInt(freq.getData("CYCLE"));
			if (cycle == 1) {
				if (StringTool.getTimestamp(sdttm, "yyyyMMddHHmm").compareTo(
						order.getTimestamp("EFF_DATE")) < 0) {
					effDate = StringTool.getString(order
							.getTimestamp("EFF_DATE"), "yyyyMMddHHmm");
					sdttm = StringTool.getString(
							order.getTimestamp("EFF_DATE"), "yyyyMMddHHmm");
					effRange.add(effDate);
				} else {
					effRange.add(sdttm);// ǰ̨�Ѿ������ shibl 20121015 modidfy
				}
				effRange.add(edttm);
			} else {
				if (isNullString(order.getValue("LAST_DSPN_DATE"))
						|| order.getTimestamp("EFF_DATE").compareTo(
								StringTool.getTimestamp(sdttm, "yyyyMMddHHmm")) > 0) {
					effDate = StringTool.getString(order
							.getTimestamp("EFF_DATE"), "yyyyMMddHHmm");
				} else {
					effDate = StringTool.getString(order
							.getTimestamp("LAST_DSPN_DATE"), "yyyyMMddHHmm");
				}
				effDate = effDate.substring(0, 12);
				String now = StringTool.getString(SystemTool.getInstance()
						.getDate(), "yyyyMMdd");
				// ��ǰ����ʱ����
				Timestamp date = StringTool.rollDate(StringTool.getTimestamp(
						effDate, "yyyyMMddHHmm"), Long.parseLong((cycle - 1)
						+ ""));
				// System.out.println("2222--" + StringTool.getString(date,
				// "yyyyMMddHHmm"));
				if (order.getTimestamp("EFF_DATE").compareTo(
						StringTool.getTimestamp(sdttm, "yyyyMMddHHmm")) > 0) {
					effRange.add(effDate);
					effRange.add(edttm);
				} else if (now.equals(effDate.substring(0, 8))
						|| StringTool.getString(date, "yyyyMMddHHmm")
								.compareTo(now + arrageTime()[0]) >= 0) {//
					// System.out.println("?????????");
					effRange = getNextDispenseDttm(StringTool.getTimestamp(
							effDate, "yyyyMMddHHmm"));
				} else {
					// System.out.println("date--" + date);
					effRange = getNextDispenseDttm(date);
				}
			}
			// zhangyong20101123 end
		}
		// ��ҩƷ
		else {
			effRange = getDispenseTrtDttmArrange(effDate);
		}
		// System.out.println("effRange:" + effRange);

		if (effRange == null || effRange.size() < 2) {
			return result;
		}
		sdttm = TCM_Transform.getString(effRange.get(0));
		edttm = TCM_Transform.getString(effRange.get(1));
		if (isNullString(sdttm) || isNullString(edttm)) {
			return result;
		}
		// zhangyong20100708 begin
		// String today =
		// StringTool.getString(TJDODBTool.getInstance().getDBTime(),
		// "yyyyMMdd");
		// if (!today.equals(sdttm.substring(0, 8))) {
		// return result;
		// }
		// zhangyong20100708 end
		if (isNullString(effDate) || isNullString(sdttm) || isNullString(edttm)
				|| order == null) {
			return result;
		}

		// �õ�Ƶ��ʱ���
		// System.out.println("1----" + effDate);
		// System.out.println("2----" + dcDate);
		// System.out.println("3----" + sdttm);
		// System.out.println("4----" + edttm);
		// System.out.println("5----" + order.getValue("FREQ_CODE"));
		// System.out.println("6----" + isOperation);
		List standTime = getDispenseArrangeDttm(effDate, dcDate, sdttm, edttm,
				order.getValue("FREQ_CODE"), isOperation, order);
		// System.out.println("standTime:" + standTime);
		if (standTime == null || standTime.size() < 1) {
			return result;
		}
		Map qty = new HashMap();
		qty = getDispenseQty(order, standTime);
		// System.out.println("qty:"+qty);
		if (qty == null) {
			return result;
		}
		result.add(standTime);
		result.add(qty);
		// result.add(sdttm);
		// result.add(edttm);
		return result;
	}

	/**
	 * ����������ʱ������
	 * 
	 * @param effDate
	 *            String �������� 200904011300
	 * @param dcDate
	 *            String ͣ������ 200904021300
	 * @param sdttm
	 *            String ��ҩ������ʱ�� 200904011400
	 * @param edttm
	 *            String ��ҩ������ʱ�� 200904021400
	 * @param order
	 *            Order TParm
	 * @return result List. List standTime=(List)
	 *         result.get(0);standTime=[200904010800
	 *         ,200904011200,200904011600];Map qty=(Map)result.get(1);
	 *         //order���LASTDSPN_QTY ORDER_LASTDSPN_QTY //order���ACUMDSPN_QTY
	 *         ORDER_ACUMDSPN_QTY //order���ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
	 *         //M���dispenseQty M_DISPENSE_QTY //M���dispenseUnit M_DISPENSE_UNIT
	 *         //M���dosageQty M_DOSAGE_QTY //M���dosageUnit M_DOSAGE_UNIT
	 *         //D���MediQty D_MEDI_QTY //D���MediUnit D_MEDI_UNIT //D���dosageQty
	 *         D_DOSAGE_QTY //D���dosageUnit D_DOSAGE_UNIT
	 */
	public List getOdiStQty(String effDate, String dcDate, String sdttm,
			String edttm, TParm order, String level) {
		// System.out.println("======getOdiStQty come in======");
		// System.out.println("1_effDate---" + effDate);
		// System.out.println("2_dcDate---" + dcDate);
		// System.out.println("3_sdttm---" + sdttm);
		// System.out.println("order---" + order);
		List result = new ArrayList();
		List times = new ArrayList();
		Map qty = new HashMap();
		result.add(times);
		result.add(qty);
		String ctzSql = "SELECT CTZ1_CODE,CTZ2_CODE,CTZ3_CODE FROM ADM_INP WHERE CASE_NO='"
				+ order.getValue("CASE_NO") + "'";
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(ctzSql));
		if (ctzParm == null) {
			// System.out.println("ctzParm---Ϊ��");
			return result;
		}
		if (ctzParm.getErrCode() != 0) {
			return result;
		}
		String orderCode = order.getValue("ORDER_CODE");
		TParm sysFee = new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM SYS_FEE WHERE ORDER_CODE='" + orderCode + "'"));
		if (sysFee == null) {
			return result;
		}
		if (sysFee.getErrCode() != 0) {
			return result;
		}
		String ctz1Code = ctzParm.getValue("CTZ1_CODE", 0);
		String ctz2Code = ctzParm.getValue("CTZ2_CODE", 0);
		String ctz3Code = ctzParm.getValue("CTZ3_CODE", 0);
		// BIL bil;

		double ownPrice = BIL.getFee(orderCode, level);
		double nhiPrice = sysFee.getDouble("NHI_PRICE", 0);
		double rate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code, orderCode,
				level);
		// System.out.println("ownPrice="+ownPrice);
		String NULL = "";
		// order���LASTDSPN_QTY
		qty.put("ORDER_LASTDSPN_QTY", 0.0);
		// order���ACUMDSPN_QTY
		qty.put("ORDER_ACUMDSPN_QTY", 0.0);
		// order���ACUMMEDI_QTY
		qty.put("ORDER_ACUMMEDI_QTY", 0.0);
		// M���dispenseQty
		qty.put("M_DISPENSE_QTY", 0.0);
		// M���dispenseUnit
		qty.put("M_DISPENSE_UNIT", NULL);
		// M���dosageQty
		qty.put("M_DOSAGE_QTY", 0.0);
		// M���dosageUnit
		qty.put("M_DOSAGE_UNIT", NULL);
		// D���MediQty
		qty.put("D_MEDI_QTY", 0.0);
		// D���MediUnit
		qty.put("D_MEDI_UNIT", NULL);
		// D���dosageQty
		qty.put("D_DOSAGE_QTY", 0.0);
		// D���dosageUnit
		qty.put("D_DOSAGE_UNIT", NULL);
		// order���las_dspn_date
		qty.put("ORDER_LAST_DSPN_DATE", sdttm);
		if (isNullString(effDate) || isNullString(sdttm) || isNullString(edttm)) {
			System.out
					.println(" isNullString(effDate)|| isNullString(sdttm)|| isNullString(edttm) 1683");
			return result;
		}

		if (order == null) {
			System.out.println("order==null 1688");
			return result;
		}

		String rxKind = order.getValue("RX_KIND");
		// System.out.println("rxKind=====" + rxKind);
		// System.out.println("ehui in the house");
		// ��ʱ
		if ("ST".equalsIgnoreCase(rxKind)) {
			// ȡ��phaBase
			// TParm parmBase=new
			// TParm(TJDODBTool.getInstance().select("SELECT * FROM PHA_BASE WHERE ORDER_CODE='"+order.getValue("ORDER_CODE")+"'"));
			times = new ArrayList();
			Timestamp temp;
			// System.out.println("effDate============"+effDate);
			if (effDate.length() == 12) {
				temp = StringTool.getTimestamp(effDate, "yyyyMMddHHmm");
				// System.out.println("12 temp=========" + temp);
				// luhai modify 2012-04-21 ��ʱҽ��չ��time��4λ begin
				// times.add(StringTool.getString(temp, "yyyyMMddHHmmss"));
				times.add(StringTool.getString(temp, "yyyyMMddHHmm"));
				// luhai modify 2012-04-21 ��ʱҽ��չ��time��4λ end
			} else if (effDate.length() == 14) {
				effDate = effDate.substring(0, 12);
				// System.out.println("14 effDate=" + effDate);
				temp = StringTool.getTimestamp(effDate, "yyyyMMddHHmm");
				// System.out.println("14 temp=========" + temp);
				// luhai modify 2012-04-21 ��ʱҽ��չ��time��4λ begin
				// times.add(StringTool.getString(temp, "yyyyMMddHHmmss"));
				times.add(StringTool.getString(temp, "yyyyMMddHHmm"));
				// luhai modify 2012-04-21 ��ʱҽ��չ��time��4λ end
			} else {
				// System.out.println("effDate =============" + effDate);
			}
			qty = getDispenseQty(order, times);
			// System.out.println("qty=============" + qty);
			result.add(times);
			result.add(qty);
			// System.out.println("is coninuous:"+order.getBoolean("CONTINUOUS_FLG"));
			 if(order.getBoolean("CONTINUOUS_FLG")){
			 double oldDispenseQty=(Double)qty.get("ORDER_LASTDSPN_QTY");
			 TParm trans=this.setTrn(order);
			 double dispenseUnitQty=trans.getDouble("STOCK_QTY");
			 double mediUnitQty=trans.getDouble("MEDI_QTY");
			 //����ʷ��ҩ���ϼ�+���ο�ҩ������ʷ��ҩ���ϼƱȽϣ����ǰ�ߴ�����Ҫ��ҩ������ߴ�����Ҫ��ҩ.
			 //�Ȱ���ʷ��ҩ����dispenseQty)ת���ɿ�ҩ��λ������
			 oldDispenseQty=oldDispenseQty*mediUnitQty;
			 double mediQty=(Double)qty.get("ORDER_ACUMMEDI_QTY");
//			 System.out.println("mediQty==========="+mediQty);
//			 System.out.println("oldDispenseQty===="+oldDispenseQty);
			 if(mediQty<oldDispenseQty){
			 /**
			 * //�����ۼƷ�ҩ��ͬ��ǰ
			 orderAccumDspnQty=order.getDouble("ACUMDSPN_QTY");
			 //���ΰ�ҩ��Ϊ0
			 orderLastDspnQty=0.0;
			 dspnMDispenseQty=0.0;
			 dspnMDosageQty=0.0;
			 orderAccumMediQty=orderAccumMediQty+order.getDouble("ACUMMEDI_QTY");
			 */
			 qty.put("ORDER_LASTDSPN_QTY", 0.0);
//			 qty.put("ORDER_ACUMDSPN_QTY", oldDispenseQty);
//			 qty.put("ORDER_ACUMMEDI_QTY", mediQty);
			 qty.put("M_DISPENSE_QTY", 0.0);
			 qty.put("M_DOSAGE_QTY", 0.0);
			 qty.put("D_DISPENSE_QTY", 0.0);
			 qty.put("D_DOSAGE_QTY", 0.0);
			 }
			 }
		}
		// ����������
		else {

			String cat1Type = order.getValue("CAT1_TYPE");
			boolean isOperation = !"PHA".equalsIgnoreCase(cat1Type);
			if (effDate.length() > 12) {
				effDate = effDate.substring(0, 12);
			}
			long effDateInt = StringTool.getLong(effDate);
			long dcDateInt;
			if (!isNullString(dcDate))
				dcDateInt = StringTool.getLong(dcDate);
			else
				dcDateInt = 999912311240L;
			long sdttmInt = StringTool.getLong(sdttm);
			long edttmInt = StringTool.getLong(edttm);
			// �������ڱ�ͣ�����ڻ���ͣ�������ڰ�ҩ���֮ǰ�����������ڰ�ҩ����֮�����������ڰ�ҩ���䣬�����ü���������
			// System.out.println("=effDateInt==>" + effDateInt);
			// System.out.println("=dcDateInt==>" + dcDateInt);
			// System.out.println("=edttmInt==>" + edttmInt);
			// System.out.println("=sdttmInt==>" + sdttmInt);
			// if (effDateInt >= dcDateInt || effDateInt >= edttmInt ||
			// effDateInt >= sdttmInt && !isOperation) {
			//
			// System.out.println("TotQtyTool->getOdiStQty->����������->�������ڱ�ͣ�����ڻ���ͣ�������ڰ�ҩ���֮ǰ�����������ڰ�ҩ����֮�����������ڰ�ҩ���䣬�����ü���������");
			// return result;
			// }

			String realEndDttm = "";
			//
			if (!isNullString(dcDate) && (dcDateInt <= sdttmInt)) {
				// System.out.println("dcDate---" + dcDate);
				// realEndDttm = StringTool.getString(StringTool.getTimestamp(
				// dcDate, "yyyyMMddHHmm"), "yyyyMMddHHmmss");
				realEndDttm = dcDate;
			} else if (!isNullString(dcDate) && (dcDateInt > sdttmInt)) {
				// System.out.println("sdttm---" + sdttm);
				realEndDttm = sdttm;
			}
			if (isNullString(dcDate))
				realEndDttm = sdttm;

			if (isNullString(cat1Type))
				return result;

			// System.out.println("st isOperation="+isOperation);
			List standing = getStandingTime(order.getValue("FREQ_CODE"),
					isOperation);
			// System.out.println("standing===========" + standing);
			if (standing == null || standing.size() < 1) {
				// System.out.println("1750");
				return result;
			}
			int cycle = this.setFreq(order.getValue("FREQ_CODE")).getInt(
					"CYCLE");
			// System.out.println("cycle============"+cycle);
			// System.out.println("effDate=========="+effDate);
			// System.out.println("realEndDttm======"+realEndDttm);
			List days = new ArrayList();
			if (cycle == 1) {
				// zhangyong20101214 begin
				if (order.getValue("LAST_DSPN_DATE") != null
						&& order.getValue("LAST_DSPN_DATE").length() > 0) {
					String last_dspn_date = StringTool.getString(order
							.getTimestamp("LAST_DSPN_DATE"), "yyyyMMddHHmm");
					// System.out.println("last_dspn_date====" +
					// last_dspn_date);
					effDate = last_dspn_date;
				}
				// zhangyong20101214 end
				days.add(effDate);
				// SHIBL 20120414 ADD
				String nsCheckCode = order.getValue("NS_CHECK_CODE");
				if (!this.isNullString(nsCheckCode)) {
					if (!isNullString(dcDate))
						days.add(sdttm);
				} else {
					days.add(realEndDttm);
				}
				// System.out.println("realEndDttm=" + realEndDttm);
				// System.out.println("days---" + days);
				// System.out.println("standing---" + standing);
				// System.out.println("effDate---" + effDate);
				// System.out.println("realEndDttm---" + realEndDttm);
				// System.out.println("edttm---" + edttm);
				// ͨ���ж��Ƿ��л�ʿ��˱���ж��Ƿ�Ϊ���DC���̣�����ǣ���ȥ���в�����DC_DATE֮�����е�M��START_DTTM��D��ORDER_DATE��ORDER_DATETIME
				if (!this.isNullString(nsCheckCode)
						&& order.getValue("CAT1_TYPE").equals("PHA")) {
					if (!isNullString(dcDate))
						times = getDttm(days, standing, realEndDttm, edttm);
				} else
					times = getDttm(days, standing, effDate, realEndDttm);
				// System.out.println("times=======" + times);
			} else {
				// System.out.println("effDate---" + effDate);
				// System.out.println("realEndDttm---" + realEndDttm);
				// System.out.println("cycle---" + cycle);
				// System.out.println("standing---" + standing);
				// System.out.println("-----------------------------");
				times = getMutilCycleStDttm(effDate, realEndDttm, cycle,
						standing);
				// System.out.println("times===" + times);
			}
			if (times == null || times.size() < 1) {
				result.add(times);
				result.add(qty);
				// System.out.println("times----" + times);
				// System.out.println("times----" + times.size());
				// System.out.println("result----" + result);
				return result;
			}
			// System.out.println("before getDispenseQty");
			qty = getDispenseQty(order, times);
			if (qty == null) {
				// System.out.println("qty is null");
				return result;
			}

			qty.put("ORDER_LAST_DSPN_DATE", sdttm);
		}
		// System.out.println("return->ORDER_ACUMMEDI_QTY"+qty.get("ORDER_ACUMMEDI_QTY"));
		// System.out.println("return->ORDER_ACUMDSPN_QTY"+qty.get("ORDER_ACUMDSPN_QTY"));

		double ownAmtM = StringTool.round(BIL.getFee(orderCode, TypeTool
				.getDouble(qty.get("M_DOSAGE_QTY")), level), 2);
		double totAmtM = StringTool.round(ownAmtM * rate, 2);
		// System.out.println("----"+qty);
		// System.out.println("in_1---" +
		// TypeTool.getDouble(qty.get("D_DOSAGE_QTY")));
		// System.out.println("in_2---" + rate);
		// System.out.println("in_3---" + level);
		// System.out.println("in4---"+orderCode);
		// System.out.println("out---" + BIL.getFee(orderCode,
		// TypeTool.getDouble(qty.
		// get("D_DOSAGE_QTY")) * rate, level));
		double totAmtD = StringTool.round(BIL.getFee(orderCode, TypeTool
				.getDouble(qty.get("D_DOSAGE_QTY"))
				* rate, level), 2);
		result = new ArrayList();
		result.add(times);
		result.add(qty);
		qty.put("OWN_PRICE", ownPrice);
		qty.put("NHI_PRICE", nhiPrice);
		qty.put("TOT_AMTM", totAmtM);
		qty.put("TOT_AMTD", totAmtD);
		qty.put("DISCOUNT_RATE", rate);
		qty.put("OWN_AMT", ownAmtM);
		// System.out.println("---------------" + times.size());
		// System.out.println("---------------" + result);
		return result;
	}

	/**
	 * ����Ƶ�ε�cycle>1��Ƶ�εĲʹ�ʱ��� 0
	 * 
	 * @param effDate
	 *            String 200905200130
	 * @param realEndDttm
	 *            String 200905211400
	 * @param standing
	 *            List [0900] ��Ƶ�εĲʹ�ʱ�䡣
	 * @param freqCode
	 *            QOD
	 * @return result List result.get(0)=200905200900...
	 */
	public List getMutilCycleDttm(String effDate, String realEndDttm,
			int cycle, List standing, String sdttm) {
//		 System.out.println("1.effDate====" + effDate);
//		 System.out.println("2.realEndDttm====" + realEndDttm);
//		 System.out.println("3.cycle====" + cycle);
//		 System.out.println("4.standing====" + standing);
//		 System.out.println("5.sdttm====" + sdttm);
		List result = new ArrayList();
		if (isNullString(effDate) || isNullString(realEndDttm)) {
			return result;
		}

		if (standing == null || standing.size() < 1) {
			return result;
		}

		if (effDate.length() != 12 || realEndDttm.length() != 12) {
			return result;
		}
		// Ĭ��CYCLE>1��Ƶ�ε�standingtimeֻ��һ��
		try {
			// //ȡ�ÿ�ʼ���ںͽ�������
			// String startDate = effDate.substring(0, 8);
			// String startTime = (String) standing.get(0);
			// while (true) {
			// System.out.println("-----------------------------------");
			// System.out.println("effDate="+effDate);
			// System.out.println("realEndDttm="+realEndDttm);
			// System.out.println("startDate+startTime="+(startDate+startTime));
			// System.out.println("cycle====="+cycle);
			// Timestamp effDttm = StringTool.getTimestamp(effDate,
			// "yyyyMMddHHmm");
			// Timestamp endDttm = StringTool.getTimestamp(realEndDttm,
			// "yyyyMMddHHmm");
			// Timestamp tempDttm = StringTool.getTimestamp(startDate +
			// startTime, "yyyyMMddHHmm");
			// Long sTime = tempDttm.getTime();
			// Long tempTime = effDttm.getTime();
			// Long eTime = endDttm.getTime();
			//
			// System.out.println("sTime=tempDttm=" + tempDttm);
			// System.out.println("tempTime=effDttm=" + effDttm);
			// System.out.println("eTime=endDttm=" + endDttm);
			// System.out.println("tempTime<sTime=" + (tempTime < sTime));
			// System.out.println("tempTime>=sTime&&tempTime<eTime" +
			// (tempTime >= sTime && tempTime < eTime));
			// System.out.println("tempTime=" + tempTime);
			// System.out.println("sTime====" + sTime);
			// System.out.println("eTiem====" + eTime);
			//
			// // if (tempTime >= sTime && tempTime < eTime) {
			// //
			// // }
			//
			// //System.out.println("");
			// // if (tempTime < sTime) {
			// // if (cycle < 1) {
			// // startDate = StringTool.getString(StringTool.rollDate(
			// // StringTool.getTimestamp(effDate, "yyyyMMddHHmm"),
			// // Long.parseLong("1")), "yyyyMMddHHmm");
			// // result.add(startDate);
			// // return result;
			// // }
			// // startDate = StringTool.getString(StringTool.rollDate(
			// // StringTool.getTimestamp(startDate, "yyyyMMdd"),
			// // Long.parseLong(cycle + "")), "yyyyMMdd");
			// // count++;
			// // continue;
			// // }
			// if (tempTime > sTime && tempTime <= eTime) {
			// result.add(StringTool.getString(tempDttm, "yyyyMMddHHmm"));
			// if (cycle < 1) {
			// cycle = 1;
			// }
			// startDate = StringTool.getString(StringTool.rollDate(
			// StringTool.getTimestamp(startDate, "yyyyMMdd"),
			// Long.parseLong(cycle + "")), "yyyyMMdd");
			// System.out.println("startDate---2--"+startDate);
			// }
			// else {
			// break;
			// }
			// }
			// return result;
			// zhangyong20101123 begin
			// ȡ��ִ��ʱ��
			String startDate = effDate.substring(0, 8);

			// startDate = StringTool.getString(StringTool.rollDate(StringTool.
			// getTimestamp(startDate, "yyyyMMdd"), 1L), "yyyyMMdd");
			// effDate = StringTool.getString(StringTool.rollDate(
			// StringTool.getTimestamp(effDate, "yyyyMMddHHmm"),
			// Long.parseLong( cycle + "")), "yyyyMMddHHmm");

			String startTime = (String) standing.get(0);
			// ȡ�ý�������
			Timestamp endDttm = StringTool.getTimestamp(realEndDttm,
					"yyyyMMddHHmm");
			Timestamp effDateT = StringTool.getTimestamp(effDate,
			"yyyyMMddHHmm");	
			while (true) {
				Timestamp startDttm = StringTool.getTimestamp(startDate
						+ startTime, "yyyyMMddHHmm");
				Long start_Time = startDttm.getTime();
				Long end_Time = endDttm.getTime();
				// ��ʼʱ��
				Timestamp effDttm = StringTool.getTimestamp(sdttm,
						"yyyyMMddHHmm");
				Long tempTime = effDttm.getTime();

				// System.out.println("startDttm----"+startDttm);

				if (start_Time >= end_Time) {
					break;
				} else if (start_Time > tempTime && start_Time < end_Time&&start_Time>effDateT.getTime()) {
					result.add(StringTool.getString(startDttm, "yyyyMMddHHmm"));
					if (cycle < 1) {
						cycle = 1;
					}
					startDate = StringTool.getString(StringTool.rollDate(
							StringTool.getTimestamp(startDate, "yyyyMMdd"),
							Long.parseLong(cycle + "")), "yyyyMMdd");
					continue;
				} else {
					if (cycle < 1) {
						cycle = 1;
					}
					startDate = StringTool.getString(StringTool.rollDate(
							StringTool.getTimestamp(startDate, "yyyyMMdd"),
							Long.parseLong(cycle + "")), "yyyyMMdd");
					continue;
				}
			}
			// System.out.println("result----"+result);
			return result;
			// zhangyong20101123 end
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * ����Ƶ�ε�cycle>1��Ƶ�εĲʹ�ʱ��� 0
	 * 
	 * @param effDate
	 *            String 200905200130
	 * @param realEndDttm
	 *            String 200905211400
	 * @param standing
	 *            List [0900] ��Ƶ�εĲʹ�ʱ�䡣
	 * @param freqCode
	 *            QOD
	 * @param freqTime
	 *            ����
	 * @return result List result.get(0)=200905200900...
	 */
	public List getMutilCycleStDttm(String effDate, String realEndDttm,
			int cycle, List standing) {
		List result = new ArrayList();
		if (isNullString(effDate) || isNullString(realEndDttm)) {
			return result;
		}

		if (standing == null || standing.size() < 1) {
			return result;
		}

		if (effDate.length() != 12 || realEndDttm.length() != 12) {
			return result;
		}
		// Ĭ��CYCLE>1��Ƶ�ε�standingtimeֻ��һ��

		// ȡ�ÿ�ʼ���ںͽ�������
		String startDate = effDate.substring(0, 8);
		String startTime = (String) standing.get(0);
		// System.out.println("realEndDttm" + realEndDttm);
		// System.out.println("effDate====" + effDate);
		// System.out.println("realEndDttm.time=" +
		// StringTool.getTimestamp(realEndDttm, "yyyyMMdd"));
		// System.out.println("effDate.timr=====" +
		// StringTool.getTimestamp(effDate, "yyyyMMdd"));
		// System.out.println("reverse dayDiff==" +
		// StringTool.getDateDiffer(
		// StringTool.getTimestamp(effDate, "yyyyMMddHHmm"),
		// StringTool.getTimestamp(realEndDttm, "yyyyMMddHHmm")));
		// System.out.println("realEndDttm=" + realEndDttm);
		// System.out.println("effDate=" + effDate);
		int dayDiff = StringTool.getDateDiffer(StringTool.getTimestamp(
				realEndDttm.substring(0, 12), "yyyyMMddHHmm"), StringTool
				.getTimestamp(effDate.substring(0, 12), "yyyyMMddHHmm"));
		// System.out.println("cycle=" + cycle);
		// System.out.println("dayDiff=" + dayDiff);
		if (cycle > dayDiff) {
			for (int i = 0; i <= dayDiff; i++) {
				for (int j = 0; j < standing.size(); j++) {
					String tempDate = startDate + standing.get(j);
					// System.out.println("tempDate=" + tempDate);
					// System.out.println("effDate=" + effDate);
					// System.out.println("realEndDttm=" + realEndDttm);
					if (TypeTool.getLong(tempDate) >= TypeTool.getLong(effDate)
							&& TypeTool.getLong(tempDate) < TypeTool
									.getLong(realEndDttm)) {
						result.add(startDate + standing.get(j));
					}
				}
				startDate = StringTool.getString(StringTool.rollDate(StringTool
						.getTimestamp(startDate, "yyyyMMdd"), 1L), "yyyyMMdd");
				// System.out.println("startDate=" + startDate);
			}
		} else {
			result = getT(effDate, realEndDttm, standing, cycle);
			// System.out.println("result=" + result);
		}
		// System.out.println("result----"+result);
		return result;
	}

	/**
	 * 
	 * @param date
	 *            String "201001221530"
	 * @param list
	 *            List
	 * @return String
	 */
	public static String getTimePoint(String date, List list, int l, boolean one) {
		String s = date.substring(8);
		for (int i = 0; i < list.size(); i++) {
			String s1 = (String) list.get(i);
			if (s.compareTo(s1) < 0)
				return date.substring(0, 8) + s1;
		}
		Timestamp d = StringTool.getTimestamp(date.substring(0, 8), "yyyyMMdd");
		if (one)
			d = StringTool.rollDate(d, 1);
		else
			d = StringTool.rollDate(d, l);
		return StringTool.getString(d, "yyyyMMdd") + list.get(0);
	}

	public static List getT(String d0, String d1, List list, int l) {
		List r = new ArrayList();
		String t = getTimePoint(d0, list, l, true);
		while (t.compareTo(d1) < 0) {
			r.add(t);
			t = getTimePoint(t, list, l, false);
		}
		return r;
	}

	/**
	 * @param effDate
	 *            String 200904011350
	 * @return result List ��������ʱ��İ�ҩ����ʱ��
	 */
	public List getDispenseDttmArrange(String effDate) {
		List result = new ArrayList();
		if (isNullString(effDate)) {
			// System.out.println("TotQtyTool->getDispenseDttmArrange->effDate is null");
			return result;
		}
		TParm parm = getSchDate(effDate);
		if (parm.getErrCode() != 0) {
			return new ArrayList();
		}
		String[] times = arrageTime();
		String sdttm = StringTool.getString(parm.getTimestamp("SCH_DATE", 0),
				"yyyyMMdd")
				+ times[0];
		String edttm = StringTool.getString(parm.getTimestamp("SCH_DATE", 1),
				"yyyyMMdd")
				+ times[1];
		// System.out.println("sdttm->"+sdttm);
		// System.out.println("edttm->"+edttm);
		result.add(sdttm);
		result.add(edttm);
		return result;
	}

	/**
	 * ȡ������ڱ���ʱ�����һ�ΰ�ҩʱ��
	 * 
	 * @param effDate
	 * @return
	 */
	public List getLastDspnDttm(String effDate) {

		List result = new ArrayList();
		if (isNullString(effDate)) {
			// System.out.println("TotQtyTool->getDispenseDttmArrange->effDate is null");
			return result;
		}
		String sql = "SELECT MAX(SCH_DATE) SCH_DATE FROM ODI_DISTDATE WHERE DIST_FLG='Y' AND SCH_DATE<TO_DATE('"
				+ effDate + "','YYYYMMDDHH24MI')";
		// System.out.println("getLastDspnDttm.sql=" + sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() != 0) {
			return new ArrayList();
		}
		String[] times = arrageTime();
		String sdttm = StringTool.getString(parm.getTimestamp("SCH_DATE", 0),
				"yyyyMMdd")
				+ times[0];
		// zhangyong20100703 begin
		String edttm = effDate.substring(0, 8) + times[1];
		result.add(sdttm);
		result.add(edttm);
		// zhangyong20100703 end
		return result;

	}

	/**
	 * ����չ�����ݿ������ڼ��������ʱ��
	 * 
	 * @param effDate
	 * @return
	 */
	public List getDispenseTrtDttmArrange(String effDate) {
		List result = new ArrayList();
		if (isNullString(effDate)) {
			System.out.println("TotQtyTool->getDispenseDttmArrange->effDate is null");
			return result;
		}
		Timestamp nextdate =StringTool.getTimestamp(effDate.substring(0, 8), "yyyyMMdd");
		String sdttmYMD = StringTool.getString(nextdate, "yyyyMMdd");
		String sdttm = sdttmYMD + "0000";
		String nnextdate= StringTool.getString(StringTool.rollDate(nextdate, 1L), "yyyyMMdd");
		String edttm = nnextdate + "0000";
		result.add(sdttm);
		result.add(edttm);
		return result;
	}
	/**
	 * ���ݸ���ʱ��ȡ�ð�������ĺ�7�����ҩ����
	 * 
	 * @param effDate
	 *            String ���磺20090404
	 * @return TParm
	 */
	public TParm getSchDate(String effDate) {
		if (effDate.length() >= 12)
			effDate = effDate.substring(0, 8);
		// ��ҩ����
		// ȡ�ý�����磺��Timestamp��2009-03-30��2009-03-31����������ȡ��7���ڣ�edit:Ӧ���޶���7��������ڡ����ģ�
		// String endEffDate =
		// StringTool.getString(StringTool.rollDate(StringTool.
		// getTimestamp(effDate, "yyyyMMdd"), 7), "yyyyMMdd");
		String sql = " SELECT * FROM ODI_DISTDATE"
				+ " 		WHERE SCH_DATE>=TO_DATE('" + effDate
				+ "','YYYYMMDDHH24MI') " +
				// "		AND SCH_DATE<TO_DATE('" + endEffDate +
				// "','YYYYMMDDHH24MI') " +
				"		AND DIST_FLG='Y' " + "		ORDER BY SCH_DATE";
		// System.out.println("TotQtyTool->getDispenseDttmArrange->sql:"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("TotQtyTool->getDispenseDttmArrange->parm"+parm);

		return parm;
	}

	/**
	 * ���ݸ���ʱ�䣬��������ڸ�ʱ�����һ������İ�ҩʱ���
	 * 
	 * @param now
	 *            Timestamp
	 * @return result ArrayList ��������(200904051400,200904061400)
	 */
	public List getNextDispenseDttm(Timestamp now) {
		List result = new ArrayList();
		if (now == null) {
			return result;
		}
		TParm parm = this.getSchDate(StringTool.getString(now, "yyyyMMdd"));
		// System.out.println("parm=====" + parm);
		if (parm.getErrCode() != 0) {
			// System.out.println("!=7");
			return result;
		}
		String[] times = arrageTime();
		String sdttm = StringTool.getString(parm.getTimestamp("SCH_DATE", 0),
				"yyyyMMdd")
				+ times[0] + "00";

		String edttm = StringTool.getString(parm.getTimestamp("SCH_DATE", 1),
				"yyyyMMdd")
				+ times[1] + "00";
		Timestamp timeSdttm = StringTool.getTimestamp(sdttm, "yyyyMMddHHmmss");
		// System.out.println("now======"+now);
		// System.out.println("timeSddtm======"+timeSdttm);
		// System.out.println("now.getTiem======"+now.getTime());
		// System.out.println("sdttm.gettime======"+timeSdttm.getTime());
		// System.out.println("differ======"+(now.getTime()-timeSdttm.getTime()));
		if (now.getTime() - timeSdttm.getTime() >= 0) {
			sdttm = StringTool.getString(parm.getTimestamp("SCH_DATE", 1),
					"yyyyMMdd")
					+ times[0];
			edttm = StringTool.getString(parm.getTimestamp("SCH_DATE", 2),
					"yyyyMMdd")
					+ times[1];
		} else {
			sdttm = StringTool.getString(parm.getTimestamp("SCH_DATE", 0),
					"yyyyMMdd")
					+ times[0];
			edttm = StringTool.getString(parm.getTimestamp("SCH_DATE", 1),
					"yyyyMMdd")
					+ times[1];
		}
		// System.out.println("getNextDispenseDttm.sdttm======" + sdttm);
		// System.out.println("getNextDispenseDttm.edttm======" + edttm);
		result.add(sdttm);
		result.add(edttm);
		return result;
	}

	/**
	 * ��������������������������Ƽ��á����и����mediUnit����ҩ��λ����Ҫ����sys_fee�е�unit_code��Ҳ���ǼƼ۵�λ
	 * 
	 * @param mediQty
	 *            double �ԼƼ۵�λΪ��λ������
	 * @param mediUnit
	 *            String ��ҩ��λ��Ӧ����sys_fee��unit_code��
	 * @param freqCode
	 *            String Ƶ�δ���
	 * @param takeDays
	 *            int �շ�
	 * @return parm TParm
	 *         parm.getValue("DOSAGE_UNIT")����ҩ��λ��parm.getDouble("DOSAGE_QTY"
	 *         ):��ҩ����
	 */
	public static TParm getIbsQty(double mediQty, String mediUnit,
			String freqCode, int takeDays) {
		TParm parm = new TParm();
		if (isNullString(mediUnit) || isNullString(freqCode)) {
			return parm;
		}
		if (takeDays < 0) {
			return parm;
		}
		parm.setData("DOSAGE_UNIT", mediUnit);
		TParm parmTrn = new TParm(TJDODBTool.getInstance().select(
				GET_TIMES + " WHERE FREQ_CODE='" + freqCode + "'"));
		// System.out.println("TotQtyTool->getIbsQty->parmTrn"+parmTrn);
		int times = parmTrn.getInt("FREQ_TIMES", 0);
		double dosageQty = StringTool.round(mediQty * times * takeDays, 4);
		parm.setData("DOSAGE_QTY", dosageQty);
		return parm;
	}

	/**
	 * �ж��ǲ��ǿ��ַ���
	 * 
	 * @param s
	 * @return boolean Y:�գ�N:����
	 */
	public static boolean isNullString(String s) {
		boolean result = (s == null);
		if (s == null) {
			return true;
		}
		if (s.length() < 1) {
			return true;
		}
		return false;
	}

	/**
	 * ��ʱ���������������
	 * 
	 * @param order
	 *            TParm
	 * @param execTime
	 *            String yyyyMMddHHmm
	 * @return result List.List time=result.get(0);Map qty=result.get(1);List
	 *         startToEnd=result.get(2).qty�еĲ�����
	 *         //ORDER_LASTDSPN_QTY:order���LASTDSPN_QTY
	 *         //ORDER_ACUMDSPN_QTY��order���ACUMDSPN_QTY
	 *         //ORDER_ACUMMEDI_QTY��order���ACUMMEDI_QTY
	 *         //M_DISPENSE_QTY��M���dispenseQty //M_DISPENSE_UNIT��M���dispenseUnit
	 *         //M_DOSAGE_QTY��M���dosageQty //M_DOSAGE_UNIT��M���dosageUnit
	 *         //D_MEDI_QTY��D���MediQty //D_MEDI_UNIT��D���MediUnit
	 *         //D_DOSAGE_QTY��D���dosageQty //D_DOSAGE_UNIT��D���dosageUnit
	 *         //order���las_dspn_date
	 *         //ORDER_LAST_DSPN_DATE�����õ�����ҩʱ������������ʱ�����һչ��ʱ�������ҽ����DCʱ��
	 *         ��������ʱ������һ��
	 *         //startToEnd.get(0)������yyyymmddhhmmss�Ŀ�ʼʱ��.M���START_DTTM
	 *         //startToEnd.get(1):����yyyymmddhhmmss�Ľ���ʱ��.M���END_DTTM
	 */
	public List getOdiTrtStQty(TParm order, String execTime, String level) {
		// System.out.println("order===="+order);
		// System.out.println("execTime===="+execTime);
		List result = new ArrayList();
		if (order == null) {
			// System.out.println("order is null");
			return null;
		}
		if (order.getCount() == 0) {
			// System.out.println("order.count =0");
			return null;
		}
		if (this.isNullString(execTime)) {
			// System.out.println("execTime is null");
			return null;
		}
		if (execTime.length() != 12) {
			// System.out.println("execTime.length !=12");
			return null;
		}
		String nsCheckCode = order.getValue("NS_CHECK_CODE");
		// System.out.println("ncCheckCode==========="+nsCheckCode);
		// ͨ���ж��Ƿ��л�ʿ��˱���ж��Ƿ�Ϊ���DC���̣�����ǣ���ȥ���в�����DC_DATE֮�����е�M��START_DTTM��D��ORDER_DATE��ORDER_DATETIME
		if (!this.isNullString(nsCheckCode)) {
			// System.out.println("dc int house");
			result = getOdiTrtDcQty(order);
			// System.out.println("result---" + result);
			return result;
		}
		// System.out.println("effDate==="+order.getData("EFF_DATE"));
		String orderDate = StringTool.getString(TypeTool.getTimestamp(order
				.getData("EFF_DATE")), "yyyyMMddHHmm");
		// System.out.println("orderDate="+orderDate);

		String lastDspnDate = "";
		List effdispenseDttm=getDispenseTrtDttmArrange(execTime);
		long orderDateLong = TypeTool.getLong(orderDate);
		long execTimeLong = TypeTool.getLong(effdispenseDttm.get(0));
		long dcDateLong = 0;
		String dcDate = StringTool.getString(TypeTool.getTimestamp(order
				.getData("DC_DATE")), "yyyyMMddHHmm");
		// System.out.println("dcDate==="+dcDate);
		if (!this.isNullString(dcDate)) {
			dcDateLong = TypeTool.getLong(dcDate);
			// System.out.println("dcDateLong="+dcDateLong);
			// ���ʱ�䲻������DCʱ��
			// if (execTimeLong >= dcDateLong) {
			// System.out.println("���ʱ�䲻������DCʱ��");
			// return null;
			// }
		}
		if (!this.isNullString(dcDate) && execTimeLong >= dcDateLong) {
			// ���ʱ������DCʱ��
			lastDspnDate = dcDate;
		} else {
			// endDate,��DC_DATE����������ʱ�����һչ��ʱ���н����һ����
			// lastDspnDate = StringTool.getString(StringTool.rollDate(
			// StringTool.getTimestamp(execTime, "yyyyMMddHHmm"), 1L),
			// "yyyyMMdd") +
			// "0000";
			// zhangyong20101121 begin
			lastDspnDate = StringTool.getString(StringTool.getTimestamp(
					effdispenseDttm.get(0).toString(), "yyyyMMddHHmm"), "yyyyMMdd")
					+ "2359";
			// zhangyong20101121 end
		}
		// System.out.println("endDate="+lastDspnDate);
		String endDate = lastDspnDate;
		long endDateLong = TypeTool.getLong(endDate);
		if (dcDateLong > 0) {
			endDate = (dcDateLong < endDateLong) ? dcDate : endDate;
		}
		String freqCode = order.getValue("FREQ_CODE");
		if (this.isNullString(freqCode)) {
			// System.out.println("freqCode is null");
			return null;
		}
		// ȡ��Ƶ��ʱ���

		// System.out.println("orderDate---"+orderDate);
		// System.out.println("endDate---"+endDate);
		// System.out.println("freqCode---"+freqCode);
		List timeList = getTimeList(orderDate, endDate, freqCode);
		// System.out.println("timeList---" + timeList);
		if (timeList == null) {
			timeList = new ArrayList();
			System.out.println("timeList is null");
			// return null;
		}
		int time = timeList.size();
		if (time == 0) {
			System.out.println("timeList .size =0");
			// return null;
		}
		Map qty = new HashMap();
		double dosageQty = TypeTool.getDouble(order.getData("MEDI_QTY"));
		result.add(timeList);
		double tempQty = StringTool.round(dosageQty * time, 2);
		String tempUnit = order.getValue("MEDI_UNIT");

		String ctzSql = "SELECT CTZ1_CODE,CTZ2_CODE,CTZ3_CODE FROM ADM_INP WHERE CASE_NO='"
				+ order.getValue("CASE_NO") + "'";
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(ctzSql));
		if (ctzParm == null) {
			// System.out.println("ctzParm");
			return null;
		}
		if (ctzParm.getErrCode() != 0) {
			// System.out.println("ctzParm.getErrCode()");
			return null;
		}
		TParm sysFee = new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM SYS_FEE WHERE ORDER_CODE='"
						+ order.getValue("ORDER_CODE") + "'"));
		if (sysFee == null) {
			System.out.println("sysFee");
			return result;
		}
		if (sysFee.getErrCode() != 0) {
			System.out.println("sysFee.getErrCode");
			return result;
		}
		double ownPrice = BIL.getFee(order.getValue("ORDER_CODE"), level);

		double nhiPrice = sysFee.getDouble("NHI_PRICE", 0);
		double rate = BIL.getRate(ctzParm.getValue("CZT1_CODE", 0), ctzParm
				.getValue("CZT2_CODE", 0), ctzParm.getValue("CZT3_CODE", 0),
				order.getValue("ORDER_CODE"), level);
		double ownAmtM = StringTool.round(BIL.getFee(order
				.getValue("ORDER_CODE"), tempQty, level), 2);
		double totAmtM = StringTool.round(ownAmtM * rate, 2);
		double totAmtD = StringTool.round(BIL.getFee(order
				.getValue("ORDER_CODE"), TypeTool.getDouble(order
				.getData("MEDI_QTY"))
				* rate, level), 2);
		// order���LASTDSPN_QTY
		qty.put("ORDER_LASTDSPN_QTY", tempQty);
		// order���ACUMDSPN_QTY
		qty.put("ORDER_ACUMDSPN_QTY", tempQty);
		// order���ACUMMEDI_QTY
		qty.put("ORDER_ACUMMEDI_QTY", tempQty);
		// M���dispenseQty
		qty.put("M_DISPENSE_QTY", tempQty);
		// M���dispenseUnit
		qty.put("M_DISPENSE_UNIT", tempUnit);
		// M���dosageQty
		
		//modify by yangjj 20150610
		qty.put("M_DOSAGE_QTY", dosageQty* time);
		//qty.put("M_DOSAGE_QTY", TypeTool.getDouble(order.getData("DOSAGE_QTY"))
			//	* time);
		
		// System.out.println("M_DOSAGE_QTY------" +
		// TypeTool.getDouble(order.getData("DOSAGE_QTY")) * time);
		// M���dosageUnit
		qty.put("M_DOSAGE_UNIT", tempUnit);
		// D���MediQty
		qty.put("D_MEDI_QTY", TypeTool.getDouble(order.getData("MEDI_QTY")));
		// D���MediUnit
		qty.put("D_MEDI_UNIT", tempUnit);
		// D���dosageQty
		
		//modify by yangjj 20150610
		qty.put("D_DOSAGE_QTY", dosageQty);
		//qty
				//.put("D_DOSAGE_QTY", TypeTool.getDouble(order
					//	.getData("DOSAGE_QTY")));
		// D���dosageUnit
		qty.put("D_DOSAGE_UNIT", tempUnit);
		// order���las_dspn_date
		// ���õ�����ҩʱ������������ʱ�����һչ��ʱ�������ҽ����DCʱ�䡣������ʱ������һ��
		// System.out.println("lastDspnDate=" +
		// StringTool.getTimestamp(lastDspnDate, "yyyyMMddHHmm"));
		// System.out.println("lastDspnDate="+lastDspnDate);
		qty.put("ORDER_LAST_DSPN_DATE", StringTool.getTimestamp(lastDspnDate,
				"yyyyMMddHHmm"));
		/**
		 * //M���OWN_PRICE OWN_PRICE //M���NHI_PRICE NHI_PRICE //M���OWN_AMT
		 * OWN_AMT //M���TOT_AMT TOT_AMTM //M���DISCOUNT_RATE DISCOUNT_RATE
		 * //D���TOT_AMT TOT_AMTD
		 */
		qty.put("OWN_PRICE", ownPrice);
		qty.put("NHI_PRICE", nhiPrice);
		qty.put("TOT_AMTM", totAmtM);
		qty.put("TOT_AMTD", totAmtD);
		qty.put("DISCOUNT_RATE", rate);
		qty.put("OWN_AMT", ownAmtM);
		List startToEnd = new ArrayList();
		startToEnd.add(orderDate);
		startToEnd.add(endDate);
		result.add(qty);
		result.add(startToEnd);
		// System.out.println("result111="+result);
		return result;
	}

	/**
	 * ���ݸ����ORDER����ȥ���в���DC_DATE֮���M��D���е����ݣ���ɺ�getOdiTrtStQty�ķ�����ͬ��ʽ�Ĳ�������
	 * 
	 * @param order
	 * @return
	 */
	public List getOdiTrtDcQty(TParm order) {
		List result = new ArrayList();
		Timestamp dcDateTime = order.getTimestamp("DC_DATE");
		String dcDate = StringTool.getString(dcDateTime, "yyyyMMddHHmm");
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		long dcDateLong = dcDateTime.getTime();
		long nowLong = now.getTime();
		if (nowLong < dcDateLong) {
			System.out.println("���DCʱ�䲻������DC_DATE");
			return null;
		}
		String caseNo = order.getValue("CASE_NO");
		String orderNo = order.getValue("ORDER_NO");
		int orderSeq = order.getInt("ORDER_SEQ");
		// ����M����START_DTTM����DC_DATE������
		String getM = "SELECT START_DTTM " + "	FROM ODI_DSPNM "
				+ "	WHERE CASE_NO='" + caseNo + "' " + "		  AND ORDER_NO='"
				+ orderNo + "' " + "		  AND ORDER_SEQ= " + orderSeq + ""
				+ "		  AND START_DTTM<='" + dcDate + "'"
				+ "	ORDER BY START_DTTM";
		// System.out.println("getM=============="+getM);
		TParm mParm = new TParm(TJDODBTool.getInstance().select(getM));
		if (mParm.getErrCode() != 0) {
			// System.out.println("mParm is wrong" + mParm.getErrText());
			return null;
		}
		int count = mParm.getCount();
		// if (count < 0) {
		// System.out.println("mParm.count<=0");
		// return result;
		// }
		List startDttm = new ArrayList();
		for (int i = 0; i < count; i++) {
			startDttm.add(mParm.getValue("START_DTTM", i));
		}
		String dcQtySql = "SELECT TABLE1.DOSAGE_QTY DC_TOT FROM( SELECT DOSAGE_QTY "
				+ "	FROM ODI_DSPNM "
				+ "	WHERE CASE_NO='"
				+ caseNo
				+ "' "
				+ "		  AND ORDER_NO='"
				+ orderNo
				+ "' "
				+ "		  AND ORDER_SEQ= "
				+ orderSeq
				+ ""
				+ "		  AND START_DTTM<='"
				+ dcDate
				+ "') TABLE1";
		TParm qtyParm = new TParm(TJDODBTool.getInstance().select(dcQtySql));
		if (qtyParm.getErrCode() != 0) {
			System.out.println("qtyParm is wrong" + qtyParm.getErrText());
			return null;
		}
		double dcTot = TypeTool.getDouble(qtyParm.getData("DC_TOT", 0));
		Timestamp tomorrow = StringTool.getTimestamp(dcDate, "yyyyMMddHHmm");
		tomorrow = StringTool.rollDate(tomorrow, 1L);
		String tomoStr = StringTool.getString(tomorrow, "yyyyMMdd") + "0000";
		// System.out.println("tomoStr="+tomoStr);
		String getD = "SELECT ORDER_DATE||ORDER_DATETIME START_DTTM "
				+ "	FROM ODI_DSPND " + "	WHERE CASE_NO='" + caseNo + "' "
				+ "		  AND ORDER_NO='" + orderNo + "' " + "		  AND ORDER_SEQ= "
				+ orderSeq + "		  AND (ORDER_DATE||ORDER_DATETIME)<='"
				+ tomoStr + "'" + "	ORDER BY START_DTTM";
		TParm dParm = new TParm(TJDODBTool.getInstance().select(getD));
		if (dParm.getErrCode() != 0) {
			System.out.println("dParm is wrong" + dParm.getErrCode());
			return null;
		}
		count = dParm.getCount();
		// if (count < 0) {
		// System.out.println("dParm.count<=0");
		// return null;
		// }
		List timeList = new ArrayList();
		for (int i = 0; i < count; i++) {
			timeList.add(dParm.getValue("START_DTTM", i));
		}
		Map qty = new HashMap();
		qty.put("DC_TOT", dcTot);
		result.add(timeList);
		result.add(qty);
		result.add(startDttm);
		// System.out.println("result----" + result);
		return result;
	}

	/**
	 * ���ڴ��������������
	 * 
	 * @param order
	 *            TParm
	 * @param execTime
	 *            String yyyyMMddHHmm
	 * @return result List.List time=result.get(0);Map qty=result.get(1);List
	 *         startToEnd=result.get(2).qty�еĲ�����
	 *         //ORDER_LASTDSPN_QTY:order���LASTDSPN_QTY
	 *         //ORDER_ACUMDSPN_QTY��order���ACUMDSPN_QTY
	 *         //ORDER_ACUMMEDI_QTY��order���ACUMMEDI_QTY
	 *         //M_DISPENSE_QTY��M���dispenseQty //M_DISPENSE_UNIT��M���dispenseUnit
	 *         //M_DOSAGE_QTY��M���dosageQty //M_DOSAGE_UNIT��M���dosageUnit
	 *         //D_MEDI_QTY��D���MediQty //D_MEDI_UNIT��D���MediUnit
	 *         //D_DOSAGE_QTY��D���dosageQty //D_DOSAGE_UNIT��D���dosageUnit
	 *         //order���las_dspn_date
	 *         //ORDER_LAST_DSPN_DATE�����õ�����ҩʱ������������ʱ�����һչ��ʱ�������ҽ����DCʱ��
	 *         ��������ʱ������һ��
	 *         //startToEnd.get(0)������yyyymmddhhmmss�Ŀ�ʼʱ��.M���START_DTTM
	 *         //startToEnd.get(1):����yyyymmddhhmmss�Ľ���ʱ��.M���END_DTTM
	 */
	public List getOdiTrtUdQty(TParm order, String execTime, String level) {
		// System.out.println("--order--" + order);
		// System.out.println("--execTime--" + execTime);
		// System.out.println("--level--" + level);
		List result = new ArrayList();
		if (order == null) {
			System.out.println("order is null");
			return null;
		}
		// System.out.println("order.count="+order.getCount());
		// if(order.getCount()!=1){
		// System.out.println("order.getCount()!=1");
		// return null;
		// }
		if (this.isNullString(execTime)) {
			System.out.println("execTime is null");
			return null;
		}
		if (execTime.length() != 12) {
			System.out.println("execTime .length is not 12");
			return null;
		}
		String effDate = StringTool.getString(order.getTimestamp("EFF_DATE"),
				"yyyyMMddHHmm");
		if (this.isNullString(effDate)) {
			// System.out.println("effDate is null");
			System.out.println("effDateTime=" + order.getTimestamp("EFF_DATE"));
			return null;
		}
		String lastDspnDate = StringTool.getString(order
				.getTimestamp("LAST_DSPN_DATE"), "yyyyMMddHHmm");
		if (this.isNullString(lastDspnDate)) {
			// System.out.println("lastDspnDate is null");
			System.out.println("lastDspnDateTime="
					+ order.getTimestamp("LAST_DSPN_DATE"));
		}
		String startDate = lastDspnDate;
		List DispenseTrtDttm=new ArrayList();
		// System.out.println("lastDspnDate="+lastDspnDate);
		if (this.isNullString(lastDspnDate)
				|| order.getTimestamp("EFF_DATE").compareTo(
						StringTool.getTimestamp(execTime, "yyyyMMddHHmm")) > 0) {// shibl
			// modify
			// 20131231
			startDate = effDate;
			DispenseTrtDttm=getDispenseTrtDttmArrange(effDate);
		} else {
			// zhangyong201011221 begin
			DispenseTrtDttm=getDispenseTrtDttmArrange(execTime);
			startDate = DispenseTrtDttm.get(0).toString();
			// zhangyong201011221 end
		}
		String dcDate = StringTool.getString(order.getTimestamp("DC_DATE"),
				"yyyyMMddHHmm");

		// zhangyong20101121 begin
		String endDate = StringTool.getString(StringTool.getTimestamp(DispenseTrtDttm.get(0).toString(),
				"yyyyMMddHHmm"), "yyyyMMdd")
				+ "2359";
		// zhangyong20101121 end

		// String endDate = StringTool.getString(StringTool.rollDate(StringTool.
		// getTimestamp(execTime, "yyyyMMddHHmm"), 1L), "yyyyMMdd") + "0000";
		// System.out.println("------endDate==="+endDate);
		if (!this.isNullString(dcDate)) {
			// System.out.println("dcDate is null");
			// System.out.println("dcDateTime=" +
			// order.getTimestamp("DC_DATE"));
			long dcDateLong = TypeTool.getLong(dcDate);
			long endDateLong = TypeTool.getLong(endDate);
			// �����DC_DATE�����ж�DC_DATE������ڱ���չ����ĩ��ʱ�䣬��END_DATE=DC_DATE
			if (dcDateLong > 0 && dcDateLong < endDateLong) {
				endDate = dcDate;
			}
		}

		// System.out.println("endDate==="+endDate);

		long effDateLong = TypeTool.getLong(effDate);
		long lastDateLong = TypeTool.getLong(lastDspnDate);
		// ���û��LAST_DSPN_DATE����START_DATE=EFF_DATE
		if (lastDateLong == 0) {
			startDate = effDate;
		}
		// System.out.println("startDate==="+startDate);
		// System.out.println("endDate====="+endDate);
		String freqCode = order.getValue("FREQ_CODE");
		if (this.isNullString(freqCode)) {
			System.out.println("freqCode is null");
			return null;
		}
		// ȡ��Ƶ������
		TParm freqParm = this.setFreq(freqCode);
//		System.out.println("freqParm " + freqParm);
		if (freqParm.getErrCode() != 0) {
			System.out.println("freqParm is wrong");
			return null;
		}
		List timeList = new ArrayList();
		// System.out.println("freqParm===" + freqParm);
		if (freqParm.getBoolean("STAT_FLG")) {
			timeList.add(startDate);
			return timeList;
		}
		int cycle = freqParm.getInt("CYCLE");
		if (cycle <= 0) {
			System.out.println("cycle <=0");
			cycle = 1;
		}
		// ȡ��Ƶ��ʱ���
		// System.out.println("--startDate--" + startDate);
		// System.out.println("--endDate--" + endDate);
		// System.out.println("--freqCode--" + freqCode);
		if (cycle > 1) {
			List Standingtimes = getStandingTime(freqCode, true); // ͨ�����ù��÷���ȡ��Ƶ��ʱ��
			System.out.println(order.getTimestamp("EFF_DATE"));
			System.out.println(execTime);
			if (order.getTimestamp("EFF_DATE").compareTo(StringTool.getTimestamp(execTime, "yyyyMMddHHmm")) > 0) {
				System.out.println("================");
				timeList = getMutilCycleStDttm(effDate,endDate, cycle, Standingtimes);
				System.out.println("========timeList========"+timeList);
			}else {
				String caee_no = order.getValue("CASE_NO");
				String order_no = order.getValue("ORDER_NO");
				String order_seq = order.getValue("ORDER_SEQ");
				String sql = "SELECT ORDER_DATE, ORDER_DATETIME FROM ODI_DSPND WHERE CASE_NO = '"
						+ caee_no
						+ "' AND ORDER_NO = '"
						+ order_no
						+ "' AND ORDER_SEQ='"
						+ order_seq
						+ "' ORDER BY ORDER_DATE, ORDER_DATETIME DESC";
				TParm dspndParm = new TParm(TJDODBTool.getInstance().select(sql));
				if (dspndParm != null && dspndParm.getCount() > 0) {
					effDate = dspndParm.getValue("ORDER_DATE", 0)
							+ dspndParm.getValue("ORDER_DATETIME", 0);
				} else {
					effDate = effDate;
				}
				timeList=getMutilCycleDttm(effDate,
						endDate, cycle, Standingtimes, startDate);	
			}	
		} else {
			timeList = getTimeList(startDate, endDate, freqCode);
		}
		if (timeList == null) {
			timeList = new ArrayList();
			System.out.println("timeList is null");
			// return null;
		}
		int time = timeList.size();
		if (time == 0) {
			System.out.println("--order--" + order);
			System.out.println("timeList .size =0");
			// return null;
		}
		Map qty = new HashMap();
		double dosageQty = TypeTool.getDouble(order.getData("MEDI_QTY"));
		result.add(timeList);
		double tempQty = StringTool.round(dosageQty * time, 2);
		String tempUnit = order.getValue("MEDI_UNIT");
		String ctzSql = "SELECT CTZ1_CODE,CTZ2_CODE,CTZ3_CODE FROM ADM_INP WHERE CASE_NO='"
				+ order.getValue("CASE_NO") + "'";
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(ctzSql));
		if (ctzParm == null) {
			System.out.println("ctzcode---------------is null");
			return null;
		}
		if (ctzParm.getErrCode() != 0) {
			System.out.println("ctzcode---------------is errcode");
			return null;
		}
		TParm sysFee = new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM SYS_FEE WHERE ORDER_CODE='"
						+ order.getValue("ORDER_CODE") + "'"));
		if (sysFee == null) {
			System.out.println("sysFee---------------is null");
			return null;
		}
		if (sysFee.getErrCode() != 0) {
			System.out.println("sysFee---------------is errcode");
			return null;
		}
		// System.out.println("1111111");
		double ownPrice = BIL.getFee(order.getValue("ORDER_CODE"), level);
		// System.out.println("2222222");
		double nhiPrice = sysFee.getDouble("NHI_PRICE", 0);
		double rate = BIL.getRate(ctzParm.getValue("CZT1_CODE", 0), ctzParm
				.getValue("CZT2_CODE", 0), ctzParm.getValue("CZT3_CODE", 0),
				order.getValue("ORDER_CODE"), level);
		// System.out.println("333333");
		double ownAmtM = StringTool.round(BIL.getFee(order
				.getValue("ORDER_CODE"), tempQty, level), 2);
		// System.out.println("444444");
		double totAmtM = StringTool.round(ownAmtM * rate, 2);
		double totAmtD = StringTool.round(BIL.getFee(order
				.getValue("ORDER_CODE"), TypeTool.getDouble(order
				.getData("MEDI_QTY"))
				* rate, level), 2);
		// System.out.println("555555");
		// order���LASTDSPN_QTY
		qty.put("ORDER_LASTDSPN_QTY", tempQty);
		// order���ACUMDSPN_QTY
		qty.put("ORDER_ACUMDSPN_QTY", tempQty);
		// order���ACUMMEDI_QTY
		qty.put("ORDER_ACUMMEDI_QTY", tempQty);
		// M���dispenseQty
		qty.put("M_DISPENSE_QTY", tempQty);
		// M���dispenseUnit
		qty.put("M_DISPENSE_UNIT", tempUnit);
		// M���dosageQty
		qty.put("M_DOSAGE_QTY", tempQty);
		// M���dosageUnit
		qty.put("M_DOSAGE_UNIT", tempUnit);
		// D���MediQty
		qty.put("D_MEDI_QTY", TypeTool.getDouble(order.getData("MEDI_QTY")));
		// D���MediUnit
		qty.put("D_MEDI_UNIT", tempUnit);
		// D���dosageQty
		qty.put("D_DOSAGE_QTY", TypeTool.getDouble(order.getData("MEDI_QTY")));
		// D���dosageUnit
		qty.put("D_DOSAGE_UNIT", tempUnit);
		// order���las_dspn_date
		// ���õ�����ҩʱ������������ʱ�����һչ��ʱ�������ҽ����DCʱ�䡣������ʱ������һ��
		// System.out.println("lastDspnDate="+StringTool.getTimestamp(endDate,"yyyyMMddHHmm"));
		// System.out.println("lastDspnDate="+endDate);
		qty.put("ORDER_LAST_DSPN_DATE", StringTool.getTimestamp(endDate,
				"yyyyMMddHHmm"));
		/**
		 * //M���OWN_PRICE OWN_PRICE //M���NHI_PRICE NHI_PRICE //M���OWN_AMT
		 * OWN_AMT //M���TOT_AMT TOT_AMTM //M���DISCOUNT_RATE DISCOUNT_RATE
		 * //D���TOT_AMT TOT_AMTD
		 */
		qty.put("OWN_PRICE", ownPrice);
		qty.put("NHI_PRICE", nhiPrice);
		qty.put("TOT_AMTM", totAmtM);
		qty.put("TOT_AMTD", totAmtD);
		qty.put("DISCOUNT_RATE", rate);
		qty.put("OWN_AMT", ownAmtM);
		List startToEnd = new ArrayList();
		startToEnd.add(startDate);
		startToEnd.add(endDate);
		result.add(qty);
		result.add(startToEnd);
		// System.out.println("qty=" + qty);
		// System.out.println("startToEnd=" + startToEnd);
		return result;
	}

	/**
	 * ���ݸ�������ʱ���Ƶ�δ���ȡ��ִ��ʱ���б�
	 * 
	 * @param startDate
	 * @param endDate
	 * @param freqCode
	 * @return
	 */
	public List getTimeList(String startDate, String endDate, String freqCode) {
		List timeList = new ArrayList();
		if (this.isNullString(startDate) || this.isNullString(endDate)
				|| this.isNullString(freqCode)) {
			System.out.println("getTimeList . is null");
			return null;
		}
		if (startDate.length() != 12 || endDate.length() != 12) {
			System.out.println("getTimeList. 12 is wrong");
			return null;
		}
		// System.out.println("startDate=="+startDate);
		// System.out.println("endDate===="+endDate);
		long startDateLong = TypeTool.getLong(startDate);
		long endDateLong = TypeTool.getLong(endDate);
		if (startDateLong > endDateLong) {
			System.out.println("endDateLong<startDateLong");
			return null;
		}
		// ȡ��Ƶ������
		TParm freqParm = this.setFreq(freqCode);
		// System.out.println("freqParm " + freqParm);
		if (freqParm.getErrCode() != 0) {
			System.out.println("freqParm is wrong");
			return null;
		}
		// System.out.println("freqParm===" + freqParm);
		if (freqParm.getBoolean("STAT_FLG")) {
			timeList.add(startDate);
			return timeList;
		}
		int cycle = freqParm.getInt("CYCLE");
		if (cycle <= 0) {
			System.out.println("cycle <=0");
			cycle = 1;
		}
		// System.out.println("endDateTime=" + endDate);
		// System.out.println("startTime===" + startDate);
		int dayDiff = StringTool.getDateDiffer(StringTool.getTimestamp(endDate
				.substring(0, 8), "yyyyMMdd"), StringTool.getTimestamp(
				startDate.substring(0, 8), "yyyyMMdd"));
		// System.out.println("dayDiff=" + dayDiff);
		List Standingtimes = new ArrayList(); // Ƶ��ʱ�㼯�ϣ�0800,1600����
		Standingtimes = getStandingTime(freqCode, true); // ͨ�����ù��÷���ȡ��Ƶ��ʱ��
		if (Standingtimes == null) {
			System.out.println("ȡ��Ƶ��ʱ����� is null");
			return null;
		}
		if (Standingtimes.size() <= 0) {
			System.out.println("ȡ��Ƶ��ʱ����� size<=0");
			return null;
		}
		if (dayDiff == 0) {
			dayDiff = 1;
		}
		String tempDate = startDate.substring(0, 8);
		// zhangyong20101121 begin
		long day = (long) cycle - 1;
		Timestamp newDay = StringTool.rollDate(StringTool.getTimestamp(
				tempDate, "yyyyMMdd"), day);
		tempDate = StringTool.getString(newDay, "yyyyMMdd");
		// zhangyong20101121 end
		int step = dayDiff / cycle;
		for (int i = 0; i < step + 1; i++) {
			String ymdhm = "";
			for (int j = 0; j < Standingtimes.size(); j++) {
				// System.out.println("tempDate--1-"+tempDate);
				ymdhm = tempDate + Standingtimes.get(j);
				long temp = TypeTool.getLong(ymdhm);
				// System.out.println("j=" + j);
				// System.out.println("temp=" + temp);
				// System.out.println("startDateLong=" + startDateLong);
				// System.out.println("endDateLong===" + endDateLong);
				if (temp > startDateLong && temp <= endDateLong) {
					timeList.add(ymdhm);
				}
			}
			// System.out.println("tempDateTime="
			// + StringTool.getTimestamp(ymdhm, "yyyyMMddHHmm"));
			tempDate = StringTool.getString(StringTool.rollDate(StringTool
					.getTimestamp(tempDate, "yyyyMMdd"), TypeTool
					.getLong(cycle)), "yyyyMMdd");
			// System.out.println("tempDate--2-" + tempDate);
		}
		for (int i = 0; i < timeList.size(); i++) {
			for (int j = i + 1; j < timeList.size(); j++) {
				if (j < timeList.size()) {
					long iLong = TypeTool.getLong(timeList.get(i));
					long jLong = TypeTool.getLong(timeList.get(j));
					if (iLong > jLong) {
						timeList.set(i, jLong);
						timeList.set(j, iLong);
					}
				}
			}
		}
		return timeList;
	}

	public static void main(String[] args) {
		List l = new ArrayList();
		l.add("0900");
		List m = TotQtyTool.getInstance().getMutilCycleDttm("201401031111",
				"201401042359", 2, l, "201401030000");
		List m1 = TotQtyTool.getInstance().getMutilCycleStDttm("201401020130",
				"201401022359", 2, l);
		List m2 = TotQtyTool.getInstance().getMutilCycleDttm("201301091310",
				"201301111310", 2, l, "201301101310");
		System.out.println("===========1=======" + m);
		System.out.println("= ======2=======" + m1);
		System.out.println("=========3=======" + m2);
	}
}
