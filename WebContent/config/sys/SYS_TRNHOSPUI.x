## TBuilder Config File ## Title:外转院所## Company:bluecore## Author: shibl 2012.01.05## version 1.0#<Type=TFrame>UI.Title=外转院所UI.MenuConfig=%ROOT%\config\sys\SYS_TRNHOSP_Menu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.sys.SYSTrnHospControlUI.item=tPanel_0;TABLEUI.layout=nullUI.TopMenu=YUI.TopToolBar=YTABLE.Type=TTableTABLE.X=9TABLE.Y=110TABLE.Width=84TABLE.Height=81TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=机构代码,150;机构名称,200;PY1,100;PY2,100;序号,50;机构属性,100,HOSP_TYPE;备注,200;操作者,150;操作时间,150;操作IP,150TABLE.ParmMap=HOSP_CODE;HOSP_DESC;PY1;PY2;SEQ;HOSP_TYPE;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERMTABLE.LockRows=TABLE.ColumnHorizontalAlignmentData= 0,left;1,left;2,left;3,left;4,right;5,left;6,left;7,left;8,left;9,leftTABLE.ClickedAction=onTableClickedTABLE.AutoModifyDataStore=YTABLE.LockColumns=allTABLE.Item=HOSP_TYPEtPanel_0.Type=TPaneltPanel_0.X=8tPanel_0.Y=8tPanel_0.Width=1011tPanel_0.Height=97tPanel_0.Border=组tPanel_0.AutoWidth=YtPanel_0.Item=tLabel_0;HOSP_CODE;tLabel_1;HOSP_DESC;tLabel_2;PY1;PY2;tLabel_3;tLabel_4;HOSP_TYPE;tLabel_5;tLabel_6;DESCRIPTION;SEQSEQ.Type=TNumberTextFieldSEQ.X=293SEQ.Y=54SEQ.Width=98SEQ.Height=20SEQ.Text=0SEQ.Format=#########0DESCRIPTION.Type=TTextFieldDESCRIPTION.X=449DESCRIPTION.Y=53DESCRIPTION.Width=193DESCRIPTION.Height=20DESCRIPTION.Text=tLabel_6.Type=TLabeltLabel_6.X=410tLabel_6.Y=57tLabel_6.Width=35tLabel_6.Height=15tLabel_6.Text=备注tLabel_5.Type=TLabeltLabel_5.X=222tLabel_5.Y=57tLabel_5.Width=72tLabel_5.Height=15tLabel_5.Text=顺序号:HOSP_TYPE.Type=TComboBoxHOSP_TYPE.X=85HOSP_TYPE.Y=54HOSP_TYPE.Width=117HOSP_TYPE.Height=23HOSP_TYPE.Text=HOSP_TYPE.showID=YHOSP_TYPE.Editable=YHOSP_TYPE.StringData= [[id,name],[,],[1,医院],[2,社区]]HOSP_TYPE.ShowText=NHOSP_TYPE.ShowName=YHOSP_TYPE.TableShowList=nametLabel_4.Type=TLabeltLabel_4.X=17tLabel_4.Y=59tLabel_4.Width=69tLabel_4.Height=15tLabel_4.Text=机构属性:tLabel_3.Type=TLabeltLabel_3.X=647tLabel_3.Y=16tLabel_3.Width=35tLabel_3.Height=15tLabel_3.Text=PY2PY2.Type=TTextFieldPY2.X=685PY2.Y=14PY2.Width=135PY2.Height=20PY2.Text=PY1.Type=TTextFieldPY1.X=502PY1.Y=14PY1.Width=138PY1.Height=20PY1.Text=tLabel_2.Type=TLabeltLabel_2.X=472tLabel_2.Y=15tLabel_2.Width=25tLabel_2.Height=15tLabel_2.Text=PY1HOSP_DESC.Type=TTextFieldHOSP_DESC.X=292HOSP_DESC.Y=14HOSP_DESC.Width=166HOSP_DESC.Height=20HOSP_DESC.Text=HOSP_DESC.Action=onHospDescActiontLabel_1.Type=TLabeltLabel_1.X=224tLabel_1.Y=16tLabel_1.Width=72tLabel_1.Height=17tLabel_1.Text=机构名称:HOSP_CODE.Type=TTextFieldHOSP_CODE.X=86HOSP_CODE.Y=14HOSP_CODE.Width=115HOSP_CODE.Height=21HOSP_CODE.Text=tLabel_0.Type=TLabeltLabel_0.X=17tLabel_0.Y=17tLabel_0.Width=72tLabel_0.Height=15tLabel_0.Text=机构代码: