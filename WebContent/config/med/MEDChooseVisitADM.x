#  # Title: 就诊序号选择  #  # Description:就诊序号选择  #  # Copyright: JavaHis (c) 2008  #  # @author fudw  # @version 1.0<Type=TFrame>UI.Title=就诊号选择//UI.MenuConfig=%ROOT%\config\opb\OPBChooseVisit_Menu.xUI.Width=580UI.Height=450UI.toolbar=YUI.controlclassname=com.javahis.ui.med.MedChooseVisitAdmControlUI.item=tLabel_0;MR_NO;tLabel_1;PAT_NAME;tLabel_2;tLabel_3;AGE;tLabel_4;TABLE;tLabel_13;tButton_2;tButton_3;SEX_CODE;tButton_1;STARTTIME;ENDTIME;SESSION_CODE;DEPT_CODE;DR_CODEUI.layout=nullUI.FocusList=UI.X=0UI.Y=0UI.TopMenu=YUI.TopToolBar=YUI.ShowTitle=NUI.AutoX=NUI.AutoY=NUI.AutoWidth=NUI.AutoHeight=NUI.AutoSize=0UI.Border=凸DR_CODE.Type=人员下拉列表DR_CODE.X=414DR_CODE.Y=141DR_CODE.Width=81DR_CODE.Height=23DR_CODE.Text=TButtonDR_CODE.showID=YDR_CODE.showName=YDR_CODE.showText=NDR_CODE.showValue=NDR_CODE.showPy1=YDR_CODE.showPy2=YDR_CODE.Editable=YDR_CODE.Tip=人员DR_CODE.TableShowList=nameDR_CODE.ModuleParmString=DR_CODE.ModuleParmTag=DEPT_CODE.Type=科室下拉列表DEPT_CODE.X=272DEPT_CODE.Y=148DEPT_CODE.Width=81DEPT_CODE.Height=23DEPT_CODE.Text=TButtonDEPT_CODE.showID=YDEPT_CODE.showName=YDEPT_CODE.showText=NDEPT_CODE.showValue=NDEPT_CODE.showPy1=YDEPT_CODE.showPy2=YDEPT_CODE.Editable=YDEPT_CODE.Tip=科室DEPT_CODE.TableShowList=nameSESSION_CODE.Type=时段下拉列表SESSION_CODE.X=142SESSION_CODE.Y=146SESSION_CODE.Width=81SESSION_CODE.Height=23SESSION_CODE.Text=TButtonSESSION_CODE.showID=YSESSION_CODE.showName=YSESSION_CODE.showText=NSESSION_CODE.showValue=NSESSION_CODE.showPy1=YSESSION_CODE.showPy2=YSESSION_CODE.Editable=YSESSION_CODE.Tip=时段SESSION_CODE.TableShowList=nameSESSION_CODE.ModuleParmString=SESSION_CODE.ModuleParmTag=ENDTIME.Type=TTextFormatENDTIME.X=249ENDTIME.Y=53ENDTIME.Width=110ENDTIME.Height=20ENDTIME.Text=TTextFormatENDTIME.FormatType=dateENDTIME.Format=yyyy/MM/ddENDTIME.showDownButton=YSTARTTIME.Type=TTextFormatSTARTTIME.X=106STARTTIME.Y=53STARTTIME.Width=110STARTTIME.Height=20STARTTIME.Text=TTextFormatSTARTTIME.FormatType=dateSTARTTIME.Format=yyyy/MM/ddSTARTTIME.showDownButton=YtButton_1.Type=TButtontButton_1.X=371tButton_1.Y=52tButton_1.Width=81tButton_1.Height=23tButton_1.Text=查询tButton_1.Action=onQuerySEX_CODE.Type=性别下拉列表SEX_CODE.X=367SEX_CODE.Y=10SEX_CODE.Width=79SEX_CODE.Height=23SEX_CODE.Text=TButtonSEX_CODE.showID=NSEX_CODE.showName=YSEX_CODE.showText=NSEX_CODE.showValue=NSEX_CODE.showPy1=YSEX_CODE.showPy2=YSEX_CODE.Editable=NSEX_CODE.Tip=性别SEX_CODE.TableShowList=nameSEX_CODE.ModuleParmString=GROUP_ID:SYS_SEXSEX_CODE.ModuleParmTag=SEX_CODE.Enabled=NtButton_3.Type=TButtontButton_3.X=311tButton_3.Y=377tButton_3.Width=81tButton_3.Height=23tButton_3.Text=取消tButton_3.Action=onClosetButton_2.Type=TButtontButton_2.X=109tButton_2.Y=377tButton_2.Width=81tButton_2.Height=23tButton_2.Text=传回tButton_2.Action=onOKtLabel_13.Type=TLabeltLabel_13.X=225tLabel_13.Y=61tLabel_13.Width=12tLabel_13.Height=15tLabel_13.Text=~TABLE.Type=TTableTABLE.X=14TABLE.Y=83TABLE.Width=492TABLE.Height=277TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoWidth=YTABLE.Header=就诊日期,100;时段,70,SESSION_CODE;科别,120,DEPT_CODE;医生,100,DR_CODE;就诊序号,100TABLE.StringData=TABLE.ParmMap=ADM_DATE;SESSION_CODE;DEPT_CODE;DR_CODE;CASE_NOTABLE.LockRows=0,1,2,3,4,5TABLE.Item=SESSION_CODE;DEPT_CODE;DR_CODEtLabel_4.Type=TLabeltLabel_4.X=17tLabel_4.Y=54tLabel_4.Width=84tLabel_4.Height=15tLabel_4.Text=就诊起迄日:AGE.Type=TTextFieldAGE.X=488AGE.Y=11AGE.Width=67AGE.Height=20AGE.Text=AGE.InputLength=20AGE.Enabled=NtLabel_3.Type=TLabeltLabel_3.X=449tLabel_3.Y=15tLabel_3.Width=37tLabel_3.Height=15tLabel_3.Text=年龄:tLabel_2.Type=TLabeltLabel_2.X=331tLabel_2.Y=15tLabel_2.Width=36tLabel_2.Height=15tLabel_2.Text=性别:PAT_NAME.Type=TTextFieldPAT_NAME.X=242PAT_NAME.Y=12PAT_NAME.Width=77PAT_NAME.Height=20PAT_NAME.Text=PAT_NAME.InputLength=10PAT_NAME.Enabled=NtLabel_1.Type=TLabeltLabel_1.X=200tLabel_1.Y=15tLabel_1.Width=38tLabel_1.Height=15tLabel_1.Text=姓名:MR_NO.Type=TTextFieldMR_NO.X=77MR_NO.Y=12MR_NO.Width=107MR_NO.Height=20MR_NO.Text=MR_NO.InputLength=20MR_NO.Action=onQueryMR_NO.Enabled=NtLabel_0.Type=TLabeltLabel_0.X=17tLabel_0.Y=16tLabel_0.Width=51tLabel_0.Height=15tLabel_0.Text=病案号: