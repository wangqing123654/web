<Type=TMenuBar>
UI.Item=File;Window
UI.button=insert;delete;|;print;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=insert;delete;|;print;|;clear;|;close

insert.Type=TMenuItem
insert.Text=����
insert.Tip=����
insert.M=S
insert.key=Ctrl+S
insert.Action=onSave
insert.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=D
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=C
print.Action=onPrint
print.pic=print.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.key=Ctrl+Q
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif