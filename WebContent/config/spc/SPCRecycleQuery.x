 #  # Title: 麻精空瓶回收记录查询UI  #  # Description:麻精空瓶回收记录查询  #  # Copyright: JavaHis (c) 2013  #  # @author shendr 2013-09-26 # @version 1.0<Type=TFrame>UI.Title=�龫��ƿ���ռ�¼��ѯUI.MenuConfig=%ROOT%\config\spc\SPCRecycleQueryMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.spc.SPCRecycleQueryControlUI.item=tPanel_0;TABLEUI.layout=nullUI.FocusList=UI.X=5TABLE.Type=TTableTABLE.X=5TABLE.Y=59TABLE.Width=1014TABLE.Height=680TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoHeight=YTABLE.Header=ҩƷ����,100;ҩƷ����,150;���,80;�龫�����,100;��������,80;���Ҳ���,100,EXE_DEPT_CODE;��λ��,80;������,100;������Ա,100;����ʱ��,120TABLE.ParmMap=ORDER_CODE;ORDER_DESC;SPECIFICATION;BAR_CODE;PAT_NAME;EXE_DEPT_CODE;BED_NO;MR_NO;RECLAIM_USER;RECLAIM_DATETABLE.Item=EXE_DEPT_CODETABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,leftTABLE.LockColumns=ALLTABLE.ClickedAction=onTableClickedtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=52tPanel_0.Border=��|tPanel_0.Item=tLabel_0;tLabel_2;ORDER_CODE;ORDER_DESC;tLabel_4;RECLAIM_DATE;EXE_DEPT_CODEEXE_DEPT_CODE.Type=������ҩ�������б�EXE_DEPT_CODE.X=88EXE_DEPT_CODE.Y=17EXE_DEPT_CODE.Width=166EXE_DEPT_CODE.Height=20EXE_DEPT_CODE.Text=EXE_DEPT_CODE.HorizontalAlignment=2EXE_DEPT_CODE.PopupMenuHeader=����,100;����,100EXE_DEPT_CODE.PopupMenuWidth=300EXE_DEPT_CODE.PopupMenuHeight=300EXE_DEPT_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1EXE_DEPT_CODE.FormatType=comboEXE_DEPT_CODE.ShowDownButton=YEXE_DEPT_CODE.Tip=ҩ��EXE_DEPT_CODE.ShowColumnList=NAMERECLAIM_DATE.Type=TTextFormatRECLAIM_DATE.X=713RECLAIM_DATE.Y=17RECLAIM_DATE.Width=164RECLAIM_DATE.Height=20RECLAIM_DATE.Text=TTextFormatRECLAIM_DATE.Format=yyyy-MM-dd HH:mm:ssRECLAIM_DATE.FormatType=dateRECLAIM_DATE.showDownButton=YRECLAIM_DATE.HorizontalAlignment=2tLabel_4.Type=TLabeltLabel_4.X=640tLabel_4.Y=19tLabel_4.Width=72tLabel_4.Height=15tLabel_4.Text=����ʱ�䣺tLabel_4.Color=blueORDER_DESC.Type=TTextFieldORDER_DESC.X=444ORDER_DESC.Y=16ORDER_DESC.Width=175ORDER_DESC.Height=20ORDER_DESC.Text=ORDER_DESC.Enabled=NORDER_CODE.Type=TTextFieldORDER_CODE.X=324ORDER_CODE.Y=16ORDER_CODE.Width=103ORDER_CODE.Height=20ORDER_CODE.Text=tLabel_2.Type=TLabeltLabel_2.X=274tLabel_2.Y=19tLabel_2.Width=47tLabel_2.Height=15tLabel_2.Text=ҩƷ:tLabel_2.Color=bluetLabel_0.Type=TLabeltLabel_0.X=18tLabel_0.Y=20tLabel_0.Width=71tLabel_0.Height=15tLabel_0.Text=���Ҳ�����tLabel_0.FontName=tLabel_0.Color=blue