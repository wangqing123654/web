#
# TBuilder Config File 
#
# Title:料位查询
#
# Company:JavaHis
#
# Author:zhangy 2009.05.06
#
# version 1.0
#

<Type=TFrame>
UI.Title=选择料位
UI.MenuConfig=%ROOT%\config\ind\INDMacValidMenu.x
UI.Width=360
UI.Height=500
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.ind.INDMacValidControl
UI.item=tPanel_0;tPanel_1
UI.layout=null
UI.Name=
UI.Text=选择料位
UI.Tip=选择料位
UI.TopMenu=Y
UI.TopToolBar=Y
tPanel_1.Type=TPanel
tPanel_1.X=5
tPanel_1.Y=55
tPanel_1.Width=350
tPanel_1.Height=440
tPanel_1.AutoX=Y
tPanel_1.AutoWidth=Y
tPanel_1.AutoHeight=Y
tPanel_1.Border=凹
tPanel_1.Item=TABLE
TABLE.Type=TTable
TABLE.X=77
TABLE.Y=32
TABLE.Width=346
TABLE.Height=436
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoSize=0
TABLE.AutoHeight=Y
TABLE.AutoWidth=Y
TABLE.AutoY=Y
TABLE.AutoX=Y
TABLE.Header=料位代码,150;料位名称,180
TABLE.ParmMap=MATERIAL_LOC_CODE;MATERIAL_CHN_DESC
TABLE.ColumnHorizontalAlignmentData=0,left;1,left
TABLE.LockColumns=0,1
TABLE.ClickedAction=onTableClicked
TABLE.Item=
TABLE.AutoModifyDataStore=N
TABLE.ModifyTag=
TABLE.AutoModifyObject=Y
tPanel_0.Type=TPanel
tPanel_0.X=5
tPanel_0.Y=5
tPanel_0.Width=350
tPanel_0.Height=44
tPanel_0.AutoX=Y
tPanel_0.AutoY=Y
tPanel_0.AutoWidth=Y
tPanel_0.Border=组
tPanel_0.Item=tLabel_0;CODE;tLabel_2;DESC
DESC.Type=TTextField
DESC.X=250
DESC.Y=11
DESC.Width=77
DESC.Height=20
DESC.Text=
DESC.Enabled=N
tLabel_2.Type=TLabel
tLabel_2.X=176
tLabel_2.Y=13
tLabel_2.Width=72
tLabel_2.Height=15
tLabel_2.Text=料位名称:
CODE.Type=TTextField
CODE.X=82
CODE.Y=11
CODE.Width=80
CODE.Height=20
CODE.Text=
CODE.Enabled=N
tLabel_0.Type=TLabel
tLabel_0.X=10
tLabel_0.Y=14
tLabel_0.Width=72
tLabel_0.Height=15
tLabel_0.Text=料位代码: