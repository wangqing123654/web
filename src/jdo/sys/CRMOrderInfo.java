package jdo.sys;


public class CRMOrderInfo {
	public CRMOrderInfo(){
		
	}
	private String date;
	private String departId;
	private String departName;
	private String doctorId;
	private String doctorName;
	private int  state;//预约状态     0:未预约       1：已预约          2：不可预约        3：预约停诊        4：预约确认        5: 预约报到          6：不出诊
	private String timePeriod;
	private Integer source;  //来源类型：0： CRM   1：HIS
	private String orderMrNo;  //预约人病案号
	private String orderName;  //预约人姓名
	private String orderSex;  //预约人性别
	private String orderTelephone;  //预约人电话
	private String validNumber;  //预约人证件号
	private String orderBirthday;  //预约人 生日
	private String id; //主键 
	private String clinictypeId; //号别编码
	private String clinictypeName; //号别名称
	private String quegroupId; //给号组别编码
	private String quegroupName; //给号组别名称
	
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getSource() {
		return source;
	}
	public void setSource(Integer source) {
		this.source = source;
	}
	
	public String getOrderMrNo() {
		return orderMrNo;
	}
	public void setOrderMrNo(String orderMrNo) {
		this.orderMrNo = orderMrNo;
	}
	public String getOrderName() {
		return orderName;
	}
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}
	public String getOrderSex() {
		return orderSex;
	}
	public void setOrderSex(String orderSex) {
		this.orderSex = orderSex;
	}
	public String getOrderTelephone() {
		return orderTelephone;
	}
	public void setOrderTelephone(String orderTelephone) {
		this.orderTelephone = orderTelephone;
	}
	public String getValidNumber() {
		return validNumber;
	}
	public void setValidNumber(String validNumber) {
		this.validNumber = validNumber;
	}
	public String getOrderBirthday() {
		return orderBirthday;
	}
	public void setOrderBirthday(String orderBirthday) {
		this.orderBirthday = orderBirthday;
	}
	public void setTimePeriod(String timePeriod){
		this.timePeriod=timePeriod;
	}
	public String getTimePeriod(){
		return timePeriod;
	}
	
	public void setDate(String date){
		this.date=date;
	}
	public String getDate(){
		return date;
	}
	
	public void setDepartId(String departId){
		this.departId=departId;
	}
	public String getDepartId(){
		return departId;
	}
	
	public void setDepartName(String departName){
		this.departName=departName;
	}
	public String getDepartName(){
		return departName;
	}
	
	public void setDoctorId(String doctorId){
		this.doctorId=doctorId;
	}
	public String getDoctorId(){
		return doctorId;
	}
	
	public void setDoctorName(String doctorName){
		this.doctorName=doctorName;
	}
	public String getDoctorName(){
		return doctorName;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	

}
