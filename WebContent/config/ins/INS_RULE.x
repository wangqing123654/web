## TBuilder Config File ## Title:## Company:JavaHis## Author:庞犇 2011.11.07## version 1.0#<Type=TFrame>UI.Title=医保三目字典UI.MenuConfig=%ROOT%\config\ins\INS_RULEMenu.xUI.Width=1091UI.Height=758UI.toolbar=YUI.controlclassname=com.javahis.ui.ins.INS_RULEControlUI.item=tPanel_0;tPanel_7;tPanel_8;tMovePane_1;DOSE_CODE;SYSFEE_BUTTON;INSRULE_BUTTON;CORRESONP_BUTTON;SAVE_BUTTONUI.layout=nullUI.LoadFlg=NUI.TopMenu=YUI.TopToolBar=YUI.FocusList=SAVE_BUTTON.Type=TButtonSAVE_BUTTON.X=589SAVE_BUTTON.Y=377SAVE_BUTTON.Width=112SAVE_BUTTON.Height=30SAVE_BUTTON.Text=<--保存选中SAVE_BUTTON.Action=onSaveCORRESONP_BUTTON.Type=TButtonCORRESONP_BUTTON.X=589CORRESONP_BUTTON.Y=295CORRESONP_BUTTON.Width=112CORRESONP_BUTTON.Height=31CORRESONP_BUTTON.Text=当前对应-->CORRESONP_BUTTON.Action=onQueryCorresonpINSRULE_BUTTON.Type=TButtonINSRULE_BUTTON.X=588INSRULE_BUTTON.Y=213INSRULE_BUTTON.Width=113INSRULE_BUTTON.Height=31INSRULE_BUTTON.Text=模糊查询-->INSRULE_BUTTON.Action=onQueryInsruleSYSFEE_BUTTON.Type=TButtonSYSFEE_BUTTON.X=589SYSFEE_BUTTON.Y=131SYSFEE_BUTTON.Width=111SYSFEE_BUTTON.Height=31SYSFEE_BUTTON.Text=<--模糊查询SYSFEE_BUTTON.Action=onQuerySysfeeDOSE_CODE.Type=剂型下拉区域DOSE_CODE.X=108DOSE_CODE.Y=37DOSE_CODE.Width=81DOSE_CODE.Height=23DOSE_CODE.Text=DOSE_CODE.HorizontalAlignment=2DOSE_CODE.PopupMenuHeader=代码,100;名称,100DOSE_CODE.PopupMenuWidth=300DOSE_CODE.PopupMenuHeight=300DOSE_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1DOSE_CODE.FormatType=comboDOSE_CODE.ShowDownButton=YDOSE_CODE.Tip=剂型DOSE_CODE.ShowColumnList=NAMEtMovePane_1.Type=TMovePanetMovePane_1.X=744tMovePane_1.Y=71tMovePane_1.Width=21tMovePane_1.Height=682tMovePane_1.Text=TMovePanetMovePane_1.MoveType=1tMovePane_1.AutoHeight=YtMovePane_1.EntityData=tPanel_7,4;tPanel_8,3tPanel_8.Type=TPaneltPanel_8.X=702tPanel_8.Y=71tPanel_8.Width=384tPanel_8.Height=682tPanel_8.AutoWidth=YtPanel_8.AutoHeight=YtPanel_8.Border=组tPanel_8.Item=TABLE_RULE;tLabel_0;TABLE_SHARETABLE_SHARE.Type=TTableTABLE_SHARE.X=7TABLE_SHARE.Y=475TABLE_SHARE.Width=367TABLE_SHARE.Height=196TABLE_SHARE.SpacingRow=1TABLE_SHARE.RowHeight=20TABLE_SHARE.AutoWidth=YTABLE_SHARE.AutoHeight=YTABLE_SHARE.Header=三目编码,100;医保名称,170;最高限价,60;剂型,80;规格,80;批准文号,120;生产厂商,200;开始时间,140,template,yyyy/MM/dd;结束时间,140,template,yyyy/MM/ddTABLE_SHARE.LockColumns=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14TABLE_SHARE.ColumnHorizontalAlignmentData=1,left;2,left;6,left;7,left;8,left;9,leftTABLE_SHARE.ParmMap=SFXMBM;XMMC;BZJG;JX;GG;PZWH;SCQY;KSSJ;JSSJtLabel_0.Type=TLabeltLabel_0.X=7tLabel_0.Y=455tLabel_0.Width=72tLabel_0.Height=15tLabel_0.Text=共用代码tLabel_0.Color=蓝TABLE_RULE.Type=TTableTABLE_RULE.X=8TABLE_RULE.Y=9TABLE_RULE.Width=367TABLE_RULE.Height=439TABLE_RULE.SpacingRow=1TABLE_RULE.RowHeight=20TABLE_RULE.Header=三目编码,100;医保名称,170;最高限价,60;门诊用药,60,boolean;儿童用药,60,boolean;住院用药,60,boolean;剂型,80;规格,80;批准文号,120;生产厂商,200;开始时间,140,template,yyyy/MM/dd;结束时间,140,template,yyyy/MM/ddTABLE_RULE.AutoHeight=NTABLE_RULE.AutoWidth=YTABLE_RULE.LockColumns=0,1,2,3,4,5,6,7,8,9,10,11,12,13,14TABLE_RULE.ColumnHorizontalAlignmentData=1,left;2,left;6,left;7,left;8,left;9,leftTABLE_RULE.ParmMap=SFXMBM;XMMC;BZJG;MZYYBZ;ETYYBZ;YKD242;JX;GG;PZWH;SCQY;KSSJ;JSSJtPanel_7.Type=TPaneltPanel_7.X=8tPanel_7.Y=70tPanel_7.Width=577tPanel_7.Height=683tPanel_7.Border=组tPanel_7.AutoHeight=YtPanel_7.Item=tPanel_9;tPanel_10tPanel_10.Type=TPaneltPanel_10.X=8tPanel_10.Y=466tPanel_10.Width=558tPanel_10.Height=206tPanel_10.Border=组|历史记录查询tPanel_10.AutoWidth=YtPanel_10.AutoHeight=YtPanel_10.Item=TABLE_HISTORYTABLE_HISTORY.Type=TTableTABLE_HISTORY.X=11TABLE_HISTORY.Y=19TABLE_HISTORY.Width=536TABLE_HISTORY.Height=176TABLE_HISTORY.SpacingRow=1TABLE_HISTORY.RowHeight=20TABLE_HISTORY.Header=医嘱码,60;医嘱名称,200;开始时间,120;结束时间,120;住院医保码,80;门诊医保码,80;急诊医保码,80;医保名称,100;医保价格,60;价格,60;规格,120TABLE_HISTORY.LockColumns=0,1,2,3,4,5,6,7,8,9,10,11,12,13TABLE_HISTORY.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,right;9,right;10,left;11,right;12,rightTABLE_HISTORY.Item=INSPAY_TYPETABLE_HISTORY.ParmMap=ORDER_CODE;ORDER_DESC;START_DATE;END_DATE;NHI_CODE_I;NHI_CODE_O;NHI_CODE_E;NHI_FEE_DESC;NHI_PRICE;OWN_PRICE;SPECIFICATIONTABLE_HISTORY.AutoWidth=YTABLE_HISTORY.AutoHeight=YtPanel_9.Type=TPaneltPanel_9.X=7tPanel_9.Y=4tPanel_9.Width=559tPanel_9.Height=466tPanel_9.Border=组|医嘱查询tPanel_9.AutoWidth=YtPanel_9.Item=TABLE_FEE;INSPAY_TYPE;MAN_CODEMAN_CODE.Type=生产厂商下拉区域MAN_CODE.X=223MAN_CODE.Y=-26MAN_CODE.Width=81MAN_CODE.Height=23MAN_CODE.Text=MAN_CODE.HorizontalAlignment=2MAN_CODE.PopupMenuHeader=代码,100;名称,100MAN_CODE.PopupMenuWidth=300MAN_CODE.PopupMenuHeight=300MAN_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1MAN_CODE.FormatType=comboMAN_CODE.ShowDownButton=YMAN_CODE.Tip=生产厂商MAN_CODE.ShowColumnList=NAMEINSPAY_TYPE.Type=TComboBoxINSPAY_TYPE.X=111INSPAY_TYPE.Y=28INSPAY_TYPE.Width=81INSPAY_TYPE.Height=23INSPAY_TYPE.Text=TButtonINSPAY_TYPE.showID=YINSPAY_TYPE.Editable=YINSPAY_TYPE.StringData=[[id,text],[,],[A,医保],[B,增付],[C,自费]]TABLE_FEE.Type=TTableTABLE_FEE.X=13TABLE_FEE.Y=21TABLE_FEE.Width=537TABLE_FEE.Height=438TABLE_FEE.SpacingRow=1TABLE_FEE.RowHeight=20TABLE_FEE.Header=医嘱码,60;医嘱名称,220;住院医保码,80;门诊医保码,80;急诊医保码,80;设定修改日期,100,template,yyyy/MM/dd;医保名称,100;医保价格,60;价格,60;剂型,80;规格,120;批准文号,120;生产厂商,170TABLE_FEE.LockColumns=0,1,2,3,4,5,6,7,9,10,11,12,13TABLE_FEE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;5,left;6,right;7,right;8,left;9,left;10,left;11,left;12,left;13,leftTABLE_FEE.ParmMap=ORDER_CODE;ORDER_DESC;NHI_CODE_I;NHI_CODE_O;NHI_CODE_E;DATE;NHI_FEE_DESC;NHI_PRICE;OWN_PRICE;DOSE_CHN_DESC;SPECIFICATION;HYGIENE_TRADE_CODE;MAN_CODETABLE_FEE.AutoWidth=NTABLE_FEE.AutoHeight=NTABLE_FEE.Item=MAN_CODE;DOSE_CODE;INSPAY_TYPEtPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=8tPanel_0.Width=1081tPanel_0.Height=57tPanel_0.Border=组|tPanel_0.AutoWidth=YtPanel_0.Item=tPanel_5tPanel_5.Type=TPaneltPanel_5.X=360tPanel_5.Y=2tPanel_5.Width=578tPanel_5.Height=52tPanel_5.Border=组|条件tPanel_5.Item=tLabel_1;tLabel_2;XMRJ;ADM_DATEADM_DATE.Type=TTextFormatADM_DATE.X=324ADM_DATE.Y=16ADM_DATE.Width=98ADM_DATE.Height=20ADM_DATE.Text=ADM_DATE.FormatType=dateADM_DATE.Format=yyyy/MM/ddADM_DATE.HorizontalAlignment=2ADM_DATE.showDownButton=YXMRJ.Type=TTextFieldXMRJ.X=87XMRJ.Y=18XMRJ.Width=112XMRJ.Height=20XMRJ.Text=XMRJ.Action=onQutryDatatLabel_2.Type=TLabeltLabel_2.X=251tLabel_2.Y=19tLabel_2.Width=72tLabel_2.Height=15tLabel_2.Text=查询时间tLabel_2.Color=蓝tLabel_1.Type=TLabeltLabel_1.X=17tLabel_1.Y=22tLabel_1.Width=62tLabel_1.Height=15tLabel_1.Text=拼音查询tLabel_1.Color=蓝