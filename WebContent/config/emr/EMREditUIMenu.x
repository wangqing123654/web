<Type=TMenuBar>
UI.Item=File;EditMenu;Insert;InsWidget
UI.button=save;|;ModifyFontCombo;ModifyFontSizeCombo;FontBMenu;FontIMenu;|;InsertCurrentTime;|;delFixText;|;ClearMenu;|;SavePatInfo;|;exit

EditMenu.Type=TMenu
EditMenu.Text=�༭
EditMenu.zhText=�༭
EditMenu.enTip=Apply
EditMenu.enText=Apply
EditMenu.M=E
EditMenu.Item=ClearMenu;CutMenu;CopyMenu;PasteMenu;DeleteMenu;addDLText;delFixText

File.Type=TMenu
File.Text=�ļ�
File.zhText=�ļ�
File.enText=File
File.M=F
File.Item=save;|;exit

Insert.Type=TMenu
Insert.Text=����
Insert.zhText=����
Insert.enText=Insert
Insert.M=I
Insert.Item=InsertPY;InsertLCSJ;InsertTemplatePY;InsertPatInfo;|;InsertCurrentTime


InsWidget.Type=TMenu
InsWidget.Text=�ؼ�
InsWidget.zhText=�ؼ�
InsWidget.enText=Insert Widget
InsWidget.M=I
InsWidget.Item=InsertCurrentTime


CutMenu.Type=TMenuItem
CutMenu.Text=����
CutMenu.zhText=����
CutMenu.enText=Cut
CutMenu.M=T
CutMenu.Action=onCutMenu
cutMenu.Key=Ctrl+X

CopyMenu.Type=TMenuItem
CopyMenu.Text=����
CopyMenu.zhText=����
CopyMenu.enText=Copy
CopyMenu.M=C
CopyMenu.Action=onCopyMenu
CopyMenu.Key=Ctrl+C

PasteMenu.Type=TMenuItem
PasteMenu.Text=ճ��
PasteMenu.zhText=ճ��
PasteMenu.enText=Paste
PasteMenu.M=P
PasteMenu.Action=onPasteMenu
PasteMenu.Key=Ctrl+V

DeleteMenu.Type=TMenuItem
DeleteMenu.Text=ɾ��
DeleteMenu.zhText=ɾ��
DeleteMenu.enText=Delete
DeleteMenu.M=D
DeleteMenu.Action=onDeleteMenu
DeleteMenu.Key=Delete

save.Type=TMenuItem
save.Text=����
save.zhText=����
save.enText=Save
save.Tip=����
save.zhTip=����
save.enTip=Save
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

printIndex.Type=TMenuItem
printIndex.Text=�Զ����ӡ
printIndex.zhText=�Զ����ӡ
printIndex.enText=Custom Print
printIndex.Tip=�Զ����ӡ
printIndex.zhTip=�Զ����ӡ
printIndex.enTip=Save
printIndex.M=S
printIndex.key=Ctrl+P
printIndex.Action=onPrintIndex
printIndex.pic=print.gif

submit.Type=TMenuItem
submit.Text=�ύ
submit.zhText=�ύ
submit.enText=Commit
submit.Tip=�ύ
submit.zhTip=�ύ
submit.enTip=Commit
submit.M=T
submit.key=Ctrl+T
submit.Action=onSubmit
submit.pic=017.gif

submitCancel.Type=TMenuItem
submitCancel.Text=ȡ���ύ
submitCancel.zhText=ȡ���ύ
submitCancel.enText=Cancel Commit
submitCancel.Tip=ȡ���ύ
submitCancel.zhTip=ȡ���ύ
submitCancel.enTip=Cancel Commit
submitCancel.M=T
submitCancel.key=Ctrl+P
submitCancel.Action=onSubmitCancel
submitCancel.pic=030.gif

applyEdit.Type=TMenuItem
applyEdit.Text=����༭
applyEdit.zhText=����༭
applyEdit.enText=Apply Edit
applyEdit.Tip=����༭
applyEdit.zhTip=����༭
applyEdit.enTip=Apply Edit
applyEdit.M=A
applyEdit.key=
applyEdit.Action=onEdit
applyEdit.pic=unlock.gif

