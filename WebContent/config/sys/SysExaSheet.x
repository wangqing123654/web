#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:ehui 2009.05.11
#
# version 1.0
#

<Type=TFrame>
UI.Title=���ñ�
UI.MenuConfig=
UI.Width=600
UI.Height=500
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sys.SysFeeExaSheetQuote
UI.item=tRootPanel_1
UI.layout=null
TABLE2.Type=TTable
TABLE2.X=373
TABLE2.Y=20
TABLE2.Width=206
TABLE2.Height=430
TABLE2.SpacingRow=1
TABLE2.RowHeight=20
TABLE2.Header=ѡ,30,boolean;����,170
TABLE2.ParmMap=EXEC;ORDER_DESC
TABLE2.AutoHeight=N
TABLE2.LockColumns=0,1
TABLE2.ColumnHorizontalAlignmentData=1,left
TABLE2.ClickedAction=onFetchBack|TABLE2
TABLE1.Type=TTable
TABLE1.X=160
TABLE1.Y=20
TABLE1.Width=214
TABLE1.Height=430
TABLE1.SpacingRow=1
TABLE1.RowHeight=20
TABLE1.Header=ѡ,30,boolean;����,180
TABLE1.ParmMap=EXEC;ORDER_DESC
TABLE1.AutoHeight=N
TABLE1.ColumnHorizontalAlignmentData=1,left
TABLE1.LockColumns=0,1
TABLE1.ClickedAction=onFetchBack|TABLE1
MENU.Type=TTable
MENU.X=5
MENU.Y=20
MENU.Width=156
MENU.Height=430
MENU.SpacingRow=1
MENU.RowHeight=20
MENU.Header=����,150
MENU.ParmMap=CATEGORY_CHN_DESC
MENU.AutoHeight=N
MENU.LockColumns=0
MENU.ClickedAction=onClick
MENU.ColumnHorizontalAlignmentData=0,left;1,left

tRootPanel_1.Type=TRootPanel
tRootPanel_1.X=0
tRootPanel_1.Y=0
tRootPanel_1.Width=600
tRootPanel_1.Height=500
tRootPanel_1.item=MENU;TABLE1;TABLE2;tButton_27
tRootPanel_1.AutoX=Y
tRootPanel_1.AutoY=Y
tRootPanel_1.AutoWidth=Y
tRootPanel_1.AutoHeight=Y
tRootPanel_1.AutoSize=0
tRootPanel_1.Title=���ñ�
tRootPanel_1.ShowTitle=N

tButton_27.Type=TButton
tButton_27.X=5
tButton_27.Y=455
tButton_27.Width=81
tButton_27.Height=23
tButton_27.Text=�ر�
tButton_27.Action=onClose