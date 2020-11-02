package com.javahis.ui.erd;


import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import jdo.erd.ErdForBedAndRecordTool;
import java.sql.Timestamp;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TNull;
import jdo.sys.Pat;
import com.javahis.util.StringUtil;
import com.dongyang.ui.event.TPopupMenuEvent;
import java.util.Calendar;

/**
 * <p>Title: ���ﶯ̬��¼����</p>
 *
 * <p>Description: ����һ���趨��λ��ʱ���
 *                 SYS_PATINFO���Ʋ��˼�¼��ERD_RECORD </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author ZangJH 2009-9-10
 *
 * @version 1.0
 */
public class ERDDynamicRcdMainContol
    extends TControl {

    //ĳ�����˵Ĳ�����
    private String caseNo = "";
    private String mrNo = "";
    private String patName = "";
    //��¼��ǰ�Ĵ���
    private String bedNo = "";
    private String erdregionCode = "";
    //Ŀǰ�����ѱ������ܿ�֮��
    private String flg = "";

    //�մ�
    private TCheckBox EmptyBed;
    private TComboBox ERD_REGION_CODE;
    private TTable table;
    private TTabbedPane tabPanel;

    //�ڶ���ҳǩ�Ŀؼ�
    private TRadioButton Radio1; //��Ժ
    private TRadioButton Radio2; //תסԺ

    private TTextField PAT_NAME;
    private TTextField AGE;
    private TTextField IDNO;
    private TComboBox MARRIGE;
    private TTextField OFFICE;
    private TTextField O_TEL;
    private TTextField O_POSTNO;
    private TTextField O_ADDRESS;
//    private TTextField H_TEL;
    private TTextField H_ADDRESS;
    private TTextField H_POSTNO;
    private TTextField CONTACTER;
    private TTextField CONT_TEL;
    private TTextField CONT_ADDRESS;
    private TTextField OUT_DIAG_CODE;
    private TTextField HIDE_CODE;//���ؿؼ�����ICD_CODEʹ��
    private TTextField CODE_REMARK;



    private TTextField DISCHG_DATE_TIME;
    private TTextField IPD_IN_DATE_TIME;

    private TComboBox SEX;
    private TComboBox CTZ1_CODE;
    private TTextFormat HEAL_LV;
    private TComboBox CODE_STATUS;
    private TTextFormat OP_CODE;
    private TComboBox OP_LEVEL;
    private TTextFormat OCCUPATION;
    private TTextField RESID_PROVICE;
    private TTextFormat RESID_COUNTRY;
    private TTextFormat FOLK;
    private TTextFormat RELATIONSHIP;
    private TTextFormat NATION;
    private TTextFormat ERD_REGION;
    private TTextFormat OUT_ERD_REGION;
    private TTextFormat IN_DEPT;
    private TTextFormat OUT_DEPT;
    private TTextFormat DR_CODE;
    private TTextFormat MAIN_SUGEON;
    private TComboBox DISCHG_TYPE;
    private TTextFormat IPD_IN_DEPT;
    private TComboBox TRAN_HOSP;
    private TTextFormat DISCHG_DATE_DAY;
    private TTextFormat IPD_IN_DATE_DAY;


    private TNumberTextField REAL_STAY_DAYS;
    private TNumberTextField GET_TIMES;
    private TNumberTextField SUCCESS_TIMES;
    private TNumberTextField ACCOMPANY_WEEK;
    private TNumberTextField ACCOMPANY_MONTH;
    private TNumberTextField ACCOMPANY_YEAR;

    private TTextFormat BIRTH_DATE;
    private TTextFormat IN_DATE;
    private TTextFormat OUT_DATE;
    private TTextFormat OP_DATE;
    private TTextFormat ACCOMP_DATE;

    public ERDDynamicRcdMainContol() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        //�õ��ⲿ�Ĳ���
        initParmFromOutside();;
        //������ĳ�ʼ��
        myInitControler();
        onQuery();
        //���ó����ص�����
        callFunction("UI|RESID_PROVICE_DESC|setPopupMenuParameter","aaa","%ROOT%\\config\\sys\\SYSHOMEPLACEPopup.x");
        //textfield���ܻش�ֵ
        callFunction("UI|RESID_PROVICE_DESC|addEventListener",TPopupMenuEvent.RETURN_VALUE,this,"popReturn1");
    }

    public void popReturn1(String tag, Object obj) {
            TParm parm = (TParm) obj;
            this.setValue("RESID_PROVICE", parm.getValue("HOMEPLACE_CODE"));
            this.setValue("RESID_PROVICE_DESC", parm.getValue("HOMEPLACE_DESC"));
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        table.setParmValue(new TParm());
        //��ʼ����ǰtable
        initTable();
    }

    /**
     * ��ʼ��table
     * ��ѯ�����ǣ� caseNo/����
     */
    public void initTable() {

        TParm selParm = new TParm();
        selParm = getQueryParm();
        TParm query = ErdForBedAndRecordTool.getInstance().selBed(selParm);
        if (query.getCount() <= 0) {
            table.setParmValue(query);
            this.messageBox("û��������ݣ�");
            return;
        }
        //ѭ���޸�ʱ���ʽ
        for (int i = 0; i < query.getCount(); i++) {
            if (query.getData("CASE_NO", i) == null)
                query.setData("EXE_FLG", i, "N");
            else
                query.setData("EXE_FLG", i, "Y");
        }
        table.setParmValue(query);
        return;
    }

    /**
     * �õ���ѯ����
     * @return TParm
     */
    public TParm getQueryParm() {

        TParm parm = new TParm();
        //����
        String erdRegion = ERD_REGION_CODE.getValue();
        if (!"".equals(erdRegion)) {
            parm.setData("ERD_REGION_CODE", erdRegion);
        }
        if (EmptyBed.isSelected()) {
            parm.setData("OCCUPY_FLG", "Y");
        }
        return parm;
    }

    /**
     * ת������
     */
    public void onTransfer() {
        onTransferSave();
        /*TParm parm = new TParm();
        parm.setData("CASE_NO", this.getCaseNo());
        parm.setData("MR_NO", this.getMrNo());
        String value = (String)this.openDialog(
            "%ROOT%\\config\\erd\\ERDTransferBed.x", parm);
        if ("OK".equals(value))*/
            onQuery();
    }

    public void onTransferSave(){
        TParm saveData=new TParm();
        saveData.setData("CASE_NO",getCaseNo());
        saveData.setData("MR_NO",getMrNo());
        if(!initCurrentRegionBed(saveData))
            return;
        if(!initNextRegionBed(saveData))
            return;
        saveData.setData("OPT_USER",Operator.getID());
        saveData.setData("OPT_TERM",Operator.getIP());
        //���淽��
        //����actionִ������
        TParm result = TIOM_AppServer.executeAction(
            "action.erd.ERDDynamicRcdAction",
            "onTransfer", saveData);
        if (result.getErrCode() < 0)
            messageBox("����ʧ��");
    }

    /**
     * ȡ�ò�����ǰ������������
     * @param saveData TParm
     */
    public boolean initCurrentRegionBed(TParm saveData){
        TParm parm = new TParm();
        parm.setData("CASE_NO",getCaseNo());
        parm = ErdForBedAndRecordTool.getInstance().selERDRegionBedByPat(parm);
        if(parm.getErrCode() < 0){
            messageBox(parm.getErrName());
            return false;
        }
        if(parm.getCount() <= 0){
            messageBox("δȡ�ò�����ǰ��");
            return false;
        }
        saveData.setData("fromRegion",parm.getData("ERD_REGION_CODE",0));
        saveData.setData("fromBed",parm.getData("BED_NO",0));
        return true;
    }

    /**
     * ȡ�ò�������ת�����������
     * @param saveData TParm
     */
    public boolean initNextRegionBed(TParm saveData){
        if(getTable().getSelectedRow() < 0){
            messageBox("δѡ��մ�");
            return false;
        }
        TParm tableParm =getTable().getParmValue();
        if(tableParm.getValue("CASE_NO",getTable().getSelectedRow()) != null &&
           tableParm.getValue("CASE_NO",getTable().getSelectedRow()).length() != 0 &&
           !tableParm.getValue("CASE_NO",getTable().getSelectedRow()).equalsIgnoreCase("null")){
            messageBox("�˲����Ѿ��в���");
            return false;
        }
        saveData.setData("toRegion",tableParm.getData("ERD_REGION_CODE",getTable().getSelectedRow()));
        saveData.setData("toBed",tableParm.getData("BED_NO",getTable().getSelectedRow()));
        return true;
    }

    private TTable getTable(){
        return ((TTable)getComponent("TABLE"));
    }

    /**
     * ���
     */
    public void onClear(){

        int page = tabPanel.getSelectedIndex();
        if (page == 0) {
        	 setValue("ERD_REGION_CODE","");//===������
        	 setValue("OCCUPY_FLG","N");//===�մ�
            getTable().removeRowAll();
        }
        else{
            setValue("DISCHG_TYPE","");
            setValue("DISCHG_DATE_DAY","");
            setValue("DISCHG_DATE_TIME",":");
            setValue("TRAN_HOSP","");

            setValue("IPD_IN_DEPT","");
            setValue("IPD_IN_DATE_DAY","");
            setValue("IPD_IN_DATE_TIME",":");

            setValue("PAT_NAME","");
            setValue("SEX","");
            setValue("AGE","");
            setValue("CONTACTER","");
            setValue("RELATIONSHIP","");
            setValue("CONT_TEL","");
            setValue("MARRIGE","");
            setValue("OCCUPATION","");
            setValue("CONT_ADDRESS","");
            setValue("FOLK","");
            setValue("NATION","");
            setValue("OFFICE","");
            setValue("O_TEL","");
            setValue("O_POSTNO","");
            setValue("BIRTH_DATE","");
            setValue("O_ADDRESS","");
            setValue("RESID_PROVICE","");
            setValue("RESID_PROVICE_DESC","");
            setValue("RESID_COUNTRY","");
            setValue("H_ADDRESS","");
            setValue("H_POSTNO","");
            setValue("IDNO","");
            setValue("CTZ1_CODE","");
            setValue("IN_DATE","");
            setValue("ERD_REGION","");
            setValue("IN_DEPT","");
            setValue("OP_CODE","");
            setValue("OP_DATE","");
            setValue("OUT_DATE","");
            setValue("OUT_ERD_REGION","");
            setValue("OUT_DEPT","");
            setValue("OP_LEVEL","");
            setValue("HEAL_LV","");
            setValue("REAL_STAY_DAYS","");
            setValue("OUT_DIAG_CODE","");
            setValue("CODE_REMARK","");
            setValue("CODE_STATUS","");
            setValue("MAIN_SUGEON","");
            setValue("GET_TIMES","");
            setValue("SUCCESS_TIMES","");
            setValue("DR_CODE","");
            setValue("ACCOMP_DATE","");
            setValue("ACCOMPANY_WEEK","");
            setValue("ACCOMPANY_MONTH","");
            setValue("ACCOMPANY_YEAR","");
        }
    }


    /**
     * ����
     * @return boolean
     */
    public boolean onSave() {

        //�ж�ҳǩ���ò�ͬ�ı��涯��
        TParm parm0 = new TParm(); //��һ��ҳǩ
        TParm parm1 = new TParm(); //�ڶ���ҳǩ
        //�õ���ǰҳǩ��
        int page = tabPanel.getSelectedIndex();
        if (page == 0) {
            //����ERD_BED
            parm0 = getPage0Data();
            //����ò����Ѵ��ڱ�ERD_RECORD�о͸��»��߲���
            boolean IorUFlg=checkPatExist(getCaseNo());
            if(IorUFlg){
                messageBox("�����Ѿ���������");
                return true;
            }
            parm0.setData("IORU_FLG",IorUFlg);
            //=====�ж������Ƿ�ѡ����λ
//            System.out.println("parm0 parm0 parm0 is ::"+parm0);
            if(this.getBedNo().length()==0||
            		this.getBedNo().equals(null)){
            	this.messageBox("��ѡ��λ");
            	return true;
            	
            }
            //����actionִ������
            TParm result = TIOM_AppServer.executeAction(
                "action.erd.ERDDynamicRcdAction",
                "setBed", parm0);
            if (result.getErrCode() < 0) {
                this.messageBox_(result);
                return false;
            }
        }
        else if (page == 1) {
            //����ERD_RECORD
            parm1 = getPage1Data();
            TParm result=new TParm();
            if(!checkPatExist(getCaseNo())){
                //����actionִ������
                result = TIOM_AppServer.executeAction(
                    "action.erd.ERDDynamicRcdAction",
                    "setPatRecord", parm1);
            }else{
                //����Ƿ�ת��ERD--��Ϊtrue��ʱ���̨���崲����
                parm1.setData("OUT_FLG",ifOutErd());
                if(ifOutErd() && getValue("OUT_DATE") == null){
                    messageBox("��¼���Ժʱ��");
                    return false;
                }
                //��λ��λ
                parm1.setData("ERD_REGION_CODE",getErdBedInfo().getData("ERD_REGION_CODE",0));
                parm1.setData("BED_NO",getErdBedInfo().getData("BED_NO",0));

                if(ifOutErd() &&
                   getErdBedInfo().getCount() <=0 &&
                   !checkReturnDate(caseNo)){
                    messageBox("�˲������崲");
                    return true;
                }
                if(getValueBoolean("RETURN") &&
                   getErdBedInfo().getCount()>0){
                    messageBox("������Ȼ��Ժ");
                    return true;
                }
                if(getErdBedInfo().getCount() <=0)
                    parm1.setData("OUT_FLG",false);
                if(getValueString("RETURN").equals("Y")){
                    if(parm1.getData("RETURN_DATE") == null ||
                       parm1.getValue("RETURN_DATE").equalsIgnoreCase("null")||
                       parm1.getValue("RETURN_DATE").length() == 0){
                        messageBox("�������л�ʱ��");
                        return false;
                    }
                    Object obj = openDialog("%ROOT%\\config\\erd\\ERDBedSelUI.x");
                    if(obj == null)
                        return false;
                    else{
                        parm1.setData("ERD_REGION_CODE_R",
                                      ((TParm) obj).getValue("ERD_REGION_CODE"));
                        parm1.setData("BED_NO_R",
                                      ((TParm) obj).getValue("BED_NO"));
                        parm1.setData("RETURN_FLG",getValueString("RETURN"));
                    }
                }
                result = TIOM_AppServer.executeAction(
                    "action.erd.ERDDynamicRcdAction",
                    "updatePatRecord", parm1);
            }
            if (result.getErrCode() < 0) {
                this.messageBox_(result);
                return false;
            }
        }
        this.messageBox("����ɹ���");
        return true;
    }

    /**
     * �жϸò����Ƿ��Ѿ�������--���뻹�Ǹ���
     * @param caseNo String
     * @return boolean ����--true ������--false
     */
    private boolean checkReturnDate(String caseNo){
        String sqlSel=" SELECT RETURN_DATE FROM ERD_RECORD "+
            "WHERE CASE_NO="+caseNo;
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlSel));
        if(result.getValue("RETURN_DATE",0) == null)
            return false;
        if(result.getValue("RETURN_DATE",0).length() == 0)
            return false;
        return true;
    }
    /**
     * �õ��ò��˵����۴���Ϣ
     * @param caseNo String
     * @return TParm
     */
    private TParm getErdBedInfo(){
        String sql=" SELECT * FROM ERD_BED WHERE CASE_NO='"+getCaseNo()+"' ";
        return new TParm(TJDODBTool.getInstance().select(sql));

    }

    /**
     * �õ�����Ϊ0��ҳǩ�Ĳ���
     * @return TParm
     */
    private TParm getPage0Data() {
        TParm result = new TParm();
        result.setData("ERD_REGION_CODE", getErdregionCode());
        result.setData("BED_NO", getBedNo());
        result.setData("CASE_NO", getCaseNo());
        result.setData("MR_NO", getMrNo());
        result.setData("OCCUPY_FLG", "Y");
        result.setData("OPT_USER", Operator.getID());
        result.setData("OPT_TERM", Operator.getIP());
        result.setData("ADM_STATUS", "6");
        result.setData("PAT_INFO",copyPatDate().getData());
        result.setData("RETURN_DATE",getValueString("RETURN_DATE").length() == 0?
                                     new TNull(Timestamp.class):
                                     getValue("RETURN_DATE"));
        return result;
    }

    /**
     * ��SYS_PATINFO�õ����ݡ���COPY��ERD_RECORD
     * @return TParm
     */
    private TParm copyPatDate(){
        Timestamp now=TJDODBTool.getInstance().getDBTime();

        String mrNo=this.getMrNo();
        //�õ�SYS_PATINFO������
        String sqlSysPatInfo="SELECT * FROM SYS_PATINFO WHERE MR_NO='"+mrNo+"'";
        TParm sysPatInfoParm=new TParm(TJDODBTool.getInstance().select(sqlSysPatInfo));
        //�õ�REG_PATADM������
        String sqlRegPatAdm="SELECT * FROM REG_PATADM WHERE CASE_NO='"+this.getCaseNo()+"'";
        TParm sysRegPatAdm=new TParm(TJDODBTool.getInstance().select(sqlRegPatAdm));
        //�õ�ERD_BED������
//        String sqlErdBed="SELECT * FROM ERD_BED WHERE CASE_NO='"+this.getCaseNo()+"'";
//        TParm sysErdBed=new TParm(TJDODBTool.getInstance().select(sqlErdBed));

        TParm result=new TParm();
        result.setData("CASE_NO",this.getCaseNo());
        result.setData("MR_NO",this.getMrNo());
        result.setData("ERD_NO","");//����
        result.setData("PAT_NAME",sysPatInfoParm.getData("PAT_NAME",0));
        result.setData("SEX",sysPatInfoParm.getData("SEX_CODE",0));


        result.setData("BIRTH_DATE",sysPatInfoParm.getData("BIRTH_DATE",0));
        Pat pat = Pat.onQueryByMrNo(this.getMrNo());
        result.setData("AGE",StringUtil.getInstance().showAge(pat.getBirthday(), now));
        result.setData("MARRIGE",sysPatInfoParm.getData("MARRIAGE_CODE",0));
        result.setData("OCCUPATION",sysPatInfoParm.getData("OCC_CODE",0));
        result.setData("RESID_PROVICE",
                       !"".equals(sysPatInfoParm.getData("RESID_POST_CODE", 0).toString())?
                       ("" + sysPatInfoParm.getData("RESID_POST_CODE", 0)).substring(0, 2) :
                       new TNull(String.class));
        result.setData("RESID_PROVICE_DESC",getPatHome(getValueString("RESID_PROVICE")).getData("HOMEPLACE_DESC",0));
        result.setData("RESID_COUNTRY",
                       !"".equals(sysPatInfoParm.getData("RESID_POST_CODE", 0).toString()) ?
                       ("" + sysPatInfoParm.getData("RESID_POST_CODE", 0)):
                       new TNull(String.class));
        result.setData("FOLK",sysPatInfoParm.getData("SPECIES_CODE",0));
        result.setData("NATION",sysPatInfoParm.getData("NATION_CODE",0));
        result.setData("IDNO",sysPatInfoParm.getData("IDNO",0));
        result.setData("CTZ1_CODE",sysPatInfoParm.getData("CTZ1_CODE",0));

        result.setData("OFFICE",sysPatInfoParm.getData("COMPANY_DESC",0));
        result.setData("O_ADDRESS",new TNull(String.class));
        result.setData("O_TEL",new TNull(String.class));
        result.setData("O_POSTNO",new TNull(String.class));
        result.setData("H_ADDRESS",new TNull(String.class));

        result.setData("H_TEL",new TNull(String.class));
        result.setData("H_POSTNO",new TNull(String.class));
        result.setData("CONTACTER",sysPatInfoParm.getData("CONTACTS_NAME",0));
        result.setData("RELATIONSHIP",sysPatInfoParm.getData("RELATION_CODE",0));
        result.setData("CONT_ADDRESS",sysPatInfoParm.getData("CONTACTS_ADDRESS",0));

        result.setData("CONT_TEL",sysPatInfoParm.getData("CONTACTS_TEL",0));
        result.setData("IN_DATE",now);
        result.setData("IN_DEPT",sysRegPatAdm.getData("DEPT_CODE",0));
        result.setData("ERD_REGION",getErdregionCode());
        result.setData("OUT_DATE",new TNull(Timestamp.class));

        result.setData("OUT_DEPT",new TNull(String.class));
        result.setData("OUT_ERD_REGION",new TNull(String.class));
        result.setData("REAL_STAY_DAYS",1);//����һ��COPY���ݵ�ERD_RECORD���е�ʱ��Ĭ������-��1��
        result.setData("OUT_DIAG_CODE",new TNull(String.class));
        result.setData("CODE_REMARK",new TNull(String.class));

        result.setData("CODE_STATUS",new TNull(String.class));
        result.setData("GET_TIMES",new TNull(Integer.class));
        result.setData("SUCCESS_TIMES",new TNull(Integer.class));
        result.setData("DR_CODE",getPatRegInfo().getValue("REALDR_CODE",0));
        result.setData("OP_CODE",new TNull(String.class));

        result.setData("OP_DATE",new TNull(Timestamp.class));
        result.setData("MAIN_SUGEON",new TNull(String.class));
        result.setData("OP_LEVEL",new TNull(String.class));
        result.setData("HEAL_LV",new TNull(String.class));
        result.setData("ACCOMPANY_WEEK",new TNull(Integer.class));

        result.setData("ACCOMPANY_MONTH",new TNull(Integer.class));
        result.setData("ACCOMPANY_YEAR",new TNull(Integer.class));
        result.setData("ACCOMP_DATE",new TNull(Timestamp.class));
        result.setData("STATUS",new TNull(String.class));
        result.setData("DISCHG_TYPE",new TNull(String.class));

        result.setData("DISCHG_DATE",new TNull(Timestamp.class));
        result.setData("TRAN_HOSP",new TNull(String.class));
        result.setData("IPD_IN_DEPT",new TNull(String.class));
        result.setData("IPD_IN_DATE",new TNull(Timestamp.class));
        result.setData("OPT_USER",Operator.getID());
        result.setData("OPT_TERM",Operator.getIP());
        return result;
    }


    private TParm getPatHome(String code){
        String sql=" SELECT HOMEPLACE_DESC FROM SYS_HOMEPLACE WHERE HOMEPLACE_CODE='"+code+"'";
        TParm parm = (new TParm(TJDODBTool.getInstance().select(sql)));
        return parm;
    }

    /**
     * �жϸò����Ƿ����м�¼
     */
    private TParm getPatRegInfo(){
        String caseNo=getCaseNo();
        String sql=" SELECT REALDR_CODE,ADM_DATE FROM REG_PATADM WHERE CASE_NO='"+caseNo+"'";
        TParm parm = (new TParm(TJDODBTool.getInstance().select(sql)));
        return parm;
    }

    /**
     * �õ�����Ϊ1��ҳǩ�Ĳ���
     * @return TParm
     */
    private TParm getPage1Data() {

        TParm result = new TParm();
        if (Radio1.isSelected()||Radio2.isSelected()) { //ת��Ժ תסԺ
            result.setData("ADM_STATUS", "9");
            result.setData("STATUS", "9");
        }else{//����
            result.setData("ADM_STATUS", "6");
            result.setData("STATUS", "6");
        }

        result.setData("CASE_NO", getCaseNo());
        result.setData("MR_NO", getMrNo());
        result.setData("ERD_NO", ""); //��ʱ����
        result.setData("PAT_NAME",
                       PAT_NAME.getValue() == null ? new TNull(String.class) :
                       PAT_NAME.getValue());
        result.setData("AGE",
                       AGE.getValue() == null ? new TNull(String.class) :
                       AGE.getValue());

        result.setData("IDNO",
                       IDNO.getValue() == null ? new TNull(String.class) :
                       IDNO.getValue());
        result.setData("MARRIGE",
                       MARRIGE.getValue() == null ? new TNull(String.class) :
                       MARRIGE.getValue());
        result.setData("O_TEL",
                       O_TEL.getValue() == null ? new TNull(String.class) :
                       O_TEL.getValue());
//        result.setData("H_TEL",
//                       H_TEL.getValue() == null ? new TNull(String.class) :
//                       H_TEL.getValue());

        result.setData("O_POSTNO",
                       O_POSTNO.getValue() == null ? new TNull(String.class) :
                       O_POSTNO.getValue());
        result.setData("OFFICE",
                       OFFICE.getValue() == null ? new TNull(String.class) :
                       OFFICE.getValue());

        result.setData("O_ADDRESS",
                       O_ADDRESS.getValue() == null ? new TNull(String.class) :
                       O_ADDRESS.getValue());
        result.setData("H_ADDRESS",
                       H_ADDRESS.getValue() == null ? new TNull(String.class) :
                       H_ADDRESS.getValue());
        result.setData("H_POSTNO",
                       H_POSTNO.getValue() == null ? new TNull(String.class) :
                       H_POSTNO.getValue());
        result.setData("CONTACTER",
                       CONTACTER.getValue() == null ? new TNull(String.class) :
                       CONTACTER.getValue());
        result.setData("CONT_TEL",
                       CONT_TEL.getValue() == null ? new TNull(String.class) :
                       CONT_TEL.getValue());

        result.setData("CONT_ADDRESS",
                       CONT_ADDRESS.getValue() == null ? new TNull(String.class) :
                       CONT_ADDRESS.getValue());
        //�����ؿؼ����õ�
        result.setData("OUT_DIAG_CODE",
                       HIDE_CODE.getValue() == null ? new TNull(String.class) :
                       HIDE_CODE.getValue());
        result.setData("CODE_REMARK",
                       CODE_REMARK.getValue() == null ? new TNull(String.class) :
                       CODE_REMARK.getValue());
        result.setData("CODE_STATUS",
                       CODE_STATUS.getValue() == null ? new TNull(String.class) :
                       CODE_STATUS.getValue());
        result.setData("OP_CODE",
                       OP_CODE.getValue() == null ? new TNull(String.class) :
                       OP_CODE.getValue());

        result.setData("OP_LEVEL",
                       OP_LEVEL.getValue() == null ? new TNull(String.class) :
                       OP_LEVEL.getValue());
        result.setData("HEAL_LV",
                       HEAL_LV.getValue() == null ? new TNull(String.class) :
                       HEAL_LV.getValue());
        result.setData("SEX",
                       SEX.getValue() == null ? new TNull(String.class) :
                       SEX.getValue());
        result.setData("CTZ1_CODE",
                       CTZ1_CODE.getValue() == null ? new TNull(String.class) :
                       CTZ1_CODE.getValue());
        result.setData("OCCUPATION",
                       OCCUPATION.getValue() == null ? new TNull(String.class) :
                       OCCUPATION.getValue());

        result.setData("RESID_PROVICE",
                       RESID_PROVICE.getValue() == null ? new TNull(String.class) :
                       RESID_PROVICE.getValue());
        result.setData("RESID_COUNTRY",
                       RESID_COUNTRY.getValue() == null ? new TNull(String.class) :
                       RESID_COUNTRY.getValue());
        result.setData("FOLK",
                       FOLK.getValue() == null ? new TNull(String.class) :
                       FOLK.getValue());
        result.setData("RELATIONSHIP",
                       RELATIONSHIP.getValue() == null ? new TNull(String.class) :
                       RELATIONSHIP.getValue());
        result.setData("NATION",
                       NATION.getValue() == null ? new TNull(String.class) :
                       NATION.getValue());
        result.setData("ERD_REGION",
                       ERD_REGION.getValue() == null ? new TNull(String.class) :
                       ERD_REGION.getValue());

        result.setData("OUT_ERD_REGION",
                       OUT_ERD_REGION.getValue() == null ? new TNull(String.class) :
                       OUT_ERD_REGION.getValue());
        result.setData("IN_DEPT",
                       IN_DEPT.getValue() == null ? new TNull(String.class) :
                       IN_DEPT.getValue());
        result.setData("OUT_DEPT",
                       OUT_DEPT.getValue() == null ? new TNull(String.class) :
                       OUT_DEPT.getValue());
        result.setData("DR_CODE",
                       DR_CODE.getValue() == null ? new TNull(String.class) :
                       DR_CODE.getValue());
        result.setData("MAIN_SUGEON",
                       MAIN_SUGEON.getValue() == null ? new TNull(String.class) :
                       MAIN_SUGEON.getValue());

        result.setData("REAL_STAY_DAYS",
                       REAL_STAY_DAYS.getValue() == null ? 0.00 :
                       REAL_STAY_DAYS.getValue());
        result.setData("GET_TIMES",
                       GET_TIMES.getValue() == null ? 0.00 :
                       GET_TIMES.getValue());
        result.setData("SUCCESS_TIMES",
                       SUCCESS_TIMES.getValue() == null ? 0.00 :
                       SUCCESS_TIMES.getValue());
        result.setData("ACCOMPANY_WEEK",
                       ACCOMPANY_WEEK.getValue() == null ? 0.00 :
                       ACCOMPANY_WEEK.getValue());
        result.setData("ACCOMPANY_YEAR",
                       ACCOMPANY_YEAR.getValue() == null ? 0.00 :
                       ACCOMPANY_YEAR.getValue());

        result.setData("ACCOMPANY_MONTH",
                       ACCOMPANY_MONTH.getValue() == null ? 0.00 :
                       ACCOMPANY_MONTH.getValue());
        result.setData("BIRTH_DATE",
                       BIRTH_DATE.getValue() == null ? new TNull(Timestamp.class) :
                       BIRTH_DATE.getValue());
        result.setData("IN_DATE",
                       IN_DATE.getValue() == null ? new TNull(Timestamp.class) :
                       IN_DATE.getValue());
        result.setData("OUT_DATE",
                       OUT_DATE.getValue() == null ? new TNull(Timestamp.class) :
                       OUT_DATE.getValue());
        result.setData("OP_DATE",
                       OP_DATE.getValue() == null ? new TNull(Timestamp.class) :
                       OP_DATE.getValue());

        result.setData("ACCOMP_DATE",
                       ACCOMP_DATE.getValue() == null ? new TNull(Timestamp.class) :
                       ACCOMP_DATE.getValue());
        result.setData("DISCHG_TYPE",
                       DISCHG_TYPE.getValue() == null ? new TNull(Timestamp.class) :
                       DISCHG_TYPE.getValue());
        result.setData("IPD_IN_DEPT",
                       IPD_IN_DEPT.getValue() == null ? new TNull(String.class) :
                       IPD_IN_DEPT.getValue());
        result.setData("TRAN_HOSP",
                       TRAN_HOSP.getValue() == null ? new TNull(String.class) :
                       TRAN_HOSP.getValue());

        String dischDateString = DISCHG_DATE_DAY.getText();
        result.setData("DISCHG_DATE",
                       dischDateString.equals("") ? new TNull(Timestamp.class) :
                       StringTool.getTimestamp(dischDateString,
                                               "yyyy/MM/dd HH:mm:ss"));

        String ipdInDateString = IPD_IN_DATE_DAY.getText();
        result.setData("IPD_IN_DATE",
                       ipdInDateString.equals("") ? new TNull(Timestamp.class) :
                       StringTool.getTimestamp(ipdInDateString,
                                               "yyyy/MM/dd HH:mm"));
        TTextFormat RETURN_DATE = (TTextFormat)this.getComponent("RETURN_DATE");
        String returnDate = RETURN_DATE.getText();
        result.setData("RETURN_DATE",returnDate.length() == 0?new TNull(Timestamp.class):getValue("RETURN_DATE"));
        result.setData("OPT_USER", Operator.getID());
        result.setData("OPT_TERM", Operator.getIP());

        return result;
    }


    /**
     * �ر��¼�
     * @return boolean
     */
    public boolean onClosing() {
        switch (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", this.YES_NO_CANCEL_OPTION)) {
            case 0:
                if (!onSave())
                    return false;
                break;
            case 1:
                break;
            case 2:
                return false;
        }
        return true;
    }

    /**
     * ��ʼ���������caseNo/mrNo�����ⲿ����������洫���Ĳ�����
     */
    public void initParmFromOutside() {

        //�Ӳ�����������õ�����TParm
        TParm outsideParm = (TParm)this.getParameter();
        if (outsideParm != null) {
            //������Ų�ѯ��caseNo
            setCaseNo(outsideParm.getValue("CASE_NO"));
            //��������ѯ��mrNo
            setMrNo(outsideParm.getValue("MR_NO"));
            //��������ѯ��mrNo
            setPatName(outsideParm.getValue("PAT_NAME"));
            //��ǣ�NURSE-��ʿ OPD-����ҽ��
            setFlg(outsideParm.getValue("FLG"));
        }

    }

    /**
     * table�ϵ�checkBoxע�����
     * @param obj Object
     */
    public void onTableCheckBoxChangeValue(Object obj) {
        //��õ����table����
        TTable table = (TTable) obj;
        //ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
        table.acceptText();
        TParm tblParm = table.getParmValue();
        int count = tblParm.getCount();
        //���ѡ�е���/��
        int col = table.getSelectedColumn();
        int row = table.getSelectedRow();
        //ѡ�е�ʱ���Զ��������
        if (col == 0) {
           
            boolean flg = TypeTool.getBoolean(table.getValueAt(row, 0));
            //ѭ������Ƿ�����Ѿ��д�����-��ת��
            if (flg) {
                //����ò����Ѿ����ڴ��ϣ������ٱ�������ĵĴ�λ
                if(isExistOfBed()){
                    this.messageBox("�ò������д�λ\n��Ҫת����");
//                    table.setValueAt(false, row, 0);
                    table.setItem(row, 0, false);
                    tblParm.setData("EXE_FLG", row, "N");
                    return;
                }

                for (int i = 0; i < count; i++) {
                    String rowCaseNo = (String) tblParm.getData("CASE_NO", i);
                    if (rowCaseNo != null && rowCaseNo.equals(getCaseNo())) {
                        this.messageBox("�ò������д�λ\n�����ظ����ã�");
//                        table.setValueAt(false, row, 0);
                        tblParm.setData("EXE_FLG", row, "N");
                        table.setItem(row, 0, false);
                        return;
                    }
                }
            }
            else{
            	//====��ѯ�ô�λ�Ƿ��Ѿ��в���
            	String rowRegionCode = (String) tblParm.getData("ERD_REGION_CODE", row);
            	String rowBedNo = (String) tblParm.getData("BED_NO", row);
            	if(isExistOfPat(rowBedNo,rowRegionCode)){
            		this.messageBox("�ô�λ�Ѿ��в�����ס�����ɲ�����");
//            		 table.setValueAt(true, row, 0);
            		 tblParm.setData("EXE_FLG", row, "Y");
            		table.setItem(row, 0, true);
                     return;
            	}
            	else{
            		table.setValueAt("", row, 4);
            		table.setValueAt("", row, 5);
            	}
//                String rowCaseNo = (String) tblParm.getData("CASE_NO", row);
//                if (rowCaseNo != null ){
//                    this.messageBox("�ô�λ���в���");
//                    table.setValueAt(true, row, 0);
//                    return;
//                }
            }
            table.setParmValue(tblParm);
            updateTableParm(flg, row);
        }

    }
    /**
     * ���ò����Ƿ������д�λ�������ظ�����
     */
    private boolean isExistOfBed(){
        String sql=" SELECT COUNT(CASE_NO) AS COUNT FROM ERD_BED "+
            " WHERE CASE_NO='"+ getCaseNo() +"'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return TypeTool.getBoolean(result.getData("COUNT",0));
    }
    
    /**
     * ���ô�λ�Ƿ����Ѿ���ס
     */
    private boolean isExistOfPat(String bedNo,String erdRegionCode){
        String sql=" SELECT OCCUPY_FLG AS COUNT FROM ERD_BED "+
            " WHERE BED_NO='"+ bedNo +"' AND ERD_REGION_CODE = '"+ erdRegionCode +"' ";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return TypeTool.getBoolean(result.getBoolean("COUNT",0));
    }


    /**
     * ���ݹ�ѡ����table�ϵ�parm����
     * @param flg boolean ��ѡ���
     * @param row int �к�
     */
    private void updateTableParm(boolean flg, int row) {
    	table.acceptText();
    	
        TParm parm = table.getParmValue();
        //�Ƿ�ѡ
        if (flg) {
            parm.setData("PAT_NAME", row, getPatName());
            parm.setData("CASE_NO", row, getCaseNo());
            parm.setData("MR_NO", row, getMrNo());
            setBedNo( (String) parm.getData("BED_NO", row));
            setErdregionCode( (String) parm.getData("ERD_REGION_CODE", row));
        }
        else {
            parm.setData("PAT_NAME", row, null);
            parm.setData("CASE_NO", row, null);
            parm.setData("MR_NO", row, null);
            setBedNo(null);
            setErdregionCode(null);
        }
        table.setParmValue(parm);

    }

    /**
     * ���ȵõ�����UI�Ŀؼ�����/ע����Ӧ���¼�
     */
    public void myInitControler() {
        tabPanel = (TTabbedPane)this.getComponent("Tab");
        //�õ�table�ؼ�
        table = (TTable)this.getComponent("TABLE");
        EmptyBed = (TCheckBox)this.getComponent("OCCUPY_FLG");
        ERD_REGION_CODE = (TComboBox)this.getComponent("ERD_REGION_CODE");
        //��дOPD_ORDER
        Radio1 = (TRadioButton)this.getComponent("Radio1"); //��Ժ
        Radio2 = (TRadioButton)this.getComponent("Radio2"); //תסԺ
        //����ERD_RECORD
        PAT_NAME = (TTextField)this.getComponent("PAT_NAME");
        AGE = (TTextField)this.getComponent("AGE");
        IDNO = (TTextField)this.getComponent("IDNO");
        MARRIGE = (TComboBox)this.getComponent("MARRIGE");
        OFFICE = (TTextField)this.getComponent("OFFICE");
        O_TEL = (TTextField)this.getComponent("O_TEL");
        O_POSTNO = (TTextField)this.getComponent("O_POSTNO");
        O_ADDRESS = (TTextField)this.getComponent("O_ADDRESS");
//        H_TEL = (TTextField)this.getComponent("H_TEL");
        H_ADDRESS = (TTextField)this.getComponent("H_ADDRESS");
        H_POSTNO = (TTextField)this.getComponent("H_POSTNO");
        CONTACTER = (TTextField)this.getComponent("CONTACTER");
        CONT_TEL = (TTextField)this.getComponent("CONT_TEL");
        CONT_ADDRESS = (TTextField)this.getComponent("CONT_ADDRESS");
        OUT_DIAG_CODE = (TTextField)this.getComponent("OUT_DIAG_CODE");
        HIDE_CODE= (TTextField)this.getComponent("HIDE_CODE");
        CODE_REMARK = (TTextField)this.getComponent("CODE_REMARK");
        DISCHG_DATE_TIME = (TTextField)this.getComponent("DISCHG_DATE_TIME");
        IPD_IN_DATE_TIME = (TTextField)this.getComponent("IPD_IN_DATE_TIME");

        SEX = (TComboBox)this.getComponent("SEX");
        CTZ1_CODE = (TComboBox)this.getComponent("CTZ1_CODE");
        CODE_STATUS = (TComboBox)this.getComponent("CODE_STATUS");
        HEAL_LV = (TTextFormat)this.getComponent("HEAL_LV");
        OP_CODE = (TTextFormat)this.getComponent("OP_CODE");
        OP_LEVEL = (TComboBox)this.getComponent("OP_LEVEL");
        OCCUPATION = (TTextFormat)this.getComponent("OCCUPATION");
        RESID_PROVICE = (TTextField)this.getComponent("RESID_PROVICE");
        RESID_COUNTRY = (TTextFormat)this.getComponent("RESID_COUNTRY");
        FOLK = (TTextFormat)this.getComponent("FOLK");
        RELATIONSHIP = (TTextFormat)this.getComponent("RELATIONSHIP");
        NATION = (TTextFormat)this.getComponent("NATION");
        ERD_REGION = (TTextFormat)this.getComponent("ERD_REGION");
        OUT_ERD_REGION = (TTextFormat)this.getComponent("OUT_ERD_REGION");
        IN_DEPT = (TTextFormat)this.getComponent("IN_DEPT");
        OUT_DEPT = (TTextFormat)this.getComponent("OUT_DEPT");
        DR_CODE = (TTextFormat)this.getComponent("DR_CODE");
        MAIN_SUGEON = (TTextFormat)this.getComponent("MAIN_SUGEON");
        DISCHG_TYPE = (TComboBox)this.getComponent("DISCHG_TYPE");
        IPD_IN_DEPT = (TTextFormat)this.getComponent("IPD_IN_DEPT");
        TRAN_HOSP = (TComboBox)this.getComponent("TRAN_HOSP");

        REAL_STAY_DAYS = (TNumberTextField)this.getComponent("REAL_STAY_DAYS");
        GET_TIMES = (TNumberTextField)this.getComponent("GET_TIMES");
        SUCCESS_TIMES = (TNumberTextField)this.getComponent("SUCCESS_TIMES");
        ACCOMPANY_WEEK = (TNumberTextField)this.getComponent("ACCOMPANY_WEEK");
        ACCOMPANY_MONTH = (TNumberTextField)this.getComponent("ACCOMPANY_MONTH");
        ACCOMPANY_YEAR = (TNumberTextField)this.getComponent("ACCOMPANY_YEAR");

        BIRTH_DATE = (TTextFormat)this.getComponent("BIRTH_DATE");
        IN_DATE = (TTextFormat)this.getComponent("IN_DATE");
        OUT_DATE = (TTextFormat)this.getComponent("OUT_DATE");
        OP_DATE = (TTextFormat)this.getComponent("OP_DATE");
        ACCOMP_DATE = (TTextFormat)this.getComponent("ACCOMP_DATE");
        DISCHG_DATE_DAY = (TTextFormat)this.getComponent("DISCHG_DATE_DAY");
        IPD_IN_DATE_DAY = (TTextFormat)this.getComponent("IPD_IN_DATE_DAY");

        //��tableע��CHECK_BOX_CLICKED��������¼�
        this.callFunction("UI|TABLE|addEventListener",
                          TTableEvent.CHECK_BOX_CLICKED, this,
                          "onTableCheckBoxChangeValue");
        // ���õ����˵�
        OUT_DIAG_CODE.setPopupMenuParameter("",getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSICDPopup.x"));
        // ������ܷ���ֵ����
        OUT_DIAG_CODE.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");


        //����Ǽ���ҽ����ֻ��ʾ�ڶ���ҳǩ
        if("OPD".equals(this.getFlg())){
            if(checkPatExist(this.getCaseNo())){
                tabPanel.setSelectedIndex(1);
            }else{
            	 tabPanel.setSelectedIndex(0);
//                this.messageBox("���Ȱ������ۣ�");
            }
//            tabPanel.setEnabledAt(0,false);
        }
        setContrlValue();
    }

    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String icd_desc = parm.getValue("ICD_CHN_DESC");
        String icd_code = parm.getValue("ICD_CODE");
        OUT_DIAG_CODE.setValue("");
        HIDE_CODE.setValue("");
        if (!StringUtil.isNullString(icd_code)){
            HIDE_CODE.setValue(icd_code);
            OUT_DIAG_CODE.setValue(icd_desc);
        }
    }


    /**
     * �жϸò����Ƿ����м�¼
     */
    private TParm getPatInfo(){
        String caseNo=this.getCaseNo();
        String sql="SELECT * FROM ERD_RECORD WHERE CASE_NO='"+caseNo+"'";
        return (new TParm(TJDODBTool.getInstance().select(sql)));
    }

    /**
     * ���ݲ��˵���Ϣ��ʼ���ؼ�
     */
    private void setContrlValue(){
        TParm pat=getPatInfo();
        PAT_NAME.setValue((String)pat.getData("PAT_NAME",0));
        AGE.setValue((String)pat.getData("AGE",0));
        IDNO.setValue((String)pat.getData("IDNO",0));
        MARRIGE.setValue((String)pat.getData("MARRIGE",0));
        OFFICE.setValue((String)pat.getData("OFFICE",0));
        O_TEL.setValue((String)pat.getData("O_TEL",0));
        O_POSTNO.setValue((String)pat.getData("O_POSTNO",0));
        O_ADDRESS.setValue((String)pat.getData("O_ADDRESS",0));
        H_ADDRESS.setValue((String)pat.getData("H_ADDRESS",0));
        H_POSTNO.setValue((String)pat.getData("H_POSTNO",0));
        CONTACTER.setValue((String)pat.getData("CONTACTER",0));
        CONT_TEL.setValue((String)pat.getData("CONT_TEL",0));
        CONT_ADDRESS.setValue((String)pat.getData("CONT_ADDRESS",0));
        HIDE_CODE.setValue((String)pat.getData("OUT_DIAG_CODE",0));
        if(!"".equals(HIDE_CODE.getText())){
            String sql =
                " SELECT ICD_CHN_DESC FROM  SYS_DIAGNOSIS WHERE ICD_CODE='" +
                HIDE_CODE.getText()+ "'";
            TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
            OUT_DIAG_CODE.setValue(parm.getValue("ICD_CHN_DESC",0));
        }else
            OUT_DIAG_CODE.setValue("");
        CODE_REMARK.setValue((String)pat.getData("CODE_REMARK",0));
        CODE_STATUS.setValue((String)pat.getData("CODE_STATUS",0));
        OP_CODE.setValue((String)pat.getData("OP_CODE",0));
        OP_LEVEL.setValue((String)pat.getData("OP_LEVEL",0));
        HEAL_LV.setValue((String)pat.getData("HEAL_LV",0));
        DISCHG_DATE_TIME.setValue((String)pat.getData("DISCHG_DATE_TIME",0));
        IPD_IN_DATE_TIME.setValue((String)pat.getData("IPD_IN_DATE_TIME",0));
        SEX.setValue((String)pat.getData("SEX",0));
        CTZ1_CODE.setValue((String)pat.getData("CTZ1_CODE",0));
        OCCUPATION.setValue((String)pat.getData("OCCUPATION",0));
        RESID_PROVICE.setValue((String)pat.getData("RESID_PROVICE",0));
        setValue("RESID_PROVICE_DESC",getPatHome(getValueString("RESID_PROVICE")).getData("HOMEPLACE_DESC",0));
        RESID_COUNTRY.setValue((String)pat.getData("RESID_COUNTRY",0));
        FOLK.setValue((String)pat.getData("FOLK",0));
        RELATIONSHIP.setValue((String)pat.getData("RELATIONSHIP",0));
        NATION.setValue((String)pat.getData("NATION",0));
        ERD_REGION.setValue((String)pat.getData("ERD_REGION",0));
        OUT_ERD_REGION.setValue((String)pat.getData("OUT_ERD_REGION",0));
        IN_DEPT.setValue((String)pat.getData("IN_DEPT",0));
        OUT_DEPT.setValue((String)pat.getData("OUT_DEPT",0));
        DR_CODE.setValue((String)pat.getData("DR_CODE",0));
        MAIN_SUGEON.setValue((String)pat.getData("MAIN_SUGEON",0));
        DISCHG_TYPE.setValue((String)pat.getData("DISCHG_TYPE",0));
        IPD_IN_DEPT.setValue((String)pat.getData("IPD_IN_DEPT",0));
        TRAN_HOSP.setValue((String)pat.getData("TRAN_HOSP",0));
        //����ʵ������������δ��->��ǰʱ��-�������գ��ѳ�->��Ժʱ��-��������
        REAL_STAY_DAYS.setValue(getRealInDays(pat.getData("OUT_DATE",0)+"",pat));
        GET_TIMES.setValue(pat.getData("GET_TIMES",0)+"");
        SUCCESS_TIMES.setValue(pat.getData("SUCCESS_TIMES",0)+"");
        ACCOMPANY_WEEK.setValue(pat.getData("ACCOMPANY_WEEK",0)+"");
        ACCOMPANY_MONTH.setValue(pat.getData("ACCOMPANY_MONTH",0)+"");
        ACCOMPANY_YEAR.setValue(pat.getData("ACCOMPANY_YEAR",0)+"");
        BIRTH_DATE.setValue((Timestamp)pat.getData("BIRTH_DATE",0));
        IN_DATE.setValue((Timestamp)pat.getData("IN_DATE",0));
        OUT_DATE.setValue((Timestamp)pat.getData("OUT_DATE",0));
        OP_DATE.setValue((Timestamp)pat.getData("OP_DATE",0));
        ACCOMP_DATE.setValue((Timestamp)pat.getData("ACCOMP_DATE",0));
        DISCHG_DATE_DAY.setValue((Timestamp)pat.getData("DISCHG_DATE",0));
        IPD_IN_DATE_DAY.setValue((Timestamp)pat.getData("IPD_IN_DATE",0));
        setValue("RETURN_DATE",pat.getData("RETURN_DATE",0));
    }



    /**
     * �жϸò����Ƿ��Ѿ�������--���뻹�Ǹ���
     * @param caseNo String
     * @return boolean ����--true ������--false
     */
    private boolean checkPatExist(String caseNo){
        String sqlSel=" SELECT COUNT(CASE_NO) AS COUNT FROM ERD_RECORD "+
            "WHERE CASE_NO="+caseNo;
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlSel));
        return TypeTool.getBoolean(result.getData("COUNT",0));
    }

    /**
     * �ò����Ƿ�Ҫת������
     * @return boolean
     */
    private boolean ifOutErd(){
        return Radio1.isSelected()||Radio2.isSelected();
    }

    public void onEmptyBed() {
        onQuery();
    }

    /**
     * �л�Tabҳ��ʱ����
     */
    public void onChangeTab(){
        int selIndex=tabPanel.getSelectedIndex();
        //��һҳΪ��¼
        if(1==selIndex){
            setContrlValue();
        }
    }
    /**
     * �õ�ʵ����������
     * @param startDate Timestamp
     * @param endDate Timestamp
     * @return int
     */
    private int getRealInDays(String outDate, TParm data) {
        Timestamp endDate = TJDODBTool.getInstance().getDBTime();
        //��û����Ժ�յ�ʱ��IN_DATE=����
        Timestamp startDate = data.getTimestamp("IN_DATE", 0) == null ?
            endDate : data.getTimestamp("IN_DATE", 0);
        //��û��ת��ס��ǵĵ�ʱ��
        if (!(outDate.trim().length()==0||"null".equals(outDate))) {
            endDate = data.getTimestamp("OUT_DATE", 0);
        }
        int diff=StringTool.getDateDiffer(endDate, startDate);
        return diff;
    }



    public String getCaseNo() {
        return caseNo;
    }

    public String getMrNo() {
        return mrNo;
    }

    public String getFlg() {
        return flg;
    }

    public String getPatName() {
        return patName;
    }

    public String getBedNo() {
        return bedNo;
    }

    public String getErdregionCode() {
        return erdregionCode;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    public void setFlg(String flg) {
        this.flg = flg;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    public void setBedNo(String bedNo) {
        this.bedNo = bedNo;
    }

    public void setErdregionCode(String erdregionCode) {
        this.erdregionCode = erdregionCode;
    }

    /**
     * ��ʾ�˵�
     */
    public void onShowWindowsFunction(){
        callFunction("UI|showTopMenu");
    }

    public void onOutInReturn(String status){
        if(status.equals("Out")){
            IPD_IN_DEPT.setValue("");
            IPD_IN_DATE_DAY.setValue("");
            IPD_IN_DEPT.setEnabled(false);
            IPD_IN_DATE_DAY.setEnabled(false);
            DISCHG_TYPE.setValue("");
            DISCHG_DATE_DAY.setValue("");
            TRAN_HOSP.setValue("");
            DISCHG_TYPE.setEnabled(true);
            DISCHG_DATE_DAY.setEnabled(true);
            TRAN_HOSP.setEnabled(true);
            ((TTextFormat)this.getComponent("RETURN_DATE")).setValue("");
            ((TTextFormat)this.getComponent("RETURN_DATE")).setEnabled(false);
        }
        else if(status.equals("In")){
            IPD_IN_DEPT.setValue("");
            IPD_IN_DATE_DAY.setValue("");
            IPD_IN_DEPT.setEnabled(true);
            IPD_IN_DATE_DAY.setEnabled(true);
            DISCHG_TYPE.setValue("");
            DISCHG_DATE_DAY.setValue("");
            TRAN_HOSP.setValue("");
            DISCHG_TYPE.setEnabled(false);
            DISCHG_DATE_DAY.setEnabled(false);
            TRAN_HOSP.setEnabled(false);
            ((TTextFormat)this.getComponent("RETURN_DATE")).setValue("");
            ((TTextFormat)this.getComponent("RETURN_DATE")).setEnabled(false);
        }
        else if(status.equals("Return")){
            IPD_IN_DEPT.setValue("");
            IPD_IN_DATE_DAY.setValue("");
            IPD_IN_DEPT.setEnabled(false);
            IPD_IN_DATE_DAY.setEnabled(false);
            DISCHG_TYPE.setValue("");
            DISCHG_DATE_DAY.setValue("");
            TRAN_HOSP.setValue("");
            DISCHG_TYPE.setEnabled(false);
            DISCHG_DATE_DAY.setEnabled(false);
            TRAN_HOSP.setEnabled(false);
            ((TTextFormat)this.getComponent("RETURN_DATE")).setValue("");
            ((TTextFormat)this.getComponent("RETURN_DATE")).setEnabled(true);
        }
    }

    /**
         * ������������
         */
        public void setACCOMP_DATE(){
            //���������ֹ����
            if (this.getValue("OUT_DATE") != null) { //�жϳ�Ժ�����Ƿ�Ϊ�գ������ֹ�������Գ�Ժ����Ϊ��ʼ��ʼ����ģ���Ҫѯ��һ�£�
                int s_year = this.getValueInt("ACCOMPANY_YEAR"); //��������
                int s_month = this.getValueInt("ACCOMPANY_MONTH"); //����
                int s_week = this.getValueInt("ACCOMPANY_WEEK"); //����

                Timestamp accomp_date = StringTool.getTimestamp(this.getValue(
                    "OUT_DATE").toString(), "yyyy-MM-dd"); //��ȡ��Ժ����
                if (s_week > 0) { //��Ժ���ڼ�������
                    accomp_date = StringTool.rollDate(accomp_date,
                                                      (long) (7 * s_week));
                }
                if (s_month > 0) { //������������������򽫳�Ժ���ڼ������� ����������ֹ����
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(accomp_date);
                    cal.add(cal.MONTH, s_month);
                    accomp_date = new Timestamp(cal.getTimeInMillis()); //����������� �����ֹ����
                }
                if (s_year > 0) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(accomp_date);
                    cal.add(cal.YEAR, s_year);
                    accomp_date = new Timestamp(cal.getTimeInMillis()); //����������� �����ֹ����
                }
                if (s_week > 0 || s_month > 0 || s_year > 0)
                    this.setValue("ACCOMP_DATE", accomp_date);
                else
                    this.setValue("ACCOMP_DATE", new TNull(Timestamp.class));
            }
            else //û�г�Ժ���ڣ��������ھ�Ϊ��
                this.setValue("ACCOMP_DATE", new TNull(Timestamp.class));
    }
    //��������
    public static void main(String[] args) {
        JavaHisDebug.initClient();
        //JavaHisDebug.TBuilder();

//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("erd\\ERDDynamicRcd.x");
    }


}
