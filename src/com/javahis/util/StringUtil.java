package com.javahis.util;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import jdo.opd.OPDSysParmTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;

/**
 * 
 * <p>
 * Title:���ֹ��÷���
 * </p>
 * 
 * <p>
 * Description:���ֹ��÷���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author ehui 20080917
 * @version 1.0
 */
public class StringUtil {
	private static StringUtil instanceObject;
	/**
	 * @alias ���ִ�д  �f
	 */
	private static final String[] NUMBERS = { "��", "Ҽ", "��", "��", "��", "��",
			"½", "��", "��", "��" };
	/**
	 * @alias ���λ������   �f
	 */
	private static final String[] IUNIT = { "Ԫ", "ʰ", "��", "Ǫ", "��", "ʰ", "��",
			"Ǫ", "��", "ʰ", "��", "Ǫ", "��", "ʰ", "��", "Ǫ" };
	
	//
	private static final String[] DUNIT = { "��", "��", "��" };

	public StringUtil() {
	}

	public static synchronized StringUtil getInstance() {
		if (instanceObject == null) {
			instanceObject = new StringUtil();
		}
		return instanceObject;
	}

	/**
	 * ���ݺ��ַ���ƴ������ĸ
	 * 
	 * @param chinese
	 * @return ƴ������ĸ
	 */
	public static String onCode(String chinese) {
		SystemTool st = new SystemTool();
		String value = st.charToCode(chinese);
		if (null == value || value.length() < 1) {
			return null;
		}
		return value;

	}

	/**
	 * @alias ��������
	 * @param String
	 *            birthday ��������(20050810)
	 * @return String ����(1��2����3��) ����˵����<br>
	 *         1��ȡAPʱ����<br>
	 */
	public String countAge(String birthday, Timestamp admDate, String type) {
		String rString = ""; // ����
		String ayear = ""; // ��
		String amonth = ""; // ��
		String aday = ""; // ��
		if (birthday.length() == 0) {
			return rString;
		} else {
			// 1. ȡ��ϵͳ����
			String sysDate = "";
			if (type.equals("Y")) {
				sysDate = this.getDate(admDate);
			} else {
				sysDate = this.getDate();
			}
			char ch2[] = new char[sysDate.length()];
			sysDate.getChars(0, sysDate.length(), ch2, 0);
			String syear2 = new String(ch2, 0, ch2.length - 4);
			String smonth2 = new String(ch2, ch2.length - 4, 2);
			String sday2 = new String(ch2, ch2.length - 2, 2);
			// ��������ת��������
			int iyear2 = Integer.parseInt(syear2);
			if (syear2.length() != 4) {
				iyear2 = (iyear2 + 1911);
			}
			int imonth2 = Integer.parseInt(smonth2);
			int iday2 = Integer.parseInt(sday2);
			// ȡ��������������ִ�
			char ch[] = new char[birthday.length()];
			birthday.getChars(0, birthday.length(), ch, 0);
			String syear1 = new String(ch, 0, ch.length - 4);
			String smonth1 = new String(ch, ch.length - 4, 2);
			String sday1 = new String(ch, ch.length - 2, 2);
			// ��������ת��������
			int iyear1 = Integer.parseInt(syear1);
			if (syear1.length() != 4) {
				iyear1 = (iyear1 + 1911);
			}
			int imonth1 = Integer.parseInt(smonth1);
			int iday1 = Integer.parseInt(sday1);
			// �趨������ʼֵ
			int year = 0;
			int month = 0;
			int day = 0;
			// ȡ����������������
			if (iday2 < iday1) {
				if ((imonth2 == 2) || (imonth2 == 4) || (imonth2 == 6)
						|| (imonth2 == 9) || (imonth2 == 11)) {
					day = ((iday2 + 31) - iday1) + 1;
					imonth2 = (imonth2 - 1);
				} else if (imonth2 == 3) {
					String g = "" + iyear2;
					String a = this.IsLeapYear(g);
					if (a == "True") {
						day = (iday2 + 29) - iday1 + 1;
					} else if (a == "false") {
						day = (iday2 + 28) - iday1 + 1;
					} else {
						day = (iday2 + 28) - iday1 + 1;
					}
					imonth2 = (imonth2 - 1);
				} else if (imonth2 == 1) {
					day = (iday2 - iday1) + 31;
					imonth2 = 12;
					iyear2--;
				} else {
					day = ((iday2 + 30) - iday1) + 1;
					imonth2 = (imonth2 - 1);
				}
			} else {
				day = (iday2 - iday1) + 1;
			}
			// ȡ�������ڵ��·ݲ�
			if (imonth2 < imonth1) {
				month = (imonth2 + 12) - imonth1;
				iyear2 = iyear2 - 1;
			} else {
				month = imonth2 - imonth1;
			}

			// ȡ����������ݲ�
			year = iyear2 - iyear1;
			// �����ת���ִ�
			ayear = Integer.toString(year);
			amonth = Integer.toString(month);
			aday = Integer.toString(day);

			rString = ayear + "��" + amonth + "����" + aday + "��";
		}
		return rString;
	}

