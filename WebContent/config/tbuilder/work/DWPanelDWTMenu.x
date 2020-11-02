UI.item=File;Edit;Window
UI.button=Save;|;SelectObjectToolButton;|;Undo;Redo;|;Delete;|;&
	ModifyText;ModifyFont;ModifyFontSize;|;AlignmentLeft;AlignmentCenter;AlignmentRight;FontTypeB;FontTypeI;|&;&
	Close;|;Refurbish;|;Exit

File.type=TMenu
File.text=�ļ�
File.M=F
File.item=Save;Close;|;Exit

Save.type=TMenuItem
Save.text=����
Save.tip=����
Save.M=S
Save.key=Ctrl+S
Save.Action=onSave
Save.pic=save.gif

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

Edit.type=TMenu
Edit.text=�༭
Edit.M=E
Edit.item=Undo;Redo;|;Delete;|;Preview

Undo.type=TMenuItem
Undo.text=����
Undo.tip=����
Undo.M=U
Undo.key=Ctrl+Z
Undo.Action=onUndo
Undo.pic=Undo.gif

Redo.type=TMenuItem
Redo.text=�ָ�
Redo.tip=�ָ�
Redo.M=R
Redo.key=Shift+Ctrl+Z
Redo.Action=onRedo
Redo.pic=Redo.gif

Delete.type=TMenuItem
Delete.text=ɾ��
Delete.tip=ɾ��
Delete.M=D
Delete.key=delete
Delete.Action=onDelete
Delete.pic=Delete.gif

ModifyText.type=TTextField
ModifyText.tip=�༭����
ModifyText.PreferredWidth=80
ModifyText.Enabled=N

ModifyFont.type=TFontCombo
ModifyFont.PreferredWidth=80
ModifyFont.SelectedAction=onModifyFont

ModifyFontSize.type=TFontSizeCombo
ModifyFontSize.PreferredWidth=40
ModifyFontSize.SelectedAction=onModifyFontSize

AlignmentLeft.type=TMenuItem
AlignmentLeft.text=����
AlignmentLeft.tip=����
AlignmentLeft.Action=onAlignmentLeft
AlignmentLeft.pic=Left.gif

AlignmentCenter.type=TMenuItem
AlignmentCenter.text=����
AlignmentCenter.tip=����
AlignmentCenter.Action=onAlignmentCenter
AlignmentCenter.pic=Center.gif

AlignmentRight.type=TMenuItem
AlignmentRight.text=����
AlignmentRight.tip=����
AlignmentRight.Action=onAlignmentRight
AlignmentRight.pic=Right.gif

FontTypeB.type=TMenuItem
FontTypeB.text=�Ӵ�
FontTypeB.tip=�Ӵ�
FontTypeB.SelectedMode=Y
FontTypeB.Action=onFontTypeB
FontTypeB.pic=B.gif

FontTypeI.type=TMenuItem
FontTypeI.text=��б
FontTypeI.tip=��б
FontTypeI.Action=onFontTypeI
FontTypeI.SelectedMode=Y
FontTypeI.pic=I.gif

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
