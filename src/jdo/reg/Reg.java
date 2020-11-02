package jdo.reg;

import com.dongyang.data.TModifiedData;
import com.dongyang.data.StringValue;
import jdo.sys.Pat;
import java.sql.Timestamp;
import com.dongyang.data.BooleanValue;
import com.dongyang.data.TimestampValue;
import com.dongyang.data.TParm;
import com.javahis.ui.opb.Objects;
import com.javahis.util.JavaHisDebug;
import jdo.sys.Operator;

/**
 *
 * <p>Title: �ҺŶ���</p>
 *
 * <p>Description: �ҺŶ���</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2008.09.19
 * @version 1.0
 */
public class Reg extends TModifiedData{
    /**
     * ѹ������ʹ�ÿ���
     */
    private boolean tload;
    /**
     * ���߶���
     */
    private Pat pat;
    /**
     * �����վ�
     */
    private RegReceipt regReceipt;
    /**
     * ����� CASE_NO
     */
    private String caseNo;
    /**
     * ԭ����� OLD_CASE_NO
     */
    private String oldCaseNo;
    /**
     * Ԥ������� preFlg
     * yanjing20131217
     */
    private String preFlg;
    
    /**
     * �ż��� ADM_TYPE
     */
    private String admType;
    /**
     * ����Ժ�� REGION
     */
    private String region;
    /**
     * �Һ����� ADM_DATE
     */
    private Timestamp admDate;
    /**
     * �ҺŲ������� REG_DATE
     */
    private Timestamp regDate;
    /**
     * ʱ�δ��� SESSION_CODE
     */
    private String sessionCode;
    /**
     * ���� CLINICAREA_CODE
     */
    private String clinicareaCode;
    /**
     * ���� CLINICROOM_NO
     */
    private String clinicroomNo;
    /**
     * �ű� CLINICTYPE_CODE
     */
    private String clinictypeCode;
    /**
     * ����� QUE_NO
     */
    private int queNo;
    /**
     * ԤԼʱ�� (0800) ԤԼ8�� REG_ADM_TIME
     */
    private String regAdmTime;
    /**
     * �Ʊ���� DEPT_CODE
     */
    private String deptCode;
    /**
     * ����ҽʦ DR_CODE
     */
    private String drCode;
    /**
     * ʵ�ʿ���Ʊ� REALDEPT_CODE
     */
    private StringValue realdeptCode = new StringValue(this);
    /**
     * ʵ�ʿ���ҽʦ REALDR_CODE
     */
    private StringValue realdrCode = new StringValue(this);
    /**
     * ����ԤԼ(0: ԤԼ��1������) APPT_CODE
     */
    private String apptCode;
    /**
     * ������( 0:���1������) VISIT_CODE
     */
    private String visitCode;
    /**
     * �Һŷ�ʽ REGMETHOD_CODE
     */
    private String regmethodCode;
    /**
     * ��ֱ� CTZ1_CODE
     */
    private String ctz1Code;
    /**
     * �ۿ�2 CTZ2_CODE
     */
    private String ctz2Code;
    /**
     * �ۿ�3 CTZ3_CODE
     */
    private String ctz3Code;
    /**
     * ת��Ժ������ TRANHOSP_CODE
     */
    private String tranhospCode;
    /**
     * ���˺� TRIAGE_NO
     */
    private String triageNo;
    /**
     * ��Լ��Ŀ(���˵�λ) CONTRACT_CODE
     */
    private String contractCode;
    /**
     * ����ע�� ARRIVE_FLG
     */
    private BooleanValue arriveFlg = new BooleanValue(this);
    /**
     * �˹���Ա REGCAN_USER
     */
    private StringValue regcanUser = new StringValue(this);
    /**
     * �˹����� REGCAN_DATE
     */
    private TimestampValue regcanDate = new TimestampValue(this);
    /**
     * �Һ�Ժ�� ADM_REGION
     */
    private String admRegion;
    /**
     * Ԥ������ʱ��(�ƻ�����) PREVENT_SCH_CODE
     */
    private String preventSchCode;
    /**
     * DRG�� DRG_CODE
     */
    private String drgCode;
    /**
     * ����ע�� HEAT_FLG
     */
    private boolean heatFlg;
    /**
     * ������� ADM_STATUS
     * 1���ѹҺ�
     * 2���ѿ���
     * 3���ѽɷ�
     * 4����ȡҩ
     * 5�������
     * 6: ����Ժ <�����ṩ>
     */
    private StringValue admStatus = new StringValue(this);
    /**
     * ����״̬ REPORT_STATUS
     * 1 ȫ��δ���
     * 2 �������
     * 3 ȫ�����
     */
    private StringValue reportStatus = new StringValue(this);
    /**
     * ���� kg 100.50 kg WEIGHT
     */
    private double weight;
    /**
     * ��� 170.30cm HEIGHT
     */
    private double height;
    /**
     * ���˵ȼ�
     */
    private String erdLevel;//ERD_LEVEL
    /**
     * vipע��
     */
    private BooleanValue vipFlg = new BooleanValue(this);
    /**
     * ����ע��
     */
    private StringValue seeDrFlg = new StringValue(this);
    /**
     * ����ȼ�
     */
    private StringValue serviceLevel = new StringValue(this);// SERVICE_LEVEL
    /**
     * ҽ�ƿ����׺�
     */
    private String tredeNo = new  String();
    /**
     * ҽ����====pangb 2011-12-5
     */
    private String nhiNo = new String();
    
