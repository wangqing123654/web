UI.item=File;Window
UI.button=Refurbish;|;Exit

File.type=TMenu
File.text=�ļ�
File.M=F
File.item=NewProject;|;Exit

NewProject.type=TMenuItem
NewProject.text=�½�����...
NewProject.tip=�½�����
NewProject.M=N
NewProject.key=Ctrl+N
NewProject.Action=onNewProject
NewProject.pic=new.gif


Exit.type=TMenuItem
Exit.text=�˳�
Exit.tip=�˳�
Exit.M=X
Exit.key=Ctrl+F4
Exit.Action=onClose
Exit.pic=close.gif

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
