FileServer.Main.IP=127.0.0.1
FileServer.Main.Root=C:\JavaHisFile

//         PIC Config
PatInfPIC.LocalPath=C:\JavaHis\Temp
PatInfPIC.ServerPath=PatPic

//         DrugPic Config
PHAInfoPic.LocalPath=C:\JavaHis\Temp
PHAInfoPic.ServerPath=DrugPic

DBTableModifyLog.Root=DBTableModifyLog
DBTableLocalLog.Root=C:\JavaHis\DBTableLocalLogs

RootServer.IP=127.0.0.1

EmrTemplet=EmrFileData\EmrTemplet
EmrData=EmrFileData\EmrData
EmrLocalTempFileName=ģ���ļ�
emr.name = 'EMR02000104'

UDD_DISBATCH_LocalPath=C:\JavaHis\logs

//         JavaCTool Config
JavaCTool.JavaHome=C:\Program Files (x86)\Java\jdk1.6.0_45
JavaCTool.SourcePath=E:\apache-tomcat-8.5.8-ayh\webapps\web\common\src
JavaCTool.OutPath=E:\apache-tomcat-8.5.8-ayh\webapps\web\common\classes
JavaCTool.ClassPath=E:\apache-tomcat-8.5.8-ayh\webapps\web\common\lib\T40-api.jar;E:\apache-tomcat-8.5.8-ayh\webapps\web\common\classes

//         JavaHis Word
JavaHisWord.localTempPath=C:\JavaHis\JavaHisWord

//         Locale Table Columns
Locale.Version=20150325
Locale.SYS_FEE.Columns=ORDER_CODE,ORDER_DESC,PY1,PY2,TRADE_ENG_DESC,GOODS_DESC,GOODS_PYCODE,ALIAS_DESC,ALIAS_PYCODE,NHI_FEE_DESC,ORDER_CAT1_CODE,CAT1_TYPE,SPECIFICATION,ORDERSET_FLG,OWN_PRICE,CHARGE_HOSP_CODE,UNIT_CODE,CTRL_FLG,OWN_PRICE2,OWN_PRICE3,ACTIVE_FLG,MAN_CODE,REGION_CODE,SEQ,USE_CAT,ORDER_DEPT_CODE, IPD_FIT_FLG
Locale.SYS_DIAGNOSIS.Columns=ICD_CODE,ICD_CHN_DESC,ICD_ENG_DESC,PY1,PY2,DESCRIPTION,ICD_TYPE,MIC_FLG,DIAG_DEPT_CODE

//ҩ���ۿ�Ʒѵ�
//DOSAGE:��ҩ��DISPENSE:��ҩ
CHARGE_POINT=DOSAGE
//סԺҩ�������Ƿ����������
IS_CHECK=Y
//סԺҩ�������Ƿ�����ҩ����
IS_DOSAGE=Y
//������ҩ�����ѭ������
CYCLE_TIMES_FOR_ERR=3

//         ��������
//         �����ֲ�ʷ
ODOEmrTempletZSCLASSCODE=EMR020001
ODOEmrTempletZSSUBCLASSCODE=EMR02000103
ODOEmrTempletZSPath=JHW\�ţ������ﲡ��\��(��)�ﲡ��
ODOEmrTempletZSFileName=�����ֲ�ʷ
//ODOEmrTempletZSCLASSCODE=02
//ODOEmrTempletZSSUBCLASSCODE=0205
//ODOEmrTempletZSPath=�����ֲ�ʷ
//ODOEmrTempletZSFileName=�����ֲ�ʷ

//ODOEmrTempletZSFileName=Sub��Obj��Phy Template

//         ����ʷ
ODOEmrTempletFAMILYHISTORY_CLASSCODE=18
ODOEmrTempletFAMILYHISTORY_SUBCLASSCODE=1801
ODOEmrTempletFAMILYHISTORY_Path=����ʷ
ODOEmrTempletFAMILYHISTORY_FileName=�������ʷ


//סԺ֤
ADMEmrINHOSPPATH=JHW\�ţ������ﲡ��\��(��)�ﲡ��
ADMEmrINHOSPFILENAME=סԺ֤
ADMEmrINHOSPSUBCLASSCODE=EMR02000109
ADMEmrINHOSPCLASSCODE=EMR020001

//�ż��ﻤʿվ���ýṹ������
ONWEmrMRCODE=EMR02000104
//������¼���ýṹ������
OPEEmrMRCODE=EMR05010214
//���鱨��
LABIP=192.168.8.231

