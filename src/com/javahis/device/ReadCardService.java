package com.javahis.device;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Properties;

import com.dongyang.data.TParm;

/**
 * �Ͼ�����ҵ�����
 * 
 * @author lixiang
 * 
 */
public class ReadCardService {

	// ���Կ���
	private static final boolean isDebug = true;
	// ������Ϣ���
	private static final String ERR_FLG = "err";
	// ���ӷ��ؾ��
	private static int hReader = -1;
	//
	private static String STRING_CODING = "gb2312";

	/**
	 * ������ȡ����ֵ��������Ϣ;
	 * 
	 * @param ret_value
	 *            ���ص�ʮ����
	 * @return 0�ɹ� -1ʧ��;
	 */
	public static int ReturnRet(int ret_value, TParm parm) {

		// System.out.println("=========ret_value���ص�ʮ����ֵΪ=====" + ret_value);
		String ret = Integer.toHexString(ret_value);
		// System.out.println("========ret_valueת��ʮ������ֵΪ===========" + ret);
		if (ret.equalsIgnoreCase("0")) {
			parm.setData(ERR_FLG, "�ɹ�");
			return 0;
		} else if (ret.equalsIgnoreCase("1031")) {

		} else if (ret.equalsIgnoreCase("1041")) {

		} else if (ret.equalsIgnoreCase("1051")) {

		} else if (ret.equalsIgnoreCase("1061")) {

		} else if (ret.equalsIgnoreCase("1101")) {

		} else if (ret.equalsIgnoreCase("1111")) {

		} else if (ret.equalsIgnoreCase("1121")) {

		} else if (ret.equalsIgnoreCase("1122")) {
			parm.setData(ERR_FLG, "��ȡָ���ļ�ʧ��");
			return -1;
		} else if (ret.equalsIgnoreCase("1181")) {
			parm.setData(ERR_FLG, "����ʧ��");
			return -1;
		} else if (ret.equalsIgnoreCase("1182")) {
			parm.setData(ERR_FLG, "����TACʧ��");
			return -1;
		} else if (ret.equalsIgnoreCase("7001")) {
			parm.setData(ERR_FLG, "ִ��ָ��ʧ��");
			return -1;
		} else if (ret.equalsIgnoreCase("7002")) {
			parm.setData(ERR_FLG, "�޿�");
			return -1;
		} else if (ret.equalsIgnoreCase("7101")) {
			parm.setData(ERR_FLG, "Log�ļ�����ʧ��");
			return -1;
		} else if (ret.equalsIgnoreCase("1091")) {
			parm.setData(ERR_FLG, "Desfire��֤ʧ��");
			return -1;
		} else if (ret.equalsIgnoreCase("1170")) {

		} else if (ret.equalsIgnoreCase("1171")) {

		} else if (ret.equalsIgnoreCase("1172")) {

		} else if (ret.equalsIgnoreCase("1173")) {

		} else if (ret.equalsIgnoreCase("1174")) {

		} else if (ret.equalsIgnoreCase("1175")) {

		} else if (ret.equalsIgnoreCase("1176")) {

		} else if (ret.equalsIgnoreCase("1177")) {

		} else if (ret.equalsIgnoreCase("1178")) {

		} else if (ret.equalsIgnoreCase("1179")) {
			parm.setData(ERR_FLG, "Desfireָ���ʽ����");
			return -1;
		} else if (ret.equalsIgnoreCase("1000")) {
		} else if (ret.equalsIgnoreCase("1001")) {
		} else if (ret.equalsIgnoreCase("1002")) {
		} else if (ret.equalsIgnoreCase("1003")) {
		} else if (ret.equalsIgnoreCase("1004")) {
		} else if (ret.equalsIgnoreCase("1005")) {
		} else if (ret.equalsIgnoreCase("1006")) {
		} else if (ret.equalsIgnoreCase("1007")) {
		} else if (ret.equalsIgnoreCase("1008")) {
		} else if (ret.equalsIgnoreCase("1009")) {
			parm.setData(ERR_FLG, "�޿�");
		} else if (ret.equalsIgnoreCase("7E")) {
			parm.setData(ERR_FLG,
					"Length_Error Length of command string invalid");
			return -1;
		} else if (ret.equalsIgnoreCase("9E")) {
			parm.setData(ERR_FLG,
					"Parameter_Error Value of the parameter(s) invalid");
			return -1;
		} else if (ret.equalsIgnoreCase("AE")) {
			parm
					.setData(
							ERR_FLG,
							"Authentication_Error AUTHENTICATION_ERROR Current authentication status does not allow the requested command");
			return -1;
		} else if (ret.equalsIgnoreCase("0C")) {
			parm
					.setData(
							ERR_FLG,
							"NO_CHANGES No changes done to backup files, CommitTransaction/AbortTransaction not necessary");
			return -1;
		} else if (ret.equalsIgnoreCase("0E")) {
			parm
					.setData(ERR_FLG,
							"OUT_OF_EEPROM_ERROR Insufficient NV-Memory to complete command");
			return -1;
		} else if (ret.equalsIgnoreCase("1C")) {
			parm.setData(ERR_FLG,
					"ILLEGAL_COMMAND_CODE Command code not supported");
			return -1;
		} else if (ret.equalsIgnoreCase("1E")) {
			parm
					.setData(ERR_FLG,
							"INTEGRITY_ERROR CRC or MAC does not match data Padding bytes not valid");
			return -1;
		} else if (ret.equalsIgnoreCase("40")) {
			parm.setData(ERR_FLG, "NO_SUCH_KEY Invalid key number specified");
			return -1;
		} else if (ret.equalsIgnoreCase("9D")) {
			parm
					.setData(
							ERR_FLG,
							"PERMISSION_DENIED Current configuration / status does not allow the requested command");
			return -1;
		} else if (ret.equalsIgnoreCase("A0")) {
			parm.setData(ERR_FLG,
					"APPLICATION_NOT_FOUND Requested AID not present on PICC");
			return -1;
		} else if (ret.equalsIgnoreCase("A1")) {
			parm
					.setData(
							ERR_FLG,
							"APPL_INTEGRITY_ERROR Unrecoverable error within application, application will be disabled *");
			return -1;
		} else if (ret.equalsIgnoreCase("AF")) {
			parm
					.setData(ERR_FLG,
							"ADDITIONAL_FRAME Additional data frame is expected to be sent");
			return -1;
		} else if (ret.equalsIgnoreCase("BE")) {
			parm
					.setData(
							ERR_FLG,
							"BOUNDARY_ERROR Attempt to read/write data from/to beyond the file's/record's limits. Attempt to exceed the limits of a value file.");
			return -1;
		} else if (ret.equalsIgnoreCase("C1")) {
			parm
					.setData(
							ERR_FLG,
							"PICC_INTEGRITY_ERROR Unrecoverable error within PICC, PICC will be disabled *.");
			return -1;
		} else if (ret.equalsIgnoreCase("CD")) {
			parm
					.setData(ERR_FLG,
							"PICC_DISABLED_ERROR PICC was disabled by an unrecoverable error *");
			return -1;
		} else if (ret.equalsIgnoreCase("CE")) {
			parm
					.setData(
							ERR_FLG,
							"COUNT_ERROR Number of Applications limited to 28, no additional CreateApplication possible");
			return -1;
		} else if (ret.equalsIgnoreCase("DE")) {
			parm
					.setData(
							ERR_FLG,
							"DUPLICATE_ERROR Creation of file/application failed because file/application with same number already exists");
			return -1;
		} else if (ret.equalsIgnoreCase("EE")) {
			parm
					.setData(
							ERR_FLG,
							"EEPROM_ERROR Could not complete NV-write operation due to loss of power, internal backup/rollback mechanism activated *");
			return -1;
		} else if (ret.equalsIgnoreCase("F0")) {
			parm.setData(ERR_FLG,
					"FILE_NOT_FOUND Specified file number does not exist");
			return -1;
		} else if (ret.equalsIgnoreCase("F1")) {
			parm.setData(ERR_FLG,
					"FILE_NOT_FOUND Specified file number does not exist");
			return -1;
		}
		if (ret.startsWith("12")) {
			parm.setData(ERR_FLG, "ע�������ʧ��");
			return -1;
		} else {
			parm.setData(ERR_FLG, "δ֪����");
			return -1;
		}

	}

