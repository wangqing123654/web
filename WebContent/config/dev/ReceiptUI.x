## TBuilder Config File ## Title:## Company:JavaHis## Author:sundx 2009.10.30## version 1.0#<Type=TFrame>UI.Title=生成入库单UI.MenuConfig=%ROOT%\config\dev\ReceiptMenu.xUI.Width=361UI.Height=544UI.toolbar=YUI.controlclassname=com.javahis.ui.dev.RecceiptControlUI.item=tPanel_1;TABLEUI.layout=nullUI.TopMenu=YUI.TopToolBar=YTABLE.Type=TTableTABLE.X=6TABLE.Y=159TABLE.Width=350TABLE.Height=379TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.Header=验收日期,100;验收单号,100;供应厂商,200,SUP_CODE;验收科室,100,RECEIPT_DEPT;发票号码,100;验收人员,100,RECEIPT_USERTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.LockColumns=0,1,2,3,4,5TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,leftTABLE.Item=SUP_CODE;RECEIPT_DEPT;RECEIPT_USERTABLE.ParmMap=RECEIPT_DATE;RECEIPT_NO;SUP_CODE;RECEIPT_DEPT;INVOICE_NO;RECEIPT_USERTABLE.ClickedAction=TABLE.DoubleClickedAction=onGenerateReceiptTABLE.FocusType=2tPanel_1.Type=TPaneltPanel_1.X=6tPanel_1.Y=8tPanel_1.Width=350tPanel_1.Height=147tPanel_1.Border=组tPanel_1.Item=tLabel_4;RECEIPT_NO;tLabel_5;INVOICE_NO;tLabel_6;RECEIPT_DATE_BEGIN;tLabel_7;RECEIPT_DATE_END;tLabel_8;tLabel_9;RECEIPT_USER;tLabel_10;SUP_CODE;RECEIPT_DEPTtPanel_1.AutoWidth=YRECEIPT_DEPT.Type=设备科室下拉区域RECEIPT_DEPT.X=73RECEIPT_DEPT.Y=71RECEIPT_DEPT.Width=95RECEIPT_DEPT.Height=23RECEIPT_DEPT.Text=RECEIPT_DEPT.HorizontalAlignment=2RECEIPT_DEPT.PopupMenuHeader=代码,100;名称,100RECEIPT_DEPT.PopupMenuWidth=300RECEIPT_DEPT.PopupMenuHeight=300RECEIPT_DEPT.PopupMenuFilter=ID,1;NAME,1;PY1,1RECEIPT_DEPT.FormatType=comboRECEIPT_DEPT.ShowDownButton=YRECEIPT_DEPT.Tip= 设备部门RECEIPT_DEPT.ShowColumnList=NAMESUP_CODE.Type=供应厂商SUP_CODE.X=73SUP_CODE.Y=105SUP_CODE.Width=262SUP_CODE.Height=23SUP_CODE.Text=SUP_CODE.HorizontalAlignment=2SUP_CODE.PopupMenuHeader=ID,100;NAME,100SUP_CODE.PopupMenuWidth=300SUP_CODE.PopupMenuHeight=300SUP_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1SUP_CODE.FormatType=comboSUP_CODE.ShowDownButton=YSUP_CODE.Tip=供应厂商SUP_CODE.ShowColumnList=NAMEtLabel_10.Type=TLabeltLabel_10.X=11tLabel_10.Y=110tLabel_10.Width=60tLabel_10.Height=15tLabel_10.Text=供应厂商RECEIPT_USER.Type=人员RECEIPT_USER.X=236RECEIPT_USER.Y=72RECEIPT_USER.Width=99RECEIPT_USER.Height=23RECEIPT_USER.Text=RECEIPT_USER.HorizontalAlignment=2RECEIPT_USER.PopupMenuHeader=ID,100;NAME,100RECEIPT_USER.PopupMenuWidth=300RECEIPT_USER.PopupMenuHeight=300RECEIPT_USER.PopupMenuFilter=ID,1;NAME,1;PY1,1RECEIPT_USER.FormatType=comboRECEIPT_USER.ShowDownButton=YRECEIPT_USER.Tip=人员RECEIPT_USER.ShowColumnList=NAMERECEIPT_USER.Dept=<RECEIPT_DEPT>tLabel_9.Type=TLabeltLabel_9.X=174tLabel_9.Y=74tLabel_9.Width=58tLabel_9.Height=15tLabel_9.Text=验收人员tLabel_8.Type=TLabeltLabel_8.X=11tLabel_8.Y=76tLabel_8.Width=62tLabel_8.Height=15tLabel_8.Text=验收科室RECEIPT_DATE_END.Type=TTextFormatRECEIPT_DATE_END.X=236RECEIPT_DATE_END.Y=39RECEIPT_DATE_END.Width=99RECEIPT_DATE_END.Height=20RECEIPT_DATE_END.Text=RECEIPT_DATE_END.Format=yyyy/MM/ddRECEIPT_DATE_END.FormatType=dateRECEIPT_DATE_END.showDownButton=YtLabel_7.Type=TLabeltLabel_7.X=189tLabel_7.Y=42tLabel_7.Width=16tLabel_7.Height=15tLabel_7.Text=至RECEIPT_DATE_BEGIN.Type=TTextFormatRECEIPT_DATE_BEGIN.X=73RECEIPT_DATE_BEGIN.Y=38RECEIPT_DATE_BEGIN.Width=96RECEIPT_DATE_BEGIN.Height=20RECEIPT_DATE_BEGIN.Text=RECEIPT_DATE_BEGIN.Format=yyyy/MM/ddRECEIPT_DATE_BEGIN.FormatType=dateRECEIPT_DATE_BEGIN.showDownButton=YtLabel_6.Type=TLabeltLabel_6.X=11tLabel_6.Y=42tLabel_6.Width=60tLabel_6.Height=15tLabel_6.Text=验收日期INVOICE_NO.Type=TTextFieldINVOICE_NO.X=235INVOICE_NO.Y=8INVOICE_NO.Width=100INVOICE_NO.Height=20INVOICE_NO.Text=tLabel_5.Type=TLabeltLabel_5.X=170tLabel_5.Y=10tLabel_5.Width=65tLabel_5.Height=15tLabel_5.Text=发票号码RECEIPT_NO.Type=TTextFieldRECEIPT_NO.X=72RECEIPT_NO.Y=7RECEIPT_NO.Width=96RECEIPT_NO.Height=20RECEIPT_NO.Text=RECEIPT_NO.Action=onQuerytLabel_4.Type=TLabeltLabel_4.X=11tLabel_4.Y=10tLabel_4.Width=63tLabel_4.Height=15tLabel_4.Text=验收单号