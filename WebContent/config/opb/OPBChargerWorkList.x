#
  # Title: 收费员工作量统计
  #
  # Description:收费员工作量统计
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author fudw
  # @version 1.0
<Type=TFrame>
UI.Title=收费员工作统计表
UI.MenuConfig=%ROOT%\config\opb\OPBChargerWorkList_Menu.x
UI.Width=1024
UI.Height=748
UI.toolbar=Y
UI.controlclassname=com.javahis.ui.opb.OPBChargerWorkListControl
UI.item=tLabel_4;tDateField_0;tLabel_5;tDateField_1;tTable_0;tLabel_0;区域下拉列表_0;tLabel_1;门急别下拉列表_0;tLabel_2;人员下拉列表_0
UI.layout=null
UI.FocusList=
UI.X=0
UI.Y=0
UI.TopMenu=Y
UI.TopToolBar=Y
UI.ShowTitle=N
UI.AutoX=N
UI.AutoY=N
UI.AutoWidth=N
UI.AutoHeight=N
UI.AutoSize=0
UI.Border=凸
人员下拉列表_0.Type=人员下拉列表
人员下拉列表_0.X=606
人员下拉列表_0.Y=16
人员下拉列表_0.Width=81
人员下拉列表_0.Height=23
人员下拉列表_0.Text=TButton
人员下拉列表_0.showID=Y
人员下拉列表_0.showName=Y
人员下拉列表_0.showText=N
人员下拉列表_0.showValue=N
人员下拉列表_0.showPy1=Y
人员下拉列表_0.showPy2=Y
人员下拉列表_0.Editable=Y
人员下拉列表_0.Tip=人员
人员下拉列表_0.TableShowList=id,name
人员下拉列表_0.ModuleParmString=
人员下拉列表_0.ModuleParmTag=
人员下拉列表_0.PosType=5
tLabel_2.Type=TLabel
tLabel_2.X=554
tLabel_2.Y=21
tLabel_2.Width=53
tLabel_2.Height=15
tLabel_2.Text=收费员:
门急别下拉列表_0.Type=门急别下拉列表
门急别下拉列表_0.X=216
门急别下拉列表_0.Y=16
门急别下拉列表_0.Width=81
门急别下拉列表_0.Height=23
门急别下拉列表_0.Text=TButton
门急别下拉列表_0.showID=Y
门急别下拉列表_0.showName=Y
门急别下拉列表_0.showText=N
门急别下拉列表_0.showValue=N
门急别下拉列表_0.showPy1=Y
门急别下拉列表_0.showPy2=Y
门急别下拉列表_0.Editable=Y
门急别下拉列表_0.Tip=门急别
门急别下拉列表_0.TableShowList=id,name
门急别下拉列表_0.ModuleParmString=GROUP_ID:SYS_ADMoeTYPE
门急别下拉列表_0.ModuleParmTag=
tLabel_1.Type=TLabel
tLabel_1.X=151
tLabel_1.Y=20
tLabel_1.Width=64
tLabel_1.Height=15
tLabel_1.Text=门急诊别:
区域下拉列表_0.Type=区域下拉列表
区域下拉列表_0.X=56
区域下拉列表_0.Y=15
区域下拉列表_0.Width=81
区域下拉列表_0.Height=23
区域下拉列表_0.Text=TButton
区域下拉列表_0.showID=Y
区域下拉列表_0.showName=Y
区域下拉列表_0.showText=N
区域下拉列表_0.showValue=N
区域下拉列表_0.showPy1=Y
区域下拉列表_0.showPy2=Y
区域下拉列表_0.Editable=Y
区域下拉列表_0.Tip=区域
区域下拉列表_0.TableShowList=id,name
区域下拉列表_0.ModuleParmString=
区域下拉列表_0.ModuleParmTag=
tLabel_0.Type=TLabel
tLabel_0.X=20
tLabel_0.Y=19
tLabel_0.Width=35
tLabel_0.Height=15
tLabel_0.Text=区域:
tTable_0.Type=TTable
tTable_0.X=19
tTable_0.Y=50
tTable_0.Width=995
tTable_0.Height=688
tTable_0.SpacingRow=1
tTable_0.RowHeight=20
tTable_0.AutoWidth=Y
tTable_0.Header=区域,100;门急诊别,100;日期,70;收费员,70;收费次数,70;收费金额,70;退费次数,70;退费金额,70;总金额,70;操作者,100;操作日期,100;使用终端,100
tTable_0.AutoHeight=Y
tTable_0.StringData=[[西安交大,门诊,2008/09/29,S00101,10,1,2000.00,300.00,1700.00,S00101,2008/09/29,127.0.0.1]]
tDateField_1.Type=TDateField
tDateField_1.X=462
tDateField_1.Y=15
tDateField_1.Width=77
tDateField_1.Height=20
tDateField_1.Text=0000/00/00
tLabel_5.Type=TLabel
tLabel_5.X=447
tLabel_5.Y=20
tLabel_5.Width=14
tLabel_5.Height=15
tLabel_5.Text=至
tDateField_0.Type=TDateField
tDateField_0.X=368
tDateField_0.Y=15
tDateField_0.Width=77
tDateField_0.Height=20
tDateField_0.Text=0000/00/00
tLabel_4.Type=TLabel
tLabel_4.X=302
tLabel_4.Y=19
tLabel_4.Width=64
tLabel_4.Height=15
tLabel_4.Text=起迄日期: