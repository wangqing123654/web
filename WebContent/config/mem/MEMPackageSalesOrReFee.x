## TBuilder Config File ## Title: 套餐销售/退费明细报表## Company:JavaHis## Author:黄婷婷 2015.11.18## version 1.0#<Type=TFrame>UI.Title=套餐销售/退费明细表UI.MenuConfig=%ROOT%\config\mem\MEMPackageSalesOrReFeeMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.mem.MEMPackageSalesOrReFeeControlUI.item=tPanel_0;TABLEUI.layout=nullUI.TopMenu=YUI.TopToolBar=YTABLE.Type=TTableTABLE.X=8TABLE.Y=82TABLE.Width=81TABLE.Height=81TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=序号,40;套餐编码,80;套餐名称,200;病案号,80;姓名,100;身份,150;客户来源,120,CUSTOMER_SOURCE;状态,60;购买/退费时间,140;购买/退费金额,100;备注,150TABLE.LockColumns=allTABLE.Item=CUSTOMER_SOURCETABLE.ParmMap=SEQ;PACKAGE_CODE;PACKAGE_DESC;MR_NO;PAT_NAME;CTZ_DESC;CUSTOMER_SOURCE;STATUS;BILL_DATE;AR_AMT;DESCRIPTIONTABLE.ColumnHorizontalAlignmentData=1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,right;10,leftTABLE.ClickedAction=TABLE.ChangeValueAction=TABLE.ChangeAction=TABLE.ColumnChangeAction=TABLE.RowChangeAction=TABLE.DoubleClickedAction=TABLE.RightClickedAction=tPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=6tPanel_0.Width=1014tPanel_0.Height=70tPanel_0.Border=组|查询条件tPanel_0.AutoX=YtPanel_0.AutoWidth=YtPanel_0.Item=tLabel_8;tLabel_10;S_DATE;E_DATE;tLabel_0;MR_NO;tLabel_1;PACKAGE_CODE;套餐类别_0;套餐类别_1;tLabel_2;STATUS;CUSTOMER_SOURCECUSTOMER_SOURCE.Type=客户来源CUSTOMER_SOURCE.X=871CUSTOMER_SOURCE.Y=55CUSTOMER_SOURCE.Width=81CUSTOMER_SOURCE.Height=23CUSTOMER_SOURCE.Text=CUSTOMER_SOURCE.HorizontalAlignment=2CUSTOMER_SOURCE.PopupMenuHeader=代码,100;名称,100CUSTOMER_SOURCE.PopupMenuWidth=300CUSTOMER_SOURCE.PopupMenuHeight=300CUSTOMER_SOURCE.PopupMenuFilter=ID,1;NAME,1CUSTOMER_SOURCE.FormatType=comboCUSTOMER_SOURCE.ShowDownButton=YCUSTOMER_SOURCE.Tip=客户来源CUSTOMER_SOURCE.ShowColumnList=NAMECUSTOMER_SOURCE.HisOneNullRow=YCUSTOMER_SOURCE.Visible=NSTATUS.Type=TComboBoxSTATUS.X=888STATUS.Y=29STATUS.Width=81STATUS.Height=23STATUS.Text=TButtonSTATUS.showID=YSTATUS.Editable=YSTATUS.StringData=[[id,text],[0,全部],[1,购买],[2,退费]]STATUS.TableShowList=texttLabel_2.Type=TLabeltLabel_2.X=843tLabel_2.Y=31tLabel_2.Width=47tLabel_2.Height=15tLabel_2.Text=状态：tLabel_2.Color=蓝套餐类别_1.Type=套餐类别套餐类别_1.X=847套餐类别_1.Y=39套餐类别_0.Type=套餐类别套餐类别_0.X=840套餐类别_0.Y=34PACKAGE_CODE.Type=套餐种类属性下拉区域PACKAGE_CODE.X=667PACKAGE_CODE.Y=27PACKAGE_CODE.Width=159PACKAGE_CODE.Height=23PACKAGE_CODE.Text=PACKAGE_CODE.HorizontalAlignment=2PACKAGE_CODE.PopupMenuHeader=代码,70;名称,200PACKAGE_CODE.PopupMenuWidth=200PACKAGE_CODE.PopupMenuHeight=100PACKAGE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1PACKAGE_CODE.FormatType=comboPACKAGE_CODE.ShowDownButton=YPACKAGE_CODE.Tip=套餐种类属性PACKAGE_CODE.ShowColumnList=NAMEPACKAGE_CODE.HisOneNullRow=YtLabel_1.Type=TLabeltLabel_1.X=592tLabel_1.Y=31tLabel_1.Width=72tLabel_1.Height=15tLabel_1.Text=套餐名称：tLabel_1.Color=蓝MR_NO.Type=TTextFieldMR_NO.X=478MR_NO.Y=29MR_NO.Width=101MR_NO.Height=20MR_NO.Text=MR_NO.Action=onMrNotLabel_0.Type=TLabeltLabel_0.X=426tLabel_0.Y=32tLabel_0.Width=58tLabel_0.Height=15tLabel_0.Text=病案号：tLabel_0.Color=蓝E_DATE.Type=TTextFormatE_DATE.X=259E_DATE.Y=30E_DATE.Width=155E_DATE.Height=20E_DATE.Text=TTextFormatE_DATE.FormatType=dateE_DATE.Format=yyyy/MM/dd HH:mm:ssE_DATE.showDownButton=YS_DATE.Type=TTextFormatS_DATE.X=83S_DATE.Y=30S_DATE.Width=155S_DATE.Height=20S_DATE.Text=TTextFormatS_DATE.FormatType=dateS_DATE.Format=yyyy/MM/dd HH:mm:ssS_DATE.showDownButton=YtLabel_10.Type=TLabeltLabel_10.X=242tLabel_10.Y=32tLabel_10.Width=20tLabel_10.Height=15tLabel_10.Text=至tLabel_10.Color=bluetLabel_8.Type=TLabeltLabel_8.X=15tLabel_8.Y=33tLabel_8.Width=72tLabel_8.Height=15tLabel_8.Text=查询时间：tLabel_8.Color=blue