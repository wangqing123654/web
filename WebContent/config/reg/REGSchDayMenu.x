# 
#  Title:ҽʦ�հ��
# 
#  Description:ҽʦ�հ��
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.10.30
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;new;delete;query;inscon;|;export;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;new;delete;Refresh;query;inscon;|;export;|;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

new.Type=TMenuItem
new.Text=�Ӻ�
new.Tip=�Ӻ�
new.M=N
new.key=Ctrl+N
new.Action=onAdd
new.pic=039.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif


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



inscon.Type=TMenuItem
inscon.Text=�ѹҺ�����
inscon.Tip=�ѹҺ�����
inscon.M=IS
inscon.Action=onInscon
inscon.pic=inscon.gif

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

export.Type=TMenuItem
export.Text=���
export.Tip=���
execrpt.M=E
export.Action=onExport
export.pic=export.gif

