# 
#  Title:�ż���Һ��շ�Ա�սᱨ��
# 
#  Description:�ż���Һ��շ�Ա�սᱨ��
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;read;|;query;|;cancel;|;saveinv;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;read;|;query;|;cancel;|;saveinv;|;clear;|;close

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

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�ر�
close.Tip=�ر�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

read.Type=TMenuItem
read.Text=ҽ�ƿ�
read.Tip=ҽ�ƿ�(Ctrl+R)
read.M=R
read.key=Ctrl+R
read.Action=onReadEKT
read.pic=042.gif

cancel.Type=TMenuItem
cancel.Text=ȡ�����
cancel.Tip=ȡ�����
cancel.M=F
cancel.key=Ctrl+H
cancel.Action=onCancle
cancel.pic=Undo.gif

saveinv.Type=TMenuItem
saveinv.Text=Ʊ�ű���
saveinv.Tip=Ʊ�ű���
saveinv.M=S
saveinv.key=Ctrl+S
saveinv.Action=onSaveInvNo
saveinv.pic=save.gif