# 
#  Title:�ٴ�·�����ԭ���
# 
#  Description:�ٴ�·�����ԭ���
# 
#  Copyright: Copyright (c) Javahis 2011
# 
#  author pangben 2011.07.07
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;apply;|;download;|;print;|;clear;|;close;|;

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;apply;|;download;|;print;|;clear;|;close;|;

apply.Type=TMenuItem
apply.Text=����
apply.Tip=����
apply.M=E
apply.key=F4
apply.Action=onApply
apply.pic=008.gif

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

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

download.Type=TMenuItem
download.Text=����Ʊ����������
download.Tip=����Ʊ����������
download.M=E
download.key=Ctrl+E
download.Action=onDownLoad
download.pic=001.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

