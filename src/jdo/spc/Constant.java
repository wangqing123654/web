package jdo.spc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;




/**
 * <p>
 * Title: ҩ���Tool
 * </p>
 *
 * <p>
 * Description: ҩ���Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 *
 * <p>
 * Company:BlueCore
 * </p>
 *
 * @author Yuanxm 2012.08.30
 * @version 1.0
 */
public class Constant {
	
	public static  List<NameValuePair> parameters = new ArrayList<NameValuePair>();  
	
	public static final String IPPORT = "http://172.20.10.63:8080/";
	/**
	 * ����״̬����URL
	 */
	public static final String SERVER_STATUS_URL = IPPORT+"IDILinkerService.svc/Status";
	
	/**
	 * �û���¼URL
	 */
	public static final String LOGIN_URL = IPPORT+"IDILinkerService.svc/User/";
	
	/**
	 * �û���ѯURL
	 */
	public static final String FIND_USER_URL = IPPORT+"IDILinkerService.svc/GetUser/";
	
	/**
	 * ҩ����λ����URL
	 */
	public static final String CARGO_URL = IPPORT+"IDILinkerService.svc/LabelData/1";
	
	/**
	 * ҩ�����URL
	 */
	public static final String DRUGBASKET_URL = IPPORT+"IDILinkerService.svc/LabelData/2";
	
	/**
	 * ҩ�����URL
	 */
	public static final String MEDICINECHEST_URL = IPPORT+"IDILinkerService.svc/LabelData/3";
	
	/**
	 * ҩ��URL 
	 */
	public static final String PCS_URL = IPPORT+"IDILinkerService.svc/LabelData/4";
	
	/**
	 * ��ȡ���ӱ�ǩURL
	 */
	public static final String LABLE_URL = IPPORT+"IDILinkerService.svc/ResultESLData";
	
	

}
