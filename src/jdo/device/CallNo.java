package jdo.device;

import com.dongyang.config.TConfig;
import com.javahis.util.JavaHisDebug;

public class CallNo {
    /**
     * ������
     */
    private String host;
    /**
     * �˿�
     */
    private int port;
    /**
     * ·��
     */
    private String path;
    /**
     * ���ƿռ�
     */
    private String namespace;
    /**
     * �û���
     */
    private String userName;
    /**
     * ����
     */
    private String password;
    /**
     * ��ȷ�ķ���
     */
    private String success;
    /**
     * ��ʼ��
     */
    public boolean init()
    {
        if("N".equals(TConfig.getSystemValue("ServerIsReady")))
            return false;
        setHost(TConfig.getSystemValue("CallHost"));
        int port = 80;
        try{
            port = Integer.parseInt(TConfig.getSystemValue("CallPort"));
        }catch(Exception e)
        {
            return false;
        }
        setPort(port);
        setPath(TConfig.getSystemValue("CallPath"));
        setNamespace(TConfig.getSystemValue("CallNamespace"));
        return true;
    }

    /**
     * ����������
     * @param host String
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * �õ�������
     * @return String
     */
    public String getHost() {
        return host;
    }

    /**
     * ���ö˿�
     * @param port String
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * �õ��˿�
     * @return int
     */
    public int getPort() {
        return port;
    }

    /**
     * ����·��
     * @param path String
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * �õ�·��
     * @return String
     */
    public String getPath() {
        return path;
    }

    /**
     * �������ƿռ�
     * @param namespace String
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * �õ����ƿռ�
     * @return String
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * �����û���
     * @param userName String
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * �õ��û���
     * @return String
     */
    public String getUserName() {
        return userName;
    }

    /**
     * ��������
     * @param password String
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * �õ�����
     * @return String
     */
    public String getPassword() {
        return password;
    }

    /**
     * ���óɹ�
     * @param success String
     */
    public void setSuccess(String success) {
        this.success = success;
    }

