## TBuilder Config File ## Title:每周套餐## Company:JavaHis## Author:zhangy 2010.11.17## version 1.0#<Type=TFrame>UI.Title=每周套餐设定UI.MenuConfig=%ROOT%\config\nss\NSSWeeklyMenuMenu.xUI.Width=1024UI.Height=748UI.toolbar=YUI.controlclassname=com.javahis.ui.nss.NSSWeeklyMenuControlUI.item=tPanel_0;TABLEUI.layout=nullUI.TopMenu=YUI.TopToolBar=YTABLE.Type=TTableTABLE.X=2TABLE.Y=88TABLE.Width=81TABLE.Height=655TABLE.SpacingRow=1TABLE.RowHeight=20TABLE.AutoX=YTABLE.AutoWidth=YTABLE.AutoHeight=YTABLE.LockColumns=allTABLE.Header=日期,60,WEEKLY_CODE;饮食区分,100,DIET_TYPE;套餐代码,120;周套餐中文,120;周套餐英文,120;拼音码,80;助记码,80;顺序号,80;备注,150;价格,100TABLE.ParmMap=WEEKLY_CODE;DIET_TYPE;PACK_CODE;PACK_CHN_DESC;PACK_ENG_DESC;PY1;PY2;SEQ;DESCRIPTION;PRICETABLE.Item=WEEKLY_CODE;DIET_TYPETABLE.ColumnHorizontalAlignmentData=0,left;1,left;2,left;3,left;4,left;6,left;;8,left;9,rightTABLE.ClickedAction=onTableClicktPanel_0.Type=TPaneltPanel_0.X=5tPanel_0.Y=5tPanel_0.Width=1014tPanel_0.Height=78tPanel_0.AutoX=YtPanel_0.AutoY=YtPanel_0.AutoWidth=YtPanel_0.Border=组tPanel_0.Item=tLabel_34;WEEKLY_CODE;tLabel_35;DIET_TYPE;PACK_CODE;tLabel_36;tLabel_37;PACK_CHN_DESC;tLabel_38;PACK_ENG_DESC;tLabel_39;PY1;tLabel_40;PY2;tLabel_41;SEQ;tLabel_42;DESCRIPTION;tLabel_43;PRICEPRICE.Type=TNumberTextFieldPRICE.X=855PRICE.Y=45PRICE.Width=77PRICE.Height=20PRICE.Text=0PRICE.Format=#########0.00tLabel_43.Type=TLabeltLabel_43.X=771tLabel_43.Y=48tLabel_43.Width=72tLabel_43.Height=15tLabel_43.Text=价格:DESCRIPTION.Type=TTextFieldDESCRIPTION.X=565DESCRIPTION.Y=45DESCRIPTION.Width=184DESCRIPTION.Height=20DESCRIPTION.Text=tLabel_42.Type=TLabeltLabel_42.X=513tLabel_42.Y=48tLabel_42.Width=46tLabel_42.Height=15tLabel_42.Text=备注:SEQ.Type=TNumberTextFieldSEQ.X=409SEQ.Y=45SEQ.Width=77SEQ.Height=20SEQ.Text=0SEQ.Format=#########0tLabel_41.Type=TLabeltLabel_41.X=337tLabel_41.Y=48tLabel_41.Width=72tLabel_41.Height=15tLabel_41.Text=顺序号:PY2.Type=TTextFieldPY2.X=241PY2.Y=45PY2.Width=80PY2.Height=20PY2.Text=tLabel_40.Type=TLabeltLabel_40.X=180tLabel_40.Y=48tLabel_40.Width=56tLabel_40.Height=15tLabel_40.Text=助记码:PY1.Type=TTextFieldPY1.X=72PY1.Y=45PY1.Width=80PY1.Height=20PY1.Text=tLabel_39.Type=TLabeltLabel_39.X=14tLabel_39.Y=48tLabel_39.Width=56tLabel_39.Height=15tLabel_39.Text=拼音码:PACK_ENG_DESC.Type=TTextFieldPACK_ENG_DESC.X=855PACK_ENG_DESC.Y=9PACK_ENG_DESC.Width=120PACK_ENG_DESC.Height=20PACK_ENG_DESC.Text=tLabel_38.Type=TLabeltLabel_38.X=769tLabel_38.Y=12tLabel_38.Width=80tLabel_38.Height=15tLabel_38.Text=周套餐英文:PACK_CHN_DESC.Type=TTextFieldPACK_CHN_DESC.X=629PACK_CHN_DESC.Y=9PACK_CHN_DESC.Width=120PACK_CHN_DESC.Height=20PACK_CHN_DESC.Text=PACK_CHN_DESC.Action=onPackDescActiontLabel_37.Type=TLabeltLabel_37.X=548tLabel_37.Y=12tLabel_37.Width=77tLabel_37.Height=15tLabel_37.Text=周套餐中文:tLabel_36.Type=TLabeltLabel_36.X=337tLabel_36.Y=12tLabel_36.Width=69tLabel_36.Height=15tLabel_36.Text=套餐代码:tLabel_36.Color=bluePACK_CODE.Type=饮食套餐下拉区域PACK_CODE.X=409PACK_CODE.Y=7PACK_CODE.Width=120PACK_CODE.Height=23PACK_CODE.Text=PACK_CODE.HorizontalAlignment=2PACK_CODE.PopupMenuHeader=代码,100;名称,100PACK_CODE.PopupMenuWidth=300PACK_CODE.PopupMenuHeight=300PACK_CODE.PopupMenuFilter=ID,1;NAME,1;PY1,1PACK_CODE.FormatType=comboPACK_CODE.ShowDownButton=YPACK_CODE.Tip=饮食套餐PACK_CODE.ShowColumnList=NAMEPACK_CODE.HisOneNullRow=YPACK_CODE.DietType=<DIET_TYPE>PACK_CODE.Action=onChangeNSSPackDIET_TYPE.Type=饮食分类下拉区域DIET_TYPE.X=221DIET_TYPE.Y=7DIET_TYPE.Width=100DIET_TYPE.Height=23DIET_TYPE.Text=DIET_TYPE.HorizontalAlignment=2DIET_TYPE.PopupMenuHeader=代码,100;名称,100DIET_TYPE.PopupMenuWidth=300DIET_TYPE.PopupMenuHeight=300DIET_TYPE.PopupMenuFilter=ID,1;NAME,1;PY1,1DIET_TYPE.FormatType=comboDIET_TYPE.ShowDownButton=YDIET_TYPE.Tip=饮食分类DIET_TYPE.ShowColumnList=NAMEDIET_TYPE.HisOneNullRow=YDIET_TYPE.Action=PACK_CODE|onQuerytLabel_35.Type=TLabeltLabel_35.X=150tLabel_35.Y=12tLabel_35.Width=70tLabel_35.Height=15tLabel_35.Text=饮食区分:tLabel_35.Color=blueWEEKLY_CODE.Type=TComboBoxWEEKLY_CODE.X=54WEEKLY_CODE.Y=7WEEKLY_CODE.Width=80WEEKLY_CODE.Height=23WEEKLY_CODE.Text=TButtonWEEKLY_CODE.showID=YWEEKLY_CODE.Editable=YWEEKLY_CODE.ShowText=NWEEKLY_CODE.ShowName=YWEEKLY_CODE.TableShowList=nameWEEKLY_CODE.StringData=[[id,name],[,],[01,周一],[02,周二],[03,周三],[04,周四],[05,周五],[06,周六],[07,周日]]tLabel_34.Type=TLabeltLabel_34.X=14tLabel_34.Y=12tLabel_34.Width=39tLabel_34.Height=15tLabel_34.Text=日期:tLabel_34.Color=blue