cancelEdit.Type=TMenuItem
cancelEdit.Text=ȡ���༭
cancelEdit.zhText=ȡ���༭
cancelEdit.enText=Cancel
cancelEdit.Tip=ȡ���༭
cancelEdit.zhTip=ȡ���༭
cancelEdit.enTip=Cancel
cancelEdit.M=N
cancelEdit.key=
cancelEdit.Action=onCancelEdit
cancelEdit.pic=lock.gif

InsertTable.type=TMenuItem
InsertTable.Text=������
InsertTable.zhText=������
InsertTable.enText=Insert Table
InsertTable.Tip=������
InsertTable.zhTip=������
InsertTable.enTip=Insert Table
InsertTable.M=N
InsertTable.key=
InsertTable.Action=onInsertTable
InsertTable.pic=inscon.gif

DelTable.type=TMenuItem
DelTable.Text=ɾ�����
DelTable.zhText=ɾ�����
DelTable.enText=Delete Table
DelTable.Tip=ɾ�����
DelTable.zhTip=ɾ�����
DelTable.enTip=Delete Table
DelTable.M=D
DelTable.key=
DelTable.Action=onDelTable
DelTable.pic=cancle.gif

InsertTableRow.type=TMenuItem
InsertTableRow.Text=��������
InsertTableRow.zhText=��������
InsertTableRow.enText=Insert Row
InsertTableRow.Tip=��������
InsertTableRow.zhTip=��������
InsertTableRow.enTip=Insert Row
InsertTableRow.M=R
InsertTableRow.key=
InsertTableRow.Action=onInsertTableRow
InsertTableRow.pic=sys.gif

AddTableRow.type=TMenuItem
AddTableRow.Text=׷�ӱ����
AddTableRow.zhText=׷�ӱ����
AddTableRow.enText=Add Row
AddTableRow.Tip=׷�ӱ����
AddTableRow.zhTip=׷�ӱ����
AddTableRow.enTip=Add  Row
AddTableRow.M=A
AddTableRow.key=
AddTableRow.Action=onAddTableRow
AddTableRow.pic=change.gif

DelTableRow.type=TMenuItem
DelTableRow.Text=ɾ�������
DelTableRow.zhText=ɾ�������
DelTableRow.enText=Delete Row
DelTableRow.Tip=ɾ�������
DelTableRow.zhTip=ɾ�������
DelTableRow.enTip=Delete Row
DelTableRow.M=B
DelTableRow.key=
DelTableRow.Action=onDelTableRow
DelTableRow.pic=cancle.gif

InsertPY.type=TMenuItem
InsertPY.Text=����Ƭ��
InsertPY.zhText=����Ƭ��
InsertPY.enText=Insert Phrase
InsertPY.Tip=����Ƭ��
InsertPY.zhTip=����Ƭ��
InsertPY.enTip=Insert Phrase
InsertPY.M=P
InsertPY.key=
InsertPY.Action=onInsertPY
InsertPY.pic=sys.gif


InsertTemplatePY.type=TMenuItem
InsertTemplatePY.Text=������ģ��
InsertTemplatePY.zhText=������ģ��
InsertTemplatePY.enText=Insert Template Phrase
InsertTemplatePY.Tip=������ģ��
InsertTemplatePY.zhTip=������ģ��
InsertTemplatePY.enTip=Insert Template Phrase
InsertTemplatePY.M=P
InsertTemplatePY.key=
InsertTemplatePY.Action=onInsertTemplatePY
InsertTemplatePY.pic=sys.gif


InsertLCSJ.type=TMenuItem
InsertLCSJ.Text=�ٴ�����
InsertLCSJ.zhText=�ٴ�����
InsertLCSJ.enText=Clinical data
InsertLCSJ.Tip=�ٴ�����
InsertLCSJ.zhTip=�ٴ�����
InsertLCSJ.enTip=Clinical data
InsertLCSJ.M=C
InsertLCSJ.key=
InsertLCSJ.Action=onInsertLCSJ
InsertLCSJ.pic=sys.gif

PrintSetup.type=TMenuItem
PrintSetup.Text=��ӡ����
PrintSetup.zhText=��ӡ����
PrintSetup.enText=Print Setup
PrintSetup.Tip=��ӡ����
PrintSetup.zhTip=��ӡ����
PrintSetup.enTip=Print Setup
PrintSetup.M=S
PrintSetup.key=
PrintSetup.Action=onPrintSetup
PrintSetup.pic=print-2.gif

PrintShow.type=TMenuItem
PrintShow.Text=��ӡ
PrintShow.zhText=��ӡ
PrintShow.enText=Print
PrintShow.Tip=��ӡ
PrintShow.zhTip=��ӡ
PrintShow.enTip=Print
PrintShow.M=P
PrintShow.key=
PrintShow.Action=onPrint
PrintShow.pic=print.gif

PrintClear.Type=TMenuItem
PrintClear.Text=����Ԥ��
PrintClear.zhText=����Ԥ��
PrintClear.enText=preview
PrintClear.Tip=����Ԥ��
PrintClear.zhTip=����Ԥ��
PrintClear.enTip=preview
PrintClear.M=N
PrintClear.key=
PrintClear.Action=onPrintClear
PrintClear.pic=Retrieve.gif

HongR.Type=TMenuItem
HongR.Text=��ˢ��
HongR.zhText=��ˢ��
HongR.enText=preview
HongR.Tip=��ˢ��
HongR.zhTip=��ˢ��
HongR.enTip=preview
HongR.M=N
HongR.key=
HongR.Action=onHongR
HongR.pic=Redo.gif

exit.Type=TMenuItem
exit.Text=�ر�
exit.zhText=�ر�
exit.enText=Quit
exit.Tip=�ر�
exit.zhTip=�ر�
exit.enTip=Quit
exit.M=C
exit.key=Alt+F4
exit.Action=onClose
exit.pic=close.gif

LinkPrint.Type=TMenuItem
LinkPrint.Text=��ӡ
LinkPrint.zhText=��ӡ
LinkPrint.enText=Continue Print
LinkPrint.Enabled=true
LinkPrint.M=T
LinkPrint.Action=onPrintXDDialog

ShowRowIDSwitch.Type=TMenuItem
ShowRowIDSwitch.Text=��ʾ�кſ���
ShowRowIDSwitch.zhText=��ʾ�кſ���
ShowRowIDSwitch.enText=Show Line No
ShowRowIDSwitch.Enabled=true
ShowRowIDSwitch.M=T
ShowRowIDSwitch.Action=onShowRowIDSwitch

addDLText.type=TMenuItem
addDLText.Text=����
addDLText.zhText=����
addDLText.enText=Class
addDLText.Tip=����
addDLText.zhTip=����
addDLText.enTip=Class
addDLText.M=E
addDLText.key=
addDLText.Action=onAddDLText

ImageEditMenu.Type=TMenu
ImageEditMenu.Text=ͼƬ�༭��
ImageEditMenu.M=A
ImageEditMenu.Item=InsertImageEditMenu;DeleteImageEditMenu;InsertBlockMenu;InsertGLineMenu;|;SizeBlockMenu

InsertImageEditMenu.Type=TMenuItem
InsertImageEditMenu.Text=����ͼƬ�༭��
InsertImageEditMenu.M=R
InsertImageEditMenu.Action=onInsertImageEdit

DeleteImageEditMenu.Type=TMenuItem
DeleteImageEditMenu.Text=ɾ��
DeleteImageEditMenu.M=R
DeleteImageEditMenu.Action=onDeleteImageEdit

InsertBlockMenu.Type=TMenuItem
InsertBlockMenu.Text=�����
InsertBlockMenu.M=R
InsertBlockMenu.Action=onInsertGBlock

InsertGLineMenu.Type=TMenuItem
InsertGLineMenu.Text=�����߶�
InsertGLineMenu.M=R
InsertGLineMenu.Action=onInsertGLine

SizeBlockMenu.Type=TMenu
SizeBlockMenu.Text=�����ߴ�
SizeBlockMenu.M=R
SizeBlockMenu.item=SizeBlock1Menu;SizeBlock2Menu;SizeBlock3Menu;SizeBlock4Menu;SizeBlock5Menu;SizeBlock6Menu

SizeBlock1Menu.Type=TMenuItem
SizeBlock1Menu.Text=�϶���
SizeBlock1Menu.M=R
SizeBlock1Menu.Action=onSizeBlockMenu|1
SizeBlock1Menu.Key=Ctrl+1

