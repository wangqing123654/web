#
# TBuilder Config File 
#
# Title:程序权限管理
#
# Company:JavaHis
#
# Author:zhangy 2009.10.19
#
# version 1.0
#

<Type=TFrame>
UI.Title=程序权限控制
UI.MenuConfig=%ROOT%\config\sys\SYSPopedomMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sys.SYSPopedomControl
UI.item=tPanel_10;tPanel_12
UI.layout=null
UI.TopMenu=Y
UI.TopToolBar=Y
UI.FocusList=PRG_ID;PRG_NAME;PRG_PY1;PRG_PY2;PRG_SEQ;PRG_DESCRIPTION;PRG_STATE;PRG_DATA;POPEDEM_ID;POPEDEM_NAME;POPEDEM_SEQ;POPEDEM_DESCRIPTION
tPanel_12.Type=TPanel
tPanel_12.X=307
tPanel_12.Y=13
tPanel_12.Width=717
tPanel_12.Height=727
tPanel_12.AutoY=N
tPanel_12.AutoWidth=Y
tPanel_12.AutoHeight=N
tPanel_12.Border=组
tPanel_12.Item=tPanel_4;tPanel_5;tPanel_6;tPanel_7
tPanel_12.AutoX=N
tPanel_12.AutoSize=0
tPanel_7.Type=TPanel
tPanel_7.X=6
tPanel_7.Y=411
tPanel_7.Width=705
tPanel_7.Height=310
tPanel_7.AutoX=Y
tPanel_7.AutoWidth=Y
tPanel_7.AutoHeight=Y
tPanel_7.AutoSize=0
tPanel_7.Border=凹
tPanel_7.Item=TABLE_D
TABLE_D.Type=TTable
TABLE_D.X=15
TABLE_D.Y=2
TABLE_D.Width=701
TABLE_D.Height=306
TABLE_D.SpacingRow=1
TABLE_D.RowHeight=20
TABLE_D.AutoX=Y
TABLE_D.AutoY=Y
TABLE_D.AutoWidth=Y
TABLE_D.AutoHeight=Y
TABLE_D.AutoSize=0
TABLE_D.Header=编号,100;名称,100;顺序号,100;备注,200
TABLE_D.LockColumns=all
TABLE_D.Item=
TABLE_D.ColumnHorizontalAlignmentData=0,left;1,left;2,right;3,left
TABLE_D.ParmMap=ID;NAME;SEQ;DESCRIPTION
TABLE_D.ClickedAction=onTableDClicked
tPanel_6.Type=TPanel
tPanel_6.X=6
tPanel_6.Y=335
tPanel_6.Width=705
tPanel_6.Height=76
tPanel_6.Border=组|权限信息
tPanel_6.AutoX=Y
tPanel_6.AutoWidth=Y
tPanel_6.AutoSize=0
tPanel_6.Item=tLabel_32;POPEDEM_ID;tLabel_33;POPEDEM_NAME;tLabel_34;POPEDEM_SEQ;tLabel_35;POPEDEM_DESCRIPTION
POPEDEM_DESCRIPTION.Type=TTextField
POPEDEM_DESCRIPTION.X=85
POPEDEM_DESCRIPTION.Y=47
POPEDEM_DESCRIPTION.Width=310
POPEDEM_DESCRIPTION.Height=20
POPEDEM_DESCRIPTION.Text=
tLabel_35.Type=TLabel
tLabel_35.X=13
tLabel_35.Y=49
tLabel_35.Width=72
tLabel_35.Height=15
tLabel_35.Text=备注
POPEDEM_SEQ.Type=TNumberTextField
POPEDEM_SEQ.X=476
POPEDEM_SEQ.Y=21
POPEDEM_SEQ.Width=77
POPEDEM_SEQ.Height=20
POPEDEM_SEQ.Text=0
POPEDEM_SEQ.Format=#########0
tLabel_34.Type=TLabel
tLabel_34.X=421
tLabel_34.Y=23
tLabel_34.Width=54
tLabel_34.Height=15
tLabel_34.Text=顺序号
POPEDEM_NAME.Type=TTextField
POPEDEM_NAME.X=262
POPEDEM_NAME.Y=21
POPEDEM_NAME.Width=133
POPEDEM_NAME.Height=20
POPEDEM_NAME.Text=
tLabel_33.Type=TLabel
tLabel_33.X=190
tLabel_33.Y=23
tLabel_33.Width=72
tLabel_33.Height=15
tLabel_33.Text=权限名称
POPEDEM_ID.Type=TTextField
POPEDEM_ID.X=85
POPEDEM_ID.Y=21
POPEDEM_ID.Width=77
POPEDEM_ID.Height=20
POPEDEM_ID.Text=
tLabel_32.Type=TLabel
tLabel_32.X=13
tLabel_32.Y=23
tLabel_32.Width=72
tLabel_32.Height=15
tLabel_32.Text=权限编号
tPanel_5.Type=TPanel
tPanel_5.X=6
tPanel_5.Y=116
tPanel_5.Width=705
tPanel_5.Height=218
tPanel_5.AutoX=Y
tPanel_5.AutoWidth=Y
tPanel_5.Border=凹
tPanel_5.Item=TABLE_M
tPanel_5.AutoSize=0
TABLE_M.Type=TTable
TABLE_M.X=49
TABLE_M.Y=2
TABLE_M.Width=701
TABLE_M.Height=214
TABLE_M.SpacingRow=1
TABLE_M.RowHeight=20
TABLE_M.AutoX=Y
TABLE_M.AutoY=Y
TABLE_M.AutoWidth=Y
TABLE_M.AutoHeight=Y
TABLE_M.AutoSize=0
TABLE_M.Header=编号,80;名称,80;拼音码,80;助记码,80;顺序号,80;备注,150;传参,80;程序路径,200
TABLE_M.LockColumns=all
TABLE_M.ParmMap=ID;NAME;PY1;PY2;SEQ;DESCRIPTION;STATE;DATA
TABLE_M.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,right;5,left;6,left;7,left
TABLE_M.ClickedAction=onTableMClicked
tPanel_4.Type=TPanel
tPanel_4.X=6
tPanel_4.Y=6
tPanel_4.Width=705
tPanel_4.Height=110
tPanel_4.AutoX=Y
tPanel_4.AutoY=Y
tPanel_4.AutoWidth=Y
tPanel_4.Border=组|程序信息
tPanel_4.AutoSize=0
tPanel_4.Item=tLabel_24;PRG_ID;tLabel_25;PRG_NAME;tLabel_26;PRG_PY1;tLabel_27;PRG_PY2;tLabel_28;PRG_SEQ;tLabel_29;PRG_DESCRIPTION;tLabel_30;PRG_STATE;tLabel_31;PRG_DATA
PRG_DATA.Type=TTextField
PRG_DATA.X=262
PRG_DATA.Y=79
PRG_DATA.Width=392
PRG_DATA.Height=20
PRG_DATA.Text=
tLabel_31.Type=TLabel
tLabel_31.X=190
tLabel_31.Y=81
tLabel_31.Width=72
tLabel_31.Height=15
tLabel_31.Text=程序路径
PRG_STATE.Type=TTextField
PRG_STATE.X=85
PRG_STATE.Y=79
PRG_STATE.Width=77
PRG_STATE.Height=20
PRG_STATE.Text=
tLabel_30.Type=TLabel
tLabel_30.X=12
tLabel_30.Y=81
tLabel_30.Width=72
tLabel_30.Height=15
tLabel_30.Text=传参
PRG_DESCRIPTION.Type=TTextField
PRG_DESCRIPTION.X=262
PRG_DESCRIPTION.Y=51
PRG_DESCRIPTION.Width=392
PRG_DESCRIPTION.Height=20
PRG_DESCRIPTION.Text=
tLabel_29.Type=TLabel
tLabel_29.X=190
tLabel_29.Y=54
tLabel_29.Width=72
tLabel_29.Height=15
tLabel_29.Text=备注
PRG_SEQ.Type=TNumberTextField
PRG_SEQ.X=85
PRG_SEQ.Y=51
PRG_SEQ.Width=77
PRG_SEQ.Height=20
PRG_SEQ.Text=0
PRG_SEQ.Format=#########0
tLabel_28.Type=TLabel
tLabel_28.X=12
tLabel_28.Y=54
tLabel_28.Width=72
tLabel_28.Height=15
tLabel_28.Text=顺序号
PRG_PY2.Type=TTextField
PRG_PY2.X=576
PRG_PY2.Y=23
PRG_PY2.Width=77
PRG_PY2.Height=20
PRG_PY2.Text=
tLabel_27.Type=TLabel
tLabel_27.X=519
tLabel_27.Y=26
tLabel_27.Width=56
tLabel_27.Height=15
tLabel_27.Text=助记码
PRG_PY1.Type=TTextField
PRG_PY1.X=420
PRG_PY1.Y=23
PRG_PY1.Width=77
PRG_PY1.Height=20
PRG_PY1.Text=
tLabel_26.Type=TLabel
tLabel_26.X=365
tLabel_26.Y=26
tLabel_26.Width=56
tLabel_26.Height=15
tLabel_26.Text=拼音码
PRG_NAME.Type=TTextField
PRG_NAME.X=262
PRG_NAME.Y=23
PRG_NAME.Width=77
PRG_NAME.Height=20
PRG_NAME.Text=
PRG_NAME.Action=onNameAction
tLabel_25.Type=TLabel
tLabel_25.X=190
tLabel_25.Y=26
tLabel_25.Width=72
tLabel_25.Height=15
tLabel_25.Text=程序名称
PRG_ID.Type=TTextField
PRG_ID.X=85
PRG_ID.Y=23
PRG_ID.Width=77
PRG_ID.Height=20
PRG_ID.Text=
tLabel_24.Type=TLabel
tLabel_24.X=13
tLabel_24.Y=26
tLabel_24.Width=72
tLabel_24.Height=15
tLabel_24.Text=程序编号
tPanel_10.Type=TPanel
tPanel_10.X=5
tPanel_10.Y=5
tPanel_10.Width=300
tPanel_10.Height=738
tPanel_10.AutoX=Y
tPanel_10.AutoY=Y
tPanel_10.AutoHeight=Y
tPanel_10.Border=组|系统架构图
tPanel_10.Item=tPanel_11
tPanel_10.MenuConfig=
tPanel_10.TopMenu=N
tPanel_10.TopToolBar=N
tPanel_11.Type=TPanel
tPanel_11.X=6
tPanel_11.Y=19
tPanel_11.Width=288
tPanel_11.Height=713
tPanel_11.AutoX=Y
tPanel_11.AutoY=Y
tPanel_11.AutoWidth=Y
tPanel_11.AutoHeight=Y
tPanel_11.AutoSize=0
tPanel_11.Border=凹
tPanel_11.Item=TREE
TREE.Type=TTree
TREE.X=22
TREE.Y=21
TREE.Width=284
TREE.Height=81
TREE.SpacingRow=1
TREE.RowHeight=20
TREE.AutoX=Y
TREE.AutoY=Y
TREE.AutoWidth=Y
TREE.AutoHeight=Y
TREE.AutoSize=0
TREE.Pics=ROOT:dir1.gif;PATH:dir1.gif;TABLE:table.gif;ROLE:role.gif;SYS:sys.gif;PRG:prg.gif