	/*
	 * ��byteת����int��Ȼ������Integer.toHexString(int)��ת����16�����ַ��� Convert byte[] to hex
	 * string.
	 * 
	 * @param src byte[] data
	 * 
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src, int byteLength) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || byteLength <= 0) {
			return null;
		}
		for (int i = 0; i < byteLength; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * ���Ӷ�����
	 * 
	 * @return 0 �ɹ� -1 ʧ��
	 */
	public static int LinkReaderPro() {

		// ���������ںŸ��������ļ���115200
		hReader = NJSMCardDriver.LinkReaderPro(1, 57600);
		if (isDebug) {
			System.out.println("���Ӷ���������ֵ=============" + hReader);
		}
		// �жϷ���ֵ
		if (hReader < 0) {
			return -1;
		}
		// ���ý�����ˮ�� ,�ӿ��ĵ�û�ᵽ������������

		return 0;
	}

	/**
	 * �����źͿ����ͣ�����Desfire����M1���� parm(cardType,cardNO,err)
	 * 
	 * @param parm
	 * @return
	 */
	public static int ReadCardNO(TParm parm) {
		// �жϷ���ֵ
		if (hReader < 0) {
			parm.setData("err", "�������Ӷ�����");
			return -1;
		}
		STCardInfo stCardInfo = new STCardInfo();
		// ����Ѱ����������һ���Ǳ�����õ�
		int ret = NJSMCardDriver.FoundCardPro(hReader, stCardInfo);
		// �жϷ���ֵ
		if (ReturnRet(ret, parm) == -1) {
			return -1;
		}
		//
		String strCardType = Byte.toString(stCardInfo.getCardType());
		int intCardType = Integer.valueOf(strCardType);
		String strHexCardType = Integer.toHexString(intCardType);
		if (isDebug) {
			System.out.println("strCardType������===" + strHexCardType);// �����ͣ�16����ת��
		}

		// System.out.println("uid leng====="+Integer.valueOf(new
		// String(stCardInfo.getUid()),16));
		// 1AΪM1(C)��
		// DFΪDesfire(A,B)��

		// �����C��
		if (strHexCardType.equalsIgnoreCase("1A")) {
			// �����Ƚ�����֤,ÿ����ֻ֤�ܲ���һ������
			ret = NJSMCardDriver.M1_AuthPro(hReader, 2);
			if (ReturnRet(ret, parm) == -1) {
				// System.out.println("��֤�д���");
				return -1;
			}
			// ��ȡ����
			M1Data m1Data = new M1Data();
			ret = NJSMCardDriver.M1_ReadData(hReader, 2, m1Data);
			// �жϷ���ֵ
			if (ReturnRet(ret, parm) == -1) {
				return -1;
			}
			// ����M1�Ŀ������BCD��洢�ģ�������Ҫ��BCD��ת��Ϊ10���Ƶı���
			// BCD��ת10�����ǰ���ÿ4λ��Ӧһ��10����������ת�������磺1001��BCD��=9��10���ƣ�
			// ��ô���ص�16���ֽ� ǰ8λΪ����ŵ��ֽڡ���Ҫ�Ƚ�ǰ8���ֽ�ת��Ϊ2���ƣ�Ȼ���ڰ���ÿ4λ���һ��10�������ó�һ�������
			// 91 10 00 01 00 00 94 73
			byte buteb[] = m1Data.getpData();
			String kamianhao = ReadCardService.getM1CardNo(buteb);
			// ���ÿ���
			parm.setData("cardNO", kamianhao);

			// Desfire��
		} else if (strCardType.equalsIgnoreCase("DF")) {
			STDFAuth stDFAuth = new STDFAuth();
			stDFAuth.setDfaID(new byte[] { 0x01, 0x00, 0x00 });
			stDFAuth.keyNo = 0x01;
			stDFAuth.psamAPP = 0x11;
			stDFAuth.psamKeyIndex = 0x01;
			stDFAuth.psamKeyType = 0x01;
			ret = NJSMCardDriver.DF_AuthPro(hReader, stDFAuth);
			// �жϷ���ֵ
			if (ReturnRet(ret, parm) == -1) {
				return -1;
			}
			// ��ȡ������Ϣ
			STDFInfo stDFInfo = new STDFInfo();
			ret = NJSMCardDriver.DF_ReadCardInfo(hReader, stDFInfo);
			// �жϷ���ֵ
			if (ReturnRet(ret, parm) == -1) {
				return -1;
			}

			// �����
			String kamianhao = "";
			for (int i = 0; i < stDFInfo.getKamianhao().length; i++) {
				// ���������⣿��������
				String strByte = Integer.valueOf(
						Byte.toString(stDFInfo.getKamianhao()[i]), 16)
						.toString();// 16ת��Ϊ10����
				if (isDebug) {
					System.out.println("Desfire���ֽ�Ϊ��" + strByte);
				}
				// ������λ��0
				if (strByte.length() < 2) {
					strByte = addZeroForNum(strByte, 2);
				}
				kamianhao += strByte;

			}
			parm.setData("cardNO", kamianhao);

		} else {
			parm.setData("ERR_FLG", "δ֪������");
			return -1;
		}
		// ������;
		NJSMCardDriver.TK_PCD_Beep(255);

		return 0;

	}

	/**
	 * ������Ϣ������Desfire����M1����
	 * 
	 * @param parm
	 *            /// <param name="cardNO">���صĿ���� ����16</param> /// <param
	 *            name="identifyNO">���ص����֤��</param> /// <param
	 *            name="patientName">���ص�����</param> /// <param
	 *            name="healthNO">���صĽ���������</param> /// <param
	 *            name="siType">���ص�ҽ������</param> /// <param
	 *            name="siNO">���ص�ҽ����</param> /// <param
	 *            name="err">���صĴ�����Ϣ</param>
	 * 
	 * @return -1 ʧ�� 0 �ɹ�
	 */
	public static int ReadCardInfo(TParm parm) {

		// �жϷ���ֵ
		if (hReader < 0) {
			parm.setData(ERR_FLG, "���Ӷ�����ʧ��");
			return -1;
		}
		STCardInfo stCardInfo = new STCardInfo();

		// ����Ѱ����������һ���Ǳ�����õ�
		int i = NJSMCardDriver.FoundCardPro(hReader, stCardInfo);
		// �жϷ���ֵ
		if (ReturnRet(i, parm) == -1) {
			return -1;
		}

		// ���ؿ�����
		String strCardType = Byte.toString(stCardInfo.getCardType());
		int intCardType = Integer.valueOf(strCardType);
		String strHexCardType = Integer.toHexString(intCardType);
		if (isDebug) {
			System.out.println("strCardType������===" + strHexCardType);
		}
		// �����C��
		if (strHexCardType.equalsIgnoreCase("1A")) {
			// �����Ƚ�����֤,ÿ����ֻ֤�ܲ���һ������
			i = NJSMCardDriver.M1_AuthPro(hReader, 2);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// ��ȡ����
			M1Data m1Data = new M1Data();
			i = NJSMCardDriver.M1_ReadData(hReader, 2, m1Data);
			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// ����M1�Ŀ������BCD��洢�ģ�������Ҫ��BCD��ת��Ϊ10���Ƶı���
			// BCD��ת10�����ǰ���ÿ4λ��Ӧһ��10����������ת�������磺1001��BCD��=9��10���ƣ�
			// ��ô���ص�16���ֽ� ǰ8λΪ����ŵ��ֽڡ���Ҫ�Ƚ�ǰ8���ֽ�ת��Ϊ2���ƣ�Ȼ���ڰ���ÿ4λ���һ��10�������ó�һ�������
			// 91 10 00 01 00 00 94 73
			byte cardData[] = m1Data.getpData();
			String kamianhao = ReadCardService.getM1CardNo(cardData);// �����
			parm.setData("cardNO", kamianhao);

			// ��28-33�飨����31�鲻�����д������Ϣ��ȡ����
			// �����Ƚ�����֤,ÿ����ֻ֤�ܲ���һ������
			i = NJSMCardDriver.M1_AuthPro(hReader, 28);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// ��ȡ����
			M1Data b28 = new M1Data();
			i = NJSMCardDriver.M1_ReadData(hReader, 28, b28);
			// ��ȡ����
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// �����Ƚ�����֤,ÿ����ֻ֤�ܲ���һ������
			i = NJSMCardDriver.M1_AuthPro(hReader, 29);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// ��ȡ����
			M1Data b29 = new M1Data();
			i = NJSMCardDriver.M1_ReadData(hReader, 29, b29);
			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// �����Ƚ�����֤,ÿ����ֻ֤�ܲ���һ������
			i = NJSMCardDriver.M1_AuthPro(hReader, 30);
			if (ReturnRet(i, parm) == -1) {
				return -1;

			}

			M1Data b30 = new M1Data();
			i = NJSMCardDriver.M1_ReadData(hReader, 30, b30);
			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// �����Ƚ�����֤,ÿ����ֻ֤�ܲ���һ������
			i = NJSMCardDriver.M1_AuthPro(hReader, 32);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			M1Data b32 = new M1Data();
			i = NJSMCardDriver.M1_ReadData(hReader, 32, b32);
			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// �����Ƚ�����֤,ÿ����ֻ֤�ܲ���һ������
			i = NJSMCardDriver.M1_AuthPro(hReader, 33);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			M1Data b33 = new M1Data();
			i = NJSMCardDriver.M1_ReadData(hReader, 33, b33);
			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// ��������
			// 0-19 Ϊ������Ϣ
			byte[] bpatientName = new byte[] { b28.pData[0], b28.pData[1],
					b28.pData[2], b28.pData[3], b28.pData[4], b28.pData[5],
					b28.pData[6], b28.pData[7], b28.pData[8], b28.pData[9],
					b28.pData[10], b28.pData[11], b28.pData[12], b28.pData[13],
					b28.pData[14], b28.pData[15], b29.pData[0], b29.pData[1],
					b29.pData[2], b29.pData[3] };
			try {
				String strPatientName = new String(bpatientName, "gb2312");
				if (isDebug) {
					System.out.println("===strPatientName===" + strPatientName);
				}
				parm.setData("patientName", strPatientName);
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			// 20-39Ϊ����������
			byte[] bhealthNO = new byte[] { b29.pData[4], b29.pData[5],
					b29.pData[6], b29.pData[7], b29.pData[8], b29.pData[9],
					b29.pData[10], b29.pData[11], b29.pData[12], b29.pData[13],
					b29.pData[14], b29.pData[15], b30.pData[0], b30.pData[1],
					b30.pData[2], b30.pData[3], b30.pData[4], b30.pData[5],
					b30.pData[6], b30.pData[7] };
			try {
				String strHealthNO = new String(bhealthNO, "gb2312");
				if (isDebug) {
					System.out.println("===strHealthNO===" + strHealthNO);
				}
				parm.setData("healthNO", strHealthNO);
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			// 40-41Ϊҽ������
			byte[] bsiType = new byte[] { b30.pData[8], b30.pData[9] };
			try {
				String strSiType = new String(bsiType, "gb2312");
				if (isDebug) {
					System.out.println("===strSiType===" + strSiType);
				}
				parm.setData("siType", strSiType);
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			// 42-61Ϊҽ����
			byte[] bsiNO = new byte[] { b30.pData[10], b30.pData[11],
					b30.pData[12], b30.pData[13], b30.pData[14], b30.pData[15],
					b32.pData[0], b32.pData[1], b32.pData[2], b32.pData[3],
					b32.pData[4], b32.pData[5], b32.pData[6], b32.pData[7],
					b32.pData[8], b32.pData[9], b32.pData[10], b32.pData[11],
					b32.pData[12], b32.pData[13] };
			try {
				String strSiNO = new String(bsiNO, "gb2312");
				if (isDebug) {
					System.out.println("===strSiNO===" + strSiNO);
				}
				parm.setData("siNO", strSiNO);
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			// 40-41Ϊ���֤��
			byte[] bidentifityNO = new byte[] { b32.pData[14], b32.pData[15],
					b33.pData[0], b33.pData[1], b33.pData[2], b33.pData[3],
					b33.pData[4], b33.pData[5], b33.pData[6], b33.pData[7],
					b33.pData[8], b33.pData[9], b33.pData[10], b33.pData[11],
					b33.pData[12], b33.pData[13], b33.pData[14], b33.pData[15] };

			try {
				String strIdentifityNO = new String(bidentifityNO, "gb2312");
				if (isDebug) {
					System.out.println("===strIdentifityNO==="
							+ strIdentifityNO);
				}
				parm.setData("identifyNO", strIdentifityNO);
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}

				return -1;
			}
			// A��B��
		} else if (strHexCardType.equalsIgnoreCase("DF")) {
			// TODO ��û�п��� ��û�в��ԣ�
			STDFAuth stDFAuth = new STDFAuth();
			stDFAuth.setDfaID(new byte[] { 0x01, 0x00, 0x00 });
			stDFAuth.keyNo = 0x01;
			stDFAuth.psamAPP = 0x11;
			stDFAuth.psamKeyIndex = 0x01;
			stDFAuth.psamKeyType = 0x01;
			i = NJSMCardDriver.DF_AuthPro(hReader, stDFAuth);
			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// ��ȡ������Ϣ
			STDFInfo stDFInfo = new STDFInfo();
			i = NJSMCardDriver.DF_ReadCardInfo(hReader, stDFInfo);
			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// �����
			String kamianhao = getDCardNo(stDFInfo.getKamianhao());
			parm.setData("cardNO", kamianhao);
			// ֤�����ͣ�01Ϊ���֤�ţ�
			if (getHexString(stDFInfo.getZhengjianleixing()).equalsIgnoreCase(
					"01")) {
				String zhengjianbianhao = "";
				for (int j = 0; j < stDFInfo.getZhengjianbianhao().length; j++) {
					String strByte = getString(
							stDFInfo.getZhengjianbianhao()[j], 16);
					// ������λ��0
					if (strByte.length() < 2) {
						strByte = addZeroForNum(strByte, 2);
					}
					zhengjianbianhao += strByte;// ת��Ϊ16����
				}
				parm.setData("identifyNO", zhengjianbianhao);

			}

			// YY��֤
			i = NJSMCardYYDriver.YYAuthPro(hReader);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			NJYY njyy = new NJYY();
			i = NJSMCardYYDriver.YYReadData(hReader, njyy);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			//
			try {
				String strPatientName = new String(njyy.getSickName(),
						STRING_CODING);
				parm.setData("patientName", strPatientName);

			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			//
			byte[] bHealthNO = new byte[] { njyy.getErecordNo()[0],
					njyy.getErecordNo()[1], njyy.getErecordNo()[2],
					njyy.getErecordNo()[3], njyy.getErecordNo()[4],
					njyy.getErecordNo()[5], njyy.getErecordNo()[6],
					njyy.getErecordNo()[7], njyy.getErecordNo()[8],
					njyy.getErecordNo()[9], njyy.getErecordNo()[10],
					njyy.getErecordNo()[11], njyy.getErecordNo()[12],
					njyy.getErecordNo()[13], njyy.getErecordNo()[14],
					njyy.getErecordNo()[15], njyy.getErecordNo()[16],
					njyy.getErecordNo()[17], njyy.getErecordNo()[18],
					njyy.getErecordNo()[19] };

			try {
				String strHealthNO = new String(bHealthNO, STRING_CODING);
				parm.setData("healthNO", strHealthNO);

			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			//
			byte[] bSiType = new byte[] { njyy.getFlag()[0], njyy.getFlag()[1] };
			try {
				String strSiType = new String(bSiType, STRING_CODING);
				parm.setData("siType", strSiType);

			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			// Ϊҽ����
			byte[] bSiNO = new byte[] { njyy.getErecordNo()[20],
					njyy.getErecordNo()[21], njyy.getErecordNo()[22],
					njyy.getErecordNo()[23], njyy.getErecordNo()[24],
					njyy.getErecordNo()[25], njyy.getErecordNo()[26],
					njyy.getErecordNo()[27], njyy.getErecordNo()[28],
					njyy.getErecordNo()[29], njyy.getFlag()[2],
					njyy.getHspNo()[0], njyy.getHspNo()[1], njyy.getHspNo()[2],
					njyy.getHspNo()[3], njyy.getHspNo()[4], njyy.getHspNo()[5],
					njyy.getHspNo()[6], njyy.getHspNo()[7], njyy.getHspNo()[8] };
			try {
				String strSiNO = new String(bSiNO, "gb2312");
				if (isDebug) {
					System.out.println("===strSiNO===" + strSiNO);
				}
				parm.setData("siNO", strSiNO);
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}

		} else {
			if (isDebug) {
				System.out.println("============δ֪������=========");
			}
			parm.setData(ERR_FLG, "δ֪������");
			return -1;
		}
		return 0;

	}

	/**
	 * д����Ϣ������Desfire����M1����;
	 * 
	 * @param parm
	 *            <param name="cardNO">����� ����16 �������бȶ��Ƿ���ȷ</param> <param
	 *            name="identifyNO">���֤��</param> <param
	 *            name="patientName">����</param> <param
	 *            name="healthNO">����������</param> <param
	 *            name="siType">ҽ������</param> <param name="siNO">ҽ����</param>
	 *            <param name="err">������Ϣ</param>
	 * @return
	 */
	public static int WriteCardInfo(TParm parm) {
		String cardNO = parm.getValue("cardNO");
		String identifyNO = parm.getValue("identifyNO");
		String patientName = parm.getValue("patientName");
		String healthNO = parm.getValue("healthNO");
		String siType = parm.getValue("siType");
		String siNO = parm.getValue("siNO");

		// �жϷ���ֵ
		if (hReader < 0) {
			parm.setData(ERR_FLG, "���Ӷ�����ʧ��");
			return -1;
		}
		// ��ʼ���ڴ�
		STCardInfo st = new STCardInfo();
		// ����Ѱ����������һ���Ǳ�����õ�
		int i = NJSMCardDriver.FoundCardPro(hReader, st);
		// �жϷ���ֵ
		if (ReturnRet(i, parm) == -1) {
			return -1;
		}
		String strCardType = Byte.toString(st.getCardType());
		int intCardType = Integer.valueOf(strCardType);
		String strHexCardType = Integer.toHexString(intCardType);
		//
		if (isDebug) {
			System.out.println("��������===" + strHexCardType);
		}
		// M1��д��
		// �����Ƚ�����֤,ÿ����ֻ֤�ܲ���һ������
		if (strHexCardType.equalsIgnoreCase("1A")) {
			// �����Ƚ�����֤,ÿ����ֻ֤�ܲ���һ������
			i = NJSMCardDriver.M1_AuthPro(hReader, 2);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// ��ȡ����
			// ��ȡ����
			M1Data m1Data = new M1Data();
			i = NJSMCardDriver.M1_ReadData(hReader, 2, m1Data);
			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// ����M1�Ŀ������BCD��洢�ģ�������Ҫ��BCD��ת��Ϊ10���Ƶı���
			// BCD��ת10�����ǰ���ÿ4λ��Ӧһ��10����������ת�������磺1001��BCD��=9��10���ƣ�
			// ��ô���ص�16���ֽ� ǰ8λΪ����ŵ��ֽڡ���Ҫ�Ƚ�ǰ8���ֽ�ת��Ϊ2���ƣ�Ȼ���ڰ���ÿ4λ���һ��10�������ó�һ�������
			// 91 10 00 01 00 00 94 73
			byte cardData[] = m1Data.getpData();
			String kamianhao = ReadCardService.getM1CardNo(cardData);// �����
			if (!cardNO.equalsIgnoreCase(kamianhao)) {
				parm.setData(ERR_FLG, "���Ų�һ�£�����");
				return -1;
			}
			// 0-19 Ϊ������Ϣ
			byte[] bpatientName = null;
			// 20-39Ϊ����������
			byte[] bhealthNO = null;
			// 40-41Ϊҽ������
			byte[] bsiType = null;
			// 42-61Ϊҽ����
			byte[] bsiNO = null;
			// 40-41Ϊ���֤��
			byte[] bidentifityNO = null;
			//
			try {
				int length = patientName.getBytes(STRING_CODING).length;
				if (length > 20) {
					parm.setData(ERR_FLG, "��������̫��");
					return -1;
				} else {
					bpatientName = new byte[20];
					byte[] temp = patientName.getBytes(STRING_CODING);
					for (int m = 0; m < length; m++) {
						bpatientName[m] = temp[m];
					}

				}
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			//
			try {
				int length = healthNO.getBytes(STRING_CODING).length;
				if (length > 20) {
					parm.setData(ERR_FLG, "����������̫��");
					return -1;
				} else {
					bhealthNO = new byte[20];
					byte[] temp = healthNO.getBytes(STRING_CODING);
					for (int m = 0; m < length; m++) {
						bhealthNO[m] = temp[m];
					}
				}
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			//
			try {
				int length = siType.getBytes(STRING_CODING).length;
				if (length > 2) {
					parm.setData(ERR_FLG, "ҽ������̫��");
					return -1;
				} else {
					bsiType = new byte[20];
					byte[] temp = siType.getBytes(STRING_CODING);
					for (int m = 0; m < length; m++) {
						bsiType[m] = temp[m];
					}
				}
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			// 42-61Ϊҽ����
			try {
				int length = siNO.getBytes(STRING_CODING).length;
				if (length > 20) {
					parm.setData(ERR_FLG, "ҽ����̫��");
					return -1;
				} else {
					bsiNO = new byte[20];
					byte[] temp = siNO.getBytes(STRING_CODING);
					for (int m = 0; m < length; m++) {
						bsiNO[m] = temp[m];
					}
				}
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}

			// 40-41Ϊ���֤��
			try {
				int length = identifyNO.getBytes(STRING_CODING).length;
				if (length > 18) {
					parm.setData(ERR_FLG, "���֤��̫��");
					return -1;
				} else {
					bidentifityNO = new byte[20];
					byte[] temp = identifyNO.getBytes(STRING_CODING);
					for (int m = 0; m < length; m++) {
						bidentifityNO[m] = temp[m];
					}
				}
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			//
			M1Data data28 = new M1Data();
			byte block28[] = data28.getpData();
			block28 = new byte[] { bpatientName[0], bpatientName[1],
					bpatientName[2], bpatientName[3], bpatientName[4],
					bpatientName[5], bpatientName[6], bpatientName[7],
					bpatientName[8], bpatientName[9], bpatientName[10],
					bpatientName[11], bpatientName[12], bpatientName[13],
					bpatientName[14], bpatientName[15] };

			M1Data data29 = new M1Data();
			byte block29[] = data29.getpData();
			block29 = new byte[] { bpatientName[16], bpatientName[17],
					bpatientName[18], bpatientName[19], bhealthNO[0],
					bhealthNO[1], bhealthNO[2], bhealthNO[3], bhealthNO[4],
					bhealthNO[5], bhealthNO[6], bhealthNO[7], bhealthNO[8],
					bhealthNO[9], bhealthNO[10], bhealthNO[11], };

			M1Data data30 = new M1Data();
			byte block30[] = data30.getpData();
			block30 = new byte[] { bhealthNO[12], bhealthNO[13], bhealthNO[14],
					bhealthNO[15], bhealthNO[16], bhealthNO[17], bhealthNO[18],
					bhealthNO[19], bsiType[0], bsiType[1], bsiNO[0], bsiNO[1],
					bsiNO[2], bsiNO[3], bsiNO[4], bsiNO[5] };

			M1Data data32 = new M1Data();
			byte block32[] = data32.getpData();
			block32 = new byte[] { bsiNO[6], bsiNO[7], bsiNO[8], bsiNO[9],
					bsiNO[10], bsiNO[11], bsiNO[12], bsiNO[13], bsiNO[14],
					bsiNO[15], bsiNO[16], bsiNO[17], bsiNO[18], bsiNO[19],
					bidentifityNO[0], bidentifityNO[1] };
			M1Data data33 = new M1Data();
			byte block33[] = data33.getpData();
			block33 = new byte[] { bidentifityNO[2], bidentifityNO[3],
					bidentifityNO[4], bidentifityNO[5], bidentifityNO[6],
					bidentifityNO[7], bidentifityNO[8], bidentifityNO[9],
					bidentifityNO[10], bidentifityNO[11], bidentifityNO[12],
					bidentifityNO[13], bidentifityNO[14], bidentifityNO[15],
					bidentifityNO[16], bidentifityNO[17] };

			// ��28-33�飨����31�鲻�����д������Ϣ��ȡ����
			// �����Ƚ�����֤,ÿ����ֻ֤�ܲ���һ������
			i = NJSMCardDriver.M1_AuthPro(hReader, 28);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// д������
			i = NJSMCardDriver.M1_WriteData(hReader, 28, block28);
			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// �����Ƚ�����֤,ÿ����ֻ֤�ܲ���һ������
			i = NJSMCardDriver.M1_AuthPro(hReader, 29);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// д������
			i = NJSMCardDriver.M1_WriteData(hReader, 29, block29);
			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// �����Ƚ�����֤,ÿ����ֻ֤�ܲ���һ������
			i = NJSMCardDriver.M1_AuthPro(hReader, 30);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			i = NJSMCardDriver.M1_WriteData(hReader, 30, block30);
			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// �����Ƚ�����֤,ÿ����ֻ֤�ܲ���һ������
			i = NJSMCardDriver.M1_AuthPro(hReader, 32);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			i = NJSMCardDriver.M1_WriteData(hReader, 32, block32);
			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// �����Ƚ�����֤,ÿ����ֻ֤�ܲ���һ������
			i = NJSMCardDriver.M1_AuthPro(hReader, 33);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			i = NJSMCardDriver.M1_WriteData(hReader, 33, block33);
			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;

			}
			// д����������

		} else if (strHexCardType.equalsIgnoreCase("DF")) {
			// TODO ���޿��޷����� Desfire��д��
			// Desfire����֤
			STDFAuth stDFAuth = new STDFAuth();
			stDFAuth.setDfaID(new byte[] { 0x01, 0x00, 0x00 });
			stDFAuth.keyNo = 0x01;
			stDFAuth.psamAPP = 0x11;
			stDFAuth.psamKeyIndex = 0x01;
			stDFAuth.psamKeyType = 0x01;
			i = NJSMCardDriver.DF_AuthPro(hReader, stDFAuth);
			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// ��ȡ������Ϣ
			STDFInfo stDFInfo = new STDFInfo();
			i = NJSMCardDriver.DF_ReadCardInfo(hReader, stDFInfo);

			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// �����
			String kamianhao = getDCardNo(stDFInfo.getKamianhao());
			if (cardNO != kamianhao) {
				parm.setData(ERR_FLG, "���Ų�һ�£�����");
				return -1;
			}
			i = NJSMCardYYDriver.YYAuthPro(hReader);
			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			NJYY njyy = new NJYY();
			// ��������
			try {
				byte[] bPatientName = patientName.getBytes(STRING_CODING);
				for (int j = 0; j < 20; j++) {
					if (j < bPatientName.length) {
						njyy.sickName[j] = bPatientName[j];
						// ����20λ��0;
					} else {
						njyy.sickName[j] = 0;
					}
				}

			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			// ҽ������
			byte[] bSiType;
			try {
				bSiType = siType.getBytes(STRING_CODING);
				for (int j = 0; j < 2; j++) {
					if (j < bSiType.length) {
						njyy.flag[j] = bSiType[j];
					} else {
						njyy.flag[j] = 0;
					}
				}
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			// ����ǰ20λΪ���������� ��10λ Ϊҽ����
			byte[] bIdentifyNO;
			try {
				bIdentifyNO = healthNO.getBytes(STRING_CODING);
				for (int j = 0; j < 20; j++) {
					if (j < bIdentifyNO.length) {
						njyy.erecordNo[j] = bIdentifyNO[j];
					} else {
						njyy.erecordNo[j] = 0;
					}
				}
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
			}
			// ҽ����
			byte[] bSiNO;
			try {
				bSiNO = siNO.getBytes(STRING_CODING);
				for (int j = 20; j < 30; j++) {
					if (j - 20 < bSiNO.length) {
						njyy.erecordNo[j] = bSiNO[j - 20];
					} else {
						njyy.erecordNo[j] = 0;
					}
				}
				if (bSiNO.length >= 11) {
					njyy.flag[2] = bSiNO[10];
				} else {
					njyy.flag[2] = 0;
				}
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}

			for (int j = 0; j < 9; j++) {
				if (j + 11 < bSiNO.length) {
					njyy.hspNo[j] = bSiNO[j + 11];
				} else {
					njyy.hspNo[j] = 0;
				}
			}
			// д��
			i = NJSMCardYYDriver.YYWriteData(hReader, njyy);
			// �жϷ���ֵ
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// δ֪������
		} else {
			parm.setData(ERR_FLG, "δ֪������");
			return -1;
		}

		return 0;
	}

	/**
	 * �������ķ���
	 * 
	 * @param actionName
	 *            Action����
	 * @param actionMethod
	 *            ���÷�������
	 * @param parm
	 *            ������parameter(���봫������)|err���صĴ�����Ϣ
	 * @return -1 ʧ�� 0 �ɹ� 1 ���粻ͨ
	 */
	public static int InvokeISiteCenter(String actionName, String actionMethod,
			TParm parm) {
		int i;
		int li_result;
		int appCode = 0;
		// ��ȡ�����ļ�;
		String path = new ReadCardService().getClass().getResource(
				"/com/javahis/device").getPath();
		IniReader reader = null;
		try {
			reader = new IniReader(path + "/eapagent.ini");
		} catch (IOException e) {
			if (isDebug) {
				e.printStackTrace();
			}
			parm.setData(ERR_FLG, "��ȡ�����ļ�ʧ��.");
			return -1;
		}
		// ȡ���ķ�����IP��ַ;
		String ipaddress = reader.getValue("HTTP_SERVER", "HOST");
		// ������ͨ��
		if (ping(ipaddress)) {
			NJEapagentDriver.EapAgent_Init();
			NJEapagentDriver.EapAgent_Reset();
			NJEapagentDriver.EapAgent_SetAction(actionName);
			NJEapagentDriver.EapAgent_SetActionMethod(actionMethod);
			NJEapagentDriver.EapAgent_PutParameter("put", parm
					.getValue("parameter"));
			EapReturnParm ret_paramemter = new EapReturnParm();
			li_result = NJEapagentDriver.EapAgent_SendRequest();
			if (li_result != 0) {
				parm.setData(ERR_FLG, "��������ʧ�ܣ���鿴��־");
				return -1;
			} else {
				EapAppCode eapAppCode = new EapAppCode();
				NJEapagentDriver.EapAgent_GetAppCode(eapAppCode);
				appCode = eapAppCode.appCode;
				if (appCode != 0) {
					EapErrorMessage eapErrorMessage = new EapErrorMessage();
					i = NJEapagentDriver
							.EapAgent_GetBriefErrorMessage(eapErrorMessage);
					parm.setData(ERR_FLG, eapErrorMessage
							.getBriefErrorMessage());
					return -1;

				} else {
					i = NJEapagentDriver.EapAgent_GetParameter("out",
							ret_paramemter);
					parm.setData("parameter", ret_paramemter.getParaValue());
				}

			}

		} else {
			parm.setData(ERR_FLG, "���粻ͨ����������");
			return 1;
		}

		return 0;
	}

	/**
	 * ִ��ping����;
	 * 
	 * @param host
	 *            ������ַ
	 * @return
	 */
	private static boolean ping(String host) {
		int timeOut = 3000; // ��ʱӦ����3������
		boolean status = false;
		try {
			status = InetAddress.getByName(host).isReachable(timeOut);
		} catch (UnknownHostException e) {
			if (isDebug) {
				e.printStackTrace();
			}
			return false;
		} catch (IOException e) {
			if (isDebug) {
				e.printStackTrace();
			}
			return false;
		}
		return status;
	}

	/**
	 * ���M1������;
	 * 
	 * @param buteb
	 * @return
	 */
	private static String getM1CardNo(byte buteb[]) {
		String kamianhao = "";// �����
		for (int i = 0; i < 8; i++) {
			// ������
			String strBin = Integer.toBinaryString(Integer.valueOf(Byte
					.toString(buteb[i])));
			if (isDebug) {
				System.out.println("�ֽ�Ϊ:" + strBin);
			}
			strBin = addZeroForNum(strBin, 8);
			if (isDebug) {
				System.out.println("�ֽڲ�0��:" + strBin);
			}
			// ����8λȡ��8λ;
			if (strBin.length() > 8) {
				strBin = strBin.substring(strBin.length() - 8, strBin.length());
			}
			if (isDebug) {
				System.out.println("������Ϊ:" + strBin);
				System.out.println("������ǰ4λ:"
						+ String.valueOf(Integer.parseInt(strBin
								.substring(0, 4), 2)));
				System.out.println("�����ƺ�4λ====="
						+ String.valueOf(Integer.parseInt(strBin
								.substring(4, 8), 2)));
			}

			kamianhao += String.valueOf(Integer.parseInt(
					strBin.substring(0, 4), 2))
					+ String.valueOf(Integer
							.parseInt(strBin.substring(4, 8), 2));

		}
		if (isDebug) {
			System.out.println("==kamianhao==" + kamianhao);
		}
		return kamianhao;
	}

	/**
	 * ���D���Ŀ���
	 * 
	 * @param bKamianhao
	 * @return
	 */
	private static String getDCardNo(byte bKamianhao[]) {
		String kamianhao = "";
		for (int j = 0; j < bKamianhao.length; j++) {
			// ���������⣿��������
			String strByte = Integer.valueOf(Byte.toString(bKamianhao[j]), 16)
					.toString();// 16ת��Ϊ10����
			if (isDebug) {
				System.out.println("Desfire���ֽ�Ϊ��" + strByte);
			}
			// ������λ��0
			if (strByte.length() < 2) {
				strByte = addZeroForNum(strByte, 2);
			}
			kamianhao += strByte;

		}

		return kamianhao;

	}

	/**
	 * ��byte�����ƣ�ת��ʮ�����ַ���
	 * 
	 * @param b
	 * @param radix
	 * @return
	 */
	private static String getString(byte b, int radix) {

		return Integer.valueOf(Byte.toString(b), radix).toString();
	}

	/**
	 * ת��16�����ִ�
	 * 
	 * @param b
	 * @return
	 */
	private static String getHexString(byte b) {

		String strB = Byte.toString(b);
		int intB = Integer.valueOf(strB);
		String strHexByte = Integer.toHexString(intB);

		return strHexByte;
	}

	/*
	 * 
	 * ���ֲ���λ����0
	 * 
	 * @param str
	 * 
	 * @param strLength
	 */
	private static String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append("0").append(str);// ��0
				// sb.append(str).append("0");//�Ҳ�0
				str = sb.toString();
				strLen = str.length();
			}
		}

		return str;
	}

	public static void main(String args[]) {
		System.out.println("==============��Ԫ����==============");
		// 1.��ʼ��;
		/**
		 * int ret0 = NJSMCardDriver.init();
		 * System.out.println("====����dll======" + ret0);
		 **/
		// 2.���Ӷ�����
		/**
		 * int ret1 = ReadCardService.LinkReaderPro();
		 * System.out.println("====�������======" + ret1);
		 **/
		// 3.�������ͼ���;
		// int ret2 = ReadCardService.ReadCardNO(new TParm());
		// System.out.println("====�������ͼ���=====" + ret2);

		// 4.������Ϣ

		/**
		 * TParm p3 = new TParm(); int ret3 = ReadCardService.ReadCardInfo(p3);
		 * System.out.println("=====������Ϣret3======" + ret3); if (ret3 == -1) {
		 * System.out.println("������Ϣ��=====" + p3.getData(ERR_FLG)); return; }
		 * System.out.println("���ţ�" + p3.getValue("cardNO"));
		 * System.out.println("���֤�ţ�" + p3.getValue("identifyNO"));
		 * System.out.println("������" + p3.getValue("patientName"));
		 * System.out.println("���������ţ�" + p3.getValue("healthNO"));
		 * System.out.println("ҽ�����ͣ�" + p3.getValue("siType"));
		 * System.out.println("ҽ���ţ�" + p3.getValue("siNO"));
		 **/

		// 5.д����Ϣ;
		// byte b = 0x11;
		// System.out.println("==test==" + Integer.valueOf(Byte.toString(b),
		// 16));

		// 6.�ͷŶ����豸
		// int ret99 = NJSMCardDriver.FreeReader(ret0);
		// 7.ע��TFReader.dll
		// int ret100 = NJSMCardDriver.close();

		// 8.��ȡ�����ļ�
		// System.out.println(new
		// ReadCardService().getClass().getResource("/com/javahis/device").getPath());

		// 9.
		/**
		 * String path=new
		 * ReadCardService().getClass().getResource("/com/javahis/device"
		 * ).getPath(); try { IniReader reader = new
		 * IniReader(path+"/TFReaderConfig.ini"); String strPort =
		 * reader.getValue("COM", "port"); String strBaud =
		 * reader.getValue("COM", "Baud"); System.out.println("===strPort===" +
		 * strPort); System.out.println("===strBaud===" + strBaud);
		 * 
		 * } catch (IOException e) { e.printStackTrace(); }
		 **/
		// System.out.println("ping========="+ping("10.43.45.3"));

	}

	/**
	 * ��ȡini�����ļ���
	 * 
	 * @author lixiang
	 * 
	 */
	public static class IniReader {

		protected HashMap sections = new HashMap();
		private transient String currentSecion;
		private transient Properties current;

		public IniReader(String filename) throws IOException {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			read(reader);
			reader.close();
		}

		/**
		 * 
		 * @param reader
		 * @throws IOException
		 */
		protected void read(BufferedReader reader) throws IOException {
			String line;
			while ((line = reader.readLine()) != null) {
				parseLine(line);
			}
		}

		/**
		 * 
		 * @param line
		 */
		protected void parseLine(String line) {
			line = line.trim();
			if (line.matches("\\[.*\\]")) {
				// ����� JDK 1.4(����1.4)���°汾���޸�Ϊ
				// if (line.startsWith("[") && line.endsWith("]")) {
				if (current != null) {
					sections.put(currentSecion, current);
				}
				currentSecion = line.replaceFirst("\\[(.*)\\]", "$1");
				// JDK ���� 1.4 ʱ
				// currentSection = line.substring(1�� line.length() - 1);
				current = new Properties();
			} else if (line.matches(".*=.*")) {
				// JDK ���� 1.4 ʱ
				// } else if (line.indexOf(��=��) >= 0) {
				int i = line.indexOf('=');
				String name = line.substring(0, i);
				String value = line.substring(i + 1);
				current.setProperty(name, value);
			}
		}

		/**
		 * 
		 * @param section
		 * @param name
		 * @return
		 */
		public String getValue(String section, String name) {
			Properties p = (Properties) sections.get(section);

			if (p == null) {
				return null;
			}

			String value = p.getProperty(name);
			return value;
		}

	}
}
