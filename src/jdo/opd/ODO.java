package jdo.opd;

import java.sql.Timestamp;

import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;

import com.dongyang.data.StringValue;
import com.dongyang.data.TParm;
import com.dongyang.data.TimestampValue;
import com.dongyang.jdo.TJDOObject;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.javahis.util.JavaHisDebug;

/**
 *
 * <p>Title: ODO���ṹ
 *
 * <p>Description: ODO���ṹ</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: javahis
 *
 * @author ehui 20080911
 * @version 1.0
 */
public class ODO extends TJDOObject {
	private Reg reg;
	/**
	 * ���߶���
	 */
	private Pat pat;
	/**
	 * �ż�ס��
	 */
	private String admType;
	/**
	 * �����
	 */
	private String caseNo;
	/**
	 * ҽʦ
	 */
	private String drCode;
	/**
	 * ��ͯע��
	 */
	private boolean Child;
	/**
	 * ����ʱ��
	 */
	private TimestampValue orderDate=new TimestampValue(null);
	/**
	 * ����
	 */
	private StringValue subjText = new StringValue(null);
	/**
	 * �ֲ�ʷ
	 */
	private StringValue objText = new StringValue(null);
	/**
	 * ����
	 */
	private StringValue physexamRec = new StringValue(null);
	/**
	 * ����ʷ
	 */
	private MedhistoryList medhistoryList;
	/**
	 * ����ʷ
	 */
	private DrugAllergyList drugAllergyList;
	/**
	 * ���
	 */
	private DiagRecList diagRecList;
	/**
	 * ����ǩ
	 */
	private PrescriptionList prescriptionList;
	/**
	 * ҽ����ʷ
	 */
	private OrderHistoryList orderhistoryList;
	/**
	 * �½����
	 */
	private boolean newFlg;
	/**
	 * �ݴ���
	 */
	private boolean tempSaveFlg;

	/**
	 * ������
	 */
	public ODO() {
		//��ʼ�������
		setCaseNo("");
		//��ʼ���ż�ס��
		setAdmType("");
		//��ʼ������
		setSubjText("");
		//��ʼ���ֲ�ʷ
		setObjText("");
		//��ʼ������
		setPhysexamRec("");
		//��ʼ��ҽʦ
		setDrCode("");
		//��ʼ������ʷ
		setMedhistoryList(new MedhistoryList());
		//��ʼ�����
		setDiagRecList(new DiagRecList());
		//��ʼ������ʷ
		setDrugAllergyList(new DrugAllergyList());
		PrescriptionList p=new PrescriptionList(this);
		p.setReg(this.getReg());
		//��ʼ������ǩ
		setPrescriptionList(p);
		//��ʼ��ҽ����ʷ
		setOrderhistoryList(new OrderHistoryList());
		//���ô洢״̬�ı��λ
		this.setNewFlg(true);
	}
	/**
	 * ���ö�ͯע��
	 * @param child boolean
	 */
	public void SetChild(boolean child){
		this.Child=child;
	}
	/**
	 * �õ���ͯע��
	 * @return child boolean
	 */
	public boolean isChild(){
		return this.Child;
	}
	/**
	 * ���ùҺŶ���
	 * @param reg �ҺŶ���
	 */
	public void setReg(Reg reg){
		if(reg==null){
			this.reg=new Reg();
			return;
		}
		this.reg=reg;
		this.setPat(reg.getPat());
		this.prescriptionList.setReg(this.getReg());
	}
	/**
	 * �õ��ҺŶ���
	 * @return Reg ����
	 */
	public Reg getReg(){
		return this.reg;
	}
	/**
	 * ���û��߶���
	 * @param pat Pat
	 */
	public void setPat(Pat pat) {
		this.pat = pat;
		
	}

	/**
	 * �õ����߶���
	 * @return Pat
	 */
	public Pat getPat() {
		return pat;
	}

	/**
	 * �����ż�ס��
	 * @param value String
	 */
	public void setAdmType(String value) {
		admType = value;
	}

	/**
	 * �õ��ż�ס��
	 * @return String
	 */
	public String getAdmType() {
		return admType;
	}

