 #
 # Title: 
 #
 # Description: 
 #
 # Copyright: JavaHis (c) 2009
 #
 # @author WangM 2010.09.30
 # @version 1.0
 
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;print;|;save;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;print;|;save;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
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

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

save.Type=TMenuItem
save.Text=�վ��˷�
save.Tip=�վ��˷�
save.M=P
save.Action=onSave
save.pic=030.gif

print.Type=TMenuItem
print.Text=����EXECL
print.Tip=����EXECL
print.M=P
print.Action=onExecl
print.pic=export.gif