	/**
	 * ���ݲ������պʹ���Ľ������ڣ����㲡�����䲢�����Ƿ�Ϊ��ͯ�Բ�ͬ����ʽ��ʾ���䣬���Ƕ�ͯ����ʾX��X��X�գ����������ʾx��
	 * 
	 * @param odo
	 * @return String ������ʾ������
	 */
	public static String showAge(Timestamp birthday, Timestamp t) {
		String age = "";
		String[] res;
		res = StringTool.CountAgeByTimestamp(birthday, t);
		if (OPDSysParmTool.getInstance().isChild(birthday)) {
			age = res[0] + "��" + res[1] + "��" + res[2] + "��";
		} else {
			age = res[0] + "��";
		}
		return age;
	}

	/**
	 * ���ϵͳ��ǰʱ��
	 * 
	 * @return String
	 */
	public String getDate() {
		String result = "";
		// ��õ�ǰʱ��
		SimpleDateFormat time = new SimpleDateFormat("yyyyMMdd");
		result = time.format(new Date()).toString();
		return result;
	}

	/**
	 * ���ϵͳ��ǰʱ��
	 * 
	 * @return String
	 */
	public String getDate(Timestamp admDate) {
		String result = "";
		// ��õ�ǰʱ��
		SimpleDateFormat time = new SimpleDateFormat("yyyyMMdd");
		result = time.format(admDate).toString();
		return result;
	}

	/**
	 * ȷ��ĳ�����������Ƿ������������
	 * 
	 * @param a
	 *            String
	 * @return String
	 */
	public String IsLeapYear(String a) {
		String temp = "False";
		int yearleap = 0;
		yearleap = Integer.parseInt(a);
		GregorianCalendar gcalendar = new GregorianCalendar();
		if (gcalendar.isLeapYear(yearleap))
			temp = "True";
		else
			temp = "False";
		return temp;
	}

