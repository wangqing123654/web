package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import java.util.regex.Pattern;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import java.util.regex.Matcher;
import jdo.clp.CLPManagemTool;
import jdo.sys.Operator;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * Title: �ٴ�·��׼��׼��
 * </p>
 * 
 * <p>
 * Description: �ٴ�·��׼��׼��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class CLPManagemControl extends TControl {
	public CLPManagemControl() {

	}

	/**
	 * �������
	 */
	private String case_no;
	private String mr_no;
	// �ύ��Ӧ������
	private int commitIndex = 21;

	/**
	 * ҳ���ʼ������
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}

	// ���ݲ���ѡ������ѯ��Ӧ�Ĳ�����Ϣ
	private void initPatientInfo() {

		TParm inParm = (TParm) this.getParameter();
		case_no = inParm.getValue("CLP", "CASE_NO");
		mr_no = inParm.getValue("CLP", "MR_NO");
		this.setValue("IS_IN", "Y");
		//System.out.println("mr_no:::::"+mr_no);
		String flg = inParm.getValue("CLP", "FLG");
		if (null != flg && flg.equals("Y")) {
			callFunction("UI|close|setEnabled", false);
		}
	}

	/**
	 * ��ʼ��
	 */
	private void initPage() {
		// �󶨱���¼�
		callFunction("UI|TABLEMANAGEM|addEventListener", "TABLEMANAGEM->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// ҳ�����ʱ���ݲ���ѡ������ѯ��Ӧ�Ĳ�����Ϣ
		initPatientInfo();
		this.onQuery();
		String sql="SELECT CASE_NO FROM ADM_INP WHERE CASE_NO='"+case_no+"' AND DS_DATE IS NOT NULL";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount()>0) {//��Ժ���˲����Բ���
			callFunction("UI|save|setEnabled", false);
			callFunction("UI|delete|setEnabled", false);
			callFunction("UI|add|setEnabled", false);
			callFunction("UI|change|setEnabled", false);
			callFunction("UI|query|setEnabled", false);
			callFunction("UI|clear|setEnabled", false);
		}
	}

	// ��¼�к�
	int selectRow = -1;

	/**
	 * ����ǰTOOLBAR
	 */
	public void onShowWindowsFunction() {
		// ��ʾUIshowTopMenu
		callFunction("UI|showTopMenu");
	}

	/**
	 * ҳ���ѯ����
	 */
	public void onQuery() {
		this.getPaitientManagerListTParm();
	}

	private TParm getPaitientManagerListTParm() {
		
		// ��ѯ����
		TParm selectTparm = new TParm();
		TParm resultTParm = new TParm();
		// �޸ģ�ʹ�ò���ѡ��ҳ���mr_no
		selectTparm.setData("MR_NO", this.mr_no);
		selectTparm.setData("CASE_NO", this.case_no);
		this.putBasicSysInfoIntoParm(selectTparm);
		if(this.getValueBoolean("IS_IN")){//��ѯ����·��
			// ���ò�ѯ����
			resultTParm = CLPManagemTool.getInstance()
					.selectData(selectTparm);
			if (resultTParm.getCount() > 0) {
				TParm rowParm = resultTParm.getRow(0);
				String clncPathCode = rowParm.getValue("CLNCPATH_CODE");
				String evlCode = rowParm.getValue("EVL_CODE");
				this.setValue("CLNCPATH_CODE", clncPathCode);
				this.setValue("EVL_CODE", evlCode);
			}else{
				this.setValue("CLNCPATH_CODE", "");
				this.setValue("EVL_CODE", "");
			}
			callFunction("UI|change|setEnabled", true);
			callFunction("UI|timeInterval|setEnabled", true);
			callFunction("UI|save|setEnabled", true);
		}
		if(this.getValueBoolean("IS_CANCELLATION")){//��ѯ����·��
			// ���ò�ѯ����
			resultTParm = CLPManagemTool.getInstance()
					.selecCanceltData(selectTparm);
			callFunction("UI|change|setEnabled", false);
			callFunction("UI|timeInterval|setEnabled", false);
			callFunction("UI|save|setEnabled", false);
		}
		if(this.getValueBoolean("IS_OVERFLOW")){//��ѯ���·��
			// ���ò�ѯ����
			resultTParm = CLPManagemTool.getInstance()
					.selectOverData(selectTparm);
			callFunction("UI|change|setEnabled", false);
			callFunction("UI|timeInterval|setEnabled", false);
			callFunction("UI|save|setEnabled", false);
		}
		this.callFunction("UI|TABLEMANAGEM|setParmValue", resultTParm);
		return resultTParm;
	}

	/**
	 *���淽��
	 */
	public void onSave() {
		// �����ٴ�·��׼��׼��״̬
		TTable table = (TTable) this.getComponent("TABLEMANAGEM");
		table.acceptText();
		int selectedRow = this.getSelectedRow("TABLEMANAGEM");
		if (selectedRow < 0) {
			this.messageBox("��ѡ���ٴ�·��");
			return;
		}
		TParm parm = table.getParmValue().getRow(selectedRow);
		TParm updateParm = new TParm();
		updateParm.setData("tableParm", parm.getData());
		updateParm.setData("basicMap", this.getBasicOperatorMap());
		updateParm.setData("currentDateStr", this.getCurrentDateStr());
		TParm result = TIOM_AppServer.executeAction(
				"action.clp.CLPManagemAction", "updateManagem", updateParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0001");
		} else {
			this.messageBox("P0001");
		}
		// ����ɹ���ˢ��ҳ���¼
		this.onQuery();
	}

	/**
	 * ��ӷ���
	 */
	public void onAdd() {
		TTable table = (TTable) this.getComponent("TABLEMANAGEM");
		TParm parm = new TParm();
		parm.setData("MR_NO", this.mr_no);
		parm.setData("CASE_NO", this.case_no);
		this.putBasicSysInfoIntoParm(parm);
		TParm result = CLPManagemTool.getInstance().getPatientInfo(parm);
		if (result.getCount() <= 0) {
			this.messageBox("���Ȳ�ѯסԺ������Ϣ");
			return;
		}
		// ���ò�ѯ����
		TParm resultTParm = CLPManagemTool.getInstance().selectData(parm);
		if (result.getCount() <= 0) {
			this.messageBox("���Ȳ�ѯסԺ������Ϣ");
			return;
		}
		if (resultTParm.getCount() > 0) {
			this.messageBox("�ò����ٴ�·����Ϣ�Ѿ�����!");
			return;
		}
		if (!validData()) {
			return;
		}
		parm = getTparmFromBasicInfo();
		putBasicSysInfoIntoParm(parm);
		String sql = "SELECT CASE_NO FROM CLP_MANAGEM WHERE CASE_NO='"
				+ parm.getValue("CASE_NO") + "' AND END_DTTM IS NOT NULL";
		TParm selparm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selparm.getErrCode() < 0) {
			this.messageBox("E0002");
			return;
		}
		if (selparm.getCount("CASE_NO")>0) {
			this.messageBox("�˲������ٴ�·����Ŀ�Ѿ����,���������");
			return;
		}
		//====yanjing 20140710 ��ѯ��·���Ƿ�������ϵ����� start
		String cancleSql = "SELECT CASE_NO FROM CLP_MANAGEM WHERE CASE_NO = '"+parm.getValue("CASE_NO")+"' " +
				"AND CLNCPATH_CODE = '"+parm.getValue("CLNCPATH_CODE")+"'";
		TParm cancleParm = new TParm(TJDODBTool.getInstance().select(cancleSql));
		if (cancleParm.getErrCode() < 0) {
			this.messageBox("E0002");
			return;
		}
		if (cancleParm.getCount("CASE_NO")>0) {
			this.messageBox("�˲����������ϵĸ��ٴ�·����Ŀ");
			return;
		}
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		parm.setData("IN_DATE", datestr);
		parm.setData("START_DTTM", getCurrentDateTimeStr());
		TParm resultParam = TIOM_AppServer.executeAction(
				"action.clp.CLPManagemAction", "insertManagem", parm);
		if (resultParam.getErrCode() >= 0) {
			this.messageBox("P0002");
		} else {
			this.messageBox("E0002");
		}
		this.onQuery();

	}

	/**
	 * ɾ������
	 */
	public void onDelete() {
		selectRow = this.getSelectedRow("TABLEMANAGEM");
		if (selectRow == -1) {
			this.messageBox("��ѡ����Ҫɾ�����ٴ�·��");
			return;
		}
		if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 2) == 0) {
			TTable table = (TTable) this.getComponent("TABLEMANAGEM");
			int selRow = table.getSelectedRow();
			TParm tableParm = table.getParmValue();
			String CASE_NO = tableParm.getValue("CASE_NO", selRow);
			String CLNCPATH_CODE = tableParm.getValue("CLNCPATH_CODE", selRow);
			TParm parm = new TParm();
			parm.setData("CASE_NO", CASE_NO);
			parm.setData("CLNCPATH_CODE", CLNCPATH_CODE);
			TParm saveParm = new TParm();
			saveParm.setData("parm", parm.getData());
			saveParm.setData("basicMap", this.getBasicOperatorMap());
			TParm result = TIOM_AppServer.executeAction(
					"action.clp.CLPManagemAction", "deleteManagem", saveParm);
			if (result.getErrCode() < 0) {
				messageBox(result.getErrText());
				return;
			}
			// ɾ��������ʾ
			int row = (Integer) callFunction("UI|TABLEMANAGEM|getSelectedRow");
			if (row < 0) {
				return;
			}
			this.callFunction("UI|TABLEMANAGEM|removeRow", row);
			this.callFunction("UI|TABLEMANAGEM|setSelectRow", row);
			this.messageBox("P0003");
		} else {
			return;
		}
	}

	/**
	 *�ٴ�·���䶯
	 */
	public void onChange() {
		TTable table = (TTable) this.getComponent("TABLEMANAGEM");
		if (table.getRowCount() <= 0) {
			this.messageBox("��ѡ��·��!");
			return;
		}
		// ��ֹ�û��Ķ��ٴ�·��״̬Ϊ�ѽ��뵫δ��������������ÿ�α���������ݿ�������ȡֵ
		TParm tableParm = getPaitientManagerListTParm();
		String isInStr = (String) tableParm.getData("IS_IN", 0);
		if ("N".equals(isInStr)) {
			this.messageBox("��ȷ������ѡ·��Ϊ�ѽ��룬δ���ϻ������·��!�������·��״̬���뼰ʱ����!");
			return;
		}
		TParm sendParm = new TParm();
		sendParm.setData("OLD_CLNCPATH_CODE", this
				.getValueString("CLNCPATH_CODE"));
		sendParm.setData("MR_NO", this.mr_no);
		sendParm.setData("EVL_CODE", this.getValueString("EVL_CODE"));
		sendParm.setData("CASE_NO", this.case_no);
		String resultstr = (String) this.openDialog(
				"%ROOT%\\config\\clp\\CLPManageChangeNew.x", sendParm);
		if (null!=resultstr) {
			if (resultstr.toLowerCase().indexOf("success") >= 0) {
				this.onQuery();
			}
		}
	}

