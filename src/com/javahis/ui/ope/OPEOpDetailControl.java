package com.javahis.ui.ope;

import com.dongyang.control.*;
import jdo.sys.Pat;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;

import java.awt.Component;
import java.util.Vector;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TLabel;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import java.sql.Timestamp;
import jdo.ope.OPEOpBookTool;
import jdo.ope.OPEOpDetailTool;
import jdo.sys.SystemTool;
import com.javahis.util.StringUtil;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.config.TConfig;
import com.javahis.util.EmrUtil;
import java.util.List;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import jdo.adm.ADMInpTool;

/**
 * <p>Title: ������¼</p>
 *
 * <p>Description: ������¼</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-28
 * @version 1.0
 */
public class OPEOpDetailControl
    extends TControl {
    private String OP_RECORD_NO = "";//������¼����
    private String OPBOOK_SEQ = "";//����������
    private String SAVE_FLG = "new";//���淽ʽ new:�½�  update:�޸�
    private String MR_NO = "";//MR_NO
    private String CASE_NO = "";//CASE_NO
    private String IPD_NO = "";//סԺ�� סԺҽ��վ���õ�ʱ�� ��Ҫ���˲���
    private TTable EXTRA_TABLE; //����ѭ��ʦ���
    private TTable CIRCULE_TABLE; //ѭ����ʿ���
    private TTable SCRUB_TABLE; //ϴ�ֻ�ʿ���
    private TTable ANA_TABLE; //����ҽʦ���
    private TParm eventParmEmr;//�ṹ���������صķ����б�
    private TTable Daily_Table ;
    private TTable OP_Table;
    private TParm DetailData;//������¼��Ϣ
    private String ADM_TYPE = "";//�ż�ס��
    private String SYSTEM = "";//��¼���ĸ�ϵͳ����
    private Pat pat;//==========pangben modify 20110701 ������Ϣ
    public void onInit() {
        super.onInit();
        Daily_Table = (TTable)this.getComponent("Daily_Table");
        OP_Table = (TTable)this.getComponent("OP_Table");
        EXTRA_TABLE = (TTable)this.getComponent("EXTRA_TABLE");
        CIRCULE_TABLE = (TTable)this.getComponent("CIRCULE_TABLE");
        SCRUB_TABLE = (TTable)this.getComponent("SCRUB_TABLE");
        ANA_TABLE = (TTable)this.getComponent("ANA_TABLE");
        ANA_TABLE.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                   "onANATableMainCharge");
        PageParmInit();
        TableInit();
    }
    /**
     * ҳ�������ʼ��
     */
    private void PageParmInit(){
        //ģ�����
//        TParm parmR = new TParm();
//        parmR.setData("SYSTEM","OPD");//����ϵͳ���
//        parmR.setData("ADM_TYPE","O");
//        parmR.setData("CASE_NO","100307000010");
//        parmR.setData("MR_NO","000000000579");
        //�Ӳ�  ����ԤԼ����
        Object obj = this.getParameter();
//        Object obj = parmR;
        TParm parmObj = new TParm();
        if(obj instanceof TParm){
            parmObj = (TParm)obj;
        }else{
            return;
        }
        MR_NO = parmObj.getValue("MR_NO");
        //��������� �С�SYSTEM���� ��ʾ��ҽ��վ���õ�
        SYSTEM = parmObj.getValue("SYSTEM");
        ADM_TYPE = parmObj.getValue("ADM_TYPE");
        //�ж��ż�ס�� ��ʾ�������߲���
        TLabel tLabel_9 = (TLabel)this.getComponent("tLabel_9");
        if(ADM_TYPE.equals("O")||ADM_TYPE.equals("E")){
            tLabel_9.setZhText("��������");
            this.callFunction("UI|OP_STATION_CODE_I|setVisible", false);
            this.callFunction("UI|OP_STATION_CODE_O|setVisible", true);
        }else if(ADM_TYPE.equals("I")){
            tLabel_9.setZhText("��������");
            this.callFunction("UI|OP_STATION_CODE_I|setVisible", true);
            this.callFunction("UI|OP_STATION_CODE_O|setVisible", false);
        }
        if(SYSTEM.length()>0){
            this.setValue("MR_NO",MR_NO);
            CASE_NO = parmObj.getValue("CASE_NO");
            onMR_NO();
        }else{
            OPBOOK_SEQ = parmObj.getValue("OPBOOK_SEQ");
            if(parmObj.getValue("EDITABLE").equals("FALSE")){//add by wanglong 20121219
            	setOpBookData();
            	setUIFalse();
            } else//add by wanglong 20121219
            selectOpBook(OPBOOK_SEQ);
        }
    }

    /**
     * Table��ʼ��
     */
    public void TableInit() {
        //����Table ����
        OP_Table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                  "onCreateEditComponentOP");
        //�������ı��¼�
        OP_Table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                  "onOpTableMainCharge");
        OpList opList = new OpList();
        OP_Table.addItem("OpList", opList);
        //���Table����
        Daily_Table = (TTable)this.getComponent("Daily_Table");
        Daily_Table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                     "onCreateEditComponent");
        //����ϸı��¼�
        Daily_Table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                     "onDiagTableMainCharge");
        OrderList orderList = new OrderList();
        Daily_Table.addItem("OrderList", orderList);
