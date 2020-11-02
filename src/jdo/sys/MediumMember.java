package jdo.sys;

public class MediumMember {
	private String id;
	private String mrNo;				        //������
	private String name;			 	        //����
	private String jianpin;				        //��ƴ	
	private String enName;				        //Ӣ����
	private String formerName;			        //������
	private String validType;			        //��Ч֤������ --�ֵ�
	private String validNumber;		            //��Ч֤����
	private String sex;			 	            //�Ա�--�ֵ�
	private String birthday;			 	        //��������
	private String nationality;			        //����--�ֵ�
	private String nation;			 	        //����--�ֵ�
	private String religionCode; 		        //�ڽ�--�ֵ�
	private String marital;			  	        //����--�ֵ�
	private String residAddress; 		        // ������ַ--�ֵ�
	private String residAddressEx; 		        // ��ϸ��ַ
	private String birthAddress;                //������--�ֵ�*
	private String presentAddress; 		        // ��סַ--�ֵ�
	private String presentAddressEx; 	        // ����ϸ��ַ
	private String email;			 	        //��������
	private String wechat; 				        // ΢�źţ�
	private String birthHospital; 		        // ����ҽԺ
	private String telephone;                   // �ֻ��绰*
	private String phone;			  	        // �����绰*
	private String school;				        //��ҵԺУ
	private String schoolPhone; 		        // У���绰
	private String learnMode; 			        // ��֪��ʽ--�ֵ�
	private String contract1Unit; 		        // ��ͬ��λ1--�ֵ�
	private String insurance1Card; 		        // ���տ���1
	private String contract2Unit; 		        // ��ͬ��λ2--�ֵ�
	private String insurance2Card; 		        // ���տ���2
	
	//-------------------------�໤��/��ϵ��1
	private String firstFamilyName;	            //����
	private String firstFamilyRelationship;	    //��ϵ--�ֵ�
	private String firstFamilyContactWay;		//��ϵ��ʽ
	private String firstFamilyTelephone;		//�ֻ���
	private String firstFamilyOrganization;	    //������λ
	private String firstFamilyValidType;		//֤������--�ֵ�
	private String firstFamilyValidNumber;	    //֤������
	private String firstFamilyEmail;		    //�ʼ�
	
	//-------------------------�໤��/��ϵ��2
	private String secondFamilyName;	        //����
	private String secondFamilyRelationship;	//��ϵ--�ֵ�
	private String secondFamilyContactWay;		//��ϵ��ʽ
	private String secondFamilyTelephone;		//�ֻ���
	private String secondFamilyOrganization;	//������λ
	private String secondFamilyValidType;		//֤������--�ֵ�
	private String secondFamilyValidNumber;	    //֤������
	private String secondFamilyEmail;		    //�ʼ�
	
	private String ctz1Code; 			        // ���һ--�ֵ�
	private String ctz2Code; 			        // ��ݶ�--�ֵ�
	private String ctz3Code; 			        // �����--�ֵ�
	private String specialDiet;                 //������ʳ--�ֵ�*
	private String register1Code;               //�Һ����һ--�ֵ�*
	private String register2Code;               //�Һ���ݶ�--�ֵ�*
	 						  
	private String cardId;				        //��Ա��id��
	
	//----------------------------VIP��Ϣ
	private String memCode;	                    // ��Ա���--�ֵ�
	private String familyDoctor;	            // ��ͥҽ��--�ֵ�
	private String accountManagerName;	        // �ͻ�����
	private String accountManagerCode;	        // �ͻ�������--�ֵ�
	private String startDate;	                    // ��Ч����
	private String endDate;	                    // ʧЧ����
	private String buyMoon;	                    // ��������
	private String happenMoon;	                // ��������
	
