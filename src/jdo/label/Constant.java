package jdo.label;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;




/**
 * <p>
 * Title: 药筐绑定Tool
 * </p>
 *
 * <p>
 * Description: 药筐绑定Tool
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
	
	public static final String IPPORT = "http://172.20.10.63:8081/";
	
	//public static final String IPPORT = "http://172.20.168.132:8081/";
	/**
	 * 基站区域URL
	 */
	public static final String APPREGION_URL = IPPORT+"IdIlinkerService.svc/APRegion";
	
	/**
	 * 标签设定URL
	 */
	public static final String APTAG_URL = IPPORT+"IdIlinkerService.svc/APTag";
	
	/**
	 * 检查要发送数据的标签区域是否对应URL
	 */
	public static final String CHECKREGIONLABEL_URL = IPPORT+"IDILinkerService.svc/CheckRegionLabel/";
	
	/**
	 * 发送标签显示数据URL
	 */
	public static final String LABELDATA_URL = IPPORT+"IDILinkerService.svc/LabelData/1";
	
	/**
	 * 通过事件号查询发送结果URL
	 */
	public static final String SENDRESULT_URL = IPPORT+"IDILinkerService.svc/SendResult/ObjectId";
	
	/**
	 * 查询区域下基站号和IPURL
	 */
	public static final String GETREGIONAP_URL = IPPORT+"IDILinkerService.svc/GetRegionAP/RegionId";
	
	/**
	 *查询区域下全部标签最后一次通讯状态URL 
	 */
	public static final String GETREGIONLABEL_URL = IPPORT+"IDILinkerService.svc/GetRegionLabel/RegionId";
	
	/**
	 * 查询区域下全部标签最后一次通讯状态URL
	 */
	public static final String GETLABEL_URL = IPPORT+"IDILinkerService.svc/GetLabel/LabelNo";
	
	

}
