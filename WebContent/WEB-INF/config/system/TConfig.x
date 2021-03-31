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
EmrLocalTempFileName=模版文件
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

//药房扣库计费点
//DOSAGE:配药；DISPENSE:发药
CHARGE_POINT=DOSAGE
//住院药房流程是否有审核流程
IS_CHECK=Y
//住院药房流程是否有配药流程
IS_DOSAGE=Y
//病区配药出错后循环次数
CYCLE_TIMES_FOR_ERR=3

//         调用门诊
//         主诉现病史
ODOEmrTempletZSCLASSCODE=EMR020001
ODOEmrTempletZSSUBCLASSCODE=EMR02000103
ODOEmrTempletZSPath=JHW\门（急）诊病历\门(急)诊病历
ODOEmrTempletZSFileName=主诉现病史
//ODOEmrTempletZSCLASSCODE=02
//ODOEmrTempletZSSUBCLASSCODE=0205
//ODOEmrTempletZSPath=主诉现病史
//ODOEmrTempletZSFileName=主诉现病史

//ODOEmrTempletZSFileName=Sub、Obj、Phy Template

//         家族史
ODOEmrTempletFAMILYHISTORY_CLASSCODE=18
ODOEmrTempletFAMILYHISTORY_SUBCLASSCODE=1801
ODOEmrTempletFAMILYHISTORY_Path=家族史
ODOEmrTempletFAMILYHISTORY_FileName=门诊家族史


//住院证
ADMEmrINHOSPPATH=JHW\门（急）诊病历\门(急)诊病历
ADMEmrINHOSPFILENAME=住院证
ADMEmrINHOSPSUBCLASSCODE=EMR02000109
ADMEmrINHOSPCLASSCODE=EMR020001

//门急诊护士站调用结构化病历
ONWEmrMRCODE=EMR02000104
//手术记录调用结构化病历
OPEEmrMRCODE=EMR05010214
//检验报告
LABIP=192.168.8.231

//检查报告
RISIP=192.168.8.231

//        JavaHis Language
Language=zh,中文;en,English


//        EKT Config
ekt.switch=true
ekt.opd.EKTDialogSwitch=true
ekt.port=COM3
//住院包药机XML文件存放路径
ATCPATH.I=C:\JavaHis\ATC_I\
//门诊包药机XML文件存放路径
ATCPATH.O=C:\JavaHis\ATC_O\
//门诊包药机文件服务器IP
FILE_SERVER_IP=127.0.0.1

//门诊包药机文件服务器端口号
PORT=8103

//门诊包药机服务器数据库
ACTDBO.Type=oracle
//ACTDBO.Address=127.0.0.1
ACTDBO.Address=127.0.0.1
ACTDBO.Port=1521
ACTDBO.DBName=ORCL
ACTDBO.UserName=fds_user
ACTDBO.Password=yuyama


//住院包药机服务器数据库
ACTDBI.Type=oracle
ACTDBI.Address=127.0.0.1
//ACTDBI.Address=127.0.0.1
ACTDBI.Port=1521
ACTDBI.DBName=ORCL
ACTDBI.UserName=fds_user
ACTDBI.Password=yuyama


//叫号接口
//CallHost=127.0.0.1
//CallPort=80
//CallPath=/fzservice/service.asmx
//CallNamespace=http://tempuri.org/
//ServerIsReady=Y
IsCallNO=N


//条码接口
//门诊
CallLabHostO=127.0.0.1
//健康体检
CallLabHostH=127.0.0.1
//急诊
CallLabHostE=127.0.0.1
//端口号
CallLabPort=9100
//是否启用
ServerLabIsReady=Y

//信息看板XML存放路径
MONITOR.PATH=C:\JavaHis\MONITOR\
//信息看板服务器IP
MONITOR.SERVER=127.0.0.1
//信息看板服务器端口号
MONITOR.PORT=8103



//中文字体缩放比例
ZhFontSizeProportion=1.0

//英文字体缩放比例
EnFontSizeProportion=0.8

//合理用药
PassIsReadyOE=Y
EnforcementFlg=N
WarnFlg=2

//邮件服务器配置
//SMTP服和器地址
mail.smtp.host=smtp.gmail.com
//SMTP服务器端口号
mail.smtp.port=465
//SMTP服务器需要身份认证
mail.smtp.auth=Yes
//服务器是否要求安全连接(SSL)
mail.smtp.smtpSSL=Yes
//发送邮箱
mail.admin.account=li.xiang790130@gmail.com
//发送邮箱密码
mail.admin.password=eu000319
//研究病例隐去标签名称列表
EMR.TEST_TAG=HR02.01.002|HR01.01.002.02|HR03.00.004.01|HR03.00.004.02|HR03.00.004.03|HR03.00.004.04|HR03.00.004.05|HR03.00.004.06

//医保设置
InsInPath=D:\log\INS_IN\
InsOutPath=D:\log\INS_OUT\
InsLogPath=c:/INS/log.txt
InsDebug=1
InsType=0
InsHost=127.0.0.1
InsPort=8002
//批次开关
BatchServer=Y

WEB_SERVICES_43IP=127.0.0.1:8080/web
WEB_SERVICES_IP=127.0.0.1:8080/web
WEB_SERVICES_58IP=127.0.0.1:8080/web

//盒装包药机webservices 路径
bsm.path=http://127.0.0.1:1080/web_consis/ServiceConsis.asmx?WSDL
//盒装包药机虚拟库
BOXDEPT_CODE=040110

//预交金参数
ekt.ayhSwitch=true

//中药是否检查库存  //huangtt20131216
PHA_STOCK_FLG=Y

inp.name=EMR100009
inp.nameseq = 09
inp.nameparent = EMR10


//WEB_SERVICES_IP_CRM=192.168.8.231:8080/crm
//crm.switch=false

//his.ip=192.168.8.231

SystemVersion=V1.071

//二代身份证图片保存路径
sid.path=C:\\IdCard

//病患锁定病患时间
LockPatTime =3

//复诊病历记录
OEEmrMRCODE=EMR02000166

// 药事服务费医嘱代码
PHA_SERVICE_FEE=ZAZ01142