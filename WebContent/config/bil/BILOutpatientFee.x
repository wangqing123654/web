## TBuilder Config File ## Title:## Company:JavaHis## Author:kangy 2016.06.12## version 1.0#<Type=TFrame>UI.Title=门诊减免明细表UI.MenuConfig=%ROOT%\config\bil\BILOutpatientFeeMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.bil.BILOutpatientFeeControlUI.item=tPanel_0;tPanel_1;OPT_USER;REDUCE_USERUI.layout=nullUI.ShowMenu=YUI.TopToolBar=YUI.ShowTitle=NUI.LoadFlg=NUI.TopMenu=YUI.AutoSize=0REDUCE_USER.Type=人员REDUCE_USER.X=349REDUCE_USER.Y=9REDUCE_USER.Width=81REDUCE_USER.Height=23REDUCE_USER.Text=REDUCE_USER.HorizontalAlignment=2REDUCE_USER.PopupMenuHeader=代码,100;名称,100REDUCE_USER.PopupMenuWidth=300REDUCE_USER.PopupMenuHeight=300REDUCE_USER.PopupMenuFilter=ID,1;NAME,1;PY1,1REDUCE_USER.FormatType=comboREDUCE_USER.ShowDownButton=YREDUCE_USER.Tip=人员REDUCE_USER.ShowColumnList=NAMEREDUCE_USER.Visible=NOPT_USER.Type=人员OPT_USER.X=172OPT_USER.Y=5OPT_USER.Width=81OPT_USER.Height=23OPT_USER.Text=OPT_USER.HorizontalAlignment=2OPT_USER.PopupMenuHeader=代码,100;名称,100OPT_USER.PopupMenuWidth=300OPT_USER.PopupMenuHeight=300OPT_USER.PopupMenuFilter=ID,1;NAME,1;PY1,1OPT_USER.FormatType=comboOPT_USER.ShowDownButton=YOPT_USER.Tip=人员OPT_USER.ShowColumnList=NAMEOPT_USER.Visible=NtPanel_1.Type=TPaneltPanel_1.X=7tPanel_1.Y=77tPanel_1.Width=1012tPanel_1.Height=666tPanel_1.Border=组tPanel_1.AutoWidth=YtPanel_1.AutoHeight=YtPanel_1.Item=TABLETABLE.Type=TTableTABLE.X=1TABLE.Y=12TABLE.Width=994TABLE.Height=650TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.LockColumns=allTABLE.Item=OPT_USER;REDUCE_USERtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=13tPanel_0.Width=1014tPanel_0.Height=60tPanel_0.Border=组|查询条件tPanel_0.AutoWidth=YtPanel_0.AutoHeight=NtPanel_0.AutoW=NtPanel_0.AutoH=NtPanel_0.Item=tLabel_0;START_DATE;tLabel_1;END_DATE;QTJM;YZJM;tLabel_10;MR_NOMR_NO.Type=TTextFieldMR_NO.X=709MR_NO.Y=23MR_NO.Width=77MR_NO.Height=20MR_NO.Text=MR_NO.Action=onMrno;onQuerytLabel_10.Type=TLabeltLabel_10.X=657tLabel_10.Y=26tLabel_10.Width=53tLabel_10.Height=15tLabel_10.Text=病案号:tLabel_10.Color=蓝YZJM.Type=TRadioButtonYZJM.X=455YZJM.Y=19YZJM.Width=81YZJM.Height=23YZJM.Text=医嘱减免YZJM.Group=REDUCEYZJM.Color=蓝YZJM.Selected=YYZJM.Action=onQueryQTJM.Type=TRadioButtonQTJM.X=563QTJM.Y=21QTJM.Width=81QTJM.Height=23QTJM.Text=其他减免QTJM.Group=REDUCEQTJM.Color=蓝QTJM.Action=onQuery.Type=TRadioButton.X=450.Y=20.Width=81.Height=23.Text=医嘱减免.Group=REDUCE.Selected=Y.Color=蓝END_DATE.Type=TTextFormatEND_DATE.X=275END_DATE.Y=20END_DATE.Width=160END_DATE.Height=20END_DATE.Text=END_DATE.showDownButton=YEND_DATE.FormatType=dateEND_DATE.Format=yyyy/MM/dd HH:mm:sstLabel_1.Type=TLabeltLabel_1.X=264tLabel_1.Y=28tLabel_1.Width=18tLabel_1.Height=15tLabel_1.Text=~tLabel_1.Color=蓝START_DATE.Type=TTextFormatSTART_DATE.X=101START_DATE.Y=20START_DATE.Width=160START_DATE.Height=20START_DATE.Text=START_DATE.showDownButton=YSTART_DATE.FormatType=dateSTART_DATE.Format=yyyy/MM/dd HH:mm:sstLabel_0.Type=TLabeltLabel_0.X=35tLabel_0.Y=23tLabel_0.Width=68tLabel_0.Height=15tLabel_0.Text=减免时间:tLabel_0.Color=蓝