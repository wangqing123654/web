package com.javahis.ui.ekt;

import java.sql.Timestamp;

import javax.swing.SwingUtilities;

import jdo.adm.ADMTool;
import jdo.ekt.EKTGreenPathTool;
import jdo.ekt.EKTIO;
import jdo.reg.PatAdmTool;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.OdoUtil;

/**
 * <p>Title: ҽ�ƿ���ɫͨ��</p>
 *
 * <p>Description: ҽ�ƿ���ɫͨ��</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20111008
 * @version 1.0
 */
public class EKTGreenPathControl extends TControl{
	private TParm parmEKT;//ҽ�ƿ���������
    public EKTGreenPathControl() {
    }
    TParm data;
    int selectRow = -1;
    TParm accptDate = new TParm();
    private boolean isSave=false;//�ܿ��Ƿ��Ѿ�ִ�в�ѯ����

    public void onInit() {
        super.onInit();
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("APPLY_DATE", now); //Ԥ������
        setValue("APPROVE_DATE", now); //Ԥ������
        setValue("APPLY_USER", Operator.getID());
        setValue("APPROVE_USER",Operator.getID());
        ( (TTable) getComponent("TABLE")).addEventListener("TABLE->"
            + TTableEvent.CLICKED, this, "onTableClicked");
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            accptDate = (TParm) obj;
        }
        setValue("ADM_TYPE", accptDate.getData("ADM_TYPE"));
        if (accptDate.getData("MR_NO") == null ||
            "".equals(accptDate.getData("MR_NO")))
            setValue("MR_NO", "");
        else {
            String mr_no = accptDate.getData("MR_NO").toString();
            this.setValue("MR_NO", mr_no);
           // onMrNo();
        }
    }

    /**
     * ���Ӷ�Table�ļ���
     *
     * @param row
     *            int
     */
    public void onTableClicked(int row) {
        // ѡ����
        if (row < 0)
            return;
        setValueForParm(
            "ADM_TYPE;CASE_NO;MR_NO;PAT_NAME;APPLY_DATE;APPLY_AMT;APPLY_USER;DESCRIPTION;APPLY_CAUSE;CANCLE_FLG;APPROVE_DATE;APPROVE_AMT;APPROVE_USER",
            data, row);
        selectRow = row;
        // ���ɱ༭
        ( (TTextField) getComponent("MR_NO")).setEnabled(false);
        // ����ɾ����ť״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * �����Ų�ѯ
     */
    public void onMrNo() {
        Pat pat = new Pat();
        String mrNo = getValue("MR_NO").toString().trim();
        pat = Pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            this.messageBox("���޲�����");
            return;
        }
        TParm parm=new TParm();
        parm.setData("MR_NO", pat.getMrNo());
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            parm.setData("REGION_CODE", Operator.getRegion());
        TParm check =PatAdmTool.getInstance().selEKTByMrNo(parm);
        if (check.getData("MR_NO", 0) == null ||
            "".equals(check.getData("MR_NO", 0))) {
            this.messageBox("�˲���û��ִ�йҺŲ�����");
            this.onClear();
            return;
        }

        if(check.getCount()==1){
            afterInitOpb(check,pat);
            return;
        }
       onRecord(check, pat);
    }
    /**
     *   ִ����ʾ���ݲ���
     */
    private void afterInitOpb(TParm caseNo,Pat pat) {
        this.setValue("MR_NO", pat.getMrNo());
        this.setValue("CASE_NO", caseNo.getData("CASE_NO", 0));
        this.setValue("PAT_NAME", pat.getName());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    onQuery();
                } catch (Exception e) {
                }
            }
        });
        isSave=true;//ִ�в�ѯ����
        this.grabFocus("APPLY_AMT");
        // ���ɱ༭
        ((TTextField) getComponent("MR_NO")).setEnabled(false);

    }
    /**
    * �����¼ѡ��
    */
   public void onRecord(TParm check,Pat pat) {
       //��ʼ��pat
       pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
       if (pat == null) {
           messageBox_("���޴˲�����!");
           //���޴˲��������ܲ��ҹҺ���Ϣ
           callFunction("UI|record|setEnabled", false);
           return;
       }
       TParm parm = new TParm();
       parm.setData("MR_NO", pat.getMrNo());
       parm.setData("PAT_NAME", pat.getName());
       parm.setData("SEX_CODE", pat.getSexCode());
       parm.setData("AGE", OdoUtil.showAge(pat.getBirthday(), SystemTool.getInstance().getDate()));
       //�ж��Ƿ����ϸ�㿪�ľ����ѡ��
       parm.setData("count", "0");
       String caseNo = (String) openDialog(
           "%ROOT%\\config\\ekt\\EKTChooseVisit.x", parm);
       if (caseNo == null || caseNo.length() == 0 || caseNo.equals("null")) {
           return;
       }
       Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
       if (reg == null) {
           messageBox("�Һ���Ϣ����!");
           return;
       }
       check=new TParm();
       check.addData("CASE_NO", reg.caseNo());
       //reg�õ������ݷ������
       //ͨ��reg��caseNo�õ�pat
       //��ʼ��opb�����ݴ���
       afterInitOpb(check,pat);
   }

    /**
     * ����
     */
    public void onInsert() {
        TParm parm = new TParm();
        parm.setData("CASE_NO",this.getValueString("CASE_NO"));
        parm.setData("MR_NO",this.getValueString("MR_NO"));
        parm.setData("PAT_NAME",this.getValueString("PAT_NAME"));
        parm.setData("ADM_TYPE",this.getValueString("ADM_TYPE"));
        parm.setData("APPLY_AMT",this.getValueString("APPLY_AMT"));
        parm.setData("APPLY_USER",this.getValueString("APPLY_USER"));
        parm.setData("DESCRIPTION",this.getValueString("DESCRIPTION"));
        parm.setData("APPLY_CAUSE",this.getValueString("APPLY_CAUSE"));
        parm.setData("APPLY_DATE", getValue("APPLY_DATE"));
        parm.setData("APPROVE_DATE", getValue("APPROVE_DATE"));
        parm.setData("APPROVE_USER", getValue("APPROVE_USER"));
        parm.setData("APPROVE_AMT", getValue("APPROVE_AMT"));
        parm.setData("CANCLE_FLG", "N");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = TIOM_AppServer.executeAction(
            "action.ekt.EKTAction",
            "insertData", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //У���Ƿ�ֹͣ����
        TParm checkStopFee = ADMTool.getInstance().checkStopFee(this.
            getValueString("CASE_NO"));
        if (checkStopFee.getErrCode() < 0) {
            err(checkStopFee.getErrCode() + " " + checkStopFee.getErrText());
            return;
        }
        //onMrNo();
        this.messageBox("P0001");
    }

    /**
     * ����
     */
    public void onUpdate() {
        TTable table = (TTable) getComponent("TABLE");
        int row = table.getSelectedRow();
        //�ж��Ƿ��Ѿ�����
        if(table.getValueAt(row,11).toString().equals("Y")){
            this.messageBox_("�Ѿ����ϲ����޸�");
            return;
        }
        TParm parm = getParmForTag("CASE_NO;MR_NO;PAT_NAME;ADM_TYPE;APPLY_AMT;APPLY_USER;DESCRIPTION;APPLY_CAUSE");
        parm.setData("APPLY_DATE", getValue("APPLY_DATE"));
        parm.setData("APPROVE_DATE", getValue("APPROVE_DATE"));
        parm.setData("APPROVE_USER", getValue("APPROVE_USER"));
        parm.setData("APPROVE_AMT", getValue("APPROVE_AMT"));
        parm.setData("CANCLE_FLG", "N");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = EKTGreenPathTool.getInstance().updatedata(parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        if (row < 0)
            return;
        // ˢ�£�����ĩ��ĳ�е�ֵ
        data.setRowData(row, parm);
        ( (TTable) getComponent("TABLE")).setRowParmValue(row, data);
        this.messageBox("P0005");
    }

    /**
     * ����
     */
    public void onSave() {
        if (!this.getPopedem("LEADER")) {
           this.messageBox("���鳤���������ɫͨ������!");
           return;
       }
       if(!isSave){
           this.messageBox("���ѯ�˲����Ƿ���ڹҺ���Ϣ");
           return;
       }
        if(!checkSaveData()){
            return;
        }
        onInsert();
        onClear();
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        if (!this.getPopedem("LEADER")) {
            this.messageBox("���鳤����ɾ����ɫͨ������!");
            return;
        }
        if (selectRow == -1) {
            return;
        }
        TParm parm = data.getRow(selectRow);
        if (parm.getValue("CANCLE_FLG").equals("Y")) {
            this.messageBox("�Ѿ�����");
            return;
        }
        if (this.messageBox("��ʾ", "ȷ��Ҫ���ϸ�����ɫͨ��������", 2) == 0) {

            parm.setData("APPLY_DATE",StringTool.getString(data.getTimestamp("APPLY_DATE",selectRow),"yyyyMMddHHmmss"));
            parm.setData("OPT_USER",Operator.getID());
            parm.setData("OPT_TERM",Operator.getIP());
            if(!checkGreenPath(parm)){
                this.messageBox_("��������");
                return;
            }
            TParm result = TIOM_AppServer.executeAction(
            "action.ekt.EKTAction",
            "cancelGreenPath", parm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            TTable table = ( (TTable) getComponent("TABLE"));
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            this.messageBox("P0005");
          //  this.clearValue("APPLY_AMT;APPLY_CAUSE;DESCRIPTION;APPROVE_USER");
            //Timestamp now = SystemTool.getInstance().getDate();
//            setValue("APPLY_DATE", now); //Ԥ������
//            setValue("APPROVE_DATE", now); //Ԥ������
//            setValue("APPLY_USER", Operator.getID());
//            setValue("APPROVE_USER", Operator.getID());
            //this.onMrNo();
            selectRow=-1;
            onClear();
        }
        else {
            return;
        }
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("MR_NO", getValueString("MR_NO"));
        parm.setDataN("CASE_NO", getValueString("CASE_NO"));
        data = EKTGreenPathTool.getInstance().selectGreenPath(parm);
        // �жϴ���ֵ
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ( (TTable) getComponent("TABLE")).setParmValue(data);
    }

    /**
     * ���
     */
    public void onClear() {
        clearValue(
            "MR_NO;CASE_NO;PAT_NAME;APPLY_AMT;APPROVE_AMT;APPLY_CAUSE;DESCRIPTION;ADM_TYPE");
        ( (TTable) getComponent("TABLE")).clearSelection();
        selectRow = -1;
        // ����ɾ����ť״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TTextField) getComponent("MR_NO")).setEnabled(true);
        onQuery();
        setValue("APPLY_DATE", SystemTool.getInstance().getDate()); //Ԥ������
        setValue("APPLY_USER", Operator.getID());
        setValue("APPROVE_USER",Operator.getID());
        isSave=false;
    }
    /**
     * ��˱�������
     * @return boolean
     */
    public boolean checkSaveData(){
        //����������ڲ���Ϊ��
        if(this.getValue("APPLY_DATE")==null){
            this.messageBox_("��ѡ����������");
            this.grabFocus("APPLY_DATE");
            return false;
        }
        //����������
        if(!(this.getValueString("APPLY_AMT").length()>0&&Double.valueOf(this.getValueString("APPLY_AMT"))!=0)){
            this.messageBox_("����д������");
            this.grabFocus("APPLY_AMT");
            return false;
        }
        //���������Ա
        if(this.getValueString("APPLY_USER").length()<=0){
            this.messageBox_("��ѡ��������Ա");
            this.grabFocus("APPLY_USER");
            return false;
        }
        //�����׼���ڲ���Ϊ��
        if(this.getValue("APPROVE_DATE")==null){
            this.messageBox_("��ѡ����׼����");
            this.grabFocus("APPROVE_DATE");
            return false;
        }
        //�����׼����
        if(!(this.getValueString("APPROVE_AMT").length()>0&&Double.valueOf(this.getValueString("APPROVE_AMT"))!=0)){
            this.messageBox_("����д��׼���");
            this.grabFocus("APPROVE_AMT");
            return false;
        }
        //�����׼��Ա
        if(this.getValueString("APPROVE_USER").length()<=0){
            this.messageBox_("��ѡ����׼��Ա");
            this.grabFocus("APPROVE_USER");
            return false;
        }
        return true;
    }
    /**
     * ��ѯREG_PATADM�е���ɫͨ����ֵ �Ƿ����Ҫ���ϵĽ����С��Ҫ���ϵĽ�������
     * @param CASE_NO String
     * @param greenPath double
     * @return boolean  true:��������   false:��������
     */
    private boolean checkGreenPath(TParm parm){
        TParm result = new TParm();
        result.setData("MR_NO",parm.getValue("MR_NO"));
        result.setData("CASE_NO",parm.getValue("CASE_NO"));
        TParm adm = PatAdmTool.getInstance().selEKTByMrNo(parm);
        //�ȽϿۿ����Ƿ���������ܽ��
        if(adm.getDouble("GREEN_BALANCE",0)>= adm.getDouble("GREEN_PATH_TOTAL",0))
            return true;
        //�ȽϿۿ����Ƿ���ڴ˴��˷ѵĽ��
        else if(adm.getDouble("GREEN_BALANCE",0) >= parm.getDouble("APPROVE_AMT"))
            return true;
        else
            return false;
    }
    /**
     * ��������
     */
    public void onReadEktCard(){
    	parmEKT = EKTIO.getInstance().TXreadEKT();
		if (null == parmEKT || parmEKT.getErrCode() < 0
				|| parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmEKT.getErrText());
			return;
		}
		this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
		onMrNo();
    }
    /**
     * ԤԼ�ҺŲ���
     */
    public void onSaveReg(){
    	if (null==this.getValue("ADM_TYPE")|| this.getValue("ADM_TYPE").toString().length()<=0) {
			this.messageBox("�������ż���");
			this.grabFocus("ADM_TYPE");
			return;
		}
    	TParm parm=new TParm();
    	parm.setData("ADM_TYPE",this.getValue("ADM_TYPE"));
    	parm.setData("MR_NO",this.getValue("MR_NO"));
    	TParm regParm = (TParm) openDialog(
				"%ROOT%\\config\\reg\\REGBespeak.x", parm);
    }
}
