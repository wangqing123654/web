<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;query;|;clear;|;close

save.Type=TMenuItem
save.Text=�⿪
save.Tip=�⿪(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=026.gif

query.Type=TMenuItem
query.Text=��������
query.Tip=��������
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=tempsave.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=ȫ���⿪
clear.Tip=ȫ���⿪(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=034.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

