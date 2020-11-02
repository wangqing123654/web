package jdo.hl7.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author shibl
 *
 */
public class Status {
	public static final int KLS= 0;// ����״̬
	public static final int JSS = 1;// ҽ������
	public static final int YYS = 2;// ԤԼ
	public static final int CAYYS = 3;// ȡ��ԤԼ
	public static final int DJS = 4;// ����
	public static final int CADJS = 5;// ȡ������
	public static final int DJWCS = 6;// ������
	public static final int BGS = 7;// �������
	public static final int SHS = 8;// ����������
	public static final int DCS = 9;// DCҽ������
	public static final int CABGS = 10;// ȡ������
	public static final int QS = 11;// ǩ��
	public static final int CAQS = 12;//ȡ��ǩ��
	public static final int JS = 13;//����
	/**
	 * ״̬���������ƶ�Ӧ
	 * @return
	 */
	public static Map<Integer,String> getStatusMap(){
		Map<Integer,String> map=new HashMap<Integer,String>();
		map.put(Status.KLS, "����״̬");
		map.put(Status.JSS, "ҽ������");
		map.put(Status.YYS, "ԤԼ״̬");
		map.put(Status.CAYYS, "ȡ��ԤԼ");
		map.put(Status.DJS, "����״̬");
		map.put(Status.CADJS, "ȡ������");
		map.put(Status.DJWCS, "������");
		map.put(Status.BGS, "����״̬");
		map.put(Status.SHS, "���״̬");
		map.put(Status.DCS, "DC״̬");
		map.put(Status.CABGS, "ȡ������");
		map.put(Status.QS, "ǩ��״̬");
		map.put(Status.CAQS, "ȡ��ǩ��");
		map.put(Status.JS, "����״̬");
		return map;
	}
}
