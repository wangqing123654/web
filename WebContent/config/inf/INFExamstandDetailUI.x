#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:sundx 2009.12.18
#
# version 1.0
#

<Type=TFrame>
UI.Title=科室检测方案检测项目对照
UI.MenuConfig=%ROOT%\config\inf\INFExamstandDetailMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.inf.INFExamstandDetailControl
UI.item=tPanel_0;TABLE
UI.layout=null
UI.TopMenu=Y
UI.TopToolBar=Y
UI.ShowMenu=Y
TABLE.Type=TTable
TABLE.X=6
TABLE.Y=91
TABLE.Width=1012
TABLE.Height=651
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoX=Y
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.Header=检测标准,100,EXAMSTAND_CODE;检测项目,100,INFITEM_CODE;监测单项评比,100,double;操作人员,100,OPERATOR;操作时间,100;操作端末,100
TABLE.ParmMap=EXAMSTAND_CODE;INFITEM_CODE;ITEM_GAINPOINT;OPT_USER;OPT_DATE;OPT_TERM
TABLE.Item=EXAMSTAND_CODE;INFITEM_CODE;OPERATOR
TABLE.LockColumns=0,3,4,5
TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,right;3,left;4,left;5,left
TABLE.AutoModifyDataStore=Y
tPanel_0.Type=TPanel
tPanel_0.X=5
tPanel_0.Y=5
tPanel_0.Width=1014
tPanel_0.Height=79
tPanel_0.AutoWidth=Y
tPanel_0.AutoY=Y
tPanel_0.AutoHeight=N
tPanel_0.Border=组
tPanel_0.AutoX=Y
tPanel_0.Item=EXAMSTAND_CODE;tLabel_0
OPERATOR.Type=人员
OPERATOR.X=343
OPERATOR.Y=34
OPERATOR.Width=81
OPERATOR.Height=23
OPERATOR.Text=
OPERATOR.HorizontalAlignment=2
OPERATOR.PopupMenuHeader=代码,100;名称,100
OPERATOR.PopupMenuWidth=300
OPERATOR.PopupMenuHeight=300
OPERATOR.PopupMenuFilter=ID,1;NAME,1;PY1,1
OPERATOR.FormatType=combo
OPERATOR.ShowDownButton=Y
OPERATOR.Tip=人员
OPERATOR.ShowColumnList=NAME
INFITEM_CODE.Type=检测项目下拉区域
INFITEM_CODE.X=350
INFITEM_CODE.Y=26
INFITEM_CODE.Width=81
INFITEM_CODE.Height=23
INFITEM_CODE.Text=
INFITEM_CODE.HorizontalAlignment=2
INFITEM_CODE.PopupMenuHeader=代码,100;名称,100
INFITEM_CODE.PopupMenuWidth=300
INFITEM_CODE.PopupMenuHeight=300
INFITEM_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
INFITEM_CODE.FormatType=combo
INFITEM_CODE.ShowDownButton=Y
INFITEM_CODE.Tip=检测项目
INFITEM_CODE.ShowColumnList=NAME
INFITEM_CODE.HisOneNullRow=Y
tLabel_0.Type=TLabel
tLabel_0.X=16
tLabel_0.Y=32
tLabel_0.Width=72
tLabel_0.Height=15
tLabel_0.Text=检测标准
EXAMSTAND_CODE.Type=监测标准下拉区域
EXAMSTAND_CODE.X=111
EXAMSTAND_CODE.Y=28
EXAMSTAND_CODE.Width=168
EXAMSTAND_CODE.Height=23
EXAMSTAND_CODE.Text=
EXAMSTAND_CODE.HorizontalAlignment=2
EXAMSTAND_CODE.PopupMenuHeader=代码,100;名称,100
EXAMSTAND_CODE.PopupMenuWidth=300
EXAMSTAND_CODE.PopupMenuHeight=300
EXAMSTAND_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
EXAMSTAND_CODE.FormatType=combo
EXAMSTAND_CODE.ShowDownButton=Y
EXAMSTAND_CODE.Tip=监测标准
EXAMSTAND_CODE.ShowColumnList=NAME
EXAMSTAND_CODE.HisOneNullRow=Y
EXAMSTAND_CODE.Action=onStand