	/**
	 * ���������
	 * @param value String
	 */
	public void setCaseNo(String value) {
		this.caseNo = value;
	}

	/**
	 * �õ������
	 * @return String
	 */
	public String getCaseNo() {
		return caseNo;
	}

	/**
	 * ����ҽʦ
	 * @param value String
	 */
	public void setDrCode(String value) {
		this.drCode = value;
	}

	/**
	 * �õ�ҽʦ
	 * @return String
	 */
	public String getDrCode() {
		return drCode;
	}

	
	/**
	 * ���ÿ�������
	 * @param orderDate Timestamp
	 */
	public void setOrderDate(Timestamp orderDate) {
		this.orderDate.setValue(orderDate);
	}

	/**
	 * �õ�����ʱ��
	 * @return Timestamp
	 */
	public Timestamp getOrderDate() {
		return orderDate.getValue();
	}

	/**
	 * ��������
	 * @param value String
	 */
	public void setSubjText(String value) {
		subjText.setValue(value);
	}

	/**
	 * �õ�����
	 * @return String
	 */
	public String getSubjText() {
		return subjText.getValue();
	}

	/**
	 * �޸�����
	 * @param value String
	 */
	public void modifySubjText(String value) {
		subjText.modifyValue(value);
	}

	/**
	 * �����ֲ�ʷ
	 * @param value String
	 */
	public void setObjText(String value) {
		objText.setValue(value);
	}

	/**
	 * �õ��ֲ�ʷ
	 * @return String
	 */
	public String getObjText() {
		return objText.getValue();
	}

	/**
	 * �޸�
	 * @param value String
	 */
	public void modifyObjText(String value) {
		objText.modifyValue(value);
	}

	/**
	 * ��������
	 * @param value String
	 */
	public void setPhysexamRec(String value) {
		physexamRec.setValue(value);
	}

	/**
	 * �õ�����
	 * @return String
	 */
	public String getPhysexamRec() {
		return physexamRec.getValue();
	}

	/**
	 * �޸�����
	 * @param value String
	 */
	public void modifyPhysexamRec(String value) {
		physexamRec.modifyValue(value);
	}

	/**
	 * ���ü���ʷ
	 * @param medhistoryList MedhistoryList
	 */
	public void setMedhistoryList(MedhistoryList medhistoryList) {
		this.medhistoryList = medhistoryList;
	}

	/**
	 * �õ�����ʷ
	 * @return MedhistoryList
	 */
	public MedhistoryList getMedhistoryList() {
		return medhistoryList;
	}

	/**
	 * �������
	 * @param diagRecList DiagRecList
	 */
	public void setDiagRecList(DiagRecList diagRecList) {
		this.diagRecList = diagRecList;
	}

	/**
	 * �õ����
	 * @return DiagRecList
	 */
	public DiagRecList getDiagRecList() {
		return diagRecList;
	}

	/**
	 * ���ù���ʷ
	 * @param drugAllergyList DrugAllergyList
	 */
	public void setDrugAllergyList(DrugAllergyList drugAllergyList) {
		this.drugAllergyList = drugAllergyList;
	}

	/**
	 * �õ�����ʷ
	 * @return DrugAllergyList
	 */
	public DrugAllergyList getDrugAllergyList() {
		return drugAllergyList;
	}

	/**
	 * ���ô���ǩ
	 * @param prescriptionList PrescriptionList
	 */
	public void setPrescriptionList(PrescriptionList prescriptionList) {
		this.prescriptionList = prescriptionList;
	}

	/**
	 * �õ�����ǩ
	 * @return PrescriptionList
	 */
	public PrescriptionList getPrescriptionList() {
		return prescriptionList;
	}

	/**
	 * �õ�ҽ����ʷ
	 * @return orderhistoryList
	 */
	public OrderHistoryList getOrderhistoryList() {
		return orderhistoryList;
	}

	/**
	 * ����ҽ����ʷ
	 * @param orderhistoryList OrderHistoryList
	 */
	public void setOrderhistoryList(OrderHistoryList orderhistoryList) {
		this.orderhistoryList = orderhistoryList;
	}

