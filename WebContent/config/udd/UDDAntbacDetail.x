## TBuilder Config File ## Title:## Company:JavaHis## Author:yanj 2013.03.21## version 1.0#<Type=TFrame>UI.Title=使用抗菌药物的出院患者明细UI.MenuConfig=%ROOT%\config\udd\UDDAntbacDetialMenu.xUI.Width=1485UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.udd.UDDAntDetailControlUI.item=tPanel_0;tPanel_2UI.layout=nullUI.AutoWidth=YUI.MenuMap=UI.FocusList=MR_NO;CASE_NO;PAT_NAME;UI.TopMenu=YUI.TopToolBar=YUI.ShowMenu=YtPanel_2.Type=TPaneltPanel_2.X=4tPanel_2.Y=91tPanel_2.Width=1475tPanel_2.Height=649tPanel_2.Border=组tPanel_2.AutoWidth=YtPanel_2.AutoHeight=YtPanel_2.Item=TABLETABLE.Type=TTableTABLE.X=5TABLE.Y=8TABLE.Width=754TABLE.Height=518TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoWidth=YTABLE.AutoX=NTABLE.AutoY=NTABLE.Header=区域,80;病案号,100;姓名,60;性别,40;年龄,40;住院号,100;出院科室,80;出院日期,80;住院天数,70;药品代码,80;药品名称,200;规格,60;数量,60,double,#########0.000;单位,60;零售价,60,double,#########0.0000;零售金额,60,double,#########0.00TABLE.ParmMap=REGION_CHN_DESC;MR_NO;NAME;SEX_CODE;GRADE;CASE_NO;DEPT_DESC;DS_DATE;DAYS;ORDER_CODE;ORDER_DESC;SPECIFICATION;SUM_QTY;UNIT_DESC;OWN_PRICE;SUM_AMTTABLE.AutoHeight=YTABLE.RowSelectionAllowed=YTABLE.Enabled=YTABLE.LockColumns=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;5,left;6,left;7,left;9,left;10,left;11,left;12,right;13,right;14,right;15,rightTABLE.ClickedAction=tPanel_0.Type=TPaneltPanel_0.X=3tPanel_0.Y=4tPanel_0.Width=1477tPanel_0.Height=81tPanel_0.Border=组tPanel_0.Opaque=NtPanel_0.Item=tLabel_4;ORDER_CODE;tLabel_6;START_DATE;tLabel_7;END_DATE;tLabel_8;ORDER_DESC;tLabel_0;tLabel_17;TOT;tLabel_20;MR_NO;NAME;REGION_CODE;DEPT_CODE;DS_FLGtPanel_0.AutoWidth=YDS_FLG.Type=TCheckBoxDS_FLG.X=876DS_FLG.Y=11DS_FLG.Width=81DS_FLG.Height=23DS_FLG.Text=出院带药DS_FLG.Color=蓝DEPT_CODE.Type=科室DEPT_CODE.X=737DEPT_CODE.Y=13DEPT_CODE.Width=115DEPT_CODE.Height=23DEPT_CODE.Text=DEPT_CODE.HorizontalAlignment=2DEPT_CODE.PopupMenuHeader=代码,100;名称,100DEPT_CODE.PopupMenuWidth=300DEPT_CODE.PopupMenuHeight=300DEPT_CODE.FormatType=comboDEPT_CODE.ShowDownButton=YDEPT_CODE.Tip=科室DEPT_CODE.ShowColumnList=NAMEDEPT_CODE.HisOneNullRow=YDEPT_CODE.ValueColumn=NAMEDEPT_CODE.FinalFlg=YDEPT_CODE.ClassIfy=0DEPT_CODE.IpdFitFlg=YREGION_CODE.Type=区域下拉列表REGION_CODE.X=65REGION_CODE.Y=15REGION_CODE.Width=112REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=YREGION_CODE.showPy2=YREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=NAME.Type=TTextFieldNAME.X=180NAME.Y=48NAME.Width=77NAME.Height=23NAME.Text=NAME.Enabled=NMR_NO.Type=TTextFieldMR_NO.X=64MR_NO.Y=48MR_NO.Width=114MR_NO.Height=23MR_NO.Text=MR_NO.Action=onMrNotLabel_20.Type=TLabeltLabel_20.X=11tLabel_20.Y=53tLabel_20.Width=58tLabel_20.Height=15tLabel_20.Text=病案号：tLabel_20.Color=蓝TOT.Type=TNumberTextFieldTOT.X=737TOT.Y=48TOT.Width=115TOT.Height=23TOT.Text=0TOT.Format=#########0.00tLabel_17.Type=TLabeltLabel_17.X=692tLabel_17.Y=53tLabel_17.Width=68tLabel_17.Height=15tLabel_17.Text=总计：tLabel_0.Type=TLabeltLabel_0.X=11tLabel_0.Y=19tLabel_0.Width=54tLabel_0.Height=15tLabel_0.Text=区域：tLabel_0.Color=蓝ORDER_DESC.Type=TTextFieldORDER_DESC.X=450ORDER_DESC.Y=48ORDER_DESC.Width=190ORDER_DESC.Height=23ORDER_DESC.Text=ORDER_DESC.Enabled=NtLabel_8.Type=TLabeltLabel_8.X=664tLabel_8.Y=18tLabel_8.Width=72tLabel_8.Height=15tLabel_8.Text=出院科室：tLabel_8.Color=蓝END_DATE.Type=TTextFormatEND_DATE.X=475END_DATE.Y=14END_DATE.Width=164END_DATE.Height=23END_DATE.Text=END_DATE.showDownButton=YEND_DATE.Format=yyyy/MM/dd HH:mm:ssEND_DATE.FormatType=dateEND_DATE.ShowColumnList=2,leftEND_DATE.HorizontalAlignment=2tLabel_7.Type=TLabeltLabel_7.X=453tLabel_7.Y=19tLabel_7.Width=17tLabel_7.Height=15tLabel_7.Text=至tLabel_7.Color=蓝START_DATE.Type=TTextFormatSTART_DATE.X=280START_DATE.Y=14START_DATE.Width=164START_DATE.Height=23START_DATE.Text=START_DATE.Color=黑START_DATE.showDownButton=YSTART_DATE.FormatType=dateSTART_DATE.Format=yyyy/MM/dd HH:mm:ssSTART_DATE.HorizontalAlignment=2tLabel_6.Type=TLabeltLabel_6.X=212tLabel_6.Y=19tLabel_6.Width=72tLabel_6.Height=15tLabel_6.Text=统计区间：tLabel_6.Color=蓝ORDER_CODE.Type=TTextFieldORDER_CODE.X=376ORDER_CODE.Y=48ORDER_CODE.Width=71ORDER_CODE.Height=23ORDER_CODE.Text=tLabel_4.Type=TLabeltLabel_4.X=302tLabel_4.Y=53tLabel_4.Width=72tLabel_4.Height=15tLabel_4.Text=药品代码：tLabel_4.Color=蓝