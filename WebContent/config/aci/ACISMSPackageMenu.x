<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;delete;|;new;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;delete;|;new;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����
new.M=Q
new.key=Ctrl+F
new.Action=onNew
new.pic=new.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.key=Ctrl+Q
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
