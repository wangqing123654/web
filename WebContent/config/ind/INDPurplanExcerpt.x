#
# TBuilder Config File 
#
# Title:采购计划
#
# Company:JavaHis
#
# Author:zhangy 2009.05.05
#
# version 1.0
#

<Type=TFrame>
UI.Title=引用
UI.MenuConfig=%ROOT%\config\ind\INDPurplanExcerptMenu.x
UI.Width=859
UI.Height=600
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.ind.INDPurplanExcerptControl
UI.item=tPanel_3;TABLE
UI.layout=null
UI.Name=
UI.Text=引用
UI.Tip=引用
UI.TopToolBar=Y
UI.TopMenu=Y
UI.ShowTitle=N
UI.ShowMenu=N
TABLE.Type=TTable
TABLE.X=0
TABLE.Y=141
TABLE.Width=849
TABLE.Height=309
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoX=Y
TABLE.AutoY=N
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.AutoSize=5
TABLE.Header=选,30,boolean;药品名称,170;规格,80;单位,40,UNIT;上期采购量,80,double,#####0.000;上期销售量,80,double,#####0.000;主库库存量,80,double,#####0.000;中库库存量,80,double,#####0.000;库存量,60,double,#####0.000;计划生成量,80,double,#####0.000;采购单价,65,double,#####0.0000;计划采购金额,120,double,#####0.00;供货商,160,SUP_CODE_2;生产厂商,200,MAN_CODE;安全库存量,100,double,#####0.000;最高存量,100,double,#####0.000;在途量,80,double,#####0.000
TABLE.ParmMap=SELECT_FLG;ORDER_DESC;SPECIFICATION;PURCH_UNIT;BUY_QTY;SELL_QTY;MAIN_QTY;MIDD_QTY;STOCK_QTY;E_QTY;CONTRACT_PRICE;TOT_MONEY;SUP_CODE;MAN_CODE;SAFE_QTY;MAX_QTY;BUY_UNRECEIVE_QTY
TABLE.LockColumns=1,2,3,4,5,6,7,8,11,12,13,14,15,16
TABLE.ColumnHorizontalAlignmentData=1,left;2,left;3,left;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,left;13,left;14,right;15,right;16,right
TABLE.ChangeAction=
TABLE.AutoModifyDataStore=N
TABLE.Item=SUP_CODE_2;UNIT;MAN_CODE
tPanel_3.Type=TPanel
tPanel_3.X=5
tPanel_3.Y=5
tPanel_3.Width=849
tPanel_3.Height=132
tPanel_3.Border=凸
tPanel_3.AutoX=Y
tPanel_3.AutoY=Y
tPanel_3.AutoWidth=Y
tPanel_3.Item=tPanel_8
tPanel_3.AutoHeight=N
tPanel_3.AutoW=N
tPanel_3.AutoH=N
tPanel_8.Type=TPanel
tPanel_8.X=322
tPanel_8.Y=7
tPanel_8.Width=835
tPanel_8.Height=118
tPanel_8.Border=组
tPanel_8.AutoX=Y
tPanel_8.AutoY=Y
tPanel_8.AutoW=N
tPanel_8.AutoWidth=Y
tPanel_8.Item=tLabel_11;GROUP_ORDER;tLabel_12;tLabel_13;CHECK_SAFE;CHECK_ORDER;ORDER_CODE;ORDER_DESC;tLabel_14;GROUP_SUP;tLabel_22;START_DATE;tLabel_23;END_DATE;SELECT_ALL;tLabel_24;ORDER_TYPE;tLabel_25;MATERIAL_LOC_CODE;tLabel_26;SUM_MONEY;UNIT;SUP_CODE;SUP_CODE_2;MAN_CODE
tPanel_8.AutoHeight=Y
MAN_CODE.Type=生产厂商
MAN_CODE.X=484
MAN_CODE.Y=131
MAN_CODE.Width=81
MAN_CODE.Height=23
MAN_CODE.Text=TButton
MAN_CODE.showID=Y
MAN_CODE.showName=Y
MAN_CODE.showText=N
MAN_CODE.showValue=N
MAN_CODE.showPy1=N
MAN_CODE.showPy2=N
MAN_CODE.Editable=Y
MAN_CODE.Tip=生产厂商
MAN_CODE.TableShowList=name
MAN_CODE.ModuleParmString=
MAN_CODE.ModuleParmTag=
SUP_CODE_2.Type=供应厂商下拉列表
SUP_CODE_2.X=416
SUP_CODE_2.Y=133
SUP_CODE_2.Width=81
SUP_CODE_2.Height=23
SUP_CODE_2.Text=TButton
SUP_CODE_2.showID=Y
SUP_CODE_2.showName=Y
SUP_CODE_2.showText=N
SUP_CODE_2.showValue=N
SUP_CODE_2.showPy1=N
SUP_CODE_2.showPy2=N
SUP_CODE_2.Editable=Y
SUP_CODE_2.Tip=供应厂商
SUP_CODE_2.TableShowList=name
SUP_CODE_2.ModuleParmString=
SUP_CODE_2.ModuleParmTag=
SUP_CODE.Type=供应厂商
SUP_CODE.X=222
SUP_CODE.Y=59
SUP_CODE.Width=187
SUP_CODE.Height=23
SUP_CODE.Text=
SUP_CODE.HorizontalAlignment=2
SUP_CODE.PopupMenuHeader=ID,100;NAME,100
SUP_CODE.PopupMenuWidth=300
SUP_CODE.PopupMenuHeight=300
SUP_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
SUP_CODE.FormatType=combo
SUP_CODE.ShowDownButton=Y
SUP_CODE.Tip=供应厂商
SUP_CODE.ShowColumnList=NAME
SUP_CODE.Enabled=N
SUP_CODE.ValueColumn=ID
SUP_CODE.HisOneNullRow=Y
SUP_CODE.PhaFlg=Y
UNIT.Type=计量单位下拉列表
UNIT.X=107
UNIT.Y=139
UNIT.Width=10
UNIT.Height=23
UNIT.Text=TButton
UNIT.showID=Y
UNIT.showName=Y
UNIT.showText=N
UNIT.showValue=N
UNIT.showPy1=N
UNIT.showPy2=N
UNIT.Editable=Y
UNIT.Tip=计量单位
UNIT.TableShowList=name
SUM_MONEY.Type=TNumberTextField
SUM_MONEY.X=615
SUM_MONEY.Y=91
SUM_MONEY.Width=100
SUM_MONEY.Height=20
SUM_MONEY.Text=0
SUM_MONEY.Format=#########0.00
SUM_MONEY.Enabled=N
tLabel_26.Type=TLabel
tLabel_26.X=539
tLabel_26.Y=94
tLabel_26.Width=72
tLabel_26.Height=15
tLabel_26.Text=金额合计:
MATERIAL_LOC_CODE.Type=料位
MATERIAL_LOC_CODE.X=407
MATERIAL_LOC_CODE.Y=89
MATERIAL_LOC_CODE.Width=100
MATERIAL_LOC_CODE.Height=23
MATERIAL_LOC_CODE.Text=TButton
MATERIAL_LOC_CODE.showID=Y
MATERIAL_LOC_CODE.showName=Y
MATERIAL_LOC_CODE.showText=N
MATERIAL_LOC_CODE.showValue=N
MATERIAL_LOC_CODE.showPy1=N
MATERIAL_LOC_CODE.showPy2=N
MATERIAL_LOC_CODE.Editable=Y
MATERIAL_LOC_CODE.Tip=料位
MATERIAL_LOC_CODE.TableShowList=name
MATERIAL_LOC_CODE.ModuleParmString=
MATERIAL_LOC_CODE.ModuleParmTag=
MATERIAL_LOC_CODE.ExpandWidth=30
tLabel_25.Type=TLabel
tLabel_25.X=357
tLabel_25.Y=94
tLabel_25.Width=40
tLabel_25.Height=15
tLabel_25.Text=料位:
tLabel_25.Color=blue
ORDER_TYPE.Type=药品分类下拉列表
ORDER_TYPE.X=222
ORDER_TYPE.Y=89
ORDER_TYPE.Width=100
ORDER_TYPE.Height=23
ORDER_TYPE.Text=TButton
ORDER_TYPE.showID=Y
ORDER_TYPE.showName=Y
ORDER_TYPE.showText=N
ORDER_TYPE.showValue=N
ORDER_TYPE.showPy1=N
ORDER_TYPE.showPy2=N
ORDER_TYPE.Editable=Y
ORDER_TYPE.Tip=药品分类
ORDER_TYPE.TableShowList=name
ORDER_TYPE.ModuleParmString=GROUP_ID:SYS_PHATYPE
ORDER_TYPE.ModuleParmTag=
tLabel_24.Type=TLabel
tLabel_24.X=144
tLabel_24.Y=94
tLabel_24.Width=72
tLabel_24.Height=15
tLabel_24.Text=药品分类:
tLabel_24.Color=blue
SELECT_ALL.Type=TCheckBox
SELECT_ALL.X=14
SELECT_ALL.Y=90
SELECT_ALL.Width=81
SELECT_ALL.Height=23
SELECT_ALL.Text=全选
SELECT_ALL.Action=onCheckSelectAll
END_DATE.Type=TTextFormat
END_DATE.X=292
END_DATE.Y=6
END_DATE.Width=160
END_DATE.Height=20
END_DATE.Text=TTextField
END_DATE.showDownButton=Y
END_DATE.Format=yyyy/MM/dd HH:mm:ss
END_DATE.FormatType=date
END_DATE.HorizontalAlignment=2
tLabel_23.Type=TLabel
tLabel_23.X=258
tLabel_23.Y=9
tLabel_23.Width=30
tLabel_23.Height=15
tLabel_23.Text=～
tLabel_23.VerticalAlignment=0
tLabel_23.HorizontalAlignment=0
START_DATE.Type=TTextFormat
START_DATE.X=94
START_DATE.Y=6
START_DATE.Width=160
START_DATE.Height=20
START_DATE.Text=
START_DATE.HorizontalAlignment=2
START_DATE.showDownButton=Y
START_DATE.Format=yyyy/MM/dd HH:mm:ss
START_DATE.FormatType=date
tLabel_22.Type=TLabel
tLabel_22.X=18
tLabel_22.Y=9
tLabel_22.Width=72
tLabel_22.Height=15
tLabel_22.Text=统计区间:
tLabel_22.Color=blue
GROUP_SUP.Type=TRadioButton
GROUP_SUP.X=14
GROUP_SUP.Y=59
GROUP_SUP.Width=20
GROUP_SUP.Height=23
GROUP_SUP.Text=
GROUP_SUP.Group=group1
GROUP_SUP.Action=onRadioButtonChange
tLabel_14.Type=TLabel
tLabel_14.X=38
tLabel_14.Y=63
tLabel_14.Width=180
tLabel_14.Height=15
tLabel_14.Text=依照指定供应厂商采购查询
tLabel_14.Color=blue
ORDER_DESC.Type=TTextField
ORDER_DESC.X=502
ORDER_DESC.Y=32
ORDER_DESC.Width=170
ORDER_DESC.Height=20
ORDER_DESC.Text=
ORDER_DESC.Enabled=N
ORDER_CODE.Type=TTextField
ORDER_CODE.X=388
ORDER_CODE.Y=32
ORDER_CODE.Width=111
ORDER_CODE.Height=20
ORDER_CODE.Text=
ORDER_CODE.Enabled=N
CHECK_ORDER.Type=TCheckBox
CHECK_ORDER.X=289
CHECK_ORDER.Y=30
CHECK_ORDER.Width=20
CHECK_ORDER.Height=23
CHECK_ORDER.Text=
CHECK_ORDER.Action=onCheckOrderSelect
CHECK_SAFE.Type=TCheckBox
CHECK_SAFE.X=165
CHECK_SAFE.Y=30
CHECK_SAFE.Width=20
CHECK_SAFE.Height=23
CHECK_SAFE.Text=
tLabel_13.Type=TLabel
tLabel_13.X=317
tLabel_13.Y=35
tLabel_13.Width=72
tLabel_13.Height=15
tLabel_13.Text=指定药品
tLabel_13.Color=blue
tLabel_12.Type=TLabel
tLabel_12.X=192
tLabel_12.Y=35
tLabel_12.Width=100
tLabel_12.Height=15
tLabel_12.Text=安全库存量
tLabel_12.Color=blue
GROUP_ORDER.Type=TRadioButton
GROUP_ORDER.X=14
GROUP_ORDER.Y=30
GROUP_ORDER.Width=20
GROUP_ORDER.Height=23
GROUP_ORDER.Text=
GROUP_ORDER.Group=group1
GROUP_ORDER.Selected=Y
GROUP_ORDER.Action=onRadioButtonChange
tLabel_11.Type=TLabel
tLabel_11.X=38
tLabel_11.Y=35
tLabel_11.Width=100
tLabel_11.Height=15
tLabel_11.Text=依照药品查询
tLabel_11.Color=blue