## TBuilder Config File ## Title:## Company:JavaHis## Author:庞犇 2011.05.04## version 1.0#<Type=TFrame>UI.Title=UI.MenuConfig=UI.Width=446UI.Height=300UI.toolbar=NUI.controlclassname=com.javahis.ui.clp.CLPOrderPopupControlUI.item=tPanel_5UI.layout=nullUI.FocusList=EDITUI.X=5UI.AutoX=YtPanel_5.Type=TPaneltPanel_5.X=5tPanel_5.Y=5tPanel_5.Width=446tPanel_5.Height=290tPanel_5.Border=凸tPanel_5.Item=tLabel_4;tLabel_5;EDIT;TABLEtPanel_5.AutoX=YtPanel_5.AutoWidth=YtPanel_5.AutoHeight=YtPanel_5.AutoY=YCHKTYPE_CODE.Type=查核类别下拉区域CHKTYPE_CODE.X=10CHKTYPE_CODE.Y=91CHKTYPE_CODE.Width=81CHKTYPE_CODE.Height=23CHKTYPE_CODE.Text=CHKTYPE_CODE.HorizontalAlignment=2CHKTYPE_CODE.PopupMenuHeader=代码,100;名称,100CHKTYPE_CODE.PopupMenuWidth=300CHKTYPE_CODE.PopupMenuHeight=300CHKTYPE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1CHKTYPE_CODE.FormatType=comboCHKTYPE_CODE.ShowDownButton=YCHKTYPE_CODE.Tip=查核类别CHKTYPE_CODE.ShowColumnList=NAMECHKTYPE_CODE.ValueColumn=IDCHKTYPE_CODE.Visible=YCHKTYPE_CODE.DynamicDownload=NCHKUSER_CODE.Type=临床路径执行人员下拉区域CHKUSER_CODE.X=10CHKUSER_CODE.Y=91CHKUSER_CODE.Width=81CHKUSER_CODE.Height=23CHKUSER_CODE.Text=CHKUSER_CODE.HorizontalAlignment=2CHKUSER_CODE.PopupMenuHeader=代码,100;名称,100CHKUSER_CODE.PopupMenuWidth=300CHKUSER_CODE.PopupMenuHeight=300CHKUSER_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1CHKUSER_CODE.FormatType=comboCHKUSER_CODE.ShowDownButton=YCHKUSER_CODE.Tip=临床路径执行人员CHKUSER_CODE.ShowColumnList=NAMECHKUSER_CODE.ValueColumn=IDCHKUSER_CODE.Visible=YCHKUSER_CODE.DynamicDownload=NCLNCPATH_CODE.Type=适用临床路径下拉区域CLNCPATH_CODE.X=10CLNCPATH_CODE.Y=91CLNCPATH_CODE.Width=81CLNCPATH_CODE.Height=23CLNCPATH_CODE.Text=CLNCPATH_CODE.HorizontalAlignment=2CLNCPATH_CODE.PopupMenuHeader=代码,100;名称,100CLNCPATH_CODE.PopupMenuWidth=300CLNCPATH_CODE.PopupMenuHeight=300CLNCPATH_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1CLNCPATH_CODE.FormatType=comboCLNCPATH_CODE.ShowDownButton=YCLNCPATH_CODE.Tip=适用临床路径CLNCPATH_CODE.ShowColumnList=NAMECLNCPATH_CODE.ValueColumn=IDCLNCPATH_CODE.Visible=YCLNCPATH_CODE.DynamicDownload=NTABLE.Type=TTableTABLE.X=7TABLE.Y=32TABLE.Width=432TABLE.Height=251TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.Header=查核类别,100,CHKTYPE_CODE;项目代码,100;区域代码,100;中文说明,200;英文说明,200;项目拼音,100;助记码,100;顺序号,45;适用临床路径项目,100,CLNCPATH_CODE;被查核人员,100,CHKUSER_CODE;单独查核,60,Boolean;备注,200TABLE.LockColumns=allTABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,right;8,left;9,left;10,left;11,leftTABLE.ParmMap=CHKTYPE_CODE;ORDER_CODE;REGION_CODE;ORDER_CHN_DESC;ORDER_ENG_DESC;PY1;PY2;SEQ;CLNCPATH_CODE;CHKUSER_CODE;INDV_FLG;DESCRIPTIONTABLE.AutoX=YTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Item=CHKTYPE_CODE;CLNCPATH_CODE;CHKUSER_CODETABLE.SQL=TABLE.LocalTableName=CLP_CHKITEMEDIT.Type=TTextFieldEDIT.X=10EDIT.Y=6EDIT.Width=367EDIT.Height=20EDIT.Text=EDIT.Action=EDIT.FocusLostAction=grabFocustLabel_5.Type=TLabeltLabel_5.X=406tLabel_5.Y=9tLabel_5.Width=15tLabel_5.Height=14tLabel_5.Text=tLabel_5.PictureName=sys.giftLabel_4.Type=TLabeltLabel_4.X=388tLabel_4.Y=9tLabel_4.Width=21tLabel_4.Height=15tLabel_4.Text=tLabel_4.PictureName=table.giftLabel_4.Action=onResetFile