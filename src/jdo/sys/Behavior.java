package jdo.sys;

public class Behavior {
	private String conductId;		   //��Ϊid
	private String conductName;	   //��Ϊ����
	private String source;			   //��Դ
	private String departmentId;	   //����Id
	private String department;	   //����
	private String doctorId;		   //ҽ��Id
	private String doctor;		      //ҽ������
	private String otherStaff;      //����������Ա��������Ա���ƣ�
	private String event;		      //�¼�����
	private String content;		   //�¼�ժҪ
	private String behaviorDate;      //��Ϊ��������
	private String createUser;	   //������
	
	public void setCreateUser(String createUser){
		this.createUser=createUser;
	}
	public String getCreateUser(){
		return createUser;
	}
	
	public void setBehaviorDate(String behaviorDate){
		this.behaviorDate=behaviorDate;
	}
	public String getBehaviorDate(){
		return behaviorDate;
	}
	
	public void setContent(String content){
		this.content=content;
	}
	public String getContent(){
		return content;
	}
	public void setEvent(String event){
		this.event=event;
	}
	public String getEvent(){
		return event;
	}
	
	public void setOtherStaff(String otherStaff){
		this.otherStaff=otherStaff;
	}
	public String getOtherStaff(){
		return otherStaff;
	}
	
	public void setConductId(String conductId){
		this.conductId=conductId;
	}
	public String getConductId(){
		return conductId;
	}
	public void setConductName(String conductName){
		this.conductName=conductName;
	}
	public String getConductName(){
		return conductName;
	}
	
	public void setSource(String source){
		this.source=source;
	}
	public String getSource(){
		return source;
	}
	
	public void setDoctorId(String doctorId){
		this.doctorId=doctorId;
	}
	public String getDoctorId(){
		return doctorId;
	}
	
	public void setDoctorName(String doctor){
		this.doctor=doctor;
	}
	public String getDoctorName(){
		return doctor;
	}
	
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getDepartmenttId() {
		return departmentId;
	}
	
	public void setDepartmentName(String department) {
		this.department = department;
	}
	public String getDepartmenttName() {
		return department;
	}

}
