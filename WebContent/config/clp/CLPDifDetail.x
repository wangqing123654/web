## TBuilder Config File ## Title:## Company:JavaHis## Author:shibaoliu 2011.07.08## version 1.0#<Type=TFrame>UI.Title=UI.MenuConfig=%ROOT%\config\clp\CLPDifDetailMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.clp.CLPDifDetailControlUI.item=tPanel_0;CLPTABLEUI.layout=nullUI.zhTitle=临床路径差异细表UI.TopMenu=YUI.TopToolBar=YUI.ShowTitle=NUI.ShowMenu=YCLPTABLE.Type=TTableCLPTABLE.X=5CLPTABLE.Y=88CLPTABLE.Width=1206CLPTABLE.Height=644CLPTABLE.SpacingRow=1CLPTABLE.RowHeight=20CLPTABLE.AutoWidth=YCLPTABLE.AutoHeight=YCLPTABLE.Header=区域,100,REGION_CODE;临床路径,150;病案号,100;住院号,100;付款方式,90;科室,120;病区,100;查核人员,120;标准项目,190;标准数量,80;标准费用,80;实际项目,190;实际数量,80;实际费用,100;数量差额,100;费用差额,100;变异类别,100;变异原因,100CLPTABLE.ParmMap=REGION_CODE;CLNCPATH_CHN_DESC;MR_NO;IPD_NO;CTZ_DESC;DEPT_CHN_DESC;STATION_DESC;USER_NAME;ORDER_DESC;TOT;FEE_STAND;ORDER_DESC_1;MAINTOT;FEE_REAL;REAL_TOT;FEE_DIFF;MONCAT_CHN_DESC;VARIANCE_CHN_DESCCLPTABLE.LockColumns=allCLPTABLE.HorizontalAlignmentData=CLPTABLE.ColumnHorizontalAlignmentData= 0,left;1,left;2,left;3,left;4,left;5,left;6,right;7,left;8,left;9,right;10,right;11,left;12,right;13,right;14,right;15,right;16,left;17,leftCLPTABLE.ColumnSelectionAllowed=YCLPTABLE.Item=REGION_CODEtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=81tPanel_0.Border=组|查询条件tPanel_0.Item=tLabel_1;START_DATE;tLabel_2;END_DATE;REGION_CODEtPanel_0.AutoWidth=YtPanel_0.MenuConfig=tPanel_0.ControlClassName=com.javahis.ui.clp.CLPDifDetailControlREGION_CODE.Type=区域下拉列表REGION_CODE.X=305REGION_CODE.Y=158REGION_CODE.Width=81REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=YREGION_CODE.showPy2=YREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=END_DATE.Type=TTextFormatEND_DATE.X=382END_DATE.Y=23END_DATE.Width=195END_DATE.Height=20END_DATE.Text=END_DATE.FormatType=dateEND_DATE.Format=yyyy/MM/dd HH:mm:ssEND_DATE.showDownButton=YtLabel_2.Type=TLabeltLabel_2.X=294tLabel_2.Y=26tLabel_2.Width=72tLabel_2.Height=15tLabel_2.Text=截止时间START_DATE.Type=TTextFormatSTART_DATE.X=95START_DATE.Y=24START_DATE.Width=187START_DATE.Height=20START_DATE.Text=START_DATE.FormatType=dateSTART_DATE.Format=yyyy/MM/dd HH:mm:ssSTART_DATE.showDownButton=YtLabel_1.Type=TLabeltLabel_1.X=33tLabel_1.Y=29tLabel_1.Width=70tLabel_1.Height=15tLabel_1.Text=开始时间