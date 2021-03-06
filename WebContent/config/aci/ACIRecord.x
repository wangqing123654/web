#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:wangl 2009.07.01
#
# version 1.0
#

<Type=TFrame>
UI.Title=医疗差错与事故
UI.MenuConfig=%ROOT%\config\aci\ACIRecordMenu.x
UI.Width=1490
UI.Height=1490
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.aci.ACIRecordControl
UI.item=Panel1;Panel3;tPanel_0
UI.layout=null
UI.FocusList=TYPE_CODE;CLASS_CODE;OCCUR_DATE;DEPT_CODE;STATION_CODE;IPD_NO;MR_NO;PAT_NAME;PAT_COMPANY;IN_CHARGE_CODE;IDENTITY_DATE;IDENTITY_COMPANY;PATIENT_CONDITITION;RESULT_CODE;
UI.TopMenu=Y
UI.TopToolBar=Y
UI.X=5
UI.AutoX=Y
UI.Y=5
UI.AutoY=Y
UI.AutoWidth=Y
UI.AutoHeight=Y
tPanel_0.Type=TPanel
tPanel_0.X=33
tPanel_0.Y=6
tPanel_0.Width=1480
tPanel_0.Height=71
tPanel_0.Border=组|查询条件
tPanel_0.AutoWidth=Y
tPanel_0.AutoX=Y
tPanel_0.Item=S_DATE;E_DATE;tLabel_14;tLabel_15;tLabel_16;DEPT_CODE_S
DEPT_CODE_S.Type=科室下拉列表
DEPT_CODE_S.X=460
DEPT_CODE_S.Y=28
DEPT_CODE_S.Width=118
DEPT_CODE_S.Height=23
DEPT_CODE_S.Text=TButton
DEPT_CODE_S.showID=Y
DEPT_CODE_S.showName=Y
DEPT_CODE_S.showText=N
DEPT_CODE_S.showValue=N
DEPT_CODE_S.showPy1=Y
DEPT_CODE_S.showPy2=Y
DEPT_CODE_S.Editable=Y
DEPT_CODE_S.Tip=科室
DEPT_CODE_S.TableShowList=name
tLabel_16.Type=TLabel
tLabel_16.X=423
tLabel_16.Y=32
tLabel_16.Width=38
tLabel_16.Height=15
tLabel_16.Text=科室:
tLabel_15.Type=TLabel
tLabel_15.X=211
tLabel_15.Y=32
tLabel_15.Width=72
tLabel_15.Height=15
tLabel_15.Text=查询迄日:
tLabel_14.Type=TLabel
tLabel_14.X=19
tLabel_14.Y=32
tLabel_14.Width=72
tLabel_14.Height=15
tLabel_14.Text=查询起日:
E_DATE.Type=TTextFormat
E_DATE.X=287
E_DATE.Y=29
E_DATE.Width=110
E_DATE.Height=20
E_DATE.Text=TTextFormat
E_DATE.showDownButton=Y
E_DATE.FormatType=date
E_DATE.Format=yyyy/MM/dd
S_DATE.Type=TTextFormat
S_DATE.X=90
S_DATE.Y=29
S_DATE.Width=110
S_DATE.Height=20
S_DATE.Text=TTextFormat
S_DATE.showDownButton=Y
S_DATE.FormatType=date
S_DATE.Format=yyyy/MM/dd
Panel3.Type=TPanel
Panel3.X=4
Panel3.Y=250
Panel3.Width=1480
Panel3.Height=1235
Panel3.Border=组|差错与事故
Panel3.AutoY=N
Panel3.AutoWidth=Y
Panel3.AutoHeight=Y
Panel3.Item=Table;ADM_TYPE;REGION;ORG_CODE;AREA_CODE;ROOM_LOCATION
Panel3.AutoX=Y
ROOM_LOCATION.Type=位置字典下拉列表
ROOM_LOCATION.X=267
ROOM_LOCATION.Y=145
ROOM_LOCATION.Width=81
ROOM_LOCATION.Height=23
ROOM_LOCATION.Text=TButton
ROOM_LOCATION.showID=Y
ROOM_LOCATION.showName=Y
ROOM_LOCATION.showText=N
ROOM_LOCATION.showValue=N
ROOM_LOCATION.showPy1=N
ROOM_LOCATION.showPy2=N
ROOM_LOCATION.Editable=Y
ROOM_LOCATION.Tip=位置字典
ROOM_LOCATION.TableShowList=name
ROOM_LOCATION.ModuleParmString=GROUP_ID:SYS_LOCATION
ROOM_LOCATION.ModuleParmTag=
ROOM_LOCATION.ExpandWidth=80
AREA_CODE.Type=诊区下拉列表
AREA_CODE.X=108
AREA_CODE.Y=214
AREA_CODE.Width=81
AREA_CODE.Height=23
AREA_CODE.Text=TButton
AREA_CODE.showID=Y
AREA_CODE.showName=Y
AREA_CODE.showText=N
AREA_CODE.showValue=N
AREA_CODE.showPy1=N
AREA_CODE.showPy2=N
AREA_CODE.Editable=Y
AREA_CODE.Tip=诊区
AREA_CODE.TableShowList=name
AREA_CODE.ModuleParmTag=
ORG_CODE.Type=药房下拉列表
ORG_CODE.X=106
ORG_CODE.Y=130
ORG_CODE.Width=81
ORG_CODE.Height=23
ORG_CODE.Text=TButton
ORG_CODE.showID=Y
ORG_CODE.showName=Y
ORG_CODE.showText=N
ORG_CODE.showValue=N
ORG_CODE.showPy1=N
ORG_CODE.showPy2=N
ORG_CODE.Editable=Y
ORG_CODE.Tip=药房
ORG_CODE.TableShowList=name
ORG_CODE.ModuleParmTag=
ORG_CODE.ExpandWidth=80
REGION.Type=区域下拉列表
REGION.X=196
REGION.Y=53
REGION.Width=81
REGION.Height=23
REGION.Text=TButton
REGION.showID=Y
REGION.showName=Y
REGION.showText=N
REGION.showValue=N
REGION.showPy1=N
REGION.showPy2=N
REGION.Editable=Y
REGION.Tip=区域
REGION.TableShowList=name
REGION.ModuleParmString=
REGION.ModuleParmTag=
REGION.ExpandWidth=80
ADM_TYPE.Type=TComboBox
ADM_TYPE.X=67
ADM_TYPE.Y=41
ADM_TYPE.Width=81
ADM_TYPE.Height=23
ADM_TYPE.Text=TButton
ADM_TYPE.showID=Y
ADM_TYPE.Editable=Y
ADM_TYPE.StringData=[[id,text],[,],[O,门诊],[E,急诊]]
ADM_TYPE.TableShowList=text
ADM_TYPE.ExpandWidth=80
Table.Type=TTable
Table.X=13
Table.Y=24
Table.Width=1458
Table.Height=1200
Table.SpacingRow=1
Table.RowHeight=20
Table.AutoX=Y
Table.AutoY=Y
Table.AutoWidth=Y
Table.AutoHeight=Y
Table.Header=编号,100;类型,80,TYPE_CODE;等级,80,CLASS_CODE;科室,80,DEPT_CODE;病区,80,STATION_CODE;发生日期,80;病案号,100;住院号,100;病患姓名,80;工作单位,120;主要负责人,80;定性日期,80;定性机构,80;原因,80,PATIENT_CONDITITION;后果,80,RESULT_CODE;操作人员,80;操作日期,100;操作终端,100
Table.LockColumns=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17
Table.Item=TYPE_CODE;CLASS_CODE;DEPT_CODE;STATION_CODE;PATIENT_CONDITITION;RESULT_CODE
Table.SQL=
Table.ColumnHorizontalAlignmentData=
Table.FocusIndexList=
Table.ParmMap=ACI_RECORD_NO;TYPE_CODE;CLASS_CODE;DEPT_CODE;STATION_CODE;OCCUR_DATE;MR_NO;IPD_NO;PAT_NAME;PAT_COMPANY;IN_CHARGE_CODE;IDENTITY_DATE;IDENTITY_COMPANY;PATIENT_CONDITITION;RESULT_CODE;OPT_USER;OPT_DATE;OPT_TERM
Table.AutoModifyDataStore=Y
Panel1.Type=TPanel
Panel1.X=5
Panel1.Y=75
Panel1.Width=1480
Panel1.Height=173
Panel1.Border=组|基本信息
Panel1.Item=tLabel_0;tLabel_1;tLabel_2;tLabel_3;tLabel_4;tLabel_5;tLabel_6;tLabel_7;tLabel_8;tLabel_9;tLabel_10;tLabel_11;tLabel_12;tLabel_13;TYPE_CODE;CLASS_CODE;OCCUR_DATE;DEPT_CODE;STATION_CODE;IPD_NO;MR_NO;PAT_NAME;PAT_COMPANY;IDENTITY_DATE;PATIENT_CONDITITION;RESULT_CODE;IDENTITY_COMPANY;IN_CHARGE_CODE
Panel1.AutoWidth=Y
Panel1.AutoX=Y
Panel1.AutoY=N
Panel1.FocusList=CLINICAREA_CODE;CLINIC_DESC;AREA_PY1;AREA_PY2;AREA_SEQ;AREA_DESCRIPTION;REGION_CODE
IN_CHARGE_CODE.Type=TTextField
IN_CHARGE_CODE.X=102
IN_CHARGE_CODE.Y=112
IN_CHARGE_CODE.Width=124
IN_CHARGE_CODE.Height=20
IN_CHARGE_CODE.Text=
IDENTITY_COMPANY.Type=TTextField
IDENTITY_COMPANY.X=524
IDENTITY_COMPANY.Y=112
IDENTITY_COMPANY.Width=132
IDENTITY_COMPANY.Height=20
IDENTITY_COMPANY.Text=
RESULT_CODE.Type=事故差错后果
RESULT_CODE.X=366
RESULT_CODE.Y=141
RESULT_CODE.Width=81
RESULT_CODE.Height=23
RESULT_CODE.Text=TButton
RESULT_CODE.showID=Y
RESULT_CODE.showName=Y
RESULT_CODE.showText=N
RESULT_CODE.showValue=N
RESULT_CODE.showPy1=Y
RESULT_CODE.showPy2=Y
RESULT_CODE.Editable=Y
RESULT_CODE.Tip=事故差错后果
RESULT_CODE.TableShowList=name
RESULT_CODE.ModuleParmString=GROUP_ID:ACI_RESLUT
RESULT_CODE.ModuleParmTag=
RESULT_CODE.ExpandWidth=80
PATIENT_CONDITITION.Type=事故差错原因
PATIENT_CONDITITION.X=130
PATIENT_CONDITITION.Y=141
PATIENT_CONDITITION.Width=96
PATIENT_CONDITITION.Height=23
PATIENT_CONDITITION.Text=TButton
PATIENT_CONDITITION.showID=Y
PATIENT_CONDITITION.showName=Y
PATIENT_CONDITITION.showText=N
PATIENT_CONDITITION.showValue=N
PATIENT_CONDITITION.showPy1=Y
PATIENT_CONDITITION.showPy2=Y
PATIENT_CONDITITION.Editable=Y
PATIENT_CONDITITION.Tip=事故差错原因
PATIENT_CONDITITION.TableShowList=name
PATIENT_CONDITITION.ModuleParmString=GROUP_ID:ACI_RESON
PATIENT_CONDITITION.ModuleParmTag=
PATIENT_CONDITITION.ExpandWidth=80
IDENTITY_DATE.Type=TTextFormat
IDENTITY_DATE.X=337
IDENTITY_DATE.Y=112
IDENTITY_DATE.Width=110
IDENTITY_DATE.Height=20
IDENTITY_DATE.Text=TTextFormat
IDENTITY_DATE.showDownButton=Y
IDENTITY_DATE.FormatType=date
IDENTITY_DATE.Format=yyyy/MM/dd
PAT_COMPANY.Type=TTextField
PAT_COMPANY.X=336
PAT_COMPANY.Y=82
PAT_COMPANY.Width=296
PAT_COMPANY.Height=20
PAT_COMPANY.Text=
PAT_NAME.Type=TTextField
PAT_NAME.X=102
PAT_NAME.Y=82
PAT_NAME.Width=125
PAT_NAME.Height=20
PAT_NAME.Text=
PAT_NAME.Enabled=Y
MR_NO.Type=TTextField
MR_NO.X=524
MR_NO.Y=52
MR_NO.Width=110
MR_NO.Height=20
MR_NO.Text=
MR_NO.Action=onMrNo
IPD_NO.Type=TTextField
IPD_NO.X=335
IPD_NO.Y=52
IPD_NO.Width=101
IPD_NO.Height=20
IPD_NO.Text=
STATION_CODE.Type=病区下拉列表
STATION_CODE.X=186
STATION_CODE.Y=51
STATION_CODE.Width=81
STATION_CODE.Height=23
STATION_CODE.Text=TButton
STATION_CODE.showID=Y
STATION_CODE.showName=Y
STATION_CODE.showText=N
STATION_CODE.showValue=N
STATION_CODE.showPy1=Y
STATION_CODE.showPy2=Y
STATION_CODE.Editable=Y
STATION_CODE.Tip=病区
STATION_CODE.TableShowList=name
STATION_CODE.ExpandWidth=80
DEPT_CODE.Type=科室下拉列表
DEPT_CODE.X=61
DEPT_CODE.Y=51
DEPT_CODE.Width=81
DEPT_CODE.Height=23
DEPT_CODE.Text=TButton
DEPT_CODE.showID=Y
DEPT_CODE.showName=Y
DEPT_CODE.showText=N
DEPT_CODE.showValue=N
DEPT_CODE.showPy1=Y
DEPT_CODE.showPy2=Y
DEPT_CODE.Editable=Y
DEPT_CODE.Tip=科室
DEPT_CODE.TableShowList=name
DEPT_CODE.ExpandWidth=80
OCCUR_DATE.Type=TTextFormat
OCCUR_DATE.X=524
OCCUR_DATE.Y=22
OCCUR_DATE.Width=110
OCCUR_DATE.Height=20
OCCUR_DATE.Text=TTextFormat
OCCUR_DATE.showDownButton=Y
OCCUR_DATE.FormatType=date
OCCUR_DATE.Format=yyyy/MM/dd
CLASS_CODE.Type=事故差错等级
CLASS_CODE.X=355
CLASS_CODE.Y=21
CLASS_CODE.Width=81
CLASS_CODE.Height=23
CLASS_CODE.Text=TButton
CLASS_CODE.showID=Y
CLASS_CODE.showName=Y
CLASS_CODE.showText=N
CLASS_CODE.showValue=N
CLASS_CODE.showPy1=Y
CLASS_CODE.showPy2=Y
CLASS_CODE.Editable=Y
CLASS_CODE.Tip=事故差错等级
CLASS_CODE.TableShowList=name
CLASS_CODE.ModuleParmString=GROUP_ID:ACI_CLASS
CLASS_CODE.ModuleParmTag=
CLASS_CODE.ExpandWidth=80
TYPE_CODE.Type=事故差错类型
TYPE_CODE.X=138
TYPE_CODE.Y=21
TYPE_CODE.Width=88
TYPE_CODE.Height=23
TYPE_CODE.Text=TButton
TYPE_CODE.showID=Y
TYPE_CODE.showName=Y
TYPE_CODE.showText=N
TYPE_CODE.showValue=N
TYPE_CODE.showPy1=Y
TYPE_CODE.showPy2=Y
TYPE_CODE.Editable=Y
TYPE_CODE.Tip=事故差错类型
TYPE_CODE.TableShowList=name
TYPE_CODE.ModuleParmString=GROUP_ID:ACI_TYPE
TYPE_CODE.ModuleParmTag=
TYPE_CODE.ExpandWidth=80
tLabel_13.Type=TLabel
tLabel_13.X=247
tLabel_13.Y=145
tLabel_13.Width=106
tLabel_13.Height=15
tLabel_13.Text=差错与事故后果:
tLabel_12.Type=TLabel
tLabel_12.X=22
tLabel_12.Y=145
tLabel_12.Width=106
tLabel_12.Height=15
tLabel_12.Text=差错与事故原因:
tLabel_11.Type=TLabel
tLabel_11.X=456
tLabel_11.Y=115
tLabel_11.Width=72
tLabel_11.Height=15
tLabel_11.Text=定性机构:
tLabel_10.Type=TLabel
tLabel_10.X=273
tLabel_10.Y=115
tLabel_10.Width=72
tLabel_10.Height=15
tLabel_10.Text=定性日期:
tLabel_9.Type=TLabel
tLabel_9.X=21
tLabel_9.Y=115
tLabel_9.Width=80
tLabel_9.Height=15
tLabel_9.Text=主要责任人:
tLabel_8.Type=TLabel
tLabel_8.X=273
tLabel_8.Y=85
tLabel_8.Width=63
tLabel_8.Height=15
tLabel_8.Text=工作单位:
tLabel_7.Type=TLabel
tLabel_7.X=21
tLabel_7.Y=85
tLabel_7.Width=72
tLabel_7.Height=15
tLabel_7.Text=病患姓名:
tLabel_6.Type=TLabel
tLabel_6.X=273
tLabel_6.Y=55
tLabel_6.Width=56
tLabel_6.Height=15
tLabel_6.Text=住院号:
tLabel_6.Color=黑
tLabel_5.Type=TLabel
tLabel_5.X=456
tLabel_5.Y=55
tLabel_5.Width=56
tLabel_5.Height=15
tLabel_5.Text=病案号:
tLabel_5.Color=蓝
tLabel_4.Type=TLabel
tLabel_4.X=456
tLabel_4.Y=25
tLabel_4.Width=72
tLabel_4.Height=15
tLabel_4.Text=发生日期:
tLabel_3.Type=TLabel
tLabel_3.X=146
tLabel_3.Y=55
tLabel_3.Width=41
tLabel_3.Height=15
tLabel_3.Text=病区:
tLabel_2.Type=TLabel
tLabel_2.X=21
tLabel_2.Y=55
tLabel_2.Width=41
tLabel_2.Height=15
tLabel_2.Text=科室:
tLabel_1.Type=TLabel
tLabel_1.X=244
tLabel_1.Y=25
tLabel_1.Width=106
tLabel_1.Height=15
tLabel_1.Text=差错与事故等级:
tLabel_0.Type=TLabel
tLabel_0.X=21
tLabel_0.Y=25
tLabel_0.Width=106
tLabel_0.Height=15
tLabel_0.Text=事故与差错类型: