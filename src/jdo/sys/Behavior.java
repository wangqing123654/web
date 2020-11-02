package jdo.sys;

public class Behavior {
	private String conductId;		   //行为id
	private String conductName;	   //行为名称
	private String source;			   //来源
	private String departmentId;	   //科室Id
	private String department;	   //科室
	private String doctorId;		   //医生Id
	private String doctor;		      //医生姓名
	private String otherStaff;      //其他工作人员（操作人员名称）
	private String event;		      //事件名称
	private String content;		   //事件摘要
	private String behaviorDate;      //行为发生日期
	private String createUser;	   //创建人
	
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
