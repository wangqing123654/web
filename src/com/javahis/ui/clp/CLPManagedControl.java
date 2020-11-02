package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import java.util.regex.Pattern;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.util.regex.Matcher;
import java.sql.Timestamp;
import jdo.clp.CLPManagedTool;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import com.dongyang.data.TNull;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import java.util.Map;
import java.util.HashMap;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TKeyListener;
import java.awt.event.KeyEvent;
import jdo.sys.PatTool;
import com.dongyang.ui.TTextField;

/**
 * <p>Title: չ����׼</p>
 *
 * <p>Description: չ����׼</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPManagedControl extends TControl {
    //��¼ѡ������
    int selectRow = -1;
    //��Աtable�е�ѡ����Ӧ��column����
    int checkPatientTableIndex=0;
    public CLPManagedControl() {

    }

    /**
     * ҳ���ʼ������
     */
    public void onInit() {
        super.onInit();
        initPage();
        this.onClear();
    }

    /**
     * ��ʼ��ҳ��
     */
    private void initPage() {
        //��ʼ������
//        initStation();
        //��ʼ��չ��ʱ��ؼ�ֵ
        //�󶨲�����Ϣ���
        callFunction("UI|PatientTable|addEventListener",
                     "PatientTable->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        //�󶨲�����Ϣ���valueChange
        callFunction("UI|PatientTable|addEventListener",
                     "PatientTable->" + TTableEvent.CHANGE_VALUE, this,
                     "onTableChangeValue");
        //�󶨿ؼ��¼�
        callFunction("UI|MR_NO|addEventListener",
                     "MR_NO->" + TKeyListener.KEY_RELEASED, this,
                     "onKeyReleased");
    }

    public void onKeyReleased(KeyEvent e) {
        if (e.getKeyCode() != 10) {
            return;
        }
        //TTextField ipdNo=(TTextField)this.getComponent("IPD_NO");
        //ipdNo.setFocusable(true);
        //this.onQuery();
    }

    public void onTableClicked(int row) {
        if (row < 0) {
            return;
        }
        initPatientOrderCodeTable();
        //����ѡ��checkboxѡ��״̬ begin
        TTable patientTable=(TTable)this.getComponent("PatientTable");
        int selectedIndex=patientTable.getSelectedColumn();
        if(selectedIndex==this.checkPatientTableIndex){
            TParm tableParm = patientTable.getParmValue();
            String status=tableParm.getRow(row).getValue("STATUS");
            if("Y".equals(status)){
                status="N";
            }else{
                status="Y";
            }
            tableParm.setData("STATUS",row,status);
            patientTable.setParmValue(tableParm);
        }
        //����ѡ��checkboxѡ��״̬ end
        selectRow = row;
        //���²��˵�ѡ�и�����Ϣ
        setpatientPeopleCount();
    }
    /**
     * ���Ӷ�Tableֵ�ı�ļ���
     * @param obj Object
     */
    public void onTableChangeValue(Object obj) {
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return;
        if (node.getColumn() != 0)
            return;
        //������Ա����
        this.setpatientPeopleCount();
    }

    public void initPatientOrderCodeTable() {
        int row = this.getSelectedRow("PatientTable");
        TTable table = (TTable)this.getComponent("PatientTable");
        TParm tableTParm = table.getParmValue();
        TParm parmRow = tableTParm.getRow(row);
        //this.putBasicSysInfoIntoParm(parmRow);
        TParm result = CLPManagedTool.getInstance().selectPatientOrderInfo(
                parmRow);
        this.callFunction("UI|TABLE|setParmValue", result);
    }

    /**
     * ��ѯ���˴���
     */
    public void onQuery() {
        TParm selectParm = getSelectedParm();
        this.putBasicSysInfoIntoParm(selectParm);
        TParm result = CLPManagedTool.getInstance().selectPatient(selectParm);
        if (result.getErrCode()<0) {
			this.messageBox("��ѯʧ��");
			return;
		}
        if (result.getCount()<=0) {
			this.messageBox("û�в�ѯ������");
			this.setValue("total",0+"");
			callFunction("UI|PatientTable|setParmValue", new TParm());
			return;
		}
        String sql="SELECT CASE_NO FROM CLP_MANAGED WHERE CASE_NO='";
        TParm clpParm=null;
        String openUp=this.getValueString("OPEN_UP");
        TParm parmValue=new TParm();
        //int index=0;
        String tempSql="";
        for (int i = 0; i < result.getCount(); i++) {
        	tempSql=sql+result.getValue("CASE_NO",i)+"'";
        	clpParm= new TParm(TJDODBTool.getInstance().select(tempSql));
			if (openUp.equals("2")) {
				if (clpParm.getCount()>0) {
					getParmValue(parmValue, result, i);
					parmValue.addData("OPEN_UP", "2");
				}
			
			}else if(openUp.equals("3")){
				if (clpParm.getCount()<=0) {
					getParmValue(parmValue, result, i);
					parmValue.addData("OPEN_UP","3");
				}
			}else{
				if (clpParm.getCount()>0) {
					getParmValue(parmValue, result, i);
					parmValue.addData("OPEN_UP", "2");
				}else{
					getParmValue(parmValue, result, i);
					parmValue.addData("OPEN_UP","3");
				}
			}
		}
        parmValue.setCount(parmValue.getCount("CASE_NO"));
        callFunction("UI|PatientTable|setParmValue", parmValue);
        //��������Ϣ����Ϊ0
        this.setValue("total",parmValue.getCount("CASE_NO")<0?0:parmValue.getCount("CASE_NO")+"");
    }
    private void getParmValue(TParm parm,TParm result,int index){
    	parm.addData("STATION_CODE", result.getValue("STATION_CODE",index));
    	parm.addData("PAT_NAME", result.getValue("PAT_NAME",index));
    	parm.addData("DEPT_CODE", result.getValue("DEPT_CODE",index));
    	parm.addData("STATUS", result.getValue("STATUS",index));
    	parm.addData("CASE_NO", result.getValue("CASE_NO",index));
    	parm.addData("MR_NO", result.getValue("MR_NO",index));
    	parm.addData("IPD_NO", result.getValue("IPD_NO",index));
    	parm.addData("BED_NO", result.getValue("BED_NO",index));
    	parm.addData("VERSION", result.getValue("VERSION",index));
    	parm.addData("REGION_CODE", result.getValue("REGION_CODE",index));
    	parm.addData("CLNCPATH_CODE", result.getValue("CLNCPATH_CODE",index));
    }
    /**
     * �õ���ѯ����
     * @return TParm
     */
    private TParm getSelectedParm() {
        TParm parm = new TParm();
        this.putParamWithObjNameForQuery("DEPT_CODE", parm);
        this.putParamWithObjNameForQuery("STATION_CODE", parm);
        this.putParamLikeWithObjName("BED_NO", parm);
        String mr_no = this.getValueString("MR_NO");
        if (!(mr_no == null || mr_no.length() <= 0)) {
            //�Զ�����mr_no
            mr_no = PatTool.getInstance().checkMrno(mr_no);
            parm.setData("MR_NO",mr_no);
        }
        return parm;
    }

    /**
     * ��շ���
     */
    public void onClear() {
        //���ʱ��
//        List<String> inputList = new ArrayList<String>();
//        inputList.add("DEPT_CODE");
//        inputList.add("STATION_CODE");
//        inputList.add("BED_NO");
//        inputList.add("MR_NO");
//        this.clearInput(inputList);
        this.clearValue("CLNCPATH_CODE;DEPT_CODE;STATION_CODE;BED_NO;MR_NO");
        this.setValue("AMD_TYPE", "2");
        Timestamp date = StringTool.getTimestamp(new Date());
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		this.setValue("OPEN_UP", "1");
		callFunction("UI|PatientTable|setParmValue", new TParm());
    }

    /**
     * չ��ִ�л���
     */
    public void nurseWorkListOpen() {

    }

    /**
     * չ��ʵ��
     */
    public void deployPractice() {
        //��֤
        if(!checkTablePatientCount()){
            return;
        }
        //��¼��ѯ���Ĳ���
        TParm patientTParm = getPatientTABLEInfo();
        if (patientTParm.getCount()<=0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
        TParm clpParm=null;
        TParm clpSParm=null;
        for (int i = 0; i < patientTParm.getCount(); i++) {
        	clpParm= new TParm(TJDODBTool.getInstance().select(
    				"SELECT SCHD_CODE FROM IBS_ORDD WHERE CASE_NO='"
    						+ patientTParm.getValue("CASE_NO",i) + "' GROUP BY SCHD_CODE"));
        	clpSParm= new TParm(TJDODBTool.getInstance().select(
    				"SELECT CLNCPATH_CODE FROM IBS_ORDD WHERE CASE_NO='"
    						+ patientTParm.getValue("CASE_NO",i) + "' GROUP BY CLNCPATH_CODE"));
        	if (clpSParm.getCount()<=0) {
				this.messageBox("�����ţ�"+ patientTParm.getValue("MR_NO",i)+
						"����:"+patientTParm.getValue("PAT_NAME",i)+"û�л�ô˲���ҽ������");
				return;
			}
        	if (clpSParm.getCount()>1) {
        		if (this.messageBox("��ʾ","�����ţ�"+ patientTParm.getValue("MR_NO",i)+
						"����:"+patientTParm.getValue("PAT_NAME",i)+"���ױ��д���Ϊ��ֵ����·�������,�Ƿ����",2)!=0) {
					return;
				}
			}
        	for (int j = 0; j < clpParm.getCount(); j++) {
				if (null==clpParm.getValue("SCHD_CODE",j)||clpParm.getValue("SCHD_CODE",j).length()<=0) {
					if (this.messageBox("��ʾ","�����ţ�"+ patientTParm.getValue("MR_NO",i)+
						"����:"+patientTParm.getValue("PAT_NAME",i)+"����Ϊ��ֵʱ��,�Ƿ����",2)!=0) {
						return;
					}
				}
			}
		}
        TParm inputParm = new TParm();
        inputParm.setData("patientTParm", patientTParm.getData());
        inputParm.setData("deployDate", this.getDeployDate());
        inputParm.setData("operator", getBasicOperatorMap());
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPManagedAction",
                "deployPractice", inputParm);
        if (result.getErrCode() < 0) {
            this.messageBox("չ��ʧ��");
        } else {
            this.messageBox("չ���ɹ�");
        }

    }
    /**
     * ����ѯ������ѡ�в����ĸ����Ƿ����0����-��ѯ����
     * @return boolean
     */
    private boolean checkTablePatientCount(){
        boolean flag=true;
        //����ѡ�еĲ�������
        this.setpatientPeopleCount();
        int totalCount=this.getIntValue(this.getValueString("total"));
        if(totalCount>0){
            flag=true;
        }else{
            this.messageBox("����ѡ����Ҫչ���Ĳ�����Ϣ");
            flag=false;
        }
        return flag;
    }

    /**
     * ȫѡ���˰�ť���
     */
    public void checkAllChecked() {
        setPatientTableCheck();
        //���ñ���ѡ��״̬
        setPatientTableCheck();
        //���²�������
        this.setpatientPeopleCount();
    }

    /**
     * ���ò�������ѡ��״̬
     */
    private void setPatientTableCheck() {
        TCheckBox tchkbox = (TCheckBox)this.getComponent("checkAll");
        TTable table = (TTable)this.getComponent("PatientTable");
        TParm tableParm = table.getParmValue();
        String tmpValue = "N";
        if (tchkbox.isSelected()) {
            tmpValue = "Y";
        }
        for (int i = 0; i < tableParm.getCount(); i++) {
            tableParm.setData("STATUS", i, tmpValue);
        }
        table.setParmValue(tableParm);
    }

//    /**
//     * �õ�ѡȡ�Ĳ���ѡ��ʽ
//     * ���� 1:ȫ�� 2���Ӳ���ѡ�� 3:�Ӵ���ѡ�� 4 : ��סԺ��ѡ�� 5:mr_no 6:ipd_no
//     */
//    private int getPatientSelectType() {
//        int returnvalue = -1;
//        //ȫԺ����
//        TRadioButton allPatient = (TRadioButton)this.getComponent("AllPatient");
//        if (allPatient.isSelected()) {
//            returnvalue = 1;
//        }
//        //���ݴ���ѡ����
//        TRadioButton patientByBed = (TRadioButton)this.getComponent(
//                "PatientByBed");
//        if (patientByBed.isSelected()) {
//            returnvalue = 3;
//        }
////        //����case_noѡ����
////        TRadioButton patientByCaseNo = (TRadioButton)this.getComponent("PatientByCaseNo");
////        if(patientByCaseNo.isSelected()){
////            returnvalue=4;
////        }
//        //����mr_noѡ����
//        TRadioButton patientByMrNo = (TRadioButton)this.getComponent(
//                "PatientByMrNo");
//        if (patientByMrNo.isSelected()) {
//            returnvalue = 5;
//        }
//        //����ipd_noѡ����
//        TRadioButton patientByIPDNo = (TRadioButton)this.getComponent(
//                "PatientByIPDNo");
//        if (patientByIPDNo.isSelected()) {
//            returnvalue = 6;
//        }
//        //���ݲ���ѡ����
//        TRadioButton patientByStation = (TRadioButton)this.getComponent(
//                "PatientByStation");
//        if (patientByStation.isSelected()) {
//            returnvalue = 2;
//        }
//        return returnvalue;
//    }

//    /**
//     * �����û�ѡ���ѯ����Ӧ�Ĳ�����Ϣ
//     * ���ʹ������£�1:ȫ�� 2���Ӳ���ѡ�� 3:�Ӵ���ѡ�� 4 : ��סԺ��ѡ�� 5:mr_no 6 :ipd_no
//     * @return TParm
//     */
//    private TParm getPatientInfoByChoiceTypeId(int  choiceId){
//        if(choiceId == -1){
//            return null;
//        }
//        TParm returnParm = new TParm();
//        TParm selectParm = new TParm();
//        selectParm.setData("REGION_CODE",Operator.getRegion());
//        //ȫ��
//        if(choiceId==1){
//            //���޲���
//            returnParm = CLPManagedTool.getInstance().selectPatient(selectParm);
//            System.out.println("���в��˸���:" + returnParm);
//        }
//        //����ѡ��
//        if(choiceId==2){
//            TTable table =(TTable)this.getComponent("TABLE");
//            //����table���һ������ֵ�����Ӵ��д���table���һ�������ֵ�޷���ȡ
//            table.acceptText();
//            int rowcount= table.getRowCount();
//            List stationList = new ArrayList();
//            for(int i=0;i<rowcount;i++){
//                String isFlagStr = (String)table.getValueAt(i,3);
//                if("Y".equals(isFlagStr)){
//                    stationList.add(table.getValueAt(i,0));
//                }
//            }
//            selectParm.setData("StationList",stationList);
//            returnParm = CLPManagedTool.getInstance().selectPatientByStationCodeList(selectParm);
//        }
//        //����ѡ��
//        if(choiceId==3){
//            selectParm.setData("BED_NO",this.getValue("bed_no"));
//            returnParm = CLPManagedTool.getInstance().selectPatient(selectParm);
//            System.out.println("���в��˸���:" + returnParm);
//        }
//        //case_noѡ��
//        if(choiceId==4){
//            System.out.println("סԺ�Ų�ѯ����");
//            selectParm.setData("CASE_NO",this.getValue("case_no"));
//            returnParm = CLPManagedTool.getInstance().selectPatient(selectParm);
//            System.out.println("���в��˸���:" + returnParm);
//        }
//        //mr_noѡ��
//        if(choiceId==5){
//            System.out.println("mr_no��ѯ����");
//            String mr_no=(String)this.getValue("mr_no");
//            mr_no = PatTool.getInstance().checkMrno(mr_no);
//            selectParm.setData("MR_NO",mr_no);
//            returnParm = CLPManagedTool.getInstance().selectPatient(selectParm);
//            System.out.println("���в��˸���:" + returnParm);
//        }
//        //ipd_noѡ��
//        if(choiceId==6){
//            System.out.println("סԺ��ipd_no��ѯ����");
//            selectParm.setData("IPD_NO",this.getValue("ipd_no"));
//            returnParm = CLPManagedTool.getInstance().selectPatient(selectParm);
//            System.out.println("���в��˸���:" + returnParm);
//        }
//        return returnParm;
//    }
    /**
     * ��ԭʵ��
     */
    public void returnPractice() {
        //��֤
        if (!checkTablePatientCount()) {
            return;
        }
        //��¼��ѯ���Ĳ���
        TParm patientTParm = getPatientTABLEInfo();
        TParm inputParm = new TParm();
        inputParm.setData("patientTParm", patientTParm.getData());
        inputParm.setData("operator", getBasicOperatorMap());
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPManagedAction",
                "returnPractice", inputParm);
        if (result.getErrCode() < 0) {
            this.messageBox("��ԭʧ��");
        } else {
            this.messageBox("��ԭ�ɹ�");
        }
    }

    /**
     * չ����׼
     */
    public void deployStandard() {
        //��֤
        if (!checkTablePatientCount()) {
            return;
        }
        //��¼��ѯ���Ĳ���
        TParm patientTParm = getPatientTABLEInfo();
        TParm inputParm = new TParm();
        inputParm.setData("patientTParm", patientTParm.getData());
        inputParm.setData("deployDate", this.getDeployDate());
        inputParm.setData("operator", getBasicOperatorMap());
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPManagedAction",
                "deployStandard", inputParm);
        if (result.getErrCode() < 0) {
            this.messageBox("չ����׼ʧ��");
        } else {
            this.messageBox("չ����׼�ɹ�");
        }
    }

    /**
     * ���ò�����������
     */
    private void setpatientPeopleCount() {
        TTable table = (TTable)this.getComponent("PatientTable");
//        int total = 0;
//        table.acceptText();
//        TParm tableParm = table.getParmValue();
//        for (int i = 0; i < tableParm.getCount(); i++) {
//            TParm parmRow = tableParm.getRow(i);
//            if ("Y".equalsIgnoreCase(parmRow.getValue("STATUS"))) {
//                total++;
//            }
//        }
//        this.setValue("total", total + "");
    }

    /**
     * �õ����������ѡ��Ĳ���
     * @return TParm
     */
    private TParm getPatientTABLEInfo() {
        TParm returnParm=new TParm();
        TTable table = (TTable)this.getComponent("PatientTable");
        table.acceptText();
        TParm tableParm = (TParm)this.callFunction(
                "UI|PatientTable|getParmValue");
        int count=0;
        for (int i = 0; i < tableParm.getCount(); i++) {
            TParm rowParm = tableParm.getRow(i);
            if ("N".equalsIgnoreCase(rowParm.getValue("STATUS"))) {
            }else{
            	//============pangb  20120522 �Ż�����
            	returnParm.setRowData(count, tableParm, i);
            	//============pangb  20120522 stop
                count++;
            }
        }
//        for (Integer tmp : rowIdList) {
//            tableParm.removeRow(tmp);
//        }
//        tableParm.setCount(count);
        returnParm.setCount(count);
        return returnParm;
    }

    /**
     * �õ�չ��ʱ��
     * @return String
     */
    private String getDeployDate() {
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        String datestrtmp = this.getValueString("deployDate");
        if (!this.checkNullAndEmpty(datestrtmp)) {
            datestrtmp = datestr;
        } else {
            datestrtmp = datestrtmp.substring(0, 10).replace("-", "");
        }
        return datestrtmp;
    }

    /**
     * �����Ķ�Ӧ��Ԫ�����óɿ�д�����������óɲ���д
     * @param tableName String
     * @param rowNum int
     * @param columnNum int
     */
    private void setTableEnabled(String tableName, int rowNum, int columnNum) {
        TTable table = (TTable)this.getComponent(tableName);
        int totalColumnMaxLength = table.getColumnCount();
        int totalRowMaxLength = table.getRowCount();
        //����
        String lockColumnStr = "";
        for (int i = 0; i < totalColumnMaxLength; i++) {
            if (!(i + "").equals(columnNum + "")) {
                lockColumnStr += i + ",";
            }
        }
        lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
        table.setLockColumns(lockColumnStr);
        //����
        String lockRowStr = "";
        for (int i = 0; i < totalRowMaxLength; i++) {
            if (!(i + "").equals(rowNum + "")) {
                lockRowStr += i + ",";
            }
        }
        lockRowStr = lockRowStr.substring(0,
                                          ((lockRowStr.length() - 1) < 0 ? 0 :
                                           (lockRowStr.length() - 1)));
        if (lockRowStr.length() > 0) {
            table.setLockRows(lockRowStr);
        }

    }

    /**
     * ���ؼ�ֵ����TParam����(���Դ�����ò���ֵ)
     * @param objName String
     */
    private void putParamWithObjName(String objName, TParm parm,
                                     String paramName) {
        String objstr = this.getValueString(objName);
        objstr = objstr;
        parm.setData(paramName, objstr);
    }


    /**
     * ���ؼ�ֵ����TParam����(���ò���ֵ��ؼ�����ͬ)
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        objstr = objstr;
        //����ֵ��ؼ�����ͬ
        parm.setData(objName, objstr);
    }

    /**
     * ���ؼ�ֵ����TParam����(���Դ�����ò���ֵ)
     * @param objName String
     */
    private void putParamLikeWithObjName(String objName, TParm parm,
                                         String paramName) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr + "%";
            parm.setData(paramName, objstr);
        }

    }

    /**
     * ���ؼ�ֵ����TParam����(���ò���ֵ��ؼ�����ͬ)
     * @param objName String
     * @param parm TParm
     */
    private void putParamLikeWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr + "%";
            //����ֵ��ؼ�����ͬ
            parm.setData(objName, objstr);
        }
    }

    /**
     * ���ڷ���������ȫƥ����в�ѯ�Ŀؼ�
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjNameForQuery(String objName, TParm parm) {
        putParamWithObjNameForQuery(objName, parm, objName);
    }

    /**
     * ���ڷ���������ȫƥ����в�ѯ�Ŀؼ�
     * @param objName String
     * @param parm TParm
     * @param paramName String
     */
    private void putParamWithObjNameForQuery(String objName, TParm parm,
                                             String paramName) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            //����ֵ��ؼ�����ͬ
            parm.setData(paramName, objstr);
        }
    }

    /**
     * ���ؼ��Ƿ�Ϊ��
     * @param componentName String
     * @return boolean
     */
    private boolean checkComponentNullOrEmpty(String componentName) {
        if (componentName == null || "".equals(componentName)) {
            return false;
        }
        String valueStr = this.getValueString(componentName);
        if (valueStr == null || "".equals(valueStr)) {
            return false;
        }
        return true;
    }

    /**
     * �õ�ָ��table��ѡ����
     * @param tableName String
     * @return int
     */
    private int getSelectedRow(String tableName) {
        int selectedIndex = -1;
        if (tableName == null || tableName.length() <= 0) {
            return -1;
        }
        Object componentObj = this.getComponent(tableName);
        if (!(componentObj instanceof TTable)) {
            return -1;
        }
        TTable table = (TTable) componentObj;
        selectedIndex = table.getSelectedRow();
        return selectedIndex;
    }

    /**
     * ������֤����
     * @param validData String
     * @return boolean
     */
    private boolean validNumber(String validData) {
        Pattern p = Pattern.compile("[0-9]{1,}");
        Matcher match = p.matcher(validData);
        if (!match.matches()) {
            return false;
        }
        return true;
    }

    /**
     * ��TParm�м���ϵͳĬ����Ϣ
     * @param parm TParm
     */
    private void putBasicSysInfoIntoParm(TParm parm) {
        int total = parm.getCount();
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM",  Operator.getIP());
        //parm.setData("MR_NO", this.getValue("MR_NO"));
        parm.setData("AMD_TYPE",  this.getValue("AMD_TYPE"));
        parm.setData("CLNCPATH_CODE",  this.getValue("CLNCPATH_CODE"));
        parm.setData("IPD_NO", "");
        parm.setData("START_DATE", SystemTool.getInstance().getDateReplace(this.getValueString("START_DATE"), true).toString());
        parm.setData("END_DATE", SystemTool.getInstance().getDateReplace(this.getValueString("END_DATE"), true).toString());
    }

    /**
     * ����Operator�õ�map
     * @return Map
     */
    private Map getBasicOperatorMap() {
        Map map = new HashMap();
        map.put("REGION_CODE", Operator.getRegion());
        map.put("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        map.put("OPT_DATE", datestr);
        map.put("OPT_TERM", Operator.getIP());
        return map;
    }

    /**
     * �õ���ǰʱ���ַ�������
     * @param dataFormatStr String
     * @return String
     */
    private String getCurrentDateStr(String dataFormatStr) {
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, dataFormatStr);
        return datestr;
    }

    /**
     * �õ���ǰʱ���ַ�������
     * @return String
     */
    private String getCurrentDateStr() {
        return getCurrentDateStr("yyyyMMdd");
    }

    /**
     * ����Ƿ�Ϊ�ջ�մ�
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr) {
        if (checkstr == null) {
            return false;
        }
        if ("".equals(checkstr)) {
            return false;
        }
        return true;
    }

    /**
     * ����TParm
     * @param from TParm
     * @param to TParm
     * @param row int
     */
    private void cloneTParm(TParm from, TParm to, int row) {
        for (int i = 0; i < from.getNames().length; i++) {
            to.addData(from.getNames()[i],
                       from.getValue(from.getNames()[i], row));
        }
    }

    /**
     * ��¡����
     * @param parm TParm
     * @return TParm
     */
    private TParm cloneTParm(TParm from) {
        TParm returnTParm = new TParm();
        for (int i = 0; i < from.getNames().length; i++) {
            returnTParm.setData(from.getNames()[i],
                                from.getValue(from.getNames()[i]));
        }
        return returnTParm;
    }

    /**
     * ����TParm ���null�ķ���
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNullVector(TParm parm, String keyStr, Class type) {
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getData(keyStr, i) == null) {
                TNull tnull = new TNull(type);
                parm.setData(keyStr, i, tnull);
            }
        }
    }

    /**
     * ����TParm ��nullֵ����
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNull(TParm parm, String keyStr, Class type) {
        if (parm.getData(keyStr) == null) {
            TNull tnull = new TNull(type);
            parm.setData(keyStr, tnull);
        }
    }

    /**
     * ��ռ����ж�Ӧ��������ֵ
     * @param inputNames List
     */
    private void clearInput(List<String> inputNames) {
        for (String inputstr : inputNames) {
            this.setValue(inputstr, "");
        }
    }

    /**
     * �õ�int��ֵ
     * @param str String
     * @return int
     */
    private int getIntValue(String str) {
        int tmp = 0;
        try {
            tmp = Integer.parseInt(str);
        } catch (Exception e) {
            tmp = 0;
        }
        return tmp;
    }


}
