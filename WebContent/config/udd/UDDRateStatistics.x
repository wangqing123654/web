## TBuilder Config File ## Title:## Company:JavaHis## Author:yanjing 2013.08.05## version 1.0#<Type=TFrame>UI.Title=送检率统计UI.MenuConfig=%ROOT%\config\udd\UDDRateStatisticsMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.udd.UDDRateStatisticsControlUI.item=tPanel_3;TABLEPANEUI.layout=nullUI.TopMenu=YUI.TopToolBar=YTABLEPANE.Type=TTabbedPaneTABLEPANE.X=10TABLEPANE.Y=79TABLEPANE.Width=1009TABLEPANE.Height=664TABLEPANE.Item=tPanel_15;tPanel_16TABLEPANE.AutoWidth=YTABLEPANE.AutoHeight=YtPanel_16.Type=TPaneltPanel_16.X=39tPanel_16.Y=9tPanel_16.Width=516tPanel_16.Height=312tPanel_16.Name=门急诊tPanel_16.Item=tPanel_18;TABLE_MtPanel_16.AutoWidth=YtPanel_16.AutoHeight=YTABLE_M.Type=TTableTABLE_M.X=5TABLE_M.Y=64TABLE_M.Width=994TABLE_M.Height=566TABLE_M.SpacingRow=1TABLE_M.RowHeight=20TABLE_M.AutoWidth=YTABLE_M.AutoHeight=YTABLE_M.Header=院区,150;门急诊,100,PATIENT_STATE;科室,100,DEPT_CODE;医生,100,DR_CODE2;使用抗菌药物总例数,150,double,#######0;病原学检查送检例数,150,double,#######0;病原学检查百分率,110TABLE_M.LockColumns=allTABLE_M.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,right;5,right;6,rightTABLE_M.ParmMap=IN_STATION_CODE;ADM_TYPE;DEPT_CHN_DESC;USER_NAME;ALLNUM;SJ_NUM;STATIStPanel_18.Type=TPaneltPanel_18.X=5tPanel_18.Y=4tPanel_18.Width=994tPanel_18.Height=53tPanel_18.Border=组tPanel_18.AutoWidth=YtPanel_18.Item=tLabel_19;tLabel_20;CASE_CODE;DR_CODE2DR_CODE2.Type=门诊适用人员DR_CODE2.X=310DR_CODE2.Y=13DR_CODE2.Width=125DR_CODE2.Height=23DR_CODE2.Text=DR_CODE2.HorizontalAlignment=2DR_CODE2.PopupMenuHeader=代码,100;名称,100DR_CODE2.PopupMenuWidth=300DR_CODE2.PopupMenuHeight=300DR_CODE2.PopupMenuFilter=ID,1;NAME,1;PY1,1DR_CODE2.FormatType=comboDR_CODE2.ShowDownButton=YDR_CODE2.Tip=门诊适用人员DR_CODE2.ShowColumnList=NAMEDR_CODE2.HisOneNullRow=YCASE_CODE.Type=TComboBoxCASE_CODE.X=104CASE_CODE.Y=13CASE_CODE.Width=92CASE_CODE.Height=23CASE_CODE.Text=TButtonCASE_CODE.showID=YCASE_CODE.Editable=YCASE_CODE.StringData=[[id,text],[],['O','门诊'],['E','急诊']]CASE_CODE.TableShowList=textCASE_CODE.ShowName=YtLabel_20.Type=TLabeltLabel_20.X=263tLabel_20.Y=18tLabel_20.Width=43tLabel_20.Height=15tLabel_20.Text=医生：tLabel_20.Color=蓝tLabel_19.Type=TLabeltLabel_19.X=15tLabel_19.Y=18tLabel_19.Width=96tLabel_19.Height=15tLabel_19.Text=门急诊类别：tLabel_19.Color=蓝tPanel_15.Type=TPaneltPanel_15.X=57tPanel_15.Y=22tPanel_15.Width=516tPanel_15.Height=312tPanel_15.Name=住院tPanel_15.Item=tPanel_17;TABLE_ZtPanel_15.AutoWidth=YtPanel_15.AutoHeight=YTABLE_Z.Type=TTableTABLE_Z.X=6TABLE_Z.Y=57TABLE_Z.Width=993TABLE_Z.Height=573TABLE_Z.SpacingRow=1TABLE_Z.RowHeight=20TABLE_Z.AutoWidth=YTABLE_Z.AutoHeight=YTABLE_Z.Header=院区,150;住院状态,100,PATIENT_STATE;科室,100,DEPT_CODE;医生,100,DR_CODE1;使用抗菌药物总例数,180,double,#######0;病原学检查送检例数,180,double,#######0;病原学检查百分率,150TABLE_Z.LockColumns=allTABLE_Z.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,right;5,right;6,rightTABLE_Z.ParmMap=IN_STATION_CODE;DS_TYPE;DEPT_CHN_DESC;USER_NAME;ALLNUM;SJ_NUM;STATIStPanel_17.Type=TPaneltPanel_17.X=6tPanel_17.Y=4tPanel_17.Width=993tPanel_17.Height=46tPanel_17.Border=组tPanel_17.AutoWidth=YtPanel_17.Item=tLabel_10;tLabel_17;PATIENT_STATE;DEPT_CODE;DR_CODE1;tLabel_18tLabel_18.Type=TLabeltLabel_18.X=243tLabel_18.Y=17tLabel_18.Width=72tLabel_18.Height=15tLabel_18.Text=科室：tLabel_18.Color=蓝DR_CODE1.Type=人员DR_CODE1.X=528DR_CODE1.Y=14DR_CODE1.Width=99DR_CODE1.Height=23DR_CODE1.Text=DR_CODE1.HorizontalAlignment=2DR_CODE1.PopupMenuHeader=代码,100;名称,100DR_CODE1.PopupMenuWidth=300DR_CODE1.PopupMenuHeight=300DR_CODE1.PopupMenuFilter=ID,1;NAME,1;PY1,1DR_CODE1.FormatType=comboDR_CODE1.ShowDownButton=YDR_CODE1.Tip=人员DR_CODE1.ShowColumnList=NAMEDR_CODE1.HisOneNullRow=YDR_CODE1.Action=DR_CODE1.Dept=<DEPT_CODE>DR_CODE1.EndDateFlg=1DEPT_CODE.Type=科室DEPT_CODE.X=290DEPT_CODE.Y=13DEPT_CODE.Width=134DEPT_CODE.Height=23DEPT_CODE.Text=DEPT_CODE.HorizontalAlignment=2DEPT_CODE.PopupMenuHeader=代码,100;名称,100DEPT_CODE.PopupMenuWidth=300DEPT_CODE.PopupMenuHeight=300DEPT_CODE.FormatType=comboDEPT_CODE.ShowDownButton=YDEPT_CODE.Tip=科室DEPT_CODE.ShowColumnList=NAMEDEPT_CODE.HisOneNullRow=YDEPT_CODE.Action=DR_CODE1|onQueryDEPT_CODE.FinalFlg=DEPT_CODE.IpdFitFlg=YPATIENT_STATE.Type=TComboBoxPATIENT_STATE.X=91PATIENT_STATE.Y=14PATIENT_STATE.Width=120PATIENT_STATE.Height=23PATIENT_STATE.Text=TButtonPATIENT_STATE.showID=YPATIENT_STATE.Editable=YPATIENT_STATE.StringData=[[id,text],[],[1,'在院'],[2,'出院']]PATIENT_STATE.TableShowList=texttLabel_17.Type=TLabeltLabel_17.X=482tLabel_17.Y=18tLabel_17.Width=47tLabel_17.Height=15tLabel_17.Text=医生：tLabel_17.Color=蓝tLabel_10.Type=TLabeltLabel_10.X=15tLabel_10.Y=18tLabel_10.Width=72tLabel_10.Height=15tLabel_10.Text=住院状态：tLabel_10.Color=蓝tPanel_3.Type=TPaneltPanel_3.X=11tPanel_3.Y=11tPanel_3.Width=1008tPanel_3.Height=59tPanel_3.Border=组tPanel_3.AutoWidth=YtPanel_3.Item=tLabel_0;tLabel_2;START_DATE;tLabel_5;END_DATE;tLabel_6;USER_NAME;REGION_CODEREGION_CODE.Type=区域下拉列表REGION_CODE.X=60REGION_CODE.Y=18REGION_CODE.Width=135REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=YREGION_CODE.showPy2=YREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=REGION_CODE.Enabled=NUSER_NAME.Type=人员下拉列表USER_NAME.X=775USER_NAME.Y=16USER_NAME.Width=137USER_NAME.Height=23USER_NAME.Text=TButtonUSER_NAME.showID=YUSER_NAME.showName=YUSER_NAME.showText=NUSER_NAME.showValue=NUSER_NAME.showPy1=YUSER_NAME.showPy2=YUSER_NAME.Editable=YUSER_NAME.Tip=人员USER_NAME.TableShowList=nameUSER_NAME.ModuleParmString=USER_NAME.ModuleParmTag=USER_NAME.Enabled=NtLabel_6.Type=TLabeltLabel_6.X=702tLabel_6.Y=21tLabel_6.Width=72tLabel_6.Height=15tLabel_6.Text=操作人员：tLabel_6.Color=黑END_DATE.Type=TTextFormatEND_DATE.X=507END_DATE.Y=18END_DATE.Width=162END_DATE.Height=20END_DATE.Text=END_DATE.Format=yyyy/MM/dd HH:mm:ssEND_DATE.FormatType=dateEND_DATE.showDownButton=YtLabel_5.Type=TLabeltLabel_5.X=483tLabel_5.Y=21tLabel_5.Width=24tLabel_5.Height=15tLabel_5.Text=至tLabel_5.Color=蓝START_DATE.Type=TTextFormatSTART_DATE.X=316START_DATE.Y=18START_DATE.Width=153START_DATE.Height=20START_DATE.Text=START_DATE.showDownButton=YSTART_DATE.Format=yyyy/MM/dd HH:mm:ssSTART_DATE.FormatType=datetLabel_2.Type=TLabeltLabel_2.X=243tLabel_2.Y=23tLabel_2.Width=72tLabel_2.Height=15tLabel_2.Text=查询时间：tLabel_2.Color=蓝tLabel_0.Type=TLabeltLabel_0.X=15tLabel_0.Y=22tLabel_0.Width=50tLabel_0.Height=15tLabel_0.Text=区域：tLabel_0.Color=蓝