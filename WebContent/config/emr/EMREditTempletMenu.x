<Type=TMenuBar>
UI.Item=File;Edid;ImageEditMenu;Insert;TableElem;InsertElement;AllExpression;Window
UI.button=save;|;ModifyFontCombo;ModifyFontSizeCombo;AlignmentLeft;AlignmentCenter;AlignmentRight;ShowZoom;|;FontBMenu;FontIMenu;|;clear;|;ClearMenu;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;PrintSetup;PrintPageSet;PrintShow;PrintClear;LinkPrint;ShowRowIDSwitch;Refresh;|;clear;|;close

Edid.Type=TMenu
Edid.Text=�༭
Edid.M=E
Edid.Item=delFixText;FixTextProperty;DIVProperty;|;MicroFieldProperty;addDLText;TableProperty;|;ClearMenu;CutMenu;CopyMenu;PasteMenu;DeleteMenu;DeleteCapMenu;|;FormatSet;FormatUse

Insert.Type=TMenu
Insert.Text=�ؼ�
Insert.M=I
Insert.Item=insertFixText;InsertSingleChoose;InsertRelateChoose;InsertMultiChoose;InsertHasChoose;|;InsertMicroField;|;addInputMessage;|;InsertCaptureObject;CaptureDataProperty;|;InsertCheckBoxChooseObject;CustomScriptDialog;|;InsertPictureObject;InsertNumberChooseObject;|;InsertTextFormat


TableElem.Type=TMenu
TableElem.Text=���
TableElem.M=T
TableElem.Item=InsertTable;DelTable;MergerCell;InsertTableRow;AddTableRow;DelTableRow


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
InsertElement.Item=insertElementData;insertHideElement;InsertTemplatePY;

insertElementData.type=TMenuItem
insertElementData.Text=��������Ԫ��
insertElementData.Tip=��������Ԫ��
insertElementData.M=I
insertElementData.key=
insertElementData.Action=onInserElement

insertHideElement.type=TMenuItem
insertHideElement.Text=������������Ԫ��
insertHideElement.Tip=������������Ԫ��
insertHideElement.M=I
insertHideElement.key=
insertHideElement.Action=onInserHideElement

InsertTemplatePY.type=TMenuItem
InsertTemplatePY.Text=������ģ��
InsertTemplatePY.Tip=������ģ��
InsertTemplatePY.M=I
InsertTemplatePY.key=
InsertTemplatePY.Action=onInsertTemplatePY


ClearMenu.Type=TMenuItem
ClearMenu.Text=��ռ�����
ClearMenu.Tip=��ռ�����
ClearMenu.M=v
ClearMenu.Action=onClearMenu
ClearMenu.Key=
ClearMenu.pic=001.gif

MergerCell.type=TMenuItem
MergerCell.Text=�ϲ���Ԫ��
MergerCell.zhText=�ϲ���Ԫ��
MergerCell.enText=Merger Cell
MergerCell.Tip=�ϲ���Ԫ��
MergerCell.zhTip=�ϲ���Ԫ��
MergerCell.enTip=Merger Cell
MergerCell.M=M
MergerCell.key=
MergerCell.Action=onMergerCell
MergerCell.pic=

FormatSet.type=TMenuItem
FormatSet.Text=��ʽˢѡ��
FormatSet.Tip=��ʽˢѡ��
FormatSet.M=
FormatSet.key=
FormatSet.Action=onFormatSet
FormatSet.pic=

FormatUse.type=TMenuItem
FormatUse.Text=��ʽˢʹ��
FormatUse.Tip=��ʽˢʹ��
FormatUse.M=
FormatUse.key=
FormatUse.Action=onFormatUse
FormatUse.pic=

AllExpression.Type=TMenu
AllExpression.Text=���ʽ
AllExpression.M=
AllExpression.Item=InsertExpressionCaptureObject;ExpressionInsert;ExpressionSetting;ExpressionCalculate;

InsertExpressionCaptureObject.Type=TMenuItem
InsertExpressionCaptureObject.Text=���빫ʽץȡ
InsertExpressionCaptureObject.M=
InsertExpressionCaptureObject.Action=onInsertExpressionCapture

ExpressionInsert.type=TMenuItem
ExpressionInsert.Text=������ʽ
ExpressionInsert.Tip=������ʽ
ExpressionInsert.M=
ExpressionInsert.key=
ExpressionInsert.Action=onInsertExpression
ExpressionInsert.pic=

ExpressionSetting.type=TMenuItem
ExpressionSetting.Text=���ʽ����
ExpressionSetting.Tip=���ʽ����
ExpressionSetting.M=
ExpressionSetting.key=
ExpressionSetting.Action=onSettingExpression
ExpressionSetting.pic=

ExpressionCalculate.type=TMenuItem
ExpressionCalculate.Text=������ʽ
ExpressionCalculate.Tip=������ʽ
ExpressionCalculate.M=
ExpressionCalculate.key=
ExpressionCalculate.Action=onCalculateExpression
ExpressionCalculate.pic=