	/**
	 * �õ�һ��ʱ���Ӻ��ǰ�Ƽ����ʱ��,nowdateΪʱ��,delayΪǰ�ƻ���ӵ�����
	 * 
	 * @param date
	 *            String
	 * @return String
	 */
	public String getNextDay(String nowdate, String delay) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String mdate = "";
			Date d = strToDate(nowdate);
			long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24
					* 60 * 60;
			d.setTime(myTime * 1000);
			mdate = format.format(d);
			return mdate;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * �õ�һ��ʱ���Ӻ��ǰ�Ƽ����ʱ��,nowdateΪʱ��,delayΪǰ�ƻ���ӵ�����
	 * 
	 * @param date
	 *            String
	 * @return long
	 */
	public long getNextDayMiss(String nowdate, String delay) {
		try {
			Date d = strToDate(nowdate);
			long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24
					* 60 * 60;
			d.setTime(myTime * 1000);
			return d.getTime();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * ����ʱ���ʽ�ַ���ת��Ϊʱ�� yyyy-MM-dd
	 * 
	 * @param strDate
	 *            String
	 * @return Date
	 */
	public Date strToDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * ȡ����ת��д
	 * 
	 * @param num
	 *            double
	 * @return String
	 */
	public String numberToWord(double num) {
		
		DecimalFormat df = new DecimalFormat("0.00");
		boolean flg;
		if (num == 0) {
			return "��Ԫ��";
		}
		if (num < 0) {
			num = 0 - num;
			flg = true;
		} else
			flg = false;
		String str=df.format(num);
		str = str.replaceAll(",", "");// ȥ��","
		String integerStr;// ������������
		String decimalStr;// С����������

		// ��ʼ���������������ֺ�С������
		if (str.indexOf(".") > 0) {
			integerStr = str.substring(0, str.indexOf("."));
			decimalStr = str.substring(str.indexOf(".") + 1);
		} else if (str.indexOf(".") == 0) {
			integerStr = "";
			decimalStr = str.substring(1);
		} else {
			integerStr = str;
			decimalStr = "";
		}
		// integerStrȥ����0������ȥ��decimalStr��β0(����������ȥ)
		if (!integerStr.equals("")) {
			integerStr = Long.toString(Long.parseLong(integerStr));
			if (integerStr.equals("0")) {
				integerStr = "";
			}
		}
		// overflow��������������ֱ�ӷ���
		if (integerStr.length() > IUNIT.length) {
			System.out.println(str + ":������������");
			return str;
		}

		int[] integers = toArray(integerStr);// ������������
		boolean isMust5 = isMust5(integerStr);// ������λ
		int[] decimals = toArray(decimalStr);// С����������
		String result=getChineseInteger(integers, isMust5)
		+ getChineseDecimal(decimals);
		if (flg)
			return "��" + result.toString() + "��";
		else
			if(result.toString().indexOf("��")!=-1){
				return result.toString();
			}else{
				return result.toString() + "��";
			}
	}

	private static int[] toArray(String number) {
		int[] array = new int[number.length()];
		for (int i = 0; i < number.length(); i++) {
			array[i] = Integer.parseInt(number.substring(i, i + 1));
		}
		return array;
	}

	private static String getChineseInteger(int[] integers, boolean isMust5) {
		StringBuffer chineseInteger = new StringBuffer("");
		int length = integers.length;
		for (int i = 0; i < length; i++) {
			// 0�����ڹؼ�λ�ã�1234(��)5678(��)9012(��)3456(Ԫ)
			// ���������10(ʰԪ��ҼʰԪ��Ҽʰ��Ԫ��ʰ��Ԫ)
			String key = "";
			if (integers[i] == 0) {
				if ((length - i) == 13)// ��(��)(����)
					key = IUNIT[4];
				else if ((length - i) == 9)// ��(����)
					key = IUNIT[8];
				else if ((length - i) == 5 && isMust5)// ��(������)
					key = IUNIT[4];
				else if ((length - i) == 1)// Ԫ(����)
					key = IUNIT[0];
				// 0����0ʱ���㣬���������һλ
				if ((length - i) > 1 && integers[i + 1] != 0)
					key += NUMBERS[0];
			}
			chineseInteger.append(integers[i] == 0 ? key
					: (NUMBERS[integers[i]] + IUNIT[length - i - 1]));
		}
		return chineseInteger.toString();
	}

	private static String getChineseDecimal(int[] decimals) {
		StringBuffer chineseDecimal = new StringBuffer("");
		for (int i = 0; i < decimals.length; i++) {
			// ��ȥ3λС��֮���
			if (i == 3)
				break;
			chineseDecimal.append(decimals[i] == 0 ? ""
					: (NUMBERS[decimals[i]] + DUNIT[i]));
		}
		return chineseDecimal.toString();
	}

	private static boolean isMust5(String integerStr) {
		int length = integerStr.length();
		if (length > 4) {
			String subInteger = "";
			if (length > 8) {
				// ȡ�ôӵ�λ������5����8λ���ִ�
				subInteger = integerStr.substring(length - 8, length - 4);
			} else {
				subInteger = integerStr.substring(0, length - 4);
			}
			return Integer.parseInt(subInteger) > 0;
		} else {
			return false;
		}
	}
	/**
	 * �ж��ǲ��ǿ��ַ���
	 * 
	 * @param s
	 * @return boolean Y:�գ�N:����
	 */
	public static boolean isNullString(String s) {
		return (s == null || s.length() < 1);
	}

	/**
	 * ���鿽������
	 * 
	 * @param sql1
	 *            String[]
	 * @param sql2
	 *            String[]
	 * @return String[]
	 */
	public static String[] copyArray(String sql1[], String sql2[]) {
		if (sql1.length == 0)
			return sql2;
		if (sql2.length == 0)
			return sql1;
		String data[] = new String[sql1.length + sql2.length];
		System.arraycopy(sql1, 0, data, 0, sql1.length);
		System.arraycopy(sql2, 0, data, sql1.length, sql2.length);
		return data;
	}

	/**
	 * �жϸ����List�Ƿ�Ϊ��
	 * 
	 * @param list
	 * @return boolean �����򷵻��棬�粻���򷵻ؼ�
	 */
	public static boolean isNullList(List list) {
		return (list == null || list.size() < 1);
	}

	/**
	 * �жϸ����List�Ƿ�Ϊ�գ��Լ�SIZE�Ƿ���ڵ��ڸ����SIZE
	 * 
	 * @param list
	 *            List
	 * @param size
	 *            int
	 * @return boolean ��Ϊ�ջ�LIST��SIZE���������ֵ���򷵻��棬���򷵻ؼ�
	 */
	public static boolean isSatisefyListSize(List list, int size) {
		return (list == null || list.size() < size);
	}

	/**
	 * �жϸ���������Ƿ�Ϊ��
	 * 
	 * @param list
	 * @return boolean �����򷵻��棬�粻���򷵻ؼ�
	 */
	public static boolean isNullArray(Object[] obj) {
		return (obj == null || obj.length < 1);
	}

	/**
	 * �жϸ���������Ƿ�Ϊ�գ��Լ�SIZE�Ƿ���ڵ��ڸ����SIZE
	 * 
	 * @param list
	 *            List
	 * @param size
	 *            int
	 * @return boolean ��Ϊ�ջ�LIST��SIZE���������ֵ���򷵻��棬���򷵻ؼ�
	 */
	public static boolean isSatisefyListSize(Object[] obj, int size) {
		return (obj == null || obj.length < size);
	}

	/**
	 * ͨ�����֤�õ�������Code
	 * 
	 * @param idNo
	 *            String
	 * @return String
	 */
	public static String getIdNoToHomeCode(String idNo) {
		if (idNo.trim().length() < 15) {
			return idNo;
		}
		return idNo.substring(0, 6);

	}

	/**
	 * ���ݸ���TABLE��������,WHERE�����õ���ѯ�е�ֵ
	 * 
	 * @param tableName
	 * @param columnName
	 * @param whereStr
	 * @return
	 */
	public static String getDesc(String tableName, String columnName,
			String whereStr) {
		if (StringUtil.isNullString(tableName)
				|| StringUtil.isNullString(columnName)
				|| StringUtil.isNullString(whereStr)) {
			// System.out.println("StringUtil.getDesc-> inParameter is null");
			return "";
		}
		String sql = "SELECT # FROM # WHERE #";
		sql = sql.replaceFirst("#", columnName).replaceFirst("#", tableName)
				.replaceFirst("#", whereStr);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() != 0) {
			return "";
		}
		return parm.getValue(columnName, 0);
	}
}