    /**
     * �õ��ɹ�
     * @return String
     */
    public String getSuccess() {
        return success;
    }
    /**
     * �Һ�ͬ��
     * @param VISIT_DATE String �Һ����� NOT NULL
     * @param VISIT_NO String �Һź� NOT NULL CASENO
     * @param CLINIC_LABEL String ר�� ��ͨ��
     * @param SERIAL_NO String С��
     * @param PATIENT_ID String ���˿���
     * @param PNAME String ��������
     * @param PSEX String �Ա�
     * @param PAGE String ����
     * @param IDENTITY String ���
     * @param DEPTID String ����ID
     * @param REGISTERING_DATE String �Һ�ʱ�䣨2010-2-8 11:11:11��
     * @param DOCTOR String ҽ����� ��ͨ����Ϊ��
     * @param CTYPE String 0 �Һ� 1 �˺�  2�������
     * @param OPTYPE String �������� 0 ��� 1 ɾ��  2 ������߸��� 3 �˺�
     * @param TIME_DESC String ���
     * �Һ� CTYPE 0 OPTYPE 2
     * �˺� CTYPE 1 OPTYPE 3
     * @return String �ɹ����� "true" ʧ�ܷ��� "false"
     */
    public String SyncClinicMaster(String VISIT_DATE,
                                   String VISIT_NO,
                                   String CLINIC_LABEL,
                                   String SERIAL_NO,
                                   String PATIENT_ID,
                                   String PNAME,
                                   String PSEX,
                                   String PAGE,
                                   String IDENTITY,
                                   String DEPTID,
                                   String REGISTERING_DATE,
                                   String DOCTOR,
                                   String CTYPE ,
                                   String OPTYPE,
                                   String TIME_DESC)
    {
        StringBuffer xml = new StringBuffer();
        addXml(xml,"VISIT_DATE",VISIT_DATE);
        addXml(xml,"VISIT_NO",VISIT_NO);
        addXml(xml,"CLINIC_LABEL",CLINIC_LABEL);
        addXml(xml,"SERIAL_NO",SERIAL_NO);
        addXml(xml,"PATIENT_ID",PATIENT_ID);
        addXml(xml,"PNAME",PNAME);
        addXml(xml,"PSEX",PSEX);
        addXml(xml,"PAGE",PAGE);
        addXml(xml,"IDENTITY",IDENTITY);
        addXml(xml,"DEPTID",DEPTID);
        addXml(xml,"REGISTERING_DATE",REGISTERING_DATE);
        addXml(xml,"DOCTOR",DOCTOR);
        addXml(xml,"CTYPE",CTYPE);
        addXml(xml,"OPTYPE",OPTYPE);
        addXml(xml,"TIME_DESC",TIME_DESC);
        String a = TIOM_Service.executeService(getHost(),getPort(),getPath(),getNamespace(),"SyncClinicMaster",xml.toString());
        return a;
    }
    public void addXml(StringBuffer xml,String name,String value)
    {
        xml.append("<");
        xml.append(name);
        xml.append(">");
        xml.append(value);
        xml.append("</");
        xml.append(name);
        xml.append(">");
    }
    /**
     * �к�ͬ��
     * @param VISIT_DATE String �Һ�����
     * @param VISIT_NO String �Һź� CASENO
     * @param CLINIC_LABEL String ר�� ��ͨ��
     * @param SERIAL_NO String С��
     * @param PNAME String ��������
     * @param PSEX String �Ա�
     * @param IDENTITY String ���
     * @param DOCTOR String ҽ������ ��ͨ����Ϊ��
     * @param COMPUTERIP String ��������(����IP)
     * @return String
     */
    public String CallClinicMaster(String VISIT_DATE,
                                   String VISIT_NO,
                                   String CLINIC_LABEL,
                                   String SERIAL_NO,
                                   String PNAME,
                                   String PSEX,
                                   String IDENTITY,
                                   String DOCTOR,
                                   String COMPUTERIP)
    {
        StringBuffer xml = new StringBuffer();
        addXml(xml,"VISIT_DATE",VISIT_DATE);
        addXml(xml,"VISIT_NO",VISIT_NO);
        addXml(xml,"CLINIC_LABEL",CLINIC_LABEL);
        addXml(xml,"SERIAL_NO",SERIAL_NO);
        addXml(xml,"PNAME",PNAME);
        addXml(xml,"PSEX",PSEX);
        addXml(xml,"IDENTITY",IDENTITY);
        addXml(xml,"DOCTOR",DOCTOR);
        addXml(xml,"COMPUTERIP",COMPUTERIP);
        String a = TIOM_Service.executeService(getHost(),getPort(),getPath(),getNamespace(),"CallClinicMaster",xml.toString());
        return a;
    }
    /**
     * ȡҩ�б�
     * @param VISIT_DATE String  ��������           NOT NULL,
     * @param VISIT_NO String �����
     * @param SERIAL_NO String С��(��)
     * @param PATIENT_ID String ���˿��� MRNO
     * @param PNAME String ��������
     * @param PSEX String �Ա�
     * @param IDENTITY String ���(��)
     * @param DEPTID String ҩ������
     * @param REGISTERING_DATE String ��ҩʱ�䣨2010-2-8 11:11:11��
     * @param DOCTOR String ��������
     * @param CTYPE String 0 ��ҩ 1 ��ҩ���
     * @param OPTYPE String �������� 2 ������߸���
     * ҽ����ҩ����� CTYPE 0
     * ����ҩ�ص� CTYPE 1
     * @return String
     */
    public String SyncDrug(String VISIT_DATE,
                           String VISIT_NO,
                           String SERIAL_NO,
                           String PATIENT_ID,
                           String PNAME,
                           String PSEX,
                           String IDENTITY,
                           String DEPTID,
                           String REGISTERING_DATE,
                           String DOCTOR,
                           String CTYPE,
                           String OPTYPE)
    {
        StringBuffer xml = new StringBuffer();
        addXml(xml,"VISIT_DATE",VISIT_DATE);
        addXml(xml,"VISIT_NO",VISIT_NO);
        addXml(xml,"SERIAL_NO",SERIAL_NO);
        addXml(xml,"PATIENT_ID",PATIENT_ID);
        addXml(xml,"PNAME",PNAME);
        addXml(xml,"PSEX",PSEX);
        addXml(xml,"IDENTITY",IDENTITY);
        addXml(xml,"REGISTERING_DATE",REGISTERING_DATE);
        addXml(xml,"DEPTID",DEPTID);
        addXml(xml,"DOCTOR",DOCTOR);
        addXml(xml,"CTYPE",CTYPE);
        addXml(xml,"OPTYPE",OPTYPE);
        return TIOM_Service.executeService(getHost(),getPort(),getPath(),getNamespace(),"SyncDrug",xml.toString());
    }
    /**
     * ȡҩ�к�
     * @param COUNTER_NO String ���ں�
     * @param SERIAL_NO String ��ҩ��
     * @param PSEX String �Ա�, (��)
     * @param DRUGNMAE String ҩƷ�������� �׼Ӻ�~2��~3.2Ԫ&ֹʹƬ~3��~2Ԫ, (��)
     * @param DOCTOR String ҩʦ����,(��)
     * @param COMPUTERIP String ���ؼ����IP
     * @param ALLPAY String �ܼ�Ǯ(��)
     * @return String
     */
    public String CallDrug(String COUNTER_NO,
                           String SERIAL_NO,
                           String PSEX,
                           String DRUGNMAE,
                           String DOCTOR,
                           String COMPUTERIP,
                           String ALLPAY)
    {
        StringBuffer xml = new StringBuffer();
        addXml(xml,"COUNTER_NO",COUNTER_NO);
        addXml(xml,"SERIAL_NO",SERIAL_NO);
        addXml(xml,"PSEX",PSEX);
        addXml(xml,"DRUGNMAE",DRUGNMAE);
        addXml(xml,"DOCTOR",DOCTOR);
        addXml(xml,"COMPUTERIP",COMPUTERIP);
        addXml(xml,"ALLPAY",ALLPAY);
        return TIOM_Service.executeService(getHost(),getPort(),getPath(),getNamespace(),"CallDrug",xml.toString());
    }
    /**
     * Lisͬ��
     * @param EXAM_ITEM String ���� Ψһ
     * @param PATIENT_ID String ���˿���
     * @param LISITEM String �����Ŀ
     * @param PNAME String ��������
     * @param PSEX String �Ա�
     * @param PAGE String ����
     * @param IDENTITY String ��
     * @param REGISTERING_DATE String �Һ�ʱ�䣨2010-2-8 11:11:11��
     * @param DOCTOR String ��
     * @param CTYPE String 0 ˢ��   2������
     * @param OPTYPE String 2 ������߸���
     * @return String
     */
    public String SyncLisMaster(String EXAM_ITEM,
                                String PATIENT_ID,
                                String LISITEM,
                                String PNAME,
                                String PSEX,
                                String PAGE,
                                String IDENTITY,
                                String REGISTERING_DATE,
                                String DOCTOR,
                                String CTYPE,
                                String OPTYPE)
    {
        StringBuffer xml = new StringBuffer();
        addXml(xml,"EXAM_ITEM",EXAM_ITEM);
        addXml(xml,"PATIENT_ID",PATIENT_ID);
        addXml(xml,"LISITEM",LISITEM);
        addXml(xml,"PNAME",PNAME);
        addXml(xml,"PSEX",PSEX);
        addXml(xml,"PAGE",PAGE);
        addXml(xml,"IDENTITY",IDENTITY);
        addXml(xml,"REGISTERING_DATE",REGISTERING_DATE);
        addXml(xml,"DOCTOR",DOCTOR);
        addXml(xml,"CTYPE",CTYPE);
        addXml(xml,"OPTYPE",OPTYPE);
        return TIOM_Service.executeService(getHost(),getPort(),getPath(),getNamespace(),"SyncLisMaster",xml.toString());
    }

