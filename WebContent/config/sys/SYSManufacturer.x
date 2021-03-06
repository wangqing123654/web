#
# TBuilder Config File 
#
# Title:生产厂商
#
# Company:JavaHis
#
# Author:zhangy 2009.06.08
#
# version 1.0
#

<Type=TFrame>
UI.Title=生产厂商维护
UI.MenuConfig=%ROOT%\config\sys\SYSManufacturer_Menu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sys.SYSMannfacturerControl
UI.item=tPanel_4;tPanel_5
UI.layout=null
UI.Text=生产厂商维护
UI.Tip=生产厂商维护
UI.TopToolBar=Y
UI.TopMenu=Y
UI.FocusList=MAN_CODE;MAN_CHN_DESC;MAN_ENG_DESC;MAN_ABS_DESC;PY1;PY2;SEQ;DESCRIPTION;TEL;FAX;NATIONAL_CODE;POST_P;POST_C;POST_CODE;ADDRESS;E_MAIL;WEBSITE;PHA_FLG;MAT_FLG;DEV_FLG;OTHER_FLG
tPanel_5.Type=TPanel
tPanel_5.X=5
tPanel_5.Y=244
tPanel_5.Width=1014
tPanel_5.Height=499
tPanel_5.AutoX=Y
tPanel_5.AutoWidth=Y
tPanel_5.AutoHeight=Y
tPanel_5.Border=凹
tPanel_5.Item=TABLE
TABLE.Type=TTable
TABLE.X=72
TABLE.Y=2
TABLE.Width=81
TABLE.Height=495
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoSize=0
TABLE.AutoHeight=Y
TABLE.AutoWidth=Y
TABLE.AutoY=Y
TABLE.AutoX=Y
TABLE.Header=厂商代码,80;厂商名称,240;英文名称,120;厂商简称,120;拼音,60;助记码,60;顺序号,60;备注,120;国家,150,NATIONAL_CODE;邮编,120;地址,120;电话,120;传真机,120;网址,120;邮箱,120;药品厂商,80,boolean;低值耗材,80,boolean;设备生产,80,boolean;其他厂商,80,boolean;操作者,80;操作时间,120;操作IP,120
TABLE.Item=NATIONAL_CODE
TABLE.LockColumns=all
TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,right;7,left;8,left;9,left;10,left;11,left;12,left;13,left;18,left;19,left;20,left;21,left
TABLE.AutoModifyDataStore=Y
TABLE.ParmMap=MAN_CODE;MAN_CHN_DESC;MAN_ENG_DESC;MAN_ABS_DESC;PY1;PY2;SEQ;DESCRIPTION;NATIONAL_CODE;POST_CODE;ADDRESS;TEL;FAX;WEBSITE;E_MAIL;PHA_FLG;MAT_FLG;DEV_FLG;OTHER_FLG;OPT_USER;OPT_DATE;OPT_TERM
TABLE.ClickedAction=onTableClicked
tPanel_4.Type=TPanel
tPanel_4.X=5
tPanel_4.Y=5
tPanel_4.Width=1014
tPanel_4.Height=232
tPanel_4.AutoX=Y
tPanel_4.AutoY=Y
tPanel_4.AutoWidth=Y
tPanel_4.Border=组
tPanel_4.Item=tLabel_21;tLabel_22;tLabel_23;tLabel_24;MAN_CODE;MAN_CHN_DESC;MAN_ENG_DESC;MAN_ABS_DESC;tLabel_25;PY1;tLabel_26;PY2;tLabel_27;SEQ;tLabel_28;DESCRIPTION;tLabel_29;tLabel_30;tLabel_31;tLabel_32;tLabel_33;tLabel_34;tLabel_35;PHA_FLG;MAT_FLG;DEV_FLG;OTHER_FLG;FAX;WEBSITE;E_MAIL;ADDRESS;TEL;POST_CODE;tLabel_2;tLabel_9;NATIONAL_CODE;POST_P;POST_C
POST_C.Type=省
POST_C.X=348
POST_C.Y=102
POST_C.Width=120
POST_C.Height=23
POST_C.Text=
POST_C.HorizontalAlignment=2
POST_C.PopupMenuHeader=代码,100;名称,100
POST_C.PopupMenuWidth=300
POST_C.PopupMenuHeight=300
POST_C.PopupMenuFilter=ID,1;NAME,1;PY1,1
POST_C.FormatType=combo
POST_C.ShowDownButton=Y
POST_C.Tip=省
POST_C.ShowColumnList=NAME
POST_C.Action=POST_P|onQuery
POST_C.PostCode=<POST_P>
POST_C.HisOneNullRow=Y
POST_P.Type=市
POST_P.X=604
POST_P.Y=102
POST_P.Width=149
POST_P.Height=23
POST_P.Text=
POST_P.HorizontalAlignment=2
POST_P.PopupMenuHeader=代码,100;名称,100
POST_P.PopupMenuWidth=300
POST_P.PopupMenuHeight=300
POST_P.PopupMenuFilter=ID,1;NAME,1;PY1,1
POST_P.FormatType=combo
POST_P.ShowDownButton=Y
POST_P.Tip=市
POST_P.ShowColumnList=NAME
POST_P.Action=POST_C|onQuery;selectCode_1
POST_P.PostCode=<POST_C>
POST_P.HisOneNullRow=Y
NATIONAL_CODE.Type=国籍
NATIONAL_CODE.X=91
NATIONAL_CODE.Y=102
NATIONAL_CODE.Width=151
NATIONAL_CODE.Height=23
NATIONAL_CODE.Text=
NATIONAL_CODE.HorizontalAlignment=2
NATIONAL_CODE.PopupMenuHeader=代码,100;名称,100
NATIONAL_CODE.PopupMenuWidth=300
NATIONAL_CODE.PopupMenuHeight=300
NATIONAL_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
NATIONAL_CODE.FormatType=combo
NATIONAL_CODE.ShowDownButton=Y
NATIONAL_CODE.Tip=国籍
NATIONAL_CODE.ShowColumnList=NAME
NATIONAL_CODE.HisOneNullRow=Y
tLabel_9.Type=TLabel
tLabel_9.X=526
tLabel_9.Y=106
tLabel_9.Width=72
tLabel_9.Height=15
tLabel_9.Text=县、市:
tLabel_2.Type=TLabel
tLabel_2.X=267
tLabel_2.Y=106
tLabel_2.Width=80
tLabel_2.Height=15
tLabel_2.Text=省、直辖市:
POST_CODE.Type=TTextField
POST_CODE.X=837
POST_CODE.Y=103
POST_CODE.Width=100
POST_CODE.Height=20
POST_CODE.Text=
POST_CODE.Action=onPost
TEL.Type=TTextField
TEL.X=91
TEL.Y=71
TEL.Width=100
TEL.Height=20
TEL.Text=
ADDRESS.Type=TTextField
ADDRESS.X=91
ADDRESS.Y=135
ADDRESS.Width=376
ADDRESS.Height=20
ADDRESS.Text=
E_MAIL.Type=TTextField
E_MAIL.X=604
E_MAIL.Y=135
E_MAIL.Width=354
E_MAIL.Height=20
E_MAIL.Text=
WEBSITE.Type=TTextField
WEBSITE.X=91
WEBSITE.Y=167
WEBSITE.Width=377
WEBSITE.Height=20
WEBSITE.Text=
FAX.Type=TTextField
FAX.X=348
FAX.Y=71
FAX.Width=120
FAX.Height=20
FAX.Text=
OTHER_FLG.Type=TCheckBox
OTHER_FLG.X=523
OTHER_FLG.Y=169
OTHER_FLG.Width=150
OTHER_FLG.Height=23
OTHER_FLG.Text=其它生产厂商注记
DEV_FLG.Type=TCheckBox
DEV_FLG.X=523
DEV_FLG.Y=200
DEV_FLG.Width=150
DEV_FLG.Height=23
DEV_FLG.Text=设备生产厂商注记
MAT_FLG.Type=TCheckBox
MAT_FLG.X=264
MAT_FLG.Y=200
MAT_FLG.Width=180
MAT_FLG.Height=23
MAT_FLG.Text=低值耗材生产厂商注记
PHA_FLG.Type=TCheckBox
PHA_FLG.X=15
PHA_FLG.Y=200
PHA_FLG.Width=150
PHA_FLG.Height=23
PHA_FLG.Text=药品生产厂商注记
tLabel_35.Type=TLabel
tLabel_35.X=526
tLabel_35.Y=138
tLabel_35.Width=65
tLabel_35.Height=15
tLabel_35.Text=邮箱:
tLabel_34.Type=TLabel
tLabel_34.X=18
tLabel_34.Y=165
tLabel_34.Width=72
tLabel_34.Height=15
tLabel_34.Text=网址:
tLabel_33.Type=TLabel
tLabel_33.X=267
tLabel_33.Y=74
tLabel_33.Width=72
tLabel_33.Height=15
tLabel_33.Text=传真机:
tLabel_32.Type=TLabel
tLabel_32.X=17
tLabel_32.Y=74
tLabel_32.Width=72
tLabel_32.Height=15
tLabel_32.Text=电话:
tLabel_31.Type=TLabel
tLabel_31.X=18
tLabel_31.Y=138
tLabel_31.Width=72
tLabel_31.Height=15
tLabel_31.Text=地址:
tLabel_30.Type=TLabel
tLabel_30.X=776
tLabel_30.Y=106
tLabel_30.Width=53
tLabel_30.Height=15
tLabel_30.Text=邮编:
tLabel_29.Type=TLabel
tLabel_29.X=18
tLabel_29.Y=106
tLabel_29.Width=72
tLabel_29.Height=15
tLabel_29.Text=国家:
DESCRIPTION.Type=TTextField
DESCRIPTION.X=837
DESCRIPTION.Y=42
DESCRIPTION.Width=120
DESCRIPTION.Height=20
DESCRIPTION.Text=
tLabel_28.Type=TLabel
tLabel_28.X=772
tLabel_28.Y=45
tLabel_28.Width=65
tLabel_28.Height=15
tLabel_28.Text=备注:
SEQ.Type=TNumberTextField
SEQ.X=605
SEQ.Y=42
SEQ.Width=80
SEQ.Height=20
SEQ.Text=0
SEQ.Format=#########0
tLabel_27.Type=TLabel
tLabel_27.X=526
tLabel_27.Y=45
tLabel_27.Width=72
tLabel_27.Height=15
tLabel_27.Text=顺序号:
PY2.Type=TTextField
PY2.X=348
PY2.Y=42
PY2.Width=100
PY2.Height=20
PY2.Text=
tLabel_26.Type=TLabel
tLabel_26.X=267
tLabel_26.Y=45
tLabel_26.Width=72
tLabel_26.Height=15
tLabel_26.Text=助记码:
PY1.Type=TTextField
PY1.X=91
PY1.Y=42
PY1.Width=100
PY1.Height=20
PY1.Text=
tLabel_25.Type=TLabel
tLabel_25.X=17
tLabel_25.Y=45
tLabel_25.Width=72
tLabel_25.Height=15
tLabel_25.Text=拼音:
MAN_ABS_DESC.Type=TTextField
MAN_ABS_DESC.X=837
MAN_ABS_DESC.Y=12
MAN_ABS_DESC.Width=100
MAN_ABS_DESC.Height=20
MAN_ABS_DESC.Text=
MAN_ENG_DESC.Type=TTextField
MAN_ENG_DESC.X=605
MAN_ENG_DESC.Y=12
MAN_ENG_DESC.Width=120
MAN_ENG_DESC.Height=20
MAN_ENG_DESC.Text=
MAN_CHN_DESC.Type=TTextField
MAN_CHN_DESC.X=348
MAN_CHN_DESC.Y=12
MAN_CHN_DESC.Width=150
MAN_CHN_DESC.Height=20
MAN_CHN_DESC.Text=
MAN_CHN_DESC.Action=onManDescAction
MAN_CODE.Type=TTextField
MAN_CODE.X=91
MAN_CODE.Y=12
MAN_CODE.Width=120
MAN_CODE.Height=20
MAN_CODE.Text=
tLabel_24.Type=TLabel
tLabel_24.X=772
tLabel_24.Y=14
tLabel_24.Width=65
tLabel_24.Height=15
tLabel_24.Text=厂商简称:
tLabel_23.Type=TLabel
tLabel_23.X=526
tLabel_23.Y=14
tLabel_23.Width=72
tLabel_23.Height=15
tLabel_23.Text=英文名称:
tLabel_22.Type=TLabel
tLabel_22.X=267
tLabel_22.Y=14
tLabel_22.Width=80
tLabel_22.Height=15
tLabel_22.Text=厂商名称:
tLabel_22.Color=blue
tLabel_21.Type=TLabel
tLabel_21.X=15
tLabel_21.Y=14
tLabel_21.Width=70
tLabel_21.Height=15
tLabel_21.Text=厂商代码:
tLabel_21.Color=blue