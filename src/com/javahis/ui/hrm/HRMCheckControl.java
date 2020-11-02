package com.javahis.ui.hrm;

import java.sql.Timestamp;

import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPatAdm;
import jdo.hrm.HRMSchdayDr;
import jdo.reg.SessionTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.EmrUtil;
import com.javahis.util.StringUtil;
/**
 * <p>Title: ����������������</p>
 *
 * <p>Description: ����������������</p>
 *
 * <p>Copyright: javahis 20090922</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMCheckControl extends TControl {
	//�ҺŶ���
	private HRMPatAdm patAdm;
	//ҽ������
	private HRMOrder order;
	//TABLE
	private TTable table,orderTable;
	//�ṹ������ģ������
	private String tempName;
	//������
	private HRMSchdayDr sch;
	//CASE_NO
	private String caseNo;
	//deptAttribute
	private String deptAttribute;
	/**
	 * ��ʼ���¼�
	 */
	public void onInit() {
		super.onInit();
		//��ʼ���ؼ�
		initComponent();
		//��ʼ������
		initData();
	}
	/**
	 * ��ʼ������
	 */
	private void initData() {
		//��ʼ�����ݶ���
		patAdm=new HRMPatAdm();
		order=new HRMOrder();
		//ȡ�õ�ǰSESSION_CODE
		String sessionCode = SessionTool.getInstance().getDrSessionNow("O",Operator.getRegion());
		if(StringUtil.isNullString(sessionCode)){
			this.messageBox_("ȡ��ʱ�δ���");
			return;
		}
		//ȡ�õ�ǰ���ҡ�ҽʦ�ĿƱ�����
		deptAttribute=HRMSchdayDr.getDeptAttribute();
		if(StringUtil.isNullString(deptAttribute)){
			this.messageBox_("ȡ�ÿƱ����Դ���");
			return;
		}
		// System.out.println("Check.attribute="+deptAttribute);
		// System.out.println("Check.session="+sessionCode);
		if(deptAttribute==null){
			this.messageBox_("ȡ�ÿƱ�����ʧ��");
			return;
		}
		TJDODBTool g;
		Timestamp now=TJDODBTool.getInstance().getDBTime();
		Timestamp tomorrow=StringTool.rollDate(now, 1L);
		this.setValue("START_DATE", now);
		this.setValue("END_DATE", tomorrow);
		String startDate=StringTool.getString(now,"yyyyMMdd");
		String endDate=StringTool.getString(tomorrow,"yyyyMMdd");
		//ȡ�õ�ǰҽʦӦ������Ĳ����б�
		TParm patParm=order.onQueryByDeptAttribute( deptAttribute,false,startDate,endDate);
		table.setParmValue(patParm);
	}
	/**
	 * ��ʼ���ؼ�
	 */
	private void initComponent() {
		table=(TTable)this.getComponent("TABLE");
		orderTable=(TTable)this.getComponent("ORDER_TABLE");
	}
	/**
	 * ����TABLE�����¼�������ѡ���е�CASE_NO��ʼ��ҽ������
	 */
	public void onTableClick(){
		int row=table.getSelectedRow();
		if(row<0){
			return;
		}
		if(order.onQueryByCaseNo(patAdm.getItemString(row, "CASE_NO"))==-1){
			this.messageBox_("ȡ������ʧ��");
			return;
		}
		orderTable.setDataStore(order);
		orderTable.setDSValue();

	}
	/**
	 * TABLE˫���¼�
	 */
	public void onPatChoose(){
		TParm parm = new TParm();
		//���ݼ��
		int row=table.getSelectedRow();
		if(row<0){
			return;
		}
		TParm emrParm=new TParm();
		TParm patParm=table.getParmValue();
		if(patParm==null){
			return;
		}
		int count=patParm.getCount();
		if(count<1){
			return;
		}
		//���ô򿪽ṹ�������Ĳ������򿪽ṹ������������
		caseNo=patParm.getValue("CASE_NO",row);
		order.filt(caseNo);
		patAdm.onQueryByCaseNo(caseNo);
		tempName=patParm.getValue("MR_CODE",row);
		emrParm.setData("MR_CODE",tempName);
		emrParm.setData("CASE_NO",caseNo);
		// System.out.println("emrParm="+emrParm);
		emrParm=EmrUtil.getInstance().getEmrFilePath(emrParm);
		// System.out.println("emrParm========="+emrParm);

        parm.setData("SYSTEM_TYPE", "HRM");
        parm.setData("ADM_TYPE","H");
        parm.setData("CASE_NO",caseNo);
        parm.setData("PAT_NAME",patParm.getValue("PAT_NAME",row));
        parm.setData("MR_NO",patParm.getValue("MR_NO",row));
        parm.setData("IPD_NO","");
        parm.setData("ADM_DATE",patParm.getData("REPORT_DATE",row));
        parm.setData("DEPT_CODE",patParm.getData("DEPT_CODE",row));
        parm.setData("EMR_FILE_DATA",emrParm);
        parm.setData("STYLETYPE","1");
        parm.setData("RULETYPE","2");
        parm.addListener("EMR_LISTENER",this,"emrListener");
        parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
        this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x",parm);
	}
	/**
	 * ���ýṹ�������Ľӿ���Ҫ�ķ���(������)
	 * @param parm
	 */
	public void emrListener(TParm parm){
	}
	/**
	 * ���ýṹ�������ı���ӿ���Ҫ�ķ���
	 * @param parm
	 */
	public void emrSaveListener(TParm parm){
                //TEMPWANGM20100609
		TParm result=order.saveByCheck(Operator.getRegion(), Operator.getDept(), caseNo,deptAttribute,"",false);
		if(result.getErrCode()!=0){
//			this.messageBox_(result.getErrText());
			return;
		}
		this.messageBox("P0001");
		order.resetModify();
		initData();
	}
	/**
	 * ����MR_NO��ѯ
	 */
	public void onMrNo(){
		String mrNo=this.getValueString("MR_NO");
		if(StringUtil.isNullString(mrNo)){
			return;
		}
		mrNo=StringTool.fill0(mrNo, PatTool.getInstance().getMrNoLength()); //===========  chenxi 
		this.setValue("MR_NO", mrNo);

	}
}
