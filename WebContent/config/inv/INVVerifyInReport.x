## TBuilder Config File ## Title:物资验收入库统计表## Company:JavaHis## Author:zhangh 2013.05.03## version 1.0#<Type=TFrame>UI.Title=验收入库统计查询报表UI.MenuConfig=%ROOT%\config\inv\INVVerifyInReportMenu.xUI.Width=1075UI.Height=641UI.toolbar=YUI.controlclassname=com.javahis.ui.inv.INVVerifyInReportControlUI.item=TABLE;tPanel_2UI.layout=nullUI.TopMenu=YUI.TopToolBar=YUI.X=5UI.AutoX=YUI.Y=5UI.AutoY=YUI.AutoWidth=YUI.AutoHeight=NUI.AutoW=NUI.AutoH=NtPanel_2.Type=TPaneltPanel_2.X=10tPanel_2.Y=5tPanel_2.Width=1060tPanel_2.Height=108tPanel_2.Border=组tPanel_2.AutoY=YtPanel_2.AutoWidth=YtPanel_2.Item=tLabel_2;INV_CODE;tLabel_4;tLabel_1;tLabel_9;tLabel_10;START_TIME;END_TIME;ORG_CODE;SUP_CODE;INV_DESC;UNITUNIT.Type=计量单位下拉列表UNIT.X=534UNIT.Y=71UNIT.Width=81UNIT.Height=23UNIT.Text=TButtonUNIT.showID=YUNIT.showName=YUNIT.showText=NUNIT.showValue=NUNIT.showPy1=YUNIT.showPy2=YUNIT.Editable=YUNIT.Tip=计量单位UNIT.TableShowList=nameUNIT.Enabled=NUNIT.Visible=NINV_DESC.Type=TTextFieldINV_DESC.X=193INV_DESC.Y=73INV_DESC.Width=334INV_DESC.Height=20INV_DESC.Text=INV_DESC.Enabled=NSUP_CODE.Type=供应厂商SUP_CODE.X=665SUP_CODE.Y=28SUP_CODE.Width=125SUP_CODE.Height=21SUP_CODE.Text=SUP_CODE.HorizontalAlignment=2SUP_CODE.PopupMenuHeader=代码,100;名称,100SUP_CODE.PopupMenuWidth=300SUP_CODE.PopupMenuHeight=300SUP_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1SUP_CODE.FormatType=comboSUP_CODE.ShowDownButton=YSUP_CODE.Tip=供应厂商SUP_CODE.ShowColumnList=NAMESUP_CODE.HisOneNullRow=YORG_CODE.Type=物资部门下拉区域ORG_CODE.X=480ORG_CODE.Y=28ORG_CODE.Width=114ORG_CODE.Height=21ORG_CODE.Text=ORG_CODE.HorizontalAlignment=2ORG_CODE.PopupMenuHeader=代码,100;名称,100ORG_CODE.PopupMenuWidth=300ORG_CODE.PopupMenuHeight=300ORG_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1ORG_CODE.FormatType=comboORG_CODE.ShowDownButton=YORG_CODE.Tip=物资部门ORG_CODE.ShowColumnList=NAMEORG_CODE.Enabled=YORG_CODE.HisOneNullRow=YEND_TIME.Type=TTextFormatEND_TIME.X=261END_TIME.Y=28END_TIME.Width=152END_TIME.Height=21END_TIME.Text=END_TIME.FormatType=dateEND_TIME.Format=yyyy/MM/dd HH:mm:ssEND_TIME.showDownButton=YEND_TIME.HorizontalAlignment=2START_TIME.Type=TTextFormatSTART_TIME.X=73START_TIME.Y=28START_TIME.Width=160START_TIME.Height=21START_TIME.Text=START_TIME.Format=yyyy/MM/dd HH:mm:ssSTART_TIME.FormatType=dateSTART_TIME.showDownButton=YSTART_TIME.HorizontalAlignment=2tLabel_10.Type=TLabeltLabel_10.X=417tLabel_10.Y=28tLabel_10.Width=71tLabel_10.Height=21tLabel_10.Text=验收部门：tLabel_10.Color=蓝tLabel_9.Type=TLabeltLabel_9.X=240tLabel_9.Y=28tLabel_9.Width=17tLabel_9.Height=21tLabel_9.Text=至tLabel_9.Color=蓝tLabel_1.Type=TLabeltLabel_1.X=6tLabel_1.Y=28tLabel_1.Width=70tLabel_1.Height=21tLabel_1.Text=统计区间：tLabel_1.Color=蓝tLabel_4.Type=TLabeltLabel_4.X=598tLabel_4.Y=28tLabel_4.Width=71tLabel_4.Height=21tLabel_4.Text=供应厂商：tLabel_4.Color=蓝INV_CODE.Type=TTextFieldINV_CODE.X=73INV_CODE.Y=73INV_CODE.Width=115INV_CODE.Height=20INV_CODE.Text=INV_CODE.Action=onFiltertLabel_2.Type=TLabeltLabel_2.X=6tLabel_2.Y=75tLabel_2.Width=66tLabel_2.Height=15tLabel_2.Text=物质代码:tLabel_2.Color=蓝TABLE.Type=TTableTABLE.X=9TABLE.Y=117TABLE.Width=1059TABLE.Height=511TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoHeight=YTABLE.AutoWidth=YTABLE.Header=验收部门,140;供应厂商,140;验收日期,110;物质代码,100;序号,50;名称,200;规格,140;购入数,70;赠送数,70;单位,70;购入价,80;购入金额,80;效期,110TABLE.ParmMap=ORG_DESC;SUP_ABS_DESC;VERIFYIN_DATE;INV_CODE;INVSEQ_NO;INV_CHN_DESC;DESCRIPTION;QTY;GIFT_QTY;UNIT_CHN_DESC;UNIT_PRICE;IN_PRICE;VALID_DATETABLE.AutoModifyDataStore=NTABLE.SQL=TABLE.Enabled=NTABLE.LockColumns=ALLTABLE.DoubleClickedAction=TABLE.AutoX=YTABLE.AutoY=NTABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,right;4,left;5,left;6,right;7,right;8,left;9,right;10,right;11,left;12,leftTABLE.Item=