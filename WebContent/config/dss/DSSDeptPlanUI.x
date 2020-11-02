#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:sundx 20091019
#
# version 1.0
#

<Type=TFrame>
UI.Title=科室绩效
UI.MenuConfig=%ROOT%\config\dss\DSSDeptPlanMenu.x
UI.Width=1290
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.dss.DSSDeptPlanControl
UI.item=tPanel_0;tPanel_1;tMovePane_0
UI.layout=null
UI.Text=科室评估方案
UI.X=10
UI.Y=10
PLAN_C.Type=评估方案
PLAN_C.X=203
PLAN_C.Y=321
PLAN_C.Width=81
PLAN_C.Height=23
PLAN_C.Text=TButton
PLAN_C.showID=Y
PLAN_C.showName=Y
PLAN_C.showText=N
PLAN_C.showValue=N
PLAN_C.showPy1=N
PLAN_C.showPy2=N
PLAN_C.Editable=Y
PLAN_C.Tip=评估方案
PLAN_C.TableShowList=name
PLAN_C.ModuleParmTag=
KPI.Type=KPI指标
KPI.X=198
KPI.Y=182
KPI.Width=81
KPI.Height=23
KPI.Text=TButton
KPI.showID=Y
KPI.showName=Y
KPI.showText=N
KPI.showValue=N
KPI.showPy1=N
KPI.showPy2=N
KPI.Editable=Y
KPI.Tip=KPI指标
KPI.TableShowList=name
KPI.ModuleParmTag=
KPI_ATTRIBUTE.Type=TComboBox
KPI_ATTRIBUTE.X=208
KPI_ATTRIBUTE.Y=149
KPI_ATTRIBUTE.Width=81
KPI_ATTRIBUTE.Height=23
KPI_ATTRIBUTE.Text=TButton
KPI_ATTRIBUTE.showID=Y
KPI_ATTRIBUTE.Editable=Y
KPI_ATTRIBUTE.StringData=[[id,text],["",""],[0,取平均值],[1,累加]]
KPI_ATTRIBUTE.TableShowList=text
DEPT_D.Type=科室下拉列表
DEPT_D.X=210
DEPT_D.Y=274
DEPT_D.Width=81
DEPT_D.Height=23
DEPT_D.Text=TButton
DEPT_D.showID=Y
DEPT_D.showName=Y
DEPT_D.showText=N
DEPT_D.showValue=N
DEPT_D.showPy1=Y
DEPT_D.showPy2=Y
DEPT_D.Editable=Y
DEPT_D.Tip=科室
DEPT_D.TableShowList=name
DEPT_M.Type=科室下拉列表
DEPT_M.X=214
DEPT_M.Y=164
DEPT_M.Width=81
DEPT_M.Height=23
DEPT_M.Text=TButton
DEPT_M.showID=Y
DEPT_M.showName=Y
DEPT_M.showText=N
DEPT_M.showValue=N
DEPT_M.showPy1=Y
DEPT_M.showPy2=Y
DEPT_M.Editable=Y
DEPT_M.Tip=科室
DEPT_M.TableShowList=name
OPERATOR.Type=人员下拉列表
OPERATOR.X=230
OPERATOR.Y=276
OPERATOR.Width=81
OPERATOR.Height=23
OPERATOR.Text=TButton
OPERATOR.showID=Y
OPERATOR.showName=Y
OPERATOR.showText=N
OPERATOR.showValue=N
OPERATOR.showPy1=Y
OPERATOR.showPy2=Y
OPERATOR.Editable=Y
OPERATOR.Tip=人员
OPERATOR.TableShowList=name
OPERATOR.ModuleParmString=
OPERATOR.ModuleParmTag=
tMovePane_0.Type=TMovePane
tMovePane_0.X=322
tMovePane_0.Y=5
tMovePane_0.Width=7
tMovePane_0.Height=730
tMovePane_0.Text=TMovePane
tMovePane_0.MoveType=1
tMovePane_0.Border=凸
tMovePane_0.AutoHeight=Y
tMovePane_0.EntityData=tPanel_0,4;tPanel_1,3
tPanel_1.Type=TPanel
tPanel_1.X=335
tPanel_1.Y=6
tPanel_1.Width=944
tPanel_1.Height=737
tPanel_1.Border=组
tPanel_1.item=TABLE_M;TABLE_D;tLabel_1;tLabel_3;PLAN;DEPT
tPanel_1.AutoHeight=Y
DEPT.Type=科室
DEPT.X=80
DEPT.Y=13
DEPT.Width=136
DEPT.Height=23
DEPT.Text=
DEPT.HorizontalAlignment=2
DEPT.PopupMenuHeader=ID,100;NAME,100
DEPT.PopupMenuWidth=300
DEPT.PopupMenuHeight=300
DEPT.PopupMenuFilter=ID,1;NAME,1;PY1,1
DEPT.FormatType=combo
DEPT.ShowDownButton=Y
DEPT.Tip=科室
DEPT.ShowColumnList=NAME
PLAN.Type=评估方案
PLAN.X=283
PLAN.Y=14
PLAN.Width=118
PLAN.Height=23
PLAN.Text=TButton
PLAN.showID=Y
PLAN.showName=Y
PLAN.showText=N
PLAN.showValue=N
PLAN.showPy1=N
PLAN.showPy2=N
PLAN.Editable=Y
PLAN.Tip=评估方案
PLAN.TableShowList=name
PLAN.ModuleParmTag=
tLabel_3.Type=TLabel
tLabel_3.X=231
tLabel_3.Y=17
tLabel_3.Width=72
tLabel_3.Height=15
tLabel_3.Text=方案
tLabel_1.Type=TLabel
tLabel_1.X=26
tLabel_1.Y=19
tLabel_1.Width=44
tLabel_1.Height=15
tLabel_1.Text=科室
TABLE_D.Type=TTable
TABLE_D.X=7
TABLE_D.Y=328
TABLE_D.Width=925
TABLE_D.Height=326
TABLE_D.SpacingRow=1
TABLE_D.RowHeight=20
TABLE_D.Header=科室,100,DEPT_D;评估方案,100,PLAN_C;是否是最小科室,100,BOOLEAN;KPI代码,100,KPI;KPI一级指标类别,160,KPI;KPI二级指标类别,160,KPI;权重,100,double; 顺序号,100,int;备注,100;KPI 表达式,100;KPI 目标表达式,100;KPI 状态,100;KPI 属性,100,KPI_ATTRIBUTE;操作人员,100,OPERATOR;操作时间,100;操作端末,100
TABLE_D.ParmMap=DEPT_CODE;PLAN_CODE;LEAF;KPI_CODE;KPI_LEVEL1;KPI_LEVEL2;WEIGHT;SEQ;DESCRIPTION;KPI_VALUE;KPI_GOAL;KPI_STATUS;KPI_ATTRIBUTE;OPT_USER;OPT_DATE;OPT_TERM
TABLE_D.LockColumns=0,1,2,3,4,5,9,10,11,12,13,14,15
TABLE_D.Item=OPERATOR;DEPT_D;KPI_ATTRIBUTE;KPI;PLAN_C
TABLE_D.AutoWidth=Y
TABLE_D.AutoModifyDataStore=Y
TABLE_D.FocusType=2
TABLE_D.ColumnHorizontalAlignmentData=0,left;1,left;3,left;4,left;5,left;6,right;7,right;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left
TABLE_M.Type=TTable
TABLE_M.X=9
TABLE_M.Y=57
TABLE_M.Width=926
TABLE_M.Height=263
TABLE_M.SpacingRow=1
TABLE_M.RowHeight=20
TABLE_M.AutoWidth=Y
TABLE_M.Header=科室,100,DEPT_M;评估方案,100,PLAN_C;备注,100;序号,100;操作人员,100,OPERATOR;操作时间,100;操作端末,100
TABLE_M.LockRows=
TABLE_M.ParmMap=DEPT_CODE;PLAN_CODE;DESCRIPTION;SEQ;OPT_USER;OPT_DATE;OPT_TERM
TABLE_M.Item=DEPT_M;OPERATOR;PLAN_C
TABLE_M.LockColumns=4,5,6
TABLE_M.AutoModifyDataStore=Y
TABLE_M.FocusType=2
TABLE_M.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,right;4,left;5,left;6,left
tPanel_0.Type=TPanel
tPanel_0.X=5
tPanel_0.Y=8
tPanel_0.Width=306
tPanel_0.Height=735
tPanel_0.Border=组
tPanel_0.Item=TREE
tPanel_0.AutoX=Y
tPanel_0.AutoY=Y
tPanel_0.AutoHeight=Y
TREE.Type=TTree
TREE.X=8
TREE.Y=11
TREE.Width=284
TREE.Height=713
TREE.SpacingRow=1
TREE.RowHeight=20
TREE.AutoY=Y
TREE.AutoX=Y
TREE.AutoHeight=Y
TREE.AutoWidth=Y
TREE.pics=Root:sys.gif;Path:dir1.gif:dir1.gif;UI:refurbish.gif;Module:table.gif