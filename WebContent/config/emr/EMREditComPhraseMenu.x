<Type=TMenuBar>
UI.Item=File;Edid;Insert;InsertElement;Window
UI.button=save;|;ModifyFontCombo;ModifyFontSizeCombo;AlignmentLeft;AlignmentCenter;AlignmentRight;ShowZoom;|;FontBMenu;FontIMenu;|;delFixText;|;FixTextProperty|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;close

Edid.Type=TMenu
Edid.Text=�༭
Edid.M=E
Edid.Item=delFixText;FixTextProperty;DIVProperty;|;MicroFieldProperty;addDLText;TableProperty;|;CutMenu;CopyMenu;PasteMenu;DeleteMenu;DeleteCapMenu

Insert.Type=TMenu
Insert.Text=����
Insert.M=I
Insert.Item=insertFixText;InsertSingleChoose;InsertRelateChoose;InsertMultiChoose;InsertHasChoose;|;InsertMicroField;|;InsertTable;DelTable;InsertTableRow;AddTableRow;DelTableRow;|;addInputMessage;|;InsertCaptureObject;CaptureDataProperty;|;InsertCheckBoxChooseObject;CustomScriptDialog;|;InsertPictureObject;InsertNumberChooseObject;|;InsertTextFormat


save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

ShowZoom.type=TShowZoomCombo
ShowZoom.PreferredWidth=80
ShowZoom.ShowID=false

ModifyFontCombo.type=TFontCombo
ModifyFontSizeCombo.type=TFontSizeCombo

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

insertFixText.type=TMenuItem
insertFixText.Text=�̶��ı�
insertFixText.Tip=�̶��ı�
insertFixText.M=I
insertFixText.key=
insertFixText.Action=onInsertFixText
insertFixText.pic=emr-1.gif

FixTextProperty.type=TMenuItem
FixTextProperty.Text=Ԫ������
FixTextProperty.Tip=Ԫ������
FixTextProperty.M=E
FixTextProperty.key=
FixTextProperty.Action=onFixTextProperty
FixTextProperty.pic=spreadout.gif

InsertSingleChoose.type=TMenuItem
InsertSingleChoose.Text=���뵥ѡ
InsertSingleChoose.Tip=���뵥ѡ
InsertSingleChoose.M=I
InsertSingleChoose.key=
InsertSingleChoose.Action=onInsertSingleChoose
InsertSingleChoose.pic=check_up_rpt.gif


InsertRelateChoose.type=TMenuItem
InsertRelateChoose.Text=�������
InsertRelateChoose.Tip=�������
InsertRelateChoose.M=I
InsertRelateChoose.key=
InsertRelateChoose.Action=onInsertRelateChoose
InsertRelateChoose.pic=check_up_rpt.gif

InsertMultiChoose.type=TMenuItem
InsertMultiChoose.Text=�����ѡ
InsertMultiChoose.Tip=�����ѡ
InsertMultiChoose.M=I
InsertMultiChoose.key=
InsertMultiChoose.Action=onInsertMultiChoose
InsertMultiChoose.pic=check_up_rpt.gif

InsertHasChoose.type=TMenuItem
InsertHasChoose.Text=����ѡ��
InsertHasChoose.Tip=����ѡ��
InsertHasChoose.M=I
InsertHasChoose.key=
InsertHasChoose.Action=onInsertHasChoose
InsertHasChoose.pic=check_up_rpt.gif

InsertMicroField.type=TMenuItem
InsertMicroField.Text=�����
InsertMicroField.Tip=�����
InsertMicroField.M=I
InsertMicroField.key=
InsertMicroField.Action=onInsertMicroField
InsertMicroField.pic=check_up_rpt.gif

MicroFieldProperty.type=TMenuItem
MicroFieldProperty.Text=������
MicroFieldProperty.Tip=������
MicroFieldProperty.M=I
MicroFieldProperty.key=
MicroFieldProperty.Action=onMicroFieldProperty
MicroFieldProperty.pic=check_up_rpt.gif

InsertTable.type=TMenuItem
InsertTable.Text=������
InsertTable.Tip=������
InsertTable.M=I
InsertTable.key=
InsertTable.Action=onInsertTable
InsertTable.pic=inscon.gif

DelTable.type=TMenuItem
DelTable.Text=ɾ�����
DelTable.Tip=ɾ�����
DelTable.M=I
DelTable.key=
DelTable.Action=onDelTable
DelTable.pic=cancle.gif

InsertTableRow.type=TMenuItem
InsertTableRow.Text=��������
InsertTableRow.Tip=��������
InsertTableRow.M=I
InsertTableRow.key=
InsertTableRow.Action=onInsertTableRow
InsertTableRow.pic=sys.gif

AddTableRow.type=TMenuItem
AddTableRow.Text=׷�ӱ����
AddTableRow.Tip=׷�ӱ����
AddTableRow.M=I
AddTableRow.key=
AddTableRow.Action=onAddTableRow
AddTableRow.pic=change.gif

DelTableRow.type=TMenuItem
DelTableRow.Text=ɾ�������
DelTableRow.Tip=ɾ�������
DelTableRow.M=I
DelTableRow.key=
DelTableRow.Action=onDelTableRow
DelTableRow.pic=cancle.gif

PrintSetup.type=TMenuItem
PrintSetup.Text=��ӡ����
PrintSetup.Tip=��ӡ����
PrintSetup.M=I
PrintSetup.key=
PrintSetup.Action=onPrintSetup
PrintSetup.pic=print-2.gif