    /**
     * ��ע==huangtt 20131106 REQUIREMENT
     */
    private StringValue requirement = new StringValue(this);
    /**
     * ����״̬==huangtt 20140307 VISIT_STATE
     */
    private StringValue visitState = new StringValue(this);
    
    /**
     * ҽ�������� ��1.��ͨ 2 ���� 
     */
    private String insPatType = new String();
    /**
     * ҽ�������
     */
    private String confirmNo =new String();
    
    public String getConfirmNo() {
		return confirmNo;
	}
	public void setConfirmNo(String confirmNo) {
		this.confirmNo = confirmNo;
	}
	public String getInsPatType() {
		return insPatType;
	}
	public void setInsPatType(String insPatType) {
		this.insPatType = insPatType;
	}
	public String getNhiNo() {
		return nhiNo;
	}
	public void setNhiNo(String nhiNo) {
		this.nhiNo = nhiNo;
	}
	/**
     * ������
     */
    public Reg()
    {
        StringBuffer sb = new StringBuffer();
        //�����
        sb.append("caseNo:CASE_NO;");
      //Ԥ�������  yanjing 20131217
        sb.append("preFlg:IS_PRE_ORDER;");
      //ԭ�����
        sb.append("oldCaseNo:OLD_CASE_NO;");
        //�ż���
        sb.append("admType:ADM_TYPE;");
        //����Ժ��
        sb.append("region:REGION_CODE;");
        //�Һ�����
        sb.append("admDate:ADM_DATE;");
        //�ҺŲ�������
        sb.append("regDate:REG_DATE;");
        //ʱ�δ���
        sb.append("sessionCode:SESSION_CODE;");
        //����
        sb.append("clinicareaCode:CLINICAREA_CODE;");
        //����
        sb.append("clinicroomNo:CLINICROOM_NO;");
        //�ű�
        sb.append("clinictypeCode:CLINICTYPE_CODE;");
        //�����
        sb.append("queNo:QUE_NO;");
        //ԤԼʱ��
        sb.append("regAdmTime:REG_ADM_TIME;");
        //�Ʊ����
        sb.append("deptCode:DEPT_CODE;");
        //����ҽʦ
        sb.append("drCode:DR_CODE;");
        //ʵ�ʿ���Ʊ�
        sb.append("realdeptCode:REALDEPT_CODE;");
        //ʵ�ʿ���ҽʦ
        sb.append("realdrCode:REALDR_CODE;");
        //����ԤԼ
        sb.append("apptCode:APPT_CODE;");
        //������
        sb.append("visitCode:VISIT_CODE;");
        //�Һŷ�ʽ
        sb.append("regmethodCode:REGMETHOD_CODE;");
        //��ֱ�
        sb.append("ctz1Code:CTZ1_CODE;");
        //�ۿ�2
        sb.append("ctz2Code:CTZ2_CODE;");
        //�ۿ�3
        sb.append("ctz3Code:CTZ3_CODE;");
        //ת��Ժ������
        sb.append("tranhospCode:TRANHOSP_CODE;");
        //���˺�
        sb.append("triageNo:TRIAGE_NO;");
        //��Լ��Ŀ
        sb.append("contractCode:CONTRACT_CODE;");
        //����ע��
        sb.append("arriveFlg:ARRIVE_FLG;");
        //�˹���Ա
        sb.append("regcanUser:REGCAN_USER;");
        //�˹�����
        sb.append("regcanDate:REGCAN_DATE;");
        //�Һ�Ժ��
        sb.append("admRegion:ADM_REGION;");
        //Ԥ������ʱ��
        sb.append("preventSchCode:PREVENT_SCH_CODE;");
        //DRG��
        sb.append("drgCode:DRG_CODE;");
        //DRG��
        sb.append("heatFlg:HEAT_FLG;");
        //�������
        sb.append("admStatus:ADM_STATUS;");
        //����״̬
        sb.append("reportStatus:REPORT_STATUS;");
        //����
        sb.append("weight:WEIGHT;");
        //����
        sb.append("height:HEIGHT;");
        //���˵ȼ�
        sb.append("erdLevel:ERD_LEVEL;");
//        //VIPע��
        sb.append("vipFlg:VIP_FLG;");
        //����ע��
        sb.append("seeDrFlg:SEE_DR_FLG;");
        //����ȼ�
        sb.append("serviceLevel:SERVICE_LEVEL;");
        //ҽ����====pangb 2011-12-05
        sb.append("nhiNo:NHI_NO;");
        sb.append("insPatType:INS_PAT_TYPE;");
      //��ע===huangtt 20131106
        sb.append("requirement:REQUIREMENT;");
        sb.append("visitState:VISIT_STATE;");
        sb.append("confirmNo:CONFIRM_NO");
        
        setMapString(sb.toString());

    }
    /**
     * ���û��߶���
     * @param pat Pat
     */
    public void setPat(Pat pat)
    {
        this.pat = pat;
    }
    /**
     * �õ����߶���
     * @return Pat
     */
    public Pat getPat()
    {
        return pat;
    }
    /**
     * ���������վ�
     */
    public void createReceipt()
    {
        regReceipt = new RegReceipt();
        //�ż����վݶ���
        regReceipt.setAdmType(getAdmType());//3�ż���(REG_RECEIPT)
        regReceipt.setRegion(getRegion());//4����(REG_RECEIPT)

    }
    /**
     * ���������վ�
     * @param regReceipt RegReceipt
     */
    public void setRegReceipt(RegReceipt regReceipt)
    {
        this.regReceipt = regReceipt;
    }
    /**
     * �õ������վ�
     * @return RegReceipt
     */
    public RegReceipt getRegReceipt()
    {
        return regReceipt;
    }
    /**
     * ���ý��׺�
     * @param tredeNo String
     */
    public void setTredeNo(String tredeNo)
    {
        this.tredeNo = tredeNo;
    }
    /**
     * �õ����׺�
     * @return String
     */
    public String getTredeNo()
    {
        return tredeNo;
    }
    /**
     * ���þ����
     * @param caseNo String
     */
    public void setCaseNo(String caseNo)
    {
        this.caseNo = caseNo;
    }
    /**
     * �õ������
     * @return String
     */
    public String caseNo()
    {
        return caseNo;
    }
    /**
     * ���þɾ����
     * @param caseNo String
     * yanjing 20131226
     */
    public void setOldCaseNo(String oldCaseNo)
    {
        this.oldCaseNo = oldCaseNo;
    }
    /**
     * �õ��ɾ����
     * @return String
     * yanjing 20131226
     */
    public String getOldCaseNo()
    {
        return oldCaseNo;
    }
    /**
     * ����Ԥ�������
     * @param preFlg String
     * yanjing 20131217
     */
    public void setPreFlg(String preFlg)
    {
        this.preFlg = preFlg;
    }
    /**
     * �õ�Ԥ�������
     * @return String
     * yanjing
     * 20131217
     */
    public String preFlg()
    {
        return preFlg;
    }
    /**
     * �����ż���
     * @param admType String
     */
    public void setAdmType(String admType)
    {
        this.admType = admType;
    }
    /**
     * �õ��ż���
     * @return String
     */
    public String getAdmType()
    {
        return admType;
    }
    /**
     * ���ò���Ժ��
     * @param region String
     */
    public void setRegion(String region)
    {
        this.region = region;
    }
    /**
     * �õ�����Ժ��
     * @return String
     */
    public String getRegion()
    {
        return region;
    }
    /**
     * ���ùҺ�����
     * @param admDate Timestamp
     */
    public void setAdmDate(Timestamp admDate)
    {
        this.admDate = admDate;
    }
    /**
     * �õ��Һ�����
     * @return Timestamp
     */
    public Timestamp getAdmDate()
    {
        return admDate;
    }
    /**
     * ���ùҺŲ�������
     * @param regDate Timestamp
     */
    public void setRegDate(Timestamp regDate)
    {
        this.regDate = regDate;
    }
    /**
     * �õ��ҺŲ�������
     * @return Timestamp
     */
    public Timestamp getRegDate()
    {
        return regDate;
    }
    /**
     * ����ʱ�δ���
     * @param sessionCode String
     */
    public void setSessionCode(String sessionCode)
    {
        this.sessionCode = sessionCode;
    }
    /**
     * �õ�ʱ�δ���
     * @return String
     */
    public String getSessionCode()
    {
        return sessionCode;
    }
    /**
     * ��������
     * @param clinicareaCode String
     */
    public void setClinicareaCode(String clinicareaCode)
    {
        this.clinicareaCode = clinicareaCode;
    }
    /**
     * �õ�����
     * @return String
     */
    public String getClinicareaCode()
    {
        return clinicareaCode;
    }
    /**
     * ��������
     * @param clinicroomNo String
     */
    public void setClinicroomNo(String clinicroomNo)
    {
        this.clinicroomNo = clinicroomNo;
    }
    /**
     * �õ�����
     * @return String
     */
    public String getClinicroomNo()
    {
        return clinicroomNo;
    }
    /**
     * ���úű�
     * @param clinictypeCode String
     */
    public void setClinictypeCode(String clinictypeCode)
    {
        this.clinictypeCode = clinictypeCode;
    }
    /**
     * �õ��ű�
     * @return String
     */
    public String getClinictypeCode()
    {
        return clinictypeCode;
    }
    /**
     * ���þ����
     * @param queNo int
     */
    public void setQueNo(int queNo)
    {
        this.queNo = queNo;
    }
    /**
     * �õ������
     * @return int
     */
    public int getQueNo()
    {
        return queNo;
    }
    /**
     * ����ԤԼʱ�� (0800) ԤԼ8��
     * @param regAdmTime String
     */
    public void setRegAdmTime(String regAdmTime)
    {
        this.regAdmTime = regAdmTime;
    }
    /**
     * �õ�ԤԼʱ�� (0800) ԤԼ8��
     * @return String
     */
    public String getRegAdmTime()
    {
        return regAdmTime;
    }
    /**
     * ���ÿƱ����
     * @param deptCode String
     */
    public void setDeptCode(String deptCode)
    {
        this.deptCode = deptCode;
    }
    /**
     * �õ��Ʊ����
     * @return String
     */
    public String getDeptCode()
    {
        return deptCode;
    }
    /**
     * ���ÿ���ҽʦ
     * @param drCode String
     */
    public void setDrCode(String drCode)
    {
        this.drCode = drCode;
    }
    /**
     * �õ�����ҽʦ
     * @return String
     */
    public String getDrCode()
    {
        return drCode;
    }
    /**
     * ����ʵ�ʿ���Ʊ�
     * @param realdeptCode String
     */
    public void setRealdeptCode(String realdeptCode)
    {
        this.realdeptCode.setValue(realdeptCode);
    }
    /**
     * �õ�ʵ�ʿ���Ʊ�
     * @return String
     */
    public String getRealdeptCode()
    {
        return this.realdeptCode.getValue();
    }
    /**
     * �޸�ʵ�ʿ���Ʊ�
     * @param realdeptCode String
     */
    public void modifyRealdeptCode(String realdeptCode)
    {
        this.realdeptCode.modifyValue(realdeptCode);
    }
    /**
     * ����ʵ�ʿ���ҽʦ
     * @param realdrCode String
     */
    public void setRealdrCode(String realdrCode)
    {
        this.realdrCode.setValue(realdrCode);
    }
    /**
     * �õ�ʵ�ʿ���ҽʦ
     * @return String
     */
    public String getRealdrCode()
    {
        return this.realdrCode.getValue();
    }
    /**
     * �޸�ʵ�ʿ���ҽʦ
     * @param realdrCode String
     */
    public void modifyRealdrCode(String realdrCode)
    {
        this.realdrCode.modifyValue(realdrCode);
    }
    /**
     * ���õ���ԤԼ(0: ԤԼ��1������)
     * @param apptCode String
     */
    public void setApptCode(String apptCode)
    {
        this.apptCode = apptCode;
    }
    /**
     * �õ�����ԤԼ(0: ԤԼ��1������)
     * @return String
     */
    public String getApptCode()
    {
        return apptCode;
    }
    /**
     * ���ó�����( 0:���1������)
     * @param visitCode String
     */
    public void setVisitCode(String visitCode)
    {
        this.visitCode = visitCode;
    }
    /**
     * �õ�������( 0:���1������)
     * @return String
     */
    public String getVisitCode()
    {
        return visitCode;
    }
    /**
     * ���ùҺŷ�ʽ
     * @param regmethodCode String
     */
    public void setRegmethodCode(String regmethodCode)
    {
        this.regmethodCode = regmethodCode;
    }
    /**
     * �õ��Һŷ�ʽ
     * @return String
     */
    public String getRegmethodCode()
    {
        return regmethodCode;
    }
    /**
     * ������ֱ�
     * @param ctz1Code String
     */
    public void setCtz1Code(String ctz1Code)
    {
        this.ctz1Code = ctz1Code;
    }
    /**
     * �õ���ֱ�
     * @return String
     */
    public String getCtz1Code()
    {
        return ctz1Code;
    }
    /**
     * �����ۿ�2
     * @param ctz2Code String
     */
    public void setCtz2Code(String ctz2Code)
    {
        this.ctz2Code = ctz2Code;
    }
    /**
     * �õ��ۿ�2
     * @return String
     */
    public String getCtz2Code()
    {
        return ctz2Code;
    }
    /**
     * �����ۿ�3
     * @param ctz3Code String
     */
    public void setCtz3Code(String ctz3Code)
    {
        this.ctz3Code = ctz3Code;
    }
    /**
     * �õ��ۿ�3
     * @return String
     */
    public String getCtz3Code()
    {
        return ctz3Code;
    }
    /**
     * ����ת��Ժ������
     * @param tranhospCode String
     */
    public void setTranhospCode(String tranhospCode)
    {
        this.tranhospCode = tranhospCode;
    }
    /**
     * �õ�ת��Ժ������
     * @return String
     */
    public String getTranhospCode()
    {
        return tranhospCode;
    }
    /**
     * ���ü��˺�
     * @param triageNo String
     */
    public void setTriageNo(String triageNo)
    {
        this.triageNo = triageNo;
    }
    /**
     * �õ����˺�
     * @return String
     */
    public String getTriageNo()
    {
        return triageNo;
    }
    /**
     * ���ú�Լ��Ŀ(���˵�λ)
     * @param contractCode String
     */
    public void setContractCode(String contractCode)
    {
        this.contractCode = contractCode;
    }
    /**
     * �õ���Լ��Ŀ(���˵�λ)
     * @return String
     */
    public String getContractCode()
    {
        return contractCode;
    }
    /**
     * ���ñ���ע��
     * @param arriveFlg boolean
     */
    public void setArriveFlg(boolean arriveFlg)
    {
        this.arriveFlg.setValue(arriveFlg);
    }
    /**
     * �õ�����ע��
     * @return boolean
     */
    public boolean isArriveFlg()
    {
        return this.arriveFlg.getValue();
    }
    /**
     * �޸ı���ע��
     * @param arriveFlg boolean
     */
    public void modifyArriveFlg(boolean arriveFlg)
    {
        this.arriveFlg.modifyValue(arriveFlg);
    }
    /**
     * �����˹���Ա
     * @param regcanUser String
     */
    public void setRegcanUser(String regcanUser)
    {
        this.regcanUser.setValue(regcanUser);
    }
    /**
     * �õ��˹���Ա
     * @return String
     */
    public String getRegcanUser()
    {
        return regcanUser.getValue();
    }
    /**
     * �޸��˹���Ա
     * @param regcanUser String
     */
    public void modifyRegcanUser(String regcanUser)
    {
        this.regcanUser.modifyValue(regcanUser);
    }
    /**
     * �����˹�����
     * @param regcanDate Timestamp
     */
    public void setRegcanDate(Timestamp regcanDate)
    {
        this.regcanDate.setValue(regcanDate);
    }
    /**
     * �õ��˹�����
     * @return Timestamp
     */
    public Timestamp getRegcanDate()
    {
        return regcanDate.getValue();
    }
    /**
     * �޸��˹�����
     * @param regcanDate Timestamp
     */
    public void modifyRegcanDate(Timestamp regcanDate)
    {
        this.regcanDate.modifyValue(regcanDate);
    }
    /**
     * ���ùҺ�Ժ��
     * @param admRegion String
     */
    public void setAdmRegion(String admRegion)
    {
        this.admRegion = admRegion;
    }
    /**
     * �õ��Һ�Ժ��
     * @return String
     */
    public String getAdmRegion()
    {
        return admRegion;
    }
    /**
     * ����Ԥ������ʱ��(�ƻ�����)
     * @param preventSchCode String
     */
    public void setPreventSchCode(String preventSchCode)
    {
        this.preventSchCode = preventSchCode;
    }
    /**
     * �õ�Ԥ������ʱ��(�ƻ�����)
     * @return String
     */
    public String getPreventSchCode()
    {
        return preventSchCode;
    }
    /**
     * ����DRG��
     * @param drgCode String
     */
    public void setDrgCode(String drgCode)
    {
        this.drgCode = drgCode;
    }
    /**
     * �õ�DRG��
     * @return String
     */
    public String getDrgCode()
    {
        return drgCode;
    }
    /**
     * ���÷���ע��
     * @param heatFlg boolean
     */
    public void setHeatFlg(boolean heatFlg)
    {
        this.heatFlg = heatFlg;
    }
    /**
     * �õ�����ע��
     * @return boolean
     */
    public boolean isHeatFlg()
    {
        return heatFlg;
    }
    /**
     * ���þ������
     * 1���ѹҺ�
     * 2���ѿ���
     * 3���ѽɷ�
     * 4����ȡҩ
     * 5�������
     * 6: ����Ժ <�����ṩ>
     * @param admStatus String
     */
    public void setAdmStatus(String admStatus)
    {
        this.admStatus.setValue(admStatus);
    }
    /**
     * �õ��������
     * 1���ѹҺ�
     * 2���ѿ���
     * 3���ѽɷ�
     * 4����ȡҩ
     * 5�������
     * 6: ����Ժ <�����ṩ>
     * @return String
     */
    public String getAdmStatus()
    {
        return admStatus.getValue();
    }
    /**
     * �޸ľ������
     * 1���ѹҺ�
     * 2���ѿ���
     * 3���ѽɷ�
     * 4����ȡҩ
     * 5�������
     * 6: ����Ժ <�����ṩ>
     * @param admStatus String
     */
    public void modifyAdmStatus(String admStatus)
    {
        this.admStatus.modifyValue(admStatus);
    }
    /**
     * ���ñ���״̬
     * 1 ȫ��δ���
     * 2 �������
     * 3 ȫ�����
     * @param reportStatus String
     */
    public void setReportStatus(String reportStatus)
    {
        this.reportStatus.setValue(reportStatus);
    }
    /**
     * �õ�����״̬
     * 1 ȫ��δ���
     * 2 �������
     * 3 ȫ�����
     * @return String
     */
    public String getReportStatus()
    {
        return reportStatus.getValue();
    }
    /**
     * �޸ı���״̬
     * 1 ȫ��δ���
     * 2 �������
     * 3 ȫ�����
     * @param reportStatus String
     */
    public void modifyreportStatus(String reportStatus)
    {
        this.reportStatus.modifyValue(reportStatus);
    }
    /**
     * �������� kg 100.50 kg
     * @param weight double
     */
    public void setWeight(double weight)
    {
        this.weight = weight;
    }
    /**
     * �õ����� kg 100.50 kg
     * @return double
     */
    public double getWeight()
    {
        return weight;
    }
    /**
     * ������� 170.30cm
     * @param height double
     */
    public void setHeight(double height)
    {
        this.height = height;
    }
    /**
     * �õ���� 170.30cm
     * @return double
     */
    public double getHeight()
    {
        return height;
    }
    /**
     * ���ü��˵ȼ�
     * @param erdLevel String
     */
    public void setErdLevel(String erdLevel){
    this.erdLevel = erdLevel;
    }
    /**
     * �õ����˵ȼ�
     * @return String
     */
    public String getErdLevel(){
    return erdLevel;
    }
    /**
     * ����VIPע��
     * @param vipFlg boolean
     */
    public void setVipFlg(boolean vipFlg)
    {
        this.vipFlg.setValue(vipFlg);
    }
    /**
     * �õ�VIPע��
     * @return boolean
     */
    public boolean getVipFlg()
    {
        return this.arriveFlg.getValue();
    }
    /**
     * ���ÿ���ע��
     * @param seeDrFlg String
     */
    public void setSeeDrFlg(String seeDrFlg){
    	this.seeDrFlg.setValue(seeDrFlg);
    }
    /**
     * ���÷���ȼ�
     * @param serviceLevel String
     */
    public void setServiceLevel(String serviceLevel){
        this.serviceLevel.setValue(serviceLevel);
    }
    /**
     * �õ�����ȼ�
     * @return String
     */
    public String getServiceLevel(){
        return this.serviceLevel.getValue();
    }
    /**
     * �õ�����ע��
     * @return seeDrFlg String
     */
    public String getSeeDrFlg(){
    	return this.seeDrFlg.getValue();
    }
    /**
     * �޸Ŀ���ע��
     * @param seeDrFlg String
     */
    public void modifySeeDrFlg(String seeDrFlg){
    	this.seeDrFlg.modifyValue(seeDrFlg);
    }
    /**
     * �޸ķ���ȼ�
     * @param serviceLevel String
     */
    public void modifyServiceLevel(String serviceLevel){
        this.serviceLevel.modifyValue(serviceLevel);
    }
    
