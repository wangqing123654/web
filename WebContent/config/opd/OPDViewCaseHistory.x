#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:ehui 2009.10.29
#
# version 1.0
#

<Type=TFrame>
UI.Title=就诊记录
UI.MenuConfig=%ROOT%\config\opd\OPDViewCaseHistoryMenu.x
UI.Width=1280
UI.Height=780
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.opd.OpdViewCaseHistoryControl
UI.item=tPanel_0
UI.layout=null
UI.TopMenu=Y
UI.TopToolBar=Y
UI.ShowMenu=Y
tPanel_0.Type=TPanel
tPanel_0.X=5
tPanel_0.Y=5
tPanel_0.Width=1270
tPanel_0.Height=770
tPanel_0.AutoX=Y
tPanel_0.AutoY=Y
tPanel_0.AutoWidth=Y
tPanel_0.AutoHeight=Y
tPanel_0.Border=组
tPanel_0.Item=tPanel_1;tPanel_2;tMovePane_0;tLabel_53;MR_NO;tLabel_54;PAT_NAME
PAT_NAME.Type=TTextField
PAT_NAME.X=238
PAT_NAME.Y=11
PAT_NAME.Width=110
PAT_NAME.Height=20
PAT_NAME.Text=
PAT_NAME.Enabled=N
tLabel_54.Type=TLabel
tLabel_54.X=205
tLabel_54.Y=15
tLabel_54.Width=29
tLabel_54.Height=15
tLabel_54.Text=姓名
tLabel_54.Color=黑
MR_NO.Type=TTextField
MR_NO.X=69
MR_NO.Y=12
MR_NO.Width=123
MR_NO.Height=20
MR_NO.Text=
MR_NO.ClickedAction=
MR_NO.Action=onMrNo
tLabel_53.Type=TLabel
tLabel_53.X=15
tLabel_53.Y=15
tLabel_53.Width=46
tLabel_53.Height=15
tLabel_53.Text=病案号
tLabel_53.Color=蓝
tMovePane_0.Type=TMovePane
tMovePane_0.X=275
tMovePane_0.Y=42
tMovePane_0.Width=12
tMovePane_0.Height=717
tMovePane_0.Text=
tMovePane_0.MoveType=1
tMovePane_0.CursorType=0
tMovePane_0.AutoHeight=Y
tMovePane_0.EntityData=tPanel_1,4;tPanel_2,3
tMovePane_0.Style=3
tPanel_2.Type=TPanel
tPanel_2.X=288
tPanel_2.Y=35
tPanel_2.Width=971
tPanel_2.Height=724
tPanel_2.Border=组|医嘱详细
tPanel_2.AutoHeight=Y
tPanel_2.AutoWidth=Y
tPanel_2.Item=SUBJ_TEXT;OBJ_TEXT;PHYSEXAM_REC;DIAG_TABLE;TTABBEDPANE;tLabel_56;tLabel_57;tLabel_58
tLabel_58.Type=TLabel
tLabel_58.X=18
tLabel_58.Y=206
tLabel_58.Width=32
tLabel_58.Height=15
tLabel_58.Text=体征
tLabel_57.Type=TLabel
tLabel_57.X=17
tLabel_57.Y=92
tLabel_57.Width=47
tLabel_57.Height=15
tLabel_57.Text=现病史
tLabel_56.Type=TLabel
tLabel_56.X=15
tLabel_56.Y=24
tLabel_56.Width=37
tLabel_56.Height=15
tLabel_56.Text=主诉
INS_PAY.Type=TComboBox
INS_PAY.X=549
INS_PAY.Y=16
INS_PAY.Width=81
INS_PAY.Height=23
INS_PAY.Text=TButton
INS_PAY.showID=Y
INS_PAY.Editable=Y
INS_PAY.StringData=[[id,name],[A,医保],[B,增付],[C,自费]]
INS_PAY.ShowName=Y
INS_PAY.ShowText=N
INS_PAY.TableShowList=name
DCTEXCEP_CODE.Type=特殊煎法下拉列表
DCTEXCEP_CODE.X=662
DCTEXCEP_CODE.Y=22
DCTEXCEP_CODE.Width=81
DCTEXCEP_CODE.Height=23
DCTEXCEP_CODE.Text=TButton
DCTEXCEP_CODE.showID=Y
DCTEXCEP_CODE.showName=Y
DCTEXCEP_CODE.showText=N
DCTEXCEP_CODE.showValue=N
DCTEXCEP_CODE.showPy1=Y
DCTEXCEP_CODE.showPy2=Y
DCTEXCEP_CODE.Editable=Y
DCTEXCEP_CODE.Tip=特殊煎法
DCTEXCEP_CODE.TableShowList=name
DCTEXCEP_CODE.ModuleParmString=GROUP_ID:PHA_DCTEXCEP
DCTEXCEP_CODE.ModuleParmTag=
ROUTE_CODE.Type=途径下拉列表
ROUTE_CODE.X=463
ROUTE_CODE.Y=26
ROUTE_CODE.Width=81
ROUTE_CODE.Height=23
ROUTE_CODE.Text=TButton
ROUTE_CODE.showID=Y
ROUTE_CODE.showName=Y
ROUTE_CODE.showText=N
ROUTE_CODE.showValue=N
ROUTE_CODE.showPy1=Y
ROUTE_CODE.showPy2=Y
ROUTE_CODE.Editable=Y
ROUTE_CODE.Tip=频次
ROUTE_CODE.TableShowList=name
DEPT.Type=药房
DEPT.X=655
DEPT.Y=275
DEPT.Width=81
DEPT.Height=23
DEPT.Text=
DEPT.HorizontalAlignment=2
DEPT.PopupMenuHeader=ID,100;NAME,100
DEPT.PopupMenuWidth=300
DEPT.PopupMenuHeight=300
DEPT.PopupMenuFilter=ID,1;NAME,1;PY1,1
DEPT.FormatType=combo
DEPT.ShowDownButton=Y
DEPT.Tip=药房
DEPT.ShowColumnList=NAME
OP_EXEC_DEPT.Type=科室(处置专用)下拉区域
OP_EXEC_DEPT.X=552
OP_EXEC_DEPT.Y=271
OP_EXEC_DEPT.Width=81
OP_EXEC_DEPT.Height=23
OP_EXEC_DEPT.Text=
OP_EXEC_DEPT.HorizontalAlignment=2
OP_EXEC_DEPT.PopupMenuHeader=ID,100;NAME,100
OP_EXEC_DEPT.PopupMenuWidth=300
OP_EXEC_DEPT.PopupMenuHeight=300
OP_EXEC_DEPT.PopupMenuFilter=ID,1;NAME,1;PY1,1
OP_EXEC_DEPT.FormatType=combo
OP_EXEC_DEPT.ShowDownButton=Y
OP_EXEC_DEPT.Tip=科室(处置专用)
OP_EXEC_DEPT.ShowColumnList=NAME
FREQ_CODE1.Type=频次
FREQ_CODE1.X=439
FREQ_CODE1.Y=273
FREQ_CODE1.Width=81
FREQ_CODE1.Height=23
FREQ_CODE1.Text=
FREQ_CODE1.HorizontalAlignment=2
FREQ_CODE1.PopupMenuHeader=ID,100;NAME,100
FREQ_CODE1.PopupMenuWidth=300
FREQ_CODE1.PopupMenuHeight=300
FREQ_CODE1.PopupMenuFilter=ID,1;NAME,1;PY1,1
FREQ_CODE1.FormatType=combo
FREQ_CODE1.ShowDownButton=Y
FREQ_CODE1.Tip=频次
FREQ_CODE1.ShowColumnList=NAME
INS_PAY1.Type=支付方式下拉列表
INS_PAY1.X=336
INS_PAY1.Y=274
INS_PAY1.Width=81
INS_PAY1.Height=23
INS_PAY1.Text=TButton
INS_PAY1.showID=Y
INS_PAY1.showName=Y
INS_PAY1.showText=N
INS_PAY1.showValue=N
INS_PAY1.showPy1=Y
INS_PAY1.showPy2=Y
INS_PAY1.Editable=Y
INS_PAY1.Tip=支付方式
INS_PAY1.TableShowList=name
INS_PAY1.ModuleParmString=GROUP_ID:SYS_PAYTYPE
INS_PAY1.ModuleParmTag=
OPTITEM_CODE.Type=检查项目
OPTITEM_CODE.X=228
OPTITEM_CODE.Y=275
OPTITEM_CODE.Width=81
OPTITEM_CODE.Height=23
OPTITEM_CODE.Text=
OPTITEM_CODE.HorizontalAlignment=2
OPTITEM_CODE.PopupMenuHeader=ID,100;NAME,100
OPTITEM_CODE.PopupMenuWidth=300
OPTITEM_CODE.PopupMenuHeight=300
OPTITEM_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
OPTITEM_CODE.FormatType=combo
OPTITEM_CODE.ShowDownButton=Y
OPTITEM_CODE.Tip=检查项目
OPTITEM_CODE.ShowColumnList=NAME
UNIT_CODE.Type=计量单位
UNIT_CODE.X=133
UNIT_CODE.Y=273
UNIT_CODE.Width=81
UNIT_CODE.Height=23
UNIT_CODE.Text=
UNIT_CODE.HorizontalAlignment=2
UNIT_CODE.PopupMenuHeader=ID,100;NAME,100
UNIT_CODE.PopupMenuWidth=300
UNIT_CODE.PopupMenuHeight=300
UNIT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
UNIT_CODE.FormatType=combo
UNIT_CODE.ShowDownButton=Y
UNIT_CODE.Tip=计量单位
UNIT_CODE.ShowColumnList=NAME
TECH_DEPT.Type=科室
TECH_DEPT.X=36
TECH_DEPT.Y=269
TECH_DEPT.Width=81
TECH_DEPT.Height=23
TECH_DEPT.Text=
TECH_DEPT.HorizontalAlignment=2
TECH_DEPT.PopupMenuHeader=ID,100;NAME,100
TECH_DEPT.PopupMenuWidth=300
TECH_DEPT.PopupMenuHeight=300
TECH_DEPT.PopupMenuFilter=ID,1;NAME,1;PY1,1
TECH_DEPT.FormatType=combo
TECH_DEPT.ShowDownButton=Y
TECH_DEPT.Tip=科室
TECH_DEPT.ShowColumnList=NAME
TTABBEDPANE.Type=TTabbedPane
TTABBEDPANE.X=10
TTABBEDPANE.Y=301
TTABBEDPANE.Width=950
TTABBEDPANE.Height=412
TTABBEDPANE.AutoWidth=Y
TTABBEDPANE.AutoHeight=Y
TTABBEDPANE.Item=tPanel_3;tPanel_4;tPanel_5;tPanel_6;tPanel_7
tPanel_7.Type=TPanel
tPanel_7.X=98
tPanel_7.Y=8
tPanel_7.Width=81
tPanel_7.Height=81
tPanel_7.Text=
tPanel_7.Name=管制药品
tPanel_7.Item=CTRL_TABLE
CTRL_TABLE.Type=TTable
CTRL_TABLE.X=7
CTRL_TABLE.Y=5
CTRL_TABLE.Width=933
CTRL_TABLE.Height=373
CTRL_TABLE.SpacingRow=1
CTRL_TABLE.RowHeight=20
CTRL_TABLE.AutoX=N
CTRL_TABLE.AutoY=N
CTRL_TABLE.AutoWidth=Y
CTRL_TABLE.AutoHeight=Y
CTRL_TABLE.Header=连,30,boolean;组,30;医嘱,200;用量,60,double,##0.00;单位,40,UNIT_CODE;频次,50,FREQ_CODE;用法,80,ROUTE_CODE;日份,50,int,#0;总量,60,double;备,30,boolean;盒,30,boolean;单位,60,UNIT_CODE;执行科室,80,DEPT;医生备注,200;护士备注,200;急作,40,boolean;给付类别,80,INS_PAY
CTRL_TABLE.LockColumns=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16
CTRL_TABLE.ColumnHorizontalAlignmentData=1,right;2,left;3,right;4,left;5,left;6,left;7,right;8,right;11,left;12,left;13,left;14,left;16,left
CTRL_TABLE.Item=UNIT_CODE;DEPT;FREQ_CODE;ROUTE_CODE;INS_PAY
CTRL_TABLE.ParmMap=LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;EXEC_DEPT_CODE;DR_NOTE;NS_NOTE;URGENT_FLG;INSPAY_TYPE
tPanel_6.Type=TPanel
tPanel_6.X=51
tPanel_6.Y=8
tPanel_6.Width=81
tPanel_6.Height=81
tPanel_6.Text=
tPanel_6.Name=草药
tPanel_6.Item=tLabel_9;TAKE_DAYS;tLabel_10;DCT_TAKE_QTY;tLabel_11;FREQ_CODE;tLabel_25;CHN_ROUTE_CODE;tLabel_26;DCTAGENT_CODE;CHN_TABLE;tLabel_0;RX_NO
RX_NO.Type=TComboBox
RX_NO.X=55
RX_NO.Y=8
RX_NO.Width=112
RX_NO.Height=23
RX_NO.Text=TButton
RX_NO.showID=N
RX_NO.Editable=Y
RX_NO.ParmMap=id:ID;name:NAME
RX_NO.TableShowList=name
RX_NO.ShowName=Y
RX_NO.ShowText=N
RX_NO.SelectedAction=onChangeRx
tLabel_0.Type=TLabel
tLabel_0.X=10
tLabel_0.Y=12
tLabel_0.Width=49
tLabel_0.Height=15
tLabel_0.Text=处方签

DCTAGENT_CODE.Type=煎药方式下拉列表
DCTAGENT_CODE.X=729
DCTAGENT_CODE.Y=8
DCTAGENT_CODE.Width=81
DCTAGENT_CODE.Height=23
DCTAGENT_CODE.Text=TButton
DCTAGENT_CODE.showID=N
DCTAGENT_CODE.showName=Y
DCTAGENT_CODE.showText=N
DCTAGENT_CODE.showValue=N
DCTAGENT_CODE.showPy1=Y
DCTAGENT_CODE.showPy2=Y
DCTAGENT_CODE.Editable=N
DCTAGENT_CODE.Tip=煎法
DCTAGENT_CODE.TableShowList=name
DCTAGENT_CODE.ModuleParmString=GROUP_ID:PHA_DCTAGENT
DCTAGENT_CODE.ModuleParmTag=
DCTAGENT_CODE.Enabled=N
tLabel_26.Type=TLabel
tLabel_26.X=668
tLabel_26.Y=12
tLabel_26.Width=59
tLabel_26.Height=15
tLabel_26.Text=煎药方式
CHN_ROUTE_CODE.Type=途径下拉列表
CHN_ROUTE_CODE.X=581
CHN_ROUTE_CODE.Y=8
CHN_ROUTE_CODE.Width=81
CHN_ROUTE_CODE.Height=23
CHN_ROUTE_CODE.Text=TButton
CHN_ROUTE_CODE.showID=N
CHN_ROUTE_CODE.showName=Y
CHN_ROUTE_CODE.showText=N
CHN_ROUTE_CODE.showValue=N
CHN_ROUTE_CODE.showPy1=Y
CHN_ROUTE_CODE.showPy2=Y
CHN_ROUTE_CODE.Editable=N
CHN_ROUTE_CODE.Tip=频次
CHN_ROUTE_CODE.TableShowList=name
CHN_ROUTE_CODE.WesmedFlg=N
CHN_ROUTE_CODE.Enabled=N
tLabel_25.Type=TLabel
tLabel_25.X=551
tLabel_25.Y=12
tLabel_25.Width=30
tLabel_25.Height=15
tLabel_25.Text=用法
FREQ_CODE.Type=频次
FREQ_CODE.X=461
FREQ_CODE.Y=8
FREQ_CODE.Width=87
FREQ_CODE.Height=23
FREQ_CODE.Text=
FREQ_CODE.HorizontalAlignment=2
FREQ_CODE.PopupMenuHeader=ID,100;NAME,100
FREQ_CODE.PopupMenuWidth=300
FREQ_CODE.PopupMenuHeight=300
FREQ_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1
FREQ_CODE.FormatType=combo
FREQ_CODE.ShowDownButton=Y
FREQ_CODE.Tip=频次
FREQ_CODE.ShowColumnList=NAME
FREQ_CODE.Enabled=N
tLabel_11.Type=TLabel
tLabel_11.X=431
tLabel_11.Y=12
tLabel_11.Width=32
tLabel_11.Height=15
tLabel_11.Text=频次
DCT_TAKE_QTY.Type=TTextField
DCT_TAKE_QTY.X=350
DCT_TAKE_QTY.Y=9
DCT_TAKE_QTY.Width=77
DCT_TAKE_QTY.Height=20
DCT_TAKE_QTY.Text=
DCT_TAKE_QTY.Enabled=N
tLabel_10.Type=TLabel
tLabel_10.X=290
tLabel_10.Y=12
tLabel_10.Width=57
tLabel_10.Height=15
tLabel_10.Text=饮片计量
TAKE_DAYS.Type=TNumberTextField
TAKE_DAYS.X=243
TAKE_DAYS.Y=9
TAKE_DAYS.Width=40
TAKE_DAYS.Height=20
TAKE_DAYS.Text=0
TAKE_DAYS.Format=##0
TAKE_DAYS.Enabled=N
tLabel_9.Type=TLabel
tLabel_9.X=171
tLabel_9.Y=12
tLabel_9.Width=66
tLabel_9.Height=15
tLabel_9.Text=付数/日份
tPanel_5.Type=TPanel
tPanel_5.X=59
tPanel_5.Y=12
tPanel_5.Width=81
tPanel_5.Height=81
tPanel_5.Text=
tPanel_5.Name=西、成药
tPanel_5.Item=MED_TABLE
MED_TABLE.Type=TTable
MED_TABLE.X=8
MED_TABLE.Y=5
MED_TABLE.Width=932
MED_TABLE.Height=373
MED_TABLE.SpacingRow=1
MED_TABLE.RowHeight=20
MED_TABLE.AutoX=N
MED_TABLE.AutoY=N
MED_TABLE.AutoWidth=Y
MED_TABLE.AutoHeight=Y
MED_TABLE.Header=连,30,boolean;组,30;医嘱,200;用量,60,double,##0.00;单位,40,UNIT_CODE;频次,50,FREQ_CODE;用法,80,ROUTE_CODE;日份,50,int,#0;总量,60,double;备,30,boolean;盒,30,boolean;单位,60,UNIT_CODE;执行科室,80,DEPT;医生备注,200;护士备注,200;急作,40,boolean;给付类别,80,INS_PAY
MED_TABLE.LockColumns=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16
MED_TABLE.ColumnHorizontalAlignmentData=1,right;2,left;3,right;4,left;5,left;6,left;7,right;8,right;11,left;12,left;13,left;14,left;16,left
MED_TABLE.ParmMap=LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;EXEC_DEPT_CODE;DR_NOTE;NS_NOTE;URGENT_FLG;INSPAY_TYPE
MED_TABLE.Item=UNIT_CODE;DEPT;FREQ_CODE;ROUTE_CODE;INS_PAY
MED_TABLE.FocusType=2
tPanel_4.Type=TPanel
tPanel_4.X=29
tPanel_4.Y=8
tPanel_4.Width=81
tPanel_4.Height=81
tPanel_4.Text=
tPanel_4.Title=
tPanel_4.Name=处置
tPanel_4.Item=OP_TABLE
OP_TABLE.Type=TTable
OP_TABLE.X=4
OP_TABLE.Y=5
OP_TABLE.Width=936
OP_TABLE.Height=373
OP_TABLE.SpacingRow=1
OP_TABLE.RowHeight=20
OP_TABLE.AutoX=N
OP_TABLE.AutoY=N
OP_TABLE.AutoWidth=Y
OP_TABLE.AutoHeight=Y
OP_TABLE.Header=连,30,boolean;组,30,int;医嘱,200;用量,40,double,##0.00;单位,40,UNIT_CODE;频次,50,FREQ_CODE;日份,40,int,#0;总量,40,double;执行科室,80,OP_EXEC_DEPT;医生备注,200;护士备注,200;急作,40,boolean;给付类别,80,INS_PAY
OP_TABLE.ColumnHorizontalAlignmentData=2,left;3,right;4,left;5,left;6,right;7,right;8,left;9,left;10,left;12,left
OP_TABLE.ParmMap=LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;DISPENSE_QTY;EXEC_DEPT_CODE;DR_NOTE;NS_NOTE;URGENT_FLG;INSPAY_TYPE
OP_TABLE.Item=UNIT_CODE;FREQ_CODE;OP_EXEC_DEPT;INS_PAY
OP_TABLE.FocusType=2
OP_TABLE.LockColumns=0,1,2,3,4,5,6,7,8,9,10,11,12
tPanel_3.Type=TPanel
tPanel_3.X=29
tPanel_3.Y=51
tPanel_3.Width=81
tPanel_3.Height=81
tPanel_3.Text=
tPanel_3.Title=
tPanel_3.Name=检验检查
tPanel_3.Item=EXA_TABLE
EXA_TABLE.Type=TTable
EXA_TABLE.X=5
EXA_TABLE.Y=7
EXA_TABLE.Width=935
EXA_TABLE.Height=371
EXA_TABLE.SpacingRow=1
EXA_TABLE.RowHeight=20
EXA_TABLE.AutoWidth=Y
EXA_TABLE.AutoHeight=Y
EXA_TABLE.Header=医嘱,200;数量,40,double;单位,40,UNIT_CODE;执行科室,80,TECH_DEPT;检体,80,OPTITEM_CODE;医生备注,200;护士备注,200;急作,40,boolean;给付类别,80,INS_PAY
EXA_TABLE.ParmMap=ORDER_DESC;MEDI_QTY;MEDI_UNIT;EXEC_DEPT_CODE;OPTITEM_CODE;DR_NOTE;NS_NOTE;URGENT_FLG;INSPAY_TYPE
EXA_TABLE.Item=TECH_DEPT;UNIT_CODE;OPTITEM_CODE;INS_PAY
EXA_TABLE.LockColumns=0,1,2,3,4,5,6,7,8
EXA_TABLE.FocusType=2
EXA_TABLE.ColumnHorizontalAlignmentData=0,left;1,right;2,left;3,left;4,left;5,left;6,left;8,left
WC.Type=TComboBox
WC.X=568
WC.Y=23
WC.Width=81
WC.Height=23
WC.Text=TButton
WC.showID=Y
WC.Editable=Y
WC.StringData=[[id,name],[C,中医],[W,西医]]
WC.ShowName=Y
WC.ShowText=N
WC.TableShowList=name
DIAG_TABLE.Type=TTable
DIAG_TABLE.X=414
DIAG_TABLE.Y=22
DIAG_TABLE.Width=546
DIAG_TABLE.Height=244
DIAG_TABLE.SpacingRow=1
DIAG_TABLE.RowHeight=20
DIAG_TABLE.Header=中西医,80,WC;主,40,boolean;诊断内容,200;备注,200
DIAG_TABLE.AutoWidth=Y
DIAG_TABLE.Item=WC
DIAG_TABLE.LockColumns=0,1,2,3
DIAG_TABLE.ColumnHorizontalAlignmentData=0,left;2,left;3,left
DIAG_TABLE.ParmMap=ICD_TYPE;MAIN_DIAG_FLG;ICD_CHN_DESC;DIAG_NOTE
PHYSEXAM_REC.Type=TTextArea
PHYSEXAM_REC.X=64
PHYSEXAM_REC.Y=167
PHYSEXAM_REC.Width=333
PHYSEXAM_REC.Height=99
PHYSEXAM_REC.SpacingRow=1
PHYSEXAM_REC.RowHeight=20
OBJ_TEXT.Type=TTextArea
OBJ_TEXT.X=63
OBJ_TEXT.Y=60
OBJ_TEXT.Width=331
OBJ_TEXT.Height=81
OBJ_TEXT.SpacingRow=1
OBJ_TEXT.RowHeight=20
SUBJ_TEXT.Type=TTextField
SUBJ_TEXT.X=64
SUBJ_TEXT.Y=21
SUBJ_TEXT.Width=330
SUBJ_TEXT.Height=20
SUBJ_TEXT.Text=
tPanel_1.Type=TPanel
tPanel_1.X=8
tPanel_1.Y=35
tPanel_1.Width=265
tPanel_1.Height=724
tPanel_1.AutoHeight=Y
tPanel_1.Border=组|就诊记录
tPanel_1.Item=TABLE;OPERATOR
OPERATOR.Type=人员下拉列表
OPERATOR.X=46
OPERATOR.Y=525
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
TABLE.Type=TTable
TABLE.X=8
TABLE.Y=27
TABLE.Width=246
TABLE.Height=686
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.AutoY=N
TABLE.AutoX=N
TABLE.Header=就诊日期,100,timestamp,yyyy/MM/dd;就诊医师,100,OPERATOR
TABLE.LockColumns=0,1
TABLE.ColumnHorizontalAlignmentData=0,left;1,left
TABLE.Item=OPERATOR
TABLE.ParmMap=ADM_DATE;DR_CODE
TABLE.ClickedAction=onTableClicked

CHN_TABLE.Type=TTable
CHN_TABLE.X=6
CHN_TABLE.Y=34
CHN_TABLE.Width=934
CHN_TABLE.Height=344
CHN_TABLE.SpacingRow=1
CHN_TABLE.RowHeight=20
CHN_TABLE.AutoWidth=Y
CHN_TABLE.AutoHeight=Y
CHN_TABLE.Header=名称,100;用量,100,double;特殊煎法,100,DCTEXCEP_CODE;名称,100;用量,100,double;特殊煎法,100,DCTEXCEP_CODE;名称,100;用量,100,double;特殊煎法,100,DCTEXCEP_CODE;名称,100;用量,100,double;特殊煎法,100,DCTEXCEP_CODE
CHN_TABLE.LockColumns=0,1,2,3,4,5,6,7,8,9,10,11
CHN_TABLE.ColumnHorizontalAlignmentData=0,left;1,right;2,left;3,left;4,right;5,left;6,left;7,right;8,left;9,left;10,right;11,left
CHN_TABLE.ParmMap=ORDER_DESC1;MEDI_QTY1;DCTEXCEP_CODE1;ORDER_DESC2;MEDI_QTY2;DCTEXCEP_CODE2;ORDER_DESC3;MEDI_QTY3;DCTEXCEP_CODE3;ORDER_DESC4;MEDI_QTY4;DCTEXCEP_CODE4
CHN_TABLE.Item=DCTEXCEP_CODE