UI.item=File;Window
UI.button=SQLCreate;SQLEdit;|;Save;|;SQLCommit;SQLRetrieve;|;Close;|;Refurbish;|;Exit


File.type=TMenu
File.text=�ļ�
File.M=F
File.item=SQLCreate;SQLEdit;|;Save;|;SQLCommit;SQLRetrieve;|;Close;|;Exit

Save.type=TMenuItem
Save.text=����
Save.tip=����
Save.M=S
Save.key=Ctrl+S
Save.Action=onSave
Save.pic=save.gif

SQLCreate.type=TMenuItem
SQLCreate.text=����
SQLCreate.tip=����
SQLCreate.M=T
SQLCreate.key=Ctrl+T
SQLCreate.Action=onSQLCreate
SQLCreate.pic=Create.gif

SQLEdit.type=TMenuItem
SQLEdit.text=�޸�
SQLEdit.tip=�޸�
SQLEdit.M=E
SQLEdit.key=Ctrl+E
SQLEdit.Action=onSQLEdit
SQLEdit.pic=Edit.gif

SQLCommit.type=TMenuItem
SQLCommit.text=�ύ
SQLCommit.tip=�ύ
SQLCommit.M=L
SQLCommit.key=Ctrl+L
SQLCommit.Action=onSQLCommit
SQLCommit.pic=Commit.gif

SQLRetrieve.type=TMenuItem
SQLRetrieve.text=��ѯ
SQLRetrieve.tip=��ѯ
SQLRetrieve.M=L
SQLRetrieve.key=Ctrl+L
SQLRetrieve.Action=onSQLRetrieve
SQLRetrieve.pic=Retrieve.gif

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
