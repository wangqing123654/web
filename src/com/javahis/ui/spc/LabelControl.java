package com.javahis.ui.spc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.label.LabelImpl;
import jdo.label.LabelInf;

import com.dongyang.control.TControl;

/**
 * 
 * <p>Title:�������ӱ�ǩ�ܿ��� </p>
 *
 * <p>Description:TODO </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author Yuanxm
 * @version 1.0
 */

public class LabelControl extends TControl {

	private static final LabelControl INSTANCE = new LabelControl();

	public static LabelControl getInstance() {
		return INSTANCE;
	}

	/**
	 * �������ӱ�ǩ
	 * 
	 * @param list
	 * @param url
	 * @return
	 */
	public boolean sendLabelDate(List<Map<String, Object>> list, String url) {

		List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();

		String objectId = getObjectId();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = (Map<String, Object>) list.get(i);
			Map<String, Object> newMap = new LinkedHashMap<String, Object>();
			newMap.put("ObjectId", objectId);
			newMap.put("ObjectType", 1);
			newMap.put("ObjectName", "medBasket");
			//���ӱ�ǩID
			newMap.put("TagNo", map.get("TagNo"));
			
			//����
			newMap.put("APRegion", map.get("APRegion"));
			newMap.put("ShelfNo", "10411515515");
			
			//��ʾ��ҩƷ�������ӱ�ǩ��һ����ʾ���� 
			newMap.put("ProductName", map.get("ProductName"));
			
			//�ڶ�����ʾ����
			String spec = (String) map.get("SPECIFICATION");
			if(spec != null && !spec.equals("") &&  (spec.length() > 12)){
				spec = spec.substring(0,12);
			}
			
			String num = (String)map.get("NUM");
			if(num == null || num.equals("null") || num.equals("")){
				num = "";
			}
			
			spec = spec + " "+num;
			
			//System.out.println("Spec--------------:"+spec +" ����Ϊ��"+spec.length());
			// ���ӱ�ǩ��ʾ�ڶ������� ��� �� ����
			newMap.put("Spec", spec);
			
			//��˸����
			newMap.put("Light", map.get("Light"));

			// map.put("LightTimes", 3);
			newMap.put("RSend", "1");
			newMap.put("RSendSum", 1);
			newMap.put("Enabled", false);
			newList.add(newMap);
		}
		LabelInf labelInf = new LabelImpl();
		Map<String, Object> returnMap = labelInf.labelData(newList, url);
		if (null != returnMap) {
			String status = (String) returnMap.get("Status");
			if (null != status && "10000".equals(status)) {// ���µ��ӱ�ǩ״̬�ɹ�
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	// ��ȡ����ʱ��
	public static String getNowTime(String dateformat) {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);// ���Է�����޸����ڸ�ʽ
		String hehe = dateFormat.format(now);
		return hehe;
	}

	public static String getObjectId() {
		String dateStr = getNowTime("yyyyMMddHHmmssSSS");
		return "10" + dateStr;
	}

}
