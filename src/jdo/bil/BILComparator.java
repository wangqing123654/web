package jdo.bil;

import java.sql.Timestamp;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

/**
 * <p>
 * Title: ����סԺ���񱨱�����ıȽ���
 * </p>
 * 
 * <p>
 * Description: ����סԺ���񱨱�����ıȽ���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2012
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wanglong 2012.07.10
 * @version 1.0
 */
public class BILComparator implements Comparator {
	private boolean des;// ����ķ��򣺵�����ݼ�
	private int col;// ����������к�

	public BILComparator() {
		this(true, 0);
	}

	public BILComparator(boolean des, int col) {
		this.des = des;
		this.col = col;
	}

	public boolean isDes() {
		return this.des;
	}

	public void setDes(boolean des) {
		this.des = des;
	}

	public int getCol() {
		return this.col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	/**
	 * �Ƚ����ı�׼����
	 * 
	 * @param o1
	 *            Object, o2 Object
	 * @return int
	 */
	public int compare(Object o1, Object o2) {
		Vector v1 = (Vector) o1;
		Vector v2 = (Vector) o2;
		if ((!(o1 instanceof Vector)) && (!(o2 instanceof Vector))) {// ������Vector����֮��Ƚϴ�С
			return -1;
		}
		String v1Str = "";
		String v2Str = "";
		int result = 0;// ���
		Object obj1 = v1.get(this.col);
		Object obj2 = v2.get(this.col);
//		System.out.println("obj1:"+obj1);
//		System.out.println("obj2:"+obj2);
//		System.out.println("obj1 Class:"+obj1.getClass());
//		System.out.println("obj2 Class:"+obj2.getClass());
		if (obj1 == null)
			obj1 = "";
		if (obj2 == null)
			obj2 = "";
		if ((obj1.getClass() == String.class)
				&& (obj2.getClass() == String.class)) {// �����Ϊ�ַ�����
			try {
				v1Str = obj1 == null ? "" : (String) obj1;
				v2Str = obj2 == null ? "" : (String) obj2;
			} catch (java.lang.Exception e) {
			}
			if (v1Str.matches("\\-?\\p{Digit}+(\\056?\\p{Digit}+)?")
					&& v2Str.matches("\\-?\\p{Digit}+(\\056?\\p{Digit}+)?")) {// �����Ϊ�����ַ���
				result = (Double.valueOf(v1Str)).compareTo(Double.valueOf(v2Str));
			} else {// ���Ϊ����ַ���
				if ((v1Str.indexOf("����") != -1) && (v2Str.indexOf("����") != -1)) {
					//===========��������������д������֣���������==================
					String v1StartStr = "";
					String v2StartStr = "";
					if (v1Str.matches("\\p{Digit}+����")) {
						// ���´������ơ�XX������������
						v1StartStr = v1Str.substring(0, v1Str.indexOf("����"));
						v2StartStr = v2Str.substring(0, v2Str.indexOf("����"));
						if (v1StartStr.matches("\\p{Digit}+")
								&& v2StartStr.matches("\\p{Digit}+")) {
							int v1Num = Integer.parseInt(v1StartStr);
							int v2Num = Integer.parseInt(v2StartStr);
							if (v1Num > v2Num) {
								result = 1;
								if (!(this.des))
									result = -result;
								return result;
							} else if (v1Num < v2Num) {
								result = -1;
								if (!(this.des))
									result = -result;
								return result;
							}
						}
					}
					else if (v1Str.matches("CCU\\p{Digit}+����")
							&& v2Str.matches("CCU\\p{Digit}+����")) {
						// ���´������ơ�CCUX������������
						v1StartStr = v1Str.substring(v1Str.indexOf("CCU") + 3, v1Str.indexOf("����"));
						v2StartStr = v2Str.substring(v2Str.indexOf("CCU") + 3, v2Str.indexOf("����"));
						if (v1StartStr.matches("\\p{Digit}+")
								&& v2StartStr.matches("\\p{Digit}+")) {
							int v1Num = Integer.parseInt(v1StartStr);
							int v2Num = Integer.parseInt(v2StartStr);
							if (v1Num > v2Num) {
								result = 1;
								if (!(this.des))
									result = -result;
								return result;
							} else if (v1Num < v2Num) {
								result = -1;
								if (!(this.des))
									result = -result;
								return result;
							}
						}
					}
					else if (v1Str.matches("ICU\\p{Digit}+����")
							&& v2Str.matches("ICU\\p{Digit}+����")) {
						// ���´������ơ�ICUX������������
						v1StartStr = v1Str.substring(v1Str.indexOf("ICU") + 3, v1Str.indexOf("����"));
						v2StartStr = v2Str.substring(v2Str.indexOf("ICU") + 3, v2Str.indexOf("����"));
						if (v1StartStr.matches("\\p{Digit}+")
								&& v2StartStr.matches("\\p{Digit}+")) {
							int v1Num = Integer.parseInt(v1StartStr);
							int v2Num = Integer.parseInt(v2StartStr);
							if (v1Num > v2Num) {
								result = 1;
								if (!(this.des))
									result = -result;
								return result;
							} else if (v1Num < v2Num) {
								result = -1;
								if (!(this.des))
									result = -result;
								return result;
							}
						}
					}
					else if (v1Str.matches("����ҽ�Ʋ���\\p{Digit}+F")
							&& v2Str.matches("����ҽ�Ʋ���\\p{Digit}+F")) {
						// ���´������ơ�����ҽ�Ʋ���XXF��������
						v1StartStr = v1Str.substring(v1Str.indexOf("����") + 2, v1Str.length() - 1);
						v2StartStr = v2Str.substring(v2Str.indexOf("����") + 2, v2Str.length() - 1);
						if (v1StartStr.matches("\\p{Digit}+")
								&& v2StartStr.matches("\\p{Digit}+")) {
							int v1Num = Integer.parseInt(v1StartStr);
							int v2Num = Integer.parseInt(v2StartStr);
							if (v1Num > v2Num) {
								result = 1;
								if (!(this.des))
									result = -result;
								return result;
							} else if (v1Num < v2Num) {
								result = -1;
								if (!(this.des))
									result = -result;
								return result;
							}
						}
					}
				} else if (v1Str.matches("\\p{Digit}+��")
						&& v2Str.matches("\\p{Digit}+��")) {
					// ���´������ơ�XX�ꡱ������
					String v1StartStr = v1Str.replaceAll("[^0-9]", "");
					String v2StartStr = v2Str.replaceAll("[^0-9]", "");
					if (v1StartStr.matches("\\p{Digit}+")
							&& v2StartStr.matches("\\p{Digit}+")) {
						int v1Num = Integer.parseInt(v1StartStr);
						int v2Num = Integer.parseInt(v2StartStr);
						if (v1Num > v2Num) {
							result = 1;
							if (!(this.des))
								result = -result;
							return result;
						} else if (v1Num < v2Num) {
							result = -1;
							if (!(this.des))
								result = -result;
							return result;
						}
					}
				}
				char[] strArr1 = v1Str.toCharArray();
				char[] strArr2 = v2Str.toCharArray();
				int colCount = v1Str.length() < v2Str.length() ? v1Str.length() : v2Str.length();
				int i = 0;
				for (; i < colCount; i++) {
					if (charRank(strArr1[i]) > charRank(strArr2[i])) {
						result = 1;
						break;
					} else if (charRank(strArr1[i]) < charRank(strArr2[i])) {
						result = -1;
						break;
					} else {// �ȼ���ͬ
						if (strArr1[i] == strArr2[i]) {
							continue;
						}
						String str1 = "" + strArr1[i];
						String str2 = "" + strArr2[i];
						if (str1.matches("[\u4e00-\u9fa5]{1}")
								&& str1.matches("[һ�����������߰˾���]{1}")) {
							result = compareCNNum(strArr1[i], strArr2[i]);
							break;
						} else if (str1.matches("[\u4e00-\u9fa5]{1}")
								&& !str1.matches("[һ�����������߰˾���]{1}")) {
							result = compareCNLetter(strArr1[i], strArr2[i]);
							break;
						} else {
							if (strArr1[i] > strArr2[i]) {
								result = 1;
								break;
							} else if (strArr1[i] < strArr2[i]) {
								result = -1;
								break;
							}
						}
					}
				}
				if ((i == colCount) && (v1Str.length() < v2Str.length())) {// ��������ǰ����ͬ���Ȳ���ȫ��ͬʱ
					return 1;
				} else if ((i == colCount) && (v1Str.length() > v2Str.length())) {
					result = -1;
				}
			}
		} else if (((obj1.getClass() == Double.class) ||
				   (obj1.getClass() == Integer.class) || 
				   (obj1.getClass() == Long.class))
				&& ((obj2.getClass() == Double.class) || 
				   (obj2.getClass() == Integer.class) || 
				   (obj2.getClass() == Long.class))) {// �����Ϊ��������
			if (Double.parseDouble(obj1 + "") > Double.parseDouble(obj2 + ""))
				result = 1;
			else if (Double.parseDouble(obj1 + "") < Double.parseDouble(obj2 + ""))
				result = -1;
		} else if ((obj1.getClass() == Timestamp.class)
				&& (obj2.getClass() == Timestamp.class)) {// �����Ϊ��������
			if (((Timestamp) obj1).getTime() > ((Timestamp) obj2).getTime())
				result = 1;
			else if (((Timestamp) obj1).getTime() < ((Timestamp) obj2).getTime())
				result = -1;
		} else {//����������Ͳ�ͬ
			try {
				//v1Str = (String) obj1;
				//v2Str = (String) obj2;
				v1Str = obj1+"";
				v2Str = obj2+"";
			} catch (java.lang.Exception w) {
			}
			if(v1Str.matches("\\-?\\p{Digit}+(\\056?\\p{Digit}+)?")&&v1Str.matches("\\-?\\p{Digit}+(\\056?\\p{Digit}+)?")){
				if (Double.parseDouble(v1Str) > Double.parseDouble(v2Str))
					result = 1;
				else if (Double.parseDouble(v1Str) < Double.parseDouble(v2Str))
					result = -1;
			}else{
				result = v1Str.compareTo(v2Str);	
			}
		}
		if (!(this.des))
			result = -result;
		return result;
	}

	/**
	 * ���������űȽ�
	 * 
	 * @param ch1
	 *            char, ch2 char
	 * @return int
	 */
	public int compareCNNum(char ch1, char ch2) {
		int c1 = -1;
		int c2 = -1;
		switch (ch1) {
		case '��':
			c1 = 0;
			break;
		case 'һ':
			c1 = 1;
			break;
		case '��':
			c1 = 2;
			break;
		case '��':
			c1 = 3;
			break;
		case '��':
			c1 = 4;
			break;
		case '��':
			c1 = 5;
			break;
		case '��':
			c1 = 6;
			break;
		case '��':
			c1 = 7;
			break;
		case '��':
			c1 = 8;
			break;
		case '��':
			c1 = 9;
			break;
		}
		switch (ch2) {
		case '��':
			c2 = 0;
			break;
		case 'һ':
			c2 = 1;
			break;
		case '��':
			c2 = 2;
			break;
		case '��':
			c2 = 3;
			break;
		case '��':
			c2 = 4;
			break;
		case '��':
			c2 = 5;
			break;
		case '��':
			c2 = 6;
			break;
		case '��':
			c2 = 7;
			break;
		case '��':
			c2 = 8;
			break;
		case '��':
			c2 = 9;
			break;
		}
		return c1 > c2 ? 1 : -1;
	}

	/**
	 * ���ĺ��ֱȽ�
	 * 
	 * @param ch1
	 *            char, ch2 char
	 * @return int
	 */
	public int compareCNLetter(char ch1, char ch2) {
		String str1 = "" + ch1;
		String str2 = "" + ch2;
		Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
		String[] strArr = { str1, str2 };
		Arrays.sort(strArr, cmp);
		if (str1.equals(strArr[0]))
			return -1;
		else
			return 1;
	}

	/**
	 * ���ַ����ֵȼ�
	 * 
	 * @param ch
	 *            char
	 * @return int
	 */
	public int charRank(char ch) {
		String str = "" + ch;
		if (str.matches("\\p{Punct}{1}")) {// ������
			return 1;
		} else if (str.matches("\\p{Digit}{1}")) {// ����
			return 2;
		} else if (str.matches("\\p{Alpha}{1}")) {// Ӣ��
			return 3;
		} else if (str.matches("[\u4e00-\u9fa5]{1}")) {
			if (str.matches("[һ�����������߰˾���]{1}")) {// ��������
				return 4;
			} else {
				return 5;// �������ĺ���
			}
		} else {// �����������ַ���
			return 6;
		}
	}

}
