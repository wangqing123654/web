package com.javahis.ui.spc.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.message.BasicNameValuePair;


/**
 * ���ӱ�ǩ������
 * 
 * @author liyh
 * @date 20121112
 * 
 */
public class ElectronicTagUtil {

	/**
	 * ʵ��
	 */
	public static ElectronicTagUtil instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INDTool
	 */
	public static ElectronicTagUtil getInstance() {
		if (instanceObject == null)
			instanceObject = new ElectronicTagUtil();
		return instanceObject;
	}

	/**
	 * ��½
	 */
	public static void login() {
		ElectronicTagsInf eti = new ElectronicTagsImpl();
		Map<String, Object> map = eti.login("admin", "123");
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
			Constant.parameters.clear();
		}

		Constant.parameters.add(0, new BasicNameValuePair("Token", (String) map.get("Token")));
		Constant.parameters.add(1, new BasicNameValuePair("UserId", (String) map.get("UserId")));
		Constant.parameters.add(2, new BasicNameValuePair("RoleId", (String) map.get("RoleId")));
	}

	/**
	 * ���µ��ӱ�ǩ
	 * 
	 * @param bastkId  ���ӱ�ǩID
	 * @param nameInfo ��һ����ʾ����
	 * @param spec �ڶ�����ʾ����
	 * @param num �ڶ�����ʾ����
	 * @param lightNum ��������
	 * @return
	 * @author liyh
	 * @date 20120919
	 */
	public boolean sendEleTag(String bastkId, String nameInfo, String spec, String num, int lightNum) {
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		UUID uuid = UUID.randomUUID();
		if (null == uuid)
			uuid = UUID.randomUUID();

		m.put("ObjectId", uuid.toString());
		m.put("ObjectType", 3);
		m.put("ObjectName", "medBasket");
		// m.put("LabelNo", "01048A");
		// ���ӱ�ǩid
		m.put("LabelNo", bastkId);
		// ��վid
		m.put("StationID", "2");
		// ��һ�� ��ʾ�û���������
		m.put("ProductName", nameInfo);
		// �ڶ��� ��ʾ������ ���Ա�
		m.put("Spec", spec + " " + num);
		// ������ ���ӱ�ǩ��ά���뺬��
		m.put("ShelfNo", bastkId);

		// ��˸����
		m.put("Light", lightNum);
		// �Ƿ����ƣ�true:��
		m.put("Enabled", true);

		Iterator it = m.entrySet().iterator();
//		System.out.println("------------��ҩ--���ӱ�ǩ��������---------start----------");
		ElectronicTagsInf eti = new ElectronicTagsImpl();
		// ���õ��ӱ�ǩ�ӿ�
		Map<String, Object> map = eti.cargoUpdate(m);
		
		System.out.println("��ҩ================================begin");
		Iterator it1 = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it1.next();
			System.out.println(entry.getKey() + "===================="+ entry.getValue());
		}
		System.out.println("��ҩ================================end");
		
		if (null != map) {
			it = map.entrySet().iterator();
			String status = (String) map.get("Status");
			if (null != status && "10000".equals(status)) {// ���µ��ӱ�ǩ״̬�ɹ�
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}
	
	public static void main(String[] args){
		ElectronicTagUtil.getInstance().login();
		ElectronicTagUtil.getInstance().sendEleTag("", "" , "","",1);
	}
}
