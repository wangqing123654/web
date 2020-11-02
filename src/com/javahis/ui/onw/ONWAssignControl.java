package com.javahis.ui.onw;

import com.dongyang.data.TParm;
import com.dongyang.control.TControl;
import jdo.reg.PatAdmTool;
import jdo.sys.Operator;
import com.dongyang.manager.TCM_Transform;
import jdo.bil.BIL;
import jdo.reg.SessionTool;
import jdo.sys.SystemTool;
import jdo.reg.SchDayTool;
import com.dongyang.ui.TComboBox;
import jdo.device.CallNo;
import com.dongyang.util.StringTool;
import jdo.sys.Pat;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.ui.reg.REGPatAdmControl;

import jdo.reg.Reg;

/**
 * <p>Title:���� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:JavaHis </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ONWAssignControl extends TControl {
    TParm recptype = new TParm(); //�Ӳ�
    double regFee;
    String receiptType = "REG_FEE";
    private String admType = "O";
    String defSession = "";//��¼��ǰʱ�������ĸ��Һ�ʱ��
    String oldSession ="";//��¼�����Һŵ�ʱ�� ���ڱȽ��Ƿ��Ƿ��ﵽͬʱ��
    String service_level = "";//����ȼ�
    public void onInit() {
        super.onInit();
        init();
        callFunction("UI|SESSION_CODE|onQuery");
        setValue("REGION_CODE", Operator.getRegion());
        //���ܲ���
        recptype = (TParm)this.getParameter();
        setValue("ADM_DATE", SystemTool.getInstance().getDate());
        setValue("ADM_TYPE", recptype.getData("ADM_TYPE"));
        oldSession = recptype.getValue("SESSION_CODE");
        admType = recptype.getValue("ADM_TYPE");
        defSession = SessionTool.getInstance().getDefSessionNow(admType,Operator.getRegion());
        setValue("SESSION_CODE", defSession);
        callFunction("UI|SESSION_CODE|onQuery");
        callFunction("UI|REALDEPT_CODE|onQuery");
        callFunction("UI|CLINICROOM_NO|onQuery");
        setValue("CLINICTYPE_CODE", recptype.getData("CLINICTYPE_CODE"));
        setValue("MR_NO", recptype.getData("MR_NO"));
        setValue("CASE_NO", recptype.getData("CASE_NO"));
        setValue("PAT_NAME", recptype.getData("PAT_NAME"));
        setValue("DEPT_CODE", recptype.getData("REALDEPT_CODE"));
        setValue("DR_CODE", recptype.getData("REALDR_CODE"));
        setValue("CTZ1_CODE",recptype.getData("CTZ1_CODE"));
        service_level = recptype.getValue("SERVICE_LEVEL");
        onCLINICTYPE_Chg();//���ݺű� ˢ������combo
        regFee = BIL.getRegFee(admType,recptype.getValue("CLINICTYPE_CODE"),service_level);
    }
    /**
     * ����
     */
    public void onSave() {
        if (this.getValue("REALDR_CODE").toString().equals("")) {
            this.messageBox_("��ѡ��ҽʦ");
            return;
        }
        if(this.getValueString("SESSION_CODE").equals("")){
            this.messageBox_("��ѡ��ʱ��");
            return;
        }
        if(this.getValueString("REALDEPT_CODE").equals("")){
            this.messageBox_("��ѡ��Ʊ�");
            return;
        }
        if(this.getValueString("CLINICROOM_NO").equals("")){
            this.messageBox_("��ѡ������");
            return;
        }
        if(!oldSession.equals(this.getValueString("SESSION_CODE"))){
            if(this.messageBox("��ʾ","��ͬʱ���Ƿ����ת�",0)==1){
                return;
            }
        }
        if(this.getValueString("REALDR_CODE").equals(this.getValueString("DR_CODE"))){
            this.messageBox_("���ɷ��ﵽͬһ��ҽʦ");
            return;
        }
        //�жϷ��� �Ƿ����ԭ�кű�ķ���
        int re = this.onCLINICTYPE_CODE();
        if (re==2) {
            this.messageBox_("�ű���ø���ԭ�ű���ã����ɷ��");
            return;
        }else if(re==1){
            if(this.messageBox("��ʾ","�ű���õ���ԭ�ű���ã�Ҫ���з��������?",0)==1){
                return;
            }
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO", getValue("CASE_NO"));
        parm.setDataN("SESSION_CODE",getValue("SESSION_CODE"));
        parm.setData("REALDEPT_CODE", getValue("REALDEPT_CODE"));
        parm.setData("REALDR_CODE", getValue("REALDR_CODE"));
        parm.setData("CLINICROOM_NO", getValue("CLINICROOM_NO"));
        parm.setData("ADM_TYPE",admType);
        parm.setData("ADM_DATE",this.getValue("ADM_DATE"));
        parm.setData("QUE_NO",recptype.getValue("QUE_NO"));
        parm.setData("REGION_CODE",Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm dataD = TIOM_AppServer.executeAction(
            "action.onw.ONWAssignAction",
            "onAssign", parm); //����
        if (dataD.getErrCode() < 0) {
            this.messageBox("E0005");
        } else {
            this.messageBox("P0005");
            /** ���ýк�ϵͳ����ȷ��ת����к�ϵͳ��ʲô��Ϣ��ʽ�� add by duzhw start */
            REGPatAdmControl regPatAdm = new REGPatAdmControl();
            String result = regPatAdm.callNo("UNREG", getValue("CASE_NO").toString());
            
            if("true".equals(result)){
            	/** add by duzhw end */
            	try{
                    onCall_T(); //�����Ŷӽк� �ӿ� �˺�
                    onCall_G(); //�����Ŷӽк� �ӿ� �Һ�
                }catch(Exception e){

                }finally{
                callFunction("UI|onClose");
                }
            }
            
        }
    }

    /**
     * �жϺű����
     * @return int  0:����ת�� �ű��ԭ�ű������ͬ;
     *              1:����ת�� �ºű���õ���ԭ�ű���� ��Ҫ��ʾ;
     *              2:����ת��ºű���ø���ԭ�ű���� ��ֹ�ͷ�����߷���ת��
     */
    public int onCLINICTYPE_CODE() {
        double reg_fee = BIL.getRegFee(admType,
                                       TCM_Transform.getString(getValue("CLINICTYPE_CODE")),service_level);
        if (reg_fee < 0)
            return 0;
        if (reg_fee > regFee) //�ºű���ø���ԭ�ű����
            return 2;
        else if (reg_fee < regFee)//�ºű���õ���ԭ�ű����
            return 1;
        else
            return 0;
    }

    /**
     * ͨ���ű�ɸѡҽʦ
     */
    public void onDr_code() {
        TParm parm = new TParm();
        parm.setData("SESSION_CODE", getValue("SESSION_CODE"));
        parm.setData("ADM_DATE", getValue("ADM_DATE"));
        TParm dataD = SchDayTool.getInstance().selDrByClinicType(parm);
        setValue("REALDR_CODE",dataD);
    }
    /**
     * ʱ�θı� ������ҺͿƱ��ѡ����
     */
    public void onSESSION_CODE(){
        this.clearValue("CLINICROOM_NO;REALDEPT_CODE");
    }
    /**
     * �ű�ı� ���ҽ��Cobom��ѡ��ֵ
     */
    public void onCLINICTYPE_Chg(){
        //���ҽʦ
        this.clearValue("REALDR_CODE");
        //ɸѡ�����ҵ�ҽʦ
        this.callFunction("UI|REALDR_CODE|onQuery");
    }
    /**
     * ���Ҹı� �޸�ҽʦcombo��ѡ��ֵ
     */
    public void onCLINICROOM_NO_Chg(){
        if(this.getValueString("CLINICROOM_NO").length()<=0){
            return;
        }
        //���ҽʦ
        this.clearValue("REALDR_CODE");
        //ɸѡ�����ҵ�ҽʦ
        this.callFunction("UI|REALDR_CODE|onQuery");
        TComboBox Dr = (TComboBox)this.getComponent("REALDR_CODE");
        //���ҽʦComboѡ�����1����ʾ ������п���ҽ�� Ĭ��ѡ��ÿ���ҽ��
        if(Dr.getItemCount()>1){
            Dr.setSelectedIndex(1);
            //��ѯ�հ�� �����������
            TParm parm = new TParm();
            parm.setData("ADM_DATE", recptype.getData("ADM_DATE"));
            parm.setData("REGION_CODE", recptype.getValue("REGION_CODE"));
            parm.setData("ADM_TYPE", recptype.getValue("ADM_TYPE"));
            parm.setData("SESSION_CODE", getValue("SESSION_CODE"));
            parm.setData("DR_CODE", getValue("REALDR_CODE"));
            TParm schDay = SchDayTool.getInstance().selectdata(parm); //��ѯ�հ��
            this.setValue("CLINICROOM_NO", schDay.getValue("CLINICROOM_NO", 0));
            this.setValue("REALDEPT_CODE", schDay.getValue("DEPT_CODE", 0));
        }
    }
    /**
     * ת��ҽʦ�ı��¼�
     */
    public void onREALDR_CODE_Chg(){
        if(this.getValueString("REALDR_CODE").length()<=0){
            return;
        }
        //�����û�ID ��ѯ�հ�� �������
        //��ѯ�հ�� �����������
        TParm parm = new TParm();
        parm.setData("ADM_DATE", recptype.getData("ADM_DATE"));
        parm.setData("REGION_CODE",recptype.getValue("REGION_CODE"));
        parm.setData("ADM_TYPE",recptype.getValue("ADM_TYPE"));
        parm.setData("SESSION_CODE",getValue("SESSION_CODE"));
        parm.setData("DR_CODE",getValue("REALDR_CODE"));
        TParm schDay = SchDayTool.getInstance().selectdata(parm); //��ѯ�հ��
        this.setValue("CLINICROOM_NO",schDay.getValue("CLINICROOM_NO",0));
        this.setValue("REALDEPT_CODE",schDay.getValue("DEPT_CODE",0));
    }
    /**
     * ���
     */
    public void onClear(){
        setValue("SESSION_CODE", defSession);
        this.clearValue("CLINICTYPE_CODE;CLINICROOM_NO;REALDEPT_CODE;REALDR_CODE");
    }
//    /**
//     * �����б��ʼ��
//     */
//    private void deptComboInit(){
//        String sql =
//            " SELECT DISTINCT A.DEPT_CODE AS ID,B.DEPT_ABS_DESC AS NAME, B.PY1 AS PY1, B.PY2 AS PY2 " +
//            "   FROM REG_SCHDAY A,SYS_DEPT B " +
//            "  WHERE B.ACTIVE_FLG='Y' " +
//            "    AND A.DEPT_CODE = B.DEPT_CODE ";
//        String sql1 = " ORDER BY A.DEPT_CODE ";
//
//        StringBuffer sb = new StringBuffer();
//
//        String regionCode = this.getValueString("REGION_CODE");
//        if (regionCode != null && regionCode.length() > 0)
//            sb.append(" AND A.REGION_CODE = '" + regionCode + "' ");
//        String admType = this.getValueString("ADM_TYPE");
//        if (admType != null && admType.length() > 0) {
//            sb.append(" AND A.ADM_TYPE = '" + admType + "' ");
//        }
//        Object value = this.getValue("ADM_DATE");
//        String admDate = "";
//        if (value instanceof Timestamp)
//            admDate = StringTool.getString(TCM_Transform.getTimestamp(value),
//                                           "yyyyMMdd");
//        if (admDate != null && admDate.length() > 0) {
//            sb.append(" AND A.ADM_DATE = '" + admDate + "' ");
//        }
//        String sessionCode = this.getValueString("SESSION_CODE");
//        if (sessionCode != null && sessionCode.length() > 0) {
//            sb.append(" AND A.SESSION_CODE = '" + sessionCode + "' ");
//        }
//        String CLINICTYPE_CODE = this.getValueString("CLINICTYPE_CODE");
//        if(CLINICTYPE_CODE != null&& CLINICTYPE_CODE.length()>0){
//            sb.append(" AND A.CLINICTYPE_CODE='"+ CLINICTYPE_CODE +"' ");
//        }
//        if (sb.length() > 0)
//            sql += sb.toString() + sql1;
//        else
//            sql = sql + sql1;
//        TTextFormat deptList = (TTextFormat)this.getComponent("tTextFormat_0");
//        deptList.setPopupMenuSQL(sql);
//    }
    /**
     * �кŽӿ� (���ﱣ��ʱ����) �Һ�
     */
    public void onCall_G(){
        Pat pat  = Pat.onQueryByMrNo(recptype.getValue("MR_NO"));
        CallNo call = new CallNo();
        Reg reg = Reg.onQueryByCaseNo(pat,recptype.getValue("CASE_NO"));
        if(call.init()){
            String reg_date = StringTool.getString(recptype.getTimestamp(
                "REG_DATE"), "yyyy-MM-dd HH:mi:ss");
            String ctz_desc = ( (TComboBox)this.getComponent("CTZ1_CODE")).
                getSelectedName();
            String CLINICTYPE_DESC = ( (TComboBox)this.getComponent(
                "CLINICTYPE_CODE")).getSelectedName();
            String realDr = this.getValueString("REALDR_CODE");
            String realDept = this.getValueString("REALDEPT_CODE");
            String Session = ( (TComboBox)this.getComponent("SESSION_CODE")).getSelectedName();
            String s = call.SyncClinicMaster(reg_date, //�Һ�����
                                             recptype.getValue("CASE_NO"), //�������
                                             CLINICTYPE_DESC, //���
                                             reg.getQueNo()+"", //���
                                             recptype.getValue("MR_NO"), //������
                                             pat.getName(), //����
                                             pat.getSexString(), //�Ա�
                                             StringTool.CountAgeByTimestamp(pat.
                getBirthday(), SystemTool.getInstance().getDate())[0], //����
                                             ctz_desc, //�����
                                             realDept, //����
                                             reg_date, //�Һ�ʱ��
                                             realDr, //ҽ��
                                             "0", "2",Session);
        }
    }
    /**
     * �кŽӿ� (���ﱣ��ʱ����) �˺�
     */
    public void onCall_T(){
        Pat pat  = Pat.onQueryByMrNo(recptype.getValue("MR_NO"));
        CallNo call = new CallNo();
        if(call.init()){
            String reg_date = StringTool.getString(recptype.getTimestamp(
                "REG_DATE"), "yyyy-MM-dd HH:mi:ss");
            String ctz_desc = ( (TComboBox)this.getComponent("CTZ1_CODE")).
                getSelectedName();
            String CLINICTYPE_DESC = ( (TComboBox)this.getComponent(
                "CLINICTYPE_CODE")).getSelectedName();
            String Session = ( (TComboBox)this.getComponent("SESSION_CODE")).getSelectedName();
            String s = call.SyncClinicMaster(reg_date, //�Һ�����
                                             recptype.getValue("CASE_NO"), //�������
                                             CLINICTYPE_DESC, //���
                                             recptype.getValue("QUE_NO"), //���
                                             recptype.getValue("MR_NO"), //������
                                             pat.getName(), //����
                                             pat.getSexString(), //�Ա�
                                             StringTool.CountAgeByTimestamp(pat.
                getBirthday(), SystemTool.getInstance().getDate())[0], //����
                                             ctz_desc, //�����
                                             recptype.getValue("REALDEPT_CODE"), //����
                                             reg_date, //�Һ�ʱ��
                                             recptype.getValue("REALDR_CODE"), //ҽ��
                                             "1", "3",Session);
        }
    }

}