//        //���Gridֵ�ı��¼�
        this.addEventListener("Daily_Table->" + TTableEvent.CHANGE_VALUE,
                              "onDiagTableValueCharge");

        //����Gridֵ�ı��¼�
        this.addEventListener("OP_Table->" + TTableEvent.CHANGE_VALUE,
                              "onOpTableValueCharge");
    }
    /**
     * ����
     */
    public void onSave(){
        delTable();
        if(!checkData())
            return;
        //û��ԤԼ���� �����б���
        if(OPBOOK_SEQ.length()<=0){
            return;
        }
        if("new".equals(SAVE_FLG)){
            insert();
        }else if("update".equals(SAVE_FLG)){
            update();
        }
    }
    /**
     * ����������¼
     */
    private void insert() {
        //������¼��
        OP_RECORD_NO = SystemTool.getInstance().getNo("ALL", "OPE", "OP_RECORD_NO",
                                                "OP_RECORD_NO"); //����ȡ��ԭ��
        TParm parm = this.getSaveData();
        TParm result = TIOM_AppServer.executeAction(
            "action.ope.OPEDetailAction",
            "insertData", parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        this.messageBox("P0005");
    }
    /**
     * �޸�������¼
     */
    private void update(){
        TParm updateData = getSaveData();
        TParm result = OPEOpDetailTool.getInstance().updateData(updateData);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        this.messageBox("P0005");
    }
    /**
     * ���ò���������Ϣ
     * @param pat Pat
     */
    private void setPatInfo(Pat pat){
        this.setValue("MR_NO",pat.getMrNo());
        if("en".equals(this.getLanguage())){
            this.setValue("PAT_NAME",pat.getName1());//����
            //��������
            String[] res = StringTool.CountAgeByTimestamp(pat.getBirthday(),SystemTool.getInstance().getDate());
            this.setValue("AGE",res[0]+"Y");
        }else{
            this.setValue("PAT_NAME",pat.getName());//����
            this.setValue("AGE",
                      StringUtil.getInstance().showAge(pat.getBirthday(),
            SystemTool.getInstance().getDate()));//����
        }
        this.setValue("SEX",pat.getSexCode());
        TParm patParm = pat.getParm();
        patParm.setData("CASE_NO", StringUtil.getDesc("OPE_OPBOOK", "CASE_NO", "OPBOOK_SEQ='" + OPBOOK_SEQ + "'"));
        TParm admParm = ADMInpTool.getInstance().selectall(patParm);// wanglong add 20141011
        double weight = admParm.getDouble("WEIGHT", 0);
//        this.setValue("Weight", pat.getWeight());
        this.setValue("Weight", weight + "");
    }
    /**
     * ����ҳ����Ϣ ��ȡ������Ϣ
     */
    private TParm getSaveData() {
        TParm parm = new TParm();
        parm.setData("OPBOOK_NO",OPBOOK_SEQ);//����������
        parm.setData("OP_RECORD_NO",OP_RECORD_NO);//������¼���
        parm.setData("ADM_TYPE",this.getValue("ADM_TYPE"));
        parm.setData("MR_NO",MR_NO);
        parm.setData("IPD_NO",IPD_NO);
        parm.setData("CASE_NO",CASE_NO);
        parm.setData("BED_NO",this.getValue("BED_NO"));
        parm.setData("URGBLADE_FLG",this.getValueString("URGBLADE_FLG"));//�����������
        parm.setData("OP_DATE",StringTool.getString((Timestamp)this.getValue("OP_DATE"),"yyyyMMddHHmmss"));//����ʱ��
        parm.setData("TIME_NEED",this.getValue("TIME_NEED"));//����ʱ��
        parm.setData("ROOM_NO",this.getValue("ROOM_NO"));//������
        parm.setData("TYPE_CODE",this.getValue("TYPE_CODE"));//��������
        parm.setData("ANA_CODE",this.getValue("ANA_CODE"));//����ʽ
        parm.setData("ASA_CODE",this.getValueString("ASA_CODE"));//����ּ�   add by wanglong 20121206
        parm.setData("NNIS_CODE",this.getValueString("NNIS_CODE"));//�������շּ�   add by wanglong 20121206
        parm.setData("PART_CODE",this.getValueString("PART_CODE"));//������λ   add by wanglong 20121206
        parm.setData("ISO_FLG",this.getValueString("ISO_FLG"));//�����������   add by wanglong 20121206
        parm.setData("STERILE_FLG",this.getValueString("STERILE_FLG"));//�޾���� add by huangjw 20141016
        parm.setData("MIRROR_FLG",this.getValueString("MIRROR_FLG"));//ǻ�����add by huangjw 20141016
        parm.setData("OP_DEPT_CODE",this.getValue("OP_DEPT_CODE"));//��������
        if(ADM_TYPE.equals("I"))
            parm.setData("OP_STATION_CODE",this.getValue("OP_STATION_CODE_I"));//��������
        else if(ADM_TYPE.equals("O")||ADM_TYPE.equals("E"))
            parm.setData("OP_STATION_CODE",this.getValue("OP_STATION_CODE_O"));//��������
        parm.setData("RANK_CODE",this.getValue("RANK_CODE"));//�����ȼ�
        parm.setData("WAY_CODE",this.getValue("WAY_CODE"));//��������
        TParm Daily_Data = this.getDailyData();//��ȡ�����Ϣ
        parm.setData("DIAG_CODE1",Daily_Data.getValue("DIAG_CODE1"));
        parm.setData("DIAG_CODE2",Daily_Data.getValue("DIAG_CODE2"));
        parm.setData("DIAG_CODE3",Daily_Data.getValue("DIAG_CODE3"));
        parm.setData("BOOK_DEPT_CODE",this.getValue("BOOK_DEPT_CODE"));//ԤԼ����
        TParm Op_Data = this.getOpData();//��ȡ������Ϣ
        parm.setData("OP_CODE1",Op_Data.getValue("OP_CODE1"));
        parm.setData("OP_CODE2",Op_Data.getValue("OP_CODE2"));
        parm.setData("BOOK_DR_CODE",this.getValueString("BOOK_DR_CODE"));//ԤԼҽʦ
        parm.setData("MAIN_SURGEON",this.getValueString("MAIN_SURGEON"));//����ҽʦ
        parm.setData("REAL_AST1",this.getValueString("REAL_AST1"));
        parm.setData("REAL_AST2",this.getValueString("REAL_AST2"));
        parm.setData("REAL_AST3",this.getValueString("REAL_AST3"));
        parm.setData("REAL_AST4",this.getValueString("REAL_AST4"));
        //������ʿ
        TParm CIRCULE = this.getCIRCULEData();
        parm.setData("CIRCULE_USER1",CIRCULE.getValue("CIRCULE_USER1"));
        parm.setData("CIRCULE_USER2",CIRCULE.getValue("CIRCULE_USER2"));
        parm.setData("CIRCULE_USER3",CIRCULE.getValue("CIRCULE_USER3"));
        parm.setData("CIRCULE_USER4",CIRCULE.getValue("CIRCULE_USER4"));
        //ˢ�ֻ�ʿ
        TParm SCRUB = this.getSCRUBData();
        parm.setData("SCRUB_USER1",SCRUB.getValue("SCRUB_USER1"));
        parm.setData("SCRUB_USER2",SCRUB.getValue("SCRUB_USER2"));
        parm.setData("SCRUB_USER3",SCRUB.getValue("SCRUB_USER3"));
        parm.setData("SCRUB_USER4",SCRUB.getValue("SCRUB_USER4"));
        //����ҽʦ
        TParm ANA = this.getANAData();
        parm.setData("ANA_USER1",ANA.getValue("ANA_USER1"));
        parm.setData("ANA_USER2",ANA.getValue("ANA_USER2"));
        //����ѭ��ʦ
        TParm EXTRA = this.getEXTRAData();
        parm.setData("EXTRA_USER1",EXTRA.getValue("EXTRA_USER1"));
        parm.setData("EXTRA_USER2",EXTRA.getValue("EXTRA_USER2"));
        parm.setData("DRG_CODE","");//DRG���� ��������
        parm.setData("NURSE_START_DATE","");//������ʱ��������
        parm.setData("ENTER_DATE","");//����ʱ�䣨������
        //�����µ�ʱ�䣨��������ʱ��OP_DATE����һ��  modify by wanglong 20121219
        parm.setData("OP_START_DATE",StringTool.getString((Timestamp)this.getValue("OP_DATE"),"yyyyMMddHHmmss"));
        //�������ʱ�䣨������ modify by wanglong 20121219
        parm.setData("OP_END_DATE",StringTool.getString((Timestamp)this.getValue("OP_END_DATE"),"yyyyMMddHHmmss"));
        parm.setData("NURSE_END_DATE","");//������ʱ��������
        parm.setData("EXIT_DATE","");//�Ƴ�ʱ�䣨������
        parm.setData("BIOPSY_FLG","");//��Ƭע�ǣ�������
        parm.setData("BILL_FLG","");//������ע�ǣ�������
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        return parm;
    }
    /**
     * ��ȡ�������
     */
    private TParm getDailyData(){
        TParm parm = new TParm();
        int index = 2;//����� ��2��Ϊ��ʼֵ ��Ϊ�������1
        for(int i=0;i<Daily_Table.getRowCount();i++){
            if(Daily_Table.getValueAt(i,2).toString().trim().length()>0){
                //�ж������
                if("Y".equals(Daily_Table.getValueAt(i,1).toString())){
                    parm.setData("DIAG_CODE1",Daily_Table.getValueAt(i,2));
                }else{
                    parm.setData("DIAG_CODE"+index,Daily_Table.getValueAt(i,2));
                    index++;
                }
            }
        }
        return parm;
    }
    /**
     * ��ȡ������ʽ����
     * @return TParm
     */
    private TParm getOpData(){
        TParm parm = new TParm();
        for(int i=0;i<OP_Table.getRowCount();i++){
            if(OP_Table.getValueAt(i,2).toString().trim().length()>0){
                //�ж������
                if("Y".equals(OP_Table.getValueAt(i,1))){
                    parm.setData("OP_CODE1",OP_Table.getValueAt(i,2));
                }else{
                    parm.setData("OP_CODE2",OP_Table.getValueAt(i,2));
                }
            }
        }
        return parm;
    }

    /**
     * ɾ����Ϻ�����������Ϣ����ѡɾ����ǵģ�
     */
    private void delTable(){
        OP_Table.acceptText();
        Daily_Table.acceptText();
        for(int i=OP_Table.getRowCount()-1;i>=0;i--){
            if("Y".equals(OP_Table.getValueAt(i,0))){
                OP_Table.removeRow(i);
            }
        }
        for(int i=Daily_Table.getRowCount()-1;i>=0;i--){
            if("Y".equals(Daily_Table.getValueAt(i,0))){
                Daily_Table.removeRow(i);
            }
        }
    }

    /**
     * �������
     */
    private boolean checkData(){
        if("".equals(this.getValueString("ADM_TYPE"))){
            this.messageBox("E0075");
            this.grabFocus("ADM_TYPE");
            return false;
        }
        if(this.getValue("OP_DATE")==null){
            this.messageBox("E0076");//����д��������
            this.grabFocus("OP_DATE");
            return false;
        }
        if(this.getValue("OP_END_DATE")==null){// add by wanglong 20121219
            this.messageBox("����д������������");
            this.grabFocus("OP_END_DATE");
            return false;
        }
        if("".equals(this.getValueString("OP_DEPT_CODE"))){
            this.messageBox("E0077");//��ѡ����������
            this.grabFocus("OP_DEPT_CODE");
            return false;
        }
        
        //У���������� add by huangjw 20141105
        if("".equals(this.getValueString("TYPE_CODE"))){
        	this.messageBox("����д��������");
        	this.grabFocus("TYPE_CODE");
        	return false;
        }
        OP_Table.acceptText();
        Daily_Table.acceptText();
        boolean flg = false;//����ϱ�ʶ true:��������ϣ��������� false:�����������(������)
        for(int i=0;i<OP_Table.getRowCount();i++){
            if("Y".equals(OP_Table.getValueAt(i,1))){
                flg = true;
            }
        }
        if(!flg){
            this.messageBox("E0078");//��ѡ��һ������������Ϊ������
            return flg;
        }
        flg = false;
        for(int i=0;i<Daily_Table.getRowCount();i++){
            if("Y".equals(Daily_Table.getValueAt(i,1))){
                flg = true;
            }
        }
        if(!flg){
            this.messageBox("E0079");
            return flg;
        }
        //�ж��Ƿ�ѡ��������ҽʦ
        flg = false;
        for(int i=0;i<ANA_TABLE.getRowCount();i++){
            if ("Y".equals(ANA_TABLE.getValueAt(i,0).toString())){
                flg = true;
            }
        }
        if(!flg){
            this.messageBox("E0080");
            return flg;
        }
        return flg;
    }
    /**
     * �����Żس��¼�
     */
    public void onMR_NO(){
        pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
        if(pat==null){
            this.messageBox("E0081");
            return;
        }
        MR_NO = pat.getMrNo();
        this.setValue("MR_NO",MR_NO);
        TParm opParm = new TParm();
        opParm.setData("MR_NO",MR_NO);
        if(SYSTEM.length()>0){
            opParm.setData("CASE_NO",CASE_NO);
        }
        //��ѯ����ԤԼ��Ϣ
        //===============pangben modify 20110630 start
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            opParm.setData("REGION_CODE", Operator.getRegion());
        }
        //=============pangben modify 20110630 stop

        TParm opBook = OPEOpBookTool.getInstance().selectOpBook(opParm);
        //�ж��Ƿ��������ԤԼ��Ϣ
        int opBookCount = opBook.getCount();
        if(opBookCount>0){
            TParm parm = new TParm();
            parm.setData("MR_NO",MR_NO);
            if (SYSTEM.length() > 0) {
                parm.setData("CASE_NO", CASE_NO);
            }
            Object obj = this.openDialog("%ROOT%/config/ope/OPEOpBookChoose.x",parm);
            TParm re = new TParm();
            if(obj instanceof TParm){
                re = (TParm)obj;
            }else {
                this.setValue("MR_NO","");
                return;
            }
            SAVE_FLG = re.getValue("FLG");//����״̬  new:�½� update:�޸�
            OPBOOK_SEQ = re.getValue("OPBOOK_SEQ");
            this.setValue("OPBOOK_NO",OPBOOK_SEQ);
            if("new".equals(SAVE_FLG)){
                setOpBookData();//��ȡԤԼ������Ϣ����ֵ
            }
            if("update".equals(SAVE_FLG)){
                OP_RECORD_NO = re.getValue("OP_RECORD_NO");
                setOpRecordData();
            }
            if ("close".equals(SAVE_FLG)) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            closeWindow();
                        }
                        catch (Exception e) {
                        }
                    }
                });
            }
            callFunction("UI|MR_NO|setEnabled", false);
            callFunction("UI|OPBOOK_NO|setEnabled", false);
            setPatInfo(pat);
        }else{
            this.messageBox("E0082");
            return;
        }
    }
    /**
     * ԤԼ�Żس��¼�
     */
    public void onOPBOOK_NO(){
        String seq = this.getValueString("OPBOOK_NO");
        this.selectOpBook(seq);
    }
    /**
     * ���� ԤԼ�� ��ѯ����ԤԼ��Ϣ
     * @param OpBookNo String
     */
    private void selectOpBook(String OpBookNo){
        //��������ԤԼ���� ��ѯ����ԤԼ��Ϣ
        TParm parm = new TParm();
        parm.setData("OPBOOK_SEQ",OpBookNo);//����ԤԼ����
        //��ѯ����ԤԼ
        TParm OpBook = OPEOpBookTool.getInstance().selectOpBook(parm);
        if(OpBook.getCount()<=0){
            this.messageBox("E0083");
            return;
        }
        OPBOOK_SEQ = OpBook.getValue("OPBOOK_SEQ",0);
        MR_NO  = OpBook.getValue("MR_NO",0);
        //����ԤԼ���� ��ѯ������¼
        TParm opRecord = OPEOpDetailTool.getInstance().selectData(parm);
        //�����ԤԼ���� ������������¼ ��ô�½�������¼
        if(opRecord.getCount()<=0){
            SAVE_FLG = "new";//����״̬  new:�½� update:�޸�
            this.setValue("OPBOOK_NO",OPBOOK_SEQ);
            setOpBookData();//��ȡԤԼ������Ϣ����ֵ
            callFunction("UI|MR_NO|setEnabled", false);
            callFunction("UI|OPBOOK_NO|setEnabled", false);
            pat= Pat.onQueryByMrNo(MR_NO);//======pangben modify 20110701 ��ò�����Ϣ
            setPatInfo(pat);
        }else{
            TParm p = new TParm();
            p.setData("OPBOOK_SEQ",OPBOOK_SEQ);
            Object obj = this.openDialog("%ROOT%/config/ope/OPEOpBookChoose.x",p);
            TParm re = new TParm();
            if(obj instanceof TParm){
                re = (TParm)obj;
            }else {
                this.setValue("OPBOOK_NO","");
                return;
            }
            SAVE_FLG = re.getValue("FLG");//����״̬  new:�½� update:�޸�
            OPBOOK_SEQ = re.getValue("OPBOOK_SEQ");
            this.setValue("OPBOOK_NO",OPBOOK_SEQ);
            if("new".equals(SAVE_FLG)){
                setOpBookData();//��ȡԤԼ������Ϣ����ֵ
            }
            if("update".equals(SAVE_FLG)){
                OP_RECORD_NO = re.getValue("OP_RECORD_NO");
                setOpRecordData();
            }
            if ("close".equals(SAVE_FLG)) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            closeWindow();
                        }
                        catch (Exception e) {
                        }
                    }
                });
            }
            callFunction("UI|MR_NO|setEnabled", false);
            callFunction("UI|OPBOOK_NO|setEnabled", false);
            pat= Pat.onQueryByMrNo(MR_NO);//======pangben modify 20110701 ��ò�����Ϣ
            setPatInfo(pat);
        }
    }
    /**
     * ��ȡԤԼ������Ϣ����ֵ
     */
    private void setOpBookData(){
        this.setValue("OPBOOK_NO",OPBOOK_SEQ);//add by wanglong 20140411
        TParm opBook = new TParm();
        opBook.setData("OPBOOK_SEQ",OPBOOK_SEQ);
        TParm result = OPEOpBookTool.getInstance().selectOpBook(opBook);
        MR_NO = result.getValue("MR_NO",0);
        CASE_NO = result.getValue("CASE_NO",0);
        IPD_NO = result.getValue("IPD_NO",0);
        this.setValue("IPD_NO",result.getValue("IPD_NO",0));
        this.setValue("URGBLADE_FLG",result.getBoolean("URGBLADE_FLG",0));//�������
        this.setValue("OP_DATE",result.getTimestamp("OP_DATE",0));//��������
        this.setValue("ADM_TYPE",result.getValue("ADM_TYPE",0));
        this.setValue("OP_DEPT_CODE",result.getValue("OP_DEPT_CODE",0));
        ADM_TYPE = result.getValue("ADM_TYPE",0);
        if(ADM_TYPE.equals("I"))
            this.setValue("OP_STATION_CODE_I",result.getValue("OP_STATION_CODE",0));
        else if(ADM_TYPE.equals("O")||ADM_TYPE.equals("E"))
            this.setValue("OP_STATION_CODE_O",result.getValue("OP_STATION_CODE",0));
        this.setValue("BED_NO",result.getValue("BED_NO",0));
        this.setValue("ROOM_NO",result.getValue("ROOM_NO",0));
        this.setValue("TYPE_CODE",result.getValue("TYPE_CODE",0));
        this.setValue("RANK_CODE",result.getValue("RANK_CODE",0));
        this.setValue("ANA_CODE",result.getValue("ANA_CODE",0));
        this.setValue("PART_CODE",result.getValue("PART_CODE",0));//������λ  add by wanglong 20121206
        this.setValue("ISO_FLG",result.getBoolean("ISO_FLG",0));//����������� add by wanglong 20121206
        this.setValue("STERILE_FLG", result.getBoolean("STERILE_FLG",0));//�޾���� add by huangjw 20141016
        this.setValue("MIRROR_FLG", result.getBoolean("MIRROR_FLG",0));//ǻ����� add by huangjw 20141016
        this.setValue("MAIN_SURGEON",result.getValue("MAIN_SURGEON",0));
        this.setValue("REAL_AST1",result.getValue("BOOK_AST_1",0));
        this.setValue("REAL_AST2",result.getValue("BOOK_AST_2",0));
        this.setValue("REAL_AST3",result.getValue("BOOK_AST_3",0));
        this.setValue("REAL_AST4",result.getValue("BOOK_AST_4",0));
        Daily_Table.removeRowAll();
        //���
        if(result.getValue("DIAG_CODE1",0).length()>0){
            int index = Daily_Table.addRow();
            Daily_Table.setItem(index,2,result.getValue("DIAG_CODE1",0));
            Daily_Table.setItem(index,3,result.getValue("DIAG_CODE1",0));
            Daily_Table.setItem(index,1,"Y");
        }
        if(result.getValue("DIAG_CODE2",0).length()>0){
            int index = Daily_Table.addRow();
            Daily_Table.setItem(index, 2, result.getValue("DIAG_CODE2", 0));
            Daily_Table.setItem(index, 3, result.getValue("DIAG_CODE2", 0));
        }
        if(result.getValue("DIAG_CODE3",0).length()>0){
            int index = Daily_Table.addRow();
            Daily_Table.setItem(index, 2, result.getValue("DIAG_CODE3", 0));
            Daily_Table.setItem(index, 3, result.getValue("DIAG_CODE3", 0));
        }
        Daily_Table.addRow();
        //������ʽ
        OP_Table.removeRowAll();
        if(result.getValue("OP_CODE1",0).length()>0){
            int index = OP_Table.addRow();
            OP_Table.setItem(index,2,result.getValue("OP_CODE1",0));
            OP_Table.setItem(index,3,result.getValue("OP_CODE1",0));
            OP_Table.setItem(index,1,"Y");
        }
        if(result.getValue("OP_CODE2",0).length()>0){
            int index = OP_Table.addRow();
            OP_Table.setItem(index,2,result.getValue("OP_CODE2",0));
            OP_Table.setItem(index,3,result.getValue("OP_CODE2",0));
        }
        OP_Table.addRow();
        int row = 0;
        //����ѭ��ʦ
        if(result.getValue("EXTRA_USER1",0).length()>0){
            row = EXTRA_TABLE.addRow();
            EXTRA_TABLE.setItem(row,0,result.getValue("EXTRA_USER1",0));
        }
        if(result.getValue("EXTRA_USER2",0).length()>0){
            row = EXTRA_TABLE.addRow();
            EXTRA_TABLE.setItem(row,0,result.getValue("EXTRA_USER2",0));
        }
        //ѭ����ʿ
        if(result.getValue("CIRCULE_USER1",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,result.getValue("CIRCULE_USER1",0));
        }
        if(result.getValue("CIRCULE_USER2",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,result.getValue("CIRCULE_USER2",0));
        }
        if(result.getValue("CIRCULE_USER3",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,result.getValue("CIRCULE_USER3",0));
        }
        if(result.getValue("CIRCULE_USER4",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,result.getValue("CIRCULE_USER4",0));
        }
        //ϴ�ֻ�ʿ
        if(result.getValue("SCRUB_USER1",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,result.getValue("SCRUB_USER1",0));
        }
        if(result.getValue("SCRUB_USER2",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,result.getValue("SCRUB_USER2",0));
        }
        if(result.getValue("SCRUB_USER3",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,result.getValue("SCRUB_USER3",0));
        }
        if(result.getValue("SCRUB_USER4",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,result.getValue("SCRUB_USER4",0));
        }
        //����ҽʦ
        if(result.getValue("ANA_USER1",0).length()>0){
            row = ANA_TABLE.addRow();
            ANA_TABLE.setItem(row,0,"Y");
            ANA_TABLE.setItem(row,1,result.getValue("ANA_USER1",0));
        }
        if(result.getValue("ANA_USER2",0).length()>0){
            row = ANA_TABLE.addRow();
            ANA_TABLE.setItem(row,0,"N");
            ANA_TABLE.setItem(row,1,result.getValue("ANA_USER2",0));
        }
        TLabel tLabel_9 = (TLabel)this.getComponent("tLabel_9");
        if(ADM_TYPE.equals("O")||ADM_TYPE.equals("E")){
            tLabel_9.setZhText("��������");
            this.callFunction("UI|OP_STATION_CODE_I|setVisible", false);
            this.callFunction("UI|OP_STATION_CODE_O|setVisible", true);
        }else if(ADM_TYPE.equals("I")){
            tLabel_9.setZhText("��������");
            this.callFunction("UI|OP_STATION_CODE_I|setVisible", true);
            this.callFunction("UI|OP_STATION_CODE_O|setVisible", false);
        }
        pat= Pat.onQueryByMrNo(MR_NO);//add by wanglong 20140411 ��ò�����Ϣ
        setPatInfo(pat);
    }
    /**
     * ��ȡ������¼��Ϣ����ֵ
     */
    private void setOpRecordData(){
        TParm opRecord = new TParm();
        opRecord.setData("OP_RECORD_NO",OP_RECORD_NO);
        DetailData = OPEOpDetailTool.getInstance().selectData(opRecord);
        CASE_NO = DetailData.getValue("CASE_NO",0);
        IPD_NO = DetailData.getValue("IPD_NO",0);
        this.setValue("IPD_NO",DetailData.getValue("IPD_NO",0));
        this.setValue("ADM_TYPE",DetailData.getValue("ADM_TYPE",0));
        this.setValue("OP_DATE",DetailData.getTimestamp("OP_DATE",0));
        this.setValue("OP_END_DATE",DetailData.getTimestamp("OP_END_DATE",0));//��������ʱ��   add by wanglong 20121219
        this.setValue("OP_DEPT_CODE",DetailData.getValue("OP_DEPT_CODE",0));
        ADM_TYPE=DetailData.getValue("ADM_TYPE",0);
        if(ADM_TYPE.equals("I"))
            this.setValue("OP_STATION_CODE_I",DetailData.getValue("OP_STATION_CODE",0));
        else if(ADM_TYPE.equals("O")||ADM_TYPE.equals("E"))
            this.setValue("OP_STATION_CODE_O",DetailData.getValue("OP_STATION_CODE",0));
        this.setValue("BED_NO",DetailData.getValue("BED_NO",0));
        this.setValue("ROOM_NO",DetailData.getValue("ROOM_NO",0));
        this.setValue("TYPE_CODE",DetailData.getValue("TYPE_CODE",0));
        this.setValue("RANK_CODE",DetailData.getValue("RANK_CODE",0));
        this.setValue("WAY_CODE",DetailData.getValue("WAY_CODE",0));
        this.setValue("ANA_CODE",DetailData.getValue("ANA_CODE",0));
        this.setValue("URGBLADE_FLG",DetailData.getValue("URGBLADE_FLG",0));
        this.setValue("ASA_CODE",DetailData.getValue("ASA_CODE",0));//����ּ�   add by wanglong 20121206
        this.setValue("NNIS_CODE",DetailData.getValue("NNIS_CODE",0));//�������շּ�   add by wanglong 20121206
        this.setValue("PART_CODE",DetailData.getValue("PART_CODE",0));//������λ   add by wanglong 20121206
        this.setValue("ISO_FLG",DetailData.getValue("ISO_FLG",0));//�����������   add by wanglong 20121206
        this.setValue("STERILE_FLG", DetailData.getBoolean("STERILE_FLG",0));//�޾���� add by huangjw 20141016
        this.setValue("MIRROR_FLG", DetailData.getBoolean("MIRROR_FLG",0));//ǻ����� add by huangjw 20141016
        this.setValue("MAIN_SURGEON",DetailData.getValue("MAIN_SURGEON",0));
        this.setValue("REAL_AST1",DetailData.getValue("REAL_AST1",0));
        this.setValue("REAL_AST2",DetailData.getValue("REAL_AST2",0));
        this.setValue("REAL_AST3",DetailData.getValue("REAL_AST3",0));
        this.setValue("REAL_AST4",DetailData.getValue("REAL_AST4",0));
        Daily_Table.removeRowAll();
        if(DetailData.getValue("DIAG_CODE1",0).length()>0){
            int index = Daily_Table.addRow();
            Daily_Table.setItem(index,2,DetailData.getValue("DIAG_CODE1",0));
            Daily_Table.setItem(index,3,DetailData.getValue("DIAG_CODE1",0));
            Daily_Table.setItem(index,1,"Y");
        }
        if(DetailData.getValue("DIAG_CODE2",0).length()>0){
            int index = Daily_Table.addRow();
            Daily_Table.setItem(index, 2, DetailData.getValue("DIAG_CODE2", 0));
            Daily_Table.setItem(index, 3, DetailData.getValue("DIAG_CODE2", 0));
            Daily_Table.setItem(index,1,"N");
        }
        if(DetailData.getValue("DIAG_CODE3",0).length()>0){
            int index = Daily_Table.addRow();
            Daily_Table.setItem(index, 2, DetailData.getValue("DIAG_CODE3", 0));
            Daily_Table.setItem(index, 3, DetailData.getValue("DIAG_CODE3", 0));
            Daily_Table.setItem(index,1,"N");
        }
        Daily_Table.addRow();
        OP_Table.removeRowAll();
        if(DetailData.getValue("OP_CODE1",0).length()>0){
            int index = OP_Table.addRow();
            OP_Table.setItem(index,2,DetailData.getValue("OP_CODE1",0));
            OP_Table.setItem(index,3,DetailData.getValue("OP_CODE1",0));
            OP_Table.setItem(index,1,"Y");
        }
        if(DetailData.getValue("OP_CODE2",0).length()>0){
            int index = OP_Table.addRow();
            OP_Table.setItem(index,2,DetailData.getValue("OP_CODE2",0));
            OP_Table.setItem(index,3,DetailData.getValue("OP_CODE2",0));
            OP_Table.setItem(index,1,"N");
        }
        OP_Table.addRow();
        int row;
        //����ѭ��ʦ
        if(DetailData.getValue("EXTRA_USER1",0).length()>0){
            row = EXTRA_TABLE.addRow();
            EXTRA_TABLE.setItem(row,0,DetailData.getValue("EXTRA_USER1",0));
        }
        if(DetailData.getValue("EXTRA_USER2",0).length()>0){
            row = EXTRA_TABLE.addRow();
            EXTRA_TABLE.setItem(row,0,DetailData.getValue("EXTRA_USER2",0));
        }
        //ѭ����ʿ
        if(DetailData.getValue("CIRCULE_USER1",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,DetailData.getValue("CIRCULE_USER1",0));
        }
        if(DetailData.getValue("CIRCULE_USER2",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,DetailData.getValue("CIRCULE_USER2",0));
        }
        if(DetailData.getValue("CIRCULE_USER3",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,DetailData.getValue("CIRCULE_USER3",0));
        }
        if(DetailData.getValue("CIRCULE_USER4",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,DetailData.getValue("CIRCULE_USER4",0));
        }
        //ϴ�ֻ�ʿ
        if(DetailData.getValue("SCRUB_USER1",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,DetailData.getValue("SCRUB_USER1",0));
        }
        if(DetailData.getValue("SCRUB_USER2",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,DetailData.getValue("SCRUB_USER2",0));
        }
        if(DetailData.getValue("SCRUB_USER3",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,DetailData.getValue("SCRUB_USER3",0));
        }
        if(DetailData.getValue("SCRUB_USER4",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,DetailData.getValue("SCRUB_USER4",0));
        }
        //����ҽʦ
        if(DetailData.getValue("ANA_USER1",0).length()>0){
            row = ANA_TABLE.addRow();
            ANA_TABLE.setItem(row,0,"Y");
            ANA_TABLE.setItem(row,1,DetailData.getValue("ANA_USER1",0));
        }
        if(DetailData.getValue("ANA_USER2",0).length()>0){
            row = ANA_TABLE.addRow();
            ANA_TABLE.setItem(row,0,"N");
            ANA_TABLE.setItem(row,1,DetailData.getValue("ANA_USER2",0));
        }
    }
    /**
     * ����ѭ��ʦ ����¼�
     */
    public void onEXTRA_ADD() {
        String user_id = this.getValueString("EXTRA_USER");
        if (!checkGrid(EXTRA_TABLE, user_id, 0))
            return;
        if (EXTRA_TABLE.getRowCount() >= 2) {
            this.messageBox("E0084");
            return;
        }
        if (user_id.length() > 0) {
            int row = EXTRA_TABLE.addRow();
            EXTRA_TABLE.setItem(row, 0, user_id);
            this.clearValue("EXTRA_USER");
        }
    }

    /**
     * ����ѭ��ʦ ɾ���¼�
     */
    public void onEXTRA_DEL() {
        int row = EXTRA_TABLE.getSelectedRow();
        if (row > -1) {
            EXTRA_TABLE.removeRow(row);
        }
    }

    /**
     * Ѳ�ػ�ʿ ����¼�
     */
    public void onCIRCULE_ADD() {
        String user_id = this.getValueString("CIRCULE_USER");
        if (!checkGrid(CIRCULE_TABLE, user_id, 0))
            return;
        if (CIRCULE_TABLE.getRowCount() >= 4) {
            this.messageBox("E0085");
            return;
        }
        if (user_id.length() > 0) {
            int row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row, 0, user_id);
            this.clearValue("CIRCULE_USER");
        }
    }

    /**
     * Ѳ�ػ�ʿ ɾ���¼�
     */
    public void onCIRCULE_DEL() {
        int row = CIRCULE_TABLE.getSelectedRow();
        if (row > -1) {
            CIRCULE_TABLE.removeRow(row);
        }
    }

    /**
     * ϴ�ֻ�ʿ ����¼�
     */
    public void onSCRUB_ADD() {
        String user_id = this.getValueString("SCRUB_USER");
        if (!checkGrid(SCRUB_TABLE, user_id, 0))
            return;
        if (SCRUB_TABLE.getRowCount() >= 4) {
            this.messageBox("E0086");
            return;
        }
        if (user_id.length() > 0) {
            int row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row, 0, user_id);
            this.clearValue("SCRUB_USER");
        }
    }

    /**
     * ϴ�ֻ�ʿ ɾ���¼�
     */
    public void onSCRUB_DEL() {
        int row = SCRUB_TABLE.getSelectedRow();
        if (row > -1) {
            SCRUB_TABLE.removeRow(row);
        }
    }

    /**
     * ����ҽʦ ����¼�
     */
    public void onANA_ADD() {
        String user_id = this.getValueString("ANA_USER");
        if (!checkGrid(ANA_TABLE, user_id, 1))
            return;
        if (ANA_TABLE.getRowCount() >= 2) {
            this.messageBox("E0087");
            return;
        }
        if (user_id.length() > 0) {
            int row = ANA_TABLE.addRow();
            ANA_TABLE.setItem(row, 1, user_id);
            ANA_TABLE.setItem(row, 0, "N");
            this.clearValue("ANA_USER");
        }
    }

    /**
     * ����ҽʦ ɾ���¼�
     */
    public void onANA_DEL() {
        int row = ANA_TABLE.getSelectedRow();
        if (row > -1) {
            ANA_TABLE.removeRow(row);
        }
    }

    /**
     * ���Grid���Ƿ����ظ�����Ա
     * @param table TTable
     * @param user_id String
     * @return boolean
     */
    private boolean checkGrid(TTable table, String user_id, int column) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if (user_id.equals(table.getValueAt(i, column).toString())) {
                this.messageBox("E0088");
                return false;
            }
        }
        return true;
    }

    /**
     * ��ȡ����ѭ��ʦGrid����
     * @return TParm
     */
    private TParm getEXTRAData() {
        TParm parm = new TParm();
        for (int i = 0; i < EXTRA_TABLE.getRowCount(); i++) {
            if (EXTRA_TABLE.getValueAt(i, 0).toString().trim().length() > 0) {
                parm.setData("EXTRA_USER" + (i + 1),
                             EXTRA_TABLE.getValueAt(i, 0));
            }
        }
        return parm;
    }

    /**
     * ��ȡ������ʿGrid����
     * @return TParm
     */
    private TParm getCIRCULEData() {
        TParm parm = new TParm();
        for (int i = 0; i < CIRCULE_TABLE.getRowCount(); i++) {
            if (CIRCULE_TABLE.getValueAt(i, 0).toString().trim().length() > 0) {
                parm.setData("CIRCULE_USER" + (i + 1),
                             CIRCULE_TABLE.getValueAt(i, 0));
            }
        }
        return parm;
    }

    /**
     * ��ȡϴ�ֻ�ʿGrid����
     * @return TParm
     */
    private TParm getSCRUBData() {
        TParm parm = new TParm();
        for (int i = 0; i < SCRUB_TABLE.getRowCount(); i++) {
            if (SCRUB_TABLE.getValueAt(i, 0).toString().trim().length() > 0) {
                parm.setData("SCRUB_USER" + (i + 1),
                             SCRUB_TABLE.getValueAt(i, 0));
            }
        }
        return parm;
    }

    /**
     * ��ȡ����ҽʦGrid����
     * @return TParm
     */
    private TParm getANAData() {
        TParm parm = new TParm();
        int index = 2;
        for (int i = 0; i < ANA_TABLE.getRowCount(); i++) {
            if (ANA_TABLE.getValueAt(i, 1).toString().trim().length() > 0) {
                if ("Y".equals(ANA_TABLE.getValueAt(i, 0).toString().trim()))
                    parm.setData("ANA_USER1", ANA_TABLE.getValueAt(i, 1));
                else
                    parm.setData("ANA_USER" + index, ANA_TABLE.getValueAt(i, 1));
            }
        }
        return parm;
    }

    /**
     * ����ҽʦ ����ʶ����¼�
     */
    public void onANATableMainCharge(Object obj) {
        ANA_TABLE.acceptText();
        if (ANA_TABLE.getSelectedColumn() == 0) {
            int row = ANA_TABLE.getSelectedRow();
            for (int i = 0; i < ANA_TABLE.getRowCount(); i++) {
                ANA_TABLE.setItem(i, 0, "N");
            }
            ANA_TABLE.setItem(row, 0, "Y");
        }
    }

    /**
     *������������ OpICD
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComponentOP(Component com, int row, int column) {
        //����ICD10�Ի������
        if (column != 2)
            return;
        if (! (com instanceof TTextField))
            return;
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        //��table�ϵ���text����ICD10��������
        textfield.setPopupMenuParameter("OP_ICD",
                                        getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSOpICD.x"));
        //����text���ӽ���ICD10�������ڵĻش�ֵ
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                   "newOPOrder");
    }

    /**
     * ȡ������ICD����ֵ
     * @param tag String
     * @param obj Object
     */
    public void newOPOrder(String tag, Object obj) {
        TTable table = (TTable)this.callFunction("UI|OP_Table|getThis");
        //sysfee���ص����ݰ�
        TParm parm = (TParm) obj;
        String orderCode = parm.getValue("OPERATION_ICD");
        table.setItem(table.getSelectedRow(), "OP_ICD", orderCode);
        if("en".equals(this.getLanguage()))
            table.setItem(table.getSelectedRow(), "OP_DESC",
                          parm.getValue("OPT_ENG_DESC"));
        else
            table.setItem(table.getSelectedRow(), "OP_DESC",
                          parm.getValue("OPT_CHN_DESC"));
    }

    /**
     *��ϵ������� ICD10
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComponent(Component com, int row, int column) {
        //����ICD10�Ի������
        if (column != 2)
            return;
        if (! (com instanceof TTextField))
            return;
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        //��table�ϵ���text����ICD10��������
        textfield.setPopupMenuParameter("ICD10",
                                        getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSICDPopup.x"));
        //����text���ӽ���ICD10�������ڵĻش�ֵ
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                   "newAgentOrder");
    }

    /**
     * ȡ��ICD10����ֵ
     * @param tag String
     * @param obj Object
     */
    public void newAgentOrder(String tag, Object obj) {
        TTable table = (TTable)this.callFunction("UI|Daily_Table|getThis");
        //sysfee���ص����ݰ�
        TParm parm = (TParm) obj;
        String orderCode = parm.getValue("ICD_CODE");
        table.setItem(table.getSelectedRow(), "DAILY_CODE", orderCode);
        if("en".equals(this.getLanguage()))
            table.setItem(table.getSelectedRow(), "DAILY_DESC",
                          parm.getValue("ICD_ENG_DESC"));
        else
            table.setItem(table.getSelectedRow(), "DAILY_DESC",
                          parm.getValue("ICD_CHN_DESC"));
    }

    /**
     * ���Grid ֵ�ı��¼�
     * @param obj Object
     */
    public void onDiagTableValueCharge(Object obj) {
        //�õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
        TTableNode node = (TTableNode) obj;
        if (node.getColumn() == 2) {
            if (node.getRow() == (Daily_Table.getRowCount() - 1))
                Daily_Table.addRow();
        }
    }

    /**
     * ����Grid ֵ�ı��¼�
     * @param obj Object
     */
    public void onOpTableValueCharge(Object obj) {
        //�õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
        TTableNode node = (TTableNode) obj;
        if (node.getColumn() == 2) {
            if (node.getRow() == (OP_Table.getRowCount() - 1))
                OP_Table.addRow();
        }
    }

    /**
     * ���Grid ����ϱ���޸��¼�
     * @param obj Object
     */
    public void onDiagTableMainCharge(Object obj) {
        Daily_Table.acceptText();
        if (Daily_Table.getSelectedColumn() == 1) {
            int row = Daily_Table.getSelectedRow();
            for (int i = 0; i < Daily_Table.getRowCount(); i++) {
                Daily_Table.setItem(i, "MAIN_FLG", "N");
            }
            Daily_Table.setItem(row, "MAIN_FLG", "Y");
        }
    }

    /**
     * ���Grid ����ϱ���޸��¼�
     * @param obj Object
     */
    public void onOpTableMainCharge(Object obj) {
        OP_Table.acceptText();
        if (OP_Table.getSelectedColumn() == 1) {
            int row = OP_Table.getSelectedRow();
            for (int i = 0; i < OP_Table.getRowCount(); i++) {
                OP_Table.setItem(i, "MAIN_FLG", "N");
            }
            OP_Table.setItem(row, "MAIN_FLG", "Y");
        }
    }
    
    /**
     * ѡ��Ƴ�������
     */
    public void onDeptOp(){
        String dept_code = this.getValueString("OP_DEPT_CODE");
        if(dept_code.length()<=0){
            this.messageBox("E0077");
            return;
        }
        String op_icd = (String)this.openDialog("%ROOT%/config/ope/OPEDeptOpShow.x",dept_code);
        if(op_icd==null||op_icd.length()<=0){
            return;
        }
        //���ش�ֵ ��ʾ�ڱ����
        OP_Table.setValueAt_(op_icd, OP_Table.getRowCount() - 1, 2);
        OP_Table.setValueAt_(op_icd, OP_Table.getRowCount() - 1, 3);
        OP_Table.addRow();
    }
    
    /**
     * ���
     */
    public void onClear(){
        this.clearValue("MR_NO;IPD_NO;PAT_NAME;AGE;SEX;Weight;OPBOOK_NO;ADM_TYPE;OP_DATE;OP_END_DATE;OP_DEPT_CODE;OP_STATION_CODE_I;OP_STATION_CODE_O;");//modify by wanglong 20130514
        this.clearValue("BED_NO;ROOM_NO;TYPE_CODE;RANK_CODE;ANA_CODE;WAY_CODE;URGBLADE_FLG;MAIN_SURGEON;");
        this.clearValue("REAL_AST1;REAL_AST2;REAL_AST3;REAL_AST4;ASA_CODE;NNIS_CODE;PART_CODE;ISO_FLG");//modify by wanglong 20121212
        this.clearValue("MIRROR_FLG;STERILE_FLG");//modify by huangjw 20141016
        onSelectOpType();// wanglong add 20140929
        EXTRA_TABLE.removeRowAll();
        CIRCULE_TABLE.removeRowAll();
        SCRUB_TABLE.removeRowAll();
        ANA_TABLE.removeRowAll();
        Daily_Table.removeRowAll();
        OP_Table.removeRowAll();
        Daily_Table.addRow();
        OP_Table.addRow();
        callFunction("UI|MR_NO|setEnabled", true);
        callFunction("UI|OPBOOK_NO|setEnabled", true);
    }
    
    /**
     * ���CODE�滻���� ģ����ѯ���ڲ��ࣩ
     */
    public class OrderList
        extends TLabel {
        TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("ICD_CODE");
            Vector d = (Vector) parm.getData("ICD_CHN_DESC");
            Vector e = (Vector) parm.getData("ICD_ENG_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i))){
                    if ("en".equals(OPEOpDetailControl.this.getLanguage())) {
                        return "" + e.get(i);
                    }
                    else {
                        return "" + d.get(i);
                    }
                }
            }
            return s;
        }
    }

    /**
     * ����CODE�滻���� ģ����ѯ���ڲ��ࣩ
     */
    public class OpList
        extends TLabel {
        TDataStore dataStore = new TDataStore();
        public OpList(){
            dataStore.setSQL("select * from SYS_OPERATIONICD");
            dataStore.retrieve();
        }
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("OPERATION_ICD");
            Vector d = (Vector) parm.getData("OPT_CHN_DESC");
            Vector e = (Vector) parm.getData("OPT_ENG_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i))){
                    if ("en".equals(OPEOpDetailControl.this.getLanguage())) {
                        return "" + e.get(i);
                    }
                    else {
                        return "" + d.get(i);
                    }
                }
            }
            return s;
        }
    }
    
    /**
     * ��������ѡ���¼�
     */
    public void onOP_DEPT_CODE(){
        this.clearValue("OP_STATION_CODE;BED_NO;MAIN_SURGEON");
    }
    /**
     * ����ѡ���¼�
     */
    public void onOP_STATION_CODE(){
        this.clearValue("BED_NO");
    }
    
    /**
     * ���ýṹ������ ��д������¼
     */
    public void onEmr(){//�˷�������ʹ�� by wanglong 20121220
        if(OP_RECORD_NO.length()<=0){
            return;
        }
        TParm opParm = new TParm();
        opParm.setData("OP_RECORD_NO",OP_RECORD_NO);
        TParm emrData = OPEOpDetailTool.getInstance().selectData(opParm);
        TParm parm = new TParm();
        TParm emrParm = new TParm();
        emrParm.setData("MR_CODE", TConfig.getSystemValue("OPEEmrMRCODE"));//��ȡ������¼ģ���ID
        emrParm.setData("CASE_NO", emrData.getValue("CASE_NO",0));
        emrParm = EmrUtil.getInstance().getEmrFilePath(emrParm);
        String adm_type = emrData.getValue("ADM_TYPE",0);
        String SYSTEM_TYPE = "";
        if("O".equals(adm_type)){
            SYSTEM_TYPE = "ODO";
        }else if("E".equals(adm_type)){
            SYSTEM_TYPE = "EMG";
        }else if("I".equals(adm_type)){
            SYSTEM_TYPE = "ODI";
        }else if("H".equals(adm_type)){
            SYSTEM_TYPE = "HRM";
        }
        parm.setData("SYSTEM_TYPE", SYSTEM_TYPE);
        parm.setData("ADM_TYPE", adm_type);
        parm.setData("STYLETYPE","1");//��ʽ
        parm.setData("CASE_NO", emrData.getValue("CASE_NO",0));
        parm.setData("MR_NO", emrData.getValue("MR_NO",0));
        parm.setData("IPD_NO", emrData.getValue("IPD_NO",0));
        parm.setData("EMR_FILE_DATA", emrParm);
        parm.setData("RULETYPE","2");//�޸�Ȩ��
        parm.addListener("EMR_LISTENER",this,"emrListener");
        parm.addListener("EMR_SAVE_LISTENER",this,"emrSaveListener");
       //System.out.println("========onEmr parm========="+parm);
        this.openDialog("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
    }
    
    /**
     * EMR����
     * @param parm TParm
     */
    public void emrListener(TParm parm){//modify by wanglong 20121220
        NameList nl = new NameList();
		eventParmEmr = parm;
		TParm data = OPEOpDetailTool.getInstance().selectForEmr(OP_RECORD_NO);
		parm.runListener("setMicroData", "����", data.getValue("DEPT_DESC", 0));// ����
		parm.runListener("setMicroData", "����", data.getValue("STATION_DESC", 0));// ����
		parm.runListener("setMicroData", "��������", data.getValue("OP_DATE", 0));// ��������
		parm.runListener("setMicroData", "������ʼʱ��", data.getValue("OP_START_DATE", 0));// ������ʼʱ��
		parm.runListener("setMicroData", "��������ʱ��", data.getValue("OP_END_DATE", 0));// ��������ʱ��
		parm.runListener("setMicroData", "�������", data.getValue("ICD_DESC", 0));// �������
		parm.runListener("setMicroData", "��������", data.getValue("OPT_DESC", 0));// ��������
		parm.runListener("setMicroData", "����ҽ��", data.getValue("SURGEON_USER", 0));// ����ҽ��
		parm.runListener("setMicroData", "һ��", data.getValue("AST_USER1", 0));// һ��
		parm.runListener("setMicroData", "����", data.getValue("AST_USER2", 0));// ����
		parm.runListener("setMicroData", "����", data.getValue("AST_USER3", 0));// ����
		parm.runListener("setMicroData", "����", data.getValue("AST_USER4", 0));// ����
		parm.runListener("setMicroData", "����ҽʦ", data.getValue("ANA_USER", 0));// ����ҽʦ
		parm.runListener("setMicroData", "����ʽ", data.getValue("ANA_DESC", 0));// ����ʽ
		parm.runListener("setMicroData", "����ּ�", data.getValue("ASA_DESC", 0));// ����ּ�
		parm.runListener("setMicroData", "������λ", data.getValue("PART_DESC", 0));// ������λ
    }
    
    /**
     * EMR������� ȡ�ṹ����������д��ֵ
     * @param parm TParm
     */
    public void emrSaveListener(TParm parm){
        List name = new ArrayList();//��ȡֵ�ؼ���������List����ʽ ����
        name.add("weight");
        name.add("height");
        //����EMR�е�ȡֵ������ ����Object��ֵ
        Object[] obj  = (Object[])eventParmEmr.runListener("getCaptureValueArray",name);
        //�ӷ���ֵ���в���
    }
    /**
     * ģ����ѯ���ڲ��ࣩ �û������滻
     */
    public class NameList extends TLabel {
        TDataStore dataStore;
        public NameList(){
            dataStore = new TDataStore();
            dataStore.setSQL("SELECT USER_ID,USER_NAME FROM SYS_OPERATOR");
            dataStore.retrieve();
        }
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("USER_ID");
            Vector d = (Vector) parm.getData("USER_NAME");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i)))
                    return "" + d.get(i);
            }
            return s;
        }
    }
    
    /**
     * ��ӡ
     */
	public void onPrint() {// �˷�����ʱ��û�б�ʹ��  by wanglong 20121220
        if(DetailData==null||OP_RECORD_NO.length()<=0){
            return;
        }
        OrderList ol = new OrderList();
        OpList op = new OpList();
        NameList nl = new NameList();
        Pat pat = Pat.onQueryByMrNo(DetailData.getValue("MR_NO",0));
        TParm printData = new TParm();
        printData.setData("OP_RECORD_NO","TEXT",DetailData.getValue("OPBOOK_NO",0));
        printData.setData("PAT_NAME","TEXT",pat.getName());
        printData.setData("OP_DATE","TEXT",StringTool.getString(DetailData.getTimestamp("OP_DATE",0),"yyyy��MM��dd��"));
        printData.setData("SEX","TEXT",pat.getSexCode());
        printData.setData("AGE","TEXT",StringUtil.showAge(pat.getBirthday(),DetailData.getTimestamp("OP_DATE",0)));
        TParm patParm = pat.getParm();
        patParm.setData("CASE_NO", StringUtil.getDesc("OPE_OPBOOK", "CASE_NO", "OPBOOK_SEQ='" + OPBOOK_SEQ + "'"));
        TParm admParm = ADMInpTool.getInstance().selectall(patParm);// wanglong add 20141011
        double weight = admParm.getDouble("WEIGHT", 0);
//        printData.setData("WEIGHT", "TEXT", pat.getWeight() + " kg");
        printData.setData("WEIGHT", "TEXT", weight + " kg");
        printData.setData("DIAG_CODE1","TEXT",ol.getTableShowValue(DetailData.getValue("DIAG_CODE1",0)));
        printData.setData("DIAG_CODE2","TEXT",ol.getTableShowValue(DetailData.getValue("DIAG_CODE2",0)));
        printData.setData("DIAG_CODE3","TEXT",ol.getTableShowValue(DetailData.getValue("DIAG_CODE3",0)));
        printData.setData("OP_CODE1","TEXT",op.getTableShowValue(DetailData.getValue("OP_CODE1",0)));
        printData.setData("MAIN_SURGEON","TEXT",nl.getTableShowValue(DetailData.getValue("MAIN_SURGEON",0)));
        printData.setData("REAL_AST1","TEXT",nl.getTableShowValue(DetailData.getValue("REAL_AST1",0)));
        printData.setData("REAL_AST2","TEXT",nl.getTableShowValue(DetailData.getValue("REAL_AST2",0)));
        printData.setData("REAL_AST3","TEXT",nl.getTableShowValue(DetailData.getValue("REAL_AST3",0)));
        printData.setData("REAL_AST4","TEXT",nl.getTableShowValue(DetailData.getValue("REAL_AST4",0)));
        this.openPrintDialog("%ROOT%\\config\\prt\\OPE\\OPEDetail.jhw",printData);
    }
	
    /**
     * ����ģ�崴��
     * =============pangben modify 20110701
     */
    public void onOpstmp(){
        if(null==pat || MR_NO==null || this.getValueString("MR_NO").equals("")){//modify by wanglong 20130514
            this.messageBox("�����벡����");
            return ;
        }
        TParm opParm = new TParm();
        opParm.setData("CASE_NO",CASE_NO);
        opParm.setData("REGION_CODE",Operator.getRegion());
        TParm parm = new TParm();
        String SYSTEM_TYPE="";
        if("O".equals(ADM_TYPE)){
            SYSTEM_TYPE = "ODO";
        }else if("E".equals(ADM_TYPE)){
            SYSTEM_TYPE = "EMG";
        }else if("I".equals(ADM_TYPE)){
            SYSTEM_TYPE = "ODI";
        }else if("H".equals(ADM_TYPE)){
            SYSTEM_TYPE = "HRM";
        }
        TParm result = ADMInpTool.getInstance().selectall(opParm);//סԺ��Ϣ
        //parm.setData("SYSTEM_TYPE", SYSTEM_TYPE);//delete by wanglong 20121220
        parm.setData("SYSTEM_TYPE", "OPE");//add by wanglong 20121220
        parm.setData("ADM_TYPE", ADM_TYPE);
        parm.setData("CASE_NO", CASE_NO);
        parm.setData("PAT_NAME", pat.getName());
        parm.setData("MR_NO", MR_NO);
        parm.setData("IPD_NO", IPD_NO);
        parm.setData("ADM_DATE", result.getTimestamp("ADM_DATE",0));
        parm.setData("DEPT_CODE", this.getValue("OP_DEPT_CODE"));
        parm.setData("STATION_CODE", this.getValue("OP_STATION_CODE_I"));
//       if (this.isOidrFlg()) {
//           parm.setData("RULETYPE", "3");
//           //д������(����)
//           parm.setData("WRITE_TYPE","OIDR");
//       }else {
           parm.setData("RULETYPE", "2");
           //д������(����)
           parm.setData("WRITE_TYPE","");
 //      }
        String opBookNo = this.getValueString("OPBOOK_NO");//add by wanglong 20130608
        if (StringUtil.isNullString(opBookNo)) {
            return;
        }
        String opRecordNo = OPEOpDetailTool.getInstance().selectForEmrByBookNo(opBookNo);
        if (!StringUtil.isNullString(opRecordNo)) {// ����������¼���Σ������������벻���� modify by wanglong 20130608
            result = OPEOpDetailTool.getInstance().selectForEmr(OP_RECORD_NO);
            TParm action = new TParm();
            action.setData("����", result.getValue("DEPT_DESC", 0));// ����
            action.setData("����", result.getValue("STATION_DESC", 0));// ����
            action.setData("��������", result.getValue("OP_DATE", 0));// ��������
            if (StringUtil.isNullString(result.getValue("OP_START_DATE", 0))) {
                action.setData("������ʼʱ��", result.getValue("OP_DATE", 0));// ������ʼʱ��
            } else {
                action.setData("������ʼʱ��", result.getValue("OP_START_DATE", 0));// ������ʼʱ��
            }
            action.setData("��������ʱ��", result.getValue("OP_END_DATE", 0));// ��������ʱ��
            action.setData("�������", result.getValue("ICD_DESC", 0));// �������
            action.setData("��ǰ���", result.getValue("ICD_DESC", 0));// ��ǰ���
            action.setData("��������", result.getValue("OPT_DESC", 0));// ��������
            action.setData("��������", result.getValue("TYPE_DESC", 0));// ��������
            action.setData("����ҽ��", result.getValue("SURGEON_USER", 0));// ����ҽ��
            action.setData("����ҽʦ", result.getValue("SURGEON_USER", 0));// ����ҽ��
            action.setData("һ��", result.getValue("AST_USER1", 0));// һ��
            action.setData("����", result.getValue("AST_USER2", 0));// ����
            action.setData("����", result.getValue("AST_USER3", 0));// ����
            action.setData("����", result.getValue("AST_USER4", 0));// ����
            action.setData("����ҽʦ", result.getValue("ANA_USER", 0));// ����ҽʦ
            action.setData("����ʽ", result.getValue("ANA_DESC", 0));// ����ʽ
            action.setData("����ּ�", result.getValue("ASA_DESC", 0));// ����ּ�
            action.setData("������λ", result.getValue("PART_DESC", 0));// ������λ
            parm.setData("OPE_DATA", action);
        }
        parm.setData("TYPE", "F");// ��ΪF���������ʾ�ò���ÿ�ξ����������¼��ΪF����ֻ��ʾ���ξ���ʱ��������¼ add by wanglong 20121220
        parm.setData("EMR_DATA_LIST", new TParm());
        parm.addListener("EMR_LISTENER", this, "emrListener");
        parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
        this.openDialog("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
    }
    
    /**
     * ���ý���������������������ɱ༭
     */
	public void setUIFalse() {// add by wanglong 20121219
		((TTextField) this.getComponent("MR_NO")).setEnabled(false);
		((TTextField) this.getComponent("OPBOOK_NO")).setEnabled(false);
		((TComboBox) this.getComponent("ADM_TYPE")).setEnabled(false);
		((TTextFormat) this.getComponent("OP_DATE")).setEnabled(false);
		((TTextFormat) this.getComponent("OP_END_DATE")).setEnabled(false);
		((TTextFormat) this.getComponent("OP_DEPT_CODE")).setEnabled(false);
		((TTextFormat) this.getComponent("OP_STATION_CODE_I")).setEnabled(false);
		((TTextFormat) this.getComponent("OP_STATION_CODE_O")).setEnabled(false);
		((TTextFormat) this.getComponent("BED_NO")).setEnabled(false);
		((TTextFormat) this.getComponent("ROOM_NO")).setEnabled(false);
		((TComboBox) this.getComponent("TYPE_CODE")).setEnabled(false);
		((TComboBox) this.getComponent("RANK_CODE")).setEnabled(false);
		((TComboBox) this.getComponent("WAY_CODE")).setEnabled(false);
		((TComboBox) this.getComponent("ANA_CODE")).setEnabled(false);
		((TCheckBox) this.getComponent("URGBLADE_FLG")).setEnabled(false);
		((TTextFormat) this.getComponent("ASA_CODE")).setEnabled(false);
		((TTextFormat) this.getComponent("NNIS_CODE")).setEnabled(false);
		((TTextFormat) this.getComponent("PART_CODE")).setEnabled(false);
		((TCheckBox) this.getComponent("ISO_FLG")).setEnabled(false);
		((TCheckBox) this.getComponent("MIRROR_FLG")).setEnabled(false);//add by huangjw 20141016
		((TCheckBox) this.getComponent("STERILE_FLG")).setEnabled(false);//add by huangjw 20141016
		((TTextFormat) this.getComponent("MAIN_SURGEON")).setEnabled(false);
		((TTextFormat) this.getComponent("REAL_AST1")).setEnabled(false);
		((TTextFormat) this.getComponent("REAL_AST2")).setEnabled(false);
		((TTextFormat) this.getComponent("REAL_AST3")).setEnabled(false);
		((TTextFormat) this.getComponent("REAL_AST4")).setEnabled(false);
		((TTable) this.getComponent("Daily_Table")).setEnabled(false);
		((TButton) this.getComponent("SelectDEPT_OP")).setEnabled(false);
		((TTable) this.getComponent("OP_Table")).setEnabled(false);
		((TTable) this.getComponent("EXTRA_TABLE")).setEnabled(false);
		((TTextFormat) this.getComponent("EXTRA_USER")).setEnabled(false);
		((TButton) this.getComponent("EXTRA_ADD")).setEnabled(false);
		((TButton) this.getComponent("EXTRA_DEL")).setEnabled(false);
		((TTable) this.getComponent("CIRCULE_TABLE")).setEnabled(false);
		((TTextFormat) this.getComponent("CIRCULE_USER")).setEnabled(false);
		((TButton) this.getComponent("CIRCULE_ADD")).setEnabled(false);
		((TButton) this.getComponent("CIRCULE_DEL")).setEnabled(false);
		((TTable) this.getComponent("SCRUB_TABLE")).setEnabled(false);
		((TTextFormat) this.getComponent("SCRUB_USER")).setEnabled(false);
		((TButton) this.getComponent("SCRUB_ADD")).setEnabled(false);
		((TButton) this.getComponent("SCRUB_DEL")).setEnabled(false);
		((TTable) this.getComponent("ANA_TABLE")).setEnabled(false);
		((TTextFormat) this.getComponent("ANA_USER")).setEnabled(false);
		((TButton) this.getComponent("ANA_ADD")).setEnabled(false);
		((TButton) this.getComponent("ANA_DEL")).setEnabled(false);
		((TMenuItem) this.getComponent("save")).setEnabled(false);
		((TMenuItem) this.getComponent("clear")).setEnabled(false);
//		((TMenuItem) this.getComponent("emr")).setEnabled(false);//delete by wanglong 20140411
		((TMenuItem) this.getComponent("opstmp")).setEnabled(false);
	}

    /**
     * ���������͡��롰�����䡱����
     */
    public void onSelectOpType() {// wanglong add 20140929
        String typeCode = this.getValueString("TYPE_CODE");
        TTextFormat roomNo = (TTextFormat) this.getComponent("ROOM_NO");
        String sql =
                "SELECT B.ID,B.CHN_DESC AS NAME,B.PY1 FROM OPE_IPROOM A,SYS_DICTIONARY B "
                        + " WHERE B.GROUP_ID='OPE_OPROOM' AND A.ROOM_NO=B.ID # ORDER BY B.SEQ,B.ID";
        if (!StringUtil.isNullString(typeCode)) {
            sql = sql.replaceFirst("#", " AND A.TYPE_CODE = '" + typeCode + "' ");
            this.setValue("ROOM_NO", "");
        } else {
            sql =
                    "SELECT ID,CHN_DESC AS NAME,PY1 FROM SYS_DICTIONARY WHERE GROUP_ID='OPE_OPROOM' ORDER BY SEQ,ID";
        }
        TParm roomParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (roomParm.getErrCode() < 0) {
            this.messageBox_("ȡ��������Ϣʧ��");
            return;
        }
        roomNo.setPopupMenuData(roomParm);
        roomNo.setComboSelectRow();
        roomNo.popupMenuShowData();
    }
    
}
