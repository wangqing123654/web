#
# TBuilder Config File 
#
# Title: 料位档
#
# Company:JavaHis
#
# Author:zhangy 2009.04.22
#
# version 1.0
#

<Type=TFrame>
UI.Title=药品料位
UI.MenuConfig=%ROOT%\config\ind\INDMateriallocMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.ind.INDMateriallocControl
UI.item=tPanel_1;tPanel_2;tMovePane_0
UI.layout=null
UI.TopMenu=Y
UI.TopToolBar=Y
UI.ShowTitle=N
UI.ShowMenu=N
UI.Text=药品料位
UI.Tip=药品料位
UI.FocusList=ORG_CODE;MATERIAL_LOC_CODE;MATERIAL_CHN_DESC;MATERIAL_ENG_DESC;PY1;PY2;SEQ;DESCRIPTION
tMovePane_0.Type=TMovePane
tMovePane_0.X=4
tMovePane_0.Y=93
tMovePane_0.Width=100
tMovePane_0.Height=5
tMovePane_0.Text=
tMovePane_0.MoveType=2
tMovePane_0.Border=凸
tMovePane_0.AutoX=Y
tMovePane_0.AutoWidth=Y
tMovePane_0.EntityData=tPanel_1,2;tPanel_2,1
tMovePane_0.Style=3
tPanel_2.Type=TPanel
tPanel_2.X=5
tPanel_2.Y=98
tPanel_2.Width=1014
tPanel_2.Height=635
tPanel_2.Border=凹
tPanel_2.AutoX=Y
tPanel_2.AutoWidth=Y
tPanel_2.AutoHeight=Y
tPanel_2.AutoSize=5
tPanel_2.Item=TABLE
TABLE.Type=TTable
TABLE.X=142
TABLE.Y=2
TABLE.Width=1010
TABLE.Height=631
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoX=Y
TABLE.AutoY=Y
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.AutoSize=0
TABLE.Header=部门名称,100,ORG_CODE;料位代码,80;料位名称,100;英文名称,80;拼音,80;助记码,80;序号,60;备注,150;操作者,100;操作时间,120;操作IP,100
TABLE.LockColumns=0,1,2,3,4,5,6,7,8,9,10
TABLE.AutoModifyDataStore=Y
TABLE.Item=ORG_CODE
TABLE.ParmMap=ORG_CODE;MATERIAL_LOC_CODE;MATERIAL_CHN_DESC;MATERIAL_ENG_DESC;PY1;PY2;SEQ;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERM
TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,right;7,left;8,left;9,left;10,left
TABLE.ClickedAction=onTableClicked
tPanel_1.Type=TPanel
tPanel_1.X=5
tPanel_1.Y=5
tPanel_1.Width=1014
tPanel_1.Height=88
tPanel_1.Border=组
tPanel_1.AutoSize=5
tPanel_1.AutoX=Y
tPanel_1.AutoY=Y
tPanel_1.AutoWidth=Y
tPanel_1.Item=tLabel_0;tLabel_1;tLabel_2;tLabel_3;tLabel_4;tLabel_7;tLabel_9;tLabel_10;MATERIAL_LOC_CODE;MATERIAL_CHN_DESC;MATERIAL_ENG_DESC;PY1;PY2;SEQ;DESCRIPTION;ORG_CODE
tPanel_1.FocusList=
ORG_CODE.Type=药房
ORG_CODE.X=92
ORG_CODE.Y=15
ORG_CODE.Width=120
ORG_CODE.Height=23
ORG_CODE.Text=
ORG_CODE.HorizontalAlignment=2
ORG_CODE.PopupMenuHeader=ID,100;NAME,100
ORG_CODE.PopupMenuWidth=300
ORG_CODE.PopupMenuHeight=300
ORG_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
ORG_CODE.FormatType=combo
ORG_CODE.ShowDownButton=Y
ORG_CODE.Tip=药房
ORG_CODE.ShowColumnList=NAME
ORG_CODE.OrgFlg=Y
DESCRIPTION.Type=TTextField
DESCRIPTION.X=699
DESCRIPTION.Y=54
DESCRIPTION.Width=150
DESCRIPTION.Height=20
DESCRIPTION.Text=
SEQ.Type=TNumberTextField
SEQ.X=500
SEQ.Y=54
SEQ.Width=100
SEQ.Height=20
SEQ.Text=0
SEQ.Format=#########0
PY2.Type=TTextField
PY2.X=305
PY2.Y=54
PY2.Width=100
PY2.Height=20
PY2.Text=
PY1.Type=TTextField
PY1.X=92
PY1.Y=54
PY1.Width=120
PY1.Height=20
PY1.Text=
MATERIAL_ENG_DESC.Type=TTextField
MATERIAL_ENG_DESC.X=699
MATERIAL_ENG_DESC.Y=17
MATERIAL_ENG_DESC.Width=100
MATERIAL_ENG_DESC.Height=20
MATERIAL_ENG_DESC.Text=
MATERIAL_CHN_DESC.Type=TTextField
MATERIAL_CHN_DESC.X=500
MATERIAL_CHN_DESC.Y=17
MATERIAL_CHN_DESC.Width=100
MATERIAL_CHN_DESC.Height=20
MATERIAL_CHN_DESC.Text=
MATERIAL_CHN_DESC.Action=onMaterialAction
MATERIAL_LOC_CODE.Type=TTextField
MATERIAL_LOC_CODE.X=305
MATERIAL_LOC_CODE.Y=17
MATERIAL_LOC_CODE.Width=100
MATERIAL_LOC_CODE.Height=20
MATERIAL_LOC_CODE.Text=
MATERIAL_LOC_CODE.Action=
tLabel_10.Type=TLabel
tLabel_10.X=626
tLabel_10.Y=56
tLabel_10.Width=72
tLabel_10.Height=15
tLabel_10.Text=备注:
tLabel_9.Type=TLabel
tLabel_9.X=427
tLabel_9.Y=56
tLabel_9.Width=72
tLabel_9.Height=15
tLabel_9.Text=序号:
tLabel_7.Type=TLabel
tLabel_7.X=233
tLabel_7.Y=56
tLabel_7.Width=72
tLabel_7.Height=15
tLabel_7.Text=助记码:
tLabel_4.Type=TLabel
tLabel_4.X=20
tLabel_4.Y=56
tLabel_4.Width=72
tLabel_4.Height=15
tLabel_4.Text=拼音:
tLabel_3.Type=TLabel
tLabel_3.X=626
tLabel_3.Y=20
tLabel_3.Width=72
tLabel_3.Height=15
tLabel_3.Text=英文名称:
tLabel_2.Type=TLabel
tLabel_2.X=427
tLabel_2.Y=20
tLabel_2.Width=72
tLabel_2.Height=15
tLabel_2.Text=料位名称:
tLabel_1.Type=TLabel
tLabel_1.X=233
tLabel_1.Y=20
tLabel_1.Width=72
tLabel_1.Height=15
tLabel_1.Text=料位代码:
tLabel_1.Color=blue
tLabel_0.Type=TLabel
tLabel_0.X=20
tLabel_0.Y=20
tLabel_0.Width=72
tLabel_0.Height=15
tLabel_0.Text=部门代码:
tLabel_0.Color=blue
tLabel_0.AutoX=N
tLabel_0.AutoY=N
tLabel_0.AutoSize=20