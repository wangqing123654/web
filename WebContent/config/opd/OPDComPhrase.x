<Type=TFrame>
UI.Title=常用片语
UI.MenuConfig=%ROOT%\config\opd\OPDComPhraseMenu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.opd.OPDComPhraseControl
UI.Item=LABEL;TABLECOM;DEPT;OPERATOR
UI.TopMenu=Y
UI.ShowTitle=N
UI.TopToolBar=Y
UI.zhTitle=常用片语
UI.enTitle=Phrase
PHRASE_TYPE.Type=TComboBox
PHRASE_TYPE.X=339
PHRASE_TYPE.Y=6
PHRASE_TYPE.Width=81
PHRASE_TYPE.Height=23
PHRASE_TYPE.Text=TButton
PHRASE_TYPE.showID=Y
PHRASE_TYPE.Editable=Y
PHRASE_TYPE.zhTip=片语类型
PHRASE_TYPE.enTip=Phrase Type
PHRASE_TYPE.ShowName=Y
PHRASE_TYPE.ShowText=N
PHRASE_TYPE.TableShowList=name
PHRASE_TYPE.StringData=[[id,name,enname],[1,主诉,Chief Complain],[2,现病史,Present History],[3,体征,Physical Exam],[4,家族史,Family Med History]]
OPERATOR.Type=人员下拉列表
OPERATOR.X=62
OPERATOR.Y=6
OPERATOR.Width=181
OPERATOR.Height=23
OPERATOR.Text=TButton
OPERATOR.showID=Y
OPERATOR.showName=Y
OPERATOR.showText=N
OPERATOR.showValue=N
OPERATOR.showPy1=N
OPERATOR.showPy2=N
OPERATOR.Editable=Y
OPERATOR.Tip=人员
OPERATOR.TableShowList=name
OPERATOR.ModuleParmString=
OPERATOR.ModuleParmTag=
DEPT.Type=科室下拉列表
DEPT.X=63
DEPT.Y=6
DEPT.Width=181
DEPT.Height=23
DEPT.Text=TButton
DEPT.showID=Y
DEPT.showName=Y
DEPT.showText=N
DEPT.showValue=N
DEPT.showPy1=N
DEPT.showPy2=N
DEPT.Editable=Y
DEPT.Tip=科室
DEPT.TableShowList=name
TABLECOM.Type=TTable
TABLECOM.X=11
TABLECOM.Y=34
TABLECOM.Width=81
TABLECOM.Height=709
TABLECOM.SpacingRow=1
TABLECOM.RowHeight=20
TABLECOM.AutoX=Y
TABLECOM.AutoWidth=Y
TABLECOM.AutoHeight=Y
TABLECOM.Header=片语简码,200;片语类型,150,PHRASE_TYPE;内容,200;操作人员,100,OPERATOR;操作日期,100
TABLECOM.AutoModifyDataStore=Y
TABLECOM.SQL=
TABLECOM.ParmMap=PHRASE_CODE;PHRASE_TYPE;PHRASE_TEXT;OPT_USER;OPT_DATE
TABLECOM.AutoModifyObject=N
TABLECOM.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left
TABLECOM.Item=OPERATOR;PHRASE_TYPE
TABLECOM.FocusIndexList=0,1,2
TABLECOM.enHeader=Phrase Code;Phrase;Content;Operator;Operation Data
TABLECOM.FocusType=2
TABLECOM.LockColumns=
LABEL.Type=TLabel
LABEL.X=12
LABEL.Y=11
LABEL.Width=47
LABEL.Height=15
LABEL.Text=科室