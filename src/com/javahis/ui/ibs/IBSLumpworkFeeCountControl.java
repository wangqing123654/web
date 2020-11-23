package com.javahis.ui.ibs;

import java.sql.Timestamp;

import jdo.adm.ADMInpTool;
import jdo.bil.BILLumpWorkTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: �ײ����¼��������
 * </p>
 * 
 * <p>
 * Description: סԺ�ײͲ����Ѿ��ƷѵĲ������¼������
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 20160905
 * @version 4.5
 */
public class IBSLumpworkFeeCountControl extends TControl {
	private TTable table;

	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
		onPage();
	}

	public void onPage() {
		Timestamp rollDay = StringTool.rollDate(SystemTool.getInstance().getDate(), -7);
		String startDate = StringTool.getString(rollDay, "yyyy/MM/dd");
        // System.out.println("startDate"+startDate);
        setValue("START_DATE", startDate+" 00:00:00");
        setValue("END_DATE",StringTool.getString(SystemTool.
                getInstance().getDate(), "yyyy/MM/dd")+" 23:59:59");
		this.clearText("MR_NO;DEPT_CODE;STATION_CODE");
		table.setParmValue(new TParm());
	}
	/**
	 * ��ԃ
	 */
	public void onQuery() {
		String sDate =SystemTool.getInstance().getDateReplace(getValue("START_DATE").toString(), true).toString();
		String eDate = SystemTool.getInstance().getDateReplace(getValue("END_DATE").toString(), true).toString();
		TParm rexpParm = new TParm();
		rexpParm.setData("START_DATE", sDate);// ��ѯ��ʼ��ʱ��
		rexpParm.setData("END_DATE", eDate);// ��ѯ����ʱ��
		if (null != this.getValue("MR_NO")
				&& this.getValue("MR_NO").toString().length() > 0) {
			rexpParm.setData("MR_NO", this.getValue("MR_NO"));
		}
		if (null != this.getValue("DEPT_CODE")
				&& this.getValue("DEPT_CODE").toString().length() > 0) {
			rexpParm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
		}
		if (null != this.getValue("LUMPWORK_CODE")
				&& this.getValue("LUMPWORK_CODE").toString().length() > 0) {
			rexpParm.setData("LUMPWORK_CODE", this.getValue("LUMPWORK_CODE"));
		}
		if (null != this.getValue("STATION_CODE")
				&& this.getValue("STATION_CODE").toString().length() > 0) {
			rexpParm.setData("STATION_CODE", this.getValue("STATION_CODE"));
		}
		table.setParmValue(new TParm());
		rexpParm.setData("DS_DATE","N");
		TParm result=ADMInpTool.getInstance().selectLumpWork(rexpParm);
		if(result.getErrCode()<0){
			this.messageBox("��ѯ��������");
			return;
		}
		if(result.getCount()<=0){
			this.messageBox("û�в�ѯ������");
			return;
		}
		table.setParmValue(result);
	}
	/**
	 * ����
	 */
	public void onSave() {
		int index = table.getSelectedRow();
		if (index < 0) {
			this.messageBox("��ѡ����Ҫ����������");
			return;
		}
		TParm tableParm = table.getParmValue();
		TParm lumpParm = BILLumpWorkTool.getInstance().getLumpRate(
				tableParm.getValue("CASE_NO", index),
				tableParm.getValue("MR_NO", index),
				tableParm.getValue("LUMPWORK_CODE",index).toString());
		if (lumpParm.getErrCode() <0) {
			this.messageBox("�����ۿ۳�������:"+lumpParm.getErrText());
			return;
		}
		if (null == lumpParm.getValue("RATE")
				|| lumpParm.getValue("RATE").length() <= 0) {
			this.messageBox("û�л���ײ��ۿ�");
			return;
		}
		double rate = lumpParm.getDouble("RATE");
//		if (rate <= 0) {
//			this.messageBox("�˲����ײ��ۿ۴������⣬�ײ��ۿ�Ϊ:" + rate + ",�����Բ���");
//			return;
//		}
		if(this.messageBox("��ʾ", "�ײ��ۿ�Ϊ:"+rate+",�Ƿ����", 2)!=0){
			return;
		}
		TParm parm =new TParm();
		parm.setData("LUMPWORK_RATE", rate);//�ײ��ۿ�
		parm.setData("CASE_NO",tableParm.getValue("CASE_NO", index));
		parm.setData("CTZ1_CODE",tableParm.getValue("CTZ1_CODE", index));
		parm.setData("CTZ2_CODE",tableParm.getValue("CTZ2_CODE", index));
		parm.setData("CTZ3_CODE",tableParm.getValue("CTZ3_CODE", index));
		parm.setData("OPT_USER",Operator.getID());
		parm.setData("OPT_TERM",Operator.getIP());
	    TParm result = TIOM_AppServer.executeAction("action.ibs.IBSAction",
	                "onExeLumpWorkFeeCount", parm); // סԺ�ǼǱ���===liing
	    if (result.getErrCode() < 0) {
			this.messageBox("����ʧ��," + result.getErrText());
			return;
		}
		this.messageBox("P0005");
		onQuery();
	}
	/**
	 * 
	* @Title: onExeIncludeBatch
	* @Description: TODO(�ײ��޸����ִ������)
	* @author pangben 2015-10-20
	* @throws
	 */
	public void onExeIncludeBatch() {
		int index=table.getSelectedRow();
		if(index<0){
			this.messageBox("��ѡ����Ҫ����������");
			return;
		}
		String caseNo=table.getParmValue().getValue("CASE_NO",index);
		TParm selParm = new TParm();
		String sql = "SELECT MR_NO,LUMPWORK_RATE FROM ADM_INP WHERE CASE_NO='"
				+ caseNo
				+ "' AND IN_DATE IS NOT NULL AND CANCEL_FLG <> 'Y' AND LUMPWORK_CODE IS NOT NULL ";
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selParm.getCount() <= 0) {
			this.messageBox("�˲��������ײͻ���,�����Բ���");
			return;
		}
		if(null==selParm.getValue("LUMPWORK_RATE",0)||
				selParm.getValue("LUMPWORK_RATE",0).length()<=0||selParm.getDouble("LUMPWORK_RATE",0)==0.00){
			this.messageBox("�˲���δ�����ײ��ۿ�,�����Բ���");
    		return ;
    	}
		if (null != caseNo && caseNo.length() > 0) {
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			TParm result = TIOM_AppServer.executeAction("action.ibs.IBSAction",
					"onExeIbsLumpWorkBatch", parm);
			if (result.getErrCode() < 0) {
				this.messageBox("����ʧ��," + result.getErrText());
				return;
			}
			if (null != result.getValue("MESSAGE")
					&& result.getValue("MESSAGE").length() > 0) {
				this.messageBox(result.getValue("MESSAGE"));
				return;
			}
			this.messageBox("P0005");
			onQuery();
		} else {
			this.messageBox("��ѡ�񲡻�");
		}
	}
	/**
	 * ��ѯ������
	 */
	public void onMrNo() {
		Pat pat = new Pat();
		String mrno = getValue("MR_NO").toString().trim();
		if (!this.queryPat(mrno))
			return;
		pat = pat.onQueryByMrNo(mrno);
		// System.out.println("------------------>pat"+pat);//==liling 20140513
		// ����
		if (pat == null || "".equals(getValueString("MR_NO"))) {
			this.messageBox_("���޲���! ");
			this.onClear(); // ���
			return;
		}else{
			this.setValue("MR_NO", pat.getMrNo());
		}
	}

	/**
	 * ��ѯ������Ϣ
	 * 
	 * @param mrNo
	 *            String
	 * @return boolean
	 */
	public boolean queryPat(String mrNo) {
		Pat pat = new Pat();
		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.messageBox("E0081");
			return false;
		}
		String allMrNo = PatTool.getInstance().checkMrno(mrNo);
		if (mrNo != null && !allMrNo.equals(pat.getMrNo())) {
			// ============xueyf modify 20120307 start
			messageBox("������" + allMrNo + " �Ѻϲ���" + pat.getMrNo());
			// ============xueyf modify 20120307 stop
		}

		return true;
	}
	/**
	 * ���
	 */
	public void onClear() {
		onPage();
	}
}

