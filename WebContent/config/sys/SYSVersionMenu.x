<Type=TMenuBar>
UI.Item=File;Window
UI.button=Refresh;exit

Window.Type=TMenu
Window.Text=����
Window.zhText=����
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.zhText=�ļ�
File.enText=File
File.M=F
File.Item=ModifyPassword;|;exit

ModifyPassword.Type=TMenuItem
ModifyPassword.Text=�޸�����
ModifyPassword.zhText=�޸�����
ModifyPassword.enText=Modify Password
ModifyPassword.zhTip=�޸�����
ModifyPassword.enTip=Modify Password
ModifyPassword.M=P
ModifyPassword.key=
ModifyPassword.Action=onModifyPassword


Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.zhText=ˢ��
Refresh.enText=Refresh
Refresh.zhTip=���¼��س���
Refresh.enTip=reset
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReresh
Refresh.pic=Refresh.gif

exit.Type=TMenuItem
exit.Text=�˳�
exit.zhText=�˳�
exit.enText=Exit
exit.zhTip=�˳�����
exit.enTip=exit
exit.M=X
exit.key=Alt+F4
exit.Action=onClose
exit.pic=close.gif