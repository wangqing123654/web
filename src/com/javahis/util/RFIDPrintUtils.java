package com.javahis.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.javahis.device.Uitltool;

/**
 * ���ӱ�ǩ��ӡ������
 * 
 * @author lixiang
 * 
 */
public class RFIDPrintUtils {
	/**
	 * Ʒ��
	 */
	public static final String PARM_NAME = "P_NAME";
	/**
	 * ���
	 */
	public static final String PARM_SPEC = "P_SPEC";
	/**
	 * Ч��
	 */
	public static final String PARM_VALID_DATE = "P_VALID_D";

	/**
	 * Ч��
	 */
	public static final String PARM_NUM = "P_NUM";

	/**
	 * ����
	 */
	public static final String PARM_CODE = "P_CODE";

	/**
	 * RFIDֵ(ʮ������ʽ)
	 */
	public static final String PARM_PRFID = "P_PRFID";
	/**
	 * ���Կ���
	 */
	private static boolean isDebug = true;

	/**
	 * ��ӡ���ӱ�ǩ��ģ��
	 */
	/*
	 * private static String RFID_PRINT_TEMPLATE = "{D0590,1035,0570|}\r\n" +
	 * "{C|}\r\n" + "{PC000;0160,0095,2,2,e,00,B=����:|}\r\n" +
	 * "{PC001;0280,0095,2,2,e,00,B=#PNAME|}\r\n" +
	 * "{PC002;0160,0145,2,2,e,00,B=���:|}\r\n" +
	 * "{PC003;0280,0145,2,2,e,00,B=#PSPEC|}\r\n" +
	 * "{XB00;0170,0210,B,1,05,05,13,13,05,0,0127,+0000000000,0,00=#PCODE|}\r\n"
	 * + "{PV00;0382,0374,0035,0035,B,00,B=#PCODE|}\r\n" +
	 * "{XB01;0000,0000,r,T24,G2,B01=#PRFID|}\r\n" +
	 * "{PC004;0160,0195,2,2,e,00,B=Ч��:|}\r\n" +
	 * "{PC005;0280,0195,2,2,e,00,B=#PVDATE|}\r\n" +
	 * "{XS;I,0001,0000C6201|}\r\n";
	 */

	/*
	 * private static String RFID_PRINT_TEMPLATE = "{D0590,1035,0570|}\r\n" +
	 * "{C|}\r\n" + "{PC000;0120,0095,2,2,e,00,B=����:|}\r\n" +
	 * "{PC001;0240,0095,2,2,e,00,B=#PNAME|}\r\n" +
	 * "{PC002;0120,0145,2,2,e,00,B=���:|}\r\n" +
	 * "{PC003;0240,0145,2,2,e,00,B=#PSPEC|}\r\n" +
	 * "{XB00;0130,0210,A,3,05,0,0127,+0000000000,000,0,00=>#PCODE|}\r\n" +
	 * "{PV00;0197,0374,0035,0035,B,00,B=#PCODE|}\r\n" +
	 * "{XB01;0000,0000,r,T24,G2,B01=#PRFID|}\r\n" +
	 * "{PC004;0120,0195,2,2,e,00,B=Ч��:|}\r\n" +
	 * "{PC005;0240,0195,2,2,e,00,B=#PVDATE|}\r\n" +
	 * "{XS;I,0001,0000C6201|}\r\n";
	 */

	private static String TOXIC_RFID_PRINT_TEMPLATE = "{U2;0120|}\r\n"
			+ "{D0421,0755,0401|}\r\n"
			+ "{AY;+00,0|}\r\n"
			+ "{C|}\r\n"
			+ "{XB01;0000,0000,r,T24,G2,B01=#PRFID|}\r\n"
			+ "{PC000;0048,0056,15,15,e,00,B=���ƣ�|}\r\n"
			+ "{PC002;0128,0056,15,15,e,00,B=#PNAME|}\r\n"
			+ "{PC003;0048,0096,15,15,e,00,B=���|}\r\n"
			+ "{PC004;0128,0096,15,15,e,00,B=#PSPEC|}\r\n"
			+ "{PC005;0048,0136,15,15,e,00,B=������|}\r\n"
			+ "{PC006;0128,0136,15,15,e,00,B=#PNUM|}\r\n"
			+ "{XB00;0047,0150,A,3,05,0,0127,+0000000000,000,0,00=>5#PCODE|}\r\n"
			+ "{PC001;0132,0302,1,1,e,00,B=#PCODE|}\r\n"
			+ "{XS;I,0001,0000C6201|}\r\n" + "{U1;0120|}";