	/**
	 * �����������
	 * @param newFlg boolean
	 */
	public void setNewFlg(boolean newFlg) {
		this.newFlg = newFlg;
	}

	/**
	 * �Ƿ�������״̬
	 * @return boolean true �ǵ�һ�α��� false ���ǵ�һ�α���
	 */
	public boolean isNewFlg() {
		return newFlg;
	}

	/**
	 * �����ݴ���
	 * @param tempSaveFlg boolean
	 */
	public void setTempSaveFlg(boolean tempSaveFlg) {
		this.tempSaveFlg = tempSaveFlg;
	}

	/**
	 * �Ƿ����ݴ�
	 * @return boolean true �ݴ� false ����
	 */
	public boolean isTempSaveFlg() {
		return tempSaveFlg;
	}

	/**
	 * ��ʼ������
	 * @param parm TParm
	 * @return boolean �棺�ɹ����٣�ʧ��
	 */
	public boolean initParm(TParm parm) {
		if (parm == null)
			return false;
		//��ʼ�����߿���
		if (!initSubjrecParm(parm.getParm("SUBJREC")))
			return false;
		//��ʼ������
		if (!initPrescription(parm.getParm("ORDER")))
			return false;
		//��ʼ�����
		if (!initDiagRec(parm.getParm("DIAGREC")))
			return false;
		//��ʼ������ʷ
		if (!initDrugAllergy(parm.getParm("DRUGALLERGY")))
			return false;
		//��ʼ������ʷ
		if (!initMedHistory(parm.getParm("MEDHISTORY")))
			return false;
		//��ʼ��������ʷ
		if (!initOrderHistoryList(parm.getParm("ORDERHISTORY")))
			return false;
		//���ÿ���ע��
		if(reg!=null){
			reg.modifySeeDrFlg("Y");
		}
		
		return true;
	}

	/**
	 * ��ʼ������
	 * @param parm
	 * @return boolean �棺�ɹ����٣�ʧ�� 
	 */
	public boolean initPrescription(TParm parm) {
		if (parm == null)
			return false;
		return getPrescriptionList().initParm(parm);
	}

	/**
	 * ��ʼ�����߿���
	 * @param parm TParm
	 * @return boolean �棺�ɹ����٣�ʧ��
	 */
	public boolean initSubjrecParm(TParm parm) {
		if (parm == null)
			return false;
		//�����
		setCaseNo(parm.getValue("CASE_NO", 0));
		//�ż�ס��
		setAdmType(parm.getValue("ADM_TYPE", 0));
		//ҽʦ
		setDrCode(parm.getValue("DR_CODE", 0));
		//�õ�����
		setSubjText(parm.getValue("SUBJ_TEXT", 0));
		//�ֲ�ʷ
		setObjText(parm.getValue("OBJ_TEXT", 0));
		//����
		setPhysexamRec(parm.getValue("PHYSEXAM_REC", 0));
		//��������
		setOrderDate(parm.getTimestamp("ORDER_DATE", 0));
		return true;
	}

	/**
	 * ��ʼ������ʷ
	 * @param parm TParm
	 * @return boolean �棺�ɹ����٣�ʧ��
	 */
	public boolean initDrugAllergy(TParm parm) {
		if (parm == null)
			return false;
		return this.getDrugAllergyList().initParm(parm);
	}

	/**
	 * ��ʼ������ʷ
	 * @param parm TParm
	 * @return boolean �棺�ɹ����٣�ʧ��
	 */
	public boolean initMedHistory(TParm parm) {
		if (parm == null)
			return false;
		//System.out.println("inmethodd"+parm);
		return this.getMedhistoryList().initParm(parm);
	}

	/**
	 * ��ʼ�����
	 * @param parm TParm
	 * @return boolean �棺�ɹ����٣�ʧ��
	 */
	public boolean initDiagRec(TParm parm) {
		if (parm == null)
			return false;
		return this.getDiagRecList().initParm(parm);
	}

