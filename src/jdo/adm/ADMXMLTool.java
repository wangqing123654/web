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
     * ʵ��
     */
    public static ADMXMLTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SchWeekTool
     */
    public static ADMXMLTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMXMLTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ADMXMLTool() {
        setModuleName("");
        onInit();
        data = TIOM_Database.getLocalTable("SYS_DICTIONARY");
    }
    /**
     * ͨ��CASE_NO������Ϣ�����XML�ļ�
     * @param CASE_NO String
     * @return TParm
     */
    public TParm creatXMLFile(String CASE_NO){
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        //סԺ��Ϣ
        TParm ADM_INP = ADMInpTool.getInstance().selectForXML(parm);
        if (ADM_INP.getErrCode() < 0) {
            err("ERR:" + ADM_INP.getErrCode() + ADM_INP.getErrText() +
                ADM_INP.getErrName());
            return ADM_INP;
        }
        //��ѯ������Ϣ
        TParm OpParm = new TParm();
        OpParm.setData("CASE_NO",CASE_NO);
        OpParm.setData("CANCEL_FLG","N");
        TParm OP_DATA = OPEOpBookTool.getInstance().selectOpBook(OpParm);
        if (OP_DATA.getErrCode() < 0) {
            err("ERR:" + OP_DATA.getErrCode() + OP_DATA.getErrText() +
                OP_DATA.getErrName());
            return OP_DATA;
        }
        //��ѯ��Ժ�����
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
        //��ѯ����ת����Ϣ
        //ת����Ϣ
        TParm inParm = new TParm();
        inParm.setData("CASE_NO",CASE_NO);
        inParm.setData("PSF_KIND","INDP");
        TParm IN = ADMChgTool.getInstance().queryChgForMRO(inParm);
        TParm outParm = new TParm();
        outParm.setData("CASE_NO",CASE_NO);
        outParm.setData("PSF_KIND","OUDP");
        TParm OUT = ADMChgTool.getInstance().queryChgForMRO(outParm);
        TParm XMLParm = new TParm();
        XMLParm.setData("StationCode",ADM_INP.getValue("STATION_CODE",0));//����CODE
        XMLParm.setData("BedNo",ADM_INP.getValue("BED_NO",0));//����
        XMLParm.setData("RoomNo",ADM_INP.getValue("ROOM_CODE",0));//������
        XMLParm.setData("MrNo",ADM_INP.getValue("MR_NO",0));//������
        XMLParm.setData("PatName",ADM_INP.getValue("PAT_NAME",0));//����
        XMLParm.setData("Sex", ADM_INP.getValue("SEX", 0)); //�Ա�
        XMLParm.setData("Age",
                        ( (String[]) StringTool.CountAgeByTimestamp(ADM_INP.
            getTimestamp("BIRTH_DATE", 0), ADM_INP.getTimestamp("IN_DATE", 0)))[0]);//����
        XMLParm.setData("InDate", StringTool.getString(ADM_INP.getTimestamp("IN_DATE", 0),"yyyy-MM-dd HH:mm:ss"));//��Ժ����
        XMLParm.setData("DirectorDr",ADM_INP.getValue("DIRECTOR_DR_CODE",0));//����ҽ��
        XMLParm.setData("ChiefNs",ADM_INP.getValue("VS_NURSE_CODE",0));//��ʿ��
        XMLParm.setData("AttendDr",ADM_INP.getValue("ATTEND_DR_CODE",0));//����ҽ��
        XMLParm.setData("OpDate",StringTool.getString(OP_DATA.getTimestamp("OP_DATE",0),"yyyy-MM-dd HH:mm:ss"));//��������
        XMLParm.setData("NursingClass",getDesc("ADM_NURSING_CLASS",ADM_INP.getValue("NURSING_CLASS",0)));//����ȼ�
        XMLParm.setData("DieCondition",getDesc("SYS_DIE_CONDITION",ADM_INP.getValue("DIE_CONDITION",0)));//��ʳ���
        XMLParm.setData("ChargeClass",ADM_INP.getValue("CTZ_DESC",0));//�������
        XMLParm.setData("illState",getDesc("ADM_PATIENT_STATUS",ADM_INP.getValue("PATIENT_STATUS",0)));//����
        XMLParm.setData("Cared",ADM_INP.getInt("CARE_NUM",0)>0?"��":"��");//�㻤
        XMLParm.setData("Toilet",getDesc("ADM_TOILET",ADM_INP.getValue("TOILET",0)));//��޷�ʽ
        XMLParm.setData("Measure",getDesc("ADM_MEASURE",ADM_INP.getValue("IO_MEASURE",0)));//������ʽ
        XMLParm.setData("IsolationId",getDesc("ADM_ISOLATION",ADM_INP.getValue("ISOLATION",0)));//�������
        XMLParm.setData("Allergies",ADM_INP.getValue("ALLERGY",0).equals("Y")?"��":"��");//�������
        XMLParm.setData("Diag",DIAG.getValue("ICD_CHN_DESC",0));//��Ժ�����
        XMLParm.setData("IsAdd",ADM_INP.getValue("ISADD",0));//�Ƿ�Ӵ�
        XMLParm.setData("OutDate",StringTool.getString(ADM_INP.getTimestamp("DS_DATE",0),"yyyy-MM-dd HH:mm:ss"));//��Ժ����
        XMLParm.setData("TurnIn",StringTool.getString(IN.getTimestamp("CHG_DATE",0),"yyyy-MM-dd HH:mm:ss"));//ת������
        XMLParm.setData("TurnOut",StringTool.getString(OUT.getTimestamp("CHG_DATE",0),"yyyy-MM-dd HH:mm:ss"));//ת������
        if(!this.creatXML(XMLParm)){
            result.setErr(-1,"����XMLʧ��");
            return result;
        }
        return result;
    }
    /**
     * ������������XML�ļ�
     * @param parm TParm
     * @return boolean
     */
    public boolean creatXML(TParm parm){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
        stringBuffer.append("<CallSysInterface>");
        stringBuffer.append("<InPatInfo>");
        //����CODE
        stringBuffer.append("<StationCode>");
        stringBuffer.append(parm.getValue("StationCode"));
        stringBuffer.append("</StationCode>");
        //��λ��
        stringBuffer.append("<BedNo>");
        stringBuffer.append(parm.getValue("BedNo"));
        stringBuffer.append("</BedNo>");
        //������
        stringBuffer.append("<RoomNo>");
        stringBuffer.append(parm.getValue("RoomNo"));
        stringBuffer.append("</RoomNo>");
        //������
        stringBuffer.append("<MrNo>");
        stringBuffer.append(parm.getValue("MrNo"));
        stringBuffer.append("</MrNo>");
        //��������
        stringBuffer.append("<PatName>");
        stringBuffer.append(parm.getValue("PatName"));
        stringBuffer.append("</PatName>");
        //�Ա�
        stringBuffer.append("<Sex>");
        stringBuffer.append(parm.getValue("Sex"));
        stringBuffer.append("</Sex>");
        //����
        stringBuffer.append("<Age>");
        stringBuffer.append(parm.getValue("Age"));
        stringBuffer.append("</Age>");
        //��Ժ����
        stringBuffer.append("<InDate>");
        stringBuffer.append(parm.getValue("InDate"));
        stringBuffer.append("</InDate>");
        //����ҽʦ
        stringBuffer.append("<DirectorDr>");
        stringBuffer.append(parm.getValue("DirectorDr"));
        stringBuffer.append("</DirectorDr>");
        //��ʿ��
        stringBuffer.append("<ChiefNs>");
        stringBuffer.append(parm.getValue("ChiefNs"));
        stringBuffer.append("</ChiefNs>");
        //����ҽ��
        stringBuffer.append("<AttendDr>");
        stringBuffer.append(parm.getValue("AttendDr"));
        stringBuffer.append("</AttendDr>");
        //��������
        stringBuffer.append("<OpDate>");
        stringBuffer.append(parm.getValue("OpDate"));
        stringBuffer.append("</OpDate>");
        //����ȼ�
        stringBuffer.append("<NursingClass>");
        stringBuffer.append(parm.getValue("NursingClass"));
        stringBuffer.append("</NursingClass>");
        //��ʳ���
        stringBuffer.append("<DieCondition>");
        stringBuffer.append(parm.getValue("DieCondition"));
        stringBuffer.append("</DieCondition>");
        //�������
        stringBuffer.append("<ChargeClass>");
        stringBuffer.append(parm.getValue("ChargeClass"));
        stringBuffer.append("</ChargeClass>");
        //����
        stringBuffer.append("<illState>");
        stringBuffer.append(parm.getValue("illState"));
        stringBuffer.append("</illState>");
        //�㻤
        stringBuffer.append("<Cared>");
        stringBuffer.append(parm.getValue("Cared"));
        stringBuffer.append("</Cared>");
        //��޷�ʽ
        stringBuffer.append("<Toilet>");
        stringBuffer.append(parm.getValue("Toilet"));
        stringBuffer.append("</Toilet>");
        //������ʽ
        stringBuffer.append("<Measure>");
        stringBuffer.append(parm.getValue("Measure"));
        stringBuffer.append("</Measure>");
        //�������
        stringBuffer.append("<IsolationId>");
        stringBuffer.append(parm.getValue("IsolationId"));
        stringBuffer.append("</IsolationId>");
        //�������
        stringBuffer.append("<Allergies>");
        stringBuffer.append(parm.getValue("Allergies"));
        stringBuffer.append("</Allergies>");
        //��Ժ�����
        stringBuffer.append("<Diag>");
        stringBuffer.append(parm.getValue("Diag"));
        stringBuffer.append("</Diag>");
        //�Ƿ�Ӵ�
        stringBuffer.append("<IsAdd>");
        stringBuffer.append(parm.getValue("IsAdd"));
        stringBuffer.append("</IsAdd>");
        //��Ժ����
        stringBuffer.append("<OutDate>");
        stringBuffer.append(parm.getValue("OutDate"));
        stringBuffer.append("</OutDate>");
        //ת������
        stringBuffer.append("<TurnIn>");
        stringBuffer.append(parm.getValue("TurnIn"));
        stringBuffer.append("</TurnIn>");
        //ת������
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
     * �һ����ֵ�����
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