    //===add by huangtt 20131106 start
    /**
     * ���ñ�ע REQUIREMENT
     * @param requirement String
     */
    public void setRequirement(String requirement)
    {
        this.requirement.setValue(requirement);
    }
    /**
     * �õ���ע
     * @return String
     */
    public String getRequirement()
    {
        return requirement.getValue();
    }
    /**
     * ���þ���״̬ visitState
     * @param requirement String
     */
    public void setVisitState(String visitState)
    {
        this.visitState.setValue(visitState);
    }
    /**
     * �õ�����״̬
     * @return String
     */
    public String getVisitState()
    {
        return visitState.getValue();
    }
    //===add by huangtt 20131106 end
    /**
     * �õ����ز���
     * @return TParm
     */
    public TParm getParm()
    {
        TParm parm = super.getParm();
        if(getPat() != null){
            parm.setData("MR_NO", pat.getMrNo());
            parm.setData("NHI_NO", null==pat.getNhiNo()?"":pat.getNhiNo());//============pangben modify 20110809 ҽ������
        }
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        return parm;
    }
    /**
     * ���ݾ���Ų�ѯ�ҺŶ���
     * @param pat Pat
     * @param caseNo String
     * @return Reg
     */
    public static Reg onQueryByCaseNo(Pat pat,String caseNo)
    {
        if(caseNo == null || caseNo.length() == 0)
            return null;
        TParm parm = PatAdmTool.getInstance().getInfoForCaseNo(caseNo);
        if(parm.getErrCode() < 0)
            return null;
        if(parm.getCount() <= 0)
            return null;
        Reg reg = new Reg();
        reg.setPat(pat);
       if(! reg.initParm(parm,0)){
           return null;
       }
       reg.setReassureFlg(Objects.toString(parm.getData("REASSURE_FLG", 0),null));
        return reg;
    }

