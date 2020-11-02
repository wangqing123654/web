package jdo.sys;



public class CRMInfo {
	public CRMInfo(){
		
	}
	
	
	
	/**
	 * 病案号 MR_NO
	 */
	private String mrNo = "";

	
	/**
	 * 姓名 PAT_NAME
	 */
	private String name = "";
	
	
	public String getMrNo() {
		return mrNo;
	}



	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getValidType() {
		return validType;
	}



	public void setValidType(String validType) {
		this.validType = validType;
	}



	public String getValidNumber() {
		return validNumber;
	}



	public void setValidNumber(String validNumber) {
		this.validNumber = validNumber;
	}



	public String getSex() {
		return sex;
	}



	public void setSex(String sex) {
		this.sex = sex;
	}



	public String getBirthday() {
		return birthday;
	}



	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}



	public String getNationality() {
		return nationality;
	}



	public void setNationality(String nationality) {
		this.nationality = nationality;
	}



	public String getNation() {
		return nation;
	}



	public void setNation(String nation) {
		this.nation = nation;
	}



	public String getMarital() {
		return marital;
	}



	public void setMarital(String marital) {
		this.marital = marital;
	}



	public String getResidAddress() {
		return residAddress;
	}



	public void setResidAddress(String residAddress) {
		this.residAddress = residAddress;
	}



	public String getResidAddressEx() {
		return residAddressEx;
	}



	public void setResidAddressEx(String residAddressEx) {
		this.residAddressEx = residAddressEx;
	}



	public String getPresentAddress() {
		return presentAddress;
	}



	public void setPresentAddress(String presentAddress) {
		this.presentAddress = presentAddress;
	}



	public String getPresentAddressEx() {
		return presentAddressEx;
	}



	public void setPresentAddressEx(String presentAddressEx) {
		this.presentAddressEx = presentAddressEx;
	}



	public String getRemarks() {
		return remarks;
	}



	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}



	public String getPhone() {
		return phone;
	}



	public void setPhone(String phone) {
		this.phone = phone;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	/**
	 * 证件类型ID_TYPE
	 */
	private String validType = "";
	
	
	
	


	/**
	 * 身份证号 IDNO
	 */
	private String validNumber = "";
	
	
	/**
	 * 性别 SEX_CODE
	 */
	private String sex = "";
	
	
	/**
	 * 出生日期 BIRTH_DATE
	 */
	private String birthday = "";
	
	
	/**
	 * 国籍 NATION_CODE
	 */
	private String nationality = "";
	
	
	/**
	 * 民族SPECIES_CODE
	 */
	private String nation = "";
	
	
	/**
	 *婚姻MARRIAGE_CODE
	 */
	private String marital = "";
	
	
	/**
	 * 户籍地址RESID_POST_CODE
	 */
	private String residAddress = "";
	
	
	/**
	 * 详细地址RESID_ADDRESS
	 */
	private String residAddressEx = "";
	
	
	/**
	 * 现住址POST_CODE
	 */
	private String presentAddress = "";
	
	
	/**
	 * 现详细地址CURRENT_ADDRESS
	 */
	private String presentAddressEx = "";
	
	

	/**
	 * 备注REMARKS
	 */
	private String remarks = "";
	
	
	/**
	 * 联系方式TEL_HOME
	 */
	private String phone = "";
	
	
	
	private String id="";
	
	
	
}
