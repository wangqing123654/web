## TBuilder Config File ## Title:## Company:JavaHis## Author:caoy 2013.09.16## version 1.0#<Type=TFrame>UI.Title=会诊次数统计表UI.MenuConfig=%ROOT%\config\inp\INP_ConsNumberMenu.xUI.Width=1490UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.inp.INP_ConsNumberControlUI.item=tPanel_0;tPanel_1UI.layout=nullUI.X=5UI.AutoX=YUI.Y=5UI.AutoY=YUI.AutoWidth=YUI.TopMenu=YUI.TopToolBar=YUI.ShowMenu=YUI.ShowTitle=NtPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=88tPanel_1.Width=1480tPanel_1.Height=651tPanel_1.Border=组|会诊次数统计数据tPanel_1.AutoX=NtPanel_1.AutoY=NtPanel_1.AutoWidth=YtPanel_1.Item=TABLETABLE.Type=TTableTABLE.X=15TABLE.Y=26TABLE.Width=1452TABLE.Height=613TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoY=YTABLE.AutoHeight=YTABLE.AutoWidth=YTABLE.Header=区域,100,REGION_CODE;科室,100,DEPT_CODE;医生,100,DR_CODE;病案号,110;病患名称,110;累计次数,100TABLE.ParmMap=REGION_CODE;DEPT_CODE;DR_CODE;IND_NO;PATH_NAME;SUMNUMBERTABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,rightTABLE.LockColumns=ALLTABLE.Item=REGION_CODE;DEPT_CODE;DR_CODEtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1480tPanel_0.Height=81tPanel_0.Border=组|查询条件tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.Item=tLabel_0;REGION_CODE;tLabel_1;DEPT_CODE;tLabel_2;DR_CODEDR_CODE.Type=人员DR_CODE.X=444DR_CODE.Y=31DR_CODE.Width=112DR_CODE.Height=23DR_CODE.Text=DR_CODE.HorizontalAlignment=2DR_CODE.PopupMenuHeader=代码,100;名称,100DR_CODE.PopupMenuWidth=300DR_CODE.PopupMenuHeight=300DR_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DR_CODE.FormatType=comboDR_CODE.ShowDownButton=YDR_CODE.Tip=人员DR_CODE.ShowColumnList=NAMEDR_CODE.HisOneNullRow=YDR_CODE.IpdFitFlg=YDR_CODE.Dept=<DEPT_CODE>DR_CODE.Classify=DR_CODE.PosType=1DR_CODE.EndDateFlg=1tLabel_2.Type=TLabeltLabel_2.X=400tLabel_2.Y=34tLabel_2.Width=44tLabel_2.Height=15tLabel_2.Text=医生：tLabel_2.Color=blueDEPT_CODE.Type=科室DEPT_CODE.X=253DEPT_CODE.Y=31DEPT_CODE.Width=127DEPT_CODE.Height=23DEPT_CODE.Text=DEPT_CODE.HorizontalAlignment=2DEPT_CODE.PopupMenuHeader=代码,100;名称,100DEPT_CODE.PopupMenuWidth=300DEPT_CODE.PopupMenuHeight=300DEPT_CODE.FormatType=comboDEPT_CODE.ShowDownButton=YDEPT_CODE.Tip=科室DEPT_CODE.ShowColumnList=NAMEDEPT_CODE.HisOneNullRow=YDEPT_CODE.IpdFitFlg=YDEPT_CODE.Action=DR_CODE|onQueryDEPT_CODE.ClassIfy=0tLabel_1.Type=TLabeltLabel_1.X=210tLabel_1.Y=34tLabel_1.Width=45tLabel_1.Height=15tLabel_1.Text=科室：tLabel_1.Color=blueREGION_CODE.Type=区域下拉列表REGION_CODE.X=73REGION_CODE.Y=31REGION_CODE.Width=118REGION_CODE.Height=23REGION_CODE.Text=TButtonREGION_CODE.showID=YREGION_CODE.showName=YREGION_CODE.showText=NREGION_CODE.showValue=NREGION_CODE.showPy1=NREGION_CODE.showPy2=NREGION_CODE.Editable=YREGION_CODE.Tip=区域REGION_CODE.TableShowList=nameREGION_CODE.ModuleParmString=REGION_CODE.ModuleParmTag=tLabel_0.Type=TLabeltLabel_0.X=30tLabel_0.Y=34tLabel_0.Width=43tLabel_0.Height=15tLabel_0.Text=区域：tLabel_0.Color=blue