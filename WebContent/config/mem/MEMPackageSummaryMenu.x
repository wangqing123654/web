#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:sunqy 2014.04.24
#
# version 1.0
#

<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;print;|;execl;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;print;|;execl;|;clear;|;close

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

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=F
print.key=Ctrl+P
print.Action=onPrint
print.pic=Print.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

execl.Type=TMenuItem
execl.Text=����Excel
execl.Tip=����Excel
execl.M=
execl.key=
execl.Action=onExport
execl.pic=export.gif
