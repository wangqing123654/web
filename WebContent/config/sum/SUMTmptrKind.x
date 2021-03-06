# 
#  Title:挂号方式UI
# 
#  Description:挂号方式UI
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
<Type=TFrame>
UI.Title=挂号方式窗口
UI.MenuConfig=%ROOT%\config\sum\SUMTmptrKindMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.sum.SUMTmptrKindControl
UI.item=tPanel_0;Table
UI.layout=null
UI.FocusList=TMPTRKINDCODE;TMPTRKINDDESC;PRESENTNOTATION
UI.TopMenu=Y
UI.TopToolBar=Y
UI.ShowTitle=Y
UI.ShowMenu=Y
Table.Type=TTable
Table.X=5
Table.Y=142
Table.Width=1014
Table.Height=545
Table.SpacingRow=1
Table.RowHeight=20
Table.AutoX=Y
Table.AutoWidth=Y
Table.AutoHeight=Y
Table.AutoSize=5
Table.LockColumns=
Table.StringData=string,string,string,string,string,string
Table.Header=体温种类代码,100;体温种类说明,100;呈现记号,70;操作人员,80;操作日期,120;操作终端,120
Table.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,right;5,left;6,right;10,left;11,right;12,right
Table.ParmMap=TMPTRKINDCODE;TMPTRKINDDESC;PRESENTNOTATION;OPT_USER;OPT_DATE;OPT_TERM
Table.LockRows=0,1,2,3,4,5
tPanel_0.Type=TPanel
tPanel_0.X=5
tPanel_0.Y=5
tPanel_0.Width=1016
tPanel_0.Height=133
tPanel_0.Item=tLabel_9;tLabel_11;TMPTRKINDCODE;TMPTRKINDDESC;tLabel_18;PRESENTNOTATION;tLabel_2
tPanel_0.Enabled=Y
tPanel_0.Border=凸
tPanel_0.AutoWidth=Y
tPanel_0.TopMenu=Y
tPanel_0.TopToolBar=Y
tPanel_0.AutoSize=3
tLabel_2.Type=TLabel
tLabel_2.X=262
tLabel_2.Y=26
tLabel_2.Width=72
tLabel_2.Height=15
tLabel_2.Text= *
tLabel_2.Color=red
PRESENTNOTATION.Type=TTextField
PRESENTNOTATION.X=111
PRESENTNOTATION.Y=89
PRESENTNOTATION.Width=154
PRESENTNOTATION.Height=20
PRESENTNOTATION.Text=
PRESENTNOTATION.InputLength=20
tLabel_18.Type=TLabel
tLabel_18.X=20
tLabel_18.Y=92
tLabel_18.Width=72
tLabel_18.Height=15
tLabel_18.Text=呈现记号:
TMPTRKINDDESC.Type=TTextField
TMPTRKINDDESC.X=111
TMPTRKINDDESC.Y=56
TMPTRKINDDESC.Width=236
TMPTRKINDDESC.Height=20
TMPTRKINDDESC.Text=
TMPTRKINDDESC.Tip=挂号方式说明
TMPTRKINDDESC.InputLength=10
TMPTRKINDDESC.Action=
TMPTRKINDDESC.HorizontalAlignment=2
TMPTRKINDCODE.Type=TTextField
TMPTRKINDCODE.X=111
TMPTRKINDCODE.Y=23
TMPTRKINDCODE.Width=127
TMPTRKINDCODE.Height=20
TMPTRKINDCODE.Text=
TMPTRKINDCODE.Tip=挂号方式
TMPTRKINDCODE.InputLength=0
tLabel_11.Type=TLabel
tLabel_11.X=20
tLabel_11.Y=26
tLabel_11.Width=91
tLabel_11.Height=15
tLabel_11.Text=体温种类代码:
tLabel_11.Color=蓝
tLabel_11.HorizontalAlignment=2
tLabel_9.Type=TLabel
tLabel_9.X=20
tLabel_9.Y=59
tLabel_9.Width=91
tLabel_9.Height=15
tLabel_9.Text=体温种类说明:
tLabel_9.HorizontalAlignment=2
tLabel_9.Color=黑