#
# TBuilder Config File 
#
# Title:常用医嘱维护
#
# Company:JavaHis
#
# Author:ehui 2009.04.11
#
# version 1.0
#

<Type=TFrame>
UI.Title=常用医嘱
UI.MenuConfig=%ROOT%\config\opd\ODOCommonOrderMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.opd.OdoCommonOrderControl
UI.item=LABEL;DEPT;OPERATOR;TABPANEL;OPTITEM_CODE;DEPT;INS_PAY
UI.layout=null
UI.TopToolBar=Y
UI.TopMenu=Y
DCTEXCEP_CODE.Type=特殊煎法下拉列表
DCTEXCEP_CODE.X=232
DCTEXCEP_CODE.Y=8
DCTEXCEP_CODE.Width=81
DCTEXCEP_CODE.Height=23
DCTEXCEP_CODE.Text=TButton
DCTEXCEP_CODE.showID=N
DCTEXCEP_CODE.showName=Y
DCTEXCEP_CODE.showText=N
DCTEXCEP_CODE.showValue=N
DCTEXCEP_CODE.showPy1=Y
DCTEXCEP_CODE.showPy2=Y
DCTEXCEP_CODE.Editable=Y
DCTEXCEP_CODE.Tip=特殊煎法
DCTEXCEP_CODE.TableShowList=name
DCTEXCEP_CODE.ModuleParmString=GROUP_ID:PHA_DCTEXCEP
DCTEXCEP_CODE.ModuleParmTag=
INS_PAY.Type=付款途径下拉列表
INS_PAY.X=70
INS_PAY.Y=414
INS_PAY.Width=81
INS_PAY.Height=23
INS_PAY.Text=TButton
INS_PAY.showID=N
INS_PAY.showName=Y
INS_PAY.showText=N
INS_PAY.showValue=N
INS_PAY.showPy1=N
INS_PAY.showPy2=N
INS_PAY.Editable=Y
INS_PAY.Tip=付款途径
INS_PAY.TableShowList=name
INS_PAY.ModuleParmString=GROUP_ID:SYS_DEFAULTPAYWAY
INS_PAY.ModuleParmTag=


OPTITEM_CODE.Type=检体下拉列表
OPTITEM_CODE.X=224
OPTITEM_CODE.Y=448
OPTITEM_CODE.Width=81
OPTITEM_CODE.Height=23
OPTITEM_CODE.Text=TButton
OPTITEM_CODE.showID=N
OPTITEM_CODE.showName=Y
OPTITEM_CODE.showText=N
OPTITEM_CODE.showValue=N
OPTITEM_CODE.showPy1=N
OPTITEM_CODE.showPy2=N
OPTITEM_CODE.Editable=Y
OPTITEM_CODE.Tip=性别
OPTITEM_CODE.TableShowList=name
OPTITEM_CODE.ModuleParmTag=

