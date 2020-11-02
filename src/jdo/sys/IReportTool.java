package jdo.sys;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.javahis.util.IReport;
/**
 * <p>
 * Title: 报表公用方法
 * </p>
 * 
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author chenxi 2013-04-19
 * @version 4.0.1
 */
public class IReportTool {
	public static final String OFF="off";
	public static final String ON="on";
	/**
	 * 实例
	 */
	private  static IReportTool instance ;
	/**
	 * 得到实例
	 * 
	 * @return EKTIO
	 */
	public static IReportTool getInstance() {
		if (instance == null)
			instance = new IReportTool();
		return instance;
	}

	/**
	 * 读取 ReportConfig.x
	 * 
	 * @return reportConfig
	 */
	public static TConfig getReportConfig() {
		TConfig reportConfig = TConfig
				.getConfig("WEB-INF\\config\\system\\ReportConfig.x");
		return reportConfig;
	}
	/**
	 * 得到报表打印开关
	 * @param id 报表打印开关 on为开 off为关
	 * @return
	 * liling 20140717 add
	 */
	public String getPrintSwitch(String id){
		String com = "";
		com = getReportConfig().getString(id) ;
		return com;
	}	
	/**
	 * 得到报表预览开关
	 * @param id 报表预览开关 on为开 off为关
	 * @return
	 *  liling 20140717 add
	 */
	public String getPreviewSwitch(String id){
		String com = "";
		com = getReportConfig().getString(id) ;
		return com;
	}
	/**
	 * 得到报表预览开关(布尔型)
	 * 报表预览开关 on为开 off为关
	 * @return
	 *  xiongwg 20150520 add
	 */
	public boolean getPreviewSwitchFlg(String id){
		boolean com;
		if(null!=getPreviewSwitch(id)&&getPreviewSwitch(id).equalsIgnoreCase("ON")){
			com = true;
		}else{
			com = false;
		}
		return com;
	}
	/**
	 * 得到报表路径
	 * id  配置文件配置报表路径的id
	 * 
	 * @return   com
	 */
	public String getReportPath(String id) {
		String com = "";
		com =getReportConfig().getString(id) ;
		if (com == null || com.trim().length() <= 0) {
			System.out.println("配置报表路径错误！");
		}
		return com;
	}	
	/**
	 * 得到动态报表数据
	 * id 配置文件的标示，通过它取classname
	 * parm 参数
	 * @return  result
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	public TParm getReportParm(String id,TParm parm) {
		String className = this.getReportPath(id) ;
		TParm result = null ;
		Class c  ;
		try {
			  c = Class.forName(className);
			  IReport instance=(IReport)c.newInstance();
			  result = instance.getReportParm(parm);
				} catch (ClassNotFoundException e) {
				System.out.println("错误信息1======"+e.toString());
				} catch (InstantiationException e) {
				System.out.println("错误信息2======"+e.toString());
				} catch (IllegalAccessException e) {
				System.out.println("错误信息3======"+e.toString());
				}
			
		return result; 
	}
	public static void main(String[] args) {
		IReportTool  tool  =  new IReportTool() ;
//		tool.getReportParm("") ;
	}
}