//��鱨��
RISIP=192.168.8.231

//        JavaHis Language
Language=zh,����;en,English


//        EKT Config
ekt.switch=true
ekt.opd.EKTDialogSwitch=true
ekt.port=COM3
//סԺ��ҩ��XML�ļ����·��
ATCPATH.I=C:\JavaHis\ATC_I\
//�����ҩ��XML�ļ����·��
ATCPATH.O=C:\JavaHis\ATC_O\
//�����ҩ���ļ�������IP
FILE_SERVER_IP=127.0.0.1

//�����ҩ���ļ��������˿ں�
PORT=8103

//�����ҩ�����������ݿ�
ACTDBO.Type=oracle
//ACTDBO.Address=127.0.0.1
ACTDBO.Address=127.0.0.1
ACTDBO.Port=1521
ACTDBO.DBName=ORCL
ACTDBO.UserName=fds_user
ACTDBO.Password=yuyama


//סԺ��ҩ�����������ݿ�
ACTDBI.Type=oracle
ACTDBI.Address=127.0.0.1
//ACTDBI.Address=127.0.0.1
ACTDBI.Port=1521
ACTDBI.DBName=ORCL
ACTDBI.UserName=fds_user
ACTDBI.Password=yuyama


//�кŽӿ�
//CallHost=127.0.0.1
//CallPort=80
//CallPath=/fzservice/service.asmx
//CallNamespace=http://tempuri.org/
//ServerIsReady=Y
IsCallNO=N


//����ӿ�
//����
CallLabHostO=127.0.0.1
//�������
CallLabHostH=127.0.0.1
//����
CallLabHostE=127.0.0.1
//�˿ں�
CallLabPort=9100
//�Ƿ�����
ServerLabIsReady=Y

//��Ϣ����XML���·��
MONITOR.PATH=C:\JavaHis\MONITOR\
//��Ϣ���������IP
MONITOR.SERVER=127.0.0.1
//��Ϣ����������˿ں�
MONITOR.PORT=8103



//�����������ű���
ZhFontSizeProportion=1.0

//Ӣ���������ű���
EnFontSizeProportion=0.8

//������ҩ
PassIsReadyOE=Y
EnforcementFlg=N
WarnFlg=2

//�ʼ�����������
//SMTP��������ַ
mail.smtp.host=smtp.gmail.com
//SMTP�������˿ں�
mail.smtp.port=465
//SMTP��������Ҫ�����֤
mail.smtp.auth=Yes
//�������Ƿ�Ҫ��ȫ����(SSL)
mail.smtp.smtpSSL=Yes
//��������
mail.admin.account=li.xiang790130@gmail.com
//������������
mail.admin.password=eu000319
//�о�������ȥ��ǩ�����б�
EMR.TEST_TAG=HR02.01.002|HR01.01.002.02|HR03.00.004.01|HR03.00.004.02|HR03.00.004.03|HR03.00.004.04|HR03.00.004.05|HR03.00.004.06

//ҽ������
InsInPath=D:\log\INS_IN\
InsOutPath=D:\log\INS_OUT\
InsLogPath=c:/INS/log.txt
InsDebug=1
InsType=0
InsHost=127.0.0.1
InsPort=8002
//���ο���
BatchServer=Y

WEB_SERVICES_43IP=127.0.0.1:8080/web
WEB_SERVICES_IP=127.0.0.1:8080/web
WEB_SERVICES_58IP=127.0.0.1:8080/web

//��װ��ҩ��webservices ·��
bsm.path=http://127.0.0.1:1080/web_consis/ServiceConsis.asmx?WSDL
//��װ��ҩ�������
BOXDEPT_CODE=040110

//Ԥ�������
ekt.ayhSwitch=true

//��ҩ�Ƿ�����  //huangtt20131216
PHA_STOCK_FLG=Y

inp.name=EMR100009
inp.nameseq = 09
inp.nameparent = EMR10


//WEB_SERVICES_IP_CRM=192.168.8.231:8080/crm
//crm.switch=false

//his.ip=192.168.8.231

SystemVersion=V1.071

//�������֤ͼƬ����·��
sid.path=C:\\IdCard

//������������ʱ��
LockPatTime =3

//���ﲡ����¼
OEEmrMRCODE=EMR02000166

// ҩ�·����ҽ������
PHA_SERVICE_FEE=ZAZ01142