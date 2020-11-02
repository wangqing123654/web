#
# TBuilder Config File 
#
# Title:盘盈亏统计表
#
# Company:JavaHis
#
# Author:zhangy 2009.10.15
#
# version 1.0
#

<Type=TFrame>
UI.Title=盘盈亏统计
UI.MenuConfig=%ROOT%\config\ind\INDQtyCheckQueryMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.ind.INDQtyCheckQueryControl
UI.item=tPanel_1;tPanel_2;tPanel_3
UI.layout=null
UI.TopMenu=Y
UI.TopToolBar=Y
tPanel_3.Type=TPanel
tPanel_3.X=5
tPanel_3.Y=573
tPanel_3.Width=1014
tPanel_3.Height=40
tPanel_3.AutoX=Y
tPanel_3.AutoWidth=Y
tPanel_3.Border=组
tPanel_3.Item=tLabel_13;SUM_COUNT;tLabel_14;tLabel_18;SUM_CHECK_AMT;SUM_CHECK_QTY
SUM_CHECK_QTY.Type=TNumberTextField
SUM_CHECK_QTY.X=223
SUM_CHECK_QTY.Y=9
SUM_CHECK_QTY.Width=100
SUM_CHECK_QTY.Height=20
SUM_CHECK_QTY.Text=0
SUM_CHECK_QTY.Format=#########0.000
SUM_CHECK_QTY.Enabled=N
SUM_CHECK_AMT.Type=TNumberTextField
SUM_CHECK_AMT.X=420
SUM_CHECK_AMT.Y=9
SUM_CHECK_AMT.Width=120
SUM_CHECK_AMT.Height=20
SUM_CHECK_AMT.Text=0
SUM_CHECK_AMT.Format=#########0.00
SUM_CHECK_AMT.Enabled=N
tLabel_18.Type=TLabel
tLabel_18.X=342
tLabel_18.Y=12
tLabel_18.Width=72
tLabel_18.Height=15
tLabel_18.Text=盈亏总金额
SUM_TOT.Type=TNumberTextField
SUM_TOT.X=244
SUM_TOT.Y=9
SUM_TOT.Width=100
SUM_TOT.Height=20
SUM_TOT.Text=0
SUM_TOT.Format=#########0.000
SUM_TOT.Enabled=N
tLabel_14.Type=TLabel
tLabel_14.X=176
tLabel_14.Y=12
tLabel_14.Width=47
tLabel_14.Height=15
tLabel_14.Text=盈亏数
SUM_COUNT.Type=TNumberTextField
SUM_COUNT.X=77
SUM_COUNT.Y=9
SUM_COUNT.Width=77
SUM_COUNT.Height=20
SUM_COUNT.Text=0
SUM_COUNT.Format=#########0
SUM_COUNT.Enabled=N
tLabel_13.Type=TLabel
tLabel_13.X=14
tLabel_13.Y=12
tLabel_13.Width=62
tLabel_13.Height=15
tLabel_13.Text=合计笔数
tPanel_2.Type=TPanel
tPanel_2.X=5
tPanel_2.Y=107
tPanel_2.Width=1014
tPanel_2.Height=462
tPanel_2.AutoX=Y
tPanel_2.AutoWidth=Y
tPanel_2.Border=凹
tPanel_2.Item=TABLE_M;TABLE_D
TABLE_D.Type=TTable
TABLE_D.X=171
TABLE_D.Y=62
TABLE_D.Width=81
TABLE_D.Height=81
TABLE_D.SpacingRow=1
TABLE_D.RowHeight=20
TABLE_D.AutoX=Y
TABLE_D.AutoY=Y
TABLE_D.AutoWidth=Y
TABLE_D.AutoHeight=Y
TABLE_D.AutoSize=0
TABLE_D.Visible=N
TABLE_D.Header=解冻日期,120;药品分类,80;药品名称,200;规格,120;单位,50;批号,120;效期,120;原库存,80,double,#####0.000;盘点量,80,double,#####0.000;盈亏数,80,double,#####0.000;零售价,80,double,#####0.0000;金额,120,double,#####0.00
TABLE_D.LockColumns=all
TABLE_D.ParmMap=UNFREEZE_DATE;TYPE_CODE;ORDER_DESC;SPECIFICATION;UNIT_CHN_DESC;BATCH_NO;VALID_DATE;STOCK_QTY;ACTUAL_CHECK_QTY;CHECK_QTY;OWN_PRICE;OWN_AMT
TABLE_D.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,right;8,right;9,right;10,right;11,right
TABLE_M.Type=TTable
TABLE_M.X=74
TABLE_M.Y=60
TABLE_M.Width=81
TABLE_M.Height=81
TABLE_M.SpacingRow=1
TABLE_M.RowHeight=20
TABLE_M.AutoX=Y
TABLE_M.AutoY=Y
TABLE_M.AutoWidth=Y
TABLE_M.AutoHeight=Y
TABLE_M.AutoSize=0
TABLE_M.Header=药品分类,80;药品名称,200;规格,120;单位,50;盈亏数,80,double,#####0.000;零售价,80,double,#####0.0000;金额,120,double,#####0.00
TABLE_M.Visible=Y
TABLE_M.LockColumns=all
TABLE_M.ParmMap=TYPE_CODE;ORDER_DESC;SPECIFICATION;UNIT_CHN_DESC;CHECK_QTY;OWN_PRICE;OWN_AMT
TABLE_M.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,right;5,right;6,right
tPanel_1.Type=TPanel
tPanel_1.X=5
tPanel_1.Y=5
tPanel_1.Width=1014
tPanel_1.Height=98
tPanel_1.AutoX=Y
tPanel_1.AutoWidth=Y
tPanel_1.AutoY=Y
tPanel_1.Border=组
tPanel_1.Item=tLabel_6;tLabel_7;tLabel_11;TYPE_CODE;ORG_CODE;tLabel_12;START_DATE;tLabel_15;END_DATE;tLabel_16;ORDER_CODE;ORDER_DESC;tLabel_19;tLabel_20;RadioButton3;RadioButton4;tLabel_0;BATCH_NO;OWN_PRICE_A;tLabel_1;OWN_PRICE_B;CHECK_QTY_A;tLabel_2;CHECK_QTY_B;tLabel_4;CHECK_AMT_A;tLabel_5;CHECK_AMT_B
CHECK_AMT_B.Type=TNumberTextField
CHECK_AMT_B.X=779
CHECK_AMT_B.Y=69
CHECK_AMT_B.Width=77
CHECK_AMT_B.Height=20
CHECK_AMT_B.Text=0
CHECK_AMT_B.Format=#########0.00
tLabel_5.Type=TLabel
tLabel_5.X=752
tLabel_5.Y=72
tLabel_5.Width=21
tLabel_5.Height=15
tLabel_5.Text=～
CHECK_AMT_A.Type=TNumberTextField
CHECK_AMT_A.X=667
CHECK_AMT_A.Y=69
CHECK_AMT_A.Width=77
CHECK_AMT_A.Height=20
CHECK_AMT_A.Text=0
CHECK_AMT_A.Format=#########0.00
tLabel_4.Type=TLabel
tLabel_4.X=610
tLabel_4.Y=72
tLabel_4.Width=54
tLabel_4.Height=15
tLabel_4.Text=盈亏额
tLabel_4.Color=blue
CHECK_QTY_B.Type=TNumberTextField
CHECK_QTY_B.X=481
CHECK_QTY_B.Y=69
CHECK_QTY_B.Width=77
CHECK_QTY_B.Height=20
CHECK_QTY_B.Text=0
CHECK_QTY_B.Format=#########0.000
tLabel_2.Type=TLabel
tLabel_2.X=458
tLabel_2.Y=71
tLabel_2.Width=21
tLabel_2.Height=15
tLabel_2.Text=～
CHECK_QTY_A.Type=TNumberTextField
CHECK_QTY_A.X=375
CHECK_QTY_A.Y=69
CHECK_QTY_A.Width=77
CHECK_QTY_A.Height=20
CHECK_QTY_A.Text=0
CHECK_QTY_A.Format=#########0.000
OWN_PRICE_B.Type=TNumberTextField
OWN_PRICE_B.X=189
OWN_PRICE_B.Y=69
OWN_PRICE_B.Width=77
OWN_PRICE_B.Height=20
OWN_PRICE_B.Text=0
OWN_PRICE_B.Format=#########0.0000
tLabel_1.Type=TLabel
tLabel_1.X=167
tLabel_1.Y=71
tLabel_1.Width=20
tLabel_1.Height=15
tLabel_1.Text=～
OWN_PRICE_A.Type=TNumberTextField
OWN_PRICE_A.X=84
OWN_PRICE_A.Y=69
OWN_PRICE_A.Width=77
OWN_PRICE_A.Height=20
OWN_PRICE_A.Text=0
OWN_PRICE_A.Format=#########0.0000
BATCH_NO.Type=TTextField
BATCH_NO.X=667
BATCH_NO.Y=37
BATCH_NO.Width=120
BATCH_NO.Height=20
BATCH_NO.Text=
tLabel_0.Type=TLabel
tLabel_0.X=610
tLabel_0.Y=41
tLabel_0.Width=42
tLabel_0.Height=15
tLabel_0.Text=批号
tLabel_0.Color=blue
RadioButton4.Type=TRadioButton
RadioButton4.X=819
RadioButton4.Y=6
RadioButton4.Width=58
RadioButton4.Height=23
RadioButton4.Text=明细
RadioButton4.Group=
RadioButton4.Name=group2
RadioButton4.Action=onSelectTypeAction
RadioButton3.Type=TRadioButton
RadioButton3.X=760
RadioButton3.Y=6
RadioButton3.Width=55
RadioButton3.Height=23
RadioButton3.Text=汇总
RadioButton3.Group=
RadioButton3.Selected=Y
RadioButton3.Name=group2
RadioButton3.Action=onSelectTypeAction
tLabel_20.Type=TLabel
tLabel_20.X=684
tLabel_20.Y=10
tLabel_20.Width=72
tLabel_20.Height=15
tLabel_20.Text=查询类别
tLabel_19.Type=TLabel
tLabel_19.X=308
tLabel_19.Y=72
tLabel_19.Width=58
tLabel_19.Height=15
tLabel_19.Text=盈亏数
tLabel_19.Color=blue
ORDER_DESC.Type=TTextField
ORDER_DESC.X=432
ORDER_DESC.Y=37
ORDER_DESC.Width=150
ORDER_DESC.Height=20
ORDER_DESC.Text=
ORDER_DESC.Enabled=N
ORDER_CODE.Type=TTextField
ORDER_CODE.X=306
ORDER_CODE.Y=37
ORDER_CODE.Width=120
ORDER_CODE.Height=20
ORDER_CODE.Text=
tLabel_16.Type=TLabel
tLabel_16.X=235
tLabel_16.Y=41
tLabel_16.Width=72
tLabel_16.Height=15
tLabel_16.Text=药品代码
tLabel_16.Color=blue
END_DATE.Type=TTextFormat
END_DATE.X=491
END_DATE.Y=7
END_DATE.Width=160
END_DATE.Height=20
END_DATE.Text=
END_DATE.Color=黑
END_DATE.showDownButton=Y
END_DATE.FormatType=date
END_DATE.Format=yyyy/MM/dd HH:mm:ss
tLabel_15.Type=TLabel
tLabel_15.X=472
tLabel_15.Y=11
tLabel_15.Width=21
tLabel_15.Height=15
tLabel_15.Text=～
START_DATE.Type=TTextFormat
START_DATE.X=306
START_DATE.Y=7
START_DATE.Width=160
START_DATE.Height=20
START_DATE.Text=
START_DATE.showDownButton=Y
START_DATE.FormatType=date
START_DATE.Format=yyyy/MM/dd HH:mm:ss
tLabel_12.Type=TLabel
tLabel_12.X=235
tLabel_12.Y=11
tLabel_12.Width=72
tLabel_12.Height=15
tLabel_12.Text=统计区间
tLabel_12.Color=blue
ORG_CODE.Type=药房下拉列表
ORG_CODE.X=84
ORG_CODE.Y=7
ORG_CODE.Width=120
ORG_CODE.Height=23
ORG_CODE.Text=TButton
ORG_CODE.showID=Y
ORG_CODE.showName=Y
ORG_CODE.showText=N
ORG_CODE.showValue=N
ORG_CODE.showPy1=N
ORG_CODE.showPy2=N
ORG_CODE.Editable=Y
ORG_CODE.Tip=药房
ORG_CODE.TableShowList=name
ORG_CODE.ModuleParmTag=
ORG_CODE.ExpandWidth=30
ORG_CODE.OrgFlg=
ORG_CODE.OrgType=
TYPE_CODE.Type=药品分类下拉列表
TYPE_CODE.X=84
TYPE_CODE.Y=37
TYPE_CODE.Width=120
TYPE_CODE.Height=23
TYPE_CODE.Text=TButton
TYPE_CODE.showID=Y
TYPE_CODE.showName=Y
TYPE_CODE.showText=N
TYPE_CODE.showValue=N
TYPE_CODE.showPy1=N
TYPE_CODE.showPy2=N
TYPE_CODE.Editable=Y
TYPE_CODE.Tip=药品分类
TYPE_CODE.TableShowList=name
TYPE_CODE.ModuleParmString=GROUP_ID:SYS_PHATYPE
TYPE_CODE.ModuleParmTag=
tLabel_11.Type=TLabel
tLabel_11.X=13
tLabel_11.Y=72
tLabel_11.Width=55
tLabel_11.Height=15
tLabel_11.Text=零售价
tLabel_11.Color=blue
tLabel_7.Type=TLabel
tLabel_7.X=13
tLabel_7.Y=41
tLabel_7.Width=72
tLabel_7.Height=15
tLabel_7.Text=药品分类
tLabel_7.Color=blue
tLabel_6.Type=TLabel
tLabel_6.X=13
tLabel_6.Y=11
tLabel_6.Width=72
tLabel_6.Height=15
tLabel_6.Text=统计部门
tLabel_6.Color=blue