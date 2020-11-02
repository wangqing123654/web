package com.javahis.ui.inw;

import com.dongyang.control.TControl;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TTextFormat;
import com.dongyang.data.TParm;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import java.util.Vector;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.sta.STADeptListTool;
import jdo.ibs.IBSOrdermTool;
/**
 * <p>Title: </p>
 *
 * <p>Description:סԺ��ʿվ����ά��������</p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class INWStationMaintainControl
    extends TControl {

    //����
    private TTable masterTbl;
    private TTable detailTbl;

    private TTextFormat DEPT;
    private TTextFormat STATION;

    private TTextFormat DATE;

    private TCheckBox CHECK1;
    private TCheckBox CHECK2;
    private TCheckBox CHECK3;
    private TCheckBox CHECK4;//add by wanglong 0304
    private TCheckBox AllSel;
    TParm masterTblParm = new TParm();
    private TNumberTextField totPerson;

    public INWStationMaintainControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        //������ĳ�ʼ��
        myInitControler();

        //���Ը�����Ա��ʼ������
//        System.out.println("=>" + Operator.getDept());
//        System.out.println("=>" + Operator.getName());
//        System.out.println("=>" + Operator.getID());
        initByOptUser();
    }


    /**
     * ��ʼ��
     * ���ݵ�½��Ա��(Ȩ��)��Ϣ��ʼ��
     * ���õ���½��Ա����Ϣ��
     */
    public void initByOptUser() {
        DEPT.setValue(Operator.getDept());
        STATION.setValue(Operator.getStation());
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        //�ܿز������в�����ѯ
        if(DEPT.getValue()==null){
            this.messageBox("��ѡ����ң�");
            return;
        }
        if(STATION.getValue()==null){
            this.messageBox("��ѡ������");
            return;
        }

        //�����ǰ������
        masterTbl.removeRowAll();
        detailTbl.removeRowAll();

        //��ʼ����ǰtable
        initDataStoreToTable();

    }

    /**
     * ��ʼ��table
     * ��ѯ�����ǣ� caseNo/����
     */
    /*public void initDataStoreToTable() {
        //���û�����--û��LAST_DSPN_DATE���޷���ѯ
        String matserColum = " STATION_CODE, BED_NO,IPD_NO,MR_NO,CASE_NO ";//CASE_NO
        String nowDate=StringTool.getString((Timestamp)DATE.getValue(),"yyyyMMddHHmmss");
        String EXEsql = " SELECT DISTINCT " + " (SELECT PAT_NAME FROM SYS_PATINFO WHERE SYS_PATINFO.MR_NO=ODI_ORDER.MR_NO) AS PAT_NAME, " +
            matserColum +
            " FROM ODI_ORDER WHERE " +
            (buildWhere().equals("") ? "" : (buildWhere() + " AND ")) +
            " RX_KIND = 'UD' AND CAT1_TYPE NOT IN ('LIS','RIS','PHA') AND DC_DR_CODE IS NULL " +
            " AND NS_CHECK_CODE IS NOT NULL "+
            " AND LAST_DSPN_DATE <= TO_DATE("+nowDate+",'YYYYMMDDHH24MISS')"+
            "ORDER BY MR_NO ";
//        System.out.println("����ά��SQL===>"+EXEsql);

        TParm parm=new TParm(TJDODBTool.getInstance().select(EXEsql));
        //�ֶ�����һ��
        for(int i=0;i<parm.getCount();i++){
            parm.setData("EXE",i,"N");
        }

        masterTbl.setParmValue(parm);
        totPerson.setValue(masterTbl.getRowCount());
    }*/

    /**
     * ��ʼ��table
     * ��ѯ�����ǣ� caseNo/����
     */
    public void initDataStoreToTable() {
        //���û�����--û��LAST_DSPN_DATE���޷���ѯ
        //=================pangben modify 20110511 start  �����������ֻ����ʾ�����������
        String region="";
        if(null!=Operator.getRegion()&&Operator.getRegion().length()>0)
            region=" AND A.REGION_CODE='"+Operator.getRegion()+"'";
        //=================pangben modify 20110511 stop
//        System.out.println("��������" + this.getValue("DATE"));
        String dateS = StringTool.getString(TypeTool.getTimestamp(getValue("DATE")),"yyyyMMdd");
        String EXEsql =
            " SELECT B.PAT_NAME,A.STATION_CODE, A.BED_NO,A.IPD_NO,A.MR_NO,A.CASE_NO " +
            "   FROM ADM_INP A,SYS_PATINFO B" +
            "  WHERE DS_DATE IS NULL" +
            (getValueString("DEPT").length() == 0 ? "" :
             "   AND DEPT_CODE = '" + getValueString("DEPT") + "'") +
            (getValueString("STATION").length() == 0 ? "" :
             "   AND STATION_CODE = '" + getValueString("STATION") + "'") +
            "    AND (CANCEL_FLG <> 'Y' OR CANCEL_FLG IS NULL)" +
            "    AND A.IN_DATE < TO_DATE('"+dateS+"','YYYYMMDD') "+
            "    AND A.BED_NO IS NOT NULL " +
            "    AND A.MR_NO = B.MR_NO" + region +
            "  ORDER BY A.MR_NO ";
//        System.out.println("����ά��SQL===>"+EXEsql);

       TParm parm = new TParm(TJDODBTool.getInstance().select(EXEsql));
       //=======pangben modify 20110511 start
       if (null == parm || parm.getCount("STATION_CODE") < 1) {
           this.messageBox("û��Ҫ���ҵ�����");
           return;
       }
        //=======pangben modify 20110511 stop
        //�ֶ�����һ��
        for(int i=0;i<parm.getCount();i++){
            parm.setData("EXE",i,"N");
        }
        masterTbl.setParmValue(parm);
        totPerson.setValue(masterTbl.getRowCount());
    }

    public void onSave(){
        if(AllSel.getValue().equals("Y"))
            onSaveAll();
        else
            onSavePerson();
    }

    public void onSavePerson(){
        masterTbl.acceptText();
        boolean saveFlag = false;
        for(int i = 0;i < masterTbl.getRowCount();i++){
            if (!TypeTool.getBoolean(masterTbl.getValueAt(i, 0)))
                continue;
            saveFlag = true;
            break;
        }
        if(!saveFlag){
            messageBox("��չ������");
            return;
        }
        if (CHECK2.isSelected()) {
            //�̶����õĹ���
            if (!unfoldFeePerson()) {
                this.messageBox("�̶�����\nչ��ʧ�ܣ�");
            }
            else
                this.messageBox("�̶�����\nչ���ɹ���");
        }
        int rows=masterTbl.getRowCount();
        if(rows<=0){
            this.messageBox("û�г���ҽ��չ�����ݣ�");
            return;
        }

        //ѭ�������Ƿ��й�ѡ����Ŀ
        boolean saveFlg=false;
        for(int i=0;i<rows;i++){
            if (TypeTool.getBoolean(masterTbl.getValueAt(i, 0)))
                saveFlg=true;
        }
        if(!saveFlg){
            this.messageBox("��ѡ��չ�����ݣ�");
            return;
        }

        //����ѡ��ִ����Ӧ�Ķ���--����ҽ�� �̶����� ������־
        if (CHECK1.isSelected()) {
            //����չ������
            if (!unfoldOrder()) {
                this.messageBox("���ڴ���\nչ��ʧ�ܣ�");
            }
            else
                this.messageBox("���ڴ���\nչ���ɹ���");
        }
        onQuery();
        return;
    }

    /**
     * ����
     */
    public void onSaveAll(){
        if (CHECK2.isSelected()) {
            //�̶����õĹ���
            if (!unfoldFee()) {
                this.messageBox("�̶�����\nչ��ʧ�ܣ�");
            }
            else
                this.messageBox("�̶�����\nչ���ɹ���");
        }

        if (CHECK4.isSelected()) {//�����ڲ�����־ǰִ�� add by wanglong 20140304
            if(!unfoldINFLog()){
                this.messageBox("�п���־\nչ��ʧ�ܣ�");
            }
            else
                this.messageBox("�п���־\nչ���ɹ���");
        }
        
        
        if (CHECK3.isSelected()) {
            if(!unfoldLog()){
                this.messageBox("������־\nչ��ʧ�ܣ�");
            }
            else
                this.messageBox("������־\nչ���ɹ���");
        }
        masterTbl.acceptText();
        int rows=masterTbl.getRowCount();
        if(rows<=0){
            this.messageBox("û�г���ҽ��չ�����ݣ�");
            return;
        }

        //ѭ�������Ƿ��й�ѡ����Ŀ
        boolean saveFlg=false;
        for(int i=0;i<rows;i++){
            if (TypeTool.getBoolean(masterTbl.getValueAt(i, 0)))
                saveFlg=true;
        }
        if(!saveFlg){
            this.messageBox("��ѡ��չ�����ݣ�");
            return;
        }

        //����ѡ��ִ����Ӧ�Ķ���--����ҽ�� �̶����� ������־
        if (CHECK1.isSelected()) {
            //����չ������
            if (!unfoldOrder()) {
                this.messageBox("���ڴ���\nչ��ʧ�ܣ�");
            }
            else
                this.messageBox("���ڴ���\nչ���ɹ���");
        }
        onQuery();
        return;
    }
    
    /**
     * �п���־չ��
     * @return boolean
     */
    private boolean unfoldINFLog() {// add by wanglong 20140304
        TParm sql = new TParm();
        Timestamp time = StringTool.rollDate((Timestamp) this.getValue("DATE"), -1);//ͳ������
        sql.setData("STADATE", StringTool.getString(time, "yyyyMMdd"));
        sql.setData("OPT_USER", Operator.getID());
        sql.setData("OPT_TERM", Operator.getIP());
        sql.setData("REGION_CODE", Operator.getRegion());
        TParm dept = new TParm();
        dept = STADeptListTool.getInstance().selectIPD_DEPT(sql);
        TParm parm = new TParm();
        parm.setData("SQL", sql.getData());
        parm.setData("DEPT", dept.getData());
        TParm result =
                TIOM_AppServer.executeAction("action.inf.InfAction", "insertDaily_Rec",
                                             parm);
        if (result.getErrCode() < 0) {
            System.out.println("" + result);
            return false;
        }
        return true;
    }

    /**
     * ������־չ��
     * @return boolean
     */
    private boolean unfoldLog() {
//        TParm parm = new TParm();
//        parm.setData("STATION",STATION.getValue());
//        this.openDialog("%ROOT%/config/sta/STAStationLog.x",parm);
        TParm sql = new TParm();
        Timestamp time = StringTool.rollDate( (Timestamp)this.getValue(
            "DATE"), -1);
        sql.setData("STADATE", StringTool.getString(time,"yyyyMMdd"));
        sql.setData("OPT_USER",Operator.getID());
        sql.setData("OPT_TERM",Operator.getIP());
        sql.setData("REGION_CODE",Operator.getRegion());
//        System.out.println("");
        TParm dept = new TParm();
        dept = STADeptListTool.getInstance().selectIPD_DEPT(sql);
        TParm parm = new TParm();
        parm.setData("SQL", sql.getData());
        parm.setData("DEPT", dept.getData());
        TParm result = TIOM_AppServer.executeAction(
            "action.sta.STADailyAction",
            "insertStation_Daily", parm);
        if (result.getErrCode() < 0) {
            System.out.println("" + result);
            return false;
        }
        return true;
    }


    /**
     * ����չ��
     * @return boolean
     */
    private boolean unfoldFee() {
        if (("" + STATION.getValue()) == null ||
            ("" + STATION.getValue()).length() == 0 ||
            ("" + STATION.getValue()).equalsIgnoreCase("null")) {
            messageBox("�̶�����չ����Ҫ���벡��");
            return false;
        }
        String caseNo = "";
        String caseNoSeq = "";
        boolean flg = false;
        for (int i = 0; i < masterTblParm.getCount(); i++) {
//            System.out.println("��"+i+"�����λ"+masterTblParm.getBoolean("EXE", i));
            if (masterTblParm.getBoolean("EXE", i)) {
                caseNo = masterTblParm.getValue("CASE_NO", i);
                caseNoSeq = IBSOrdermTool.getInstance().
                                   selCaseNoSeqForPatch(
                                           caseNo,
                                           StringTool.getString(StringTool.
                        rollDate(SystemTool.
                                 getInstance().getDate(), -1), "yyyyMMdd"));
//                System.out.println("��"+i+"��"+caseNoSeq);
                if (caseNoSeq.length() > 0)
                    flg = true;
            }
        }
//        System.out.println(">>>>>>>>>>>>>>>>"+flg);
        if (flg) {
            if (this.messageBox("��ʾ", "�Ѳ�������,�Ƿ���չ", 2) == 0) {
//                this.messageBox("��չ��ʼ");
                TParm parm = new TParm();
                parm.setData("STATION_CODE", STATION.getValue());
                parm.setData("DATE", SystemTool.getInstance().getDate());
                parm.setData("OPT_USER", Operator.getID());
                parm.setData("OPT_TERM", Operator.getIP());
                TParm result = TIOM_AppServer.executeAction(
                        "action.adm.ADMInpAction",
                        "postAutoBill", parm); // סԺ�ǼǱ���
                if (result.getErrCode() < 0) {
                    this.messageBox_(result.getErrName() + result.getErrText());
                    return false;
                }
            }
//            this.messageBox("��չ����");
        } else {
//            this.messageBox("��չʧ�ܿ�ʼ");
            TParm parm = new TParm();
            parm.setData("STATION_CODE", STATION.getValue());
            parm.setData("DATE", SystemTool.getInstance().getDate());
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_TERM", Operator.getIP());
            TParm result = TIOM_AppServer.executeAction(
                    "action.adm.ADMInpAction",
                    "postAutoBill", parm); // סԺ�ǼǱ���
            if (result.getErrCode() < 0) {
                this.messageBox_(result.getErrName() + result.getErrText());
                return false;
            }
//            this.messageBox("��չʧ�ܽ���");

        }

        return true;
    }

    /**
     * ����չ��(����)
     * @return boolean
     */
    private boolean unfoldFeePerson() {
        for (int i = 0; i < masterTbl.getRowCount(); i++) {
            if (!TypeTool.getBoolean(masterTbl.getValueAt(i, 0)))
                continue;
            String caseNo = "" +
                            masterTbl.getParmValue().getData("CASE_NO", i);
            TParm parm = new TParm();
            parm.setData("CASE_NO", caseNo);
            parm.setData("DATE", SystemTool.getInstance().getDate());
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_TERM", Operator.getIP());
            String caseNoSeq = IBSOrdermTool.getInstance().selCaseNoSeqForPatch(
                    caseNo,
                    StringTool.getString( StringTool.rollDate(SystemTool.getInstance().getDate(),-1),
                                         "yyyyMMdd"));
            if (caseNoSeq.length() > 0) {
                if (this.messageBox("��ʾ", "�Ѳ�������,�Ƿ���չ", 2) == 0) {
                    TParm result = TIOM_AppServer.executeAction(
                            "action.adm.ADMInpAction",
                            "postAutoBillOfMen", parm); // סԺ�ǼǱ���
                    if (result.getErrCode() < 0) {
                        this.messageBox_(result.getErrName() +
                                         result.getErrText());
                        return false;
                    }

                }
            } else {
                TParm result = TIOM_AppServer.executeAction(
                        "action.adm.ADMInpAction",
                        "postAutoBillOfMen", parm); // סԺ�ǼǱ���
                if (result.getErrCode() < 0) {
                    this.messageBox_(result.getErrName() +
                                     result.getErrText());
                    return false;
                }

            }
        }
        return true;
    }
    /**
     * �����������¼�
     * @param row int
     */
    public void onMasterTbl(int row){

        detailTbl.removeRowAll();
        //ѡ�������
        int selRow=row;
        if(selRow==-1)
            return;
        // ֻ����ʾ����
        showDate();
    }

    /**
     * ��û�й�ѡ��ʱ��ֻ��ʾ��
     */
    private void showDate(){
    	 Timestamp now = TJDODBTool.getInstance().getDBTime();
         String nowString = StringTool.getString(now, "yyyyMMdd") + "0000";
        //��ǰѡ���к�
        int selRow = masterTbl.getSelectedRow();
        //�õ������ʹ��žͿ���ȷ��һ��Ψһ�Ĳ���
        String stationCode = (String) masterTbl.getParmValue().getData(
            "STATION_CODE", selRow);
        String bedNo = (String) masterTbl.getParmValue().getData("BED_NO",
            selRow);
        String caseNo = "" + masterTbl.getParmValue().getData("CASE_NO",
            selRow);
        String sql =
            "SELECT * FROM ODI_ORDER WHERE STATION_CODE='" +
            stationCode + "' AND BED_NO='" + bedNo +
            "' AND CASE_NO = '"+caseNo+""+
            "' AND CAT1_TYPE NOT IN ('LIS','RIS','PHA') AND RX_KIND = 'UD' " +
            " AND (DC_DATE IS NULL OR DC_DATE > TO_DATE('"+ nowString+ "','YYYYMMDDHH24MI') )"+
            " AND (ORDERSET_CODE IS NULL OR ORDERSET_CODE = ORDER_CODE)"+
            " AND NS_CHECK_DATE IS NOT NULL";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));

        detailTbl.setParmValue(result);

    }
    /**
     * չ��
     * @return boolean
     */
    public boolean unfoldOrder(){

        //�õ���������--չ���˵�caseNo
        TParm patData=masterTbl.getParmValue();
        int rowCount=masterTbl.getRowCount();
        Vector VcaseNo=new Vector();
        for (int i = 0; i < rowCount; i++) {
            String caseNo=(String) patData.getData("CASE_NO", i);
            String matserColum = " STATION_CODE, BED_NO,IPD_NO,MR_NO,CASE_NO ";//CASE_NO
            Timestamp now = TJDODBTool.getInstance().getDBTime();
            String nowString = StringTool.getString(now, "yyyyMMdd") + "0000";
            String EXEsql = " SELECT DISTINCT " + " (SELECT PAT_NAME FROM SYS_PATINFO WHERE SYS_PATINFO.MR_NO=ODI_ORDER.MR_NO) AS PAT_NAME, " +
                            matserColum +
                            " FROM ODI_ORDER WHERE " +
                            (buildWhere().equals("") ? "" : (buildWhere() + " AND ")) +
                            " RX_KIND = 'UD' AND CAT1_TYPE NOT IN ('LIS','RIS','PHA') " +
                            " AND NS_CHECK_CODE IS NOT NULL "+
                            " AND (DC_DATE IS NULL OR DC_DATE > TO_DATE('"+ nowString+ "','YYYYMMDDHH24MI') )"+
                            " AND CASE_NO = '"+caseNo+"'"+
                            " AND NS_CHECK_DATE IS NOT NULL "+
                            " ORDER BY MR_NO ";
            TParm parm=new TParm(TJDODBTool.getInstance().select(EXEsql));
            if(parm.getCount() <= 0)
                continue;
            //���л������Ҳ�������caseNo������
            if (TypeTool.getBoolean(masterTbl.getValueAt(i,0))&&VcaseNo.indexOf(caseNo)==-1){
                //һ���õ�caseNo
                VcaseNo.add(caseNo);
            }
        }

        //���û�в���
        if(VcaseNo.size()<=0){
//            this.messageBox("��ѡ��Ҫչ���Ĳ��ˣ�");
//            this.messageBox("�޳��ڴ���չ�����ϣ�");
            return true;
        }
        //׼��������̨������
        TParm toData=new TParm();
        toData.setData("CASE_NO",VcaseNo);
        //������Ա��Ϣ
        toData.setData("OPT_USER",Operator.getID());
        toData.setData("OPT_DATE",TJDODBTool.getInstance().getDBTime());
        toData.setData("OPT_TERM",Operator.getIP());

        //����actionִ������
        TParm result = TIOM_AppServer.executeAction("action.inw.InwStationMaintainAction",
                                                    "onSave", toData);
        if (result.getErrCode() < 0) {
            this.messageBox_(result);
            return false;
        }

        return true;
    }
    /**
     * ͨ���ӽ���õ���TParm�õ�where���
     * @return String
     */
    public String buildWhere() {
        //�õ������Ͽؼ�
        TParm queryParm = getQueryParm();
        //��queryParm��������е�����
        StringBuffer where = new StringBuffer();
        //����caseNo
        String deptCodeTemp = (String) queryParm.getData("DEPT");
        if (!"".equals(deptCodeTemp)) {
            where.append(deptCodeTemp);
        }
        //����stationCode
        String stationCodeTemp = (String) queryParm.getData("STATION");
        if (!"".equals(stationCodeTemp) && !"".equals(where.toString().trim())) {
            where.append(" AND ");
        }
        where.append(stationCodeTemp);
        //���չ��ʱ��
//        String deteTemp = (String) queryParm.getData("DATE");
//        if (!"".equals(deteTemp) && !"".equals(where.toString().trim())) {
//            where.append(" AND ");
//        }
//        where.append(deteTemp);

//        if (!"".equals(where.toString().trim()))
//            return "WHERE " + where.toString();
        return where.toString();

    }

    /**
     * ��ý����ϵ����в�ѯ����
     * @return TParm
     */
    public TParm getQueryParm() {
        //��ý����ϵĲ���
        TParm result = new TParm();
        String deptCode=(String) DEPT.getValue();
        if ("".equals(deptCode)) {
            result.setData("DEPT", "");
        }
        else {
//            result.setData("DEPT", "EXEC_DEPT_CODE = '" + deptCode + "'");
            result.setData("DEPT", "DEPT_CODE = '" + deptCode + "'");
        }

        String station=(String) STATION.getValue();
        if ("".equals(station)) {
            result.setData("STATION", "");
        }
        else {
            result.setData("STATION", "STATION_CODE = '" + station + "'");
        }

        String date = (TypeTool.getString( DATE.getValue())).substring(0,10);

        if ("".equals(date)) {
            result.setData("DATE", "");
        }
        else {
            result.setData("DATE", "ORDER_DATE BETWEEN TO_DATE('" + date + " 000000" +"','yyyy-MM-dd hh24miss') AND TO_DATE('" +date + " 235959" +"','yyyy-MM-dd hh24miss')");
        }

        return result;

    }


    /**
     * ���ȵõ�����UI�Ŀؼ�����/ע����Ӧ���¼�
     * ����
     */
    public void myInitControler() {
        //�õ�table�ؼ�
        masterTbl = (TTable)this.getComponent("masterTbl");
        detailTbl = (TTable)this.getComponent("detailTbl");

        CHECK1 = (TCheckBox)this.getComponent("CHECK1");
        CHECK2 = (TCheckBox)this.getComponent("CHECK2");
        CHECK3 = (TCheckBox)this.getComponent("CHECK3");
        CHECK4 = (TCheckBox)this.getComponent("CHECK4");//add by wanglong 0304
        AllSel = (TCheckBox)this.getComponent("AllSel");

        DEPT = (TTextFormat)this.getComponent("DEPT");
        STATION = (TTextFormat)this.getComponent("STATION");

        DATE = (TTextFormat)this.getComponent("DATE");

        totPerson = (TNumberTextField)this.getComponent("totPerson");

        Timestamp date = TJDODBTool.getInstance().getDBTime();
        DATE.setValue(date);

        //������tableע�ᵥ���¼�����
        this.callFunction("UI|masterTbl|addEventListener",
                          "masterTbl->" + TTableEvent.CLICKED, this,
                          "onMasterTbl");
        //������tableע��checkbox�¼�����
        getTTable("masterTbl").addEventListener(
                        TTableEvent.CHECK_BOX_CLICKED, this, "onTableComponent");

    }
    public TTable getTTable(String tag) {
            return (TTable) this.getComponent(tag);
    }
    /**
     * table��checkbox�¼�
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
//        System.out.println("�������");
            TTable masterTbl = (TTable) obj;
            masterTbl.acceptText();
            masterTblParm = masterTbl.getParmValue();
//            System.out.println("+++++++++++"+masterTblParm);
            return true;
    }

    /**
     * ȫѡ���е�����
     */
    public void onAllSel() {
        int rowCount = masterTbl.getRowCount();
        boolean flg = StringTool.getBoolean(AllSel.getValue());
        for(int i=0;i<rowCount;i++){
            masterTbl.getParmValue().setDataN("EXE",i,AllSel.getValue());
            masterTbl.setValueAt(flg,i,0);
        }
        masterTbl.acceptText();
    }

    /**
     * ��ղ���
     */
    public void onClear() {
        masterTbl.setParmValue(new TParm());
        detailTbl.setParmValue(new TParm());
        masterTblParm = new TParm();
        DEPT.setValue("");
        STATION.setValue("");

        CHECK1.setSelected(true); ;
        CHECK2.setSelected(true); ;
        CHECK3.setSelected(true);
        AllSel.setSelected(false);

        totPerson.setValue(0);
    }


    //��������
    public static void main(String[] args) {
        JavaHisDebug.initClient();
        //JavaHisDebug.TBuilder();

//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("inw\\INWStationMaintain.x");

    }


}
