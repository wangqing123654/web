#
# TBuilder Config File 
#
# Title:���ƻ���ԤԼ��ѯ
#
# Company:JavaHis
#
# Author:�ƿ��� 2014.12.15
#
# version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;export;print;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;export;print;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q  
query.key=Ctrl+Q
query.Action=onQuery
query.pic=query.gif 

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=
print.key=
print.Action=onPrint
print.pic=print.gif

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

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

export.Type=TMenuItem
export.Text=���
export.Tip=���
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif