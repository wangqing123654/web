package jdo.sta;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: У�������
 * </p>
 * 
 * <p>
 * Description: У�����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author caowl
 * @version 1.0
 */

public class STAValidator extends TJDOTool {

	// �Ա����ԣ�/�������ICD10����
	private String[] ICD10ForMail = { "B26.0", "C60", "C61", "C62", "C63",
			"D07.4", "D07.5", "D07.6", "D17.6", "D29", "D40", "E29", "E89.5",
			"F52.4", "I86.1", "L29.1", "N40", "N41", "N42", "N43", "N44",
			"N45", "N46", "N47", "N48", "N49", "N50", "N51", "Q53", "Q54",
			"Q55", "R86", "S31.2", "S31.3", "Z12.5" };
	// �Ա�Ů�ԣ�/�������ICD10����
	private String[] ICD10ForFemail = { "A34", "B37.3", "C51", "C52", "C53",
			"C54", "C55", "C56", "C57", "C58", "C79.6", "D06", "D07.0",
			"D07.1", "D07.2", "D07.3", "D25", "D26", "D27", "D28", "D39",
			"E28", "E89.4", "F52.5", "F53", "I86.3", "L29.2", "M80.0", "M80.1",
			"M81.0", "M81.1", "M83.0", "N70", "N71", "N72", "N73", "N74",
			"N75", "N76", "N77", "N78", "N79", "N80", "N81", "N82", "N83",
			"N84", "N85", "N86", "N87", "N88", "N89", "N90", "N91", "N92",
			"N93", "N94", "N95", "N96", "N97",
			"N98",
			"N99.2",
			"N99.3",
			"P54.6", // O00-O99
			"Q50", "Q51", "Q52", "R87", "S31.4", "S37.4", "S37.5", "S37.6",
			"T19.2", "T19.3", "T83.3", "Z01.4", "Z12.4", "Z30.1", "Z30.3",
			"Z30.5", "Z31.1", "Z31.2", "Z32", "Z33", "Z34", "Z35", "Z36",
			"Z37", "Z39", "Z87.5", "Z97.5" };
	// �Ա����ԣ�/������������
	// private String[] operatorCodeForMail = { "60","61","62","63","64",
	// "87.9", "98.24", "99.94" };
	// �Ա�Ů�ԣ�/������������
	// private String[] operatorCodeForFemail = { "65","66","67","68","69",
	// "70","71","72","73","74",
	// "75", "87.8", "88.46","88.78",
	// "89.26", "92.17", "96.14","96.15","96.16",
	// "96.17","96.18", "97.7", "98.16","98.17" };

	// ������Ϣ
	private String error1Msg = "ֻ���������ԣ�B26.0��C60-C63��D07.4-D07.6��D17.6��D29��D40��E29��E89.5��F52.4��I86.1��L29.1��N40-N51��Q53-Q55��R86��S31.2-S31.3��Z12.5";
	private String error2Msg = "ֻ������Ů�ԣ�A34��B37.3��C51-C58��C79.6��D06��D07.0-D07.3��D25-D28��D39��E28��E89.4��F52.5��F53��I86.3��L29.2��L70.5��M80.0-M80.1��M81.0-M81.1��"
			+ "M83.0��N70-N98��N99.2-N99.3��O00-O99��P54.6��Q50-Q52��R87��S31.4��S37.4-S37.6��T19.2-T19.3��T83.3��Z01.4��Z12.4��Z30.1��Z30.3��Z30.5��Z31.1-Z31.2��Z32-Z36��Z39��"
			+ "Z43.7��Z87.5��Z97.5";
	// private String error3Msg="ֻ���������ԣ�60-64��87.9��98.24��99.94";
	// private String
	// error4Msg="ֻ������Ů�ԣ�65-75��87.8��88.46��88.78��89.26��92.17��96.14-96.18��97.7��98.16-98.17";
	// private String error5Msg="������ƽ������Ϊ��4��������Ժ��ʽΪ��5��";
	private String error6Msg = "����=��Ժ����-��������";
	// private String error7Msg="��Ժ����Ӧ���ڵ����ϴγ�Ժ����";
	private String error8Msg = "��Ժ����<=��Ժ���ڣ���Ժ����<=��Ժ��ȷ������<=��Ժ���ڣ���Ժ����<=����������<=��Ժ����";
	private String error9Msg = "��(��)����ϱ���(ICD-10)����Ժ��ϱ���(ICD-10)����Ҫ��ϱ���(ICD-10)��������ϱ���(ICD-10)������ϱ��뷶ΧӦΪ��A��U��ͷ��Z��ͷ�ı��룻��������ĸV��W��X��Y��ͷ�ı���"
			+ "ҽԺ��Ⱦ���Ʊ���(ICD-10)������뷶ΧӦΪ��A00-U99��Z00-Z99��������M80000/0-M99999/6��������ĸV��W��X��Y��ͷ�ı���";
	private String error10Msg = "����(��)����ϱ���(ICD-10)����Ժ��ϱ���(ICD-10)����Ҫ��ϱ���(ICD-10)��������ϱ���(ICD-10)����P10-P15����Ժ����-�������ڡ�28�죬�����䲻��1����(��)������<=28��";
	private String error11Msg = "���з���������������γ�Ժ������ϱ����з����֣���Z37����";
	private String error12Msg = "ICD-10��̬ѧ���뷶Χ��M8��M9��ͷ��";
	// private String
	// error13Msg="����Ҫ��ϱ���(ICD-10)Ϊ��S00-T99�������˺��ж��ⲿԭ�����(ICD-10)�����˺��ж��ⲿԭ�����Ʊ���";
	// private String error14Msg="ICD-10���뷶Χ��V00-Y99";
	private String error15Msg = "���ȴ���Ӧ�������ȳɹ�����������Ҫ��ϳ�Ժ�����������ϳ�Ժ�������Ժ��ʽΪ����ʱ�����ȴ������Ե������ȳɹ�������1����ʾ����δ�ɹ�����";
	private String error16Msg = "ʵ��סԺ(��)>=�ػ�(��)+һ������(��)+��������(��)+��������(��)";
	private String error17Msg = "���Ʒ��䲡��ICD����ΪZ37�������<=28�������������";
	private String error18Msg = "����<=28�������������";
	private String error19Msg = "����<1�����Ӥ������";
	private String error20Msg = "��Ժ����ʱ��<=�໤�ҽ�������ʱ��<�໤���˳�����ʱ��<=��Ժ����ʱ��";
	private String error21Msg = "�ܷ���=���������֮��";
	private String error22Msg = "�������>=0";
	SimpleDateFormat Df = new SimpleDateFormat();

	/**
	 * ʵ��
	 */
	public static STAValidator instanceObject;
	private int compareTo;

	/**
	 * �õ�ʵ��
	 * 
	 * @return RegMethodTool
	 */
	public static STAValidator getInstance() {
		if (instanceObject == null)
			instanceObject = new STAValidator();
		return instanceObject;
	}

	public STAValidator() {
		onInit();
	}

	/**
	 * У�鷽��
	 * */
	public TParm checkUI(TParm initParm) {
		TParm result = new TParm();
		// System.out.println(initParm);
		StringBuffer errorBuffer = new StringBuffer();
		// ��һ�����ǿ�У��
		// 1.P900 ҽ�ƻ�������
		String P900 = initParm.getValue("P900");
		if (P900 == null || P900.length() == 0) {
			errorBuffer.append("<ERR:P900ҽ�ƻ������벻��Ϊ��>\r\n");
		}
		// 2.P6891 ��������
		String P6891 = initParm.getValue("P6891");
		if (P6891 == null || P6891.length() == 0) {
			errorBuffer.append("<ERR:P6891�������Ʋ���Ϊ��>\r\n");
		}
		// 5.P1 ҽ�ƿ����ʽ
		String P1 = initParm.getValue("P1");
		if (P1 == null || P1.length() == 0) {
			errorBuffer.append("<ERR:P1ҽ�ƿ����ʽ����Ϊ��>\r\n");
		}
		// 6.P2סԺ����
		String P2 = initParm.getValue("P2");
		if (P2 == null || P2.length() == 0) {
			errorBuffer.append("<ERR:P2סԺ��������Ϊ��>\r\n");
		}
		// 7.P3������
		String P3 = initParm.getValue("P3");
		if (P3 == null || P3.length() == 0) {
			errorBuffer.append("<ERR:P3�����Ų���Ϊ��>\r\n");
		}
		// 8.P4����
		String P4 = initParm.getValue("P4");
		if (P4 == null || P4.length() == 0) {
			errorBuffer.append("<ERR:P4��������Ϊ��>\r\n");
		}
		// 9.P5�Ա�
		Integer P5 = initParm.getInt("P5");
		if (P5 == null || P5.equals("")) {
			errorBuffer.append("<ERR:P5�Ա���Ϊ��>\r\n");
		}
		Integer sex = P5;

		// 12.P8����״��
		String P8 = initParm.getValue("P8");
		if (P8 == null || P8.length() == 0) {
			errorBuffer.append("<ERR:P8����״������Ϊ��>\r\n");
		}
		// 31.P804 ��Ժ;��
		String P804 = initParm.getValue("P804");
		if (P804 == null || P804.length() == 0) {
			errorBuffer.append("<ERR:P804��Ժ;������Ϊ��>\r\n");
		}
		// 33.P22 ��Ժ����
		String P22 = initParm.getValue("P22");
		if (P22 == null || P22.length() == 0) {
			errorBuffer.append("<ERR:P22��Ժ���ڲ���Ϊ��>\r\n");
		}
		// 34.P23 ��Ժ�Ʊ�
		String P23 = initParm.getValue("P23");
		if (P23 == null || P23.length() == 0) {
			errorBuffer.append("<ERR:P23��Ժ�Ʊ���Ϊ��>\r\n");
		}

		// 37.P25 ��Ժ����
		String P25 = initParm.getValue("P25");
		if (P25 == null || P25.length() == 0) {
			errorBuffer.append("<ERR:P25��Ժ���ڲ���Ϊ��>\r\n");
		}
		// 38.P26 ��Ժ�Ʊ�
		String P26 = initParm.getValue("P26");
		if (P26 == null || P26.length() == 0) {
			errorBuffer.append("<ERR:P26��Ժ�Ʊ���Ϊ��>\r\n");
		}
		// 40.P27 ʵ��סԺ����
		Integer P27 = initParm.getInt("P27");
		if (P27 == null || P27.equals("")) {
			errorBuffer.append("<ERR:P27ʵ��סԺ��������Ϊ��>\r\n");
		}
		// 41.P28 �ţ���������ϱ���
		String P28 = initParm.getValue("P28");
		if (P28 == null || P28.length() == 0) {
			errorBuffer.append("<ERR:P28�ţ���������ϱ��벻��Ϊ��>\r\n");
		}
		// 42.P281 �ţ��������������
		String P281 = initParm.getValue("P281");
		if (P281 == null || P281.length() == 0) {
			errorBuffer.append("<ERR:P281�ţ������������������Ϊ��>\r\n");
		}
//		// 43.P29 ��Ժʱ���
//		String P29 = initParm.getValue("P29");
//		if (P29 == null || P29.length() == 0) {
//			errorBuffer.append("<ERR:P29��Ժʱ�������Ϊ��>\r\n");
//		}
		// 44.P30 ��Ժ��ϱ���
		String P30 = initParm.getValue("P30");
		if (P30 == null || P30.length() == 0) {
			errorBuffer.append("<ERR:P30��Ժ��ϱ��벻��Ϊ��>\r\n");
		}
		// 45.P301 ��Ժ�������
		String P301 = initParm.getValue("P301");
		if (P301 == null || P301.length() == 0) {
			errorBuffer.append("<ERR:P301��Ժ�����������Ϊ��>\r\n");
		}
		// 46.P31 ��Ժ��ȷ������
		String P31 = initParm.getValue("P31");
		if (P31.length() == 0 || P31 == null) {
			errorBuffer.append("<ERR:P31��Ժ��ȷ�����ڲ���Ϊ��>\r\n");
		}
		// 47.P321 ��Ҫ��ϱ���
		// System.out.println( "----->\r\n"+initParm.getValue("P321"));
		String P321 = initParm.getValue("P321");
		if (P321 == null || P321.length() == 0) {
			errorBuffer.append("<ERR:P321��Ҫ��ϱ��벻��Ϊ��>\r\n");
		}
		// 48.P322 ��Ҫ��ϼ�������
		String P322 = initParm.getValue("P322");
		if (P322 == null || P322.length() == 0) {
			errorBuffer.append("<ERR:P322��Ҫ��ϼ�����������Ϊ��>\r\n");
		}
		// 121.P431 ������
		String P431 = initParm.getValue("P431");
		if (P431 == null || P431.length() == 0) {
			errorBuffer.append("<ERR:P431�����β���Ϊ��>\r\n");
		}
		// 122.P432 ��(����)��ҽʦ
		String P432 = initParm.getValue("P432");
		if (P432 == null || P431.length() == 0) {
			errorBuffer.append("<ERR:P432��(����)��ҽʦ����Ϊ��>\r\n");
		}
		// 123.P433 ����ҽʦ
		String P433 = initParm.getValue("P433");
		if (P433 == null || P433.length() == 0) {
			errorBuffer.append("<ERR:P433����ҽʦ����Ϊ��>\r\n");
		}
		// 124.P434 סԺҽʦ
		String P434 = initParm.getValue("P434");
		if (P434 == null || P434.length() == 0) {
			errorBuffer.append("<ERR:P434סԺҽʦ����Ϊ��>\r\n");
		}
		// 125.P819 ���λ�ʿ
		String P819 = initParm.getValue("P819");
		if (P819 == null || P819.length() == 0) {
			errorBuffer.append("<ERR:P819���λ�ʿ����Ϊ��>\r\n");
		}

		// 130.P44 ��������
		String P44 = initParm.getValue("P44");
		if (P44 == null || P44.length() == 0) {
			errorBuffer.append("<ERR:P44������������Ϊ��>\r\n");
		}
		// 131.P45 �ʿ�ҽʦ
		String P45 = initParm.getValue("P45");
		if (P45 == null || P45.length() == 0) {
			errorBuffer.append("<ERR:P45�ʿ�ҽʦ����Ϊ��>\r\n");
		}
		// 132.P46 �ʿػ�ʦ
		String P46 = initParm.getValue("P46");
		if (P46 == null || P46.length() == 0) {
			errorBuffer.append("<ERR:P46�ʿػ�ʦ����Ϊ��>\r\n");
		}
		// 291.P62 ABOѪ��
		String P62 = initParm.getValue("P62");
		if (P62 == null || P62.length() == 0) {
			errorBuffer.append("<ERR:P62ABOѪ�Ͳ���Ϊ��>\r\n");
		}
		// 292.P63 RhѪ��
		String P63 = initParm.getValue("P63");
		if (P63 == null || P63.length() == 0) {
			errorBuffer.append("<ERR:P63RhѪ�Ͳ���Ϊ��>\r\n");
		}
		// 314.P741 ��Ժ��ʽ
		String P741 = initParm.getValue("P741");
		if (P741 == null || P741.length() == 0) {
			errorBuffer.append("<ERR:P741��Ժ��ʽ����Ϊ��>\r\n");
		}
		// 317.P782 סԺ�ܷ���
		Double P782 = initParm.getDouble("P782");
		if (P782 == null || P782.equals("")) {
			errorBuffer.append("<ERR:P782סԺ�ܷ��ò���Ϊ��>\r\n");
		}
		// �ڶ�������ϱ���ICD10У��

		// �ţ���������ϱ���
		if (P28 != null && !P28.equals("")) {
			if (!(this.checkICD10(sex, P28))) {

				if (sex == 1) {

					errorBuffer
							.append("<ERR:P28 �ţ���������ϱ��� error1" + this.error1Msg + ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P28  �ţ���������ϱ���  error2 " + this.error2Msg
							+ ">\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P28))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P28 error9  �ţ���������ϱ���  " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P28 error9 �ţ���������ϱ��� " + this.error9Msg
							+ ">\r\n");
				}

			}
		}

		// ��Ժ��ϱ���
		if (P30 != null && !P30.equals("")) {
			if (!(this.checkICD10(sex, P30))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P30 ��Ժ��ϱ��� error1 " + this.error1Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P30 ��Ժ��ϱ��� error2 " + this.error2Msg
							+ ">\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P30))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P30 ��Ժ��ϱ��� error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P30  ��Ժ��ϱ��� error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// ��Ҫ��ϱ���
		if (P321 != null && !P321.equals("")) {
			if (!(this.checkICD10(sex, P321))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P321 ��Ҫ��ϱ��� error1 " + this.error1Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P321 ��Ҫ��ϱ���  error2 " + this.error2Msg
							+ ">\r\n");
				}

			}

			if (!(this.checkICD10Other(sex, P321))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P321 ��Ҫ��ϱ���  error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P321 ��Ҫ��ϱ��� error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// ����Ҫ��ϱ���(ICD-10)Ϊ��S00-T99�������˺��ж��ⲿԭ�����(ICD-10)�����˺��ж��ⲿԭ�����Ʊ���

		// if (P321 != null && (P321.substring(0).equals("S") ||
		// P321.substring(0).equals("T"))) {
		// if (initParm.getValue("P361") == null
		// || initParm.getValue("P361").equals("")) {
		//			
		// errorBuffer
		// .append("<ERR:P361  error13 "+this.error13Msg+">\r\n");
		//				
		// }
		// // ICD-10���뷶Χ��V00-Y99
		// if (!(initParm.getValue("P361").substring(0).equals("V")
		// || initParm.getValue("P361").substring(0).equals("W")
		// || initParm.getValue("P361").substring(0).equals("X") || initParm
		// .getValue("P361").substring(0).equals("Y"))) {
		//			
		// errorBuffer.append("<ERR:P361 error14 "+this.error14Msg+">\r\n");
		//			
		// }
		// if (initParm.getValue("P362") == null
		// || initParm.getValue("P362").equals("")) {
		//			
		// errorBuffer
		// .append("<ERR:P362 error13 "+this.error13Msg+">\r\n");
		//				
		// }
		// }
		// ������ϱ���
		String P324 = initParm.getValue("P324");
		if (P324 != null && !P324.equals("")) {
			if (!(this.checkICD10(sex, P324))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P324 ������ϱ��� 1 error1 " + this.error1Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P324 ������ϱ��� 1 error2 " + this.error2Msg
							+ ">\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P324))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P324 ������ϱ��� 1 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P324 ������ϱ��� 1 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}

		// ������ϱ���2
		String P327 = initParm.getValue("P327");
		if (P327 != null && !P327.equals("")) {
			if (!(this.checkICD10(sex, P327))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P327 ������ϱ���2 error1 " + this.error1Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P327  ������ϱ���2 error2 " + this.error2Msg
							+ ">\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P327))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P327 ������ϱ���2 error9 " + this.error9Msg+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P327 ������ϱ���2 error9 " + this.error9Msg+ ">\r\n");
				}

			}
		}
		// ������ϱ���3
		String P3291 = initParm.getValue("P3291");
		if (P3291 != null && !P3291.equals("")) {
			if (!(this.checkICD10(sex, P3291))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3291   ������ϱ���3 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3291  ������ϱ���3  error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P3291))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3291  ������ϱ���3 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3291  ������ϱ���3 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// ������ϱ���4
		String P3294 = initParm.getValue("P3294");
		if (P3294 != null && !P3294.equals("")) {
			if (!(this.checkICD10(sex, P3294))) {

				if (sex == 1) {
					errorBuffer.append("<ERR:P3294 ������ϱ���4 error1 " + this.error1Msg+ " >\r\n");
				}
				if (sex == 2) {
					errorBuffer.append("<ERR:P3294 ������ϱ���4 error2 " + this.error2Msg+ " >\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P3294))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3294 ������ϱ���4 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3294 ������ϱ���4 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// ������ϱ���5
		String P3297 = initParm.getValue("P3297");
		if (P3297 != null && !P3297.equals("")) {
			if (!(this.checkICD10(sex, P3297))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3297 ������ϱ���5 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3297 ������ϱ���5 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P3297))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3297 ������ϱ���5 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3297 ������ϱ���5 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// ������ϱ���6
		String P3281 = initParm.getValue("P3281");
		if (P3281 != null && !P3281.equals("")) {
			if (!(this.checkICD10(sex, P3281))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3281 ������ϱ���6 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3281 ������ϱ���6 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P3281))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3281 ������ϱ���6 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3281 ������ϱ���6 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// ������ϱ���7
		String P3284 = initParm.getValue("P3284");
		if (P3284 != null && !P3284.equals("")) {
			if (!(this.checkICD10(sex, P3284))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3284 ������ϱ���7 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3284 ������ϱ���7 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P3284))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3284 ������ϱ���7 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3284 ������ϱ���7 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// ������ϱ���8
		String P3287 = initParm.getValue("P3287");
		if (P3287 != null && !P3287.equals("")) {
			if (!(this.checkICD10(sex, P3287))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3287 ������ϱ���8 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3287 ������ϱ���8 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P3287))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3287 ������ϱ���8 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3287 ������ϱ���8 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// ������ϱ���9
		String P3271 = initParm.getValue("P3271");
		if (P3271 != null && !P3271.equals("")) {
			if (!(this.checkICD10(sex, P3271))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3271 ������ϱ���9 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3271 ������ϱ���9 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P3271))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3271 ������ϱ���9 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3271 ������ϱ���9 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// ������ϱ���10
		String P3274 = initParm.getValue("P3274");
		if (P3274 != null && !P3274.equals("")) {
			if (!(this.checkICD10(sex, P3274))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3274 ������ϱ���10 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3274 ������ϱ���10  error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P3274))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3274  ������ϱ���10 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3274  ������ϱ���10 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// ������ϱ���1
		String P351 = initParm.getValue("P351");
		// ICD-10���뷶Χ��M80000/0-M99999/6
		if (P351 != null && !P351.equals("")) {
			if (!(StringTool.compareTo(P351, "M80000/0") > 0 && StringTool
					.compareTo("M99999/6", P351) > 0)) {
				{
					errorBuffer.append("<ERR:P351 ������ϱ���1 error12 " + this.error12Msg
							+ " >\r\n");
				}

			}

			if (!(this.checkICD10(sex, P351))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P351 ������ϱ���1  error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P351������ϱ���1 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
		}
		// ������ϱ���2
		String P353 = initParm.getValue("P353");
		// ICD-10���뷶Χ��M80000/0-M99999/6
		if (P353 != null && !P353.equals("")) {
			if (!(StringTool.compareTo(P353, "M80000/0") > 0 && StringTool
					.compareTo("M99999/6", P353) > 0)) {
				{
					errorBuffer.append("<ERR:P353 ������ϱ���2 error12 " + this.error12Msg
							+ " >\r\n");
				}

			}

			if (!(this.checkICD10(sex, P353))) {
				if (sex == 1) {
					errorBuffer.append("<ERR:P353 ������ϱ���2 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {
					errorBuffer.append("<ERR:P353 ������ϱ���2 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
		}
		// ������ϱ���3
		String P355 = initParm.getValue("P355");
		// ICD-10���뷶Χ��M80000/0-M99999/6
		if (P355 != null && !P355.equals("")) {
			if (!(StringTool.compareTo(P355, "M80000/0") > 0 && StringTool
					.compareTo("M99999/6", P355) > 0)) {
				{
					errorBuffer.append("<ERR:P355 ������ϱ���3 error12 " + this.error12Msg
							+ " >\r\n");
				}

			}

			if (!(this.checkICD10(sex, P355))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P355  ������ϱ���3 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P355 ������ϱ���3 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
		}
		// ���ˡ��ж����ⲿ���ر���1
		String P361 = initParm.getValue("P361");
		if (P361 != null && !P361.equals("")) {
			if (!(this.checkICD10(sex, P361))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P361 ���ˡ��ж����ⲿ���ر���1 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P361  ���ˡ��ж����ⲿ���ر���1 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
		}
		// ���ˡ��ж����ⲿ���ر���2
		String P363 = initParm.getValue("P363");
		if (P363 != null && !P363.equals("")) {
			if (!(this.checkICD10(sex, P363))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P363  ���ˡ��ж����ⲿ���ر���2 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P363 ���ˡ��ж����ⲿ���ر���2  error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
		}
		// ���ˡ��ж����ⲿ���ر���3
		String P365 = initParm.getValue("P365");
		if (P365 != null && !P365.equals("")) {
			if (!(this.checkICD10(sex, P365))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P365 error1  ���ˡ��ж����ⲿ���ر���3" + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P365 error2  ���ˡ��ж����ⲿ���ر���3" + this.error2Msg
							+ " >\r\n");
				}

			}
		}
		// ����������������У��
		// ������������1
		String P490 = initParm.getValue("P490");
		// if (P490 !=null && !P490.equals("")) {
		// if (!(this.checkOperatorCode(sex, P490))) {
		//
		// if (sex == 1) {
		// errorBuffer.append("<ERR:P490 error3 "+this.error3Msg+" >\r\n");
		// }
		// if (sex == 2) {
		// errorBuffer.append("<ERR:P490 error4 "+this.error4Msg+" >\r\n");
		// }
		//		
		// }
		// }
		// ������������2
		String P4911 = initParm.getValue("P4911");
		// if (P4911 !=null && !P4911.equals("")) {
		// if (!(this.checkOperatorCode(sex, P4911))) {
		//
		// if (sex == 1) {
		//				
		// errorBuffer.append("<ERR:P4911 error3 "+this.error3Msg+" >\r\n");
		// }
		// if (sex == 2) {
		//				
		// errorBuffer.append("<ERR:P4911 error4 "+this.error4Msg+" >\r\n");
		//				
		// }
		//		
		// }
		// }
		// ���Ĳ��������У��
		// 11.P7����
		Integer P7 = initParm.getInt("P7");// �õ�ҪУ�������
		String P6 = initParm.getValue("P6");// ��������
		long age = 0;
		long mod = 0;
		if (P6 != null && !P6.equals("")) {
			Timestamp dt1 = getFormateDate(initParm.getTimestamp("P22"),"yyyyMMdd");
			Timestamp dt2 = initParm.getTimestamp("P6");
			long i = (dt1.getTime() - dt2.getTime()) / (1000 * 60 * 60 * 24);
			age = i / 365;
			if (age < 1) {
				if (initParm.getValue("P66") == null
						|| initParm.getValue("P66").toString().equals("")) {
					errorBuffer.append("<ERR:P66 �����䲻��1����ģ����� error19 " + this.error19Msg
							+ " >\r\n");
				}
				age = i;
				mod = i;
				if (age <= 28) {
					if (initParm.getValue("P67") == null
							|| initParm.getValue("P67").toString().equals("")) {
						errorBuffer.append("<ERR:P67 ��������������(��) error18 "
								+ this.error18Msg + " >\r\n");
					}
					if (initParm.getValue("P681") == null
							|| initParm.getValue("P681").toString().equals("")) {
						errorBuffer.append("<ERR:��������������1 P681 error17 "
								+ this.error17Msg + " >\r\n");
					}
				}
			}else{
				if (P7 > age + 1 || P7 < age - 1) {
					errorBuffer.append("<ERR:P7 ���� error6 " + this.error6Msg + " >\r\n");
				}
			}
		}
		// P28��P30��P321��P324��P3291
		// ����(��)����ϱ���(ICD-10)����Ժ��ϱ���(ICD-10)����Ҫ��ϱ���(ICD-10)��������ϱ���(ICD-10)����P10-P15��
		// ��Ժ����-�������ڡ�28�죬�����䲻��1����(��)������<=28��
		if (P28 != null && !P28.equals("")) {
			if (StringTool.compareTo(P28.substring(0, 3), "P10") > 0
					&& StringTool.compareTo("P15", P28.substring(0, 3)) > 0) {
				if (!(initParm.getValue("P66") != null
						&& initParm.getInt("P66") <= 28 && mod <= 28)) {
					errorBuffer.append("<ERR:P66 �����䲻��1����ģ�����  error10 " + this.error10Msg
							+ " >\r\n");
				}
			}
		}
		if (P30 != null && !P30.equals("")) {
			if (StringTool.compareTo(P30.substring(0, 3), "P10") > 0
					&& StringTool.compareTo("P15", P30.substring(0, 3)) > 0) {
				if (!(initParm.getValue("P66") != null
						&& initParm.getInt("P66") <= 28 && mod <= 28)) {
					errorBuffer.append("<ERR:P66 �����䲻��1����ģ����� error10 " + this.error10Msg
							+ " >\r\n");
				}
			}
		}
		if (P321 != null && !P321.equals("")) {
			if (StringTool.compareTo(P321.substring(0, 3), "P10") > 0
					&& StringTool.compareTo("P15", P321.substring(0, 3)) > 0) {
				if (!(initParm.getValue("P66") != null
						&& initParm.getInt("P66") <= 28 && mod <= 28)) {
					errorBuffer.append("<ERR:P66 �����䲻��1����ģ����� error10 " + this.error10Msg
							+ " >\r\n");
				}
			}
			if (P324 != null && !P324.equals(""))
				if (StringTool.compareTo(P324.substring(0, 3), "P10") > 0
						&& StringTool.compareTo("P15", P324.substring(0, 3)) > 0) {
					if (!(initParm.getValue("P66") != null
							&& initParm.getInt("P66") <= 28 && mod <= 28)) {
						errorBuffer.append("<ERR:P66 �����䲻��1����ģ����� error10 "
								+ this.error10Msg + " >\r\n");
					}
				}
		}
		if (P3291 != null && !P3291.equals("")) {
			if (StringTool.compareTo(P3291.substring(0, 3), "P10") > 0
					&& StringTool.compareTo("P15", P3291.substring(0, 3)) > 0) {
				if (!(initParm.getValue("P66") != null
						&& initParm.getInt("P66") <= 28 && mod <= 28)) {
					errorBuffer.append("<ERR:P66 �����䲻��1����ģ����� error10 " + this.error10Msg
							+ " >\r\n");
				}
			}
		}
		if (P324 != null && !P324.equals("")) {
			if (StringTool.compareTo(P324.substring(0, 3), "Z37") == 0) {
				if (initParm.getValue("P681") == null
						|| initParm.getValue("P681").toString().equals("")) {
					errorBuffer.append("<ERR:P681  ����Ҫ��ϻ���������ϱ���ΪZ37ʱ���� >\r\n");
				}
			}
		}
		// ���岽����Ժ����Ӧ�ô��ڵ����ϴγ�Ժ���� �ϴγ�Ժ����
		// String MR_NO = initParm.getValue("MR_NO").toString();
		// String sql = "SELECT DS_DATE FROM ADM_INP WHERE MR_NO = '" + MR_NO
		// + "' ORDER BY DS_DATE DESC";
		// TParm DS_DateParm = new TParm(TJDODBTool.getInstance().select(sql));
		// int count = DS_DateParm.getCount();
		// String DS_Date = "";
		// if(count >= 2){
		// DS_Date =(String) DS_DateParm.getData("DS_DATE", 0);// �ϴγ�Ժ����
		// }

		// if(DS_Date != null && !DS_Date.equals("") && P22 != null &&
		// !P22.endsWith("")){
		// if (!(this.dateCompare(DS_Date, P22))) {
		// errorBuffer.append("<ERR:P22 error7 "+this.error7Msg+" >\r\n");
		// }
		// }
		// ����������Ժ����<=��Ժ���� ��Ժ����<=��Ժ��ȷ������<=��Ժ���� ��Ժ����<=����������<=��Ժ���� error8
		String P491 = initParm.getValue("P491");// ������������
		// ��Ժ����<=��Ժ����
		if (P22 != null && !P22.equals("") && P25 != null && !P25.equals("")) {
			if (!(this.dateCompare(P22, P25))) {
				errorBuffer.append("<ERR:P22 ��Ժ����, P25 ��Ժ���� error8 " + this.error8Msg
						+ " >\r\n");
			}
		}
		// ��Ժ����<=��Ժ��ȷ������<=��Ժ����
		if (P22 != null && !P22.equals("") && P31 != null && !P31.equals("")) {
			if (!(this.dateCompare(P22, P31) && this.dateCompare(P31, P25))) {
				errorBuffer.append("<ERR:P22��Ժ���� , P31 ��Ժ��ȷ������ error8 " + this.error8Msg
						+ " >\r\n");
			}
		}
		// ��Ժ����<=������������1<=��Ժ����
		if (P22 != null && !P22.equals("") && P491 != null && !P491.equals("")) {
			if (!(this.dateCompare(P22, P491) && this.dateCompare(P491, P25))) {
				errorBuffer.append("<ERR:P22��Ժ����, P491������������1 error8 " + this.error8Msg
						+ " >\r\n");
			}
			// ��������Ϊ��
			if (P490.equals("") && P490 == null) {
				errorBuffer.append("<ERR:�Զ������  P490��������Ϊ��" + " >\r\n");
			}
			String P492 = initParm.getValue("P492");
			// ��������Ϊ��
			if (P492.equals("") && P492 == null) {
				errorBuffer.append("<ERR:�Զ������ P492��������Ϊ��  " + " >\r\n");
			}
		}
		// ��Ժ����<=������������2<=��Ժ����
		String P4912 = initParm.getValue("P4912");
		if (P22 != null && !P22.equals("") && P4912 != null
				&& !P4912.equals("")) {
			if (!(this.dateCompare(P22, P4912) && this.dateCompare(P4912, P25))) {
				errorBuffer.append("<ERR:P22��Ժ����, P4912������������2 error8 " + this.error8Msg
						+ " >\r\n");
			}
			// ��������Ϊ��
			if (P4911.equals("") && P4911 == null) {
				errorBuffer.append("<ERR:�Զ������  P4911��������Ϊ��" + " >\r\n");
			}
			String P4913 = initParm.getValue("P4913");
			// ��������Ϊ��
			if (P4913.equals("") && P4913 == null) {
				errorBuffer.append("<ERR:�Զ������ P4913��������Ϊ��  " + " >\r\n");
			}
		}
		// ��Ժ����<=������������3<=��Ժ����
		String P4923 = initParm.getValue("P4923");
		if (P22 != null && !P22.equals("") && P4923 != null
				&& !P4923.equals("")) {
			if (!(this.dateCompare(P22, P4923) && this.dateCompare(P4923, P25))) {
				errorBuffer.append("<ERR:P22��Ժ����, P4923������������3 error8 " + this.error8Msg
						+ " >\r\n");
			}
			String P4922 = initParm.getValue("P4922");
			// ��������Ϊ��
			if (P4922.equals("") && P4922 == null) {
				errorBuffer.append("<ERR:�Զ������  P4922��������Ϊ��" + " >\r\n");
			}
			String P4924 = initParm.getValue("P4924");
			// ��������Ϊ��
			if (P4924.equals("") && P4924 == null) {
				errorBuffer.append("<ERR:�Զ������ P4924��������Ϊ��  " + " >\r\n");
			}
		}

		// ��Ժ����<=������������4<=��Ժ����
		String P4534 = initParm.getValue("P4534");
		if (P22 != null && !P22.equals("") && P4534 != null
				&& !P4534.equals("")) {
			if (!(this.dateCompare(P22, P4534) && this.dateCompare(P4534, P25))) {
				errorBuffer.append("<ERR:P22��Ժ����, P4534������������4 error8 " + this.error8Msg
						+ " >\r\n");
			}
			String P4533 = initParm.getValue("P4533");
			// ��������Ϊ��
			if (P4533.equals("") && P4533 == null) {
				errorBuffer.append("<ERR:�Զ������  P4533��������Ϊ��" + " >\r\n");
			}
			String P4535 = initParm.getValue("P4535");
			// ��������Ϊ��
			if (P4535.equals("") && P4535 == null) {
				errorBuffer.append("<ERR:�Զ������ P4535��������Ϊ��  " + " >\r\n");
			}
		}
		// ��Ժ����<=������������5<=��Ժ����
		String P4545 = initParm.getValue("P4545");
		if (P22 != null && !P22.equals("") && P4545 != null
				&& !P4545.equals("")) {
			if (!(this.dateCompare(P22, P4545) && this.dateCompare(P4545, P25))) {
				errorBuffer.append("<ERR:P22��Ժ����, P4545������������5 error8 " + this.error8Msg
						+ " >\r\n");
			}
			String P4544 = initParm.getValue("P4544");
			// ��������Ϊ��
			if (P4544.equals("") && P4544 == null) {
				errorBuffer.append("<ERR:�Զ������  P4544��������Ϊ��" + " >\r\n");
			}
			String P4546 = initParm.getValue("P4546");
			// ��������Ϊ��
			if (P4546.equals("") && P4546 == null) {
				errorBuffer.append("<ERR:�Զ������ P4546��������Ϊ��  " + " >\r\n");
			}
		}
		// ��Ժ����<=������������6<=��Ժ����
		String P45003 = initParm.getValue("P45003");
		if (P22 != null && !P22.equals("") && P45003 != null
				&& !P45003.equals("")) {
			if (!(this.dateCompare(P22, P45003) && this
					.dateCompare(P45003, P25))) {
				errorBuffer.append("<ERR:P22��Ժ����, P45003������������6 error8 " + this.error8Msg
						+ " >\r\n");
			}
			String P45002 = initParm.getValue("P45002");
			// ��������Ϊ��
			if (P45002.equals("") && P45002 == null) {
				errorBuffer.append("<ERR:�Զ������  P45002��������Ϊ��" + " >\r\n");
			}
			String P45004 = initParm.getValue("P45004");
			// ��������Ϊ��
			if (P45004.equals("") && P45004 == null) {
				errorBuffer.append("<ERR:�Զ������ P45004��������Ϊ��  " + " >\r\n");
			}
		}
		// ��Ժ����<=������������7<=��Ժ����
		String p45015 = initParm.getValue("P45015");
		if (P22 != null && !P22.equals("") && p45015 != null
				&& !p45015.equals("")) {
			if (!(this.dateCompare(P22, p45015) && this
					.dateCompare(p45015, P25))) {
				errorBuffer.append("<ERR:P22��Ժ����, P45015������������7 error8 " + this.error8Msg
						+ " >\r\n");
			}
			String P45014 = initParm.getValue("P45014");
			// ��������Ϊ��
			if (P45014.equals("") && P45014 == null) {
				errorBuffer.append("<ERR:�Զ������  P45014��������Ϊ��" + " >\r\n");
			}
			String P45016 = initParm.getValue("P45016");
			// ��������Ϊ��
			if (P45016.equals("") && P45016 == null) {
				errorBuffer.append("<ERR:�Զ������ P45016��������Ϊ��  " + " >\r\n");
			}
		}
		// ��Ժ����<=������������8<=��Ժ����
		String p45027 = initParm.getValue("P45027");
		if (P22 != null && !P22.equals("") && p45027 != null
				&& !p45027.equals("")) {
			if (!(this.dateCompare(P22, p45027) && this
					.dateCompare(p45027, P25))) {
				errorBuffer.append("<ERR:P22��Ժ����, p45027������������8  error8 " + this.error8Msg
						+ " >\r\n");
			}
			String P45026 = initParm.getValue("P45026");
			// ��������Ϊ��
			if (P45026.equals("") && P45026 == null) {
				errorBuffer.append("<ERR:�Զ������  P45026��������Ϊ��" + " >\r\n");
			}
			String P45028 = initParm.getValue("P45028");
			// ��������Ϊ��
			if (P45028.equals("") && P45028 == null) {
				errorBuffer.append("<ERR:�Զ������ P45028��������Ϊ��  " + " >\r\n");
			}
		}

		// ��Ժ����<=������������9<=��Ժ����
		String p45039 = initParm.getValue("P45039");
		if (P22 != null && !P22.equals("") && p45039 != null
				&& !p45039.equals("")) {
			if (!(this.dateCompare(P22, p45039) && this
					.dateCompare(p45039, P25))) {
				errorBuffer.append("<ERR:P22��Ժ����, P45039������������9 error8 " + this.error8Msg
						+ " >\r\n");
			}
			String P45038 = initParm.getValue("P45038");
			// ��������Ϊ��
			if (P45038.equals("") && P45038 == null) {
				errorBuffer.append("<ERR:�Զ������  P45038��������Ϊ��" + " >\r\n");
			}
			String P45040 = initParm.getValue("P45040");
			// ��������Ϊ��
			if (P45040.equals("") && P45040 == null) {
				errorBuffer.append("<ERR:�Զ������ P45040��������Ϊ��  " + " >\r\n");
			}
		}
		// ��Ժ����<=������������10<=��Ժ����
		String p45051 = initParm.getValue("P45051");
		if (P22 != null && !P22.equals("") && p45051 != null
				&& !p45051.equals("")) {
			if (!(this.dateCompare(P22, p45051) && this
					.dateCompare(p45051, P25))) {
				errorBuffer.append("<ERR:P22��Ժ����, p45051������������10 error8 " + this.error8Msg
						+ " >\r\n");
			}
			String P45050 = initParm.getValue("P45050");
			// ��������Ϊ��
			if (P45050.equals("") && P45050 == null) {
				errorBuffer.append("<ERR:�Զ������  P45050��������Ϊ��" + " >\r\n");
			}
			String P45052 = initParm.getValue("P45052");
			// ��������Ϊ��
			if (P45052.equals("") && P45052 == null) {
				errorBuffer.append("<ERR:�Զ������ P45040��������Ϊ��  " + " >\r\n");
			}
		}
		// ��Ժ����ʱ��<=�໤�ҽ�������ʱ��<�໤���˳�����ʱ��<=��Ժ����ʱ�� error 20
		String P6912 = initParm.getValue("P6912");// ��֢�໤��1����ʱ��
		String P6913 = initParm.getValue("P6913");// ��֢�໤��1�˳�ʱ��
		if (P22 != null && !P22.equals("") && P6912 != null
				&& !P6912.equals("") && P6913 != null && !P6913.equals("")
				&& P25 != null && !P25.equals("")) {
			if (!(this.dateCompare(P22, P6912)
					&& this.dateCompare(P6912, P6913) && this.dateCompare(
					P6913, P25))) {
				errorBuffer.append("<ERR:P22��Ժ����ʱ��, P6912�໤�ҽ�������ʱ��,P6913�໤���˳�����ʱ��, P25��Ժ����ʱ��  error20 "
						+ this.error20Msg + " >\r\n");
			}
		}

		String P6915 = initParm.getValue("P6915");// ��֢�໤��2����ʱ��
		String P6916 = initParm.getValue("P6916");// ��֢�໤��2�˳�ʱ��
		if (P22 != null && !P22.equals("") && P6915 != null
				&& !P6915.equals("") && P6916 != null && !P6916.equals("")
				&& P25 != null && !P25.equals("")) {
			if (!(this.dateCompare(P22, P6915)
					&& this.dateCompare(P6915, P6916) && this.dateCompare(
					P6916, P25))) {
				errorBuffer.append("<ERR:P22��Ժ����ʱ��, P6915�໤�ҽ�������ʱ��2,P6916�໤���˳�����ʱ��2, P25��Ժ����ʱ�� error20 "
						+ this.error20Msg + " >\r\n");
			}
		}

		String P6918 = initParm.getValue("P6918");// ��֢�໤��3����ʱ��
		String P6919 = initParm.getValue("P6919");// ��֢�໤��3�˳�ʱ��
		if (P22 != null && !P22.equals("") && P6918 != null
				&& !P6918.equals("") && P6919 != null && !P6919.equals("")
				&& P25 != null && !P25.equals("")) {
			if (!(this.dateCompare(P22, P6918)
					&& this.dateCompare(P6918, P6919) && this.dateCompare(
					P6919, P25))) {
				errorBuffer.append("<ERR:P22��Ժ����ʱ��, P6918�໤�ҽ�������ʱ��3,P6919�໤���˳�����ʱ��3, P25��Ժ����ʱ�� error20 "
						+ this.error20Msg + " >\r\n");
			}
		}

		String P6921 = initParm.getValue("P6921");// ��֢�໤��4����ʱ��
		String P6922 = initParm.getValue("P6922");// ��֢�໤��4�˳�ʱ��
		if (P22 != null && !P22.equals("") && P6921 != null
				&& !P6921.equals("") && P6922 != null && !P6922.equals("")
				&& P25 != null && !P25.equals("")) {
			if (!(this.dateCompare(P22, P6921)
					&& this.dateCompare(P6921, P6922) && this.dateCompare(
					P6922, P25))) {
				errorBuffer.append("<ERR:P22��Ժ����ʱ��, P6921�໤�ҽ�������ʱ��4,P6922�໤���˳�����ʱ��4, P25��Ժ����ʱ�� error20 "
						+ this.error20Msg + " >\r\n");
			}
		}

		String P6924 = initParm.getValue("P6924");// ��֢�໤��5����ʱ��
		String P6925 = initParm.getValue("P6925");// ��֢�໤��5�˳�ʱ��
		if (P22 != null && !P22.equals("") && P6924 != null
				&& !P6924.equals("") && P6925 != null && !P6925.equals("")
				&& P25 != null && !P6925.equals("")) {
			if (!(this.dateCompare(P22, P6924)
					&& this.dateCompare(P6924, P6925) && this.dateCompare(
					P6925, P25))) {
				errorBuffer.append("<ERR:P22��Ժ����ʱ��, P6924�໤�ҽ�������ʱ��5,P6925�໤���˳�����ʱ��5, P25��Ժ����ʱ�� error20 "
						+ this.error20Msg + " >\r\n");
			}
		}

		// ���߲����������>=0
		double sums = 0.00;
		Double P752 = initParm.getDouble("P752");// һ��ҽ�Ʒ����
		if (!(P752 == null || P752.equals(""))) {
			if (!(P752 >= 0)) {
				errorBuffer.append("<ERR:P752 һ��ҽ�Ʒ���� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P752;
		}

		Double P754 = initParm.getDouble("P754");// һ�����Ʋ�����
		if (!(P754 == null || P754.equals(""))) {
			if (!(P754 >= 0)) {
				errorBuffer.append("<ERR:P754 һ�����Ʋ�����error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P754;
		}
		Double P755 = initParm.getDouble("P755");// �����
		if (!(P755 == null || P755.equals(""))) {
			if (!(P755 >= 0)) {
				errorBuffer.append("<ERR:P755����� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P755;
		}
		Double P756 = initParm.getDouble("P756");// �ۺ�ҽ�Ʒ�������������
		if (!(P756 == null || P756.equals(""))) {
			if (!(P756 >= 0)) {
				errorBuffer.append("<ERR:P756  �ۺ�ҽ�Ʒ������������� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P756;
		}
		Double P757 = initParm.getDouble("P757");// ������Ϸ�
		if (!(P757 == null || P757.equals(""))) {
			if (!(P757 >= 0)) {
				errorBuffer.append("<ERR:P757  ������Ϸ� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P757;
		}
		Double P758 = initParm.getDouble("P758");// ʵ������Ϸ�
		if (!(P758 == null || P758.equals(""))) {
			if (!(P758 >= 0)) {
				errorBuffer.append("<ERR:P758  ʵ������Ϸ� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P758;
		}
		Double P759 = initParm.getDouble("P759");// Ӱ��ѧ��Ϸ�
		if (!(P759 == null || P759.equals(""))) {
			if (!(P759 >= 0)) {
				errorBuffer.append("<ERR:P759 Ӱ��ѧ��Ϸ� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P759;
		}

		Double P760 = initParm.getDouble("P760");// �ٴ������Ŀ��
		if (!(P760 == null || P760.equals(""))) {
			if (!(P760 >= 0)) {
				errorBuffer.append("<ERR:P760 �ٴ������Ŀ�� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P760;
		}
		Double P761 = initParm.getDouble("P761");// ������������Ŀ��
		if (!(P761 == null || P761.equals(""))) {
			if (!(P761 >= 0)) {
				errorBuffer.append("<ERR:P761 ������������Ŀ�� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P761;
		}
		Double P762 = initParm.getDouble("P762");// �ٴ��������Ʒ�
		if (!(P762 == null || P762.equals(""))) {
			if (!(P762 >= 0)) {
				errorBuffer.append("<ERR:P762 �ٴ��������Ʒ� error22 " + this.error22Msg
						+ " >\r\n");
			}
			// sums += Double.parseDouble(P762);
		}
		Double P763 = initParm.getDouble("P763");// �������Ʒ�
		if (!(P763 == null || P763.equals(""))) {
			if (!(P763 >= 0)) {
				errorBuffer.append("<ERR:P763 �������Ʒ� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P763;
		}
		Double P764 = initParm.getDouble("P764");// �����
		if (!(P764 == null || P764.equals(""))) {
			if (!(P764 >= 0)) {
				errorBuffer.append("<ERR:P764  ����� error22 " + this.error22Msg
						+ " >\r\n");
			}
			// sums += Double.parseDouble(P764);
		}
		Double P765 = initParm.getDouble("P765");// ������
		if (!(P765 == null || P765.equals(""))) {
			if (!(P765 >= 0)) {
				errorBuffer.append("<ERR:P765  ������ error22 " + this.error22Msg
						+ " >\r\n");
			}
			// sums += Double.parseDouble(P765);
		}

		Double P767 = initParm.getDouble("P767");// ������
		if (!(P767 == null || P767.equals(""))) {
			if (!(P767 >= 0)) {
				errorBuffer.append("<ERR:P767  ������ error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P767;
		}
		Double P768 = initParm.getDouble("P768");// ��ҽ���Ʒ�
		if (!(P768 == null || P768.equals(""))) {
			if (!(P768 >= 0)) {
				errorBuffer.append("<ERR:P768 ��ҽ���Ʒ� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P768;
		}
		Double P769 = initParm.getDouble("P769");// ��ҩ��
		if (!(P769 == null || P769.equals(""))) {
			if (!(P769 >= 0)) {
				errorBuffer.append("<ERR:P769  ��ҩ�� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P769;
		}
		Double P770 = initParm.getDouble("P770");// ����ҩ�����
		if (!(P770 == null || P770.equals(""))) {
			if (!(P770 >= 0)) {
				errorBuffer.append("<ERR:P770  ����ҩ����� error22 " + this.error22Msg
						+ " >\r\n");
			}
			// sums += Double.parseDouble(P770);
		}
		Double P771 = initParm.getDouble("P771");// �г�ҩ��
		if (!(P771 == null || P771.equals(""))) {
			if (!(P771 >= 0)) {
				errorBuffer.append("<ERR:P771  �г�ҩ�� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P771;
		}
		Double P772 = initParm.getDouble("P772");// �в�ҩ��
		if (!(P772 == null || P772.equals(""))) {
			if (!(P772 >= 0)) {
				errorBuffer.append("<ERR:P772  �в�ҩ�� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P772;
		}
		Double P773 = initParm.getDouble("P773");// Ѫ��
		if (!(P773 == null || P773.equals(""))) {
			if (!(P773 >= 0)) {
				errorBuffer.append("<ERR:P773  Ѫ��  error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P773;
		}
		Double P774 = initParm.getDouble("P774");// �׵�������Ʒ��
		if (!(P774 == null || P774.equals(""))) {
			if (!(P774 >= 0)) {
				errorBuffer.append("<ERR:P774 �׵�������Ʒ�� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P774;
		}
		Double P775 = initParm.getDouble("P775");// �򵰰�����Ʒ��
		if (!(P775 == null || P775.equals(""))) {
			if (!(P775 >= 0)) {
				errorBuffer.append("<ERR:P775  �򵰰�����Ʒ��  error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P775;
		}
		Double P776 = initParm.getDouble("P776");// ��Ѫ��������Ʒ��
		if (!(P776 == null || P776.equals(""))) {
			if (!(P776 >= 0)) {
				errorBuffer.append("<ERR:P776 ��Ѫ��������Ʒ�� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P776;
		}
		Double P777 = initParm.getDouble("P777");// ϸ����������Ʒ��
		if (!(P777 == null || P777.equals(""))) {
			if (!(P777 >= 0)) {
				errorBuffer.append("<ERR:P777  ϸ����������Ʒ�� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P777;
		}
		Double P778 = initParm.getDouble("P778");// ���һ����ҽ�ò��Ϸ�
		if (!(P778 == null || P778.equals(""))) {
			if (!(P778 >= 0)) {
				errorBuffer.append("<ERR:P778  ���һ����ҽ�ò��Ϸ� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P778;
		}
		Double P779 = initParm.getDouble("P779");// ����һ����ҽ�ò�����
		if (!(P779 == null || P779.equals(""))) {
			if (!(P779 >= 0)) {
				errorBuffer.append("<ERR:P779 ����һ����ҽ�ò����� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P779;
		}
		Double P780 = initParm.getDouble("P780");// ������һ����ҽ�ò��Ϸ�
		if (!(P780 == null || P780.equals(""))) {
			if (!(P780 >= 0)) {
				errorBuffer.append("<ERR:P780 ������һ����ҽ�ò��Ϸ� error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P780;
		}
		Double P781 = initParm.getDouble("P781");// ������
		if (!(P781 == null || P781.equals(""))) {
			if (!(P781 >= 0)) {
				errorBuffer.append("<ERR:P781 ������  error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P781;
		}
		// �ڰ˲����ܷ���=�������֮��
		// סԺ�ܷ��� P782
		double sum = P782;
		if (sum != StringTool.round(sums, 2)) {
			errorBuffer.append("<ERR:P782 סԺ�ܷ��� error21 " + this.error21Msg + " >\r\n");
		}
		// �ھŲ���ʵ��סԺ>=�ػ�+һ������+��������+��������
		Integer admDays = P27;
		Integer specialDays = initParm.getInt("P561");
		Integer firstDays = initParm.getInt("P562");
		Integer secondDays = initParm.getInt("P563");
		Integer thirdDays = initParm.getInt("P564");
		if (admDays != null && !admDays.equals("") && specialDays != null
				&& !specialDays.equals("") && firstDays != null
				&& !firstDays.equals("") && secondDays != null
				&& !secondDays.equals("") && thirdDays != null
				&& !thirdDays.equals("")) {
			if (!(admDays >= (firstDays + secondDays + thirdDays))) {
				errorBuffer.append("<ERR:P561,P562,P563,P564 error16 "
						+ this.error16Msg + " >\r\n");
			}
		}
		// ������ƽ������Ϊ��4��������Ժ��ʽΪ��5��
		String P323 = initParm.getValue("P323");// ��Ҫ��ϳ�Ժ���
		String P326 = initParm.getValue("P326");//�������1��Ժ���
		String P329 = initParm.getValue("P329");//2
		String P3293 = initParm.getValue("P3293");//3
		String P3296 = initParm.getValue("P3296");//4
		String P3299 = initParm.getValue("P3299");//5
		String P3283 = initParm.getValue("P3283");//6
		String P3286 = initParm.getValue("P3286");//7
		String P3289 = initParm.getValue("P3289");//8
		String P3273 = initParm.getValue("P3273");//9
		String P3276 = initParm.getValue("P3276");//10
		Integer P421 = initParm.getInt("P421");
		Integer P422 = initParm.getInt("P422");
		if (P323.equals("4")||P326.equals("4")||P329.equals("4")||P3293.equals("4")||P3296.equals("4")||P3299.equals("4")
				||P3283.equals("4")||P3286.equals("4")||P3289.equals("4")||P3273.equals("4")||P3276.equals("4")||P741.equals("5")) {
			// if (initParm.getValue("P741") == null
			// || !(initParm.getValue("P741").equals("5"))) {
			// errorBuffer.append("<ERR:P741 error5 "+this.error5Msg+" >\r\n");
			// }
			// ���ȴ���=���ȳɹ����������ȴ���=���ȳɹ�����+1
			if (P421 != null && !P421.equals("") && P422 != null
					&& !P422.equals("")) {
				if (!(P421 == P422 + 1)&&P421!= P422) {
					errorBuffer.append("<ERR:����ʱ�����ȴ��� = ���ȳɹ�����+1 P421, P422 error15 "
							+ this.error15Msg + " >\r\n");
				}
			}

		} else {
			// ���ȴ��� = ���ȳɹ�����
			if (P421 != null && !P421.equals("") && P422 != null
					&& !P422.equals("")) {
				if (P421!= P422) {
					errorBuffer.append("<ERR:P421���ȴ��� , P422���ȳɹ�����  error15 "
							+ this.error15Msg + " >\r\n");
				}
			}
		}
		// ���֤������֤
		String P13 = initParm.getValue("P13");
		boolean flg = true;
		if (P13 != null && !(P13.toString().equals(""))) {
			STAIdcardValidator idcard = new STAIdcardValidator();
			flg = idcard.isValidatedAllIdcard(P13.toString());
			if (!flg) {
				errorBuffer.append("<ERR:P13 ���֤���벻�Ϸ� >\r\n");
			}
		}
		// p782��p751
		Double P751 = initParm.getDouble("P751");
		if (P782 < P751) {
			errorBuffer.append("<ERR:error 22 סԺ�ܷ���/סԺ�ܷ��������Ը����     >\r\n");
		}

		// p761��p762
		if (P761 < P762) {
			errorBuffer.append("<ERR: error23  ������������Ŀ��/�ٴ��������Ʒ�     >\r\n");
		}

		// p763��p764+p765
		if (P763 < P764 + P765) {
			errorBuffer.append("<ERR: error24  �������Ʒ�/�����/������     >\r\n");
		}

		// p769��p770
		if (P769 < P770) {
			errorBuffer.append("<ERR: error25  ��ҩ�ࣺ��ҩ��/����ҩ�����     >\r\n");
		}

		// ���˺��ж��ⲿԭ�����(ICD-10)
		// ICD-10��ϱ��뷶Χ��V��W��X��Y��ͷ��
		P361 = initParm.getValue("P361");
		if (P361 != null && !P361.equals("")) {
			if (!(StringTool.compareTo(P361.substring(0, 1), "V") >= 0 && StringTool
					.compareTo(P361.substring(0, 1), "Y") <= 0)) {
				errorBuffer
						.append("<ERR: P361�������1 error 13  ���˺��ж��ⲿԭ�����(ICD-10) ��ϱ��뷶Χ��V��W��X��Y��ͷ��    >\r\n");
			}
		}
		P363 = initParm.getValue("P363");
		if (P363 != null && !P363.equals("")) {
			if (!(StringTool.compareTo(P363.substring(0, 1), "V") >= 0 && StringTool
					.compareTo(P363.substring(0, 1), "Y") <= 0)) {
				errorBuffer
						.append("<ERR:P363�������2 error 13  ���˺��ж��ⲿԭ�����(ICD-10) ��ϱ��뷶Χ��V��W��X��Y��ͷ��    >\r\n");
			}
		}
		P365 = initParm.getValue("P365");
		if (P365 != null && !P365.equals("")) {
			if (!(StringTool.compareTo(P365.substring(0, 1), "V") >= 0 && StringTool
					.compareTo(P365.substring(0, 1), "Y") <= 0)) {
				errorBuffer
						.append("<ERR:P365�������3 error 13  ���˺��ж��ⲿԭ�����(ICD-10) ��ϱ��뷶Χ��V��W��X��Y��ͷ��    >\r\n");
			}
		}
		// ��Ժ����ʱ����ʿ�����
		String P47 = initParm.getValue("P47");
		if (P22 != null && !P22.equals("") && P47 != null && !P47.equals("")) {
			if (!this.dateCompare(P22, P47)) {
				errorBuffer.append("<ERR:P47 �ʿ����� error27  ��Ժ����ʱ����ʿ����� >\r\n");
			}
			Timestamp Tp47=initParm.getTimestamp("P47");
			Timestamp now=SystemTool.getInstance().getDate();
			if(Tp47.getTime()>now.getTime()){
				errorBuffer.append("<ERR:P47 �ʿ�����      �ʿ����� >��ǰ����\r\n");
			}
		}
		// ����Ҫ��ϻ���������ϱ�����ַ��䷽ʽ����O80-O84����������ֵı���O00-O08������䷽ʽ���벢��ʱ��������з����ֵı���Z37
		// P324��P3291
		if (P324 != null && !P324.equals("")) {
			if (StringTool.compareTo(P324.substring(0, 3), "O80") > 0
					&& StringTool.compareTo("O84", P324.substring(0, 3)) > 0) {

			}
		}
		if (errorBuffer.toString().length() > 0) {
			result.setErrCode(-100);
			result.setErrText(errorBuffer.toString());
		}
		return result;
	}
	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private Timestamp getFormateDate(Timestamp a, String b) {
		if (a == null) {
			return a;
		}
		Df = new SimpleDateFormat(b);
		String dfStr = Df.format(a);
		Timestamp redate = StringTool.getTimestamp(dfStr, b);
		return redate;
	}
	/**
	 * �������ڵıȽϷ��� date1<=date2 ���� true
	 * */
	public boolean dateCompare(String date1, String date2) {
		boolean flg = false;
		if (date1.length() > 19) {
			date1 = date1.substring(0, 18);
		}
		if (date2.length() > 19) {
			date2 = date2.substring(0, 18);
		}
		date1 = date1.replace("-", "/");
		date2 = date2.replace("-", "/");
		int count = StringTool.compareTo(date1, date2);
		if (count <= 0) {
			flg = true;
			return flg;
		}
		return flg;
	}

	/**
	 * ��֤�Ƿ�������
	 * */
	public boolean isNumber(String str) {
		boolean flg = true;
		str = str.trim();
		if (str == null || str.trim() == "") {
			flg = false;
			return flg;
		}

		flg = str.matches(("^[0-9_]+$"));
		return flg;
	}

	/**
	 * У���Ƿ��ǲ�����nλ������
	 * */
	public boolean isNumberN(String str, int n) {
		boolean flg = true;
		str = str.trim();
		if (str == null || str.trim() == "") {
			flg = false;
			return flg;
		}

		flg = str.matches(("^[0-9_]+$"));
		if (flg) {
			if (str.length() > n) {
				flg = false;
			}
		}
		return flg;
	}

	/**
	 * �����Ա���֤�������ICD10���� ���ԣ�1 Ů�ԣ�2
	 * */
	public boolean checkICD10(int i, String str) {

		boolean flg = true;
		// ����
		if (i == 1) {
			for (String icd10 : ICD10ForFemail) {
				int j = icd10.length();
				if (str.length() >= j) {
					String str1 = str.substring(0, j);
					if (str1.equals(icd10)) {
						flg = false;
					}
				}
			}
			if (StringTool.compareTo(str, "O00") >=0
					&& StringTool.compareTo("O99", str) >=0) {
				flg = false;
			}
		}
		// Ů��
		if (i == 2) {
			for (String icd10 : ICD10ForMail) {
				int j = icd10.length();
				if (str.length() >= j) {
					String str1 = str.substring(0, j);
					if (str1.equals(icd10)) {
						flg = false;
					}
				}
			}
		}
		return flg;
	}

	// /**
	// * �����Ա���֤�������� ���ԣ�1 Ů�ԣ�2
	// * */
	// public boolean checkOperatorCode(int i, String str) {
	// boolean flg = true;
	// if (i == 1) {
	// for (String operator : this.operatorCodeForFemail) {
	// int length = operator.length();
	// String str1 = str.substring(0,length);
	// if (str1.equals(operator)) {
	// flg = false;
	// }
	// }
	// }
	// if (i == 2) {
	// for (String operator : this.operatorCodeForMail) {
	// int length = operator.length();
	// String str1 = str.substring(0,length);
	// if (str1.equals(operator)) {
	// flg = false;
	// }
	// }
	// }
	// return flg;
	// }

	/***
	 * 
	 * ��(��)����ϱ���(ICD-10)����Ժ��ϱ���(ICD-10)����Ժʱ��Ҫ��ϱ���(ICD-10)��
	 * ��Ժʱ������ϱ���(ICD-10)��ҽԺ��Ⱦ���Ʊ���(ICD-10)������뷶ΧӦΪ��
	 * A00-U99��Z00-Z99����������ĸV��W��X��Y��ͷ�ı���  M80000/0-M99999/6
	 * */
	public boolean checkICD10Other(int i, String str) {

		boolean flg = true;
		String PDStart="M80000/0";
		String PDEnd="M99999/6";
		if(str.length()==PDStart.length()&&str.contains("/")){
			if(StringTool.compareTo(str, PDStart) > 0&&StringTool.compareTo(PDEnd, str)>0){
				flg = false;
			}
		}
		str = str.substring(0, 1);
		if (!((StringTool.compareTo(str, "A")>=0 && StringTool.compareTo(str,
				"U")<=0) || str.equals("Z"))) {
			flg = false;
		}
		return flg;

	}

}