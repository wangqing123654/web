# 
#  Title:ҽ�Ƽ��ָ���ϴ�
#
#  Description:ҽ�Ƽ��ָ���ϴ�
# 
#  Copyright: Copyright (c) Javahis 2012
# 
#  author shibl 2012.6.30
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;excel;|;log;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;excel;|;log;|;clear;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��(F5)
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


excel.Type=TMenuItem
excel.Text=���Excel
excel.Tip=��ӡ(Ctrl+E)
excel.M=E
excel.key=Ctrl+E
excel.Action=onExport
excel.pic=exportexcel.gif


log.Type=TMenuItem
log.Text=��ѯ��־
log.Tip=��ѯ(Ctrl+L)
log.M=E
log.key=Ctrl+L
log.Action=onLog
log.pic=date.gif