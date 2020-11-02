package jdo.sys;

import java.util.HashMap;
import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class SysPhaBarTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static SysPhaBarTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return OperatorTool
	 */
	public static SysPhaBarTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SysPhaBarTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SysPhaBarTool() {
	}

	/**
	 * ȡ��ҩƷ����
	 * 
	 * @return String
	 */
	public String getBarCode() {
		return SystemTool.getInstance().getNo("ALL", "UDD", "BAR_CODE",
				"BAR_CODE");
	}

	/**
	 * ��װBAR_CODE����
	 * 
	 * @param orderParm
	 * @param Type
	 *            ��UDDסԺҩ����
	 * @return
	 */
	public TParm getaddBarParm(TParm orderParm, String Type) {
		TParm result = new TParm();
		String admType = orderParm.getValue("ADM_TYPE");
		String sql = "";
		// ����ҽ������
		String LbarCode = "";
		// �ڷ�����
		String ObarCode = "";
		// ��������
		String EbarCode = "";
		// �������
		String IbarCode = "";
		// �������
		String FbarCode = "";
		String FlinkNo = "";
		//
		Map map = new HashMap();
		Map linkmap = new HashMap();
		int Ocount = 0;
		for (int i = 0; i < orderParm.getCount("CASE_NO"); i++) {
			TParm parm = orderParm.getRow(i);
			if (parm.getValue("CAT1_TYPE").equals("PHA")) {
				if (admType.equals("O") || admType.equals("E")) {

				} else if (admType.equals("I")) {
					sql = "SELECT * FROM ODI_DSPNM WHERE CASE_NO='"
							+ parm.getValue("CASE_NO") + "' AND ORDER_NO='"
							+ parm.getValue("ORDER_NO") + "' AND ORDER_SEQ='"
							+ parm.getValue("ORDER_SEQ") + "' AND START_DTTM='"
							+ parm.getValue("START_DTTM") + "'";
				} else if (admType.equals("H")) {

				}
				TParm dataParm = new TParm(getDBTool().select(sql));
				// ���Ӻ�
				String linkNo = dataParm.getValue("LINK_NO", 0);
				// ҩƷ����
//				String type = getDoseType(dataParm.getValue("ORDER_CODE", 0));
				String type=getClassifyType(dataParm.getValue("ROUTE_CODE", 0));
				String barCode = dataParm.getValue("BAR_CODE", 0);
				// // �ж�����ҽ����һ��һ�룩
				// if (!linkNo.equals("")) {
				// // System.out.println("����ҽ��");
				// // ���ӺŲ�����������ֶ�Ϊ��
				// if (barCode.equals("")) {
				// // ���ӺŲ����ȡ��
				// if (!linkNo.equals(FlinkNo)) {
				// LbarCode = getBarCode();
				// FlinkNo = linkNo;
				// // ���Ӻ���Ⱦ���Ų����ȡ��
				// } else if (linkNo.equals(FlinkNo)
				// && linkmap.get(parm.getValue("CASE_NO")) == null) {
				// LbarCode = getBarCode();
				// linkmap.put(parm.getValue("CASE_NO"),
				// parm.getValue("CASE_NO"));
				// }
				// }
				// // �ֶ�Ϊ�ս�
				// if (barCode.equals(""))
				// orderParm.addData("BAR_CODE", LbarCode);
				// else
				// orderParm.addData("BAR_CODE", barCode);
				// } else {
				// �ڷ���һ��һ�룩
				if ("O".equals(type)) {
					// ��ʿִ��
					// if (Type.equals("INW")) {
					// if (barCode.equals(""))
					// orderParm.addData("BAR_CODE", "");
					// else
					// orderParm.addData("BAR_CODE", barCode);
					// }
					// סԺҩ����ҩ
					if (Type.equals("UDD")) {
						if (map.get(parm.getValue("CASE_NO")) == null) {
							ObarCode = getBarCode();
							map.put(parm.getValue("CASE_NO"), ObarCode);
						}
						// System.out.println("�ڷ�");
						if (barCode.equals(""))
							orderParm.addData("BAR_CODE",
									(String) map.get(parm.getValue("CASE_NO")));
						else
							orderParm.addData("BAR_CODE", barCode);
					}
				} else {
					orderParm.addData("BAR_CODE", barCode);
				}
				// // ���ã�һ��һ�룩
				// else if ("E".equals(type)) {
				// EbarCode = getBarCode();
				// // System.out.println("����");
				// if (barCode.equals(""))
				// orderParm.addData("BAR_CODE", EbarCode);
				// else
				// orderParm.addData("BAR_CODE", barCode);
				// }
				// // �����һ��һ�룩
				// else if ("I".equals(type)) {
				// IbarCode = getBarCode();
				// // System.out.println("���");
				// if (barCode.equals(""))
				// orderParm.addData("BAR_CODE", IbarCode);
				// else
				// orderParm.addData("BAR_CODE", barCode);
				// }
				// // ��Σ�һ��һ�룩
				// else if ("F".equals(type)) {
				// FbarCode = getBarCode();
				// // System.out.println("���");
				// if (barCode.equals(""))
				// orderParm.addData("BAR_CODE", FbarCode);
				// else
				// orderParm.addData("BAR_CODE", barCode);
				// } else {
				// orderParm.addData("BAR_CODE", "");
				// }
				// }
				// } else {
				// orderParm.addData("BAR_CODE", "");
			}
		}
		return orderParm;
	}

	/**
	 * �õ�����
	 * 
	 * @param orderCode
	 * @return
	 */
	public String getDoseType(String orderCode) {
		String sql = " SELECT A.ORDER_CODE,A.ORDER_DESC,B.DOSE_TYPE FROM PHA_BASE A,PHA_DOSE B "
				+ " WHERE A.DOSE_CODE=B.DOSE_CODE ";
		TParm parm = new TParm();
		if (orderCode.equals("")) {
			return "";
		} else {
			sql += " AND A.ORDER_CODE='" + orderCode + "'";
		}
		parm = new TParm(getDBTool().select(sql));
		if (parm.getCount() < 0) {
			System.out.println("ҽ��" + orderCode + "�õ����ʹ���");
		}
		return parm.getValue("DOSE_TYPE", 0);
	}
    /**
     * ���ݷ����õ�����
     * @param routeCode
     * @return
     */
	public String getClassifyType(String routeCode) {
		String sql = " SELECT A.ROUTE_CODE,A.ROUTE_CHN_DESC,A.CLASSIFY_TYPE FROM SYS_PHAROUTE A";
		TParm parm = new TParm();
		if (routeCode.equals("")) {
			return "";
		} else {
			sql += " WHERE A.ROUTE_CODE='" + routeCode + "'";
		}
		parm = new TParm(getDBTool().select(sql));
		if (parm.getCount() < 0) {
			System.out.println("�÷�" + routeCode + "�õ����ʹ���");
		}
		return parm.getValue("CLASSIFY_TYPE", 0);
	}

	/**
	 * getDBTool ���ݿ⹤��ʵ��
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
}
