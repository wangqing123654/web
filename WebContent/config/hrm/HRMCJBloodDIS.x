## TBuilder Config File ## Title:## Company:JavaHis## Author:zgc 2010.05.17## version 1.0#<Type=TFrame>UI.Title=检验检查详细统计UI.MenuConfig=UI.Width=638UI.Height=500UI.toolbar=YUI.controlclassname=com.javahis.ui.hrm.HRMLisUI.item=tPanel_0;tblDisUI.layout=nulltblDis.Type=TTabletblDis.X=4tblDis.Y=56tblDis.Width=628tblDis.Height=438tblDis.SpacingRow=1tblDis.RowHeight=20tblDis.AutoHeight=YtblDis.AutoWidth=YtblDis.AutoX=YtblDis.Header=检查检查项目,200;人数,200tblDis.Visible=YtblDis.ParmMap=ORDER_DESC;COUNT(CASE_NO)tblDis.LockColumns=alltblDis.ColumnHorizontalAlignmentData=0,left;1,righttPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=10tPanel_0.Width=628tPanel_0.Height=41tPanel_0.AutoWidth=YtPanel_0.AutoHeight=NtPanel_0.Border=组tPanel_0.Item=tLabel_2;tButton_0;tButton_1;tButton_2;StartDate;EndDate;tLabel_0tLabel_0.Type=TLabeltLabel_0.X=7tLabel_0.Y=16tLabel_0.Width=72tLabel_0.Height=15tLabel_0.Text=时间段：EndDate.Type=TTextFormatEndDate.X=175EndDate.Y=13EndDate.Width=77EndDate.Height=20EndDate.Text=EndDate.Enabled=NStartDate.Type=TTextFormatStartDate.X=58StartDate.Y=13StartDate.Width=77StartDate.Height=20StartDate.Text=StartDate.Enabled=NtButton_2.Type=TButtontButton_2.X=516tButton_2.Y=12tButton_2.Width=81tButton_2.Height=23tButton_2.Text=关闭tButton_2.Action=onClosetButton_1.Type=TButtontButton_1.X=388tButton_1.Y=12tButton_1.Width=97tButton_1.Height=23tButton_1.Text=汇出ExceltButton_1.Action=onExportDistButton_1.ControlClassName=com.javahis.ui.hrm.HRMCJBloodstatisticsControltButton_0.Type=TButtontButton_0.X=277tButton_0.Y=12tButton_0.Width=81tButton_0.Height=23tButton_0.Text=打印tButton_0.Key=tButton_0.Action=onPrintDistButton_0.ControlClassName=com.javahis.ui.hrm.HRMCJBloodstatisticsControltLabel_2.Type=TLabeltLabel_2.X=143tLabel_2.Y=14tLabel_2.Width=23tLabel_2.Height=19tLabel_2.Text=至