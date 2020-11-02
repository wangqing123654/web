package jdo.adm;

import com.dongyang.data.TSocket;
import com.dongyang.config.TConfig;
import jdo.sys.SYSBedTool;
import jdo.sys.SystemTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.data.TParm;
import jdo.sys.PatTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import java.util.Vector;
import jdo.ope.OPEOpBookTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ADMXMLTool  extends TJDOTool {
    private TDataStore data;
    /**
     * 实例
     */
    public static ADMXMLTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static ADMXMLTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMXMLTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ADMXMLTool() {
        setModuleName("");
        onInit();
        data = TIOM_Database.getLocalTable("SYS_DICTIONARY");
    }
    /**
     * 通过CASE_NO生成信息看板的XML文件
     * @param CASE_NO String
     * @return TParm
     */
    public TParm creatXMLFile(String CASE_NO){
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        //住院信息
        TParm ADM_INP = ADMInpTool.getInstance().selectForXML(parm);
        if (ADM_INP.getErrCode() < 0) {
            err("ERR:" + ADM_INP.getErrCode() + ADM_INP.getErrText() +
                ADM_INP.getErrName());
            return ADM_INP;
        }
        //查询手术信息
        TParm OpParm = new TParm();
        OpParm.setData("CASE_NO",CASE_NO);
        OpParm.setData("CANCEL_FLG","N");
        TParm OP_DATA = OPEOpBookTool.getInstance().selectOpBook(OpParm);
        if (OP_DATA.getErrCode() < 0) {
            err("ERR:" + OP_DATA.getErrCode() + OP_DATA.getErrText() +
                OP_DATA.getErrName());
            return OP_DATA;
        }
        //查询入院主诊断
        TParm diagParm = new TParm();
        diagParm.setData("CASE_NO",CASE_NO);
        diagParm.setData("IO_TYPE","I");
        diagParm.setData("MAINDIAG_FLG","Y");
        TParm DIAG = ADMDiagTool.getInstance().queryData(diagParm);
        if (DIAG.getErrCode() < 0) {
            err("ERR:" + DIAG.getErrCode() + DIAG.getErrText() +
                DIAG.getErrName());
            return DIAG;
        }
        //查询病患转科信息
        //转入信息
        TParm inParm = new TParm();
        inParm.setData("CASE_NO",CASE_NO);
        inParm.setData("PSF_KIND","INDP");
        TParm IN = ADMChgTool.getInstance().queryChgForMRO(inParm);
        TParm outParm = new TParm();
        outParm.setData("CASE_NO",CASE_NO);
        outParm.setData("PSF_KIND","OUDP");
        TParm OUT = ADMChgTool.getInstance().queryChgForMRO(outParm);
        TParm XMLParm = new TParm();
        XMLParm.setData("StationCode",ADM_INP.getValue("STATION_CODE",0));//病区CODE
        XMLParm.setData("BedNo",ADM_INP.getValue("BED_NO",0));//床号
        XMLParm.setData("RoomNo",ADM_INP.getValue("ROOM_CODE",0));//病房号
        XMLParm.setData("MrNo",ADM_INP.getValue("MR_NO",0));//病案号
        XMLParm.setData("PatName",ADM_INP.getValue("PAT_NAME",0));//姓名
        XMLParm.setData("Sex", ADM_INP.getValue("SEX", 0)); //性别
        XMLParm.setData("Age",
                        ( (String[]) StringTool.CountAgeByTimestamp(ADM_INP.
            getTimestamp("BIRTH_DATE", 0), ADM_INP.getTimestamp("IN_DATE", 0)))[0]);//年龄
        XMLParm.setData("InDate", StringTool.getString(ADM_INP.getTimestamp("IN_DATE", 0),"yyyy-MM-dd HH:mm:ss"));//入院日期
        XMLParm.setData("DirectorDr",ADM_INP.getValue("DIRECTOR_DR_CODE",0));//主任医生
        XMLParm.setData("ChiefNs",ADM_INP.getValue("VS_NURSE_CODE",0));//护士长
        XMLParm.setData("AttendDr",ADM_INP.getValue("ATTEND_DR_CODE",0));//主管医生
        XMLParm.setData("OpDate",StringTool.getString(OP_DATA.getTimestamp("OP_DATE",0),"yyyy-MM-dd HH:mm:ss"));//手术日期
        XMLParm.setData("NursingClass",getDesc("ADM_NURSING_CLASS",ADM_INP.getValue("NURSING_CLASS",0)));//护理等级
        XMLParm.setData("DieCondition",getDesc("SYS_DIE_CONDITION",ADM_INP.getValue("DIE_CONDITION",0)));//饮食情况
        XMLParm.setData("ChargeClass",ADM_INP.getValue("CTZ_DESC",0));//费用类别
        XMLParm.setData("illState",getDesc("ADM_PATIENT_STATUS",ADM_INP.getValue("PATIENT_STATUS",0)));//病情
        XMLParm.setData("Cared",ADM_INP.getInt("CARE_NUM",0)>0?"是":"否");//陪护
        XMLParm.setData("Toilet",getDesc("ADM_TOILET",ADM_INP.getValue("TOILET",0)));//入厕方式
        XMLParm.setData("Measure",getDesc("ADM_MEASURE",ADM_INP.getValue("IO_MEASURE",0)));//计量方式
        XMLParm.setData("IsolationId",getDesc("ADM_ISOLATION",ADM_INP.getValue("ISOLATION",0)));//隔离情况
        XMLParm.setData("Allergies",ADM_INP.getValue("ALLERGY",0).equals("Y")?"是":"否");//过敏情况
        XMLParm.setData("Diag",DIAG.getValue("ICD_CHN_DESC",0));//入院主诊断
        XMLParm.setData("IsAdd",ADM_INP.getValue("ISADD",0));//是否加床
        XMLParm.setData("OutDate",StringTool.getString(ADM_INP.getTimestamp("DS_DATE",0),"yyyy-MM-dd HH:mm:ss"));//出院日期
        XMLParm.setData("TurnIn",StringTool.getString(IN.getTimestamp("CHG_DATE",0),"yyyy-MM-dd HH:mm:ss"));//转入日期
        XMLParm.setData("TurnOut",StringTool.getString(OUT.getTimestamp("CHG_DATE",0),"yyyy-MM-dd HH:mm:ss"));//转出日期
        if(!this.creatXML(XMLParm)){
            result.setErr(-1,"生成XML失败");
            return result;
        }
        return result;
    }
    /**
     * 根据数据生成XML文件
     * @param parm TParm
     * @return boolean
     */
    public boolean creatXML(TParm parm){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
        stringBuffer.append("<CallSysInterface>");
        stringBuffer.append("<InPatInfo>");
        //病区CODE
        stringBuffer.append("<StationCode>");
        stringBuffer.append(parm.getValue("StationCode"));
        stringBuffer.append("</StationCode>");
        //床位号
        stringBuffer.append("<BedNo>");
        stringBuffer.append(parm.getValue("BedNo"));
        stringBuffer.append("</BedNo>");
        //病房号
        stringBuffer.append("<RoomNo>");
        stringBuffer.append(parm.getValue("RoomNo"));
        stringBuffer.append("</RoomNo>");
        //病案号
        stringBuffer.append("<MrNo>");
        stringBuffer.append(parm.getValue("MrNo"));
        stringBuffer.append("</MrNo>");
        //病患姓名
        stringBuffer.append("<PatName>");
        stringBuffer.append(parm.getValue("PatName"));
        stringBuffer.append("</PatName>");
        //性别
        stringBuffer.append("<Sex>");
        stringBuffer.append(parm.getValue("Sex"));
        stringBuffer.append("</Sex>");
        //年龄
        stringBuffer.append("<Age>");
        stringBuffer.append(parm.getValue("Age"));
        stringBuffer.append("</Age>");
        //入院日期
        stringBuffer.append("<InDate>");
        stringBuffer.append(parm.getValue("InDate"));
        stringBuffer.append("</InDate>");
        //主任医师
        stringBuffer.append("<DirectorDr>");
        stringBuffer.append(parm.getValue("DirectorDr"));
        stringBuffer.append("</DirectorDr>");
        //护士长
        stringBuffer.append("<ChiefNs>");
        stringBuffer.append(parm.getValue("ChiefNs"));
        stringBuffer.append("</ChiefNs>");
        //主管医生
        stringBuffer.append("<AttendDr>");
        stringBuffer.append(parm.getValue("AttendDr"));
        stringBuffer.append("</AttendDr>");
        //手术日期
        stringBuffer.append("<OpDate>");
        stringBuffer.append(parm.getValue("OpDate"));
        stringBuffer.append("</OpDate>");
        //护理等级
        stringBuffer.append("<NursingClass>");
        stringBuffer.append(parm.getValue("NursingClass"));
        stringBuffer.append("</NursingClass>");
        //饮食情况
        stringBuffer.append("<DieCondition>");
        stringBuffer.append(parm.getValue("DieCondition"));
        stringBuffer.append("</DieCondition>");
        //费用类别
        stringBuffer.append("<ChargeClass>");
        stringBuffer.append(parm.getValue("ChargeClass"));
        stringBuffer.append("</ChargeClass>");
        //病情
        stringBuffer.append("<illState>");
        stringBuffer.append(parm.getValue("illState"));
        stringBuffer.append("</illState>");
        //陪护
        stringBuffer.append("<Cared>");
        stringBuffer.append(parm.getValue("Cared"));
        stringBuffer.append("</Cared>");
        //入厕方式
        stringBuffer.append("<Toilet>");
        stringBuffer.append(parm.getValue("Toilet"));
        stringBuffer.append("</Toilet>");
        //计量方式
        stringBuffer.append("<Measure>");
        stringBuffer.append(parm.getValue("Measure"));
        stringBuffer.append("</Measure>");
        //隔离情况
        stringBuffer.append("<IsolationId>");
        stringBuffer.append(parm.getValue("IsolationId"));
        stringBuffer.append("</IsolationId>");
        //过敏情况
        stringBuffer.append("<Allergies>");
        stringBuffer.append(parm.getValue("Allergies"));
        stringBuffer.append("</Allergies>");
        //入院主诊断
        stringBuffer.append("<Diag>");
        stringBuffer.append(parm.getValue("Diag"));
        stringBuffer.append("</Diag>");
        //是否加床
        stringBuffer.append("<IsAdd>");
        stringBuffer.append(parm.getValue("IsAdd"));
        stringBuffer.append("</IsAdd>");
        //出院日期
        stringBuffer.append("<OutDate>");
        stringBuffer.append(parm.getValue("OutDate"));
        stringBuffer.append("</OutDate>");
        //转入日期
        stringBuffer.append("<TurnIn>");
        stringBuffer.append(parm.getValue("TurnIn"));
        stringBuffer.append("</TurnIn>");
        //转出日期
        stringBuffer.append("<TurnOut>");
        stringBuffer.append(parm.getValue("TurnOut"));
        stringBuffer.append("</TurnOut>");
        stringBuffer.append("</InPatInfo>");
        stringBuffer.append("</CallSysInterface>");
//        System.out.println("stringBuffer:"+stringBuffer);
        String path = TConfig.getSystemValue("MONITOR.PATH");
//        System.out.println("path:"+path);
        String fileServerIP = TConfig.getSystemValue("MONITOR.SERVER");
//        System.out.println("fileServerIP:"+fileServerIP);
        String port = TConfig.getSystemValue("MONITOR.PORT");
//        System.out.println("port:"+port);
        Timestamp timestamp = SystemTool.getInstance().getDate();
        String timestampStr = StringTool.getString(timestamp,"yyyyMMddHHmmss");
        TSocket socket = new TSocket(fileServerIP,Integer.parseInt(port));
//        System.out.println("pp:"+path + parm.getValue("MrNo") + "_" + timestampStr + ".xml");
        return TIOM_FileServer.writeFile(socket,path + parm.getValue("StationCode")+"_"+parm.getValue("RoomNo")+"_"+parm.getValue("BedNo") + "_" + timestampStr + ".xml",stringBuffer.toString().getBytes());
    }

    /**
     * 兑换大字典中文
     * @param group_id String
     * @param id String
     * @return String
     */
    private String getDesc(String group_id,String id){
        if (data == null)
                return id;
            String filter = " GROUP_ID='"+group_id+"'";
            data.setFilter(filter);
            data.filter();
            TParm parm = data.getBuffer(data.PRIMARY);
            Vector v = (Vector) parm.getData("ID");
            Vector d = (Vector) parm.getData("CHN_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (id.equals(v.get(i)))
                    return "" + d.get(i);
            }
            return id;
    }
}
