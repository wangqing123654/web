################################################ <p>Title:床位检索 </p>## <p>Description:床位检索 </p>## <p>Copyright: Copyright (c) 2008</p>## <p>Company:Javahis </p>## @author JiaoY# @version 1.0###############################################<Type=TFrame>UI.Title=床位检索UI.MenuConfig=%ROOT%\config\mem\MEMUpdateBirthDateMenu.xUI.Width=363UI.Height=381UI.toolbar=YUI.controlclassname=com.javahis.ui.mem.MEMUpdateBirthDateControlUI.Item=tLabel_0;BIRTH_DATE;tLabel_1;GESTATIONAL_WEEKS;tLabel_20;tLabel_21;NEW_BODY_WEIGHT;NEW_BODY_HEIGHT;tLabel_2;tLabel_3UI.TopMenu=YUI.TopToolBar=YUI.MenuMap=UI.ShowTitle=NtLabel_3.Type=TLabeltLabel_3.X=248tLabel_3.Y=143tLabel_3.Width=23tLabel_3.Height=15tLabel_3.Text=cmtLabel_2.Type=TLabeltLabel_2.X=248tLabel_2.Y=98tLabel_2.Width=17tLabel_2.Height=15tLabel_2.Text=gNEW_BODY_HEIGHT.Type=TNumberTextFieldNEW_BODY_HEIGHT.X=78NEW_BODY_HEIGHT.Y=140NEW_BODY_HEIGHT.Width=168NEW_BODY_HEIGHT.Height=20NEW_BODY_HEIGHT.Text=0NEW_BODY_HEIGHT.Format=#########0.00NEW_BODY_WEIGHT.Type=TNumberTextFieldNEW_BODY_WEIGHT.X=78NEW_BODY_WEIGHT.Y=96NEW_BODY_WEIGHT.Width=168NEW_BODY_WEIGHT.Height=20NEW_BODY_WEIGHT.Text=0NEW_BODY_WEIGHT.Format=#########0.00tLabel_21.Type=TLabeltLabel_21.X=7tLabel_21.Y=142tLabel_21.Width=65tLabel_21.Height=15tLabel_21.Text=出生身长:tLabel_20.Type=TLabeltLabel_20.X=8tLabel_20.Y=99tLabel_20.Width=68tLabel_20.Height=15tLabel_20.Text=出生体重:GESTATIONAL_WEEKS.Type=TTextFieldGESTATIONAL_WEEKS.X=79GESTATIONAL_WEEKS.Y=51GESTATIONAL_WEEKS.Width=168GESTATIONAL_WEEKS.Height=20GESTATIONAL_WEEKS.Text=tLabel_1.Type=TLabeltLabel_1.X=7tLabel_1.Y=56tLabel_1.Width=43tLabel_1.Height=15tLabel_1.Text=孕周:BIRTH_DATE.Type=TTextFormatBIRTH_DATE.X=80BIRTH_DATE.Y=11BIRTH_DATE.Width=166BIRTH_DATE.Height=21BIRTH_DATE.Text=BIRTH_DATE.showDownButton=YBIRTH_DATE.FormatType=dateBIRTH_DATE.Format=yyyy/MM/dd HH:mm:ssBIRTH_DATE.NextFocus=GESTATIONAL_WEEKStLabel_0.Type=TLabeltLabel_0.X=10tLabel_0.Y=14tLabel_0.Width=69tLabel_0.Height=15tLabel_0.Text=出生日期: