#############################################
# <p>Title:���ʹ���Menu </p>
#
# <p>Description:���ʹ���Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author zhangh 2013.05.3
# @version 1.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;print;|;excel;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;clear;|;print;|;excel;|;close


clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�ر�
close.Tip=�ر�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+Q)
query.M=Q
query.key=Ctrl+Q
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ(Ctrl+E)
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

excel.Type=TMenuItem
excel.Text=��Excel
excel.Tip=��Excel(Ctrl+E)
excel.M=E
excel.key=Ctrl+E
excel.Action=onExecl
excel.pic=exportexcel.gif