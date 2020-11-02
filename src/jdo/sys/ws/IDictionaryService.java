package jdo.sys.ws;

import javax.jws.WebService;



/**
 * <p>Title:ͳһ����ͳ�ýӿ�</p>
 *
 * <p>Description: ͳһ����ͳ�ýӿ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author li.xiang 2013-04-29
 * @version 4.0
 */
@WebService
public interface IDictionaryService {
	
    /**
     * �õ��汾
     * @return String
     */
    public String getVersion();
    
    /**
     * �õ�������Ϣ
     * @param code String
     * @param password String
     * @return String[]
     **/
    public String[] getDeptInf(String code,String password);

    /** * �õ�����
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getStation(String code,String password);
    
    
    /**
     * ͬ����ɫ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getReulInf(String code,String password);
    
    /**
     * �õ�������Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getDeptInfCode(String code,String password,String deptcode);
    
    /**
     * �û���Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getOperatorInf(String code,String password);
    
    /**
     * LISҽ����Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getLisOrder(String code,String password);
    
    
    /**
     * ҩƷ�����ѯ
     * @param code String
     * @param password String
     * @param name String
     * @return String[]
     */
    public String[] getPhaClassify(String code,String password,String name);
    
    /**
     * ҩƷ��ѯ
     * @param code String
     * @param password String
     * @param classify String
     * @return String[]
     */
    public String[] getPhaInf(String code,String password,String classify);
    
    /**
     * �õ�ҩƷ��Ϣ
     * @param code String
     * @param password String
     * @param orderCode String
     * @return String[]
     */
    public String[] getPhaOrder(String code,String password,String ordercode);
    
    /**
     * סԺ��ҩ���õ�HISϵͳҩƷ�ֵ�(ͬ����)
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getODIPhaOrderInfo(String code,String password);
    
    /**
     * סԺ��ҩ���õ�HISϵͳҩƷ�ֵ䵥ҽ����ѯ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getODIPhaOrderInfoItem(String code,String password,String ordercode);
    
    /**
     * ҩƷ��λ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getIndMaterialloc(String code,String password);
    
    /**
     * ҩƷ��λ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getIndMateriallocOrder(String code,String password,String orgcode,String ordercode);
    
    /**
     * �õ���Һϵͳҽ����Ϣ
     * @param code String
     * @param password String
     * @param ordercode String
     * @return String
     */
    public String[] getPhaEsyOrder(String code,String password,String ordercode);
    
    /**
     * �Ա��ֵ�ͬ��
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getSexInf(String code,String password);
    
    
    /**
     * Ѫ��
     * @param code String
     * @param password String
     * @return String[]
     *//*
    public String[] getBloodType(String code,String password);
    /**
     * ��ݣ��������
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getCtzInf(String code,String password);
    
    /**
     * �õ�������Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getDeptInfSY(String code,String password);
    
    /**
     * �õ�Ƶ��
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getPhaFreqInfSY(String code, String password);
    
    
    /**
     * �õ���ҩ��ʽ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getRouteInf(String code, String password);
    
    /**
     * ͬ�����
     * @param code String
     * @param password String
     * @param admtype String
     * @param startdate String
     * @param enddate String
     * @return String[]
     */
    public String[] getRegWorkList(String code,String password,String admtype,String startdate,String enddate);
    
    /**
     * �õ�ҩƷ��Ϣ(ƴ����)
     * @param code String
     * @param password String
     * @param py1 String
     * @param startrow int
     * @param endrow int
     * @return String[]
     */
    public String[] getPhaOrderPY1(String code,String password,String py1,int startrow,int endrow);    
    
    /**
     * �õ�Ƶ��
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getPhaFreqInf(String code, String password);
    
    /**
     * �����Ϣ
     * @param code String
     * @param password String
     * @param classify String
     * @param type String
     * @return String[]
     */
    public String[] getDiagnosisInf(String code,String password,String classify,String type);
    
    /**
     * �����ֵ�
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getSysOperationICD(String code,String password);
    
    /**
     * ע����Ϣ
     * @param code String ���
     * @param chnDesc String ��������
     * @param engDesc String Ӣ������
     * @param contactsName String ��ϵ��
     * @param tel String �绰
     * @param email String mail
     * @param password String ����
     * @return String Success
     */
    public String regist(String code,String chnDesc,String engDesc,String contactsName,String tel,String email,String password);
    
