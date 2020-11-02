#############################################
# <p>Title:非常态门诊 </p>
#
# <p>Description:非常态门诊 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author zhangk 2010-10-26
# @version 4.0
#############################################
<Type=TFrame>
UI.Title=非常态门诊
UI.MenuConfig=%ROOT%\config\opd\OPDAbnormalRegMenu.x
UI.Width=800
UI.Height=500
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.opd.OPDAbnormalRegControl
UI.Item=tPanel_0
tPanel_0.Type=TPanel
tPanel_0.X=5
tPanel_0.Y=5
tPanel_0.Width=790
tPanel_0.Height=490
tPanel_0.AutoX=Y
tPanel_0.AutoY=Y
tPanel_0.AutoWidth=Y
tPanel_0.AutoHeight=Y
tPanel_0.Border=凸
tPanel_0.Item=tLabel_0;DATE;tLabel_1;SESSION_CODE;tLabel_2;DEPT_CODE;tLabel_3;DR_CODE;CLINICTYPE_CODE;tLabel_4;tLabel_5;MR_NO;tLabel_6;PAT_NAME;tLabel_7;SEX_CODE;tLabel_8;tLabel_9;CTZ1_CODE;SERVICE_LEVEL;tLabel_10;REG_FEE;tLabel_11;CLINIC_FEE;tLabel_12;AR_AMT;tLabel_13;CTZ2_CODE;tLabel_25;CTZ3_CODE;tLabel_14;BIRTHDAY;tLabel_15;CLINICROOM_NO
CLINICROOM_NO.Type=TTextFormat
CLINICROOM_NO.X=645
CLINICROOM_NO.Y=72
CLINICROOM_NO.Width=120
CLINICROOM_NO.Height=22
CLINICROOM_NO.Text=
CLINICROOM_NO.showDownButton=Y
CLINICROOM_NO.PopupMenuHeader=代码,100;名称,200
CLINICROOM_NO.PopupMenuWidth=300
CLINICROOM_NO.PopupMenuHeight=300
CLINICROOM_NO.PopupMenuFilter=ID,1;NAME,1;PY1,1
CLINICROOM_NO.FormatType=combo
CLINICROOM_NO.HisOneNullRow=Y
CLINICROOM_NO.ValueColumn=ID
CLINICROOM_NO.ShowColumnList=NAME
CLINICROOM_NO.HorizontalAlignment=2
tLabel_15.Type=TLabel
tLabel_15.X=584
tLabel_15.Y=76
tLabel_15.Width=57
tLabel_15.Height=15
tLabel_15.Text=诊   间
BIRTHDAY.Type=TTextFormat
BIRTHDAY.X=645
BIRTHDAY.Y=115
BIRTHDAY.Width=120
BIRTHDAY.Height=22
BIRTHDAY.Text=
BIRTHDAY.FormatType=date
BIRTHDAY.Format=yyyy/MM/dd
BIRTHDAY.showDownButton=Y
BIRTHDAY.Enabled=N
tLabel_14.Type=TLabel
tLabel_14.X=583
tLabel_14.Y=119
tLabel_14.Width=63
tLabel_14.Height=15
tLabel_14.Text=出生日期
CTZ3_CODE.Type=身份折扣下拉列表
CTZ3_CODE.X=446
CTZ3_CODE.Y=160
CTZ3_CODE.Width=120
CTZ3_CODE.Height=22
CTZ3_CODE.Text=TButton
CTZ3_CODE.showID=Y
CTZ3_CODE.showName=Y
CTZ3_CODE.showText=N
CTZ3_CODE.showValue=N
CTZ3_CODE.showPy1=N
CTZ3_CODE.showPy2=N
CTZ3_CODE.Editable=Y
CTZ3_CODE.Tip=主身份
CTZ3_CODE.TableShowList=name
tLabel_25.Type=TLabel
tLabel_25.X=398
tLabel_25.Y=164
tLabel_25.Width=45
tLabel_25.Height=15
tLabel_25.Text=身份三
CTZ2_CODE.Type=身份折扣下拉列表
CTZ2_CODE.X=261
CTZ2_CODE.Y=160
CTZ2_CODE.Width=120
CTZ2_CODE.Height=22
CTZ2_CODE.Text=TButton
CTZ2_CODE.showID=Y
CTZ2_CODE.showName=Y
CTZ2_CODE.showText=N
CTZ2_CODE.showValue=N
CTZ2_CODE.showPy1=N
CTZ2_CODE.showPy2=N
CTZ2_CODE.Editable=Y
CTZ2_CODE.Tip=主身份
CTZ2_CODE.TableShowList=name
tLabel_13.Type=TLabel
tLabel_13.X=211
tLabel_13.Y=164
tLabel_13.Width=46
tLabel_13.Height=15
tLabel_13.Text=身份二
AR_AMT.Type=TNumberTextField
AR_AMT.X=445
AR_AMT.Y=204
AR_AMT.Width=120
AR_AMT.Height=60
AR_AMT.Text=0.00
AR_AMT.Format=#########0.00
AR_AMT.FontSize=30
AR_AMT.Enabled=N
tLabel_12.Type=TLabel
tLabel_12.X=397
tLabel_12.Y=227
tLabel_12.Width=50
tLabel_12.Height=15
tLabel_12.Text=总费用
CLINIC_FEE.Type=TNumberTextField
CLINIC_FEE.X=261
CLINIC_FEE.Y=204
CLINIC_FEE.Width=120
CLINIC_FEE.Height=60
CLINIC_FEE.Text=0.00
CLINIC_FEE.Format=#########0.00
CLINIC_FEE.FontSize=30
CLINIC_FEE.Enabled=N
tLabel_11.Type=TLabel
tLabel_11.X=210
tLabel_11.Y=227
tLabel_11.Width=51
tLabel_11.Height=15
tLabel_11.Text=诊查费
REG_FEE.Type=TNumberTextField
REG_FEE.X=70
REG_FEE.Y=204
REG_FEE.Width=120
REG_FEE.Height=60
REG_FEE.Text=0.00
REG_FEE.Format=#########0.00
REG_FEE.FontSize=30
REG_FEE.Enabled=N
tLabel_10.Type=TLabel
tLabel_10.X=582
tLabel_10.Y=164
tLabel_10.Width=60
tLabel_10.Height=15
tLabel_10.Text=服务等级
SERVICE_LEVEL.Type=服务等级下拉区域
SERVICE_LEVEL.X=645
SERVICE_LEVEL.Y=160
SERVICE_LEVEL.Width=120
SERVICE_LEVEL.Height=22
SERVICE_LEVEL.Text=
SERVICE_LEVEL.HorizontalAlignment=2
SERVICE_LEVEL.PopupMenuHeader=代码,100;名称,100
SERVICE_LEVEL.PopupMenuWidth=300
SERVICE_LEVEL.PopupMenuHeight=300
SERVICE_LEVEL.PopupMenuFilter=ID,1;NAME,1;PY1,1
SERVICE_LEVEL.FormatType=combo
SERVICE_LEVEL.ShowDownButton=Y
SERVICE_LEVEL.Tip=服务等级
SERVICE_LEVEL.ShowColumnList=NAME
SERVICE_LEVEL.HisOneNullRow=Y
SERVICE_LEVEL.Action=onClickClinicType
CTZ1_CODE.Type=身份折扣下拉列表
CTZ1_CODE.X=70
CTZ1_CODE.Y=160
CTZ1_CODE.Width=120
CTZ1_CODE.Height=22
CTZ1_CODE.Text=TButton
CTZ1_CODE.showID=Y
CTZ1_CODE.showName=Y
CTZ1_CODE.showText=N
CTZ1_CODE.showValue=N
CTZ1_CODE.showPy1=Y
CTZ1_CODE.showPy2=Y
CTZ1_CODE.Editable=Y
CTZ1_CODE.Tip=主身份
CTZ1_CODE.TableShowList=name
CTZ1_CODE.Action=
CTZ1_CODE.SelectedAction=onClickClinicType
tLabel_9.Type=TLabel
tLabel_9.X=20
tLabel_9.Y=164
tLabel_9.Width=48
tLabel_9.Height=15
tLabel_9.Text=身份一
tLabel_8.Type=TLabel
tLabel_8.X=21
tLabel_8.Y=227
tLabel_8.Width=55
tLabel_8.Height=15
tLabel_8.Text=挂号费
SEX_CODE.Type=性别下拉列表
SEX_CODE.X=445
SEX_CODE.Y=115
SEX_CODE.Width=120
SEX_CODE.Height=22
SEX_CODE.Text=TButton
SEX_CODE.showID=Y
SEX_CODE.showName=Y
SEX_CODE.showText=N
SEX_CODE.showValue=N
SEX_CODE.showPy1=N
SEX_CODE.showPy2=N
SEX_CODE.Editable=Y
SEX_CODE.Tip=性别
SEX_CODE.TableShowList=name
SEX_CODE.ModuleParmString=GROUP_ID:SYS_SEX
SEX_CODE.ModuleParmTag=
SEX_CODE.Enabled=N
tLabel_7.Type=TLabel
tLabel_7.X=398
tLabel_7.Y=119
tLabel_7.Width=46
tLabel_7.Height=15
tLabel_7.Text=性  别
PAT_NAME.Type=TTextField
PAT_NAME.X=260
PAT_NAME.Y=115
PAT_NAME.Width=120
PAT_NAME.Height=22
PAT_NAME.Text=
PAT_NAME.Enabled=N
tLabel_6.Type=TLabel
tLabel_6.X=211
tLabel_6.Y=119
tLabel_6.Width=47
tLabel_6.Height=15
tLabel_6.Text=姓  名
MR_NO.Type=TTextField
MR_NO.X=70
MR_NO.Y=115
MR_NO.Width=120
MR_NO.Height=22
MR_NO.Text=
MR_NO.Enabled=N
tLabel_5.Type=TLabel
tLabel_5.X=19
tLabel_5.Y=119
tLabel_5.Width=48
tLabel_5.Height=15
tLabel_5.Text=病案号
tLabel_4.Type=TLabel
tLabel_4.X=397
tLabel_4.Y=76
tLabel_4.Width=50
tLabel_4.Height=15
tLabel_4.Text=诊  别
CLINICTYPE_CODE.Type=号别
CLINICTYPE_CODE.X=445
CLINICTYPE_CODE.Y=72
CLINICTYPE_CODE.Width=120
CLINICTYPE_CODE.Height=22
CLINICTYPE_CODE.Text=
CLINICTYPE_CODE.HorizontalAlignment=2
CLINICTYPE_CODE.PopupMenuHeader=代码,100;名称,100
CLINICTYPE_CODE.PopupMenuWidth=300
CLINICTYPE_CODE.PopupMenuHeight=300
CLINICTYPE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
CLINICTYPE_CODE.FormatType=combo
CLINICTYPE_CODE.ShowDownButton=Y
CLINICTYPE_CODE.Tip=号别
CLINICTYPE_CODE.ShowColumnList=NAME
CLINICTYPE_CODE.HisOneNullRow=Y
CLINICTYPE_CODE.ClickedAction=
CLINICTYPE_CODE.Action=onClickClinicType
DR_CODE.Type=人员
DR_CODE.X=260
DR_CODE.Y=72
DR_CODE.Width=120
DR_CODE.Height=22
DR_CODE.Text=
DR_CODE.HorizontalAlignment=2
DR_CODE.PopupMenuHeader=代码,100;名称,100
DR_CODE.PopupMenuWidth=300
DR_CODE.PopupMenuHeight=300
DR_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
DR_CODE.FormatType=combo
DR_CODE.ShowDownButton=Y
DR_CODE.Tip=人员
DR_CODE.ShowColumnList=NAME
DR_CODE.HisOneNullRow=Y
DR_CODE.Classify=0
DR_CODE.OpdFitFlg=Y
tLabel_3.Type=TLabel
tLabel_3.X=211
tLabel_3.Y=76
tLabel_3.Width=46
tLabel_3.Height=15
tLabel_3.Text=医  生
DEPT_CODE.Type=科室
DEPT_CODE.X=70
DEPT_CODE.Y=72
DEPT_CODE.Width=120
DEPT_CODE.Height=22
DEPT_CODE.Text=
DEPT_CODE.HorizontalAlignment=2
DEPT_CODE.PopupMenuHeader=代码,100;名称,100
DEPT_CODE.PopupMenuWidth=300
DEPT_CODE.PopupMenuHeight=300
DEPT_CODE.FormatType=combo
DEPT_CODE.ShowDownButton=Y
DEPT_CODE.Tip=科室
DEPT_CODE.ShowColumnList=NAME
DEPT_CODE.HisOneNullRow=Y
DEPT_CODE.DeptCat1=
DEPT_CODE.OpdFitFlg=Y
tLabel_2.Type=TLabel
tLabel_2.X=19
tLabel_2.Y=76
tLabel_2.Width=45
tLabel_2.Height=15
tLabel_2.Text=科  别
SESSION_CODE.Type=时段下拉列表
SESSION_CODE.X=260
SESSION_CODE.Y=28
SESSION_CODE.Width=120
SESSION_CODE.Height=22
SESSION_CODE.Text=TButton
SESSION_CODE.showID=Y
SESSION_CODE.showName=Y
SESSION_CODE.showText=N
SESSION_CODE.showValue=N
SESSION_CODE.showPy1=N
SESSION_CODE.showPy2=N
SESSION_CODE.Editable=Y
SESSION_CODE.Tip=时段
SESSION_CODE.TableShowList=name
SESSION_CODE.ModuleParmString=
SESSION_CODE.ModuleParmTag=
SESSION_CODE.AdmType=O
SESSION_CODE.Enabled=N
tLabel_1.Type=TLabel
tLabel_1.X=211
tLabel_1.Y=32
tLabel_1.Width=49
tLabel_1.Height=15
tLabel_1.Text=时  段
DATE.Type=TTextFormat
DATE.X=69
DATE.Y=28
DATE.Width=120
DATE.Height=22
DATE.Text=
DATE.FormatType=date
DATE.showDownButton=Y
DATE.Format=yyyy/MM/dd
DATE.HorizontalAlignment=2
DATE.Enabled=N
tLabel_0.Type=TLabel
tLabel_0.X=19
tLabel_0.Y=32
tLabel_0.Width=49
tLabel_0.Height=15
tLabel_0.Text=日  期