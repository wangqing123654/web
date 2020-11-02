package jdo.sys;

public class CRMReg {
	
    public CRMReg(){
		
	}
	private String departmenttId; //科室编码
	private String deptartmentName;//科室名称
	private String doctorId;//医生编码
	private String doctorName;//医生名称
	private String reserveDate;//预约日期（预约就诊当天的时间，格式要求：yyyy-MM-dd）
	private String reserveTimeSection;//预约时间段(看病具体的时间段：开始时间-结束时间，格式要求：HH:mm-HH:mm)
	private String status;//预约状态:未预约 1：已预约 2：不可预约 3：预约停诊 4：预约确认 
	private String sessionId; //时段编码
	private String sessionName; //时段名称
	private String action; //更新状态
	private String queNo; //当前预约数
	private String maxQue; //最大预约数
	private String regFee; //费用 
	private String clinicroom; //诊室
	private String clinicarea; //诊区
	private String interveenTime; //时间粒度
	private String quegroupId; //给号组别编码
	private String quegroupName; //给号组别名称
	private String clinictypeId; //号别编码
	private String clinictypeName; //号别名称
	
	
	
	

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
