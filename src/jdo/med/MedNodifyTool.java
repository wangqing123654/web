package jdo.med;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.print.DocFlavor.STRING;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:检验检查通知
 *
 * <p>Description: 
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx 
 * @version 4.0
 */
public class MedNodifyTool extends TJDOTool{
	
	/**
	 * 构建实例
	 */
  public static MedNodifyTool instanceObject ;
    /**
     * 得到实例
     * @return
     */
  public static MedNodifyTool getInstance(){
	  if(instanceObject==null)
		  instanceObject = new MedNodifyTool() ;
	  return instanceObject ;
  }
  
  /**
   * 构造器
   */
  public MedNodifyTool(){
	  setModuleName("med\\MEDNodifyModule.x") ;
	  onInit() ;
  }
  
  /**
   * 查询  配置字典
   */
  public TParm query(TParm parm){
	  String sql = "SELECT A.*,B.PASSWORD  FROM MED_NODIFY_CONFIG A ,SKT_USER B " +
	  		       " WHERE A.SKT_USER = B.ID AND A.OPT_TERM ='"+parm.getValue("IP")+"'" +
	  		       		" AND OPEN_FLG = 'Y' " ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;     
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 查询  配置字典
   */
  public TParm queryIp(TParm parm){
	  String sql = "SELECT A.* FROM MED_NODIFY_CONFIG A  " +
	  		       " WHERE A.OPT_TERM ='"+parm.getValue("IP")+"'" ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;     
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 查询  配置字典所有
   */
  public TParm queryAll(TParm parm){
	  String sql = "SELECT A.*,B.PASSWORD  FROM MED_NODIFY_CONFIG A ,SKT_USER B " +
	  		       " WHERE A.SKT_USER = B.ID AND OPEN_FLG = 'Y' " ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;     
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 查询  检查主项
   */
  public TParm selectCheckResult(TParm parm){
	  TParm result = this.query("selectCheckResult", parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 查询  检验结果
   */
  public TParm selectResultLIS(TParm parm){
	  TParm result = this.query("selectResultLIS", parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 查询  检查结果
   */
  public TParm selectResultRIS(TParm parm){
	  TParm result = this.query("selectResultRIS", parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 查询   检验、检查结果是否出来
   */
  public TParm selectResultOut(TParm parm){
	  SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");  
      Date date = new Date(); 
         
      Calendar ca=Calendar.getInstance();
      ca.setTime(date);
      ca.add(Calendar.HOUR_OF_DAY, -3);
      
      String  endDate= format.format(date);
      String startDate =format.format(ca.getTime());
      
      /*String startDate = format.format(date).toString()+"000000";
      String endDate = format.format(date).toString()+"235959";*/
      
      String whereSql = "" ;
	  String sql = "SELECT CASE_NO,MR_NO FROM MED_NODIFY " +
	  		       " WHERE SEND_STAT='1' " +
	  		       " AND SEND_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')" +
	  		       " AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"  ;
	  //
	  if(parm.getValue("SKT_TYPE").equals("1"))
		  whereSql = " AND BILLING_DOCTORS = '"+parm.getValue("SKT_USER")+"'" ;
	  else {
		  if(parm.getValue("ADM_TYPE").equals("1"))
			  whereSql = " AND STATION_CODE = '"+parm.getValue("STATION_CODE")+"'" ;
		  else whereSql = " AND CLINICAREA_CODE = '"+parm.getValue("STATION_CODE")+"'" ;
	  }
	  sql +=whereSql ;
	  //
	  //System.out.println("====MedNodifyTool sql========"+sql);  
	  //
	  TParm result =new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;  
  }
  /**
   * 新增
   * @param parm
   * @return
   */
  public TParm insertData(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("insertdata", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
	  return result ;
	  
  }
  

  /**
   *  更新用户姓名
   * @param parm
   * @return
   */
  
  public TParm onUpdateName(TParm parm ,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("updatename", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
  }
  /**
   *  更新全字段
   * @param parm
   * @return
   */
  
  public TParm onUpdateData(TParm parm ,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("updatedata", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
  }
  /**
   *  更新状态，发送与没有发送状态
   * @param parm
   * @return
   */
  
  public TParm onUpdateStat(TParm parm,TConnection conn ){
	  TParm result = new TParm() ;
	  result = update("onUpdateStat", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
  }
  /**
   * 删除
   * @param parm
   * @return
   */
  public TParm deleteData(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
	  return result ;
  }

}

