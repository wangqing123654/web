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
File.text=�ļ�
File.M=F
File.item=Save;|;PrintSetup;Print;|;Close;|;Exit

Save.type=TMenuItem
Save.text=����
Save.tip=����
Save.M=S
Save.key=Ctrl+S
Save.Action=onSave
Save.pic=save.gif

PrintSetup.type=TMenuItem
PrintSetup.text=��ӡ����
PrintSetup.tip=��ӡ����
PrintSetup.M=U
PrintSetup.Action=onPrintSetup

Print.type=TMenuItem
Print.text=��ӡ
Print.tip=��ӡ
Print.M=P
Print.Action=onPrint
Print.pic=Print.gif

Close.type=TMenuItem
Close.text=�ر�
Close.tip=�ر�
Close.M=C
Close.key=Ctrl+Q
Close.Action=onClosePanel
Close.pic=close1.gif

Exit.type=TMenuItem
Exit.text=�˳�
Exit.tip=�˳�
Exit.M=X
Exit.key=Ctrl+F4
Exit.Action=onClose
Exit.pic=close.gif

Preview.type=TMenuItem
Preview.text=����
Preview.tip=��������
Preview.M=P
Preview.key=Ctrl+P
Preview.Action="onPreview"
Preview.pic=Preview.gif


Window.type=TMenu
Window.text=����
Window.M=W
Window.item=Refurbish;RefurbishAction

Refurbish.type=TMenuItem
Refurbish.text=ˢ��
Refurbish.tip=ˢ��
Refurbish.M=R
Refurbish.key=F5
Refurbish.Action="onReset"
Refurbish.pic=Refresh.gif

RefurbishAction.type=TMenuItem
RefurbishAction.text=ˢ��Action
RefurbishAction.tip=ˢ��Action
RefurbishAction.M=A
RefurbishAction.key=F6
RefurbishAction.Action="onResetAction"
RefurbishAction.pic=Refresh.gif

SelectObjectToolButton.type=TComboButton
SelectObjectToolButton.ColumnCount=2
SelectObjectToolButton.RowCount=3
SelectObjectToolButton.Button=SOTB_ARROW
SelectObjectToolButton.Items=SOTB_ARROW,ѡ�����,Arrow.gif,onSOTBArrow;&
			     SOTB_TEXT,�����ı�,Text.gif,onSOTBText;&
			     SOTB_COLUMN,������,Column.gif,onSOTBColumn;&
			     SOTB_LINE,������,Line.gif,onSOTBLine;&
			     SOTB_PICTURE,����ͼƬ,Picture.gif,onSOTBPicture;&
			     SOTB_BUTTON,������ť,Button.gif,onSOTBButton;&
			     SOTB_GROUP,������,Group.gif,onSOTBGroup
