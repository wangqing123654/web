############################################## <p>Title:手术人员安排 </p>## <p>Description:手术人员安排 </p>## <p>Copyright: Copyright (c) 2008</p>## <p>Company: Javahis</p>## @author ZhangK 2009.09.28# @version 4.0#############################################<Type=TFrame>UI.Title=手术人员安排UI.MenuConfig=%ROOT%\config\ope\OPEPersonnelMenu.xUI.Width=950UI.Height=500UI.toolbar=YUI.controlclassname=com.javahis.ui.ope.OPEPersonnelControlUI.Item=tPanel_0;tPanel_1;tPanel_2;tPanel_3;tPanel_4UI.TopMenu=YUI.TopToolBar=YUI.ShowMenu=NUI.ShowTitle=NtPanel_4.Type=TPaneltPanel_4.X=710tPanel_4.Y=110tPanel_4.Width=230tPanel_4.Height=220tPanel_4.Border=组|麻醉医师tPanel_4.Item=ANA_TABLE;ANA_ADD;ANA_DEL;ANA_USERANA_USER.Type=人员ANA_USER.X=131ANA_USER.Y=27ANA_USER.Width=90ANA_USER.Height=22ANA_USER.Text=ANA_USER.HorizontalAlignment=2ANA_USER.PopupMenuHeader=ID,100;NAME,100ANA_USER.PopupMenuWidth=300ANA_USER.PopupMenuHeight=300ANA_USER.PopupMenuFilter=ID,1;NAME,1;PY1,1ANA_USER.FormatType=comboANA_USER.ShowDownButton=YANA_USER.Tip=人员ANA_USER.ShowColumnList=NAMEANA_USER.PosType=1ANA_DEL.Type=TButtonANA_DEL.X=134ANA_DEL.Y=87ANA_DEL.Width=85ANA_DEL.Height=23ANA_DEL.Text=-->删除ANA_DEL.Action=onANA_DELANA_ADD.Type=TButtonANA_ADD.X=134ANA_ADD.Y=60ANA_ADD.Width=85ANA_ADD.Height=23ANA_ADD.Text=<--增加ANA_ADD.Action=onANA_ADDANA_TABLE.Type=TTableANA_TABLE.X=10ANA_TABLE.Y=39ANA_TABLE.Width=115ANA_TABLE.Height=81ANA_TABLE.SpacingRow=1ANA_TABLE.RowHeight=20ANA_TABLE.AutoX=YANA_TABLE.AutoY=YANA_TABLE.AutoHeight=YANA_TABLE.Header=主,30,boolean;麻醉医师,80,OP_USERANA_TABLE.Item=OP_USERANA_TABLE.LockColumns=1ANA_TABLE.ColumnHorizontalAlignmentData=1,leftANA_TABLE.ParmMap=MAIN_FLG;USER_IDtPanel_3.Type=TPaneltPanel_3.X=475tPanel_3.Y=110tPanel_3.Width=230tPanel_3.Height=220tPanel_3.Border=组|器械护士tPanel_3.Item=SCRUB_TABLE;SCRUB_ADD;SCRUB_DEL;SCRUB_USERSCRUB_USER.Type=人员SCRUB_USER.X=131SCRUB_USER.Y=27SCRUB_USER.Width=90SCRUB_USER.Height=22SCRUB_USER.Text=SCRUB_USER.HorizontalAlignment=2SCRUB_USER.PopupMenuHeader=ID,100;NAME,100SCRUB_USER.PopupMenuWidth=300SCRUB_USER.PopupMenuHeight=300SCRUB_USER.PopupMenuFilter=ID,1;NAME,1;PY1,1SCRUB_USER.FormatType=comboSCRUB_USER.ShowDownButton=YSCRUB_USER.Tip=人员SCRUB_USER.ShowColumnList=NAMESCRUB_USER.PosType=3SCRUB_DEL.Type=TButtonSCRUB_DEL.X=135SCRUB_DEL.Y=87SCRUB_DEL.Width=84SCRUB_DEL.Height=23SCRUB_DEL.Text=-->删除SCRUB_DEL.Action=onSCRUB_DELSCRUB_ADD.Type=TButtonSCRUB_ADD.X=135SCRUB_ADD.Y=59SCRUB_ADD.Width=84SCRUB_ADD.Height=23SCRUB_ADD.Text=<--增加SCRUB_ADD.Action=onSCRUB_ADDSCRUB_TABLE.Type=TTableSCRUB_TABLE.X=12SCRUB_TABLE.Y=28SCRUB_TABLE.Width=115SCRUB_TABLE.Height=81SCRUB_TABLE.SpacingRow=1SCRUB_TABLE.RowHeight=20SCRUB_TABLE.AutoX=YSCRUB_TABLE.AutoY=YSCRUB_TABLE.AutoHeight=YSCRUB_TABLE.Header=器械护士,110,OP_USERSCRUB_TABLE.Item=OP_USERSCRUB_TABLE.LockColumns=allSCRUB_TABLE.ColumnHorizontalAlignmentData=0,lefttPanel_2.Type=TPaneltPanel_2.X=240tPanel_2.Y=110tPanel_2.Width=230tPanel_2.Height=220tPanel_2.Border=组|巡回护士tPanel_2.Item=CIRCULE_TABLE;CIRCULE_ADD;CIRCULE_DEL;CIRCULE_USERCIRCULE_USER.Type=人员CIRCULE_USER.X=131CIRCULE_USER.Y=27CIRCULE_USER.Width=90CIRCULE_USER.Height=22CIRCULE_USER.Text=CIRCULE_USER.HorizontalAlignment=2CIRCULE_USER.PopupMenuHeader=ID,100;NAME,100CIRCULE_USER.PopupMenuWidth=300CIRCULE_USER.PopupMenuHeight=300CIRCULE_USER.PopupMenuFilter=ID,1;NAME,1;PY1,1CIRCULE_USER.FormatType=comboCIRCULE_USER.ShowDownButton=YCIRCULE_USER.Tip=人员CIRCULE_USER.ShowColumnList=NAMECIRCULE_USER.PosType=3CIRCULE_DEL.Type=TButtonCIRCULE_DEL.X=133CIRCULE_DEL.Y=87CIRCULE_DEL.Width=86CIRCULE_DEL.Height=23CIRCULE_DEL.Text=-->删除CIRCULE_DEL.Action=onCIRCULE_DELCIRCULE_ADD.Type=TButtonCIRCULE_ADD.X=133CIRCULE_ADD.Y=58CIRCULE_ADD.Width=86CIRCULE_ADD.Height=23CIRCULE_ADD.Text=<--增加CIRCULE_ADD.Action=onCIRCULE_ADDCIRCULE_TABLE.Type=TTableCIRCULE_TABLE.X=19CIRCULE_TABLE.Y=31CIRCULE_TABLE.Width=115CIRCULE_TABLE.Height=81CIRCULE_TABLE.SpacingRow=1CIRCULE_TABLE.RowHeight=20CIRCULE_TABLE.AutoX=YCIRCULE_TABLE.AutoY=YCIRCULE_TABLE.AutoHeight=YCIRCULE_TABLE.Header=巡回护士,110,OP_USERCIRCULE_TABLE.Item=OP_USERCIRCULE_TABLE.LockColumns=allCIRCULE_TABLE.ColumnHorizontalAlignmentData=0,lefttPanel_1.Type=TPaneltPanel_1.X=5tPanel_1.Y=110tPanel_1.Width=230tPanel_1.Height=220tPanel_1.Border=组|体外循环医师/技师tPanel_1.AutoX=YtPanel_1.Item=EXTRA_TABLE;EXTRA_ADD;EXTRA_DEL;EXTRA_USER;OP_USEROP_USER.Type=人员下拉列表OP_USER.X=135OP_USER.Y=132OP_USER.Width=81OP_USER.Height=23OP_USER.Text=TButtonOP_USER.showID=YOP_USER.showName=YOP_USER.showText=NOP_USER.showValue=NOP_USER.showPy1=NOP_USER.showPy2=NOP_USER.Editable=YOP_USER.Tip=人员OP_USER.TableShowList=nameOP_USER.ModuleParmString=OP_USER.ModuleParmTag=OP_USER.Visible=NEXTRA_USER.Type=人员EXTRA_USER.X=131EXTRA_USER.Y=27EXTRA_USER.Width=90EXTRA_USER.Height=22EXTRA_USER.Text=EXTRA_USER.HorizontalAlignment=2EXTRA_USER.PopupMenuHeader=ID,100;NAME,100EXTRA_USER.PopupMenuWidth=300EXTRA_USER.PopupMenuHeight=300EXTRA_USER.PopupMenuFilter=ID,1;NAME,1;PY1,1EXTRA_USER.FormatType=comboEXTRA_USER.ShowDownButton=YEXTRA_USER.Tip=人员EXTRA_USER.ShowColumnList=NAMEEXTRA_USER.PosType=1EXTRA_DEL.Type=TButtonEXTRA_DEL.X=133EXTRA_DEL.Y=89EXTRA_DEL.Width=85EXTRA_DEL.Height=23EXTRA_DEL.Text=-->删除EXTRA_DEL.Action=onEXTRA_DELEXTRA_ADD.Type=TButtonEXTRA_ADD.X=133EXTRA_ADD.Y=58EXTRA_ADD.Width=85EXTRA_ADD.Height=23EXTRA_ADD.Text=<--增加EXTRA_ADD.Action=onEXTRA_ADDEXTRA_TABLE.Type=TTableEXTRA_TABLE.X=9EXTRA_TABLE.Y=24EXTRA_TABLE.Width=115EXTRA_TABLE.Height=185EXTRA_TABLE.SpacingRow=1EXTRA_TABLE.RowHeight=20EXTRA_TABLE.AutoX=YEXTRA_TABLE.AutoY=YEXTRA_TABLE.AutoHeight=YEXTRA_TABLE.Header=体外循环师/技师,110,OP_USEREXTRA_TABLE.Item=OP_USEREXTRA_TABLE.LockColumns=allEXTRA_TABLE.ColumnHorizontalAlignmentData=0,lefttPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=940tPanel_0.Height=100tPanel_0.Border=凸tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.Item=tLabel_0;MR_NO;tLabel_1;OP_DATE;tLabel_2;PAT_NAME;tLabel_3;ROOM_NO;tLabel_4;TIME_NEED;tLabel_5;OP_CODE1;STATESTATE.Type=TCheckBoxSTATE.X=545STATE.Y=70STATE.Width=81STATE.Height=23STATE.Text=排程完毕OP_CODE1.Type=TTextFieldOP_CODE1.X=113OP_CODE1.Y=71OP_CODE1.Width=395OP_CODE1.Height=20OP_CODE1.Text=OP_CODE1.Enabled=NtLabel_5.Type=TLabeltLabel_5.X=40tLabel_5.Y=74tLabel_5.Width=64tLabel_5.Height=15tLabel_5.Text=手 术 项TIME_NEED.Type=TTextFieldTIME_NEED.X=617TIME_NEED.Y=41TIME_NEED.Width=100TIME_NEED.Height=20TIME_NEED.Text=TIME_NEED.Enabled=NtLabel_4.Type=TLabeltLabel_4.X=547tLabel_4.Y=44tLabel_4.Width=62tLabel_4.Height=15tLabel_4.Text=手术需时ROOM_NO.Type=手术室列表ROOM_NO.X=387ROOM_NO.Y=41ROOM_NO.Width=120ROOM_NO.Height=20ROOM_NO.Text=TButtonROOM_NO.showID=YROOM_NO.showName=YROOM_NO.showText=NROOM_NO.showValue=NROOM_NO.showPy1=YROOM_NO.showPy2=YROOM_NO.Editable=YROOM_NO.Tip=手术室ROOM_NO.TableShowList=nameROOM_NO.ModuleParmString=GROUP_ID:OPE_OPROOMROOM_NO.ModuleParmTag=tLabel_3.Type=TLabeltLabel_3.X=311tLabel_3.Y=44tLabel_3.Width=62tLabel_3.Height=15tLabel_3.Text=手 术 间PAT_NAME.Type=TTextFieldPAT_NAME.X=387PAT_NAME.Y=10PAT_NAME.Width=120PAT_NAME.Height=20PAT_NAME.Text=PAT_NAME.Enabled=NtLabel_2.Type=TLabeltLabel_2.X=312tLabel_2.Y=13tLabel_2.Width=63tLabel_2.Height=15tLabel_2.Text=姓    名OP_DATE.Type=TTextFormatOP_DATE.X=113OP_DATE.Y=41OP_DATE.Width=160OP_DATE.Height=20OP_DATE.Text=TTextFormatOP_DATE.FormatType=dateOP_DATE.Format=yyyy/MM/dd HH:mm:ssOP_DATE.showDownButton=YOP_DATE.HorizontalAlignment=2tLabel_1.Type=TLabeltLabel_1.X=40tLabel_1.Y=44tLabel_1.Width=64tLabel_1.Height=15tLabel_1.Text=手术时间MR_NO.Type=TTextFieldMR_NO.X=113MR_NO.Y=10MR_NO.Width=120MR_NO.Height=20MR_NO.Text=MR_NO.Enabled=NtLabel_0.Type=TLabeltLabel_0.X=40tLabel_0.Y=14tLabel_0.Width=66tLabel_0.Height=15tLabel_0.Text=病 案 号