	private String hairpinType;	                // ��������--�ֵ�
	private String hairpinCause;	            // ����ԭ��--�ֵ�
	private String predictStartDate;	            // Ԥ��Ч��
	private String predictEndDate;	            // ԤʧЧ��
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public String getJianpin() {
		return jianpin;
	}
	public void setJianpin(String jianpin) {
		this.jianpin = jianpin;
	}
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}
	public String getFormerName() {
		return formerName;
	}
	public void setFormerName(String formerName) {
		this.formerName = formerName;
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
	public String getReligionCode() {
		return religionCode;
	}
	public void setReligionCode(String religionCode) {
		this.religionCode = religionCode;
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
	public String getBirthAddress() {
		return birthAddress;
	}
	public void setBirthAddress(String birthAddress) {
		this.birthAddress = birthAddress;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getWechat() {
		return wechat;
	}
	public void setWechat(String wechat) {
		this.wechat = wechat;
	}
	public String getBirthHospital() {
		return birthHospital;
	}
	public void setBirthHospital(String birthHospital) {
		this.birthHospital = birthHospital;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public String getSchoolPhone() {
		return schoolPhone;
	}
	public void setSchoolPhone(String schoolPhone) {
		this.schoolPhone = schoolPhone;
	}
	public String getLearnMode() {
		return learnMode;
	}
	public void setLearnMode(String learnMode) {
		this.learnMode = learnMode;
	}
	public String getContract1Unit() {
		return contract1Unit;
	}
	public void setContract1Unit(String contract1Unit) {
		this.contract1Unit = contract1Unit;
	}
	public String getInsurance1Card() {
		return insurance1Card;
	}
	public void setInsurance1Card(String insurance1Card) {
		this.insurance1Card = insurance1Card;
	}
	public String getContract2Unit() {
		return contract2Unit;
	}
	public void setContract2Unit(String contract2Unit) {
		this.contract2Unit = contract2Unit;
	}
	public String getInsurance2Card() {
		return insurance2Card;
	}
	public void setInsurance2Card(String insurance2Card) {
		this.insurance2Card = insurance2Card;
	}
	public String getFirstFamilyName() {
		return firstFamilyName;
	}
	public void setFirstFamilyName(String firstFamilyName) {
		this.firstFamilyName = firstFamilyName;
	}
	public String getFirstFamilyRelationship() {
		return firstFamilyRelationship;
	}
	public void setFirstFamilyRelationship(String firstFamilyRelationship) {
		this.firstFamilyRelationship = firstFamilyRelationship;
	}
	public String getFirstFamilyContactWay() {
		return firstFamilyContactWay;
	}
	public void setFirstFamilyContactWay(String firstFamilyContactWay) {
		this.firstFamilyContactWay = firstFamilyContactWay;
	}
	public String getFirstFamilyTelephone() {
		return firstFamilyTelephone;
	}
	public void setFirstFamilyTelephone(String firstFamilyTelephone) {
		this.firstFamilyTelephone = firstFamilyTelephone;
	}
	public String getFirstFamilyOrganization() {
		return firstFamilyOrganization;
	}
	public void setFirstFamilyOrganization(String firstFamilyOrganization) {
		this.firstFamilyOrganization = firstFamilyOrganization;
	}
	public String getFirstFamilyValidType() {
		return firstFamilyValidType;
	}
	public void setFirstFamilyValidType(String firstFamilyValidType) {
		this.firstFamilyValidType = firstFamilyValidType;
	}
	public String getFirstFamilyValidNumber() {
		return firstFamilyValidNumber;
	}
	public void setFirstFamilyValidNumber(String firstFamilyValidNumber) {
		this.firstFamilyValidNumber = firstFamilyValidNumber;
	}
	public String getFirstFamilyEmail() {
		return firstFamilyEmail;
	}
	public void setFirstFamilyEmail(String firstFamilyEmail) {
		this.firstFamilyEmail = firstFamilyEmail;
	}
	public String getSecondFamilyName() {
		return secondFamilyName;
	}
	public void setSecondFamilyName(String secondFamilyName) {
		this.secondFamilyName = secondFamilyName;
	}
	public String getSecondFamilyRelationship() {
		return secondFamilyRelationship;
	}
	public void setSecondFamilyRelationship(String secondFamilyRelationship) {
		this.secondFamilyRelationship = secondFamilyRelationship;
	}
	public String getSecondFamilyContactWay() {
		return secondFamilyContactWay;
	}
	public void setSecondFamilyContactWay(String secondFamilyContactWay) {
		this.secondFamilyContactWay = secondFamilyContactWay;
	}
	public String getSecondFamilyTelephone() {
		return secondFamilyTelephone;
	}
	public void setSecondFamilyTelephone(String secondFamilyTelephone) {
		this.secondFamilyTelephone = secondFamilyTelephone;
	}
	public String getSecondFamilyOrganization() {
		return secondFamilyOrganization;
	}
	public void setSecondFamilyOrganization(String secondFamilyOrganization) {
		this.secondFamilyOrganization = secondFamilyOrganization;
	}
	public String getSecondFamilyValidType() {
		return secondFamilyValidType;
	}
	public void setSecondFamilyValidType(String secondFamilyValidType) {
		this.secondFamilyValidType = secondFamilyValidType;
	}
	public String getSecondFamilyValidNumber() {
		return secondFamilyValidNumber;
	}
	public void setSecondFamilyValidNumber(String secondFamilyValidNumber) {
		this.secondFamilyValidNumber = secondFamilyValidNumber;
	}
	public String getSecondFamilyEmail() {
		return secondFamilyEmail;
	}
	public void setSecondFamilyEmail(String secondFamilyEmail) {
		this.secondFamilyEmail = secondFamilyEmail;
	}
	public String getCtz1Code() {
		return ctz1Code;
	}
	public void setCtz1Code(String ctz1Code) {
		this.ctz1Code = ctz1Code;
	}
	public String getCtz2Code() {
		return ctz2Code;
	}
	public void setCtz2Code(String ctz2Code) {
		this.ctz2Code = ctz2Code;
	}
	public String getCtz3Code() {
		return ctz3Code;
	}
	public void setCtz3Code(String ctz3Code) {
		this.ctz3Code = ctz3Code;
	}
	public String getSpecialDiet() {
		return specialDiet;
	}
	public void setSpecialDiet(String specialDiet) {
		this.specialDiet = specialDiet;
	}
	public String getRegister1Code() {
		return register1Code;
	}
	public void setRegister1Code(String register1Code) {
		this.register1Code = register1Code;
	}
	public String getRegister2Code() {
		return register2Code;
	}
	public void setRegister2Code(String register2Code) {
		this.register2Code = register2Code;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getMemCode() {
		return memCode;
	}
	public void setMemCode(String memCode) {
		this.memCode = memCode;
	}
	public String getFamilyDoctor() {
		return familyDoctor;
	}
	public void setFamilyDoctor(String familyDoctor) {
		this.familyDoctor = familyDoctor;
	}
	public String getAccountManagerName() {
		return accountManagerName;
	}
	public void setAccountManagerName(String accountManagerName) {
		this.accountManagerName = accountManagerName;
	}
	public String getAccountManagerCode() {
		return accountManagerCode;
	}
	public void setAccountManagerCode(String accountManagerCode) {
		this.accountManagerCode = accountManagerCode;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getBuyMoon() {
		return buyMoon;
	}
	public void setBuyMoon(String buyMoon) {
		this.buyMoon = buyMoon;
	}
	public String getHappenMoon() {
		return happenMoon;
	}
	public void setHappenMoon(String happenMoon) {
		this.happenMoon = happenMoon;
	}
	public String getHairpinType() {
		return hairpinType;
	}
	public void setHairpinType(String hairpinType) {
		this.hairpinType = hairpinType;
	}
	public String getHairpinCause() {
		return hairpinCause;
	}
	public void setHairpinCause(String hairpinCause) {
		this.hairpinCause = hairpinCause;
	}
	public String getPredictStartDate() {
		return predictStartDate;
	}
	public void setPredictStartDate(String predictStartDate) {
		this.predictStartDate = predictStartDate;
	}
	public String getPredictEndDate() {
		return predictEndDate;
	}
	public void setPredictEndDate(String predictEndDate) {
		this.predictEndDate = predictEndDate;
	}
	public String getCost() {
		return cost;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}
	public String getSponsor1() {
		return sponsor1;
	}
	public void setSponsor1(String sponsor1) {
		this.sponsor1 = sponsor1;
	}
	public String getSponsor2() {
		return sponsor2;
	}
	public void setSponsor2(String sponsor2) {
		this.sponsor2 = sponsor2;
	}
	public String getSponsor3() {
		return sponsor3;
	}
	public void setSponsor3(String sponsor3) {
		this.sponsor3 = sponsor3;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	private String cost;	                    // ��Ա����
	private String sponsor1;	                // ������1--�ֵ�
	private String sponsor2;	                // ������2--�ֵ�	
	private String sponsor3;	                // ������3--�ֵ�	
	private String remark;	                    // ��ע


}
