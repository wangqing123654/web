## TBuilder Config File ## Title:## Company:JavaHis## Author:kangy 2017.11.03## version 1.0#<Type=TFrame>UI.Title=预收套餐汇总表UI.MenuConfig=%ROOT%\config\mem\MEMPackageSummaryMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.mem.MEMPackageSummaryControlUI.item=tPanel_0;tPanel_1UI.layout=nullUI.TopMenu=YUI.TopToolBar=YUI.ShowMenu=YUI.ShowTitle=NtPanel_1.Type=TPaneltPanel_1.X=8tPanel_1.Y=94tPanel_1.Width=1008tPanel_1.Height=649tPanel_1.Border=组tPanel_1.AutoWidth=YtPanel_1.AutoHeight=YtPanel_1.Item=TABLETABLE.Type=TTableTABLE.X=9TABLE.Y=6TABLE.Width=991TABLE.Height=632TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=日期,100;套餐类别,100;前日结余,100,double,########0.00;套餐销售,100,double,########0.00;套餐退费,100,double,########0.00;套餐结转,100,double,########0.00;套餐余额,100,double,########0.00TABLE.LockColumns=allTABLE.ParmMap=BILL_DATE;CHN_DESC;AR_AMT;XS_AMT;TF_AMT;JZ_AMT;YE_AMTTABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,right;3,right;4,right;5,right;6,rightTABLE.Item=tPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=-1tPanel_0.Width=1002tPanel_0.Height=92tPanel_0.Border=组|查询条件tPanel_0.AutoWidth=YtPanel_0.Item=tLabel_0;START_DATE;tLabel_2;END_DATE;tLabel_3;PACKAGE_CLASSPACKAGE_CLASS.Type=套餐分类PACKAGE_CLASS.X=507PACKAGE_CLASS.Y=30PACKAGE_CLASS.Width=138PACKAGE_CLASS.Height=23PACKAGE_CLASS.Text=PACKAGE_CLASS.HorizontalAlignment=2PACKAGE_CLASS.PopupMenuHeader=代码,100;名称,100PACKAGE_CLASS.PopupMenuWidth=300PACKAGE_CLASS.PopupMenuHeight=300PACKAGE_CLASS.PopupMenuFilter=ID,1;NAME,1;PY1,1PACKAGE_CLASS.FormatType=comboPACKAGE_CLASS.ShowDownButton=YPACKAGE_CLASS.Tip=套餐分类PACKAGE_CLASS.ShowColumnList=NAMEPACKAGE_CLASS.HisOneNullRow=YtLabel_3.Type=TLabeltLabel_3.X=441tLabel_3.Y=34tLabel_3.Width=66tLabel_3.Height=15tLabel_3.Text=套餐类别:tLabel_3.Color=蓝END_DATE.Type=TTextFormatEND_DATE.X=264END_DATE.Y=33END_DATE.Width=158END_DATE.Height=20END_DATE.Text=END_DATE.showDownButton=YEND_DATE.FormatType=dateEND_DATE.Format=yyyy/MM/dd HH:mm:sstLabel_2.Type=TLabeltLabel_2.X=252tLabel_2.Y=39tLabel_2.Width=17tLabel_2.Height=15tLabel_2.Text=~tLabel_2.Color=蓝START_DATE.Type=TTextFormatSTART_DATE.X=90START_DATE.Y=32START_DATE.Width=158START_DATE.Height=20START_DATE.Text=START_DATE.FormatType=dateSTART_DATE.Format=yyyy/MM/dd HH:mm:ssSTART_DATE.showDownButton=YtLabel_0.Type=TLabeltLabel_0.X=24tLabel_0.Y=35tLabel_0.Width=68tLabel_0.Height=15tLabel_0.Text=查询时间:tLabel_0.Color=蓝