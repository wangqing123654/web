## TBuilder Config File ## Title:结构化病例## Company:JavaHis## Author:WangM 2009.10.06## version 1.0#<Type=TFrame>UI.Title=电子病历UI.MenuConfig=%ROOT%\config\emr\EMRUIMenu.xUI.Width=1280UI.Height=800UI.toolbar=YUI.controlclassname=com.javahis.ui.emr.TEmrWordNewControlUI.item=tPanel_5;TREEPANEL;MOVEPANE;PANELUI.layout=nullUI.AutoSize=3UI.zhTitle=电子病历UI.enTitle=EMRPANEL.Type=TPanelPANEL.X=284PANEL.Y=46PANEL.Width=993PANEL.Height=749PANEL.Border=凹PANEL.AutoWidth=YPANEL.AutoHeight=YPANEL.AutoSize=3PANEL.Item=WORDWORD.Type=TWordWORD.X=2WORD.Y=5WORD.Width=986WORD.Height=741WORD.AutoX=YWORD.AutoY=YWORD.AutoWidth=YWORD.AutoHeight=YWORD.AutoSize=3MOVEPANE.Type=TMovePaneMOVEPANE.X=281MOVEPANE.Y=46MOVEPANE.Width=2MOVEPANE.Height=747MOVEPANE.Text=MOVEPANE.MoveType=1MOVEPANE.Style=3MOVEPANE.DoubleClickType=1MOVEPANE.EntityData=TREEPANEL,4;PANEL,3TREEPANEL.Type=TPanelTREEPANEL.X=3TREEPANEL.Y=45TREEPANEL.Width=276TREEPANEL.Height=752TREEPANEL.AutoSize=3TREEPANEL.AutoX=YTREEPANEL.AutoY=NTREEPANEL.AutoHeight=YTREEPANEL.Border=凸TREEPANEL.Item=TREE;SVAE_PAT;CLINIC_DATA;SUB_TEMPLATE;SPECIAL_CHARS;NATION_STANDARD;CUSTOM_SORT1;CUSTOM_SORT2TREEPANEL.MenuConfig=CUSTOM_SORT2.Type=TButtonCUSTOM_SORT2.X=174CUSTOM_SORT2.Y=609CUSTOM_SORT2.Width=85CUSTOM_SORT2.Height=23CUSTOM_SORT2.Text=科室分类CUSTOM_SORT2.Action=onCustomSort2CUSTOM_SORT2.Visible=NCUSTOM_SORT1.Type=TButtonCUSTOM_SORT1.X=141CUSTOM_SORT1.Y=573CUSTOM_SORT1.Width=130CUSTOM_SORT1.Height=23CUSTOM_SORT1.Text=医疗过程CUSTOM_SORT1.Action=onCustomSort1NATION_STANDARD.Type=TButtonNATION_STANDARD.X=5NATION_STANDARD.Y=573NATION_STANDARD.Width=130NATION_STANDARD.Height=23NATION_STANDARD.Text=卫生部分类NATION_STANDARD.Action=onNationStandardSPECIAL_CHARS.Type=TButtonSPECIAL_CHARS.X=212SPECIAL_CHARS.Y=5SPECIAL_CHARS.Width=59SPECIAL_CHARS.Height=23SPECIAL_CHARS.Text=特殊字符SPECIAL_CHARS.Action=onSpecialCharsSPECIAL_CHARS.FontSize=12SUB_TEMPLATE.Type=TButtonSUB_TEMPLATE.X=157SUB_TEMPLATE.Y=5SUB_TEMPLATE.Width=49SUB_TEMPLATE.Height=23SUB_TEMPLATE.Text=子模版SUB_TEMPLATE.zhText=SUB_TEMPLATE.Action=onSubTemplateSUB_TEMPLATE.FontSize=12CLINIC_DATA.Type=TButtonCLINIC_DATA.X=93CLINIC_DATA.Y=5CLINIC_DATA.Width=59CLINIC_DATA.Height=23CLINIC_DATA.Text=临床数据CLINIC_DATA.FontSize=12CLINIC_DATA.Action=onInsertLCSJSVAE_PAT.Type=TButtonSVAE_PAT.X=5SVAE_PAT.Y=5SVAE_PAT.Width=82SVAE_PAT.Height=23SVAE_PAT.Text=收藏病患信息SVAE_PAT.FontSize=12SVAE_PAT.Action=onSavePatInfoTREE.Type=TTreeTREE.X=4TREE.Y=30TREE.Width=266TREE.Height=540TREE.SpacingRow=1TREE.RowHeight=20TREE.Pics=Root:sys.gif;1:dir1.gif;2:dir1.gif;3:dir1.gif;4:check_up_rpt.gifTREE.AutoX=YTREE.AutoY=NTREE.AutoWidth=YTREE.AutoHeight=NTREE.AutoSize=3TREE.typePopupMenuTags=1:ADDALLMENU;2:OPENMENU;3:ADDMENU;4:MENUCHECKtPanel_5.Type=TPaneltPanel_5.X=3tPanel_5.Y=3tPanel_5.Width=1274tPanel_5.Height=42tPanel_5.Border=凸tPanel_5.AutoX=YtPanel_5.AutoY=YtPanel_5.AutoWidth=YtPanel_5.Item=tLabel_1;MR_NO;PAT_NAME;IPD_LAB;IPD_NO;CANPRINT_FLG;MODIFY_FLG;SHOW_ROW_LINE;TB_CONTINUE_PRINTtPanel_5.AutoSize=3TB_CONTINUE_PRINT.Type=TButtonTB_CONTINUE_PRINT.X=792TB_CONTINUE_PRINT.Y=9TB_CONTINUE_PRINT.Width=81TB_CONTINUE_PRINT.Height=23TB_CONTINUE_PRINT.Text=续印TB_CONTINUE_PRINT.Action=onPrintXDDialogSHOW_ROW_LINE.Type=TCheckBoxSHOW_ROW_LINE.X=690SHOW_ROW_LINE.Y=10SHOW_ROW_LINE.Width=81SHOW_ROW_LINE.Height=23SHOW_ROW_LINE.Text=显示行号SHOW_ROW_LINE.Action=onShowRowIDSwitchMODIFY_FLG.Type=TCheckBoxMODIFY_FLG.X=588MODIFY_FLG.Y=10MODIFY_FLG.Width=81MODIFY_FLG.Height=20MODIFY_FLG.Text=允许修改MODIFY_FLG.Visible=NMODIFY_FLG.Selected=YCANPRINT_FLG.Type=TCheckBoxCANPRINT_FLG.X=493CANPRINT_FLG.Y=10CANPRINT_FLG.Width=81CANPRINT_FLG.Height=20CANPRINT_FLG.Text=允许打印CANPRINT_FLG.Visible=NCANPRINT_FLG.Selected=YIPD_NO.Type=TTextFieldIPD_NO.X=341IPD_NO.Y=10IPD_NO.Width=124IPD_NO.Height=22IPD_NO.Text=IPD_NO.Enabled=NIPD_LAB.Type=TLabelIPD_LAB.X=287IPD_LAB.Y=10IPD_LAB.Width=53IPD_LAB.Height=22IPD_LAB.Text=住院号IPD_LAB.zhText=住院号IPD_LAB.enText=IpdNoPAT_NAME.Type=TTextFieldPAT_NAME.X=185PAT_NAME.Y=10PAT_NAME.Width=85PAT_NAME.Height=22PAT_NAME.Text=PAT_NAME.Enabled=NMR_NO.Type=TTextFieldMR_NO.X=65MR_NO.Y=10MR_NO.Width=114MR_NO.Height=22MR_NO.Text=MR_NO.Enabled=NtLabel_1.Type=TLabeltLabel_1.X=9tLabel_1.Y=10tLabel_1.Width=50tLabel_1.Height=22tLabel_1.Text=病案号tLabel_1.zhText=病案号tLabel_1.HorizontalAlignment=tLabel_1.enText=MrNoADDALLMENU.item=creatMenuOPENMENU.item=openMenuADDMENU.item=addMenuMENUCHECK.item=delFilecreatMenu.Type=TMenuItemcreatMenu.Text=新建病历creatMenu.zhText=新建病历creatMenu.enText=New MedicalcreatMenu.M=NcreatMenu.Action=onCreatMenuopenMenu.Type=TMenuItemopenMenu.Text=打开病历openMenu.zhText=打开病历openMenu.enText=Open MedicalopenMenu.M=OopenMenu.Action=onOpenMenuaddMenu.Type=TMenuItemaddMenu.Text=新增病历addMenu.zhText=新增病历addMenu.enText=Append MedicaladdMenu.M=DaddMenu.Action=onAddMenudelFile.Type=TMenuItemdelFile.Text=删除病历delFile.zhText=删除病历delFile.enText=Delete MedicaldelFile.M=DeletedelFile.Action=onDelFile