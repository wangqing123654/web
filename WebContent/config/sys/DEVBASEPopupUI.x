<Type=TFrame>
UI.Width=400
UI.Height=300
UI.layout=null
UI.bkcolor=160,220,230
UI.Item=tPanel_0
UI.ControlClassName=com.javahis.ui.sys.DEVBASEPopupControl
UI.FocusList=EDIT
tPanel_0.Type=TPanel
tPanel_0.X=0
tPanel_0.Y=0
tPanel_0.Width=400
tPanel_0.Height=300
tPanel_0.AutoX=Y
tPanel_0.AutoY=Y
tPanel_0.AutoHeight=Y
tPanel_0.AutoWidth=Y
tPanel_0.Border=凸
tPanel_0.AutoSize=0
tPanel_0.Item=EDIT;TABLE;tLabel_0;tLabel_1
tPanel_0.ControlClassName=
UNIT_CODE.Type=计量单位下拉列表
UNIT_CODE.X=239
UNIT_CODE.Y=40
UNIT_CODE.Width=81
UNIT_CODE.Height=23
UNIT_CODE.Text=TButton
UNIT_CODE.showID=Y
UNIT_CODE.showName=Y
UNIT_CODE.showText=N
UNIT_CODE.showValue=N
UNIT_CODE.showPy1=Y
UNIT_CODE.showPy2=Y
UNIT_CODE.Editable=Y
UNIT_CODE.Tip=计量单位
UNIT_CODE.TableShowList=name
tLabel_1.Type=TLabel
tLabel_1.X=378
tLabel_1.Y=4
tLabel_1.Width=16
tLabel_1.Height=14
tLabel_1.Text=
tLabel_1.PictureName=sys.gif
tLabel_1.CursorType=12
tLabel_1.Action=onResetFile
tLabel_0.Type=TLabel
tLabel_0.X=359
tLabel_0.Y=4
tLabel_0.Width=16
tLabel_0.Height=14
tLabel_0.Text=
tLabel_0.PictureName=table.gif
tLabel_0.CursorType=12
tLabel_0.Action=onResetDW
TABLE.Type=TTable
TABLE.X=2
TABLE.Y=24
TABLE.Width=200
TABLE.Height=272
TABLE.SpacingRow=1
TABLE.RowHeight=20
TABLE.AutoSize=2
TABLE.AutoX=Y
TABLE.AutoY=N
TABLE.AutoWidth=Y
TABLE.AutoHeight=Y
TABLE.LocalTableName=
TABLE.LockColumns=all
TABLE.ShowCount=20
TABLE.SQL=
TABLE.Header=设备编码,100;设备名称,200;规格,100;设备单位,70,UNIT_CODE
TABLE.ParmMap=DEV_CODE;DEV_CHN_DESC;SPECIFICATION;UNIT_CODE
TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,right;3,left
TABLE.AutoModifyDataStore=Y
TABLE.Item=UNIT_CODE
EDIT.Type=TTextField
EDIT.X=2
EDIT.Y=2
EDIT.Width=353
EDIT.Height=20
EDIT.Text=
EDIT.FocusLostAction=grabFocus