#####################################################
# <p>Title:ԤԼסԺMenu </p>
#
# <p>Description: ԤԼסԺMenu</p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK
# @version 1.0
#####################################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;new;|;Wrist;|;cancel;|;resv;|;close

Window.Type=TMenu
Window.Text=����
Window.enTip=Window
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.enText=File
File.enTip=File
File.M=F
File.Item=save;|;new;|;Wrist;|;cancel;|;resv;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.enTip=save
save.enText=save
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����(Ctrl+N)
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=new.gif


Wrist.Type=TMenuItem
Wrist.Text=��ӡ���
Wrist.Tip=��ӡ���
Wrist.M=
Wrist.key=
Wrist.Action=onWrist
Wrist.pic=print-1.gif

cancel.Type=TMenuItem
cancel.Text=ȡ��ע��
cancel.Tip=ȡ��ע��
cancel.M=
cancel.key=
cancel.Action=onCancel
cancel.pic=030.gif


close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.enTip=Log out
close.enText=Log out
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif

resv.Type=TMenuItem
resv.Text=������Ժ
resv.Tip=������Ժ
resv.M=
resv.key=
resv.Action=onResv
resv.pic=Redo.gif



