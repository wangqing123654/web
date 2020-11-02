package com.javahis.ui.spc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jdo.ind.Constant;
import jdo.ind.ElectronicTagsImpl;
import jdo.ind.ElectronicTagsInf;
import jdo.label.LabelImpl;
import jdo.label.LabelInf;

import org.apache.http.message.BasicNameValuePair;

import com.dongyang.control.TControl;

/**
 * 
 * <p>Title:���ӱ�ǩ�ܿ��� </p>
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
public class EleTagControl  extends TControl  {
	
	private static final EleTagControl INSTANCE =new EleTagControl();
	
	public static EleTagControl getInstance(){
        return INSTANCE;
    }

	
	public  void login(){
		ElectronicTagsInf eti = new ElectronicTagsImpl() ;
		Map<String, Object> map = eti.login("admin", "123");
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it
					.next();
			Constant.parameters.clear();
		}
		 
		Constant.parameters.add(0,new BasicNameValuePair("Token", (String)map.get("Token")));
		Constant.parameters.add(1,new BasicNameValuePair("UserId", (String)map.get("UserId")));
		Constant.parameters.add(2,new BasicNameValuePair("RoleId", (String)map.get("RoleId")));
	}
	
    /**
     * ���µ��ӱ�ǩ
     * @param bastkId  ���ӱ�ǩID
     * @param nameInfo  
     * @param spec
     * @param num
     * @param lightNum TODO
     * @return
     * @author 
     * @date 
     */
	  public boolean sendEleTag(String bastkId,String nameInfo,String spec ,String num, int lightNum){
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		UUID uuid = UUID.randomUUID();
		if(null == uuid )
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
		
		if(spec != null && !spec.equals("")){
			spec = spec.substring(0,12);
		}
		// �ڶ��� ��ʾ������ ���Ա�
		m.put("Spec", spec + " " + num);
		// ������ ���ӱ�ǩ��ά���뺬��
		 
		m.put("ShelfNo", bastkId);
	 
		// ��˸����
		m.put("Light", lightNum);
		// �Ƿ����ƣ�true:��
		m.put("Enabled", true);
		
		Iterator it = m.entrySet().iterator();
	 
		ElectronicTagsInf eti = new ElectronicTagsImpl();
		// ���õ��ӱ�ǩ�ӿ�
		Map<String, Object> map = eti.cargoUpdate(m);
		
		
		if(null != map){
			it = map.entrySet().iterator();
			 
	    	String status = (String) map.get("Status");
	    	if(null != status && "10000".equals(status)){//���µ��ӱ�ǩ״̬�ɹ�
	    		return true;
	    	}else{
	    		return false;
	    	}			
		}else{
			return false;
		}
	  }
	
	
	  /**
	   * �������ӱ�ǩ
	   * @param list
	   * @param url
	   * @return
	   */
	  public boolean sendNewEleTag(List<Map<String, Object>> list,String url ){
		  
		  List<Map<String, Object>> newList = new ArrayList<Map<String,Object>>();
		  
		  String objectId =  getObjectId();
		  for(int i = 0 ; i < list.size() ; i++){
			   Map<String, Object> map = (Map<String, Object>)list.get(i);
			   Map<String ,Object> newMap = new LinkedHashMap<String, Object>();
			   newMap.put("ObjectId",objectId);
			   newMap.put("ObjectType", 1);
			   newMap.put("ObjectName", "medBasket");
			   newMap.put("TagNo", map.get("TagNo"));
			   newMap.put("APRegion", map.get("APRegion"));
			   newMap.put("ShelfNo", "10411515515");
			   newMap.put("ProductName",map.get("ProductName"));
			   String spec = (String) map.get("SPECIFICATION") ;
			   //���ӱ�ǩ��ʾ�ڶ������� ��� �� ����
			   newMap.put("Spec", spec);
			   newMap.put("Light", map.get("Light"));
				
				//map.put("LightTimes", 3);
			   newMap.put("RSend", "1");
			   newMap.put("RSendSum", 3);
			   newMap.put("Enabled", false);
			   newList.add(newMap);
		  }
		  LabelInf labelInf = new LabelImpl();
		  Map<String, Object> returnMap  = labelInf.labelData(newList, url);
		  if(null != returnMap){
			  String status = (String) returnMap.get("Status");
		    	if(null != status && "10000".equals(status)){//���µ��ӱ�ǩ״̬�ɹ�
		    		return true;
		    	}else{
		    		return false;
		    	}			
		  }else{
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
	
	public static String getObjectId(){
		String dateStr = getNowTime("yyyyMMddHHmmssSSS");
		return "10"+dateStr ;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
