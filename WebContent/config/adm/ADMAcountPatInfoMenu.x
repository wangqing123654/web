#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:sunqy 2014.05.12
#
# version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;print;|;clear;|;excel;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;print;|;clear;|;excel;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(query)
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
clear.Tip=���(clear)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

excel.Type=TMenuItem
excel.Text=���Excel
excel.Tip=���Excel
excel.M=S
excel.key=Ctrl+S
excel.Action=onExcel
excel.pic=045.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(close)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

print.Type=TMenuItem
print.Text=��ӡ
print.zhText=��ӡ
print.enText=Print
print.Tip=��ӡ(Ctrl+P)
print.zhTip=��ӡ
print.enTip=Print
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif


