UI.item=File;Window
UI.button=Save;|;Print;|;PriviewZoom;PrintZoom;|;Close;|;Refurbish;|;Exit


PriviewZoom.type=TSpinner
PriviewZoom.PreferredWidth=40
PriviewZoom.text=100
PriviewZoom.ChangedAction=onPriviewZoomChanged

PrintZoom.type=TSpinner
PrintZoom.PreferredWidth=40
PrintZoom.text=100
PrintZoom.ChangedAction=onPrintZoomChanged

File.type=TMenu
File.text=文件
File.M=F
File.item=Save;|;PrintSetup;Print;|;Close;|;Exit

Save.type=TMenuItem
Save.text=保存
Save.tip=保存
Save.M=S
Save.key=Ctrl+S
Save.Action=onSave
Save.pic=save.gif

PrintSetup.type=TMenuItem
PrintSetup.text=打印设置
PrintSetup.tip=打印设置
PrintSetup.M=U
PrintSetup.Action=onPrintSetup

Print.type=TMenuItem
Print.text=打印
Print.tip=打印
Print.M=P
Print.Action=onPrint
Print.pic=Print.gif

Close.type=TMenuItem
Close.text=关闭
Close.tip=关闭
Close.M=C
Close.key=Ctrl+Q
Close.Action=onClosePanel
Close.pic=close1.gif

Exit.type=TMenuItem
Exit.text=退出
Exit.tip=退出
Exit.M=X
Exit.key=Ctrl+F4
Exit.Action=onClose
Exit.pic=close.gif

Preview.type=TMenuItem
Preview.text=阅览
Preview.tip=阅览窗口
Preview.M=P
Preview.key=Ctrl+P
Preview.Action="onPreview"
Preview.pic=Preview.gif


Window.type=TMenu
Window.text=窗口
Window.M=W
Window.item=Refurbish;RefurbishAction

Refurbish.type=TMenuItem
Refurbish.text=刷新
Refurbish.tip=刷新
Refurbish.M=R
Refurbish.key=F5
Refurbish.Action="onReset"
Refurbish.pic=Refresh.gif

RefurbishAction.type=TMenuItem
RefurbishAction.text=刷新Action
RefurbishAction.tip=刷新Action
RefurbishAction.M=A
RefurbishAction.key=F6
RefurbishAction.Action="onResetAction"
RefurbishAction.pic=Refresh.gif

SelectObjectToolButton.type=TComboButton
SelectObjectToolButton.ColumnCount=2
SelectObjectToolButton.RowCount=3
SelectObjectToolButton.Button=SOTB_ARROW
SelectObjectToolButton.Items=SOTB_ARROW,选择对象,Arrow.gif,onSOTBArrow;&
			     SOTB_TEXT,创建文本,Text.gif,onSOTBText;&
			     SOTB_COLUMN,创建列,Column.gif,onSOTBColumn;&
			     SOTB_LINE,创建线,Line.gif,onSOTBLine;&
			     SOTB_PICTURE,创建图片,Picture.gif,onSOTBPicture;&
			     SOTB_BUTTON,创建按钮,Button.gif,onSOTBButton;&
			     SOTB_GROUP,创建组,Group.gif,onSOTBGroup