	/**
	 * ��ʽ��ӡģ��
	 */
	private static String RFID_PRINT_TEMPLATE = "{U2;0120|}\r\n"
			+ "{D0421,0755,0401|}\r\n"
			+ "{AY;+00,0|}\r\n"
			+ "{C|}\r\n"
			+ "{XB01;0000,0000,r,T24,G2,B01=#PRFID|}\r\n"
			+ "{PC000;0048,0056,15,15,e,00,B=���ƣ�|}\r\n"
			+ "{PC002;0128,0056,15,15,e,00,B=#PNAME|}\r\n"
			+ "{PC003;0048,0096,15,15,e,00,B=���|}\r\n"
			+ "{PC004;0128,0096,15,15,e,00,B=#PSPEC|}\r\n"
			+ "{PC005;0048,0136,15,15,e,00,B=Ч�ڣ�|}\r\n"
			+ "{PC006;0128,0136,15,15,e,00,B=#PVDATE|}\r\n"
			+ "{XB00;0047,0150,A,3,05,0,0127,+0000000000,000,0,00=>#PCODES|}\r\n"
			+ "{PC001;0132,0302,1,1,e,00,B=#PCODE|}\r\n"
			+ "{XS;I,0001,0000C6201|}\r\n" + "{U1;0120|}\r\n";

