package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.dongyang.db.TConnection;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>Title:医师日班表工具类 </p>
 *
 * <p>Description:医师日班表工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.16
 * @version 1.0
 */
public class SchDayTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static SchDayTool instanceObject;
    
    private boolean crmFlg = StringTool.getBoolean(TConfig.getSystemValue("crm.switch"));
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static SchDayTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SchDayTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SchDayTool() {
        setModuleName("reg\\REGSchDayModule.x");
        onInit();
    }
    /**
     * 新增日班表(前端)
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
        parm.setData("ADM_DATE", adm_Date);
        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增日班表（后端）
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection connection) {
        TParm result = new TParm();
        parm.setData("ADM_DATE", parm.getData("ADM_DATE"));
        result = update("insertdata", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增日班表
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm schWeekForDay(TParm parm, TConnection connection) {
        Object obj = this.checkSpreadOut(parm);
//        System.out.println("obj检核数据" + obj);
        TParm result = (TParm) obj;
        if (result.getErrCode() != 0)
            return result;
        Timestamp admDateStart = (Timestamp) parm.getData("ADM_DATE_START");
        Timestamp admDateEnd = (Timestamp) parm.getData("ADM_DATE_END");
        int rangeDay = StringTool.getDateDiffer(admDateEnd, admDateStart);
        int notCount =0;
        for (int i = 0; i <= rangeDay; i++) {
            Timestamp day = StringTool.rollDate(admDateStart, i);
            TParm inParm = new TParm();
            String adm_Date = StringTool.getString(day, "yyyyMMdd");
//             inParm.setData("ADM_DATE",adm_Date);
            inParm.setData("ADM_DATE", day);
            inParm.setData("DAYOFWEEK", StringTool.getWeek(day));
            //add by huangtt 20140911 start
            if(inParm.getDouble("DAYOFWEEK") == 0){
            	inParm.setData("DAYOFWEEK", 7);
            }
            //add by huangtt 20140911 end
            inParm.setDataN("REGION_CODE", parm.getValue("REGION_CODE"));
            inParm.setDataN("ADM_TYPE", parm.getValue("ADM_TYPE"));
            inParm.setDataN("DR_CODE", parm.getValue("DR_CODE"));
            inParm.setDataN("DEPT_CODE", parm.getValue("DEPT_CODE"));
            //add by huangtt 20141111  增加时段 start
            if(parm.getValue("SESSION_CODE") != null || !"".equals(parm.getValue("SESSION_CODE") )){
            	inParm.setDataN("SESSION_CODE", parm.getValue("SESSION_CODE"));  

            }
            //add by huangtt 20141111  增加时段  end
            if(crmFlg && "O".equals(parm.getValue("ADM_TYPE"))){
            	 
				TParm schdayParm = new TParm();
				schdayParm.setData("REALDEPT_CODE", parm.getValue("DEPT_CODE"));
				schdayParm.setData("CLINICTYPE_CODE", parm.getValue("CLINICTYPE_CODE"));
				schdayParm.setData("ADM_DATE", adm_Date);
				schdayParm.setData("REALDR_CODE", parm.getValue("DR_CODE"));
				schdayParm.setData("SESSION_CODE", parm
						.getValue("SESSION_CODE"));
				// System.out.println("schdayParm=="+schdayParm);
				if (checkSchday(schdayParm) > 0) {
					continue;
				}
            }else{
            	//删除当天日班
                result = deletedata(inParm);
            }
           
            result = SchWeekTool.getInstance().selectdata(inParm);
            int count = result.getCount();
            if(count == 0){
            	notCount++;
            }
            
            for (int j = 0; j < count; j++) {
                TParm p = new TParm();
                p.setRowData( -1, result, j);
                p.setData("ADM_DATE", adm_Date);
                p.setData("REALDEPT_CODE", p.getData("DEPT_CODE"));
                p.setData("REALDR_CODE", p.getData("DR_CODE"));
                p.setData("QUE_NO", 1);
                p.setData("MAX_QUE",
                          PanelGroupTool.getInstance().getMaxQue(p.
                    getValue("QUEGROUP_CODE")));
                p.setData("VIP_FLG",
                          PanelGroupTool.getInstance().getVipFlg(p.
                    getValue("QUEGROUP_CODE")));
                p.setData("STOP_SESSION", "N");
                p.setData("REFRSN_CODE", "");
//                System.out.println("周转日数据"+p);
                //诊室反查诊区信息
                TParm roomParm = PanelRoomTool.getInstance().getAreaByRoom(p.getValue(
                    "CLINICROOM_NO"));
                String areaCode = roomParm.getValue("CLINICAREA_CODE",0);
                p.setData("REG_CLINICAREA",areaCode);
                TParm result1 = insertdata(p, connection);
                if (result1.getErrCode() < 0)
                    return result1;
            }
        }

        if(notCount > rangeDay){
        	result.setErrCode(-1);
        	result.setErrText("没有查到可以展开的周班表");
        }
        return result;
    }
    /**
     * 周班转日班(新)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm schWeekForDayNew(TParm parm, TConnection connection) {
        Object obj = this.checkSpreadOutNew(parm);
        //System.out.println("obj检核数据" + obj);
        TParm result = (TParm) obj;
        if (result.getErrCode() != 0)
            return result;
        Timestamp admDateStart = (Timestamp) parm.getData("ADM_DATE_START");
        Timestamp admDateEnd = (Timestamp) parm.getData("ADM_DATE_END");
        int rangeDay = StringTool.getDateDiffer(admDateEnd, admDateStart);
        for (int i = 0; i <= rangeDay; i++) {
            Timestamp day = StringTool.rollDate(admDateStart, i);
            TParm inParm = new TParm();
            String adm_Date = StringTool.getString(day, "yyyyMMdd");
            inParm.setData("ADM_DATE", day);
            inParm.setData("DAYOFWEEK", StringTool.getWeek(day));
            //add by huangtt 20140911 start
            if(inParm.getDouble("DAYOFWEEK") == 0){
            	inParm.setData("DAYOFWEEK", 7);
            }
            //add by huangtt 20140911 end
            inParm.setDataN("REGION_CODE", parm.getValue("REGION_CODE"));
            inParm.setDataN("ADM_TYPE", parm.getValue("ADM_TYPE"));
            inParm.setDataN("CLINICROOM_NO", parm.getValue("CLINICROOM_NO"));
            
            TParm selDataNewParm = SchWeekTool.getInstance().selDataNew(inParm); 
            // System.out.println("周转日查询(新)"+selDataNewParm);
            int count = selDataNewParm.getCount();
            //modify by huangtt 20141112
            String c="";
            if(crmFlg && "O".equals(parm.getValue("ADM_TYPE"))){
            	 for(int k=0;k<count;k++){
            		 TParm schdayParm = new TParm();
                	 schdayParm.setData("REALDEPT_CODE", selDataNewParm.getValue("DEPT_CODE",k));
                	 schdayParm.setData("ADM_DATE", adm_Date);
                	 schdayParm.setData("CLINICTYPE_CODE", selDataNewParm.getValue("CLINICTYPE_CODE",k));
                	 schdayParm.setData("REALDR_CODE", selDataNewParm.getValue("DR_CODE",k));
                	 schdayParm.setData("SESSION_CODE", selDataNewParm.getValue("SESSION_CODE",k));
//                	 System.out.println("schdayParm=="+schdayParm);
                 	if(checkSchday(schdayParm) > 0){
                 		c = c + k + ","; 
                 		continue;
                 	}
            	 }
            	 if(c.length() > 0){
            		 c = c.substring(0, c.length()-1);
            	 }
            	
            }else{
            	 //删除当天日班
                result = delDataNew(inParm);
                //System.out.println("删除当天日班"+inParm);
            }
            
            for (int j = 0; j < count; j++) {
            	if(c.length()>0){
            		if(c.contains(j+"")){
            			continue;
            		}
            	}
            	
                TParm p = new TParm();
                p.setRowData( -1, selDataNewParm, j);
                p.setData("ADM_DATE", adm_Date);
                p.setData("REALDEPT_CODE", p.getData("DEPT_CODE"));
                p.setData("REALDR_CODE", p.getData("DR_CODE"));
                p.setData("QUE_NO", 1);
                p.setData("MAX_QUE",
                          PanelGroupTool.getInstance().getMaxQue(p.
                    getValue("QUEGROUP_CODE")));
                p.setData("VIP_FLG",
                          PanelGroupTool.getInstance().getVipFlg(p.
                    getValue("QUEGROUP_CODE")));
                p.setData("STOP_SESSION", "N");
                p.setData("REFRSN_CODE", "");
                //System.out.println("周转日数据"+p);
                //诊室反查诊区信息
                TParm roomParm = PanelRoomTool.getInstance().getAreaByRoom(p.getValue(
                    "CLINICROOM_NO"));
                String areaCode = roomParm.getValue("CLINICAREA_CODE",0);
                p.setData("REG_CLINICAREA",areaCode);
                TParm result1 = insertdata(p, connection);
                if (result1.getErrCode() < 0)
                    return result1;
            }
        }
        return result;
    }

    /**
     * 展班前检核
     * @param parm TParm
     * @return TParm
     */
    public TParm checkSpreadOut(TParm parm) {
        TParm result = new TParm();
        Timestamp admDateStart = (Timestamp) parm.getData("ADM_DATE_START");
        Timestamp admDateEnd = (Timestamp) parm.getData("ADM_DATE_END");
        //得到起日迄日天数差
        int rangeDay = StringTool.getDateDiffer(admDateEnd, admDateStart);
        for (int i = 0; i <= rangeDay; i++) {
            Timestamp day = StringTool.rollDate(admDateStart, i);
            TParm inParm = new TParm();
            String adm_Date = StringTool.getString(day, "yyyyMMdd");
            inParm.setData("ADM_DATE", adm_Date);
            inParm.setDataN("REGION_CODE", parm.getValue("REGION_CODE"));
            inParm.setDataN("ADM_TYPE", parm.getValue("ADM_TYPE"));
            inParm.setDataN("DR_CODE", parm.getValue("DR_CODE"));
            inParm.setDataN("DEPT_CODE", parm.getValue("DEPT_CODE"));
            result = SchDayTool.getInstance().selectdata(inParm);
            if (result.getCount() > 0) {
                inParm.setData("ADM_DATE", day);
                result = PatAdmTool.getInstance().selectdata(inParm);
                if (result.getCount() > 0) {
                    TParm errResult = new TParm();
                    errResult.setErr(1, "已有挂号信息,不能展开排班");
                    return errResult;
                }
            }
        }
        return result;
    }
    /**
     * 展班前检核(新)
     * @param parm TParm
     * @return TParm
     */
    public TParm checkSpreadOutNew(TParm parm) {
        TParm result = new TParm();
        Timestamp admDateStart = (Timestamp) parm.getData("ADM_DATE_START");
        Timestamp admDateEnd = (Timestamp) parm.getData("ADM_DATE_END");
        //得到起日迄日天数差
        int rangeDay = StringTool.getDateDiffer(admDateEnd, admDateStart);
        for (int i = 0; i <= rangeDay; i++) {
            Timestamp day = StringTool.rollDate(admDateStart, i);
            TParm inParm = new TParm();
            String adm_Date = StringTool.getString(day, "yyyyMMdd");
            inParm.setData("ADM_DATE", adm_Date);
            inParm.setDataN("REGION_CODE", parm.getValue("REGION_CODE"));
            inParm.setDataN("ADM_TYPE", parm.getValue("ADM_TYPE"));
            inParm.setDataN("CLINICROOM_NO", parm.getValue("CLINICROOM_NO"));
            result = SchDayTool.getInstance().selDataNew(inParm);
            if (result.getCount() > 0) {
                inParm.setData("ADM_DATE", day);
                result = PatAdmTool.getInstance().selDataNew(inParm);
                if (result.getCount() > 0) {
                    TParm errResult = new TParm();
                    errResult.setErr(1, "已有挂号信息,不能展开排班");
                    return errResult;
                }
            }
        }
        return result;
    }
    /**
     * 更新日班表
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
        parm.setData("ADM_DATE", adm_Date);
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新日班表(For ONW)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateForSchdayDr(TParm parm, TConnection conn) {

        TParm result = update("update_for_schday_dr", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询日班表
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        String admDate = "";
        if (parm.getData("ADM_DATE") instanceof String) {
            admDate = parm.getValue("ADM_DATE");
        }
        else {
            admDate = StringTool.getString(parm.getTimestamp("ADM_DATE"),
                                           "yyyyMMdd");
        }
        parm.setData("ADM_DATE", admDate);
        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 周转日查询日班表(新)
     * @param parm TParm
     * @return TParm
     */
    public TParm selDataNew(TParm parm) {
        String admDate = "";
        if (parm.getData("ADM_DATE") instanceof String) {
            admDate = parm.getValue("ADM_DATE");
        }
        else {
            admDate = StringTool.getString(parm.getTimestamp("ADM_DATE"),
                                           "yyyyMMdd");
        }
        parm.setData("ADM_DATE", admDate);
        String regionWhere = "";
        if(parm.getData("REGION_CODE")!=null)
              regionWhere= " AND REGION_CODE = '"+parm.getData("REGION_CODE")+"' ";
        String admTypeWhere = "";
        if(parm.getData("ADM_TYPE")!=null)
              admTypeWhere= " AND ADM_TYPE = '"+parm.getData("ADM_TYPE")+"' ";
        String roomWhere = "";
        if(parm.getData("CLINICROOM_NO")!=null)
              roomWhere= " AND CLINICROOM_NO IN ("+parm.getData("CLINICROOM_NO")+") ";
        String sql =
                " SELECT REGION_CODE, ADM_TYPE, ADM_DATE, SESSION_CODE, CLINICROOM_NO,"+
                "        WEST_MEDI_FLG, DEPT_CODE, REG_CLINICAREA, DR_CODE, REALDEPT_CODE,"+
                "        REALDR_CODE, CLINICTYPE_CODE, QUEGROUP_CODE, QUE_NO, MAX_QUE, VIP_FLG,"+
                "        CLINICTMP_FLG, STOP_SESSION, REFRSN_CODE, OPT_USER, OPT_DATE, OPT_TERM "+
                "   FROM REG_SCHDAY "+
                "  WHERE ADM_DATE = '"+admDate+"' "+
                regionWhere+
                admTypeWhere+
                roomWhere;
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//        TParm result = query("selDataNew", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除
     * @param parm TParm
     * @return TParm
     */
    public TParm deletedata(TParm parm) {
         Timestamp admDate = TCM_Transform.getTimestamp(parm.getData(
             "ADM_DATE"));
         String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
        parm.setData("ADM_DATE", adm_Date);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 周转日删除(根据诊室)
     * @param parm TParm
     * @return TParm
     */
    public TParm delDataNew(TParm parm) {

        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData(
                "ADM_DATE"));
        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
        parm.setData("ADM_DATE", adm_Date);
        String regionWhere = "";
        if (parm.getData("REGION_CODE") != null)
            regionWhere = " AND REGION_CODE = '" + parm.getData("REGION_CODE") +
                          "' ";
        String admTypeWhere = "";
        if (parm.getData("ADM_TYPE") != null)
            admTypeWhere = " AND ADM_TYPE = '" + parm.getData("ADM_TYPE") +
                           "' ";
        String roomWhere = "";
        if (parm.getData("CLINICROOM_NO") != null)
            roomWhere = " AND CLINICROOM_NO IN (" +
                        parm.getData("CLINICROOM_NO") + ") ";
        String sql =
                " DELETE FROM REG_SCHDAY " +
                "      WHERE ADM_DATE = '" + adm_Date + "' " +
                regionWhere +
                admTypeWhere +
                roomWhere;
        //System.out.println("删除日班"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
//        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询医师排班(一般)
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDrTable(TParm parm) {
        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
        parm.setData("ADM_DATE", adm_Date);
        TParm result = query("onQueryDrTable", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询医师排班中某一个班的具体信息(一般)校验改班是否存在
     * yanjing
     * 2013-05-07
     */
    public TParm selectOneDrTable(TParm parm) {
//        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
//        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
//        parm.setData("ADM_DATE", adm_Date);
        TParm result = query("onQueryOneDrTable", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询医师班表（VIP）
     * @param parm TParm
     * @return TParm
     */
    public TParm selVIPDrTable(TParm parm) {
        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
        parm.setData("ADM_DATE", adm_Date);
        TParm result = query("onQueryVipDrTable", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询就诊号,最大就诊号
     * @param region String
     * @param admType String
     * @param admDate Timestamp
     * @param session String
     * @param clinicRoom String
     * @return int
     */
    public int  selectqueno(String region, String admType, String admDate,
                           String session, String clinicRoom) {
        TParm parm = new TParm();
        parm.setData("REGION_CODE", region);
        parm.setData("ADM_TYPE", admType);
        parm.setData("ADM_DATE", admDate);
        parm.setData("SESSION_CODE", session);
        parm.setData("CLINICROOM_NO", clinicRoom);
        if (this.getResultInt(query("selectqueno", parm), "QUE_NO") >
            this.getResultInt(query("selectqueno", parm), "MAX_QUE")) {
            return -1;
        }
        return this.getResultInt(query("selectqueno", parm), "QUE_NO");

    }
    /**
     * 更新就诊号
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updatequeno(TParm parm,TConnection connection) {
		if ("java.sql.Timestamp".equalsIgnoreCase(parm.getData("ADM_DATE")
				.getClass().getName())) { // modify by wanglong 20121225
			String adm_Date = StringTool.getString(parm.getTimestamp("ADM_DATE"), "yyyyMMdd");
			parm.setData("ADM_DATE", adm_Date);
		}
		TParm result = update("updatequeno", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
    }
    /**
     * 更新就诊号
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     * =============pangben 2012-6-18 重号问题
     */
    public TParm updatequeno(TParm parm) {
//        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
//        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
//        parm.setData("ADM_DATE", adm_Date);
        TParm result = update("updatequeno", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 得到VIP标记位
     * @param region String 区域
     * @param admType String 门急别
     * @param admDate String 日期
     * @param session String 时段
     * @param clinicRoom String 诊室
     * @return boolean
     */
    public boolean isVipflg(String region, String admType, String admDate,
                            String session, String clinicRoom) {
        TParm parm = new TParm();
        parm.setData("REGION_CODE", region);
        parm.setData("ADM_TYPE", admType);
        parm.setData("ADM_DATE", admDate);
        parm.setData("SESSION_CODE", session);
        parm.setData("CLINICROOM_NO", clinicRoom);
        return getResultBoolean(query("selectclinictype", parm), "VIP_FLG");

    }

    /**
     * 通过诊室查询诊区
     * @param parm TParm
     * @return TParm
     */
    public TParm SELECT_REG_SCHDAY_CLINICROOM(TParm parm) {
        TParm result = new TParm();
        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
        parm.setData("ADM_DATE", adm_Date);
        result = query("select_reg_clinicroom_schday", parm);
        return result;
    }
    /**
     * 通过诊室查询对应诊区
     * @param clinicRoom String
     * @return TParm
     */
    public TParm selclinicAreaByRoom(String clinicRoom) {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("CLINICROOM_NO", clinicRoom);
        result = query("selclinicAreaByRoom", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 普通诊出诊医师维护
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm INSERT_REG_SCHDAY_FOR_SCHDAY_DR(TParm parm,
                                                 TConnection connection) {
        TParm result = new TParm();
//         String admDate = TCM_Transform.getString(TCM_Transform.getTimestamp(parm.getData("ADM_DATE"),"yyyyMMdd"));
//         String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
//         parm.setData("ADM_DATE", admDate);

        result = update("insert_reg_schday_for_schday_dr", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * 通过号别筛选当天医师
     * @param parm TParm
     * @return TParm
     */
    public TParm selDrByClinicType(TParm parm) {
        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
        parm.setData("ADM_DATE", adm_Date);
        TParm result = query("selDrByClinicType", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 得到中西医标记
     * @param regionCode String 区域
     * @param admType String 门急别
     * @param admDate String 就诊日期
     * @param sessionCode String 时段
     * @param clinicroomNo String 诊室
     * @return string
     */
    public String selWestMediFlg(String regionCode, String admType,
                                  String admDate, String sessionCode,
                                  String clinicroomNo) {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("REGION_CODE",regionCode);
        parm.setData("ADM_TYPE",admType);
        parm.setData("ADM_DATE",admDate);
        parm.setData("SESSION_CODE",sessionCode);
        parm.setData("CLINICROOM_NO",clinicroomNo);
        result = query("selWestMediFlg",parm);
        if(result.getErrCode()<0){
            err(result.getErrName()+ " " +result.getErrText());
        }
        String westMediFlg = result.getValue("",0);
        return westMediFlg;
    }
    /**
     * 得到临时诊注记
     * @param regionCode String 区域
     * @param admType String 门急别
     * @param admDate String 就诊日期
     * @param sessionCode String 时段
     * @param clinicroomNo String 诊室
     * @return boolean
     */
    public boolean selClinicTmpFlg(String regionCode, String admType,
                                   String admDate, String sessionCode, //CLINICTMP_FLG
                                   String clinicroomNo) {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("REGION_CODE", regionCode);
        parm.setData("ADM_TYPE", admType);
        parm.setData("ADM_DATE", admDate);
        parm.setData("SESSION_CODE", sessionCode);
        parm.setData("CLINICROOM_NO", clinicroomNo);
        result = query("selClinicTmpFlg", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return false;
        }
        return true;

    }
    /**
     * 代诊
     * ====zhangp 20120621
     * @param parm
     * @param connection
     * @return TParm
     */
    public TParm updateRegSchDay(TParm parm,TConnection connection){
        Timestamp admDate = TCM_Transform.getTimestamp(parm.getData("ADM_DATE"));
        String adm_Date = StringTool.getString(admDate, "yyyyMMdd");
    	String sql =
    		" UPDATE REG_SCHDAY" +
    		" SET REALDEPT_CODE = '" + parm.getValue("REALDEPT_CODE") + "'," +
    		" REALDR_CODE = '" + parm.getValue("REALDR_CODE") + "'" +
    		" WHERE REGION_CODE = '" + parm.getValue("REGION_CODE") + "'" +
    		" AND ADM_TYPE = '" + parm.getValue("ADM_TYPE") + "'" +
    		" AND ADM_DATE = '" + adm_Date + "'" +
    		" AND SESSION_CODE = '" + parm.getValue("SESSION_CODE") + "'" +
    		" AND CLINICROOM_NO = '" + parm.getValue("CLINICROOM_NO") + "'";
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql, connection));
    	if(result.getErrCode()<0){
    		err(result.getErrName() + " " + result.getErrText());
    		connection.rollback();
    		return result;
    	}
    	sql = 
    		" UPDATE REG_PATADM" +
    		" SET REALDR_CODE = '" + parm.getValue("REALDR_CODE") + "'," +
    		" REALDEPT_CODE = '" + parm.getValue("REALDEPT_CODE") + "'" +
    		" WHERE REGION_CODE = '" + parm.getValue("REGION_CODE") + "'" +
    		" AND ADM_TYPE = '" + parm.getValue("ADM_TYPE") + "'" +
    		" AND ADM_DATE = TO_DATE ('" + adm_Date + "', 'YYYYMMDD')" +
    		" AND SESSION_CODE = '" + parm.getValue("SESSION_CODE") + "'" +
    		" AND CLINICROOM_NO = '" + parm.getValue("CLINICROOM_NO") + "'";
    	result = new TParm(TJDODBTool.getInstance().update(sql, connection));
    	if(result.getErrCode()<0){
    		err(result.getErrName() + " " + result.getErrText());
    		connection.rollback();
    		return result;
    	}
    	return result;
    }
    
    /**
     * 查询班表是否存在
     * @param parm
     * @return
     */
    public int checkSchday(TParm parm){

    	String sql = "SELECT COUNT(CLINICROOM_NO) COUNT FROM REG_SCHDAY" +
    			" WHERE ADM_TYPE = 'O'" +
    			" AND REALDEPT_CODE = '"+parm.getValue("REALDEPT_CODE")+"'" +
    			" AND ADM_DATE = '"+parm.getValue("ADM_DATE").toString().replace("/", "").replace("-", "").substring(0, 8)+"'" ;
    	
    	if(!"".equals(parm.getValue("SESSION_CODE"))){
    		sql = sql + " AND SESSION_CODE = '"+parm.getValue("SESSION_CODE")+"'" ;
    	}
    	if(!"".equals(parm.getValue("REALDR_CODE"))){
    		sql = sql + " AND REALDR_CODE = '"+parm.getValue("REALDR_CODE")+"'" ;
    	}else{
    		sql = sql + " AND REALDR_CODE IS NULL" ;
    	}
    	if(!"".equals(parm.getValue("CLINICTYPE_CODE"))){
    		sql = sql + " AND CLINICTYPE_CODE = '"+parm.getValue("CLINICTYPE_CODE")+"'" ;
    	}
//    	System.out.println("sql=="+sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	return result.getInt("COUNT", 0);
    }
    
    public TParm getVipSchday(TParm parm){
    	String sql="";
    	if(!("".equals(parm.getValue("REALDR_CODE")) || null == parm.getValue("REALDR_CODE"))){
    		sql = "SELECT   B.ADM_DATE, B.CLINICTYPE_CODE, D.CLINICTYPE_DESC, A.SESSION_CODE,"
				+ " E.SESSION_DESC, B.REALDEPT_CODE, F.DEPT_CHN_DESC REALDEPT_DESC,"
				+ " A.CLINICROOM_NO, C.CLINICROOM_DESC, B.REALDR_CODE,"
				+ " I.CLINICAREA_CODE ,I.CLINIC_DESC,"
				+ " G.USER_NAME REALDR_DESC, A.QUE_NO, A.START_TIME, A.QUE_STATUS,"
				+ "  A.ADD_FLG, H.OWN_PRICE, '' I_TIME,B.QUEGROUP_CODE,J.QUEGROUP_DESC,B.MAX_QUE" 
				+ " ,B.CLINICTYPE_CODE, K.CLINICTYPE_DESC"
				+ " FROM REG_CLINICQUE A,"
				+ " REG_SCHDAY B,"
				+ " REG_CLINICROOM C,"
				+ " REG_CLINICTYPE D,"
				+ " REG_SESSION E,"
				+ " SYS_DEPT F,"
				+ " SYS_OPERATOR G,"
				+ " REG_CLINICAREA I,"
				+ " REG_QUEGROUP J,"
				+ " (SELECT A.CLINICTYPE_CODE, SUM(B.OWN_PRICE) OWN_PRICE FROM REG_CLINICTYPE_FEE A,SYS_FEE B"
				+ " WHERE A.ORDER_CODE=B.ORDER_CODE"
				+ " GROUP BY A.CLINICTYPE_CODE) H," 
				+ " REG_CLINICTYPE K"
				+ " WHERE A.ADM_DATE = B.ADM_DATE"
				+ " AND A.ADM_TYPE = B.ADM_TYPE"
				+ " AND A.SESSION_CODE = B.SESSION_CODE"
				+ " AND A.CLINICROOM_NO = B.CLINICROOM_NO"
				+ " AND B.CLINICROOM_NO = C.CLINICROOM_NO"
				+ " AND B.CLINICTYPE_CODE = D.CLINICTYPE_CODE"
				+ " AND B.SESSION_CODE = E.SESSION_CODE"
				+ " AND B.REALDEPT_CODE = F.DEPT_CODE"
				+ " AND B.REALDR_CODE = G.USER_ID"
				+ " AND B.CLINICTYPE_CODE = H.CLINICTYPE_CODE"
				+ " AND B.REG_CLINICAREA = I.CLINICAREA_CODE"
				+ " AND B.QUEGROUP_CODE = J.QUEGROUP_CODE" 
				+ " AND B.CLINICTYPE_CODE=K.CLINICTYPE_CODE"
				+ " AND B.ADM_DATE = '"
				+ parm.getValue("ADM_DATE").replace("/", "").replace("-", "")
						.substring(0, 8)
				+ "'"
				+ " AND B.REALDEPT_CODE = '"
				+ parm.getValue("REALDEPT_CODE")
				+ "'"
				+ " AND B.REALDR_CODE = '"
				+ parm.getValue("REALDR_CODE")
				+ "'"
				+ " AND B.CLINICROOM_NO = '"
				+ parm.getValue("CLINICROOM_NO")
				+ "'"
				+ " AND B.SESSION_CODE = '"
				+ parm.getValue("SESSION_CODE")
				+ "'"
				+ " ORDER BY B.ADM_DATE, B.REALDEPT_CODE, B.REALDR_CODE, A.START_TIME";
    	}else{
    		sql = "SELECT   B.ADM_DATE, B.CLINICTYPE_CODE, D.CLINICTYPE_DESC, A.SESSION_CODE,"
				+ " E.SESSION_DESC, B.REALDEPT_CODE, F.DEPT_CHN_DESC REALDEPT_DESC,"
				+ " A.CLINICROOM_NO, C.CLINICROOM_DESC, B.REALDR_CODE,"
				+ " I.CLINICAREA_CODE ,I.CLINIC_DESC,"
				+ " '' REALDR_DESC, A.QUE_NO, A.START_TIME, A.QUE_STATUS,"
				+ "  A.ADD_FLG, H.OWN_PRICE, '' I_TIME,B.QUEGROUP_CODE,J.QUEGROUP_DESC,B.MAX_QUE"
				+ " ,B.CLINICTYPE_CODE, K.CLINICTYPE_DESC"
				+ " FROM REG_CLINICQUE A,"
				+ " REG_SCHDAY B,"
				+ " REG_CLINICROOM C,"
				+ " REG_CLINICTYPE D,"
				+ " REG_SESSION E,"
				+ " SYS_DEPT F,"
				+ " REG_CLINICAREA I,"
				+ " REG_QUEGROUP J,"
				+ " (SELECT A.CLINICTYPE_CODE, SUM(B.OWN_PRICE) OWN_PRICE FROM REG_CLINICTYPE_FEE A,SYS_FEE B"
				+ " WHERE A.ORDER_CODE=B.ORDER_CODE"
				+ " GROUP BY A.CLINICTYPE_CODE) H,"
				+ " REG_CLINICTYPE K"
				+ " WHERE A.ADM_DATE = B.ADM_DATE"
				+ " AND A.ADM_TYPE = B.ADM_TYPE"
				+ " AND A.SESSION_CODE = B.SESSION_CODE"
				+ " AND A.CLINICROOM_NO = B.CLINICROOM_NO"
				+ " AND B.CLINICROOM_NO = C.CLINICROOM_NO"
				+ " AND B.CLINICTYPE_CODE = D.CLINICTYPE_CODE"
				+ " AND B.SESSION_CODE = E.SESSION_CODE"
				+ " AND B.REALDEPT_CODE = F.DEPT_CODE"
				+ " AND B.CLINICTYPE_CODE = H.CLINICTYPE_CODE"
				+ " AND B.REG_CLINICAREA = I.CLINICAREA_CODE"
				+ " AND B.QUEGROUP_CODE = J.QUEGROUP_CODE"
				+ " AND B.CLINICTYPE_CODE=K.CLINICTYPE_CODE"
				+ " AND B.ADM_DATE = '"
				+ parm.getValue("ADM_DATE").replace("/", "").replace("-", "")
						.substring(0, 8)
				+ "'"
				+ " AND B.REALDEPT_CODE = '"
				+ parm.getValue("REALDEPT_CODE")
				+ "'"
				+ " AND B.CLINICROOM_NO = '"
				+ parm.getValue("CLINICROOM_NO")
				+ "'"
				+ " AND B.SESSION_CODE = '"
				+ parm.getValue("SESSION_CODE")
				+ "'"
				+ " ORDER BY B.ADM_DATE, B.REALDEPT_CODE, A.START_TIME";
    	}
		
		TParm parmVip = new TParm(TJDODBTool.getInstance().select(sql));

		sql = "SELECT START_TIME FROM REG_QUEMETHOD WHERE QUEGROUP_CODE = '"
				+ parmVip.getValue("QUEGROUP_CODE", 0)
				+ "' ORDER BY START_TIME";
		TParm parmTime = new TParm(TJDODBTool.getInstance().select(sql));
		SimpleDateFormat sdf = new SimpleDateFormat("mmss");
		String t1 = parmTime.getValue("START_TIME", 0);
		String t2 = parmTime.getValue("START_TIME", 1);
		long time = 0;
		try {
			time = (sdf.parse(t2).getTime() - sdf.parse(t1).getTime()) / 1000;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		REGQueMethodTool rq = new REGQueMethodTool();
		for (int i = 0; i < parmVip.getCount(); i++) {
			parmVip.setData("I_TIME", i, time);
			String sTime = parmVip.getValue("START_TIME", i);
			String eTime = rq.addTime(sTime, time + "");
			parmVip.setData("START_TIME", i, sTime.subSequence(0, 2) + ":"
					+ sTime.subSequence(2, 4) + "-" + eTime.subSequence(0, 2)
					+ ":" + eTime.subSequence(2, 4));

		}

		return parmVip;
    }
    
    public TParm updateSchdayCrmsyncflg(TParm parm){
    	String sql="UPDATE REG_SCHDAY SET CRM_SYNC_FLG = 'Y'" +
			" WHERE CRM_SYNC_FLG = 'N' " +
			" AND ADM_DATE = '"+parm.getValue("ADM_DATE").replace("/", "").replace("-", "").substring(0, 8)+"'" +
   		" AND REALDEPT_CODE = '"+parm.getValue("REALDEPT_CODE")+"'" ;
   		if(!("".equals(parm.getValue("REALDR_CODE")) || null == parm.getValue("REALDR_CODE"))){
   			sql = sql + " AND REALDR_CODE = '"+parm.getValue("REALDR_CODE")+"'" ;
   		}
   		
   		sql =  sql + " AND CLINICROOM_NO = '"+parm.getValue("CLINICROOM_NO")+"'" +
   		" AND SESSION_CODE = '"+parm.getValue("SESSION_CODE")+"'";
	   TParm result = new TParm(TJDODBTool.getInstance().update(sql));
	   return result; 
    }
    
    public String getClinicroomNo(String admDate,String sessionCode,String admType){
    	String sql = "  SELECT CLINICROOM_NO, CLINICROOM_DESC" +
    			" FROM REG_CLINICROOM" +
    			" WHERE ADM_TYPE = '"+admType+"'" +
    			" AND CLINICROOM_NO NOT IN" +
    			" (SELECT DISTINCT CLINICROOM_NO" +
    			" FROM REG_SCHDAY" +
    			" WHERE SESSION_CODE = '"+sessionCode+"'" +
    			" AND ADM_TYPE = '"+admType+"'" +
    			" AND ADM_DATE = '"+admDate+"')" +
    			" ORDER BY CLINICROOM_NO";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()>0){
    		return parm.getValue("CLINICROOM_NO", 0); 
    	}else{
    		return "";
    	}
    }
    
    

}
