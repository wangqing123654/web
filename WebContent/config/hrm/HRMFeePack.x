## TBuilder Config File ## Title:健检套餐设定## Company:JavaHis## Author:ehui 2009.09.21## version 1.0#<Type=TFrame>UI.Title=收费套餐设定UI.MenuConfig=%ROOT%\config\hrm\HRMFeePackMenu.xUI.Width=1280UI.Height=780UI.toolbar=YUI.controlclassname=com.javahis.ui.hrm.HRMFeePackControlUI.item=tPanel_0UI.layout=nullUI.FocusList=PACKAGE_DESCUI.TopMenu=YUI.TopToolBar=YUI.ShowMenu=YtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1270tPanel_0.Height=770tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.AutoHeight=YtPanel_0.Border=组tPanel_0.Item=MAIN_PANEL;DETAIL_PANEL;tMovePane_0tMovePane_0.Type=TMovePanetMovePane_0.X=10tMovePane_0.Y=249tMovePane_0.Width=1249tMovePane_0.Height=10tMovePane_0.Text=tMovePane_0.MoveType=2tMovePane_0.EntityData=MAIN_PANEL,2;DETAIL_PANEL,1tMovePane_0.Style=4tMovePane_0.CursorType=2tMovePane_0.AutoWidth=YDETAIL_PANEL.Type=TPanelDETAIL_PANEL.X=5DETAIL_PANEL.Y=259DETAIL_PANEL.Width=1254DETAIL_PANEL.Height=500DETAIL_PANEL.Border=组|套餐细项DETAIL_PANEL.AutoHeight=YDETAIL_PANEL.AutoWidth=YDETAIL_PANEL.Item=DETAIL_TABLEUNIT_CODE.Type=计量单位下拉列表UNIT_CODE.X=80UNIT_CODE.Y=15UNIT_CODE.Width=81UNIT_CODE.Height=23UNIT_CODE.Text=TButtonUNIT_CODE.showID=NUNIT_CODE.showName=YUNIT_CODE.showText=NUNIT_CODE.showValue=NUNIT_CODE.showPy1=YUNIT_CODE.showPy2=YUNIT_CODE.Editable=YUNIT_CODE.Tip=计量单位UNIT_CODE.TableShowList=nameDETAIL_TABLE.Type=TTableDETAIL_TABLE.X=8DETAIL_TABLE.Y=26DETAIL_TABLE.Width=806DETAIL_TABLE.Height=463DETAIL_TABLE.SpacingRow=1DETAIL_TABLE.RowHeight=20DETAIL_TABLE.AutoWidth=YDETAIL_TABLE.AutoHeight=YDETAIL_TABLE.Header=套餐名称,200;医嘱名称,180;数量,60;单位,60,UNIT_CODE;原价,60,double;套餐价,60,double;检体,80,ITEM_CODE;执行科室,100,EXEC_DEPT_CODE;科别属性,100,DEPT_ATTRIBUTE;打印序号,80,intDETAIL_TABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,right;3,left;4,right;5,right;6,left;7,left;8,left;9,rightDETAIL_TABLE.ParmMap=PACKAGE_DESC;ORDER_DESC;DISPENSE_QTY;DISPENSE_UNIT;ORIGINAL_PRICE_MAIN;PACKAGE_PRICE_MAIN;OPTITEM_CODE;EXEC_DEPT_CODE;DEPT_ATTRIBUTE;CHECK_SEQDETAIL_TABLE.LockColumns=0,3,4,5DETAIL_TABLE.Item=UNIT_CODE;EXEC_DEPT_CODE;ITEM_CODE;DEPT_ATTRIBUTEDETAIL_TABLE.ClickedAction=onDetailClickDETAIL_TABLE.AutoModifyDataStore=YDETAIL_TABLE.RightClickedAction=showPopMenuDETAIL_TABLE.FocusIndexList=1DETAIL_TABLE.FocusType=2MAIN_PANEL.Type=TPanelMAIN_PANEL.X=5MAIN_PANEL.Y=2MAIN_PANEL.Width=1254MAIN_PANEL.Height=243MAIN_PANEL.Border=组|套餐主项MAIN_PANEL.AutoHeight=NMAIN_PANEL.Item=MAIN_TABLE;tLabel_9;PACKAGE_DESCMAIN_PANEL.AutoWidth=YPACKAGE_DESC.Type=健康检查套餐下拉区域PACKAGE_DESC.X=72PACKAGE_DESC.Y=20PACKAGE_DESC.Width=132PACKAGE_DESC.Height=23PACKAGE_DESC.Text=PACKAGE_DESC.HisOneNullRow=YPACKAGE_DESC.HorizontalAlignment=2PACKAGE_DESC.PopupMenuHeader=代码,100;名称,100PACKAGE_DESC.PopupMenuWidth=300PACKAGE_DESC.PopupMenuHeight=300PACKAGE_DESC.PopupMenuFilter=ID,1;NAME,1;PY1,1PACKAGE_DESC.FormatType=comboPACKAGE_DESC.ShowDownButton=YPACKAGE_DESC.Tip=健康检查套餐PACKAGE_DESC.ShowColumnList=NAMEPACKAGE_DESC.Action=onQueryByCodeDEPT_ATTRIBUTE.Type=科室属性下拉区域DEPT_ATTRIBUTE.X=335DEPT_ATTRIBUTE.Y=16DEPT_ATTRIBUTE.Width=81DEPT_ATTRIBUTE.Height=23DEPT_ATTRIBUTE.Text=DEPT_ATTRIBUTE.HorizontalAlignment=2DEPT_ATTRIBUTE.PopupMenuHeader=代码,100;名称,100DEPT_ATTRIBUTE.PopupMenuWidth=300DEPT_ATTRIBUTE.PopupMenuHeight=300DEPT_ATTRIBUTE.PopupMenuFilter=ID,1;NAME,1;PY1,1DEPT_ATTRIBUTE.FormatType=comboDEPT_ATTRIBUTE.ShowDownButton=YDEPT_ATTRIBUTE.Tip=科室属性DEPT_ATTRIBUTE.ShowColumnList=NAMEDEPT_ATTRIBUTE.HisOneNullRow=YITEM_CODE.Type=检查项目ITEM_CODE.X=398ITEM_CODE.Y=15ITEM_CODE.Width=81ITEM_CODE.Height=23ITEM_CODE.Text=ITEM_CODE.HorizontalAlignment=2ITEM_CODE.PopupMenuHeader=代码,100;名称,100ITEM_CODE.PopupMenuWidth=300ITEM_CODE.PopupMenuHeight=300ITEM_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1ITEM_CODE.FormatType=comboITEM_CODE.ShowDownButton=YITEM_CODE.Tip=检查项目ITEM_CODE.ShowColumnList=NAMEEXEC_DEPT_CODE.Type=科室EXEC_DEPT_CODE.X=338EXEC_DEPT_CODE.Y=15EXEC_DEPT_CODE.Width=81EXEC_DEPT_CODE.Height=23EXEC_DEPT_CODE.Text=EXEC_DEPT_CODE.HisOneNullRow=YEXEC_DEPT_CODE.HorizontalAlignment=2EXEC_DEPT_CODE.PopupMenuHeader=代码,100;名称,100EXEC_DEPT_CODE.PopupMenuWidth=300EXEC_DEPT_CODE.PopupMenuHeight=300EXEC_DEPT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1EXEC_DEPT_CODE.FormatType=comboEXEC_DEPT_CODE.ShowDownButton=YEXEC_DEPT_CODE.Tip=科室EXEC_DEPT_CODE.ShowColumnList=NAMEEXEC_DEPT_CODE.FinalFlg=YtLabel_9.Type=TLabeltLabel_9.X=10tLabel_9.Y=26tLabel_9.Width=72tLabel_9.Height=15tLabel_9.Text=套餐名称tLabel_9.Color=蓝MAIN_TABLE.Type=TTableMAIN_TABLE.X=8MAIN_TABLE.Y=51MAIN_TABLE.Width=1235MAIN_TABLE.Height=181MAIN_TABLE.SpacingRow=1MAIN_TABLE.RowHeight=20MAIN_TABLE.AutoWidth=YMAIN_TABLE.AutoHeight=YMAIN_TABLE.Header=启用,40,boolean;套餐代码,100;套餐名称,400;原价总额,80,double;套餐价总额,80,double;总检模板,140,TOT_MR_CODE;备注,100MAIN_TABLE.ColumnHorizontalAlignmentData=1,left;2,left;3,right;4,right;5,left;6,leftMAIN_TABLE.ParmMap=ACTIVE_FLG;PACKAGE_CODE;PACKAGE_DESC;TC_TOT_AMT;TC_AR_AMT;TOT_MR_CODE;DESCRIPTIONMAIN_TABLE.AutoModifyDataStore=YMAIN_TABLE.Item=TOT_MR_CODEMAIN_TABLE.ClickedAction=onMainClickMAIN_TABLE.LockColumns=1,3MAIN_TABLE.FocusType=2TOT_MR_CODE.Type=TTextFormatTOT_MR_CODE.X=405TOT_MR_CODE.Y=105TOT_MR_CODE.Width=170TOT_MR_CODE.Height=23TOT_MR_CODE.Text=TOT_MR_CODE.HorizontalAlignment=2TOT_MR_CODE.PopupMenuHeader=代码,100;名称,150TOT_MR_CODE.PopupMenuWidth=255TOT_MR_CODE.PopupMenuHeight=150TOT_MR_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1TOT_MR_CODE.PopupMenuSQL=SELECT SUBCLASS_CODE AS ID,SUBCLASS_DESC AS NAME,PY1 FROM EMR_TEMPLET WHERE HRM_FLG='Y' AND SYSTEM_CODE='HRM' AND SUBCLASS_DESC LIKE '%总检%'TOT_MR_CODE.FormatType=comboTOT_MR_CODE.ShowDownButton=YTOT_MR_CODE.Tip=总检模板TOT_MR_CODE.ShowColumnList=NAMETOT_MR_CODE.ShowName=Y