	/**
	 * ��ӡRFID��ǩ
	 * 
	 * @param parm
	 */
	public static boolean send2LPT(TParm parm) {
		// ȡ����;
		// 1.Ʒ��
		String pName = parm.getValue(PARM_NAME);
		// 2.���
		String pSpec = parm.getValue(PARM_SPEC);
		// 3.Ч��
		String pValidDate = parm.getValue(PARM_VALID_DATE);
		// 4.����
		String pCode = parm.getValue(PARM_CODE);
		// 5.rfidֵ
		String pRfid = parm.getValue(PARM_PRFID);

		// �滻ģ��
		// 1.
		// String rfidData = RFID_PRINT_TEMPLATE.replaceAll("#PNAME", pName);
		String rfidData = StringTool.replaceAll(RFID_PRINT_TEMPLATE, "#PNAME",
				pName);
		// System.out.println("--"+rfidData);

		// 2
		// rfidData = RFID_PRINT_TEMPLATE.replaceAll("#PSPEC", pSpec);
		rfidData = StringTool.replaceAll(rfidData, "#PSPEC", pSpec);
		// 3Ч��
		rfidData = StringTool.replaceAll(rfidData, "#PVDATE", pValidDate);

		// 4.
		// rfidData = RFID_PRINT_TEMPLATE.replaceAll("#PCODE", pCode);
		String barcode = encode(pCode);
		rfidData = StringTool.replaceAll(rfidData, "#PCODES", barcode);
		//
		rfidData = StringTool.replaceAll(rfidData, "#PCODE", pCode);

		// 5
		// rfidData = RFID_PRINT_TEMPLATE.replaceAll("#PRFID", pRfid);
		// ת��16��������
		// System.out.println("===10������ʽ===="+Uitltool.encode(pRfid));

		// System.out.println("===10������ʽ===="+Uitltool.decode((Uitltool.encode(pRfid))));
		rfidData = StringTool.replaceAll(rfidData, "#PRFID", Uitltool
				.encode(pRfid));
		if (isDebug) {
			System.out.println("rfidData:\r\n" + rfidData);
		}
		synchronized (rfidData) {
			if (isDebug) {
				System.out.println("---rfidData----" + rfidData);
			}
			// ������LPT1
			FileWriter fw = null;
			PrintWriter out = null;
			try {
				fw = new FileWriter("LPT1");
				// fw = new FileWriter("mydata.txt");

				out = new PrintWriter(fw);
				out.print(rfidData);
				return true;
			} catch (IOException e) {
				System.out.println(pName + "��ӡ����");
				e.printStackTrace();
				return false;
			} finally {
				out.close();
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * 
	 * @param s1
	 * @return
	 */
	private static String encode(String s1) {
		// String s1="A13040500001";
		String s2 = s1.substring(0, 2);
		// System.out.println("--s2--"+s2);

		String s3 = s1.substring(2, s1.length());
		// System.out.println("--s3--"+s3);
		String strCode = "6" + s2 + ">5" + s3;
		if (isDebug) {
			System.out.println("----strCode-----" + strCode);
		}
		return strCode;
	}

	/**
	 * ��ӡRFID��ǩ
	 * 
	 * @param parm
	 */
	public static boolean send2LPTToxic(TParm parm) {
		// ȡ����;
		// 1.Ʒ��
		String pName = parm.getValue(PARM_NAME);
		// 2.���
		String pSpec = parm.getValue(PARM_SPEC);
		// 3.Ч��
		String pNum = parm.getValue(PARM_NUM);
		// 4.����
		String pCode = parm.getValue(PARM_CODE);
		// 5.rfidֵ
		String pRfid = parm.getValue(PARM_PRFID);

		// �滻ģ��
		// 1.
		// String rfidData = RFID_PRINT_TEMPLATE.replaceAll("#PNAME", pName);
		String rfidData = StringTool.replaceAll(TOXIC_RFID_PRINT_TEMPLATE,
				"#PNAME", pName);
		// System.out.println("--"+rfidData);

		// 2
		// rfidData = RFID_PRINT_TEMPLATE.replaceAll("#PSPEC", pSpec);
		rfidData = StringTool.replaceAll(rfidData, "#PSPEC", pSpec);
		// 3Ч��
		rfidData = StringTool.replaceAll(rfidData, "#PNUM", pNum);

		// 4.
		// rfidData = RFID_PRINT_TEMPLATE.replaceAll("#PCODE", pCode);
		rfidData = StringTool.replaceAll(rfidData, "#PCODE", pCode);

		// 5
		// rfidData = RFID_PRINT_TEMPLATE.replaceAll("#PRFID", pRfid);
		// ת��16��������
		// System.out.println("===10������ʽ===="+Uitltool.encode(pRfid));

		// System.out.println("===10������ʽ===="+Uitltool.decode((Uitltool.encode(pRfid))));
		rfidData = StringTool.replaceAll(rfidData, "#PRFID", Uitltool
				.encode(pRfid));
		if (isDebug) {
			System.out.println("rfidData:\r\n" + rfidData);
		}
		// ������LPT1
		FileWriter fw = null;
		PrintWriter out = null;
		try {
			fw = new FileWriter("LPT1");
			// fw = new FileWriter("mydata.txt");

			out = new PrintWriter(fw);
			out.print(rfidData);
			return true;
		} catch (IOException e) {
			System.out.println(pName + "��ӡ����");
			e.printStackTrace();
			return false;
		} finally {
			out.close();
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		/*
		 * TParm parm = new TParm();
		 * 
		 * parm.setData(RFIDPrintUtils.PARM_NAME, "Ʒ������1");
		 * 
		 * parm.setData(RFIDPrintUtils.PARM_SPEC, "������15*15");
		 * 
		 * parm.setData(RFIDPrintUtils.PARM_VALID_DATE, "2013/04/13");
		 * 
		 * // ʮ���� parm.setData(RFIDPrintUtils.PARM_CODE, "130104000001");
		 * 
		 * // ��Ҫת��(��һ��Ӧ������ģ���Ƿ��������) // ʮ������
		 * parm.setData(RFIDPrintUtils.PARM_PRFID, "130104000001");
		 * 
		 * // ������ӡ RFIDPrintUtils.send2LPT(parm);
		 */

		// �����ӡ
		// 6A1>53040500001 A13040500001
		String s1 = "A13040500001";
		String s2 = s1.substring(0, 2);
		System.out.println("--s2--" + s2);

		String s3 = s1.substring(2, s1.length());
		System.out.println("--s3--" + s3);
		String strCode = "6" + s2 + ">5" + s3;
		System.out.println("----strCode----" + strCode);

	}

}
