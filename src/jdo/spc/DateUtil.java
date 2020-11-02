package jdo.spc;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	/** */
	/**
	 * Ĭ�����ڸ�ʽ
	 */
	private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/** */
	/**
	 * <Ĭ�Ϲ��캯��>
	 */
	private DateUtil() {
	}

	/** */
	/**
	 * <�ַ���ת��������> <���ת����ʽΪ�գ�������Ĭ�ϸ�ʽ����ת������>
	 * 
	 * @param str
	 *            �ַ���
	 * @param format
	 *            ���ڸ�ʽ
	 * @return ����
	 * @see [�ࡢ��#��������#��Ա]
	 */
	public static Date str2Date(String str, String format) {
		if (null == str || "".equals(str)) {
			return null;
		}
		// ���û��ָ���ַ���ת���ĸ�ʽ������Ĭ�ϸ�ʽ����ת��
		if (null == format || "".equals(format)) {
			format = DEFAULT_FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(str);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	/** */
	/**
	 * <һ�仰���ܼ���> <������ϸ����>
	 * 
	 * @param date
	 *            ����
	 * @param format
	 *            ���ڸ�ʽ
	 * @return �ַ���
	 * @see [�ࡢ��#��������#��Ա]
	 */
	public static String date2Str(Date date, String format) {
		if (null == date) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/** */
	/**
	 * <ʱ���ת��Ϊ�ַ���> <������ϸ����>
	 * 
	 * @param time
	 * @return
	 * @see [�ࡢ��#��������#��Ա]
	 */
	public static String timestamp2Str(Timestamp time) {
		Date date = new Date(time.getTime());
		return date2Str(date, DEFAULT_FORMAT);
	}

	/** */
	/**
	 * <һ�仰���ܼ���> <������ϸ����>
	 * 
	 * @param str
	 * @return
	 * @see [�ࡢ��#��������#��Ա]
	 */
	public static Timestamp str2Timestamp(String str) {
		Date date = str2Date(str, DEFAULT_FORMAT);
		return new Timestamp(date.getTime());
	}
}
