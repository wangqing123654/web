package jdo.sys;

public class CRMReg {
	
    public CRMReg(){
		
	}
	private String departmenttId; //���ұ���
	private String deptartmentName;//��������
	private String doctorId;//ҽ������
	private String doctorName;//ҽ������
	private String reserveDate;//ԤԼ���ڣ�ԤԼ���ﵱ���ʱ�䣬��ʽҪ��yyyy-MM-dd��
	private String reserveTimeSection;//ԤԼʱ���(���������ʱ��Σ���ʼʱ��-����ʱ�䣬��ʽҪ��HH:mm-HH:mm)
	private String status;//ԤԼ״̬:δԤԼ 1����ԤԼ 2������ԤԼ 3��ԤԼͣ�� 4��ԤԼȷ�� 
	private String sessionId; //ʱ�α���
	private String sessionName; //ʱ������
	private String action; //����״̬
	private String queNo; //��ǰԤԼ��
	private String maxQue; //���ԤԼ��
	private String regFee; //���� 
	private String clinicroom; //����
	private String clinicarea; //����
	private String interveenTime; //ʱ������
	private String quegroupId; //����������
	private String quegroupName; //�����������
	private String clinictypeId; //�ű����
	private String clinictypeName; //�ű�����
	
	
	
	

	public String getClinictypeId() {
		return clinictypeId;
	}
	public void setClinictypeId(String clinictypeId) {
		this.clinictypeId = clinictypeId;
	}
	public String getClinictypeName() {
		return clinictypeName;
	}
	public void setClinictypeName(String clinictypeName) {
		this.clinictypeName = clinictypeName;
	}
	public String getQuegroupId() {
		return quegroupId;
	}
	public void setQuegroupId(String quegroupId) {
		this.quegroupId = quegroupId;
	}
	public String getQuegroupName() {
		return quegroupName;
	}
	public void setQuegroupName(String quegroupName) {
		this.quegroupName = quegroupName;
	}
	public String getInterveenTime() {
		return interveenTime;
	}
	public void setInterveenTime(String interveenTime) {
		this.interveenTime = interveenTime;
	}
	public String getClinicroom() {
		return clinicroom;
	}
	public void setClinicroom(String clinicroom) {
		this.clinicroom = clinicroom;
	}
	public String getClinicarea() {
		return clinicarea;
	}
	public void setClinicarea(String clinicarea) {
		this.clinicarea = clinicarea;
	}
	public String getDeptartmentName() {
		return deptartmentName;
	}
	public void setDeptartmentName(String deptartmentName) {
		this.deptartmentName = deptartmentName;
	}
	public String getRegFee() {
		return regFee;
	}
	public void setRegFee(String regFee) {
		this.regFee = regFee;
	}
	public void setQueNo(String queNo){
		this.queNo=queNo;
	}
	public String getQueNo(){
		return queNo;
	}
	
	public void setMaxQue(String maxQue){
		this.maxQue=maxQue;
	}
	public String getMaxQue(){
		return maxQue;
	}
	
	public void setAction(String action){
		this.action=action;
	}
	public String getAction(){
		return action;
	}
	
	public void setSessionId(String sessionId){
		this.sessionId=sessionId;
	}
	public String getSessionId(){
		return sessionId;
	}
	public void setSessionName(String sessionName){
		this.sessionName=sessionName;
	}
	
	public String getSessionName(){
		return sessionName;
	}
	
	public void setDepartmenttId(String departmenttId) {
		this.departmenttId = departmenttId;
	}
	public String getDepartmenttId() {
		return departmenttId;
	}
	
	public void setDepartmenttName(String deptartmentName) {
		this.deptartmentName = deptartmentName;
	}
	public String getDepartmenttName() {
		return deptartmentName;
	}
	
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	public String getDoctorId() {
		return doctorId;
	}
	
	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	public String getDoctorName() {
		return doctorName;
	}
	
	public void setReserveDate(String reserveDate) {
		this.reserveDate = reserveDate;
	}
	public String getReserveDate() {
		return reserveDate;
	}
	
	public void setReserveTimeSection(String reserveTimeSection) {
		this.reserveTimeSection = reserveTimeSection;
	}
	public String getReserveTimeSection() {
		return reserveTimeSection;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatus() {
		return status;
	}
	

}
