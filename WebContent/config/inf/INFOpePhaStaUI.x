## TBuilder Config File ## Title:## Company:JavaHis## Author:SHIBL 2014.02.28## version 1.0#<Type=TFrame>UI.Title=医院围手术期抗菌药物使用评价表UI.MenuConfig=%ROOT%\config\inf\INFOpePhaStaMenu.xUI.Width=102UI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.inf.INFOpePhaStaUIControlUI.item=tPanel_1;TABLEUI.layout=nullUI.TopToolBar=YUI.TopMenu=YTABLE.Type=TTableTABLE.X=14TABLE.Y=91TABLE.Width=81TABLE.Height=81TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=科室,120,DEPT_CODE;姓名,100;病历号,90;性别,60;年龄,90;手术名称,220;切口愈合等级,80,HEALTH_LEVEL;手术医师,90,OPE_CODE;手术开始时间,150;手术结束时间,150;麻醉方式,100,ANA_WAY;抗菌药物,320;未用,50;术前0.5-2h,90;术中追加,90;术后(24/48/72/>72h),120;说明,80  TABLE.ParmMap=OUT_DEPT;PAT_NAME;MR_NO;SEX;AGE;OP_DESC;HEALTH_LEVEL;MAIN_SUGEON;OP_DATE;OP_END_DATE;ANA_WAY;ORDER_DESC;NOT_USE;ANT_BEF0RE;ANT_CENTER;ANT_AFTER;EXPLAIN TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,left;17,left TABLE.Item=DEPT_CODE;HEALTH_LEVEL;OPE_CODE;ANA_WAY TABLE.LockColumns=ALLtPanel_1.Type=TPaneltPanel_1.X=14tPanel_1.Y=18tPanel_1.Width=1005tPanel_1.Height=69tPanel_1.Border=组tPanel_1.AutoWidth=YtPanel_1.Item=tLabel_6;S_DATE;tLabel_7;E_DATE;tLabel_8;DEPT_CODE;tLabel_9;OPES_DATE;tLabel_10;OPEE_DATE;tLabel_11;OPE_CODE;ANA_WAY;HEALTH_LEVELHEALTH_LEVEL.Type=愈合等级HEALTH_LEVEL.X=773HEALTH_LEVEL.Y=41HEALTH_LEVEL.Width=81HEALTH_LEVEL.Height=23HEALTH_LEVEL.Text=HEALTH_LEVEL.HorizontalAlignment=2HEALTH_LEVEL.PopupMenuHeader=代码,100;名称,100HEALTH_LEVEL.PopupMenuWidth=300HEALTH_LEVEL.PopupMenuHeight=300HEALTH_LEVEL.PopupMenuFilter=ID,1;NAME,1;PY1,1HEALTH_LEVEL.FormatType=comboHEALTH_LEVEL.ShowDownButton=YHEALTH_LEVEL.Tip=愈合等级HEALTH_LEVEL.ShowColumnList=NAMEHEALTH_LEVEL.Visible=NANA_WAY.Type=麻醉方式下拉区域ANA_WAY.X=633ANA_WAY.Y=40ANA_WAY.Width=81ANA_WAY.Height=23ANA_WAY.Text=ANA_WAY.HorizontalAlignment=2ANA_WAY.PopupMenuHeader=代码,100;名称,100ANA_WAY.PopupMenuWidth=300ANA_WAY.PopupMenuHeight=300ANA_WAY.PopupMenuFilter=ID,1;NAME,1;PY1,1ANA_WAY.FormatType=comboANA_WAY.ShowDownButton=YANA_WAY.Tip=麻醉方式下拉区域ANA_WAY.ShowColumnList=NAMEANA_WAY.Visible=NOPE_CODE.Type=人员OPE_CODE.X=744OPE_CODE.Y=9OPE_CODE.Width=133OPE_CODE.Height=23OPE_CODE.Text=OPE_CODE.HorizontalAlignment=2OPE_CODE.PopupMenuHeader=代码,100;名称,100OPE_CODE.PopupMenuWidth=300OPE_CODE.PopupMenuHeight=300OPE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1OPE_CODE.FormatType=comboOPE_CODE.ShowDownButton=YOPE_CODE.Tip=人员OPE_CODE.ShowColumnList=NAMEOPE_CODE.HisOneNullRow=YOPE_CODE.Dept=<DEPT_CODE>OPE_CODE.PosType=1OPE_CODE.Classify=1OPE_CODE.EndDateFlg=1tLabel_11.Type=TLabeltLabel_11.X=678tLabel_11.Y=13tLabel_11.Width=72tLabel_11.Height=15tLabel_11.Text=手术医师:tLabel_11.Color=蓝OPEE_DATE.Type=TTextFormatOPEE_DATE.X=282OPEE_DATE.Y=35OPEE_DATE.Width=170OPEE_DATE.Height=20OPEE_DATE.Text=OPEE_DATE.FormatType=dateOPEE_DATE.Format=yyyy/MM/dd HH:mm:ssOPEE_DATE.showDownButton=YtLabel_10.Type=TLabeltLabel_10.X=256tLabel_10.Y=37tLabel_10.Width=23tLabel_10.Height=15tLabel_10.Text=至tLabel_10.Color=蓝OPES_DATE.Type=TTextFormatOPES_DATE.X=84OPES_DATE.Y=36OPES_DATE.Width=168OPES_DATE.Height=20OPES_DATE.Text=OPES_DATE.FormatType=dateOPES_DATE.Format=yyyy/MM/dd HH:mm:ssOPES_DATE.showDownButton=YtLabel_9.Type=TLabeltLabel_9.X=14tLabel_9.Y=40tLabel_9.Width=72tLabel_9.Height=15tLabel_9.Text=手术日期:tLabel_9.Color=蓝DEPT_CODE.Type=科室DEPT_CODE.X=531DEPT_CODE.Y=10DEPT_CODE.Width=134DEPT_CODE.Height=23DEPT_CODE.Text=DEPT_CODE.HorizontalAlignment=2DEPT_CODE.PopupMenuHeader=代码,100;名称,100DEPT_CODE.PopupMenuWidth=300DEPT_CODE.PopupMenuHeight=300DEPT_CODE.FormatType=comboDEPT_CODE.ShowDownButton=YDEPT_CODE.Tip=科室DEPT_CODE.ShowColumnList=NAMEDEPT_CODE.HisOneNullRow=YDEPT_CODE.ClassIfy=0DEPT_CODE.Action=OPE_CODE|onQuery;tLabel_8.Type=TLabeltLabel_8.X=465tLabel_8.Y=13tLabel_8.Width=72tLabel_8.Height=15tLabel_8.Text=出院科室:tLabel_8.Color=蓝E_DATE.Type=TTextFormatE_DATE.X=281E_DATE.Y=11E_DATE.Width=170E_DATE.Height=20E_DATE.Text=TTextFormatE_DATE.FormatType=dateE_DATE.Format=yyyy/MM/dd HH:mm:ssE_DATE.showDownButton=YtLabel_7.Type=TLabeltLabel_7.X=256tLabel_7.Y=12tLabel_7.Width=21tLabel_7.Height=15tLabel_7.Text=至tLabel_7.Color=蓝S_DATE.Type=TTextFormatS_DATE.X=83S_DATE.Y=10S_DATE.Width=170S_DATE.Height=20S_DATE.Text=TTextFormatS_DATE.showDownButton=YS_DATE.FormatType=dateS_DATE.Format=yyyy/MM/dd HH:mm:sstLabel_6.Type=TLabeltLabel_6.X=17tLabel_6.Y=13tLabel_6.Width=68tLabel_6.Height=15tLabel_6.Text=出院日期:tLabel_6.Color=蓝