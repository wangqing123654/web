## TBuilder Config File ## Title:请假登记作业## Company:JavaHis## Author:wangzhilei 2012.08.16## version 1.0#<Type=TFrame>UI.Title=长期医嘱摆药UI.MenuConfig=%Root%\config\spc\SPCDispenseOutAssginNormalMenum.xUI.Width=1024UI.Height=820UI.toolbar=YUI.controlclassname=com.javahis.ui.spc.SPCDispenseOutAssginNormalControlUI.item=tPanel_2;tPanel_3;tMovePane_1;tPanel_4;TABLE_ORDER;tPanel_0UI.layout=nullUI.MenuMap=UI.Y=-15UI.TopMenu=YUI.TopToolBar=YUI.Tip=长期医嘱摆药UI.Text=长期医嘱摆药tPanel_0.Type=TPaneltPanel_0.X=7tPanel_0.Y=238tPanel_0.Width=1012tPanel_0.Height=59tPanel_0.Border=组|更换药盒tPanel_0.AutoWidth=YtPanel_0.AutoW=NtPanel_0.Item=tLabel_1;BOX_ESL_ID;tLabel_0;BOX_CHECK;tLabel_3;PAT_COUNT;tLabel_4;GROUP_ID;GROUP_ID_SELECT_LABEL;GROUP_ID_SELECT;tLabel_5;BOX_CLEARtPanel_0.AutoX=YBOX_CLEAR.Type=TTextFieldBOX_CLEAR.X=450BOX_CLEAR.Y=18BOX_CLEAR.Width=110BOX_CLEAR.Height=32BOX_CLEAR.Text=BOX_CLEAR.FontSize=16BOX_CLEAR.Action=onBoxCleartLabel_5.Type=TLabeltLabel_5.X=362tLabel_5.Y=23tLabel_5.Width=97tLabel_5.Height=23tLabel_5.Text=清空药盒：tLabel_5.FontSize=19GROUP_ID_SELECT.Type=TTextFieldGROUP_ID_SELECT.X=988GROUP_ID_SELECT.Y=18GROUP_ID_SELECT.Width=15GROUP_ID_SELECT.Height=32GROUP_ID_SELECT.Text=GROUP_ID_SELECT.FontSize=16GROUP_ID_SELECT.Visible=NGROUP_ID_SELECT.Action=GROUP_ID_SELECT.Enabled=YGROUP_ID_SELECT_LABEL.Type=TLabelGROUP_ID_SELECT_LABEL.X=962GROUP_ID_SELECT_LABEL.Y=23GROUP_ID_SELECT_LABEL.Width=19GROUP_ID_SELECT_LABEL.Height=23GROUP_ID_SELECT_LABEL.Text=选择绑定药盒组：GROUP_ID_SELECT_LABEL.FontSize=19GROUP_ID_SELECT_LABEL.Visible=NGROUP_ID.Type=TTextFieldGROUP_ID.X=937GROUP_ID.Y=20GROUP_ID.Width=16GROUP_ID.Height=32GROUP_ID.Text=GROUP_ID.FontSize=16GROUP_ID.Enabled=NGROUP_ID.Visible=NtLabel_4.Type=TLabeltLabel_4.X=910tLabel_4.Y=23tLabel_4.Width=20tLabel_4.Height=24tLabel_4.Text=当前绑定药盒组：tLabel_4.FontSize=19tLabel_4.Visible=NPAT_COUNT.Type=TTextFieldPAT_COUNT.X=64PAT_COUNT.Y=18PAT_COUNT.Width=50PAT_COUNT.Height=32PAT_COUNT.Text=PAT_COUNT.FontSize=16PAT_COUNT.Enabled=NtLabel_3.Type=TLabeltLabel_3.X=14tLabel_3.Y=23tLabel_3.Width=62tLabel_3.Height=23tLabel_3.Text=人数：tLabel_3.FontSize=19BOX_CHECK.Type=TTextFieldBOX_CHECK.X=677BOX_CHECK.Y=18BOX_CHECK.Width=110BOX_CHECK.Height=32BOX_CHECK.Text=BOX_CHECK.FontSize=16BOX_CHECK.Action=onBoxChecktLabel_0.Type=TLabeltLabel_0.X=586tLabel_0.Y=23tLabel_0.Width=107tLabel_0.Height=23tLabel_0.Text=摆药核对：tLabel_0.FontSize=19BOX_ESL_ID.Type=TTextFieldBOX_ESL_ID.X=225BOX_ESL_ID.Y=18BOX_ESL_ID.Width=110BOX_ESL_ID.Height=32BOX_ESL_ID.Text=BOX_ESL_ID.FontSize=16BOX_ESL_ID.Action=onEleTagCodeBOX_ESL_ID.Visible=YtLabel_1.Type=TLabeltLabel_1.X=135tLabel_1.Y=23tLabel_1.Width=96tLabel_1.Height=23tLabel_1.Text=绑定药盒：tLabel_1.FontSize=19tLabel_1.Color=黑tLabel_1.Visible=YTABLE_ORDER.Type=TTableTABLE_ORDER.X=5TABLE_ORDER.Y=56TABLE_ORDER.Width=1012TABLE_ORDER.Height=179TABLE_ORDER.SpacingRow=1TABLE_ORDER.RowHeight=30TABLE_ORDER.Header=病区,200;统药单号,200;摆药时间,200;展开人员,130TABLE_ORDER.AutoWidth=YTABLE_ORDER.AutoX=YTABLE_ORDER.ParmMap=STATION_DESC;INTGMED_NO;DSPN_DATE;USER_NAME;STATION_CODETABLE_ORDER.LockColumns=allTABLE_ORDER.ClickedAction=tableOrderClickedtPanel_4.Type=TPaneltPanel_4.X=440tPanel_4.Y=297tPanel_4.Width=579tPanel_4.Height=518tPanel_4.Border=组|病患用药信息tPanel_4.AutoHeight=YtPanel_4.AutoWidth=YtPanel_4.Item=TABLE_YTABLE_Y.Type=TTableTABLE_Y.X=9TABLE_Y.Y=22TABLE_Y.Width=557TABLE_Y.Height=483TABLE_Y.SpacingRow=1TABLE_Y.RowHeight=20TABLE_Y.AutoY=YTABLE_Y.AutoX=YTABLE_Y.AutoWidth=YTABLE_Y.AutoHeight=YTABLE_Y.ParmMap=ORDER_DESC;MEDI_QTY;DISPENSE_QTY;ROUTE_CHN_DESC;FREQ_CHN_DESC;TAKE_DAYS;OWN_AMT;CASE_NO;ORDER_NO;ORDER_SEQ;START_DTTMTABLE_Y.Header=药品名称,240;用量,100;总量,90;用法,100;频次,110;天数,50TABLE_Y.LockColumns=allTABLE_Y.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,right;5,left;6,rightTABLE_Y.Item=TABLE_Y.ColumnSelectionAllowed=YtMovePane_1.Type=TMovePanetMovePane_1.X=671tMovePane_1.Y=103tMovePane_1.Width=9tMovePane_1.Height=694tMovePane_1.Text=TMovePanetMovePane_1.MoveType=1tMovePane_1.EntityData=tPanel_3,4;tPanel_4,3tPanel_3.Type=TPaneltPanel_3.X=6tPanel_3.Y=297tPanel_3.Width=432tPanel_3.Height=518tPanel_3.Border=组|病患信息tPanel_3.AutoX=YtPanel_3.AutoHeight=YtPanel_3.Item=TABLE_NTABLE_N.Type=TTableTABLE_N.X=12TABLE_N.Y=24TABLE_N.Width=410TABLE_N.Height=483TABLE_N.SpacingRow=1TABLE_N.RowHeight=20TABLE_N.AutoX=YTABLE_N.AutoY=YTABLE_N.AutoWidth=YTABLE_N.AutoHeight=YTABLE_N.Header=核对,35,boolean;病案号,105;床号,65;病患名称,95;药盒标签,90;区域,60;就诊号,60;统药单,60;病区,60;床号,60;病区号,60TABLE_N.ParmMap=SELECT_FLG;MR_NO;BED_NO_DESC;PAT_NAME;BOX_ESL_ID;AP_REGION;CASE_NO;INTGMED_NO;STATION_DESC;BED_NO;STATION_CODETABLE_N.Item=TABLE_N.LockColumns=allTABLE_N.ClickedAction=onTableMClickedTABLE_N.ColumnHorizontalAlignmentData=1,left;2,left;3,lefttPanel_2.Type=TPaneltPanel_2.X=5tPanel_2.Y=5tPanel_2.Width=1014tPanel_2.Height=46tPanel_2.Border=组|tPanel_2.AutoWidth=YtPanel_2.AutoX=YtPanel_2.Item=EMPLOYEE_CODE;BOSS;AGENT;INTGMED_NO;LEAVE_CODE;STATION_DESC;tLabel_2;tLabel_7;tLabel_8;tLabel_9;START_DATE;END_DATE;STATION_ID;INTGMED_NO_QUERYtPanel_2.AutoY=YINTGMED_NO_QUERY.Type=TTextFieldINTGMED_NO_QUERY.X=861INTGMED_NO_QUERY.Y=7INTGMED_NO_QUERY.Width=141INTGMED_NO_QUERY.Height=32INTGMED_NO_QUERY.Text=INTGMED_NO_QUERY.FontSize=16STATION_ID.Type=病区下拉列表STATION_ID.X=550STATION_ID.Y=7STATION_ID.Width=192STATION_ID.Height=32STATION_ID.Text=TButtonSTATION_ID.showID=YSTATION_ID.showName=YSTATION_ID.showText=NSTATION_ID.showValue=NSTATION_ID.showPy1=YSTATION_ID.showPy2=YSTATION_ID.Editable=YSTATION_ID.Tip=病区STATION_ID.TableShowList=id,nameSTATION_ID.FontSize=16END_DATE.Type=TTextFormatEND_DATE.X=295END_DATE.Y=6END_DATE.Width=177END_DATE.Height=32END_DATE.Text=TTextFormatEND_DATE.FormatType=dateEND_DATE.Format=yyyy/MM/dd HH:mm:ssEND_DATE.showDownButton=YEND_DATE.FontSize=16END_DATE.HorizontalAlignment=2START_DATE.Type=TTextFormatSTART_DATE.X=94START_DATE.Y=6START_DATE.Width=177START_DATE.Height=32START_DATE.Text=TTextFormatSTART_DATE.Format=yyyy/MM/dd HH:mm:ssSTART_DATE.FormatType=dateSTART_DATE.showDownButton=YSTART_DATE.FontSize=16START_DATE.HorizontalAlignment=2tLabel_9.Type=TLabeltLabel_9.X=771tLabel_9.Y=11tLabel_9.Width=103tLabel_9.Height=26tLabel_9.Text=统药单号：tLabel_9.FontSize=20tLabel_9.Color=蓝tLabel_8.Type=TLabeltLabel_8.X=499tLabel_8.Y=9tLabel_8.Width=62tLabel_8.Height=26tLabel_8.Text=病区：tLabel_8.FontSize=20tLabel_8.Color=蓝tLabel_7.Type=TLabeltLabel_7.X=274tLabel_7.Y=6tLabel_7.Width=25tLabel_7.Height=26tLabel_7.Text=～tLabel_7.FontSize=20tLabel_7.Color=蓝tLabel_2.Type=TLabeltLabel_2.X=6tLabel_2.Y=8tLabel_2.Width=100tLabel_2.Height=26tLabel_2.Text=调配时间：tLabel_2.FontSize=20tLabel_2.Color=蓝STATION_DESC.Type=TTextFieldSTATION_DESC.X=997STATION_DESC.Y=-4STATION_DESC.Width=14STATION_DESC.Height=32STATION_DESC.Text=STATION_DESC.Enabled=NSTATION_DESC.FontSize=20STATION_DESC.Visible=NLEAVE_CODE.Type=假别下拉区域(台心HRS)LEAVE_CODE.X=246LEAVE_CODE.Y=19LEAVE_CODE.Width=114LEAVE_CODE.Height=23LEAVE_CODE.Text=LEAVE_CODE.HorizontalAlignment=2LEAVE_CODE.PopupMenuHeader=编号,100;名称,100LEAVE_CODE.PopupMenuWidth=300LEAVE_CODE.PopupMenuHeight=300LEAVE_CODE.PopupMenuFilter=ID,1;NAME,1LEAVE_CODE.FormatType=comboLEAVE_CODE.ShowDownButton=YLEAVE_CODE.Tip=假别(台心HRS)LEAVE_CODE.ShowColumnList=NAMELEAVE_CODE.HisOneNullRow=YINTGMED_NO.Type=TTextFieldINTGMED_NO.X=982INTGMED_NO.Y=-2INTGMED_NO.Width=13INTGMED_NO.Height=32INTGMED_NO.Text=INTGMED_NO.Action=onIntgmEdNoINTGMED_NO.FontSize=20INTGMED_NO.Visible=NAGENT.Type=员工下拉区域(台心HRS)AGENT.X=245AGENT.Y=51AGENT.Width=115AGENT.Height=23AGENT.Text=AGENT.HorizontalAlignment=2AGENT.PopupMenuHeader=编号,100;名称,100AGENT.PopupMenuWidth=300AGENT.PopupMenuHeight=300AGENT.PopupMenuFilter=ID,1;NAME,1;PY1,1AGENT.FormatType=comboAGENT.ShowDownButton=YAGENT.Tip=员工(台心HRS)AGENT.ShowColumnList=NAMEAGENT.HisOneNullRow=YBOSS.Type=员工下拉区域(台心HRS)BOSS.X=56BOSS.Y=51BOSS.Width=115BOSS.Height=23BOSS.Text=BOSS.HorizontalAlignment=2BOSS.PopupMenuHeader=编号,100;名称,100BOSS.PopupMenuWidth=300BOSS.PopupMenuHeight=300BOSS.PopupMenuFilter=ID,1;NAME,1;PY1,1BOSS.FormatType=comboBOSS.ShowDownButton=YBOSS.Tip=员工(台心HRS)BOSS.ShowColumnList=NAMEBOSS.HisOneNullRow=YEMPLOYEE_CODE.Type=员工下拉区域(台心HRS)EMPLOYEE_CODE.X=56EMPLOYEE_CODE.Y=18EMPLOYEE_CODE.Width=115EMPLOYEE_CODE.Height=23EMPLOYEE_CODE.Text=EMPLOYEE_CODE.HorizontalAlignment=2EMPLOYEE_CODE.PopupMenuHeader=编号,100;名称,100EMPLOYEE_CODE.PopupMenuWidth=300EMPLOYEE_CODE.PopupMenuHeight=300EMPLOYEE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1EMPLOYEE_CODE.FormatType=comboEMPLOYEE_CODE.ShowDownButton=YEMPLOYEE_CODE.Tip=员工(台心HRS)EMPLOYEE_CODE.ShowColumnList=NAMEEMPLOYEE_CODE.HisOneNullRow=YEMPLOYEE_CODE.Action=onClickEMPLOYEE_CODE