UI.item=File;Edit;Window
UI.button=Save;|;LanguageComboTool;|;InputParm;Preview;Close;|;Refurbish;|;Exit

File.type=TMenu
File.text=�ļ�
File.M=F
File.item=NewProject;Save;Close;|;Exit

NewProject.type=TMenuItem
NewProject.text=�½�����...
NewProject.tip=�½�����
NewProject.M=N
NewProject.key=Ctrl+N
NewProject.Action=onNewProject
NewProject.pic=new.gif

Save.type=TMenuItem
Save.text=����
Save.tip=����
Save.M=S
Save.key=Ctrl+S
Save.Action=onSave
Save.pic=save.gif

LanguageComboTool.type=TLanguageCombo
LanguageComboTool.selectedAction=onChangeLanguage

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
Edit.item=Delete;AlignControls;|;InputParm;Preview

Delete.type=TMenuItem
Delete.text=ɾ��
Delete.tip=ɾ��
Delete.M=D
Delete.key=delete
Delete.Action="onDelete"
Delete.pic=Delete.gif

AlignControls.type=TMenu
AlignControls.text=�������
AlignControls.M=N
AlignControls.item=AlignUP;AlignDown;AlignLeft;AlignRight;|;AlignWCenter;AlignHCenter;|;AlignWidth;AlignHeight;|;AlignWSpace;AlignHSpace

AlignUP.type=TMenuItem
AlignUP.text=������
AlignUP.tip=������
AlignUP.M=U
AlignUP.key=Ctrl+1
AlignUP.Action="onAlignUP"
AlignUP.pic=AlignUP.gif

AlignDown.type=TMenuItem
AlignDown.text=�׶���
AlignDown.tip=�׶���
AlignDown.M=D
AlignDown.key=Ctrl+2
AlignDown.Action="onAlignDown"
AlignDown.pic=AlignDown.gif

AlignLeft.type=TMenuItem
AlignLeft.text=�����
AlignLeft.tip=�����
AlignLeft.M=L
AlignLeft.key=Ctrl+3
AlignLeft.Action="onAlignLeft"
AlignLeft.pic=AlignLeft.gif

AlignRight.type=TMenuItem
AlignRight.text=�Ҷ���
AlignRight.tip=�Ҷ���
AlignRight.M=R
AlignRight.key=Ctrl+4
AlignRight.Action="onAlignRight"
AlignRight.pic=AlignRight.gif

AlignWCenter.type=TMenuItem
AlignWCenter.text=ˮƽ����
AlignWCenter.tip=ˮƽ����
AlignWCenter.M=T
AlignWCenter.Action="onAlignWCenter"
AlignWCenter.pic=AlignWCenter.gif

AlignHCenter.type=TMenuItem
AlignHCenter.text=��ֱ����
AlignHCenter.tip=��ֱ����
AlignHCenter.M=I
AlignHCenter.Action="onAlignHCenter"
AlignHCenter.pic=AlignHCenter.gif

AlignWidth.type=TMenuItem
AlignWidth.text=ͬ��
AlignWidth.tip=ͬ��
AlignWidth.M=W
AlignWidth.key=Ctrl+5
AlignWidth.Action="onAlignWidth"
AlignWidth.pic=AlignWidth.gif

AlignHeight.type=TMenuItem
AlignHeight.text=ͬ��
AlignHeight.tip=ͬ��
AlignHeight.M=H
AlignHeight.key=Ctrl+6
AlignHeight.Action="onAlignHeight"
AlignHeight.pic=AlignHeight.gif

AlignWSpace.type=TMenuItem
AlignWSpace.text=����ͬ���
AlignWSpace.tip=����ͬ���
AlignWSpace.M=K
AlignWSpace.Action="onAlignWSpace"
AlignWSpace.pic=AlignWSpace.gif

AlignHSpace.type=TMenuItem
AlignHSpace.text=����ͬ���
AlignHSpace.tip=����ͬ���
AlignHSpace.M=L
AlignHSpace.Action="onAlignHSpace"
AlignHSpace.pic=AlignHSpace.gif

InputParm.type=TMenuItem
InputParm.text=�����������
InputParm.tip=�����������
InputParm.M=L
InputParm.Action="onInputParm"
InputParm.pic=new.gif


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
Window.item=Refurbish;RefurbishAction;MEMAction

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

MEMAction.type=TMenuItem
MEMAction.text=�쿴�ڴ�
MEMAction.tip=�쿴�ڴ�
MEMAction.M=A
MEMAction.key=
MEMAction.Action="onMEMAction"
//RefurbishAction.pic=Refresh.gif