    /**
     * ��ȡע��״̬
     * @param code String
     * @return String
     * No Find û���ҵ�
     * NEW ����,��ȷ��
     * ENABLED ��Ч��
     * DISABLED ��Ч��
     */
    public String getRegistStatus(String code);
    
    /**
     * �޸�����
     * @param code String
     * @param oldPassword String
     * @param newPassword String
     * @return String
     */
    public String modifyPassword(String code,String oldPassword,String newPassword);
    
    /**
     * �õ������ֵ���Ϣ
     * @return String[]
     */
    public String[] getShareTable();
    
    /**
     * ע�����Ϣ
     * @param code String ���̱��
     * @param password String ����
     * @param tableName String ����
     * @param action String ����
     * @return String
     */
    public String registTable(String code,String password,String tableName,String action);
    
    /**
     * �õ���ע����Ϣ
     * @param code String
     * @param password String
     * @param tableName String
     * @return String[]
     */
    public String[] getRegistTableInf(String code,String password,String tableName);
    
    /**
     * �õ��б仯�ı�
     * @param code String
     * @param password String
     * @param status String
     * @return String[]
     */
    public String[] getModifyTable(String code,String password,String status);
    
    
    /**
     * �õ��仯��Ϣ
     * @param code String
     * @param password String
     * @param status String
     * @param tableName String
     * @return String[]
     */
    public String[] getModifyInf(String code,String password,String status,String tableName);
    
    
    /**
     * ȡ����Ϣ
     * @param code String
     * @param password String
     * @param tableName String
     * @param index String
     * @return String
     */
    public String fetchInf(String code,String password,String tableName,String index);
    
    /**
     * ȷ��
     * @param code String
     * @param password String
     * @param tableName String
     * @param index String
     * @return String
     */
    public String confirmedInf(String code,String password,String tableName,String index);
    
    
    /**
     * ɾ��ͬ����Ϣ
     * @param code String
     * @param password String
     * @param tableName String
     * @param index String
     * @return String
     */
    public String deleteInf(String code,String password,String tableName,String index);
    
    /**
     * �û���Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getOperatorInfSY(String code,String password);
    
    /**
     * Ƥ�Ի�д�ż�ҽ����ע
     * @param code String
     * @param password String
     * @param caseNo String
     * @param rxNo String
     * @param seqNo String
     * @return String[]
     */
    public String[] readOpdOrderPS(String code,String password,String caseno,String rxno,String seqno,String value);
    
    /**
     * �õ��ż�ס��
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getAdmType(String code,String password);
    
    /**
     * �õ�ҽʦ�Ű�
     * @param code String
     * @param password String
     * @param admDate String ���� 20100921
     * @return String[]
     * ʱ��,�ż�ס��,����,����,ҽʦ
     */
    public String[] getRegSchDay(String code,String password,String admDate);
    
    /**
     * �õ�������
     * @param code String
     * @param password String
     * @return String
     */
    public String[] getRegionJD(String code,String password);
    
    /**
     * �õ��ż�ҽ���Ƿ��˷�
     * @param code String
     * @param password String
     * @param caseno String
     * @param rxno String
     * @param seqno String
     * @return String[]
     */
    public String[] getHisCancelOrder(String code,String password,String caseno,String rxno,String seqno);
    
    /**
     * �õ�����������Ϣ�ż�ҽ����Ϣ
     * @param code String
     * @param password String
     * @param rxno String
     * @return String[]
     */
    public String[] getPatInfAndOrder(String code,String password,String rxno);
    
    /**
     * �õ���Һҽ����Ϣ
     * @param code String
     * @param password String
     * @param rxno String
     * @return String[]
     */
    public String[] getOrderSY(String code,String password,String rxno);

    
    /**
     * �õ��ֵ���Ϣ
     * @param groupId String
     * @param id String
     * @return String
     */
    public String getDictionary(String groupId,String id);
    