TABPANEL.Type=TTabbedPane
TABPANEL.X=13
TABPANEL.Y=38
TABPANEL.Width=653
TABPANEL.Height=367
TABPANEL.AutoWidth=Y
TABPANEL.AutoHeight=Y
TABPANEL.Item=tPanel_0;tPanel_1
tPanel_1.Type=TPanel
tPanel_1.X=57
tPanel_1.Y=9
tPanel_1.Width=81
tPanel_1.Height=81
tPanel_1.Name=中医
tPanel_1.Enabled=Y
tPanel_1.Item=tButton_0;tButton_1;tLabel_3;RX_NO;TAKE_DAYS;tLabel_5;tLabel_6;CHN_FREQ_CODE;tLabel_7;CHN_ROUTE_CODE;tLabel_8;DCTAGENT_CODE;tLabel_9;PACKAGE_TOT;CHNTABLE
tPanel_1.FocusList=TAKE_DAYS;CHN_FREQ_CODE;CHN_ROUTE_CODE;DCTAGENT_CODE
CHNTABLE.Type=TTable
CHNTABLE.X=5
CHNTABLE.Y=37
CHNTABLE.Width=991
CHNTABLE.Height=634
CHNTABLE.SpacingRow=1
CHNTABLE.RowHeight=20
CHNTABLE.AutoWidth=Y
CHNTABLE.AutoHeight=Y
CHNTABLE.Header=药名,100;用量,100;特殊煎法,140,DCTEXCEP_CODE
CHNTABLE.ParmMap=ORDER_DESC;MEDI_QTY;DCTEXCEP_CODE
CHNTABLE.Item=DCTEXCEP_CODE
CHNTABLE.FocusIndexList=0,1,2
CHNTABLE.ColumnHorizontalAlignmentData=0,left;1,right;2,left
PACKAGE_TOT.Type=TTextField
PACKAGE_TOT.X=851
PACKAGE_TOT.Y=7
PACKAGE_TOT.Width=77
PACKAGE_TOT.Height=20
PACKAGE_TOT.Text=
PACKAGE_TOT.Enabled=N
tLabel_9.Type=TLabel
tLabel_9.X=813
tLabel_9.Y=10
tLabel_9.Width=33
tLabel_9.Height=15
tLabel_9.Text=总重
DCTAGENT_CODE.Type=煎药方式下拉列表
DCTAGENT_CODE.X=708
DCTAGENT_CODE.Y=6
DCTAGENT_CODE.Width=102
DCTAGENT_CODE.Height=23
DCTAGENT_CODE.Text=TButton
DCTAGENT_CODE.showID=N
DCTAGENT_CODE.showName=Y
DCTAGENT_CODE.showText=N
DCTAGENT_CODE.showValue=N
DCTAGENT_CODE.showPy1=N
DCTAGENT_CODE.showPy2=N
DCTAGENT_CODE.Editable=Y
DCTAGENT_CODE.Tip=煎法
DCTAGENT_CODE.TableShowList=name
DCTAGENT_CODE.ModuleParmString=GROUP_ID:PHA_DCTAGENT
DCTAGENT_CODE.ModuleParmTag=
DCTAGENT_CODE.SelectedAction=onDctAgent
tLabel_8.Type=TLabel
tLabel_8.X=677
tLabel_8.Y=10
tLabel_8.Width=30
tLabel_8.Height=15
tLabel_8.Text=煎法
CHN_ROUTE_CODE.Type=途径下拉列表
CHN_ROUTE_CODE.X=582
CHN_ROUTE_CODE.Y=6
CHN_ROUTE_CODE.Width=90
CHN_ROUTE_CODE.Height=23
CHN_ROUTE_CODE.Text=TButton
CHN_ROUTE_CODE.showID=N
CHN_ROUTE_CODE.showName=Y
CHN_ROUTE_CODE.showText=N
CHN_ROUTE_CODE.showValue=N
CHN_ROUTE_CODE.showPy1=N
CHN_ROUTE_CODE.showPy2=N
CHN_ROUTE_CODE.Editable=Y
CHN_ROUTE_CODE.Tip=频次
CHN_ROUTE_CODE.TableShowList=name
CHN_ROUTE_CODE.WesmedFlg=N
tLabel_7.Type=TLabel
tLabel_7.X=550
tLabel_7.Y=10
tLabel_7.Width=32
tLabel_7.Height=15
tLabel_7.Text=用法
CHN_FREQ_CODE.Type=频次下拉列表
CHN_FREQ_CODE.X=476
CHN_FREQ_CODE.Y=6
CHN_FREQ_CODE.Width=69
CHN_FREQ_CODE.Height=23
CHN_FREQ_CODE.Text=TButton
CHN_FREQ_CODE.showID=N
CHN_FREQ_CODE.showName=Y
CHN_FREQ_CODE.showText=N
CHN_FREQ_CODE.showValue=N
CHN_FREQ_CODE.showPy1=N
CHN_FREQ_CODE.showPy2=N
CHN_FREQ_CODE.Editable=Y
CHN_FREQ_CODE.Tip=频次
CHN_FREQ_CODE.TableShowList=name
tLabel_6.Type=TLabel
tLabel_6.X=445
tLabel_6.Y=10
tLabel_6.Width=32
tLabel_6.Height=15
tLabel_6.Text=频次
tLabel_5.Type=TLabel
tLabel_5.X=348
tLabel_5.Y=10
tLabel_5.Width=46
tLabel_5.Height=15
tLabel_5.Text=付数
tLabel_5.zhText=付数
tLabel_5.enText=TakeDay
TAKE_DAYS.Type=TTextField
TAKE_DAYS.X=404
TAKE_DAYS.Y=7
TAKE_DAYS.Width=35
TAKE_DAYS.Height=20
TAKE_DAYS.Text=
RX_NO.Type=TComboBox
RX_NO.X=226
RX_NO.Y=6
RX_NO.Width=118
RX_NO.Height=23
RX_NO.Text=TButton
RX_NO.showID=N
RX_NO.Editable=Y
RX_NO.ParmMap=id:ID;name:NAME
RX_NO.ShowName=Y
RX_NO.ShowText=N
RX_NO.TableShowList=name
RX_NO.Action=
RX_NO.SelectedAction=onChangeRx
tLabel_3.Type=TLabel
tLabel_3.X=176
tLabel_3.Y=10
tLabel_3.Width=47
tLabel_3.Height=15
tLabel_3.Text=处方号
tLabel_3.zhText=处方号
tLabel_3.enText=Rx:
tButton_1.Type=TButton
tButton_1.X=88
tButton_1.Y=6
tButton_1.Width=81
tButton_1.Height=23
tButton_1.Text=删处方
tButton_1.Action=onDeleteR
tButton_1.zhText=删处方
tButton_1.enText=Del Rx
tButton_0.Type=TButton
tButton_0.X=4
tButton_0.Y=6
tButton_0.Width=81
tButton_0.Height=23
tButton_0.Text=新处方
tButton_0.Action=onNewR
tButton_0.zhText=新处方
tButton_0.enText=Add Rx
tPanel_0.Type=TPanel
tPanel_0.X=39
tPanel_0.Y=23
tPanel_0.Width=81
tPanel_0.Height=81
tPanel_0.Name=检验处置及药品
tPanel_0.Item=MEDTABLE
ROUTE_CODE.Type=用法下拉区域
ROUTE_CODE.X=432
ROUTE_CODE.Y=317
ROUTE_CODE.Width=81
ROUTE_CODE.Height=23
ROUTE_CODE.Text=
ROUTE_CODE.HorizontalAlignment=2
ROUTE_CODE.PopupMenuHeader=代码,100;名称,100
ROUTE_CODE.PopupMenuWidth=300
ROUTE_CODE.PopupMenuHeight=300
ROUTE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
ROUTE_CODE.FormatType=combo
ROUTE_CODE.ShowDownButton=Y
ROUTE_CODE.Tip=用法
ROUTE_CODE.ShowColumnList=NAME
FREQ_CODE.Type=频次
FREQ_CODE.X=317
FREQ_CODE.Y=316
FREQ_CODE.Width=81
FREQ_CODE.Height=23
FREQ_CODE.Text=
FREQ_CODE.HorizontalAlignment=2
FREQ_CODE.PopupMenuHeader=代码,100;名称,100
FREQ_CODE.PopupMenuWidth=300
FREQ_CODE.PopupMenuHeight=300
FREQ_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
FREQ_CODE.FormatType=combo
FREQ_CODE.ShowDownButton=Y
FREQ_CODE.Tip=频次
FREQ_CODE.ShowColumnList=NAME
UNIT_CODE.Type=计量单位
UNIT_CODE.X=154
UNIT_CODE.Y=310
UNIT_CODE.Width=81
UNIT_CODE.Height=23
UNIT_CODE.Text=
UNIT_CODE.HorizontalAlignment=2
UNIT_CODE.PopupMenuHeader=代码,100;名称,100
UNIT_CODE.PopupMenuWidth=300
UNIT_CODE.PopupMenuHeight=300
UNIT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
UNIT_CODE.FormatType=combo
UNIT_CODE.ShowDownButton=Y
UNIT_CODE.Tip=计量单位
UNIT_CODE.ShowColumnList=NAME
MEDTABLE.Type=TTable
MEDTABLE.X=3
MEDTABLE.Y=429
MEDTABLE.Width=991
MEDTABLE.Height=666
MEDTABLE.SpacingRow=1
MEDTABLE.RowHeight=20
MEDTABLE.AutoX=Y
MEDTABLE.AutoY=Y
MEDTABLE.AutoWidth=Y
MEDTABLE.AutoHeight=Y
MEDTABLE.Header=医嘱,200;用量,60,double;单位,60,UNIT_CODE;频次,70,FREQ_CODE;用法,60,ROUTE_CODE;日份,70,int;医生备注,200;特定科室,80,DEPT;盒,30,boolean;门,30,boolean;急,30,boolean;住,30,boolean
MEDTABLE.LockColumns=2
MEDTABLE.FocusIndexList=0,1,3,4,5
MEDTABLE.ColumnHorizontalAlignmentData=0,left;1,right;2,left;3,left;4,left;5,right;6,left;7,left
MEDTABLE.Item=UNIT_CODE;DEPT;FREQ_CODE;ROUTE_CODE
MEDTABLE.ParmMap=ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;DESCRIPTION;SPCFYDEPT;GIVEBOX_FLG;OPD_FIT_FLG;EMG_FIT_FLG;IPD_FIT_FLG
MEDTABLE.FocusType=2
MEDTABLE.AutoModifyDataStore=Y
OPERATOR.Type=人员下拉列表
OPERATOR.X=51
OPERATOR.Y=8
OPERATOR.Width=120
OPERATOR.Height=23
OPERATOR.Text=TButton
OPERATOR.showID=N
OPERATOR.showName=Y
OPERATOR.showText=N
OPERATOR.showValue=N
OPERATOR.showPy1=N
OPERATOR.showPy2=N
OPERATOR.Editable=Y
OPERATOR.Tip=人员
OPERATOR.TableShowList=name
OPERATOR.ModuleParmString=
OPERATOR.ModuleParmTag=
DEPT.Type=科室下拉列表
DEPT.X=46
DEPT.Y=8
DEPT.Width=120
DEPT.Height=23
DEPT.Text=TButton
DEPT.showID=N
DEPT.showName=Y
DEPT.showText=N
DEPT.showValue=N
DEPT.showPy1=N
DEPT.showPy2=N
DEPT.Editable=Y
DEPT.Tip=科室
DEPT.TableShowList=name
DEPT.Classify=0
DEPT.FinalFlg=Y
DEPT.OpdFitFlg=Y
DEPT.EmgFitFlg=Y
DEPT.IpdFitFlg=Y
LABEL.Type=TLabel
LABEL.X=12
LABEL.Y=13
LABEL.Width=34
LABEL.Height=15
LABEL.Text=科室