SizeBlock2Menu.Type=TMenuItem
SizeBlock2Menu.Text=�¶���
SizeBlock2Menu.M=R
SizeBlock2Menu.Action=onSizeBlockMenu|2
SizeBlock2Menu.Key=Ctrl+2

SizeBlock3Menu.Type=TMenuItem
SizeBlock3Menu.Text=�����
SizeBlock3Menu.M=R
SizeBlock3Menu.Action=onSizeBlockMenu|3
SizeBlock3Menu.Key=Ctrl+3

SizeBlock4Menu.Type=TMenuItem
SizeBlock4Menu.Text=�Ҷ���
SizeBlock4Menu.M=R
SizeBlock4Menu.Action=onSizeBlockMenu|4
SizeBlock4Menu.Key=Ctrl+4

SizeBlock5Menu.Type=TMenuItem
SizeBlock5Menu.Text=ͬ��
SizeBlock5Menu.M=R
SizeBlock5Menu.Action=onSizeBlockMenu|5
SizeBlock5Menu.Key=Ctrl+5

SizeBlock6Menu.Type=TMenuItem
SizeBlock6Menu.Text=ͬ��
SizeBlock6Menu.M=R
SizeBlock6Menu.Action=onSizeBlockMenu|6
SizeBlock6Menu.Key=Ctrl+6

delFixText.type=TMenuItem
delFixText.Text=ɾ���ؼ�
delFixText.Tip=ɾ���ؼ�
delFixText.M=E
delFixText.key=
delFixText.Action=onDelFixText
delFixText.pic=delete.gif

InsertPictureObject.Type=TMenuItem
InsertPictureObject.Text=ͼƬ
InsertPictureObject.M=R
InsertPictureObject.Action=onInsertPictureObject

insertFixText.type=TMenuItem
insertFixText.Text=���±�
insertFixText.Tip=���±�
insertFixText.M=I
insertFixText.key=
insertFixText.Action=onInsertFixText
insertFixText.pic=emr-1.gif

FixTextProperty.type=TMenuItem
FixTextProperty.Text=���±�����
FixTextProperty.Tip=���±�����
FixTextProperty.M=E
FixTextProperty.key=
FixTextProperty.Action=onFixTextProperty
FixTextProperty.pic=spreadout.gif

InsertCurrentTime.type=TMenuItem
InsertCurrentTime.Text=����ʱ��
InsertCurrentTime.Tip=����ʱ��
InsertCurrentTime.M=T
InsertCurrentTime.key=
InsertCurrentTime.Action=onInsertCurrentTime
InsertCurrentTime.pic=date.gif

PastePictureObject.type=TMenuItem
PastePictureObject.Text=ճ��ͼƬ
PastePictureObject.Tip=ճ��ͼƬ
PastePictureObject.M=P
PastePictureObject.key=
PastePictureObject.Action=onPastePicture
PastePictureObject.pic=Picture.gif

ClearMenu.Type=TMenuItem
ClearMenu.Text=�������
ClearMenu.Tip=�������
ClearMenu.M=v
ClearMenu.Action=onClearMenu
ClearMenu.Key=
ClearMenu.pic=001.gif

SavePatInfo.Type=TMenuItem
SavePatInfo.Text=�ղ���Ϣ
SavePatInfo.Tip=�ղ���Ϣ
SavePatInfo.M=v
SavePatInfo.Action=onSavePatInfo
SavePatInfo.Key=
SavePatInfo.pic=002.gif


InsertPatInfo.Type=TMenuItem
InsertPatInfo.Text=������Ϣ
InsertPatInfo.Tip=������Ϣ
InsertPatInfo.M=v
InsertPatInfo.Action=onInsertPatInfo
InsertPatInfo.Key=
InsertPatInfo.pic=emr-1.gif

ModifyFontCombo.type=TFontCombo
ModifyFontSizeCombo.type=TFontSizeCombo

FontBMenu.type=TMenuItem
FontBMenu.text=����
FontBMenu.tip=����
FontBMenu.pic=B.gif

FontIMenu.type=TMenuItem
FontIMenu.text=б��
FontIMenu.tip=б��
FontIMenu.pic=I.gif 
