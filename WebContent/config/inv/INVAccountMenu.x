#############################################
# <p>Title:��������</p>
#
# <p>Description:��������</p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: BlueCore</p>
#
# @author huangtt 2013.05.03
# @version 1.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;print;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;print;|;excel;|;clear;|;close

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=X
clear.key=
clear.Action=onClear
clear.pic=clear.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=
print.key=
print.Action=onPrint
print.pic=print.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif



close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif



excel.Type=TMenuItem
excel.Text=���Excel
excel.Tip=���Ctrl+E)
excel.M=E
excel.key=Ctrl+E
excel.Action=onExecl
excel.pic=exportexcel.gif