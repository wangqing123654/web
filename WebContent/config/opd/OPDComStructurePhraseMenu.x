<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;edit;clear;|;ClearMenu;|;close

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
File.Item=save;delete;Refresh;|;close

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

clear.Type=TMenuItem
clear.Text=���
clear.zhText=���
clear.enText=Clear
clear.Tip=���
clear.zhTip=���
clear.enTip=Clear
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.zhText=ɾ��
delete.enText=Delete
delete.Tip=ɾ��
delete.zhTip=ɾ��
delete.enTip=Delete
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.zhText=ˢ��
Refresh.enText=Refresh
Refresh.Tip=ˢ��
Refresh.zhTip=ˢ��
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

edit.Type=TMenuItem
edit.Text=�༭Ƭ��
edit.zhText=�༭Ƭ��
edit.enText=Edit
edit.Tip=�༭Ƭ��
edit.zhTip=�༭Ƭ��
edit.enTip=Edit
edit.M=N
edit.Action=onEdit
edit.pic=emr-2.gif

close.Type=TMenuItem
close.Text=�˳�
close.zhText=�˳�
close.enText=Quit
close.Tip=�˳�
close.zhTip=�˳�
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

ClearMenu.Type=TMenuItem
ClearMenu.Text=��ռ�����
ClearMenu.zhText=��ռ�����
ClearMenu.Tip=��ռ�����
ClearMenu.zhTip=��ռ�����
ClearMenu.enTip=Clear
ClearMenu.M=C
ClearMenu.Action=onClearMenu
ClearMenu.pic=001.gif