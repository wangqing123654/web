<Type=TFrame>UI.Title=出院病患医疗费用总表UI.MenuConfig=%ROOT%\config\bil\BILOutHospTotWordMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.bil.BILOutHospTotalWordControlUI.item=tPanel_1;tPanel_2UI.layout=nullUI.FocusList=UI.X=0UI.Y=10UI.TopMenu=YUI.TopToolBar=YUI.ShowTitle=NUI.AutoX=NUI.AutoY=NUI.AutoWidth=NUI.AutoHeight=NUI.AutoSize=0UI.Border=凸tPanel_2.Type=TPaneltPanel_2.X=7tPanel_2.Y=198tPanel_2.Width=1010tPanel_2.Height=543tPanel_2.Border=组|tPanel_2.AutoX=YtPanel_2.AutoWidth=YtPanel_2.AutoHeight=YtPanel_2.Item=TableTable.Type=TTableTable.X=158Table.Y=12Table.Width=81Table.Height=521Table.SpacingRow=1Table.RowHeight=20Table.AutoX=YTable.AutoY=YTable.AutoWidth=YTable.AutoHeight=YTable.Header=科室名称,100;病区,120;合计金额,150;CHARGE01,120;CHARGE02,120;CHARGE03,120;CHARGE04,120;CHARGE05,120;CHARGE06,120;CHARGE07,120;CHARGE08,120;CHARGE09,120;CHARGE10,120;CHARGE11,120;CHARGE12,120;CHARGE13,120;CHARGE14,120;CHARGE15,120;CHARGE16,120;CHARGE17,120;CHARGE18,120;CHARGE19,120;CHARGE20,120;CHARGE21,120Table.ParmMap=DEPT_CHN_DESC;STATION_DESC;TOT_AMT;CHARGE01;CHARGE02;CHARGE03;CHARGE04;CHARGE05;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE10;CHARGE11;CHARGE12;CHARGE13;CHARGE14;CHARGE15;CHARGE16;CHARGE17;CHARGE18;CHARGE19;CHARGE20;CHARGE21Table.ColumnHorizontalAlignmentData=0,left;1,left;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,rightTable.LockColumns=alltPanel_1.Type=TPaneltPanel_1.X=7tPanel_1.Y=7tPanel_1.Width=1010tPanel_1.Height=187tPanel_1.Border=组|查询条件tPanel_1.AutoX=YtPanel_1.AutoY=YtPanel_1.AutoWidth=YtPanel_1.Item=tLabel_19;S_DATE;E_DATE;tLabel_20;tLabel_0;tLabel_1;tRadioButton_0;tRadioButton_1;tRadioButton_2;CTZ_CODE;tRadioButton_3;tRadioButton_4;tRadioButton_5;DEPT_CODE;STATION_CODE;tLabel_2;BILL_DATE;tCheckBox_0;tRadioButton_6;LUMPWORK_CODE;tRadioButton_7tPanel_1.AutoHeight=NtRadioButton_7.Type=TRadioButtontRadioButton_7.X=876tRadioButton_7.Y=89tRadioButton_7.Width=67tRadioButton_7.Height=23tRadioButton_7.Text=非套餐tRadioButton_7.Group=3tRadioButton_7.Enabled=NtRadioButton_7.Color=蓝tRadioButton_7.Action=onCheckBoxtRadioButton_7.Selected=YLUMPWORK_CODE.Type=包干套餐下拉区域LUMPWORK_CODE.X=645LUMPWORK_CODE.Y=89LUMPWORK_CODE.Width=211LUMPWORK_CODE.Height=22LUMPWORK_CODE.Text=LUMPWORK_CODE.HorizontalAlignment=2LUMPWORK_CODE.PopupMenuHeader=代码,43;名称,395;金额,75LUMPWORK_CODE.PopupMenuWidth=519LUMPWORK_CODE.PopupMenuHeight=300LUMPWORK_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1LUMPWORK_CODE.FormatType=comboLUMPWORK_CODE.ShowDownButton=YLUMPWORK_CODE.Tip=包干套餐LUMPWORK_CODE.ShowColumnList=NAMELUMPWORK_CODE.Enabled=NLUMPWORK_CODE.HisOneNullRow=YLUMPWORK_CODE.AdmType=ItRadioButton_6.Type=TRadioButtontRadioButton_6.X=590tRadioButton_6.Y=89tRadioButton_6.Width=55tRadioButton_6.Height=23tRadioButton_6.Text=套餐tRadioButton_6.Group=3tRadioButton_6.Color=蓝tRadioButton_6.Action=onCheckBox|LUMPWORKtRadioButton_6.Enabled=NtCheckBox_0.Type=TCheckBoxtCheckBox_0.X=501tCheckBox_0.Y=89tCheckBox_0.Width=88tCheckBox_0.Height=23tCheckBox_0.Text=有无套餐:tCheckBox_0.Color=蓝tCheckBox_0.Action=onCheckBoxBILL_DATE.Type=TTextFormatBILL_DATE.X=456BILL_DATE.Y=41BILL_DATE.Width=110BILL_DATE.Height=20BILL_DATE.Text=TTextFormatBILL_DATE.showDownButton=YBILL_DATE.Format=yyyy/MM/ddBILL_DATE.FormatType=datetLabel_2.Type=TLabeltLabel_2.X=374tLabel_2.Y=44tLabel_2.Width=72tLabel_2.Height=15tLabel_2.Text=切帐日期:tLabel_2.Color=蓝STATION_CODE.Type=病区STATION_CODE.X=857STATION_CODE.Y=40STATION_CODE.Width=118STATION_CODE.Height=23STATION_CODE.Text=STATION_CODE.HorizontalAlignment=2STATION_CODE.PopupMenuHeader=代码,100;名称,100STATION_CODE.PopupMenuWidth=300STATION_CODE.PopupMenuHeight=300STATION_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1STATION_CODE.FormatType=comboSTATION_CODE.ShowDownButton=YSTATION_CODE.Tip=病区STATION_CODE.ShowColumnList=NAMESTATION_CODE.HisOneNullRow=YSTATION_CODE.DeptCode=<DEPT_CODE>DEPT_CODE.Type=科室DEPT_CODE.X=656DEPT_CODE.Y=40DEPT_CODE.Width=122DEPT_CODE.Height=23DEPT_CODE.Text=DEPT_CODE.HorizontalAlignment=2DEPT_CODE.PopupMenuHeader=代码,100;名称,100DEPT_CODE.PopupMenuWidth=300DEPT_CODE.PopupMenuHeight=300DEPT_CODE.FormatType=comboDEPT_CODE.ShowDownButton=YDEPT_CODE.Tip=科室DEPT_CODE.ShowColumnList=NAMEDEPT_CODE.HisOneNullRow=YDEPT_CODE.FinalFlg=YDEPT_CODE.ClassIfy=0DEPT_CODE.Action=onDEPTtRadioButton_5.Type=TRadioButtontRadioButton_5.X=269tRadioButton_5.Y=130tRadioButton_5.Width=81tRadioButton_5.Height=23tRadioButton_5.Text=未结转tRadioButton_5.Group=2tRadioButton_5.Color=蓝tRadioButton_4.Type=TRadioButtontRadioButton_4.X=149tRadioButton_4.Y=130tRadioButton_4.Width=81tRadioButton_4.Height=23tRadioButton_4.Text=已结转tRadioButton_4.Group=2tRadioButton_4.Color=蓝tRadioButton_3.Type=TRadioButtontRadioButton_3.X=42tRadioButton_3.Y=130tRadioButton_3.Width=81tRadioButton_3.Height=23tRadioButton_3.Text=全部tRadioButton_3.Group=2tRadioButton_3.Selected=YtRadioButton_3.Color=蓝CTZ_CODE.Type=身份折扣下拉列表CTZ_CODE.X=330CTZ_CODE.Y=89CTZ_CODE.Width=99CTZ_CODE.Height=23CTZ_CODE.Text=TButtonCTZ_CODE.showID=YCTZ_CODE.showName=YCTZ_CODE.showText=NCTZ_CODE.showValue=NCTZ_CODE.showPy1=NCTZ_CODE.showPy2=NCTZ_CODE.Editable=YCTZ_CODE.Tip=主身份CTZ_CODE.TableShowList=nameCTZ_CODE.MainCtzFlg=CTZ_CODE.NhiCtzFlg=YCTZ_CODE.Enabled=NtRadioButton_2.Type=TRadioButtontRadioButton_2.X=269tRadioButton_2.Y=89tRadioButton_2.Width=55tRadioButton_2.Height=23tRadioButton_2.Text=医保tRadioButton_2.Group=1tRadioButton_2.Action=onRodio|CTZtRadioButton_2.Color=蓝tRadioButton_1.Type=TRadioButtontRadioButton_1.X=149tRadioButton_1.Y=89tRadioButton_1.Width=81tRadioButton_1.Height=23tRadioButton_1.Text=自费tRadioButton_1.Group=1tRadioButton_1.Action=onRodiotRadioButton_1.Color=蓝tRadioButton_0.Type=TRadioButtontRadioButton_0.X=42tRadioButton_0.Y=89tRadioButton_0.Width=81tRadioButton_0.Height=23tRadioButton_0.Text=全部tRadioButton_0.Group=1tRadioButton_0.Selected=YtRadioButton_0.Action=onRodiotRadioButton_0.Color=蓝tLabel_1.Type=TLabeltLabel_1.X=784tLabel_1.Y=44tLabel_1.Width=72tLabel_1.Height=15tLabel_1.Text=开单病区:tLabel_1.Color=蓝tLabel_0.Type=TLabeltLabel_0.X=584tLabel_0.Y=44tLabel_0.Width=72tLabel_0.Height=15tLabel_0.Text=开单科室:tLabel_0.Color=蓝tLabel_20.Type=TLabeltLabel_20.X=225tLabel_20.Y=44tLabel_20.Width=18tLabel_20.Height=15tLabel_20.Text=至tLabel_20.Color=蓝E_DATE.Type=TTextFormatE_DATE.X=246E_DATE.Y=41E_DATE.Width=110E_DATE.Height=20E_DATE.Text=TTextFormatE_DATE.FormatType=dateE_DATE.Format=yyyy/MM/ddE_DATE.showDownButton=YS_DATE.Type=TTextFormatS_DATE.X=111S_DATE.Y=41S_DATE.Width=110S_DATE.Height=20S_DATE.Text=TTextFormatS_DATE.showDownButton=YS_DATE.FormatType=dateS_DATE.Format=yyyy/MM/ddtLabel_19.Type=TLabeltLabel_19.X=43tLabel_19.Y=44tLabel_19.Width=72tLabel_19.Height=15tLabel_19.Text=起迄日期:tLabel_19.Color=蓝