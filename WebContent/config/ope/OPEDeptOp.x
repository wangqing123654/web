#############################################
# <p>Title:科常用手术码 </p>
#
# <p>Description:科常用手术码 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.09.23
# @version 4.0
#############################################
<Type=TFrame>
UI.Title=科常用手术码
UI.MenuConfig=%ROOT%\config\ope\OPEDeptOpMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.ope.OPEDeptOpControl
UI.Item=tPanel_0;Table
UI.TopToolBar=Y
UI.ShowTitle=N
UI.ShowMenu=N
UI.TopMenu=Y
Table.Type=TTable
Table.X=86
Table.Y=90
Table.Width=81
Table.Height=653
Table.SpacingRow=1
Table.RowHeight=20
Table.AutoX=Y
Table.AutoWidth=Y
Table.AutoHeight=Y
Table.Header=科别,120,DEPT_CODE1;手术ICD,100;手术名称,300;序号,60;操作人员,80;操作日期,100;操作终端,100
Table.ParmMap=DEPT_CODE;OP_CODE;OPT_CHN_DESC;SEQ;OPT_USER;OPT_DATE;OPT_TERM
Table.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left
Table.LockRows=
Table.LockColumns=all
Table.Item=DEPT_CODE1
tPanel_0.Type=TPanel
tPanel_0.X=5
tPanel_0.Y=5
tPanel_0.Width=1014
tPanel_0.Height=80
tPanel_0.Border=凸
tPanel_0.AutoX=Y
tPanel_0.AutoY=Y
tPanel_0.AutoWidth=Y
tPanel_0.Item=tLabel_0;DEPT_CODE1;tLabel_1;OP_CODE;OPT_CHN_DESC;tLabel_2;SEQ;DEPT_CODE
DEPT_CODE.Type=科室
DEPT_CODE.X=123
DEPT_CODE.Y=17
DEPT_CODE.Width=120
DEPT_CODE.Height=22
DEPT_CODE.Text=
DEPT_CODE.HorizontalAlignment=2
DEPT_CODE.PopupMenuHeader=ID,100;NAME,100
DEPT_CODE.PopupMenuWidth=300
DEPT_CODE.PopupMenuHeight=300
DEPT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
DEPT_CODE.FormatType=combo
DEPT_CODE.ShowDownButton=Y
DEPT_CODE.Tip=科室
DEPT_CODE.ShowColumnList=NAME
DEPT_CODE.FinalFlg=Y
DEPT_CODE.ClassIfy=0
DEPT_CODE.HisOneNullRow=Y
SEQ.Type=TNumberTextField
SEQ.X=344
SEQ.Y=19
SEQ.Width=60
SEQ.Height=20
SEQ.Text=0
SEQ.Format=#########0
tLabel_2.Type=TLabel
tLabel_2.X=290
tLabel_2.Y=22
tLabel_2.Width=48
tLabel_2.Height=15
tLabel_2.Text=排序号
OPT_CHN_DESC.Type=TTextField
OPT_CHN_DESC.X=208
OPT_CHN_DESC.Y=51
OPT_CHN_DESC.Width=300
OPT_CHN_DESC.Height=20
OPT_CHN_DESC.Text=
OPT_CHN_DESC.Enabled=N
OP_CODE.Type=TTextField
OP_CODE.X=123
OP_CODE.Y=51
OP_CODE.Width=80
OP_CODE.Height=20
OP_CODE.Text=
tLabel_1.Type=TLabel
tLabel_1.X=49
tLabel_1.Y=54
tLabel_1.Width=59
tLabel_1.Height=15
tLabel_1.Text=手术ICD
tLabel_1.HorizontalAlignment=2
tLabel_1.Color=蓝
DEPT_CODE1.Type=科室下拉列表
DEPT_CODE1.X=443
DEPT_CODE1.Y=17
DEPT_CODE1.Width=120
DEPT_CODE1.Height=23
DEPT_CODE1.Text=TButton
DEPT_CODE1.showID=Y
DEPT_CODE1.showName=Y
DEPT_CODE1.showText=N
DEPT_CODE1.showValue=N
DEPT_CODE1.showPy1=Y
DEPT_CODE1.showPy2=Y
DEPT_CODE1.Editable=Y
DEPT_CODE1.Tip=科室
DEPT_CODE1.TableShowList=name
DEPT_CODE1.Visible=N
tLabel_0.Type=TLabel
tLabel_0.X=48
tLabel_0.Y=22
tLabel_0.Width=63
tLabel_0.Height=15
tLabel_0.Text=科    别
tLabel_0.Color=蓝