## TBuilder Config File ## Title:输血反应查询## Company:JavaHis## Author:zhangy 2009.12.22## version 1.0#<Type=TFrame>UI.Title=输血反应查询UI.MenuConfig=%ROOT%\config\bms\BMSSplreactQueryMenu.xUI.Width=520UI.Height=400UI.toolbar=YUI.controlclassname=com.javahis.ui.bms.BMSSpleractQueryControlUI.item=TABLE;DEPT_CODE;STATION_CODEUI.layout=nullUI.TopMenu=YUI.TopToolBar=YSTATION_CODE.Type=病区下拉列表STATION_CODE.X=103STATION_CODE.Y=16STATION_CODE.Width=81STATION_CODE.Height=23STATION_CODE.Text=TButtonSTATION_CODE.showID=YSTATION_CODE.showName=YSTATION_CODE.showText=NSTATION_CODE.showValue=NSTATION_CODE.showPy1=NSTATION_CODE.showPy2=NSTATION_CODE.Editable=YSTATION_CODE.Tip=病区STATION_CODE.TableShowList=nameDEPT_CODE.Type=科室下拉列表DEPT_CODE.X=15DEPT_CODE.Y=14DEPT_CODE.Width=81DEPT_CODE.Height=23DEPT_CODE.Text=TButtonDEPT_CODE.showID=YDEPT_CODE.showName=YDEPT_CODE.showText=NDEPT_CODE.showValue=NDEPT_CODE.showPy1=NDEPT_CODE.showPy2=NDEPT_CODE.Editable=YDEPT_CODE.Tip=科室DEPT_CODE.TableShowList=nameTABLE.Type=TTableTABLE.X=11TABLE.Y=57TABLE.Width=510TABLE.Height=338TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoY=YTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.Header=科室,100,DEPT_CODE;病区,100,STATION_CODE;病案号,100;就诊序号,100;住院号,100TABLE.ParmMap=DEPT_CODE;STATION_CODE;MR_NO;CASE_NO;IPD_NOTABLE.LockColumns=allTABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,leftTABLE.Item=DEPT_CODE;STATION_CODE