    /**
     * ��������
     * @return boolean true �ɹ� false ʧ��
     */
    public boolean onNew()
    {
        if(getPat() == null)
            return false;
        return PatAdmTool.getInstance().newReg(this);
    }
    /**
     * ���涯��
     * @return boolean
     */
    public boolean onSave()
    {
        if(!isModified())
            return true;
        if(!PatAdmTool.getInstance().onSave(this))
            return false;
        reset();
        return true;
    }
    /**
     * ��������(ҽ�ƿ�����ʹ��)
     * @return boolean true �ɹ� false ʧ��
     */
    public boolean onNewForEKT()
    {
        if(getPat() == null)
            return false;
        return PatAdmTool.getInstance().newRegForEKT(this);
    }
    /**
     * ����ѹ�����Կ���
     * @param tload boolean
     */
    public void setTLoad(boolean tload)
    {
        this.tload = tload;
    }
    /**
     * �õ�ѹ�����Կ���
     * @return boolean
     */
    public boolean isTLoad()
    {
        return tload;
    }

    public static void main(String args[])
    {
        JavaHisDebug.initClient();
        Pat pat = Pat.onQueryByMrNo("000000000041");
//        Reg reg = Reg.onQueryByCaseNo(pat,"080922000001");
        Reg reg = Reg.onQueryByCaseNo(pat, "090101000005");

        reg.setPat(pat);
        reg.modifyRealdeptCode("ddddd");
    }
    
    private String reassureFlg;

	public String getReassureFlg() {
		return reassureFlg;
	}
	public void setReassureFlg(String reassureFlg) {
		this.reassureFlg = reassureFlg;
	}
}