//	/**
//	 * ʵ��ʱ���趨
//	 */
//	public void durationConfig() {
//		int selectIndex = this.getSelectedRow("TABLEMANAGEM");
//		if (selectIndex < 0) {
//			this.messageBox("��ѡ���趨����");
//			return;
//		}
//		TParm sendParm = new TParm();
//		TTable table = (TTable) this.getComponent("TABLEMANAGEM");
//		TParm tableParm = table.getParmValue();
//		sendParm.setData("MR_NO", tableParm.getRow(0).getValue("MR_NO"));
//		sendParm.setData("PAT_NAME", tableParm.getRow(0).getValue("PAT_NAME"));
//		sendParm.setData("CLNCPATH_CODE", tableParm.getRow(0).getValue(
//				"CLNCPATH_CODE"));
//		sendParm.setData("CASE_NO", tableParm.getRow(0).getValue("CASE_NO"));
//		sendParm.setData("VERSION", tableParm.getRow(0).getValue("VERSION"));// =======pangben
//																				// 2012-5-28
//																				// �汾
//		sendParm
//				.setData("DEPT_CODE", tableParm.getRow(0).getValue("DEPT_CODE"));// =======pangben
//																					// 2012-5-28
//																					// ����
//		String resultstr = (String) this.openDialog(
//				"%ROOT%\\config\\clp\\CLPDurationConfig.x", sendParm);
//	}
	
	/**
	 * չ��ʵ�ʣ�·��չ����--ȥ����ʵ��ʱ���趨���������棬
	 * ��Ϊ֮ǰ���������﹦�ܣ�·��չ��--xiongwg20150428
	 */
	public void durationConfig() {
		int selectIndex = this.getSelectedRow("TABLEMANAGEM");
		if (selectIndex < 0) {
			this.messageBox("��ѡ���趨����");
			return;
		}
		//��ȡ���ݲ���
		TParm patientTParm = new TParm();
		TTable table = (TTable) this.getComponent("TABLEMANAGEM");
		//========pangben 2015-10-21 У�����ٴ�·��
		TParm tableParm = table.getParmValue();
		TParm clpParm= new TParm(TJDODBTool.getInstance().select(
				"SELECT SCHD_CODE FROM IBS_ORDD WHERE CASE_NO='"
						+ tableParm.getRow(0).getValue("CASE_NO") + "' GROUP BY SCHD_CODE"));
    	TParm clpSParm= new TParm(TJDODBTool.getInstance().select(
				"SELECT CLNCPATH_CODE FROM IBS_ORDD WHERE CASE_NO='"
						+ tableParm.getRow(0).getValue("CASE_NO") + "' GROUP BY CLNCPATH_CODE"));
    	if (clpSParm.getCount()<=0) {
			this.messageBox("�����ţ�"+ tableParm.getRow(0).getValue("MR_NO")+
					"����:"+tableParm.getRow(0).getValue("PAT_NAME")+"û�л�ô˲���ҽ������");
			return;
		}
    	if (clpSParm.getCount()>1) {
    		if (this.messageBox("��ʾ","�����ţ�"+ tableParm.getRow(0).getValue("MR_NO")+
					"����:"+tableParm.getRow(0).getValue("PAT_NAME")+"���ױ��д���Ϊ��ֵ����·�������,�Ƿ����",2)!=0) {
				return;
			}
		}
    	for (int j = 0; j < clpParm.getCount(); j++) {
			if (null==clpParm.getValue("SCHD_CODE",j)||clpParm.getValue("SCHD_CODE",j).length()<=0) {
				if (this.messageBox("��ʾ","�����ţ�"+ tableParm.getRow(0).getValue("MR_NO")+
					"����:"+tableParm.getRow(0).getValue("PAT_NAME")+"����Ϊ��ֵʱ��,�Ƿ����",2)!=0) {
					return;
				}
			}
		}
		patientTParm.addData("REGION_CODE",
                this.getOperatorMap().get("REGION_CODE"));
		patientTParm.addData("MR_NO", tableParm.getRow(0).getValue("MR_NO"));
		patientTParm.addData("PAT_NAME", tableParm.getRow(0).getValue("PAT_NAME"));
		patientTParm.addData("CLNCPATH_CODE", tableParm.getRow(0).getValue(
				"CLNCPATH_CODE"));
		patientTParm.addData("CASE_NO", tableParm.getRow(0).getValue("CASE_NO"));
		patientTParm.addData("VERSION", tableParm.getRow(0).getValue("VERSION"));// =======pangben
																				// 2012-5-28
																				// �汾
		patientTParm
				.addData("DEPT_CODE", tableParm.getRow(0).getValue("DEPT_CODE"));// =======pangben
																					// 2012-5-28
																					// ����
		patientTParm.setCount(1);
		
		TParm inputParm = new TParm();
        inputParm.setData("patientTParm", patientTParm.getData());
        inputParm.setData("deployDate", this.getDeployDate());
        inputParm.setData("operator", getOperatorMap());
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
     * ����Operator�õ�map
     * @return Map
     */
    private Map getOperatorMap() {
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
	 * �ӽ����еõ���
	 * 
	 * @return TParm
	 */
	private TParm getTparmFromBasicInfo() {
		TParm parm = new TParm();
		//this.putParamWithObjName("CASE_NO", parm);
		//this.putParamWithObjName("MR_NO", parm);
		this.putParamWithObjName("CLNCPATH_CODE", parm);
		this.putParamWithObjName("EVL_CODE", parm);
		parm.setData("CASE_NO",case_no);
		parm.setData("MR_NO",mr_no);
		return parm;
	}

	/**
	 * ������֤����
	 * 
	 * @return boolean
	 */
	private boolean validData() {
		boolean flag = true;
		if (this.getValueString("CLNCPATH_CODE") == null
				|| this.getValueString("CLNCPATH_CODE").length() <= 0) {
			this.messageBox("��ѡ���ٴ�·��");
			return false;
		}
		if (this.getValueString("EVL_CODE") == null
				|| this.getValueString("EVL_CODE").length() <= 0) {
			this.messageBox("��ѡ����������");
			return false;
		}
		return flag;
	}

	/**
	 * ҳ����շ���
	 */
	public void onClear() {
		this.setValue("CLNCPATH_CODE", "");
		this.setValue("EVL_CODE", "");
		 this.setValue("IS_IN", "Y");
		 this.onQuery();
	}

	/**
	 * ���������
	 */
	public void onTableClicked(int row) {
		TTable table = (TTable) this.getComponent("TABLEMANAGEM");
		table.acceptText();
		int selectedColumnIndex = table.getSelectedColumn();
		int selectedRowIndex = table.getSelectedRow();
		if (selectedRowIndex < 0) {
			return;
		}
		TParm tableParm = table.getParmValue();
		/* ���뵥ѡ�ж� begin */
		// �Ƿ�����ʶ
		String isInstr = (String) tableParm.getData("IS_IN", selectedRowIndex);
		boolean isInFalg = "Y".equals(isInstr) ? true : false;
		// �Ƿ���������
		String isCancelLationStr = (String) tableParm.getData(
				"IS_CANCELLATION", selectedRowIndex);
		boolean isCancelLationFlag = "Y".equals(isCancelLationStr) ? true
				: false;
		// ���������ֵ
		String isOverFlowStr = (String) tableParm.getData("IS_OVERFLOW",
				selectedRowIndex);
		boolean isOverFlowFlag = "Y".equals(isOverFlowStr) ? true : false;
		// ״̬��ʶ (״̬�������ı�ʶû�й���)
		String statusStr = (String) tableParm.getData("STATUS",
				selectedRowIndex);
		boolean isStatusFlag = "Y".equals(statusStr) ? true : false;
		// ʵ����Ҫ���õı�ʶ�����ϣ����ֵ
		String isInSetStr = isInstr;
		String isCancelLationSetStr = isCancelLationStr;
		String isOverFlowSetStr = isOverFlowStr;
		// ״ֵ̬
		String isStatusStr = "N";
		// �����롱��ѡ��
		if (selectedColumnIndex == 0) {
			if (isInFalg) {
				// ԭ��ѡ������Ҳ����ѡ��
				isInSetStr = "Y";
			} else {
				isInSetStr = "Y";
			}
			// ����
			isCancelLationSetStr = "N";
			isOverFlowSetStr = "N";
		}
		// ����
		if (selectedColumnIndex == 1) {
			if (isCancelLationFlag) {
				// ԭ��ѡ������Ҳ����ѡ��
				isCancelLationSetStr = "Y";
			} else {
				isCancelLationSetStr = "Y";
			}
			// ����
			isInSetStr = "N";
			isOverFlowSetStr = "N";
		}
		// ���
		if (selectedColumnIndex == 2) {
			if (isOverFlowFlag) {
				// ԭ��ѡ������Ҳ����ѡ��
				isOverFlowSetStr = "Y";
			} else {
				isOverFlowSetStr = "Y";
			}
			// ����
			isInSetStr = "N";
			isCancelLationSetStr = "N";
		}
		// ״̬ (ע��״̬������������ʶû�й�����ϵ)
		if (selectedColumnIndex == commitIndex) {
			if (isStatusFlag) {
				isStatusStr = "N";
			} else {
				isStatusStr = "Y";
			}
		}
		// ��ֵд��Table��Tparm
		if (selectedColumnIndex == 0 || selectedColumnIndex == 1
				|| selectedColumnIndex == 2
				|| selectedColumnIndex == commitIndex) {
			tableParm.setData("IS_IN", selectedRowIndex, isInSetStr);
			tableParm.setData("IS_CANCELLATION", selectedRowIndex,
					isCancelLationSetStr);
			tableParm
					.setData("IS_OVERFLOW", selectedRowIndex, isOverFlowSetStr);
			tableParm.setData("STATUS", selectedRowIndex, isStatusStr);
			table.setParmValue(tableParm);
		}
		/* ���뵥ѡ�ж� end */
		if(table.getRowCount() == 1){
			table.setSelectedRow(0);
		}
	}

	/**
	 * �����Ķ�Ӧ��Ԫ�����óɿ�д�����������óɲ���д
	 * 
	 * @param tableName
	 *            String
	 * @param rowNum
	 *            int
	 * @param columnNum
	 *            int
	 */
	private void setTableEnabled(String tableName, int rowNum, int columnNum) {
		TTable table = (TTable) this.getComponent(tableName);
		int totalColumnMaxLength = table.getColumnCount();
		int totalRowMaxLength = table.getRowCount();
		// ����
		String lockColumnStr = "";
		for (int i = 0; i < totalColumnMaxLength; i++) {
			if (!(i + "").equals(columnNum + "")) {
				lockColumnStr += i + ",";
			}
		}
		lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
		table.setLockColumns(lockColumnStr);
		// ����
		String lockRowStr = "";
		for (int i = 0; i < totalRowMaxLength; i++) {
			if (!(i + "").equals(rowNum + "")) {
				lockRowStr += i + ",";
			}
		}
		lockRowStr = lockRowStr.substring(0, ((lockRowStr.length() - 1) < 0 ? 0
				: (lockRowStr.length() - 1)));
		if (lockRowStr.length() > 0) {
			table.setLockRows(lockRowStr);
		}

	}

	/**
	 * ���ؼ�ֵ����TParam����(���Դ�����ò���ֵ)
	 * 
	 * @param objName
	 *            String
	 */
	private void putParamWithObjName(String objName, TParm parm,
			String paramName) {
		String objstr = this.getValueString(objName);
		objstr = objstr;
		parm.setData(paramName, objstr);
	}

	/**
	 * ���ؼ�ֵ����TParam����(���ò���ֵ��ؼ�����ͬ)
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 */
	private void putParamWithObjName(String objName, TParm parm) {
		String objstr = this.getValueString(objName);
		//System.out.println(objstr);
		objstr = objstr;
		// ����ֵ��ؼ�����ͬ
		parm.setData(objName, objstr);
	}

	/**
	 * ���ؼ�ֵ����TParam����(���Դ�����ò���ֵ)
	 * 
	 * @param objName
	 *            String
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
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 */
	private void putParamLikeWithObjName(String objName, TParm parm) {
		String objstr = this.getValueString(objName);
		if (objstr != null && objstr.length() > 0) {
			objstr = "%" + objstr + "%";
			// ����ֵ��ؼ�����ͬ
			parm.setData(objName, objstr);
		}
	}

	/**
	 * ���ڷ���������ȫƥ����в�ѯ�Ŀؼ�
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 */
	private void putParamWithObjNameForQuery(String objName, TParm parm) {
		putParamWithObjNameForQuery(objName, parm, objName);
	}

	/**
	 * ���ڷ���������ȫƥ����в�ѯ�Ŀؼ�
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 * @param paramName
	 *            String
	 */
	private void putParamWithObjNameForQuery(String objName, TParm parm,
			String paramName) {
		String objstr = this.getValueString(objName);
		if (objstr != null && objstr.length() > 0) {
			// ����ֵ��ؼ�����ͬ
			parm.setData(paramName, objstr);
		}
	}

	/**
	 * ���ؼ��Ƿ�Ϊ��
	 * 
	 * @param componentName
	 *            String
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
	 * 
	 * @param tableName
	 *            String
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
	 * 
	 * @param validData
	 *            String
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
	 * 
	 * @param parm
	 *            TParm
	 */
	private void putBasicSysInfoIntoParm(TParm parm) {
		//int total = parm.getCount();
		//System.out.println("total" + total);
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("OPT_USER", Operator.getID());
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		parm.setData("OPT_DATE", datestr);
		parm.setData("OPT_TERM", Operator.getIP());
	}

	/**
	 * �õ���ǰʱ���ַ�������
	 * 
	 * @param dataFormatStr
	 *            String
	 * @return String
	 */
	private String getCurrentDateStr(String dataFormatStr) {
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, dataFormatStr);
		return datestr;
	}

	/**
	 * �õ���ǰʱ���ַ�������
	 * 
	 * @return String
	 */
	private String getCurrentDateStr() {
		return getCurrentDateStr("yyyyMMdd");
	}

	/**
	 * ����Ƿ�Ϊ�ջ�մ�
	 * 
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
	 * 
	 * @param from
	 *            TParm
	 * @param to
	 *            TParm
	 * @param row
	 *            int
	 */
	private void cloneTParm(TParm from, TParm to, int row) {
		for (int i = 0; i < from.getNames().length; i++) {
			to.addData(from.getNames()[i], from.getValue(from.getNames()[i],
					row));
		}
	}

	/**
	 * ����Operator�õ�map
	 * 
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
	 * �õ���ǰ��ʱ�䣬������ʱ����
	 * 
	 * @return String
	 */
	private String getCurrentDateTimeStr() {
		return getCurrentDateTimeStr("yyyyMMddHHmmss");
	}

	/**
	 * �õ���ǰʱ�䣬������ʱ����
	 * 
	 * @param formatStr
	 *            String
	 * @return String
	 */
	private String getCurrentDateTimeStr(String formatStr) {
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		String dateStr = format.format(new Date());
		return dateStr;
	}

}
