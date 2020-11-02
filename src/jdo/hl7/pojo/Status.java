package jdo.hl7.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author shibl
 *
 */
public class Status {
	public static final int KLS= 0;// 开立状态
	public static final int JSS = 1;// 医嘱接收
	public static final int YYS = 2;// 预约
	public static final int CAYYS = 3;// 取消预约
	public static final int DJS = 4;// 到检
	public static final int CADJS = 5;// 取消到检
	public static final int DJWCS = 6;// 检查完成
	public static final int BGS = 7;// 报告完成
	public static final int SHS = 8;// 报告审核完成
	public static final int DCS = 9;// DC医嘱接收
	public static final int CABGS = 10;// 取消报告
	public static final int QS = 11;// 签收
	public static final int CAQS = 12;//取消签收
	public static final int JS = 13;//拒收
	/**
	 * 状态编码与名称对应
	 * @return
	 */
	public static Map<Integer,String> getStatusMap(){
		Map<Integer,String> map=new HashMap<Integer,String>();
		map.put(Status.KLS, "开立状态");
		map.put(Status.JSS, "医嘱接收");
		map.put(Status.YYS, "预约状态");
		map.put(Status.CAYYS, "取消预约");
		map.put(Status.DJS, "到检状态");
		map.put(Status.CADJS, "取消到检");
		map.put(Status.DJWCS, "检查完成");
		map.put(Status.BGS, "报告状态");
		map.put(Status.SHS, "审核状态");
		map.put(Status.DCS, "DC状态");
		map.put(Status.CABGS, "取消报告");
		map.put(Status.QS, "签收状态");
		map.put(Status.CAQS, "取消签收");
		map.put(Status.JS, "拒收状态");
		return map;
	}
}
