## TBuilder Config File ## Title:手术包回收,消毒## Company:javahis## Author:fudw 2009.05.15## version 1.0#<Type=TFrame>UI.Title=回收消毒UI.MenuConfig=%ROOT%\config\inv\INVNewBackDisnfectionMenu.xUI.Width=1024UI.Height=743UI.toolbar=YUI.controlclassname=com.javahis.ui.inv.INVNewBackDisnfectionControlUI.item=tPanel_1;TABLE_PACKM;TABLED;tMovePane_2;tPanel_2;TABLE_DIS;tMovePane_0;PEOPLEUI.layout=nullUI.Name=回收UI.Text=回收UI.TopMenu=YUI.TopToolBar=YUI.FocusList=SCREAMPEOPLE.Type=人员PEOPLE.X=287PEOPLE.Y=491PEOPLE.Width=81PEOPLE.Height=23PEOPLE.Text=PEOPLE.HorizontalAlignment=2PEOPLE.PopupMenuHeader=代码,100;名称,100PEOPLE.PopupMenuWidth=300PEOPLE.PopupMenuHeight=300PEOPLE.PopupMenuFilter=ID,1;NAME,1;PY1,1PEOPLE.FormatType=comboPEOPLE.ShowDownButton=YPEOPLE.Tip=人员PEOPLE.ShowColumnList=NAMEPEOPLE.ValueColumn=IDtMovePane_0.Type=TMovePanetMovePane_0.X=260tMovePane_0.Y=7tMovePane_0.Width=5tMovePane_0.Height=728tMovePane_0.Text=TMovePanetMovePane_0.MoveType=1tMovePane_0.AutoHeight=YtMovePane_0.Border=凸tMovePane_0.EntityData=tPanel_2,4;TABLE_DIS,4;tPanel_1,3;TABLE_PACKM,3;TABLED,3;tMovePane_2,3TABLE_DIS.Type=TTableTABLE_DIS.X=5TABLE_DIS.Y=90TABLE_DIS.Width=254TABLE_DIS.Height=648TABLE_DIS.SpacingRow=1TABLE_DIS.RowHeight=20TABLE_DIS.AutoX=YTABLE_DIS.AutoHeight=YTABLE_DIS.LockColumns=1,2,3TABLE_DIS.StringData=TABLE_DIS.Header=删,30,boolean;回收单号,100;日期,80;人员,60,PEOPLE;操作地址,100;部门代码,100TABLE_DIS.ParmMap=DELETE_FLG;RECYCLE_NO;OPT_DATE;OPT_USER;OPT_TERM;ORG_CODETABLE_DIS.Item=OPERATOR;PEOPLETABLE_DIS.ClickedAction=onTableDisClickedtPanel_2.Type=TPaneltPanel_2.X=5tPanel_2.Y=5tPanel_2.Width=254tPanel_2.Height=81tPanel_2.Border=组tPanel_2.AutoX=YtPanel_2.AutoY=YtPanel_2.Item=tLabel_17;SEL_RECYCLE_NO;tLabel_18;START_RECYCLE_DATE;tLabel_24;END_RECYCLE_DATEEND_RECYCLE_DATE.Type=TTextFormatEND_RECYCLE_DATE.X=153END_RECYCLE_DATE.Y=41END_RECYCLE_DATE.Width=95END_RECYCLE_DATE.Height=20END_RECYCLE_DATE.Text=END_RECYCLE_DATE.showDownButton=YEND_RECYCLE_DATE.FormatType=dateEND_RECYCLE_DATE.Format=yyyy/MM/ddtLabel_24.Type=TLabeltLabel_24.X=140tLabel_24.Y=50tLabel_24.Width=11tLabel_24.Height=15tLabel_24.Text=~tLabel_24.Color=蓝START_RECYCLE_DATE.Type=TTextFormatSTART_RECYCLE_DATE.X=46START_RECYCLE_DATE.Y=41START_RECYCLE_DATE.Width=92START_RECYCLE_DATE.Height=20START_RECYCLE_DATE.Text=START_RECYCLE_DATE.showDownButton=YSTART_RECYCLE_DATE.FormatType=dateSTART_RECYCLE_DATE.Format=yyyy/MM/ddtLabel_18.Type=TLabeltLabel_18.X=8tLabel_18.Y=44tLabel_18.Width=44tLabel_18.Height=15tLabel_18.Text=日期：tLabel_18.Color=蓝SEL_RECYCLE_NO.Type=TTextFieldSEL_RECYCLE_NO.X=77SEL_RECYCLE_NO.Y=11SEL_RECYCLE_NO.Width=171SEL_RECYCLE_NO.Height=20SEL_RECYCLE_NO.Text=tLabel_17.Type=TLabeltLabel_17.X=8tLabel_17.Y=14tLabel_17.Width=72tLabel_17.Height=15tLabel_17.Text=回收单号：tLabel_17.Color=蓝tMovePane_2.Type=TMovePanetMovePane_2.X=266tMovePane_2.Y=315tMovePane_2.Width=753tMovePane_2.Height=5tMovePane_2.Text=tMovePane_2.MoveType=2tMovePane_2.Style=4tMovePane_2.CursorType=4tMovePane_2.AutoWidth=YtMovePane_2.EntityData=TABLE_PACKM,2;TABLED,1tMovePane_2.DoubleClickType=2TABLED.Type=TTableTABLED.X=266TABLED.Y=320TABLED.Width=899TABLED.Height=415TABLED.SpacingRow=1TABLED.RowHeight=20TABLED.Header=物资名称,200;单位,60,STOCK_UNIT;数量,60;成本,60;耗,35,boolean;折损次数,70,intTABLED.ParmMap=INV_CHN_DESC;STOCK_UNIT;QTY;COST_PRICE;ONCE_USE_FLG;RECOUNT_TIME;INV_CODE;INVSEQ_NO;PACK_CODE;PACK_SEQ_NOTABLED.AutoWidth=YTABLED.AutoHeight=YTABLED.LockColumns=0,1,2,3,4TABLED.AutoModifyDataStore=NTABLED.Item=STOCK_UNITTABLED.ClickedAction=TABLED.HorizontalAlignmentData=TABLED.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,right;5,right;7,rightTABLED.FocusIndexList=5TABLED.FocusType=2TABLE_PACKM.Type=TTableTABLE_PACKM.X=266TABLE_PACKM.Y=153TABLE_PACKM.Width=753TABLE_PACKM.Height=161TABLE_PACKM.SpacingRow=1TABLE_PACKM.RowHeight=20TABLE_PACKM.AutoWidth=YTABLE_PACKM.ParmMap=PACK_DESC;PACK_CODE;PACK_SEQ_NO;QTY;STATUS;RECYCLE_DATE;RECYCLE_USER;WASH_DATE;WASH_USER;DISINFECTION_DATE;DISINFECTION_VALID_DATE;DISINFECTION_USER;DISINFECTION_POTSEQ;DISINFECTION_PROGRAM;BARCODETABLE_PACKM.Header=包名,200;包号,70;序号,70;数量,60;状态,70,STATUS;回收日期,100;回收人员,80,OPERATOR;清洗日期,100;清洗人员,80,OPERATOR;消毒日期,100;消毒效期,100;消毒人员,80,OPERATOR;锅次,80;程序,80;条码,100TABLE_PACKM.AutoModifyDataStore=NTABLE_PACKM.ClickedAction=onTableMClickedTABLE_PACKM.LockColumns=0,1,2,3,4,5,6,7,8,9,11,12,13TABLE_PACKM.Item=STATUS;OPERATOR;PEOPLETABLE_PACKM.DoubleClickedAction=TABLE_PACKM.ColumnHorizontalAlignmentData=0,left;1,left;2,leftTABLE_PACKM.FocusIndexList=5TABLE_PACKM.FocusType=2tPanel_1.Type=TPaneltPanel_1.X=265tPanel_1.Y=5tPanel_1.Width=754tPanel_1.Height=147tPanel_1.Border=组|手术包tPanel_1.AutoY=YtPanel_1.AutoWidth=YtPanel_1.Item=tLabel_4;PACK_CODE;tLabel_7;PACK_DESC;tLabel_0;PACK_SEQ_NO;BACK;SCREAM;tLabel_1;WASH;DISINFECTION;tLabel_15;tLabel_2;DISINFECTION_POTSEQ;tLabel_3;DISINFECTION_PROGRAM;ORG_CODEtPanel_1.AutoX=NtPanel_1.TopMenu=YtPanel_1.TopToolBar=YtPanel_1.Text=回收tPanel_1.Tip=回收tPanel_1.Title=回收tPanel_1.Enabled=NORG_CODE.Type=物资部门下拉区域ORG_CODE.X=92ORG_CODE.Y=20ORG_CODE.Width=100ORG_CODE.Height=23ORG_CODE.Text=ORG_CODE.HorizontalAlignment=2ORG_CODE.PopupMenuHeader=代码,100;名称,100ORG_CODE.PopupMenuWidth=300ORG_CODE.PopupMenuHeight=300ORG_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1ORG_CODE.FormatType=comboORG_CODE.ShowDownButton=YORG_CODE.Tip= 物资部门ORG_CODE.ShowColumnList=NAMEORG_CODE.OrgType=BORG_CODE.HisOneNullRow=YDISINFECTION_PROGRAM.Type=TTextFieldDISINFECTION_PROGRAM.X=209DISINFECTION_PROGRAM.Y=90DISINFECTION_PROGRAM.Width=109DISINFECTION_PROGRAM.Height=20DISINFECTION_PROGRAM.Text=tLabel_3.Type=TLabeltLabel_3.X=172tLabel_3.Y=91tLabel_3.Width=43tLabel_3.Height=15tLabel_3.Text=程序：DISINFECTION_POTSEQ.Type=TTextFieldDISINFECTION_POTSEQ.X=51DISINFECTION_POTSEQ.Y=90DISINFECTION_POTSEQ.Width=109DISINFECTION_POTSEQ.Height=20DISINFECTION_POTSEQ.Text=tLabel_2.Type=TLabeltLabel_2.X=12tLabel_2.Y=91tLabel_2.Width=42tLabel_2.Height=15tLabel_2.Text=锅次：tLabel_15.Type=TLabeltLabel_15.X=12tLabel_15.Y=23tLabel_15.Width=85tLabel_15.Height=15tLabel_15.Text=供应室部门：tLabel_15.Color=蓝DISINFECTION.Type=TCheckBoxDISINFECTION.X=117DISINFECTION.Y=118DISINFECTION.Width=57DISINFECTION.Height=23DISINFECTION.Text=消毒DISINFECTION.Selected=YDISINFECTION.Enabled=NWASH.Type=TCheckBoxWASH.X=64WASH.Y=118WASH.Width=56WASH.Height=23WASH.Text=清洗WASH.Selected=YWASH.Enabled=NSTOCK_UNIT.Type=TComboBoxSTOCK_UNIT.X=440STOCK_UNIT.Y=50STOCK_UNIT.Width=81STOCK_UNIT.Height=23STOCK_UNIT.Text=TButtonSTOCK_UNIT.showID=YSTOCK_UNIT.Editable=YSTOCK_UNIT.SQL=SELECT UNIT_CODE,UNIT_CHN_DESC FROM SYS_UNITSTOCK_UNIT.ParmMap=id:UNIT_CODE;name:UNIT_CHN_DESCSTOCK_UNIT.ShowText=NSTOCK_UNIT.ShowName=YSTOCK_UNIT.ExpandWidth=10STOCK_UNIT.TableShowList=nametLabel_1.Type=TLabeltLabel_1.X=211tLabel_1.Y=23tLabel_1.Width=64tLabel_1.Height=15tLabel_1.Text=条码扫描:SCREAM.Type=TTextFieldSCREAM.X=279SCREAM.Y=21SCREAM.Width=178SCREAM.Height=20SCREAM.Text=SCREAM.Action=onScreamOPERATOR.Type=TComboBoxOPERATOR.X=378OPERATOR.Y=53OPERATOR.Width=81OPERATOR.Height=23OPERATOR.Text=TButtonOPERATOR.showID=YOPERATOR.Editable=YOPERATOR.ShowName=YOPERATOR.ShowText=NOPERATOR.TableShowList=nameOPERATOR.SQL=SELECT USER_ID,USER_NAME FROM SYS_OPERATOROPERATOR.ParmMap=id:USER_ID;name:USER_NAMESTATUS.Type=TComboBoxSTATUS.X=316STATUS.Y=46STATUS.Width=81STATUS.Height=23STATUS.Text=TButtonSTATUS.showID=YSTATUS.Editable=YSTATUS.StringData=[[id,name],[,],['0',在库],['1',出库],['2',已回收],['3',已消毒],['4',维修中]]STATUS.ShowName=YSTATUS.ShowText=NSTATUS.TableShowList=nameSTATUS.ExpandWidth=10BACK.Type=TCheckBoxBACK.X=12BACK.Y=118BACK.Width=54BACK.Height=23BACK.Text=回收BACK.Selected=YBACK.Action=onBackSelectedBACK.Enabled=NPACK_SEQ_NO.Type=TNumberTextFieldPACK_SEQ_NO.X=209PACK_SEQ_NO.Y=54PACK_SEQ_NO.Width=109PACK_SEQ_NO.Height=20PACK_SEQ_NO.Text=0PACK_SEQ_NO.Format=#########0PACK_SEQ_NO.Action=addPackagetLabel_0.Type=TLabeltLabel_0.X=172tLabel_0.Y=56tLabel_0.Width=37tLabel_0.Height=15tLabel_0.Text=序号:tLabel_0.Color=蓝PACK_DESC.Type=TTextFieldPACK_DESC.X=371PACK_DESC.Y=54PACK_DESC.Width=150PACK_DESC.Height=20PACK_DESC.Text=PACK_DESC.Action=PACK_DESC.Enabled=NtLabel_7.Type=TLabeltLabel_7.X=333tLabel_7.Y=56tLabel_7.Width=38tLabel_7.Height=15tLabel_7.Text=包名:tLabel_7.Color=蓝PACK_CODE.Type=TTextFieldPACK_CODE.X=50PACK_CODE.Y=54PACK_CODE.Width=109PACK_CODE.Height=20PACK_CODE.Text=PACK_CODE.Action=PACK_CODE.FocusLostAction=tLabel_4.Type=TLabeltLabel_4.X=12tLabel_4.Y=56tLabel_4.Width=38tLabel_4.Height=15tLabel_4.Text=包号:tLabel_4.Color=蓝