    /**
     * ���没����Ϣ
     * @param code String
     * @param password String
     * @param name String ����
     * @param birthday String ��������
     * @param sex String �Ա�
     * @param SID String ���֤��
     * @param tel String �绰
     * @param address String ��ַ
     * @return String
     */
    public String savePat(String code,String password,String name,String birthday,String sex,String SID,String tel,String address);
    
    /**
     * �������֤�ŵõ����߱��
     * @param code String
     * @param password String
     * @param SID String ���֤��
     * @param name String ����
     * @return String
     */
    public String[] getPatForSID(String code,String password,String SID,String name);
    
    
    /**
     * ԤԼ�Һ�
     * @param code String
     * @param password String
     * @param mrNo String ������
     * @param date String �Һ�����
     * @param sessionCode String ʱ��
     * @param admType String �ż���
     * @param deptCode String ����
     * @param clinicRoomNo String ���
     * @param drCode String ҽ������
     * @param regionCode String ����
     * @param ctz1Code String �������
     * @param serviceLevel String ����ȼ�
     * @return String
     */
    public String regAppt(String code,String password,String mrNo,String date,String sessionCode,
                          String admType,String deptCode,String clinicRoomNo,String drCode,String regionCode,
                          String ctz1Code,String serviceLevel);
    
    /**
     * ԤԼ�˺�
     * @param code String
     * @param password String
     * @param caseNo String
     * @return String
     */
    public String regUnAppt(String code,String password,String caseNo);
    
    
    /**
     * ȡ�ò�����ǰ����ԤԼδ������Ϣ
     * @param code String
     * @param password String
     * @param mrNo String
     * @return String[]
     */
    public String[] getPatAppRegInfo(String code,String password,String mrNo);
    
    /**
     * ȡ��ʱ����Ϣ
     * @param code String
     * @param password String
     * @param admType String
     * @return String[]
     */
    public String[] getSessionInf(String code,String password,String admType);
    
    /**
     * ȡ�������Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getClinicRoom(String code,String password);
    
    /**
     * ȡ��������Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getClinicArea(String code,String password);
    
    /**
     * ȡ�ÿ��ҷ�����Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getDeptClassRule(String code,String password);
    
    /**
     * ��ȡsys_fee���ݣ���������
     * @param code
     * @param password
     * @return
     */
    public String[] getSysFee(String code, String password);
    
    /**
     * ��ȡsys_fee_history���ݣ���������
     * @param code
     * @param password
     * @return
     */
    public String[] getSysFeeHistory(String code, String password);
    
    /**
     * ��ȡ�����ֵ�����
     * @param code
     * @param password
     * @param tableName
     * @return
     */
    public String[] getData(String code, String password, String tableName);
    
    
    /**
     * ͨ�������Ż�ȡ������Ϣ����������
     * @param mr_no
     * @return
     */
    public String[] getPatByMrNo(String mr_no);
    
  
    /**
     * ��ð����Ϣ
     */
	public String getSchDay(String beginDate,String endDate,String deptCode,String drCode);
	
	
	/**
	 * ����޸ĺ�İ����Ϣ 
	 */
	public String getUpdatedScyDay(String beginDate,String endDate);
	
	/**
	 * ���VIP�����Ϣ
	 *
	 */
	public String getVipSchDay(String beginDate, String endDate);
	
	/**
	 * ���°����CRMͬ�����
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public boolean updateSchdayCrmSyncFlg(String beginDate, String endDate);
	
	/**
	 * ȡ�úű������Ϣ
	 * @return
	 */
	public String[] getClinictypeFee(String code, String password);
	
	/**
	 * ȡ�úű���Ϣ
	 * @return
	 */
	public String[] getClinictype(String code, String password);
	
	/**
	 * ȡ�ùҺ������Ϣ
	 * @return
	 */
	public String[] getQuegroup(String code, String password);
	
	/**
	 * ȡ�ùҺŷ�ʽ��Ϣ
	 * @return
	 */
	public String[] getRegmethod(String code, String password);
	
	/**
     * �û���Ӧ�Ŀ�����Ϣ
     * @param code String
     * @param password String
     * @return String[]
     */
    public String[] getOperatorDeptCode(String code,String password);
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
