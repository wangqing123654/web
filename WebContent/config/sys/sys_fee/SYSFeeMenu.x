<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;new;delete;query;|;update;|;clear;|;export;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;delete;Refresh;query;|;update;|;clear;|;export;|;regist;|;close

export.Type=TMenuItem
export.Text=����
export.M=S
export.Action=onExport
export.pic=export.gif

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=039.gif

//delete.Type=TMenuItem
//delete.Text=ɾ��
//delete.Tip=ɾ��
//delete.M=N
//delete.key=Delete
//delete.Action=onDelete
//delete.pic=delete.gif


query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

regist.Type=TMenuItem
regist.Text=ע���������
regist.Tip=ע���������
regist.M=
regist.key=
regist.Action=onRegist
regist.pic=007.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

update.Type=TMenuItem
update.Text=�ײͼ�Ǯ����
update.Tip=�ײͼ�Ǯ����
update.M=T
update.key=Ctrl+T
update.Action=onUpdate
update.pic=tempsave.gif