	/**
	 * ��ʼ��ҽ����ʷ
	 * @param parm TParm
	 * @return boolean �棺�ɹ����٣�ʧ��
	 */
	public boolean initOrderHistoryList(TParm parm) {
		if (parm == null)
			return false;
		return getOrderhistoryList().initParm(parm);
	}

	/**
	 * �������
	 * @return TParm
	 */
	public TParm checkData() {
		TParm result = new TParm();
		return result;
	}

	/**
	 * �õ�ȫ������
	 * @return TParm
	 */
	public TParm getParm() {
		TParm result = new TParm();
		//�������߿�������
		result.setData("SUBJREC", getSubjrecParm().getData());
		//����ҽ������
		result.setData("ORDER", this.getPrescriptionList().getParm().getData());
		//���ع���ʷ����
		result.setData("DRUGALLERGY", this.getDrugAllergyList().getParm().getData());
		//���ؼ���ʷ����
		result.setData("MEDHISTORY", this.getMedhistoryList().getParm()
				.getData());
		//�����������
		result.setData("DIAGREC", this.getDiagRecList().getParm().getData());
		//����ҽ����ʷ����
		result.setData("ORDERHISTORY", this.getOrderhistoryList().getParm()
				.getData());
		//���عҺ�����
		result.setData("REG",this.getReg().getParm().getData());
		//���ز�������
		result.setData("PAT",this.getPat().getParm().getData());
		
		return result;
	}

	/**
	 * �ж����߿����Ƿ��޸�
	 * @return boolean true �޸� false û���޸�
	 */
	public boolean isModifiedSubjrec() {
		if (subjText.isModified())
			return true;
		if (objText.isModified())
			return true;
		if (physexamRec.isModified())
			return true;
		return false;
	}

	/**
	 * �õ����߿���Parm
	 * @return TParm
	 */
	public TParm getSubjrecParm() {
		TParm parm = new TParm();
		if (!isNewFlg() && !isModifiedSubjrec()) {
			parm.setData("SAVE_TYPE", "NO");
			return parm;
		}
		//���ñ������� ��һ�α���ʹ��INSERT �ٴα���UPDATE
		parm.setData("SAVE_TYPE", isNewFlg() ? "INSERT" : "UPDATE");
		//�����
		parm.setData("CASE_NO", getCaseNo());
		if (isNewFlg()) {
			//�ż�ס��
			parm.setData("ADM_TYPE", getAdmType());
			//ҽʦ
			parm.setData("DR_CODE", getDrCode());
		}
		//�õ�����
		parm.setData("SUBJ_TEXT", getSubjText());
		//�ֲ�ʷ
		parm.setData("OBJ_TEXT", getObjText());
		//����
		parm.setData("PHYSEXAM_REC", getPhysexamRec());
		//������Ա���
		parm.setData("OPT_USER", Operator.getID());
		//������ԱIP
		parm.setData("OPT_TERM", Operator.getIP());
		return parm;
	}

	/**
	 * ����
	 * @return TParm
	 */
	public TParm onSave() {
		TParm parm = getParm();
		// �������
		TParm diagrecparm = parm.getParm("DIAGREC");
		//System.out.println("odo in odoAction==="+diagrecparm);
		TParm result = TIOM_AppServer.executeAction("action.opd.ODOAction",
				"onSave", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		reset();
		return result;
	}
	public void reset()
	{
		prescriptionList.reset();
	}
	/**
	 * ��ѯ
	 * @param caseNo String
	 */
	public boolean onQuery(String caseNo,String admType) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO",pat.getMrNo());
		parm.setData("ADM_TYPE",admType);
		TParm result = TIOM_AppServer.executeAction("action.opd.ODOAction",
				"onQuery", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		if (!initParm(result)) {
			err("��������ʧ��");
			return false;
		}
		setCaseNo(caseNo);
		this.setNewFlg(false);
		return true;

	}



	public static void main(String args[]) {
		JavaHisDebug.initClient();
		StringTool f;
		int days=StringTool.getDateDiffer(StringTool.getTimestamp("20090916","yyyyMMdd"), StringTool.getTimestamp("20090918","yyyyMMdd"));
		//System.out.println("days="+days);
				
	}
}