PrintClear.Type=TMenuItem
PrintClear.Text=����Ԥ��
PrintClear.Tip=����Ԥ��
PrintClear.M=I
PrintClear.key=
PrintClear.Action=onPrintClear
PrintClear.pic=Picture.gif


PrintPageSet.type=TMenuItem
PrintPageSet.Text=ҳ������
PrintPageSet.Tip=ҳ������
PrintPageSet.M=I
PrintPageSet.key=
PrintPageSet.Action=onPrintPageSet
PrintPageSet.pic=031.gif

PrintShow.type=TMenuItem
PrintShow.Text=��ӡ
PrintShow.Tip=��ӡ
PrintShow.M=I
PrintShow.key=
PrintShow.Action=onPrint
PrintShow.pic=print.gif

delFixText.type=TMenuItem
delFixText.Text=ɾ��Ԫ��
delFixText.Tip=ɾ��Ԫ��
delFixText.M=E
delFixText.key=
delFixText.Action=onDelFixText
delFixText.pic=delete.gif

addDLText.type=TMenuItem
addDLText.Text=����
addDLText.Tip=����
addDLText.M=E
addDLText.key=
addDLText.Action=onAddDLText
addDLText.pic=Retrieve.gif

addInputMessage.type=TMenuItem
addInputMessage.Text=������ʾ���
addInputMessage.Tip=������ʾ���
addInputMessage.M=I
addInputMessage.key=
addInputMessage.Action=onAddInputMessage
addInputMessage.pic=Retrieve.gif

InsertCaptureObject.Type=TMenuItem
InsertCaptureObject.Text=ץȡ
InsertCaptureObject.M=R
InsertCaptureObject.Action=onInsertCaptureObject

CaptureDataProperty.Type=TMenuItem
CaptureDataProperty.Text=ץȡ����
CaptureDataProperty.M=R
CaptureDataProperty.Action=onCaptureDataProperty

TableProperty.type=TMenuItem
TableProperty.Text=�������
TableProperty.Tip=�������
TableProperty.M=E
TableProperty.key=
TableProperty.Action=onTableProperty
TableProperty.pic=Retrieve.gif

FontBMenu.type=TMenuItem
FontBMenu.text=����
FontBMenu.tip=����
FontBMenu.pic=B.gif

FontIMenu.type=TMenuItem
FontIMenu.text=б��
FontIMenu.tip=б��
FontIMenu.pic=I.gif 

InsertCheckBoxChooseObject.Type=TMenuItem
InsertCheckBoxChooseObject.Text=ѡ���
InsertCheckBoxChooseObject.M=R
InsertCheckBoxChooseObject.Action=onInsertCheckBoxChooseObject

CustomScriptDialog.Type=TMenuItem
CustomScriptDialog.Text=�Զ���ű��Ի���
CustomScriptDialog.M=R
CustomScriptDialog.Action=onCustomScriptDialog

InsertPictureObject.Type=TMenuItem
InsertPictureObject.Text=ͼƬ
InsertPictureObject.M=R
InsertPictureObject.Action=onInsertPictureObject

InsertNumberChooseObject.Type=TMenuItem
InsertNumberChooseObject.Text=����ѡ��
InsertNumberChooseObject.M=R
InsertNumberChooseObject.Action=onInsertNumberChooseObject

CutMenu.Type=TMenuItem
CutMenu.Text=����
CutMenu.M=v
CutMenu.Action=onCutMenu
cutMenu.Key=Ctrl+X

CopyMenu.Type=TMenuItem
CopyMenu.Text=����
CopyMenu.M=v
CopyMenu.Action=onCopyMenu
CopyMenu.Key=Ctrl+C

PasteMenu.Type=TMenuItem
PasteMenu.Text=ճ��
PasteMenu.M=v
PasteMenu.Action=onPasteMenu
PasteMenu.Key=Ctrl+V

DeleteMenu.Type=TMenuItem
DeleteMenu.Text=ɾ��
DeleteMenu.M=d
DeleteMenu.Action=onDeleteMenu
DeleteMenu.Key=

DeleteCapMenu.Type=TMenuItem
DeleteCapMenu.Text=ɾ��ץȡ��
DeleteCapMenu.M=d
DeleteCapMenu.Action=onDeleteCap
DeleteCapMenu.Key=

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

DIVProperty.Type=TMenuItem
DIVProperty.Text=ͼ�����
DIVProperty.M=T
DIVProperty.Action=onDIVProperty
DIVProperty.Key=Ctrl+0

LinkPrint.Type=TMenuItem
LinkPrint.Text=��ӡ
LinkPrint.Enabled=true
LinkPrint.M=T
LinkPrint.Action=onPrintXDDialog

ShowRowIDSwitch.Type=TMenuItem
ShowRowIDSwitch.Text=��ʾ�кſ���
ShowRowIDSwitch.Enabled=true
ShowRowIDSwitch.M=T
ShowRowIDSwitch.Action=onShowRowIDSwitch

InsertTextFormat.type=TMenuItem
InsertTextFormat.Text=����������
InsertTextFormat.Tip=����������
InsertTextFormat.M=I
InsertTextFormat.key=
InsertTextFormat.Action=onInsertETextFormatObject
InsertTextFormat.pic=check_up_rpt.gif

InsertElement.Type=TMenu
InsertElement.Text=����Ԫ��
InsertElement.M=E
InsertElement.Item=insertElementData

insertElementData.type=TMenuItem
insertElementData.Text=��������Ԫ��
insertElementData.Tip=��������Ԫ��
insertElementData.M=I
insertElementData.key=
insertElementData.Action=onInserElement