## TBuilder Config File ## Title:## Company:JavaHis## Author:陈红 2013.03.18## version 1.0#<Type=TFrame>UI.Title=放射科工作量统计UI.MenuConfig=%ROOT%\config\opb\OPBRadiologyDeptWorkMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.opb.OPBRadiologyDeptWorkControlUI.item=tPanel_2;tPanel_3UI.layout=nullUI.ShowTitle=NUI.TopMenu=YUI.TopToolBar=YUI.ShowMenu=NtPanel_3.Type=TPaneltPanel_3.X=6tPanel_3.Y=108tPanel_3.Width=997tPanel_3.Height=487tPanel_3.Border=组tPanel_3.Item=TTabletPanel_3.AutoWidth=YTTable.Type=TTableTTable.X=6TTable.Y=7TTable.Width=980TTable.Height=473TTable.SpacingRow=1TTable.RowHeight=20TTable.Header=区域,120,REGION_CODE;科室,110;病区,110;CR次数,110;CR合计,110;CT次数,110;CT合计,110;MR次数,110;MR合计,110TTable.ParmMap=REGION_CODE;DEPT_ABS_DESC;STATION_DESC;CRCOUNT;CRSUM;CTCOUNT;CTSUM;MRCOUNT;MRSUMTTable.AutoWidth=YTTable.Item=REGION_CODETTable.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,right;4,right;5,right;6,right;7,right;8,righttPanel_2.Type=TPaneltPanel_2.X=5tPanel_2.Y=5tPanel_2.Width=1014tPanel_2.Height=91tPanel_2.Border=组tPanel_2.Item=tLabel_0;START_DATE;END_DATE;tLabel_1;REGION_CODE;tLabel_12tPanel_2.AutoWidth=YtPanel_2.AutoX=YtPanel_2.AutoY=YtLabel_12.Type=TLabeltLabel_12.X=25tLabel_12.Y=34tLabel_12.Width=45tLabel_12.Height=15tLabel_12.Text=区域：tLabel_12.Color=蓝REGION_CODE.Type=区域下拉列表REGION_CODE.X=68REGION_CODE.Y=30REGION_CODE.Width=135REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=YREGION_CODE.showPy2=YREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=REGION_CODE.Visible=YREGION_CODE.Enabled=NtLabel_1.Type=TLabeltLabel_1.X=614tLabel_1.Y=35tLabel_1.Width=22tLabel_1.Height=15tLabel_1.Text=～END_DATE.Type=TTextFormatEND_DATE.X=637END_DATE.Y=30END_DATE.Width=187END_DATE.Height=24END_DATE.Text=END_DATE.Format=yyyy/MM/dd HH:mm:ssEND_DATE.FormatType=dateEND_DATE.showDownButton=YSTART_DATE.Type=TTextFormatSTART_DATE.X=418START_DATE.Y=30START_DATE.Width=187START_DATE.Height=24START_DATE.Text=START_DATE.FormatType=dateSTART_DATE.Format=yyyy/MM/dd HH:mm:ssSTART_DATE.showDownButton=YtLabel_0.Type=TLabeltLabel_0.X=348tLabel_0.Y=34tLabel_0.Width=72tLabel_0.Height=15tLabel_0.Text=查询区间：tLabel_0.Color=蓝