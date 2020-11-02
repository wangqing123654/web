package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: 医嘱模板替换工具类</p>
 *
 * <p>Description: 医嘱模板替换工具类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2011.05.27
 * @version 1.0
 */
public class SYSComorderReplaceTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static SYSComorderReplaceTool instanceObject;
    /**
     * 得到实例
     * @return DeptTool
     */
    public static SYSComorderReplaceTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSComorderReplaceTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SYSComorderReplaceTool() {
        setModuleName("sys\\SYSComorderReplaceModule.x");
        onInit();
    }

    /**
     * 查询全字段
     * @param parm TParm
     * @return TParm
     */
    public TParm select(TParm parm) {
        TParm result = query("select", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询最大SEQ
     * @return TParm
     */
    //=====modify-begin (by wanglong 20120903)===============================
    public TParm selMaxSeq(String orderCode) {
        TParm result = new TParm();
        String sql = "SELECT MAX (SEQ) AS SEQ FROM SYS_COMORDER_REPLACE" +
                     " WHERE ORDER_CODE = '"+orderCode+"'";
        //======modify-end===================================================
        result = new TParm(TJDODBTool.getInstance().select(sql));
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 替换模板医嘱数据
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onReplace(TParm parm, TConnection connection) {
        TParm returnParm = parm.getParm("ACTION");
        //System.out.println("returnParm"+returnParm);
        //double mediQty = 0.00;//delete by wanglong 20120903
        //double oldMediQty = 0.00;//delete by wanglong 20120903
        String tableFlg = parm.getData("FLG").toString();//===YANJING 20150111
        TParm result = new TParm();
        int count = returnParm.getCount("ORDER_CODE");
        String sql = "";
//        String scpSql = "";
        for (int i = 0; i < count; i++) {
            //mediQty = returnParm.getInt("MEDI_QTY", i);//delete by wanglong 20120903
            //oldMediQty = returnParm.getInt("MEDI_QTY_OLD", i);//delete by wanglong 20120903
            //=====modify-begin (by wanglong 20120903)===============================
        	if(tableFlg.equals("SYS_ORDER_PACKD")){//手术室模版
        		String routeWhere="";
//                if(returnParm.getValue("ROUTE_CODE_OLD", i).equals("")){
//                	routeWhere= " AND (ROUTE_CODE = '" + returnParm.getValue("ROUTE_CODE_OLD", i) + "' OR  ROUTE_CODE IS NULL)";
//                }else{
//                	routeWhere= " AND ROUTE_CODE = '" + returnParm.getValue("ROUTE_CODE_OLD", i) + "'";
//                }
                String unitWhere="";
//                if(returnParm.getValue("MEDI_UNIT_OLD", i).equals("")){
//                	unitWhere= " AND (DOSAGE_UNIT = '" + returnParm.getValue("MEDI_UNIT_OLD", i) + "' OR DOSAGE_UNIT IS NULL)";
//                }else{
//                	unitWhere= " AND DOSAGE_UNIT = '" + returnParm.getValue("MEDI_UNIT_OLD", i) + "'";
//                }
                sql =
//                    "UPDATE OPD_COMORDER " +
                	"UPDATE " +""+tableFlg+""+
                    "  SET ORDER_CODE = '" + returnParm.getValue("ORDER_CODE", i) +"', " +
                         "ORDER_DESC = '" + returnParm.getValue("ORDER_DESC", i) + "', " +
//                         "MEDI_QTY = " + returnParm.getInt("MEDI_QTY", i) +
                         "DOSAGE_UNIT = " + returnParm.getInt("MEDI_UNIT", i) +
                    " WHERE ORDER_CODE = '" + returnParm.getValue("ORDER_CODE_OLD", i) + "' " +
                   routeWhere+
                   unitWhere;
                //======modify-end========================================================   
        	}else if(tableFlg.equals("CLP")){
        		String scpSql =
                        " UPDATE SYS_COMORDER_REPLACE SET UPDATE_FLG = 'Y' "+
                        "  WHERE ORDER_CODE = '"+returnParm.getValue("ORDER_CODE", i)+"' "+
                        "    AND SEQ = "+returnParm.getValue("SEQ", i)+" ";
               // System.out.println("scpSql"+scpSql);
                result = new TParm(TJDODBTool.getInstance().update(scpSql, connection));
                if (result.getErrCode() < 0) {
                    err(result.getErrCode() + " " + result.getErrText());
                    return result;
                }
        	}
        	else{
        		String routeWhere="";
//                if(returnParm.getValue("ROUTE_CODE_OLD", i).equals("")){
//                	routeWhere= " AND (ROUTE_CODE = '" + returnParm.getValue("ROUTE_CODE_OLD", i) + "' OR  ROUTE_CODE IS NULL)";
//                }else{
//                	routeWhere= " AND ROUTE_CODE = '" + returnParm.getValue("ROUTE_CODE_OLD", i) + "'";
//                }
                String unitWhere="";
//                if(returnParm.getValue("MEDI_UNIT_OLD", i).equals("")){
//                	unitWhere= " AND (MEDI_UNIT = '" + returnParm.getValue("MEDI_UNIT_OLD", i) + "' OR MEDI_UNIT IS NULL)";
//                }else{
//                	unitWhere= " AND MEDI_UNIT = '" + returnParm.getValue("MEDI_UNIT_OLD", i) + "'";
//                }
                sql =
//                    "UPDATE OPD_COMORDER " +
                	"UPDATE " +""+tableFlg+""+
                    "  SET ORDER_CODE = '" + returnParm.getValue("ORDER_CODE", i) +"', " +
                         "ORDER_DESC = '" + returnParm.getValue("ORDER_DESC", i) + "', " +
                         "MEDI_QTY = " + returnParm.getInt("MEDI_QTY", i) +","+
                         "MEDI_UNIT = " + returnParm.getInt("MEDI_UNIT", i) +","+
                         "ROUTE_CODE = '" + returnParm.getValue("ROUTE_CODE", i)+"'" +
                    " WHERE ORDER_CODE = '" + returnParm.getValue("ORDER_CODE_OLD", i) + "' " +
                   routeWhere+
                   unitWhere;
                //======modify-end========================================================   
        	}
            result = new TParm(TJDODBTool.getInstance().update(sql, connection));
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return result;
            }
           
//            scpSql =
//                " UPDATE SYS_COMORDER_REPLACE SET UPDATE_FLG = 'Y' "+
//                "  WHERE ORDER_CODE = '"+returnParm.getValue("ORDER_CODE", i)+"' "+
//                "    AND SEQ = "+returnParm.getValue("SEQ", i)+" ";
           // System.out.println("scpSql"+scpSql);
//            result = new TParm(TJDODBTool.getInstance().update(scpSql, connection));
//            if (result.getErrCode() < 0) {
//                err(result.getErrCode() + " " + result.getErrText());
//                return result;
//            }

        }
        return result;
    }
    /**
     * 新增
     * @param parm
     * @param connection
     * @return
     */
    public TParm insertComorderReplace(TParm parm, TConnection connection){
    	TParm result=update("insertComorderReplace",parm, connection);
    	if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
    	return result;
    }
    /**
     * 修改
     * @param parm
     * @param connection
     * @return
     */
    public TParm updateComorderReplace(TParm parm, TConnection connection){
    	TParm result=update("updateComorderReplace",parm, connection);
    	if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
    	return result;
    }
    /**
     * 删除
     * @param parm
     * @param connection
     * @return
     */
    public TParm deleteComorderReplace(TParm parm, TConnection connection){
    	TParm result=update("deleteComorderReplace",parm, connection);
    	if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
    	return result;
    }
}