    /**
     * ����ͬ��  �ɹ����� "true" ʧ�ܷ��� "false"
     * @param DEPTID String  ���Ҵ���Ψһ  NOT NULL
     * @param DEPTNAME String  ��������
     * @param READNAME String ���Ҷ���
     * @param OPTYPE String   �������� 0 ��� 1 ɾ��  2 ������߸���
     * @return String
     */
    public String SyncDept(String DEPTID,
                           String DEPTNAME,
                           String READNAME,
                           String OPTYPE)
    {
        StringBuffer xml = new StringBuffer();
        addXml(xml,"DEPTID",DEPTID);
        addXml(xml,"DEPTNAME",DEPTNAME);
        addXml(xml,"READNAME",READNAME);
        addXml(xml,"OPTYPE",OPTYPE);
        return TIOM_Service.executeService(getHost(),getPort(),getPath(),getNamespace(),"SyncDept",xml.toString());
    }

    /**
     * ҽ��ͬ��  �ɹ����� "true" ʧ�ܷ��� "false"
     * @param USERID String  ҽ������Ψһ NOT NULL
     * @param USERNAME String ҽ������
     * @param READNAME String ҽ�����ֶ���
     * @param DEPTID String ���Ҵ���
     * @param TITLE String ְ��
     * @param OPTYPE String  �������� 0 ��� 1 ɾ��  2 ������߸���
     * @return String
     */
    public String SyncDoctors(String USERID,
                              String USERNAME,
                              String READNAME,
                              String DEPTID,
                              String TITLE,
                              String OPTYPE)
    {
        StringBuffer xml = new StringBuffer();
        addXml(xml,"USERID",USERID);
        addXml(xml,"USERNAME",USERNAME);
        addXml(xml,"READNAME",READNAME);
        addXml(xml,"DEPTID",DEPTID);
        addXml(xml,"TITLE",TITLE);
        addXml(xml,"OPTYPE",OPTYPE);
        return TIOM_Service.executeService(getHost(),getPort(),getPath(),getNamespace(),"SyncDoctors",xml.toString());
    }

