#############################################
# <p>Title:������ĿMenu </p>
#
# <p>Description:������ĿMenu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.05.11
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;lending;|;dbfConvert;|;print;excel;|;clear;|;mrshow;|;searchshow;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;lending;|;dbfConvert;|;print;excel;|;clear;|;mrshow;|;searchshow;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=ɾ��
delete.Tip=ɾ��(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif


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

mrshow.Type=TMenuItem
mrshow.Text=�������
mrshow.Tip=�������(Ctrl+W)
mrshow.M=W
mrshow.key=Ctrl+W
mrshow.Action=onShow
mrshow.pic=012.gif

searchshow.Type=TMenuItem
searchshow.Text=ȫ�ļ���
searchshow.Tip=ȫ�ļ���(Ctrl+W)
searchshow.M=H
searchshow.key=Ctrl+H
searchshow.Action=onAllSearch
searchshow.pic=006.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

lending.Type=TMenuItem
lending.Text=���Ĳ���
lending.Tip=���Ĳ���(Alt+L)
lending.M=L
lending.key=Alt+L
lending.Action=onLending
lending.pic=emr.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ(Ctrl+P)
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

dbfConvert.Type=TMenuItem
dbfConvert.Text=DBFת��
dbfConvert.Tip=DBFת��
dbfConvert.M=P
dbfConvert.Action=saveDBF
dbfConvert.pic=046.gif

excel.Type=TMenuItem
excel.Text=���Excel
excel.Tip=���Ctrl+E)
excel.M=E
excel.key=Ctrl+E
excel.Action=onExecl
excel.pic=exportexcel.gif

