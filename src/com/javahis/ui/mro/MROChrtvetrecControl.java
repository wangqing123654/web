package com.javahis.ui.mro;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import jdo.mro.MROChrtvetrecTool;
import jdo.mro.MROChrtvetstdTool;
import jdo.mro.MROPrintTool;
import jdo.mro.MROQlayControlMTool;
import jdo.mro.MRORecordTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.sys.Pat;
import javax.swing.SwingUtilities;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;


/**
 * <p>Title: �������</p>
 *
 * <p>Description:������� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-4-30
 * @version 1.0
 */
public class MROChrtvetrecControl extends TControl {
    private TParm patInfo;//������Ϣ add by wanglong 20130819
    private String CASE_NO;//�������
    private String MR_NO;
    private String IPD_NO;
    private String VS_CODE;//����ҽʦ
    private String EXAMINE_CODE;//��˱�׼
    private String EXAMINE_DATE;//�������
    TParm data;
    int selectRow = -1;
    private String State = "mro";//��¼�ô��ڵ���״̬�����ĸ�ģ����ã�
    private boolean MRO_CHAT_FLG = false;//��¼�Ƿ��Ѿ����ͨ��
    
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        this.initPage();
    }
    
    /**
     * ˢ�³�ʼ������
     */
    public void onInitReset(){
        //��ʼ��ҳ��
        this.initPage();
    }
    
    /**
     * ��ʼ��ҳ��
     */
    public void initPage() {
        Object obj = this.getParameter();
        if (obj != null) {
            TParm parmData = (TParm) obj;
            CASE_NO = parmData.getValue("MRO", "CASE_NO");
            // ���ܲ��� "odi"��ʾסԺҽ��վ
            if (parmData.getValue("MRO", "STATE").trim().length() > 0) {
                State = parmData.getValue("MRO", "STATE");
            }
        } else {
            this.messageBox_("������ʼ��ʧ�ܣ�");
        }
        onClear();
        TTable table = (TTable) this.getComponent("MROTable");
        table.addEventListener("MROTable->" + TTableEvent.CLICKED, this, "onTableClicked");
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBox");
        // ת��ʱ���ʽ(�����ڲ���)
        OrderList orderDesc = new OrderList();
        table.addItem("OrderList", orderDesc);
        this.onQuery();
        queryPatInfo(); // ��ѯ������Ϣ
        if (State.equals("ODI")) {// ��סԺҽ��վ����
            ((TMenuItem) getComponent("delete")).setVisible(false);// ����ɾ����ť
            // ���ò��ɱ༭��
            this.callFunction("UI|VS_CODE|setEnabled", false);
            this.callFunction("UI|EXAMINE_DATE|setEnabled", false);
            this.callFunction("UI|TYPE_CODE|setEnabled", false);
            this.callFunction("UI|EXAMINE_CODE|setEnabled", false);
            this.callFunction("UI|DEDUCT_NOTE|setEnabled", false);
            this.callFunction("UI|DEDUCT_SCORE|setEnabled", false);
            this.callFunction("UI|URG_FLG|setEnabled", false);
            this.callFunction("UI|AUTO_QLAY|setEnabled", false);//add by wanglong 20131025
            this.callFunction("UI|MANUAL_QLAY|setEnabled", false);
            // ���ÿɱ༭��
            if (!MRO_CHAT_FLG) {//add by wanglong 20130909
                ((TTextField) this.getComponent("REPLY_REMK")).setEnabled(true);// ��ɻظ���ע
            }
            // ���ء������ɡ���ѡ��
            ((TCheckBox) this.getComponent("MRO_CHAT_FLG")).setEnabled(false);
        }
    }
    
    /**
     * �õ��˵�
     * @param tag String
     * @return TMenuItem
     */
    public TMenuItem getTMenuItem(String tag){
        return (TMenuItem)this.getComponent(tag);
    }

    /**
     * ���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
        // ѡ����
        if (row < 0) return;
        setValueForParm("VS_CODE;DEDUCT_NOTE;TYPE_CODE;EXAMINE_CODE;REPLY_REMK;DEDUCT_SCORE;REPLY_DTTM;REPLY_DR_CODE",
                        data, row);
        //�����סԺ���ô˴���
        if (State.equals("ODI")) {
            // ����ظ�ʱ��Ϊ��
            if (this.getValue("REPLY_DTTM") == null) {
                // ��ʼ���ظ�ʱ��ͻָ���Ա
                this.setValue("REPLY_DTTM", SystemTool.getInstance().getDate());
                ((TComboBox) this.getComponent("REPLY_DR_CODE")).setValue(Operator.getID()); // �ظ�ҽʦ
            }
        }
        selectRow = row;
        //�Ƿ񼱼�
        if(data.getValue("URG_FLG",row).equals("Y"))
            ((TCheckBox)this.getComponent("URG_FLG")).setSelected(true);
        else
            ((TCheckBox)this.getComponent("URG_FLG")).setSelected(false);
        // ����ɾ����ť״̬
        ((TMenuItem) getComponent("delete")).setEnabled(true);
        EXAMINE_CODE = data.getValue("EXAMINE_CODE",row);//��¼ѡ���м�˴���
        EXAMINE_DATE = data.getValue("EXAMINE_DATE",row);//��¼ѡ���м������
        //�޸�������ڸ�ʽ
        this.setValue("EXAMINE_DATE",StringTool.getTimestamp(data.getValue("EXAMINE_DATE",row),"yyyyMMdd"));
    }
    
    /**
     * CheckBox��ѡ�¼�
     * @param obj
     * @return
     */
    public boolean onTableCheckBox(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        return true;
    }

    /**
     * ���
     */
    public void onClear(){
        selectRow = -1;
        this.clearValue("DEDUCT_SCORE;TYPE_CODE;EXAMINE_CODE;DEDUCT_NOTE;REPLY_REMK");//modify by wanglong 20130819
        EXAMINE_CODE = "";// ��˱�׼
        EXAMINE_DATE = "";// �������
//        patInfo = new TParm();//add by wanglong 20130819
        if(State.equals("ODI")){
            //��ʼ���ظ�ʱ��ͻָ���Ա
            this.setValue("REPLY_DTTM",SystemTool.getInstance().getDate());
            ((TComboBox)this.getComponent("REPLY_DR_CODE")).setValue(Operator.getID());//�ظ�ҽʦ
        }else{
            this.clearValue("REPLY_DTTM;REPLY_DR_CODE");
        }
        if(MRO_CHAT_FLG){
            this.clearValue("VS_CODE");
        }
        // ���ѡ����
        ((TTable) this.getComponent("MROTable")).clearSelection();
        // ����ȡ��ѡ��
        ((TCheckBox) this.getComponent("URG_FLG")).setSelected(false);
        // ����ɾ����ť״̬
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        // ��ʼ�����ʱ��
        this.setValue("EXAMINE_DATE", SystemTool.getInstance().getDate());
        // ����ȫ����ѯ
        ((TRadioButton) this.getComponent("rbt1")).setSelected(true);
        // ���²�ѯ�����Ϣ
        // queryChr();
    }
    
    /**
     * ��ѯ
     */
    public void onQuery() {
        selectRow = -1;
        this.clearValue("DEDUCT_SCORE;TYPE_CODE;EXAMINE_CODE;DEDUCT_NOTE;REPLY_REMK");
        if(State.equals("ODI")){
            //��ʼ���ظ�ʱ��ͻָ���Ա
            this.setValue("REPLY_DTTM",SystemTool.getInstance().getDate());
            ((TComboBox)this.getComponent("REPLY_DR_CODE")).setValue(Operator.getID());//�ظ�ҽʦ
        }else{
            this.clearValue("REPLY_DTTM;REPLY_DR_CODE");
        }
        // ���ѡ����
        ((TTable) this.getComponent("MROTable")).clearSelection();
        // ����ȡ��ѡ��
        ((TCheckBox) this.getComponent("URG_FLG")).setSelected(false);
        // ��ʼ�����ʱ��
        this.setValue("EXAMINE_DATE", SystemTool.getInstance().getDate());
        TParm parm = new TParm();
        // �ѻظ��ļ����Ϣ
        if (((TRadioButton) this.getComponent("rbt2")).isSelected()) parm.setData("REPLY", "Y");
        else if (((TRadioButton) this.getComponent("rbt3")).isSelected()) parm.setData("REPLY", "N");
        parm.setData("CASE_NO", CASE_NO);
        parm.setData("VS_CODE", this.getValue("VS_CODE"));
        data = MROChrtvetrecTool.getInstance().queryData(parm);
        // �жϴ���ֵ
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        // ����ɾ����ť״̬
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        ((TTable) getComponent("MROTable")).setParmValue(data);
        checkCHAT_FLG();
        //========= add by wanglong 20130819
        TParm parms = new TParm();
        parms.addData("CASE_NO", CASE_NO);
        TParm result =
                TIOM_AppServer.executeAction("action.mro.MROQlayControlAction", "updateScore", parms);//���µ÷�
        if (result.getErrCode() != 0) {
            this.messageBox(result.getErrText());
            return;
        }
        result = MROQlayControlMTool.getInstance().queryQlayControlSUM(parms.getRow(0));//��ѯ�÷�
        if(result.getErrCode() != 0){
            this.messageBox(result.getErrText());
            return;
        }
        this.setValue("SCORE", result.getValue("SUMSCODE", 0));//��ʾ�÷ֵ�����
        //========= add end
    }
    
    /**
     * �鿴 ������˱��
     */
    private void checkCHAT_FLG() {
        // ��ȡ������ҳ��Ϣ �鿴 ������˱��
        TParm mroParm = new TParm();
        mroParm.setData("CASE_NO", CASE_NO);
        TParm MROInfo = MROQlayControlMTool.getInstance().queryQlayControlSUM(mroParm);
//        TParm MROInfo = MRORecordTool.getInstance().getInHospInfo(mroParm);
        if ("2".equals(MROInfo.getValue("MRO_CHAT_FLG", 0))) {
            this.setValue("MRO_CHAT_FLG", true);
            MRO_CHAT_FLG = true;
            lock(false);
        } else {
            this.setValue("MRO_CHAT_FLG", "N");
            MRO_CHAT_FLG = false;
            lock(true);
        }
    }
    
    /**
     * ��ѯ����סԺ��Ϣ
     */
    private void queryPatInfo(){
        if(CASE_NO.length()==0){
            this.messageBox("��ʼ����������û�о���ţ�");
            return;
        }
        patInfo = MROChrtvetrecTool.getInstance().selectdata(CASE_NO);
        // �жϴ���ֵ
        if (patInfo.getErrCode() < 0) {
            messageBox(patInfo.getErrText());
            return;
        }
        //��ֵ
        VS_CODE = patInfo.getValue("VS_DR_CODE",0);//����ҽ��
        ((TComboBox)this.getComponent("VS_CODE")).setSelectedID(VS_CODE);
        IPD_NO = patInfo.getValue("IPD_NO",0);//סԺ��
        MR_NO = patInfo.getValue("MR_NO",0);//������
    }
    
    /**
     * ��ѯ���������Ϣ
     */
    private void queryChr(){
        if(CASE_NO.length()==0){
            this.messageBox("��ʼ����������û�о���ţ�");
            return;
        }
        data = MROChrtvetrecTool.getInstance().selectAllChrtvetrec(CASE_NO);
        // �жϴ���ֵ
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ((TTable) getComponent("MROTable")).setParmValue(data);
        checkCHAT_FLG();
    }

    /**
     * ����
     */
    public void onInsert() {
        TParm parm = this.getParmForTag("VS_CODE;DEDUCT_NOTE;DEDUCT_SCORE;TYPE_CODE;EXAMINE_CODE;REPLY_DTTM;REPLY_DR_CODE;REPLY_REMK");
        //===============add by wanglong 20130909
        if (StringUtil.isNullString(parm.getValue("VS_CODE"))) {
            this.messageBox("����д����ҽʦ");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (sdf.parse(this.getText("EXAMINE_DATE"), new ParsePosition(0))==null) {
            this.messageBox("����д��ȷ���������");
            return;
        }
        String date = StringTool.getString((Timestamp) this.getValue("EXAMINE_DATE"), "yyyyMMdd");
        parm.setData("EXAMINE_DATE", date);// ʱ��
        if (StringUtil.isNullString(parm.getValue("TYPE_CODE"))) {
            this.messageBox("����д�����Ŀ");
            return;
        }
        if (StringUtil.isNullString(parm.getValue("EXAMINE_CODE"))) {
            this.messageBox("����дǷȱ����");
            return;
        }
        if (StringUtil.isNullString(parm.getValue("DEDUCT_SCORE"))) {
            this.messageBox("����д�۷�");
            return;
        }
        //===============add end
        //�Ƿ񼱼�
        if ( ( (TCheckBox)this.getComponent("URG_FLG")).isSelected())
            parm.setData("URG_FLG", "Y");
        else
            parm.setData("URG_FLG", "N");
        parm.setData("MR_NO",MR_NO);//������
        parm.setData("IPD_NO",IPD_NO);//סԺ��
        parm.setData("CASE_NO",CASE_NO);//�������
        // ȡ���ʿط�������Ϣ
        String sql =
                "SELECT A.METHOD_CODE, A.METHOD_DESC, C.METHOD_TYPE_CODE, C.METHOD_TYPE_DESC, B.EXAMINE_CODE, "
                        + "       B.TYPE_CODE, B.EXAMINE_DESC, B.ENNAME, B.DESCRIPTION, B.SCORE, B.URG_FLG, B.SPCFY_DEPT, "
                        + "       B.METHOD_PARM, B.CHECK_RANGE, B.CHECK_FLG, B.CHECK_SQL, C.EMR_CHECK_NULL, C.EMR_CHECK_TIME, "
                        + "       C.TABLE_CHECK_NULL, C.TABLE_CHECK_TIME, C.TIME_VALUE "
                        + "  FROM MRO_METHOD A, MRO_CHRTVETSTD B, MRO_METHOD_TYPE C "
                        + " WHERE A.METHOD_CODE = B.METHOD_CODE "
                        + "   AND A.METHOD_TYPE_CODE = C.METHOD_TYPE_CODE "
                        + "   AND B.CHECK_FLG = 'Y' "
                        + "   AND A.METHOD_CODE = '#' ";
        sql = sql.replaceFirst("#", this.getValueString("EXAMINE_CODE"));
        TParm methodTypeParm = new TParm(TJDODBTool.getInstance().select(sql));
        double interval = methodTypeParm.getDouble("TIME_VALUE", 0);
        if (interval != 0) {// add by wanglong 2013909
            TParm patParm = ADMInpTool.getInstance().selectall(parm);
            if ("1".equals(methodTypeParm.getValue("CHECK_RANGE", 0))) {
                Timestamp offset = rollHour(patParm.getTimestamp("IN_DATE", 0), interval);
                Timestamp now = SystemTool.getInstance().getDate();
                if (offset.getTime() > now.getTime()) {
                    if (this.messageBox("��ʾ", "�ò�����Ժδ����" + TypeTool.getInt(interval)
                            + "Сʱ����ʱЧ���ڣ�����������²�Ӧ���и�����ˣ�\n�Ƿ������", 0) != 0) {
                        return;
                    }
                }
            } else {
                Timestamp offset = rollHour(patParm.getTimestamp("DS_DATE", 0), interval);
                Timestamp now = SystemTool.getInstance().getDate();
                if (offset.getTime() > now.getTime()) {
                    if (this.messageBox("��ʾ", "�ò�����Ժδ����" + TypeTool.getInt(interval)
                            + "Сʱ����ʱЧ���ڣ�����������²�Ӧ���и�����ˣ�\n�Ƿ������", 0) != 0) {
                        return;
                    }
                }
            }
        }
        // =============add by wanglong 20130819
        parm.setData("STATUS", "0");// δͨ�����
        parm.setData("QUERYSTATUS", "1");// �������
        TParm rangeParm = MROChrtvetstdTool.getInstance().selectdata(parm.getValue("EXAMINE_CODE"));
        if (rangeParm.getErrCode() != 0) {
            this.messageBox(rangeParm.getErrText());
            return;
        }
        parm.setData("CHECK_RANGE", rangeParm.getValue("CHECK_RANGE", 0));
        parm.setData("CHECK_USER", Operator.getID());
        parm.setData("CHECK_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", parm.getData("CHECK_DATE"));
        parm.setData("OPT_TERM", Operator.getIP());
        // ==============add end
        parm.setData("DEDUCT_SCORECOUNT", "1");
        TParm result = TIOM_AppServer.executeAction("action.mro.MROChrtvetrecAction", "insertdata", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox_("���ʧ�ܣ�" + result.getErrText()+result.getErrName());
            return;
        }
        // ====================addd by wanglong 20130929
        if(((TCheckBox) this.getComponent("SEND_MSG")).isSelected()){
            TParm msgParm = new TParm();
            msgParm.setData("OPT_USER", Operator.getID());
            msgParm.setData("OPT_TERM", Operator.getIP());
            TParm caseParm = new TParm();
            caseParm.addData("CASE_NO", CASE_NO);
            msgParm.setData("CASE_NO", caseParm.getData());
            // ִ����������
            TParm msgResult =
                    TIOM_AppServer.executeAction("action.mro.MROQlayControlAction", "onBoardMessage",
                                                 msgParm);
            // �����ж�
            if (msgResult == null || !msgResult.getErrText().equals("")) {
                this.messageBox("��������Ϣ����ʧ��" + " , " + msgResult.getErrText());
                return;
            }
            // this.messageBox("���ͳɹ�");  
        }
        // ====================add end
        // ��ʾ��������
        int row =
                ((TTable) getComponent("MROTable")).addRow(parm,
                //add by wanglong 20130819
                "FLG;VS_CODE;EXAMINE_DATE;TYPE_CODE;EXAMINE_CODE;DEDUCT_SCORE;URG_FLG;DEDUCT_NOTE;REPLY_DTTM;REPLY_DR_CODE;REPLY_REMK");
        data.setRowData(row, parm);
        this.messageBox("P0005");
        this.onClear();
        this.onQuery();
    }
    
    /**
     * ����
     */
    public void onUpdate() {
//        if (selectRow == -1) {
//            this.messageBox_("��ѡ��ĳһ�����ݣ�");
//            return;
//        }
//        if (data.getValue("REPLY_DR_CODE", selectRow).trim().length() > 0) {
//            this.messageBox_("ҽ������ɣ������޸ģ�");
//            return;
//        }
        if (State.equals("ODI")) {
//            String sql =
//                    "SELECT EMAIL_STATUS,BOARD_STATUS FROM MRO_CHRTVETREC WHERE CASE_NO='#' AND EXAMINE_CODE='#'";
//            sql = sql.replaceFirst("#", CASE_NO);
//            sql = sql.replaceFirst("#", this.getValueString("EXAMINE_CODE"));
//            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//            if (!result.getValue("EMAIL_STATUS", 0).equals("Y")
//                    && !result.getValue("BOARD_STATUS", 0).equals("Y")) {
//                this.messageBox("δ��������ͨ��(���ʼ�)����Ŀ���ܻظ�");
//                return;
//            }
            if (this.getValueString("DEDUCT_NOTE").trim().equals("")) {
                this.messageBox("û�б�ע��Ϣ����Ŀ����ظ���ע");
                return;
            }
        }
        TParm parm = this.getParmForTag("VS_CODE;DEDUCT_NOTE;DEDUCT_SCORE;TYPE_CODE;EXAMINE_CODE;REPLY_DR_CODE;REPLY_REMK");
        //===============add by wanglong 20130909
        if (StringUtil.isNullString(parm.getValue("VS_CODE"))) {
            this.messageBox("����д����ҽʦ");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (sdf.parse(this.getText("EXAMINE_DATE"), new ParsePosition(0)) == null) {
            this.messageBox("����д��ȷ���������");
            return;
        }
        // ��ʱ��
        String date = StringTool.getString((Timestamp) this.getValue("EXAMINE_DATE"), "yyyyMMdd");
        parm.setData("EXAMINE_DATE", date);// ʱ��
        if (StringUtil.isNullString(parm.getValue("TYPE_CODE"))) {
            this.messageBox("����д�����Ŀ");
            return;
        }
        if (StringUtil.isNullString(parm.getValue("EXAMINE_CODE"))) {
            this.messageBox("����дǷȱ����");
            return;
        }
        if (StringUtil.isNullString(parm.getValue("DEDUCT_SCORE"))) {
            this.messageBox("����д�۷�");
            return;
        }
        //===============add end
        //�Ƿ񼱼�
        if (((TCheckBox) this.getComponent("URG_FLG")).isSelected()) parm.setData("URG_FLG", "Y");
        else parm.setData("URG_FLG", "N");
        parm.setData("CASE_NO", CASE_NO);// �������
        // ԭ��ʱ��
        parm.setData("OLD_EXAMINE_DATE", EXAMINE_DATE);
        // �ж��Ƿ��Ǵ�סԺҽ��վ����� ҽ�����лظ���
        if (State.equals("ODI")) {// ����ǻظ�ʱ��Ϊ��ǰʱ��
            parm.setData("REPLY_DTTM", SystemTool.getInstance().getDate());
        } else {// ������� ����¼ʱ��
            parm.setData("REPLY_DTTM", "");
        }
        parm.setData("OLD_EXAMINE_CODE", EXAMINE_CODE);//��˱�׼
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("DEDUCT_SCORECOUNT", data.getData("DEDUCT_SCORECOUNT",selectRow));//add by wanglong 20130909
        TParm result = TIOM_AppServer.executeAction("action.mro.MROChrtvetrecAction", "updatedata", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox_("�޸�ʧ�ܣ�" + result.getErrText());
            return;
        }
        // ѡ����
        int row = ((TTable) getComponent("MROTable")).getSelectedRow();
        if (row < 0) return;
        // ˢ�£�����ĩ��ĳ�е�ֵ
        data.setRowData(row, parm);
        ((TTable) getComponent("MROTable")).setRowParmValue(row, data);
        this.messageBox_("�޸ĳɹ���");
        this.onQuery();
    }
    /**
     * ����
     */
    public void onSave() {
        TParm parmr = new TParm();
        parmr.setData("CASE_NO", CASE_NO);
        parmr = MROQlayControlMTool.getInstance().queryQlayControlSUM(parmr);//��MRO_RECORD�в�ѯ�Ƿ���ɣ��Լ��ܷ�����Ϣ
        if (parmr.getErrCode() < 0) {
            this.messageBox(parmr.getErrText());
            return;
        }
        if ((MRO_CHAT_FLG && "0".equals(parmr.getValue("TYPERESULT", 0)))
                || (!MRO_CHAT_FLG && "1".equals(parmr.getValue("TYPERESULT", 0)))) {
            this.messageBox("������״̬�쳣�����˳��������½����ٽ��в���");
            return;
        }
        //�����ҽ��վ���� ���Ҳ����Ѿ�ͨ����� �򲻿ɱ���
        if (State.equals("ODI") && MRO_CHAT_FLG) {
            this.messageBox_("��������Ѿ�ͨ�������ɱ��棡");
            return;
        }
        //ȡ�����״̬
        if (MRO_CHAT_FLG && "N".equals(this.getValueString("MRO_CHAT_FLG"))) {
            int re = this.messageBox("��ʾ", "ȷ��Ҫȡ�����ͨ��״̬��", 0);
            if (re == 1) return;
            else {
                TParm parm = new TParm();
                parm.setData("CASE_NO", CASE_NO);
                parm.setData("MRO_CHAT_FLG", "1");
                parm.setData("TYPERESULT", "0");
                TParm result =
                        TIOM_AppServer.executeAction("action.mro.MROChrtvetrecAction",
                                                     "updateMRO_CHAT_FLG", parm);
                if (result.getErrCode() < 0) {
                    this.messageBox("E0005");
                    return;
                }
                this.messageBox("P0005");
                this.onClear();
                queryChr();
                return;
            }
        }
        //������
        if ("Y".equals(this.getValueString("MRO_CHAT_FLG"))) {
            TParm parm = new TParm();
            parm.setData("CASE_NO", CASE_NO);
            parm.setData("MRO_CHAT_FLG", "2");
            parm.setData("TYPERESULT", "1");
            TParm result =
                    TIOM_AppServer.executeAction("action.mro.MROChrtvetrecAction",
                                                 "updateMRO_CHAT_FLG", parm);
            if (result.getErrCode() < 0) {
                this.messageBox("E0005");
                return;
            }
            this.messageBox("P0005");
            this.onClear();
            queryChr();
        }else{
            if ("UPDATE".equals(updateCheck())) {
                onUpdate();
            } else {
                if (!State.equals("ODI")) onInsert();
            }
            // ========= add by wanglong 20130819
            TParm parm = new TParm();
            parm.addData("CASE_NO", CASE_NO);
            TParm result =
                    TIOM_AppServer.executeAction("action.mro.MROQlayControlAction", "updateScore", parm);//���µ÷�
            if (result.getErrCode() != 0) {
                this.messageBox(result.getErrText());
                return;
            }
            result = MROQlayControlMTool.getInstance().queryQlayControlSUM(parm.getRow(0));// ��ѯ�÷�
            if (result.getErrCode() != 0) {
                this.messageBox(result.getErrText());
                return;
            }
            this.setValue("SCORE", result.getValue("SUMSCODE", 0));//��ʾ�÷ֵ�����
            //========= add end
        }
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        if (selectRow == -1) return;
        if (MRO_CHAT_FLG) {// add by wanglong 20131025
            this.messageBox("����ȡ�����");
            return;
        }
        TParm parmr = new TParm();
        parmr.setData("CASE_NO", CASE_NO);
        parmr.setData("MR_NO", MR_NO);
        parmr = MROQlayControlMTool.getInstance().queryQlayControlSUM(parmr);// ��MRO_RECORD�в�ѯ�Ƿ���ɣ��Լ��ܷ�����Ϣ
        // �����Ƿ��Ѿ��ύ���
        if ("1".equals(parmr.getValue("TYPERESULT", 0))) {
            this.messageBox("������״̬�쳣����ˢ�½��棬�ٽ��в���");
            return;
        }
        int i = 0;
        for ( ; i < data.getCount(); i++) {
            if (data.getValue("FLG", i).equals("Y")) {
                break;
            }
        }
        if (i == data.getCount()) {
            this.messageBox("���ȹ�ѡһ������");
            return;
        }
        TTable table= ((TTable) getComponent("MROTable"));
        int successCount = 0;
        if (this.messageBox("��ʾ", "�Ƿ�ɾ��", 2) == 0) {
            for (int j = 0; j < data.getCount(); j++) {
                if (data.getValue("FLG", j).equals("Y")) {
                    if (data.getValue("REPLY_DR_CODE", j).trim().length() > 0) {
                        if (this.messageBox("��ʾ",
                                            table.getShowParmValue().getValue("EXAMINE_CODE", j)
                                                    + "��ҽ������ɻظ����Ƿ�ɾ��", 2) != 0) {
                            continue;
                        }
                    }
                    TParm parm = new TParm();
                    parm.setData("CASE_NO", CASE_NO);
                    parm.setData("EXAMINE_CODE", data.getValue("EXAMINE_CODE", j));
                    parm.setData("EXAMINE_DATE", data.getValue("EXAMINE_DATE", j));
                    TParm result =
                            TIOM_AppServer.executeAction("action.mro.MROChrtvetrecAction",
                                                         "deletedata", parm);
                    if (result.getErrCode() < 0) {
                        messageBox(result.getErrText());
                        return;
                    }
                    successCount++;
                }
            }
            if (successCount > 0) {
                this.messageBox("ɾ���ɹ���");
            }
            onClear();
            onQuery();
        }
    }
    /**
     * ҳǩѡ���¼�
     */
    public void Change() {
        TTabbedPane tp = (TTabbedPane)this.getComponent("tTabbedPane_0");
        int p_num = tp.getSelectedIndex();
        if (p_num == 0) {
            TParm data = MROPrintTool.getInstance().getNewMroRecordprintData(CASE_NO);
            if (data.getErrCode() < 0) {
                this.messageBox("E0005");
            }
            this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MRO_NEWRECORD.jhw", data);
            tp.setSelectedIndex(7);
        } else if (p_num == 1) {
            TParm admParm = new TParm();
            admParm.setData("CASE_NO", CASE_NO);
            TParm result = ADMTool.getInstance().getADM_INFO(admParm);
            Pat pat = Pat.onQueryByMrNo(result.getValue("MR_NO", 0));
            TParm parm = new TParm();
            parm.setData("SYSTEM_TYPE", "ODI");
            parm.setData("ADM_TYPE", "I");
            parm.setData("CASE_NO", CASE_NO);
            parm.setData("PAT_NAME", pat.getName());
            parm.setData("MR_NO", pat.getMrNo());
            parm.setData("IPD_NO", result.getValue("IPD_NO", 0));
            parm.setData("ADM_DATE", result.getTimestamp("IN_DATE", 0));
            parm.setData("DEPT_CODE", result.getValue("DEPT_CODE", 0));
            parm.setData("RULETYPE", "1");
            parm.setData("EMR_DATA_LIST", new TParm());
            parm.addListener("EMR_LISTENER", this, "emrListener");
            parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
            this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
            tp.setSelectedIndex(7);
        } else if (p_num == 2) {
            TParm parm = new TParm();
            parm.setData("INW", "CASE_NO", CASE_NO);
            this.openWindow("%ROOT%\\config\\inw\\INWOrderSheetPrtAndPreView.x", parm);
            tp.setSelectedIndex(7);
        } else if (p_num == 3) {
            // =================chenxi modefy 2012.05.10
            TParm admParm = new TParm();
            admParm.setData("CASE_NO", CASE_NO);
            TParm result = ADMTool.getInstance().getADM_INFO(admParm);
            Pat pat = Pat.onQueryByMrNo(result.getValue("MR_NO", 0));
            SystemTool.getInstance().OpenIE("http://172.20.40.50/ami/html/webviewer.html?showlist&un=his&pw=hishis&ris_pat_id=" + pat.getMrNo());
            tp.setSelectedIndex(7);
        } else if (p_num == 4) {
            TParm admParm = new TParm();
            admParm.setData("CASE_NO", CASE_NO);
            TParm result = ADMTool.getInstance().getADM_INFO(admParm);
            Pat pat = Pat.onQueryByMrNo(result.getValue("MR_NO", 0));
            SystemTool.getInstance().OpenIE("http://172.20.109.241/reportform.ASPX?patNO=" + pat.getMrNo());
            tp.setSelectedIndex(7);
            // ==================chenxi modefy 2012.05.10
        } else if (p_num == 5) {
            SystemTool.getInstance().OpenIE("http:///tra/index.asp?caseno=" + CASE_NO + "&CLV=1111");
            tp.setSelectedIndex(7);
        } else if (p_num == 6) {
            TParm parm = new TParm();
            parm.setData("SUM", "CASE_NO", CASE_NO);
            parm.setData("SUM", "ADM_TYPE", "I");
            parm.setData("SUM", "FLG", "MRO");
            this.openWindow("%ROOT%\\config\\sum\\SUMVitalSign.x", parm);
            tp.setSelectedIndex(7);
        }
    }

    /**
     * ҳǩѡ���¼�
     */
    public void TPChange() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                Change();
            }
        });
    }

    /**
     * Ƿȱ�����б�� �䶯�¼�
     */
    public void selectChange() {
        // �Զ���ȡ����ֵ
        this.setValue("DEDUCT_SCORE", ((TComboBox) this.getComponent("EXAMINE_CODE")).getSelectedText());
    }
    
    /**
     * �ر��¼�
     * @return boolean
     */
    public boolean onClosing() {
        switch (messageBox("��ʾ��Ϣ", "�˳��༭״̬?", this.YES_NO_OPTION)) {
            case 0:
                break;
            case 1:
                return false;
        }
        return true;
    }
    
    /**
     * �ر�
     */
    public Object onClosePanel() {
        if (State.equals("ODI")) {
            this.closeWindow();
            return "OK";
        }
        return null;
    }

    /**
     * �����ַ�����ʽת��
     * @param date String  ��ʽΪ 20090101 ת��Ϊ 2009/01/01
     */
    public String changeDF(String date){
        String result =date.substring(0,4) + "/" +date.substring(4,6) + "/" +date.substring(6);
        return result;
    }
    
    /**
     * ת��EXAMINE_DATE�ֶθ�ʽ���ڲ��ࣩ
     */
    public class OrderList extends TLabel {

        public String getTableShowValue(String s) {
            if (s.length() == 8) {
                String date = s.substring(0, 4) + "/" + s.substring(4, 6) + "/" + s.substring(6, 8);
                return date;
            } else return s;
        }
    }
    
    /**
     * ����ǰTOOLBAR (��ODIMainUI.x��ܵ��õ�)
     */
    public void onShowWindowsFunction() {
        // ��ʾUIshowTopMenu
        callFunction("UI|showTopMenu");
    }
    
    /**
     * ������ �¼�
     */
    public void onMRO_CHAT_FLG() {
        onClear();
        if ("Y".equals(this.getValueString("MRO_CHAT_FLG"))) {
            lock(false);
        } else if ("N".equals(this.getValueString("MRO_CHAT_FLG"))) {
            if (!MRO_CHAT_FLG) lock(true);
        }
    }
    
    /**
     * ���ÿؼ��Ƿ�ɱ༭
     * @param flg boolean
     */
    private void lock(boolean flg) {
        if (State.equals("ODI")) {//add by wanglong 20131104
            flg = false;
        }
        callFunction("UI|VS_CODE|setEnabled", flg);
        callFunction("UI|EXAMINE_DATE|setEnabled", flg);
        callFunction("UI|DEDUCT_NOTE|setEnabled", flg);
        callFunction("UI|TYPE_CODE|setEnabled", flg);
        callFunction("UI|EXAMINE_CODE|setEnabled", flg);
        callFunction("UI|DEDUCT_SCORE|setEnabled", flg);
        // ===================add by wanglong 20131025
        callFunction("UI|rbt1|setEnabled", flg);
        callFunction("UI|rbt2|setEnabled", flg);
        callFunction("UI|rbt3|setEnabled", flg);
        callFunction("UI|URG_FLG|setEnabled", flg);
        callFunction("UI|SEND_MSG|setEnabled", flg);
        callFunction("UI|AUTO_QLAY|setEnabled", flg);
//        callFunction("UI|MANUAL_QLAY|setEnabled", flg);
        // ===================add end
    }
    
    /**
     * �ж���inset����update
     * @return String
     */
    private String updateCheck(){
        String re = "SAVE";
        //����������ѯ���ݿ����Ƿ���ڸ��� ��Ϣ
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        //modify by wanglong 20130909
//        parm.setData("EXAMINE_DATE",StringTool.getString((Timestamp)this.getValue("EXAMINE_DATE"),"yyyyMMdd"));
        parm.setData("EXAMINE_CODE",this.getValue("EXAMINE_CODE"));
        TParm check = MROChrtvetrecTool.getInstance().selectChrData(parm);
        //����������� ���� UPDATE
        if(check.getCount()>0){
            re = "UPDATE";
        }
        return re;
    }
    
    /**
     * ���͹�������Ϣ
     * @return
     */
    public void onSendBoardMessage() {//add by wanglong 20130819
        TParm caseParm = new TParm();
        caseParm.addData("CASE_NO", CASE_NO);
        TParm parm = new TParm();
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("CASE_NO", caseParm.getData());
        // ִ����������
        TParm result = TIOM_AppServer.executeAction(
                "action.mro.MROQlayControlAction", "onBoardMessage", parm);
        // �����ж�
        if (result == null || !result.getErrText().equals("")) {
            this.messageBox("����ʧ��" + " , " + result.getErrText());
            return;
        }
        this.messageBox("���ͳɹ�");
    }
    
    /**
     * �Զ��ʿ�
     * @return
     */
    public void onAutoQlayControl() {//add by wanglong 20130819
        if (MRO_CHAT_FLG) {
            this.messageBox("�Ѿ��ύ�����ܽ����Զ��ʿ�");
            return;
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO", CASE_NO);
        TParm temp = MROQlayControlMTool.getInstance().queryQlayControlSUM(parm);
        if (!temp.getValue("TYPERESULT", 0).equals("0")) {
            this.messageBox("�Ѿ��ύ�����ܽ����Զ��ʿ�");
            return;
        }
        temp = new TParm();
        temp.setData("CASE_NO", CASE_NO);
        temp.setData("OPT_USER", Operator.getID());
        temp.setData("OPT_TERM", Operator.getIP());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("parmTEMP", temp.getData());
        TParm result =
                TIOM_AppServer.executeAction("action.mro.MROQlayControlAction",
                                             "onQlayControlMethod", parm);
        // �����ж�
        if (result == null || !result.getErrText().equals("")) {
            this.messageBox("����ʧ��" + ", " + result.getErrText());
            return;
        }
        temp = MROQlayControlMTool.getInstance().queryQlayControlSUM(parm);
        if(temp.getErrCode() != 0){
            this.messageBox(temp.getErrText());
            return;
        }
        this.setValue("SCORE", temp.getValue("SUMSCODE", 0));// ��ʾ�÷ֵ�����
        this.setValue("VS_CODE", "");
        onQuery();
    }
    
    /**
     * �ֶ��ʿ�
     * @return
     */
    public void onManualQlayControl(){//add by wanglong 20130819
        TParm parm = new TParm();
        if (patInfo.getValue("DS_DATE", 0).length()==0) {
            parm.setData("TYPE", "TYPE_IN");// ��Ժ
        } else parm.setData("TYPE", "TYPE_OUT");// ��Ժ
        parm.setData("CASE_NO", CASE_NO);
        parm.setData("MR_NO", MR_NO);
        parm.setData("PAT_NAME", patInfo.getData("PAT_NAME", 0));
        parm.setData("IPD_NO", IPD_NO);
        parm.setData("ADM_DATE", patInfo.getData("ADM_DATE", 0));
        parm.setData("DEPT_CODE", patInfo.getData("DEPT_CODE", 0));
        parm.setData("STATION_CODE", patInfo.getData("STATION_CODE", 0));
        parm.setData("TYPERESULT", patInfo.getData("TYPERESULT", 0)); // �Ƿ����
        parm.setData("OPEN_USER", Operator.getName());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("VS_DR_CODE", patInfo.getValue("VS_DR_CODE", 0));// add by wanglong 20121105
        parm.addListener("onReturnContent", this, "onReturnContent");
        this.openWindow("%ROOT%\\config\\mro\\MROQlayDataControlUI.x", parm);// �������ϸ���ֵ���ֶ��ʿ�
//        onQuery();
    }
    
    /**
     * ����
     * @param value
     */
    public void onReturnContent(String value) {//add by wanglong 20130819
//        this.setValue("SCORE", value.split(";")[1]);//��ʾ�÷ֵ�����
        this.setValue("VS_CODE", "");
        initPage();
    }
    
    /**
     * ���Excel
     */
    public void onExport() {//add by wanglong 20130819
        TTable table = (TTable)this.getComponent("MROTable");
        if (table.getRowCount()<1) {
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "���������Ŀ�嵥");
    }
    
    /**
     * ����ƫ��Сʱ
     * @param t Timestamp
     * @param hour double
     * @return Timestamp
     */
    public Timestamp rollHour(Timestamp t, double hour) {// add by wanglong 20130909
        return new Timestamp(t.getTime() + (long) hour * 60 * 60 * 1000);
    }
    
    public void onExamCodeQuery(String tag){
//        this.clearValue(tag);
        TComboBox a=((TComboBox)this.getComponent(tag));
        a.removeAll();
        a.onQuery();
    }
}