    /**
     * ��½
     * @param COMPUTERIP String ����ip
     * @param DOCTOR String ҽ������
     * @param fDate String ʱ��
     * @param OPTYPE String 1��½,0�˳�
     * @return String
     */
    public String SynLogin(String COMPUTERIP,
                           String DOCTOR,
                           String fDate,
                           String OPTYPE)
    {
        StringBuffer xml = new StringBuffer();
        addXml(xml,"COMPUTERIP",COMPUTERIP);
        addXml(xml,"DOCTOR",DOCTOR);
        addXml(xml,"FDATE",fDate);
        addXml(xml,"OPTYPE",OPTYPE);
        String a = TIOM_Service.executeService(getHost(),getPort(),getPath(),getNamespace(),"SynLogin",xml.toString());
        return a;
    }


    public static void main(String args[])
    {
        JavaHisDebug.initClient();
        CallNo call = new CallNo();
        /*call.setHost("192.168.1.4");
               call.setPort(80);
               call.setPath("/service.asmx");
               call.setNamespace("http://tempuri.org/");
               //call.setUserName("aaa");
               //call.setPassword("bbb");*/
        call.init();
        String s = call.SyncClinicMaster("2010-04-06 14:10:10","20100406001CASE_NO","ר��","10","MR_NO","����","��","30","���","����ID","2010-04-06 14:10:10","ҽ�����","0","2","����");
        System.out.println(s);
    }
}
