#  # Title: 组长票据领用管理  #  # Description:组长票据领用管理  #  # Copyright: JavaHis (c) 2008  #  # @author fudw  # @version 1.0<Type=TFrame>UI.Title=票据管理UI.MenuConfig=%ROOT%\config\bil\BILInvoiceMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.bil.BILInvoiceControlUI.item=tPanel_0;tPanel_1UI.layout=nullUI.AutoX=YUI.AutoY=YUI.TopMenu=YUI.TopToolBar=YUI.Y=5tPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=100tPanel_1.Width=1014tPanel_1.Height=643tPanel_1.Border=凸tPanel_1.Item=TABLE;OPT_USERtPanel_1.AutoX=YtPanel_1.AutoWidth=YtPanel_1.AutoHeight=YOPT_USER.Type=人员OPT_USER.X=532OPT_USER.Y=66OPT_USER.Width=81OPT_USER.Height=23OPT_USER.Text=OPT_USER.HorizontalAlignment=2OPT_USER.PopupMenuHeader=代码,100;名称,100OPT_USER.PopupMenuWidth=300OPT_USER.PopupMenuHeight=300OPT_USER.PopupMenuFilter=ID,1;NAME,1;PY1,1OPT_USER.FormatType=comboOPT_USER.ShowDownButton=YOPT_USER.Tip=人员OPT_USER.ShowColumnList=NAMEOPT_USER.HisOneNullRow=YOPT_USER.EndDateFlg=1TABLE.Type=TTableTABLE.X=4TABLE.Y=10TABLE.Width=992TABLE.Height=626TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.Header=确认,40,boolean;票据类别,100,RECP_TYPE;开始票号,100;结束票号,100;目前票号,100;领用人员,100,CASHIER_CODE_T;领用日期,100;开账IP,105;交回日期,100;使用状态,100,STATUS;操作人员,100,OPT_USER;操作时间,100;操作终端,105TABLE.ParmMap=FLG;RECP_TYPE;START_INVNO;END_INVNO;UPDATE_NO;CASHIER_CODE;START_VALID_DATE;TERM_IP;END_VALID_DATE;STATUS;OPT_USER;OPT_DATE;OPT_TERM TABLE.LockRows=TABLE.LockColumns=1,2,3,4,5,6,7,8,9,10,11,12TABLE.Item=STATUS;RECP_TYPE;CASHIER_CODE_T;OPT_USERTABLE.ColumnHorizontalAlignmentData=1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left;11,left;12,left;TABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.AutoX=YTABLE.AutoY=YtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=92tPanel_0.Border=凸tPanel_0.Item=tLabel_0;tLabel_1;tLabel_2;tLabel_3;tLabel_4;tDateField_0;tDateField_1;RECP_TYPE;STATUS;START_VALID_DATE;END_VALID_DATE;CASHIER_CODE;CASHIER_CODE_TtPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YCASHIER_CODE_T.Type=人员CASHIER_CODE_T.X=521CASHIER_CODE_T.Y=17CASHIER_CODE_T.Width=81CASHIER_CODE_T.Height=23CASHIER_CODE_T.Text=CASHIER_CODE_T.HorizontalAlignment=2CASHIER_CODE_T.PopupMenuHeader=代码,100;名称,100CASHIER_CODE_T.PopupMenuWidth=300CASHIER_CODE_T.PopupMenuHeight=300CASHIER_CODE_T.PopupMenuFilter=ID,1;NAME,1;PY1,1CASHIER_CODE_T.FormatType=comboCASHIER_CODE_T.ShowDownButton=YCASHIER_CODE_T.Tip=人员CASHIER_CODE_T.ShowColumnList=NAMECASHIER_CODE_T.Visible=NCASHIER_CODE_T.HisOneNullRow=YCASHIER_CODE_T.EndDateFlg=1CASHIER_CODE.Type=人员CASHIER_CODE.X=88CASHIER_CODE.Y=54CASHIER_CODE.Width=108CASHIER_CODE.Height=23CASHIER_CODE.Text=CASHIER_CODE.HorizontalAlignment=2CASHIER_CODE.PopupMenuHeader=代码,100;名称,100CASHIER_CODE.PopupMenuWidth=300CASHIER_CODE.PopupMenuHeight=300CASHIER_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1CASHIER_CODE.FormatType=comboCASHIER_CODE.ShowDownButton=YCASHIER_CODE.Tip=人员CASHIER_CODE.ShowColumnList=NAMECASHIER_CODE.PosType=5CASHIER_CODE.HisOneNullRow=YCASHIER_CODE.EndDateFlg=1END_VALID_DATE.Type=TTextFormatEND_VALID_DATE.X=451END_VALID_DATE.Y=55END_VALID_DATE.Width=110END_VALID_DATE.Height=20END_VALID_DATE.Text=TTextFormatEND_VALID_DATE.showDownButton=YEND_VALID_DATE.FormatType=dateEND_VALID_DATE.Format=yyyy/MM/ddSTART_VALID_DATE.Type=TTextFormatSTART_VALID_DATE.X=269START_VALID_DATE.Y=55START_VALID_DATE.Width=110START_VALID_DATE.Height=20START_VALID_DATE.Text=TTextFormatSTART_VALID_DATE.showDownButton=YSTART_VALID_DATE.FormatType=dateSTART_VALID_DATE.Format=yyyy/MM/ddSTATUS.Type=票据状态下拉列表STATUS.X=269STATUS.Y=14STATUS.Width=81STATUS.Height=23STATUS.Text=TButtonSTATUS.showID=YSTATUS.showName=YSTATUS.showText=NSTATUS.showValue=NSTATUS.showPy1=YSTATUS.showPy2=YSTATUS.Editable=YSTATUS.Tip=票据状态下拉列表STATUS.TableShowList=nameSTATUS.ModuleParmString=GROUP_ID:BIL_INVOICE_STATUSSTATUS.ModuleParmTag=STATUS.ExpandWidth=80STATUS.Enabled=YRECP_TYPE.Type=收据类别下拉列表RECP_TYPE.X=88RECP_TYPE.Y=14RECP_TYPE.Width=108RECP_TYPE.Height=23RECP_TYPE.Text=TButtonRECP_TYPE.showID=YRECP_TYPE.showName=YRECP_TYPE.showText=NRECP_TYPE.showValue=NRECP_TYPE.showPy1=YRECP_TYPE.showPy2=YRECP_TYPE.Editable=YRECP_TYPE.Tip=收据类别下拉列表RECP_TYPE.TableShowList=nameRECP_TYPE.ModuleParmString=GROUP_ID:BIL_RECP_TYPERECP_TYPE.ModuleParmTag=RECP_TYPE.ExpandWidth=80tDateField_1.Type=TDateFieldtDateField_1.X=466tDateField_1.Y=95tDateField_0.Type=TDateFieldtDateField_0.X=459tDateField_0.Y=87tLabel_4.Type=TLabeltLabel_4.X=385tLabel_4.Y=58tLabel_4.Width=64tLabel_4.Height=15tLabel_4.Text=缴回时间:tLabel_3.Type=TLabeltLabel_3.X=204tLabel_3.Y=58tLabel_3.Width=66tLabel_3.Height=15tLabel_3.Text=领用时间:tLabel_2.Type=TLabeltLabel_2.X=23tLabel_2.Y=58tLabel_2.Width=66tLabel_2.Height=15tLabel_2.Text=领用人员:tLabel_2.Color=蓝tLabel_1.Type=TLabeltLabel_1.X=22tLabel_1.Y=18tLabel_1.Width=65tLabel_1.Height=15tLabel_1.Text=领用类别:tLabel_1.Color=蓝tLabel_0.Type=TLabeltLabel_0.X=204tLabel_0.Y=18tLabel_0.Width=66tLabel_0.Height=15tLabel_0.Text=使用状态:tLabel_0.Color=蓝